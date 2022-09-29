package com.joyintech.tams.cfets.trdconfirm.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealDbDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trd.repo.dto.CfetsRepoDealDbDto;
import com.joyintech.tams.cfets.trdconfirm.dao.CfetsTrdConfirmMapper;
import com.joyintech.tams.cfets.trdconfirm.dto.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public void cstpAutoMatchAfterReg(CfetsBondDealDbDto bondDealDbDto, CfetsRepoDealDbDto repoDealDbDto, CfetsTrdBondFirstSubInfoDto subInfoDto) {
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
//            matchDto = this.cstpSecTradeAutoMatch(interfTradSecdeal, iCstpTrade);
        } else if (StringUtil.isNotEmpty(repoDealDbDto)
                && StringUtil.equals(supportCstpFlagRepo, DictCodeList.是否_是.getValue())) {
            logger.info("银行间CSTP成交回报-质押式回购登记数据:{}", JsonUtils.object2Json(repoDealDbDto));
//            matchDto = this.cstpRepoTradeAutoMatch(interfOpicsTradRepo, iCstpTrade);
        } else if (StringUtil.isNotEmpty(subInfoDto)
                && StringUtil.equals(isSupportCstpFlagBondfirst, DictCodeList.是否_是.getValue())) {
            logger.info("银行间CSTP成交回报-一级申购登记数据:{}", JsonUtils.object2Json(subInfoDto));
            matchDto = this.cstpBidTradeAutoMatch(subInfoDto);
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
     * 一级申购登记数据匹配下行数据
     *
     * @param subInfoDto 一级申购登记数据
     * @return com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdConfirmMatchDto
     * @author jiangpeng
     * @date 2022/9/29 14:05
     */
    public CfetsTrdConfirmMatchDto cstpBidTradeAutoMatch(CfetsTrdBondFirstSubInfoDto subInfoDto) {
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
        match.setDealType(BussinessDictCodeList.报价方式_登记报价.getValue());
        return match;
    }
}
