package com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.ValidUtils;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.MstCustomerInfoAddMapper;
import com.joyintech.tams.common.base.dao.MstCustomerInfoMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.component.business.common.dto.InterfPayhsrcsecinfoDto;
import com.joyintech.tams.component.business.common.service.CommonAutoCouponService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req.SelectOptionCommonRequest;
import com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dao.FinListedBondInfoQueryMapper;
import com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dto.dto.FinListedBondInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dto.req.FinListedBondInfoQueryRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private FinListedBondInfoQueryMapper finListedBondInfoQueryMapper;
    @Resource
    private MstCustomerInfoMapper mstCustomerInfoMapper;
    @Resource
    private MstCustomerInfoAddMapper mstCustomerInfoAddMapper;
    @Resource
    private FinProductMapper finProductMapper;
    @Resource
    private IPlatformDate iPlatformDate;
    @Resource
    private JoyinbusinessCommonService joyinbusinessCommonService;

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
        List<String> primaryClassifications = new ArrayList<>();
        List<String> secondaryClassifications = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(request.getCustomAssetType())) {
            for (int i = 0; i < request.getCustomAssetType().size(); i++) {
                if (request.getCustomAssetType().get(i).size() >= 1) {
                    String type9 = request.getCustomAssetType().get(i).get(0);
                    primaryClassifications.add(type9);
                }
                if (request.getCustomAssetType().get(i).size() >= 2) {
                    String type10 = request.getCustomAssetType().get(i).get(1);
                    secondaryClassifications.add(type10);
                }
            }
            request.setPrimaryClassifications(primaryClassifications);
            request.setSecondaryClassifications(secondaryClassifications);
        }
        if (Objects.nonNull(request.getPageMode())) {
            PageMode pageMode = request.getPageMode();
            String sort = pageMode.getSort();
            if (StringUtil.isNotBlank(sort) && sort.contains("Str")) {
                pageMode.setSort(sort.replaceAll("Str", ""));
                request.setPageMode(pageMode);
            }
        }
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
     * 查询债券资产代码
     * <p>
     * <功能详细描述>
     *
     * @param request
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getAssetId(SelectOptionCommonRequest request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        Page<SelectOptionDto> page = super.queryForPage(request.getPageMode().getPageNo(),
                request.getPageMode().getPageSize(),
                () -> finListedBondInfoQueryMapper.getAssetId(param));
        return page;
    }


    /**
     * 加券
     *
     * @param secId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addCoupons(String secId) {
        AjaxResult ajaxResult = new AjaxResult();
        logger.info("addCoupons >> 待上市债券信息加券参数：secId: [{}]", secId);
        List<ValidMessage> validMessages = new ArrayList<>();
        FinProduct finProduct = finProductMapper.selectByFinprodMarketIdAndType2(secId, BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        //该债券信息在系统中已存在，无法加券！
        if (finProduct != null) {
            validMessages.add(new ValidMessage("该债券信息在系统中已存在，无法加券！"));
            ajaxResult.setErrorList(validMessages);
            ajaxResult.setMessageType(MessageType.错误消息);
            return ajaxResult;
        }
        //债券信息查询
        FinListedBondInfoQueryRequest request = new FinListedBondInfoQueryRequest();
        request.setSecId(secId);
        FinListedBondInfoQuery001Dto finListedBondInfoQuery001Dto = queryFinWindMarketForCoupons(request);
        if (Objects.isNull(finListedBondInfoQuery001Dto)) {
            validMessages.add(new ValidMessage("未查询到该待上市债券信息，无法加券，请手工维护"));
            ajaxResult.setErrorList(validMessages);
            ajaxResult.setMessageType(MessageType.错误消息);
            return ajaxResult;
        }
        finListedBondInfoQuery001Dto.setIsAutoUpdate(DictCodeList.是否_否.getValue());
        //非空校验
        emptyCheck(finListedBondInfoQuery001Dto, validMessages);
        if (CollectionUtils.isNotEmpty(validMessages)) {
            ajaxResult.setErrorList(validMessages);
            ajaxResult.setMessageType(MessageType.警告消息);
            return ajaxResult;
        }
        //接口数据转换
        InterfPayhsrcsecinfoDto interfPayhsrcsecinfoDto = convertInterfPayhsrcsecinfoDto(finListedBondInfoQuery001Dto);
        //公共加券操作
        commonAutoCouponService.autoBondCouponSave(interfPayhsrcsecinfoDto);
        //保存操作记录
        this.saveOperate(secId, "自动加券", DictCodeListExt3.操作类型_新建, null);
        //发行人处理
        return ajaxResult;
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
        String finProdId = commonBusinessService.getFinProdId(secId, BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue(), StringUtil.EMPTY);
        finProduct.setFinprodId(finProdId);
        finProduct.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        finProduct.setFinprodAbbr(finListedBondInfoQuery001Dto.getBondAbbreviation());
        finProduct.setFinprodName(finListedBondInfoQuery001Dto.getBondName());
        finProduct.setProfitType(DictCodeList.收益类型_收益型.getValue());
        finProduct.setCouponSpecies(finListedBondInfoQuery001Dto.getCouponSpecies());
        /**--息票品种modify 2019-04-15 end--**/
        finProduct.setFinprodMarketId(secId);
        //发行金额除以万元
        finProduct.setIssuePrice(Objects.nonNull(finListedBondInfoQuery001Dto.getIssuePx()) ? new BigDecimal(finListedBondInfoQuery001Dto.getIssuePx()) : null);

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
        finProduct.setIssueType(finListedBondInfoQuery001Dto.getRaiseMode());
        /*--发行方式--*/
        finProduct.setVdate(finListedBondInfoQuery001Dto.getValueDate());
        finProduct.setMdate(finListedBondInfoQuery001Dto.getExpiryDate());
        /*---发行截止日 addby shenchen 2019-05-05----*/
        finProduct.setIdate(finListedBondInfoQuery001Dto.getIssueDate());
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
        finProductAdd.setIsAutoUpdate(DictCodeList.是否_否.getValue());
        finProductAdd.setCrossMarket(DictCodeList.是否_否.getValue());
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
        finProductType.setBranch(Objects.nonNull(finListedBondInfoQuery001Dto.getAdditionalBatches()) ? finListedBondInfoQuery001Dto.getAdditionalBatches().longValue() : 0L);
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
        if (Objects.nonNull(finProductTypeConfig)) {
            finProductType.setType9(finProductTypeConfig.getInputValue2());
            //取债券资料表系统债券类型2
            finProductType.setType10(finProductTypeConfig.getInputValue3());
            finProductType.setType11(finProductTypeConfig.getInputValue4());
            finProductType.setType12(finProductTypeConfig.getInputValue5());
        }
        baseFinProductBaseInfoDto.setFinProductType(finProductType);
        //发行信息
        FinProductIssue finProductIssue = new FinProductIssue();
        finProductIssue.setFinprodId(finProdId);
        finProductIssue.setIssueClass(finListedBondInfoQuery001Dto.getIssueMethod());
        finProductIssue.setTenderType(finListedBondInfoQuery001Dto.getBiddingMethod());
        finProductIssue.setTenderTarget(finListedBondInfoQuery001Dto.getTenderTarget());
        //计划发行量
        finProductIssue.setPlanIssueAmt(Objects.nonNull(finListedBondInfoQuery001Dto.getIssueSize()) ? new BigDecimal(finListedBondInfoQuery001Dto.getIssueSize()) : null);
        finProductIssue.setIssueStyle(finListedBondInfoQuery001Dto.getReleaseType());
        finProductIssue.setAddBatch(0L);
        finProductIssue.setIssuePrice(Objects.nonNull(finListedBondInfoQuery001Dto.getIssuePx()) ? new BigDecimal(finListedBondInfoQuery001Dto.getIssuePx()) : null);
        finProductIssue.setReferYield(Objects.nonNull(finListedBondInfoQuery001Dto.getCouponRate()) ? new BigDecimal(finListedBondInfoQuery001Dto.getCouponRate()) : null);
        finProductIssue.setRateGrade(finListedBondInfoQuery001Dto.getDecimalPlaces());
        finProductIssue.setBp(Objects.nonNull(finListedBondInfoQuery001Dto.getSpread()) ? new BigDecimal(finListedBondInfoQuery001Dto.getSpread()) : null);
        finProductIssue.setIssueDate(finListedBondInfoQuery001Dto.getIssueDate());
        finProductIssue.setPayDate(finListedBondInfoQuery001Dto.getPayDate());
        finProductIssue.setFirstConfirmDate(finListedBondInfoQuery001Dto.getInterestAccrualDate());
        finProductIssue.setIssueSdate(finListedBondInfoQuery001Dto.getIssueStartTime());
        finProductIssue.setIssueEdate(finListedBondInfoQuery001Dto.getIssueEndTime());
        try {
            finProductIssue.setTenderSdate(Objects.nonNull(finListedBondInfoQuery001Dto.getBidBeginDate()) ? DateUtil.parseDate(finListedBondInfoQuery001Dto.getBidBeginDate(), DateUtil.DEFAULT_DATETIME_FORMAT) : null);
            finProductIssue.setTenderEdate(Objects.nonNull(finListedBondInfoQuery001Dto.getBidEndDate()) ? DateUtil.parseDate(finListedBondInfoQuery001Dto.getBidEndDate(), DateUtil.DEFAULT_DATETIME_FORMAT) : null);
        } catch (ParseException e) {
            logger.error("时间转换异常：", e);
        }
        finProductIssue.setPayEdate(finListedBondInfoQuery001Dto.getEventDate());
        finProductIssue.setAssignRegdate(finListedBondInfoQuery001Dto.getDebtRegistrationDate());
        finProductIssue.setMinPamt(finListedBondInfoQuery001Dto.getSingleMinSubAmt());
        finProductIssue.setMaxPamt(finListedBondInfoQuery001Dto.getSingleMaxSubAmt());
        finProductIssue.setMaxTotalamt(finListedBondInfoQuery001Dto.getMaxSubAmt());
        finProductIssue.setMinPerTamt(finListedBondInfoQuery001Dto.getMinPerTenderAmt());
        finProductIssue.setMaxPerUtamt(finListedBondInfoQuery001Dto.getMaxPerTenderAmt());
        finProductIssue.setMinTamt(finListedBondInfoQuery001Dto.getMinTenderPxForPlan());
        finProductIssue.setMaxTamt(finListedBondInfoQuery001Dto.getMaxTenderPxForPlan());
        finProductIssue.setTenderUnit(finListedBondInfoQuery001Dto.getTenderStep());
        finProductIssue.setMaxTenderUnit(Objects.nonNull(finListedBondInfoQuery001Dto.getMedianMaxBidMarked()) ? new BigDecimal(finListedBondInfoQuery001Dto.getMedianMaxBidMarked()) : null);
        finProductIssue.setMaxTenderPlus(Objects.nonNull(finListedBondInfoQuery001Dto.getMaxTenderPxDiffer()) ? new BigDecimal(finListedBondInfoQuery001Dto.getMaxTenderPxDiffer()) : null);
        finProductIssue.setSeriYN(finListedBondInfoQuery001Dto.getContinuousTenderLimit());
        finProductIssue.setMaxTotalTender(finListedBondInfoQuery001Dto.getMaxTotalTenderAmt());
        finProductIssue.setPayCycle(finListedBondInfoQuery001Dto.getInterestPaymentFrequency());
        finProductIssue.setIntType(finListedBondInfoQuery001Dto.getRateType());
        baseFinProductBaseInfoDto.setFinProductIssue(finProductIssue);

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


    /**
     * 保存操作记录
     *
     * @param tradeId
     * @param remark
     * @param operType
     */
    private void saveOperate(String tradeId, String remark, DictCodeListExt3 operType, String workFlowId) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(tradeId);
        oprHisReq.setOperType(operType.getValue());
        oprHisReq.setBusiDes(remark);
        oprHisReq.setWorkflowId(workFlowId);
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

    /**
     * 空检查
     *
     * @param dto
     * @param validList
     */
    private void emptyCheck(FinListedBondInfoQuery001Dto dto, List<ValidMessage> validList) {
        ValidUtils.valid(dto, validList);
        if (Objects.nonNull(dto.getValueDate()) && Objects.nonNull(dto.getExpiryDate()) && dto.getValueDate().compareTo(dto.getExpiryDate()) >= 0) {
            validList.add(new ValidMessage("起息日不能大于到期日"));
        }
        if (StringUtil.isNotEmpty(dto.getIssueMethod()) && (StringUtil.equals(dto.getIssueMethod(), DictCodeListExt4.新债_发行方式_招标发行.getValue()) ||
                StringUtil.equals(dto.getIssueMethod(), DictCodeListExt4.新债_发行方式_簿记建档.getValue()))) {
            if (StringUtil.isEmpty(dto.getTenderTarget())) {
                validList.add(new ValidMessage("招标标的不能为空"));
            }
        }
        if (StringUtil.isNotEmpty(dto.getReleaseType()) && StringUtil.equals(dto.getReleaseType(), DictCodeListExt4.发行类型_增发.getValue())) {
            if (Objects.isNull(dto.getAdditionalBatches())) {
                validList.add(new ValidMessage("增发批次不能为空"));
            }
        }
        if (StringUtil.isNotEmpty(dto.getIssueMethod()) && (StringUtil.equals(dto.getIssueMethod(), DictCodeListExt4.新债_发行方式_报价发行.getValue()) || (
                StringUtil.equals(dto.getIssueMethod(), DictCodeListExt4.新债_发行方式_报价发行.getValue()) && StringUtil.isNotEmpty(dto.getBiddingMethod()) &&
                        (StringUtil.equals(dto.getBiddingMethod(), DictCodeListExt4.招标方式_单一价格招标) || StringUtil.equals(dto.getBiddingMethod(), DictCodeListExt4.招标方式_数量招标))
        ))) {
            if (Objects.isNull(dto.getIssueDate())) {
                validList.add(new ValidMessage("发行日不能为空"));
            } else if (DateUtil.compareDate(dto.getIssueDate(), iPlatformDate.getSysDate()) < 0) {
                validList.add(new ValidMessage("发行日需大于等于平台日"));
            }
            if (Objects.isNull(dto.getPayDate())) {
                validList.add(new ValidMessage("缴款日不能为空"));
            }
            if (Objects.isNull(dto.getIssuePx())) {
                validList.add(new ValidMessage("发行价格不能为空"));
            }
        }
        if (StringUtil.isNotEmpty(dto.getIssueMethod()) && StringUtil.equals(dto.getIssueMethod(), DictCodeListExt4.新债_发行方式_报价发行.getValue()) &&
                StringUtil.equals(dto.getIssueMethod(), DictCodeListExt4.招标方式_数量招标)) {
            if (Objects.isNull(dto.getIssuePx())) {
                validList.add(new ValidMessage("发行价格不能为空"));
            }
        }
        if (StringUtil.isNotEmpty(dto.getIssueMethod()) && StringUtil.equals(dto.getIssueMethod(), DictCodeListExt4.新债_发行方式_报价发行.getValue()) &&
                (StringUtil.isNotEmpty(dto.getCouponSpecies()) && (StringUtil.equals(dto.getCouponSpecies(), DictCodeList.息票品种_贴现.getValue()) || StringUtil.equals(dto.getCouponSpecies(), DictCodeList.息票品种_零息.getValue())))) {
            if (Objects.isNull(dto.getStipulationValue())) {
                validList.add(new ValidMessage("参考收益率不能为空"));
            }
        }
        if (StringUtil.isNotEmpty(dto.getReleaseType()) && StringUtil.equals(dto.getReleaseType(), DictCodeListExt4.发行类型_增发.getValue())) {
//            if (StringUtil.isNotEmpty(dto.getProductAddDto().getCrossMarket()) && StringUtil.equals(dto.getProductAddDto().getCrossMarket(), DictCodeList.是否_是.getValue())) {
//                if (StringUtil.isEmpty(dto.getProductAddDto().getCrossMarketCode())) {
//                    validList.add(new ValidMessage("跨市场代码不能为空"));
//                }
//            }
            if (StringUtil.isEmpty(dto.getListingCode())) {
                validList.add(new ValidMessage("上市代码不能为空"));
            }
            if (StringUtil.isNotEmpty(dto.getAdditionalTenderBeginDate()) && DateUtil.compareDate(DateUtil.parse(dto.getAdditionalTenderBeginDate()), iPlatformDate.getSysDate()) < 0) {
                validList.add(new ValidMessage("增发投标日截止日需大于等于平台日"));
            }
        } else {
            if (StringUtil.isNotEmpty(dto.getBidEndDate()) && DateUtil.compareDate(DateUtil.parse(dto.getBidEndDate()), iPlatformDate.getSysDate()) < 0) {
                validList.add(new ValidMessage("投标日截止日需大于等于平台日"));
            }
        }
    }

}
