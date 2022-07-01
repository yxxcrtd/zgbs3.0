package com.joyintech.tams.invest.entrust.etrinvestbondinfo.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.EtrInvestBondInfoDto;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.service.EtrInvestBondInfoService;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.validator.EtrInvestBondInfoValidator;
import com.joyintech.tools.util.ValidationUtil;
import com.joyintech.tools.util.ValidationUtil.ErrorMessage;
import com.joyintech.tools.util.ValidationUtil.ValidResult;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;

/**
 * 债券多指令
 *
 * @author renping
 * @version 1.0 2019年12月30日
 * @see EtrInvestBondInfo004Controller
 * @since 1.0
 */
@Controller
@PageId("EtrInvestBondInfo004")
@RequestMapping("/invest/entrust/etrinvestbondinfo/EtrInvestBondInfo004")
public class EtrInvestBondInfo004Controller extends BaseController {

    /**
     * 业务编号组件
     */
    @Autowired(required = false)
    private BaseGetBusinessId bussGetBussinessIdService;

    /**
     * 债券委托service
     */
    @Autowired
    private EtrInvestBondInfoService etrInvestBondInfoService;

    /**
     * EtrInvestBondInfoValidator
     */
    @Autowired(required = false)
    private EtrInvestBondInfoValidator etrInvestBondInfoValidator;

    @Autowired
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate", DateUtil.format(sysDate));
        // 导入
        String tempList = getParam("tempList");
        EtrInvestBondInfoDto dto = etrInvestBondInfoService.getEntrustListData(tempList);
        model.addAttribute(GlobalConstant.ENTITY, dto);
        model.addAttribute("etrInvestBondInfoList", StringUtil.isEmptyList(
                dto.getSelectedItem()) ? null : JsonUtils.object2Json(dto.getSelectedItem()));
        model.addAttribute("MY_SUP_TIME_TYPE_02", BussinessDictCodeList.监督时分_委托监控.getValue());
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("shortSellingSupported", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
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
     * 导入excel数据时check
     *
     * @param map dto
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("checkImportData")
    public AjaxResult checkImportData(String fileName) {
        return etrInvestBondInfoService.checkImportData(fileName);
    }

    /**
     * 保存按钮点击时的check
     *
     * @param dtoList dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody List<EtrInvestBondInfoDto> dtoList, BindingResult result) {
        List<ValidMessage> messages = new ArrayList<>();
        StringBuffer confirmMessage = new StringBuffer();
        for (int i = 0; i < dtoList.size(); i++ ) {
            EtrInvestBondInfoDto dto = dtoList.get(i);
            ValidResult itemResult = ValidationUtil.validateBean(dto, ValidateGroupEdit.class);
            if (!StringUtil.isEmptyList(itemResult.getAllErrors())) {
                StringBuffer message = new StringBuffer();
                message.append(getMessage("ETR_E_0025", String.valueOf(i + 1)));
                for (ErrorMessage errorMessage : itemResult.getAllErrors()) {
                    message.append("<br />");
                    message.append(errorMessage.getMessage());
                }
                messages.add(new ValidMessage(message.toString()));
            }
            // 可用现金软校验
            if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH),
                    DictCodeListExt1.是或否_是.getValue())
                    && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                    && dto.getProAvailableCash() != null
                    && dto.getEntrustNum().compareTo(dto.getProAvailableCash()) == 1) {
                if (StringUtil.isNotBlank(confirmMessage)) {
                    confirmMessage.append("<br />");
                }
                confirmMessage.append(getMessage("ETR_E_0025", String.valueOf(i + 1)));
                confirmMessage.append("<br />");
                confirmMessage.append(getMessage("TRD_C_0002"));
            }
            // 可用持仓软校验
            else if (StringUtil.equals(
                    CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
                    DictCodeListExt1.是或否_是.getValue())
                    && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue()))
                    && dto.getAssetAvailablePosition() != null
                    && dto.getEntrustNum().compareTo(dto.getAssetAvailablePosition()) > 0) {
                if (StringUtil.isNotBlank(confirmMessage)) {
                    confirmMessage.append("<br />");
                }
                confirmMessage.append(getMessage("ETR_E_0025", String.valueOf(i + 1)));
                confirmMessage.append("<br />");
                confirmMessage.append(getMessage("ETR_BOND_002"));
            }
        }
        if (messages.size() > 0) {
            AjaxResult errorRs = new AjaxResult();
            errorRs.setErrorList(messages);
            return errorRs;
        }
        if (StringUtil.isNotEmpty(confirmMessage.toString())) {
            AjaxResult wr = new AjaxResult();
            wr.setData(confirmMessage.toString());
            return wr;
        }
        return commonCheck(result, dtoList.get(0).getOperateType());
    }

    /**
     * 保存/提交
     *
     * @param dtoList dto
     * @param br BindingResult
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody EtrInvestBondInfoDto allDto, BindingResult br)
            throws Exception {
        List<EtrInvestBondInfoDto> dtoList = allDto.getSelectedItem();
    	for(EtrInvestBondInfoDto dto : dtoList) {
        	if(dto.getEntrustNum()!=null) {
        		dto.setEntrustNum(dto.getEntrustNum().multiply(new BigDecimal(10000)));//万元转成元
        	}
            if(dto.getMinAmt()!=null) {
            	dto.setMinAmt(dto.getMinAmt().multiply(new BigDecimal(10000)));//万元转成元
            }
            if(dto.getEntrustOwnNum() != null) {
            	dto.setEntrustOwnNum(dto.getEntrustOwnNum().multiply(new BigDecimal(10000)));
            }
            if(dto.getEntrustIntegrateNum() != null) {
            	dto.setEntrustIntegrateNum(dto.getEntrustIntegrateNum().multiply(new BigDecimal(10000)));
            }
            if(StringUtil.equals(dto.getSettleSpreed(),DictCodeList.清算速度_T_0.getValue())){
                dto.setSettleSpreed("0");
            }
            if(StringUtil.equals(dto.getSettleSpreed(),DictCodeList.清算速度_T_1.getValue())){
                dto.setSettleSpreed("1");
            }
        }
        AjaxResult ajaxResult = this.check(dtoList, br);
        if (!StringUtil.isEmptyList(ajaxResult.getErrorList())) {
            return ajaxResult;
        }
        for (EtrInvestBondInfoDto dto : dtoList) {
            if (StringUtil.isEmpty(dto.getEntrustId())) {
                BussinessCodeParameter param = new BussinessCodeParameter();
                param.setCommonBusinessType(DictCodeList.共通业务类型_债券投资委托);
                String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(
                        param) : IDGenerator.generate();
                dto.setEntrustId(code);
            }
//            dto.setWorkflowParameter("new|||1|EtrRepo003|EtrRepo003_btn_submit|true");
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
        ProcessParameter<EtrInvestBondInfoDto> processParameter = new ProcessParameter<EtrInvestBondInfoDto>();
        WorkFlowParameter<EtrInvestBondInfoDto> params = new WorkFlowParameter<EtrInvestBondInfoDto>();
        params.setPageId("EtrInvestBondInfo002");
        params.setElementId(dtoList.get(0).getElementId());
        params.setBusinessCode(EtrInvestBondInfoDto::getEntrustId);
        params.setBusinessType(DictCodeList.共通业务类型_债券投资委托.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
                entity -> this.getMessage("EtrInvestBondInfo001") + "(" + entity.getPortfolioId() + ")"
                        + "-" + this.getMessage("EtrInvestBondInfo001.ps") + "("
                        + SysDataUtil.getNameFromDataDict(DictCode.交易方向, entity.getPs()) + ")" + "-"
                        + "(" + this.getMessage("EtrInvestBondInfo001.vdate") + ":"
                        + DateUtil.format(entity.getVdate()) + ")-("
                        + this.getMessage("EtrInvestBondInfo001.mdate") + ":"
                        + DateUtil.format(entity.getMdate()) + ")-"
                        + this.getMessage("EtrInvestBondInfo001.entrustAmt") + ":"
                        + entity.getEntrustNum());
        processParameter.setWorkFlowParameter(params);
        String msg = etrInvestBondInfoService.start(processParameter,
                dtoList.toArray(new EtrInvestBondInfoDto[dtoList.size()]));
        return new AjaxResult(msg);
    }
}
