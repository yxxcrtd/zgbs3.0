/*
 * 文件名：PaActiveRemind001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年8月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.remind.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.DictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.remind.dao.PaActiveRemind001Mapper;
import com.joyintech.tams.invest.remind.dao.PaActiveRemind002Mapper;
import com.joyintech.tams.invest.remind.dto.PaActiveRemind001Dto;
import com.joyintech.tams.invest.remind.dto.PaActiveRemind002Dto;
import com.joyintech.tams.systemtools.util.CommonUtil;
/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author xiexinting
 * @version 1.0 2018年8月21日
 * @see PaActiveRemind001Service
 * @since 1.0
 */
@Service
public class PaActiveRemind001Service extends BaseService<PaActiveRemind001Dto> {

    /**
     * 获取mapper对象
     */
    @Autowired
    private PaActiveRemind001Mapper paActiveRemind001Mapper;
    @Autowired
    private PaActiveRemind002Mapper paActiveRemind002Mapper;
    @Autowired
    private IPlatformDate iPlatformDate;
    /**
     *
     * 查询主动提醒数量汇总
     * <功能详细描述>
     * @param param 查询时间
     * @return result 汇总集合
     * @see
     * @since 1.0
     */
    public Map<String, Object> searchCount(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<String, Object>();
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
        param.put("R3S14", BussinessDictCodeList.提醒类型_小分类_质押式回购存续期.getValue());
        param.put("R3S15", BussinessDictCodeList.提醒类型_小分类_买断式回购存续期.getValue());
        param.put("R3S13", BussinessDictCodeList.提醒类型_小分类_存款类资产存续期.getValue());
        param.put("R4S1", BussinessDictCodeList.提醒类型_小分类_债券行权.getValue());
        param.put("R4S2", BussinessDictCodeList.提醒类型_小分类_债券覆盖.getValue());
        param.put("R9S1", BussinessDictCodeList.提醒类型_小分类_数据修改.getValue());
        param.put("R10S1", BussinessDictCodeList.提醒类型_小分类_债券上市流通操作.getValue());
        param.put("R10S2", BussinessDictCodeList.提醒类型_小分类_资产要素登记修改.getValue());
        param.put("R12S1", BussinessDictCodeList.提醒类型_小分类_标准券额度不足提醒.getValue());
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

        //param.put("newCreate", BussinessDictCodeList.数据状态_新建.getValue());
        param.put("purchase", BussinessDictCodeList.交易方向_申购.getValue());
        param.put("redemption", BussinessDictCodeList.交易方向_赎回.getValue());
        //param.put("redemptionConfirm", BussinessDictCodeList.交易方向_赎回确认.getValue());
        //param.put("nonmonetaryFund", BussinessDictCodeList.基金类资产类型_非货币基金.getValue());
        //param.put("monetaryFund", BussinessDictCodeList.基金类资产类型_货币基金.getValue());
        //param.put("unauthorized", BussinessDictCodeList.行权状态_未行权.getValue());
        //param.put("fund", BussinessDictCodeList.金融产品类型_基金.getValue());
        //param.put("bond", BussinessDictCodeList.金融产品类型_债券.getValue());
        //param.put("unsettlement", BussinessDictCodeList.结算状态_未结算.getValue());
        //param.put("netWorth", BussinessDictCodeList.收益类型_净值.getValue());
        param.put("currency", BussinessDictCodeList.收益类型_货币.getValue());
        //param.put("no", BussinessDictCodeList.是否_否.getValue());
        param.put("midIncome", BussinessDictCodeList.现金流类型_中间收息.getValue());
        param.put("midReturn", BussinessDictCodeList.现金流类型_中间还本.getValue());
        param.put("dueRepayment", BussinessDictCodeList.现金流类型_到期还本.getValue());
        /**
         * add by dingjunfeng start
         */
        param.put("income", BussinessDictCodeList.交易方向_收息.getValue());
        param.put("returnThis", BussinessDictCodeList.交易方向_还本.getValue());
        param.put("maturity", BussinessDictCodeList.交易方向_到期.getValue());
        param.put("ps_T22", BussinessDictCodeList.交易方向_收息到账.getValue());
        param.put("ps_T23", BussinessDictCodeList.交易方向_还本到账.getValue());
        /**
         * add by dingjunfeng  end
         */

        param.put("configurationDays",
                CacheUtil.getSysParam(BussinessConstant.CONFIGURATION_DAYS));
        //add by liupengfei for 多级法人改造 S
        if(MultilevelDataAuthorityUtil.isEnabled()){
            param.put("isEnabled",true);
            //右上角选择框为空时 存放本级机构及有权限的下级机构 选择时 存放所选机构
            List<String> selectedInstitutionList = new ArrayList<>();
            if(StringUtil.isNotEmpty(MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName()))){
                selectedInstitutionList.add(MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName()));
            }
            //当前用户所属机构代码
            String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
            param.put("institutionCode",createInst);
            param.put("selectedInstitutionList",selectedInstitutionList);
        }
        //add by liupengfei for 多级法人改造 E
        // 实时查询
        if (1==1) {
            // 产品
            result.putAll(searchCountR1(param));
            // 315检查
            result.putAll(searchCountR2(param));
            // 回款及存续期确认
            result.putAll(searchCountR3(param));
            // 债券覆盖及行权
            result.putAll(searchCountR4(param));
            // 风险提醒
            result.putAll(searchCountR5(param));
            // 净值披露
            result.putAll(searchCountR6(param));
            // 债券评级变动
            result.putAll(searchCountR7(param));
            //舆情提醒
            result.putAll(searchCountR8(param));
            //公共数据变更
            result.putAll(searchCountR9(param));
            //债券上市流通
            result.putAll(searchCountR10(param));
            //add by chenjiaxing 分销缴款提醒 20210727 start
            //分销缴款提醒
            result.putAll(searchCountR11(param));
            //add by chenjiaxing 分销缴款提醒 20210727 end
            //标准券额度不足提醒
            result.putAll(searchCountR12(param));

        }
        // 查询主动提醒数据表
        else {

        }
        return result;
    }

    /**
     * 舆情提醒(内容还没补写)
     */
    public Map<String, Object> searchCountR8(Map<String, Object> param){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.putAll(this.searchCountR7S3(param));
        return resultMap;
    }
    /**
     * 净值披露
     *
     * @param param
     */
    public Map<String, Object> searchCountR6(Map<String, Object> param) {
        return paActiveRemind001Mapper.searchCountR6S1(param);
    }
    /**
     * 债券评级变动
     *
     * @param param
     */
    public Map<String, Object> searchCountR7(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.putAll(this.searchCountR7S1(param));
        resultMap.putAll(this.searchCountR7S2(param));
        return resultMap;
    }
    /**
     * 债券评级变动
     *
     * @param param
     */
    public Map<String, Object> searchCountR7S1(Map<String, Object> param) {
    	return paActiveRemind001Mapper.searchCountR7S1(param);
    }
    /**
     * 舆情提醒
     */
    public Map<String, Object> searchCountR7S3(Map<String, Object> param) {
//        Map<String, Object> map=new HashMap<>();
//        //获取所有资产信息
//        List<PaActiveRemind002Dto> assetinfo = paActiveRemind001Mapper.searchAssetInfo();
//        param.put("startDate", new SimpleDateFormat("yyyy-MM-dd").format(param.get("quaryDate")));
//        param.put("endDate", new SimpleDateFormat("yyyy-MM-dd").format(param.get("quaryDate")));
//        int i=0;
//        for(PaActiveRemind002Dto par:assetinfo){
//            param.put("finprodid", par.getFinprodid());
//            List<PaActiveRemind002Dto> info = paActiveRemind001Mapper.searchCountR7S3(param);
//            if(!StringUtil.isEmptyList(info)){
//                i++;
//            }
//        }
//        map.put("R7S3", i);
//        return map;
    	
    	// START  modify by wj  20200415 舆情提醒数量 与 详情数量不一致。。
    	Map<String, Object> map=new HashMap<>();

        // 直接获取R7S3的数量
        param.put("startDate", new SimpleDateFormat("yyyy-MM-dd").format(param.get("quaryDate")));
        param.put("endDate", new SimpleDateFormat("yyyy-MM-dd").format(param.get("quaryDate")));
        
        map = paActiveRemind001Mapper.searchCountR7S3new(param);
        
        return map;
    }
    /**
     * 项目类资产
     *
     * @param param
     */
    public Map<String, Object> searchCountR7S2(Map<String, Object> param) {
        return paActiveRemind001Mapper.searchCountR7S2(param);
    }
    public Map<String, Object> searchCountR3S16(Map<String, Object> param) {
        return paActiveRemind001Mapper.searchCountR3S16(param);
    }
    /**
     * 实时询主动提醒数量_产品端
     *
     * @param param
     */
    public Map<String, Object> searchCountR1(Map<String, Object> param) {
        param.put("platDate", iPlatformDate.getSysDate());
        param.put("userId", LoginUserUtil.getUserName());
        //ZGBUG-51 最新净值小于1的显示数量 modify by lwj 2020/3/27 start
        //wangyang1 06-19-20 二级法人改造
        CommonUtil.setInstitutionMapByType_ms(param);  
        List<PaActiveRemind001Dto> res = paActiveRemind002Mapper.selramindR1S7(param);
        //ZGBUG-51 最新净值小于1的显示数量 modify by lwj 2020/3/27 end
//        StringBuffer r1s7 =new StringBuffer();
//        if(StringUtil.isNotEmpty(res)) {
//            for(String re : res) {
//                r1s7.append(re);
//                r1s7.append("; ");
//            }
//        }
        Map<String, Object> r1 = paActiveRemind001Mapper.searchCountR1(param);
//        r1.put(BussinessDictCodeList.提醒类型_小分类_产品的最新净值小于1.getValue(), r1s7);
        /** 
         * ZGBUG-51 最新净值小于1的显示数量 modify by chendijun 2020/3/7
         */
        r1.put(BussinessDictCodeList.提醒类型_小分类_产品的最新净值小于1.getValue(), StringUtil.isEmptyList(res)?0:res.size());
        return r1;
    }

    /**
     * 实时查询主动提醒数量_315检查
     *
     * @param param
     */
    public Map<String, Object> searchCountR2(Map<String, Object> param) {
        return paActiveRemind001Mapper.searchCountR2(param);
    }

    /**
     * 实时查询主动提醒数量_回款及存续期确认
     *
     * @param param
     */
    public Map<String, Object> searchCountR3(Map<String, Object> param) {

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.putAll(this.searchCountR3S5(param));
        resultMap.putAll(this.searchCountR3S6(param));
        resultMap.putAll(this.searchCountR3S8(param));
        resultMap.putAll(this.searchCountR3S14(param));
        resultMap.putAll(this.searchCountR3S15(param));
        resultMap.putAll(this.searchCountR3S13(param));
        resultMap.putAll(this.searchCountR3S16(param));
        param.put("quaryDateStr",DateUtil.format((Date)param.get("quaryDate")));
        resultMap.putAll(paActiveRemind001Mapper.searchCountR3(param));
        return resultMap;
    }

    /**
     * 实时查询主动提醒数量_项目类资产存续期
     *
     * @param param
     */
    public Map<String, Object> searchCountR3S8(Map<String, Object> param) {
        param.put("R3S8", BussinessDictCodeList.提醒类型_小分类_项目类资产存续期.getValue());
        param.put("TRD17", BussinessDictCodeList.共同业务类型_项目类资产存续期交易.getValue());
        param.put("userName", LoginUserUtil.getUserName());
        return paActiveRemind001Mapper.searchCountR3S8(param);
    }



    /**
     * 实时查询主动提醒数量_质押式回购存续期
     *
     * @param param
     */
    public Map<String, Object> searchCountR3S14(Map<String, Object> param) {
        param.put("R3S14", BussinessDictCodeList.提醒类型_小分类_质押式回购存续期.getValue());
        param.put("TRD16", BussinessDictCodeList.共同业务类型_回购存续期交易.getValue());
        param.put("userName", LoginUserUtil.getUserName());
        return paActiveRemind001Mapper.searchCountR3S14(param);
    }

    /**
     * 实时查询主动提醒数量_质押式回购存续期
     *
     * @param param
     */
    public Map<String, Object> searchCountR3S13(Map<String, Object> param) {
        param.put("R3S13", BussinessDictCodeList.提醒类型_小分类_存款类资产存续期.getValue());
        param.put("TRD12", BussinessDictCodeList.共同业务类型_存款类资产存续期交易.getValue());
        param.put("userName", LoginUserUtil.getUserName());
        return paActiveRemind001Mapper.searchCountR3S13(param);
    }

    /**
     * 实时查询主动提醒数量_质押式回购存续期
     *
     * @param param
     */
    public Map<String, Object> searchCountR3S15(Map<String, Object> param) {
        param.put("R3S15", BussinessDictCodeList.提醒类型_小分类_买断式回购存续期.getValue());
        param.put("TRD49", BussinessDictCodeList.共同业务类型_买断式回购存续期交易.getValue());
        param.put("userName", LoginUserUtil.getUserName());
        return paActiveRemind001Mapper.searchCountR3S15(param);
    }

    /**
     * 实时查询主动提醒数量_债券覆盖及行权
     *
     * @param param
     */
    public Map<String, Object> searchCountR4(Map<String, Object> param) {
        return paActiveRemind001Mapper.searchCountR4(param);
    }

    /**
     * 实时查询主动提醒数量_风险提醒
     *
     * @param param
     */
    public Map<String, Object> searchCountR5(Map<String, Object> param) {
//        param.put("R5S1", BussinessDictCodeList.指标状态_预警.getValue());
//        param.put("R5S2", BussinessDictCodeList.指标状态_禁止.getValue());
//        param.put("R5S3",BussinessDictCodeList.指标状态_异常.getValue());
//        //BussinessDictCodeList.指标_状态.getValue();
//        param.put("SUPSTAT", Arrays.asList(new String [] {}));
        CommonUtil.setInstitutionMapByType_ms(param); 
        param.put("R5S1", BussinessDictCodeListRds.监督状态_触发预警.getValue());
        param.put("R5S2", BussinessDictCodeListRds.监督状态_触发超限.getValue());
        param.put("R5S3", BussinessDictCodeListRds.监督状态_异常.getValue());  
        if (StringUtil.isNotEmpty(param.get("institution"))) {
            List<String> institutionList = LoginUserUtil.getTreeInstCode(); 
            param.put("institutionList",institutionList); 
        }
        Map<String, Object> temp = paActiveRemind001Mapper.searchCountR5(param);
        //add by chenjiaxing 20200726 固定值参数更新提醒 start
        Map<String, Object> tempS3 = paActiveRemind001Mapper.searchCountR5S3(param);
        //查看是否具有菜单权限
        boolean isShow = SecurityUtil.isPermitted("RskSupervisionIndicatorParam001_btn_gdz_create");
        if(isShow){
            temp.put("R5S3",tempS3.get("R5S3"));
        }else{
            temp.put("R5S3",BigDecimal.ZERO);
        }
        //add by chenjiaxing 20200726 固定值参数更新提醒 end
        return temp;
    }

    /**
     *  实时查询主动提醒数量---债券存续期
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    private Map<String,Object> searchCountR3S5(Map<String,Object> param){
        param.put("userName", LoginUserUtil.getUserName());
        param.put("POSITION_TYPE03", BussinessDictCodeList.持仓类型_实际.getValue());
        param.put("R3S5", BussinessDictCodeList.提醒类型_小分类_债券存续期.getValue());
        return paActiveRemind001Mapper.searchCountR3S5(param);
    }

    private Map<String, Object> searchCountR3S6(Map<String,Object> param){
        param.put("R3S6", BussinessDictCodeList.提醒类型_小分类_债券存续期回款确认.getValue());
        return paActiveRemind001Mapper.searchCountR3S6(param);
    }

    /**
     * 公共数据变更
     * @param param
     * @return
     */
    public Map<String, Object> searchCountR9(Map<String, Object> param){
        Map<String, Object> resultMap = new HashMap<>();
        if(MultilevelDataAuthorityUtil.isEnabled()){
            resultMap.putAll(this.searchCountR9S1(param));
        }
        else{
            resultMap.put("R9S1",0);
        }
        return resultMap;
    }

    /**
     * 公共数据变更
     * @param param
     * @return
     */
    public Map<String, Object> searchCountR10(Map<String, Object> param){
        Map<String, Object> resultMap = new HashMap<>();
        param.put("quaryDateStr",DateUtil.format((Date)param.get("quaryDate")));
        resultMap.putAll(this.searchCountR10S1(param));
        resultMap.putAll(this.searchCountR10S2(param));
        return resultMap;
    }

    /**
     * 公共数据变更-数据修改
     * @param param
     * @return
     */
    private Map<String, Object> searchCountR9S1(Map<String,Object> param){
        param.put("R9S1", BussinessDictCodeList.提醒类型_小分类_数据修改.getValue());
        return paActiveRemind001Mapper.searchCountR9S1(param);
    }

    //add by chenjiaxing 分销缴款提醒 20210727 start
    /**
     * 实时查询主动提醒数量_分销缴款提醒
     *
     * @param param
     */
    public Map<String, Object> searchCountR11(Map<String, Object> param) {
        param.put("R11S1", BussinessDictCodeList.提醒类型_小分类_分销缴款提醒.getValue());
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
        Map<String, Object> temp = new HashMap<>();
        if(StringUtil.equals(isShow01,"false") && StringUtil.equals(isShow02,"false")){
            temp.put("R11S1", BigDecimal.ZERO);
            return temp;
        }else{
            return paActiveRemind001Mapper.searchCountR11S1(param);
        }
    }


    /**
     * 实时查询标准券额度不足提醒
     *
     * @param param
     */
    public Map<String, Object> searchCountR12(Map<String, Object> param) {
        param.put("R12S1", BussinessDictCodeList.提醒类型_小分类_标准券额度不足提醒.getValue());
        param.put("quaryDateStr",DateUtil.format((Date)param.get("quaryDate")));
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
        Map<String, Object> temp = new HashMap<>();
        if(StringUtil.equals(isShow01,"false") && StringUtil.equals(isShow02,"false")){
            temp.put("R12S1", BigDecimal.ZERO);
            return temp;
        }else{
            return paActiveRemind001Mapper.searchCountR12S1(param);
        }
    }
    //add by chenjiaxing 分销缴款提醒 20210727 end


    /**
     * 债券上市流通操作
     *
     * @param param
     */
    public Map<String, Object> searchCountR10S1(Map<String, Object> param) {
        return paActiveRemind001Mapper.searchCountR10S1(param);
    }

    /**
     * 资产要素登记修改
     *
     * @param param
     */
    public Map<String, Object> searchCountR10S2(Map<String, Object> param) {
        return paActiveRemind001Mapper.searchCountR10S2(param);
    }


    /**
     * 标准券额度不足查询资产代码
     * @param param
     * @return
     */
    public Map<String, Object> searchFinprodId(Map<String, Object> param) {
        return paActiveRemind001Mapper.searchFinprodId(param);
    }
}
