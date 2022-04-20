/*
 * 文件名：FinProductCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年5月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.finproduct.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.calculation.entity.DealTypesFeeDto;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.common.batch.finproduct.dao.FinProductCommonMapper;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.batch.finproduct.service.FinProductCommonBatchService;
import com.joyintech.tams.valuation.common.batch.tradecommon.dto.ValExecApplyInfoDto;
import com.joyintech.tams.valuation.common.batch.tradecommon.dto.ValTrdProductDealInfoDto;
import com.joyintech.tams.valuation.common.batch.tradecommon.service.ValTradeCommonService;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCode;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.dto.TrdFinProductBarterDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 标的类共通方法
 * 
 * @author wangzhiang
 * @version 1.0 2018年5月31日
 * @see FinProductCommonService
 * @since 1.0
 */
@Service
public class FinProductCommonService {

    /**
     * 标的类共同mapper
     */
    @Autowired
    private FinProductCommonMapper finProductCommonMapper;

    /**
     * 计算类共通Service
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * 标的类共通方法Service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;
    
    /**
     * 账务类共通方法Service
     */
    @Autowired
    private AccountingCommonService accountingCommonService;
    
    /**
     * 标的类共通方法Service
     */
    @Autowired
    private FinProductCommonBatchService finProductCommonBatchService;

    @Autowired
    ValTradeCommonService valTradeCommonService;
    /**
     * 公用方法：标的类共通方法 - 标的信息查询
     * @param finprodId 标的代码【必输项】
     * @param queryCriteria 查询类型【必输项】
     * @return Map<TargetSearchDto, Object> 返回值获取，直接用查询实体类作为key值获取。
     */
    public Map<TargetSearchDto, Object> getTargetInfo(String finprodId,
                                                      List<TargetSearchDto> queryCriteria) {
        return finProductCommonBatchService.getTargetInfo(finprodId, queryCriteria);
    }

    /**
     * 标的类共通方法设计 - 估值方案特殊资产配置查询
     *
     * @param happenDate 发生日
     * @param bokSetId 账套代码
     * @param schemetype 方案类型
     * @param finprodId 资产代码
     * @return
     * @see
     * @since 1.0
     */
    public List<SchemeDetailDto> getSpecialSchemeDetail(Date happenDate, String bokSetId,
                                                        String schemetype, String finprodId) {
        return finProductCommonBatchService.getSpecialSchemeDetail(happenDate, bokSetId, schemetype, finprodId);
    }

    /**
     * 估值方案查询
     *
     * @param happenDate 发生日
     * @param booksetId 账套代码
     * @param secType 证券品种
     * @param schemeType 方案类型
     * @param invAim 投资目的
     * @param secDetailType 证券细分类
     * @param tradeMarket 交易市场
     * @param
     * @return 估值方案结果集
     * @see
     * @since 1.0
     */
    public List<SchemeDetailDto> getSchemeDetail(Date happenDate, String booksetId, String secType,
                                                 String schemeType, String tradeMarket,
                                                 String secDetailType, String invAim) {
        return finProductCommonBatchService.getSchemeDetail(happenDate, booksetId, secType, schemeType, tradeMarket, secDetailType, invAim);
    }

    /**
     * 当前计划对应分支序号查询
     *
     * @param finprodId 标的代码
     * @param finprodType 金融产品类型
     * @param branchType 分支类型
     * @param mdate 到期日
     * @return
     * @see
     * @since 1.0
     */
    public Long queryValFinProductDealBranchNow(String finprodId, String finprodType,
                                                String branchType, Date mdate) {
        return finProductCommonBatchService.queryValFinProductDealBranchNow(finprodId, finprodType, branchType, mdate);
    }

    /**
     * 最大分支序号查询
     *
     * @param finprodId 标的代码
     * @param finprodType 金融产品类型
     * @return
     * @see
     * @since 1.0
     */
    public Long queryValFinProductDealMaxBranch(String finprodId, String finprodType) {
        return finProductCommonBatchService.queryValFinProductDealMaxBranch(finprodId, finprodType);
    }

    /**
     * 账务明细代码信息查询
     * @param booksetId 账套代码
     * @param bokdetailId 账务明细代码
     * @return
     */
    public List<BokDetailId> bookdetailInforQuery(String booksetId, String bokdetailId) {
        return finProductCommonBatchService.bookdetailInforQuery(booksetId, bokdetailId);
    }

    /**
     * 当前未还本金
     *
     * @param happenDate 发生日
     * @param printAmt 本金
     * @param finprodId 标的代码
     * @return 市价净价
     * @see
     * @since 1.0
     */
    public BigDecimal getUnpaidPrinAmt(Date happenDate, String finprodId, BigDecimal printAmt) {
        return finProductCommonBatchService.getUnpaidPrinAmt(happenDate, finprodId, printAmt);

    }

    /**
     * 下一行权计划查询 <功能详细描述>
     *
     * @param finprodId 金融产品代码
     * @param finprodType 金融产品类型
     * @param exerciseDate 计划行权日
     * @param exerciseType 行权方式
     * @return 下一行权计划
     * @see
     * @since 1.0
     */
    public FinExercisePlan queryFinExercisePlan(String finprodId, String finprodType,
                                                Date exerciseDate, String exerciseType) {
        return finProductCommonBatchService.queryFinExercisePlan(finprodId, finprodType, exerciseDate, exerciseType);
    }

    /**
     * 公用方法：标的类共通方法 - 付息截止日判断
     *
     * @param sDate 收息日【必输项】
     * @param finprodId 金融产品代码【必输项】
     * @return 是否付息截止日（Boolean）true:是 false:否
     * @see
     * @since 1.0
     */
    public FinCashPlanDto payDeadlineJudgment(String finprodId, Date sDate) {
        return finProductCommonBatchService.payDeadlineJudgment(finprodId, sDate);
    }

    /**
     * 公共方法：标的类共通方法-查询交易信息List
     *
     * @param finprodId 金融产品代码【必输项】
     * @param finprodType 金融产品类型【必输项】
     * @param branch 分支序号
     * @return 查询结果集(List<交易信息Dto>）
     * @see
     * @since 1.0
     */
    public List<TradeInfoDto> getTradeInfoDtoList(String finprodId, String finprodType,
                                                  Long branch) {
        return finProductCommonBatchService.getTradeInfoDtoList(finprodId, finprodType, branch);
    }

    /**
     * 当前付息区间查询
     *
     * @param busiId 业务明细代码
     * @param happenDate 发生日
     * @return
     * @see
     * @since 1.0
     */
    public List<TargetCashPlan> getCurrentIntRange(String busiId, Date happenDate) {
        return finProductCommonBatchService.getCurrentIntRange(busiId, happenDate);
    }

    /**
     * 公共方法：标的类共通方法-利率行情信息查询
     *
     * @param selectDate 查询日期
     * @param intRateId 行情代码
     * @return MAP<行情代码，利率行情信息>
     * @see
     * @since 1.0
     */
    public Map<String, MstIntRateInfo> getValIntRateInfo(Date selectDate, String intRateId) {
        return finProductCommonBatchService.getValIntRateInfo(selectDate, intRateId);
    }

    /**
     * 市价查询-计划类净值型
     *
     * @param happenDate 发生日
     * @param finProdId 金融产品代码
     * @return BigDecimal 市价
     * @see
     * @since 1.0
     */
    public MarketPriceDto getFundMarketPriceForPlanAsset(Date happenDate, String finProdId, String booksetId) {
        return finProductCommonBatchService.getFundMarketPriceForPlanAsset(happenDate, finProdId, booksetId);
    }

    /**
     * 质押品信息查询 <功能详细描述>
     *
     * @param finprodId
     * @param finprodType
     * @return
     * @see
     * @since 1.0
     */
    public List<PledgeInfoDto> getPledgeInfo(String finprodId, String finprodType) {
        return finProductCommonBatchService.getPledgeInfo(finprodId, finprodType);
    }

    /**
     *  根据账套代码查询核算主体
     * @param booksetId 账套代码
     * @return
     */
    public FinProduct queryAcctSubject(String booksetId) {
        return finProductCommonBatchService.queryAcctSubject(booksetId);
    }



    /**
     * 百元券面共通查询方法
     * 
     * @param finprodId 标的代码
     * @param happenDate 发生日
     * @return faceValue 处理后的百元券面
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public BigDecimal getFaceValue(String finprodId, Date happenDate) {
        BigDecimal faceValue = new BigDecimal("100");
        BigDecimal payedFaceValue = new BigDecimal("0");
        // 增加查询类型Dto
        TargetSearchDto searchDto = new TargetSearchDto();
        searchDto.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
        searchDto.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        List<TargetSearchDto> listSearchDto = new ArrayList<>();
        // 放入list中传参
        listSearchDto.add(searchDto);
        // 获取现金流计划结果集
        List<TargetCashPlan> listDto = (List<TargetCashPlan>)this.getTargetInfo(finprodId,
            listSearchDto).get(searchDto);
        for (TargetCashPlan plan : listDto) {
            // 计息结束日 <= 发生日
            if (DateUtil.compareDate(plan.getMdate(), happenDate) <= 0) {
                payedFaceValue = ArithmeticUtil.doAdd(payedFaceValue, plan.getPrinAmt());
            }
        }
        faceValue = ArithmeticUtil.doSub(faceValue, payedFaceValue);
        return faceValue;
    }

    /**
     * 查询适用估值方案
     * @param queryDate
     * @param booksetId
     * @param invAim
     * @param finProduct
     * @return
     */
    public SchemeDetailDto querySchemaDetail(Date queryDate, String booksetId, String invAim,TargetFinProduct finProduct) {
        SchemeDetailDto schemeDetail = null;


        // 如果启用了特殊资产估值方案则先查询特殊资产估值方案
        String specialAssetConfigSwitch = CacheUtil.getSysParam("SPECIAL_ASSET_CONFIG_SWITCH");
        if ("ON".equals(specialAssetConfigSwitch)) {
            List<SchemeDetailDto> specialSchemeDetailDtoList = finProductCommonBatchService.getSpecialSchemeDetail(queryDate, booksetId, DictCodeListExt1.配置方案类型_估值方案.getValue(), finProduct.getFinprodId());
            if (!CollectionUtils.isEmpty(specialSchemeDetailDtoList)) {
                for (SchemeDetailDto item : specialSchemeDetailDtoList) {
                    if (item.getInvAim() == null || item.getInvAim().equals(invAim)) {
                        schemeDetail = item;
                        break;
                    }
                }
            }
        }

        //上面没找到再从估值方案找
        if(schemeDetail == null){
            Map<String, Object> params = new HashMap<>();
            params.put("happenDate", queryDate);
            params.put("booksetId", booksetId);
            List<SchemeDetailDto> listSchemeDetailDto =finProductCommonBatchService.getSchemeDetail(queryDate, booksetId, finProduct.getType18(), DictCodeListExt1.配置方案类型_估值方案.getValue(), finProduct.getTradeMarket(), finProduct.getType1(), invAim);
            if(!CollectionUtils.isEmpty(listSchemeDetailDto)){
                schemeDetail = listSchemeDetailDto.get(0);
            }
        }

        return schemeDetail;
    }


    /**
     * 市价查询-债券
     *
     * @param happenDate 发生日
     * @param booksetId 账套代码
     * @param finProdId 产品代码
     * @param invAim 投资目的
     * @return 市价净值、估值日期
     * @see
     * @since 1.0
     */
    public MarketPriceDto getBondMarketPrice(Date happenDate, String booksetId, String finProdId,
                                             String invAim) {
        // 1.定义变量
        // 定义 变量.市价Dto
        MarketPriceDto marketPriceDto = new MarketPriceDto();
        // 市价净值
        marketPriceDto.setPrice(BigDecimal.ZERO);
        // 估值方案Dto
        SchemeDetailDto schemeDetailDto = null;
        //行情查询日期
        Date queryDate=null;

        // 3.查询债券基本信息----调用共通方法-【标的类共通方法设计】
        List<TargetSearchDto> queryCriteria = new ArrayList<>();
        // 定义查询对象targetSearchDto，queryType设值RST_FINPRODUCT
        TargetSearchDto targetSearchDto = new TargetSearchDto();
        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteria.add(targetSearchDto);
        // 调用共通方法查询标的信息C---targetFinProduct
        Map<TargetSearchDto, Object> targetSearchMap = this.getTargetInfo(finProdId,queryCriteria);
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetSearchMap.get(targetSearchDto);
        // 判断是否为空
        if (targetFinProduct == null) {
            return marketPriceDto;
        }
        // 赋值市场代码
//        String finprodMarketId = targetFinProduct.getFinprodMarketId();

        // 5.0
        // 查看数据库配置表 - 特殊资产配置开关是否。只有开关打开才执行特殊资产逻辑
        //查询估值方案
        schemeDetailDto = querySchemaDetail(happenDate,booksetId,invAim,targetFinProduct);
        if(schemeDetailDto==null){
//            logger.warn("账套代码："+booksetId+"，资产代码："+finProdId+"，投资目的："+invAim+"找不到估值方案，市价按0计算.");
            marketPriceDto.setPrice(BigDecimal.ZERO);
            return marketPriceDto;
        }

        // 5.2 根据估值方案取对应市价
        // 参数.投资目的 == 'AC'
        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(invAim)) {
            // 变量.市价净价 = 0;
            // marketPrice = BigDecimal.ZERO;
            marketPriceDto.setPrice(BigDecimal.ZERO);
        }
        // 参数.投资目的 = “FVTPL” || （参数.投资目的 = “FVOCI”)
        else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)
                 || DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)) {
            // 估值方法为成本法
            if (DictCodeListExt1.估值方法_成本法.getValue().equals(schemeDetailDto.getValuationMethod())
                || DictCodeListExt1.估值方法_摊余成本法.getValue().equals(schemeDetailDto.getValuationMethod())) {
                // marketPrice = BigDecimal.ZERO;
                marketPriceDto.setPrice(BigDecimal.ZERO);
            }
            // 市值法、市值成本孰低法、模型法
            else {
                // 获取行情查询日期
                 queryDate = getQuotationQueryDate(schemeDetailDto.getValuationDate(), happenDate);
                // 查询行情价格
                marketPriceDto = getQuotation(schemeDetailDto,queryDate, targetFinProduct.getFinprodMarketId(),targetFinProduct.getType18());
                // 小数位处理
                //setQuotaDigit(schemeDetailDto.getQuotaDigit(), marketPriceDto);
            }
        }

        // modified by wangzhiang 零息贴现综合收益逻辑到取市价共通里 20181212 start
        // 变量.标的信息.息票品种为零息或贴现时
        if (DictCodeList.息票品种_零息.getValue().equals(targetFinProduct.getCouponSpecies())
            || DictCodeList.息票品种_贴现.getValue().equals(targetFinProduct.getCouponSpecies())) {
            BigDecimal marketPrice = marketPriceDto.getPrice();
            // if（变量.市价 = null）
            if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)
                    /**add by wujie on 20190621 start**/
                    &&
                    !(
                            DictCodeListExt1.估值方法_摊余成本法.getValue().equals(schemeDetailDto.getValuationMethod())
                                    ||
                                    DictCodeListExt1.估值方法_成本法.getValue().equals(schemeDetailDto.getValuationMethod())
                    )
            /**add by wujie on 20190621 end**/
            ) {
                // 2018年12月14日 增加开关控制
                // 需求：零息、贴现债的【市价】计算方式改为取中债估值，【交易性】时取中债净价，【非交易性】时取中债全价；计算影子价格的市价取中债全价。
                // 注意：若是查市场行情时，开关需要设为01

                // modify by wj 20200620 二级法人改造 五个特殊参数 修改 为特殊取值方法
                String institutionCode = MultilevelDataAuthorityUtil.getOrgCodeByBussinessIDType(booksetId, com.joyintech.framework.constant.DictCodeList.联社模式查询数据权限_修改和删除.getValue(), DictCodeList.共通业务类型_账套管理.getValue());
                if(StringUtil.equals(CacheUtil.getSpecialSysParam("PRICE_CAL_TYPE",institutionCode), GlobalConstant.TYPE_02)){
                    // 直接取中债全价
                    marketPrice = marketPriceDto.getAllPrice();
                }
                else{
                    //变量.市价 = round（变量.市价 +（计提类账务处理结果.发生日-变量.标的信息.起息日+1）/（变量.标的信息.实际到期日-变量.标的信息.起息日）*（100-C.发行价），2）
                    //（计提类账务处理结果.发生日-变量.标的信息.起息日+1）
                    long day1 = DateUtil.getBetweenDayNumber(targetFinProduct.getVdate(), happenDate) + 1;
                    // （变量.标的信息.实际到期日-变量.标的信息.起息日））
                    long day2 = DateUtil.getBetweenDayNumber(targetFinProduct.getVdate(),
                        targetFinProduct.getActmdate());
                    BigDecimal day = ArithmeticUtil.divide(new BigDecimal(day1), new BigDecimal(day2));
                    BigDecimal priceAdd = ArithmeticUtil.multiply(day,
                        ArithmeticUtil.subtract(new BigDecimal(100), targetFinProduct.getIssuePrice()));
                    if(marketPrice ==null || marketPrice.compareTo(BigDecimal.ZERO)==0){
                        marketPrice =targetFinProduct.getIssuePrice();
                    }
                    marketPrice = ArithmeticUtil.doAdd(marketPrice, priceAdd);
                    // 市价Dto.市价净价 = 变量.市价净价
                }
                marketPriceDto.setPrice(marketPrice);
            }
        }

        if(DictCodeListExt1.证券品种_债券.getValue().equals(targetFinProduct.getType18())) {
            /** add by yanya on 20200116 市价查询-债券 价格等于"全价"处理 stat**/
            //if(变量.价格类型 = “全价“)
            if (DictCodeListExt3.价格类别_全价.getValue().equals(schemeDetailDto.getPriceClass())) {
                if (marketPriceDto.getPrice() != null && marketPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal 本日应计利息余额 = this.getBondFullPriceInterestBalance(finProdId, happenDate);
                    //出参.市价净价 - 变量.本日应计利息余额
                    marketPriceDto.setPrice(ArithmeticUtil.subtract(marketPriceDto.getPrice(), 本日应计利息余额));
                } else {
                    if (DictCodeList.息票品种_零息.getValue().equals(targetFinProduct.getCouponSpecies())
                            || DictCodeList.息票品种_贴现.getValue().equals(targetFinProduct.getCouponSpecies())) {
                        marketPriceDto.setPrice(targetFinProduct.getIssuePrice());
                    }
                }
            } else {
                if ((DictCodeList.息票品种_零息.getValue().equals(targetFinProduct.getCouponSpecies())
                        || DictCodeList.息票品种_贴现.getValue().equals(targetFinProduct.getCouponSpecies()))
                        && (marketPriceDto.getPrice() == null || marketPriceDto.getPrice().compareTo(BigDecimal.ZERO) == 0)) {
                    marketPriceDto.setPrice(targetFinProduct.getIssuePrice());
                }
            }
            /** add by yanya on 20200116 市价查询-债券 价格等于"全价"处理 end**/
        }

        // 小数位处理
//        setQuotaDigit(schemeDetailDto.getQuotaDigit(), marketPriceDto);
        setQuotaDigit(schemeDetailDto.getQuotaDigit(), marketPriceDto,schemeDetailDto.getPriceTailMode());
        // modified by wangzhiang 零息贴现综合收益逻辑到取市价共通里 20181212 end


        if(queryDate!=null){
            //理论估值日期
            String isHoliday = calculationCommonService.getIsHoliday(queryDate,targetFinProduct.getCalendarId());

            if("0".equals(isHoliday)){
                marketPriceDto.setSchemeValDate(queryDate);
            }else{
                marketPriceDto.setSchemeValDate(calculationCommonService.getCDate(queryDate,targetFinProduct.getCalendarId() ));
            }
        }

        // add by weipeng  20210807：资产对应计算用估值行情入库存表落地 start
        // 估值方案代码
        marketPriceDto.setSchemeId(schemeDetailDto.getSchemeId());
        // 估值方案明细代码
        marketPriceDto.setSchemeDetailId(schemeDetailDto.getSchemeDetailId());
        // add by weipeng  20210807：资产对应计算用估值行情入库存表落地 end

        return marketPriceDto;
    }


    /**
     * 价格类型为全价时，计算利息余额
     * @param finProdId
     * @param happenDate
     * @return
     */
    private BigDecimal getBondFullPriceInterestBalance(String finProdId, Date happenDate) {
        /** add by yanya on 20200117 价格类型为全价时，计算利息余额 start**/
        /**
         参数名：					查询标的信息
         账务明细代码					入参.金融产品代码
         查询类型					查询类型参考下文赋值
         返回值：					标的结果集
         **/
        List<TargetSearchDto> queryCriteriaQJ = new ArrayList<>();
        // 定义查询对象targetSearchDto，queryType设值RST_FINPRODUCT
        //第1条记录				标的信息
        TargetSearchDto 标的信息 = new TargetSearchDto();
        //数据代码				RST_FINPRODUCT
        标的信息.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteriaQJ.add(标的信息);
        //第2条记录  还本计划
        TargetSearchDto 还本计划 = new TargetSearchDto();
        //数据代码				RST_CASH_PLAN
        还本计划.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
        //现金流类型				本金
        还本计划.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        queryCriteriaQJ.add(还本计划);
        //第3条记录				付息计划
        TargetSearchDto 付息计划 = new TargetSearchDto();
        //数据代码				RST_CASH_PLAN
        付息计划.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
        //现金流类型				利息
        付息计划.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        queryCriteriaQJ.add(付息计划);
        //第4条记录				报价计划
        TargetSearchDto 报价计划 = new TargetSearchDto();
        //数据代码				RST_CASH_CALCRULE
        报价计划.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
        //现金流类型				利息
        报价计划.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        queryCriteriaQJ.add(报价计划);
        //第5条记录				现金流支付规则
        TargetSearchDto 现金流支付规则 = new TargetSearchDto();
        //数据代码				RST_CASH_PAYRULE
        现金流支付规则.setQueryType(ValDictCodeList.标的_现金流支付规则.getValue());
        //现金流类型				利息
        现金流支付规则.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        queryCriteriaQJ.add(现金流支付规则);
        // 调用共通方法查询标的信息C---targetFinProduct
        Map<TargetSearchDto, Object> targetSearchQJMap = this.getTargetInfo(finProdId, queryCriteriaQJ);
        //1 定义 		变量.标的信息 = 上述共通返回标的信息
        TargetFinProduct targetFinProductQJ = (TargetFinProduct) targetSearchQJMap.get(标的信息);
        //2 定义 		变量.还本列表 = 上述共通返回还本列表
        List<TargetCashPlan> 还本计划列表 = (List<TargetCashPlan>) targetSearchQJMap.get(还本计划);
        //3 定义 		变量.付息列表 = 上述共通返回的计划列表
        List<TargetCashPlan> 付息计划列表 = (List<TargetCashPlan>) targetSearchQJMap.get(付息计划);
        //4 定义 		变量.报价列表 = 上述共通返回报价列表
        List<TargetCashCalcRule> 报价计划列表 = (List<TargetCashCalcRule>) targetSearchQJMap.get(报价计划);
        //5 定义 		变量.支付规则 = 上述共通返回支付规则列表
        List<TargetCashPayRule> 现金流支付规则列表 = (List<TargetCashPayRule>) targetSearchQJMap.get(现金流支付规则);
        /**
         调用共通
         参数名：					百元券面查询
         发生日					入参.发生日
         标的代码					入参.金融产品代码
         返回值：					百元券面面值
         **/
        //变量.百元券面面值 						=	百元券面面值
        BigDecimal 百元券面面值 = this.getFaceValue(finProdId, happenDate);
        /**
         调用债券应计利息共通				*共通调用，同债券计提批处理逻辑
         参数名：					债券应计利息计算
         计算日					入参.发生日
         资产信息					变量.资产信息
         付息计划列表					变量.付息计划列表
         债券利率重置信息					变量.报价信息
         当前面值					变量.百元券面面值
         返回值：					应计利息
         **/
        DrawBaseInfoSelectComResult acctEntity = new DrawBaseInfoSelectComResult();
        acctEntity.setHappenDate(happenDate);
        AccurIntCalOutParameter accurIntCalOutParameter = calculationCommonService.getCalculate(new DrawBaseInfoSelectComDTO(),
                targetFinProductQJ, 付息计划列表, 报价计划列表, 现金流支付规则列表, 百元券面面值, acctEntity);
        //应计利息
        BigDecimal 应计利息 = accurIntCalOutParameter.getInterest();
        //返回值.应计利息做小数位处理
        //if（标的信息.交易场所 = "银行间"）
        if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(targetFinProductQJ.getTradeMarket())) {
            //调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_BANK
            String SEC_INT_SCALE_BANK = CacheUtil.getSysParam("SEC_INT_SCALE_BANK");
            应计利息 = ArithmeticUtil.round(应计利息,
                    Integer.parseInt(StringUtils.isEmpty(SEC_INT_SCALE_BANK) ? "12" : SEC_INT_SCALE_BANK),null);
        }
        //else if（标的信息.交易场所 = "上交所" or "深交所"）
        else if(DictCodeListExt1.估值交易市场_上交所.getValue().equals(targetFinProductQJ.getTradeMarket())
                || DictCodeListExt1.估值交易市场_深交所.getValue().equals(targetFinProductQJ.getTradeMarket())) {
            //调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_EXCHANGE
            String SEC_INT_SCALE_EXCHANGE = CacheUtil.getSysParam("SEC_INT_SCALE_EXCHANGE");
            应计利息 = ArithmeticUtil.round(应计利息,
                    Integer.parseInt(StringUtils.isEmpty(SEC_INT_SCALE_EXCHANGE) ? "8" : SEC_INT_SCALE_EXCHANGE),null);
        }
        //else
        else {
            String SEC_INT_SCALE_BANK = CacheUtil.getSysParam("SEC_INT_SCALE_BANK");
            //调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_BANK
            应计利息 = ArithmeticUtil.round(应计利息,
                    Integer.parseInt(StringUtils.isEmpty(SEC_INT_SCALE_BANK) ? "12" : SEC_INT_SCALE_BANK),null);
        }
        BigDecimal 本日应计利息余额 = BigDecimal.ZERO;
        if(应计利息 != null) {
            //变量.本日应计利息余额	=	上述返回值 * 变量.百元券面面值 /100   调用金额共通方法设置有效小数位
            本日应计利息余额 = ArithmeticUtil.multiply(应计利息, 百元券面面值);
            本日应计利息余额 = ArithmeticUtil.divide(本日应计利息余额, new BigDecimal(100), NumberEnum.HIGH_PREC_NUM.getCode());
            本日应计利息余额 = ArithmeticUtil.round(本日应计利息余额, NumberEnum.MONEY);
        }
        /** add by yanya on 20200117 价格类型为全价时，计算利息余额 end**/
        return 本日应计利息余额;
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param valuationDate
     * @param happenDate
     * @return
     * @see
     * @since 1.0
     */
    private Date getQuotationQueryDate(String valuationDate, Date happenDate) {
        Date queryDate = happenDate;//默认为行情日
        // 估值日期：行情日-1
        if (DictCodeListExt1.估值日期_行情日减1.getValue().equals(valuationDate)) {
            // 变量.查询日期 = 发生日-1
            queryDate = DateUtil.addDay(happenDate, -1);
        }
        // 估值日期：行情日
        if (DictCodeListExt1.估值日期_行情日.getValue().equals(valuationDate)) {
            // 变量.查询日期 = 发生日
            queryDate = happenDate;
        }
        // 估值日期：行情日+1
        if (DictCodeListExt1.估值日期_行情日加1.getValue().equals(valuationDate)) {
            // 变量.查询日期 = 发生日+1
            queryDate = DateUtil.addDay(happenDate, 1);
        }
        return queryDate;
    }


    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param quotaDigit
     * @param marketPriceDto
     * @see
     * @since 1.0
     */
    public void setQuotaDigit(BigDecimal quotaDigit, MarketPriceDto marketPriceDto, String priceTailMode) {
        // 按照行情位数调用金额共通方法设置有效小数位（高精度）
        if (quotaDigit != null) {
            RoundingMode roundingMode = null;
            if(DictCodeList.估值舍入规则_四舍五入.getValue().equals(priceTailMode)){
                roundingMode = RoundingMode.HALF_UP;
            }
            else if(DictCodeList.估值舍入规则_舍位.getValue().equals(priceTailMode)){
                roundingMode = RoundingMode.DOWN;
            }
            else if(DictCodeList.估值舍入规则_进位.getValue().equals(priceTailMode)){
                roundingMode = RoundingMode.UP;
            }
            marketPriceDto.setPrice(ArithmeticUtil.round(marketPriceDto.getPrice(),
                quotaDigit.intValue(), roundingMode));
        }
    }


    /**
     * 估值市价查询共通
     * @param schemeDetailDto
     *  估值方案配置
     * @param queryDate
     *  查询日期
     * @param finprodMarketId
     *  市场代码
     * @param queryType
     *  查询资产类型
     * @return
     */
    private MarketPriceDto getQuotation(SchemeDetailDto schemeDetailDto,Date queryDate,String finprodMarketId,String queryType) {
//            Date queryDate, String valuationPrice,
//                                        String valuationPrice2, String finprodMarketId,
//                                        String queryType, String priceClass) {
        MarketPriceDto marketPriceDto = getMarketPriceByValPrice(
                schemeDetailDto.getValuationPrice(), queryDate, finprodMarketId, queryType, schemeDetailDto.getPriceClass(),schemeDetailDto.getValuationMethod());
        if(marketPriceDto ==null){
            marketPriceDto =new MarketPriceDto();
        }
        return marketPriceDto;
    }


    /**
     * 获取 市价Dto
     * 
     * @param valuePrice 估值市价
     * @param queryDate 查询日期
     * @param finprodMarketId 市场代码
     * @param queryType 查询类型：基金、债券
     * @param priceClass 价格种类：全价、净价
     * @return
     * @see
     * @since 1.0
     */
    private MarketPriceDto getMarketPriceByValPrice(String valuePrice, Date queryDate,
                                                    String finprodMarketId, String queryType, String priceClass,String valuationMethod) {
        MarketPriceDto marketPriceDto = new MarketPriceDto();
        String valueSource = null;
        boolean isMarketInfo = false;
        if(DictCodeListExt1.估值方法_模型法.getValue().equals(valuationMethod)){
            valueSource = DictCodeListExt1.估值来源_模型法_MOD.getValue();
        }else{
            if(DictCodeList.估值市价_收盘价.getValue().equals(valuePrice)
                    || DictCodeList.估值市价_平均价.getValue().equals(valuePrice)
                    || DictCodeList.估值市价_最高价.getValue().equals(valuePrice)
                    || DictCodeList.估值市价_最低价.getValue().equals(valuePrice)
                    || DictCodeList.估值市价_买一价.getValue().equals(valuePrice)
                    || DictCodeList.估值市价_卖一价.getValue().equals(valuePrice)){
                isMarketInfo = true;
            }
            //TODO 其他得估值市价需要补充
            else{
                valueSource = valuePrice;
            }


        }
        if (isMarketInfo) {
            // 定义 变量.估值市价类型
            String valuePriceType = "";
            if (DictCodeList.估值市价_收盘价.getValue().equals(valuePrice)) {
                valuePriceType = "01";
            }
            else if (DictCodeList.估值市价_平均价.getValue().equals(valuePrice)) {
                valuePriceType = "02";
            }
            else if (DictCodeList.估值市价_最高价.getValue().equals(valuePrice)) {
                valuePriceType = "03";
            }
            else if (DictCodeList.估值市价_最低价.getValue().equals(valuePrice)) {
                valuePriceType = "04";
            }
            else if (DictCodeList.估值市价_买一价.getValue().equals(valuePrice)) {
                valuePriceType = "05";
            }
            else if (DictCodeList.估值市价_卖一价.getValue().equals(valuePrice)) {
                valuePriceType = "06";
            }
            if (StringUtil.isNotEmpty(valuePriceType)) {
                MarketInfoDto marketInfoDto = null;
                // 市价查询-基金
                if (DictCodeListExt1.金融产品类型_基金.getValue().equals(queryType)) {
                    // 变量.交易所基金估值行情DTO_1 = 【4.2】查询结果.get(0);
                    FundValuationDto fundValuationDto = CollectionUtils.isEmpty(
                        finProductCommonMapper.getFundValuation(queryDate, finprodMarketId,
                            valuePriceType)) ? null : finProductCommonMapper.getFundValuation(
                                queryDate, finprodMarketId, valuePriceType).get(0);
                    marketInfoDto = fundValuationDto;
                }
                // 市价查询-债券
                else {
                    // 变量.债券行情第三方DTO = 【4.2】查询结果.get(0);
                    BondMarketInfoDto bondMarketInfoDto = CollectionUtils.isEmpty(
                        finProductCommonMapper.getBondMarketInfo(finprodMarketId, queryDate,
                            valuePriceType)) ? null : finProductCommonMapper.getBondMarketInfo(
                                finprodMarketId, queryDate, valuePriceType).get(0);
                    marketInfoDto = bondMarketInfoDto;
                }
                if (marketInfoDto != null) {
                    // DTO.估值日期 = 估值日期
                    marketPriceDto.setValDate(marketInfoDto.getValueDate());
                    // DTO.市价净值 = 通过债券行情第三方获取市价净价
                    marketPriceDto.setPrice(this.getPriceFromMarket(marketInfoDto, valuePrice, priceClass));
                    //DTO.估值来源=估值来源
                    marketPriceDto.setValueSource(marketInfoDto.getValueSource());
                    return marketPriceDto;
                }

            }
        }
        else {
//            // 市价查询-基金
//            if (DictCodeListExt1.证券品种_基金.getValue().equals(queryType)) {
//                // 变量.场外基金估值行情DTO = 【4.1】查询结果.get(0);
//                List<FinProductValuationDto> listFinProductValuationDto = finProductCommonMapper.getFinProValuation(queryDate, finprodMarketId);
//                if (!CollectionUtils.isEmpty(listFinProductValuationDto)) {
//                    MarketPriceDto marketPriceDto = new MarketPriceDto();
//                    // 第一市价DTO.估值日期 = 估值日期
//                    marketPriceDto.setValDate(listFinProductValuationDto.get(0).getValDate());
//                    // 第一市价DTO.市价净价 = 【4.1】查询结果.get(0).估值;
//                    marketPriceDto.setPrice(listFinProductValuationDto.get(0).getValuation());
//                    return marketPriceDto;
//                }
//            }
//            /**add by wujie on 20201015 end**/
//            // 市价查询-债券--债券时估值来源不为空，否则不查
//            else
            if (DictCodeListExt1.证券品种_债券.getValue().equals(queryType)) {
                if(StringUtil.isNotEmpty(valueSource)) {
                    // 变量.债券行情 = 【4.1】查询结果.get(0);
                    List<BondValuationInfoDto> listBondValuationInfoDto = finProductCommonMapper.getBondValuationInfo(
                            finprodMarketId, queryDate, valueSource);
                    if (!CollectionUtils.isEmpty(listBondValuationInfoDto)) {
                        // 第一市价DTO.估值日期 = 【4.1】查询结果.get(0).估值日期
                        marketPriceDto.setValDate(listBondValuationInfoDto.get(0).getValueDate());
                        // 第一市价DTO.市价净价 = 【4.1】查询结果.get(0).市价净价;
                        // modify by yanya on 20200212 全价时取全价
                        if (DictCodeListExt3.价格类别_全价.getValue().equals(priceClass)) {
                            marketPriceDto.setPrice(listBondValuationInfoDto.get(0).getAllPrice());
                        } else {
                            marketPriceDto.setPrice(listBondValuationInfoDto.get(0).getPrice());
                        }
                        // 2018年12月14日 魏鹏：返回结果接收【4.1】查询结果.get(0).市价全价
                        marketPriceDto.setAllPrice(listBondValuationInfoDto.get(0).getAllPrice());
                        //DTO.估值来源=估值来源
                        marketPriceDto.setValueSource(listBondValuationInfoDto.get(0).getValueSource());
                        return marketPriceDto;
                    }
                }
            }
            //市价查询-其他资产类场外估值行情查询
            else {
                List<FinProductValuationDto> listFinProductValuationDto = finProductCommonMapper.getFinProValuation(queryDate, finprodMarketId);
                if (!CollectionUtils.isEmpty(listFinProductValuationDto)) {
                    marketPriceDto.setValDate(listFinProductValuationDto.get(0).getValDate());
                    marketPriceDto.setPrice(listFinProductValuationDto.get(0).getValuation());
                    return marketPriceDto;
                }
            }

        }
        return marketPriceDto;
    }

    /**
     * 根据行情Dto查询市价净价
     * 
     * @param dto
     * @param valuePrice
     * @return
     * @see
     * @since 1.0
     */
    private BigDecimal getPriceFromMarket(MarketInfoDto dto, String valuePrice, String priceClass) {
        BigDecimal marketPrice = BigDecimal.ZERO;
        // 判断 变量.债券行情第三方DTO != null,则说明取的是债券行情第三方
        if (dto != null) {
            // if 变量.估值市价=“收盘价”
            if (DictCodeList.估值市价_收盘价.getValue().equals(valuePrice)) {
                if(DictCodeListExt3.价格类别_全价.getValue().equals(priceClass)){
                    // 变量.市价净价 = 变量.债券行情第三方DTO.收盘价全价;
                    marketPrice = dto.getClosePriceFull();
                }
                else{
                    // 变量.市价净价 = 变量.债券行情第三方DTO.收盘价净价;
                    marketPrice = dto.getClosePrice();
                }
            }
            // if 变量.估值市价=“平均价”
            else if (DictCodeList.估值市价_平均价.getValue().equals(valuePrice)) {
                if(DictCodeListExt3.价格类别_全价.getValue().equals(priceClass)){
                    // 变量.市价净价 = 变量.债券行情第三方DTO.平均价全价;
                    marketPrice = dto.getAveragePriceFull();
                }
                else{
                    // 变量.市价净价 = 变量.债券行情第三方DTO.平均价净价;
                    marketPrice = dto.getAveragePrice();
                }
            }
            // if 变量.估值市价=“最高价”
            else if (DictCodeList.估值市价_最高价.getValue().equals(valuePrice)) {
                if(DictCodeListExt3.价格类别_全价.getValue().equals(priceClass)){
                    // 变量.市价净价 = 变量.债券行情第三方DTO.最高价全价;
                    marketPrice = dto.getMaxPriceFull();
                }
                else{
                    // 变量.市价净价 = 变量.债券行情第三方DTO.最高价净价;
                    marketPrice = dto.getMaxPrice();
                }
            }
            // if 变量.估值市价=“最低价”
            else if (DictCodeList.估值市价_最低价.getValue().equals(valuePrice)) {
                if(DictCodeListExt3.价格类别_全价.getValue().equals(priceClass)){
                    // 变量.市价净价 = 变量.债券行情第三方DTO.最低价全价;
                    marketPrice = dto.getMinPriceFull();
                }
                else{
                    // 变量.市价净价 = 变量.债券行情第三方DTO.最低价净价;
                    marketPrice = dto.getMinPrice();
                }
            }
            // if 变量.估值市价=“买一价”
            else if (DictCodeList.估值市价_买一价.getValue().equals(valuePrice)) {
                if(DictCodeListExt3.价格类别_全价.getValue().equals(priceClass)){
                    // 变量.市价净价 = 变量.债券行情第三方DTO.买一价全价;
                    marketPrice = dto.getBuyOnePriceFull();
                }
                else{
                    // 变量.市价净价 = 变量.债券行情第三方DTO.买一价净价;
                    marketPrice = dto.getBuyOnePrice();
                }
            }
            // if 变量.估值市价=“卖一价”
            else if (DictCodeList.估值市价_卖一价.getValue().equals(valuePrice)) {
                if(DictCodeListExt3.价格类别_全价.getValue().equals(priceClass)){
                    // 变量.市价净价 = 变量.债券行情第三方DTO.卖一价全价;
                    marketPrice = dto.getSellOnePriceFull();
                }
                else{
                    // 变量.市价净价 = 变量.债券行情第三方DTO.卖一价净价;
                    marketPrice = dto.getSellOnePrice();
                }
            }
        }
        return marketPrice;
    }

    /**
     * 市价查询-基金
     * 
     * @param happenDate 发生日
     * @param booksetId 账套代码
     * @param finProdId 金融产品代码
     * @return 估值、估值日期
     * @see
     * @since 1.0
     */
    public MarketPriceDto getFundMarketPrice(Date happenDate, String booksetId, String finProdId) {
        // 1.定义估值
        // BigDecimal value = BigDecimal.ZERO;
        // 定义 变量.市价Dto
        MarketPriceDto marketPriceDto = new MarketPriceDto();
        marketPriceDto.setPrice(BigDecimal.ZERO);

        Date queryDate =null;

        // 估值方案Dto
        SchemeDetailDto schemeDetailDto = null;

        // 2.调用共通查询估值方案
        Map<String, Object> params = new HashMap<>();
        params.put("happenDate", happenDate);
        params.put("booksetId", booksetId);

        // 3.查询债券基本信息----调用共通方法-【标的类共通方法设计】
        List<TargetSearchDto> queryCriteria = new ArrayList<>();
        // 定义查询对象targetSearchDto，queryType设值RST_FINPRODUCT
        TargetSearchDto targetSearchDto = new TargetSearchDto();
        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteria.add(targetSearchDto);
        // 调用共通方法查询标的信息C---targetFinProduct
        Map<TargetSearchDto, Object> targetSearchMap = this.getTargetInfo(finProdId,
            queryCriteria);
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetSearchMap.get(targetSearchDto);
        // 判端 标的信息C 是否为空
        if (targetFinProduct == null) {
            return marketPriceDto;
        }
        // 赋值市场代码
        String finprodMarketId = targetFinProduct.getFinprodMarketId();

        // update by yangjian 2018/11/28 FOR BUG[GZHSXT-883],获取方案不需要交易场所过滤
//        List<SchemeDetailDto> listSchemeDetailDto = this.getSchemeDetail(happenDate, booksetId,
//            DictCodeListExt1.证券品种_基金.getValue(), DictCodeListExt1.配置方案类型_估值方案.getValue(),
//            targetFinProduct.getTradeMarket(), targetFinProduct.getType1(), null);
        List<SchemeDetailDto> listSchemeDetailDto = this.getSchemeDetail(happenDate, booksetId,
            DictCodeListExt1.证券品种_基金.getValue(), DictCodeListExt1.配置方案类型_估值方案.getValue(),
            targetFinProduct.getTradeMarket(), targetFinProduct.getType1(), null);


        if (CollectionUtils.isEmpty(listSchemeDetailDto) || listSchemeDetailDto.get(0) == null) {
            return marketPriceDto;
        }
        schemeDetailDto = listSchemeDetailDto.get(0);

        // 4.查询估值信息（生成mapper查询方法）
        // 4.1、查询场外基金估值行情：
        // 4.2、交易所基金估值行情：
        // 5.逻辑判断市价净值取值
        // 5.1 判断取出估值方案
        // 【交易场所对应数据字典CODE：VALUATION_TRADE_MARKET】
        // 【分类1对应数据字典CODE：FUND_TYPE】
        // FOR (产品估值方案配置明细 D:步骤2返回值估值方案配置LIST）
//        for (SchemeDetailDto item : listSchemeDetailDto) {
//            // if(D.证券细分类 != null)
//            if (item.getSecDetailType() != null) {
//                // if(C.资产二级类型 = D.证券细分类 )
//                // 标的信息中的TYPE1 对应的就是 资产二级类型
//                if (StringUtils.equals(targetFinProduct.getType1(), item.getSecDetailType())) {
//                    // 变量.估值方案DTO = D exit
//                    schemeDetailDto = item;
//                    break;
//                }
//            }
//            else {
//                // if(D.证券分类 != null)
//                if (item.getSecType() != null) {
//                    // if (D.交易场所 = “场外” and C.分类1 !=“ETF指数基金”）
//                    if (DictCodeListExt1.估值交易市场_场外.getValue().equals(item.getTradeMarket())
//                        && !DictCodeList.基金类型_ETF指数基金.getValue().equals(
//                            targetFinProduct.getType1())) {
//                        // 变量.估值方案DTO = D exit
//                        schemeDetailDto = item;
//                        break;
//                    }
//                    // if (D.交易场所 = “交易所” and C.分类1 =“ETF指数基金”）
//                    if (DictCodeListExt1.估值交易市场_交易所.getValue().equals(item.getTradeMarket())
//                        && DictCodeList.基金类型_ETF指数基金.getValue().equals(
//                            targetFinProduct.getType1())) {
//                        // 变量.估值方案DTO = D exit
//                        schemeDetailDto = item;
//                        break;
//                    }
//                }
//            }
//        }
//        // 若估值方案Dto为空，则返回null;
//        if (schemeDetailDto == null) {
//            return null;
//        }
        // 5.2 根据估值方案取对应市价
        // 参数.投资目的 = “FVTPL” 并且 D.投资目的=“FVPTL”
        // 2018年11月29日 魏鹏：市价查询-基金不需要投资目的，方案也不需要投资目的了
//        if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)
//            || DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
//                schemeDetailDto.getInvAim())) {
            // 估值方法为成本法
            if (DictCodeListExt1.估值方法_成本法.getValue().equals(schemeDetailDto.getValuationMethod())) {
                // 变量.估值 = 0;
                marketPriceDto.setPrice(BigDecimal.ZERO);
            }
            // 市值法、市值成本孰低法
            else {
                // 获取行情查询日期
                 queryDate = getQuotationQueryDate(schemeDetailDto.getValuationDate(), happenDate);
                // 查询行情价格
                marketPriceDto = getQuotation(schemeDetailDto,queryDate,finprodMarketId,targetFinProduct.getType18());
                // 小数位处理
//                setQuotaDigit(schemeDetailDto.getQuotaDigit(), marketPriceDto);
                setQuotaDigit(schemeDetailDto.getQuotaDigit(), marketPriceDto, schemeDetailDto.getPriceTailMode());

            }
//        }
            //估值方案理论估值日期
            String isHoliday = calculationCommonService.getIsHoliday(queryDate,targetFinProduct.getCalendarId() );
            if("0".equals(isHoliday)){
                marketPriceDto.setSchemeValDate(queryDate);
            }else{
                marketPriceDto.setSchemeValDate(calculationCommonService.getCDate(queryDate,targetFinProduct.getCalendarId() ));
            }

        // add by weipeng  20210807：资产对应计算用估值行情入库存表落地 start
        // 估值方案代码
        marketPriceDto.setSchemeId(schemeDetailDto.getSchemeId());
        // 估值方案明细代码
        marketPriceDto.setSchemeDetailId(schemeDetailDto.getSchemeDetailId());
        // add by weipeng  20210807：资产对应计算用估值行情入库存表落地 end
        return marketPriceDto;
    }

  

    /**
     * 公用方法：标的类共通方法 - 万份收益-基金
     * 
     * @param finprodId 标的代码
     * @param calendarId 交易日历
     * @param cdate 计算日
     * @return 万分收益信息List
     * @see
     * @since 1.0
     */
    public List<FinProductValuation> getValFinProductValuationList(String finprodId,
                                                                   String calendarId, Date cdate,
                                                                   Date drawStartDate) {
        // 标的查询-查询类型List
        List<TargetSearchDto> queryCriteria = new ArrayList<>();
        // 创建查询类型dto
        TargetSearchDto dto = new TargetSearchDto();
        // 设置查询类型dto的查询类型（查询标的信息RST_FINPRODUCT）
        dto.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteria.add(dto);
        // 调用共通方法-【标的类共通方法设计】-标的信息查询SHEET
        Map<TargetSearchDto, Object> map = this.getTargetInfo(finprodId, queryCriteria);
        // 获取基本信息DTO C
        TargetFinProduct targetFinProduct = (TargetFinProduct)map.get(dto);
        // 变量.市场代码 = C.市场代码
        String finprodMarketId = targetFinProduct.getFinprodMarketId();

        // 定义 取值日期list
        List<Date> listDates = new ArrayList<Date>();
        // IF(计算类共通【是否节假日（参数.计算日,交易日历)】 = 0)
        String isHoliday = calculationCommonService.getIsHoliday(cdate, calendarId);
        // 一、T日是工作日
        if ("0".equals(isHoliday)) {
            // IF (计算类共通【是否节假日（参数.计算日 - 1,交易日历)】 = 1) --- T-1日 是节假日，
            if ("1".equals(
                calculationCommonService.getIsHoliday(DateUtil.addDay(cdate, -1), calendarId))) {
                // 变量.上个工作日 = 计算类共通【查询上一工作日（参数.计算日,交易日历）】
                Date lastWorkday = calculationCommonService.getCDate(cdate, calendarId);
                // IF (计算类共通【是否节假日（变量.上个工作日 - 1,交易日历)】 = 0)
                //MODIFY JINMAODONG 2019-10-09 BUG修复，应该判断的是上个工作日-1是否为节假日，如果是节假日还需要做处理（国庆的时候出现了问题）
//                if ("0".equals(calculationCommonService.getIsHoliday(lastWorkday, calendarId))) {
                if ("0".equals(calculationCommonService.getIsHoliday(DateUtil.addDay(lastWorkday, -1), calendarId))) {
                    // 取值日期list.add(变量.上个工作日 ) --- 取上个交易日万份收益
                    listDates.add(lastWorkday);
                }
                else {
                    // 变量.上2个工作日 = 计算类共通【查询上一工作日（变量.上个工作日,交易日历）】
                    Date last2Workday = calculationCommonService.getCDate(lastWorkday, calendarId);
                    // 变量.临时日期 = 变量.上2个工作日+1
                    Date temporaryDate = DateUtil.addDay(last2Workday, 1);
                    // 如果是节假日，从此节假日第一天到上个交易日
                    // WHILE(变量.临时日期 <= 变量.上个工作日)
                    while (DateUtil.compareDate(temporaryDate, lastWorkday) <= 0) {
                        // 取值日期list.add(变量.临时日期)
                        listDates.add(temporaryDate);
                        // 变量.临时日期 自增1天
                        temporaryDate = DateUtil.addDay(temporaryDate, 1);
                    }
                }
            }
            else {
                // IF (计算类共通【是否节假日（参数.计算日 - 2,交易日历)】 = 1)
                if ("1".equals(calculationCommonService.getIsHoliday(DateUtil.addDay(cdate, -2),
                    calendarId))) {
                    // 变量.上个工作日 = 计算类共通【查询上一工作日（参数.计算日 -2,交易日历）】
                    Date lastWorkday = calculationCommonService.getCDate(
                        DateUtil.addDay(cdate, -2), calendarId);
                    // 变量.临时日期 = 变量.上个工作日+1
                    Date temporaryDate = DateUtil.addDay(lastWorkday, 1);
                    // WHILE(变量.临时日期 <= 参数.计算日 - 1)
                    while (DateUtil.compareDate(temporaryDate, DateUtil.addDay(cdate, -1)) <= 0) {
                        // 取值日期list.add(变量.临时日期)
                        listDates.add(temporaryDate);
                        // 变量.临时日期 自增1天
                        temporaryDate = DateUtil.addDay(temporaryDate, 1);
                    }
                }
                else {
                    // 取值日期list.add(参数.计算日 - 1)
                    listDates.add(DateUtil.addDay(cdate, -1));
                }
            }
        }

        // if（计提开始日!= null && 取值日期list != null && 取值日期list.size > 0）
        if (drawStartDate != null && listDates != null && listDates.size() > 0) {
            Iterator<Date> it = listDates.iterator();
            while (it.hasNext()) {
                Date varDate = it.next();
                // if（日期 < 计提开始日）
                if (varDate.before(drawStartDate)) {
                    it.remove();
                }
            }
        }

        if (!CollectionUtils.isEmpty(listDates)) {
            return finProductCommonMapper.getValFinProductValuationList(finprodMarketId,
                listDates);
        }
        else {
            return null;
        }

    }

    

    /**
     * 影子定价查询
     *
     * @param happenDate 发生日
     * @param booksetId 账套代码
     * @param finprodId 金融产品代码
     * @return 市价净价、估值日
     * @see
     * @since 1.0
     */
    public MarketPriceDto getShadowPrice(Date happenDate, String booksetId, String finprodId) {
        // 变量.市价净价
        BigDecimal netprice = BigDecimal.ZERO;
        // 标的查询-查询类型List
        List<TargetSearchDto> queryCriteria = new ArrayList<>();
        // 创建查询类型dto
        TargetSearchDto dto = new TargetSearchDto();
        // 设置查询类型dto的查询类型（查询标的信息RST_FINPRODUCT）
        dto.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteria.add(dto);
        // 调用共通方法-【标的类共通方法设计】-标的信息查询SHEET
        Map<TargetSearchDto, Object> map = this.getTargetInfo(finprodId, queryCriteria);
        // 获取基本信息DTO C
        TargetFinProduct targetFinProduct = (TargetFinProduct)map.get(dto);
        // 变量.市场代码 = C.市场代码
        String finprodMarketId = targetFinProduct.getFinprodMarketId();
//        if (targetFinProduct.getTradeMarket().equals(DictCodeListExt1.估值交易市场_上交所.getValue())
//            || targetFinProduct.getTradeMarket().equals(DictCodeListExt1.估值交易市场_深交所.getValue())) {
//            // 变量.估值来源 = "中证登"
//            valueSource = DictCodeList.估值市价_中证登.getValue();
//        }
//        else {
//            // 变量.估值来源 = "中债登"
//            valueSource = DictCodeList.估值市价_中债登.getValue();
//        }
        String couponSpecies = targetFinProduct.getCouponSpecies();
//        MstBondValuationInfo info = finProductCommonMapper.getNetprice(finprodMarketId, happenDate,
//            valueSource, couponSpecies);
//        if (info == null || info.getNetprice() == null) {
//            return null;
//        }
//        netprice = info.getNetprice();
        // 影子定价方案查询
        MarketPriceDto marketPriceDto = new MarketPriceDto();
        List<SchemeDetailDto> listSchemeDetailDto = this.getSchemeDetail(happenDate, booksetId,
            DictCodeListExt1.证券品种_债券.getValue(), DictCodeListExt1.配置方案类型_影子定价方案.getValue(),
            targetFinProduct.getTradeMarket(), targetFinProduct.getType1(), null);
        if (CollectionUtils.isEmpty(listSchemeDetailDto) || listSchemeDetailDto.get(0) == null) {
            return marketPriceDto;
        }
        SchemeDetailDto schemeDetailDto = listSchemeDetailDto.get(0);

        // 获取行情查询日期
        Date queryDate = getQuotationQueryDate(schemeDetailDto.getValuationDate(), happenDate);
        // 查询行情价格
        marketPriceDto = getQuotation(schemeDetailDto,queryDate,finprodMarketId,targetFinProduct.getType18());
//        // 小数位处理
//        setQuotaDigit(schemeDetailDto.getQuotaDigit(), marketPriceDto);

        // if 变量.息票品种 IN（“零息”，“贴现”）
        if (marketPriceDto.getPrice() != null && marketPriceDto.getValDate() != null
            && (DictCodeList.息票品种_零息.getValue().equals(couponSpecies)
                || DictCodeList.息票品种_贴现.getValue().equals(couponSpecies))) {
            // 2018年12月14日 增加开关控制
            // 需求：零息、贴现债的【市价】计算方式改为取中债估值，【交易性】时取中债净价，【非交易性】时取中债全价；计算影子价格的市价取中债全价。
            // 注意：若是查市场行情时，开关需要设为01
            // modify by wj 20200620 二级法人改造 五个特殊参数 修改 为特殊取值方法
            String institutionCode = MultilevelDataAuthorityUtil.getOrgCodeByBussinessIDType(booksetId, com.joyintech.framework.constant.DictCodeList.联社模式查询数据权限_修改和删除.getValue(), DictCodeList.共通业务类型_账套管理.getValue());
            if(StringUtil.equals( CacheUtil.getSpecialSysParam("PRICE_CAL_TYPE",institutionCode), GlobalConstant.TYPE_02)){
                // 直接取中债全价
                netprice = marketPriceDto.getAllPrice();
            }
            else{
                // 变量.市价净价 = 上述返回.估值
                // +（变量.估值日-变量.标的信息.起息日+1）/（C.实际到期日-C.起息日）*（100-C.发行价）
                // （变量.估值日-变量.标的信息.起息日+1）
                long day1 = DateUtil.getBetweenDayNumber(targetFinProduct.getVdate(),
                    marketPriceDto.getValDate()) + 1;
                // （C.实际到期日-C.起息日）
                long day2 = DateUtil.getBetweenDayNumber(targetFinProduct.getVdate(),
                    targetFinProduct.getActmdate());
                BigDecimal day = ArithmeticUtil.divide(new BigDecimal(day1), new BigDecimal(day2));
                BigDecimal priceAdd = ArithmeticUtil.multiply(day,
                    ArithmeticUtil.subtract(new BigDecimal(100), targetFinProduct.getIssuePrice()));
//            netprice = ArithmeticUtil.round(ArithmeticUtil.doAdd(marketPriceDto.getPrice(), priceAdd),
//                NumberEnum.MONEY);
                netprice = ArithmeticUtil.doAdd(marketPriceDto.getPrice(), priceAdd);
            }
            // 市价Dto.市价净价 = 变量.市价净价
            marketPriceDto.setPrice(netprice);
        }

        /**add by zhangquan 20200109 行情折算处理 end**/
        List<Date> queryDateList = new ArrayList<>();
        Map<Date,Double> resultMap = new HashMap<>();
        List<String> codeList = new ArrayList<>();
        //获取账套参数 债券市价折算方式
        codeList.add(ValDictCode.债券市价折算方式.getValue());
        Map<String, Object> bokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(booksetId, codeList);
        //债券市价折算方式
        String conversionMode = (String) bokBooksetExtMap.get(ValDictCode.债券市价折算方式.getValue());
        BigDecimal conversionNetPrice = BigDecimal.ZERO;
        if(marketPriceDto.getValDate()!=null) {
        	//变量.日期列表 = {出参.估值日期，入参.发生日}
        	queryDateList.add(marketPriceDto.getValDate());
        	queryDateList.add(happenDate);
        	//调用共通->标的类共通.多日期百元券面查询
        	resultMap = MultiDateHundredSecFaceQuery(queryDateList, finprodId, null);

        	if(resultMap!=null && resultMap.get(happenDate)!=null && resultMap.get(marketPriceDto.getValDate())!=null) {
        		////调用共通->标的类共通.市价折算处理
        		conversionNetPrice = ConversionMarketPrice(resultMap.get(happenDate), resultMap.get(marketPriceDto.getValDate()), conversionMode, marketPriceDto.getPrice());
        		marketPriceDto.setPrice(conversionNetPrice);
        	}
        }

        /** add by yanya on 20200117 影子定价查询 价格等于"全价"处理 stat**/
        //if(变量.价格类型 = “全价“)
        if(DictCodeListExt3.价格类别_全价.getValue().equals(schemeDetailDto.getPriceClass())
            && marketPriceDto.getPrice() != null && marketPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal 本日应计利息余额 = this.getBondFullPriceInterestBalance(finprodId, happenDate);
            //出参.市价净价 - 变量.本日应计利息余额
            marketPriceDto.setPrice(ArithmeticUtil.subtract(marketPriceDto.getPrice(), 本日应计利息余额));
        }
        /** add by yanya on 20200117 影子定价查询 价格等于"全价"处理 end**/

        // add by weipeng  20210807：资产对应计算用估值行情入库存表落地 start
        // 估值方案代码
        marketPriceDto.setSchemeId(schemeDetailDto.getSchemeId());
        // 估值方案明细代码
        marketPriceDto.setSchemeDetailId(schemeDetailDto.getSchemeDetailId());
        // add by weipeng  20210807：资产对应计算用估值行情入库存表落地 end

        // 小数位处理
        setQuotaDigit(schemeDetailDto.getQuotaDigit(), marketPriceDto, schemeDetailDto.getPriceTailMode());
        return marketPriceDto;
    }
    
    
    /**
     *多日期百元券面查询 
     * add by zhangquan 20200109
     * @param queryDateList 日期列表
     * @param finprodId 标的代码
     * @param repayPlan 还本计划列表
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
	public Map<Date,Double> MultiDateHundredSecFaceQuery(List<Date> queryDateList, String finprodId, List<TargetCashPlan> repayPlan){
    	//定义：出参.百元券面结果集
    	Map<Date,Double> resultMap = new HashMap<>();
    	//1.还本计划处理
    	TargetSearchDto searchRedinfo = new TargetSearchDto();
        List<TargetSearchDto> queryCriteria = new ArrayList<>();
        List<TargetCashPlan> listRedinfoList = new ArrayList<>();
    	if(StringUtil.isEmptyList(repayPlan)) {
            searchRedinfo.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
            searchRedinfo.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
            searchRedinfo.setFinprodId(finprodId);
            queryCriteria.add(searchRedinfo);
            
            Map<TargetSearchDto, Object> targetSearchMap = finProductCommonService.getTargetInfo(
            		finprodId, queryCriteria);
             // 变量.还本列表 = 上述共通返回还本列表
            listRedinfoList = targetSearchMap.get(searchRedinfo) == null ? null:(List<TargetCashPlan>)targetSearchMap.get(searchRedinfo);
            
    	}else {
    		listRedinfoList = repayPlan;
    	}
    	//2 计算百元券面
    	//定义变量：百元券面 = 100;
    	double hundredFaceSec = 100;
    	queryDateList.forEach(queryDate->{
    		resultMap.put(queryDate, hundredFaceSec);
    	});
    	//定义 数量 = 0
    	//double amount = 0;
    	//变量.剩余券面
    	double surplusSec = 0;
    	if (listRedinfoList != null){
            for(TargetCashPlan listRedinfo : listRedinfoList) {
                for(Date queryDate : queryDateList) {
                    if(listRedinfo.getMdate().compareTo(queryDate)==-1 || listRedinfo.getMdate().compareTo(queryDate)==0) {
                        surplusSec = resultMap.get(queryDate)-listRedinfo.getPrinAmt().doubleValue();
                        resultMap.put(queryDate, surplusSec);
                    }
                }
            }
        }
    	//返回 出参.百元券面结果集
		return resultMap;
    	
    }
    
    
    /**
     *市价折算处理
     * add by zhangquan 20200109
     * @param faceQueryValuation 估值日债券面值
     * @param faceQueryQuotation 行情日债券面值
     * @param conversionMode 市价折算方式
     * @param netprice 市价净值
     * @return 折后市价
     * @see
     * @since 1.0
     */
    public BigDecimal ConversionMarketPrice(double faceQueryValuation, double faceQueryQuotation, String conversionMode, BigDecimal netprice) {
    	//估值日债券面值 = 行情日债券面值
    	if(faceQueryValuation==faceQueryQuotation) {
    		return netprice;
    	}else {
    		//市价折算方式 ="按占比计算"
    		if(ValDictCodeList.债券市价折算方式_按占比计算.getValue().equals(conversionMode)) {
    			//市价*估值日债券面值/行情日债券面值
                // update by caiqun TISHKBS-1659 除法的时候  小数位没有精确所以报错 start
                //return netprice.multiply(BigDecimal.valueOf(faceQueryValuation)).divide(BigDecimal.valueOf(faceQueryQuotation));
                return ArithmeticUtil.divide(netprice.multiply(BigDecimal.valueOf(faceQueryValuation)), BigDecimal.valueOf(faceQueryQuotation), NumberEnum.HIGH_PREC_NUM.getCode());
                // update by caiqun TISHKBS-1659 除法的时候  小数位没有精确所以报错 end
    		}else if(ValDictCodeList.债券市价折算方式_按金额计算.getValue().equals(conversionMode)){
    			//市价-(行情日债券面值 - 估值日债券面值)
    			return netprice.subtract(BigDecimal.valueOf(faceQueryQuotation)).add(BigDecimal.valueOf(faceQueryValuation));
    		}
    	}
		return netprice;
    }
    
    /**
     * 万分收益-计划类货币型
     * 
     * @param finprodMarketId 市场代码
     * @param calenderId 交易日历
     * @param cDate 计算日,必输项
     * @param booksetId 账套代码
     * @return
     * @see
     * @since 1.0
     */
    public ValFinProductValuationDto queryFinProductValuation(String finprodMarketId,String calenderId,Date cDate,String booksetId){
        //变量.取值日期 = 计算日
        Date valDate = cDate;
//        if(tflag == 1){
//            //变量.取值日期 = 计算类共通【查询上一工作日（参数.计算日,交易日历）】
//            valDate = calculationCommonService.getCDate(cDate, calenderId);
//        }
     // 调共通方法-【详细设计-标的类共通方法设计】-估值方案查询SHEET
        List<SchemeDetailDto> listSchemeDetailDto = this.getSchemeDetail(cDate, booksetId,
            DictCodeListExt1.证券品种_计划类资产_货币型.getValue(), DictCodeListExt1.配置方案类型_估值方案.getValue(),
            null, null, null);
        SchemeDetailDto schemeDetailDto = new SchemeDetailDto();
        if (!CollectionUtils.isEmpty(listSchemeDetailDto)) {
            schemeDetailDto = listSchemeDetailDto.get(0);
        }
        // if(估值方案.估值方法=市值法)
        if(DictCodeListExt1.估值日期_行情日减1.getValue().equals(
            schemeDetailDto.getValuationDate())){
            valDate = DateUtil.addDay(cDate, -1);
        }
        if(DictCodeListExt1.估值日期_行情日.getValue().equals(
            schemeDetailDto.getValuationDate())){
            valDate = cDate;
        }
        if(DictCodeListExt1.估值日期_行情日加1.getValue().equals(
            schemeDetailDto.getValuationDate())){
            valDate = DateUtil.addDay(cDate, 1);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("finprodId", finprodMarketId);
        map.put("valDate", valDate);
        
        //modify by zhangquan 20191219 计划类（货币）-每日红利计提-取行情价格时的位数处理与估值方案里设置的位数不一致。
        ValFinProductValuationDto valFinProductValuationDto = finProductCommonMapper.queryFinProductValuation(map);
        if(valFinProductValuationDto != null) {
        	// 按照行情位数调用金额共通方法设置有效小数位（高精度）
        	BigDecimal QuotaDigit = schemeDetailDto.getQuotaDigit();
        	String PriceTailMode = schemeDetailDto.getPriceTailMode();
        	if (QuotaDigit != null) {
        		RoundingMode roundingMode = null;
        		if(DictCodeList.估值舍入规则_四舍五入.getValue().equals(PriceTailMode)){
        			roundingMode = RoundingMode.HALF_UP;
        		}
        		else if(DictCodeList.估值舍入规则_舍位.getValue().equals(PriceTailMode)){
        			roundingMode = RoundingMode.DOWN;
        		}
        		else if(DictCodeList.估值舍入规则_进位.getValue().equals(PriceTailMode)){
        			roundingMode = RoundingMode.UP;
        		}
        		valFinProductValuationDto.setValuation(ArithmeticUtil.round(valFinProductValuationDto.getValuation(),
        				QuotaDigit.intValue(), roundingMode));
        	}
            // add by weipeng  20210807：资产对应计算用估值行情入库存表落地 start
            // 估值方案代码
            valFinProductValuationDto.setSchemeId(schemeDetailDto.getSchemeId());
            // 估值方案明细代码
            valFinProductValuationDto.setSchemeDetailId(schemeDetailDto.getSchemeDetailId());
            // add by weipeng  20210807：资产对应计算用估值行情入库存表落地 end
        }
        return valFinProductValuationDto;
    }
    
    /**
     * 标的类共通方法设计 - 汇率查询
     *
     * @param bookDate 日期
     * @param bookSetId 账套代码
     * @param basicCny 基础币种
     * @param offreCny 报价币种
     * @return
     * @see
     * @since 1.0
     */
    public List<CurrencyMarketDto> getCurrencyMarket(Date bookDate, String bookSetId, String basicCny,
            String offreCny) {
        return finProductCommonBatchService.getCurrencyMarket(bookDate, bookSetId, basicCny,
                offreCny);
        }

    /**
     * 通道信息查询
     * @param finprodId
     * @return
     */
    public FinChnlInfoDto getChnlInfo(String finprodId) {
        FinChnlInfoDto dto = new FinChnlInfoDto();
        List<FinChnlInfoDto> list = finProductCommonMapper.getChnlInfo(finprodId);
        if(!StringUtil.isEmptyList(list)) {
            dto = list.get(0);
        }
        return dto;
    }
    /**
     * 查询跑批日当天还本付息交易
     * @param map
     * @return
     */
    public Map<String, Object> selectHBFXData(Map<String, Object> map) {
        return finProductCommonMapper.selectHBFXData(map);
    }

    /**
     * 市价查询-计划类利率型
     *
     * add by wujie on 20201015  remark:计划类收益型资产改造
     * @param happenDate 发生日
     * @param booksetId 账套代码
     * @param finProdId 产品代码
     * @param invAim 投资目的
     * @return 市价净值、估值日期
     */
    public MarketPriceDto gettDeltRightMarketPrice(Date happenDate, String booksetId, String finProdId,String invAim) {
        return getBondMarketPrice(happenDate,booksetId,finProdId,invAim);
//        // 1.定义变量
//        // 定义 变量.市价Dto
//        MarketPriceDto marketPriceDto = new MarketPriceDto();
//
//        marketPriceDto.setPrice(BigDecimal.ZERO);
//
//        //行情查询日期
//        Date queryDate=null;
//
//        List<TargetSearchDto> queryCriteria = new ArrayList<>();
//        TargetSearchDto targetSearchDto = new TargetSearchDto();
//        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
//        queryCriteria.add(targetSearchDto);
//        Map<TargetSearchDto, Object> targetSearchMap = this.getTargetInfo(finProdId,
//                queryCriteria);
//        TargetFinProduct targetFinProduct = (TargetFinProduct)targetSearchMap.get(targetSearchDto);
//        // 判断是否为空
//        if (targetFinProduct == null) {
//            return marketPriceDto;
//        }
//        // 赋值市场代码
//        String finprodMarketId = targetFinProduct.getFinprodMarketId();
//
//        // 查看数据库配置表 - 特殊资产配置开关是否。只有开关打开才执行特殊资产逻辑
//        String specialAssetConfigSwitch = CacheUtil.getSysParam("SPECIAL_ASSET_CONFIG_SWITCH");
//        if ("ON".equals(specialAssetConfigSwitch)) {
//            // 调共通方法-【详细设计-标的类共通方法设计】-估值方案特殊资产配置查询SHEET
//            List<SchemeDetailDto> specialList = this.getSpecialSchemeDetail(happenDate, booksetId,
//                    DictCodeListExt1.配置方案类型_估值方案.getValue(), finProdId);
//            SchemeDetailDto specialDto = null;
//            if (specialList != null && !specialList.isEmpty()) {
//                for (SchemeDetailDto item : specialList) {
//                    if (item.getInvAim() == null || item.getInvAim().equals(invAim)) {
//                        specialDto = item;
//                        break;
//                    }
//                }
//            }
//
//            // 特殊资产方案有值的情况
//            if (specialDto != null) {
//
//                if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(specialDto.getInvAim())) {
//                    // 变量.市价净价 = 0;
//                    marketPriceDto.setPrice(BigDecimal.ZERO);
//                }
//                else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)
//                        || DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)) {
//                    // 估值方法为成本法
//                    if (DictCodeListExt1.估值方法_成本法.getValue().equals(specialDto.getValuationMethod())
//                            || DictCodeListExt1.估值方法_摊余成本法.getValue().equals(specialDto.getValuationMethod())) {
//                        marketPriceDto.setPrice(BigDecimal.ZERO);
//                    }
//                    // 市值法、市值成本孰低法
//                    else {
//                        // 获取行情查询日期
//                        queryDate = getQuotationQueryDate(specialDto.getValuationDate(), happenDate);
//                        // 查询行情价格
//                        marketPriceDto = getQuotation(specialDto,queryDate,finprodMarketId,
//                                DictCodeListExt1.金融产品类型_计划类资产.getValue());
//                        // 小数位处理
//                        setQuotaDigit(specialDto.getQuotaDigit(), marketPriceDto, specialDto.getPriceTailMode());
//                    }
//                }
//                if(queryDate!=null){
//                    //理论估值日期
//                    String isHoliday = calculationCommonService.getIsHoliday(queryDate,targetFinProduct.getCalendarId());
//                    if("0".equals(isHoliday)){
//                        marketPriceDto.setSchemeValDate(queryDate);
//                    }else{
//                        marketPriceDto.setSchemeValDate(calculationCommonService.getCDate(queryDate,targetFinProduct.getCalendarId()));
//                    }
//                }
//                // add by weipeng  20210807：资产对应计算用估值行情入库存表落地 start
//                // 估值方案代码
//                marketPriceDto.setSchemeId(specialDto.getSchemeId());
//                // 估值方案明细代码
//                marketPriceDto.setSchemeDetailId(specialDto.getSchemeDetailId());
//                // add by weipeng  20210807：资产对应计算用估值行情入库存表落地 end
//                return marketPriceDto;
//            }
//        }
//
//        // 2.调用共通查询估值方案
//        Map<String, Object> params = new HashMap<>();
//        params.put("happenDate", happenDate);
//        params.put("booksetId", booksetId);
//        List<SchemeDetailDto> listSchemeDetailDto = this.getSchemeDetail(happenDate, booksetId,
//                DictCodeListExt1.证券品种_计划类资产_收益型.getValue(), DictCodeListExt1.配置方案类型_估值方案.getValue(),
//                targetFinProduct.getTradeMarket(), targetFinProduct.getType1(), invAim);
//        if (CollectionUtils.isEmpty(listSchemeDetailDto) || listSchemeDetailDto.get(0) == null) {
//            return marketPriceDto;
//        }
//        SchemeDetailDto schemeDetailDto = listSchemeDetailDto.get(0);
//
//        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(invAim)) {
//            marketPriceDto.setPrice(BigDecimal.ZERO);
//        }
//        else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)
//                || DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)) {
//            // 估值方法为成本法
//            if (DictCodeListExt1.估值方法_成本法.getValue().equals(schemeDetailDto.getValuationMethod())
//                    || DictCodeListExt1.估值方法_摊余成本法.getValue().equals(schemeDetailDto.getValuationMethod())) {
//                marketPriceDto.setPrice(BigDecimal.ZERO);
//            }
//            // 市值法、市值成本孰低法
//            else {
//                // 获取行情查询日期
//                queryDate = getQuotationQueryDate(schemeDetailDto.getValuationDate(), happenDate);
//                // 查询行情价格
//                marketPriceDto = getQuotation(schemeDetailDto,queryDate,finprodMarketId,
//                        DictCodeListExt1.金融产品类型_计划类资产.getValue());
//                setQuotaDigit(schemeDetailDto.getQuotaDigit(), marketPriceDto,schemeDetailDto.getPriceTailMode());
//            }
//        }
//
//        if(queryDate!=null){
//            //理论估值日期
//            String isHoliday = calculationCommonService.getIsHoliday(queryDate,targetFinProduct.getCalendarId());
//
//            if("0".equals(isHoliday)){
//                marketPriceDto.setSchemeValDate(queryDate);
//            }else{
//                marketPriceDto.setSchemeValDate(calculationCommonService.getCDate(queryDate,targetFinProduct.getCalendarId()));
//            }
//        }
//
//        return marketPriceDto;
    }

    /*
     * 实物查询交易信息
     * @param tradeId
     * @return
     */
    public TrdFinProductBarterDto selectTrdFinProductBarter(Map<String, Object> map) {
        return finProductCommonMapper.selectTrdFinProductBarter(map);
    }

    /**
     * 共通计算债券行权后的公允价值
     * @param booksetId 账套代码
     * @param finprodId 金融产品代码
     * @param invAim 投资母
     * @param marketPrice 市价
     * @param calculationNum 数量
     * @param happenDate 发生日
     * @return
     */
    public BigDecimal calculationSecFairValue(String booksetId, String finprodId, String invAim, BigDecimal marketPrice,
                                              BigDecimal calculationNum, Date happenDate) {

        //1.定义变量
        //定义 变量.公允价值变动是否启用行权价
        String 公允价值变动是否启用行权价 = "";
        //定义 变量.行权回售申请交易处理
        String 行权回售申请交易处理 = "";
        //定义 变量.公允价值变动
        BigDecimal 公允价值变动 = BigDecimal.ZERO;
        //定义 变量.行权申请信息DTO
        //2.处理详情
        //2.1 查询账套参数配置信息
        // 账套核算参数查询
        List<String> paramCodeList = new ArrayList<String>();
        // DL_ACCR_TYPE 账套配置表参数项_拆借计提方式
        paramCodeList.add(ValDictCodeList.账套配置表参数项_公允价值变动是否启用行权价.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_行权回售申请交易处理.getValue());
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(booksetId, paramCodeList);
        //调用账务类共通（DTO.账套代码, 参数项：如下）
        //EXEC_PRICE_CAL_TYPE	公允价值变动是否启用行权价
        if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_公允价值变动是否启用行权价.getValue()) != null){
            公允价值变动是否启用行权价 = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_公允价值变动是否启用行权价.getValue());
        }
        //变量.公允价值变动是否启用行权价 = 返回值
        if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_行权回售申请交易处理.getValue()) != null){
            行权回售申请交易处理 = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_行权回售申请交易处理.getValue());
        }
        //IF 变量.公允价值变动是否启用行权价 IS  NULL AND 数据字典：是否.否 eq 变量.公允价值变动是否启用行权价
        if(StringUtil.isEmpty(公允价值变动是否启用行权价) || StringUtil.equals(DictCodeList.是否_否.getValue(), 公允价值变动是否启用行权价)) {
            //变量.公允价值变动 =	输入.市价 * 输入.计算成本数量
            公允价值变动 = ArithmeticUtil.multiply(marketPrice, calculationNum);
            return 公允价值变动;
        }
        //调用共通设计->【详细设计-交易信息查询共通方法设计.xlsx】->债券行权回售申请查询SHEET
        ValExecApplyInfoDto valExecApplyInfoDto = valTradeCommonService.secExecApplyQuery(booksetId, finprodId, invAim,
                happenDate, 行权回售申请交易处理);
        if(valExecApplyInfoDto == null) {
            //变量.公允价值变动 =	输入.市价 * 输入.计算成本数量
            公允价值变动 = ArithmeticUtil.multiply(marketPrice, calculationNum);
            return 公允价值变动;
        }
        //IF 输入.计算成本数量 <= 变量.行权申请信息DTO.行权份额
        if(calculationNum.compareTo(valExecApplyInfoDto.getExecNum()) <= 0) {
            //变量.公允价值变动 =		变量.行权申请信息DTO.行权份额 * 变量.行权申请信息DTO.行权价格
            公允价值变动 = ArithmeticUtil.multiply(valExecApplyInfoDto.getExecPrice(), valExecApplyInfoDto.getExecNum());
        }
        else {
            //变量.公允价值变动 =变量.行权申请信息DTO.行权份额 * 变量.行权申请信息DTO.行权价格 + 输入.市价*(输入.计算成本数量-变量.行权申请信息DTO.行权份额)
            公允价值变动 = ArithmeticUtil.doAdd(
                    ArithmeticUtil.multiply(valExecApplyInfoDto.getExecPrice(), valExecApplyInfoDto.getExecNum()),
                    ArithmeticUtil.multiply(marketPrice,
                            ArithmeticUtil.doSub(calculationNum, valExecApplyInfoDto.getExecNum()))
            );
        }
        return 公允价值变动;
    }

    /**
     * 行权交易差额
     * add by yanya on 20210310
     * @param booksetId 账套代码
     * @param bookDate 入账日
     * @param bokDetailId 账务明细代码
     * @param rstBaseinfo 标的信息
     * @param dealNow 行权确认交易
     * @param batchExedetailSeqNo 批处理编号
     * @return
     */
    public ValExecFeeDiffDto calculationFeeDetailDiff(String booksetId, Date bookDate, String bokDetailId, TargetFinProduct rstBaseinfo,
                                                      FinProductDealInfoComDTO dealNow) {
        //1.定义变量
        //定义 变量.行权交易差额信息
        ValExecFeeDiffDto result = new ValExecFeeDiffDto();
        //定义 变量.交易各种费用_差额
        //印花税差额
        BigDecimal 印花税差额 = BigDecimal.ZERO;
        //经手费差额
        BigDecimal 经手费差额 = BigDecimal.ZERO;
        //证管费差额
        BigDecimal 证管费差额 = BigDecimal.ZERO;
        //结算费差额
        BigDecimal 结算费差额 = BigDecimal.ZERO;
        //佣金差额
        BigDecimal 佣金差额 = BigDecimal.ZERO;
        //交易手续费差额
        BigDecimal 交易手续费差额 = BigDecimal.ZERO;
        //结算手续费差额
        BigDecimal 结算手续费差额 = BigDecimal.ZERO;
        //通道费差额
        BigDecimal 通道费差额 = BigDecimal.ZERO;
        //交易费用差额
        BigDecimal 交易费用差额 = BigDecimal.ZERO;

        //印花税随交易支付差额
        BigDecimal 印花税随交易支付差额 = BigDecimal.ZERO;
        //经手费随交易支付差额
        BigDecimal 经手费随交易支付差额 = BigDecimal.ZERO;
        //证管费随交易支付差额
        BigDecimal 证管费随交易支付差额 = BigDecimal.ZERO;
        //结算费随交易支付差额
        BigDecimal 结算费随交易支付差额 = BigDecimal.ZERO;
        //佣金随交易支付差额
        BigDecimal 佣金随交易支付差额 = BigDecimal.ZERO;
        //交易手续费随交易支付差额
        BigDecimal 交易手续费随交易支付差额 = BigDecimal.ZERO;
        //结算手续费随交易支付差额
        BigDecimal 结算手续费随交易支付差额 = BigDecimal.ZERO;
        //交易费用随交易支付差额
        BigDecimal 交易费用不随交易支付差额 = BigDecimal.ZERO;
        //定义 变量.利息差额
        //利息金额差额
        BigDecimal 利息金额差额 = BigDecimal.ZERO;
        //定义 变量.清算款差额
        //清算款差额
        BigDecimal 清算款差额 = BigDecimal.ZERO;
        //定义 变量.应计利息 = 0
        BigDecimal 应计利息 = BigDecimal.ZERO;
        //定义 变量.交易金额_清算款 =0
        BigDecimal 交易金额清算款 = BigDecimal.ZERO;
        //定义 变量.交易份额 = 0
        BigDecimal 交易份额 = BigDecimal.ZERO;
        //定义 变量.交易本金 = 0
        BigDecimal 交易本金 = BigDecimal.ZERO;
        //2.处理详情
        //2.1 数据初始化
        Map<String, String> map = this.getPsAndTableIdByExecDeal(dealNow.getPs(), dealNow.getInvAim());
        //定义 变量.交易方向
        String 交易方向 = map.get("ps");
        //定义 变量.会计事件代码
        String 会计事件代码 = map.get("tableId");
        //调用账务共通设计->【凭证最新账务查询】
        List<BookLatestInfoDto> listSubjectBalanceInt = this.quaryExecSubjectBalance(booksetId, bookDate, bokDetailId,
                会计事件代码, dealNow.getInvAim());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listSubjectBalanceInt)) {
            String 成本基础科目性质 = this.getExecCbBaseSubjectNatrue(dealNow.getInvAim());
            String 应计利息基础科目性质 = this.getExecYjlxBaseSubjectNatrue(dealNow.getInvAim());
            String 清算款基础科目性质 = DictCodeBaseSubjectNatrue.清算款.getValue();
            for(BookLatestInfoDto item : listSubjectBalanceInt) {
                //if(变量.账务明细.基础科目性质 = XX应计利息的账务明细 )
                if(StringUtil.equals(应计利息基础科目性质, item.getBaseSubjectNature())) {
                    ///变量.账务明细.借贷方向为借时
                    if(StringUtil.equals(item.getCdFlag(), DictCodeList.借贷方向_借.getValue())) {
                        //变量.应计利息 -= 变量.账务明细.原币发生额
                        应计利息 = ArithmeticUtil.doSub(应计利息, item.getoHappenAmt());
                    }
                    //变量.账务明细.借贷方向为贷时
                    else if(StringUtil.equals(item.getCdFlag(), DictCodeList.借贷方向_贷.getValue())) {
                        //变量.应计利息 +=	变量.账务明细.原币发生额
                        应计利息 = ArithmeticUtil.doAdd(应计利息, item.getoHappenAmt());
                    }
                }
                // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                else if(StringUtil.equals(成本基础科目性质, item.getBaseSubjectNature())) {
                    //变量.交易份额 += 变量.账务明细.发生数量
                    交易份额 = ArithmeticUtil.doAdd(交易份额, item.getHappenNumber());
                    //变量.交易本金 += 变量.账务明细.原币发生额
                    交易本金 = ArithmeticUtil.doAdd(交易本金, item.getoHappenAmt());
                }
                //if(变量.账务明细.基础科目性质 = 清算款的账务明细 )
                else if(StringUtil.equals(清算款基础科目性质, item.getBaseSubjectNature())) {
                    //变量.交易金额_清算款 += 变量.账务明细.原币发生额
                    交易金额清算款 = ArithmeticUtil.doAdd(交易金额清算款, item.getoHappenAmt());
                }

            };
        }
        //2.2 费用差额
        //定义 变量.确认交易各种费用
        //调用 共通【费用处理共通】  参考本设计书sheet【费用处理共通】
        DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                dealNow.getValTradeFeeDetailList(), dealNow.getProdId(), dealNow.getPortfolioId());
        //IF 数据字典:交易方向.行权-回售 eq 入参.行权确认交易DTO.交易方向
        if(StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), dealNow.getPs())) {
            List<TrdTradeFeeDetail> allList = new ArrayList<>();
            //定义查询参数
            Map<String, Object> 入账查询参数 = new HashMap<>();
            //账套代码					交易类账务处理结果.账套代码
            入账查询参数.put("booksetId", booksetId);
            //金融产品代码					交易明细.金融产品代码
            入账查询参数.put("finprodId", dealNow.getFinprodId());
            //金融产品类型					交易明细.金融产品类型
            入账查询参数.put("finprodType", dealNow.getFinprodType());
            //投资目的					交易明细.投资目的
            入账查询参数.put("invAim", dealNow.getInvAim());
            //业务类型					数据字典：业务类型.债券交易
            入账查询参数.put("busiType", DictCodeList.业务类型_债券交易预处理.getValue());
            //交易方向					数据字典：交易方向.收息到账
            入账查询参数.put("ps", 交易方向);
            //分支序号					交易明细.分支序号
            入账查询参数.put("branch", dealNow.getBranch());
            //入账日_结束日					交易类账务处理结果.入账日 - 1
            入账查询参数.put("bookDate", dealNow.getMdate());
            //调用【交易信息查询共通方法设计-入账交易信息查询】
            List<ValTrdProductDealInfoDto>  dealList = valTradeCommonService.findEntryTradeInfo(入账查询参数);
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(dealList)) {
                //LOOP 变量.交易信息List  交易信息ENTITY
                for(ValTrdProductDealInfoDto item : dealList) {
                    //调用【交易信息查询共通方法设计-交易费用明细查询】
                    List<TrdTradeFeeDetail> fee = valTradeCommonService.findTrdTradeFeeDetailInfo(item.getTradeId());
                    if (org.apache.commons.collections.CollectionUtils.isNotEmpty(fee)) {
                        allList.addAll(fee);
                    }
                }
            }
            //调用 共通【费用处理共通】  参考本设计书sheet【费用处理共通】
            DealTypesFeeDto applyFeeDto = calculationCommonService.quaryDealTypesFeeamt(allList, dealNow.getProdId(), dealNow.getPortfolioId());
            //变量.交易各种费用_差额.印花税	=		变量.确认交易各种费用.印花税	-		变量.交易各种费用.印花税
            印花税差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getStampDuty(), applyFeeDto.getStampDuty());
            //变量.交易各种费用_差额.经手费	=		变量.确认交易各种费用.经手费	-		变量.交易各种费用.经手费
            经手费差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getBrokerage(), applyFeeDto.getBrokerage());
            //变量.交易各种费用_差额.证管费	=		变量.确认交易各种费用.证管费	-		变量.交易各种费用.证管费
            证管费差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getCertificateFee(), applyFeeDto.getCertificateFee());
            //变量.交易各种费用_差额.结算费	=		变量.确认交易各种费用.结算费	-		变量.交易各种费用.结算费
            结算费差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getClearingFee(), applyFeeDto.getClearingFee());
            //变量.交易各种费用_差额.佣金	=		变量.确认交易各种费用.佣金		-		变量.交易各种费用.佣金
            佣金差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getCommission(), applyFeeDto.getCommission());
            //变量.交易各种费用_差额.交易手续费	=		变量.确认交易各种费用.交易手续费	-		变量.交易各种费用.交易手续费
            交易手续费差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getTransactionFee(), applyFeeDto.getTransactionFee());
            //变量.交易各种费用_差额.结算手续费	=		变量.确认交易各种费用.结算手续费	-		变量.交易各种费用.结算手续费
            结算手续费差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getSettlementFee(), applyFeeDto.getSettlementFee());
            //变量.交易各种费用_差额.交易费用	=		变量.确认交易各种费用.交易费用		-		变量.交易各种费用.交易费用
            交易费用差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getFeeAmt(), applyFeeDto.getFeeAmt());
            //变量.交易各种费用_差额.通道费		=		变量.确认交易各种费用.通道费		-		变量.交易各种费用.通道费
            通道费差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getChlFee(), applyFeeDto.getChlFee());
            //变量.交易各种费用_差额.随交易支付_印花税	=	变量.确认交易各种费用.随交易支付_印花税	-	变量.交易各种费用.随交易支付_印花税
            印花税随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getStampDutyForDealPay(), applyFeeDto.getStampDutyForDealPay());
            //变量.交易各种费用_差额.随交易支付_经手费	=	变量.确认交易各种费用.随交易支付_经手费	-	变量.交易各种费用.随交易支付_经手费
            经手费随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getBrokerageForDealPay(), applyFeeDto.getBrokerageForDealPay());
            //变量.交易各种费用_差额.随交易支付_证管费	=	变量.确认交易各种费用.随交易支付_证管费	-	变量.交易各种费用.随交易支付_证管费
            证管费随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getCertificateFeeForDealPay(), applyFeeDto.getCertificateFeeForDealPay());
            //变量.交易各种费用_差额.随交易支付_结算费	=	变量.确认交易各种费用.随交易支付_结算费	-	变量.交易各种费用.随交易支付_结算费
            结算费随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getClearingFeeForDealPay(), applyFeeDto.getClearingFeeForDealPay());
            //变量.交易各种费用_差额.随交易支付_佣金		=	变量.确认交易各种费用.随交易支付_佣金  -	变量.交易各种费用.随交易支付_佣金
            佣金随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getCommissionForDealPay(), applyFeeDto.getCommissionForDealPay());
            //变量.交易各种费用_差额.随交易支付_交易手续费	=	变量.确认交易各种费用.随交易支付_交易手续费	-	变量.交易各种费用.随交易支付_交易手续费
            交易手续费随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getTransactionFeeForDealPay(), applyFeeDto.getTransactionFeeForDealPay());
            //变量.交易各种费用_差额.随交易支付_结算手续费	=	变量.确认交易各种费用.随交易支付_结算手续费	-	变量.交易各种费用.随交易支付_结算手续费
            结算手续费随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getSettlementFeeForDealPay(), applyFeeDto.getSettlementFeeForDealPay());
            //变量.交易各种费用_差额.不随交易费用	=		变量.确认交易各种费用.不随交易费用	-		变量.交易各种费用.不随交易费用
            交易费用不随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getNoFeeAmt(), applyFeeDto.getNoFeeAmt());

        }
        //ELSE IF 数据字典:交易方向.行权-赎回 eq 入参.行权确认交易DTO.交易方向
        else if(StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), dealNow.getPs())) {
            //变量.交易各种费用_差额 = 变量.确认交易各种费用
            //变量.交易各种费用_差额.印花税	=		变量.确认交易各种费用.印花税	-		变量.交易各种费用.印花税
            印花税差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getStampDuty());
            //变量.交易各种费用_差额.经手费	=		变量.确认交易各种费用.经手费	-		变量.交易各种费用.经手费
            经手费差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getBrokerage());
            //变量.交易各种费用_差额.证管费	=		变量.确认交易各种费用.证管费	-		变量.交易各种费用.证管费
            证管费差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getCertificateFee());
            //变量.交易各种费用_差额.结算费	=		变量.确认交易各种费用.结算费	-		变量.交易各种费用.结算费
            结算费差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getClearingFee());
            //变量.交易各种费用_差额.佣金	=		变量.确认交易各种费用.佣金		-		变量.交易各种费用.佣金
            佣金差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getCommission());
            //变量.交易各种费用_差额.交易手续费	=		变量.确认交易各种费用.交易手续费	-		变量.交易各种费用.交易手续费
            交易手续费差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getTransactionFee());
            //变量.交易各种费用_差额.结算手续费	=		变量.确认交易各种费用.结算手续费	-		变量.交易各种费用.结算手续费
            结算手续费差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getSettlementFee());
            //变量.交易各种费用_差额.交易费用	=		变量.确认交易各种费用.交易费用		-		变量.交易各种费用.交易费用
            交易费用差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getFeeAmt());
            //变量.交易各种费用_差额.通道费		=		变量.确认交易各种费用.通道费		-		变量.交易各种费用.通道费
            通道费差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getChlFee());
            //变量.交易各种费用_差额.随交易支付_印花税	=	变量.确认交易各种费用.随交易支付_印花税	-	变量.交易各种费用.随交易支付_印花税
            印花税随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getStampDutyForDealPay());
            //变量.交易各种费用_差额.随交易支付_经手费	=	变量.确认交易各种费用.随交易支付_经手费	-	变量.交易各种费用.随交易支付_经手费
            经手费随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getBrokerageForDealPay());
            //变量.交易各种费用_差额.随交易支付_证管费	=	变量.确认交易各种费用.随交易支付_证管费	-	变量.交易各种费用.随交易支付_证管费
            证管费随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getCertificateFeeForDealPay());
            //变量.交易各种费用_差额.随交易支付_结算费	=	变量.确认交易各种费用.随交易支付_结算费	-	变量.交易各种费用.随交易支付_结算费
            结算费随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getClearingFeeForDealPay());
            //变量.交易各种费用_差额.随交易支付_佣金		=	变量.确认交易各种费用.随交易支付_佣金  -	变量.交易各种费用.随交易支付_佣金
            佣金随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getCommissionForDealPay());
            //变量.交易各种费用_差额.随交易支付_交易手续费	=	变量.确认交易各种费用.随交易支付_交易手续费	-	变量.交易各种费用.随交易支付_交易手续费
            交易手续费随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getTransactionFeeForDealPay());
            //变量.交易各种费用_差额.随交易支付_结算手续费	=	变量.确认交易各种费用.随交易支付_结算手续费	-	变量.交易各种费用.随交易支付_结算手续费
            结算手续费随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getSettlementFeeForDealPay());
            //变量.交易各种费用_差额.不随交易费用	=		变量.确认交易各种费用.不随交易费用	-		变量.交易各种费用.不随交易费用
            交易费用不随交易支付差额 = ArithmeticUtil.doSub(dealTypesFeeDto.getNoFeeAmt());
        }
        //2.3 利息差额处理
        //IF 数据字典：投资目的.公允价值变动类 eq 入参.行权确认交易DTO.投资目的
        if (StringUtil.equals(DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue(), dealNow.getInvAim())) {
            //变量.利息差额 =					入参.行权确认交易DTO.利息金额 - 变量.应计利息
            利息金额差额 = ArithmeticUtil.doSub(dealNow.getIntAmt(), 应计利息);
        }
        else {
            //IF 数据字典：息票品种.零息 eq 入参.标的信息.息票品种 OR 数据字典：息票品种.贴现 eq 入参.标的信息.息票品种
            if (StringUtil.equals(DictCodeList.息票品种_零息.getValue(), rstBaseinfo.getCouponSpecies())
                    || StringUtil.equals(DictCodeList.息票品种_贴现.getValue(), rstBaseinfo.getCouponSpecies())) {
                //变量.利息差额 = 0
                利息金额差额 = BigDecimal.ZERO;
            }
            //息票品种非零息、贴现
            else {
                //变量.利息差额 =	入参.行权确认交易DTO.利息金额 - 变量.应计利息
                利息金额差额 = ArithmeticUtil.doSub(dealNow.getIntAmt(), 应计利息);
            }
        }
        //2.4 清算款差额处理
        //变量.清算款差额 = 入参.行权确认交易DTO.交易金额 - 变量.交易金额_清算款
        清算款差额 = ArithmeticUtil.doSub(dealNow.getTradeAmt(), 交易金额清算款);
        //交易金额差额
        result.setTradeAmtDiff(清算款差额);
        //利息金额差额
        result.setIntAmtDiff(利息金额差额);
        //印花税差额
        result.setStampDutyDiff(印花税差额);
        //经手费差额
        result.setBrokerageDiff(经手费差额);
        //证管费差额
        result.setCertificateFeeDiff(证管费差额);
        //结算费差额
        result.setClearingFeeDiff(结算费差额);
        //佣金差额
        result.setCommissionDiff(佣金差额);
        //交易手续费差额
        result.setTransactionFeeDiff(交易手续费差额);
        //结算手续费差额
        result.setSettlementFeeDiff(结算手续费差额);
        //随交易支付_印花税差额
        result.setStampDutyForDealPayDiff(印花税随交易支付差额);
        //随交易支付_经手费差额
        result.setBrokerageForDealPayDiff(经手费随交易支付差额);
        //随交易支付_证管费差额
        result.setCertificateFeeForDealPayDiff(证管费随交易支付差额);
        //随交易支付_结算费差额
        result.setClearingFeeForDealPayDiff(结算费随交易支付差额);
        //随交易支付_佣金差额
        result.setCommissionForDealPayDiff(佣金随交易支付差额);
        //随交易支付_交易手续费差额
        result.setTransactionFeeForDealPayDiff(交易手续费随交易支付差额);
        //随交易支付_结算手续费差额
        result.setSettlementFeeForDealPayDiff(结算手续费随交易支付差额);
        //通道费差额
        result.setChlFeeDiff(通道费差额);
        //交易费用差额
        result.setFeeAmtDiff(交易费用差额);
        //不随交易支付费用差额
        result.setNoFeeAmtDiff(交易费用不随交易支付差额);
        //账务明细代码

        //印花税账务明细代码
        result.setStampDutyCode(dealTypesFeeDto.getStampDutyCode());
        //经手费账务明细代码
        result.setBrokerageCode(dealTypesFeeDto.getBrokerageCode());
        //证管费账务明细代码
        result.setCertificateFeeCode(dealTypesFeeDto.getCertificateFeeCode());
        //结算费账务明细代码
        result.setClearingFeeCode(dealTypesFeeDto.getClearingFeeCode());
        //佣金账务明细代码
        result.setCommissionCode(dealTypesFeeDto.getCommissionCode());
        //交易手续费账务明细代码
        result.setTransactionFeeCode(dealTypesFeeDto.getTransactionFeeCode());
        //结算手续费账务明细代码
        result.setSettlementFeeCode(dealTypesFeeDto.getSettlementFeeCode());
        //通道费账务明细代码
        result.setChlFeeCode(dealTypesFeeDto.getChlFeeCode());

        return result;
    }

    /**
     * 根据行权交易方向投资目的获取行权申请的交易方向及场景代码
     * @param ps 行权确认交易方向
     * @param invAim 投资目的
     * @return
     */
    private Map<String, String> getPsAndTableIdByExecDeal(String ps, String invAim) {
        String returnPs = ps;//返回的交易方向
        String tableId = "";//返回的场景代码
        //数据字典：交易方向.行权-回售+数据字典：投资目的.摊余成本类=数据字典：交易方向.行权-回售申请+数据字典：会计事件代码.摊余成本债券-回售申请
        if(StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), ps)
                && StringUtil.equals(DictCodeListExt1.投资目的_摊余成本类.getValue(), invAim)) {
            returnPs = DictCodeListExt3.交易方向_行权回售申请.getValue();
            tableId = DictCodeListTableId.会计场景代码_摊余成本债券_回售申请.getValue();
        }
        //数据字典：交易方向.行权-回售+数据字典：投资目的.公允价值变动类=数据字典：交易方向.行权-回售申请+数据字典：会计事件代码.公允价值损益类债券-回售申请
        else if (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), ps)
                && StringUtil.equals(DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue(), invAim)) {
            returnPs = DictCodeListExt3.交易方向_行权回售申请.getValue();
            tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_回售申请.getValue();
        }
        //数据字典：交易方向.行权-回售+数据字典：投资目的.综合收益类=数据字典：交易方向.行权-回售申请+数据字典：会计事件代码.综合收益债券-回售申请
        else if (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), ps)
                && StringUtil.equals(DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue(), invAim)) {
            returnPs = DictCodeListExt3.交易方向_行权回售申请.getValue();
            tableId = DictCodeListTableId.会计场景代码_综合收益债券_回售申请.getValue();
        }
        //数据字典：交易方向.行权-赎回+数据字典：投资目的.摊余成本类=数据字典：交易方向.行权-赎回结转+数据字典：会计事件代码.摊余成本债券-赎回结转
        else if (StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), ps)
                && StringUtil.equals(DictCodeListExt1.投资目的_摊余成本类.getValue(), invAim)) {
            returnPs = DictCodeListExt3.交易方向_行权赎回结转.getValue();
            tableId = DictCodeListTableId.会计场景代码_摊余成本债券_赎回结转.getValue();
        }
        //数据字典：交易方向.行权-赎回+数据字典：投资目的.公允价值变动类=数据字典：交易方向.行权-赎回结转+数据字典：会计事件代码.公允价值损益类债券-赎回结转
        else if (StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), ps)
                && StringUtil.equals(DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue(), invAim)) {
            returnPs = DictCodeListExt3.交易方向_行权赎回结转.getValue();
            tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_赎回结转.getValue();
        }
        //数据字典：交易方向.行权-赎回+数据字典：投资目的.综合收益类=数据字典：交易方向.行权-赎回结转+数据字典：会计事件代码.综合收益债券-赎回结转
        else if (StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), ps)
                && StringUtil.equals(DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue(), invAim)) {
            returnPs = DictCodeListExt3.交易方向_行权赎回结转.getValue();
            tableId = DictCodeListTableId.会计场景代码_综合收益债券_赎回结转.getValue();
        }
        Map<String, String> map = new HashMap<>();
        map.put("ps", returnPs);
        map.put("tableId", tableId);
        return map;
    }

    /**
     * 根据投资目的获取债券的应计利息基础科目性质
     * @param invAim 投资目的
     * @return
     */
    private String getExecYjlxBaseSubjectNatrue(String invAim) {
        String yjlxBaseSubjectNatrue = null;
        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(invAim)) {
            yjlxBaseSubjectNatrue = DictCodeBaseSubjectNatrue.摊余成本类债券应计利息.getValue();
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)) {
            yjlxBaseSubjectNatrue = DictCodeBaseSubjectNatrue.公允价值损益类应计利息.getValue();
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)) {
            yjlxBaseSubjectNatrue = DictCodeBaseSubjectNatrue.综合收益类应计利息.getValue();
        }
        return yjlxBaseSubjectNatrue;
    }

    /**
     * 根据投资目的获取债券的成本基础科目性质
     * @param invAim 投资目的
     * @return
     */
    private String getExecCbBaseSubjectNatrue(String invAim) {
        String cbBaseSubjectNatrue = null;
        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(invAim)) {
            cbBaseSubjectNatrue = DictCodeBaseSubjectNatrue.摊余成本类成本.getValue();
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)) {
            cbBaseSubjectNatrue = DictCodeBaseSubjectNatrue.公允价值损益类_成本.getValue();
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)) {
            cbBaseSubjectNatrue = DictCodeBaseSubjectNatrue.综合收益类成本.getValue();
        }
        return cbBaseSubjectNatrue;
    }

    /**
     * 调用共通最新账务查询一日余额(行权申请查询)
     * @param booksetId 账套代码
     * @param bookDate 入账日
     * @param bokDetailId 账务明细代码
     * @param tableId 场景代码
     * @param invAim 投资目的
     * @param batchExedetailSeqNo 批处理编号
     * @return
     */
    private List<BookLatestInfoDto> quaryExecSubjectBalance(String booksetId, Date bookDate, String bokDetailId,
                                                            String tableId, String invAim) {
        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(booksetId);
        searchDao.setBokDetailId(bokDetailId);
        searchDao.setBookDate(bookDate);
        searchDao.setTableId(tableId);
        // 2.2.应计利息类科目余额
        List<BookLatestInfoDto> list = new ArrayList<BookLatestInfoDto>();
        List<String> baseSubjectNatureList = new ArrayList<>();//查询的基础科目性质
        //添加成本
        baseSubjectNatureList.add(this.getExecCbBaseSubjectNatrue(invAim));
        //添加应计利息
        baseSubjectNatureList.add(this.getExecYjlxBaseSubjectNatrue(invAim));
        //添加清算款
        baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.清算款.getValue());
        searchDao.setBaseSubjectNatureList(baseSubjectNatureList);
        list = accountingCommonService.getBookLatestInfo(booksetId, bookDate, bokDetailId, baseSubjectNatureList, tableId,
                null, null, null, null, null);
        return list;
    }

    /**
     * 公共方法：标的类共通方法 - 标的信息查询，查询标的金融产品类型（投管分类）
     * @param finprodId 标的代码【必输项】
     * @return　返回值获取，直接查询标的金融产品类型（投管分类）
     */
    public String getfinprodType2(String finprodId) {
        return  finProductCommonMapper.getfinprodType2(finprodId);
    }

}
