package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.common.dto.InterfPayhsrcsecinfoDto;
import com.joyintech.tams.component.business.common.service.CommonAutoCouponService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dao.FinBondMarketInfoQueryMapper;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketCashPlanDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoDetailQueryDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondPayHSrcSecOptionDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req.FinBondMarketInfoQueryRequest;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req.SelectOptionCommonRequest;
import com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dto.dto.FinListedBondInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dto.req.FinListedBondInfoQueryRequest;
import com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.service.FinListedBondInfoQuery001Service;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 债券市场信息查询服务类
 *
 * @author zhuys
 * @version 1.0 2022年4月11日
 * @see FinBondMarketInfoQuery001Service
 * @since 1.0
 */
@Service
public class FinBondMarketInfoQuery001Service extends BaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private CommonAutoCouponService commonAutoCouponService;
    @Resource
    private FinListedBondInfoQuery001Service finListedBondInfoQuery001Service;
    @Resource
    private FinBondMarketInfoQueryMapper finBondMarketInfoQueryMapper;
    @Resource
    private FinProductMapper finProductMapper;
    @Resource
    private IPlatformDate iPlatformDate;
    @Resource
    private JoyinbusinessCommonService joyinbusinessCommonService;

    /**
     * 债券市场信息分页查询
     *
     * @param request
     * @return
     */
    public Page<FinBondMarketInfoQuery001Dto> queryForPage(FinBondMarketInfoQueryRequest request) {
        logger.info("queryForPage >> 债券市场信息分页查询参数：{}", JSON.toJSONString(request));
        request.setPlatformDate(iPlatformDate.getSysDateStr());
        PageHelper.startPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize());
        List<String> secTypes = new ArrayList<>();
        List<String> secType2s = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(request.getCustomAssetType())) {
            for (int i = 0; i < request.getCustomAssetType().size(); i++) {
                if (request.getCustomAssetType().get(i).size() >= 1) {
                    String type9 = request.getCustomAssetType().get(i).get(0);
                    secTypes.add(type9);
                }
                if (request.getCustomAssetType().get(i).size() >= 2) {
                    String type10 = request.getCustomAssetType().get(i).get(1);
                    secType2s.add(type10);
                }
            }
            request.setSecTypes(secTypes);
            request.setSecType2s(secType2s);
        }
        List<FinBondMarketInfoQuery001Dto> bondMarketInfoQuery004Dtos = finBondMarketInfoQueryMapper.queryForPage(request);
        return new Page<>(PageInfo.of(bondMarketInfoQuery004Dtos));
    }

    /**
     * 债券市场详情信息查询
     *
     * @param secId
     * @return
     */
    public FinListedBondInfoQuery001Dto detailInfo(String secId) {
        logger.info("detailInfo >> 债券市场详情信息查询参数：secId：【{}】", secId);
        FinListedBondInfoQueryRequest request = new FinListedBondInfoQueryRequest();
        request.setSecId(secId);
        request.setPlatformDate(iPlatformDate.getSysDateStr());
        FinListedBondInfoQuery001Dto finListedBondInfoQuery001Dto = finListedBondInfoQuery001Service.queryFinProductForCoupons(request);
        if (Objects.isNull(finListedBondInfoQuery001Dto)) {
            finListedBondInfoQuery001Dto = finListedBondInfoQuery001Service.queryFinWindMarketForCoupons(request);
        }
        if (Objects.nonNull(finListedBondInfoQuery001Dto)) {
            //付息计划信息
            Map<String, Object> param = new HashMap<>(1);
            param.put("secId", secId);
            List<FinBondMarketCashPlanDto> finBondMarketCashPlanDtos = finBondMarketInfoQueryMapper.queryFinBondMarketCashPlan(param);
            finListedBondInfoQuery001Dto.setBondMarketCashPlanDtos(finBondMarketCashPlanDtos);
            //行权计划信息
            List<FinBondPayHSrcSecOptionDto> finBondPayHSrcSecOptionDtos = finBondMarketInfoQueryMapper.queryFinBondPayHSrcSecOption(secId);
            finListedBondInfoQuery001Dto.setBondPayHSrcSecOptionDtos(finBondPayHSrcSecOptionDtos);
        }
        return finListedBondInfoQuery001Dto;
    }

    /**
     * 加券
     *
     * @param secId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addCoupons(String secId) {
        logger.info("addCoupons >> 债券市场加券参数：secId：【{}】", secId);
        AjaxResult ajaxResult = new AjaxResult();
        FinProduct finProduct = finProductMapper.selectByFinprodMarketId(secId);
        List<ValidMessage> validMessages = new ArrayList<>();
        if (Objects.isNull(finProduct)) {
            validMessages.add(new ValidMessage("该债券信息在系统中已存在，无法加券！"));
            ajaxResult.setErrorList(validMessages);
            ajaxResult.setMessageType(MessageType.错误消息);
            return ajaxResult;
        }
        //校验
        InterfPayhsrcsecinfoDto interfPayhsrcsecinfoDto = commonAutoCouponService.selectWindBondAssetBySecId(secId);
        if (Objects.isNull(interfPayhsrcsecinfoDto)) {
            validMessages.add(new ValidMessage("未查询到该待上市债券信息，无法加券，请手工维护"));
            ajaxResult.setErrorList(validMessages);
            ajaxResult.setMessageType(MessageType.错误消息);
            return ajaxResult;
        }
        //校验必填项
        addCouponsCheck(validMessages, interfPayhsrcsecinfoDto);
        if (CollectionUtils.isNotEmpty(validMessages)) {
            ajaxResult.setErrorList(validMessages);
            ajaxResult.setMessageType(MessageType.警告消息);
            return ajaxResult;
        }
        /****************自动加券****start****************/
        //自动加券错误信息
        List<String> errorInfoList = new ArrayList<>();
        //调用债券自动加券方法
        InterfPayhsrcsecinfoDto params = new InterfPayhsrcsecinfoDto();
        params.setSecidList(Lists.newArrayList(secId));
        //加券
        InterfPayhsrcsecinfoDto result = commonAutoCouponService.autoBondCoupon(params);
        if (null != result && null != result.getErrorBondList()) {
            errorInfoList.addAll(result.getErrorBondList());
        }
        /****************自动加券****end****************/
        if (CollectionUtils.isNotEmpty(errorInfoList)) {
            validMessages = errorInfoList.stream().map(errorInfo -> new ValidMessage(errorInfo)).collect(Collectors.toList());
            ajaxResult.setErrorList(validMessages);
            ajaxResult.setMessageType(MessageType.错误消息);
        } else {
            //操作记录
            this.saveOperate(secId, "自动加券", DictCodeListExt3.操作类型_新建, null);
        }
        return ajaxResult;
    }

    /**
     * 加券市场信息查询
     *
     * @param secId
     * @return
     */
    public FinBondMarketInfoDetailQueryDto queryFinBondMarketForCoupons(String secId) {
        logger.info("queryFinBondMarketForCoupons >> 加券市场信息查询参数：{}", secId);
        Map<String, Object> param = new HashMap<>(2);
        param.put("secId", secId);
        param.put("platformDate", iPlatformDate.getSysDateStr());
        FinBondMarketInfoDetailQueryDto detailQueryDto = finBondMarketInfoQueryMapper.queryFinBondMarketForCoupons(param);
        if (Objects.nonNull(detailQueryDto)) {
            //行权计划信息
            List<FinBondPayHSrcSecOptionDto> finBondPayHSrcSecOptionDtos = finBondMarketInfoQueryMapper.queryFinBondPayHSrcSecOption(secId);
            detailQueryDto.setBondPayHSrcSecOptionDtos(finBondPayHSrcSecOptionDtos);
            //付息计划信息
            List<String> scheduTypeList = Lists.newArrayList();
            scheduTypeList.add(DictCodeList.现金流类型_到期收息.getValue());
            scheduTypeList.add(DictCodeList.现金流类型_中间收息.getValue());
            param.put("scheduTypeList", scheduTypeList);
            List<FinBondMarketCashPlanDto> finBondMarketCashPlanDtos = finBondMarketInfoQueryMapper.queryFinBondMarketCashPlan(param);
            detailQueryDto.setBondMarketCashPlanDtos(finBondMarketCashPlanDtos);
            //还本计划信息
            scheduTypeList = Lists.newArrayList();
            scheduTypeList.add(DictCodeList.现金流类型_到期还本.getValue());
            scheduTypeList.add(DictCodeList.现金流类型_中间还本.getValue());
            param.put("scheduTypeList", scheduTypeList);
            List<FinBondMarketCashPlanDto> bondMarketInvRepayDtos = finBondMarketInfoQueryMapper.queryFinBondMarketCashPlan(param);
            detailQueryDto.setBondMarketInvRepayDtos(bondMarketInvRepayDtos);
        } else {
            param.put("FINPROD_TYPE2_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
            param.put("FIN_CASH_BUSINESS_TYPE_01", DictCodeListExt1.现金流业务类型_金融产品.getValue());
            param.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
            param.put("CASH_TYPE_1_01", DictCodeListExt1.现金流一级类型_本金.getValue());
            param.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
            param.put("CASH_TYPE_2_C01", DictCodeListExt1.现金流二级类型_本金.getValue());
            param.put("FINPROD_TYPE_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
            param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
            param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
            param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
            param.put("finprodType", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            detailQueryDto = finBondMarketInfoQueryMapper.queryFinProductForCoupons(param);
            if (Objects.nonNull(detailQueryDto)) {
                Map<String, Object> map = new HashMap<>();
                //付息计划
                map.put("cashId", detailQueryDto.getBasisStyle());
                map.put("FINPROD_TYPE_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                if (StringUtil.isNotEmpty(detailQueryDto.getBasisStyle())) {
                    List<FinBondMarketCashPlanDto> finBondMarketCashPlanDtos = finBondMarketInfoQueryMapper.selectInvScheduleList(map);
                    detailQueryDto.setBondMarketCashPlanDtos(finBondMarketCashPlanDtos);
                }
                //还本信息
                map.put("cashId", detailQueryDto.getPrincipalCashId());
                map.put("FINPROD_TYPE_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                if (StringUtil.isNotEmpty(detailQueryDto.getPrincipalCashId())) {
                    List<FinBondMarketCashPlanDto> invRepayList = finBondMarketInfoQueryMapper.selectInvScheduleList(map);
                    detailQueryDto.setBondMarketInvRepayDtos(invRepayList);
                }

                //行权计划
                map.put("cashId", detailQueryDto.getPrincipalCashId());
                map.put("FINPROD_TYPE_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                if (StringUtil.isNotEmpty(detailQueryDto.getPrincipalCashId())) {
                    List<FinBondPayHSrcSecOptionDto> bondPayHSrcSecOptionDtos = finBondMarketInfoQueryMapper.selectFinExercisePlanList(map);
                    detailQueryDto.setBondPayHSrcSecOptionDtos(bondPayHSrcSecOptionDtos);
                }
            }
        }
        return detailQueryDto;
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
        Page<SelectOptionDto> page = super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(), () -> finBondMarketInfoQueryMapper.getAssetId(param));
        return page;
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
     * 加券参数校验
     *
     * @param validMessages
     * @param interfPayhsrcsecinfoDto
     */
    private void addCouponsCheck(List<ValidMessage> validMessages, InterfPayhsrcsecinfoDto interfPayhsrcsecinfoDto) {
        if (StringUtil.isBlank(interfPayhsrcsecinfoDto.getSecid())) {
            getValidMessages("资产代码", validMessages);
        }
        if (StringUtil.isBlank(interfPayhsrcsecinfoDto.getSecfullname())) {
            getValidMessages("资产名称", validMessages);
        }
        if (StringUtil.isBlank(interfPayhsrcsecinfoDto.getSecname())) {
            getValidMessages("资产名称", validMessages);
        }
        if (StringUtil.isBlank(interfPayhsrcsecinfoDto.getMarket())) {
            getValidMessages("交易场所", validMessages);
        }
        if (StringUtil.isBlank(interfPayhsrcsecinfoDto.getInstitution())) {
            getValidMessages("登记托管机构", validMessages);
        }
        if (Objects.isNull(interfPayhsrcsecinfoDto.getIssueprice())) {
            getValidMessages("发行价格", validMessages);
        }
        if (Objects.isNull(interfPayhsrcsecinfoDto.getPubenddate())) {
            getValidMessages("发行日", validMessages);
        }
        if (Objects.isNull(interfPayhsrcsecinfoDto.getVdate())) {
            getValidMessages("起息日", validMessages);
        }
        if (Objects.isNull(interfPayhsrcsecinfoDto.getMdate())) {
            getValidMessages("到期日", validMessages);
        }
        if (Objects.isNull(interfPayhsrcsecinfoDto.getIssueamt())) {
            getValidMessages("发行金额", validMessages);
        }
        if (StringUtil.isBlank(interfPayhsrcsecinfoDto.getIssuershort())) {
            getValidMessages("发行人", validMessages);
        }
        if (StringUtil.isBlank(interfPayhsrcsecinfoDto.getIssuetype())) {
            getValidMessages("发行方式", validMessages);
        }
        if (StringUtil.isBlank(interfPayhsrcsecinfoDto.getCcy())) {
            getValidMessages("币种", validMessages);
        }
        if (StringUtil.isBlank(interfPayhsrcsecinfoDto.getCouponspecies())) {
            getValidMessages("息票品种", validMessages);
        }
    }

    /**
     * 封装校验
     *
     * @param field
     * @param validMessages
     */
    private void getValidMessages(String field, List<ValidMessage> validMessages) {
        validMessages.add(new ValidMessage(String.format("%s%s", field, "不能为空")));
    }
}
