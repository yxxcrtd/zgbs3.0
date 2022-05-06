package com.joyintech.tams.invest.entrust.etrconbond.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.etrconbond.dto.EtrConBondDto;
import com.joyintech.tams.invest.entrust.etrconbond.service.EtrConBondService;
import com.joyintech.tams.invest.entrust.etrwtspotex.dto.EtrInvestPersonInfoDto;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.tools.util.ValidationUtil;
import com.joyintech.tools.util.ValidationUtil.ErrorMessage;
import com.joyintech.tools.util.ValidationUtil.ValidResult;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;

import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 可转债申购多对多指令新建
 *
 * @author gaopeng
 * @version 1.0 2019年12月18日
 * @see EtrConBond003Controller
 * @since 1.0
 */

@Controller
@PageId("EtrConBond003")
@RequestMapping("/invest/entrust/etrconbond/EtrConBond003")
public class EtrConBond003Controller extends BaseController {
	
    /**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
    /**
     * 可转债委托service
     */
    @Autowired
    private EtrConBondService etrConBondService;

    
    @Override
    public String init(Model model) {
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate", DateUtil.format(sysDate));
            //导入
        String tempList = getParam("tempList");
        EtrConBondDto dto = etrConBondService.getEntrustListData(tempList);
        model.addAttribute(GlobalConstant.ENTITY, dto);
        model.addAttribute("etrConBondDtoList", StringUtil.isEmptyList(
            dto.getSelectedItem()) ? null : JsonUtils.object2Json(dto.getSelectedItem()));
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("etrPs", DictCodeList.交易方向_申购.getValue());
        model.addAttribute("INV_AIM_default", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);
        return super.init(model);
    }
    
    /**
     * 导入时的check
     *
     * @param map dto
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("checkImportData")
    public AjaxResult checkImportData(String fileName) {
        return etrConBondService.checkImportData(fileName);
    }
    
    /**
     * 保存按钮点击时的check
     *
     * @param List<EtrConBondDto> dtoList
     * @param BindingResult result
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody EtrConBondDto allDto, BindingResult result) {
        List<EtrConBondDto> dtoList = allDto.getSelectedItem();
        List<ValidMessage> messages = new ArrayList<>();
        for (int i = 0; i < dtoList.size(); i++ ) {
            dtoList.get(i).setEtrInvestPersonInfo(new EtrInvestPersonInfoDto());
            dtoList.get(i).getEtrInvestPersonInfo().setInvManagerId(LoginUserUtil.getUserName());
            dtoList.get(i).getEtrInvestPersonInfo().setRoom(LoginUserUtil.getDeptCode());
            dtoList.get(i).getEtrInvestPersonInfo().setCounterId(etrConBondService.getCounterIdByFinprodId(dtoList.get(i).getFinprodId()));
            ValidResult itemResult = ValidationUtil.validateBean(dtoList.get(i),
                ValidateGroupEdit.class);
            if (!StringUtil.isEmptyList(itemResult.getAllErrors())) {
                StringBuffer message = new StringBuffer();
                message.append(getMessage("ETR_E_0025", String.valueOf(i + 1)));
                for (ErrorMessage errorMessage : itemResult.getAllErrors()) {
                    message.append("<br />");
                    message.append(errorMessage.getMessage());
                }
                messages.add(new ValidMessage(message.toString()));
            }
        }
        if (messages.size() > 0) {
            AjaxResult errorRs = new AjaxResult();
            errorRs.setErrorList(messages);
            return errorRs;
        }
        return commonCheck(result, dtoList.get(0).getOperateType());
    }
    /**
     * 批量提交
     * 
     * @param List<EtrConBondDto> dtoList
     * @param String msg
     * @throws WorkFlowException 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody EtrConBondDto allDto, BindingResult br) throws Exception {
    	   AjaxResult ajaxResult = this.check(allDto, br);
        if (!StringUtil.isEmptyList(ajaxResult.getErrorList())) {
            return ajaxResult;
        }
        List<EtrConBondDto> dtoList = allDto.getSelectedItem();
        for(EtrConBondDto dto: dtoList) {
            if(!StringUtil.isNotEmpty(dto.getEntrustId())){
                BussinessCodeParameter param = new BussinessCodeParameter();
                param.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_可转债申购委托));
                String code = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
                dto.setEntrustId(code);
            }
            dto.getEtrInvestPersonInfo().setInvManagerId(dto.getCreateUser());
            dto.getEtrInvestPersonInfo().setRoom(dto.getCreateDept());
            dto.setCounterId(etrConBondService.getCounterIdByFinprodId(dto.getFinprodId()));
           
        }
        // ----------add by zhangrui1 20200704 预占功能 start----------
        if (StringUtil.equals(BussinessConstant.SUBMIT, allDto.getOperateType())) {
            AjaxResult ar = EntrustRdsCommonAop.doListPreOccupy(allDto.getSelectedItem(),
                    DictCodeList.平安资产类型_债券.getValue(),
                    BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue(),
                    false, null);
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        // ----------add by zhangrui1 20200704 预占功能 end----------
        // add by zhangrui1 批量提交设置风控指标明细 start
        EntrustRdsCommonAop.riskListToChildren(allDto.getRiskTrialRetDto(), dtoList,
                (dto, list) -> dto.setRiskTrialRetDto(list));
        // add by zhangrui1 批量提交设置风控指标明细 end
        ProcessParameter<EtrConBondDto> processParameter = new ProcessParameter<EtrConBondDto>();
        WorkFlowParameter<EtrConBondDto> params = new WorkFlowParameter<EtrConBondDto>();
        params.setPageId("EtrConBond002");
        params.setElementId(dtoList.get(0).getElementId());
        params.setBusinessCode(EtrConBondDto::getEntrustId);
        params.setBusinessType(DictCodeListExt2.共通业务类型_可转债申购委托.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
            entity -> this.getMessage("EtrConBond001") + "(" + entity.getPortfolioId() + ")"
                      + ")-(" +  this.getMessage("EtrConBond001.purchDate") + ":" + DateUtil.format(entity.getEntrustDate())
                      + this.getMessage("EtrBondFirstLBidding002.finprodId") + ":"
                      + entity.getFinprodMarketId()
                      );
        processParameter.setWorkFlowParameter(params);
        String msg = etrConBondService.start(processParameter, dtoList.toArray(new EtrConBondDto[dtoList.size()]));
        return new AjaxResult(msg);
    }
    
}
