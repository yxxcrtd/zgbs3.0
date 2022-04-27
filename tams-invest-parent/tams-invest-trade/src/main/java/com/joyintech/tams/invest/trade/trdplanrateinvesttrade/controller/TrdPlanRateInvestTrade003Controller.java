/*
 * 文件名：TrdPlanRateInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.trdplanrateinvesttrade.controller;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.framework.util.*;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections.CollectionUtils;
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
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.dao.WflTrdProductDealMapper;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.riskrds.constant.InterfUtil;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.BussTrdAvailableCashQuery;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.trdplanrateinvesttrade.dto.TrdPlanRateInvestTradeDto;
import com.joyintech.tams.invest.trade.trdplanrateinvesttrade.service.TrdPlanRateInvestTrade002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 计划类资产投资交易新建
 * @author fanshuanghu
 * @version 1.0 2018年11月22日
 * @see TrdPlanRateInvestTrade002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdPlanRateInvestTrade003")
@RequestMapping("/invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade003")
public class TrdPlanRateInvestTrade003Controller extends BaseController{
    @Autowired
    private TradeCommonService tradeCommonService;
    /**
     * 划款指令
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    /**
     * 注入计划类（利率型）service
     */
    @Autowired
    private TrdPlanRateInvestTrade002Service trdPlanRateInvestTrade002Service;
    /**
     * 主键生成类
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;
    /**
     * 附件上传类
     */
    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;
    /**
     * 平台日Service
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    /**
     * 代理类
     */
    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired(required = false)
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;
    
    @Autowired
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;
    /**
     * 初始化方法
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向,DictCode.交易流程状态,DictCode.计划类资产一级分类,DictCode.计划类资产二级分类,
                /*原因、zhuyongkang 2019/7/30  start*/
                DictCode.行内资产二级分类,
                DictCode.行内资产三级分类,
                DictCode.行内资产四级分类,
                /* 原因、zhuyongkang 2019/7/30  end*/
                DictCode.清算速度,DictCode.币种,
                DictCode.是否,
                DictCode.分配方式,
                DictCode.投资目的, DictCode.业务模式);

        // 一体化对应etliu start
//        model.addAttribute("PS_PURCHASE_T04",DictCodeList.交易方向_申购.getValue());
        model.addAttribute("PS_T01",DictCodeList.交易方向_买入.getValue());
        model.addAttribute("collectType_01", DictCodeListExt2.收取方式_回款前扣除.getValue());
        model.addAttribute("PS_T02",DictCodeList.交易方向_卖出.getValue());
        // 一体化对应etliu end
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("FINPROD_TYPE_F17",DictCodeListExt1.金融产品类型_计划类资产.getValue());

        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));

        Map<String,Object>  initMap = RequestUtil.getPageParam();
        String portfolioId = getParam("portfolioId");
        String entrustId = getParam("entrustId");
        String finprodId = getParam("finprodId");
        //String tradeId = getParam("tradeId");
        String totleTradeId = getParam("totleTradeId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            TrdPlanRateInvestTradeDto entity = trdPlanRateInvestTrade002Service.getBussinessEntityByWorkflowId(
                    workflowId);
            if(entity.getSettleSpeed()!=null){
                entity.setMySettleSpeed("0"+entity.getSettleSpeed().toString());}
            model.addAttribute("trdInvestPersonInfo002Dto", JsonUtils.object2Json(entity.getTrdInvestPersonInfo002Dto()));
            model.addAttribute("stlPayOrdersList", JsonUtils.object2Json(entity.getBussTrdTransferOrder().get(0)));
            model.addAttribute("entity",entity);
            model.addAttribute("riskTrialRetDto", JsonUtils.object2Json(entity.getRiskTrialRetDto()));
            model.addAttribute("workflowId", workflowId);
            //model.addAttribute("tradeId", entity.getTradeId());
            model.addAttribute("totleTradeId", entity.getTotleTradeId());

            model.addAttribute("bussTrdAvailableCashQuery",entity.getBussTrdAvailableCashQuery());
            // 判断逐渐是否为空
        }
        else if (StringUtil.isNotEmpty(totleTradeId)) {
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            if(bussCommonMapperDelegateService != null){
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDeal.setTotleTradeId(totleTradeId);
                wflTrdProductDeal = bussCommonMapperDelegateService.selectByMultiPrimaryKey(WflTrdProductDealMapper.class, wflTrdProductDeal, WflTrdProductDeal.class);
            }
            if(wflTrdProductDeal != null){
                String workFlowId = wflTrdProductDeal.getWorkflowId();
                if(workFlowId != null && !workFlowId.isEmpty()){
                    TrdPlanRateInvestTradeDto entity = trdPlanRateInvestTrade002Service.getBussinessEntityByWorkflowId(
                            workFlowId);
                    entity.setMySettleSpeed("0"+entity.getSettleSpeed().toString());
                    model.addAttribute("entity",entity);
                    model.addAttribute("riskTrialRetDto", JsonUtils.object2Json(entity.getRiskTrialRetDto()));
                    model.addAttribute("workflowId", workflowId);
                    if(entity.getBussTrdTransferOrder() != null && entity.getBussTrdTransferOrder().size()>0){
                        model.addAttribute("stlPayOrdersList",JsonUtils.object2Json(entity.getBussTrdTransferOrder().get(0)));
                    }
                    model.addAttribute("trdInvestPersonInfo002Dto",JsonUtils.object2Json(entity.getTrdInvestPersonInfo002Dto()));
                    //model.addAttribute("tradeId", tradeId);
                    model.addAttribute("totleTradeId", totleTradeId);
                }
            }
            else {
                TrdPlanRateInvestTradeDto entity = trdPlanRateInvestTrade002Service.getInitEntity(totleTradeId);//                if(StringUtil.isNotEmpty(entrustId) && !"undefined".equals(entrustId)){
//                    entity.setEntrustId(entrustId);
//                }
//                else{
//                    entity.setEntrustId("");
//                }
//                entity.setTradeId(tradeId.substring(0,18));
                if(StringUtil.isNotEmpty(portfolioId)) {
                    entity.setPortfolioId(portfolioId);
                }
                entity.setMySettleSpeed("0"+(entity.getSettleSpeed()+1));
                model.addAttribute("entity",entity);
                model.addAttribute("trdInvestPersonInfo002Dto",JsonUtils.object2Json(entity.getTrdInvestPersonInfo002Dto()));
                // model.addAttribute("tradeId", tradeId);
                model.addAttribute("totleTradeId", totleTradeId);

            }
        }else if(entrustId != null){

        	TrdPlanRateInvestTradeDto entity = new TrdPlanRateInvestTradeDto();

        	 entity.setEntrustId(entrustId);
             String finprodMarketId = getParam("finprodMarketId");
             entity.setFinprodMarketId(finprodMarketId);
             entity.setPs(getParam("ps"));
             String unfinishedAmt=this.getParam("unfinishedAmt");
             String[] entrustIdList = entrustId.split("\\,");
 		    String[] portfolioIdList = portfolioId.split("\\,");
 		    String[] unfinishedAmtList = unfinishedAmt.split("\\,");
             
             List<BussTrdTransfenpeiDto> list = new ArrayList<>();
             for(int i = 0; i<entrustIdList.length;i++){
             	if(new BigDecimal(unfinishedAmtList[i]).compareTo(new BigDecimal(0))==1){
 	            	BussTrdTransfenpeiDto bussTrdTransfenpeiDto = new BussTrdTransfenpeiDto();
 	            	bussTrdTransfenpeiDto.setEntrustId(entrustIdList[i]);
 	            	bussTrdTransfenpeiDto.setPortfolioId(portfolioIdList[i]);
 	            	bussTrdTransfenpeiDto.setDoEntrustMoney(new BigDecimal(unfinishedAmtList[i]));
 				    list.add(bussTrdTransfenpeiDto);
             	}
 			}
             entity.setBussTrdTransfenpeiDto(list);
             entity.setMySettleSpeed(DictCodeList.清算速度_T_0.getValue());
             model.addAttribute("BussTrdTransfenpeiDto", list);
             model.addAttribute("finprodId", finprodId);
             model.addAttribute("entity", entity);
             model.addAttribute("tradeId", entrustId);
             model.addAttribute("entrustId", entrustId);
        }else if(StringUtil.equals(this.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){

            //add by yujiaping 2020-12-16 TISHKBS-1259 计划利率型交易新建时，清算速度默认T+0，并且不可修改start
            TrdPlanRateInvestTradeDto entity = new TrdPlanRateInvestTradeDto();
            entity.setMySettleSpeed(DictCodeList.清算速度_T_0.getValue());
            model.addAttribute("entity", entity);
            //add by yujiaping 2020-12-16 TISHKBS-1259 计划利率型交易新建时，清算速度默认T+0，并且不可修改end

        }
        model.addAttribute("YES_OR_NO_N",DictCodeList.是否_否.getValue());
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
     * 资产代码下拉框
     * @return  资产代码下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getFinprodMarketId")
    public List<SelectOptionDto> getFinprodMarketId(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return trdPlanRateInvestTrade002Service.getFinprodMarketId(map);
    }

    /**
     * 获取交易日（平台日）
     * @return 获取交易日（平台日）
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getTradeDate")
    public Map<String,Object> getTradeDate(){
        String tradeDate = "";
        if(iPlatformDate != null){
            tradeDate = iPlatformDate.getSysDateStr();
        }
        Map<String,Object> map = new HashMap<>();
        map.put("tradeDate", tradeDate);
        return map;
    }

    /**
     * 获取金融产品类型
     * @param map 请求参数集合(金融产品代码)
     * @return 金融产品类型
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getFinprodType")
    public Map<String,Object> getFinprodType(@RequestBody Map<String,Object> map){
        return trdPlanRateInvestTrade002Service.getFinprodType(map);
    }

    /**
     * 获取交割日、交易日历ID、是否是节假日
     * @param map 金融产品代码、交易日、清算速度
     * @return 交割日、交易日历ID、是否是节假日集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("settleDateCalcu")
    public Map<String,String> settleDateCalcu(@RequestBody Map<String,Object> map){
        return trdPlanRateInvestTrade002Service.settleDateCalcu(map);
    }
    /**
     * 获取应计利息
     * @param map 请求参数
     * @return 应计利息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getIntAmt")
    public AjaxResult getIntAmt(@RequestBody Map<String,Object> map){
        String intAmt = trdPlanRateInvestTrade002Service.getIntAmtCalcu(map);
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setData(intAmt);
        return ajaxResult;
    }
    /**
     * 划款指令生成实体类
     * @return 金融产品交易实体类
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getEntity")
    public TrdProductDeal  getEntity(){
        Map<String,Object> map = RequestUtil.getPageParam();
        TrdPlanRateInvestTradeDto dto = BeanUtil.mapToObject(map, TrdPlanRateInvestTradeDto.class);
        TrdInvestPersonInfo002Dto trdInvestPersonInfo002Dto = new TrdInvestPersonInfo002Dto();
        dto.setTrdInvestPersonInfo002Dto(trdInvestPersonInfo002Dto);
        dto.getTrdInvestPersonInfo002Dto().setContactInfo(map.get("trdInvestPersonInfo002Dto[contactInfo]").toString());
        dto.getTrdInvestPersonInfo002Dto().setCounterId(map.get("trdInvestPersonInfo002Dto[counterId]").toString());
        dto.getTrdInvestPersonInfo002Dto().setCounterTrader(map.get("trdInvestPersonInfo002Dto[counterTrader]").toString());
        dto.getTrdInvestPersonInfo002Dto().setIsOutTrade(map.get("trdInvestPersonInfo002Dto[isOutTrade]").toString());
        dto.getTrdInvestPersonInfo002Dto().setTrader(map.get("trdInvestPersonInfo002Dto[trader]").toString());
        return trdPlanRateInvestTrade002Service.getEntity(dto);
    }
    /**
     * 画面点击保存按钮时check
     *
     * @param dto 实体类
     * @param result
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdPlanRateInvestTradeDto dto, BindingResult result) {
        //huwenbin 指令接收交易录单验证数据已存在流程中
        /*if(StringUtil.isNotEmpty(dto.getEntrustId()) && WorkFlowUtil.isNotFromToDoList()){
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
        //外部交易对手买入，校验持仓+本金 < 融资金额或者发行金额
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()) &&
                StringUtil.equals(dto.getTrdInvestPersonInfo002Dto().getIsOutTrade(), "S")){
            // 调用基础组件.【组合可用现金和实际现金查询更新组件.查询持仓(可用)】
            Map<String, Object> paramMap = new HashMap<String, Object>();
      //      paramMap.put("portfolioId", dto.getPortfolioId());
            paramMap.put("tradeDate", DateUtil.format(dto.getTradeDate()));
            paramMap.put("ccy", dto.getCcy());
            //paramMap.put("calendarId", dto.getCalendarId());
            paramMap.put("chlId", dto.getChlId());
            paramMap.put("finprodId", dto.getFinprodId());
            //考虑流程中被占用,是的情况
            paramMap.put("isContainWfl",DictCodeListExt1.是或否_是.getValue());
            //获得资产维度的持仓
            Map<String, Object>  resultMap = bussTrdAvailableCashQueryService.getFinAvaPosition(paramMap);

            BigDecimal position = new BigDecimal((String) resultMap.get("POSITION"));
            //融资金额
            BigDecimal issueAmt = new BigDecimal((String)resultMap.get("ISSUEAMT"));
            //流程中占用金额
            BigDecimal positionFlow =null;
            if(StringUtil.isNotEmpty(resultMap.get("POSITIONFLOW"))){
                positionFlow=new BigDecimal((String) resultMap.get("POSITIONFLOW"));
            }
            position= ArithmeticUtil.doAdd(position,positionFlow);
            position = com.joyintech.framework.util.ArithmeticUtil.doAdd(position, dto.getTradeAmt());
            //资产 融资金额、发行金额
            if (issueAmt.compareTo(position) < 0) {
                ar.setData(getMessage("TRD_C_0005"));
            }
        }
        return ar;
    }
    /**
     * 保存
     * @param dto dto
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody TrdPlanRateInvestTradeDto dto, BindingResult br) throws IOException, InstantiationException, WorkFlowException, InvocationTargetException, IllegalAccessException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        List<TrdPlanRateInvestTradeDto> dtoList = new ArrayList<>();
        List<BussTrdTransfenpeiDto> fenpeiList = dto.getBussTrdTransfenpeiDto();//组合分配
        List<BussTrdTransferOrderDto> orderList = dto.getBussTrdTransferOrder();//划款指令
        for(BussTrdTransferOrderDto bussTrdTransferOrder : orderList) {
            TrdPlanRateInvestTradeDto tempDto = new TrdPlanRateInvestTradeDto();
            BeanUtil.copyProperties(tempDto, dto);
            List<BussTrdTransferOrderDto> tmpOrderList = new ArrayList<BussTrdTransferOrderDto>();
            if(fenpeiList != null && fenpeiList.size()>0){
                for (int i = 0; i < fenpeiList.size(); i++) {
                    BussTrdTransfenpeiDto fenpei = fenpeiList.get(i);
                    if (StringUtils.equals(bussTrdTransferOrder.getPortfolioId(), fenpei.getPortfolioId())&& fenpei.getPrinAmt().compareTo(new BigDecimal(0))==1) {
                        tmpOrderList.add(bussTrdTransferOrder);
                        tempDto.setPortfolioId(bussTrdTransferOrder.getPortfolioId());
                        tempDto.setTradeAmt(fenpei.getTradeAmt());
                        tempDto.setCpriceAmt(fenpei.getCpriceAmt());
                        tempDto.setFpriceAmt(fenpei.getFpriceAmt());
//    					tempDto.setBussTrdTransferOrder(tmpOrderList);
                        tempDto.setPrinAmt(fenpei.getPrinAmt());
                        tempDto.setChannelFee(fenpei.getChannelFee());
                        tempDto.setTradeAmtAndFee(fenpei.getTradeAmtAndFee());//交易金额
                        tempDto.setIntAmt(fenpei.getIntAmt());//应计利息总额
                        tempDto.setEntrustId(fenpei.getEntrustId());//设置委托代码
                        BussTrdAvailableCashQuery btac = new BussTrdAvailableCashQuery();
                        btac.setAvalibleCash (fenpei.getBussTrdAvailableCashQueryavalibleCash());
                        btac.setAvalibleCashT1(fenpei.getBussTrdAvailableCashQueryavalibleCash1());
                        btac.setChannelAvailableCash(fenpei.getChannelAvailableCash());
                        btac.setAssetPosition(fenpei.getAssetsavailablepositions());
                        tempDto.setBussTrdAvailableCashQuery(btac);
                        if(tempDto.getTradeId() == null || tempDto.getTradeId().isEmpty() || tempDto.getTradeId().length() > 25){


                            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                            // 一体化对应etliu start
//            if(StringUtil.equals(tempDto.getPs(), DictCodeList.交易方向_申购.getValue())){
                            if(StringUtil.equals(tempDto.getPs(), DictCodeList.交易方向_买入.getValue())){
                                // 一体化对应etliu end
                                bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_净值型资产申购);
                                if(StringUtil.equals(DictCodeList.计划类资产一级分类_券商资产管理计划, tempDto.getFinprodType())){
                                    bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
                                }else if(StringUtil.equals(DictCodeList.计划类资产一级分类_信托产品, tempDto.getFinprodType())){
                                    bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_信托产品);
                                }else{
                                    bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
                                }
                                // 一体化对应etliu start
//            }else if(StringUtil.equals(tempDto.getPs(), DictCodeList.交易方向_赎回.getValue())){
                            }else if(StringUtil.equals(tempDto.getPs(), DictCodeList.交易方向_卖出.getValue())){
                                // 一体化对应etliu end
                                bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_净值型资产赎回);
                                if(StringUtil.equals(DictCodeList.计划类资产一级分类_券商资产管理计划, tempDto.getFinprodType())){
                                    bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
                                }else if(StringUtil.equals(DictCodeList.计划类资产一级分类_信托产品, tempDto.getFinprodType())){
                                    bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_信托产品);
                                }else{
                                    bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
                                }
                            }
                            if(baseGetBusinessId != null){
                                String tradId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) : IDGenerator.generate();
                                tempDto.setTotleTradeId(tradId);
                            }
//            if(!StringUtil.isNotEmpty(tempDto.getTradeId()) || tempDto.getTradeId().length() > 20){
//                tempDto.setTradeId(tempDto.getTradeId().substring(0,10));
                            
                            
                            String  tradeId=trdPlanRateInvestTrade002Service.getTradeId(tempDto.getPs(),tempDto.getFinprodType());//获取买入卖出交易的交易编号
                            tempDto.setTradeId(tradeId);
                         //   tempDto.setCounterId(dto.getTrdInvestPersonInfo002Dto().getCounterId());
                        }

                        tempDto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(tmpOrderList));
                        StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
                        BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, bussTrdTransferOrder);
                        List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
                        list.add(stlPayOrdersDto);
                        tempDto.setStlPayOrdersList(list);

                        if (CollectionUtils.isNotEmpty(tempDto.getFileArray())
                                && StringUtil.isNotEmpty(tempDto.getTradeId())) {
                            // 将附件和当前业务对象绑定（更新附件的业务代码）
                            fileAttachmentProcess.updateBussinessNo(
                                    tempDto.getFileArray().stream().map(UploadFile::getFileNo).collect(
                                            Collectors.toList()),
                                    tempDto.getTradeId());
                        } 
                        tempDto.setRiskTrialRetDto(null); 
                        //多组合交易对风险试算处理逻辑
                        if(CollectionUtils.isNotEmpty(dto.getRiskTrialRetDto())){
                             List<RiskTrialRetDto> riskTrialRetlist = new ArrayList<>();
                             for (RiskTrialRetDto riskTrialRetDto:dto.getRiskTrialRetDto()) {
                                if(StringUtils.equals(riskTrialRetDto.getPortfolioCode(),fenpei.getPortfolioId())){
                                    riskTrialRetlist.add(riskTrialRetDto); 
                                }
                                List<String> colist= interfRiskRdsService.queryPortfolioPackageDtlByBagId(riskTrialRetDto.getPortfolioCode());
                                if (!CollectionUtils.isEmpty(colist) && colist.contains(fenpei.getPortfolioId())) {
                                    riskTrialRetlist.add(riskTrialRetDto); 
                                } 
                             }  
                             tempDto.setRiskTrialRetDto(riskTrialRetlist);
                        }
                        dtoList.add(tempDto);
                    }
                }
            }else{ 
                BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                // 一体化对应etliu start
//            if(StringUtil.equals(tempDto.getPs(), DictCodeList.交易方向_申购.getValue())){
                if(StringUtil.equals(tempDto.getPs(), DictCodeList.交易方向_买入.getValue())){
                    // 一体化对应etliu end
                    bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_净值型资产申购);
                    if(StringUtil.equals(DictCodeList.计划类资产一级分类_券商资产管理计划, tempDto.getFinprodType())){
                        bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
                    }else if(StringUtil.equals(DictCodeList.计划类资产一级分类_信托产品, tempDto.getFinprodType())){
                        bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_信托产品);
                    }else{
                        bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
                    }
                    // 一体化对应etliu start
//            }else if(StringUtil.equals(tempDto.getPs(), DictCodeList.交易方向_赎回.getValue())){
                }else if(StringUtil.equals(tempDto.getPs(), DictCodeList.交易方向_卖出.getValue())){
                    // 一体化对应etliu end
                    bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_净值型资产赎回);
                    if(StringUtil.equals(DictCodeList.计划类资产一级分类_券商资产管理计划, tempDto.getFinprodType())){
                        bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
                    }else if(StringUtil.equals(DictCodeList.计划类资产一级分类_信托产品, tempDto.getFinprodType())){
                        bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_信托产品);
                    }else{
                        bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
                    }
                }
                if(baseGetBusinessId != null){
                    String tradId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) : IDGenerator.generate();
                    tempDto.setTotleTradeId(tradId);
                }
//            if(!StringUtil.isNotEmpty(tempDto.getTradeId()) || tempDto.getTradeId().length() > 20){
//                tempDto.setTradeId(tempDto.getTradeId().substring(0,10));


                dtoList.add(tempDto);
            }
        }
        //----------add by jjpei 20200114 核风险版本 start----------
        if(dto.getOperateType().equals(BussinessConstant.SUBMIT)&& CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)){
            AjaxResult ar =interfRiskRdsService.checkBatchRiskRds(dtoList,new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
                    BussinessDictCodeListRds.接口系统ID_实占接口.getValue(), BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(),DictCodeList.平安资产类型_计划类资产_利率型.getValue(),
                    (sm,en)->interfRiskRdsService.sendRiskRdsData(sm,en));
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        //----------add by jjpei 20200114 核风险版本 end----------  
        ProcessParameter<TrdPlanRateInvestTradeDto> processParameter = new ProcessParameter<TrdPlanRateInvestTradeDto>();
        WorkFlowParameter<TrdPlanRateInvestTradeDto> params = new WorkFlowParameter<TrdPlanRateInvestTradeDto>();
        params.setPageId("TrdPlanRateInvestTrade002");
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getTotleTradeId());
        params.setBusinessType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
        // 业务说明：“交易编号：” + 上述取得的交易编号+“资产名称：”+画面.资产名称+“交易日：”+画面.交易日
//        params.setComment(
//                entity -> this.getMessage("TrdPlanRateInvestTrade001.tradeId") + "("
//                        + entity.getTradeId() + ")" + "-"
//                        + this.getMessage("TrdPlanRateInvestTrade001.finprodName") + "("
//                        + entity.getFinprodName() + ")"
//                        + this.getMessage("TrdPlanRateInvestTrade001.tradeDate") + "("
//                        + DateUtil.format(entity.getTradeDate()) + ")" );
        params.setComment((entity) -> WorkFlowUtil.concatComment(
                getMessage("TrdPlanRateInvestTrade001.tradeId"),
                entity.getTotleTradeId(),
                getMessage("TrdPlanRateInvestTrade001.finprodName"), 
                entity.getFinprodName(),
                getMessage("TrdPlanRateInvestTrade001.tradeDate"),
                DateUtil.format(entity.getTradeDate())
                ));  
        processParameter.setWorkFlowParameter(params);
        String msg = trdPlanRateInvestTrade002Service.start(processParameter, dtoList.toArray(new TrdPlanRateInvestTradeDto[]{}));
        return new AjaxResult(msg);
    }
    
    @ResponseBody
    @RequestMapping("/selectIndex")
    public String selectIndex() {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_利率型_存续期);
        String index=baseGetBusinessId.buildBussinessCode(codeParameter);
        return StringUtil.trimHtmlTag(index);
    }
    /**
     * 查询资产管理人 <功能详细描述>
     * 
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("selectFManeger")
    public Map<String, String> selectFManeger(@RequestBody Map<String, Object> param){
    	String finprodId=param.get("finprodId").toString();
        return trdPlanRateInvestTrade002Service.selectFManeger(finprodId);
    }
}
