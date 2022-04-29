/*
 * 文件名：FundBaseInfoMoneyFund.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：HuangJian
 * 修改时间：2018年6月26日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.baseinfo.irs;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.IrsCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.base.util.InterfHelper;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValIrsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 利率互换标的信息（VAL_IRS_INFO）
 *
 * @author 殷亚
 * @version 1.0
 * @date 2022/4/15 9:45
 * @return
 * @see
 */
@Service
@Scope("prototype")
public class IrsBaseInfoRecService {

    /**
     * TemplateAnalysisForXMLService
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;

    @Autowired
    private InterfCommonService interfCommonService;

    /**
     * IrsCommonResult
     *
     * @param templateSourceFlgDTO TemplateSourceFlgDTO
     * @param acctEntity           TemplateOrderInfoReaderResult
     * @return LendStandardCommonResult
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public IrsCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                   TemplateOrderInfoReaderResult acctEntity) {
        IrsCommonResult result = new IrsCommonResult();
        List<ValIrsInfo> listValIrsInfo = new ArrayList<ValIrsInfo>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValIrsInfo = returnResult.getT(ValIrsInfo.class);

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = this.handleValIrsInfo(listValIrsInfo);
        if (map != null) {
            //交易类金融产品list
            result.setListValTradeFinProduct(
                    (List<FinTradeProduct>) map.get("valTradeFinProduct"));
            //金融产品分类list
            result.setListValFinProductType((List<FinProductType>) map.get("valFinProductType"));
            // 现金流信息List
            List<FinCashInfo> listValCashInfo = (List<FinCashInfo>) map.get("ValCashInfo");
            result.setListValCashInfo(listValCashInfo);
            // 现金流计算规则List
            List<FinCashCalcRule> listValCashCalcRule = (List<FinCashCalcRule>) map.get(
                    "ValCashCalcRule");
            result.setListValCashCalcRule(listValCashCalcRule);
            //现金流支付规则List
            List<FinCashPayRule> listFinCashPayRule = (List<FinCashPayRule>) map.get(
                    "FinCashPayRule");
            result.setListValCashPayRule(listFinCashPayRule);
            //现金流利率信息
            result.setListValCashIntInfo((List<FinRateInfo>) map.get("ValFinCashIntInfo"));

        }

        // 添加日志
        result.setValInterflog(returnResult.getValInterflog());
        return result;

    }

    /**
     * 更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
     * 利率互换标的信息处理：交易类金融产品/金融产品分类/现金流信息/现金流计算规则/现金流支付规则/现金流利率信息
     *
     * @param listValIrsInfo List<ValIrsInfo>
     * @return Map<String, Object>
     * @author 殷亚
     * @date 2022/4/15 14:43
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> handleValIrsInfo(List<ValIrsInfo> listValIrsInfo) {
        Map<String, Object> resultMap = new HashMap<>();
        List<FinTradeProduct> listValTradeFinProduct = new ArrayList<FinTradeProduct>();
        List<FinProductType> listValFinProductType = new ArrayList<FinProductType>();
        List<FinCashInfo> listValCashInfo = new ArrayList<FinCashInfo>();
        List<FinCashCalcRule> listValCashCalcRule = new ArrayList<FinCashCalcRule>();
        List<FinCashPayRule> listFinCashPayRule = new ArrayList<FinCashPayRule>();
        List<FinRateInfo> listFinCashIntInfo = new ArrayList<FinRateInfo>();

        for (ValIrsInfo item : listValIrsInfo) {


            //交易类金融产品
            FinTradeProduct valTradeFinProduct = this.getValTradeFinProduct(item);
            listValTradeFinProduct.add(valTradeFinProduct);

            //金融产品分类
            FinProductType valFinProductType = this.getValFinProductType(item);
            listValFinProductType.add(valFinProductType);

            /************* 现金流信息-收取 ************/
            FinCashInfo valCashInfoR = getValCashInfo(item, DictCodeList.交易方向_收取.getValue());
            listValCashInfo.add(valCashInfoR);
            FinCashCalcRule valCashCalcRuleR = getValCashCalcRule(item, valCashInfoR);

            if (StringUtil.isNotEmpty(valCashCalcRuleR.getCashId())) {
                listValCashCalcRule.add(valCashCalcRuleR);
            }

            FinCashPayRule payRuleR = getValCashPayRule(item, valCashInfoR);
            listFinCashPayRule.add(payRuleR);
            /************* 现金流信息-收取 ************/


            /************* 现金流信息-支付 ************/
            FinCashInfo valCashInfoP = getValCashInfo(item, DictCodeList.交易方向_支付.getValue());
            listValCashInfo.add(valCashInfoP);
            FinCashCalcRule valCashCalcRuleP = getValCashCalcRule(item, valCashInfoP);
            if (StringUtil.isNotEmpty(valCashCalcRuleP.getCashId())) {
                listValCashCalcRule.add(valCashCalcRuleP);
            }
            FinCashPayRule payRuleP = getValCashPayRule(item, valCashInfoP);
            listFinCashPayRule.add(payRuleP);
            /************* 现金流信息-支付 ************/

            listFinCashIntInfo.add(this.getFinCashIntInfo(item, valCashInfoR));
            listFinCashIntInfo.add(this.getFinCashIntInfo(item, valCashInfoP));


        }
        resultMap.put("valTradeFinProduct", listValTradeFinProduct);
        resultMap.put("valFinProductType", listValFinProductType);
        resultMap.put("ValCashInfo", listValCashInfo);
        resultMap.put("ValCashCalcRule", listValCashCalcRule);
        resultMap.put("FinCashPayRule", listFinCashPayRule);
        resultMap.put("ValFinCashIntInfo", listFinCashIntInfo);

        return resultMap;
    }

    /**
     * 交易类金融产品 T1
     *
     * @param dto ValIrsInfo
     * @return ValTradeFinProduct
     * @see
     * @since 1.0
     */
    private FinTradeProduct getValTradeFinProduct(ValIrsInfo dto) {
        FinTradeProduct result = new FinTradeProduct();
        // 金融产品代码 = A.拆借标的代码
        result.setFinprodId(dto.getTradeId());
        // 金融产品类型 = A.业务类型
        result.setFinprodType(DictCodeListExt1.金融产品类型_利率互换.getValue());
        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 分支类型 = 设置全局变量(默认为空)
        result.setBranchType(null);
        // 名义本金 = A.名义本金
        result.setPrin(dto.getPrinAmt());
        // 本金币种 = A.币种
        result.setCcy(dto.getCcy());
        // 起息日 = A.起息日
        result.setVdate(dto.getVdate());
        // 到期日 = A.到期日
        result.setMdate(dto.getMdate());
        // 期限(天) = A.到期日-A.起息日
        result.setTerm(DateUtil.getBetweenDayNumber(dto.getVdate(), dto.getMdate()));
        // 原金融产品代码
        result.setoFinprodId(dto.getTradeId());
        //期限品种 = A.期限品种
        result.setTermType(dto.getTermKind());
        //本金收益特征 = A.本金收益特征
        result.setCapiIncomeFeature(dto.getCapiProfFeature());
        // 所属机构 = A.所属机构
        result.setBelongInstitutionCode(dto.getBelongInstitutionCode());
        // 交易对手
        result.setCounterId(dto.getCounterId());
        //互换方式
        result.setIrsType(dto.getIrsType());
        //交易日历
        result.setCalendarId(dto.getCalendarId());
        return result;

    }

    /**
     * 接收 金融产品分类表 T2
     *
     * @param dto ValIrsInfo
     * @return ValFinProductType
     * @see
     * @since 1.0
     */
    private FinProductType getValFinProductType(ValIrsInfo dto) {
        FinProductType result = new FinProductType();
        // 金融产品代码 = A.拆借标的代码
        result.setFinprodId(dto.getTradeId());
        //分类1 = A.资产类型
        result.setType1("01");
        // 分类2 = 调用共通方法查询核算分类
        FinProductClass fdto = new FinProductClass();
        //金融产品类型
        fdto.setFinprodType(DictCodeListExt1.金融产品类型_利率互换.getValue());
        //资产类型 固定01
        fdto.setAssetType("01");
        // 分类3 = 调用共通方法查询税务分类
        result.setType3(interfCommonService.getFinanceProductTaxClass(fdto));
        // 分类18 = 调用共通方法查询证券品种
        result.setType18(interfCommonService.getFinanceProductSecType(fdto));
        result.setBranch(InterfCommonConstant.分支序号_默认);
        return result;
    }


    /**
     * T3 现金流信息
     *
     * @param dto 01-基础信息
     * @return 现金流信息
     * @see
     * @since 1.0
     */
    private FinCashInfo getValCashInfo(ValIrsInfo dto, String ps) {
        FinCashInfo valCashInfo = new FinCashInfo();
        if (ps.equals(DictCodeList.交易方向_收取.getValue())) {
            // 现金流代码 = A.拆借标的代码+A.业务类型+数据字典：现金流二级类型.利息
            valCashInfo.setCashId(InterfHelper.getMd5CashId_(dto.getTradeId(), DictCodeList.业务类型_利率互换交易.getValue(),
                    DictCodeListExt1.现金流二级类型_收取.getValue()));
            // 现金流一级类型 = 02 利息
            valCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            // 现金流二级类型 = C17 收取
            valCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_收取.getValue());
            if (DictCodeListExt1.利率互换方式_收取固定.getValue().equals(dto.getIrsType())) {
                valCashInfo.setRateType(DictCodeListExt1.计算类型_固定利率.getValue());
            } else {
                valCashInfo.setRateType(DictCodeListExt1.计算类型_浮动利率.getValue());
            }

        } else if (ps.equals(DictCodeList.交易方向_支付.getValue())) {
            // 现金流代码 = A.拆借标的代码+A.业务类型+数据字典：现金流二级类型.本金
            valCashInfo.setCashId(InterfHelper.getMd5CashId_(dto.getTradeId(), DictCodeList.业务类型_利率互换交易.getValue(),
                    DictCodeListExt1.现金流二级类型_支付.getValue()));
            // 现金流一级类型 = 02 利息
            valCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            // 现金流二级类型 = C16 支付
            valCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_支付.getValue());

            if (DictCodeListExt1.利率互换方式_收取浮动.getValue().equals(dto.getIrsType())) {
                valCashInfo.setRateType(DictCodeListExt1.计算类型_固定利率.getValue());
            } else {
                valCashInfo.setRateType(DictCodeListExt1.计算类型_浮动利率.getValue());
            }
        }
        // 金融产品代码 = A.资产代码
        valCashInfo.setFinprodId(dto.getTradeId());
        // 金融产品类型 = A.业务类型
        valCashInfo.setFinprodType(DictCodeListExt1.金融产品类型_利率互换.getValue());
        // 分支序号 = 设置全局变量(默认为0）
        valCashInfo.setBranch(InterfCommonConstant.分支序号_默认);
        return valCashInfo;
    }

    /**
     * 现金流计算规则
     *
     * @param dto 01-基础信息
     * @return 现金流计算规则
     * @see
     * @since 1.0
     */
    private FinCashCalcRule getValCashCalcRule(ValIrsInfo dto, FinCashInfo T3) {

        FinCashCalcRule valCashCalcRule = new FinCashCalcRule();
        valCashCalcRule.setEffDate(dto.getVdate());
        if (DictCodeListExt1.计算类型_固定利率.getValue().equals(T3.getRateType())) {
            // 生效利率 = A.拆借利率
            valCashCalcRule.setYield(dto.getFixRate());
            // 计算基础 = A.计息基础
            valCashCalcRule.setBasis(dto.getFixBasis());
        }else  if (DictCodeListExt1.计算类型_浮动利率.getValue().equals(T3.getRateType())) {
            Map<String,Object> paramMap = new HashMap<String, Object>();
            // 生效利率 = A.拆借利率
            paramMap.put("sysDate",DateUtil.format(dto.getVdate()));
            paramMap.put("intRateId", dto.getLinkRate());
            MstIntRateInfo mstIntRateInfo = interfCommonService.getMstIntRateInfo(paramMap);
            BigDecimal yield = (mstIntRateInfo.getIntRate()==null?BigDecimal.ZERO:mstIntRateInfo.getIntRate())
                    .add(dto.getSpreadRate()==null?BigDecimal.ZERO:dto.getSpreadRate());
            valCashCalcRule.setYield(ArithmeticUtil.divide(yield,new BigDecimal(100)));
            // 计算基础 = A.计息基础
            valCashCalcRule.setBasis(dto.getFloatBasis());
        }
        // 现金流代码 = T3.现金流代码
        valCashCalcRule.setCashId(T3.getCashId());
        // 计算类型 = A.利率类型
        valCashCalcRule.setCalcType(T3.getRateType());
        valCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
        // 金融产品代码 = A.资产代码
        valCashCalcRule.setFinprodId(dto.getTradeId());
        // 金融产品类型 = A.业务类型
        valCashCalcRule.setFinprodType(T3.getFinprodType());
        //基数类型 = 数据字典：基数类型.实收资本
        valCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
        //基数日期类型 = 数据字典：基数日期类型.当日
        valCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
        // 分支序号 = 设置全局变量(默认为0）
        valCashCalcRule.setBranch(InterfCommonConstant.分支序号_默认);
        return valCashCalcRule;

    }


    private FinCashPayRule getValCashPayRule(ValIrsInfo dto, FinCashInfo T3) {
        FinCashPayRule payrule = new FinCashPayRule();
        String cashId = T3.getCashId();
        // 现金流代码
        payrule.setCashId(cashId);
        payrule.setEffDate(dto.getVdate());
        if (DictCodeListExt1.计算类型_固定利率.getValue().equals(T3.getRateType())) {
            // 起息日 = 接口.起息日
            payrule.setFirstPayDate(dto.getFixVdate());
            // 频率
            payrule.setPayCycle(dto.getFixPayCycle());
        }else  if (DictCodeListExt1.计算类型_浮动利率.getValue().equals(T3.getRateType())) {
            // 起息日 = 接口.起息日
            payrule.setFirstPayDate(dto.getFloatVdate());
            // 频率
            payrule.setPayCycle(dto.getFloatPayCycle());
        }

        // 推算方式
        payrule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());
        // 现金流单位
        payrule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码= 接口.拆借标的代码
        payrule.setFinprodId(dto.getTradeId());
        // 金融产品类型 = 接口.业务类型
        payrule.setFinprodType(T3.getFinprodType());
        // 支付方式
        payrule.setPayType(DictCodeList.支付方式_现金.getValue());
        // 分支序号
        payrule.setBranch(InterfCommonConstant.分支序号_默认);
        return payrule;

    }


    /**
     * 现金流利率信息表构建
     *
     * @param dto     接口数据dto
     * @param cashInfo 已经构建的现金流信息
     * @return
     */
    private FinRateInfo getFinCashIntInfo(ValIrsInfo dto, FinCashInfo cashInfo) {
        //现金流利率信息
        FinRateInfo valFinCashIntInfo = new FinRateInfo();
        //现金流代码=MD5(A.资金账户账号+数据字典：金融产品类型.资金账户+数据字典：现金流二级类型.利息)
        valFinCashIntInfo.setCashId(cashInfo.getCashId());
        valFinCashIntInfo.setEffDate(dto.getVdate());
        //固定利率的取固定端
        if (DictCodeListExt1.计算类型_固定利率.getValue().equals(cashInfo.getRateType())) {
            //生效日=A.生效日
            //初始利率=A.初始利率
            valFinCashIntInfo.setRate(dto.getFixRate());
            //计算基础=A.计息基础
            valFinCashIntInfo.setBasis(dto.getFixBasis());
        } else if (DictCodeListExt1.计算类型_浮动利率.getValue().equals(cashInfo.getRateType())) {
            //计算基础=A.计息基础
            valFinCashIntInfo.setBasis(dto.getFloatBasis());
            //系数=A.系数
            valFinCashIntInfo.setCoefficient(BigDecimal.ONE);
            //利差=A.利差
            valFinCashIntInfo.setSpreadRate(dto.getSpreadRate());
            //基准编号=A.基准利率编号
            valFinCashIntInfo.setBenchmarkId(dto.getLinkRate());
            //首期利率重置日
            valFinCashIntInfo.setFirstConfirmDate(dto.getFloatComfimDate());
            //利率重置频率
            valFinCashIntInfo.setResetFreq(dto.getResetCycle());
            //重置类型
            valFinCashIntInfo.setResetType(dto.getResetType());
            //提前观察数量
            valFinCashIntInfo.setObserveBefDay(dto.getObserveBefDay());
            //提前观察单位
            valFinCashIntInfo.setObserveBefUnit(dto.getObserveBefUnit());

        }
        //利率类型=A.利率类型
        valFinCashIntInfo.setIntType(cashInfo.getRateType());
        //金融产品代码=A.资金账户账号
        valFinCashIntInfo.setFinprodId(cashInfo.getFinprodId());
        //金融产品类型=数据字典：金融产品类型.资金账户
        valFinCashIntInfo.setFinprodType(cashInfo.getFinprodType());
        //基准类型
        valFinCashIntInfo.setBenchmarkType(DictCodeListExt1.基准类型_2_利率.getValue());
        //分支序号 = 设置全局变量(默认为0）
        valFinCashIntInfo.setBranch(InterfCommonConstant.分支序号_默认);
        return valFinCashIntInfo;
    }
}
