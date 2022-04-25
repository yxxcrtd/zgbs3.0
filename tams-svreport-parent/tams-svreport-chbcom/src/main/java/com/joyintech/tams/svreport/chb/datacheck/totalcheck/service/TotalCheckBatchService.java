/*
 * 文件名：DelayVerification001Service.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统
 * 修改人：dingzhipeng
 * 修改时间：2017年9月25日
 * 修改内容：新建
 * 系统名称：中信银行资管系统
 */

package com.joyintech.tams.svreport.chb.datacheck.totalcheck.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.CacheUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.batch.report.autosendcb.common.service.SvrCBBaseService;
import com.joyintech.tams.common.svreport.chb.constant.CheckConfConstants;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.ChbCcyConvertUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.common.svreport.datacheck.dao.ChbCheckRuleoutMapper;
import com.joyintech.tams.common.svreport.datacheck.dto.ChbCheckRuleout;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.report.common.constant.BatchBeanName;
import com.joyintech.tams.svreport.chb.datacheck.totalcheck.dao.ChbTotalCheckCBMapper;
import com.joyintech.tams.svreport.chb.datacheck.totalcheck.dao.ChbTotalCheckDetailMapper;
import com.joyintech.tams.svreport.chb.datacheck.totalcheck.dao.ChbTotalCheckMapper;
import com.joyintech.tams.svreport.chb.datacheck.totalcheck.dto.ChbTotalCheckCBConstants;
import com.joyintech.tams.svreport.chb.datacheck.totalcheck.dto.ChbTotalCheckDetailDto;
import com.joyintech.tams.svreport.chb.datacheck.totalcheck.dto.ChbTotalCheckDto;
import com.joyintech.tams.svreport.chb.datacheck.totalcheck.util.ChbTotalCheckCBUtils;
import com.joyintech.tams.svreport.chb.monthreport.dao.ChbMonthReportMapper;
import com.joyintech.tams.svreport.chb.monthreport.dto.ChbMonthReport;
import com.joyintech.tams.svreport.chb.monthreport.dto.ChbMonthReportDetail;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.tools.collectors.JoyinCollectors;

/**
 * 
 * 功能:中体核查service
 * @since 1.7.0
 * @version 1.7.0
 * @作者：weiyuqiang
 * @创建时间：2019年4月30日
 * @修改记录：
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TotalCheckBatchService extends SvrCBBaseService{

    
    /**
     * 总体核查mapper
     */
    @Autowired
    private ChbTotalCheckMapper chbTotalCheckMapper;
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private SqlSessionTemplate template;
    
    @Autowired
    private ChbMonthReportMapper chbMonthReportMapper;
    
    @Autowired
    private ChbTotalCheckCBMapper chbTotalCheckCBMapper;
    
    @Autowired
    private ChbTotalCheckDetailMapper chbTotalCheckDetailMapper;
    
    @Autowired
    private ChbCheckRuleoutMapper chbCheckRuleoutMapper;
    
    /**
     * 功能:中债报表总体核查入口
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月18日
     * @param batchDate 跑批日
     * void
     * @throws BatchException 
     */
    public void autoCheckRegisterReport(Date batchDate){
        // 待插入的总体核查数据
        List<ChbTotalCheckDto> totalCheckList = new ArrayList<ChbTotalCheckDto>();
        // 带插入的总体核查明细数据
        List<ChbTotalCheckDetailDto> totalCheckDetailList = new ArrayList<ChbTotalCheckDetailDto>();
        // 根据批处理日期进行总体核查
        // 汇率map放至外层，提高性能
        Map<String, BigDecimal> rateMap = new HashMap<>();
        // 1.存续余额与月度报表对比
        checkRegisterReportSubAmtCompareWithMonthReport(totalCheckList,totalCheckDetailList,batchDate,rateMap);
        // 2.产品存续金额和资产持仓偏差情况
        checkRegisterReportPrdSubAndAssetHoldDeviation(totalCheckList,totalCheckDetailList,batchDate,rateMap);
        // 3.募集总量登记明细
        checkRegisterReportPrdRaiseTotal(totalCheckList,totalCheckDetailList,batchDate);
        // 4.月末底层持仓报送情况
        checkRegisterReportMonthBotAssetHold(totalCheckList,totalCheckDetailList,batchDate);
        // 5.资产持仓为负的产品
        checkRegisterReportPrdAssetHold(totalCheckList,totalCheckDetailList,batchDate);
        // 6.现金持仓为负的产品
        checkRegisterReportPrdCashHold(totalCheckList,totalCheckDetailList,batchDate);
        // 7.交易价格偏差过大的资产
        checkRegisterReportTradePrice(totalCheckList,totalCheckDetailList,batchDate);
        // 8.资管计划底层金额偏差过大
        checkRegisterReportAstBotAmt(totalCheckList,totalCheckDetailList,batchDate);
        // 插入数据库
        insertBatch(totalCheckList,totalCheckDetailList,batchDate);
    }
    
    /**
     * 功能:中债报表总体核查-1.存续余额与月度报表对比
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月19日
     * @param totalCheckList 待插入的总体核查数据
     * @param totalCheckDetailList 带插入的总体核查明细数据
     * @param batchDate 跑批日
     * @param rateMap 汇率
     * void
     */
    @SuppressWarnings("deprecation")
    private void checkRegisterReportSubAmtCompareWithMonthReport(List<ChbTotalCheckDto> totalCheckList,
            List<ChbTotalCheckDetailDto> totalCheckDetailList, Date batchDate,Map<String, BigDecimal> rateMap ){
        // 1.存续余额与月度报表对比
        //     批处理生成逻辑：月报的报送日期=批处理日期，计算指标时，对应的查询日期为该月报对应的期数的月末
        // 根据批处理日期查询当天报送的月报
//        List<ChbMonthReport> todayMonthReportList = chbMonthReportMapper.selectMonthReportBySendDate(ChbTotalCheckCBUtils.getLastDayOfLayMonth(batchDate));
        List<ChbMonthReport> todayMonthReportList = chbMonthReportMapper.selectMonthReportBySendDate(batchDate);
        // 生成总体检查数据
        ChbTotalCheckDto totalCheck = geneTotalCheckByCdateAndCheckType(batchDate, ChbTotalCheckCBConstants.CHECKTYPE01);
        // 总体检查合格率
        BigDecimal yield = BigDecimal.ZERO;
        // 根据月报的月末日期进行存续余额与月报对比
        if (CollectionUtils.isNotEmpty(todayMonthReportList)){
            for (ChbMonthReport monthReport : todayMonthReportList){
                // 月末日期=结束日
                Date monthEndDate = monthReport.getEnddate();
                ChbTotalCheckDetailDto totalCheckDetail = geneTotalCheckDetail(totalCheck.getSeqno(),monthEndDate,
                        monthReport.getIssuePartyId(),monthReport.getIssuePartyName());
                // 理财产品只数 = 发行登记表（CHB_PRD_ISSUE_SUB）.产品起始日期<=查询日
                // &&终止登记（CHB_PRD_END_SUB）不存在实际终止日期<=查询日期的记录
                BigDecimal prodSubstainsCount = chbTotalCheckCBMapper.selectProdSubstainsCount(monthEndDate, null,null,null);
                if (prodSubstainsCount == null){
                    prodSubstainsCount = BigDecimal.ZERO;
                }
//                // 查询理财产品只数（人民币产品）
//                BigDecimal prodSubstainsCountCny = chbTotalCheckCBMapper.selectProdSubstainsCount(monthEndDate, DictCodeListExt1.币种_中债_人民币.getValue(),null,null);
//                if (prodSubstainsCountCny == null){
//                    prodSubstainsCountCny = BigDecimal.ZERO;
//                }
                // 理财产品存续金额(万元) = 取业务结束日最接近查询日期（业务结束日<=查询日，日期最大的那条）的产品存续登记表（CHB_PRD_DEAL_SUB）的产品份额，
                // 若某产品在【产品存续登记表】取不到记录则取募集总量登记（CHB_PRD_RAISETOT_SUB）的募集总份额作为该产品的份额
                // 产品份额*查询日最新的单位净值（取不到则为1）得到存续金额，若募集币种不是人民币，则根据参数配置取查询日期最新的
                // 汇率值折算成人民币
                // 理财产品存续金额(万元)
                BigDecimal prdSubstainAmt = BigDecimal.ZERO;
//                // 理财产品存续金额(万元)（人民币产品）
//                BigDecimal prdSubstainAmtCny = BigDecimal.ZERO;
                // 查询理财产品存续金额(万元)方法-旧版
//                List<Map<String,Object>> prdSubstainAmtList = chbTotalCheckCBMapper.selectProdSubstainsAmt(monthEndDate);
                // 查询理财产品存续金额(万元)方法-新版(取自产品头寸)
                List<Map<String,Object>> prdSubstainAmtList = chbTotalCheckCBMapper.selectProdSubstainsAmtFromPosition(monthEndDate);
//                // 区分多币种的产品集合
//                Map<String,String> prodCcyList = new HashMap<String,String>();
                if (CollectionUtils.isNotEmpty(prdSubstainAmtList)) {
                    // 计算理财产品存续金额
                    for (Map<String, Object> map : prdSubstainAmtList){
                        // 产品份额
                        BigDecimal prdQty = ChbTotalCheckCBUtils.getBigDecimal(map.get(ChbTotalCheckCBConstants.PRDQTY));
                        // 产品净值
                        BigDecimal prdValue = ChbTotalCheckCBUtils.getBigDecimal(map.get(ChbTotalCheckCBConstants.PRDVALUE));
                        // 产品募集币种
                        String prdRaiseCcy = (String)map.get(ChbTotalCheckCBConstants.PRDRAISECCY);
//                        // 产品募集币种
//                        String prdid = (String)map.get(ChbTotalCheckCBConstants.PRDID);
//                        // 多币种以逗号隔开
//                        if (prodCcyList.containsKey(prdid)){
//                            prodCcyList.put(prdid, prodCcyList.get(prdid) + GlobalConstant.COMMA + prdRaiseCcy);
//                        } else {
//                            prodCcyList.put(prdid, prdRaiseCcy);
//                        }
                        // 非人民币则计入
                        if (!StringUtil.isEmpty(prdRaiseCcy)){
                            if (!DictCodeListExt1.币种_中债_人民币.getValue().equals(prdRaiseCcy)){
                                // 理财产品存续金额(万元)
                                prdSubstainAmt = prdSubstainAmt.add(ChbCcyConvertUtil.changeCnyMoney(prdRaiseCcy, prdQty.multiply(prdValue).divide(new BigDecimal(10000),6,BigDecimal.ROUND_HALF_UP), monthEndDate, rateMap));
                            } else {
                                // 理财产品存续金额(万元)(人民币产品)
                                prdSubstainAmt = prdSubstainAmt.add(prdQty.multiply(prdValue).divide(new BigDecimal(10000),6,BigDecimal.ROUND_HALF_UP));
                            }
                        } else {
                            logger.error("批处理日期：{},1.存续余额与月度报表对比->产品募集币种为空！",DateUtil.format(batchDate));
                        }
                    }
//                    // 计算理财产品存续人民币
//                    for (Map<String, Object> map : prdSubstainAmtList){
//                        // 产品募集币种
//                        String prdid = (String)map.get(ChbTotalCheckCBConstants.PRDID);
//                        // 多币种不计入
//                        if (StringUtil.isNotEmpty(prodCcyList.get(prdid))
//                                &&  prodCcyList.get(prdid).contains(GlobalConstant.COMMA)){
//                            continue;
//                        }
//                        // 产品份额
//                        BigDecimal prdQty = ChbTotalCheckCBUtils.getBigDecimal(map.get(ChbTotalCheckCBConstants.PRDQTY));
//                        // 产品净值
//                        BigDecimal prdValue = ChbTotalCheckCBUtils.getBigDecimal(map.get(ChbTotalCheckCBConstants.PRDVALUE));
//                        // 产品募集币种
//                        String prdRaiseCcy = (String)map.get(ChbTotalCheckCBConstants.PRDRAISECCY);
//                        // 非人民币则计入
//                        if (!StringUtil.isEmpty(prdRaiseCcy)){
//                            if (DictCodeListExt1.币种_中债_人民币.getValue().equals(prdRaiseCcy)){
//                                // 理财产品存续金额(万元)(人民币产品)
//                                prdSubstainAmtCny = prdSubstainAmtCny.add(prdQty.multiply(prdValue).divide(new BigDecimal(10000),6,BigDecimal.ROUND_HALF_UP));
//                            }
//                        } else {
//                            logger.error("批处理日期：{},1.存续余额与月度报表对比->产品募集币种为空！",DateUtil.format(batchDate));
//                        }
//                    }
                }
                // 月报理财产品只数
                BigDecimal monthProdSubstainsCount = BigDecimal.ZERO;
//                // 月报理财产品只数（人民币产品）
//                BigDecimal monthProdSubstainsCountCny = BigDecimal.ZERO;
                // 月报理财产品存续金额(万元)
                BigDecimal monthPrdSubstainAmt = BigDecimal.ZERO;
//                // 月报理财产品存续金额(万元)（人民币产品）
//                BigDecimal monthPrdSubstainAmtCny = BigDecimal.ZERO;
                // 偏离度
                BigDecimal deviation = BigDecimal.ZERO;
//                // 偏离度（人民币产品）
//                BigDecimal deviationCny = BigDecimal.ZERO;
                // 合格率
                BigDecimal detailYield = BigDecimal.ZERO;
                // 根据月报明细统计相应值
                if (CollectionUtils.isNotEmpty(monthReport.getReportDetailList())){
                    for (ChbMonthReportDetail reportDetail : monthReport.getReportDetailList()){
                        // 月报理财产品只数 = 月报明细表（CHB_MONTH_REPORT_DETAIL）.termid=查询日期所在年月&&propertycode in( 'GMLCCP','SMLCCP')的DURATIONPRODCOUNTS之和
                        // 若查询日期所在年月数据不存在，该字段显示0
                        // 月度统计表存续余额（万元） = 月报明细表（CHB_MONTH_REPORT_DETAIL）.termid=查询日期所在年月&&propertycode in( 'GMLCCP','SMLCCP')的BALANCE+
                        // 保本产品的BALANCE之和
                        // 若查询日期所在年月数据不存在，该字段显示0
                        if (ChbTotalCheckCBConstants.GMLCCP.equals(reportDetail.getPropertycode())
                                || ChbTotalCheckCBConstants.SMLCCP.equals(reportDetail.getPropertycode())
                                || ChbTotalCheckCBConstants.BBLCCP.equals(reportDetail.getPropertycode())){
                            monthProdSubstainsCount = monthProdSubstainsCount.add(new BigDecimal(reportDetail.getDurationprodcounts()));
                            monthPrdSubstainAmt = monthPrdSubstainAmt.add(reportDetail.getBalance().divide(new BigDecimal(10000),6,BigDecimal.ROUND_HALF_UP));
                        }
                        // 月度统计表存续只数(人民币产品) = 月报明细表（CHB_MONTH_REPORT_DETAIL）.termid=查询日期所在年月&&propertycode ='RMB'的DURATIONPRODCOUNTS之和
                        // 若查询日期所在年月数据不存在，该字段显示0
                        // 月报明细表（CHB_MONTH_REPORT_DETAIL）.termid=查询日期所在年月&&propertycode ='RMB'的BALANCE之和
//                        // 若查询日期所在年月数据不存在，该字段显示0
//                        if (ChbTotalCheckCBConstants.RMB.equals(reportDetail.getPropertycode())){
//                            monthProdSubstainsCountCny = monthProdSubstainsCountCny.add(new BigDecimal(reportDetail.getDurationprodcounts()));
//                            monthPrdSubstainAmtCny = monthPrdSubstainAmtCny.add(reportDetail.getBalance().divide(new BigDecimal(10000),6,BigDecimal.ROUND_HALF_UP));
//                        }
                    }
                    // 偏离度 = 绝对值（理财产品存续金额(万元)-月度统计表存续余额）/月度统计表存续余额*100%，计算结果不保留小数位，四舍五入
                    // 若查询日期所在年月数据不存在，该字段显示0
                    if (BigDecimal.ZERO.compareTo(monthPrdSubstainAmt) != 0){
                        deviation = prdSubstainAmt.subtract(monthPrdSubstainAmt).divide(monthPrdSubstainAmt,2,BigDecimal.ROUND_HALF_UP);
                    } else {
                        logger.error("批处理日期：{},月度报表期数：{},1.存续余额与月度报表对比->月度统计表存续余额为0！",DateUtil.format(batchDate),monthReport.getTermid());
                    }
                    deviation = deviation.setScale(2,BigDecimal.ROUND_HALF_UP).abs();
                    // 偏离度(人民币产品) = 绝对值（理财产品存续金额(万元、人民币产品)-月度统计表存续余额（万元、人民币产品））/月度统计表存续余额（万元、人民币产品）*100%，计算结果不保留小数位，四舍五入
//                    // 若查询日期所在年月数据不存在，该字段显示0
//                    if (BigDecimal.ZERO.compareTo(monthPrdSubstainAmtCny) != 0){
//                        deviationCny = prdSubstainAmtCny.subtract(monthPrdSubstainAmtCny).divide(monthPrdSubstainAmtCny,2,BigDecimal.ROUND_HALF_UP);
//                    } else {
//                        logger.error("批处理日期：{},月度报表期数：{},1.存续余额与月度报表对比->月度统计表存续余额（万元、人民币产品）为0！",DateUtil.format(batchDate),monthReport.getTermid());
//                    }
//                    deviationCny = deviationCny.setScale(2,BigDecimal.ROUND_HALF_UP).abs();
                    // 合格率 = 100%-偏离度，2个偏离度，谁小取谁
//                    if (deviation.compareTo(deviationCny) >=0 ){
//                        detailYield = new BigDecimal(1).subtract(deviationCny);
//                    } else {
//                        detailYield = new BigDecimal(1).subtract(deviation);
//                    }
                    detailYield = new BigDecimal(1).subtract(deviation);
                    yield = yield.add(detailYield);
                }
                // 理财产品存续只数
                totalCheckDetail.setPrdNum(prodSubstainsCount);
                // 理财产品存续金额(万元)
                totalCheckDetail.setPrdAmt(prdSubstainAmt);
                // 月报理财产品只数
                totalCheckDetail.setMonthPrdNum(monthProdSubstainsCount);
                // 月报理财产品存续金额(万元)
                totalCheckDetail.setMonthPrdAmt(monthPrdSubstainAmt);
                // 偏离度
                totalCheckDetail.setDeviation(deviation);
//                // 理财产品只数（人民币产品）
//                totalCheckDetail.setPrdNumCny(prodSubstainsCountCny);
//                // 理财产品存续金额(万元)（人民币产品）
//                totalCheckDetail.setPrdAmtCny(prdSubstainAmtCny);
//                // 月报理财产品只数（人民币产品）
//                totalCheckDetail.setMonthPrdNumCny(monthProdSubstainsCountCny);
//                // 月报理财产品存续金额(万元)（人民币产品）
//                totalCheckDetail.setMonthPrdAmtCny(monthPrdSubstainAmtCny);
//                // 偏离度（人民币产品）
//                totalCheckDetail.setDeviationCny(deviationCny);
                // 合格率
                totalCheckDetail.setDetailYield(detailYield);
                // 添加至插入队列
                totalCheckDetailList.add(totalCheckDetail);
            }
            // 总体核查主表合格率计算：暂时为平均值,因为明细表就一条数据所以取平均值没问题
            if (totalCheckDetailList.size() != 0) {
                yield = yield.divide(new BigDecimal(totalCheckDetailList.size()),
                        2,BigDecimal.ROUND_HALF_UP).setScale(2);
            } else if (totalCheckDetailList.size() > 1){
                logger.error("批处理日期：{},1.存续余额与月度报表对比->查询出多条月报记录！",DateUtil.format(batchDate));
            }
        }
        totalCheck.setYield(yield);
        // 添加至插入队列
        totalCheckList.add(totalCheck);
    }

    /**
     * 功能:中债报表总体核查-2.产品存续金额和资产持仓偏差情况
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月19日
     * @param totalCheckList 待插入的总体核查数据
     * @param totalCheckDetailList 带插入的总体核查明细数据
     * @param batchDate 跑批日
     * @param rateMap 汇率
     * void
     */
    @SuppressWarnings("deprecation")
    private void checkRegisterReportPrdSubAndAssetHoldDeviation(List<ChbTotalCheckDto> totalCheckList,
            List<ChbTotalCheckDetailDto> totalCheckDetailList, Date batchDate,Map<String, BigDecimal> rateMap){
        //增加例外情况过滤
        List<ChbCheckRuleout> confs=chbCheckRuleoutMapper.selectDataByCheckTypeAndReprotCode(CheckConfConstants.CHB_CHECK_TYPE_S, CheckConfConstants.CHB_TOTAL_CHECK_02);
        Map<String, List<ChbCheckRuleout>> confsMap = confs.stream().collect(
                 JoyinCollectors.groupingBy(x -> x.getProdId()));
        List<String> prodIgnoreList = new ArrayList<>(confsMap.keySet());
        // 生成总体检查数据
        ChbTotalCheckDto totalCheck = geneTotalCheckByCdateAndCheckType(batchDate, ChbTotalCheckCBConstants.CHECKTYPE02);
        // 总体检查合格率
        BigDecimal yield = BigDecimal.ZERO;
        // 发行机构代码
//        String issueId = CommonUtil.selectBankCode();
        String issueId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
        // 发行机构名称
//        String issueName = CommonUtil.selectBankCodeName();
        String issueName = CacheUtil.getSysParam("SVR_ISSUE_PARTY_NAME");
        // 2.产品存续金额和资产持仓偏差情况
        // 存续的封闭式产品总数： = 1.的理财产品存续只数&&封闭式产品                                           
        BigDecimal prodSubstainsCloseCount = chbTotalCheckCBMapper.selectProdSubstainsCount(batchDate, null,ChbTotalCheckCBConstants.PROD_TYPE_C,null);
        // 偏差过大的封闭式产品：理财产品存续金额÷ (资产持仓总金额-负债持仓总金额+现金持仓金额)，计算结果大于1.25或小于0.75 则认为是偏差过大                                                                        
        //                  理财产品存续金额 = 存续的封闭式产品的募集总量登记（CHB_PRD_RAISETOT_SUB）的募集总份额作为该产品的存续金额&&报送日期<=查询日期       
        //                  资产持仓总金额-负债持仓总金额 = 资产交易每日头寸CHB_ASSET_POSITION.日期等于查询日期的剩余金额&&关联资产要素登记表（CHB_ASSET_INFO_SUB）得到已报送的资产
        //                                          资产-负债 :ASSET_TYPE2 1901 1902 2000是负债其他是资产
        //                                          读：通道是否穿透报送的开关，若为穿透报送，则资产头寸表过滤掉通道资产；若为不穿透报送，则资产头寸表过滤掉通道底层资产      
        //                      现金持仓金额                                      理财产品现金持仓数量=理财产品实际募集金额-交易信息登记(       
        //                                                              资产买入/存入银行存款/拆出和逆回购首期/拆入和正回购到期/投资资产支出/其他支出类交易信息折算人民币金额+      
        //                                                              资产卖出/银行存款到期/拆出和逆回购到期/拆入和正回购首期/投资资产所得收益/其他收入类交易信息折算人民币金额)+       
        //                                                              存续期登记信息（累计申购金额-累计兑付金额）      
        int isDeviationCount = 0;
        List<ChbTotalCheckDetailDto> totalCheckListAdd = new ArrayList<ChbTotalCheckDetailDto>();
        // 查询剩余现金和资产持仓
        List<ChbTotalCheckDetailDto> totalCheckListQuery = queryPrdCash(batchDate,
                ChbTotalCheckCBConstants.PROD_TYPE_C,true,prodIgnoreList);
        // 查询理财产品存续金额(万元)方法-新版(取自产品头寸)
        List<Map<String, Object>> prdSubstainAmtList = chbTotalCheckCBMapper
                .selectProdSubstainsAmtFromPosition(batchDate);
        if (CollectionUtils.isNotEmpty(totalCheckListQuery)){
            for (ChbTotalCheckDetailDto detail : totalCheckListQuery){
                // 初始化值
                detail.setPrdAmt(BigDecimal.ZERO);
                if (CollectionUtils.isNotEmpty(prdSubstainAmtList)) {
                    // 计算理财产品存续金额
                    for (Map<String, Object> map : prdSubstainAmtList) {
                        // 产品份额
                        BigDecimal prdQty = ChbTotalCheckCBUtils
                                .getBigDecimal(map.get(ChbTotalCheckCBConstants.PRDQTY));
                        // 产品净值
                        BigDecimal prdValue = ChbTotalCheckCBUtils
                                .getBigDecimal(map.get(ChbTotalCheckCBConstants.PRDVALUE));
                        // 产品募集币种
                        String prdRaiseCcy = (String) map.get(ChbTotalCheckCBConstants.PRDRAISECCY);
                        // 产品id
                        String prdid = (String)map.get(ChbTotalCheckCBConstants.PRDID);
                        // 只取本产品的份额计算
                        // 非人民币则计入
                        if (StringUtil.isNotEmpty(prdid)
                                && prdid.equals(detail.getPrdId())
                                && StringUtil.isNotEmpty(prdRaiseCcy)) {
                            if (DictCodeListExt1.币种_中债_人民币.getValue().equals(prdRaiseCcy)) {
                                // 理财产品存续金额
                                detail.setPrdAmt(detail.getPrdAmt().add(prdQty.multiply(prdValue)));
                            } else {
                                // 净值折算
                                BigDecimal prdValueCny = ChbCcyConvertUtil.changeCnyMoney(prdRaiseCcy, prdValue, batchDate, rateMap);
                                // 理财产品存续金额
                                detail.setPrdAmt(detail.getPrdAmt().add(prdQty.multiply(prdValueCny)));
                            }
                        }
                    }
                }
                // 理财产品存续金额
                BigDecimal prdAmt = detail.getPrdAmt() == null?BigDecimal.ZERO:detail.getPrdAmt() ;
                // 资产持仓总金额
                BigDecimal prdAstHoldAmt = detail.getPrdAstholdAmt() == null?BigDecimal.ZERO:detail.getPrdAstholdAmt() ;
                // 负债持仓总金额
                BigDecimal prdBalholdAmt = detail.getPrdBalholdAmt() == null?BigDecimal.ZERO:detail.getPrdBalholdAmt() ;
                // 现金持仓总金额
                BigDecimal prdCashholdAmt = detail.getPrdCashholdAmt() == null?BigDecimal.ZERO:detail.getPrdCashholdAmt() ;
                BigDecimal prdAmtCal = prdAstHoldAmt.subtract(prdBalholdAmt).add(prdCashholdAmt);
                if (prdAmtCal.compareTo(BigDecimal.ZERO) == 0){
//                    logger.error("批处理日期：{},2.产品存续金额和资产持仓偏差情况->资产持仓总金额-负债持仓总金额+现金持仓金额为0！",DateUtil.format(batchDate));
//                    totalCheck.setYield(yield);
//                    totalCheckList.add(totalCheck);
//                    return;
                    // 是否偏差过大：否
                    detail.setIsDeviation(DictCodeList.是否_中债_是.getValue());
                    // 偏差过大的产品计入
                    totalCheckListAdd.add(detail);
                    isDeviationCount++;
                } else {
                    BigDecimal rate = prdAmt.divide(prdAmtCal,2,BigDecimal.ROUND_HALF_UP);
                    rate = rate.setScale(2, BigDecimal.ROUND_HALF_UP);
                    // 例外情况不计入统计
                    if (CollectionUtils.isEmpty(prodIgnoreList)
                            || !prodIgnoreList.contains(detail.getPrdId())){
                        if (BigDecimal.valueOf(0.75).compareTo(rate) > 0
                                ||  BigDecimal.valueOf(1.25).compareTo(rate) < 0){
                            // 是否偏差过大：否
                            detail.setIsDeviation(DictCodeList.是否_中债_是.getValue());
                            // 偏差过大的产品计入
                            totalCheckListAdd.add(detail);
                            isDeviationCount++;
                        }
                    } else {
                        prodSubstainsCloseCount = prodSubstainsCloseCount.subtract(new BigDecimal(1));
                    }
                }
            }
            // 生成总体检查数据,检查日期取批处理日期
            geneTotalCheckDetail(totalCheckListAdd,totalCheck.getSeqno(),batchDate,issueId,issueName,batchDate);
            // 合格率%=100%-有偏差过大情况的封闭式产品总数/存续的封闭式产品总数*100%
            if (BigDecimal.ZERO.compareTo(prodSubstainsCloseCount) != 0){
                yield = (new BigDecimal(1)).subtract((new BigDecimal(isDeviationCount)).divide(prodSubstainsCloseCount,2,BigDecimal.ROUND_HALF_UP));
            } else {
                logger.error("批处理日期：{},2.产品存续金额和资产持仓偏差情况->存续的封闭式产品总数为0！",DateUtil.format(batchDate));
            }
            // 添加至插入队列
            totalCheckDetailList.addAll(totalCheckListAdd);
        }
        // 添加至插入队列
        totalCheck.setYield(yield);
        totalCheckList.add(totalCheck);
    }
    
    /**
     * 功能:中债报表总体核查-3.募集总量登记明细
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月23日
     * @param totalCheckList 待插入的总体核查数据
     * @param totalCheckDetailList 带插入的总体核查明细数据
     * @param batchDate 跑批日
     * void
     */
    private void checkRegisterReportPrdRaiseTotal(List<ChbTotalCheckDto> totalCheckList,
            List<ChbTotalCheckDetailDto> totalCheckDetailList, Date batchDate){
        //增加例外情况过滤
        List<ChbCheckRuleout> confs=chbCheckRuleoutMapper.selectDataByCheckTypeAndReprotCode(CheckConfConstants.CHB_CHECK_TYPE_S, CheckConfConstants.CHB_TOTAL_CHECK_03);
        Map<String, List<ChbCheckRuleout>> confsMap = confs.stream().collect(
                 JoyinCollectors.groupingBy(x -> x.getProdId()));
        List<String> prodIgnoreList = new ArrayList<>(confsMap.keySet());
        // 发行机构代码
//        String issueId = CommonUtil.selectBankCode();
        String issueId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
        // 发行机构名称
//        String issueName = CommonUtil.selectBankCodeName();
        String issueName = CacheUtil.getSysParam("SVR_ISSUE_PARTY_NAME");
        // 生成总体检查数据
        ChbTotalCheckDto totalCheck = geneTotalCheckByCdateAndCheckType(batchDate, ChbTotalCheckCBConstants.CHECKTYPE03);
        // 总体检查合格率
        BigDecimal yield = BigDecimal.ZERO;
        // 已报送数量
        int isRegNum = 0;
        // 应报送数量
        int shouldRegNum = 0;
        List<ChbTotalCheckDetailDto> totalCheckListQuery = chbTotalCheckCBMapper.selectPrdRaiseTotal(batchDate,null);
        List<ChbTotalCheckDetailDto> totalCheckListAdd = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(totalCheckListQuery)){
            shouldRegNum = totalCheckListQuery.size();
            for (ChbTotalCheckDetailDto detail : totalCheckListQuery){
                // 募集总量已登记则计入已报送数量
                // 例外情况不计入统计
                if (CollectionUtils.isEmpty(prodIgnoreList)
                        || !prodIgnoreList.contains(detail.getPrdId())){
                    if (DictCodeList.是否_中债_是.getValue().equals(detail.getIsReg())){
                        isRegNum++;
                    }
                    totalCheckListAdd.add(detail);
                } else {
                    shouldRegNum--;
                }
            }
            // 生成总体检查数据,检查日期取批处理日期
            geneTotalCheckDetail(totalCheckListAdd,totalCheck.getSeqno(),batchDate,issueId,issueName,null);
            // 合格率%=已报送数量/应报送数量*100%
            if (shouldRegNum != 0){
                yield = (new BigDecimal(isRegNum)).divide(new BigDecimal(shouldRegNum),2,BigDecimal.ROUND_HALF_UP);
            } else {
                logger.error("批处理日期：{},3.募集总量登记明细->应报送数量为0！",DateUtil.format(batchDate));
            }
            // 添加至插入队列
            totalCheckDetailList.addAll(totalCheckListAdd);
        }
        // 添加至插入队列
        totalCheck.setYield(yield);
        totalCheckList.add(totalCheck);
    }
    

    /**
     * 功能:中债报表总体核查-4.月末底层持仓报送情况
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月23日
     * @param totalCheckList 待插入的总体核查数据
     * @param totalCheckDetailList 带插入的总体核查明细数据
     * @param batchDate 跑批日
     * void
     */
    private void checkRegisterReportMonthBotAssetHold(List<ChbTotalCheckDto> totalCheckList,
            List<ChbTotalCheckDetailDto> totalCheckDetailList, Date batchDate){
        // 发行机构代码
//        String issueId = CommonUtil.selectBankCode();
        String issueId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
        // 发行机构名称
//        String issueName = CommonUtil.selectBankCodeName();
        String issueName = CacheUtil.getSysParam("SVR_ISSUE_PARTY_NAME");
        // 生成总体检查数据
        ChbTotalCheckDto totalCheck = geneTotalCheckByCdateAndCheckType(batchDate, ChbTotalCheckCBConstants.CHECKTYPE04);
        // 总体检查合格率
        BigDecimal yield = BigDecimal.ZERO;
        // 实报底层持仓资产数
        int isRegNum = 0;
        // 应报底层持仓资产数
        int shouldRegNum = 0;
        List<ChbTotalCheckDetailDto> totalCheckListAdd = chbTotalCheckCBMapper.selectMonthBotAssetHold(
                batchDate,ChbTotalCheckCBUtils.getLastDayOfLayMonth(batchDate));
        if (CollectionUtils.isNotEmpty(totalCheckListAdd)){
            shouldRegNum = totalCheckListAdd.size();
            for (ChbTotalCheckDetailDto detail : totalCheckListAdd){
                // 底层持仓已登记则计入已报送数量
                if (DictCodeList.是否_中债_是.getValue().equals(detail.getIsReg())){
                    isRegNum++;
                } else {
                    // 为否置空
                    detail.setBotIssueAmt(null);
                    detail.setBotIssueNum(null);
                    detail.setBotUninvestCash(null);
                    detail.setBotAssetAmt(null);
                    detail.setBotDebtAmt(null);
                    detail.setIsDeviation(null);
                }
                // 检查日期为月末
                if (detail.getCheckDate() != null){
                    detail.setCheckDate(ChbTotalCheckCBUtils.getLastDayOfMonth(detail.getCheckDate()));
                }
                // 是否偏差过大：否
                detail.setIsDeviation(DictCodeList.是否_中债_否.getValue());
            }
            // 生成总体检查数据
            geneTotalCheckDetail(totalCheckListAdd,totalCheck.getSeqno(),null,issueId,issueName,null);
            // 合格率%=实报底层持仓资产数/应报底层持仓资产数*100%
            if (shouldRegNum != 0){
                yield = (new BigDecimal(isRegNum)).divide(new BigDecimal(shouldRegNum),2,BigDecimal.ROUND_HALF_UP);
            } else {
                logger.error("批处理日期：{},4.月末底层持仓报送情况->应报底层持仓资产数为0！",DateUtil.format(batchDate));
            }
            // 添加至插入队列
            totalCheckDetailList.addAll(totalCheckListAdd);
        }
        // 添加至插入队列
        totalCheck.setYield(yield);
        totalCheckList.add(totalCheck);
    }

    /**
     * 功能:中债报表总体核查-5.资产持仓为负的产品
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月23日
     * @param totalCheckList 待插入的总体核查数据
     * @param totalCheckDetailList 带插入的总体核查明细数据
     * @param batchDate 跑批日
     * void
     */
    private void checkRegisterReportPrdAssetHold(List<ChbTotalCheckDto> totalCheckList,
            List<ChbTotalCheckDetailDto> totalCheckDetailList, Date batchDate){
        //增加例外情况过滤
        List<ChbCheckRuleout> confs=chbCheckRuleoutMapper.selectDataByCheckTypeAndReprotCode(CheckConfConstants.CHB_CHECK_TYPE_S, CheckConfConstants.CHB_TOTAL_CHECK_05);
        Map<String, List<ChbCheckRuleout>> confsMap = confs.stream().collect(
                 JoyinCollectors.groupingBy(x -> x.getProdId()));
        List<String> prodIgnoreList = new ArrayList<>(confsMap.keySet());
        // 发行机构代码
//        String issueId = CommonUtil.selectBankCode();
        String issueId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
        // 发行机构名称
//        String issueName = CommonUtil.selectBankCodeName();
        String issueName = CacheUtil.getSysParam("SVR_ISSUE_PARTY_NAME");
        // 生成总体检查数据
        ChbTotalCheckDto totalCheck = geneTotalCheckByCdateAndCheckType(batchDate, ChbTotalCheckCBConstants.CHECKTYPE05);
        // 总体检查合格率
        BigDecimal yield = BigDecimal.ZERO;
        // 资产持仓为负产品数
        int isRegNum = 0;
        Map<String,String> isRegPordMap = new HashMap<String,String>();
        // 存续产品数
        int shouldRegNum = 0;
        Map<String,String> shouldRegPordMap = new HashMap<String,String>();
        List<ChbTotalCheckDetailDto> totalCheckListAdd = chbTotalCheckCBMapper.selectAssetHoldLoad(batchDate,prodIgnoreList);
        if (CollectionUtils.isNotEmpty(totalCheckListAdd)){
            for (ChbTotalCheckDetailDto detail : totalCheckListAdd){
                if (detail.getAstHoldNum() == null){
                    detail.setAstHoldNum(BigDecimal.ZERO);
                }
                // 资产持仓<0
                // 例外情况不计入统计
                if (CollectionUtils.isEmpty(prodIgnoreList)
                        || !prodIgnoreList.contains(detail.getPrdId())){
                    if (BigDecimal.ZERO.compareTo(detail.getAstHoldNum()) > 0){
                        if (StringUtil.isEmpty(isRegPordMap.get(detail.getPrdId()))){
                            isRegNum++;
                            isRegPordMap.put(detail.getPrdId(),detail.getPrdId());
                        }
                        // 生成总体检查数据
                        geneTotalCheckDetail(detail,totalCheck.getSeqno(),batchDate,issueId,issueName,null);
                        // 添加至插入队列
                        totalCheckDetailList.add(detail);
                    }
                    // 存续产品
                    if (StringUtil.isEmpty(shouldRegPordMap.get(detail.getPrdId()))
                            && DictCodeList.是否_中债_是.getValue().equals(detail.getIsIssueReg())){
                        shouldRegNum++;
                        shouldRegPordMap.put(detail.getPrdId(),detail.getPrdId());
                    }
                }
            }
            // 合格率%=100%-存在持仓为负的理财产品只数/存续产品数*100%
            if (shouldRegNum != 0){
                yield = (new BigDecimal(1)).subtract((new BigDecimal(isRegNum)).divide(new BigDecimal(shouldRegNum),2,BigDecimal.ROUND_HALF_UP));
            } else {
                logger.error("批处理日期：{},5.资产持仓为负的产品->存续产品数为0！",DateUtil.format(batchDate));
            }
        }
        // 添加至插入队列
        totalCheck.setYield(yield);
        totalCheckList.add(totalCheck);
    }

    /**
     * 功能:中债报表总体核查-6.现金持仓为负的产品
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月23日
     * @param totalCheckList 待插入的总体核查数据
     * @param totalCheckDetailList 带插入的总体核查明细数据
     * @param batchDate 跑批日
     * void
     */
    private void checkRegisterReportPrdCashHold(List<ChbTotalCheckDto> totalCheckList,
            List<ChbTotalCheckDetailDto> totalCheckDetailList, Date batchDate){
        //增加例外情况过滤
        List<ChbCheckRuleout> confs=chbCheckRuleoutMapper.selectDataByCheckTypeAndReprotCode(CheckConfConstants.CHB_CHECK_TYPE_S, CheckConfConstants.CHB_TOTAL_CHECK_06);
        Map<String, List<ChbCheckRuleout>> confsMap = confs.stream().collect(
                 JoyinCollectors.groupingBy(x -> x.getProdId()));
        List<String> prodIgnoreList = new ArrayList<>(confsMap.keySet());
        // 发行机构代码
//        String issueId = CommonUtil.selectBankCode();
        String issueId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
        // 发行机构名称
//        String issueName = CommonUtil.selectBankCodeName();
        String issueName = CacheUtil.getSysParam("SVR_ISSUE_PARTY_NAME");
        // 生成总体检查数据
        ChbTotalCheckDto totalCheck = geneTotalCheckByCdateAndCheckType(batchDate, ChbTotalCheckCBConstants.CHECKTYPE06);
        // 总体检查合格率
        BigDecimal yield = BigDecimal.ZERO;
        // 现金持仓为负产品数
        int isRegNum = 0;
        Map<String,String> isRegPordMap = new HashMap<String,String>();
        // 存续产品数
        int shouldRegNum = 0;
        Map<String,String> shouldRegPordMap = new HashMap<String,String>();
        // 查询理财产品存续金额(万元)方法-新版(取自产品头寸)
        // 存续期累计金额,支持多币种
        List<Map<String,String>> prdDealList = chbTotalCheckCBMapper.selectPrdDealAmt(batchDate);
        Map<String,BigDecimal> prdDealMap = new HashMap<String,BigDecimal>();
        for (Map<String,String> prdDeal : prdDealList){
            String ccyPurCash = prdDeal.get(ChbTotalCheckCBConstants.CCYCASH);
            String prodid = prdDeal.get(ChbTotalCheckCBConstants.PRDID);
            // 过滤
            if (CollectionUtils.isEmpty(prodIgnoreList)
                    || !prodIgnoreList.contains(prodid)){
                BigDecimal prdDealAmt = BigDecimal.ZERO;
                if (StringUtil.isNotEmpty(ccyPurCash)){
                    // 以分号隔开多币种
                    List<String> dealCcyList = StringUtil.splitToList(GlobalConstant.SEMICOLON,ccyPurCash );
                    if (CollectionUtils.isNotEmpty(dealCcyList)){
                        for (String dealCcy : dealCcyList){ 
                            // 以逗号隔开币种、申购、兑付、收益
                            List<String> dealDetailList = StringUtil.splitToList(GlobalConstant.COMMA,dealCcy );
                            if (CollectionUtils.isNotEmpty(dealDetailList)
                                    && dealDetailList.size() >=3){
                                // 0币种、1申购、2兑付、3收益
                                BigDecimal purAmt = ChbTotalCheckCBUtils.getBigDecimal(dealDetailList.get(1));
                                BigDecimal redAmt = ChbTotalCheckCBUtils.getBigDecimal(dealDetailList.get(2));
                                prdDealAmt = prdDealAmt.add(purAmt).subtract(redAmt);
                            }
                        }
                    }
                }
                // 存在该产品则累加
                if (prdDealMap.containsKey(prodid)){
                    BigDecimal prdDealSumAmt = prdDealMap.get(prodid).add(prdDealAmt);
                    prdDealMap.put(prodid,prdDealSumAmt);
                } else {
                    prdDealMap.put(prodid, prdDealAmt);
                }
            }
        }
        // 查询剩余现金和资产持仓
        List<ChbTotalCheckDetailDto> totalCheckListQuery = queryPrdCash(batchDate,
                ChbTotalCheckCBConstants.PROD_TYPE_C,false,prodIgnoreList);
        List<ChbTotalCheckDetailDto> totalCheckListAdd = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(totalCheckListQuery)){
            for (ChbTotalCheckDetailDto detail : totalCheckListQuery){
                // 现金持仓加上存续金额
                BigDecimal prdDealAmt = prdDealMap.get(detail.getPrdId());
                if (prdDealAmt == null){
                    prdDealAmt = BigDecimal.ZERO;
                }
                detail.setPrdCashholdAmt(detail.getPrdCashholdAmt().add(prdDealAmt));
                // 现金持仓<0
                if (CollectionUtils.isEmpty(prodIgnoreList)
                        || !prodIgnoreList.contains(detail.getPrdId())){
                    if (BigDecimal.ZERO.compareTo(detail.getPrdCashholdAmt()) > 0){
                        if (StringUtil.isEmpty(isRegPordMap.get(detail.getPrdId()))){
                            isRegNum++;
                            isRegPordMap.put(detail.getPrdId(),detail.getPrdId());
                        }
                        totalCheckListAdd.add(detail);
                    }
                    // 存续产品
                    if (StringUtil.isEmpty(shouldRegPordMap.get(detail.getPrdId()))
                            && DictCodeList.是否_中债_是.getValue().equals(detail.getIsIssueReg())){
                        shouldRegNum++;
                        shouldRegPordMap.put(detail.getPrdId(),detail.getPrdId());
                    }
                }
            }
            // 生成总体检查数据
            geneTotalCheckDetail(totalCheckListAdd,totalCheck.getSeqno(),batchDate,issueId,issueName,null);
            // 合格率%=100%-存在持仓为负的理财产品只数/存续产品数*100%
            if (shouldRegNum != 0){
                yield = (new BigDecimal(1)).subtract((new BigDecimal(isRegNum)).divide(new BigDecimal(shouldRegNum),2,BigDecimal.ROUND_HALF_UP));
            } else {
                logger.error("批处理日期：{},6.现金持仓为负的产品->存续产品数为0！",DateUtil.format(batchDate));
            }
            // 添加至插入队列
            totalCheckDetailList.addAll(totalCheckListAdd);
        }
        // 添加至插入队列
        totalCheck.setYield(yield);
        totalCheckList.add(totalCheck);
    }

    /**
     * 功能:中债报表总体核查-7.交易价格偏差过大的资产
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月23日
     * @param totalCheckList 待插入的总体核查数据
     * @param totalCheckDetailList 带插入的总体核查明细数据
     * @param batchDate 跑批日
     * void
     */
    private void checkRegisterReportTradePrice(List<ChbTotalCheckDto> totalCheckList,
            List<ChbTotalCheckDetailDto> totalCheckDetailList, Date batchDate){
        //增加例外情况过滤
        List<ChbCheckRuleout> confs=chbCheckRuleoutMapper.selectDataByCheckTypeAndReprotCode(CheckConfConstants.CHB_CHECK_TYPE_S, CheckConfConstants.CHB_TOTAL_CHECK_07);
        Map<String, List<ChbCheckRuleout>> confsMap = confs.stream().collect(
                 JoyinCollectors.groupingBy(x -> x.getProdId()));
        List<String> prodIgnoreList = new ArrayList<>(confsMap.keySet());
        // 发行机构代码
//        String issueId = CommonUtil.selectBankCode();
        String issueId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
        // 发行机构名称
//        String issueName = CommonUtil.selectBankCodeName();
        String issueName = CacheUtil.getSysParam("SVR_ISSUE_PARTY_NAME");
        // 生成总体检查数据
        ChbTotalCheckDto totalCheck = geneTotalCheckByCdateAndCheckType(batchDate, ChbTotalCheckCBConstants.CHECKTYPE07);
        // 总体检查合格率
        BigDecimal yield = BigDecimal.ZERO;
        // 交易价格偏差过大资产
        int isRegNum = 0;
        Map<String,String> isRegPordMap = new HashMap<String,String>();
        // 存续产品数
        int shouldRegNum = 0;
        Map<String,String> shouldRegPordMap = new HashMap<String,String>();
        List<ChbTotalCheckDetailDto> totalCheckListQuery = chbTotalCheckCBMapper.selectTradePrice(batchDate,prodIgnoreList);
        List<ChbTotalCheckDetailDto> totalCheckListAdd = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(totalCheckListQuery)){
            for (ChbTotalCheckDetailDto detail : totalCheckListQuery){
                // 同一个产品代码、同一个行内资产/负债编码的最高交易单价大于9倍最低交易单价
                if (CollectionUtils.isEmpty(prodIgnoreList) || !prodIgnoreList.contains(detail.getPrdId())) {
                    if (detail.getAstMaxUprice() != null && detail.getAstMinUprice() != null) {
                        if (detail.getAstMaxUprice()
                                .compareTo(detail.getAstMinUprice().multiply(new BigDecimal(9))) > 0){
                            if (StringUtil.isEmpty(isRegPordMap.get(detail.getPrdId()))) {
                                isRegNum++;
                                isRegPordMap.put(detail.getPrdId(),detail.getPrdId());
                            }
                        totalCheckListAdd.add(detail);
                        }
                    } else {
                        logger.error("批处理日期：{},7.交易价格偏差过大的资产->产品最大（小）成交价格为空！", DateUtil.format(batchDate));
                        totalCheck.setYield(yield);
                        totalCheckList.add(totalCheck);
                        return;
                    }
                    // 存续产品
                    if (StringUtil.isEmpty(shouldRegPordMap.get(detail.getPrdId()))
                            && DictCodeList.是否_中债_是.getValue().equals(detail.getIsIssueReg())){
                        shouldRegNum++;
                        shouldRegPordMap.put(detail.getPrdId(),detail.getPrdId());
                    }
                }
            }
            // 生成总体检查数据
            geneTotalCheckDetail(totalCheckListAdd,totalCheck.getSeqno(),batchDate,issueId,issueName,null);
            // 合格率%=100%-交易价格偏差过大资产对应的产品数/登记系统存续产品数*100%
            if (shouldRegNum != 0){
                yield = (new BigDecimal(1)).subtract((new BigDecimal(isRegNum)).divide(new BigDecimal(shouldRegNum),2,BigDecimal.ROUND_HALF_UP));
            } else {
                logger.error("批处理日期：{},7.交易价格偏差过大的资产->存续产品数为0！",DateUtil.format(batchDate));
            }
            // 添加至插入队列
            totalCheckDetailList.addAll(totalCheckListAdd);
        }
        // 添加至插入队列
        totalCheck.setYield(yield);
        totalCheckList.add(totalCheck);
    }
    
    /**
     * 功能:中债报表总体核查-8.资管计划底层金额偏差过大
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月23日
     * @param totalCheckList 待插入的总体核查数据
     * @param totalCheckDetailList 带插入的总体核查明细数据
     * @param batchDate 跑批日
     * void
     */
    private void checkRegisterReportAstBotAmt(List<ChbTotalCheckDto> totalCheckList,
            List<ChbTotalCheckDetailDto> totalCheckDetailList, Date batchDate){
     // 发行机构代码
//        String issueId = CommonUtil.selectBankCode();
        String issueId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
        // 发行机构名称
//        String issueName = CommonUtil.selectBankCodeName();
        String issueName = CacheUtil.getSysParam("SVR_ISSUE_PARTY_NAME");
        // 生成总体检查数据
        ChbTotalCheckDto totalCheck = geneTotalCheckByCdateAndCheckType(batchDate, ChbTotalCheckCBConstants.CHECKTYPE08);
        // 总体检查合格率
        BigDecimal yield = BigDecimal.ZERO;
        // 计划发行金额与资产市值偏差过大数量
        int isRegNum = 0;
        // 报送底层的计划数
        int shouldRegNum = 0;
        List<ChbTotalCheckDetailDto> totalCheckListAdd = chbTotalCheckCBMapper.selectAstBotAmt(batchDate
                ,ChbTotalCheckCBUtils.getLastDayOfLayMonth(batchDate));
        if (CollectionUtils.isNotEmpty(totalCheckListAdd)){
            shouldRegNum = totalCheckListAdd.size();
            for (ChbTotalCheckDetailDto detail : totalCheckListAdd){
                // 计划发行金额/资产市值 计算结果大于1.5小于0.5的资产只数
                // 计划发行金额 :ISSUE_AMT
                // 资产市值：未投资头寸UNINVEST_CASH + 市值
                BigDecimal botUninvestCash = detail.getBotUninvestCash() == null?BigDecimal.ZERO:detail.getBotUninvestCash();
                BigDecimal prdAstholdAmt = detail.getBotAssetAmt() == null?BigDecimal.ZERO:detail.getBotAssetAmt();
                BigDecimal prdBalholdAmt = detail.getBotDebtAmt() == null?BigDecimal.ZERO:detail.getBotDebtAmt();
                BigDecimal assetPrice = botUninvestCash.add(prdAstholdAmt).subtract(prdBalholdAmt);
                if (BigDecimal.ZERO.compareTo(assetPrice) != 0){
                    BigDecimal calAmt = detail.getBotIssueAmt() == null?BigDecimal.ZERO:detail.getBotIssueAmt();
                    calAmt = calAmt.divide(assetPrice,2,BigDecimal.ROUND_HALF_UP);
                    if ( ( BigDecimal.valueOf(1.5)).compareTo(calAmt) < 0
                            || ( BigDecimal.valueOf(0.5)).compareTo(calAmt) > 0){
                        isRegNum++;
                        detail.setIsDeviation(DictCodeList.是否_中债_是.getValue());
                        // 生成总体检查数据
                        geneTotalCheckDetail(detail,totalCheck.getSeqno(),batchDate,issueId,issueName,null);
                        // 添加至插入队列
                        totalCheckDetailList.add(detail);
                    }
                } else {
                    logger.error("批处理日期：{},8.资管计划底层金额偏差过大->资产市值为零！",DateUtil.format(batchDate));
                    totalCheck.setYield(yield);
                    totalCheckList.add(totalCheck);
                    return;
                }
            }
        }
        // 合格率%=100%-计划发行金额与资产市值偏差过大数量/报送底层的计划数*100%
        if (shouldRegNum != 0){
            yield = (new BigDecimal(1)).subtract((new BigDecimal(isRegNum)).divide(new BigDecimal(shouldRegNum),2,BigDecimal.ROUND_HALF_UP));
        } else {
            logger.error("批处理日期：{},8.资管计划底层金额偏差过大产->报送底层的计划数为0！",DateUtil.format(batchDate));
        }
        // 添加至插入队列
        totalCheck.setYield(yield);
        totalCheckList.add(totalCheck);
    }
    
    /**
     * 功能:根据统计日期、检查类型生成总体检查数据
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月19日
     * @param cdate 统计日期
     * @param checkType 检查类型
     * @return 
     * ChbTotalCheckDto
     */
    private ChbTotalCheckDto geneTotalCheckByCdateAndCheckType(Date cdate,String checkType){
        ChbTotalCheckDto totalCheck = new ChbTotalCheckDto();
        totalCheck.setSeqno(IDGenerator.generate(36));
        totalCheck.setCdate(cdate);
        totalCheck.setCheckType(checkType);
        totalCheck.setOrgCode(CommonUtil.getOrgCode());
        totalCheck.setCreateDept(CommonUtil.getDeptCode());
        totalCheck.setCreateUser(CommonUtil.getUserName());
        totalCheck.setUpdateUser(CommonUtil.getUserName());
        return totalCheck;
    }
    
    /**
     * 功能:根据统计日期、检查类型生成总体检查明细数据
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月19日
     * @param cdate 统计日期
     * @param checkType 检查类型
     * @return 
     * ChbTotalCheckDto
     */
    private void geneTotalCheckDetail(List<ChbTotalCheckDetailDto> totalCheckList,String totalSeqno ,Date checkDate,
            String issuePartyId, String issuePartyName,Date holdDate){
        if (CollectionUtils.isNotEmpty(totalCheckList)){
            for (ChbTotalCheckDetailDto totalCheckDetail : totalCheckList){
                totalCheckDetail.setSeqno(IDGenerator.generate(36));
                if (StringUtil.isNotEmpty(totalSeqno)){
                    totalCheckDetail.setTotalseqno(totalSeqno);
                }
                if (holdDate != null){
                    totalCheckDetail.setPrdHolddate(holdDate);
                }
                if (checkDate != null){
                    totalCheckDetail.setCheckDate(checkDate);
                }
                if (StringUtil.isNotEmpty(issuePartyId)){
                    totalCheckDetail.setIssuePartyId(issuePartyId);
                }
                if (StringUtil.isNotEmpty(issuePartyName)){
                    totalCheckDetail.setIssuePartyName(issuePartyName);
                }
                totalCheckDetail.setOrgCode(CommonUtil.getOrgCode());
                totalCheckDetail.setCreateDept(CommonUtil.getDeptCode());
                totalCheckDetail.setCreateUser(CommonUtil.getUserName());
                totalCheckDetail.setCreateTime(DateUtil.now());
                totalCheckDetail.setUpdateTime(null);
                totalCheckDetail.setUpdateUser(null);
            }
        }
        
    }

    /**
     * 功能:根据统计日期、检查类型生成总体检查明细数据
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月19日
     * @param cdate 统计日期
     * @param checkType 检查类型
     * @return 
     * ChbTotalCheckDto
     */
    private void geneTotalCheckDetail(ChbTotalCheckDetailDto totalCheckDetail,String totalSeqno ,Date checkDate,
            String issuePartyId, String issuePartyName,Date holdDate){
        totalCheckDetail.setSeqno(IDGenerator.generate(36));
        if (StringUtil.isNotEmpty(totalSeqno)){
            totalCheckDetail.setTotalseqno(totalSeqno);
        }
        if (holdDate != null){
            totalCheckDetail.setPrdHolddate(holdDate);
        }
        if (checkDate != null){
            totalCheckDetail.setCheckDate(checkDate);
        }
        if (StringUtil.isNotEmpty(issuePartyId)){
            totalCheckDetail.setIssuePartyId(issuePartyId);
        }
        if (StringUtil.isNotEmpty(issuePartyName)){
            totalCheckDetail.setIssuePartyName(issuePartyName);
        }
        totalCheckDetail.setOrgCode(CommonUtil.getOrgCode());
        totalCheckDetail.setCreateDept(CommonUtil.getDeptCode());
        totalCheckDetail.setCreateUser(CommonUtil.getUserName());
        totalCheckDetail.setCreateTime(DateUtil.now());
        totalCheckDetail.setUpdateTime(null);
        totalCheckDetail.setUpdateUser(null);
    }
    
    /**
     * 功能:根据总体核查的seqno、检查日期、发行机构代码、发行机构名称生成总体核查明细记录
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月19日
     * @param totalSeqno
     * @param checkDate
     * @param issuePartyId
     * @param issuePartyName
     * @return 
     * ChbTotalCheckDetailDto
     */
    private ChbTotalCheckDetailDto geneTotalCheckDetail(String totalSeqno ,Date checkDate,
            String issuePartyId, String issuePartyName){
        ChbTotalCheckDetailDto totalCheckDetail = new ChbTotalCheckDetailDto();
        totalCheckDetail.setSeqno(IDGenerator.generate(36));
        if (StringUtil.isNotEmpty(totalSeqno)){
            totalCheckDetail.setTotalseqno(totalSeqno);
        }
        if (checkDate != null){
            totalCheckDetail.setCheckDate(checkDate);
        }
        if (StringUtil.isNotEmpty(issuePartyId)){
            totalCheckDetail.setIssuePartyId(issuePartyId);
        }
        if (StringUtil.isNotEmpty(issuePartyName)){
            totalCheckDetail.setIssuePartyName(issuePartyName);
        }
        totalCheckDetail.setOrgCode(CommonUtil.getOrgCode());
        totalCheckDetail.setCreateDept(CommonUtil.getDeptCode());
        totalCheckDetail.setCreateUser(CommonUtil.getUserName());
        totalCheckDetail.setCreateTime(DateUtil.now());
        totalCheckDetail.setUpdateTime(null);
        totalCheckDetail.setUpdateUser(null);
        return totalCheckDetail;
    }
    
    /**
     * 功能:带提交的方式单条处理数据结果，入库
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年3月7日
     * @param cdate 统计日期 
     * @param totalCheckList 中债总体检查
     * @param totalCheckDetailList 中债总体检查明细
     * void
     */
    private void insertBatch(List<ChbTotalCheckDto> totalCheckList,
            List<ChbTotalCheckDetailDto> totalCheckDetailList,Date cdate){
        // 先删除明细表数据再删除主表数据
        chbTotalCheckDetailMapper.deletTotalCheckDeatailByCdate(cdate);
        chbTotalCheckMapper.deletTotalCheckByCdate(cdate);
        // 插入主表数据
        if (CollectionUtils.isNotEmpty(totalCheckList)) {
            int count = 0;
            SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            try{
                ChbTotalCheckMapper batchMapper =  session.getMapper(ChbTotalCheckMapper.class);
                for(ChbTotalCheckDto asset : totalCheckList){
                    batchMapper.insert(asset);
                    count++;
                    if(count%1000==0){
                        session.commit();
                        // 清理缓存，防止溢出
                        session.clearCache();
                    }
                }
                session.commit();
                // 清理缓存，防止溢出
                session.clearCache();
            }
            catch(Exception e){
            	logger.error(e.getMessage(),e);
                session.rollback();
                throw new BusinessException(e);
            }
            finally {
                session.close();
            }
        }
        // 插入总体核查你明细表数据
        if (CollectionUtils.isNotEmpty(totalCheckDetailList)) {
            int count = 0;
            SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            try{
                ChbTotalCheckDetailMapper batchMapper =  session.getMapper(ChbTotalCheckDetailMapper.class);
                for(ChbTotalCheckDetailDto asset : totalCheckDetailList){
                    batchMapper.insert(asset);
                    count++;
                    if(count%1000==0){
                        session.commit();
                        // 清理缓存，防止溢出
                        session.clearCache();
                    }
                }
                session.commit();
                // 清理缓存，防止溢出
                session.clearCache();
            }
            catch(Exception e){
            	logger.error(e.getMessage(),e);
                session.rollback();
                throw new BusinessException(e);
            }
            finally {
                session.close();
            }
        }
    }
    
    /**
     * 功能:查询剩余现金持仓和资产持仓
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年5月28日
     * @param batchDate 跑批日
     * @param openType 开放模式
     * @param isCalAssetHold 是否计算资产持仓
     * @return 
     * List<ChbTotalCheckDetailDto>
     */
    @SuppressWarnings("deprecation")
    private List<ChbTotalCheckDetailDto> queryPrdCash(Date batchDate,String openType,
            boolean isCalAssetHold,List<String> prodIgnoreList){
        // 用于去重key:prodid,value:明细数据
        Map<String,ChbTotalCheckDetailDto> holdMap = new HashMap<>();
        // 是否穿透报送
//        String isChlBtm = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_CHL_BTM);
        // 汇率map放至外层，提高性能
        Map<String, BigDecimal> rateMap = new HashMap<>();
        // 查询初始现金持仓
        List<ChbTotalCheckDetailDto> prdInitList = chbTotalCheckCBMapper.selectPrdInitAmt(batchDate,openType,prodIgnoreList);
        for (ChbTotalCheckDetailDto detail : prdInitList){
            // 需要新覆盖的数据
            ChbTotalCheckDetailDto addDetail = new ChbTotalCheckDetailDto();
            if (holdMap.get(detail.getPrdId()) != null){
                // 如果已存在则用这条加上条
                ChbTotalCheckDetailDto ystDetail = holdMap.get(detail.getPrdId());
                // 产品id
                addDetail.setPrdId(ystDetail.getPrdId());
                // 计算资产持仓才加运作模式
                if (isCalAssetHold){
                    // 运作模式
                    addDetail.setOperateMode(ystDetail.getOperateMode());
                    // 资产持仓金额
                    addDetail.setPrdAstholdAmt(ystDetail.getPrdAstholdAmt());
                    // 负债持仓总金额
                    addDetail.setPrdBalholdAmt(ystDetail.getPrdBalholdAmt());
                }
                // 现金持仓总金额
                addDetail.setPrdCashholdAmt(ystDetail.getPrdCashholdAmt());
            } else {
                // 产品id
                addDetail.setPrdId(detail.getPrdId());
                // 计算资产持仓才加运作模式
                if (isCalAssetHold){
                    // 运作模式
                    addDetail.setOperateMode(detail.getOperateMode());
                    // 资产持仓金额
                    addDetail.setPrdAstholdAmt(BigDecimal.ZERO);
                    // 负债持仓总金额
                    addDetail.setPrdBalholdAmt(BigDecimal.ZERO);
                }
                // 现金持仓总金额
                addDetail.setPrdCashholdAmt(BigDecimal.ZERO);
            }
            // 初始现金持仓金额
            BigDecimal prdInitAmt = detail.getPrdInitAmt();
            // 初始现金持仓币种
            String initCcy = detail.getInitCcy();
            // 初始现金持仓金额折算人民币
            BigDecimal prdRaistAmtCny = ChbCcyConvertUtil.changeCnyMoney(initCcy, prdInitAmt, batchDate, rateMap);
            // 现金持仓=募集金额-交易+存续期+初始持仓
            addDetail.setPrdCashholdAmt(addDetail.getPrdCashholdAmt().add(prdRaistAmtCny));
            holdMap.put(addDetail.getPrdId(), addDetail);
        }
        // 计算资产持仓
        if (isCalAssetHold){
            // 查询资产持仓
            List<ChbTotalCheckDetailDto> assetPositionList = chbTotalCheckCBMapper.selectAssetPosition(batchDate,
                    openType,null,prodIgnoreList);
            for (ChbTotalCheckDetailDto detail : assetPositionList){
                // 需要新覆盖的数据
                ChbTotalCheckDetailDto addDetail = new ChbTotalCheckDetailDto();
                if (holdMap.get(detail.getPrdId()) != null){
                    // 如果已存在则用这条加上条
                    ChbTotalCheckDetailDto ystDetail = holdMap.get(detail.getPrdId());
                    // 产品id
                    addDetail.setPrdId(ystDetail.getPrdId());
                    // 运作模式
                    addDetail.setOperateMode(ystDetail.getOperateMode());
                    // 资产持仓金额
                    addDetail.setPrdAstholdAmt(ystDetail.getPrdAstholdAmt());
                    // 负债持仓总金额
                    addDetail.setPrdBalholdAmt(ystDetail.getPrdBalholdAmt());
                    // 现金持仓总金额
                    addDetail.setPrdCashholdAmt(ystDetail.getPrdCashholdAmt());
                } else {
                    // 产品id
                    addDetail.setPrdId(detail.getPrdId());
                    // 运作模式
                    addDetail.setOperateMode(detail.getOperateMode());
                    // 资产持仓金额
                    addDetail.setPrdAstholdAmt(BigDecimal.ZERO);
                    // 负债持仓总金额
                    addDetail.setPrdBalholdAmt(BigDecimal.ZERO);
                    // 现金持仓总金额
                    addDetail.setPrdCashholdAmt(BigDecimal.ZERO);
                }
                // 资产负债持仓币种
                BigDecimal prdAstholdAmt = ChbCcyConvertUtil.changeCnyMoney(detail.getAssetHoldCcy(), detail.getPrdAstholdAmt(), batchDate, rateMap);
                // 资产负债持仓币种
                BigDecimal prdBaltholdAmt = ChbCcyConvertUtil.changeCnyMoney(detail.getAssetHoldCcy(), detail.getPrdBalholdAmt(), batchDate, rateMap);
                // 资产持仓金额
                addDetail.setPrdAstholdAmt(addDetail.getPrdAstholdAmt().add(prdAstholdAmt));
                // 负债持仓总金额
                addDetail.setPrdBalholdAmt(addDetail.getPrdBalholdAmt().add(prdBaltholdAmt));
                holdMap.put(addDetail.getPrdId(),addDetail);
            }
        }
        // 查询产品募集交易
        List<ChbTotalCheckDetailDto> prdRaiseList = chbTotalCheckCBMapper.selectPrdRaiseDeal(batchDate,
                openType,null,prodIgnoreList);
        for (ChbTotalCheckDetailDto detail : prdRaiseList){
            // 需要新覆盖的数据
            ChbTotalCheckDetailDto addDetail = new ChbTotalCheckDetailDto();
            if (holdMap.get(detail.getPrdId()) != null){
                // 如果已存在则用这条加上条
                ChbTotalCheckDetailDto ystDetail = holdMap.get(detail.getPrdId());
                // 产品id
                addDetail.setPrdId(ystDetail.getPrdId());
                // 计算资产持仓才加运作模式
                if (isCalAssetHold){
                    // 运作模式
                    addDetail.setOperateMode(ystDetail.getOperateMode());
                    // 资产持仓金额
                    addDetail.setPrdAstholdAmt(ystDetail.getPrdAstholdAmt());
                    // 负债持仓总金额
                    addDetail.setPrdBalholdAmt(ystDetail.getPrdBalholdAmt());
                }
                // 现金持仓总金额
                addDetail.setPrdCashholdAmt(ystDetail.getPrdCashholdAmt());
            } else {
                // 产品id
                addDetail.setPrdId(detail.getPrdId());
                // 计算资产持仓才加运作模式
                if (isCalAssetHold){
                    // 运作模式
                    addDetail.setOperateMode(detail.getOperateMode());
                    // 资产持仓金额
                    addDetail.setPrdAstholdAmt(BigDecimal.ZERO);
                    // 负债持仓总金额
                    addDetail.setPrdBalholdAmt(BigDecimal.ZERO);
                }
                // 现金持仓总金额
                addDetail.setPrdCashholdAmt(BigDecimal.ZERO);
            }
            // 募集总金额
            BigDecimal prdRaistAmt = BigDecimal.ZERO;
            // 认购币种及金额:原币种，原币种金额，折算人民币金额
            String subCcyAndAmt = detail.getSubscribeCcyAmt();
            // 多组数据以分号隔开
            List<String> subCcyAndAmtList = StringUtil.splitToList(GlobalConstant.SEMICOLON, subCcyAndAmt);
            for (String subCcyAmt : subCcyAndAmtList){
                // 原币种，原币种金额，折算人民币金额，以逗号隔开
                List<String> infoList = StringUtil.splitToList(GlobalConstant.COMMA, subCcyAmt);
                // 原币种
                String ccy = infoList.get(0);
                // 原币种金额
                BigDecimal ccyAmt = ChbTotalCheckCBUtils
                        .getBigDecimal(infoList.get(1));
                // 募集金额计算累计值
                prdRaistAmt = prdRaistAmt.add(ChbCcyConvertUtil.changeCnyMoney(ccy, ccyAmt, batchDate, rateMap));
            }
            // 现金持仓=募集金额-交易+存续期+初始持仓
            addDetail.setPrdCashholdAmt(addDetail.getPrdCashholdAmt().add(prdRaistAmt));
            holdMap.put(addDetail.getPrdId(), addDetail);
        }
        // 查询产品资产交易
        List<ChbTotalCheckDetailDto> prdAssetList = chbTotalCheckCBMapper.selectPrdAssetDeal(batchDate,
                openType,null,prodIgnoreList);
        for (ChbTotalCheckDetailDto detail : prdAssetList){
            // 需要新覆盖的数据
            ChbTotalCheckDetailDto addDetail = new ChbTotalCheckDetailDto();
            if (holdMap.get(detail.getPrdId()) != null){
                // 如果已存在则用这条加上条
                ChbTotalCheckDetailDto ystDetail = holdMap.get(detail.getPrdId());
                // 产品id
                addDetail.setPrdId(ystDetail.getPrdId());
                // 计算资产持仓才加运作模式
                if (isCalAssetHold){
                    // 运作模式
                    addDetail.setOperateMode(ystDetail.getOperateMode());
                    // 资产持仓金额
                    addDetail.setPrdAstholdAmt(ystDetail.getPrdAstholdAmt());
                    // 负债持仓总金额
                    addDetail.setPrdBalholdAmt(ystDetail.getPrdBalholdAmt());
                }
                // 现金持仓总金额
                addDetail.setPrdCashholdAmt(ystDetail.getPrdCashholdAmt());
            } else {
                // 产品id
                addDetail.setPrdId(detail.getPrdId());
                // 计算资产持仓才加运作模式
                if (isCalAssetHold){
                    // 运作模式
                    addDetail.setOperateMode(detail.getOperateMode());
                    // 资产持仓金额
                    addDetail.setPrdAstholdAmt(BigDecimal.ZERO);
                    // 负债持仓总金额
                    addDetail.setPrdBalholdAmt(BigDecimal.ZERO);
                }
                // 现金持仓总金额
                addDetail.setPrdCashholdAmt(BigDecimal.ZERO);
            }
            // 交易金额
            BigDecimal prdAssetDealAmt = detail.getPrdAssetAmt();
            // 交易币种
            String prdAssetCcy = detail.getPrdAssetCcy();
            // 折算人民币
            prdAssetDealAmt = ChbCcyConvertUtil.changeCnyMoney(prdAssetCcy, prdAssetDealAmt, batchDate, rateMap);
            // 现金持仓=募集金额-交易+存续期+初始持仓
            addDetail.setPrdCashholdAmt(addDetail.getPrdCashholdAmt().subtract(prdAssetDealAmt));
            holdMap.put(addDetail.getPrdId(), addDetail);
        }
        // 查询产品存续期交易
        List<ChbTotalCheckDetailDto> prdDurList = chbTotalCheckCBMapper.selectPrdDurDeal(batchDate,
                openType,null,prodIgnoreList);
        for (ChbTotalCheckDetailDto detail : prdDurList){
            // 需要新覆盖的数据
            ChbTotalCheckDetailDto addDetail = new ChbTotalCheckDetailDto();
            if (holdMap.get(detail.getPrdId()) != null){
                // 如果已存在则用这条加上条
                ChbTotalCheckDetailDto ystDetail = holdMap.get(detail.getPrdId());
                // 产品id
                addDetail.setPrdId(ystDetail.getPrdId());
                // 计算资产持仓才加运作模式
                if (isCalAssetHold){
                    // 运作模式
                    addDetail.setOperateMode(ystDetail.getOperateMode());
                    // 资产持仓金额
                    addDetail.setPrdAstholdAmt(ystDetail.getPrdAstholdAmt());
                    // 负债持仓总金额
                    addDetail.setPrdBalholdAmt(ystDetail.getPrdBalholdAmt());
                }
                // 现金持仓总金额
                addDetail.setPrdCashholdAmt(ystDetail.getPrdCashholdAmt());
            } else {
                // 产品id
                addDetail.setPrdId(detail.getPrdId());
                // 计算资产持仓才加运作模式
                if (isCalAssetHold){
                    // 运作模式
                    addDetail.setOperateMode(detail.getOperateMode());
                    // 资产持仓金额
                    addDetail.setPrdAstholdAmt(BigDecimal.ZERO);
                    // 负债持仓总金额
                    addDetail.setPrdBalholdAmt(BigDecimal.ZERO);
                }
                // 现金持仓总金额
                addDetail.setPrdCashholdAmt(BigDecimal.ZERO);
            }
            // 存续期总金额
            BigDecimal prdDurAmt = BigDecimal.ZERO;
            // 存续期交易币种及申购兑付金额
            String subCcyAndAmt = detail.getCcyPurchase();
            // 多组数据以分号隔开
            List<String> subCcyAndAmtList = StringUtil.splitToList(GlobalConstant.SEMICOLON, subCcyAndAmt);
            for (String subCcyAmt : subCcyAndAmtList){
                // 币种，申购，赎回，兑付，以逗号隔开
                List<String> infoList = StringUtil.splitToList(GlobalConstant.COMMA, subCcyAmt);
//                // 原币种
//                String ccy = infoList.get(0);
//                // 获取当天汇率
//                String rateKey = DateUtil.format(detail.getRaiseEnddate()) + ccy;
//                BigDecimal orgRate = BigDecimal.ONE;
//                if (rateMap.get(rateKey)!= null){
//                    orgRate = rateMap.get(rateKey);
//                } else {
//                    ChbCcyConvertUtil.getExchangeRate(ccy, detail.getRaiseEnddate(), rateMap);
//                    // 重新取汇率
//                    orgRate = rateMap.get(rateKey);
//                }
                // 申购金额折算人民币
                BigDecimal subAmt = ChbTotalCheckCBUtils
                        .getBigDecimal(infoList.get(1));
                // 赎回金额折算人民币
                BigDecimal redAmt = ChbTotalCheckCBUtils
                        .getBigDecimal(infoList.get(2));
//                if (orgRate != null){
//                    // 折算汇率为原币金额
//                    subAmt.divide(orgRate,2,BigDecimal.ROUND_HALF_UP);
//                    redAmt.divide(orgRate,2,BigDecimal.ROUND_HALF_UP);
//                } else {
//                    logger.info("计算产品剩余现金持仓-产品存续期交易汇率取值错误！");
//                }
                BigDecimal subSubstratRedAmt = subAmt.subtract(redAmt);
                // 存续期交易计算累计值
                prdDurAmt = prdDurAmt.add(subSubstratRedAmt);
            }
            // 现金持仓=募集金额-交易+存续期+初始持仓
            addDetail.setPrdCashholdAmt(addDetail.getPrdCashholdAmt().add(prdDurAmt));
            holdMap.put(addDetail.getPrdId(), addDetail);
        }
        return new ArrayList<>(holdMap.values());
    }

    /**
     * 功能:数据变更接口统一调度入口
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年5月9日
     * @param executeDate
     * @param prodList
     * @param assetList
     * @throws BusinessException 
     * @see com.joyintech.tams.batch.report.autosendcb.common.service.SvrCBBaseService#execute(java.util.Date, java.util.List, java.util.List)
     */
    @Override
    public void execute(Date executeDate, List<String> prodList, List<String> assetList,Map<String,List<String>> param) throws BusinessException {
        try {
            // 设置批处理bean
            setBatchBeanName(BatchBeanName.数据检查);
            // 批处理开始
            logInfoBatchStart();
            autoCheckRegisterReport(executeDate);
            // 批处理结束
            logInfoBatchEnd();
        } catch (Exception e) {
        	logger.error(e.getMessage(),e);
            throw new BusinessException(e);
        }
    }
}
