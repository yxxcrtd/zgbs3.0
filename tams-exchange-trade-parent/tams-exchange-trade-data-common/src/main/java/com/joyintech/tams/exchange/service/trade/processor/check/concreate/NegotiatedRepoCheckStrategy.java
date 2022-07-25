package com.joyintech.tams.exchange.service.trade.processor.check.concreate;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.bean.IntegratedOrderPlaceBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.InstructionPs;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.InstructionPledge;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.enums.exchange.RepoBsEnum;
import com.joyintech.tams.exchange.common.enums.invest.BusiTypeTgEnum;
import com.joyintech.tams.exchange.common.utils.EnumUtil;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionPledgeService;
import com.joyintech.tams.exchange.datacommon.db.service.IntegratedEntrustService;
import com.joyintech.tams.exchange.service.trade.processor.check.BaseCheckKit;
import com.joyintech.tams.exchange.service.trade.processor.check.IBusinessCheckStrategy;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.joyintech.tams.exchange.common.constant.joyin.InvestmentAssetType1.NEGOTIATED_REPO;
import static com.joyintech.tams.exchange.common.enums.invest.BusiTypeTgEnum.中途换券;
import static com.joyintech.tams.exchange.common.enums.invest.BusiTypeTgEnum.解除质押;

/**
 * 校验质押式协议回购
 *
 * @author swwan
 * @date 2022/4/24 - 17:19
 */
@Component
public class NegotiatedRepoCheckStrategy extends BaseCheckKit implements IBusinessCheckStrategy {
    @Autowired
    private IntegratedEntrustService integratedEntrustService;
    @Autowired
    private InstructionPledgeService instructionPledgeService;

    @Override
    public boolean support(Instruction instruction) {
        return NEGOTIATED_REPO.codeEquals(instruction.getAssetType1());
    }

    @Override
    public ResultBean check(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tickBean) {
        IntegratedOrderPlaceBean bean = (IntegratedOrderPlaceBean) order;
        IntegratedEntrust integratedEntrust = buildIntegratedEntrust(instruction, order);
        List<InstructionPledge> instructionPledges = instructionPledgeService.queryInstructionPledgesByInstructionId(instruction.getEntrustId());

        checkQuote(instruction, bean);
        checkPropDealerTradeInfo(integratedEntrust, instruction);
        checkPrice(instruction, order);
        checkNum(instruction, order, instructionPledges);
        checkAmt(instruction, order);

        // 解除质押和中途换券无需判断
        if (EnumUtil.notIn(instruction.getBusiTypeTg(), 解除质押, 中途换券)
                && (TradeUtils.equals(integratedEntrust.getEntrustBalance(), BigDecimal.ZERO)
                || TradeUtils.equals(integratedEntrust.getBackBalance(), BigDecimal.ZERO))) {
            throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "回购金额或到期结算金额不能为0");
        }
        return ResultBean.success();
    }

    private IntegratedEntrust buildIntegratedEntrust(Instruction instruction, BaseOrderRequest order) {
        IntegratedEntrust integratedEntrust = new IntegratedEntrust();
        try {
            BeanUtil.copyProperties(integratedEntrust, instruction);
            BeanUtil.copyProperties(integratedEntrust, order);
            return integratedEntrust;
        } catch (Exception e) {
            throw new BusinessResultException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 校验对手方信息
     */
    private void checkPropDealerTradeInfo(IntegratedEntrust entrust, Instruction instruction) {
        if (StrUtil.isEmpty(entrust.getCounterId()) || StrUtil.isEmpty(entrust.getCounterName())) {
            throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "交易对手代码或名称不能为空");
        }
        if (StrUtil.isEmpty(entrust.getPropDealerId())) {
            throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "对手方交易商代码不能为空");
        }
        if (StrUtil.isEmpty(entrust.getPropTradeId())) {
            throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "对手方交易员代码不能为空");
        }

        if (StringUtil.equals(instruction.getTradeMarket(), Market.SZSE.investValue)) {
            if (StrUtil.isEmpty(entrust.getPropInvestorId())) {
                throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "对手方交易主体代码不能为空");
            }
            if (StrUtil.isEmpty(entrust.getPropOwnerType())) {
                throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "对手方交易主体类型不能为空");
            }
        }
    }

    /**
     * 校验行情
     */
    private void checkQuote(Instruction instruction, IntegratedOrderPlaceBean bean) {
        String requestNo = bean.getRequestNo();
        String firstRepoPs = instruction.getFirstRepoPs();

        if (
                StringUtil.isEmpty(requestNo)
                        && RepoBsEnum.逆回购.codeEquals(firstRepoPs)
                        && !BusiTypeTgEnum.提前购回.codeEquals(instruction.getBusiTypeTg())
        ) {
            throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "逆回购行情编号不能为空");
        }

        if (RepoBsEnum.逆回购.codeEquals(firstRepoPs) && StringUtil.isNotEmpty(requestNo)) {
            List<IntegratedEntrust> entrustList = integratedEntrustService.queryIntegratedEntrustByAccountId(bean.getAccountId());
            if (CollectionUtils.isNotEmpty(entrustList)) {
                Optional<IntegratedEntrust> optional = entrustList.stream()
                        .filter(entrust -> StringUtil.equals(entrust.getSellerNo(), requestNo)
                                && (entrust.getEntrustStatus() < 9 && entrust.getEntrustStatus() != 7)).findFirst();
                if (optional.isPresent()) {
                    throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "行情已委托，不能重复下单");
                }
            }
        }
    }

    /**
     * 校验回购利率
     */
    private void checkPrice(Instruction instruction, BaseOrderRequest bean) {
        String firstRepoPs = instruction.getFirstRepoPs();
        BigDecimal instructionPrice = instruction.getEntrustPrice();
        BigDecimal orderPrice = bean.getEntrustPrice();

        if (RepoBsEnum.正回购.codeEquals(firstRepoPs) || RepoBsEnum.正回购.codeEquals(instruction.getPs())) {
            if (TradeUtils.lessThan(instructionPrice, orderPrice)) {
                throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "正回购时，下单回购利率不允许超过指令回购利率！");
            }
        }

        if (RepoBsEnum.逆回购.codeEquals(firstRepoPs) || RepoBsEnum.逆回购.codeEquals(instruction.getPs())) {
            if (TradeUtils.bigThan(instructionPrice, orderPrice)) {
                throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "逆回购时，下单回购利率不允许低于指令回购利率！");
            }
        }
    }

    /**
     * 校验质押券数量
     */
    private void checkNum(Instruction instruction, BaseOrderRequest bean, List<InstructionPledge> instructionPledges) {
        String firstRepoPs = instruction.getFirstRepoPs();
//        Long instructionVolume = instruction.getEntrustNum();
        Long orderVolume = bean.getEntrustVolume();

        // 正回购首期指令，下单时委托质押券数量不允许超过指令剩余质押券数量
        if (RepoBsEnum.正回购.codeEquals(instruction.getPs())) {
            InstructionPledge instructionPledge = instructionPledges.get(0);
            BigDecimal pledgeNumber = instructionPledge.getPledgeNumber();
            // TODO 万元？张？剩余质押券数量？
            /*if (orderVolume > instructionVolume) {
                throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "正回购首期时，下单质押券数量不允许超过指令剩余质押券数量！");
            }*/
        }
    }

    /**
     * 校验回购金额
     */
    private void checkAmt(Instruction instruction, BaseOrderRequest bean) {
        BigDecimal entrustBalance = bean.getEntrustBalance();
        BigDecimal instructionLeftAmt = NumberUtil.sub(instruction.getEntrustAmt(), instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity());

        // 正回购首期指令，委托回购金额不允许超过指令剩余回购金额
        if (RepoBsEnum.正回购.codeEquals(instruction.getPs())) {
            if (TradeUtils.bigThan(entrustBalance, instructionLeftAmt)) {
                throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "正回购首期时，下单回购金额不允许超过指令剩余回购金额！");
            }
        }
    }

    private void checkSpecilParamInShMarket(IntegratedEntrust integratedEntrust) {
        if (Market.SHSE.codeNotEquals(integratedEntrust.getMarket())) {
            return;
        }

        String liaisonTel = integratedEntrust.getLiaisonTel();
        if (StrUtil.isEmpty(liaisonTel)) {
            throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "对手方交易员不能为空");
        }
        if (liaisonTel.length() < 4) {
            throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "对手方交易员不能小于4位");
        }
        // TODO: 属性设置应移出
        integratedEntrust.setPropSeatNo(integratedEntrust.getLiaisonTel().substring(1, 4));
        if (Strings.isNullOrEmpty(integratedEntrust.getPropSeatName())) {
            integratedEntrust.setPropSeatName(integratedEntrust.getPropSeatNo());
        }
    }


}
