package com.joyintech.tams.invest.trade.transfer.lendtransfer.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseFinProductCashInputDto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;
import com.joyintech.tams.invest.trade.transfer.lendtransfer.dto.LendTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.lendtransfer.dto.LendTransfer002Dto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 拆借资产转仓Service
 * @author liuwei
 * @version 1.0 2020年02月18日
 * @see LendTransfer001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LendTransfer001Service extends CommonExportService<LendTransfer001Dto>
        implements BaseProcessService<LendTransfer001Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(LendTransfer001Service.class);

    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;
    /**
     * 划款指令生成
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * 金融产品交易Service
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    /**
     * 金融产品交易业务流程表Mapper
     */
    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;
    /**
     * 金融产品交易业务附属流程表Mapper
     */
    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;
    /**
     * 划款指令业务流程表Mapper
     */
    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;
    /**
     * 金融产品交易扩展业务流程表Mapper
     */
    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;
    /**
     * 金融产品交易其它业务流程表Mapper
     */
    @Autowired
    private WflTrdProductDealOthMapper wflTrdProductDealOthMapper;

    @Autowired(required = false)
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired(required = false)
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired(required = false)
    private PtlTradeMapper ptlTradeMapper;

    @Autowired(required = false)
    private FinProductTypeMapper finProductTypeMapper;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 平台日期组件接口
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 获取转仓信息—组合代码
     * @return 联想下拉框实体类集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortfolioId(Map<String,Object> map){
        return simpleRestTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/LendTransfer/getPortfolioId",
                map, SelectOptionDto.class);
    }

    /**
     * 获取转仓基本信息
     * @return 交易信息
     * @see
     * @since 1.0
     */
    public LendTransfer001Dto getTradeInfo(String tradeId){
        return simpleRestTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/LendTransfer/getTradeInfo?tradeId="+tradeId,
                LendTransfer001Dto.class);
    }

    /**
     * 获取转仓dto
     * @return 转仓dto
     * @see
     * @since 1.0
     */
    public void genFinProductDto(LendTransfer002Dto dto, LendTransfer001Dto entity){
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
//        entity.setIntRate(ArithmeticUtil.divide(entity.getIntRate(),new BigDecimal(100)));
        BaseTradeProductDealDto dto1 = genBaseTradeProductDealDto(dto,entity, DictCodeList.交易方向_买入.getValue());
        baseTradeProductDealDtos.add(dto1);
        BaseTradeProductDealDto dto2 = genBaseTradeProductDealDto(dto,entity, DictCodeList.交易方向_卖出.getValue());
        dto2.getTrdProductDeal().setrTradeId(dto1.getTrdProductDeal().getTradeId());
        dto2.getTrdProductDeal().setSplitTradeId(dto1.getTrdProductDeal().getTradeId());
        baseTradeProductDealDtos.add(dto2);
        finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        dto.setFinProductTradeDto(finProductTradeDto);
        //风险试算
        if(!StringUtil.isEmptyList(entity.getRiskTrialRetDto())){
            for (RiskTrialRetDto riskTrialRetDto:entity.getRiskTrialRetDto()) {
                if(StringUtil.equals(riskTrialRetDto.getPortfolioId(),dto.getPortfolioId())){
                    dto.setRiskTrialRetDto(null);
                    List<RiskTrialRetDto> riskTrialRetDtos = new ArrayList<>();
                    riskTrialRetDtos.add(riskTrialRetDto);
                    dto.setRiskTrialRetDto(riskTrialRetDtos);
                }
            }
        }
    }

    /**
     * 获取转仓产品对应买入或卖出交易dto
     * @return 转仓dto
     * @see
     * @since 1.0
     */
    public BaseTradeProductDealDto genBaseTradeProductDealDto(LendTransfer002Dto dto,LendTransfer001Dto entity,String ps){
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        //组合代码转为母组合代码
        dto.setActPortfolioId(dto.getPortfolioId());
        dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));
        entity.setActPortfolioId(entity.getPortfolioIdOut());
        entity.setPortfolioIdOut(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioIdOut()));
        //金融产品交易表
        TrdProductDeal trdProductDeal = genTrdProductDeal(dto,entity,ps);
        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
        //金融产品交易附属表
        TrdProductDealAdd trdProductDealAdd = genTrdProductDealAdd(dto,entity,ps);
        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
        baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);
        //金融产品交易扩展
        PaTrdProductDeal paTrdProductDeal = genPaTrdProductDeal(dto,entity,ps);
        paTrdProductDeal.setTradeId(trdProductDeal.getTradeId());
        baseTradeProductDealDto.setPaTrdProductDeal(paTrdProductDeal);
        //组合交易表
        List<PtlTrade> ptlTrades = genPtlTrades(dto,entity,ps,trdProductDeal.getTradeId());
        baseTradeProductDealDto.setPtlTrade(ptlTrades);
        //划款指令
        gen划款指令_BY_交易(baseTradeProductDealDto, DictCodeListExt1.指令状态_新建.getValue());
        return baseTradeProductDealDto;
    }

    /**
     * 获取金融产品交易表dto
     * @return 金融产品交易表dto
     * @see
     * @since 1.0
     */
    public TrdProductDeal genTrdProductDeal(LendTransfer002Dto dto,LendTransfer001Dto entity,String ps){
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        if(StringUtil.isNotEmpty(entity.getTradeId())){
            if (baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                if (StringUtil.equals(entity.getPs(),DictCodeList.回购拆借方向_拆入.getValue())) {
                    if (StringUtil.equals(entity.getPaBussType(),DictCodeList.拆借类型_特殊目的载体.getValue())) {
                        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_特拆交易);
                    }
                    else {
                        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_拆入交易);
                    }
                }
                else {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_拆出交易);
                }
                trdProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            }
            else {
                trdProductDeal.setTradeId(IDGenerator.generate());
            }
            trdProductDeal.setBusiType(DictCodeList.共通业务类型_拆借投资交易.getValue());
            // 金融产品类型（投管分类）
            if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_拆入.getValue())) {
                if (StringUtil.equals(entity.getPaBussType(), DictCodeList.拆借类型_特殊目的载体.getValue())) {
                    trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue());
                }
                else {
                    trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
                }
            }
            else {
                trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
            }
            // 分支序号
            String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
            trdProductDeal.setBranch(Long.parseLong(branch));
            trdProductDeal.setCpriceAmt(dto.getPrinAmt()); // 净价总额
            trdProductDeal.setCcy(entity.getCcy()); // 交易币种
            trdProductDeal.setPs(ps); // 交易方向
            trdProductDeal.setVdate(entity.getVdate()); // 起息日
            trdProductDeal.setMdate(entity.getMdate()); // 到期日
            trdProductDeal.setSettleDate(dto.getTradeDate()); // 交割日
            trdProductDeal.setShareAmt(dto.getPrinAmt()); // 交易份额
            trdProductDeal.setPrinAmt(dto.getPrinAmt()); // 交易本金
            trdProductDeal.setTradeAmt(dto.getTradeAmt()); // 交易金额
            trdProductDeal.setIntAmt(ArithmeticUtil.doSub(dto.getTradeAmt(),dto.getPrinAmt()));
            trdProductDeal.setIsClean(DictCodeList.是否_是.getValue()); // 是否结清
            trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
            trdProductDeal.setTradeDate(DateUtil.parse(DateUtil.format(entity.getTradeDate())));
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            if(ps.equals(DictCodeList.交易方向_买入.getValue())){
                trdProductDeal.setCounterId(entity.getPortfolioIdOut());
                trdProductDeal.setRemark("拆借资产转入");
                trdProductDeal.setFinprodId(trdProductDeal.getTradeId());
            }
            else {
                trdProductDeal.setCounterId(dto.getPortfolioId());
                trdProductDeal.setRemark("拆借资产转出");
                trdProductDeal.setFinprodId(entity.getTradeId());
            }
            trdProductDeal.setIsOutTrade(DictCodeList.是否_否.getValue());
            trdProductDeal.setTrader(trdProductDeal.getCreateUser());
            trdProductDeal.setIsSwapTransaction(DictCodeList.是否_是.getValue());

        }
        return trdProductDeal;
    }

    /**
     * 获取金融产品交易附属表dto
     * @return 金融产品交易附属表dto
     * @see
     * @since 1.0
     */
    public TrdProductDealAdd genTrdProductDealAdd(LendTransfer002Dto dto, LendTransfer001Dto entity, String ps){
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        if(ps.equals(DictCodeList.交易方向_买入.getValue())){
            trdProductDealAdd.setPortfolioId(dto.getPortfolioId());
        }
        else {
            trdProductDealAdd.setPortfolioId(entity.getPortfolioIdOut());
        }
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        return trdProductDealAdd;
    }

    /**
     * 获取金融产品交易扩展表dto
     * @return 金融产品交易附属表dto
     * @see
     * @since 1.0
     */
    public PaTrdProductDeal genPaTrdProductDeal(LendTransfer002Dto dto, LendTransfer001Dto entity, String ps){
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        // 平安业务类型
        if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_拆入.getValue())) {
            if (StringUtil.equals(entity.getPaBussType(), DictCodeList.拆借类型_特殊目的载体.getValue())) {
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_特拆拆入.getValue());
            }
            else {
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_普通拆入.getValue());
            }
        }
        else {
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_普通拆出.getValue());
        }
        paTrdProductDeal.setBackLendDirection(entity.getPs());
        paTrdProductDeal.setDeptCode(paTrdProductDeal.getCreateDept()); // 交易所属部门
        return paTrdProductDeal;
    }

    /**
     * 获取组合交易表list
     * @return 组合交易表list
     * @see
     * @since 1.0
     */
    public List<PtlTrade> genPtlTrades(LendTransfer002Dto dto, LendTransfer001Dto entity, String ps,String tradeId){
        List<PtlTrade> list = new ArrayList<>();
        if(ps.equals(DictCodeList.交易方向_买入.getValue())) {
            PtlTrade ptlTrade = genPtlTrade(dto.getPortfolioId(),entity,ps,tradeId);
            list.add(ptlTrade);
            if(!dto.getPortfolioId().equals(dto.getActPortfolioId())){
                PtlTrade actPtlTrade = genPtlTrade(dto.getActPortfolioId(),entity,ps,tradeId);
                list.add(actPtlTrade);
            }
        }
        else {
            PtlTrade ptlTrade = genPtlTrade(entity.getPortfolioIdOut(),entity,ps,tradeId);
            list.add(ptlTrade);
            if(!entity.getPortfolioIdOut().equals(entity.getActPortfolioId())){
                PtlTrade actPtlTrade = genPtlTrade(entity.getActPortfolioId(),entity,ps,tradeId);
                list.add(actPtlTrade);
            }
        }
        return list;
    }

    /**
     * 获取组合交易表dto
     * @return 组合交易表dto
     * @see
     * @since 1.0
     */
    public PtlTrade genPtlTrade(String portfolioId, LendTransfer001Dto entity, String ps,String tradeId){
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(portfolioId); // 组合代码
        ptlTrade.setBusiNo(tradeId); // 业务编号
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
        ptlTrade.setBusiId(tradeId); // 业务明细代码
        ptlTrade.setBusiType(DictCodeList.共通业务类型_拆借投资交易.getValue()); // 业务类型
        ptlTrade.setSettleDate(entity.getTradeDate()); // 实际发生日
        return ptlTrade;
    }

    /**
     * 获取划款指令list
     * @return 划款指令list
     * @see
     * @since 1.0
     */
    public void gen划款指令_BY_交易(BaseTradeProductDealDto baseTradeProductDealDto,String mode) {
        BussSettleOrderParameterDto param = new BussSettleOrderParameterDto();
        param.setIsTotalTrade(DictCodeList.是否_否.getValue());
        param.setPortfolioId(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId());
        param.setDeal(baseTradeProductDealDto.getTrdProductDeal());
        if(bussTrdTransferOrderService != null) {
            List<BussTrdTransferOrderDto> bussTrdTransferOrderDtos = Arrays.asList(bussTrdTransferOrderService.generateOrder(param));
            if (bussTrdTransferOrderDtos != null) {
                for (BussTrdTransferOrderDto ret : bussTrdTransferOrderDtos) {
                    if (StringUtil.equals(OperationType.REVOKE.toString(), mode)) {
                        ret.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                    }
                    if (StringUtil.equals(ret.getPs(), DictCodeListExt1.收支方向_支出.getValue())) {
                        ret.setMyAcctNo(ret.getPayAcctNo());
                        ret.setCounterAcctNo(ret.getRecAcctNo());
                    } else {
                        ret.setMyAcctNo(ret.getRecAcctNo());
                        ret.setCounterAcctNo(ret.getPayAcctNo());
                    }
                    if (StringUtil.isNotEmpty(ret.getIsNeedSend())) {
                        if (DictCodeList.是否_是.getCodeName().equals(ret.getIsNeedSend())) {
                            ret.setIsNeedSend(DictCodeList.是否_是.getValue());
                        } else if (DictCodeList.是否_否.getCodeName().equals(ret.getIsNeedSend())) {
                            ret.setIsNeedSend(DictCodeList.是否_否.getValue());
                        }
                    }
                }
                StlPayOrdersDto stl = gen划款指令流程(bussTrdTransferOrderDtos, baseTradeProductDealDto.getTrdProductDealAdd(), null);
                if (!StringUtil.isNotEmpty(stl.getSettleDate())) {
                    stl.setSettleDate(baseTradeProductDealDto.getTrdProductDeal().getSettleDate());
                }
                baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stl));
            }
        }
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
    public StlPayOrdersDto gen划款指令流程(List<BussTrdTransferOrderDto> stlPayOrdersList, TrdProductDealAdd dealAdd,
                                     String workflowId) {
        StlPayOrdersDto stlPayOrders = null;
        for (BussTrdTransferOrderDto dto : stlPayOrdersList) {
            if (dto.getPortfolioId().equals(dealAdd.getPortfolioId())) {
                stlPayOrders = dto;
                stlPayOrders.setOrderStatus(DictCodeList.清算操作类型_新建.getValue());
                stlPayOrders.setTradeId(dealAdd.getTradeId());
                stlPayOrders.setWorkflowId(workflowId);
            }
        }
        return stlPayOrders;
    }

    /**
     * 新建界面提交时插入持仓流水 {@inheritDoc}
     */
    @Override
    public void doInsert(LendTransfer001Dto entity, OperationType type) {
        if (entity.getOperateType().equals("submit")) {
            beforeComplete(entity);
        }
    }

    /**
     * 保存中进行提交时进行性插入持仓流水 {@inheritDoc}
     */
    @Override
    public void doUpdate(LendTransfer001Dto entity) {
        if (entity.getOperateType().equals("submit")) {
            doDelete(entity.getWorkflowId());
            if(!StringUtil.isEmptyList(entity.getLendTransfer002DtoList())) {
                entity.getLendTransfer002DtoList().forEach(lendTransfer002Dto -> {
                    genFinProductDto(lendTransfer002Dto, entity);
                });
            }
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(LendTransfer001Dto.class, true);
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
            beforeComplete(entity);
        }
    }

    /**
     * 作废操作,清空业务表 {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
    }

    /**
     * 流程 {@inheritDoc}
     */
    @Override
    public void doComplete(LendTransfer001Dto entity, OperationType type) {
        if (StringUtil.equals(entity.getOperateType(), "submit")) {
            //删除业务流程表
            baseTradeProductDealCommService.deleteWflData(entity.getWorkflowId());
            if (!StringUtil.isEmptyList(entity.getLendTransfer002DtoList())) {
                entity.getLendTransfer002DtoList().forEach(lendTransfer002Dto -> {
                    //插入组合持仓和各业务表
                    FinProductTradeDto finProductTradeDto = lendTransfer002Dto.getFinProductTradeDto();
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                    baseTradeProductDealCommService.execute(finProductTradeDto);
                    //插入组合交易表和金融产品交易扩展表
                    if(!StringUtil.isEmptyList(finProductTradeDto.getBaseTradeProductDealDtos())){
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {
                            if(!StringUtil.isEmptyList(baseTradeProductDealDto.getPtlTrade())){
                                baseTradeProductDealDto.getPtlTrade().forEach(ptlTrade -> {
                                    ptlTradeMapper.insertSelective(ptlTrade);
                                });
                            }
                            if(baseTradeProductDealDto.getPaTrdProductDeal() != null){
                                paTrdProductDealMapper.insertSelective(baseTradeProductDealDto.getPaTrdProductDeal());
                            }
                            // 所有交易生效后调用的共通方法
                            TrdProductDeal trdProductDeal = baseTradeProductDealDto.getTrdProductDeal();
                            TrdProductDealAdd trdProductDealAdd = baseTradeProductDealDto.getTrdProductDealAdd();
                            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                            inputDto.setProdid(trdProductDealAdd.getPortfolioId());
                            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                            inputDto.setTrdProductDeal(trdProductDeal);
                            inputDto.setTrdProductDealAdd(trdProductDealAdd);
                            inputDto.setTrdTradeFeeDetail(null);
//                            AllTradeCommonUtil.setDeals(inputDto, trdProductDeal, trdProductDealAdd, baseTradeProductDealDto.getPaTrdProductDeal(),
//                                    baseTradeProductDealDto.getStlPayOrdersList(), baseTradeProductDealDto.getPtlTrade(), lendTransfer002Dto.getActPortfolioId());
                            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

                            //风险试算
                            basRiskMathCommService.insert(lendTransfer002Dto.getRiskTrialRetDto(), trdProductDeal.getTradeId());

                            entity.setIntRate(ArithmeticUtil.divide(entity.getIntRate(),new BigDecimal(100),4));
                            // 7.6写入现金流相关信息表
                            this.cashFlowLogic(trdProductDeal,entity);

                            // 7.7写入交易信息进交易报送信息表，为中债报送用
                            this.dealInfoCbReportLogic_7_7(trdProductDeal,entity);

                            // 7.8写入负债(同业拆入/卖出回购)报送表，为中债报送用
                            // 资产负债报送信息Entity
                            this.assetDebtCbReportLogic_7_8_1(trdProductDeal,baseTradeProductDealDto.getPaTrdProductDeal(),entity);
                            // 负债(同业拆入/卖出回购)报送信息Entity
                            this.assetDebtCbReportLogic_7_8_2(trdProductDeal,entity);
                        });
                    }
                    //若带转出产品组合是子组合，插入对应持仓
                    if (StringUtil.isNotEmpty(entity.getPortfolioIdOut()) && !entity.getActPortfolioId().equals(
                            entity.getPortfolioIdOut())) {
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId());
                                    }
                                });
                        baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioIdOut());
                                    }
                                });
                    }
                    //若带转入产品组合是子组合，插入对应持仓
                    if (StringUtil.isNotEmpty(lendTransfer002Dto.getPortfolioId()) && !lendTransfer002Dto.getActPortfolioId().equals(
                            lendTransfer002Dto.getPortfolioId())) {
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(lendTransfer002Dto.getActPortfolioId());
                                    }
                                });
                        baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(lendTransfer002Dto.getPortfolioId());
                                    }
                                });
                    }
                });
            }
        }
    }

    private void beforeComplete(LendTransfer001Dto entity) {
        if(!StringUtil.isEmptyList(entity.getLendTransfer002DtoList())){
            entity.getLendTransfer002DtoList().forEach(lendTransfer002Dto -> {
                if(!StringUtil.isEmptyList(lendTransfer002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos())){
                    lendTransfer002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {
                        //入业务流程表以及入账
                        excute(baseTradeProductDealDto,lendTransfer002Dto,entity);
                    });
                    //入组合持仓或流水表
                    FinProductTradeDto finProductTradeDto = lendTransfer002Dto.getFinProductTradeDto();
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    baseTradeProductDealCommService.execute(finProductTradeDto);
                    //若带转出产品组合是子组合，插入对应持仓
                    if (StringUtil.isNotEmpty(entity.getPortfolioIdOut()) && !entity.getActPortfolioId().equals(
                            entity.getPortfolioIdOut())) {
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId());
                                    }
                                });
                        baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioIdOut());
                                    }
                                });
                    }
                    //若带转入产品组合是子组合，插入对应持仓
                    if (StringUtil.isNotEmpty(lendTransfer002Dto.getPortfolioId()) && !lendTransfer002Dto.getActPortfolioId().equals(
                            lendTransfer002Dto.getPortfolioId())) {
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(lendTransfer002Dto.getActPortfolioId());
                                    }
                                });
                        baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(lendTransfer002Dto.getPortfolioId());
                                    }
                                });
                    }
                }
            });
        }
    }

    /**
     * 入业务流程表以及入账
     *
     * @see
     * @since 1.0
     */
    public void excute(BaseTradeProductDealDto baseTradeProductDealDto,LendTransfer002Dto dto,LendTransfer001Dto entity){
        // 调用授信共通
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        if(baseTradeProductDealDto.getTrdProductDeal() != null) {
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, baseTradeProductDealDto.getTrdProductDeal());
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            inputDto.setTrdProductDeal(trdProductDeal);
            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
        }
        //入业务流程表
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        TrdProductDeal wTrdProductDeal = trdProductDeal;
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, wTrdProductDeal);
        wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
        wflTrdProductDeal.setIntAmt(null);
        wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);

        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        TrdProductDealAdd wTrdProductDealAdd = baseTradeProductDealDto.getTrdProductDealAdd();
        if(wTrdProductDealAdd != null) {
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, wTrdProductDealAdd);
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
            wflTrdProductDealAdd.setPortfolioId(wTrdProductDeal.getPs().equals(DictCodeList.交易方向_买入.getValue()) ?
                    dto.getActPortfolioId() : entity.getActPortfolioId());
            wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        }

        WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
        PaTrdProductDeal wPaTrdProductDeal = baseTradeProductDealDto.getPaTrdProductDeal();
        if(wPaTrdProductDeal != null) {
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, wPaTrdProductDeal);
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
        }

        // 金融产品交易业务流程其他信息Entity -> 金融产品交易业务流程其他信息表
        WflTrdProductDealOth wflTrdProductDealOth = new WflTrdProductDealOth();
        wflTrdProductDealOth.setTradeId(trdProductDeal.getTradeId()); // 交易编号
        wflTrdProductDealOth.setBasis(entity.getBasis()); // 计息基础
        wflTrdProductDealOth.setRate(entity.getIntRate()); // 利率
        wflTrdProductDealOth.setTerm(DateUtil.getBetweenDayNumber(entity.getVdate(),entity.getMdate()));// 期限(天)
        wflTrdProductDealOth.setmTradeAmt(trdProductDeal.getTradeAmt());// 到期金额
        wflTrdProductDealOth.setDlType(entity.getPaBussType());// 拆借类型
        wflTrdProductDealOth.setWorkflowId(entity.getWorkflowId()); // 工作流代码
        wflTrdProductDealOth.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflTrdProductDealOthMapper.insertSelective(wflTrdProductDealOth);

        List<StlPayOrdersDto> list = baseTradeProductDealDto.getStlPayOrdersList();
        if ( list!= null && list.size() > 0) {
            for (StlPayOrdersDto stlPayOrdersDto : list) {
                WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                if (wflStlPayOrders.getOrderId() != null) {
                    wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                }
            }
        }

        if(StringUtil.equals(trdProductDeal.getPs(),DictCodeList.交易方向_买入.getValue())) {
            //交易类金融产品表
            FinTradeProduct finTradeProduct = genFinTradeProduct(trdProductDeal, entity);
            finTradeProductMapper.insertSelective(finTradeProduct);
            //金融产品分类
            FinProductType finProductType = genFinProductType(trdProductDeal, entity);
            finTradeProduct.setCapiIncomeFeature(DictCodeList.是否_是.getValue());
            ValItemValueGenUtil.genTypeForFinProductType(finTradeProduct, finProductType);
            finProductTypeMapper.insertSelective(finProductType);
        }
    }

    /**
     * 创建交易类金融产品实体
     *
     * @see
     * @since 1.0
     */
    public FinTradeProduct genFinTradeProduct(TrdProductDeal trdProductDeal,LendTransfer001Dto entity){
        FinTradeProduct finTradeProduct = new FinTradeProduct();
        finTradeProduct.setFinprodId(trdProductDeal.getTradeId()); // 金融产品代码
        finTradeProduct.setBranch(trdProductDeal.getBranch()); // 分支序号
        finTradeProduct.setFinprodType2(trdProductDeal.getFinprodType2()); // 金融产品类型（投管分类）
        finTradeProduct.setIntType(DictCodeList.利率类型_固定利率.getValue()); // 利率类型
        finTradeProduct.setIntRate(ArithmeticUtil.divide(entity.getIntRate(),new BigDecimal(100),4)); // 固定利率
        // 本金收益特征 = 拆出：保本，拆入不存
        if (DictCodeList.回购拆借方向_拆出.getValue().equals(entity.getPs())) {
            // ※若画面联动1.7查询出的组合为保本
            finTradeProduct.setCapiIncomeFeature(DictCodeListExt1.本金收益特征_保本.getValue());
        }
        finTradeProduct.setCounterId(trdProductDeal.getCounterId()); // 交易对手
        finTradeProduct.setWorkflowId(entity.getWorkflowId()); // 工作流代码
        finTradeProduct.setPrin(trdProductDeal.getPrinAmt());
        finTradeProduct.setCcy(trdProductDeal.getCcy());
        finTradeProduct.setVdate(entity.getVdate());
        finTradeProduct.setMdate(entity.getMdate());
        finTradeProduct.setTerm(DateUtil.getBetweenDayNumber(entity.getVdate(),entity.getMdate()));
        finTradeProduct.setIntType(DictCodeList.利率类型_固定利率.getValue());
        finTradeProduct.setBasis(entity.getBasis());
        finTradeProduct.setmPrinAmt(trdProductDeal.getPrinAmt());
        //计算剩余应计利息
        Long term = DateUtil.getBetweenDayNumber(entity.getVdate(),entity.getMdate());
        BigDecimal basis = StringUtil.equals(entity.getBasis(),DictCodeList.计息基础_A_365.getValue()) ?
                new BigDecimal(365) : new BigDecimal(360);
        BigDecimal intAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(trdProductDeal.getPrinAmt(),
                finTradeProduct.getIntRate(),new BigDecimal(term)),basis);
        finTradeProduct.setmIntAmt(intAmt);
        finTradeProduct.setmTradeAmt(ArithmeticUtil.doAdd(finTradeProduct.getmPrinAmt(),intAmt));
        finTradeProduct.setoFinprodId(entity.getTradeId());
        finTradeProduct.setCalendarId(entity.getCalendarId());
        // 名义本金、名义币种、起息日、到期日、期限(天)、计息基础、交易日历、回购/拆借方向 本身就在dto中
        finTradeProduct.setTermType(commonBusinessService.getRepTermType(
                DictCodeList.回购拆借方向_拆出.getValue().equals(
                        trdProductDeal.getPs()) ? DictCodeListExt1.金融产品类型_拆出.getValue() : DictCodeListExt1.金融产品类型_拆入.getValue(),
                finTradeProduct.getTerm()));
        return finTradeProduct;
    }

    /**
     * 创建金融产品分类实体
     *
     * @see
     * @since 1.0
     */
    public FinProductType genFinProductType(TrdProductDeal trdProductDeal,LendTransfer001Dto entity){
        FinProductType finProductTypeKey = new FinProductType();
        finProductTypeKey.setFinprodId(entity.getTradeId());
        finProductTypeKey.setBranch(trdProductDeal.getBranch());
        FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(finProductTypeKey);
        finProductType.setFinprodId(trdProductDeal.getTradeId()); // 金融产品代码
//        finProductType.setBranch(trdProductDeal.getBranch()); // 分支序号
//        finProductType.setType6(entity.getPaBussType()); // 分类6
//        finProductType.setType7(entity.getPs());
//        finProductType.setType8(DictCodeList.平安资产类型_拆借.getValue());
//        FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
//        finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_无标的投管资产类型映射行内资产分类.getValue());
//        finProductTypeConfig.setInputValue1(DictCodeList.平安资产类型_拆借.getValue());
//        finProductTypeConfig.setInputValue2(entity.getPs());
//        finProductTypeConfig = simpleRestTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
//                finProductTypeConfig, FinProductTypeConfig.class);
//        finProductType.setType9(finProductTypeConfig.getOutputValue2());
//        finProductType.setType10(finProductTypeConfig.getOutputValue3());
//        finProductType.setType11(finProductTypeConfig.getOutputValue4());
//        finProductType.setType12(finProductTypeConfig.getOutputValue5());
        finProductType.setWorkflowId(entity.getWorkflowId()); // 工作流代码
        return finProductType;
    }

    /**
     * 现金流相关逻辑
     *
     * @param dto 参数
     * @see
     * @since 1.0
     */
    private void cashFlowLogic(TrdProductDeal dto,LendTransfer001Dto entity) {
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);

        BaseFinProductCashInputDto inputDto = new BaseFinProductCashInputDto();

        // List<现金流信息Entity>
        List<FinCashInfo> finCashInfoList = new ArrayList<>();
        // 现金流信息Entity-本金
        FinCashInfo finCashInfoPrin = new FinCashInfo();
        finCashInfoPrin.setCashId(commonBusinessService.generateCashFlowCode(dto.getTradeId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong, DictCodeListExt1.现金流二级类型_本金.getValue()));
        finCashInfoPrin.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        finCashInfoPrin.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
        finCashInfoPrin.setCcy(dto.getCcy());
        finCashInfoPrin.setFinprodId(dto.getTradeId());
        finCashInfoPrin.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        finCashInfoPrin.setBranch(branchLong);
        finCashInfoPrin.setWorkflowId(entity.getWorkflowId()); // 工作流代码
        finCashInfoList.add(finCashInfoPrin);

        // 现金流信息Entity-利息
        FinCashInfo finCashInfoInt = new FinCashInfo();
        finCashInfoInt.setCashId(commonBusinessService.generateCashFlowCode(dto.getTradeId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong, DictCodeListExt1.现金流二级类型_利息.getValue()));
        finCashInfoInt.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        finCashInfoInt.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
        finCashInfoInt.setCcy(dto.getCcy());
        finCashInfoInt.setFinprodId(dto.getTradeId());
        finCashInfoInt.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        finCashInfoInt.setBranch(branchLong);
        finCashInfoInt.setWorkflowId(entity.getWorkflowId()); // 工作流代码
        finCashInfoList.add(finCashInfoInt);

        inputDto.setFinCashInfoList(finCashInfoList);

        // List<现金流支付规则表Entity>
        List<FinCashPayRule> finCashPayRuleList = new ArrayList<>();

        // 现金流支付规则表Entity-利息
        FinCashPayRule finCashPayRule = new FinCashPayRule();
        finCashPayRule.setCashId(finCashInfoInt.getCashId());
        finCashPayRule.setEffDate(dto.getVdate());

        // 一体化对应etliu start
        // finCashPayRule.setPayCycle(DictCodeList.支付频率_付息频率_到期支付.getValue());
        finCashPayRule.setPayCycle(DictCodeList.频率_到期一次.getValue());
        // 一体化对应etliu end

        // 一体化对应etliu
        // finCashPayRule.setCalcRule(DictCodeList.推算方法_起息日向后.getValue());
        finCashPayRule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());

        finCashPayRule.setFirstPayDate(dto.getMdate());
        finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        finCashPayRule.setFinprodId(dto.getTradeId());
        finCashPayRule.setFinprodType(dto.getFinprodType());
        finCashPayRule.setFinprodType2(dto.getFinprodType2());
        finCashPayRule.setBranch(branchLong);
        finCashPayRule.setWorkflowId(entity.getWorkflowId()); // 工作流代码
        finCashPayRuleList.add(finCashPayRule);

        inputDto.setFinCashPayRuleList(finCashPayRuleList);

        // List<现金流计算规则表Entity>
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();

        // 现金流计算规则表Entity-利息
        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
        finCashCalcRule.setCashId(finCashInfoInt.getCashId());
        finCashCalcRule.setEffDate(dto.getVdate()); // 生效日期
        // 一体化对应etliu
        // finCashCalcRule.setRelaeseDate(dto.getVdate()); // 公布日期
        finCashCalcRule.setCalcType(DictCodeList.利率类型_固定利率.getValue());
        finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
        finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
        finCashCalcRule.setBasis(entity.getBasis());
        finCashCalcRule.setYield(entity.getIntRate());
        finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
        finCashCalcRule.setFinprodId(dto.getTradeId()); // 金融产品代码
        finCashCalcRule.setFinprodType(dto.getFinprodType());
        finCashCalcRule.setFinprodType2(dto.getFinprodType2());
        finCashCalcRule.setBranch(branchLong);
        finCashCalcRule.setWorkflowId(entity.getWorkflowId()); // 工作流代码
        finCashCalcRuleList.add(finCashCalcRule);

        inputDto.setFinCashCalcRuleList(finCashCalcRuleList);

        // List<现金流利率信息表Entity>
        List<FinRateInfo> finRateInfoList = new ArrayList<>();

        // 现金流利率信息表Entity-利息
        FinRateInfo finRateInfo = new FinRateInfo();
        finRateInfo.setCashId(finCashInfoInt.getCashId()); // 现金流代码
        finRateInfo.setEffDate(dto.getVdate());
        // 一体化对应etliu
        // finRateInfo.setRateType(DictCodeList.利率类型_固定利率.getValue());
        finRateInfo.setIntType(DictCodeList.利率类型_固定利率.getValue());
        finRateInfo.setBasis(entity.getBasis());
        finRateInfo.setRate(finCashCalcRule.getYield());
        finRateInfo.setFinprodId(dto.getTradeId()); // 金融产品代码
        finRateInfo.setFinprodType(dto.getFinprodType());
        finRateInfo.setFinprodType2(dto.getFinprodType2());
        finRateInfo.setBranch(branchLong);
        finRateInfo.setWorkflowId(entity.getWorkflowId()); // 工作流代码
        finRateInfoList.add(finRateInfo);

        inputDto.setFinRateInfoList(finRateInfoList);

        // List<现金流计划表Entity>
        List<FinCashPlan> finCashPlanList = new ArrayList<>();

        // 现金流计划表Entity 到期还本
        FinCashPlan finCashPlan = new FinCashPlan();
        finCashPlan.setCashId(finCashInfoPrin.getCashId());
        finCashPlan.setCashNum(1L);
        finCashPlan.setCashType(DictCodeList.现金流类型_到期还本.getValue());
        finCashPlan.setVdateUnadjust(dto.getVdate());
        finCashPlan.setMdateUnadjust(dto.getMdate());
        finCashPlan.setPayDateUnadjust(dto.getMdate());
        finCashPlan.setVdate(dto.getVdate());
        finCashPlan.setMdate(dto.getMdate());
        finCashPlan.setPayDate(dto.getMdate());
        finCashPlan.setTermdays(DateUtil.getBetweenDayNumber(dto.getVdate(), dto.getMdate()) + 1);
        finCashPlan.setPrinAmt(dto.getPrinAmt());
        finCashPlan.setIntPrinAmt(dto.getPrinAmt());
        finCashPlan.setIntAmt(BigDecimal.ZERO);
        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        finCashPlan.setCashAmt(dto.getPrinAmt());
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        finCashPlan.setFinprodId(dto.getTradeId());
        finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
        finCashPlan.setFinprodType(dto.getFinprodType());
        finCashPlan.setFinprodType2(dto.getFinprodType2());
        finCashPlan.setBranch(branchLong);
        finCashPlan.setWorkflowId(entity.getWorkflowId()); // 工作流代码
        finCashPlanList.add(finCashPlan);

        // 现金流计划表Entity 到期付息
        finCashPlan = new FinCashPlan();
        finCashPlan.setCashId(finCashInfoInt.getCashId());
        finCashPlan.setCashNum(2L);
        finCashPlan.setCashType(DictCodeList.现金流类型_到期收息.getValue());
        finCashPlan.setVdateUnadjust(dto.getVdate());
        finCashPlan.setMdateUnadjust(dto.getMdate());
        finCashPlan.setPayDateUnadjust(dto.getMdate());
        finCashPlan.setVdate(dto.getVdate());
        finCashPlan.setMdate(dto.getMdate());
        finCashPlan.setPayDate(dto.getMdate());
        finCashPlan.setTermdays(DateUtil.getBetweenDayNumber(dto.getVdate(), dto.getMdate()) + 1);
        finCashPlan.setPrinAmt(BigDecimal.ZERO);
        finCashPlan.setIntPrinAmt(dto.getPrinAmt());

        // 计划支付利息
        // ※调用业务组件.【平安银行算法组件.投资交易回购利息】，拆借算法同回购一样，可参考回购利息算法
        finCashPlan.setIntAmt(dto.getIntAmt());

        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        finCashPlan.setCashAmt(finCashPlan.getIntAmt());
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        finCashPlan.setFinprodId(dto.getTradeId());
        finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
        finCashPlan.setFinprodType(dto.getFinprodType());
        finCashPlan.setFinprodType2(dto.getFinprodType2());
        finCashPlan.setBranch(branchLong);
        finCashPlan.setWorkflowId(entity.getWorkflowId()); // 工作流代码
        finCashPlanList.add(finCashPlan);

        inputDto.setFinCashPlanList(finCashPlanList);

        // 调用共通登录现金流相关数据
        simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertFinProductCashData",
                inputDto, Boolean.class);
    }

    /**
     * 7.7写入交易信息进交易报送信息表，为中债报送用
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void dealInfoCbReportLogic_7_7(TrdProductDeal dto,LendTransfer001Dto entity) {
        // 7.7写入交易信息进交易报送信息表，为中债报送用
        // 1)查询产品登记编码
        String prodRegistCode = simpleRestTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectProdRegistCode/" + entity.getPortfolioIdOut(), null,
                String.class);

        if (StringUtil.isBlank(prodRegistCode)) {
            return;
        }

        // 2）写入交易报送信息
        // 交易报送信息Entity
        RepTrade repTrade = new RepTrade();
        repTrade.setRepFinprodRegId(prodRegistCode); // 产品登记编码
        repTrade.setRepBankTradeCode(dto.getTradeId()); // 行内交易编码
        repTrade.setRepBankAssetCode(dto.getTradeId()); // 行内资产/负债编码
        repTrade.setRepCcy(dto.getCcy()); // 币种
        repTrade.setRepOccurMoney(dto.getPrinAmt()); // 发生金额

        // 折算人民币金额（元） = 画面.本金金额*收盘价(利用画面.币种与交易日查询汇率行情表中最新的收盘价)
        BigDecimal 收盘价 = commonBusinessService.selectClosePrice(dto.getCcy(), DateUtil.format(dto.getTradeDate()));
        BigDecimal 折算人民币金额 = ArithmeticUtil.multiply(dto.getPrinAmt(), 收盘价);
        repTrade.setRepMoneyCny(折算人民币金额);

        // 资金流动类型: 数据字典缓存.资金流动类型 ※根据交易方向判断，若拆入，即拆入和正回购首期，若拆出，即为拆出和逆回购首期
        String 资金流动类型 = DictCodeListExt2.资金流动类型_同业拆入和卖出回购首期.getValue();
        if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_拆出.getValue())) {
            资金流动类型 = DictCodeListExt2.资金流动类型_拆放同业和买入返售首期.getValue();
        }
        repTrade.setRepMoneyFlowType(资金流动类型);
        repTrade.setRepDelCode(dto.getTradeId()); // 成交编号/合同号
        repTrade.setRepCount(dto.getPrinAmt()); // 数量

        repTrade.setRepTradeDate(dto.getTradeDate()); // 交易日
        repTrade.setRepTradeComponent(StringUtil.isNotEmpty(dto.getCounterId())?dto.getCounterId():""); // 交易对手方
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue()); // 报送状态
        repTrade.setRepStatusDate(iPlatformDate.getSysDate()); // 数据日期
        repTrade.setRepRemark(dto.getRemark()); // 备注

        // 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
        simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepTrade",
                Arrays.asList(repTrade), Boolean.class);
    }

    /**
     * 调用资产负债信息报送组件 写入资产负债报送信息
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void assetDebtCbReportLogic_7_8_1(TrdProductDeal dto,PaTrdProductDeal paTrdProductDeal,LendTransfer001Dto entity) {
        // 资产负债报送信息Entity
        RepAssetDept repAssetDept = new RepAssetDept();
        repAssetDept.setRepFinprodId(dto.getTradeId()); // 金融产品代码
        repAssetDept.setFinprodMarketId(dto.getTradeId()); // 市场代码
        // 发行机构代码
        String 发行机构代码 = CacheUtil.getSysParam(BussinessConstant.ISSUE_PARTY_ID);
        repAssetDept.setRepIssuePartyId(发行机构代码);
        repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_同业拆入.getValue());
        // 资产/负债类别
        if (StringUtil.equals(entity.getPaBussType(), DictCodeList.拆借类型_普通拆借.getValue())
                && StringUtil.equals(paTrdProductDeal.getBackLendDirection(), DictCodeList.回购拆借方向_拆出.getValue())) {
            repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_拆放同业.getValue());
        }
        repAssetDept.setRepCcy(dto.getCcy());// 币种
        repAssetDept.setRepSendStatus(DictCodeList.是否_是.getValue()); // 报送状态
        // 交易流通场所
        repAssetDept.setRepTradeMarket(DictCodeList.交易流通场所_全国银行间同业拆借市场.getValue());
        // 调用资产负债信息报送组件
        simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepAssetDebt",
                Arrays.asList(repAssetDept), Boolean.class);
    }

    /**
     * 调用负债(同业拆入卖出回购)报送信息组件 写入负债(同业拆入卖出回购)报送信息
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void assetDebtCbReportLogic_7_8_2(TrdProductDeal dto,LendTransfer001Dto entity) {
        // 拆入、正回购 -> 负债(拆入或正回购)类要素登记
        if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_拆入.getValue())) {
            // 负债(同业拆入/卖出回购)报送信息Entity
            RepLiabilities repLiabilities = new RepLiabilities();
            repLiabilities.setRepFinprodId(dto.getTradeId()); // 金融产品代码
            repLiabilities.setRepValueDate(dto.getVdate()); // 起息日
            repLiabilities.setRepExpiringDate(dto.getMdate()); // 到期日
            repLiabilities.setRepCounterparty(dto.getCounterId()); // 对手方
            repLiabilities.setRepAnnualInterestRate(entity.getIntRate().multiply(new BigDecimal(100))); // 年利率%
            repLiabilities.setRepBasis(entity.getBasis()); // 计息基础
            repLiabilities.setRepRemark(dto.getRemark()); // 备注

            // 调用负债(同业拆入卖出回购)报送信息组件
            simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepLiabilities",
                    Arrays.asList(repLiabilities), Boolean.class);
        }
        // 拆出、逆回购 -> 拆放同业及买入返售
        else {
            RepAssetInterLend repAssetInterLend = new RepAssetInterLend();
            repAssetInterLend.setRepFinprodId(dto.getTradeId()); // 金融产品代码
            repAssetInterLend.setRepVdate(dto.getVdate()); // 起息日
            repAssetInterLend.setRepMdate(dto.getMdate()); // 到期日
            repAssetInterLend.setRepTradeTarget(dto.getCounterId()); // 对手方
            repAssetInterLend.setRepRateYear(entity.getIntRate().multiply(new BigDecimal(100))); // 年利率%
            repAssetInterLend.setRepBasis(entity.getBasis()); // 计息基础
            repAssetInterLend.setRepRemark(dto.getRemark()); // 备注

            // 调用负债(拆放同业及买入返售)报送信息组件
            simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepDlRepoInfo",
                    Arrays.asList(repAssetInterLend), Boolean.class);
        }
    }

    @Override
    public void doBack(LendTransfer001Dto entity) {
        if(!StringUtil.isEmptyList(entity.getLendTransfer002DtoList())){
            entity.getLendTransfer002DtoList().forEach(lendTransfer002Dto -> {
                if(!StringUtil.isEmptyList(lendTransfer002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos())){
                    lendTransfer002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {
                        String tradeId = baseTradeProductDealDto.getTrdProductDeal().getTradeId();
                        if(tradeId != null){
                            AllTradeCommonUtil.deletePosition("",tradeId);
                        }
                    });
                }
            });
        }
    }

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.MASERT)
    @Override
    public String checkForSubmit(FlowEntity entity, boolean isMutiApproval) {
        if (isMutiApproval) {
            // 业务编号
            String tradeId = entity.getTradeId();
            if (!this.multiApproveTradeCheck(tradeId)) {
                return getMessage("BUSS_COM_E_0100", tradeId);
            }
        }
        return null;
    }

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。
     * <功能详细描述>
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    public boolean multiApproveTradeCheck(String tradeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("tradeId", tradeId);
        int count = simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/DepositTransfere001/getWorkflowTradeId",
                map, Integer.class);
        if (count > 0) {
            return true;
        }
        return false;
    }
    /**
     * 获取处于流程中的持仓（但是未记录在在持仓流水中）
     * @return 处于流程中的持仓（但是未记录在在持仓流水中）
     * @see
     * @since 1.0
     */
    public BigDecimal selectWflAmtLend(Map<String,Object> map){
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/LendTransfer/selectWflAmtLend",map,
        		BigDecimal.class);
    }

}
