/*
 * 文件名：RskPortfolioPackageDtlValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年11月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.riskredis.quota.portfolioindicator.validator;

import java.util.ArrayList;
import java.util.List;

import com.joyintech.tams.invest.riskredis.quota.portfolioindicator.dto.RskPortfolioPackageDtlDto;
import com.joyintech.tams.invest.riskredis.quota.portfolioindicator.dto.RskPortfolioPackageDtlImp;
import org.springframework.stereotype.Component;

import com.joyintech.tams.common.base.entity.RskPortfolioPackageDtl;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;

/**
 * 组合包明细验证
 * 〈功能详细描述〉
 * @author 裴家俊
 * @version 1.0 2018年11月13日
 * @see RskPortfolioPackageDtlValidator
 * @since 1.0
 */
@Component
public class RskPortfolioPackageDtlValidator implements BeanValidator{

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        RskPortfolioPackageDtlDto dto = (RskPortfolioPackageDtlDto)value;
        List<RskPortfolioPackageDtlImp> rskPortfolioPackageDtlList = dto.getRskPortfolioPackageDtlList();
        if(null!=rskPortfolioPackageDtlList&&!rskPortfolioPackageDtlList.isEmpty()){
            if (rskPortfolioPackageDtlList.size() == 1){
                resultList.add(new ValidMessage("组合数据要大于1!"));
            }
            for(RskPortfolioPackageDtl entity:rskPortfolioPackageDtlList){
            	 //组合成分不可为空
                if(StringUtil.isEmpty(entity.getPortfolioId())){
                    resultList.add(new ValidMessage("",
                        getMessage("COMMON_E_0057", getMessage("RskSupervisionPortfPoint003.portfolioMix"))));
                    break;
                }
            }
        }
        else{
        	 resultList.add(new ValidMessage("",
                     getMessage("COMMON_E_0057", getMessage("RskSupervisionPortfPoint003.portfolioMix"))));
        }
        return resultList;
    }

}
