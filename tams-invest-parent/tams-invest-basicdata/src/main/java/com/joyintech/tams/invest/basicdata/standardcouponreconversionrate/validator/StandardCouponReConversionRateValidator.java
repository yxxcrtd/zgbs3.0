package com.joyintech.tams.invest.basicdata.standardcouponreconversionrate.validator;


import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.invest.basicdata.standardcouponreconversionrate.dto.StandardCouponReConversionRateDto;
import com.joyintech.tams.invest.basicdata.standardcouponreconversionrate.service.StandardCouponReConversionRateService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class StandardCouponReConversionRateValidator implements BeanValidator {

    @Autowired
    private StandardCouponReConversionRateService standardCouponReConversionRateService;

    @Override
    public List<ValidMessage> valid(Object o) {
        StandardCouponReConversionRateDto dto = (StandardCouponReConversionRateDto) o;
        List<ValidMessage> resultList = new ArrayList<ValidMessage> ();
        if (StringUtil.equals(dto.getOperateType(), "new")){
            StandardCouponReConversionRateDto vo = new StandardCouponReConversionRateDto();
            vo.setBrokerId(dto.getBrokerId());
            //是否存在流程中
            if (isInProcess(dto.getBrokerId(), "StandardCouponReConversionRate002")) {
                resultList.add(new ValidMessage(this.getMessage("存在流程中的数据，请维护相同模式的数据", dto.getTandardRate ())));
                return resultList;
            }
            if(StringUtils.isNotEmpty(vo.getFinprodId())&&StringUtils.isEmpty(vo.getAccountType())){
                vo.setFinprodId("system");
                List<StandardCouponReConversionRateDto> list = standardCouponReConversionRateService.getList(dto);
                if(!list.isEmpty()&&list.size()>0){
                    resultList.add(new ValidMessage(this.getMessage("已存在同一券商不同模式的数据，请维护相同模式的数据", dto.getTandardRate ())));
                    return resultList;
                }
            }
            if(StringUtils.isEmpty(vo.getFinprodId())&&StringUtils.isNotEmpty(vo.getAccountType())){
                vo.setAccountType("system");
                List<StandardCouponReConversionRateDto> list = standardCouponReConversionRateService.getList(dto);
                if(!list.isEmpty()&&list.size()>0){
                    resultList.add(new ValidMessage(this.getMessage("已存在同一券商不同模式的数据，请维护相同模式的数据", dto.getTandardRate ())));
                    return resultList;
                }
            }
        }
        if(StringUtil.equals(dto.getOperateType(), "edit")){
            if(StringUtil.isNotEmpty(dto.getStartDate())){
                String platDate = DateUtil.format(DateUtil.platDateFromCache());
                if(DateUtil.parse(dto.getStartDate()).getTime() < DateUtil.parse(platDate).getTime()){
                    resultList.add(new ValidMessage(this.getMessage("该债券折算率已经使用，请重新建立！")));
                }
            }
        }
        if (StringUtil.equals(dto.getOperateType(), "new") || StringUtil.equals(dto.getOperateType(), "update")) {
            if(StringUtils.isEmpty(dto.getFinprodId())&&StringUtils.isEmpty(dto.getAccountType())){
                resultList.add(new ValidMessage(this.getMessage("资产代码、资金账户不可都为空", dto.getTandardRate ())));
                return resultList;
            }
            if(StringUtil.isNotEmpty(dto.getTandardRate ())){
                if(!NumberUtils.isNumber(dto.getTandardRate ())) {
                    resultList.add(new ValidMessage(this.getMessage("输入的标准券再折算率不是数字", dto.getTandardRate ())));
                }else {
                    BigDecimal tandardRate = new BigDecimal (dto.getTandardRate ());
                    BigDecimal tandardRate0 = new BigDecimal ("0");
                    BigDecimal tandardRate1 = new BigDecimal ("100");
                    if(tandardRate.compareTo (tandardRate0) < 0 || tandardRate.compareTo (tandardRate1) > 0){
                        resultList.add(new ValidMessage(this.getMessage("输入的标准券再折算率不在0和100之间", dto.getTandardRate ())));
                    }
                }
            }
            if(StringUtil.isNotEmpty(dto.getStartDate())){
                //获取平台日
                String platDate = DateUtil.format(DateUtil.platDateFromCache());
                if(DateUtil.parse(dto.getStartDate()).getTime() < DateUtil.parse(platDate).getTime()){
                    resultList.add(new ValidMessage(this.getMessage("开始使用日期不允许小于平台日期，请重新设置！")));
                }
                if(StringUtil.isNotEmpty(dto.getEndDate())){
                    if(DateUtil.parse(dto.getEndDate()).getTime() <= DateUtil.parse(dto.getStartDate()).getTime()){
                        resultList.add(new ValidMessage(this.getMessage("结束使用日期需大于开始使用日期，请重新设置！")));
                    }
                }
            }
            if(!StringUtil.equals(dto.getOperateType(), "update")){
                StandardCouponReConversionRateDto entity = standardCouponReConversionRateService.selectByPrimaryKey(dto);
                if(entity != null){
                    resultList.add(new ValidMessage(this.getMessage("该券商对应债券的再折算率维护已存在！")));
                }
            }
        }
        return resultList;
    }

    /**
     * 判断是否在流程中
     *
     * @param pk1
     * @return
     */
    public Boolean isInProcess(String pk1, String... pageId) {
        if (StringUtil.isEmpty(pk1)) return Boolean.FALSE;
        WflValidInputParameter wflValidInputParameter = null;
        for (String str : pageId) {
            wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setPageId(str);
            wflValidInputParameter.setBusinessDataPk1(pk1);
            wflValidInputParameter.setSeachMode("01");
            List<WflValidInputParameter> result = standardCouponReConversionRateService.getBusinessData(wflValidInputParameter);
            if (CollectionUtils.isNotEmpty(result)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
