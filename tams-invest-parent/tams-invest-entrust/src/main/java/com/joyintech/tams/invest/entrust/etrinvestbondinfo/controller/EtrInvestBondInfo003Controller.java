package com.joyintech.tams.invest.entrust.etrinvestbondinfo.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.pagemenusplit.PageMenuSplitUtil;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.EtrInvestBondInfoDto;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.service.EtrInvestBondInfoService;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.validator.EtrInvestBondInfoValidator;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody; 
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 债券多组合单资产委托新建 
 * 
 * @author wangguiqi
 * @version 1.0 2019年8月14日
 * @see EtrInvestBondInfo003Controller
 * @since 1.0
 */
@Controller
@PageId("EtrInvestBondInfo003")
@RequestMapping("/invest/entrust/etrinvestbondinfo/EtrInvestBondInfo003")
public class EtrInvestBondInfo003Controller extends BaseController {
	
	/**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
    /**
     * 债券投资委托service
     */
    @Autowired
    private EtrInvestBondInfoService etrInvestBondInfoService;
    
    /**
     * 债券投资委托validator
     */
    @Autowired
    private EtrInvestBondInfoValidator etrinvestbondinfovalidator;
    /**
     * 附件处理接口
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    
    @Autowired
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;
    
    /**
     * 初始化002新建页面
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.行内资产二级分类);
        this.setSelectOptionToModel(model, DictCode.行内资产三级分类);
        this.setSelectOptionToModel(model, DictCode.行内资产四级分类);
        this.setSelectOptionToModel(model, DictCode.币种);
        this.setSelectOptionToModel(model, DictCode.交易场所);
        this.setSelectOptionToModel(model, DictCode.交易市场);
        this.setSelectOptionToModel(model, DictCode.交易方向);
        this.setSelectOptionToModel(model, DictCode.清算速度);
        this.setSelectOptionToModel(model, DictCode.投资目的);
        this.setSelectOptionToModel(model, DictCode.交易平台);
        this.setSelectOptionToModel(model, DictCode.价格模式);
        this.setSelectOptionToModel(model, DictCode.业务模式);
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate",DateUtil.format(sysDate));
        model.addAttribute("bankRoom", DictCodeListExt1.交易场所_银行间.getValue());
        model.addAttribute("bankPlat", DictCodeList.交易平台_外汇交易中心.getValue());
        model.addAttribute("shse",DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("szse", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        model.addAttribute("oneMarket", DictCodeListExt1.交易市场_一级市场.getValue());
        model.addAttribute("marketLv2", DictCodeListExt1.交易市场_二级市场.getValue());
        model.addAttribute("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        model.addAttribute("TRADE_PLACE_02", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("TRADE_PLACE_03", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_01", DictCodeList.交易平台_外汇交易中心.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_99", DictCodeList.交易平台_其它.getValue());
        model.addAttribute("SHARES_TRADE_MARKET_01", DictCodeListExt1.交易市场_一级市场.getValue());
        model.addAttribute("SHARES_TRADE_MARKET_02", DictCodeListExt1.交易市场_二级市场.getValue());
        model.addAttribute("T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("T02", DictCodeList.交易方向_卖出.getValue());
        // 一体化对应etliu start
        model.addAttribute("T03",DictCodeList.交易方向_行权_回售.getValue());
        model.addAttribute("T39",DictCodeList.交易方向_行权_赎回.getValue());
        // 一体化对应etliu end
        model.addAttribute("type01", DictCodeList.收益类型_净值型.getValue());
        model.addAttribute("type03", DictCodeList.收益类型_货币型.getValue());
        model.addAttribute("type02", DictCodeList.收益类型_收益型.getValue());
        model.addAttribute("aim01", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
        model.addAttribute("aim02", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        model.addAttribute("N", DictCodeList.是否_否.getValue());
        model.addAttribute("PRICEMODEL_03", DictCodeListExt2.价格模式_市价指令.getValue());
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        String entrustId = getParam("entrustId");
        String finprodId = getParam("finprodId");
        String portfolioId = getParam("portfolioId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            EtrInvestBondInfoDto dto = etrInvestBondInfoService.getBussinessEntityByWorkflowId(
                workflowId);
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",workflowId);
        }
        else if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(entrustId)) {
            EtrInvestBondInfoDto dto = etrInvestBondInfoService.selectByKey(entrustId,finprodId,portfolioId);
            if(dto.getSettleSpeed()!=null){
                if(dto.getSettleSpeed()==0){
                    dto.setSettleSpreed(DictCodeList.清算速度_T_0.getValue());
                }else if(dto.getSettleSpeed()==1){
                    dto.setSettleSpreed(DictCodeList.清算速度_T_1.getValue());
                }
            }
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("tradeId", dto.getEntrustId());
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",entrustId);
        }
        //新建初始化
        else if (WorkFlowUtil.isNotFromToDoList() && !StringUtil.isNotEmpty(entrustId)) {
            EtrInvestBondInfoDto entity = new EtrInvestBondInfoDto();
            entity.setVdate(sysDate);
            entity.setMdate(sysDate);
            entity.setEntrustStatus(DictCodeList.委托状态_新建.getValue());
            model.addAttribute(GlobalConstant.ENTITY, entity);
        }
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("shortSellingSupported", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
        //配置单位 add by yeming 20210721 start
        model.addAttribute("entrustAmtUnit", CacheUtil.getSysParam("entrustAmtUnit"));
        //配置单位 add by yeming 20210721 end
        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数
        // add by etliu 20210501 银行间场内场外拆分 start
        model.addAttribute("parentPageId", this.getParam("parentPageId"));
        PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易场所, this.getParam("parentPageId"));
        PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易平台, this.getParam("parentPageId"));
        // add by etliu 20210501 银行间场内场外拆分 end

        return super.init(model);
    }
    /**
     * 获取交易所资金账户可用现金
     *
     * @param List<EtrInvestBondInfoDto> dtoList
     * @param BindingResult result
     * @return check结果
     * @author wangguiqi
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getSecManageAvailableCash")
    public BigDecimal getSecManageAvailableCash(@RequestBody Map<String, Object> param){
        return etrInvestBondInfoService.getSecManageAvailableCash(param);
    }
    /**
     * 保存按钮点击时的check
     *
     * @param List<EtrInvestBondInfoDto> dtoList
     * @param BindingResult result
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) List<EtrInvestBondInfoDto> dtoList, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        List<ValidMessage> messages = new ArrayList<>();
        boolean chlId = false;
        if(dtoList.size()>0) {
        	messages = etrinvestbondinfovalidator.bussValid(dtoList.get(0));
			if(!StringUtil.isNotEmpty(dtoList.get(0).getVdate()))
				messages.add(new ValidMessage("vdate", "请输入委托开始日。"));
            //2021/3/25 王亚宁  ZGBSS-392 债券多组合指令新建，未选择交易平台时，提示两行
//			if(!StringUtil.isNotEmpty(dtoList.get(0).getTradePlat()))
//				messages.add(new ValidMessage("tradePlat", "请输入交易平台。"));
			if(!StringUtil.isNotEmpty(dtoList.get(0).getMdate()))
				messages.add(new ValidMessage("mdate", "请输入委托终止日。")); 
			if(!StringUtil.isNotEmpty(dtoList.get(0).getCcy()))
				messages.add(new ValidMessage("ccy", "请输入币种。")); 
			if (!StringUtil.isNotEmpty(dtoList.get(0).getTradeMarketMode()))
				messages.add(new ValidMessage("tradeMarketMode", "请输入交易市场。")); 
			if(!StringUtil.isNotEmpty(dtoList.get(0).getSettleSpreed()))
				messages.add(new ValidMessage("settleSpreed", "请输入清算速度。")); 
			if(StringUtil.isNotEmpty(dtoList.get(0).getChlId()))
				chlId = true;
        }
        for(int i = 0; i<dtoList.size(); i++) {
			if(!StringUtil.isNotEmpty(dtoList.get(i).getPortfolioId()))
				messages.add(new ValidMessage("请输入组合代码(第"+(i+1)+"行)。")); 
			if(!StringUtil.isNotEmpty(dtoList.get(i).getEntrustNum()))
				messages.add(new ValidMessage("请输入券面总额(第"+(i+1)+"行)。")); 
			if(!StringUtil.isNotEmpty(dtoList.get(i).getInvAim()))
				messages.add(new ValidMessage("请输入投资目的(第"+(i+1)+"行)。")); 
			if(chlId && StringUtil.isNotEmpty(dtoList.get(i).getSecAcctId()))
				messages.add(new ValidMessage("不能同时输入通道代码和证券账户(第"+(i+1)+"行)。")); 
        }
        
        if(messages.size()>0) { 
        	AjaxResult errorRs = new AjaxResult();
        	errorRs.setErrorList(messages);
        	return errorRs;
        }
        
        if(dtoList.size()>0) {
            BigDecimal prinAmt = dtoList.stream().map(EtrInvestBondInfoDto::getEntrustNum).max((x1, x2) -> x1.compareTo(x2)).get();
            String counterId = dtoList.get(0).getCounterId();
            String 平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
            String tradeMarket = dtoList.get(0).getTradeMarket();
            String ps = dtoList.get(0).getPs();
            String finprodId = dtoList.get(0).getFinprodId();
            // 授权额度检查（当本金金额高于副总裁/总裁助理相应资产的授权额度时，提示消息）
            if (prinAmt != null) {
                CommonBusinessService commonBusinessService = SpringContextUtil.getBean(
                    CommonBusinessService.class);
                Map<String, Object> params = CommonBusinessService.setParams(counterId, prinAmt, LoginUserUtil.getUserName(),
                        平安资产类型, null, tradeMarket, ps,finprodId, null);
                if (commonBusinessService != null && commonBusinessService.是否超过副总额额度_指令用(params)) {
                    boolean isAuthorizationSoftcontrol = Boolean.parseBoolean(CacheUtil.getSysParam("IS_AUTHORIZATION_SOFTCONTROL"));
                    // 交易(委托)授权硬控
                    if (!isAuthorizationSoftcontrol) {
                        ValidMessage validMessage = new ValidMessage(
                            // 修改超过副总裁的授权额度时报出的message 赵东东 2019.8.27 start
                            getMessage("TRD_E_0016","领导"));
                            // 修改超过副总裁的授权额度时报出的message 赵东东 2019.8.27 end
                        messages.add(validMessage);
                        AjaxResult errorRs = new AjaxResult();
                        errorRs.setErrorList(messages);
                        return errorRs;
                    } else {
                        AjaxResult ar = new AjaxResult();
                        ar.setData("交易本金金额超过领导授权额度，是否确认提交?");
                        return ar;
                    }
                }
            }
        }
        
        // 可用现金软校验
        for(EtrInvestBondInfoDto dto: dtoList) {
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                    && dto.getProAvailableCash() != null) {
               if (dto.getEntrustNum().compareTo(dto.getProAvailableCash()) == 1) {
                   AjaxResult ar = new AjaxResult();
                   ar.setData(this.getMessage("TRD_C_0002"));
                   return ar;
               }
            }
        }
        
        return commonCheck(result, dtoList.get(0).getOperateType());
    }
    /**
     * 批量提交
     * 
     * @param List<EtrInvestBondInfoDto> dtoList
     * @param String msg
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody List<EtrInvestBondInfoDto> dtoList, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        List<EtrInvestBondInfoDto> dataList = new ArrayList<>();
        for(EtrInvestBondInfoDto dto: dtoList) {
        	if(!dto.equals(dtoList.get(0))) {
        		for(SysUploadFileInfo file: dto.getFileArray()) {
            		UploadFile uploadFile = fileAttachmentProcess.fetch(file.getSeqNo());
    	            if (uploadFile != null) {
    	                String generateId = IDGenerator.generate();
    	                uploadFile.setFileNo(generateId);
    	                fileAttachmentProcess.upload(uploadFile);
    	                file.setSeqNo(generateId);
    	            }
            	}
        	}
        	if(!StringUtil.isNotEmpty(dto.getEntrustId())){
                BussinessCodeParameter param = new BussinessCodeParameter();
                param.setCommonBusinessType(DictCodeList.共通业务类型_债券投资委托);
                String code = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
                dto.setEntrustId(code);
            }
        	dto.setEntrustOwnNum(dto.getEntrustNum());
            dto.setEntrustIntegrateNum(BigDecimal.ZERO);
            if (StringUtil.isNotEmpty(dto.getEntrustId())) {
                dataList.add(dto);
            }
        	dto.setWorkflowParameter("new|||1|EtrInvestBondInfo003|EtrInvestBondInfo003_btn_submit|true");
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                dto.setSettleSpreed("0");
            }
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                dto.setSettleSpreed("1");
            }
        }
        if (StringUtil.equals(BussinessConstant.SUBMIT, dtoList.get(0).getOperateType())) {
            AjaxResult ar = EntrustRdsCommonAop.doListPreOccupy(
                    dataList, DictCodeList.平安资产类型_债券.getValue(),
                    BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue(),
                    true, dtoList.get(0).getEtrInvestPersonInfo());
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        EntrustRdsCommonAop.riskListToChildren(dtoList.get(0).getRiskTrialRetDto(), dtoList, (dto, list) -> dto.setRiskTrialRetDto(list));
        // ----------add by 预占功能 end----------
        ProcessParameter<EtrInvestBondInfoDto> processParameter = new ProcessParameter<EtrInvestBondInfoDto>();
        WorkFlowParameter<EtrInvestBondInfoDto> params = new WorkFlowParameter<EtrInvestBondInfoDto>();
        params.setPageId("EtrInvestBondInfo002");
        params.setElementId(dtoList.get(0).getElementId());
        params.setBusinessCode(EtrInvestBondInfoDto::getEntrustId);
        params.setBusinessType(DictCodeList.共通业务类型_债券投资委托.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
            entity -> this.getMessage("EtrInvestBondInfo001") + "(" + entity.getPortfolioId() + ")"
                      + "-" + this.getMessage("EtrInvestBondInfo001.ps")+"("+SysDataUtil.getNameFromDataDict(DictCode.交易方向, entity.getPs())+")" + "-" + "("
                      + this.getMessage("EtrInvestBondInfo001.vdate") + ":" + DateUtil.format(entity.getVdate())
                      + ")-(" + this.getMessage("EtrInvestBondInfo001.mdate") + ":"
                      + DateUtil.format(entity.getMdate())+ ")-"
                      + this.getMessage("EtrInvestBondInfo001.entrustAmt") + ":"
                      + entity.getEntrustNum());
        processParameter.setWorkFlowParameter(params);
        String msg = etrInvestBondInfoService.start(processParameter, dtoList.toArray(new EtrInvestBondInfoDto[dtoList.size()]));
        return new AjaxResult(msg);
    }
}
