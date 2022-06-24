/*
 * 文件名：TrdPlanNetInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年12月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.plannet.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.trade.bond.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.plannet.dto.TrdPlanNetInvestTradeDto;
import com.joyintech.tams.invest.trade.plannet.service.TrdPlanNetInvestTrade002Service;
import com.joyintech.tams.invest.trade.plannet.service.TrdPlanNetInvestTrade003Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author QuJianZhong
 * @version 1.0 2020年1月7日
 * @see TrdPlanNetInvestTrade002Controller
 * @since 1.0
 */
@PageId("TrdPlanNetInvestTrade004")
@Controller
@RequestMapping("/invest/trade/plannet/TrdPlanNetInvestTrade004")
public class TrdPlanNetInvestTrade004Controller extends BaseController {
	
    @Autowired
    private TrdPlanNetInvestTrade002Service trdPlanNetInvestTrade002Service;
    
    @Autowired
    private TrdPlanNetInvestTrade003Service trdPlanNetInvestTrade003Service;
    
    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;
    
    @Autowired
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;

    /**
     * 划款指令业务组件service
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * 业务编号组件
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired(required = false)
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;


    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        /*添加资产二级-四级分类的数据字典值 zhangpuyu 2019/7/31 start*/
        setSelectOptionToModel(model, new DictCode[] {DictCode.计划类资产一级分类,DictCode.计划类资产二级分类,
            DictCode.交易方向, DictCode.清算速度, DictCode.币种,DictCode.行内资产二级分类, DictCode.行内资产三级分类,
            DictCode.行内资产四级分类,DictCode.分配方式});
        /*添加资产二级-四级分类的数据字典值 zhangpuyu 2019/7/31 end*/
        model.addAttribute("ps_T04", DictCodeList.交易方向_申购.getValue());
        model.addAttribute("ps_T06", DictCodeList.交易方向_赎回.getValue());
        model.addAttribute("ps_mr", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("ps_mc", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("plansType01", DictCodeList.计划类资产二级分类_公募计划.getValue());
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        
        TrdPlanNetInvestTradeDto dto = new TrdPlanNetInvestTradeDto();
        String tradeId = this.getParam("tradeId");
        String status = this.getParam("validStatus");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        model.addAttribute("TodoList", !WorkFlowUtil.isNotFromToDoList());
        if (StringUtil.isNotEmpty(tradeId)) {
            // 双击查看反显流程中的数据
            if (StringUtil.equals(status, DictCodeListExt1.交易流程状态_流程中.getValue())) {
                dto = trdPlanNetInvestTrade002Service.getBussinessEntityByWorkflowId(workflowId);
                if (dto.getBussTrdTransferOrder() != null
                    && dto.getBussTrdTransferOrder().size() > 0) {
                    BussTrdTransferOrderDto bttoDto = dto.getBussTrdTransferOrder().get(0);
                    model.addAttribute("entity_bussRepFinProductInfoDto",
                        JsonUtils.object2Json(bttoDto));
                }
                TrdInvestPersonInfo002Dto tipiDto = dto.getTrdInvestPersonInfo();
                model.addAttribute("riskTrialRetDto", JsonUtils.object2Json(dto.getRiskTrialRetDto()));
                model.addAttribute("entity_trdInvestPersonInfo", JsonUtils.object2Json(tipiDto));
                model.addAttribute("businessId", workflowId);
            }
            // 双击查看反显非流程中的数据
            else {
                if (StringUtil.equals(status, DictCodeListExt1.交易流程状态_撤销.getValue())) {
                    tradeId = this.getParam("pTradeId");
                }
                dto = trdPlanNetInvestTrade002Service.getTrdFundDto(tradeId);
                model.addAttribute("tradeId", dto.getTradeId());
                model.addAttribute("entity_trdInvestPersonInfo",
                    JsonUtils.object2Json(dto.getTrdInvestPersonInfo()));
                model.addAttribute("businessId", dto.getTradeId());
            }
        }
        else if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            dto = trdPlanNetInvestTrade002Service.getBussinessEntityByWorkflowId(workflowId);
            if (dto.getBussTrdTransferOrder() != null
                && dto.getBussTrdTransferOrder().size() > 0) {
                BussTrdTransferOrderDto bttoDto = dto.getBussTrdTransferOrder().get(0);
                model.addAttribute("entity_bussRepFinProductInfoDto",
                    JsonUtils.object2Json(bttoDto));
            }
            TrdInvestPersonInfo002Dto tipiDto = dto.getTrdInvestPersonInfo();
            model.addAttribute("riskTrialRetDto", JsonUtils.object2Json(dto.getRiskTrialRetDto()));
            model.addAttribute("entity_trdInvestPersonInfo", JsonUtils.object2Json(tipiDto));
            model.addAttribute("businessId", workflowId);
        } else {
            String entrustId = this.getParam("entrustId");
            if (StringUtil.isNotEmpty(entrustId)) {
            	String[] entrustIdArr = entrustId.split(",");
            	com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto  entrustDto = trdPlanNetInvestTrade002Service.getEtrustDto(entrustIdArr[0]);
                if (entrustDto != null) {
                    EtrEntrust eedto = entrustDto.getEtrEntrust();
                    if (eedto != null) {
                        dto.setFinprodId(eedto.getFinprodId());
                        dto.setPs(eedto.getPs());
                        TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
                        trdInvestPersonInfo.setCounterTrader(eedto.getCounterTrader());
                        trdInvestPersonInfo.setIsOutTrade(eedto.getIsOutTrade());
                        trdInvestPersonInfo.setCounterId(eedto.getCounterId());
                        trdInvestPersonInfo.setContactInfo(eedto.getContactInfo());
                        if (entrustDto.getEtrDistributeList() != null
                            && entrustDto.getEtrDistributeList().size() > 0) {
                            trdInvestPersonInfo.setTrader(
                                entrustDto.getEtrDistributeList().get(0).getTrader());
                        }
                        model.addAttribute("entity_trdInvestPersonInfo",
                            JsonUtils.object2Json(trdInvestPersonInfo));
                    }
                }
                dto.setEntrustId(entrustId);
                model.addAttribute("businessId", entrustId);
            }
        }
        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        dto.setProfitType(DictCodeList.收益类型_净值型.getValue());
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("FINPROD_TYPE_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        model.addAttribute("entity", dto);
        //---add by 裴家俊---2019/04/04-----
        model.addAttribute("workflowId", workflowId);
        //---add by 裴家俊---2019/04/04-----
        //sjj add 20190422 计划类净值型申购的清算速度默认T+1 start
        model.addAttribute("settleSpreed_T1", DictCodeList.清算速度_T_1.getValue());
        model.addAttribute("settleSpreed_T2", DictCodeListExt2.清算速度_T_2.getValue());
        //sjj add 20190422 计划类净值型申购的清算速度默认T+1 end
        
        model.addAttribute("ACCT_TYPE_A01", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        model.addAttribute("ACCT_TYPE_A07", DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        
        //add by lizhi 是否限制划款指令开关 2020年2月26日  start
        model.addAttribute("GEN_PAY_ORDERS_AUTO",CacheUtil.getSysParam(BussinessConstant.GEN_PAY_ORDERS_AUTO));
        //add by lizhi 是否限制划款指令开关 2020年2月26日  end
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
     * 资产代码联想下拉框 <功能详细描述>
     * 
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("getFinprodId")
    @ResponseBody
    Page<SelectOptionDto> getFinprodId() {
        return trdPlanNetInvestTrade002Service.getFinprodId(RequestUtil.getPageParam());
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
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) TrdPlanNetInvestTradeDto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
    	//huwenbin 指令接收交易录单验证数据已存在流程中
      /*  if(StringUtil.isNotEmpty(dto.getEntrustId()) && WorkFlowUtil.isNotFromToDoList()){
    		String str = tradeCommonService.check(dto.getEntrustId(), this.getPageId());
    		if(StringUtil.isNotEmpty(str)){
    			AjaxResult res= new AjaxResult();
				res.setMessage(str, MessageType.错误消息);
    			return res;
    		}
        	// 查询交易流程中录入的总金额	王贵奇	2019/9/3
        	BigDecimal entrustNum = tradeCommonService.getEntrustAmtByEntrustId(dto.getEntrustId());
            BigDecimal wflPrinAmt = tradeCommonService.getWflPrinAmt(dto.getEntrustId(), dto.getPortfolioId());
            if(entrustNum.compareTo(wflPrinAmt.add(dto.getFpriceAmt())) == -1) {
            	AjaxResult res= new AjaxResult();
                res.setMessage("可用委托金额不足："+ entrustNum.subtract(wflPrinAmt), MessageType.错误消息);
                return res;
            }
    	}*/
        AjaxResult ar = commonCheck(result, dto.getOperateType());
        //modify by mashukai 20210114 ZGBSS-215 start
        Boolean amtFlag = false;
        List<BussTrdTransfenpeiDto> fenpei = dto.getBussTrdTransfenpeiDto();
        for (BussTrdTransfenpeiDto fenpeiDto : fenpei) {
            if (StringUtil.equals(dto.getPs(),DictCodeList.交易方向_申购.getValue())){
                if (fenpeiDto.getTradeAmt() != null && fenpeiDto.getBussTrdAvailableCashQueryavalibleCash() != null &&
                        fenpeiDto.getTradeAmt().compareTo(fenpeiDto.getBussTrdAvailableCashQueryavalibleCash()) == 1){
                    ar.setData("交易金额大于组合可用金额（可透支）," + ar.getData());
                    amtFlag = true;
                    break;
                } 
            }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_赎回.getValue())){
                if (fenpeiDto.getShareAmt() != null && fenpeiDto.getAssetsavailablepositions() != null &&
                        fenpeiDto.getShareAmt().compareTo(fenpeiDto.getAssetsavailablepositions()) == 1){
                    return new AjaxResult("交易份额不能大于资产可用持仓！",MessageType.错误消息);
                }
            }
             
        }
        if (!amtFlag){
            //按照组合代码分组
            Map<String, List<BussTrdTransfenpeiDto>> groupBylist = dto.getBussTrdTransfenpeiDto().stream().collect(Collectors.groupingBy(BussTrdTransfenpeiDto::getPortfolioId));
            if (groupBylist.size() > 0) {
                // map遍历
                if (StringUtil.equals(dto.getPs(),DictCodeList.交易方向_申购.getValue())){
                    groupBylist.forEach((k, v) -> {
                        BigDecimal tAmt = BigDecimal.ZERO;
                        BigDecimal aAmt = BigDecimal.ZERO;
                        for (BussTrdTransfenpeiDto fenpeiDto : v) {
                            if (fenpeiDto.getTradeAmt() != null && fenpeiDto.getBussTrdAvailableCashQueryavalibleCash() != null){
                                aAmt = fenpeiDto.getBussTrdAvailableCashQueryavalibleCash();
//                                tAmt = aAmt.add(fenpeiDto.getTradeAmt());
                                tAmt = fenpeiDto.getTradeAmt();
                            }
                        }
                        if (tAmt.compareTo(aAmt) == 1){
                            ar.setData("组合" + v.get(0).getPortfolioId() + "交易金额大于组合可用金额（可透支）," + ar.getData());
                        }
                    }); 
                }else if (StringUtil.equals(dto.getPs(),DictCodeList.交易方向_赎回.getValue())){
                    for (String port : groupBylist.keySet()){
                        List<BussTrdTransfenpeiDto> checkList = groupBylist.get(port);
                        if (checkList.size()>0){
                            BigDecimal tShare = BigDecimal.ZERO;
                            BigDecimal cShare = BigDecimal.ZERO;
                            for (BussTrdTransfenpeiDto fenpeiDto :checkList){
                                if (fenpeiDto.getAssetsavailablepositions() != null && fenpeiDto.getShareAmt() != null){
                                    tShare = tShare.add(fenpeiDto.getShareAmt());
                                    cShare = fenpeiDto.getAssetsavailablepositions();
                                }
                            }
                            if (tShare.compareTo(cShare) == 1) {
                                return new AjaxResult("交易份额不能大于资产可用持仓！",MessageType.错误消息);
                            }
                        }
                    } 
                }
            } 
        }
        //modify by mashukai 20210114 ZGBSS-215 end    
//        if (StringUtil.isNotEmpty(dto.getCalendarId()) && dto.getTradeDate() != null) {
//            boolean flg = dateCalService.isDealCalendarHoliday(dto.getTradeDate(),
//                dto.getCalendarId());
//            if (flg == true) {
//                ar.setData("交易日期为节假日," + (String)ar.getData());
//            }
//        }
        //外部交易对手买入，校验持仓+本金 < 融资金额或者发行金额
        if ((StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()) ||
                StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()) ) &&
                StringUtil.equals(dto.getTrdInvestPersonInfo().getIsOutTrade(), "S")){
            // 调用基础组件.【组合可用现金和实际现金查询更新组件.查询持仓(可用)】
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("portfolioId", dto.getPortfolioId());
            paramMap.put("tradeDate", DateUtil.format(dto.getTradeDate()));
            paramMap.put("ccy", dto.getCcy());
            paramMap.put("calendarId", dto.getCalendarId());
            paramMap.put("chlId", dto.getChlId());
            paramMap.put("finprodId", dto.getFinprodId());
            //获得资产维度的持仓
            Map<String, Object>  resultMap = bussTrdAvailableCashQueryService.getFinAvaPosition(paramMap);

            BigDecimal position = new BigDecimal((String) resultMap.get("POSITION"));
            //融资金额
            BigDecimal issueAmt = new BigDecimal((String)resultMap.get("ISSUEAMT"));
            position = com.joyintech.framework.util.ArithmeticUtil.doAdd(position, dto.getTradeAmt());
            //资产 融资金额、发行金额
            if (issueAmt.compareTo(position) < 0) {
            	return new AjaxResult("资产发行金额不足，限制交易！",MessageType.错误消息);
            }
        }
        return ar;
    }

    /**
     * 新建/修改流程 <功能详细描述>
     * 
     * @param dto
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("save")
    @ResponseBody
    public AjaxResult save(@RequestBody TrdPlanNetInvestTradeDto dto, BindingResult br) throws InvocationTargetException, IllegalAccessException, IOException, InstantiationException, WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        List<TrdPlanNetInvestTradeDto> dtoList = new ArrayList<>();
    	List<BussTrdTransfenpeiDto> fenpeiList = dto.getBussTrdTransfenpeiDto();//组合分配
    	List<BussTrdTransferOrderDto> orderList = dto.getBussTrdTransferOrder();//划款指令
    	for(int i = 0; i < fenpeiList.size(); i++) {
    		if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()) 
					|| StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())){
				if(!(fenpeiList.get(i).getTradeAmt().compareTo(BigDecimal.ZERO) == 1)){
					continue;
				}
			}else if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()) 
					|| StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())){
				if( fenpeiList.get(i).getShareAmt()!=null && !(fenpeiList.get(i).getShareAmt().compareTo(BigDecimal.ZERO) == 1)){
					continue;
				}
			}
    		TrdPlanNetInvestTradeDto tempDto = new TrdPlanNetInvestTradeDto();
    		BeanUtil.copyProperties(tempDto, dto);
    		List<BussTrdTransferOrderDto> tmpOrderList = new ArrayList<BussTrdTransferOrderDto>();
			tempDto.setEntrustId(fenpeiList.get(i).getEntrustId());//委托代码
			tempDto.setPortfolioId(fenpeiList.get(i).getPortfolioId());//组合代码
			tempDto.setTradeAmt(fenpeiList.get(i).getTradeAmt());//交易金额
			tempDto.setPrinAmt(fenpeiList.get(i).getTradeAmt());
			tempDto.setShareAmt(fenpeiList.get(i).getShareAmt());//交易份额
			if(!StringUtil.isEmptyList(orderList)){
				tmpOrderList.add(orderList.get(i));
				tempDto.setBussTrdTransferOrder(tmpOrderList);
			}
			if (baseGetBusinessId != null) {
	            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
	            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_净值型_交易);
	            if(StringUtil.equals(dto.getType7(), DictCodeList.计划类资产二级分类_公募计划.getValue())){
	                codeParameter.setType1(DictCodeList.计划类资产二级分类_公募计划);
	            }else if(StringUtil.equals(dto.getType7(), DictCodeList.计划类资产二级分类_委托债权.getValue())){
	                codeParameter.setType1(DictCodeList.计划类资产二级分类_委托债权);
	            }else if(StringUtil.equals(dto.getType7(), DictCodeList.计划类资产二级分类_委托权益.getValue())){
	                codeParameter.setType1(DictCodeList.计划类资产二级分类_委托权益);
	            }else if(StringUtil.equals(dto.getType7(), DictCodeList.计划类资产二级分类_自主管理.getValue())){
	                codeParameter.setType1(DictCodeList.计划类资产二级分类_自主管理);
	            }
	            tempDto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
	        }
	        else {
	        	tempDto.setTradeId(IDGenerator.generate());
	        }
			tempDto.setRiskTrialRetDto(null);
            //多组合交易对风险试算处理逻辑
            if(CollectionUtils.isNotEmpty(dto.getRiskTrialRetDto())){
                 List<RiskTrialRetDto> riskTrialRetlist = new ArrayList<>();
                 for (RiskTrialRetDto riskTrialRetDto:dto.getRiskTrialRetDto()) {
                    if(StringUtils.equals(riskTrialRetDto.getPortfolioCode(),fenpeiList.get(i).getPortfolioId())){
                        riskTrialRetlist.add(riskTrialRetDto); 
                    }
                    List<String> colist= interfRiskRdsService.queryPortfolioPackageDtlByBagId(riskTrialRetDto.getPortfolioCode());
                    if (!CollectionUtils.isEmpty(colist) && colist.contains(fenpeiList.get(i).getPortfolioId())) {
                        riskTrialRetlist.add(riskTrialRetDto); 
                    } 
                 }  
                 tempDto.setRiskTrialRetDto(riskTrialRetlist);
            }
			dtoList.add(tempDto);
    	}
    	 //----------add by jjpei 20200114 核风险版本 start----------
        if(dto.getOperateType().equals(BussinessConstant.SUBMIT)&& CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)){
            AjaxResult ar =interfRiskRdsService.checkBatchRiskRds(dtoList,new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
                    BussinessDictCodeListRds.接口系统ID_实占接口.getValue(), BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(),DictCodeList.平安资产类型_计划类资产_净值型.getValue(),
                    (sm,en)->interfRiskRdsService.sendRiskRdsData(sm,en));
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        //----------add by jjpei 20200114 核风险版本 end---------- 
        ProcessParameter<TrdPlanNetInvestTradeDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdPlanNetInvestTradeDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId("TrdPlanNetInvestTrade002");
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            processParameter.setOperationType(OperationType.NEW);
        }
        else {
            processParameter.setOperationType(OperationType.MODIFY);
        }
        workFlowParameter.setPageId("TrdPlanNetInvestTrade002");
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode((entity) -> entity.getTradeId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue());
        workFlowParameter.setComment(
            (entity) -> WorkFlowUtil.concatComment(getMessage("TrdPlanNetInvestTrade002.tradeId"),
                entity.getTradeId(),
                getMessage("TrdPlanNetInvestTrade002.finprodName"),
                entity.getFinprodName(),
                getMessage("TrdPlanNetInvestTrade002.tradeDate"),
                DateUtil.format(entity.getTradeDate())));
        if (BussinessConstant.SUBMIT.equals(dto.getOperateType())) {
        	String totleTradeId = IDGenerator.generate();
        	for (TrdPlanNetInvestTradeDto trdPlanNetInvestTradeDto : dtoList) {
        		prepareEntity(trdPlanNetInvestTradeDto);
        		trdPlanNetInvestTradeDto.setTotleTradeId(totleTradeId);
        	}
        }
        String msg = trdPlanNetInvestTrade002Service.start(processParameter, dtoList.toArray(new TrdPlanNetInvestTradeDto[]{}));
        return new AjaxResult(msg);
    }

    /**
     * 处理基金投资交易自定义dto <功能详细描述>
     * 
     * @param dto
     * @see
     * @since 1.0
     */
    public void prepareEntity(TrdPlanNetInvestTradeDto dto) {
        dto.setActPortfolioId(dto.getPortfolioId());
        dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));
        dto.setPrinAmt(dto.getTradeAmt());
        dto.setYield(ArithmeticUtil.divide(dto.getYield(), new BigDecimal(100)));
        dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), new BigDecimal(100)));
        dto.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue")));
        dto.setIsCancel(DictCodeList.是否_否.getValue());
        dto.setBusiType(DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue());
        dto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        dto.setSettleDate(dto.getTradeDate());
        if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())){
            dto.setIsClean(DictCodeList.是否_是.getValue());
            dto.setIssuePartyId(CacheUtil.getSysParam(BussinessConstant.ISSUE_PARTY_ID));
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                BigDecimal zero = new BigDecimal(0);
                BigDecimal redemptionFee = dto.getRedemptionFee();
                if (redemptionFee == null) {
                    redemptionFee = zero;
                }
                BigDecimal channelFee = dto.getChannelFee();
                if (channelFee == null) {
                    channelFee = zero;
                }
                BigDecimal hostingFee = dto.getHostingFee();
                if (hostingFee == null) {
                    hostingFee = zero;
                }
                dto.setFeeAmtPay(redemptionFee.add(channelFee.add(hostingFee)));
                if (dto.getFeeAmtPay().equals(zero)) {
                    dto.setFeeAmtPay(null);
                }
            }
            dto.setFeeAmt(dto.getFeeAmtPay());
        }
        TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
        if (personInfo != null) {
            dto.setTrader(personInfo.getTrader());
            dto.setCounterId(personInfo.getCounterId());
            dto.setIsOutTrade(personInfo.getIsOutTrade());
            dto.setCounterTrader(personInfo.getCounterTrader());
            dto.setCounterContact(personInfo.getContactInfo());
        }
        // 金融产品交易附属表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();

        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
        ||StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
            // 申购申请、赎回申请，将确认日期保存到 预计申购确认日 字段
            trdProductDealAdd.setPurCfmDate(dto.getPurCfmDate());
            /*Map<String, Object> param = new HashMap<String, Object>();
            param.put("finprodId", dto.getFinprodId());
            Map<String, Object> result = trdPlanNetInvestTrade003Service.getPurSpeed(param);
            String speed = (String)result.get("SPEED");
            String calId = (String)result.get("CALID");
            if (StringUtil.equals(speed, "1") || StringUtil.equals(speed, "2")) {
                int speedCount = Integer.parseInt(speed);
                Date tradeDate = dto.getTradeDate();
                List<String> holidayList = dateCalService.getDealCalendarHoliday(
                    DateUtil.parse(DateUtil.format(tradeDate)),
                    DateUtil.addDay(DateUtil.parse(DateUtil.format(tradeDate)), 15), calId);
                Date tradeDay = dateCalService.calculationTradingDay(
                    DateUtil.parse(DateUtil.format(tradeDate)), speedCount, holidayList, "1");
                trdProductDealAdd.setPurCfmDate(tradeDay);
            }*/
        }
        else {
            trdProductDealAdd.setRedCfmDate(dto.getRedCfmDate());
        }
        trdProductDealAdd.setRedCfmDate(dto.getRedCfmDate());
        trdProductDealAdd.setTradeId(dto.getTradeId());
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId());
        trdProductDealAdd.setEntrustId(dto.getEntrustId());
        //trdProductDealAdd.setProdId(dto.getFinprodId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        dto.setTrdProductDealAdd(trdProductDealAdd);

        List<PtlTrade> ptlTrades = new ArrayList<>();
        // 组合交易表
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        ptlTrade.setBusiNo(dto.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(dto.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue());
        ptlTrade.setSettleDate(dto.getTradeDate());
        ptlTrades.add(ptlTrade);
        if (!dto.getPortfolioId().equals(dto.getActPortfolioId())) {
            // 组合交易表
            PtlTrade ptlTrade1 = new PtlTrade();
            ptlTrade1.setPortfolioId(dto.getActPortfolioId());
            ptlTrade1.setBusiNo(dto.getTradeId());
            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade1.setBusiId(dto.getFinprodId());
            ptlTrade1.setBusiType(DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue());
            ptlTrade1.setSettleDate(dto.getTradeDate());
            ptlTrades.add(ptlTrade1);
        }
        dto.setPtlTrade(ptlTrades);
        // 划款指令
        if (dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0) {
            dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getTradeId());
            dto.setBussTrdTransferOrder(
                bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            dto.getBussTrdTransferOrder().get(0).setIsTotalTrade(DictCodeList.是否_否.getValue());
        }
        // 金融产品交易扩展
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(dto.getTradeId());
        String paBussType = null;
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_净值型_申购申请.getValue();
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_净值型_申购确认.getValue();
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_净值型_赎回申请.getValue();
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_净值型_赎回确认.getValue();
        }
        paTrdProductDeal.setPaBussType(paBussType);
        paTrdProductDeal.setDeptCode(personInfo==null?null:personInfo.getTraderDept());
        dto.setPaTrdProductDeal(paTrdProductDeal);
        dto.setPortfolioId(dto.getActPortfolioId());
    }

    /**
     * 组合可用现金查询 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getProAvailableCash")
    public Map<String, Object> getProAvailableCash(@RequestBody Map<String, Object> param) {
        return trdPlanNetInvestTrade002Service.getProAvailableCash(param);
    }

    /**
     * 查询组合可用持仓 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getPtlSecPosition")
    public BigDecimal getPtlSecPosition(@RequestBody PtlSecPositionFlowDto dto) {
        return trdPlanNetInvestTrade002Service.getPtlSecPosition(dto);
    }
    
    @ResponseBody
    @RequestMapping("/selectIndex")
    public String selectIndex() {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_净值型_交易);
        String index=baseGetBusinessId.buildBussinessCode(codeParameter);
        return StringUtil.trimHtmlTag(index);
    }
    
    /**
     * 查询单位净值 <功能详细描述>
     * 
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("getPrice")
    @ResponseBody
    public BigDecimal getPrice(@RequestBody Map<String, Object> map) {
        return trdPlanNetInvestTrade003Service.getPrice(map);
    }
    
    /**
     * 查询委托信息
     *
     * @return 委托信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getEntrustInfos")
    public List<BaseEtrInvestInfoDto> getEntrustInfos(@RequestBody TrdBondInvestTradeDto dto) {
        //中标结果查询进来  add by gejiaao 2019/7/26 end
        List<BaseEtrInvestInfoDto> entrustInfos = trdBondInvestTrade002Service.getEntrustInfos(dto.getEntrustId());
        return entrustInfos;
    }
}
