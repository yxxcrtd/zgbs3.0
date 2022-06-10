package com.joyintech.tams.invest.common.reconciliation.service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.reconciliation.dao.ReconciliationCommonMapper;
import com.joyintech.tams.invest.common.reconciliation.dto.AccountFeePlan;
import com.joyintech.tams.invest.common.reconciliation.dto.AccountPlanSearchInfo;
import com.joyintech.tams.invest.common.reconciliation.dto.DayEndAccountDaichuliDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 对账公共服务
 *
 * @author fuyongsheng
 * @version 1.0 2021年10月26日
 * @see ReconciliationCommonService
 * @since 1.0
 */
@Service
public class ReconciliationCommonService {

    /**
     * 日志
     */
    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;

    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private StlSecReconMapper stlSecReconMapper;

    @Autowired
    private SettleTransDataMapper settleTransDataMapper;

    @Autowired
    private SettleTransDataStatusMapper settleTransDataStatusMapper;

    @Autowired
    private StlSecReconDetailMapper stlSecReconDetailMapper;

    @Autowired
    private ReconciliationCommonMapper reconciliationCommonMapper;
    /**
     * 平台日获取接口
     */
    @Autowired(required=false)
    private IPlatformDate iPlatformDate;

    /**
     * 处理对账
     * @param list
     */
    public void executeReconciliation(List<DayEndAccountDaichuliDto> list) {
        if (list != null && list.size() > 0) {
            //获取对账误差值
            Map<String, BigDecimal> wuCha = new HashMap<String, BigDecimal>();
            Map<String, Object> param = new HashMap<>();
            param.put("paraCode", DictCodeListStl.参数代码_误差值.getValue());
            List<StlSecReconPara> stlSecReconParaList = reconciliationCommonMapper.queryAccountPara(param);
            if (stlSecReconParaList != null && stlSecReconParaList.size() > 0) {
                for (StlSecReconPara para : stlSecReconParaList) {
                    if(StringUtil.isEmpty(para.getCustomerId())) {
                        wuCha.put("99",para.getParaValue() == null ? null : new BigDecimal(para.getParaValue()));
                    } else {
                        wuCha.put(para.getCustomerId(),para.getParaValue() == null ? null : new BigDecimal(para.getParaValue()));
                    }
                }
            }

            //轮询待对账数据
            for (DayEndAccountDaichuliDto dto : list) {
                //处理待对账数据
                chuliAccountInfo(dto, wuCha);
            }
            //将待对账数据List存入
            saveAccountInfo(list);
        }
    }

    /**
     * 处理待手工对账对账
     * @param list
     */
    public void executeManualReconciliation(List<DayEndAccountDaichuliDto> list) {
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        //将待手工对账的待处理数据List存入
        saveManualAccountInfo(list);
    }

    /**
     * 删除对账结果不一致和待处理数据
     */
    public void deleteCheckDisaccords(Date batchDate){
        Map<String, Object> param = new HashMap<>();
        param.put("batchDate", DateUtil.format(batchDate));
        param.put("CHECK_RESULT_0", DictCodeListExt3.对账状态结果_不一致.getValue());
        param.put("CHECK_RESULT_2", DictCodeListExt3.对账状态结果_待处理.getValue());
        // 删除成交明细状态表对账结果为不一致的数据
        reconciliationCommonMapper.deleteSettleTransDataStatusDisaccords(param);
        // 查询券商对账结果表对账结果为不一致的数据
        List<StlSecRecon> stlSecRecons = reconciliationCommonMapper.queryStlSecReconDisaccords(param);
        if(CollectionUtils.isNotEmpty(stlSecRecons)){
            param.put("stlSecRecons", stlSecRecons);
            //批量删除券商对账结果明细对账结果为不一致的数据
            reconciliationCommonMapper.batchDeleteStlSecReconDetail(param);
            //批量删除券商对账单对账结果为不一致的数据
            reconciliationCommonMapper.batchDeleteStlSecRecon(param);
        }
    }

    /**
     * 处理待对账数据
     * @param dto
     * @param wuCha
     */
    private void chuliAccountInfo(DayEndAccountDaichuliDto dto, Map<String, BigDecimal> wuCha) {
        BigDecimal wuChaZhi = wuCha.get(dto.getCustomerId());
        if (wuChaZhi == null) {
            wuChaZhi = wuCha.get("99");
        }
        //其他费用拆分（经手费+证管费+印花税+结算费+过户费+手续费）
        chuliOtherFee(dto);
        //误差不为空时
        if (wuChaZhi != null) {
            //if （abs（待对账数据.成交金额 - 待对账数据.清算成交金额）<= 变量.误差 and abs（待对账数据.成交数量- 待对账数据.清算成交数量）<=变量.误差 and
            //abs（待对账数据.佣金- 待对账数据.清算佣金）<= 变量.误差 and abs（待对账数据.印花税- 待对账数据.清算印花税）<=变量.误差 and
            //abs（待对账数据.经手费- 待对账数据.清算经手费）<= 变量.误差 and abs（待对账数据.证管费- 待对账数据.清算证管费）<= 变量.误差 and
            //abs（待对账数据.结算费- 待对账数据.清算结算费）< 变量.误差 and and abs（待对账数据.成交数量- 待对账数据.清算成交数量<= 变量.误差
            //and		待对账数据.金融产品类型 = 待对账数据.清算金融产品类型 and 待对账数据.交易方向 = 待对账数据.清算交易方向
            //and		待对账数据.交易场所 = 待对账数据.清算交易场所 and 待对账数据.交易日期 = 待对账数据.清算交易日期
            //and		待对账数据.证券账户 = 待对账数据.清算证券账户
            BigDecimal otherFee = ArithmeticUtil.doAdd(dto.getJsfAmt2(),dto.getZgfAmt2(),dto.getSettleFee2());
            if (ArithmeticUtil.subtract(dto.getTradeAmt(), dto.getTradeAmt2()).abs().compareTo(wuChaZhi) <= 0
                    && ArithmeticUtil.subtract(dto.getTradeNum(), dto.getTradeNum2()).abs().compareTo(wuChaZhi) <= 0
                    && ArithmeticUtil.subtract(dto.getStampFee(), dto.getStampFee2()).abs().compareTo(wuChaZhi) <= 0
//                        && ArithmeticUtil.subtract(dto.getOtherFeeamt(), otherFee).abs().compareTo(wuChaZhi) <= 0
                    && ArithmeticUtil.subtract(dto.getGhfAmt(), dto.getGhfAmt2()).abs().compareTo(wuChaZhi) <= 0
//                    && ArithmeticUtil.subtract(dto.getSxfAmt(), dto.getSxfAmt2()).abs().compareTo(wuChaZhi) <= 0
                    && ArithmeticUtil.subtract(dto.getJsfAmt(), dto.getJsfAmt2()).abs().compareTo(wuChaZhi) <= 0
                    && ArithmeticUtil.subtract(dto.getZgfAmt(), dto.getZgfAmt2()).abs().compareTo(wuChaZhi) <= 0
                    && ArithmeticUtil.subtract(dto.getSettleFee(), dto.getSettleFee2()).abs().compareTo(wuChaZhi) <= 0
                    && ArithmeticUtil.subtract(dto.getCommissionAmt(), dto.getCommissionAmt2()).abs().compareTo(wuChaZhi) <= 0
//                    && dto.getFinprodType() != null && dto.getFinprodType().equals(dto.getFinprodType2())
//                    && dto.getPs() != null && dto.getPs().equals(dto.getPs2())
                    && dto.getTradeMarket() != null && dto.getTradeMarket().equals(dto.getTradeMarket2())
                    && dto.getTradeDate() != null && dto.getTradeDate().compareTo(dto.getTradeDate2()) == 0
                    && dto.getSecAcctId() != null && dto.getSecAcctId().equals(dto.getSecAcctId2())) {
                dto.setCheckResult(DictCodeListExt3.对账状态结果_一致.getValue());
                //误差处理
                chuliWucha(dto);
            } else {
                dto.setCheckResult(DictCodeListExt3.对账状态结果_不一致.getValue());
                String checkInfo = "";
                //待对账数据.成交金额 ！= 待对账数据.清算成交金额
                if (ArithmeticUtil.subtract(dto.getTradeAmt(), dto.getTradeAmt2()).compareTo(BigDecimal.ZERO) != 0) {
                    String tradeAmt2 = dto.getTradeAmt2() == null ? "空" : dto.getTradeAmt2().toString();
                    String tradeAmt = dto.getTradeAmt() == null ? "空" : dto.getTradeAmt().toString();
                    checkInfo = checkInfo + "成交金额结算数据【" + tradeAmt2 + "】对账单【" + tradeAmt + "】\n";
                }
                //待对账数据.佣金 ！= 待对账数据.清算佣金
                if (ArithmeticUtil.subtract(dto.getCommissionAmt(), dto.getCommissionAmt2()).compareTo(BigDecimal.ZERO) != 0) {
                    String commissionAmt2 = dto.getCommissionAmt2() == null ? "空" : dto.getCommissionAmt2().toString();
                    String commissionAmt = dto.getCommissionAmt() == null ? "空" : dto.getCommissionAmt().toString();
                    checkInfo = checkInfo + "佣金结算数据【" + commissionAmt2 + "】对账单【" + commissionAmt + "】\n";
                }
                //待对账数据.证管费 ！= 待对账数据.清算证管费
                if (ArithmeticUtil.subtract(dto.getZgfAmt(), dto.getZgfAmt2()).compareTo(BigDecimal.ZERO) != 0) {
                    String zgfAmt2 = dto.getZgfAmt2() == null ? "空" : dto.getZgfAmt2().toString();
                    String zgfAmt = dto.getZgfAmt() == null ? "空" : dto.getZgfAmt().toString();
                    checkInfo = checkInfo + "证管费结算数据【" + zgfAmt2 + "】对账单【" + zgfAmt + "】\n";
                }
                // 待对账数据.印花税 ！= 待对账数据.清算印花税
                if (ArithmeticUtil.subtract(dto.getStampFee(), dto.getStampFee2()).compareTo(BigDecimal.ZERO) != 0) {
                    String stampFee2 = dto.getStampFee2() == null ? "空" : dto.getStampFee2().toString();
                    String stampFee = dto.getStampFee() == null ? "空" : dto.getStampFee().toString();
                    checkInfo = checkInfo + "印花税结算数据【" + stampFee2 + "】对账单【" + stampFee + "】\n";
                }
                //待对账数据.结算费 ！= 待对账数据.清算结算费
                if (ArithmeticUtil.subtract(dto.getSettleFee(), dto.getSettleFee2()).compareTo(BigDecimal.ZERO) != 0) {
                    String settleFee2 = dto.getSettleFee2() == null ? "空" : dto.getSettleFee2().toString();
                    String settleFee = dto.getSettleFee() == null ? "空" : dto.getSettleFee().toString();
                    checkInfo = checkInfo + "结算费结算数据【" + settleFee2 + "】对账单【" + settleFee + "】\n";
                }
                // 待对账数据.过户费 ！= 待对账数据.清算过户费
                if (ArithmeticUtil.subtract(dto.getGhfAmt(), dto.getGhfAmt2()).compareTo(BigDecimal.ZERO) != 0) {
                    String getGhfAmt2 = dto.getGhfAmt2() == null ? "空" : dto.getGhfAmt2().toString();
                    String getGhfAmt = dto.getGhfAmt() == null ? "空" : dto.getGhfAmt().toString();
                    checkInfo = checkInfo + "过户费结算数据【" + getGhfAmt2 + "】对账单【" + getGhfAmt + "】\n";
                }
                // 待对账数据.手续费 ！= 待对账数据.清算手续费
//                if (ArithmeticUtil.subtract(dto.getSxfAmt(), dto.getSxfAmt2()).compareTo(BigDecimal.ZERO) != 0) {
//                    String getSxfAmt2 = dto.getSxfAmt2() == null ? "空" : dto.getSxfAmt2().toString();
//                    String getSxfAmt = dto.getSxfAmt() == null ? "空" : dto.getSxfAmt().toString();
//                    checkInfo = checkInfo + "手续费结算数据【" + getSxfAmt2 + "】对账单【" + getSxfAmt + "】\n";
//                }
                // 待对账数据.成交数量 ！= 待对账数据.清算成交数量
                if (ArithmeticUtil.subtract(dto.getTradeNum(), dto.getTradeNum2()).compareTo(BigDecimal.ZERO) != 0) {
                    String getTradeNum2 = dto.getTradeNum2() == null ? "空" : dto.getTradeNum2().toString();
                    String getTradeNum = dto.getTradeNum() == null ? "空" : dto.getTradeNum().toString();
                    checkInfo = checkInfo + "成交数量结算数据【" + getTradeNum2 + "】对账单【" + getTradeNum + "】\n";
                }
                if (dto.getFinprodType() !=null && !dto.getFinprodType().equals(dto.getFinprodType2())) {
                    checkInfo = checkInfo + "金融产品类型结算数据【" + dto.getFinprodType2() + "】对账单【" + dto.getFinprodType() + "】\n";
                }
                //待对账数据.交易方向 ！= 待对账数据.清算交易方向
                if (dto.getPs() != null && !dto.getPs().equals(dto.getPs2())) {
                    checkInfo = checkInfo + "交易方向结算数据【" + dto.getPs2() + "】对账单【" + dto.getPs() + "】\n";
                }
                //待对账数据.交易场所 ！= 待对账数据.清算交易场所
                if (dto.getTradeMarket() != null && !dto.getTradeMarket().equals(dto.getTradeMarket2())) {
                    checkInfo = checkInfo + "交易场所结算数据【" + dto.getTradeMarket2() + "】对账单【" + dto.getTradeMarket() + "】\n";
                }
                //待对账数据.交易日期 ！= 待对账数据.清算交易日期
                if (dto.getTradeDate() != null && dto.getTradeDate().equals(dto.getTradeDate2())) {
                    checkInfo = checkInfo + "交易日期结算数据【" + DateUtil.format(dto.getTradeDate2()) + "】对账单【" + DateUtil.format(dto.getTradeDate()) + "】\n";
                }
                //待对账数据.证券账户 ！= 待对账数据.清算证券账户
                if (dto.getSecAcctId() != null && !dto.getSecAcctId().equals(dto.getSecAcctId2())) {
                    checkInfo = checkInfo + "证券账户结算数据【" + dto.getSecAcctId2() + "】对账单【" + dto.getSecAcctId() + "】\n";
                }
                dto.setReconInfo(checkInfo);
            }
        } else {
            //没有误差值时
            //	待对账数据.金融产品类型 = 待对账数据.清算金融产品类型 and 待对账数据.交易方向 = 待对账数据.清算交易方向
            //	待对账数据.交易场所 = 待对账数据.清算交易场所 and 待对账数据.交易日期 = 待对账数据.清算交易日期
            //		待对账数据.证券账户 = 待对账数据.清算证券账户
            if (
//                    dto.getFinprodType() != null && dto.getFinprodType().equals(dto.getFinprodType2())
//                    && dto.getPs() != null && dto.getPs().equals(dto.getPs2()) &&
                    dto.getTradeMarket() != null && dto.getTradeMarket().equals(dto.getTradeMarket2())
                    && dto.getTradeDate() != null && dto.getTradeDate().equals(dto.getTradeDate2())
                    && dto.getSecAcctId() != null && dto.getSecAcctId().equals(dto.getSecAcctId2())) {
                dto.setCheckResult(DictCodeListExt3.对账状态结果_一致.getValue());
                //误差处理
                chuliWucha(dto);
            } else {
                dto.setCheckResult(DictCodeListExt3.对账状态结果_不一致.getValue());
                String checkInfo = "";
                if (dto.getFinprodType() !=null && !dto.getFinprodType().equals(dto.getFinprodType2())) {
                    checkInfo = checkInfo + "金融产品类型结算数据【" + dto.getFinprodType2() + "】对账单【" + dto.getFinprodType() + "】\n";
                }
                //待对账数据.交易方向 ！= 待对账数据.清算交易方向
                if (dto.getPs() != null && !dto.getPs().equals(dto.getPs2())) {
                    checkInfo = checkInfo + "交易方向结算数据【" + dto.getPs2() + "】对账单【" + dto.getPs() + "】\n";
                }
                //待对账数据.交易场所 ！= 待对账数据.清算交易场所
                if (dto.getTradeMarket() != null && !dto.getTradeMarket().equals(dto.getTradeMarket2())) {
                    checkInfo = checkInfo + "交易场所结算数据【" + dto.getTradeMarket2() + "】对账单【" + dto.getTradeMarket() + "】\n";
                }
                //待对账数据.交易日期 ！= 待对账数据.清算交易日期
                if (dto.getTradeDate() != null && dto.getTradeDate().equals(dto.getTradeDate2())) {
                    checkInfo = checkInfo + "交易日期结算数据【" + DateUtil.format(dto.getTradeDate2()) + "】对账单【" + DateUtil.format(dto.getTradeDate()) + "】\n";
                }
                //待对账数据.证券账户 ！= 待对账数据.清算证券账户
                if (dto.getSecAcctId() != null && !dto.getSecAcctId().equals(dto.getSecAcctId2())) {
                    checkInfo = checkInfo + "证券账户结算数据【" + dto.getSecAcctId2() + "】对账单【" + dto.getSecAcctId() + "】\n";
                }
                dto.setReconInfo(checkInfo);
            }
        }
    }

    /**
     * 处理误差，更新场内清算数据
     * @param dto
     */
    public void chuliWucha(DayEndAccountDaichuliDto dto) {
        dto.setTradeAmtCe(ArithmeticUtil.subtract(dto.getTradeAmt().abs(), dto.getTradeAmt2().abs()));
        dto.setTradeNumCe(ArithmeticUtil.subtract(dto.getTradeNum().abs(), dto.getTradeNum2().abs()));
        dto.setStampFeeCe(ArithmeticUtil.subtract(dto.getStampFee(), dto.getStampFee2()));
        dto.setJsfAmtCe(ArithmeticUtil.subtract(dto.getJsfAmt(), dto.getJsfAmt2()));
        dto.setGhfAmtCe(ArithmeticUtil.subtract(dto.getGhfAmt(), dto.getGhfAmt2()));
        dto.setZgfAmtCe(ArithmeticUtil.subtract(dto.getZgfAmt(), dto.getZgfAmt2()));
        dto.setSxfAmtCe(ArithmeticUtil.subtract(dto.getSxfAmt(), dto.getSxfAmt2()));
        dto.setSettleFeeCe(ArithmeticUtil.subtract(dto.getSettleFee(), dto.getSettleFee2()));
        dto.setCommissionAmtCe(ArithmeticUtil.subtract(dto.getCommissionAmt(), dto.getCommissionAmt2()));

        //查询证券账户信息
        StlSecAcctInfo stlSecAcctInfo = stlSecAcctInfoMapper.selectByPrimaryKey(dto.getSecAcctId());
        //变量.标的分类信息
        FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
        finProductTypeKey.setBranch(0L);
        finProductTypeKey.setFinprodId(dto.getFinprodId());
        FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(finProductTypeKey);

        //有差额费用统计
        List<String> feeList = new ArrayList<String>();
        if (BigDecimal.ZERO.compareTo(dto.getStampFeeCe()) != 0) {
            feeList.add(DictCodeListExt1.一次性费用类型_印花税.getValue());
        }
        if (BigDecimal.ZERO.compareTo(dto.getJsfAmtCe()) != 0) {
            feeList.add(DictCodeListExt1.一次性费用类型_经手费.getValue());
        }
        if (BigDecimal.ZERO.compareTo(dto.getGhfAmtCe()) != 0) {
            feeList.add(DictCodeListExt1.一次性费用类型_过户费.getValue());
        }
        if (BigDecimal.ZERO.compareTo(dto.getZgfAmtCe()) != 0) {
            feeList.add(DictCodeListExt1.一次性费用类型_证管费.getValue());
        }
        if (BigDecimal.ZERO.compareTo(dto.getSxfAmtCe()) != 0) {
            feeList.add(DictCodeListExt1.一次性费用类型_交易手续费.getValue());
        }
        if (BigDecimal.ZERO.compareTo(dto.getSettleFeeCe()) != 0) {
            feeList.add(DictCodeListExt1.一次性费用类型_结算费.getValue());
        }
        if (BigDecimal.ZERO.compareTo(dto.getCommissionAmtCe()) != 0) {
            feeList.add(DictCodeListExt1.一次性费用类型_佣金.getValue());
        }
        if (feeList != null && feeList.size() > 0) {
            for (String feeType : feeList) {
                AccountPlanSearchInfo localPlanSearchInfo = new AccountPlanSearchInfo();
                // 交易场所
                if (StringUtil.isNotEmpty(dto.getTradeMarket())) {
                    localPlanSearchInfo.setTradeMarket(dto.getTradeMarket());
                }
                // 交易平台
                if (StringUtil.isNotEmpty(dto.getTradePlat())) {
                    localPlanSearchInfo.setTradePlatform(dto.getTradePlat());
                }
                // 费用类型
                if (StringUtil.isNotEmpty(feeType)) {
                    localPlanSearchInfo.setFeeType(feeType);
                }
                // 登记托管机构
                if (stlSecAcctInfo != null && StringUtil.isNotEmpty(stlSecAcctInfo.getRegistOrg())) {
                    localPlanSearchInfo.setRegistOrg(stlSecAcctInfo.getRegistOrg());
                }
                // 交易品种
                String tradeType = dto.getFinprodType();
                if (StringUtil.isNotEmpty(tradeType)) {
                    if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), tradeType)) {
                        tradeType = DictCodeList.平安资产类型_债券.getValue();
                    }
                    if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), tradeType)
                            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), tradeType)) {
                        tradeType = DictCodeList.平安资产类型_质押式回购.getValue();
                    }
                    if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue(), tradeType)) {
                        tradeType = DictCodeList.平安资产类型_基金.getValue();
                    }
                    if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue(), tradeType)) {
                        tradeType = DictCodeListExt2.平安资产类型_股票.getValue();
                    }
                    localPlanSearchInfo.setTradeType(tradeType);
                }
                // 交易方向
                if (StringUtil.isNotEmpty(dto.getPs())) {
                    localPlanSearchInfo.setPs(dto.getPs());
                }
//                // 期限品种
//                if (StringUtil.isNotEmpty(dto.getTermType())) {
//                    localPlanSearchInfo.setTermType(dto.getTermType());
//                }
                // 证券账户
                if (StringUtil.isNotEmpty(dto.getSecAcctId())) {
                    localPlanSearchInfo.setSecAcctId(dto.getSecAcctId());
                }
                // 券商代码
                if (StringUtil.isNotEmpty(dto.getCustomerId())) {
                    localPlanSearchInfo.setBrokerageNo(dto.getCustomerId());
                }
                // 证券分类1
                if (finProductType != null && StringUtil.isNotEmpty(finProductType.getType9())) {
                    localPlanSearchInfo.setAssetClassify1(finProductType.getType9());
                }
                // 证券分类2
                if (finProductType != null && StringUtil.isNotEmpty(finProductType.getType10())) {
                    localPlanSearchInfo.setAssetClassify2(finProductType.getType10());
                }
                // 证券分类3
                if (finProductType != null && StringUtil.isNotEmpty(finProductType.getType11())) {
                    localPlanSearchInfo.setAssetClassify3(finProductType.getType11());
                }
                // 组合代码
                if (StringUtil.isNotEmpty(dto.getPortfolioId())) {
                    localPlanSearchInfo.setPortfolioId(dto.getPortfolioId());
                }
                //调用共通方法【费用方案获取】
                AccountFeePlan feePlan = getFeePlan(localPlanSearchInfo, DateUtil.format(dto.getTradeDate()));
                String isPay = feePlan == null || feePlan.getTrdFeeConfig() == null ? null :  feePlan.getTrdFeeConfig().getIsPayWithTrade();
                if (DictCodeListExt1.一次性费用类型_佣金.getValue().equals(feeType)) {
                    dto.setIsPayWithTrade(isPay);
                }
                //费用信息.是否随交易支付 = 是 and 费用信息.费用类型 = 佣金
                if (DictCodeListExt1.是否_是.getValue().equals(isPay)
                        && DictCodeListExt1.一次性费用类型_佣金.getValue().equals(feeType)) {
                    //if 待对账数据.交易方向 = 买入 or 正回购
                    if (DictCodeList.交易方向_买入.getValue().equals(dto.getPs())
                        || DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 start
                        || DictCodeListExt1.交易方向_债转股.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 end
                    ) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 + 待清算数据.清算佣金差额
                        dto.setSettleAmtCe(ArithmeticUtil.doAdd(dto.getSettleAmtCe(), dto.getCommissionAmtCe()));
                    } else if (DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
                            || DictCodeList.回购拆借方向_逆回购.getValue().equals(dto.getPs())) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 - 待清算数据.清算佣金差额
                        dto.setSettleAmtCe(ArithmeticUtil.subtract(dto.getSettleAmtCe(), dto.getCommissionAmtCe()));
                    }
                } else if (DictCodeListExt1.是否_是.getValue().equals(isPay)
                        && DictCodeListExt1.一次性费用类型_经手费.getValue().equals(feeType)) {
                    if (DictCodeList.交易方向_买入.getValue().equals(dto.getPs())
                        || DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getPs())
                        || DictCodeList.交易方向_申购.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 start
                        || DictCodeListExt1.交易方向_债转股.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 end
                    ) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 + 待清算数据.清算经手费差额
                        dto.setSettleAmtCe(ArithmeticUtil.doAdd(dto.getSettleAmtCe(), dto.getJsfAmtCe()));
                    } else if (DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
                            || DictCodeList.回购拆借方向_逆回购.getValue().equals(dto.getPs())) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 - 待清算数据.清算佣金差额
                        dto.setSettleAmtCe(ArithmeticUtil.subtract(dto.getSettleAmtCe(), dto.getJsfAmtCe()));
                    }
                } else if (DictCodeListExt1.是否_是.getValue().equals(isPay)
                        && DictCodeListExt1.一次性费用类型_证管费.getValue().equals(feeType)) {
                    if (DictCodeList.交易方向_买入.getValue().equals(dto.getPs())
                        || DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 start
                        || DictCodeListExt1.交易方向_债转股.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 end
                    ) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 + 待清算数据.清算证管费差额
                        dto.setSettleAmtCe(ArithmeticUtil.doAdd(dto.getSettleAmtCe(), dto.getZgfAmtCe()));
                    } else if (DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
                            || DictCodeList.回购拆借方向_逆回购.getValue().equals(dto.getPs())) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 - 待清算数据.清算证管费差额
                        dto.setSettleAmtCe(ArithmeticUtil.subtract(dto.getSettleAmtCe(), dto.getZgfAmtCe()));
                    }
                } else if (DictCodeListExt1.是否_是.getValue().equals(isPay)
                        && DictCodeListExt1.一次性费用类型_印花税.getValue().equals(feeType)) {
                    if (DictCodeList.交易方向_买入.getValue().equals(dto.getPs())
                        || DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 start
                        || DictCodeListExt1.交易方向_债转股.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 end
                    ) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 + 待清算数据.清算印花税差额
                        dto.setSettleAmtCe(ArithmeticUtil.doAdd(dto.getSettleAmtCe(), dto.getStampFeeCe()));
                    } else if (DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
                            || DictCodeList.回购拆借方向_逆回购.getValue().equals(dto.getPs())) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 - 待清算数据.清算印花税差额
                        dto.setSettleAmtCe(ArithmeticUtil.subtract(dto.getSettleAmtCe(), dto.getStampFeeCe()));
                    }
                } else if (DictCodeListExt1.是否_是.getValue().equals(isPay)
                        && DictCodeListExt1.一次性费用类型_结算费.getValue().equals(feeType)) {
                    if (DictCodeList.交易方向_买入.getValue().equals(dto.getPs())
                        || DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 start
                        || DictCodeListExt1.交易方向_债转股.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 end
                    ) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 + 待清算数据.清算结算费差额
                        dto.setSettleAmtCe(ArithmeticUtil.doAdd(dto.getSettleAmtCe(), dto.getSettleFeeCe()));
                    } else if (DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
                            || DictCodeList.回购拆借方向_逆回购.getValue().equals(dto.getPs())) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 - 待清算数据.清算结算费差额
                        dto.setSettleAmtCe(ArithmeticUtil.subtract(dto.getSettleAmtCe(), dto.getSettleFeeCe()));
                    }
                } else if (DictCodeListExt1.是否_是.getValue().equals(isPay)
                        && DictCodeListExt1.一次性费用类型_过户费.getValue().equals(feeType)) {
                    if (DictCodeList.交易方向_买入.getValue().equals(dto.getPs())
                        || DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 start
                        || DictCodeListExt1.交易方向_债转股.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 end
                    ) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 + 待清算数据.清算过户费差额
                        dto.setSettleAmtCe(ArithmeticUtil.doAdd(dto.getSettleAmtCe(), dto.getGhfAmtCe()));
                    } else if (DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
                            || DictCodeList.回购拆借方向_逆回购.getValue().equals(dto.getPs())) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 - 待清算数据.清算过户费差额
                        dto.setSettleAmtCe(ArithmeticUtil.subtract(dto.getSettleAmtCe(), dto.getGhfAmtCe()));
                    }
                } else if (DictCodeListExt1.是否_是.getValue().equals(isPay)
                        && DictCodeListExt1.一次性费用类型_交易手续费.getValue().equals(feeType)) {
                    if (DictCodeList.交易方向_买入.getValue().equals(dto.getPs())
                        || DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 start
                        || DictCodeListExt1.交易方向_债转股.getValue().equals(dto.getPs())
                        // add by fuyongsheng 20211207 自动对账批处理开发 end
                    ) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 + 待清算数据.清算手续费差额
                        dto.setSettleAmtCe(ArithmeticUtil.doAdd(dto.getSettleAmtCe(), dto.getSxfAmtCe()));
                    } else if (DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
                            || DictCodeList.回购拆借方向_逆回购.getValue().equals(dto.getPs())) {
                        //待对账数据.清算金额差额 = 待对账数据.清算金额差额 - 待清算数据.清算手续费差额
                        dto.setSettleAmtCe(ArithmeticUtil.subtract(dto.getSettleAmtCe(), dto.getSxfAmtCe()));
                    }
                }
            }
        }
    }

    /**
     * 其他费用拆分 (经手费+证管费+印花税+结算费+过户费+手续费）
     * @param dto
     */
    private void chuliOtherFee(DayEndAccountDaichuliDto dto) {
        BigDecimal fee = ArithmeticUtil.doAdd(dto.getJsfAmt(),dto.getZgfAmt(),dto.getStampFee(),dto.getSettleFee(),dto.getGhfAmt(),dto.getSxfAmt());
        // 经手费+证管费+印花税+结算费+过户费+手续费=0 并且 待对账数据.一级总费用!=0
        BigDecimal totalTeeamt = dto.getTotalTeeamt();
        if(fee.equals(BigDecimal.ZERO) && totalTeeamt != null && !totalTeeamt.equals(BigDecimal.ZERO)){
            // 清算一级总费用
            BigDecimal settleTotalTeeamt = ArithmeticUtil.doAdd(dto.getJsfAmt2(),dto.getZgfAmt2(),dto.getStampFee2(),dto.getSettleFee2(),dto.getGhfAmt2(),dto.getSxfAmt2());
            // 经手费
            dto.setJsfAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(totalTeeamt, dto.getJsfAmt2()), settleTotalTeeamt, 2));
            // 证管费
            dto.setZgfAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(totalTeeamt, dto.getZgfAmt2()), settleTotalTeeamt, 2));
            // 印花税
            dto.setStampFee(ArithmeticUtil.divide(ArithmeticUtil.multiply(totalTeeamt, dto.getStampFee2()), settleTotalTeeamt, 2));
            // 结算费
            dto.setSettleFee(ArithmeticUtil.divide(ArithmeticUtil.multiply(totalTeeamt, dto.getSettleFee2()), settleTotalTeeamt, 2));
            // 过户费
            dto.setGhfAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(totalTeeamt, dto.getGhfAmt2()), settleTotalTeeamt, 2));
            // 手续费
            dto.setSxfAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(totalTeeamt, dto.getSxfAmt2()), settleTotalTeeamt, 2));
            // 差额处理，放到最大的费用上(差额=清算一级总费用-经手费-证管费-印花税-结算费-过户费-手续费)
            // 最终加到最大的费用上,如最大值相等，则随便加在哪个上面,这里加在第一个最大值上
            BigDecimal balance = settleTotalTeeamt
                    .subtract(dto.getJsfAmt())
                    .subtract(dto.getZgfAmt())
                    .subtract(dto.getStampFee())
                    .subtract(dto.getSettleFee())
                    .subtract(dto.getGhfAmt())
                    .subtract(dto.getSxfAmt());
            if(balance.compareTo(BigDecimal.ZERO)>0){
                // 求出最大值
                BigDecimal[] arrays = new BigDecimal[]{dto.getJsfAmt(), dto.getZgfAmt(),dto.getStampFee(),
                        dto.getSettleFee(),dto.getGhfAmt(),dto.getSxfAmt()};
                BigDecimal max = Arrays.stream(arrays).max(Comparator.naturalOrder()).get();
                if(dto.getJsfAmt().compareTo(max) == 0){
                    dto.setJsfAmt(dto.getJsfAmt().add(balance));
                }else if(dto.getZgfAmt().compareTo(max) == 0){
                    dto.setZgfAmt(dto.getZgfAmt().add(balance));
                }else if(dto.getStampFee().compareTo(max) == 0){
                    dto.setStampFee(dto.getStampFee().add(balance));
                }else if(dto.getSettleFee().compareTo(max) == 0){
                    dto.setSettleFee(dto.getSettleFee().add(balance));
                }else if(dto.getGhfAmt().compareTo(max) == 0){
                    dto.setGhfAmt(dto.getGhfAmt().add(balance));
                }else if(dto.getSxfAmt().compareTo(max) == 0){
                    dto.setSxfAmt(dto.getSxfAmt().add(balance));
                }
            }
        }
    }

    /**
     * 获取费用方案
     *
     * @return
     */
    public AccountFeePlan getFeePlan(AccountPlanSearchInfo planSearchInfo, String feeDate) {
        AccountFeePlan feePlan = null;
        Map<String, Object> searchParam = new HashMap<>();
        // 1.1 交易费用配置查询
        searchParam.put("planInfo", planSearchInfo);
        searchParam.put("feeDate", feeDate);
        List<TrdFeeConfig> trdFeeConfigs = reconciliationCommonMapper.getTrdFeeConfig(searchParam);
        if (trdFeeConfigs != null && !trdFeeConfigs.isEmpty()) {
            feePlan = new AccountFeePlan();
            // 取第一条数据
            feePlan.setTrdFeeConfig(trdFeeConfigs.get(0));
            // 1.2 交易费用配置明细
            List<TrdFeeConfigDetail> trdFeeConfigDetails = reconciliationCommonMapper
                    .getTrdFeeConfigDetail(trdFeeConfigs.get(0).getSeqNo());
            feePlan.setTrdFeeConfigDetails(trdFeeConfigDetails);
        }
        return feePlan;
    }

    /**
     * 登录自动对账结果数据
     * @param list
     */
    private void saveAccountInfo(List<DayEndAccountDaichuliDto> list) {
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        for (DayEndAccountDaichuliDto dto : list) {
            // 登录自动对账对账单结果表
            saveStlSecRecon(dto);
            // 查询成交明细数据
            List<SettleTransData> listSettleTransData = querySettleTransDatas(dto);
            if(CollectionUtils.isNotEmpty(listSettleTransData)){
                // 重新结算交易明细数据
                SettleTransData std = getReCalSettleTransData(listSettleTransData.get(0),dto);
                // 更新成交明细数据
                settleTransDataMapper.updateByPrimaryKeySelective(std);
                // 登录成交明细状态表和券商对账单详情表
                saveSettleTransDataStatusDetail(dto, listSettleTransData);
            }
        }
    }

    /**
     * 登录待手工对账的结果数据
     * @param list
     */
    private void saveManualAccountInfo(List<DayEndAccountDaichuliDto> list) {
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        for (DayEndAccountDaichuliDto dto : list) {
            dto.setSeqNo(IDGenerator.generate());
            // 登录待手工对账对账单结果表
            saveManualStlSecRecon(dto);
            // 查询待手工对账的成交明细数据
            List<SettleTransData> listSettleTransData = queryManualSettleTransDatas(dto);
            if(CollectionUtils.isEmpty(listSettleTransData)){
                continue;
            }
            // 登录成交明细状态表和券商对账单详情表
            saveSettleTransDataStatusDetail(dto, listSettleTransData);
        }
    }

    /**
     * 登录自动对账券商对账单结果表
     * @param dto
     */
    private void saveStlSecRecon(DayEndAccountDaichuliDto dto){
        //根据券商对账单删除之前的对账数据
        stlSecReconMapper.deleteByPrimaryKey(dto.getSeqNo());
        //封装新的对账单数据
        StlSecRecon stlSecRecon = new StlSecRecon();
        stlSecRecon.setSeqNo(dto.getSeqNo());
        stlSecRecon.setCustomerId(dto.getCustomerId());
        stlSecRecon.setPortfolioId(dto.getPortfolioId());
        stlSecRecon.setFinprodId(dto.getFinprodId());
        stlSecRecon.setMarketId(dto.getMarketId());
        stlSecRecon.setSettleDate(dto.getTradeDate());
        stlSecRecon.setTradeMarket(dto.getTradeMarket());
        stlSecRecon.setCheckResult(dto.getCheckResult());
        stlSecRecon.setStlSecDeme(dto.getParaValue());
        stlSecRecon.setAccount(dto.getAccout());
        stlSecRecon.setEntrustTradeId(dto.getTradeId2());
        stlSecRecon.setReconInfo(StringUtil.isEmpty(dto.getReconInfo()) ? null : dto.getReconInfo().getBytes());
        reconciliationCommonMapper.insertStlSecRecon(stlSecRecon);
    }

    /**
     * 登录待手工对账单结果表
     * @param dto
     */
    private void saveManualStlSecRecon(DayEndAccountDaichuliDto dto){
        //根据券商对账单删除之前的对账数据
        stlSecReconMapper.deleteByPrimaryKey(dto.getSeqNo());
        //封装新的对账单数据
        StlSecRecon stlSecRecon = new StlSecRecon();
        stlSecRecon.setSeqNo(dto.getSeqNo());
        stlSecRecon.setCustomerId(dto.getCustomerId());
        stlSecRecon.setFinprodId(dto.getFinprodId());
        stlSecRecon.setSettleDate(dto.getTradeDate2());
        stlSecRecon.setTradeMarket(dto.getTradeMarket2());
        stlSecRecon.setCheckResult(dto.getCheckResult());
        stlSecRecon.setStlSecDeme(dto.getParaValue());
        stlSecRecon.setAccount(dto.getAccout());
        stlSecRecon.setEntrustTradeId(dto.getTradeId2());
        reconciliationCommonMapper.insertStlSecRecon(stlSecRecon);
    }

    /**
     * 登录成交明细状态表和券商对账单详情表
     * @param dto
     * @param listSettleTransData
     */
    private void saveSettleTransDataStatusDetail(DayEndAccountDaichuliDto dto, List<SettleTransData> listSettleTransData){
        if(CollectionUtils.isEmpty(listSettleTransData)){
            return;
        }
        StlSecReconDetail detail;
        SettleTransDataStatus status;
        for (SettleTransData settleTransData : listSettleTransData) {
            detail = new StlSecReconDetail();
            detail.setSeqNo(dto.getSeqNo());
            detail.setTradeId(settleTransData.getTradeId());
            detail.setStlSecDeme(dto.getParaValue());
            StlSecReconDetailKey detailKey = new StlSecReconDetailKey();
            detailKey.setTradeId(settleTransData.getTradeId());
            detailKey.setSeqNo(dto.getSeqNo());
            // GZYHTZJY-597:为防止主键冲突,先删除再插入 fuyongsheng 20211125 edit start
            stlSecReconDetailMapper.deleteByPrimaryKey(detailKey);
            // GZYHTZJY-597:为防止主键冲突,先删除再插入 fuyongsheng 20211125 edit end
            reconciliationCommonMapper.insertStlSecReconDetail(detail);
            // GZYHTZJY-597:为防止主键冲突,先删除再插入 fuyongsheng 20211125 edit start
            settleTransDataStatusMapper.deleteByPrimaryKey(settleTransData.getTradeId());
            // GZYHTZJY-597:为防止主键冲突,先删除再插入 fuyongsheng 20211125 edit end
            status = new SettleTransDataStatus();
            status.setTradeId(settleTransData.getTradeId());
            status.setCheckResult(dto.getCheckResult());
            settleTransDataStatusMapper.insertSelective(status);
        }
    }

    /**
     * 查询自动对账清算明细
     * @param dto
     * @return
     */
    private List<SettleTransData> querySettleTransDatas(DayEndAccountDaichuliDto dto){
        //调用【清算明细查询】
        Map<String, Object> param = new HashMap<String, Object>();
        if(DictCodeListStl.对账维度_委托汇总.getValue().equals(dto.getParaValue())){
            param.put("applyTradeId", dto.getTradeId2());
            param.put("dealDate", dto.getTradeDate());
            param.put("seatNumber", dto.getSeatNumber());
        }else if(DictCodeListStl.对账维度_证券汇总.getValue().equals(dto.getParaValue())){
            param.put("finprodId", dto.getFinprodId());
            param.put("dealDate", dto.getTradeDate());
            param.put("seatNumber", dto.getSeatNumber());
        }else if(DictCodeListStl.对账维度_成交明细.getValue().equals(dto.getParaValue())){
            if(StringUtil.isEmpty(dto.getTradeId())){
                param.put("finprodId", dto.getFinprodId());
                param.put("secAcctId", dto.getSecAcctId());
                param.put("seatNumber", dto.getSeatNumber());
                param.put("tradeDate", dto.getTradeDate());
            }else {
                param.put("cjbh", dto.getTradeId());
                param.put("seatNumber", dto.getSeatNumber());
                param.put("tradeDate", dto.getTradeDate());
            }
        }
        return reconciliationCommonMapper.getSettleTransData(param);
    }

    /**
     * 查询待手工对账明细
     * @param dto
     * @return
     */
    private List<SettleTransData> queryManualSettleTransDatas(DayEndAccountDaichuliDto dto){
        //调用【清算明细查询】
        Map<String, Object> param = new HashMap<String, Object>();
        if(DictCodeListStl.对账维度_委托汇总.getValue().equals(dto.getParaValue())){
            param.put("applyTradeId", dto.getTradeId2());
            param.put("dealDate", dto.getTradeDate2());
            param.put("seatNumber", dto.getSeatNumber());
        }else if(DictCodeListStl.对账维度_证券汇总.getValue().equals(dto.getParaValue())){
            param.put("finprodId", dto.getFinprodId());
            param.put("dealDate", dto.getTradeDate2());
            param.put("seatNumber", dto.getSeatNumber());
        }else if(DictCodeListStl.对账维度_成交明细.getValue().equals(dto.getParaValue())){
            param.put("finprodId", dto.getFinprodId());
            param.put("secAcctId", dto.getSecAcctId2());
            param.put("seatNumber", dto.getSeatNumber());
            param.put("tradeDate", dto.getTradeDate2());
            param.put("cjbh", dto.getTradeId2());
        }
        return reconciliationCommonMapper.getSettleTransData(param);
    }

    /**
     * 获取新的交易明细
     * @param settleTransData
     * @param dto
     * @return
     */
    public SettleTransData getReCalSettleTransData(SettleTransData settleTransData, DayEndAccountDaichuliDto dto){
        SettleTransData settleTransDataNew = new SettleTransData();
        settleTransDataNew.setTradeId(settleTransData.getTradeId());
        //成交数据明细表.佣金									=	成交数据明细表.佣金 + 待对账数据.清算佣金差额
        settleTransDataNew.setCommissionAmt(ArithmeticUtil.doAdd(settleTransData.getCommissionAmt(), dto.getCommissionAmtCe()));
        if (settleTransDataNew.getCommissionAmt() != null
                && settleTransDataNew.getCommissionAmt().compareTo(BigDecimal.ZERO) != 0
                && StringUtil.isNotEmpty(dto.getIsPayWithTrade())) {
            settleTransDataNew.setIsPayWithTrade(dto.getIsPayWithTrade());
        } else if (StringUtil.isEmpty(dto.getIsPayWithTrade())
                && settleTransDataNew.getCommissionAmt() != null
                && settleTransDataNew.getCommissionAmt().compareTo(BigDecimal.ZERO) != 0) {
            settleTransDataNew.setIsPayWithTrade(DictCodeListExt1.是否_否.getValue());
        }
        //成交数据明细表.经手费									=	成交数据明细表.经手费 + 待对账数据.清算经手费差额
        settleTransDataNew.setJsfAmt(ArithmeticUtil.doAdd(settleTransData.getJsfAmt(), dto.getJsfAmtCe()));
        //成交数据明细表.证管费									=	成交数据明细表.证管费 + 待对账数据.清算证管费差额
        settleTransDataNew.setZgfAmt(ArithmeticUtil.doAdd(settleTransData.getZgfAmt(), dto.getZgfAmtCe()));
        //成交数据明细表.印花税									=	成交数据明细表.印花税 + 待对账数据.清算印花税差额
        settleTransDataNew.setStampFee(ArithmeticUtil.doAdd(settleTransData.getStampFee(), dto.getStampFeeCe()));
        //成交数据明细表.结算费									=	成交数据明细表.结算费 + 待对账数据.清算结算费差额
        settleTransDataNew.setSettleFee(ArithmeticUtil.doAdd(settleTransData.getSettleFee(), dto.getSettleFeeCe()));
        //成交数据明细表.过户费									=	成交数据明细表.过户费 +待对账数据.清算过户费差额
        settleTransDataNew.setGhfAmt(ArithmeticUtil.doAdd(settleTransData.getGhfAmt(), dto.getGhfAmtCe()));
        //成交数据明细表.手续费									=	成交数据明细表.手续费 +待对账数据.清算手续费差额
        settleTransDataNew.setSxfAmt(ArithmeticUtil.doAdd(settleTransData.getSxfAmt(), dto.getSxfAmtCe()));
        //成交数据明细表.全价总额									=	成交数据明细表.成交金额 +待对账数据.清算成交金额差额
        settleTransDataNew.setFpriceAmt(ArithmeticUtil.doAdd(settleTransData.getFpriceAmt(), dto.getTradeAmtCe()));
        //成交数据明细表.清算金额									=	成交数据明细表.清算金额 +待对账数据.清算金额差额
        settleTransDataNew.setTradeAmt(ArithmeticUtil.doAdd(settleTransData.getTradeAmt(), dto.getSettleAmtCe(), dto.getTradeAmtCe()));
        //成交数据明细表.成交数量									=	成交数据明细表.成交数量 +待对账数据.清算成交数量差额
        settleTransDataNew.setShareAmt(ArithmeticUtil.doAdd(settleTransData.getShareAmt(), dto.getTradeNumCe()));
        return settleTransDataNew;
    }
}
