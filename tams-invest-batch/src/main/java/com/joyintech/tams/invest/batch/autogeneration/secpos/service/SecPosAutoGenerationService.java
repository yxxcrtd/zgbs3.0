/*
 * 文件名：SecPosAutoGenerationService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：刘二通
 * 修改时间：2018年11月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.autogeneration.secpos.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.busicommon.productRating.dto.CommonProductRatingDto;
import com.joyintech.tams.busicommon.productRating.service.CommonProductRatingService;
import com.joyintech.tams.common.base.dao.AssetTargetDbMapper;
import com.joyintech.tams.common.base.entity.AssetTargetDb;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.batch.autogeneration.secpos.dao.SecPosAutoGenerationMapper;
import com.joyintech.tams.invest.batch.autogeneration.secpos.dto.AssetTargetDbDto;

/**
 * 组合持仓自动生成批处理Service
 *
 * @author 刘二通
 * @version 1.0 2018年11月20日
 * @see SecPosAutoGenerationService
 * @since 1.0
 */
@Service
public class SecPosAutoGenerationService {

    /**
     * SecPosAutoGenerationMapper
     */
    @Autowired
    private SecPosAutoGenerationMapper secPosAutoGenerationMapper;

    @Autowired
    private AssetTargetDbMapper assetTargetDbMapper;
    
    @Autowired
    private CommonProductRatingService commonProductRatingService;
    /**
     * 组合持仓自动生成
     *
     * @param batchDate 执行日
     * @param productIdList
     * @see
     * @since 1.0
     */
    public void autoGeneration(Date batchDate, List<String> productIdList) {

        // 删除当日持仓数据
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("batchDate", batchDate);
        if (productIdList.size() < 1000) {
            paramMap.put("productIdList", productIdList);
        }
        else {
            paramMap.put("productIdList", null);
        }
        secPosAutoGenerationMapper.deleteTodaySecPosData(paramMap);
        //add for 华兴 by liupengfei S
        //删除当日组合持仓融入融出明细数据
        secPosAutoGenerationMapper.deleteTodaySecPosDetailData(paramMap);
        //add for 华兴 by liupengfei E

        // 生成当日持仓数据
        paramMap.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());
        paramMap.put("POSITION_TYPE_01", DictCodeList.持仓类型_可用.getValue());
        paramMap.put("lastDate", DateUtil.addDay(batchDate, -1));
        secPosAutoGenerationMapper.insertTodaySecPosData(paramMap);
        //add for 华兴 by liupengfei S
        //通过汇总昨日持仓数据和当日融入融出明细数据，生成当日持仓数据
        paramMap.put("POSITION_TYPE_05", DictCodeList.持仓类型_融入.getValue());
        paramMap.put("POSITION_TYPE_06", DictCodeList.持仓类型_融出.getValue());
        secPosAutoGenerationMapper.insertTodaySecPosDetailData(paramMap);
        //add for 华兴 by liupengfei E
		
		secPosAutoGenerationMapper.deleteTodayStaSecPosData(paramMap);
        
        secPosAutoGenerationMapper.insertTodayStaSecPosData(paramMap);
    }


    /**
     * 组合持仓分析报表自动生成
     *
     * @param batchDate 执行日
     * @param productIdList
     * @see
     * @since 1.0
     */
    public void autoSecReportGeneration(Date batchDate, List<String> productIdList) {

        // 删除当日持仓数据
        Map<String, Object> paramMap = new HashMap<String, Object>();
        batchDate = DateUtil.addDay(batchDate, -1);
        paramMap.put("batchDate", batchDate);
        if (productIdList.size() < 1000) {
            paramMap.put("productIdList", productIdList);
        }
        else {
            paramMap.put("productIdList", null);
        }
        paramMap.put("lastDate", DateUtil.addDay(batchDate, -1));
        
        //查询产品风险等级评定排序sql片段
        Map<String,Object> sqlSortMap = new HashMap<String,Object>();
        sqlSortMap.put("oType", "98");
        String sqlSortString = commonProductRatingService.getProductRatingOrderSql(sqlSortMap);
        paramMap.put("sqlSortString", sqlSortString);
        
        //塞入字典值
        paramMap.put("PETE_DIMENTION_01", DictCodeListExt3.穿透维度_不穿透.getValue());
        paramMap.put("PETE_DIMENTION_02", DictCodeListExt3.穿透维度_穿透.getValue());
        paramMap.put("PETE_DIMENTION_03", DictCodeListExt3.穿透维度_底层资产.getValue());
        paramMap.put("GRADE_SOURCE_TYPE_01", DictCodeListExt3.评级来源类型_外部.getValue());
        paramMap.put("GRADE_SOURCE_TYPE_02", DictCodeListExt3.评级来源类型_内部.getValue());
        paramMap.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        paramMap.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        paramMap.put("INSTITUTION_INDUSTRY_FIRST_LEVEL_10", DictCodeList.机构所属行业一级分类_金融业.getValue());
        
        paramMap.put("NATURE_ORG_01", DictCodeListExt3.融资人性质_企业类_事业类_债务人或保证人.getValue());
        paramMap.put("NATURE_ORG_02", DictCodeListExt3.融资人性质_商业银行.getValue());
        paramMap.put("NATURE_ORG_03", DictCodeListExt3.融资人性质_非银金融机构.getValue());
        paramMap.put("NATURE_ORG_04", DictCodeListExt3.融资人性质_企事业法人_银行间市场证券化.getValue());
        paramMap.put("NATURE_ORG_05", DictCodeListExt3.融资人性质_金融机构.getValue());
        paramMap.put("NATURE_ORG_06", DictCodeListExt3.融资人性质_其他证券化.getValue());
        paramMap.put("NATURE_ORG_07", DictCodeListExt3.融资人性质_企事业法人_金融机构.getValue());
        
        paramMap.put("CUSTOM_ASSET_TYPE_THREE_12", DictCodeListExt3.行内资产三级分类_资产支持证券.getValue());
        paramMap.put("CUSTOM_ASSET_TYPE_FOUR_015", DictCodeListExt3.行内资产四级分类_短融.getValue());
        paramMap.put("CUSTOM_ASSET_TYPE_FOUR_016", DictCodeListExt3.行内资产四级分类_超短融.getValue());
        paramMap.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        paramMap.put("CONFIG_TYPE_PED_RISK_TYPE", DictCodeListExt2.配置类型_产品存续期风险分类.getValue());

        //删除当前跑平日汇总数据
        secPosAutoGenerationMapper.deleteReportTodaySecPosData(paramMap);
        //获取当前跑平日汇总数据
        List<AssetTargetDbDto> secPosDataList = secPosAutoGenerationMapper.getSecPosData(paramMap);
        List<AssetTargetDbDto> underlyingAssetsData = secPosAutoGenerationMapper.getUnderlyingAssetsData(paramMap);
        //不穿透的资产总规模
        BigDecimal noPassBshareScale = BigDecimal.ZERO;
        //穿透的资产总规模
        BigDecimal isPassBshareScale = BigDecimal.ZERO;
        //底层的资产总规模
        BigDecimal underlyingBshareScale = BigDecimal.ZERO;
        //底层资产汇总List
        List<AssetTargetDbDto> underlyingAssetsSumList = new ArrayList<AssetTargetDbDto>();
        
        if(!StringUtil.isEmptyList(secPosDataList) &&secPosDataList.size()>0) {
            Map<String,Object> noPassBshareScaleMap = new HashMap<String, Object>();
            Map<String,Object> isPassBshareScaleMap = new HashMap<String, Object>();
            
            //先按组合ID汇总资产规模
            for(AssetTargetDbDto dto:secPosDataList) {
                if(StringUtil.isNotEmpty(dto.getRiskAssetType2())) {
                    if(StringUtil.equals(dto.getIsPerne(), DictCodeListExt3.穿透维度_不穿透.getValue())) {
                        noPassBshareScale = (BigDecimal)noPassBshareScaleMap.get(dto.getPortfolioId());  
                        noPassBshareScale = ArithmeticUtil.doAdd(noPassBshareScale,dto.getBshareScale());
                        
                        noPassBshareScaleMap.put(dto.getPortfolioId(), noPassBshareScale);
                    } else if (StringUtil.equals(dto.getIsPerne(), DictCodeListExt3.穿透维度_穿透.getValue()))                        
                        isPassBshareScale = (BigDecimal)isPassBshareScaleMap.get(dto.getPortfolioId());  
                        isPassBshareScale = ArithmeticUtil.doAdd(isPassBshareScale,dto.getBshareScale());
                        
                        isPassBshareScaleMap.put(dto.getPortfolioId(), isPassBshareScale);
                    } 
                }
            
            if(!StringUtil.isEmptyList(underlyingAssetsData) &&underlyingAssetsData.size()>0) {
                //按组合ID汇总资产总规模
                Map<String,Object> underlyingBshareScaleMap = new HashMap<String, Object>();
                for(AssetTargetDbDto dto:underlyingAssetsData) {
                    AssetTargetDbDto underlyingBshareScaleSumDto = (AssetTargetDbDto)underlyingBshareScaleMap.get(dto.getPortfolioId());
                    if(underlyingBshareScaleSumDto != null) {
                        underlyingBshareScaleSumDto.setBshareScale(ArithmeticUtil.doAdd(underlyingBshareScaleSumDto.getBshareScale(),dto.getBshareScale()));
                    } else {
                        underlyingBshareScaleSumDto = new AssetTargetDbDto();
                        underlyingBshareScaleSumDto.setPortfolioId(dto.getPortfolioId());
                        underlyingBshareScaleSumDto.setBshareScale(dto.getBshareScale());
                        underlyingBshareScaleMap.put(dto.getPortfolioId(), underlyingBshareScaleSumDto);
                    }
                    
                    underlyingBshareScale = ArithmeticUtil.doAdd(underlyingBshareScale,dto.getBshareScale());
                }
                
                //再次循环计算出集中度和风险评定等级
                for(AssetTargetDbDto dto:underlyingAssetsData) {
                    AssetTargetDbDto underlyingBshareScaleSumDto = (AssetTargetDbDto)underlyingBshareScaleMap.get(dto.getPortfolioId());
                    if(underlyingBshareScaleSumDto != null) {
                        BigDecimal crn = ArithmeticUtil.divide(dto.getBshareScale(), underlyingBshareScaleSumDto.getBshareScale(), 2);
                        dto.setCrn(crn);
                        String assetRiskType = this.getDtoAssetRiskType(dto);
                        dto.setAssetRiskType(assetRiskType);
                        
                        BigDecimal assetRiskTypevalue = ArithmeticUtil.doAdd(StringUtil.isNotEmpty(underlyingBshareScaleSumDto.getAssetRiskType())?
                            new BigDecimal(underlyingBshareScaleSumDto.getAssetRiskType()): BigDecimal.ZERO,
                            ArithmeticUtil.multiply(crn,StringUtil.isNotEmpty(assetRiskType)?new BigDecimal(assetRiskType): BigDecimal.ZERO));
                        underlyingBshareScaleSumDto.setAssetRiskType(assetRiskTypevalue.toString()); 
                    }
                    //如果不是债券，rate(票面利率%)和BShareFaceValue(持仓券面总额)设置成空
                    if(!StringUtil.equals(dto.getPinanAssetType(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
                        dto.setRate(null);
                        dto.setBshareFaceValue(null);
                    }
                    assetTargetDbMapper.insert(dto);  
                }
                
                for(String key:underlyingBshareScaleMap.keySet()) {
                    AssetTargetDbDto underlyingBshareScaleSumDto = (AssetTargetDbDto)underlyingBshareScaleMap.get(key);
                    String assetRiskType = underlyingBshareScaleSumDto.getAssetRiskType();
                    if(StringUtil.isNotEmpty(assetRiskType)) {
                        underlyingBshareScaleSumDto.setAssetRiskType(this.IRConvert(new BigDecimal(assetRiskType))); 
                    }
                    underlyingBshareScaleSumDto.setCrn(ArithmeticUtil.divide(underlyingBshareScaleSumDto.getBshareScale(), underlyingBshareScale, 2));
                    underlyingAssetsSumList.add(underlyingBshareScaleSumDto);
                }
                
            }
            
            for(AssetTargetDbDto item:secPosDataList) {
                //剩余期限计算
                if(item.getMdate()!=null){
                    item.setLastPeriod(calculationSurplusDays(item.getFinprodId(), DateUtil.format(item.getVdate()), DateUtil.format(item.getMdate()), DateUtil.format(batchDate), item.getBranch()));
                }
                //获取剩余期限、成本收益率、含权信息、下一行权日、剩余行权期限信息、债券评级
                if (StringUtil.isNotEmpty(item.getPinanAssetType()) && item.getPinanAssetType().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    //****债券评级**** begin
                    //外部评级
                    List<CommonProductRatingDto> ratingResultList = getRatingResult(item.getFinprodMarketId(), "01");//01是外部02是内部
                    if (!StringUtil.isEmptyList(ratingResultList) && ratingResultList.size() > 0) {
                        //获取外部主体评级
                        List<CommonProductRatingDto> collectMList = ratingResultList.stream().filter(ii -> ii.getoType().equals("91")).collect(Collectors.toList());
                        if (!StringUtil.isEmptyList(collectMList) && collectMList.size() > 0) {
                            item.setmGradeResult(collectMList.get(0).getGradeResult());//主体评级结果
                            item.setmGradeOrg(collectMList.get(0).getGradeOrgId());// 主体评级结构

                        }
                        //获取外部债项评级
                        List<CommonProductRatingDto> collectBList = ratingResultList.stream().filter(ii -> ii.getoType().equals("02")).collect(Collectors.toList());
                        if (!StringUtil.isEmptyList(collectBList) && collectBList.size() > 0) {
                            item.setbGradeResult(collectBList.get(0).getGradeResult());//债项评级结果
                            item.setbGradeOrg(collectBList.get(0).getGradeOrgId());// 债项评级结构

                        }
                    }

                    //内部评级
                    List<CommonProductRatingDto> ratingResultListIn = getRatingResult(item.getFinprodMarketId(), "02");//01是外部02是内部
                    if (!StringUtil.isEmptyList(ratingResultListIn) && ratingResultListIn.size() > 0) {
                        //获取内部主体评级
                        List<CommonProductRatingDto> collectMListIn = ratingResultListIn.stream().filter(ii -> ii.getoType().equals("91")).collect(Collectors.toList());
                        if (!StringUtil.isEmptyList(collectMListIn) && collectMListIn.size() > 0) {
                            item.setmGradeResultIn(collectMListIn.get(0).getGradeResult());//主体评级结果
                            item.setmGradeOrgIn(collectMListIn.get(0).getGradeOrgId());// 主体评级结构

                        }
                        //获取内部债项评级
                        List<CommonProductRatingDto> collectBListIn = ratingResultListIn.stream().filter(ii -> ii.getoType().equals("02")).collect(Collectors.toList());
                        if (!StringUtil.isEmptyList(collectBListIn) && collectBListIn.size() > 0) {
                            item.setbGradeResultIn(collectBListIn.get(0).getGradeResult());//债项评级结果
                            item.setbGradeOrgIn(collectBListIn.get(0).getGradeOrgId());// 债项评级结构

                        }
                    }
                    // ****债券评级**** end
                    //成本收益率计算
                    item.setCostRate(calculationCostRate(item.getFinprodId(), DateUtil.format(batchDate), item.getPortfolioId(), item.getIsPerne()));

                    if (StringUtil.equals(item.getIsRight(),DictCodeList.是否_是.getValue())) {
                        List<FinExercisePlan> finExercisePlanList =new ArrayList<FinExercisePlan>();
                        if(item.getExDate() !=null && item.getExDate() !=""){
                            finExercisePlanList = calculationSurplusExercise(item.getFinprodId(), DateUtil.format(batchDate), DictCodeListExt1.金融产品类型_投管用_债券.getValue(), "", item.getExDate());
                        }
                        if (finExercisePlanList != null && !StringUtil.isEmptyList(finExercisePlanList) && finExercisePlanList.size() > 0) {
                            //下一行权日
                            item.setExNextDate(finExercisePlanList.get(0).getExerciseDate());
                            //剩余行权日期计算
                            item.setLastExPeriod(calculationExPeriodDays(item.getFinprodId(), DateUtil.format(item.getVdate()), DateUtil.format(batchDate), item.getBranch(), DateUtil.format(finExercisePlanList.get(0).getExerciseDate())));
                            //含权信息获取
                            if (item.getIsSus().equals(DictCodeList.是否_是.getValue())) {
                                //含权信息 = 数据字典.行权信息.永续
                                item.setExInfo(DictCodeListExt3.含权信息_永续.getCodeName());
                            }
                            if (item.getIsRight().equals(DictCodeList.是否_是.getValue())) {
                                if (finExercisePlanList.size() == 1 && finExercisePlanList.get(0).getExerciseType().equals(DictCodeList.行权方式_赎回.getValue())) {
                                    item.setExInfo(DictCodeListExt3.含权信息_赎回权.getCodeName());
                                }
                                if (finExercisePlanList.size() == 1 && finExercisePlanList.get(0).getExerciseType().equals(DictCodeList.行权方式_回售.getValue())) {
                                    item.setExInfo(DictCodeListExt3.含权信息_回售权.getCodeName());
                                }
                                if (finExercisePlanList.size() > 1) {
                                    Map<String, List<FinExercisePlan>> groupBylist =
                                            finExercisePlanList.stream().collect(Collectors.groupingBy(FinExercisePlan::getExerciseType));
                                    if (groupBylist.containsKey(DictCodeList.行权方式_赎回.getValue())) {
                                        item.setExInfo(DictCodeListExt3.含权信息_赎回权.getCodeName());
                                    }
                                    if (groupBylist.containsKey(DictCodeList.行权方式_回售.getValue())) {
                                        item.setExInfo(DictCodeListExt3.含权信息_回售权.getCodeName());
                                    }
                                    if (groupBylist.containsKey(DictCodeList.行权方式_赎回.getValue()) && groupBylist.containsKey(DictCodeList.行权方式_回售.getValue())) {
                                        item.setExInfo(DictCodeListExt3.含权信息_回售权.getCodeName() + "," + DictCodeListExt3.含权信息_赎回权.getCodeName());
                                    }
                                }

                            }

                        }
                    }
                    
                    
                }
                
              //计算集中度
                if(StringUtil.isNotEmpty(item.getRiskAssetType2())) {
                    BigDecimal crn = BigDecimal.ZERO;
                    if(StringUtil.equals(item.getIsPerne(), DictCodeListExt3.穿透维度_不穿透.getValue())) {
                        noPassBshareScale = (BigDecimal)noPassBshareScaleMap.get(item.getPortfolioId());
                        crn = ArithmeticUtil.divide(item.getBshareScale(), noPassBshareScale, 2); 
                    } else {
                        isPassBshareScale = (BigDecimal)isPassBshareScaleMap.get(item.getPortfolioId());
                        crn = ArithmeticUtil.divide(item.getBshareScale(), isPassBshareScale, 2);
                    }
                    
                    item.setCrn(crn);

                    //设置资产风险等级
                    item.setAssetRiskType(getDtoAssetRiskType(item));
                }
                
                for(AssetTargetDbDto underlyingAssetsSumDto : underlyingAssetsSumList) {
                    if(StringUtil.equals(item.getPortfolioId(), underlyingAssetsSumDto.getPortfolioId())
                        && StringUtil.equals(item.getIsPerne(), DictCodeListExt3.穿透维度_不穿透.getValue())) {
                        item.setAssetRiskType(underlyingAssetsSumDto.getAssetRiskType());
                    }
                }
                //如果不是债券，rate(票面利率%)和BShareFaceValue(持仓券面总额)设置成空
                if(!StringUtil.equals(item.getPinanAssetType(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
                    item.setRate(null);
                    item.setBshareFaceValue(null);
                }
                if(StringUtil.equals(item.getPinanAssetType(),DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue())){
                    item.setPrin(item.getPosAmount());
                }
                try{
                    assetTargetDbMapper.insert(item);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }


    /**
     * 成本收益率计算
     *资产代码，查询日期
     * @param
     * @return
     */

    public BigDecimal calculationCostRate(String finprodId, String searchDate, String portfolioId, String pass)
    {
        BigDecimal yesterDayPosAmount = new BigDecimal(0); //昨日持仓数量
        BigDecimal yesterDaycostRate = new BigDecimal(0); //昨日综合成本收益率
        Map<String, Object> map=new HashMap<>();
        map.put("finprodId",finprodId);
        map.put("date", searchDate);
        map.put("pass",pass);
        //根据资产代码查询相关所有的交易信息

        List<TrdProductDeal> tradeList = secPosAutoGenerationMapper.getTradeListByFinprodId(map);

        // 根据资产代码查询昨日指标信息的持仓数量和综合成本收益率
        map.put("date", DateUtil.format(DateUtil.addDay(DateUtil.parse(searchDate),-1)));//查询日-1
        AssetTargetDb yesterdayTarget = secPosAutoGenerationMapper.getYesterdayTarget(map);

        if(yesterdayTarget!=null )
        {
            yesterDayPosAmount=yesterdayTarget.getPosAmount()==null?new BigDecimal(0):yesterdayTarget.getPosAmount();
            yesterDaycostRate=yesterdayTarget.getCostRate()==null?new BigDecimal(0):yesterdayTarget.getCostRate();
        }
        if(!StringUtil.isEmptyList(tradeList))
        {
            for (TrdProductDeal trdProductDeal : tradeList)
            {
                if(trdProductDeal.getPs().equals(DictCodeList.交易方向_买入.getValue()))
                {

                    if(yesterDayPosAmount.add(trdProductDeal.getShareAmt()==null?new BigDecimal(0):trdProductDeal.getShareAmt()).compareTo(new BigDecimal(0))!=0) {
                        yesterDaycostRate = (yesterDayPosAmount.multiply(yesterDaycostRate).add(trdProductDeal.getShareAmt() == null ? new BigDecimal(0) : trdProductDeal.getShareAmt().multiply(trdProductDeal.getYield() == null ? new BigDecimal(0) : trdProductDeal.getYield()))).divide(yesterDayPosAmount.add(trdProductDeal.getShareAmt()== null ? new BigDecimal(0) : trdProductDeal.getShareAmt()),6,BigDecimal.ROUND_HALF_UP);
                    }
                    yesterDayPosAmount=yesterDayPosAmount.add(trdProductDeal.getShareAmt()==null?new BigDecimal(0):trdProductDeal.getShareAmt());

                }

                if(trdProductDeal.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {

                    yesterDayPosAmount = yesterDayPosAmount.subtract(trdProductDeal.getTradeAmt() == null ? new BigDecimal(0) : trdProductDeal.getTradeAmt());
                }
            }

        }
//        return yesterDaycostRate.multiply(new BigDecimal(100)).setScale(4,BigDecimal.ROUND_HALF_UP);
        //update by lwj 组合持仓分析报表自动生成批处理跑批报值大于为此列指定的允许精度错误  start 20200331
        return yesterDaycostRate.setScale(6,BigDecimal.ROUND_HALF_UP);
        //update by lwj 组合持仓分析报表自动生成批处理跑批报值大于为此列指定的允许精度错误  end 20200331
    }
    /**
     * 剩余期限计算
     *  资产代码、起息日、查询日期、分支序号
     * @param
     * @return
     */
    public  BigDecimal calculationSurplusDays(String finprodId,String vdate,String mdate,String searchDate, Long branch)
    {
        BigDecimal surplusDays=new BigDecimal(0);// 剩余期限
        //根据资产代码、起息日、分支序号 查询对应的计息基础
        Map<String, Object> map=new HashMap<>();
        map.put("finprodId",finprodId);
        map.put("effDate",vdate);
        map.put("branch",branch);
        map.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
        FinCashCalcRule finCashCaluRule = secPosAutoGenerationMapper.getBasisByFinprodId
                (map);
        if(finCashCaluRule!=null )
        {
            if(finCashCaluRule.getBasis().equals(DictCodeList.计息基础_A_365.getValue()) ||finCashCaluRule.getBasis().equals(DictCodeList.计息基础_A_365F.getValue()))
            {
                //变量.剩余期限 = round（（参数.到期日 - 参数.查询日）/365，2）
                surplusDays= new BigDecimal(getDay(DateUtil.parse(searchDate),DateUtil.parse(mdate))).divide(new BigDecimal(365),2, BigDecimal.ROUND_HALF_UP);
            }
            else if(finCashCaluRule.getBasis().equals(DictCodeList.计息基础_A_360.getValue()) ||finCashCaluRule.getBasis().equals(DictCodeList.计息基础_30_360.getValue()))
            {
                //变量.剩余期限 = round（（参数.到期日 - 参数.查询日）/360，2）
                surplusDays= new BigDecimal(getDay(DateUtil.parse(searchDate),DateUtil.parse(mdate))).divide(new BigDecimal(360),2, BigDecimal.ROUND_HALF_UP);
            }
            else   if(finCashCaluRule.getBasis().equals(DictCodeList.计息基础_A_A_平均分配.getValue()) ||finCashCaluRule.getBasis().equals(DictCodeList.计息基础_A_A_按实际天数分配.getValue()) ||finCashCaluRule.getBasis().equals(DictCodeList.计息基础_A_A.getValue()))
            {
                //if 参数.到期日.年份 = 参数.查询日.年份  变量.剩余期限 = round（（参数.到期日 - 参数.查询日）/（参数.查询日的年的12月31-参数.查询日的年的1月1+1），2）

                if(DateUtil.getYear(mdate)==DateUtil.getYear(searchDate)) {

                    surplusDays = new BigDecimal(getDay(DateUtil.parse(searchDate), DateUtil.parse(mdate))).divide(new BigDecimal(DateUtil.getDayOfYear(searchDate)), 2, BigDecimal.ROUND_HALF_UP);
                }
            }
            else
            {
                if(DateUtil.getYear(mdate)==DateUtil.getYear(searchDate)) {

                    surplusDays = new BigDecimal(getDay(DateUtil.parse(searchDate), DateUtil.parse(mdate))).divide(new BigDecimal(DateUtil.getDayOfYear(searchDate)), 2, BigDecimal.ROUND_HALF_UP);
                }
            }
        }
        return surplusDays;
    }

    /**
     * 剩余行权期限计算
     *  资产代码、起息日\下一行权日期、查询日期\分支序号
     * @param
     * @return
     */
    public  BigDecimal calculationExPeriodDays(String finprodId,String date,String searchDate, Long branch,String lastExPeriodDate)
    {
        BigDecimal surplusDays=new BigDecimal(0);// 剩余行权期限
        //根据资产代码、起息日、分支序号 查询对应的计息基础
        Map<String, Object> map=new HashMap<>();
        map.put("finprodId",finprodId);
        map.put("effDate",date);
        map.put("branch",branch);
        map.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
        FinCashCalcRule finCashCalcRule = secPosAutoGenerationMapper.getBasisByFinprodId(
                map);
        if(finCashCalcRule!=null )
        {

            if(finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_365.getValue()) ||finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_365F.getValue()))
            {
                //变量.剩余行权期限 = round（（参数.下一行权日期 - 参数.查询日）/365，2）
                surplusDays= new BigDecimal(getDay(DateUtil.parse(searchDate),DateUtil.parse(lastExPeriodDate))).divide(new BigDecimal(365),2, BigDecimal.ROUND_HALF_UP);
            }
            else if(finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_360.getValue()) ||finCashCalcRule.getBasis().equals(DictCodeList.计息基础_30_360.getValue()))
            {
                //变量.剩余行权期限 = round（（参数.下一行权日期 - 参数.查询日）/360，2）
                surplusDays= new BigDecimal(getDay(DateUtil.parse(searchDate),DateUtil.parse(lastExPeriodDate))).divide(new BigDecimal(360),2, BigDecimal.ROUND_HALF_UP);
            }
            else   if(finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_A_平均分配.getValue()) ||finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_A_按实际天数分配.getValue()) ||finCashCalcRule.getBasis().equals(DictCodeList.计息基础_A_A.getValue()))
            {
                //if 参数.到期日.年份 = 参数.查询日.年份  变量.剩余期限 = round（（参数.到期日 - 参数.查询日）/（参数.查询日的年的12月31-参数.查询日的年的1月1+1），2）

                if(DateUtil.getYear(lastExPeriodDate)==DateUtil.getYear(searchDate)) {

                    surplusDays = new BigDecimal(getDay(DateUtil.parse(searchDate), DateUtil.parse(lastExPeriodDate))).divide(new BigDecimal(DateUtil.getDayOfYear(searchDate)), 2, BigDecimal.ROUND_HALF_UP);
                }
            }
            else
            {
                if(DateUtil.getYear(lastExPeriodDate)==DateUtil.getYear(searchDate)) {

                    surplusDays = new BigDecimal(getDay(DateUtil.parse(searchDate), DateUtil.parse(lastExPeriodDate))).divide(new BigDecimal(DateUtil.getDayOfYear(searchDate)), 2, BigDecimal.ROUND_HALF_UP);
                }
            }
        }
        return surplusDays;
    }
    /**
     * 下一行权日查询
     *资产代码、查询日、金融产品类型、行权方式\含权摊销日
     * @param
     * @return
     */
    public   List<FinExercisePlan>  calculationSurplusExercise(String finprodId, String seachDate, String finprodType, String execMode, String exDate)
    {
        // Sonar Fix modify by huwei 220609   Introduce a new variable
        /*String nextExDate="";//下一行权日
        if(StringUtil.equals(exDate,DictCodeList.含权债摊销日_最近赎回行权日.getValue()))
        {
            execMode=DictCodeList.行权方式_赎回.getValue();
        }
        if(StringUtil.equals(exDate,DictCodeList.含权债摊销日_最近回售行权日.getValue()))
        {
            execMode=DictCodeList.行权方式_回售.getValue();
        }*/
        Map<String, Object> map=new HashMap<>();
        map.put("finprodId",finprodId);
        map.put("exerciseDate",seachDate);
        map.put("finprodtype",finprodType);
        List<FinExercisePlan> finExercisePlanList = secPosAutoGenerationMapper.getExerciseDate( map);
        return finExercisePlanList;

    }
    //获取俩个时间相差的天数
    public  long getDay( Date date1,Date date2)
    {
        // 获取相差的天数
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        long timeInMillis1 = calendar.getTimeInMillis();
        calendar.setTime(date2);
        long timeInMillis2 = calendar.getTimeInMillis();
        long betweenDays =  (timeInMillis2 - timeInMillis1) / (1000L*3600L*24L);
        return betweenDays;

    }


    /**
     * 根据资产代码、评级类型（主体、债项）获取对应的评级信息
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public List<CommonProductRatingDto> getRatingResult(String finprodId,String gradeType)
    {
        Map<String, Object> mapRating = new HashMap<>();
        // 查询外部主体评级相关信息
        mapRating.put("finprodId", finprodId);
        mapRating.put("gradeType", gradeType);
        List<CommonProductRatingDto> ratingResultList = secPosAutoGenerationMapper.getBestProductRating(mapRating);
        return  ratingResultList;
    }
    
    private String getDtoAssetRiskType(AssetTargetDbDto dto) {
        String assetRiskType = "";
        if(StringUtil.isNotEmpty(dto.getGradeResult())) {
            BigDecimal gradeResult = new BigDecimal(dto.getGradeResult());
            //判断是否含权 or 金融机构次级债券 ，如果是，将之前算出的评级结果等级升一个等级
             if(StringUtil.equals(dto.getIsRight(),DictCodeList.是否_是.getValue())
                 || StringUtil.equals(dto.getCustomAssetTypeFour(),DictCodeListExt3.行内资产四级分类_金融机构次级债.getValue())
                 ) {
                 
                 if(!StringUtil.equals(dto.getGradeResult(),"01")) {
                     gradeResult = ArithmeticUtil.subtract(gradeResult, BigDecimal.ONE);
                 }
                 
             }
             //根据资产风险等级映射表传入风险等级资产一、二级分类、集中度、评级类型、融资人性质/资产类型、评级结果 查询资产等级 ※如果没找到资产等级降一级继续匹配，当都匹配不了则为空
             Map<String,Object> paramsMap = new HashMap<String, Object>();
             paramsMap.put("gradeResult", gradeResult);
             paramsMap.put("riskAssetType1", StringUtil.isNotEmpty(dto.getRiskAssetType1())?dto.getRiskAssetType1():"");
             paramsMap.put("riskAssetType2", StringUtil.isNotEmpty(dto.getRiskAssetType2())?dto.getRiskAssetType2():"");
             paramsMap.put("gradeType", StringUtil.isNotEmpty(dto.getGradeType())?dto.getGradeType():"");
             paramsMap.put("inOrOut", StringUtil.isNotEmpty(dto.getInOrOut())?dto.getInOrOut():"");
             paramsMap.put("crn", StringUtil.isNotEmpty(dto.getCrn())?dto.getCrn():BigDecimal.ZERO);
             paramsMap.put("natureOrg", StringUtil.isNotEmpty(dto.getNatureOrg())?dto.getNatureOrg():"");
             
            List<String> assetRiskTypeList = new ArrayList<>();
            if(!StringUtil.isEmpty(dto.getRiskAssetType1())){
                assetRiskTypeList = secPosAutoGenerationMapper.getAssetRiskTypeList(paramsMap);
            }
             if(!StringUtil.isEmptyList(assetRiskTypeList)) {
                 assetRiskType = assetRiskTypeList.get(0);
             }
             
             if((StringUtil.equals(dto.getCustomAssetTypeFive(),DictCodeListExt3.行内资产五级分类_上市银行优先股.getValue())
                 || StringUtil.equals(dto.getIsSus(),DictCodeList.是否_是.getValue()))
                 && StringUtil.isNotEmpty(assetRiskType)) {
                 if(!StringUtil.equals(dto.getGradeResult(),"01")) {
                 }
                 
             }
        }

        
        return assetRiskType;
    }
    
    /**
     * 组合资产最终风险等级按PR1=[0，1.25]、PR2=(1.25，2.25]、PR3=(2.25，3.25]、PR4=(3.25，4.25]、PR5=(4.25，∞）
     */
    private String IRConvert(BigDecimal IR) {
        String newIR = "0";
        
        if(BigDecimal.ZERO.compareTo(IR)<=0 
            && BigDecimal.valueOf(1.25).compareTo(IR)>=0) {
            newIR = "01";
        } 
        else if (BigDecimal.valueOf(1.25).compareTo(IR)<0
            && BigDecimal.valueOf(2.25).compareTo(IR)>=0) {
            newIR = "02";
        } 
        else if (BigDecimal.valueOf(2.25).compareTo(IR)<0
            && BigDecimal.valueOf(3.25).compareTo(IR)>=0) {
            newIR = "03";
        } 
        else if (BigDecimal.valueOf(3.25).compareTo(IR)<0
            && BigDecimal.valueOf(4.25).compareTo(IR)>=0) {
            newIR = "04";
        } 
        else if (BigDecimal.valueOf(4.25).compareTo(IR)<0) {
            newIR = "05";
        } 
        return newIR; 
    }
}
