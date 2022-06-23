/*
 * 文件名：FinProductFeePay004Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zcw
 * 修改时间：2019年02月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.product.feepay.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.TrdCommonWflParam;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.product.dto.FinProductExpireBaseTrdDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.TradeProductDealCommService;
import com.joyintech.tams.invest.common.valuationinterf.ValuationInterfService;
import com.joyintech.tams.invest.product.common.service.ProductCommonService;
import com.joyintech.tams.invest.product.feepay.dto.FinProductFeePay001Dto;
import com.joyintech.tams.invest.product.feepay.dto.FinProductFeePay002Dto;
import com.joyintech.tams.invest.product.feepay.dto.FinProductFeePay003Dto;
import com.joyintech.tams.invest.product.feepay.dto.FinProductFeePay005Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 产品费用支付管理-批量确认
 *
 * @author zcw
 * @version 1.0 2019年02月14日
 * @see FinProductFeePay004Service
 * @since 1.0
 */
@Service
public class FinProductFeePay004Service extends BaseService<FinProductFeePay005Dto> implements BaseProcessService<FinProductFeePay005Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(FinProductFeePay004Service.class);

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private TradeProductDealCommService baseTradeProductDealCommService;

    /**
     * {@linkplain}
     */

    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * {@linkplain}
     */
    @Autowired
    private ProductCommonService productCommonService;

    /**
     * {@linkplain}
     */
    @Autowired
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired(required = false)
    private BaseGetBussinessId baseGetBussinessId;

    /**
     * ValuationInterfService
     */
    @Autowired
    private ValuationInterfService valuationInterfService;

    @Autowired
    private FinProductFeePay002Service finProductFeePay002Service;

    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;
    
    @Autowired
    private TrdAsynHandleStatusMapper trdAsynHandleStatusMapper;
    /**
     * 委托指令共通
     */
    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;
    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void insertBussFlowData(FinProductFeePay002Dto entity, FinProductFeePay005Dto entityDto) {
        this.setFeeCashId(entity);
        FinProductFeePay003Dto finProductFeePay003Dto = new FinProductFeePay003Dto();
        // 转换费用列表
        finProductFeePay003Dto.setFinprodId(entity.getFinprodId());
        finProductFeePay003Dto.setActPayAmt(entity.getActPayAmt());
        finProductFeePay003Dto.setPayAmt(entity.getPayAmt());
        finProductFeePay003Dto.setIsClean(entity.getIsClean());
        List<FinProductFeePay003Dto> finProductFeePay003DtoList = new ArrayList<FinProductFeePay003Dto>();
        finProductFeePay003DtoList.add(finProductFeePay003Dto);
        entity.setFeeInfoList(finProductFeePay003DtoList);
        // 划款指令
        if (!StringUtil.isEmptyList(entityDto.getBussTrdTransferOrder())) {
            entityDto.getBussTrdTransferOrder().get(0).setSettleAmt(entity.getActPayAmt());
            entityDto.getBussTrdTransferOrder().get(0).setPortfolioId(entity.getPtlId());
        }
        if (CollectionUtils.isNotEmpty(entity.getFileArray())
                && StringUtil.isNotEmpty(entity.getTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getFileArray().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getTradeId());
        }

        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }

        StlPayOrders stlPayOrders = this.genStlPayOrdersDto(entityDto, entity.getTradeId());

        if (baseGetBussinessId != null) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setSeqNo("9998");
            stlPayOrders.setOrderId(baseGetBussinessId.buildBussinessCode(bussinessCodeParameter));
        } else {
            stlPayOrders.setOrderId(IDGenerator.generate());
        }

        TrdCommonWflParam trdCommonWflParam = new TrdCommonWflParam();

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        if (StringUtil.isNotEmpty(stlPayOrders)) {
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrders);
            wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            trdCommonWflParam.setWflStlPayOrdersDtos(Arrays.asList(wflStlPayOrders));
        }

        TrdFinProductDealTotal trdFinProductDealTotal = this.genTrdFinProductDealTotal(entity,
                stlPayOrders.getSettleAmt());
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, trdFinProductDealTotal);
        wflTrdFinProductDealTotal.setWorkflowStatus(
                DictCodeListExt1.交易流程状态_流程中.getValue());
        trdCommonWflParam.setWflTrdFinProductDealTotal(wflTrdFinProductDealTotal);

        //pa 金融汇总交易表
        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, wflTrdFinProductDealTotal);
        wflPaTrdFinDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品费用支付.getValue());
        trdCommonWflParam.setWflPaTrdFinDealTotal(wflPaTrdFinDealTotal);

        FinProductExpireBaseTrdDto finProductExpireBaseTrdDto = this.genBaseTradeDealDtoList(entity, stlPayOrders);

        List<WflTrdProductDeal> wflTrdProductDealList = new ArrayList<WflTrdProductDeal>();
        List<WflTrdProductDealAdd> wflTrdProductDealAddList = new ArrayList<WflTrdProductDealAdd>();
        List<WflPaTrdProductDeal> wflPaTrdProductDealList = new ArrayList<WflPaTrdProductDeal>();

        for (TrdProductDeal trdProductDeal : finProductExpireBaseTrdDto.getTrdProductDealList()) {
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, trdProductDeal);
            wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealList.add(wflTrdProductDeal);
        }

        for (TrdProductDealAdd trdProductDealAdd : finProductExpireBaseTrdDto.getTrdProductDealAddList()) {
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, trdProductDealAdd);
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealAddList.add(wflTrdProductDealAdd);
        }

        for (PaTrdProductDeal paTrdProductDeal : finProductExpireBaseTrdDto.getPaTrdProductDealList()) {
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, paTrdProductDeal);
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            wflPaTrdProductDealList.add(wflPaTrdProductDeal);
        }

        trdCommonWflParam.setWflTrdProductDeals(wflTrdProductDealList);
        trdCommonWflParam.setWflDealAdds(wflTrdProductDealAddList);
        trdCommonWflParam.setWflPaDeals(wflPaTrdProductDealList);

        restTemplate.postForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/insertBussFlowData", trdCommonWflParam, String.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.deleteWflData(workflowId);
    }

    private void setFeeCashId(FinProductFeePay002Dto entity) {
        if (StringUtil.isNotEmpty(entity.getCashId()) 
            && !StringUtil.equals(entity.getCashId(),"N") ) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("feeId", entity.getFeeId());
        map.put("finprodId", entity.getFinprodId());
        map.put("cashType1", DictCodeListExt1.现金流一级类型_费用.getValue());
        String cahsId = restTemplate.postForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectCashId", map, String.class);
        entity.setCashId(cahsId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doInsert(FinProductFeePay005Dto entityDto, OperationType type) {
        if (!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
            for (FinProductFeePay002Dto entity : entityDto.getFinProductFeePay()) {
                this.insertBussFlowData(entity, entityDto);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(FinProductFeePay005Dto entityDto) {
        // 先删除流程中数据
        for (FinProductFeePay002Dto entity : entityDto.getFinProductFeePay()) {
            if (!StringUtil.equals("revoke", entity.getOperateType())
                    && !StringUtil.equals("delete", entity.getOperateType())) {
                // 先删除流程中数据
                this.doDelete(entity.getWorkflowId());
                // 重新insert
                this.insertBussFlowData(entity, entityDto);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(FinProductFeePay005Dto entityDto, OperationType type) {
        if (type == OperationType.NEW) {
            List<StlPayOrders> stlPayOrdersList = this.genStlPayOrdersDtos(entityDto, entityDto.getTradeId());
            BigDecimal settleAmt = BigDecimal.ZERO;
            for (StlPayOrders stlPayOrders :stlPayOrdersList) {
                if (baseGetBussinessId != null) {
                    BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                    bussinessCodeParameter.setSeqNo("9998");
                    stlPayOrders.setOrderId(baseGetBussinessId.buildBussinessCode(bussinessCodeParameter));
                } else {
                    stlPayOrders.setOrderId(IDGenerator.generate());
                }
                if (StringUtil.equals(stlPayOrders.getPortfolioId(),entityDto.getFinProductFeePay().get(0).getFinprodMarketId())){
                    settleAmt = stlPayOrders.getSettleAmt();
                }
            }
            // 金融产品交易汇总表
            TrdFinProductDealTotal trdFinProductDealTotal = this.genTrdFinProductDealTotal(entityDto,
                    settleAmt);
            trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
            // 20191212add 产品费用支付新增插表
            TrdAsynHandleStatus trdAsynHandleStatus = new TrdAsynHandleStatus();
            trdAsynHandleStatus.setTradeId(entityDto.getTradeId());
            trdAsynHandleStatus.setTrdTableType("01");		//交易表类型	01金融产品交易汇总表
            trdAsynHandleStatus.setHandleStatus("01");		//处理状态	01待处理
            trdAsynHandleStatusMapper.insertSelective(trdAsynHandleStatus);
            
            //pa 金融产品交易汇总表
            PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(paTrdFinProductDealTotal, trdFinProductDealTotal);

            paTrdFinProductDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品费用支付.getValue());
            //插入pa 金融产品交易汇总表
            paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);

            // 组合交易表
            PtlTrade ptlTrade = this.genPtlTrdea(entityDto);

            ptlTradeMapper.insertSelective(ptlTrade);
            for (StlPayOrders stlPayOrders :stlPayOrdersList) {
                stlPayOrders.setTradeId(entityDto.getTradeId());
                stlPayOrdersMapper.insertSelective(stlPayOrders);
            }


            String tempFinprodType = productCommonService.convertFeeTypeToCashType(entityDto.getFeeType());
            if (StringUtil.isEmpty(tempFinprodType)) {
                tempFinprodType = entityDto.getFeeType();
            }

            //add by leipeng 20190706 如果有子代码则使用分层编号
            if (StringUtil.isEmptyList(entityDto.getFinProductFeePay()) && StringUtil.isNotEmpty(entityDto.getFinProductFeePay().get(0).getsFinprodId())) {
                //子分层直接查询的现金流二级类型..如果查不到则直接使用原费用
                tempFinprodType = entityDto.getFeeType();
            }
            // 交易费用明细Entity
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            trdTradeFeeDetail.setTradeId(entityDto.getTradeId());
            trdTradeFeeDetail.setFeeType(tempFinprodType);
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            trdTradeFeeDetail.setFeeAmt(entityDto.getPayAmtSum());
            trdTradeFeeDetail.setFeeId(entityDto.getFeeId());
            trdTradeFeeDetail.setUpdateUser(entityDto.getUpdateUser());
            trdTradeFeeDetail.setCreateUser(entityDto.getCreateUser());
            trdTradeFeeDetail.setCreateDept(entityDto.getCreateDept());
            trdTradeFeeDetail.setWorkflowId(entityDto.getWorkflowId());

            trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);


            // 组合现金流水Entity
            List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<PtlCashPositionFlowDto>();
            for (StlPayOrders stlPayOrders :stlPayOrdersList) {
                PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
                ptlCashPositionFlowDto.setPortfolioId(ptlTrade.getBusiId());
                ptlCashPositionFlowDto.setAcctId(stlPayOrders.getMyAcctNo());
                ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
                ptlCashPositionFlowDto.setCdate(entityDto.getPayDate());
                ptlCashPositionFlowDto.setCcy(stlPayOrders.getCcy());
                ptlCashPositionFlowDto.setAmount(stlPayOrders.getSettleAmt().negate());
                ptlCashPositionFlowDto.setOrderId(stlPayOrders.getOrderId());
                ptlCashPositionFlows.add(ptlCashPositionFlowDto);
            }
//          委托指令流水共通调用
            //增加委托指令流水逻辑
            if(baseEtrCashPositionCommService!=null){
                PtlPositionHoldDto ptlHoldDto = new PtlPositionHoldDto();
                ptlHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
                baseEtrCashPositionCommService.portfolioEtrCashPosition(ptlHoldDto, true);
            }
            PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
            ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
            restTemplate.postForObject(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
                    ptlPositionHoldDto, Boolean.class);
            for (FinProductFeePay002Dto entity : entityDto.getFinProductFeePay()) {
                entity.setTradeId(entityDto.getTradeId());
                this.doCompleteInsert(entity, entityDto);
            }
        }
    }

    /**
     * 生成金融产品交易汇总
     *
     * @param entity    产品费用支付Dto
     * @param actPayAmt 实付金额
     * @return 金融产品交易汇总
     * @see
     * @since 1.0
     */
    private TrdFinProductDealTotal genTrdFinProductDealTotal(FinProductFeePay002Dto entity,
                                                             BigDecimal actPayAmt) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getTradeId());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_产品费用支付管理.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        trdFinProductDealTotal.setVdate(entity.getStartDate());
        trdFinProductDealTotal.setMdate(entity.getEndDate());
        trdFinProductDealTotal.setSettleDate(entity.getPayDate());
        trdFinProductDealTotal.setTradeAmt(actPayAmt);
        trdFinProductDealTotal.setCashId(entity.getCashId());
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_产品付费.getValue());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setPortfolioId(entity.getPtlId());
        trdFinProductDealTotal.setRemark(entity.getRemark());
        trdFinProductDealTotal.setUpdateUser(entity.getUpdateUser());
        trdFinProductDealTotal.setCreateUser(entity.getCreateUser());
        trdFinProductDealTotal.setCreateDept(entity.getCreateDept());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());

        trdFinProductDealTotal.setFinprodType(entity.getFeeType());
        trdFinProductDealTotal.setFinprodType2(entity.getFeeType());
        if (StringUtil.equals(entity.getChargeType(), DictCodeList.收费类型_不规则费用.getValue())) {
            trdFinProductDealTotal.setMdate(entity.getPayDate());
        }

        return trdFinProductDealTotal;
    }

    /**
     * 生成金融产品交易汇总
     *
     * @param entity    产品费用支付Dto
     * @param actPayAmt 实付金额
     * @return 金融产品交易汇总
     * @see
     * @since 1.0
     */
    private TrdFinProductDealTotal genTrdFinProductDealTotal(FinProductFeePay005Dto entity,
                                                             BigDecimal actPayAmt) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getTradeId());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_产品费用支付管理.getValue());
        if (!StringUtil.isEmptyList(entity.getFinProductFeePay())) {
            trdFinProductDealTotal.setFinprodId(entity.getFinProductFeePay().get(0).getFinprodId());
            trdFinProductDealTotal.setVdate(entity.getFinProductFeePay().get(0).getStartDate());
            trdFinProductDealTotal.setMdate(entity.getFinProductFeePay().get(0).getEndDate());
            trdFinProductDealTotal.setPortfolioId(entity.getFinProductFeePay().get(0).getPtlId());
        }
        trdFinProductDealTotal.setSettleDate(entity.getPayDate());
        trdFinProductDealTotal.setTradeAmt(actPayAmt);
        //trdFinProductDealTotal.setCashId(entity.getCashId());
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_产品付费.getValue());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());

        trdFinProductDealTotal.setRemark(entity.getRemark());
        trdFinProductDealTotal.setUpdateUser(entity.getUpdateUser());
        trdFinProductDealTotal.setCreateUser(entity.getCreateUser());
        trdFinProductDealTotal.setCreateDept(entity.getCreateDept());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());

        trdFinProductDealTotal.setFinprodType(entity.getFeeType());
        trdFinProductDealTotal.setFinprodType2(entity.getFeeType());
//        if(StringUtil.equals(entity.getChargeType(), DictCodeList.收费类型_不规则费用.getValue())) {
//            trdFinProductDealTotal.setMdate(entity.getPayDate());
//        }

        return trdFinProductDealTotal;
    }

    /**
     * 生成划款指令信息
     *
     * @param entityDto 产品费用支付Dto
     * @param tradeId 交易编号
     * @return 划款指令信息
     * @see
     * @since 1.0
     */
    private StlPayOrders genStlPayOrdersDto(FinProductFeePay005Dto entityDto, String tradeId) {
        entityDto.setBussTrdTransferOrder(
                bussTrdTransferOrderService.save(entityDto.getBussTrdTransferOrder()));
        // 划款指令表Entity
        StlPayOrders stlPayOrders = new StlPayOrders();
        if (!StringUtil.isEmptyList(entityDto.getBussTrdTransferOrder())) {
            BussTrdTransferOrderDto bussTrdTransferOrderDto = entityDto.getBussTrdTransferOrder().get(
                    0);
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, bussTrdTransferOrderDto);
            stlPayOrders.setTradeId(tradeId);
            stlPayOrders.setWorkflowId(entityDto.getWorkflowId());

            List<? extends DataDictItem> yesNoItemList = SysDataUtil.getDataDictItem(
                    DictCode.是否.getValue());
            yesNoItemList.forEach(item -> {
                if (StringUtil.equals(item.getItemValue(), stlPayOrders.getIsNeedSend())) {
                    stlPayOrders.setIsNeedSend(item.getItemCode());
                }
            });
        }
        stlPayOrders.setCreateTime(entityDto.getCreateTime());
        return stlPayOrders;
    }

    /**
     * 生成划款指令信息
     *
     * @param entityDto 产品费用支付Dto
     * @param tradeId 交易编号
     * @return 划款指令信息
     * @see
     * @since 1.0
     */
    private List<StlPayOrders> genStlPayOrdersDtos(FinProductFeePay005Dto entityDto, String tradeId) {
        entityDto.setBussTrdTransferOrder(
                bussTrdTransferOrderService.save(entityDto.getBussTrdTransferOrder()));
        // 划款指令表Entity
        List<StlPayOrders> stlPayOrdersList = new ArrayList<>();
        if (!StringUtil.isEmptyList(entityDto.getBussTrdTransferOrder())) {
            for (BussTrdTransferOrderDto bussTrdTransferOrderDto:entityDto.getBussTrdTransferOrder()) {
                StlPayOrders stlPayOrders = new StlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, bussTrdTransferOrderDto);
                stlPayOrders.setTradeId(tradeId);
                stlPayOrders.setWorkflowId(entityDto.getWorkflowId());

                List<? extends DataDictItem> yesNoItemList = SysDataUtil.getDataDictItem(
                        DictCode.是否.getValue());
                yesNoItemList.forEach(item -> {
                    if (StringUtil.equals(item.getItemValue(), stlPayOrders.getIsNeedSend())) {
                        stlPayOrders.setIsNeedSend(item.getItemCode());
                    }
                });
                stlPayOrders.setCreateTime(entityDto.getCreateTime());
                stlPayOrdersList.add(stlPayOrders);
            }
        }
        return stlPayOrdersList;
    }

    /**
     * 金融产品交易共通业务逻辑处理
     *
     * @param entity       产品费用支付Dto
     * @param stlPayOrders 指令信息
     * @return 金融产品交易共通
     * @see
     * @since 1.0
     */
    private FinProductExpireBaseTrdDto genBaseTradeDealDtoList(FinProductFeePay002Dto entity,
                                                               StlPayOrders stlPayOrders) {
        FinProductExpireBaseTrdDto finProductExpireBaseTrdDto = new FinProductExpireBaseTrdDto();

        List<TrdProductDeal> trdProductDealList = new ArrayList<>();
        List<TrdProductDealAdd> trdProductDealAddList = new ArrayList<>();
        List<PaTrdProductDeal> paTrdProductDealList = new ArrayList<>();
        List<TrdTradeFeeDetail> feeDetailList = new ArrayList<TrdTradeFeeDetail>();

        if (!StringUtil.isEmptyList(entity.getFeeInfoList())) {
            for (FinProductFeePay003Dto finProductFeePay003Dto : entity.getFeeInfoList()) {
                if (StringUtil.isNotEmpty(finProductFeePay003Dto.getsFinprodId())) {
                    finProductFeePay003Dto.setFinprodId(finProductFeePay003Dto.getsFinprodId());
                    Map<String, Object> map = new HashMap<>();
                    map.put("feeId", entity.getFeeId());
                    map.put("finprodId", finProductFeePay003Dto.getsFinprodId());
                    map.put("cashType1", DictCodeListExt1.现金流一级类型_费用.getValue());
                    String cahsId = restTemplate.postForObject(
                            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectCashId", map, String.class);
                    entity.setCashId(cahsId);

                }
                // 转换金融交易共通dto
                BaseTradeProductDealDto baseTradeProductDealDto = this.convertBaseTrade(entity,
                        finProductFeePay003Dto.getFinprodId(), finProductFeePay003Dto.getActPayAmt(), finProductFeePay003Dto.getPayAmt(), finProductFeePay003Dto.getIsClean(), finProductFeePay003Dto.getAdjustAmt());
                // 金融产品交易
                trdProductDealList.add(baseTradeProductDealDto.getTrdProductDeal());
                trdProductDealAddList.add(baseTradeProductDealDto.getTrdProductDealAdd());
                paTrdProductDealList.add(baseTradeProductDealDto.getPaTrdProductDeal());
                feeDetailList.addAll(baseTradeProductDealDto.getTrdTradeFeeDetailList());
            }
        } else {

            if (!StringUtil.isEmptyList(entity.getFeelayeredInfoList())) {
                //添加不规则费用逻辑
                for (FinProductFeePay003Dto finProductFeePay003Dto : entity.getFeelayeredInfoList()) {

                    if (StringUtil.isNotEmpty(finProductFeePay003Dto.getsFinprodId())) {
                        finProductFeePay003Dto.setFinprodId(finProductFeePay003Dto.getsFinprodId());
                    }

                    // 转换金融交易共通dto
                    BaseTradeProductDealDto baseTradeProductDealDto = this.convertBaseTrade(entity,
                            finProductFeePay003Dto.getFinprodId(), finProductFeePay003Dto.getActPayAmt(), finProductFeePay003Dto.getPayAmt(), finProductFeePay003Dto.getIsClean(), finProductFeePay003Dto.getAdjustAmt());
                    // 金融产品交易
                    trdProductDealList.add(baseTradeProductDealDto.getTrdProductDeal());
                    trdProductDealAddList.add(baseTradeProductDealDto.getTrdProductDealAdd());
                    paTrdProductDealList.add(baseTradeProductDealDto.getPaTrdProductDeal());
                    feeDetailList.addAll(baseTradeProductDealDto.getTrdTradeFeeDetailList());
                }
            }

            // 转换金融交易共通dto
            BaseTradeProductDealDto baseTradeProductDealDto = this.convertBaseTrade(entity,
                    entity.getFinprodId(), stlPayOrders.getSettleAmt(), null, null, null);

            trdProductDealList.add(baseTradeProductDealDto.getTrdProductDeal());
            trdProductDealAddList.add(baseTradeProductDealDto.getTrdProductDealAdd());
            paTrdProductDealList.add(baseTradeProductDealDto.getPaTrdProductDeal());

            if (!StringUtil.isEmptyList(entity.getTaxPositionList())) {
                // 增值税列表处理
                List<PtlFeePosition> positionList = restTemplate.getForList(
                        ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryTaxUnpayPosition?ptlId="
                                + entity.getPtlId()
                                + "&cdate="
                                + DateUtil.format(
                                entity.getEndDate()),
                        PtlFeePosition.class);
                if (!StringUtil.isEmptyList(positionList)) {
                    for (PtlFeePosition ptlFeePosition : positionList) {
                        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
                        trdTradeFeeDetail.setTradeId(
                                baseTradeProductDealDto.getTrdProductDeal().getTradeId());
                        trdTradeFeeDetail.setFeeType(ptlFeePosition.getPtlFeeType());
                        trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                        trdTradeFeeDetail.setFeeAmt(ptlFeePosition.getTdyAccumulPayFee());
                        trdTradeFeeDetail.setFeeId(entity.getFeeId());
                        trdTradeFeeDetail.setUpdateUser(entity.getUpdateUser());
                        trdTradeFeeDetail.setFeeType(entity.getFeeType());
                        trdTradeFeeDetail.setCreateUser(entity.getCreateUser());
                        trdTradeFeeDetail.setCreateDept(entity.getCreateDept());
                        trdTradeFeeDetail.setWorkflowId(entity.getWorkflowId());
                        feeDetailList.add(trdTradeFeeDetail);
                    }
                }
                baseTradeProductDealDto.setTrdTradeFeeDetailList(feeDetailList);
            }
        }

        finProductExpireBaseTrdDto.setTrdProductDealList(trdProductDealList);
        finProductExpireBaseTrdDto.setTrdProductDealAddList(trdProductDealAddList);
        finProductExpireBaseTrdDto.setPaTrdProductDealList(paTrdProductDealList);
        finProductExpireBaseTrdDto.setTrdTradeFeeDetailList(feeDetailList);

        return finProductExpireBaseTrdDto;
    }

    /**
     * 生成组合交易表信息
     *
     * @param entity 产品费用支付Dto
     * @return 组合交易表信息
     * @see
     * @since 1.0
     */
    private PtlTrade genPtlTrdea(FinProductFeePay002Dto entity) {
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(entity.getPtlId());
        ptlTrade.setBusiNo(entity.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(entity.getPtlId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_产品费用支付管理.getValue());
        ptlTrade.setSettleDate(entity.getPayDate());
        ptlTrade.setUpdateUser(entity.getUpdateUser());
        ptlTrade.setCreateUser(entity.getCreateUser());
        ptlTrade.setCreateDept(entity.getCreateDept());

        return ptlTrade;
    }

    /**
     * 生成组合交易表信息
     *
     * @param entity 产品费用支付Dto
     * @return 组合交易表信息
     * @see
     * @since 1.0
     */
    private PtlTrade genPtlTrdea(FinProductFeePay005Dto entity) {
        PtlTrade ptlTrade = new PtlTrade();
        if (!StringUtil.isEmptyList(entity.getFinProductFeePay())) {
            ptlTrade.setPortfolioId(entity.getFinProductFeePay().get(0).getPtlId());
            ptlTrade.setBusiId(entity.getFinProductFeePay().get(0).getPtlId());
        }
        ptlTrade.setBusiNo(entity.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_产品费用支付管理.getValue());
        ptlTrade.setSettleDate(entity.getPayDate());
        ptlTrade.setUpdateUser(entity.getUpdateUser());
        ptlTrade.setCreateUser(entity.getCreateUser());
        ptlTrade.setCreateDept(entity.getCreateDept());

        return ptlTrade;
    }

    /**
     * 新建确认doComplete
     *
     * @param entity 产品费用支付Dto
     * @see
     * @since 1.0
     */
    private void doCompleteInsert(FinProductFeePay002Dto entity, FinProductFeePay005Dto entityDto) {
        this.setFeeCashId(entity);
        FinProductFeePay003Dto finProductFeePay003Dto = new FinProductFeePay003Dto(); 
        // 转换费用列表
        finProductFeePay003Dto.setFinprodId(entity.getFinprodId());
        finProductFeePay003Dto.setsFinprodId(entity.getsFinprodId());
        finProductFeePay003Dto.setActPayAmt(entity.getActPayAmt());
        finProductFeePay003Dto.setPayAmt(entity.getPayAmt());
        finProductFeePay003Dto.setIsClean(entity.getIsClean());
        List<FinProductFeePay003Dto> finProductFeePay003DtoList = new ArrayList<FinProductFeePay003Dto>();
        finProductFeePay003DtoList.add(finProductFeePay003Dto);
        entity.setFeeInfoList(finProductFeePay003DtoList);
        // 划款指令
//        if (!StringUtil.isEmptyList(entityDto.getBussTrdTransferOrder())) {
//            entityDto.getBussTrdTransferOrder().get(0).setSettleAmt(entity.getActPayAmt());
//            entityDto.getBussTrdTransferOrder().get(0).setPortfolioId(entity.getPtlId());
//        }
        StlPayOrders stlPayOrders = this.genStlPayOrdersDto(entityDto, entity.getTradeId());
        if (baseGetBussinessId != null) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setSeqNo("9998");
            stlPayOrders.setOrderId(baseGetBussinessId.buildBussinessCode(bussinessCodeParameter));
        } else {
            stlPayOrders.setOrderId(IDGenerator.generate());
        }
        // 金融产品交易汇总表
        TrdFinProductDealTotal trdFinProductDealTotal = this.genTrdFinProductDealTotal(entity,
                stlPayOrders.getSettleAmt());

        //pa 金融产品交易汇总表
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(paTrdFinProductDealTotal, trdFinProductDealTotal);
        paTrdFinProductDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品费用支付.getValue());
        //插入pa 金融产品交易汇总表
        //  bussCommonMapperDelegateService.insertSelective(PaTrdFinProductDealTotalMapper.class, paTrdFinProductDealTotal);

        entity.setPayDate(entityDto.getPayDate());
        FinProductExpireBaseTrdDto finProductExpireBaseTrdDto = this.genBaseTradeDealDtoList(
                entity, stlPayOrders);


//            if(StringUtil.isNotEmpty(finProductExpireBaseTrdDto)){
//            	TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
//                trdTradeFeeDetail.setTradeId(trdFinProductDealTotal.getTotleTradeId());
//                trdTradeFeeDetail.setFeeType(entity.getFeeType());
//                trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
//                trdTradeFeeDetail.setFeeAmt(entity.getActPayAmt());
//                trdTradeFeeDetail.setFeeId(entity.getFeeId());
//                trdTradeFeeDetail.setUpdateUser(entity.getUpdateUser());
//                trdTradeFeeDetail.setCreateUser(entity.getCreateUser());
//                trdTradeFeeDetail.setCreateDept(entity.getCreateDept());
//                trdTradeFeeDetail.setWorkflowId(entity.getWorkflowId());
//                finProductExpireBaseTrdDto.getTrdTradeFeeDetailList().add(trdTradeFeeDetail);
//            	if(finProductExpireBaseTrdDto.getTrdProductDealList().size()>0){
//                    trdFinProductDealTotal.setCashId(finProductExpireBaseTrdDto.getTrdProductDealList().get(0).getCashId());
//
//            	}
//
//            }


        // 组合交易表
//        PtlTrade ptlTrade = this.genPtlTrdea(entity);
//        
//    	stlPayOrders.setTradeId(entity.getTradeId());
        //	bussCommonMapperDelegateService.insertSelective(StlPayOrdersMapper.class,
        // 		stlPayOrders);

        // 批量插入
        restTemplate.postForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/insertBatchData", finProductExpireBaseTrdDto, String.class);

//        // 组合现金流水Entity
//        List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<PtlCashPositionFlowDto>();
//        PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
//        ptlCashPositionFlowDto.setPortfolioId(entity.getPtlId());
//        ptlCashPositionFlowDto.setAcctId(stlPayOrders.getMyAcctNo());
//        ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
//        ptlCashPositionFlowDto.setCdate(entity.getPayDate());
//        ptlCashPositionFlowDto.setCcy(stlPayOrders.getCcy());
//        ptlCashPositionFlowDto.setAmount(stlPayOrders.getSettleAmt().negate());
//        ptlCashPositionFlowDto.setOrderId(stlPayOrders.getOrderId());
//        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
//
//        PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
//        ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
//        restTemplate.postForObject(
//            ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
//            ptlPositionHoldDto, Boolean.class);

        // 一体化对应etliu start
        // 所有交易生效后调用的共通方法
        if (!StringUtil.isEmptyList(finProductExpireBaseTrdDto.getTrdProductDealList())) {
            String profitType = null; // 收益类型

            // 查询组合收益类型
            PtlPortfolio ptlPortfolio = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                    PtlPortfolioMapper.class, entity.getPtlId(), PtlPortfolio.class);
            if (ptlPortfolio != null) {
                profitType = ptlPortfolio.getProfitType();
            }

            // 查询理财产品代码
            String finprodId = valuationInterfService.selectFinprodIdByPortfolioId(entity.getPtlId());

            for (TrdProductDeal tradeDto : finProductExpireBaseTrdDto.getTrdProductDealList()) {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(entity.getFinprodId());
                if (StringUtil.isBlank(inputDto.getProdid())) {
                    inputDto.setProdid(tradeDto.getFinprodId());
                }
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                //是否计提费用
                boolean jitiFeeFlg = false;
                TrdTradeFeeDetail feeInfo = new TrdTradeFeeDetail();
                if (finProductExpireBaseTrdDto.getTrdTradeFeeDetailList() != null
                        && finProductExpireBaseTrdDto.getTrdTradeFeeDetailList().size() > 0) {
                    for (TrdTradeFeeDetail fee : finProductExpireBaseTrdDto.getTrdTradeFeeDetailList()) {
                        if (fee.getTradeId().equals(tradeDto.getTradeId())) {
                            feeInfo = fee;
                            break;
                        }
                    }
                    if (feeInfo != null && StringUtils.isNotEmpty(feeInfo.getFeeId())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("feeId", feeInfo.getFeeId());
                        MstFeeinfo info = restTemplate.postForObject(
                                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectFeeId", map, MstFeeinfo.class);
                        if (info != null && DictCodeList.收费类型_计提式费用.getValue().equals(info.getChargeType())) {
                            jitiFeeFlg = true;
                        } else {
                            jitiFeeFlg = false;
                        }
                    }
                }
                if (jitiFeeFlg && !(StringUtil.equals(DictCodeList.收益类型_收益型.getValue(), profitType)
                        && StringUtil.equals(entity.getFeeType(),
                        DictCodeList.费用品种类型_浮动差额中收.getValue()))) {
                    inputDto.setProdid(tradeDto.getFinprodId());
                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                }
                // 货币、净值型产品浮动管理或者增值附加税费支付：产品代码应该传理财产品代码（F16_CYFS190004）而不是投资组合代码(CYFS190004)
                else if (!StringUtil.equals(DictCodeList.收益类型_收益型.getValue(), profitType)
                        && (StringUtil.equals(entity.getFeeType(),
                        DictCodeList.费用品种类型_浮动管理费.getValue())
                        || StringUtil.equals(entity.getFeeType(),
                        DictCodeList.费用品种类型_增值附加税费.getValue()))) {
                    inputDto.setProdid(finprodId);
                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                }
                // 预期收益产品中收支付：产品代码应该传投资组合代码，产品类型应该传投资组合(F18）
                else if (StringUtil.equals(DictCodeList.收益类型_收益型.getValue(), profitType)
                        && StringUtil.equals(entity.getFeeType(),
                        DictCodeList.费用品种类型_浮动差额中收.getValue())) {
                    inputDto.setProdid(entity.getPtlId());
                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                }
 
                inputDto.setTrdProductDeal(tradeDto);
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            }
        }
        // 一体化对应etliu end

        // 删除原业务流程记录
        this.deleteBussFlowData(entityDto.getWorkflowId());
    }

    /**
     * 根据汇总交易编号查询交易信息
     *
     * @param tradeId 汇总交易编号
     * @return 交易信息
     * @see
     * @since 1.0
     */
    public List<TrdProductDeal> queryTrdProductDealByTotleTradeId(String tradeId) {
        return restTemplate.getForList(ServiceApplication.PRODUCT_SERVICE_NAME
                        + "/FinProductCommon/queryTrdProductDealByTotleTradeId?tradeId="
                        + tradeId,
                TrdProductDeal.class);
    }


    /**
     * 查询组合下产品关联的费用
     *
     * @param map 参数
     * @return 关联的费用
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryFeeInfoList(Map<String, Object> map) {
        return restTemplate.postForList(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryFeeInfoList", map,
                SelectOptionDto.class);
    }

    /**
     * 转换金融交易共通dto
     *
     * @param entity    费用明细
     * @param finprodId 金融产品代码
     * @param actPayAmt 实付金额
     * @return 交易共通dto
     * @see
     * @since 1.0
     */
    private BaseTradeProductDealDto convertBaseTrade(FinProductFeePay002Dto entity,
                                                     String finprodId, BigDecimal actPayAmt, BigDecimal payAmt, String isClean, BigDecimal adjustAmt) {
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        List<TrdTradeFeeDetail> feeDetailList = new ArrayList<TrdTradeFeeDetail>();
        // 金融产品交易Entity
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_产品费用支付管理);
            trdProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_产品费用支付管理.getValue());

//        trdProductDeal.setFinprodType(
//            productCommonService.convertFeeTypeToCashType(entity.getFeeType()));
        String tempFinprodType = productCommonService.convertFeeTypeToCashType(entity.getFeeType());
        if (StringUtil.isEmpty(tempFinprodType)) {
            tempFinprodType = entity.getFeeType();
        }

        //add by leipeng 20190706 如果有子代码则使用分层编号
        if (StringUtil.isNotEmpty(entity.getsFinprodId())) {
            finprodId = entity.getsFinprodId();
            //子分层直接查询的现金流二级类型..如果查不到则直接使用原费用
            tempFinprodType = entity.getFeeType();
        }
        trdProductDeal.setFinprodId(finprodId);

        trdProductDeal.setFinprodType(tempFinprodType);
        trdProductDeal.setFinprodType2(trdProductDeal.getFinprodType());
        trdProductDeal.setBranch(
                Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        trdProductDeal.setTradeDate(entity.getPayDate());
        trdProductDeal.setVdate(entity.getStartDate());
        trdProductDeal.setMdate(entity.getEndDate());
        trdProductDeal.setSettleDate(entity.getPayDate());
        trdProductDeal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        trdProductDeal.setPrinAmt(actPayAmt);
        trdProductDeal.setFpriceAmt(actPayAmt);
        trdProductDeal.setTradeAmt(actPayAmt);
        trdProductDeal.setFeeAmt(actPayAmt);
        trdProductDeal.setPs(DictCodeList.交易方向_产品付费.getValue());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        trdProductDeal.setIsClean(isClean);
        trdProductDeal.setTotleTradeId(entity.getTradeId());
        trdProductDeal.setCashId(entity.getCashId());
        trdProductDeal.setRegistOrg(entity.getRegTrustOrg());
        trdProductDeal.setRemark(entity.getRemark());
        trdProductDeal.setUpdateUser(entity.getUpdateUser());
        trdProductDeal.setCreateUser(entity.getCreateUser());
        trdProductDeal.setCreateDept(entity.getCreateDept());
        trdProductDeal.setWorkflowId(entity.getWorkflowId());
        if (StringUtil.equals(entity.getChargeType(), DictCodeList.收费类型_不规则费用.getValue())) {
            trdProductDeal.setMdate(entity.getPayDate());
            trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
        }
        // 金融产品交易附属表Entity
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
        trdProductDealAdd.setPortfolioId(entity.getPtlId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        trdProductDealAdd.setUpdateUser(entity.getUpdateUser());
        trdProductDealAdd.setCreateUser(entity.getCreateUser());
        trdProductDealAdd.setCreateDept(entity.getCreateDept());
        trdProductDealAdd.setWorkflowId(entity.getWorkflowId());
        // 金融产品交易扩展Entity
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(trdProductDeal.getTradeId());
        paTrdProductDeal.setTheoryAmt(payAmt);
        paTrdProductDeal.setRemainPrinAmt(adjustAmt);
        paTrdProductDeal.setTheoryInt(payAmt);
        //add by gcl 2019/3/18 start
        paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_产品费用支付.getValue());
        if (StringUtil.equals(DictCodeList.费用品种类型_销售手续费.getValue(), entity.getFeeType())) {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品销售费支付.getValue());
        } else if (StringUtil.equals(DictCodeList.费用品种类型_固定管理费.getValue(), entity.getFeeType())) {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品管理费支付.getValue());
        } else if (StringUtil.equals(DictCodeList.费用品种类型_托管费.getValue(), entity.getFeeType())) {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品托管费支付.getValue());
        } else {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品其他费用.getValue());

        }
        //add by gcl 2019/3/18 end
        // 交易费用明细Entity
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
        trdTradeFeeDetail.setFeeType(tempFinprodType);
        trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
        trdTradeFeeDetail.setFeeAmt(actPayAmt);
        trdTradeFeeDetail.setFeeId(entity.getFeeId());
        trdTradeFeeDetail.setUpdateUser(entity.getUpdateUser());
        trdTradeFeeDetail.setCreateUser(entity.getCreateUser());
        trdTradeFeeDetail.setCreateDept(entity.getCreateDept());
        trdTradeFeeDetail.setWorkflowId(entity.getWorkflowId());
        feeDetailList.add(trdTradeFeeDetail);

        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
        baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品交易附属表
        baseTradeProductDealDto.setPaTrdProductDeal(paTrdProductDeal);// 金融产品交易扩展
        baseTradeProductDealDto.setTrdTradeFeeDetailList(feeDetailList);// 交易费用明细表

        return baseTradeProductDealDto;
    }

    /**
     * 删除业务流程数据
     *
     * @param workflowId 工作流代码
     * @see
     * @since 1.0
     */
    private void deleteBussFlowData(String workflowId) {
        baseTradeProductDealCommService.deleteWflData(workflowId);
    }

    /**
     * 费用交易列表
     *
     * @param map 参数
     * @return 费用交易列表
     * @see
     * @since 1.0
     */
    public List<FinProductFeePay002Dto> queryprodInfoList(Map<String, Object> map) {
        List<FinProductFeePay001Dto> dtoList = null;
        List<FinProductFeePay002Dto> resList = new ArrayList<FinProductFeePay002Dto>();
        try {
            dtoList = JsonUtils.json2List((String) map.get("finProductFee"), FinProductFeePay001Dto.class);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        if (CollectionUtils.isNotEmpty(dtoList)) {
            for (FinProductFeePay001Dto dto : dtoList) {

                HashMap<Object, Object> requestMap = new HashMap<>();
                requestMap.put("ptlId", dto.getPtlId());
                requestMap.put("feeType", dto.getFeeType());
                requestMap.put("calcEndDate", DateUtil.format(dto.getPositionEndDate()));
                // add by jiangpeng on 20210507 start
                // TISHKBS-2109 【产品费用支付】-待确认数据进行确认时，报错
                requestMap.put("calcStartDate", DateUtil.format(dto.getPositionStartDate()));
                // add by jiangpeng on 20210507 end
                requestMap.put("finprodId", dto.getFinprodId());
                requestMap.put("feeId", dto.getFeeId());
                requestMap.put("flag", DictCodeList.是否_是.getValue());
                //add by yzg 20191227 过滤净值型分档产品子产品 start
                requestMap.put("cashId", dto.getCashId());
                //add by yzg 20191227 过滤净值型分档产品子产品 end


                // 查询产品费用头寸表
//	            String cashType2 = restTemplate.getForObject(
//	                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectCashType2?finprodId="
//	                            + res.getFinprodId()
//	                            + "&feeId="
//	                            + res.getFeeId(),
//	                            String.class);
//	            
//	          //如果产品无数据可能是分层产品
//	            if(!StringUtil.isNotEmpty(cashType2)){
//	            	cashType2 = restTemplate.getForObject(
//	                        ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectLayyerdCashType2?finprodId="
//	                                + map.get("finprodId")
//	                                + "&feeId="
//	                                + map.get("feeId"),
//	                                String.class);
//	            }
//	            
//	            
//	            requestMap.put("feeType", cashType2);
                List<FinProductFeePay003Dto> list = restTemplate.postForList(
                        ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryFeePositionByPtlLayeredId",
                        requestMap, FinProductFeePay003Dto.class);
                if (!StringUtil.isEmptyList(list)) {
                    for (FinProductFeePay003Dto payinfo : list) {
                        FinProductFeePay002Dto res = new FinProductFeePay002Dto();
                        BeanUtil.copyPropertiesIgnoreNull(res, dto);
                        res.setStartDate(dto.getPositionStartDate());
                        res.setEndDate(dto.getPositionEndDate());
                        res.setPayAmt(payinfo.getActPayAmt());
                        res.setActPayAmt(payinfo.getActPayAmt());
                        res.setAdjustAmt(BigDecimal.ZERO);
                        res.setBranch(payinfo.getBranch());
                        res.setsFinprodId(payinfo.getsFinprodId());
                        res.setBasicBill(payinfo.getBasicBill());
                        String sFinprodId = payinfo.getsFinprodId();
                        if (StringUtil.isEmpty(sFinprodId)) {
                            sFinprodId = payinfo.getFinprodId();
                        }
                        BigDecimal unclearAmt = restTemplate.getForObject(
                                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryUnCleanFee?ptlId="
                                        + dto.getPtlId()
                                        + "&feeId="
                                        + dto.getFeeId()
                                        + "&payDate="
                                        + DateUtil.format(dto.getPayDate())
                                        + "&finprodId="
                                        + sFinprodId,
                                BigDecimal.class);
                        res.setUnclearAmt(unclearAmt);
                        //处理理论金额  BS19
                        //                res  =  handlePayAmt(res);
                        resList.add(res);
                    }

                }

            }

        }
        return resList;
    }

    private FinProductFeePay002Dto handlePayAmt(FinProductFeePay002Dto dto) {
        BigDecimal payAmt = BigDecimal.ZERO;
        List<FinProductFeePay003Dto> resultList = finProductFeePay002Service.queryFeePositionDetail(dto.getFinprodId(),
                dto.getStartDateStr(), dto.getEndDateStr(), dto.getFeeId(), dto.getBranch() == null ? null : Long.toString(dto.getBranch()),
                dto.getFeeType());
        if (null != resultList && !resultList.isEmpty()) {
            for (FinProductFeePay003Dto entity : resultList) {
                payAmt = ArithmeticUtil.doAdd(payAmt, entity.getPayAmt());
            }
        }
        dto.setPayAmt(payAmt);
        dto.setActPayAmt(payAmt);
        return dto;
    }

}
