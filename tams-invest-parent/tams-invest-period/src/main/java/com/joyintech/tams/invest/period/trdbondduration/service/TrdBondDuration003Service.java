package com.joyintech.tams.invest.period.trdbondduration.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.constant.IntRateType;
import com.joyintech.calculation.shell.AccurIntCalculator;
import com.joyintech.calculation.shell.param.AccurIntCalInParameter;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.shell.param.SecInfoModel;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.TrdBondPtlSecExerciseInfo;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.impl.BussTrdTransferOrderImplService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdbondduration.dto.SecPositionExerciseInfo003Dto;
import com.joyintech.tams.invest.period.trdbondduration.dto.TrdBondDuration003ProcessDto;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.CommentFunction;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

import static com.joyintech.tams.invest.common.asset.service.BondAssetCalcService.toLocalDate;
import static com.joyintech.tams.invest.common.asset.service.BondAssetCalcService.toSecInfoModel;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.period.trdbondduration.service
 * @Description: 债券存续期管理-查询
 * @date Date : 2018年09月21日 14:32
 */
@Service
public class TrdBondDuration003Service extends BaseService<TrdBondDuration003ProcessDto> implements BaseProcessService<TrdBondDuration003ProcessDto> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;

    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;

    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;

    @Autowired
    private PeriodCommonService periodCommonService;

    @Autowired
    private BussTrdTransferOrderImplService bussTrdTransferOrderImplService;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private TrdTradeFeeTotalMapper trdTradeFeeTotalMapper;

    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommService;
    
    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;
    
    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    @Override
    public void doComplete(TrdBondDuration003ProcessDto entity, OperationType type) {
        if (type != OperationType.REVOKE) {
            // 插入Entity到对应的业务流程DB中
            insertData(entity);
            execute交易共通(entity, DictCodeListExt1.交易流程状态_有效);
            调用估值接口(entity);
        }
        else if (type == OperationType.REVOKE) {
            execute交易共通(entity, DictCodeListExt1.交易流程状态_撤销);

            Map<String, Object> params = new HashMap<>();
            params.put("workflowId", entity.getWorkflowId());
            params.put("bizCode", entity.getBizCode());
            params.put("updateUser", SecurityUtil.getLoginUser().getUserName());
            params.put("revokeReason", entity.getRevokeReason());
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/doRevokeComplete?", params,
                String.class);
        }
        baseTradeProductDealCommService.deleteWflData(entity.getWorkflowId());
    }

    private void 调用估值接口(TrdBondDuration003ProcessDto entity) {
        entity.getTrdProductDeals().forEach(trdProductDeal -> {
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            inputDto.setTrdProductDeal(trdProductDeal);
            entity.getTrdProductDealAdds().forEach(trdProductDealAdd -> {
                if (trdProductDeal.getTradeId().equals(trdProductDealAdd.getTradeId())) {
                    inputDto.setTrdProductDealAdd(trdProductDealAdd);
                }
            });
            entity.getStlPayOrders().forEach(orders -> {
                if (trdProductDeal.getTradeId().equals(orders.getTradeId())) {
                    inputDto.setStlPayOrders(orders);
                }
            });

            inputDto.setProdid(inputDto.getTrdProductDealAdd().getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            // 子组合估值调用
            String actPortfolio = AllTradeCommonUtil.exchangePortfolio(inputDto.getTrdProductDealAdd().getPortfolioId());
            if (actPortfolio != null && !actPortfolio.equals(inputDto.getTrdProductDealAdd().getPortfolioId())) {
                inputDto.setActPortfolioId(actPortfolio);
            }

            List<PtlTrade> ptlTradeList = new ArrayList<>();
            for (PtlTrade ptlTrade : entity.getPtlTrades()) {
                if (trdProductDeal.getTradeId().equals(ptlTrade.getBusiNo())) {
                    ptlTradeList.add(ptlTrade);
                }
            }
            inputDto.setPtlTradeList(ptlTradeList);
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
        });
    }

    @Override
    public void doInsert(TrdBondDuration003ProcessDto entity, OperationType type) {
        // 将附件和当前业务对象绑定（更新附件的业务代码）
        fileAttachmentProcess.updateBussinessNo(
            entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getBizCode());
        if (!StringUtil.equals(entity.getOperateType(), "submit")) {
            return;
        }
        saveDataWhenStartWorkflow(entity, type);
    }

    @Override
    public void doUpdate(TrdBondDuration003ProcessDto entity) {
        // 将附件和当前业务对象绑定（更新附件的业务代码）
        fileAttachmentProcess.updateBussinessNo(
                entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getBizCode());

        genFinProductTradeDto(entity);

        ProcessMark newEntity;
        try {
            newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdBondDuration003ProcessDto.class, true);
            // 工作流ID
            newEntity.setWorkflowId(entity.getWorkflowId());
            newEntity.setStatus(entity.getStatus());
            newEntity.setBusinessData(JsonUtils.object2Json(entity));
            // 更新业务表数据
            // 操作留痕表
            WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
        }
        catch (Exception e) {
            logger.error("更新留痕表出错", e);
        }

        saveDataWhenStartWorkflow(entity, OperationType.MODIFY);
    }

    private void saveDataWhenStartWorkflow(TrdBondDuration003ProcessDto entity, OperationType type) {
        if (BussinessConstant.SUBMIT.equals(entity.getOperateType()) && type != OperationType.REVOKE) {
            // 插入Entity到对应的业务流程DB中
            insertWflData(entity);
            execute交易共通(entity, DictCodeListExt1.交易流程状态_流程中);
        }
        else if (type == OperationType.REVOKE) {
        	 //删除原来数据
        	 wflTrdFinProductDealTotalMapper.deleteByWorkflowId(entity.getWorkflowId());
             // TODO
        	 Map<String, Object> params = new HashMap<>();
             params.put("bizCode", entity.getBizCode());
             String ps = entity.getPs();
             WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
             BeanUtils.copyProperties(entity.getTrdFinProductDealTotal(), wflTrdFinProductDealTotal, "createTime", "updateTime");
             wflTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
             wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
             wflTrdFinProductDealTotal.setPs(entity.getPs());
             wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);
        }
    }

    private void insertData(TrdBondDuration003ProcessDto entity) {

        //多级法人改造 add by xr 20200711
        if(MultilevelDataAuthorityUtil.isEnabled()){
            // 机构代码
            entity.getTrdFinProductDealTotal().setInstitutionCode(MultilevelDataAuthorityUtil.getOrganizationCode(entity.getCreateUser()));
        }
        //多级法人改造 add by xr  20200711

        trdFinProductDealTotalMapper.insertSelective(entity.getTrdFinProductDealTotal());

        paTrdFinProductDealTotalMapper.insertSelective(entity.getPaTrdFinProductDealTotal());

        entity.getTrdProductDeals().forEach(trdProductDeal -> {
            trdProductDealMapper.insertSelective(trdProductDeal);
        });

        entity.getTrdProductDealAdds().forEach(trdProductDeal -> {
            trdProductDealAddMapper.insertSelective(trdProductDeal);
        });

        entity.getPaTrdProductDeals().forEach(trdProductDeal -> {
            paTrdProductDealMapper.insertSelective(trdProductDeal);
        });

        entity.getPtlTrades().forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));

        trdTradeFeeTotalMapper.insertSelective(entity.getTrdTradeFeeTotal());

        entity.getTrdTradeFeeDetails().forEach(trdTradeFeeDetail -> trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail));
        
        entity.getStlPayOrders().forEach(stlPayOrders -> {
        	stlPayOrdersMapper.insert(stlPayOrders);
        });
    }

    private void insertWflData(TrdBondDuration003ProcessDto entity) {
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtils.copyProperties(entity.getTrdFinProductDealTotal(), wflTrdFinProductDealTotal, "createTime", "updateTime");
        wflTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);

        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        BeanUtils.copyProperties(entity.getPaTrdFinProductDealTotal(), wflPaTrdFinDealTotal, "createTime", "updateTime");
        wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);

        entity.getTrdProductDeals().forEach(trdProductDeal -> {
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtils.copyProperties(trdProductDeal, wflTrdProductDeal, "createTime", "updateTime");
            wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
        });

        entity.getTrdProductDealAdds().forEach(trdProductDeal -> {
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtils.copyProperties(trdProductDeal, wflTrdProductDealAdd, "createTime", "updateTime");
            wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        });

        entity.getPaTrdProductDeals().forEach(trdProductDeal -> {
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtils.copyProperties(trdProductDeal, wflPaTrdProductDeal, "createTime", "updateTime");
            wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
        });
    }

    /**
     * 批量调用共通 <功能详细描述>
     * 
     * @param entity 业务实体
     * @param 交易流程状态
     * @see
     * @since 1.0
     */
    private void execute交易共通(TrdBondDuration003ProcessDto entity, DictCodeListExt1 交易流程状态) {
        // 调用业务组件.【金融产品交易共通.交易产品接口】
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        tradeDto.setWorkflowStatus(交易流程状态.getValue());

        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        if (DictCodeListExt1.交易流程状态_撤销 != 交易流程状态) {
            entity.getTrdProductDeals().forEach(trdProductDeal -> {
                BaseTradeProductDealDto dto = new BaseTradeProductDealDto();
                dto.setTrdProductDeal(trdProductDeal);
                entity.getTrdProductDealAdds().forEach(trdProductDealAdd -> {
                    if (trdProductDealAdd.getTradeId().equals(trdProductDeal.getTradeId())) {
                        String parentPortfolioId = AllTradeCommonUtil.exchangePortfolio(trdProductDealAdd.getPortfolioId());
                        if (parentPortfolioId != null && !parentPortfolioId.equals(trdProductDealAdd.getPortfolioId())) {
                            trdProductDealAdd.setPortfolioId(parentPortfolioId);
                        }
                        dto.setTrdProductDealAdd(trdProductDealAdd);
                    }
                });
                entity.getStlPayOrders().forEach(stlPayOrders -> {
                    if (stlPayOrders.getTradeId().equals(trdProductDeal.getTradeId())) {
                        StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
                        BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, stlPayOrders);
                        dto.setStlPayOrdersList(Arrays.asList(stlPayOrdersDto));
                    }
                });

                baseTradeProductDealDtos.add(dto);
            });
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        }
        else {
            List<BaseTradeProductDealDto> baseTradeProductDealDtos1 = periodCommonService.findEffectiveDeal(entity.getBizCode(),
                交易流程状态);
            for (BaseTradeProductDealDto baseTradeProductDealDto : baseTradeProductDealDtos1) {
                TrdProductDeal deal1 = baseTradeProductDealDto.getTrdProductDeal();
                // add by jiangpeng on 20210524 start ZGBSSE-226
                deal1.setPayDate(entity.getPayDate());
                // add by jiangpeng on 20210524 end ZGBSSE-226
                deal1.setTradeId(entity.getBizCode() + 'R');
                deal1.setCreateUser(tradeDto.getCreateUser());
                deal1.setCreateTime(tradeDto.getCreateTime());
                deal1.setCreateDept(tradeDto.getCreateDept());
                deal1.setUpdateTime(tradeDto.getUpdateTime());
                deal1.setUpdateUser(tradeDto.getUpdateUser());
            }
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos1);
        }
        baseTradeProductDealCommServiceImpl.execute(tradeDto);
    }

    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommServiceImpl.delete(workflowId);
    }

    public CommentFunction<TrdBondDuration003ProcessDto, String> 生成业务说明() {
        return entity -> WorkFlowUtil.concatComment(
            this.getMessage("TrdBondDuration001.finprodName") + GlobalConstant.OPEN_PARENTHESIS + entity.getFinprodName()
                                                    + GlobalConstant.CLOSE_PARENTHESIS,
            "资产交易类型" + GlobalConstant.OPEN_PARENTHESIS + DictCodeListExt1.平安业务类型_债券类资产行权.getCodeName()
                                                                                        + GlobalConstant.CLOSE_PARENTHESIS,
            this.getMessage(
                "TrdBondDuration001.busiCode") + GlobalConstant.OPEN_PARENTHESIS + entity.getBizCode() + GlobalConstant.CLOSE_PARENTHESIS);
    }

    public void genFinProductTradeDto(TrdBondDuration003ProcessDto entity) {
        Date sysDate = DateUtil.platDateFromCache();
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getBizCode());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        trdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        trdFinProductDealTotal.setBranch(0L);
        trdFinProductDealTotal.setTradeDate(sysDate);
        trdFinProductDealTotal.setVdate(entity.getVdate());
        trdFinProductDealTotal.setMdate(entity.getMdate());
        trdFinProductDealTotal.setSettleDate(entity.getSettleDate());
        trdFinProductDealTotal.setCcy(entity.getCcy());
        trdFinProductDealTotal.setCpriceAmt(entity.getcPriceAmt());
        trdFinProductDealTotal.setShareAmt(ArithmeticUtil.divide(entity.getQty(), new BigDecimal(100), 4));
        trdFinProductDealTotal.setPrinAmt(entity.getcPriceAmt());
        trdFinProductDealTotal.setIntAmt(BigDecimal.ZERO);
        trdFinProductDealTotal.setFpriceAmt(entity.getfPriceAmt());
        if (DictCodeListExt2.收取方式_回款前扣除.getValue().equals(entity.getCollectType())) {
            trdFinProductDealTotal.setFeeAmtPay(entity.getChannelFee());
        }
        trdFinProductDealTotal.setTradeAmt(entity.getfPriceAmt());
        trdFinProductDealTotal.setFeeAmt(trdFinProductDealTotal.getFeeAmtPay());
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_行权_赎回.getValue());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setIsClean(DictCodeList.是否_是.getValue());
        trdFinProductDealTotal.setChlId(entity.getChlId());
        trdFinProductDealTotal.setTradeMarket(entity.getTradeMarket());
        trdFinProductDealTotal.setTrader(SecurityUtil.getLoginUser().getUserName());
        trdFinProductDealTotal.setRemark(entity.getRemark());
        trdFinProductDealTotal.setPayDate(entity.getPayDate());
        trdFinProductDealTotal.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
        entity.setTrdFinProductDealTotal(trdFinProductDealTotal);

        TrdTradeFeeTotal feeTotal = new TrdTradeFeeTotal();
        feeTotal.setTotleTradeId(entity.getBizCode());
        feeTotal.setFeeAmt(entity.getChannelFee());
        // 交易费用明细表.费用类型 = “结算费”
        feeTotal.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
        if (DictCodeListExt2.收取方式_回款前扣除.getValue().equals(entity.getCollectType())) {
            feeTotal.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        }
        else {
            feeTotal.setIsPayWithTrade(DictCodeList.是否_否.getValue());
        }
        entity.setTrdTradeFeeTotal(feeTotal);

        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        paTrdFinProductDealTotal.setTotleTradeId(entity.getBizCode());
        paTrdFinProductDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
//        paTrdFinProductDealTotal.setTheoryAmt(entity.getIntAmtTheory().add(entity.getcPriceAmt()));
        paTrdFinProductDealTotal.setTheoryAmt(ArithmeticUtil.doAdd(entity.getIntAmtTheory(),entity.getcPriceAmt()));
        entity.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);

        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtos = bussTrdTransferOrderImplService.save(
            entity.getBussTrdTransferOrder());

        List<StlPayOrders> stlPayOrdersList = new ArrayList<>();
        entity.setStlPayOrders(stlPayOrdersList);

        Map<String, BussTrdTransferOrderDto> map = bussTrdTransferOrderDtos.stream().collect(JoyinCollectors.toMap(
            stlPayOrders -> stlPayOrders.getPortfolioId() + "_" + stlPayOrders.getSecAcctId() + "_" + stlPayOrders.getInvAim(),
            stlPayOrders -> stlPayOrders));

        List<TrdProductDeal> trdProductDeals = new ArrayList<>();
        List<TrdProductDealAdd> trdProductDealAdds = new ArrayList<>();
        List<PaTrdProductDeal> paTrdProductDeals = new ArrayList<>();
        List<PtlTrade> ptlTrades = new ArrayList<>();
        List<TrdTradeFeeDetail> trdTradeFeeDetails = new ArrayList<>();
        
        //update中删除之前的流水数据 start
        if(entity.getTrdProductDeals()!=null) {
        	PtlSecPositionFlowDto pFlowDto = new PtlSecPositionFlowDto();
        	pFlowDto.setEntrust(true);
        	for(TrdProductDeal oldDeal:entity.getTrdProductDeals()) {
        		pFlowDto.setTradeId(oldDeal.getTradeId());
        		basePtlSecPositionInfoService.deleteSecPosition(pFlowDto);
        	}
        }
        //update中删除之前的流水数据 end
        
        entity.setTrdProductDeals(trdProductDeals);
        entity.setTrdProductDealAdds(trdProductDealAdds);
        entity.setPaTrdProductDeals(paTrdProductDeals);
        entity.setPtlTrades(ptlTrades);
        entity.setTrdTradeFeeDetails(trdTradeFeeDetails);

        // 单位面值
        entity.setParValue(this.getUnitPrice(entity.getFinprodId(), entity.getSettleDate()));

        entity.getTrdBondPtlSecExerciseInfos().forEach(trdBondPtlSecExerciseInfo -> {
            BussinessCodeParameter parameter = new BussinessCodeParameter();
            parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
            TrdProductDeal deal = new TrdProductDeal();
            deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
            deal.setTotleTradeId(entity.getBizCode());
            deal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
            deal.setFinprodId(entity.getFinprodId());
            deal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
            deal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            deal.setBranch(0L);
            deal.setTradeDate(sysDate);
            deal.setVdate(entity.getVdate());
            deal.setMdate(entity.getMdate());
            deal.setSettleDate(entity.getSettleDate());
            deal.setCcy(entity.getCcy());
            deal.setCpriceAmt(trdBondPtlSecExerciseInfo.getcPriceAmt());
            deal.setShareAmt(ArithmeticUtil.divide(trdBondPtlSecExerciseInfo.getQty(), new BigDecimal(100), 4));
            deal.setPrinAmt(trdBondPtlSecExerciseInfo.getcPriceAmt());
            deal.setIntAmt(BigDecimal.ZERO);
            deal.setFpriceAmt(trdBondPtlSecExerciseInfo.getcPriceAmt());
            if (DictCodeListExt2.收取方式_回款前扣除.getValue().equals(entity.getCollectType())) {
                deal.setFeeAmtPay(trdBondPtlSecExerciseInfo.getChannelFee());
            }
            deal.setTradeAmt(trdBondPtlSecExerciseInfo.getSettleAmt());
            deal.setFeeAmt(trdBondPtlSecExerciseInfo.getChannelFee());
            deal.setPs(DictCodeList.交易方向_行权_赎回.getValue());
            deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            deal.setIsCancel(DictCodeList.是否_否.getValue());
            deal.setIsClean(DictCodeList.是否_是.getValue());
            deal.setChlId(entity.getChlId());
            deal.setTradeMarket(entity.getTradeMarket());
            deal.setTrader(SecurityUtil.getLoginUser().getUserName());
            deal.setSecManageAcctId(trdBondPtlSecExerciseInfo.getSecAcctId());
            deal.setPayDate(entity.getPayDate());
            deal.setAccrNum(deal.getShareAmt());
            deal.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
            deal.setInvAim(trdBondPtlSecExerciseInfo.getInvAim());
            deal.setParValue(entity.getParValue());
            trdProductDeals.add(deal);

            BussTrdTransferOrderDto bussTrdTransferOrderDto =new BussTrdTransferOrderDto();
            if(deal.getChlId() != null && StringUtil.isNotEmpty(deal.getChlId())){
            	bussTrdTransferOrderDto = map.get(
                      trdBondPtlSecExerciseInfo.getPortfolioId() + "_" + entity.getChlId() + "_"
                    		  									 + trdBondPtlSecExerciseInfo.getInvAim());
            }else{
            	bussTrdTransferOrderDto = map.get(
                        trdBondPtlSecExerciseInfo.getPortfolioId() + "_" + trdBondPtlSecExerciseInfo.getSecAcctId() + "_"
                      		  									 + trdBondPtlSecExerciseInfo.getInvAim());
            }
//            BussTrdTransferOrderDto bussTrdTransferOrderDto = map.get(
//                trdBondPtlSecExerciseInfo.getPortfolioId() + "_" + trdBondPtlSecExerciseInfo.getSecAcctId() + "_"
//                                                                      + trdBondPtlSecExerciseInfo.getInvAim());
            if (bussTrdTransferOrderDto != null) {
                StlPayOrders order = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(order, bussTrdTransferOrderDto);
                order.setTradeId(deal.getTradeId());
                stlPayOrdersList.add(order);
            }

            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            trdProductDealAdd.setPortfolioId(trdBondPtlSecExerciseInfo.getPortfolioId());
            trdProductDealAdd.setTradeId(deal.getTradeId());
            trdProductDealAdds.add(trdProductDealAdd);

            PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
            paTrdProductDeal.setTradeId(deal.getTradeId());
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
            paTrdProductDeals.add(paTrdProductDeal);

            PtlTrade ptlTrade = new PtlTrade();
            ptlTrade.setPortfolioId(trdBondPtlSecExerciseInfo.getPortfolioId());
            ptlTrade.setBusiNo(deal.getTradeId());
            ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade.setBusiId(entity.getFinprodId());
            ptlTrade.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
            ptlTrade.setSettleDate(entity.getSettleDate());
            ptlTrades.add(ptlTrade);

            String parentPortfolioId = AllTradeCommonUtil.exchangePortfolio(trdBondPtlSecExerciseInfo.getPortfolioId());
            if (parentPortfolioId != null && !parentPortfolioId.equals(trdBondPtlSecExerciseInfo.getPortfolioId())) {
                PtlTrade ptlTrade1 = new PtlTrade();
                BeanUtil.copyPropertiesIgnoreNull(ptlTrade1, ptlTrade);
                ptlTrade1.setPortfolioId(parentPortfolioId);
                ptlTrades.add(ptlTrade1);
            }

            TrdTradeFeeDetail feeDetail = new TrdTradeFeeDetail();
            feeDetail.setTradeId(deal.getTradeId());
            feeDetail.setFeeAmt(trdBondPtlSecExerciseInfo.getChannelFee());
            // 交易费用明细表.费用类型 = “结算费”
            feeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
            if (DictCodeListExt2.收取方式_回款前扣除.getValue().equals(entity.getCollectType())) {
                feeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            }
            else {
                feeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            }
            trdTradeFeeDetails.add(feeDetail);
        });
    }

    /**
     * 计算单位面值
     * author sjj
     * @param finprodId 金融产品代码
     * @param settleDate 交割日
     * @return 单位面值
     * @see
     * @since 1.0
     */
    public BigDecimal getUnitPrice(String finprodId, Date settleDate) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("finprodId", finprodId);
        parameters.put("settleDate", DateUtil.format(settleDate));
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getUnitPrice", parameters,
                BigDecimal.class);
    }

    @Override
    public void doBack(TrdBondDuration003ProcessDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBizCode());
        deleteWflData(entity);
    }
    
    private void deleteWflData(TrdBondDuration003ProcessDto entity) {
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtils.copyProperties(entity.getTrdFinProductDealTotal(), wflTrdFinProductDealTotal, "createTime", "updateTime");
        wflTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflTrdFinProductDealTotalMapper.deleteByPrimaryKey(wflTrdFinProductDealTotal);

        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        BeanUtils.copyProperties(entity.getPaTrdFinProductDealTotal(), wflPaTrdFinDealTotal, "createTime", "updateTime");
        wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflPaTrdFinDealTotalMapper.deleteByPrimaryKey(wflPaTrdFinDealTotal);

        entity.getTrdProductDeals().forEach(trdProductDeal -> {
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtils.copyProperties(trdProductDeal, wflTrdProductDeal, "createTime", "updateTime");
            wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealMapper.deleteByPrimaryKey(wflTrdProductDeal);
        });

        entity.getTrdProductDealAdds().forEach(trdProductDeal -> {
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtils.copyProperties(trdProductDeal, wflTrdProductDealAdd, "createTime", "updateTime");
            wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealAddMapper.deleteByPrimaryKey(wflTrdProductDealAdd);
        });

        entity.getPaTrdProductDeals().forEach(trdProductDeal -> {
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtils.copyProperties(trdProductDeal, wflPaTrdProductDeal, "createTime", "updateTime");
            wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflPaTrdProductDealMapper.deleteByPrimaryKey(wflPaTrdProductDeal);
        });
    }
    
    @Override
    public void doRevoke(TrdBondDuration003ProcessDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBizCode());
    }

    public List<SecPositionExerciseInfo003Dto> findSecPosition(Map<String, Object> pageParam) {
        pageParam.put("loginUserName", SecurityUtil.getLoginUser().getUserName());
        return restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondDuration/findSecPosition", pageParam,
            SecPositionExerciseInfo003Dto.class);
    }

    /**
     * 计算债券单位应计利息
     *
     * @param pageParam 业务实体
     * @return 单位应计利息
     * @see
     * @since 1.0
     */
    private BigDecimal calc(Map<String, Object> pageParam) {

        // 如果交易场所为上交所、深交所以外的情况
        boolean isJiaoYiSuo = false;

        //查询债券单位应计利息
        AccurIntCalInParameter accurIntCalInParameter = new AccurIntCalInParameter();
        // 百元面值应计利息
        BigDecimal accurInt = BigDecimal.ZERO;
        //单位利息
        BigDecimal unitInt = BigDecimal.ZERO;

        final int SCALE = 12;
        // 计算日
        accurIntCalInParameter.setCalDate(toLocalDate(DateUtil.parse((String)pageParam.get("exerciseDate"))));

        // a.当前债券面额取得（根据还本计划计算）
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("finprodId", pageParam.get("finprodId"));
        paraMap.put("settleDate", DateUtil.formatDate((String)pageParam.get("exerciseDate")));
        BigDecimal curSecAmt = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getCurSecAmt", paraMap, BigDecimal.class);

        Map<String, Object> tempParamMap = new HashMap<String, Object>();
        tempParamMap.put("finprodId", pageParam.get("finprodId"));
        // b.获取计息区间投资端利率报价信息（债券的利率重置信息）
        // List<BondsIntReset> bondsIntResetList = transactionBond001Mapper.getInvRateQuo(dto);
        List<FinCashCalcRule> finCashCalcRuleList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/selectBondsIntResetList", tempParamMap,
                FinCashCalcRule.class);

        // c.获取债券的付息计划信息
        List<FinCashPlanDto> finCashPlanList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getFinCashPlan", tempParamMap, FinCashPlanDto.class);
        List<SecIntSchedule> invScheduleList = new ArrayList<>();
        List<FinCashPlanDto> intSchList = new ArrayList<FinCashPlanDto>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(), DictCodeListExt1.现金流一级类型_利息.getValue())
                    && item.getVdateUnadjust() != null) {
                SecIntSchedule schedule = new SecIntSchedule();
                schedule.setTheoryStartDate(toLocalDate(item.getVdateUnadjust()));
                schedule.setTheoryCutoffDate(toLocalDate(item.getMdateUnadjust()));
                schedule.setActualStartDate(toLocalDate(item.getVdate()));
                schedule.setActualCutoffDate(toLocalDate(item.getMdate()));
                schedule.setAmount(item.getIntAmt());
                invScheduleList.add(schedule);
                intSchList.add(item);
            }
        }
        // 计算行权收益率，非行权时，取大于当日的行权日
        tempParamMap.put("ps", DictCodeList.交易方向_行权_赎回);
        // d.获取债券基本资料信息
        SecInfoModelDto secInfoModelDto = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecInfoModel", tempParamMap, SecInfoModelDto.class);
        SecInfoModel secInfoModel = toSecInfoModel(secInfoModelDto);

        List<PaperIrReset> paperIrResetList = finCashCalcRuleList.stream().map(item -> {
            PaperIrReset ret = new PaperIrReset();
            // BeanUtil.copyPropertiesIgnoreNull(ret, item);
            ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            ret.setParRate(item.getYield());
            ret.setParRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            return ret;
        }).collect(Collectors.toList());
        if (paperIrResetList != null && paperIrResetList.size() > 0) {
            paperIrResetList.get(0).setBaseRateEffDate(BasicUtil.dateToLocal(secInfoModelDto.getStartDate()));
        }
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            // 多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            // 多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        // 债券基本资料
        accurIntCalInParameter.setSecInfoModel(secInfoModel);
        // 付息计划
        accurIntCalInParameter.setSecIntScheduleList(invScheduleList);
        // 利率重置信息
        accurIntCalInParameter.setPaperIrResetList(paperIrResetList);
        // 从计算工厂中创建应计利息计算器
        AccurIntCalculator calc = CalculatorFactory.createAccurIntCalculator();
        // 计算利息
        AccurIntCalOutParameter accurIntCalOutParameter = calc.calculate(accurIntCalInParameter);
        accurInt = ArithmeticUtil.round(accurIntCalOutParameter.getInterest(), SCALE, RoundingMode.HALF_UP);

        if(StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), secInfoModelDto.getTradeMarket())
                || StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), secInfoModelDto.getTradeMarket())){
            isJiaoYiSuo = true;
        }
        if(StringUtil.isNotEmpty(accurInt)){
            if (isJiaoYiSuo) {
                unitInt = ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                                Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                        "SEC_INT_SCALE_EXCHANGE")) ? "8" : CacheUtil.getSysParam("SEC_INT_SCALE_EXCHANGE")),
                                RoundingMode.HALF_UP);
            }
            else {
                unitInt = ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                        Integer.parseInt(StringUtils.isEmpty(
                                CacheUtil.getSysParam("SEC_INT_SCALE_BANK")) ? "12" : CacheUtil.getSysParam("SEC_INT_SCALE_BANK")),
                        RoundingMode.HALF_UP);
            }
        }
        return unitInt;
    }

    public TrdBondDuration003ProcessDto findEffectiveData(Map<String, Object> params) {
        return null;
    }

    public List<TrdBondPtlSecExerciseInfo> setExeInfos(TrdBondDuration003ProcessDto entity) {
        return null;
    }

    public TrdBondDuration003ProcessDto getTrdBondDuration003ProcessDto(Map<String, Object> params) {
        TrdBondDuration003ProcessDto entity;
        entity = new TrdBondDuration003ProcessDto();
        entity.setFinprodId((String) params.get("finprodId"));
        entity.setPs("T39");
        entity.setBizCode((String) params.get("busiCode"));
        //从接口中获取通道和payDate
        Map<String,Object> map = getPkByBizCode(entity.getBizCode());
        if(map==null) {
            entity.setChlId((String) params.get("secManageAcctId"));
            entity.setPayDate(DateUtil.parse((String) params.get("payDate")));	
        }
        else {
        	 entity.setChlId((String)map.get("CHILD"));
        	 entity.setPayDate(DateUtil.parse((String) map.get("PAYDATE")));	
        }
        ProcessMark processMark = WorkflowContext.getProcessMarkService().getParameterByAnnotation(entity);
        ProcessMark temp = WorkflowContext.getProcessMarkService().getBussinessEntityByPk(processMark);
        if (null != temp && StringUtil.isNotEmpty(temp.getBusinessData())) {
            entity = JsonUtils.json2Object(temp.getBusinessData(), TrdBondDuration003ProcessDto.class);
        }
        // modify by etliu 20220228 start
        // 广农商二阶段 GNS-896:【广农商场内清算】场内债券存续期-行权赎回，批处理后未生成交易
        else {
            // 留痕表没数据的时候，从业务表查
            Map<String, Object> paramMap = new HashMap<>();
            String totalTradeId = (String) params.get("busiCode");
            paramMap.put("totalTradeId", totalTradeId);
            entity = simpleRestTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondDuration/findEffectiveDataXq",
                    paramMap, TrdBondDuration003ProcessDto.class);
            if (entity != null && !StringUtil.isEmptyList(entity.getTrdBondPtlSecExerciseInfos())) {
                List<BussTrdTransferOrderDto> orderList = new ArrayList<>();
                for (TrdBondPtlSecExerciseInfo tempDto : entity.getTrdBondPtlSecExerciseInfos()) {
                    // 查询划款指令
                    orderList.addAll(simpleRestTemplate.postForList(
                            ServiceApplication.SETTLE_SERVICE_NAME + "/BussSettleOrderComm/selectOrdersByBizCode",
                            tempDto.getTotalTradeId(), BussTrdTransferOrderDto.class));
                }
                entity.setBussTrdTransferOrder(orderList);
            }
        }
        // modify by etliu 20220228 end
        return entity;
    }

    public List<String> findProcessDtos(Map<String, Object> param) {
        param.put("userName", LoginUserUtil.getUserName());
        param.put("ps", "T39");
        List<String> dtos = restTemplate.postForObject(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondDuration/findProcessDtos", param,
                ArrayList.class);
        return dtos;
    }
    
    private Map<String,Object> getPkByBizCode(String bizCode){
    	return restTemplate.getForObject( ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondDuration/getPkByBizCode?bizCode="+bizCode, Map.class);
    }

    public List<String> findWorkflowProcessDtos(Map<String, Object> param) {
        return restTemplate.postForObject(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondDuration/findWorkflowProcessDtos", param,
                ArrayList.class);
    }

    /**
     * 资产代码：下拉框
     *
     * @param parameters 画面参数
     * @return 资产代码：下拉框
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodId(Map<String, Object> parameters) {
        return restTemplate.postForPage(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondDuration/getFinprodId", parameters,
                SelectOptionDto.class);
    }
}
