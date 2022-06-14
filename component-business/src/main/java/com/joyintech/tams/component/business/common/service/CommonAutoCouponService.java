/*
 * 文件名：CommonAutoCouponService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年1月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.common.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.MstCustomerInfoAddMapper;
import com.joyintech.tams.common.base.dao.MstCustomerInfoMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.report.dto.BaseRepAssetEquityInfoDto;
import com.joyintech.tams.component.business.asset.dto.FinPayInterestStdInfoDto;
import com.joyintech.tams.component.business.common.constant.FundDateType;
import com.joyintech.tams.component.business.common.dto.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.report.asset.bond.dto.BussFinBondRepInfoDto;
import com.joyintech.tams.component.business.report.asset.fund.dto.BussRepFundInfoDto;
import com.joyintech.tams.component.business.report.asset.project.dto.BussRepEquityInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.calculator.AdjustedTradeDayCal;
import com.joyintech.tams.invest.common.calculator.AssetInterestCal;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.xmlrelation.XmlRelationInputParaDto;
import com.joyintech.tams.invest.common.dto.xmlrelation.XmlRelationOutputDto;
import com.joyintech.tams.invest.common.service.InvDataCheckService;
import com.joyintech.tams.invest.common.util.xmlrelation.XmlRelationUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 自动加券
 * 〈功能详细描述〉
 *
 * @author zhangnian
 * @version 1.0 2019年1月14日
 * @see CommonAutoCouponService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
public class CommonAutoCouponService extends BaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private BaseGetBusinessId bussGetBussinessIdService;

    @Autowired
    private MstCustomerInfoMapper mstCustomerInfoMapper;
    @Autowired
    private MstCustomerInfoAddMapper mstCustomerInfoAddMapper;

    /**
     * 付息计划，开放计划，利率重置，申购计划，赎回计划计算用和日期关联的Service
     */
    @Autowired
    private DateCalService dateCalService;

    @Autowired
    private InvDataCheckService chbInvDataCheckService;

    /**
     * 获取wind资产信息(债券)
     * <功能详细描述>
     *
     * @return
     * @see
     * @since 1.0
     */
    public List<InterfPayhsrcsecinfoDto> selectWindBondAsset(Map<String, Object> paramMap) {
        return restTemplate.postForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectWindBondAsset", paramMap,
                InterfPayhsrcsecinfoDto.class);
    }

    /**
     * 获取wind资产信息(基金)
     * <功能详细描述>
     *
     * @return
     * @see
     * @since 1.0
     */
    public List<InfChinamutualfunddescriptionDto> selectWindFundAsset(Map<String, Object> paramMap) {
        return restTemplate.postForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectWindFundAsset", paramMap,
                InfChinamutualfunddescriptionDto.class);
    }

    /**
     * 获取wind资产信息(债券)
     * <功能详细描述>
     *
     * @param secid
     * @return
     * @see
     * @since 1.0
     */
    public InterfPayhsrcsecinfoDto selectWindBondAssetBySecId(String secid) {
        return restTemplate.getForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectWindBondAssetBySecId?secid=" + secid,
                InterfPayhsrcsecinfoDto.class);
    }

    /**
     * WIND_中国债券特殊条款
     * <功能详细描述>
     *
     * @param sInfoWindcode
     * @return
     * @see
     * @since 1.0
     */
    public InterfCbondspecialconditions selectInterfCbondspecialconditions(String sInfoWindcode) {
        return restTemplate.getForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectInterfCbondspecialconditions?sInfoWindcode=" + sInfoWindcode,
                InterfCbondspecialconditions.class);
    }

    /**
     * WIND_浮息债报价表
     * <功能详细描述>
     *
     * @param sInfoWindcode
     * @return
     * @see
     * @since 1.0
     */
    public List<InterfFloatcouprate> selectInterfFloatingcouponsrate(String sInfoWindcode) {
        return restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectInterfFloatingcouponsrate?sInfoWindcode=" + sInfoWindcode,
                InterfFloatcouprate.class);
    }

    /**
     * Wind 债券分类板块
     * <功能详细描述>
     *
     * @param sInfoWindcode
     * @return
     * @see
     * @since 1.0
     */
    public InterfCbondindustrywind selectInterfCbondindustrywind(String sInfoWindcode) {
        return restTemplate.getForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectInterfCbondindustrywind?sInfoWindcode=" + sInfoWindcode,
                InterfCbondindustrywind.class);
    }

    /**
     * 中国债券基本资料
     * <功能详细描述>
     *
     * @param sInfoWindcode
     * @return
     * @see
     * @since 1.0
     */
    public InterfCbonddescription selectInterfCbonddescription(String sInfoWindcode) {
        return restTemplate.getForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectInterfCbonddescription?sInfoWindcode=" + sInfoWindcode,
                InterfCbonddescription.class);
    }

    /**
     * 获取wind资产信息(基金)
     * <功能详细描述>
     *
     * @param fInfoWindcode
     * @return
     * @see
     * @since 1.0
     */
    public InterfCmfunddescrip selectWindFundAssetByfInfoWindcode(String fInfoWindcode) {
//        return restTemplate.getForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectWindFundAssetByfInfoWindcode?fInfoWindcode="+fInfoWindcode,
//            InfChinamutualfunddescriptionDto.class);
        /*--modify by shenchen 2019-04-11 基金基本资料实体类更换--*/
        return restTemplate.getForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectWindFundAssetByfInfoWindcode?fInfoWindcode=" + fInfoWindcode,
                InterfCmfunddescrip.class);
    }


    /**
     * wind债券行权计划
     * <功能详细描述>
     *
     * @param secid 债券id
     * @return
     * @see
     * @since 1.0
     */
    public List<InterfPayhsrcsecoption> selectWindBondExercise(String secid) {
        return restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectWindBondExercise?secid=" + secid,
                InterfPayhsrcsecoption.class);
    }

    /**
     * wind债券付息计划
     * <功能详细描述>
     *
     * @param secid
     * @return
     * @see
     * @since 1.0
     */
    public List<InterfPayhsrcsecschedule> selectWindBondSchedule(String secid) {
        return restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectWindBondSchedule?secid=" + secid,
                InterfPayhsrcsecschedule.class);
    }

    /**
     * 获取中国货币市场基金份额结转方式
     * <功能详细描述>
     *
     * @param fInfoWindcode
     * @return
     * @see
     * @since 1.0
     */
    public InfCmoneymarketfscarryoverm selectWindFundMoneymarketfscarryoverm(String fInfoWindcode) {
        return restTemplate.getForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectWindFundMoneymarketfscarryoverm?fInfoWindcode=" + fInfoWindcode,
                InfCmoneymarketfscarryoverm.class);
    }

    /**
     * 自动加券(债券)保存
     * <功能详细描述>
     *
     * @param dto
     * @see
     * @since 1.0
     */
    public void autoBondCouponSave(InterfPayhsrcsecinfoDto dto) {
        restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/autoBondCoupon", dto,
                Integer.class);
    }

    /**
     * 自动加券(基金)保存
     * <功能详细描述>
     *
     * @param dto
     * @see
     * @since 1.0
     */
    public void autoFundCouponSave(InfChinamutualfunddescriptionDto dto) {
        restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/autoFundCoupon", dto,
                Integer.class);
    }

    /**
     * 自动加券(债券) 封装dto
     *
     * @param dto
     * @param finprodMarketId
     * @return
     * @see
     * @since 1.0
     */
    public InterfPayhsrcsecinfoDto autoBondCoupon(InterfPayhsrcsecinfoDto dto, String finprodMarketId) {

        List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList = new ArrayList<BaseFinProductBaseInfoDto>();
        Long branch = StringUtil.isEmpty(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)) ? 0 : Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (null != dto.getSecidList() && !dto.getSecidList().isEmpty()) {
            for (String secid : dto.getSecidList()) {
                BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
                //WIND_债券基本信息数据 
                InterfPayhsrcsecinfoDto interfPayhsrcsecinfo = selectWindBondAssetBySecId(secid);
                //WIND_债券行权信息数据
                List<InterfPayhsrcsecoption> payhsrcsecoptionList = selectWindBondExercise(secid);
                //WIND_债券付息计划数据
                List<InterfPayhsrcsecschedule> interfPayhsrcsecscheduleList = selectWindBondSchedule(secid);
                //WIND_中国债券特殊条款
                //InterfCbondspecialconditions interfCbondspecialconditions = selectInterfCbondspecialconditions(secid);//中国债券特殊条款注释
                //WIND_浮息债报价表
                List<InterfFloatcouprate> interfFloatingcouponsrateList = selectInterfFloatingcouponsrate(secid);
                //Wind 债券分类板块(注释  modify by shenchen 2019-04-19)
                //InterfCbondindustrywind interfCbondindustrywind = selectInterfCbondindustrywind(secid);
                //中国债券基本资料
                InterfCbonddescription interfCbonddescription = selectInterfCbonddescription(secid);
                if (!StringUtil.isNotEmpty(interfPayhsrcsecinfo)) {
                    return null;
                }
                //交易日历
                List<MstCalendarInfo> mstCalendarInfoList = getMstCalendarInfo();
                //new标的类金融产品
                FinProduct finProduct = new FinProduct();
                String finprodId = commonBusinessService.getFinProdId(interfPayhsrcsecinfo.getSecid(), BussinessDictCodeList.金融产品类型_投管用_债券.getValue(), StringUtil.EMPTY);
                finProduct.setFinprodId(finprodId);
                finProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                finProduct.setFinprodAbbr(interfPayhsrcsecinfo.getSecname());
                finProduct.setFinprodName(interfPayhsrcsecinfo.getSecfullname());
                finProduct.setProfitType(DictCodeList.收益类型_收益型.getValue());
                /**--息票品种modify 2019-04-15 start--**/
//              inputParaDto.setDictCode(BussinessDictCode.wind息票品种.getValue());
//              inputParaDto.setItemCode(interfPayhsrcsecinfo.getCouponspecies());
//              finProduct.setCouponSpecies(windCovCom(inputParaDto));
                finProduct.setCouponSpecies(interfPayhsrcsecinfo.getCouponspecies());
                /**--息票品种modify 2019-04-15 end--**/
                finProduct.setFinprodMarketId(interfPayhsrcsecinfo.getSecid());
                finProduct.setIssuePrice(interfPayhsrcsecinfo.getIssueprice());
                //发行金额除以万元
                if (StringUtil.isNotEmpty(interfPayhsrcsecinfo.getIssueamt())) {
                    finProduct.setIssueAmt(interfPayhsrcsecinfo.getIssueamt().divide(new BigDecimal(10000)));
                } else {
                    finProduct.setIssueAmt(null);
                }

                finProduct.setCcy(interfPayhsrcsecinfo.getCcy());
                finProduct.setBlnArea(interfPayhsrcsecinfo.getBlnarea());//境内外
                /**--交易场所modify 2019-04-15 start--**/
//              inputParaDto.setDictCode(DictCode.交易场所_WIND.getValue());
//              inputParaDto.setItemCode(interfPayhsrcsecinfo.getMarket());
//              finProduct.setTradeMarket(windCovCom(inputParaDto));
                finProduct.setTradeMarket(interfPayhsrcsecinfo.getMarket());
                /**--交易场所modify 2019-04-15 end--**/
                finProduct.setIssuer(getIssuer(interfPayhsrcsecinfo));
                //2019年5月27日  交易日历
                if (StringUtil.equals(DictCodeListExt1.交易场所_银行间.getValue(), interfPayhsrcsecinfo.getMarket())) {
                    finProduct.setCalendarId("03");//银行间
                } else if (StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), interfPayhsrcsecinfo.getMarket())) {
                    finProduct.setCalendarId("04");//上交所
                } else if (StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), interfPayhsrcsecinfo.getMarket())) {
                    finProduct.setCalendarId("05");//深交所
                } else if (StringUtil.equals(DictCodeListExt1.交易场所_机构间私募系统.getValue(), interfPayhsrcsecinfo.getMarket())) {
                    finProduct.setCalendarId("01");//中国法定节假日
                }
                for (MstCalendarInfo mstCalendarInfo : mstCalendarInfoList) {
                    //第三方日历编号等于市场  -对应的日历编号
                    if (StringUtil.equals(mstCalendarInfo.getOtherId(), interfPayhsrcsecinfo.getMarket())) {
                        finProduct.setCalendarId(mstCalendarInfo.getCalendarId());
                        break;
                    }
                }

                //资产支持证券:默认非保  其他债券类型默认保本
                /*if(BussinessDictCodeList.债券一级分类_资产支持证券.getValue().equals(interfPayhsrcsecinfo.getPbocsectype())){
                    finProduct.setCapiIncomeFeature(DictCodeListExt1.本金收益特征_非保本.getValue());//保本标志
                }else{
                    finProduct.setCapiIncomeFeature(DictCodeList.产品收益标识_保证收益.getValue());
                }*/
//                if(null!=interfPayhsrcsecinfo && null!=interfPayhsrcsecinfo.getIssuetype()){
//                    if(interfPayhsrcsecinfo.getIssuetype().startsWith("43901")){
//                        finProduct.setIssueType(BussinessDictCodeList.发行方式_公开发行.getValue()); 
//                    }else{
//                        finProduct.setIssueType(BussinessDictCodeList.发行方式_非公开发行.getValue());
//                    }
//                }
                finProduct.setIssueType(interfPayhsrcsecinfo.getIssuetype());//发行方式
                /*--发行方式--*/
                finProduct.setVdate(interfPayhsrcsecinfo.getVdate());
                finProduct.setMdate(interfPayhsrcsecinfo.getMdate());
                /*---发行截止日 addby shenchen 2019-05-05----*/
                finProduct.setIdate(interfPayhsrcsecinfo.getPubenddate());
                /*---发行截止日 addby shenchen 2019-05-05----*/
                if (StringUtil.isNotEmpty(interfPayhsrcsecinfo.getVdate()) && StringUtil.isNotEmpty(interfPayhsrcsecinfo.getMdate())) {
                    finProduct.setTermDays(DateUtil.getBetweenDayNumber(DateUtil.format(interfPayhsrcsecinfo.getVdate()), DateUtil.format(interfPayhsrcsecinfo.getMdate())));
                }
//                finProduct.setDelistingDate();
                finProduct.setIsChl(DictCodeList.是否_否.getValue());
                /**--add by shenchen 2019-04-10--*/
                // finProduct.setIsSus(interfPayhsrcsecinfo.getIssus());//是否永续
                /**--add by shenchen 2019-04-10--*/
                /**--add by shenchen 2019-04-10--*/
//                if(StringUtil.equals(interfPayhsrcsecinfo.getIsredemption(), BussinessDictCodeList.是否_是.getValue())
//                    ||StringUtil.equals(interfPayhsrcsecinfo.getIsprecash(), BussinessDictCodeList.是否_是.getValue())){
                if (StringUtil.equals(interfPayhsrcsecinfo.getCalloption(), DictCodeList.是否_是.getValue())
                        || StringUtil.equals(interfPayhsrcsecinfo.getPutoption(), DictCodeList.是否_是.getValue())) {
                    /**--add by shenchen 2019-04-10--*/
                    finProduct.setIsRight(DictCodeList.是否_是.getValue());
                } else {
                    finProduct.setIsRight(DictCodeList.是否_否.getValue());
                }
                finProduct.setRegistOrg(interfPayhsrcsecinfo.getInstitution());
                // add by lizhi 20190603 是否永续债,Y是N否 start
                finProduct.setIsSus(interfPayhsrcsecinfo.getIssus());
                finProduct.setDelistingDate(interfPayhsrcsecinfo.getDelistingDate());
                // add by lizhi 20190603 是否永续债,Y是N否 end
                baseFinProductBaseInfoDto.setFinProduct(finProduct);
                //new标的类金融产品附属
                FinProductAdd finProductAdd = new FinProductAdd();
                finProductAdd.setFinprodId(finprodId);
                finProductAdd.setWindId(secid);
                finProductAdd.setIsAutoUpdate(DictCodeList.是否_是.getValue());
                // add by lizhi 20190603 面值 start
                finProductAdd.setFaceValue(interfPayhsrcsecinfo.getFacevalue());
                // add by lizhi 20190603 面值 end
                //是否次级债
                finProductAdd.setIsSecBond(interfPayhsrcsecinfo.getIssecbond());
                //是否城投债
                finProductAdd.setIsCityBond(interfPayhsrcsecinfo.getCityInvestSecWind());
                // 20200819 债券自动加券优化-判断城投债级别 wangwei start
                // 城投债的场合需要判断城投债级别
                if (StringUtil.isNotEmpty(finProductAdd.getIsCityBond())
                        && StringUtil.equals(DictCodeList.是否_是.getValue(), finProductAdd.getIsCityBond())) {
                    if (StringUtil.isNotEmpty(interfPayhsrcsecinfo.getCityInvestSecCounty())) {
                        // 若接口表中城投债县及县级市字段不为空，则城投债级别为县及县级市
                        finProductAdd.setCityBondLev("03");
                    } else if (StringUtil.isNotEmpty(interfPayhsrcsecinfo.getCityInvestSecCities())) {
                        // 若接口表中城投债县及县级市字段不为空，则城投债级别为县及县级市
                        finProductAdd.setCityBondLev("02");
                    } else if (StringUtil.isNotEmpty(interfPayhsrcsecinfo.getCityInvestSecProvinces())) {
                        // 其他场合城投债级别为省及省会（单列市）
                        finProductAdd.setCityBondLev("01");
                    }
                }
                // 20200819 债券自动加券优化-判断城投债级别 wangwei end
                finProductAdd.setIsNetprice(interfPayhsrcsecinfo.getIsNetprice());
                finProductAdd.setCrossMarketCode(interfPayhsrcsecinfo.getCrossMarketCode());
                baseFinProductBaseInfoDto.setFinProductAdd(finProductAdd);
                //new金融产品分类
                FinProductType finProductType = new FinProductType();
                /**--add by shenchen 2019-04-10--*/
                //finProductType.setType4(interfCbondindustrywind.getsInfoIndustrycode());//债券分类板块注释
                //finProductType.setType5(interfCbondindustrywind.getsInfoIndustrycode2());
                finProductType.setType4(interfPayhsrcsecinfo.getSectype());//取债券资料表系统债券类型
                finProductType.setType5(interfPayhsrcsecinfo.getSectype2());//取债券资料表系统债券类型2
                finProductType.setType6(interfPayhsrcsecinfo.getSectype());//取债券资料表系统债券类型
                finProductType.setType7(interfPayhsrcsecinfo.getSectype2());//取债券资料表系统债券类型2

                FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
                finProductTypeConfig.setInputValue1(DictCodeListExt1.金融产品类型_债券.getValue());
                finProductTypeConfig.setInputValue2(interfPayhsrcsecinfo.getSectype());
                finProductTypeConfig.setInputValue3(interfPayhsrcsecinfo.getSectype2());
                //金融债的时候判断是否次级债
                if (StringUtil.equals(interfPayhsrcsecinfo.getSectype(),
                        DictCodeList.WIND一级分类_含债券一级分类等_金融债.getValue())) {
                    finProductTypeConfig.setInputValue4(interfPayhsrcsecinfo.getIssecbond());
                }
                //同业存单的时候五级是否他行
                if (StringUtil.equals(interfPayhsrcsecinfo.getSectype(),
                        DictCodeList.WIND一级分类_含债券一级分类等_同业存单.getValue())) {
                    //                //判断是否是本行存单，若是则显示本行，否则显示他行
                    if (StringUtil.equals(interfPayhsrcsecinfo.getAssetClass(), "2501")) {
                        finProductTypeConfig.setInputValue4(DictCodeList.是否_否.getValue());
                    } else if (StringUtil.equals(interfPayhsrcsecinfo.getAssetClass(), "2502")) {
                        finProductTypeConfig.setInputValue4(DictCodeList.是否_是.getValue());
                    } else {
                        finProductTypeConfig.setInputValue4(DictCodeList.是否_否.getValue());
                    }
                }

                finProductTypeConfig = restTemplate.postForObject(ServiceApplication.ASSET_SERVICE_NAME + "/AssetCommon/getAssetThreeType",
                        finProductTypeConfig, FinProductTypeConfig.class);
                finProductType.setType9(finProductTypeConfig.getInputValue2());//取债券资料表系统债券类型
                finProductType.setType10(finProductTypeConfig.getInputValue3());//取债券资料表系统债券类型2
                finProductType.setType11(finProductTypeConfig.getInputValue4());
                finProductType.setType12(finProductTypeConfig.getInputValue5());


                finProductType.setType8(DictCodeListExt1.金融产品类型_债券.getValue());//type8
                /**--add by shenchen 2019-04-10--*/
                finProductType.setFinprodId(finprodId);
                finProductType.setBranch(branch);
                baseFinProductBaseInfoDto.setFinProductType(finProductType);
                //new现金流信息
                List<FinCashInfo> finCashInfoList = new ArrayList<FinCashInfo>();
                //现金流信息Entity-本金
                FinCashInfo finCashInfoPrincipal = encapFinCashInfo(finprodId, DictCodeListExt1.现金流一级类型_本金.getValue(), DictCodeListExt1.现金流二级类型_本金.getValue(), branch);
                //现金流信息Entity-利息
                FinCashInfo finCashInfoInterest = encapFinCashInfo(finprodId, DictCodeListExt1.现金流一级类型_利息.getValue(), DictCodeListExt1.现金流二级类型_利息.getValue(), branch);
                // add by lizhi 20190603 币种 start
                finCashInfoPrincipal.setCcy(interfPayhsrcsecinfo.getCcy());
                finCashInfoInterest.setCcy(interfPayhsrcsecinfo.getCcy());
                // add by lizhi 20190603 币种 end
                finCashInfoList.add(finCashInfoPrincipal);
                finCashInfoList.add(finCashInfoInterest);
                baseFinProductBaseInfoDto.setFinCashInfoList(finCashInfoList);

                //new现金流计算规则
                List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<FinCashCalcRule>();
                for (InterfFloatcouprate interfFloatingcouponsrate : interfFloatingcouponsrateList) {
                    FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
                    finCashCalcRule.setCashId(finCashInfoInterest.getCashId());
                    finCashCalcRule.setEffDate(interfFloatingcouponsrate.getChangeDt());//变动日期
                    finCashCalcRule.setCalcType(interfPayhsrcsecinfo.getInterestrate());
                    finCashCalcRule.setBaseType(BussinessDictCodeList.基数类型_存续本金.getValue());
                    finCashCalcRule.setBaseDateType(BussinessDictCodeList.基数日期类型_当日.getValue());
                    /**--modify by shenchen 2019-04-19--**/
                    //              inputParaDto.setDictCode(BussinessDictCode.wind计息基础.getValue());
                    //              inputParaDto.setItemCode(interfPayhsrcsecinfo.getRatebasic());
                    finCashCalcRule.setBasis(interfPayhsrcsecinfo.getRatebasic());
                    finCashCalcRule.setYield(ArithmeticUtil.divide(interfFloatingcouponsrate.getRate(), new BigDecimal(100)));
                    if (DateUtil.compareDate(interfFloatingcouponsrate.getChangeDt(), interfPayhsrcsecinfo.getVdate()) == 0) {
                        finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
                    } else {
                        finCashCalcRule.setIsInitial(DictCodeList.是否_否.getValue());
                    }
                    finCashCalcRule.setFinprodId(finprodId);
                    finCashCalcRule.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                    finCashCalcRule.setBranch(branch);
                    finCashCalcRuleList.add(finCashCalcRule);
                }
                // 202012105 债券自动加券功能优化-增加债券现金流计算规则生成规则 wangwei start
                // wind提供的INTERF_FLOATCOUPRATE表只提供浮息债及含权债对应有披露债券利率调整浮动的记录。
                // 自动加券功能不通过界面，无法生成现金流计算规则，所有在入库前需要生成该除上述债券外的债券现金流计算规则
                if (StringUtil.isEmptyList(interfFloatingcouponsrateList)
                        && StringUtil.equals("01", interfPayhsrcsecinfo.getInterestrate())
                        && StringUtil.equals(DictCodeList.是否_否.getValue(), finProduct.getIsRight())) {
                    FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
                    finCashCalcRule.setCashId(finCashInfoInterest.getCashId());
                    finCashCalcRule.setEffDate(interfPayhsrcsecinfo.getVdate());//变动日期
                    finCashCalcRule.setCalcType(interfPayhsrcsecinfo.getInterestrate());
                    finCashCalcRule.setBaseType(BussinessDictCodeList.基数类型_实收资本.getValue());
                    finCashCalcRule.setBaseDateType(BussinessDictCodeList.基数日期类型_当日.getValue());
                    finCashCalcRule.setBasis(interfPayhsrcsecinfo.getRatebasic());
                    finCashCalcRule.setYield(interfPayhsrcsecinfo.getPaperir());
                    finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
                    finCashCalcRule.setFinprodId(finprodId);
                    finCashCalcRule.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                    finCashCalcRule.setBranch(branch);
                    finCashCalcRuleList.add(finCashCalcRule);
                }
                // 202012105 债券自动加券功能优化-增加债券现金流计算规则生成规则 wangwei end
                baseFinProductBaseInfoDto.setFinCashCalcRuleList(finCashCalcRuleList);
                //new现金流利率信息
                List<FinRateInfo> finRateInfoList = new ArrayList<FinRateInfo>();
                FinRateInfo finRateInfo = new FinRateInfo();
                finRateInfo.setCashId(finCashInfoInterest.getCashId());
                finRateInfo.setEffDate(interfPayhsrcsecinfo.getVdate());
                // 一体化对应etliu start
//                finRateInfo.setRateType(interfPayhsrcsecinfo.getInterestrate());
                finRateInfo.setIntType(interfPayhsrcsecinfo.getInterestrate());
                // 一体化对应etliu end
                finRateInfo.setBasis(interfPayhsrcsecinfo.getRatebasic());
                finRateInfo.setResetType(DictCodeList.重置类型_基准利率调整规则_付息后下一计息期生效.getValue());
                finRateInfo.setRate(interfPayhsrcsecinfo.getPaperir());
                finRateInfo.setCoefficient(interfPayhsrcsecinfo.getCoefficient());
                finRateInfo.setSpreadRate(interfPayhsrcsecinfo.getSpreadrate8());
                // 一体化对应etliu start
//                finRateInfo.setRateMax(interfPayhsrcsecinfo.getToprate());
//                finRateInfo.setRateMin(interfPayhsrcsecinfo.getBottomrate());
//                finRateInfo.setIndexId(interfPayhsrcsecinfo.getRatecode());
//                finRateInfo.setIndexSource(DictCodeList.行情类型_利率.getValue());
                finRateInfo.setHighestRate(interfPayhsrcsecinfo.getToprate());
                finRateInfo.setLowestRate(interfPayhsrcsecinfo.getBottomrate());
                finRateInfo.setBenchmarkId(interfPayhsrcsecinfo.getRatecode());
                finRateInfo.setBenchmarkType(DictCodeList.行情类型_利率.getValue());
                // 一体化对应etliu end
                finRateInfo.setFinprodId(finprodId);
                finRateInfo.setBranch(branch);
                finRateInfo.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                //金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
                finRateInfo.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
                //add by zengchengfa start 2019-07-02
                finRateInfo.setBenchmarkIdAccuracy(interfPayhsrcsecinfo.getBaseRateAccuracy());
                finRateInfo.setFirstConfirmDate(interfPayhsrcsecinfo.getFirstRateDeterminationDate());
                finRateInfoList.add(finRateInfo);
                baseFinProductBaseInfoDto.setFinRateInfoList(finRateInfoList);
                //new支付规则
                List<FinCashPayRule> finCashPayRuleList = new ArrayList<FinCashPayRule>();
                FinCashPayRule finCashPayRule = new FinCashPayRule();
                finCashPayRule.setCashId(finCashInfoInterest.getCashId());
                finCashPayRule.setEffDate(interfPayhsrcsecinfo.getVdate());
                finCashPayRule.setPayCycleVal(Long.valueOf(1));
                finCashPayRule.setPayCycle(interfPayhsrcsecinfo.getPaycycle());
                /*--自动加券 支付频率不再用判断 modify by shenchen 2019-04-10--*/
//                if(interfPayhsrcsecinfo.getInterestfrequency()==1){
//                    // 一体化对应etliu start
////                    finCashPayRule.setPayCycle(DictCodeList.支付频率_付息频率_年付.getValue());
//                    finCashPayRule.setPayCycle(DictCodeList.频率_年.getValue());
//                 // 一体化对应etliu end
//                }else if(interfPayhsrcsecinfo.getInterestfrequency()==2){
//                    // 一体化对应etliu start
////                    finCashPayRule.setPayCycle(DictCodeList.支付频率_付息频率_半年付.getValue());
//                    finCashPayRule.setPayCycle(DictCodeList.频率_半年.getValue());
//                 // 一体化对应etliu end
//                }else if(interfPayhsrcsecinfo.getInterestfrequency()==4){
//                    // 一体化对应etliu start
////                    finCashPayRule.setPayCycle(DictCodeList.支付频率_付息频率_季付.getValue());
//                    finCashPayRule.setPayCycle(DictCodeList.频率_季.getValue());
//                 // 一体化对应etliu end
//                }else if(interfPayhsrcsecinfo.getInterestfrequency()==12){
//                    // 一体化对应etliu start
////                    finCashPayRule.setPayCycle(DictCodeList.支付频率_付息频率_月付.getValue());
//                    finCashPayRule.setPayCycle(DictCodeList.频率_月.getValue());
//                 // 一体化对应etliu end
//                }else if(StringUtil.equals(interfPayhsrcsecinfo.getCouponspecies(), "DIS")
//                    ||StringUtil.equals(interfPayhsrcsecinfo.getCouponspecies(), "IAM")
//                    ||StringUtil.equals(interfPayhsrcsecinfo.getCouponspecies(), "ZCO")){
//                    // 一体化对应etliu start
////                        finCashPayRule.setPayCycle(DictCodeList.支付频率_付息频率_到期支付.getValue());
//                    finCashPayRule.setPayCycle(DictCodeList.频率_到期一次.getValue());
//                     // 一体化对应etliu end
//                }
                finCashPayRule.setPayCycle(interfPayhsrcsecinfo.getPaycycle());//支付频率
                /*--自动加券 支付频率不再用判断 modify by shenchen 2019-04-10--*/
                finCashPayRule.setEndTdayRule(DictCodeList.营业日准则_不调整.getValue());
//              inputParaDto.setDictCode(BussinessDictCode.wind营业日准则.getValue());
//              inputParaDto.setItemCode(interfPayhsrcsecinfo.getWorkdayrule());
//              finCashPayRule.setPayTdayRule(windCovCom(inputParaDto));
                finCashPayRule.setPayTdayRule(interfPayhsrcsecinfo.getWorkdayrule());
                /**--息票品种modify 2019-04-15 end--**/
                /**--推算方式modify 2019-04-15 start--**/
//              inputParaDto.setDictCode(BussinessDictCode.wind推算方式.getValue());
//              inputParaDto.setItemCode(interfPayhsrcsecinfo.getSchecalrule());
//              finCashPayRule.setCalcRule(windCovCom(inputParaDto));
                finCashPayRule.setCalcRule(interfPayhsrcsecinfo.getSchecalrule());

                /**--推算方式modify 2019-04-15 end--**/
                finCashPayRule.setFirstPayDate(interfPayhsrcsecinfo.getFirstrateday());
                finCashPayRule.setCashUnitType(DictCodeList.现金流单位_百元法.getValue());
                finCashPayRule.setFinprodId(finprodId);
                finCashPayRule.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                finCashPayRule.setBranch(branch);
                finCashPayRule.setPayType(DictCodeList.分红方式_现金分红.getValue());

                //为了特殊处理首次支付日是节假日的计算利息 start 20200108
                if (StringUtil.equals(interfPayhsrcsecinfo.getSchecalrule(), DictCodeList.推算方式_首次支付日.getValue())
                        && StringUtil.isNotEmpty(interfPayhsrcsecscheduleList) && interfPayhsrcsecscheduleList.size() > 0) {
                    //计算付息计划 如果首次付息日=第一条计划的支付日，推算方式为起息日向后
                    Date mdate = null;
                    if (StringUtil.isNotEmpty(interfPayhsrcsecinfo.getMdate())) {
                        mdate = getFirstMdate(interfPayhsrcsecinfo, finCashPayRule);
                    }
                    if (StringUtil.equals("01", interfPayhsrcsecscheduleList.get(0).getSchedutype()) &&
                            DateUtil.compareDate(interfPayhsrcsecinfo.getFirstrateday(), mdate) == 0) {
                        finCashPayRule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());
                    } else {
                        finCashPayRule.setCalcRule(interfPayhsrcsecinfo.getSchecalrule());
                    }
                }
                //为了特殊处理首次支付日是节假日的计算利息 end 20200108
                finCashPayRuleList.add(finCashPayRule);
                baseFinProductBaseInfoDto.setFinCashPayRuleList(finCashPayRuleList);
                //现金流支付计划
                List<FinCashPlan> finCashPlanList = new ArrayList<FinCashPlan>();
                for (InterfPayhsrcsecschedule interfPayhsrcsecschedule : interfPayhsrcsecscheduleList) {

                    /**--modify 2019-04-17 start--**/
                    //如果为"03"或者"02"-还本，则设置本金的现金流代码,否则为"04","01"-收息，设置收息的现金流代码
                    //支付本金
                    if ("03".equals(interfPayhsrcsecschedule.getSchedutype()) || "02".equals(interfPayhsrcsecschedule.getSchedutype())) {
                        FinCashPlan finCashPlan = new FinCashPlan();
                        finCashPlan.setCashId(finCashInfoPrincipal.getCashId());
                        //finCashPlan.setCashId(finCashInfoPrincipal.getCashId());注释，cashId根据类型判断
                        //如果资讯_债券付息计划数据Entity.实际付息结束日 = 资讯_债券基本信息数据Entity.到期日，则为到期还本
                        if (DateUtil.compareDate(interfPayhsrcsecschedule.getInterestendday(), interfPayhsrcsecinfo.getMdate()) != 0) {
                            finCashPlan.setCashType(BussinessDictCodeList.现金流类型_中间还本.getValue());
                        } else {
                            finCashPlan.setCashType(BussinessDictCodeList.现金流类型_到期还本.getValue());
                        }
                        finCashPlan.setVdateUnadjust(interfPayhsrcsecschedule.getIntereststartday());
                        finCashPlan.setVdate(interfPayhsrcsecschedule.getIntereststartday());
                        finCashPlan.setTermdays(Long.valueOf(0));
                        finCashPlan.setVdateTerm(interfPayhsrcsecschedule.getTotalintereststartday());
                        finCashPlan.setMdateTerm(interfPayhsrcsecschedule.getTotalinterestendday());
                        finCashPlan.setTermdaysTerm(Long.valueOf(0));
                        finCashPlan.setPrinAmt(interfPayhsrcsecschedule.getPrinamtpay());
                        /**mdify by shenchen 2019-04-23 如果为本金，则计息本金取支付本金**/
                        finCashPlan.setIntPrinAmt(interfPayhsrcsecschedule.getPrinamtpay());
                        /**mdify by shenchen 2019-04-23**/
                        finCashPlan.setIntAmt(BigDecimal.ZERO);
                        finCashPlan.setIsPayInt(BussinessDictCodeList.是否_是.getValue());
                        finCashPlan.setCashAmt(interfPayhsrcsecschedule.getPrinamtpay());
                        finCashPlan.setCashBaseamt(new BigDecimal(100));
                        /**--modify 2019-04-17 end--**/
                        finCashPlan.setMdateUnadjust(interfPayhsrcsecschedule.getInterestendday());
                        finCashPlan.setPayDateUnadjust(interfPayhsrcsecschedule.getInterestpayday());
                        finCashPlan.setMdate(interfPayhsrcsecschedule.getInterestendday());
                        finCashPlan.setPayDate(interfPayhsrcsecschedule.getInterestpayday());
                        /**mdify by shenchen 2019-04-23**/
                        //finCashPlan.setIntPrinAmt(interfPayhsrcsecschedule.getPrinamtpay());
                        /**mdify by shenchen 2019-04-23**/
                        //finCashPlan.setFrequency(Long.valueOf(interfPayhsrcsecinfo.getInterestfrequency()));接口表无年付息次数字段
                        finCashPlan.setCashUnitType(BussinessDictCodeList.现金流单位_百元法.getValue());
                        finCashPlan.setFinprodId(finprodId);
                        finCashPlan.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
                        finCashPlan.setBranch(branch);
                        finCashPlan.setPayType(BussinessDictCodeList.分红方式_现金分红.getValue());
                        finCashPlanList.add(finCashPlan);
                    } else if ("04".equals(interfPayhsrcsecschedule.getSchedutype()) || "01".equals(interfPayhsrcsecschedule.getSchedutype())) {
//                    	if(null!=interfPayhsrcsecschedule.getPrinamtpay()&&interfPayhsrcsecschedule.getPrinamtpay().compareTo(BigDecimal.ZERO)>0){
                        FinCashPlan finCashPlan = new FinCashPlan();
                        //如果资讯_债券付息计划数据Entity.实际付息结束日 = 资讯_债券基本信息数据Entity.到期日，则为到期还本
                        finCashPlan.setCashId(finCashInfoInterest.getCashId());
                        if (DateUtil.compareDate(interfPayhsrcsecschedule.getInterestendday(), interfPayhsrcsecinfo.getMdate()) != 0) {
                            finCashPlan.setCashType(BussinessDictCodeList.现金流类型_中间收息.getValue());
                        } else {
                            finCashPlan.setCashType(BussinessDictCodeList.现金流类型_到期收息.getValue());
                        }
                        finCashPlan.setVdateUnadjust(interfPayhsrcsecschedule.getIntereststartday());
                        finCashPlan.setVdate(interfPayhsrcsecschedule.getIntereststartday());
                        finCashPlan.setTermdaysTerm(DateUtil.getBetweenDayNumber(DateUtil.format(interfPayhsrcsecschedule.getIntereststartday()), DateUtil.format(interfPayhsrcsecschedule.getInterestendday())));
                        finCashPlan.setVdateTerm(interfPayhsrcsecschedule.getTotalintereststartday());
                        finCashPlan.setMdateTerm(interfPayhsrcsecschedule.getTotalinterestendday());
                        finCashPlan.setTermdaysTerm(DateUtil.getBetweenDayNumber(DateUtil.format(interfPayhsrcsecschedule.getTotalintereststartday()), DateUtil.format(interfPayhsrcsecschedule.getTotalinterestendday())));
                        /**mdify by shenchen 2019-04-23 如果为本金，则计息本金取计息本金**/
                        finCashPlan.setIntPrinAmt(interfPayhsrcsecschedule.getPrinamt());
                        /**mdify by shenchen 2019-04-23**/
                        finCashPlan.setIntAmt(interfPayhsrcsecschedule.getAccrueintamt());
                        finCashPlan.setCashAmt(interfPayhsrcsecschedule.getAccrueintamt());
                        finCashPlan.setCashBaseamt(new BigDecimal(100));
                        /**--modify 2019-04-17 end--**/
                        finCashPlan.setMdateUnadjust(interfPayhsrcsecschedule.getInterestendday());
                        finCashPlan.setPayDateUnadjust(interfPayhsrcsecschedule.getInterestpayday());
                        finCashPlan.setMdate(interfPayhsrcsecschedule.getInterestendday());
                        finCashPlan.setPayDate(interfPayhsrcsecschedule.getInterestpayday());
                        /**mdify by shenchen 2019-04-23**/
                        //finCashPlan.setIntPrinAmt(interfPayhsrcsecschedule.getPrinamtpay());
                        /**mdify by shenchen 2019-04-23**/
                        //finCashPlan.setFrequency(Long.valueOf(interfPayhsrcsecinfo.getInterestfrequency()));接口表无年付息次数字段
                        finCashPlan.setCashUnitType(BussinessDictCodeList.现金流单位_百元法.getValue());
                        finCashPlan.setFinprodId(finprodId);
                        finCashPlan.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
                        finCashPlan.setBranch(branch);
                        finCashPlan.setPayType(BussinessDictCodeList.分红方式_现金分红.getValue());
                        finCashPlan.setRangeYield(interfPayhsrcsecschedule.getCouponrate());
                        finCashPlanList.add(finCashPlan);
//                    	}
                    }

                }
                baseFinProductBaseInfoDto.setFinCashPlanList(finCashPlanList);
                //new行权信息
                List<FinExercisePlan> finExercisePlanList = new ArrayList<FinExercisePlan>();
                for (InterfPayhsrcsecoption payhsrcsecoption : payhsrcsecoptionList) {
                    FinExercisePlan finExercisePlan = new FinExercisePlan();
                    finExercisePlan.setCashId(finCashInfoPrincipal.getCashId());
                    /**--modify 2019-04-17 start--**/
//                  inputParaDto.setDictCode(BussinessDictCode.wind行权方式.getValue());
//                  inputParaDto.setItemCode(payhsrcsecoption.getOptiontype());
//                  finExercisePlan.setExerciseType(windCovCom(inputParaDto));
                    finExercisePlan.setExerciseType(payhsrcsecoption.getOptiontype());
                    finExercisePlan.setExerciseStatus(DictCodeList.行权状态_未行权.getValue());
                    /**--modify 2019-04-17 end--**/
                    finExercisePlan.setVdate(payhsrcsecoption.getOptionstartday());
                    finExercisePlan.setMdate(payhsrcsecoption.getOptionendday());
                    //根据交易日历推算是否节假日
                    Date optionpayday = payhsrcsecoption.getOptionpayday();//实际行权日
                    Date theoryoptionpayday = payhsrcsecoption.getTheoryoptionpayday();//行权信息表理论行权日
                    if (dateCalService.isDealCalendarHoliday(theoryoptionpayday, finProduct.getCalendarId())) {
                        //如果是节假日朝后推算
                        Date tempDate = DateUtil.parse(DateUtil.plusDays(DateUtil.format(theoryoptionpayday), 15));
                        //获取节假日
                        List<String> holidayList = dateCalService.getDealCalendarHoliday(theoryoptionpayday, tempDate, finProduct.getCalendarId());
                        theoryoptionpayday = DateUtil.parse(AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(theoryoptionpayday),
                                DictCodeList.营业日准则_向后调整.getValue(), holidayList));

                    }
                    //实际行权日为空或者小于理论行权日的时候，实际行权日 = 理论行权日
                    if (!StringUtil.isNotEmpty(optionpayday) || DateUtil.compareDate(optionpayday, theoryoptionpayday) < 0) {
                        finExercisePlan.setActExerciseDate(theoryoptionpayday);//实际行权日
                    } else {
                        finExercisePlan.setActExerciseDate(optionpayday);//实际行权日
                    }

                    //finExercisePlan.setExerciseDate(theoryoptionpayday);//行权信息表理论行权日
                    // TISHKBS-1382
                    finExercisePlan.setExerciseDate(payhsrcsecoption.getTheoryoptionpayday());//行权信息表理论行权日

                    finExercisePlan.setExercisePrice(payhsrcsecoption.getOptionprice());
                    /**--现行权条款取行权信息表行权条款 add by shenchen 2019-04-11--**/
                    //finExercisePlan.setExerciseRemark(interfCbondspecialconditions.getbInfoContent());//中国债券特殊条款注释
                    finExercisePlan.setExerciseRemark(payhsrcsecoption.getOptionclause());//行权信息表行权条款
                    /**--现行权条款取行权信息表行权条款 add by shenchen 2019-04-11--**/
                    finExercisePlan.setFinprodId(finprodId);
                    finExercisePlan.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                    finExercisePlan.setBranch(branch);
                    finExercisePlanList.add(finExercisePlan);
                }
                baseFinProductBaseInfoDto.setFinExercisePlanList(finExercisePlanList);

                //查询资讯_债券信息评级list
                List<InterfPayhsrcsecrating> interfPayhsrcsecratingList = selectInterfPayhsrcsecrating(secid);
                //new债券信用评级
                List<MstBondCreditRating> mstBondCreditRatingList = new ArrayList<MstBondCreditRating>();
                if (null != interfPayhsrcsecratingList && interfPayhsrcsecratingList.size() != 0) {
                    for (InterfPayhsrcsecrating interfPayhsrcsecrating : interfPayhsrcsecratingList) {
                        MstBondCreditRating mstBondCreditRating = new MstBondCreditRating();
                        mstBondCreditRating.setoCode(interfPayhsrcsecrating.getSecid());//债券ID
                        mstBondCreditRating.setGradeOrgId(interfPayhsrcsecrating.getRatingorg());//评级机构
                        mstBondCreditRating.setGradeDate(interfPayhsrcsecrating.getPubdate());//评级日期取发布日期
                        //mstBondCreditRating.setIsrecommand(interfPayhsrcsecrating.getIsrecommand());//是否推荐
                        mstBondCreditRating.setoType(interfPayhsrcsecrating.getRatingtype());//评级类型
                        mstBondCreditRating.setShortLongTerm(interfPayhsrcsecrating.getRatingsubtype());//长期短期
                        mstBondCreditRating.setGradeResult(interfPayhsrcsecrating.getRating());//评级结果
                        //mstBondCreditRating.setSecIssueId(getIssuer(interfPayhsrcsecinfo.getIssuershort(), "IS_ISSUER"));//标的类金融产品发行人
                        mstBondCreditRating.setInputType(BussinessDictCodeList.录入方式_接口导入.getValue());//录入方式
                        mstBondCreditRatingList.add(mstBondCreditRating);
                    }
                    baseFinProductBaseInfoDto.setMstBondCreditRatingList(mstBondCreditRatingList);//组装到baseInfoDto
                }
                //new中债报送信息
                List<MstCustomerInfoAdd> mstCustomerInfoAddList = selectMstCustomerInfoAdd(finProduct.getIssuer());//根据发行人查MST_CUSTOMER_INFO_ADD
                RepAssetBond repAssetBond = new RepAssetBond();//中债报送信息
                RepAssetDept repAssetDept = new RepAssetDept();
                //评级结果查询(主体评级,离系统日最近的日期)
                String rating = selectRating(secid, DateUtil.getSysDate());
                repAssetBond.setRepFinprodId(finprodId);//金融产品代码
                repAssetBond.setSecId(interfPayhsrcsecinfo.getSecid());//债券代码
                repAssetBond.setRepIssueMode(interfPayhsrcsecinfo.getIssuetype());//发行方式
                repAssetBond.setRepMainGrade(rating);//主体评级
                repAssetBond.setRepRegistOrg(interfPayhsrcsecinfo.getInstitution());//登记托管机构
                repAssetBond.setRepRegistorgComment(interfPayhsrcsecinfo.getInstitutiontext());//登记托管机构说明
                //[交易流通场所][具体类别]发行机构类型（按规模划分）,发行机构类型（按技术领域划分）发行机构类型（按经济类型划分）选择“99”时,备注不可为空，默认为无
                /*if (StringUtil.equals("99",interfPayhsrcsecinfo.getMarket2())||
                        StringUtil.equals("99",interfPayhsrcsecinfo.getConcreteClass())||
                        StringUtil.equals("99",interfPayhsrcsecinfo.getIssuerScaleType())||
                        StringUtil.equals("99",interfPayhsrcsecinfo.getIssuerTechnologyType())||
                        StringUtil.equals("99",interfPayhsrcsecinfo.getIssuerEconomicType())){
                    repAssetBond.setRepRemark("无");
                }*/
                repAssetBond.setRepRemark(interfPayhsrcsecinfo.getRemark());
                repAssetDept.setRepFinprodId(finprodId);//金融产品代码
                repAssetDept.setFinprodMarketId(secid);//债券代码
                repAssetDept.setRepFinprodName(interfPayhsrcsecinfo.getSecname());//债券名称
                repAssetDept.setRepIssuePartyId("C10816");//发行机构代码(参数值)
                //repAssetDept.setRepAssetType(exchangeType(interfPayhsrcsecinfo.getChbsectype2()));//资产负债类别
                repAssetDept.setRepAssetType(interfPayhsrcsecinfo.getAssetClass());
                repAssetDept.setRepCcy(interfPayhsrcsecinfo.getCcy());//币种
                //repAssetBond.setRepSectype(repSectype);具体类别
                repAssetBond.setRepSectype(interfPayhsrcsecinfo.getConcreteClass());//具体类别 add by lpl 2019-12-20
                try {
                    repAssetDept.setRepStatusDate(sdf.parse(DateUtil.getSysDate()));//数据日期
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                repAssetDept.setRepTradeMarket(interfPayhsrcsecinfo.getMarket2());//交易流通场所
                repAssetDept.setIsRepTarget(BussinessDictCodeList.是否_否.getValue());//默认否

                repAssetBond.setRepPartyType1(interfPayhsrcsecinfo.getIssuerScaleType());//机构类型按规模分
                repAssetBond.setRepPartyType2(interfPayhsrcsecinfo.getIssuerEconomicType());//机构类型按经济类型分
                repAssetBond.setRepPartyType3(interfPayhsrcsecinfo.getIssuerTechnologyType());//机构类型按技术领域分
                repAssetBond.setRepIndustry(interfPayhsrcsecinfo.getIssuerInstitutions());//发行机构所属行业
                // repAssetBond.setRepIndustryLevel2(interfPayhsrcsecinfo.getIndustry2());//发行机构所属行业二级分类
                // add by caiqun 20200915 TISHKBS-864 通过底层估值表导入的债券，加券的数据不全、不正确，导致交易异常  start
                if (null != mstCustomerInfoAddList && mstCustomerInfoAddList.size() != 0) {
                    for (MstCustomerInfoAdd mstCustomerInfoAdd : mstCustomerInfoAddList) {
	                		/*repAssetBond.setRepPartyType1(mstCustomerInfoAdd.getPartyType1());//机构类型按规模分
	                		repAssetBond.setRepPartyType2(mstCustomerInfoAdd.getPartyType2());//机构类型按经济类型分
	                		repAssetBond.setRepPartyType3(mstCustomerInfoAdd.getPartyType3());//机构类型按技术领域分
	                		repAssetBond.setRepIndustry(mstCustomerInfoAdd.getIndustry());//发行机构所属行业
	                		repAssetBond.setRepIndustryLevel2(mstCustomerInfoAdd.getIndustry2());//发行机构所属行业二级分类*/
                        repAssetBond.setRepIndustryLevel2(mstCustomerInfoAdd.getIndustry2());//发行机构所属行业二级分类
                    }
                } else {
                    repAssetBond.setRepIndustryLevel2(interfPayhsrcsecinfo.getIssuerinstitutionssecond());//发行机构所属行业二级分类
                    //repAssetBond.setRepIndustryLevel2(interfPayhsrcsecinfo.getRepIndustryLevel2());
                }
                // add by caiqun 20200915 TISHKBS-864 通过底层估值表导入的债券，加券的数据不全、不正确，导致交易异常  end
//                baseFinProductBaseInfoDto.setRepAssetBond(repAssetBond);//存中债报送界面机构类型等字段
//                baseFinProductBaseInfoDto.setRepAssetDept(repAssetDept);//存中债报送界面资产负债类别
                baseFinProductBaseInfoDto.setRepAssetType(interfPayhsrcsecinfo.getAssetClass());
                baseFinProductBaseInfoList.add(baseFinProductBaseInfoDto);
            }
        }
        dto.setBaseFinProductBaseInfoList(baseFinProductBaseInfoList);
        return dto;
    }


    /**
     * 新增 2019-04-12
     * 债券自动加券信息组件
     */
    public InterfPayhsrcsecinfoDto busiAutoAddSecPrice(InterfPayhsrcsecinfoDto dto, String finprodMarketId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Long branch = StringUtil.isEmpty(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)) ? 0 : Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        String sysDate = null;//系统平台日期(字符串类型)
        String newDate = null;//平台日减行情天数(字符串类型)
        Date date = null;//平台日减行情天数(Date类型)
        try {
            if (null != dto.getSecidList() && !dto.getSecidList().isEmpty()) {
                for (String secid : dto.getSecidList()) {
                    //判断是否存在债券估值行情信息
                    int count = selectMstBondValuationInfoCount(secid);
                    sysDate = DateUtil.getSysDate();//系统平台日期(字符串类型)
                    date = sdf.parse(sysDate);
                    int changeDays = count > 0 ? 0 : -9;//行情天数
                    newDate = exchangeDate(date, changeDays);//获取系统平台日期-行情天数(字符串类型)
                    //资讯_债券市价估值行情list
                    List<InterfPayhsrccbvlt> interfPayhsrccbvltList = selectInterfPayhsrccbvlt(secid, sysDate, newDate);
                    //一.存在查询结果,构建债券估值entity
                    List<MstBondValuationInfo> mstBondValuationInfoList = new ArrayList<MstBondValuationInfo>();//债券估值行情list,
                    if (null != interfPayhsrccbvltList && interfPayhsrccbvltList.size() != 0) {
                        for (InterfPayhsrccbvlt interfPayhsrccbvlt : interfPayhsrccbvltList) {
                            //new债券估值行情实体类
                            MstBondValuationInfo mstBondValuationInfo = new MstBondValuationInfo();
                            mstBondValuationInfo.setSecId(interfPayhsrccbvlt.getSecid());//债券代码
                            mstBondValuationInfo.setValueDate(sdf.parse(interfPayhsrccbvlt.getCdate()));//估值日
                            mstBondValuationInfo.setValueSource(interfPayhsrccbvlt.getValueSource());//估值来源
                            mstBondValuationInfo.setLastPeriod(interfPayhsrccbvlt.getEncashYears());//剩余期限
                            mstBondValuationInfo.setPrice(interfPayhsrccbvlt.getMdprice());//市价全价
                            mstBondValuationInfo.setNetprice(interfPayhsrccbvlt.getMcprice());//市价净价
                            mstBondValuationInfo.setmDuration(interfPayhsrccbvlt.getmMduration());//市价久期
                            mstBondValuationInfo.setmConvexity(interfPayhsrccbvlt.getmCnvxty());//市价凸性
                            mstBondValuationInfo.setBpvalue(interfPayhsrccbvlt.getBasisvalue());//基点价值
                            mstBondValuationInfo.setSduration(interfPayhsrccbvlt.getMsprdD());//利差久期
                            mstBondValuationInfo.setScnvxty(interfPayhsrccbvlt.getMsprdCnvxty());//利差凸性
                            mstBondValuationInfo.setInterestDuration(interfPayhsrccbvlt.getMyieldD());//利率久期
                            mstBondValuationInfo.setInterestCnvxty(interfPayhsrccbvlt.getMyieldCnvxty());//利率凸性
                            mstBondValuationInfo.setBpyield(interfPayhsrccbvlt.getMyield());//市价收益率
                            mstBondValuationInfo.setInputType(BussinessDictCodeList.录入方式_接口导入.getValue());//录入方式
                            mstBondValuationInfoList.add(mstBondValuationInfo);//添加到债券估值list
                        }
                    }
                    dto.setMstBondValuationInfoList(mstBondValuationInfoList);//装入dto

                    //INTERF_BOND_MARKET_INFO查询资讯_市场行情表数据
                    List<InterfBondMarketInfo> interfBondMarketInfoList = selectInterfBondMarketInfo(secid, sysDate, newDate);
                    List<MstBondMarketInfo> mstBondMarketInfoList = new ArrayList<MstBondMarketInfo>();
                    //二.存在查询结果,则构建债券市场行情entity
                    if (null != interfBondMarketInfoList && interfBondMarketInfoList.size() != 0) {
                        for (InterfBondMarketInfo interfBondMarketInfo : interfBondMarketInfoList) {
                            //new债券市场行情entity  MST_BOND_MARKET_INFO
                            MstBondMarketInfo mstBondMarketInfo = new MstBondMarketInfo();
                            mstBondMarketInfo.setSecId(interfBondMarketInfo.getSecId());//债券代码
                            mstBondMarketInfo.setValueDate(interfBondMarketInfo.getValueDate());//估值日
                            //mstBondMarketInfo.setValueSource("CBC");//估值来源，经确认，默认CBC
                            mstBondMarketInfo.setClosePrice(interfBondMarketInfo.getClosePrice());//收盘价
                            mstBondMarketInfo.setAveragePrice(interfBondMarketInfo.getAveragePrice());//平均价
                            mstBondMarketInfo.setMaxPrice(interfBondMarketInfo.getMaxPrice());//最高价
                            mstBondMarketInfo.setMinPrice(interfBondMarketInfo.getMinPrice());//最低价
                            mstBondMarketInfo.setBuyOnePrice(interfBondMarketInfo.getBuyOnePrice());//买一价
                            mstBondMarketInfo.setSellOnePrice(interfBondMarketInfo.getSellOnePrice());//卖一价
                            mstBondMarketInfo.setInputType(BussinessDictCodeList.录入方式_接口导入.getValue());//录入方式
                            mstBondMarketInfoList.add(mstBondMarketInfo);//添加到债券市场行情list
                        }
                    }
                    dto.setMstBondMarketInfoList(mstBondMarketInfoList);//装入dto

                    //判断当前日期是否存在回购标准券折算率 (FIN_STDSEC_CONVERSE_RADIO)
                    int standardcount = selectFinStdsecConverseRadioCount(secid, sysDate);
                    Boolean isConvert = standardcount > 0 ? false : true;//是否加折算率
                    List<FinStdsecConverseRadio> finStdsecConverseRadioList = new ArrayList<FinStdsecConverseRadio>();//回购标准券折算率list
                    //如果为true,代表不存在回购标准折算率
                    if (isConvert) {
                        //查询资讯_回购标准折算率
                        List<InterfStdsecConRadio> interfStdsecConRadioList = selectInterfStdsecConRadio(secid, sysDate);
                        //三.存在查询结果，则构建回购标准折算率entity
                        if (null != interfStdsecConRadioList && interfStdsecConRadioList.size() != 0) {
                            for (InterfStdsecConRadio interfStdsecConRadio : interfStdsecConRadioList) {
                                FinStdsecConverseRadio finStdsecConverseRadio = new FinStdsecConverseRadio();
                                finStdsecConverseRadio.setFinprodId(interfStdsecConRadio.getSecId());//债券ID
                                finStdsecConverseRadio.setBranch(branch);//分支序号
                                finStdsecConverseRadio.setVdate(interfStdsecConRadio.getVdate());//开始适用日
                                finStdsecConverseRadio.setMdate(interfStdsecConRadio.getMdate());//结束适用日
                                finStdsecConverseRadio.setScale(interfStdsecConRadio.getScale());//折算比例
                                finStdsecConverseRadio.setCvnperhundred(interfStdsecConRadio.getCvnperhundred());//折合标准券
                                finStdsecConverseRadioList.add(finStdsecConverseRadio);
                            }
                        }
                    }
                    dto.setFinStdsecConverseRadioList(finStdsecConverseRadioList);//装入dto
                }
            }
            return dto;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 自动加券(基金)
     * <功能详细描述>
     *
     * @param dto
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    public InfChinamutualfunddescriptionDto autoFundCoupon(InfChinamutualfunddescriptionDto dto) throws ParseException {
        autoFundCoupon(dto, dto.getfInfoWindcode());
        InfChinamutualfunddescriptionDto newInfChinamutualfunddescriptionDto = checkFundRepData(dto);
        //中债校验通过
        String isPermitAddcoupon = CacheUtil.getSysParam(BussinessConstant.IS_PERMIT_ADDCOUPON);
        if (!(StringUtil.isEmptyList(newInfChinamutualfunddescriptionDto.getBaseFinProductBaseInfoList())
                && StringUtil.equals(isPermitAddcoupon, DictCodeList.是否_否.getValue()))) {
            autoFundCouponSave(dto);
            //基金自动加券
            busiAutoAddFundPrice(dto, dto.getfInfoWindcode());
            //场外金融产品保存
            finProductValuationSave(dto);
            //基金市场行情保存
            mstFundMarketInfoSave(dto);
            newInfChinamutualfunddescriptionDto.setBaseFinProductBaseInfoList(dto.getBaseFinProductBaseInfoList());
        }
        return newInfChinamutualfunddescriptionDto;
    }

    /**
     * 自动加券(基金) 封装dto
     *
     * @param dto
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    public InfChinamutualfunddescriptionDto autoFundCoupon(InfChinamutualfunddescriptionDto dto, String finprodMarketId) throws ParseException {
        List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList = new ArrayList<BaseFinProductBaseInfoDto>();
        if (null != dto.getfInfoWindcodeList() && !dto.getfInfoWindcodeList().isEmpty()) {
            //交易日历
            List<MstCalendarInfo> mstCalendarInfoList = getMstCalendarInfo();
            Long branch = StringUtil.isEmpty(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)) ? 0 : Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
            for (String fInfoWindcode : dto.getfInfoWindcodeList()) {
                XmlRelationInputParaDto inputParaDto = new XmlRelationInputParaDto();
                BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
                //基金基本信息
                /**--add by shenchen 2019-04-11 基金基本资料实体类更换--*/
                //InfChinamutualfunddescription infChinamutualfunddescription = selectWindFundAssetByfInfoWindcode(fInfoWindcode);
                InterfCmfunddescrip infChinamutualfunddescription = selectWindFundAssetByfInfoWindcode(fInfoWindcode);
                /**--add by shenchen 2019-04-11 基金基本资料实体类更换--*/
                //查询结果为空
                if (!StringUtil.isNotEmpty(infChinamutualfunddescription)) {
                    return null;
                }
                /**--基金加券新设计注释 add by shenchen 2019-04-11--*/
                //中国货币市场基金份额结转方式
                //InfCmoneymarketfscarryoverm infCmoneymarketfscarryoverm =  selectWindFundMoneymarketfscarryoverm(fInfoWindcode);
                /**--基金加券新设计注释 add by shenchen 2019-04-11--*/
                //new标的类金融产品
                FinProduct finProduct = new FinProduct();
                String finprodId = commonBusinessService.getFinProdId(fInfoWindcode, DictCodeListExt1.金融产品类型_投管用_基金.getValue(), StringUtil.EMPTY);
                finProduct.setFinprodId(finprodId);
                finProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
                finProduct.setFinprodAbbr(infChinamutualfunddescription.getfInfoName());
                finProduct.setFinprodName(infChinamutualfunddescription.getfInfoFullname());
                if (StringUtil.equals("货币市场型", infChinamutualfunddescription.getfInfoType())) {
                    finProduct.setProfitType(DictCodeList.收益类型_货币型.getValue());
                } else {
                    finProduct.setProfitType(DictCodeList.收益类型_净值型.getValue());
                }
                /**--modify by shenchen 2019-04-11*/
                //finProduct.setFinprodMarketId(infChinamutualfunddescription.getfInfoWindcode());
                //finProduct.setCcy(infChinamutualfunddescription.getCrnyCode());
                finProduct.setFinprodMarketId(infChinamutualfunddescription.getfInfoCode());
                finProduct.setCcy(infChinamutualfunddescription.getCcy());//币种
                /**--modify by shenchen 2019-04-11*/
                if (StringUtil.equals("CNY", infChinamutualfunddescription.getCcy())) {
                    finProduct.setBlnArea(DictCodeList.境内外_境内.getValue());
                } else {
                    finProduct.setBlnArea(DictCodeList.境内外_境外.getValue());
                }
                /**--modify by shenchen 2019-04-11*/
//              inputParaDto.setDictCode(DictCode.交易场所_WIND.getValue());
//              inputParaDto.setItemCode(infChinamutualfunddescription.getfInfoExchmarket());
//              finProduct.setTradeMarket(windCovCom(inputParaDto));
                finProduct.setTradeMarket(infChinamutualfunddescription.getfInfoExchmarket());
                /**--modify by shenchen 2019-04-11*/
                //2019年5月27日  法定节假日
                if (StringUtil.equals(BussinessDictCodeList.交易场所_场外.getValue(), infChinamutualfunddescription.getfInfoExchmarket())) {
                    finProduct.setCalendarId("01");//银行间
                } else if (StringUtil.equals(BussinessDictCodeList.交易场所_上海证券交易所.getValue(), infChinamutualfunddescription.getfInfoExchmarket())) {
                    finProduct.setCalendarId("04");//上交所
                } else if (StringUtil.equals(BussinessDictCodeList.交易场所_深圳证券交易所.getValue(), infChinamutualfunddescription.getfInfoExchmarket())) {
                    finProduct.setCalendarId("05");//深交所
                }
                for (MstCalendarInfo mstCalendarInfo : mstCalendarInfoList) {
                    inputParaDto.setDictCode(DictCode.交易场所_WIND.getValue());
                    inputParaDto.setItemCode("SH");
//                  if(StringUtil.equals(mstCalendarInfo.getOtherId(),windCovCom(inputParaDto))){
                    //第三方日历编号等于上交所
                    if (StringUtil.equals(mstCalendarInfo.getOtherId(), "02")) {
                        finProduct.setCalendarId(mstCalendarInfo.getCalendarId());
                        break;
                    }
                }
                /**--管理人 modify 2019-04-11 --*/
                //finProduct.setManager(getIssuer(infChinamutualfunddescription.getfInfoCorpFundmanagementcomp()));
                //finProduct.setIssuer(getIssuer(infChinamutualfunddescription.getfInfoCorpFundmanagementcomp()));
                finProduct.setManager(getIssuer(infChinamutualfunddescription.getfInfoCorpFundmanagementid(), "IS_ASSTE_MANAGER"));
                finProduct.setIssuer(getIssuer(infChinamutualfunddescription.getfInfoCorpFundmanagementid(), "IS_ISSUER"));
                /**--管理人 modify 2019-04-11 --*/
                //托管人
                finProduct.setTrusteeId(getIssuer(infChinamutualfunddescription.getfInfoCustodianbankid(), "IS_TRUSTER"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if (StringUtil.isNotEmpty(infChinamutualfunddescription.getfInfoIssuedate())) {
                    finProduct.setIdate(sdf.parse(infChinamutualfunddescription.getfInfoIssuedate()));
                }
                if (StringUtil.isNotEmpty(infChinamutualfunddescription.getfInfoSetupdate())) {
                    finProduct.setVdate(sdf.parse(infChinamutualfunddescription.getfInfoSetupdate()));
                }
                if (StringUtil.isNotEmpty(infChinamutualfunddescription.getfInfoMaturitydate())) {
                    finProduct.setMdate(sdf.parse(infChinamutualfunddescription.getfInfoMaturitydate()));
                }
                if (null != infChinamutualfunddescription.getfInfoSetupdate() && null != infChinamutualfunddescription.getfInfoMaturitydate()) {
                    long termDays = DateUtil.getBetweenDayNumber(infChinamutualfunddescription.getfInfoSetupdate(), infChinamutualfunddescription.getfInfoMaturitydate());
                    finProduct.setTermDays(termDays);
                }
                if (StringUtil.isNotEmpty(infChinamutualfunddescription.getfIssueOefStartdateinst())) {
                    finProduct.setToffStrdate(sdf.parse(infChinamutualfunddescription.getfIssueOefStartdateinst()));
                }
                if (StringUtil.isNotEmpty(infChinamutualfunddescription.getfIssueOefDnddateinst())) {
                    finProduct.setToffEnddate(sdf.parse(infChinamutualfunddescription.getfIssueOefDnddateinst()));
                }
                finProduct.setIsChl(DictCodeList.是否_否.getValue());
                //本金收益特征：基金默认非保本
                finProduct.setCapiIncomeFeature(BussinessDictCodeList.本金收益特征_非保本.getValue());
                baseFinProductBaseInfoDto.setFinProduct(finProduct);
                //new标的类金融产品附属
                FinProductAdd finProductAdd = new FinProductAdd();
                finProductAdd.setFinprodId(finprodId);
                /*--modify by shenchen 2019-04-11 INTERF_CMFUNDDESCRIP表无此字段--*/
                //finProductAdd.setWindId(infChinamutualfunddescription.getObjectId());
                /*--modify by shenchen 2019-04-11 INTERF_CMFUNDDESCRIP表无此字段--*/
                finProductAdd.setIsAutoUpdate(DictCodeList.是否_是.getValue());
                /**--是否按日复利 modify by shenchen 2019-04-11--*/
//                if(StringUtil.equals(infCmoneymarketfscarryoverm.getfInfoIncomescarryoverm(), "1")){
//                    finProductAdd.setIsCompoundInt(DictCodeList.是否_是.getValue());
//                }else{
//                    finProductAdd.setIsCompoundInt(DictCodeList.是否_否.getValue());
//                }
                if (BussinessDictCodeList.收益结转频率_按月结转.getValue().equals(infChinamutualfunddescription.getPaycycle())) {
                    finProductAdd.setIsCompoundInt(infChinamutualfunddescription.getIsCompoundInt());
                }
                // 买入确认日 申购确认速度
                if (StringUtil.isNotEmpty(infChinamutualfunddescription.getPconfirmspeed())) {
                    String[] purSpeed = infChinamutualfunddescription.getPconfirmspeed().split("\\+");
                    if (purSpeed.length > 1) {
                        finProductAdd.setPurSpeed(Long.valueOf(purSpeed[1]));
                    }
                }
                //卖出确认日 赎回确认速度
                if (StringUtil.isNotEmpty(infChinamutualfunddescription.getSconfirmspeed())) {
                    String[] redSpeed = infChinamutualfunddescription.getSconfirmspeed().split("\\+");
                    if (redSpeed.length > 1) {
                        finProductAdd.setRedSpeed(Long.valueOf(redSpeed[1]));
                    }
                }
                /**--是否按日复利 modify by shenchen 2019-04-11--*/


                finProductAdd.setTrmFundAbbr(infChinamutualfunddescription.getTrmFundAbbr());
                finProductAdd.setTrmFundType(infChinamutualfunddescription.getTrmFundType());
                finProductAdd.setPurRedId(infChinamutualfunddescription.getPurRedId());
                finProductAdd.setPurRedAbbr(infChinamutualfunddescription.getPurRedAbbr());
                finProductAdd.setPurStartLow(infChinamutualfunddescription.getPurStartLow());
                finProductAdd.setRedStartLow(infChinamutualfunddescription.getRedStartLow());
                finProductAdd.setEtfType(infChinamutualfunddescription.getEtfType());
                finProductAdd.setEtfMinPrunit(infChinamutualfunddescription.getEtfMinPrunit());

                // ETF类型：如果实际项目中提供的该字段枚举值不是这样，则需要修改代码
                if (StringUtil.equals("ETF", infChinamutualfunddescription.getTrmFundType())) {
                    // wind提供的枚举值：货币市场型基金、被动指数型基金、被动指数型债券基金、国际(QDII)被动指数型股票基金、商品型基金
                    // 我们系统的枚举值：货币ETF、债券ETF、黄金ETF、单市场ETF、跨市场ETF、跨境ETF
                    if (StringUtil.equals("货币市场型基金", infChinamutualfunddescription.getEtfType())) {
                        // wind货币市场型基金对应系统货币ETF
                        finProductAdd.setEtfType(DictCodeListExt2.ETF类型_货币ETF.getValue());
                    } else if (StringUtil.equals("被动指数型债券基金", infChinamutualfunddescription.getEtfType())) {
                        // wind被动指数型债券基金对应系统债券ETF
                        finProductAdd.setEtfType(DictCodeListExt2.ETF类型_债券ETF.getValue());
                    } else if (StringUtil.equals("商品型基金", infChinamutualfunddescription.getEtfType())) {
                        // wind商品型基金对应系统黄金ETF
                        finProductAdd.setEtfType(DictCodeListExt2.ETF类型_黄金ETF.getValue());
                    } else if (StringUtil.equals("03", infChinamutualfunddescription.getfInfoType2())) {
                        // 根据wind数据分析，股票基金，eft类型包含了被动指数型基金、被动指数型股票基金
                        if (StringUtil.equals("被动指数型基金", infChinamutualfunddescription.getEtfType())) {
                            // 暂时先将被动指数型基金对应到单市场ETF，国际(QDII)被动指数型股票基金对应到跨境ETF
                            finProductAdd.setEtfType(DictCodeListExt2.ETF类型_单市场ETF.getValue());
                        } else if (StringUtil.equals("国际(QDII)被动指数型股票基金", infChinamutualfunddescription.getEtfType())) {
                            finProductAdd.setEtfType(DictCodeListExt2.ETF类型_跨境ETF.getValue());
                        } else {
                            finProductAdd.setEtfType(DictCodeListExt2.ETF类型_跨市场ETF.getValue());
                        }
                    } else {
                        finProductAdd.setEtfType(DictCodeListExt2.ETF类型_单市场ETF.getValue());
                    }

                }


                baseFinProductBaseInfoDto.setFinProductAdd(finProductAdd);
                //new金融产品分类
                FinProductType finProductType = new FinProductType();
                /**--分类4，分类5取基金基本资料表基本类型，基本类型2 modify 2019-04-11 start--*/
//                if(StringUtil.equals(infChinamutualfunddescription.getfInfoFirstinvesttype(),"货币市场型")){
//                    finProductType.setType4(DictCodeList.基金类资产类型_货币基金.getValue());
//                }else{
//                    finProductType.setType4(DictCodeList.基金类资产类型_非货币基金.getValue());
//                    if(StringUtil.equals(infChinamutualfunddescription.getfInfoFirstinvesttype(),"股票型")){
//                        finProductType.setType5(DictCodeList.基金类资产二级类型_股票基金.getValue());
//                    }else if(StringUtil.equals(infChinamutualfunddescription.getfInfoFirstinvesttype(),"混合型")){
//                        finProductType.setType5(DictCodeList.基金类资产二级类型_混合基金.getValue());
//                    }else if(StringUtil.equals(infChinamutualfunddescription.getfInfoFirstinvesttype(),"保本型")){
//                        finProductType.setType5(DictCodeList.基金类资产二级类型_保本基金.getValue());
//                    }else if(StringUtil.equals(infChinamutualfunddescription.getfInfoFirstinvesttype(),"FOF")
//                        ||StringUtil.equals(infChinamutualfunddescription.getfInfoFirstinvesttype(),"商品型")
//                        ||StringUtil.equals(infChinamutualfunddescription.getfInfoFirstinvesttype(),"QDII")){
//                        finProductType.setType5(DictCodeList.基金类资产二级类型_其他公募基金.getValue());
//                    }else if(StringUtil.equals(infChinamutualfunddescription.getfInfoFirstinvesttype(),"债券型")){
//                        finProductType.setType5(DictCodeList.基金类资产二级类型_债券基金.getValue());
//                    }
//                }
                finProductType.setType4(infChinamutualfunddescription.getfInfoType());//基本类型
                finProductType.setType6(infChinamutualfunddescription.getfInfoType());//基本类型

                FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
                finProductTypeConfig.setInputValue1(BussinessDictCodeList.平安资产类型_基金.getValue());
                finProductTypeConfig.setInputValue2(infChinamutualfunddescription.getfInfoType());
                finProductTypeConfig.setInputValue3(infChinamutualfunddescription.getfInfoType2());
//                if (StringUtil.equals(BussinessDictCodeList.基金类资产类型_非货币基金.getValue(),infChinamutualfunddescription.getfInfoType())
//                        && StringUtil.equals(BussinessDictCodeList.基金类资产二级类型_债券基金.getValue(), infChinamutualfunddescription.getfInfoType2())
//                && (StringUtil.equals(CacheUtil.getSysParam("IS_SHOW_TYPE"), "01") ||
//                        StringUtil.isEmpty(CacheUtil.getSysParam("IS_SHOW_TYPE")))){
//                    finProductTypeConfig.setInputValue2(StringUtil.EMPTY);
//                    finProductTypeConfig.setInputValue3(StringUtil.EMPTY);
//                }
                finProductTypeConfig = restTemplate.postForObject(ServiceApplication.ASSET_SERVICE_NAME + "/AssetCommon/getAssetThreeType",
                        finProductTypeConfig, FinProductTypeConfig.class);

                finProductType.setType9(finProductTypeConfig.getInputValue2());//基本类型
                finProductType.setType10(finProductTypeConfig.getInputValue3());//基本类型2
                finProductType.setType11(finProductTypeConfig.getInputValue4());
                finProductType.setType12(finProductTypeConfig.getInputValue5());

                finProductType.setType5(infChinamutualfunddescription.getfInfoType2());//基本类型2
                finProductType.setType7(infChinamutualfunddescription.getfInfoType2());//基本类型2

                finProductType.setType8(BussinessDictCodeList.平安资产类型_基金.getValue());//type8
                /**--分类4，分类5取基金基本资料表基本类型，基本类型2 modify 2019-04-11 end--*/
                finProductType.setFinprodId(finprodId);
                finProductType.setBranch(branch);
                baseFinProductBaseInfoDto.setFinProductType(finProductType);
                //new现金流信息
                List<FinCashInfo> finCashInfoList = new ArrayList<FinCashInfo>();
                //现金流信息Entity-本金
                FinCashInfo finCashInfoPrincipal = encapFinCashInfo(finprodId, DictCodeListExt1.现金流一级类型_本金.getValue(), DictCodeListExt1.现金流二级类型_本金.getValue(), branch);
                //现金流信息Entity-利息
                FinCashInfo finCashInfoInterest = encapFinCashInfo(finprodId, DictCodeListExt1.现金流一级类型_利息.getValue(), DictCodeListExt1.现金流二级类型_利息.getValue(), branch);
                finCashInfoList.add(finCashInfoPrincipal);
                finCashInfoList.add(finCashInfoInterest);
                baseFinProductBaseInfoDto.setFinCashInfoList(finCashInfoList);
                //支付规则
                List<FinCashPayRule> finCashPayRuleList = new ArrayList<FinCashPayRule>();
                FinCashPayRule finCashPayRule = new FinCashPayRule();
                finCashPayRule.setCashId(finCashInfoInterest.getCashId());
                /**--支付规则生效日 modify 2019-04-11--*/
//              finCashPayRule.setEffDate(DateUtil.parse(infChinamutualfunddescription.getfInfoMaturitydate()));
                finCashPayRule.setEffDate(DateUtil.parse(infChinamutualfunddescription.getfInfoSetupdate()));//资讯_基本资料成立日期
                /**--支付规则生效日 modify 2019-04-11--*/
                finCashPayRule.setPayCycleVal(Long.valueOf(1));
                /**--支付频率 直接取基金基本资料分红频率 modify 2019-04-11--*/
//                if(StringUtil.equals(infCmoneymarketfscarryoverm.getfInfoIncomescarryoverdtcode(), FundDateType.每日.getValue())){
//                 // 一体化对应etliu start
////                    finCashPayRule.setPayCycle(DictCodeList.支付频率_付息频率_日付.getValue());
//                    finCashPayRule.setPayCycle(DictCodeList.频率_日.getValue());
//                 // 一体化对应etliu end
//                }else{
//                 // 一体化对应etliu start
////                    finCashPayRule.setPayCycle(DictCodeList.支付频率_付息频率_月付.getValue());
//                    finCashPayRule.setPayCycle(DictCodeList.频率_月.getValue());
//                 // 一体化对应etliu end
//                }
                finCashPayRule.setPayCycle(infChinamutualfunddescription.getPaycycle());//基金基本资料分红频率
                /**--支付频率 直接取基金基本资料分红频率 modify 2019-04-11--*/

                /**------日期类型  modify  2019-04-11 start----*/
                FundDateType[] fundDateTypeArr = FundDateType.values();
//                for(FundDateType fundDateType : fundDateTypeArr){
//                    if(StringUtil.equals(infCmoneymarketfscarryoverm.getfInfoIncomescarryoverdtcode(), fundDateType.getValue())){
//                        finCashPayRule.setDayValue(fundDateType.getDayValue());
//                        finCashPayRule.setDayType(fundDateType.getDayType());
//                    }
//                }
                finCashPayRule.setDayValue(String.valueOf(infChinamutualfunddescription.getDayvalue()));//取基金基本资料分红日
                finCashPayRule.setDayType(DictCodeListExt1.日期类型_几号.getValue());//数据字典表.日期类型.几号
                /**------日期类型  modify  2019-04-11 end-----*/
                finCashPayRule.setFinprodId(finprodId);
                finCashPayRule.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
                finCashPayRule.setBranch(branch);
//                finCashPayRule.setPayType(BussinessDictCodeList.分红方式_红利再投.getValue());
                finCashPayRule.setPayType(infChinamutualfunddescription.getPaytype());
                finCashPayRuleList.add(finCashPayRule);
                baseFinProductBaseInfoDto.setFinCashPayRuleList(finCashPayRuleList);

                /**--增加基金报送信息 add by shenchen 2019-04-11 start--*/
                //new基金报送信息entity
                RepFund repfund = new RepFund();
                RepAssetDept repAssetDept = new RepAssetDept();
                repfund.setRepFinprodId(finprodId);//金融类产品代码
                repfund.setRepIndustry(infChinamutualfunddescription.getRepindustry1());//基金基本资料行业1
                repfund.setRepIndustryLevel2(infChinamutualfunddescription.getRepindustry2());//基金基本资料行业2
                repfund.setRepRegistOrg(infChinamutualfunddescription.getRepregistorg());//基金基本资料登记备案机构
                repfund.setRepIsGovInvest(infChinamutualfunddescription.getRepisgovinvest());//基金基本资料是否属于政府投资基金
                repfund.setRepGovInvestFor(infChinamutualfunddescription.getRepgovinvestfor());//基金基本资料政府投资基本投向
                repfund.setRepTrustPartyName(infChinamutualfunddescription.getReptrustpartyname());//基金基本资料托管机构名称
                repfund.setRepInvestStage(infChinamutualfunddescription.getInvestmentPhase());//基金基本资料投资阶段
                repfund.setRepFundInvestAsset(infChinamutualfunddescription.getRepfundinvestasset());//基金基本资料投资资产
                repAssetDept.setRepFinprodId(finprodId);//金融产品代码
                repAssetDept.setFinprodMarketId(infChinamutualfunddescription.getfInfoCode());//基金代码
                repAssetDept.setRepFinprodName(infChinamutualfunddescription.getfInfoFullname());//债券名称
                repAssetDept.setRepIssuePartyId("C10816");//发行机构代码(参数值)
                repAssetDept.setRepAssetType(exchangeFundType(infChinamutualfunddescription.getfInfoType(), infChinamutualfunddescription.getfInfoType2()));//资产负债类别
                repAssetDept.setRepCcy(infChinamutualfunddescription.getCcy());//币种
                //repAssetBond.setRepSectype(repSectype);具体类别
                try {
                    repAssetDept.setRepStatusDate(sdf.parse(DateUtil.getSysDate()));//数据日期
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                repAssetDept.setRepTradeMarket(infChinamutualfunddescription.getMarket2());//交易流通场所
                repAssetDept.setIsRepTarget(DictCodeList.是否_否.getValue());//默认否
                //[资产/负债类别]为[1106] [1303] [2401] [2402] [2403] [2499] [2601] [2602] [2604] [2605] [2606]
                // [2607] [2610] [2611] [2612] [2613] [2614] [2616] [2617] [2618] [2619] [2620] [2699] 时,必填
                if (StringUtil.isNotEmpty(repAssetDept.getRepAssetType()) &&
                        (StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_债券基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股票基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_货币市场基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_混合基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_基金中基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_其他公募基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_权益类基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_固收类基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_混合类基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_期货类及其他衍生品类基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_其他私募证券投资基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_上市公司定增基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_并购基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_房地产基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_基础设施基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_创业投资基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_红酒艺术品等商品基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_私募证券类FOF基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_其他私募股权投资基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_私募股权投资类FOF基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_创业投资类FOF基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_其他私募投资类FOF基金.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_其他私募基金.getValue()))) {
                    repfund.setRepMngPartyName(getCounterName(infChinamutualfunddescription.getfInfoCorpFundmanagementid()));
                }
                //[交易流通场所][登记备案机构][投资企业类型（按规模划分）][投资企业类型（按技术领域划分）][投资企业类型（按经济类型划分）]选择99的场合，备注必须填
                //[投资阶段]选择99,备注必须填
                if (StringUtil.equals("99", infChinamutualfunddescription.getMarket2()) ||
                        StringUtil.equals("99", infChinamutualfunddescription.getRepregistorg()) ||
                        StringUtil.equals("99", infChinamutualfunddescription.getIssuerScaleType()) ||
                        StringUtil.equals("99", infChinamutualfunddescription.getIssuerTechnologyType()) ||
                        StringUtil.equals("99", infChinamutualfunddescription.getIssuerEconomicType()) ||
                        StringUtil.equals("99", infChinamutualfunddescription.getInvestmentPhase())) {
                    repfund.setRepRemark("无");
                }
                repfund.setRepFundCompName(infChinamutualfunddescription.getFundCompanyName()); // 基金公司名称
                repfund.setRepPartyType1(infChinamutualfunddescription.getIssuerScaleType());//机构类型按规模分
                repfund.setRepPartyType2(infChinamutualfunddescription.getIssuerEconomicType());//机构类型按经济类型分
                repfund.setRepPartyType3(infChinamutualfunddescription.getIssuerTechnologyType());//机构类型按技术领域分

                baseFinProductBaseInfoDto.setRepAssetDept(repAssetDept);//存中债报送界面资产负债类别
                baseFinProductBaseInfoDto.setRepfund(repfund);
                /**--增加基金报送信息 add by shenchen 2019-04-11 end--*/
                baseFinProductBaseInfoList.add(baseFinProductBaseInfoDto);

            }
        }
        dto.setBaseFinProductBaseInfoList(baseFinProductBaseInfoList);
        return dto;
    }

    /**
     * 现金流信息
     * <功能详细描述>
     *
     * @param finprodId
     * @param cashType1
     * @param cashType2
     * @param branch
     * @return
     * @see
     * @since 1.0
     */
    private FinCashInfo encapFinCashInfo(String finprodId, String cashType1, String cashType2, Long branch) {
        FinCashInfo finCashInfo = new FinCashInfo();
        //现金流ID
        String cashId = commonBusinessService.generateCashFlowCode(finprodId, DictCodeListExt1.现金流业务类型_金融产品.getValue(), branch, cashType2);
        finCashInfo.setCashId(cashId);
        finCashInfo.setCashType1(cashType1);
        finCashInfo.setCashType2(cashType2);
        finCashInfo.setBranch(branch);
        finCashInfo.setFinprodId(finprodId);
        finCashInfo.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        return finCashInfo;
    }

    /**
     * 日历信息
     * <功能详细描述>
     *
     * @return
     * @see
     * @since 1.0
     */
    private List<MstCalendarInfo> getMstCalendarInfo() {
        return restTemplate.getForList(ServiceApplication.BASICDATA_SERVICE_NAME
                + "/MstMarketCalendarInfo/getMstCalendarInfo", MstCalendarInfo.class);
    }

    /**
     * 根据外部代码1获取客户代码
     * <功能详细描述>
     *
     * @param linkId1
     * @param type    类型
     * @return
     * @see
     * @since 1.0
     */
    private String getIssuer(String linkId1, String type) {
        return restTemplate.getForObject(ServiceApplication.BASICDATA_SERVICE_NAME
                + "/MstCustomerNature/getIssuer?linkId1=" + linkId1 + "&type=" + type, String.class);
    }

    /**
     * 根据外部代码1获取客户代码
     * <功能详细描述>
     *
     * @param interfPayhsrcsecinfo
     * @return
     * @see
     * @since 1.0
     */
    private String getIssuer(InterfPayhsrcsecinfoDto interfPayhsrcsecinfo) {
        String customerId = restTemplate.getForObject(ServiceApplication.BASICDATA_SERVICE_NAME
                + "/MstCustomerNature/getIssuer?linkId1=" + interfPayhsrcsecinfo.getIssuershort() + "&type=" + "IS_ISSUER", String.class);
        if (StringUtil.isNotEmpty(customerId)) {
        } else {
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共通业务类型_市场机构管理);
            customerId = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();

            MstCustomerInfo mstCustomerInfo = new MstCustomerInfo();
            MstCustomerInfoAddWithBLOBs mstCustomerInfoAdd = new MstCustomerInfoAddWithBLOBs();
            mstCustomerInfo.setCustomerId(customerId);
            mstCustomerInfo.setCustomerName(interfPayhsrcsecinfo.getIssuershort());
            mstCustomerInfo.setIsIssuer("Y");
            /* 发行人加券后客户类型默认：市场机构 add by liuwei 20201118 start */
            mstCustomerInfo.setCustomerType("01");
            /* 发行人加券后客户类型默认：市场机构 add by liuwei 20201118 end */

            mstCustomerInfoAdd.setCustomerId(customerId);
            //发行人所属行业一二级分类
//            String industry = interfPayhsrcsecinfo.getIndustry();
//            if (industry != null && industry.length() == 1) {
//                industry = "0" + industry;
//            }
//            mstCustomerInfoAdd.setIndustry(industry);
//            mstCustomerInfoAdd.setIndustry2(interfPayhsrcsecinfo.getChecktypecode());
//            mstCustomerInfoAdd.setIsListedCompany(interfPayhsrcsecinfo.getIsListed());
            //客户信息表插入数据
            mstCustomerInfoMapper.insert(mstCustomerInfo);
            mstCustomerInfoAddMapper.insert(mstCustomerInfoAdd);
        }
        return customerId;
    }

    /**
     * wind数据字典值转成平台需要的字典值
     * <功能详细描述>
     *
     * @param inputParaDto
     * @return
     * @see
     * @since 1.0
     */
    private String windCovCom(XmlRelationInputParaDto inputParaDto) {
        //数据字典转化
        List<XmlRelationInputParaDto> list = new ArrayList<XmlRelationInputParaDto>();
        inputParaDto.setSysCode(DictCodeListExt1.子系统代码_共通.getValue());
        list.add(inputParaDto);
        XmlRelationOutputDto basisRelationDto = XmlRelationUtil.getOutPut(list);
        if (null != basisRelationDto && null != basisRelationDto.getResultDtoList()) {
            return basisRelationDto.getResultDtoList().get(0).getItemCode();
        }
        return StringUtil.EMPTY;
    }

    /**
     * 基金交易自动加券
     *
     * @param finprodMarketId
     * @see
     * @since 1.0
     */
    public AutoCouponDto autoFundTradeCoupon(String finprodMarketId) {
        InfChinamutualfunddescriptionDto dto = new InfChinamutualfunddescriptionDto();
        //根据前台代码获取wind代码
        List<FinProduct> fWindCode = restTemplate.getForList(
                ServiceApplication.COMMON_SERVICE_NAME + "/core/getFwindCode?finprodMarketId="
                        + finprodMarketId, FinProduct.class);
        if (fWindCode.isEmpty()) {
            return null;
        }
        //判断金融产品表中是否已存在，若存在不自动加券，返回产品信息
        AutoCouponDto finProduct = restTemplate.getForObject(
                ServiceApplication.COMMON_SERVICE_NAME
                        + "/core/selectAutoFinProd?finprodMarketId=" + finprodMarketId,
                AutoCouponDto.class);

        if (StringUtil.isNotEmpty(finProduct) && StringUtil.isNotEmpty(finProduct.getFinprodId())) {
            //不加券
            return finProduct;
        }

        List<String> list = new ArrayList<>();
        list.add(fWindCode.get(0).getFinprodMarketId());
        dto.setfInfoWindcodeList(list);
        try {
            autoFundCoupon(dto);
            List<BaseFinProductBaseInfoDto> baseInfoDtos = dto.getBaseFinProductBaseInfoList();
            if (StringUtil.isNotEmpty(baseInfoDtos)) {
                finProduct = new AutoCouponDto();
                BeanUtil.copyPropertiesIgnoreNull(finProduct, baseInfoDtos.get(0).getFinProduct());
                if (StringUtil.isNotEmpty(baseInfoDtos.get(0).getFinProductType())) {
                    finProduct.setType4(baseInfoDtos.get(0).getFinProductType().getType4());
                }
            }
            return finProduct;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 债券交易自动加券
     *
     * @param finprodMarketId
     * @return
     * @see
     * @since 1.0
     */
    public AutoCouponDto autoBondTradeCoupon(String finprodMarketId) {

        //判断金融产品表中是否已存在，若存在不自动加券，返回产品信息
        AutoCouponDto finProduct = restTemplate.getForObject(
                ServiceApplication.COMMON_SERVICE_NAME
                        + "/core/selectAutoFinProd?finprodMarketId=" + finprodMarketId,
                AutoCouponDto.class);

        if (StringUtil.isNotEmpty(finProduct) && StringUtil.isNotEmpty(finProduct.getFinprodId())) {
            //不加券
            return finProduct;
        }
        InterfPayhsrcsecinfoDto dto = new InterfPayhsrcsecinfoDto();
        List<String> secList = new ArrayList<>();
        secList.add(finprodMarketId);
        dto.setSecidList(secList);
        InterfPayhsrcsecinfoDto newDto = autoBondCoupon(dto);
        List<BaseFinProductBaseInfoDto> baseInfoDtos = newDto.getBaseFinProductBaseInfoList();
        if (StringUtil.isNotEmpty(baseInfoDtos) && !baseInfoDtos.isEmpty()) {
            finProduct = new AutoCouponDto();
            BeanUtil.copyPropertiesIgnoreNull(finProduct, baseInfoDtos.get(0).getFinProduct());
            FinProductType productType = baseInfoDtos.get(0).getFinProductType();
            if (StringUtil.isNotEmpty(productType)) {
                finProduct.setType4(productType.getType4());//设置债券一级分类
                finProduct.setType9(productType.getType9());
                finProduct.setType10(productType.getType10());
                finProduct.setType11(productType.getType11());
                finProduct.setType12(productType.getType12());
            }
        }
        return finProduct;
    }

    /**
     * 一级分类改为人行分类，与中债类型对应
     */
    private String exchangeType(String type) {
        String assettype = null;
        if ("B01".equals(type)) {
            assettype = "1101";//国债
        } else if ("B02".equals(type)) {
            assettype = "1110";//地方政府债
        } else if ("B03".equals(type)) {
            assettype = "1109";//央票
        } else if ("B04".equals(type)) {
            assettype = "1102";//政策性金融债
        } else if ("B05".equals(type)) {
            assettype = "1111";//政府支持性机构债
        } else if ("B06".equals(type)) {
            assettype = "1112";//商业性金融债
        } else if ("B07".equals(type)) {
            assettype = "1113";//企业债
        } else if ("B08".equals(type)) {
            assettype = "1114";//公司债
        } else if ("B09".equals(type)) {
            assettype = "1115";//企业债务融资工具
        } else if ("B10".equals(type)) {
            assettype = "1116";//资产支持证券
        } else if ("B11".equals(type)) {
            assettype = "2502";//他行发行的同业存单
        } else if ("B12".equals(type)) {
            assettype = "1301";//优先股
        } else if ("B13".equals(type)) {
            assettype = "2101";//理财直接融资工具
        }
        return assettype;
    }


    /**
     * F01 货币基金
     * F02 非货币基金
     * 01 债券基金
     * 02 货币市场基金
     * 03 股票基金
     * 04 混合基金
     * 06 其他公募基金
     *
     * @param type
     * @return
     * @see
     * @since 1.0
     */
    private String exchangeFundType(String type, String type2) {
        String assettype = null;
        if ("F01".equals(type)) {
            assettype = "2401";//货币基金
        } else if ("F02".equals(type)) {
            if ("01".equals(type2)) {
                assettype = "1106";//债券基金
            } else if ("02".equals(type2)) {
                assettype = "2401";//货币市场基金
            } else if ("03".equals(type2)) {
                assettype = "1303";//股票基金
            } else if ("04".equals(type2)) {
                assettype = "2402";//混合基金
            } else if ("06".equals(type2)) {
                assettype = "2499";//其他公募基金
            }
        }
        return assettype;
    }

    /**
     * 查询是否存在债券估值行情count
     *
     * @param finprodMarketId
     * @return
     */
    public int selectMstBondValuationInfoCount(String finprodMarketId) {
        List<Integer> intList = restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectMstBondValuationInfoCount?finprodMarketId=" + finprodMarketId,
                Integer.class);
        return intList.size() != 0 ? intList.get(0) : 0;
    }

    /**
     * 查询资讯_债券市价估值list
     *
     * @param finprodMarketId
     * @param sysDate
     * @param newDate
     * @return
     */
    public List<InterfPayhsrccbvlt> selectInterfPayhsrccbvlt(String finprodMarketId, String sysDate, String newDate) {
        return restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectInterfPayhsrccbvlt?finprodMarketId=" + finprodMarketId + "&sysDate=" + sysDate + "&newDate=" + newDate,
                InterfPayhsrccbvlt.class);
    }

    /**
     * 查询资讯_市场行情数据INTERF_BOND_MARKET_INFO
     *
     * @param finprodMarketId
     * @param sysDate
     * @param newDate
     * @return
     */
    public List<InterfBondMarketInfo> selectInterfBondMarketInfo(String finprodMarketId, String sysDate, String newDate) {
        return restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectInterfBondMarketInfo?finprodMarketId=" + finprodMarketId + "&sysDate=" + sysDate + "&newDate=" + newDate,
                InterfBondMarketInfo.class);
    }

    /**
     * 判断当前日期是否存在回购标准券折算率 (FIN_STDSEC_CONVERSE_RADIO)
     *
     * @param finprodMarketId
     * @return
     */
    public int selectFinStdsecConverseRadioCount(String finprodMarketId, String sysDate) {
        List<Integer> intList = restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectFinStdsecConverseRadioCount?finprodMarketId=" + finprodMarketId + "&sysDate=" + sysDate,
                Integer.class);
        return intList.size() != 0 ? intList.get(0) : 0;
    }

    /**
     * 查询资讯_回购标准折算率(INTERF_STDSEC_CON_RADIO)
     *
     * @param finprodMarketId
     * @return
     */
    public List<InterfStdsecConRadio> selectInterfStdsecConRadio(String finprodMarketId, String sysDate) {
        return restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectInterfStdsecConRadio?finprodMarketId=" + finprodMarketId + "&sysDate=" + sysDate,
                InterfStdsecConRadio.class);
    }

    /**
     * 查询是否存在场外行情信息-基金加券
     *
     * @param finprodMarketId
     * @return
     */
    public int selectFinProductValuationCount(String finprodMarketId) {
        List<Integer> intList = restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectFinProductValuationCount?finprodMarketId=" + finprodMarketId,
                Integer.class);
        return intList.size() != 0 ? intList.get(0) : 0;
    }

    /**
     * 查询资讯_基金净值list-基金加券
     *
     * @param finprodMarketId
     * @param sysDate
     * @param newDate
     * @return
     */
    public List<InterfCmfundnav> selectInterfCmfundnav(String finprodMarketId, String sysDate, String newDate) {
        return restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectInterfCmfundnav?finprodMarketId=" + finprodMarketId + "&sysDate=" + sysDate + "&newDate=" + newDate,
                InterfCmfundnav.class);
    }

    /**
     * 判断是否存在基金市场行情信息-基金加券
     *
     * @param finprodMarketId
     * @return
     */
    public int selectMstFundMarketInfoCount(String finprodMarketId) {
        List<Integer> intList = restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectMstFundMarketInfoCount?finprodMarketId=" + finprodMarketId,
                Integer.class);
        return intList.size() != 0 ? intList.get(0) : 0;
    }

    /**
     * 查询资讯_基金市场行情list-基金加券
     *
     * @param finprodMarketId
     * @param newDate
     * @param sysDate
     * @return
     */
    public List<InterfFMarketInfo> selectInterfFMarketInfo(String finprodMarketId, String sysDate, String newDate) {
        return restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectInterfFMarketInfo?finprodMarketId=" + finprodMarketId + "&sysDate=" + sysDate + "&newDate=" + newDate,
                InterfFMarketInfo.class);
    }

    /**
     * 债券估值行情保存(新增)
     * <功能详细描述>
     *
     * @param dto
     * @see
     * @since 1.0
     */
    public void mstBondValuationInfoSave(InterfPayhsrcsecinfoDto dto) {
        restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/mstBondValuationInfoSave", dto,
                Integer.class);
    }

    /**
     * 债券市场行情保存(新增)
     * <功能详细描述>
     *
     * @param dto
     * @see
     * @since 1.0
     */
    public void mstBondMarketInfoSave(InterfPayhsrcsecinfoDto dto) {
        restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/mstBondMarketInfoSave", dto,
                Integer.class);
    }

    /**
     * 回购标准折算率保存(新增)
     * <功能详细描述>
     *
     * @param dto
     * @see
     * @since 1.0
     */
    public void finStdsecConverseRadioSave(InterfPayhsrcsecinfoDto dto) {
        restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/finStdsecConverseRadioSave", dto,
                Integer.class);
    }

    /**
     * 基金-场外金融产品保存(新增)
     * <功能详细描述>
     *
     * @param dto
     * @see
     * @since 1.0
     */
    public void finProductValuationSave(InfChinamutualfunddescriptionDto dto) {
        restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/finProductValuationSave", dto,
                Integer.class);
    }

    /**
     * 基金-基金市场行情保存(新增)
     * <功能详细描述>
     *
     * @param dto
     * @see
     * @since 1.0
     */
    public void mstFundMarketInfoSave(InfChinamutualfunddescriptionDto dto) {
        restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/mstFundMarketInfoSave", dto,
                Integer.class);
    }

    /**
     * 查询客户信息附属表list-债券中债
     *
     * @param customerid
     * @return
     */
    public List<MstCustomerInfoAdd> selectMstCustomerInfoAdd(String customerid) {
        return restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectMstCustomerInfoAdd?customerid=" + customerid,
                MstCustomerInfoAdd.class);
    }

    /**
     * 查询资讯_债券评级信息表
     *
     * @param secid
     * @return
     */
    public List<InterfPayhsrcsecrating> selectInterfPayhsrcsecrating(String secid) {
        return restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectInterfPayhsrcsecrating?secid=" + secid,
                InterfPayhsrcsecrating.class);
    }

    /**
     * 查询资讯_债券评级中主体评级结果
     *
     * @param secid
     * @param date
     * @return
     */
    public String selectRating(String secid, String date) {
        List<InterfPayhsrcsecrating> rateList = restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectRating?secid=" + secid + "&date=" + date,
                InterfPayhsrcsecrating.class);
        return rateList.size() != 0 ? rateList.get(0).getRating() : null;
    }

    /**
     * 自动加券(债券)
     * <功能详细描述>
     *
     * @param dto
     * @see
     * @since 1.0
     */
    public InterfPayhsrcsecinfoDto autoBondCoupon(InterfPayhsrcsecinfoDto dto) {
        autoBondCoupon(dto, dto.getFinprodId());
        // 20201210 债券自动加券bug修正 wangwei start
        // 第一步获取自动加券的信息时，债券发行金额除以了10000，在第二步入库前需要恢复数据
        if (!StringUtil.isEmptyList(dto.getBaseFinProductBaseInfoList())) {
            for (BaseFinProductBaseInfoDto finProductBaseInfoDto : dto.getBaseFinProductBaseInfoList()) {
                if (finProductBaseInfoDto.getFinProduct() != null && finProductBaseInfoDto.getFinProduct().getIssueAmt() != null) {
                    FinProduct finProduct = finProductBaseInfoDto.getFinProduct();
                    finProduct.setIssueAmt(finProduct.getIssueAmt().multiply(new BigDecimal(10000)));
                }
            }
        }
        // 20201210 债券自动加券bug修正 wangwei end

        //校验债券中债报送数据，不符合的债券，不进行加券
        InterfPayhsrcsecinfoDto newInterfPayhsrcsecinfoDto = checkBondRepData(dto);
        String isPermitAddcoupon = CacheUtil.getSysParam(BussinessConstant.IS_PERMIT_ADDCOUPON);
        if (!(StringUtil.isEmptyList(newInterfPayhsrcsecinfoDto.getBaseFinProductBaseInfoList())
                && StringUtil.equals(isPermitAddcoupon, DictCodeList.是否_否.getValue()))) {
            autoBondCouponSave(dto);
            //债券自动加券
            busiAutoAddSecPrice(dto, dto.getFinprodId());
            //债券估值行情保存
            mstBondValuationInfoSave(dto);
            //债券市场行情保存
            mstBondMarketInfoSave(dto);
            //回购标准折算率保存
            finStdsecConverseRadioSave(dto);
            newInterfPayhsrcsecinfoDto.setBaseFinProductBaseInfoList(dto.getBaseFinProductBaseInfoList());
        }
        return newInterfPayhsrcsecinfoDto;
    }


    /**
     * 基金自动加券组件-新增2019-04-13
     *
     * @param dto
     * @param finprodMarketId
     * @return
     * @throws ParseException
     */
    public InfChinamutualfunddescriptionDto busiAutoAddFundPrice(InfChinamutualfunddescriptionDto dto, String finprodMarketId) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Long branch = StringUtil.isEmpty(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)) ? 0 : Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        String sysDate = null;//系统平台日期(字符串类型)
        String newDate = null;//平台日减行情天数(字符串类型)
        Date date = null;//平台日减行情天数(Date类型)
        try {
            if (null != dto.getfInfoWindcodeList() && !dto.getfInfoWindcodeList().isEmpty()) {
                for (String fInfoWindcode : dto.getfInfoWindcodeList()) {
                    //判断是否存在债券估值行情信息
                    int count = selectFinProductValuationCount(fInfoWindcode);
                    SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMdd");
                    Date dateFormat = DateUtil.parse(DateUtil.getSysDate());
                    sysDate = DateUtil.format(dateFormat, "yyyyMMdd");//系统平台日期(字符串类型)
                    //date = sdformat.parse(sysDate);
                    int changeDays = count > 0 ? 0 : -9;//行情天数
                    Calendar rightNow = Calendar.getInstance();
                    rightNow.setTime(dateFormat);
                    rightNow.add(Calendar.DAY_OF_YEAR, changeDays);//根据行情天数做加减
                    newDate = sdformat.format(rightNow.getTime());//获取系统平台日期-行情天数(字符串类型)

                    //查询资讯_基金净值)
                    List<InterfCmfundnav> interfCmfundnavList = selectInterfCmfundnav(fInfoWindcode, sysDate, newDate);
                    //一.如果存在查询结果，则循环构建场外金融产品估值entity
                    List<FinProductValuation> finProductValuationList = new ArrayList<FinProductValuation>();
                    if (null != interfCmfundnavList && interfCmfundnavList.size() != 0) {
                        for (InterfCmfundnav interfCmfundnav : interfCmfundnavList) {
                            //构建场外金融产品估值entity
                            FinProductValuation finProductValuation = new FinProductValuation();
                            finProductValuation.setFinprodId(interfCmfundnav.getfInfoCode());//金融产品代码
                            for (BaseFinProductBaseInfoDto baseInfoDto : dto.getBaseFinProductBaseInfoList()) {
                                if (interfCmfundnav.getfInfoCode().equals(baseInfoDto.getFinProduct().getFinprodMarketId())) {
                                    finProductValuation.setFinprodType(baseInfoDto.getFinProduct().getFinprodType());
                                    finProductValuation.setFinprodType2(baseInfoDto.getFinProduct().getFinprodType2());
                                }
                            }
                            finProductValuation.setValDate(sdformat.parse(interfCmfundnav.getPriceDate()));//估值日期
                            finProductValuation.setPublishDate(sdformat.parse(interfCmfundnav.getAnnDate()));//公告日期
                            finProductValuation.setValuation(interfCmfundnav.getfNavUnit());//单位净值.万份收益
                            finProductValuation.setDay7YearYield(interfCmfundnav.getfInfoYearlyroe());//七日年化收益率
                            finProductValuation.setYearYield(interfCmfundnav.getYearlyroe());//年化收益率
                            finProductValuation.setTotalValue(interfCmfundnav.getfNavAccumulated());//累计净值
                            finProductValuation.setInputType(BussinessDictCodeList.录入方式_接口导入.getValue());//录入方式(数据字典.接入方式.接口录入)
                            finProductValuationList.add(finProductValuation);
                        }
                    }
                    dto.setFinProductValuationList(finProductValuationList);//场外金融产品估值装入

                    //判断是否存在基金市场行情信息
                    int marketCount = selectMstFundMarketInfoCount(fInfoWindcode);
                    date = sdformat.parse(sysDate);
                    changeDays = marketCount > 0 ? 0 : -9;//行情天数
                    newDate = exchangeDate(date, changeDays);//获取系统平台日期-行情天数(字符串类型)
                    //查询资讯_基金市场行情
                    List<InterfFMarketInfo> interfFMarketInfoList = selectInterfFMarketInfo(fInfoWindcode, sysDate, newDate);
                    //如果存在查询结果，则循环构建基金市场行情entity
                    List<MstFundMarketInfo> mstFundMarketInfoList = new ArrayList<MstFundMarketInfo>();
                    if (null != interfFMarketInfoList && interfFMarketInfoList.size() != 0) {
                        for (InterfFMarketInfo interfFMarketInfo : interfFMarketInfoList) {
                            MstFundMarketInfo mstFundMarketInfo = new MstFundMarketInfo();
                            mstFundMarketInfo.setFundCode(interfFMarketInfo.getFundCode());//基金代码
                            mstFundMarketInfo.setValueDate(interfFMarketInfo.getValueDate());//估值日
                            mstFundMarketInfo.setClosePrice(interfFMarketInfo.getClosePrice());//收盘价
                            mstFundMarketInfo.setAveragePrice(interfFMarketInfo.getAveragePrice());//平均价
                            mstFundMarketInfo.setMaxPrice(interfFMarketInfo.getMaxPrice());//最高价
                            mstFundMarketInfo.setMinPrice(interfFMarketInfo.getMinPrice());//最低价
                            mstFundMarketInfo.setBuyOnePrice(interfFMarketInfo.getBuyOnePrice());//买一价
                            mstFundMarketInfo.setSellOnePrice(interfFMarketInfo.getSellOnePrice());//卖一价
                            //mstFundMarketInfo.setValueSource(interfFMarketInfo.getSource());
                            mstFundMarketInfo.setInputType(BussinessDictCodeList.录入方式_接口导入.getValue());//录入方式
                            mstFundMarketInfoList.add(mstFundMarketInfo);
                        }
                    }
                    dto.setMstFundMarketInfoList(mstFundMarketInfoList);//装入dto
                }
            }
            return dto;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 行情天数换算
     *
     * @param date
     * @param changeDays
     * @return 换算后日期
     */
    private String exchangeDate(Date date, int changeDays) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.add(Calendar.DAY_OF_YEAR, changeDays);//根据行情天数做加减
        date = rightNow.getTime();//获取系统平台日期-行情天数(Date类型)
        return sdf.format(date);//获取系统平台日期-行情天数(字符串类型)
    }

    /**
     * 用起息日向后模式推算复习计划，做判断，如果日期
     *
     * @param interfPayhsrcsecinfo
     * @param finCashPayRule
     * @return
     */
    public Date getFirstMdate(InterfPayhsrcsecinfoDto interfPayhsrcsecinfo, FinCashPayRule finCashPayRule) {
        FinPayInterestStdInfoDto finPayInterestStdInfoDto = new FinPayInterestStdInfoDto();
        //资产公共DTO
        AssetCommonInfoDto scai = new AssetCommonInfoDto();
        BeanUtil.copyPropertiesIgnoreNull(scai, interfPayhsrcsecinfo); //复制资产信息
        //到期日+15天
        Date mdate = DateUtil.parse(DateUtil.plusDays(DateUtil.format(scai.getMdate()), 15));
        //获取节假日
        List<String> holidayList = dateCalService.getDealCalendarHoliday(scai.getVdate(), mdate, scai.getCalendarId());
        //如果首次付息日=第一条计划的支付日，推算方式为起息日向后
        finCashPayRule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());
        List<FinCashPlanDto> invList = AssetInterestCal.cal(finCashPayRule, scai, holidayList, false, FinCashPlanDto.class);
        //排序
        Collections.sort(invList, new Comparator<FinCashPlanDto>() {
            @Override
            public int compare(FinCashPlanDto o1, FinCashPlanDto o2) {
                return o1.getVdate().compareTo(o2.getVdate());
            }
        });
        if (StringUtil.isNotEmpty(invList) && invList.size() > 0) {
            return invList.get(0).getMdate();
        }
        return null;
    }

    /**
     * 批量自动加券功能
     * add by zhouqiang
     *
     * @return
     */
    public AutoCouponDto autoBondCouponBatch(String secid) {
        InterfPayhsrcsecinfoDto interfPayhsrcsecinfoDto = new InterfPayhsrcsecinfoDto();
        List<String> secList = new ArrayList<>();
        //判断金融产品表中是否已存在，若存在不自动加券，返回产品信息
        AutoCouponDto finProduct = simpleRestTemplate.getForObject(
                ServiceApplication.COMMON_SERVICE_NAME
                        + "/core/selectAutoFinProd?finprodMarketId=" + secid,
                AutoCouponDto.class);
        if (StringUtil.isNotEmpty(finProduct)) {
            return finProduct;
        }
        secList.add(secid);
        interfPayhsrcsecinfoDto.setSecidList(secList);
        //自动加券
        autoBondCoupon(interfPayhsrcsecinfoDto);
        List<BaseFinProductBaseInfoDto> baseInfoDtos = interfPayhsrcsecinfoDto.getBaseFinProductBaseInfoList();
        if (StringUtil.isNotEmpty(baseInfoDtos)) {
            finProduct = new AutoCouponDto();
            BeanUtil.copyPropertiesIgnoreNull(finProduct, baseInfoDtos.get(0).getFinProduct());
            if (StringUtil.isNotEmpty(baseInfoDtos.get(0).getFinProductType())) {
                finProduct.setType4(baseInfoDtos.get(0).getFinProductType().getType4());//设置债券一级分类
            }
        }
        return finProduct;

    }

    /**
     * 查询万德接口表中债券所有的利率信息
     * 徽商个性化：add by zhouqiang
     *
     * @param finCashCalcRule
     * @return
     */
    public List<FinCashCalcRule> getFinCashCalcRule(FinCashCalcRule finCashCalcRule) {
        return simpleRestTemplate.postForList(
                ServiceApplication.ASSET_SERVICE_NAME + "/FinBondTargetAsset/getFinCashCalcRule", finCashCalcRule,
                FinCashCalcRule.class);
    }

    /**
     * 自动加券(股票)
     * <功能详细描述>
     *
     * @param dto
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    public InterfStockInfoDto autoStockCoupon(InterfStockInfoDto dto, String finprodMarketId) {
        List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList = new ArrayList<BaseFinProductBaseInfoDto>();
        Long branch = StringUtil.isEmpty(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)) ? 0 : Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        String defaultNumber = "0";
        BigDecimal defaultBigNumber = BigDecimal.ZERO;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (null != dto.getStockCodeList() && StringUtil.isNotEmpty(dto.getStockCodeList())) {
            for (String stockCode : dto.getStockCodeList()) {
                BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
                //从万德接口表中查询股票基本信息数据
                InterfStockInfoDto interfStockInfoDto = selectWindStockAssetByStockCode(stockCode);
                if (null == interfStockInfoDto || !StringUtil.isNotEmpty(interfStockInfoDto)) {
                    return null;
                }
                FinProduct finProduct = new FinProduct();
                String finprodId = commonBusinessService.getFinProdId(interfStockInfoDto.getAssetcode(), BussinessDictCodeList.金融产品类型_股票.getValue(), StringUtil.EMPTY);
                finProduct.setFinprodId(finprodId);
                finProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
                finProduct.setFinprodAbbr(interfStockInfoDto.getAssetname());
                finProduct.setFinprodName(interfStockInfoDto.getAssetname());
                finProduct.setFinprodType(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
                //交易场所
                finProduct.setTradeMarket(interfStockInfoDto.getTradeplace());
                finProduct.setVdate(interfStockInfoDto.getListingdate());//上市日期
                finProduct.setMdate(interfStockInfoDto.getDelistingdate());//退市日期
                finProduct.setCcy(interfStockInfoDto.getCcy());//币种
                finProduct.setCalendarId(interfStockInfoDto.getTradecalender());//交易日历
                finProduct.setActmdate(interfStockInfoDto.getDelistingdate());//实际到期日
                finProduct.setBlnArea(DictCodeList.境内外_境内.getValue());
                finProduct.setCreateUser("interf");
                finProduct.setFinprodMarketId(interfStockInfoDto.getAssetcode());
                finProduct.setIssuer(interfStockInfoDto.getCompanycode());//发行人
                finProduct.setIsChl(DictCodeList.是否_否.getValue());//是否通道
                finProduct.setProfitType(DictCodeList.收益类型_净值型.getValue());//收益类型
                baseFinProductBaseInfoDto.setFinProduct(finProduct);
                //new标的类金融产品附属
                FinProductAdd finProductAdd = new FinProductAdd();
                finProductAdd.setFinprodId(finprodId);
                finProductAdd.setBoard(interfStockInfoDto.getListingboard());//上市板
                finProductAdd.setShOrZh(interfStockInfoDto.getIshorsexpress());//是否沪港通或深港通
                finProductAdd.setIsMarginFinprod(interfStockInfoDto.getIsmargintrading());//是否融资融券标的
                finProductAdd.setCreateUser("interf");
                finProductAdd.setUpdateUser("interf");
                baseFinProductBaseInfoDto.setFinProductAdd(finProductAdd);
                FinProductType finProductType = new FinProductType();
                finProductType.setFinprodId(finprodId);
                finProductType.setBranch(branch);
                finProductType.setType1(interfStockInfoDto.getType1());
                finProductType.setType2(interfStockInfoDto.getType2());
                finProductType.setType3(interfStockInfoDto.getType3());
                finProductType.setType6(interfStockInfoDto.getType2());
                finProductType.setType8(interfStockInfoDto.getType8());
                finProductType.setCreateUser("interf");
                finProductType.setUpdateUser("interf");
                baseFinProductBaseInfoDto.setFinProductType(finProductType);
                RepAssetDept repAssetDept = new RepAssetDept();
                repAssetDept.setRepFinprodId(finprodId);
                repAssetDept.setFinprodMarketId(stockCode);
                repAssetDept.setRepFinprodName(interfStockInfoDto.getAssetname());
                repAssetDept.setRepIssuePartyId("C10403");//发行机构代码
                repAssetDept.setRepAssetType("1302");//资产负债类别
                repAssetDept.setRepCcy(interfStockInfoDto.getCcy());
                repAssetDept.setRepTradeMarket(interfStockInfoDto.getTradeplace());
                repAssetDept.setCreateUser("interf");
                repAssetDept.setUpdateUser("interf");
                baseFinProductBaseInfoDto.setRepAssetDept(repAssetDept);
                BaseRepAssetEquityInfoDto baseRepAssetEquityInfoDto = new BaseRepAssetEquityInfoDto();
                List<RepAssetEquity> repAssetEquityList = new ArrayList<RepAssetEquity>();
                RepAssetEquity repAssetEquity = new RepAssetEquity();
                //[资产/负债类别]为[1301] [1302] [1305] 时,必填
                if (StringUtil.isNotEmpty(repAssetDept.getRepAssetType()) &&
                        (StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股权.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股票_二级市场.getValue()) ||
                                StringUtil.equals(repAssetDept.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股票_一级市场.getValue()))) {
                    repAssetEquity.setRepDeptEquity("01");
                }
                //[交易流通场所][股票类型]企业类型（按规模划分）,企业类型（按技术领域划分）,企业类型（按经济类型划分）选择“99”时，备注必填
                //投资阶段默认为06,不额外做校验
                if (StringUtil.equals("99", interfStockInfoDto.getTradeplace()) ||
                        StringUtil.equals("99", interfStockInfoDto.getStocktype()) ||
                        StringUtil.equals("99", interfStockInfoDto.getEnterprisetype1()) ||
                        StringUtil.equals("99", interfStockInfoDto.getEnterprisetype2()) ||
                        StringUtil.equals("99", interfStockInfoDto.getEnterprisetype3())) {
                    repAssetEquity.setRepRemark("无");
                }
                repAssetEquity.setRepFinprodId(finprodId);
                repAssetEquity.setRepSharesId(stockCode);
                repAssetEquity.setRepSharesType(interfStockInfoDto.getStocktype());//股票类型
                repAssetEquity.setRepIndustry(interfStockInfoDto.getIndustryn());//行业一级分类
                repAssetEquity.setRepIndustryLevel2(interfStockInfoDto.getIndustrynSecond());//行业二级分类
                repAssetEquity.setRepInvestStage("06");//投资阶段[默认为“已上市”]
                repAssetEquity.setRepPartyType1(interfStockInfoDto.getEnterprisetype1());//企业类型（按规模划分）
                repAssetEquity.setRepPartyType2(interfStockInfoDto.getEnterprisetype2());//企业类型（按经济类型划分）
                repAssetEquity.setRepPartyType3(interfStockInfoDto.getEnterprisetype3());//企业类型（按技术领域划分）
                repAssetEquity.setRepIsPledge("02");//是否为质押融资
                repAssetEquity.setCreateUser("interf");
                repAssetEquity.setUpdateUser("interf");
                repAssetEquityList.add(repAssetEquity);
                baseRepAssetEquityInfoDto.setRepAssetEquityList(repAssetEquityList);
                baseFinProductBaseInfoDto.setRepAssetEquity(repAssetEquity);
                baseFinProductBaseInfoDto.setBaseRepAssetEquityInfoDto(baseRepAssetEquityInfoDto);
                //构建JXS_ISSUE_INFO
                JxsIssueInfo jxsIssueInfo = new JxsIssueInfo();
                jxsIssueInfo.setActivedate(interfStockInfoDto.getListingdate());//激活日期
                jxsIssueInfo.setCashflowcfg(defaultNumber);//现金流配置
                jxsIssueInfo.setCashflowcfg2(defaultNumber);//空方现金流配置
                jxsIssueInfo.setContractsize(defaultBigNumber);
                jxsIssueInfo.setCountrycode("CN");
                jxsIssueInfo.setCreateuser("interf");
                jxsIssueInfo.setUpdateUser("interf");
                jxsIssueInfo.setEndprice(defaultBigNumber);
                jxsIssueInfo.setExchangecode(interfStockInfoDto.getTradeplace());
                jxsIssueInfo.setFixincometype(defaultBigNumber);
                jxsIssueInfo.setGlgroup(interfStockInfoDto.getGlgroup());
                jxsIssueInfo.setIncomecur(DictCodeListExt1.币种_中债_人民币.getValue());
                jxsIssueInfo.setIncomecur2(DictCodeListExt1.币种_中债_人民币.getValue());
                jxsIssueInfo.setIssuer("5002");
                jxsIssueInfo.setOptiontype(defaultBigNumber);
                jxsIssueInfo.setPricur(DictCodeListExt1.币种_中债_人民币.getValue());
                jxsIssueInfo.setSecurityid(finprodId);
                jxsIssueInfo.setSecurityname(interfStockInfoDto.getAssetname());
                jxsIssueInfo.setSourceid(defaultBigNumber);
                jxsIssueInfo.setStartprice(defaultBigNumber);
                jxsIssueInfo.setSwapexchangeind(defaultBigNumber);
                jxsIssueInfo.setRpsectype(interfStockInfoDto.getRpsectype());
                baseFinProductBaseInfoDto.setJxsIssueInfo(jxsIssueInfo);
                baseFinProductBaseInfoList.add(baseFinProductBaseInfoDto);
            }

            dto.setBaseFinProductBaseInfoList(baseFinProductBaseInfoList);
        }
        return dto;
    }

    /**
     * 获取wind资产信息(股票)
     * <功能详细描述>
     *
     * @param stockCode
     * @return
     * @see
     * @since 1.0
     */
    public InterfStockInfoDto selectWindStockAssetByStockCode(String stockCode) {
        return simpleRestTemplate.getForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectWindStockAssetByStockCode?stockCode=" + stockCode,
                InterfStockInfoDto.class);
    }

    /**
     * 徽商功能移植
     * new 自动加券(债券) 中债要素check
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public InterfPayhsrcsecinfoDto checkBondRepData(InterfPayhsrcsecinfoDto dto) {
        InterfPayhsrcsecinfoDto interfPayhsrcsecinfoDtoNew = new InterfPayhsrcsecinfoDto();
        List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList = new ArrayList<BaseFinProductBaseInfoDto>();
        List<String> errorAssetInfo = new ArrayList<>();
        if (null != dto.getBaseFinProductBaseInfoList() && !StringUtil.isEmptyList(dto.getBaseFinProductBaseInfoList())) {
            for (BaseFinProductBaseInfoDto baseInfoDto1 : dto.getBaseFinProductBaseInfoList()) {
                FinBondTargetAssetDto bondTargetDto = new FinBondTargetAssetDto();
                List<BaseFinProductBaseInfoDto> baseList = new ArrayList<>();
                BaseFinProductBaseInfoDto baseInfoDto2 = new BaseFinProductBaseInfoDto();
                BeanUtil.copyPropertiesIgnoreNull(baseInfoDto2, baseInfoDto1);
                baseList.add(baseInfoDto2);
                bondTargetDto.setRepAssetType(baseInfoDto1.getRepAssetType());
                bondTargetDto.setBaseFinProductBaseInfoList(baseList);
                //中债要素
                BussFinBondRepInfoDto bussFinBondRepInfoDto = new BussFinBondRepInfoDto();
                bussFinBondRepInfoDto.setRepPartyType1(baseInfoDto1.getRepAssetBond().getRepPartyType1());
                bussFinBondRepInfoDto.setRepPartyType2(baseInfoDto1.getRepAssetBond().getRepPartyType2());
                bussFinBondRepInfoDto.setRepPartyType3(baseInfoDto1.getRepAssetBond().getRepPartyType3());
                bussFinBondRepInfoDto.setRepRegistOrg(baseInfoDto1.getRepAssetBond().getRepRegistOrg());
                bussFinBondRepInfoDto.setRepSectype(baseInfoDto1.getRepAssetBond().getRepSectype());//具体类别
                bussFinBondRepInfoDto.setRepTradeMarket(baseInfoDto1.getRepAssetDept().getRepTradeMarket());
                bussFinBondRepInfoDto.setRepRegistOrg(baseInfoDto1.getRepAssetBond().getRepRegistOrg());
                bussFinBondRepInfoDto.setRepAssetType(baseInfoDto1.getRepAssetDept().getRepAssetType());
                bussFinBondRepInfoDto.setRepIndustry(baseInfoDto1.getRepAssetBond().getRepIndustry());
                bussFinBondRepInfoDto.setRepIndustryLevel2(baseInfoDto1.getRepAssetBond().getRepIndustryLevel2());
                bussFinBondRepInfoDto.setRepRegistorgComment(baseInfoDto1.getRepAssetBond().getRepRegistorgComment());//登记托管机构说明
                bussFinBondRepInfoDto.setRepRemark(baseInfoDto1.getRepAssetBond().getRepRemark());
                bondTargetDto.setBussFinBondRepInfoDto(bussFinBondRepInfoDto);
                bondTargetDto.setFinprodMarketId(baseInfoDto1.getFinProduct().getFinprodMarketId());
                bondTargetDto.setFinprodName(baseInfoDto1.getFinProduct().getFinprodName());
                bondTargetDto.setIssueType(baseInfoDto1.getFinProduct().getIssueType());
                bondTargetDto.setMdate(baseInfoDto1.getFinProduct().getMdate());
                bondTargetDto.setRegistOrg(baseInfoDto1.getRepAssetBond().getRepRegistOrg());
                List<ValidMessage> resultList = new ArrayList<ValidMessage>();
                resultList = chbInvDataCheckService.chbCheckCommon(resultList, bondTargetDto, "FinBondTargetAsset002Interf", bondTargetDto.getRepAssetType());
                //中债校验通过
                if (StringUtil.isEmptyList(resultList)) {
                    baseFinProductBaseInfoList.add(baseInfoDto1);
                } else {
                    //中债校验不通过的数据
                    StringBuilder sb = new StringBuilder(StringUtils.EMPTY);
                    sb.append(GlobalConstant.HTML_NEWLINE + "债券代码：" + baseInfoDto1.getFinProduct().getFinprodMarketId() + GlobalConstant.HTML_NEWLINE);
                    sb.append("债券名称：" + baseInfoDto1.getFinProduct().getFinprodAbbr() + GlobalConstant.HTML_NEWLINE);
                    sb.append("校验未通过原因：");
                    resultList.forEach(item -> {
                        sb.append(GlobalConstant.HTML_NEWLINE + item.getMessage() + "。");
                    });
                    //换行使展示整齐
                    sb.append(GlobalConstant.HTML_NEWLINE);
                    errorAssetInfo.add(sb.toString());
                }
            }
            interfPayhsrcsecinfoDtoNew.setBaseFinProductBaseInfoList(baseFinProductBaseInfoList);
            interfPayhsrcsecinfoDtoNew.setErrorBondList(errorAssetInfo);
        }
        return interfPayhsrcsecinfoDtoNew;
    }

    /**
     * 徽商功能移植
     * new 自动加券(基金) 中债要素check
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public InfChinamutualfunddescriptionDto checkFundRepData(InfChinamutualfunddescriptionDto dto) {
        InfChinamutualfunddescriptionDto infChinamutualfunddescriptionNew = new InfChinamutualfunddescriptionDto();
        List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList = new ArrayList<BaseFinProductBaseInfoDto>();
        List<String> errorAssetInfo = new ArrayList<>();
        if (null != dto.getBaseFinProductBaseInfoList() && !StringUtil.isEmptyList(dto.getBaseFinProductBaseInfoList())) {
            for (BaseFinProductBaseInfoDto baseInfoDto1 : dto.getBaseFinProductBaseInfoList()) {
                FinFundTargetDto finFundTargetDto = new FinFundTargetDto();
                List<BaseFinProductBaseInfoDto> baseInfoList = new ArrayList<>();
                BaseFinProductBaseInfoDto baseInfoDto2 = new BaseFinProductBaseInfoDto();
                BeanUtil.copyPropertiesIgnoreNull(baseInfoDto2, baseInfoDto1);
                baseInfoList.add(baseInfoDto2);
                BussRepFundInfoDto bussRepFundInfoDto = new BussRepFundInfoDto();
                bussRepFundInfoDto.setRepFund(baseInfoDto1.getRepfund());
//    			bussRepFundInfoDto.setFinancialComissue(baseInfoDto1.getRepfund().getFinancialComissue());//是否由金融资产投资公司发行[万德未提供该字段] 不支持私募基金，不用加此字段
                bussRepFundInfoDto.setRepAssetType(baseInfoDto1.getRepAssetDept().getRepAssetType());
                //是否投向“市场化债转股相关产品的部分”不在值域范围内,万德未提供该字段，无法校验
                bussRepFundInfoDto.setRepFundCompName(baseInfoDto1.getRepfund().getRepFundCompName());
                bussRepFundInfoDto.setRepFundInvestAsset(baseInfoDto1.getRepfund().getRepFundInvestAsset());//基金投资资产
                bussRepFundInfoDto.setRepGovInvestFor(baseInfoDto1.getRepfund().getRepGovInvestFor());
                bussRepFundInfoDto.setRepIndustry(baseInfoDto1.getRepfund().getRepIndustry());
                bussRepFundInfoDto.setRepIndustryLevel2(baseInfoDto1.getRepfund().getRepIndustryLevel2());
                //人行机构规模,万德未提供该字段，无法校验
                bussRepFundInfoDto.setRepInvestStage(baseInfoDto1.getRepfund().getRepInvestStage());
                //公共PPP不在值域范围内、投向地方政府融资平台要素不在值域范围内。万德未提供该字段，无法校验
                bussRepFundInfoDto.setRepRegistOrg(baseInfoDto1.getRepfund().getRepRegistOrg());
                //登记备案机构为“99 其他”;投资企业类型（规模）为“99 其他”;当投资企业类型（经济类型）为“99 其他”;当投资企业类型（技术领域）为“99 其他”;交易流通场所为“99”;备注信息默认“无”
                if (StringUtil.equals(BussinessDictCodeList.中债_登记托管机构_其他.getValue(), baseInfoDto1.getRepfund().getRepRegistOrg())
                        || StringUtil.equals(DictCodeListExt2.发行机构类型_按技术领域划分_其他.getValue(), baseInfoDto1.getRepfund().getRepRegistOrg())
                        || StringUtil.equals(DictCodeListExt2.发行机构类型_按经济类型划分_其他.getValue(), baseInfoDto1.getRepfund().getRepRegistOrg())
                        || StringUtil.equals(DictCodeListExt2.发行机构类型_按规模划分_其他.getValue(), baseInfoDto1.getRepfund().getRepRegistOrg())
                        || StringUtil.equals(DictCodeListExt2.发行机构类型_按规模划分_其他.getValue(), baseInfoDto1.getRepAssetDept().getRepTradeMarket())
                        || StringUtil.equals(DictCodeListExt2.投资阶段_其他.getValue(), baseInfoDto1.getRepfund().getRepInvestStage())) {
                    bussRepFundInfoDto.setRepRemark("无备注信息");
                } else {
                    bussRepFundInfoDto.setRepRemark(baseInfoDto1.getRepfund().getRepRemark());//备注[万德未提供该字段]
                }
                bussRepFundInfoDto.setRepTradeMarket(baseInfoDto1.getRepAssetDept().getRepTradeMarket());
                bussRepFundInfoDto.setRepTrustPartyName(baseInfoDto1.getRepfund().getRepTrustPartyName());
                bussRepFundInfoDto.setRepIsGovInvest(baseInfoDto1.getRepfund().getRepIsGovInvest());//当是否属于政府投资基金
                bussRepFundInfoDto.setRepPartyType1(baseInfoDto1.getRepfund().getRepPartyType1());
                bussRepFundInfoDto.setRepPartyType2(baseInfoDto1.getRepfund().getRepPartyType2());
                bussRepFundInfoDto.setRepPartyType3(baseInfoDto1.getRepfund().getRepPartyType3());
                bussRepFundInfoDto.setRepFund(null);//调用chbCheckCommon方法时，该子对象需要置空，否则会影响取值
                finFundTargetDto.setFinprodMarketId(baseInfoDto1.getFinProduct().getFinprodMarketId());
                finFundTargetDto.setFinprodName(baseInfoDto1.getFinProduct().getFinprodName());
                finFundTargetDto.setBussRepFundInfoDto(bussRepFundInfoDto);
                List<ValidMessage> resultList = new ArrayList<ValidMessage>();
                resultList = chbInvDataCheckService.chbCheckCommon(resultList, finFundTargetDto, "FinFundTarget002Interf", null);
                //中债校验通过
                if (StringUtil.isEmptyList(resultList)) {
                    baseFinProductBaseInfoList.add(baseInfoDto1);
                } else {
                    //中债校验不通过的数据
                    StringBuilder sb = new StringBuilder(StringUtils.EMPTY);
                    sb.append(GlobalConstant.HTML_NEWLINE + "基金代码：" + baseInfoDto1.getFinProduct().getFinprodMarketId() + GlobalConstant.HTML_NEWLINE);
                    sb.append("基金名称：" + baseInfoDto1.getFinProduct().getFinprodAbbr() + GlobalConstant.HTML_NEWLINE);
                    sb.append("校验未通过原因：");
                    resultList.forEach(item -> {
                        sb.append(GlobalConstant.HTML_NEWLINE + item.getMessage() + "。");
                    });
                    //换行使展示整齐
                    sb.append(GlobalConstant.HTML_NEWLINE);
                    errorAssetInfo.add(sb.toString());
                }
            }
            infChinamutualfunddescriptionNew.setBaseFinProductBaseInfoList(baseFinProductBaseInfoList);
            infChinamutualfunddescriptionNew.setErrorFundList(errorAssetInfo);
        }
        return infChinamutualfunddescriptionNew;

    }


    public InterfStockInfoDto checkStockRepData(InterfStockInfoDto dto) {
        InterfStockInfoDto interfStockInfoDtoNew = new InterfStockInfoDto();
        List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList = new ArrayList<BaseFinProductBaseInfoDto>();
        List<String> errorAssetInfo = new ArrayList<>();
        if (null != dto.getBaseFinProductBaseInfoList() && !StringUtil.isEmptyList(dto.getBaseFinProductBaseInfoList())) {
            for (BaseFinProductBaseInfoDto baseInfoDto1 : dto.getBaseFinProductBaseInfoList()) {
                FinStockInfoDto finStockInfoDto = new FinStockInfoDto();
                List<BaseFinProductBaseInfoDto> baseInfoList = new ArrayList<>();
                BaseFinProductBaseInfoDto baseInfoDto2 = new BaseFinProductBaseInfoDto();
                BeanUtil.copyPropertiesIgnoreNull(baseInfoDto2, baseInfoDto1);
                baseInfoList.add(baseInfoDto2);
                BussRepEquityInfoDto bussRepEquityInfoDto = new BussRepEquityInfoDto();
                //是否投向“市场化债转股相关产品的部分”、是否为债转股不在值域范围内,万德未提供该字段，无法校验
                bussRepEquityInfoDto.setRepIndustry(baseInfoDto1.getRepAssetEquity().getRepIndustry());//行业一级分类
                bussRepEquityInfoDto.setRepIndustryLevel2(baseInfoDto1.getRepAssetEquity().getRepIndustryLevel2());//行业二级分类
                bussRepEquityInfoDto.setRepPartyType1(baseInfoDto1.getRepAssetEquity().getRepPartyType1());
                bussRepEquityInfoDto.setRepPartyType2(baseInfoDto1.getRepAssetEquity().getRepPartyType2());
                bussRepEquityInfoDto.setRepPartyType3(baseInfoDto1.getRepAssetEquity().getRepPartyType3());
                //人行机构规模要素不在值域范围内。万德未提供该字段，无法校验
                bussRepEquityInfoDto.setRepInvestStage(baseInfoDto1.getRepAssetEquity().getRepInvestStage());//投资阶段
                bussRepEquityInfoDto.setRepIsPledge(baseInfoDto1.getRepAssetEquity().getRepIsPledge());//是否为质押融资
                //公共PPP不在值域范围内、是否由金融资产投资公司发行要素必须为空、投向地方政府融资平台要素不在值域范围内。万德未提供该字段，无法校验
                bussRepEquityInfoDto.setRepRemark(baseInfoDto1.getRepAssetEquity().getRepRemark());//备注[万德未提供该字段]
                bussRepEquityInfoDto.setRepSharesType(baseInfoDto1.getRepAssetEquity().getRepSharesType());//股票类型
                bussRepEquityInfoDto.setRepDeptEquity(baseInfoDto1.getRepAssetEquity().getRepDeptEquity());//是否为债转股
                finStockInfoDto.setBussRepEquityInfoDto(bussRepEquityInfoDto);
                finStockInfoDto.setFinprodMarketId(baseInfoDto1.getFinProduct().getFinprodMarketId());
                finStockInfoDto.setFinprodName(baseInfoDto1.getFinProduct().getFinprodName());
                List<ValidMessage> resultList = new ArrayList<ValidMessage>();
                resultList = chbInvDataCheckService.chbCheckCommon(resultList, finStockInfoDto, "FinStockInfo002Interf", null);
                //中债校验通过
                if (StringUtil.isEmptyList(resultList)) {
                    baseFinProductBaseInfoList.add(baseInfoDto1);
                } else {
                    //中债校验不通过的数据
                    StringBuilder sb = new StringBuilder(StringUtils.EMPTY);

                    sb.append(GlobalConstant.HTML_NEWLINE + "股票代码：" + baseInfoDto1.getFinProduct().getFinprodMarketId() + GlobalConstant.HTML_NEWLINE);
                    sb.append("股票名称：" + baseInfoDto1.getFinProduct().getFinprodAbbr() + GlobalConstant.HTML_NEWLINE);
                    sb.append("校验未通过原因：");
                    resultList.forEach(item -> {
                        sb.append(GlobalConstant.HTML_NEWLINE + item.getMessage() + "。");
                    });
                    //换行使展示整齐
                    sb.append(GlobalConstant.HTML_NEWLINE);
                    errorAssetInfo.add(sb.toString());
                }
            }
            interfStockInfoDtoNew.setBaseFinProductBaseInfoList(baseFinProductBaseInfoList);
            //错误信息
            interfStockInfoDtoNew.setErrorStockList(errorAssetInfo);
        }
        return interfStockInfoDtoNew;

    }


    public List<InterfStockInfoDto> selectWindStockAsset(Map<String, Object> paramMap) {
        return simpleRestTemplate.postForList(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectWindStockAsset", paramMap,
                InterfStockInfoDto.class);
    }

    /**
     * 自动加券(股票)
     * <功能详细描述>
     *
     * @param dto
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    public InterfStockInfoDto autoStockCoupon(InterfStockInfoDto dto) throws ParseException {
        autoStockCoupon(dto, dto.getAssetcode());
        //中债要素check
        InterfStockInfoDto newInterfStockInfoDto = checkStockRepData(dto);
        String isPermitAddcoupon = CacheUtil.getSysParam(BussinessConstant.IS_PERMIT_ADDCOUPON);
        if (!(StringUtil.isEmptyList(newInterfStockInfoDto.getBaseFinProductBaseInfoList())
                && StringUtil.equals(isPermitAddcoupon, DictCodeList.是否_否.getValue()))) {
            autoStockCouponSave(newInterfStockInfoDto);
            newInterfStockInfoDto.setBaseFinProductBaseInfoList(dto.getBaseFinProductBaseInfoList());
        }
        return newInterfStockInfoDto;
    }

    /**
     * 自动加券保存(股票)
     * <功能详细描述>
     *
     * @param dto
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    private void autoStockCouponSave(InterfStockInfoDto dto) {
        simpleRestTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/autoStockCouponSave", dto,
                Integer.class);

    }

    /**
     * 查询交易对手名称
     *
     * @return
     */
    public String getCounterName(String counterId) {
        return restTemplate.getForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrProject/getCounterName?counterId=" + counterId,
                String.class);
    }
}