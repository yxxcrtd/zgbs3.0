package com.joyintech.tams.common.basicdata.marketdata.yieldcurveinfo.validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.basicdata.marketdata.yieldcurveinfo.dto.ComYieldCurveInfoDto;
import com.joyintech.tams.common.basicdata.marketdata.yieldcurveinfo.service.ComYieldCurveInfoService;
import com.joyintech.tams.component.base.core.MstWorkflowCommonService;
import com.joyintech.tams.component.base.core.dto.WflValidInputParameter;
import com.joyintech.tams.constant.DictCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 收益率曲线信息验证 〈一句话功能简述〉 〈功能详细描述〉
 *
 * @author wangzhao
 * @version 1.0 2018年8月9日
 * @see ComYieldCurveInfoValidator
 * @since 1.0
 */
@Transactional
@Component
public class ComYieldCurveInfoValidator implements BeanValidator {
    /**
     *
     */
    @Autowired
    private ComYieldCurveInfoService comYieldCurveInfoService;

    /**
     * 流程组件
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    @Override
    public List<ValidMessage> valid(Object value) {
        ComYieldCurveInfoDto dto = (ComYieldCurveInfoDto)value;
        ArrayList<ValidMessage> errorList = new ArrayList<>();
        Date sysDate = DateUtil.platDateFromCache();
        if (WorkFlowUtil.isNotFromToDoList()) {
            if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
                // 新建时曲线日期小于平台日期时
                Date curveDate = dto.getCurveDate();
                if (curveDate != null) {
                    if (DateUtil.compareDate(curveDate, sysDate) < 0) {
                        errorList.add(new ValidMessage("curveDate",
                            getMessage("BUSS_COM_E_0004",
                                getMessage("ComYieldCurveInfo001.curveDate"),
                                getMessage("ComYieldCurveInfo001.sysDate"))));
                    }
                    String curveCode = dto.getCurveCode();
                    Map<String, Object> pageParam = new HashMap<>();
                    pageParam.put("curveCode", curveCode);
                    pageParam.put("curveDate", DateUtil.format(curveDate));
                    pageParam.put("curveType", dto.getCurveType());
                    List<ComYieldCurveInfoDto> list = comYieldCurveInfoService.selectAllList(
                        pageParam);
                    if (list != null && !list.isEmpty()) {
                        errorList.add(new ValidMessage("curveCode", getMessage(
                            "BUSS_COM_E_0006", getMessage("ComYieldCurveInfo001.curveName") + ","
                                               + getMessage("ComYieldCurveInfo001.curveDate")+","+getMessage("ComYieldCurveInfo001.curveType"),",")));

                    }
                    WflValidInputParameter pk4  = new WflValidInputParameter();
                    pk4.setPageId("ComYieldCurveInfo002");
                    pk4.setBusinessDataPk1(dto.getCurveCode());
                    pk4.setBusinessDataPk2(dto.getCurveDateStr());
                    pk4.setBusinessDataPk3(dto.getCurveType());
                    if(mstWorkflowCommonService.isInWflProcess(pk4)){
                        errorList.add(new ValidMessage("数据已存在于流程中，不允许添加"));
                    }
                }
            }
            if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)) {
                // 点击编辑按钮，判断曲线日期小于平台日期
//                Date curveDate = dto.getCurveDate();
//                if (DateUtil.compareDate(curveDate, sysDate) < 0) {
//                    errorList.add(new ValidMessage("curveDate",
//                        getMessage("BUSS_COM_E_0018", getMessage("ComYieldCurveInfo001.curveDate"),
//                            getMessage("ComYieldCurveInfo001.sysDate"))));
//                }
                WflValidInputParameter pk4  = new WflValidInputParameter();
                pk4.setPageId("ComYieldCurveInfo002");
                pk4.setBusinessDataPk1(dto.getCurveCode());
                pk4.setBusinessDataPk2(dto.getCurveDateStr());
                pk4.setBusinessDataPk3(dto.getCurveType());
                if(mstWorkflowCommonService.isInWflProcess(pk4)){
                    errorList.add(new ValidMessage("数据已存在于流程中，不允许修改"));
                }
            }

            if (StringUtil.equals(dto.getOperateType(), "delete")) {
                // 点击删除操作时的判断
                if (dto.getSelectedItem() == null || dto.getSelectedItem().size() == 0) {
                    errorList.add(new ValidMessage("table", getMessage("COMMON_E_0002")));
                }
                // 判断曲线日期小于平台日期时，不能删除
                List<ComYieldCurveInfoDto> items = dto.getSelectedItem();
                for (ComYieldCurveInfoDto comYieldCurveInfoDto : items) {
                    Date curveDate = comYieldCurveInfoDto.getCurveDate();
//                    if (DateUtil.compareDate(curveDate, sysDate) < 0) {
//                        errorList.add(new ValidMessage("curveDate",
//                            getMessage("BUSS_COM_E_0015",
//                                getMessage("ComYieldCurveInfo001.curveDate"),
//                                getMessage("ComYieldCurveInfo001.sysDate"))));
//                    }
                }
            }
        }
        return errorList;
    }
}
