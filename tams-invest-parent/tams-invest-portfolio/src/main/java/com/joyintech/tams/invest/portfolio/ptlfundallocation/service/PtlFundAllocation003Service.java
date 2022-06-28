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

import com.google.common.collect.Maps;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.EtrCashPositionFlowMapper;
import com.joyintech.tams.common.base.dao.PtlCashPositionFlowMapper;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.portfolio.ptlfundallocation.dto.PtlFundAllocation001Dto;
import com.joyintech.tams.invest.portfolio.ptlfundallocation.dto.PtlFundAllocation002Dto;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.service.WorkFlowService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 银证转账新建
 * @author fanshuanghuF
 * @version 1.0 2019年1月30日
 * @see PtlFundAllocation003Service
 * @since 1.0
 */
@Service("PtlFundAllocation003Service")
@Transactional(rollbackFor=Exception.class)
public class PtlFundAllocation003Service extends CommonExportService<PtlFundAllocation002Dto>
        implements BaseProcessService<PtlFundAllocation002Dto> {
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
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 业务流程表service
     */
    @Autowired(required = false)
    private WorkFlowService workflowService;
    /**
     * 查询组合
     * @return 组合集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortfolioIdBysecId(Map<String,Object> map){
        return simpleRestTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getPortfolioIdBysecId", map, SelectOptionDto.class);
    }

    /**
     * 查询收款账号
     * @return 收款账号集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getAcctNo(Map<String,Object> map){
        return simpleRestTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getAcctNo", map, SelectOptionDto.class);
    }

    /**
     * 查询付款账号
     * @return 付款账号集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getMyAcctNo(Map<String,Object> map){
        return simpleRestTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getMyAcctNo", map, SelectOptionDto.class);
    }

    /**
     * 获取账户余额
     * @return 账户余额
     * @see
     * @since 1.0
     */
    public BigDecimal getAmount(Map<String,Object> map){
        return simpleRestTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getAmount", map, BigDecimal.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(PtlFundAllocation002Dto entity, OperationType type) {
        entity.setOperateType(type.toString());
//        for (int i = 0; i < entity.getPtlFundAllocation001Dtos().size(); i++) {
//            PtlFundAllocation001Dto detailDto = entity.getPtlFundAllocation001Dtos().get(i);
//            PtlFundAllocation002Dto dto = new PtlFundAllocation002Dto();
//            this.setEntityy(detailDto, entity, dto, i);
//            simpleRestTemplate.postForObject(
//                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doComplete", entity,
//                    Integer.class);
//        }
        int i =0;
        for(PtlFundAllocation001Dto ptlFundAllocation001Dto :entity.getPtlFundAllocation001Dtos()){
            PtlFundAllocation002Dto ptlFundAllocationDto = new PtlFundAllocation002Dto();
            BeanUtil.copyPropertiesIgnoreNull(ptlFundAllocationDto, entity);
            ptlFundAllocationDto.setPortfolioId(ptlFundAllocation001Dto.getPortfolioId());
            ptlFundAllocationDto.setActPortfolioId(ptlFundAllocation001Dto.getPortfolioId());
            ptlFundAllocationDto.setSettleAmt(ptlFundAllocation001Dto.getSettleAmt());
            ptlFundAllocationDto.setPositionType("submit");
            if (StringUtil.isEmpty(entity.getMyAcctNo())){
                ptlFundAllocationDto.setMyAcctNo(ptlFundAllocation001Dto.getAcctNo());
                ptlFundAllocationDto.setCounterAcctNo(entity.getCounterAcctNo());
            }
            if (StringUtil.isEmpty(entity.getCounterAcctNo())){
                ptlFundAllocationDto.setMyAcctNo(entity.getMyAcctNo());
                ptlFundAllocationDto.setCounterAcctNo(ptlFundAllocation001Dto.getAcctNo());
            }
            for (BussTrdTransferOrderDto bussTrdTransferOrderDto : entity.getBussTrdTransferOrder()){
                if (bussTrdTransferOrderDto.getPortfolioId().equals(ptlFundAllocationDto.getPortfolioId())){
                    List<BussTrdTransferOrderDto> bussTrdTransferOrderDtos = new ArrayList<>();
                    bussTrdTransferOrderDtos.add(bussTrdTransferOrderDto);
                    ptlFundAllocationDto.setBussTrdTransferOrder(bussTrdTransferOrderDtos);
                }
            }
            if(!Objects.isNull(entity.getBussTrdTransferOrder().get(i))){
                ptlFundAllocationDto.setPayType(entity.getPayType());
                ptlFundAllocationDto = this.setDots(entity.getBussTrdTransferOrder().get(i) , ptlFundAllocationDto,i);
            }
            i++;
            ptlFundAllocationDto.setTradeId(null);
            ptlFundAllocationDto.setOrderId(ptlFundAllocationDto.getBussTrdTransferOrder().get(0).getOrderId());
            simpleRestTemplate.postForObject(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doComplete", this.setEntity(ptlFundAllocationDto,type),
                    Integer.class);
        }
    }

    @Override
    public void doDelete(String workflowId) {
        PtlFundAllocation002Dto entity = this.getBussinessEntityByWorkflowId(workflowId);
        entity.setPositionType(entity.getOperateType());
        if(StringUtil.isEmpty(entity.getOrderId())&&entity.getBussTrdTransferOrder()!=null&&entity.getBussTrdTransferOrder().size()>0){
            entity.setOrderId(entity.getBussTrdTransferOrder().get(0).getOrderId());
        }
        if (StringUtil.isNotEmpty(entity.getOrderId())) {
            simpleRestTemplate.postForObject(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doDelete", entity,
                    String.class);
        }
    }

    @Override
    public void doBack(PtlFundAllocation002Dto ptlFundAllocation002Dto) {
        for(BussTrdTransferOrderDto bussTrdTransferOrderDto:ptlFundAllocation002Dto.getBussTrdTransferOrder()){
            AllTradeCommonUtil.deletePositionByOrderId(bussTrdTransferOrderDto.getOrderId());
            if (!StringUtil.equals("revoke", ptlFundAllocation002Dto.getOperateType())
                    && !StringUtil.equals("delete", ptlFundAllocation002Dto.getOperateType())) {
                bussTrdTransferOrderDto.setMyAcctNo(bussTrdTransferOrderDto.getPayAcctNo());
                restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doBack", bussTrdTransferOrderDto,
                        String.class);
            }
        }
    }

    @Override
    public void doInsert(PtlFundAllocation002Dto entity, OperationType type) {
        if ("save".equals(entity.getOperateType())) {
            return;
        }
        for (int i = 0; i < entity.getPtlFundAllocation001Dtos().size(); i++) {
            PtlFundAllocation001Dto detailDto = entity.getPtlFundAllocation001Dtos().get(i);
            PtlFundAllocation002Dto dto = new PtlFundAllocation002Dto();
            if(StringUtils.isEmpty(dto.getPositionType())){
                dto.setPositionType(entity.getOperateType());
            }
            this.setEntityy(detailDto,entity, dto, i);
            dto.setOperateType(entity.getOperateType());
            if(!Objects.isNull(entity.getBussTrdTransferOrder().get(i))){
                dto.setPayType(entity.getPayType());
                dto.setWorkflowId(entity.getWorkflowId());
                dto = this.setDots(entity.getBussTrdTransferOrder().get(i) , dto, i);
            }
            restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doInsertAction", dto,
                    String.class);
//            if (bussCommonMapperDelegateService != null && dto.getPtlCashPositionFlow() != null
//                    && StringUtil.isNotBlank(dto.getPtlCashPositionFlow().getAcctId())) {
//                bussCommonMapperDelegateService.insert(PtlCashPositionFlowMapper.class, dto.getPtlCashPositionFlow());
//                dto.getPtlCashPositionFlow().setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
//                bussCommonMapperDelegateService.insert(EtrCashPositionFlowMapper.class, dto.getPtlCashPositionFlow());
//                PtlCashPositionFlow flow = dto.getPtlCashPositionFlow();
//                //flow.setAcctId(entity.getCounterAcctNo());
//                if (BussinessDictCodeList.指令类型_银证转账_银转证.getValue().equals(entity.getPayType())) {
//                    flow.setAcctId(entity.getCounterAcctNo());
//                }else if(BussinessDictCodeList.指令类型_银证转账_证转银.getValue().equals(entity.getPayType())) {
//                    flow.setAcctId(entity.getMyAcctNo());
//                }
//                // flow.setAmount(entity.getBussTrdTransferOrder().get(i).getSettleAmt().negate());
//                if(entity.getBussTrdTransferOrder().get(i).getSettleAmt()!=null){
//                    flow.setAmount(dto.getPtlCashPositionFlow().getAmount().negate());
//                }
//                BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
//                bussinessCodeParameter.setSeqNo("9999");
//                String seqno = baseGetBussinessId != null ? baseGetBussinessId.buildBussinessCode(bussinessCodeParameter) : IDGenerator.generate();
//                flow.setSeqno(seqno);
//                flow.setPortfolioId(entity.getBussTrdTransferOrder().get(i).getPortfolioId());
//                flow.setHodingType(BussinessDictCodeList.持仓类型_可用.getValue());
//                bussCommonMapperDelegateService.insert(PtlCashPositionFlowMapper.class, flow);
//                flow.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
//                bussCommonMapperDelegateService.insert(EtrCashPositionFlowMapper.class, flow);
//            }
        }
    }
    @Override
    public void doUpdate(PtlFundAllocation002Dto entity){
        Map<String, Object> map = Maps.newConcurrentMap();
        map.put("title", "资金划拨多组合管理,划拨金额:"+entity.getSettleAmt()+",交易日期:("+DateUtil.format(entity.getSettleDate())+")");
        map.put("workFlowId",entity.getWorkflowId());
        map.put("updateUser", LoginUserUtil.getUserName());
        map.put("updateTime",DateUtil.now());
        workflowService.updateTitle(map);
        if ("save".equals(entity.getOperateType())) {
            return;
        }
        String orderId = null;
        if(StringUtil.isNotEmpty(entity.getOrderId())) {
            orderId = entity.getOrderId();
        }else if(!StringUtil.isNotEmpty(entity.getOrderId()) && StringUtil.isNotEmpty(entity.getBussTrdTransferOrder().get(0).getOrderId())) {
            orderId = entity.getBussTrdTransferOrder().get(0).getOrderId();
        }
        if(bussCommonMapperDelegateService != null){
            entity.setOrderId(orderId);
            entity.setPositionType("update");
            simpleRestTemplate.postForObject(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/doDelete", entity,
                    String.class);
            doInsert(entity, OperationType.NEW);
        }
    }

    public PtlFundAllocation002Dto setDots(BussTrdTransferOrderDto entity ,PtlFundAllocation002Dto dto,int i){
        dto.setCcy(entity.getCcy());
        dto.setSettleDate(entity.getSettleDate());
        dto.setPortfolioId(entity.getPortfolioId());
        dto.setSettleAmt(entity.getSettleAmt());
        dto.setMyAcctNo(entity.getPayAcctNo());
        dto.setCounterAcctNo(entity.getRecAcctNo());
        return dto;
    }
    /**
     * 实体类的合成
     * @param dto 资金划拨实体类
     * @return 资金划拨实体类
     * @see
     * @since 1.0
     */
    public PtlFundAllocation002Dto setEntityy(PtlFundAllocation001Dto detail,PtlFundAllocation002Dto dto, PtlFundAllocation002Dto result,int i){
        if(!StringUtil.isNotEmpty(result.getTradeId())) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setCommonBusinessType1(BussinessDictCodeList.共同业务类型_资金划拨管理);
            result.setTradeId(baseGetBussinessId !=null ? baseGetBussinessId.buildBussinessCode(bussinessCodeParameter):IDGenerator.generate());
            dto.setTradeId(result.getTradeId());
        }
        // 先处理付款账户，生成可用持仓流水，生效后处理成实际及收款账户
        /**
         * 组合现金流水
         */
        PtlCashPositionFlow ptlCashPositionFlow = new PtlCashPositionFlow();
        ptlCashPositionFlow.setPortfolioId(detail.getPortfolioId());
        if (BussinessDictCodeList.指令类型_银证转账_银转证.getValue().equals(dto.getPayType())) {
            ptlCashPositionFlow.setAcctId(detail.getAcctNo());//付款账户
        }else if(BussinessDictCodeList.指令类型_银证转账_证转银.getValue().equals(dto.getPayType())) {
            ptlCashPositionFlow.setAcctId(dto.getMyAcctNo());//付款账户
        }
        ptlCashPositionFlow.setAmount(ArithmeticUtil.multiply(detail.getSettleAmt(),new BigDecimal("-1")));
        ptlCashPositionFlow.setCcy(dto.getCcy());
        ptlCashPositionFlow.setHodingType(BussinessDictCodeList.持仓类型_可用.getValue());
        ptlCashPositionFlow.setCdate(dto.getSettleDate());
        ptlCashPositionFlow.setOrderId(dto.getBussTrdTransferOrder().get(i).getOrderId());
        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        bussinessCodeParameter.setSeqNo("9999");
        String seqno = baseGetBussinessId !=null ? baseGetBussinessId.buildBussinessCode(bussinessCodeParameter):IDGenerator.generate();
        if(dto.getPtlCashPositionFlow() != null && StringUtil.isNotEmpty(dto.getPtlCashPositionFlow().getSeqno())){
            ptlCashPositionFlow.setSeqno(dto.getPtlCashPositionFlow().getSeqno());
        }
        else{
            ptlCashPositionFlow.setSeqno(seqno);
        }
        ptlCashPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
        result.setOrderId(dto.getBussTrdTransferOrder().get(i).getOrderId());
        result.setPtlCashPositionFlow(ptlCashPositionFlow);
        dto.setOrderId(dto.getBussTrdTransferOrder().get(i).getOrderId());
//        dto.setPtlCashPositionFlow(ptlCashPositionFlow);


        /**
         * 组合现金余额
         */
        PtlCashPosition ptlCashPosition = new PtlCashPosition();
        //ptlCashPosition.setAcctId(detail.getAcctNo());
        if (BussinessDictCodeList.指令类型_银证转账_银转证.getValue().equals(dto.getPayType())) {
            ptlCashPositionFlow.setAcctId(detail.getAcctNo());//付款账户
        }else if(BussinessDictCodeList.指令类型_银证转账_证转银.getValue().equals(dto.getPayType())) {
            ptlCashPositionFlow.setAcctId(dto.getMyAcctNo());//付款账户
        }
        ptlCashPosition.setAmount(ptlCashPositionFlow.getAmount());
        ptlCashPosition.setCcy(dto.getCcy());
        ptlCashPosition.setPortfolioId(detail.getPortfolioId());
        ptlCashPosition.setHodingType(BussinessDictCodeList.持仓类型_实际.getValue());
        ptlCashPosition.setCdate(dto.getSettleDate());
        result.setPtlCashPosition(ptlCashPosition);
//        dto.setPtlCashPosition(ptlCashPosition);

        /**
         * 组合交易
         */
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        ptlTrade.setBusiNo(dto.getTradeId());
        ptlTrade.setBusiTable(BussinessDictCodeList.业务表_交易资金指令表.getValue());
        ptlTrade.setBusiType(BussinessDictCodeList.平安划款类型_托管划款.getValue());
        dto.setPtlTrade(ptlTrade);
        /**
         * 金融产品交易
         */
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(dto.getTradeId());
        trdProductDeal.setTradeStatus(BussinessDictCodeList.交易状态_初始.getValue());
        trdProductDeal.setCcy(dto.getCcy());
        if (StringUtil.equals(dto.getPayType(), BussinessDictCodeList.指令类型_银证转账_银转证.getValue())) {
            // 银证转账
            trdProductDeal.setPs(BussinessDictCodeList.交易方向_银证转账.getValue());
        }
        else if (StringUtil.equals(dto.getPayType(),
                BussinessDictCodeList.指令类型_银证转账_证转银.getValue())) {
            // 证银转账
            trdProductDeal.setPs(BussinessDictCodeList.交易方向_证银转账.getValue());
        }
        trdProductDeal.setIsCancel(BussinessDictCodeList.是否_否.getValue());
        trdProductDeal.setTradeDate(dto.getSettleDate());
        trdProductDeal.setSettleDate(dto.getSettleDate());
        trdProductDeal.setRemark(dto.getRemark());
        trdProductDeal.setTradeAmt(dto.getSettleAmt());
        dto.setTrdProductDeal(trdProductDeal);
        // 交易附属表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId());
        trdProductDealAdd.setGenType(BussinessDictCodeList.生成方式_画面录入.getValue());
        dto.setTrdProductDealAdd(trdProductDealAdd);
        /**
         * 金融产品交易扩展表
         */
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(trdProductDeal.getTradeId());
        if(StringUtil.equals(dto.getPayType(), BussinessDictCodeList.指令类型_银证转账_银转证.getValue())){
            //银证转账
            paTrdProductDeal.setOrderBussType(BussinessDictCodeList.指令业务类型_银证转账.getValue());
            paTrdProductDeal.setPaBussType(BussinessDictCodeList.平安业务类型_银证转账.getValue());//平安业务类型
        }else if(StringUtil.equals(dto.getPayType(), BussinessDictCodeList.指令类型_银证转账_证转银.getValue())){
            //证银转账
            paTrdProductDeal.setOrderBussType(BussinessDictCodeList.指令业务类型_证银转账.getValue());
            paTrdProductDeal.setPaBussType(BussinessDictCodeList.平安业务类型_证银转账.getValue());//平安业务类型
        }
        dto.setPaTrdProductDeal(paTrdProductDeal);
        //add by yejiejie 2019-3-19 end
        if(bussTrdTransferOrderService != null){
            dto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, dto.getBussTrdTransferOrder().get(0));
            stlPayOrdersDto.setTradeId(dto.getTradeId());// 不知道之前为啥要改这块，现在直接影响到 资金划拨管理查询 lijun add
            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
            list.add(stlPayOrdersDto);
            dto.setStlPayOrdersList(list);
        }
        dto.setOperateType("NEW");
        dto.setRemark(dto.getRemark());

        return dto;
    }


    public PtlFundAllocation002Dto setEntity(PtlFundAllocation002Dto dto, OperationType type){
        if(!StringUtil.isNotEmpty(dto.getTradeId())) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资金划拨管理);
            dto.setTradeId(baseGetBussinessId !=null ? baseGetBussinessId.buildBussinessCode(bussinessCodeParameter):IDGenerator.generate());
        }
        // 先处理付款账户，生成可用持仓流水，生效后处理成实际及收款账户
        /**
         * 组合现金流水
         */
        PtlCashPositionFlow ptlCashPositionFlow = new PtlCashPositionFlow();
        ptlCashPositionFlow.setPortfolioId(dto.getPortfolioId());
        ptlCashPositionFlow.setAcctId(dto.getMyAcctNo());//付款账户
        if(ptlCashPositionFlow.getAcctId()==null||"".equals(ptlCashPositionFlow.getAcctId())){
            ptlCashPositionFlow.setAcctId(dto.getCounterAcctNo());
        }
/*        if (BussinessDictCodeList.指令类型_银证转账_银转证.getValue().equals(dto.getPayType())) {
            ptlCashPositionFlow.setAmount(ArithmeticUtil.multiply(dto.getSettleAmt(),new BigDecimal("-1")));
        }else if(BussinessDictCodeList.指令类型_银证转账_证转银.getValue().equals(dto.getPayType())) {
            ptlCashPositionFlow.setAmount(dto.getSettleAmt());
        }*/
        ptlCashPositionFlow.setAmount(ArithmeticUtil.multiply(dto.getSettleAmt(),new BigDecimal("-1")));
        if (OperationType.REVOKE == type) {
            ptlCashPositionFlow.setAmount(ArithmeticUtil.multiply(ptlCashPositionFlow.getAmount(),new BigDecimal("-1")));
        }
        ptlCashPositionFlow.setCcy(dto.getCcy());
        ptlCashPositionFlow.setHodingType(BussinessDictCodeList.持仓类型_可用.getValue());
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
        ptlCashPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
        dto.setPtlCashPositionFlow(ptlCashPositionFlow);

        /**
         * 组合现金余额
         */
        PtlCashPosition ptlCashPosition = new PtlCashPosition();
        ptlCashPosition.setAcctId(dto.getMyAcctNo());
        ptlCashPosition.setAmount(ptlCashPositionFlow.getAmount());
        ptlCashPosition.setCcy(dto.getCcy());
        ptlCashPosition.setPortfolioId(dto.getPortfolioId());
        ptlCashPosition.setHodingType(BussinessDictCodeList.持仓类型_实际.getValue());
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
//        ptlTrade.setBusiType(BussinessDictCodeList.划款类型_托管划款.getValue());
        ptlTrade.setBusiType(BussinessDictCodeList.平安划款类型_托管划款.getValue());
        // 一体化对应etliu end
        dto.setPtlTrade(ptlTrade);
        /**
         * 金融产品交易
         */
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(dto.getTradeId());
//        trdProductDeal.setTradeStatus(BussinessDictCodeList.画面数据状态_有效.getValue());
        trdProductDeal.setTradeStatus(BussinessDictCodeList.交易状态_初始.getValue());
        trdProductDeal.setCcy(dto.getCcy());

//        trdProductDeal.setPs(dto.getPayType());
        if (StringUtil.equals(dto.getPayType(), BussinessDictCodeList.指令类型_银证转账_银转证.getValue())) {
            // 银证转账
            trdProductDeal.setPs(BussinessDictCodeList.交易方向_银证转账.getValue());
        }
        else if (StringUtil.equals(dto.getPayType(),
                BussinessDictCodeList.指令类型_银证转账_证转银.getValue())) {
            // 证银转账
            trdProductDeal.setPs(BussinessDictCodeList.交易方向_证银转账.getValue());
        }

        trdProductDeal.setIsCancel(BussinessDictCodeList.是否_否.getValue());
        trdProductDeal.setTradeDate(dto.getSettleDate());
        trdProductDeal.setSettleDate(dto.getSettleDate());
        trdProductDeal.setRemark(dto.getRemark());
        trdProductDeal.setTradeAmt(dto.getSettleAmt());
        dto.setTrdProductDeal(trdProductDeal);

        // 交易附属表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId());
        trdProductDealAdd.setGenType(BussinessDictCodeList.生成方式_画面录入.getValue());
        dto.setTrdProductDealAdd(trdProductDealAdd);

        /**
         * 金融产品交易扩展表
         */
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(trdProductDeal.getTradeId());
        if(StringUtil.equals(dto.getPayType(), BussinessDictCodeList.指令类型_银证转账_银转证.getValue())){
            //银证转账
            paTrdProductDeal.setOrderBussType(BussinessDictCodeList.指令业务类型_银证转账.getValue());
            paTrdProductDeal.setPaBussType(BussinessDictCodeList.平安业务类型_银证转账.getValue());//平安业务类型
        }else if(StringUtil.equals(dto.getPayType(), BussinessDictCodeList.指令类型_银证转账_证转银.getValue())){
            //证银转账
            paTrdProductDeal.setOrderBussType(BussinessDictCodeList.指令业务类型_证银转账.getValue());
            paTrdProductDeal.setPaBussType(BussinessDictCodeList.平安业务类型_证银转账.getValue());//平安业务类型
        }
        dto.setPaTrdProductDeal(paTrdProductDeal);
        //add by yejiejie 2019-3-19 end

        if(bussTrdTransferOrderService != null){
            dto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, dto.getBussTrdTransferOrder().get(0));
            stlPayOrdersDto.setTradeId(dto.getTradeId());// 不知道之前为啥要改这块，现在直接影响到 资金划拨管理查询 lijun add
            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
            list.add(stlPayOrdersDto);
            dto.setStlPayOrdersList(list);
        }
        dto.setOperateType("NEW");
        dto.setRemark(dto.getRemark());

        return dto;
    }

    /**
     * 获取组合资金账户和账户余额
     */
    public PtlFundAllocation001Dto getPosition(Map<String, Object> pageParam) {
        return simpleRestTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getPosition", pageParam, PtlFundAllocation001Dto.class);
    }

    /**
     * 根据账户获取明细
     */
    public List<PtlFundAllocation001Dto> getPtlFundAllocation001Dtos(Map<String, Object> pageParam) {
        return simpleRestTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getPtlFundAllocation001Dtos", pageParam, List.class);
    }

    /**
     * 获取组合在该证券账户的明细信息
     */
    public List<PtlFundAllocation001Dto> getDetailInfo(Map<String, Object> pageParam) {
        return simpleRestTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlFundAllocation/getDetailInfo", pageParam, List.class);
    }
}
