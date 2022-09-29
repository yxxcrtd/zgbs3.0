package com.joyintech.tams.invest.product.productduration.netproductbonusscheme.service;

import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.group.impl.dao.BasePtlCashPositionInfoMapper;
import com.joyintech.tams.invest.common.component.business.common.BaseAssetCashPositionCommService;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioCashPositionsFlowInputDto;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioPositionsFlowInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.TradeProductDealCommService;
import com.joyintech.tams.invest.product.productduration.netproductbonusscheme.dto.ActualTimeCapital001Dto;
import com.joyintech.tams.invest.product.productduration.netproductbonusscheme.dto.FinNetProductBonusDealDto;
import com.joyintech.tams.invest.product.productduration.netproductbonusscheme.dto.FinNetProductBonusSchemeDto;
import com.joyintech.tams.invest.product.productduration.netproductduration.dto.FinNetProductDurationLayerDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 净值型分红方案service
 *
 * @author liuwei
 * @version 1.0 2018年10月17日
 * @see FinNetProductBonusSchemeService
 * @since 1.0
 */
@Service
public class FinNetProductBonusSchemeService extends CommonExportService<FinNetProductBonusSchemeDto> implements BaseProcessService<FinNetProductBonusSchemeDto> {

    /**
     * 资产头寸现金持仓共通
     */
    @Autowired(required = false)
    private BaseAssetCashPositionCommService baseAssetCashPositionCommService;

    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    @Autowired
    private TradeProductDealCommService baseTradeProductDealCommServiceImpl;

    @Autowired
    private FinCashInfoMapper finCashInfoMapper;
    
    @Autowired
    private BasePtlCashPositionInfoMapper basePtlCashPositionInfoMapper;
    
    /**
     * 现金DAO
     */
    @Autowired
    private EtrCashPositionMapper etrCashPositionMapper;

    /**
     * 委托共同Service
     */
    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    
    @Autowired
    private FinCashPayRuleMapper finCashPayRuleMapper;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;

    @Autowired
    private PrdSaleParamMapper prdSaleParamMapper;

    @Autowired
    private FinProductAddMapper finProductAddMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private RepDurationMapper repDurationMapper;
    
    @Autowired
    private TrdAsynHandleStatusMapper trdAsynHandleStatusMapper;
    /**
     * 业务标号生成共通
     */
    @Autowired(required=false)
    private BaseGetBusinessId baseGetBusinessId;
    
    
    @Override
    public void doInsert(FinNetProductBonusSchemeDto dto, OperationType operationType) {
    	 if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
    		 this.handleBussFlowData(dto);
    	 }
    }
    
    @Override
    public void doUpdate(FinNetProductBonusSchemeDto dto) {    	
    	 if (!StringUtil.equals("revoke", dto.getOperateType())
                 && !StringUtil.equals("delete", dto.getOperateType())) {
    		 this.handleBussFlowData(dto);
    	 }
    }
    
    @Override
    public void doBack(FinNetProductBonusSchemeDto dto) {
    	 if (!StringUtil.equals("revoke", dto.getOperateType())
                 && !StringUtil.equals("delete", dto.getOperateType())) {
    	 this.delhandleBussFlowData(dto);
    	 }
    }
    
    private void handleBussFlowData(FinNetProductBonusSchemeDto dto) {
        if (dto.getDealType().equals("bonusCash")) {
            // 划款指令
            List<StlPayOrders> stlPayOrders = new ArrayList<StlPayOrders>();
            if (dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0) {
                for (BussTrdTransferOrderDto orderDto : dto.getBussTrdTransferOrder()) {
                    StlPayOrders stlPayOrdersDto = new StlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, orderDto);
                    stlPayOrdersDto.setOrderId(orderDto.getOrderId());
                    stlPayOrdersDto.setTradeId(dto.getTradeId());
                    stlPayOrdersDto.setOrderType(orderDto.getOrderType());
                    //净值型现金分红生成指令为汇总交易
                    //add by wm 2019-11-06 start 
                    stlPayOrdersDto.setIsTotalTrade(DictCodeList.是否_是.getValue());
                    //add by wm 2019-11-06 end 
                    if (orderDto.getPs().equals("01")) {
                        stlPayOrdersDto.setMyAcctNo(orderDto.getRecAcctNo());
                        //20191211净值型红利再投保持与存续期交易红利再投一致	taoran add
                        stlPayOrdersDto.setCounterAcctNo(orderDto.getPayAcctNo());
                    }
                    else {
                        stlPayOrdersDto.setMyAcctNo(orderDto.getPayAcctNo());
                        //20191211净值型红利再投保持与存续期交易红利再投一致	taoran add
                        stlPayOrdersDto.setCounterAcctNo(orderDto.getRecAcctNo());
                    }
                    stlPayOrdersDto.setSettleAmt(orderDto.getSettleAmt());
                    stlPayOrdersDto.setCcy(orderDto.getCcy());
                    stlPayOrdersDto.setSettleDate(orderDto.getSettleDate());
                    stlPayOrders.add(stlPayOrdersDto);
                    //stlPayOrdersMapper.insertSelective(stlPayOrdersDto);
                    
                    /**
                     * 指令现金流水
                     */                    
//                    EtrCashPosition tempPosition = new EtrCashPosition();
//                    tempPosition.setPortfolioId(stlPayOrdersDto.getPortfolioId());
//                    tempPosition.setAcctId(stlPayOrdersDto.getMyAcctNo());
//                    tempPosition.setAmount(stlPayOrdersDto.getSettleAmt().negate());
//                    tempPosition.setCcy(stlPayOrdersDto.getCcy());
//                    tempPosition.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
//                    tempPosition.setCdate(stlPayOrdersDto.getSettleDate());
//                    EtrCashPositionKey etrCashPositionKey = new EtrCashPosition();
//                    BeanUtil.copyPropertiesIgnoreNull(etrCashPositionKey, tempPosition);
//                    EtrCashPosition EtrCashPositionKey = etrCashPositionMapper.selectByPrimaryKey(etrCashPositionKey);
//                    tempPosition.setAmount(ArithmeticUtil.doAdd(tempPosition.getAmount(),
//                            StringUtil.isNotEmpty(EtrCashPositionKey) ? EtrCashPositionKey.getAmount() : BigDecimal.ZERO));
//                    etrCashPositionMapper.updateByPrimaryKeySelective(tempPosition);
                }
            }
            // 组合现金流水Entity
            List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<PtlCashPositionFlowDto>();
            if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                for (BussTrdTransferOrderDto orderDto : dto.getBussTrdTransferOrder()) {
                    PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
                    ptlCashPositionFlowDto.setPortfolioId(dto.getPortfolioId());                                    
                        if (orderDto.getPs().equals("01")) {
                            ptlCashPositionFlowDto.setAcctId(orderDto.getRecAcctNo());
                            ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt());
                        }
                        else {
                            ptlCashPositionFlowDto.setAcctId(orderDto.getPayAcctNo());
                            ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt().negate());
                        }

                    ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
                    ptlCashPositionFlowDto.setCdate(dto.getActBonusDate());
                    ptlCashPositionFlowDto.setCcy(dto.getCcy());
                    ptlCashPositionFlowDto.setOrderId(orderDto.getOrderId());
                    ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                }
            }
            PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
            ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
            if(baseEtrCashPositionCommService!=null){
                baseEtrCashPositionCommService.portfolioEtrCashPosition(ptlPositionHoldDto, false);
            }
//            basePtlCashPositionInfoService.insertCashPositionFlow(ptlCashPositionFlowDto2);
            restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
                ptlPositionHoldDto, Boolean.class);           
    	}
    }
    
    private void delhandleBussFlowData(FinNetProductBonusSchemeDto dto) {
        if (dto.getDealType().equals("bonusCash")) {
            // 划款指令
            List<StlPayOrders> stlPayOrders = new ArrayList<StlPayOrders>();
            if (dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0) {
                for (BussTrdTransferOrderDto orderDto : dto.getBussTrdTransferOrder()) {
                    StlPayOrders stlPayOrdersDto = new StlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, orderDto);
                    stlPayOrdersDto.setOrderId(orderDto.getOrderId());
                    stlPayOrdersDto.setTradeId(dto.getTradeId());
                    stlPayOrdersDto.setOrderType(orderDto.getOrderType());
                    //净值型现金分红生成指令为汇总交易
                    //add by wm 2019-11-06 start 
                    stlPayOrdersDto.setIsTotalTrade(DictCodeList.是否_是.getValue());
                    //add by wm 2019-11-06 end 
                    if (orderDto.getPs().equals("01")) {
                        stlPayOrdersDto.setMyAcctNo(orderDto.getRecAcctNo());
                        //20191211净值型红利再投保持与存续期交易红利再投一致	taoran add
                        stlPayOrdersDto.setCounterAcctNo(orderDto.getPayAcctNo());
                    }
                    else {
                        stlPayOrdersDto.setMyAcctNo(orderDto.getPayAcctNo());
                        //20191211净值型红利再投保持与存续期交易红利再投一致	taoran add
                        stlPayOrdersDto.setCounterAcctNo(orderDto.getRecAcctNo());
                    }
                    stlPayOrdersDto.setSettleAmt(orderDto.getSettleAmt());
                    stlPayOrdersDto.setCcy(orderDto.getCcy());
                    stlPayOrdersDto.setSettleDate(orderDto.getSettleDate());
                    stlPayOrders.add(stlPayOrdersDto);
                    //stlPayOrdersMapper.insertSelective(stlPayOrdersDto);
                    
                    /**
                     * 指令现金流水
                     */                    
//                    EtrCashPosition tempPosition = new EtrCashPosition();
//                    tempPosition.setPortfolioId(stlPayOrdersDto.getPortfolioId());
//                    tempPosition.setAcctId(stlPayOrdersDto.getMyAcctNo());
//                    tempPosition.setAmount(stlPayOrdersDto.getSettleAmt());
//                    tempPosition.setCcy(stlPayOrdersDto.getCcy());
//                    tempPosition.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
//                    tempPosition.setCdate(stlPayOrdersDto.getSettleDate());
//                    EtrCashPositionKey etrCashPositionKey = new EtrCashPosition();
//                    BeanUtil.copyPropertiesIgnoreNull(etrCashPositionKey, tempPosition);
//                    EtrCashPosition EtrCashPositionKey = etrCashPositionMapper.selectByPrimaryKey(etrCashPositionKey);
//                    tempPosition.setAmount(ArithmeticUtil.doAdd(tempPosition.getAmount(), EtrCashPositionKey.getAmount()));
//                   	 etrCashPositionMapper.updateByPrimaryKeySelective(tempPosition);
                }
            }
            // 组合现金流水Entity
            List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<PtlCashPositionFlowDto>();
            if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                for (BussTrdTransferOrderDto orderDto : dto.getBussTrdTransferOrder()) {
                    PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
                    ptlCashPositionFlowDto.setPortfolioId(dto.getPortfolioId());                                    
                        if (orderDto.getPs().equals("01")) {
                            ptlCashPositionFlowDto.setAcctId(orderDto.getRecAcctNo());
                            ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt());
                        }
                        else {
                            ptlCashPositionFlowDto.setAcctId(orderDto.getPayAcctNo());
                            ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt().negate());
                        }

                    ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
                    ptlCashPositionFlowDto.setCdate(dto.getActBonusDate());
                    ptlCashPositionFlowDto.setCcy(dto.getCcy());
                    ptlCashPositionFlowDto.setOrderId(orderDto.getOrderId());                   
                    basePtlCashPositionInfoMapper.deletePtlCashPositionFlow(ptlCashPositionFlowDto);
                    ptlCashPositionFlowDto.setAmount(ptlCashPositionFlowDto.getAmount().negate());
                    ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                }
            }
            PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
            ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
            if(baseEtrCashPositionCommService!=null){
                baseEtrCashPositionCommService.portfolioEtrCashPosition(ptlPositionHoldDto, false);
            }
    	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(FinNetProductBonusSchemeDto dto, OperationType type) {
        String action = StringUtil.EMPTY;
        String dealType = dto.getDealType();
        if (dealType.equals("bonusScheRegister")) {
            if (StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())) {
                dto.setFinprodId(dto.getProdStyle());
            }
            // 登记
            if (OperationType.NEW.equals(type)) {
                action = "insert";
            }
            else if (OperationType.MODIFY.equals(type)) {
                action = "update";
            }
            else {
                action = "delete";
            }
            if (StringUtil.equals(dto.getBonuScalcFlag(), DictCodeList.分红方案_按单位分红.getValue())) {
                dto.setTotalAmt(null);
            }
            else {
                dto.setUnitBonusVlu(null);
            }
            restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/" + action, dto,
                Integer.class);
        }
        else if (dealType.equals("bonusAmtConfirm") || dealType.equals("bonusCash") || dealType.equals("bonusShareConfirm")) {
            if (OperationType.NEW.equals(type)) {
                doCompleteInsertOrUpdate(dto, false);
            }
            else if (type == OperationType.REVOKE || type == OperationType.DELETE) {
                doCompleteInsertOrUpdate(dto, true);
            }
            // 删除工作流表
            baseTradeProductDealCommServiceImpl.delete(dto.getWorkflowId());
        }
    }

    private void doCompleteInsertOrUpdate(FinNetProductBonusSchemeDto dto, boolean isRevoke) {
        // 金融产品交易汇总表(TRD_FIN_PRODUCT_DEAL_TOTAL)
        TrdFinProductDealTotal trdFinProductDealTotal = genTrdFinProductDealTotal(dto);
        if (isRevoke) {
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_是.getValue());
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            trdFinProductDealTotalMapper.updateByPrimaryKeySelective(trdFinProductDealTotal);

            // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            trdFinProductDealTotal.setpTradeId(trdFinProductDealTotal.getTotleTradeId());
            // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
            trdFinProductDealTotal.setTotleTradeId(
                trdFinProductDealTotal.getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
            trdFinProductDealTotal.setWorkflowId(dto.getWorkflowId());
        }
        trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);

        PaTrdFinProductDealTotal paTotal = new PaTrdFinProductDealTotal();
        paTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        if (dto.getDealType().equals("bonusCash")) {
            paTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品现金分红.getValue());
            paTrdFinProductDealTotalMapper.insertSelective(paTotal);            
        }
        else if (dto.getDealType().equals("bonusShareConfirm")) {
            paTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品红利转投.getValue());
            paTrdFinProductDealTotalMapper.insertSelective(paTotal);
        }

        // 金融产品交易(TRD_PRODUCT_DEAL)
        List<TrdProductDeal> listTrdProductDeal = genTrdProductDeal(dto,isRevoke);   
        for(TrdProductDeal trdProductDeal:listTrdProductDeal) { 
            if (isRevoke) {
                // 交易状态=“撤销”
                trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                trdProductDeal.setIsCancel(DictCodeList.是否_是.getValue());
                trdProductDealMapper.updateByPrimaryKeySelective(trdProductDeal);

                trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
                // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
                trdProductDeal.setpTradeId(trdProductDeal.getTradeId());
                // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
                trdProductDeal.setTotleTradeId(trdProductDeal.getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                trdProductDeal.setTradeId(trdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                // add by wm将带R的交易的创建时间与更新时间修改为最新的系统时间 start 2019-09-02
                trdProductDeal.setCreateTime(DateUtil.now());
                trdProductDeal.setUpdateTime(DateUtil.now());
                // add by wm将带R的交易的创建时间与更新时间修改为最新的系统时间 end 2019-09-02
                // 撤销说明=画面.撤销说明
                trdProductDeal.setCancelRemark(dto.getRevokeReason());
            }
            trdProductDealMapper.insertSelective(trdProductDeal);

            // if(dto.getDealType().equals("bonusCash") || dto.getDealType().equals("bonusShareConfirm")){
            // 调用估值入账接口
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            if (DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
                TrdProductDeal trdProductDealforVal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDealforVal, trdProductDeal);
                trdProductDealforVal.setFinprodId(dto.getFinprodId());
                trdProductDealforVal.setBranch(0l);
                trdProductDealforVal.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                trdProductDealforVal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
                inputDto.setTrdProductDeal(trdProductDealforVal);
                inputDto.setProdid(dto.getFinprodId());
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
            } else {
                inputDto.setProdid(trdProductDeal.getFinprodId());
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                inputDto.setTrdProductDeal(trdProductDeal);
            }


            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            // }
            // add by zlp 20190318 start //指令凭证打印添加类型
            // 金融产品交易扩展表Entity
            PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
            paTrdProductDeal.setTradeId(trdProductDeal.getTradeId());
            if (dto.getDealType().equals("bonusCash")) {
                paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品分红.getValue());
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_产品现金分红.getValue());
                paTrdProductDealMapper.insertSelective(paTrdProductDeal);
            }
            else if (dto.getDealType().equals("bonusShareConfirm")) {
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_产品红利转投.getValue());
                paTrdProductDealMapper.insertSelective(paTrdProductDeal);
            }
            // add by zlp 20190318 end
        }
        // 金融产品交易附属表(TRD_PRODUCT_DEAL_ADD)
        List<TrdProductDealAdd> listTrdProductDealAdd = genTrdProductDealAdd(dto); 
        for(TrdProductDealAdd trdProductDealAdd : listTrdProductDealAdd) {
            if (isRevoke) {
                trdProductDealAdd.setTradeId(trdProductDealAdd.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
            }
            trdProductDealAddMapper.insertSelective(trdProductDealAdd);
        }

        // 组合交易表(PTL_TRADE)
        PtlTrade ptlTrade = genPtlTrade(dto);
        if (isRevoke) {
            ptlTrade.setBusiNo(ptlTrade.getBusiNo() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
        }
        ptlTradeMapper.insertSelective(ptlTrade);

        if (dto.getDealType().equals("bonusCash")) {
            // 划款指令
            List<StlPayOrders> stlPayOrders = new ArrayList<StlPayOrders>();
            if (dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0) {
                for (BussTrdTransferOrderDto orderDto : dto.getBussTrdTransferOrder()) {
                    StlPayOrders stlPayOrdersDto = new StlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, orderDto);
                    stlPayOrdersDto.setOrderId(orderDto.getOrderId());
                    stlPayOrdersDto.setTradeId(dto.getTradeId());
                    stlPayOrdersDto.setOrderType(orderDto.getOrderType());
                    //净值型现金分红生成指令为汇总交易
                    //add by wm 2019-11-06 start 
                    stlPayOrdersDto.setIsTotalTrade(DictCodeList.是否_是.getValue());
                    //add by wm 2019-11-06 end 
                    if (orderDto.getPs().equals("01")) {
                        stlPayOrdersDto.setMyAcctNo(orderDto.getRecAcctNo());
                        //20191211净值型红利再投保持与存续期交易红利再投一致	taoran add
                        stlPayOrdersDto.setCounterAcctNo(orderDto.getPayAcctNo());
                    }
                    else {
                        stlPayOrdersDto.setMyAcctNo(orderDto.getPayAcctNo());
                        //20191211净值型红利再投保持与存续期交易红利再投一致	taoran add
                        stlPayOrdersDto.setCounterAcctNo(orderDto.getRecAcctNo());
                    }
                    if (isRevoke) {
                        stlPayOrdersDto.setSettleAmt(orderDto.getSettleAmt().negate());
                        stlPayOrdersDto.setTradeId(dto.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                        stlPayOrdersDto.setOrderId(orderDto.getOrderId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                        stlPayOrdersDto.setpTradeId(orderDto.getOrderId());
                    }
                    else {
                        stlPayOrdersDto.setSettleAmt(orderDto.getSettleAmt());
                    }
                    stlPayOrdersDto.setCcy(orderDto.getCcy());
                    stlPayOrdersDto.setSettleDate(orderDto.getSettleDate());
                    stlPayOrders.add(stlPayOrdersDto);
                    stlPayOrdersMapper.insertSelective(stlPayOrdersDto);
                    
                    /**
                     * 指令现金流水
                     */    
//                    if(isRevoke) {
//                    EtrCashPosition tempPosition = new EtrCashPosition();
//                    tempPosition.setPortfolioId(stlPayOrdersDto.getPortfolioId());
//                    tempPosition.setAcctId(stlPayOrdersDto.getMyAcctNo());
//                    tempPosition.setAmount(stlPayOrdersDto.getSettleAmt().negate());
//                    tempPosition.setCcy(stlPayOrdersDto.getCcy());
//                    tempPosition.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
//                    tempPosition.setCdate(stlPayOrdersDto.getSettleDate());
//                    EtrCashPositionKey etrCashPositionKey = new EtrCashPosition();
//                    BeanUtil.copyPropertiesIgnoreNull(etrCashPositionKey, tempPosition);
//                    EtrCashPosition EtrCashPositionKey = etrCashPositionMapper.selectByPrimaryKey(etrCashPositionKey);
//                    tempPosition.setAmount(ArithmeticUtil.doAdd(tempPosition.getAmount(),
//                            StringUtil.isNotEmpty(EtrCashPositionKey) ? EtrCashPositionKey.getAmount(): BigDecimal.ZERO));
//                   	 etrCashPositionMapper.updateByPrimaryKeySelective(tempPosition);
//                   	 etrCashPositionMapper.updateByPrimaryKeySelective(tempPosition);
//                    }
                }
            }

//            // 组合持仓流水Entity
//            List<PtlSecPositionFlowDto> ptlSecPositionFlows = genPtlSecPositionFlowDto(dto, isRevoke);
            // 组合现金流水Entity
            List<PtlCashPositionFlowDto> ptlCashPositionFlows = genPtlCashPositionFlowDto(dto, isRevoke);
            PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
//            ptlPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlows);
            ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
            if(isRevoke) {
                if (baseEtrCashPositionCommService != null) {
                    baseEtrCashPositionCommService.portfolioEtrCashPosition(ptlPositionHoldDto, false);
                }
            }
            restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
                ptlPositionHoldDto, Boolean.class);

            if ("001".equals(CacheUtil.getSysParam("CUSTOMER_BANK_CODE"))) {
                // 调用个性化接口- 资金到账标识接口 lijun add by 2019-08-12
                restTemplate.postForObject(
                    ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/FinProductInterf/sendFundToTAccount",
                    trdFinProductDealTotal, String.class);
            }
    	} else if(dto.getDealType().equals("bonusShareConfirm")){
    		// 组合持仓流水Entity
    		PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
            List<PtlSecPositionFlowDto> ptlSecPositionFlows = genPtlSecPositionFlowDto(dto,isRevoke);
            ptlPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlows);
            
            restTemplate.postForObject(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
                    ptlPositionHoldDto, Boolean.class);
    	}
        // 删除原业务流程记录
        this.deleteBussFlowData(dto.getWorkflowId());
        
        //20191008新增
    	TrdAsynHandleStatus trdAsynHandleStatus = new TrdAsynHandleStatus();
        trdAsynHandleStatus.setTradeId(trdFinProductDealTotal.getTotleTradeId());		//交易汇总id
        trdAsynHandleStatus.setTrdTableType("01");		// 交易表类型(01：金融产品交易汇总表，02：金融产品交易表)
        trdAsynHandleStatus.setHandleStatus("01");		//01	待处理
        trdAsynHandleStatusMapper.insertSelective(trdAsynHandleStatus);
    }

    private List<PtlSecPositionFlowDto> genPtlSecPositionFlowDto(FinNetProductBonusSchemeDto dto, boolean isRevoke) {
        List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<PtlSecPositionFlowDto>();
        PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
        ptlSecPositionFlowDto.setTradeId(dto.getTradeId());
        ptlSecPositionFlowDto.setPortfolioId(dto.getPortfolioId());
        if (StringUtil.isNotEmpty(dto.getProdStyle())) {
            ptlSecPositionFlowDto.setFinprodId(dto.getProdStyle());
            ptlSecPositionFlowDto.setpFinprodId(dto.getFinprodId());
        }
        else {
            ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
        }
        ptlSecPositionFlowDto.setBranch(0L);
        ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
        ptlSecPositionFlowDto.setCcy(dto.getCcy());
        ptlSecPositionFlowDto.setSecAcctId("999999");
        ptlSecPositionFlowDto.setInvAim("999999");
        ptlSecPositionFlowDto.setCdate(dto.getConfirmDate());
    	if(isRevoke){
    		ptlSecPositionFlowDto.setAmount(dto.getBonusShareNum().negate());
    	}else{
    		ptlSecPositionFlowDto.setAmount(dto.getBonusShareNum());
    	}
    	
    	//
		if(StringUtil.equals(DictCodeList.是否_是.getValue(),dto.getIsCycle())) {
			if(!StringUtil.isEmptyList(dto.getListCycleProductInfo())) {
				for(FinNetProductBonusSchemeDto dtoTmp:dto.getListCycleProductInfo()) {
					PtlSecPositionFlowDto ptlSecPositionFlowDtoTmp = new PtlSecPositionFlowDto();
					BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTmp,ptlSecPositionFlowDto);
					ptlSecPositionFlowDtoTmp.setBranch(dtoTmp.getBranch());
					ptlSecPositionFlowDtoTmp.setFinprodId(dtoTmp.getFinprodId());
					ptlSecPositionFlowDtoTmp.setpFinprodId(dto.getFinprodId());
					if(isRevoke){
						ptlSecPositionFlowDtoTmp.setAmount(dtoTmp.getBonusShareNum().negate());
			    	}else{
			    		ptlSecPositionFlowDtoTmp.setAmount(dtoTmp.getBonusShareNum());
			    	}
					ptlSecPositionFlows.add(ptlSecPositionFlowDtoTmp);
				}
			}
		} else {
			ptlSecPositionFlows.add(ptlSecPositionFlowDto);
		}
		return ptlSecPositionFlows;
	}

    private List<PtlCashPositionFlowDto> genPtlCashPositionFlowDto(FinNetProductBonusSchemeDto dto, boolean isRevoke) {
        List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<PtlCashPositionFlowDto>();
        if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
            for (BussTrdTransferOrderDto orderDto : dto.getBussTrdTransferOrder()) {
                PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
                ptlCashPositionFlowDto.setPortfolioId(dto.getPortfolioId());
                if (isRevoke) {
                    if (orderDto.getPs().equals("01")) {
                        ptlCashPositionFlowDto.setAcctId(orderDto.getRecAcctNo());
                        ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt().negate());
                    }
                    else {
                        ptlCashPositionFlowDto.setAcctId(orderDto.getPayAcctNo());
                        ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt());
                    }
                }
                else {
                    if (orderDto.getPs().equals("01")) {
                        ptlCashPositionFlowDto.setAcctId(orderDto.getRecAcctNo());
                        ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt());
                    }
                    else {
                        ptlCashPositionFlowDto.setAcctId(orderDto.getPayAcctNo());
                        ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt().negate());
                    }

                }
                ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
                ptlCashPositionFlowDto.setCdate(dto.getActBonusDate());
                ptlCashPositionFlowDto.setCcy(dto.getCcy());
                ptlCashPositionFlowDto.setOrderId(orderDto.getOrderId());
                ptlCashPositionFlows.add(ptlCashPositionFlowDto);
            }
        }
        return ptlCashPositionFlows;
    }

    private void deleteBussFlowData(String workflowId) {
        baseTradeProductDealCommServiceImpl.deleteWflData(workflowId);
    }

    /**
     * 组装 组合交易信息
     *
     * @param dto
     * @return
     */
    private PtlTrade genPtlTrade(FinNetProductBonusSchemeDto dto) {
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        ptlTrade.setBusiNo(dto.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(dto.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.业务类型_净值型TA交易.getValue());
        ptlTrade.setCreateUser(dto.getCreateUser());
        ptlTrade.setCreateDept(dto.getCreateDept());
        ptlTrade.setCreateTime(dto.getCreateTime());
        ptlTrade.setUpdateUser(dto.getUpdateUser());
        ptlTrade.setUpdateTime(dto.getCreateTime());
        if (dto.getDealType().equals("bonusAmtConfirm")) {
            ptlTrade.setSettleDate(DateUtil.platDateFromCache());
        }
        else if (dto.getDealType().equals("bonusCash")) {
            ptlTrade.setSettleDate(dto.getActBonusDate());
        }
        else if (dto.getDealType().equals("bonusShareConfirm")) {
            ptlTrade.setSettleDate(dto.getConfirmDate());
        }
        return ptlTrade;
    }

    /**
     * 组装 交易附属信息
     *
     * @param dto
     * @return
     */
	private List<TrdProductDealAdd> genTrdProductDealAdd(FinNetProductBonusSchemeDto dto) {
		List<TrdProductDealAdd> listTrdProductDealAdd = new ArrayList<TrdProductDealAdd>();
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(dto.getTradeId());
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        trdProductDealAdd.setCreateUser(dto.getCreateUser());
        trdProductDealAdd.setCreateDept(dto.getCreateDept());
        trdProductDealAdd.setCreateTime(dto.getCreateTime());
        trdProductDealAdd.setUpdateUser(dto.getUpdateUser());
        trdProductDealAdd.setUpdateTime(dto.getCreateTime());
        if (dto.getDealType().equals("bonusAmtConfirm")) {
            trdProductDealAdd.setRegDate(dto.getRegDate());
        }
        else if (dto.getDealType().equals("bonusCash")) {
            trdProductDealAdd.setRegDate(dto.getActBonusDate());
        }else if(dto.getDealType().equals("bonusShareConfirm")){
        	trdProductDealAdd.setRegDate(dto.getConfirmDate());
        }
    	if (StringUtil.equals(DictCodeList.是否_是.getValue(),dto.getIsCycle())) {
    		if(!StringUtil.isEmptyList(dto.getListCycleProductInfo())){
				for(FinNetProductBonusSchemeDto dtoTmp:dto.getListCycleProductInfo()){
					TrdProductDealAdd trdProductDealAddtmp = new TrdProductDealAdd();
					BeanUtil.copyPropertiesIgnoreNull(trdProductDealAddtmp,trdProductDealAdd);
					
					trdProductDealAddtmp.setTradeId(dtoTmp.getTradeId());
					listTrdProductDealAdd.add(trdProductDealAddtmp);
				}
    		}
    	} else {
    		listTrdProductDealAdd.add(trdProductDealAdd);
    	}
    	
		return listTrdProductDealAdd;
    }

    /**
     * 组装 金融产品交易(TRD_PRODUCT_DEAL) entity
     *
     * @param dto
     * @return
     */
	private List<TrdProductDeal> genTrdProductDeal(FinNetProductBonusSchemeDto dto,boolean isRevoke) {
		List<TrdProductDeal> listTrdProductDeal = new ArrayList<TrdProductDeal>();
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(dto.getTradeId());
        trdProductDeal.setBusiType(DictCodeList.业务类型_净值型TA交易.getValue());
		if (StringUtil.equals(DictCodeList.是否_是.getValue(),dto.getIsCycle())) {
			trdProductDeal.setFinprodId(dto.getFinprodId());
			trdProductDeal.setBranch(dto.getBranch());
			trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue());
			trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_理财子产品.getValue());

		} else if (StringUtil.isNotEmpty(dto.getProdStyle())) {
            trdProductDeal.setFinprodId(dto.getProdStyle());
            trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
			trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
			trdProductDeal.setBranch(0L);
		} else {
            trdProductDeal.setFinprodId(dto.getFinprodId());
            trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
			trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
			trdProductDeal.setBranch(0L);
        }
        // trdProductDeal.setFinprodId(dto.getFinprodId());
        trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
        // trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        trdProductDeal.setCcy(dto.getCcy());
        trdProductDeal.setUnitInt(dto.getUnitBonusVlu());
        trdProductDeal.setUnitCprice(dto.getNetvalue());
        trdProductDeal.setIntAmt(dto.getActTotalAmt());
        trdProductDeal.setTradeAmt(dto.getActTotalAmt());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        trdProductDeal.setRemark(dto.getRemark());
        trdProductDeal.setTotleTradeId(dto.getTradeId());
        trdProductDeal.setCreateUser(dto.getCreateUser());
        trdProductDeal.setCreateDept(dto.getCreateDept());
        trdProductDeal.setCreateTime(dto.getCreateTime());
        trdProductDeal.setUpdateUser(dto.getUpdateUser());
        trdProductDeal.setUpdateTime(dto.getCreateTime());
        trdProductDeal.setbTradeId(dto.getbTradeId());
        trdProductDeal.setBranch(0L);
        if (dto.getDealType().equals("bonusAmtConfirm")) {
            trdProductDeal.setPs(DictCodeList.交易方向_产品除权登记.getValue());
            trdProductDeal.setbTradeId(dto.getBonusPlanId());
            // 分红金额确认交易金额为空、交易日为除权日、交割日为红利下发日 modify by wm start 2019-07-04
            trdProductDeal.setTradeAmt(null);
            trdProductDeal.setTradeDate(dto.getDelDate());
            trdProductDeal.setSettleDate(dto.getBonusDate());
            // trdProductDeal.setTradeDate(iPlatformDate.getSysDate());
            // trdProductDeal.setSettleDate(iPlatformDate.getSysDate());
            // 分红金额确认交易金额为空、交易日为除权日、交割日为红利下发日 modify by wm end 2019-07-04
            trdProductDeal.setShareAmt(dto.getRegDateNum());
            trdProductDeal.setFpriceAmt(dto.getActTotalAmt());
            trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
        }
        else if (dto.getDealType().equals("bonusCash")) {
            trdProductDeal.setPs(DictCodeList.交易方向_产品现金分红.getValue());
            trdProductDeal.setTradeDate(dto.getActBonusDate());
            trdProductDeal.setSettleDate(dto.getActBonusDate());
            trdProductDeal.setFpriceAmt(dto.getBonusCashAmt());
            trdProductDeal.setTradeAmt(dto.getBonusCashAmt());
            trdProductDeal.setRemark(dto.getBonusDealRemark());
        }
        else if (dto.getDealType().equals("bonusShareConfirm")) {
            trdProductDeal.setTradeDate(DateUtil.platDateFromCache());
            trdProductDeal.setSettleDate(dto.getConfirmDate());
            trdProductDeal.setShareAmt(dto.getBonusShareNum());
            trdProductDeal.setIntAmt(dto.getBonusShareAmt());
            trdProductDeal.setTradeAmt(dto.getBonusShareAmt());
            trdProductDeal.setFpriceAmt(dto.getBonusShareAmt());
            trdProductDeal.setPs(DictCodeList.交易方向_产品红利转投.getValue());
            trdProductDeal.setRemark(dto.getBonusDealRemark());
            trdProductDeal.setUnitCprice(dto.getTdyUnitValue());
            trdProductDeal.setCpriceDate(dto.getVluRefDate());
        }
		/**每一个周期型产品期次子产品的现金分红和红利再投需要新建一笔交易 start **/
		if (StringUtil.equals(DictCodeList.是否_是.getValue(),dto.getIsCycle())) {
			if(!StringUtil.isEmptyList(dto.getListCycleProductInfo())){
				for(FinNetProductBonusSchemeDto dtoTmp:dto.getListCycleProductInfo()){
					TrdProductDeal trdProductDealtmp = new TrdProductDeal();
					BeanUtil.copyPropertiesIgnoreNull(trdProductDealtmp,trdProductDeal);
					BussinessCodeParameter parameter = new BussinessCodeParameter();
                    parameter.setCommonBusinessType(DictCodeList.共通业务类型_净值型产品分红方案管理);
                    String tradeId = baseGetBusinessId.buildBussinessCode(parameter); 
					if (isRevoke) {
					    Map<String, Object> map = new HashMap<String, Object>(); 
					    map.put("totleTradeId", trdProductDealtmp.getTotleTradeId());
                        //ADD BY JIANGPENG ON 20210427 TISHKBS-2042 CLONE - 【产品存续期查询】净值型周期型红利再投交易，撤销，审批人审批报错
                        map.put("branch", dtoTmp.getBranch());
                        //ADD BY JIANGPENG ON 20210427 TISHKBS-2042 CLONE - 【产品存续期查询】净值型周期型红利再投交易，撤销，审批人审批报错
					    tradeId = restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/queryTradeIdByTotle",
				            map, String.class);
                    } 
		            dtoTmp.setTradeId(tradeId);
		            trdProductDealtmp.setTradeId(tradeId);
					
					//现金分红
					if (dto.getDealType().equals("bonusCash")) {
						trdProductDealtmp.setFinprodId(dtoTmp.getFinprodId());
						trdProductDealtmp.setBranch(dtoTmp.getBranch());
						trdProductDealtmp.setFpriceAmt(dtoTmp.getBonusCashAmt());
						trdProductDealtmp.setTradeAmt(dtoTmp.getBonusCashAmt());
					}//红利再投 
					else if (dto.getDealType().equals("bonusShareConfirm")) {
						trdProductDealtmp.setShareAmt(dtoTmp.getBonusShareNum());
						trdProductDealtmp.setIntAmt(dtoTmp.getBonusShareAmt());
						trdProductDealtmp.setTradeAmt(dtoTmp.getBonusShareAmt());
						trdProductDealtmp.setFpriceAmt(dtoTmp.getBonusShareAmt());
						//红利再投交易产品ID和branch赋值 yzg 20200310 start
						trdProductDealtmp.setFinprodId(dtoTmp.getFinprodId());
						trdProductDealtmp.setBranch(dtoTmp.getBranch());
						//红利再投交易产品ID和branch赋值 yzg 20200310 end
						
					} 
					listTrdProductDeal.add(trdProductDealtmp);
				}
			}
		} else {
			listTrdProductDeal.add(trdProductDeal);
		}
		/**每一个周期型产品期次子产品需要新建一笔交易 end **/
		return listTrdProductDeal;
    }

    /**
     * 组装 金融产品交易汇总表(TRD_FIN_PRODUCT_DEAL_TOTAL) entity
     *
     * @param dto
     * @return
     */
    private TrdFinProductDealTotal genTrdFinProductDealTotal(FinNetProductBonusSchemeDto dto) {
        // 金融产品交易汇总表(TRD_FIN_PRODUCT_DEAL_TOTAL)
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(dto.getTradeId());
        trdFinProductDealTotal.setBusiType(DictCodeList.业务类型_净值型TA交易.getValue());
        trdFinProductDealTotal.setFinprodId(dto.getFinprodId());
        trdFinProductDealTotal.setPortfolioId(dto.getPortfolioId());
        trdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        trdFinProductDealTotal.setTradeDate(DateUtil.platDateFromCache());
        trdFinProductDealTotal.setSettleDate(DateUtil.platDateFromCache());
        trdFinProductDealTotal.setCcy(dto.getCcy());
        trdFinProductDealTotal.setUnitInt(dto.getUnitBonusVlu());
        trdFinProductDealTotal.setIntAmt(dto.getActTotalAmt());
        trdFinProductDealTotal.setTradeAmt(dto.getActTotalAmt());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setRemark(dto.getRemark());
        trdFinProductDealTotal.setCreateUser(dto.getCreateUser());
        trdFinProductDealTotal.setCreateDept(dto.getCreateDept());
        trdFinProductDealTotal.setCreateTime(dto.getCreateTime());
        trdFinProductDealTotal.setUpdateUser(dto.getUpdateUser());
        trdFinProductDealTotal.setUpdateTime(dto.getCreateTime());
        // 增加total分支序号存储 2019-07-04 add by wm
        trdFinProductDealTotal.setBranch(0L);
        if (dto.getDealType().equals("bonusAmtConfirm")) {
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_产品除权登记.getValue());
            trdFinProductDealTotal.setShareAmt(dto.getRegDateNum());
            trdFinProductDealTotal.setFpriceAmt(dto.getActTotalAmt());
            // 分红金额确认交易金额为空、交易日为除权日、交割日为红利下发日 add by wm start 2019-07-04
            trdFinProductDealTotal.setTradeAmt(null);
            trdFinProductDealTotal.setTradeDate(dto.getDelDate());
            trdFinProductDealTotal.setSettleDate(dto.getBonusDate());
            // 分红金额确认交易金额为空、交易日为除权日、交割日为红利下发日 add by wm end 2019-07-04
        }
        else if (dto.getDealType().equals("bonusCash")) {
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_产品现金分红.getValue());
            trdFinProductDealTotal.setTradeDate(dto.getActBonusDate());
            trdFinProductDealTotal.setSettleDate(dto.getActBonusDate());
            trdFinProductDealTotal.setFpriceAmt(dto.getBonusCashAmt());
            trdFinProductDealTotal.setTradeAmt(dto.getBonusCashAmt());
            trdFinProductDealTotal.setRemark(dto.getBonusDealRemark());
        }
        else if (dto.getDealType().equals("bonusShareConfirm")) {
            trdFinProductDealTotal.setTradeDate(dto.getConfirmDate());
            trdFinProductDealTotal.setSettleDate(dto.getConfirmDate());
            trdFinProductDealTotal.setShareAmt(dto.getBonusShareNum());
            trdFinProductDealTotal.setIntAmt(dto.getBonusShareAmt());
            trdFinProductDealTotal.setTradeAmt(dto.getBonusShareAmt());
            trdFinProductDealTotal.setFpriceAmt(dto.getBonusShareAmt());
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_产品红利转投.getValue());
            trdFinProductDealTotal.setRemark(dto.getBonusDealRemark());
        }
        return trdFinProductDealTotal;
    }

    /**
     * 产品代码的联想下拉框事件
     *
     * @param map 查询参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodMarketId(Map<String, Object> map) {
        User user = SecurityUtil.getLoginUser();
        map.put("userName", user.getUserName());
        return restTemplate.postForPage(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/getFinprodMarketId",
            map, SelectOptionDto.class);
    }

    /**
     * 查询界面询事件
     *
     * @param map 页面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<FinNetProductBonusSchemeDto> selectForPage(Map<String, Object> map) {
        Page<FinNetProductBonusSchemeDto> page = restTemplate.postForPage(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/findPage", map,
            FinNetProductBonusSchemeDto.class);
        return page;
    }

    /**
     * 删除事件
     *
     * @param dto 页面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     *//*
        * public int delete(FinNetProductBonusSchemeDto dto){ return restTemplate.postForObject( ServiceApplication.PRODUCT_SERVICE_NAME +
        * "/FinNetProductBonusScheme/delete", dto, Integer.class); }
        */

    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
            "/com/joyintech/tams/invest/product/productduration/netproductbonusscheme/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }

    /**
     * 设置日期格式 {@inheritDoc}
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if (("delDate".equals(propName)) && value != null) {
            cell.setCellValue(DateUtil.format((Date)value));
        }
        if (("regDate".equals(propName)) && value != null) {
            cell.setCellValue(DateUtil.format((Date)value));
        }
        if (("vluRefDate".equals(propName)) && value != null) {
            cell.setCellValue(DateUtil.format((Date)value));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<FinNetProductBonusSchemeDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }

    /**
     * 分红金额确认、现金分红、红利再投初始化查询
     *
     * @param bonusPlanId 分红方案编号
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public FinNetProductBonusSchemeDto selectInfo(String bonusPlanId) {
        return restTemplate.getForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/selectInfo?bonusPlanId=" + bonusPlanId,
            FinNetProductBonusSchemeDto.class);
    }

    /**
     * 新建界面产品代码的下拉框事件
     *
     * @param map 查询参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodMarketIdList(Map<String, Object> map) {
        User user = SecurityUtil.getLoginUser();
        map.put("userName", user.getUserName());
        return restTemplate.postForPage(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/getFinprodMarketIdList", map,
            SelectOptionDto.class);
    }

    /**
     * 查询产品简称和产品名称
     *
     * @param map 查询参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public FinNetProductBonusSchemeDto getAbbrAndName(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/getAbbrAndName",
            map, FinNetProductBonusSchemeDto.class);
    }

    /**
     * 查询分红方案编号
     *
     * @param map 查询参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public int selectBonuScalcFlag(Map<String, Object> map) {
        return restTemplate.postForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/selectBonuScalcFlag", map, Integer.class);
    }

    /**
     * 产品是否已派发红利
     *
     * @param map 查询参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public String IsDividend(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/IsDividend", map,
            String.class);
    }

    /**
     * 查询交易日历编号
     *
     * @param finprodId 金融产品代码
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public String selectCalendarId(String finprodId) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/selectCalendarId",
            finprodId, String.class);
    }

    /**
     * 验证同一除权日是否重复登录
     *
     * @param map 查询参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public int countDelDate(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/countDelDate", map,
            Integer.class);
    }

    /**
     * 查询基准日未分配余额
     *
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public BigDecimal getTdyUnpaidIntincexp(Map<String, Object> map) {
        return restTemplate.postForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/getTdyUnpaidIntincexp", map, BigDecimal.class);
    }

    /**
     * 查询净值参考日
     *
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Date getVluRefDate(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/getVluRefDate",
            map, Date.class);
    }

    /**
     * 查询参考日净值和份额
     *
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public FinNetProductBonusSchemeDto getTdyUnitValueAndTdyShareAmt(Map<String, Object> map) {
        return restTemplate.postForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/getTdyUnitValueAndTdyShareAmt", map,
            FinNetProductBonusSchemeDto.class);
    }

    /**
     * 查询产品是否存在有效的现金分红或红利再投交易
     *
     * @param map 查询参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public int isExistBonusDeal(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/isExistBonusDeal",
            map, Integer.class);
    }

    /**
     * 查询净值产品的分红模式 
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public String queryBonusMode(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/queryBonusMode",
            map, String.class);
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public int insertPosition(FinNetProductBonusSchemeDto dto) {
        // 可用持仓流水、可用现金流水处理
        PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
        PortfolioPositionsFlowInputDto inputDto = new PortfolioPositionsFlowInputDto();
        TrdProductDeal deal = new TrdProductDeal();
        Date updateTime = new Date();
        deal.setTradeId(dto.getTradeId());
        deal.setBusiType(DictCodeList.共通业务类型_净值型产品分红方案管理.getValue());
        deal.setFinprodId(dto.getFinprodId());
        deal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        deal.setTradeDate(DateUtil.platDateFromCache());
        deal.setSettleDate(DateUtil.platDateFromCache());
        deal.setCcy(dto.getCcy());
        deal.setUnitInt(dto.getUnitBonusVlu());
        deal.setShareAmt(dto.getRegDateNum());
        deal.setIntAmt(dto.getBonusCashAmt());
        deal.setTradeAmt(dto.getBonusCashAmt());
        deal.setPs(DictCodeList.交易方向_产品现金分红.getValue());
        deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        deal.setRemark(dto.getRemark());
        deal.setTotleTradeId(dto.getTradeId());
        deal.setCreateUser(dto.getCreateUser());
        deal.setCreateDept(deal.getCreateDept());
        deal.setCreateTime(deal.getCreateTime());
        deal.setUpdateUser(dto.getUpdateUser());
        deal.setUpdateTime(updateTime);
        inputDto.setPositionType(DictCodeList.持仓类型_可用.getValue());
        inputDto.setTrdProductDeal(deal);
        inputDto.setPortfolioId(dto.getFinprodId());
        List<PtlSecPositionFlowDto> listPos = baseAssetCashPositionCommService.positionDeal(inputDto);
        if (listPos != null) {
            ptlPositionHoldDto.setPtlSecPositionFlows(listPos);
        }

        PortfolioCashPositionsFlowInputDto cashInputDto = new PortfolioCashPositionsFlowInputDto();
        cashInputDto.setPositionType(DictCodeList.持仓类型_可用.getValue());
        cashInputDto.setTrdProductDeal(deal);
        List<StlPayOrdersDto> stlPayOrders = new ArrayList<StlPayOrdersDto>();
        if (dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0) {
            for (BussTrdTransferOrderDto orderDto : dto.getBussTrdTransferOrder()) {
                StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
                stlPayOrdersDto.setOrderId(orderDto.getOrderId());
                stlPayOrdersDto.setOrderType(orderDto.getOrderType());
                stlPayOrdersDto.setMyAcctNo(orderDto.getMyAcctNo());
                stlPayOrdersDto.setCcy(orderDto.getCcy());
                stlPayOrdersDto.setSettleDate(orderDto.getSettleDate());
                stlPayOrdersDto.setSettleAmt(orderDto.getSettleAmt());
                stlPayOrders.add(stlPayOrdersDto);
            }
        }
        cashInputDto.setStlPayOrders(stlPayOrders);
        cashInputDto.setPortfolioId(dto.getFinprodId());
        List<PtlCashPositionFlowDto> listCash = baseAssetCashPositionCommService.cashDeal(cashInputDto);
        if (listCash != null) {
            ptlPositionHoldDto.setPtlCashPositionFlows(listCash);
        }
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/insertPosition",
            ptlPositionHoldDto, Integer.class);
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public List<FinNetProductBonusDealDto> queryBonusDetail(Map<String, Object> map) {
        List<FinNetProductBonusDealDto> list = restTemplate.postForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/queryBonusDetail", map,
            FinNetProductBonusDealDto.class);
        return list;
    }

    /**
     * 生成划款指令信息
     * 
     * @param entity 产品费用支付Dto
     * @return 划款指令信息
     * @see
     * @since 1.0
     */
    private StlPayOrders genStlPayOrdersDto(FinNetProductBonusSchemeDto entity) {
        entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(entity.getBussTrdTransferOrder()));
        // 划款指令表Entity
        StlPayOrders stlPayOrders = new StlPayOrders();
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            BussTrdTransferOrderDto bussTrdTransferOrderDto = entity.getBussTrdTransferOrder().get(0);
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, bussTrdTransferOrderDto);
            stlPayOrders.setTradeId(entity.getTradeId());
            stlPayOrders.setWorkflowId(entity.getWorkflowId());

            List<? extends DataDictItem> yesNoItemList = SysDataUtil.getDataDictItem(DictCode.是否.getValue());
            yesNoItemList.forEach(item -> {
                if (StringUtil.equals(item.getItemValue(), stlPayOrders.getIsNeedSend())) {
                    stlPayOrders.setIsNeedSend(item.getItemCode());
                }
            });
        }
        stlPayOrders.setCreateTime(entity.getCreateTime());
        stlPayOrders.setUpdateTime(entity.getUpdateTime());
        return stlPayOrders;
    }

    public String queryBonusPlanId(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/queryBonusPlanId",
            map, String.class);
    }

    /**
     * 查询交易详细数据
     *
     * @param map
     * @return
     */
    public FinNetProductBonusSchemeDto selectDetailInfo(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/selectDetailInfo",
            map, FinNetProductBonusSchemeDto.class);
    }

    /**
     * 是否做过分红金额确认，做过确认则带出交易id
     *
     * @param dto
     * @return
     */
    public String idBonusAmtConfirm(FinNetProductBonusSchemeDto dto) {
        Map<String, Object> param = new HashMap<String, Object>();
        // 当产品为分档产品时，产品id为赋值为产品款型
        if (StringUtil.isNotEmpty(dto.getProdStyle())) {
            param.put("finprodId", dto.getProdStyle());
        }
        else {
            param.put("finprodId", dto.getFinprodId());
        }
        param.put("bTradeId", dto.getBonusPlanId());
        param.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/idBonusAmtConfirm",
            param, String.class);
    }

    public List<TrdFinProductDealTotal> queryAfterTrdFinProductDealTotal(FinNetProductBonusSchemeDto dto) {
        return restTemplate.postForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/queryAfterTrdFinProductDealTotal", dto,
            TrdFinProductDealTotal.class);
    }

    public List<FinNetProductDurationLayerDto> getProdStyleList(Map<String, Object> map) {
        return restTemplate.postForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/queryLayAmtDetailByFinprodId", map,
            FinNetProductDurationLayerDto.class);
    }

    public FinProductAdd selectFinProductADD(String finprodId) {
        return restTemplate.getForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/selectFinProductADD?finprodId=" + finprodId,
            FinProductAdd.class);
    }

    /**
     * 查询产品是否已完全有效的现金分红或红利再投交易
     *
     * @param map 查询参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public BigDecimal isExceedBonusDeal(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductBonusScheme/isExceedBonusDeal",
            map, BigDecimal.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> sendProductBonusSchemeToSale(List<String> bonusPlanIds) {
        return restTemplate.postForObject(
            ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/FinProductInterf/sendProductBonusSchemeToSale", bonusPlanIds,
            Map.class);
    }
    public List<FinNetProductBonusSchemeDto> findCycleDataTableInfo(Map<String,Object> map) {
        return restTemplate.postForList(ServiceApplication.PRODUCT_SERVICE_NAME
                + "/FinNetProductBonusScheme/findCycleDataTableInfo",map,
                FinNetProductBonusSchemeDto.class);
    }
    /**
     * 查询资金账户信息
     *
     * @param map 画面参数
     * @return 调用微服务
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public List<ActualTimeCapital001Dto> selectCapital(Map<String, Object> map){
        // add by caiqun  工单YHZGCPB20200806001实时资金头寸里不能同时查询多个产品的现金 20200813 start
        String portfolioId = (String) map.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            map.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        String acctNo = (String) map.get("acctNo");
        if (StringUtil.isNotEmpty(portfolioId)) {
            map.put("acctNoList", StringUtil.splitToList(GlobalConstant.COMMA, acctNo));
        }
        // add by caiqun 工单YHZGCPB20200806001实时资金头寸里不能同时查询多个产品的现金 20200813 end
        Page<ActualTimeCapital001Dto> page = restTemplate.postForPage(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ActualTimeCapital/findPage",
                map, ActualTimeCapital001Dto.class);
        List<ActualTimeCapital001Dto> list =  page.getRoot();
        return list;
    }
}
