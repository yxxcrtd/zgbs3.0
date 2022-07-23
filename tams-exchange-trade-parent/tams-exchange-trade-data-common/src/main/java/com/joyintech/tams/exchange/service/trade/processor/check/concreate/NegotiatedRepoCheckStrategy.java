package com.joyintech.tams.exchange.service.trade.processor.check.concreate;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.bean.IntegratedOrderPlaceBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.InstructionPs;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.enums.invest.BusiTypeTgEnum;
import com.joyintech.tams.exchange.common.utils.EntrustPropUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
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

    @Override
    public boolean support(Instruction instruction) {
        return NEGOTIATED_REPO.codeEquals(instruction.getAssetType1());
    }

    @Override
    public ResultBean check(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tickBean) {
        IntegratedOrderPlaceBean bean = (IntegratedOrderPlaceBean) order;
        if (InstructionPs.REVERSEDREPO.codeEquals(instruction.getFirstRepoPs()) && StringUtil.isEmpty(bean.getRequestNo())
                && !BusiTypeTgEnum.提前购回.codeEquals(instruction.getBusiTypeTg())) {
            throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "逆回购行情编号不能为空");
        }
        if (InstructionPs.REVERSEDREPO.codeEquals(instruction.getFirstRepoPs()) && StringUtil.isNotEmpty(bean.getRequestNo())) {
            List<IntegratedEntrust> entrustList = integratedEntrustService.queryIntegratedEntrustByAccountId(bean.getAccountId());
            if (CollectionUtils.isNotEmpty(entrustList)) {
                Optional<IntegratedEntrust> optional = entrustList.stream()
                        .filter(entrust -> StringUtil.equals(entrust.getSellerNo(), bean.getRequestNo())
                                && (entrust.getEntrustStatus() < 9 && entrust.getEntrustStatus() != 7)).findFirst();
                if (optional.isPresent()) {
                    throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "行情已委托，不能重复下单");
                }
            }
        }

        IntegratedEntrust integratedEntrust = new IntegratedEntrust();
        try {
            BeanUtil.copyProperties(integratedEntrust, instruction);
            BeanUtil.copyProperties(integratedEntrust, order);
        } catch (Exception e) {
            throw new BusinessResultException(ErrorCode.SYSTEM_ERROR);
        }
//        String entrustProp = EntrustPropUtils.getEntrustProp(instruction, order.getEntrustProp());

        //检查对手方信息
        checkPropDealerTradeInfo(integratedEntrust, instruction);

        // 解除质押和中途换券无需判断
        if (!BusiTypeTgEnum.解除质押.codeEquals(instruction.getBusiTypeTg())
                && !BusiTypeTgEnum.中途换券.codeEquals(instruction.getBusiTypeTg())
                && (TradeUtils.equals(integratedEntrust.getEntrustBalance(), BigDecimal.ZERO)
                || TradeUtils.equals(integratedEntrust.getBackBalance(), BigDecimal.ZERO))) {
            throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "回购金额或到期结算金额不能为0");
        }
        return ResultBean.success();
    }

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
