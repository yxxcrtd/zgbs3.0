package com.joyintech.tams.invest.riskredis.quota.superviseindicator.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;

import com.joyintech.tams.invest.riskredis.quota.superviseindicator.dto.RskPortfolioIndicatorDto;
import com.joyintech.tams.invest.riskredis.quota.superviseindicator.dto.RskSupervisionIndicator002Dto;
import com.joyintech.tams.invest.riskredis.quota.superviseindicator.dto.RskSupervisionIndicatorDto;
import com.joyintech.tams.invest.riskredis.quota.superviseindicator.service.RskSupervisionIndicator001Service;
import com.joyintech.tams.invest.riskredis.quota.superviseindicator.service.RskSupervisionIndicator002Service;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.JoyinMessage;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.DataDict;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;

import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;


/**
 * 监控指标
 * @author 裴家俊
 * @version 1.0 2018年10月23日
 * @see RskSupervisionIndicator002Controller
 * @since 1.0
 */
@Controller
@PageId("RskSupervisionIndicator002")
@RequestMapping("/invest/riskredis/quota/superviseindicator/RskSupervisionIndicator002")
public class RskSupervisionIndicator002Controller extends BaseController {
	
	/**
     * RskSupervisionIndicator001Service
     */
    @Autowired(required = false)
    private RskSupervisionIndicator001Service rskSupervisionIndicator001Service;
    /**
     * RskSupervisionIndicator002Service
     */
    @Autowired(required = false)
    private RskSupervisionIndicator002Service rskSupervisionIndicator002Service;
    /**
     * 初始化的查询条件
     */
    private static final IDictCode[] DICT_CODES = new BussinessDictCodeRds[] {BussinessDictCodeRds.指标类型,
        BussinessDictCodeRds.监督类型, BussinessDictCodeRds.运算符, BussinessDictCodeRds.连接符,BussinessDictCodeRds.操作符,BussinessDictCodeRds.监控方向,
        BussinessDictCodeRds.单位, BussinessDictCodeRds.监督时分,  BussinessDictCodeRds.组合层次,BussinessDictCodeRds.监督频率,
        BussinessDictCodeRds.分类维度,BussinessDictCodeRds.申万行业一级类型,BussinessDictCodeRds.收益类型,BussinessDictCodeRds.开放类型,BussinessDictCodeRds.募集方式,
        BussinessDictCodeRds.投资性质,BussinessDictCodeRds.监控方向,BussinessDictCodeRds.触警操作,BussinessDictCodeRds.除0通过条件
       };
    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DICT_CODES);
        SelectDto selectJdsfDto = this.getSelectDataFromDataDic(BussinessDictCodeRds.监督时分, null);
        List<SelectOptionDto> optionJdsf = selectJdsfDto.getOptions();
        Map<String,List<SelectOptionDto>> mapJdsf = optionJdsf.stream().collect(JoyinCollectors.groupingBy(x->x.getId()));
        SelectDto selectJsmsDto = this.getSelectDataFromDataDic(BussinessDictCodeRds.组合层次, null);
        List<SelectOptionDto> optionJsms = selectJsmsDto.getOptions();
        Map<String,List<SelectOptionDto>> mapZhcc = optionJsms.stream().collect(JoyinCollectors.groupingBy(x->x.getId()));
        SelectDto selectJdlxDto = this.getSelectDataFromDataDic(BussinessDictCodeRds.监督类型, null);
        List<SelectOptionDto> optionJdlx = selectJdlxDto.getOptions();
        Map<String,List<SelectOptionDto>> mapJdlx = optionJdlx.stream().collect(JoyinCollectors.groupingBy(x->x.getId()));
        //查询指标参数
        List<SelectOptionDto> mapZbcs = rskSupervisionIndicator001Service.getIndicatorParamList();
        model.addAttribute("mapJdsf",mapJdsf);
        model.addAttribute("mapZhcc",mapZhcc);
        model.addAttribute("mapJdlx",mapJdlx);
        model.addAttribute("INDICATOR_PARAM",mapZbcs);
        model.addAttribute("PORTFOLIO_LEVEL_01",BussinessDictCodeListRds.组合层次_单组合.getValue());
        model.addAttribute("CLASSIFY_DIMENSION_01", BussinessDictCodeListRds.分类维度_收益类型.getValue());
        model.addAttribute("xztzje", BussinessDictCodeListRds.指标类型_限制投资金额类.getValue());
        model.addAttribute("drjyl", BussinessDictCodeListRds.指标类型_当日交易量_市场成交量控制类.getValue());
        model.addAttribute("jzd", BussinessDictCodeListRds.指标类型_集中度类.getValue());
        model.addAttribute("zxjgkz", BussinessDictCodeListRds.指标类型_执行价格控制类.getValue());
        model.addAttribute("lzxgl", BussinessDictCodeListRds.指标类型_流动性管理类.getValue());
        model.addAttribute("txkz", BussinessDictCodeListRds.指标类型_投向控制类.getValue());
        model.addAttribute("ggkz", BussinessDictCodeListRds.指标类型_杠杆控制类.getValue());
        model.addAttribute("qxkz", BussinessDictCodeListRds.指标类型_期限控制类.getValue());
        model.addAttribute("jyekz", BussinessDictCodeListRds.指标类型_交易额控制类.getValue());
        model.addAttribute("wtjk", BussinessDictCodeListRds.监督时分_委托监控.getValue());
        model.addAttribute("jyjk", BussinessDictCodeListRds.监督时分_交易监控.getValue());
        model.addAttribute("shjk", BussinessDictCodeListRds.监督时分_事后监控.getValue());
        model.addAttribute("cfjk", BussinessDictCodeListRds.监督时分_拆分监控.getValue());
        model.addAttribute("cjcz_yj", BussinessDictCodeListRds.触警操作_预警.getValue());
        model.addAttribute("cjcz_sp", BussinessDictCodeListRds.触警操作_审批.getValue());
        model.addAttribute("cjcz_jz", BussinessDictCodeListRds.触警操作_禁止.getValue());
        model.addAttribute("clsfyc_tsyc", BussinessDictCodeListRds.除0通过条件_提示异常.getValue());
        String workflowId = getParam("workflowId");
        // 待办跳转
        if (StringUtil.isNotEmpty(workflowId)&&!WorkFlowUtil.isNotFromToDoList()) {
            model.addAttribute("workflowId", workflowId);
            RskSupervisionIndicator002Dto rskSupervisionIndicator002Dto = rskSupervisionIndicator002Service.getBussinessEntityByWorkflowId(workflowId);
            if(null==rskSupervisionIndicator002Dto.getRskSupervisionIndicatorDto()){
            	  RskSupervisionIndicator002Dto deleDto = rskSupervisionIndicator002Service.getBussinessEntityByIndId(rskSupervisionIndicator002Dto.getIndId());
                  if(StringUtil.isNotEmpty(deleDto.getPortfolioCodeConst())){
      	            List<RskPortfolioIndicatorDto> rskPortIndList=Arrays.asList(deleDto.getPortfolioCodeConst().split(GlobalConstant.COMMA)).stream().map(item ->{
      	        		RskPortfolioIndicatorDto rskEntry=new RskPortfolioIndicatorDto();
      	        		rskEntry.setPortfolioId(item);
      	        		return rskEntry;
      	        	}).collect(Collectors.toList());
      	          deleDto.setSelectedPortfIndItem(rskPortIndList);
                  };
                  RskSupervisionIndicatorDto rskHavedDto=new RskSupervisionIndicatorDto();
              	BeanUtil.copyPropertiesIgnoreNull(rskHavedDto, deleDto);
              	deleDto.setRskSupervisionIndicatorDto(rskHavedDto);
              	 model.addAttribute("entity", deleDto);
            }
            else{
	            model.addAttribute("entity", rskSupervisionIndicator002Dto);
            }
            model.addAttribute("startWfType", rskSupervisionIndicator002Dto.getStatus().equals("P")?"delete":rskSupervisionIndicator002Dto.getStartWfType());
        } else if (StringUtil.isNotEmpty(this.getParam("indId"))){
            String indId = this.getParam("indId");
            RskSupervisionIndicator002Dto rskSupervisionIndicator002Dto = rskSupervisionIndicator002Service.getBussinessEntityByIndId(indId);
            if(StringUtil.isNotEmpty(rskSupervisionIndicator002Dto.getPortfolioCodeConst())){
	            List<RskPortfolioIndicatorDto> rskPortIndList=Arrays.asList(rskSupervisionIndicator002Dto.getPortfolioCodeConst().split(GlobalConstant.COMMA)).stream().map(item ->{
	        		RskPortfolioIndicatorDto rskEntry=new RskPortfolioIndicatorDto();
	        		rskEntry.setPortfolioId(item);
	        		return rskEntry;
	        	}).collect(Collectors.toList());
	            rskSupervisionIndicator002Dto.setSelectedPortfIndItem(rskPortIndList);
            };
            RskSupervisionIndicatorDto rskHavedDto=new RskSupervisionIndicatorDto();
        	BeanUtil.copyPropertiesIgnoreNull(rskHavedDto, rskSupervisionIndicator002Dto);
        	rskSupervisionIndicator002Dto.setRskSupervisionIndicatorDto(rskHavedDto);
            model.addAttribute(GlobalConstant.ENTITY, rskSupervisionIndicator002Dto);
        }
        if (StringUtil.isNotEmpty( getParam("startWfType"))){
        	model.addAttribute("startWfType", getParam("startWfType"));
        }
        model.addAttribute("SYS_DATE", DateUtil.format(DateUtil.platDateFromCache()));
        return super.init(model);
    }
  
    /**
     * 获取指标类型联动关联数据值
     * @return 画面元素
     * @throws Exception Exception
     * @since 1.0
     */
	@ResponseBody
    @RequestMapping("/getIndTypeRelateItems")
    public List<SelectOptionDto> getIndTypeRelateItems(@RequestParam(name="dictCode", required=false) String dictCode)
         {
		  DataDict dataDict = CacheUtil.getDictCode(dictCode);
    	List<? extends DataDictItem> itemList =  dataDict.getItems();
        List<SelectOptionDto> options = new ArrayList<>(itemList.size());
        itemList.forEach(item -> {
            SelectOptionDto option = new SelectOptionDto();
            // 值
            option.setId(item.getItemCode());
            // 文本
            option.setText(item.getItemValue());
            // 简称
            option.setCol( dataDict.getDictCode());
            // 级联父级元素code值
            option.setParentId(item.getParentId());
            options.add(option);
        });
    	return options;
    }

    /**
     * 获取枚举型参数动态禁投或可投项--add by jjpei 20190811---
     * @return 画面元素
     * @throws Exception Exception
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getForbidEnumInfo")
    public List<SelectOptionDto> getForbidEnumInfo(@RequestParam(name="forbidEnum", required=false) String forbidEnum)
             {
        List<SelectOptionDto> options = rskSupervisionIndicator001Service.getForbidEnumInfo(forbidEnum);
        return options;
    }
    /**
     * 获取组合树结构数据
     * @return 画面元素
     * @throws Exception Exception
     * @since 1.0
     */
    @SuppressWarnings("rawtypes")
	@ResponseBody
    @RequestMapping("/getAllPortfolioBySort")
    public List<TreeNode> getAllPortfolioBySort(@RequestParam(name="type", required=false) String type,@RequestParam(name="indId", required=false) String indId,@RequestParam(name="portfName", required=false) String portfName,@RequestParam(name="profitType", required=false) String profitType
    		,@RequestParam(name="issueMode", required=false) String issueMode,@RequestParam(name="raiseType", required=false) String raiseType,@RequestParam(name="investType", required=false) String investType,@RequestParam(name="isFaceSingleInvestor", required=false) String isFaceSingleInvestor)
         {
        return rskSupervisionIndicator001Service.getAllPortfolioBySort(type,indId,portfName,profitType,issueMode,raiseType,investType,isFaceSingleInvestor);
        
    }
    
    /**
     * 获取组合包结构数据
     * @return 画面元素
     * @throws Exception Exception
     * @since 1.0
     */
    @SuppressWarnings("rawtypes")
	@ResponseBody
    @RequestMapping("/getAllPortfolioBag")
    public List<TreeNode> getAllPortfolioBag(@RequestParam(name="bagName", required=false) String bagName,@RequestParam(name="indId", required=false) String indId)
         {
        return rskSupervisionIndicator001Service.getAllPortfolioBag(bagName,indId);
    }
    
    /**
     * 保存按钮点击时的check
     * @return check结果
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) RskSupervisionIndicator002Dto dto, BindingResult result ) {
    	if(result.getAllErrors().isEmpty()&&StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)
    			&&rskSupervisionIndicator002Service.isConfigure(dto.getRskSupervisionIndicatorDto().getIndId())>0){
    		List<JoyinMessage> joyinMessages = new ArrayList<>();
    		JoyinMessage ms = new JoyinMessage(getMessage("RSKRDS_W_0065"), MessageType.确认消息);
    		joyinMessages.add(ms);
    		AjaxResult wr = commonCheck(result, dto.getOperateType());
            wr.setJoyinMessages(joyinMessages);
            return wr;
    	}
    	return commonCheck(result, dto.getOperateType());
    }
    @ResponseBody
    @RequestMapping("findPage")
    public Page<RskSupervisionIndicator002Dto> findPage(){
    	return rskSupervisionIndicator002Service.selectForPage(
                RequestUtil.getParameters(getRequest()));
    }
    
    /**
     * 保存
     * @param dto
     * @throws Exception
     * @since 1.0
     */
    @DTXTransaction
    @ResponseBody
    @RequestMapping(PAGE_SAVE)
    public AjaxResult save(@RequestBody RskSupervisionIndicator002Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
        	 // 监督指标代码生成
        	dto.getRskSupervisionIndicatorDto().setIndId(UUID.randomUUID().toString().replaceAll("-", ""));
        	dto.getRskSupervisionIndicatorDto().setIsEnabled(BussinessDictCodeListRds.使用状态_启用.getValue());
        }
        if(null != dto.getRskSupervisionIndicatorDto()) {
	        if (!StringUtil.isEmpty(dto.getRskSupervisionIndicatorDto().getIndId())) {
	        	dto.setIndId(dto.getRskSupervisionIndicatorDto().getIndId());
	        }
	        if (!StringUtil.isEmpty(dto.getRskSupervisionIndicatorDto().getIndName())) {
	        	dto.setIndName(dto.getRskSupervisionIndicatorDto().getIndName());
	        }
        }
        // 监督指标代码生成
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_限额监督指标管理);
        ProcessParameter<RskSupervisionIndicator002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<RskSupervisionIndicator002Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        // 金融产品代码作为业务编号
        String businessCode = dto.getRskSupervisionIndicatorDto().getIndId();
        params.setBusinessCode(businessCode);
        params.setBusinessType(BussinessDictCodeListRds.共通业务类型_限额监督指标管理.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment(getMessage("RskSupervisionIndicator001.indId"),
                GlobalConstant.OPEN_PARENTHESIS, businessCode, GlobalConstant.CLOSE_PARENTHESIS,
                getMessage("RskSupervisionIndicator002.indName"), GlobalConstant.OPEN_PARENTHESIS,
                entity.getRskSupervisionIndicatorDto().getIndName(), GlobalConstant.CLOSE_PARENTHESIS);
        });
        processParameter.setWorkFlowParameter(params);
        String msg = rskSupervisionIndicator002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
    
    //------------add by liqianqian  20190906---指标名称验证---start---
	@ResponseBody
    @RequestMapping("chkIndNameRepeat")
	public AjaxResult chkIndNameRepeat(@RequestParam(value="indName") String indName,
			@RequestParam(value="indId") String indId) {
		AjaxResult aj = new AjaxResult();
		List<ValidMessage> resultList = new ArrayList<ValidMessage>();
		// 判断指标名称是否重复
		int count = rskSupervisionIndicator002Service.searchRepeatByIndName(indId,
				indName);
		if (count > 0) {
			resultList.add(new ValidMessage(getMessage("RSKRDS_W_0070")));
		}
		aj.setErrorList(resultList);
		return aj;
	}
	// ------------add by liqianqian 20190906---指标名称验证---end---
}
    
