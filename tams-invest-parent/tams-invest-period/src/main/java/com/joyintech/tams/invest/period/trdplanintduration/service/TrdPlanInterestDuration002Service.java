package com.joyintech.tams.invest.period.trdplanintduration.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.BasePtlRecePositionService;
import com.joyintech.tams.invest.common.autoperiod.planinterest.AutoPeriodPlanInterestCommonService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdplanintduration.dto.TrdPlanInterestDurationProcessDto;
import com.joyintech.tams.invest.period.trdprojduration.dto.TrdProjectDurationProcessDto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.CommentFunction;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.joyintech.tams.invest.period.common.service.PeriodCommonService.setWorkflowId;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.period.trdbondduration.service
 * @Description: 债券存续期管理-查询
 * @date Date : 2018年09月21日 14:32
 */
@Service
public class TrdPlanInterestDuration002Service extends BaseService<TrdPlanInterestDurationProcessDto> implements BaseProcessService<TrdPlanInterestDurationProcessDto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdPlanInterestDuration002Service.class);

    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;

    @Autowired
    private InvestInterfCommService investInterfCommService;
    
    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    /**
     * AutoPeriodPlanInterestCommonService
     */
    @Autowired
    private AutoPeriodPlanInterestCommonService autoPeriodPlanInterestCommonService;
    @Autowired
    private BasePtlRecePositionService basePtlRecePositionService;

    public TrdPlanInterestDurationProcessDto findEffectiveData(Map<String, Object> pageParam) {
        pageParam.put("userName", LoginUserUtil.getUserName());
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdPlanInterestDuration/findEffectiveData",
            pageParam, TrdPlanInterestDurationProcessDto.class);
    }

    @Override
    public void doComplete(TrdPlanInterestDurationProcessDto entity, OperationType operationType) {
        entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        if (OperationType.REVOKE == operationType) {
            execute交易共通(entity, DictCodeListExt1.交易流程状态_撤销);
            entity.setOperateType(BussinessConstant.REVOKE);
        }
        else {
            execute交易共通(entity, DictCodeListExt1.交易流程状态_有效);
        }
        entity.setIssuePartyId(CacheUtil.getSysParam("issue_party_id"));
        // 新建交易撤销数据 或 插入Entity到对应的业务DB中, 并删除流程表中的数据
        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdPlanInterestDuration/doComplete", entity,
            String.class);
    }

    @Override
    public void doInsert(TrdPlanInterestDurationProcessDto entity, OperationType type) {
        if (BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
            setWorkflowId(entity.getBaseTotalTradeInfoDto(), entity.getWorkflowId());
        }
        saveDataWhenStartWorkflow(entity, type);
    }

    private void saveDataWhenStartWorkflow(TrdPlanInterestDurationProcessDto entity, OperationType type) {
        if (type != OperationType.REVOKE && BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
            // 插入Entity到对应的业务流程DB中
            if (entity.getNeedSkipInsertWfl() == null || !entity.getNeedSkipInsertWfl()) {
                restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdPlanInterestDuration/doInsert",
                    entity, String.class);
            }
                execute交易共通(entity, DictCodeListExt1.交易流程状态_流程中);
            }
        else if (type == OperationType.REVOKE) {
            restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdPlanInterestDuration/doRevoke", entity,
                String.class);
        }
    }

    private void execute交易共通(TrdPlanInterestDurationProcessDto entity, DictCodeListExt1 交易流程状态) {
        // 调用业务组件.【金融产品交易共通.交易产品接口】
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        tradeDto.setWorkflowStatus(交易流程状态.getValue());

        // 调交易共通时，一个利息明细一条交易，但存表时，一个交易明细可能有两个交易（收息、还本）
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = genBaseTradeDto(entity, 交易流程状态);
        for (BaseTradeProductDealDto baseTradeProductDealDto : baseTradeProductDealDtos) {
            TrdProductDeal deal1 = baseTradeProductDealDto.getTrdProductDeal();
            if (DictCodeListExt1.交易流程状态_撤销 == 交易流程状态) {
                if (entity.getBusiCode().lastIndexOf('R') != entity.getBusiCode().length() - 1) {
                    deal1.setTradeId(entity.getBusiCode() + 'R');
                }
                else{
                    deal1.setTradeId(entity.getBusiCode());
                }
            }
            else{
                deal1.setTradeId(entity.getBusiCode());
            }
            if (DictCodeList.交易方向_还本到账.getValue().equals(deal1.getPs())) {
                deal1.setPs(DictCodeList.交易方向_还本.getValue());
            }
            if (DictCodeList.交易方向_收息到账.getValue().equals(deal1.getPs())) {
                deal1.setPs(DictCodeList.交易方向_收息.getValue());
            }
            if(StringUtil.isNotEmpty( deal1.getFinprodId())){
            	Map<String, Object> pageParam = new HashMap<>();
            	pageParam.put("finprodId",  deal1.getFinprodId());
            	List<SelectOptionDto> selectOptionDto = restTemplate.postForList(ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/getChannelIdAndCollectTypeForAsset",
            			pageParam , SelectOptionDto.class);
            	if(!StringUtil.isEmptyList(selectOptionDto)){
            		//设置通道id  撤销通道持仓
            		deal1.setChlId(selectOptionDto.get(0).getId());
            	}
            }
            deal1.setShareAmt(deal1.getPrinAmt());
            deal1.setCreateUser(tradeDto.getCreateUser());
            deal1.setCreateTime(tradeDto.getCreateTime());
            deal1.setCreateDept(tradeDto.getCreateDept());
            deal1.setUpdateTime(tradeDto.getUpdateTime());
            deal1.setUpdateUser(tradeDto.getUpdateUser());
            getInterfInputDto(entity, baseTradeProductDealDto, 交易流程状态);
        }
        if (DictCodeListExt1.交易流程状态_撤销 == 交易流程状态) {
            baseTradeProductDealDtos.forEach(baseTradeProductDealDto -> {
                baseTradeProductDealDto.getStlPayOrdersList().forEach(stlPayOrdersDto -> {
                    stlPayOrdersDto.setOrderId(stlPayOrdersDto.getOrderId() + 'R');
                });
            });
        }
        tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        baseTradeProductDealCommServiceImpl.execute(tradeDto);

        //处理还本不带息的功能
        List<BaseTradeProductDealDto> baseReceTradeProductDealDtos = genBaseReceTradeDto(entity, 交易流程状态);
        if(baseReceTradeProductDealDtos!=null && baseReceTradeProductDealDtos.size()>0){//获取还本不带息的交易
            for(BaseTradeProductDealDto baseReceTradeProductDealDto : baseReceTradeProductDealDtos){
                TrdProductDeal deal1 = baseReceTradeProductDealDto.getTrdProductDeal();
                if (DictCodeListExt1.交易流程状态_撤销 == 交易流程状态) {
                    if (deal1.getTradeId().lastIndexOf('R') != deal1.getTradeId().length() - 1) {
                        deal1.setTradeId(deal1.getTradeId() + 'R');
                    }
                }
                deal1.setChlId(entity.getChlId());
                deal1.setCreateUser(tradeDto.getCreateUser());
                deal1.setCreateTime(tradeDto.getCreateTime());
                deal1.setCreateDept(tradeDto.getCreateDept());
                deal1.setUpdateTime(tradeDto.getUpdateTime());
                deal1.setUpdateUser(tradeDto.getUpdateUser());
                getInterfInputDto(entity, baseReceTradeProductDealDto, 交易流程状态);
            }
            FinProductTradeDto finProductTrades = new FinProductTradeDto();
            finProductTrades.setWorkflowStatus(交易流程状态.getValue());
            finProductTrades.setNeedSum(true);
            finProductTrades.setBaseTradeProductDealDtos(baseReceTradeProductDealDtos);
            basePtlRecePositionService.execute(finProductTrades);
        }

        execute交易共通子组合(entity,交易流程状态);
        execute还本不带息交易子组合(entity,交易流程状态);//还本不带息
    }

    private List<BaseTradeProductDealDto> genBaseTradeDto(TrdPlanInterestDurationProcessDto entity, DictCodeListExt1 交易流程状态) {

        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        // 调交易共通时，一个利息明细一条交易，但存表时，一个交易明细可能有两个交易（收息、还本）
        if (DictCodeListExt1.交易流程状态_撤销 != 交易流程状态) {
            entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
                TrdProductDeal deal = baseTradeInfoDto.getTrdProductDeal();
                if (deal == null) { // 付息还本不同频的还本
                    deal = baseTradeInfoDto.getBaseTrade().getTrdProductDeal();
                    TrdProductDeal dealExe = new TrdProductDeal();
                    BeanUtils.copyProperties(deal, dealExe);
                    setDeal(baseTradeProductDealDtos, baseTradeInfoDto,baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd(), dealExe,
                            entity);
                } else { // 纯付息或中间还本到期还本带付息
                    if (baseTradeInfoDto.getBaseTrade() == null
                            || baseTradeInfoDto.getBaseTrade().getTrdProductDeal() == null) { // 纯付息
                        TrdProductDeal dealExe = new TrdProductDeal();
                        BeanUtils.copyProperties(deal, dealExe);
                        setDeal(baseTradeProductDealDtos, baseTradeInfoDto, baseTradeInfoDto.getTrdProductDealAdd(),
                                dealExe, entity);
                    }
                    if (baseTradeInfoDto.getBaseTrade() != null
                            && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) { // 还本到期带付息的还本交易
                        TrdProductDeal deal1 = baseTradeInfoDto.getBaseTrade().getTrdProductDeal();
                        TrdProductDeal dealExe1 = new TrdProductDeal();
                        BeanUtils.copyProperties(deal1, dealExe1);
                        setDeal(baseTradeProductDealDtos, baseTradeInfoDto,
                                baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd(), dealExe1, entity);
                    }
                }
            });
        } else {
            List<BaseTradeProductDealDto> baseTradeProductDealDtos1 = periodCommonService.findEffectiveDeal(entity.getBusiCode(),交易流程状态);
            for (BaseTradeProductDealDto baseTradeProductDealDto : baseTradeProductDealDtos1) {
                TrdProductDeal deal1 = baseTradeProductDealDto.getTrdProductDeal();
                deal1.setTradeId(entity.getBusiCode() + 'R');
                deal1.setShareAmt(ArithmeticUtil.divide(entity.getQty(), new BigDecimal(100), 4));
            }
        	// add by renping 撤销的时候没有反写持仓bug对应 20191028 start
            return baseTradeProductDealDtos1;
        	// add by renping 撤销的时候没有反写持仓bug对应 20191028 end
        }
        return baseTradeProductDealDtos;
    }

    private void setDeal(List<BaseTradeProductDealDto> baseTradeProductDealDtos, BaseTradeInfoDto baseTradeInfoDto, TrdProductDealAdd add,
                         TrdProductDeal dealExe, TrdPlanInterestDurationProcessDto entity) {
        dealExe.setPs(dealExe.getStatus());
        dealExe.setChlId(entity.getChlId());
        dealExe.setFpriceAmt(dealExe.getTradeAmt());
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        dealExe.setTradeId(entity.getBusiCode());
        baseTradeProductDealDto.setTrdProductDeal(dealExe);
        baseTradeProductDealDto.setTrdProductDealAdd(add);
        StlPayOrdersDto orders = new StlPayOrdersDto();
        if(baseTradeInfoDto.getStlPayOrders() != null){
            BeanUtils.copyProperties(baseTradeInfoDto.getStlPayOrders(), orders);
        }
        baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(new StlPayOrdersDto[]{orders}));
        baseTradeProductDealDtos.add(baseTradeProductDealDto);
    }

    private void execute交易共通子组合(TrdPlanInterestDurationProcessDto entity, DictCodeListExt1 交易流程状态) {
        // 调用业务组件.【金融产品交易共通.交易产品接口】
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        tradeDto.setWorkflowStatus(交易流程状态.getValue());
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        // 调交易共通时，一个利息明细一条交易，但存表时，一个交易明细可能有两个交易（收息、还本）
        if (DictCodeListExt1.交易流程状态_撤销 != 交易流程状态) {
            List<BaseTradeInfoDto> baseTradeInfoDtoList = entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList();
            baseTradeInfoDtoList.forEach(baseTradeInfoDto -> {
                //                    子组合调用
                baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(x->{
                    List<BaseTradeProductDealDto> temp = new ArrayList<>();
                    List<TrdProductDeal> deals = periodCommonService.getCommonExcuteDtoByTrade(x, temp);
                    temp.forEach(dealDto -> {
                        if (DictCodeList.交易方向_还本.getValue().equals(dealDto.getTrdProductDeal().getPs())
                                || DictCodeList.交易方向_到期.getValue().equals(dealDto.getTrdProductDeal().getPs())) {
                            dealDto.setStlPayOrdersList(null);
                        }
                        dealDto.getTrdProductDeal().setTradeId(entity.getBusiCode());
                    });
                    baseTradeProductDealDtos.addAll(temp);
                    temp.clear();
                });
            });
            // 调用投管接口 add by qcx 20190206 start
            baseTradeProductDealDtos.forEach(baseTradeProductDealDto -> {
                getInterfInputDto(entity, baseTradeProductDealDto, DictCodeListExt1.画面数据状态_新建);
            });
            // 调用投管接口 add by qcx 20190206 end
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        }
        else {
            Map<String,Object>map = new HashMap<>();
            map.put("statusValid", 交易流程状态.getValue());
            map.put("statusValid03", DictCodeListExt1.交易流程状态_撤销.getValue());
            map.put("bizCode", entity.getBusiCode());
            baseTradeProductDealDtos.addAll(periodCommonService.getChiledTradeProductDealInfo(map));
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
            // 调用投管接口 add by qcx 20190206 start
            baseTradeProductDealDtos.forEach(baseTradeProductDealDto -> {
                getInterfInputDto(entity, baseTradeProductDealDto, DictCodeListExt1.交易流程状态_撤销);
                if(entity.getBusiCode().endsWith("R")){
                    baseTradeProductDealDto.getTrdProductDeal().setTradeId(entity.getBusiCode());
                }
                else{
                    baseTradeProductDealDto.getTrdProductDeal().setTradeId(entity.getBusiCode()+"R");
                }
            });
            // 调用投管接口 add by qcx 20190206 end
        }
        baseTradeProductDealCommServiceImpl.insertPosition(tradeDto);
    }

    private void getInterfInputDto(TrdPlanInterestDurationProcessDto entity, BaseTradeProductDealDto baseTradeProductDealDto,
                                   DictCodeListExt1 交易流程状态) {
        if (baseTradeProductDealDto.getTrdProductDealAdd() == null
                || StringUtil.isEmpty(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())
                || AllTradeCommonUtil.isNotActPortfolio(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())) {
            return;
        }
        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        investInterfCommDto.setPageId("TrdPlanInterestDuration002");
        investInterfCommDto.setTradeStatus(交易流程状态.getValue());
        investInterfCommDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
        investInterfCommDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setPaBussType(entity.getPaBussType());
        investInterfCommDto.setPaTrdProductDeal(paTrdProductDeal);
        if (baseTradeProductDealDto.getStlPayOrdersList() != null
                && !baseTradeProductDealDto.getStlPayOrdersList().isEmpty()) {
            investInterfCommDto.setStlPayOrders(baseTradeProductDealDto.getStlPayOrdersList().get(0));
        }
        investInterfCommDto.setOtherTypeForConvert(DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue());
        investInterfCommService.doInvestInterf(investInterfCommDto);
    }

    private List<BaseTradeProductDealDto> findEffectiveDeal(String busiCode, DictCodeListExt1 交易流程状态) {
        Map<String, Object> params = new HashMap<>();
        params.put("bizCode", busiCode);
        params.put("statusValid", 交易流程状态.getValue());
        params.put("statusValid03", DictCodeListExt1.交易流程状态_撤销.getValue());
        return restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdProjectDuration/findEffectiveDeal",
                params, BaseTradeProductDealDto.class);
    }

    /**
     * 一个交易明细一个划款指令
     *
     * @param entity
     * @param total
     * @param entity TrdPlanInterestDurationProcessDto
     * @return
     */
    private StlPayOrders gen划款指令流程(List<BussTrdTransferOrderDto> stlPayOrdersList, TrdFinProductDealTotal total,
                                   TrdPlanInterestDurationProcessDto entity) {

        StlPayOrders stlPayOrders = null;
        for (BussTrdTransferOrderDto dto : stlPayOrdersList) {
            if (dto.getPortfolioId().equals(total.getPortfolioId())) {
                stlPayOrders = dto;
                stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
                stlPayOrders.setTradeId(total.getTotleTradeId());
                stlPayOrders.setSettleAmt(total.getFpriceAmt());
                stlPayOrders.setWorkflowId(entity.getWorkflowId());
            }
        }
        return stlPayOrders;
    }

    @Override
    public void doUpdate(TrdPlanInterestDurationProcessDto entity) {
        //删除持仓流水 start
        if(entity.getBaseTotalTradeInfoDto()!=null&&entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList()!=null) {
        	PtlSecPositionFlowDto pFlowDto = new PtlSecPositionFlowDto();
        	pFlowDto.setEntrust(true);
        	for(BaseTradeInfoDto baseTradeInfoDto:entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList()) {
        		if(baseTradeInfoDto.getTrdProductDeal()!=null) {
        			pFlowDto.setTradeId(baseTradeInfoDto.getTrdProductDeal().getTradeId());
                	basePtlSecPositionInfoService.deleteSecPosition(pFlowDto);
        		}
        	}
        }
        //删除持仓流水 end
        entity.setBaseTotalTradeInfoDto(genFinProductTradeDto(entity));
        ProcessMark newEntity = null;
        try {
            newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdPlanInterestDurationProcessDto.class, true);
            // 工作流ID
            newEntity.setWorkflowId(entity.getWorkflowId());
            newEntity.setStatus(entity.getStatus());
            newEntity.setBusinessData(JsonUtils.object2Json(entity));
            // 更新业务表数据
            // 操作留痕表
            WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        saveDataWhenStartWorkflow(entity, OperationType.NEW);
    }

    @Autowired
    private PeriodCommonService periodCommonService;

    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommServiceImpl.delete(workflowId);
    }

    /**
     * 以组合维度生成dto，就是以利息明细维度组装新的dto，作为属性保存到流程entity中<br/>
     * 汇总 <br/>
     * 汇总扩展 <br/>
     * 汇总费用明细 <br/>
     * List<Dto> | 交易 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 附属 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 扩展 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 费用明细 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 组合交易 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| List<划款指令>
     */
    public BaseTotalTradeInfoDto genFinProductTradeDto(TrdPlanInterestDurationProcessDto entity) {

        BaseTotalTradeInfoDto result = autoPeriodPlanInterestCommonService.genFinProductTradeDto(entity);
        return result;
    }

    public List<FinAssetIntInfo> findAssetIntInfo(Map<String, Object> pageParam) {
        pageParam.put("userName", LoginUserUtil.getUserName());
        List<FinAssetIntInfo> finAssetIntInfos = restTemplate.postForList(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdPlanInterestDuration/findAssetIntInfo", pageParam,
            FinAssetIntInfo.class);
        String statusValid = (String)pageParam.get("statusValid");
        for (int i = 0; i < finAssetIntInfos.size(); i++ ) {
         // 一体化对应etliu start
//            finAssetIntInfos.get(i).setCalcRuleString(
//                SysDataUtil.getNameFromDataDict(DictCode.支付频率_付息频率, finAssetIntInfos.get(i).getCalcRule()));
            finAssetIntInfos.get(i).setCalcRuleString(
                SysDataUtil.getNameFromDataDict(DictCode.频率, finAssetIntInfos.get(i).getCalcRule()));
         // 一体化对应etliu end
        }
        if (DictCodeListExt1.画面数据状态_待处理.getValue().equals(statusValid)) {
            // 按占比计算还本金额、理论本息、实收本息
            BigDecimal sumAmount = BigDecimal.ZERO;
            BigDecimal sumTheoryAmt=BigDecimal.ZERO;//所有组合的理论本息和
            BigDecimal payUnpaidintAmt=BigDecimal.ZERO;//累计还本不带息总额

            for (FinAssetIntInfo finAssetIntInfo : finAssetIntInfos) {
                sumAmount = ArithmeticUtil.doAdd(sumAmount, finAssetIntInfo.getAmount());//还本总金额
                sumTheoryAmt=ArithmeticUtil.doAdd(sumTheoryAmt, finAssetIntInfo.getTheoryAmt());//理论本息和
                payUnpaidintAmt=ArithmeticUtil.doAdd(payUnpaidintAmt, finAssetIntInfo.getRepaymentUnpayInterest());
            }

            BigDecimal 还本金额 = (BigDecimal)pageParam.get("returnAmount");
            BigDecimal 理论本息 = (BigDecimal)pageParam.get("theoryAmt");
            BigDecimal 实收本息 = (BigDecimal)pageParam.get("fpriceAmt");
            BigDecimal 总剩余本金 = (BigDecimal)pageParam.get("qty");
            BigDecimal 税费金额 = (BigDecimal)pageParam.get("taxAmount");
            // add by renping 2019-10-15 添加通道费 start
            BigDecimal 通道费金额 = (BigDecimal)pageParam.get("channelFee");
            BigDecimal 到账金额 = (BigDecimal)pageParam.get("tradeAmt");
            BigDecimal 区间未付利息=(BigDecimal)pageParam.get("repaymentUnpayInterest");
            //BigDecimal 区间未付利息=(BigDecimal)pageParam.get("repaymentUnpayInterest");
            // add by renping 2019-10-15 添加通道费 end

            BigDecimal sum还本金额 = BigDecimal.ZERO;
            BigDecimal sum理论本息 = BigDecimal.ZERO;
            BigDecimal sum实收本息 = BigDecimal.ZERO;
            BigDecimal sum总剩余本金 = BigDecimal.ZERO;
            BigDecimal sum税费金额 = BigDecimal.ZERO;
            // add by renping 2019-10-15 添加通道费 start
            BigDecimal sum通道费金额 = BigDecimal.ZERO;
            BigDecimal sum到账金额 = BigDecimal.ZERO;
            // add by renping 2019-10-15 添加通道费 end
            BigDecimal sum区间未付利息 = BigDecimal.ZERO;
            for (int i = 0; i < finAssetIntInfos.size(); i++ ) {
                if (i == finAssetIntInfos.size() - 1) {
                    finAssetIntInfos.get(i).setReturnAmount(ArithmeticUtil.subtract(还本金额, sum还本金额));
                    finAssetIntInfos.get(i).setTheoryAmt(ArithmeticUtil.subtract(理论本息, sum理论本息));
                    finAssetIntInfos.get(i).setFpriceAmt(ArithmeticUtil.subtract(实收本息, sum实收本息));
                    finAssetIntInfos.get(i).setQty(ArithmeticUtil.subtract(总剩余本金, sum总剩余本金));
                    finAssetIntInfos.get(i).setTaxAmount(ArithmeticUtil.subtract(税费金额, sum税费金额));
                    // add by renping 2019-10-15 添加通道费 start
                    finAssetIntInfos.get(i).setChannelFee(ArithmeticUtil.subtract(通道费金额, sum通道费金额));
                    finAssetIntInfos.get(i).setTradeAmt(ArithmeticUtil.subtract(到账金额, sum到账金额));
                    // add by renping 2019-10-15 添加通道费 end

                }
                else {
                    BigDecimal prop = ArithmeticUtil.divide(finAssetIntInfos.get(i).getAmount(), sumAmount, 12,
                        RoundingMode.HALF_UP);
                    BigDecimal this还本金额 = ArithmeticUtil.round(ArithmeticUtil.multiply(还本金额, prop), 2, RoundingMode.HALF_UP);
                    BigDecimal this理论本息 = ArithmeticUtil.round(ArithmeticUtil.multiply(理论本息, prop), 2, RoundingMode.HALF_UP);
                    BigDecimal this实收本息 = ArithmeticUtil.round(ArithmeticUtil.multiply(实收本息, prop), 2, RoundingMode.HALF_UP);
                    BigDecimal this总剩余本金 = ArithmeticUtil.round(ArithmeticUtil.multiply(总剩余本金, prop), 2, RoundingMode.HALF_UP);
                    BigDecimal this税费金额 = ArithmeticUtil.round(ArithmeticUtil.multiply(税费金额, prop), 2, RoundingMode.HALF_UP);
                    // add by renping 2019-10-15 添加通道费 start
                    BigDecimal this通道费金额 = ArithmeticUtil.round(ArithmeticUtil.multiply(通道费金额, prop), 2, RoundingMode.HALF_UP);
                    BigDecimal this到账金额 = ArithmeticUtil.round(ArithmeticUtil.multiply(到账金额, prop), 2, RoundingMode.HALF_UP);
                    // add by renping 2019-10-15 添加通道费 end
                    sum还本金额 = ArithmeticUtil.doAdd(sum还本金额, this还本金额);
                    sum理论本息 = ArithmeticUtil.doAdd(sum理论本息, this理论本息);
                    sum实收本息 = ArithmeticUtil.doAdd(sum实收本息, this实收本息);
                    sum总剩余本金 = ArithmeticUtil.doAdd(sum总剩余本金, this总剩余本金);
                    sum税费金额 = ArithmeticUtil.doAdd(sum税费金额, this税费金额);
                    // add by renping 2019-10-15 添加通道费 start
                    sum通道费金额 = ArithmeticUtil.doAdd(sum通道费金额, this通道费金额);
                    sum到账金额 = ArithmeticUtil.doAdd(sum到账金额, this到账金额);
                    // add by renping 2019-10-15 添加通道费 end
                    finAssetIntInfos.get(i).setReturnAmount(this还本金额);
                    finAssetIntInfos.get(i).setTheoryAmt(this理论本息);
                    finAssetIntInfos.get(i).setFpriceAmt(this实收本息);
                    finAssetIntInfos.get(i).setQty(this总剩余本金);
                    finAssetIntInfos.get(i).setTaxAmount(this税费金额);
                    // add by renping 2019-10-15 添加通道费 start
                    finAssetIntInfos.get(i).setChannelFee(this通道费金额);
                    finAssetIntInfos.get(i).setTradeAmt(this到账金额);
//                    finAssetIntInfos.get(i).setIntAmt(this实收本息);
                    // add by renping 2019-10-15 添加通道费 end
                }
            }
        }
        return finAssetIntInfos;
    }

    public static Map<String, Object> genParams(TrdPlanInterestDurationProcessDto entity) {
        return new HashMap<String, Object>() {
            {
                put("tradeAmt", entity.getTradeAmt());
                put("channelFee", entity.getChannelFee());
                put("vDate", entity.getvDate());
                put("mDate", entity.getmDate());
                put("yield", entity.getYield());
                put("interestPayDay", entity.getInterestPayDay());
                put("finprodId", entity.getFinprodId());
                put("busiCode", entity.getBusiCode());
                put("returnAmount", entity.getReturnAmount());
                put("theoryAmt", entity.getTheoryAmt());
                put("fpriceAmt", entity.getFpriceAmt());
                put("qty", entity.getQty());
                put("chlId", entity.getChlId());
                put("taxAmount", entity.getTaxAmount());
                put("statusValid", entity.getStatusValid());
            }
        };
    }

    public CommentFunction<TrdPlanInterestDurationProcessDto, String> 生成业务说明() {
        return entity -> WorkFlowUtil.concatComment(this.getMessage("TrdBondDuration001.finprodName")
                        + GlobalConstant.OPEN_PARENTHESIS + entity.getFinAssetInfo().getFinprodName()
                        + GlobalConstant.CLOSE_PARENTHESIS,
                "资产类型" + GlobalConstant.OPEN_PARENTHESIS + DictCodeListExt1.金融产品类型_计划类资产.getCodeName() + GlobalConstant.CLOSE_PARENTHESIS,
                this.getMessage(
                        "TrdBondDuration001.busiCode") + GlobalConstant.OPEN_PARENTHESIS + entity.getBusiCode() + GlobalConstant.CLOSE_PARENTHESIS);
    }

    @Override
    public void doBack(TrdPlanInterestDurationProcessDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBusiCode());
    }

    @Override
    public void doRevoke(TrdPlanInterestDurationProcessDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBusiCode());
    }

    private List<BaseTradeProductDealDto> genBaseReceTradeDto(TrdPlanInterestDurationProcessDto entity, DictCodeListExt1 交易流程状态) {

        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        // 调交易共通时，一个利息明细一条交易，但存表时，一个交易明细可能有两个交易（收息、还本）
        if (DictCodeListExt1.交易流程状态_撤销 != 交易流程状态) {
            entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
                if(baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList()!=null
                        && baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().size()>0){//还本不带息不为空
                    baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().forEach(baseReceInfoDto->{
                        TrdProductDeal trdProductDeal=baseReceInfoDto.getTrdProductDeal();
                        TrdProductDealAdd add =baseReceInfoDto.getTrdProductDealAdd();
                        List<PtlTrade>  ptlTradelist=baseReceInfoDto.getPtlTrade();
                        TrdProductDeal dealExe = new TrdProductDeal();
                        BeanUtils.copyProperties(trdProductDeal, dealExe);
                        setReceDeal(baseTradeProductDealDtos, baseReceInfoDto,add, dealExe,ptlTradelist,
                                entity.getChlId());
                    });
                }
            });
        }
        else {
            List<BaseTradeProductDealDto> baseTradeProductDealDtos1 = periodCommonService.findReceEffectiveDeal(entity.getBusiCode(),交易流程状态);
            return baseTradeProductDealDtos1;
        }
        return baseTradeProductDealDtos;
    }


    private void setReceDeal(List<BaseTradeProductDealDto> baseTradeProductDealDtos, BaseTradeInfoDto baseTradeInfoDto,
                             TrdProductDealAdd add,
                             TrdProductDeal dealExe,List<PtlTrade> ptlTradeList, String chlId) {
        dealExe.setChlId(chlId);
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        //dealExe.sett(entity.getBusiCode());
        baseTradeProductDealDto.setTrdProductDeal(dealExe);
        baseTradeProductDealDto.setTrdProductDealAdd(add);
        baseTradeProductDealDto.setPtlTrade(ptlTradeList);
        baseTradeProductDealDto.setPayDate(basePtlRecePositionService.getDurationPayDate(dealExe.getFinprodId(),dealExe.getBranch()));
        baseTradeProductDealDto.setRecType(DictCodeListExt1.应收款类型_还本未收利息.getValue());
        baseTradeProductDealDtos.add(baseTradeProductDealDto);
    }

    private void execute还本不带息交易子组合(TrdPlanInterestDurationProcessDto entity, DictCodeListExt1 交易流程状态) {
        // 调用业务组件.【金融产品交易共通.交易产品接口】
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        tradeDto.setWorkflowStatus(交易流程状态.getValue());

        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        // 调交易共通时，一个利息明细一条交易，但存表时，一个交易明细可能有两个交易（收息、还本）
        if (DictCodeListExt1.交易流程状态_撤销 != 交易流程状态) {
            List<BaseTradeInfoDto> baseTradeInfoDtoList = entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList();
            baseTradeInfoDtoList.forEach(baseTradeInfoDto -> {
                baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(x->{
                    if(x.getRepaymentUnpayInterestBaseTradeInfoDtoList()!=null && x.getRepaymentUnpayInterestBaseTradeInfoDtoList().size()>0){
                        List<BaseTradeProductDealDto> temp = new ArrayList<>();
                        x.getRepaymentUnpayInterestBaseTradeInfoDtoList().forEach(baseReceInfoDto->{
                            TrdProductDeal trdProductDeal=baseReceInfoDto.getTrdProductDeal();
                            TrdProductDealAdd add =baseReceInfoDto.getTrdProductDealAdd();
                            List<PtlTrade>  ptlTradelist=baseReceInfoDto.getPtlTrade();
                            TrdProductDeal dealExe = new TrdProductDeal();
                            BeanUtils.copyProperties(trdProductDeal, dealExe);
                            setReceDeal(baseTradeProductDealDtos, baseReceInfoDto,add, dealExe,ptlTradelist,
                                    null);
                        });

                    }
                });
            });
            // 调用投管接口 add by qcx 20190206 start
            baseTradeProductDealDtos.forEach(baseTradeProductDealDto -> {
                getInterfInputDto(entity, baseTradeProductDealDto, DictCodeListExt1.画面数据状态_新建);
            });
            // 调用投管接口 add by qcx 20190206 end
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        }
        else {
            Map<String,Object>map = new HashMap<>();
            map.put("statusValid", 交易流程状态.getValue());
            map.put("statusValid03", DictCodeListExt1.交易流程状态_撤销.getValue());
            map.put("bizCode", entity.getBusiCode());
            //baseTradeProductDealDtos.addAll(periodCommonService.getChiledTradeProductDealInfo(map));
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
            // 调用投管接口 add by qcx 20190206 start
            baseTradeProductDealDtos.forEach(baseTradeProductDealDto -> {
                getInterfInputDto(entity, baseTradeProductDealDto, DictCodeListExt1.交易流程状态_撤销);
            });
            // 调用投管接口 add by qcx 20190206 end
        }

        basePtlRecePositionService.insertPosition(tradeDto);
    }
}
