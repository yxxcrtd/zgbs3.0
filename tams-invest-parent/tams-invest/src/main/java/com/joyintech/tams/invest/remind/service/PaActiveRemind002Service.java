/*
 * 文件名：PaActiveRemind002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年8月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.remind.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.SysActivereminderInfoMapper;
import com.joyintech.tams.common.base.entity.SysActivereminderInfo;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.remind.dao.PaActiveRemind002Mapper;
import com.joyintech.tams.invest.remind.dto.PaActiveRemind001Dto;
import com.joyintech.tams.invest.riskredis.quota.rsksupervisionriskanalyze.dto.RskSupervisionRiskAnalyzeDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author xiexinting
 * @version 1.0 2018年8月22日
 * @see PaActiveRemind002Service
 * @since 1.0
 */
@Service
public class PaActiveRemind002Service extends BaseService<PaActiveRemind001Dto> {

    /**
     * 获取mapper对象
     */
    @Autowired
    private PaActiveRemind002Mapper paActiveRemind002Mapper;

    /**
     * {@linkplain}
     */
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    @Autowired
    private IPlatformDate iPlatformDate;
    /**
     *
     * 根据前台传入的查询参数进行判断查询出不同类别的详细信息
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询结果
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    public List<PaActiveRemind001Dto> selRamind(Map<String, Object> param)
             {
        List<PaActiveRemind001Dto> dto = new ArrayList<PaActiveRemind001Dto>();
        //净值披露提醒,债券评级提醒,项目类资产标的修改提醒,债券借贷融出的券还本收息提醒
        param.put("R6S1", BussinessDictCodeList.提醒类型_小分类_净值披露提醒.getValue());
        param.put("R7S1", BussinessDictCodeList.提醒类型_小分类_债项和主体评级变动.getValue());
        param.put("R7S2", BussinessDictCodeList.提醒类型_小分类_项目类资产标的修改提醒.getValue());
        param.put("R7S3", BussinessDictCodeList.提醒类型_小分类_舆情提醒.getValue());
        param.put("R3S16", BussinessDictCodeList.提醒类型_小分类_债券借贷融出的券还本收息.getValue());       
        param.put("R1S1", BussinessDictCodeList.提醒类型_小分类_产品正在募集.getValue());
        param.put("R1S2", BussinessDictCodeList.提醒类型_小分类_产品起息.getValue());
        param.put("R1S3", BussinessDictCodeList.提醒类型_小分类_产品中间付息.getValue());
        param.put("R1S4", BussinessDictCodeList.提醒类型_小分类_子周期强制赎回提醒.getValue());
        param.put("R1S5", BussinessDictCodeList.提醒类型_小分类_产品到期.getValue());
        param.put("R1S6", BussinessDictCodeList.提醒类型_小分类_产品费用支付.getValue());
        param.put("R1S7", BussinessDictCodeList.提醒类型_小分类_产品的最新净值小于1.getValue());
        param.put("R2S1", BussinessDictCodeList.提醒类型_小分类_项目类提前5天.getValue());
        param.put("R2S2", BussinessDictCodeList.提醒类型_小分类_项目类提前1个月.getValue());
        param.put("R2S3", BussinessDictCodeList.提醒类型_小分类_项目类提前3个月.getValue());
        param.put("R2S4", BussinessDictCodeList.提醒类型_小分类_计划类利率型提前5天.getValue());
        param.put("R2S5", BussinessDictCodeList.提醒类型_小分类_计划类利率型提前1个月.getValue());
        param.put("R2S6", BussinessDictCodeList.提醒类型_小分类_计划类利率型提前3个月.getValue());
        param.put("R3S1", BussinessDictCodeList.提醒类型_小分类_基金类净值型回款确认.getValue());
        param.put("R3S2", BussinessDictCodeList.提醒类型_小分类_基金类货币型回款确认.getValue());
        param.put("R3S3", BussinessDictCodeList.提醒类型_小分类_计划类净值型回款确认.getValue());
        param.put("R3S4", BussinessDictCodeList.提醒类型_小分类_货币基金分红提醒.getValue());
        param.put("R3S5", BussinessDictCodeList.提醒类型_小分类_债券存续期.getValue());
        param.put("R3S6", BussinessDictCodeList.提醒类型_小分类_债券存续期回款确认.getValue());
        param.put("R3S7", BussinessDictCodeList.提醒类型_小分类_回购回款确认.getValue());
        param.put("R3S8", BussinessDictCodeList.提醒类型_小分类_项目类资产存续期.getValue());
        param.put("R3S9", BussinessDictCodeList.提醒类型_小分类_项目类资产存续期回款确认.getValue());
        param.put("R3S10", BussinessDictCodeList.提醒类型_小分类_计划类利率型存续期.getValue());
        param.put("R3S11", BussinessDictCodeList.提醒类型_小分类_计划类利率型存续期回款确认.getValue());
        param.put("R3S12", BussinessDictCodeList.提醒类型_小分类_计划类货币型回款确认.getValue());
        param.put("R3S13", BussinessDictCodeList.提醒类型_小分类_存款类资产存续期.getValue());
        param.put("R3S14", BussinessDictCodeList.提醒类型_小分类_质押式回购存续期.getValue());
        param.put("R3S15", BussinessDictCodeList.提醒类型_小分类_买断式回购存续期.getValue());
        param.put("R4S1", BussinessDictCodeList.提醒类型_小分类_债券行权.getValue());
        param.put("R4S2", BussinessDictCodeList.提醒类型_小分类_债券覆盖.getValue());
        param.put("R9S1", BussinessDictCodeList.提醒类型_小分类_数据修改.getValue());
        param.put("noFlag", BussinessDictCodeList.是否_否.getValue());
        param.put("yesFlag", BussinessDictCodeList.是否_是.getValue());
        param.put("tradeStatus_01", BussinessDictCodeList.交易状态_初始.getValue());
        param.put("finprodType_01", BussinessDictCodeList.现金流业务类型_金融产品.getValue());
        param.put("issueStatus_01", BussinessDictCodeList.发行状态_发行成功.getValue());
        param.put("cashType1_02", BussinessDictCodeList.现金流一级类型_利息.getValue());
        param.put("cashType1_03", BussinessDictCodeList.现金流一级类型_费用.getValue());
        param.put("chargeType_01", BussinessDictCodeList.收费类型_计提式费用.getValue());
        param.put("ps_C1", BussinessDictCodeList.交易方向_产品认购.getValue());
        param.put("ps_C2", BussinessDictCodeList.交易方向_产品申购.getValue());
        param.put("ps_C4", BussinessDictCodeList.交易方向_产品赎回.getValue());
        param.put("ps_C5", BussinessDictCodeList.交易方向_产品到期.getValue());
        param.put("ps_C7", BussinessDictCodeList.交易方向_中间付息.getValue());
        param.put("ps_C9", BussinessDictCodeList.交易方向_产品现金分红.getValue());
        param.put("ps_C10", BussinessDictCodeList.交易方向_产品红利转投.getValue());
        param.put("ps_C11", BussinessDictCodeList.交易方向_产品付费.getValue());
        param.put("ps_C17", BussinessDictCodeList.交易方向_产品提前赎回.getValue());
        param.put("ps_T02", BussinessDictCodeList.交易方向_卖出.getValue());
        param.put("ps_T09", BussinessDictCodeList.交易方向_收息.getValue());
        param.put("ps_T08", BussinessDictCodeList.交易方向_还本.getValue());
        param.put("ps_T07", BussinessDictCodeList.交易方向_到期.getValue());
        param.put("ps_T11", BussinessDictCodeList.交易方向_现金分红.getValue());
        param.put("ps_T12", BussinessDictCodeList.交易方向_红利再投.getValue());
        param.put("ps_T19", BussinessDictCodeList.交易方向_赎回确认.getValue());
        param.put("ps_T22", BussinessDictCodeList.交易方向_收息到账.getValue());
        param.put("ps_T23", BussinessDictCodeList.交易方向_还本到账.getValue());
        param.put("ps_T04", BussinessDictCodeList.交易方向_申购.getValue());
        param.put("ps_T24", BussinessDictCodeList.交易方向_赎回到账.getValue());
        param.put("profitType_01", BussinessDictCodeList.收益类型_净值.getValue());
        param.put("profitType_02", BussinessDictCodeList.收益类型_预期收益.getValue());
        param.put("profitType_03", BussinessDictCodeList.收益类型_货币.getValue());
        param.put("finType_F16", BussinessDictCodeList.金融产品类型_投管用_理财产品.getValue());
        param.put("finType_F18", BussinessDictCodeList.金融产品类型_投管用_项目类资产.getValue());
        param.put("finType_F17", BussinessDictCodeList.金融产品类型_投管用_计划类资产.getValue());
        param.put("cashType_01", BussinessDictCodeList.现金流类型_中间收息.getValue());
        param.put("cashType_02", BussinessDictCodeList.现金流类型_中间还本.getValue());
        param.put("cashType_03", BussinessDictCodeList.现金流类型_到期还本.getValue());
        param.put("cashType_04", BussinessDictCodeList.现金流类型_到期收息.getValue());
        param.put("cashType_05", BussinessDictCodeList.现金流类型_费用支付.getValue());
        param.put("bond", BussinessDictCodeList.金融产品类型_债券.getValue());
        param.put("unauthorized", BussinessDictCodeList.行权状态_未行权.getValue());
        param.put("fund", BussinessDictCodeList.金融产品类型_基金.getValue());
        param.put("nonmonetaryFund", BussinessDictCodeList.基金类资产类型_非货币基金.getValue());
        param.put("monetaryFund", BussinessDictCodeList.基金类资产类型_货币基金.getValue());
        param.put("unsettlement", BussinessDictCodeList.结算状态_未结算.getValue());
        param.put("no", BussinessDictCodeList.是否_否.getValue());
        param.put("newCreate", BussinessDictCodeList.数据状态_新建.getValue());
        param.put("redemptionConfirm", BussinessDictCodeList.交易方向_赎回确认.getValue());
        param.put("redemptionAccount", BussinessDictCodeList.交易方向_赎回到账.getValue());
        param.put("sale", BussinessDictCodeList.交易方向_卖出.getValue());
        param.put("netWorth", BussinessDictCodeList.收益类型_净值.getValue());
        param.put("currency", BussinessDictCodeList.收益类型_货币.getValue());
        param.put("dispose_10", BussinessDictCodeList.处理模式_周期型.getValue());
        param.put("dispose_12", BussinessDictCodeList.处理模式_分档周期型.getValue());
        param.put("paycycle", BussinessDictCodeList.收益结转频率_按月结转.getValue());
        param.put("income", BussinessDictCodeList.交易方向_收息.getValue());
        param.put("returnThis", BussinessDictCodeList.交易方向_还本.getValue());
        param.put("maturity", BussinessDictCodeList.交易方向_到期.getValue());
        param.put("platDate", iPlatformDate.getSysDate());
        param.put("R10S1", BussinessDictCodeList.提醒类型_小分类_债券上市流通操作.getValue());
        param.put("R10S2", BussinessDictCodeList.提醒类型_小分类_资产要素登记修改.getValue());
        param.put("R12S1", BussinessDictCodeList.提醒类型_小分类_标准券额度不足提醒.getValue());
        // 多级法人改造 add by caiqun 20200615
        if(MultilevelDataAuthorityUtil.isEnabled()) {
			param.put("isEnabled", true);	// add by liupengfei for 多级法人改造
            //当前用户所属机构代码
            String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
            //右上角选择框为空时 存放本级机构及有权限的下级机构 选择时 存放所选机构
            List<String> resourcesTreeInstCodeList =  LoginUserUtil.getTreeInstCode();
            param.put("institutionCode",createInst);
            param.put("resourcesTreeInstCodeList",resourcesTreeInstCodeList);
            List<String> selectedInstitutionList = new ArrayList<>();
            selectedInstitutionList.add(createInst);
            param.put("selectedInstitutionList",selectedInstitutionList);
        }
        // 多级法人改造 add by caiqun 20200615
        String mainCategory = (String)param.get("mainCategory");
        //判断下拉框大分类
        if (StringUtil.isNotEmpty(mainCategory) && StringUtil.equals(mainCategory, BussinessDictCodeList.提醒类型_大分类_产品端.getValue())) {
            selramindR1(param, dto);
        }
        if (StringUtil.isNotEmpty(mainCategory) && StringUtil.equals(mainCategory, BussinessDictCodeList.提醒类型_大分类_315检查.getValue())){
            selramindR2(param, dto);
        }
        if (StringUtil.isNotEmpty(mainCategory) && StringUtil.equals(mainCategory, BussinessDictCodeList.提醒类型_大分类_回款及存续期确认.getValue())){
            selramindR3(param, dto);
        }
        if (StringUtil.isNotEmpty(mainCategory) && StringUtil.equals(mainCategory, BussinessDictCodeList.提醒类型_大分类_债券覆盖及行权.getValue())){
            selramindR4(param, dto);
        }
        if (StringUtil.isNotEmpty(mainCategory) && StringUtil.equals(mainCategory, BussinessDictCodeList.提醒类型_大分类_风险提醒.getValue())){
            selramindR5(param, dto);
        }
        //净值披露提醒
        if (StringUtil.isNotEmpty(mainCategory) && StringUtil.equals(mainCategory, BussinessDictCodeList.提醒类型_大分类_披露管理.getValue())){
            selramindR6(param, dto);
        }
        //债券评级提醒
        if (StringUtil.isNotEmpty(mainCategory) && StringUtil.equals(mainCategory, BussinessDictCodeList.提醒类型_大分类_变更提醒.getValue())){
            selramindR7(param, dto);
        }
        //公共数据变更提醒
        if (StringUtil.isNotEmpty(mainCategory) && StringUtil.equals(mainCategory, BussinessDictCodeList.提醒类型_大分类_公共数据变更.getValue())){
            selramindR9(param, dto);
        }
        //add by chenjiaxing 分销缴款提醒 20210727 start
        if (StringUtil.isNotEmpty(mainCategory) && StringUtil.equals(mainCategory, BussinessDictCodeList.提醒类型_大分类_分销缴款提醒.getValue())){
            selramindR11(param, dto);
        }
         //标准券额度不足提醒
         if (StringUtil.isNotEmpty(mainCategory) && StringUtil.equals(mainCategory, BussinessDictCodeList.提醒类型_大分类_标准券额度不足提醒.getValue())){
             param.put("quaryDateStr",DateUtil.format((Date)param.get("quaryDate")));
             selramindR12(param, dto);
         }
        //add by chenjiaxing 分销缴款提醒 20210727 end
         //债券上市流通提醒
         if (StringUtil.isNotEmpty(mainCategory) && StringUtil.equals(mainCategory, BussinessDictCodeList.提醒类型_大分类_债券上市流通.getValue())){
             param.put("quaryDateStr",DateUtil.format((Date)param.get("quaryDate")));
             selramindR10(param, dto);
         }
//        param.put("no", BussinessDictCodeList.是否_否.getValue());
//        param.put("newCreate", BussinessDictCodeList.数据状态_新建.getValue());
        param.put("income", BussinessDictCodeList.交易方向_收息.getValue());
        param.put("returnThis", BussinessDictCodeList.交易方向_还本.getValue());
        param.put("maturity", BussinessDictCodeList.交易方向_到期.getValue());
        param.put("purchase", BussinessDictCodeList.交易方向_申购.getValue());
        param.put("redemption", BussinessDictCodeList.交易方向_赎回.getValue());
//        param.put("redemptionConfirm", BussinessDictCodeList.交易方向_赎回确认.getValue());
//        param.put("nonmonetaryFund", BussinessDictCodeList.基金类资产类型_非货币基金.getValue());
//        param.put("monetaryFund", BussinessDictCodeList.基金类资产类型_货币基金.getValue());
//        param.put("unauthorized", BussinessDictCodeList.行权状态_未行权.getValue());
//        param.put("fund", BussinessDictCodeList.金融产品类型_基金.getValue());
        param.put("projectAssets", BussinessDictCodeList.金融产品类型_项目类资产.getValue());
        param.put("plannedAssets", BussinessDictCodeList.金融产品类型_计划类资产.getValue());
//        param.put("unsettlement", BussinessDictCodeList.结算状态_未结算.getValue());
//        param.put("netWorth", BussinessDictCodeList.收益类型_净值.getValue());
//        param.put("currency", BussinessDictCodeList.收益类型_货币.getValue());
        param.put("midIncome", BussinessDictCodeList.现金流类型_中间收息.getValue());
        param.put("midReturn", BussinessDictCodeList.现金流类型_中间还本.getValue());
        param.put("dueRepayment", BussinessDictCodeList.现金流类型_到期还本.getValue());
        param.put("B01", BussinessDictCodeList.提醒类型_大分类_315检查.getValue());
//        param.put("B03", BussinessDictCodeList.提醒类型_大分类_债券行权.getValue());
//        param.put("B04", BussinessDictCodeList.提醒类型_大分类_债券覆盖.getValue());
//        param.put("B02", BussinessDictCodeList.提醒类型_大分类_申赎及存续期确认.getValue());
        param.put("S19", BussinessDictCodeList.提醒类型_小分类_债券存续期.getValue());
//        param.put("S02", BussinessDictCodeList.提醒类型_小分类_债券提前1个月.getValue());
//        param.put("S01", BussinessDictCodeList.提醒类型_小分类_债券提前3个月.getValue());
//        param.put("S03", BussinessDictCodeList.提醒类型_小分类_债券提前5天.getValue());
        param.put("S22", BussinessDictCodeList.提醒类型_小分类_债券行权.getValue());
        param.put("S23", BussinessDictCodeList.提醒类型_小分类_债券覆盖.getValue());
//        param.put("S15", BussinessDictCodeList.提醒类型_小分类_基金类_净值型回款确认.getValue());
//        param.put("S13", BussinessDictCodeList.提醒类型_小分类_基金类_净值型申购确认.getValue());
//        param.put("S14", BussinessDictCodeList.提醒类型_小分类_基金类_净值型赎回确认.getValue());
//        param.put("S10", BussinessDictCodeList.提醒类型_小分类_基金类_货币型申购确认.getValue());
//        param.put("S12", BussinessDictCodeList.提醒类型_小分类_基金类_货币型回款确认.getValue());
//        param.put("S11", BussinessDictCodeList.提醒类型_小分类_基金类_货币型赎回确认.getValue());
//        param.put("S18", BussinessDictCodeList.提醒类型_小分类_计划类_净值型回款确认.getValue());
//        param.put("S16", BussinessDictCodeList.提醒类型_小分类_计划类_净值型申购确认.getValue());
//        param.put("S17", BussinessDictCodeList.提醒类型_小分类_计划类_净值型赎回确认.getValue());
//        param.put("S21", BussinessDictCodeList.提醒类型_小分类_计划类_利率型存续期.getValue());
//        param.put("S08", BussinessDictCodeList.提醒类型_小分类_计划类_利率型提前1个月.getValue());
//        param.put("S07", BussinessDictCodeList.提醒类型_小分类_计划类_利率型提前3个月.getValue());
//        param.put("S09", BussinessDictCodeList.提醒类型_小分类_计划类_利率型提前5天.getValue());
        param.put("S05", BussinessDictCodeList.提醒类型_小分类_项目类提前1个月.getValue());
        param.put("S04", BussinessDictCodeList.提醒类型_小分类_项目类提前3个月.getValue());
        param.put("S06", BussinessDictCodeList.提醒类型_小分类_项目类提前5天.getValue());
        param.put("S20", BussinessDictCodeList.提醒类型_小分类_项目类资产存续期.getValue());
//        param.put("bondThreeMonth", getMessage("PaActiveRemind001.bondThreeMonth"));
//        param.put("bondOneMonth", getMessage("PaActiveRemind001.bondOneMonth"));
//        param.put("bondFiveDay", getMessage("PaActiveRemind001.bondFiveDay"));
//        param.put("projectThreeMonth", getMessage("PaActiveRemind001.projectThreeMonth"));
//        param.put("projectOneMonth", getMessage("PaActiveRemind001.projectOneMonth"));
//        param.put("projectFiveDay", getMessage("PaActiveRemind001.projectFiveDay"));
//        param.put("planThreeMon", getMessage("PaActiveRemind001.planThreeMon"));
//        param.put("planOneMon", getMessage("PaActiveRemind001.planOneMon"));
//        param.put("planFiveDay", getMessage("PaActiveRemind001.planFiveDay"));
//        param.put("currencyPurchaseConfirm", getMessage("PaActiveRemind001.currencyPurchaseConfirm"));
//        param.put("currencyRedemptionConfirm", getMessage("PaActiveRemind001.currencyRedemptionConfirm"));
//        param.put("networthPurchaseConfirm", getMessage("PaActiveRemind001.networthPurchaseConfirm"));
//        param.put("networthRedemptionConfirm", getMessage("PaActiveRemind001.networthRedemptionConfirm"));
//        param.put("PlanPurchaseConfirm", getMessage("PaActiveRemind001.PlanPurchaseConfirm"));
//        param.put("PlanRedemptionConfirm", getMessage("PaActiveRemind001.PlanRedemptionConfirm"));
//        param.put("dueThreeMonth", getMessage("PaActiveRemind001.dueThreeMonth"));
//        param.put("dueOneMonth", getMessage("PaActiveRemind001.dueOneMonth"));
//        param.put("dueFiveDay", getMessage("PaActiveRemind001.dueFiveDay"));
//        param.put("bondExercise", getMessage("PaActiveRemind001.bondExercise"));
//        param.put("currencyPaybackConfirm", getMessage("PaActiveRemind001.currencyPaybackConfirm"));
//        param.put("networthPaybackConfirm", getMessage("PaActiveRemind001.networthPaybackConfirm"));
//        param.put("PlanPaybackConfirm", getMessage("PaActiveRemind001.PlanPaybackConfirm"));
//        param.put("paymentConfirm", getMessage("PaActiveRemind001.paymentConfirm"));
//        param.put("bondCoverage", getMessage("PaActiveRemind001.bondCoverage"));
//        param.put("bondDuration", getMessage("PaActiveRemind001.bondDuration"));
//        param.put("projectAssetDuration", getMessage("PaActiveRemind001.projectAssetDuration"));
//        param.put("planRateDuration", getMessage("PaActiveRemind001.planRateDuration"));
//        param.put("due", getMessage("PaActiveRemind001.due"));
        //判断大分类是否为空
//        if (StringUtil.isNotEmpty(mainCategoryarry)) {
//            for (int i = 0; i < mainCategoryarry.length; i++ ) {
//                //判断大分类的类别并判断小分类是否为空
//                if (StringUtil.equals(mainCategoryarry[i], param.get("B01"))
//                    && StringUtil.isNotEmpty(smallCategoryarry)) {
//                    for (int k = 0; k < smallCategoryarry.length; k++ ) {
//                        //判断小分类的类别
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S01"))) {
//                            dto.addAll(paActiveRemind002Mapper.selBondThreeMon(param));
//                        }
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S02"))) {
//                            dto.addAll(paActiveRemind002Mapper.selBondOneMon(param));
//                        }
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S03"))) {
//                            dto.addAll(paActiveRemind002Mapper.selBondFiveDay(param));
//                        }
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S04"))) {
//                            dto.addAll(paActiveRemind002Mapper.ProjectThreeMon(param));
//                        }
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S05"))) {
//                            dto.addAll(paActiveRemind002Mapper.ProjectOneMon(param));
//                        }
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S06"))) {
//                            dto.addAll(paActiveRemind002Mapper.ProjectFiveDay(param));
//                        }
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S07"))) {
//                            dto.addAll(paActiveRemind002Mapper.PlanThreeMon(param));
//                        }
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S08"))) {
//                            dto.addAll(paActiveRemind002Mapper.PlanOneMon(param));
//                        }
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S09"))) {
//                            dto.addAll(paActiveRemind002Mapper.PlanFiveDay(param));
//                        }
//                    }
//                }
//                else if (StringUtil.equals(mainCategoryarry[i], param.get("B01"))
//                         && !StringUtil.isNotEmpty(smallCategoryarry)) {
//                    dto.addAll(paActiveRemind002Mapper.selBondThreeMon(param));
//                    dto.addAll(paActiveRemind002Mapper.selBondOneMon(param));
//                    dto.addAll(paActiveRemind002Mapper.selBondFiveDay(param));
//                    dto.addAll(paActiveRemind002Mapper.ProjectThreeMon(param));
//                    dto.addAll(paActiveRemind002Mapper.ProjectOneMon(param));
//                    dto.addAll(paActiveRemind002Mapper.ProjectFiveDay(param));
//                    dto.addAll(paActiveRemind002Mapper.PlanThreeMon(param));
//                    dto.addAll(paActiveRemind002Mapper.PlanOneMon(param));
//                    dto.addAll(paActiveRemind002Mapper.PlanFiveDay(param));
//                }
//                //申赎及存续期确认
//                if (StringUtil.equals(mainCategoryarry[i], param.get("B02"))
//                    && StringUtil.isNotEmpty(smallCategoryarry)) {
//                    for (int k = 0; k < smallCategoryarry.length; k++ ) {
////                        if (StringUtil.equals(smallCategoryarry[k], param.get("S10"))) {
////                            dto.addAll(paActiveRemind002Mapper.currencyPurchaseConfirm(param));
////                        }
////                        if (StringUtil.equals(smallCategoryarry[k], param.get("S11"))) {
////                            dto.addAll(paActiveRemind002Mapper.currencyRedemptionConfirm(param));
////                        }
////                        if (StringUtil.equals(smallCategoryarry[k], param.get("S12"))) {
////                            dto.addAll(paActiveRemind002Mapper.currencyPaybackConfirm(param));
////                        }
////                        if (StringUtil.equals(smallCategoryarry[k], param.get("S13"))) {
////                            dto.addAll(paActiveRemind002Mapper.networthPurchaseConfirm(param));
////                        }
////                        if (StringUtil.equals(smallCategoryarry[k], param.get("S14"))) {
////                            dto.addAll(paActiveRemind002Mapper.networthRedemptionConfirm(param));
////                        }
////                        if (StringUtil.equals(smallCategoryarry[k], param.get("S15"))) {
////                            dto.addAll(paActiveRemind002Mapper.networthPaybackConfirm(param));
////                        }
////                        if (StringUtil.equals(smallCategoryarry[k], param.get("S16"))) {
////                            dto.addAll(paActiveRemind002Mapper.PlanPurchaseConfirm(param));
////                        }
////                        if (StringUtil.equals(smallCategoryarry[k], param.get("S17"))) {
////                            dto.addAll(paActiveRemind002Mapper.PlanRedemptionConfirm(param));
////                        }
////                        if (StringUtil.equals(smallCategoryarry[k], param.get("S18"))) {
////                            dto.addAll(paActiveRemind002Mapper.PlanPaybackConfirm(param));
////                        }
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S19"))) {
//                            dto.addAll(paActiveRemind002Mapper.bondDuration(param));
//                        }
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S20"))) {
//                            dto.addAll(paActiveRemind002Mapper.projectAssetDuration(param));
//                        }
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S21"))) {
//                            dto.addAll(paActiveRemind002Mapper.planRateDuration(param));
//                        }
//                    }
//                }
//                else if (StringUtil.equals(mainCategoryarry[i], param.get("B02"))
//                         && !StringUtil.isNotEmpty(smallCategoryarry)) {
////                    dto.addAll(paActiveRemind002Mapper.currencyPurchaseConfirm(param));
////                    dto.addAll(paActiveRemind002Mapper.currencyRedemptionConfirm(param));
////                    dto.addAll(paActiveRemind002Mapper.networthPurchaseConfirm(param));
////                    dto.addAll(paActiveRemind002Mapper.networthRedemptionConfirm(param));
////                    dto.addAll(paActiveRemind002Mapper.PlanPurchaseConfirm(param));
////                    dto.addAll(paActiveRemind002Mapper.PlanRedemptionConfirm(param));
////                    dto.addAll(paActiveRemind002Mapper.currencyPaybackConfirm(param));
////                    dto.addAll(paActiveRemind002Mapper.networthPaybackConfirm(param));
////                    dto.addAll(paActiveRemind002Mapper.PlanPaybackConfirm(param));
//                    dto.addAll(paActiveRemind002Mapper.bondDuration(param));
//                    dto.addAll(paActiveRemind002Mapper.projectAssetDuration(param));
//                    dto.addAll(paActiveRemind002Mapper.planRateDuration(param));
//                }
//                //债券行权
//                if (StringUtil.equals(mainCategoryarry[i], param.get("B03"))
//                    && StringUtil.isNotEmpty(smallCategoryarry)) {
//                    for (int k = 0; k < smallCategoryarry.length; k++ ) {
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S22"))) {
//                            dto.addAll(paActiveRemind002Mapper.bondExercise(param));
//                        }
//                    }
//                }
//                else if (StringUtil.equals(mainCategoryarry[i], param.get("B03"))
//                         && !StringUtil.isNotEmpty(smallCategoryarry)) {
//                    dto.addAll(paActiveRemind002Mapper.bondExercise(param));
//                }
//                //债券覆盖
//                if (StringUtil.equals(mainCategoryarry[i], param.get("B04"))
//                    && StringUtil.isNotEmpty(smallCategoryarry)) {
//                    for (int k = 0; k < smallCategoryarry.length; k++ ) {
//                        if (StringUtil.equals(smallCategoryarry[k], param.get("S23"))) {
//
//                        }
//                    }
//                }
//                else if (StringUtil.equals(mainCategoryarry[i], param.get("B04"))
//                         && !StringUtil.isNotEmpty(smallCategoryarry)) {
//
//                }
//            }
//        }
        boolean flag = true;
        for (PaActiveRemind001Dto paActiveRemind001Dto : dto) {
           if (!StringUtil.isNotEmpty(paActiveRemind001Dto.getReminder())) {
               flag = false; 
          } 
        }
        if (flag) {
            dto.sort((a,b)->{return a.getReminder().compareTo(b.getReminder());});
        } 
        return dto;
    }

    /**
     * 净值披露
     * @param param
     * @param sysRemDayS
     * @param dto
     */
    public void selramindR6(Map<String, Object> param, List<PaActiveRemind001Dto> dto){
        String smallCategory = (String)param.get("smallCategory");
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_净值披露提醒.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR6S1(param));
        }
    }
    
    /**
     * 债券评级提醒
     * @param param
     * @param sysRemDayS
     * @param dto
     */
    public void selramindR7(Map<String, Object> param, List<PaActiveRemind001Dto> dto){
        String smallCategory = (String)param.get("smallCategory");
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_债项和主体评级变动.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR7S1(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_项目类资产标的修改提醒.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR7S2(param));
        }
        //舆情提醒
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_舆情提醒.getValue())) {
            param.put("startDate", new SimpleDateFormat("yyyy-MM-dd").format(param.get("quaryDate")));
            param.put("endDate", new SimpleDateFormat("yyyy-MM-dd").format(param.get("quaryDate")));
            dto.addAll(paActiveRemind002Mapper.selramindR7S3(param));
        }
    }

    /**
     * 公共数据变更提醒
     * @param param
     * @param dto
     */
    public void selramindR9(Map<String, Object> param, List<PaActiveRemind001Dto> dto){
        String smallCategory = (String)param.get("smallCategory");
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_数据修改.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR9S1(param));
        }
    }

    //add by chenjiaxing 分销缴款提醒 20210727 start
    /**
     * 分销缴款提醒
     * @param param
     * @param dto
     */
    public void selramindR11(Map<String, Object> param, List<PaActiveRemind001Dto> dto){
        String smallCategory = (String)param.get("smallCategory");
        param.put("R11S1", BussinessDictCodeList.提醒类型_小分类_分销缴款提醒.getValue());
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_分销缴款提醒.getValue())) {
            boolean isShow010 = SecurityUtil.isPermitted("EtrBondBiddingResultIb001_btn_search");
            boolean isShow020 = SecurityUtil.isPermitted("EtrBondBiddingResultEx001_btn_search");
            String isShow01 = "false";
            String isShow02 = "false";
            if(isShow010){
                isShow01 = "true";
            }
            if(isShow020){
                isShow02 = "true";
            }
            param.put("isShow01", isShow01);
            param.put("isShow02", isShow02);
            if(StringUtil.equals(isShow01,"false") && StringUtil.equals(isShow02,"false")){

            }else{
                dto.addAll(paActiveRemind002Mapper.selramindR11S1(param));
            }
        }
    }
    //add by chenjiaxing 分销缴款提醒 20210727 start


    /**
     * 标准券额度不足提醒
     * @param param
     * @param dto
     */
    public void selramindR12(Map<String, Object> param, List<PaActiveRemind001Dto> dto){
        String smallCategory = (String)param.get("smallCategory");
        param.put("R12S1", BussinessDictCodeList.提醒类型_小分类_标准券额度不足提醒.getValue());
        param.put("userId", LoginUserUtil.getUserName());
        com.joyintech.framework.base.intf.Page page = CacheUtil.getPage("EtrExchangeTradeFjyywzl001");
        String menuId=page.getMenuId();
        List<PaActiveRemind001Dto> list=paActiveRemind002Mapper.selramindR12S1(param);
        list.stream().forEach(item->{
            item.setMenuId(menuId);
        });
        dto.addAll(list);
    }

    /**
     * 产品端主动提醒
     * @param param
     * @param sysRemDayS
     * @param dto
     */
    public void selramindR1(Map<String, Object> param, List<PaActiveRemind001Dto> dto){
        String smallCategory = (String)param.get("smallCategory");
        param.put("userId", LoginUserUtil.getUserName());
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_产品正在募集.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR1S1(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_产品起息.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR1S2(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_产品中间付息.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR1S3(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_子周期强制赎回提醒.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR1S4(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_产品到期.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR1S5(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_产品费用支付.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR1S6(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_产品的最新净值小于1.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR1S7(param));
        }
    }

    /**
     * 315检查主动提醒
     * @param param
     * @param sysRemDayS
     * @param dto
     */
    public void selramindR2(Map<String, Object> param, List<PaActiveRemind001Dto> dto){
        String smallCategory = (String)param.get("smallCategory");
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_项目类提前5天.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR2S1(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_项目类提前1个月.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR2S2(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_项目类提前3个月.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR2S3(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_计划类利率型提前5天.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR2S4(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_计划类利率型提前1个月.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR2S5(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_计划类利率型提前3个月.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR2S6(param));
        }
    }

    /**
     * 回款及存续期确认主动提醒
     * @param param
     * @param sysRemDayS
     * @param dto
     */
    public void selramindR3(Map<String, Object> param, List<PaActiveRemind001Dto> dto){
        param.put("userId", LoginUserUtil.getUserName());
        String smallCategory = (String)param.get("smallCategory");
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_基金类净值型回款确认.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR3S1(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_基金类货币型回款确认.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR3S2(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_计划类净值型回款确认.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR3S3(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_货币基金分红提醒.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR3S4(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_债券存续期.getValue())) {
            this.selramindR3S5(param,dto);
        }
//    	if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_债券存续期回款确认.getValue())) {
//    	    this.selramindR3S6(param, dto);
//    	}
//    	if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_回购回款确认.getValue())) {
//
//    	}
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_项目类资产存续期.getValue())) {
            this.selramindR3S8(param, dto);
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_存款类资产存续期.getValue())) {
            this.selramindR3S13(param, dto);
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_质押式回购存续期.getValue())) {
            this.selramindR3S14(param, dto);
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_买断式回购存续期.getValue())) {
            this.selramindR3S15(param, dto);
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_计划类利率型存续期.getValue())) {
        	param.put("quaryDateStr",DateUtil.format((Date)param.get("quaryDate"))); 
            dto.addAll(paActiveRemind002Mapper.selramindR3S10(param));
        }
//    	if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_计划类利率型存续期回款确认.getValue())) {
//    		dto.addAll(paActiveRemind002Mapper.selramindR3S11(param));
//    	}
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_计划类货币型回款确认.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR3S12(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_债券借贷融出的券还本收息.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR3S16(param));
        }
    }
    /**
     * 主动提醒 回款及存续期确认--债券存续期
     * <功能详细描述>
     * @param param
     * @param dto
     * @see
     * @since 1.0
     */
    private void selramindR3S5(Map<String, Object> param,List<PaActiveRemind001Dto> dto){
        param.put("userName", LoginUserUtil.getUserName());
        param.put("POSITION_TYPE03", BussinessDictCodeList.持仓类型_实际.getValue());
        param.put("R3S5", BussinessDictCodeList.提醒类型_小分类_债券存续期.getValue());
        dto.addAll(paActiveRemind002Mapper.selramindR3S5(param));
    }
    /**
     * 主动提醒 回款及存续期确认-债券存续期回款确认
     * <功能详细描述>
     * @param param
     * @param dto
     * @see
     * @since 1.0
     */
//    private void selramindR3S6(Map<String, Object> param,List<PaActiveRemind001Dto> dto){
//        param.put("R3S6", BussinessDictCodeList.提醒类型_小分类_债券存续期回款确认.getValue());
//        dto.addAll(paActiveRemind002Mapper.selramindR3S6(param));
//    }
    /**
     * 主动提醒 回款及存续期确认-项目类存续期
     * <功能详细描述>
     * @param param
     * @param dto
     * @see
     * @since 1.0
     */
    private void selramindR3S8(Map<String, Object> param,List<PaActiveRemind001Dto> dto){
        param.put("R3S8", BussinessDictCodeList.提醒类型_小分类_项目类资产存续期.getValue());
        param.put("TRD17", BussinessDictCodeList.共同业务类型_项目类资产存续期交易.getValue());
        param.put("POSITION_TYPE03", BussinessDictCodeList.持仓类型_实际.getValue());
        param.put("userName", LoginUserUtil.getUserName());
        dto.addAll(paActiveRemind002Mapper.selramindR3S8(param));
    }

    /**
     * 主动提醒 回款及存续期确认-存款类存续期
     * <功能详细描述>
     * @param param
     * @param dto
     * @see
     * @since 1.0
     */
    private void selramindR3S13(Map<String, Object> param,List<PaActiveRemind001Dto> dto){
        param.put("R3S13", BussinessDictCodeList.提醒类型_小分类_存款类资产存续期.getValue());
        param.put("TRD04", BussinessDictCodeList.共同业务类型_存款类资产投资交易.getValue());
        param.put("POSITION_TYPE03", BussinessDictCodeList.持仓类型_实际.getValue());
        param.put("userName", LoginUserUtil.getUserName());
        dto.addAll(paActiveRemind002Mapper.selramindR3S13(param));
    }

    /**
     * 主动提醒 回款及存续期确认-质押式回购存续期
     * <功能详细描述>
     * @param param
     * @param dto
     * @see
     * @since 1.0
     */
    private void selramindR3S14(Map<String, Object> param,List<PaActiveRemind001Dto> dto){
        param.put("R3S14", BussinessDictCodeList.提醒类型_小分类_质押式回购存续期.getValue());
        param.put("TRD16", BussinessDictCodeList.共同业务类型_回购存续期交易.getValue());
        param.put("POSITION_TYPE03", BussinessDictCodeList.持仓类型_实际.getValue());
        param.put("userName", LoginUserUtil.getUserName());
        dto.addAll(paActiveRemind002Mapper.selramindR3S14(param));
    }

    /**
     * 主动提醒 回款及存续期确认-质押式回购存续期
     * <功能详细描述>
     * @param param
     * @param dto
     * @see
     * @since 1.0
     */
    private void selramindR3S15(Map<String, Object> param,List<PaActiveRemind001Dto> dto){
        param.put("R3S15", BussinessDictCodeList.提醒类型_小分类_买断式回购存续期.getValue());
        param.put("TRD49", BussinessDictCodeList.共同业务类型_买断式回购存续期交易.getValue());
        param.put("POSITION_TYPE03", BussinessDictCodeList.持仓类型_实际.getValue());
        param.put("userName", LoginUserUtil.getUserName());
        dto.addAll(paActiveRemind002Mapper.selramindR3S15(param));
    }

    /**
     * 债券覆盖及行权主动提醒
     * @param param
     * @param sysRemDayS
     * @param dto
     */

    public void selramindR4(Map<String, Object> param, List<PaActiveRemind001Dto> dto){
        String smallCategory = (String)param.get("smallCategory");
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_债券行权.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR4S1(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_债券覆盖.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR4S2(param));
        }
    }

    /**
     * 风险提醒主动提醒
     * @param param
     * @param sysRemDayS
     * @param dto
     */
    public void selramindR5(Map<String, Object> param, List<PaActiveRemind001Dto> dto){
        String smallCategory = (String)param.get("smallCategory");
        //update by chenjiaxing 固定值参数更新提醒 20210726 start
        if(StringUtil.equals(smallCategory,"R5S3")){
            boolean isShow = SecurityUtil.isPermitted("RskSupervisionIndicatorParam001_btn_gdz_create");
            if(isShow){
                dto.addAll(paActiveRemind002Mapper.selramindR5S3(param));
            }else{

            }
        }else{
            List<String> supStatList = new ArrayList<>();
            if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_指标预警提醒.getValue())) {
                supStatList.add(BussinessDictCodeListRds.监督状态_触发预警.getValue());
                param.put("remType",BussinessDictCodeList.提醒类型_小分类_指标预警提醒.getValue());
            }
            if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_指标违规提醒.getValue())) {
                supStatList.add(BussinessDictCodeListRds.监督状态_触发超限.getValue());
                supStatList.add(BussinessDictCodeListRds.监督状态_异常.getValue());
                param.put("remType",BussinessDictCodeList.提醒类型_小分类_指标违规提醒.getValue());
            }
            param.put("PORTFOLIO_LEVEL_01", BussinessDictCodeListRds.组合层次_单组合.getValue());
            param.put("PORTFOLIO_LEVEL_02", BussinessDictCodeListRds.组合层次_组合包.getValue());
            param.put("supStatList",supStatList);
            param.put("starSupDate", param.get("quaryDate"));
            param.put("endSupDate", param.get("quaryDate"));
            dto.addAll(paActiveRemind002Mapper.selramindR5(param));
        }
        //update by chenjiaxing 固定值参数更新提醒 20210726 end
    }

    @Autowired
    private SysActivereminderInfoMapper sysActivereminderInfoMapper;

    /**
     * 关闭主动提醒插入主动提醒信息表
     * @param param
     * @return
     */
    public int closeRemind(Map<String, Object> param) {
        SysActivereminderInfo sysActivereminderInfo = new SysActivereminderInfo();
        sysActivereminderInfo.setSeqno(IDGenerator.generate());
        sysActivereminderInfo.setCdate(DateUtil.parse((String)param.get("cdate")));
        sysActivereminderInfo.setRemType((String)param.get("remType"));
        sysActivereminderInfo.setFinprodId((String)param.get("finprodId"));
        sysActivereminderInfo.setFinprodName((String)param.get("finprodName"));
        sysActivereminderInfo.setRemark((String)param.get("remark"));
        sysActivereminderInfo.setFeeId((String)param.get("feeId"));
        sysActivereminderInfo.setMdate(DateUtil.parse((String)param.get("mdate")));
        sysActivereminderInfo.setEffflag(BussinessDictCodeList.是否_否.getValue());
        sysActivereminderInfo.setCloseflag("0");
        if(BussinessDictCodeList.提醒类型_小分类_标准券额度不足提醒.getValue().equals((String)param.get("remType"))){
            sysActivereminderInfo.setBusinessPrimaryKey1((String)param.get("tradePlace"));
        }
        //当前用户所属机构代码
        String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
        sysActivereminderInfo.setInstitutionCode(createInst);
        return sysActivereminderInfoMapper.insert(sysActivereminderInfo);
    }

    /**
     * 债券上市流通提醒
     * @param param
     * @param dto
     */
    public void selramindR10(Map<String, Object> param, List<PaActiveRemind001Dto> dto){
        String smallCategory = (String)param.get("smallCategory");
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_债券上市流通操作.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR10S1(param));
        }
        if(StringUtil.isEmpty(smallCategory) || StringUtil.equals(smallCategory, BussinessDictCodeList.提醒类型_小分类_资产要素登记修改.getValue())) {
            dto.addAll(paActiveRemind002Mapper.selramindR10S2(param));
        }
    }
}
