/*
 * 文件名：TrdFundDurationDealEntity.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：丁志鹏
 * 修改时间：2018年11月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.period.trdfunddurationnew.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
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
import com.joyintech.tams.invest.period.trdfunddurationnew.dto.TrdFundDurationNew001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
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
 * @see TrdFundDurationDealNewEntity
 * @since 1.0
 */
@Service
public class TrdFundDurationDealNewEntity extends BaseService<TrdFundDurationNew001Dto> {
    /**
     * 共通业务编号
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    /**
     * 共通mapper处理
     */
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
    private FinProductMapper finProductMapper;

    /**
     * 处理在代办中提交及保存的操作
     * @param entity 业务实体
     * @param type   操作类型
     * @param 交易流程状态
     * @param 不需要处理实时头寸
     */
    public void saveDataWhenStartWorkflow(TrdFundDurationNew001Dto entity, OperationType type, String 交易流程状态, boolean 不需要处理实时头寸) {
        String  实际流程状态 = 交易流程状态;
    	if (BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
            // 插入Entity到对应的业务流程DB中
            if (entity.getNeedSkipInsertWfl() == null || !entity.getNeedSkipInsertWfl()) {
                //共同
                实际流程状态 = restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdPlanInterestDuration/doInsert",
                    entity, String.class);
            }
            if (type != OperationType.REVOKE) {
                if (!DictCodeListExt1.交易流程状态_流程中.getValue().equals(实际流程状态)) {
                    execute交易共通(entity, DictCodeListExt1.交易流程状态_有效, 不需要处理实时头寸);
                }
                else {
                    execute交易共通(entity, DictCodeListExt1.交易流程状态_流程中, 不需要处理实时头寸);
                }
            }
        }
        else if (type == OperationType.REVOKE) {
            restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/doRevoke", entity,
                String.class);
        }
    }

    /**
     * 调用共通
     * @param entity 实例
     * @param 交易流程状态 交易状态
     * @param 不需要处理实时头寸
     */
    public void execute交易共通(TrdFundDurationNew001Dto entity, DictCodeListExt1 交易流程状态, boolean 不需要处理实时头寸) {
        // 调用业务组件.【金融产品交易共通.交易产品接口】
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        tradeDto.setWorkflowStatus(交易流程状态.getValue());
        tradeDto.setNeedExecuteEtrPosition(不需要处理实时头寸);
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        // 调交易共通时，一个利息明细一条交易，但存表时，一个交易明细可能有两个交易（收息、还本）
        if (DictCodeListExt1.交易流程状态_撤销 != 交易流程状态) {
            entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
                getCommonExcuteDtoByTrade(entity, baseTradeInfoDto,baseTradeProductDealDtos, 交易流程状态);
            });
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
            baseTradeProductDealCommServiceImpl.execute(tradeDto);
            execute交易共通子组合(entity,交易流程状态, false);
        } else {
            List<BaseTradeProductDealDto> baseTradeProductDealDtos1 = periodCommonService.findEffectiveDealForFund(entity.getpTradeId(),交易流程状态);
            for (BaseTradeProductDealDto baseTradeProductDealDto : baseTradeProductDealDtos1) {
                if (baseTradeProductDealDto.getStlPayOrdersList() == null || baseTradeProductDealDto.getStlPayOrdersList().isEmpty()) {
                    periodCommonService.gen划款指令_BY_交易(baseTradeProductDealDto, "REVOKE");
                }
                else {
                    List<StlPayOrdersDto> stls = new ArrayList<>();
                    baseTradeProductDealDto.getStlPayOrdersList().forEach(stlPayOrdersDto -> {// 查询时，多组合的划款指令都查出来了，根据组合代码分配
                        if (stlPayOrdersDto.getPortfolioId().equals(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())) {
                            stls.add(stlPayOrdersDto);
                        }
                    });
                    baseTradeProductDealDto.setStlPayOrdersList(stls);
                }
                TrdProductDeal deal1 = baseTradeProductDealDto.getTrdProductDeal();
                //给原交易编号赋值 TISHKBS-1365  add by caojin 20201004
                deal1.setbTradeId(deal1.getTradeId());
                deal1.setTradeId(deal1.getTradeId() + 'R');
                deal1.setCreateUser(tradeDto.getCreateUser());
                deal1.setCreateTime(tradeDto.getCreateTime());
                deal1.setCreateDept(tradeDto.getCreateDept());
                deal1.setUpdateTime(tradeDto.getUpdateTime());
                deal1.setUpdateUser(tradeDto.getUpdateUser());
                if (baseTradeProductDealDto.getStlPayOrdersList() != null) {
                    baseTradeProductDealDto.getStlPayOrdersList().forEach(stlPayOrdersDto ->
                            stlPayOrdersDto.setOrderId(stlPayOrdersDto.getOrderId() + 'R'));
                }
                getInterfInputDto(entity, baseTradeProductDealDto, 交易流程状态);
            }
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos1);
            baseTradeProductDealCommServiceImpl.execute(tradeDto);
            execute交易共通子组合(entity,交易流程状态, false);

            if (!DictCodeList.交易方向_红利再投.getValue().equals(entity.getPs())) {// 撤销现金分红的除权登记交易时，同时撤销现金分红的持仓
                List<BaseTradeProductDealDto> totalXjfh = this.findBTradeEffectiveDeal(entity.getpTradeId());
                if(totalXjfh != null){
                    List<BaseTradeProductDealDto> total现金分红交易 = new ArrayList<BaseTradeProductDealDto>();
                    for(BaseTradeProductDealDto baseTradeProductDealDto : totalXjfh){
                        if(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDeal())
                                && StringUtil.equals((baseTradeProductDealDto.getTrdProductDeal().getPs()),DictCodeList.交易方向_现金分红.getValue())){
                            total现金分红交易.add(baseTradeProductDealDto);
                        }
                    }
                    if (total现金分红交易 != null && !total现金分红交易.isEmpty()) {// 红利再投的除权登记交易时，这里应该检索不到数据，不处理
                        for (BaseTradeProductDealDto baseTradeProductDealDto : total现金分红交易) {
                            TrdProductDeal deal1 = baseTradeProductDealDto.getTrdProductDeal();
                            deal1.setTradeId(deal1.getTradeId() + 'R');
                            deal1.setCreateUser(tradeDto.getCreateUser());
                            deal1.setCreateTime(tradeDto.getCreateTime());
                            deal1.setCreateDept(tradeDto.getCreateDept());
                            deal1.setUpdateTime(tradeDto.getUpdateTime());
                            deal1.setUpdateUser(tradeDto.getUpdateUser());
                            getInterfInputDto(entity, baseTradeProductDealDto, 交易流程状态);
                        }
                        tradeDto.setBaseTradeProductDealDtos(total现金分红交易);
                        baseTradeProductDealCommServiceImpl.execute(tradeDto);
                        execute交易共通子组合(entity, 交易流程状态, true);
                    }
                }
            }
        }
    }

    public List<BaseTradeProductDealDto> findBTradeEffectiveDeal(String tradeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("statusValid", DictCodeListExt1.交易流程状态_撤销.getValue());
        map.put("statusValid03", DictCodeListExt1.交易流程状态_撤销.getValue());
        map.put("YON_Y", BussinessDictCodeList.是否_是.getValue());
        map.put("isTqzq", "");
        map.put("bTrade", tradeId);
        String bizCode = restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDurationNew/findBTradeId",
                map, String.class);
        if (StringUtil.isNotEmpty(bizCode)) {
            map.put("bizCode", bizCode);
            List<BaseTradeProductDealDto> result = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDurationNew" +
                            "/findEffectiveDeal",
                    map, BaseTradeProductDealDto.class);
            return result;
        }
        return null;
    }
    /**
     * 根据交易dto组装共同dto
     * @param entity
     * @param baseTradeInfoDto 交易dto
     * @param baseTradeProductDealDtos 共同dto list
     * @param 交易流程状态
     */
    private void getCommonExcuteDtoByTrade(TrdFundDurationNew001Dto entity, BaseTradeInfoDto baseTradeInfoDto,
                                           List<BaseTradeProductDealDto> baseTradeProductDealDtos, DictCodeListExt1 交易流程状态) {
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
    /**
     *
     */
//        子组合调用共同
    public void execute交易共通子组合(TrdFundDurationNew001Dto entity, DictCodeListExt1 交易流程状态, boolean 现金分红交易){
        // 调用业务组件.【金融产品交易共通.交易产品接口】
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        tradeDto.setWorkflowStatus(交易流程状态.getValue());

        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        // 调交易共通时，一个利息明细一条交易，但存表时，一个交易明细可能有两个交易（收息、还本）
        if (DictCodeListExt1.交易流程状态_撤销 != 交易流程状态) {
            entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
//                    子组合调用
                baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(x->{
                    getCommonExcuteDtoByTrade(entity, x,baseTradeProductDealDtos, 交易流程状态);
                });
            });
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        }
        else {
            Map<String,Object> map = new HashMap<>();
            map.put("statusValid", 交易流程状态.getValue());
            map.put("statusValid03", DictCodeListExt1.交易流程状态_撤销.getValue());
            map.put("isFund", Boolean.TRUE);
            if (现金分红交易) {
                map.put("bTrade", entity.getpTradeId());
            }
            else {
                map.put("bizCode", entity.getpTradeId());
            }
            baseTradeProductDealDtos.addAll(periodCommonService.getChiledTradeProductDealInfo(map));
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
            // 调用投管接口 add by qcx 20190206 start
            baseTradeProductDealDtos.forEach(baseTradeProductDealDto -> {
                getInterfInputDto(entity, baseTradeProductDealDto, DictCodeListExt1.交易流程状态_撤销);
            });
            // 调用投管接口 add by qcx 20190206 end
        }
        baseTradeProductDealCommServiceImpl.insertPosition(tradeDto);
    }

    private void getInterfInputDto(TrdFundDurationNew001Dto entity, BaseTradeProductDealDto baseTradeProductDealDto,
                                   DictCodeListExt1 交易流程状态) {
        if (baseTradeProductDealDto.getTrdProductDealAdd() == null
                || StringUtil.isEmpty(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())
                || AllTradeCommonUtil.isNotActPortfolio(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())) {
            return;
        }
        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        if(StringUtil.equals(entity.getAssetType(), DictCodeList.基金类资产类型_非货币基金.getValue())){
            investInterfCommDto.setPageId("TrdFundDuration002.01"); // 非货基
            paTrdProductDeal.setPaBussType("01");
            investInterfCommDto.setOtherTypeForConvert(DictCodeList.基金类资产类型_非货币基金.getValue());
        }
        else {
            investInterfCommDto.setPageId("TrdFundDuration002.02"); // 货基
            paTrdProductDeal.setPaBussType("02");
            investInterfCommDto.setOtherTypeForConvert(DictCodeList.基金类资产类型_货币基金.getValue());
        }
        investInterfCommDto.setTradeStatus(交易流程状态.getValue());
        investInterfCommDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
        investInterfCommDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
        investInterfCommDto.setPaTrdProductDeal(paTrdProductDeal);
        if (baseTradeProductDealDto.getStlPayOrdersList() != null
                && !baseTradeProductDealDto.getStlPayOrdersList().isEmpty()) {
            investInterfCommDto.setStlPayOrders(baseTradeProductDealDto.getStlPayOrdersList().get(0));
        }
        investInterfCommService.doInvestInterf(investInterfCommDto);
    }

    private void setDeal(TrdFundDurationNew001Dto entity, DictCodeListExt1 交易流程状态, List<BaseTradeProductDealDto> baseTradeProductDealDtos,
                         BaseTradeInfoDto baseTradeInfoDto, TrdProductDealAdd add, TrdProductDeal dealExe) {
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDto.setTrdProductDeal(dealExe);
        baseTradeProductDealDto.setTrdProductDealAdd(add);
        StlPayOrdersDto orders = new StlPayOrdersDto();
        if (baseTradeInfoDto.getStlPayOrders() != null) {
            BeanUtils.copyProperties(baseTradeInfoDto.getStlPayOrders(), orders);
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(new StlPayOrdersDto[]{orders}));
        }
        baseTradeProductDealDtos.add(baseTradeProductDealDto);
        getInterfInputDto(entity, baseTradeProductDealDto, 交易流程状态);
    }
    /**
     *
     * @param entity
     * @return
     */
    private List<WflPaTrdProductDeal> gen交易扩展业务流程(TrdFundDurationNew001Dto entity) {
        List<WflPaTrdProductDeal> wflPaTrdProductDeals = new ArrayList<>();
        WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
//        wflPaTrdProductDeal.setTradeId(entity.getTradeId());
//        货币基金红利再投
        if (StringUtil.equals(entity.getType(), "01")) {
            wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_货币基金红利再投.getValue());
        }
//        非货币基金分红除权
        else if (StringUtil.equals(entity.getType(), "02")) {
            if (DictCodeList.基金类资产类型_货币基金.getValue().equals(entity.getAssetType())) {
                wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_货币基金分红除权.getValue());
            }
            else {
            wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_非货币基金分红除权.getValue());
        }
        }
//        非货币基金现金分红
        else if (StringUtil.equals(entity.getType(), "03")) {
            if (DictCodeList.基金类资产类型_货币基金.getValue().equals(entity.getAssetType())) {
                wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_货币基金现金分红.getValue());
            }
            else {
            wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_非货币基金现金分红.getValue());
        }
        }
//        非货币基金红利再投
        else if (StringUtil.equals(entity.getType(), "04")) {
            wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_非货币基金红利再投.getValue());
        }
        wflPaTrdProductDeal.setWorkflowStatus(entity.getStatus());
        wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
        wflPaTrdProductDeals.add(wflPaTrdProductDeal);
        return wflPaTrdProductDeals;
    }

    public List<WflStlPayOrders> gen划款指令流程(TrdFundDurationNew001Dto entity) {
        if (entity.getBussTrdTransferOrder() == null) {
            return new ArrayList<>();
        }
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderService
                .save(entity.getBussTrdTransferOrder());
        List<WflStlPayOrders> stlPayOrdersDtos = new ArrayList<>();
        if (stlPayOrdersList != null) {
            stlPayOrdersList.forEach(stlPayOrders -> {
                WflStlPayOrders dto = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrders);
//                dto.setTradeId(entity.getTradeId());
                dto.setWorkflowId(entity.getWorkflowId());
                dto.setWorkflowStatus(DictCodeList.数据状态_新建中.getValue());
                dto.setCreateDept(entity.getCreateDept());
                dto.setUpdateTime(entity.getUpdateTime());
                dto.setCreateUser(entity.getCreateUser());
                dto.setCreateTime(entity.getCreateTime());
//              撤销
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
    public BaseTotalTradeInfoDto genBaseTotalTradeInfoDto(TrdFundDurationNew001Dto entity) {
        if (StringUtil.isEmpty(entity.getTradeMarket())) {
            FinProduct asset = finProductMapper.selectByPrimaryKey(entity.getFinprodId());
            entity.setTradeMarket(asset.getTradeMarket());
        }
        BaseTotalTradeInfoDto result = new BaseTotalTradeInfoDto();

        if(!StringUtil.equals(entity.getPstype(),DictCodeList.分红方式_现金分红.getValue())){
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
        //划款指令
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderService.save(
                entity.getBussTrdTransferOrder());
//        根据利息明细处理汇总及交易dto
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
//                StlPayOrdersDto stl = periodCommonService.gen划款指令流程(stlPayOrdersList, baseTradeInfoDto.getTrdProductDealAdd(), entity.getWorkflowId());
            StlPayOrdersDto stl = this.gen划款指令流程Add(stlPayOrdersList, baseTradeInfoDto.getTrdProductDealAdd(),baseTradeInfoDto.getTrdProductDeal(), entity.getWorkflowId());
            baseTradeInfoDto.setStlPayOrders(stl);
            //生成子组合利息明细共同方法
            Map<String,Object> subMap = new HashMap<>();
            subMap.put("portfolioId",finAssetIntInfo.getPortfolioId());
//            持仓时间
            subMap.put("dealDate", DateUtil.format(entity.getRegistDate()));
//            是否提前
            subMap.put("isAdvance", "N");
//            资产代码
            subMap.put("finprodMarketId",entity.getFinprodMarketId());
//            自定义不累加
            Map<String, BigDecimal> defaultMap = new HashMap<>();
            //defaultMap.put("unitCprice",finAssetIntInfo.getUnitCprice());
            //subMap.put("defaultMap", JsonUtils.object2Json(defaultMap));
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
//          子组合交易没有处理前置交易编号如果需要再说
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
                    //生成子组合划款指令
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
	 * 生成组合交易
	 * <功能详细描述>
	 * @param entity 业务实体
	 * @param finAssetIntInfo 利息明细
	 * @param deal 交易
	 * @return 生成结果
	 * @see
	 * @since 1.0
	 */
	private List<PtlTrade> 生成组合交易(TrdFundDurationNew001Dto entity, FinAssetIntInfo finAssetIntInfo, TrdProductDeal deal) {
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade =new PtlTrade();
		ptlTrade.setPortfolioId(finAssetIntInfo.getPortfolioId());
		ptlTrade.setBusiNo(deal.getTradeId());
		ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
		ptlTrade.setBusiId(entity.getFinprodId());
		ptlTrade.setBusiType(DictCodeList.共通业务类型_基金类资产存续期.getValue());
		ptlTrade.setSecManageAcctId("999999");
		ptlTrade.setSettleDate(deal.getTradeDate());
		ptlTrades.add(ptlTrade);
		return ptlTrades;
	}

	/**
     * 生成交易扩展 <功能详细描述>
     *
     * @param entity          业务实体
     * @param finAssetIntInfo 利息明细
     * @param deal            交易dto
     * @return 生成结果
     * @see
     * @since 1.0
     */
    private PaTrdProductDeal 生成交易扩展(TrdFundDurationNew001Dto entity, FinAssetIntInfo finAssetIntInfo,
            TrdProductDeal deal) {
        PaTrdProductDeal padeal = new PaTrdProductDeal();
        padeal.setTradeId(deal.getTradeId());
        if(StringUtil.equals(entity.getAssetType(), DictCodeList.基金类资产类型_货币基金.getValue())){
            if(StringUtil.equals(deal.getPs(),DictCodeList.交易方向_红利再投.getValue())){
                padeal.setPaBussType(DictCodeListExt1.平安业务类型_货币基金红利再投.getValue());
            }else if(StringUtil.equals(deal.getPs(),DictCodeList.交易方向_现金分红.getValue())){
                padeal.setPaBussType(DictCodeListExt1.平安业务类型_货币基金现金分红.getValue());
            }
        }else if(StringUtil.equals(entity.getAssetType(), DictCodeList.基金类资产类型_非货币基金.getValue())){
            if(StringUtil.equals(deal.getPs(),DictCodeList.交易方向_现金分红.getValue())){
                padeal.setPaBussType(DictCodeListExt1.平安业务类型_非货币基金现金分红.getValue());
            }else if(StringUtil.equals(deal.getPs(),DictCodeList.交易方向_红利再投.getValue())){
                padeal.setPaBussType(DictCodeListExt1.平安业务类型_非货币基金红利再投.getValue());
            }else if(StringUtil.equals(deal.getPs(),DictCodeList.交易方向_除权登记.getValue())){
                padeal.setPaBussType(DictCodeListExt1.平安业务类型_非货币基金分红除权.getValue());
            }
        }
        padeal.setStatus(entity.getStatus());
        padeal.setWorkflowId(entity.getWorkflowId());
        return padeal;
    }

    /**
     * 生成交易附属 <功能详细描述>
     *
     * @param entity          业务entity
     * @param finAssetIntInfo 利息明细
     * @param deal            生成的交易
     * @return
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd 生成交易附属(TrdFundDurationNew001Dto entity, FinAssetIntInfo finAssetIntInfo,
            TrdProductDeal deal) {
    	TrdProductDealAdd dealAdd = new TrdProductDealAdd();
      dealAdd.setTradeId(deal.getTradeId());
      dealAdd.setPortfolioId(finAssetIntInfo.getPortfolioId());
//      理财产品代码 金融产品代码
      dealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
      dealAdd.setPayType(finAssetIntInfo.getBonusMode());
      dealAdd.setStatus(entity.getStatus());
      dealAdd.setWorkflowId(entity.getWorkflowId());
//      分红除权
       if (StringUtil.equals(deal.getPs(), DictCodeList.交易方向_除权登记.getValue())) {
    	  //dealAdd.setRegDate(entity.getRegistDate());
    	  dealAdd.setBonusCfmDate(entity.getArriveDate() != null ? entity.getArriveDate() : entity.getReInvesDate());
       }
////      非货基红利再投|| 基金现金分红
//      else if(StringUtil.equals(deal.getPs(), DictCodeList.交易方向_现金分红.getValue())
//               || (StringUtil.equals(deal.getPs(), DictCodeList.交易方向_红利再投.getValue())
//                    && StringUtil.equals(entity.getAssetType(), DictCodeList.基金类资产类型_非货币基金.getValue()))){
//    	  dealAdd.setRegDate(entity.getRegistDate());
//      }
        dealAdd.setRegDate(entity.getRegistDate());
        //增加该字段保存权益登记日份额
        dealAdd.setRegDateAmt(finAssetIntInfo.getRegistShare());
        return dealAdd;
    }

    /**
     *
     * 生成交易 <功能详细描述>
     *
     * @param entity          业务entity
     * @param finAssetIntInfo 利率信息
     * @return 交易dto
     * @see
     * @since 1.0
     */
    private TrdProductDeal 生成交易(TrdFundDurationNew001Dto entity, FinAssetIntInfo finAssetIntInfo) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTotleTradeId(entity.getTotleTradeId());
        // 交易编号
        trdProductDeal.setTradeId(autoPeriodCommonService
                .buildBussinessCode(new BussinessCodeParameter(DictCodeList.共通业务类型_资产存续期交易)));
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_基金类资产存续期.getValue());
//      金融产品代码
        trdProductDeal.setFinprodId(entity.getFinprodId());
        trdProductDeal.setWorkflowId(entity.getWorkflowId());
        trdProductDeal.setRemark(entity.getRemark());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
//      金融产品类型
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
//      币种
        trdProductDeal.setCcy(entity.getCcy());
        //来自现金分红
        if(StringUtil.equals(DictCodeList.分红方式_现金分红.getValue(),entity.getPstype())){
            trdProductDeal.setPs(DictCodeList.交易方向_现金分红.getValue());
            //交易金额 分红金额
            trdProductDeal.setTradeAmt(finAssetIntInfo.getTradeAmt());
            trdProductDeal.setPrinAmt(finAssetIntInfo.getTradeAmt());
            trdProductDeal.setTradeDate(entity.getArriveDate());
            trdProductDeal.setShareAmt(finAssetIntInfo.getQty());
            // 单位净值 单位分红净值
            //trdProductDeal.setUnitCprice(finAssetIntInfo.getUnitCprice());
            trdProductDeal.setbTradeId(entity.getbTradeId());
        }else if (StringUtil.equals(DictCodeList.分红方式_红利再投.getValue(),entity.getPstype())){
            if(StringUtil.equals(entity.getAssetType(),DictCodeList.基金类资产类型_非货币基金.getValue())){
                trdProductDeal.setPs(DictCodeList.交易方向_红利再投.getValue());
                trdProductDeal.setPrinAmt(finAssetIntInfo.getTradeAmt());
                // 单位净值 单位分红净值
                //trdProductDeal.setUnitCprice(finAssetIntInfo.getUnitCprice());
                trdProductDeal.setTradeDate(entity.getReInvesDate());
                //交易份额
                trdProductDeal.setShareAmt(finAssetIntInfo.getQty());
                trdProductDeal.setbTradeId(entity.getbTradeId());
            }
        }else{
            if(StringUtil.equals(entity.getAssetType(),DictCodeList.基金类资产类型_货币基金.getValue())
                    && StringUtil.equals(finAssetIntInfo.getBonusMode(),DictCodeList.分红方式_红利再投.getValue())){
                trdProductDeal.setPs(DictCodeList.交易方向_红利再投.getValue());
                //         红利再投日
                trdProductDeal.setTradeDate(entity.getReInvesDate());
                //          交易份额    画面.分红金额
                trdProductDeal.setShareAmt(finAssetIntInfo.getTradeAmt());
                //        交易本金字段保存为分红金额
                trdProductDeal.setPrinAmt(finAssetIntInfo.getTradeAmt());
                trdProductDeal.setIntAmt(finAssetIntInfo.getTradeAmt());
                trdProductDeal.setFpriceAmt(finAssetIntInfo.getTradeAmt());
            }else{
                trdProductDeal.setPs(DictCodeList.交易方向_除权登记.getValue());
                //除权日
                trdProductDeal.setTradeDate(entity.getExRightsDate());
                trdProductDeal.setPrinAmt(finAssetIntInfo.getTradeAmt());
                trdProductDeal.setShareAmt(finAssetIntInfo.getQty());
                //单位净值 单位分红净值
                //trdProductDeal.setUnitCprice(finAssetIntInfo.getUnitCprice());
                // 交易金额
                trdProductDeal.setTradeAmt(finAssetIntInfo.getTradeAmt());
            }
        }

//      SHBS-980 全价总额未存值
        trdProductDeal.setFpriceAmt(finAssetIntInfo.getTradeAmt());
//    日期与交易日一致
        trdProductDeal.setSettleDate(trdProductDeal.getTradeDate());
//    利息总额 分红金额
        trdProductDeal.setIntAmt(finAssetIntInfo.getTradeAmt());
        // 通道交易 新增 强成西 2019/03/26 start
        trdProductDeal.setChlId("999999".equals(finAssetIntInfo.getChIld()) ? null : finAssetIntInfo.getChIld());
        // 通道交易 新增 强成西 2019/03/26 end
        trdProductDeal.setSecManageAcctId(finAssetIntInfo.getSecAcctId());
        trdProductDeal.setTradeMarket(entity.getTradeMarket());
        return trdProductDeal;
    }

    /**
     *
     * 2.0改造生成汇总扩展交易entity <功能详细描述>
     *
     * @param entity                 业务entity
     * @param trdFinProductDealTotal 汇总entity
     * @return 目标entity
     * @see
     * @since 1.0
     */
    private PaTrdFinProductDealTotal gen汇总扩展业务流程(TrdFundDurationNew001Dto entity,
            TrdFinProductDealTotal trdFinProductDealTotal) {
        PaTrdFinProductDealTotal paTrdTotal = new PaTrdFinProductDealTotal();
        paTrdTotal.setTotleTradeId(entity.getTotleTradeId());
        paTrdTotal.setRegDate(entity.getRegistDate());
        //除权日
        paTrdTotal.setTheoryPayDate(entity.getExRightsDate());
        paTrdTotal.setWorkflowId(entity.getWorkflowId());

        //JJTZJY-130 场外公募基金存续期交易-现金分红来账分拣确认时，弹窗页面报错（bug） start
        if(StringUtil.equals(entity.getPstype(),DictCodeList.分红方式_现金分红.getValue())){
            paTrdTotal.setPayType(DictCodeList.分红方式_现金分红.getValue());
        }
        //JJTZJY-130 场外公募基金存续期交易-现金分红来账分拣确认时，弹窗页面报错（bug） end
        return paTrdTotal;
    }

    /**
     *
     * 2.0改造生成汇总交易entity <功能详细描述>
     *
     * @param entity 源entity
     * @return 目标entity
     * @see
     * @since 1.0
     */
    private TrdFinProductDealTotal gen汇总流程表(TrdFundDurationNew001Dto entity) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getTotleTradeId());
//      交易编号
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_基金类资产存续期.getValue());
//      金融产品代码
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());

        trdFinProductDealTotal.setRemark(entity.getRemark());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        // 金融产品类型
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        //币种
        trdFinProductDealTotal.setCcy(entity.getCcy());
        //红利下发日
        trdFinProductDealTotal.setTradeDate(entity.getArriveDate());
        //红利再投日
        trdFinProductDealTotal.setSettleDate(entity.getReInvesDate());
        trdFinProductDealTotal.setShareAmt(entity.getQty());
        trdFinProductDealTotal.setPrinAmt(entity.getRegistShare());
        // 交易金额
        trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());
        trdFinProductDealTotal.setbTradeId(entity.getbTradeId());
        trdFinProductDealTotal.setIntAmt(entity.getTradeAmt());
        trdFinProductDealTotal.setTradeMarket(entity.getTradeMarket());
        // 多级法人改造 add by caiqun 20200615
        if(MultilevelDataAuthorityUtil.isEnabled()){
            // 机构代码
            trdFinProductDealTotal.setInstitutionCode(MultilevelDataAuthorityUtil.getOrganizationCode(entity.getCreateUser()));
        }
        // 多级法人改造 add by caiqun 20200615

        //JJTZJY-130 场外公募基金存续期交易-现金分红来账分拣确认时，弹窗页面报错（bug） start
        if(StringUtil.equals(entity.getPstype(),DictCodeList.分红方式_现金分红.getValue())){
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_现金分红.getValue());
        }
        //JJTZJY-130 场外公募基金存续期交易-现金分红来账分拣确认时，弹窗页面报错（bug） end
        return trdFinProductDealTotal;
    }

    /**
     * 生成划款指令（根据划款指令列表分配划款指令）
     * <功能详细描述>
     * @param stlPayOrdersList 划款指令list
     * @param dealAdd 交易附属
     * @param workflowId 工作流代码
     * @return 生成结果
     * @see
     * @since 1.0
     */
    public StlPayOrdersDto gen划款指令流程Add(List<BussTrdTransferOrderDto> stlPayOrdersList, TrdProductDealAdd dealAdd,TrdProductDeal deal,
                                     String workflowId) {
        StlPayOrdersDto stlPayOrders = null;
        if(!StringUtil.equals(dealAdd.getPayType(),DictCodeList.分红方式_现金分红.getValue())){
            return stlPayOrders;
        }
        for (BussTrdTransferOrderDto dto : stlPayOrdersList) {
            if (dto.getPortfolioId().equals(dealAdd.getPortfolioId())
                    && (StringUtil.equals(dto.getSecAcctId(), deal.getSecManageAcctId())
                        || StringUtil.equals(dto.getSecAcctId(), deal.getChlId()))) {
                stlPayOrders = dto;
                stlPayOrders.setOrderStatus(DictCodeList.清算操作类型_新建.getValue());
                stlPayOrders.setTradeId(dealAdd.getTradeId());
                stlPayOrders.setWorkflowId(workflowId);
                break;
            }
        }
        return stlPayOrders;
    }
}
