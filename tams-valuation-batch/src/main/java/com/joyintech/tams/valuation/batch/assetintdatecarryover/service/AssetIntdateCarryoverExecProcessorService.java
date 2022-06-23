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

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.valuation.batch.assetintdatecarryover.dao.AssetIntdateCarryoverMapper;
import com.joyintech.tams.valuation.batch.assetintdatecarryover.dto.AssetIntdateCarryoverDeal;
import com.joyintech.tams.valuation.batch.assetintdatecarryover.dto.AssetIntdateCarryoverExecReaderDetail;
import com.joyintech.tams.valuation.batch.assetintdatecarryover.dto.AssetIntdateCarryoverExecReaderResult;
import com.joyintech.tams.valuation.batch.assetintdatecarryover.dto.AssetIntdateCarryoverResult;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产理论结息日结转行权- processor相关业务处理
 * 
 * @author yanya
 * @version 1.0 2021年3月11日
 * @see AssetIntdateCarryoverExecProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class AssetIntdateCarryoverExecProcessorService {

    @Autowired
    AssetIntdateCarryoverMapper assetIntdateCarryoverMapper;

    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private FinProductCommonService finProductCommonService;

    @Autowired
    private AccountingCommonService accountingCommonService;

    @Autowired
    private InterfCommonService interfCommonService;

    // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 start
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 end

    /**
     * process 数据处理
     * @param param1 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public AssetIntdateCarryoverResult execute(Map<String, Object> param1) throws Exception {
        // 获取reader返回值
        AssetIntdateCarryoverExecReaderResult acctEntity = (AssetIntdateCarryoverExecReaderResult) param1.get(GlobalConstant.READER_RESULT);
        // 若reader传递的参数或计提基础信息列表为空，不进行后续处理
        if (acctEntity == null || acctEntity.getAssetIntdateCarryoverExecReaderDetailList() == null
                || acctEntity.getAssetIntdateCarryoverExecReaderDetailList().isEmpty()) {
            return null;
        }
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param1.get("BATCHINFO");
        SysBatchTask batchTask = (SysBatchTask)param1.get("BATCHTASK");
        // 定义返回值list
        AssetIntdateCarryoverResult assetIntdateCarryoverResult = new AssetIntdateCarryoverResult();

        // 循环生成数据
        for (AssetIntdateCarryoverExecReaderDetail detail : acctEntity.getAssetIntdateCarryoverExecReaderDetailList()) {
            // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 start
            try{
                //1.判断是否生成交易类标的
                //定义 变量.行权分支序号=调用共通标的【当前计划对应分支序号查询（DTO.金融产品代码，DTO.金融产品类型，数据字典：现金流一级类型.行权，DTO.调整后的计算结束日）】
                Long 行权分支序号 = finProductCommonService.queryValFinProductDealBranchNow(
                        acctEntity.getFinprodId(), acctEntity.getFinprodType(),
                        DictCodeListExt3.分支类型_行权.getValue(), detail.getExerciseDate());
                //2.根据账套生成行权赎回结转交易
                //3.1 判断交易是否已存在
                Map<String, Object> map = new HashMap<>();
                map.put("busiTable", DictCodeList.业务表_金融产品表.getValue());
                map.put("booksetId", detail.getBooksetId());
                map.put("finprodId", detail.getFinprodId());
                map.put("finprodType", detail.getFinprodType());
                map.put("exerciseDate", detail.getExerciseDate());
                map.put("ps", DictCodeListExt3.交易方向_行权赎回结转.getValue());
                Integer num = assetIntdateCarryoverMapper.selectExecTradeCount(map);
                //if（数量>0 ） return;
                if(num != null  && num > 0) {
                    break;
                }
                //3.2.成本查询
                //调用共通【基础科目性质查询】
                //参数:
                //分组代码=成本类对应科目查询 对应的枚举CODE
                //定义 变量.基础科目性质List = 上述返回
                List<String> subjectNatureListForJc = accountingCommonService.getValSubjectNatureListWithAcctStandardId(
                        DictCodeListNatureGroup.分组代码_成本类对应科目查询.getValue(), DictCodeListExt2.会计准则_企业准则.getValue());
                // 设值科目余额查询参数Dto
                SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
                searchDto.setHappenDate(detail.getBooksetDate());//发生日 = DTO.账套日期
                searchDto.setBookDate(detail.getBooksetDate());// 入账日 = DTO.账套日期
                searchDto.setBooksetId(detail.getBooksetId());// 账套代码 = 变量.账套明细.账套代码
                searchDto.setBaseSubjectNatureList(subjectNatureListForJc);//基础科目性质 = 变量.基础科目性质List
                searchDto.setBusiId(detail.getFinprodId());//业务明细代码 = DTO.金融产品代码
                //定义 变量.科目余额信息list = 上述返回值
                List<SubjectBalance> subjectList = accountingCommonService.getSubjectBalanceExpectDirection(searchDto,
                        DictCodeList.余额方向_借方.getValue());
                if (CollectionUtils.isNotEmpty(subjectList)) {
                    //3.3 根据行权计划生成交易
                    //定义 变量.是否生成交易类金融产品 = true;
                    boolean 是否生成交易类金融产品 = true;
                    //LOOP: 变量.科目余额信息list -> 变量.科目余额信息
                    for(SubjectBalance balance : subjectList) {
                        //定义 变量.业务类型 = 数据字典：业务类型.债券交易预处理
                        String busiType = DictCodeList.业务类型_债券交易预处理.getValue();
                        //定义 变量.交易方向 = 数据字典：交易方向.行权-赎回结转
                        String ps = DictCodeListExt3.交易方向_行权赎回结转.getValue();
                        //定义 变量.账务明细代码信息 = 调用共通标的【账务明细代码信息查询（变量.科目余额信息.账务明细代码，变量.账套明细.账套代码）】
                        List<BokDetailId> listValBokBookdetail = finProductCommonService.bookdetailInforQuery(
                                detail.getBooksetId(), balance.getBokDetailId());
                        BokDetailId valBokBookdetail = new BokDetailId();
                        if (listValBokBookdetail != null && listValBokBookdetail.size() > 0) {
                            valBokBookdetail = listValBokBookdetail.get(0);
                        }
                        //定义 变量.当日计算成本科目余额 = 变量.科目余额信息.科目余额
                        BigDecimal 当日计算成本科目余额 = balance.getBalance();
                        //调用账务共通设计-> 【债券实际成本数量查询】
                        //参数名					【债券实际成本数量查询】
                        //账套代码					DTO.账套代码
                        //入账日					DTO.入账日
                        //发生日					DTO.发生日
                        //交易场所					DTO.交易场所
                        //账务明细代码					变量.科目余额信息.账务明细代码
                        //投资目的					变量.科目余额信息.投资目的
                        //任务优先级					批处理参数.任务优先级
                        //返回值：					变量.成本科目数量
                        //变量.当日计算成本科目余额 = 上述返回值
                        BigDecimal 当日计算成本科目数量 = accountingCommonService.findBondActualCostNum(detail.getBooksetId(), detail.getBooksetDate(),
                                detail.getBooksetDate(), balance.getBokDetailId(), detail.getTradeMarket(),
                                balance.getInvAim(), batchTask.getTaskPri());
                        //if(变量.当日计算成本科目余额 = 0)
                        if(当日计算成本科目数量 == null || 当日计算成本科目数量.compareTo(BigDecimal.ZERO) == 0) {
                            continue;
                        }
                        BigDecimal faceValue = finProductCommonService.getFaceValue(detail.getFinprodId(),
                                DateUtil.addDay(detail.getBooksetDate(), -1));
                        //if（变量.行权分支序号 = null）
                        if(行权分支序号 == null) {
                            //变量.最大分支序号 =  （DTO.调整后的计算结束日 （'YYYYMMDD'）|| 03）转成数值型
                            行权分支序号 = new Long(DateUtil.format(detail.getExerciseDate(),"yyyyMMdd") +  "03");
                            //创建交易信息("行权"，0，变量.业务类型，变量.账务明细代码信息，变量.最大分支序号，变量.交易方向，DTO,变量.是否生成交易类金融产品)
                            //注：变量.是否生成交易类金融产品 false :不生成交易类金融产品  true : 生成交易类金融产品
                            是否生成交易类金融产品 = true;
                        }
                        //else
                        else {
                            //创建交易信息("行权"，0，变量.业务类型，变量.账务明细代码信息，变量.行权分支序号，变量.交易方向，DTO)
                            //注：不生成交易类金融产品
                            是否生成交易类金融产品 = false;
                        }
                        AssetIntdateCarryoverDeal assetIntdateCarryoverDeal = this.createExecDeal(busiType, ps, 行权分支序号, 当日计算成本科目余额,
                                当日计算成本科目数量, faceValue, valBokBookdetail, detail, assetIntdateCarryoverResult,是否生成交易类金融产品);
                        assetIntdateCarryoverResult.getAssetIntdateCarryoverDealList().add(
                                assetIntdateCarryoverDeal);
                    }
                }
            }
            catch (Exception e){
                logger.error("《资产理论结息日结转批处理-标的类》--自动行权处理 报错相关信息： 账套信息【"+detail.getBooksetId()+"】；金融产品代码【"+detail.getFinprodId()+"】",e);
                throw new Exception(e);
            }
            // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 end
        }
        return assetIntdateCarryoverResult;

    }

    /**
     * 构建行权预交易数据
     * @param busiType
     * @param ps
     * @param branch
     * @param tradeAmt
     * @param shareAmt
     * @param faceValue
     * @param valBokBookdetail
     * @param detail
     * @param assetIntdateCarryoverResult
     * @param tradeFlag
     * @return
     */
    private AssetIntdateCarryoverDeal createExecDeal(String busiType, String ps, Long branch,BigDecimal tradeAmt, BigDecimal shareAmt, BigDecimal faceValue,
                                                     BokDetailId valBokBookdetail, AssetIntdateCarryoverExecReaderDetail detail,
                                                     AssetIntdateCarryoverResult assetIntdateCarryoverResult,
                                                     boolean tradeFlag) {
        // 资产理论结息日结转信息 实体类定义
        AssetIntdateCarryoverDeal dto = new AssetIntdateCarryoverDeal();
        FinTradeProductKey finTradeProductKey = new FinTradeProductKey();
        finTradeProductKey.setFinprodId(detail.getFinprodId());
        finTradeProductKey.setBranch(branch);
        boolean flag = this.checkCreateFinTrdProduct(assetIntdateCarryoverResult, finTradeProductKey);
        if (tradeFlag && flag) {
            // 交易类金融产品
            FinTradeProduct valTradeFinProduct = new FinTradeProduct();
            // 金融产品代码 DTO.金融产品代码
            valTradeFinProduct.setFinprodId(detail.getFinprodId());
            // 金融产品类型 DTO.金融产品类型
            valTradeFinProduct.setFinprodType(detail.getFinprodType());
            // 分支序号 参数.分支序号
            valTradeFinProduct.setBranch(branch);
            // 分支类型 = 行权
            valTradeFinProduct.setBranchType(DictCodeListExt3.分支类型_行权.getValue());
            // 通道代码 DTO.通道代码
            valTradeFinProduct.setChlAgrtId(null);
            // 本金币种 DTO.币种
            valTradeFinProduct.setCcy(detail.getCcy());
            // 起息日 DTO.调整后的计算开始日
            valTradeFinProduct.setVdate(detail.getVdate());
            // 到期日 DTO.调整后的计算结束日
            valTradeFinProduct.setMdate(detail.getBooksetDate());
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
            // 名义本金 同到期金额
            valTradeFinProduct.setPrin(null);
            // 原金融产品代码 DTO.原金融产品代码
            valTradeFinProduct.setoFinprodId(detail.getFinprodId());
            // 创建时间 共通字段
            valTradeFinProduct.setCreateTime(new Date());
            // 更新时间 共通字段
            valTradeFinProduct.setUpdateTime(new Date());
            dto.setValTradeFinProduct(valTradeFinProduct);
        }
        // 【金融产品交易】
        // 数据生成===============================================================================
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        // 交易编号 自动生成
        valFinProductDeal.setTradeId(interfCommonService.getTradeid(ps, detail.getBooksetDate(), "A"));
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
        valFinProductDeal.setMdate(detail.getBooksetDate());
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
        // 单位全价 = 行权价格
        valFinProductDeal.setUnitFprice(detail.getExercisePrice());
        // 单位面值 参数.单位面值
        valFinProductDeal.setParValue(null);
        // 净价总额 金额
        valFinProductDeal.setCpriceAmt(null);
        // 交易份额 参数.交易份额
        valFinProductDeal.setShareAmt(shareAmt);
        // 交易本金 金额
        valFinProductDeal.setPrinAmt(BigDecimal.ZERO);
        // 单位面值 参数.单位面值
        valFinProductDeal.setParValue(faceValue);
        // 利息总额 金额
        valFinProductDeal.setIntAmt(BigDecimal.ZERO);
        // 全价总额 金额
        valFinProductDeal.setFpriceAmt(BigDecimal.ZERO);
        // 随交易支付费用 0
        valFinProductDeal.setFeeAmtPay(BigDecimal.ZERO);
        // 交易金额 = 交易金额
        valFinProductDeal.setTradeAmt(tradeAmt);
        // 未随交易支付费用 0
        valFinProductDeal.setFeeAmtUnpay(BigDecimal.ZERO);
        // 总交易费用 0
        valFinProductDeal.setFeeAmt(BigDecimal.ZERO);
        // 交易方向 参数.交易方向
        valFinProductDeal.setPs(ps);
        // 到期收益率 空
        valFinProductDeal.setYield(null);
        // 行权收益率 空
        valFinProductDeal.setExerYield(null);
        // 交易场所 DTO.交易场所
        valFinProductDeal.setTradeMarket(detail.getTradeMarket());
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
        dto.setValFinProductDeal(valFinProductDeal);

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
        valFinProductDealExt.setMpFinprodId(detail.getFinprodId());
        // 主协议分支序号 = 交易类金融产品.分支序号
        valFinProductDealExt.setMpBranch(BookConstant.DEFALUT_BRANCH);
        // 创建时间 共通字段
        valFinProductDealExt.setCreateTime(new Date());
        // 更新时间 共通字段
        valFinProductDealExt.setUpdateTime(new Date());
        dto.setValFinProductDealExt(valFinProductDealExt);

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
        //投资目的
        valDealAccounting.setInvAim(valFinProductDeal.getInvAim());
        dto.setValDealAccounting(valDealAccounting);

        // 【入账账务明细表】
        // 数据生成===============================================================================
        BokDealAccountingDetail valDealAccountingDetail = new BokDealAccountingDetail();
        valDealAccountingDetail.setBooksetId(detail.getBooksetId());// 账套代码
        valDealAccountingDetail.setBusiNo(valFinProductDeal.getTradeId());// 业务编码
        valDealAccountingDetail.setBusiTable(DictCodeList.业务表_金融产品表.getValue());// 业务表，指令表、交易表等
        valDealAccountingDetail.setBusiType(busiType);// 业务类型，债券交易、债券存续交易、基金交易、收划款等
        valDealAccountingDetail.setBokDetailId(valBokBookdetail.getBokDetailId());// 账务明细代码
        dto.setValDealAccountingDetail(valDealAccountingDetail);
        return dto;
    }

    /**
     * 生成 交易类金融产品 前校验是否已生成过
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
                        && finTradeProductKey.getBranch().equals(
                        item.getValTradeFinProduct().getBranch())) {
                    return false;
                }
            }
        }
        return true;
    }

}
