/*
 * 文件名：AssetIntdateCarryoverProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年5月7日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.assetintdatecarryover.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.BokDetailIdMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListNatureGroup;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.assetintdatecarryover.dao.AssetIntdateCarryoverMapper;
import com.joyintech.tams.valuation.batch.assetintdatecarryover.dto.*;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.service.BookAccountCommonService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 资产理论结息日结转- processor相关业务处理
 * 
 * @author chengzhong
 * @version 1.0 2018年6月7日
 * @see AssetIntdateCarryoverProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class AssetIntdateCarryoverProcessorService {

    @Autowired
    private AccountingCommonService accountingCommonService;

    @Autowired
    private FinProductCommonService finProductCommonService;

    @Autowired
    private InterfCommonService interfCommonService;

    /**
     * reader方法的 Service
     */
    @Autowired
    private AssetIntdateCarryoverMapper assetIntdateCarryoverMapper;

    /**
     * reader方法的 Service
     */
    @Autowired
    private BookAccountCommonService bookAccountCommonService;

    @Autowired
    private BokDetailIdMapper bokDetailIdMapper;

    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 start
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 end

    /**
     * process 数据处理
     * 
     * @param param1 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public AssetIntdateCarryoverResult execute(Map<String, Object> param1) throws Exception {
        // 获取reader返回值
        AssetIntdateCarryoverReaderResult acctEntity = (AssetIntdateCarryoverReaderResult)param1.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数或计提基础信息列表为空，不进行后续处理
        if (acctEntity == null || acctEntity.getAssetIntdateCarryoverReaderDetailList() == null
            || acctEntity.getAssetIntdateCarryoverReaderDetailList().isEmpty()) {
            return null;
        }

        // 定义返回值list
        AssetIntdateCarryoverResult assetIntdateCarryoverResult = new AssetIntdateCarryoverResult();

        // 变量.最大分支序号 = 调用共通标的【最大分支序号查询（DTO.金融产品代码，DTO.金融产品类型）】
        Long maxBranch = finProductCommonService.queryValFinProductDealMaxBranch(
            acctEntity.getFinprodId(), acctEntity.getFinprodType());
        if (maxBranch == null) {
            maxBranch = new Long(0);
        }

        // 循环生成数据
        for (AssetIntdateCarryoverReaderDetail detail : acctEntity.getAssetIntdateCarryoverReaderDetailList()) {
            // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 start
            try{
                // 债券预交易 处理
                handleStep1(detail, assetIntdateCarryoverResult, maxBranch);
                // 4.处理 买断式回购派息交易
                handleStep2(detail, assetIntdateCarryoverResult, maxBranch);
            }
            catch (Exception e){
                logger.error("《资产理论结息日结转批处理-标的类》--自动结转处理 报错相关信息： 账套信息【"+detail.getBooksetId()+"】；金融产品代码【"+detail.getFinprodId()+"】",e);
                throw new Exception(e);
            }
            // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 end
        }

        return assetIntdateCarryoverResult;

    }

    /**
     * 生成交易 <功能详细描述>
     * 
     * @param Type 类型
     * @param amount 金额
     * @param busiType 业务类型
     * @param valBokBookdetail 账务明细信息
     * @param branch 分支序号
     * @param dealPs 交易方向
     * @param detail 结转信息
     * @param faceValue 单位面值
     * @param isCreat 是否生成交易类金融产品
     * @return
     * @see
     * @since 1.0
     */
    private AssetIntdateCarryoverDeal creatCarryoverDeal(String type, BigDecimal amount,
                                                         String busiType,
                                                         BokDetailId valBokBookdetail, Long branch,
                                                         String dealPs,
                                                         AssetIntdateCarryoverReaderDetail detail,
                                                         BigDecimal faceValue, boolean isCreat,
                                                         AssetIntdateCarryoverResult assetIntdateCarryoverResult) {

        // 资产理论结息日结转信息 实体类定义
        AssetIntdateCarryoverDeal dao = new AssetIntdateCarryoverDeal();

        // 【交易类金融产品】
        // 数据生成===============================================================================
        //重复数据不生成
        boolean isChongfu = false;
        if (assetIntdateCarryoverResult != null
            && assetIntdateCarryoverResult.getAssetIntdateCarryoverDealList() != null
            && assetIntdateCarryoverResult.getAssetIntdateCarryoverDealList().size() > 0) {
            for (AssetIntdateCarryoverDeal deal : assetIntdateCarryoverResult.getAssetIntdateCarryoverDealList()) {
                FinTradeProduct trade = deal.getValTradeFinProduct();
                // 根据主键判断交易类金融产品表是否已经生成数据（多账套跑批时规避）
                if (trade != null && StringUtils.isNotEmpty(trade.getFinprodId())
                    && detail.getFinprodId().equals(trade.getFinprodId())
                    && detail.getFinprodType().equals(trade.getFinprodType())
                    && ((branch != null && branch.equals(trade.getBranch())
                         || (branch == null && trade.getBranch() == null)))) {
                    isChongfu = true;
                }
            }
        }
        FinTradeProductKey finTradeProductKey = new FinTradeProductKey();
        finTradeProductKey.setFinprodId(detail.getFinprodId());
        finTradeProductKey.setBranch(branch);
        boolean flag = this.checkCreateFinTrdProduct(assetIntdateCarryoverResult,
            finTradeProductKey);
        if (isCreat && !isChongfu && flag) {
            // 交易类金融产品
            FinTradeProduct valTradeFinProduct = new FinTradeProduct();
            // 金融产品代码 DTO.金融产品代码
            valTradeFinProduct.setFinprodId(detail.getFinprodId());
            // 金融产品类型 DTO.金融产品类型
            valTradeFinProduct.setFinprodType(detail.getFinprodType());
            // 分支序号 参数.分支序号
            valTradeFinProduct.setBranch(branch);
            // 分支类型 decode（参数.类型,"本金","应收本金"数据字典，"利息"，"应收利息"数据字典）
            if (type.equals(DictCodeListExt1.现金流一级类型_本金.getValue())) {
                valTradeFinProduct.setBranchType(DictCodeList.分支类型_应收本金.getValue());
            }
            else if (type.equals(DictCodeListExt1.现金流一级类型_利息.getValue())) {
                valTradeFinProduct.setBranchType(DictCodeList.分支类型_应收利息.getValue());
            }
            // 通道代码 DTO.通道代码
            valTradeFinProduct.setChlAgrtId(detail.getChlFinprodId());

            // 本金币种 DTO.币种
            valTradeFinProduct.setCcy(detail.getCcy());
            // 起息日 DTO.调整后的计算开始日
            valTradeFinProduct.setVdate(detail.getVdate());
            // 到期日 DTO.调整后的计算结束日
            valTradeFinProduct.setMdate(detail.getMdate());
            // 期限(天) 空
            valTradeFinProduct.setTerm(null);
            // 利率类型 空
            valTradeFinProduct.setIntType(null);
            // 固定利率 空
            valTradeFinProduct.setIntRate(null);
            // 利率编号 空
            valTradeFinProduct.setIntRateId(null);
            // 计息基础 空
            valTradeFinProduct.setBasis(null);
            if (type.equals(DictCodeListExt1.现金流一级类型_本金.getValue())) {
                // 到期本金 DTO.计划支付本金
                valTradeFinProduct.setmPrinAmt(detail.getPrinAmt());
                // 到期金额
                valTradeFinProduct.setmTradeAmt(detail.getPrinAmt());
            }
            if (type.equals(DictCodeListExt1.现金流一级类型_利息.getValue())) {
                // 到期利息 DTO.计划支付利息
                valTradeFinProduct.setmIntAmt(detail.getIntAmt());
                // 到期金额
                valTradeFinProduct.setmTradeAmt(detail.getIntAmt());
            }
            // 名义本金 同到期金额
            valTradeFinProduct.setPrin(null);

            // 原金融产品代码 DTO.原金融产品代码
            valTradeFinProduct.setoFinprodId(detail.getFinprodId());
            
          //【一体化修改】：begin========================================================================= yangjian 2018/1/4
          //【一体化修改】：针对估值一体化变更【60：交易类金融产品表原字段【原金融产品类型】删除】
            // 原金融产品类型 DTO.原金融产品类型
            //valTradeFinProduct.setoFinprodType(detail.getFinprodType());

          
            

            //针对估值一体化变更【63：交易类金融产品表原字段【逾期交易编号】删除】
            // 逾期交易编号 空
            //valTradeFinProduct.setoTradeId(null);
            
          //【一体化修改】：end========================================================================= yangjian 2018/1/4
            
            
            
            // 创建时间 共通字段
            valTradeFinProduct.setCreateTime(new Date());
            // 更新时间 共通字段
            valTradeFinProduct.setUpdateTime(new Date());
            dao.setValTradeFinProduct(valTradeFinProduct);
        }

        // 【金融产品交易】
        // 数据生成===============================================================================
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        // 交易编号 自动生成
        valFinProductDeal.setTradeId(
            interfCommonService.getTradeid(dealPs, detail.getBooksetDate(), "A"));
        // 业务类型 参数.业务类型
        valFinProductDeal.setBusiType(busiType);
        // 金融产品代码 DTO.金融产品代码
        valFinProductDeal.setFinprodId(detail.getFinprodId());
        // 金融产品类型 DTO.金融产品类型
        valFinProductDeal.setFinprodType(detail.getFinprodType());
        // 分支序号 参数.分支序号
        valFinProductDeal.setBranch(branch);
        // 交易日 DTO.账套日
        valFinProductDeal.setTradeDate(detail.getBooksetDate());
        // 起息日 DTO.调整后的计算开始日
        valFinProductDeal.setVdate(detail.getVdate());
        // 到期日 DTO.调整后的计算结束日
        valFinProductDeal.setMdate(detail.getMdate());
        // 交割日 DTO.账套日
        valFinProductDeal.setSettleDate(detail.getBooksetDate());
        // 交易币种 DTO.币种
        valFinProductDeal.setCcy(detail.getCcy());
        // 净值日期 空
        valFinProductDeal.setCpriceDate(null);
        if (type.equals(DictCodeListExt1.现金流一级类型_本金.getValue())) {
            // 单位净价 空
            valFinProductDeal.setUnitCprice(amount);
        }
        if (type.equals(DictCodeListExt1.现金流一级类型_利息.getValue())) {
            // 单位利息 空
            valFinProductDeal.setUnitInt(amount);
        }

        // 单位全价 空
        valFinProductDeal.setUnitFprice(null);
        // 单位面值 参数.单位面值
        valFinProductDeal.setParValue(faceValue);
        // 净价总额 金额
        valFinProductDeal.setCpriceAmt(BigDecimal.ZERO);
        // 交易份额 参数.交易份额
        valFinProductDeal.setShareAmt(BigDecimal.ZERO);
        // 交易本金 金额
        valFinProductDeal.setPrinAmt(BigDecimal.ZERO);
        // 利息总额 金额
        valFinProductDeal.setIntAmt(BigDecimal.ZERO);
        // 全价总额 金额
        valFinProductDeal.setFpriceAmt(BigDecimal.ZERO);
        // 随交易支付费用 0
        valFinProductDeal.setFeeAmtPay(BigDecimal.ZERO);
        // 交易金额 = 全价总额
        valFinProductDeal.setTradeAmt(BigDecimal.ZERO);
        // 未随交易支付费用 0
        valFinProductDeal.setFeeAmtUnpay(BigDecimal.ZERO);
        // 总交易费用 0
        valFinProductDeal.setFeeAmt(BigDecimal.ZERO);
        // 交易方向 参数.交易方向
        valFinProductDeal.setPs(dealPs);
        // 到期收益率 空
        valFinProductDeal.setYield(null);
        // 行权收益率 空
        valFinProductDeal.setExerYield(null);
        // 投资目的 账务明细代码信息.投资目的
        valFinProductDeal.setInvAim(valBokBookdetail.getInvAim());
        // 交易状态 正常
        valFinProductDeal.setStatus(DictCodeList.冲正标识_正常交易.getValue());
        valFinProductDeal.setTradeStatus(DictCodeList.冲正标识_正常交易.getValue());
        // 原交易编号 空
        valFinProductDeal.setpTradeId(null);
        // 是否结清 空
        valFinProductDeal.setIsClean(null);
        // 汇总交易编号 空
        valFinProductDeal.setTotleTradeId(null);
        // 反向交易编号 空
        valFinProductDeal.setrTradeId(null);
        // 通道代码 DTO.通道代码
        valFinProductDeal.setChlId(detail.getChlFinprodId());
        // 交易对手 空
        valFinProductDeal.setCounterId(null);
        // 交易对手类型 "外部"
//        valFinProductDeal.setCounterType(DictCodeListExt1.交易对手类型_外部交易对手.getValue());
        // 一体化：交易对手类型改成是否外部交易
        valFinProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 交易场所 DTO.交易场所
        valFinProductDeal.setTradeMarket(detail.getTradeMarket());
        // 交易员 空
        valFinProductDeal.setTrader(null);
        // 证券管理户代码 账务明细代码信息.证券管理户代码
        valFinProductDeal.setSecManageAcctId(valBokBookdetail.getSecManageAcctId());
        // 关联系统交易编号 空
        valFinProductDeal.setfTradeId(null);
        // 创建时间 共通字段
        valFinProductDeal.setCreateTime(new Date());
        // 更新时间 共通字段
        valFinProductDeal.setUpdateTime(new Date());
        dao.setValFinProductDeal(valFinProductDeal);

        // 【估值核算交易扩展表】
        // 数据生成===============================================================================
        // 估值核算交易扩展表
     //一体化问题12:表名变更(金融产品交易扩展表(TRD_PRODUCT_DEAL_EXT) -> 金融产品交易附属表(TRD_PRODUCT_DEAL_ADD))
        TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 金融产品交易已生成的交易编号
        valFinProductDealExt.setTradeId(valFinProductDeal.getTradeId());
        // 投资组合代码 空
        valFinProductDealExt.setPortfolioId(null);
        // 理财产品代码 空
        //valFinProductDealExt.setProdId(null);//【一体化注释：金融产品交易附属表TRD_PRODUCT_DEAL_ADD去除对PROD_ID字段】
//        // 是否有结算指令 否
//        valFinProductDealExt.setHasOrder(DictCodeList.是否_否.getValue());
        // 生成方式 "批处理生成"
        valFinProductDealExt.setGenType(DictCodeListExt1.生成方式_批处理生成.getValue());
        // 创建时间 共通字段
        valFinProductDealExt.setCreateTime(new Date());
        // 更新时间 共通字段
        valFinProductDealExt.setUpdateTime(new Date());
        dao.setValFinProductDealExt(valFinProductDealExt);

        // 【入账信息表】
        // 数据生成===============================================================================
        // 入账信息表
        BokDealAccounting valDealAccounting = new BokDealAccounting();
        // 账套代码 DTO.账套代码
        valDealAccounting.setBooksetId(detail.getBooksetId());
        // 业务编号 金融产品交易已生成的交易编号
        valDealAccounting.setBusiNo(valFinProductDeal.getTradeId());
        // 业务表 "金融产品表"
        valDealAccounting.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        // 业务类型 参数.业务类型
//        valDealAccounting.setBusiType(busiType);
        // 录入日 DTO.账套日
        valDealAccounting.setBooksetDate(detail.getBooksetDate());
        // 入账日 DTO.账套日
        valDealAccounting.setBookDate(detail.getBooksetDate());
        // 实际发生日 DTO.账套日
        valDealAccounting.setHappenDate(detail.getBooksetDate());
        // 账务明细代码 账务明细代码信息.账务明细代码
//        valDealAccounting.setBokDetailId(valBokBookdetail.getBokDetailId());
        // 是否已入账 否
        valDealAccounting.setIsAccounted(DictCodeList.是否_否.getValue());
        // 入账凭证编号 空
        valDealAccounting.setBookId(null);
        
        /**add by wujie on 20181211 start remark:增加投资目的字段赋值**/
        valDealAccounting.setInvAim(valBokBookdetail.getInvAim());
        /**add by wujie on 20181211 end**/

        dao.setValDealAccounting(valDealAccounting);

        // 【入账账务明细表】
        // 数据生成===============================================================================
        BokDealAccountingDetail valDealAccountingDetail = new BokDealAccountingDetail();
        valDealAccountingDetail.setBooksetId(detail.getBooksetId());// 账套代码
        valDealAccountingDetail.setBusiNo(valFinProductDeal.getTradeId());// 业务编码
        valDealAccountingDetail.setBusiTable(DictCodeList.业务表_金融产品表.getValue());// 业务表，指令表、交易表等
        valDealAccountingDetail.setBusiType(busiType);// 业务类型，债券交易、债券存续交易、基金交易、收划款等
        valDealAccountingDetail.setBokDetailId(valBokBookdetail.getBokDetailId());// 账务明细代码

        dao.setValDealAccountingDetail(valDealAccountingDetail);

        return dao;
    }

    /**
     * 生成交易 <功能详细描述>
     * 
     * @param pledge SecIsnotPledgeDto
     * @param amount 金额
     * @param busiType 业务类型
     * @param valBokBookdetail 账务明细信息
     * @param branch 分支序号
     * @param dealPs 交易方向
     * @param detail 结转信息
     * @param pledgeNumber 质押数量
     * @param faceValue 单位面值
     * @param isCreat 是否生成交易类金融产品
     * @return
     * @see
     * @since 1.0
     */
    private AssetIntdateCarryoverDeal creatCarryoverRepDeal(SecIsnotPledgeDto pledge,
                                                            BigDecimal amount, String busiType,
                                                            String bokDetailId, Long branch,
                                                            String dealPs,
                                                            AssetIntdateCarryoverReaderDetail detail,
                                                            AssetIntdateCarryoverResult assetIntdateCarryoverResult) {

        // 资产理论结息日结转信息 实体类定义
        AssetIntdateCarryoverDeal dao = new AssetIntdateCarryoverDeal();

        // 【金融产品交易】
        // 数据生成===============================================================================
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        // 交易编号 自动生成
        valFinProductDeal.setTradeId(
            interfCommonService.getTradeid(dealPs, detail.getBooksetDate(), "A"));
        // 业务类型 参数.业务类型
        valFinProductDeal.setBusiType(busiType);
        // 金融产品代码 DTO.金融产品代码
        valFinProductDeal.setFinprodId(detail.getFinprodId());
        // 金融产品类型 DTO.金融产品类型
        valFinProductDeal.setFinprodType(detail.getFinprodType());
        // 分支序号 参数.分支序号
        valFinProductDeal.setBranch(branch);
        // 交易日 DTO.账套日
        valFinProductDeal.setTradeDate(detail.getBooksetDate());
        // 起息日 DTO.调整后的计算开始日
        valFinProductDeal.setVdate(detail.getVdate());
        // 到期日 DTO.调整后的计算结束日
        valFinProductDeal.setMdate(detail.getMdate());
        // 交割日 DTO.账套日
        valFinProductDeal.setSettleDate(detail.getBooksetDate());
        // 交易币种 DTO.币种
        valFinProductDeal.setCcy(detail.getCcy());
        // 净值日期 空
        valFinProductDeal.setCpriceDate(null);
        // 单位净价 空
        valFinProductDeal.setUnitCprice(null);
        // 单位利息 空
        valFinProductDeal.setUnitInt(null);
        // 单位全价 空
        valFinProductDeal.setUnitFprice(amount);
        // 单位面值 参数.单位面值
        valFinProductDeal.setParValue(null);
        // 净价总额 金额
        valFinProductDeal.setCpriceAmt(null);
        // 交易份额 参数.交易份额 = 交易份额（质押数量）
        valFinProductDeal.setShareAmt(pledge.getPledgeNumber());
        // 交易本金 金额
        valFinProductDeal.setPrinAmt(BigDecimal.ZERO);
        // 利息总额 金额
        valFinProductDeal.setIntAmt(BigDecimal.ZERO);
        // 全价总额 金额
        valFinProductDeal.setFpriceAmt(BigDecimal.ZERO);
        // 随交易支付费用 0
        valFinProductDeal.setFeeAmtPay(BigDecimal.ZERO);
        // 交易金额 = 全价总额
        valFinProductDeal.setTradeAmt(BigDecimal.ZERO);
        // 未随交易支付费用 0
        valFinProductDeal.setFeeAmtUnpay(BigDecimal.ZERO);
        // 总交易费用 0
        valFinProductDeal.setFeeAmt(BigDecimal.ZERO);
        // 交易方向 参数.交易方向（“债券派息”/ "债券派本"）
        valFinProductDeal.setPs(dealPs);
        // 到期收益率 空
        valFinProductDeal.setYield(null);
        // 行权收益率 空
        valFinProductDeal.setExerYield(null);
        // 交易场所 DTO.交易场所
        valFinProductDeal.setTradeMarket(detail.getTradeMarket());
        // 投资目的 账务明细代码信息.投资目的 = “卖空”
        valFinProductDeal.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
        // 交易状态 正常
        valFinProductDeal.setStatus(DictCodeList.冲正标识_正常交易.getValue());
        valFinProductDeal.setTradeStatus(DictCodeList.冲正标识_正常交易.getValue());
        // 原交易编号 空
        valFinProductDeal.setpTradeId(null);
        // 是否结清 空
        valFinProductDeal.setIsClean(null);
        // 汇总交易编号 空
        valFinProductDeal.setTotleTradeId(null);
        // 反向交易编号 空
        valFinProductDeal.setrTradeId(null);
        // 通道代码 DTO.通道代码
        valFinProductDeal.setChlId(null);
        // 交易对手 空
        valFinProductDeal.setCounterId(null);
        // 一体化：交易对手类型改成是否外部交易
        valFinProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 交易员 空
        valFinProductDeal.setTrader(null);
        // 证券管理户代码 账务明细代码信息.证券管理户代码
        valFinProductDeal.setSecManageAcctId(null);
        // 关联系统交易编号 空
        valFinProductDeal.setfTradeId(null);
        // 创建时间 共通字段
        valFinProductDeal.setCreateTime(new Date());
        // 更新时间 共通字段
        valFinProductDeal.setUpdateTime(new Date());
        dao.setValFinProductDeal(valFinProductDeal);

        // 【估值核算交易扩展表】
        // 数据生成===============================================================================
        // 估值核算交易扩展表
        //一体化问题12:表名变更(金融产品交易扩展表(TRD_PRODUCT_DEAL_EXT) -> 金融产品交易附属表(TRD_PRODUCT_DEAL_ADD))
        TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 金融产品交易已生成的交易编号
        valFinProductDealExt.setTradeId(valFinProductDeal.getTradeId());
        // 投资组合代码 空
        valFinProductDealExt.setPortfolioId(null);
        // 生成方式 "批处理生成"
        valFinProductDealExt.setGenType(DictCodeListExt1.生成方式_批处理生成.getValue());
        // 主协议金融产品代码 = 交易类金融产品.金融产品代码
        valFinProductDealExt.setMpFinprodId(pledge.getFinprodId());
        // 主协议分支序号 = 交易类金融产品.分支序号
        valFinProductDealExt.setMpBranch(BookConstant.DEFALUT_BRANCH);
        // 创建时间 共通字段
        valFinProductDealExt.setCreateTime(new Date());
        // 更新时间 共通字段
        valFinProductDealExt.setUpdateTime(new Date());
        dao.setValFinProductDealExt(valFinProductDealExt);

        // 【入账信息表】
        // 数据生成===============================================================================
        // 入账信息表
        BokDealAccounting valDealAccounting = new BokDealAccounting();
        // 账套代码 DTO.账套代码
        valDealAccounting.setBooksetId(detail.getBooksetId());
        // 业务编号 金融产品交易已生成的交易编号
        valDealAccounting.setBusiNo(valFinProductDeal.getTradeId());
        // 业务表 "金融产品表"
        valDealAccounting.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        // 录入日 DTO.账套日
        valDealAccounting.setBooksetDate(detail.getBooksetDate());
        // 入账日 DTO.账套日
        valDealAccounting.setBookDate(detail.getBooksetDate());
        // 实际发生日 DTO.账套日
        valDealAccounting.setHappenDate(detail.getBooksetDate());
        // 是否已入账 否
        valDealAccounting.setIsAccounted(DictCodeList.是否_否.getValue());
        // 入账凭证编号 空
        valDealAccounting.setBookId(null);

        dao.setValDealAccounting(valDealAccounting);

        // 【入账账务明细表】
        // 数据生成===============================================================================
        BokDealAccountingDetail valDealAccountingDetail = new BokDealAccountingDetail();
        valDealAccountingDetail.setBooksetId(detail.getBooksetId());// 账套代码
        valDealAccountingDetail.setBusiNo(valFinProductDeal.getTradeId());// 业务编码
        valDealAccountingDetail.setBusiTable(DictCodeList.业务表_金融产品表.getValue());// 业务表，指令表、交易表等
        valDealAccountingDetail.setBusiType(busiType);// 业务类型，债券交易、债券存续交易、基金交易、收划款等
        valDealAccountingDetail.setBokDetailId(bokDetailId);// 账务明细代码
        dao.setValDealAccountingDetail(valDealAccountingDetail);

        // 【账务明细代码】 判断是否已存在，已存在跳过处理
        // 数据生成===============================================================================
        /** modify by yanya on 20210104 质押券的账务明细代码判定用质押券判定 start**/
        BokDetailIdKey key = new BokDetailIdKey();
        BokDetailId valbokDetailIdSell = new BokDetailId();
        valbokDetailIdSell.setBusiId(detail.getFinprodId());
        valbokDetailIdSell.setBusiDetailType(detail.getFinprodType());
        valbokDetailIdSell.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
        //key.setBokDetailId(bokDetailId);
        key.setBokDetailId(bookAccountCommonService.genMd5BokDetailId(valbokDetailIdSell));
        key.setBooksetId(detail.getBooksetId());
        /** modify by yanya on 20210104 质押券的账务明细代码判定用质押券判定 end**/
        boolean bokDetailIdflag = checkCreateBokDetailId(assetIntdateCarryoverResult, key);
        // 判断是否已存在，已存在跳过处理
        if (bokDetailIdflag) {
            BokDetailId bokDetailIdNew = new BokDetailId();
            // 账套代码 = DTO.账套代码
            bokDetailIdNew.setBooksetId(detail.getBooksetId());
            // 业务明细代码 = DTO.金融产品代码
            bokDetailIdNew.setBusiId(detail.getFinprodId());
            // 业务明细类型 = DTO.金融产品类型
            bokDetailIdNew.setBusiDetailType(detail.getFinprodType());
            // 投资目的 =null
            bokDetailIdNew.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
            // 账务明细代码 = 共通生成
            BokDetailId valbokDetailId = new BokDetailId();
            valbokDetailId.setBusiId(detail.getFinprodId());
            valbokDetailId.setBusiDetailType(detail.getFinprodType());
            valbokDetailId.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
            bokDetailIdNew.setBokDetailId(
                bookAccountCommonService.genMd5BokDetailId(valbokDetailId));
            // 交易编号 =
            bokDetailIdNew.setTradeId(null);
            // 四级科目尾号 = DTO.金融产品代码
            bokDetailIdNew.setDetailSubjectNoTail(detail.getFinprodId());
            // 四级科目尾号名称 = DTO.金融产品名称
            bokDetailIdNew.setDetailSubnameTail(detail.getFinprodName());
            //modify by yanya on 20201217 增加分支序号
            bokDetailIdNew.setBranch(BookConstant.DEFALUT_BRANCH);
            // 入库
            dao.setBokDetailId(bokDetailIdNew);
        }
        // 【交易类金融产品】 判断是否已存在，已存在跳过处理
        // 数据生成===============================================================================
        FinTradeProductKey finTradeProductKey = new FinTradeProductKey();
        finTradeProductKey.setFinprodId(detail.getFinprodId());
        finTradeProductKey.setBranch(branch);
        boolean flag = this.checkCreateFinTrdProduct(assetIntdateCarryoverResult,
            finTradeProductKey);
        if (flag) {
            FinTradeProduct finTradeProduct = new FinTradeProduct();
            // 金融产品代码= DTO.金融产品代码
            finTradeProduct.setFinprodId(detail.getFinprodId());
            // 金融产品类型= DTO.金融产品类型
            finTradeProduct.setFinprodType(detail.getFinprodType());
            // 分支序号= 参数.分支序号
            finTradeProduct.setBranch(branch);
            // 分支类型= decode（参数.类型,"本金","应收本金"数据字典，"利息"，"应收利息"数据字典）
            if (DictCodeList.交易方向_还本.getValue().equals(dealPs)) {
                finTradeProduct.setBranchType(DictCodeList.分支类型_应收本金.getValue());
                // 到期本金= DTO.计划支付本金 参数.类型 = 本金时有值
                finTradeProduct.setmPrinAmt(detail.getPrinAmt());
            }
            else if (DictCodeList.交易方向_收息.getValue().equals(dealPs)) {
                finTradeProduct.setBranchType(DictCodeList.分支类型_应收利息.getValue());
                // 到期利息= DTO.计划支付利息 参数.类型 = 利息时有值
                finTradeProduct.setmIntAmt(detail.getIntAmt());
            }
            // 通道代码= DTO.通道代码
            finTradeProduct.setChlAgrtId(detail.getChlFinprodId());
            // 名义本金= 空
            // 本金币种= DTO.币种
            finTradeProduct.setCcy(detail.getCcy());
            // 起息日= DTO.调整后的计算开始日
            finTradeProduct.setVdate(detail.getVdate());
            // 到期日= DTO.调整后的计算结束日
            finTradeProduct.setMdate(detail.getMdate());
            // 期限(天)= 空
            // 利率类型= 空
            // 固定利率= 空
            // 利率编号= 空
            // 计息基础= 空
            // 到期金额= DTO.计划支付本金+DTO.计划支付利息
            finTradeProduct.setmTradeAmt(
                ArithmeticUtil.doAdd(detail.getPrinAmt(), detail.getIntAmt()));
            // 原金融产品代码 DTO.原金融产品代码
            finTradeProduct.setoFinprodId(detail.getFinprodId());
            // 逾期交易编号= 空
            // 入库
            dao.setValTradeFinProduct(finTradeProduct);
        }

        return dao;
    }

    /**
     * 获取当日成本 <功能详细描述>
     * 
     * @param subjectBalance
     * @param detail
     * @return
     * @see
     * @since 1.0
     */
    private BigDecimal getTodayCb(SubjectBalance subjectBalance,
                                  AssetIntdateCarryoverReaderDetail detail) {
        // 2.7 T+1相关发生额统计

        // 变量.当日计算成本科目余额 = 变量.当日成本科目余额
        BigDecimal varDrjscbKmye = subjectBalance.getBalance();

        if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(detail.getTradeMarket())) {
            // 买入场景
            String tableId = null;
            List<String> baseSubjectNatureList = new ArrayList<String>();
            if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(
                subjectBalance.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_买入.getValue();
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.公允价值损益类_成本.getValue());
            }
            else if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(subjectBalance.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_摊余成本类债券_买入.getValue();
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.摊余成本类成本.getValue());
            }
            else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
                subjectBalance.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_综合收益类债券_买入.getValue();
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.综合收益类成本.getValue());
            }
            // 调用账务共通设计->【凭证最新账务查询】
            List<BookLatestInfoDto> listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
                detail.getBooksetId(), detail.getBooksetDate(), subjectBalance.getBokDetailId(),
                baseSubjectNatureList, tableId, null, null,
                DateUtil.addDay(detail.getBooksetDate(), -1), null, null);

            // LOOP 变量.买入账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrue.公允价值损益类_成本.getValue().equals(
                        var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrue.摊余成本类成本.getValue().equals(
                            var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrue.综合收益类成本.getValue().equals(
                            var.getBaseSubjectNature())) {
                        // 变量.当日计算成本科目余额 -= 变量.账务明细.原币发生额
                        varDrjscbKmye = ArithmeticUtil.subtract(varDrjscbKmye,
                            var.getoHappenAmt());
                    }

                }
            }
            // 卖出场景
            if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(
                subjectBalance.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_卖出.getValue();
            }
            else if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(subjectBalance.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_摊余成本类债券_卖出.getValue();
            }
            else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
                subjectBalance.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_综合收益类债券_卖出.getValue();
            }
            // 调用账务共通设计->【凭证最新账务查询】
            listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
                detail.getBooksetId(), detail.getBooksetDate(), subjectBalance.getBokDetailId(),
                baseSubjectNatureList, tableId, null, null,
                DateUtil.addDay(detail.getBooksetDate(), -1), null, null);
            // LOOP 变量.卖出账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrue.公允价值损益类_成本.getValue().equals(
                        var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrue.摊余成本类成本.getValue().equals(
                            var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrue.综合收益类成本.getValue().equals(
                            var.getBaseSubjectNature())) {
                        // 变量.当日计算成本科目余额 += 变量.账务明细.原币发生额
                        varDrjscbKmye = ArithmeticUtil.doAdd(varDrjscbKmye, var.getoHappenAmt());
                    }
                }
            }
        }
        return varDrjscbKmye;
    }

    /**
     * 生成 交易类金融产品 前校验是否已生成过
     * 
     * @param assetIntdateCarryoverResult
     * @param finTradeProductKey
     * @return
     * @see
     * @since 1.0
     */
    private boolean checkCreateFinTrdProduct(AssetIntdateCarryoverResult assetIntdateCarryoverResult,
                                             FinTradeProductKey finTradeProductKey) {
        // 查询 交易类标的
        FinTradeProduct finTradeProduct = finTradeProductMapper.selectByPrimaryKey(
            finTradeProductKey);
        // 交易类标的不为空
        if (finTradeProduct != null) {
            // 生成标志 = false，不重新生成交易类标的
            return false;
        }
        else {
            // 获取 返回结果中的所有交易
            List<AssetIntdateCarryoverDeal> listDeal = assetIntdateCarryoverResult.getAssetIntdateCarryoverDealList();
            // 判断 交易中是否有生成过与当前相同的标的信息
            for (AssetIntdateCarryoverDeal item : listDeal) {
                // 金融产品代码、金融产品类型、分支序号一致，则已生成过，则不需重复生成
                if (item.getValTradeFinProduct() != null
                    && StringUtil.equals(finTradeProductKey.getFinprodId(),
                        item.getValTradeFinProduct().getFinprodId())
//                    && StringUtil.equals(finTradeProductKey.getFinprodType(),
//                        item.getValTradeFinProduct().getFinprodType())
                    && finTradeProductKey.getBranch().equals(
                        item.getValTradeFinProduct().getBranch())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 生成 账务明细代码 前校验是否已生成过
     * 
     * @param assetIntdateCarryoverResult
     * @param finTradeProductKey
     * @return
     * @see
     * @since 1.0
     */
    private boolean checkCreateBokDetailId(AssetIntdateCarryoverResult assetIntdateCarryoverResult,
                                           BokDetailIdKey bokDetailIdKey) {
        // 查询 交易类标的
        BokDetailId bokDetailId = bokDetailIdMapper.selectByPrimaryKey(bokDetailIdKey);;
        // 交易类标的不为空
        if (bokDetailId != null) {
            // 生成标志 = false，不重新生成交易类标的
            return false;
        }
        else {
            // 获取 返回结果中的所有交易
            List<AssetIntdateCarryoverDeal> listDeal = assetIntdateCarryoverResult.getAssetIntdateCarryoverDealList();
            // 判断 交易中是否有生成过与当前相同的标的信息
            for (AssetIntdateCarryoverDeal item : listDeal) {
                // 金融产品代码、金融产品类型、分支序号一致，则已生成过，则不需重复生成
                //modify by yanya on 20201217 增加账务明细为空的判定
                if (bokDetailIdKey.getBokDetailId() != null && item.getBokDetailId() != null
                    && StringUtil.equals(bokDetailIdKey.getBokDetailId(),
                        item.getBokDetailId().getBokDetailId())
                    && StringUtil.equals(bokDetailIdKey.getBooksetId(),
                        item.getBokDetailId().getBooksetId())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 债券预交易处理
     * 
     * @param detail 账务明细
     * @param assetIntdateCarryoverResult 返回结果
     * @param maxBranch 最大分支序号
     * @see
     * @since 1.0
     */
    private void handleStep1(AssetIntdateCarryoverReaderDetail detail,
                             AssetIntdateCarryoverResult assetIntdateCarryoverResult,
                             Long maxBranch) {
        // 变量.本金分支号
        Long branchPri = BookConstant.DEFALUT_BRANCH;
        // 变量.利息分支号
        Long branchInt = BookConstant.DEFALUT_BRANCH;
        // 定义 变量.本金
        BigDecimal printAmt = BigDecimal.ZERO;

        // 定义交易方向
        String glob_ps = null;

        // if（DTO.现金流一级类型 = "本金"）
        if (detail.getCashType1().equals(DictCodeListExt1.现金流一级类型_本金.getValue())) {
            // 变量.本金分支号 = 调用共通标的【当前计划对应分支序号查询（DTO.金融产品代码，DTO.金融产品类型，"本金"，DTO.调整后的计算结束日）】
            branchPri = finProductCommonService.queryValFinProductDealBranchNow(
                detail.getFinprodId(), detail.getFinprodType(), DictCodeList.分支类型_应收本金.getValue(),
                detail.getMdate());
            // 变量.利息分支号 = 调用共通标的【当前计划对应分支序号查询（DTO.金融产品代码，DTO.金融产品类型，"利息"，DTO.调整后的计算结束日）】
            branchInt = finProductCommonService.queryValFinProductDealBranchNow(
                detail.getFinprodId(), detail.getFinprodType(), DictCodeList.分支类型_应收利息.getValue(),
                detail.getMdate());

            // 一级类型为本金的时候，交易方向取还本
            glob_ps = DictCodeList.交易方向_还本.getValue();

        }
        else {
            // 变量.利息分支号 = 调用共通标的【当前计划对应分支序号查询（DTO.金融产品代码，DTO.金融产品类型，"利息"，DTO.调整后的计算结束日）】
            branchInt = finProductCommonService.queryValFinProductDealBranchNow(
                detail.getFinprodId(), detail.getFinprodType(), DictCodeList.分支类型_应收利息.getValue(),
                detail.getMdate());

            // 其他情况，交易方向取交易方向_收息
            glob_ps = DictCodeList.交易方向_收息.getValue();
        }

        // 1.是否存在已交易生成
        int dealCount = assetIntdateCarryoverMapper.queryDealCount(detail.getFinprodId(),
                    detail.getFinprodType(), detail.getMdate(), detail.getBooksetId(), glob_ps);
        
        if (dealCount > 0) {
            return;
        }

        // 2.成本查询
        // 2.1基础科目性质查询
        List<String> subjectNatureList = accountingCommonService.getValSubjectNatureList(
    		DictCodeListNatureGroup.分组代码_成本类对应科目查询.getValue());

        // 2.2 最新余额查询
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setHappenDate(detail.getBooksetDate());
        searchDao.setBookDate(detail.getBooksetDate());
        searchDao.setBooksetId(detail.getBooksetId());
        searchDao.setBusiId(detail.getFinprodId());
        //删除在途日期条件 20190515 金茂东
//      searchDao.setOnwayDate(DateUtil.addDay(detail.getBooksetDate(), -1));

        searchDao.setBaseSubjectNatureList(subjectNatureList);
        List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDao, DictCodeList.余额方向_借方.getValue());
        // 是否生成交易类金融产品
        boolean isCreat = true;
        // 定义 变量.还本不带息Flag = false;
//        boolean withoutIntFlag = false;
        if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
            // 3.根据科目余额生成交易
            for (SubjectBalance subjectBalance : listSubjectBalance) {
                // 变量.利息 = 0
                BigDecimal intAmt = BigDecimal.ZERO;
                // 变量.本金 = 0
                printAmt = BigDecimal.ZERO;
                // 变量.业务类型
                String busiType = null;
                // 变量.交易方向
                String dealPs = null;
                // 变量.单位面值 = 0
                BigDecimal faceValue = BigDecimal.ZERO;

                // 变量.账务明细代码信息 = 调用共通标的【账务明细代码信息查询（变量.科目余额信息.账务明细代码，变量.账套明细.账套代码）】
                List<BokDetailId> listValBokBookdetail = finProductCommonService.bookdetailInforQuery(
                    detail.getBooksetId(), subjectBalance.getBokDetailId());
                BokDetailId valBokBookdetail = new BokDetailId();
                if (listValBokBookdetail != null && listValBokBookdetail.size() > 0) {
                    valBokBookdetail = listValBokBookdetail.get(0);
                }

                // if（变量.科目余额信息.科目余额 = 0）
                if (ArithmeticUtil.equals(subjectBalance.getBalance(), BigDecimal.ZERO)
                    && !detail.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券.getValue())) {
                    continue;
                }
                else {
                    if (detail.getFinprodType().equals(
                            DictCodeListExt1.金融产品类型_计划类资产.getValue())
                        		|| detail.getFinprodType().equals(
                                        DictCodeListExt1.金融产品类型_存款.getValue())
                        		|| detail.getFinprodType().equals(
                        				DictCodeListExt1.金融产品类型_同业借款.getValue())) {// DTO.金融产品类型
                                                                                               // =
                                                                                               // 非债券情况

                        if(detail.getFinprodType().equals(DictCodeListExt1.金融产品类型_计划类资产.getValue())){
                            // 变量.业务类型 = "计划类资产交易"
                            busiType = DictCodeList.业务类型_计划类资产预交易.getValue();
                        } 
                        else if (detail.getFinprodType().equals(DictCodeListExt1.金融产品类型_同业借款.getValue())) {
                            // 变量.业务类型 = "拆借预交易"
                            busiType = DictCodeList.业务类型_拆借预交易.getValue();
                        } 
                        else if(detail.getFinprodType().equals(DictCodeListExt1.金融产品类型_存款.getValue())) {
                           	// 变量.业务类型 = "存款类资产交易"
                            busiType = DictCodeList.业务类型_存款资产预交易.getValue();
                        }
 
                        faceValue = BigDecimal.ONE;
                        // 变量.当前本金 = 调用共通计算类共通【当前未还本金（DTO.账套日期,DTO.金融产品代码,DTO.发行规模）】
                        BigDecimal dqPrintAmt = finProductCommonService.getUnpaidPrinAmt(
                            detail.getBooksetDate(), detail.getFinprodId(), detail.getIssueAmt());
                        // if（DTO.现金流一级类型 = "本金"）
                        if (detail.getCashType1().equals(DictCodeListExt1.现金流一级类型_本金.getValue())) {
                            // 变量.本金 = DTO.计划支付本金/变量.当前本金
                            printAmt = ArithmeticUtil.divide(detail.getPrinAmt(), dqPrintAmt,
                                NumberEnum.ALL_PRECISION.getCode());
                            // 变量.利息 = DTO.计划支付利息/变量.当前本金
                            intAmt = ArithmeticUtil.divide(detail.getIntAmt(), dqPrintAmt,
                                NumberEnum.ALL_PRECISION.getCode());
                        }
                        else {
                            // 变量.利息 = DTO.计划支付利息/变量.当前本金
                            intAmt = ArithmeticUtil.divide(detail.getIntAmt(), dqPrintAmt,
                                NumberEnum.ALL_PRECISION.getCode());
                        }
                    }
                    // if(DTO.金融产品类型 = "债券")
                    else if (detail.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券.getValue())) {
                        if (ArithmeticUtil.equals(getTodayCb(subjectBalance, detail),
                            BigDecimal.ZERO)) {
                            continue;
                        }

                        // 设计变更 add by zhangyf 20181026 start
                        // if 变量.账务明细代码信息.投资目的！= “卖空”
                        if (!DictCodeListExt1.投资目的_卖空.getValue().equals(
                            valBokBookdetail.getInvAim())) {
                            // 变量.业务类型 = "债券交易预处理"
                            busiType = DictCodeList.业务类型_债券交易预处理.getValue();
                        }
                        else {
                            // 变量.业务类型 =“债券借贷标的债券交易”
                            busiType = DictCodeList.业务类型_债券借贷标的债券交易.getValue();
                        }
                        // 设计变更 add by zhangyf 20181026 end

                        // 设计变更 hide by zhangyf 20181026 start
                        /*
                         * // 变量.业务类型 = "债券交易" busiType = DictCodeList.业务类型_债券交易预处理.getValue();
                         */
                        // 设计变更 hide by zhangyf 20181026 end

                        faceValue = finProductCommonService.getFaceValue(detail.getFinprodId(),
                            DateUtil.addDay(detail.getBooksetDate(), -1));
                        // if（DTO.现金流一级类型 = "本金"）
                        if (detail.getCashType1().equals(DictCodeListExt1.现金流一级类型_本金.getValue())) {
                            // 变量.本金 = DTO.计划支付本金
                            printAmt = detail.getPrinAmt();
                            // 变量.利息 = DTO.计划支付利息
                            intAmt = detail.getIntAmt();
                            // if DTO.计划支付利息！=0 变量.还本不带息Flag = true
//                            if (!ArithmeticUtil.equals(BigDecimal.ZERO, detail.getIntAmt())) {
//                                withoutIntFlag = true;
//                            }
                        }
                        else {
                            // 变量.利息 = DTO.计划支付利息
                            intAmt = detail.getIntAmt();
                        }
                    }
                }
                /*
                 * // 变量.账务明细代码信息 = 调用共通标的【账务明细代码信息查询（变量.科目余额信息.账务明细代码，变量.账套明细.账套代码）】
                 * List<BokDetailId> listValBokBookdetail =
                 * finProductCommonService.bookdetailInforQuery( detail.getBooksetId(),
                 * subjectBalance.getBokDetailId()); BokDetailId valBokBookdetail = new
                 * BokDetailId(); if (listValBokBookdetail != null && listValBokBookdetail.size() >
                 * 0) { valBokBookdetail = listValBokBookdetail.get(0); }
                 */
                // if（变量.利息 != 0）
                if (!ArithmeticUtil.equals(intAmt, BigDecimal.ZERO)) {
                	
                	/**moidfy modify wujie on 20210107
                    // 设计变更 add by zhangyf 20181026 start
                    // if DTO.金融产品类型 = "债券资产" && 变量.账务明细代码信息.投资目的 = 卖空
                    if (detail.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券.getValue())
                        && DictCodeListExt1.投资目的_卖空.getValue().equals(valBokBookdetail.getInvAim())) {
                        // 变量.交易方向 = "卖空债券结息"
                        dealPs = DictCodeList.交易方向_卖空债券结息.getValue();
                    }
                    else {**/
                        // 变量.交易方向 = "收息"
                        dealPs = DictCodeList.交易方向_收息.getValue();
//                    }
                    // 设计变更 add by zhangyf 20181026 end

                    // 设计变更 hide by zhangyf 20181026 start
                    /*
                     * // 变量.交易方向 = "收息" dealPs = DictCodeList.交易方向_收息.getValue();
                     */
                    // 设计变更 hide by zhangyf 20181026 end

                    // if（变量.利息分支号 = null）
                    if (branchInt == null) {
                    	/**MODIFY BY WUJIE ON 20200107
                        // if 变量.还本不带息Flag = true
                        if (DictCodeList.是否_否.getValue().equals(detail.getIsPayInt())) {
                            // 调用当前付息区间查询
                            List<TargetCashPlan> listPlan = finProductCommonService.getCurrentIntRange(
                                detail.getFinprodId(), detail.getMdate());
                            // 变量.最大分支序号 = （返回值.调整后的计算结束日 （'YYYYMMDD'）|| 02）转成数值型
                            if (CollectionUtils.isNotEmpty(listPlan)) {
                                maxBranch = new Long(
                                    DateUtil.format(listPlan.get(0).getMdate(), "yyyyMMdd")
                                                     + "02");;
                            }
                        }
                        else {
                        **/
                            // 变量.最大分支序号 = （DTO.调整后的计算结束日 （'YYYYMMDD'）|| 02）转成数值型
                            maxBranch = new Long(
                                DateUtil.format(detail.getMdate(), "yyyyMMdd") + "02");
                      /**  } MODIFY BY WUJIE ON 20200107**/
                        // 创建交易信息("利息"，变量.利息，变量.业务类型，变量.账务明细代码信息，变量.最大分支序号，变量.交易方向，DTO)
                        AssetIntdateCarryoverDeal assetIntdateCarryoverDeal = creatCarryoverDeal(
                            DictCodeListExt1.现金流一级类型_利息.getValue(), intAmt, busiType, valBokBookdetail,
                            maxBranch, dealPs, detail, faceValue, isCreat,
                            assetIntdateCarryoverResult);
                        assetIntdateCarryoverResult.getAssetIntdateCarryoverDealList().add(
                            assetIntdateCarryoverDeal);
                    }
                    else {
                        // 创建交易信息("利息"，变量.利息，变量.业务类型，变量.账务明细代码信息，变量.利息分支号，变量.交易方向，DTO)
                        AssetIntdateCarryoverDeal assetIntdateCarryoverDeal = creatCarryoverDeal(
                            DictCodeListExt1.现金流一级类型_利息.getValue(), intAmt, busiType, valBokBookdetail,
                            branchInt, dealPs, detail, faceValue, false,
                            assetIntdateCarryoverResult);
                        assetIntdateCarryoverResult.getAssetIntdateCarryoverDealList().add(
                            assetIntdateCarryoverDeal);
                    }
                }
                // if（变量.本金 != 0）
                if (!ArithmeticUtil.equals(printAmt, BigDecimal.ZERO)) {
                	/**modify by wujie on 20210107
                    // 设计变更 add by zhangyf 20181026 start
                    // if DTO.金融产品类型 = "债券资产" && 变量.账务明细代码信息.投资目的 = 卖空
                    if (detail.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券.getValue())
                        && DictCodeListExt1.投资目的_卖空.getValue().equals(valBokBookdetail.getInvAim())) {
                        // 变量.交易方向 = "卖空债券还本"
                        dealPs = DictCodeList.交易方向_卖空债券还本.getValue();
                    }
                    else {**/
                        // 变量.交易方向 = "还本"
                        dealPs = DictCodeList.交易方向_还本.getValue();
//                    }
                    // 设计变更 add by zhangyf 20181026 start

                    // 设计变更 hide by zhangyf 20181026 start
                    /*
                     * // 变量.交易方向 = "还本" dealPs = DictCodeList.交易方向_还本.getValue();
                     */
                    // 设计变更 hide by zhangyf 20181026 start

                    // if（DTO.实际到期日 = DTO.调整后的计算结束日）
                    if (detail.getActmdate().equals(detail.getMdate())) {
                        // 变量.交易方向 = "到期"
                        dealPs = DictCodeList.交易方向_到期.getValue();
                    }
                    // if（变量.本金分支号 = null）
                    if (branchPri == null) {
                        // // 变量.最大分支序号++
                        // maxBranch++ ;
                        // 变量.最大分支序号 = （DTO.调整后的计算结束日 （'YYYYMMDD'）|| 01）转成数值型
                        maxBranch = new Long(
                            DateUtil.format(detail.getMdate(), "yyyyMMdd") + "01");
                        // 创建交易信息("本金"，变量.利息，变量.业务类型，变量.账务明细代码信息，变量.最大分支序号，变量.交易方向，DTO)
                        AssetIntdateCarryoverDeal assetIntdateCarryoverDeal = creatCarryoverDeal(
                            DictCodeListExt1.现金流一级类型_本金.getValue(), printAmt, busiType,
                            valBokBookdetail, maxBranch, dealPs, detail, faceValue, isCreat,
                            assetIntdateCarryoverResult);
                        assetIntdateCarryoverResult.getAssetIntdateCarryoverDealList().add(
                            assetIntdateCarryoverDeal);
                    }
                    else {
                        // 创建交易信息("本金"，变量.利息，变量.业务类型，变量.账务明细代码信息，变量.本金分支号，变量.交易方向，DTO)
                        AssetIntdateCarryoverDeal assetIntdateCarryoverDeal = creatCarryoverDeal(
                            DictCodeListExt1.现金流一级类型_本金.getValue(), printAmt, busiType,
                            valBokBookdetail, branchPri, dealPs, detail, faceValue, false,
                            assetIntdateCarryoverResult);
                        assetIntdateCarryoverResult.getAssetIntdateCarryoverDealList().add(
                            assetIntdateCarryoverDeal);
                    }
                }
                isCreat = false;
            }
        }
    }

    /**
     * 债券回购预交易 处理
     * 
     * @param detail 账务明细
     * @param assetIntdateCarryoverResult 返回结果
     * @param maxBranch 最大分支序号
     * @see
     * @since 1.0
     */
    private void handleStep2(AssetIntdateCarryoverReaderDetail detail,
                             AssetIntdateCarryoverResult assetIntdateCarryoverResult,
                             Long maxBranch) {
    	// 变量.交易方向 = "收息"
    	String glb_ps = DictCodeList.交易方向_收息.getValue();
        // if（变量.本金（DTO.计划支付本金） != 0）
        if (!ArithmeticUtil.equals(detail.getPrinAmt(), BigDecimal.ZERO)) {
        	glb_ps = DictCodeList.交易方向_还本.getValue();
        }
        // 4.1 查询本次收息还本的债券是否被质押
        List<String> trdFinprodTypeList = new ArrayList<>();
        trdFinprodTypeList.add(DictCodeListExt1.金融产品类型_债券买断式正回购.getValue());
        trdFinprodTypeList.add(DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue());
        // 返回结果：买断式回购交易列表
        List<SecIsnotPledgeDto> listSecIsnotPledgeDto = assetIntdateCarryoverMapper.querySecIsnotPledge(
            detail.getFinprodId(), detail.getFinprodType(), trdFinprodTypeList, detail.getMdate());
        if (listSecIsnotPledgeDto == null || listSecIsnotPledgeDto.size() <= 0) {
            return;
        }
        // LOOP 买断式回购交易列表 -> 回购交易明细
        for (SecIsnotPledgeDto pledge : listSecIsnotPledgeDto) {
            // 买断式正回购交易存在数量
            int pledgeCount = assetIntdateCarryoverMapper.queryPledgeCount(pledge.getFinprodId(),
                pledge.getFinprodType(), detail.getBooksetId(), detail.getMdate(),
                DictCodeList.业务表_金融产品表.getValue(), glb_ps);
            if (pledgeCount > 0) {
                continue;
            }
            // 最新余额查询
            SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
            searchDao.setHappenDate(detail.getBooksetDate());
            searchDao.setBookDate(detail.getBooksetDate());
            searchDao.setBooksetId(detail.getBooksetId());
            // 业务明细代码 = 交易类金融产品.金融产品代码
            searchDao.setBusiId(pledge.getFinprodId());
            searchDao.setOnwayDate(DateUtil.addDay(detail.getBooksetDate(), -1));
            // 基础科目性质 = "成本"
            searchDao.setBaseSubjectNatureList(
                Arrays.asList(DictCodeBaseSubjectNatrue.成本.getValue()));
            List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            // if (科目余额信息list == null || 科目余额信息list.size = 0 || 科目余额信息list.get(0).科目余额 = 0)
            // continue;
            if (listSubjectBalance == null || listSubjectBalance.size() == 0
                || ArithmeticUtil.equals(listSubjectBalance.get(0).getBalance(),
                    BigDecimal.ZERO)) {
                continue;
            }
                        
            // 变量.分支序号 = 调用共通标的【当前计划对应分支序号查询（DTO.金融产品代码，DTO.金融产品类型，"本金"，DTO.调整后的计算结束日）】
            // *通过交易方向区分传入参数“本金”,“利息”
            // 变量.本金分支号
            Long branch = BookConstant.DEFALUT_BRANCH;
            // if（交易方向 = “债券派本”）
            if (DictCodeList.交易方向_还本.getValue().equals(glb_ps)) {
                // 变量.本金分支号 = 调用共通标的【当前计划对应分支序号查询（DTO.金融产品代码，DTO.金融产品类型，"本金"，DTO.调整后的计算结束日）】
                branch = finProductCommonService.queryValFinProductDealBranchNow(
                    detail.getFinprodId(), detail.getFinprodType(),
                    DictCodeList.分支类型_应收本金.getValue(), detail.getMdate());
                // IF(变量.本金分支号 == null)
                if (branch == null) {
                    // 变量.本金分支号 = （DTO.调整后的计算结束日 （'YYYYMMDD'）|| 01）转成数值型
                    branch = new Long(DateUtil.format(detail.getMdate(), "yyyyMMdd") + "01");
                }
                BokDetailId valbokDetailId = new BokDetailId();
                valbokDetailId.setBusiId(pledge.getFinprodId());
                valbokDetailId.setBusiDetailType(pledge.getFinprodType());
                String bokDetailId = bookAccountCommonService.genMd5BokDetailId(valbokDetailId);
                
                
                //  创建买断式回购派息交易信息(回购交易明细.质押金融产品代码 ，回购交易明细.质押金融产品类型，变量.金额，业务类型=“买断式回购预处理”
                // ，账务明细共通（回购交易明细.金融产品代码 ，回购交易明细.金融产品类型），回购交易明细.分支序号，变量.交易方向，DTO)
                AssetIntdateCarryoverDeal assetIntdateCarryoverDeal = creatCarryoverRepDeal(
                    pledge, detail.getPrinAmt(),
                    DictCodeList.业务类型_买断式回购预处理.getValue(), bokDetailId, branch, glb_ps,
                    detail, assetIntdateCarryoverResult);
                assetIntdateCarryoverResult.getAssetIntdateCarryoverDealList().add(
                    assetIntdateCarryoverDeal);
                
                if (!ArithmeticUtil.equals(detail.getIntAmt(), BigDecimal.ZERO)) {
                	// 买断式正回购交易存在数量
                    pledgeCount = assetIntdateCarryoverMapper.queryPledgeCount(pledge.getFinprodId(),
                        pledge.getFinprodType(), detail.getBooksetId(), detail.getMdate(),
                        DictCodeList.业务表_金融产品表.getValue(), DictCodeList.交易方向_收息.getValue());
                    if (pledgeCount > 0) {
                        continue;
                    }
                    
                    // 变量.本金分支号 = 调用共通标的【当前计划对应分支序号查询（DTO.金融产品代码，DTO.金融产品类型，"本金"，DTO.调整后的计算结束日）】
                    branch = finProductCommonService.queryValFinProductDealBranchNow(
                        detail.getFinprodId(), detail.getFinprodType(),
                        DictCodeList.分支类型_应收利息.getValue(), detail.getMdate());
                    // IF(变量.本金分支号 == null)
                    if (branch == null) {
                        // 变量.本金分支号 = （DTO.调整后的计算结束日 （'YYYYMMDD'）|| 01）转成数值型
                        branch = new Long(DateUtil.format(detail.getMdate(), "yyyyMMdd") + "02");
                    }
                    
                     //  创建买断式回购派息交易信息(回购交易明细.质押金融产品代码 ，回购交易明细.质押金融产品类型，变量.金额，业务类型=“买断式回购预处理”
                    // ，账务明细共通（回购交易明细.金融产品代码 ，回购交易明细.金融产品类型），回购交易明细.分支序号，变量.交易方向，DTO)
                    AssetIntdateCarryoverDeal dealInt = creatCarryoverRepDeal(
                        pledge, detail.getIntAmt(),
                        DictCodeList.业务类型_买断式回购预处理.getValue(), bokDetailId, branch, DictCodeList.交易方向_收息.getValue(),
                        detail, assetIntdateCarryoverResult);
                    assetIntdateCarryoverResult.getAssetIntdateCarryoverDealList().add(
                    		dealInt);
                }
                
            }
            else {
                // 变量.利息分支号 = 调用共通标的【当前计划对应分支序号查询（DTO.金融产品代码，DTO.金融产品类型，"利息"，DTO.调整后的计算结束日）】
                branch = finProductCommonService.queryValFinProductDealBranchNow(
                    detail.getFinprodId(), detail.getFinprodType(),
                    DictCodeList.分支类型_应收利息.getValue(), detail.getMdate());
                // IF(变量.本金分支号 == null)
                if (branch == null) {
                    // 变量.利息分支号 = （DTO.调整后的计算结束日 （'YYYYMMDD'）|| 02）转成数值型
                    branch = new Long(DateUtil.format(detail.getMdate(), "yyyyMMdd") + "02");
                }
                // 变量.金额 = DTO.计划支付现金流
                BokDetailId valbokDetailId = new BokDetailId();
                valbokDetailId.setBusiId(pledge.getFinprodId());
                valbokDetailId.setBusiDetailType(pledge.getFinprodType());
                String bokDetailId = bookAccountCommonService.genMd5BokDetailId(valbokDetailId);
                //  创建买断式回购派息交易信息(回购交易明细.质押金融产品代码 ，回购交易明细.质押金融产品类型，变量.金额，业务类型=“买断式回购预处理”
                // ，账务明细共通（回购交易明细.金融产品代码 ，回购交易明细.金融产品类型），回购交易明细.分支序号，变量.交易方向，DTO)
                AssetIntdateCarryoverDeal assetIntdateCarryoverDeal = creatCarryoverRepDeal(
                    pledge, detail.getIntAmt(),
                    DictCodeList.业务类型_买断式回购预处理.getValue(), bokDetailId, branch, glb_ps,
                    detail, assetIntdateCarryoverResult);
                assetIntdateCarryoverResult.getAssetIntdateCarryoverDealList().add(
                    assetIntdateCarryoverDeal);
            }
        }
    }
}
