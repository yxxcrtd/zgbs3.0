package com.joyintech.tams.invest.period.trdprojnetduration.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdprojnetduration.dto.TrdProjectNetDurationDto;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.workflow.engine.constant.OperationType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrdProjectNetDurationDealEntity extends BaseService<TrdProjectNetDurationDto> {
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;
    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;
    @Autowired
    private PeriodCommonService periodCommonService;

    /**
     * 文件附件接口
     */
    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 除权登记时获取划款指令dto
     * @param entity 数据实体
     */
    public void generateOrder(TrdProjectNetDurationDto entity){
//        划款指令不为空时不处理
        if(CollectionUtils.isNotEmpty(entity.getBussTrdTransferOrder())){
            return;
        }
        TrdProductDeal deal = getTrdProductDeal(entity);
        BussSettleOrderParameterDto param = new BussSettleOrderParameterDto();
        param.setIsTotalTrade(DictCodeList.是否_否.getValue());
        param.setPortfolioId(entity.getPortfolioId());
        param.setDeal(deal);
        BussTrdTransferOrderDto ret = bussTrdTransferOrderService.generateOrder(param);
        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtos = new ArrayList<>();
        if(ret != null){
            bussTrdTransferOrderDtos.add(ret);
            if(StringUtil.equals(OperationType.REVOKE.toString(), entity.getMode())) {
                ret.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
            }
        }
        entity.setBussTrdTransferOrder(bussTrdTransferOrderDtos);
    }

    /**
     * 处理在代办中提交及保存的操作
     * @param entity 业务实体
     * @param type 操作类型
     * @param 是否不需要执行实时持仓共通
     */
    public void saveDataWhenStartWorkflow(TrdProjectNetDurationDto entity, OperationType type, boolean 是否不需要执行实时持仓共通) {
        if (!CollectionUtils.isEmpty(entity.getUploadFileList())
                && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            String busiCode = entity.getTotleTradeId();
            if (entity.getTotleTradeId() != null && entity.getTotleTradeId().endsWith("R")){
                busiCode = entity.getTotleTradeId().substring(0,entity.getTotleTradeId().length()-1);
            }
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),busiCode);
        }
        if (BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
            // 插入Entity到对应的业务流程DB中
            String status = null;
            if (entity.getNeedSkipInsertWfl() == null || !entity.getNeedSkipInsertWfl()) {
                status = restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdPlanInterestDuration/doInsert",
                        entity, String.class);
            }
            if (type != OperationType.REVOKE) {
                if (status != null && DictCodeListExt1.交易流程状态_有效.getValue().equals(status)) {
                    execute交易共通(entity, DictCodeListExt1.交易流程状态_有效, 是否不需要执行实时持仓共通);
                }
                else {
                    execute交易共通(entity, DictCodeListExt1.交易流程状态_流程中, 是否不需要执行实时持仓共通);
                }
            }
        }
        else if (type == OperationType.REVOKE) {
            restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/doRevoke", entity,
                    String.class);
        }
    }
    public void execute交易共通(TrdProjectNetDurationDto entity, DictCodeListExt1 交易流程状态, boolean 是否不需要执行实时持仓共通) {
        // 调用业务组件.【金融产品交易共通.交易产品接口】
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        tradeDto.setWorkflowStatus(交易流程状态.getValue());
        tradeDto.setWorkflowId(entity.getWorkflowId());
        tradeDto.setNeedExecuteEtrPosition(是否不需要执行实时持仓共通);

        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        // 调交易共通时，一个利息明细一条交易，但存表时，一个交易明细可能有两个交易（收息、还本）
        if (DictCodeListExt1.交易流程状态_撤销 != 交易流程状态) {
            entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
                getCommonExcuteDtoByTrade(entity, 交易流程状态, baseTradeInfoDto,baseTradeProductDealDtos);
            });
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        } else {
            List<BaseTradeProductDealDto> baseTradeProductDealDtos1 = periodCommonService.findEffectiveDeal(entity.getpTradeId(),交易流程状态);
            //除权登记划款指令特殊处理（多组合持仓分红，带出所有指令）
            //指令需要分组
            baseTradeProductDealDtos1 = groupPayByList(baseTradeProductDealDtos1);
            for (BaseTradeProductDealDto baseTradeProductDealDto : baseTradeProductDealDtos1) {
                TrdProductDeal deal1 = baseTradeProductDealDto.getTrdProductDeal();
                deal1.setTradeId(deal1.getTradeId() + 'R');
                if(deal1.getShareAmt() == null){ // if(deal1.getPrinAmt() != null){ 不能直接把本金设置给交易份额
                    deal1.setShareAmt(deal1.getPrinAmt());
                }
                deal1.setCreateUser(tradeDto.getCreateUser());
                deal1.setCreateTime(tradeDto.getCreateTime());
                deal1.setCreateDept(tradeDto.getCreateDept());
                deal1.setUpdateTime(tradeDto.getUpdateTime());
                deal1.setUpdateUser(tradeDto.getUpdateUser());
            }
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos1);
        }

        baseTradeProductDealCommServiceImpl.execute(tradeDto);
//        子组合调用共通
        execute交易共通子组合(entity,交易流程状态);
        //撤销除权登记后，处理现金分红的持仓
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
                execute交易共通子组合(entity, 交易流程状态);
            }
        }
    }

    /**
     * 分组撤销除权登记后的指令分组
     */
    public List<BaseTradeProductDealDto> groupPayByList(List<BaseTradeProductDealDto> baseTradeProductDealDtos1){
        Map<String,Object> param = new HashMap<>();
        List<String> groupTradeId = new ArrayList<>();
        for (BaseTradeProductDealDto item : baseTradeProductDealDtos1) {
            groupTradeId.add(item.getTrdProductDeal().getTradeId());
        }
        param.put("groupTradeId",groupTradeId);
        List<StlPayOrdersDto> stlPayOrders = periodCommonService.getGroupPayByList(param);
        try {
            for (BaseTradeProductDealDto baseTradeProductDealDto : baseTradeProductDealDtos1) {
                for(StlPayOrdersDto stlPayOrdersDto : baseTradeProductDealDto.getStlPayOrdersList()){
                    boolean flag = false;
                    for(StlPayOrdersDto stlPayOrdersDtoGroup : stlPayOrders){
                        List<StlPayOrdersDto> stlPayOrdersDtoList = new ArrayList<>();
                        if(null!=baseTradeProductDealDto.getTrdProductDeal()
                                &&StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getTradeId(),stlPayOrdersDtoGroup.getTradeId())
                        &&StringUtil.equals(stlPayOrdersDtoGroup.getOrderId(),stlPayOrdersDto.getOrderId())){
                            flag = true;
                            stlPayOrdersDtoList.add(stlPayOrdersDto);
                            baseTradeProductDealDto.setStlPayOrdersList(stlPayOrdersDtoList);
                            break;
                        }
                    }
                    if(flag){break;}
                }
            }
        }
        catch (Exception e){}
        return baseTradeProductDealDtos1;
    }
    /**
     * 根据交易dto组装共同dto
     * @param entity
     * @param 交易流程状态
     * @param baseTradeInfoDto 交易dto
     * @param baseTradeProductDealDtos 共同dto list
     */
    private void getCommonExcuteDtoByTrade(TrdProjectNetDurationDto entity, DictCodeListExt1 交易流程状态, BaseTradeInfoDto baseTradeInfoDto, List<BaseTradeProductDealDto> baseTradeProductDealDtos) {
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

    private void setDeal(TrdProjectNetDurationDto entity, DictCodeListExt1 交易流程状态, List<BaseTradeProductDealDto> baseTradeProductDealDtos, BaseTradeInfoDto baseTradeInfoDto, TrdProductDealAdd add, TrdProductDeal dealExe) {
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDto.setTrdProductDeal(dealExe);
        baseTradeProductDealDto.setTrdProductDealAdd(add);
        StlPayOrdersDto orders = new StlPayOrdersDto();
        if (baseTradeInfoDto.getStlPayOrders() != null) {
            BeanUtils.copyProperties(baseTradeInfoDto.getStlPayOrders(), orders);
        }
        baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(new StlPayOrdersDto[]{orders}));
        baseTradeProductDealDtos.add(baseTradeProductDealDto);
    }
    /**
     *
     * @param entity
     * @return
     */
    private List<WflPaTrdProductDeal> gen交易扩展业务流程(TrdProjectNetDurationDto entity) {
        List<WflPaTrdProductDeal> wflPaTrdProductDeals = new ArrayList<>();
        WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
        wflPaTrdProductDeal.setTradeId(entity.getTotleTradeId());
//        计划类净值型分红除权
        if (StringUtil.equals(entity.getType(),"02")){
            //wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_净值型_分红除权.getValue());
        }
//        计划类净值型现金分红
        else if (StringUtil.equals(entity.getType(),"03")){
            wflPaTrdProductDeal.setPaBussType(DictCodeListExt3.平安业务类型_权益类资产_现金分红.getValue());
        }
//        计划类净值型再投
        else if (StringUtil.equals(entity.getType(),"04")){
            //wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_净值型_红利再投.getValue());
        }
        wflPaTrdProductDeal.setWorkflowStatus(entity.getStatus());
        wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
        wflPaTrdProductDeals.add(wflPaTrdProductDeal);
        return wflPaTrdProductDeals;
    }

    /**
     *
     * @param entity
     * @return
     */
    private List<WflTrdProductDealAdd> gen交易附属业务流程(TrdProjectNetDurationDto entity) {
        List<WflTrdProductDealAdd>wflTrdProductDealAdds = new ArrayList<>();
        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        wflTrdProductDealAdd.setTradeId(entity.getTotleTradeId());
        wflTrdProductDealAdd.setPortfolioId(entity.getPortfolioId());
//        理财产品代码 金融产品代码
        wflTrdProductDealAdd.setProdId(entity.getFinprodId());
        wflTrdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        wflTrdProductDealAdd.setPayType(entity.getBonusMode());
        wflTrdProductDealAdd.setPortfolioId(entity.getPortfolioId());
        wflTrdProductDealAdd.setWorkflowStatus(entity.getStatus());
        wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
//        净值分红除权
        if(StringUtil.equals(entity.getType(),"02")){
            wflTrdProductDealAdd.setRegDate(entity.getRegistDate());
            wflTrdProductDealAdd.setBonusCfmDate(entity.getArriveDate()!=null?entity.getArriveDate():entity.getReInvesDate());
        }
//        净值红利再投|| 现金分红
        else{
            wflTrdProductDealAdd.setRegDate(entity.getRegistDate());
        }
        //       登记份额
        wflTrdProductDealAdd.setRegDateAmt(entity.getRegistShare());
        wflTrdProductDealAdds.add(wflTrdProductDealAdd);
        return wflTrdProductDealAdds;
    }

    /**
     *
     * 金融产品交易
     * <功能详细描述>
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    public FinProductTradeDto genFinProductTradeDto(TrdProjectNetDurationDto entity) {
        // 5.构造调用组件的输入参数DTO
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDtos.add(baseTradeProductDealDto);
        TrdProductDeal trdProductDeal = getTrdProductDeal(entity);
        if(entity.getBussTrdTransferOrder()!=null &&entity.getBussTrdTransferOrder().size()>0 ){
            // 划款指令表Entity
            List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderService.save(
                    entity.getBussTrdTransferOrder());
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
//        添加参数分红类型
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
    public static TrdProductDeal getTrdProductDeal(TrdProjectNetDurationDto entity) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
//        交易编号
        trdProductDeal.setTradeId(entity.getTotleTradeId());
        trdProductDeal.setBusiType(DictCodeListExt3.共通业务类型_权益类资产存续期.getValue());
//        金融产品代码
        trdProductDeal.setFinprodId(entity.getFinprodId());
//        金融产品类型
        trdProductDeal.setFinprodType2(DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue());
//        币种
        trdProductDeal.setCcy(entity.getCcy());
//        净值型除权登记
        if(StringUtil.equals(entity.getType(),"02")){
            trdProductDeal.setPs(DictCodeList.交易方向_除权登记.getValue());
//            除权日
            trdProductDeal.setTradeDate(entity.getExRightsDate());
//            交易本金 ->权益登记日份额
            trdProductDeal.setPrinAmt(entity.getRegistShare());
//            单位净值 单位分红净值
            trdProductDeal.setUnitCprice(entity.getUnitCprice());
        }
//        净值型 现金分红
        else if(StringUtil.equals(entity.getType(),"03")){
            trdProductDeal.setPs(DictCodeList.交易方向_现金分红.getValue());
//            交易金额 分红金额
            trdProductDeal.setTradeAmt(entity.getTradeAmt());
            trdProductDeal.setTradeDate(entity.getArriveDate());
            //            单位净值 单位分红净值
            trdProductDeal.setUnitCprice(entity.getUnitCprice());
        }
//        净值型 红利再投
        else if(StringUtil.equals(entity.getType(),"04")){
            trdProductDeal.setPs(DictCodeList.交易方向_红利再投.getValue());
            //            单位净值 单位分红净值
            trdProductDeal.setUnitCprice(entity.getUnitCprice());
            trdProductDeal.setTradeDate(entity.getReInvesDate());
//            交易份额
            trdProductDeal.setShareAmt(entity.getQty()!=null?entity.getQty():entity.getTradeAmt());
        }
        if(trdProductDeal.getTradeAmt()==null){
            //        共同调用使用
            trdProductDeal.setTradeAmt(entity.getShareAmt());
        }
        //维护交易本金字段为分红金额
        trdProductDeal.setPrinAmt(entity.getTradeAmt());
//      日期与交易日一致
        trdProductDeal.setSettleDate(trdProductDeal.getTradeDate());
//      利息总额 分红金额
        trdProductDeal.setIntAmt(entity.getTradeAmt());
        trdProductDeal.setSecManageAcctId("999999");
        return trdProductDeal;
    }
    public List<WflTrdProductDeal> gen交易业务流程(TrdProjectNetDurationDto entity) {
        List<WflTrdProductDeal> deals = new ArrayList<>();
        WflTrdProductDeal trdProductDeal = new WflTrdProductDeal();
//        交易编号
        trdProductDeal.setTradeId(entity.getTotleTradeId());
        trdProductDeal.setBusiType(DictCodeListExt3.共通业务类型_权益类资产存续期.getValue());
//        金融产品代码
        trdProductDeal.setFinprodId(entity.getFinprodId());
        trdProductDeal.setWorkflowId(entity.getWorkflowId());
        trdProductDeal.setWorkflowStatus(entity.getStatus());
        trdProductDeal.setTrader(entity.getTrader());
        trdProductDeal.setRemark(entity.getRemark());
//              撤销
        if(StringUtil.equals(OperationType.REVOKE.toString(), entity.getMode())) {
            trdProductDeal.setpTradeId(entity.getpTradeId());
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
        }
        else{
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        }
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
//        金融产品类型
        trdProductDeal.setFinprodType2(DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue());
//        币种
        trdProductDeal.setCcy(entity.getCcy());
//        货币基金红利再投
        if(StringUtil.equals(entity.getType(),"01")){
            trdProductDeal.setPs(DictCodeList.交易方向_红利再投.getValue());
//           红利再投日
            trdProductDeal.setTradeDate(entity.getReInvesDate());
//            交易份额	画面.分红金额
            trdProductDeal.setShareAmt(entity.getQty());
        }
//        非货币基金分红除权
        else if(StringUtil.equals(entity.getType(),"02")){
            trdProductDeal.setPs(DictCodeList.交易方向_除权登记.getValue());
//            除权日
            trdProductDeal.setTradeDate(entity.getExRightsDate());
//            交易本金 ->权益登记日份额
            trdProductDeal.setPrinAmt(entity.getRegistShare());
//            单位净值 单位分红净值
            trdProductDeal.setUnitCprice(entity.getUnitCprice());
        }
//        非货基 现金分红
        else if(StringUtil.equals(entity.getType(),"03")){
            trdProductDeal.setPs(DictCodeList.交易方向_现金分红.getValue());
//            交易金额 分红金额
            trdProductDeal.setTradeAmt(entity.getTradeAmt());
            //            单位净值 单位分红净值
            trdProductDeal.setUnitCprice(entity.getUnitCprice());
            //            红利再投日
            trdProductDeal.setTradeDate(entity.getArriveDate());
            trdProductDeal.setbTradeId(entity.getbTradeId());
        }
//        非货基 红利再投
        else if(StringUtil.equals(entity.getType(),"04")){
            trdProductDeal.setPs(DictCodeList.交易方向_红利再投.getValue());
            //            单位净值 单位分红净值
            trdProductDeal.setUnitCprice(entity.getUnitCprice());
//            红利再投日
            trdProductDeal.setTradeDate(entity.getReInvesDate());
//            交易份额
            trdProductDeal.setbTradeId(entity.getbTradeId());
            trdProductDeal.setShareAmt(entity.getQty());
        }
//维护交易本金字段为分红金额
        trdProductDeal.setPrinAmt(entity.getTradeAmt());
//      日期与交易日一致
        trdProductDeal.setSettleDate(trdProductDeal.getTradeDate());
//      利息总额 分红金额
        trdProductDeal.setIntAmt(entity.getTradeAmt());
        trdProductDeal.setSecManageAcctId("999999");
        deals.add(trdProductDeal);
        return deals;
    }

    public List<WflStlPayOrders> gen划款指令流程(TrdProjectNetDurationDto entity) {
        if(entity.getBussTrdTransferOrder() == null&& !StringUtil.equals(entity.getType(), "03")){
            return new ArrayList<>();
        }
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderService.save(
                entity.getBussTrdTransferOrder());
        List<WflStlPayOrders> stlPayOrdersDtos = new ArrayList<>();
        if (stlPayOrdersList != null) {
            stlPayOrdersList.forEach(stlPayOrders -> {
                WflStlPayOrders dto = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrders);
                dto.setTradeId(entity.getTotleTradeId());
                dto.setWorkflowId(entity.getWorkflowId());
                dto.setWorkflowStatus(DictCodeList.数据状态_新建中.getValue());
                dto.setCreateDept(entity.getCreateDept());
                dto.setUpdateTime(entity.getUpdateTime());
                dto.setCreateUser(entity.getCreateUser());
                dto.setCreateTime(entity.getCreateTime());
//              撤销
                if(StringUtil.equals(OperationType.REVOKE.toString(), entity.getOperateType())) {
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
    public BaseTotalTradeInfoDto genBaseTotalTradeInfoDto(TrdProjectNetDurationDto entity) {
        BaseTotalTradeInfoDto result = new BaseTotalTradeInfoDto();
//      不为现金分红时仅生成不入库
        if(!StringUtil.equals(entity.getType(), "03")) {
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
//        处理划款指令
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderService.save(
                entity.getBussTrdTransferOrder());
//        根据利息明细处理汇总及交易dto
        List<FinAssetIntInfo> 利息明细 = entity.getFinAssetIntInfoList();
        //处理交易明细的前置编号
        List<FinAssetIntInfo> finAssetIntInfos = new ArrayList<>();
        利息明细.forEach(finAssetIntInfo -> {
            BaseTradeInfoDto baseTradeInfoDto = new BaseTradeInfoDto();
            baseTradeInfoDtoList.add(baseTradeInfoDto);
            TrdProductDeal deal = 生成交易(entity, finAssetIntInfo);
            //生成交易明细的前置编号
            finAssetIntInfo.setTradeId(deal.getTradeId());
            finAssetIntInfos.add(finAssetIntInfo);
            baseTradeInfoDto.setTrdProductDeal(deal);
            TrdProductDealAdd dealAdd = 生成交易附属(entity, finAssetIntInfo, deal);
            baseTradeInfoDto.setTrdProductDealAdd(dealAdd);
            PaTrdProductDeal paDeal = 生成交易扩展(entity, finAssetIntInfo, deal);
            baseTradeInfoDto.setPaTrdProductDeal(paDeal);
            baseTradeInfoDto.setPtlTrade(生成组合交易(entity, finAssetIntInfo, deal));
            if(StringUtil.equals(entity.getType(), "02")||StringUtil.equals(entity.getType(), "03")){
                baseTradeInfoDto.setTrdTradeFeeDetail(生成组合交易费用明细(entity, finAssetIntInfo, deal));
            }
            StlPayOrders stl = periodCommonService.gen划款指令流程(stlPayOrdersList,dealAdd,entity.getWorkflowId());
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
//            自定义不累加
            Map<String, BigDecimal> defaultMap = new HashMap<>();
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
                if(StringUtil.equals(entity.getType(), "02")||StringUtil.equals(entity.getType(), "03")){
                    baseTradeInfoDto.setTrdTradeFeeDetail(生成组合交易费用明细(entity, subFinAssetIntInfo, deal));
                }
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
        entity.setFinAssetIntInfoList(finAssetIntInfos);
        return result;
    }
    /**
     *
     */
//        子组合调用共同
    public void execute交易共通子组合(TrdProjectNetDurationDto entity, DictCodeListExt1 交易流程状态){
        // 调用业务组件.【金融产品交易共通.交易产品接口】
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        tradeDto.setWorkflowStatus(交易流程状态.getValue());
        tradeDto.setWorkflowId(entity.getWorkflowId());

        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        // 调交易共通时，一个利息明细一条交易，但存表时，一个交易明细可能有两个交易（收息、还本）
        if (DictCodeListExt1.交易流程状态_撤销 != 交易流程状态) {
            entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
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
            map.put("bizCode", entity.getpTradeId());
            baseTradeProductDealDtos.addAll(periodCommonService.getChiledTradeProductDealInfo(map));
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        }
        baseTradeProductDealCommServiceImpl.insertPosition(tradeDto);
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
    private List<PtlTrade>  生成组合交易(TrdProjectNetDurationDto entity, FinAssetIntInfo finAssetIntInfo, TrdProductDeal deal) {
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade =new PtlTrade();
        ptlTrade.setPortfolioId(finAssetIntInfo.getPortfolioId());
        ptlTrade.setBusiNo(deal.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(entity.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_计划类资产_净值型_存续期.getValue());
        ptlTrade.setSecManageAcctId("999999");
        ptlTrade.setSettleDate(entity.getTradeDate());
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
    private PaTrdProductDeal 生成交易扩展(TrdProjectNetDurationDto entity, FinAssetIntInfo finAssetIntInfo,
                                    TrdProductDeal deal) {
        PaTrdProductDeal padeal = new PaTrdProductDeal();
        padeal.setTradeId(deal.getTradeId());
        //        计划类净值型分红除权
        if (StringUtil.equals(entity.getType(),"02")){
            //padeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_净值型_分红除权.getValue());
        }
//        计划类净值型现金分红
        else if (StringUtil.equals(entity.getType(),"03")){
            padeal.setPaBussType(DictCodeListExt3.平安业务类型_权益类资产_现金分红.getValue());
        }
//        计划类净值型再投
        else if (StringUtil.equals(entity.getType(),"04")){
            //padeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_净值型_红利再投.getValue());
        }
////      货币基金红利再投
//        if (StringUtil.equals(entity.getType(), "01")) {
//            padeal.setPaBussType(DictCodeListExt1.平安业务类型_货币基金红利再投.getValue());
//        }
////      非货币基金分红除权
//        else if (StringUtil.equals(entity.getType(), "02")) {
//            padeal.setPaBussType(DictCodeListExt1.平安业务类型_非货币基金分红除权.getValue());
//        }
////      非货币基金现金分红
//        else if (StringUtil.equals(entity.getType(), "03")) {
//            padeal.setPaBussType(DictCodeListExt1.平安业务类型_非货币基金现金分红.getValue());
//        }
////      非货币基金红利再投
//        else if (StringUtil.equals(entity.getType(), "04")) {
//            padeal.setPaBussType(DictCodeListExt1.平安业务类型_非货币基金红利再投.getValue());
//        }
////        TODO 货币分红除权
//        else if (StringUtil.equals(entity.getType(), "06")) {
//            padeal.setPaBussType(DictCodeListExt1.平安业务类型_货币基金现金分红.getValue());
//        }
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
    private TrdProductDealAdd 生成交易附属(TrdProjectNetDurationDto entity, FinAssetIntInfo finAssetIntInfo,
                                     TrdProductDeal deal) {
        TrdProductDealAdd dealAdd = new TrdProductDealAdd();
        dealAdd.setTradeId(deal.getTradeId());
        dealAdd.setPortfolioId(finAssetIntInfo.getPortfolioId());
//      理财产品代码 金融产品代码
//      dealAdd.pr(entity.getFinprodId());
        dealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        dealAdd.setPayType(entity.getBonusMode());
        dealAdd.setStatus(entity.getStatus());
        dealAdd.setWorkflowId(entity.getWorkflowId());
//      货币基金分红
        if (StringUtil.equals(entity.getType(), "01")) {

        }
//      分红除权
        else if (StringUtil.equals(entity.getType(), "02")||StringUtil.equals(entity.getType(), "05")) {
            dealAdd.setRegDate(entity.getRegistDate());
            dealAdd
                    .setBonusCfmDate(entity.getArriveDate() != null ? entity.getArriveDate() : entity.getReInvesDate());
        }
//      非货基 红利再投|| 现金分红
        else {
            dealAdd.setRegDate(entity.getRegistDate());
        }
        //      增加该字段保存权益登记日份额
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
    private TrdProductDeal 生成交易(TrdProjectNetDurationDto entity, FinAssetIntInfo finAssetIntInfo) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTotleTradeId(entity.getTotleTradeId());
        // 交易编号
        trdProductDeal.setTradeId(autoPeriodCommonService
                .buildBussinessCode(new BussinessCodeParameter(DictCodeList.共通业务类型_资产存续期交易)));
        trdProductDeal.setBusiType(DictCodeListExt3.共通业务类型_权益类资产存续期.getValue());
//      金融产品代码
        trdProductDeal.setFinprodId(entity.getFinprodId());
        trdProductDeal.setWorkflowId(entity.getWorkflowId());
        trdProductDeal.setRemark(entity.getRemark());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
//      金融产品类型
        trdProductDeal.setFinprodType2(DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue());
//      币种
        trdProductDeal.setCcy(entity.getCcy());
//      货币基金红利再投
        if (StringUtil.equals(entity.getType(), "01")) {
            trdProductDeal.setPs(DictCodeList.交易方向_红利再投.getValue());
//         红利再投日
            trdProductDeal.setTradeDate(entity.getReInvesDate());
//          交易份额    画面.分红金额
            trdProductDeal.setShareAmt(finAssetIntInfo.getQty());
        }
//      除权登记
        else if (StringUtil.equals(entity.getType(), "02")||StringUtil.equals(entity.getType(), "05")) {
            trdProductDeal.setPs(DictCodeList.交易方向_除权登记.getValue());
//          除权日
            trdProductDeal.setTradeDate(entity.getExRightsDate());
//          交易本金 ->权益登记日份额
            trdProductDeal.setPrinAmt(finAssetIntInfo.getRegistShare());
//          单位净值 单位分红净值
            trdProductDeal.setUnitCprice(finAssetIntInfo.getUnitCprice());
            // 交易金额
            trdProductDeal.setTradeAmt(finAssetIntInfo.getTradeAmt());
        }
//      现金分红
        else if (StringUtil.equals(entity.getType(), "03")||StringUtil.equals(entity.getType(), "06")) {
            trdProductDeal.setPs(DictCodeList.交易方向_现金分红.getValue());
//          交易金额 分红金额
            trdProductDeal.setTradeAmt(finAssetIntInfo.getTradeAmt());
            trdProductDeal.setTradeDate(entity.getArriveDate());
            // 单位净值 单位分红净值
            trdProductDeal.setUnitCprice(finAssetIntInfo.getUnitCprice());
            trdProductDeal.setbTradeId(finAssetIntInfo.getTradeId());
        }
//      非货基 红利再投
        else if (StringUtil.equals(entity.getType(), "04")) {
            trdProductDeal.setPs(DictCodeList.交易方向_红利再投.getValue());
            // 单位净值 单位分红净值
            trdProductDeal.setUnitCprice(finAssetIntInfo.getUnitCprice());
            trdProductDeal.setTradeDate(entity.getReInvesDate());
//          交易份额
            trdProductDeal.setShareAmt(finAssetIntInfo.getQty());
            trdProductDeal.setbTradeId(finAssetIntInfo.getTradeId());
        }
        //      SHBS-980 全价总额未存值
        trdProductDeal.setFpriceAmt(finAssetIntInfo.getTradeAmt());
        //        交易本金字段保存为分红金额
        trdProductDeal.setPrinAmt(finAssetIntInfo.getTradeAmt());
//    日期与交易日一致
        trdProductDeal.setSettleDate(trdProductDeal.getTradeDate());
//    利息总额 分红金额
        trdProductDeal.setIntAmt(finAssetIntInfo.getTradeAmt());
        if(StringUtil.equals(entity.getType(), "02")||StringUtil.equals(entity.getType(), "03")){
            trdProductDeal.setFpriceAmt(finAssetIntInfo.getTradeAmt());
            trdProductDeal.setTradeAmt(finAssetIntInfo.getFpriceAmt());
            trdProductDeal.setFeeAmtPay(finAssetIntInfo.getFeeAmtPay());
        }
        trdProductDeal.setSecManageAcctId("999999");
        trdProductDeal.setChlId(entity.getChlId());
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
    private PaTrdFinProductDealTotal gen汇总扩展业务流程(TrdProjectNetDurationDto entity,
                                                 TrdFinProductDealTotal trdFinProductDealTotal) {
        PaTrdFinProductDealTotal paTrdTotal = new PaTrdFinProductDealTotal();
        paTrdTotal.setTotleTradeId(entity.getTotleTradeId());
//        非货币基金分红除权
        if (StringUtil.equals(entity.getType(), "02")) {
            paTrdTotal.setPaBussType(DictCodeListExt3.平安业务类型_权益类资产_现金分红.getValue());
            paTrdTotal.setRegDate(entity.getRegistDate());
            paTrdTotal.setBonusCfmDate(StringUtil.isNotEmpty(entity.getArriveDate())?entity.getArriveDate():entity.getReInvesDate());
        }
//        非货币基金现金分红
        else if (StringUtil.equals(entity.getType(), "03")) {
            paTrdTotal.setPaBussType(DictCodeListExt3.平安业务类型_权益类资产_现金分红.getValue());
            paTrdTotal.setRegDate(entity.getRegistDate());
        }
//        非货币基金红利再投
        else if (StringUtil.equals(entity.getType(), "04")) {
            //paTrdTotal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_净值型_红利再投.getValue());
            paTrdTotal.setRegDate(entity.getRegistDate());
        }
//        分红类型
        paTrdTotal.setPayType(entity.getBonusMode());
        paTrdTotal.setWorkflowId(entity.getWorkflowId());
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
    private TrdFinProductDealTotal gen汇总流程表(TrdProjectNetDurationDto entity) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getTotleTradeId());
//      交易编号
        trdFinProductDealTotal.setBusiType(DictCodeListExt3.共通业务类型_权益类资产存续期.getValue());
//      金融产品代码
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());

        trdFinProductDealTotal.setRemark(entity.getRemark());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
//    TODO
//    trdFinProductDealTotal.setFinprodType(finprodType);
        // 金融产品类型
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue());
//      币种
        trdFinProductDealTotal.setCcy(entity.getCcy());
//      货币基金红利再投
        if (StringUtil.equals(entity.getType(), "01")) {
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_红利再投.getValue());
//         红利再投日
            trdFinProductDealTotal.setTradeDate(entity.getReInvesDate());
//          交易份额    画面.分红金额
            trdFinProductDealTotal.setShareAmt(entity.getQty());
        }
//      非货币基金分红除权
        else if (StringUtil.equals(entity.getType(), "02")||StringUtil.equals(entity.getType(), "05")) {
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_除权登记.getValue());
//          除权日
            trdFinProductDealTotal.setTradeDate(entity.getExRightsDate());
//          交易本金 ->权益登记日份额
            trdFinProductDealTotal.setPrinAmt(entity.getRegistShare());
//          单位净值 单位分红净值
            trdFinProductDealTotal.setUnitCprice(entity.getUnitCprice());
            // 交易金额
            trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());
        }
//      非货基 现金分红
        else if (StringUtil.equals(entity.getType(), "03")||StringUtil.equals(entity.getType(), "06")) {
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_现金分红.getValue());
//          交易金额 分红金额
            trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());
            trdFinProductDealTotal.setTradeDate(entity.getArriveDate());
            // 单位净值 单位分红净值
            trdFinProductDealTotal.setUnitCprice(entity.getUnitCprice());
            trdFinProductDealTotal.setbTradeId(entity.getbTradeId());
        }
//      非货基 红利再投
        else if (StringUtil.equals(entity.getType(), "04")) {
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_红利再投.getValue());
            // 单位净值 单位分红净值
            trdFinProductDealTotal.setUnitCprice(entity.getUnitCprice());
            trdFinProductDealTotal.setTradeDate(entity.getReInvesDate());
//          交易份额
            trdFinProductDealTotal.setShareAmt(entity.getQty());
            trdFinProductDealTotal.setbTradeId(entity.getbTradeId());
        }
        trdFinProductDealTotal.setPrinAmt(entity.getRegistShare());
//    日期与交易日一致
        trdFinProductDealTotal.setSettleDate(trdFinProductDealTotal.getTradeDate());
//    利息总额 分红金额
        trdFinProductDealTotal.setIntAmt(entity.getTradeAmt());
        if(StringUtil.equals(entity.getType(), "02")||StringUtil.equals(entity.getType(), "03")){
            //到账金额(即交易金额)
            BigDecimal tradeAmt = BigDecimal.ZERO;
            //分红金额
            BigDecimal fpriceAmt = BigDecimal.ZERO;
            //分红份额
            BigDecimal qty = BigDecimal.ZERO;
            if(null!=entity.getFinAssetIntInfoList()&&!entity.getFinAssetIntInfoList().isEmpty()){
                for(FinAssetIntInfo finAssetIntInfo : entity.getFinAssetIntInfoList()){
                    tradeAmt = ArithmeticUtil.doAdd(finAssetIntInfo.getFpriceAmt(),tradeAmt);
                    fpriceAmt = ArithmeticUtil.doAdd(finAssetIntInfo.getTradeAmt(),fpriceAmt);
                    qty = ArithmeticUtil.doAdd(finAssetIntInfo.getRegistShare(),qty);
                }
            }
            trdFinProductDealTotal.setFpriceAmt(fpriceAmt);
            trdFinProductDealTotal.setTradeAmt(tradeAmt);
            trdFinProductDealTotal.setFeeAmtPay(entity.getFeeAmtPay());
            trdFinProductDealTotal.setShareAmt(qty);
            //    利息总额 分红金额
            trdFinProductDealTotal.setIntAmt(trdFinProductDealTotal.getTradeAmt());
        }

        trdFinProductDealTotal.setChlId(entity.getChlId());
        return trdFinProductDealTotal;
    }

    private List<TrdTradeFeeDetail>  生成组合交易费用明细(TrdProjectNetDurationDto entity, FinAssetIntInfo finAssetIntInfo, TrdProductDeal deal) {
        List<TrdTradeFeeDetail> trdTradeFeeDetailList = new ArrayList<>();
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        trdTradeFeeDetail.setTradeId(deal.getTradeId());
        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
        trdTradeFeeDetail.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        trdTradeFeeDetail.setFeeAmt(finAssetIntInfo.getFeeAmtPay());
        trdTradeFeeDetailList.add(trdTradeFeeDetail);
        return trdTradeFeeDetailList;
    }
}
