/*
 * 文件名：TrdDepositInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年10月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.trade.trddepositinvesttrade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealMapper;
import com.joyintech.tams.common.base.dao.WflTrdProductDealMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.InterfSendOpics;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
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
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.trddepositinvesttrade.dto.TrdDepositInvestTrade001Dto;
import com.joyintech.tams.invest.trade.trddepositinvesttrade.service.TrdDepositInvestTrade002Service;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 存款类资产多组合交易新建
 * @author caojin
 * @version 1.0 2019年12月23日
 * @see TrdDepositInvestTrade001Controller
 * @since 1.0
 */
@Controller
@PageId("TrdDepositInvestTrade003")
@RequestMapping("/invest/trade/trddepositinvesttrade/TrdDepositInvestTrade003")
public class TrdDepositInvestTrade003Controller extends BaseController {
	@Autowired
	private TradeCommonService tradeCommonService;
    /**
     * 注入业务主键生产类
     */
    @Autowired(required=false)
    private BaseGetBusinessId baseGetBusinessId;
    /**
     * 划款指令
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    /**
     * 注入存款类交易Service
     */
    @Autowired
    private TrdDepositInvestTrade002Service trdDepositInvestTrade002Service;
    /**
     * 通用Mapper共同代理方法
     */
    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    
    @Autowired(required = false)
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;

    @Autowired(required = false)
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;
    
    /**
     * 初始化方法
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向,DictCode.计息基础,DictCode.是否,DictCode.存款类资产类型,DictCode.币种
        		,DictCode.行内资产二级分类,DictCode.行内资产三级分类,DictCode.行内资产四级分类,DictCode.分配方式);// 新增资产二、三、四级分类 王贵奇 2019/8/1
        model.addAttribute("buy",DictCodeList.交易方向_买入.getValue());
        model.addAttribute("YES",DictCodeList.是否_是.getValue());
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("FINPROD_TYPE_F07", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        model.addAttribute("PS_T01",DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02",DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("collectType_01", DictCodeListExt2.收取方式_回款前扣除.getValue());
        model.addAttribute("IS_CAL_LOADRATE", CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE));
        
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        
        TrdInvestPersonInfo002Dto dto = new TrdInvestPersonInfo002Dto();
        model.addAttribute("TrdInvestPersonInfo002Dto", JsonUtils.object2Json(dto));
        BussTrdTransferOrderDto bussTrdTransferOrderDto = new BussTrdTransferOrderDto();
        model.addAttribute("TrdInvestPersonInfo002Dto", JsonUtils.object2Json(bussTrdTransferOrderDto));
        String portfolioId = getParam("portfolioId");
        String entrustId = getParam("entrustId"); 
        String finprodId = getParam("finprodId");        
        String tradeId = getParam("tradeId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && workflowId != null && !workflowId.isEmpty()) {
            TrdDepositInvestTrade001Dto entity = trdDepositInvestTrade002Service.getBussinessEntityByWorkflowId(
                workflowId);
            model.addAttribute("finprodId",entity.getFinprodId());
            model.addAttribute("entity",entity);
            model.addAttribute("interfSendOpicsList",entity.getInterfSendOpicsList());
            if(entity.getBussTrdTransferOrder() != null && entity.getBussTrdTransferOrder().size()>0){
                model.addAttribute("stlPayOrdersList",JsonUtils.object2Json(entity.getBussTrdTransferOrder().get(0)));
            }
            model.addAttribute("workflowId",workflowId);
            model.addAttribute("trdInvestPersonInfo002Dto",JsonUtils.object2Json(entity.getTrdInvestPersonInfo002Dto()));
            model.addAttribute("riskTrialRetDto", JsonUtils.object2Json(entity.getRiskTrialRetDto()));
            model.addAttribute("tradeId", entity.getTradeId());
            // 判断逐渐是否为空
        }
        else if (StringUtil.isNotEmpty(tradeId)) {
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            if(bussCommonMapperDelegateService != null){
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDeal.setTradeId(tradeId);
                wflTrdProductDeal = bussCommonMapperDelegateService.selectByMultiPrimaryKey(WflTrdProductDealMapper.class, wflTrdProductDeal, WflTrdProductDeal.class);
            }
            if(wflTrdProductDeal != null){
                String workFlowId = wflTrdProductDeal.getWorkflowId();
                if(workFlowId != null && !workFlowId.isEmpty()){
                    TrdDepositInvestTrade001Dto entity = trdDepositInvestTrade002Service.getBussinessEntityByWorkflowId(
                        workFlowId);
                    model.addAttribute("finprodId",entity.getFinprodId());
                    model.addAttribute("entity",entity);
                    model.addAttribute("workflowId",workflowId);
                    model.addAttribute("interfSendOpicsList",entity.getInterfSendOpicsList());
                    model.addAttribute("riskTrialRetDto", JsonUtils.object2Json(entity.getRiskTrialRetDto()));
                    if(entity.getBussTrdTransferOrder() != null && entity.getBussTrdTransferOrder().size()>0){
                        model.addAttribute("stlPayOrdersList",JsonUtils.object2Json(entity.getBussTrdTransferOrder().get(0)));
                    }
                    model.addAttribute("trdInvestPersonInfo002Dto",JsonUtils.object2Json(entity.getTrdInvestPersonInfo002Dto()));
                    model.addAttribute("tradeId", workflowId);
                }
            }
            else {
                TrdProductDeal trdProductDeal = new TrdProductDeal();
                TrdProductDealAdd  trdProductDealAdd = new TrdProductDealAdd();
                if(bussCommonMapperDelegateService != null){
                    trdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(TrdProductDealMapper.class, tradeId, TrdProductDeal.class);
                    trdProductDealAdd = bussCommonMapperDelegateService.selectBySinglePrimaryKey(TrdProductDealAddMapper.class, tradeId, TrdProductDealAdd.class);
                }
                finprodId = trdProductDeal.getFinprodId();
                TrdDepositInvestTrade001Dto entity = trdDepositInvestTrade002Service.getFinInfo(finprodId);
                entity.setPrinAmt(trdProductDeal.getPrinAmt());
                entity.setTradeDate(trdProductDeal.getTradeDate());
                entity.setIntAmt(trdProductDeal.getIntAmt());
                entity.setPs(trdProductDeal.getPs());
                entity.setRemark(trdProductDeal.getRemark());
                // 通道交易 新增 强成西 2019/03/26 start
                entity.setTradeAmtAndFee(trdProductDeal.getTradeAmt());
                entity.setChlId(trdProductDeal.getChlId());
                entity.setChannelFee(trdProductDeal.getFeeAmtPay());
                // 通道交易 新增 强成西 2019/03/26 end
                entity.setLoadRate("");
                //entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(trdProductDealAdd.getPortfolioId()));
                FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, finprodId, FinProduct.class);
                entity.setFinprodMarketId(finProduct.getFinprodMarketId());
                entity.setEntrustId(trdProductDealAdd.getEntrustId());
                TrdInvestPersonInfo002Dto trdInvestPersonInfo002Dto = new TrdInvestPersonInfo002Dto();
                entity.setTrdInvestPersonInfo002Dto(trdInvestPersonInfo002Dto);
                createEntity(entity,portfolioId,trdProductDeal);
                StlPayOrdersDto stlPayOrdersList = null;
                if(trdDepositInvestTrade002Service.stlInfo(tradeId) != null){
                    stlPayOrdersList = trdDepositInvestTrade002Service.stlInfo(tradeId);
                    List<StlPayOrdersDto> stlPayOrdersLists = new ArrayList<>();
                    stlPayOrdersLists.add(stlPayOrdersList);
                    entity.setStlPayOrdersList(stlPayOrdersLists);
                };
                List<InterfSendOpics> interfSendOpicsList = trdDepositInvestTrade002Service.getOpics(tradeId.substring(0,18));
                if(interfSendOpicsList != null && interfSendOpicsList.size() > 1){
                    for(int i = 0; i<2; i++){
                        if(!interfSendOpicsList.get(i).getVdate().isEmpty() && interfSendOpicsList.get(i).getVdate().length()==8){
                            String vDate = interfSendOpicsList.get(i).getVdate();
                            String temp = vDate.substring(0, 4) + "-" +vDate.substring(4,6) + "-" + vDate.substring(6,8);
                            interfSendOpicsList.get(i).setVdate(temp);
                        }
                        if(!interfSendOpicsList.get(i).getMdate().isEmpty() && interfSendOpicsList.get(i).getMdate().length()==8){
                            String mDate = interfSendOpicsList.get(i).getMdate();
                            String temp = mDate.substring(0, 4) + "-" +mDate.substring(4,6) + "-" + mDate.substring(6,8);
                            interfSendOpicsList.get(i).setMdate(temp);
                        }
                        if(!interfSendOpicsList.get(i).getDealdate().isEmpty() && interfSendOpicsList.get(i).getDealdate().length()==8){
                            String dealdate = interfSendOpicsList.get(i).getDealdate();
                            String temp = dealdate.substring(0, 4) + "-" +dealdate.substring(4,6) + "-" + dealdate.substring(6,8);
                            interfSendOpicsList.get(i).setDealdate(temp);
                        }
                    }
                    model.addAttribute("interfSendOpicsList",interfSendOpicsList);
                }
                model.addAttribute("finprodId",entity.getFinprodId());
                entity.setTradeId(tradeId.substring(0,18));
                model.addAttribute("entity",entity);
//                model.addAttribute("stlPayOrdersList",JsonUtils.object2Json(stlPayOrdersList));
                model.addAttribute("trdInvestPersonInfo002Dto",JsonUtils.object2Json(entity.getTrdInvestPersonInfo002Dto()));
                model.addAttribute("tradeId", tradeId);
            }
        }else if(entrustId != null){

            TrdDepositInvestTrade001Dto entity = new TrdDepositInvestTrade001Dto();

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
            
            model.addAttribute("BussTrdTransfenpeiDto", list);
            model.addAttribute("finprodId", finprodId);
            model.addAttribute("entity", entity);
            model.addAttribute("tradeId", entrustId);
        }
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
     * 获取资产提供行
     * @return 资产提供行下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getAssetProvider")
    public List<SelectOptionDto> getAssetProvider(){
        Map<String,Object> map = RequestUtil.getPageParam();
        map.put("counterType", DictCodeListExt1.交易对手类型_分行.getValue());
        if(StringUtil.isNotEmpty(map.get("finprodMarketId"))){
            map.put("finprodName","F07_"+map.get("finprodMarketId"));
            return trdDepositInvestTrade002Service.getAssetProvider(map);
        }else{
            return null;
        }
    }
    /**
     * 由资产代码带出画面表单的内容
     * @return 存款类投资交易实体类
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getFinInfo")
    public TrdDepositInvestTrade001Dto getFinInfo(){
        String finprodId = getParam("finprodId");
        return trdDepositInvestTrade002Service.getFinInfo(finprodId);
    }
    /**
     * 交易日
     * @return 交易日
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getInitDate")
    public String getInitDate(){
        return iPlatformDate.getSysDateStr();
    }
    /**
     * 获取opics
     * @param dto 存款实体类
     * @return opics实体类
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getOpics")
    public List<Map<String,Object>> getOpics(@RequestBody TrdDepositInvestTrade001Dto dto){
        List<Map<String,Object>> list= new ArrayList<>();
        for(int i = 0; i<2;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("trad", LoginUserUtil.getUserName());
            map.put("mdate", dto.getMdate());
            map.put("vdate", dto.getVdate());
            map.put("amt", dto.getPrinAmt());
            map.put("branch", "06");
            map.put("brok", "D");
            map.put("sendflag", "Y");
            map.put("ccy", dto.getCcy());
            map.put("basis", dto.getBasis());
            map.put("spread", "0");
            map.put("dealdate", dto.getTradeDate());
            //map.put("sendflag", "Y");
            map.put("intrate", dto.getLoadRate());
            list.add(map);
        }
        
        if(dto.getFinprodType2() == null || dto.getFinprodType2().equals(DictCodeList.业务编号一级分类_存放同业)){
          list.get(0).put("verind", "Y");
          list.get(1).put("verind", "Y");
          list.get(0).put("dealtype", "PL");
          list.get(1).put("dealtype", "DE");
          list.get(0).put("port", "DEPO");
          list.get(1).put("port", "DEAG");
          list.get(0).put("branch", "06");
          list.get(1).put("branch", "04");
          list.get(0).put("dealtext", "TCMS DEPO");
          list.get(1).put("dealtext", "TCMS DEPO");
        }else{
          list.get(0).put("verind", "N");
          list.get(1).put("verind", "N");
          list.get(0).put("port", "LOAN");
          list.get(1).put("port", "LOAN");
          list.get(0).put("branch", "06");
          list.get(1).put("branch", "06");
          list.get(0).put("dealtext", "TCMS BORW");
          list.get(1).put("dealtext", "TCMS BORW");
          list.get(0).put("dealtype", "");
      }
        return list;
    }
    /**
     * 查看是否保本
     * @return 是否
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getIsGuarantee")
    public String getIsGuarantee(){
        String portfolioId = getParam("portfolioId");
        return StringUtil.trimHtmlTag(trdDepositInvestTrade002Service.getIsGuarantee(portfolioId));
    }
    /**
     * 获取持仓、可用现金等
     * @return 金额集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("available")
    public List<SuggestDto> available(){
        Map<String,Object> map = RequestUtil.getPageParam();
        List<SuggestDto> list = trdDepositInvestTrade002Service.available(map);
        return list;
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
        TrdDepositInvestTrade001Dto dto = BeanUtil.mapToObject(map, TrdDepositInvestTrade001Dto.class);
        TrdInvestPersonInfo002Dto trdInvestPersonInfo002Dto = new TrdInvestPersonInfo002Dto();
        dto.setTrdInvestPersonInfo002Dto(trdInvestPersonInfo002Dto);
        dto.getTrdInvestPersonInfo002Dto().setContactInfo(map.get("trdInvestPersonInfo002Dto[contactInfo]").toString());
        dto.getTrdInvestPersonInfo002Dto().setCounterId(map.get("trdInvestPersonInfo002Dto[counterId]").toString());
        dto.getTrdInvestPersonInfo002Dto().setCounterTrader(map.get("trdInvestPersonInfo002Dto[counterTrader]").toString());
        dto.getTrdInvestPersonInfo002Dto().setIsOutTrade(map.get("trdInvestPersonInfo002Dto[isOutTrade]").toString());
        dto.getTrdInvestPersonInfo002Dto().setTrader(map.get("trdInvestPersonInfo002Dto[trader]").toString());
        return trdDepositInvestTrade002Service.getEntity(dto);
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
        ValidateGroupEdit.class}) TrdDepositInvestTrade001Dto dto, BindingResult result) {
    	//有check错误直接返回
    	if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
    	//huwenbin 指令接收交易录单验证数据已存在流程中
       /* if(StringUtil.isNotEmpty(dto.getEntrustId()) && WorkFlowUtil.isNotFromToDoList()){
    		String str = tradeCommonService.check(dto.getEntrustId(), this.getPageId());
    		if(StringUtil.isNotEmpty(str)){
    			AjaxResult res= new AjaxResult();
				res.setMessage(str, MessageType.错误消息);
    			return res;
    		}
        	// 查询交易流程中录入的总金额	王贵奇	2019/9/3
        	BigDecimal entrustNum = tradeCommonService.getEntrustAmtByEntrustId(dto.getEntrustId());
            BigDecimal wflPrinAmt = tradeCommonService.getWflPrinAmt(dto.getEntrustId(), dto.getPortfolioId());
            if(entrustNum.compareTo(wflPrinAmt.add(dto.getPrinAmt())) == -1) {
            	AjaxResult res= new AjaxResult();
            	 List<ValidMessage> errorList = new ArrayList<>();
            	 ValidMessage validMessage = new ValidMessage("prinAmt",
            			 "可用委托数量不足,当前最多可委托数量为:"+ entrustNum.subtract(wflPrinAmt));
                     errorList.add(validMessage);
                res.setErrorList(errorList);
                res.setMessageType(MessageType.错误消息);
                return res;
            }
        	
    	}*/
        return commonCheck(result, dto.getOperateType());
    }
    /**
     * 获取利率总额
     * @param map 本金、利率
     * @return 利率总额
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getIntAmt")
    public String getIntAmt(@RequestBody Map<String,Object> map){
        return trdDepositInvestTrade002Service.getIntAmt(map);
    }
    
    /**
     * 获取资金代码
     * @return 联想下拉框实体类集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getFinprodMarketId")
    public List<SelectOptionDto> getFinprodMarketId(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return trdDepositInvestTrade002Service.getFinprodMarketId2(map);
    }
    
    /**
     * 授权额度检查
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("checkPrinAmt")
    public Map<String,Object>  checkprinAmt(@RequestBody Map<String,Object> map){
    	Map<String,Object> result = new HashMap<String,Object>();
    	List prinAmtList=(List)map.get("prinAmtData");
        String counterId = (String)map.get("counterId");
        String 平安资产类型 = DictCodeList.平安资产类型_存款.getValue();
        String windtypeone = "D01";
        String tradeMarket = (String)map.get("tradeMarket");
        String ps = (String)map.get("ps");
        String finprodId = (String)map.get("finprodId");
    	// 授权额度检查（当本金金额高于副总裁/总裁助理相应资产的授权额度时，提示消息）
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        for(int i=0;i<prinAmtList.size();i++){
        	Map<String, Object> params = CommonBusinessService.setParams(counterId, new BigDecimal((String)prinAmtList.get(i)), LoginUserUtil.getUserName(),
                    平安资产类型, windtypeone, tradeMarket, ps, finprodId, null);
	        if (commonBusinessService != null && commonBusinessService.是否超过副总额额度(params)) {
	            // 交易本金金额超过副总裁/总裁助理授权额度，无法发起交易!
	        	result.put("flag", "false");
	        	return result;
	        }
        }
        return result;
    }
    
    /**
     * 合成存款类投资交易实体类
     * @param dto 交易对手实体类
     * @param portfolioId 组合id
     * @return 投资交易实体类
     * @see
     * @since 1.0
     */
    public TrdDepositInvestTrade001Dto createEntity(TrdDepositInvestTrade001Dto dto,String portfolioId,TrdProductDeal dealDto){
        dto.getTrdInvestPersonInfo002Dto().setCounterId(dealDto.getCounterId());
        dto.getTrdInvestPersonInfo002Dto().setTrader(dealDto.getTrader());
        dto.getTrdInvestPersonInfo002Dto().setContactInfo(dealDto.getCounterContact());
        dto.getTrdInvestPersonInfo002Dto().setCounterTrader(dealDto.getCounterTrader());
        dto.getTrdInvestPersonInfo002Dto().setIsOutTrade(dealDto.getIsOutTrade());
        dto.getTrdInvestPersonInfo002Dto().setTraderDept(dealDto.getCreateDept());
        dto.setPortfolioId(portfolioId);
        if(dealDto.getCounterTrader() == null || dealDto.getCounterTrader().isEmpty()){
            TrdProductDeal temp = bussCommonMapperDelegateService.selectBySinglePrimaryKey(TrdProductDealMapper.class, dealDto.getTradeId().substring(0,18), TrdProductDeal.class);
            dto.getTrdInvestPersonInfo002Dto().setCounterId(temp.getCounterId());
            dto.getTrdInvestPersonInfo002Dto().setTrader(temp.getTrader());
            dto.getTrdInvestPersonInfo002Dto().setContactInfo(temp.getCounterContact());
            dto.getTrdInvestPersonInfo002Dto().setCounterTrader(temp.getCounterTrader());
            dto.getTrdInvestPersonInfo002Dto().setIsOutTrade(temp.getIsOutTrade());
            dto.getTrdInvestPersonInfo002Dto().setTraderDept(temp.getCreateDept());
        }
        return dto;
    }
    
    /**
     * 获取组合代码
     * @return 联想文本框
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getPortfolioId")
    public List<SuggestDto> getPortfolioId(){
        Map<String,Object> map = RequestUtil.getPageParam();
        map.put("userId",SecurityUtil.getLoginUser().getUid());
        return trdDepositInvestTrade002Service.getPortfolioId(map);
    }
    /**
     * 获取opics下拉框
     * @return 下拉框
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getSelectOptions")
    public List<SelectOptionDto> getSelectOptions(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return trdDepositInvestTrade002Service.getSelectOptions(map);
    }
    
    @ResponseBody
    @RequestMapping("/selectIndex")
    public String selectIndex() {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_存款录入);
        String index=baseGetBusinessId.buildBussinessCode(codeParameter);
        return StringUtil.trimHtmlTag(index);
    }
    
    /**
     * 保存
     * @param dto dto
     * @return 结果
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody TrdDepositInvestTrade001Dto dto) throws InvocationTargetException, IllegalAccessException, WorkFlowException, IOException, InstantiationException {
       
    	List<TrdDepositInvestTrade001Dto> dtoList = new ArrayList<>();
    	List<BussTrdTransfenpeiDto> fenpeiList = dto.getBussTrdTransfenpeiDto();//组合分配
    	List<BussTrdTransferOrderDto> orderList = dto.getBussTrdTransferOrder();//划款指令
    	for(BussTrdTransferOrderDto bussTrdTransferOrder : orderList) {    		
    		TrdDepositInvestTrade001Dto tempDto = new TrdDepositInvestTrade001Dto();
    		BeanUtil.copyProperties(tempDto, dto);
    		List<BussTrdTransferOrderDto> tmpOrderList = new ArrayList<BussTrdTransferOrderDto>();
    		if(fenpeiList != null && fenpeiList.size()>0){
                for (int i = 0; i < fenpeiList.size(); i++) {
                    BussTrdTransfenpeiDto fenpei = fenpeiList.get(i);
    				if (StringUtils.equals(bussTrdTransferOrder.getPortfolioId(), fenpei.getPortfolioId()) && fenpei.getPrinAmt().compareTo(new BigDecimal(0))==1) {
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
				        if(tempDto.getTradeId() == null || tempDto.getTradeId().isEmpty() || tempDto.getTradeId().length() > 25){
				            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter(); 
				            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_存款录入);
				            if(DictCodeList.存款类资产类型_同业借款.getValue().equals(tempDto.getFinprodType())){
				                bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_同业借款);
				            }else{
				                bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_存放同业);
				            }
				            tempDto.setTradeId((baseGetBusinessId != null && baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) != null)? baseGetBusinessId.buildBussinessCode(bussinessCodeParameter):IDGenerator.generate().substring(0, 30));
				            if(tempDto.getTradeId().length()>31){
				            	tempDto.setTradeId(dto.getTradeId().substring(0,29));
				            }
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
                }
    		}else{
    			BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter(); 
	            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_存款录入);
	            if(DictCodeList.存款类资产类型_同业借款.getValue().equals(tempDto.getFinprodType())){
	                bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_同业借款);
	            }else{
	                bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_存放同业);
	            }
	            tempDto.setTradeId((baseGetBusinessId != null && baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) != null)? baseGetBusinessId.buildBussinessCode(bussinessCodeParameter):IDGenerator.generate().substring(0, 30));
	            if(dto.getTradeId().length()>31){
	            	tempDto.setTradeId(dto.getTradeId().substring(0,29));
	            }
    			dtoList.add(tempDto);
    		}
    	}
        
        
        ProcessParameter<TrdDepositInvestTrade001Dto> processParameter = new ProcessParameter<TrdDepositInvestTrade001Dto>();
        WorkFlowParameter<TrdDepositInvestTrade001Dto> params = new WorkFlowParameter<TrdDepositInvestTrade001Dto>();
        params.setPageId("TrdDepositInvestTrade002"); 
        //----------add by jjpei 20200114 核风险版本 start----------
        if(dto.getOperateType().equals(BussinessConstant.SUBMIT)&& CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)){
            AjaxResult ar =interfRiskRdsService.checkBatchRiskRds(dtoList,new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
                    BussinessDictCodeListRds.接口系统ID_实占接口.getValue(), BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(),DictCodeList.平安资产类型_存款.getValue(),
                    (sm,en)->interfRiskRdsService.sendRiskRdsData(sm,en));
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        //----------add by jjpei 20200114 核风险版本 end---------- 
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getTradeId());
        params.setBusinessType(DictCodeList.共通业务类型_存款类资产交易.getValue());
        // 业务说明：“交易编号：” + 上述取得的交易编号+“资产名称：”+画面.资产名称+“交易日：”+画面.交易日 
        params.setComment(
            entity -> this.getMessage("TrdPlanRateInvestTrade001.tradeId") + "("
                      + entity.getTradeId() + ")" + "-"
                      + this.getMessage("TrdPlanRateInvestTrade001.finprodName") + "("
                      + entity.getFinprodName() + ")" 
                      + this.getMessage("TrdPlanRateInvestTrade001.tradeDate") + "("
                          + DateUtil.format(entity.getTradeDate()) + ")" );
        processParameter.setWorkFlowParameter(params);
        String msg = trdDepositInvestTrade002Service.start(processParameter, dtoList.toArray(new TrdDepositInvestTrade001Dto[]{}));
        return new AjaxResult(msg);
    }

    /**
     * 查询剩余融资金额
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getIssueAmt")
    public AjaxResult getIssueAmt(@RequestBody Map<String,Object> map){
        // 调用基础组件.【组合可用现金和实际现金查询更新组件.查询持仓(可用)】
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tradeDate", (String) map.get("tradeDate"));
        paramMap.put("ccy", (String)map.get("ccy"));
        //  paramMap.put("calendarId", dto.getCalendarId());
        paramMap.put("finprodId", (String) map.get("finprodId"));
        //考虑流程中被占用,是的话
        paramMap.put("isContainWfl",DictCodeListExt1.是或否_是.getValue());
        //获得资产维度的持仓
        Map<String, Object>  resultMap = bussTrdAvailableCashQueryService.getFinAvaPosition(paramMap);

        //融资金额
        BigDecimal issueAmt = new BigDecimal((String)resultMap.get("ISSUEAMT"));
        //已融资金额
        BigDecimal position = new BigDecimal((String) resultMap.get("POSITION"));
        //流程中占用金额
        BigDecimal positionFlow =null;
        if(StringUtil.isNotEmpty(resultMap.get("POSITIONFLOW"))){
            positionFlow=new BigDecimal((String) resultMap.get("POSITIONFLOW"));
        }
        position=ArithmeticUtil.doAdd(position,positionFlow);
        AjaxResult result = new AjaxResult();
        BigDecimal resultAmt = issueAmt.subtract(position);
        result.setData(resultAmt);
        return  result;
    }
}
