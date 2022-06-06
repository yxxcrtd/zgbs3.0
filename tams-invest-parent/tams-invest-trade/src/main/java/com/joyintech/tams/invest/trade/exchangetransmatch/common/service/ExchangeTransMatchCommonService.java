package com.joyintech.tams.invest.trade.exchangetransmatch.common.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.trade.dto.MinTrdUnitLimitDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bidding.ExchangeTransMatchBidding1Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.credit.ExchangeTransMatchCredit1Service;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.FinDealPledgeMapper;
import com.joyintech.tams.common.base.dao.FinPledgeDisplaceDetailMapper;
import com.joyintech.tams.common.base.dao.FinPledgeDisplaceMapper;
import com.joyintech.tams.common.base.dao.FinPledgeDisplaceResultMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.common.base.dao.PaTrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.PaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.PtlTradeMapper;
import com.joyintech.tams.common.base.dao.SettleDataTotalPledgeMatchMapper;
import com.joyintech.tams.common.base.dao.SettleTransDataTotalAddMapper;
import com.joyintech.tams.common.base.dao.SettleTransDataTotalMapper;
import com.joyintech.tams.common.base.dao.SettleTransDataTotalMatchMapper;
import com.joyintech.tams.common.base.dao.SettleTransDataTotalStatusMapper;
import com.joyintech.tams.common.base.dao.SettleTransTgMappingMapper;
import com.joyintech.tams.common.base.dao.StlPayOrdersMapper;
import com.joyintech.tams.common.base.dao.TrdDealTotalAddMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductBarterMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductBarterTotalMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealMapper;
import com.joyintech.tams.common.base.dao.TrdStasecFlowDetailMapper;
import com.joyintech.tams.common.base.dao.TrdStasecFlowMapper;
import com.joyintech.tams.common.base.dao.TrdTradeFeeDetailMapper;
import com.joyintech.tams.common.base.dao.TrdTradeFeeTotalMapper;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
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
    private EtrEntrustAddMapper etrEntrustAddMapper;

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

    @Value("${application.micorservice.contextPath.tamsServer:http://tams-server}")
    private String tamsSerever;

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextPath.single:false}")
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
                settleTransDataTotalStatus.setMatchStatus(DictCodeListExt3.下行交易匹配状态_匹配完成.getValue());
                settleTransDataTotalStatus.setUpdateTime(new Date());
                settleTransDataTotalStatus.setUpdateUser(StringUtil.isNotEmpty(LoginUserUtil.getUserName())?LoginUserUtil.getUserName():"system");
                settleTransDataTotalStatusMapper.updateByPrimaryKey(settleTransDataTotalStatus);
            } else {
                settleTransDataTotalStatus = new SettleTransDataTotalStatus();
                settleTransDataTotalStatus.setSeqNo(dto.getSeqId());
                settleTransDataTotalStatus.setMatchStatus(DictCodeListExt3.下行交易匹配状态_匹配完成.getValue());
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
                            ?DictCodeListExt3.下行交易匹配状态_未匹配.getValue():DictCodeListExt3.下行交易匹配状态_部分匹配.getValue());
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
        entrustMap.put("PS_T93", DictCodeListExt3.交易方向_解除质押.getValue());
        entrustMap.put("PS_T94", DictCodeListExt3.交易方向_中途换券.getValue());
        entrustMap.put("TARGET_CLASS_01", DictCodeListExt3.目标类型_数量.getValue());
        entrustMap.put("TARGET_CLASS_02", DictCodeListExt3.目标类型_金额.getValue());
        entrustMap.put("DISPLACE_DIRECTION_01", DictCodeListExt3.质押券置换_置换方向_换入.getValue());
        entrustMap.put("DISPLACE_DIRECTION_02", DictCodeListExt3.质押券置换_置换方向_换出.getValue());
        List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoList = exchangeTransMatchCommonMapper.getEtrEntrustByIdList(entrustMap);
        if(!StringUtil.isEmptyList(commonEntrustDtoList)) {
            String matchStatus = DictCodeListExt3.下行交易匹配状态_未匹配.getValue();

            if((StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                    || StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()))
                && StringUtil.equals(dto.getSettleTransDataTotalAdd().getRepoType(),DictCodeList.回购类型_质押式协议回购.getValue())){
                matchStatus = this.calcAgreRepShareAndAmount(dto,commonEntrustDtoList,matchStatus);
            }
            else {
                matchStatus = this.calcShareAndAmount(dto,commonEntrustDtoList,matchStatus);
            }

            // 处理多组合系列户指令的组合分配
            this.handleMultiPortfolioAllocation(commonEntrustDtoList);

            //处理匹配成交相关的表
            this.handleSettleTransTableInfo(dto,commonEntrustDtoList,matchStatus);
        }
        logger.debug("处理成交匹配数据---结束");
    }

    /**
     * 处理多组合系列户指令的组合分配
     * @param commonEntrustDtoList
     */
    private void handleMultiPortfolioAllocation(List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoList){
        //系列户指令处理组合分配
        commonEntrustDtoList.stream().filter(item->StringUtil.equals(item.getEntrustClass(),DictCodeListExt3.投资指令类型_多组合单资产指令.getValue())).forEach(item->{
            List<ExchangeTransMatchCommonEntrustDetailDto> entrustDetailDtoList = exchangeTransMatchCommonMapper.getEntrustDetailList(item.getEntrustId());
            if(!StringUtil.isEmptyList(entrustDetailDtoList)) {
                // 获取最小交易单元
                MinTrdUnitLimitDto minTrdUnitLimitDto = this.getMinTrdUnitLimit(item);
                // 获取各组合历史已分配指令数量
                Map<String, BigDecimal> portfolioHisMatchEnMap = this.getPortfolioHisMatchEnInfo(item.getEntrustId());
                // 计算目前各组合剩余分配数量
                Map<String, BigDecimal> portfolioRemainQtyMap = this.portfolioRemainQtyInfo(portfolioHisMatchEnMap, entrustDetailDtoList);
                // 目前各组合剩余分配数量总数
                BigDecimal portfolioRemainQtyTotal = portfolioRemainQtyMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                // 本次剩余分配总数
                BigDecimal remainMatchQty = item.getMarchQty();
                // 本次各组合分配数量
                Map<String, BigDecimal> portfolioMatchQtyMap = new HashMap<>();
                // 1、按目前各组合剩余分配数量,降序排列
                List<Map.Entry<String, BigDecimal>> portfolioRemainQtyList = new ArrayList<>(portfolioRemainQtyMap.entrySet());
                portfolioRemainQtyList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                // 2、循环一轮,每个组合赋值最小交易数量,如不够一轮循环,则将剩余数量赋值于当前不够的组合
                for(Map.Entry<String, BigDecimal> portfolioEntry : portfolioRemainQtyList){
                    if(ArithmeticUtil.subtract(remainMatchQty, minTrdUnitLimitDto.getMinNum()).compareTo(BigDecimal.ZERO) < 0){
                        portfolioMatchQtyMap.put(portfolioEntry.getKey(), remainMatchQty);
                        remainMatchQty = BigDecimal.ZERO;
                        break;
                    }else{
                        portfolioMatchQtyMap.put(portfolioEntry.getKey(), minTrdUnitLimitDto.getMinNum());
                        remainMatchQty = ArithmeticUtil.subtract(remainMatchQty, minTrdUnitLimitDto.getMinNum());
                    }
                }
                // 3、如循环一轮后仍有剩余数量,则按照 各组合剩余分配数量比例 进行拆分,拆分结果需满足最小交易单元的整数倍
                if(remainMatchQty.compareTo(BigDecimal.ZERO) > 0){
                    logger.debug("【指令编号:{},循环一轮后剩余数量为:{}】", item.getEntrustId(), remainMatchQty);
                    BigDecimal minStep = minTrdUnitLimitDto.getMinStep();
                    for (Map.Entry<String, BigDecimal> portfolioEntry : portfolioRemainQtyList) {
                        BigDecimal portfolioRemainQty = portfolioRemainQtyMap.get(portfolioEntry.getKey());
                        // 分配比例
                        BigDecimal ratio = ArithmeticUtil.divide(portfolioRemainQty, portfolioRemainQtyTotal, 4);
                        // 临时分配数量
                        BigDecimal matchQtyTemp = ArithmeticUtil.multiply(remainMatchQty, ratio);
                        // 最小交易单元整数倍
                        BigDecimal stepTimes = ArithmeticUtil.divide(matchQtyTemp, minStep, 0, RoundingMode.FLOOR);
                        // 分配数量
                        BigDecimal matchQty = ArithmeticUtil.multiply(minStep, stepTimes);
                        // 合并分配结果
                        portfolioMatchQtyMap.put(portfolioEntry.getKey(), ArithmeticUtil.doAdd(matchQty,
                                portfolioMatchQtyMap.get(portfolioEntry.getKey())));
                        // 剩余分配数量
                        remainMatchQty = ArithmeticUtil.subtract(remainMatchQty, matchQty);
                    }
                }
                // 4、如按比例拆分完,仍有剩余数量尾差,则按照最小交易单元按顺序依次分配,直至分配完为止
                if(remainMatchQty.compareTo(BigDecimal.ZERO) > 0){
                    logger.debug("【指令编号:{},比例拆分完后剩余尾差数量为:{}】", item.getEntrustId(), remainMatchQty);
                    // 尾差分配数量Map
                    Map<String, BigDecimal> tailMatchQtyMap = new HashMap<>();
                    for (Map.Entry<String, BigDecimal> portfolioEntry : portfolioRemainQtyList) {
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
                logger.debug("【指令编号:{},最终分配数量结果为:{}】", item.getEntrustId(), JSON.toJSONString(portfolioMatchQtyMap));
                // 5、根据本次分配数量计算 分配金额和交易支付费用
                // 本次各组合分配金额
                Map<String, BigDecimal> portfolioMatchAmtMap = new HashMap<>();
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
                    portfolioMatchAmtMap.put(portfolioId, ArithmeticUtil.multiply(matchQty,item.getPrice()));

                    BigDecimal matchFeeAmtPay;
                    BigDecimal matchFeeAmtUnPay;
                    if(index != length-1){
                        matchFeeAmtPay = ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getMarchFeeAmtPay(), matchQty),
                                item.getMarchQty(), 4);
                        marchTotalFeeAmtPay = ArithmeticUtil.doAdd(marchTotalFeeAmtPay, matchFeeAmtPay);

                        matchFeeAmtUnPay = ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getMarchFeeAmtUnPay(), matchQty),
                                item.getMarchQty(), 4);
                        marchTotalFeeAmtUnPay = ArithmeticUtil.doAdd(marchTotalFeeAmtUnPay, matchFeeAmtUnPay);

                        portfolioMatchFeeAmtPayMap.put(portfolioId, matchFeeAmtPay);
                        portfolioMatchFeeAmtUnPayMap.put(portfolioId, matchFeeAmtUnPay);
                    }else{
                        portfolioMatchFeeAmtPayMap.put(portfolioId, ArithmeticUtil.subtract(item.getMarchFeeAmtPay(), marchTotalFeeAmtPay));
                        portfolioMatchFeeAmtUnPayMap.put(portfolioId, ArithmeticUtil.subtract(item.getMarchFeeAmtUnPay(), marchTotalFeeAmtUnPay));
                    }
                }
                for(ExchangeTransMatchCommonEntrustDetailDto entrustDetailDto : entrustDetailDtoList) {
                    entrustDetailDto.setMarchQty(null == portfolioMatchQtyMap.get(entrustDetailDto.getPortfolioId()) ?
                            BigDecimal.ZERO : portfolioMatchQtyMap.get(entrustDetailDto.getPortfolioId()));
                    entrustDetailDto.setMarchAmt(null == portfolioMatchAmtMap.get(entrustDetailDto.getPortfolioId()) ?
                            BigDecimal.ZERO : portfolioMatchAmtMap.get(entrustDetailDto.getPortfolioId()));
                    entrustDetailDto.setMarchFeeAmtPay(null == portfolioMatchFeeAmtPayMap.get(entrustDetailDto.getPortfolioId()) ?
                            BigDecimal.ZERO : portfolioMatchFeeAmtPayMap.get(entrustDetailDto.getPortfolioId()));
                    entrustDetailDto.setMarchFeeAmtUnPay(null == portfolioMatchFeeAmtUnPayMap.get(entrustDetailDto.getPortfolioId()) ?
                            BigDecimal.ZERO : portfolioMatchFeeAmtUnPayMap.get(entrustDetailDto.getPortfolioId()));
                }
                logger.debug("【指令编号:{},最终结果为:{}】", item.getEntrustId(), JSON.toJSONString(entrustDetailDtoList));
                item.setEntrustDetailDtoList(entrustDetailDtoList);
            }
        });
    }

    /**
     * 获取指令的最小交易单元
     * @param entrustDto
     * @return
     */
    private MinTrdUnitLimitDto getMinTrdUnitLimit(ExchangeTransMatchCommonEntrustDto entrustDto){
        String tradePlat = DictCodeListExt3.交易平台_新债平台.getValue().equals(entrustDto.getTradePlat())
                ? entrustDto.getDealMode() : entrustDto.getTradePlat();
        return trdCommonBaseService.getMinTrdUnitLimit(entrustDto.getFinprodId(), tradePlat,
                entrustDto.getTradeMarket(), entrustDto.getFinprodType2(),entrustDto.getPs());
    }

    /**
     * 查询各组合历史已分配指令数量信息
     * @param entrustId
     * @return
     */
    private Map<String, BigDecimal> getPortfolioHisMatchEnInfo(String entrustId){
        Map<String, BigDecimal> result = new HashMap<>();
        List<TrdProductDeal> trdProductDeals = exchangeTransMatchCommonMapper.getTrdProductDealsByEntrustId(entrustId);
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
                                                           List<ExchangeTransMatchCommonEntrustDetailDto> entrustDetailDtoList){
        Map<String, BigDecimal> result = new HashMap<>();
        for(ExchangeTransMatchCommonEntrustDetailDto entrustDetailDto : entrustDetailDtoList){
            // 组合已分配数量
            BigDecimal matchQtyEn = portfolioHisMatchEnMap.get(entrustDetailDto.getPortfolioId());
            result.put(entrustDetailDto.getPortfolioId(), ArithmeticUtil.subtract(entrustDetailDto.getEntrustNum(), matchQtyEn));
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

            // ZGTFCP-931:手动匹配存在多次匹配需要更新匹配数据而不是重新插入 modify fuyongsheng 20220319 start
            if(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue().equals(settleTransDataTotalMatch.getMatchClass())){
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

            //质押式协议回购需要处理 汇总成交质押品匹配结果表
            if((StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                    || StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()))
                    && StringUtil.equals(dto.getSettleTransDataTotalAdd().getRepoType(),DictCodeList.回购类型_质押式协议回购.getValue())){
                List<SettleDataTotalPledgeMatch> settleDataTotalPledgeMatchList = this.genSettleDataTotalPledgeMatch(dto,commonEntrustDto);
                for (SettleDataTotalPledgeMatch settleDataTotalPledgeMatch:settleDataTotalPledgeMatchList) {
                    // ZGTFCP-931:手动匹配存在多次匹配需要更新匹配数据而不是重新插入 modify fuyongsheng 20220319 start
                    if(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue().equals(settleTransDataTotalMatch.getMatchClass())){
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

            //登录:成交数据与投管交易关系（SETTLE_TRANS_TG_MAPPING）
            List<SettleTransTgMapping> settleTransTgMappingList = this.genSettleTransTgMapping(dto,tradeId);
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

        //更新:汇总成交数据状态表（SETTLE_TRANS_DATA_TOTAL_STATUS）
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
        logger.debug("处理匹配成交相关的表---结束");
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
            else if(StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_配债.getValue())) {
                List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtList = exchangeTransMatchBond5Service.genFinProductTradeDto(dto,commonEntrustDto);
                exchangeTransMatchBaseTradeDtos.addAll(baseTradeProductDealDtList);
            }
            else if (StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_质押券入库.getValue())
            || StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_质押券出库.getValue())) {
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
                if(StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_中途换券.getValue())
                || StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_解除质押.getValue())) {
                    baseTradeProductDealDtList = exchangeTransMatchRepo3Service.genFinProductTradeDto(dto,commonEntrustDto);
                }
                else if (StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_到期续做.getValue())){
                    baseTradeProductDealDtList = exchangeTransMatchRepo4Service.genFinProductTradeDto(dto,commonEntrustDto);
                }
                else if (StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_提前购回.getValue())
                || StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_到期购回.getValue())){
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
        List<FinDealPledge> firstFinDealPledgeTempList = new ArrayList<>();
        List<FinPledgeDisplaceResult> finPledgeDisplaceResultTempList = new ArrayList<>();
        // modify by etliu 20220110 end

        //中途换券和解除质押时需要处理 质押券置换和质押券置换结果
        if(StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_中途换券.getValue())
                || StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_解除质押.getValue())) {
            TrdFinProductDealTotal trdFinProductDealTotal = exchangeTransMatchBaseTradeDtos.get(0).getTrdFinProductDealTotal();
            FinPledgeDisplace finPledgeDisplace = exchangeTransMatchRepo3Service.genFinPledgeDisplace(trdFinProductDealTotal);
            finPledgeDisplaceMapper.insertSelective(finPledgeDisplace);

            for(BaseTradeProductDealDto baseTradeProductDealDto: exchangeTransMatchBaseTradeDtos) {
                if(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDeal())){
                    FinPledgeDisplaceDetail finPledgeDisplaceDetail = exchangeTransMatchRepo3Service.genFinPledgeDisplaceDetail(finPledgeDisplace,baseTradeProductDealDto.getTrdProductDeal());

                    // modify by etliu 20220110 start
                    // GZYHTZJY-1032: 协议回购的头寸处理，需要走PTL_POSITION_FLOW_CFG配置
                    if (StringUtil.equals(DictCodeListExt3.质押券置换_置换方向_换出.getValue(), finPledgeDisplaceDetail.getTradeType())) {
                        finPledgeDisplaceDetailHuanChu = finPledgeDisplaceDetail;
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
                if(StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_解除质押.getValue())){
                    firstFinDealPledgeTempList.addAll(firstFinDealPledgeList);
                }
                finPledgeDisplaceResultTempList.addAll(finPledgeDisplaceResultList);
            }
        }
        baseTradeProductDealDtos.addAll(exchangeTransMatchBaseTradeDtos);
        logger.debug("交易表数据:"+JsonUtils.object2Json(finProductTradeDto));
        // 处理新老头寸和指令完成度
        this.executePositionAndEntrustFinish(dto, commonEntrustDto, finProductTradeDto, exchangeTransMatchBaseTradeDtos,
                finPledgeDisplaceDetailHuanChu, firstFinDealPledgeTempList, finPledgeDisplaceResultTempList);
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
            if (StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_质押券入库.getValue())
                    || StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_质押券出库.getValue())) {
                TrdStasecFlow trdStasecFlow = exchangeTransMatchBond6Service.generateTrdStasecFlow(dto,commonEntrustDto,baseTradeProductDealDto.getTrdProductDeal());
                trdStasecFlowMapper.insertSelective(trdStasecFlow);

                TrdStasecFlowDetail trdStasecFlowDetail = exchangeTransMatchBond6Service.generateTrdStasecFlowDetail(dto,commonEntrustDto,baseTradeProductDealDto.getTrdProductDeal());
                trdStasecFlowDetailMapper.insertSelective(trdStasecFlowDetail);
            }

            //竞价回购
            if(dto.getSettleTransDataTotalAdd() != null && !StringUtil.equals(dto.getSettleTransDataTotalAdd().getRepoType(),DictCodeList.回购类型_质押式协议回购.getValue())) {
                if(StringUtil.equals(commonEntrustDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                        || StringUtil.equals(commonEntrustDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                    SettleTransData settleTransData = new SettleTransData();
                    BeanUtil.copyPropertiesIgnoreNull(settleTransData,dto);
                    FinTradeProduct finTradeProduct = exchangeTransMatchRepo1Service.genFinTradeProduct(settleTransData, baseTradeProductDealDto.getTrdProductDeal());
                    finTradeProductMapper.insertSelective(finTradeProduct);

                    FinProductType finProductType = exchangeTransMatchRepo1Service.genFinProductType(dto,baseTradeProductDealDto.getTrdProductDeal());

                    finProductTypeMapper.insertSelective(finProductType);
                }
            }else{
                if(StringUtil.isNotEmpty(baseTradeProductDealDto.getFinTradeProduct())) {
                    finTradeProductMapper.insertSelective(baseTradeProductDealDto.getFinTradeProduct());
                }

                if(StringUtil.isNotEmpty(baseTradeProductDealDto.getFinProductType())) {
                    finProductTypeMapper.insertSelective(baseTradeProductDealDto.getFinProductType());
                }
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
     * @param firstFinDealPledgeTempList
     * @param finPledgeDisplaceResultTempList
     */
    private void executePositionAndEntrustFinish(ExchangeTransMatchCommonDto dto,ExchangeTransMatchCommonEntrustDto commonEntrustDto,
                 FinProductTradeDto finProductTradeDto, List<ExchangeTransMatchBaseTradeDto> exchangeTransMatchBaseTradeDtos,
                 FinPledgeDisplaceDetail finPledgeDisplaceDetailHuanChu,List<FinDealPledge> firstFinDealPledgeTempList,
                 List<FinPledgeDisplaceResult> finPledgeDisplaceResultTempList){
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
                    finPledgeDisplaceDetailHuanChu, firstFinDealPledgeTempList, finPledgeDisplaceResultTempList);
        }
        else if(StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_质押券入库.getValue())
                || StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_质押券出库.getValue())){
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
        if(StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_中途换券.getValue())) {
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
                    // 金融产品代码
                    firstFinDealPledge.setFinprodId(dealTotal.getFinprodId());
                    // 换入质押券
                    SettleTransDataTotalPledge inPledge = table.get(firstFinDealPledge.getPledgeFinprodId(), DictCodeListExt3.质押券置换_置换方向_换入.getValue());
                    // 换出质押券
                    SettleTransDataTotalPledge outPledge = table.get(firstFinDealPledge.getPledgeFinprodId(), DictCodeListExt3.质押券置换_置换方向_换出.getValue());
                    if(null != inPledge){
                        // 如果换入的质押券跟首期一致,则券面总额、质押券数量相加
                        firstFinDealPledge.setPledgeAmt(ArithmeticUtil.doAdd(firstFinDealPledge.getPledgeAmt(), inPledge.getPledgeAmt()));
                        firstFinDealPledge.setPledgeNumber(ArithmeticUtil.doAdd(firstFinDealPledge.getPledgeNumber(), inPledge.getPledgeNumber()));
                    }else if(null != outPledge){
                        // 如果出的质押券跟首期一致,则券面总额、质押券数量相减
                        firstFinDealPledge.setPledgeAmt(ArithmeticUtil.subtract(firstFinDealPledge.getPledgeAmt(), outPledge.getPledgeAmt()));
                        firstFinDealPledge.setPledgeNumber(ArithmeticUtil.subtract(firstFinDealPledge.getPledgeNumber(), inPledge.getPledgeNumber()));
                    }
                    // 如果质押券面额为0,则忽略
                    if(BigDecimal.ZERO.compareTo(firstFinDealPledge.getPledgeAmt()) >= 0){
                        continue;
                    }
                    finDealPledgeList.add(firstFinDealPledge);
                }
                // 首期质押券金融代码
                List<String> firstPladgeFinprodIds = firstFinDealPledgeList.stream()
                        .map(FinDealPledge::getPledgeFinprodId)
                        .collect(Collectors.toList());
                // 筛选出此次换入并且不在首期里的质押券
                List<SettleTransDataTotalPledge> inpledges = dto.getSettleTransDataTotalPledgeList().stream()
                        .filter(item -> DictCodeListExt3.质押券置换_置换方向_换入.getValue().equals(item.getDisplaceDirection())
                                && !firstPladgeFinprodIds.contains(item.getPledgeFinprodId()))
                        .collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(inpledges)){
                    for(SettleTransDataTotalPledge inpledge : inpledges){
                        FinDealPledge finDealPledge = new FinDealPledge();
                        finDealPledge.setFinprodId(dealTotal.getFinprodId());
                        finDealPledge.setPledgeFinprodId(inpledge.getPledgeFinprodId());
                        finDealPledge.setPledgeNumber(inpledge.getPledgeNumber());
                        finDealPledge.setPledgeAmt(inpledge.getPledgeAmt());
                        finDealPledge.setPledgeFaceValue(inpledge.getPledgeFaceValue());
                        finDealPledgeList.add(finDealPledge);
                    }
                }
                BigDecimal totalAmt = BigDecimal.ZERO;
                for(FinDealPledge finDealPledge : finDealPledgeList){
                    totalAmt = ArithmeticUtil.doAdd(ArithmeticUtil.multiply(finDealPledge.getPledgeNumber(), finDealPledge.getPledgeFaceValue()));
                }
                // 查询首期交易信息
                TrdProductDeal firstDeal = trdProductDealMapper.selectByPrimaryKey(dealTotal.getFinprodId());
                // 质押率=首期.交易本金/SUM(质押面值*质押数量)
                BigDecimal pledgeRatio = ArithmeticUtil.divide(firstDeal.getPrinAmt(), totalAmt, 2, RoundingMode.HALF_UP);
                // 赋值质押率
                for(FinDealPledge finDealPledge : finDealPledgeList){
                    finDealPledge.setPledgeRatio(pledgeRatio);
                }
            }
        }
        // 解除质押需要删除首期的质押品信息
        else if(StringUtil.equals(commonEntrustDto.getPs(), DictCodeListExt3.交易方向_解除质押.getValue())){
            for(FinDealPledge finDealPledge:firstFinDealPledgeList) {
                finDealPledgeMapper.deleteByPrimaryKey(finDealPledge);
            }
        }
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
        else if(StringUtil.equals(dealDto.getPs(),DictCodeListExt3.交易方向_中途换券.getValue())
        || StringUtil.equals(dealDto.getPs(),DictCodeListExt3.交易方向_解除质押.getValue())) {
            for(SettleTransDataTotalPledge settleTransDataTotalPledge:dealDto.getSettleTransDataTotalPledgeList()) {
                SettleDataTotalPledgeMatch settleDataTotalPledgeMatch = new SettleDataTotalPledgeMatch();
                if(StringUtil.equals(dealDto.getPs(),DictCodeListExt3.交易方向_中途换券.getValue())) {
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
                else if(StringUtil.equals(dealDto.getPs(),DictCodeListExt3.交易方向_解除质押.getValue())){
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
                            && (StringUtil.equals(item.getPs(), DictCodeListExt3.交易方向_中途换券.getValue()) || StringUtil.equals(item.getPs(), DictCodeListExt3.交易方向_解除质押.getValue()))).collect(Collectors.toList());
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
                        // FinPledgeDisplaceResult finPledgeDisplaceResult = finPledgeDisplaceResultList.stream().findFirst().filter(result->StringUtil.equals(result.getResultType(),DictCodeListExt3.质押券置换_置换结果_置换前.getValue())).get();
                        FinPledgeDisplaceResult finPledgeDisplaceResult = finPledgeDisplaceResultList.stream().filter(result->StringUtil.equals(result.getResultType(),DictCodeListExt3.质押券置换_置换结果_置换前.getValue())).findFirst().get();
                        // ZGTFCP-1805 : 先filter再findFirst modify by fuyongsheng 20220324 end
                        if(StringUtil.equals(itemList.get(0).getPs(), DictCodeListExt3.交易方向_中途换券.getValue())){
//                            List<FinDealPledge> firstFinDealPledgeList = exchangeTransMatchRepo3Service.getFirstRepoFinDealPledge(settleTransDataTotal.getfCjbh(),settleTransDataTotal.getVdate(),itemList.get(0).getPortfolioId());
                            List<FinDealPledge> firstFinDealPledgeList = exchangeTransMatchRepo3Service.getFirstRepoFinDealPledge(
                                    settleTransDataTotal.getfCjbh(),settleTransDataTotal.getVdate(),
                                    settleTransDataTotal.getSeatNumber(),settleTransDataTotal.getSecAcctId());
                            if(!StringUtil.isEmptyList(firstFinDealPledgeList)) {
                                for(FinDealPledge finDealPledge:firstFinDealPledgeList) {
                                    finDealPledge.setPledgeFinprodId(finPledgeDisplaceResult.getFinprodId());
                                    finDealPledge.setPledgeRatio(finPledgeDisplaceResult.getScale());
                                    finDealPledge.setPledgeAmt(finPledgeDisplaceResult.getPledgeAmt());
                                    finDealPledgeMapper.updateByPrimaryKeySelective(finDealPledge);
                                }
                            }
                        }
                        else {
                            //查询首期交易
                            // GZYHTZJY-1160:查询首期交易方向是T01 modify fuyongsheng 20220119 start
//                            TrdProductDeal trdProductDeal = exchangeTransMatchRepo3Service.getFirstRepoTrade(
//                                    settleTransDataTotal.getfCjbh(),settleTransDataTotal.getVdate(),
//                                    //settleTransDataTotal.getPs(),
//                                    DictCodeList.交易方向_买入.getValue(),
//                                    itemList.get(0).getPortfolioId());
                            TrdProductDeal trdProductDeal = trdCommonBaseService.getFirstRepoTrade(settleTransDataTotal.getfCjbh(),
                                    settleTransDataTotal.getSeatNumber(), settleTransDataTotal.getSecAcctId(),
                                    settleTransDataTotal.getVdate());
                            // GZYHTZJY-1160:查询首期交易方向是T01 modify fuyongsheng 20220119 end
                            if(StringUtil.isNotEmpty(trdProductDeal)) {
                                FinDealPledge finDealPledge = new FinDealPledge();
                                finDealPledge.setFinprodId(trdProductDeal.getTradeId());
                                finDealPledge.setBranch(0L);
                                finDealPledge.setPledgeFinprodId(finPledgeDisplaceResult.getFinprodId());
                                finDealPledge.setPledgeRatio(finPledgeDisplaceResult.getScale());
                                finDealPledge.setPledgeNumber(ArithmeticUtil.divide( finPledgeDisplaceResult.getPledgeAmt(),new BigDecimal(100),2));
                                finDealPledgeMapper.insertSelective(finDealPledge);
                            }

                        }

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
            if(StringUtil.isNotEmpty(settleTransDataTotalStatus)) {
                //取得剩余匹配份额
                BigDecimal remainNum = exchangeTransMatchCommonMapper.getRemainNum(dto.getSeqNo());
                settleTransDataTotalStatus.setMatchStatus(BigDecimal.ZERO.compareTo(remainNum) >=0
                        ?DictCodeListExt3.下行交易匹配状态_未匹配.getValue():DictCodeListExt3.下行交易匹配状态_部分匹配.getValue());
                settleTransDataTotalStatus.setUpdateTime(new Date());
                settleTransDataTotalStatus.setUpdateUser(StringUtil.isNotEmpty(LoginUserUtil.getUserName())?LoginUserUtil.getUserName():"system");
                settleTransDataTotalStatusMapper.updateByPrimaryKey(settleTransDataTotalStatus);
            }  else {
                settleTransDataTotalStatus = new SettleTransDataTotalStatus();
                settleTransDataTotalStatus.setSeqNo(dto.getSeqNo());
                //取得剩余匹配份额
                BigDecimal remainNum = exchangeTransMatchCommonMapper.getRemainNum(dto.getSeqNo());
                settleTransDataTotalStatus.setMatchStatus(BigDecimal.ZERO.compareTo(remainNum) >=0
                        ?DictCodeListExt3.下行交易匹配状态_未匹配.getValue():DictCodeListExt3.下行交易匹配状态_部分匹配.getValue());
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
    private String calcShareAndAmount(ExchangeTransMatchCommonDto dto,List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoList,String matchStatus){
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
            else if(StringUtil.equals(commonEntrustDto.getTargetType(),DictCodeListExt3.目标类型_数量.getValue())) {
                //已匹配份额
                BigDecimal marchQty = BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(), commonEntrustDto.getRemainEntrustNum()))>0?dto.getRemainNum() : commonEntrustDto.getRemainEntrustNum();
                commonEntrustDto.setMarchQty(marchQty);
                //已匹配金额
                if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    //质押券出入库成交信息中只有质押券的数量，目标类型只有数量，需要根据折算率计算标准券数量
                    if(StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券入库.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券出库.getValue())) {
                        //已匹配金额
                        commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(marchQty,commonEntrustDto.getPrice()));
                    }
                    // modify by etliu 20220227 start
                    // 广农商二阶段 GNS-920:【广农商场内清算】广农商场内债转股清算交易生成后，对应的持仓流水更新有误
                    if (StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_债转股.getValue())) {
                        //已匹配金额（债转股只考虑成交数据和指令一对一的场景）
                        commonEntrustDto.setMarchAmt(dto.getFpriceAmt());
                    }
                    // modify by etliu 20220227 end
                    else {
                        //已匹配金额
                        commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(marchQty,dto.getUnitFprice()));
                    }

                }
                else {
                    commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(marchQty,dto.getUnitCprice()));
                }

                //指令已匹配份额
                BigDecimal marchQtyEn = BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),commonEntrustDto.getRemainEntrustNum()))>0?dto.getRemainNum() : commonEntrustDto.getRemainEntrustNum();
                commonEntrustDto.setMarchQtyEn(marchQtyEn);
                //指令已匹配金额
                commonEntrustDto.setMarchAmtEn(ArithmeticUtil.multiply(marchQtyEn,commonEntrustDto.getPrice()));
            }
            else {
                //已匹配份额
                BigDecimal entrustQty = ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),dto.getUnitCprice(),4);
                commonEntrustDto.setMarchQty(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),entrustQty))>0 ? dto.getRemainNum() : entrustQty);
                //已匹配金额
                BigDecimal entrustAmt = BigDecimal.ZERO;
                if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    entrustAmt = ArithmeticUtil.multiply(entrustQty,dto.getUnitFprice());
                }
                else {
                    entrustAmt = ArithmeticUtil.multiply(entrustQty,dto.getUnitCprice());
                }
                commonEntrustDto.setMarchAmt(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainAmount(),entrustAmt))>0?dto.getRemainAmount() : entrustAmt);

                //指令已匹配份额
                BigDecimal marchAmtEn = ArithmeticUtil.multiply(dto.getRemainNum(),dto.getUnitCprice());
                commonEntrustDto.setMarchQtyEn(ArithmeticUtil.divide(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(marchAmtEn,commonEntrustDto.getRemainEntrustAmt()))>0?marchAmtEn : commonEntrustDto.getRemainEntrustAmt(),commonEntrustDto.getPrice(),4));
                //指令已匹配金额
                commonEntrustDto.setMarchAmtEn(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(marchAmtEn,commonEntrustDto.getRemainEntrustAmt()))>0?marchAmtEn : commonEntrustDto.getRemainEntrustAmt());
            }

            if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                    || StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                if(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),commonEntrustDto.getRemainEntrustAmt())) >=0) {
                    matchStatus = DictCodeListExt3.下行交易匹配状态_匹配完成.getValue();
                }
                else {
                    matchStatus = DictCodeListExt3.下行交易匹配状态_部分匹配.getValue();
                }
            }
            else if((StringUtil.equals(commonEntrustDto.getTargetType(),DictCodeListExt3.目标类型_数量.getValue())
                    && BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),commonEntrustDto.getRemainEntrustNum())) >=0)
                    || (StringUtil.equals(commonEntrustDto.getTargetType(),DictCodeListExt3.目标类型_金额.getValue())
                    && BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),dto.getUnitCprice(),4))) >=0)) {
                matchStatus = DictCodeListExt3.下行交易匹配状态_匹配完成.getValue();
            }
            else if((StringUtil.equals(commonEntrustDto.getTargetType(),DictCodeListExt3.目标类型_数量.getValue())
                    && BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),commonEntrustDto.getRemainEntrustNum())) < 0)
                    || (StringUtil.equals(commonEntrustDto.getTargetType(),DictCodeListExt3.目标类型_金额.getValue())
                    && BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),dto.getUnitCprice(),4))) < 0)){
                matchStatus = DictCodeListExt3.下行交易匹配状态_部分匹配.getValue();
            }
            // modify by etliu 20220228 start
            // 广农商二阶段 GNS-834:【广农商场内清算】上交所基金买入卖出，手动匹配生成的交易，随交易支付费用值没登
            commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalFeeAmtPay()));
            commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalUnFeeAmtPay()));
            // modify by etliu 20220228 end
        }
        //多条指令匹配时
        else {
            //处理可分配份额和可分配金额
            BigDecimal totalMarchQty = BigDecimal.ZERO;
            matchStatus = DictCodeListExt3.下行交易匹配状态_未匹配.getValue();
            for(ExchangeTransMatchCommonEntrustDto commonEntrustDto : commonEntrustDtoList) {
                if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                        || StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                    commonEntrustDto.setMarchQty(commonEntrustDto.getRemainEntrustAmt());
                    commonEntrustDto.setMarchAmt(commonEntrustDto.getRemainEntrustAmt());
                }
                else if(StringUtil.equals(commonEntrustDto.getTargetType(),DictCodeListExt3.目标类型_数量.getValue())) {
                    commonEntrustDto.setMarchQty(commonEntrustDto.getRemainEntrustNum());
                    if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                        if(StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券入库.getValue())
                                || StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券出库.getValue())) {
                            commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(commonEntrustDto.getRemainEntrustNum(),commonEntrustDto.getPrice()));
                        }
                        else {
                            commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(commonEntrustDto.getRemainEntrustNum(),dto.getUnitFprice()));
                        }

                    }
                    else {
                        commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(commonEntrustDto.getRemainEntrustNum(),dto.getUnitCprice()));
                    }

                }
                else {
                    commonEntrustDto.setMarchQty(ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),dto.getUnitCprice(),4));
                    if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                        commonEntrustDto.setMarchAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),dto.getUnitCprice(),4),dto.getUnitFprice()));
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
                    else if(StringUtil.equals(commonEntrustDto.getTargetType(),DictCodeListExt3.目标类型_数量.getValue())) {
                        commonEntrustDto.setMarchQtyEn(commonEntrustDto.getRemainEntrustNum());
                        commonEntrustDto.setMarchAmtEn(ArithmeticUtil.multiply(commonEntrustDto.getRemainEntrustNum(),commonEntrustDto.getPrice()));
                    }
                    else {
                        commonEntrustDto.setMarchQtyEn(ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),commonEntrustDto.getPrice(),4));
                        commonEntrustDto.setMarchAmtEn(commonEntrustDto.getRemainEntrustAmt());
                    }

                    // modify by etliu 20220228 start
                    // 广农商二阶段 GNS-834:【广农商场内清算】上交所基金买入卖出，手动匹配生成的交易，随交易支付费用值没登
                    commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalFeeAmtPay()));
                    commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalUnFeeAmtPay()));
                    // modify by etliu 20220228 end
                }
//                matchStatus = DictCodeListExt3.下行交易匹配状态_匹配完成.getValue();
                matchStatus = DictCodeListExt3.下行交易匹配状态_部分匹配.getValue();
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
                            else if(StringUtil.equals(commonEntrustDto.getTargetType(),DictCodeListExt3.目标类型_数量.getValue())) {
                                commonEntrustDto.setMarchQtyEn(commonEntrustDto.getRemainEntrustNum());
                                commonEntrustDto.setMarchAmtEn(ArithmeticUtil.multiply(commonEntrustDto.getRemainEntrustNum(),commonEntrustDto.getPrice()));
                            }
                            else {
                                commonEntrustDto.setMarchQtyEn(ArithmeticUtil.divide(commonEntrustDto.getRemainEntrustAmt(),commonEntrustDto.getPrice(),4));
                                commonEntrustDto.setMarchAmtEn(commonEntrustDto.getRemainEntrustAmt());
                            }

                            commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalFeeAmtPay()));
                            commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalUnFeeAmtPay()));

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
                            else if(StringUtil.equals(commonEntrustDto.getTargetType(),DictCodeListExt3.目标类型_数量.getValue())) {
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
                    for(int i = 0 ;i< commonEntrustDtoList.size(); i++) {
                        ExchangeTransMatchCommonEntrustDto commonEntrustDto = commonEntrustDtoList.get(i);
                        BigDecimal marchQty = BigDecimal.ZERO;
                        BigDecimal marchAmt = BigDecimal.ZERO;

                        if(i<commonEntrustDtoList.size()-1) {
                            marchQty = ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),totalMarchQty,4),dto.getRemainNum());
                            marchAmt = ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),totalMarchQty,4),dto.getRemainAmount());

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
                        if(StringUtil.equals(commonEntrustDto.getTargetType(),DictCodeListExt3.目标类型_数量.getValue())) {
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

                commonEntrustDtoList.clear();
                commonEntrustDtoList.addAll(commonEntrustDtoListTmp);
                if(dto.getRemainNum().compareTo(totalFinishMarchQty) >0 || dto.getRemainAmount().compareTo(totalFinishMarchAmt) >0){
                    matchStatus = DictCodeListExt3.下行交易匹配状态_部分匹配.getValue();
                }else{
                    matchStatus = DictCodeListExt3.下行交易匹配状态_匹配完成.getValue();
                }
            }
        }

        return matchStatus;
    }

    /**
     * 计算处理已分配份额，已分配金额，指令已分配金额，指令已分配份额(质押式协议回购)
     * @param dto
     * @param commonEntrustDtoList
     * @param matchStatus
     */
    private String calcAgreRepShareAndAmount(ExchangeTransMatchCommonDto dto,List<ExchangeTransMatchCommonEntrustDto> commonEntrustDtoList,String matchStatus){

        //只有一条匹配指令时
        if(dto.getEntrustIdList().size() == 1) {
            ExchangeTransMatchCommonEntrustDto commonEntrustDto = commonEntrustDtoList.get(0);

            //已匹配份额
            BigDecimal marchQty = BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(), commonEntrustDto.getRemainEntrustNum()))>0?dto.getRemainNum() : commonEntrustDto.getRemainEntrustNum();
            commonEntrustDto.setMarchQty(marchQty);
            //已匹配金额=已分配份额
            commonEntrustDto.setMarchAmt(marchQty);
            //指令已匹配份额
            BigDecimal marchQtyEn = BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),commonEntrustDto.getRemainEntrustNum()))>0?dto.getRemainNum() : commonEntrustDto.getRemainEntrustNum();
            commonEntrustDto.setMarchQtyEn(marchQtyEn);
            //指令已匹配金额 = 指令已匹配份额
            commonEntrustDto.setMarchAmtEn(marchQtyEn);

            commonEntrustDto.setMarchFeeAmtPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalFeeAmtPay()));
            commonEntrustDto.setMarchFeeAmtUnPay(ArithmeticUtil.multiply(ArithmeticUtil.divide(commonEntrustDto.getMarchQty(),dto.getRemainNum(),4),dto.getTotalUnFeeAmtPay()));

            if(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),commonEntrustDto.getRemainEntrustNum())) >=0) {
                matchStatus = DictCodeListExt3.下行交易匹配状态_匹配完成.getValue();
            }
            else if(BigDecimal.ZERO.compareTo(ArithmeticUtil.subtract(dto.getRemainNum(),commonEntrustDto.getRemainEntrustNum())) < 0
            ){
                matchStatus = DictCodeListExt3.下行交易匹配状态_部分匹配.getValue();
            }
        }
        //多条指令匹配时
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
                matchStatus = DictCodeListExt3.下行交易匹配状态_匹配完成.getValue();
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
                        if(StringUtil.equals(commonEntrustDto.getTargetType(),DictCodeListExt3.目标类型_数量.getValue())) {
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

                commonEntrustDtoList.clear();
                commonEntrustDtoList.addAll(commonEntrustDtoListTmp);
                matchStatus = DictCodeListExt3.下行交易匹配状态_部分匹配.getValue();
            }
        }

        return matchStatus;
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
}
