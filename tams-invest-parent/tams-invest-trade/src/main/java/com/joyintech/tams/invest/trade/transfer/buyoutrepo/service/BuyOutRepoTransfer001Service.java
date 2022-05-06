package com.joyintech.tams.invest.trade.transfer.buyoutrepo.service;

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
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.trade.transfer.buyoutrepo.dto.BuyOutRepoTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.buyoutrepo.dto.BuyOutRepoTransfer002Dto;
import com.joyintech.tams.invest.trade.transfer.netplantransfer.dto.NetPlanTransfer001Dto;
import com.joyintech.tools.collectors.JoyinCollectors;
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
 * 买断式回购转仓Service
 * @author liuwei
 * @version 1.0 2019年12月31日
 * @see BuyOutRepoTransfer001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BuyOutRepoTransfer001Service extends CommonExportService<BuyOutRepoTransfer001Dto>
        implements BaseProcessService<BuyOutRepoTransfer001Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(BuyOutRepoTransfer001Service.class);

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
     * 回购质押品Mapper
     */
    @Autowired
    private FinDealPledgeMapper finDealPledgeMapper;
    /**
     * 交易类金融产品Mapper
     */
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;
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
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * 通用Mapper共同代理方法
     */
    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    /**
     * 金融产品分类表Mappeer
     */
    @Autowired(required = false)
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired(required = false)
    private FinCashInfoMapper finCashInfoMapper;

    @Autowired(required = false)

    private FinCashPayRuleMapper finCashPayRuleMapper;

    @Autowired(required = false)
    private FinCashCalcRuleMapper finCashCalcRuleMapper;

    @Autowired(required = false)
    private FinRateInfoMapper finRateInfoMapper;

    @Autowired(required = false)
    private FinCashPlanMapper finCashPlanMapper;
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    /**
     * MultiApproveTradeCheckService
     */
    /*@Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;*/

    /**
     * 获取初始化交易信息
     * @return 联想下拉框实体类集合
     * @see
     * @since 1.0
     */
    public BuyOutRepoTransfer001Dto getTradeInfo(String tradeId){
        return simpleRestTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/BuyOutRepoTransfer/getTradeInfo?tradeId="+tradeId,
                BuyOutRepoTransfer001Dto.class);
    }

    /**
     * 获取转仓信息—组合代码
     * @return 联想文本框实体类集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortfolioId(Map<String, Object> map){
        return simpleRestTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/BuyOutRepoTransfer/getPortfolioId",
                map, SelectOptionDto.class);
    }

    /**
     * 获取产品持有债券数量
     * @return 产品持有债券数量
     * @see
     * @since 1.0
     */
    public BigDecimal selectPosition(BuyOutRepoTransfer001Dto dto){
        Map<String,Object> map = new HashMap<>();
        map.put("finprodId",dto.getFinprodId());
        map.put("portfolioId",dto.getPortfolioId());
        map.put("secAcctId",dto.getSecManageAcctId());
        map.put("chlId",dto.getChlId());
        map.put("cdate", DateUtil.format(dto.getTradeDate()));
        map.put("HODING_TYPE_03", DictCodeList.持仓类型_实际.getValue());
        map.put("HODING_TYPE_05", DictCodeList.持仓类型_融入.getValue());
        map.put("HODING_TYPE_06", DictCodeList.持仓类型_融出.getValue());
        map.put("HODING_TYPE_04", DictCodeList.持仓类型_质押.getValue());
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/BuyOutRepoTransfer/selectPosition",
                map, BigDecimal.class);
    }

    /**
     * 获取转仓dto
     * @return 转仓dto
     * @see
     * @since 1.0
     */
    public void genFinProductDto(BuyOutRepoTransfer002Dto dto, BuyOutRepoTransfer001Dto entity){
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        baseTradeProductDealDtos.add(genBaseTradeProductDealDto(dto,entity, DictCodeList.交易方向_买入.getValue(),null,null));
        baseTradeProductDealDtos.add(genBaseTradeProductDealDto(dto,entity, DictCodeList.交易方向_卖出.getValue(),
                baseTradeProductDealDtos.get(0).getTrdProductDeal().getTradeId(),baseTradeProductDealDtos.get(0).getTrdProductDeal().getTradeId()));
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
    public BaseTradeProductDealDto genBaseTradeProductDealDto(BuyOutRepoTransfer002Dto dto,BuyOutRepoTransfer001Dto entity,String ps,String tradeId,String rtradeId){
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        //组合代码转为母组合代码
        dto.setActPortfolioId(dto.getPortfolioId());
        dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));
        entity.setActPortfolioId(entity.getPortfolioIdOut());
        entity.setPortfolioIdOut(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioIdOut()));
        //金融产品交易表
        TrdProductDeal trdProductDeal = genTrdProductDeal(dto,entity,ps);
        if(StringUtil.equals(ps,DictCodeList.交易方向_卖出.getValue()) && StringUtil.isNotEmpty(tradeId)){
            trdProductDeal.setSplitTradeId(tradeId);
            trdProductDeal.setrTradeId(rtradeId);
        }
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
        //add by liuwei for huaxing 20200309 start
        baseTradeProductDealDto.setSecLendFinprodId(trdProductDeal.getTradeId());
        //add by liuwei for huaxing 20200309 end
        return baseTradeProductDealDto;
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
     * 获取组合交易表list
     * @return 组合交易表list
     * @see
     * @since 1.0
     */
    public List<PtlTrade> genPtlTrades(BuyOutRepoTransfer002Dto dto, BuyOutRepoTransfer001Dto entity, String ps,String tradeId){
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
    public PtlTrade genPtlTrade(String portfolioId, BuyOutRepoTransfer001Dto entity, String ps,String tradeId){
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(portfolioId);
        ptlTrade.setBusiNo(tradeId);
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(tradeId);
        ptlTrade.setBusiType(DictCodeList.共通业务类型_买断式回购交易.getValue());
        ptlTrade.setSettleDate(DateUtil.parse(DateUtil.format(entity.getTradeDate())));
        ptlTrade.setSecManageAcctId(entity.getSecManageAcctId());
        return ptlTrade;
    }

    /**
     * 获取金融产品交易扩展表dto
     * @return 金融产品交易附属表dto
     * @see
     * @since 1.0
     */
    public PaTrdProductDeal genPaTrdProductDeal(BuyOutRepoTransfer002Dto dto, BuyOutRepoTransfer001Dto entity, String ps){
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        if(entity.getPs().equals(DictCodeList.回购拆借方向_正回购.getValue())){
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_买断式正回购.getValue());
        }
        else {
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_买断式逆回购.getValue());
        }
        paTrdProductDeal.setBackLendDirection(entity.getPs());
        paTrdProductDeal.setDeptCode(paTrdProductDeal.getCreateDept());
        return paTrdProductDeal;
    }

    /**
     * 获取金融产品交易附属表dto
     * @return 金融产品交易附属表dto
     * @see
     * @since 1.0
     */
    public TrdProductDealAdd genTrdProductDealAdd(BuyOutRepoTransfer002Dto dto, BuyOutRepoTransfer001Dto entity, String ps){
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
     * 获取金融产品交易表dto
     * @return 金融产品交易表dto
     * @see
     * @since 1.0
     */
    public TrdProductDeal genTrdProductDeal(BuyOutRepoTransfer002Dto dto,BuyOutRepoTransfer001Dto entity,String ps){
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        if(StringUtil.isNotEmpty(entity.getFinprodId())){
            if (baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
                } else {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
                }
                trdProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            }
            else {
                trdProductDeal.setTradeId(IDGenerator.generate());
            }
            trdProductDeal.setBusiType(DictCodeList.共通业务类型_买断式回购交易.getValue());
            if(StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_正回购.getValue())){
                trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
                trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
            }
            else{
                trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
                trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
            }
            trdProductDeal.setBranch(Long.parseLong("0"));
            trdProductDeal.setVdate(DateUtil.parse(DateUtil.format(entity.getVdate())));
            trdProductDeal.setMdate(DateUtil.parse(DateUtil.format(entity.getMdate())));
            trdProductDeal.setTradeDate(DateUtil.parse(DateUtil.format(entity.getTradeDate())));
            trdProductDeal.setSettleDate(DateUtil.parse(DateUtil.format(entity.getTradeDate())));
            trdProductDeal.setCcy(entity.getCcy());
            trdProductDeal.setSettleSpeed(Long.parseLong("0"));
            // 计算交易本金
            BigDecimal prinAmt = this.getPrinAmt(dto,entity);
            trdProductDeal.setShareAmt(prinAmt);
            trdProductDeal.setPrinAmt(prinAmt);
            trdProductDeal.setIntAmt(ArithmeticUtil.subtract(dto.getFirSettleAmt(),prinAmt));
            trdProductDeal.setTradeAmt(dto.getFirSettleAmt());
            trdProductDeal.setFpriceAmt(dto.getFirSettleAmt());
            trdProductDeal.setPs(ps);
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            if(ps.equals(DictCodeList.交易方向_买入.getValue())){
                trdProductDeal.setCounterId(entity.getPortfolioIdOut());
                trdProductDeal.setRemark(entity.getPs().equals(DictCodeList.回购拆借方向_正回购.getValue()) ?
                        "买断式正回购转入" : "买断式逆回购转入");
                trdProductDeal.setFinprodId(trdProductDeal.getTradeId());
            }
            else {
                trdProductDeal.setCounterId(dto.getPortfolioId());
                trdProductDeal.setRemark(entity.getPs().equals(DictCodeList.回购拆借方向_正回购.getValue()) ?
                        "买断式正回购转出" : "买断式逆回购转出");
                trdProductDeal.setFinprodId(entity.getTradeId());
            }
            trdProductDeal.setIsOutTrade(DictCodeList.是否_否.getValue());
            trdProductDeal.setTrader(trdProductDeal.getCreateUser());
            trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
            trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
            trdProductDeal.setTradeMarket(entity.getTradeMarket());
            trdProductDeal.setSecManageAcctId(entity.getSecManageAcctId());
            trdProductDeal.setChlId(entity.getChlId());
            //add by liuwei for huaxing 20200330 start
            trdProductDeal.setTrader(trdProductDeal.getCreateUser());
            //add by liuwei for huaxing 20200330 end

            trdProductDeal.setIsSwapTransaction(DictCodeList.是否_是.getValue());
        }
        return trdProductDeal;
    }

    /**
     * 计算交易本金
     * @return 金融产品交易表dto
     * @see
     * @since 1.0
     */
    public BigDecimal getPrinAmt(BuyOutRepoTransfer002Dto dto,BuyOutRepoTransfer001Dto entity){
        Map<String,Object> map = new HashMap<>();
        map.put("cdate",DateUtil.format(entity.getTradeDate()));
        map.put("tradeId",entity.getTradeId());
        map.put("finprodMarketId",entity.getFinprodMarketId());
        //1、取出待转仓产品组合持有债券总持仓（针对债券和交易）
        BigDecimal bondAmt = this.getBondAmount(map);
        //2、取出待转仓产品组合总持仓（针对交易）
        BigDecimal tradeAmt = this.getTradeAmount(map);
        //3、计算交易本金: 列表.券面总额 * 步骤2.总持仓 / 步骤1.总持仓 / 100
        BigDecimal printAmt = dto.getTradeAmt();
        printAmt = ArithmeticUtil.divide(ArithmeticUtil.divide(ArithmeticUtil.multiply(printAmt,tradeAmt),bondAmt,2),100,2);
        return printAmt;
    }

    /**
     * 获取待转仓产品组合持有债券总持仓（针对债券和交易）
     * @return 持仓值
     *
     * @see
     * @since 1.0
     */
    public BigDecimal getBondAmount(Map<String,Object> map){
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/BuyOutRepoTransfer/getBondAmount",
                map, BigDecimal.class);
    }

    /**
     * 获取待转仓产品组合总持仓（针对交易）
     * @return 持仓值
     *
     * @see
     * @since 1.0
     */
    public BigDecimal getTradeAmount(Map<String,Object> map){
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/BuyOutRepoTransfer/getTradeAmount",
                map, BigDecimal.class);
    }

    private void beforeComplete(BuyOutRepoTransfer001Dto entity) {
        if(!StringUtil.isEmptyList(entity.getBuyOutRepoTransfer002DtoList())){
            entity.getBuyOutRepoTransfer002DtoList().forEach(buyOutRepoTransfer002Dto -> {
                if(!StringUtil.isEmptyList(buyOutRepoTransfer002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos())){
                    buyOutRepoTransfer002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {
                        //入业务流程表以及入账
                        excute(baseTradeProductDealDto,buyOutRepoTransfer002Dto,entity);
                        //插入质押品持仓
                        this.ptlSecPositionFlowLogic(entity,buyOutRepoTransfer002Dto,baseTradeProductDealDto);
                    });
                    //入组合持仓或流水表
                    FinProductTradeDto finProductTradeDto = buyOutRepoTransfer002Dto.getFinProductTradeDto();
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
                    if (StringUtil.isNotEmpty(buyOutRepoTransfer002Dto.getPortfolioId()) && !buyOutRepoTransfer002Dto.getActPortfolioId().equals(
                            buyOutRepoTransfer002Dto.getPortfolioId())) {
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(buyOutRepoTransfer002Dto.getActPortfolioId());
                                    }
                                });
                        baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(buyOutRepoTransfer002Dto.getPortfolioId());
                                    }
                                });
                    }
                }
            });
        }
    }

    /**
     * 写入质押品的组合持仓流水
     *
     * @param input 画面参数Dto
     *
     */
    private boolean ptlSecPositionFlowLogic(BuyOutRepoTransfer001Dto entity, BuyOutRepoTransfer002Dto input,BaseTradeProductDealDto baseTradeProductDealDto) {
        String tradeId = baseTradeProductDealDto.getTrdProductDeal().getTradeId();
        String portfolioId = null;
        String actPortfolioId = null;
        // 待分配投资目的的质押券面总额
        BigDecimal pledgePrin = input.getTradeAmt();
        if(StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getPs(),DictCodeList.交易方向_买入.getValue())){
            portfolioId = input.getPortfolioId();
            actPortfolioId = input.getActPortfolioId();
        }
        else {
            portfolioId = entity.getPortfolioIdOut();
            actPortfolioId = entity.getActPortfolioId();
            tradeId = baseTradeProductDealDto.getTrdProductDeal().getFinprodId();
        }
        List<PtlSecPositionFlowDto> lstPtlSecPositionFlowDto = new ArrayList<>();
        // List<组合持仓流水DTO>(若上记的List<回购质押品DTO>为空，则此为空，反之循环构造DTO)
        // 通过上记输出的List<输出的质押品DTO>，循环该List构造组合持仓流水DTO
        // 可质押券信息（含投资目的）

        if (DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getPs())) {
            Map<String, List<PledgeBondInfoDto>> avaMap = this.selectAvaPledgeWithInvAim(entity,input).stream().collect(
                    JoyinCollectors.groupingBy(x -> x.getBondCode()));
            avaMap.entrySet().stream().forEach(entry -> {
                Collections.sort(entry.getValue(), new Comparator<PledgeBondInfoDto>() {

                    @Override
                    public int compare(PledgeBondInfoDto o1, PledgeBondInfoDto o2) {
                        return o1.getInvAimSort().compareTo(o2.getInvAimSort());
                    }
                });
            });

            FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class,
                    entity.getFinprodId(), FinProduct.class);
            List<PledgeBondInfoDto> pledgeList = avaMap.get(finProduct.getFinprodMarketId());
            if (!StringUtil.isEmptyList(pledgeList)) {
                for (PledgeBondInfoDto avaBond : pledgeList) {
                    PtlSecPositionFlowDto resultDto = new PtlSecPositionFlowDto();
                    // 当前投资目的可分配券面总额
                    BigDecimal avaPrin = avaBond.getAvaPledgePrin();
                    // 当前投资目的决定分配的券面总额
                    BigDecimal prin = pledgePrin.compareTo(avaPrin) > 0 ? avaPrin : pledgePrin;

                    // 组合代码 = 画面.组合代码
                    resultDto.setPortfolioId(portfolioId);
                    // 证券管理账户/通道代码 = nvl（nvl（金融产品交易Entity.证券托管账户，金融产品交易Entity.通道代码），999999）
                    // resultDto.setSecAcctId(StringUtil.isEmpty(input.getSecAcctId()) ? "999999" : input.getSecAcctId());
                    resultDto.setSecAcctId(StringUtil.isEmpty(entity.getSecManageAcctId()) ? (StringUtil.isEmpty(
                            entity.getChlId()) ? "999999" : entity.getChlId()) : entity.getSecManageAcctId());
                    // 金融产品代码 = 输出质押品DTO.金融产品代码
                    resultDto.setFinprodId(entity.getFinprodId());
                    // 分支序号 = 系统固定参数，默认为0
                    resultDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
                    // 持仓类型
                    resultDto.setHodingType(DictCodeList.持仓类型_融出.getValue());
                    // 日期 = 金融产品交易Entity.交易日
                    resultDto.setCdate(input.getTradeDate());
                    if(StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getPs(),DictCodeList.交易方向_卖出.getValue())){
                        prin = prin.negate();
                    }
                    // 数量 = 输出质押品DTO.质押数量
                    resultDto.setAmount(ArithmeticUtil.divide(prin, new BigDecimal("100"), 4));
                    baseTradeProductDealDto.setSecAmount(ArithmeticUtil.divide(prin, new BigDecimal("100"), 4));
                    // 币种 = 金融产品交易Entity.币种
                    resultDto.setCcy(entity.getCcy());
                    // 交易编号 = 金融产品交易Entity.交易编号
                    resultDto.setTradeId(baseTradeProductDealDto.getTrdProductDeal().getTradeId());
                    //母金融产品代码 : 转出交易登原始交易编号
                    resultDto.setpFinprodId(tradeId);
                    // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
                    resultDto.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());

                    if (avaPrin.compareTo(BigDecimal.ZERO) > 0) {
                        lstPtlSecPositionFlowDto.add(resultDto);
                        if (StringUtil.isNotEmpty(actPortfolioId)
                                && !StringUtil.equals(actPortfolioId, portfolioId)) {
                            PtlSecPositionFlowDto subResultDto = new PtlSecPositionFlowDto();
                            BeanUtil.copyPropertiesIgnoreNull(subResultDto, resultDto);
                            subResultDto.setPortfolioId(actPortfolioId);
                            lstPtlSecPositionFlowDto.add(subResultDto);
                        }
                    }
                    // 分配完毕后,停止可质押券信息（含投资目的）的循环
                    pledgePrin = pledgePrin.subtract(prin);
                    if (pledgePrin.compareTo(BigDecimal.ZERO) <= 0) {
                        break;
                    }
                }
            }
        }
        else {
            PtlSecPositionFlowDto resultDto = new PtlSecPositionFlowDto();
            // 组合代码 = 画面.组合代码
            resultDto.setPortfolioId(portfolioId);
            // 证券管理账户/通道代码 = nvl（nvl（金融产品交易Entity.证券托管账户，金融产品交易Entity.通道代码），999999）
            // resultDto.setSecAcctId(StringUtil.isEmpty(input.getSecAcctId()) ? "999999" : input.getSecAcctId());
            resultDto.setSecAcctId(StringUtil.isEmpty(entity.getSecManageAcctId()) ? (StringUtil.isEmpty(
                    entity.getSecManageAcctId()) ? "999999" : entity.getSecManageAcctId()) : entity.getSecManageAcctId());
            // 金融产品代码 = 输出质押品DTO.金融产品代码
            resultDto.setFinprodId(entity.getFinprodId());
            // 分支序号 = 系统固定参数，默认为0
            resultDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
            // 持仓类型
            resultDto.setHodingType(DictCodeList.持仓类型_融入.getValue());
            // 日期 = 金融产品交易Entity.交易日
            resultDto.setCdate(input.getTradeDate());
            // 数量 = 输出质押品DTO.质押数量
            if(StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getPs(),DictCodeList.交易方向_卖出.getValue())){
                resultDto.setAmount(ArithmeticUtil.divide(input.getTradeAmt().negate(), 100, 4));
                baseTradeProductDealDto.setSecAmount(ArithmeticUtil.divide(input.getTradeAmt().negate(), 100, 4));

            }
            else {
                resultDto.setAmount(ArithmeticUtil.divide(input.getTradeAmt(), 100, 4));
                baseTradeProductDealDto.setSecAmount(ArithmeticUtil.divide(input.getTradeAmt(), 100, 4));
            }
            // 币种 = 金融产品交易Entity.币种
            resultDto.setCcy(entity.getCcy());
            // 交易编号 = 金融产品交易Entity.交易编号
            resultDto.setTradeId(baseTradeProductDealDto.getTrdProductDeal().getTradeId());
            //母金融产品代码 : 转出交易登原始交易编号
            resultDto.setpFinprodId(tradeId);
            // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
            resultDto.setInvAim("999999");
            lstPtlSecPositionFlowDto.add(resultDto);

            if (StringUtil.isNotEmpty(actPortfolioId)
                    && !StringUtil.equals(actPortfolioId, portfolioId)) {
                PtlSecPositionFlowDto subResultDto = new PtlSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(subResultDto, resultDto);
                subResultDto.setPortfolioId(actPortfolioId);
                lstPtlSecPositionFlowDto.add(subResultDto);
            }
        }
        if (StringUtil.isEmptyList(lstPtlSecPositionFlowDto)) {
            return true;
        }
        else {
            PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
            ptlPositionHoldDto.setPtlSecPositionFlows(lstPtlSecPositionFlowDto);
            return simpleRestTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/BuyoutRepoResolute/insertPosition", ptlPositionHoldDto,
                    Boolean.class);
        }
    }

    /**
     * 根据画面条件检索可质押券信息（含投资目的）
     *
     * @param input 画面参数
     * @return List<PledgeBondInfoDto>
     */
    public List<PledgeBondInfoDto> selectAvaPledgeWithInvAim(BuyOutRepoTransfer001Dto entity,BuyOutRepoTransfer002Dto input) {
        Map<String, Object> param = new HashMap<>();
        param.put("portfolioId", input.getPortfolioId());
        param.put("ps", entity.getPs());
        param.put("isWithInvAim", true);
        param.put("INV_AIM_AC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        param.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue());
        param.put("INV_AIM_FVTPL", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
        param.put("chlId", entity.getChlId());
        param.put("secAcctId", entity.getSecManageAcctId());
        return simpleRestTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/BuyoutRepoResolute/selectAvaPledgeBondDetailList", param,
                PledgeBondInfoDto.class);
    }

    private void ptlSecPositionFlowLogicDelete(String tradeId) {
        simpleRestTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/BuyoutRepoResolute/deletePtlSecPositionFlow?tradeId=" + tradeId,
                Long.class);
    }

    /**
     * 入业务流程表以及入账
     *
     * @see
     * @since 1.0
     */
    public void excute(BaseTradeProductDealDto baseTradeProductDealDto,BuyOutRepoTransfer002Dto dto,BuyOutRepoTransfer001Dto entity){
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
        //买入交易入交易类金融产品表和回购质押品
        if(StringUtil.equals(trdProductDeal.getPs(),DictCodeList.交易方向_买入.getValue())){
            // 交易类金融产品
            FinTradeProduct finTradeProduct = this.genFinTradeProduct(dto, entity,trdProductDeal);
            // 回购质押品
            FinDealPledge finDealPledge = this.genFinDealPledge(dto, entity,trdProductDeal);
            finTradeProductMapper.insertSelective(finTradeProduct);
            finDealPledgeMapper.insertSelective(finDealPledge);
            //插入金融产品分类表
            FinProductType finProductTypeKey = new FinProductType();
            finProductTypeKey.setFinprodId(entity.getTradeId());
            finProductTypeKey.setBranch(Long.valueOf("0"));
            FinProductType finProductType = bussCommonMapperDelegateService.selectByMultiPrimaryKey(FinProductTypeMapper.class,
                    finProductTypeKey,FinProductType.class);
            finProductType.setFinprodId(trdProductDeal.getTradeId());
            finProductType.setCreateUser(trdProductDeal.getCreateUser());
            finProductType.setCreateDept(trdProductDeal.getCreateDept());
            finProductType.setCreateTime(trdProductDeal.getCreateTime());
            finProductType.setUpdateUser(trdProductDeal.getUpdateUser());
            finProductType.setUpdateTime(trdProductDeal.getUpdateTime());
            finProductTypeMapper.insertSelective(finProductType);
//                finTradeProductMapper.updateByPrimaryKeySelective(finTradeProduct);
//                finDealPledgeMapper.updateByPrimaryKeySelective(finDealPledge);
        }
    }

    /**
     * 组装交易类金融产品实体
     *
     * @see
     * @since 1.0
     */
    public FinTradeProduct genFinTradeProduct(BuyOutRepoTransfer002Dto dto,BuyOutRepoTransfer001Dto entity,TrdProductDeal trdProductDeal){
        FinTradeProduct finTradeProductKey = new FinTradeProduct();
        finTradeProductKey.setFinprodId(entity.getTradeId());
        finTradeProductKey.setBranch(trdProductDeal.getBranch());
        FinTradeProduct finTradeProduct = new FinTradeProduct();
        FinTradeProduct ofinTradeProduct = bussCommonMapperDelegateService.selectByMultiPrimaryKey(
                FinTradeProductMapper.class,finTradeProductKey,FinTradeProduct.class);
        finTradeProduct.setFinprodId(trdProductDeal.getTradeId());
        finTradeProduct.setBranch(trdProductDeal.getBranch());
        finTradeProduct.setFinprodType(trdProductDeal.getFinprodType());
        finTradeProduct.setFinprodType2(trdProductDeal.getFinprodType2());
        finTradeProduct.setChlAgrtId(trdProductDeal.getChlId());
        finTradeProduct.setPrin(trdProductDeal.getPrinAmt());
        finTradeProduct.setCcy(trdProductDeal.getCcy());
        finTradeProduct.setVdate(entity.getVdate());
        finTradeProduct.setMdate(entity.getMdate());
        finTradeProduct.setTerm(entity.getRepoterm());
        finTradeProduct.setIntType(DictCodeList.利率类型_固定利率.getValue());
        finTradeProduct.setIntRate(ArithmeticUtil.divide(entity.getIntRate(),new BigDecimal(100),14));
        finTradeProduct.setBasis(entity.getBasis());
        finTradeProduct.setmPrinAmt(dto.getFirSettleAmt());
        finTradeProduct.setmIntAmt(ArithmeticUtil.subtract(dto.getDueSettleAmt(),dto.getFirSettleAmt()));
        finTradeProduct.setmTradeAmt(dto.getDueSettleAmt());
        finTradeProduct.setoFinprodId(entity.getTradeId());
        finTradeProduct.setTradeMarket(entity.getTradeMarket());
        finTradeProduct.setCalendarId(entity.getCalendarId());
        finTradeProduct.setTermType(entity.getTermType());
        finTradeProduct.setCounterId(entity.getPortfolioIdOut());
        if(ofinTradeProduct!= null){
            finTradeProduct.setmUnitCprice(ofinTradeProduct.getmUnitCprice());
            finTradeProduct.setmUnitInt(ofinTradeProduct.getmUnitInt());
            finTradeProduct.setmUnitFprice(ofinTradeProduct.getmUnitFprice());
            finTradeProduct.setmYield(ofinTradeProduct.getmYield());
            finTradeProduct.setmDeliveryType(ofinTradeProduct.getmDeliveryType());
        }
        return finTradeProduct;
    }

    /**
     * 组装回购质押品实体
     *
     * @see
     * @since 1.0
     */
    public FinDealPledge genFinDealPledge(BuyOutRepoTransfer002Dto dto,BuyOutRepoTransfer001Dto entity,TrdProductDeal trdProductDeal){
        FinDealPledge findealpledgeKey = new FinDealPledge();
        findealpledgeKey.setFinprodId(entity.getTradeId());
        findealpledgeKey.setBranch(trdProductDeal.getBranch());
        findealpledgeKey.setPledgeFinprodId(entity.getFinprodId());
        FinDealPledge finDealPledge = new FinDealPledge();
        finDealPledge = bussCommonMapperDelegateService.selectByMultiPrimaryKey(
                FinDealPledgeMapper.class,findealpledgeKey,FinDealPledge.class);
        finDealPledge.setFinprodId(trdProductDeal.getTradeId());
        finDealPledge.setBranch(trdProductDeal.getBranch());
        finDealPledge.setPledgeFinprodId(entity.getFinprodId());
        finDealPledge.setPledgeFaceValue(new BigDecimal(100));
        finDealPledge.setPledgeNumber(ArithmeticUtil.divide(dto.getTradeAmt(),new BigDecimal(100),2));
        finDealPledge.setPledgeRatio(entity.getPledgeRatio());
        finDealPledge.setPledgeAmt(ArithmeticUtil.multiply(dto.getTradeAmt(),entity.getPledgeRatio()));
        return finDealPledge;
    }

    /**
     * 根据交易编号删除对应数据
     *
     * @param tradeId 买断式回购信息
     * @return 回购质押品信息
     */
    public void deleteFinDealPledgeByTradeId(String tradeId){
        simpleRestTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/BuyoutRepoResolute/deleteFinDealPledgeByTradeId?tradeId=" + tradeId,
                Integer.class);
    }

    /**
     * 根据交易编号删除交易类金融产品
     *
     * @param tradeId 交易编号
     * @return Db
     */
    private void deleteFinTradeProductByTradeId(String tradeId) {
        simpleRestTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/BuyoutRepoResolute/deleteFinTradeProductByTradeId?tradeId=" + tradeId,
                Integer.class);
    }

    /**
     * 根据交易编号删除金融产品分类表
     *
     * @param tradeId 交易编号
     * @return Db
     */
    private void deleteFinProductTypeByTradeId(String tradeId) {
        simpleRestTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/BuyOutRepoTransfer/deleteFinProductTypeByTradeId?tradeId=" + tradeId,
                Integer.class);
    }

    /**
     * 新建界面提交时插入持仓流水 {@inheritDoc}
     */
    @Override
    public void doInsert(BuyOutRepoTransfer001Dto entity, OperationType type) {
        if (entity.getOperateType().equals("submit")) {
            beforeComplete(entity);
        }
    }

    /**
     * 保存中进行提交时进行性插入持仓流水 {@inheritDoc}
     */
    @Override
    public void doUpdate(BuyOutRepoTransfer001Dto entity) {
        if (entity.getOperateType().equals("submit")) {
            doDelete(entity.getWorkflowId());
            if(!StringUtil.isEmptyList(entity.getBuyOutRepoTransfer002DtoList())) {
                entity.getBuyOutRepoTransfer002DtoList().forEach(buyOutRepoTransfer002Dto -> {
                    genFinProductDto(buyOutRepoTransfer002Dto, entity);
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
        // 根据工作流代码查询交易编号、组合代码
        List<WflTrdProductDeal> wflTrdProductDealList = simpleRestTemplate.getForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/BuyoutRepoResolute/selectWflTrdProductDeal?workflowId=" + workflowId,
                WflTrdProductDeal.class);

        if (!StringUtil.isEmptyList(wflTrdProductDealList)) {
            wflTrdProductDealList.forEach(wflTrdProductDeal -> {
                String tradeId = wflTrdProductDeal.getTradeId();
                this.deleteFinDealPledgeByTradeId(tradeId);
                this.deleteFinTradeProductByTradeId(tradeId);
                this.deleteFinProductTypeByTradeId(tradeId);
                this.ptlSecPositionFlowLogicDelete(tradeId);
            });
        }
        baseTradeProductDealCommService.delete(workflowId);
    }

    /**
     * 流程 {@inheritDoc}
     */
    @Override
    public void doComplete(BuyOutRepoTransfer001Dto entity, OperationType type) {
        if (StringUtil.equals(entity.getOperateType(), "submit")) {
            //删除业务流程表
            baseTradeProductDealCommService.deleteWflData(entity.getWorkflowId());
            if (!StringUtil.isEmptyList(entity.getBuyOutRepoTransfer002DtoList())) {
                entity.getBuyOutRepoTransfer002DtoList().forEach(buyOutRepoTransfer002Dto -> {
                    //插入组合持仓和各业务表
                    FinProductTradeDto finProductTradeDto = buyOutRepoTransfer002Dto.getFinProductTradeDto();
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {
                        baseTradeProductDealDto.setSecAmount(baseTradeProductDealDto.getSecAmount()==null?new BigDecimal("0"):baseTradeProductDealDto.getSecAmount());
                    });
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
                            // 通道交易
                            TrdTradeFeeDetail trdTradeFeeDetail = null;
                            if (StringUtil.isNotEmpty(trdProductDeal.getChlId())) {
                                // 交易费用明细
                                trdTradeFeeDetail = AllTradeCommonUtil.genChannelTradeFeeDetail(trdProductDeal.getTradeId(), buyOutRepoTransfer002Dto.getChannelFee());
                                trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
                            }
                            // 通道交易
                            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                            inputDto.setProdid(trdProductDealAdd.getPortfolioId());
                            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                            inputDto.setTrdProductDeal(trdProductDeal);
                            inputDto.setTrdProductDealAdd(trdProductDealAdd);
                            inputDto.setTrdTradeFeeDetail(null);
                            inputDto.setStlPayOrders(baseTradeProductDealDto.getStlPayOrdersList().get(0));
                            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

                            //风险试算
                            basRiskMathCommService.insert(buyOutRepoTransfer002Dto.getRiskTrialRetDto(), trdProductDeal.getTradeId());

                            //入现金流信息表
                            TrdProductDeal tpdEntity = baseTradeProductDealDto.getTrdProductDeal();
                            // 现金流信息-本金
                            FinCashInfo finCashInfoAmt = new FinCashInfo();
                            finCashInfoAmt.setCashId(CommonUtil.generateCashFlowCode(tpdEntity.getTradeId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),
                                    tpdEntity.getBranch(), DictCodeListExt1.现金流二级类型_本金.getValue()));
                            finCashInfoAmt.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
                            finCashInfoAmt.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
                            finCashInfoAmt.setCcy(tpdEntity.getCcy());
                            finCashInfoAmt.setFinprodId(tpdEntity.getTradeId());
                            finCashInfoAmt.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
                            finCashInfoAmt.setBranch(tpdEntity.getBranch());

                            // 现金流信息-利息
                            FinCashInfo finCashInfoInt = new FinCashInfo();
                            finCashInfoInt.setCashId(CommonUtil.generateCashFlowCode(tpdEntity.getTradeId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),
                                    tpdEntity.getBranch(), DictCodeListExt1.现金流二级类型_利息.getValue()));
                            finCashInfoInt.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
                            finCashInfoInt.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
                            finCashInfoInt.setCcy(tpdEntity.getCcy());
                            finCashInfoInt.setFinprodId(tpdEntity.getTradeId());
                            finCashInfoInt.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
                            finCashInfoInt.setBranch(tpdEntity.getBranch());

                            // 现金流支付规则表Entity-利息
                            FinCashPayRule finCashPayRule = new FinCashPayRule();
                            finCashPayRule.setCashId(finCashInfoInt.getCashId());
                            finCashPayRule.setEffDate(tpdEntity.getVdate());
                            finCashPayRule.setFirstPayDate(tpdEntity.getMdate());
                            finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
                            finCashPayRule.setFinprodId(tpdEntity.getTradeId());
                            finCashPayRule.setFinprodType2(tpdEntity.getFinprodType2());
                            finCashPayRule.setBranch(tpdEntity.getBranch());

                            // 现金流计算规则表Entity-利息
                            FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
                            finCashCalcRule.setCashId(finCashInfoInt.getCashId());
                            finCashCalcRule.setEffDate(tpdEntity.getVdate());
                            finCashCalcRule.setCalcType(DictCodeList.计提类型_固定频率.getValue());
                            finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
                            finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
                            finCashCalcRule.setBasis(entity.getBasis());
                            finCashCalcRule.setYield(ArithmeticUtil.divide(entity.getIntRate(), 100, 14));
                            finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
                            finCashCalcRule.setFinprodId(tpdEntity.getTradeId());
                            finCashCalcRule.setFinprodType2(tpdEntity.getFinprodType2());
                            finCashCalcRule.setBranch(tpdEntity.getBranch());

                            // 现金流利率信息表Entity-利息
                            FinRateInfo finRateInfo = new FinRateInfo();
                            finRateInfo.setCashId(finCashInfoInt.getCashId());
                            finRateInfo.setEffDate(entity.getVdate());
                            finRateInfo.setIntType(DictCodeList.利率类型_固定利率.getValue());
                            finRateInfo.setBasis(entity.getBasis());
                            finRateInfo.setRate(ArithmeticUtil.divide(entity.getIntRate(), 100, 14));
                            finRateInfo.setFinprodId(tpdEntity.getTradeId());
                            finRateInfo.setFinprodType2(tpdEntity.getFinprodType2());
                            finRateInfo.setBranch(tpdEntity.getBranch());

                            // 现金流计划表Entity(本金)
                            FinCashPlan finCashPlanAmt = new FinCashPlan();
                            finCashPlanAmt.setCashId(finCashInfoAmt.getCashId());
                            finCashPlanAmt.setCashNum(new Long(1));
                            finCashPlanAmt.setCashType(DictCodeList.现金流类型_到期还本.getValue());
                            finCashPlanAmt.setVdateUnadjust(tpdEntity.getVdate());
                            finCashPlanAmt.setMdateUnadjust(tpdEntity.getMdate());
                            finCashPlanAmt.setPayDateUnadjust(tpdEntity.getMdate());
                            finCashPlanAmt.setVdate(tpdEntity.getVdate());
                            finCashPlanAmt.setMdate(tpdEntity.getMdate());
                            finCashPlanAmt.setPayDate(tpdEntity.getMdate());
                            finCashPlanAmt.setTermdays(DateUtil.getBetweenDayNumber(tpdEntity.getVdate(), tpdEntity.getMdate()));
                            finCashPlanAmt.setPrinAmt(tpdEntity.getPrinAmt());
                            finCashPlanAmt.setIntPrinAmt(tpdEntity.getPrinAmt());
                            finCashPlanAmt.setIntAmt(BigDecimal.ZERO);
                            finCashPlanAmt.setIsPayInt(DictCodeList.是否_是.getValue());
                            finCashPlanAmt.setCashAmt(tpdEntity.getPrinAmt());
                            finCashPlanAmt.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
                            finCashPlanAmt.setFinprodId(tpdEntity.getTradeId());
                            finCashPlanAmt.setFinprodType2(tpdEntity.getFinprodType2());
                            finCashPlanAmt.setBranch(tpdEntity.getBranch());
                            finCashPlanAmt.setPayType(DictCodeList.分红方式_现金分红.getValue());
                            // 现金流计划表Entity(利息)
                            FinCashPlan finCashPlanInt = new FinCashPlan();
                            finCashPlanInt.setCashId(finCashInfoInt.getCashId());
                            finCashPlanInt.setCashNum(new Long(2));
                            finCashPlanInt.setCashType(DictCodeList.现金流类型_到期收息.getValue());
                            finCashPlanInt.setVdateUnadjust(tpdEntity.getVdate());
                            finCashPlanInt.setMdateUnadjust(tpdEntity.getMdate());
                            finCashPlanInt.setPayDateUnadjust(tpdEntity.getMdate());
                            finCashPlanInt.setVdate(tpdEntity.getVdate());
                            finCashPlanInt.setMdate(tpdEntity.getMdate());
                            finCashPlanInt.setPayDate(tpdEntity.getMdate());
                            finCashPlanInt.setTermdays(DateUtil.getBetweenDayNumber(tpdEntity.getVdate(), tpdEntity.getMdate()));
                            finCashPlanInt.setPrinAmt(BigDecimal.ZERO);
                            finCashPlanInt.setIntPrinAmt(tpdEntity.getPrinAmt());
                            finCashPlanInt.setIntAmt(ArithmeticUtil.subtract(buyOutRepoTransfer002Dto.getDueSettleAmt(), buyOutRepoTransfer002Dto.getFirSettleAmt()));
                            finCashPlanInt.setIsPayInt(DictCodeList.是否_是.getValue());
                            finCashPlanInt.setCashAmt(ArithmeticUtil.subtract(buyOutRepoTransfer002Dto.getDueSettleAmt(), buyOutRepoTransfer002Dto.getFirSettleAmt()));
                            finCashPlanInt.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
                            finCashPlanInt.setFinprodId(tpdEntity.getTradeId());
                            finCashPlanInt.setFinprodType2(tpdEntity.getFinprodType2());
                            finCashPlanInt.setBranch(tpdEntity.getBranch());
                            finCashPlanInt.setPayType(DictCodeList.分红方式_现金分红.getValue());

                            finCashInfoMapper.insertSelective(finCashInfoAmt);
                            finCashInfoMapper.insertSelective(finCashInfoInt);
                            finCashPayRuleMapper.insertSelective(finCashPayRule);
                            finCashCalcRuleMapper.insertSelective(finCashCalcRule);
                            finRateInfoMapper.insertSelective(finRateInfo);
                            finCashPlanMapper.insertSelective(finCashPlanAmt);
                            finCashPlanMapper.insertSelective(finCashPlanInt);

                            // 写入交易信息进交易报送信息表，为中债报送用
                            // 交易报送信息Entity
                            RepTrade repTradeKey = new RepTrade();
                            repTradeKey.setRepBankTradeCode(entity.getTradeId());
                            if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
                                repTradeKey.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_同业拆入和卖出回购首期.getValue());
                            }
                            else if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
                                repTradeKey.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_拆放同业和买入返售首期.getValue());
                            }
                            RepTrade repTrade = bussCommonMapperDelegateService.selectByMultiPrimaryKey(RepTradeMapper.class,repTradeKey,RepTrade.class);
                            RepTrade rtEntity = new RepTrade();
                            if(repTrade != null) {
                                BeanUtil.copyPropertiesIgnoreNull(rtEntity, repTrade);
                                rtEntity.setRepBankTradeCode(tpdEntity.getTradeId());
                                rtEntity.setRepBankAssetCode(tpdEntity.getTradeId());
                                rtEntity.setRepOccurMoney(tpdEntity.getPrinAmt());
                                repTrade.setRepRemark("买断式回购交易转仓");

                                // 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
                                simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/BuyoutRepoResolute/insertRepTrade",
                                        Arrays.asList(rtEntity), Boolean.class);
                            }

                            // 资产负债报送信息Entity
                            RepAssetDept repAssetDept = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                                    RepAssetDeptMapper.class,entity.getTradeId(),RepAssetDept.class);
                            if(repAssetDept != null) {
                                repAssetDept.setRepFinprodId(tpdEntity.getTradeId());
                                repAssetDept.setFinprodMarketId(tpdEntity.getTradeId());

                                // 调用资产负债信息报送组件
                                simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/BuyoutRepoResolute/insertRepAssetDebt",
                                        Arrays.asList(repAssetDept), Boolean.class);
                            }

                            // 拆入、正回购 -> 负债(拆入或正回购)类要素登记
                            if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
                                // 负债(同业拆入/卖出回购)报送信息Entity
                                RepLiabilities repLiabilities = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                                        RepLiabilitiesMapper.class,entity.getTradeId(),RepLiabilities.class);
                                if(repLiabilities != null) {
                                    repLiabilities.setRepFinprodId(tpdEntity.getTradeId());
                                    repLiabilities.setRepRemark("买断式回购交易转仓");
                                    repLiabilities.setRepBuyBackMoney(tpdEntity.getPrinAmt());

                                    // 调用负债(同业拆入卖出回购)报送信息组件
                                    simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/BuyoutRepoResolute/insertRepLiabilities",
                                            Arrays.asList(repLiabilities), Boolean.class);
                                }
                            }
                            // 拆出、逆回购 -> 拆放同业及买入返售
                            else {
                                RepAssetInterLend repAssetInterLend = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                                        RepAssetInterLendMapper.class,entity.getTradeId(),RepAssetInterLend.class);
                                if(repAssetInterLend != null) {
                                    repAssetInterLend.setRepFinprodId(tpdEntity.getTradeId());
                                    repAssetInterLend.setRepRemark("买断式回购交易转仓");
                                    repAssetInterLend.setRepTargetAmt(tpdEntity.getPrinAmt());

                                    // 调用负债(同业拆入卖出回购)报送信息组件
                                    simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/BuyoutRepoResolute/insertRepDlRepoInfo",
                                            Arrays.asList(repAssetInterLend), Boolean.class);
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
                    if (StringUtil.isNotEmpty(buyOutRepoTransfer002Dto.getPortfolioId()) && !buyOutRepoTransfer002Dto.getActPortfolioId().equals(
                            buyOutRepoTransfer002Dto.getPortfolioId())) {
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(buyOutRepoTransfer002Dto.getActPortfolioId());
                                    }
                                });
                        baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> {
                                    if(baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(buyOutRepoTransfer002Dto.getPortfolioId());
                                    }
                                });
                    }
                });
            }
        }
    }

    @Override
    public void doBack(BuyOutRepoTransfer001Dto entity) {
        if(!StringUtil.isEmptyList(entity.getBuyOutRepoTransfer002DtoList())){
            entity.getBuyOutRepoTransfer002DtoList().forEach(buyOutRepoTransfer002Dto -> {
                if(!StringUtil.isEmptyList(buyOutRepoTransfer002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos())){
                    buyOutRepoTransfer002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {
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
     * @param tradeId
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
}
