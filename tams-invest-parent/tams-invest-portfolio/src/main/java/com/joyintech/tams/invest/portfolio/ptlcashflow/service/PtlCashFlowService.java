/*
 * 文件名：PtlCashFlowService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年10月26日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.portfolio.ptlcashflow.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.util.MapParseUtil;
import com.joyintech.tams.invest.portfolio.ptlcashflow.dto.PartyInfoDto;
import com.joyintech.tams.invest.portfolio.ptlcashflow.dto.PtlCashFlowDto;
import com.joyintech.tams.invest.portfolio.ptlcashflow.dto.PtlCashFlowInterfaceDto;
import com.joyintech.tams.invest.portfolio.ptlcashflow.dto.SettleInfoDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义现金流
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年10月26日
 * @see PtlCashFlowService
 * @since 1.0
 */
@Service
public class PtlCashFlowService extends CommonExportService<PtlCashFlowDto> 
    implements BaseProcessService<PtlCashFlowDto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(PtlCashFlowService.class);

    /**
     * 划款指令共同
     */
    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    /**
     * 共通交易
     */
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;

    @Autowired
    private MapperDelegateService bussCommonMapperDelegateService;
    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(PtlCashFlowDto entity, OperationType type) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        String action = StringUtil.EMPTY;
        // entity.setSystemCode("SYS");
        if (OperationType.REVOKE.equals(type)) {
            action = "delete";
        }
        else if (OperationType.NEW.equals(type)) {
            action = "insert";
        }

        this.deleteWflData(entity.getWorkflowId());
        restTemplate.postForObject(
            ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlCashFlow/" + action, entity,
            Integer.class);

        //发送数据到第三方系统
        try {
            PtlCashFlowInterfaceDto ptlCashFlowInterfaceDto = new PtlCashFlowInterfaceDto();
            ptlCashFlowInterfaceDto.setTRADE_ID(entity.getTradeId());//交易编号
            ptlCashFlowInterfaceDto.setF_TRADE_ID(entity.getfTradeId());//关联业务编号

            //交易类型
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_收取.getValue())) {
                ptlCashFlowInterfaceDto.setTRADE_TYPE(DictCodeListExt1.平安业务类型_自定义现金流收取.getValue());
            }
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_支付.getValue())) {
                ptlCashFlowInterfaceDto.setTRADE_TYPE(DictCodeListExt1.平安业务类型_自定义现金流支付.getValue());
            }
            ptlCashFlowInterfaceDto.setPORTFOLIO_ID(entity.getPortfolioId());//组合代码
            ptlCashFlowInterfaceDto.setPORTFOLIO_NAME(entity.getPortfolioName());//组合名称
            ptlCashFlowInterfaceDto.setCOUNTER_ID(entity.getCounterId());//交易对手代码
            ptlCashFlowInterfaceDto.setCOUNTER_NAME(entity.getCounterTrader());//交易对手名称
            ptlCashFlowInterfaceDto.setFINPROD_ID(entity.getFinprodId());//资产代码
            ptlCashFlowInterfaceDto.setFINPROD_NAME(entity.getFinprodAbbr());//资产名称
            ptlCashFlowInterfaceDto.setTRADE_DATE(entity.getTradeDate());//交易日
            ptlCashFlowInterfaceDto.setSETTLE_DATE(entity.getSettleDate());//交割日
            ptlCashFlowInterfaceDto.setCASH_TYPE(entity.getCustomCashType());//现金流类型
            ptlCashFlowInterfaceDto.setPS(entity.getPs());//收付方向
            ptlCashFlowInterfaceDto.setTRADE_AMT(entity.getTradeAmt());//交易金额
            ptlCashFlowInterfaceDto.setCCY(entity.getCcy());//结算币种
            if (OperationType.NEW.equals(type)) {
                ptlCashFlowInterfaceDto.setTRADE_STATUS(DictCodeListExt1.交易状态_初始.getValue());//交易状态
            }
            if (OperationType.REVOKE.equals(type)) {
                ptlCashFlowInterfaceDto.setTRADE_STATUS(DictCodeListExt1.交易状态_撤销.getValue());//交易状态
            }
            ptlCashFlowInterfaceDto.setP_TRADE_ID(entity.getpTradeId());//原交易编号
            ptlCashFlowInterfaceDto.setCANCEL_REMARK(entity.getCancelRemark());//撤销说明
            ptlCashFlowInterfaceDto.setREMARK(entity.getRemark());//备注
            //参与人信息

            List<PartyInfoDto> partyInfoDtoList = new ArrayList<>();
            // 本方交易员
            PartyInfoDto partyInfoMap = new PartyInfoDto();
            // 参与人角色
            partyInfoMap.setPARTY_ROLE("01");
            // 参与人名称
            partyInfoMap.setPARTY_NAME(entity.getTrader());
            // 参与人联系方式
            partyInfoMap.setPARTY_CONTACT_INFO("");
            partyInfoDtoList.add(partyInfoMap);
            // 对手方交易员
            PartyInfoDto party = new PartyInfoDto();
            // 参与人角色
            party.setPARTY_ROLE("02");
            // 参与人名称
            party.setPARTY_NAME(entity.getCounterTrader());
            // 参与人联系方式
            party.setPARTY_CONTACT_INFO(entity.getCounterContact());
            partyInfoDtoList.add(party);
            ptlCashFlowInterfaceDto.setPARTY_INFO(partyInfoDtoList);
            //划款信息
            if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder()) && entity.getBussTrdTransferOrder().size() > 0) {
                List<SettleInfoDto> settleInfoDtoList = new ArrayList<>();
                entity.getBussTrdTransferOrder().forEach(item -> {
                    SettleInfoDto settleInfoDto = new SettleInfoDto();
                    settleInfoDto.setPAY_ACCT_NO(item.getPayAcctNo());//付款账户
                    settleInfoDto.setREC_ACCT_NO(item.getRecAcctNo());//收款账户
                    settleInfoDto.setSETTLE_AMT(item.getSettleAmt());//划款金额
                    settleInfoDto.setCCY(item.getCcy());//币种
                    settleInfoDtoList.add(settleInfoDto);
                });
                ptlCashFlowInterfaceDto.setSETTLE_INFO(settleInfoDtoList);
            }
            Map<String, Object> map = MapParseUtil.transformUpperCase(JsonUtils.object2Map(ptlCashFlowInterfaceDto));
            map.put("pageId", "RofvCustomerDeal");
            investInterfCommService.doInvestInterf(map);
        }
        catch (Exception ex)
        {}
    }

    private void deleteWflData(String workflowId) {
        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "WORKFLOW_ID",workflowId);
        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD", "WORKFLOW_ID",workflowId);
        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_STL_PAY_ORDERS", "WORKFLOW_ID",workflowId);
        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL", "WORKFLOW_ID",workflowId);
    }

    @Override
    public void doInsert(PtlCashFlowDto entity, OperationType type) {
        saveDataWhenStartWorkflow(entity, type);
    }
    /**
     * 工作流doUpdate
     * @param entity entity 业务entity
     */
    @Override
    public void doUpdate(PtlCashFlowDto entity) {
        if("submit".equals(entity.getOperateType())) {
            doDelete(entity.getWorkflowId());
        }
        saveDataWhenStartWorkflow(entity, OperationType.NEW);
    }
    
    @Override
    public void doDelete(String workflowId) {
        restTemplate.getForObject(
            ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlCashFlow/doDelete?workflowId="+ workflowId,
            void.class);
    }

    // ZGZGST-1559 退回时释放头寸 fuyongsheng 20210827 add start
    @Override
    public void doBack(PtlCashFlowDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getBusiNo());
    }
    // ZGZGST-1559 退回时释放头寸 fuyongsheng 20210827 add end

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity
     * @param type
     * @see
     * @since 1.0
     */
    private void saveDataWhenStartWorkflow(PtlCashFlowDto entity, OperationType type) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        if ("submit".equals(entity.getOperateType())||type.equals(OperationType.REVOKE)) {
//          产品交易DTO
            List<WflTrdProductDeal> wflTrdProductDeals = getwflTrdProductDeals(entity);
//          金融产品交易扩展表Entity
            List<WflPaTrdProductDeal> wflPaTrdProductDeals = getWflPaTrdProductDeal(entity);
//          金融产品交易附属表Entity
            List<WflTrdProductDealAdd> wflTrdProductDealAdds = getWflTrdProductDealAdd(entity);
            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
            List<WflStlPayOrders> stlPayOrdersDtos = getWflStlPayOrders(entity);
            if (type == OperationType.REVOKE) {
                wflTrdProductDeals.forEach(wflTrdProductDeal -> {
                    String oldTradeId = "";
                    if(entity.getBusiNo().lastIndexOf("R") == entity.getBusiNo().length()-1){
                        oldTradeId = StringUtil.substring(entity.getBusiNo(),0,entity.getBusiNo().length()-1);
                    }else{
                        oldTradeId = entity.getBusiNo();
                    }
                    wflTrdProductDeal.setpTradeId(oldTradeId);
                    wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    //    wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + "R");
                });
//                wflPaTrdProductDeals.forEach(wflTrdProductDeal -> {
//                    wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + "R");
//                });
//
//                wflTrdProductDealAdds.forEach(wflTrdProductDeal -> {
//                    wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + "R");
//                });

                stlPayOrdersDtos.forEach(order -> {
                    //          order.setTradeId(order.getTradeId() + "R");
                    order.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                    order.setOrderId(order.getOrderId() + "R");
                });

            }
//          组合交易表Entity
//          划款指令表Entity(调用划款指令共通业务组件的保存处理，拿到输出的划款指令结果DTO。加工形成金融产品交易DTO的要素数据)
            wflTrdProductDeals.forEach(wflTrdProductDeal -> wflTrdProductDealMapper.insertSelective(wflTrdProductDeal));
            stlPayOrdersDtos.forEach(wflStlPayOrders -> wflStlPayOrdersMapper.insertSelective(wflStlPayOrders));
            wflPaTrdProductDeals.forEach(wflPaTrdProductDeal -> wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal));
            wflTrdProductDealAdds.forEach(wflTrdProductDealAdd -> wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd));

            // 调用业务组件.【金融产品交易共通.交易产品接口】
            FinProductTradeDto finProductTradeDto = genFinProductTradeDto(entity);
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            baseTradeProductDealCommService.execute(finProductTradeDto);

            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !StringUtil.equals(entity.getPortfolioId(), entity.getActPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
        }
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    private FinProductTradeDto genFinProductTradeDto(PtlCashFlowDto entity) {
     // 5.构造调用组件的输入参数DTO
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDtos.add(baseTradeProductDealDto);
        TrdProductDeal trdProductDeal = getTrdProductDeal(entity);
        if(entity.getBussTrdTransferOrder()!=null &&entity.getBussTrdTransferOrder().size()>0 ){
            // 划款指令表Entity
            List<BussTrdTransferOrderDto> list = new ArrayList<BussTrdTransferOrderDto>();
            for(BussTrdTransferOrderBase e:entity.getBussTrdTransferOrder()){
                BussTrdTransferOrderDto d = new BussTrdTransferOrderDto();
                BeanUtil.copyPropertiesIgnoreNull(d, e);
                list.add(d);
            }
            List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderService.save(list);
            List<StlPayOrdersDto> stlPayOrdersDtos = new ArrayList<>();
            if (stlPayOrdersList != null) {
                stlPayOrdersList.forEach(stlPayOrders -> {
                    StlPayOrdersDto dto = new StlPayOrdersDto();
                    BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrders);
                    stlPayOrdersDtos.add(dto);
                });
            }
            baseTradeProductDealDto.setStlPayOrdersList(stlPayOrdersDtos);
        }
        TrdProductDealAdd add = new TrdProductDealAdd();
        add.setPortfolioId(entity.getPortfolioId());
        baseTradeProductDealDto.setTrdProductDealAdd(add);
        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
        finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        return finProductTradeDto;
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    private TrdProductDeal getTrdProductDeal(PtlCashFlowDto dto) {
      //金融产品交易信息dto
        TrdProductDeal entity = new TrdProductDeal();
        entity.setTradeId(dto.getBusiNo());
        if(StringUtil.isNotEmpty(dto.getFinprodId())){
        entity.setFinprodId(dto.getFinprodId());
        }else{
            entity.setFinprodId("999999999999");
        }
        entity.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_自定义现金流.getValue());
        entity.setBranch(dto.getBranch());
        entity.setTradeDate(dto.getSettleDate());
        entity.setSettleDate(dto.getSettleDate());
        entity.setCcy(dto.getCcy());
        entity.setTradeAmt(dto.getTradeAmt());
        entity.setPs(dto.getPs());
        entity.setIsClean(dto.getIsClean());
        entity.setCounterId(dto.getCounterId());
        entity.setfTradeId(dto.getfTradeId());
        entity.setRemark(dto.getRemark());
        entity.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        entity.setIsCancel(DictCodeList.是否_否.getValue());
        entity.setpTradeId(null);
        entity.setTradeId(dto.getBusiNo());
        entity.setRemark(dto.getRemark());
        // ZGZGST-1339: 设置业务类型,fuyongsheng 20210824 add start
        entity.setBusiType(dto.getBusiType());
        // ZGZGST-1339: 设置业务类型,fuyongsheng 20210824 add end
        return entity;
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    private List<WflStlPayOrders> getWflStlPayOrders(PtlCashFlowDto entity) {
      //划款指令表entity
        List<WflStlPayOrders> wflStlPayOrdersList = new ArrayList<WflStlPayOrders>();
        List<BussTrdTransferOrderBase> list = entity.getBussTrdTransferOrder();
        if(list != null && list.size()>0){
            for( BussTrdTransferOrderBase d:list){
                WflStlPayOrders dto = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(dto, d);
                dto.setTradeId(entity.getBusiNo());
                dto.setWorkflowId(entity.getWorkflowId());
                dto.setWorkflowStatus(DictCodeList.数据状态_新建中.getValue());
                if(StringUtil.equals(entity.getPs(), DictCodeList.交易方向_收取.getValue())){
                    dto.setMyAcctNo(d.getRecAcctNo());
                    dto.setCounterAcctNo(d.getPayAcctNo());
                }
                if(StringUtil.equals(entity.getPs(), DictCodeList.交易方向_支付.getValue())){
                    dto.setMyAcctNo(d.getPayAcctNo());
                    dto.setCounterAcctNo(d.getRecAcctNo());
                }
                
                wflStlPayOrdersList.add(dto);
            }
        }
        return wflStlPayOrdersList;
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    private List<WflTrdProductDealAdd> getWflTrdProductDealAdd(PtlCashFlowDto entity) {
        /**
         * 金融产品交易附属流程表
         */
        WflTrdProductDealAdd WflTrdProductDealAdd = new WflTrdProductDealAdd();
        WflTrdProductDealAdd.setTradeId(entity.getBusiNo());
        WflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
        //entityAdd.setProdId(dto.getFinprodId());
        WflTrdProductDealAdd.setCustomCashType(entity.getCustomCashType());
        // 一体化对应etliu start
//        WflTrdProductDealAdd.setGenType(DictCodeListExt1.录入方式_手工维护.getValue());
        WflTrdProductDealAdd.setGenType(DictCodeListExt1.录入方式_手工录入.getValue());
        // 一体化对应etliu end
        WflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
        WflTrdProductDealAdd.setWorkflowStatus(entity.getStatus());
        List<WflTrdProductDealAdd> list = new ArrayList<WflTrdProductDealAdd>();
        list.add(WflTrdProductDealAdd);
        return list;
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    private List<WflPaTrdProductDeal> getWflPaTrdProductDeal(PtlCashFlowDto entity) {
        /**
         * 金融产品交易扩展流程表
         */
        WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
        wflPaTrdProductDeal.setTradeId(entity.getBusiNo());
        if(StringUtil.equals(entity.getPs(), DictCodeList.交易方向_收取.getValue())){
            wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_自定义现金流收取.getValue());
        }
        if(StringUtil.equals(entity.getPs(), DictCodeList.交易方向_支付.getValue())){
            wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_自定义现金流支付.getValue());
        }
        wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
        wflPaTrdProductDeal.setWorkflowStatus(entity.getStatus());
        List<WflPaTrdProductDeal> list = new ArrayList<WflPaTrdProductDeal>();
        list.add(wflPaTrdProductDeal);
        return list;
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    private List<WflTrdProductDeal> getwflTrdProductDeals(PtlCashFlowDto entity) {
        /**
         * 金融产品交易业务流程表
         */
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdProductDeal.setTradeId(entity.getBusiNo());
        if(StringUtil.isNotEmpty(entity.getFinprodId())){
        wflTrdProductDeal.setFinprodId(entity.getFinprodId());
        }else{
            wflTrdProductDeal.setFinprodId("999999999999");
        }
        wflTrdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_自定义现金流.getValue());
        wflTrdProductDeal.setBranch(entity.getBranch());
        wflTrdProductDeal.setTradeDate(entity.getSettleDate());
        wflTrdProductDeal.setSettleDate(entity.getSettleDate());
        wflTrdProductDeal.setCcy(entity.getCcy());
        wflTrdProductDeal.setTradeAmt(entity.getTradeAmt());
        wflTrdProductDeal.setPs(entity.getPs());
        wflTrdProductDeal.setIsClean(entity.getIsClean());
        wflTrdProductDeal.setCounterId(entity.getCounterId());
        wflTrdProductDeal.setfTradeId(entity.getfTradeId());
        wflTrdProductDeal.setRemark(entity.getRemark());
        wflTrdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        wflTrdProductDeal.setRemark(entity.getRemark());
        wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
        wflTrdProductDeal.setWorkflowStatus(entity.getStatus());
        List<WflTrdProductDeal> list = new ArrayList<WflTrdProductDeal>();
        list.add(wflTrdProductDeal);
        return list;
    }
    /**
     * 获得业务编号
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getBusiNo(String param) {
        return restTemplate.getForList(ServiceApplication.PORTFOLIO_SERVICE_NAME
            + "/PtlCashFlow/getBusiNo?param=" + param,SuggestDto.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getPortfolioId(String param) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("param", param);
        User user = SecurityUtil.getLoginUser();
        map.put("userName", user.getUserName());
        return restTemplate.postForPage(ServiceApplication.PORTFOLIO_SERVICE_NAME
            + "/PtlCashFlow/getPortfolioId"  ,map,SelectOptionDto.class);
    }
    /**
     * 获取组合代码的下拉框
     * <功能详细描述>
     * @return SuggestDto
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortfolioIdToNew(Map<String,Object> param) {
        User user = SecurityUtil.getLoginUser();
        param.put("userName", user.getUserName());
        return restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME
            + "/PtlCashFlow/getPortfolioIdToNew" ,param,SelectOptionDto.class);
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getFinprodMarketId(String param) {
        return restTemplate.getForList(ServiceApplication.PORTFOLIO_SERVICE_NAME
            + "/PtlCashFlow/getFinprodMarketId?param=" + param,SuggestDto.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public Page<PtlCashFlowDto> selectForPage(Map<String, Object> pageParam) {
        User user = SecurityUtil.getLoginUser();
        pageParam.put("userName", user.getUserName());
        return restTemplate.postForPage(
            ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlCashFlow/findPage",
            pageParam, PtlCashFlowDto.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getCounterId(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.PORTFOLIO_SERVICE_NAME
            + "/PtlCashFlow/getCounterId" , param,SelectOptionDto.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodIdToNew(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.PORTFOLIO_SERVICE_NAME
            + "/PtlCashFlow/getFinprodIdToNew" , param,SelectOptionDto.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public PtlCashFlowDto getEntity(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME
            + "/PtlCashFlow/getEntity" , map,PtlCashFlowDto.class);
    }
    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
            "/com/joyintech/tams/invest/portfolio/ptlcashflow/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PtlCashFlowDto> selectPageData(Map<String, Object> selectParam) {
        try {
            return selectForPage(selectParam);
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getFTradeId(Map<String, Object> param) {
        return restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME
            + "/PtlCashFlow/getFTradeId" , param,SelectOptionDto.class);
    }
    
}
