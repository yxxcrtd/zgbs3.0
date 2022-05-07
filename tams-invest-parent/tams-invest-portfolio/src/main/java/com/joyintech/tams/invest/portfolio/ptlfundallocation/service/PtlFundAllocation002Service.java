/*
 * 文件名：PtlFundAllocation002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2019年1月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.portfolio.ptlfundallocation.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.interf.service.InterfComponentService;
import com.joyintech.tams.invest.portfolio.ptlfundallocation.dto.PtlFundAllocationDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 银证转账新建
 * @author fanshuanghu
 * @version 1.0 2019年1月30日
 * @see PtlFundAllocation002Service
 * @since 1.0
 */
@Service("PtlFundAllocation002Service")
@Transactional(rollbackFor=Exception.class)
public class PtlFundAllocation002Service extends CommonExportService<PtlFundAllocationDto>
        implements BaseProcessService<PtlFundAllocationDto>{
    /**
     * 基础Mapper代理Service
     */
    @Autowired(required=false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    /**
     * 业务编号生成类
     */
    @Autowired(required =false)
    public BaseGetBusinessId baseGetBussinessId;
    /**
     * 划款指令
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * {@linkplain}
     */
    @Autowired
    private InterfComponentService interfComponentService;
    
   


    /**
     * 查询组合
     * @return 组合集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortfolioId(Map<String,Object> map){
        return restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getPortfolioId", map, SelectOptionDto.class);
    }

    /**
     * 查询收款账号
     * @return 收款账号集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCounterAcctNo(Map<String,Object> map){
        return restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getCounterAcctNo", map, SelectOptionDto.class);
    }

    /**
     * 查询付款账号
     * @return 付款账号集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getMyAcctNo(Map<String,Object> map){
        return restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getMyAcctNo", map, SelectOptionDto.class);
    }

    /**
     * 获取账户余额
     * @return 账户余额
     * @see
     * @since 1.0
     */
    public BigDecimal getAmount(Map<String,Object> map){
        return restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getAmount", map, BigDecimal.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(PtlFundAllocationDto entity, OperationType type) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPositionType(entity.getOperateType());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        PtlFundAllocationDto dto = this.setEntity(entity, type);
        dto.setOperateType(type.toString());
        restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doComplete", dto, Integer.class);

//        if(StringUtil.equals(entity.getOperateType(), "NEW")){
//            //调用托管网银接口
//            List<StlPayOrders> stlPayOrdersList = new ArrayList<StlPayOrders>();
//            entity.getBussTrdTransferOrder().forEach(item -> {
//            	item.setPs(entity.getPs());
//                stlPayOrdersList.add(item);
//            });
//            interfComponentService.sendCusdepOrder(stlPayOrdersList, false);
//        }
    }

    @Override
    public void doDelete(String workflowId) {
        PtlFundAllocationDto entity = this.getBussinessEntityByWorkflowId(workflowId);
        entity.setPositionType(entity.getOperateType());
        if (StringUtil.isNotEmpty(entity.getOrderId())) {
            restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doDelete", entity, String.class);
        }
    }

    @Override
    public void doInsert(PtlFundAllocationDto entity, OperationType type) {
        entity.setActPortfolioId(entity.getPortfolioId());
        if(StringUtils.isEmpty(entity.getPositionType())){
            entity.setPositionType(entity.getOperateType());
        }
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        this.setEntity(entity, type);
        restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doInsertAction", entity,
                String.class);
    }

    @Override
    public void doUpdate(PtlFundAllocationDto entity){
        if(bussCommonMapperDelegateService != null){
            if(!StringUtil.equals("save", entity.getOperateType())){
                entity.setPositionType("update");
            } else{
                entity.setPositionType(entity.getOperateType());
            }
            restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doDelete", entity, String.class);
            doInsert(entity, OperationType.NEW);
        }
    }

    @Override
    public void doBack(PtlFundAllocationDto entity) {
        AllTradeCommonUtil.deletePositionByOrderId(entity.getOrderId());
        restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doBack", entity,
                String.class);
    }

    @Override
    public void doRevoke(PtlFundAllocationDto entity) {
        AllTradeCommonUtil.deletePositionByOrderId(entity.getOrderId());
        restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doBack", entity,
                String.class);
    }

    /**
     * 实体类的合成
     * @param dto 资金划拨实体类
     * @param type
     * @return 资金划拨实体类
     * @see
     * @since 1.0
     */
    public PtlFundAllocationDto setEntity(PtlFundAllocationDto dto, OperationType type){
        // 先处理付款账户，生成可用持仓流水，生效后处理成实际及收款账户
        /**
         * 组合现金流水
         */
        PtlCashPositionFlow ptlCashPositionFlow = new PtlCashPositionFlow();
        ptlCashPositionFlow.setPortfolioId(dto.getPortfolioId());
        ptlCashPositionFlow.setAcctId(dto.getMyAcctNo());
        ptlCashPositionFlow.setAmount(dto.getSettleAmt());
        ptlCashPositionFlow.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
//        if (DictCodeListExt1.指令类型_银证转账_银转证.getValue().equals(dto.getPayType())) {
        ptlCashPositionFlow.setAmount(ArithmeticUtil.multiply(dto.getSettleAmt(),new BigDecimal("-1")));
//        }
        if (OperationType.REVOKE == type) {
            ptlCashPositionFlow.setAmount(ArithmeticUtil.multiply(ptlCashPositionFlow.getAmount(),new BigDecimal("-1")));
        }
        ptlCashPositionFlow.setCcy(dto.getCcy());
        ptlCashPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
        ptlCashPositionFlow.setCdate(dto.getSettleDate());
        ptlCashPositionFlow.setOrderId(dto.getBussTrdTransferOrder().get(0).getOrderId());
        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        bussinessCodeParameter.setSeqNo("9999");
        String seqno = baseGetBussinessId !=null ? baseGetBussinessId.buildBussinessCode(bussinessCodeParameter):IDGenerator.generate();
        if(dto.getPtlCashPositionFlow() != null && StringUtil.isNotEmpty(dto.getPtlCashPositionFlow().getSeqno())){
            ptlCashPositionFlow.setSeqno(dto.getPtlCashPositionFlow().getSeqno());
        }
        else{
            ptlCashPositionFlow.setSeqno(seqno);
        }
        dto.setPtlCashPositionFlow(ptlCashPositionFlow);

        /**
         * 组合现金余额
         */
        PtlCashPosition ptlCashPosition = new PtlCashPosition();
        ptlCashPosition.setAcctId(dto.getMyAcctNo());
        ptlCashPosition.setAmount(ptlCashPositionFlow.getAmount());
        ptlCashPosition.setCcy(dto.getCcy());
        ptlCashPosition.setPortfolioId(dto.getPortfolioId());
        ptlCashPosition.setHodingType(DictCodeList.持仓类型_实际.getValue());
        ptlCashPosition.setCdate(dto.getSettleDate());
        dto.setPtlCashPosition(ptlCashPosition);

        /**
         * 组合交易
         */
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        ptlTrade.setBusiNo(dto.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
        // 一体化对应etliu start
//        ptlTrade.setBusiType(DictCodeList.划款类型_托管划款.getValue());
        ptlTrade.setBusiType(DictCodeListExt1.平安划款类型_托管划款.getValue());
        // 一体化对应etliu end
        dto.setPtlTrade(ptlTrade);
        /**
         * 金融产品交易
         */
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(dto.getTradeId());
//        trdProductDeal.setTradeStatus(DictCodeListExt1.画面数据状态_有效.getValue());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setCcy(dto.getCcy());

//      trdProductDeal.setPs(dto.getPayType());
        if (StringUtil.equals(dto.getPayType(), DictCodeListExt1.指令类型_银证转账_银转证.getValue())) {
            // 银证转账
            trdProductDeal.setPs(DictCodeList.交易方向_银证转账.getValue());
        }
        else if (StringUtil.equals(dto.getPayType(),
            DictCodeListExt1.指令类型_银证转账_证转银.getValue())) {
            // 证银转账
            trdProductDeal.setPs(DictCodeList.交易方向_证银转账.getValue());
        }

        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        trdProductDeal.setTradeDate(dto.getSettleDate());
        trdProductDeal.setSettleDate(dto.getSettleDate());
        trdProductDeal.setRemark(dto.getRemark());
        trdProductDeal.setTradeAmt(dto.getSettleAmt());
        dto.setTrdProductDeal(trdProductDeal);

        // 交易附属表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        dto.setTrdProductDealAdd(trdProductDealAdd);

        /**
         * 金融产品交易扩展表
         */
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(trdProductDeal.getTradeId());
        if (StringUtil.equals(DictCodeListExt1.指令类型_银证转账_银转证.getValue(), dto.getPayType())) {
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_资金划拨银证转账.getValue());
        }
        else if (StringUtil.equals(DictCodeListExt1.指令类型_银证转账_证转银.getValue(), dto.getPayType())) {
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_资金划拨证银转账.getValue());
        }
        dto.setPaTrdProductDeal(paTrdProductDeal);

        if(bussTrdTransferOrderService != null){
            dto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, dto.getBussTrdTransferOrder().get(0));
            stlPayOrdersDto.setTradeId(dto.getTradeId());
            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
            list.add(stlPayOrdersDto);
            dto.setStlPayOrdersList(list);
        }
        dto.setOperateType("NEW");
        dto.setRemark(dto.getRemark());

        return dto;
    }

    /**
     * 获取账户余额
     * @return 账户余额
     * @see
     * @since 1.0
     */
    public BigDecimal getActAmount(Map<String,Object> map){
        return restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getActAmount", map, BigDecimal.class);
    }
}
