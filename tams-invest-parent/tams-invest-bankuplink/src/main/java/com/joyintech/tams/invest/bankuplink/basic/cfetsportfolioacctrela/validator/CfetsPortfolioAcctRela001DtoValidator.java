/*
 * 文件名：CfetsBondDeal.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.bankuplink.basic.cfetsportfolioacctrela.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.invest.bankuplink.basic.cfetsportfolioacctrela.dto.CfetsPortfolioAcctRela001Dto;
import com.joyintech.tams.invest.bankuplink.basic.cfetsportfolioacctrela.service.CfetsPortfolioAcctRelaService;
import org.springframework.util.CollectionUtils;

/**
 * 现券对话报价验证类
 * 
 * @author zhangliang
 * @version 1.0 2020年5月14日
 * @see CfetsPortfolioAcctRela001DtoValidator
 * @since 1.0
 */
@Component
public class CfetsPortfolioAcctRela001DtoValidator implements BeanValidator {
    
    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsPortfolioAcctRelaService cfetsPortfolioAcctRelaService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        CfetsPortfolioAcctRela001Dto rela001Dto = (CfetsPortfolioAcctRela001Dto) value;
        List<ValidMessage> validList = new ArrayList<ValidMessage>();
        List<CfetsPortfolioAcctRela001Dto> list = rela001Dto.getSelectedItem();
        if (StringUtil.equals(rela001Dto.getOperateType(), "delete")) {
            // 删除check
            if (!CollectionUtils.isEmpty(list)) {
                for (CfetsPortfolioAcctRela001Dto dto : list) {
                    // 验证信息
                    if (!StringUtil.isNotEmpty(dto.getPartyIdOld())) {
                        dto.setPartyIdOld(dto.getPartyId());
                    }
                    if (!StringUtil.isNotEmpty(dto.getPortfolioIdOld())) {
                        dto.setPortfolioIdOld(dto.getPortfolioId());
                    }
                    if (!StringUtil.isNotEmpty(dto.getCfetsAcctTypeOld())) {
                        dto.setCfetsAcctTypeOld(dto.getCfetsAcctType());
                    }
                }
            }
        }else {

            if (!CollectionUtils.isEmpty(list)) {
                for (CfetsPortfolioAcctRela001Dto dto : list) {
                    //组合代码
                    if (StringUtils.isNotEmpty(dto.getPortfolioId())) {
                        validList.add(new ValidMessage("请输入组合代码"));
                    }
                    //机构21位码
                    if (StringUtils.isEmpty(dto.getPartyCustomerId())) {
                        validList.add(new ValidMessage("请输入机构21位码"));
                    }
                    //账户类型
                    if (StringUtils.isEmpty(dto.getCfetsAcctType())) {
                        validList.add(new ValidMessage("请输入账户类型"));
                    }
                    //机构6位码
                    if (StringUtils.isEmpty(dto.getPartyId())) {
                        validList.add(new ValidMessage("请输入机构6位码"));
                    }
                    //机构名称
                    if (StringUtils.isEmpty(dto.getPartyName())) {
                        validList.add(new ValidMessage("请输入机构名称"));
                    }

                    // 验证信息
                    if (!StringUtil.isNotEmpty(dto.getPartyIdOld())) {
                        dto.setPartyIdOld(dto.getPartyId());
                    }
                    if (!StringUtil.isNotEmpty(dto.getPortfolioIdOld())) {
                        dto.setPortfolioIdOld(dto.getPortfolioId());
                    }
                    if (!StringUtil.isNotEmpty(dto.getCfetsAcctTypeOld())) {
                        dto.setCfetsAcctTypeOld(dto.getCfetsAcctType());
                    }

                    if (StringUtils.isNotEmpty(dto.getPortfolioId())) {
                        String portfolioIds = dto.getPortfolioId();
                        for (String portfolioId : portfolioIds.split(",")) {
                            dto.setPortfolioId(portfolioId);
                            if (StringUtil.equals(dto.getOperateType(), "submit")) {
                                int count = cfetsPortfolioAcctRelaService.checkPortfolioIdAcctTypeExists(dto);
                                if (count > 0) {
                                    validList.add(new ValidMessage("组合代码[" + portfolioId + "]+账户来源[" + dto.getPartyIdOld() + "]已经存在！"));
                                }
                            }
                        }
                    }

                }
            }
        }

        return validList;
    }

}
