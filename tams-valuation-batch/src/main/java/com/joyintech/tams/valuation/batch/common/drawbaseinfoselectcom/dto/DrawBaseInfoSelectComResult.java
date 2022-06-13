/*
 * 文件名：DrawBaseInfoSelectComResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wudebao
 * 修改时间：2018年5月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.tams.common.base.entity.BokAmoConfig;
import com.joyintech.tams.common.base.entity.BokFinProductValuationPara;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 计提基础信息取得结果DTO 〈功能详细描述〉
 *
 * @author wudebao
 * @version 1.0 2018年5月5日
 * @see DrawBaseInfoSelectComResult
 * @since 1.0
 */
public class DrawBaseInfoSelectComResult extends NeedOperateEntity {

    /**
     * 账套代码
     */
    private String booksetId;

    /**
     * 账务明细代码
     */
    private String bokDetailId;

    /**
     * 业务明细代码
     */
    private String busiId;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 会计场景
     */
    private TreeNode bookSetTreeNode;

    /**
     * 发生日期
     */
    private Date happenDate;

    /**
     * 入账日期
     */
    private Date bookDate;

    /**
     * 计提基础信息list
     */
    private List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTO = new ArrayList<DrawBaseInfoSelectComDTO>();

    /**
     * 业务明细类型（金融产品类型）
     */
    private String busiDetailType;

    /**
     * 分支序号
     */
    private Long branch;

    /**
     * 基础科目性质
     */
    private String baseSubjectNature;

    /**
     * 标的信息
     */
    private TargetFinProduct targetFinProduct;

    /**
     * 还本信息
     */
    private List<TargetCashPlan> targetCashPlanList;

    /**
     * 付息信息
     */
    private List<TargetCashPlan> targetCashPlanIntList;

    /**
     * 标的_现金流计算规则信息
     */
    private List<TargetCashCalcRule> targetCashCalcRuleList;

    /**
     * 标的_现金流支付规则信息
     */
    private List<TargetCashPayRule> targetCashPayRuleList;

    /**
     * 标的_行权计划
     */
    private FinExercisePlan finExercisePlan;

    /**
     * 摊销重算配置
     */
    private List<BokAmoConfig> bokAmoConfigList;

    /**
     * 区间到账应计利息
     */
    private List<InitTrdProductDealDto> intervalArrivalAccountList;

    /**
     * 标的_附加信息
     */
    private FinProductAdd finProductAdd;

    /**
     * 账套金融产品估值参数
     */
    private BokFinProductValuationPara bokFinProductValuationPara;

    /**
     * 标的_现金流计算规则信息-收取(利率互换特殊处理)
     * @author 殷亚
     * @date 2022/4/27 15:36
     * @see
     * @version 1.0
     */
    private List<TargetCashCalcRule> targetCashCalcRuleListR;
    /**
     * 标的_现金流计算规则信息-支付(利率互换特殊处理)
     * @author 殷亚
     * @date 2022/4/27 15:36
     * @see
     * @version 1.0
     */
    private List<TargetCashCalcRule> targetCashCalcRuleListP;


    /* @Author 殷亚
     * @Description 金融类标的信息
     * @Date  2022-3-21 10:45
     * @Version 1.0
     * @Param
     * @Return
     **/
    private List<TradeInfoDto> tradeInfoDtoList;


    /**
     * add by weipeng on 20220429：债券计提区分货币型产品和非货币型产品
     * 估值方案收益类型
     */
    private String schProfitType;


    /**
     * 取得 账套代码
     *
     * @return 账套代码
     */
    public String getBooksetId() {
        return booksetId;
    }

    /**
     * 设置 账套代码
     *
     * @param booksetId 账套代码
     */
    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }

    /**
     * 取得 账务明细代码
     *
     * @return 账务明细代码
     */
    public String getBokDetailId() {
        return bokDetailId;
    }

    /**
     * 设置 账务明细代码
     *
     * @param bokDetailId 账务明细代码
     */
    public void setBokDetailId(String bokDetailId) {
        this.bokDetailId = bokDetailId;
    }

    /**
     * 取得 投资目的
     *
     * @return 投资目的
     */
    public String getInvAim() {
        return invAim;
    }

    /**
     * 设置 投资目的
     *
     * @param invAim 投资目的
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    /**
     * 取得 会计场景
     *
     * @return 会计场景
     */
    public TreeNode getBookSetTreeNode() {
        return bookSetTreeNode;
    }

    /**
     * 设置 会计场景
     *
     * @param bookSetTreeNode 会计场景
     */
    public void setBookSetTreeNode(TreeNode bookSetTreeNode) {
        this.bookSetTreeNode = bookSetTreeNode;
    }

    /**
     * 取得 发生日期
     *
     * @return 发生日期
     */
    public Date getHappenDate() {
        return happenDate;
    }

    /**
     * 设置 发生日期
     *
     * @param happenDate 发生日期
     */
    public void setHappenDate(Date happenDate) {
        this.happenDate = happenDate;
    }

    /**
     * 取得 入账日期
     *
     * @return 入账日期
     */
    public Date getBookDate() {
        return bookDate;
    }

    /**
     * 设置 入账日期
     *
     * @param bookDate 入账日期
     */
    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
    }

    /**
     * 取得 计提基础信息list
     *
     * @return 计提基础信息list
     */
    public List<DrawBaseInfoSelectComDTO> getListDrawBaseInfoSelectComDTO() {
        return listDrawBaseInfoSelectComDTO;
    }

    /**
     * 设置 计提基础信息list
     *
     * @param listDrawBaseInfoSelectComDTO 计提基础信息list
     */
    public void setListDrawBaseInfoSelectComDTO(List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTO) {
        this.listDrawBaseInfoSelectComDTO = listDrawBaseInfoSelectComDTO;
    }

    /**
     * 获取 busiId
     *
     * @return busiId.
     */
    public String getBusiId() {
        return busiId;
    }

    /**
     * 设置 busiId
     *
     * @param busiId busiId
     */
    public void setBusiId(String busiId) {
        this.busiId = busiId;
    }

    /**
     * 获取 busiDetailType
     * @return busiDetailType.
     */
    public String getBusiDetailType() {
        return busiDetailType;
    }

    /**
     * 设置 busiDetailType
     * @param busiDetailType
     */
    public void setBusiDetailType(String busiDetailType) {
        this.busiDetailType = busiDetailType;
    }

    /**
     * 获取 branch
     * @return branch.
     */
    public Long getBranch() {
        return branch;
    }

    /**
     * 设置 branch
     * @param branch
     */
    public void setBranch(Long branch) {
        this.branch = branch;
    }

    /**
     * 获取 baseSubjectNature
     * @return baseSubjectNature.
     */
    public String getBaseSubjectNature() {
        return baseSubjectNature;
    }

    /**
     * 设置 baseSubjectNature
     * @param baseSubjectNature
     */
    public void setBaseSubjectNature(String baseSubjectNature) {
        this.baseSubjectNature = baseSubjectNature;
    }

    public TargetFinProduct getTargetFinProduct() {
        return targetFinProduct;
    }

    public void setTargetFinProduct(TargetFinProduct targetFinProduct) {
        this.targetFinProduct = targetFinProduct;
    }

    public List<TargetCashPlan> getTargetCashPlanList() {
        return targetCashPlanList;
    }

    public void setTargetCashPlanList(List<TargetCashPlan> targetCashPlanList) {
        this.targetCashPlanList = targetCashPlanList;
    }

    public List<TargetCashPlan> getTargetCashPlanIntList() {
        return targetCashPlanIntList;
    }

    public void setTargetCashPlanIntList(List<TargetCashPlan> targetCashPlanIntList) {
        this.targetCashPlanIntList = targetCashPlanIntList;
    }

    public List<TargetCashCalcRule> getTargetCashCalcRuleList() {
        return targetCashCalcRuleList;
    }

    public void setTargetCashCalcRuleList(List<TargetCashCalcRule> targetCashCalcRuleList) {
        this.targetCashCalcRuleList = targetCashCalcRuleList;
    }

    public List<TargetCashPayRule> getTargetCashPayRuleList() {
        return targetCashPayRuleList;
    }

    public void setTargetCashPayRuleList(List<TargetCashPayRule> targetCashPayRuleList) {
        this.targetCashPayRuleList = targetCashPayRuleList;
    }

    public FinExercisePlan getFinExercisePlan() {
        return finExercisePlan;
    }

    public void setFinExercisePlan(FinExercisePlan finExercisePlan) {
        this.finExercisePlan = finExercisePlan;
    }

    public List<BokAmoConfig> getBokAmoConfigList() {
        return bokAmoConfigList;
    }

    public void setBokAmoConfigList(List<BokAmoConfig> bokAmoConfigList) {
        this.bokAmoConfigList = bokAmoConfigList;
    }

    public FinProductAdd getFinProductAdd() {
        return finProductAdd;
    }

    public void setFinProductAdd(FinProductAdd finProductAdd) {
        this.finProductAdd = finProductAdd;
    }

    public BokFinProductValuationPara getBokFinProductValuationPara() {
        return bokFinProductValuationPara;
    }

    public void setBokFinProductValuationPara(BokFinProductValuationPara bokFinProductValuationPara) {
        this.bokFinProductValuationPara = bokFinProductValuationPara;
    }

    public List<InitTrdProductDealDto> getIntervalArrivalAccountList() {
        return intervalArrivalAccountList;
    }

    public void setIntervalArrivalAccountList(List<InitTrdProductDealDto> intervalArrivalAccountList) {
        this.intervalArrivalAccountList = intervalArrivalAccountList;
    }

    public List<TargetCashCalcRule> getTargetCashCalcRuleListR() {
        return targetCashCalcRuleListR;
    }

    public void setTargetCashCalcRuleListR(List<TargetCashCalcRule> targetCashCalcRuleListR) {
        this.targetCashCalcRuleListR = targetCashCalcRuleListR;
    }

    public List<TargetCashCalcRule> getTargetCashCalcRuleListP() {
        return targetCashCalcRuleListP;
    }

    public void setTargetCashCalcRuleListP(List<TargetCashCalcRule> targetCashCalcRuleListP) {
        this.targetCashCalcRuleListP = targetCashCalcRuleListP;
    }

    public List<TradeInfoDto> getTradeInfoDtoList() {
        return tradeInfoDtoList;
    }

    public void setTradeInfoDtoList(List<TradeInfoDto> tradeInfoDtoList) {
        this.tradeInfoDtoList = tradeInfoDtoList;
    }

    public String getSchProfitType() {
        return schProfitType;
    }

    public void setSchProfitType(String schProfitType) {
        this.schProfitType = schProfitType;
    }
}
