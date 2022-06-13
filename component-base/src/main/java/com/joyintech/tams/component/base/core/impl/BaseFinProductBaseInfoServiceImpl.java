/*
 * 文件名：StandardFinancialProductsImpl.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年8月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.base.core.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.base.core.BaseFinProductBaseInfoService;
import com.joyintech.tams.component.base.core.BaseFinProductCashCalcRuleService;
import com.joyintech.tams.component.base.core.BaseFinProductCashCountingRuleService;
import com.joyintech.tams.component.base.core.BaseFinProductCashInfoService;
import com.joyintech.tams.component.base.core.BaseFinProductCashPayPlanService;
import com.joyintech.tams.component.base.core.BaseFinProductCashRateRuleService;
import com.joyintech.tams.component.base.core.BaseFinProductExercisePlanService;
import com.joyintech.tams.component.base.core.BaseFinProductExerciseRuleService;
import com.joyintech.tams.component.base.core.BaseFinProductOpenPlanService;
import com.joyintech.tams.component.base.core.BaseFinProductOpenRuleService;
import com.joyintech.tams.component.base.core.BaseFinProductPayRuleService;
import com.joyintech.tams.component.base.core.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.component.base.core.dto.BaseFinProductCashCalcRuleDto;
import com.joyintech.tams.component.base.core.dto.BaseFinProductCashCountingRuleDto;
import com.joyintech.tams.component.base.core.dto.BaseFinProductCashRateRuleDto;
import com.joyintech.tams.component.base.core.dto.BaseFinProductExerciseRuleDto;
import com.joyintech.tams.component.base.core.dto.BaseFinProductOpenPlanDto;
import com.joyintech.tams.component.base.core.dto.BaseFinProductOpenRuleDto;
import com.joyintech.tams.component.base.core.impl.dao.BaseFinRecommentMapper;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;

/**
 * 标的类金融产品基本信息组件 〈功能详细描述〉
 * 
 * @author xiexinting
 * @version 1.0 2018年8月29日
 * @see BaseFinProductBaseInfoServiceImpl
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseFinProductBaseInfoServiceImpl implements BaseFinProductBaseInfoService {

    /**
     * 自动生成标的类金融产品表mapper对象
     */
    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * 自动生成标的类金融产品附属表mapper对象
     */
    @Autowired
    private FinProductAddMapper finProductAddMapper;

    /**
     * 自动生成金融产品分类表mapper对象
     */
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    // 增加推荐方与推荐费率 add by renping 20191014 start
    /**
     * 自动生成标的类金融产品推荐表mapper对象
     */
    @Autowired
    private FinProductRecommentMapper finProductRecommentMapper;

    /**
     * 标的类金融产品推荐mapper对象
     */
    @Autowired(required = false)
    private BaseFinRecommentMapper baseFinRecommentMapper;
    // 增加推荐方与推荐费率 add by renping 20191014 end

    /**
     * 金融产品现金流信息组件
     */
    @Autowired(required = false)
    private BaseFinProductCashInfoService baseFinProductCashInfoService;

    /**
     * 金融产品现金流支付规则组件
     */
    @Autowired(required = false)
    private BaseFinProductPayRuleService baseFinProductPayRuleService;

    /**
     * 金融产品现金流支付计划组件
     */
    @Autowired(required = false)
    private BaseFinProductCashPayPlanService baseFinProductCashPayPlanService;

    /**
     * 现金流计算规则组件
     */
    @Autowired(required = false)
    private BaseFinProductCashCalcRuleService baseFinProductCashCalcRuleService;

    /**
     * 现金流计提规则组件
     */
    @Autowired(required = false)
    private BaseFinProductCashCountingRuleService baseFinProductCashCountingRuleService;

    /**
     * 现金流利率规则信息组件
     */
    @Autowired(required = false)
    private BaseFinProductCashRateRuleService baseFinProductCashRateRuleService;

    /**
     * 金融产品现金流行权规则组件
     */
    @Autowired(required = false)
    private BaseFinProductExerciseRuleService baseFinProductExerciseRuleService;

    /**
     * 金融产品现金流行权计划组件
     */
    @Autowired(required = false)
    private BaseFinProductExercisePlanService baseFinProductExercisePlanService;

    /**
     * 开放计划组件
     */
    @Autowired(required = false)
    private BaseFinProductOpenPlanService baseFinProductOpenPlanService;

    /**
     * 开放规则组件
     */
    @Autowired(required = false)
    private BaseFinProductOpenRuleService baseFinProductOpenRuleService;

    /**
     * 债券估值行情保存mapper(新增)
     */
    @Autowired
    private MstBondValuationInfoMapper mstBondValuationInfoMapper;
    /**
     * 债券市场行情保存mapper(新增)
     * author shenchen
     */
    @Autowired
    private MstBondMarketInfoMapper mstBondMarketInfoMapper;
    /**
     * 回购标准折算率保存mapper(新增)
     * author shenchen
     */
    @Autowired
    private FinStdsecConverseRadioMapper finStdsecConverseRadioMapper;
    /**
     * 基金-场外金融产品保存mapper(新增)
     * author shenchen
     */
    @Autowired
    private FinProductValuationMapper finProductValuationMapper;
    /**
     * 基金-基金市场行情保存(新增)
     * author shenchen
     */
    @Autowired
    private MstFundMarketInfoMapper mstFundMarketInfoMapper;
    /**
     * 基金报送信息 (新增)
     */
    @Autowired
    private RepFundMapper repFundMapper;
    /**
     * 中债报送
     */
    @Autowired
    private RepAssetBondMapper repAssetBondMapper;
    @Autowired
    private RepAssetDeptMapper repAssetDeptMapper;

    @Autowired
    private FinProductIssueMapper finProductIssueMapper;
    /**
     * 债券信息评级
     */
    @Autowired
    private MstBondCreditRatingMapper mstBondCreditRatingMapper;
    /**
     * 新建标的类金融产品基本信息
     */
    @Override
    public boolean insert(List<BaseFinProductBaseInfoDto> list) {
        if (list != null && list.size() > 0) {
            // 循环遍历list里的dto
            for (BaseFinProductBaseInfoDto dto : list) {
                // 插入标的类金融产品表
                if (dto.getFinProduct() != null) {
                    // 一体化对应etliu start
                    ValItemValueGenUtil.genFinprodTypeForFinProduct(dto.getFinProduct(), dto.getFinProductType());
                    // 一体化对应etliu end

                    finProductMapper.insert(dto.getFinProduct());
                }
                // 插入标的类金融产品附属表
                if (dto.getFinProductAdd() != null) {
                    finProductAddMapper.insert(dto.getFinProductAdd());
                }
                // 插入金融产品分类表
                if (dto.getFinProductType() != null) {
                    // 一体化对应etliu start
                    ValItemValueGenUtil.genTypeForFinProductType(dto.getFinProduct(), dto.getFinProductType());
                    // 一体化对应etliu end
                    
                    finProductTypeMapper.insert(dto.getFinProductType());
                }
                // 增加推荐方与推荐费率 add by renping 20191014 start
                if (!StringUtil.isEmptyList(dto.getFinProductRecommentList())) {
                    for (FinProductRecomment finProductRecomment : dto.getFinProductRecommentList()) {
                        finProductRecomment.setRecfeeRate(ArithmeticUtil.divide(
                            finProductRecomment.getRecfeeRate(), new BigDecimal(100), 6));
                        finProductRecommentMapper.insert(finProductRecomment);
                    }
                }
                // 增加推荐方与推荐费率 add by renping 20191014 end
                // 调用接口插入金融产品现金流信息表
                if (null != baseFinProductCashInfoService && dto.getFinCashInfoList()!=null && !dto.getFinCashInfoList().isEmpty() ) {
                    baseFinProductCashInfoService.insertList(dto.getFinCashInfoList());
                }
                // 调用接口插入金融产品现金流支付规则表
                if (null != baseFinProductPayRuleService && dto.getFinCashPayRuleList()!=null
                    && !dto.getFinCashPayRuleList().isEmpty() ) {
                    baseFinProductPayRuleService.insert(dto.getFinCashPayRuleList());
                }
                // 调用接口插入金融产品现金流计提规则表
                if (null != baseFinProductCashCountingRuleService && dto.getFinCashCountingRuleList()!=null
                    && !dto.getFinCashCountingRuleList().isEmpty() ) {
                    baseFinProductCashCountingRuleService.insert(dto.getFinCashCountingRuleList());
                }
                // 调用接口插入金融产品现金流支付支付计划表
                if (null != baseFinProductCashPayPlanService && dto.getFinCashPlanList()!=null
                    && !dto.getFinCashPlanList().isEmpty() ) {
                    baseFinProductCashPayPlanService.insertList(dto.getFinCashPlanList());
                }
                // 调用接口插入金融产品现金流计算规则表
                if (null != baseFinProductCashCalcRuleService && dto.getFinCashCalcRuleList()!=null
                    && !dto.getFinCashCalcRuleList().isEmpty() ) {
                    baseFinProductCashCalcRuleService.insert(dto.getFinCashCalcRuleList());
                }
                // 调用接口插入金融产品现金流利率信息表
                if (null != baseFinProductCashRateRuleService && dto.getFinRateInfoList()!=null
                    && !dto.getFinRateInfoList().isEmpty() ) {
                    baseFinProductCashRateRuleService.insert(dto.getFinRateInfoList());
                }
                // 调用接口插入金融产品现金流行权规则表
                if (null != baseFinProductExerciseRuleService && dto.getFinExerciseRuleList()!=null
                    && !dto.getFinExerciseRuleList().isEmpty() ) {
                    baseFinProductExerciseRuleService.insert(dto.getFinExerciseRuleList());
                }
                // 调用接口插入金融产品现金流行权计划表
                if (null != baseFinProductExercisePlanService && dto.getFinExercisePlanList()!=null
                    && !dto.getFinExercisePlanList().isEmpty() ) {
                    baseFinProductExercisePlanService.insert(dto.getFinExercisePlanList());
                }
                // 调用接口插入开放计划表
                if (null != baseFinProductOpenPlanService && dto.getFinOpenPlanList()!=null && !dto.getFinOpenPlanList().isEmpty() ) {
                    baseFinProductOpenPlanService.insert(dto.getFinOpenPlanList());
                }
                // 调用接口插入开放规则表
                if (null != baseFinProductOpenRuleService && dto.getFinOpenRuleList()!=null && !dto.getFinOpenRuleList().isEmpty() ) {
                    baseFinProductOpenRuleService.insert(dto.getFinOpenRuleList());
                }
                //基金报送信息
                if(null!=dto.getRepfund()){
                	repFundMapper.insert(dto.getRepfund());
                }
                //债券中债报送
                if(null!=dto.getRepAssetBond()){
                	repAssetBondMapper.insert(dto.getRepAssetBond());
                }
                //债券中债报送
                if(null!=dto.getRepAssetDept()){
                	repAssetDeptMapper.insert(dto.getRepAssetDept());
                }
                //债券评级信息
                if(null!=dto.getMstBondCreditRatingList() && !dto.getMstBondCreditRatingList().isEmpty()){
                	for(MstBondCreditRating mstBondCreditRating : dto.getMstBondCreditRatingList()){
                		mstBondCreditRatingMapper.insert(mstBondCreditRating);
                	}
                }
                //发行信息
                if(null != dto.getFinProductIssue()){
                    finProductIssueMapper.insert(dto.getFinProductIssue());
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 删除标的类金融产品基本信息
     */
    @Override
    public boolean delete(List<String> finprodIdList) {
        // 判断接口是否为空
        if (finprodIdList != null && finprodIdList.size() > 0) {
            // 遍历金融代码集合
            if (finprodIdList != null && finprodIdList.size() > 0) {
                for (String finprodId : finprodIdList) {
                    // 通过金融代码删除金融产品现金流信息表
                    FinCashInfo finCashInfo = new FinCashInfo();
                    // 先获取现金流ID
                    List<String> cashIdList = new ArrayList<>();
                    if (null != baseFinProductCashInfoService) {
                        finCashInfo.setFinprodId(finprodId);
                        List<FinCashInfo> finCashInfoList = baseFinProductCashInfoService.select(
                            finCashInfo);
                        for (FinCashInfo entity : finCashInfoList) {
                            cashIdList.add(entity.getCashId());
                        }
                        for (String cashId : cashIdList) {
                            finCashInfo.setCashId(cashId);
                            baseFinProductCashInfoService.deleteByPrimaryKey(finCashInfo);
                        }
                    }
                    else {
                        continue;
                    }
                    // 通过金融代码删除标的类金融产品表
                    finProductMapper.deleteByPrimaryKey(finprodId);
                    // 通过金融代码删除标的类金融产品附属表表
                    finProductAddMapper.deleteByPrimaryKey(finprodId);
                    // 通过金融代码删除金融产品分类表
                    FinProductTypeKey key = new FinProductTypeKey();
                    //Long branch =StringUtil.isEmpty(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE))?0:Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
                    Long branch = Long.parseLong("0");
                    key.setFinprodId(finprodId);
                    key.setBranch(branch);
                    finProductTypeMapper.deleteByPrimaryKey(key);

                    // 增加推荐方与推荐费率 add by renping 20191014 start
                    baseFinRecommentMapper.deleteByProductId(finprodId);
                    // 增加推荐方与推荐费率 add by renping 20191014 end

                    // 通过金融代码删除金融产品现金流支付规则表
                    if (null != baseFinProductPayRuleService) {
                        FinCashPayRule finCashPayRule = new FinCashPayRule();
                        finCashPayRule.setFinprodId(finprodId);
                        for (String cashId : cashIdList) {
                            finCashPayRule.setCashId(cashId);
                            baseFinProductPayRuleService.delete(finCashPayRule);
                        }
                    }
                    // 通过金融代码删除金融产品现金流支付计划表
                    if (null != baseFinProductCashPayPlanService) {
                        FinCashPlan finCashPlan = new FinCashPlan();
                        finCashPlan.setFinprodId(finprodId);
                        for (String cashId : cashIdList) {
                            finCashPlan.setCashId(cashId);
                            baseFinProductCashPayPlanService.deleteByPrimaryKey(finCashPlan);
                        }
                    }
                    // 通过金融代码删除金融产品现金流计算规则表
                    if (null != baseFinProductCashCalcRuleService) {
                        BaseFinProductCashCalcRuleDto baseFinProductCashCalcRuleDto = new BaseFinProductCashCalcRuleDto();
                        baseFinProductCashCalcRuleDto.setFinprodId(finprodId);
                        for (String cashId : cashIdList) {
                            baseFinProductCashCalcRuleDto.setCashId(cashId);
                            // modify by jiangpeng on 20210610 工单 joyinzgcpsj20210608005
//                            baseFinProductCashCalcRuleDto.setIsInitial(DictCodeList.是否_是.getValue());
                            baseFinProductCashCalcRuleService.deleteByBaseFinProductCashCalcRuleDto(
                                baseFinProductCashCalcRuleDto);
                        }
                    }
                    // 通过金融代码删除金融产品现金流计提规则表
                    if (null != baseFinProductCashCountingRuleService) {
                        BaseFinProductCashCountingRuleDto baseFinProductCashCountingRuleDto = new BaseFinProductCashCountingRuleDto();
                        baseFinProductCashCountingRuleDto.setFinprodId(finprodId);
                        for (String cashId : cashIdList) {
                            baseFinProductCashCountingRuleDto.setCashId(cashId);
                            baseFinProductCashCountingRuleService.deleteByBaseFinProductCashCountingRuleDto(
                                baseFinProductCashCountingRuleDto);
                        }
                    }
                    // 通过金融代码删除金融产品现金流利率规则信息表
                    if (null != baseFinProductCashRateRuleService) {
                        BaseFinProductCashRateRuleDto baseFinProductCashRateRuleDto = new BaseFinProductCashRateRuleDto();
                        baseFinProductCashRateRuleDto.setFinprodId(finprodId);
                        for (String cashId : cashIdList) {
                            baseFinProductCashRateRuleDto.setCashId(cashId);
                            baseFinProductCashRateRuleService.deleteByBaseFinProductCashRateRuleDto(
                                baseFinProductCashRateRuleDto);
                        }
                    }
                    // 通过金融代码删除金融产品现金流行权规则表
                    if (null != baseFinProductExerciseRuleService) {
                        BaseFinProductExerciseRuleDto baseFinProductExerciseRuleDto = new BaseFinProductExerciseRuleDto();
                        baseFinProductExerciseRuleDto.setFinprodId(finprodId);
                        for (String cashId : cashIdList) {
                            baseFinProductExerciseRuleDto.setCashId(cashId);
                            baseFinProductExerciseRuleService.delete(
                                baseFinProductExerciseRuleDto);
                        }
                    }
                    // 通过金融代码删除金融产品现金流行权计划表
                    if (null != baseFinProductExercisePlanService) {
                        FinExercisePlan finExercisePlan = new FinExercisePlan();
                        finExercisePlan.setFinprodId(finprodId);
                        for (String cashId : cashIdList) {
                            finExercisePlan.setCashId(cashId);
                            baseFinProductExercisePlanService.deleteByKey(finExercisePlan);
                        }
                    }
                    // 通过金融代码删除开放计划表
                    if (null != baseFinProductOpenPlanService) {
                        BaseFinProductOpenPlanDto baseFinProductOpenPlanDto = new BaseFinProductOpenPlanDto();
                        baseFinProductOpenPlanDto.setFinprodId(finprodId);
                        for (String cashId : cashIdList) {
                            baseFinProductOpenPlanDto.setCashId(cashId);
                            baseFinProductOpenPlanService.deleteByFinOpenPlan(
                                baseFinProductOpenPlanDto);
                        }
                    }
                    // 通过现金流代码删除开放规则表
                    if (null != baseFinProductCashInfoService) {
                        BaseFinProductOpenRuleDto baseFinProductOpenRuleDto = new BaseFinProductOpenRuleDto();
                        for (String cashId : cashIdList) {
                            baseFinProductOpenRuleDto.setCashId(cashId);
                            baseFinProductOpenRuleService.delete(baseFinProductOpenRuleDto);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 修改标的类金融产品基本信息
     */
    @Override
    public boolean update(List<String> finprodIdList, List<BaseFinProductBaseInfoDto> list) {
        if (finprodIdList != null && finprodIdList.size() > 0 && list != null && list.size() > 0) {
            if (this.delete(finprodIdList)) {
                return this.insert(list);
            }
        }
        return false;
    }

    /**
     * 查询标的类金融产品基本信息
     */
    @Override
    public List<BaseFinProductBaseInfoDto> select(List<String> finprodIdList) {
        // 判断集合是否为空
        if (finprodIdList != null && finprodIdList.size() > 0) {
            List<BaseFinProductBaseInfoDto> resultList = new ArrayList<BaseFinProductBaseInfoDto>();
            BaseFinProductBaseInfoDto dto = new BaseFinProductBaseInfoDto();
            for (String finprodId : finprodIdList) {
                // 通过金融产品代码查询标的类金融产品表
                dto.setFinProduct(finProductMapper.selectByPrimaryKey(finprodId));
                // 通过金融产品代码查询标的类金融产品附属表
                dto.setFinProductAdd(finProductAddMapper.selectByPrimaryKey(finprodId));
                // 通过金融产品代码查询金融产品分类表
                FinProductTypeKey key = new FinProductTypeKey();
                key.setFinprodId(finprodId);
                key.setBranch((long)0);
                dto.setFinProductType(finProductTypeMapper.selectByPrimaryKey(key));
                // 通过金融产品代码查询金融产品现金流信息表
                if (null != baseFinProductCashInfoService) {
                    FinCashInfo finCashInfo = new FinCashInfo();
                    finCashInfo.setFinprodId(finprodId);
                    dto.setFinCashInfoList(baseFinProductCashInfoService.select(finCashInfo));
                }
                // 通过金融产品代码查询金融产品现金流支付规则表
                if (null != baseFinProductPayRuleService) {
                    FinCashPayRule finCashPayRule = new FinCashPayRule();
                    finCashPayRule.setFinprodId(finprodId);
                    dto.setFinCashPayRuleList(baseFinProductPayRuleService.select(finCashPayRule));
                }
                // 通过金融产品代码查询金融产品现金流支付计划表
                if (null != baseFinProductCashPayPlanService) {
                    FinCashPlan finCashPlan = new FinCashPlan();
                    finCashPlan.setFinprodId(finprodId);
                    dto.setFinCashPlanList(baseFinProductCashPayPlanService.select(finCashPlan));
                }
                // 通过金融产品代码查询金融产品现金流计算规则表
                if (null != baseFinProductCashCalcRuleService) {
                    BaseFinProductCashCalcRuleDto baseFinProductCashCalcRuleDto = new BaseFinProductCashCalcRuleDto();
                    baseFinProductCashCalcRuleDto.setFinprodId(finprodId);
                    dto.setFinCashCalcRuleList(
                        baseFinProductCashCalcRuleService.selectByBaseFinProductCashCalcRuleDto(
                            baseFinProductCashCalcRuleDto));
                }
                // 通过金融产品代码查询金融产品现金流计算规则表
                if (null != baseFinProductCashCountingRuleService) {
                    BaseFinProductCashCountingRuleDto baseFinProductCashCountingRuleDto = new BaseFinProductCashCountingRuleDto();
                    baseFinProductCashCountingRuleDto.setFinprodId(finprodId);
                    dto.setFinCashCountingRuleList(
                        baseFinProductCashCountingRuleService.selectByBaseFinProductCashCountingRuleDto(
                            baseFinProductCashCountingRuleDto));
                }
                // 通过金融产品代码查询金融产品现金流利率规则信息表
                if (null != baseFinProductCashRateRuleService) {
                    BaseFinProductCashRateRuleDto baseFinProductCashRateRuleDto = new BaseFinProductCashRateRuleDto();
                    baseFinProductCashRateRuleDto.setFinprodId(finprodId);
                    dto.setFinRateInfoList(
                        baseFinProductCashRateRuleService.selectByBaseFinProductCashRateRuleDto(
                            baseFinProductCashRateRuleDto));
                }
                // 通过金融产品代码查询金融产品现金流计提规则信息表
                if (null != baseFinProductCashRateRuleService) {
                    BaseFinProductCashRateRuleDto baseFinProductCashRateRuleDto = new BaseFinProductCashRateRuleDto();
                    baseFinProductCashRateRuleDto.setFinprodId(finprodId);
                    dto.setFinRateInfoList(
                        baseFinProductCashRateRuleService.selectByBaseFinProductCashRateRuleDto(
                            baseFinProductCashRateRuleDto));
                }
                // 通过金融产品代码查询金融产品现金流行权规则表
                if (null != baseFinProductExerciseRuleService) {
                    BaseFinProductExerciseRuleDto baseFinProductExerciseRuleDto = new BaseFinProductExerciseRuleDto();
                    baseFinProductExerciseRuleDto.setFinprodId(finprodId);
                    dto.setFinExerciseRuleList(
                        baseFinProductExerciseRuleService.select(baseFinProductExerciseRuleDto));
                }
                // 通过金融产品代码查询金融产品现金流行权计划表
                if (null != baseFinProductExercisePlanService) {
                    FinExercisePlan finExercisePlan = new FinExercisePlan();
                    finExercisePlan.setFinprodId(finprodId);
                    dto.setFinExercisePlanList(
                        baseFinProductExercisePlanService.select(finExercisePlan));
                }
                // 通过金融产品代码查询开放计划表
                if (null != baseFinProductOpenPlanService) {
                    BaseFinProductOpenPlanDto baseFinProductOpenPlanDto = new BaseFinProductOpenPlanDto();
                    baseFinProductOpenPlanDto.setFinprodId(finprodId);
                    dto.setFinOpenPlanList(baseFinProductOpenPlanService.selectByFinOpenPlan(
                        baseFinProductOpenPlanDto));
                }
                // 通过现金流代码查询开发规则表
                if (null != baseFinProductOpenRuleService
                    && null != baseFinProductCashInfoService) {
                    List<FinCashInfo> finCashInfoList = dto.getFinCashInfoList();
                    List<FinOpenRule> finOpenRuleList = new ArrayList<>();
                    if (finCashInfoList != null && finCashInfoList.size() > 0) {
                        BaseFinProductOpenRuleDto baseFinProductOpenRuleDto = new BaseFinProductOpenRuleDto();
                        for (FinCashInfo entity : finCashInfoList) {
                            baseFinProductOpenRuleDto.setCashId(entity.getCashId());
                            List<FinOpenRule> onpenrulelist = baseFinProductOpenRuleService.search(
                                baseFinProductOpenRuleDto);
                            finOpenRuleList.addAll(onpenrulelist);
                        }
                    }
                    dto.setFinOpenRuleList(finOpenRuleList);
                }
                resultList.add(dto);
            }
            return resultList;
        }
        return null;
    }
    
    /**
     * 债券估值行情保存(新增)
     */
	@Override
	public boolean insertMstBondValuationInfo(List<MstBondValuationInfo> list) {
		if (null != baseFinProductCashInfoService && list!=null && !list.isEmpty() ) {
			for(MstBondValuationInfo mstBondValuationInfo:list){
				mstBondValuationInfoMapper.insert(mstBondValuationInfo);
			}
			return true;
		}
		return false;
	}
	/**
     * 债券市场行情保存(新增)
     */
	@Override
	public boolean insertmstBondMarketInfo(List<MstBondMarketInfo> list) {
		if (null != baseFinProductCashInfoService && list!=null && !list.isEmpty() ) {
			for(MstBondMarketInfo mstBondMarketInfo:list){
				mstBondMarketInfoMapper.insert(mstBondMarketInfo);
			}
			return true;
		}
		return false;
	}

	/**
     * 回购标准折算率保存(新增)
     */
	@Override
	public boolean insertFinStdsecConverseRadio(List<FinStdsecConverseRadio> list) {
		if (null != baseFinProductCashInfoService && list!=null && !list.isEmpty() ) {
			for(FinStdsecConverseRadio finStdsecConverseRadio:list){
				finStdsecConverseRadioMapper.insert(finStdsecConverseRadio);
			}
			return true;
		}
		return false;
	}
	/**
     * 基金-场外金融产品保存(新增)
     */
	@Override
	public boolean insertFinProductValuation(List<FinProductValuation> list) {
		if (null != baseFinProductCashInfoService && list!=null && !list.isEmpty() ) {
			for(FinProductValuation finProductValuation:list){
				finProductValuationMapper.insert(finProductValuation);
			}
			return true;
		}
		return false;
	}
	/**
     * 基金-基金市场行情保存(新增)
     */
	@Override
	public boolean insertMstFundMarketInfo(List<MstFundMarketInfo> list) {
		if (null != baseFinProductCashInfoService && list!=null && !list.isEmpty() ) {
			for(MstFundMarketInfo mstFundMarketInfo:list){
				mstFundMarketInfoMapper.insert(mstFundMarketInfo);
			}
			return true;
		}
		return false;
	}
}
