package com.joyintech.tams.cfets.trdconfirm.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.bankuplink.common.util.ProdTypeUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.BigDecimalUtil;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trd.repo.dto.CfetsRepoDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trdconfirm.dao.CfetsTrdConfirmMapper;
import com.joyintech.tams.cfets.trdconfirm.dto.*;
import com.joyintech.tams.common.base.entity.InterfCstpBjfxjg;
import com.joyintech.tams.common.base.entity.InterfCstpTrade;
import com.joyintech.tams.common.base.entity.InterfOpicsTradRepo;
import com.joyintech.tams.common.base.entity.InterfTradSecdeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 银行间交易自动匹配
 *
 * @author jiangpeng
 * @date 2022/9/29 10:24
 */
@Service("cfetsTrdConfirmAutoMatchService_dtx")
public class CfetsTrdConfirmAutoMatchService extends BaseService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CfetsTrdConfirm001Service cfetsTrdConfirm001Service;

    @Autowired
    private CfetsTrdConfirmService cfetsTrdConfirmService;

    @Autowired
    private MatchFeaturesService matchFeaturesService;

    @Autowired
    private CfetsTrdConfirmMapper cfetsTrdConfirmMapper;

    /**
     * CSTP成交回报-自动匹配 用于登记后调用自动匹配
     */
    public void cstpAutoMatchAfterReg(CfetsBondDealRegisterQuoteDto bondDealDbDto, CfetsRepoDealRegisterQuoteDto repoDealDbDto, CfetsTrdBondFirstSubInfoDto subInfoDto) {
        if (StringUtil.equals(CacheUtil.getSysParam("cstpAutoMatchAfterRegSwitch"), DictCodeList.是否_否.getValue())) {
            logger.info("cstpAutoMatchAfterRegSwitch:{}", CacheUtil.getSysParam("cstpAutoMatchAfterRegSwitch"));
            return;
        }
        logger.info("-----------银行间CSTP成交回报-自动匹配Start-------------");
        CfetsTrdConfirmMatchDto matchDto = new CfetsTrdConfirmMatchDto();
        //是否支持CSTP下行接口_现券买卖
        String supportCstpFlagBond = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BOND");
        logger.info("是否支持CSTP下行接口_现券买卖:{}", supportCstpFlagBond);

        //是否支持CSTP下行接口_质押式回购
        String supportCstpFlagRepo = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_REPO");
        //是否支持CSTP下行接口_质押式回购
        logger.info("是否支持CSTP下行接口_质押式回购:{}", supportCstpFlagRepo);

        //是否支持CSTP下行接口_一级申购
        String isSupportCstpFlagBondfirst = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BONDFIRST");
        logger.info("是否支持CSTP下行接口_一级申购:{}", isSupportCstpFlagBondfirst);

        if (StringUtil.isNotEmpty(bondDealDbDto)
                && StringUtil.equals(supportCstpFlagBond, DictCodeList.是否_是.getValue())) {
            logger.info("银行间CSTP成交回报-现券买卖登记数据:{}", JsonUtils.object2Json(bondDealDbDto));
            matchDto = this.cstpSecTradeAutoAfterReg(bondDealDbDto);
        } else if (StringUtil.isNotEmpty(repoDealDbDto)
                && StringUtil.equals(supportCstpFlagRepo, DictCodeList.是否_是.getValue())) {
            logger.info("银行间CSTP成交回报-质押式回购登记数据:{}", JsonUtils.object2Json(repoDealDbDto));
            matchDto = this.cstpRepoTradeAutoAfterReg(repoDealDbDto);
        } else if (StringUtil.isNotEmpty(subInfoDto)
                && StringUtil.equals(isSupportCstpFlagBondfirst, DictCodeList.是否_是.getValue())) {
            logger.info("银行间CSTP成交回报-一级申购登记数据:{}", JsonUtils.object2Json(subInfoDto));
            matchDto = this.cstpBidTradeAutoAfterReg(subInfoDto);
        }
        if (!StringUtil.isNotEmpty(matchDto)) {
            logger.info("-----------无可匹配的下行数据End-------------");
            return;
        }
        // 自动确认开关
        String cfetsIsAutoSure = CacheUtil.getSysParam("CFETS_IS_AUTO_SURE");
        logger.info("自动确认开关:{}", cfetsIsAutoSure);
        if (StringUtil.equals(cfetsIsAutoSure, DictCodeList.是否_是.getValue())) {
            logger.info("-----------自动匹配的报价数据成交确认-------------");
            //报价数据，会自动成交
            CfetsTrdConfirmRegInfoListResponse regInfo = new CfetsTrdConfirmRegInfoListResponse();
            regInfo.setDealType(matchDto.getDealType());
            regInfo.setTotleTradeId(matchDto.getPreTradeId());
            List<ValidMessage> messages = new ArrayList<>();
            matchDto.setTrdConfStatus(DictCodeList.确认状态_已确认.getValue());
            if (StringUtil.isNotEmpty(bondDealDbDto)) {
                //对dto进行封装
                CfetsTrdBondInvestTradeDto entity = cfetsTrdConfirmService.packageEquity(regInfo, DictCodeListExt1.生成方式_接口接入.getValue(), matchDto.getCstpTradeId());
                CfetsTrdConfirmMatchDto oldMatchDto = entity.getCfetsTrdConfirmMatchDto();
                matchDto.setTrdId(oldMatchDto.getTrdId());
                matchDto.setTrdConfStatus(oldMatchDto.getTrdConfStatus());
                entity.setCfetsTrdConfirmMatchDto(matchDto);
                entity.setOprType(DictCodeListExt3.操作类型_自动匹配确认.getValue());
                cfetsTrdConfirm001Service.doCompleteMethod(entity, OperationType.NEW);
            } else if (StringUtil.isNotEmpty(repoDealDbDto)) {
                //对dto进行封装
                CfetsTrdRepoTrade003Dto entity = cfetsTrdConfirmService.packageRepoEquity(regInfo, DictCodeListExt1.生成方式_接口接入.getValue(), matchDto.getCstpTradeId());
                CfetsTrdConfirmMatchDto oldMatchDto = entity.getCfetsTrdConfirmMatchDto();
                matchDto.setTrdId(oldMatchDto.getTrdId());
                matchDto.setTrdConfStatus(oldMatchDto.getTrdConfStatus());
                entity.setCfetsTrdConfirmMatchDto(matchDto);
                entity.setOprType(DictCodeListExt3.操作类型_自动匹配确认.getValue());
                //成交确认提交方法
                cfetsTrdConfirm001Service.doCompleteRepoMethod(entity, OperationType.NEW, false);
            } else if (StringUtil.isNotEmpty(subInfoDto)) {
                //对dto进行封装
                CfetsTrdFirstBondInvestTradeDto entity = cfetsTrdConfirmService.prepareBidEntity(regInfo.getTotleTradeId(), DictCodeListExt1.生成方式_接口接入.getValue(),
                        matchDto.getCstpTradeId());
                CfetsTrdConfirmMatchDto oldMatchDto = entity.getCfetsTrdConfirmMatchDto();
                matchDto.setTrdId(oldMatchDto.getTrdId());
                matchDto.setTrdConfStatus(oldMatchDto.getTrdConfStatus());
                entity.setCfetsTrdConfirmMatchDto(matchDto);
                entity.setOprType(DictCodeListExt3.操作类型_自动匹配确认.getValue());
                //成交确认提交方法
                cfetsTrdConfirm001Service.doCompleteBidMethod(entity, OperationType.NEW, DictCodeListExt1.生成方式_接口接入.getValue());
            }
        }
        logger.info("-----------银行间CSTP成交回报-自动匹配End-------------");
    }

    /**
     * 债券登记数据匹配下行数据
     *
     * @param bondDto 债券登记数据
     * @return com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdConfirmMatchDto
     * @author jiangpeng
     * @date 2022/9/29 14:05
     */
    public CfetsTrdConfirmMatchDto cstpSecTradeAutoAfterReg(CfetsBondDealRegisterQuoteDto bondDto) {
        CfetsTrdConfirmRegInfoListResponse confirmDto;
        // 自动匹配元素查询
        Map<String, MatchDetFeatures> map = matchFeaturesService.queryAutoMatchFeatures("BOND");
        Map<String, Object> param = new HashMap<>();
        if (map.get("tradeDate") != null && StringUtil.equals(map.get("tradeDate").getType(), "01")) {
            //成交日期
            param.put("tradeDate", bondDto.getTradeDate());
        }
        if (map.get("finprodMarketId") != null && StringUtil.equals(map.get("finprodMarketId").getType(), "01")) {
            //资产代码
            param.put("finprodMarketId", bondDto.getFinprodMarketId());
        }
        if (map.get("ps") != null && StringUtil.equals(map.get("ps").getType(), "01")) {
            //交易方向
            param.put("ps", bondDto.getPs());
        }
        if (map.get("settleSpeed") != null && StringUtil.equals(map.get("settleSpeed").getType(), "01")) {
            //清算速度
            if (StringUtil.equals(bondDto.getSettleSpeed(), "01")) {
                param.put("settleSpeed", "1");
            } else if (StringUtil.equals(bondDto.getSettleSpeed(), "02")) {
                param.put("settleSpeed", "2");
            } else if (StringUtil.equals(bondDto.getSettleSpeed(), "03")) {
                param.put("settleSpeed", "3");
            }
        }
        if (map.get("secManageAcctId") != null && StringUtil.equals(map.get("secManageAcctId").getType(), "01")) {
            //证券账户
            param.put("secManageAcctId", bondDto.getSecManageAcctId());
        }
        if (map.get("yield") != null && StringUtil.equals(map.get("yield").getType(), "01")) {
            //成交价
            param.put("yield", bondDto.getYield());
            param.put("price", bondDto.getPrice());
        }
        if (map.get("faceValue") != null && StringUtil.equals(map.get("faceValue").getType(), "01")) {
            // 券面总额
            param.put("faceValue", bondDto.getShareAmt());
        }
        if (map.get("settleDate") != null && StringUtil.equals(map.get("settleDate").getType(), "01")) {
            // 结算日期
            param.put("settleDate", DateUtil.format(bondDto.getSettleDate(), "yyyy-MM-dd"));
        }
        if (map.get("deliveryType") != null && StringUtil.equals(map.get("deliveryType").getType(), "01")) {
            // 结算方式
            if (StringUtil.equals(bondDto.getDeliveryType(), "03")) {
                param.put("deliveryType", "0");
            } else if (StringUtil.equals(bondDto.getDeliveryType(), "02")) {
                param.put("deliveryType", "5");
            } else if (StringUtil.equals(bondDto.getDeliveryType(), "01")) {
                param.put("deliveryType", "4");
            }
        }
        if (map.get("counterId") != null && StringUtil.equals(map.get("counterId").getType(), "01")) {
            // 对方手
            param.put("counterId", bondDto.getCounterId());
        }
        if (map.get("settleAtm") != null && StringUtil.equals(map.get("settleAtm").getType(), "01")) {
            // 结算金额
            param.put("settleAtm", bondDto.getFpriceAmt());
        }
        // 获取一级申购下行数据
        List<CfetsTrdConfirmRegInfoListResponse> confirmDtoList = cfetsTrdConfirmMapper.selectSecCSTPByPrimaryKey(param);
        if (StringUtil.isEmptyList(confirmDtoList)) {
            return null;
        }
        confirmDto = confirmDtoList.get(0);
        logger.info("-----------自动匹配查询到一级申购下行数据-------------");
        //插入匹配表
        CfetsTrdConfirmMatchDto match = new CfetsTrdConfirmMatchDto();
        match.setSeqno(IDGenerator.generate());
        match.setCstpTradeId(confirmDto.getExecid());
        match.setExeId(null);
        match.setPreTradeId(bondDto.getTotleTradeId());
        match.setMatchType(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue());
        match.setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
        match.setTrdConfStatus(DictCodeList.确认状态_未确认.getValue());
        match.setCreateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
        match.setCreateDept(CommonUtil.getDeptCode());
        match.setUpdateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
        match.setDealType(BussinessDictCodeList.交易类型_登记报价.getValue());
        return match;
    }


    /**
     * 回购登记数据匹配下行数据
     *
     * @param repoDto 回购登记数据
     * @return com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdConfirmMatchDto
     * @author jiangpeng
     * @date 2022/9/29 14:05
     */
    public CfetsTrdConfirmMatchDto cstpRepoTradeAutoAfterReg(CfetsRepoDealRegisterQuoteDto repoDto) {
        CfetsTrdConfirmRegInfoListResponse confirmDto;
        // 自动匹配元素查询
        Map<String, MatchDetFeatures> map = matchFeaturesService.queryAutoMatchFeatures("REPO");
        Map<String, Object> param = new HashMap<>();
        if (map.get("tradeDate") != null && StringUtil.equals(map.get("tradeDate").getType(), "01")) {
            param.put("tradeDate", repoDto.getTradeDate());
        }
        if (map.get("termDays") != null && StringUtil.equals(map.get("termDays").getType(), "01")) {
            param.put("termDays", repoDto.getRepoTerm());
        }
        if (map.get("ps") != null && StringUtil.equals(map.get("ps").getType(), "01")) {
            param.put("ps", repoDto.getPs());
        }
        if (map.get("settleSpeed") != null && StringUtil.equals(map.get("settleSpeed").getType(), "01")) {
            if (StringUtil.equals(repoDto.getSettleSpeed(), "01")) {
                param.put("settleSpeed", "1");
            } else if (StringUtil.equals(repoDto.getSettleSpeed(), "02")) {
                param.put("settleSpeed", "2");
            } else if (StringUtil.equals(repoDto.getSettleSpeed(), "03")) {
                param.put("settleSpeed", "3");
            }
        }
        if (map.get("secManageAcctId") != null && StringUtil.equals(map.get("secManageAcctId").getType(), "01")) {
            param.put("secManageAcctId", repoDto.getSecManageAcctId());
        }
        if (map.get("rate") != null && StringUtil.equals(map.get("rate").getType(), "01")) {
            param.put("rate", repoDto.getRate());
        }
        if (map.get("vsettleDate") != null && StringUtil.equals(map.get("vsettleDate").getType(), "01")) {
            param.put("vsettleDate", DateUtil.format(repoDto.getvSettleDate(), "yyyyMMdd"));
        }
        if (map.get("msettleDate") != null && StringUtil.equals(map.get("msettleDate").getType(), "01")) {
            param.put("msettleDate", DateUtil.format(repoDto.getmSettleDate(), "yyyyMMdd"));
        }
        if (map.get("tradeAmt") != null && StringUtil.equals(map.get("tradeAmt").getType(), "01")) {
            param.put("tradeAmt", repoDto.getTradeAmt());//首次结算金额
        }
        if (map.get("mTradeAmt") != null && StringUtil.equals(map.get("mTradeAmt").getType(), "01")) {
            param.put("mTradeAmt", repoDto.getmTradeAmt());
        }
        // 获取一级申购下行数据
        List<CfetsTrdConfirmRegInfoListResponse> confirmDtoList = cfetsTrdConfirmMapper.selectRepoCSTPByPrimaryKey(param);
        if (StringUtil.isEmptyList(confirmDtoList)) {
            return null;
        }
        confirmDto = confirmDtoList.get(0);
        logger.info("-----------自动匹配查询到一级申购下行数据-------------");
        //插入匹配表
        CfetsTrdConfirmMatchDto match = new CfetsTrdConfirmMatchDto();
        match.setSeqno(IDGenerator.generate());
        match.setCstpTradeId(confirmDto.getExecid());
        match.setExeId(null);
        match.setPreTradeId(repoDto.getTotleTradeId());
        match.setMatchType(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue());
        match.setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
        match.setTrdConfStatus(DictCodeList.确认状态_未确认.getValue());
        match.setCreateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
        match.setCreateDept(CommonUtil.getDeptCode());
        match.setUpdateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
        match.setDealType(BussinessDictCodeList.交易类型_登记报价.getValue());
        return match;
    }

    /**
     * 一级申购登记数据匹配下行数据
     *
     * @param subInfoDto 一级申购登记数据
     * @return com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdConfirmMatchDto
     * @author jiangpeng
     * @date 2022/9/29 14:05
     */
    public CfetsTrdConfirmMatchDto cstpBidTradeAutoAfterReg(CfetsTrdBondFirstSubInfoDto subInfoDto) {
        CfetsTrdConfirmRegInfoListResponse confirmDto;
        // 自动匹配元素查询
        Map<String, MatchDetFeatures> map = matchFeaturesService.queryAutoMatchFeatures("BID");
        Map<String, Object> param = new HashMap<>();
        if (map.get("tradeDate") != null && StringUtil.equals(map.get("tradeDate").getType(), "01")) {
            //成交日期
            param.put("tradeDate", DateUtil.format(subInfoDto.getTradeDate()));
        }
        if (map.get("finprodMarketId") != null && StringUtil.equals(map.get("finprodMarketId").getType(), "01")) {
            //资产代码
            param.put("finprodMarketId", StringUtil.substring(subInfoDto.getFinprodMarketId(), 0, subInfoDto.getFinprodMarketId().length() - 3));
        }
        if (map.get("ouPartyId") != null && StringUtil.equals(map.get("ouPartyId").getType(), "01")) {
            //本方
            param.put("ouPartyId", subInfoDto.getOuPartyId());
        }
        if (map.get("subscriptionAmount") != null && StringUtil.equals(map.get("subscriptionAmount").getType(), "01")) {
            //认购量[亿元(小数点后保留1位)]
            param.put("subscriptionAmount", ArithmeticUtil.divide(subInfoDto.getSubscriptionAmount(), new BigDecimal(100000000), 1));
        }
        if (map.get("issuePrice") != null && StringUtil.equals(map.get("issuePrice").getType(), "01")) {
            //发行价格[元(小数点后保留2位)]
            param.put("issuePrice", ArithmeticUtil.round(subInfoDto.getIssuePrice(), 2, RoundingMode.HALF_UP));
        }
        // 一级申购 可选要素：本方交易员、应缴款金额
        if (map.get("ourTrader") != null && StringUtil.equals(map.get("ourTrader").getType(), "01")) {
            param.put("ourTrader", subInfoDto.getOurTrader());//本方交易员
        }
        if (map.get("payableAmount") != null && StringUtil.equals(map.get("payableAmount").getType(), "01")) {
            //应缴款金额[元(小数点后保留2位)]
            param.put("payableAmount", ArithmeticUtil.round(subInfoDto.getPayableAmount(), 2, RoundingMode.HALF_UP));
        }
        // 获取一级申购下行数据
        List<CfetsTrdConfirmRegInfoListResponse> confirmDtoList = cfetsTrdConfirmMapper.selectBidCSTPByPrimaryKey(param);
        if (StringUtil.isEmptyList(confirmDtoList)) {
            return null;
        }
        confirmDto = confirmDtoList.get(0);
        logger.info("-----------自动匹配查询到一级申购下行数据-------------");
        //插入匹配表
        CfetsTrdConfirmMatchDto match = new CfetsTrdConfirmMatchDto();
        match.setSeqno(IDGenerator.generate());
        match.setCstpTradeId(confirmDto.getExecid());
        match.setExeId(null);
        match.setPreTradeId(subInfoDto.getBidId());
        match.setMatchType(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue());
        match.setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
        match.setTrdConfStatus(DictCodeList.确认状态_未确认.getValue());
        match.setCreateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
        match.setCreateDept(CommonUtil.getDeptCode());
        match.setUpdateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
        match.setDealType(BussinessDictCodeList.交易类型_登记报价.getValue());
        return match;
    }


    /**
     * CSTP成交回报-自动匹配
     */
    @Transactional(rollbackFor = Exception.class)
    @DTXTransaction
    public AjaxResult cstpAutoMatchMethod(InterfTradSecdeal interfTradSecdeal,
                                          InterfOpicsTradRepo interfOpicsTradRepo, InterfCstpBjfxjg interfCstpBjfxjg, InterfCstpTrade iCstpTrade) {
        logger.info("-----------银行间CSTP成交回报-自动匹配Start-------------");
        CfetsTrdConfirmMatchDto matchDto = new CfetsTrdConfirmMatchDto();
        //是否支持CSTP下行接口_现券买卖
        String supportCstpFlagBond = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BOND");
        logger.info("是否支持CSTP下行接口_现券买卖:{}", supportCstpFlagBond);
        //是否支持CSTP下行接口_质押式回购
        String supportCstpFlagRepo = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_REPO");
        //是否支持CSTP下行接口_质押式回购
        logger.info("是否支持CSTP下行接口_质押式回购:{}", supportCstpFlagRepo);
        String cfetsIsAutoSure = CacheUtil.getSysParam("CFETS_IS_AUTO_SURE");
        logger.info("自动确认开关:{}", cfetsIsAutoSure);
        //是否支持CSTP下行接口_一级申购
        String isSupportCstpFlagBondfirst = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BONDFIRST");
        logger.info("是否支持CSTP下行接口_一级申购:{}", isSupportCstpFlagBondfirst);
        if (StringUtil.isNotEmpty(interfTradSecdeal) && StringUtil.equals(supportCstpFlagBond, DictCodeList.是否_是.getValue())) {
            logger.info("银行间CSTP成交回报-现券买卖匹配报文:{}", JsonUtils.object2Json(interfTradSecdeal));
            matchDto = this.cstpSecTradeAutoMatch(interfTradSecdeal, iCstpTrade);
        } else if (StringUtil.isNotEmpty(interfOpicsTradRepo) && StringUtil.equals(supportCstpFlagRepo, DictCodeList.是否_是.getValue())) {
            logger.info("银行间CSTP成交回报-质押式回购匹配报文:{}", JsonUtils.object2Json(interfOpicsTradRepo));
            matchDto = this.cstpRepoTradeAutoMatch(interfOpicsTradRepo, iCstpTrade);
        } else if (StringUtil.isNotEmpty(interfCstpBjfxjg) && StringUtil.equals(isSupportCstpFlagBondfirst, DictCodeList.是否_是.getValue())) {
            logger.info("银行间CSTP成交回报-一级申购匹配报文:{}", JsonUtils.object2Json(interfCstpBjfxjg));
            matchDto = this.cstpBidTradeAutoMatch(interfCstpBjfxjg);
        }
        if (!StringUtil.isNotEmpty(matchDto)) {
            logger.info("-----------无可匹配的交易End-------------");
            return new AjaxResult();
        }
        if (StringUtil.isNotEmpty(matchDto.getDealType()) && StringUtil.equals(matchDto.getDealType(), "999999")
                && !StringUtil.equals(cfetsIsAutoSure, DictCodeList.是否_是.getValue())) {
            //针对下行回来数据，上行或登记数据已经进行成交确认，此时更新匹配关系表
            logger.info("-----------已存在数据更新匹配关系表-------------");
            cfetsTrdConfirmMapper.updateTrdQutoMatchInfo(matchDto);
        } else if (StringUtil.isNotEmpty(matchDto.getDealType())
                && StringUtil.equals(matchDto.getDealType(), BussinessDictCodeList.交易类型_登记报价.getValue())
                && !StringUtil.equals(cfetsIsAutoSure, DictCodeList.是否_是.getValue())) {
            //针对登记数据，仅仅匹配
            logger.info("-----------自动匹配的登记数据成交确认-------------");
            cfetsTrdConfirmMapper.insertTrdQutoMatchInfo(matchDto);
        } else {
            logger.info("-----------自动匹配的报价数据成交确认-------------");
            //报价数据，会自动成交
            CfetsTrdConfirmRegInfoListResponse regInfo = new CfetsTrdConfirmRegInfoListResponse();
            regInfo.setDealType(matchDto.getDealType());
            regInfo.setTotleTradeId(matchDto.getPreTradeId());
            List<ValidMessage> messages = new ArrayList<>();
            try {
                if (StringUtil.isNotEmpty(interfTradSecdeal)) {
                    //对dto进行封装
                    CfetsTrdBondInvestTradeDto entity = cfetsTrdConfirmService.packageEquity(regInfo, DictCodeListExt1.生成方式_接口接入.getValue(),
                            interfTradSecdeal.getDealno());
                    CfetsTrdConfirmMatchDto oldMatchDto = entity.getCfetsTrdConfirmMatchDto();
                    matchDto.setTrdId(oldMatchDto.getTrdId());
                    matchDto.setTrdConfStatus(oldMatchDto.getTrdConfStatus());
                    entity.setCfetsTrdConfirmMatchDto(matchDto);
                    entity.setOprType(DictCodeListExt3.操作类型_自动匹配确认.getValue());
                    cfetsTrdConfirm001Service.doCompleteMethod(entity, OperationType.NEW);
                } else if (StringUtil.isNotEmpty(interfOpicsTradRepo)) {
                    //对dto进行封装
                    CfetsTrdRepoTrade003Dto entity = cfetsTrdConfirmService.packageRepoEquity(regInfo, DictCodeListExt1.生成方式_接口接入.getValue(),
                            interfOpicsTradRepo.getTradeid());
                    CfetsTrdConfirmMatchDto oldMatchDto = entity.getCfetsTrdConfirmMatchDto();
                    matchDto.setTrdId(oldMatchDto.getTrdId());
                    matchDto.setTrdConfStatus(oldMatchDto.getTrdConfStatus());
                    entity.setCfetsTrdConfirmMatchDto(matchDto);
                    entity.setOprType(DictCodeListExt3.操作类型_自动匹配确认.getValue());
                    //成交确认提交方法
                    cfetsTrdConfirm001Service.doCompleteRepoMethod(entity, OperationType.NEW, false);
                } else if (StringUtil.isNotEmpty(regInfo.getTotleTradeId())) {
                    //对dto进行封装
                    CfetsTrdFirstBondInvestTradeDto entity = cfetsTrdConfirmService.prepareBidEntity(regInfo.getTotleTradeId(), DictCodeListExt1.生成方式_接口接入.getValue(),
                            interfCstpBjfxjg.getTrdId());
                    CfetsTrdConfirmMatchDto oldMatchDto = entity.getCfetsTrdConfirmMatchDto();
                    matchDto.setTrdId(oldMatchDto.getTrdId());
                    matchDto.setTrdConfStatus(oldMatchDto.getTrdConfStatus());
                    entity.setCfetsTrdConfirmMatchDto(matchDto);
                    entity.setOprType(DictCodeListExt3.操作类型_自动匹配确认.getValue());
                    //成交确认提交方法
                    cfetsTrdConfirm001Service.doCompleteBidMethod(entity, OperationType.NEW, DictCodeListExt1.生成方式_接口接入.getValue());
                }
            } catch (Exception e) {
                //若插入数据异常，提示错误信息
                Message ms = PositionUtil.ajaxErrorReturn(e);
                if (null != ms && StringUtil.isNotEmpty(ms.getMessage())) {
                    messages.add(new ValidMessage(getMessage("CONFIRM_MSG_009", regInfo.getTotleTradeId(), DictCodeListExt3.操作类型_自动匹配确认.getCodeName(), ms.getMessage())));
                } else {
                    messages.add(new ValidMessage(getMessage("CONFIRM_MSG_008", regInfo.getTotleTradeId(), DictCodeListExt3.操作类型_自动匹配确认.getCodeName())));
                }
                logger.error(e.getMessage(), e);
            }
            if (!StringUtil.isEmptyList(messages) && messages.size() > 0) {
                AjaxResult res = new AjaxResult();
                res.setErrorList(messages);
                return res;
            }
        }
        logger.info("-----------银行间CSTP成交回报-自动匹配End-------------");
        return new AjaxResult();
    }

    /**
     * 债券自动匹配
     *
     * @param interfTradSecdeal 下行数据
     * @param iCstpTrade        下行数据
     * @return com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdConfirmMatchDto
     */
    private CfetsTrdConfirmMatchDto cstpSecTradeAutoMatch(InterfTradSecdeal interfTradSecdeal, InterfCstpTrade iCstpTrade) {
        if (!StringUtil.isNotEmpty(interfTradSecdeal.getYield8()) || !StringUtil.isNotEmpty(interfTradSecdeal.getPrinamt())
                || !StringUtil.isNotEmpty(interfTradSecdeal.getSettdate()) || !StringUtil.isNotEmpty(interfTradSecdeal.getDealdate())
        ) {
            return null;
        }
        CfetsTrdConfirmRegInfoListResponse confirmDto = new CfetsTrdConfirmRegInfoListResponse();
        String dealType = "999999";
        boolean isContinue = true, isUpdate = false, isInsert = false;
        logger.info(" 1.CSTP下行成交回报数据的成交编号匹配已生成的银行间交易数据中的成交编号,根据cstp下行成交回报的成交编号，查询匹配关系 ");
        //1.CSTP下行成交回报数据的成交编号匹配已生成的银行间交易数据中的成交编号,根据cstp下行成交回报的成交编号，查询匹配关系
        CfetsTrdConfirmMatchDto matchDto = cfetsTrdConfirmMapper.selectMatchDtoByExecId(interfTradSecdeal.getFedealno());
        if (null != matchDto && StringUtil.isNotEmpty(matchDto.getPreTradeId())) {
            Map<String, Object> param = new HashMap<>();
            param.put("totleTradeId", matchDto.getPreTradeId());
            param.put("finprodType2", DictCodeListExt1.金融产品类型_债券.getValue());
            //根据登记编号，查询登记信息
            confirmDto = cfetsTrdConfirmMapper.selectRegInfoByKey(matchDto.getPreTradeId());
            if (null != confirmDto) {
                isContinue = false;
                isUpdate = true;
                dealType = "999999";
                logger.info("-----------自动匹配查询到已成交的数据-------------");
            }
        }
        //2.CSTP下行成交回报数据中的报价编号匹配报价数据的业务编号（报价编号）
        if (isContinue && null != iCstpTrade && StringUtil.isNotEmpty(iCstpTrade.getQuoteid())) {
            //根据报价编号，查询报价信息
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("cfetsQuoteId", iCstpTrade.getQuoteid());
            confirmDto = cfetsTrdConfirmMapper.selectIbuInfoByKey(pageParam);
            if (null != confirmDto && StringUtil.isNotEmpty(confirmDto.getQuoteType())) {
                isContinue = false;
                isInsert = true;
                dealType = confirmDto.getQuoteType();//报价
                logger.info("-----------自动匹配查询到报价数据-------------");
            }
        }
        Map<String, MatchDetFeatures> map = matchFeaturesService.queryAutoMatchFeatures("BOND");
        //3.CSTP下行成交回报数据再与已生成的银行间交易品种（默认债券买卖和质押式回购首期）交易登记数据进行匹配,查询所有的符合条件登记信息
        if (isContinue && null != iCstpTrade) {
            Map<String, Object> param = new HashMap<>();
            param.put("finprodType2", DictCodeListExt1.金融产品类型_债券.getValue());
            if (map.get("tradeDate") != null && StringUtil.equals(map.get("tradeDate").getType(), "01")) {
                //成交日期
                param.put("tradeDate", DateUtil.format(interfTradSecdeal.getDealdate(), "yyyy-MM-dd"));
            }
            if (map.get("finprodMarketId") != null && StringUtil.equals(map.get("finprodMarketId").getType(), "01")) {
                //资产代码
                param.put("finprodMarketId", interfTradSecdeal.getSecid());
            }
            if (map.get("ps") != null && StringUtil.equals(map.get("ps").getType(), "01")) {
                //交易方向
                param.put("ps", interfTradSecdeal.getPs());
            }
            if (map.get("settleSpeed") != null && StringUtil.equals(map.get("settleSpeed").getType(), "01")) {
                //清算速度
                if (StringUtil.equals(interfTradSecdeal.getSettleSpeed(), "1")) {
                    param.put("settleSpeed", "01");
                } else if (StringUtil.equals(interfTradSecdeal.getSettleSpeed(), "2")) {
                    param.put("settleSpeed", "02");
                } else if (StringUtil.equals(interfTradSecdeal.getSettleSpeed(), "3")) {
                    param.put("settleSpeed", "03");
                }
            }
            if (map.get("secManageAcctId") != null && StringUtil.equals(map.get("secManageAcctId").getType(), "01")) {
                //证券账户
                param.put("secManageAcctId", interfTradSecdeal.getOurdepositaccountnumber());
            }
            if (map.get("yield") != null && StringUtil.equals(map.get("yield").getType(), "01")) {
                //成交价
                param.put("yield", interfTradSecdeal.getYield8());
                param.put("price", interfTradSecdeal.getPrice8());
            }
            if (map.get("faceValue") != null && StringUtil.equals(map.get("faceValue").getType(), "01")) {
                // 券面总额
                param.put("faceValue", interfTradSecdeal.getPrinamt());
            }
            if (map.get("settleDate") != null && StringUtil.equals(map.get("settleDate").getType(), "01")) {
                // 结算日期
                param.put("settleDate", DateUtil.format(interfTradSecdeal.getSettdate()));
            }
            if (map.get("counterId") != null && StringUtil.equals(map.get("counterId").getType(), "01")) {
                // 结算日期
                param.put("counterId", interfTradSecdeal.getCno());
            }
            if (map.get("deliveryType") != null && StringUtil.equals(map.get("deliveryType").getType(), "01")) {
                // 结算方式
                if (StringUtil.equals(iCstpTrade.getDeliverytype(), "0")) {
                    param.put("deliveryType", "03");
                } else if (StringUtil.equals(iCstpTrade.getDeliverytype(), "5")) {
                    param.put("deliveryType", "02");
                } else if (StringUtil.equals(iCstpTrade.getDeliverytype(), "4")) {
                    param.put("deliveryType", "01");
                }
            }
            //根据登记编号，查询登记信息
            List<CfetsTrdConfirmRegInfoListResponse> confirmDtoList = cfetsTrdConfirmMapper.selectRegDtoByPrimaryKey(param);
            if (!StringUtil.isEmptyList(confirmDtoList)) {
                confirmDto = confirmDtoList.get(0);
                isInsert = true;
                dealType = BussinessDictCodeList.交易类型_登记报价.getValue();
                logger.info("-----------自动匹配查询到登记数据-------------");
            }
        }
        //将CSTP数据和已有的登记或报价数据进行比较
        //债券
        if (!StringUtil.isNotEmpty(confirmDto) || !StringUtil.isNotEmpty(confirmDto.getTradeYield())
                || !StringUtil.isNotEmpty(confirmDto.getFaceValue())
                || !StringUtil.isNotEmpty(confirmDto.getSettleDate())
                || !StringUtil.isNotEmpty(confirmDto.getTradeDate())
        ) {
            return null;
        }
        //对下行数据进行处理
        if (StringUtil.equals(iCstpTrade.getDeliverytype(), "0")) {
            iCstpTrade.setDeliverytype("03");
        } else if (StringUtil.equals(iCstpTrade.getDeliverytype(), "5")) {
            iCstpTrade.setDeliverytype("02");
        } else if (StringUtil.equals(iCstpTrade.getDeliverytype(), "4")) {
            iCstpTrade.setDeliverytype("01");
        }
        if (StringUtil.equals(interfTradSecdeal.getSettleSpeed(), "1")) {
            interfTradSecdeal.setSettleSpeed("01");
        } else if (StringUtil.equals(interfTradSecdeal.getSettleSpeed(), "2")) {
            interfTradSecdeal.setSettleSpeed("02");
        } else if (StringUtil.equals(interfTradSecdeal.getSettleSpeed(), "3")) {
            interfTradSecdeal.setSettleSpeed("03");
        }
        //对上行数据进行处理
        if (StringUtil.equals(confirmDto.getSettleSpeed(), "0")) {
            confirmDto.setSettleSpeed("01");
        } else if (StringUtil.equals(confirmDto.getSettleSpeed(), "1")) {
            confirmDto.setSettleSpeed("02");
        } else if (StringUtil.equals(confirmDto.getSettleSpeed(), "2")) {
            confirmDto.setSettleSpeed("03");
        }

        if (StringUtil.equals(dealType, BussinessDictCodeList.交易类型_登记报价.getValue())
                || (confirmDto.getTradeDate().compareTo(interfTradSecdeal.getDealdate()) == 0
                && StringUtil.equals(confirmDto.getFinprodMarketId(), interfTradSecdeal.getSecid())
                && StringUtil.equals(confirmDto.getPs(), interfTradSecdeal.getPs())
                && StringUtil.equals(confirmDto.getSettleSpeed(), interfTradSecdeal.getSettleSpeed())
                && StringUtil.equals(confirmDto.getSecManageAcctId(), interfTradSecdeal.getOurdepositaccountnumber())
                && (BigDecimalUtil.ACompareToB(confirmDto.getYield(), interfTradSecdeal.getYield8(), 4) == 0 ||
                BigDecimalUtil.ACompareToB(confirmDto.getUnitCprice(), interfTradSecdeal.getPrice8(), 4) == 0)
                && BigDecimalUtil.ACompareToB(confirmDto.getFaceValue(), interfTradSecdeal.getPrinamt(), 2) == 0
                && confirmDto.getSettleDate().compareTo(interfTradSecdeal.getSettdate()) == 0
                && StringUtil.equals(confirmDto.getDeliveryType(), iCstpTrade.getDeliverytype()))
        ) {
            if (isUpdate) {
                // 更新
                CfetsTrdConfirmMatchDto match = new CfetsTrdConfirmMatchDto();
                match.setPreTradeId(confirmDto.getTotleTradeId());
                match.setMatchType(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue());
                match.setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
                match.setUpdateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
                match.setDealType("999999");
                return match;
            } else if (isInsert) {
                //插入匹配表
                CfetsTrdConfirmMatchDto match = new CfetsTrdConfirmMatchDto();
                match.setSeqno(IDGenerator.generate());
                match.setCstpTradeId(interfTradSecdeal.getDealno());
                match.setExeId(interfTradSecdeal.getFedealno());
                match.setPreTradeId(confirmDto.getTotleTradeId());
                match.setMatchType(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue());
                match.setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
                match.setTrdConfStatus(DictCodeList.确认状态_未确认.getValue());
                match.setCreateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
                match.setCreateDept(CommonUtil.getDeptCode());
                match.setUpdateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
                match.setDealType(dealType);
                return match;
            }
        }
        return null;
    }

    /**
     * <p>一级申购自动匹配
     * <p>匹配要素：
     * <p>一级申购
     * <p>基础要素：成交日期、资产代码、交易场所、本方、认购量、成交价
     * <p>可选要素：本方交易员、应缴款金额
     * <p>当存在多条可匹配数据时，按时间优先的方案匹配
     *
     * @param interfCstpBjfxjg 下行数据
     * @return com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdConfirmMatchDto
     * @author jiangpeng
     * @date 2022/5/7 9:59
     */
    private CfetsTrdConfirmMatchDto cstpBidTradeAutoMatch(InterfCstpBjfxjg interfCstpBjfxjg) {
        // 下行数据需要匹配的字段为空,直接返回空值
        if (!StringUtil.isNotEmpty(interfCstpBjfxjg.getSubscriptionTime())
                || !StringUtil.isNotEmpty(interfCstpBjfxjg.getInstrumentId())
                || !StringUtil.isNotEmpty(interfCstpBjfxjg.getSubscribeInstitutionId())
                || !StringUtil.isNotEmpty(interfCstpBjfxjg.getSubscriptionAmount())
        ) {
            return null;
        }
        CfetsTrdConfirmRegInfoListResponse confirmDto;
        String dealType = "999999";
        // 一级申购下行报文没有成交编号,不存在该场景
        //1.CSTP下行成交回报数据的成交编号匹配已生成的银行间交易数据中的成交编号,根据cstp下行成交回报的成交编号，查询匹配关系
        // 一级申购没有上行报文,不存在该场景
        //2.CSTP下行成交回报数据中的报价编号匹配报价数据的业务编号（报价编号）
        //3.CSTP下行成交回报数据再与已生成的银行间交易品种（一级申购）交易登记数据进行匹配,查询所有的符合条件登记信息
        Map<String, MatchDetFeatures> map = matchFeaturesService.queryAutoMatchFeatures("BID");
        Map<String, Object> param = new HashMap<>();
        param.put("finprodType2", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        if (map.get("tradeDate") != null && StringUtil.equals(map.get("tradeDate").getType(), "01")) {
            //成交日期
            param.put("tradeDate", DateUtil.format(DateUtil.parse(interfCstpBjfxjg.getSubscriptionTime()), "yyyy-MM-dd"));
        }
        if (map.get("finprodMarketId") != null && StringUtil.equals(map.get("finprodMarketId").getType(), "01")) {
            //资产代码
            param.put("finprodMarketId", interfCstpBjfxjg.getInstrumentId());
        }
        if (map.get("ouPartyId") != null && StringUtil.equals(map.get("ouPartyId").getType(), "01")) {
            //本方
            param.put("ouPartyId", interfCstpBjfxjg.getSubscribeInstitutionId());
        }
        if (map.get("subscriptionAmount") != null && StringUtil.equals(map.get("subscriptionAmount").getType(), "01")) {
            //认购量
            param.put("subscriptionAmount", interfCstpBjfxjg.getSubscriptionAmount());
        }
        if (map.get("issuePrice") != null && StringUtil.equals(map.get("issuePrice").getType(), "01")) {
            // 成交价
//                param.put("couponRate", interfCstpBjfxjg.getCouponRate());//票面利率(%)
//                param.put("spread", interfCstpBjfxjg.getSpread());//利差(BP)
            param.put("issuePrice", interfCstpBjfxjg.getIssuePrice());//发行价格(元)
        }
        if (map.get("ourTrader") != null && StringUtil.equals(map.get("ourTrader").getType(), "01")) {
            //本方交易员
            param.put("ourTrader", interfCstpBjfxjg.getSubmitUser());
        }
        if (map.get("payableAmount") != null && StringUtil.equals(map.get("payableAmount").getType(), "01")) {
            //应缴款金额
            param.put("payableAmount", interfCstpBjfxjg.getActualFee());
        }
        //根据登记编号，查询登记信息
        List<CfetsTrdConfirmRegInfoListResponse> confirmDtoList = cfetsTrdConfirmMapper.selectBidDtoByPrimaryKey(param);
        if (StringUtil.isEmptyList(confirmDtoList)) {
            return null;
        }
        confirmDto = confirmDtoList.get(0);
        dealType = BussinessDictCodeList.交易类型_登记报价.getValue();
        logger.info("-----------自动匹配查询到一级申购登记数据-------------");
        //将CSTP数据和已有的登记或报价数据进行比较

        //插入匹配表
        CfetsTrdConfirmMatchDto match = new CfetsTrdConfirmMatchDto();
        match.setSeqno(IDGenerator.generate());
        match.setCstpTradeId(interfCstpBjfxjg.getTrdId());
        match.setExeId(null);
        match.setPreTradeId(confirmDto.getTotleTradeId());
        match.setMatchType(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue());
        match.setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
        match.setTrdConfStatus(DictCodeList.确认状态_未确认.getValue());
        match.setCreateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
        match.setCreateDept(CommonUtil.getDeptCode());
        match.setUpdateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
        match.setDealType(dealType);
        return match;
    }

    /**
     * 回购自动匹配
     *
     * @param interfOpicsTradRepo 下行数据
     * @param iCstpTrade          下行数据
     * @return com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdConfirmMatchDto
     */
    private CfetsTrdConfirmMatchDto cstpRepoTradeAutoMatch(InterfOpicsTradRepo interfOpicsTradRepo, InterfCstpTrade iCstpTrade) {
        if (!StringUtil.isNotEmpty(interfOpicsTradRepo.getDealdate()) || !StringUtil.isNotEmpty(iCstpTrade.getPrice())
                || !StringUtil.isNotEmpty(iCstpTrade.getTradecashamt()) || !StringUtil.isNotEmpty(iCstpTrade.getSettldate())
                || !StringUtil.isNotEmpty(iCstpTrade.getSettldate2()) || !StringUtil.isNotEmpty(interfOpicsTradRepo.getTerm())
        ) {
            return null;
        }
        CfetsTrdConfirmRegInfoListResponse confirmDto = new CfetsTrdConfirmRegInfoListResponse();
        String dealType = "999999";
        boolean isContinue = true;
        boolean isUpdate = false;
        boolean isInsert = false;
        //1.CSTP下行成交回报数据的成交编号匹配已生成的银行间交易数据中的成交编号,根据cstp下行成交回报的成交编号，查询匹配关系
        logger.info("根据cstp下行成交回报的成交编号，查询匹配关系");
        CfetsTrdConfirmMatchDto matchDto = cfetsTrdConfirmMapper.selectMatchDtoByExecId(interfOpicsTradRepo.getOritradeid());
        if (null != matchDto && StringUtil.isNotEmpty(matchDto.getPreTradeId())) {
            //根据登记编号，查询登记信息
            confirmDto = cfetsTrdConfirmMapper.selectRegInfoByKey(matchDto.getPreTradeId());
            if (null != confirmDto) {
                isContinue = false;
                isUpdate = true;
                dealType = "999999";
            }
        }
        Map<String, MatchDetFeatures> map = matchFeaturesService.queryAutoMatchFeatures("REPO");
        //2.CSTP下行成交回报数据中的报价编号匹配报价数据的业务编号（报价编号）
        if (isContinue && StringUtil.isNotEmpty(iCstpTrade.getQuoteid())) {
            logger.info("CSTP下行成交回报数据中的报价编号匹配报价数据的业务编号");
            //根据报价编号，查询报价信息
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("cfetsQuoteId", iCstpTrade.getQuoteid());
            confirmDto = cfetsTrdConfirmMapper.selectRepoIbuInfoByKey(pageParam);
            if (null != confirmDto && StringUtil.isNotEmpty(confirmDto.getQuoteType())) {
                //比较抵押品
                //查询cstp数据的质押券信息
                List<CfetsTrdConfirmCstpInfoListResponse> cstpPledges = cfetsTrdConfirmMapper.findAllCstpPledgesList(iCstpTrade.getExecid());
                //查询登记或报价的质押券信息
                List<CfetsTrdConfirmRegInfoListResponse> regAndIbuPledges = cfetsTrdConfirmMapper.findAllRegAndIbuPledgesList(confirmDto.getTotleTradeId());
                if (!StringUtil.isEmptyList(regAndIbuPledges) && !StringUtil.isEmptyList(cstpPledges)) {
                    for (int i = 0; i < regAndIbuPledges.size(); i++) {
                        CfetsTrdConfirmRegInfoListResponse regAndIbuDemo = regAndIbuPledges.get(i);
                        CfetsTrdConfirmCstpInfoListResponse cstpDemo = cstpPledges.get(i);
                        if (!StringUtil.equals(regAndIbuDemo.getPledgeFinprodId(), cstpDemo.getPledgeFinprodId())) {
                            return null;
                        }
                        if (StringUtil.isNotEmpty(regAndIbuDemo.getPledgeRatio()) && StringUtil.isNotEmpty(cstpDemo.getPledgeRatio())
                                && regAndIbuDemo.getPledgeRatio().compareTo(cstpDemo.getPledgeRatio()) != 0) {
                            return null;
                        }
                        if (StringUtil.isNotEmpty(regAndIbuDemo.getPledgeFaceValue()) && StringUtil.isNotEmpty(cstpDemo.getPledgeFaceValue())
                                && regAndIbuDemo.getPledgeFaceValue().compareTo(cstpDemo.getPledgeFaceValue()) != 0) {
                            return null;
                        }
                    }
                    isContinue = false;
                    isInsert = true;
                    dealType = confirmDto.getQuoteType();//报价
                }
            }
        }
        //3.CSTP下行成交回报数据再与已生成的银行间交易品种（默认债券买卖和质押式回购首期）交易登记数据进行匹配,查询所有的符合条件登记信息
        if (isContinue) {
            logger.info("查询所有的符合条件登记信息");
            Map<String, Object> param = new HashMap<>();
            if (StringUtil.equals(interfOpicsTradRepo.getProdtype(), ProdTypeUtil.PRODTYPE_PLEDGE)
                    && StringUtil.equals(interfOpicsTradRepo.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
                param.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
            } else if (StringUtil.equals(interfOpicsTradRepo.getProdtype(), ProdTypeUtil.PRODTYPE_PLEDGE)
                    && StringUtil.equals(interfOpicsTradRepo.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
                param.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            }
            if (map.get("tradeDate") != null && StringUtil.equals(map.get("tradeDate").getType(), "01")) {
                param.put("tradeDate", DateUtil.format(interfOpicsTradRepo.getDealdate(), "yyyy-MM-dd"));
            }
            if (map.get("termDays") != null && StringUtil.equals(map.get("termDays").getType(), "01")) {
                param.put("termDays", interfOpicsTradRepo.getTerm());
            }
            if (map.get("ps") != null && StringUtil.equals(map.get("ps").getType(), "01")) {
                param.put("ps", interfOpicsTradRepo.getPs());
            }
            if (map.get("settleSpeed") != null && StringUtil.equals(map.get("settleSpeed").getType(), "01")) {
                if (StringUtil.equals(interfOpicsTradRepo.getSettleSpeed(), "1")) {
                    param.put("settleSpeed", "01");
                } else if (StringUtil.equals(interfOpicsTradRepo.getSettleSpeed(), "2")) {
                    param.put("settleSpeed", "02");
                } else if (StringUtil.equals(interfOpicsTradRepo.getSettleSpeed(), "3")) {
                    param.put("settleSpeed", "03");
                }
            }
            if (map.get("secManageAcctId") != null && StringUtil.equals(map.get("secManageAcctId").getType(), "01")) {
                param.put("secManageAcctId", interfOpicsTradRepo.getOurdepositaccountnumber());
            }
            if (map.get("rate") != null && StringUtil.equals(map.get("rate").getType(), "01")) {
                param.put("rate", iCstpTrade.getPrice());
            }
            if (map.get("vsettleDate") != null && StringUtil.equals(map.get("vsettleDate").getType(), "01")) {
                param.put("vsettleDate", iCstpTrade.getSettldate());
            }
            if (map.get("msettleDate") != null && StringUtil.equals(map.get("msettleDate").getType(), "01")) {
                param.put("msettleDate", iCstpTrade.getSettldate2());
            }
            if (map.get("tradeAmt") != null && StringUtil.equals(map.get("tradeAmt").getType(), "01")) {
                param.put("tradeAmt", iCstpTrade.getTradecashamt());//首次结算金额
            }
            if (map.get("mTradeAmt") != null && StringUtil.equals(map.get("mTradeAmt").getType(), "01")) {
                param.put("mTradeAmt", iCstpTrade.getSettlcurramt2());
            }
            //根据登记编号，查询登记信息
            logger.info("根据登记编号，查询登记信息");
            List<CfetsTrdConfirmRegInfoListResponse> confirmDtoList = cfetsTrdConfirmMapper.selectRegDtoByPrimaryKey(param);
            logger.info("查询到登记数据:{}", JsonUtils.object2Json(confirmDtoList));
            if (!StringUtil.isEmptyList(confirmDtoList)) {
                //查询cstp数据的质押券信息
                List<CfetsTrdConfirmCstpInfoListResponse>
                        cstpPledges = cfetsTrdConfirmMapper.findAllCstpPledgesList(iCstpTrade.getExecid());
                for (CfetsTrdConfirmRegInfoListResponse item : confirmDtoList) {
                    //查询登记或报价的质押券信息
                    List<CfetsTrdConfirmRegInfoListResponse>
                            regAndIbuPledges = cfetsTrdConfirmMapper.findAllRegAndIbuPledgesList(item.getTotleTradeId());
                    boolean isMatch = true;
                    if (StringUtil.isEmptyList(regAndIbuPledges) || StringUtil.isEmptyList(cstpPledges)) {
                        continue;
                    }
                    if (!StringUtil.isEmptyList(regAndIbuPledges) && !StringUtil.isEmptyList(cstpPledges)) {
                        for (int i = 0; i < regAndIbuPledges.size(); i++) {
                            CfetsTrdConfirmRegInfoListResponse regAndIbuDemo = regAndIbuPledges.get(i);
                            CfetsTrdConfirmCstpInfoListResponse cstpDemo = cstpPledges.get(i);
                            if (!StringUtil.equals(regAndIbuDemo.getPledgeFinprodId(), cstpDemo.getPledgeFinprodId())
                                    && map.get("pledgeFinprodId") != null && StringUtil.equals(map.get("pledgeFinprodId").getType(), "01")) {
                                isMatch = false;
                                break;
                            }
                            if (StringUtil.isNotEmpty(regAndIbuDemo.getPledgeRatio()) && StringUtil.isNotEmpty(cstpDemo.getPledgeRatio())
                                    && regAndIbuDemo.getPledgeRatio().compareTo(cstpDemo.getPledgeRatio()) != 0
                                    && map.get("pledgeRatio") != null && StringUtil.equals(map.get("pledgeRatio").getType(), "01")) {
                                isMatch = false;
                                break;
                            }
                            if (StringUtil.isNotEmpty(regAndIbuDemo.getPledgeFaceValue()) && StringUtil.isNotEmpty(cstpDemo.getPledgeFaceValue())
                                    && regAndIbuDemo.getPledgeFaceValue().compareTo(cstpDemo.getPledgeFaceValue()) != 0
                                    && map.get("pledgeFaceValue") != null && StringUtil.equals(map.get("pledgeFaceValue").getType(), "01")) {
                                isMatch = false;
                                break;
                            }
                        }
                    }
                    if (isMatch) {
                        confirmDto = item;
                        isInsert = true;
                        dealType = BussinessDictCodeList.交易类型_登记报价.getValue();
                        break;
                    } else {
                        logger.info("质押券未匹配到数据");
                    }
                }
            }
        }
        //将CSTP数据和已有的登记或报价数据进行比较
        //回购
        if (null == confirmDto || !StringUtil.isNotEmpty(confirmDto.getRate())
                || !StringUtil.isNotEmpty(confirmDto.getTermDays())
                || !StringUtil.isNotEmpty(confirmDto.getTradeAmt())
                || !StringUtil.isNotEmpty(confirmDto.getVsettleDate())
                || !StringUtil.isNotEmpty(confirmDto.getMsettleDate())
                || !StringUtil.isNotEmpty(confirmDto.getTradeDate())
        ) {
            return null;
        }
        //对下行数据进行处理
        if (StringUtil.equals(interfOpicsTradRepo.getSettleSpeed(), "1")) {
            interfOpicsTradRepo.setSettleSpeed("01");
        } else if (StringUtil.equals(interfOpicsTradRepo.getSettleSpeed(), "2")) {
            interfOpicsTradRepo.setSettleSpeed("02");
        } else if (StringUtil.equals(interfOpicsTradRepo.getSettleSpeed(), "3")) {
            interfOpicsTradRepo.setSettleSpeed("03");
        }
        //对上行数据进行处理
        if (StringUtil.equals(confirmDto.getSettleSpeed(), "0")) {
            confirmDto.setSettleSpeed("01");
        } else if (StringUtil.equals(confirmDto.getSettleSpeed(), "1")) {
            confirmDto.setSettleSpeed("02");
        } else if (StringUtil.equals(confirmDto.getSettleSpeed(), "2")) {
            confirmDto.setSettleSpeed("03");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if (StringUtil.equals(dealType, BussinessDictCodeList.交易类型_登记报价.getValue())
                || (confirmDto.getTradeDate().compareTo(interfOpicsTradRepo.getDealdate()) == 0
                && confirmDto.getTermDays().compareTo(new BigDecimal(interfOpicsTradRepo.getTerm())) == 0
                && StringUtil.equals(confirmDto.getPs(), interfOpicsTradRepo.getPs())
                && StringUtil.equals(confirmDto.getSettleSpeed(), interfOpicsTradRepo.getSettleSpeed())
                && StringUtil.equals(confirmDto.getSecManageAcctId(), interfOpicsTradRepo.getOurdepositaccountnumber())
                && BigDecimalUtil.ACompareToB(confirmDto.getRate(), iCstpTrade.getPrice(), 4) == 0
                && StringUtil.equals(sdf.format(confirmDto.getVsettleDate()), iCstpTrade.getSettldate())
                && StringUtil.equals(sdf.format(confirmDto.getMsettleDate()), iCstpTrade.getSettldate2())
                && BigDecimalUtil.ACompareToB(confirmDto.getTradeAmt(), iCstpTrade.getTradecashamt(), 2) == 0)
        ) {
            //更新下行匹配关系表
            if (isUpdate) {
                // 更新
                CfetsTrdConfirmMatchDto match = new CfetsTrdConfirmMatchDto();
                match.setPreTradeId(confirmDto.getTotleTradeId());
                match.setMatchType(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue());
                match.setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
                match.setUpdateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
                match.setDealType("999999");
                return match;
            } else if (isInsert) {
                //插入匹配表
                CfetsTrdConfirmMatchDto match = new CfetsTrdConfirmMatchDto();
                match.setSeqno(IDGenerator.generate());
                match.setCstpTradeId(interfOpicsTradRepo.getTradeid());
                match.setExeId(interfOpicsTradRepo.getOritradeid());
                match.setPreTradeId(confirmDto.getTotleTradeId());
                match.setMatchType(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue());
                match.setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
                match.setTrdConfStatus(DictCodeList.确认状态_未确认.getValue());
                match.setCreateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
                match.setCreateDept(CommonUtil.getDeptCode());
                match.setUpdateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
                match.setDealType(dealType);
                return match;
            }
        }
        return null;
    }


    /**
     * 回溯登记成交确认
     */
    @DTXTransaction
    public void resaleregautodeal(String totleTradeId) {
        cfetsTrdConfirm001Service.doCompleteMethod(cfetsTrdConfirmService.prepareEntity(totleTradeId, DictCodeListExt1.生成方式_批处理生成.getValue()), OperationType.NEW);
    }
}
