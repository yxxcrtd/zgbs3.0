package com.joyintech.tams.exchange.period.creditprotectioncertificate.validator;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.BillingDetailsDto;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodSaveDto;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈功能详细描述〉
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月17日
 * @since 1.0
 */
@Component
public class CreditProtectionCertificatePeriodSaveDtoValidator implements BeanValidator {

    @Override
    public List<ValidMessage> valid(Object o) {
        List<ValidMessage> errorList = new ArrayList<>();
        CreditProtectionCertificatePeriodSaveDto dto = (CreditProtectionCertificatePeriodSaveDto) o;
        if (StringUtil.equals(dto.getSettleMethod(), DictCodeListExt4.结算方式_标的_实物结算.getValue())) {
            if (dto.getDeliverBond() == null) {
                ValidMessage validMessage = new ValidMessage();
                validMessage.setField("settleMethod");
                validMessage.setMessage("结算方式为实物结算时，交付债券不能为空");
            }
            if (CollectionUtils.isEmpty(dto.getBillingDetails())) {
                ValidMessage validMessage = new ValidMessage();
                validMessage.setField("deliverBondAmount");
                validMessage.setMessage("结算方式为实物结算时，债券交付数量不能为空");
            } else {
                List<BillingDetailsDto> billingDetails = dto.getBillingDetails();
                for (BillingDetailsDto billingDetail : billingDetails) {
                    if (null == billingDetail.getDeliverBondAmount()) {
                        ValidMessage validMessage = new ValidMessage();
                        validMessage.setField("deliverBondAmount");
                        validMessage.setMessage("结算方式为实物结算时，债券交付数量不能为空");
                        break;
                    }
                }
            }
            //保护凭证的名义本金数量超过组合下可用数量
            //债券交付数量不超过组合下的债券可用数量
        }
        return errorList;
    }
}
