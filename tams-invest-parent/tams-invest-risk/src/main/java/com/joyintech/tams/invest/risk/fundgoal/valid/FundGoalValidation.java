package com.joyintech.tams.invest.risk.fundgoal.valid;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.invest.risk.fundgoal.dto.FundGoalDto;
import com.joyintech.tams.invest.risk.fundgoal.service.FundGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Classname FundGoalValidation
 * @Description TODO
 * @author zhuhanlu 
 * @date 2020/11/3 16:28 
 * @version 1.0 
 */
@Component
public class FundGoalValidation  implements BeanValidator {

    @Autowired
    FundGoalService fundGoalService;
    @Override
    public List<ValidMessage> valid(Object o) {
        // 获取平台日期
        Date sysDate = DateUtil.platDateFromCache();
        FundGoalDto fundGoalDto = (FundGoalDto)o;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        if ((fundGoalDto.getPageModel().equals(GlobalConstant.DETAIL_PAGE_MODEL_NEW))&& StringUtil.isNotEmpty(fundGoalDto.getFinprodMarket())){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("finprodId",fundGoalDto.getFinprodId());
            map.put("goalDate",DateUtil.format(fundGoalDto.getGoalDate(), "yyyy-MM-dd"));
            if (fundGoalService.selectByPrimaryKey(map) != null){
                resultList.add(new ValidMessage("该资产在此投向维护日已有记录，不可再新增"));
            }
        }
        // TISHKS-666:下一投向公布日早于或等于投向维护日不允许提交 modify by fuyongsheng 20220505 start
//        if(DateUtil.compareDate(fundGoalDto.getGoalDate(),fundGoalDto.getNextPubDate())>0){
//            resultList.add(new ValidMessage("下一投向公布日早于投向维护日不允许提交"));
//        }
        if(DateUtil.compareDate(fundGoalDto.getGoalDate(),fundGoalDto.getNextPubDate())>=0){
            resultList.add(new ValidMessage("下一投向公布日早于或等于投向维护日不允许提交"));
        }
        // TISHKS-666:下一投向公布日早于或等于投向维护日不允许提交 modify by fuyongsheng 20220505 end
        // TISHKS-560:基金投向维护下一投向公布日必须大于平台日 add by liujiasheng 20220507 start
        if(DateUtil.compareDate(fundGoalDto.getNextPubDate(),sysDate)<=0){
            resultList.add(new ValidMessage("下一投向公布日早于或等于平台日不允许提交"));
        }
        // TISHKS-560:基金投向维护下一投向公布日必须大于平台日 add by liujiasheng 20220507 end
        if (fundGoalDto.getbEquityPer()!=null&&fundGoalDto.getbEquityPer().compareTo(new BigDecimal(0))<0){
            resultList.add(new ValidMessage("权益类占比不能小于0"));
        }
        if (fundGoalDto.getbCreditorRightsPer()!=null&&fundGoalDto.getbCreditorRightsPer().compareTo(new BigDecimal(0))<0){
            resultList.add(new ValidMessage("债券类占比不能小于0"));
        }
        if (fundGoalDto.getbCommodityDervPer()!=null&&fundGoalDto.getbCommodityDervPer().compareTo(new BigDecimal(0))<0){
            resultList.add(new ValidMessage("商品及衍生品占比不能小于0"));
        }
        if (fundGoalDto.getbDebtPer()!=null&&fundGoalDto.getbDebtPer().compareTo(new BigDecimal(0))<0){
            resultList.add(new ValidMessage("负债占比不能小于0"));
        }
        return resultList;
    }
}
