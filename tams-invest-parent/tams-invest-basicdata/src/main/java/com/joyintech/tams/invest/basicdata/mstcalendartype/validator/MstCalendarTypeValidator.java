/*
 * 文件名：MstCalendarTypeValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年8月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.basicdata.mstcalendartype.validator;

import java.util.ArrayList;
import java.util.List;

import com.joyintech.tams.invest.basicdata.mstcalendartype.dto.MstCalendarType001Dto;
import com.joyintech.tams.invest.basicdata.mstcalendartype.service.MstCalendarType001Service;
import com.joyintech.tams.invest.basicdata.mstcaptialaccount.dto.MstCaptialAccountDto;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author qiangchengxi
 * @version 1.0 2018年8月20日
 * @see MstCalendarTypeValidator
 * @since 1.0
 */
@Component
public class MstCalendarTypeValidator implements BeanValidator {

    @Autowired
    private MstCalendarType001Service mstCalendarType001Service;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        // TISHKS-725:增加日历类型是否被使用校验 modify by fuyongsheng 20220507 start
        MstCalendarType001Dto dto = (MstCalendarType001Dto)value;
        ArrayList<ValidMessage> errorList = new ArrayList<>();
        if (WorkFlowUtil.isNotFromToDoList()) {
            if(StringUtils.equals(dto.getOperateType(), "delete")){
                List<MstCalendarType001Dto> list = dto.getSelectedItem();
                for (MstCalendarType001Dto mst : list) {
                    String calendarName = mst.getCalendarName();
                    // 查询当前系统是否存在该日历名称
                    int num = mstCalendarType001Service.hasCalendarName(calendarName);
                    if(num > 0){
                        errorList.add(new ValidMessage("选中的日历类型" + "(" + calendarName + ")" + "已经被使用，不允许删除！"));
                    }
                }
            }
        }
        // TISHKS-725:增加日历类型是否被使用校验 modify by fuyongsheng 20220507 end
        /*if (WorkFlowUtil.isNotFromToDoList()) {
            if(StringUtil.equals(dto.getPageModel(),GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
                if(!StringUtil.isNotEmpty(dto.getCalendarName())){
                    errorList.add(new ValidMessage("calendarName", getMessage("BUSS_COM_E_0038",
                            getMessage("MstCalendarType002.calendarName"))));
                }
                if(!StringUtil.isNotEmpty(dto.getCalendarType())){
                    errorList.add(new ValidMessage("calendarType", getMessage("BUSS_COM_E_0038",
                            getMessage("MstCalendarType002.calendarTypeInclude"))));
                }
            }
            else if (StringUtil.equals(dto.getOperateType(), "search")) {
                    if (!StringUtil.isNotEmpty(dto.getStartCDate()) && !StringUtil.isNotEmpty(dto.getEndCDate())) {
                        errorList.add(new ValidMessage("cDate", getMessage("BUSS_COM_E_0038",
                            getMessage("MstCalendarType004.cDate"))));
                }
            }
        }
        else if(StringUtil.equals(dto.getPageModel(),GlobalConstant.DETAIL_PAGE_MODEL_NEW)||
                StringUtil.equals(dto.getPageModel(),GlobalConstant.DETAIL_PAGE_MODEL_EDIT)){
            if(!StringUtil.isNotEmpty(dto.getCalendarName())){
                errorList.add(new ValidMessage("calendarName", getMessage("BUSS_COM_E_0038",
                        getMessage("MstCalendarType002.calendarName"))));
            }
            if(!StringUtil.isNotEmpty(dto.getCalendarType())){
                errorList.add(new ValidMessage("calendarType", getMessage("BUSS_COM_E_0038",
                        getMessage("MstCalendarType002.calendarTypeInclude"))));
            }
        }*/
        return errorList;
    }
}
