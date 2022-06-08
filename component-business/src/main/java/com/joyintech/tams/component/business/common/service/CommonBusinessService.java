/*
 * 文件名：CommonBusinessService.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：杨智 修改时间：2018年10月16日 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.component.business.common.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.dao.PaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.dao.PtlTradeMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.dao.CommonBusinessMapper;
import com.joyintech.tams.component.business.common.dto.FinCashResetRuleDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.risktrial.RiskTrialComm;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.workflow.engine.exception.WflCode;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 各种共通组件
 *
 * @author 何阳阳
 * @version 1.0 2018年10月23日
 * @see CommonBusinessService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
public class CommonBusinessService extends BaseService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CommonBusinessMapper commonBusinessMapper;

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;

    /**
     * 金融产品分类
     */
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private DateCalService dateCalService;
    /**
     * 监督指标入口接口
     */
    @Autowired
    private RiskTrialComm riskTrialComm;


    /**
     * 交易产品接口
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Autowired
    private  BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

    /**
     *
     * @param trdProductDeal 需要传 tradeId cancelRemark workflowId不传则不删wfl表
     * @param totleFlag  是否处理totle表
     */
    public  void revokeReverseTrade(TrdProductDeal trdProductDeal,boolean totleFlag) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        String reverseTradeId = trdProductDeal.getTradeId();

        if (reverseTradeId.lastIndexOf('R') == reverseTradeId.length() - 1) {
            reverseTradeId = reverseTradeId.substring(0, reverseTradeId.length() - 1);
        }

        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = new ArrayList<>();
        if (totleFlag) {
            baseTradeProductDealDtoList  = restTemplate.getForList(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTotelTradeInfo?tradeId=" + reverseTradeId,
                    BaseTradeProductDealDto.class);
        } else {

            baseTradeProductDealDtoList= restTemplate.getForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
                        + reverseTradeId,
                BaseTradeProductDealDto.class);
        }
        //如果为null
        if(CollectionUtils.isEmpty(baseTradeProductDealDtoList)){
            return;
        }
        baseTradeProductDealDtoList.forEach(baseTradeProductDealDto -> {
            if (baseTradeProductDealDto.getSecLendFinprodId() == null){
                baseTradeProductDealDto.setSecLendFinprodId(trdProductDeal.getFinprodId());
            }
            baseTradeProductDealDto.setSecAmount(baseTradeProductDealDto.getSecAmount()==null?new BigDecimal("0"):baseTradeProductDealDto.getSecAmount());
        });

        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtoList);
        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());

        //如果为true 则doRevokeComplete必须要调用
        // false时  do撤销会调用 新增r数据
        if(totleFlag) {
            finProductTradeDto.setNeedSum(true);
        }
        baseTradeProductDealCommService.execute(finProductTradeDto);

        if(totleFlag) {
            Map<String, Object> reverseparams = new HashMap<>();
            reverseparams.put("workflowId", trdProductDeal.getWorkflowId());

            reverseparams.put("bizCode", reverseTradeId);
            reverseparams.put("updateUser", SecurityUtil.getLoginUser().getUserName());
            reverseparams.put("revokeReason", trdProductDeal.getCancelRemark());
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/doRevokeReverseComplete?", reverseparams,
                    String.class);
        }


        // 额外处理几张表  ptl_trade pa_trd_product_deal  rep_trade 授信等

        if (bussCommonMapperDelegateService != null) {
            for (BaseTradeProductDealDto dto : baseTradeProductDealDtoList) {
                String tradeId = dto.getTrdProductDeal()==null?"":dto.getTrdProductDeal().getTradeId();
                if(StringUtil.isNotEmpty(tradeId)){

                    if(!totleFlag) {
                        PaTrdProductDeal paTrdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                                PaTrdProductDealMapper.class, tradeId, PaTrdProductDeal.class);
                        paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId() + "R");
                        paTrdProductDealMapper.insert(paTrdProductDeal);

                        PtlTrade ptlTrade = new PtlTrade();
                        ptlTrade.setBusiNo(tradeId);
                        ptlTrade.setPortfolioId(dto.getActPortfolioId());
                        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                        ptlTrade = bussCommonMapperDelegateService.selectByMultiPrimaryKey(PtlTradeMapper.class, ptlTrade, PtlTrade.class);
                        ptlTrade.setBusiNo(ptlTrade.getBusiNo() + "R");
                        ptlTradeMapper.insert(ptlTrade);
                    }
                    riskCreditListCommonValidatorService.deleteCredit(tradeId);

                    // 调用基础组件.【交易报送组件】删除交易报送信息
                    restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepTrade",
                            Arrays.asList(tradeId), Boolean.class);
                }

            }
        }

        // 继续调用 组合 和 act组合不一致的情况
        String portfolioId = "";
        String parentPortfolioId = "";
        for (BaseTradeProductDealDto dto : baseTradeProductDealDtoList) {
            TrdProductDealAdd trdProductDealAdd = dto.getTrdProductDealAdd();
            if (trdProductDealAdd!=null && StringUtil.isNotEmpty(trdProductDealAdd.getPortfolioId())){

                portfolioId = trdProductDealAdd.getPortfolioId();
                parentPortfolioId = AllTradeCommonUtil.exchangePortfolio(portfolioId);
                if (!StringUtil.equals(portfolioId,parentPortfolioId)){
                    String tradeId = dto.getTrdProductDeal()==null?"":dto.getTrdProductDeal().getTradeId();
                    if(StringUtil.isNotEmpty(tradeId)) {
                        PtlTrade ptlTrade = new PtlTrade();
                        ptlTrade.setBusiNo(tradeId);
                        ptlTrade.setPortfolioId(parentPortfolioId);
                        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                        ptlTrade = bussCommonMapperDelegateService.selectByMultiPrimaryKey(PtlTradeMapper.class, ptlTrade, PtlTrade.class);
                        ptlTrade.setBusiNo(ptlTrade.getBusiNo() + "R");
                        ptlTradeMapper.insert(ptlTrade);
                    }

                }
            }

        }

        if(StringUtil.isNotEmpty(portfolioId) && !StringUtil.equals(portfolioId,parentPortfolioId)){
            String finalParentPortfolioId = parentPortfolioId;

            finProductTradeDto.getBaseTradeProductDealDtos()
                    .forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
                            .setPortfolioId(finalParentPortfolioId));
            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
        }

    }
    /**
     * 资产是否存在交易
     *
     * @param finprodId 金融产品代码
     * @return 交易笔数
     * @see
     * @since 1.0
     */
    public int isExistTransInAsset(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/isExistTransInAsset?finprodId=" + finprodId,
                Integer.class);
    }

    /**
     * 现金流代码生成
     *
     * @param busiNo 业务代码
     * @param busiType 业务类型
     * @param branch 分支序号
     * @param busiType2 现金流二级类型
     * @return 现金流代码
     */
    public String generateCashFlowCode(String busiNo, String busiType, long branch, String busiType2) {
        return CommonUtil.generateCashFlowCode(busiNo, busiType, branch, busiType2);
    }

    /**
     * 获取金融产品代码 <功能详细描述>
     *
     * @param finprodMarketId 市场代码
     * @param finprodType2 金融产品类型（投管分类）
     * @param chlFinprodId 通道代码
     * @return 金融产品代码
     * @see
     * @since 1.0
     */
    public String getFinProdId(String finprodMarketId, String finprodType2, String chlFinprodId) {
        if (StringUtil.isEmpty(chlFinprodId)) {
            return StringUtil.defaultString(finprodType2).concat(GlobalConstant.UNDER_LINE).concat(
                StringUtil.defaultString(finprodMarketId));
        }
        return StringUtil.defaultString(finprodType2).concat(GlobalConstant.UNDER_LINE).concat(
            StringUtil.defaultString(finprodMarketId)).concat(GlobalConstant.UNDER_LINE).concat(
                StringUtil.defaultString(chlFinprodId));
    }

    /**
     * 利用币种与交易日查询汇率行情表中最新的收盘价 非设计书写明组件 开发过程中添加组件（etliu添加）
     *
     * @param ccy 币种
     * @param tradeDate 交易日
     * @return 汇率行情表中最新的收盘价
     * @see
     * @since 1.0
     */
    public BigDecimal selectClosePrice(String ccy, String tradeDate) {
        if (StringUtil.isBlank(ccy) || StringUtil.isBlank(tradeDate)) {
            return null;
        }

        return restTemplate.postForObject(
            ServiceApplication.COMMON_SERVICE_NAME + "/core/selectClosePrice/" + ccy + "/" + tradeDate, null, BigDecimal.class);
    }

    /**
     * 资产是否存在交易
     *
     * @param finprodId 金融产品代码
     * @return 交易笔数
     * @see
     * @since 1.0
     */
    public int isTransInAsset(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/isTransInAsset?finprodId=" + finprodId,
            Integer.class);
    }



    /**
     * 获取最大交易日期 <功能详细描述>
     *
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public Date getMaxTradeDate(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/getMaxTradeDate?finprodId=" + finprodId,
            Date.class);
    }

    /**
     * 获取用户的授权金额，如果数据为null，则视为无权
     *
     * @param userName 用户名
     * @param assetType 资产类型
     * @param assetSecType 资产一级分类
     * @param tradeMarket 交易场所
     * @param tradeDirection 交易方向
     * @return 授权金额
     */
    public Map<String, Object> getAuthorizeLimitAmt(String userName, String assetType, String assetSecType, String tradeMarket,
                                                    String tradeDirection, String flag) {
        if (StringUtil.isEmpty(userName) || StringUtil.isEmpty(assetType)) {
            return null;
        }
        Map<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("assetType", assetType);
        params.put("assetSecType", assetSecType);
        params.put("tradeMarket", StringUtil.isEmpty(tradeMarket) ? "999999" : tradeMarket);
        params.put("tradeDirection", tradeDirection);
        // {参数字典.授权类型.授权、参数字典.授权类型.无限授权}
        params.put("AUTHORIZE_TYPE_02", DictCodeList.授权类型_授权.getValue());
        params.put("AUTHORIZE_TYPE_03", DictCodeList.授权类型_无限授权.getValue());
        if(StringUtil.isNotEmpty(flag)){
            params.put("AUTHORIZE_TYPE_01", DictCodeList.授权类型_不授权.getValue());
        }
        return commonBusinessMapper.getAuthorizeLimitAmt(params);
    }

    /**
     * 获取副总裁的授权金额，如果数据为null，则视为无权
     *
     * @param assetType 资产类型
     * @param assetSecType 资产一级分类
     * @param tradeMarket 交易场所
     * @param tradeDirection 交易方向
     * @return 授权金额
     */
    public BigDecimal getAuthorizeLimitAmtForFuZongCai(String assetType, String assetSecType, String tradeMarket,
                                                    String tradeDirection) {
        if (StringUtil.isEmpty(assetType)) {
            return null;
        }
        Map<String, String> params = new HashMap<>();
        params.put("rolePos", DictCodeList.角色岗位_领导.getValue());
        params.put("assetType", assetType);
        params.put("assetSecType", assetSecType);
        params.put("tradeMarket", tradeMarket);
        params.put("tradeDirection", tradeDirection);
        String institutionCode = MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
        String institutionTopCode = MultilevelDataAuthorityUtil.getService().queryTopOrganizationCode(LoginUserUtil.getUserName());
        params.put("institutionCode", institutionCode);
        params.put("institutionTopCode", institutionTopCode);
        BigDecimal result = commonBusinessMapper.getAuthorizeLimitAmtForFuZongCai(params);
        if (result == null) {
            result = BigDecimal.ZERO;
        }

        return result;
    }

    /**
     * 判断是否需要上级审批
     *
     * @param counterId 交易对手
     * @param prinAmt 金额
     * @param userName 用户名
     * @param assetType 资产类型
     * @param assetSecType 资产一级分类
     * @param tradeMarket 交易场所
     * @param tradeDirection 交易方向
     * @return 授权金额
     */
    public Map<String, Object> 是否需要上级审批(Map<String, Object> param) throws WorkFlowException {
        String counterId = (String) param.get("counterId");
        BigDecimal prinAmt = (BigDecimal) param.get("prinAmt");
        String userName = (String) param.get("userName");
        String assetType = (String) param.get("assetType");
        String assetSecType = (String) param.get("assetSecType");
        String tradeMarket = (String) param.get("tradeMarket");
        String tradeDirection = (String) param.get("tradeDirection");
        String assetId = (String) param.get("assetId");
        String workflowId = (String) param.get("workflowId");
        List<RiskTrialRetDto> riskTrialRetDtoList = (List<RiskTrialRetDto>)param.get("riskTrialRetDto");
//        Long term = (Long) param.get("term");
        Map<String, Object> variables = new HashMap<>();
        
        /** 获取 否超审批线  add by chendijun start**/
        //初始化值
        setRiskVariables(workflowId, riskTrialRetDtoList, variables);
//        variables.put(RiskUtil.APPROVE_LINE_ONE, false);
//        variables.put(RiskUtil.APPROVE_LINE_TWO, false);
//        variables.put(RiskUtil.APPROVE_LINE_THR, false);
//        
//        if(StringUtil.isNotEmpty(workflowId) && !StringUtil.isEmptyList(riskTrialRetDtoList)) {
//            Map<String,List<RiskTrialRetDto>> riskMap = new HashMap<>();
//            riskMap.put(workflowId, riskTrialRetDtoList);
//            
//            Map<String,Object> riskExceedApproveMap = riskTrialComm.getRiskExceedApproveByEntity(riskMap);
//            Map<String,Object> resultMap = (Map<String, Object>)riskExceedApproveMap.get(workflowId);
//            if(StringUtil.isNotEmpty(resultMap)) {
//                variables.putAll(resultMap);   
//            }  
//        }
        
        /** 获取 否超审批线  add by chendijun end**/
        
        logger.debug("[counterId:{},prinAmt:{},userName:{},assetType:{},assetSecType:{},tradeMarket:{},tradeDirection:{},assetId:{}]",counterId,prinAmt,userName,assetType,assetSecType,tradeMarket,tradeDirection,assetId);

        // modify by wj 20200620 二级法人改造 五个特殊参数 修改 为特殊取值方法
        String institutionCode = "";
        if(MultilevelDataAuthorityUtil.isEnabled()){
            institutionCode =  MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName());
        }
        String paraAuthorizeLimit = CacheUtil.getSpecialSysParam(BussinessConstant.AUTHORIZE_LIMIT, institutionCode);

        if (paraAuthorizeLimit == null || BussinessConstant.AUTHORIZE_LIMIT.equals(paraAuthorizeLimit)
            || DictCodeListExt1.是或否_否.getValue().equals(paraAuthorizeLimit)) {
            variables.put(BussinessConstant.是否需要上级审批, false);
            return variables;
        }

        PtlPortfolio portfolio = ptlPortfolioMapper.selectByPrimaryKey(counterId);
        // 内部交易不走授权
        if (portfolio != null) {
            variables.put(BussinessConstant.是否需要上级审批, false);
            return variables;
        }
        // 资产类型改为授权类型 modify by renping 2019-08-06  start
        // 根据资产类型取得授权类型 
        FinProductTypeConfig finProductTypeConfig = getRskAuthorizeType(assetId, assetType);
        // region 映射表中存在F12 --> SQ_14
//        if (StringUtil.equals(DictCodeListExt2.平安资产类型_债券借贷.getValue(), assetType)){
//            finProductTypeConfig.setOutputValue1("12");
//        }

        // end region
        Map<String, Object> authorizeLimitAmt = null;
        if(finProductTypeConfig!= null && StringUtil.isNotEmpty(finProductTypeConfig.getOutputValue1())){
            authorizeLimitAmt = getAuthorizeLimitAmt(LoginUserUtil.getUserName(), finProductTypeConfig.getOutputValue1(), finProductTypeConfig.getOutputValue2(),
                    tradeMarket, tradeDirection, null);
        } else {
            variables.put(BussinessConstant.是否需要上级审批, false);
            return variables;
        }
//        Map<String, Object> authorizeLimitAmt = getAuthorizeLimitAmt(LoginUserUtil.getUserName(), finProductTypeConfig.getOutputValue1(), assetSecType,
//            tradeMarket, tradeDirection, null);

        if (authorizeLimitAmt == null || authorizeLimitAmt.get("AUTHORIZE_LIMIT_AMT") == null
            || DictCodeList.授权类型_不授权.getValue().equals(authorizeLimitAmt.get("AUTHORIZE_TYPE"))) {
            String 交易方向 = SysDataUtil.getNameFromDataDict(DictCode.交易方向, tradeDirection);
            if (交易方向 == null) {
                交易方向 = SysDataUtil.getNameFromDataDict(DictCode.回购拆借方向, tradeDirection);
            }

            // modify by etliu 20220325 start
            // 没做授权时，直接当时授权不足处理，由领导审批
//            WorkFlowException e = new WorkFlowException(WflCode.WFL_E00025,
//                    "未授权或未设置授权: 金额：" + prinAmt + "，用户：" + userName + "，授权类型一级："
//                            + SysDataUtil.getNameFromDataDict(DictCode.授权类型一级,
//                                    finProductTypeConfig.getOutputValue1())
//                            + "(" + finProductTypeConfig.getOutputValue1() + ")，授权类型二级:"
//                            + (StringUtil.isEmpty(finProductTypeConfig.getOutputValue2()) ? "空"
//                                    : finProductTypeConfig.getOutputValue2())
//                            + "，交易场所：" + SysDataUtil.getNameFromDataDict(DictCode.交易场所, tradeMarket)
//                            + "，交易方向：" + 交易方向);
//            WorkFlowException e = new WorkFlowException(
//                WflCode.WFL_E00025, "未授权或未设置授权: 金额：" + prinAmt + "，用户：" + userName + "，资产类型："
//                                    + SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, assetType) + "(" + assetType
//                                    + ")，资产一级类型:" + assetSecType + "，交易场所："
//                                    + SysDataUtil.getNameFromDataDict(DictCode.交易场所, tradeMarket) + "，交易方向：" + 交易方向);
            // 资产类型改为授权类型 modify by renping 2019-08-06  end
//            logger.error("未授权或未设置授权", e);
//            throw e;
            variables.put(BussinessConstant.是否需要上级审批, true);
            return variables;
            // modify by etliu 20220325 end
        }
        // 用户最大权限是无限授权jine
        try{
            if(authorizeLimitAmt!=null){
                logger.debug("开始输出授权相关参数-------");
                authorizeLimitAmt.entrySet().forEach(entry->{
                    if(entry!=null){
                        logger.debug("[key:{},value{}]",entry.getKey(),entry.getValue());
                    }
                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage(),e);
        }
        boolean 是否需要上级审批 = !DictCodeList.授权类型_无限授权.getValue().equals(authorizeLimitAmt.get("AUTHORIZE_TYPE"));
        //
        是否需要上级审批 = 是否需要上级审批 && prinAmt.compareTo((BigDecimal)authorizeLimitAmt.get("AUTHORIZE_LIMIT_AMT")) > 0;
        variables.put(BussinessConstant.是否需要上级审批, 是否需要上级审批);

        try{
            if(variables!=null){
                logger.debug("开始输出工作流相关参数-------");
                variables.entrySet().forEach(entry->{
                    if(entry!=null){
                        logger.debug("[key:{},value{}]",entry.getKey(),entry.getValue());
                    }
                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage(),e);
        }
        return variables;
    }
    // 资产类型改为授权类型 add by renping 2019-08-06  start
    /**
     * 根据资产类型取得授权类型
     *
     * @param assetTypeList 资产类型
     * @see
     * @since 1.0
     */
    private FinProductTypeConfig getRskAuthorizeType(String assetId, String assetType) {
        if (assetId != null && assetId.length() > 0) {
            FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
            finProductTypeKey.setFinprodId(assetId);
            finProductTypeKey.setBranch(0L);
            FinProductType finProductType = finProductTypeMapper
                    .selectByPrimaryKey(finProductTypeKey);
            FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
            finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_授权分类.getValue());
            finProductTypeConfig.setInputValue1(finProductType.getType8());
            finProductTypeConfig.setInputValue2(finProductType.getType9());
            finProductTypeConfig.setInputValue3(finProductType.getType10());
            finProductTypeConfig.setInputValue4(finProductType.getType11());
            finProductTypeConfig.setInputValue5(finProductType.getType12());
            finProductTypeConfig = restTemplate.postForObject(
                    ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
                    finProductTypeConfig, FinProductTypeConfig.class);
            return finProductTypeConfig;
        } else if (StringUtil.isNotEmpty(assetType)) {
            FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
            finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_授权分类.getValue());
            finProductTypeConfig.setInputValue1(assetType);
            finProductTypeConfig = restTemplate.postForObject(
                    ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
                    finProductTypeConfig, FinProductTypeConfig.class);
            return finProductTypeConfig;
        }
        return null;
    }

    /**
     * 是否超过副总额额度审批额度(委托用)
     *
     * @param counterId
     *            交易对手
     * @param prinAmt
     *            金额
     * @param assetType
     *            资产类型
     * @param assetSecType
     *            资产一级分类
     * @param tradeMarket
     *            交易场所
     * @param tradeDirection
     *            交易方向
     * @return 是否超过副总额额度审批额度
     */
    public boolean 是否超过副总额额度_委托用(Map<String, Object> param) {
        String counterId = (String) param.get("counterId");
        BigDecimal prinAmt = (BigDecimal) param.get("prinAmt");
        String userName = (String) param.get("userName");
        String assetType = (String) param.get("assetType");
        String assetSecType = (String) param.get("assetSecType");
        String tradeMarket = (String) param.get("tradeMarket");
        String tradeDirection = (String) param.get("tradeDirection");
        String assetId = (String) param.get("assetId");
        BigDecimal term = (BigDecimal) param.get("term"); // 期限

        if (CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH) == null
                || BussinessConstant.ETRLIMIT_SWITCH
                        .equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH))
                || DictCodeListExt1.是或否_否.getValue()
                        .equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH))) {
            return false;
        }

        PtlPortfolio portfolio = ptlPortfolioMapper.selectByPrimaryKey(counterId);
        // 内部交易走授权
        if (portfolio != null) {
            return false;
        }

        // modify by wj 20200620  五个特殊参数 修改 为特殊取值方法
        String institutionCode = "";
        if(MultilevelDataAuthorityUtil.isEnabled()){
            institutionCode =  MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName());
        }
        String paraAuthorizeLimit = CacheUtil.getSpecialSysParam(BussinessConstant.AUTHORIZE_LIMIT, institutionCode);

        if (paraAuthorizeLimit == null || BussinessConstant.AUTHORIZE_LIMIT.equals(paraAuthorizeLimit)
                || DictCodeListExt1.是或否_否.getValue().equals(paraAuthorizeLimit)) {
            return false;
        }

        // 根据资产类型取得授权类型
        FinProductTypeConfig finProductTypeConfig = getRskAuthorizeType(assetId, assetType);
        BigDecimal authorizeLimitAmt = null;
        if (finProductTypeConfig!= null && StringUtil.isNotEmpty(finProductTypeConfig.getOutputValue1())) {
            authorizeLimitAmt = getAuthorizeLimitAmtForFuZongCai(
                    finProductTypeConfig.getOutputValue1(), finProductTypeConfig.getOutputValue2(),
                    tradeMarket, tradeDirection);
        }
        // authorizeLimitAmt=null时，authorizeLimitAmt=0 赵东东 2019.8.26 start
        else {
        	return false;
        }
        if(authorizeLimitAmt == null){
            authorizeLimitAmt = BigDecimal.ZERO;
        }
        if (prinAmt != null && prinAmt.compareTo(authorizeLimitAmt) > 0) {
            return true;
        }
        // authorizeLimitAmt=null时，authorizeLimitAmt=0 赵东东 2019.8.26 end
        return false;
    }

    /**
     * 判断是否需要上级审批
     *
     * @param counterId
     *            交易对手
     * @param prinAmt
     *            金额
     * @param userName
     *            用户名
     * @param assetType
     *            资产类型
     * @param assetSecType
     *            资产一级分类
     * @param tradeMarket
     *            交易场所
     * @param tradeDirection
     *            交易方向
     * @return 授权金额
     */
    public Map<String, Object> 是否需要上级审批_委托用(Map<String, Object> param) throws WorkFlowException {
        String counterId = (String) param.get("counterId");
        BigDecimal prinAmt = (BigDecimal) param.get("prinAmt");
        String userName = (String) param.get("userName");
        String assetType = (String) param.get("assetType");
        String assetSecType = (String) param.get("assetSecType");
        String tradeMarket = (String) param.get("tradeMarket");
        String tradeDirection = (String) param.get("tradeDirection");
        String assetId = (String) param.get("assetId");
        BigDecimal term = (BigDecimal) param.get("term"); // 期限
        
        if (prinAmt == null) {
            WorkFlowException e = new WorkFlowException(WflCode.WFL_E00025, "金额设置有误：" + prinAmt);
            logger.error("金额设置有误", e);
            throw e;
        }

        Map<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("tradeMarket", StringUtil.isEmpty(tradeMarket) ? "999999" : tradeMarket);
        params.put("tradeDirection", tradeDirection);
        if (term != null) {
            if (term.intValue() > 30) {
                params.put("termType", "02");
            }
            else {
                params.put("termType", "01");
            }
        }
        
        Map<String, Object> variables = new HashMap<>();
        if (CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH) == null
                || BussinessConstant.ETRLIMIT_SWITCH
                        .equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH))
                || DictCodeListExt1.是或否_否.getValue()
                        .equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH))) {
            variables.put(BussinessConstant.是否需要上级审批, false);
            return variables;
        }

        PtlPortfolio portfolio = ptlPortfolioMapper.selectByPrimaryKey(counterId);
        // 内部交易不走授权
        if (portfolio != null) {
            variables.put(BussinessConstant.是否需要上级审批, false);
            return variables;
        }

        // modify by wj 20200620 二级法人改造 五个特殊参数 修改 为特殊取值方法
        String institutionCode = "";
        if(MultilevelDataAuthorityUtil.isEnabled()){
            institutionCode =  MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName());
        }
        String paraAuthorizeLimit = CacheUtil.getSpecialSysParam(BussinessConstant.AUTHORIZE_LIMIT, institutionCode);

        if (paraAuthorizeLimit == null || BussinessConstant.AUTHORIZE_LIMIT.equals(paraAuthorizeLimit)
                || DictCodeListExt1.是或否_否.getValue().equals(paraAuthorizeLimit)) {
            variables.put(BussinessConstant.是否需要上级审批, false);
            return variables;
        }

        // 根据资产类型取得授权类型
        FinProductTypeConfig finProductTypeConfig = getRskAuthorizeType(assetId, assetType);
        Map<String, Object> authorizeLimitAmt = null;
        // region 映射表中存在F12 --> SQ_14
//        if (StringUtil.equals(DictCodeListExt2.平安资产类型_债券借贷.getValue(), assetType)){
//            finProductTypeConfig.setOutputValue1("12");
//        }

        // end region
        if (finProductTypeConfig!= null && StringUtil.isNotEmpty(finProductTypeConfig.getOutputValue1())) {
            authorizeLimitAmt = getAuthorizeLimitAmt(LoginUserUtil.getUserName(),
                    finProductTypeConfig.getOutputValue1(), finProductTypeConfig.getOutputValue2(),
                    tradeMarket, tradeDirection, null);
        } else {
            variables.put(BussinessConstant.是否需要上级审批, false);
            return variables;
        }
        if (authorizeLimitAmt == null || authorizeLimitAmt.get("AUTHORIZE_LIMIT_AMT") == null
                || DictCodeList.授权类型_不授权.getValue()
                        .equals(authorizeLimitAmt.get("AUTHORIZE_TYPE"))) {
            String 交易方向 = SysDataUtil.getNameFromDataDict(DictCode.交易方向, tradeDirection);
            if (交易方向 == null) {
                交易方向 = SysDataUtil.getNameFromDataDict(DictCode.回购拆借方向, tradeDirection);
            }

            // modify by etliu 20220325 start
            // 没做授权时，直接当时授权不足处理，由领导审批
//            WorkFlowException e = new WorkFlowException(WflCode.WFL_E00025,
//                    "未授权或未设置授权: 金额：" + prinAmt + "，用户：" + userName + "，授权类型一级："
//                            + SysDataUtil.getNameFromDataDict(DictCode.授权类型一级,
//                                    finProductTypeConfig.getOutputValue1())
//                            + "(" + finProductTypeConfig.getOutputValue1() + ")，授权类型二级:"
//                            + (StringUtil.isEmpty(finProductTypeConfig.getOutputValue2()) ? "空"
//                                    : finProductTypeConfig.getOutputValue2())
//                            + "，交易场所：" + SysDataUtil.getNameFromDataDict(DictCode.交易场所, tradeMarket)
//                            + "，交易方向：" + 交易方向);
//            logger.error("未授权或未设置授权", e);
//            throw e;
            variables.put(BussinessConstant.是否需要上级审批, true);
            return variables;
            // modify by etliu 20220325 end
        }
        // 用户最大权限是无限授权jine
        boolean 是否需要上级审批 = !DictCodeList.授权类型_无限授权.getValue()
                .equals(authorizeLimitAmt.get("AUTHORIZE_TYPE"));
        //
        是否需要上级审批 = 是否需要上级审批
                && prinAmt.compareTo((BigDecimal) authorizeLimitAmt.get("AUTHORIZE_LIMIT_AMT")) > 0;
        variables.put(BussinessConstant.是否需要上级审批, 是否需要上级审批);
        return variables;
    }
    private void setRiskVariables(String workflowId, List<RiskTrialRetDto> riskTrialRetDtoList, Map<String, Object> variables) {
        /** 获取 否超审批线  add by chendijun start**/
        //初始化值
        variables.put(RiskUtil.APPROVE_LINE_ONE, false);
        variables.put(RiskUtil.APPROVE_LINE_TWO, false);
        variables.put(RiskUtil.APPROVE_LINE_THR, false);

//        if (StringUtil.isNotEmpty(workflowId) && !StringUtil.isEmptyList(riskTrialRetDtoList)) {
        if (!StringUtil.isEmptyList(riskTrialRetDtoList)) {
            Map<String, List<RiskTrialRetDto>> riskMap = new HashMap<>();
            riskMap.put(workflowId, riskTrialRetDtoList);

            Map<String, Object> riskExceedApproveMap = riskTrialComm.getRiskExceedApproveByEntity(riskMap);
            Map<String, Object> resultMap = (Map<String, Object>) riskExceedApproveMap.get(workflowId);
            if (StringUtil.isNotEmpty(resultMap)) {
                variables.putAll(resultMap);
            }
        }
        /** 获取 否超审批线  add by chendijun end**/}
    // 资产类型改为授权类型 modify by renping 2019-08-06  end
    /**
     * 是否超过交易员额度审批额度
     *
     * @param counterId 交易对手
     * @param prinAmt 金额
     * @param assetType 资产类型
     * @param assetSecType 资产一级分类
     * @param tradeMarket 交易场所
     * @param tradeDirection 交易方向
     * @return 是否超过副总额额度审批额度
     */
    public boolean 是否超过交易员额度(String counterId, BigDecimal prinAmt, String assetType, String assetSecType,
                             String tradeMarket, String tradeDirection) {

        // modify by wj 20200620  五个特殊参数 修改 为特殊取值方法
        String institutionCode = "";
        if(MultilevelDataAuthorityUtil.isEnabled()){
            institutionCode =  MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName());
        }
        String paraAuthorizeLimit = CacheUtil.getSpecialSysParam(BussinessConstant.AUTHORIZE_LIMIT, institutionCode);

        if (paraAuthorizeLimit == null || BussinessConstant.AUTHORIZE_LIMIT.equals(paraAuthorizeLimit)
            || DictCodeListExt1.是或否_否.getValue().equals(paraAuthorizeLimit)) {
            return false;
        }
        
        PtlPortfolio portfolio = ptlPortfolioMapper.selectByPrimaryKey(counterId);
        // 内部交易走授权
        if (portfolio != null) {
            return false;
        }

        // 获取配置映射
        FinProductTypeConfig finProductTypeConfig = getRskAuthorizeType(null, assetType);

        // Sonar Fix modify by huwei 220608
        if (StringUtils.isEmpty(finProductTypeConfig)) {
            finProductTypeConfig = new FinProductTypeConfig();
        }
        Map<String, Object> authorizeLimitAmtMap = getAuthorizeLimitAmt(LoginUserUtil.getUserName()
                , finProductTypeConfig.getOutputValue1(), finProductTypeConfig.getOutputValue2(),
            tradeMarket, tradeDirection, "01");

        if (authorizeLimitAmtMap != null && !DictCodeList.授权类型_授权.getValue().equals(authorizeLimitAmtMap.get("AUTHORIZE_TYPE"))) {
            return false;
        }
        BigDecimal authorizeLimitAmt = BigDecimal.ZERO;

        if(authorizeLimitAmtMap != null){
            authorizeLimitAmt  = (BigDecimal)authorizeLimitAmtMap.get("AUTHORIZE_LIMIT_AMT");
        }
        if (prinAmt != null && prinAmt.compareTo(authorizeLimitAmt) > 0) {
            return true;
        }
        
        return false;
    }
    /**
     * 是否交易员授权
     *
     * @param counterId 交易对手
     * @param prinAmt 金额
     * @param assetType 资产类型
     * @param assetSecType 资产一级分类
     * @param tradeMarket 交易场所
     * @param tradeDirection 交易方向
     * @return 是否超过副总额额度审批额度
     */
    public boolean 是否交易员授权(String counterId, BigDecimal prinAmt, String assetType, String assetSecType,
                             String tradeMarket, String tradeDirection,String assetId) {

        // modify by wj 20200620  五个特殊参数 修改 为特殊取值方法
        String institutionCode = "";
        if(MultilevelDataAuthorityUtil.isEnabled()){
            institutionCode =  MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName());
        }
        String paraAuthorizeLimit = CacheUtil.getSpecialSysParam(BussinessConstant.AUTHORIZE_LIMIT, institutionCode);

        if (paraAuthorizeLimit == null || BussinessConstant.AUTHORIZE_LIMIT.equals(paraAuthorizeLimit)
            || DictCodeListExt1.是或否_否.getValue().equals(paraAuthorizeLimit)) {
            return false;
        }

        PtlPortfolio portfolio = ptlPortfolioMapper.selectByPrimaryKey(counterId);
        // 内部交易走授权
        if (portfolio != null) {
            return false;
        }
        Map<String, Object> authorizeLimitAmtMap = new HashMap<>();
        // 根据资产类型取得授权类型
        FinProductTypeConfig finProductTypeConfig = getRskAuthorizeType(assetId, assetType);
        if (finProductTypeConfig!= null && StringUtil.isNotEmpty(finProductTypeConfig.getOutputValue1())) {
            authorizeLimitAmtMap = getAuthorizeLimitAmt(LoginUserUtil.getUserName(), finProductTypeConfig.getOutputValue1(), finProductTypeConfig.getInputValue2(),
                    tradeMarket, tradeDirection, "01");
        }

        if (!StringUtil.isNotEmpty(authorizeLimitAmtMap)) {
            return true;
        }

        return false;
    }
    /**
     * 是否超过副总额额度审批额度
     *
     * @param counterId 交易对手
     * @param prinAmt 金额
     * @param assetType 资产类型
     * @param assetSecType 资产一级分类
     * @param tradeMarket 交易场所
     * @param tradeDirection 交易方向
     * @return 是否超过副总额额度审批额度
     */
    public boolean 是否超过副总额额度(Map<String, Object> param) {
        String counterId = (String) param.get("counterId");
        BigDecimal prinAmt = (BigDecimal) param.get("prinAmt");
        String userName = (String) param.get("userName");
        String assetType = (String) param.get("assetType");
        String assetSecType = (String) param.get("assetSecType");
        String tradeMarket = (String) param.get("tradeMarket");
        String tradeDirection = (String) param.get("tradeDirection");
        String assetId = (String) param.get("assetId");
        BigDecimal term = (BigDecimal) param.get("term"); // 期限

        // modify by wj 20200620  五个特殊参数 修改 为特殊取值方法
        String institutionCode = "";
        if(MultilevelDataAuthorityUtil.isEnabled()){
            institutionCode =  MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName());
        }
        String paraAuthorizeLimit = CacheUtil.getSpecialSysParam(BussinessConstant.AUTHORIZE_LIMIT, institutionCode);

        if (paraAuthorizeLimit == null || BussinessConstant.AUTHORIZE_LIMIT.equals(paraAuthorizeLimit)
            || DictCodeListExt1.是或否_否.getValue().equals(paraAuthorizeLimit)) {
            return false;
        }

        PtlPortfolio portfolio = ptlPortfolioMapper.selectByPrimaryKey(counterId);
        // 内部交易走授权
        if (portfolio != null) {
            return false;
        }
        // 资产类型改为授权类型 modify by renping 2019-08-06  start
        // 根据资产类型取得授权类型
        FinProductTypeConfig finProductTypeConfig = getRskAuthorizeType(assetId, assetType);
        BigDecimal authorizeLimitAmt = null;
        if (finProductTypeConfig!= null && StringUtil.isNotEmpty(finProductTypeConfig.getOutputValue1())) {
            authorizeLimitAmt = getAuthorizeLimitAmtForFuZongCai(
                    finProductTypeConfig.getOutputValue1(), finProductTypeConfig.getOutputValue2(),
                    tradeMarket, tradeDirection);
        }
        // authorizeLimitAmt=null时，authorizeLimitAmt=0 赵东东 2019.8.26 start 
        else {
        	return false;
        }
//        BigDecimal authorizeLimitAmt = getAuthorizeLimitAmtForFuZongCai(assetType, assetSecType,
//            tradeMarket, tradeDirection);
     // authorizeLimitAmt=null时，authorizeLimitAmt=0 赵东东 2019.8.26 start 
        if(authorizeLimitAmt == null){
            authorizeLimitAmt = BigDecimal.ZERO;
        }
        if (prinAmt != null && prinAmt.compareTo(authorizeLimitAmt) > 0) {
            return true;
        }
     // authorizeLimitAmt=null时，authorizeLimitAmt=0 赵东东 2019.8.26 end
        // 资产类型改为授权类型 modify by renping 2019-08-06  end
        return false;
    }
    
    /**
     * 是否超过副总额额度审批额度
     *
     * @param counterId 交易对手
     * @param prinAmt 金额
     * @param assetType 资产类型
     * @param assetSecType 资产一级分类
     * @param tradeMarket 交易场所
     * @param tradeDirection 交易方向
     * @return 是否超过副总额额度审批额度
     */
    public boolean 是否超过副总额额度_指令用(Map<String, Object> param) {
        String counterId = (String) param.get("counterId");
        BigDecimal prinAmt = (BigDecimal) param.get("prinAmt");
        String userName = (String) param.get("userName");
        String assetType = (String) param.get("assetType");
        String assetSecType = (String) param.get("assetSecType");
        String tradeMarket = (String) param.get("tradeMarket");
        String tradeDirection = (String) param.get("tradeDirection");
        String assetId = (String) param.get("assetId");
        BigDecimal term = (BigDecimal) param.get("term"); // 期限

        if (CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH) == null
            || BussinessConstant.ETRLIMIT_SWITCH
                    .equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH))
            || DictCodeListExt1.是或否_否.getValue()
                    .equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH))) {
            return false;
        }

        PtlPortfolio portfolio = ptlPortfolioMapper.selectByPrimaryKey(counterId);
        // 内部交易走授权
        if (portfolio != null) {
            return false;
        }

        // modify by wj 20200620  五个特殊参数 修改 为特殊取值方法
        String institutionCode = "";
        if(MultilevelDataAuthorityUtil.isEnabled()){
            institutionCode =  MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName());
        }
        String paraAuthorizeLimit = CacheUtil.getSpecialSysParam(BussinessConstant.AUTHORIZE_LIMIT, institutionCode);

        if (paraAuthorizeLimit == null || BussinessConstant.AUTHORIZE_LIMIT.equals(paraAuthorizeLimit)
                || DictCodeListExt1.是或否_否.getValue().equals(paraAuthorizeLimit)) {
            return false;
        }

        // 资产类型改为授权类型 modify by renping 2019-08-06  start
        // 根据资产类型取得授权类型
        FinProductTypeConfig finProductTypeConfig = getRskAuthorizeType(assetId, assetType);
        BigDecimal authorizeLimitAmt = null;
        if (finProductTypeConfig!= null && StringUtil.isNotEmpty(finProductTypeConfig.getOutputValue1())) {
            authorizeLimitAmt = getAuthorizeLimitAmtForFuZongCai(
                    finProductTypeConfig.getOutputValue1(), finProductTypeConfig.getOutputValue2(),
                    tradeMarket, tradeDirection);
        }
        // authorizeLimitAmt=null时，authorizeLimitAmt=0 赵东东 2019.8.26 start 
        else {
            return false;
        }
//        BigDecimal authorizeLimitAmt = getAuthorizeLimitAmtForFuZongCai(assetType, assetSecType,
//            tradeMarket, tradeDirection);
     // authorizeLimitAmt=null时，authorizeLimitAmt=0 赵东东 2019.8.26 start 
        if(authorizeLimitAmt == null){
            authorizeLimitAmt = BigDecimal.ZERO;
        }
        if (prinAmt != null && prinAmt.compareTo(authorizeLimitAmt) > 0) {
            return true;
        }
     // authorizeLimitAmt=null时，authorizeLimitAmt=0 赵东东 2019.8.26 end
        // 资产类型改为授权类型 modify by renping 2019-08-06  end
        return false;
    }

    /**
     * 是否是总集节点
     *
     * @param tradeId 交易编号
     * @return
     */
    public boolean isBackCheckNode(String tradeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("tradeId", tradeId);
        params.put("userName", SecurityUtil.getLoginUser().getUserName());
        params.put(BussinessConstant.总集经办, BussinessConstant.总集经办);
        params.put(BussinessConstant.总集复核, BussinessConstant.总集复核);
        Boolean result = commonBusinessMapper.isBackCheckNode(params);
        return result == null ? false : result;
    }
    /**
     * 设置市场代码后缀交易市场
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @param model
     * @see
     * @since 1.0
     */
   public void setTradeMarketId(FinProduct dto,Model model) {
       String finprodMarketId = dto.getFinprodMarketId();
       int beginIndex = StringUtil.indexOf(finprodMarketId, GlobalConstant.POINT);
       if(beginIndex > 0) {
           String tradeMarketId = finprodMarketId.substring(beginIndex);
           model.addAttribute("tradeMarketId",tradeMarketId);
           finprodMarketId = finprodMarketId.substring(0, beginIndex);
           dto.setFinprodMarketId(finprodMarketId);
       }
   }

   /**
    * 获取DVP账户余额
    * @return DVP账户余额
    * @see
    * @since 1.0
    */
   public BigDecimal getDvpAcctAmount(String portfolioId, String ccy, String acctId, String cdate) {
        if (StringUtil.isBlank(portfolioId) || StringUtil.isBlank(ccy)
            || StringUtil.isBlank(acctId) || StringUtil.isBlank(cdate)) {
            return null;
        }

        // 查询指令表数据
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("portfolioId", portfolioId);
        paramMap.put("ccy", ccy);
        paramMap.put("acctId", acctId);
        paramMap.put("cdate", DateUtil.parse(cdate));
        paramMap.put("ORDER_STATUS", DictCodeListExt1.指令状态_新建.getValue());
        List<StlPayOrders> stlPayOrdersList = commonBusinessMapper.selectStlPayOrdersList(paramMap);

        BigDecimal stlPayOrdersAmount = BigDecimal.ZERO;
        if (!StringUtil.isEmptyList(stlPayOrdersList)) {
            for (StlPayOrders stlPayOrders : stlPayOrdersList) {
                if (StringUtil.equals(acctId, stlPayOrders.getMyAcctNo())) {
                    if (StringUtil.equals(DictCodeListExt1.收支方向_支出.getValue(), stlPayOrders.getPs())) {
                        stlPayOrdersAmount = ArithmeticUtil.subtract(stlPayOrdersAmount, stlPayOrders.getSettleAmt());
                    }
                    else {
                        stlPayOrdersAmount = ArithmeticUtil.doAdd(stlPayOrdersAmount, stlPayOrders.getSettleAmt());
                    }
                }
                else {
                    if (StringUtil.equals(DictCodeListExt1.收支方向_支出.getValue(), stlPayOrders.getPs())) {
                        stlPayOrdersAmount = ArithmeticUtil.doAdd(stlPayOrdersAmount, stlPayOrders.getSettleAmt());
                    }
                    else {
                        stlPayOrdersAmount = ArithmeticUtil.subtract(stlPayOrdersAmount, stlPayOrders.getSettleAmt());
                    }
                }
            }
        }
        // 取相反数
        stlPayOrdersAmount = stlPayOrdersAmount.negate();

        // 查询DVP指令表数据
        List<StlDvpOrders> stlDvpOrdersList = commonBusinessMapper.selectStlDvpOrdersList(paramMap);
        BigDecimal stlDvpOrdersAmount = BigDecimal.ZERO;
        if (!StringUtil.isEmptyList(stlDvpOrdersList)) {
            for (StlDvpOrders stlDvpOrders : stlDvpOrdersList) {
                if (StringUtil.equals(acctId, stlDvpOrders.getMyAcctNo())) {
                    if (StringUtil.equals(DictCodeListExt1.收支方向_支出.getValue(), stlDvpOrders.getPs())) {
                        stlDvpOrdersAmount = ArithmeticUtil.subtract(stlDvpOrdersAmount, stlDvpOrders.getSettleAmt());
                    }
                    else {
                        stlDvpOrdersAmount = ArithmeticUtil.doAdd(stlDvpOrdersAmount, stlDvpOrders.getSettleAmt());
                    }
                }
                else {
                    if (StringUtil.equals(DictCodeListExt1.收支方向_支出.getValue(), stlDvpOrders.getPs())) {
                        stlDvpOrdersAmount = ArithmeticUtil.doAdd(stlDvpOrdersAmount, stlDvpOrders.getSettleAmt());
                    }
                    else {
                        stlDvpOrdersAmount = ArithmeticUtil.subtract(stlDvpOrdersAmount, stlDvpOrders.getSettleAmt());
                    }
                }
            }
        }

       return ArithmeticUtil.doAdd(stlPayOrdersAmount, stlDvpOrdersAmount);
   }
   
   /**
    * 
    * 重置计划生成																																												
    * <功能详细描述>
    * @param irr 利率重置规则
    * @param scheList 付息计划
    * @param holidayList 节假日列表
    * @return finCashResetPlanList  重置计划列表
 * @throws Exception 
    * @see
    * @since 1.0
    */
   public List<FinCashResetPlan> calFinCashResetPlanList(FinCashResetRuleDto finCashResetRuleDto,
		   List<FinCashPlanDto> scheList,List<String> holidayList) throws Exception{
	   // 重置计划列表
	   List<FinCashResetPlan> finCashResetPlanList = new ArrayList<FinCashResetPlan>();
	   // 频率值
	   int resetFreqUnit = finCashResetRuleDto.getResetFreqUnit()==0?1:finCashResetRuleDto.getResetFreqUnit();
	   // 频率
	   String resetFreq = finCashResetRuleDto.getResetFreq();
	   // 提前观察单位
	   String observeBefUnit = finCashResetRuleDto.getObserveBefUnit();
	   // 提前观察数量
	   int observeBefDay = finCashResetRuleDto.getObserveBefDay()==null?
			   0:finCashResetRuleDto.getObserveBefDay().intValue();
	   // 获取支付频率
	   // 周期
	   int cycle = resetFreqUnit;
	   // 周期单位
	   String cycleUnit = resetFreq;
	   
	   // 周 = 7 日
	   if(StringUtil.equals(resetFreq, DictCodeListExt2.频率_周.getValue())) {
		   cycle = 7*resetFreqUnit;
		   cycleUnit = DictCodeList.频率_日.getValue();
	   }
	   // 双周 = 2 周 = 2*7 日
	   if(StringUtil.equals(resetFreq, DictCodeListExt2.频率_双周.getValue())) {
		   cycle = 14*resetFreqUnit;
		   cycleUnit = DictCodeList.频率_日.getValue();
	   }
	   // 季 = 3 月
	   if(StringUtil.equals(resetFreq, DictCodeList.频率_季.getValue())) {
		   cycle = 3*resetFreqUnit;
		   cycleUnit = DictCodeList.频率_月.getValue();
	   }
	   // 半年 = 6 月
	   if(StringUtil.equals(resetFreq, DictCodeList.频率_半年.getValue())) {
		   cycle = 6*resetFreqUnit;
		   cycleUnit = DictCodeList.频率_月.getValue();
	   }
	   // 到期支付 
	   if(StringUtil.equals(resetFreq, DictCodeList.频率_到期一次.getValue())) {
		   cycle = 0;
		   cycleUnit = "";
	   }
	   
	   // 遍历现金流计划表
	   for(FinCashPlanDto finCashPlanDto: scheList){
		   // 开始日 = 调整后的计算开始日
		   Date vdate = finCashPlanDto.getVdate();
		   // 结束日 = 调整后的计算开始日
		   Date mdate = finCashPlanDto.getMdate();
		   do {
			   FinCashResetPlan finCashResetPlan = new FinCashResetPlan();
			   // 未调整的重置日
			   finCashResetPlan.setResetDateUnadjust(vdate);
			   // 调整后的重置日
			   finCashResetPlan.setResetDate(vdate);
			   // 提前观察单位为工作日
			   if(StringUtil.equals(observeBefUnit, DictCodeList.提前观察单位_延后时间单位_工作日.getValue())) {
				   // 交易日推算
				   finCashResetPlan.setConfirmDate(DateUtil.parse(dateCalService.calculationTradingDayForBefore
						   (DateUtil.format(vdate), observeBefDay, holidayList, DictCodeList.是否_是.getValue())));
			   }

			   if(StringUtil.equals(observeBefUnit, DictCodeList.提前观察单位_延后时间单位_自然日.getValue())) {
				   // 利率确认日期
				   finCashResetPlan.setConfirmDate(DateUtil.addDay(mdate, observeBefDay*-1));
			   }
			   // 自然日日期计算
			   vdate = dateCalService.naturalDateCalc(vdate, cycleUnit, cycle );
			   
			   finCashResetPlanList.add(finCashResetPlan);
		   }while(DateUtil.compareDate(vdate, mdate) < 0);
	   }
	   return finCashResetPlanList;
	}

   // 资产类型改为授权类型 add by renping 2019-08-19  start
   /**
    * 汇率查询
    *
    * @param bookDate 日期
    * @param basicCny 基础币种
    * @param offreCny 报价币种
    * @return MstExchargeQuote
    * @see
    * @since 1.0
    */
   public MstExchargeQuote getCurrencyMarket(Date bookDate, String basicCny,
                                                    String offreCny) {
       // 返回值
       MstExchargeQuote mstExchargeQuote = new MstExchargeQuote();

       if (basicCny.equals(offreCny)) {
           mstExchargeQuote.setClosePrice(new BigDecimal(1));
           mstExchargeQuote.setTradeDate(null);
           return mstExchargeQuote;
       }
       // 查询汇率行情信息
       Map<String, Object> map = new HashMap<String, Object>();
       map.put("basicCny", basicCny);
       map.put("offreCny", offreCny);
//       map.put("valuation", valuationSource);
       map.put("serchDate", bookDate);
       mstExchargeQuote = commonBusinessMapper.getExchangeRate(map);
       if(mstExchargeQuote == null) {
           return null;
       }

       // 出参.汇率 = （变量.计算价格/查询汇率行情.开盘价） 按照变量.小数位数以及尾数处理方式处理小数位。
        BigDecimal midPrice = ArithmeticUtil.multiply(mstExchargeQuote.getMidPrice(),
                new BigDecimal(mstExchargeQuote.getQuotUnit()));
        BigDecimal closePrice = ArithmeticUtil.multiply(mstExchargeQuote.getClosePrice(),
                new BigDecimal(mstExchargeQuote.getQuotUnit()));

       // 出参.汇率日期 = 查询汇率行情.交易日
       mstExchargeQuote.setMidPrice(midPrice);
       mstExchargeQuote.setClosePrice(closePrice);
       return mstExchargeQuote;
   }
   

    /**
     * 汇率换算
     *
     * @param inputAmt
     *            输入金额
     * @param bookDate
     *            日期
     * @param basicCny
     *            基础币种
     * @param offreCny
     *            报价币种
     * @return 输出金额
     * @see
     * @since 1.0
     */
    public BigDecimal calExchangeAmt(BigDecimal inputAmt, Date bookDate, String basicCny,
            String offreCny) {
        MstExchargeQuote mstExchargeQuote = this.getCurrencyMarket(bookDate, basicCny, offreCny);
        BigDecimal outputAmt = null;
        if (mstExchargeQuote != null) {
            ArithmeticUtil.multiply(inputAmt, mstExchargeQuote.getMidPrice());
        }
        return outputAmt;
    }
    // 资产类型改为授权类型 add by renping 2019-08-19  end
    
    //外汇远期、掉期交易日期联动共通 add by  gaopeng 1019-10-17 start
    /**
     * 获取对应期限
     */
    public String getTerm(String termType) {
    	String term="";
    	if(DictCodeListExt2.外汇期限_1D.getValue().equals(termType)) {
    		term ="1";
    	}else if(DictCodeListExt2.外汇期限_1W.getValue().equals(termType)) {
    		term ="7";
    	}else if(DictCodeListExt2.外汇期限_2W.getValue().equals(termType)) {
    		term ="14";
    	}else if(DictCodeListExt2.外汇期限_3W.getValue().equals(termType)) {
    		term ="21";
    	}else if(DictCodeListExt2.外汇期限_1M.getValue().equals(termType)) {
    		term ="30";
    	}else if(DictCodeListExt2.外汇期限_2M.getValue().equals(termType)) {
    		term ="60";
    	}else if(DictCodeListExt2.外汇期限_3M.getValue().equals(termType)) {
    		term ="90";
    	}else if(DictCodeListExt2.外汇期限_4M.getValue().equals(termType)) {
    		term ="120";
    	}else if(DictCodeListExt2.外汇期限_5M.getValue().equals(termType)) {
    		term ="150";
    	}else if(DictCodeListExt2.外汇期限_6M.getValue().equals(termType)) {
    		term ="180";
    	}else if(DictCodeListExt2.外汇期限_9M.getValue().equals(termType)) {
    		term ="270";
    	}else if(DictCodeListExt2.外汇期限_1Y.getValue().equals(termType)) {
    		term ="365";
    	}else if(DictCodeListExt2.外汇期限_18M.getValue().equals(termType)) {
    		term ="540";
    	}else if(DictCodeListExt2.外汇期限_2Y.getValue().equals(termType)) {
    		term ="730";
    	}else if(DictCodeListExt2.外汇期限_3Y.getValue().equals(termType)) {
    		term ="1068";
    	}
		return term;
    }
    /**
     * 获取当月最后一个工作日
     *
     * @param parameters 画面参数
     * @return 下一个工作日
     * @throws Exception
     * @see
     * @since 1.0
     */
    public String getNextLastWorkDay(Map<String, Object> parameters)
        throws Exception {
        if (dateCalService != null) {
            String lastDay = (String)parameters.get("lastDay");
            Date lastday = DateUtil.parse(lastDay);
            List<String> holiday = dateCalService.getDealCalendarHoliday(DateUtil.addDays(lastday, -31), lastday,
                (String)parameters.get("calendarId"));
            if(!holiday.contains(lastDay)) {
            	String tradingDay = lastDay;
            	return tradingDay;
            }else {
            	 String tradingDay = dateCalService.calculationTradingDayForBefore(lastDay, 1, holiday, "2");
            	 return tradingDay;
            }
        }
        return null;
    }
    //外汇远期、掉期交易日期联动共通 add by  gaopeng 1019-10-17 end
    /**
    *
    * 参数共通
    * <功能详细描述>
    * @param counterId  交易对手
    * @param prinAmt 金额
    * @param userName 用户名
    * @param assetType 资产类型
    * @param assetSecType 资产一级分类
    * @param tradeMarket 交易场所
    * @param tradeDirection 交易方向
    * @param assetId 资产代码
    * @param workflowId 工作流ID
    * @return
    * @see
    * @since 1.0
    */
   public static Map<String, Object> setParams(String counterId, BigDecimal prinAmt, String userName,
                                               String assetType, String assetSecType, String tradeMarket, String tradeDirection,
                                               String assetId,String workflowId){
       Map<String, Object> result = new HashMap<>();
       result.put("counterId", counterId);
       result.put("prinAmt", prinAmt);
       result.put("userName", userName);
       result.put("assetType", assetType);
       result.put("assetSecType", assetSecType);
       result.put("tradeMarket", tradeMarket);
       result.put("tradeDirection", tradeDirection);
       result.put("assetId", assetId);
       result.put("workflowId", workflowId);
       return result;
   };




    /**
     * 资产是否存在交易
     *
     * @param finprodId 金融产品代码
     * @return 交易笔数
     * @see
     * @since 1.0
     */
    public int isBondExerciseAsset(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/isBondExerciseAsset?finprodId=" + finprodId,
                Integer.class);
    }

	/*组合模拟试算功能合并 panbin 2020-8-18 start*/
    public String findInvAim(Map<String, Object> pageParam) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/common/entrustcommonbase/findInvAim/" + pageParam.get("portfolioId") + "/" + pageParam.get("finprodId"),
                String.class);
    }
    /*组合模拟试算功能合并 panbin 2020-8-18 end*/

    public BigDecimal selectFaceValue(Map<String, Object> params) {
        BigDecimal result = commonBusinessMapper.selectFaceValue(params);
        return result == null ? new BigDecimal(100) : result;
    }

    /**
     * 按规则生成回购期限品种
     */
    public  String getRepTermType(String finprodType, long termDay) {
        String termType = null;
        if (DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(finprodType)
                || DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue().equals(finprodType)
                || DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(finprodType)
                || DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(finprodType)) {
            termType = DictCodeList.期限品种_回购_未知.getValue();
            if (termDay == 1) {
                termType = DictCodeList.期限品种_回购_1天.getValue();
            }
            else if (termDay > 1 && termDay <= 7) {
                termType = DictCodeList.期限品种_回购_7天.getValue();
            }
            else if (termDay > 7 && termDay <= 14) {
                termType = DictCodeList.期限品种_回购_14天.getValue();
            }
            else if (termDay > 14 && termDay <= 21) {
                termType = DictCodeList.期限品种_回购_21天.getValue();
            }
            else if (termDay > 21 && termDay <= 30) {
                termType = DictCodeList.期限品种_回购_1个月.getValue();
            }
            else if (termDay > 30 && termDay <= 60) {
                termType = DictCodeList.期限品种_回购_2个月.getValue();
            }
            else if (termDay > 60 && termDay <= 91) {
                termType = DictCodeList.期限品种_回购_3个月.getValue();
            }
            else if (termDay > 91 && termDay <= 121) {
                termType = DictCodeList.期限品种_回购_4个月.getValue();
            }
            else if (termDay > 121 && termDay <= 182) {
                termType = DictCodeList.期限品种_回购_6个月.getValue();
            }
            else if (termDay > 182 && termDay <= 273) {
                termType = DictCodeList.期限品种_回购_9个月.getValue();
            }
            else if (termDay > 273 && termDay <= 365) {
                termType = DictCodeList.期限品种_回购_1年.getValue();
            }
            else {
                termType = DictCodeList.期限品种_回购_未知.getValue();
            }
        }
        else if (DictCodeListExt1.金融产品类型_拆入.getValue().equals(finprodType)
                || DictCodeListExt1.金融产品类型_拆出.getValue().equals(finprodType)
                || DictCodeListExt1.金融产品类型_同业借款.getValue().equals(finprodType)) {
            if (termDay == 1) {
                termType = DictCodeList.期限品种_内部拆借_隔夜.getValue();
            }
            else if (termDay > 1 && termDay <= 7) {
                termType = DictCodeList.期限品种_内部拆借_7天.getValue();
            }
            else if (termDay > 7 && termDay <= 14) {
                termType = DictCodeList.期限品种_内部拆借_14天.getValue();
            }
            else if (termDay > 14 && termDay <= 21) {
                termType = DictCodeList.期限品种_内部拆借_21天.getValue();
            }
            else if (termDay > 21 && termDay <= 30) {
                termType = DictCodeList.期限品种_内部拆借_1个月.getValue();
            }
            else if (termDay > 30 && termDay <= 60) {
                termType = DictCodeList.期限品种_内部拆借_2个月.getValue();
            }
            else if (termDay > 60 && termDay <= 91) {
                termType = DictCodeList.期限品种_内部拆借_3个月.getValue();
            }
            else if (termDay > 91 && termDay <= 182) {
                termType = DictCodeList.期限品种_内部拆借_6个月.getValue();
            }
            else if (termDay > 182 && termDay <= 273) {
                termType = DictCodeList.期限品种_内部拆借_9个月.getValue();
            }
            else if (termDay > 273 && termDay <= 365) {
                termType = DictCodeList.期限品种_内部拆借_12个月.getValue();
            }
            else {
                termType = DictCodeList.期限品种_内部拆借_1年以上.getValue();
            }
        }

        return termType;
    }

	   /**
    * 根据产品代码查应占用授信的机构    
    * 
    * @param finprodId
    * @return
    */
   public String selectCustomer(Map<String, Object> map) {
	   map.put("fzq", DictCodeList.平安资产类型_债券.getValue());
	   map.put("fck", DictCodeList.平安资产类型_存款.getValue());
	   map.put("fxml", DictCodeList.平安资产类型_项目类资产.getValue());
	   return commonBusinessMapper.selectCustomer(map);
   }


   @DataSource(DataSourceType.OTHER)
   public boolean isTraDirector(Map<String,Object> param){
       return commonBusinessMapper.isTraDirector(param)>0?true:false;
   }

   /**
     * 资产是否存在交易(存在撤销交易也算交易)
     *
     * @param finprodId 金融产品代码
     * @return 交易笔数
     * @see
     * @since 1.0
     */
    public int isTransInAssetTwo(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/isTransInAssetTwo?finprodId=" + finprodId,
                Integer.class);
    }
}
