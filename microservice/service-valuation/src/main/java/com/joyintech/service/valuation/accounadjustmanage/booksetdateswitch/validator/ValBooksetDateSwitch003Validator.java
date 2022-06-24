/*
 * 文件名：ValBooksetDateSwitch003Validator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：weipeng
 * 修改时间：2019年2月18日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.service.valuation.accounadjustmanage.booksetdateswitch.validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.valuation.accounadjustmanage.booksetdateswitch.dto.ValBooksetDateSwitch003Dto;
import com.joyintech.service.valuation.accounadjustmanage.booksetdateswitch.service.ValBooksetDateSwitch003Service;
import com.joyintech.tams.common.base.dao.BokBooksetDateMapper;
import com.joyintech.tams.common.base.dao.BokBooksetMapper;
import com.joyintech.tams.common.base.entity.BokBookset;

/**
 * 修改 账套日期
 * 
 * @author zhangwei
 * @version 2.0 2019年2月18日
 * @see ValBooksetDateSwitch003Validator
 * @since 2.0
 */
@Component(value="serviceValBooksetDateSwitch003Validator")
public class ValBooksetDateSwitch003Validator implements BeanValidator {

    @Autowired
    private BokBooksetDateMapper bokBooksetDateMapper;

    @Autowired
    private BokBooksetMapper bokBooksetMapper;

    @Autowired
    private ValBooksetDateSwitch003Service valBooksetDateSwitch003Service;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        List<ValidMessage> validMessageList = new ArrayList<ValidMessage>();
        List<ValBooksetDateSwitch003Dto> list = ((ValBooksetDateSwitch003Dto)value).getSelectedItem();
        for (ValBooksetDateSwitch003Dto item : list) {
            String booksetId = item.getBooksetId();
            String booksetDate = item.getBooksetDate();
            if (StringUtil.isEmpty(booksetDate) || StringUtil.isEmpty(booksetId)) {
                return validMessageList;
            }

            // 判断是否查出当前记录，防止记录已被删除
            if (bokBooksetDateMapper.selectByPrimaryKey(booksetId) == null) {
                String msg = this.getMessage("VAL_COMMON_E_0001.1");
                validMessageList.add(new ValidMessage(msg));
            }
            BokBookset valBokBookset = bokBooksetMapper.selectByPrimaryKey(booksetId);
            if (valBokBookset == null) {
                String msg = this.getMessage("VAL_COMMON_E_0001.2");
                validMessageList.add(new ValidMessage(msg));
            }
            else {
                Date startDate = valBokBookset.getStartDate();
                Date endDate = valBokBookset.getEndDate();
                Date date = DateUtil.parse(booksetDate);
                if (DateUtil.compareTo(startDate, date) > 0 || DateUtil.compareTo(date, endDate) > 0) {
                    String msg = this.getMessage("VAL_COMMON_E_0003", this.getMessage("ValZhangDateSwitch001.booksetDate"),
                        this.getMessage("ValZhangDateSwitch001.booksetDateStart") + "【" + DateUtil.format(startDate,
                            "yyyy-MM-dd") + "】",
                        this.getMessage("ValZhangDateSwitch001.booksetDateEnd") + "【" + DateUtil.format(endDate,
                            "yyyy-MM-dd") + "】");
                    validMessageList.add(new ValidMessage(msg));
                }
            }
        }
        // 是否有流程中数据
        List<String> booksetIds = checkIsProcessing(list);
        if (CollectionUtils.isNotEmpty(booksetIds)) {
            validMessageList.add(new ValidMessage(getMessage("COMMON_E_0021",
                getMessage("ValZhangDateSwitch001.booksetId"), StringUtil.join(booksetIds, ","))));
        }
        return validMessageList;
    }

 // 是否有流程中数据
    private List<String> checkIsProcessing(List<ValBooksetDateSwitch003Dto> list) {
        List<String> booksetIds = Lists.newArrayList();
        for (ValBooksetDateSwitch003Dto item : list) {
           if(valBooksetDateSwitch003Service.checkBusinessDataInFlow(item)) {
               booksetIds.add(item.getBooksetId());
           }
        }
        return booksetIds;
    }

}
