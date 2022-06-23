/*
 * 文件名：TrdFundDurationDealEntity.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：丁志鹏 修改时间：2018年11月1日 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.period.trdplancurrduration.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdplancurrduration.dto.TrdPlanCurrDuration001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 处理entity所用service 〈功能详细描述〉
 *
 * @author 丁志鹏
 * @version 1.0 2018年11月1日
 * @see TrdPlanCurrDurationDealEntity
 * @since 1.0
 */
@Service
public class TrdPlanCurrDurationDealEntity extends BaseService<TrdPlanCurrDuration001Dto> {

    /**
     * 共通交易
     */
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;

    /**
     * 划款指令共同
     */
    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * AutoPeriodCommonService
     */
    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;

    @Autowired
    private PeriodCommonService periodCommonService;

    @Autowired
    private InvestInterfCommService investInterfCommService;

    /**
     * 除权登记时获取划款指令dto
     *
     * @param entity 数据实体
     */
    public void generateOrder(TrdPlanCurrDuration001Dto entity) {
        // 划款指令不为空时不处理
        if (CollectionUtils.isNotEmpty(entity.getBussTrdTransferOrder())) {
            return;
        }
        TrdProductDeal deal = getTrdProductDeal(entity);
        BussSettleOrderParameterDto param = new BussSettleOrderParameterDto();
        param.setIsTotalTrade(DictCodeList.是否_否.getValue());
        param.setPortfolioId(entity.getPortfolioId());
        param.setDeal(deal);
        BussTrdTransferOrderDto ret = bussTrdTransferOrderService.generateOrder(param);
        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtos = new ArrayList<>();
        if (ret != null) {
            bussTrdTransferOrderDtos.add(ret);
        }
        entity.setBussTrdTransferOrder(bussTrdTransferOrderDtos);
    }

    /**
     * 处理在代办中提交及保存的操作
     *  @param entity 业务实体
     * @param type 操作类型
     * @param 不需要处理实时头寸
     */
    public void saveDataWhenStartWorkflow(TrdPlanCurrDuration001Dto entity, OperationType type, boolean 不需要处理实时头寸) {
        if (BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
            // 插入Entity到对应的业务流程DB中
            if (entity.getNeedSkipInsertWfl() == null || !entity.getNeedSkipInsertWfl()) {
                restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdPlanInterestDuration/doInsert",
                    entity, String.class);
            }
            if (type != OperationType.REVOKE) {
                execute交易共通(entity, DictCodeListExt1.交易流程状态_流程中, 不需要处理实时头寸);
            }
        }
        else if (type == OperationType.REVOKE) {
            restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/doRevoke", entity,
                String.class);
        }
    }

    public void execute交易共通(TrdPlanCurrDuration001Dto entity, DictCodeListExt1 交易流程状态, boolean 不需要处理实时头寸) {
        // 调用业务组件.【金融产品交易共通.交易产品接口】
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        tradeDto.setWorkflowStatus(交易流程状态.getValue());
        tradeDto.setNeedExecuteEtrPosition(不需要处理实时头寸);
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        // 调交易共通时，一个利息明细一条交易，但存表时，一个交易明细可能有两个交易（收息、还本）
        if (DictCodeListExt1.交易流程状态_撤销 != 交易流程状态) {
            entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
                getCommonExcuteDtoByTrade(entity, 交易流程状态, baseTradeInfoDto,baseTradeProductDealDtos);
            });
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        }
        else {
            List<BaseTradeProductDealDto> baseTradeProductDealDtos1 = periodCommonService.findEffectiveDealForFund(entity.getpTradeId(),
                交易流程状态);
            for (BaseTradeProductDealDto baseTradeProductDealDto : baseTradeProductDealDtos1) {
                TrdProductDeal deal1 = baseTradeProductDealDto.getTrdProductDeal();
                deal1.setTradeId(deal1.getTradeId() + 'R');
                if(deal1.getShareAmt() == null){// if(deal1.getPrinAmt() != null){ 不能直接把本金设置给交易份额
                    deal1.setShareAmt(deal1.getPrinAmt());
                }
                deal1.setCreateUser(tradeDto.getCreateUser());
                deal1.setCreateTime(tradeDto.getCreateTime());
                deal1.setCreateDept(tradeDto.getCreateDept());
                deal1.setUpdateTime(tradeDto.getUpdateTime());
                deal1.setUpdateUser(tradeDto.getUpdateUser());
                getInterfInputDto(entity, baseTradeProductDealDto, 交易流程状态);
            }
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos1);
        }
        baseTradeProductDealCommServiceImpl.execute(tradeDto);
        execute交易共通子组合(entity, 交易流程状态, entity.getbTradeId() == null
                ? entity.getpTradeId() : entity.getbTradeId(), false);

        if (!DictCodeList.交易方向_红利再投.getValue().equals(entity.getPs())) {// 撤销现金分红的除权登记交易时，同时撤销现金分红的持仓
            List<BaseTradeProductDealDto> total现金分红交易 = periodCommonService.findBTradeEffectiveDeal(entity.getpTradeId());
            if (total现金分红交易 != null && !total现金分红交易.isEmpty()) {// 红利再投的除权登记交易时，这里应该检索不到数据，不处理
                for (BaseTradeProductDealDto baseTradeProductDealDto : total现金分红交易) {
                    TrdProductDeal deal1 = baseTradeProductDealDto.getTrdProductDeal();
                    deal1.setTradeId(deal1.getTradeId() + 'R');
                    deal1.setCreateUser(tradeDto.getCreateUser());
                    deal1.setCreateTime(tradeDto.getCreateTime());
                    deal1.setCreateDept(tradeDto.getCreateDept());
                    deal1.setUpdateTime(tradeDto.getUpdateTime());
                    deal1.setUpdateUser(tradeDto.getUpdateUser());
                }
                tradeDto.setBaseTradeProductDealDtos(total现金分红交易);
                baseTradeProductDealCommServiceImpl.execute(tradeDto);
                execute交易共通子组合(entity, 交易流程状态,
                    entity.getbTradeId() == null ? entity.getpTradeId() : entity.getbTradeId(), true);
            }
        }
    }



    /**
     * 子组合调用共同
     * @param entity
     * @param 交易流程状态 流程状态
     * @param tradeId 交易编号
     */
    public void execute交易共通子组合(TrdPlanCurrDuration001Dto entity, DictCodeListExt1 交易流程状态, String tradeId, boolean 现金分红交易){
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
                    getCommonExcuteDtoByTrade(entity, 交易流程状态, x,baseTradeProductDealDtos);
                });
            });
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        }
        else {
            Map<String,Object>map = new HashMap<>();
            map.put("statusValid", 交易流程状态.getValue());
            map.put("statusValid03", DictCodeListExt1.交易流程状态_撤销.getValue());
            map.put("isFund", Boolean.TRUE);
            if (现金分红交易) {
                map.put("bTrade", tradeId);
            }
            else {
                map.put("bizCode", tradeId);
            }
            baseTradeProductDealDtos.addAll(periodCommonService.getChiledTradeProductDealInfo(map));
            // 调用投管接口 add by qcx 20190206 start
            baseTradeProductDealDtos.forEach(baseTradeProductDealDto -> {
                getInterfInputDto(entity, baseTradeProductDealDto, DictCodeListExt1.交易流程状态_撤销);
            });
            // 调用投管接口 add by qcx 20190206 end
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        }
        baseTradeProductDealCommServiceImpl.insertPosition(tradeDto);
    }

    private void getInterfInputDto(TrdPlanCurrDuration001Dto entity, BaseTradeProductDealDto baseTradeProductDealDto,
                                   DictCodeListExt1 交易流程状态) {
        if (baseTradeProductDealDto.getTrdProductDealAdd() == null
                || StringUtil.isEmpty(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())
                || AllTradeCommonUtil.isNotActPortfolio(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())) {
            return;
        }
        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        investInterfCommDto.setPageId("TrdPlanCurrDuration002");
        investInterfCommDto.setTradeStatus(交易流程状态.getValue());
        investInterfCommDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
        investInterfCommDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        if (StringUtil.equals(entity.getType(), "01")) {
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_货币型_红利再投.getValue());
        }
        else if (StringUtil.equals(entity.getType(), "05")) {
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_货币型_除权登记.getValue());
        }
        else if (StringUtil.equals(entity.getType(), "06")) {
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_货币型_现金分红.getValue());
        }
        investInterfCommDto.setPaTrdProductDeal(paTrdProductDeal);
        if (baseTradeProductDealDto.getStlPayOrdersList() != null
                && !baseTradeProductDealDto.getStlPayOrdersList().isEmpty()) {
            investInterfCommDto.setStlPayOrders(baseTradeProductDealDto.getStlPayOrdersList().get(0));
        }
        investInterfCommDto.setOtherTypeForConvert(DictCodeList.共通业务类型_计划类资产_货币型_存续期.getValue());
        investInterfCommService.doInvestInterf(investInterfCommDto);
    }

    /**
     * 根据交易dto组装共同dto
     * @param entity
     * @param 交易流程状态
     * @param baseTradeInfoDto 交易dto
     * @param baseTradeProductDealDtos 共同dto list
     */
    private void getCommonExcuteDtoByTrade(TrdPlanCurrDuration001Dto entity, DictCodeListExt1 交易流程状态, BaseTradeInfoDto baseTradeInfoDto, List<BaseTradeProductDealDto> baseTradeProductDealDtos) {
        TrdProductDeal deal = baseTradeInfoDto.getTrdProductDeal();
        if (deal == null) {
            deal = baseTradeInfoDto.getBaseTrade().getTrdProductDeal();
            TrdProductDeal dealExe = new TrdProductDeal();
            BeanUtils.copyProperties(deal, dealExe);
            setDeal(entity, 交易流程状态, baseTradeProductDealDtos, baseTradeInfoDto,baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd(), dealExe);
        } else {
            TrdProductDeal dealExe = new TrdProductDeal();
            BeanUtils.copyProperties(deal, dealExe);
            setDeal(entity, 交易流程状态, baseTradeProductDealDtos, baseTradeInfoDto,baseTradeInfoDto.getTrdProductDealAdd(), dealExe);

            if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
                TrdProductDeal deal1 = baseTradeInfoDto.getBaseTrade().getTrdProductDeal();
                TrdProductDeal dealExe1 = new TrdProductDeal();
                BeanUtils.copyProperties(deal1, dealExe1);
                setDeal(entity, 交易流程状态, baseTradeProductDealDtos, baseTradeInfoDto,baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd(), dealExe1);
            }
        }
    }
    private void setDeal(TrdPlanCurrDuration001Dto entity, DictCodeListExt1 交易流程状态, List<BaseTradeProductDealDto> baseTradeProductDealDtos, BaseTradeInfoDto baseTradeInfoDto,
                         TrdProductDealAdd add, TrdProductDeal dealExe) {
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDto.setTrdProductDeal(dealExe);
        baseTradeProductDealDto.setTrdProductDealAdd(add);
        StlPayOrdersDto orders = new StlPayOrdersDto();
        if (baseTradeInfoDto.getStlPayOrders() != null) {
            BeanUtils.copyProperties(baseTradeInfoDto.getStlPayOrders(), orders);
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(new StlPayOrdersDto[] {orders}));
        }
        baseTradeProductDealDtos.add(baseTradeProductDealDto);
        getInterfInputDto(entity, baseTradeProductDealDto, 交易流程状态);
    }

    /**
     * 金融产品交易 <功能详细描述>
     *
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    public FinProductTradeDto genFinProductTradeDto(TrdPlanCurrDuration001Dto entity) {
        // 5.构造调用组件的输入参数DTO
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDtos.add(baseTradeProductDealDto);
        TrdProductDeal trdProductDeal = getTrdProductDeal(entity);
        if (entity.getBussTrdTransferOrder() != null && entity.getBussTrdTransferOrder().size() > 0) {
            // 划款指令表Entity
            List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderService.save(entity.getBussTrdTransferOrder());
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
        // 添加参数分红类型
        add.setPayType(entity.getBonusMode());
        baseTradeProductDealDto.setTrdProductDealAdd(add);
        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
        finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        return finProductTradeDto;
    }

    /**
     * 为调用共通组件组装的entity
     *
     * @param entity
     * @return
     */
    public static TrdProductDeal getTrdProductDeal(TrdPlanCurrDuration001Dto entity) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        // 交易编号
        // trdProductDeal.setTradeId(entity.getTradeId());
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_计划类资产_货币型_存续期.getValue());
        // 金融产品代码
        trdProductDeal.setFinprodId(entity.getFinprodId());
        // 金融产品类型
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        // 币种
        trdProductDeal.setCcy(entity.getCcy());
        // 货币基金红利再投
        if (StringUtil.equals(entity.getType(), "01")) {
            trdProductDeal.setPs(DictCodeList.交易方向_红利再投.getValue());
            // 红利再投日
            trdProductDeal.setTradeDate(entity.getReInvesDate());
            // 交易份额 画面.分红金额
            trdProductDeal.setShareAmt(entity.getQty());
        }
        // 非货币除权登记
        else if (StringUtil.equals(entity.getType(), "02")) {
            trdProductDeal.setPs(DictCodeList.交易方向_除权登记.getValue());
            // 除权日
            trdProductDeal.setTradeDate(entity.getExRightsDate());
            // 交易本金 ->权益登记日份额
            trdProductDeal.setPrinAmt(entity.getRegistShare());
            // 单位净值 单位分红净值
            trdProductDeal.setUnitCprice(entity.getUnitCprice());
        }
        // 非货基 现金分红
        else if (StringUtil.equals(entity.getType(), "03")) {
            trdProductDeal.setPs(DictCodeList.交易方向_现金分红.getValue());
            // 交易金额 分红金额
            trdProductDeal.setTradeAmt(entity.getTradeAmt());
            // 单位净值 单位分红净值
            trdProductDeal.setUnitCprice(entity.getUnitCprice());
            trdProductDeal.setTradeDate(entity.getArriveDate());
        }
        // 非货基 红利再投
        else if (StringUtil.equals(entity.getType(), "04")) {
            trdProductDeal.setPs(DictCodeList.交易方向_红利再投.getValue());
            // 单位净值 单位分红净值
            trdProductDeal.setUnitCprice(entity.getUnitCprice());
            trdProductDeal.setTradeDate(entity.getReInvesDate());
            // 交易份额
            trdProductDeal.setShareAmt(entity.getQty());
        }
        // 日期与交易日一致
        trdProductDeal.setSettleDate(trdProductDeal.getTradeDate());
        // 利息总额 分红金额
        trdProductDeal.setIntAmt(entity.getTradeAmt());
        trdProductDeal.setSecManageAcctId("999999");
        return trdProductDeal;
    }

    public List<WflTrdProductDeal> gen交易业务流程(TrdPlanCurrDuration001Dto entity) {
        List<WflTrdProductDeal> deals = new ArrayList<>();
        WflTrdProductDeal trdProductDeal = new WflTrdProductDeal();
        // 交易编号
        // trdProductDeal.setTradeId(entity.getTradeId());
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_计划类资产_货币型_存续期.getValue());
        // 金融产品代码
        trdProductDeal.setFinprodId(entity.getFinprodId());
        trdProductDeal.setWorkflowId(entity.getWorkflowId());
        trdProductDeal.setWorkflowStatus(entity.getStatus());
        trdProductDeal.setRemark(entity.getRemark());
        // 撤销
        if (StringUtil.equals(OperationType.REVOKE.toString(), entity.getMode())) {
            trdProductDeal.setpTradeId(entity.getpTradeId());
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
        }
        else {
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        }
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        // 金融产品类型
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        // 币种
        trdProductDeal.setCcy(entity.getCcy());
        // 货币基金红利再投
        if (StringUtil.equals(entity.getType(), "01")) {
            trdProductDeal.setPs(DictCodeList.交易方向_红利再投.getValue());
            // 红利再投日
            trdProductDeal.setTradeDate(entity.getReInvesDate());
            // 交易份额 画面.分红金额
            trdProductDeal.setShareAmt(entity.getQty());
        }
        // 非货币基金分红除权
        else if (StringUtil.equals(entity.getType(), "02")) {
            trdProductDeal.setPs(DictCodeList.交易方向_除权登记.getValue());
            // 除权日
            trdProductDeal.setTradeDate(entity.getExRightsDate());
            // 交易本金 ->权益登记日份额
            trdProductDeal.setPrinAmt(entity.getRegistShare());
            // 单位净值 单位分红净值
            trdProductDeal.setUnitCprice(entity.getUnitCprice());
        }
        // 非货基 现金分红
        else if (StringUtil.equals(entity.getType(), "03")) {
            trdProductDeal.setPs(DictCodeList.交易方向_现金分红.getValue());
            // 交易金额 分红金额
            trdProductDeal.setTradeAmt(entity.getTradeAmt());
            trdProductDeal.setTradeDate(entity.getArriveDate());
            // 单位净值 单位分红净值
            trdProductDeal.setUnitCprice(entity.getUnitCprice());
            trdProductDeal.setbTradeId(entity.getbTradeId());
        }
        // 非货基 红利再投
        else if (StringUtil.equals(entity.getType(), "04")) {
            trdProductDeal.setPs(DictCodeList.交易方向_红利再投.getValue());
            // 单位净值 单位分红净值
            trdProductDeal.setUnitCprice(entity.getUnitCprice());
            trdProductDeal.setTradeDate(entity.getReInvesDate());
            // 交易份额
            trdProductDeal.setShareAmt(entity.getQty());
            trdProductDeal.setbTradeId(entity.getbTradeId());
        }

        // 日期与交易日一致
        trdProductDeal.setSettleDate(trdProductDeal.getTradeDate());
        // 利息总额 分红金额
        trdProductDeal.setIntAmt(entity.getTradeAmt());
        trdProductDeal.setSecManageAcctId("999999");
        deals.add(trdProductDeal);
        return deals;
    }

    public List<WflStlPayOrders> gen划款指令流程(TrdPlanCurrDuration001Dto entity) {
        if (entity.getBussTrdTransferOrder() == null) {
            return new ArrayList<>();
        }
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderService.save(entity.getBussTrdTransferOrder());
        List<WflStlPayOrders> stlPayOrdersDtos = new ArrayList<>();
        if (stlPayOrdersList != null) {
            stlPayOrdersList.forEach(stlPayOrders -> {
                WflStlPayOrders dto = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrders);
                // dto.setTradeId(entity.getTradeId());
                dto.setWorkflowId(entity.getWorkflowId());
                dto.setWorkflowStatus(DictCodeList.数据状态_新建中.getValue());
                dto.setCreateDept(entity.getCreateDept());
                dto.setUpdateTime(entity.getUpdateTime());
                dto.setCreateUser(entity.getCreateUser());
                dto.setCreateTime(entity.getCreateTime());
                // 撤销
                if (StringUtil.equals(OperationType.REVOKE.toString(), entity.getOperateType())) {
                    dto.setpTradeId(entity.getpTradeId());
                    dto.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                }
                stlPayOrdersDtos.add(dto);
            });
        }
        return stlPayOrdersDtos;
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
    public BaseTotalTradeInfoDto genBaseTotalTradeInfoDto(TrdPlanCurrDuration001Dto entity) {
        BaseTotalTradeInfoDto result = new BaseTotalTradeInfoDto();
//       划款指令
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderService.save(
                entity.getBussTrdTransferOrder());
        // 不为现金分红时仅生成不入库
        if (!StringUtil.equals(entity.getType(), "06")) {
            result.setNeedStlPayOrders(false);
        }
        // 金融产品交易信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
        TrdFinProductDealTotal trdFinProductDealTotal = gen汇总流程表(entity);
        result.setTrdFinProductDealTotal(trdFinProductDealTotal);
        // 金融产品交易汇总扩展Entity -> 金融产品交易汇总扩展业务流程表
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = gen汇总扩展业务流程(entity, trdFinProductDealTotal);
        result.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);

        List<BaseTradeInfoDto> baseTradeInfoDtoList = new ArrayList<>();
        result.setBaseTradeInfoDtoList(baseTradeInfoDtoList);

        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        // 根据利息明细处理汇总及交易dto
        List<FinAssetIntInfo> 利息明细 = entity.getFinAssetIntInfoList();
        利息明细.forEach(finAssetIntInfo -> {
            BaseTradeInfoDto baseTradeInfoDto = new BaseTradeInfoDto();
            baseTradeInfoDtoList.add(baseTradeInfoDto);
            TrdProductDeal deal = 生成交易(entity, finAssetIntInfo);
            baseTradeInfoDto.setTrdProductDeal(deal);
            TrdProductDealAdd dealAdd = 生成交易附属(entity, finAssetIntInfo, deal);
            baseTradeInfoDto.setTrdProductDealAdd(dealAdd);
            PaTrdProductDeal paDeal = 生成交易扩展(entity, finAssetIntInfo, deal);
            baseTradeInfoDto.setPaTrdProductDeal(paDeal);
            baseTradeInfoDto.setPtlTrade(生成组合交易(entity, finAssetIntInfo, deal));
//            生成划款指令
            StlPayOrders stl = periodCommonService.gen划款指令流程(stlPayOrdersList, baseTradeInfoDto.getTrdProductDealAdd(), entity.getWorkflowId());
            baseTradeInfoDto.setStlPayOrders(stl);
//            生成子组合利息明细共同方法
            Map<String,Object> subMap = new HashMap<>();
            subMap.put("portfolioId",finAssetIntInfo.getPortfolioId());
//            持仓时间
            subMap.put("dealDate", DateUtil.format(entity.getRegistDate()));
//            资产代码
            subMap.put("finprodMarketId",entity.getFinprodMarketId());
            //            是否提前
            subMap.put("isAdvance", "N");
            Map<String, BigDecimal> defaultMap = new HashMap<>();
            //            自定义不累加
            defaultMap.put("unitCprice",finAssetIntInfo.getUnitCprice());
            subMap.put("defaultMap", JsonUtils.object2Json(defaultMap));
////            汇总交易编号
//            subMap.put("totleTradeId",entity.getTotleTradeId());
            Map<String, BigDecimal> preMap = new HashMap<>();
            preMap.put("qty",finAssetIntInfo.getQty());
            preMap.put("registShare",finAssetIntInfo.getRegistShare());
            preMap.put("tradeAmt",finAssetIntInfo.getTradeAmt());
//            划款指令结算金额
            if(stl != null){
                preMap.put("settleAmt",stl.getSettleAmt());
            }
            subMap.put("preMap", JsonUtils.object2Json(preMap));
            subMap.put(GlobalConstant.WORKFLOW_ID,entity.getWorkflowId());
            List<FinAssetIntInfo> 子组合利息明细 = autoPeriodCommonService.distributeDataByPercent(subMap);
            List<BaseTradeInfoDto> subBaseTradeInfoDtoList = new ArrayList<>();
//            子组合交易没有处理前置交易编号如果需要再说
//            TODO
            子组合利息明细.forEach(subFinAssetIntInfo -> {
                        BaseTradeInfoDto subBaseTradeInfoDto = new BaseTradeInfoDto();
                        subBaseTradeInfoDtoList.add(subBaseTradeInfoDto);
//                        设置组合编号
                        subFinAssetIntInfo.setPortfolioId(subFinAssetIntInfo.getSubPortfolioId());
                        TrdProductDeal subDeal = 生成交易(entity, subFinAssetIntInfo);
                        subBaseTradeInfoDto.setTrdProductDeal(subDeal);
                        TrdProductDealAdd subDealAdd = 生成交易附属(entity, subFinAssetIntInfo, subDeal);
                        subBaseTradeInfoDto.setTrdProductDealAdd(subDealAdd);
                        PaTrdProductDeal subPaDeal = 生成交易扩展(entity, subFinAssetIntInfo, subDeal);
                        subBaseTradeInfoDto.setPaTrdProductDeal(subPaDeal);
                        subBaseTradeInfoDto.setPtlTrade(生成组合交易(entity, subFinAssetIntInfo, subDeal));
                        if(stl != null){
                            //            生成子组合划款指令
                            StlPayOrders subStl = new StlPayOrders();
                            BeanUtil.copyPropertiesIgnoreNull(subStl,stl);
                            subStl.setPortfolioId(subFinAssetIntInfo.getSubPortfolioId());
                            subStl.setSettleAmt(subFinAssetIntInfo.getSettleAmt());
                            subBaseTradeInfoDto.setStlPayOrders(subStl);
                        }
            });
//            设置子组合交易
            baseTradeInfoDto.setBaseTradeInfoDtoList(subBaseTradeInfoDtoList);
        });
        return result;
    }
    /**
     * 生成组合交易 <功能详细描述>
     *
     * @param entity 业务实体
     * @param finAssetIntInfo 利息明细
     * @param deal 交易
     * @return 生成结果
     * @see
     * @since 1.0
     */
    private List<PtlTrade>  生成组合交易(TrdPlanCurrDuration001Dto entity, FinAssetIntInfo finAssetIntInfo, TrdProductDeal deal) {
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(finAssetIntInfo.getPortfolioId());
        ptlTrade.setBusiNo(deal.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(entity.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_计划类资产_货币型_存续期.getValue());
        ptlTrade.setSecManageAcctId("999999");
        ptlTrade.setSettleDate(entity.getTradeDate());
        ptlTrades.add(ptlTrade);
        return ptlTrades;
    }

    /**
     * 生成交易扩展 <功能详细描述>
     *
     * @param entity 业务实体
     * @param finAssetIntInfo 利息明细
     * @param deal 交易dto
     * @return 生成结果
     * @see
     * @since 1.0
     */
    private PaTrdProductDeal 生成交易扩展(TrdPlanCurrDuration001Dto entity, FinAssetIntInfo finAssetIntInfo, TrdProductDeal deal) {
        PaTrdProductDeal padeal = new PaTrdProductDeal();
        padeal.setTradeId(deal.getTradeId());
        // 货币基金红利再投
        if (StringUtil.equals(entity.getType(), "01")) {
            padeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_货币型_红利再投.getValue());
        }
        // 非货币基金分红除权
        else if (StringUtil.equals(entity.getType(), "05")) {
            padeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_货币型_除权登记.getValue());
        }
        // TODO 货币分红除权
        else if (StringUtil.equals(entity.getType(), "06")) {
            padeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_货币型_现金分红.getValue());
        }
        padeal.setStatus(entity.getStatus());
        padeal.setWorkflowId(entity.getWorkflowId());
        return padeal;
    }

    /**
     * 生成交易附属 <功能详细描述>
     *
     * @param entity 业务entity
     * @param finAssetIntInfo 利息明细
     * @param deal 生成的交易
     * @return
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd 生成交易附属(TrdPlanCurrDuration001Dto entity, FinAssetIntInfo finAssetIntInfo, TrdProductDeal deal) {
        TrdProductDealAdd dealAdd = new TrdProductDealAdd();
        dealAdd.setTradeId(deal.getTradeId());
        dealAdd.setPortfolioId(finAssetIntInfo.getPortfolioId());
        // 理财产品代码 金融产品代码
        // dealAdd.pr(entity.getFinprodId());
        dealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        dealAdd.setPayType(entity.getBonusMode());
        dealAdd.setStatus(entity.getStatus());
        dealAdd.setWorkflowId(entity.getWorkflowId());
        if (StringUtil.equals(entity.getType(), "02") || StringUtil.equals(entity.getType(), "05")) {
            dealAdd.setBonusCfmDate(entity.getArriveDate() != null ? entity.getArriveDate() : entity.getReInvesDate());
        }
        dealAdd.setRegDate(entity.getRegistDate());
        //      增加该字段保存权益登记日份额
        dealAdd.setRegDateAmt(finAssetIntInfo.getRegistShare());
        return dealAdd;
    }

    /**
     * 生成交易 <功能详细描述>
     *
     * @param entity 业务entity
     * @param finAssetIntInfo 利率信息
     * @return 交易dto
     * @see
     * @since 1.0
     */
    private TrdProductDeal 生成交易(TrdPlanCurrDuration001Dto entity, FinAssetIntInfo finAssetIntInfo) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTotleTradeId(entity.getTotleTradeId());
        // 交易编号
        trdProductDeal.setTradeId(
            autoPeriodCommonService.buildBussinessCode(new BussinessCodeParameter(DictCodeList.共通业务类型_资产存续期交易)));
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_计划类资产_货币型_存续期.getValue());
        // 金融产品代码
        trdProductDeal.setFinprodId(entity.getFinprodId());
        trdProductDeal.setWorkflowId(entity.getWorkflowId());
        trdProductDeal.setRemark(entity.getRemark());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        // 金融产品类型
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        // 币种
        trdProductDeal.setCcy(entity.getCcy());
        // 货币基金红利再投
        if (StringUtil.equals(entity.getType(), "01")) {
            trdProductDeal.setPs(DictCodeList.交易方向_红利再投.getValue());
            // 红利再投日
            trdProductDeal.setTradeDate(entity.getReInvesDate());
            // 交易份额 画面.分红金额
            trdProductDeal.setShareAmt(finAssetIntInfo.getQty());
            trdProductDeal.setPrinAmt(finAssetIntInfo.getQty());
            trdProductDeal.setFpriceAmt(finAssetIntInfo.getQty());
        }
        // 除权登记
        else if (StringUtil.equals(entity.getType(), "02") || StringUtil.equals(entity.getType(), "05")) {
            trdProductDeal.setPs(DictCodeList.交易方向_除权登记.getValue());
            // 除权日
            trdProductDeal.setTradeDate(entity.getExRightsDate());
            // 交易本金 ->权益登记日份额
            trdProductDeal.setPrinAmt(finAssetIntInfo.getRegistShare());
            // 单位净值 单位分红净值
            trdProductDeal.setUnitCprice(finAssetIntInfo.getUnitCprice());
        }
        // 现金分红
        else if (StringUtil.equals(entity.getType(), "03") || StringUtil.equals(entity.getType(), "06")) {
            trdProductDeal.setPs(DictCodeList.交易方向_现金分红.getValue());
            // 交易金额 分红金额
            trdProductDeal.setTradeAmt(finAssetIntInfo.getTradeAmt());
            // 交易本金 ->权益登记日份额
            trdProductDeal.setPrinAmt(finAssetIntInfo.getRegistShare());
            trdProductDeal.setTradeDate(entity.getArriveDate());
            // 单位净值 单位分红净值
            trdProductDeal.setUnitCprice(finAssetIntInfo.getUnitCprice());
            trdProductDeal.setbTradeId(finAssetIntInfo.getTradeId());
            trdProductDeal.setShareAmt(finAssetIntInfo.getQty());
        }
        // 非货基 红利再投
        else if (StringUtil.equals(entity.getType(), "04")) {
            trdProductDeal.setPs(DictCodeList.交易方向_红利再投.getValue());
            // 单位净值 单位分红净值
            trdProductDeal.setUnitCprice(finAssetIntInfo.getUnitCprice());
            trdProductDeal.setTradeDate(entity.getReInvesDate());
            // 交易份额
            trdProductDeal.setShareAmt(finAssetIntInfo.getQty());
            trdProductDeal.setbTradeId(finAssetIntInfo.getTradeId());
            trdProductDeal.setPrinAmt(finAssetIntInfo.getQty());
            trdProductDeal.setFpriceAmt(finAssetIntInfo.getQty());
        }
        //      SHBS-980 全价总额未存值
        trdProductDeal.setFpriceAmt(finAssetIntInfo.getTradeAmt());
        //        交易本金字段保存为分红金额
        trdProductDeal.setPrinAmt(finAssetIntInfo.getTradeAmt());
        // 日期与交易日一致
        trdProductDeal.setSettleDate(trdProductDeal.getTradeDate());
        // 利息总额 分红金额
        trdProductDeal.setIntAmt(finAssetIntInfo.getTradeAmt());
        trdProductDeal.setSecManageAcctId("999999");
        trdProductDeal.setChlId(entity.getChlId());
        return trdProductDeal;
    }

    /**
     * 2.0改造生成汇总扩展交易entity <功能详细描述>
     *
     * @param entity 业务entity
     * @param trdFinProductDealTotal 汇总entity
     * @return 目标entity
     * @see
     * @since 1.0
     */
    private PaTrdFinProductDealTotal gen汇总扩展业务流程(TrdPlanCurrDuration001Dto entity,
                                                 TrdFinProductDealTotal trdFinProductDealTotal) {
        PaTrdFinProductDealTotal paTrdTotal = new PaTrdFinProductDealTotal();
        paTrdTotal.setTotleTradeId(entity.getTotleTradeId());
        // 货币基金红利再投
        if (StringUtil.equals(entity.getType(), "01")) {
            paTrdTotal.setRegDate(entity.getRegistDate());
            paTrdTotal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_货币型_红利再投.getValue());
        }
        else if (StringUtil.equals(entity.getType(), "05")) {
            paTrdTotal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_货币型_除权登记.getValue());
            paTrdTotal.setRegDate(entity.getRegistDate());
            paTrdTotal.setBonusCfmDate(
                StringUtil.isNotEmpty(entity.getArriveDate()) ? entity.getArriveDate() : entity.getReInvesDate());
        }
        // 非货币基金现金分红
        else if (StringUtil.equals(entity.getType(), "06")) {
            paTrdTotal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_货币型_现金分红.getValue());
            paTrdTotal.setRegDate(entity.getRegistDate());
        }
        // 分红类型
        paTrdTotal.setPayType(entity.getBonusMode());
        paTrdTotal.setWorkflowId(entity.getWorkflowId());
        return paTrdTotal;
    }

    /**
     * 2.0改造生成汇总交易entity <功能详细描述>
     *
     * @param entity 源entity
     * @return 目标entity
     * @see
     * @since 1.0
     */
    private TrdFinProductDealTotal gen汇总流程表(TrdPlanCurrDuration001Dto entity) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getTotleTradeId());
        // 交易编号
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_计划类资产_货币型_存续期.getValue());
        // 金融产品代码
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());

        trdFinProductDealTotal.setRemark(entity.getRemark());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        // TODO
        // trdFinProductDealTotal.setFinprodType(finprodType);
        // 金融产品类型
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        // 币种
        trdFinProductDealTotal.setCcy(entity.getCcy());
        // 货币基金红利再投
        if (StringUtil.equals(entity.getType(), "01")) {
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_红利再投.getValue());
            // 红利再投日
            trdFinProductDealTotal.setTradeDate(entity.getReInvesDate());
            // 交易份额 画面.分红金额
            trdFinProductDealTotal.setShareAmt(entity.getQty());
        }
        // 非货币基金分红除权
        else if (StringUtil.equals(entity.getType(), "02") || StringUtil.equals(entity.getType(), "05")) {
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_除权登记.getValue());
            // 除权日
            trdFinProductDealTotal.setTradeDate(entity.getExRightsDate());
            // 交易本金 ->权益登记日份额
            trdFinProductDealTotal.setPrinAmt(entity.getRegistShare());
            // 单位净值 单位分红净值
            trdFinProductDealTotal.setUnitCprice(entity.getUnitCprice());
        }
        // 非货基 现金分红
        else if (StringUtil.equals(entity.getType(), "03") || StringUtil.equals(entity.getType(), "06")) {
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_现金分红.getValue());
            // 交易金额 分红金额
            trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());
            trdFinProductDealTotal.setTradeDate(entity.getArriveDate());
            // 交易本金 ->权益登记日份额
            trdFinProductDealTotal.setPrinAmt(entity.getRegistShare());
            // 单位净值 单位分红净值
            trdFinProductDealTotal.setUnitCprice(entity.getUnitCprice());
            trdFinProductDealTotal.setbTradeId(entity.getbTradeId());
            trdFinProductDealTotal.setShareAmt(entity.getQty());
        }
        // 非货基 红利再投
        else if (StringUtil.equals(entity.getType(), "04")) {
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_红利再投.getValue());
            // 单位净值 单位分红净值
            trdFinProductDealTotal.setUnitCprice(entity.getUnitCprice());
            trdFinProductDealTotal.setTradeDate(entity.getReInvesDate());
            // 交易份额
            trdFinProductDealTotal.setShareAmt(entity.getQty());
            trdFinProductDealTotal.setbTradeId(entity.getbTradeId());
        }

        // 日期与交易日一致
        trdFinProductDealTotal.setSettleDate(trdFinProductDealTotal.getTradeDate());
        // 利息总额 分红金额
        trdFinProductDealTotal.setIntAmt(entity.getTradeAmt());
        trdFinProductDealTotal.setChlId(entity.getChlId());
        return trdFinProductDealTotal;
    }
}
