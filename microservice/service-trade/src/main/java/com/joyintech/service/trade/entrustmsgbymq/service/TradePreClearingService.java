package com.joyintech.service.trade.entrustmsgbymq.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.*;
import com.joyintech.service.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.service.trade.bond.service.TrdBondCalcService;
import com.joyintech.service.trade.entrustmsgbymq.dao.EntrustMsgbyMQMapper;
import com.joyintech.service.trade.entrustmsgbymq.dto.TrdTempDto;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.trade.entrust.EtrFinishStatusDto;
import com.joyintech.tams.invest.common.position.dto.EtrEntrustAddDto;
import com.joyintech.tams.invest.common.position.dto.FinTradeProductDto;
import com.joyintech.tams.invest.common.position.dto.PositionBaseData;
import com.joyintech.tams.invest.common.position.service.CommonPositionService;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 *
 * 交易预清算服务类
 * 〈功能详细描述〉
 * @author tanxuan
 * @version 1.0 2020年5月11日
 * @see TradePreClearingService
 * @since 1.0
 */
@Service
public class TradePreClearingService extends BaseService<InfContractDetailfile>{
    
    private static final Logger logger = LoggerFactory.getLogger(TradePreClearingService.class);

    /**
     * 委托成交消息主推接口Mapper
     */
    @Autowired
    private EntrustMsgbyMQMapper entrustMsgbyMQMapper;

    /**
     * 基金成交预清算消息主推接口Mapper
     */
    @Autowired
    private FundTradePreClearingService fundTradePreClearingService;

    /**
     * AutoPeriodCommonService
     */
    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;


    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 金融产品临时交易
     */
    @Autowired
    private TrdProductTempDealMapper trdProductTempDealMapper;

    /**
     * 金融产品临时汇总交易表
     */
    @Autowired
    private TrdProductTempDealTotalMapper trdProductTempDealTotalMapper;

    /**
     * 金融产品临时交易附属表
     */
    @Autowired
    private TrdProductTempDealAddMapper trdProductTempDealAddMapper;

    /**
     * 临时划款指令表
     */
    @Autowired
    private StlPayTempOrdersMapper stlPayTempOrdersMapper;

    /**
     * 交易类金融产品表
     */
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    /**
     * 金融产品分类表
     */
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    /**
     * 回购质押品表
     */
    @Autowired
    private FinDealPledgeMapper finDealPledgeMapper;

    /**
     * 委托回购质押品表
     */
    @Autowired
    private EtrPledgeMapper etrPledgeMapper;

    @Autowired
    private TrdBondCalcService trdBondCalcService;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private TrdManualSettleInfoMapper trdManualSettleInfoMapper;

    /**
     * DateCalService
     */
    @Autowired
    private DateCalService dateCalService;

    @Autowired
    private TradePreCommonService tradePreCommonService;

    @Autowired
    private TrdTempStasecFlowDetailMapper trdTempStasecFlowDetailMapper;

    @Autowired
    private TrdTempStasecFlowMapper trdTempStasecFlowMapper;

    /**
     * 组合可用持仓和实际持仓查询更新
     */
    @Autowired(required = false)
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;
    
    /**
     * 组合可用持仓和实际持仓查询更新
     */
    @Autowired(required = false)
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;

    @Autowired
    private FinPledgeDispTempMapper finPledgeDispTempMapper;

    @Autowired
    private FinPledgeDispDetailTempMapper finPledgeDispDetailTempMapper;

	@Autowired
    private PositionRequestTradeReportService positionRequestTradeReportService;

    // add by etliu 20220225 start
    // 质押券入出库没有生成入出库流水问题修复
    /**
     * 金融产品表
     */
    @Autowired
    private FinProductMapper finProductMapper;
    // add by etliu 20220225 end

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;
    /**
     * 成交数据预清算
     *
     * @param infContractDetailfile
     * @return
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public void tradePreClearing(InfContractDetailfile infContractDetailfile)
            throws Exception {
        logger.info("【成交数据预清算开始】tradePreClearing entrustId: {}, seqno: {}", infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
        // 判断是否是多组合系列户指令
        if(tradePreCommonService.isMultiPortFolioEntrust(infContractDetailfile.getEntrustId())){
            String totalTradeId = null;
            List<InfContractPortfolioDetail> details = trdCommonBaseService.getInfContractPortfolioDetails(infContractDetailfile.getSeqno());
            if(!CollectionUtils.isEmpty(details)){
                for(InfContractPortfolioDetail detail : details){
                    TrdTempDto trdTempDto = new TrdTempDto();
                    trdTempDto.setTotleTradeId(totalTradeId);
                    // 替换成各组合明细数据
                    infContractDetailfile.setPortfolioId(detail.getPortfolioId());
                    infContractDetailfile.setShareAmt(detail.getShareAmt());
                    infContractDetailfile.setTradeAmt(detail.getTradeAmt());
                    handleTradeInfo(trdTempDto, infContractDetailfile);
                    totalTradeId = trdTempDto.getTotleTradeId();
                }
            }
        }
        else{
            TrdTempDto trdTempDto = new TrdTempDto();
            handleTradeInfo(trdTempDto, infContractDetailfile);
        }
        logger.info("【成交数据预清算结束】tradePreClearing entrustId: {}, seqno: {}", infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
    }

    /**
     * 处理下单成交明细数据
     * @param trdTempDto
     * @param infContractDetailfile
     * @throws Exception
     */
    private void handleTradeInfo(TrdTempDto trdTempDto,InfContractDetailfile infContractDetailfile) throws Exception{
        if(DictCodeListExt3.交易方向_质押券入库.getValue().equals(infContractDetailfile.getPs())
                || DictCodeListExt3.交易方向_质押券出库.getValue().equals(infContractDetailfile.getPs())) {
            //标准券入出库成交数据接入：
            logger.info("【成交数据预清算-标准券入出库开始】tradePreClearing entrustId: {}, seqno: {}",
                infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
            trdStaSec(trdTempDto, infContractDetailfile);
            logger.info("【成交数据预清算-标准券入出库结束】tradePreClearing entrustId: {}, seqno: {}",
                infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
        } else {
            String type8 = "";
            if (infContractDetailfile.getFinprodId() != null) {
                type8 = entrustMsgbyMQMapper.queryById(infContractDetailfile.getFinprodId(), 0L);
            }

            logger.info("【成交数据预清算】tradePreClearing entrustId: {}, seqno: {}, type8: {}",
                infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno(), type8);

            if (DictCodeList.平安资产类型_基金.getValue().equals(type8)) {
                logger.info("【成交数据预清算-基金开始】tradePreClearing entrustId: {}, seqno: {}",
                    infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
                fundTradePreClearingService.trdFund(trdTempDto, infContractDetailfile);
                logger.info("【成交数据预清算-基金结束】tradePreClearing entrustId: {}, seqno: {}",
                    infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
            } else {
                // TrdTempDto trdTempDto = null;
                if (DictCodeList.回购拆借方向_正回购.getValue().equals(infContractDetailfile.getPs())
                        || DictCodeList.回购拆借方向_逆回购.getValue().equals(infContractDetailfile.getPs())) {
                    logger.info("【成交数据预清算dto拼装-回购开始】tradePreClearing entrustId: {}, seqno: {}",
                        infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());

                    // 指令信息处理
                    this.buildPositionBaseData(trdTempDto, infContractDetailfile);
                    // 临时交易及其他数据处理
                    this.trdRepo(trdTempDto, infContractDetailfile,infContractDetailfile.getPs());
                    logger.info("【成交数据预清算dto拼装-回购结束】tradePreClearing entrustId: {}, seqno: {}",
                        infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
                }
                else if (DictCodeListExt3.交易方向_提前购回.getValue().equals(infContractDetailfile.getPs())
                        || DictCodeListExt3.交易方向_到期购回.getValue().equals(infContractDetailfile.getPs())
                        || DictCodeListExt3.交易方向_解除质押.getValue().equals(infContractDetailfile.getPs())
                        || DictCodeListExt3.交易方向_中途换券.getValue().equals(infContractDetailfile.getPs())) {
                    logger.info("【成交数据预清算dto拼装-协议回购开始】tradePreClearing entrustId: {}, seqno: {}",
                        infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());

                    // 首期信息、指令信息处理
                    this.buildPositionBaseData(trdTempDto, infContractDetailfile);
                    // 临时交易及其他数据处理
                    this.trdRepoPeriod(trdTempDto, infContractDetailfile);
                    logger.info("【成交数据预清算dto拼装-协议回购结束】tradePreClearing entrustId: {}, seqno: {}",
                        infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
                }
                else if (DictCodeListExt3.交易方向_到期续做.getValue().equals(infContractDetailfile.getPs())){
                    logger.info("【成交数据预清算dto拼装-协议回购-到期续作开始】tradePreClearing entrustId: {}, seqno: {}",
                            infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());

                    // 首期信息、指令信息处理
                    this.buildPositionBaseData(trdTempDto, infContractDetailfile);
                    // 临时交易及其他数据处理
                    this.trdRepoPeriodT56(trdTempDto, infContractDetailfile, false);
                    logger.info("【成交数据预清算dto拼装-协议回购-到期续作结束】tradePreClearing entrustId: {}, seqno: {}",
                            infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
                }
                else if (DictCodeListExt2.平安资产类型_股票.getValue().equals(type8)) {
                    logger.info("【成交数据预清算dto拼装-股票开始】tradePreClearing entrustId: {}, seqno: {}",
                        infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());

                    this.trdStock(trdTempDto, infContractDetailfile);

                    logger.info("【成交数据预清算dto拼装-股票结束】tradePreClearing entrustId: {}, seqno: {}",
                        infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
                } else if (DictCodeList.平安资产类型_债券.getValue().equals(type8)) {
                    logger.info("【成交数据预清算dto拼装-债券开始】tradePreClearing entrustId: {}, seqno: {}",
                        infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());

                    this.trdBond(trdTempDto, infContractDetailfile);

                    logger.info("【成交数据预清算dto拼装-债券结束】tradePreClearing entrustId: {}, seqno: {}",
                        infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
                }else if (DictCodeListExt3.平安资产类型_期货.getValue().equals(type8)) {
                    logger.info("【成交数据预清算处理-期货开始】tradePreClearing entrustId: {}, seqno: {}",
                        infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());

                    this.trdFuture(trdTempDto, infContractDetailfile);

                    logger.info("【成交数据预清算处理-期货结束】tradePreClearing entrustId: {}, seqno: {}",
                        infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
                }
                if (trdTempDto != null) {
                    logger.debug("【成交数据预清算数据】trdTempDto: {}, infContractDetailfile: {}", JSON.toJSONString(trdTempDto),
                            JSON.toJSONString(infContractDetailfile));
                    // 手工交收判断 add by yufei 20201022 start
                    if(StringUtil.isNotEmpty(infContractDetailfile.getSettleDay())
                            && StringUtil.isNotEmpty(infContractDetailfile.getBusiType())){
                        logger.info("【成交数据预清算手工交收处理-开始】tradePreClearing entrustId: {}, seqno: {}",
                            infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());

                        trdTempDto.setIsManualSettle("Y");
                        this.saveSettleInfo(infContractDetailfile,trdTempDto);

                        logger.info("【成交数据预清算手工交收处理-结束】tradePreClearing entrustId: {}, seqno: {}",
                            infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
                    }
                    // 手工交收判断 add by yufei 20201022 end
                    if(!DictCodeListExt3.平安资产类型_期货.getValue().equals(type8)){
                        logger.info("【成交数据预清算入库处理-开始】tradePreClearing entrustId: {}, seqno: {}",
                            infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());

                    	this.tradePreClear(trdTempDto, infContractDetailfile);

                    	logger.info("【成交数据预清算入库处理-结束】tradePreClearing entrustId: {}, seqno: {}",
                            infContractDetailfile.getEntrustId(), infContractDetailfile.getSeqno());
                    }
                }
            }
        }
    }

    public void buildPositionBaseData(TrdTempDto trdTempDto, InfContractDetailfile infContractDetailfile) {
        // 构造开始----------------------------------------------------------
        BeanUtil.copyPropertiesIgnoreNull(trdTempDto, infContractDetailfile);
        // ZGTFCP-1242:将orderId赋值为空 modify by fuyongsheng 20220317 start
        trdTempDto.setOrderId(null);
        // ZGTFCP-1242:将orderId赋值为空 modify by fuyongsheng 20220317 end
        trdTempDto.setAssetType(DictCodeList.回购类型_质押式协议回购.getValue());
        trdTempDto.setData(infContractDetailfile);
        PositionBaseData positionBaseData = new PositionBaseData();
        trdTempDto.setPositionBaseData(positionBaseData);
        // 首期成交编号
        String finprodId = infContractDetailfile.getvTradeId();
        // 指令编号
        String entrustId = infContractDetailfile.getEntrustId();
        // 指令信息
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        positionBaseData.setEtrEntrust(etrEntrust);
        // 交易对手
        if (StringUtil.isEmpty(trdTempDto.getCounterId()) && etrEntrust != null){
            trdTempDto.setCounterId(etrEntrust.getCounterId());
        }
        // 回购拆借方向
        if (etrEntrust != null){
            // 协议回购非首期指令的finProdId存的是首期交易编号
            finprodId = etrEntrust.getFinprodId();
            trdTempDto.setFinprodType2(etrEntrust.getFinprodType2());
        }else {
            trdTempDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        }
        // 指令附属信息
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustId);
        EtrEntrustAddDto etrEntrustAddDto = new EtrEntrustAddDto();
        if (etrEntrustAdd != null){
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAddDto,etrEntrustAdd);
//            if (etrEntrustAdd.getTradeId() != null) {
//                finprodId = etrEntrustAdd.getTradeId();
//            }
        }
        positionBaseData.setEtrEntrustAddDto(etrEntrustAddDto);
        // 首期主键为空
        if (StringUtil.isEmpty(finprodId)) {
            return;
        }

        // add by etliu 20220116 start
//        String hgsqFinprodId = trdCommonBaseService.selectHgsqFinprodId(trdTempDto.getPortfolioId(), finprodId);
//        if (StringUtil.isNotBlank(hgsqFinprodId)) {
//            finprodId = hgsqFinprodId;
//        }
        // add by etliu 20220116 end
        // 首期信息
        trdTempDto.setFinprodId(finprodId);
        // 查询首期交易类金融产品数据
        FinTradeProductKey finTradeProductKey = new FinTradeProductKey();
        finTradeProductKey.setFinprodId(finprodId);
        finTradeProductKey.setBranch(0L);
        FinTradeProduct firstFinTradeProduct = finTradeProductMapper.selectByPrimaryKey(finTradeProductKey);
        FinTradeProductDto finTradeProductDto = new FinTradeProductDto();
        if (firstFinTradeProduct != null){
            BeanUtil.copyPropertiesIgnoreNull(finTradeProductDto,firstFinTradeProduct);
        }
        positionBaseData.setFinTradeProductDto(finTradeProductDto);
        // 质押券信息
        FinDealPledgeKey finDealPledgeKey = new FinDealPledgeKey();
        finDealPledgeKey.setFinprodId(finprodId);
        finDealPledgeKey.setBranch(0L);
        FinDealPledge finDealPledge = entrustMsgbyMQMapper.selectFinDealPledgeByPrimaryKey(finDealPledgeKey);
        positionBaseData.setFinDealPledge(finDealPledge);
    }

    /**
     * 标准券入出库成交数据接入
     * @param infContractDetailfile
     */
    public void trdStaSec(TrdTempDto trdTempDto, InfContractDetailfile infContractDetailfile) {
        // add by etliu 20210825 start
        // 标准券入出库的成交数据有两笔，一笔是质押券的，一笔是标准券的，需要把标准券的那条过滤掉
        // 根据指令编号查投资指令，
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(infContractDetailfile.getEntrustId());
        // modify by etliu 20220225 start
        // 质押券入出库没有生成入出库流水问题修复
//        // 查询申报信息
//        Map<String, Object> map = new HashMap<>();
//        map.put("finprodId", etrEntrust.getFinprodId());
//        map.put("othCodeType", DictCodeListExt3.金融产品其他代码类型_质押代码.getValue());
//        FinProductOthCode finProductOthCode = entrustMsgbyMQMapper.selectFinProductOthCode(map);
//        String othCode = "";
//        if (finProductOthCode != null) {
//            othCode = finProductOthCode.getOthCode();
//        }
//        if (etrEntrust != null && !StringUtil.equals(othCode, StringUtil.substringBefore(infContractDetailfile.getFinprodMarketId(), "."))) {
//            return;
//        }
        if (etrEntrust != null) {
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(etrEntrust.getFinprodId());
            if (!StringUtil.equals(finProduct.getFinprodMarketId(), infContractDetailfile.getFinprodMarketId())) {
                return;
            }
        }
        // modify by etliu 20220225 end
        // add by etliu 20210825 end

        //标准券入出库DTO封装
        TrdTempStasecFlow trdTempStasecFlow =new TrdTempStasecFlow();
        //入出库编号--自动生成
        String seqno = IDGenerator.generate();
        trdTempStasecFlow.setSeqno(seqno);
        //委托代码
        trdTempStasecFlow.setEntrustId(infContractDetailfile.getEntrustId());
        //组合代码
        trdTempStasecFlow.setPortfolioId(infContractDetailfile.getPortfolioId());
        //日期
        trdTempStasecFlow.setCdate(infContractDetailfile.getTradeDate());
        //入出库方向
        trdTempStasecFlow.setPs(infContractDetailfile.getPs());
        //证券账户代码
        Map<String,Object> secMap = new HashMap<>();
        secMap.put("acctNo", infContractDetailfile.getAcctNo());
        secMap.put("portfolioId", infContractDetailfile.getPortfolioId());
        secMap.put("tradeMarket", infContractDetailfile.getTradeMarket());
        String secAcctId = entrustMsgbyMQMapper.queryByAcctNo(secMap);
        trdTempStasecFlow.setSecAcctId(secAcctId);
        //创建人更新人
        trdTempStasecFlow.setCreateUser("exchange");
        trdTempStasecFlow.setUpdateUser("exchange");

        //标准券入出库明细DTO封装
        TrdTempStasecFlowDetail trdTempStasecFlowDetail= new TrdTempStasecFlowDetail();
        //入出库编号
        trdTempStasecFlowDetail.setSeqno(seqno);
        //金融产品代码
        trdTempStasecFlowDetail.setFinprodId(infContractDetailfile.getFinprodId());
        //分支序号
        trdTempStasecFlowDetail.setBranch(0L);
        //入出库数量
        trdTempStasecFlowDetail.setAmt(infContractDetailfile.getShareAmt());
        //创建人更新人
        trdTempStasecFlowDetail.setCreateUser("exchange");
        trdTempStasecFlowDetail.setUpdateUser("exchange");

        //标准券入出库登DB
        trdTempStasecFlowMapper.insert(trdTempStasecFlow);
        trdTempStasecFlowDetailMapper.insert(trdTempStasecFlowDetail);

        //查询当日折算率
        BigDecimal converseRadio = this.entrustMsgbyMQMapper.queryConverseRadio(infContractDetailfile.getFinprodId(), infContractDetailfile.getTradeDate());
        if(converseRadio==null) {
            converseRadio = BigDecimal.ONE;
        }

        // modify by etliu 20220303 start
        // 质押式回购出入库计算时，原公式：标准券数量=质押数量*标准折算率，改成：标准券数量=质押数量*标准折算率*面值/100  ---需求张琼
        BigDecimal 百元券面 = trdCommonBaseService.getFaceValue(infContractDetailfile.getFinprodId(), infContractDetailfile.getTradeDate());
        if (百元券面 == null) {
            百元券面 = BigDecimal.valueOf(100);
        }
        BigDecimal hundred = BigDecimal.valueOf(100);
        // modify by etliu 20220303 end

        //更新持仓头寸
        if(DictCodeListExt3.交易方向_质押券入库.getValue().equals(trdTempStasecFlow.getPs())) {
            //1.指令持仓流水（标准券）：正流水
            EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
            List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();// 持仓流水
            EtrSecPositionFlowDto etrSecPositionFlowDtoUnit = new EtrSecPositionFlowDto();// 组合持仓流水DTO
            /********************** 持仓流水DTO构造 *************************************/
            etrSecPositionFlowDtoUnit.setSeqno(IDGenerator.generate());
            etrSecPositionFlowDtoUnit.setPortfolioId(infContractDetailfile.getPortfolioId());// 组合代码
            etrSecPositionFlowDtoUnit.setSecAcctId(secAcctId);// 证券账户
            etrSecPositionFlowDtoUnit.setFinprodId("XN_PRODID999");// 金融产品代码
            etrSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
            etrSecPositionFlowDtoUnit.setInvAim("999999");
            etrSecPositionFlowDtoUnit.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());// 持仓类型
            etrSecPositionFlowDtoUnit.setCdate(infContractDetailfile.getTradeDate());// 日期
            etrSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
            // modify by etliu 20220303 start
            // 质押式回购出入库计算时，原公式：标准券数量=质押数量*标准折算率，改成：标准券数量=质押数量*标准折算率*面值/100  ---需求张琼
//            etrSecPositionFlowDtoUnit.setAmount(infContractDetailfile.getShareAmt().multiply(converseRadio));// 交易数量 * 当日折算率
            etrSecPositionFlowDtoUnit.setAmount(infContractDetailfile.getShareAmt().multiply(converseRadio)
                    .multiply(百元券面).divide(hundred));// 交易数量 * 当日折算率
            // modify by etliu 20220303 end
            etrSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
            etrSecPositionFlowDtoUnit.setTradeId(seqno);// 指令编号

            etrSecPositionFlows.add(etrSecPositionFlowDtoUnit);
            etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
            etrPositionHoldDto.setDealEtr(true);
            EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);

            //2.交易持仓流水（质押）：正流水
            List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();

            PtlSecPositionFlowDto ptlSecPositionFlowDtoUnit = new PtlSecPositionFlowDto();// 组合持仓流水DTO
            /********************** 持仓流水DTO构造 *************************************/
            ptlSecPositionFlowDtoUnit.setSeqno(IDGenerator.generate());
            ptlSecPositionFlowDtoUnit.setPortfolioId(infContractDetailfile.getPortfolioId());// 组合代码
            ptlSecPositionFlowDtoUnit.setSecAcctId(secAcctId);// 证券账户
            ptlSecPositionFlowDtoUnit.setFinprodId(trdTempStasecFlowDetail.getFinprodId());// 金融产品代码
            ptlSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
            ptlSecPositionFlowDtoUnit.setInvAim("999999");
            ptlSecPositionFlowDtoUnit.setHodingType(DictCodeList.持仓类型_质押.getValue());// 持仓类型
            ptlSecPositionFlowDtoUnit.setCdate(infContractDetailfile.getTradeDate());// 日期
            ptlSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
            ptlSecPositionFlowDtoUnit.setAmount(trdTempStasecFlowDetail.getAmt());// 数量
            ptlSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
            ptlSecPositionFlowDtoUnit.setTradeId(seqno);// 指令编号

            ptlSecPositionFlows.add(ptlSecPositionFlowDtoUnit);

            //3.交易持仓流水（标准券）：正流水
            PtlSecPositionFlowDto ptlSecPositionFlowDtoUnit2 = new PtlSecPositionFlowDto();// 组合持仓流水DTO
            /********************** 持仓流水DTO构造 *************************************/
            ptlSecPositionFlowDtoUnit2.setSeqno(IDGenerator.generate());
            ptlSecPositionFlowDtoUnit2.setPortfolioId(infContractDetailfile.getPortfolioId());// 组合代码
            ptlSecPositionFlowDtoUnit2.setSecAcctId(secAcctId);// 证券账户
            ptlSecPositionFlowDtoUnit2.setFinprodId("XN_PRODID999");// 金融产品代码
            ptlSecPositionFlowDtoUnit2.setBranch(new Long(0));// 分支序号
            ptlSecPositionFlowDtoUnit2.setInvAim("999999");
            ptlSecPositionFlowDtoUnit2.setHodingType(DictCodeList.持仓类型_实际.getValue());// 持仓类型
            ptlSecPositionFlowDtoUnit2.setCdate(infContractDetailfile.getTradeDate());// 日期
            ptlSecPositionFlowDtoUnit2.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
            // modify by etliu 20220303 start
            // 质押式回购出入库计算时，原公式：标准券数量=质押数量*标准折算率，改成：标准券数量=质押数量*标准折算率*面值/100  ---需求张琼
//            ptlSecPositionFlowDtoUnit2.setAmount(infContractDetailfile.getShareAmt().multiply(converseRadio));// 交易数量 * 当日折算率
            ptlSecPositionFlowDtoUnit2.setAmount(infContractDetailfile.getShareAmt().multiply(converseRadio)
                    .multiply(百元券面).divide(hundred));// 交易数量 * 当日折算率
            // modify by etliu 20220303 end
            ptlSecPositionFlowDtoUnit2.setpFinprodId(null);// 母金融产品代码
            ptlSecPositionFlowDtoUnit2.setTradeId(seqno);// 指令编号

            ptlSecPositionFlows.add(ptlSecPositionFlowDtoUnit2);

            // 插入持仓流水
            for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlSecPositionFlows) {
                basePtlSecPositionInfoService.inertSecPositionFlow(ptlSecPositionFlow);
            }
        } else if(DictCodeListExt3.交易方向_质押券出库.getValue().equals(trdTempStasecFlow.getPs())) {
            //0.指令持仓流水（可用）：正流水   add by lijie2 20201028
            EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
            List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();// 持仓流水
            EtrSecPositionFlowDto etrSecPositionFlowDtoUnit = new EtrSecPositionFlowDto();// 指令持仓流水DTO
            /********************** 指令持仓流水DTO构造 *************************************/
            etrSecPositionFlowDtoUnit.setSeqno(IDGenerator.generate());
            etrSecPositionFlowDtoUnit.setPortfolioId(infContractDetailfile.getPortfolioId());// 组合代码
            etrSecPositionFlowDtoUnit.setSecAcctId(secAcctId);// 证券账户
            etrSecPositionFlowDtoUnit.setFinprodId(trdTempStasecFlowDetail.getFinprodId());// 金融产品代码
            etrSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
            etrSecPositionFlowDtoUnit.setInvAim("999999");
            etrSecPositionFlowDtoUnit.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());// 持仓类型
            etrSecPositionFlowDtoUnit.setCdate(infContractDetailfile.getTradeDate());// 日期
            etrSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
            etrSecPositionFlowDtoUnit.setAmount(trdTempStasecFlowDetail.getAmt());// 数量
            etrSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
            etrSecPositionFlowDtoUnit.setTradeId(seqno);// 指令编号
            // 插入指令持仓流水
            etrSecPositionFlows.add(etrSecPositionFlowDtoUnit);
            etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
            etrPositionHoldDto.setDealEtr(true);
            EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);


            //1.交易持仓流水（质押）：负流水
            List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();

            PtlSecPositionFlowDto ptlSecPositionFlowDtoUnit = new PtlSecPositionFlowDto();// 组合持仓流水DTO
            /********************** 持仓流水DTO构造 *************************************/
            ptlSecPositionFlowDtoUnit.setSeqno(IDGenerator.generate());
            ptlSecPositionFlowDtoUnit.setPortfolioId(infContractDetailfile.getPortfolioId());// 组合代码
            ptlSecPositionFlowDtoUnit.setSecAcctId(secAcctId);// 证券账户
            ptlSecPositionFlowDtoUnit.setFinprodId(trdTempStasecFlowDetail.getFinprodId());// 金融产品代码
            ptlSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
            ptlSecPositionFlowDtoUnit.setInvAim("999999");
            ptlSecPositionFlowDtoUnit.setHodingType(DictCodeList.持仓类型_质押.getValue());// 持仓类型
            ptlSecPositionFlowDtoUnit.setCdate(infContractDetailfile.getTradeDate());// 日期
            ptlSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
            ptlSecPositionFlowDtoUnit.setAmount(trdTempStasecFlowDetail.getAmt().multiply(new BigDecimal(-1)));// 数量
            ptlSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
            ptlSecPositionFlowDtoUnit.setTradeId(seqno);// 指令编号

            ptlSecPositionFlows.add(ptlSecPositionFlowDtoUnit);

            //2.交易持仓流水（标准券）：负流水
            PtlSecPositionFlowDto ptlSecPositionFlowDtoUnit2 = new PtlSecPositionFlowDto();// 组合持仓流水DTO
            /********************** 持仓流水DTO构造 *************************************/
            ptlSecPositionFlowDtoUnit2.setSeqno(IDGenerator.generate());
            ptlSecPositionFlowDtoUnit2.setPortfolioId(infContractDetailfile.getPortfolioId());// 组合代码
            ptlSecPositionFlowDtoUnit2.setSecAcctId(secAcctId);// 证券账户
            ptlSecPositionFlowDtoUnit2.setFinprodId("XN_PRODID999");// 金融产品代码
            ptlSecPositionFlowDtoUnit2.setBranch(new Long(0));// 分支序号
            ptlSecPositionFlowDtoUnit2.setInvAim("999999");
            ptlSecPositionFlowDtoUnit2.setHodingType(DictCodeList.持仓类型_实际.getValue());// 持仓类型
            ptlSecPositionFlowDtoUnit2.setCdate(infContractDetailfile.getTradeDate());// 日期
            ptlSecPositionFlowDtoUnit2.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
            // modify by etliu 20220303 start
            // 质押式回购出入库计算时，原公式：标准券数量=质押数量*标准折算率，改成：标准券数量=质押数量*标准折算率*面值/100  ---需求张琼
//            ptlSecPositionFlowDtoUnit2.setAmount(infContractDetailfile.getShareAmt().multiply(converseRadio).multiply(new BigDecimal(-1)));// 数量
            ptlSecPositionFlowDtoUnit2.setAmount(infContractDetailfile.getShareAmt().multiply(converseRadio)
                    .multiply(百元券面).divide(hundred).multiply(new BigDecimal(-1)));// 数量
            // modify by etliu 20220303 end
            ptlSecPositionFlowDtoUnit2.setpFinprodId(null);// 母金融产品代码
            ptlSecPositionFlowDtoUnit2.setTradeId(seqno);// 指令编号

            ptlSecPositionFlows.add(ptlSecPositionFlowDtoUnit2);

            // 插入持仓流水
            for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlSecPositionFlows) {
                basePtlSecPositionInfoService.inertSecPositionFlow(ptlSecPositionFlow);
            }
        }

        //更新指令完成度
        List<EtrFinishStatusDto> baseEtrInvestInfoDtos = new ArrayList<>();
        EtrFinishStatusDto status = new EtrFinishStatusDto();
        status.setEntrustId(infContractDetailfile.getEntrustId());
        status.setTradeId(seqno);
        status.setFinishDate(infContractDetailfile.getTradeDate());
        status.setFinishAmt(trdTempStasecFlowDetail.getAmt());
        baseEtrInvestInfoDtos.add(status);
        baseEtrInvestInfoService.completeDelegate(baseEtrInvestInfoDtos);

        // 调用新头寸接口 add by fuyongsheng 20220308 start
        BeanUtil.copyPropertiesIgnoreNull(trdTempDto, infContractDetailfile);
        // 关联系统交易编号
        trdTempDto.setfTradeId(infContractDetailfile.getDealNo());
        trdTempDto.setTradeId(trdTempStasecFlow.getSeqno());
        // 金融产品类型 = 债券
        trdTempDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // 交割日
        trdTempDto.setSettleDate(infContractDetailfile.getTradeDate());
        // 调用新头寸接口 add by fuyongsheng 20220308 end
        positionRequestTradeReportService.sendTradeReportPositionRequest(trdTempDto);
    }


    /**
     * 成交数据预清算
     *
     * @param trdTempDto
     * @return
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public void tradePreClear(TrdTempDto trdTempDto, InfContractDetailfile infContractDetailfile)
            throws Exception {

        // 金融产品交易信息DTO.金融产品交易trdTempDto -> 金融产品交易表
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, trdTempDto.getTrdProductTempDeal());
        if(StringUtil.equals(trdProductDeal.getPs(),DictCodeListExt3.交易方向_逆回购.getValue()) ||StringUtil.equals(trdProductDeal.getPs(),DictCodeListExt3.交易方向_正回购.getValue()) ) {
            trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
        }

        // 金融产品交易信息DTO.金融产品交易附属表trdTempDto -> 金融产品交易附属表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, trdTempDto.getTrdProductTempDealAdd());

        // 金融产品交易信息DTO.划款指令表trdTempDto -> 划款指令表
        StlPayOrdersDto stlPayOrdersDto = null;
        if (!StringUtil.isEmptyList(trdTempDto.getBussTrdTransferOrder())) {
            stlPayOrdersDto = new StlPayOrdersDto();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, trdTempDto.getBussTrdTransferOrder().get(0));
        }

        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
        baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
        // 是否来自场内交易下单成交标识
        baseTradeProductDealDto.setIsFromExchange(DictCodeList.是否_是.getValue());
        // 划款指令
        if (stlPayOrdersDto != null) {
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrdersDto));
        }
        finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
        finProductTradeDto.setNeedSum(true);
        String assetType2 = entrustMsgbyMQMapper.queryAssetType1(trdTempDto.getEntrustId());
        finProductTradeDto.setAssetType2(assetType2);
        // 委托代码不为空处理委托代码
        finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
            String entrustId = trdTempDto.getEntrustId();
            if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
                item.setEntrustId(entrustId);
            }
        });

        String type6 = null;
        if (trdTempDto.getFinProductType() != null) {
            type6 = trdTempDto.getFinProductType().getType6();
        }

        // 正回购，处理标准券持仓
//        if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(trdTempDto.getFinprodType2())
//            && StringUtil.isNotBlank(type6) && !DictCodeList.回购类型_质押式协议回购.getValue().equals(type6)
//            && trdTempDto.getFinTradeProduct() != null && trdTempDto.getFinTradeProduct().getPrin() != null) {
//            List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();
//            PtlSecPositionFlowDto ptlSecPositionFlowDtoUnit2 = new PtlSecPositionFlowDto();// 组合持仓流水DTO
//            /********************** 持仓流水DTO构造 *************************************/
//            ptlSecPositionFlowDtoUnit2.setSeqno(IDGenerator.generate());
//            ptlSecPositionFlowDtoUnit2.setPortfolioId(trdTempDto.getPortfolioId());// 组合代码
//            ptlSecPositionFlowDtoUnit2.setSecAcctId(trdTempDto.getSecManageAcctId());// 证券账户
//            ptlSecPositionFlowDtoUnit2.setFinprodId("XN_PRODID999");// 金融产品代码
//            ptlSecPositionFlowDtoUnit2.setBranch(new Long(0));// 分支序号
//            ptlSecPositionFlowDtoUnit2.setInvAim(
//                    AllTradeCommonUtil.getInvAim(trdTempDto.getPortfolioId()));
//            ptlSecPositionFlowDtoUnit2.setHodingType(DictCodeList.持仓类型_实际.getValue());// 持仓类型
//            ptlSecPositionFlowDtoUnit2.setCdate(trdTempDto.getSettleDate());// 日期
//            ptlSecPositionFlowDtoUnit2.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
//            ptlSecPositionFlowDtoUnit2.setAmount(
//                    trdTempDto.getFinTradeProduct().getPrin().multiply(new BigDecimal(-1)));// 数量
//            ptlSecPositionFlowDtoUnit2.setpFinprodId(null);// 母金融产品代码
//            ptlSecPositionFlowDtoUnit2.setTradeId(trdTempDto.getTradeId());// 指令编号
//            ptlSecPositionFlows.add(ptlSecPositionFlowDtoUnit2);
//            // 插入持仓流水
//            for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlSecPositionFlows) {
//                basePtlSecPositionInfoService.inertSecPositionFlow(ptlSecPositionFlow);
//            }
//        }
        // ZGTFCP-1927:深交所逻辑跟上交所一样,故注释 modify by fuyongsheng 20220328 start
        // 深交所到期续做更新 首期交易的期限、利息、到期交付日、到期结算金额。
//        if (StringUtil.equals(trdTempDto.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())
//            && DictCodeListExt3.交易方向_到期续做.getValue().equals(trdTempDto.getPs())){
//            this.updateRepoH01Market04T56(trdTempDto);
//            return;
//        }
        // ZGTFCP-1927:深交所逻辑跟上交所一样,故注释 modify by fuyongsheng 20220328 end

        trdTempDto.getTrdProductTempDeal().setTotleTradeId(trdTempDto.getTotleTradeId());
        // 写入金融产品临时交易表
        trdProductTempDealMapper.insertSelective(trdTempDto.getTrdProductTempDeal());

        // 多组合系列户指令需要登录临时汇总交易表 add by fuyongsheng 20220511 start
        // 防止重复登录
        if(StringUtil.isNotEmpty(trdTempDto.getTotleTradeId()) &&
                null == trdProductTempDealTotalMapper.selectByPrimaryKey(trdTempDto.getTotleTradeId())){
            // 写入金融产品临时汇总交易表
            TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, trdTempDto.getTrdProductTempDeal());
            trdProductTempDealTotalMapper.insertSelective(trdFinProductDealTotal);
        }
        // 多组合系列户指令需要登录临时汇总交易表 add by fuyongsheng 20220511 end

        // 写入金融产品临时交易附属表
        trdProductTempDealAddMapper.insertSelective(trdTempDto.getTrdProductTempDealAdd());

        if (StringUtil.isNotEmpty(trdTempDto.getTrdProductTempDealB())){
            // 写入金融产品临时交易表
            trdProductTempDealMapper.insertSelective(trdTempDto.getTrdProductTempDealB());
        }
        if (StringUtil.isNotEmpty(trdTempDto.getTrdProductTempDealAddB())){
            // 写入金融产品临时交易附属表
            trdProductTempDealAddMapper.insertSelective(trdTempDto.getTrdProductTempDealAddB());
        }
        // 写入临时划款指令表
        if (stlPayOrdersDto != null) {
            StlPayTempOrders stlPayTempOrders = new StlPayTempOrders();
            BeanUtil.copyPropertiesIgnoreNull(stlPayTempOrders,
                    trdTempDto.getBussTrdTransferOrder().get(0));
            stlPayTempOrdersMapper.insertSelective(stlPayTempOrders);
        }
        // 质押式回购
        if ((DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(trdTempDto.getFinprodType2())
            || DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(trdTempDto.getFinprodType2()))
            && trdTempDto.getFinTradeProduct() != null&& trdTempDto.getFinTradeProduct().getFinprodId() != null) {
            // 写入交易类金融产品表
            finTradeProductMapper.insertSelective(trdTempDto.getFinTradeProduct());
            // 写入金融产品分类
            finProductTypeMapper.insertSelective(trdTempDto.getFinProductType());
            // 写入回购质押品表
            if (DictCodeList.回购类型_质押式协议回购.getValue().equals(trdTempDto.getFinProductType().getType6())
                && trdTempDto.getFinDealPledge().getPledgeFinprodId() != null) {
                finDealPledgeMapper.insertSelective(trdTempDto.getFinDealPledge());
            }
        }


        //协议回购中途换券/解除质押
        if((DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(trdTempDto.getFinprodType2()) || DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(trdTempDto.getFinprodType2()))
                && (StringUtil.equals(trdTempDto.getPs(), DictCodeListExt3.交易方向_解除质押.getValue())
                || StringUtil.equals(trdTempDto.getPs(), DictCodeListExt3.交易方向_中途换券.getValue()))) {
            //写入质押券置换临时表
            if(StringUtil.isNotEmpty(trdTempDto.getFinPledgeDispTemp())) {
                finPledgeDispTempMapper.insertSelective(trdTempDto.getFinPledgeDispTemp());

                //写入质押券置换明细临时表
                if(!StringUtil.isEmptyList(trdTempDto.getFinPledgeDispDetailTempList())) {
                    for(FinPledgeDispDetailTemp finPledgeDispDetailTemp:trdTempDto.getFinPledgeDispDetailTempList()) {
                        finPledgeDispDetailTempMapper.insertSelective(finPledgeDispDetailTemp);
                    }
                }
            }

            //写入回购质押品表（中途换券，先删除旧数据再写入）
            //GZYHTZJY-1159:日间成交不处理质押券 modify by fuyongsheng start
//            Map<String, Object> deleteParmas = new HashMap<String, Object>();
//            deleteParmas.put("finprodId",trdTempDto.getFinprodId());
//            deleteParmas.put("branch",0L);
//            entrustMsgbyMQMapper.deleteFinDealPledge(deleteParmas);
//
//            if(StringUtil.equals(trdTempDto.getPs(), DictCodeListExt3.交易方向_中途换券.getValue())
//                    && StringUtil.isNotEmpty(trdTempDto.getFinDealPledge())) {
//                finDealPledgeMapper.insertSelective(trdTempDto.getFinDealPledge());
//            }
          //GZYHTZJY-1159:日间成交不处理质押券 modify by fuyongsheng end
        }
        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
        // 手工交收判断 add by yufei 20201022 start
        finProductTradeDto.setIsDealSettlePosition(trdTempDto.getIsManualSettle());
        // 手工交收判断 add by yufei 20201022 end

        // 质押式协议回购头寸、现金走配置处理逻辑
//        if (DictCodeList.回购类型_质押式协议回购.getValue().equals(trdTempDto.getAssetType())){
//            // 场景参数设置
//            PostionParameter parameter = new PostionParameter();
//            parameter.setPs(trdTempDto.getPs());
//            parameter.setTradeMarket(trdTempDto.getTradeMarket());
//            parameter.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
//            parameter.setOperationType("NEW");
//            parameter.setTradeType("02");
//            parameter.setBusinessType(DictCodeList.回购类型_质押式协议回购.getValue().concat(trdTempDto.getFinprodType2()));
//            // 数据来源设置
//            if (StringUtil.equals(trdTempDto.getPs(), DictCodeListExt3.交易方向_解除质押.getValue())){
//                InfContractDetailfile infContractDetailfile = (InfContractDetailfile)trdTempDto.getData();
//                BigDecimal faceValue = trdCommonBaseService.getFaceValue(infContractDetailfile.getFinprodId(), infContractDetailfile.getTradeDate());
//                trdTempDto.setTradeAmt((faceValue == null ? BigDecimal.valueOf(100) : faceValue).multiply(infContractDetailfile.getShareAmt()));
//            }
//            trdTempDto.setIsFinish(DictCodeList.是否_是.getValue());
//            trdTempDto.setTradeDate(iPlatformDate.getSysDate());
//            if (StringUtil.equals(trdTempDto.getPs(),DictCodeListExt3.交易方向_到期续做.getValue()) && StringUtil.isNotEmpty(trdTempDto.getRedoType())){
//                parameter.setPs(parameter.getPs()+trdTempDto.getRedoType());
//            }
//            parameter.setCustom(trdTempDto);
//            logger.info("持仓请求报文:" + JsonUtils.object2Json(parameter));
//            commonPositionService.process(parameter);
//
//            // 3.调用基础组件.【投资委托组件.完成委托】，写入委托完成状况信息(投资委托场合才执行此步)
//            if(!finProductTradeDto.isNeedHandleFinishEntrust()){
//                baseTradeProductDealCommService.finishEntrust(finProductTradeDto.getBaseTradeProductDealDtos(), false);
//            }
//        }else {
        // 处理头寸
        positionRequestTradeReportService.sendTradeReportPositionRequest(trdTempDto);
        tradePreCommonService.executePosition(finProductTradeDto, infContractDetailfile.getAcctNo());
//        }
    }

    /**
     * 深交所到期续做更新 首期交易的期限、利息、到期交付日、到期结算金额。
     * @param trdTempDto
     */
    private void updateRepoH01Market04T56(TrdTempDto trdTempDto) {
        // 更新金融产品交易临时表
        if (StringUtil.isNotEmpty(trdTempDto.getTrdProductTempDeal())){
            entrustMsgbyMQMapper.updateTrdProductTempDeal(trdTempDto.getTrdProductTempDeal());
            entrustMsgbyMQMapper.updateTrdProductDeal(trdTempDto.getTrdProductTempDeal());
        }
        // 更新金融产品标的表
        if (StringUtil.isNotEmpty(trdTempDto.getFinTradeProduct())){
            finTradeProductMapper.updateByPrimaryKeySelective(trdTempDto.getFinTradeProduct());
        }
        // 更新质押品表
        if (StringUtil.isNotEmpty(trdTempDto.getFinDealPledge())){
            finDealPledgeMapper.updateByPrimaryKeySelective(trdTempDto.getFinDealPledge());
        }
    }

    /**
     * 根据成交明细Dto，构筑股票交易需要反映到数据库的实体
     *
     * @param dto
     *            画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void trdStock(TrdTempDto dto,InfContractDetailfile infContractDetailfile) {
        // 金融产品临时交易Entity
        // 构造开始----------------------------------------------------------
        BeanUtil.copyPropertiesIgnoreNull(dto, infContractDetailfile);
        // ZGTFCP-1242:将orderId赋值为空 modify by fuyongsheng 20220317 start
        dto.setOrderId(null);
        // ZGTFCP-1242:将orderId赋值为空 modify by fuyongsheng 20220317 end
        // 关联系统交易编号
        dto.setfTradeId(infContractDetailfile.getDealNo());
        // 分支序号
//        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
//        Long branchLong = Long.parseLong(branch);
//        dto.setBranch(branchLong);
        dto.setBranch(0L);
        // 金融产品类型 = 股票
        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        // 交割日
        dto.setSettleDate(infContractDetailfile.getTradeDate());
        // 1、根据成交明细DTO.资金账户代码取资金账户信息里的所属券商。2、根据所属券商、成交明细DTO.股东代码，取证券账户信息里的证券账户代码
        // 更改为 根据成交明细DTO.资金账户代码取证券账户信息里的证券账户代码
        Map<String,Object> secMap = new HashMap<>();
        secMap.put("acctNo", infContractDetailfile.getAcctNo());
        secMap.put("portfolioId", infContractDetailfile.getPortfolioId());
        secMap.put("tradeMarket", infContractDetailfile.getTradeMarket());
    	String secAcctId = entrustMsgbyMQMapper.queryByAcctNo(secMap);
    	dto.setSecManageAcctId(secAcctId);
    	// 随交易支付费用（本次不计算，为0）
    	dto.setFeeAmtPay(BigDecimal.ZERO);
    	// 总交易费用（本次不计算，为0）
    	dto.setFeeAmt(BigDecimal.ZERO);
    	// 交易金额（如果是买入：成交明细DTO.成交金额+随交易支付费用 如果是卖出：成交明细DTO.成交金额-随交易支付费用）
    	if(DictCodeList.交易方向_买入.getValue().equals(infContractDetailfile.getPs())){
    		dto.setTradeAmt(ArithmeticUtil.doAdd(infContractDetailfile.getTradeAmt(), dto.getFeeAmtPay()));
    	}else if(DictCodeList.交易方向_卖出.getValue().equals(infContractDetailfile.getPs())){
    		dto.setTradeAmt(ArithmeticUtil.subtract(infContractDetailfile.getTradeAmt(), dto.getFeeAmtPay()));
    	}
    	// add by etliu 20201212 SNSTZJY-2333 【金融产品交易临时交易表】交易所日间成交数据，股票、基金的全价总额没有登录
    	dto.setFpriceAmt(dto.getTradeAmt());

    	// 交易方式（根据成交明细DTO.交易平台判断，如果是竞价撮合平台，则为竞价交易。如果是上交所大宗交易平台、深交所大宗平台，则为大宗交易。）
    	if(DictCodeList.交易平台_竞价撮合平台.getValue().equals(infContractDetailfile.getTradePlat())){
    		dto.setDealMode(DictCodeListExt2.外汇交易模式_竞价交易.getValue());
    	}else if(DictCodeList.交易平台_大宗交易平台.getValue().equals(infContractDetailfile.getTradePlat())){
    		dto.setDealMode(DictCodeListExt2.外汇交易模式_大宗交易.getValue());
    	}
    	// 是否锁定 默认否
    	dto.setWithLockPeriod(DictCodeListExt2.是否有锁定期_否.getValue());
    	// 交易编号
    	BussinessCodeParameter param = new BussinessCodeParameter();
        param.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_股票投资交易));
        dto.setTradeId(autoPeriodCommonService.buildBussinessCode(param));

        // 多组合系列户指令须登录临时交易汇总表
        if(tradePreCommonService.isMultiPortFolioEntrust(infContractDetailfile.getEntrustId())
                && StringUtil.isEmpty(dto.getTotleTradeId())){
            dto.setTotleTradeId(autoPeriodCommonService.buildBussinessCode(param));
        }

        // 本方交易员
        String trader = entrustMsgbyMQMapper.queryTradeId(infContractDetailfile.getEntrustId());
        dto.setTrader(trader);
        // 交易对手
        EtrEntrust etrEntrust = entrustMsgbyMQMapper.queryEtrEntrust(infContractDetailfile.getEntrustId());
        dto.setCounterId(etrEntrust.getCounterId());
        if(dto.getCcy() == null){
            dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        }
        // 投管业务类型
        dto.setTgBusiType(DictCodeListExt2.平安业务类型_股票交易.getValue());

        TrdProductTempDeal trdProductTempDeal = new TrdProductTempDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductTempDeal, dto);
        trdProductTempDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

        dto.setTrdProductTempDeal(trdProductTempDeal);
        // 金融产品临时交易Entity
        // 构造结束----------------------------------------------------------

        // 金融产品临时交易附属表Entity
        // 构造开始----------------------------------------------------------
        TrdProductTempDealAdd trdProductTempDealAdd = new TrdProductTempDealAdd();
        trdProductTempDealAdd.setTradeId(dto.getTradeId()); // 交易编号
        trdProductTempDealAdd.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        trdProductTempDealAdd.setEntrustId(dto.getEntrustId()); // 委托代码
        trdProductTempDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue()); // 生成方式
        trdProductTempDealAdd.setDealMode(dto.getDealMode()); // 交易方式
        trdProductTempDealAdd.setWithLockPeriod(dto.getWithLockPeriod()); // 是否有锁定期
        dto.setTrdProductTempDealAdd(trdProductTempDealAdd);
        // 金融产品临时交易附属表Entity
        // 构造结束----------------------------------------------------------

        // 临时划款指令表Entity
        // 构造开始----------------------------------------------------------
        String orderId = null;
        if (baseGetBusinessId != null) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setSeqNo("9998");
            orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
        } else {
            orderId = IDGenerator.generate();
        }
        BussTrdTransferOrderDto bussTrdTransferOrderDto = new BussTrdTransferOrderDto();
        bussTrdTransferOrderDto.setOrderId(orderId); // 指令编号
        bussTrdTransferOrderDto.setSettleDate(dto.getSettleDate()); // 结算日期
        bussTrdTransferOrderDto.setMyAcctNo(infContractDetailfile.getAcctNo()); // 本方资金账户
        if(DictCodeList.交易方向_买入.getValue().equals(infContractDetailfile.getPs())){
            bussTrdTransferOrderDto.setPs(DictCodeList.收付方向_收入.getValue()); // 收付方向
        }else if(DictCodeList.交易方向_卖出.getValue().equals(infContractDetailfile.getPs())){
            bussTrdTransferOrderDto.setPs(DictCodeListExt1.收付方向_支出.getValue()); // 收付方向
        }
        bussTrdTransferOrderDto.setSettleAmt(dto.getTradeAmt()); // 结算金额
        bussTrdTransferOrderDto.setTradeId(dto.getTradeId()); // 交易编号
        bussTrdTransferOrderDto.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        bussTrdTransferOrderDto.setCcy(dto.getCcy()); // 币种
        bussTrdTransferOrderDto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue()); // 指令状态
        List<BussTrdTransferOrderDto> bussTrdTransferOrder = new ArrayList<>();
        bussTrdTransferOrder.add(bussTrdTransferOrderDto);
        dto.setBussTrdTransferOrder(bussTrdTransferOrder);
        // 划款指令表Entity
        // 构造结束----------------------------------------------------------
    }

    /**
     * 根据成交明细Dto，构筑债券交易需要反映到数据库的实体
     *
     * @param dto
     *            画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void trdBond(TrdTempDto dto,InfContractDetailfile infContractDetailfile) {
        // 金融产品临时交易Entity
        // 构造开始----------------------------------------------------------
        BeanUtil.copyPropertiesIgnoreNull(dto, infContractDetailfile);
        // ZGTFCP-1242:将orderId赋值为空 modify by fuyongsheng 20220317 start
        dto.setOrderId(null);
        // ZGTFCP-1242:将orderId赋值为空 modify by fuyongsheng 20220317 end
        // 关联系统交易编号
        dto.setfTradeId(infContractDetailfile.getDealNo());
        // 分支序号
//        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
//        Long branchLong = Long.parseLong(branch);
//        dto.setBranch(branchLong);
        dto.setBranch(0L);
        // 金融产品类型 = 债券
        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // 交割日
        dto.setSettleDate(infContractDetailfile.getTradeDate());
        // 1、根据成交明细DTO.资金账户代码取资金账户信息里的所属券商。2、根据所属券商、成交明细DTO.股东代码，取证券账户信息里的证券账户代码
        // 更改为 根据成交明细DTO.资金账户代码取证券账户信息里的证券账户代码
        Map<String,Object> secMap = new HashMap<>();
        secMap.put("acctNo", infContractDetailfile.getAcctNo());
        secMap.put("portfolioId", infContractDetailfile.getPortfolioId());
        secMap.put("tradeMarket", infContractDetailfile.getTradeMarket());
        String secAcctId = entrustMsgbyMQMapper.queryByAcctNo(secMap);
        dto.setSecManageAcctId(secAcctId);
        // 随交易支付费用（本次不计算，为0）
        dto.setFeeAmtPay(BigDecimal.ZERO);
        // 总交易费用（本次不计算，为0）
        dto.setFeeAmt(BigDecimal.ZERO);
        // 交易金额（如果是买入：成交明细DTO.成交金额+随交易支付费用 如果是卖出：成交明细DTO.成交金额-随交易支付费用）
        if(DictCodeList.交易方向_买入.getValue().equals(infContractDetailfile.getPs())){
            dto.setTradeAmt(ArithmeticUtil.doAdd(infContractDetailfile.getTradeAmt(), dto.getFeeAmtPay()));
        }else if(DictCodeList.交易方向_卖出.getValue().equals(infContractDetailfile.getPs())){
            dto.setTradeAmt(ArithmeticUtil.subtract(infContractDetailfile.getTradeAmt(), dto.getFeeAmtPay()));
        }
        // 交易编号
        BussinessCodeParameter param = new BussinessCodeParameter();
        if(DictCodeList.交易方向_买入.getValue().equals(infContractDetailfile.getPs())){
            param.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
        }else if(DictCodeList.交易方向_卖出.getValue().equals(infContractDetailfile.getPs())){
            param.setCommonBusinessType(DictCodeList.共通业务类型_债券卖出);
        }
        dto.setTradeId(autoPeriodCommonService.buildBussinessCode(param));

        // 多组合系列户指令须登录临时交易汇总表
        if(tradePreCommonService.isMultiPortFolioEntrust(infContractDetailfile.getEntrustId())
                && StringUtil.isEmpty(dto.getTotleTradeId())){
            dto.setTotleTradeId(autoPeriodCommonService.buildBussinessCode(param));
        }

        // 清算速度_T_0
        dto.setSettleSpeed(0L);
        // 交易市场
        dto.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());

        EtrEntrust etrEntrust = entrustMsgbyMQMapper.queryEtrEntrust(infContractDetailfile.getEntrustId());
        // ZGCPCN-166:修改投资目的和业务模式赋值 modify by fuyongsheng 20220421 start
        // 投资目的
        // dto.setInvAim(StringUtil.isNotEmpty(etrEntrust.getInvAim())?etrEntrust.getInvAim():AllTradeCommonUtil.getInvAim(infContractDetailfile.getPortfolioId()));
        dto.setInvAim(StringUtil.isNotEmpty(etrEntrust.getInvAim())?etrEntrust.getInvAim(): StringUtil.EMPTY);
        //业务模式
        // dto.setBusinessModel(StringUtil.isNotEmpty(etrEntrust.getBusinessModel())?etrEntrust.getInvAim():"");
        dto.setBusinessModel(StringUtil.isNotEmpty(etrEntrust.getBusinessModel())?etrEntrust.getBusinessModel():StringUtil.EMPTY);
        // ZGCPCN-166:修改投资目的和业务模式赋值 modify by fuyongsheng 20220421 end
        // 场内指令
        TrdBondInvestTradeDto inputDto = new TrdBondInvestTradeDto();
        inputDto.setSettleDate(dto.getSettleDate());
        inputDto.setFinprodId(etrEntrust.getFinprodId());
        inputDto.setInvAim(dto.getInvAim());
        inputDto.setTradeAmt(infContractDetailfile.getShareAmt().multiply(new BigDecimal(100)));
        inputDto.setTradeMarket(infContractDetailfile.getTradeMarket());
        inputDto.setFpriceAmt(infContractDetailfile.getTradeAmt());
        inputDto.setCalculateField("fpriceAmt");
        trdBondCalcService.calculateData(inputDto);
        // 单位全价
        dto.setUnitFprice(inputDto.getUnitFprice());
        // 单位应计利息
        dto.setUnitInt(inputDto.getUnitInt());
        // 净价总额
        dto.setCpriceAmt(inputDto.getCpriceAmt());
        // 全价总额
        dto.setFpriceAmt(inputDto.getFpriceAmt());
        // 利息总额
        dto.setIntAmt(inputDto.getIntAmt());
        // 本方交易员
        String trader = entrustMsgbyMQMapper.queryTradeId(infContractDetailfile.getEntrustId());
        dto.setTrader(trader);
        // 交易对手
        dto.setCounterId(etrEntrust.getCounterId());
        if(dto.getCcy() == null){
            dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        }
        // 收益率
        dto.setYield(ArithmeticUtil.divide(inputDto.getYield(), new BigDecimal(100), 8));
        // 投管业务类型
        if(DictCodeList.交易方向_买入.getValue().equals(infContractDetailfile.getPs())){
            dto.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        }else if(DictCodeList.交易方向_卖出.getValue().equals(infContractDetailfile.getPs())){
            dto.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
        }else if(DictCodeList.交易方向_行权_回售.getValue().equals(infContractDetailfile.getPs())){
            dto.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
        }else if(DictCodeListExt3.交易方向_配债.getValue().equals(infContractDetailfile.getPs())){
            dto.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产配债.getValue());
        }

        TrdProductTempDeal trdProductTempDeal = new TrdProductTempDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductTempDeal, dto);
        trdProductTempDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

        dto.setTrdProductTempDeal(trdProductTempDeal);
        // 金融产品临时交易Entity
        // 构造结束----------------------------------------------------------

        // 金融产品临时交易附属表Entity
        // 构造开始----------------------------------------------------------
        TrdProductTempDealAdd trdProductTempDealAdd = new TrdProductTempDealAdd();
        trdProductTempDealAdd.setTradeId(dto.getTradeId()); // 交易编号
        trdProductTempDealAdd.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        trdProductTempDealAdd.setEntrustId(dto.getEntrustId()); // 委托代码
        trdProductTempDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue()); // 生成方式
        dto.setTrdProductTempDealAdd(trdProductTempDealAdd);
        // 金融产品临时交易附属表Entity
        // 构造结束----------------------------------------------------------

        // 临时划款指令表Entity
        // 构造开始----------------------------------------------------------
        String orderId = null;
        if (baseGetBusinessId != null) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setSeqNo("9998");
            orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
        } else {
            orderId = IDGenerator.generate();
        }
        BussTrdTransferOrderDto bussTrdTransferOrderDto = new BussTrdTransferOrderDto();
        bussTrdTransferOrderDto.setOrderId(orderId); // 指令编号
        bussTrdTransferOrderDto.setSettleDate(dto.getSettleDate()); // 结算日期
        bussTrdTransferOrderDto.setMyAcctNo(infContractDetailfile.getAcctNo()); // 本方资金账户
        if(DictCodeList.交易方向_买入.getValue().equals(infContractDetailfile.getPs())){
            bussTrdTransferOrderDto.setPs(DictCodeList.收付方向_收入.getValue()); // 收付方向
        }else if(DictCodeList.交易方向_卖出.getValue().equals(infContractDetailfile.getPs())){
            bussTrdTransferOrderDto.setPs(DictCodeListExt1.收付方向_支出.getValue()); // 收付方向
        }
        bussTrdTransferOrderDto.setSettleAmt(dto.getTradeAmt()); // 结算金额
        bussTrdTransferOrderDto.setTradeId(dto.getTradeId()); // 交易编号
        bussTrdTransferOrderDto.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        bussTrdTransferOrderDto.setCcy(dto.getCcy()); // 币种
        bussTrdTransferOrderDto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue()); // 指令状态
        List<BussTrdTransferOrderDto> bussTrdTransferOrder = new ArrayList<>();
        bussTrdTransferOrder.add(bussTrdTransferOrderDto);
        dto.setBussTrdTransferOrder(bussTrdTransferOrder);
        // 划款指令表Entity
        // 构造结束----------------------------------------------------------
    }

    /**
     * 根据成交明细Dto，构筑质押式回购需要反映到数据库的实体
     *
     * @param dto
     *            画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void trdRepo(TrdTempDto dto,InfContractDetailfile infContractDetailfile,String ps) {
        // 金融产品临时交易Entity
        // 关联系统交易编号
        dto.setfTradeId(infContractDetailfile.getDealNo());
        // 1、根据成交明细DTO.资金账户代码取资金账户信息里的所属券商。2、根据所属券商、成交明细DTO.股东代码，取证券账户信息里的证券账户代码
        // 更改为 根据成交明细DTO.资金账户代码取证券账户信息里的证券账户代码
        Map<String,Object> secMap = new HashMap<>();
        secMap.put("acctNo", infContractDetailfile.getAcctNo());
        secMap.put("portfolioId", infContractDetailfile.getPortfolioId());
        secMap.put("tradeMarket", infContractDetailfile.getTradeMarket());
        String secAcctId = entrustMsgbyMQMapper.queryByAcctNo(secMap);
        dto.setSecManageAcctId(secAcctId);
        // 随交易支付费用（本次不计算，为0）
        dto.setFeeAmtPay(BigDecimal.ZERO);
        // 总交易费用（本次不计算，为0）
        dto.setFeeAmt(BigDecimal.ZERO);
        // 交易金额（如果是买入：成交明细DTO.成交金额+随交易支付费用 如果是卖出：成交明细DTO.成交金额-随交易支付费用）
        if(DictCodeList.回购拆借方向_逆回购.getValue().equals(infContractDetailfile.getPs())){
            dto.setTradeAmt(ArithmeticUtil.doAdd(infContractDetailfile.getTradeAmt(), dto.getFeeAmtPay()));
        }else if(DictCodeList.回购拆借方向_正回购.getValue().equals(infContractDetailfile.getPs())){
            dto.setTradeAmt(ArithmeticUtil.subtract(infContractDetailfile.getTradeAmt(), dto.getFeeAmtPay()));
        }
        // 交易本金 = 交易金额
        dto.setPrinAmt(dto.getTradeAmt());
        // 交易份额 = 交易金额 edit by yufei 2020-10-19
        dto.setShareAmt(dto.getTradeAmt());
        // 交易编号
        BussinessCodeParameter param = new BussinessCodeParameter();
        // 交割日
        dto.setSettleDate(infContractDetailfile.getTradeDate());
        if(DictCodeList.回购拆借方向_逆回购.getValue().equals(infContractDetailfile.getPs())){
            param.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
        }else if(DictCodeList.回购拆借方向_正回购.getValue().equals(infContractDetailfile.getPs())){
            param.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
        }
        if (StringUtil.isBlank(dto.getTradeId())) {
            dto.setTradeId(autoPeriodCommonService.buildBussinessCode(param));
        }

        // 多组合系列户指令须登录临时交易汇总表
        if(tradePreCommonService.isMultiPortFolioEntrust(infContractDetailfile.getEntrustId())
                && StringUtil.isEmpty(dto.getTotleTradeId())){
            dto.setTotleTradeId(autoPeriodCommonService.buildBussinessCode(param));
        }

        // 全价总额
        dto.setFpriceAmt(infContractDetailfile.getTradeAmt());
        // 清算速度_T_0
        dto.setSettleSpeed(0L);
        // 金融产品代码
        dto.setFinprodId(infContractDetailfile.getvTradeId());
        // 金融产品类型
        if(DictCodeList.回购拆借方向_逆回购.getValue().equals(infContractDetailfile.getPs())){
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        }else if(DictCodeList.回购拆借方向_正回购.getValue().equals(infContractDetailfile.getPs())){
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        }
        // 本方交易员
        String trader = entrustMsgbyMQMapper.queryTradeId(infContractDetailfile.getEntrustId());
        dto.setTrader(trader);

        if(dto.getCcy() == null){
            dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        }
        // 投管业务类型
        if(DictCodeList.回购拆借方向_逆回购.getValue().equals(infContractDetailfile.getPs())){
            dto.setTgBusiType(DictCodeListExt1.平安业务类型_逆回购.getValue());
        }else if(DictCodeList.回购拆借方向_正回购.getValue().equals(infContractDetailfile.getPs())){
            dto.setTgBusiType(DictCodeListExt1.平安业务类型_正回购.getValue());
        }
        logger.debug("[到期续作成交数据特殊处理开始],finProductId:{}, ps:{}", dto.getFinprodId(), ps);
        if(
                // ZGTFCP-1143:修改判空逻辑 modify by fuyongsheng 20220317 start
                // dto.getFinprodId() == null
                StringUtil.isEmpty(dto.getFinprodId())
                // ZGTFCP-1143:修改判空逻辑 modify by fuyongsheng 20220317 end
                // GNS-955:到期续作登录finProdId有误 modify by fuyongsheng 20220224 start
                || DictCodeListExt3.交易方向_到期续做.getValue().equals(ps)
            // GNS-955:到期续作登录finProdId有误 modify by fuyongsheng 20220224 end
        ){
            dto.setFinprodId(infContractDetailfile.getDealNo());
        }
        logger.debug("[到期续作成交数据特殊处理结束],finProductId:{}, ps:{}", dto.getFinprodId(), ps);
        dto.setBranch(0L);
        TrdProductTempDeal trdProductTempDeal = new TrdProductTempDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductTempDeal, dto);
        trdProductTempDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

        dto.setTrdProductTempDeal(trdProductTempDeal);
        // 金融产品临时交易Entity
        // 构造结束----------------------------------------------------------

        // 金融产品临时交易附属表Entity
        // 构造开始----------------------------------------------------------
        TrdProductTempDealAdd trdProductTempDealAdd = new TrdProductTempDealAdd();
        trdProductTempDealAdd.setTradeId(dto.getTradeId()); // 交易编号
        trdProductTempDealAdd.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        trdProductTempDealAdd.setEntrustId(dto.getEntrustId()); // 委托代码
        trdProductTempDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue()); // 生成方式
        dto.setTrdProductTempDealAdd(trdProductTempDealAdd);
        // 金融产品临时交易附属表Entity
        // 构造结束----------------------------------------------------------

        // 临时划款指令表Entity
        // 构造开始----------------------------------------------------------
        String orderId = dto.getOrderId();
        if (StringUtil.isBlank(orderId)) {
            if (baseGetBusinessId != null) {
                BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                bussinessCodeParameter.setSeqNo("9998");
                orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
            } else {
                orderId = IDGenerator.generate();
            }
        }
        BussTrdTransferOrderDto bussTrdTransferOrderDto = new BussTrdTransferOrderDto();
        bussTrdTransferOrderDto.setOrderId(orderId); // 指令编号
        bussTrdTransferOrderDto.setSettleDate(dto.getSettleDate()); // 结算日期
        bussTrdTransferOrderDto.setMyAcctNo(infContractDetailfile.getAcctNo()); // 本方资金账户
        if(DictCodeList.回购拆借方向_逆回购.getValue().equals(infContractDetailfile.getPs())){
            bussTrdTransferOrderDto.setPs(DictCodeList.收付方向_收入.getValue()); // 收付方向
        }else if(DictCodeList.回购拆借方向_正回购.getValue().equals(infContractDetailfile.getPs())){
            bussTrdTransferOrderDto.setPs(DictCodeListExt1.收付方向_支出.getValue()); // 收付方向
        }
        bussTrdTransferOrderDto.setSettleAmt(dto.getTradeAmt()); // 结算金额
        bussTrdTransferOrderDto.setTradeId(dto.getTradeId()); // 交易编号
        bussTrdTransferOrderDto.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        bussTrdTransferOrderDto.setCcy(dto.getCcy()); // 币种
        bussTrdTransferOrderDto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue()); // 指令状态
        List<BussTrdTransferOrderDto> bussTrdTransferOrder = new ArrayList<>();
        bussTrdTransferOrder.add(bussTrdTransferOrderDto);
        dto.setBussTrdTransferOrder(bussTrdTransferOrder);
        // 划款指令表Entity
        // 构造结束----------------------------------------------------------

        // 交易类金融产品Entity
        // 构造开始----------------------------------------------------------
        FinTradeProduct finTradeProduct = new FinTradeProduct();
        finTradeProduct.setFinprodId(dto.getFinprodId());
        finTradeProduct.setFinprodType2(dto.getFinprodType2());
//        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
//        Long branchLong = Long.parseLong(branch);
//        finTradeProduct.setBranch(branchLong);
        finTradeProduct.setBranch(0L);
        finTradeProduct.setProfitType(DictCodeList.收益类型_收益型.getValue());
        // 起息日
        finTradeProduct.setVdate(infContractDetailfile.getVdate());
        // 到期日
        finTradeProduct.setMdate(infContractDetailfile.getMdate());

        // add by etliu 20210827 start
        String assetType1 = entrustMsgbyMQMapper.queryAssetType1(infContractDetailfile.getEntrustId());
        if(DictCodeList.回购类型_质押式竞价回购.getValue().equals(assetType1)) {
            finTradeProduct.setVdate(infContractDetailfile.getTradeDate());
            if(StringUtil.isNotEmpty(infContractDetailfile.getFinprodMarketId())) {
                finTradeProduct.setTermType(infContractDetailfile.getFinprodMarketId());
                long term = this.getTerm(infContractDetailfile.getTradeMarket(),infContractDetailfile.getFinprodMarketId());
                finTradeProduct.setTerm(term);
                finTradeProduct.setMdate(DateUtil.addDay(finTradeProduct.getVdate(), Long.valueOf(term).intValue()));
            }
        }
        // add by etliu 20210827 end

        // 名义本金
        finTradeProduct.setPrin(infContractDetailfile.getTradeAmt());
        // 利率类型
        finTradeProduct.setIntType(DictCodeList.利率类型_固定利率.getValue());
        // 固定利率
        // GNS-940:协议回购finTradeProduct的intRate回购利率登录有误,应取指令里的price modify by fuyongsheng start
        // finTradeProduct.setIntRate(ArithmeticUtil.divide(infContractDetailfile.getUnitCprice(),new BigDecimal(100),8));
        if(DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType1)){
            if(dto.getPositionBaseData() != null && dto.getPositionBaseData().getEtrEntrust() != null){
                finTradeProduct.setIntRate(dto.getPositionBaseData().getEtrEntrust().getPrice());
            }
        }else{
            finTradeProduct.setIntRate(ArithmeticUtil.divide(infContractDetailfile.getUnitCprice(),new BigDecimal(100),8));
        }
        // GNS-940:协议回购finTradeProduct的intRate回购利率登录有误,应取指令里
        // 计息基础
        finTradeProduct.setBasis(DictCodeList.计息基础_A_365.getValue());
        // 交易场所
        finTradeProduct.setTradeMarket(infContractDetailfile.getTradeMarket());
        // 本金币种
        finTradeProduct.setCcy(dto.getCcy());
        // 交易对手
        finTradeProduct.setCounterId(dto.getCounterId());
        // 交易日历
        if(DictCodeListExt1.交易场所_银行间.getValue().equals(infContractDetailfile.getTradeMarket())){
            finTradeProduct.setCalendarId("03");
        }else if(DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(infContractDetailfile.getTradeMarket())){
            finTradeProduct.setCalendarId("04");
        }else if(DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(infContractDetailfile.getTradeMarket())){
            finTradeProduct.setCalendarId("05");
        }

        dto.setAssetType(assetType1);
        if(finTradeProduct.getVdate() != null && finTradeProduct.getMdate() != null){
            if(DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType1)){
                // 首期交付日
                finTradeProduct.setVpayDate(finTradeProduct.getVdate());
                // 到期交付日
                finTradeProduct.setMpayDate(finTradeProduct.getMdate());
            }else if(DictCodeList.回购类型_质押式竞价回购.getValue().equals(assetType1)){
                // 首期交付日
                Date vtradeDate = DateUtil.addDay(finTradeProduct.getVdate(), 1);
                String vpayDatethis = this.getNextWorkDay(vtradeDate,finTradeProduct.getCalendarId());
                finTradeProduct.setVpayDate(DateUtil.parse(vpayDatethis));
                // 到期交付日
                Date mtradeDate = DateUtil.addDay(finTradeProduct.getMdate(), 1);
                String mpayDatethis = this.getNextWorkDay(mtradeDate,finTradeProduct.getCalendarId());
                finTradeProduct.setMpayDate(DateUtil.parse(mpayDatethis));
            }
            // 期限品种(暂由起息日和到期日推出)
            if(DictCodeList.回购类型_质押式竞价回购.getValue().equals(assetType1)){
//            	String termType = this.getTermType(finTradeProduct.getVdate(),finTradeProduct.getMdate(),finTradeProduct.getTradeMarket());
//                if(StringUtil.isNotEmpty(infContractDetailfile.getFinprodMarketId())) {
//                    finTradeProduct.setTermType(infContractDetailfile.getFinprodMarketId());
//                    long term = this.getTerm(finTradeProduct.getTradeMarket(),infContractDetailfile.getFinprodMarketId());
//                    finTradeProduct.setTerm(term);
//                }
            }
            else {
                // 期限
                finTradeProduct.setTerm(DateUtil.getBetweenDayNumber(finTradeProduct.getVdate(), finTradeProduct.getMdate()));
                if(StringUtil.isNotEmpty(dto.getEntrustId())) {
                    EtrEntrustAdd add = etrEntrustAddMapper.selectByPrimaryKey(dto.getEntrustId());
                    if (add != null && add.getTermDays() != null) {
                        finTradeProduct.setTerm(add.getTermDays());
                    }
                }
            }
//            finTradeProduct.setTerm(DateUtil.getBetweenDayNumber(finTradeProduct.getVdate(), finTradeProduct.getMdate()));
            // 实际占款天数
            finTradeProduct.setActCapDays(DateUtil.getBetweenDayNumber(finTradeProduct.getVpayDate(), finTradeProduct.getMpayDate()));
            // 到期利息
            BigDecimal mIntAmt = BigDecimal.ZERO;
            mIntAmt = ArithmeticUtil.multiply(finTradeProduct.getPrin(),finTradeProduct.getIntRate());
            mIntAmt = ArithmeticUtil.multiply(mIntAmt,new BigDecimal(finTradeProduct.getActCapDays()));
            mIntAmt = ArithmeticUtil.divide(mIntAmt, 365, 2);
            finTradeProduct.setmIntAmt(mIntAmt);
            // 到期金额
            BigDecimal mTradeAmt = ArithmeticUtil.doAdd(finTradeProduct.getPrin(),finTradeProduct.getmIntAmt());
            finTradeProduct.setmTradeAmt(mTradeAmt);
        }
        dto.setFinTradeProduct(finTradeProduct);
        // 交易类金融产品Entity
        // 构造结束----------------------------------------------------------

        // 金融产品分类Entity
        // 构造开始----------------------------------------------------------
        FinProductType finProductType = new FinProductType();
        // 金融产品代码 = 交易类金融产品.金融产品代码
        finProductType.setFinprodId(finTradeProduct.getFinprodId());
        // 分支序号
        finProductType.setBranch(finTradeProduct.getBranch());
        // 分类6
        finProductType.setType6(assetType1);
        // 分类7
        finProductType.setType7(infContractDetailfile.getPs());
        // 分类8
        finProductType.setType8(DictCodeList.平安资产类型_质押式回购.getValue());
        dto.setFinProductType(finProductType);
        // 金融产品分类Entity
        // 构造结束----------------------------------------------------------

        // 回购质押品Entity
        // 构造开始----------------------------------------------------------
        if(DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType1)){
            FinDealPledge finDealPledge = new FinDealPledge();

//            EtrPledge etrPledge = new EtrPledge();
//        	etrPledge.setEntrustId(infContractDetailfile.getEntrustId());
//        	// 金融产品代码 = 交易类金融产品.金融产品代码
//        	etrPledge.setFinprodId(infContractDetailfile.getFinprodId());
//        	etrPledge = etrPledgeMapper.selectByPrimaryKey(etrPledge);
//        	if(etrPledge != null){
//        		BeanUtil.copyPropertiesIgnoreNull(finDealPledge, etrPledge);
//        	}
            finDealPledge.setFinprodId(finTradeProduct.getFinprodId());
            // 分支序号
            finDealPledge.setBranch(finTradeProduct.getBranch());
            finDealPledge.setPledgeFinprodId(infContractDetailfile.getFinprodId());
            BigDecimal faceValue = trdCommonBaseService.getFaceValue(infContractDetailfile.getFinprodId(), infContractDetailfile.getTradeDate());
            finDealPledge.setPledgeFaceValue(faceValue);
            finDealPledge.setPledgeNumber(infContractDetailfile.getShareAmt());
            finDealPledge.setPledgeAmt(infContractDetailfile.getTradeAmt());
            //如果成交信息中存在折算比例取成交信息中的，如果不存在就计算
            if(StringUtil.isNotEmpty(infContractDetailfile.getScale())) {
                finDealPledge.setPledgeRatio(infContractDetailfile.getScale());
            }
            else if(infContractDetailfile.getShareAmt()!=null && faceValue!=null && infContractDetailfile.getTradeAmt()!=null){
                finDealPledge.setPledgeRatio(ArithmeticUtil.divide(infContractDetailfile.getTradeAmt(),ArithmeticUtil.multiply(faceValue,infContractDetailfile.getShareAmt()),4));
            }
            dto.setFinDealPledge(finDealPledge);
        }else  if(finTradeProduct.getFinprodId() != null && infContractDetailfile.getFinprodMarketId() != null){
            FinDealPledge finDealPledge = new FinDealPledge();
            EtrPledge etrPledge = new EtrPledge();
            etrPledge.setEntrustId(infContractDetailfile.getEntrustId());
            // 金融产品代码 = 交易类金融产品.金融产品代码
            etrPledge.setFinprodId(finTradeProduct.getFinprodId());
            etrPledge = etrPledgeMapper.selectByPrimaryKey(etrPledge);
            if(etrPledge != null){
                BeanUtil.copyPropertiesIgnoreNull(finDealPledge, etrPledge);
            }
//            finDealPledge.setFinprodId(finTradeProduct.getFinprodId());
            // 分支序号
            finDealPledge.setBranch(finTradeProduct.getBranch());
            finDealPledge.setPledgeFinprodId(infContractDetailfile.getFinprodMarketId());
            dto.setFinDealPledge(finDealPledge);
        }
        // 回购质押品Entity
        // 构造结束----------------------------------------------------------
    }

    /**
     * 根据成交明细Dto，构筑质押式回购需要反映到数据库的实体(到期续做)
     */
    public void trdRepoPeriodT56(TrdTempDto dto,InfContractDetailfile infContractDetailfile, Boolean isManualSettle){
        // 交易对手
        // 查询首期交易类金融产品数据
        FinTradeProduct firstFinTradeProduct = dto.getPositionBaseData().getFinTradeProductDto();
        // 记录原首期编号
        String vTradeId = infContractDetailfile.getvTradeId();
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(),dto.getFinprodType2())) {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
            // 到期续做，处理回购首期数据
            infContractDetailfile.setPs(DictCodeList.回购拆借方向_正回购.getValue());
            dto.setPs(DictCodeList.回购拆借方向_正回购.getValue());

            // modify by etliu 20220117 start
//            if (!StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
//                // 当前成交编号作为首期交易编号生成新的首期信息
//                infContractDetailfile.setvTradeId(infContractDetailfile.getDealNo());
//            }
            // modify by etliu 20220117 end

            // 生成首期信息
            this.trdRepo(dto,infContractDetailfile, infContractDetailfile.getPs());
            dto.setPs(DictCodeListExt3.交易方向_到期续做.getValue());
            infContractDetailfile.setPs(DictCodeListExt3.交易方向_到期续做.getValue());
            // 还原原首期编号
            infContractDetailfile.setvTradeId(vTradeId);
            // modify by etliu 20220117 start
//            if (StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
//                return;
//            }
            // modify by etliu 20220117 end
            // 到期续做，到期购回数据
            TrdProductTempDeal trdProductTempDealB = new TrdProductTempDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductTempDealB,dto.getTrdProductTempDeal());
            // 交易编号
            BussinessCodeParameter param = new BussinessCodeParameter();
            // GNS-955:业务类型赋值错误 modify by fuyongsheng 20220227 start
            // param.setCommonBusinessType(DictCodeList.共通业务类型_协议回购到期购回指令);
            param.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
            // GNS-955:业务类型赋值错误 modify by fuyongsheng 20220227 end
            if (!isManualSettle) {
                trdProductTempDealB.setTradeId(autoPeriodCommonService.buildBussinessCode(param));
            }
            trdProductTempDealB.setbTradeId(dto.getTrdProductTempDeal().getTradeId());
            trdProductTempDealB.setFinprodId(vTradeId);
            trdProductTempDealB.setPs(DictCodeListExt3.交易方向_到期购回.getValue());
            trdProductTempDealB.setShareAmt(firstFinTradeProduct.getPrin());
            trdProductTempDealB.setTradeAmt(firstFinTradeProduct.getmTradeAmt());
            trdProductTempDealB.setPrinAmt(firstFinTradeProduct.getmPrinAmt());
            trdProductTempDealB.setFpriceAmt(firstFinTradeProduct.getmUnitFprice());
            trdProductTempDealB.setVdate(firstFinTradeProduct.getVdate());
            trdProductTempDealB.setMdate(firstFinTradeProduct.getMdate());
            dto.setTrdProductTempDealB(trdProductTempDealB);
            // 金融产品临时交易附属表Entity
            // 构造开始----------------------------------------------------------
            TrdProductTempDealAdd trdProductTempDealAddB = new TrdProductTempDealAdd();
            trdProductTempDealAddB.setTradeId(trdProductTempDealB.getTradeId()); // 交易编号
            trdProductTempDealAddB.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
            trdProductTempDealAddB.setEntrustId(dto.getEntrustId()); // 委托代码
            trdProductTempDealAddB.setGenType(DictCodeListExt1.生成方式_接口接入.getValue()); // 生成方式
            dto.setTrdProductTempDealAddB(trdProductTempDealAddB);
            // 金融产品临时交易附属表Entity
            // 构造结束----------------------------------------------------------
        }else {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            // modify by etliu 20220117 start
//            if (StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
//                // 到期续做，处理回购首期数据
//                infContractDetailfile.setPs(DictCodeList.回购拆借方向_逆回购.getValue());
//                dto.setPs(DictCodeList.回购拆借方向_逆回购.getValue());
//                // 生成首期信息
//                this.trdRepo(dto,infContractDetailfile);
//                dto.setPs(DictCodeListExt3.交易方向_到期续做.getValue());
//                infContractDetailfile.setPs(DictCodeListExt3.交易方向_到期续做.getValue());
//
//                return;
//            }
            // modify by etliu 20220117 end

            EtrEntrustAddDto etrEntrustAddDto = dto.getPositionBaseData().getEtrEntrustAddDto();
            if (etrEntrustAddDto != null){
                // 首期和其他方做，到期续做和本方做,
                if (StringUtil.equals(etrEntrustAddDto.getRedoType(),"1") || StringUtil.isEmpty(firstFinTradeProduct.getFinprodId())){
                    dto.setRedoType("_1");
                    // 到期续做，处理回购首期数据
                    infContractDetailfile.setPs(DictCodeList.回购拆借方向_逆回购.getValue());
                    dto.setPs(DictCodeList.回购拆借方向_逆回购.getValue());
                    // 当前成交编号作为首期交易编号生成新的首期信息
                    infContractDetailfile.setvTradeId(infContractDetailfile.getDealNo());
                    // 生成首期信息
                    this.trdRepo(dto,infContractDetailfile,DictCodeListExt3.交易方向_到期续做.getValue());
                    dto.setPs(DictCodeListExt3.交易方向_到期续做.getValue());
                    infContractDetailfile.setPs(DictCodeListExt3.交易方向_到期续做.getValue());
                    // 还原原首期编号
                    infContractDetailfile.setvTradeId(vTradeId);
                }else {
                    // 首期和本方做，到期续做和本方做
                    dto.setRedoType("_0");
                    // 到期续做，处理回购首期数据
                    infContractDetailfile.setPs(DictCodeList.回购拆借方向_逆回购.getValue());
                    dto.setPs(DictCodeList.回购拆借方向_逆回购.getValue());
                    // 当前成交编号作为首期交易编号生成新的首期信息
                    infContractDetailfile.setvTradeId(infContractDetailfile.getDealNo());
                    // 生成首期信息
                    this.trdRepo(dto,infContractDetailfile,DictCodeListExt3.交易方向_到期续做.getValue());
                    dto.setPs(DictCodeListExt3.交易方向_到期续做.getValue());
                    infContractDetailfile.setPs(DictCodeListExt3.交易方向_到期续做.getValue());
                    // 还原原首期编号
                    infContractDetailfile.setvTradeId(vTradeId);
                    // 到期续做，到期购回数据
                    TrdProductTempDeal trdProductTempDealB = new TrdProductTempDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductTempDealB,dto.getTrdProductTempDeal());
                    // 交易编号
                    BussinessCodeParameter param = new BussinessCodeParameter();
                    // GNS-955:业务类型赋值错误 modify by fuyongsheng 20220227 start
                    // param.setCommonBusinessType(DictCodeList.共通业务类型_协议回购到期购回指令);
                    param.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
                    // GNS-955:业务类型赋值错误 modify by fuyongsheng 20220227 end
                    if (!isManualSettle) {
                        trdProductTempDealB.setTradeId(autoPeriodCommonService.buildBussinessCode(param));
                    }
                    trdProductTempDealB.setbTradeId(dto.getTrdProductTempDeal().getTradeId());
                    trdProductTempDealB.setFinprodId(vTradeId);
                    trdProductTempDealB.setPs(DictCodeListExt3.交易方向_到期购回.getValue());
                    trdProductTempDealB.setShareAmt(firstFinTradeProduct.getPrin());
                    trdProductTempDealB.setTradeAmt(firstFinTradeProduct.getmTradeAmt());
                    trdProductTempDealB.setPrinAmt(firstFinTradeProduct.getmPrinAmt());
                    trdProductTempDealB.setFpriceAmt(firstFinTradeProduct.getmUnitFprice());
                    trdProductTempDealB.setVdate(firstFinTradeProduct.getVdate());
                    trdProductTempDealB.setMdate(firstFinTradeProduct.getMdate());
                    dto.setTrdProductTempDealB(trdProductTempDealB);
                    // 金融产品临时交易附属表Entity
                    // 构造开始----------------------------------------------------------
                    TrdProductTempDealAdd trdProductTempDealAddB = new TrdProductTempDealAdd();
                    trdProductTempDealAddB.setTradeId(trdProductTempDealB.getTradeId()); // 交易编号
                    trdProductTempDealAddB.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
                    trdProductTempDealAddB.setEntrustId(dto.getEntrustId()); // 委托代码
                    trdProductTempDealAddB.setGenType(DictCodeListExt1.生成方式_接口接入.getValue()); // 生成方式
                    dto.setTrdProductTempDealAddB(trdProductTempDealAddB);
                    // 金融产品临时交易附属表Entity
                    // 构造结束----------------------------------------------------------
                }
            }else {
                // 首期和本方做，到期续做和其他三方做,只接收成交消息，无指令
                dto.setRedoType("_2");
                // 到期续做，处理回购首期数据
                infContractDetailfile.setPs(DictCodeListExt3.交易方向_到期购回.getValue());
                dto.setPs(DictCodeListExt3.交易方向_到期购回.getValue());
                // 生成原首期的到期购回数据
                this.trdRepoPeriod(dto,infContractDetailfile);
                dto.setPs(DictCodeListExt3.交易方向_到期续做.getValue());
                infContractDetailfile.setPs(DictCodeListExt3.交易方向_到期续做.getValue());
            }
        }

    }

    /**
     * 根据成交明细Dto，构筑质押式回购需要反映到数据库的实体(协议回购存续期)
     *
     * @param dto
     *            画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void trdRepoPeriod(TrdTempDto dto,InfContractDetailfile infContractDetailfile) {
        // 金融产品临时交易Entity
        // 关联系统交易编号
        dto.setfTradeId(infContractDetailfile.getDealNo());
        // 1、根据成交明细DTO.资金账户代码取资金账户信息里的所属券商。2、根据所属券商、成交明细DTO.股东代码，取证券账户信息里的证券账户代码
        // 更改为 根据成交明细DTO.资金账户代码取证券账户信息里的证券账户代码
        Map<String,Object> secMap = new HashMap<>();
        secMap.put("acctNo", infContractDetailfile.getAcctNo());
        secMap.put("portfolioId", infContractDetailfile.getPortfolioId());
        secMap.put("tradeMarket", infContractDetailfile.getTradeMarket());
        String secAcctId = entrustMsgbyMQMapper.queryByAcctNo(secMap);
        dto.setSecManageAcctId(secAcctId);
        // 随交易支付费用（本次不计算，为0）
        dto.setFeeAmtPay(BigDecimal.ZERO);
        // 总交易费用（本次不计算，为0）
        dto.setFeeAmt(BigDecimal.ZERO);
        // 交易金额
        dto.setTradeAmt(infContractDetailfile.getTradeAmt());
        // 交易本金 = 交易金额
        dto.setPrinAmt(dto.getTradeAmt());
        // 交易份额 = 成交数量
        dto.setShareAmt(dto.getTradeAmt());
        // 交易编号
        BussinessCodeParameter param = new BussinessCodeParameter();
        // 交割日
        dto.setSettleDate(infContractDetailfile.getTradeDate());
        // 交易方向
        dto.setPs(infContractDetailfile.getPs());
        // 全价总额
        dto.setFpriceAmt(infContractDetailfile.getTradeAmt());
        // 清算速度_T_0
        dto.setSettleSpeed(0L);

        // 查询首期交易类金融产品数据
        FinTradeProduct firstFinTradeProduct = dto.getPositionBaseData().getFinTradeProductDto();
        // 金融产品类型
        if (firstFinTradeProduct != null) {
            dto.setFinprodType2(firstFinTradeProduct.getFinprodType2());
        }

        // 本方交易员
        String trader = entrustMsgbyMQMapper.queryTradeId(infContractDetailfile.getEntrustId());
        dto.setTrader(trader);

        if(dto.getCcy() == null){
            dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        }

        // 投管业务类型
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(),dto.getFinprodType2())) {
            dto.setTgBusiType(DictCodeListExt1.平安业务类型_正回购.getValue());
        }else {
            dto.setTgBusiType(DictCodeListExt1.平安业务类型_逆回购.getValue());
        }
        // GNS-955:修改采番业务类型 modify by fuyongsheng 20220224 start
        if(DictCodeListExt1.平安业务类型_逆回购.getValue().equals(dto.getTgBusiType())){
            param.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
        }else {
            param.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
        }
//        if(DictCodeListExt3.交易方向_提前购回.getValue().equals(infContractDetailfile.getPs())){
//            param.setCommonBusinessType(DictCodeList.共通业务类型_协议回购提前购回指令);
//        } else if(DictCodeListExt3.交易方向_到期购回.getValue().equals(infContractDetailfile.getPs())){
//            param.setCommonBusinessType(DictCodeList.共通业务类型_协议回购到期购回指令);
//        } else if(DictCodeListExt3.交易方向_到期续做.getValue().equals(infContractDetailfile.getPs())){
//            param.setCommonBusinessType(DictCodeList.共通业务类型_协议回购到期续做指令);
//        } else if(DictCodeListExt3.交易方向_解除质押.getValue().equals(infContractDetailfile.getPs())){
//            param.setCommonBusinessType(DictCodeList.共通业务类型_协议回购解除质押指令);
//        } else if(DictCodeListExt3.交易方向_中途换券.getValue().equals(infContractDetailfile.getPs())){
//            param.setCommonBusinessType(DictCodeList.共通业务类型_协议回购中途换券指令);
//        }
        // GNS-955:修改采番业务类型 modify by fuyongsheng 20220224 end
        if (StringUtil.isBlank(dto.getTradeId())) {
            dto.setTradeId(autoPeriodCommonService.buildBussinessCode(param));
        }

        // 多组合系列户指令须登录临时交易汇总表
        if(tradePreCommonService.isMultiPortFolioEntrust(infContractDetailfile.getEntrustId())
                && StringUtil.isEmpty(dto.getTotleTradeId())){
            dto.setTotleTradeId(autoPeriodCommonService.buildBussinessCode(param));
        }

        if(dto.getFinprodId() == null){
            dto.setFinprodId(infContractDetailfile.getDealNo());
        }
        dto.setBranch(0L);
        TrdProductTempDeal trdProductTempDeal = new TrdProductTempDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductTempDeal, dto);
        trdProductTempDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

        dto.setTrdProductTempDeal(trdProductTempDeal);
        // 金融产品临时交易Entity
        // 构造结束----------------------------------------------------------

        // 金融产品临时交易附属表Entity
        // 构造开始----------------------------------------------------------
        TrdProductTempDealAdd trdProductTempDealAdd = new TrdProductTempDealAdd();
        trdProductTempDealAdd.setTradeId(dto.getTradeId()); // 交易编号
        trdProductTempDealAdd.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        trdProductTempDealAdd.setEntrustId(dto.getEntrustId()); // 委托代码
        trdProductTempDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue()); // 生成方式
        dto.setTrdProductTempDealAdd(trdProductTempDealAdd);
        // 金融产品临时交易附属表Entity
        // 构造结束----------------------------------------------------------

        // 临时划款指令表Entity
        // 构造开始----------------------------------------------------------
        String orderId = dto.getOrderId();
        if (StringUtil.isBlank(orderId)) {
            if (baseGetBusinessId != null) {
                BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                bussinessCodeParameter.setSeqNo("9998");
                orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
            } else {
                orderId = IDGenerator.generate();
            }
        }
        BussTrdTransferOrderDto bussTrdTransferOrderDto = new BussTrdTransferOrderDto();
        bussTrdTransferOrderDto.setOrderId(orderId); // 指令编号
        bussTrdTransferOrderDto.setSettleDate(dto.getSettleDate()); // 结算日期
        bussTrdTransferOrderDto.setMyAcctNo(infContractDetailfile.getAcctNo()); // 本方资金账户
        if(DictCodeList.回购拆借方向_逆回购.getValue().equals(infContractDetailfile.getPs())){
            bussTrdTransferOrderDto.setPs(DictCodeList.收付方向_收入.getValue()); // 收付方向
        }else if(DictCodeList.回购拆借方向_正回购.getValue().equals(infContractDetailfile.getPs())){
            bussTrdTransferOrderDto.setPs(DictCodeListExt1.收付方向_支出.getValue()); // 收付方向
        }
        bussTrdTransferOrderDto.setSettleAmt(dto.getTradeAmt()); // 结算金额
        bussTrdTransferOrderDto.setTradeId(dto.getTradeId()); // 交易编号
        bussTrdTransferOrderDto.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        bussTrdTransferOrderDto.setCcy(dto.getCcy()); // 币种
        bussTrdTransferOrderDto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue()); // 指令状态
        List<BussTrdTransferOrderDto> bussTrdTransferOrder = new ArrayList<>();
        bussTrdTransferOrder.add(bussTrdTransferOrderDto);
        dto.setBussTrdTransferOrder(bussTrdTransferOrder);
        // 划款指令表Entity
        // 构造结束----------------------------------------------------------
        //交易方向为中途换券或者解除质押
        if(StringUtil.equals(infContractDetailfile.getPs(), DictCodeListExt3.交易方向_解除质押.getValue())
                || StringUtil.equals(infContractDetailfile.getPs(), DictCodeListExt3.交易方向_中途换券.getValue())) {
            //生成质押券置换业务编号
            BussinessCodeParameter parameter = new BussinessCodeParameter();
            parameter.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_质押券置换));
            String bussinessCode = baseGetBusinessIdImpl.buildBussinessCode(parameter);
            //构造质押券置换DTO
            FinPledgeDispTemp finPledgeDispTemp = new FinPledgeDispTemp();
            finPledgeDispTemp.setTradeId(bussinessCode);
            finPledgeDispTemp.setfTradeId(infContractDetailfile.getvTradeId());
            finPledgeDispTemp.setTradeDate(infContractDetailfile.getTradeDate());
            dto.setFinPledgeDispTemp(finPledgeDispTemp);

            //构造质押券置换DTO
            //查询已存在的回购质押品信息
            Map<String,Object> keyMap = new HashMap<String,Object>();
            keyMap.put("vTradeId",infContractDetailfile.getvTradeId());
            keyMap.put("branch",0L);
            List<FinDealPledge> finDealPledgeList = entrustMsgbyMQMapper.getFinDealPledgeList(keyMap);
            // ZGTFCP-1882: 质押品表的tradeId有时登录的是交易编号 modify by fuyongsheng 20220325 start
            if(CollectionUtils.isEmpty(finDealPledgeList)){
                // 修改查询首期交易逻辑 modify by fuyongsheng 20220426 start
                // List<TrdProductDeal> trdProductDeals = entrustMsgbyMQMapper.getTrdProductDealByFTradeId(infContractDetailfile.getvTradeId());
//                if(!CollectionUtils.isEmpty(trdProductDeals)){
//                    keyMap.put("vTradeId",trdProductDeals.get(0).getTradeId());
//                    finDealPledgeList = entrustMsgbyMQMapper.getFinDealPledgeList(keyMap);
//                }
                if(null != firstFinTradeProduct){
                    TrdProductDeal trdProductDeal = trdProductDealMapper.selectByPrimaryKey(firstFinTradeProduct.getFinprodId());
                    if(null != trdProductDeal){
                        keyMap.put("vTradeId", trdProductDeal.getTradeId());
                        finDealPledgeList = entrustMsgbyMQMapper.getFinDealPledgeList(keyMap);
                    }
                }
                // 修改查询首期交易逻辑 modify by fuyongsheng 20220426 end
            }
            // ZGTFCP-1882: 质押品表的tradeId有时登录的是交易编号 modify by fuyongsheng 20220325 end
            List<FinPledgeDispDetailTemp> finPledgeDispDetailTempList = new ArrayList<>();
            dto.setFinPledgeDispDetailTempList(finPledgeDispDetailTempList);
            //换出券或者解除质押券
            if(!StringUtil.isEmptyList(finDealPledgeList)) {
                for(FinDealPledge pledge:finDealPledgeList ) {
                    FinPledgeDispDetailTemp outputFinPledgeDispDetailTemp = new FinPledgeDispDetailTemp();
                    outputFinPledgeDispDetailTemp.setSeqNo(IDGenerator.generate());
                    outputFinPledgeDispDetailTemp.setTradeId(finPledgeDispTemp.getTradeId());
                    outputFinPledgeDispDetailTemp.setBranch(new BigDecimal(pledge.getBranch()));
                    outputFinPledgeDispDetailTemp.setFinprodId(pledge.getPledgeFinprodId());
                    outputFinPledgeDispDetailTemp.setTradeType(DictCodeListExt3.质押券置换_置换方向_换出.getValue());
                    outputFinPledgeDispDetailTemp.setPledgeDisplaceAmt(pledge.getPledgeNumber());
                    outputFinPledgeDispDetailTemp.setScale(pledge.getPledgeRatio());
                    finPledgeDispDetailTempList.add(outputFinPledgeDispDetailTemp);
                }
            }

            //如果是中途换券，需要设置换入券
            if(StringUtil.equals(infContractDetailfile.getPs(), DictCodeListExt3.交易方向_中途换券.getValue())) {
                BigDecimal faceValue = trdCommonBaseService.getFaceValue(infContractDetailfile.getFinprodId(), infContractDetailfile.getTradeDate());
                //如果成交信息中存在折算比例取成交信息中的，如果不存在就计算
                BigDecimal scale = infContractDetailfile.getScale();
                if(!StringUtil.isNotEmpty(scale) && infContractDetailfile.getShareAmt()!=null && faceValue!=null && infContractDetailfile.getTradeAmt()!=null){
                    scale = ArithmeticUtil.divide(infContractDetailfile.getTradeAmt(),ArithmeticUtil.multiply(faceValue,infContractDetailfile.getShareAmt()),4);
                }


                FinPledgeDispDetailTemp inputFinPledgeDispDetailTemp = new FinPledgeDispDetailTemp();
                inputFinPledgeDispDetailTemp.setSeqNo(IDGenerator.generate());
                inputFinPledgeDispDetailTemp.setTradeId(finPledgeDispTemp.getTradeId());
                inputFinPledgeDispDetailTemp.setFinprodId(infContractDetailfile.getFinprodId());
                inputFinPledgeDispDetailTemp.setBranch(BigDecimal.ZERO);
                inputFinPledgeDispDetailTemp.setTradeType(DictCodeListExt3.质押券置换_置换方向_换入.getValue());
                inputFinPledgeDispDetailTemp.setPledgeDisplaceAmt(infContractDetailfile.getShareAmt());
                inputFinPledgeDispDetailTemp.setScale(scale);
                finPledgeDispDetailTempList.add(inputFinPledgeDispDetailTemp);

                //构造回购质押品dto
                FinDealPledge finDealPledge = new FinDealPledge();
                finDealPledge.setFinprodId(infContractDetailfile.getvTradeId());
                finDealPledge.setBranch(0L);
                finDealPledge.setPledgeFinprodId(infContractDetailfile.getFinprodId());
                finDealPledge.setPledgeNumber(infContractDetailfile.getShareAmt());
                finDealPledge.setPledgeRatio(scale);
                finDealPledge.setPledgeFaceValue(faceValue);
                finDealPledge.setPledgeAmt(infContractDetailfile.getTradeAmt());

                dto.setFinDealPledge(finDealPledge);
            }


        }
    }
    
    /**
     * 根据成交明细Dto，构筑期货需要反映到数据库的实体(协议回购存续期)
     *
     * @param dto
     *            画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void trdFuture(TrdTempDto dto,InfContractDetailfile infContractDetailfile) {
        // 金融产品临时交易Entity
        // 构造开始----------------------------------------------------------
        BeanUtil.copyPropertiesIgnoreNull(dto, infContractDetailfile);
        // ZGTFCP-1242:将orderId赋值为空 modify by fuyongsheng 20220317 start
        dto.setOrderId(null);
        // ZGTFCP-1242:将orderId赋值为空 modify by fuyongsheng 20220317 end
        // 关联系统交易编号
        dto.setfTradeId(infContractDetailfile.getDealNo());
        // 更改为 根据成交明细DTO.资金账户代码取证券账户信息里的证券账户代码
        Map<String,Object> secMap = new HashMap<>();
        secMap.put("acctNo", infContractDetailfile.getAcctNo());
        secMap.put("portfolioId", infContractDetailfile.getPortfolioId());
        secMap.put("tradeMarket", infContractDetailfile.getTradeMarket());
        String secAcctId = entrustMsgbyMQMapper.queryByAcctNo(secMap);
        dto.setSecManageAcctId(secAcctId);
        // 随交易支付费用（本次不计算，为0）
        dto.setFeeAmtPay(BigDecimal.ZERO);
        // 总交易费用（本次不计算，为0）
        dto.setFeeAmt(BigDecimal.ZERO);
        // 交易金额
        dto.setTradeAmt(infContractDetailfile.getTradeAmt());
        // 交易份额 = 成交数量
        dto.setShareAmt(infContractDetailfile.getShareAmt());
        // 交割日
        dto.setSettleDate(infContractDetailfile.getTradeDate());
        // 交易编号
        BussinessCodeParameter param = new BussinessCodeParameter();
        param.setCommonBusinessType(DictCodeListExt3.共通业务类型_期货交易);
        dto.setTradeId(autoPeriodCommonService.buildBussinessCode(param));
        // 交易方向
        dto.setPs(infContractDetailfile.getPs());
        // 分支序号
        dto.setBranch(0L);
        // 金融产品类型（投管分类）
        dto.setFinprodType2(DictCodeListExt3.金融产品类型_投管用_期货.getValue());
        // 交易保证金
//        dto.setDealMargin(infContractDetailfile.getDealMargin());
        TrdProductTempDeal trdProductTempDeal = new TrdProductTempDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductTempDeal, dto);
        trdProductTempDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        /*************************************************************************/
        trdProductTempDeal.setTgBusiType(DictCodeListExt3.平安业务类型_期货交易.getValue());
        dto.setTrdProductTempDeal(trdProductTempDeal);
        // 金融产品临时交易Entity
        // 构造结束----------------------------------------------------------
        
        // 金融产品临时交易附属表Entity
        // 构造开始----------------------------------------------------------
        TrdProductTempDealAdd trdProductTempDealAdd = new TrdProductTempDealAdd();
        trdProductTempDealAdd.setTradeId(dto.getTradeId()); // 交易编号
        trdProductTempDealAdd.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        trdProductTempDealAdd.setEntrustId(dto.getEntrustId()); // 委托代码
        trdProductTempDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue()); // 生成方式
        //增加保证金计算 add by yzg 20210303 start
        trdProductTempDealAdd.setDepositAmt(getDealMargin(dto,infContractDetailfile));
        dto.setTrdProductTempDealAdd(trdProductTempDealAdd);
        // 金融产品临时交易附属表Entity
        // 构造结束----------------------------------------------------------
        // 平仓盈亏（盯市）
		BigDecimal profit = calClosingInvestProfit(dto);
		
        // 临时划款指令表Entity
        // 构造开始----------------------------------------------------------
        String orderId = null;
        if (baseGetBusinessId != null) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setSeqNo("9998");
            orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
        } else {
            orderId = IDGenerator.generate();
        }
        BussTrdTransferOrderDto bussTrdTransferOrderDto = new BussTrdTransferOrderDto();
        bussTrdTransferOrderDto.setOrderId(orderId); // 指令编号
        bussTrdTransferOrderDto.setSettleDate(dto.getSettleDate()); // 结算日期
        bussTrdTransferOrderDto.setMyAcctNo(infContractDetailfile.getAcctNo()); // 本方资金账户
        bussTrdTransferOrderDto.setPs(DictCodeList.收付方向_收入.getValue()); // 收付方向 ->收
        /*******************结算金额待定************************************/
        bussTrdTransferOrderDto.setSettleAmt(profit); // 平仓盈亏（盯市）
        /*******************结算金额待定************************************/
        bussTrdTransferOrderDto.setTradeId(dto.getTradeId()); // 交易编号
        bussTrdTransferOrderDto.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        bussTrdTransferOrderDto.setCcy(dto.getCcy()); // 币种
        bussTrdTransferOrderDto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue()); // 指令状态
        // 划款指令表Entity
        // 构造结束----------------------------------------------------------
        
        //金融产品临时交易表
        trdProductTempDealMapper.insertSelective(trdProductTempDeal);
        //金融产品临时交易附属表
        trdProductTempDealAddMapper.insertSelective(trdProductTempDealAdd);
        // 写入临时划款指令表
		StlPayTempOrders stlPayTempOrders = new StlPayTempOrders();
		BeanUtil.copyPropertiesIgnoreNull(stlPayTempOrders, bussTrdTransferOrderDto);
		stlPayTempOrdersMapper.insertSelective(stlPayTempOrders);

		//1.指令持仓流水（期货）：正流水
		if (StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_买入开仓.getValue())
				|| StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_卖出开仓.getValue())) {
			EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
			List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();// 持仓流水
			EtrSecPositionFlowDto etrSecPositionFlowDtoUnit = new EtrSecPositionFlowDto();// 组合持仓流水DTO
			/********************** 持仓流水DTO构造 *************************************/
			etrSecPositionFlowDtoUnit.setSeqno(IDGenerator.generate());
			etrSecPositionFlowDtoUnit.setPortfolioId(infContractDetailfile.getPortfolioId());// 组合代码
			etrSecPositionFlowDtoUnit.setSecAcctId(secAcctId);// 证券账户
			etrSecPositionFlowDtoUnit.setFinprodId(infContractDetailfile.getFinprodId());// 金融产品代码
			etrSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
			if (StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_买入开仓.getValue())) {
				etrSecPositionFlowDtoUnit.setInvAim(DictCodeListExt3.投资目的_多头.getValue());
			} else if (StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_卖出开仓.getValue())) {
				etrSecPositionFlowDtoUnit.setInvAim(DictCodeListExt3.投资目的_空头.getValue());
			}
			etrSecPositionFlowDtoUnit.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());// 持仓类型
			etrSecPositionFlowDtoUnit.setCdate(infContractDetailfile.getTradeDate());// 日期
			etrSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
			etrSecPositionFlowDtoUnit.setAmount(infContractDetailfile.getShareAmt());// 交易数量
			etrSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
			etrSecPositionFlowDtoUnit.setTradeId(dto.getTradeId());// 指令编号

			etrSecPositionFlows.add(etrSecPositionFlowDtoUnit);
			etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
			etrPositionHoldDto.setDealEtr(true);
			EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
			
			// 2.交易持仓流水（期货）：正流水
			List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();
			PtlSecPositionFlowDto ptlSecPositionFlowDtoUnit = new PtlSecPositionFlowDto();// 组合持仓流水DTO
			/********************** 持仓流水DTO构造 *************************************/
			ptlSecPositionFlowDtoUnit.setSeqno(IDGenerator.generate());
			ptlSecPositionFlowDtoUnit.setPortfolioId(infContractDetailfile.getPortfolioId());// 组合代码
			ptlSecPositionFlowDtoUnit.setSecAcctId(secAcctId);// 证券账户
			ptlSecPositionFlowDtoUnit.setFinprodId(dto.getFinprodId());// 金融产品代码
			ptlSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
			if (StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_买入开仓.getValue())) {
				ptlSecPositionFlowDtoUnit.setInvAim(DictCodeListExt3.投资目的_多头.getValue());
			} else if (StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_卖出开仓.getValue())) {
				ptlSecPositionFlowDtoUnit.setInvAim(DictCodeListExt3.投资目的_空头.getValue());
			}
			ptlSecPositionFlowDtoUnit.setHodingType(DictCodeList.持仓类型_实际.getValue());// 持仓类型
			ptlSecPositionFlowDtoUnit.setCdate(infContractDetailfile.getTradeDate());// 日期
			ptlSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
			ptlSecPositionFlowDtoUnit.setAmount(dto.getShareAmt());// 数量
			ptlSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
			ptlSecPositionFlowDtoUnit.setTradeId(dto.getTradeId());// 指令编号
			ptlSecPositionFlows.add(ptlSecPositionFlowDtoUnit);
			// 插入持仓流水
			for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlSecPositionFlows) {
				basePtlSecPositionInfoService.inertSecPositionFlow(ptlSecPositionFlow);
			}
		} else if (StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_买入平仓.getValue())
				|| StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_卖出平仓.getValue())) {
			EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
			// 3.指令现金流水：正流水
			List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();
			EtrCashPositionFlowDto etrCashPositionFlowDtoUnit = new EtrCashPositionFlowDto();// 组合持仓流水DTO
			/********************** 持仓流水DTO构造 *************************************/
			etrCashPositionFlowDtoUnit.setSeqno(IDGenerator.generate());
			etrCashPositionFlowDtoUnit.setPortfolioId(infContractDetailfile.getPortfolioId());// 组合代码
			etrCashPositionFlowDtoUnit.setAcctId(infContractDetailfile.getAcctNo());// 资金账户

			etrCashPositionFlowDtoUnit.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());// 持仓类型
			etrCashPositionFlowDtoUnit.setCdate(infContractDetailfile.getTradeDate());// 日期
			etrCashPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
			etrCashPositionFlowDtoUnit.setAmount(trdProductTempDealAdd.getDepositAmt().add(profit));// 保证金金额 + 平仓盈亏（盯市）
			etrCashPositionFlowDtoUnit.setChlFinprodId(null);// 母金融产品代码
			etrCashPositionFlowDtoUnit.setOrderId(bussTrdTransferOrderDto.getOrderId());// 指令编号
			etrCashPositionFlowDtoUnit.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());// 冲正标识
			etrCashPositionFlowDtoUnit.setOffsetId(dto.getEntrustId());// 冲销业务编号
			etrCashPositionFlows.add(etrCashPositionFlowDtoUnit);
			etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlows);
			etrPositionHoldDto.setDealEtr(true);
			EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);

			// 4.交易现金流水（期货）：正流水
			PtlCashPositionFlowDto ptlCashPositionFlowDtoUnit = new PtlCashPositionFlowDto();
			ptlCashPositionFlowDtoUnit.setSeqno(IDGenerator.generate());
			ptlCashPositionFlowDtoUnit.setPortfolioId(infContractDetailfile.getPortfolioId());// 组合代码
			ptlCashPositionFlowDtoUnit.setAcctId(infContractDetailfile.getAcctNo());// 资金账户
			ptlCashPositionFlowDtoUnit.setHodingType(DictCodeList.持仓类型_实际.getValue());// 持仓类型
			ptlCashPositionFlowDtoUnit.setCdate(infContractDetailfile.getTradeDate());// 日期
			ptlCashPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
			ptlCashPositionFlowDtoUnit.setOrderId(bussTrdTransferOrderDto.getOrderId());// 指令编号
			//*************************************待定***************
			ptlCashPositionFlowDtoUnit.setAmount(profit);// 平仓盈亏（盯市）
			//*************************************待定***************
			ptlCashPositionFlowDtoUnit.setChlFinprodId(null);// 母金融产品代码
			basePtlCashPositionInfoService.insertCashPositionFlow(ptlCashPositionFlowDtoUnit);
			
			// 5.交易持仓流水（期货）：负流水
			PtlSecPositionFlowDto ptlSecPositionFlowDtoUnit = new PtlSecPositionFlowDto();// 组合持仓流水DTO
			/********************** 持仓流水DTO构造 *************************************/
			ptlSecPositionFlowDtoUnit.setSeqno(IDGenerator.generate());
			ptlSecPositionFlowDtoUnit.setPortfolioId(infContractDetailfile.getPortfolioId());// 组合代码
			ptlSecPositionFlowDtoUnit.setSecAcctId(secAcctId);// 证券账户
			ptlSecPositionFlowDtoUnit.setFinprodId(dto.getFinprodId());// 金融产品代码
			ptlSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
			if (StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_卖出平仓.getValue())) {
				ptlSecPositionFlowDtoUnit.setInvAim(DictCodeListExt3.投资目的_多头.getValue());
			} else if (StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_买入平仓.getValue())) {
				ptlSecPositionFlowDtoUnit.setInvAim(DictCodeListExt3.投资目的_空头.getValue());
			}
			ptlSecPositionFlowDtoUnit.setHodingType(DictCodeList.持仓类型_实际.getValue());// 持仓类型
			ptlSecPositionFlowDtoUnit.setCdate(infContractDetailfile.getTradeDate());// 日期
			ptlSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
			ptlSecPositionFlowDtoUnit.setAmount(dto.getShareAmt().multiply(new BigDecimal(-1)));// 数量
			ptlSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
			ptlSecPositionFlowDtoUnit.setTradeId(dto.getTradeId());// 指令编号
			// 插入持仓流水
			basePtlSecPositionInfoService.inertSecPositionFlow(ptlSecPositionFlowDtoUnit);
		}
		
		 //更新指令完成度
        List<EtrFinishStatusDto> baseEtrInvestInfoDtos = new ArrayList<>();
        EtrFinishStatusDto status = new EtrFinishStatusDto();
        status.setEntrustId(infContractDetailfile.getEntrustId());
        status.setTradeId(dto.getTradeId());
        status.setFinishDate(infContractDetailfile.getTradeDate());
        status.setFinishAmt(dto.getShareAmt());
        baseEtrInvestInfoDtos.add(status);
        baseEtrInvestInfoService.completeDelegate(baseEtrInvestInfoDtos);
	}
    
    /**
     * 保证金计算
     * @param dto
     * @return
     */
    public BigDecimal getDealMargin(TrdTempDto dto,InfContractDetailfile infContractDetailfile) {
    	BigDecimal entrustAmt = BigDecimal.ZERO;
    	Map<String, Object> param = new HashMap<>();
    	param.put("date", DateUtil.format(infContractDetailfile.getTradeDate()));
    	param.put("psStr", infContractDetailfile.getPs());
    	param.put("finprodId", infContractDetailfile.getFinprodId());
    	param.put("portfolioId", infContractDetailfile.getPortfolioId());
    	param.put("acctNo", infContractDetailfile.getAcctNo());
    	param.put("tradeMarket", infContractDetailfile.getTradeMarket());
    	param.put("entrustNum", infContractDetailfile.getShareAmt());
    	//证券账户
    	/*Map<String, Object> map = new HashMap<>();
    	map.put("portfolioId", infContractDetailfile.getPortfolioId());
    	map.put("acctNo", infContractDetailfile.getAcctNo());
    	List<SelectOptionDto> selectOptionDtos = restTemplate.postForList(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFutureTrade/getPtlSecAcctInfo", map,
				SelectOptionDto.class);
    	if(!StringUtil.isEmptyList(selectOptionDtos)) {
    		List<Map<String, Object>> list = (List<Map<String,Object>>)selectOptionDtos.get(0).getObject();
    		for(Map<String, Object> map2 : list) {
    			if(StringUtil.equals(map2.get("tradeMarket").toString(), infContractDetailfile.getTradeMarket())){
    				param.put("secAcctId", map2.get("secAcctId"));
    			}
    		}
    	}*/
    	param.put("secAcctId", infContractDetailfile.getBondId());
    	//昨结算价
    	String secId = infContractDetailfile.getFinprodMarketId();
		//String param = "?stock_code=" + secId;
		String queryIntegratedTick = "/joyin/quote/tick?stock_code="+ secId;
		Map quoteMap = restTemplate.getForObject(ServiceApplication.QUOTE_SERVICE_NAME + queryIntegratedTick, Map.class);
		//Map quoteMap = simpleRestTemplate.getForObject("http://192.168.90.105:8080/tams/" + queryIntegratedTick, Map.class);
		if (quoteMap != null && quoteMap.get("data") != null) {
			Map<String, Object> quoteList = (Map<String, Object>) quoteMap.get("data");
			if (null != quoteList) {
				param.put("preClosePrice", quoteList.get("preClosePrice"));
			}
		}
    	if (StringUtils.equals(infContractDetailfile.getPs(), DictCodeListExt3.交易方向_买入开仓.getValue())
				|| StringUtils.equals(infContractDetailfile.getPs(), DictCodeListExt3.交易方向_卖出开仓.getValue())) {
			BigDecimal entrustPrice = infContractDetailfile.getUnitCprice();
			entrustAmt = ArithmeticUtil.multiply(infContractDetailfile.getShareAmt(), entrustPrice);
			if (StringUtils.isNotEmpty(infContractDetailfile.getFinprodId())) {
				BigDecimal faceValue = getCurSecAmt(param);
				if (null != faceValue) {
					entrustAmt = entrustAmt.multiply(faceValue);
				} else {
					entrustAmt = null;
				}
			}
		} else if (StringUtils.equals(infContractDetailfile.getPs(), DictCodeListExt3.交易方向_买入平仓.getValue())
				|| StringUtils.equals(infContractDetailfile.getPs(), DictCodeListExt3.交易方向_卖出平仓.getValue())) {
			String ps = "";
			if (StringUtils.equals(infContractDetailfile.getPs(), DictCodeListExt3.交易方向_买入平仓.getValue())) {
				ps = DictCodeListExt3.交易方向_卖出开仓.getValue();
			} else {
				ps = DictCodeListExt3.交易方向_买入开仓.getValue();
			}
			param.put("ps", ps);
			entrustAmt = getDepositAmt(param);
		}
    	return entrustAmt;
    }
    
    /**
	 * 面值
	 * 
	 * @param param
	 * @return
	 */
	public BigDecimal getCurSecAmt(Map<String, Object> param) {
		return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFutureTrade/getCurSecAmt",
				param, BigDecimal.class);
	}
	
	/**
	 * 面值
	 * 
	 * @param param
	 * @return
	 */
	public BigDecimal getDepositAmt(Map<String, Object> param) {
		return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFutureTrade/getDepositAmt",
				param, BigDecimal.class);
	}

    /**
     * 获取下一个工作日
     *
     * @param tradeDate， calendarId
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
    public String getNextWorkDay(Date tradeDate,String calendarId) {
        if (dateCalService != null) {
            List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate,
                    DateUtil.addDays(tradeDate, 31), calendarId);
            Date tradingDay = dateCalService.calculationTradingDay(tradeDate, 1, holiday, "1");
            return DateUtil.format(tradingDay);
        }
        return null;
    }

    /**
     * 获取期限品种
     *
     * @param vDate， mDate
     * @return 期限品种
     * @see
     * @since 1.0
     */
    public String getTermType(Date vDate,Date mDate,String tradeMarket) {
        String termStr = String.valueOf(DateUtil.getBetweenDayNumber(vDate,mDate));
        int[] term={1,2,3,4,7,14,21,28,91,182};
        int termNum = 0;
        for(int item:term){
            if(item<=Integer.parseInt(termStr) && item>termNum){
                termNum = item;
            }
        }
        DecimalFormat df=new DecimalFormat("000");//设置格式
        String str=df.format(termNum);//格式转换
        String termType = "";
        String typeName = "";
        if(DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(tradeMarket)){
            typeName = "GC";
            termType = typeName.concat(str);
        }else if(DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(tradeMarket)){
            typeName = "R-";
            termType = typeName.concat(str);
        }
        return termType;
    }

    /**
     * 竞价回购根据期限品种获取期限
     * @param tradeMarket
     * @return
     * @see
     * @since 1.0
     */
    public long getTerm(String tradeMarket,String finprodMarketId) {
        Map<String,Integer> termMap= new HashMap<>();
        termMap.put("001", 1);
        termMap.put("002", 2);
        termMap.put("003", 3);
        termMap.put("004", 4);
        termMap.put("007", 7);
        termMap.put("014", 14);
        termMap.put("021", 21);
        termMap.put("028", 28);
        termMap.put("091", 91);
        termMap.put("182", 182);

        String termStr = null;
        if(DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(tradeMarket)){
            termStr =  finprodMarketId.replace("GC", "");
        }else if(DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(tradeMarket)){
            if (finprodMarketId.contains("-")) {
                termStr = finprodMarketId.substring(finprodMarketId.indexOf("-") + 1);
            }
        }
        return termMap.get(termStr);
    }

    /***
     * 更新指令进度
     * @param entrustId
     */
    public void updateEntrust(String entrustId,BigDecimal orderFinishAmt) {
        EtrEntrust etrEntrust = this.entrustMsgbyMQMapper.queryEntrustNumAndAmt(entrustId);
        if(etrEntrust!=null && (StringUtil.equals(DictCodeListExt2.交易方向_配股.getValue(),etrEntrust.getPs())
                // add by dubaojun 2020-08-14 缺陷SNSTZJY-981：此类交易不存在成交确认的接口，需要单独处理。 start
                || StringUtil.equals(DictCodeList.交易方向_申购.getValue(),etrEntrust.getPs())
                || StringUtil.equals(DictCodeListExt3.交易方向_配债.getValue(),etrEntrust.getPs())
                || StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(),etrEntrust.getPs())
                || StringUtil.equals(DictCodeListExt3.交易方向_债转股.getValue(),etrEntrust.getPs()))){
            // add by dubaojun 2020-08-14 缺陷SNSTZJY-981：此类交易不存在成交确认的接口，需要单独处理。 end
            // 如果满足，则调用投资委托组件的更新委托状态接口，将委托状态设置为“已完成”
            if(BigDecimal.ZERO.compareTo(orderFinishAmt) ==0 ) {
                etrEntrust.setEntrustStatus(DictCodeList.委托状态_已接收.getValue());
            } else {
                etrEntrust.setEntrustStatus(DictCodeList.委托状态_已完成.getValue());
            }
            entrustMsgbyMQMapper.updateEntrustStatu(etrEntrust);
//            BigDecimal entrustAmt = BigDecimal.ZERO;
//            String targetType = entrustMsgbyMQMapper.queryTargetType(entrustId);
//            if(StringUtil.equals(DictCodeListExt3.目标类型_金额.getValue(),targetType)) {
//                entrustAmt=etrEntrust.getEntrustAmt();
//            }
//            if(StringUtil.equals(DictCodeListExt3.目标类型_数量.getValue(),targetType)) {
//                entrustAmt=etrEntrust.getEntrustNum();
//            }
//
//            if(entrustAmt!=null && entrustAmt.compareTo(BigDecimal.ZERO) !=0) {
//
//                BigDecimal 总交易完成度 = ArithmeticUtil.divide(finishAmt, entrustAmt, 4);
//                总交易完成度 = ArithmeticUtil.multiply(总交易完成度, BigDecimal.valueOf(100));
//
//                // 判断委托代码的完成额度是否大于等于100
//                BigDecimal bigDecimal = new BigDecimal("100.00");
//                if (总交易完成度.compareTo(bigDecimal) == 1
//                        || 总交易完成度.compareTo(bigDecimal) == 0) {
//                    // 如果满足，则调用投资委托组件的更新委托状态接口，将委托状态设置为“已完成”
//                    etrEntrust.setEntrustStatus(DictCodeList.委托状态_已完成.getValue());
//                    entrustMsgbyMQMapper.updateEntrustStatu(etrEntrust);
//                }
//            }

        }

    }

    /**
     * 处理数据
     * @param infContractDetailfile
     * @param trdTempDto
     */
    private void saveSettleInfo(InfContractDetailfile infContractDetailfile, TrdTempDto trdTempDto) {
        // 构建手工交收信息
        TrdManualSettleInfo settleInfo = new TrdManualSettleInfo();
        BeanUtil.copyPropertiesIgnoreNull(settleInfo, infContractDetailfile);
        // SETTLE_SEQ_NO	待交割序号
        settleInfo.setSettleSeqNo(IDGenerator.generate());
        // SETTLE_STATUS	交收状态数据字典
        settleInfo.setSettleStatus(DictCodeListExt3.交收状态_未交收.getValue());
        // TRADE_ID 交易编号 原业务编号
        settleInfo.setTradeId(trdTempDto.getTradeId());
        //	委托编号
        settleInfo.setEntrustNo(infContractDetailfile.getEntrustId());
        // 指令序号
        settleInfo.setOrderId(infContractDetailfile.getOrderNo());
        // SETTLE_DATE	交收日期
        settleInfo.setSettleDate(infContractDetailfile.getSettleDay());
        // SEC_ACCT_ID	证券账户
        settleInfo.setSecAcctId(infContractDetailfile.getBondId());
        // SETTLE_NUM	交割数量
        settleInfo.setSettleNum(infContractDetailfile.getShareAmt());
        // SETTLE_AMT	交割金额
        settleInfo.setSettleAmt(infContractDetailfile.getTradeAmt());
        // COUNTER_ID	交易对手
        // 交易对手
        EtrEntrust etrEntrust = entrustMsgbyMQMapper.queryEtrEntrust(infContractDetailfile.getEntrustId());
        // ZGTFCP-1366:判断指令交易对手是否为空 modify by fuyongsheng 20220319 start
        // settleInfo.setCounterId(etrEntrust.getCounterId());
        if(StringUtil.isNotEmpty(etrEntrust.getCounterId())){
            settleInfo.setCounterId(etrEntrust.getCounterId());
        }
        // ZGTFCP-1366:判断指令交易对手是否为空 modify by fuyongsheng 20220319 end
        // modify by etliu 20220114 start
        // GZYHTZJY-1128: 【场内对账清结算1231】深交所协议回购首期，下单后“手工交收”页面查不到数据 【解决日期：2022-01-14】
//        settleInfo.setPrin(trdTempDto.getPositionBaseData().getFinTradeProductDto().getPrin());
//        settleInfo.setmTradeAmt(trdTempDto.getPositionBaseData().getFinTradeProductDto().getmTradeAmt());
//        settleInfo.setPledgeNumber(trdTempDto.getPositionBaseData().getFinDealPledge().getPledgeNumber());
//        settleInfo.setPledgeId(trdTempDto.getPositionBaseData().getFinDealPledge().getPledgeFinprodId());
//        settleInfo.setbPledgeId(entrustMsgbyMQMapper.selectPledgeId(infContractDetailfile.getvTradeId()));
        FinTradeProduct finTradeProduct = trdTempDto.getFinTradeProduct();
        if (finTradeProduct == null && trdTempDto.getPositionBaseData() != null) {
            finTradeProduct = trdTempDto.getPositionBaseData().getFinTradeProductDto();
        }
        if (finTradeProduct != null) {
            settleInfo.setPrin(finTradeProduct.getPrin());
            settleInfo.setmTradeAmt(finTradeProduct.getmTradeAmt());

            FinDealPledge finDealPledge = trdTempDto.getFinDealPledge();
            if (finDealPledge == null && trdTempDto.getPositionBaseData() != null && trdTempDto.getPositionBaseData().getFinDealPledge() != null) {
                finDealPledge = trdTempDto.getPositionBaseData().getFinDealPledge();
            }
            if (finDealPledge != null) {
                settleInfo.setPledgeNumber(finDealPledge.getPledgeNumber());
                settleInfo.setPledgeId(finDealPledge.getPledgeFinprodId());
            }
            if (StringUtil.equals(DictCodeListExt3.交易方向_中途换券.getValue(), trdTempDto.getPs())) {
                settleInfo.setbPledgeId(entrustMsgbyMQMapper.selectPledgeId(infContractDetailfile.getvTradeId()));
            }
            // GNS-955:提前购回的到期结算金额应是提前购回指令的到期结算金额，不是首期里的 modify by fuyongsheng 20220227 start
            if(StringUtil.equals(DictCodeListExt3.交易方向_提前购回.getValue(), trdTempDto.getPs())){
                EtrEntrustAdd etrEntrustAdd = entrustMsgbyMQMapper.queryEtrEntrustAdd(infContractDetailfile.getEntrustId());
                settleInfo.setmTradeAmt(etrEntrustAdd.getmTradeAmt());
            }
            // GNS-955:提前购回的到期结算金额应是提前购回指令的到期结算金额，
        }
        // modify by etliu 20220114 end
        trdManualSettleInfoMapper.insertSelective(settleInfo);
    }
    
    /**
	 * 计算平仓投资收益（平仓盈亏（盯市））
	 * 
	 * @param trdTempDto
	 * @return
	 */
	private BigDecimal calClosingInvestProfit(TrdTempDto trdTempDto) {
		TrdTempDto temp = new TrdTempDto();
		BeanUtil.copyPropertiesIgnoreNull(temp, trdTempDto);
		BigDecimal closingProfit = BigDecimal.ZERO;
		if (StringUtils.equals(temp.getPs(), DictCodeListExt3.交易方向_买入平仓.getValue())
				|| StringUtils.equals(temp.getPs(), DictCodeListExt3.交易方向_卖出平仓.getValue())) {
			if (StringUtils.equals(temp.getPs(), DictCodeListExt3.交易方向_买入平仓.getValue())) {
				temp.setPs(DictCodeListExt3.交易方向_卖出开仓.getValue());
			} else {
				temp.setPs(DictCodeListExt3.交易方向_买入开仓.getValue());
			}
		}
		// 查询当前开仓
		List<Map<String, Object>> curOpenPosition = entrustMsgbyMQMapper.getOpenClosePosition(temp);
		//交易平仓数量
		BigDecimal curAmount = toBigDecimal(trdTempDto.getShareAmt());
		//交易平仓价格
		BigDecimal curPrice = toBigDecimal(trdTempDto.getUnitCprice());
		// 前一日结算价
		BigDecimal preClosingPrice = findPreClosePrice(trdTempDto.getFinprodId().substring(4,trdTempDto.getFinprodId().length()));// 要接口取值
		// 合约乘数
		BigDecimal contractMultiplier = entrustMsgbyMQMapper.queryContractMultiplier(trdTempDto.getFinprodId());
		// 多空系数
		BigDecimal flag = new BigDecimal(1);
		if (StringUtils.equals(trdTempDto.getPs(), DictCodeListExt3.交易方向_买入平仓.getValue())) {
			flag = new BigDecimal(-1);
		}
		contractMultiplier = contractMultiplier.multiply(flag);
		if (null == curOpenPosition || curOpenPosition.size() == 0) {
			// 当前开仓不存在
			closingProfit = curPrice.subtract(preClosingPrice).multiply(curAmount).multiply(contractMultiplier);
		} else {
			PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
			ptlSecPositionFlowDto.setPortfolioId(trdTempDto.getPortfolioId());
			ptlSecPositionFlowDto.setSecAcctId(trdTempDto.getSecAcctId());
			ptlSecPositionFlowDto.setFinprodId(trdTempDto.getFinprodId());
			ptlSecPositionFlowDto.setPs(trdTempDto.getPs());
			ptlSecPositionFlowDto.setCdate(trdTempDto.getTradeDate());
			ptlSecPositionFlowDto.setSecAcctId(trdTempDto.getSecManageAcctId());
			ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
			if (StringUtils.equals(trdTempDto.getPs(), DictCodeListExt3.交易方向_卖出平仓.getValue())) {
				ptlSecPositionFlowDto.setInvAim(DictCodeListExt3.投资目的_多头.getValue());
			} else if (StringUtils.equals(trdTempDto.getPs(), DictCodeListExt3.交易方向_买入平仓.getValue())) {
				ptlSecPositionFlowDto.setInvAim(DictCodeListExt3.投资目的_空头.getValue());
			}
			basePtlSecPositionInfoService.queryOtherSecPosition(ptlSecPositionFlowDto);
			try {
				Calendar cd = Calendar.getInstance();
				cd.setTime(trdTempDto.getTradeDate());
				cd.add(Calendar.DATE, -1);// 减一天
				ptlSecPositionFlowDto.setCdate(cd.getTime());
			} catch (Exception e) {
				ptlSecPositionFlowDto.setCdate(null);
			}
			// 昨日持仓
			BigDecimal preDatePosition = basePtlSecPositionInfoService.queryOtherSecPosition(ptlSecPositionFlowDto);
			List<Map<String, Object>> curClosingPosition = entrustMsgbyMQMapper.getOpenClosePosition(trdTempDto);
			//if (null != curClosingPosition && curClosingPosition.size() > 0) {
				//当日交易总平仓量
				BigDecimal TotalPosition = BigDecimal.ZERO;
				if (null != curClosingPosition && curClosingPosition.size() > 0) {
					for (Map<String, Object> res : curClosingPosition) {
						BigDecimal shareAmt = toBigDecimal(res.get("SHAREAMT"));
						if (null != shareAmt) {
							TotalPosition = TotalPosition.add(shareAmt);
						}
					}
				}
				//昨仓剩余量
				BigDecimal lastNum = preDatePosition.subtract(TotalPosition);
				if (lastNum.compareTo(BigDecimal.ZERO) < 0) {
					lastNum = BigDecimal.ZERO;
				}
				if (curAmount.compareTo(lastNum) < 0) {
					closingProfit = curPrice.subtract(preClosingPrice).multiply(curAmount).multiply(contractMultiplier);
				} else {
					closingProfit = curPrice.subtract(preClosingPrice).multiply(lastNum).multiply(contractMultiplier);
					BigDecimal curClosingSum = lastNum;// 当笔平仓数量累计值
					BigDecimal curOpenSum = BigDecimal.ZERO;// 当日平仓当日开仓部分累计值
					BigDecimal curOpenTotal = TotalPosition.subtract(preDatePosition);// 当日平仓当日开仓部分总量
					if (curOpenTotal.compareTo(BigDecimal.ZERO) < 0) {
						curOpenTotal = BigDecimal.ZERO;
					}
					Boolean isValidTrade = false;
					/*
					 * 变量.当日平仓当日开仓部分累计值 += 当笔交易.开仓数量； if 变量.当日平仓当日开仓部分累计值 >
					 * 变量.当日平仓当日开仓部分总量 变量.当笔交易记入部分 = 当笔交易.开仓数量； if
					 * 变量.是否已找到第一笔有效交易 == false； 变量.当笔交易记入部分 = 变量.当日平仓当日开仓部分累计值
					 * - 变量.当日平仓当日开仓部分总量； endif if 变量.当笔平仓数量累计值 + 变量.当笔交易记入部分 <
					 * 当日平仓交易.平仓数量（C） 平仓投资收益 += (P-当笔交易.开仓价格
					 * )*变量.当笔交易记入部分*合约乘数*多空标志 else 平仓投资收益 += (P-当笔交易.开仓价格
					 * )*（当日平仓交易.平仓数量(C) - 变量.当笔平仓数量累计值）*合约乘数*多空标志 计算结束，返回结果
					 * endif 变量.当笔平仓数量累计值 += 变量.当笔交易记入部分 变量.是否已找到第一笔有效交易 = true；
					 * endif
					 */
					for (Map<String, Object> result : curOpenPosition) {
						BigDecimal shartAmt = toBigDecimal(result.get("SHAREAMT"));
						BigDecimal unitCprice = toBigDecimal(result.get("UNITCPRICE"));
						curOpenSum = curOpenSum.add(shartAmt);
						if (curOpenSum.compareTo(curOpenTotal) > 0) {
							BigDecimal curPart = shartAmt;// 当笔交易记入部分
							if (isValidTrade == false) {
								curPart = curOpenSum.subtract(curOpenTotal);
							}
							if (curClosingSum.add(curPart).compareTo(curAmount) < 0) {
								closingProfit = closingProfit.add((curPrice.subtract(unitCprice)).multiply(curPart).multiply(contractMultiplier));
							} else {
								closingProfit = closingProfit.add((curPrice.subtract(unitCprice)).multiply(curAmount.subtract(curClosingSum)).multiply(contractMultiplier));
								return closingProfit;
							}
							curClosingSum = curClosingSum.add(curPart);
							isValidTrade = true;
						}
					}
				}
			//}
		}
		return closingProfit;
	}
	
	/** 查询交易行情信息 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BigDecimal findPreClosePrice(String secId) {
		BigDecimal preClosePrice = BigDecimal.ZERO;
		String param = "?stock_code=" + secId;
		Map quoteMap = restTemplate.getForObject(ServiceApplication.QUOTE_SERVICE_NAME + "/joyin/quote/tick" + param, Map.class);
		//Map quoteMap = simpleRestTemplate.getForObject("http://192.168.90.105:8080/tams/" + "/joyin/quote/tick" + param, Map.class);
		if (quoteMap != null && quoteMap.get("data") != null) {
			Map<String, Object> quoteList = (Map<String, Object>) quoteMap.get("data");
			if (null != quoteList) {
				preClosePrice = toBigDecimal(quoteList.get("preClosePrice"));
			}
		}
		return preClosePrice;
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	public BigDecimal toBigDecimal(Object object) {
		if (null != object && "" != object) {
			return new BigDecimal(object.toString());
		}
		return null;
	}
}

