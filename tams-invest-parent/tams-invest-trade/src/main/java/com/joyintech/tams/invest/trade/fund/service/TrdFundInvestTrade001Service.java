/*
 * 文件名：TrdFundInvestTradeService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年10月19日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.trade.fund.service;

import com.google.common.collect.Lists;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.common.base.dao.MstCounterPartyMapper;
import com.joyintech.tams.common.base.entity.MstCounterParty;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.settle.BussSettleOrderCommService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.trade.fund.dto.TrdFundInvestTradeConfirmDto;
import com.joyintech.tams.invest.trade.fund.dto.TrdFundInvestTradeDto;
import com.joyintech.tams.invest.trade.transfer.aop.RdsTransferAop;
import com.joyintech.tams.position.servive.PositionRequestHandle;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基金投资交易画面端service001
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2018年10月19日
 * @see TrdFundInvestTrade001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class TrdFundInvestTrade001Service extends CommonExportService<TrdFundInvestTradeDto> implements BaseProcessService<TrdFundInvestTradeDto> {

    private static final Logger logger = LoggerFactory.getLogger(TrdFundInvestTrade001Service.class);
    /**
     * 划款指令业务组件service
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    /**
     * 清算指令共通Service
     */
    @Autowired(required = false)
    private BussSettleOrderCommService bussSettleOrderCommService;
    /**
     * 交易产品接口
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;
    
    @Autowired
    private TrdFundInvestTrade003Service trdFundInvestTrade003Service;
    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    @Autowired
    private TrdFundInvestTrade002Service trdFundInvestTrade002Service;

    @Autowired
    private PositionRequestHandle positionRequestHandle;

    // 调用投管接口 add by renping 20190205 start
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    // 调用投管接口 add by renping 20190205 end
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private MstCounterPartyMapper mstCounterPartyMapper;

    /**
     * 
     * 分页查询
     * <功能详细描述>
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdFundInvestTradeDto> findPage(Map<String, Object> pageParam) {
        pageParam.put("loginUser", SecurityUtil.getLoginUser().getUserName());
        // 新增资产二、三、四级分类	王贵奇	2019/7/30 -开始
        if (StringUtil.isNotEmpty(pageParam.get("type9"))) {
            pageParam.put("type9", StringUtil.splitToList(null, pageParam.get("type9").toString()));
        }else{
            pageParam.remove("type9");
        }
        if (StringUtil.isNotEmpty(pageParam.get("type10"))) {
            pageParam.put("type10", StringUtil.splitToList(null, pageParam.get("type10").toString()));
        }else{
            pageParam.remove("type10");
        }
        if (StringUtil.isNotEmpty(pageParam.get("type11"))) {
            pageParam.put("type11", StringUtil.splitToList(null, pageParam.get("type11").toString()));
        }else{
            pageParam.remove("type11");
        }
        if (StringUtil.isNotEmpty(pageParam.get("portfolioId"))) {
            pageParam.put("portfolioId", StringUtil.splitToList(null, pageParam.get("portfolioId").toString()));
        }
        //多选下拉框处理（合作资产）
        Object chlFinprodIdObj = pageParam.get("chlFinprodId");
        if (chlFinprodIdObj != null){
            pageParam.put("chlFinprodIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA,(String) chlFinprodIdObj));
        }else {
            pageParam.put("chlFinprodIdList",new ArrayList<>());
        }
        // 新增资产二、三、四级分类	王贵奇	2019/7/30 -结束
        Page<TrdFundInvestTradeDto> res =  restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/findPage",
            pageParam,TrdFundInvestTradeDto.class);
        res.getRoot().forEach(fundTrade -> {
            if(fundTrade.getFlg().equals("01")&&!SecurityUtil.isPermitted("TrdFundInvestTrade001_TBLROW_edit")){
                fundTrade.setFlg("02");
            }
        });
        return res;
    }

    /**
     * 撤销审批完成操作
     */
    @Override
    public void doComplete(TrdFundInvestTradeDto entity, OperationType type) {
        entity.setOperateType(type.toString());
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        List<BaseTradeProductDealDto> dtoList = restTemplate.getForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/getFinDto?tradId="+
            entity.getTradeId(),BaseTradeProductDealDto.class);
        // 提前查询判断  是否  撤销 并且是 有转仓交易 并且转仓交易方向ps为买入
        String reverseTradeId = "";
        if(type == OperationType.REVOKE) {
            TrdProductDeal reverseTrdProductDeal = trdCommonBaseService.selectZCDealByTid(entity.getTradeId(),false);
            if (reverseTrdProductDeal != null && StringUtil.equals(DictCodeList.交易方向_买入.getValue(), reverseTrdProductDeal.getPs())) {
                reverseTradeId = reverseTrdProductDeal.getTradeId();
                RdsTransferAop.releaseOccupy(entity.getTradeId(), iPlatformDate.getSysDateStr());
            }
        }
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        for(BaseTradeProductDealDto btpddto:dtoList){
            btpddto.getTrdProductDeal().setIsCancel(DictCodeList.是否_是.getValue());
            btpddto.getTrdProductDeal().setCancelRemark(entity.getCancelRemark());
            //获取划款指令
            if(StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), entity.getPs())){
                List<BussTrdTransferOrderBase> bttob = bussSettleOrderCommService.selectOrdersByBizCode(entity.getbTradeId());
                if(bttob!=null && bttob.size()>0){
                    List<BussTrdTransferOrderDto> bttodList = new ArrayList<>();
                    BussTrdTransferOrderDto bttod = new BussTrdTransferOrderDto();
                    BeanUtil.copyPropertiesIgnoreNull(bttod, bttob.get(0));
                    bttodList.add(bttod);
                    List<StlPayOrdersDto> spod = new ArrayList<>();
                    spod.add(bussTrdTransferOrderService.save(bttodList).get(0));
                    btpddto.setStlPayOrdersList(spod);
                }
            }else if(StringUtil.equals(DictCodeList.交易方向_赎回确认.getValue(), entity.getPs())){
                Map<String, Object> accountTradId = new HashMap<>();
                Map<String, Object> param = new HashMap<>();
                param.put("tradeId", entity.getTradeId());
                accountTradId = getAccountId(param);
                if(accountTradId == null){
                    accountTradId = selectAccountTrdIdinwfl(entity.getTradeId());
                }
              List<BussTrdTransferOrderBase> bttob = new ArrayList<>();
              if(accountTradId != null){
                  if(StringUtil.equals((String)accountTradId.get("WORKFLOWID"), "0")){
                      bttob = trdFundInvestTrade003Service.selectOrdersByBizCode((String)accountTradId.get("TRADID"));
                  }else{
                      bttob = bussSettleOrderCommService.selectOrdersByBizCode((String)accountTradId.get("TRADID"));
                  }
                  if(bttob!=null && bttob.size()>0){
                      List<BussTrdTransferOrderDto> bttodList = new ArrayList<>();
                      BussTrdTransferOrderDto bttod = new BussTrdTransferOrderDto();
                      BeanUtil.copyPropertiesIgnoreNull(bttod, bttob.get(0));
                      bttodList.add(bttod);
                      List<StlPayOrdersDto> spod = new ArrayList<>();
                      spod.add(bussTrdTransferOrderService.save(bttodList).get(0));
                      btpddto.setStlPayOrdersList(spod);
                  }
              }
            }
        }
        if (DictCodeListExt2.交易方向_转换申请.getValue().equals(entity.getPs()) || DictCodeListExt2.交易方向_转换确认.getValue().equals(entity.getPs())) {
            finProductTradeDto.setNeedSum(true);
            Map<String, Object> params = new HashMap<>();
            params.put("workflowId", entity.getWorkflowId());
            params.put("bizCode", entity.getTradeId());
            params.put("updateUser", SecurityUtil.getLoginUser().getUserName());
            params.put("revokeReason", entity.getRevokeReason());
            params.put("pageId", "TrdFundInvestTrade002");
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/doRevokeComplete?", params,
                    String.class);
        }

        finProductTradeDto.setBaseTradeProductDealDtos(dtoList);
        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
        //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId()+"R");

        if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                finProductTrade.setActPortfolioId(entity.getActPortfolioId());
            });
        }

        baseTradeProductDealCommService.execute(finProductTradeDto);
        entity.setUpdateUser(finProductTradeDto.getUpdateUser());
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/doComplete",
            entity,Integer.class);

        if(StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回确认.getValue())){
            Map<String, Object> param = new HashMap<>();
            param.put("tradeId", entity.getTradeId());
            Map<String, Object> resultMap = getAccountId(param);
            if(resultMap != null){
                revokeAccountDoComplet(entity);
            }
        }
        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
        }
        
        if(StringUtil.equals(entity.getPs(), DictCodeListExt2.交易方向_认购.getValue()) 
        		|| StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购.getValue())
                || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_买入.getValue())
                || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_卖出.getValue())){
            //删除授信占用
            if (StringUtil.isNotEmpty(entity.getTradeId())){
                riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
            }
        }
        if(StringUtil.equals(DictCodeList.交易方向_赎回确认.getValue(), entity.getPs())) {
            if (StringUtil.isNotEmpty(entity.getTradeId())){
                List<String> tradeids = riskCreditListCommonValidatorService.selectTradeidByBTtradid(entity.getTradeId());
            	if (StringUtil.isNotEmpty(tradeids)) {
                	for(String tradeid : tradeids) {
                        riskCreditListCommonValidatorService.deleteCredit(tradeid);
                	}
            	}
            }
        }
        //撤销时释放授信占用
        if (type == OperationType.REVOKE && (StringUtil.equals(DictCodeList.交易方向_赎回确认.getValue(), entity.getPs()) ||
                StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购.getValue()))) {
            if (StringUtil.isNotEmpty(entity.getTradeId())){
                CreditLinesInputDto inputDto = new CreditLinesInputDto();
                inputDto.setTradeId(entity.getTradeId());
                inputDto.setTradeDate(entity.getTradeDate());
                inputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                inputDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                AllTradeCommonUtil.handleBeforeTradeComplete(Lists.newArrayList(inputDto), DictCodeListRsk.授信占用类别_实占.getValue());
            }
        }
            // 调用投管接口 add by renping 20190205 start
        if (!(StringUtil.equals(DictCodeList.基金类资产类型_货币基金.getValue(), entity.getType6())
            && (StringUtil.equals(DictCodeListExt2.交易方向_认购.getValue(), entity.getPs())
                || StringUtil.equals(DictCodeList.交易方向_申购.getValue(), entity.getPs())
                || StringUtil.equals(DictCodeList.交易方向_赎回.getValue(), entity.getPs())))
            && !(StringUtil.equals(DictCodeList.基金类资产类型_非货币基金.getValue(), entity.getType6())
                && (StringUtil.equals(DictCodeList.交易方向_赎回.getValue(), entity.getPs())))
            && !StringUtil.equals(DictCodeList.交易方向_赎回到账.getValue(), entity.getPs())) {
            InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
            if (StringUtil.equals(DictCodeList.基金类资产类型_货币基金.getValue(),
                entity.getType6())) {
                investInterfCommDto.setPageId("TrdFundInvestTrade002.02");
            }
            else {
                investInterfCommDto.setPageId("TrdFundInvestTrade002.01");
            }
            investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
            investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
            if (!StringUtil.isEmptyList(finProductTradeDto.getBaseTradeProductDealDtos())) {
                investInterfCommDto.setTrdProductDeal(
                    finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal());
                investInterfCommDto.setTrdProductDealAdd(
                    finProductTradeDto.getBaseTradeProductDealDtos().get(
                        0).getTrdProductDealAdd());
                if (!StringUtil.isEmptyList(finProductTradeDto.getBaseTradeProductDealDtos().get(
                    0).getStlPayOrdersList())) {
                    investInterfCommDto.setStlPayOrders(
                        finProductTradeDto.getBaseTradeProductDealDtos().get(
                            0).getStlPayOrdersList().get(0));
                }
                investInterfCommService.doInvestInterf(investInterfCommDto);
            }
        }
        // 调用投管接口 add by renping 20190205 end

        // 自动撤销 卖出对应 的买入转仓记录
        if(StringUtil.isNotEmpty(reverseTradeId)){
            List<BaseTradeProductDealDto> reverseDtoList = restTemplate.getForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/getFinDto?tradId="+
                    reverseTradeId,BaseTradeProductDealDto.class);

            FinProductTradeDto reverseFinProductTradeDto = new FinProductTradeDto();
            reverseFinProductTradeDto.setBaseTradeProductDealDtos(reverseDtoList);
            reverseFinProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            baseTradeProductDealCommService.execute(reverseFinProductTradeDto);

            Map<String, Object> param = new HashMap<>();
            param.put("ps", DictCodeList.交易方向_买入.getValue());
            param.put("tradeId", reverseTradeId);
            TrdFundInvestTradeDto reverseDto = trdFundInvestTrade002Service.getTrdFundDto(param);
            reverseDto.setOperateType(type.toString());
            reverseDto.setWorkflowId(entity.getWorkflowId());
            reverseDto.setUpdateUser(finProductTradeDto.getUpdateUser());
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/doComplete",
                    reverseDto,Integer.class);

        }
        // 基金转仓撤销新头寸
        if (type == OperationType.REVOKE) {
            Map<String, Object> batchRevoke = new HashMap<>();
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            data.put("busiType", "39");
            data.put("ps", entity.getPs());
            data.put("closeId", entity.getTradeId());
            data.put("systemAct", DictCodeListExt3.系统动作_撤销.getValue());
            data.put("busiAct", DictCodeListExt3.业务动作_撤销.getValue());
            dataList.add(data);
            batchRevoke.put("data", dataList);
            batchRevoke.put("userId", LoginUserUtil.getUserName());
            batchRevoke.put("totalTradeId", IDGenerator.generate());
            batchRevoke.put("modelType",DictCodeListExt1.子系统代码_交易.getValue());
            try {
                positionRequestHandle.batchRevoke(batchRevoke);
            } catch (Exception e) {
                logger.error("基金转仓撤销失败:{}", e.getMessage(), e);
            }
        }
    }
    /**
     * 
     * 交易到账撤销流程生效
     * <功能详细描述>
     * @param entity
     * @see
     * @since 1.0
     */
    public void revokeAccountDoComplet(TrdFundInvestTradeDto entity){
        Map<String, Object> accountTradId = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("tradeId", entity.getTradeId());
        accountTradId = getAccountId(param);
        if(accountTradId == null){
            accountTradId = selectAccountTrdIdinwfl(entity.getTradeId());
        }
        if(accountTradId != null){
        if(StringUtil.equals((String)accountTradId.get("WORKFLOWID"), "1")){
            List<BaseTradeProductDealDto> dtoList = restTemplate.getForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/getFinDto?tradId="+
                (String)accountTradId.get("TRADID"),BaseTradeProductDealDto.class);
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            for(BaseTradeProductDealDto btpddto:dtoList){
                btpddto.getTrdProductDeal().setIsCancel(DictCodeListExt1.交易状态_撤销.getValue());
                btpddto.getTrdProductDeal().setCancelRemark(entity.getCancelRemark());
                List<BussTrdTransferOrderBase> bttob = bussSettleOrderCommService.selectOrdersByBizCode((String)accountTradId.get("TRADID"));
                if(bttob!=null && bttob.size()>0){
                    List<BussTrdTransferOrderDto> bttodList = new ArrayList<>();
                    BussTrdTransferOrderDto bttod = new BussTrdTransferOrderDto();
                    BeanUtil.copyPropertiesIgnoreNull(bttod, bttob.get(0));
                    bttodList.add(bttod);
                    List<StlPayOrdersDto> spod = new ArrayList<>();
                    spod.add(bussTrdTransferOrderService.save(bttodList).get(0));
                    btpddto.setStlPayOrdersList(spod);
                }
            }
            finProductTradeDto.setBaseTradeProductDealDtos(dtoList);
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            // 插入组合交易表
            for(BaseTradeProductDealDto btpddto : dtoList){
                if(btpddto.getPtlTrade() != null && !btpddto.getPtlTrade().isEmpty()){
                    btpddto.getPtlTrade().get(0).setBusiNo(btpddto.getPtlTrade().get(0).getBusiNo() + "R");

                    baseTradeProductDealCommService.insertPtlTrade(btpddto.getPtlTrade().get(0));
                }
            }

            baseTradeProductDealCommService.execute(finProductTradeDto);
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
        }
        }
    }
    /**
     * 
     * 撤销流程启动时操作
     */
    @Override
    public void doInsert(TrdFundInvestTradeDto entity, OperationType type){
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        entity.setOperateType(type.toString());
        if(StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回确认.getValue())){
            Map<String, Object> accountTradId = new HashMap<>();
            Map<String, Object> param = new HashMap<>();
            param.put("tradeId", entity.getTradeId());
            accountTradId = getAccountId(param);
            if(accountTradId == null){
                accountTradId = selectAccountTrdIdinwfl(entity.getTradeId());
            }
            if(accountTradId!=null){
//                List<BussTrdTransferOrderBase> bttob = new ArrayList<>();
            if(StringUtil.equals((String)accountTradId.get("WORKFLOWID"), "0")){
//                    bttob = trdFundInvestTrade003Service.selectOrdersByBizCode((String)accountTradId.get("TRADID"));
            }else{
//                    bttob = bussSettleOrderCommService.selectOrdersByBizCode((String)accountTradId.get("TRADID"));
                TrdFundInvestTradeDto repAccount = new TrdFundInvestTradeDto();
                repAccount.setTradeId((String)accountTradId.get("TRADID"));
                repAccount.setWorkflowId(entity.getWorkflowId());
                repAccount.setCancelRemark(entity.getCancelRemark());
//                    if (bttob != null && bttob.size() > 0) {
//                        List<BussTrdTransferOrderDto> bttodList = new ArrayList<>();
//                        BussTrdTransferOrderDto bttod = new BussTrdTransferOrderDto();
//                        BeanUtil.copyPropertiesIgnoreNull(bttod, bttob.get(0));
//                        bttodList.add(bttod);
//                        repAccount.setBussTrdTransferOrder(bussTrdTransferOrderService.save(bttodList));
//                    }
                revokeAccount(repAccount);
            }
            }
        }
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/doInsert",
            entity, Integer.class);
    }
    
    /**
     * 撤销流程作废
     */
    @Override
    public void doDelete(String workflowId) {
        restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/doDelete?workflowId="+
            workflowId, Integer.class);
    }
    /**
     * 
     * 撤销已生效交易到账
     * <功能详细描述>
     * @param
     * @see
     * @since 1.0
     */
    public void revokeAccount(TrdFundInvestTradeDto entity){
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/revokeAccount",
            entity, void.class);
    }
    
    public boolean selectConfirmInfo(String bTradeId){
        return restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/selectConfirmInfo?bTradeId="+
            bTradeId, boolean.class);
    }
    
    /**
     * 
     * 查询到账交易是否托管匹配
     * <功能详细描述>
     * @param tradeId
     * @return
     * @see
     * @since 1.0
     */
    public Integer selRedemption(String tradeId){
        return restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/selRedemption?tradeId="+
            tradeId,Integer.class);
    }
    /**
     * 
     * 检验撤回数据是否有后继交易
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public boolean revokeCheck(TrdFundInvestTradeDto dto){
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/revokeCheck",
            dto,boolean.class);
    }
    /**
     * 
     * 获取交易到账交易编号
     * <功能详细描述>
     * @param
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> getAccountId(Map<String, Object> param){
        Map<String, Object> accountTradId = restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/selectAccountTrdId",
            param, Map.class);
        return accountTradId;
    }
    /**
     * 
     * 获取交易到账交易编号
     * <功能详细描述>
     * @param tradId
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> selectAccountTrdIdinwfl(String tradId){
        Map<String, Object> accountTradId = restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/selectAccountTrdIdinwfl?tradId="+
            tradId, Map.class);
        return accountTradId;
    }
    /**
     * 导出
     * 
     * @param exportParam 导出信息
     * @see
     * @since 1.0
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
    	String isShowType = (String) exportParam.getSelectParam().get("isShowType");
    	if(!StringUtil.isNotEmpty(isShowType) || StringUtil.equals(isShowType, "01")) {
        	exportParam.config("/com/joyintech/tams/invest/trade/fund/conf/export_conf.xml").dataSource(DataSourceType.MASERT);
        }else {
        	exportParam.config("/com/joyintech/tams/invest/trade/fund/conf/export_conf002.xml").dataSource(DataSourceType.MASERT);
        }
       
    }
    @Override
    public Page<TrdFundInvestTradeDto> selectPageData(Map<String, Object> selectParam) {
        return findPage(selectParam);
    }

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId)
            throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        TrdFundInvestTradeDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            BigDecimal amt = dto.getTradeAmt();
            if (amt == null || amt.compareTo(BigDecimal.ZERO) <= 0) {
                amt = dto.getShareAmt();
            }
            try {
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), amt, LoginUserUtil.getUserName(),
                        DictCodeList.平安资产类型_基金.getValue(), dto.getType6(), dto.getTradeMarket(), dto.getPs()
                        , dto.getFinprodId() ,entity.getWorkflowId());
                params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
                return commonBusinessService.是否需要上级审批(params);
            }
            catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        else {
            return null;
        }
    }

    /**
     * 获取委托代码
     * @param map 请求参数
     * @return 联想文本框实体类
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getEntrustIdByType(Map<String,Object> map){
        return restTemplate.postForList(
                ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/getEntrustId", map,
                SuggestDto.class);
    }
}
