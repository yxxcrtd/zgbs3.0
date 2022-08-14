
package com.joyintech.tams.invest.trade.trdrepotrade.validator;


import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.trade.common.validator.TradeCommonValidator;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTrade004Dto;
import com.joyintech.tams.invest.trade.trdrepotrade.service.TrdRepoTrade004Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 质押式回购->质押券置换-》画面check验证
 *
 * @author nixiukuan
 * @version 1.0 2019年11月9日
 * @see TrdRepoTrade004DtoValidator
 * @since 1.0
 */
@Component
public class TrdRepoTrade004DtoValidator implements TradeCommonValidator {


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {
        TrdRepoTrade004Dto inputDto = (TrdRepoTrade004Dto) value;
        List<ValidMessage> errorList = new ArrayList<>();
        // 起息日<置换日期<到期日
        if (inputDto != null
                && inputDto.getVdate() != null
                && inputDto.getMdate() != null
                && inputDto.getSubstitutionDate() != null) {
            if (DateUtil.compareDate(inputDto.getSubstitutionDate(),
                    inputDto.getVdate()) <0 || DateUtil.compareDate(inputDto.getMdate(),
                    inputDto.getSubstitutionDate()) <0) {
                ValidMessage validMessage = new ValidMessage("置换日期需在起息日与到期日之间!");
                errorList.add(validMessage);
            }
        }
        //最终质押券列表不能为空
        if(StringUtil.isEmptyList(inputDto == null?null:inputDto.getBussResultRepoSec())||inputDto == null?true:inputDto.getBussResultRepoSec().size()==0)
        {
            ValidMessage validMessage = new ValidMessage("最终质押券列表不能为空!");
            errorList.add(validMessage);
        }
        else
        {
           for(PledgeBondInfoDto pledgeBondInfoDto : inputDto.getBussResultRepoSec()) {
               if(pledgeBondInfoDto.getPledgePrin()==null) {
                   ValidMessage validMessage = new ValidMessage("最终质押券列表中质押券面总额不能为空!");
                   errorList.add(validMessage);
                   break;
               }
           }
        }

        return errorList;
    }
}
