package com.joyintech.tams.invest.trade.transfer.netplantransfer.service;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.trade.transfer.netplantransfer.dto.NetPlanTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.netplantransfer.dto.NetPlanTransfer002Dto;
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
 *
 * 净值型计划转仓Service
 * @author liuwei
 * @version 1.0 2019年12月23日
 * @see NetPlanTransfer001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class NetPlanTransfer001Service extends CommonExportService<NetPlanTransfer001Dto>
        implements BaseProcessService<NetPlanTransfer001Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(NetPlanTransfer001Service.class);

    /**
     * 通用Mapper共同代理方法
     */
    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    /**
     * 注入业务主键生产类
     */
    @Autowired(required=false)
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
     * 组合交易表Mapper
     */
    @Autowired
    private PtlTradeMapper ptlTradeMapper;
    /**
     * 金融产品交易扩展表Mapper
     */
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    /**
     * 平台日获取接口
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 交易报送信息表
     */
    @Autowired
    private RepTradeMapper repTradeMapper;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * MultiApproveTradeCheckService
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;

    /**
     * 获取资产代码
     * @return 联想下拉框实体类集合
     * @see
     * @since 1.0
     */
    public BigDecimal getUnitValue(Map<String,Object> paraMap){
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/NetPlanTransfer001/getUnitValue",
                paraMap, BigDecimal.class);
    }

    /**
     * 获取转仓信息—组合代码
     * @return 联想下拉框实体类集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortfolioId(Map<String,Object> map){
        return simpleRestTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/NetPlanTransfer001/getPortfolioId",
                map, SelectOptionDto.class);
    }

    /**
     * 获取资产类型7
     * @return type7
     * @see
     * @since 1.0
     */
    public String selectType7(String finprodId){
        return simpleRestTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/NetPlanTransfer001/selectType7?finprodId="+finprodId, String.class);
    }

    /**
     * 获取单位净值列表
     * @return list
     * @see
     * @since 1.0
     */
    public List<TrdProductDeal> getUnitValueList(Map<String,Object> map){
    	TrdProductDeal trdProductDeal = new TrdProductDeal();
    	return simpleRestTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/NetPlanTransfer001/getUnitValueList",map, TrdProductDeal.class);
    }

    /**
     * 获取转仓dto
     * @return 转仓dto
     * @see
     * @since 1.0
     */
    public void genFinProductDto(NetPlanTransfer002Dto dto, NetPlanTransfer001Dto entity){
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        baseTradeProductDealDtos.add(genBaseTradeProductDealDto(dto,entity, DictCodeList.交易方向_买入.getValue()));
        baseTradeProductDealDtos.add(genBaseTradeProductDealDto(dto,entity, DictCodeList.交易方向_卖出.getValue()));
        finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        dto.setFinProductTradeDto(finProductTradeDto);
        //风险试算
        if(!StringUtil.isEmptyList(entity.getRiskTrialRetDto()) && !StringUtil.isEmptyList(dto.getRiskTrialRetDto())){
            for (RiskTrialRetDto riskTrialRetDto:dto.getRiskTrialRetDto()) {
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
    public BaseTradeProductDealDto genBaseTradeProductDealDto(NetPlanTransfer002Dto dto,NetPlanTransfer001Dto entity,String ps){
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
        gen划款指令_BY_交易(baseTradeProductDealDto,DictCodeListExt1.指令状态_新建.getValue());
        return baseTradeProductDealDto;
    }

    /**
     * 获取金融产品交易表dto
     * @return 金融产品交易表dto
     * @see
     * @since 1.0
     */
    public TrdProductDeal genTrdProductDeal(NetPlanTransfer002Dto dto,NetPlanTransfer001Dto entity,String ps){
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        if(StringUtil.isNotEmpty(entity.getFinprodId())){
            if (baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_净值型_交易);
                if(StringUtil.equals(entity.getType7(), DictCodeList.计划类资产二级分类_公募计划.getValue())){
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_公募计划);
                }else if(StringUtil.equals(entity.getType7(), DictCodeList.计划类资产二级分类_委托债权.getValue())){
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_委托债权);
                }else if(StringUtil.equals(entity.getType7(), DictCodeList.计划类资产二级分类_委托权益.getValue())){
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_委托权益);
                }else if(StringUtil.equals(entity.getType7(), DictCodeList.计划类资产二级分类_自主管理.getValue())){
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_自主管理);
                }
                trdProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            }
            else {
                trdProductDeal.setTradeId(IDGenerator.generate());
            }
            trdProductDeal.setBusiType(DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue());
            trdProductDeal.setFinprodId(entity.getFinprodId());
            trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
            trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
            trdProductDeal.setTradeDate(DateUtil.parse(DateUtil.format(entity.getTradeDate())));
            trdProductDeal.setSettleDate(DateUtil.parse(DateUtil.format(entity.getTradeDate())));
            trdProductDeal.setCcy(entity.getCcy());
            trdProductDeal.setUnitCprice(dto.getUnitValue());
            trdProductDeal.setSettleSpeed(Long.parseLong("0"));
            trdProductDeal.setShareAmt(dto.getTradeAmt());
            trdProductDeal.setPrinAmt(dto.getTradeMoney());
            trdProductDeal.setTradeAmt(dto.getTradeMoney());
            trdProductDeal.setPs(ps);
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            if(ps.equals(DictCodeList.交易方向_买入.getValue())){
                trdProductDeal.setCounterId(entity.getPortfolioIdOut());
                trdProductDeal.setRemark("净值型计划转入");
            }
            else {
                trdProductDeal.setCounterId(dto.getPortfolioId());
                trdProductDeal.setRemark("净值型计划转出");
            }
            trdProductDeal.setIsOutTrade(DictCodeList.是否_否.getValue());
            //TODO 交易员获取
            trdProductDeal.setTrader(trdProductDeal.getCreateUser());
            trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
            trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());

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
    public TrdProductDealAdd genTrdProductDealAdd(NetPlanTransfer002Dto dto, NetPlanTransfer001Dto entity, String ps){
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
    public PaTrdProductDeal genPaTrdProductDeal(NetPlanTransfer002Dto dto, NetPlanTransfer001Dto entity, String ps){
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        if(ps.equals(DictCodeList.交易方向_买入.getValue())){
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_净值型_申购确认.getValue());
        }
        else {
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_净值型_赎回确认.getValue());
        }
        //TODO 部门值获取
        paTrdProductDeal.setDeptCode(paTrdProductDeal.getCreateDept());
        return paTrdProductDeal;
    }

    /**
     * 获取组合交易表list
     * @return 组合交易表list
     * @see
     * @since 1.0
     */
    public List<PtlTrade> genPtlTrades(NetPlanTransfer002Dto dto, NetPlanTransfer001Dto entity, String ps,String tradeId){
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
    public PtlTrade genPtlTrade(String portfolioId, NetPlanTransfer001Dto entity, String ps,String tradeId){
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(portfolioId);
        ptlTrade.setBusiNo(tradeId);
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(entity.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue());
        ptlTrade.setSettleDate(DateUtil.parse(DateUtil.format(entity.getTradeDate())));
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
        BussTrdTransferOrderDto ret = bussTrdTransferOrderService.generateOrder(param);
        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtos = new ArrayList<>();
        if (ret != null) {
            bussTrdTransferOrderDtos.add(ret);
            if(StringUtil.equals(OperationType.REVOKE.toString(), mode)) {
                ret.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
            }
            if (StringUtil.equals(ret.getPs(), DictCodeListExt1.收支方向_支出.getValue())) {
                ret.setMyAcctNo(ret.getPayAcctNo());
                ret.setCounterAcctNo(ret.getRecAcctNo());
            }
            else {
                ret.setMyAcctNo(ret.getRecAcctNo());
                ret.setCounterAcctNo(ret.getPayAcctNo());
            }
            if (StringUtil.isNotEmpty(ret.getIsNeedSend())) {
                if (DictCodeList.是否_是.getCodeName().equals(ret.getIsNeedSend())) {
                    ret.setIsNeedSend(DictCodeList.是否_是.getValue());
                }
                else if (DictCodeList.是否_否.getCodeName().equals(ret.getIsNeedSend())) {
                    ret.setIsNeedSend(DictCodeList.是否_否.getValue());
                }
            }
            StlPayOrdersDto stl = gen划款指令流程(bussTrdTransferOrderDtos, baseTradeProductDealDto.getTrdProductDealAdd(), null);
            if(!StringUtil.isNotEmpty(stl.getSettleDate())){
                stl.setSettleDate(baseTradeProductDealDto.getTrdProductDeal().getSettleDate());
            }
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stl));
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
     * 入业务流程表以及入账
     *
     * @see
     * @since 1.0
     */
    public void excute(BaseTradeProductDealDto baseTradeProductDealDto,NetPlanTransfer002Dto dto,NetPlanTransfer001Dto entity){
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
    }

    /**
     * 新建界面提交时插入持仓流水 {@inheritDoc}
     */
    @Override
    public void doInsert(NetPlanTransfer001Dto entity, OperationType type) {
        if (entity.getOperateType().equals("submit")) {
            beforeComplete(entity);
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

    private void beforeComplete(NetPlanTransfer001Dto entity) {
        if(!StringUtil.isEmptyList(entity.getNetPlanTransfer002DtoList())){
            entity.getNetPlanTransfer002DtoList().forEach(netPlanTransfer002Dto -> {
                if(!StringUtil.isEmptyList(netPlanTransfer002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos())){
                    netPlanTransfer002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {
                        //入业务流程表以及入账
                        excute(baseTradeProductDealDto,netPlanTransfer002Dto,entity);
                    });
                    //入组合持仓或流水表
                    FinProductTradeDto finProductTradeDto = netPlanTransfer002Dto.getFinProductTradeDto();
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
                    if (StringUtil.isNotEmpty(netPlanTransfer002Dto.getPortfolioId()) && !netPlanTransfer002Dto.getActPortfolioId().equals(
                            netPlanTransfer002Dto.getPortfolioId())) {
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(netPlanTransfer002Dto.getActPortfolioId());
                                    }
                                });
                        baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(netPlanTransfer002Dto.getPortfolioId());
                                    }
                                });
                    }
                }
            });
        }
    }

    /**
     * 保存中进行提交时进行性插入持仓流水 {@inheritDoc}
     */
    @Override
    public void doUpdate(NetPlanTransfer001Dto entity) {
        if (entity.getOperateType().equals("submit")) {
            doDelete(entity.getWorkflowId());
            if(!StringUtil.isEmptyList(entity.getNetPlanTransfer002DtoList())) {
                entity.getNetPlanTransfer002DtoList().forEach(netPlanTransfer002Dto -> {
                    genFinProductDto(netPlanTransfer002Dto, entity);
                });
            }
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(NetPlanTransfer001Dto.class, true);
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
    public void doComplete(NetPlanTransfer001Dto entity, OperationType type) {
        if (StringUtil.equals(entity.getOperateType(), "submit")) {
            //删除业务流程表
            baseTradeProductDealCommService.deleteWflData(entity.getWorkflowId());
            if (!StringUtil.isEmptyList(entity.getNetPlanTransfer002DtoList())) {
                entity.getNetPlanTransfer002DtoList().forEach(netPlanTransfer002Dto -> {
                    //插入组合持仓和各业务表
                    FinProductTradeDto finProductTradeDto = netPlanTransfer002Dto.getFinProductTradeDto();
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                    //add by yujiaping  20210312 ZGBSS-864 start
                    if(!StringUtil.isEmptyList(finProductTradeDto.getBaseTradeProductDealDtos())){   
                	int m=0;
                	int n=0;
                	for(int i=0;i<finProductTradeDto.getBaseTradeProductDealDtos().size();i++) {
                		if(StringUtil.equals(finProductTradeDto.getBaseTradeProductDealDtos().get(i).getTrdProductDeal().getPs(), "T02")) {
                			m=i;
                		}
                		if(StringUtil.equals(finProductTradeDto.getBaseTradeProductDealDtos().get(i).getTrdProductDeal().getPs(), "T01")) {
                			n=i;
                		}
                	}
                finProductTradeDto.getBaseTradeProductDealDtos().get(n).getTrdProductDeal().setrTradeId(finProductTradeDto.getBaseTradeProductDealDtos().get(m).getTrdProductDeal().getTradeId());
                    }
                 //add by yujiaping  20210312 ZGBSS-864 end
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
//                            if(StringUtil.equals(trdProductDeal.getPs(),DictCodeList.交易方向_买入.getValue())) {
//                                AllTradeCommonUtil.setDeals(inputDto, trdProductDeal, trdProductDealAdd, baseTradeProductDealDto.getPaTrdProductDeal(),
//                                        baseTradeProductDealDto.getStlPayOrdersList(), baseTradeProductDealDto.getPtlTrade(), netPlanTransfer002Dto.getActPortfolioId());
//                            }
//                            else {
//                                AllTradeCommonUtil.setDeals(inputDto, trdProductDeal, trdProductDealAdd, baseTradeProductDealDto.getPaTrdProductDeal(),
//                                        baseTradeProductDealDto.getStlPayOrdersList(), baseTradeProductDealDto.getPtlTrade(), entity.getActPortfolioId());
//                            }
                            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

                            //风险试算
                            basRiskMathCommService.insert(netPlanTransfer002Dto.getRiskTrialRetDto(), trdProductDeal.getTradeId());

                            // 插入交易报送表
                            Map<String, Object> map = new HashMap<>();
                            map.put("product", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
                            map.put("portfolioId", trdProductDealAdd.getPortfolioId());
                            if (StringUtil.equals(trdProductDeal.getPs(), DictCodeList.交易方向_买入.getValue())
                                    || StringUtil.equals(trdProductDeal.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                                String prodCode = simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/NetPlanTransfer001/selProdCode",
                                        map, String.class);
                                if (StringUtil.isNotEmpty(prodCode)) {
                                    RepTrade repentity = new RepTrade();
                                    repentity.setRepFinprodRegId(prodCode);
                                    repentity.setRepBankTradeCode(trdProductDeal.getTradeId());
                                    repentity.setRepBankAssetCode(trdProductDeal.getFinprodId());
                                    repentity.setRepCcy(trdProductDeal.getCcy());
                                    repentity.setRepOccurMoney(trdProductDeal.getPrinAmt());
                                    repentity.setRepIssuePartyId(CacheUtil.getSysParam("issue_party_id"));
                                    repentity.setRepTradeComponent(trdProductDeal.getCounterId());
                                    BigDecimal cloPrice = new BigDecimal(1);
                                    if (!StringUtil.equals(trdProductDeal.getCcy(),
                                            DictCodeList.币种_人民币_CNY.getValue())) {
                                        map.put("ccy", entity.getCcy());
                                        map.put("cny", DictCodeList.币种_人民币_CNY.getValue());
                                        map.put("tradDate", trdProductDeal.getTradeDate());
                                        BigDecimal num = simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/NetPlanTransfer001/selCloPrice",
                                                map, BigDecimal.class);
                                        if(num != null){
                                            cloPrice = num;
                                        }
                                    }
                                    repentity.setRepMoneyCny(trdProductDeal.getPrinAmt().multiply(cloPrice));
                                    repentity.setRepCount(trdProductDeal.getShareAmt());
                                    repentity.setRepMoneyFlowType(trdProductDeal.getPs().equals(
                                            DictCodeList.交易方向_买入.getValue()) ? DictCodeListExt2.资金流动类型_资产买入.getValue() : DictCodeListExt2.资金流动类型_资产卖出或到期.getValue());
                                    repentity.setRepTradeDate(trdProductDeal.getTradeDate());
                                    repentity.setRepTradeComponent(trdProductDeal.getCounterTrader());
                                    repentity.setRepSendStatus(DictCodeList.是否_是.getValue());
                                    if (iPlatformDate != null) {
                                        Date sysdate = iPlatformDate.getSysDate();
                                        repentity.setRepStatusDate(sysdate);
                                    }
                                    repentity.setRepRemark(trdProductDeal.getRemark());
                                    // 调交易报送组件
                                    repTradeMapper.insert(repentity);
                                    map.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
                                    map.put("finprodId", trdProductDeal.getFinprodId());
                                    simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/NetPlanTransfer001/updateRep",
                                            map, Integer.class);
                                }
                            }
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
                    if (StringUtil.isNotEmpty(netPlanTransfer002Dto.getPortfolioId()) && !netPlanTransfer002Dto.getActPortfolioId().equals(
                            netPlanTransfer002Dto.getPortfolioId())) {
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(netPlanTransfer002Dto.getActPortfolioId());
                                    }
                                });
                        baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(netPlanTransfer002Dto.getPortfolioId());
                                    }
                                });
                    }
                });
            }
        }
    }

    @Override
    public void doBack(NetPlanTransfer001Dto entity) {
        if(!StringUtil.isEmptyList(entity.getNetPlanTransfer002DtoList())){
            entity.getNetPlanTransfer002DtoList().forEach(netPlanTransfer002Dto -> {
                if(!StringUtil.isEmptyList(netPlanTransfer002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos())) {
                    netPlanTransfer002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {
                        AllTradeCommonUtil.deletePosition("",baseTradeProductDealDto.getTrdProductDeal().getTradeId());
                    });
                }
            });
        }
    }

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。
     * <功能详细描述>
     * @param
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
    public BigDecimal selectWflAmtNet(Map<String,Object> map){
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/LendTransfer/selectWflAmtLend",map,
        		BigDecimal.class);
    }

    /**
     * 查询组合可用持仓 <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getPtlSecPosition(PtlSecPositionFlowDto dto) {
        dto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        if (StringUtil.isNotEmpty(dto.getSecAcctId())) {
            dto.setChannel(true);
        }
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanNetInvestTrade/getPtlSecPosition",
                dto, BigDecimal.class);
    }
}
