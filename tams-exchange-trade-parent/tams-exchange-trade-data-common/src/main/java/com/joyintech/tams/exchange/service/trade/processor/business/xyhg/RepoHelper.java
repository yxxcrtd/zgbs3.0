package com.joyintech.tams.exchange.service.trade.processor.business.xyhg;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.utils.EnumUtil;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RepoHelper {
    private static final Logger logger = LoggerFactory.getLogger(RepoHelper.class);

    public boolean checkCommonParam(BigDecimal entrustPrice, long entrustVolume, IntegratedEntrust integratedEntrust, Instruction instruction) {
        try {
            if (Market.SHSE.value.equals(integratedEntrust.getMarket())) {
                String liaisonTel = integratedEntrust.getLiaisonTel();
                if (Strings.isNullOrEmpty(liaisonTel)) {
                    integratedEntrust.setErrorCode(ErrorCode.REQ_PARAMETER_ERROR.value);
                    integratedEntrust.setErrorMsg("对手方交易员不能为空");
                    return false;
                }
                if (liaisonTel.length() < 4) {
                    integratedEntrust.setErrorCode(ErrorCode.REQ_PARAMETER_ERROR.value);
                    integratedEntrust.setErrorMsg("对手方交易员不能小于4位");
                    return false;
                }
                integratedEntrust.setPropSeatNo(integratedEntrust.getLiaisonTel().substring(1, 4));
                if (Strings.isNullOrEmpty(integratedEntrust.getPropSeatName()))
                    integratedEntrust.setPropSeatName(integratedEntrust.getPropSeatNo());
            }
            BigDecimal zero = new BigDecimal("0.0");
            // 解除质押和中途换券无需判断
            if (!EntrustProp.isRepurchaseReleaseOrExchange(integratedEntrust.getEntrustProp())
                    && (TradeUtils.equals(integratedEntrust.getEntrustBalance(), zero)
                        || TradeUtils.equals(integratedEntrust.getBackBalance(), zero))) {
                integratedEntrust.setErrorCode(ErrorCode.REQ_PARAMETER_ERROR.value);
                integratedEntrust.setErrorMsg("回购金额或到期结算金额不能为0");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.info("协议回购缺少必要信息：{}", e);
            return false;
        }
    }

    public void handleParams(IntegratedEntrust integratedEntrust) {
        if (StrUtil.isEmpty(integratedEntrust.getPropSeatName())) {
            integratedEntrust.setPropSeatName(integratedEntrust.getPropSeatNo());
        }
    }

    public boolean checkCommonParamV1(BigDecimal entrustPrice, long entrustVolume, IntegratedEntrust integratedEntrust, Instruction instruction) {
//        if (EnumUtil.in(instruction.getTradeMarket(), ))

        // TODO 不应在校验中进行值的处理
        if (StrUtil.isEmpty(integratedEntrust.getPropSeatName())) {
            integratedEntrust.setPropSeatName(integratedEntrust.getPropSeatNo());
        }

        checkSpecilParamInShMarket(integratedEntrust);
        if (Market.SHSE.value.equals(integratedEntrust.getMarket())) {
            String liaisonTel = integratedEntrust.getLiaisonTel();
            if (Strings.isNullOrEmpty(liaisonTel)) {
                integratedEntrust.setErrorCode(ErrorCode.REQ_PARAMETER_ERROR.value);
                integratedEntrust.setErrorMsg("对手方交易员不能为空");
                return false;
            }
            if (liaisonTel.length() < 4) {
                integratedEntrust.setErrorCode(ErrorCode.REQ_PARAMETER_ERROR.value);
                integratedEntrust.setErrorMsg("对手方交易员不能小于4位");
                return false;
            }
            integratedEntrust.setPropSeatNo(integratedEntrust.getLiaisonTel().substring(1, 4));
            if (Strings.isNullOrEmpty(integratedEntrust.getPropSeatName()))
                integratedEntrust.setPropSeatName(integratedEntrust.getPropSeatNo());
        }
        BigDecimal zero = new BigDecimal("0.0");
        // 解除质押和中途换券无需判断
        if (!EntrustProp.isRepurchaseReleaseOrExchange(integratedEntrust.getEntrustProp())
                && (TradeUtils.equals(integratedEntrust.getEntrustBalance(), zero)
                    || TradeUtils.equals(integratedEntrust.getBackBalance(), zero))) {
            integratedEntrust.setErrorCode(ErrorCode.REQ_PARAMETER_ERROR.value);
            integratedEntrust.setErrorMsg("回购金额或到期结算金额不能为0");
            return false;
        }
        return true;

    }

    private void checkSpecilParamInShMarket(IntegratedEntrust integratedEntrust) {
        if (Market.SHSE.value.equals(integratedEntrust.getMarket())) {
            String liaisonTel = integratedEntrust.getLiaisonTel();
            if (Strings.isNullOrEmpty(liaisonTel)) {
                integratedEntrust.setErrorCode(ErrorCode.REQ_PARAMETER_ERROR.value);
                integratedEntrust.setErrorMsg("对手方交易员不能为空");
            }
            if (liaisonTel.length() < 4) {
                integratedEntrust.setErrorCode(ErrorCode.REQ_PARAMETER_ERROR.value);
                integratedEntrust.setErrorMsg("对手方交易员不能小于4位");
            }
            integratedEntrust.setPropSeatNo(integratedEntrust.getLiaisonTel().substring(1, 4));

        }
    }

}
