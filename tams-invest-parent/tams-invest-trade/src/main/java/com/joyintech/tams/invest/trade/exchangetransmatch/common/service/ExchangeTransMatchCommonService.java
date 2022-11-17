package com.joyintech.tams.invest.trade.exchangetransmatch.common.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.trade.dao.CreditLinesDealsMapper;
import com.joyintech.tams.invest.common.trade.dto.MinTrdUnitLimitDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.util.SysDataUtil;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bidding.ExchangeTransMatchBidding1Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.credit.ExchangeTransMatchCredit1Service;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDetailDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonRevokeDto;
import com.joyintech.tams.invest.trade.api.PositionUpdateParam;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dao.ExchangeTransMatchCommonMapper;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dto.ExchangeTransMatchBaseTradeDto;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.Fund.ExchangeTransMatchFund1Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bond.ExchangeTransMatchBond1Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bond.ExchangeTransMatchBond2Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bond.ExchangeTransMatchBond3Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bond.ExchangeTransMatchBond4Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bond.ExchangeTransMatchBond5Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bond.ExchangeTransMatchBond6Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bond.ExchangeTransMatchBondCommonService;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.repo.ExchangeTransMatchRepo1Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.repo.ExchangeTransMatchRepo2Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.repo.ExchangeTransMatchRepo3Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.repo.ExchangeTransMatchRepo4Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.repo.ExchangeTransMatchRepo5Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.repo.ExchangeTransMatchRepoCommonService;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.shares.ExchangeTransMatchShares1Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.position.service.PositionRequestServiceFinProductTradeService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;

import static com.joyintech.tams.constant.DictCodeListExt3.*;

/**
 * 场内成交管理共通service
 */
@Service
public class ExchangeTransMatchCommonService extends BaseService {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(ExchangeTransMatchCommonService.class);
    
    @Autowired
    private ExchangeTransMatchCommonMapper exchangeTransMatchCommonMapper;
    
    @Autowired
    private CreditLinesDealsMapper creditLinesDealsMapper;

    @Autowired
    private SettleTransDataTotalMatchMapper settleTransDataTotalMatchMapper;

    @Autowired
    private SettleDataTotalPledgeMatchMapper settleDataTotalPledgeMatchMapper;

    @Autowired
    private SettleTransTgMappingMapper settleTransTgMappingMapper;

    @Autowired
    private SettleTransDataTotalStatusMapper settleTransDataTotalStatusMapper;

    @Autowired
    private SettleTransDataTotalMapper settleTransDataTotalMapper;

    /**
     * 金融产品交易共通业务
     */
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Autowired
    private ExchangeTransMatchBondCommonService exchangeTransMatchBondCommonService;

    @Autowired
    private ExchangeTransMatchBidding1Service exchangeTransMatchBidding1Service;

    @Autowired
    private ExchangeTransMatchCredit1Service exchangeTransMatchCredit1Service;

    @Autowired
    private ExchangeTransMatchBond1Service exchangeTransMatchBond1Service;

    @Autowired
    private ExchangeTransMatchBond2Service exchangeTransMatchBond2Service;

    @Autowired
    private ExchangeTransMatchBond3Service exchangeTransMatchBond3Service;

    @Autowired
    private ExchangeTransMatchBond4Service exchangeTransMatchBond4Service;

    @Autowired
    private ExchangeTransMatchBond5Service exchangeTransMatchBond5Service;
    @Autowired
    private ExchangeTransMatchBond6Service exchangeTransMatchBond6Service;
    @Autowired
    private ExchangeTransMatchRepo1Service exchangeTransMatchRepo1Service;
    @Autowired
    private ExchangeTransMatchRepo2Service exchangeTransMatchRepo2Service;
    @Autowired
    private ExchangeTransMatchRepo3Service exchangeTransMatchRepo3Service;
    @Autowired
    private ExchangeTransMatchRepo4Service exchangeTransMatchRepo4Service;
    @Autowired
    private ExchangeTransMatchRepo5Service exchangeTransMatchRepo5Service;
    @Autowired
    private ExchangeTransMatchShares1Service exchangeTransMatchShares1Service;
    @Autowired
    private ExchangeTransMatchFund1Service exchangeTransMatchFund1Service;
	@Autowired
    private SettleTransDataTotalAddMapper settleTransDataTotalAddMapper;
	@Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
	@Autowired
	private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;
	@Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;
	@Autowired
    private TrdProductDealMapper trdProductDealMapper;
	@Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;
	@Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
	@Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;
    @Autowired
    private TrdFinProductBarterMapper trdFinProductBarterMapper;
    @Autowired
    private TrdTradeFeeTotalMapper trdTradeFeeTotalMapper;
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;
    @Autowired
    private TrdFinProductBarterTotalMapper trdFinProductBarterTotalMapper;
    @Autowired
    private PtlTradeMapper ptlTradeMapper;
    @Autowired
    private TrdStasecFlowMapper trdStasecFlowMapper;
    @Autowired
    private TrdStasecFlowDetailMapper trdStasecFlowDetailMapper;
    @Autowired
    private FinPledgeDisplaceMapper finPledgeDisplaceMapper;
    @Autowired
    private FinPledgeDisplaceDetailMapper finPledgeDisplaceDetailMapper;
    @Autowired
    private FinPledgeDisplaceResultMapper finPledgeDisplaceResultMapper;
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private FinCashInfoMapper finCashInfoMapper;

    @Autowired
    private FinCashPlanMapper finCashPlanMapper;

    @Autowired
    private FinProductTypeMapper finProductTypeMapper;
    @Autowired
    private FinDealPledgeMapper finDealPledgeMapper;

    @Autowired
    private PositionRequestServiceFinProductTradeService positionRequestService;

    public static final String ENTRUST_RECORD_NEW = "new";

    // modify by etliu 20220110 start
    // GZYHTZJY-1032: 协议回购的头寸处理，需要走PTL_POSITION_FLOW_CFG配置
    @Autowired
    private ExchangeTransMatchRepoCommonService exchangeTransMatchRepoCommonService;
    // modify by etliu 20220110 end

    @Value("${application.micorservice.contextpath.tamsServer:http://tams-server}")
    private String tamsSerever;

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextpath.single:false}")
    private boolean isSingle;

    // modify by etliu 20220222 start
    // 广农商二阶段 GNS-931:【广农商场内清算】上/深交所 基金存续期重复跑批，生成数据有误
    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    // modify by etliu 20220222 end

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;
    /**
     * 交易完成后，调用共通修改SETTLE_TRANS_TG_MAPPING，SETTLE_TRANS_DATA_TOTAL_STATUS
     * @param seqId
     * @param tradeId
     */
    public void transactionRecord(String seqId, String tradeId, OperationType type) {
        if(!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
            //登录:成交数据与投管交易关系（SETTLE_TRANS_TG_MAPPING）
            logger.debug("交易完成,调用共通，成交流水号"+ seqId +",交易编号:"+tradeId +",开始");
            SettleTransDataTotal settleTransDataTotal = settleTransDataTotalMapper.selectByPrimaryKey(seqId);
            if(StringUtil.isNotEmpty(settleTransDataTotal)) {
                ExchangeTransMatchCommonDto dto = new ExchangeTransMatchCommonDto();
                BeanUtil.copyPropertiesIgnoreNull(dto,settleTransDataTotal);
                List<SettleTransTgMapping> settleTransTgMappingList = this.genSettleTransTgMapping(dto,tradeId);
                if(!StringUtil.isEmptyList(settleTransTgMappingList)) {
                    for(SettleTransTgMapping settleTransTgMapping:settleTransTgMappingList) {
                        settleTransTgMappingMapper.insertSelective(settleTransTgMapping);
                    }
                }

            //更新:汇总成交数据状态表（SETTLE_TRANS_DATA_TOTAL_STATUS）
            SettleTransDataTotalStatus settleTransDataTotalStatus = settleTransDataTotalStatusMapper.selectByPrimaryKey(dto.getSeqId());
            if(StringUtil.isNotEmpty(settleTransDataTotalStatus)) {
                settleTransDataTotalStatus.setMatchStatus(下行交易匹配状态_匹配完成.getValue());
                settleTransDataTotalStatus.setUpdateTime(new Date());
                settleTransDataTotalStatus.setUpdateUser(StringUtil.isNotEmpty(LoginUserUtil.getUserName())?LoginUserUtil.getUserName():"system");
                settleTransDataTotalStatusMapper.updateByPrimaryKey(settleTransDataTotalStatus);
            } else {
                settleTransDataTotalStatus = new SettleTransDataTotalStatus();
                settleTransDataTotalStatus.setSeqNo(dto.getSeqId());
                settleTransDataTotalStatus.setMatchStatus(下行交易匹配状态_匹配完成.getValue());
                settleTransDataTotalStatus.setUpdateTime(new Date());
                settleTransDataTotalStatus.setUpdateUser(StringUtil.isNotEmpty(LoginUserUtil.getUserName())?LoginUserUtil.getUserName():"system");
                settleTransDataTotalStatusMapper.insert(settleTransDataTotalStatus);
            }
        }
        else {
            logger.debug("没有查询到成交信息,成交流水号:"+ seqId +",交易编号:"+tradeId);
        }

            logger.debug("交易完成,调用共通，成交流水号"+ seqId +",交易编号:"+tradeId +",结束");
        }
        else {
            logger.debug("交易撤销,调用共通,交易编号:"+tradeId +",开始");
            if(StringUtil.endsWith(tradeId,"R")) {
                tradeId = StringUtil.substring(tradeId,0,tradeId.length()-1);
            }
            String totalSeqId = exchangeTransMatchCommonMapper.getTotalSeqId(tradeId);
            if(StringUtil.isNotEmpty(totalSeqId)) {
                //如果存在委托代码，需要删除汇总成交数据匹配结果表数据
                String entrunstId = exchangeTransMatchCommonMapper.getEntrunstId(tradeId);
                if(StringUtil.isNotEmpty(entrunstId)) {
                    Map<String, String> paraMap = new HashMap<>();
                    paraMap.put("totalSeqId",totalSeqId);
                    paraMap.put("entrunstId",entrunstId);
                    exchangeTransMatchCommonMapper.deleteSettleTransDataTotalMatch(paraMap);
                }

                //更新汇总成交数据状态表
                SettleTransDataTotalStatus settleTransDataTotalStatus = settleTransDataTotalStatusMapper.selectByPrimaryKey(totalSeqId);
                if(StringUtil.isNotEmpty(settleTransDataTotalStatus)) {
                    //取得剩余匹配份额
                    BigDecimal remainNum = exchangeTransMatchCommonMapper.getRemainNum(totalSeqId);
                    settleTransDataTotalStatus.setMatchStatus(BigDecimal.ZERO.compareTo(remainNum) >=0
                            ?下行交易匹配状态_未匹配.getValue():下行交易匹配状态_部分匹配.getValue());
                    settleTransDataTotalStatus.setUpdateTime(new Date());
                    settleTransDataTotalStatus.setUpdateUser(StringUtil.isNotEmpty(LoginUserUtil.getUserName())?LoginUserUtil.getUserName():"system");
                    settleTransDataTotalStatusMapper.updateByPrimaryKey(settleTransDataTotalStatus);
                }
            }

            //成交数据与投管交易关系（SETTLE_TRANS_TG_MAPPING）：删除
            exchangeTransMatchCommonMapper.deleteSettleTransTgMapping(tradeId);
            logger.debug("交易撤销,调用共通,交易编号:"+tradeId +",结束");

        }

    }

    /**
     * 批量处理成交匹配数据
     * @param dtoList
     */
    public void transactionMacthBatch(List<ExchangeTransMatchCommonDto> dtoList){
        logger.debug("批量处理成交匹配数据---开始，批量成交数据:"+ JsonUtils.objectArray2Json(dtoList));
        for(ExchangeTransMatchCommonDto dto:dtoList) {
            this.transactionMacth(dto);
        }
        logger.debug("批量处理成交匹配数据---结束");
    }

    /**
     * 处理成交匹配数据
     * @param dto
     */
    public void transactionMacth(ExchangeTransMatchCommonDto dto) {
        logger.debug("处理成交匹配数据---开始，成交数据:"+ JsonUtils.object2Json(dto));
        //查询汇总成交数据附属表
        dto.setSettleTransDataTotalAdd(settleTransDataTotalAddMapper.selectByPrimaryKey(dto.getSeqId()));
        //查询质押品信息
        Map<String,Object> queryMap = new HashMap<>();
        queryMap.put("tradeId",dto.getSeqId());
        dto.setSettleTransDataTotalPledgeList(exchangeTransMatchCommonMapper.getSettleTransDataTotalPledgeList(queryMap));
        //查询匹配的委托信息list
        Map<String,Object> entrustMap = new HashMap<>();
        entrustMap.put("entrustIdList",dto.getEntrustIdList());
        entrustMap.put("ENTRUST_STATUS_03",DictCodeList.委托状态_已接收.getValue());
        entrustMap.put("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        entrustMap.put("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        entrustMap.put("PS_T93", 交易方向_解除质押.getValue());
        entrustMap.put("PS_T94", 交易方向_中途换券.getValue());
        entrustMap.put("TARGET_CLASS_01", 目标类型_数量.getValue());
        entrustMap.put("TARGET_CLASS_02", 目标类型_金额.getValue());
		entrustMap.put("DISPLACE_DIRECTION_01", 质押券置换_置换方向_换入.getValue());
        entrustMap.put("DISPLACE_DIRECTION_02", 质押券置换_置换方向_换出.getValue());


        List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoList;
        if(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue().equals(dto.getFinprodType2())){
            entrustMap.put("SEC_ACCT_ID", dto.getSecAcctId());
            commonEntrustDtoList = exchangeTransMatchCommonMapper.getFirstBondEtrEntrustByIdList(entrustMap);
        }else{
            entrustMap.put("ENTRUST_TYPE_21", 上农商_指令业务类型_协议回购中途换券指令.getValue());
            entrustMap.put("ENTRUST_TYPE_22", 上农商_指令业务类型_协议回购解除质押指令.getValue());
            commonEntrustDtoList = exchangeTransMatchCommonMapper.getEtrEntrustByIdList(entrustMap);
        }
        if(!StringUtil.isEmptyList(commonEntrustDtoList)) {
            logger.debug("处理成交匹配数据---处理份额，金额及费用---开始");
            logger.debug("处理成交匹配数据 commonEntrustDtoList => 【{}】", JsonUtils.object2Json(commonEntrustDtoList));
            String matchStatus = 下行交易匹配状态_未匹配.getValue();
            // ZGCPCN-1694:方法内部对list进行clear操作,须通过返回list对象进行实现 modify by fuyongsheng 20220624 start
            Pair<String, List<ExchangeTransMatchCommonEntrustDto>> pair;
            if((this.isRepo(dto.getFinprodType2()))
                && StringUtil.equals(dto.getSettleTransDataTotalAdd().getRepoType(),DictCodeList.回购类型_质押式协议回购.getValue())){
                 pair = this.calcAgreRepShareAndAmount(dto,commonEntrustDtoList,matchStatus);
            }
            else {
                 pair = this.calcShareAndAmount(dto,commonEntrustDtoList,matchStatus);
            }
            logger.debug("处理成交匹配数据---处理份额，金额及费用---结束");
            logger.debug("处理成交匹配数据 commonEntrustDtoList => 【{}】， matchStatus = 【{}】",
                    JsonUtils.object2Json(commonEntrustDtoList),
                    SysDataUtil.getNameFromDataDict(DictCode.下行交易匹配状态, pair.getFirst()));
            // ZGCPCN-1694:方法内部对list进行clear操作,须通过返回list对象进行实现 modify by fuyongsheng 20220624 end
            matchStatus = pair.getFirst();
            commonEntrustDtoList = pair.getSecond();
            // 处理多组合系列户指令的组合分配
            logger.debug("处理成交匹配数据---处理多组合系列户指令的组合分配---开始");
            this.handleMultiPortfolioAllocation(dto, commonEntrustDtoList);
            logger.debug("处理成交匹配数据---处理多组合系列户指令的组合分配---结束");

            //处理匹配成交相关的表
            this.handleSettleTransTableInfo(dto,commonEntrustDtoList,matchStatus);
        }
        logger.debug("处理成交匹配数据---结束");
    }

    /**
     * 处理多组合系列户指令的组合分配
     * @param commonEntrustDtoList
     */
    private void handleMultiPortfolioAllocation(ExchangeTransMatchCommonDto matchDto, List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoList){
        //系列户指令处理组合分配
        commonEntrustDtoList.stream()
                .filter(item->StringUtil.equals(item.getEntrustClass(),投资指令类型_多组合单资产指令.getValue()))
                .forEach(item->{
            // 获取指令明细信息
            List<ExchangeTransMatchCommonEntrustDetailDto> entrustDetailDtoList = exchangeTransMatchCommonMapper.getEntrustDetailList(item.getEntrustId());
            if(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue().equals(item.getFinprodType2())){
                entrustDetailDtoList = entrustDetailDtoList.stream()
                        .filter(dto -> dto.getSecAcctId().equals(item.getSecAcctId()))
                        .collect(Collectors.toList());
            }
            if(CollectionUtils.isNotEmpty(entrustDetailDtoList) && entrustDetailDtoList.size() > 1) {
                // 获取最小交易单元
                MinTrdUnitLimitDto minTrdUnitLimitDto = this.getMinTrdUnitLimit(item);
                // 获取各组合历史已分配指令数量
                Map<String, BigDecimal> portfolioHisMatchEnMap = this.getPortfolioHisMatchEnInfo(item);
                // 计算目前各组合剩余分配数量
                Map<String, BigDecimal> portfolioRemainQtyMap = this.portfolioRemainQtyInfo(portfolioHisMatchEnMap, entrustDetailDtoList, matchDto);
                // 本次剩余分配总数
                BigDecimal remainMatchQty = item.getMarchQty();
                // 本次各组合分配数量
                Map<String, BigDecimal> portfolioMatchQtyMap = new HashMap<>();
                // 1、按目前各组合剩余分配数量,降序排列
                List<Map.Entry<String, BigDecimal>> portfolioRemainQtyList = new ArrayList<>(portfolioRemainQtyMap.entrySet());
                portfolioRemainQtyList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                // 2、循环第一轮
                for(Map.Entry<String, BigDecimal> portfolioEntry : portfolioRemainQtyList){
                    // 判断最小交易数量是否超过该组合剩余分配数量
                    if(minTrdUnitLimitDto.getMinNum().compareTo(portfolioEntry.getValue()) > 0){
                        // 如最小交易数量超过该组合剩余分配数量, 则判断本次剩余分配总数 是否超过 组合剩余分配数量
                        if(remainMatchQty.compareTo(portfolioEntry.getValue()) >= 0){
                            // 本次剩余分配总数 大于等于 组合剩余分配数量,则分配组合剩余分配数量,更新本次剩余分配总数
                            portfolioMatchQtyMap.put(portfolioEntry.getKey(), portfolioEntry.getValue());
                            remainMatchQty = ArithmeticUtil.subtract(remainMatchQty, portfolioEntry.getValue());
                        }else{
                            // 本次剩余分配总数 小于 组合剩余分配数量,则分配 本次剩余分配总数, 本次剩余分配总数置为0,跳出循环
                            portfolioMatchQtyMap.put(portfolioEntry.getKey(), remainMatchQty);
                            remainMatchQty = BigDecimal.ZERO;
                            break;
                        }
                    }
                    else{
                        // 本次剩余分配总数 大于等于 最小交易数量,则分配 最小交易数量,更新本次剩余分配总数
                        if(remainMatchQty.compareTo(minTrdUnitLimitDto.getMinNum()) >= 0){
                            portfolioMatchQtyMap.put(portfolioEntry.getKey(), minTrdUnitLimitDto.getMinNum());
                            remainMatchQty = ArithmeticUtil.subtract(remainMatchQty, minTrdUnitLimitDto.getMinNum());
                        }else{
                            // 本次剩余分配总数 小于 最小交易数量,则分配 本次剩余分配总数, 本次剩余分配总数置为0,跳出循环
                            portfolioMatchQtyMap.put(portfolioEntry.getKey(), remainMatchQty);
                            remainMatchQty = BigDecimal.ZERO;
                            break;
                        }
                    }
                }
                logger.debug("【指令编号:{},证券账户:{},循环一轮后 portfolioMatchQtyMap:{}, 剩余数量为:{}】", item.getEntrustId(),
                        item.getSecAcctId(), JSON.toJSONString(portfolioMatchQtyMap), remainMatchQty);
                // 3、如循环一轮后仍有剩余数量,则按照 各组合剩余分配数量比例 进行拆分,拆分结果需满足最小交易单元的整数倍
                if(remainMatchQty.compareTo(BigDecimal.ZERO) > 0){
                    // 计算目前各组合剩余分配数量
                    Map<String, BigDecimal> secondPortfolioRemainQtyMap = this.portfolioRemainQtyInfo(portfolioMatchQtyMap,
                            portfolioHisMatchEnMap, entrustDetailDtoList);
                    List<Map.Entry<String, BigDecimal>> secondPortfolioRemainQtyList = new ArrayList<>(secondPortfolioRemainQtyMap.entrySet());
                    secondPortfolioRemainQtyList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                    // 目前各组合剩余分配数量总数
                    BigDecimal portfolioRemainQtyTotal = secondPortfolioRemainQtyMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal minStep = minTrdUnitLimitDto.getMinStep();
                    // 按比例拆分分配总数
                    BigDecimal rateMatchQtyTotal = BigDecimal.ZERO;
                    for (Map.Entry<String, BigDecimal> portfolioEntry : secondPortfolioRemainQtyList) {
                        BigDecimal portfolioRemainQty =  portfolioEntry.getValue();
                        // 临时分配数量
                        BigDecimal matchQtyTemp = ArithmeticUtil.divide(ArithmeticUtil.multiply(remainMatchQty, portfolioRemainQty),
                                portfolioRemainQtyTotal, 4);
                        // 最小交易单元整数倍
                        BigDecimal stepTimes = ArithmeticUtil.divide(matchQtyTemp, minStep, 0, RoundingMode.FLOOR);
                        // 分配数量
                        BigDecimal matchQty = ArithmeticUtil.multiply(minStep, stepTimes);
                        // 合并分配结果
                        portfolioMatchQtyMap.put(portfolioEntry.getKey(), ArithmeticUtil.doAdd(matchQty,
                                portfolioMatchQtyMap.get(portfolioEntry.getKey())));
                        rateMatchQtyTotal = ArithmeticUtil.doAdd(rateMatchQtyTotal, matchQty);
                    }
                    // ZGCPCN-1579:计算比例拆分之后剩余分配数量 modify by fuyongsheng 20220621 start
                    // 按比例拆分之后剩余分配数量
                    remainMatchQty = ArithmeticUtil.subtract(remainMatchQty, rateMatchQtyTotal);
                    // ZGCPCN-1579:计算比例拆分之后剩余分配数量 modify by fuyongsheng 20220621 end
                }
                logger.debug("【指令编号:{},证券账户:{},比例拆分完后 portfolioMatchQtyMap:{}, 剩余尾差数量为:{}】", item.getEntrustId(),
                        item.getSecAcctId(), JSON.toJSONString(portfolioMatchQtyMap), remainMatchQty);

                // 4、如按比例拆分完,仍有剩余数量尾差,则按照最小交易单元按顺序依次分配,直至分配完为止
                if(remainMatchQty.compareTo(BigDecimal.ZERO) > 0){
                    // 计算目前各组合剩余分配数量
                    Map<String, BigDecimal> thirdPortfolioRemainQtyMap = this.portfolioRemainQtyInfo(portfolioMatchQtyMap,
                            portfolioHisMatchEnMap, entrustDetailDtoList);
                    List<Map.Entry<String, BigDecimal>> thirdPortfolioRemainQtyList = new ArrayList<>(thirdPortfolioRemainQtyMap.entrySet());
                    thirdPortfolioRemainQtyList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                    // 尾差分配数量Map
                    Map<String, BigDecimal> tailMatchQtyMap = new HashMap<>();
                    for (Map.Entry<String, BigDecimal> portfolioEntry : thirdPortfolioRemainQtyList) {
                        String portfolioId = portfolioEntry.getKey();
                        // 分配数量
                        BigDecimal matchQty = minTrdUnitLimitDto.getMinStep();
                        // 剩余分配数量
                        remainMatchQty = ArithmeticUtil.subtract(remainMatchQty, matchQty);
                        tailMatchQtyMap.put(portfolioId, ArithmeticUtil.doAdd(matchQty, tailMatchQtyMap.get(portfolioId)));
                        if(remainMatchQty.compareTo(BigDecimal.ZERO) <= 0){
                            break;
                        }
                    }
                    for(String portfolioId : tailMatchQtyMap.keySet()){
                        // 合并尾差分配结果
                        portfolioMatchQtyMap.put(portfolioId, ArithmeticUtil.doAdd(tailMatchQtyMap.get(portfolioId),
                                portfolioMatchQtyMap.get(portfolioId)));
                    }
                }
                logger.debug("【指令编号:{},证券账户:{},最终分配数量结果为:{}】", item.getEntrustId(), item.getSecAcctId(), JSON.toJSONString(portfolioMatchQtyMap));
                // 5、根据本次分配数量计算 分配金额和交易支付费用
                this.calculatePortFolioMatchAmt(matchDto, item, portfolioMatchQtyMap, portfolioRemainQtyList, entrustDetailDtoList, item.getMarchAmt(), this.multiply(item.getMarchQty(), matchDto.getUnitCprice()));
            }
            logger.debug("【指令编号:{},证券账户:{},最终结果为:{}】", item.getEntrustId(), item.getSecAcctId(), JSON.toJSONString(entrustDetailDtoList));
            // ZGCPCN-1551:modify by fuyongsheng 20220620 start
            item.setEntrustDetailDtoList(entrustDetailDtoList);
            // ZGCPCN-1551:modify by fuyongsheng 20220620 end
        });
    }

    /**
     * 计算组合分配的金额和交易支付费用
     * @param matchDto
     * @param entrust
     * @param portfolioMatchQtyMap
     * @param portfolioRemainQtyList
     * @param entrustDetailDtoList
     */
    private void calculatePortFolioMatchAmt(ExchangeTransMatchCommonDto matchDto, ExchangeTransMatchCommonEntrustDto entrust,
                    Map<String, BigDecimal> portfolioMatchQtyMap, List<Map.Entry<String, BigDecimal>> portfolioRemainQtyList,
                                            List<ExchangeTransMatchCommonEntrustDetailDto> entrustDetailDtoList, BigDecimal marchAmt, BigDecimal mrachCpriceAmt){
        // 本次各组合分配金额
        Map<String, BigDecimal> portfolioMatchAmtMap = new HashMap<>();
        // 本次各组合分配净值金额
        Map<String, BigDecimal> portfolioMatchCpriceAmtMap = new HashMap<>();
        // 本次各组合分配随交易支付费用
        Map<String, BigDecimal> portfolioMatchFeeAmtPayMap = new HashMap<>();
        // 本次各组合分配不随交易支付费用
        Map<String, BigDecimal> portfolioMatchFeeAmtUnPayMap = new HashMap<>();

        //系列户指令组合已分配总随交易支付费用
        BigDecimal marchTotalFeeAmtPay = BigDecimal.ZERO;
        //系列户指令组合已分配总不随交易支付费用
        BigDecimal marchTotalFeeAmtUnPay = BigDecimal.ZERO;
        int length = portfolioRemainQtyList.size();
        for(int index=0; index<length; index++){
            Map.Entry<String, BigDecimal> portfolioEntry = portfolioRemainQtyList.get(index);
            String portfolioId = portfolioEntry.getKey();
            BigDecimal matchQty = portfolioMatchQtyMap.get(portfolioId);

            if (this.isBond(entrust.getFinprodType2())) {
                if (this.isPledgeCouponsInOrOut(entrust.getPs())) {
                    portfolioMatchAmtMap.put(portfolioId, this.multiplyWithScale2(matchQty, entrust.getPrice()));
                }
                // 债转股不重新计算分配金额
                else if (this.isBond2Stock(entrust.getPs())) {
                    portfolioMatchAmtMap.put(portfolioId, matchDto.getTradeAmt());
                }
                else {
                    portfolioMatchAmtMap.put(portfolioId, this.multiplyWithScale2(matchQty,matchDto.getUnitFprice()));
                }
            }
            else if (this.isRepo(matchDto.getFinprodType2())) {
                portfolioMatchAmtMap.put(portfolioId, matchQty);
            }
            else {
                portfolioMatchAmtMap.put(portfolioId, this.multiplyWithScale2(matchQty,matchDto.getUnitCprice()));
            }

            if (this.isRepo(matchDto.getFinprodType2())) {
                portfolioMatchCpriceAmtMap.put(portfolioId, matchQty);
            } else {
                portfolioMatchCpriceAmtMap.put(portfolioId, this.multiplyWithScale2(matchQty, matchDto.getUnitCprice()));
            }

            if (Objects.nonNull(entrust.getMarchFeeAmtPay())) {
                BigDecimal matchFeeAmtPay = this.divideWithScale2(this.multiply(entrust.getMarchFeeAmtPay(), matchQty), entrust.getMarchQty());
                marchTotalFeeAmtPay = ArithmeticUtil.doAdd(marchTotalFeeAmtPay, matchFeeAmtPay);
                portfolioMatchFeeAmtPayMap.put(portfolioId, matchFeeAmtPay);
            }

            if (Objects.nonNull(entrust.getMarchFeeAmtUnPay())) {
                BigDecimal matchFeeAmtUnPay = this.divideWithScale2(this.multiply(entrust.getMarchFeeAmtUnPay(), matchQty), entrust.getMarchQty());
                marchTotalFeeAmtUnPay = ArithmeticUtil.doAdd(marchTotalFeeAmtUnPay, matchFeeAmtUnPay);
                portfolioMatchFeeAmtUnPayMap.put(portfolioId, matchFeeAmtUnPay);
            }
        }
        // 将尾插分配到组合开始日最小的组合上
        String minVdatePortfolioId = this.getPortfolioIdByMinVdate(Lists.newArrayList(portfolioMatchQtyMap.keySet()));
        logger.debug("尾插分配在组合 => 【{}】", minVdatePortfolioId);

        BigDecimal reduce = portfolioMatchAmtMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        portfolioMatchAmtMap.put(minVdatePortfolioId, marchAmt.subtract(reduce).add(portfolioMatchAmtMap.get(minVdatePortfolioId)));

        BigDecimal reduceCprice = portfolioMatchCpriceAmtMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        portfolioMatchCpriceAmtMap.put(minVdatePortfolioId, mrachCpriceAmt.subtract(reduceCprice).add(portfolioMatchCpriceAmtMap.get(minVdatePortfolioId)));

        if (Objects.nonNull(entrust.getMarchFeeAmtPay())) {
            BigDecimal reducePay = portfolioMatchFeeAmtPayMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            portfolioMatchFeeAmtPayMap.put(minVdatePortfolioId, entrust.getMarchFeeAmtPay().subtract(reducePay).add(portfolioMatchFeeAmtPayMap.get(minVdatePortfolioId)));
        }

        if (Objects.nonNull(entrust.getMarchFeeAmtUnPay())) {
            BigDecimal reduceUnPay = portfolioMatchFeeAmtUnPayMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            portfolioMatchFeeAmtUnPayMap.put(minVdatePortfolioId, entrust.getMarchFeeAmtUnPay().subtract(reduceUnPay).add(portfolioMatchFeeAmtUnPayMap.get(minVdatePortfolioId)));
        }

        for(ExchangeTransMatchCommonEntrustDetailDto entrustDetailDto : entrustDetailDtoList) {
            entrustDetailDto.setMarchQty(this.getValueOrZero(portfolioMatchQtyMap.get(entrustDetailDto.getPortfolioId())));
            entrustDetailDto.setMarchAmt(this.getValueOrZero(portfolioMatchAmtMap.get(entrustDetailDto.getPortfolioId())));
            entrustDetailDto.setMarchCpriceAmt(this.getValueOrZero(portfolioMatchCpriceAmtMap.get(entrustDetailDto.getPortfolioId())));
            entrustDetailDto.setMarchFeeAmtPay(this.getValueOrZero(portfolioMatchFeeAmtPayMap.get(entrustDetailDto.getPortfolioId())));
            entrustDetailDto.setMarchFeeAmtUnPay(this.getValueOrZero(portfolioMatchFeeAmtUnPayMap.get(entrustDetailDto.getPortfolioId())));
        }
    }

    private BigDecimal getValueOrZero(BigDecimal value) {
        if (Objects.isNull(value)) {
            return BigDecimal.ZERO;
        }
        return value;
    }

    /**
     * 获取指令的最小交易单元
     * @param entrustDto
     * @return
     */
    private MinTrdUnitLimitDto getMinTrdUnitLimit(ExchangeTransMatchCommonEntrustDto entrustDto){
        String tradePlat = StringUtil.EMPTY;
        if(交易平台_新债平台.getValue().equals(entrustDto.getTradePlat())){
            tradePlat = StringUtil.isNotEmpty(entrustDto.getDealMode()) ? entrustDto.getDealMode() : 指定交易方式_协商成交.getValue();
        }else{
            tradePlat = entrustDto.getTradePlat();
        }
        String finprodId = DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue().equals(entrustDto.getFinprodType2())
                ? StringUtil.EMPTY : entrustDto.getFinprodId();
        MinTrdUnitLimitDto minTrdUnitLimitDto = trdCommonBaseService.getMinTrdUnitLimit(finprodId, tradePlat,
                entrustDto.getTradeMarket(), entrustDto.getFinprodType2(),entrustDto.getPs());
        if(minTrdUnitLimitDto == null){
            minTrdUnitLimitDto = new MinTrdUnitLimitDto();
            minTrdUnitLimitDto.setMinStep(BigDecimal.ONE);
            minTrdUnitLimitDto.setMinNum(BigDecimal.ONE);
        }
        return minTrdUnitLimitDto;
    }

    /**
     * 查询各组合历史已分配指令数量信息
     * @param dto
     * @return
     */
    private Map<String, BigDecimal> getPortfolioHisMatchEnInfo(ExchangeTransMatchCommonEntrustDto dto){
        Map<String, BigDecimal> result = new HashMap<>();
        List<TrdProductDeal> trdProductDeals;
        if(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue().equals(dto.getFinprodType2())){
            trdProductDeals = exchangeTransMatchCommonMapper.getFirstBondTrdProductDeals(dto.getEntrustId(), dto.getSecAcctId());
        }else{
            trdProductDeals = exchangeTransMatchCommonMapper.getTrdProductDealsByEntrustId(dto.getEntrustId());
        }
        if(CollectionUtils.isEmpty(trdProductDeals)){
            return result;
        }
        Map<String, List<TrdProductDeal>> trdProductDealMap = trdProductDeals.stream().collect(Collectors.groupingBy(
                TrdProductDeal::getPortfolioId));
        for(String portfolioId : trdProductDealMap.keySet()){
            List<TrdProductDeal> trdProductDealsTemp = trdProductDealMap.get(portfolioId);
            result.put(portfolioId, trdProductDealsTemp.stream().map(ele -> null == ele.getShareAmt()
                    ? BigDecimal.ZERO : ele.getShareAmt()).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return result;
    }

    /**
     * 计算各组合剩余分配数量信息
     * @param portfolioHisMatchEnMap
     * @param entrustDetailDtoList
     * @return
     */
    private Map<String, BigDecimal> portfolioRemainQtyInfo(Map<String, BigDecimal> portfolioHisMatchEnMap,
                                                           List<ExchangeTransMatchCommonEntrustDetailDto> entrustDetailDtoList,
                                                           ExchangeTransMatchCommonDto matchDto){
        Map<String, BigDecimal> result = new HashMap<>();
        for(ExchangeTransMatchCommonEntrustDetailDto entrustDetailDto : entrustDetailDtoList){
            // 组合已分配数量
            BigDecimal matchQtyEn = portfolioHisMatchEnMap.get(entrustDetailDto.getPortfolioId());
            result.put(entrustDetailDto.getPortfolioId(), ArithmeticUtil.subtract(this.isRepo(matchDto.getFinprodType2())? this.multiply100(entrustDetailDto.getEntrustNum()):entrustDetailDto.getEntrustNum(), matchQtyEn));
        }
        return result;
    }

    /**
     * 计算各组合剩余分配数量信息
     * @param portfolioMatchQtyMap
     * @param portfolioHisMatchEnMap
     * @param entrustDetailDtoList
     * @return
     */
    private Map<String, BigDecimal> portfolioRemainQtyInfo(Map<String, BigDecimal> portfolioMatchQtyMap,
                                                           Map<String, BigDecimal> portfolioHisMatchEnMap,
                                                           List<ExchangeTransMatchCommonEntrustDetailDto> entrustDetailDtoList){
        Map<String, BigDecimal> result = new HashMap<>();
        for(ExchangeTransMatchCommonEntrustDetailDto entrustDetailDto : entrustDetailDtoList){
            // 组合的历史已分配数量
            BigDecimal hisMatchQtyEn = portfolioHisMatchEnMap.get(entrustDetailDto.getPortfolioId());
            // 组合的本次已分配数量
            BigDecimal currentMatchQtyEn = portfolioMatchQtyMap.get(entrustDetailDto.getPortfolioId());
            result.put(entrustDetailDto.getPortfolioId(), ArithmeticUtil.subtract(
                    ArithmeticUtil.subtract(entrustDetailDto.getEntrustNum(), hisMatchQtyEn), currentMatchQtyEn));
        }
        return result;
    }

    /**
     * 处理匹配成交相关的表
     * @param dto
     * @param commonEntrustDtoList
     * @param matchStatus
     */
    private void handleSettleTransTableInfo(ExchangeTransMatchCommonDto dto,List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoList,String matchStatus) {
        logger.debug("处理匹配成交相关的表---开始,委托list:"+JsonUtils.objectArray2Json(commonEntrustDtoList));
        for(ExchangeTransMatchCommonEntrustDto commonEntrustDto : commonEntrustDtoList) {
            //处理业务交易表
            String tradeId = this.handleFinProductTradeDto(dto,commonEntrustDto);
            //登录:汇总成交数据匹配结果表（SETTLE_TRANS_DATA_TOTAL_MATCH）
            SettleTransDataTotalMatch settleTransDataTotalMatch = this.genSettleTransDataTotalMatch(dto,commonEntrustDto);
            // 保存SETTLE_TRANS_DATA_TOTAL_MATCH
            this.saveSettleTransDataTotalMatch(settleTransDataTotalMatch);

            //质押式协议回购需要处理 汇总成交质押品匹配结果表
            if((StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                    || StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()))
                    && StringUtil.equals(dto.getSettleTransDataTotalAdd().getRepoType(),DictCodeList.回购类型_质押式协议回购.getValue())){
                // 生成SETTLE_DATA_TOTAL_PLEDGE_MATCH
                List<SettleDataTotalPledgeMatch> settleDataTotalPledgeMatchList = this.genSettleDataTotalPledgeMatch(dto,commonEntrustDto);
                // 保存SETTLE_DATA_TOTAL_PLEDGE_MATCH
                this.saveSettleDataTotalPledgeMatch(settleDataTotalPledgeMatchList, settleTransDataTotalMatch);
            }

            //登录:成交数据与投管交易关系（SETTLE_TRANS_TG_MAPPING）
            List<SettleTransTgMapping> settleTransTgMappingList = this.genSettleTransTgMapping(dto,tradeId);
            // 保存SETTLE_TRANS_TG_MAPPING
            this.saveSettleTransTgMapping(settleTransTgMappingList);
        }

        //更新:汇总成交数据状态表（SETTLE_TRANS_DATA_TOTAL_STATUS）
        this.updateSettleTransDataTotalStatus(dto, matchStatus);

        logger.debug("处理匹配成交相关的表---结束");
    }

    /**
     * 更新:汇总成交数据状态表（SETTLE_TRANS_DATA_TOTAL_STATUS）
     * @param dto
     * @param matchStatus
     */
    private void updateSettleTransDataTotalStatus(ExchangeTransMatchCommonDto dto, String matchStatus){
        SettleTransDataTotalStatus settleTransDataTotalStatus = settleTransDataTotalStatusMapper.selectByPrimaryKey(dto.getSeqId());
        if(StringUtil.isNotEmpty(settleTransDataTotalStatus)) {
            settleTransDataTotalStatus.setMatchStatus(matchStatus);
            settleTransDataTotalStatus.setUpdateTime(new Date());
            settleTransDataTotalStatus.setUpdateUser(StringUtil.isNotEmpty(LoginUserUtil.getUserName())?LoginUserUtil.getUserName():"system");
            settleTransDataTotalStatusMapper.updateByPrimaryKey(settleTransDataTotalStatus);
        } else {
            settleTransDataTotalStatus = new SettleTransDataTotalStatus();
            settleTransDataTotalStatus.setSeqNo(dto.getSeqId());
            settleTransDataTotalStatus.setMatchStatus(matchStatus);
            settleTransDataTotalStatusMapper.insert(settleTransDataTotalStatus);
        }
    }

    /**
     * 保存settleTransDataTotalMatch
     * @param settleTransDataTotalMatch
     */
    private void saveSettleTransDataTotalMatch(SettleTransDataTotalMatch settleTransDataTotalMatch){
        // ZGTFCP-931:手动匹配存在多次匹配需要更新匹配数据而不是重新插入 modify fuyongsheng 20220319 start
        if(下行交易匹配类型_自动匹配.getValue().equals(settleTransDataTotalMatch.getMatchClass())){
            // modify by etliu 20220220 start
            // GNS-928:【广农商场内清算】深交所竞价首期解析SJSMX1文件；重复跑批时，结算下行交易汇总批处理报错
            try {
                settleTransDataTotalMatchMapper.insertSelective(settleTransDataTotalMatch);
            } catch (Exception e) {
                settleTransDataTotalMatchMapper.deleteByPrimaryKey(settleTransDataTotalMatch);
                settleTransDataTotalMatchMapper.insertSelective(settleTransDataTotalMatch);
            }
            // modify by etliu 20220220 end
        }else{ // 手动匹配
            SettleTransDataTotalMatchKey matchKey = new SettleTransDataTotalMatchKey();
            matchKey.setSeqNo(settleTransDataTotalMatch.getSeqNo());
            matchKey.setEntrunstTradeId(settleTransDataTotalMatch.getEntrunstTradeId());
            matchKey.setMatchClass(settleTransDataTotalMatch.getMatchClass());
            SettleTransDataTotalMatch record = settleTransDataTotalMatchMapper.selectByPrimaryKey(matchKey);
            if(record != null){
                record.setMarchAmt(ArithmeticUtil.doAdd(record.getMarchAmt(), settleTransDataTotalMatch.getMarchAmt()));
                record.setMarchAmtEn(ArithmeticUtil.doAdd(record.getMarchAmtEn(), settleTransDataTotalMatch.getMarchAmtEn()));
                record.setMarchQty(ArithmeticUtil.doAdd(record.getMarchQty(), settleTransDataTotalMatch.getMarchQty()));
                record.setMarchQtyEn(ArithmeticUtil.doAdd(record.getMarchQtyEn(), settleTransDataTotalMatch.getMarchQtyEn()));
                settleTransDataTotalMatchMapper.updateByPrimaryKeySelective(record);
            }else{
                settleTransDataTotalMatchMapper.insertSelective(settleTransDataTotalMatch);
            }
        }
        // ZGTFCP-931:手动匹配存在多次匹配需要更新匹配数据而不是重新插入 modify fuyongsheng 20220319 end
    }

    /**
     * 保存SettleDataTotalPledgeMatch
     * @param settleDataTotalPledgeMatchList
     * @param settleTransDataTotalMatch
     */
    private void saveSettleDataTotalPledgeMatch(List<SettleDataTotalPledgeMatch> settleDataTotalPledgeMatchList,
                                                SettleTransDataTotalMatch settleTransDataTotalMatch){
        for (SettleDataTotalPledgeMatch settleDataTotalPledgeMatch:settleDataTotalPledgeMatchList) {
            // ZGTFCP-931:手动匹配存在多次匹配需要更新匹配数据而不是重新插入 modify fuyongsheng 20220319 start
            if(下行交易匹配类型_自动匹配.getValue().equals(settleTransDataTotalMatch.getMatchClass())){
                try {
                    settleDataTotalPledgeMatchMapper.insertSelective(settleDataTotalPledgeMatch);
                } catch (Exception e) {
                    settleDataTotalPledgeMatchMapper.deleteByPrimaryKey(settleDataTotalPledgeMatch);
                    settleDataTotalPledgeMatchMapper.insertSelective(settleDataTotalPledgeMatch);
                }
            }else{ // 手动匹配
                SettleDataTotalPledgeMatchKey matchKey = new SettleDataTotalPledgeMatchKey();
                matchKey.setSeqNo(settleDataTotalPledgeMatch.getSeqNo());
                matchKey.setMatchClass(settleDataTotalPledgeMatch.getMatchClass());
                matchKey.setBranch(settleDataTotalPledgeMatch.getBranch());
                matchKey.setPledgeFinprodId(settleDataTotalPledgeMatch.getPledgeFinprodId());
                matchKey.setEntrunstTradeId(settleDataTotalPledgeMatch.getEntrunstTradeId());
                SettleDataTotalPledgeMatch record = settleDataTotalPledgeMatchMapper.selectByPrimaryKey(matchKey);
                if(record != null){
                    record.setMarchAmt(ArithmeticUtil.doAdd(record.getMarchAmt(), settleDataTotalPledgeMatch.getMarchAmt()));
                    record.setMarchAmtEn(ArithmeticUtil.doAdd(record.getMarchAmtEn(), settleDataTotalPledgeMatch.getMarchAmtEn()));
                    record.setMarchQty(ArithmeticUtil.doAdd(record.getMarchQty(), settleDataTotalPledgeMatch.getMarchQty()));
                    record.setMarchQtyEn(ArithmeticUtil.doAdd(record.getMarchQtyEn(), settleDataTotalPledgeMatch.getMarchQtyEn()));
                    settleDataTotalPledgeMatchMapper.updateByPrimaryKeySelective(record);
                }else{
                    settleDataTotalPledgeMatchMapper.insertSelective(settleDataTotalPledgeMatch);
                }
            }
            // ZGTFCP-931:手动匹配存在多次匹配需要更新匹配数据而不是重新插入 modify fuyongsheng 20220319 end
        }
    }

    private String handleFinProductTradeDto(ExchangeTransMatchCommonDto dto,ExchangeTransMatchCommonEntrustDto commonEntrustDto){
        logger.debug("处理业务交易表---开始");
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
        finProductTradeDto.setNeedSum(true);
        //GNS-949:增加是否工作流数据标识 modify by fuyongsheng 20220227 start
        finProductTradeDto.setNotWorkflow(true);
        //GNS-949:增加是否工作流数据标识 modify by fuyongsheng 20220227 end
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        List<ExchangeTransMatchBaseTradeDto> exchangeTransMatchBaseTradeDtos = new ArrayList<>();
        finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);

        String busiType = "";
        if(StringUtil.equals(commonEntrustDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
            if(StringUtil.equals(commonEntrustDto.getPs(),DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(commonEntrustDto.getPs(),DictCodeList.交易方向_卖出.getValue())
        || StringUtil.equals(commonEntrustDto.getPs(),DictCodeList.交易方向_行权_回售.getValue())) {
                List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtList = exchangeTransMatchBond1Service.genFinProductTradeDto(dto,commonEntrustDto);
                exchangeTransMatchBaseTradeDtos.addAll(baseTradeProductDealDtList);
            }
            else if (StringUtil.equals(commonEntrustDto.getPs(), DictCodeList.交易方向_申购.getValue())) {
                List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtList = exchangeTransMatchBond2Service.genFinProductTradeDto(dto,commonEntrustDto);
                exchangeTransMatchBaseTradeDtos.addAll(baseTradeProductDealDtList);
            }
            else if (StringUtil.equals(commonEntrustDto.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
                List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtList = exchangeTransMatchBond3Service.genFinProductTradeDto(dto,commonEntrustDto);
                exchangeTransMatchBaseTradeDtos.addAll(baseTradeProductDealDtList);
            }
            else if(StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt1.交易方向_债转股.getValue())) {
                List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtList = exchangeTransMatchBond4Service.genFinProductTradeDto(dto,commonEntrustDto);
                exchangeTransMatchBaseTradeDtos.addAll(baseTradeProductDealDtList);
            }
            else if(StringUtil.equals(commonEntrustDto.getPs(), 交易方向_配债.getValue())) {
                List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtList = exchangeTransMatchBond5Service.genFinProductTradeDto(dto,commonEntrustDto);
                exchangeTransMatchBaseTradeDtos.addAll(baseTradeProductDealDtList);
            }
            else if (StringUtil.equals(commonEntrustDto.getPs(), 交易方向_质押券入库.getValue())
            || StringUtil.equals(commonEntrustDto.getPs(), 交易方向_质押券出库.getValue())) {
                List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtList = exchangeTransMatchBond6Service.genFinProductTradeDto(dto,commonEntrustDto);
                exchangeTransMatchBaseTradeDtos.addAll(baseTradeProductDealDtList);
            }
        }
        if (StringUtil.equals(commonEntrustDto.getFinprodType2(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())) {
            List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtList = exchangeTransMatchBidding1Service.genFinProductTradeDto(dto,commonEntrustDto);
            exchangeTransMatchBaseTradeDtos.addAll(baseTradeProductDealDtList);
        }
        if (StringUtil.equals(commonEntrustDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue())) {

            List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtList = exchangeTransMatchCredit1Service.genFinProductTradeDto(dto,commonEntrustDto);
            exchangeTransMatchBaseTradeDtos.addAll(baseTradeProductDealDtList);
        }
        else if(StringUtil.equals(commonEntrustDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
        || StringUtil.equals(commonEntrustDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())){
            //竞价回购
            if(!StringUtil.equals(dto.getSettleTransDataTotalAdd().getRepoType(),DictCodeList.回购类型_质押式协议回购.getValue())) {
                List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtList = exchangeTransMatchRepo1Service.genFinProductTradeDto(dto,commonEntrustDto);
                exchangeTransMatchBaseTradeDtos.addAll(baseTradeProductDealDtList);
            }
            //协议回购
            else {
                List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtList = null;
                if(StringUtil.equals(commonEntrustDto.getPs(), 交易方向_中途换券.getValue())
                || StringUtil.equals(commonEntrustDto.getPs(), 交易方向_解除质押.getValue())) {
                    baseTradeProductDealDtList = exchangeTransMatchRepo3Service.genFinProductTradeDto(dto,commonEntrustDto);
                }
                else if (StringUtil.equals(commonEntrustDto.getPs(), 交易方向_到期续做.getValue())){
                    baseTradeProductDealDtList = exchangeTransMatchRepo4Service.genFinProductTradeDto(dto,commonEntrustDto);
                }
                else if (StringUtil.equals(commonEntrustDto.getPs(), 交易方向_提前购回.getValue())
                || StringUtil.equals(commonEntrustDto.getPs(), 交易方向_到期购回.getValue())){
                    baseTradeProductDealDtList = exchangeTransMatchRepo5Service.genFinProductTradeDto(dto,commonEntrustDto);
                }
                else { // 首期
                    baseTradeProductDealDtList = exchangeTransMatchRepo2Service.genFinProductTradeDto(dto,commonEntrustDto);
                }

                exchangeTransMatchBaseTradeDtos.addAll(baseTradeProductDealDtList);
            }

        }
        else if(StringUtil.equals(commonEntrustDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
            List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtList  = exchangeTransMatchFund1Service.genFinProductTradeDto(dto,commonEntrustDto);
            exchangeTransMatchBaseTradeDtos.addAll(baseTradeProductDealDtList);
        }
        else if(StringUtil.equals(commonEntrustDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
            if (StringUtil.equals(commonEntrustDto.getPs(), DictCodeList.交易方向_申购.getValue())
                    || StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt2.交易方向_增发.getValue())
                    || StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt2.交易方向_配股.getValue())
                    || StringUtil.equals(commonEntrustDto.getPs(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(commonEntrustDto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtList = exchangeTransMatchShares1Service.genFinProductTradeDto(dto, commonEntrustDto);
                exchangeTransMatchBaseTradeDtos.addAll(baseTradeProductDealDtList);
            }
        }
        //汇总交易相关信息入库
        this.insertTrdFinProductDealTotalInfo(dto, commonEntrustDto, exchangeTransMatchBaseTradeDtos);
        //交易相关信息入库
        String tradeId = this.insertTrdProductDealInfo(dto, commonEntrustDto, exchangeTransMatchBaseTradeDtos);

        // modify by etliu 20220110 start
        // GZYHTZJY-1032: 协议回购的头寸处理，需要走PTL_POSITION_FLOW_CFG配置
        FinPledgeDisplaceDetail finPledgeDisplaceDetailHuanChu = null;
        FinPledgeDisplaceDetail finPledgeDisplaceDetailHuanRu = null;
        List<FinDealPledge> firstFinDealPledgeTempList = new ArrayList<>();
        // modify by etliu 20220110 end

        //中途换券和解除质押时需要处理 质押券置换和质押券置换结果
        if(StringUtil.equals(commonEntrustDto.getPs(), 交易方向_中途换券.getValue())
                || StringUtil.equals(commonEntrustDto.getPs(), 交易方向_解除质押.getValue())) {
            TrdFinProductDealTotal trdFinProductDealTotal = exchangeTransMatchBaseTradeDtos.get(0).getTrdFinProductDealTotal();
            FinPledgeDisplace finPledgeDisplace = exchangeTransMatchRepo3Service.genFinPledgeDisplace(trdFinProductDealTotal);
            finPledgeDisplaceMapper.insertSelective(finPledgeDisplace);

            for(BaseTradeProductDealDto baseTradeProductDealDto: exchangeTransMatchBaseTradeDtos) {
                if(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDeal())){
                    FinPledgeDisplaceDetail finPledgeDisplaceDetail = exchangeTransMatchRepo3Service.genFinPledgeDisplaceDetail(finPledgeDisplace,baseTradeProductDealDto.getTrdProductDeal());

                    // modify by etliu 20220110 start
                    // GZYHTZJY-1032: 协议回购的头寸处理，需要走PTL_POSITION_FLOW_CFG配置
                    if (StringUtil.equals(质押券置换_置换方向_换出.getValue(), finPledgeDisplaceDetail.getTradeType())) {
                        finPledgeDisplaceDetailHuanChu = finPledgeDisplaceDetail;
                    }else if(StringUtil.equals(质押券置换_置换方向_换入.getValue(), finPledgeDisplaceDetail.getTradeType())){
                        finPledgeDisplaceDetailHuanRu = finPledgeDisplaceDetail;
                    }
                    // modify by etliu 20220110 end

                    finPledgeDisplaceDetailMapper.insertSelective(finPledgeDisplaceDetail);
                }
            }

            // 首期质押品信息
            List<FinDealPledge> firstFinDealPledgeList = exchangeTransMatchRepo3Service.getFirstRepoFinDealPledge(
                    dto.getfCjbh(),dto.getVdate(),dto.getSeatNumber(),dto.getSecAcctId());
            if(!StringUtil.isEmptyList(firstFinDealPledgeList)) {
                // 更新首期质押品信息并返回更新后的信息
                List<FinDealPledge> updateFinDealPledges = updateFirstFinDealPledgeInfo(dto, trdFinProductDealTotal,
                        firstFinDealPledgeList, commonEntrustDto);
                // 生成质押券置换结果信息
                List<FinPledgeDisplaceResult> finPledgeDisplaceResultList =  exchangeTransMatchRepo3Service.genFinPledgeDisplaceResult(
                        dto, commonEntrustDto, trdFinProductDealTotal, firstFinDealPledgeList, updateFinDealPledges);
                for(FinPledgeDisplaceResult finPledgeDisplaceResult:finPledgeDisplaceResultList) {
                    finPledgeDisplaceResultMapper.insertSelective(finPledgeDisplaceResult);
                }
                if(StringUtil.equals(commonEntrustDto.getPs(), 交易方向_解除质押.getValue())){
                    firstFinDealPledgeTempList.addAll(firstFinDealPledgeList);
                }
            }
        }
        baseTradeProductDealDtos.addAll(exchangeTransMatchBaseTradeDtos);
        logger.debug("交易表数据:"+JsonUtils.object2Json(finProductTradeDto));
        // 处理新老头寸和指令完成度
        this.executePositionAndEntrustFinish(dto, commonEntrustDto, finProductTradeDto, exchangeTransMatchBaseTradeDtos,
                finPledgeDisplaceDetailHuanChu, finPledgeDisplaceDetailHuanRu, firstFinDealPledgeTempList);
        logger.debug("处理业务交易表---结束,交易编号:"+tradeId);
        return tradeId;
    }

    /**
     * 汇总交易相关信息入库
     * @param dto
     * @param commonEntrustDto
     * @param exchangeTransMatchBaseTradeDtos
     */
    private void insertTrdFinProductDealTotalInfo(ExchangeTransMatchCommonDto dto, ExchangeTransMatchCommonEntrustDto commonEntrustDto,
                                                  List<ExchangeTransMatchBaseTradeDto> exchangeTransMatchBaseTradeDtos ){
        if(StringUtil.isNotEmpty(exchangeTransMatchBaseTradeDtos.get(0).getTrdFinProductDealTotal())){

            trdFinProductDealTotalMapper.insertSelective(exchangeTransMatchBaseTradeDtos.get(0).getTrdFinProductDealTotal());
        }

        if(StringUtil.isNotEmpty(exchangeTransMatchBaseTradeDtos.get(0).getPaTrdFinProductDealTotal())){
            paTrdFinProductDealTotalMapper.insertSelective(exchangeTransMatchBaseTradeDtos.get(0).getPaTrdFinProductDealTotal());
        }

        if(StringUtil.isNotEmpty(exchangeTransMatchBaseTradeDtos.get(0).getTrdDealTotalAdd())){
            trdDealTotalAddMapper.insertSelective(exchangeTransMatchBaseTradeDtos.get(0).getTrdDealTotalAdd());
        }

        if(!StringUtil.isEmptyList(exchangeTransMatchBaseTradeDtos.get(0).getTrdTradeFeeTotalList())) {
            for(TrdTradeFeeTotal trdTradeFeeTotal:exchangeTransMatchBaseTradeDtos.get(0).getTrdTradeFeeTotalList()) {
                trdTradeFeeTotalMapper.insertSelective(trdTradeFeeTotal);
            }
        }


        if(StringUtil.equals(commonEntrustDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                && StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt1.交易方向_债转股.getValue())) {
            List<TrdFinProductBarterTotal> trdFinProductBarterTotalList = exchangeTransMatchBond4Service
                    .generateDealBarterTotal(dto,commonEntrustDto,exchangeTransMatchBaseTradeDtos.get(0).getTrdFinProductDealTotal());
            if(!StringUtil.isEmptyList(trdFinProductBarterTotalList)) {
                for(TrdFinProductBarterTotal trdFinProductBarterTotal:trdFinProductBarterTotalList) {
                    trdFinProductBarterTotalMapper.insertSelective(trdFinProductBarterTotal);
                }
            }
        }
    }

    /**
     * 交易相关信息入库
     * @param dto
     * @param commonEntrustDto
     * @param exchangeTransMatchBaseTradeDtos
     * @return
     */
    private String insertTrdProductDealInfo(ExchangeTransMatchCommonDto dto, ExchangeTransMatchCommonEntrustDto commonEntrustDto,
                                                  List<ExchangeTransMatchBaseTradeDto> exchangeTransMatchBaseTradeDtos){
        String tradeId = StringUtil.EMPTY;
        for(ExchangeTransMatchBaseTradeDto baseTradeProductDealDto: exchangeTransMatchBaseTradeDtos) {

            if(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDeal())){
                tradeId = StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDeal().getTotleTradeId())?baseTradeProductDealDto.getTrdProductDeal().getTotleTradeId():baseTradeProductDealDto.getTrdProductDeal().getTradeId();
                trdProductDealMapper.insertSelective(baseTradeProductDealDto.getTrdProductDeal());
            }
            if(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDealAdd())){
                trdProductDealAddMapper.insertSelective(baseTradeProductDealDto.getTrdProductDealAdd());
            }

            if(StringUtil.isNotEmpty(baseTradeProductDealDto.getPaTrdProductDeal())){
                paTrdProductDealMapper.insertSelective(baseTradeProductDealDto.getPaTrdProductDeal());
            }

            if(!StringUtil.isEmptyList(baseTradeProductDealDto.getStlPayOrdersList())){
                for(StlPayOrdersDto stlPayOrdersDto:baseTradeProductDealDto.getStlPayOrdersList()) {
                    stlPayOrdersMapper.insertSelective(stlPayOrdersDto);
                }
            }

            if(!StringUtil.isEmptyList(baseTradeProductDealDto.getTrdFinProductBarterList())){
                for(TrdFinProductBarter trdFinProductBarter:baseTradeProductDealDto.getTrdFinProductBarterList()) {
                    trdFinProductBarterMapper.insertSelective(trdFinProductBarter);
                }
            }

            if(!StringUtil.isEmptyList(baseTradeProductDealDto.getTrdTradeFeeDetailList())){
                for(TrdTradeFeeDetail TrdTradeFeeDetail:baseTradeProductDealDto.getTrdTradeFeeDetailList()) {
                    if(StringUtil.isNotEmpty(TrdTradeFeeDetail.getFeeAmt()) && BigDecimal.ZERO.compareTo(TrdTradeFeeDetail.getFeeAmt())<0) {
                        trdTradeFeeDetailMapper.insertSelective(TrdTradeFeeDetail);
                    }
                }
            }

            if(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdTradeFeeDetail())){
                trdTradeFeeDetailMapper.insertSelective(baseTradeProductDealDto.getTrdTradeFeeDetail());
            }

            if(!StringUtil.isEmptyList(baseTradeProductDealDto.getPtlTrade())) {
                for(PtlTrade ptlTrade:baseTradeProductDealDto.getPtlTrade()) {
                    ptlTradeMapper.insertSelective(ptlTrade);
                }
            }

            //质押券出入库需要处理标准券入出库表和标准券入出库明细表
            if (StringUtil.equals(commonEntrustDto.getPs(), 交易方向_质押券入库.getValue())
                    || StringUtil.equals(commonEntrustDto.getPs(), 交易方向_质押券出库.getValue())) {
                TrdStasecFlow trdStasecFlow = exchangeTransMatchBond6Service.generateTrdStasecFlow(dto,commonEntrustDto,baseTradeProductDealDto.getTrdProductDeal());
                trdStasecFlowMapper.insertSelective(trdStasecFlow);

                TrdStasecFlowDetail trdStasecFlowDetail = exchangeTransMatchBond6Service.generateTrdStasecFlowDetail(dto,commonEntrustDto,baseTradeProductDealDto.getTrdProductDeal());
                trdStasecFlowDetailMapper.insertSelective(trdStasecFlowDetail);
            }

            if(StringUtil.isNotEmpty(baseTradeProductDealDto.getFinTradeProduct())) {
                finTradeProductMapper.insertSelective(baseTradeProductDealDto.getFinTradeProduct());
            }

            if(CollectionUtils.isNotEmpty(baseTradeProductDealDto.getFinCashInfos())){
                for(FinCashInfo finCashInfo : baseTradeProductDealDto.getFinCashInfos()){
                    finCashInfoMapper.insertSelective(finCashInfo);
                }
            }

            if(CollectionUtils.isNotEmpty(baseTradeProductDealDto.getFinCashPlans())){
                for(FinCashPlan finCashPlan : baseTradeProductDealDto.getFinCashPlans()){
                    finCashPlanMapper.insertSelective(finCashPlan);
                }
            }

            if(StringUtil.isNotEmpty(baseTradeProductDealDto.getFinProductType())) {
                finProductTypeMapper.insertSelective(baseTradeProductDealDto.getFinProductType());
            }

            if(!StringUtil.isEmptyList(baseTradeProductDealDto.getFinDealPledgeList())) {
                for(FinDealPledge finTradeProduct:baseTradeProductDealDto.getFinDealPledgeList()) {
                    finDealPledgeMapper.insertSelective(finTradeProduct);
                }
            }
        }
        return tradeId;
    }

    /**
     * 处理新老头寸和指令完成度
     * @param dto
     * @param commonEntrustDto
     * @param finProductTradeDto
     * @param exchangeTransMatchBaseTradeDtos
     * @param finPledgeDisplaceDetailHuanChu
     * @param finPledgeDisplaceDetailHuanRu
     * @param firstFinDealPledgeTempList
     */
    private void executePositionAndEntrustFinish(ExchangeTransMatchCommonDto dto,ExchangeTransMatchCommonEntrustDto commonEntrustDto,
                 FinProductTradeDto finProductTradeDto, List<ExchangeTransMatchBaseTradeDto> exchangeTransMatchBaseTradeDtos,
                 FinPledgeDisplaceDetail finPledgeDisplaceDetailHuanChu,FinPledgeDisplaceDetail finPledgeDisplaceDetailHuanRu,
                                                 List<FinDealPledge> firstFinDealPledgeTempList){
        // 设置日终批标识
        if(CollectionUtils.isNotEmpty(finProductTradeDto.getBaseTradeProductDealDtos())){
            for(BaseTradeProductDealDto dealDto : finProductTradeDto.getBaseTradeProductDealDtos()){
                dealDto.setDayEndBatch(Boolean.TRUE);
            }
        }
        //增加调用新头寸引擎代码start
        positionRequestService.sendFinProductTradeDto(finProductTradeDto);
        //增加调用头寸引擎代码end
        // modify by etliu 20220110 start
        // GZYHTZJY-1032: 协议回购的头寸处理，需要走PTL_POSITION_FLOW_CFG配置
        if ((StringUtil.equals(commonEntrustDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                || StringUtil.equals(commonEntrustDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()))
                && dto.getSettleTransDataTotalAdd() != null
                && StringUtil.equals(dto.getSettleTransDataTotalAdd().getRepoType(),DictCodeList.回购类型_质押式协议回购.getValue())) {
            // 3.调用基础组件.【投资委托组件.完成委托】，写入委托完成状况信息(投资委托场合才执行此步)
            if(!finProductTradeDto.isNeedHandleFinishEntrust()) {
                baseTradeProductDealCommService.finishEntrust(finProductTradeDto.getBaseTradeProductDealDtos(), false);
            }

            // 协议回购头寸特殊处理
            exchangeTransMatchRepoCommonService.handlePositionOfXyhg(dto, commonEntrustDto, exchangeTransMatchBaseTradeDtos,
                    finPledgeDisplaceDetailHuanChu, finPledgeDisplaceDetailHuanRu, firstFinDealPledgeTempList);
        }
        else if(StringUtil.equals(commonEntrustDto.getPs(), 交易方向_质押券入库.getValue())
                || StringUtil.equals(commonEntrustDto.getPs(), 交易方向_质押券出库.getValue())){
            // 处理质押券出入库头寸
            exchangeTransMatchBondCommonService.handlePositionOfZyQ(finProductTradeDto);
        }
        else {
            //处理头寸和指令完成度
            baseTradeProductDealCommService.execute(finProductTradeDto);
        }
        // modify by etliu 20220110 end
    }

    /**
     * 更新首期质押品信息
     * @param dto
     * @param dealTotal
     * @param firstFinDealPledgeList
     * @param commonEntrustDto
     * @return
     */
    private List<FinDealPledge> updateFirstFinDealPledgeInfo(ExchangeTransMatchCommonDto dto, TrdFinProductDealTotal dealTotal,
                                              List<FinDealPledge> firstFinDealPledgeList, ExchangeTransMatchCommonEntrustDto commonEntrustDto){
        List<FinDealPledge> finDealPledgeList = new ArrayList<>();
        //中途换券需要更新首期的质押品信息
        if(StringUtil.equals(commonEntrustDto.getPs(), 交易方向_中途换券.getValue())) {
            // 更新首期质押品信息FinDealPledge
            if(!DictCodeListStl.业务场景分类明细_债券协议正回购_换券_换钱.getValue().equals(dto.getBusiSceneCalss())){
                for(FinDealPledge finDealPledge:firstFinDealPledgeList) {
                    finDealPledgeMapper.deleteByPrimaryKey(finDealPledge);
                }
                Table<String, String, SettleTransDataTotalPledge> table = HashBasedTable.create();
                for(SettleTransDataTotalPledge pledge : dto.getSettleTransDataTotalPledgeList()){
                    table.put(pledge.getPledgeFinprodId(), pledge.getDisplaceDirection(), pledge);
                }
                for(FinDealPledge firstFinDealPledge : firstFinDealPledgeList) {
                    FinDealPledge newFinDealPledge = new FinDealPledge();
                    BeanUtil.copyPropertiesIgnoreNull(newFinDealPledge, firstFinDealPledge);
                    // 金融产品代码
                    newFinDealPledge.setFinprodId(dealTotal.getFinprodId());
                    // 换入质押券
                    SettleTransDataTotalPledge inPledge = table.get(newFinDealPledge.getPledgeFinprodId(), 质押券置换_置换方向_换入.getValue());
                    // 换出质押券
                    SettleTransDataTotalPledge outPledge = table.get(newFinDealPledge.getPledgeFinprodId(), 质押券置换_置换方向_换出.getValue());
                    if(null != inPledge){
                        // 如果换入的质押券跟首期一致,则券面总额、质押券数量相加
                        newFinDealPledge.setPledgeNumber(ArithmeticUtil.doAdd(newFinDealPledge.getPledgeNumber(), inPledge.getPledgeNumber()));
                    }else if(null != outPledge){
                        // 如果出的质押券跟首期一致,则券面总额、质押券数量相减
                        newFinDealPledge.setPledgeNumber(ArithmeticUtil.subtract(newFinDealPledge.getPledgeNumber(), outPledge.getPledgeNumber()));
                    }
                    // 如果质押券面额为0,则忽略
                    if(BigDecimal.ZERO.compareTo(newFinDealPledge.getPledgeNumber()) >= 0){
                        continue;
                    }
                    finDealPledgeList.add(newFinDealPledge);
                }
                // 首期质押券金融代码
                List<String> firstPladgeFinprodIds = firstFinDealPledgeList.stream()
                        .map(FinDealPledge::getPledgeFinprodId)
                        .collect(Collectors.toList());
                // 筛选出此次换入并且不在首期里的质押券
                List<SettleTransDataTotalPledge> inpledges = dto.getSettleTransDataTotalPledgeList().stream()
                        .filter(item -> 质押券置换_置换方向_换入.getValue().equals(item.getDisplaceDirection())
                                && !firstPladgeFinprodIds.contains(item.getPledgeFinprodId()))
                        .collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(inpledges)){
                    for(SettleTransDataTotalPledge inpledge : inpledges){
                        FinDealPledge finDealPledge = new FinDealPledge();
                        finDealPledge.setFinprodId(dealTotal.getFinprodId());
                        finDealPledge.setBranch(0L);
                        finDealPledge.setPledgeFinprodId(inpledge.getPledgeFinprodId());
                        finDealPledge.setPledgeNumber(inpledge.getPledgeNumber());
                        finDealPledge.setPledgeAmt(inpledge.getPledgeAmt());
                        finDealPledge.setPledgeFaceValue(inpledge.getPledgeFaceValue());
                        finDealPledgeList.add(finDealPledge);
                    }
                }
                BigDecimal totalAmt = BigDecimal.ZERO;
                for(FinDealPledge finDealPledge : finDealPledgeList){
                    totalAmt = ArithmeticUtil.doAdd(totalAmt, ArithmeticUtil.multiply(finDealPledge.getPledgeNumber(), finDealPledge.getPledgeFaceValue()));
                }
                // 查询首期交易信息
                TrdProductDeal firstDeal = trdProductDealMapper.selectByPrimaryKey(dealTotal.getFinprodId());
                // 质押率=首期.交易本金/SUM(质押面值*质押数量)
                BigDecimal pledgeRatio = ArithmeticUtil.divide(firstDeal.getPrinAmt(), totalAmt, 2, RoundingMode.HALF_UP);
                // 赋值质押率
                for(FinDealPledge finDealPledge : finDealPledgeList){
                    finDealPledge.setPledgeRatio(pledgeRatio);
                }
                for(FinDealPledge finDealPledge : finDealPledgeList){
                    finDealPledge.setCreateTime(new Date());
                    finDealPledge.setUpdateTime(new Date());
                    finDealPledgeMapper.insertSelective(finDealPledge);
                }
            }
        }
        // 解除质押需要删除首期的质押品信息 解除质押原先删除是为了防止再次操作,但实际也不会再次操作,所以可以不用删除
//        else if(StringUtil.equals(commonEntrustDto.getPs(), 交易方向_解除质押.getValue())){
//            for(FinDealPledge finDealPledge:firstFinDealPledgeList) {
//                finDealPledgeMapper.deleteByPrimaryKey(finDealPledge);
//            }
//        }
        return finDealPledgeList;
    }

    /**
     * 构造 汇总成交数据匹配结果表
     * @param dealDto
     * @param etrEntrust
     * @return
     */
    private SettleTransDataTotalMatch genSettleTransDataTotalMatch(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto etrEntrust) {
        SettleTransDataTotalMatch settleTransDataTotalMatch = new SettleTransDataTotalMatch();
        //成交列表选择记录.流水号
        settleTransDataTotalMatch.setSeqNo(dealDto.getSeqId());
        //匹配列表选择记录.业务编号
        settleTransDataTotalMatch.setEntrunstTradeId(etrEntrust.getEntrustId());
        //匹配列表选择记录.目标类型
        settleTransDataTotalMatch.setMatchType(etrEntrust.getTargetType());
        //数据字典.下行交易匹配方式（TRD_MATCH_TYPE）.手动匹配or 自动匹配
        settleTransDataTotalMatch.setMatchClass(dealDto.getMatchType());
        //已匹配份额
        settleTransDataTotalMatch.setMarchQty(etrEntrust.getMarchQty());
        //已匹配金额
        settleTransDataTotalMatch.setMarchAmt(etrEntrust.getMarchAmt());
        //指令已匹配份额
        settleTransDataTotalMatch.setMarchQtyEn(etrEntrust.getMarchQtyEn());
        //指令已匹配金额
        settleTransDataTotalMatch.setMarchAmtEn(etrEntrust.getMarchAmtEn());
        //币种
        settleTransDataTotalMatch.setCcy(dealDto.getCcy());
        //证券账户
        settleTransDataTotalMatch.setSecAcctId(etrEntrust.getSecAcctId());
        return settleTransDataTotalMatch;
    }

    /**
     * 构造
     * @param dealDto
     * @param etrEntrust
     * @return
     */
    private List<SettleDataTotalPledgeMatch> genSettleDataTotalPledgeMatch (ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto etrEntrust) {
        List<SettleDataTotalPledgeMatch> settleDataTotalPledgeMatchList = new ArrayList<>();
        //回购首期
        if(StringUtil.equals(dealDto.getPs(),DictCodeList.交易方向_买入.getValue())) {
            SettleDataTotalPledgeMatch settleDataTotalPledgeMatch = new SettleDataTotalPledgeMatch();
            //成交列表选择记录.流水号
            settleDataTotalPledgeMatch.setSeqNo(dealDto.getSeqId());
            //匹配列表选择记录.业务编号
            settleDataTotalPledgeMatch.setEntrunstTradeId(etrEntrust.getEntrustId());
            //匹配列表选择记录.质押金融产品代码
            settleDataTotalPledgeMatch.setPledgeFinprodId(etrEntrust.getFinprodId());
            //分支序号
            settleDataTotalPledgeMatch.setBranch(0L);
            //匹配列表选择记录.目标类型
            settleDataTotalPledgeMatch.setMatchType(etrEntrust.getTargetType());
            //数据字典.下行交易匹配方式（TRD_MATCH_TYPE）.手动匹配or 自动匹配
            settleDataTotalPledgeMatch.setMatchClass(dealDto.getMatchType());
            //已匹配份额
            settleDataTotalPledgeMatch.setMarchQty(etrEntrust.getMarchQty());
            //已匹配金额
            settleDataTotalPledgeMatch.setMarchAmt(null);
            // GZYHTZJY-969:MarchQty = MarchQtyEn modify by fuyongsheng 20211230 start
            //指令已分配份额(MarchQty = MarchQtyEn)
            // settleDataTotalPledgeMatch.setMarchQtyEn(ArithmeticUtil.divide(etrEntrust.getMarchQtyEn(),etrEntrust.getPrice(),2));
            settleDataTotalPledgeMatch.setMarchQtyEn(etrEntrust.getMarchQty());
            // GZYHTZJY-969:MarchQty = MarchQtyEn modify by fuyongsheng 20211230 end
            //指令已分配金额
            settleDataTotalPledgeMatch.setMarchAmtEn(null);
            //置换方向
            settleDataTotalPledgeMatch.setDisplaceDirection(null);

            settleDataTotalPledgeMatchList.add(settleDataTotalPledgeMatch);
        }
        //匹配类别选择记录的质押品不为空 and 交易方向 = 中途换券 or 解除质押
        else if(StringUtil.equals(dealDto.getPs(),交易方向_中途换券.getValue())
        || StringUtil.equals(dealDto.getPs(),交易方向_解除质押.getValue())) {
            for(SettleTransDataTotalPledge settleTransDataTotalPledge:dealDto.getSettleTransDataTotalPledgeList()) {
                SettleDataTotalPledgeMatch settleDataTotalPledgeMatch = new SettleDataTotalPledgeMatch();
                if(StringUtil.equals(dealDto.getPs(),交易方向_中途换券.getValue())) {
                    //成交列表选择记录.流水号
                    settleDataTotalPledgeMatch.setSeqNo(dealDto.getSeqId());
                    //匹配列表选择记录.业务编号
                    settleDataTotalPledgeMatch.setEntrunstTradeId(etrEntrust.getEntrustId());
                    //匹配列表选择记录.质押金融产品代码
                    settleDataTotalPledgeMatch.setPledgeFinprodId(settleTransDataTotalPledge.getPledgeFinprodId());
                    //分支序号
                    settleDataTotalPledgeMatch.setBranch(0L);
                    //匹配列表选择记录.目标类型
                    settleDataTotalPledgeMatch.setMatchType(etrEntrust.getTargetType());
                    //数据字典.下行交易匹配方式（TRD_MATCH_TYPE）.手动匹配or 自动匹配
                    settleDataTotalPledgeMatch.setMatchClass(dealDto.getMatchType());
                    //已匹配份额
                    settleDataTotalPledgeMatch.setMarchQty(etrEntrust.getMarchQty());
                    //已匹配金额
                    settleDataTotalPledgeMatch.setMarchAmt(null);
                    //指令已分配份额
                    settleDataTotalPledgeMatch.setMarchQtyEn(etrEntrust.getMarchQtyEn());
                    //指令已分配金额
                    settleDataTotalPledgeMatch.setMarchAmtEn(null);
                    //置换方向
                    settleDataTotalPledgeMatch.setDisplaceDirection(settleTransDataTotalPledge.getDisplaceDirection());

                }
                else if(StringUtil.equals(dealDto.getPs(),交易方向_解除质押.getValue())){
                    //成交列表选择记录.流水号
                    settleDataTotalPledgeMatch.setSeqNo(dealDto.getSeqId());
                    //匹配列表选择记录.业务编号
                    settleDataTotalPledgeMatch.setEntrunstTradeId(etrEntrust.getEntrustId());
                    //匹配列表选择记录.质押金融产品代码
                    settleDataTotalPledgeMatch.setPledgeFinprodId(settleTransDataTotalPledge.getPledgeFinprodId());
                    //分支序号
                    settleDataTotalPledgeMatch.setBranch(0L);
                    //匹配列表选择记录.目标类型
                    settleDataTotalPledgeMatch.setMatchType(etrEntrust.getTargetType());
                    //数据字典.下行交易匹配方式（TRD_MATCH_TYPE）.手动匹配or 自动匹配
                    settleDataTotalPledgeMatch.setMatchClass(dealDto.getMatchType());
                    //已匹配份额
                    settleDataTotalPledgeMatch.setMarchQty(etrEntrust.getMarchQty());
                    //已匹配金额
                    settleDataTotalPledgeMatch.setMarchAmt(etrEntrust.getMarchQty());
                    //指令已分配份额
                    settleDataTotalPledgeMatch.setMarchQtyEn(etrEntrust.getMarchQtyEn());
                    //指令已分配金额
                    settleDataTotalPledgeMatch.setMarchAmtEn(etrEntrust.getMarchQtyEn());
                    //置换方向
                    settleDataTotalPledgeMatch.setDisplaceDirection(settleTransDataTotalPledge.getDisplaceDirection());
                }
                settleDataTotalPledgeMatchList.add(settleDataTotalPledgeMatch);
            }
        }

        return settleDataTotalPledgeMatchList;
    }


    /**
     * 构造 成交数据与投管交易关系
     * @param dealDto
     * @param tradeId
     * @return
     */
    private List<SettleTransTgMapping> genSettleTransTgMapping(ExchangeTransMatchCommonDto dealDto,String tradeId) {
        List<SettleTransTgMapping> list = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("seqId",dealDto.getSeqId());
        if(StringUtil.equals(dealDto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
            || StringUtil.equals(dealDto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
            map.put("tradeIdRaw",dealDto.getTradeIdRaw());
        }
        List<SettleTransData> settleTransDataList = exchangeTransMatchCommonMapper.getSettleTransDataList(map);
        if(!StringUtil.isEmptyList(settleTransDataList)) {
            // modify by etliu 20220222 start
            // 广农商二阶段 GNS-931:【广农商场内清算】上/深交所 基金存续期重复跑批，生成数据有误
            Date platDate = iPlatformDate.getSysDate();
            // modify by etliu 20220222 end

            for(SettleTransData settleTransData:settleTransDataList) {
                SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
                settleTransTgMapping.setTradeId(settleTransData.getTradeId());
                settleTransTgMapping.setTradeIdTg(tradeId);
                // modify by etliu 20220222 start
                // 广农商二阶段 GNS-931:【广农商场内清算】上/深交所 基金存续期重复跑批，生成数据有误
//                settleTransTgMapping.setDealDate(dealDto.getDealDate());
                settleTransTgMapping.setDealDate(platDate);
                // modify by etliu 20220222 end
                settleTransTgMapping.setTgDataStatus(DictCodeListStl.投管交易数据状态_处理结束.getValue());
                list.add(settleTransTgMapping);
            }
        }

        return list;
    }

    /**
     * 保存 成交数据与投管交易关系
     * @param settleTransTgMappingList
     * @return
     */
    private void saveSettleTransTgMapping(List<SettleTransTgMapping> settleTransTgMappingList) {
        if(!StringUtil.isEmptyList(settleTransTgMappingList)) {
            for(SettleTransTgMapping settleTransTgMapping:settleTransTgMappingList) {
                try {
                    settleTransTgMappingMapper.insertSelective(settleTransTgMapping);
                } catch (Exception e) {
                    settleTransTgMappingMapper.deleteByPrimaryKey(settleTransTgMapping);
                    settleTransTgMappingMapper.insertSelective(settleTransTgMapping);
                }
            }
        }
    }

    /**
     * 批量自动取消成交匹配
     * @param dtoList
     */
    public void revokeTransactionAutoMacthBatch(List<SettleTransDataTotalMatch> dtoList) {
        logger.debug("批量取消自动成交匹配数据---开始，批量成交数据:"+ JsonUtils.objectArray2Json(dtoList));
        if(!StringUtil.isEmptyList(dtoList)) {
            for (SettleTransDataTotalMatch dto : dtoList) {
                this.revokeTransactionAutoMacth(dto);
            }
        }
        logger.debug("批量取消自动成交匹配数据---结束");
    }

    /**
     * 取消成交匹配
     * @param dto
     */
    public void revokeTransactionMacth(SettleTransDataTotalMatch dto) {
        logger.debug("取消成交匹配---开始，成交数据:"+ JsonUtils.object2Json(dto));
        List<ExchangeTransMatchCommonRevokeDto> exchangeTransMatchCommonRevokeDtoList = queryExchangeTransMatchCommonRevokeDto(dto);
        logger.debug("查询取消匹配的成交数据与投管交易关系:"+ JsonUtils.objectArray2Json(exchangeTransMatchCommonRevokeDtoList));
        if(!StringUtil.isEmptyList(exchangeTransMatchCommonRevokeDtoList)) {
            // 撤销匹配交易
            revokeMatchTrade(exchangeTransMatchCommonRevokeDtoList, dto);
            // 撤销质押券
            revokeFinPledge(exchangeTransMatchCommonRevokeDtoList, dto);
            // 撤销清算匹配数据
            revokeSettleMatchData(exchangeTransMatchCommonRevokeDtoList, dto);
            // 头寸撤销
//            positionRequestService.sendRevoke(exchangeTransMatchCommonRevokeDtoList);
        }

        logger.debug("取消成交匹配---结束");
    }

    /**
     * 取消成交匹配
     * @param dto
     */
    public void revokeTransactionAutoMacth(SettleTransDataTotalMatch dto) {
        logger.debug("取消自动成交匹配---开始，成交数据:"+ JsonUtils.object2Json(dto));
        List<ExchangeTransMatchCommonRevokeDto> exchangeTransMatchCommonRevokeDtoList = queryExchangeTransMatchCommonRevokeDto(dto);
        logger.debug("查询取消自动匹配的成交数据与投管交易关系:"+ JsonUtils.objectArray2Json(exchangeTransMatchCommonRevokeDtoList));
        if(!StringUtil.isEmptyList(exchangeTransMatchCommonRevokeDtoList)) {
            // 撤销质押券
            revokeFinPledge(exchangeTransMatchCommonRevokeDtoList, dto);
            // 撤销清算匹配数据
            revokeSettleMatchData(exchangeTransMatchCommonRevokeDtoList, dto);
        }
        logger.debug("取消自动成交匹配---结束");
    }

    /**
     * 查询取消匹配的成交数据与投管交易关系
     * @param dto
     * @return
     */
    private List<ExchangeTransMatchCommonRevokeDto> queryExchangeTransMatchCommonRevokeDto(SettleTransDataTotalMatch dto){
        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("tgDataStatus",DictCodeListStl.投管交易数据状态_处理结束.getValue());
        paraMap.put("seqNo",dto.getSeqNo());
        paraMap.put("entrunstId",dto.getEntrunstTradeId());
        paraMap.put("matchClass",dto.getMatchClass());
        return exchangeTransMatchCommonMapper.getSettleTransTgMapping(paraMap);
    };

    /**
     * 撤销匹配交易
     * @param dtoList
     * @param dto
     */
    private void revokeMatchTrade(List<ExchangeTransMatchCommonRevokeDto> dtoList, SettleTransDataTotalMatch dto){
        if(CollectionUtils.isNotEmpty(dtoList)){
            List<String> tradeIdList = new ArrayList<>();
            List<String> totalTradeIdList = new ArrayList<>();
            for(ExchangeTransMatchCommonRevokeDto revokeDto:dtoList) {
                if (StringUtil.equals(revokeDto.getIsTotalDeal(), DictCodeList.是否_是.getValue())) {
                    if(!totalTradeIdList.contains(revokeDto.getTradeIdTg())) {
                        totalTradeIdList.add(revokeDto.getTradeIdTg());
                    }

                } else {
                    if(!tradeIdList.contains(revokeDto.getTradeIdTg())) {
                        tradeIdList.add(revokeDto.getTradeIdTg());
                    }
                }
            }

            PositionUpdateParam param = new PositionUpdateParam();
            if(!StringUtil.isEmptyList(totalTradeIdList)) {
                param.setTotalTradeIdList(totalTradeIdList);
            }
            else {
                param.setTradeIdList(tradeIdList);
            }
            param.setStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            param.setBusiType(dto.getMatchType());
            param.setUpdateUser(GlobalConstant.SYSTEM_USER);
            param.setRevokeReason("取消成交匹配");

            // 处理头寸
            logger.debug("取消成交匹配---处理头寸开始，参数数据:"+ JsonUtils.object2Json(param));
            if(isSingle) {
                simpleRestTemplate.postForObject(tamsSerever + "/invest/trade/api/execute", param, Map.class);
            }
            else {
                restTemplate.postForObject(tamsSerever + "/invest/trade/api/execute", param, Map.class);
            }
            logger.debug("取消成交匹配---处理头寸结束");

            // 撤销交易
            logger.debug("取消成交匹配---撤销交易开始，参数数据:"+ JsonUtils.object2Json(param));
            if(isSingle) {
                simpleRestTemplate.postForObject(tamsSerever + "/invest/trade/api/revoke", param, String.class);
            }
            else {
                restTemplate.postForObject(tamsSerever + "/invest/trade/api/revoke", param, String.class);
            }
            logger.debug("取消成交匹配---撤销交易寸结束");
        }
    }

    /**
     * 撤销质押券
     * @param dtoList
     * @param dto
     */
    private void revokeFinPledge(List<ExchangeTransMatchCommonRevokeDto> dtoList, SettleTransDataTotalMatch dto){
        if(CollectionUtils.isNotEmpty(dtoList)){
            //质押式协议回购处理质押券置换，质押券置换明细，质押券置换结果，回购质押品 表数据
            List<ExchangeTransMatchCommonRevokeDto> repoRevokeDtoList = dtoList.stream()
                    .filter(item -> StringUtil.equals(item.getIsTotalDeal(),DictCodeList.是否_是.getValue())
                            && (StringUtil.equals(item.getPs(), 交易方向_中途换券.getValue()) || StringUtil.equals(item.getPs(), 交易方向_解除质押.getValue()))).collect(Collectors.toList());
            if(!StringUtil.isEmptyList(repoRevokeDtoList)) {
                //以汇总交易编号分组处理 质押券置换，质押券置换明细
                Map<String,List<ExchangeTransMatchCommonRevokeDto>> repoRevokeDtoListMap = repoRevokeDtoList.stream().collect(Collectors.groupingBy(ExchangeTransMatchCommonRevokeDto::getTradeIdTg));
                for(String key: repoRevokeDtoListMap.keySet()) {
                    for(ExchangeTransMatchCommonRevokeDto repoRevokeDto:repoRevokeDtoListMap.get(key)) {
                        //质押券置换明细
                        finPledgeDisplaceDetailMapper.deleteByPrimaryKey(repoRevokeDto.getTradeIdTgDetail());
                    }
                    //质押券置换
                    finPledgeDisplaceMapper.deleteByPrimaryKey(key);
                }

                //以汇总成交流水号分组处理 回购质押品DTO，质押券置换结果
                Map<String,List<ExchangeTransMatchCommonRevokeDto>> repoRevokeDtoListMap1 = repoRevokeDtoList.stream().collect(Collectors.groupingBy(ExchangeTransMatchCommonRevokeDto::getTradeIdTg));
                SettleTransDataTotal settleTransDataTotal = settleTransDataTotalMapper.selectByPrimaryKey(dto.getSeqNo());
                Map queryMap = new HashMap();
                for(String key: repoRevokeDtoListMap1.keySet()) {
                    List<ExchangeTransMatchCommonRevokeDto> itemList = repoRevokeDtoListMap1.get(key);
                    queryMap.put("tradeId",key);
                    queryMap.put("branch",0L);
                    List<FinPledgeDisplaceResult> finPledgeDisplaceResultList =  exchangeTransMatchCommonMapper.getFinPledgeDisplaceResult(queryMap);
                    if(!StringUtil.isEmptyList(finPledgeDisplaceResultList)) {
                        //更新首期的质押品信息
                        // ZGTFCP-1805 : 先filter再findFirst modify by fuyongsheng 20220324 start
                        // FinPledgeDisplaceResult finPledgeDisplaceResult = finPledgeDisplaceResultList.stream().findFirst().filter(result->StringUtil.equals(result.getResultType(),质押券置换_置换结果_置换前.getValue())).get();
                        // FinPledgeDisplaceResult finPledgeDisplaceResult = finPledgeDisplaceResultList.stream().filter(result->StringUtil.equals(result.getResultType(),质押券置换_置换结果_置换前.getValue())).findFirst().get();
                        // ZGTFCP-1805 : 先filter再findFirst modify by fuyongsheng 20220324 end
                        List<FinPledgeDisplaceResult> finPledgeDisplaceResults = finPledgeDisplaceResultList
                                .stream()
                                .filter(result->StringUtil.equals(result.getResultType(),质押券置换_置换结果_置换前.getValue()))
                                .collect(Collectors.toList());
                        // modify by fuyongsheng 撤销中途换券交易,要删除质押品表,登录质押品置换表置换前数据
                        if(StringUtil.equals(itemList.get(0).getPs(), 交易方向_中途换券.getValue())){
//                            List<FinDealPledge> firstFinDealPledgeList = exchangeTransMatchRepo3Service.getFirstRepoFinDealPledge(settleTransDataTotal.getfCjbh(),settleTransDataTotal.getVdate(),itemList.get(0).getPortfolioId());
                            List<FinDealPledge> firstFinDealPledgeList = exchangeTransMatchRepo3Service.getFirstRepoFinDealPledge(
                                    settleTransDataTotal.getfCjbh(),settleTransDataTotal.getVdate(),
                                    settleTransDataTotal.getSeatNumber(),settleTransDataTotal.getSecAcctId());
                            if(!StringUtil.isEmptyList(firstFinDealPledgeList)) {
                                for(FinDealPledge finDealPledge:firstFinDealPledgeList) {
//                                    finDealPledge.setPledgeFinprodId(finPledgeDisplaceResult.getFinprodId());
//                                    finDealPledge.setPledgeRatio(finPledgeDisplaceResult.getScale());
//                                    finDealPledge.setPledgeAmt(finPledgeDisplaceResult.getPledgeAmt());
//                                    finDealPledgeMapper.updateByPrimaryKeySelective(finDealPledge);
                                    finDealPledgeMapper.deleteByPrimaryKey(finDealPledge);
                                }
                            }
                            if(CollectionUtils.isNotEmpty(finPledgeDisplaceResults)){
                                for(FinPledgeDisplaceResult finPledgeDisplaceResult : finPledgeDisplaceResults){
                                    FinDealPledge finDealPledge = new FinDealPledge();
                                    finDealPledge.setBranch(0L);
                                    finDealPledge.setFinprodId(settleTransDataTotal.getFinprodId());
                                    finDealPledge.setPledgeFinprodId(finPledgeDisplaceResult.getFinprodId());
                                    finDealPledge.setPledgeNumber(finPledgeDisplaceResult.getPledgeAmt());
                                    finDealPledge.setPledgeRatio(finPledgeDisplaceResult.getScale());
                                    finDealPledge.setPledgeFaceValue(this.select券面面值(finDealPledge.getPledgeFinprodId()));
                                    finDealPledge.setCreateTime(new Date());
                                    finDealPledge.setUpdateTime(new Date());
                                    finDealPledgeMapper.insertSelective(finDealPledge);
                                }
                            }
                        }
                        // 解除质押不删除首期质押品表,所以撤销解除质押时也不还原质押品表  modify by fuyongsheng start
//                        else {
//                            //查询首期交易
//                            // GZYHTZJY-1160:查询首期交易方向是T01 modify fuyongsheng 20220119 start
////                            TrdProductDeal trdProductDeal = exchangeTransMatchRepo3Service.getFirstRepoTrade(
////                                    settleTransDataTotal.getfCjbh(),settleTransDataTotal.getVdate(),
////                                    //settleTransDataTotal.getPs(),
////                                    DictCodeList.交易方向_买入.getValue(),
////                                    itemList.get(0).getPortfolioId());
//                            TrdProductDeal trdProductDeal = trdCommonBaseService.getFirstRepoTrade(settleTransDataTotal.getfCjbh(),
//                                    settleTransDataTotal.getSeatNumber(), settleTransDataTotal.getSecAcctId(),
//                                    settleTransDataTotal.getVdate());
//                            // GZYHTZJY-1160:查询首期交易方向是T01 modify fuyongsheng 20220119 end
//                            if(StringUtil.isNotEmpty(trdProductDeal)) {
//                                FinDealPledge finDealPledge = new FinDealPledge();
//                                finDealPledge.setFinprodId(trdProductDeal.getTradeId());
//                                finDealPledge.setBranch(0L);
//                                finDealPledge.setPledgeFinprodId(finPledgeDisplaceResult.getFinprodId());
//                                finDealPledge.setPledgeRatio(finPledgeDisplaceResult.getScale());
//                                finDealPledge.setPledgeNumber(ArithmeticUtil.divide( finPledgeDisplaceResult.getPledgeAmt(),new BigDecimal(100),2));
//                                finDealPledgeMapper.insertSelective(finDealPledge);
//                            }
//
//                        }
                        // 解除质押不删除首期质押品表,所以撤销解除质押时也不还原质押品表  modify by fuyongsheng end

                        for (FinPledgeDisplaceResult result:finPledgeDisplaceResultList) {
                            finPledgeDisplaceResultMapper.deleteByPrimaryKey(result);
                        }

                    }
                }
            }
        }
    }

    /**
     * 撤销清算交易匹配数据
     * @param dtoList
     * @param dto
     */
    private void revokeSettleMatchData(List<ExchangeTransMatchCommonRevokeDto> dtoList, SettleTransDataTotalMatch dto){
        if(CollectionUtils.isNotEmpty(dtoList)){
            //处理成交匹配相关表
            for(ExchangeTransMatchCommonRevokeDto revokeDto:dtoList) {
                // 成交数据与投管交易关系（SETTLE_TRANS_TG_MAPPING）：删除
                SettleTransTgMappingKey key = new SettleTransTgMappingKey();
                key.setTradeId(revokeDto.getTradeId());
                key.setTradeIdTg(revokeDto.getTradeIdTg());
                settleTransTgMappingMapper.deleteByPrimaryKey(key);
                //汇总成交数据匹配结果表（SETTLE_TRANS_DATA_TOTAL_MATCH）：数据删除
                // settleTransDataTotalMatchMapper.deleteByPrimaryKey(dto);
                Map<String,String> matchDeleteMap = new HashMap<>();
                matchDeleteMap.put("totalSeqId",dto.getSeqNo());
                matchDeleteMap.put("entrunstId",dto.getEntrunstTradeId());
                exchangeTransMatchCommonMapper.deleteSettleTransDataTotalMatch(matchDeleteMap);

                Map<String,Object> deleteMap = new HashMap<>();
                deleteMap.put("seqNo",dto.getSeqNo());
                deleteMap.put("entrustId",dto.getEntrunstTradeId());
                exchangeTransMatchCommonMapper.deleteSettleDataTotalPledgeMatch(deleteMap);
            }

            //更新汇总成交数据状态表
            SettleTransDataTotalStatus settleTransDataTotalStatus = settleTransDataTotalStatusMapper.selectByPrimaryKey(dto.getSeqNo());
            // XYHGFXSJ-782:协议回购没有部分匹配,所以撤销肯定是未匹配 modify by fuyongsheng 20220802 start
            SettleTransDataTotalAdd settleTransDataTotalAdd = settleTransDataTotalAddMapper.selectByPrimaryKey(dto.getSeqNo());
            if(StringUtil.isNotEmpty(settleTransDataTotalStatus)) {
                //取得剩余匹配份额
                BigDecimal remainNum = exchangeTransMatchCommonMapper.getRemainNum(dto.getSeqNo());
                if(settleTransDataTotalAdd != null && DictCodeList.回购类型_质押式协议回购.getValue().equals(settleTransDataTotalAdd.getRepoType())){
                    settleTransDataTotalStatus.setMatchStatus(下行交易匹配状态_未匹配.getValue());
                }else{
                    settleTransDataTotalStatus.setMatchStatus(BigDecimal.ZERO.compareTo(remainNum) >=0
                            ?下行交易匹配状态_未匹配.getValue():下行交易匹配状态_部分匹配.getValue());
                }
                settleTransDataTotalStatus.setUpdateTime(new Date());
                settleTransDataTotalStatus.setUpdateUser(StringUtil.isNotEmpty(LoginUserUtil.getUserName())?LoginUserUtil.getUserName():"system");
                settleTransDataTotalStatusMapper.updateByPrimaryKey(settleTransDataTotalStatus);
            }  else {
                settleTransDataTotalStatus = new SettleTransDataTotalStatus();
                settleTransDataTotalStatus.setSeqNo(dto.getSeqNo());
                //取得剩余匹配份额
                BigDecimal remainNum = exchangeTransMatchCommonMapper.getRemainNum(dto.getSeqNo());
                if(settleTransDataTotalAdd != null && DictCodeList.回购类型_质押式协议回购.getValue().equals(settleTransDataTotalAdd.getRepoType())){
                    settleTransDataTotalStatus.setMatchStatus(下行交易匹配状态_未匹配.getValue());
                }else{
                    settleTransDataTotalStatus.setMatchStatus(BigDecimal.ZERO.compareTo(remainNum) >=0
                            ?下行交易匹配状态_未匹配.getValue():下行交易匹配状态_部分匹配.getValue());
                }
                // XYHGFXSJ-782:协议回购没有部分匹配,所以撤销肯定是未匹配 modify by fuyongsheng 20220802 end
                settleTransDataTotalStatus.setUpdateTime(new Date());
                settleTransDataTotalStatus.setUpdateUser(StringUtil.isNotEmpty(LoginUserUtil.getUserName())?LoginUserUtil.getUserName():"system");
                settleTransDataTotalStatusMapper.insert(settleTransDataTotalStatus);
            }
        }
    }

    /**
     * 计算处理已分配份额，已分配金额，指令已分配金额，指令已分配份额
     * @param dto
     * @param commonEntrustDtoList
     * @param matchStatus
     */
    private Pair<String, List<ExchangeTransMatchCommonEntrustDto>> calcShareAndAmountV0(ExchangeTransMatchCommonDto dto,List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoList,String matchStatus){
        //只有一条匹配指令时
        if(dto.getEntrustIdList().size() == 1) {
            ExchangeTransMatchCommonEntrustDto commonEntrustDto = commonEntrustDtoList.get(0);
            //特殊处理质押式竞价回购的分配逻辑
            if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                    || StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                //已匹配份额
                BigDecimal remainQty = ArithmeticUtil.divide(dto.getRemainNum(),new BigDecimal(100),2);
                BigDecimal marchQty= BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(remainQty,commonEntrustDto.getRemainEntrustNum()))>0 ? remainQty : commonEntrustDto.getRemainEntrustNum();
                commonEntrustDto.setMarchQty(ArithmeticUtil.multiply(marchQty,new BigDecimal(100)));

                //已匹配金额
                commonEntrustDto.setMarchAmt(commonEntrustDto.getMarchQty());

                //指令已匹配金额
                commonEntrustDto.setMarchAmtEn(commonEntrustDto.getMarchAmt());
                //指令已匹配份额
                commonEntrustDto.setMarchQtyEn(marchQty);

            }
            else if(StringUtil.equals(commonEntrustDto.getTargetType(),目标类型_数量.getValue())) {
                //已匹配份额
                BigDecimal marchQty = BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(), commonEntrustDto.getRemainEntrustNum()))>0?dto.getRemainNum() : commonEntrustDto.getRemainEntrustNum();
                commonEntrustDto.setMarchQty(marchQty);
                //已匹配金额
                if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    //质押券出入库成交信息中只有质押券的数量，目标类型只有数量，需要根据折算率计算标准券数量
                    if(StringUtil.equals(dto.getPs(), 交易方向_质押券入库.getValue())
                            || StringUtil.equals(dto.getPs(), 交易方向_质押券出库.getValue())) {
                        //已匹配金额
                        if (marchQty.compareTo(dto.getRemainNum()) == 0) {
                            commonEntrustDto.setMarchAmt(dto.getRemainAmount());
                        } else {
                            commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(marchQty, commonEntrustDto.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                    // modify by etliu 20220227 start
                    // 广农商二阶段 GNS-920:【广农商场内清算】广农商场内债转股清算交易生成后，对应的持仓流水更新有误
                    else if (StringUtil.equals(dto.getPs(), 交易方向_债转股.getValue())) {
                        //已匹配金额（债转股只考虑成交数据和指令一对一的场景）
                        commonEntrustDto.setMarchAmt(dto.getFpriceAmt());
                    }
                    // modify by etliu 20220227 end
                    else {
                        //已匹配金额
                        if (marchQty.compareTo(dto.getRemainNum()) == 0) {
                            commonEntrustDto.setMarchAmt(dto.getRemainAmount());
                        } else {
                            commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(marchQty,dto.getUnitFprice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                         }
                    }

                }
                else {
                    if (marchQty.compareTo(dto.getRemainNum()) == 0) {
                        commonEntrustDto.setMarchAmt(dto.getRemainAmount());
                    } else {
                        commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(marchQty, dto.getUnitCprice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                }

                //指令已匹配份额
                BigDecimal marchQtyEn = BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),commonEntrustDto.getRemainEntrustNum()))>0?dto.getRemainNum() : commonEntrustDto.getRemainEntrustNum();
                commonEntrustDto.setMarchQtyEn(marchQtyEn);
                //指令已匹配金额
                commonEntrustDto.setMarchAmtEn(ArithmeticUtil.multiply(marchQtyEn,commonEntrustDto.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            else {
                //已匹配份额
                BigDecimal entrustQty = ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),dto.getUnitCprice(),4);
                commonEntrustDto.setMarchQty(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),entrustQty))>0 ? dto.getRemainNum() : entrustQty);
                //已匹配金额
                BigDecimal entrustAmt = BigDecimal.ZERO;
                if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    entrustAmt = ArithmeticUtil.multiply(entrustQty,dto.getUnitFprice()).setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                else {
                    entrustAmt = ArithmeticUtil.multiply(entrustQty,dto.getUnitCprice()).setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                commonEntrustDto.setMarchAmt(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainAmount(),entrustAmt))>0?dto.getRemainAmount() : entrustAmt);

                //指令已匹配份额
                BigDecimal marchAmtEn = ArithmeticUtil.multiply(dto.getRemainNum(),dto.getUnitCprice()).setScale(2, BigDecimal.ROUND_HALF_UP);
                commonEntrustDto.setMarchQtyEn(ArithmeticUtil.divide(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(marchAmtEn,commonEntrustDto.getRemainEntrustAmt()))>0?marchAmtEn : commonEntrustDto.getRemainEntrustAmt(),commonEntrustDto.getPrice(),4));
                //指令已匹配金额
                commonEntrustDto.setMarchAmtEn(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(marchAmtEn,commonEntrustDto.getRemainEntrustAmt()))>0?marchAmtEn : commonEntrustDto.getRemainEntrustAmt());
            }

            if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                    || StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                if(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),commonEntrustDto.getRemainEntrustAmt())) >=0) {
                    matchStatus = 下行交易匹配状态_匹配完成.getValue();
                }
                else {
                    matchStatus = 下行交易匹配状态_部分匹配.getValue();
                }
            }
            else if((StringUtil.equals(commonEntrustDto.getTargetType(),目标类型_数量.getValue())
                    && BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),commonEntrustDto.getRemainEntrustNum())) >=0)
                    || (StringUtil.equals(commonEntrustDto.getTargetType(),目标类型_金额.getValue())
                    && BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),dto.getUnitCprice(),4))) >=0)) {
                matchStatus = 下行交易匹配状态_匹配完成.getValue();
            }
            else if((StringUtil.equals(commonEntrustDto.getTargetType(),目标类型_数量.getValue())
                    && BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),commonEntrustDto.getRemainEntrustNum())) < 0)
                    || (StringUtil.equals(commonEntrustDto.getTargetType(),目标类型_金额.getValue())
                    && BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),dto.getUnitCprice(),4))) < 0)){
                matchStatus = 下行交易匹配状态_部分匹配.getValue();
            }
            // modify by etliu 20220228 start
            // 广农商二阶段 GNS-834:【广农商场内清算】上交所基金买入卖出，手动匹配生成的交易，随交易支付费用值没登
            commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalFeeAmtPay()).setScale(2, BigDecimal.ROUND_HALF_UP));
            commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalUnFeeAmtPay()).setScale(2, BigDecimal.ROUND_HALF_UP));
            // modify by etliu 20220228 end
        }
        //多条指令匹配时
        else {
            //处理可分配份额和可分配金额
            BigDecimal totalMarchQty = BigDecimal.ZERO;
            matchStatus = 下行交易匹配状态_未匹配.getValue();
            for(ExchangeTransMatchCommonEntrustDto commonEntrustDto : commonEntrustDtoList) {
                if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                        || StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                    commonEntrustDto.setMarchQty(commonEntrustDto.getRemainEntrustAmt());
                    commonEntrustDto.setMarchAmt(commonEntrustDto.getRemainEntrustAmt());
                }
                else if(StringUtil.equals(commonEntrustDto.getTargetType(),目标类型_数量.getValue())) {
                    commonEntrustDto.setMarchQty(commonEntrustDto.getRemainEntrustNum());
                    if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                        if(StringUtil.equals(dto.getPs(), 交易方向_质押券入库.getValue())
                                || StringUtil.equals(dto.getPs(), 交易方向_质押券出库.getValue())) {
                            commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(commonEntrustDto.getRemainEntrustNum(),commonEntrustDto.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                        else {
                            commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(commonEntrustDto.getRemainEntrustNum(),dto.getUnitFprice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }

                    }
                    else {
                        commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(commonEntrustDto.getRemainEntrustNum(),dto.getUnitCprice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }

                }
                else {
                    commonEntrustDto.setMarchQty(ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),dto.getUnitCprice(),4));
                    if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                        commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),dto.getUnitCprice(),4),dto.getUnitFprice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                    else {
                        commonEntrustDto.setMarchAmt(commonEntrustDto.getRemainEntrustAmt());
                    }

                }
                totalMarchQty = ArithmeticUtil.doAdd(totalMarchQty,commonEntrustDto.getMarchQty());
            }
            // GZYHTZJY-1138:修改匹配多条指令完成度 modify by fuyongsheng start
            // if(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),totalMarchQty))>=0) {
            if(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),totalMarchQty)) < 0) {
                // GZYHTZJY-1138:修改匹配多条指令完成度 modify by fuyongsheng end
                for(ExchangeTransMatchCommonEntrustDto commonEntrustDto : commonEntrustDtoList) {
                    if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                            || StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                        commonEntrustDto.setMarchQtyEn(ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustNum(),new BigDecimal(100),2));
                        commonEntrustDto.setMarchAmtEn(commonEntrustDto.getRemainEntrustAmt());
                    }
                    else if(StringUtil.equals(commonEntrustDto.getTargetType(),目标类型_数量.getValue())) {
                        commonEntrustDto.setMarchQtyEn(commonEntrustDto.getRemainEntrustNum());
                        commonEntrustDto.setMarchAmtEn(ArithmeticUtil.multiply(commonEntrustDto.getRemainEntrustNum(),commonEntrustDto.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                    else {
                        commonEntrustDto.setMarchQtyEn(ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),commonEntrustDto.getPrice(),4));
                        commonEntrustDto.setMarchAmtEn(commonEntrustDto.getRemainEntrustAmt());
                    }

                    // modify by etliu 20220228 start
                    // 广农商二阶段 GNS-834:【广农商场内清算】上交所基金买入卖出，手动匹配生成的交易，随交易支付费用值没登
                    commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalFeeAmtPay()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalUnFeeAmtPay()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    // modify by etliu 20220228 end
                }
//                matchStatus = 下行交易匹配状态_匹配完成.getValue();
                matchStatus = 下行交易匹配状态_部分匹配.getValue();
            }
            else {
                //临时存储
                List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoListTmp = new ArrayList<>();
                String matchRule = CacheUtil.getSysParam("MATCH_RULE");
                //已经完全匹配的可匹配份额
                BigDecimal totalFinishMarchQty = BigDecimal.ZERO;
                //已经完全匹配的可匹配金额
                BigDecimal totalFinishMarchAmt = BigDecimal.ZERO;
                //已经完全匹配的可分配随交易费用
                BigDecimal totalFinishMarchFeeAmtPay = BigDecimal.ZERO;
                //已经完全匹配的可分配不随交易费用
                BigDecimal totalFinishMarchUnFeeAmtPay = BigDecimal.ZERO;


                //系统参数.匹配规则 = 按照指令下达时间顺序匹配
                if(StringUtil.equals(matchRule,"01")) {
                    commonEntrustDtoList = commonEntrustDtoList.stream().sorted(Comparator.comparing(ExchangeTransMatchCommonEntrustDto::getUpdateTime)).collect(Collectors.toList());
                }
                //系统参数.匹配规则 = 按照指令金额从高到低匹配
                else if(StringUtil.equals(matchRule,"02")) {
                    commonEntrustDtoList = commonEntrustDtoList.stream().sorted(Comparator.comparing(ExchangeTransMatchCommonEntrustDto::getRemainEntrustAmt).reversed()).collect(Collectors.toList());
                }

                if(StringUtil.equals(matchRule,"01") || StringUtil.equals(matchRule,"02")) {
                    commonEntrustDtoList = commonEntrustDtoList.stream().sorted(Comparator.comparing(ExchangeTransMatchCommonEntrustDto::getUpdateTime)).collect(Collectors.toList());
                    for(ExchangeTransMatchCommonEntrustDto commonEntrustDto : commonEntrustDtoList) {
                        //已完全匹配的可匹配份额 大于 剩余成交数量 时不再继续分配
                        if(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),totalFinishMarchQty)) >= 0){
                            break;
                        }

                        //能完全分配数据的指令
                        if(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),ArithmeticUtil.doAdd(totalFinishMarchQty,commonEntrustDto.getMarchQty()))) < 0) {
                            if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                                    || StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                                commonEntrustDto.setMarchQtyEn(ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustNum(),new BigDecimal(100),2));
                                commonEntrustDto.setMarchAmtEn(commonEntrustDto.getRemainEntrustAmt());
                            }
                            else if(StringUtil.equals(commonEntrustDto.getTargetType(),目标类型_数量.getValue())) {
                                commonEntrustDto.setMarchQtyEn(commonEntrustDto.getRemainEntrustNum());
                                commonEntrustDto.setMarchAmtEn(ArithmeticUtil.multiply(commonEntrustDto.getRemainEntrustNum(),commonEntrustDto.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                            }
                            else {
                                commonEntrustDto.setMarchQtyEn(ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),commonEntrustDto.getPrice(),4));
                                commonEntrustDto.setMarchAmtEn(commonEntrustDto.getRemainEntrustAmt());
                            }

                            commonEntrustDto.setMarchAmt(commonEntrustDto.getMarchAmt().setScale(2, BigDecimal.ROUND_HALF_UP));
                            commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalFeeAmtPay()).setScale(2, BigDecimal.ROUND_HALF_UP));
                            commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalUnFeeAmtPay()).setScale(2, BigDecimal.ROUND_HALF_UP));

                            commonEntrustDto.setLastEntrust(false);
                        }
                        //最后一笔未能完全分配数据指令
                        else {
                            //成交列表选择记录.剩余成交数量 - 匹配列表选择记录中已经完全匹配的可匹配份额
                            commonEntrustDto.setMarchQty(ArithmeticUtil.subtract(dto.getRemainNum(),totalFinishMarchQty));
                            //成交列表选择记录.剩余成交金额 - 匹配列表选择记录中已经完全匹配的可匹配金额
                            commonEntrustDto.setMarchAmt(ArithmeticUtil.subtract(dto.getRemainAmount(),totalFinishMarchAmt));

                            commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.subtract(dto.getTotalFeeAmtPay(),totalFinishMarchFeeAmtPay));
                            commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.subtract(dto.getTotalUnFeeAmtPay(),totalFinishMarchUnFeeAmtPay));

                            if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                                    || StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                                commonEntrustDto.setMarchQtyEn(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),new BigDecimal(100),2) );
                                commonEntrustDto.setMarchAmtEn(commonEntrustDto.getMarchAmt());
                            }
                            else if(StringUtil.equals(commonEntrustDto.getTargetType(),目标类型_数量.getValue())) {
                                //指令已匹配份额
                                commonEntrustDto.setMarchQtyEn(commonEntrustDto.getMarchQty());
                                //指令已匹配金额
                                commonEntrustDto.setMarchAmtEn(ArithmeticUtil.multiply(commonEntrustDto.getMarchQty(),commonEntrustDto.getPrice()));

                            }
                            else {
                                //指令已匹配份额
                                commonEntrustDto.setMarchQtyEn(ArithmeticUtil.divide(commonEntrustDto.getMarchAmt(),commonEntrustDto.getPrice(),4));
                                //指令已匹配金额
                                commonEntrustDto.setMarchAmtEn(commonEntrustDto.getMarchAmt());
                            }
                            commonEntrustDto.setLastEntrust(true);
                        }

                        totalFinishMarchQty = ArithmeticUtil.doAdd(totalFinishMarchQty,commonEntrustDto.getMarchQty());
                        totalFinishMarchAmt = ArithmeticUtil.doAdd(totalFinishMarchAmt,commonEntrustDto.getMarchAmt());
                        totalFinishMarchFeeAmtPay = ArithmeticUtil.doAdd(totalFinishMarchFeeAmtPay,commonEntrustDto.getMarchFeeAmtPay());
                        totalFinishMarchUnFeeAmtPay = ArithmeticUtil.doAdd(totalFinishMarchUnFeeAmtPay,commonEntrustDto.getMarchFeeAmtUnPay());

                        commonEntrustDtoListTmp.add(commonEntrustDto);
                    }
                }
                //系统参数.匹配规则 = 按照指令金额总体占比分配
                else if(StringUtil.equals(matchRule,"03")) {
                    // 根据组合代码批量设置组合开始日
                    this.fillVdateToExchangeTransMatchCommonEntrustDto(commonEntrustDtoList);
                    // 根据组合开始日逆序排列
                    commonEntrustDtoList = commonEntrustDtoList.stream()
                            .sorted(Comparator.comparing(ExchangeTransMatchCommonEntrustDto::getPortfolioVdate).reversed())
                            .collect(Collectors.toList());

                    for(int i = 0 ;i< commonEntrustDtoList.size(); i++) {
                        ExchangeTransMatchCommonEntrustDto commonEntrustDto = commonEntrustDtoList.get(i);
                        BigDecimal marchQty = BigDecimal.ZERO;
                        BigDecimal marchAmt = BigDecimal.ZERO;

                        if(i<commonEntrustDtoList.size()-1) {
                            marchQty = ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),totalMarchQty,4),dto.getRemainNum());
                            marchAmt = ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),totalMarchQty,4),dto.getRemainAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);

                            commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(marchQty,dto.getRemainNum(),4),dto.getTotalFeeAmtPay()).setScale(2, BigDecimal.ROUND_HALF_UP));
                            commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(marchQty,dto.getRemainNum(),4),dto.getTotalUnFeeAmtPay()).setScale(2, BigDecimal.ROUND_HALF_UP));

                            commonEntrustDto.setLastEntrust(false);
                        }
                        else {
                            marchQty = ArithmeticUtil.subtract(dto.getRemainNum(),totalFinishMarchQty);
                            marchAmt = ArithmeticUtil.subtract(dto.getRemainAmount(),totalFinishMarchAmt);

                            commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.subtract(dto.getTotalFeeAmtPay(),totalFinishMarchFeeAmtPay));
                            commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.subtract(dto.getTotalUnFeeAmtPay(),totalFinishMarchUnFeeAmtPay));

                            commonEntrustDto.setLastEntrust(true);
                        }

                        commonEntrustDto.setMarchQty(marchQty);
                        commonEntrustDto.setMarchAmt(marchAmt);
                        if(StringUtil.equals(commonEntrustDto.getTargetType(),目标类型_数量.getValue())) {
                            commonEntrustDto.setMarchQtyEn(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),totalMarchQty,4),dto.getRemainNum()));
                            commonEntrustDto.setMarchAmtEn(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),totalMarchQty,4),dto.getRemainNum(),commonEntrustDto.getPrice()));
                        }
                        else {
                            commonEntrustDto.setMarchAmtEn(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),totalMarchQty,4),dto.getRemainNum(),dto.getUnitCprice()));
                            commonEntrustDto.setMarchQtyEn(ArithmeticUtil.divide(commonEntrustDto.getMarchAmtEn(),commonEntrustDto.getPrice(),4));
                        }

                        totalFinishMarchQty = ArithmeticUtil.doAdd(totalFinishMarchQty,commonEntrustDto.getMarchQty());
                        totalFinishMarchAmt = ArithmeticUtil.doAdd(totalFinishMarchAmt,commonEntrustDto.getMarchAmt());

                        totalFinishMarchFeeAmtPay = ArithmeticUtil.doAdd(totalFinishMarchFeeAmtPay,commonEntrustDto.getMarchFeeAmtPay());
                        totalFinishMarchUnFeeAmtPay = ArithmeticUtil.doAdd(totalFinishMarchUnFeeAmtPay,commonEntrustDto.getMarchFeeAmtUnPay());

                        commonEntrustDtoListTmp.add(commonEntrustDto);
                    }
                }

                commonEntrustDtoList = new ArrayList<>();
                commonEntrustDtoList.addAll(commonEntrustDtoListTmp);
                if(dto.getRemainNum().compareTo(totalFinishMarchQty) >0 || dto.getRemainAmount().compareTo(totalFinishMarchAmt) >0){
                    matchStatus = 下行交易匹配状态_部分匹配.getValue();
                }else{
                    matchStatus = 下行交易匹配状态_匹配完成.getValue();
                }
            }
        }

        return Pair.of(matchStatus, commonEntrustDtoList);
    }

    /**
     * 计算处理已分配份额，已分配金额，指令已分配金额，指令已分配份额
     * @param dto
     * @param commonEntrustDtoList
     * @param matchStatus
     */
    private Pair<String, List<ExchangeTransMatchCommonEntrustDto>> calcShareAndAmount(ExchangeTransMatchCommonDto dto,List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoList,String matchStatus){
        //只有一条匹配指令时
        if(dto.getEntrustIdList().size() == 1) {
            ExchangeTransMatchCommonEntrustDto commonEntrustDto = commonEntrustDtoList.get(0);
            matchStatus = calcShareAndAmountWithSingleEntrust(dto, commonEntrustDto);
        }
        //多条指令匹配时
        else {
            matchStatus = calcShareAndAmountWithMultiEntrust(dto, commonEntrustDtoList);
        }

        return Pair.of(matchStatus, commonEntrustDtoList);
    }

    private String calcShareAndAmountWithSingleEntrust(ExchangeTransMatchCommonDto dto,ExchangeTransMatchCommonEntrustDto entrust) {
        // 出参 匹配状态
        String matchStatus = 下行交易匹配状态_未匹配.getValue();

        //特殊处理质押式竞价回购的分配逻辑
        if(this.isRepo(dto.getFinprodType2())) {
            //已匹配份额
            BigDecimal marchQty = this.min(entrust.getRemainEntrustNum(), this.divide100(dto.getRemainNum()));
            entrust.setMarchQty(this.multiply100(marchQty));
            //已匹配金额
            entrust.setMarchAmt(entrust.getMarchQty());
            //指令已匹配份额
            entrust.setMarchQtyEn(marchQty);
            //指令已匹配金额
            entrust.setMarchAmtEn(entrust.getMarchAmt());
            //匹配状态
            matchStatus = this.bigThan(this.divide100(dto.getRemainNum()),entrust.getRemainEntrustAmt()) ?
                    下行交易匹配状态_部分匹配.getValue() : 下行交易匹配状态_匹配完成.getValue();
        }
        else if(isNumEntrust(entrust.getTargetType())) {
            //已匹配份额
            BigDecimal marchQty = this.min(dto.getRemainNum(), entrust.getRemainEntrustNum());
            entrust.setMarchQty(marchQty);
            //已匹配金额
            if(this.isBond(dto.getFinprodType2())) {
                //质押券出入库成交信息中只有质押券的数量，目标类型只有数量，需要根据折算率计算标准券数量
                if(this.isPledgeCouponsInOrOut(dto.getPs())) {
                    entrust.setMarchAmt(this.equals(marchQty, dto.getRemainNum()) ? dto.getRemainNum() : this.multiplyWithScale2(marchQty, entrust.getPrice()));
                }
                // 广农商二阶段 GNS-920:【广农商场内清算】广农商场内债转股清算交易生成后，对应的持仓流水更新有误
                else if (isBond2Stock(dto.getPs())) {
                    //已匹配金额（债转股只考虑成交数据和指令一对一的场景）
                    entrust.setMarchAmt(dto.getFpriceAmt());
                }
                else {
                    entrust.setMarchAmt(this.equals(marchQty,dto.getRemainNum()) ? dto.getRemainAmount() : this.multiplyWithScale2(marchQty,dto.getUnitFprice()));
                }
            }
            else {
                entrust.setMarchAmt(this.equals(marchQty, dto.getRemainNum()) ? dto.getRemainAmount() : this.multiplyWithScale2(marchQty, dto.getUnitCprice()));
            }

            //指令已匹配份额
            BigDecimal marchQtyEn = this.min(dto.getRemainNum(), entrust.getRemainEntrustNum());
            entrust.setMarchQtyEn(marchQtyEn);
            //指令已匹配金额
            entrust.setMarchAmtEn(this.multiplyWithScale2(marchQtyEn, entrust.getPrice()));
            //匹配状态
            matchStatus = this.bigThan(dto.getRemainNum(),entrust.getRemainEntrustNum()) ?
                    下行交易匹配状态_部分匹配.getValue() : 下行交易匹配状态_匹配完成.getValue();
        }
        else {
            //已匹配份额
            BigDecimal entrustQty = ArithmeticUtil.divide(entrust.getRemainEntrustAmt(),dto.getUnitCprice(),4);
            entrust.setMarchQty(this.min(dto.getRemainNum(), entrustQty));
            //已匹配金额
            BigDecimal entrustAmt = this.multiplyWithScale2(entrustQty, this.isBond(dto.getFinprodType2()) ? dto.getUnitFprice() : dto.getUnitCprice());
            entrust.setMarchAmt(this.min(dto.getRemainAmount(),entrustAmt));
            //指令已匹配份额
            BigDecimal marchAmtEn = this.multiplyWithScale2(dto.getRemainNum(),dto.getUnitCprice());
            entrust.setMarchQtyEn(ArithmeticUtil.divide(this.min(marchAmtEn,entrust.getRemainEntrustAmt()),entrust.getPrice(),4));
            //指令已匹配金额
            entrust.setMarchAmtEn(this.min(marchAmtEn,entrust.getRemainEntrustAmt()));
            //匹配状态
            matchStatus = this.bigThan(dto.getRemainNum(),this.divideWithScale4(entrust.getRemainEntrustAmt(),dto.getUnitCprice())) ?
                    下行交易匹配状态_部分匹配.getValue() : 下行交易匹配状态_匹配完成.getValue();
        }

        if (Objects.nonNull(dto.getTotalFeeAmtPay())) {
            entrust.setMarchFeeAmtPay(this.divideWithScale2(this.multiply(entrust.getMarchQty(), dto.getTotalFeeAmtPay()), dto.getRemainNum()));
        }
        if (Objects.nonNull(dto.getTotalUnFeeAmtPay())) {
            entrust.setMarchFeeAmtUnPay(this.divideWithScale2(this.multiply(entrust.getMarchQty(), dto.getTotalUnFeeAmtPay()), dto.getRemainNum()));
        }

        return matchStatus;
    }

    private String calcShareAndAmountWithMultiEntrust(ExchangeTransMatchCommonDto dto, List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoList) {
        // 出参 匹配状态
        String matchStatus = 下行交易匹配状态_未匹配.getValue();

        //处理可分配份额和可分配金额
        BigDecimal totalMarchQty = BigDecimal.ZERO;
        for(ExchangeTransMatchCommonEntrustDto entrust : commonEntrustDtoList) {
            if (this.isRepo(dto.getFinprodType2())) {
                entrust.setMarchQty(entrust.getRemainEntrustAmt());
                entrust.setMarchAmt(entrust.getRemainEntrustAmt());
            }
            else if(isNumEntrust(entrust.getTargetType())) {
                entrust.setMarchQty(entrust.getRemainEntrustNum());
                if(this.isBond(dto.getFinprodType2())) {
                    entrust.setMarchAmt(this.multiplyWithScale2(entrust.getRemainEntrustNum(),
                            isPledgeCouponsInOrOut(dto.getPs()) ? entrust.getPrice() : dto.getUnitFprice()));
                }
                else {
                    entrust.setMarchAmt(this.multiplyWithScale2(entrust.getRemainEntrustNum(),dto.getUnitCprice()));
                }
            }
            else {
                entrust.setMarchQty(this.divideWithScale4(entrust.getRemainEntrustAmt(),dto.getUnitCprice()));
                entrust.setMarchAmt(this.isBond(dto.getFinprodType2()) ?
                        this.divideWithScale2(entrust.getRemainEntrustAmt().multiply(dto.getUnitFprice()), dto.getUnitCprice())
                        : entrust.getRemainEntrustAmt());
            }
            totalMarchQty = ArithmeticUtil.doAdd(totalMarchQty,entrust.getMarchQty());
        }

        // 未能完全匹配的场景
        if (this.bigThan(dto.getRemainNum(), totalMarchQty)) {
            matchStatus = 下行交易匹配状态_部分匹配.getValue();
            for(ExchangeTransMatchCommonEntrustDto entrust : commonEntrustDtoList) {
                if (this.isRepo(dto.getFinprodType2())) {
                    entrust.setMarchQtyEn(this.divide100(entrust.getRemainEntrustNum()));
                    entrust.setMarchAmtEn(entrust.getRemainEntrustAmt());
                }
                else if(isNumEntrust(entrust.getTargetType())) {
                    entrust.setMarchQtyEn(entrust.getRemainEntrustNum());
                    entrust.setMarchAmtEn(this.multiplyWithScale2(entrust.getRemainEntrustNum(),entrust.getPrice()));
                }
                else {
                    entrust.setMarchQtyEn(this.divideWithScale4(entrust.getRemainEntrustAmt(),entrust.getPrice()));
                    entrust.setMarchAmtEn(entrust.getRemainEntrustAmt());
                }
                if (Objects.nonNull(dto.getTotalFeeAmtPay())) {
                    entrust.setMarchFeeAmtPay(this.divideWithScale2(entrust.getMarchQty().multiply(dto.getTotalFeeAmtPay()), dto.getRemainNum()));
                }
                if (Objects.nonNull(dto.getTotalUnFeeAmtPay())) {
                    entrust.setMarchFeeAmtUnPay(this.divideWithScale2(entrust.getMarchQty().multiply(dto.getTotalUnFeeAmtPay()), dto.getRemainNum()));
                }
            }
        }
        // 完全匹配的场景
        else {
            //临时存储
            List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoListTmp = new ArrayList<>();
            //已经完全匹配的可匹配份额
            BigDecimal totalFinishMarchQty = BigDecimal.ZERO;
            //已经完全匹配的可匹配金额
            BigDecimal totalFinishMarchAmt = BigDecimal.ZERO;
            //已经完全匹配的可匹配净价金额
            BigDecimal totalFinishMarchCpriceAmt = BigDecimal.ZERO;
            //已经完全匹配的可分配随交易费用
            BigDecimal totalFinishMarchFeeAmtPay = BigDecimal.ZERO;
            //已经完全匹配的可分配不随交易费用
            BigDecimal totalFinishMarchUnFeeAmtPay = BigDecimal.ZERO;
            //净价总额
            BigDecimal cpriceAmt = this.multiply(dto.getRemainNum(), dto.getUnitCprice());

            //系统参数.匹配规则
            String matchRule = CacheUtil.getSysParam("MATCH_RULE");
            //系统参数.匹配规则 = 按照指令下达时间顺序匹配
            if(StringUtil.equals(matchRule,"01")) {
                commonEntrustDtoList = commonEntrustDtoList.stream().sorted(Comparator.comparing(ExchangeTransMatchCommonEntrustDto::getUpdateTime)).collect(Collectors.toList());
            }
            //系统参数.匹配规则 = 按照指令金额从高到低匹配
            else if(StringUtil.equals(matchRule,"02")) {
                commonEntrustDtoList = commonEntrustDtoList.stream()
                        .sorted(Comparator.comparing(ExchangeTransMatchCommonEntrustDto::getRemainEntrustAmt).reversed()
                            .thenComparing(ExchangeTransMatchCommonEntrustDto::getUpdateTime))
                        .collect(Collectors.toList());
            }

            if(StringUtil.equals(matchRule,"01") || StringUtil.equals(matchRule,"02")) {
                for(ExchangeTransMatchCommonEntrustDto entrust : commonEntrustDtoList) {
                    //已完全匹配的可匹配份额 大于等于 剩余成交数量 时不再继续分配
                    if (this.bigThanOrEquals(totalFinishMarchQty, dto.getRemainNum())) {
                        break;
                    }

                    //能完全分配数据的指令
                    if (this.bigThan(dto.getRemainNum(), totalFinishMarchQty.add(entrust.getMarchQty()))) {
                        if(this.isRepo(dto.getFinprodType2())) {
//                            entrust.setMarchQtyEn(this.divide100(entrust.getRemainEntrustNum()));
                            entrust.setMarchQtyEn(entrust.getRemainEntrustNum());
                            entrust.setMarchAmtEn(entrust.getRemainEntrustAmt());
                        }
                        else if(isNumEntrust(entrust.getTargetType())) {
                            entrust.setMarchQtyEn(entrust.getRemainEntrustNum());
                            entrust.setMarchAmtEn(this.multiplyWithScale2(entrust.getRemainEntrustNum(),entrust.getPrice()));
                        }
                        else {
                            entrust.setMarchQtyEn(this.divideWithScale4(entrust.getRemainEntrustAmt(),entrust.getPrice()));
                            entrust.setMarchAmtEn(entrust.getRemainEntrustAmt());
                        }

                        entrust.setMarchAmt(entrust.getMarchAmt().setScale(2, BigDecimal.ROUND_HALF_UP));
                        entrust.setMarchCpriceAmt(this.divideWithScale2(entrust.getMarchQty().multiply(cpriceAmt), dto.getRemainNum()));
                        if (Objects.nonNull(dto.getTotalFeeAmtPay())) {
                            entrust.setMarchFeeAmtPay(this.divideWithScale2(entrust.getMarchQty().multiply(dto.getTotalFeeAmtPay()), dto.getRemainNum()));
                        }
                        if (Objects.nonNull(dto.getTotalUnFeeAmtPay())) {
                            entrust.setMarchFeeAmtUnPay(this.divideWithScale2(entrust.getMarchQty().multiply(dto.getTotalUnFeeAmtPay()), dto.getRemainNum()));
                        }

                        entrust.setLastEntrust(false);
                    }
                    //最后一笔未能完全分配数据指令
                    else {
                        //成交列表选择记录.剩余成交数量 - 匹配列表选择记录中已经完全匹配的可匹配份额
                        entrust.setMarchQty(ArithmeticUtil.subtract(dto.getRemainNum(),totalFinishMarchQty));
                        //成交列表选择记录.剩余成交金额 - 匹配列表选择记录中已经完全匹配的可匹配金额
                        entrust.setMarchAmt(ArithmeticUtil.subtract(dto.getRemainAmount(),totalFinishMarchAmt));
                        //成交列表选择记录.剩余成交净价金额 - 匹配列表选择记录中已经完全匹配的可匹配净价金额
                        entrust.setMarchCpriceAmt(ArithmeticUtil.subtract(cpriceAmt,totalFinishMarchCpriceAmt));

                        if (Objects.nonNull(dto.getTotalFeeAmtPay())) {
                            entrust.setMarchFeeAmtPay(ArithmeticUtil.subtract(dto.getTotalFeeAmtPay(), totalFinishMarchFeeAmtPay));
                        }
                        if (Objects.nonNull(dto.getTotalUnFeeAmtPay())) {
                            entrust.setMarchFeeAmtUnPay(ArithmeticUtil.subtract(dto.getTotalUnFeeAmtPay(), totalFinishMarchUnFeeAmtPay));
                        }

                        if(this.isRepo(dto.getFinprodType2())) {
//                            entrust.setMarchQtyEn(this.divide100(entrust.getMarchQty()) );
                            entrust.setMarchQtyEn(entrust.getMarchQty());
                            entrust.setMarchAmtEn(entrust.getMarchAmt());
                        }
                        else if(isNumEntrust(entrust.getTargetType())) {
                            entrust.setMarchQtyEn(entrust.getMarchQty());
                            entrust.setMarchAmtEn(ArithmeticUtil.multiply(entrust.getMarchQty(),entrust.getPrice()));
                        }
                        else {
                            entrust.setMarchQtyEn(ArithmeticUtil.divide(entrust.getMarchAmt(),entrust.getPrice(),4));
                            entrust.setMarchAmtEn(entrust.getMarchAmt());
                        }
                        entrust.setLastEntrust(true);
                    }

                    totalFinishMarchQty = ArithmeticUtil.doAdd(totalFinishMarchQty,entrust.getMarchQty());
                    totalFinishMarchAmt = ArithmeticUtil.doAdd(totalFinishMarchAmt,entrust.getMarchAmt());
                    totalFinishMarchCpriceAmt = ArithmeticUtil.doAdd(totalFinishMarchCpriceAmt,entrust.getMarchCpriceAmt());
                    totalFinishMarchFeeAmtPay = ArithmeticUtil.doAdd(totalFinishMarchFeeAmtPay,entrust.getMarchFeeAmtPay());
                    totalFinishMarchUnFeeAmtPay = ArithmeticUtil.doAdd(totalFinishMarchUnFeeAmtPay,entrust.getMarchFeeAmtUnPay());

                    commonEntrustDtoListTmp.add(entrust);
                }
            }
            //系统参数.匹配规则 = 按照指令金额总体占比分配
            else if(StringUtil.equals(matchRule,"03")) {
                // 根据组合代码批量设置组合开始日
                this.fillVdateToExchangeTransMatchCommonEntrustDto(commonEntrustDtoList);
                // 根据组合开始日逆序排列
                commonEntrustDtoList = commonEntrustDtoList.stream()
                        .sorted(Comparator.comparing(ExchangeTransMatchCommonEntrustDto::getPortfolioVdate).reversed())
                        .collect(Collectors.toList());

                for(int i = 0 ;i< commonEntrustDtoList.size(); i++) {
                    ExchangeTransMatchCommonEntrustDto entrust = commonEntrustDtoList.get(i);
                    BigDecimal marchQty = BigDecimal.ZERO;
                    BigDecimal marchAmt = BigDecimal.ZERO;
                    BigDecimal marchCpriceAmt = BigDecimal.ZERO;

                    if(i<commonEntrustDtoList.size()-1) {
                        marchQty = this.divideWithScale2(this.multiply(entrust.getMarchQty(), dto.getRemainNum()), totalMarchQty);
                        marchAmt = this.divideWithScale2(this.multiply(entrust.getMarchQty(), dto.getRemainAmount()), totalMarchQty);
                        marchCpriceAmt = this.divideWithScale2(this.multiply(entrust.getMarchQty(), cpriceAmt), totalMarchQty);

                        if (Objects.nonNull(dto.getTotalFeeAmtPay())) {
                            entrust.setMarchFeeAmtPay(this.divideWithScale2(this.multiply(marchQty, dto.getTotalFeeAmtPay()), dto.getRemainNum()));
                        }
                        if (Objects.nonNull(dto.getTotalUnFeeAmtPay())) {
                            entrust.setMarchFeeAmtUnPay(this.divideWithScale2(this.multiply(marchQty, dto.getTotalUnFeeAmtPay()), dto.getRemainNum()));
                        }

                        entrust.setLastEntrust(false);
                    }
                    else {
                        marchQty = ArithmeticUtil.subtract(dto.getRemainNum(),totalFinishMarchQty);
                        marchAmt = ArithmeticUtil.subtract(dto.getRemainAmount(),totalFinishMarchAmt);
                        marchCpriceAmt = ArithmeticUtil.subtract(cpriceAmt,totalFinishMarchCpriceAmt);

                        if (Objects.nonNull(dto.getTotalFeeAmtPay())) {
                            entrust.setMarchFeeAmtPay(ArithmeticUtil.subtract(dto.getTotalFeeAmtPay(), totalFinishMarchFeeAmtPay));
                        }
                        if (Objects.nonNull(dto.getTotalUnFeeAmtPay())) {
                            entrust.setMarchFeeAmtUnPay(ArithmeticUtil.subtract(dto.getTotalUnFeeAmtPay(), totalFinishMarchUnFeeAmtPay));
                        }

                        entrust.setLastEntrust(true);
                    }

                    entrust.setMarchQty(marchQty);
                    entrust.setMarchAmt(marchAmt);
                    entrust.setMarchCpriceAmt(marchCpriceAmt);

                    if(this.isRepo(dto.getFinprodType2())) {
                        entrust.setMarchQtyEn(this.divide100(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrust.getMarchQty(),totalMarchQty,4),dto.getRemainNum())));
                        entrust.setMarchAmtEn(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrust.getMarchQty(),totalMarchQty,4),dto.getRemainNum()));
                    }
                    else if(isNumEntrust(entrust.getTargetType())) {
                        entrust.setMarchQtyEn(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrust.getMarchQty(),totalMarchQty,4),dto.getRemainNum()));
                        entrust.setMarchAmtEn(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrust.getMarchQty(),totalMarchQty,4),dto.getRemainNum(),entrust.getPrice()));
                    }
                    else {
                        entrust.setMarchAmtEn(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrust.getMarchQty(),totalMarchQty,4),dto.getRemainNum(),dto.getUnitCprice()));
                        entrust.setMarchQtyEn(ArithmeticUtil.divide(entrust.getMarchAmtEn(),entrust.getPrice(),4));
                    }

                    totalFinishMarchQty = ArithmeticUtil.doAdd(totalFinishMarchQty,entrust.getMarchQty());
                    totalFinishMarchAmt = ArithmeticUtil.doAdd(totalFinishMarchAmt,entrust.getMarchAmt());
                    totalFinishMarchCpriceAmt = ArithmeticUtil.doAdd(totalFinishMarchCpriceAmt,entrust.getMarchCpriceAmt());
                    totalFinishMarchFeeAmtPay = ArithmeticUtil.doAdd(totalFinishMarchFeeAmtPay,entrust.getMarchFeeAmtPay());
                    totalFinishMarchUnFeeAmtPay = ArithmeticUtil.doAdd(totalFinishMarchUnFeeAmtPay,entrust.getMarchFeeAmtUnPay());

                    commonEntrustDtoListTmp.add(entrust);
                }
            }

            commonEntrustDtoList = new ArrayList<>();
            commonEntrustDtoList.addAll(commonEntrustDtoListTmp);

            if(dto.getRemainNum().compareTo(totalFinishMarchQty) >0
                    || dto.getRemainAmount().compareTo(totalFinishMarchAmt) >0){
                matchStatus = 下行交易匹配状态_部分匹配.getValue();
            }else{
                matchStatus = 下行交易匹配状态_匹配完成.getValue();
            }
        }
        return matchStatus;
    }

    /**
     * 根据组合代码批量设置组合开始日
     * @param commonEntrustDtoList
     */
    private void fillVdateToExchangeTransMatchCommonEntrustDto(List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoList) {

        Map<String, Object> params = new HashMap<>();
        params.put("portfolioIdList", commonEntrustDtoList.stream().map(ExchangeTransMatchCommonEntrustDto::getPortfolioId).collect(Collectors.toList()));
        List<PtlPortfolio> portfolioVdateInfos = exchangeTransMatchCommonMapper.getPortfolioVdateInfos(params);
        logger.debug("【组合开始日数据 => {}】", JsonUtils.object2Json(portfolioVdateInfos));

        Map<String, Date> map = portfolioVdateInfos.stream().collect(Collectors.toMap(PtlPortfolio::getPortfolioId,
                PtlPortfolio::getVdate, (oldValue, newValue) -> oldValue));
        commonEntrustDtoList.forEach(item -> item.setPortfolioVdate(map.get(item.getPortfolioId())));
    }

    /**
     * 获取组合开始日最小的组合代码
     * @param portfolioIdList 组合代码列表
     */
    private String getPortfolioIdByMinVdate(List<String> portfolioIdList) {
        Map<String, Object> params = new HashMap<>();
        params.put("portfolioIdList", portfolioIdList);
        List<PtlPortfolio> portfolioVdateInfos = exchangeTransMatchCommonMapper.getPortfolioVdateInfos(params);
        logger.debug("【组合开始日数据 => {}】", JsonUtils.object2Json(portfolioVdateInfos));

        String minVdatePortfolioId = portfolioVdateInfos.stream()
                .sorted(Comparator.comparing(PtlPortfolio::getVdate))
                .map(PtlPortfolio::getPortfolioId)
                .findFirst()
                .orElse(portfolioIdList.get(0));
        logger.debug("【组合开始日最小的组合代码 => {}】", minVdatePortfolioId);
        return minVdatePortfolioId;
    }

    /**
     * 计算处理已分配份额，已分配金额，指令已分配金额，指令已分配份额(质押式协议回购)
     * @param dto
     * @param commonEntrustDtoList
     * @param matchStatus
     */
    private Pair<String, List<ExchangeTransMatchCommonEntrustDto>> calcAgreRepShareAndAmount(ExchangeTransMatchCommonDto dto, List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoList, String matchStatus){
        //只有一条匹配指令时
        if(dto.getEntrustIdList().size() == 1) {
            ExchangeTransMatchCommonEntrustDto commonEntrustDto = commonEntrustDtoList.get(0);
            if(isNumEntrust(commonEntrustDto.getTargetType())) {
                //已匹配份额
                BigDecimal marchQty =dto.getRemainNum();
                commonEntrustDto.setMarchQty(marchQty);
                //指令已匹配份额
                commonEntrustDto.setMarchQtyEn(marchQty);
            } else {
                BigDecimal marchAmt = dto.getRemainAmount();
                //已匹配金额=已分配金额
                commonEntrustDto.setMarchAmt(marchAmt);
                //指令已匹配金额 = 指令已匹配金额
                commonEntrustDto.setMarchAmtEn(marchAmt);
            }
            commonEntrustDto.setMarchFeeAmtPay(dto.getTotalFeeAmtPay());
            commonEntrustDto.setMarchFeeAmtUnPay(dto.getTotalUnFeeAmtPay());
            matchStatus = 下行交易匹配状态_匹配完成.getValue();
        }
        //多条指令匹配时(协议回购只有首期会有多条指令)
        else {
            //处理可分配份额和可分配金额
            BigDecimal totalMarchQty = BigDecimal.ZERO;
            for(ExchangeTransMatchCommonEntrustDto commonEntrustDto : commonEntrustDtoList) {
                commonEntrustDto.setMarchQty(commonEntrustDto.getRemainEntrustNum());
                commonEntrustDto.setMarchAmt(commonEntrustDto.getRemainEntrustNum());

                totalMarchQty = ArithmeticUtil.doAdd(totalMarchQty,commonEntrustDto.getMarchQty());
            }

            if(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),totalMarchQty))>=0) {
                for(ExchangeTransMatchCommonEntrustDto commonEntrustDto : commonEntrustDtoList) {
                    commonEntrustDto.setMarchQtyEn(commonEntrustDto.getRemainEntrustNum());
                    commonEntrustDto.setMarchAmtEn(commonEntrustDto.getRemainEntrustAmt());
                }
                matchStatus = 下行交易匹配状态_匹配完成.getValue();
            }
            else {
                //临时存储
                List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoListTmp = new ArrayList<>();
                String matchRule = CacheUtil.getSysParam("MATCH_RULE");
                //已经完全匹配的可匹配份额
                BigDecimal totalFinishMarchQty = BigDecimal.ZERO;
                //已经完全匹配的可匹配金额
                BigDecimal totalFinishMarchAmt = BigDecimal.ZERO;
                //已经完全匹配的可分配随交易费用
                BigDecimal totalFinishMarchFeeAmtPay = BigDecimal.ZERO;
                //已经完全匹配的可分配不随交易费用
                BigDecimal totalFinishMarchUnFeeAmtPay = BigDecimal.ZERO;


                //系统参数.匹配规则 = 按照指令下达时间顺序匹配
                if(StringUtil.equals(matchRule,"01")) {
                    commonEntrustDtoList = commonEntrustDtoList.stream().sorted(Comparator.comparing(ExchangeTransMatchCommonEntrustDto::getUpdateTime)).collect(Collectors.toList());
                }
                //系统参数.匹配规则 = 按照指令金额从高到低匹配
                else if(StringUtil.equals(matchRule,"02")) {
                    commonEntrustDtoList = commonEntrustDtoList.stream().sorted(Comparator.comparing(ExchangeTransMatchCommonEntrustDto::getRemainEntrustAmt).reversed()).collect(Collectors.toList());
                }

                if(StringUtil.equals(matchRule,"01") || StringUtil.equals(matchRule,"02")) {
                    commonEntrustDtoList = commonEntrustDtoList.stream().sorted(Comparator.comparing(ExchangeTransMatchCommonEntrustDto::getUpdateTime)).collect(Collectors.toList());
                    for(ExchangeTransMatchCommonEntrustDto commonEntrustDto : commonEntrustDtoList) {
                        //已完全匹配的可匹配份额 大于 剩余成交数量 时不再继续分配
                        if(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),totalFinishMarchQty)) >= 0){
                            break;
                        }

                        //能完全分配数据的指令
                        if(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),ArithmeticUtil.doAdd(totalFinishMarchQty,commonEntrustDto.getMarchQty()))) < 0) {
                            commonEntrustDto.setMarchQtyEn(commonEntrustDto.getRemainEntrustNum());
                            commonEntrustDto.setMarchAmtEn(commonEntrustDto.getRemainEntrustNum());

                            commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalFeeAmtPay()));
                            commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalUnFeeAmtPay()));

                            commonEntrustDto.setLastEntrust(false);
                        }
                        //最后一笔未能完全分配数据指令
                        else {
                            //成交列表选择记录.剩余成交数量 - 匹配列表选择记录中已经完全匹配的可匹配份额
                            commonEntrustDto.setMarchQty(ArithmeticUtil.doAdd(ArithmeticUtil.subtract(dto.getRemainNum(),totalFinishMarchQty),commonEntrustDto.getMarchQty()));
                            //成交列表选择记录.剩余成交金额 - 匹配列表选择记录中已经完全匹配的可匹配金额
                            commonEntrustDto.setMarchAmt(ArithmeticUtil.doAdd(ArithmeticUtil.subtract(dto.getRemainAmount(),totalFinishMarchAmt),commonEntrustDto.getMarchAmt()));

                            commonEntrustDto.setMarchQtyEn(commonEntrustDto.getMarchQty());
                            commonEntrustDto.setMarchAmtEn(commonEntrustDto.getMarchAmt());

                            commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.subtract(dto.getTotalFeeAmtPay(),totalFinishMarchFeeAmtPay));
                            commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.subtract(dto.getTotalUnFeeAmtPay(),totalFinishMarchUnFeeAmtPay));

                            commonEntrustDto.setLastEntrust(true);
                        }

                        totalFinishMarchQty = ArithmeticUtil.doAdd(totalFinishMarchQty,commonEntrustDto.getMarchQty());
                        totalFinishMarchAmt = ArithmeticUtil.doAdd(totalFinishMarchAmt,commonEntrustDto.getMarchAmt());
                        totalFinishMarchFeeAmtPay = ArithmeticUtil.doAdd(totalFinishMarchFeeAmtPay,commonEntrustDto.getMarchFeeAmtPay());
                        totalFinishMarchUnFeeAmtPay = ArithmeticUtil.doAdd(totalFinishMarchUnFeeAmtPay,commonEntrustDto.getMarchFeeAmtUnPay());

                        commonEntrustDtoListTmp.add(commonEntrustDto);
                    }
                }
                //系统参数.匹配规则 = 按照指令金额总体占比分配
                else if(StringUtil.equals(matchRule,"03")) {
                    for(int i = 0 ;i< commonEntrustDtoList.size(); i++) {
                        ExchangeTransMatchCommonEntrustDto commonEntrustDto = commonEntrustDtoList.get(i);
                        BigDecimal marchQty = BigDecimal.ZERO;
                        BigDecimal marchAmt = BigDecimal.ZERO;

                        if(i<commonEntrustDtoList.size()-1) {
                            marchQty = ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),totalMarchQty,4),dto.getRemainNum());
                            marchAmt = ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),totalMarchQty,4),dto.getRemainNum());

                            commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(marchQty,dto.getRemainNum(),4),dto.getTotalFeeAmtPay()));
                            commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(marchQty,dto.getRemainNum(),4),dto.getTotalUnFeeAmtPay()));

                            commonEntrustDto.setLastEntrust(false);
                        }
                        else {
                            marchQty = ArithmeticUtil.subtract(dto.getRemainNum(),totalFinishMarchQty);
                            marchAmt = ArithmeticUtil.subtract(dto.getRemainAmount(),totalFinishMarchAmt);

                            commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.subtract(dto.getTotalFeeAmtPay(),totalFinishMarchFeeAmtPay));
                            commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.subtract(dto.getTotalUnFeeAmtPay(),totalFinishMarchUnFeeAmtPay));

                            commonEntrustDto.setLastEntrust(true);
                        }

                        commonEntrustDto.setMarchQty(marchQty);
                        commonEntrustDto.setMarchAmt(marchAmt);
                        if(isNumEntrust(commonEntrustDto.getTargetType())) {
                            commonEntrustDto.setMarchQtyEn(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),totalMarchQty,4),dto.getRemainNum()));
                            commonEntrustDto.setMarchAmtEn(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),totalMarchQty,4),dto.getRemainNum(),commonEntrustDto.getPrice()));
                        }
                        else {
                            commonEntrustDto.setMarchAmtEn(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),totalMarchQty,4),dto.getRemainNum(),dto.getUnitCprice()));
                            commonEntrustDto.setMarchQtyEn(ArithmeticUtil.divide(commonEntrustDto.getMarchAmtEn(),commonEntrustDto.getPrice(),4));
                        }

                        totalFinishMarchQty = ArithmeticUtil.doAdd(totalFinishMarchQty,commonEntrustDto.getMarchQty());
                        totalFinishMarchAmt = ArithmeticUtil.doAdd(totalFinishMarchAmt,commonEntrustDto.getMarchAmt());

                        totalFinishMarchFeeAmtPay = ArithmeticUtil.doAdd(totalFinishMarchFeeAmtPay,commonEntrustDto.getMarchFeeAmtPay());
                        totalFinishMarchUnFeeAmtPay = ArithmeticUtil.doAdd(totalFinishMarchUnFeeAmtPay,commonEntrustDto.getMarchFeeAmtUnPay());

                        commonEntrustDtoListTmp.add(commonEntrustDto);
                    }
                }

                commonEntrustDtoList = new ArrayList<>();
                commonEntrustDtoList.addAll(commonEntrustDtoListTmp);
                matchStatus = 下行交易匹配状态_部分匹配.getValue();
            }
        }

        return Pair.of(matchStatus, commonEntrustDtoList);
    }

    /**
     * 更新指令状态为已接收
     * @param needUpdateStatusEntrustIdList
     */
    public void updateEntrustStatus(List<String> needUpdateStatusEntrustIdList) {
        if (StringUtil.isEmptyList(needUpdateStatusEntrustIdList)) {
            return;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("entrustStatus", DictCodeList.委托状态_已接收.getValue());

        int oneTimeCount = 100;
        int grps = needUpdateStatusEntrustIdList.size() / oneTimeCount;
        int idx = 0;

        for ( int i = 0; i < grps; i++ ) {
            List<String> list1 = needUpdateStatusEntrustIdList.subList(idx, idx + oneTimeCount);
            if ( list1.size() > 0 ) {
                paramMap.put("entrustIdList", list1);
                exchangeTransMatchCommonMapper.updateEntrustStatus(paramMap);
            }
            idx += oneTimeCount;
        }
        List<String> list2 = needUpdateStatusEntrustIdList.subList(idx, needUpdateStatusEntrustIdList.size());
        if ( list2.size() > 0 ) {
            paramMap.put("entrustIdList", list2);
            exchangeTransMatchCommonMapper.updateEntrustStatus(paramMap);
        }
    }

    public String getTradeIdByTotalTradeId(String totalTradeId){
        return exchangeTransMatchCommonMapper.getTradeIdByTotalTradeId(totalTradeId);
    }

    private BigDecimal select券面面值(String finprodId) {
        Map<String, Object> params = new HashMap<>();
        params.put("finprodId", finprodId);
        params.put("tradeDate", com.joyintech.framework.util.DateUtil.platDateFromCache());
        BigDecimal faceValue = creditLinesDealsMapper.getFaceValue(params);
        return null == faceValue ? new BigDecimal(100) : faceValue;
    }

    public BigDecimal min(BigDecimal num1, BigDecimal num2) {
       return this.bigThan(num1, num2) ? num2 : num1;
    }

    public BigDecimal max(BigDecimal num1, BigDecimal num2) {
        return this.bigThan(num1, num2) ? num1 : num2;
    }

    public boolean bigThan(BigDecimal d, BigDecimal decimal) {
        if (Objects.nonNull(d) && Objects.nonNull(decimal)) {
            return d.compareTo(decimal) > 0;
        }
        return false;
    }

    public boolean bigThanOrEquals(BigDecimal d, BigDecimal decimal) {
        if (Objects.nonNull(d) && Objects.nonNull(decimal)) {
            return d.compareTo(decimal) >= 0;
        }
        return false;
    }

    public boolean equals(BigDecimal d, BigDecimal decimal) {
        if (Objects.nonNull(d) && Objects.nonNull(decimal)) {
            return d.compareTo(decimal) == 0;
        }
        return false;
    }

    public boolean bigThanZero(BigDecimal value) {
        return bigThan(value, BigDecimal.ZERO);
    }

    public BigDecimal multiply(BigDecimal d, BigDecimal num) {
        return ArithmeticUtil.multiply(d, num);
    }

    public BigDecimal multiplyWithScale2(BigDecimal d, BigDecimal num) {
        return ArithmeticUtil.multiply(d, num).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal divideWithScale4(BigDecimal d, BigDecimal num) {
        return ArithmeticUtil.divide(d, num, 4, RoundingMode.HALF_UP);
    }

    public BigDecimal divideWithScale2(BigDecimal d, BigDecimal num) {
        return ArithmeticUtil.divide(d, num, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal divide100(BigDecimal num) {
        return this.divideWithScale2(num, new BigDecimal(100));
    }

    public BigDecimal multiply100(BigDecimal num) {
        return this.multiplyWithScale2(num, new BigDecimal(100));
    }

    /**
     * 根据交易方向判断是否是质押券出入库交易
     * @param ps 交易方向
     * @return  是否是质押券出入库交易
     */
    private boolean isPledgeCouponsInOrOut(String ps) {
        return StringUtil.equals(ps, 交易方向_质押券入库.getValue())
                || StringUtil.equals(ps, 交易方向_质押券出库.getValue());
    }

    private boolean isBond(String finprodType) {
        return DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType);
    }

    private boolean isRepo(String finprodType) {
        return DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(finprodType)
                || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(finprodType);
    }

    /**
     * 根据交易方向判断是否是债转股交易
     * @param ps 交易方向
     * @return  是否是债转股交易
     */
    private boolean isBond2Stock(String ps) {
        return StringUtil.equals(ps, 交易方向_债转股.getValue());
    }

    private boolean isAmtEntrust(String targetType) {
        return StringUtil.equals(targetType, 目标类型_金额.getValue());
    }

    private boolean isNumEntrust(String targetType) {
        return StringUtil.equals(targetType, 目标类型_数量.getValue());
    }
}
