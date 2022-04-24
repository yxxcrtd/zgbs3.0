package com.joyintech.tams.exchange.service.trade.processor.check.concreate;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.utils.EntrustPropUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.service.trade.processor.check.BaseCheckKit;
import com.joyintech.tams.exchange.service.trade.processor.check.IBusinessCheckStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.joyintech.tams.exchange.common.constant.joyin.InvestmentAssetType1.NEGOTIATED_REPO;

/**
 * 校验质押式协议回购
 *
 * @author swwan
 * @date 2022/4/24 - 17:19
 */
@Component
public class NegotiatedRepoCheckStrategy extends BaseCheckKit implements IBusinessCheckStrategy {

    @Override
    public boolean support(Instruction instruction) {
        return NEGOTIATED_REPO.codeEquals(instruction.getAssetType1());
    }

    @Override
    public ResultBean check(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tickBean) {
        IntegratedEntrust integratedEntrust = new IntegratedEntrust();
        try {
            BeanUtil.copyProperties(integratedEntrust, instruction);
            BeanUtil.copyProperties(integratedEntrust, order);
        } catch (Exception e) {
            throw new BusinessResultException(ErrorCode.SYSTEM_ERROR);
        }
        String entrustProp = EntrustPropUtils.getEntrustProp(instruction, order.getEntrustProp());

        checkSpecilParamInShMarket(integratedEntrust);

        // 解除质押和中途换券无需判断
        if (!EntrustProp.isRepurchaseReleaseOrExchange(entrustProp)
                && (TradeUtils.equals(integratedEntrust.getEntrustBalance(), BigDecimal.ZERO)
                || TradeUtils.equals(integratedEntrust.getBackBalance(), BigDecimal.ZERO))) {
            throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, "回购金额或到期结算金额不能为0");
        }
        return ResultBean.success();
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
