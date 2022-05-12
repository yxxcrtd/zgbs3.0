/*
 * 文件名：FinProductBuild002Validator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2018年10月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.product.productelement.netvalueproduct.validator;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.intf.Page;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.EncryptUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.prod.dto.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.asset.util.AssetCommonUtil;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.service.InvDataCheckService;
import com.joyintech.tams.invest.product.productelement.anaplerotic.dto.FinProductAnapleroticInfoDto;
import com.joyintech.tams.invest.product.productelement.netvalueproduct.dto.FinNetValueProductBuild002Dto;
import com.joyintech.tams.invest.product.productelement.netvalueproduct.service.FinNetValueProductBuild001Service;
import com.joyintech.tams.invest.product.productelement.netvalueproduct.service.FinNetValueProductBuild002Service;
import com.joyintech.tams.invest.product.productelement.scheduling.service.FinProductScheduling002Service;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 产品新建管理验证类
 *
 * @author zhangliang
 * @version 1.0 2018年10月18日
 * @see FinNetValueProductBuild002Validator
 * @since 1.0
 */
@Component
public class FinNetValueProductBuild002Validator implements BeanValidator {

    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductScheduling002Service finProductScheduling002Service;

    /**
     * {@linkplain}
     */
    @Autowired
    private FinNetValueProductBuild002Service finNetValueProductBuild002Service;

    /**
     * {@linkplain}
     */
    @Autowired
    private FinNetValueProductBuild001Service finNetValueProductBuild001Service;

    /**
     * {@linkplain}
     */
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired
    private InvDataCheckService chbInvDataCheckService;

    /**
     * {@linkplain}
     */

    /**
     * {@link MstWorkflowCommonService}
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

//    @Autowired
//    private PrdSaleParamMapper prdSaleParamMapper;
    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        FinNetValueProductBuild002Dto entity = (FinNetValueProductBuild002Dto)value;
        List<ValidMessage> validList = new ArrayList<ValidMessage>();

        // 信息调整流程中的check
        if (StringUtil.isNotEmpty(entity.getFinprodId())){
            finNetValueProductBuild001Service.wflInFlowCheck(entity.getWorkflowId(), entity.getFinprodId(), validList);
        }

        if (StringUtil.isNotEmpty(entity.getFinprodMarketId())) {
            // 规则：代码(1~3位，第1位为大写字母，第2~3位为数字或大写字母)+销售渠道代码(1位大写字母)+品牌代码(1位大写字母)+年份(2位数字)+产品序号(4位数）
//            Pattern pattern = Pattern.compile("^[A-Za-z]{1}[A-Za-z0-9]{0,2}[A-Za-z]{2}[0-9]{6}$");
//            Matcher matcher = pattern.matcher(entity.getFinprodMarketId());
//            if (!matcher.matches()) {
//                validList.add(new ValidMessage("finprodMarketId", getMessage("BUSS_COM_E_0044",
//                    getMessage("FinProductBuild002.finprodMarketId"))));
//            }
            //OPEX-598 产品代码输入类似字符￥%￥%￥，系统提示产品代码已存在，提示有误 guyunfei start
            String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
            Pattern pattern = Pattern.compile(regEx);
            Matcher m = pattern.matcher(entity.getFinprodMarketId());
            if(m.find()){
                validList.add(new ValidMessage("finprodMarketId","产品代码不应包含特殊字符"));
            }
            //OPEX-598 产品代码输入类似字符￥%￥%￥，系统提示产品代码已存在，提示有误 guyunfei end
            // 产品代码不能重复
            if (finProductScheduling002Service.checkProdInfoExist(entity.getFinprodMarketId(),
                null, null, entity.getFinprodId()) > 0) {
                validList.add(new ValidMessage("finprodMarketId", getMessage("BUSS_COM_I_0017",
                    getMessage("FinProductBuild002.finprodMarketId"))));
            }

            // 流程中的市场代码(排期和产品新建)
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            List<String> pageIdList = new ArrayList<String>();
            //pageIdList.add("FinProductScheduling002");//产品新建时若代码和排期重复，以新建为准，查询不生成待处理排期数据
            pageIdList.add("FinNetValueProductBuild002");
            pageIdList.add("FinNetValueProductBuild003");
            wflValidInputParameter.setPageIdList(pageIdList);
            wflValidInputParameter.setWorkflowId(entity.getWorkflowId());
            wflValidInputParameter.setBusinessDataPk2(entity.getFinprodMarketId());
            if (mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
                validList.add(new ValidMessage("finprodMarketId",
                    getMessage("BUSS_COM_I_0017", getMessage("FinProductScheduling002.prodId"))));
            }
        }
        // 产品名称不能重复
        if (StringUtil.isNotEmpty(entity.getFinprodName())) {
            if (finProductScheduling002Service.checkProdInfoExist(null, null,
                entity.getFinprodName(), entity.getFinprodId()) > 0) {
                validList.add(new ValidMessage("finprodName",
                    getMessage("BUSS_COM_I_0017", getMessage("FinProductBuild002.finprodName"))));
            }else
            // 产品代码不能与组合代码重复
            if (finProductScheduling002Service.chekPortfolExist(entity.getFinprodMarketId()) > 0) {
                validList.add(new ValidMessage("finprodMarketId", getMessage("BUSS_COM_I_0017",
                        "组合代码")));
            }

            // 流程中的市场代码(排期和产品新建)
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            List<String> pageIdList = new ArrayList<String>();
            pageIdList.add("FinProductScheduling002");
            pageIdList.add("FinNetValueProductBuild002");
            wflValidInputParameter.setPageIdList(pageIdList);
            wflValidInputParameter.setWorkflowId(entity.getWorkflowId());
            wflValidInputParameter.setBusinessDataPk3(
                EncryptUtil.getInstance().md5(entity.getFinprodName()));
            if (mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
                validList.add(new ValidMessage("finprodName", getMessage("BUSS_COM_I_0017",
                    getMessage("FinProductScheduling002.prodName"))));
            }
        }
        // 产品简称不能重复
        if (StringUtil.isNotEmpty(entity.getFinprodAbbr())) {
            if (finProductScheduling002Service.checkProdInfoExist(null, entity.getFinprodAbbr(),
                null, entity.getFinprodId()) > 0) {
                validList.add(new ValidMessage("finprodAbbr",
                    getMessage("BUSS_COM_I_0017", getMessage("FinProductBuild002.finprodAbbr"))));
            }

            // 流程中的市场代码(排期和产品新建)
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            List<String> pageIdList = new ArrayList<String>();
            pageIdList.add("FinProductScheduling002");
            pageIdList.add("FinNetValueProductBuild002");
            wflValidInputParameter.setPageIdList(pageIdList);
            wflValidInputParameter.setWorkflowId(entity.getWorkflowId());
            wflValidInputParameter.setBusinessDataPk4(
                EncryptUtil.getInstance().md5(entity.getFinprodAbbr()));
            if (mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
                validList.add(new ValidMessage("finprodAbbr", getMessage("BUSS_COM_I_0017",
                    getMessage("FinProductScheduling002.prodAbbr"))));
            }
        }

        //是否现金管理类产品 必填 开放式产品必输
        if (!StringUtil.isNotEmpty(entity.getIsCashManage())  && StringUtil.equals(entity.getOperationType(), DictCodeList.开放类型_开放式.getValue())) {
            validList.add(new ValidMessage("isCashManage", this.getMessage("BUSS_COM_E_0038",
                this.getMessage("FinProductBuild002.isCashManage"))));
        }

        if ((StringUtil.isNotEmpty(entity.getFirstSaleVdate())
             || StringUtil.isNotEmpty(entity.getFirstSaleMdate()))
            && StringUtil.isNotEmpty(entity.getFinprodId())) {
//            PrdSaleParam prdSaleParam = prdSaleParamMapper.selectByPrimaryKey(entity.getFinprodId());
            //需求中没有需要校验募集开始日与上次募集开始日的校验 删除校验 yjm start 20190725
           /* if (prdSaleParam != null) {
                if (DateUtil.compareDate(entity.getFirstSaleVdate(),
                    prdSaleParam.getFirstSaleVdate()) < 0) {
                    validList.add(new ValidMessage("firstSaleVdate", getMessage("FIN_PROD_E_0074",
                        getMessage("FinProductBuild002.firstSaleVdate"))));
                }
//                if (DateUtil.compareDate(entity.getFirstSaleMdate(),
//                    prdSaleParam.getFirstSaleMdate()) > 0) {
//                    validList.add(new ValidMessage("firstSaleMdate", getMessage("FIN_PROD_E_0075",
//                        getMessage("FinProductBuild002.firstSaleMdate"))));
//                }
            }*/
            //需求中没有需要校验募集开始日与上次募集开始日的校验 删除校验 yjm end 20190725
        }
        // 画面.募集开始日>画面.募集结束日
        if (StringUtil.isNotEmpty(entity.getFirstSaleVdate())
            && StringUtil.isNotEmpty(entity.getFirstSaleMdate())) {
            if (DateUtil.compareDate(entity.getFirstSaleVdate(), entity.getFirstSaleMdate()) > 0) {
                validList.add(new ValidMessage("firstSaleVdate",
                    getMessage("BUSS_COM_E_0003", getMessage("FinProductBuild002.firstSaleVdate"),
                        getMessage("FinProductBuild002.firstSaleMdate"))));
            }
        }
        // 画面.募集结束日>画面.产品起息日
        if (StringUtil.isNotEmpty(entity.getFirstSaleMdate())
            && StringUtil.isNotEmpty(entity.getVdate())) {
            if (DateUtil.compareDate(entity.getFirstSaleMdate(), entity.getVdate()) > 0) {
                validList.add(new ValidMessage("firstSaleMdate",
                    getMessage("BUSS_COM_E_0003", getMessage("FinProductBuild002.firstSaleMdate"),
                        getMessage("FinProductBuild002.vdate"))));
            }
        }
        // 画面.产品起息日>=画面.产品到期日
        if (StringUtil.isNotEmpty(entity.getVdate()) && StringUtil.isNotEmpty(entity.getMdate())) {
            if (DateUtil.compareDate(entity.getVdate(), entity.getMdate()) >= 0) {
                validList.add(new ValidMessage("vdate",
                    getMessage("BUSS_COM_E_0045", getMessage("FinProductBuild002.vdate"),
                        getMessage("FinProductBuild002.mdate"))));
            }
        }

        // "当画面.组合代码输入时根据画面.组合代码查询组合成立日和到期日 画面.产品起息日<组合开始日时 画面.产品到期日>组合结束日时"
        if (StringUtil.isNotEmpty(entity.getPtlId())) {
            PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(entity.getPtlId());
            if (ptlPortfolio != null) {
                if (StringUtil.isNotEmpty(ptlPortfolio.getVdate())
                    && StringUtil.isNotEmpty(entity.getVdate())) {
                    if (DateUtil.compareDate(entity.getVdate(), ptlPortfolio.getVdate()) < 0) {
                        validList.add(new ValidMessage("vdate", this.getMessage("BUSS_COM_E_0004",
                            this.getMessage("FinProductBuild002.vdate"),
                            this.getMessage("FinProductBuild002.portfolioVdate") + DateUtil.format(
                                ptlPortfolio.getVdate()))));
                    }
                }
                //update zhangweiqi SHBS-1457  2019-3-28 begin
                /*
                if (StringUtil.isNotEmpty(ptlPortfolio.getMdate())
                    && StringUtil.isNotEmpty(entity.getMdate())) {
                    if (DateUtil.compareDate(entity.getMdate(), ptlPortfolio.getMdate()) > 0) {
                        validList.add(new ValidMessage("mdate", this.getMessage("BUSS_COM_E_0003",
                            this.getMessage("FinProductBuild002.mdate"),
                            this.getMessage("FinProductBuild002.portfolioMdate") + DateUtil.format(
                                ptlPortfolio.getMdate()))));
                    }
                }*/
                //update zhangweiqi SHBS-1457  2019-3-28 end
            }
        }

        // 当画面.收益类型 = 数据字典缓存.收益类型.枚举值.预期收益， 画面.利率类型未填
        if (StringUtil.isNotEmpty(entity.getProfitType()) && StringUtil.equals(
            entity.getProfitType(), DictCodeList.收益类型_收益型.getValue())) {
            if (!StringUtil.isNotEmpty(entity.getCalcType())) {
                validList.add(new ValidMessage("calcType",
                    this.getMessage("BUSS_COM_E_0008",
                        this.getMessage("FinProductBuild002.profitType"),
                        this.getMessage("FinProductBuild002.msg.profitTypeE"),
                        this.getMessage("FinProductBuild002.calcType"))));
            }
        }
        // 当画面.是否分层 = 数据字典缓存.是否.枚举值.是， 画面.分层类型未填
//        if (StringUtil.isNotEmpty(entity.getIsLay())
//            && StringUtil.equals(entity.getIsLay(), DictCodeList.是否_是.getValue())) {
//            if (!StringUtil.isNotEmpty(entity.getLayType())) {
//                validList.add(new ValidMessage("layType",
//                    this.getMessage("BUSS_COM_E_0008", this.getMessage("FinProductBuild002.isLay"),
//                        DictCodeList.是否_是.getCodeName(),
//                        this.getMessage("FinProductBuild002.layType"))));
//            }
//        }

        //update zhangweiqi 开放式校验
        if (StringUtil.isNotEmpty(entity.getBussFinProductOpenPlanDto()) &&
        		!"02".equals(entity.getBussFinProductOpenPlanDto().getOpenMode())&&//add by zlp 20190314 高频开放不校验确认天数
        		StringUtil.isNotEmpty(entity.getOperationType()) && StringUtil.equals(
        		entity.getOperationType(), DictCodeList.开放类型_开放式.getValue())) {
        	if (!StringUtil.isNotEmpty(entity.getPurSpeed())) {
                validList.add(new ValidMessage("purSpeed",
                    this.getMessage("BUSS_COM_E_0008", this.getMessage("FinProductBuild002.purSpeed"),
                        DictCodeList.是否_是.getCodeName(),
                        this.getMessage("FinProductBuild002.purSpeed"))));
            }
        }
      //update zhangweiqi SHBS-1457  begin
        // 当画面.发行方式 = 数据字典缓存.发行方式.枚举值.单一 ， 画面.分行未填
        /*
        if (StringUtil.isNotEmpty(entity.getEntrustType()) && StringUtil.equals(
            entity.getEntrustType(), DictCodeList.排期发行方式_单一.getValue())) {
            if (!StringUtil.isNotEmpty(entity.getEntruster())) {
                validList.add(new ValidMessage("entruster",
                    this.getMessage("BUSS_COM_E_0008",
                        this.getMessage("FinProductBuild002.entrustType"),
                        DictCodeList.排期发行方式_单一.getCodeName(),
                        this.getMessage("FinProductBuild002.entruster"))));
            }
        }*///update zhangweiqi SHBS-1457  end
        // 当画面.收益类型 = 数据字典缓存.收益类型.枚举值.收益型 且 画面.是否分层 = 数据字典缓存.是否.枚举值.否 时 ， 画面.预期收益率未填
        if (StringUtil.isNotEmpty(entity.getProfitType())
            && StringUtil.equals(entity.getProfitType(),
                DictCodeList.收益类型_收益型.getValue())
            && StringUtil.isNotEmpty(entity.getIsLay())
            && StringUtil.equals(entity.getIsLay(), DictCodeList.是否_否.getValue())) {
            if (!StringUtil.isNotEmpty(entity.getYield())) {
                validList.add(new ValidMessage("yield", this.getMessage("BUSS_COM_E_0038",
                    this.getMessage("FinProductBuild002.yield"))));
            }
        }
        // 当画面.收益类型 = 数据字典缓存.收益类型.枚举值.净值型 OR 画面.收益类型 = 数据字典缓存.收益类型.枚举值.货币型 且 画面.是否周期型 =
        // 数据字典缓存.是否.枚举值.是 时，封闭周期（天）未填
        /*
        if (StringUtil.isNotEmpty(entity.getProfitType())
            && (StringUtil.equals(entity.getProfitType(), DictCodeList.收益类型_净值型.getValue())
                || StringUtil.equals(entity.getProfitType(),
                    DictCodeList.收益类型_货币型.getValue()))) {
            if (StringUtil.isNotEmpty(entity.getIsCycle())
                && StringUtil.equals(entity.getIsCycle(), DictCodeList.是否_是.getValue())) {
                if (!StringUtil.isNotEmpty(entity.getCloseDays())) {
                    validList.add(
                        new ValidMessage("closeDays", this.getMessage("FIN_PROD_E_0051")));
                }
            }
        }
*/
        // 当处理模式为普通N天或者滚动N天时封闭期限必填
        if (StringUtil.isNotEmpty(entity.getDealMode())
            && (StringUtil.equals(entity.getDealMode(), DictCodeList.处理模式_周期型.getValue())
                || StringUtil.equals(entity.getDealMode(),
                    DictCodeList.处理模式_分档周期型.getValue()))) {
            if (!StringUtil.isNotEmpty(entity.getInvestmentCycle())
            		&&StringUtil.equals(DictCodeListExt2.期限类型_单一固定期限.getValue(), entity.getPeriod())) {
                validList.add(new ValidMessage("investmentCycle", this.getMessage("BUSS_COM_E_0038",
                    this.getMessage("FinProductBuild002.investmentCycle"))));
            }
            if (!StringUtil.isNotEmpty(entity.getContinueFlag())
            		&& (StringUtil.equals(DictCodeListExt2.期限类型_单一固定期限.getValue(), entity.getPeriod())
            				|| StringUtil.equals(DictCodeListExt2.期限类型_多固定期限.getValue(), entity.getPeriod()))) {
            	validList.add(new ValidMessage("continueFlag", this.getMessage("BUSS_COM_E_0038",
                        this.getMessage("FinProductBuild002.continueFlag"))));
            }
        }

        if (!StringUtil.isNotEmpty(entity.getLiquidationDate())) {
            validList.add(new ValidMessage("liquidationDate", this.getMessage("BUSS_COM_E_0038",
                    this.getMessage("FinProductBuild002.liquidationDate"))));
        }
        
        // 付费计划业务组件
        if (entity.getBussFinProductFeeInfoDto() != null && !StringUtil.isEmptyList(entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList()))  {
            for (BussFinProductFeeInfoDto bussFinProductFeeInfoDto : entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList()) {
                //不规则费用非必填
                if(StringUtil.equals(DictCodeList.收费类型_不规则费用.getValue(), bussFinProductFeeInfoDto.getChargeType())){
                    continue;
                }
                if (StringUtil.isNotEmpty(bussFinProductFeeInfoDto.getProvisionFirstDay())) {
                    if (StringUtil.isNotEmpty(entity.getVdate())
                        && DateUtil.compareDate(bussFinProductFeeInfoDto.getProvisionFirstDay(),
                            entity.getVdate()) < 0) {
                        validList.add(new ValidMessage(
                            "bussFinProductFeeInfoDto.bussFinProductFeeInfoDtoList",
                            this.getMessage("BUSS_COM_E_0004",
                                this.getMessage("BussFinProductFeeInfo.provisionFirstDay"),
                                this.getMessage("FinProductBuild002.vdate"))));
                    }
                    if (StringUtil.isNotEmpty(entity.getMdate())
                        && DateUtil.compareDate(bussFinProductFeeInfoDto.getProvisionFirstDay(),
                            entity.getMdate()) > 0) {
                        validList.add(new ValidMessage(
                            "bussFinProductFeeInfoDto.bussFinProductFeeInfoDtoList",
                            this.getMessage("FIN_PROD_E_0017",
                                this.getMessage("BussFinProductFeeInfo.provisionFirstDay"),
                                this.getMessage("FinProductBuild002.mdate"))));
                    }
                } else {
                    validList.add(new ValidMessage(
                        "bussFinProductFeeInfoDto.bussFinProductFeeInfoDtoList",
                        this.getMessage("BUSS_COM_E_0038",
                            this.getMessage("BussFinProductFeeInfo.provisionFirstDay"))));
                }

                 //计提结束日
                if(!StringUtil.isNotEmpty(bussFinProductFeeInfoDto.geteAccrDate())){
                    //ZGBUG-175计提结束日未输入时提示信息建议加上产品费用模块 guyunfei  20200310 start
                    /*validList.add(new ValidMessage(
                        "bussFinProductFeeInfoDto.bussFinProductFeeInfoDtoList",
                        this.getMessage("BUSS_COM_E_0038",
                            this.getMessage("BussFinProductFeeInfo.eAccrDate"))));*/
                    validList.add(new ValidMessage(
                            "bussFinProductFeeInfoDto.bussFinProductFeeInfoDtoList",
                            "请输入产品费用模块的计提结束日"));
                    //ZGBUG-175计提结束日未输入时提示信息建议加上产品费用模块 guyunfei  20200310 end
                } else {
                    if(DateUtil.compareDate(entity.getVdate(), bussFinProductFeeInfoDto.geteAccrDate()) >= 0){
                        //计提结束日应该大于产品起息日
                        validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0106")));
                    }
                    if(StringUtil.isNotEmpty(entity.getLiquidationDate()) && DateUtil.compareDate(entity.getLiquidationDate(), bussFinProductFeeInfoDto.geteAccrDate()) <= 0){
                        //计提结束日应该小于产品清盘日
                        validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0107")));
                    }
                    if(DateUtil.compareDate(bussFinProductFeeInfoDto.getProvisionFirstDay(), bussFinProductFeeInfoDto.geteAccrDate()) >= 0){
                        //计提结束日应该大于计提开始日（首次计提结束日）
                        validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0108")));
                    }
                }
                //首次计费结束日
                if(StringUtil.isNotEmpty(bussFinProductFeeInfoDto.getFirstCalEndDate())){
                    if(DateUtil.compareDate(entity.getVdate(), bussFinProductFeeInfoDto.getFirstCalEndDate()) >= 0){
                        //首次计费结束日应该大于产品起息日
                        validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0109")));
                    }
                    if(StringUtil.isNotEmpty(entity.getLiquidationDate()) && DateUtil.compareDate(entity.getLiquidationDate(), bussFinProductFeeInfoDto.getFirstCalEndDate()) < 0){
                        //首次计费结束日应该小于产品清盘日
                        validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0110")));

                    }
                }
                //计费基数 为资产净值时 资产净值日类型不能为当日
                if (StringUtil.equals(DictCodeList.计费基数_费用品种_资产规模.getValue(),
                    bussFinProductFeeInfoDto.getBasicBill()) && StringUtil.equals(DictCodeList.基数日期类型_当日.getValue(),
                        bussFinProductFeeInfoDto.getNetAssetValueDay())) {
                    validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0113")));
                }
                //首次计费基数不能为空 yzg end 20191020
				if((StringUtil.equals(bussFinProductFeeInfoDto.getNetAssetValueDay(),DictCodeList.基数日期类型_前一自然日.getValue()) ||
						StringUtil.equals(bussFinProductFeeInfoDto.getNetAssetValueDay(), DictCodeList.基数日期类型_前一工作日.getValue())) &&
						DateUtil.compareDate(bussFinProductFeeInfoDto.getProvisionFirstDay(),entity.getVdate()) == 0 &&
						StringUtil.equals(bussFinProductFeeInfoDto.getBasicBill(), DictCodeList.计费基数_费用品种_资产规模.getValue())) {
	                if(!StringUtil.equals(bussFinProductFeeInfoDto.getFirstBaseType(), DictCodeList.计费基数_费用品种_存续本金.getValue())) {
	                	validList.add(new ValidMessage("bussFinProductFeeInfoDto.bussFinProductFeeInfoDtoList", getMessage("BussFinProductFeeInfo.firstBaseType.yes")));
	                }
				}else {
					if(StringUtil.isNotEmpty(bussFinProductFeeInfoDto.getFirstBaseType())) {
	                	validList.add(new ValidMessage("bussFinProductFeeInfoDto.bussFinProductFeeInfoDtoList", getMessage("BussFinProductFeeInfo.firstBaseType.no")));
	                }
				}

                //首次计费基数不能为空 yzg end 20191020
            }
        }

        // 浮动管理费业务组件
        if (entity.getBussFinProductFloatFeeDto() != null) {
            // add by jiangpeng on 20210708 TISHKBS-2425 start 收益类型为货币型的产品,浮动管理费仅支持选择计费基数为“资产净值”的费用
            // 计费基数 03 资产净值 04 存续本金
            if (StringUtil.isNotEmpty(entity.getBussFinProductFloatFeeDto().getBasicBill())
                    && !StringUtil.equals(DictCodeList.计费基数_费用品种_资产规模.getValue(), entity.getBussFinProductFloatFeeDto().getBasicBill())
                    && StringUtil.equals(entity.getProfitType(), DictCodeList.收益类型_货币型.getValue())) {
                validList.add(new ValidMessage("收益类型为货币型的产品,浮动管理费仅支持选择计费基数为“资产净值”的费用"));
            }
            // add by jiangpeng on 20210708 TISHKBS-2425 end
            if(StringUtil.isNotEmpty(entity.getBussFinProductFloatFeeDto().getFeeId())
                && (StringUtil.equals(DictCodeList.处理模式_普通.getValue(), entity.getDealMode())||
                StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode())) ){
                if (StringUtil.isNotEmpty(
                    entity.getBussFinProductFloatFeeDto().getProvisionFirstDay())) {
                    if (StringUtil.isNotEmpty(entity.getVdate()) && DateUtil.compareDate(
                        entity.getBussFinProductFloatFeeDto().getProvisionFirstDay(),
                        entity.getVdate()) < 0) {
                        validList.add(new ValidMessage("bussFinProductFloatFeeDto.provisionFirstDay",
                            this.getMessage("BUSS_COM_E_0004",
                                this.getMessage("BussFinProductFloatFee.provisionFirstDay"),
                                this.getMessage("FinProductBuild002.vdate"))));
                    }
                    if (StringUtil.isNotEmpty(entity.getMdate()) && DateUtil.compareDate(
                        entity.getBussFinProductFloatFeeDto().getProvisionFirstDay(),
                        entity.getMdate()) > 0) {
                        validList.add(new ValidMessage("bussFinProductFloatFeeDto.provisionFirstDay",
                            this.getMessage("FIN_PROD_E_0017",
                                this.getMessage("BussFinProductFloatFee.provisionFirstDay"),
                                this.getMessage("FinProductBuild002.mdate"))));
                    }
                }
                if (StringUtil.isNotEmpty(
                    entity.getBussFinProductFloatFeeDto().getFirstPayDate())) {
                    if (StringUtil.isNotEmpty(entity.getVdate()) && DateUtil.compareDate(
                        entity.getBussFinProductFloatFeeDto().getFirstPayDate(),
                        entity.getVdate()) < 0) {
                        validList.add(new ValidMessage("bussFinProductFloatFeeDto.firstPayDate",
                            this.getMessage("BUSS_COM_E_0004",
                                this.getMessage("BussFinProductFloatFee.firstPayDate"),
                                this.getMessage("FinProductBuild002.vdate"))));
                    }
                }
                if (StringUtil.isNotEmpty(
                    entity.getBussFinProductFloatFeeDto().geteAccrDate())) {
                    if (StringUtil.isNotEmpty(entity.getVdate()) && DateUtil.compareDate(
                        entity.getBussFinProductFloatFeeDto().geteAccrDate(),
                        entity.getVdate()) < 0) {
                        validList.add(new ValidMessage("bussFinProductFloatFeeDto.eAccrDate",
                            this.getMessage("BUSS_COM_E_0004",
                                this.getMessage("BussFinProductFloatFee.eAccrDate"),
                                this.getMessage("FinProductBuild002.vdate"))));
                    }
                    if (StringUtil.isNotEmpty(entity.getMdate()) && DateUtil.compareDate(
                        entity.getBussFinProductFloatFeeDto().geteAccrDate(),
                        entity.getMdate()) > 0) {
                        validList.add(new ValidMessage("bussFinProductFloatFeeDto.eAccrDate",
                            this.getMessage("FIN_PROD_E_0017",
                                this.getMessage("BussFinProductFloatFee.eAccrDate"),
                                this.getMessage("FinProductBuild002.mdate"))));
                    }
                }

                if(StringUtil.isNotEmpty(
                    entity.getBussFinProductFloatFeeDto().getProvisionFirstDay())
                    &&StringUtil.isNotEmpty(
                        entity.getBussFinProductFloatFeeDto().geteAccrDate())
                    &&DateUtil.compareDate(
                            entity.getBussFinProductFloatFeeDto().getProvisionFirstDay(),
                            entity.getBussFinProductFloatFeeDto().geteAccrDate()) > 0){
                    validList.add(new ValidMessage("bussFinProductFloatFeeDto.eAccrDate",
                        this.getMessage("BUSS_COM_E_0004",
                            this.getMessage("BussFinProductFloatFee.eAccrDate"),
                            this.getMessage("BussFinProductFloatFee.provisionFirstDay"))));

                }
                if(StringUtil.isEmptyList(entity.getBussFinProductFloatFeeDto().getFinFloatCalcRuleDetailDtoList())){
                	validList.add(new ValidMessage(this.getMessage("FIN_PROD_E_0135")));
                }
            }

            //add by yujiaping 2020-11-18 TISHKBS-734“分档"产品，浮动管理费增加计提结束日不能大于产品到期日的校验 start
            if (!StringUtil.isEmptyList(entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList())) {
            	 for(BussFinProductFloatFeeDto bussFinProductFloatFeeDto :
                     entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList()) {
                if(StringUtil.isNotEmpty(bussFinProductFloatFeeDto.getFeeId())
                    && (StringUtil.equals(DictCodeList.处理模式_分档.getValue(), entity.getDealMode())) ){
                    if (StringUtil.isNotEmpty(
                    		bussFinProductFloatFeeDto.geteAccrDate())) {
                        if (StringUtil.isNotEmpty(entity.getMdate()) && DateUtil.compareDate(
                        		bussFinProductFloatFeeDto.geteAccrDate(),
                            entity.getMdate()) > 0) {
                            validList.add(new ValidMessage("浮动管理费的计提结束日不能大于到期日。"));
                        }
                    }
                }
            }
          }//add by yujiaping 2020-11-18 TISHKBS-734“分档”产品，浮动管理费增加计提结束日不能大于产品到期日的校验 end
            boolean isCycleInv=false;
            HashMap<String, String> investmentCycles=new HashMap<String, String>();
            if(!StringUtil.isEmptyList(entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList())
                    && (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode()))
                    && StringUtil.equals(DictCodeListExt2.期限类型_多固定期限.getValue(), entity.getPeriod())
            ){
              for(BussFinProductFloatFeeDto bussFinProductFloatFeeDto :entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList()){
                  if(StringUtil.isNotEmpty(bussFinProductFloatFeeDto.getFeeId())
                  ){
                      if(investmentCycles.containsKey(bussFinProductFloatFeeDto.getInvestmentCycle())){
                          isCycleInv=true;
                          break;
                      }
                      investmentCycles.put(bussFinProductFloatFeeDto.getInvestmentCycle(),bussFinProductFloatFeeDto.getFeeId());
                  }
              }
          }
          if(isCycleInv){
              validList.add(new ValidMessage("净值型周期型多固定期限产品同一期限不允许设置多条浮动管理费规则。"));
          }
            
            
            if(!StringUtil.isEmptyList(entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList())
                && (StringUtil.equals(DictCodeList.处理模式_分档周期型.getValue(), entity.getDealMode())||
                StringUtil.equals(DictCodeList.处理模式_分档.getValue(), entity.getDealMode()))){
            	//add by wm 增加分档产品针对分层设置了浮动管理费没有设置相应的业绩基准校验start 2019-08-09
            	//业绩基准是否为空
            	boolean idMarkLayer = false;
            	Map<String, BussBenchMarkLayeredInfoDto> bussBenchMarkLayeredMap = new HashMap<String, BussBenchMarkLayeredInfoDto>();
            	if(entity.getBussBenchMarkLayeredInfoDto()!=null
            			&&!StringUtil.isEmptyList(entity.getBussBenchMarkLayeredInfoDto().getBussBenchMarkLayeredInfoDtoList())){
            		bussBenchMarkLayeredMap = entity.getBussBenchMarkLayeredInfoDto().getBussBenchMarkLayeredInfoDtoList().stream().collect(
                            Collectors.toMap(BussBenchMarkLayeredInfoDto:: getProdStyle, a -> a, (k1, k2) -> k1));
            	}
            	else{
            		idMarkLayer = true;
            	}
            	//add by wm 增加分档产品针对分层设置了浮动管理费没有设置相应的业绩基准校验end 2019-08-09
                for(BussFinProductFloatFeeDto bussFinProductFloatFeeDto :
                    entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList()){
                    if(StringUtil.isNotEmpty(bussFinProductFloatFeeDto.getFeeId())){
                        if (StringUtil.isNotEmpty(
                            bussFinProductFloatFeeDto.getProvisionFirstDay())) {
                            if (StringUtil.isNotEmpty(entity.getVdate()) && DateUtil.compareDate(
                                bussFinProductFloatFeeDto.getProvisionFirstDay(),
                                entity.getVdate()) < 0) {
                                validList.add(new ValidMessage("bussFinProductFloatFeeDto.provisionFirstDay",
                                    this.getMessage("BUSS_COM_E_0004",
                                        this.getMessage("BussFinProductFloatFee.provisionFirstDay"),
                                        this.getMessage("FinProductBuild002.vdate"))));
                            }
                            if (StringUtil.isNotEmpty(entity.getMdate()) && DateUtil.compareDate(
                                bussFinProductFloatFeeDto.getProvisionFirstDay(),
                                entity.getMdate()) > 0) {
                                validList.add(new ValidMessage("bussFinProductFloatFeeDto.provisionFirstDay",
                                    this.getMessage("FIN_PROD_E_0017",
                                        this.getMessage("BussFinProductFloatFee.provisionFirstDay"),
                                        this.getMessage("FinProductBuild002.mdate"))));
                            }
                        }
                        if (StringUtil.isNotEmpty(
                            bussFinProductFloatFeeDto.getFirstPayDate())) {
                            if (StringUtil.isNotEmpty(entity.getVdate()) && DateUtil.compareDate(
                                bussFinProductFloatFeeDto.getFirstPayDate(),
                                entity.getVdate()) <= 0) {
                                validList.add(new ValidMessage("bussFinProductFloatFeeDto.firstPayDate",
                                    this.getMessage("BUSS_COM_E_0004",
                                        this.getMessage("BussFinProductFloatFee.firstPayDate"),
                                        this.getMessage("FinProductBuild002.vdate"))));
                            }

                        }
                        if (StringUtil.isNotEmpty(
                            entity.getBussFinProductFloatFeeDto().geteAccrDate())) {
                            if (StringUtil.isNotEmpty(entity.getVdate()) && DateUtil.compareDate(
                                bussFinProductFloatFeeDto.geteAccrDate(),
                                entity.getVdate()) < 0) {
                                validList.add(new ValidMessage("bussFinProductFloatFeeDto.eAccrDate",
                                    this.getMessage("BUSS_COM_E_0004",
                                        this.getMessage("BussFinProductFloatFee.eAccrDate"),
                                        this.getMessage("FinProductBuild002.vdate"))));
                            }
                            if (StringUtil.isNotEmpty(entity.getMdate()) && DateUtil.compareDate(
                                bussFinProductFloatFeeDto.geteAccrDate(),
                                entity.getMdate()) > 0) {
                                validList.add(new ValidMessage("bussFinProductFloatFeeDto.eAccrDate",
                                    this.getMessage("FIN_PROD_E_0017",
                                        this.getMessage("BussFinProductFloatFee.eAccrDate"),
                                        this.getMessage("FinProductBuild002.mdate"))));
                            }
                        }

                        if(StringUtil.isNotEmpty(
                             bussFinProductFloatFeeDto.getProvisionFirstDay())
                            &&StringUtil.isNotEmpty(
                                bussFinProductFloatFeeDto.geteAccrDate())
                            &&DateUtil.compareDate(
                                    bussFinProductFloatFeeDto.getProvisionFirstDay(),
                                    bussFinProductFloatFeeDto.geteAccrDate()) > 0){
                            validList.add(new ValidMessage("bussFinProductFloatFeeDto.eAccrDate",
                                this.getMessage("BUSS_COM_E_0004",
                                    this.getMessage("BussFinProductFloatFee.eAccrDate"),
                                    this.getMessage("BussFinProductFloatFee.provisionFirstDay"))));

                        }
                        //add by wm 增加分档产品针对分层设置了浮动管理费没有设置相应的业绩基准校验start 2019-08-09
                       /* if(idMarkLayer||bussBenchMarkLayeredMap.get(bussFinProductFloatFeeDto.getProdStyle())==null){
                        	validList.add(new ValidMessage(
                                    this.getMessage("FIN_PROD_E_0132",bussFinProductFloatFeeDto.getProdStyle())));
                        }*/
                        //add by wm 增加分档产品针对分层设置了浮动管理费没有设置相应的业绩基准校验end 2019-08-09
                    }
                }
            }
        }

        // 分红计划业务组件
        if (entity.getBussFinProductBonusPlanDto() != null) {
            if (StringUtil.isNotEmpty(entity.getBussFinProductBonusPlanDto().getFirstPayDate())) {
                if (StringUtil.isNotEmpty(entity.getVdate()) && DateUtil.compareDate(
                    entity.getBussFinProductBonusPlanDto().getFirstPayDate(),
                    entity.getVdate()) <= 0) {
                    validList.add(new ValidMessage("bussFinProductBonusPlanDto.firstPayDate",
                        this.getMessage("FIN_PROD_E_0015",
                            this.getMessage("BussFinProductBonusPlan.firstPayDate"),
                            this.getMessage("FinProductBuild002.vdate"))));
                }
                if (StringUtil.isNotEmpty(entity.getMdate()) && DateUtil.compareDate(
                    entity.getBussFinProductBonusPlanDto().getFirstPayDate(),
                    entity.getMdate()) > 0) {
                    validList.add(new ValidMessage("bussFinProductBonusPlanDto.firstPayDate",
                        this.getMessage("FIN_PROD_E_0017",
                            this.getMessage("BussFinProductBonusPlan.firstPayDate"),
                            this.getMessage("FinProductBuild002.mdate"))));
                }
            }
          //add by wm 增加货币型产品时分红规则为到期支付、周期支付时，必须生成分红计划校验 start
            if((StringUtil.equals(entity.getBussFinProductBonusPlanDto().getPayCycle(),
                    DictCodeList.分红规则_到期支付.getValue())||StringUtil.equals(entity.getBussFinProductBonusPlanDto().getPayCycle(),
                            DictCodeList.分红规则_周期支付.getValue()))
            		&&StringUtil.equals(entity.getProfitType(),DictCodeList.收益类型_货币型.getValue())
            		&&StringUtil.isEmptyList(entity.getBussFinProductBonusPlanDto().getCashPlanList())){
            	validList.add(new ValidMessage("请生成分红计划！"));
            }
            //add by wm 增加货币型产品时分红规则为到期支付、周期支付时，必须生成分红计划校验 end
        }

        // 开放计划业务组件
        if (entity.getBussFinProductOpenPlanDto() != null) {
            if (StringUtil.isNotEmpty(entity.getBussFinProductOpenPlanDto().getFirstOpenDate())) {
                if (StringUtil.equals(DictCodeList.推算方法_开放确认日.getValue(), entity.getBussFinProductOpenPlanDto().getCalcRule())
                		&& StringUtil.isNotEmpty(entity.getVdate()) && DateUtil.compareDate(
                    entity.getBussFinProductOpenPlanDto().getFirstOpenDate(),
                    entity.getVdate()) <= 0) {
                    validList.add(new ValidMessage("bussFinProductOpenPlanDto.firstOpenDate",
                        this.getMessage("FIN_PROD_E_0015",
                            this.getMessage("FinProductOpenRule.firstOpenDate"),
                            this.getMessage("FinProductBuild002.vdate"))));
                }
                if (StringUtil.isNotEmpty(entity.getMdate()) && DateUtil.compareDate(
                    entity.getBussFinProductOpenPlanDto().getFirstOpenDate(),
                    entity.getMdate()) > 0) {
                    validList.add(new ValidMessage("bussFinProductOpenPlanDto.firstOpenDate",
                        this.getMessage("FIN_PROD_E_0017",
                            this.getMessage("FinProductOpenRule.firstOpenDate"),
                            this.getMessage("FinProductBuild002.mdate"))));
                }
            }
            List<FinOpenPlan> finOpenPlanList = entity.getBussFinProductOpenPlanDto().getFinOpenPlanList();
     //add by yujiaping 2020-12-2  ZGBS-50 非高频开放的产品提交时，如无开放计划限制提交，给出提醒：非高频开放的产品，开放计划不能为空start
            if(!StringUtil.equals(entity.getBussFinProductOpenPlanDto().getOpenMode(), DictCodeList.开放模式_高频开放.getValue())){
            	if(StringUtil.isEmptyList(finOpenPlanList)) {
            		validList.add(new ValidMessage("bussFinProductOpenPlanDto.finOpenPlanList",
                            this.getMessage("非高频开放的产品，开放计划不能为空。")));
            	}
            }
    //add by yujiaping 2020-12-2  ZGBS-50 非高频开放的产品提交时，如无开放计划限制提交，给出提醒：非高频开放的产品，开放计划不能为空end
            if (!StringUtil.isEmptyList(finOpenPlanList)
                && StringUtil.isNotEmpty(entity.getVdate())) {
                for (FinOpenPlan finOpenPlan : finOpenPlanList) {
                    if (StringUtil.isNotEmpty(finOpenPlan.getOpenDateStr())&&StringUtil.isNotEmpty(entity.getVdate())
                        &&StringUtil.isNotEmpty(finOpenPlan.getOpenDateEnd())
                    ){
                        if (finOpenPlan.getOpenDateStr().compareTo(entity.getVdate()) < 0
                            || finOpenPlan.getOpenDateEnd().compareTo(entity.getVdate()) < 0) {
                            validList.add(new ValidMessage("bussFinProductOpenPlanDto.finOpenPlanList",
                                this.getMessage("FIN_PROD_E_0073")));
                            break;
                        }
                    }
                }
            }
            //当申赎不同频并且不是周期型产品时，对赎回计划进行校验
            if (!StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode())
                    &&!StringUtil.equals(entity.getBussFinProductOpenPlanDto().getOpenType(),DictCodeList.申赎开放类型_申赎开放.getValue())
                    &&entity.getBussFinProductOpenPlanRedDto() != null&&
                    !StringUtil.equals(entity.getBussFinProductOpenPlanRedDto().getOpenMode(), DictCodeList.开放模式_高频开放.getValue())) {
                if(StringUtil.isEmptyList(entity.getBussFinProductOpenPlanRedDto().getFinOpenPlanList())){
                    validList.add(new ValidMessage("bussFinProductOpenPlanRedDto.finOpenPlanList",
                            "申赎不同频时，请生成赎回计划"));
            	}
            	if (StringUtil.isNotEmpty(entity.getBussFinProductOpenPlanRedDto().getFirstOpenDate())) {
                    if (StringUtil.equals(DictCodeList.推算方法_开放确认日.getValue(), entity.getBussFinProductOpenPlanRedDto().getCalcRule())
                    		&& StringUtil.isNotEmpty(entity.getVdate()) && DateUtil.compareDate(
                        entity.getBussFinProductOpenPlanRedDto().getFirstOpenDate(),
                        entity.getVdate()) <= 0) {
                        validList.add(new ValidMessage("bussFinProductOpenPlanRedDto.firstOpenDate",
                            this.getMessage("FIN_PROD_E_0015",
                                this.getMessage("FinProductOpenRule.firstOpenDate"),
                                this.getMessage("FinProductBuild002.vdate"))));
                    }
                    if (StringUtil.isNotEmpty(entity.getMdate()) && DateUtil.compareDate(
                        entity.getBussFinProductOpenPlanRedDto().getFirstOpenDate(),
                        entity.getMdate()) > 0) {
                        validList.add(new ValidMessage("bussFinProductOpenPlanRedDto.firstOpenDate",
                            this.getMessage("FIN_PROD_E_0017",
                                this.getMessage("FinProductOpenRule.firstOpenDate"),
                                this.getMessage("FinProductBuild002.mdate"))));
                    }
                }
                List<FinOpenPlan> finOpenRedPlanList = entity.getBussFinProductOpenPlanRedDto().getFinOpenPlanList();
                if (!StringUtil.isEmptyList(finOpenRedPlanList)
                    && StringUtil.isNotEmpty(entity.getVdate())) {
                    for (FinOpenPlan finOpenPlan : finOpenRedPlanList) {
                        if (finOpenPlan.getOpenDateStr().compareTo(entity.getVdate()) < 0
                            || finOpenPlan.getOpenDateEnd().compareTo(entity.getVdate()) < 0) {
                            validList.add(new ValidMessage("bussFinProductOpenPlanRedDto.finOpenPlanList",
                                this.getMessage("FIN_PROD_E_0073")));
                            break;
                        }
                    }
                }
            }
        }

        /* 单一投资者持有上限≥50%且非现金管理类产品时，调用共同方法校验投资周期  add by kongzhifang on 2022-03-02 start */
        if(entity.getBussFinProductSaleParamDto() != null) {
            BigDecimal holdingMax = entity.getBussFinProductSaleParamDto().getSingleInvestorholdingMax();

            if (DictCodeList.是否_否.getValue().equals(entity.getIsCashManage())
                    && (holdingMax != null && holdingMax.compareTo(BigDecimal.valueOf(50)) >= 0)) {
                if(CommonUtil.isType(entity.getDealMode(),DictCodeList.处理模式_周期型) && CommonUtil.isType(entity.getPeriod(),DictCodeListExt2.期限类型_单一固定期限)){
                    if(StringUtil.isNotEmpty(entity.getInvestmentCycle())){
                        Integer investmentCycle = Integer.valueOf(entity.getInvestmentCycle());
                        if(investmentCycle.compareTo(90) < 0){
                            validList.add(new ValidMessage("bussFinProductOpenPlanRedDto.finOpenPlanList",
                                    "允许单一投资者持有份额超过总份额50%的，定期开放周期不得低于90天，请检查！"));
                        }
                    }
                }else if(CommonUtil.isType(entity.getDealMode(),DictCodeList.处理模式_周期型) && CommonUtil.isType(entity.getPeriod(),DictCodeListExt2.期限类型_多固定期限)){
                    List<BussFinProductInvestPeriodDto> cycleList = entity.getBussFinProductInvestPeriodDto().getBussFinProductInvestPeriodDtoList();
                    if(!StringUtil.isEmptyList(cycleList)){
                        BussFinProductInvestPeriodDto cycle = cycleList.stream().min(Comparator.comparing(BussFinProductInvestPeriodDto::getInvestDays)).get();
                        if(cycle.getInvestDays().compareTo(90) < 0){
                            validList.add(new ValidMessage("bussFinProductOpenPlanRedDto.finOpenPlanList",
                                    "允许单一投资者持有份额超过总份额50%的，定期开放周期不得低于90天，请检查！"));
                        }
                    }
                }else{
                    //申购计划
                    List<FinOpenPlan> openPlanList = entity.getBussFinProductOpenPlanDto().getFinOpenPlanList();
                    //赎回计划
                    List<FinOpenPlan> openPlanRedList = new ArrayList<>();

                    if (entity.getBussFinProductOpenPlanRedDto() != null) {
                        openPlanRedList = entity.getBussFinProductOpenPlanRedDto().getFinOpenPlanList();
                    }

                    Integer compareResulit = AssetCommonUtil.comparePtlCycle(openPlanList, entity.getBussFinProductOpenPlanDto(), openPlanRedList, entity.getBussFinProductOpenPlanRedDto(), entity, 90);

                    if (compareResulit != null && compareResulit.equals(-1)) {
                        validList.add(new ValidMessage("bussFinProductOpenPlanRedDto.finOpenPlanList",
                                "允许单一投资者持有份额超过总份额50%的，定期开放周期不得低于90天，请检查！"));
                    }
                }
            }

            if(!DictCodeListExt1.募集方式_公募.getValue().equals(entity.getIssueType())
                    &&StringUtil.isEmpty(entity.getBussFinProductSaleParamDto().getIsFaceSingleInvestor())){
                validList.add(new ValidMessage("bussFinProductSaleParamDto.isFaceSingleInvestor",
                        getMessage("FIN_PROD_E_0001",getMessage("BussFinProductSaleParam.isFaceSingleInvestor"))));
            }
        }
        /* 单一投资者持有上限≥50%且非现金管理类产品时，调用共同方法校验投资周期  add by kongzhifang on 2022-03-02 end */

        if (StringUtil.equals(entity.getConfirmFlag(), DictCodeList.是否_是.getValue())) {
            // 根据排期金融产品代码查询排期信息
            FinNetValueProductBuild002Dto finProductSchedulingDto = finNetValueProductBuild002Service.queryProdScheduleByFinprodId(
                entity.getFinprodId());
            if (finProductSchedulingDto != null) {
                // 修改时需校验本次修改的募集起始日>上次募集起始日，本次修改的募集到期日<上次募集到期日；
                if (StringUtil.isNotEmpty(entity.getFirstSaleVdate())
                    && DateUtil.compareDate(entity.getFirstSaleVdate(),
                        finProductSchedulingDto.getFirstSaleVdate()) < 0) {
                    validList.add(new ValidMessage("firstSaleVdate", getMessage("FIN_PROD_E_0074")));
                }
                if (StringUtil.isNotEmpty(entity.getFirstSaleMdate())
                    && DateUtil.compareDate(entity.getFirstSaleMdate(),
                        finProductSchedulingDto.getFirstSaleMdate()) > 0) {
                    validList.add(new ValidMessage("firstSaleMdate", getMessage("FIN_PROD_E_0075")));
                }
                // 修改成立日时，需判断成立日>=平台日
                if (StringUtil.isNotEmpty(entity.getVdate())
                    && DateUtil.compareDate(entity.getVdate(), DateUtil.platDateFromCache()) < 0) {
                    validList.add(new ValidMessage("vdate", getMessage("BUSS_COM_E_0004", this.getMessage("FinProductBuild002.vdate"),
                        this.getMessage("FinProductQuotation001.currentDate"))));
                }
            }
        }

        // 浮动管理费的费用名称如果不为空时需判断业绩基准是否填写
        if (entity.getBussFinProductFloatFeeDto() != null) {
            BussFinProductFloatFeeDto bussFinProductFloatFeeDto = entity.getBussFinProductFloatFeeDto();
            if (StringUtil.isNotEmpty(bussFinProductFloatFeeDto.getFeeId())) {
                BussBenchMarkInfoDto bussBenchMarkInfoDto = entity.getBussBenchMarkInfoDto();
                if ((bussBenchMarkInfoDto != null
                     && !StringUtil.isNotEmpty(bussBenchMarkInfoDto.getRateType()))
                    || bussBenchMarkInfoDto == null) {
                	if(StringUtil.isEmpty(bussBenchMarkInfoDto.getPublishType()) || DictCodeListExt1.披露类型_单值披露.getValue().equals(bussBenchMarkInfoDto.getPublishType()))
                    validList.add(new ValidMessage("bussBenchMarkInfoDto.rateType", getMessage(
                        "BUSS_COM_E_0038", getMessage("BussBenchMarkInfo")
                                           + getMessage("BussBenchMarkInfo.rateType"))));
                }
            }
        }
        //如果浮动管理费的费用名称不为空才进行一下判断
//        if(entity.getBussFinProductFloatFeeDto()!=null && !"".equals(entity.getBussFinProductFloatFeeDto())){
//	        if(StringUtil.isNotEmpty(entity.getBussFinProductFloatFeeDto().getFeeId())){//add by yejiejei 2019-2-14
//		        //浮动管理费  净值日交易
//		        if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())&&entity.getBussFinProductFloatFeeDto() != null&&!StringUtil.isNotEmpty(entity.getBussFinProductFloatFeeDto().getNetAssetValueDay())){
//		        	validList.add(new ValidMessage("bussFinProductFloatFeeDto.netAssetValueDay", getMessage("BUSS_COM_E_0038",
//		                    getMessage("BussFinProductFloatFee.netAssetValueDay")), entity.getBussFinProductFloatFeeDto().getEntityName()));
//		        }
//	        }
//        }
        //add by yejiejie 2019-2-14 start

        //净值化产品的募集要素中， 产品的募集币种在兑付本金及兑付收益币种的包含之中
        // add by zcw 中债画面元素已被注释 start
//        if(StringUtil.isNotEmpty(entity.getCcy())){
//        	//add by wl 2019-4-2 start
//        	if(entity.getCcy().equals(entity.getBussRepFinProductInfoDto().getRepCashProfCcy())){
//        		if(StringUtil.isNotEmpty(entity.getBussRepFinProductInfoDto().getRepCashPrinCcy()) && StringUtil.equals(entity.getBussRepFinProductInfoDto().getRepCashPrinCcy(), entity.getBussRepFinProductInfoDto().getRepCashProfCcy())){
//        			if(entity.getCcy().equals(entity.getBussRepFinProductInfoDto().getRepCashPrinCcy())){
//
//        			}else{
//        				validList.add(new ValidMessage("ccy", getMessage("产品的募集币种、兑付本金及兑付收益币种必须相同")));
//        			}
//        		}else{
//    				validList.add(new ValidMessage("ccy", getMessage("产品的募集币种、兑付本金及兑付收益币种必须相同")));
//    			}
//        	}else{
//				validList.add(new ValidMessage("ccy", getMessage("产品的募集币种、兑付本金及兑付收益币种必须相同")));
//			}
//        	//add by wl 2019-4-2 end
//        	//兑付本金币种
//        	/*String repCashPrinCcyFlag = "0";
//        	List<String> repCashPrinCcyList = StringUtil.splitToList(GlobalConstant.COMMA,
//                    entity.getBussRepFinProductInfoDto().getRepCashPrinCcy());
//        	for (int i = 0; i < repCashPrinCcyList.size(); i++) {
//				if(StringUtil.equals(repCashPrinCcyList.get(i),entity.getCcy())){
//					repCashPrinCcyFlag = "1";
//				}
//			}
//        	if(!StringUtil.equals("1",repCashPrinCcyFlag)){
//        		//兑付本金币种不包含募集币种
//        		validList.add(new ValidMessage("ccy", getMessage("FIN_PROD_E_0093")));
//        	}
//        	//兑付收益币种
//        	String repCashProfCcyFlag = "0";
//        	List<String> repCashProfCcyList = StringUtil.splitToList(GlobalConstant.COMMA,
//                    entity.getBussRepFinProductInfoDto().getRepCashProfCcy());
//        	for (int i = 0; i < repCashProfCcyList.size(); i++) {
//				if(StringUtil.equals(repCashProfCcyList.get(i),entity.getCcy())){
//					repCashProfCcyFlag = "1";
//				}
//			}
//        	if(!StringUtil.equals("1",repCashProfCcyFlag)){
//        		//兑付收益币种不包含募集币种
//        		validList.add(new ValidMessage("ccy", getMessage("FIN_PROD_E_0094")));
//        	}*/
//        }
        // add by zcw 中债画面元素已被注释 end
        //add by yejiejie 2019-2-14 end
      //update zhangweiqi 资金账户不能为空，且至少有个托管户 begin
        /*if(StringUtil.isEmptyList(entity.getBussFinProductFundAccountInfoDto())){
            validList.add(new ValidMessage("bussFinProductFundAccountInfoDto",
                this.getMessage("BUSS_COM_E_0038",
                    this.getMessage("BussFinProductFundAccountInfo"))));
        }else{
        	 boolean flag=false;
        	 for(int k=0;k<entity.getBussFinProductFundAccountInfoDto().size();k++){
         		if(StringUtil.equals(DictCodeListExt1.资金账户类型_产品托管户.getValue(),entity.getBussFinProductFundAccountInfoDto().get(k).getAcctType())){
         			flag=true;
         		}
        	 }
        	 if(!flag){
        		 validList.add(new ValidMessage("bussFinProductFundAccountInfoDto",
        	                this.getMessage("bussFinProductFundAccountInfoDto",this.getMessage("BussFinProductFundAccountInfo_A01"))));
        	}
        }*/
      //update zhangweiqi 资金账户不能为空，且至少有个托管户 end

        //add by yejiejie 2019-2-18 start
        //资金账户中的费用名称不能重复
        if(StringUtil.isNotEmpty(entity.getBussFinProductFundAccountInfoDto())){
        	List<String> fundAccountFeetypeList = new ArrayList<String>();
        	for(int k=0;k<entity.getBussFinProductFundAccountInfoDto().size();k++){

        		List<String> list = StringUtil.splitToList(GlobalConstant.COMMA,
                        entity.getBussFinProductFundAccountInfoDto().get(k).getFeeType());
        		for(String str:list){
        			if(StringUtil.isNotEmpty(str))
        		    fundAccountFeetypeList.add(str);
        		}
        	}

        	if(fundAccountFeetypeList != null && fundAccountFeetypeList.size()!=0){
	        	for(int i=0;i<fundAccountFeetypeList.size();i++){
	        		for(int j=i+1;j<fundAccountFeetypeList.size();j++){
	        			if(StringUtil.equals(fundAccountFeetypeList.get(i),fundAccountFeetypeList.get(j))){
	        				//如果有费用相等，则给出提示信息
	        				//资金账户信息表中费用名称不能重复
	                		validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0100")));
	        			}
	        		}
	        	}
        	}
        }
        //资金账户信息表中同一个币种不能有相同的资金账户
        if(StringUtil.isNotEmpty(entity.getBussFinProductFundAccountInfoDto())){
            for(int i=0;i<entity.getBussFinProductFundAccountInfoDto().size();i++){
                //对资金账户类型_产品托管户、资金账户类型_结算备付金、资金账户类型_交易保证金 账号重复check lijun modify
                String acctType1 = entity.getBussFinProductFundAccountInfoDto().get(i).getAcctType();
                if(StringUtil.equals(DictCodeListExt1.资金账户类型_产品托管户.getValue(), acctType1)
                    || StringUtil.equals(DictCodeListExt1.资金账户类型_结算备付金账户.getValue(), acctType1)
                    || StringUtil.equals(DictCodeListExt1.资金账户类型_交易保证金账户.getValue(), acctType1)){
                    for(int j = 0;j<entity.getBussFinProductFundAccountInfoDto().size();j++){
                        String acctType2 = entity.getBussFinProductFundAccountInfoDto().get(j).getAcctType();
                        //去除自己本身比较
                        if(i == j){
                            continue;
                        }
                        if(StringUtil.equals(acctType1, acctType2)
                                &&
                            StringUtil.equals(
                                    entity.getBussFinProductFundAccountInfoDto().get(i).getCcy(),
                                    entity.getBussFinProductFundAccountInfoDto().get(j).getCcy())){
                            //如果有同一个币种有相同的资金账户类型，则给出提示信息
                            //资金账户信息表中同一个币种不能有相同的资金账户类型
                            validList.add(new ValidMessage("bussFinProductFundAccountInfoDto", getMessage("FIN_PROD_E_0101")));
                        }
                        if(StringUtil.equals(
                                entity.getBussFinProductFundAccountInfoDto().get(i).getAcctNo(),
                                entity.getBussFinProductFundAccountInfoDto().get(j).getAcctNo())
                                &&
                            StringUtil.equals(
                                    entity.getBussFinProductFundAccountInfoDto().get(i).getCcy(),
                                    entity.getBussFinProductFundAccountInfoDto().get(j).getCcy())){
                            //如果有同一个币种有相同的资金账户，则给出提示信息
                            //资金账户信息表中同一个币种不能有相同的资金账户
                            validList.add(new ValidMessage("bussFinProductFundAccountInfoDto", getMessage("FIN_PROD_E_0111")));
                        }
                    }
                }
            }
        }
        /*
        //资金账户信息列表中的费用名称必须是产品费用里面或者是浮动管理费中存在的
        if(StringUtil.isNotEmpty(entity.getBussFinProductFundAccountInfoDto())){
        	List<String> feeList = new ArrayList<String>();
        	for(int i=0;i<entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().size();i++){
        		feeList.add(entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().get(i).getFeeId());
        	}
        	feeList.add(entity.getBussFinProductFloatFeeDto().getFeeId());
        	List<String> fundAccountList = new ArrayList<String>();
        	for(int j=0;j<entity.getBussFinProductFundAccountInfoDto().size();j++){
        		fundAccountList.add(entity.getBussFinProductFundAccountInfoDto().get(j).getFeeType());
        	}
        	for(int a=0;a<fundAccountList.size();a++){
        		if(!feeList.contains(fundAccountList.get(a))){
        			//产品费用加上浮动管理费的费用名称不包括资金账户信息列表中的费用名称
        			validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0102")));
        		}
        	}
        }
        */
        //add by yejiejie 2019-2-18 end
        //资金账户信息表中资金账户或者资金账户类型不能为空
        if(StringUtil.isNotEmpty(entity.getBussFinProductFundAccountInfoDto())){
        	for(int i=0;i<entity.getBussFinProductFundAccountInfoDto().size();i++){
        		if(!StringUtil.isNotEmpty(entity.getBussFinProductFundAccountInfoDto().get(i).getAcctType())
        				|| !StringUtil.isNotEmpty(entity.getBussFinProductFundAccountInfoDto().get(i).getAcctNo())){
        			//资金账户信息表中资金账户或者资金账户类型不能为空
        			validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0103")));
        		}
        	}
        }
        //add by yejiejie 2019-2-22 end
        //add by yejiejie 2019-2-22 start
        //产品起息日≤计提开始日<=产品到期日
//        if(StringUtil.isNotEmpty(entity.getBussFinProductFeeInfoDto())&&!StringUtil.isEmptyList(entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList())){
//        	for(int i=0;i<entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().size();i++){
//        		if(StringUtil.isNotEmpty(entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().get(i).getProvisionFirstDay())){
//	        		if(DateUtil.compareDate(entity.getVdate(), entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().get(i).getProvisionFirstDay()) > 0){
//	        			//计提开始日应该大于等于产品起息日
//	        			validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0104")));
//	        		}
//	        		if(DateUtil.compareDate(entity.getMdate(), entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().get(i).getProvisionFirstDay()) < 0){
//	        			//计提开始日应该小于等于产品到期日
//	        			validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0105")));
//	        		}
//        		}
//        	}
//        }
//        //产品起息日<计提结束日<产品清盘日
//        if(StringUtil.isNotEmpty(entity.getBussFinProductFeeInfoDto())&&!StringUtil.isEmptyList(entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList())){
//        	for(int i=0;i<entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().size();i++){
//        		if(StringUtil.isNotEmpty(entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().get(i).geteAccrDate())){
//	        		if(DateUtil.compareDate(entity.getVdate(), entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().get(i).geteAccrDate()) >= 0){
//	        			//计提结束日应该大于产品起息日
//	        			validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0106")));
//	        		}
//	        		if(DateUtil.compareDate(entity.getLiquidationDate(), entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().get(i).geteAccrDate()) <= 0){
//	        			//计提结束日应该小于产品清盘日
//	        			validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0107")));
//	        		}
//	        		if(DateUtil.compareDate(entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().get(i).getProvisionFirstDay(), entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().get(i).geteAccrDate()) >= 0){
//	        			//计提结束日应该大于计提开始日（首次计提结束日）
//	        			validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0108")));
//	        		}
//        		}
//        	}
//        }
//        //产品起息日<首次计费结束日<产品清盘日
//        if(StringUtil.isNotEmpty(entity.getBussFinProductFeeInfoDto())&&!StringUtil.isEmptyList(entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList())){
//        	for(int i=0;i<entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().size();i++){
//        		if(StringUtil.isNotEmpty(entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().get(i).getFirstCalEndDate())){
//	        		if(DateUtil.compareDate(entity.getVdate(), entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().get(i).getFirstCalEndDate()) >= 0){
//	        			//首次计费结束日应该大于等于产品起息日
//	        			validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0109")));
//	        		}
//	        		if(DateUtil.compareDate(entity.getLiquidationDate(), entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList().get(i).getFirstCalEndDate()) <= 0){
//	        			//首次计费结束日应该小于等于产品清盘日
//	        			validList.add(new ValidMessage("", getMessage("FIN_PROD_E_0110")));
//
//	        		}
//        		}
//        	}
//        }
        //add by yejiejie 2019-2-22 end
        //add by yejiejie 2019-3-11 start
        //产品的起息日，到期日不能是节假日
//        if (dateCalService.isDealCalendarHoliday(entity.getVdate(), entity.getCalendarId())) {
//        	validList.add(new ValidMessage("vdate", getMessage("FIN_PROD_VDATE_0001")));
//        }
//        if (dateCalService.isDealCalendarHoliday(entity.getMdate(), entity.getCalendarId())) {
//        	validList.add(new ValidMessage("mdate", getMessage("FIN_PROD_MDATE_0002")));
//        }
        //add by yejiejie 2019-3-11 end
        //合版本 业绩回补相关 start wangcheng 20191025
        //回补
        if(StringUtil.isNotEmpty(entity.getIsAnaplerotic())
                && StringUtil.equals(entity.getIsAnaplerotic(), DictCodeList.是否_是.getValue())){
            if(StringUtil.isEmptyList(entity.getFinProductAnapleroticTabInfoDtoList())){
                validList.add(new ValidMessage(getMessage("FIN_PROD_E_0222")));
            }else{
                // MODIFY BY WJ 20200818 ZGBUGTWO-33
                //系统未校验：产品起息日<=首次回补日<=回补计提结束日<=产品到期日
                // 回补列表只有一条 get0；
                FinProductAnapleroticInfoDto anapleroticInfoDto = entity.getFinProductAnapleroticTabInfoDtoList().get(0);
                Date vdate = entity.getVdate();

                Date firstAnapleroticDate = anapleroticInfoDto.getFirstAnapleroticDate(); // 首次回补日
                Date anapleroticEndDate = anapleroticInfoDto.getAnapleroticEndDate();  // 回补计提结束日

                Date mdate = entity.getMdate();

                if(DateUtil.compareDate(vdate,firstAnapleroticDate) >= 1){
                    validList.add(new ValidMessage(getMessage("FIN_PROD_E_0233",getMessage("FIN_PROD_E_0234"),getMessage("FIN_PROD_E_0235"))));
                }
                if(DateUtil.compareDate(firstAnapleroticDate,anapleroticEndDate) >= 1){
                    validList.add(new ValidMessage(getMessage("FIN_PROD_E_0233",getMessage("FIN_PROD_E_0235"),getMessage("FIN_PROD_E_0236"))));
                }
                if(DateUtil.compareDate(anapleroticEndDate,mdate) >= 1){
                    validList.add(new ValidMessage(getMessage("FIN_PROD_E_0233",getMessage("FIN_PROD_E_0236"),getMessage("FIN_PROD_E_0237"))));
                }
                //首次回补日:若为封闭式产品，则默认为产品到期日-1自然日，可修改为产品到期日，修改为其他日期不允许修改成功
                if (StringUtil.equals(entity.getOperationType(), DictCodeList.开放类型_封闭式.getValue())
                        && !(DateUtil.compareDate(firstAnapleroticDate, mdate) == 0 || DateUtil.compareDate(DateUtil.addDay(mdate, -1), firstAnapleroticDate) == 0)) {
                    validList.add(new ValidMessage("封闭式产品,首次回补日为产品到期日-1自然日或产品到期日"));
                }
            }
            //修改bug 回补规则相关校验 20191101 add by wangcheng 20191101 start
            if(entity.getFinProductAnapleroticTabInfoDtoList() != null&&entity.getFinProductAnapleroticTabInfoDtoList().size()>0){
                for (FinProductAnapleroticInfoDto finProductAnapleroticInfoDto : entity.getFinProductAnapleroticTabInfoDtoList()) {
                    boolean flagFloat = false;
                    boolean flagFee = false;
                    if(!StringUtil.equals(entity.getDealMode(), DictCodeList.处理模式_分档.getValue())){
                        if (entity.getBussFinProductFloatFeeDto() != null) {
                            if(StringUtil.isNotEmpty(finProductAnapleroticInfoDto.getAnapleroticRule())){
                                flagFloat = StringUtil.isNotEmpty(entity.getBussFinProductFloatFeeDto().getFeeId());
                                if(StringUtil.equals(finProductAnapleroticInfoDto.getAnapleroticRule(), DictCodeListExt1.回补规则_仅使用浮动管理费回补.getValue())
                                        && !flagFloat ){
                                    validList.add(new ValidMessage(getMessage("FIN_PROD_E_0219")));
                                }
                            }
                        }
                        if(entity.getBussFinProductFeeInfoDto() != null && StringUtil.isNotEmpty(
                                entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList())){
                            List<String> list = new ArrayList<String>();
                            for (BussFinProductFeeInfoDto bussFinProductFeeInfoDto : entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList()) {
                                list.add(bussFinProductFeeInfoDto.getFeeType());
                            }
                            if(list.contains(DictCodeList.费用品种类型_固定管理费.getValue()) || list.contains(DictCodeList.费用品种类型_固定管理费.getCodeName())){
                                flagFee = true;
                            }
                            if(StringUtil.equals(finProductAnapleroticInfoDto.getAnapleroticRule(), DictCodeListExt1.回补规则_仅使用固定管理费回补.getValue())
                                    && !flagFee){
                                validList.add(new ValidMessage(getMessage("FIN_PROD_E_0220")));
                            }
                        }
                        if(StringUtil.equals(finProductAnapleroticInfoDto.getAnapleroticRule(), DictCodeListExt1.回补规则_优先使用浮动管理费回补.getValue())
                                && !flagFee && !flagFloat){
                            validList.add(new ValidMessage(getMessage("FIN_PROD_E_0221")));
                        }
                    }
                    else{
                        if(StringUtil.equals(finProductAnapleroticInfoDto.getAnapleroticRule(),
                                DictCodeListExt1.回补规则_仅使用浮动管理费回补.getValue())
                                &&!finNetValueProductBuild002Service.checkAnapleroticRule(entity,
                                finProductAnapleroticInfoDto)){
                            validList.add(new ValidMessage(getMessage("FIN_PROD_E_0230",finProductAnapleroticInfoDto.getProdStyle())));
                        }
                        else if(StringUtil.equals(finProductAnapleroticInfoDto.getAnapleroticRule(), DictCodeListExt1.回补规则_仅使用固定管理费回补.getValue())
                                &&!finNetValueProductBuild002Service.checkAnapleroticRule(entity,
                                finProductAnapleroticInfoDto)){
                            validList.add(new ValidMessage(getMessage("FIN_PROD_E_0231",finProductAnapleroticInfoDto.getProdStyle())));
                        }
                        else if(StringUtil.equals(finProductAnapleroticInfoDto.getAnapleroticRule(),
                                DictCodeListExt1.回补规则_优先使用浮动管理费回补.getValue())
                                &&!finNetValueProductBuild002Service.checkAnapleroticRule(entity,
                                finProductAnapleroticInfoDto)){
                            validList.add(new ValidMessage(getMessage("FIN_PROD_E_0232",finProductAnapleroticInfoDto.getProdStyle())));
                        }
//    	    	    	if (!StringUtil.isEmptyList(entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList())) {
//    	    	    		if(StringUtil.equals(finProductAnapleroticInfoDto.getAnapleroticRule(), DictCodeListExt1.回补规则_仅使用浮动管理费回补.getValue())){
//        						flagFloat = StringUtil.isNotEmpty(entity.getBussFinProductFloatFeeDto().getFeeId());
//        						if(StringUtil.equals(finProductAnapleroticInfoDto.getAnapleroticRule(), DictCodeListExt1.回补规则_仅使用浮动管理费回补.getValue())
//        								&& !flagFloat ){
//        							validList.add(new ValidMessage(getMessage("FIN_PROD_E_0219")));
//        						}
//        					}
//        				}
//        				if(entity.getBussFinProductFeeInfoDto() != null && StringUtil.isNotEmpty(
//        						entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList())){
//        					List<String> list = new ArrayList<String>();
//        					for (BussFinProductFeeInfoDto bussFinProductFeeInfoDto : entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList()) {
//        						list.add(bussFinProductFeeInfoDto.getFeeType());
//        					}
//        					if(list.contains(DictCodeList.费用品种类型_固定管理费.getValue()) || list.contains(DictCodeList.费用品种类型_固定管理费.getCodeName())){
//        						flagFee = true;
//        					}
//        					if(StringUtil.equals(finProductAnapleroticInfoDto.getAnapleroticRule(), DictCodeListExt1.回补规则_仅使用固定管理费回补.getValue())
//        							&& !flagFee){
//        						validList.add(new ValidMessage(getMessage("FIN_PROD_E_0220")));
//        					}
//        				}
//        				if(StringUtil.equals(finProductAnapleroticInfoDto.getAnapleroticRule(), DictCodeListExt1.回补规则_优先使用浮动管理费回补.getValue())
//        						&& !flagFee && !flagFloat){
//        					validList.add(new ValidMessage(getMessage("FIN_PROD_E_0221")));
//        				}

                    }
                }
            }
            //修改bug 回补规则相关校验 20191101 add by wangcheng 20191101 end

        }
        //合版本 业绩回补相关 end wangcheng 20191025
        if (StringUtil.isNotEmpty(entity.getProfitType())
                &&  StringUtil.equals(entity.getProfitType(),DictCodeList.收益类型_货币型.getValue())) {
            if (StringUtil.isNotEmpty(entity.getBussFinProductBonusPlanDto()) &&
            		StringUtil.isNotEmpty(entity.getBussFinProductBonusPlanDto().getPayType()) &&
            		StringUtil.equals(entity.getBussFinProductBonusPlanDto().getPayType(),DictCodeList.分红方式_现金分红.getValue())) {

                if (StringUtil.isNotEmpty(entity.getBussFinProductValuationDto()) &&
                		StringUtil.isNotEmpty(entity.getBussFinProductValuationDto().getDealLossMode())&&
                		StringUtil.equals(entity.getBussFinProductValuationDto().getDealLossMode(),
                                DictCodeListExt1.亏损处理方式_缩减当前份额.getValue())) {
                    validList.add(
                        new ValidMessage("bussFinProductValuationDto.dealLossMode", this.getMessage("FIN_PROD_E_0112")));
                }
            }
        }
        //获取对应画面的page add by lilan 20211123
        Page page=CacheUtil.getPage("FinNetValueProductBuild002");
        // 获取page中SYS_ELEMENT_CONTROL表配置的是否展示or隐藏画面元素。如果隐藏则跳过判断，如果展示则进入判断逻辑
        if (null != page && null != page.getElementcontrol("bussRepFinProductInfoCommon") && page.getElementcontrol("bussRepFinProductInfoCommon").getIsShow()) {

          //当发行方式为开放式时，校验产品运作模式不能为封闭式净值型和封闭式非净值型，反之不能为开放式净值型和开放式非净值型
            if(StringUtil.equals(entity.getOperationType(), DictCodeList.开放类型_开放式.getValue())){
                if(StringUtil.equals(entity.getBussRepFinProductInfoDto().getRepOperateMode(),DictCodeListExt2.产品运作模式_封闭式净值型.getValue()) ||
                    StringUtil.equals(entity.getBussRepFinProductInfoDto().getRepOperateMode(), DictCodeListExt2.产品运作模式_封闭式非净值型.getValue())){
                    validList.add(
                        new ValidMessage("bussRepFinProductInfoDto.repOperateMode",this.getMessage("FIN_PROD_E_0114",
                            this.getMessage("FinProductBuild002.operationType"),this.getMessage("BussRepFinProductInfo.repOperateMode"))));
                }
            }
            else if(StringUtil.equals(entity.getOperationType(), DictCodeList.开放类型_封闭式.getValue())){
                if(StringUtil.equals(entity.getBussRepFinProductInfoDto().getRepOperateMode(),DictCodeListExt2.产品运作模式_开放式净值型.getValue()) ||
                    StringUtil.equals(entity.getBussRepFinProductInfoDto().getRepOperateMode(), DictCodeListExt2.产品运作模式_开放式非净值型.getValue())){
                    validList.add(
                        new ValidMessage("bussRepFinProductInfoDto.repOperateMode",this.getMessage("FIN_PROD_E_0115",
                            this.getMessage("FinProductBuild002.operationType"),this.getMessage("BussRepFinProductInfo.repOperateMode"))));
                }
            }

            // add by zcw 中债子画面已经check了 start
    //     // 2019年8月23日 魏鹏 add
    //        //当产品运作模式选择“开放式净值型”和“开放式非净值型”时，开放模式为必填
    //        if (StringUtil.isNotEmpty(entity.getBussRepFinProductInfoDto())) {
    //            if ((DictCodeListExt2.产品运作模式_开放式净值型.getValue().equals(
    //                entity.getBussRepFinProductInfoDto().getRepOperateMode())
    //                 || DictCodeListExt2.产品运作模式_开放式非净值型.getValue().equals(
    //                     entity.getBussRepFinProductInfoDto().getRepOperateMode()))) {
    ////                if (!StringUtil.isNotEmpty(
    ////                    entity.getBussRepFinProductInfoDto().getRepOpenMode())) {
    ////                    validList.add(new ValidMessage("bussRepFinProductInfoDto.repOpenMode",
    ////                        this.getMessage("FIN_PROD_E_0133")));
    ////                }
    //            }
    //            else{
    //                // 选择封闭式净值型和“封闭式非净值型”时，开放模式必须为空
    //                if (StringUtil.isNotEmpty(
    //                        entity.getBussRepFinProductInfoDto().getRepOperateMode()) && StringUtil.isNotEmpty(
    //                    entity.getBussRepFinProductInfoDto().getRepOpenMode())) {
    //                    validList.add(new ValidMessage("bussRepFinProductInfoDto.repOpenMode",
    //                        this.getMessage("FIN_PROD_E_0134")));
    //                }
    //            }
    //        }
            // 2019年8月23日 魏鹏 add end
        // add by zcw 中债子画面已经check了 end
            if (StringUtil.isNotEmpty(entity.getFirstSaleVdate())) {
                if(DateUtil.compareDate(entity.getFirstSaleVdate(),entity.getBussRepFinProductInfoDto().getRepRaisePstrdate()) < 0
                        ||DateUtil.compareDate(entity.getBussRepFinProductInfoDto().getRepRaisePenddate(),entity.getFirstSaleVdate())<0){
                    validList.add(new ValidMessage("firstSaleVdate",this.getMessage("FIN_PROD_E_0226")));
                }
            }

            //目标客户类型
            if ((StringUtil.equals(entity.getDealMode(), DictCodeList.处理模式_周期型.getValue())
                || StringUtil.equals(entity.getDealMode(),DictCodeList.处理模式_普通.getValue()))
                && entity.getBussRepFinProductInfoDto()!=null && !StringUtil.isNotEmpty(entity.getBussRepFinProductInfoDto().getRepInvestorType())) {
                validList.add(new ValidMessage("bussFinProductSaleParamDto.investorType", this.getMessage("BUSS_COM_E_0038",
                    this.getMessage("BussRepFinProductInfo.repInvestorType"))));
            }

            //开放式 的产品 是否设置最短持有期限 和 最短持有期限（天） 必输 yjm start 20190807
            if (StringUtil.isNotEmpty(entity.getBussRepFinProductInfoDto())){
                if (StringUtil.equals(entity.getOperationType(), DictCodeList.开放类型_开放式.getValue())){
                    if (StringUtil.isEmpty(entity.getBussRepFinProductInfoDto().getIsShortTerm())){
                        validList.add(new ValidMessage("bussRepFinProductInfoDto.isShortTerm", this.getMessage("BUSS_COM_E_0038",
                                this.getMessage("BussRepFinProductInfo.isShortTerm"))));
                    }else {
                        //modify by wm 增加只有当是否设置最短持有期限为是时，最短持有期限(天)才必输
                        if (!StringUtil.isNotEmpty(entity.getBussRepFinProductInfoDto().getShortTerm())&&StringUtil.equals(entity.getBussRepFinProductInfoDto().getIsShortTerm(), DictCodeList.是否_中债_是.getValue())){
                            validList.add(new ValidMessage("bussRepFinProductInfoDto.shortTerm", this.getMessage("BUSS_COM_E_0038",
                                    this.getMessage("BussRepFinProductInfo.shortTerm"))));
                        }
                        if (!StringUtil.isNotEmpty(entity.getBussRepFinProductInfoDto().getIsFreeRedem()) && StringUtil.equals(entity.getBussRepFinProductInfoDto().getIsShortTerm(), DictCodeList.是否_中债_是.getValue())){
                            validList.add(new ValidMessage("bussRepFinProductInfoDto.isFreeRedem", this.getMessage("BUSS_COM_E_0038",
                                    this.getMessage("BussRepFinProductInfo.isFreeRedem"))));
                        }
                    }

                }

                if(StringUtil.isNotEmpty(entity.getProfitType()) && StringUtil.isNotEmpty(entity.getOperationType()) && StringUtil.isNotEmpty(entity.getBussRepFinProductInfoDto().getRepOperateMode())){
                    if (StringUtil.equals(entity.getOperationType(), DictCodeList.开放类型_开放式.getValue())){
//                    if (StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_收益型.getValue())){
//                        if (!StringUtil.equals(entity.getBussRepFinProductInfoDto().getRepOperateMode(), DictCodeListExt2.产品运作模式_开放式非净值型.getValue())){
//                            errorList.add(new ValidMessage("bussRepFinProductInfoDto.operationType",this.getMessage("FIN_PROD_E_0301")));
//                        }
//                    }
                        if (StringUtil.equals(entity.getProfitType(), DictCodeList.收益类型_净值型.getValue()) ||
                                StringUtil.equals(entity.getProfitType(), DictCodeList.收益类型_货币型.getValue())){
                            if (!StringUtil.equals(entity.getBussRepFinProductInfoDto().getRepOperateMode(), DictCodeListExt2.产品运作模式_开放式净值型.getValue())){
                                validList.add(new ValidMessage("bussRepFinProductInfoDto.operationType",this.getMessage("FIN_PROD_E_0301")));
                            }
                        }
                    }
                    if (StringUtil.equals(entity.getOperationType(), DictCodeList.开放类型_封闭式.getValue())){
//                    if (StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_收益型.getValue())){
//                        if (!StringUtil.equals(entity.getBussRepFinProductInfoDto().getRepOperateMode(), DictCodeListExt2.产品运作模式_封闭式非净值型.getValue())){
//                            errorList.add(new ValidMessage("bussRepFinProductInfoDto.operationType",this.getMessage("FIN_PROD_E_0301")));
//                        }
//                    }
                        if (StringUtil.equals(entity.getProfitType(), DictCodeList.收益类型_净值型.getValue()) ||
                                StringUtil.equals(entity.getProfitType(), DictCodeList.收益类型_货币型.getValue())){
                            if (!StringUtil.equals(entity.getBussRepFinProductInfoDto().getRepOperateMode(), DictCodeListExt2.产品运作模式_封闭式净值型.getValue())){
                                validList.add(new ValidMessage("bussRepFinProductInfoDto.operationType",this.getMessage("FIN_PROD_E_0301")));
                            }
                        }
                    }
                }
            }
            
            
        }
        //到期日与清盘日判断
        if(StringUtil.isNotEmpty(entity.getLiquidationDate())&&StringUtil.isNotEmpty(entity.getMdate())){
        	if(entity.getLiquidationDate().compareTo(entity.getMdate())<0){
        		validList.add(new ValidMessage("liquidationDate", this.getMessage("FIN_PROD_E_0127",
        				this.getMessage("FinProductBuild002.liquidationDate"),this.getMessage("FinProductBuild002.mdate"))));
        	}
        }
        //分层
        if (!StringUtil.isEmptyList(entity.getBussFinProductLayeredInfoDto())&&StringUtil.equals(DictCodeList.处理模式_分档.getValue(), entity.getDealMode())) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < entity.getBussFinProductLayeredInfoDto().size(); i++) {
                //产品款型 必填
                if(!StringUtil.isNotEmpty(entity.getBussFinProductLayeredInfoDto().get(i).getProdStyle())){
                    validList.add(new ValidMessage("", this.getMessage("BUSS_COM_E_0038",
                        this.getMessage("bussFinProductLayeredInfoDto.finProductLayeredType"))));
                } else {
                    BussFinProductLayeredInfoDto dto = (BussFinProductLayeredInfoDto) map.get(entity.getBussFinProductLayeredInfoDto().get(i).getProdStyle());
                    if(dto == null) {
                        map.put(entity.getBussFinProductLayeredInfoDto().get(i).getProdStyle(), entity.getBussFinProductLayeredInfoDto().get(i));
                    } else {
                        validList.add(new ValidMessage("", this.getMessage("FIN_PROD_E_0125",
                                this.getMessage(entity.getBussFinProductLayeredInfoDto().get(i).getProdStyle()))));
                    }
                }
                //销售渠道 必填
                if(!StringUtil.isNotEmpty(entity.getBussFinProductLayeredInfoDto().get(i).getSaleChannel())){
                    validList.add(new ValidMessage("", this.getMessage("BUSS_COM_E_0038","分层产品信息列表中的销售渠道")));
                }


                //目标客户类型 必填
                if(!StringUtil.isNotEmpty(entity.getBussFinProductLayeredInfoDto().get(i).getTarCustomerType())){
                    validList.add(new ValidMessage("", this.getMessage("BUSS_COM_E_0038",
                        this.getMessage("bussFinProductLayeredInfoDto.finProductLayeredRepInvestorType"))));
                }

            }
        } else {
            //净值型和货币型的限制
                //销售渠道
            if(!StringUtil.isNotEmpty(entity.getBussFinProductSaleParamDto().getSaleChannel())) {
            	validList.add(new ValidMessage("bussFinProductSaleParamDto.saleChannel", getMessage("BUSS_COM_E_0038",
                        getMessage("BussFinProductSaleParam.saleChannel"))));
            }


        }
        //针对业绩基准列表款型与投资周期不能重复校验 start 2019-10-12
    	if(entity.getBussBenchMarkLayeredInfoDto()!=null
    			&&!StringUtil.isEmptyList(entity.getBussBenchMarkLayeredInfoDto().getBussBenchMarkLayeredInfoDtoList())){
    		List<BussBenchMarkLayeredInfoDto> benchList = entity.getBussBenchMarkLayeredInfoDto().getBussBenchMarkLayeredInfoDtoList();
    		if(StringUtil.equals(DictCodeList.处理模式_分档.getValue(), entity.getDealMode())){
    			Map<String, BussBenchMarkLayeredInfoDto> benchMap = benchList.stream().collect(
                        Collectors.toMap(BussBenchMarkLayeredInfoDto:: getProdStyle, a -> a, (k1, k2) -> k1));
    			if (benchMap.size() != benchList.size()) {
                  validList.add(new ValidMessage("bussBenchMarkLayeredInfoDto.bussBenchMarkLayeredInfoDtoList",
                      "不能针对同一款型多次设置业绩基准"));
              }
    		}
    		else if(StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode())){
    			Map<String, BussBenchMarkLayeredInfoDto> benchMap = benchList.stream().collect(
                        Collectors.toMap(BussBenchMarkLayeredInfoDto:: getInvestmentCycle, a -> a, (k1, k2) -> k1));
    			if (benchMap.size() != benchList.size()) {
                  validList.add(new ValidMessage("bussBenchMarkLayeredInfoDto.bussBenchMarkLayeredInfoDtoList",
                      "不能针对同一周期多次设置业绩基准"));
              }
    		}
    	}
    	//针对业绩基准列表款型与投资周期不能重复校验 end 2019-10-12
    	//针对浮动管理费与业绩基准
    	if(entity.getBussBenchMarkFloatInfoDto()!=null&&entity.getBussFinProductFloatFeeDto()!= null){
    		if(StringUtil.isNotEmpty(entity.getBussFinProductFloatFeeDto().getFeeId())
    				&&StringUtil.isEmpty(entity.getBussBenchMarkFloatInfoDto().getRateType())){
    			validList.add(new ValidMessage("bussBenchMarkFloatInfoDto.rateType", getMessage("BUSS_COM_E_0038",
                        getMessage("BussBenchMarkInfo.rateType"))));
    		}
    		else if(StringUtil.isEmpty(entity.getBussFinProductFloatFeeDto().getFeeId())
    				&&StringUtil.isNotEmpty(entity.getBussBenchMarkFloatInfoDto().getRateType())){
    			validList.add(new ValidMessage("bussFinProductFloatFeeDto.feeId", getMessage("BUSS_COM_E_0038",
                        getMessage("BussFinProductFloatFee.feeName"))));
    		}
    	}
    	//投资周期校验
    	if(StringUtil.equals(DictCodeListExt2.期限类型_多固定期限.getValue(), entity.getPeriod())){
    		if(StringUtil.isEmptyList(entity.getBussFinProductInvestPeriodDto().getBussFinProductInvestPeriodDtoList())){
    			validList.add(new ValidMessage("bussFinProductInvestPeriodDto.bussFinProductInvestPeriodDtoList", "固定期限时，投资周期列表不能为空"));
    		}
    		else{
    			List<BussFinProductInvestPeriodDto>  periodList = entity.getBussFinProductInvestPeriodDto().getBussFinProductInvestPeriodDtoList();
    			Map<Integer, BussFinProductInvestPeriodDto> periodMap = periodList.stream().collect(
                        Collectors.toMap(BussFinProductInvestPeriodDto:: getInvestDays, a -> a, (k1, k2) -> k1));
    			if (periodMap.size() != periodList.size()) {
                    validList.add(new ValidMessage("bussFinProductInvestPeriodDto.bussFinProductInvestPeriodDtoList",
                        "不能设置相同的投资周期"));
                }
    		}
    	}
    	//产品经理不能为空校验
    	if(StringUtil.isEmpty(entity.getProdManager())){
			validList.add(new ValidMessage("prodManager", getMessage("BUSS_COM_E_0038",
                    getMessage("FinProductBuild002.prodManager"))));
		}
    	//行内标识码在产品申报要素配置为分开的时候必填
    	if(StringUtil.isEmpty(entity.getRepFinprodInsId())
    			&&StringUtil.equals(DictCodeList.是否_否.getValue(), CacheUtil.getSysParam(BussinessConstant.PRODUCTT_IS_MERGER))){
    		validList.add(new ValidMessage("repFinprodInsId", getMessage("BUSS_COM_E_0038",
                    getMessage("ProductReportRegister001.repFinprodInsId"))));
    	}

    	//是否轧差清算校验
    	if(StringUtil.isEmpty(entity.getLiquidationYesno())) {
    		validList.add(new ValidMessage("liquidationYesno", getMessage("BUSS_COM_E_0038",
                    getMessage("FinProductBuild002.liquidationYesno"))));
    	}
    	//产品设计系列不为空  yzg 20191211 start
    	PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("FinNetValueProductBuild002");
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        if(isEleShow != null && StringUtil.isNotEmpty(isEleShow.get("FinNetValueProductBuild002.prdDesignId")) && StringUtil.equals(isEleShow.get("FinNetValueProductBuild002.prdDesignId"), "Y")) {
	    	if(StringUtil.isEmpty(entity.getPrdDesignId())) {
	    		validList.add(new ValidMessage("prdDesignId", getMessage("BUSS_COM_E_0038",
	                    getMessage("FinProductBuild002.prdDesignId"))));
	    	}
        }
    	//产品设计系列不为空  yzg 20191211 end
        //modify by mashukai 2210609 产品运作模式在中债报送校验处已经进行了校验，这里注释掉重复的校验
//    	//产品运作模式不为空
//    	if(entity.getBussRepFinProductInfoDto()!=null && StringUtil.isEmpty(entity.getBussRepFinProductInfoDto().getRepOperateMode())) {
//    		validList.add(new ValidMessage("bussRepFinProductInfoDto.repOperateMode",getMessage("BUSS_COM_E_0038",
//	                getMessage("BussRepFinProductInfo.repOperateMode"))));
//    	}
    	//开放模式01有规律开放时规律开放周期必填
//    	if("01".equals(entity.getBussRepFinProductInfoDto().getRepOpenMode()) &&
//    			!StringUtil.isNotEmpty(entity.getBussRepFinProductInfoDto().getRepOpenCircle())) {
//    		validList.add(new ValidMessage("bussRepFinProductInfoDto.repOpenCircle","开放模式01有规律开放时规律开放周期必填"));
//    	}
    	//开放模式02无规律开放时无规律开放说明必填
//    	if("02".equals(entity.getBussRepFinProductInfoDto().getRepOpenMode()) &&
//    			!StringUtil.isNotEmpty(entity.getBussRepFinProductInfoDto().getRepRuleComment())) {
//    		validList.add(new ValidMessage("bussRepFinProductInfoDto.repRuleComment","开放模式02无规律开放时无规律开放说明必填"));
//    	}
    	//周期型和普通型业绩基准不能为空
    	if (StringUtil.isNotEmpty(entity.getDealMode())){
    		if(StringUtil.equals(entity.getDealMode(), DictCodeList.处理模式_周期型.getValue()) ||
    				StringUtil.equals(entity.getDealMode(), DictCodeList.处理模式_普通.getValue())){
    			if(StringUtil.isNotEmpty(entity.getBussBenchMarkInfoDto()) && !StringUtil.isNotEmpty(entity.getBussBenchMarkInfoDto().getPublishType())) {
    				validList.add(new ValidMessage("bussBenchMarkInfoDto.publishType", getMessage("BUSS_COM_E_0038",
    	                    getMessage("BussBenchMarkInfo.publishType"))));
    			}
    		}else if(StringUtil.equals(entity.getDealMode(), DictCodeList.处理模式_分档.getValue())) {
    			if(StringUtil.isEmptyList(entity.getBussBenchMarkLayeredInfoDto().getBussBenchMarkLayeredInfoDtoList())) {
    				validList.add(new ValidMessage(getMessage("BUSS_COM_E_0038",
    	                    getMessage("BussBenchMarkInfo"))));
    			}
    		}
    	}
    	//认购起点 addby cxq 20200109 解决 OPEX-763
    	if(!StringUtil.isNotEmpty(entity.getBussFinProductSaleParamDto().getToffStart())) {
    		validList.add(new ValidMessage("bussFinProductSaleParamDto.toffStart", getMessage("BUSS_COM_E_0038",
    				getMessage("BussFinProductSaleParam.toffStart"))));
    	}else if(entity.getBussFinProductSaleParamDto().getToffStart().compareTo(BigDecimal.ZERO)==0){
            validList.add(new ValidMessage("bussFinProductSaleParamDto.toffStart",
                    getMessage("BUSS_COM_E_0036", getMessage("BussFinProductSaleParam.toffStart"),"0")));
    	}
    	/**20200319 taoran add 同业机构报错后输入框描红*/
    	if (StringUtil.isNotEmpty(entity.getBussFinProductSaleParamDto().getInvestorType()) &&
    			entity.getBussFinProductSaleParamDto().getInvestorType().indexOf( DictCodeList.目标客户类型_金融同业专属.getValue())!=-1
         		&& !StringUtil.isNotEmpty(entity.getBussFinProductSaleParamDto().getSameOrg())) {
    		validList.add(new ValidMessage("sameOrg", this.getMessage("BUSS_COM_E_0038",
                 this.getMessage("BussFinProductSaleParam.sameOrg"))));
    	}
    	/**20200319 taoran add 同业机构报错后输入框描红*/

         validList = chbInvDataCheckService.chbCheckCommon(validList, entity, "FinNetValueProductBuild002", null);

         //同一数据行：开放开始日小于等于开放结束日，开放结束日小于等于开放确认日
        if (entity.getBussFinProductOpenPlanDto() != null) {
            List<FinOpenPlan> finOpenPlanListTemp = entity.getBussFinProductOpenPlanDto().getFinOpenPlanList();
            if (!StringUtil.isEmptyList(finOpenPlanListTemp)){
                for (FinOpenPlan finOpenPlan : finOpenPlanListTemp) {
                    if(!(finOpenPlan.getOpenDateStr().compareTo(finOpenPlan.getOpenDateEnd())<=0 && finOpenPlan.getOpenDateEnd().compareTo(finOpenPlan.getOpenVdate())<=0)){
                        validList.add(new ValidMessage("开放计划中开放开始日必须小于等于开放结束日，开放结束日必须小于等于开放确认日"));
                        break;
                    }
                }
            }
        }
        return validList;
    }

}
