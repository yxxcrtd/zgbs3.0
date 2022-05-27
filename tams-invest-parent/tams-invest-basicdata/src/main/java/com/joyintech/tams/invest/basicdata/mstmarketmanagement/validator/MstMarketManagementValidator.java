package com.joyintech.tams.invest.basicdata.mstmarketmanagement.validator;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.basicdata.mstmarketmanagement.dto.MstMarketManagementDto;
import com.joyintech.tams.invest.basicdata.mstmarketmanagement.service.MstMarketManagement001Service;
import com.joyintech.tams.invest.common.service.InvDataCheckService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author huwenbin
 * @version 1.0 2018年8月20日
 * @see MstMarketManagementValidator
 * @since 1.0
 */
@Component
public class MstMarketManagementValidator implements BeanValidator {

    @Autowired
    private IPlatformDate platformDate;

    @Autowired
    MstMarketManagement001Service mstMarketManagement001Service;

    @Override
    public List<ValidMessage> valid(Object value) {
        MstMarketManagementDto dto = (MstMarketManagementDto)value;
        List<ValidMessage> errorList = new ArrayList<>();
        if (StringUtil.equals(dto.getOperateType(), "submit") || StringUtil.equals(dto.getOperateType(), "save") ) {
            if (StringUtil.isEmpty(dto.getoType())){
                errorList.add(new ValidMessage("oType","对象类型不可为空，请重新填写！"));
            }
            if (StringUtil.isEmpty(dto.getoCode())){
                errorList.add(new ValidMessage("oCode","对象代码不可为空，请重新填写！"));
            }
            if (StringUtil.isEmpty(dto.getoCodeName())){
                errorList.add(new ValidMessage("oCodeName","对象名称不可为空，请重新填写！"));
            }
            if (StringUtil.isEmpty(dto.getGradeDate())){
                errorList.add(new ValidMessage("gradeDate","评级日期不可为空，请重新填写！"));
            }
            if (StringUtil.isNotEmpty(dto.getGradeDate()) && DateUtil.compareDate(dto.getGradeDate(), platformDate.getSysDateStr()) > 0){
                errorList.add(new ValidMessage("gradeDate","评级日期不能选择未来日期！"));
            }
            if (StringUtil.isEmpty(dto.getShortLongTerm())){
                errorList.add(new ValidMessage("shortLongTerm","评级类型不可为空，请重新填写！"));
            }
            if (StringUtil.isEmpty(dto.getGradeResult())){
                errorList.add(new ValidMessage("gradeResult","评级结果不可为空，请重新填写！"));
            }
            if (StringUtil.equals(dto.getoType(),DictCodeList.评级类型_主体评级.getValue()) && StringUtil.isEmpty(dto.getDirectionChange())){
                errorList.add(new ValidMessage("gradeResult","变动方向不可为空，请重新填写！"));
            }
            if (StringUtil.isEmpty(dto.getGradeOrgId())){
                errorList.add(new ValidMessage("gradeOrgId","评级机构不可为空，请重新填写！"));
            }

            if (StringUtil.isNotEmpty(dto.getoType()) && StringUtil.isNotEmpty(dto.getGradeOrgId())
                && StringUtil.isNotEmpty(dto.getGradeDate()) && StringUtil.isNotEmpty(dto.getoCode())){
                if (mstMarketManagement001Service.checkBusinessDataInFlow(dto)){
                    errorList.add(new ValidMessage("存在流程中重复数据,评级类型:" + dto.getoTypeName() + ",评级对象:" + dto.getoCodeName() +
                            ",评级机构:" + dto.getGradeOrgIdName() + ",评级日期:" + dto.getGradeDate() + ",请重新填写！"));
                }
            }
            if (StringUtil.equals(dto.getPageModel(),"1") && StringUtil.equals(dto.getOldGradeResult(),dto.getGradeResult())){
                errorList.add(new ValidMessage("gradeResult","请修改评级结果！"));
            }
        }
        return errorList;
    }

}
