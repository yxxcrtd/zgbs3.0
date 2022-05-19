package com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.MstCustomerInfoAddMapper;
import com.joyintech.tams.common.base.dao.MstCustomerInfoMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.component.business.common.dto.InterfPayhsrcsecinfoDto;
import com.joyintech.tams.component.business.common.service.CommonAutoCouponService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dao.FinListedBondInfoQueryMapper;
import com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dto.dto.FinListedBondInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dto.req.FinListedBondInfoQueryRequest;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 待上市债券市场信息查询服务类
 *
 * @author zhuys
 * @version 1.0 2022年4月11日
 * @see FinListedBondInfoQuery001Service
 * @since 1.0
 */
@Service
public class FinListedBondInfoQuery001Service extends BaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Resource
    private BaseGetBusinessId bussGetBussinessIdService;
    @Resource
    private CommonAutoCouponService commonAutoCouponService;
    @Resource
    private CommonBusinessService commonBusinessService;
    @Resource
    private DateCalService dateCalService;
    @Resource
    private FinListedBondInfoQueryMapper finListedBondInfoQueryMapper;
    @Resource
    private MstCustomerInfoMapper mstCustomerInfoMapper;
    @Resource
    private MstCustomerInfoAddMapper mstCustomerInfoAddMapper;
    @Resource
    private FinProductMapper finProductMapper;
    @Resource
    private IPlatformDate iPlatformDate;

    /**
     * 待上市债券市场信息分页查询
     *
     * @param request
     * @return
     */
    public Page<FinListedBondInfoQuery001Dto> queryForPage(FinListedBondInfoQueryRequest request) {
        logger.info("queryForPage >> 待上市债券市场信息分页查询参数：{}", JSON.toJSONString(request));
        request.setPlatformDate(iPlatformDate.getSysDateStr());
        PageHelper.startPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize());
        List<FinListedBondInfoQuery001Dto> finListedBondInfoQuery001Dtos = finListedBondInfoQueryMapper.queryForPage(request);
        return new Page<>(PageInfo.of(finListedBondInfoQuery001Dtos));
    }

    /**
     * 待上市接口加券市场信息查询
     *
     * @param request
     * @return
     */
    public FinListedBondInfoQuery001Dto queryFinWindMarketForCoupons(FinListedBondInfoQueryRequest request) {
        logger.info("queryFinBondMarketForCoupons >> 待上市接口加券市场信息查询参数：{}", JSON.toJSONString(request));
        request.setPlatformDate(iPlatformDate.getSysDateStr());
        FinListedBondInfoQuery001Dto finListedBondInfoQuery001Dto = finListedBondInfoQueryMapper.queryFinWindMarketForCoupons(request);
        return finListedBondInfoQuery001Dto;
    }


    /**
     * 待上市加券市场信息查询
     *
     * @param request
     * @return
     */
    public FinListedBondInfoQuery001Dto queryFinProductForCoupons(FinListedBondInfoQueryRequest request) {
        logger.info("queryFinProductForCoupons >> 加券市场信息查询参数：{}", JSON.toJSONString(request));
        request.setPlatformDate(iPlatformDate.getSysDateStr());
        FinListedBondInfoQuery001Dto finListedBondInfoQuery001Dto = finListedBondInfoQueryMapper.queryFinProductForCoupons(request);
        return finListedBondInfoQuery001Dto;
    }


    /**
     * 加券
     *
     * @param secId
     * @return
     */
    public List<ValidMessage> addCoupons(String secId) {
        logger.info("addCoupons >> 待上市债券信息加券参数：secId: [{}]", secId);
        List<ValidMessage> validMessages = new ArrayList<>();
        FinProduct finProduct = finProductMapper.selectByFinprodMarketId(secId);
        if (finProduct != null) {
            validMessages.add(new ValidMessage("该债券信息在系统中已存在，无法加券！"));
            return validMessages;
        }
        //债券信息查询
        FinListedBondInfoQueryRequest request = new FinListedBondInfoQueryRequest();
        request.setSecId(secId);
        FinListedBondInfoQuery001Dto finListedBondInfoQuery001Dto = queryFinWindMarketForCoupons(request);
        if (Objects.isNull(finListedBondInfoQuery001Dto)) {
            validMessages.add(new ValidMessage("查询债券信息为空"));
            return validMessages;
        }
        //该债券信息在系统中已存在，无法加券！

        //接口数据转换
        InterfPayhsrcsecinfoDto interfPayhsrcsecinfoDto = convertInterfPayhsrcsecinfoDto(finListedBondInfoQuery001Dto);
        //公共加券操作
        commonAutoCouponService.autoBondCouponSave(interfPayhsrcsecinfoDto);
        //发行人处理
        return validMessages;
    }

    /**
     * 接口数据转换
     *
     * @param finListedBondInfoQuery001Dto
     * @return
     */
    private InterfPayhsrcsecinfoDto convertInterfPayhsrcsecinfoDto(FinListedBondInfoQuery001Dto finListedBondInfoQuery001Dto) {
        InterfPayhsrcsecinfoDto interfPayhsrcsecinfoDto = new InterfPayhsrcsecinfoDto();
        String secId = finListedBondInfoQuery001Dto.getSecId();
        //金融产品信息List
        List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList = Lists.newArrayList();

        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();

        if (Objects.isNull(finListedBondInfoQuery001Dto)) {
            return null;
        }
        //交易日历
        List<MstCalendarInfo> mstCalendarInfoList = getMstCalendarInfo();
        //new标的类金融产品
        FinProduct finProduct = new FinProduct();
        String finProdId = commonBusinessService.getFinProdId(secId, BussinessDictCodeList.金融产品类型_投管用_债券.getValue(), StringUtil.EMPTY);
        finProduct.setFinprodId(finProdId);
        finProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        finProduct.setFinprodAbbr(finListedBondInfoQuery001Dto.getBondAbbreviation());
        finProduct.setFinprodName(finListedBondInfoQuery001Dto.getBondName());
        finProduct.setProfitType(DictCodeList.收益类型_收益型.getValue());
        finProduct.setCouponSpecies(finListedBondInfoQuery001Dto.getCouponSpecies());
        /**--息票品种modify 2019-04-15 end--**/
        finProduct.setFinprodMarketId(secId);
        //发行金额除以万元
        finProduct.setIssuePrice(finListedBondInfoQuery001Dto.getIssuePx());

        finProduct.setCcy(finListedBondInfoQuery001Dto.getCurrency());
        //境内外
        finProduct.setBlnArea(finListedBondInfoQuery001Dto.getDomesticAndForeign());
        finProduct.setTradeMarket(finListedBondInfoQuery001Dto.getTradingPlaces());
        /**--交易场所modify 2019-04-15 end--**/
        finProduct.setIssuer(getIssuer(finListedBondInfoQuery001Dto.getIssuer()));
        //2019年5月27日  交易日历
        if (StringUtil.equals(DictCodeListExt1.交易场所_银行间.getValue(), finListedBondInfoQuery001Dto.getTradingPlaces())) {
            finProduct.setCalendarId("03");
        } else if (StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), finListedBondInfoQuery001Dto.getTradingPlaces())) {
            finProduct.setCalendarId("04");
        } else if (StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), finListedBondInfoQuery001Dto.getTradingPlaces())) {
            finProduct.setCalendarId("05");
        } else if (StringUtil.equals(DictCodeListExt1.交易场所_机构间私募系统.getValue(), finListedBondInfoQuery001Dto.getTradingPlaces())) {
            //中国法定节假日
            finProduct.setCalendarId("01");
        }
        for (MstCalendarInfo mstCalendarInfo : mstCalendarInfoList) {
            //第三方日历编号等于市场  -对应的日历编号
            if (StringUtil.equals(mstCalendarInfo.getOtherId(), finListedBondInfoQuery001Dto.getTradingPlaces())) {
                finProduct.setCalendarId(mstCalendarInfo.getCalendarId());
                break;
            }
        }
        //发行方式
        finProduct.setIssueType(finListedBondInfoQuery001Dto.getIssueMethod());
        /*--发行方式--*/
        finProduct.setVdate(finListedBondInfoQuery001Dto.getValueDate());
        finProduct.setMdate(finListedBondInfoQuery001Dto.getExpiryDate());
        /*---发行截止日 addby shenchen 2019-05-05----*/
//        finProduct.setIdate(finListedBondInfoQuery001Dto.getIssueDate());
        /*---发行截止日 addby shenchen 2019-05-05----*/
        if (StringUtil.isNotEmpty(finListedBondInfoQuery001Dto.getValueDate()) && StringUtil.isNotEmpty(finListedBondInfoQuery001Dto.getExpiryDate())) {
            finProduct.setTermDays(DateUtil.getBetweenDayNumber(DateUtil.format(finListedBondInfoQuery001Dto.getValueDate()), DateUtil.format(finListedBondInfoQuery001Dto.getExpiryDate())));
        }
        finProduct.setIsChl(DictCodeList.是否_否.getValue());
        finProduct.setIsRight(finListedBondInfoQuery001Dto.getIsBondsWithRights());
        finProduct.setRegistOrg(finListedBondInfoQuery001Dto.getRegisteredCustodian());
        baseFinProductBaseInfoDto.setFinProduct(finProduct);
        //new标的类金融产品附属
        FinProductAdd finProductAdd = new FinProductAdd();
        finProductAdd.setFinprodId(finProdId);
        finProductAdd.setWindId(secId);
        finProductAdd.setIsAutoUpdate(DictCodeList.是否_是.getValue());
        //面值
        finProductAdd.setFaceValue(finListedBondInfoQuery001Dto.getFaceValue());
        // 20200819 债券自动加券优化-判断城投债级别 wangwei end
        baseFinProductBaseInfoDto.setFinProductAdd(finProductAdd);
        FinProductTypeConfig finProductTypeConfig = getProductTypeConfig(
                finListedBondInfoQuery001Dto.getPrimaryClassification(),
                finListedBondInfoQuery001Dto.getSecondaryClassification());

        //金融产品分类
        FinProductType finProductType = new FinProductType();
        finProductType.setFinprodId(finProdId);
        finProductType.setBranch(0L);
        //取债券资料表系统债券类型
        finProductType.setType4(finListedBondInfoQuery001Dto.getPrimaryClassification());
        //取债券资料表系统债券类型2
        finProductType.setType5(finListedBondInfoQuery001Dto.getSecondaryClassification());
        //取债券资料表系统债券类型
        finProductType.setType6(finListedBondInfoQuery001Dto.getPrimaryClassification());
        //取债券资料表系统债券类型2
        finProductType.setType7(finListedBondInfoQuery001Dto.getSecondaryClassification());
        //type8
        finProductType.setType8(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        //取债券资料表系统债券类型
        finProductType.setType9(finProductTypeConfig.getInputValue2());
        //取债券资料表系统债券类型2
        finProductType.setType10(finProductTypeConfig.getInputValue3());
        finProductType.setType11(finProductTypeConfig.getInputValue4());
        finProductType.setType12(finProductTypeConfig.getInputValue5());
        baseFinProductBaseInfoDto.setFinProductType(finProductType);
        baseFinProductBaseInfoList.add(baseFinProductBaseInfoDto);
        interfPayhsrcsecinfoDto.setBaseFinProductBaseInfoList(baseFinProductBaseInfoList);
        return interfPayhsrcsecinfoDto;
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
     * @param issuerShort
     * @return
     * @see
     * @since 1.0
     */
    private String getIssuer(String issuerShort) {
        String customerId = restTemplate.getForObject(ServiceApplication.BASICDATA_SERVICE_NAME
                + "/MstCustomerNature/getIssuer?linkId1=" + issuerShort + "&type=" + "IS_ISSUER", String.class);
        if (StringUtil.isEmpty(customerId)) {
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共通业务类型_市场机构管理);
            customerId = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();

            MstCustomerInfo mstCustomerInfo = new MstCustomerInfo();
            MstCustomerInfoAddWithBLOBs mstCustomerInfoAdd = new MstCustomerInfoAddWithBLOBs();
            mstCustomerInfo.setCustomerId(customerId);
            mstCustomerInfo.setCustomerName(issuerShort);
            mstCustomerInfo.setIsIssuer("Y");
            /* 发行人加券后客户类型默认：市场机构 add by liuwei 20201118 start */
            mstCustomerInfo.setCustomerType("01");
            /* 发行人加券后客户类型默认：市场机构 add by liuwei 20201118 end */

            mstCustomerInfoAdd.setCustomerId(customerId);
            //客户信息表插入数据
            mstCustomerInfoMapper.insert(mstCustomerInfo);
            mstCustomerInfoAddMapper.insert(mstCustomerInfoAdd);
        }
        return customerId;
    }

    /**
     * 根据行内二级分类获取wind分类
     *
     * @param primaryClassification
     * @param secondaryClassification
     * @return
     */
    private FinProductTypeConfig getProductTypeConfig(String primaryClassification, String secondaryClassification) {
        FinProductTypeConfig config = new FinProductTypeConfig();
        config.setOutputValue1(primaryClassification);
        config.setOutputValue2(secondaryClassification);
        config.setConfigType(DictCodeListExt2.配置类型_WIND分类.getValue());
        return finListedBondInfoQueryMapper.getWindType(config);
    }

}
