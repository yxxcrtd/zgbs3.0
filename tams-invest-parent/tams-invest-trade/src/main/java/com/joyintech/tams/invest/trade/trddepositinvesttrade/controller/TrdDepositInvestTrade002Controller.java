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
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.trade.common.aop.TradeRdsCommonAop;
import com.joyintech.tams.invest.trade.common.controller.TrdCommonController;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.trddepositinvesttrade.dto.TrdDepositInvestTrade001Dto;
import com.joyintech.tams.invest.trade.trddepositinvesttrade.service.TrdDepositInvestTrade002Service;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 存款交易新建
 * @author fanshuanghu
 * @version 1.0 2018年10月10日
 * @see TrdDepositInvestTrade001Controller
 * @since 1.0
 */
@Controller
@PageId("TrdDepositInvestTrade002")
@RequestMapping("/invest/trade/trddepositinvesttrade/TrdDepositInvestTrade002")
public class TrdDepositInvestTrade002Controller extends BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

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
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;
    
 	@Autowired(required = false)
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;
 	
 	@Autowired
    private TrdCommonController trdCommonController;
 	
    @Value("${flag:false}")
    private boolean flag;
    /**
     * 初始化方法
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向,DictCode.计息基础,DictCode.是否,DictCode.存款类资产类型,DictCode.币种
        		,DictCode.行内资产二级分类,DictCode.行内资产三级分类,DictCode.行内资产四级分类);// 新增资产二、三、四级分类 王贵奇 2019/8/1
        model.addAttribute("buy",DictCodeList.交易方向_买入.getValue());
        model.addAttribute("YES",DictCodeList.是否_是.getValue());
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("FINPROD_TYPE_F07", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        model.addAttribute("PS_T01",DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02",DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("collectType_01", DictCodeListExt2.收取方式_回款前扣除.getValue());
        model.addAttribute("IS_CAL_LOADRATE", CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE));
        
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        model.addAttribute("TRADESTATU03","");
        model.addAttribute("PTRADEID","");

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
                    model.addAttribute("workflowId",workFlowId);
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

                if(trdProductDeal!=null && StringUtils.equals(trdProductDeal.getTradeStatus(),DictCodeListExt1.交易状态_撤销.getValue())){
                    // 如果为撤销
                    model.addAttribute("TRADESTATU03",DictCodeListExt1.交易状态_撤销.getValue());
                    model.addAttribute("PTRADEID",trdProductDeal.getpTradeId());
                }

                entity.setPrinAmt(trdProductDeal.getPrinAmt());
                entity.setTradeDate(trdProductDeal.getTradeDate());
                entity.setIntAmt(trdProductDeal.getIntAmt());
                entity.setPs(trdProductDeal.getPs());
                entity.setRemark(trdProductDeal.getRemark());
                entity.setIsClean(trdProductDeal.getIsClean());
                // 通道交易 新增 强成西 2019/03/26 start
                entity.setTradeAmtAndFee(trdProductDeal.getTradeAmt());
                entity.setChlId(trdProductDeal.getChlId());
                
                TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
                trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
                if(bussCommonMapperDelegateService != null){
                	trdTradeFeeDetail = bussCommonMapperDelegateService.selectByMultiPrimaryKey(TrdTradeFeeDetailMapper.class, trdTradeFeeDetail, TrdTradeFeeDetail.class);
                }
                if(StringUtil.isNotEmpty(trdTradeFeeDetail)){
                	entity.setChannelFee(trdTradeFeeDetail.getFeeAmt());
                }
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

                // 获取workflowId 加载风险试算结果
                TrdDepositInvestTrade001Dto dto1 = new TrdDepositInvestTrade001Dto();
                dto1.setTradeId(tradeId);
                ProcessMark processMark = WorkflowContext.getProcessMarkService().getParameterByAnnotation(dto1);
                //转换当前页面
                processMark.setPageId("TrdDepositInvestTrade002");
                String workflowId_T = WorkflowContext.getProcessMarkService().getLastWorkflowId(processMark);
                model.addAttribute("workflowId", workflowId_T);
            }
        }else if(entrustId != null){
            FinProduct finProduct = null;
            String trader = "";
            EtrEntrust etrEntrust = trdDepositInvestTrade002Service.getEtrInfo(entrustId);
            TrdInvestPersonInfo002Dto trdInvestPersonInfo002Dto = new TrdInvestPersonInfo002Dto();
            portfolioId = etrEntrust.getPortfolioId();
            finprodId = etrEntrust.getFinprodId();
            TrdDepositInvestTrade001Dto entity = trdDepositInvestTrade002Service.getFinInfo(finprodId);
            if(bussCommonMapperDelegateService != null){
                finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, finprodId, FinProduct.class);
                trader= trdDepositInvestTrade002Service.getTrader(entrustId);
            }
            if(entity != null){
                entity.setPrinAmt(etrEntrust.getEntrustAmt());
                entity.setPortfolioId(portfolioId);
                entity.setFinprodId(finprodId);
                if(finProduct != null){
                    entity.setFinprodMarketId(finProduct.getFinprodMarketId());
                }
                if(trader != null && !"".equals(trader)){
                    trdInvestPersonInfo002Dto.setTrader(trader);
                }
                entity.setEntrustId(entrustId);
                entity.setTrdInvestPersonInfo002Dto(trdInvestPersonInfo002Dto);
                trdInvestPersonInfo002Dto.setCounterId(etrEntrust.getCounterId());
                trdInvestPersonInfo002Dto.setCounterTrader(etrEntrust.getCounterTrader());
                trdInvestPersonInfo002Dto.setIsOutTrade(etrEntrust.getIsOutTrade());
                trdInvestPersonInfo002Dto.setContactInfo(etrEntrust.getContactInfo());
                entity.setTrdInvestPersonInfo002Dto(trdInvestPersonInfo002Dto);
                entity.setPs(etrEntrust.getPs());
                model.addAttribute("trdInvestPersonInfo002Dto", JsonUtils.object2Json(entity.getTrdInvestPersonInfo002Dto()));
            }
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
        //查看交易方向添加清理核销 add by yzg 20201102 start
        if(StringUtil.equals(this.getParam("pageModel"), GlobalConstant.DETAIL_PAGE_MODEL_REFERENCE)) {
            if(StringUtil.equals(CacheUtil.getSysParam("IS_QUERY_ASSETS_LOSS"), "01")) {
                SelectDto selectDto = getSelectDataFromDataDic(DictCode.交易方向,null);
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(DictCodeListExt3.交易方向_清理核销.getValue());
                selectOptionDto.setText(DictCodeListExt3.交易方向_清理核销.getCodeName());
                selectOptionDto.setCol(DictCode.交易方向.getValue());
                selectDto.getOptions().add(selectOptionDto);
                model.addAttribute(DictCode.交易方向.getValue(), JsonUtils.objectArray2Json(selectDto.getOptions().toArray()));
            }
        }
        //查看交易方向添加个清理核销 add by yzg 20201102 end
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
        if(StringUtil.isNotEmpty(map.get("finprodName"))){
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
        return DateUtil.format(DateUtil.platDateFromCache());
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
        AjaxResult ajaxResult = new AjaxResult();
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
        //外部交易对手买入，校验持仓+本金 < 融资金额或者发行金额
        if (!StringUtil.equals(dto.getFinprodType(), DictCodeListExt3.存款类资产类型_活期存放.getValue()) &&
                StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()) &&
                StringUtil.equals(dto.getTrdInvestPersonInfo002Dto().getIsOutTrade(), "S")){
            // 调用基础组件.【组合可用现金和实际现金查询更新组件.查询持仓(可用)】
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("portfolioId", dto.getPortfolioId());
            paramMap.put("tradeDate", DateUtil.format(dto.getTradeDate()));
            paramMap.put("ccy", dto.getCcy());
          //  paramMap.put("calendarId", dto.getCalendarId());
            paramMap.put("chlId", dto.getChlId());
            paramMap.put("finprodId", dto.getFinprodId());
            //考虑流程中被占用,是的情况
            paramMap.put("isContainWfl",DictCodeListExt1.是或否_是.getValue());
            //针对，流程中数据退回，然后又重新提交时，流程中占有应不包含该数据
            paramMap.put("isnotContainTrds",dto.getTradeId());
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
            position = ArithmeticUtil.doAdd(position, dto.getPrinAmt());
            //资产 融资金额、发行金额
            if (issueAmt.compareTo(position) < 0) {
                ajaxResult.setData(getMessage("TRD_C_0005"));
                return ajaxResult;
            }
        }
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
        map.put("platDate",DateUtil.format(DateUtil.platDateFromCache()));
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("cashType2",DictCodeListExt1.现金流二级类型_利息.getValue());

        return trdDepositInvestTrade002Service.getFinprodMarketId2(map);
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
    /**
     * 保存
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) TrdDepositInvestTrade001Dto dto, BindingResult br) throws Exception {
    	if(flag) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter(); 
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_存款录入);
            if(DictCodeList.存款类资产类型_同业借款.getValue().equals(dto.getFinprodType())){
                bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_同业借款);
            }else{
                bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_存放同业);
            }
            dto.setTradeId((baseGetBusinessId != null && baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) != null)? baseGetBusinessId.buildBussinessCode(bussinessCodeParameter):IDGenerator.generate().substring(0, 30));
            if(dto.getTradeId().length()>31){
                dto.setTradeId(dto.getTradeId().substring(0,29));
            }
            trdDepositInvestTrade002Service.fastSave(dto);
            return new AjaxResult("成功");
    	}
    	
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        
        ProcessParameter<TrdDepositInvestTrade001Dto> processParameter = new ProcessParameter<TrdDepositInvestTrade001Dto>();
        WorkFlowParameter<TrdDepositInvestTrade001Dto> params = new WorkFlowParameter<TrdDepositInvestTrade001Dto>();
        params.setPageId("TrdDepositInvestTrade002");
        if(dto.getTradeId() == null || dto.getTradeId().isEmpty() || dto.getTradeId().length() > 25){
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter(); 
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_存款录入);
            if(DictCodeList.存款类资产类型_同业借款.getValue().equals(dto.getFinprodType())){
                bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_同业借款);
            }else{
                bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_存放同业);
            }
            dto.setTradeId((baseGetBusinessId != null && baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) != null)? baseGetBusinessId.buildBussinessCode(bussinessCodeParameter):IDGenerator.generate().substring(0, 30));
            if(dto.getTradeId().length()>31){
                dto.setTradeId(dto.getTradeId().substring(0,29));
            }
//            dto.setTradeId(IDGenerator.generate().substring(0, 31));
        }
        if(dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0 && dto.getBussTrdTransferOrder().get(0) != null && !dto.getBussTrdTransferOrder().get(0).toString().isEmpty()){
            dto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, dto.getBussTrdTransferOrder().get(0));
            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
            list.add(stlPayOrdersDto);
            dto.setStlPayOrdersList(list);
        }
        if (CollectionUtils.isNotEmpty(dto.getFileArray())
            && StringUtil.isNotEmpty(dto.getTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                dto.getFileArray().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                dto.getTradeId());
        }
        //----------add by jjpei 20200114 核风险版本 start----------
        if(dto.getOperateType().equals(BussinessConstant.SUBMIT)&& CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)){
            AjaxResult ar=interfRiskRdsService.checkRiskRds(new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
                    BussinessDictCodeListRds.接口系统ID_实占接口.getValue(),DictCodeList.平安资产类型_存款.getValue(),
                    (en)-> {
                        try {
                            return interfRiskRdsService.sendRiskRdsData(dto,en);
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                            throw new IOException();
                        } catch (InstantiationException e) {
                            logger.error(e.getMessage(), e);
                            throw new InstantiationException();
                        }
                    });
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
            // add by zhangrui1 start
            // 将实占信息设置到线程缓存出错时撤销用
            TradeRdsCommonAop.setLocalCache(dto);
            // add by zhangrui1 end
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
        String msg = trdDepositInvestTrade002Service.start(processParameter, dto);
        
        //授信占用额度
//        trdCommonController.delLinesDeal(dto.getRiskCreditListCommonDto(),dto.getTradeId());
        return new AjaxResult(msg);
    }

    /**
     * 获取当日应记利息
     * @param map 请求参数
     * @return 当日应记利息
     */
    @ResponseBody
    @RequestMapping("getTdyIntincexp")
    public BigDecimal getTdyIntincexp(@RequestBody Map<String,Object> map){
        return trdDepositInvestTrade002Service.getTdyIntincexp(map);
    }
}
