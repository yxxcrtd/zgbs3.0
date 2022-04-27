/*
 * 文件名：EtrTradePlace001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrexchangetrade.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.tams.common.base.entity.FinProductConvertible;
import com.joyintech.tams.common.base.entity.FinProductQuto;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.*;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.component.business.bond.BusiFinExercisePlanService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
//import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.etrexchangetrade.service.EtrExchangeTrade001Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;

/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 * @author chendijun
 * @version 1.0 2020年7月1日
 * @see EtrExchangeTrade001Controller
 * @since 1.0
 */
@Controller
@PageId("EtrExchangeTrade001")
@RequestMapping("/invest/entrust/etrexchangetrade/EtrExchangeTrade001")
public class EtrExchangeTrade001Controller extends BaseController{

	@Autowired
	 private IPlatformDate iPlatformDate;
	
	/**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;

    
    @Autowired
    private EtrExchangeTrade001Service etrExchangeTrade001Service;
    
    
    @Autowired
    private BusiFinExercisePlanService busiFinExercisePlanService;
    
    
	public static final String ENTRUST_RECORD_NEW = "new";

	public static final String ENTRUST_RECORD_MODIFY = "modify";

    @Value("${quote.flag:false}")
    private boolean useInterfQuoteFlag;

    @Value("${quote.ws.host}")
    private String wsHost;

    @Value("${quote.ws.url}")
    private String wsUrl;
    
    @Override
    public String init(Model model) {
    	this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.投资目的, DictCode.交易场所, DictCode.目标类型, DictCode.申报类型, DictCode.业务模式);
        etrExchangeTrade001Service.setPsSelectOptions(model,this.getSelectDataFromDataDic(DictCode.交易方向, null),false);
        etrExchangeTrade001Service.setPriceModelSelectOptions(model,this.getSelectDataFromDataDic(DictCode.价格模式, null));
        etrExchangeTrade001Service.setDeclaTypeSelectOptions(model,this.getSelectDataFromDataDic(DictCode.申报类型, null));
        // 设置固定的数据字典明细参数
        etrExchangeTrade001Service.setConstSelectOption(model);
        Date platformDate = DateUtil.platDateFromCache();
        model.addAttribute("iPlatformDate", DateUtil.format(platformDate));

        //配置单位 add by yeming 20210728 start
        // moddify by etliu 20220301 bond_traded_unit系统参数删除，默认张 start
//        String bondTradedUnit = CacheUtil.getSysParam("bond_traded_unit");
//        model.addAttribute("bond_traded_unit", bondTradedUnit );
        model.addAttribute("bond_traded_unit", BussinessConstant.BOND_TRADED_UNIT_DEFAULT);
        // moddify by etliu 20220301 end
        //配置单位 add by yeming 20210728 end

        //流程id
        String workflowId = "null".equals(getParam(GlobalConstant.WORKFLOW_ID))?null:getParam(GlobalConstant.WORKFLOW_ID);
        model.addAttribute("workflowId", workflowId);
        
        //委托代码
        String entrustId = "null".equals(getParam("entrustId"))?null:getParam("entrustId");
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        
        //母页面的pageModel
        String parentPageModel = getParam(GlobalConstant.DETAIL_PAGE_MODEL_KEY);
        model.addAttribute("parentPageModel", parentPageModel);
        //是否_是
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        
        String isFromToDoList = getParam("isFromToDoList");
        EtrExchangeTrade001Dto entity = new EtrExchangeTrade001Dto();
        if (StringUtil.isNotEmpty(workflowId)) {
        	 //流程代办checkImportData
            entity = etrExchangeTrade001Service.getBussinessEntityByWorkflowId(
                workflowId);
            //add by chenjiaxing 流程中不显示资产名称 start
                        //如果是多组合的流程数据则，单组合置空
            if(StringUtil.isNotEmpty(entity) && StringUtil.equals(entity.getIsMulti(),DictCodeList.是否_是.getValue())) {
                entity = new EtrExchangeTrade001Dto();
            }
            else {
                //add by chenjiaxing 流程中不显示资产名称 start
                String[] secIds = StringUtil.split(entity.getFinprodId(),"_");
                if(secIds.length == 2 && StringUtil.isEmpty(entity.getStockId())){
                    entity.setStockId(secIds[1]);
                }
            }
            //add by chenjiaxing 流程中不显示资产名称 end
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("workflowId", workflowId);
        }
        else if (StringUtil.isNotEmpty(entrustId) && !"true".equals(isFromToDoList)) {
        	//查看
        	entity = etrExchangeTrade001Service.getEtrExchangeTradeInfo(entrustId);
        }
        else{
            //新建页面初始化
            entity.setEntrustDate(platformDate);
            entity.setEntrustStatus(BussinessDictCodeList.委托状态_新建.getValue());
        }
        
        model.addAttribute("ENTRUST_RECORD_NEW", ENTRUST_RECORD_NEW);
        model.addAttribute("ENTRUST_RECORD_MODIFY", ENTRUST_RECORD_MODIFY);
        // 根据价格模式确定当前的数据来源
        if(entity != null && StringUtil.equals(entity.getPriceModel(), DictCodeListExt3.申报类型_点击成交申报.getValue())) {
            entity.setDataSource("01");
        }
        model.addAttribute(GlobalConstant.ENTITY, entity);
		if (null != entity && null != entity.getEntrustPrice()) {
			model.addAttribute("BOND_SCALE", entity.getEntrustPrice().scale());
		} else {
			model.addAttribute("BOND_SCALE", 3);
		}
		if(null != entity && null != entity.getCalculateField()) {
			 model.addAttribute("calculateField", entity.getCalculateField());
		}
        //最小价差List
        List<MinPirceDto> minPriceWhole = getInterfSysMinPriceAll(new HashMap<>());
        model.addAttribute("minPriceWhole", minPriceWhole);
        model.addAttribute("CONTROL_TYPE_01", BussinessDictCodeListRds.控制类型_大于.getValue());
        model.addAttribute("CONTROL_TYPE_02", BussinessDictCodeListRds.控制类型_大于等于.getValue());
        model.addAttribute("CONTROL_TYPE_03", BussinessDictCodeListRds.控制类型_小于.getValue());
        model.addAttribute("CONTROL_TYPE_04", BussinessDictCodeListRds.控制类型_小于等于.getValue());
        
        //清算速度
        etrExchangeTrade001Service.setSettleSpreedSelectOptions(model,getSelectDataFromDataDic(DictCode.清算速度, null));
        model.addAttribute("ACCOUNT_SPEED_01", DictCodeList.清算速度_T_0.getValue());
        model.addAttribute("ACCOUNT_SPEED_02", DictCodeList.清算速度_T_1.getValue());
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        
        // 场内交易指令画面行情区域自动刷新时间（毫秒）
        long etrExchange_HqArea_refresh_period = 20000;
        String tempSysParaVal = CacheUtil.getSysParam(
            BussinessConstant.ETREXCHANGE_HQAREA_REFRESH_PERIOD);
        if (StringUtil.isNotBlank(tempSysParaVal)
            && !StringUtil.equals(BussinessConstant.ETREXCHANGE_HQAREA_REFRESH_PERIOD,
                tempSysParaVal)) {
            try {
                etrExchange_HqArea_refresh_period = Long.parseLong(tempSysParaVal);
            }
            catch (Exception e) {
            }
        }
        model.addAttribute("etrExchange_HqArea_refresh_period", etrExchange_HqArea_refresh_period);

        model.addAttribute("useInterfQuoteFlag", useInterfQuoteFlag);
        model.addAttribute("wsUri", wsHost + wsUrl);
        
        return super.init(model);
    }
    

    
    /**
     * 查询交易行情信息
     * <一句话功能简述>
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findTradeQuotationsPage")
    public Page<EtrExchangeTradeQuotation001Dto> findTradeQuotationsPage(){
        return this.etrExchangeTrade001Service.findTradeQuotationsPage(RequestUtil.getPageParam());
    }
    
    
    /**
     * 查询盘后行情信息
     * <一句话功能简述>
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findAfterHoursPage")
    public Page<EtrExchangeTradeQuotation001Dto> findAfterHoursPage(){
        return this.etrExchangeTrade001Service.findAfterHoursPage(RequestUtil.getPageParam());
    }

    /**
     * 查询固定收益行情数据
     * <一句话功能简述>
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findFixedIncomePage")
    public Page<EtrExchangeTradeQuotation001Dto> findFixedIncomePage(){
        return this.etrExchangeTrade001Service.findFixedIncomePage(RequestUtil.getPageParam());
    }
    
    
    /**
     * 查询盘后行情信息
     * <一句话功能简述>
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findRepoPage")
    public Page<EtrExchangeTradeQuotation001Dto> findRepoPage(){
        Page<EtrExchangeTradeQuotation001Dto> page = new Page<EtrExchangeTradeQuotation001Dto>();
        return page;
    }
    
    
    
    /**
     * 保存按钮点击时的check
     *
     * @param dto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
	@ResponseBody
	@RequestMapping("check")
	public AjaxResult check(@RequestBody @Validated({ ValidateGroupEdit.class }) EtrExchangeTrade001Dto dto,
			BindingResult result) {
		if (result.hasErrors()) {
			return new AjaxResult(result);
		}

        etrExchangeTrade001Service.setEntrustBussType(dto);

		AjaxResult ajax  = etrExchangeTrade001Service.checkSave(dto);
		if (ajax != null && !StringUtil.isEmptyList(ajax.getErrorList()) && !StringUtil.equals(dto.getOperateType(),"save")) {
			return ajax;
		}
		
		String confirmMsg = "";
		if (StringUtils.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
			Map<String, Object> pageParam = new HashMap<String, Object>();
			Date sysDate = DateUtil.platDateFromCache();
			pageParam.put("date", DateUtil.format(sysDate));
			pageParam.put("secId", dto.getStockId());
			String price = etrExchangeTrade001Service.qryMstBondMarketPrice(pageParam);
			if (StringUtils.isNotEmpty(price)) {
				BigDecimal bigPrice = new BigDecimal(price);
				if (dto.getEntrustPrice().compareTo(bigPrice.multiply(new BigDecimal(1.02))) > 0) {
					confirmMsg = "当前价格超过上一日平均价的2%，是否继续？";
				}
			}
		}
        if (DictCodeList.交易方向_申购.getValue().equals(dto.getPs()) && DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(dto.getFinprodType2())) {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("finprodId", dto.getFinprodId());//金融产品代码
            List<String> othTypeList = new ArrayList<String>();
            othTypeList.add(DictCodeListExt3.金融产品其他代码类型_新股申购代码.getValue());
            paramMap.put("othTypeList", othTypeList);
            Map<String, Object> paramMap1 = new HashMap<String, Object>();
            paramMap1.put("finprodId", dto.getFinprodId());//金融产品代码
            paramMap1.put("secAcctId",dto.getSecAcctId());
            paramMap1.put("cDate",DateUtil.getSysDate());
            paramMap1.put("portfolioId",dto.getPortfolioId());
            String othType = DictCodeListExt3.金融产品其他代码类型_新股申购代码.getValue();              //业务类型
            List<FinProductOthCodeDto> codeDtos = etrExchangeTrade001Service.getFinprodOthCodeInfo(paramMap);
            if (codeDtos.size() > 0) {
                othType = codeDtos.get(0).getOthType();
                paramMap1.put("othType", othType);
            }
            List<FinProductQuto> qutos = etrExchangeTrade001Service.getFinProductQutoInfo(paramMap1);
            if (qutos.size() == 0) {
                confirmMsg = "当前资产无申购额度，是否继续？";
            }
        }

        //配股配债权益数量软check
        if (DictCodeListExt2.交易方向_配股.getValue().equals(dto.getPs())||DictCodeListExt3.交易方向_配债.getValue().equals(dto.getPs())) {
            Map<String, Object> param = new HashMap<String, Object>();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("finprodId", dto.getFinprodId());//金融产品代码
            List<String> othTypeList = new ArrayList<String>();
            String othType = "";
            //业务类型
            if(DictCodeListExt2.交易方向_配股.getValue().equals(dto.getPs())){
                othTypeList.add(DictCodeListExt3.金融产品其他代码类型_配股代码.getValue());
                othType=DictCodeListExt3.金融产品其他代码类型_配股代码.getValue();

            }
            if(DictCodeListExt3.交易方向_配债.getValue().equals(dto.getPs())){
                othTypeList.add(DictCodeListExt3.金融产品其他代码类型_配债代码.getValue());
            }
            paramMap.put("othTypeList", othTypeList);
            Map<String, Object> paramMap1 = new HashMap<String, Object>();
            paramMap1.put("finprodId", dto.getFinprodId());//金融产品代码
            paramMap1.put("secAcctId",dto.getSecAcctId());
            paramMap1.put("cDate",DateUtil.getSysDate());
//            paramMap1.put("portfolioId",dto.getPortfolioId());
            List<FinProductOthCodeDto> codeDtos = etrExchangeTrade001Service.getFinprodOthCodeInfo(paramMap);
            if (codeDtos.size() > 0) {
                othType = codeDtos.get(0).getOthType();
                paramMap1.put("othType", othType);
            }
            List<FinProductQuto> qutos = etrExchangeTrade001Service.getFinProductQutoInfo(paramMap1);
            if (qutos.size() == 0) {
                confirmMsg = "当前资产无配股/配债额度，是否继续？";
            }

        }

        // etr_check_cash_switch 01 提示 02 禁止
        if (CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH).equals(DictCodeListExt1.是或否_是.getValue())
                && (DictCodeList.交易方向_买入.getValue().equals(dto.getPs()) || DictCodeListExt3.交易方向_配债.getValue().equals(dto.getPs())
                    || DictCodeListExt2.交易方向_配股.getValue().equals(dto.getPs()) || DictCodeList.交易方向_申购.getValue().equals(dto.getPs())
                    || DictCodeListExt3.交易方向_逆回购.getValue().equals(dto.getPs()))) {
            Map<String,Object> param = new HashMap<String,Object>();
            param.put("portfolioId",dto.getPortfolioId());
            param.put("ccy",DictCodeList.币种_人民币_CNY.getValue());
            dto.setAccAvailableCash(etrExchangeTrade001Service.selectAccAvailableCash(param));
            if (StringUtil.isNotEmpty(dto.getAccAvailableCash()) && StringUtil.isNotEmpty(dto.getEntrustAmt())) {
                if (dto.getAccAvailableCash().compareTo(dto.getEntrustAmt()) == -1) {
                    confirmMsg = "组合可用现金不足，是否继续？";
                }
            }
        }

        // etr_check_pos_switch 01 提示 02 禁止
        if (CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH).equals(DictCodeListExt1.是或否_是.getValue()) && (
                DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
                        || DictCodeList.交易方向_行权_回售.getValue().equals(dto.getPs())
                        || DictCodeListExt3.交易方向_债转股.getValue().equals(dto.getPs())
                        || DictCodeListExt3.交易方向_质押券入库.getValue().equals(dto.getPs()))) {
            Map<String,Object> param = new HashMap<String,Object>();
            param.put("portfolioId",dto.getPortfolioId());
            param.put("finprodId",dto.getFinprodId());
            param.put("ccy",DictCodeList.币种_人民币_CNY.getValue());
            param.put("secAcctId",dto.getSecAcctId());
            dto.setAccAvailablePos(etrExchangeTrade001Service.selectAvailableNum(param));
            if (StringUtil.isNotEmpty(dto.getAccAvailablePos()) && StringUtil.isNotEmpty(dto.getEntrustNum())) {
                if (dto.getAccAvailablePos().compareTo(dto.getEntrustNum()) == -1) {
                    confirmMsg = "可用持仓不足，是否继续？";
                }
            }
        }

        //正回购、质押券出库
        // 委托指令组合的可用持仓不足时  01 提醒 02 禁止
        if (CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH).equals(DictCodeListExt1.是或否_是.getValue()) &&
                (DictCodeListExt3.交易方向_正回购.getValue().equals(dto.getPs()) || DictCodeListExt3.交易方向_质押券出库.getValue().equals(dto.getPs()))) {
            Map<String,Object> param = new HashMap<String,Object>();
            param.put("portfolioId",dto.getPortfolioId());
            param.put("finprodId","XN_PRODID999");
            if(null != dto.getSecAcctId()){
                param.put("secAcctId", dto.getSecAcctId());
            }
            param.put("ccy",DictCodeList.币种_人民币_CNY.getValue());
            dto.setAccAvailablePos(etrExchangeTrade001Service.selectAvailableNum(param));
            if (StringUtil.isNotEmpty(dto.getAccAvailablePos()) && StringUtil.isNotEmpty(dto.getEntrustNum())) {
                if (dto.getAccAvailablePos().compareTo(dto.getEntrustNum()) == -1) {
                    confirmMsg = "标准券可用持仓不足，是否继续？";
                }
            }
        }

        if (StringUtil.isNotEmpty(confirmMsg)) {
			AjaxResult ajaxResult = new AjaxResult();
			ajaxResult.setData(confirmMsg);
			return ajaxResult;
		}
		
		return commonCheck(result, dto.getOperateType());
	}
	
    /**
	 * 保存/提交
	 * 
	 * @param dto
	 *            dto
	 * @param br
	 *            br
	 * @return 结果
	 * @throws Exception
	 *             异常
	 */
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping("save")
	public AjaxResult save(@RequestBody EtrExchangeTrade001Dto dto, BindingResult br) throws Exception {
		if (br.hasErrors()) {
			return new AjaxResult(br);
		}
		//业务类型
		String businessType = etrExchangeTrade001Service.setEntrustBussType(dto);
		//add end
		if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
            BussinessCodeParameter param = etrExchangeTrade001Service.genBussinessCodeParameter(dto);
            String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
            dto.setEntrustId(code);
		}
		
		
        // ----------add by zhangrui1 20200703 预占功能 start----------
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            String finprodType2 = "";
            if(dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                finprodType2 =  DictCodeList.平安资产类型_债券.getValue();
            }
            else if(dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
                finprodType2 =  DictCodeList.平安资产类型_基金.getValue();
            }
            else if(dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                finprodType2 =  DictCodeListExt2.平安资产类型_股票.getValue();
            }
            else if((dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue()) || dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue())) ) {
                finprodType2 =  DictCodeList.平安资产类型_质押式回购.getValue();
            }
            //债转股需要走批量接口
            String infCode;
            if(StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_债转股.getValue())) {
                infCode = BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
            } 
            else {
                infCode = BussinessDictCodeListRds.接口系统ID_预占接口.getValue(); 
            }
            AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dto,
                infCode, finprodType2);
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        // ----------add by zhangrui1 20200703 预占功能 end----------
		
		
		/*
		 * if 画面.金融产品类型（投管）.股票 and 画面.交易平台 = 数据字典.交易平台.竞价交易平台 指令业务类型 =
		 * 数据字典.共通业务类型.股票竞价指令
		 * 
		 * if 画面.金融产品类型（投管）.基金 and 画面.交易平台 = 数据字典.交易平台.竞价交易平台 指令业务类型 =
		 * 数据字典.共通业务类型.基金竞价指令
		 * 
		 * if 画面.委托方向 = （数据字典.交易方向.正回购 or 逆回购） and 画面.交易平台 = 数据字典.交易平台.大宗交易平台
		 * 指令业务类型 = 数据字典.共通业务类型.大宗债券质押式回购指令
		 * 
		 * if 画面.金融产品类型（投管）.股票 and 画面.交易平台 = 数据字典.交易平台.大宗交易平台 指令业务类型 =
		 * 数据字典.共通业务类型.大宗股票指令
		 * 
		 * if 画面.金融产品类型（投管）.基金 and 画面.交易平台 = 数据字典.交易平台.大宗交易平台 指令业务类型 =
		 * 数据字典.共通业务类型.大宗基金指令
		 * 
		 * if 画面.金融产品类型（投管）.债券and 画面.交易平台 = 数据字典.交易平台.大宗交易平台 指令业务类型 =
		 * 数据字典.共通业务类型.大宗债券指令
		 */
		dto.setBusinessTypeName(SysDataUtil.getNameFromDataDict(DictCode.共通业务类型, businessType));
		dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
	    dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
	    dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
	    dto.setCreateTime(new Date());// 创建时间
		ProcessParameter<EtrExchangeTrade001Dto> processParameter = new ProcessParameter<EtrExchangeTrade001Dto>();
		WorkFlowParameter<EtrExchangeTrade001Dto> params = etrExchangeTrade001Service.getWorkFlowParameter(dto);
		params.setPageId("EtrExchangeTradeMain");
		params.setElementId(dto.getElementId());
		params.setBusinessType(businessType);
		params.setBusinessCode(dto.getEntrustId());
		params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")"
				+ "-" + "业务类型"+ "(" + entity.getBusinessTypeName() + ")-"
				+ "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
		processParameter.setWorkFlowParameter(params);
		String msg = etrExchangeTrade001Service.start(processParameter, dto);
		return new AjaxResult(msg);
	}
    
    @ResponseBody
    @RequestMapping("/selectCounterIdByCounterType")
    public List<SelectOptionDto> selectCounterIdByCounterType(){
    	return etrExchangeTrade001Service.selectCounterIdByCounterType(RequestUtil.getPageParam());
    }
    
    @ResponseBody
    @RequestMapping("/getSecAcctInfo")
    public List<SelectOptionDto> getSecAcctInfo(){
    	return etrExchangeTrade001Service.getSecAcctInfo(RequestUtil.getPageParam());
    }
    
    @ResponseBody
    @RequestMapping("/getFinprodId")
    public List<SelectOptionDto> getFinprodId(){
    	return etrExchangeTrade001Service.getFinprodId(RequestUtil.getPageParam());
    }
    
    @ResponseBody
    @RequestMapping("/getFinprodMarketInfo")
    public Page<SelectOptionDto> getFinprodMarketInfo(){
    	return etrExchangeTrade001Service.getFinprodMarketInfo(RequestUtil.getPageParam());
    }

    @ResponseBody
    @RequestMapping("/getFinprodMarketInfoPosition")
    public Page<SelectOptionDto> getFinprodMarketInfoPosition(){
        return etrExchangeTrade001Service.getFinprodMarketInfoPosition(RequestUtil.getPageParam());
    }


    @ResponseBody
    @RequestMapping("/getPtlSecAcctInfo")
    public List<SelectOptionDto> getPtlSecAcctInfo(){
    	return etrExchangeTrade001Service.getPtlSecAcctInfo(RequestUtil.getPageParam());
    }

    /**
     * 对手方交易商联想文本框初始化。
     * 
     * @return 联想文本框
     */
    @ResponseBody
    @RequestMapping("/selectCounterDealer")
    public List<SuggestDto> selectCounterDealer() {
        Map<String, Object> pageParam = RequestUtil.getPageParam();
        return etrExchangeTrade001Service.selectCounterDealer(pageParam);
    }

    /**
     * 对手方交易员联想文本框初始化。
     * 
     * @return 联想文本框
     */
    @ResponseBody
    @RequestMapping("/selectCounterTrader")
    public List<SuggestDto> selectCounterTrader() {
        Map<String, Object> pageParam = RequestUtil.getPageParam();
        return etrExchangeTrade001Service.selectCounterTrader(pageParam);
    }

    /**
     * 本方交易员联想文本框初始化。
     * 
     * @return 联想文本框
     */
    @ResponseBody
    @RequestMapping("/selectTrader")
    public List<SuggestDto> selectTrader() {
        Map<String, Object> pageParam = RequestUtil.getPageParam();
        return etrExchangeTrade001Service.selectTrader(pageParam);
    }

    /**
     * 查询资产对应组合的可用持仓信息
     * 
     * @param param
     * @return 可用持仓
     * @author dubaojun
     */
    @ResponseBody
    @RequestMapping("/selectAvailableNum")
    public String selectAvailableNum(@RequestBody Map<String, Object> param){
        BigDecimal availableNum = etrExchangeTrade001Service.selectAvailableNum(param);
        if(availableNum == null) {
            return "0.00";
        }
        return availableNum.setScale(2,RoundingMode.HALF_UP).toString();
    }

    /**
     * 查询组合对应账户的可用现金信息
     * 
     * @param param
     * @return 可用现金
     * @author dubaojun
     */
    @ResponseBody
    @RequestMapping("/selectAccAvailableCash")
    public String selectAccAvailableCash(@RequestBody Map<String, Object> param){
        BigDecimal accAvailableCash = etrExchangeTrade001Service.selectAccAvailableCash(param);
        if(accAvailableCash == null) {
            return "0.00";
        }
        return accAvailableCash.setScale(2).toString();
    }

    /**
     * 查询债券对应的最新面值
     * 
     * @param param
     * @return 最新面值
     * @author dubaojun
     */
    @ResponseBody
    @RequestMapping("/selectSecFaceValue")
    public BigDecimal selectSecFaceValue(@RequestBody Map<String, Object> param){
        BigDecimal faceValue = etrExchangeTrade001Service.selectSecFaceValue(param);
        if(faceValue == null) {
            return BigDecimal.ZERO;
        }
        return faceValue;
    }

    /**
     * 根据画面的信息计算价格和到期收益率。<br/>
     * 如果以净价为基准，则计算全价和到期收益率<br/>
     * 如果以全价为基准，则计算净价和到期收益率<br/>
     * 如果以到期收益率为基准，则计算净价和全价
     * 
     * @param param
     * @return 计算结果
     * @author dubaojun
     */
    @ResponseBody
    @RequestMapping("/calcPriceAndYield")
    public Map<String, BigDecimal> calcPriceAndYield(@RequestBody Map<String, Object> param){
        return etrExchangeTrade001Service.calcPriceAndYield(param);
    }

    /**
     * 根据画面的信息计算委托金额和委托数量。<br/>
     * 如果目标类型为“数量”，则根据数量计算金额<br/>
     * 如果目标类型为“金额”，则根据数量计算数量<br/>
     * 
     * @param param
     * @return 计算结果
     * @author dubaojun
     */
    @ResponseBody
    @RequestMapping("/calcEntrustNumAndAmt")
    public Map<String, BigDecimal> calcEntrustNumAndAmt(@RequestBody Map<String, Object> param){
        return etrExchangeTrade001Service.calcEntrustNumAndAmt(param);
    }

    /**
     * 获取最小交易单元
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("/getMinNumStep")
    public Map<String, BigDecimal> getMinNumStep(@RequestBody Map<String, Object> param){
        return etrExchangeTrade001Service.getMinNumStep(param);
    }

	/**
	 * 获取1/最小价差的倍数
	 * @param param
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getInterfSysMinPriceAll")
	public List<MinPirceDto> getInterfSysMinPriceAll(@RequestBody Map<String, Object> param) {
		List<MinPirceDto> minPirceList = etrExchangeTrade001Service.getInterfSysMinPriceAll(param);
		return minPirceList;
	}

    /***
     * 取更新时间最新记录.投资目的  指令业务类型 = 数据字典.委托类型.大宗债券指令
     * @return
     */
    @ResponseBody
    @RequestMapping("/getInvAim")
    public String getInvAim() {
        return etrExchangeTrade001Service.getInvAim();
    }


    /***
     * 取更新时间最新记录.投资目的  指令业务类型 = 数据字典.委托类型.大宗债券指令
     * @return
     */
    @ResponseBody
    @RequestMapping("/linkInvAim")
    public AjaxResult linkInvAim(@RequestBody Map<String, Object> param) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setData(etrExchangeTrade001Service.linkInvAim(param));
        return ajaxResult;
    }
    
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/selectBondExecPlan")
	public int selectBondExecPlan() throws Exception {
		String param = super.getRequest().getParameter("param");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("param", param);
		return etrExchangeTrade001Service.selectBondExecPlan(map);
	}
	
	/**
	 * 导入
	 *
	 * @param fileName
	 *            导入的文件名
	 * @return 处理结果
	 * @throws Exception
	 *             异常
	 */
	@ResponseBody
	@RequestMapping("checkImportData")
	public AjaxResult checkImportData(@RequestBody Map<String,Object> paramMap) throws Exception {
		return etrExchangeTrade001Service.checkImportData(paramMap);
	}
	
	/**
	 * 折算率如果取不到值，默认100%
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/selectStdsecConverseRatio")
	public BigDecimal selectStdsecConverseRatio(@RequestBody Map<String, Object> param) throws Exception {
		String converseRatio = etrExchangeTrade001Service.selectStdsecConverseRatio(param);
		if (StringUtils.isNotEmpty(converseRatio)) {
		    // 画面上乘以100后展示
			return ArithmeticUtil.multiply(new BigDecimal(converseRatio), BigDecimal.valueOf(100));
		}
		return null;
	}

	@RequestMapping(value = "/selectExercisePriceById")
	@ResponseBody
	public BigDecimal selectExercisePriceById(@RequestBody Map<String, Object> param) throws Exception {
		// 查询行权计划
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("finprodId", param.get("finprodId"));
		map.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
		map.put("cashType2", DictCodeListExt1.现金流二级类型_本金.getValue());
		map.put("execType01", DictCodeList.行权状态_未行权.getValue());
		map.put("execType02", DictCodeList.行权状态_已行权.getValue());
		List<FinExercisePlan> lst = busiFinExercisePlanService.getfinExercisePlanByProductId(map);
		if (null != lst && lst.size() > 0) {
			String vdate = param.get("vdate").toString();
			if (StringUtils.isNotEmpty(vdate)) {
				for (FinExercisePlan finExercisePlan : lst) {
					if (StringUtil.isNotEmpty(finExercisePlan.getVdate()) && StringUtil.isNotEmpty(finExercisePlan.getMdate())
					    && finExercisePlan.getVdate().getTime() -  DateUtil.parse(vdate).getTime() <= 0
							&& finExercisePlan.getMdate().getTime() - DateUtil.parse(vdate).getTime() >= 0) {
						return finExercisePlan.getExercisePrice();
					}
				}
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/selectConverseSharePrice")
	@ResponseBody
	public BigDecimal selectConverseSharePrice(@RequestBody Map<String, Object> param) throws Exception {
		return etrExchangeTrade001Service.selectConverseSharePrice(param);
	}
	
	@RequestMapping(value = "/selectTrdOthPrice")
	@ResponseBody
	public BigDecimal selectOthPrice(@RequestBody Map<String, Object> param) throws Exception {
		TrdOthCodeInfoDto dto = etrExchangeTrade001Service.getTrdOthCodeInfo(param);
		if (null != dto && dto.getDeclaredPrice() != null && dto.getDeclaredPrice().compareTo(BigDecimal.ZERO) > 0) {
			return dto.getDeclaredPrice();
		}
		return null;
	}
	
	@RequestMapping(value = "/selectExtPs")
	@ResponseBody
	public Map<String, Object> selectExtPs(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Date sysDate = DateUtil.platDateFromCache();
		param.put("date", DateUtil.format(sysDate));
		List<TrdOthCodeInfoDto> list = etrExchangeTrade001Service.getTrdOthCodeInfos(param);
		if (null != list && list.size() > 0) {
			for (TrdOthCodeInfoDto dto : list) {
				if (!StringUtils.equals(dto.getOthCodeType(), DictCodeListExt3.金融产品其他代码类型_配股代码.getValue())
						&& !StringUtils.equals(dto.getOthCodeType(), DictCodeListExt3.金融产品其他代码类型_配债代码.getValue())) {
					// 只能选择申购，其他委托方向不可选
					String ps = OthTypeMapToPs(dto.getOthCodeType());
					if (StringUtils.equals(ps, DictCodeList.交易方向_申购.getValue())) {
						map.clear();
						map.put(ps, dto.getDeclaredPrice());
						return map;
					} else if (StringUtils.equals(ps, DictCodeListExt3.交易方向_质押券入库.getValue())) {
						BigDecimal converseRatio = selectStdsecConverseRatio(param);
						if (null != converseRatio) {
							map.put(DictCodeListExt3.交易方向_质押券入库.getValue(), converseRatio);
							map.put(DictCodeListExt3.交易方向_质押券出库.getValue(), converseRatio);
						}
					} else {
						map.put(ps, dto.getDeclaredPrice());
					}
				}
			}
		}

		// add by etliu 20211207 start
        // 交易所债券非交易类业务迁移
        if (!map.containsKey(DictCodeListExt3.交易方向_质押券入库.getValue())) {
            BigDecimal converseRatio = selectStdsecConverseRatio(param);
            if (null != converseRatio) {
                map.put(DictCodeListExt3.交易方向_质押券入库.getValue(), converseRatio);
                map.put(DictCodeListExt3.交易方向_质押券出库.getValue(), converseRatio);
            }
        }
        String finprodId = (String) param.get("finprodId");
        if (!map.containsKey(DictCodeList.交易方向_行权_回售.getValue())) {
            List<FinExercisePlan> exercisePlanList = etrExchangeTrade001Service.selectFinExercisePlanData(finprodId, sysDate);
            if (!StringUtil.isEmptyList(exercisePlanList)) {
                map.put(DictCodeList.交易方向_行权_回售.getValue(), exercisePlanList.get(0).getExercisePrice());
            }
        }
        if (!map.containsKey(DictCodeListExt1.交易方向_债转股.getValue())) {
            FinProductConvertible finProductConvertible = etrExchangeTrade001Service.selectConverseShareData(finprodId, sysDate);
            if (finProductConvertible != null) {
                map.put(DictCodeListExt1.交易方向_债转股.getValue(), finProductConvertible.getConPrice());
            }
        }
        // add by etliu 20211207 end

		return map;
	}
	
	/**
	 * 申报类型-》交易方向的映射
	 * @param othCodeType
	 * @return
	 */
	private String OthTypeMapToPs(String othCodeType) {
		if (StringUtils.equals(othCodeType, DictCodeListExt3.金融产品其他代码类型_可转债信用申购代码.getValue())) {
			return DictCodeList.交易方向_申购.getValue();
		}else if(StringUtils.equals(othCodeType, DictCodeListExt3.金融产品其他代码类型_回售代码.getValue())){
			return DictCodeList.交易方向_行权_回售.getValue();
		}else if(StringUtils.equals(othCodeType, DictCodeListExt3.金融产品其他代码类型_可转债转股代码.getValue())){
			return DictCodeListExt1.交易方向_债转股.getValue();
		}else if(StringUtils.equals(othCodeType, DictCodeListExt3.金融产品其他代码类型_质押代码.getValue())){
			return DictCodeListExt3.交易方向_质押券入库.getValue();
		}
		return null;
	}

	//add by chenjiaxing 债券竞价交易计算偏离度 20210907 start
    /**
     * 1.6计算偏离度
     *
     * @param params 画面参数
     * @return 偏离度
     * @see
     * @since 1.0
     */
    @RequestMapping("calcDeviationDegree")
    @ResponseBody
    public String calcDeviationDegree(@RequestParam Map<String, Object> params) {
        return etrExchangeTrade001Service.calcDeviationDegree(params);
    }
    //add by chenjiaxing 债券竞价交易计算偏离度 20210907 end

    /***
     * 初始化行情
     * @return
     */
    @RequestMapping(value = "/initHq")
    public String initHq(Model model){

        model.addAttribute("useInterfQuoteFlag", useInterfQuoteFlag);
        model.addAttribute("wsUri", wsHost + wsUrl);
        return "invest/entrust/etrexchangetrade/EtrExchangeTradeHq";
    }

    /**
     * 判断所选产是不是债券并且有没有通过中债sppi测试
     * @return
     */
    @ResponseBody
    @RequestMapping("/IsBondAsset")
    public AjaxResult IsBondAsset(){
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setData(etrExchangeTrade001Service.IsBondAsset(RequestUtil.getPageParam()));
//        ajaxResult.setData("2");
         return ajaxResult;
    }

    // add by etliu 20211013 start
    // TISHKS-5 bug修复：在场内下指令时，资产选了万科之后，出现申购选项，但理论上不应该出现(万科很早就上市了)，我在申报信息维护中也没有看到万科的新股申报
    /**
     * 当前选中股票是否存在新股申购期
     *
     * @param params 画面参数
     * @return 当前选中股票是否处在新股申购期
     * @see
     * @since 1.0
     */
    @RequestMapping("isXinGuShenGou")
    @ResponseBody
    public String isXinGuShenGou(@RequestParam Map<String, Object> params) {
        return etrExchangeTrade001Service.isXinGuShenGou(params);
    }
    // add by etliu 20211013 end
}
	
