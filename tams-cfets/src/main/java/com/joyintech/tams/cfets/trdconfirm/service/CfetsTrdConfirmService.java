/*
 * 文件名：CfetsTrdConfirmService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：sjj
 * 修改时间：2021年11月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trdconfirm.service;

import com.google.common.collect.Lists;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.bankuplink.common.dto.TrdBondInvestTradeDto;
import com.joyintech.service.bankuplink.common.util.ProdTypeUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.BigDecimalUtil;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trdconfirm.dao.CfetsTrdConfirmMapper;
import com.joyintech.tams.cfets.trdconfirm.dto.*;
import com.joyintech.tams.cfets.trdquery.dao.CfetsTrdQueryMapper;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.component.business.trade.impl.BaseTradeProductDealCommServiceImpl;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.trade.lend.dto.FinTradeProductDto;
import com.joyintech.tams.position.servive.PositionRequestHandle;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 银行间交易成交确认
 *
 * @author
 * @version 1.0 2021年10月26日
 * @see CfetsTrdConfirmService
 * @since 1.0
 */
@Service("CfetsTrdConfirmService_dtx")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CfetsTrdConfirmService extends BaseService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(CfetsTrdConfirmService.class);

    @Autowired
    private CfetsTrdConfirm001Service cfetsTrdConfirm001Service;
    @Autowired
    private CfetsTrdConfirmMapper cfetsTrdConfirmMapper;

    @Autowired(required = false)
    private FinProductMapper finProductMapper;

    @Autowired(required = false)
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;

    @Autowired(required = false)
    private BaseTradeProductDealCommServiceImpl baseTradeProductDealCommServiceImpl;

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    @Autowired
    private CfetsTrdQueryMapper cfetsTrdQueryMapper;

    /**
     * 库表生成的cstp交易处理
     */
    @Autowired
    private InterfCstpTradeMapper interfCstpTradeMapper;

    /**
     * 库表生成的债券交易处理
     */
    @Autowired
    private InterfTradSecdealMapper interfTradSecdealMapper;

    /**
     * 库表生成的回购交易处理
     */
    @Autowired
    private InterfOpicsTradRepoMapper interfOpicsTradRepoMapper;

    /**
     * 库表生成的一级申购处理
     */
    @Autowired
    private InterfCstpBjfxjgMapper interfCstpBjfxjgMapper;

    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;


    /**
     * 公共方法service
     */
    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    /**
     * 头寸引擎
     */
    @Autowired
    private PositionRequestHandle positionRequestHandle;

    @Autowired
    private MatchFeaturesService matchFeaturesService;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    // 交易份额小数保留位数
    private final int decimalPlaces_4 = 4;
    String PAGE_FROM_TrdRepoTrade003 = "TrdRepoTrade003";

    /**
     * 交易登记列表分页查询
     *
     * @param dto 查询条件
     * @return Page
     * @see
     * @since 1.0
     */
    public Page<CfetsTrdConfirmRegInfoListResponse> findAllRegInfoList(CfetsTrdConfirmRegInfoListRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        String portfolioId = (String) pageParam.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            pageParam.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        String finprodId = (String) pageParam.get("finprodId");
        if (StringUtil.isNotEmpty(finprodId)) {
            pageParam.put("finprodIdList", StringUtil.splitToList(GlobalConstant.COMMA, finprodId));
        }
        // 资产类型
        String assetType = (String) pageParam.get("finprodType2");
        pageParam.put("assetType", assetType);
        // 业务类型
        String regAssetType = (String) pageParam.get("regAssetType");
        if (StringUtil.isNotEmpty(regAssetType)) {
            List<String> finprodType2List = new ArrayList<>();
            if (StringUtil.equals(regAssetType, "01")) {
                finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            } else if (StringUtil.equals(regAssetType, "02")) {
                finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            } else if (StringUtil.equals(regAssetType, "03")) {
                finprodType2List.add(regAssetType);
            }
            pageParam.put("finprodType2List", finprodType2List);
        }
        String secAcctId = (String) pageParam.get("secAcctId");
        if (StringUtil.isNotEmpty(secAcctId)) {
            pageParam.put("secAcctIdList", StringUtil.splitToList(GlobalConstant.COMMA, secAcctId));
        }
        String counterId = (String) pageParam.get("counterId");
        if (StringUtil.isNotEmpty(counterId)) {
            pageParam.put("counterIdList", StringUtil.splitToList(GlobalConstant.COMMA, counterId));
        }
        pageParam.put("ISSUE_STATUS_01", DictCodeListExt1.发行状态_发行失败.getValue());
      /*  if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
            pageParam.put("settleSpeed","0");
        }
        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
            pageParam.put("settleSpeed","1");
        }*/
        pageParam.put("sysDate", iPlatformDate.getSysDate());
        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.parse(DateUtil.getSysDate()));
        int year = c.get(Calendar.YEAR);
        pageParam.put("year", year);
        pageParam.put("sort", dto.getPageMode().getSort());
        pageParam.put("direction", dto.getPageMode().getDirection());
        String userId = SecurityUtil.getLoginUser().getUserName();
        //判断是否是交易员
        if (StringUtils.isNotEmpty(userId)) {
            //判断角色配置
            List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
            if (roleList.contains(DictCodeList.角色岗位_交易员.getValue())) {
                //交易员
                pageParam.put("userId", userId);
            }
        }
        //根据有无下行查询不同
        String support_cstp_flag_bond = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BOND");
        String support_cstp_flag_repo = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_REPO");
        if (StringUtil.equals(support_cstp_flag_bond, DictCodeList.是否_是.getValue())
                || StringUtil.equals(support_cstp_flag_repo, DictCodeList.是否_是.getValue())
        ) {
            //针对查询可匹配执行交易
            if (!StringUtil.isEmptyList(dto.getSelectedItem())) {
                CfetsTrdConfirmCstpInfoListResponse serchExecDto = dto.getSelectedItem().get(0);
                pageParam.put("isSearchMatch", DictCodeList.是否_是.getValue());
                //针对报价
                pageParam.put("execid", serchExecDto.getExecid());
                Map<String, MatchDetFeatures> map = matchFeaturesService.queryManualMatchFeatures(serchExecDto.getBusinesstype());
                if (StringUtil.equals(serchExecDto.getBusinesstype(), "BOND")) {
                    //基础要素：成交日期、资产代码、交易场所、交易方向、清算速度、证券账户、成交价（净价/到期收益率/行权收益率）、券面总额、结算日期、结算方式、结算金额、对手方
                    if (map.get("tradeDate") != null && StringUtil.equals(map.get("tradeDate").getType(), "01")) {
                        // 成交日期
                        pageParam.put("tradeDate", serchExecDto.getTradeDate());
                    }
                    if (map.get("finprodMarketId") != null && StringUtil.equals(map.get("finprodMarketId").getType(), "01")) {
                        // 资产代码
                        pageParam.put("finprodMarketId", serchExecDto.getFinprodMarketId());
                    }
                    if (map.get("ps") != null && StringUtil.equals(map.get("ps").getType(), "01")) {
                        // 交易方向
                        pageParam.put("ps", serchExecDto.getPs());
                    }
                    if (map.get("settleSpeed") != null && StringUtil.equals(map.get("settleSpeed").getType(), "01")) {
                        // 清算速度
                        pageParam.put("settleSpeed", serchExecDto.getSettleSpeed());
                    }
                    if (map.get("secManageAcctId") != null && StringUtil.equals(map.get("secManageAcctId").getType(), "01")) {
                        // 证券账户
                        pageParam.put("secManageAcctId", serchExecDto.getAcctNo());
                    }
                    if (map.get("yield") != null && StringUtil.equals(map.get("yield").getType(), "01")) {
                        // 成交价（净价/到期收益率/行权收益率）
                        pageParam.put("yield", serchExecDto.getYield());
                        pageParam.put("unitCprice", serchExecDto.getUnitCprice());
                    }
                    if (map.get("faceValue") != null && StringUtil.equals(map.get("faceValue").getType(), "01")) {
                        // 券面总额
                        pageParam.put("faceValue", serchExecDto.getLastqty());
                    }
                    if (map.get("settleDate") != null && StringUtil.equals(map.get("settleDate").getType(), "01")) {
                        // 结算日期
                        pageParam.put("settleDate", serchExecDto.getSettleDate());
                    }
                    if (map.get("deliveryType") != null && StringUtil.equals(map.get("deliveryType").getType(), "01")) {
                        // 结算方式
                        pageParam.put("deliveryType", serchExecDto.getDeliveryType());
                    }
                    if (map.get("settleAtm") != null && StringUtil.equals(map.get("settleAtm").getType(), "01")) {
                        // 结算金额
                        pageParam.put("tradeAmt", serchExecDto.getTradeAmt());
                    }
                    if (map.get("counterId") != null && StringUtil.equals(map.get("counterId").getType(), "01")) {
                        // 对手方
                        pageParam.put("counterId", serchExecDto.getCounterId());
                    }

                } else if (StringUtil.equals(serchExecDto.getBusinesstype(), "REPO")) {
                    //基础要素：成交日期、回购期限、交易场所、交易方向、清算速度、证券账户、回购利率、首次结算日、到期结算日、
                    // 首次结算金额、质押券信息（债券代码、折算比例、券面总额）
                    if (map.get("tradeDate") != null && StringUtil.equals(map.get("tradeDate").getType(), "01")) {
                        // 资产代码
                        pageParam.put("tradeDate", serchExecDto.getTradeDate());
                    }
                    if (map.get("term") != null && StringUtil.equals(map.get("term").getType(), "01")) {
                        // 资产代码
                        pageParam.put("term", serchExecDto.getTermDays());
                    }
                    if (map.get("ps") != null && StringUtil.equals(map.get("ps").getType(), "01")) {
                        // 资产代码
                        pageParam.put("ps", serchExecDto.getPs());
                    }
                    if (map.get("settleSpeed") != null && StringUtil.equals(map.get("settleSpeed").getType(), "01")) {
                        // 资产代码
                        pageParam.put("settleSpeed", serchExecDto.getSettleSpeed());
                    }
                    if (map.get("secManageAcctId") != null && StringUtil.equals(map.get("secManageAcctId").getType(), "01")) {
                        // 资产代码
                        pageParam.put("secManageAcctId", serchExecDto.getAcctNo());
                    }
                    if (map.get("rate") != null && StringUtil.equals(map.get("rate").getType(), "01")) {
                        // 回购利率
                        pageParam.put("rate", serchExecDto.getTradeYield());
                    }
                    if (map.get("vsettleDate") != null && StringUtil.equals(map.get("vsettleDate").getType(), "01")) {
                        // 资产代码
                        pageParam.put("vsettleDate", serchExecDto.getVsettleDate());
                    }
                    if (map.get("msettleDate") != null && StringUtil.equals(map.get("msettleDate").getType(), "01")) {
                        // 资产代码
                        pageParam.put("msettleDate", serchExecDto.getMsettleDate());
                    }
                    if (map.get("tradeAmt") != null && StringUtil.equals(map.get("tradeAmt").getType(), "01")) {
                        // 资产代码
                        pageParam.put("tradeAmt", serchExecDto.getTradeAmt());
                    }
                    if (map.get("mTradeAmt") != null && StringUtil.equals(map.get("mTradeAmt").getType(), "01")) {
                        //到期结算金额
                        pageParam.put("mTradeAmt", serchExecDto.getMtradeAmt());
                    }
                    if (map.get("counterId") != null && StringUtil.equals(map.get("counterId").getType(), "01")) {
                        //对手方
                        pageParam.put("counterId", serchExecDto.getCounterId());
                    }
                    if (map.get("pledgeFinprodId") != null && StringUtil.equals(map.get("pledgeFinprodId").getType(), "01")) {
                        // 质押债券代码
                        pageParam.put("pledgeFinprodIds", serchExecDto.getPledgeFinprodIds());
                    }
                    if (map.get("pledgeRatio") != null && StringUtil.equals(map.get("pledgeRatio").getType(), "01")) {
                        // 质押债券折算比例
                        pageParam.put("pledgeRatios", serchExecDto.getPledgeRatios());
                    }
                    if (map.get("pledgeFaceValue") != null && StringUtil.equals(map.get("pledgeFaceValue").getType(), "01")) {
                        // 质押债券券面总额
                        pageParam.put("pledgeFaceValues", serchExecDto.getPledgeFaceValues());
                    }
                } else if (StringUtil.equals(serchExecDto.getBusinesstype(), "BID")) {
                    // 基础要素：成交日期、资产代码、交易场所、本方、认购量、成交价
//                    pageParam.put("rate", serchExecDto.getTradeYield());
                    pageParam.put("BID", serchExecDto.getBusinesstype());
                    if (map.get("tradeDate") != null && StringUtil.equals(map.get("tradeDate").getType(), "01")) {
                        // 成交日期
                        pageParam.put("tradeDate", serchExecDto.getTradeDate());
                    }
                    if (map.get("finprodMarketId") != null && StringUtil.equals(map.get("finprodMarketId").getType(), "01")) {
                        // 资产代码
                        pageParam.put("finprodIdList", Collections.singletonList(serchExecDto.getFinprodId()));
                    }
                    if (map.get("ouPartyId") != null && StringUtil.equals(map.get("ouPartyId").getType(), "01")) {
                        // 本方
                        pageParam.put("counterIdList", Collections.singletonList(serchExecDto.getTraderid()));//本方交易员
                    }
                    if (map.get("subscriptionAmount") != null && StringUtil.equals(map.get("subscriptionAmount").getType(), "01")) {
                        // 认购量
                        pageParam.put("subscriptionAmount", ArithmeticUtil.multiply(serchExecDto.getLastqty(), BigDecimal.valueOf(10000)));
                    }
                    if (map.get("issuePrice") != null && StringUtil.equals(map.get("issuePrice").getType(), "01")) {
                        // 发行价格(元)
                        pageParam.put("issuePrice", serchExecDto.getOtherAmt());
                    }
                    if (map.get("ourTrader") != null && StringUtil.equals(map.get("ourTrader").getType(), "01")) {
                        // 一级申购 可选要素：本方交易员、应缴款金额
                        pageParam.put("ourTrader", serchExecDto.getTrader());//本方交易员
                    }
                    if (map.get("payableAmount") != null && StringUtil.equals(map.get("payableAmount").getType(), "01")) {
                        //应缴款金额
                        pageParam.put("payableAmount", serchExecDto.getTradeAmt());
                    }
                }
            }
            //登记以及报价
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdConfirmMapper.findAllRegAndIbuInfoList(pageParam));
        } else {
            //登记
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdConfirmMapper.findAllRegInfoList(pageParam));
        }
    }

    /**
     * 交易执行-成交确认
     */
    public AjaxResult trdConfirmSaveMethod(CfetsTrdConfirmRegInfoListResponse dto) {
        List<CfetsTrdConfirmRegInfoListResponse> selectedItem = dto.getSelectedItem();
        List<ValidMessage> messages = new ArrayList<>();
        String totleTradeId = null;
        try {
            if (StringUtil.isNotEmpty(selectedItem)) {
                for (CfetsTrdConfirmRegInfoListResponse item : selectedItem) {
                    totleTradeId = item.getTotleTradeId();
                    if (StringUtil.equals(item.getFinprodType2(), DictCodeListExt1.登记资产类型_债券.getValue())
                            || StringUtil.equals(item.getFinprodType2(), DictCodeListExt1.CSTP资产类型_债券.getValue())
                    ) {
                        // 债券买卖 对dto进行封装
                        CfetsTrdBondInvestTradeDto entity = this.packageEquity(item, DictCodeListExt1.生成方式_画面录入.getValue(), dto.getCstpTradeId());
                        // 成交确认提交方法
                        cfetsTrdConfirm001Service.doCompleteMethod(entity, OperationType.NEW);
                    } else if (StringUtil.equals(item.getFinprodType2(), DictCodeListExt1.登记资产类型_一级申购.getValue())
                            || StringUtil.equals(item.getFinprodType2(), DictCodeListExt1.CSTP资产类型_一级申购.getValue())) {
                        // 一级债
                        CfetsTrdFirstBondInvestTradeDto entity = this.prepareBidEntity(item.getTotleTradeId(), DictCodeListExt1.生成方式_画面录入.getValue(), dto.getCstpTradeId());
                        cfetsTrdConfirm001Service.doCompleteBidMethod(entity, OperationType.NEW, DictCodeListExt1.生成方式_画面录入.getValue());
                    } else {
                        CfetsTrdRepoTrade003Dto entity = this.packageRepoEquity(item, DictCodeListExt1.生成方式_画面录入.getValue(), dto.getCstpTradeId());
                        //成交确认提交方法
                        cfetsTrdConfirm001Service.doCompleteRepoMethod(entity, OperationType.NEW, true);
                    }
                }
            }
        } catch (Exception e) {
            //若插入数据异常，提示错误信息
            Message ms = PositionUtil.ajaxErrorReturn(e);
            if (null != ms && StringUtil.isNotEmpty(ms.getMessage())) {
                messages.add(new ValidMessage(getMessage("CONFIRM_MSG_009", totleTradeId, DictCodeListExt3.操作类型_成交确认.getCodeName(), ms.getMessage())));
            } else {
                messages.add(new ValidMessage(getMessage("CONFIRM_MSG_008", totleTradeId, DictCodeListExt3.操作类型_成交确认.getCodeName())));
            }
            e.printStackTrace();
        }
        if (!StringUtil.isEmptyList(messages) && messages.size() > 0) {
            AjaxResult res = new AjaxResult();
            res.setErrorList(messages);
            return res;
        }
        return null;
    }

    /**
     * 交易执行-发行失败
     *
     * @param dto 页面元素
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/5/7 19:35
     */
    public AjaxResult issuingFailMethod(CfetsTrdConfirmRegInfoListResponse dto) {
        logger.info("发行失败,入参:" + JsonUtils.object2Json(dto));
        List<CfetsTrdConfirmRegInfoListResponse> selectedItem = dto.getSelectedItem();
        List<ValidMessage> messages = new ArrayList<>();
        List<String> finprodIds = new ArrayList<>();
        if (StringUtil.isNotEmpty(selectedItem)) {
            selectedItem.forEach(item -> {
                if (!finprodIds.contains(item.getFinprodId())) {
                    finprodIds.add(item.getFinprodId());
                }
            });
        }
        cfetsTrdConfirm001Service.issuingFailMethod(finprodIds);
        if (!StringUtil.isEmptyList(messages) && messages.size() > 0) {
            AjaxResult res = new AjaxResult();
            res.setErrorList(messages);
            return res;
        }
        return null;
    }

    /**
     * 保存提交时，后台数据转换
     *
     * @param dto
     * @return CfetsTrdBondInvestTradeDto
     */
    public CfetsTrdBondInvestTradeDto packageEquity(CfetsTrdConfirmRegInfoListResponse dto, String genType) {
        if (StringUtil.isNotEmpty(dto.getDealType())
                && StringUtil.equals(dto.getDealType(), BussinessDictCodeList.交易类型_登记报价.getValue())
        ) {
            return prepareEntity(dto.getTotleTradeId(), genType);
        } else {
            //报价数据
            return prepareIbuEntity(dto.getTotleTradeId(), genType);
        }
    }

    /**
     * 债券匹配时组装成交数据,部分值需取下行数据的值,并重新计算明细数据
     *
     * @param dto         待成交数据
     * @param genType     生成方式
     * @param cstpTradeId 下行id
     * @return com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdBondInvestTradeDto
     * @author jiangpeng
     * @date 2022/9/19 10:14
     */
    public CfetsTrdBondInvestTradeDto packageEquity(CfetsTrdConfirmRegInfoListResponse dto, String genType,
                                                    String cstpTradeId) {
        logger.info("packageEquity -> param dto:{} genType:{} cstpTradeId:{}",JsonUtils.object2Json(dto),genType,cstpTradeId);
        CfetsTrdBondInvestTradeDto result = this.packageEquity(dto, genType);
        InterfTradSecdeal interfTradSecdeal = null;
        if (StringUtil.isNotEmpty(cstpTradeId)) {
            interfTradSecdeal = interfTradSecdealMapper.selectByPrimaryKey(cstpTradeId);
        } else if (StringUtil.isNotEmpty(dto.getExecid())) {
            logger.info("cstpTradeId is null");
            interfTradSecdeal = interfTradSecdealMapper.selectByFedealno(dto.getExecid());
        } else {
            logger.error("execid is null and cstpTradeId is null");
        }
        if (interfTradSecdeal == null || StringUtil.isEmpty(interfTradSecdeal.getFedealno())) {
            logger.error("债券下行数据为空");
            return result;
        }
        logger.info("债券下行数据:{}", JsonUtils.object2Json(interfTradSecdeal));
        // modify by jiangpeng 20221020 GLYHSCWT-143 考虑转仓时两条数据成交编号相同的情况过通过交易方向过滤 start
        String side = StringUtil.equals(interfTradSecdeal.getPs(), DictCodeList.交易方向_买入.getValue()) ? "1" : "4";
        // modify by jiangpeng 20221020 GLYHSCWT-143 考虑转仓时两条数据成交编号相同的情况过通过交易方向过滤 end
        InterfCstpTrade interfCstpTrade = interfCstpTradeMapper.selectByPrimaryKey(interfTradSecdeal.getFedealno(), side);
        if (interfCstpTrade == null) {
            logger.error("下行接口数据为空");
            return result;
        }
        logger.info("下行接口数据:{}", JsonUtils.object2Json(interfCstpTrade));
//        if (BigDecimalUtil.ACompareToB(interfCstpTrade.getSettlcurramt(), result.getFpriceAmt(), 4) == 0) {
//            logger.info("结算金额和下行数据相同,不做后续操作:{}", JsonUtils.object2Json(result));
//            return result;
//        }
        // 交易金额
        BigDecimal cPriceAmt = interfCstpTrade.getTradecashamt();
        result.setPrinAmt(interfCstpTrade.getTradecashamt());
        result.setCpriceAmt(interfCstpTrade.getTradecashamt());
        // 结算金额
        BigDecimal fpriceAmt = interfCstpTrade.getSettlcurramt();
        result.setFpriceAmt(interfCstpTrade.getSettlcurramt());
        result.setTradeAmt(interfCstpTrade.getSettlcurramt());
        // 应计利息总额
        BigDecimal intAmt = interfCstpTrade.getAccruedinteresttotalamt();
        result.setIntAmt(interfCstpTrade.getAccruedinteresttotalamt());
        // 券面总额
        BigDecimal shareAmt = result.getShareAmt();
        // 净价
        BigDecimal price = interfCstpTrade.getPrice();
        result.setUnitCprice(price);
        // 全价
        BigDecimal fPrice = interfCstpTrade.getDirtyprice();
        result.setUnitFprice(fPrice);
        // 到期收益率
        BigDecimal yield = interfCstpTrade.getYield2();
        result.setYield(yield);
        // 行权收益率
        BigDecimal strikeYield = interfCstpTrade.getStrikeyield();
        result.setExerYield(strikeYield);
        // 单位应计利息
        BigDecimal unitInt = interfCstpTrade.getAccruedinterestamt();
        result.setUnitInt(unitInt);
        // 债券面值
        BigDecimal faceValue = trdCommonBaseService.getFaceValue(dto.getFinprodId(), iPlatformDate.getSysDate());
        // 分配结算金额和交易金额
        for (int i = 0; i < result.getTrdProductDeals().size(); i++) {
            if(StringUtil.isNotEmpty(result.getTrdProductDeals().get(i).getTradeAmt())) {
                result.getTrdProductDeals().get(i).setOldTradeAmt(result.getTrdProductDeals().get(i).getTradeAmt());
            }else{
                result.getTrdProductDeals().get(i).setOldTradeAmt(ArithmeticUtil.multiply(faceValue, result.getTrdProductDeals().get(i).getShareAmt()));
            }
            // 净价
            result.getTrdProductDeals().get(i).setUnitCprice(price);
            // 全价
            result.getTrdProductDeals().get(i).setUnitFprice(fPrice);
            // 到期收益率
            result.getTrdProductDeals().get(i).setYield(yield);
            // 行权收益率
            result.getTrdProductDeals().get(i).setExerYield(strikeYield);
            // 单位应计利息
            result.getTrdProductDeals().get(i).setUnitInt(unitInt);
            result.getTrdProductDeals().get(i).setOldFpriceAmt(result.getTrdProductDeals().get(i).getFpriceAmt());
            result.getTrdProductDeals().get(i).setOldIntAmt(result.getTrdProductDeals().get(i).getIntAmt());
            // 最后一笔数据倒减
            if (i == result.getTrdProductDeals().size() - 1) {
                result.getTrdProductDeals().get(i).setCpriceAmt(cPriceAmt);
                result.getTrdProductDeals().get(i).setPrinAmt(cPriceAmt);
                result.getTrdProductDeals().get(i).setFpriceAmt(fpriceAmt);
                result.getTrdProductDeals().get(i).setTradeAmt(fpriceAmt);
                result.getTrdProductDeals().get(i).setIntAmt(intAmt);
            } else {
                BigDecimal shareAmtTemp = result.getTrdProductDeals().get(i).getShareAmt();
                // 明细券面总额/总券面总额 * 交易金额
                result.getTrdProductDeals().get(i).setCpriceAmt(
                        ArithmeticUtil.divide(
                                ArithmeticUtil.multiply(interfCstpTrade.getTradecashamt(), shareAmtTemp), shareAmt, 2));
                result.getTrdProductDeals().get(i).setPrinAmt(result.getTrdProductDeals().get(i).getCpriceAmt());
                // 明细券面总额/总券面总额 * 结算金额
                result.getTrdProductDeals().get(i).setFpriceAmt(
                        ArithmeticUtil.divide(
                                ArithmeticUtil.multiply(interfCstpTrade.getSettlcurramt(), shareAmtTemp), shareAmt, 2));
                result.getTrdProductDeals().get(i).setTradeAmt(result.getTrdProductDeals().get(i).getFpriceAmt());
                // 利息总额 = 结算金额  - 交易金额
                result.getTrdProductDeals().get(i).setIntAmt(
                        ArithmeticUtil.subtract(result.getTrdProductDeals().get(i).getFpriceAmt(), result.getTrdProductDeals().get(i).getCpriceAmt()));
                cPriceAmt = ArithmeticUtil.subtract(cPriceAmt, result.getTrdProductDeals().get(i).getCpriceAmt());
                fpriceAmt = ArithmeticUtil.subtract(fpriceAmt, result.getTrdProductDeals().get(i).getFpriceAmt());
                intAmt = ArithmeticUtil.subtract(intAmt, result.getTrdProductDeals().get(i).getIntAmt());
            }
            for (BussTrdTransferOrderDto item : result.getBussTrdTransferOrder()) {
                if(StringUtil.equals(item.getTradeId(),result.getTrdProductDeals().get(i).getTradeId())) {
                    item.setSettleAmt(result.getTrdProductDeals().get(i).getFpriceAmt());
                    break;
                }
            }
        }
        logger.info("处理后数据:{}", JsonUtils.object2Json(result));
        return result;
    }

    /**
     * 保存提交时，后台数据转换
     *
     * @param dto
     * @return CfetsTrdRepoTrade003Dto
     */
    public CfetsTrdRepoTrade003Dto packageRepoEquity(CfetsTrdConfirmRegInfoListResponse dto, String genType) {
        if (StringUtil.isNotEmpty(dto.getDealType())
                && StringUtil.equals(dto.getDealType(), BussinessDictCodeList.交易类型_登记报价.getValue())
        ) {
            //回购
            return this.prepareRepoEntity(dto.getTotleTradeId(), genType);

        } else {
            //报价数据
            return this.prepareRepoIbuEntity(dto.getTotleTradeId(), genType);
        }
    }

    /**
     * 回购匹配时组装成交数据,部分值需取下行数据的值,并重新计算明细数据
     * <p>回购利率（%）	根据CSTP成交回报数据带出
     * <p>回购利息（元）	根据CSTP成交回报数据带出
     * <p>到期结算金额（元）	根据CSTP成交回报数据带出
     * <p>回购利息（元）	根据组合到期结算金额-交易金额，计算得出
     * <p>到期结算金额（元）	根据组合交易金额占总交易金额比例*总的到期结算金额，计算得出
     *
     * @param dto         待成交数据
     * @param genType     生成方式
     * @param cstpTradeId 下行id
     * @return com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdBondInvestTradeDto
     * @author jiangpeng
     * @date 2022/9/19 10:14
     */
    public CfetsTrdRepoTrade003Dto packageRepoEquity(CfetsTrdConfirmRegInfoListResponse dto, String genType,
                                                     String cstpTradeId) {
        CfetsTrdRepoTrade003Dto result = this.packageRepoEquity(dto, genType);
        if (cstpTradeId == null) {
            logger.error("成交编号为空");
            return result;
        }
        // 回购下行数据
        InterfOpicsTradRepo interfOpicsTradRepo = interfOpicsTradRepoMapper.selectByPrimaryKey(cstpTradeId);
        if (interfOpicsTradRepo == null || StringUtil.isEmpty(interfOpicsTradRepo.getOritradeid())) {
            logger.error("回购下行数据为空");
            return result;
        }
        logger.info("回购下行数据:{}", JsonUtils.object2Json(interfOpicsTradRepo));
        // add by jiangpeng 20221020 GLYHSCWT-143 考虑转仓时两条数据成交编号相同的情况过通过交易方向过滤 start
        String side = StringUtil.equals(interfOpicsTradRepo.getPs(), DictCodeList.回购拆借方向_逆回购.getValue()) ? "1" : "4";
        // add by jiangpeng 20221020 GLYHSCWT-143 考虑转仓时两条数据成交编号相同的情况过通过交易方向过滤 end
        InterfCstpTrade interfCstpTrade = interfCstpTradeMapper.selectByPrimaryKey(interfOpicsTradRepo.getOritradeid(), side);
        if (interfCstpTrade == null) {
            logger.error("下行接口数据为空");
            return result;
        }
        logger.info("下行接口数据:{}", JsonUtils.object2Json(interfCstpTrade));
        // 回购利息（元）	    根据CSTP成交回报数据带出
        // 到期结算金额（元）	根据CSTP成交回报数据带出
        // 回购利息（元）	    根据组合到期结算金额-交易金额，计算得出
        // 到期结算金额（元）	根据组合交易金额占总交易金额比例*总的到期结算金额，计算得出

        // 交易金额
        BigDecimal tradeAmt = interfCstpTrade.getTradecashamt();
        // 倒减的那一笔回购资产代码
        BigDecimal mTradeAmt = interfCstpTrade.getSettlcurramt2();
        // 应计利息总额
        BigDecimal mIntAmt = interfCstpTrade.getAccruedinteresttotalamt();
        // 分配结算金额和交易金额
        for (int i = 0; i < result.getFinTradeProducts().size(); i++) {
            if (i != result.getFinTradeProducts().size() - 1) {
                BigDecimal shareAmtTemp = result.getFinTradeProducts().get(i).getPrin();
                // 明细交易金额/总交易金额 * 总的到期结算金额
                result.getFinTradeProducts().get(i).setmTradeAmt(
                        ArithmeticUtil.divide(
                                ArithmeticUtil.multiply(interfCstpTrade.getSettlcurramt2(), shareAmtTemp), tradeAmt, 2));
                // 根据组合到期结算金额-交易金额
                result.getFinTradeProducts().get(i).setmIntAmt(ArithmeticUtil.subtract(result.getFinTradeProducts().get(i).getmTradeAmt(), shareAmtTemp));
                mTradeAmt = ArithmeticUtil.subtract(mTradeAmt, result.getFinTradeProducts().get(i).getmTradeAmt());
                mIntAmt = ArithmeticUtil.subtract(mIntAmt, result.getFinTradeProducts().get(i).getmIntAmt());
            } else {
                result.getFinTradeProducts().get(i).setmTradeAmt(mTradeAmt);
                result.getFinTradeProducts().get(i).setmIntAmt(mIntAmt);
            }
        }
        // 回购利率（%）	    根据CSTP成交回报数据带出
        result.setRate(interfOpicsTradRepo.getReporate());
        logger.info("处理后数据:{}", JsonUtils.object2Json(result));
        return result;
    }

    /**
     * 一级申购匹配时组装成交数据,部分值需取下行数据的值,并重新计算明细数据
     * <p>发行价格	根据CSTP成交回报数据带出
     * <p>参考收益率/票面利率	根据CSTP成交回报数据带出
     * <p>认购量	根据CSTP成交回报数据带出
     * <p>应缴款金额	根据CSTP成交回报数据带出
     *
     * @param totleTradeId 待成交数据编号
     * @param genType      生成方式
     * @param cstpTradeId  下行id
     * @return com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdBondInvestTradeDto
     * @author jiangpeng
     * @date 2022/9/19 10:14
     */
    public CfetsTrdFirstBondInvestTradeDto prepareBidEntity(String totleTradeId, String genType,
                                                            String cstpTradeId) {
        CfetsTrdFirstBondInvestTradeDto result = this.prepareBidEntity(totleTradeId, genType);
        if (cstpTradeId == null) {
            logger.error("成交编号为空");
            return result;
        }
        // 回购下行数据
        InterfCstpBjfxjg interfCstpBjfxjg = interfCstpBjfxjgMapper.selectByPrimaryKey(cstpTradeId);
        if (interfCstpBjfxjg == null) {
            logger.error("一级申购下行数据为空");
            return result;
        }
        logger.info("一级申购下行数据:{}", JsonUtils.object2Json(interfCstpBjfxjg));
        if (BigDecimalUtil.ACompareToB(interfCstpBjfxjg.getSubscriptionAmount(), result.getShareAmt(), 4) == 0 &&
                BigDecimalUtil.ACompareToB(interfCstpBjfxjg.getActualFee(), result.getTradeAmt(), 4) == 0) {
            logger.info("认购量、应缴款金额 和下行数据相同,不做后续操作:{}", JsonUtils.object2Json(result));
            return result;
        }
        // 应缴款金额
        BigDecimal tradeAmt = interfCstpBjfxjg.getActualFee();
        // 认购量
        BigDecimal subscriptionAmount = ArithmeticUtil.multiply(interfCstpBjfxjg.getSubscriptionAmount(), new BigDecimal(1000000));
        // 倒减的那一笔回购资产代码
        // 分配结算金额和交易金额
        for (int i = 0; i < result.getTrdProductDeals().size(); i++) {
            // 最后一笔数据倒减
            if (i == result.getTrdProductDeals().size() - 1) {
                result.getTrdProductDeals().get(i).setTradeAmt(tradeAmt);
                result.getTrdProductDeals().get(i).setShareAmt(subscriptionAmount);
            } else {
                BigDecimal shareAmtTemp = result.getTrdProductDeals().get(i).getShareAmt();
                // 明细认购量/认购量 * 应缴款金额
                result.getTrdProductDeals().get(i).setTradeAmt(
                        ArithmeticUtil.divide(
                                ArithmeticUtil.multiply(interfCstpBjfxjg.getActualFee(), shareAmtTemp), result.getShareAmt(), 2));
                tradeAmt = ArithmeticUtil.subtract(tradeAmt, result.getTrdProductDeals().get(i).getTradeAmt());
                // 明细认购量/认购量 * 认购量
                result.getTrdProductDeals().get(i).setShareAmt(
                        ArithmeticUtil.divide(
                                ArithmeticUtil.multiply(ArithmeticUtil.multiply(interfCstpBjfxjg.getSubscriptionAmount(), new BigDecimal(1000000)),
                                        shareAmtTemp), result.getShareAmt(), 2));
                subscriptionAmount = ArithmeticUtil.subtract(subscriptionAmount, result.getTrdProductDeals().get(i).getShareAmt());
            }
        }
        result.setTradeAmt(interfCstpBjfxjg.getActualFee());
        // 亿元->转化为数量
        result.setShareAmt(ArithmeticUtil.multiply(interfCstpBjfxjg.getSubscriptionAmount(), new BigDecimal(1000000)));
        logger.info("处理后数据:{}", JsonUtils.object2Json(result));
        return result;
    }

    /**
     * 保存提交时，后台数据转换(一级申购)
     *
     * @param totleTradeId
     * @return CfetsTrdFirstBondInvestTradeDto
     */
    public CfetsTrdFirstBondInvestTradeDto prepareBidEntity(String totleTradeId, String genType) {
        String userName = null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "system";
        //如果是登记数据
        //根据汇总交易编号查询登记数据
        CfetsTrdConfirmRegInfoListResponse dtoReg = cfetsTrdConfirmMapper.selecBidInfoByKey(totleTradeId);
        CfetsTrdFirstBondInvestTradeDto entity = new CfetsTrdFirstBondInvestTradeDto();
        entity.setRegIbuId(totleTradeId);
        BeanUtil.copyPropertiesIgnoreNull(entity, dtoReg);
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = getBussinessCodeParameter(dtoReg.getPs());
            entity.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            entity.setTotleTradeId(IDGenerator.generate());
        }
        // 业务类型
        entity.setBusiType(BussinessDictCodeList.共同业务类型_一级投标确认_申购_登记.getValue());
        // 金融产品类型（估值核算）
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);
        entity.setBranch(branchLong);
        entity.setFinprodType(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        entity.setbTradeId(totleTradeId);
        // 单位面值
//        entity.setParValue(this.getUnitPrice(entity.getFinprodId(), dtoReg.getSettleDate()));
        // 交易本金 = 画面.净价总额
//        entity.setPrinAmt(dtoReg.getCpriceAmt());
        // 券面总额 认购量
        entity.setTotalFaceValue(dtoReg.getFaceValue());
        // 金融产品交易Entity 构造开始----------------------------------------------------------
        // 交易份额 = 画面.认购量/100
        entity.setShareAmt(ArithmeticUtil.divide(dtoReg.getFaceValue(), new BigDecimal(100), decimalPlaces_4, RoundingMode.HALF_UP));
        // 交易金额 = 画面.应缴款金额
        entity.setTradeAmt(dtoReg.getTradeAmt());
//        // 到期收益率 = 画面.到期收益率/100
//        if (dtoReg.getYield() != null) {
//            entity.setYield(dtoReg.getYield());
//        }
//        // 行权收益率 = 画面.行权收益率/100
//        if (dtoReg.getExerYield() != null) {
//            entity.setExerYield(dtoReg.getExerYield());
//        }
        // 是否结清 = 数据字典缓存.是否.是
        entity.setIsClean(DictCodeList.是否_否.getValue());
        entity.setIsCancel(DictCodeList.是否_否.getValue());
        // 交易市场
        entity.setTradeMarketMode(BussinessDictCodeList.交易市场_一级市场.getValue());
        // 是否外部交易
        entity.setIsOutTrade(DictCodeList.是否_是.getValue());
        //本方
        entity.setOuPartyId(dtoReg.getOuPartyId());
        // 本方交易员
        entity.setTrader(dtoReg.getOuTraderId());
        // 交易对手 发行人
        entity.setCounterId(dtoReg.getCounterId());
        // 投管业务类型
        entity.setTgBusiType(DictCodeListExt4.平安业务类型_一级申购.getValue());
        if (StringUtil.equals(dtoReg.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())) {
            entity.setTradePlat(DictCodeList.交易平台_外汇交易中心.getValue());
        } else {
            entity.setTradePlat(DictCodeList.交易平台_其它.getValue());
        }
        // 汇总扩展表
        PaTrdFinProductDealTotal productDealTotal = new PaTrdFinProductDealTotal();
        productDealTotal.setTotleTradeId(entity.getTotleTradeId());
        productDealTotal.setPaBussType(entity.getTgBusiType());
        entity.setPaTrdFinProductDealTotal(productDealTotal);

        // 汇总附属表
        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
        trdDealTotalAdd.setTotleTradeId(entity.getTotleTradeId());
        trdDealTotalAdd.setOurShareAmt(dtoReg.getOurShareAmt());
        trdDealTotalAdd.setLendinShareAmt(dtoReg.getLendinShareAmt());
        trdDealTotalAdd.setOurFpriceAmt(dtoReg.getOurFpriceAmt());
        trdDealTotalAdd.setLendinFpriceAmt(dtoReg.getLendinFpriceAmt() == null ? BigDecimal.ZERO : dtoReg.getLendinFpriceAmt());
        trdDealTotalAdd.setOurCpriceAmt(ArithmeticUtil.subtract(dtoReg.getCpriceAmt(), dtoReg.getLendinCpriceAmt()));
        trdDealTotalAdd.setLendinCpriceAmt(dtoReg.getLendinCpriceAmt() == null ? BigDecimal.ZERO : dtoReg.getLendinCpriceAmt());
        trdDealTotalAdd.setOurFpriceAmt2(dtoReg.getOwnIntAmt());
        trdDealTotalAdd.setLendinFpriceAmt2(dtoReg.getLendinIntAmt() == null ? BigDecimal.ZERO : dtoReg.getLendinIntAmt());
        trdDealTotalAdd.setOuPartyId(dtoReg.getOuPartyId());
        entity.setTrdDealTotalAdd(trdDealTotalAdd);

        // 设置费用信息
        TrdTradeFeeTotal trdTradeFeeDetail = null;
        //根据汇总交易编号查询费用信息

        TrdTradeFeeTotal ttfd = new TrdTradeFeeTotal();
        ttfd.setTotleTradeId(entity.getTotleTradeId());
        ttfd.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
        ttfd.setIsPayWithTrade(DictCodeList.是否_否.getValue());
        ttfd.setFeeAmt(dtoReg.getSettleFeeAmt());

        entity.setFeeAmt(dtoReg.getSettleFeeAmt());
        entity.setFeeAmtPay(null);
        entity.setFeeAmtUnpay(dtoReg.getSettleFeeAmt());
        entity.setTrdTradeFeeTotals(Collections.singletonList(ttfd));
        // =====================人员信息=====================
        TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
        // 本方交易员
        trdInvestPersonInfo.setTrader(dtoReg.getTrader());
        // 交易对手
        trdInvestPersonInfo.setCounterId(dtoReg.getCounterId());
        trdInvestPersonInfo.setIsOutTrade(BussinessDictCodeList.是否_是.getValue());
        // 对手方交易员
        trdInvestPersonInfo.setCounterTrader(dtoReg.getCounterTrader());
        entity.setTrdInvestPersonInfo(trdInvestPersonInfo);

        // =====================人员信息=====================


        List<TrdProductDealAdd> trdProductDealAdds = new ArrayList<>();
        entity.setTrdProductDealAdds(trdProductDealAdds);
        List<PaTrdProductDeal> paTrdProductDeals = new ArrayList<>();
        List<PtlTrade> ptlTrades = new ArrayList<>();
        // 设置交易费用明细
        List<TrdTradeFeeDetail> trdTradeFeeDetails = new ArrayList<>();
        entity.setTrdTradeFeeDetails(trdTradeFeeDetails);

        // 设置交易表
        List<TrdProductDeal> deals = new ArrayList<>();
        entity.setTrdProductDeals(deals);

        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtoList = new ArrayList<>();
        //根据交易编号查询所有登记明细
        List<CfetsRegFinProductDealDto> regDealList = cfetsTrdConfirmMapper.findAllBidDealList(dtoReg.getTotleTradeId());
        for (CfetsRegFinProductDealDto regDeal : regDealList) {
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal, regDeal);
            deal.setTradeId(baseGetBusinessId.buildBussinessCode(getBussinessCodeParameter(deal.getPs())));
            // 交易本金 = 画面.净价总额
            deal.setPrinAmt(regDeal.getCpriceAmt());
            deal.setFeeAmt(regDeal.getSettleFeeAmt());
            deal.setFeeAmtPay(BigDecimal.ZERO);
            deal.setFeeAmtUnpay(regDeal.getSettleFeeAmt());
            deal.setTotleTradeId(entity.getTotleTradeId());
            deal.setTrader(entity.getTrader());
            deal.setCounterId(entity.getCounterId());
            deal.setCounterTrader(entity.getCounterTrader());
            deal.setShareAmt(ArithmeticUtil.divide(regDeal.getFaceValue(), new BigDecimal(100), decimalPlaces_4, RoundingMode.HALF_UP));
            deal.setEntrustId(regDeal.getEntrustId());
            deal.setbTradeId(regDeal.getTradeId());
            deal.setTradeMarket(dtoReg.getTradeMarket());
            deal.setIsOutTrade(BussinessDictCodeList.是否_是.getValue());
            // 交易市场
            deal.setTradeMarketMode(BussinessDictCodeList.交易市场_一级市场.getValue());
            deals.add(deal);
            // =====================划款指令信息=====================
            bussTrdTransferOrderDtoList.addAll(this.dealOrders(deal, dtoReg.getPs()));
            // 划款指令表Entity 构造结束----------------------------------------------------------

            // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            trdProductDealAdd.setTradeId(deal.getTradeId()); // 交易编号
            trdProductDealAdd.setPortfolioId(regDeal.getPortfolioId()); // 投资组合代码
            trdProductDealAdd.setEntrustId(regDeal.getEntrustId()); // 委托代码
            trdProductDealAdd.setGenType(genType); // 生成方式
            trdProductDealAdd.setOurShareAmt(regDeal.getOurShareAmt());
            trdProductDealAdd.setLendinShareAmt(regDeal.getLendinShareAmt());
            trdProductDealAdd.setOurCpriceAmt(regDeal.getOurCpriceAmt());
            trdProductDealAdd.setLendinCpriceAmt(regDeal.getLendinCpriceAmt());
            trdProductDealAdd.setOurFpriceAmt(regDeal.getOurFpriceAmt());
            trdProductDealAdd.setLendinFpriceAmt(regDeal.getLendinFpriceAmt());
            trdProductDealAdd.setOurIntAmt(regDeal.getOurIntAmt());
            trdProductDealAdd.setLendinIntAmt(regDeal.getLendinIntAmt());
            trdProductDealAdds.add(trdProductDealAdd);
            // 金融产品交易附属表Entity 构造结束 ----------------------------------------------------------

            // 组合交易表Entity 构造开始----------------------------------------------------------
            生成组合交易Bid(regDeal, deal.getTradeId(), ptlTrades);


            // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
            生成交易扩展表Bid(regDeal, deal.getTradeId(), paTrdProductDeals);

//            trdTradeFeeDetails.addAll(生成交易费用明细数据(regDeal.getTradeId(), deal.getTradeId()));

            TrdTradeFeeDetail tradeFeeDetail = new TrdTradeFeeDetail();
            tradeFeeDetail.setTradeId(deal.getTradeId());
            tradeFeeDetail.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
            tradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            tradeFeeDetail.setFeeAmt(regDeal.getSettleFeeAmt());
            entity.setFeeAmt(regDeal.getSettleFeeAmt());
            entity.setFeeAmtPay(null);
            entity.setFeeAmtUnpay(regDeal.getSettleFeeAmt());
            trdTradeFeeDetails.add(tradeFeeDetail);

//            //风险数据
//            if (StringUtil.equals(genType, DictCodeListExt1.生成方式_画面录入.getValue())) {
//                CfetsTrdFirstBondInvestTradeDto dto = new CfetsTrdFirstBondInvestTradeDto();
//                BeanUtil.copyPropertiesIgnoreNull(dto, entity);
//                dto.setPortfolioId(regDeal.getPortfolioId());
//                dto.setTradeAmt(regDeal.getTradeAmt());
//                dto.setCpriceAmt(regDeal.getCpriceAmt());
//                dto.setFpriceAmt(regDeal.getFpriceAmt());
//                dtoList.add(dto);
//            }
        }

        entity.setPtlTrade(ptlTrades);
        // 组合交易表Entity 构造结束----------------------------------------------------------
//        entity.setDtoList(dtoList);
        entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(bussTrdTransferOrderDtoList));

        entity.setPaTrdProductDeals(paTrdProductDeals);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
        //无下行时，插入下行成交匹配关系.有下行时，手工匹配或自动匹配插入下行成交匹配表
        String support_cstp_flag_bondfirst = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BONDFIRST");
        CfetsTrdConfirmMatchDto matchDto = new CfetsTrdConfirmMatchDto();
        if (StringUtil.equals(support_cstp_flag_bondfirst, DictCodeList.是否_否.getValue())) {
            matchDto.setSeqno(IDGenerator.generate());
            matchDto.setPreTradeId(totleTradeId);
            matchDto.setTrdConfStatus(DictCodeList.确认状态_已确认.getValue());
            matchDto.setCreateUser(userName);
            matchDto.setCreateDept(CommonUtil.getDeptCode());
            matchDto.setUpdateUser(userName);
            matchDto.setTrdId(entity.getTotleTradeId());
            entity.setCfetsTrdConfirmMatchDto(matchDto);
        } else {
            matchDto.setPreTradeId(totleTradeId);
            matchDto.setTrdConfStatus(DictCodeList.确认状态_已确认.getValue());
            matchDto.setUpdateUser(userName);
            matchDto.setTrdId(entity.getTotleTradeId());
            entity.setCfetsTrdConfirmMatchDto(matchDto);
        }
        return entity;
    }

    /**
     * 由画面输入的参数dto，构筑整个需要反映到数据库的实体
     *
     * @param totleTradeId 画面输入的参数
     * @see
     * @since 1.0
     */
    public CfetsTrdBondInvestTradeDto prepareEntity(String totleTradeId, String genType) {
        String userName = null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "system";
        //如果是登记数据
        //根据汇总交易编号查询登记数据
        CfetsTrdConfirmRegInfoListResponse dtoReg = cfetsTrdConfirmMapper.selectRegInfoByKey(totleTradeId);
        CfetsTrdBondInvestTradeDto entity = new CfetsTrdBondInvestTradeDto();
        entity.setTradeDate(iPlatformDate.getSysDate());
        entity.setRegIbuId(totleTradeId);
        BeanUtil.copyPropertiesIgnoreNull(entity, dtoReg);
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = getBussinessCodeParameter(dtoReg.getPs());
            entity.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            entity.setTotleTradeId(IDGenerator.generate());
        }
        // 业务类型
        entity.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        // 金融产品类型（估值核算）
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);
        entity.setBranch(branchLong);
        entity.setFinprodType(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        entity.setbTradeId(totleTradeId);
       /* entity.setPs(dtoReg.getPs());
        entity.setFinprodId(dtoReg.getFinprodId());
        entity.setFinprodType2(dtoReg.getFinprodType2());
        entity.setTradeDate(dtoReg.getTradeDate());
        entity.setSettleDate(dtoReg.getSettleDate());
        entity.setCcy(dtoReg.getCcy());
        entity.setUnitCprice(dtoReg.getUnitCprice());
        entity.setUnitInt(dtoReg.getUnitInt());
        entity.setUnitFprice(dtoReg.getUnitFprice());
        entity.setParValue(dtoReg.getParValue());
        entity.setCpriceAmt(dtoReg.getCpriceAmt());
        entity.setIntAmt(dtoReg.getIntAmt());
        entity.setFpriceAmt(dtoReg.getFpriceAmt());
        entity.setYield(dtoReg.getYield());
        entity.setExerYield(dtoReg.getExerYield());
        entity.setInvAim(dtoReg.getInvAim());
        entity.setpTradeId();*/
        if (StringUtil.isNotEmpty(dtoReg.getSettleSpeed())) {
            entity.setSettleSpeed(new Long(dtoReg.getSettleSpeed()));
        }
        // 单位面值
        entity.setParValue(this.getUnitPrice(entity.getFinprodId(), dtoReg.getSettleDate()));
        // 交易本金 = 画面.净价总额
        entity.setPrinAmt(dtoReg.getCpriceAmt());

        // 券面总额
        entity.setTotalFaceValue(dtoReg.getFaceValue());
        // 金融产品交易Entity 构造开始----------------------------------------------------------


        // 交易份额 = 画面.券面总额/100
        entity.setShareAmt(ArithmeticUtil.divide(dtoReg.getFaceValue(), new BigDecimal(100), decimalPlaces_4, RoundingMode.HALF_UP));
        // 交易金额 = 画面.交易金额
        entity.setTradeAmt(dtoReg.getTradeAmt());
        // 到期收益率 = 画面.到期收益率/100
        if (dtoReg.getYield() != null) {
            entity.setYield(dtoReg.getYield());
        }
        // 行权收益率 = 画面.行权收益率/100
        if (dtoReg.getExerYield() != null) {
            entity.setExerYield(dtoReg.getExerYield());
        }
        // 是否结清 = 数据字典缓存.是否.是
        entity.setIsClean(DictCodeList.是否_否.getValue());
        entity.setIsCancel(DictCodeList.是否_否.getValue());

        // 是否外部交易
        entity.setIsOutTrade(DictCodeList.是否_是.getValue());
        //本方
        entity.setOuPartyId(dtoReg.getOuPartyId());
        // 本方交易员
        entity.setTrader(dtoReg.getOuTraderId());
        // 交易对手
        entity.setCounterId(dtoReg.getCounterId());
        // 对手方交易员
        entity.setCounterTrader(dtoReg.getCounterTrader());

        entity.setTgBusiType(getPaBussType(dtoReg.getPs()));
        if (StringUtil.equals(dtoReg.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())) {
            entity.setTradePlat(DictCodeList.交易平台_外汇交易中心.getValue());
        } else {
            entity.setTradePlat(DictCodeList.交易平台_其它.getValue());
        }

        // 汇总扩展表
        entity.setPaTrdFinProductDealTotal(生成汇总扩展表(entity));

        // 汇总附属表
        entity.setTrdDealTotalAdd(生成汇总附属表(entity, dtoReg));

        // 设置费用信息
        TrdTradeFeeTotal trdTradeFeeDetail = null;
        //根据汇总交易编号查询费用信息

        List<TrdTradeFeeTotal> ttfdList = getTrdTradeFeeTotles(dtoReg.getTotleTradeId(), entity.getTotleTradeId());

        entity.setFeeAmt(get费用金额(ttfdList, null, null));
        entity.setFeeAmtPay(get费用金额(ttfdList, DictCodeList.是否_是.getValue(), null));
        entity.setFeeAmtUnpay(get费用金额(ttfdList, DictCodeList.是否_否.getValue(), null));
        entity.setTrdTradeFeeTotals(ttfdList);
        // =====================人员信息=====================
        TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
        // 本方交易员
        trdInvestPersonInfo.setTrader(dtoReg.getTrader());
        // 交易对手
        trdInvestPersonInfo.setCounterId(dtoReg.getCounterId());
        trdInvestPersonInfo.setIsOutTrade(BussinessDictCodeList.是否_是.getValue());
        // 对手方交易员
        trdInvestPersonInfo.setCounterTrader(dtoReg.getCounterTrader());
        entity.setTrdInvestPersonInfo(trdInvestPersonInfo);

        // =====================人员信息=====================


        List<TrdProductDealAdd> trdProductDealAdds = new ArrayList<>();
        entity.setTrdProductDealAdds(trdProductDealAdds);
        List<PaTrdProductDeal> paTrdProductDeals = new ArrayList<>();
        List<PtlTrade> ptlTrades = new ArrayList<>();
        // 设置交易费用明细
        List<TrdTradeFeeDetail> trdTradeFeeDetails = new ArrayList<>();
        entity.setTrdTradeFeeDetails(trdTradeFeeDetails);


        // 设置交易表
        List<TrdProductDeal> deals = new ArrayList<>();
        entity.setTrdProductDeals(deals);

        List<CfetsTrdBondInvestTradeDto> dtoList = new ArrayList();
        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtoList = new ArrayList<>();

        //根据交易编号查询所有登记明细
        List<CfetsRegFinProductDealDto> regDealList = cfetsTrdConfirmMapper.findAllRegDealList(dtoReg.getTotleTradeId());
        for (CfetsRegFinProductDealDto regDeal : regDealList) {
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal, regDeal);
            deal.setTradeDate(iPlatformDate.getSysDate());
            deal.setTradeId(baseGetBusinessId.buildBussinessCode(getBussinessCodeParameter(deal.getPs())));
            // 交易本金 = 画面.净价总额
            deal.setPrinAmt(regDeal.getCpriceAmt());
            List<CfetsTrdConfirmRegFeeInfoDto> regFeeDetailList = cfetsTrdConfirmMapper.findAllRegFeeDetailInfoList(regDeal.getTradeId());
            deal.setFeeAmt(getDeatil费用金额(regFeeDetailList, null));
            deal.setFeeAmtPay(getDeatil费用金额(regFeeDetailList, DictCodeList.是否_是.getValue()));
            deal.setFeeAmtUnpay(getDeatil费用金额(regFeeDetailList, DictCodeList.是否_否.getValue()));
            deal.setTotleTradeId(entity.getTotleTradeId());
            deal.setTrader(entity.getTrader());
            deal.setCounterId(entity.getCounterId());
            deal.setCounterTrader(entity.getCounterTrader());
            deal.setbTradeId(regDeal.getTradeId());
            // 是否外部交易
            deal.setIsOutTrade(DictCodeList.是否_是.getValue());
            deals.add(deal);

            // =====================划款指令信息=====================
            bussTrdTransferOrderDtoList.addAll(this.dealOrders(deal, dtoReg.getPs()));
            // 划款指令表Entity 构造结束----------------------------------------------------------

            // =====================划款指令信息=====================

            // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            trdProductDealAdd.setTradeId(deal.getTradeId()); // 交易编号
            trdProductDealAdd.setPortfolioId(regDeal.getPortfolioId()); // 投资组合代码
            trdProductDealAdd.setEntrustId(regDeal.getEntrustId()); // 委托代码
            trdProductDealAdd.setGenType(genType); // 生成方式
            trdProductDealAdd.setOurShareAmt(regDeal.getOurShareAmt());
            trdProductDealAdd.setLendinShareAmt(regDeal.getLendinShareAmt());
            trdProductDealAdd.setOurCpriceAmt(regDeal.getOurCpriceAmt());
            trdProductDealAdd.setLendinCpriceAmt(regDeal.getLendinCpriceAmt());
            trdProductDealAdd.setOurFpriceAmt(regDeal.getOurFpriceAmt());
            trdProductDealAdd.setLendinFpriceAmt(regDeal.getLendinFpriceAmt());
            trdProductDealAdd.setOurIntAmt(regDeal.getOurIntAmt());
            trdProductDealAdd.setLendinIntAmt(regDeal.getLendinIntAmt());

            trdProductDealAdds.add(trdProductDealAdd);

            // 组合交易表Entity 构造开始----------------------------------------------------------
            生成组合交易(regDeal, deal.getTradeId(), ptlTrades);


            // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
            生成交易扩展表(regDeal, deal.getTradeId(), paTrdProductDeals);

            trdTradeFeeDetails.addAll(生成交易费用明细数据(regDeal.getTradeId(), deal.getTradeId()));

            //风险数据
            CfetsTrdBondInvestTradeDto dto = new CfetsTrdBondInvestTradeDto();
            BeanUtil.copyPropertiesIgnoreNull(dto, entity);
            dto.setPortfolioId(regDeal.getPortfolioId());
            dto.setTradeAmt(regDeal.getTradeAmt());
            dto.setCpriceAmt(regDeal.getCpriceAmt());
            dto.setFpriceAmt(regDeal.getFpriceAmt());
            dto.setEntrustId(regDeal.getEntrustId());
            dto.setTotleTradeId(deal.getTradeId());
            dtoList.add(dto);
        }

        entity.setPtlTrade(ptlTrades);
        // 组合交易表Entity 构造结束----------------------------------------------------------

        entity.setDtoList(dtoList);


//        // =====================划款指令信息=====================
        entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(bussTrdTransferOrderDtoList));
//        List<BussTrdTransferOrderDto>  bussTrdTransferOrder = new ArrayList<>();
//        BussSettleOrderParameterDto bussSettleOrderParameterDto = new BussSettleOrderParameterDto();
//        bussSettleOrderParameterDto.setIsTotalTrade(BussinessDictCodeList.是否_是.getValue());
//        bussSettleOrderParameterDto.setBankLendingType(null);
//       // bussSettleOrderParameterDto.setPortfolioId(trdBondInvestTradeDto.getPortfolioId());
//        TrdProductDeal deal = new TrdProductDeal();
//        deal.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
//        deal.setSettleDate(dtoReg.getSettleDate());
//        deal.setTradeDate(dtoReg.getTradeDate());
//        deal.setPs(dtoReg.getPs());
//        deal.setCcy(dtoReg.getCcy());
//        deal.setTradeAmt(dtoReg.getTradeAmt());
//        deal.setCounterId(dtoReg.getCounterId());
//        deal.setTradeMarket(dtoReg.getTradeMarket());
//        deal.setTradePlat(entity.getTradePlat());
//        deal.setSecManageAcctId(dtoReg.getSecManageAcctId());
//        deal.setTradeMarketMode(dtoReg.getTradeMarketMode());
//        if(!StringUtil.isEmptyList(regDealList)){
//            //多组合时，必须同托管账户
//            bussSettleOrderParameterDto.setPortfolioId(regDealList.get(0).getPortfolioId());//暂存，后面删除，目的是为了查询出账户
//        }
//        bussSettleOrderParameterDto.setDeal(deal);
//        BussTrdTransferOrderDto bussTrdTransferOrderDto = bussTrdTransferOrderService.generateOrder(bussSettleOrderParameterDto);
//        bussTrdTransferOrderDto.setPayAcctNoList(null);
//        bussTrdTransferOrderDto.setRecAcctNoList(null);
//        bussTrdTransferOrderDto.setIsTotalTrade(BussinessDictCodeList.是否_是.getValue());
//        if (StringUtil.isNotEmpty(dtoReg.getSecManageAcctId())) {
//            Map<String, Object> param = new HashMap<String, Object>();
//            param.put("secAcctId", dtoReg.getSecManageAcctId());
//            String secAcctSource = cfetsTrdConfirmMapper.selectSecAcctSource(param);
//            if (StringUtil.equals(BussinessDictCodeList.账户来源_自营.getValue(), secAcctSource)
//                    && StringUtil.equals(dtoReg.getPs(), BussinessDictCodeList.交易方向_买入.getValue())) {
//                bussTrdTransferOrderDto.setIsNeedSend(BussinessDictCodeList.是否_否.getValue());
//            }
//        }
//        bussTrdTransferOrderDto.setTradeId(entity.getTotleTradeId());
//        bussTrdTransferOrderDto.setOrderRetStatus(DictCodeList.指令处理状态_处理成功.getValue());
//        bussTrdTransferOrderDto.setPortfolioId(null);//可能是多组合，此时该字段为空
//        bussTrdTransferOrderDto.setPortfolioName(null);
//        bussTrdTransferOrderDto.setIsNeedSend(DictCodeList.是否_是.getValue());
//        bussTrdTransferOrder.add(bussTrdTransferOrderDto);
//        entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(bussTrdTransferOrder));
//        // =====================划款指令信息=====================
//
//        entity.getBussTrdTransferOrder().get(0).setTradeId(entity.getTotleTradeId());
//        // 划款指令表Entity 构造结束----------------------------------------------------------

        entity.setPaTrdProductDeals(paTrdProductDeals);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
        //无下行时，插入下行成交匹配关系.有下行时，手工匹配或自动匹配插入下行成交匹配表
        String support_cstp_flag_bond = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BOND");
        CfetsTrdConfirmMatchDto matchDto = new CfetsTrdConfirmMatchDto();
        if (StringUtil.equals(support_cstp_flag_bond, DictCodeList.是否_否.getValue())) {
            matchDto.setSeqno(IDGenerator.generate());
            matchDto.setPreTradeId(totleTradeId);
            matchDto.setTrdConfStatus(DictCodeList.确认状态_已确认.getValue());
            matchDto.setCreateUser(userName);
            matchDto.setCreateDept(CommonUtil.getDeptCode());
            matchDto.setUpdateUser(userName);
            matchDto.setTrdId(entity.getTotleTradeId());
            entity.setCfetsTrdConfirmMatchDto(matchDto);
        } else {
            matchDto.setPreTradeId(totleTradeId);
            matchDto.setTrdConfStatus(DictCodeList.确认状态_已确认.getValue());
            matchDto.setUpdateUser(userName);
            matchDto.setTrdId(entity.getTotleTradeId());
            entity.setCfetsTrdConfirmMatchDto(matchDto);
        }
        return entity;
    }

    public CfetsTrdBondInvestTradeDto prepareIbuEntity(String totleTradeId, String genType) {
        //报价数据
        Map<String, Object> pageParam = new HashMap<>();
        pageParam.put("quoteId", totleTradeId);
        CfetsTrdConfirmRegInfoListResponse dtoReg = cfetsTrdConfirmMapper.selectIbuInfoByKey(pageParam);
        // 成交编号
        String oritradeId = dtoReg.getExecid();
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(dtoReg.getFinprodId());
        // 组装交易实体类
        TrdBondInvestTradeDto bondTradeDto = new TrdBondInvestTradeDto();
        /*if(StringUtil.isNotEmpty(dtoReg.getCpPartyId())){
            bondTradeDto.setCounterId(cfetsTrdConfirmMapper.selectCounterId(dtoReg.getCpPartyId()));
        }*/
        bondTradeDto.setCounterId(dtoReg.getCpPartyId());
        bondTradeDto.setCounterTrader(dtoReg.getCpTrader());
        bondTradeDto.setTrader(dtoReg.getOuTraderId());
        bondTradeDto.setPs(dtoReg.getPs());
        // 根据证券代码查询证券账户来源,判断是否是甲类户交易
        if (StringUtil.isNotEmpty(dtoReg.getSecManageAcctId())) {
            StlSecAcctInfo stlSecAcctInfo = stlSecAcctInfoMapper.selectByPrimaryKey(dtoReg.getSecManageAcctId());
            if (StringUtil.isNotEmpty(stlSecAcctInfo)) {
                bondTradeDto.setSecAcctSource(stlSecAcctInfo.getSecAcctSource());
            }
        }
        bondTradeDto.setfTradeId(oritradeId);
        // 标的主键
        bondTradeDto.setFinprodId(dtoReg.getFinprodId());
        // 起息日
        bondTradeDto.setVdate(finProduct.getVdate());
        // 到期日
        bondTradeDto.setMdate(finProduct.getMdate());
        // 资产名称
        bondTradeDto.setFinprodName(finProduct.getFinprodName());
        // 登记托管机构
        bondTradeDto.setRegistOrg(finProduct.getRegistOrg());
        //报价编号   处理头寸需要
        bondTradeDto.setQuoteId(dtoReg.getTotleTradeId());
        // bondTradeDto.setInterfTradSecdeal(interfCstpSecdeal);
        CfetsTrdBondInvestTradeDto entity = new CfetsTrdBondInvestTradeDto();
        entity.setRegIbuId(totleTradeId);
        entity.setfTradeId(oritradeId);
        BeanUtil.copyPropertiesIgnoreNull(entity, dtoReg);
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = getBussinessCodeParameter(dtoReg.getPs());
            entity.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            entity.setTotleTradeId(IDGenerator.generate());
        }
        // 业务类型
        entity.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        // 金融产品类型（估值核算）
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);
        entity.setBranch(branchLong);
        entity.setFinprodType(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        entity.setbTradeId(totleTradeId);
        entity.setPs(dtoReg.getPs());
        entity.setFinprodId(dtoReg.getFinprodId());
        entity.setFinprodType2(dtoReg.getFinprodType2());
        entity.setTradeDate(dtoReg.getTradeDate());
        entity.setSettleDate(dtoReg.getSettleDate());
        entity.setCcy(dtoReg.getCcy());
        entity.setUnitCprice(dtoReg.getUnitCprice());
        entity.setUnitInt(dtoReg.getUnitInt());
        entity.setUnitFprice(dtoReg.getUnitFprice());
        entity.setParValue(dtoReg.getParValue());
        entity.setCpriceAmt(dtoReg.getCpriceAmt());
        entity.setIntAmt(dtoReg.getIntAmt());
        entity.setFpriceAmt(dtoReg.getFpriceAmt());
        entity.setYield(dtoReg.getYield());
        entity.setExerYield(dtoReg.getExerYield());
        // 交易场所
        entity.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
        // 交易市场
        entity.setTradeMarketMode(BussinessDictCodeList.交易市场_二级市场.getValue());
        // 交易平台
        entity.setTradePlat(DictCodeList.交易平台_外汇交易中心.getValue());
        entity.setSettleSpeed(new Long(dtoReg.getSettleSpeed()));


        //entity.setInvAim();
        // 单位面值
        entity.setParValue(this.getUnitPrice(entity.getFinprodId(), dtoReg.getSettleDate()));
        // 交易本金 = 画面.净价总额
        entity.setPrinAmt(dtoReg.getCpriceAmt());
        // 券面总额
        entity.setTotalFaceValue(ArithmeticUtil.multiply(dtoReg.getShareAmt(), new BigDecimal(100)));
        if (entity.getFinprodId().indexOf("F01_") != 0) {
            entity.setFinprodId("F01_" + dtoReg.getFinprodId());
        }
        // 金融产品交易Entity 构造开始----------------------------------------------------------

        // 交易份额 = 画面.券面总额/100
        entity.setShareAmt(dtoReg.getShareAmt());
        // 交易金额 = 画面.交易金额
        entity.setTradeAmt(dtoReg.getTradeAmt());
        // 到期收益率 = 画面.到期收益率/100
        if (dtoReg.getYield() != null) {
            entity.setYield(dtoReg.getYield());
        }
        // 行权收益率 = 画面.行权收益率/100
        if (dtoReg.getExerYield() != null) {
            entity.setExerYield(dtoReg.getExerYield());
        }
        // 是否结清 = 数据字典缓存.是否.是
        entity.setIsClean(DictCodeList.是否_否.getValue());
        entity.setIsCancel(DictCodeList.是否_否.getValue());

        // 是否外部交易
        entity.setIsOutTrade(DictCodeList.是否_是.getValue());
        //本方
        entity.setOuPartyId(dtoReg.getOuPartyId());
        // 本方交易员
        entity.setTrader(bondTradeDto.getTrader());
        // 交易对手
        entity.setCounterId(bondTradeDto.getCounterId());
        // 对手方交易员
        entity.setCounterTrader(bondTradeDto.getCounterTrader());

        entity.setTgBusiType(getPaBussType(dtoReg.getPs()));

        // 汇总扩展表
        entity.setPaTrdFinProductDealTotal(生成汇总扩展表(entity));

        // 汇总附属表
        entity.setTrdDealTotalAdd(生成汇总附属表(entity, dtoReg));

        // 设置费用信息
        TrdTradeFeeTotal trdTradeFeeDetail = null;
        //根据报价数据，组装费用信息

        List<TrdTradeFeeTotal> ttfdList = getTrdTradeIbuFeeTotles(dtoReg, entity.getTotleTradeId());
        if (null != ttfdList && ttfdList.size() > 0) {
            entity.setFeeAmt(get费用金额(ttfdList, null, null));
            entity.setFeeAmtPay(get费用金额(ttfdList, DictCodeList.是否_是.getValue(), null));
            entity.setFeeAmtUnpay(get费用金额(ttfdList, DictCodeList.是否_否.getValue(), null));
        }
        entity.setTrdTradeFeeTotals(ttfdList);
        // =====================人员信息=====================
        TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
        // 本方交易员
        trdInvestPersonInfo.setTrader(bondTradeDto.getTrader());
        // 交易对手
        trdInvestPersonInfo.setCounterId(bondTradeDto.getCounterId());
        trdInvestPersonInfo.setIsOutTrade(BussinessDictCodeList.是否_是.getValue());
        // 对手方交易员
        trdInvestPersonInfo.setCounterTrader(bondTradeDto.getCounterTrader());
        entity.setTrdInvestPersonInfo(trdInvestPersonInfo);

        // =====================人员信息=====================


        List<TrdProductDealAdd> trdProductDealAdds = new ArrayList<>();
        entity.setTrdProductDealAdds(trdProductDealAdds);
        List<PaTrdProductDeal> paTrdProductDeals = new ArrayList<>();
        List<PtlTrade> ptlTrades = new ArrayList<>();
        // 设置交易费用明细
        List<TrdTradeFeeDetail> trdTradeFeeDetails = new ArrayList<>();
        entity.setTrdTradeFeeDetails(trdTradeFeeDetails);


        // 设置交易表
        List<TrdProductDeal> deals = new ArrayList<>();
        entity.setTrdProductDeals(deals);

        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtoList = new ArrayList<>();
        List<CfetsTrdBondInvestTradeDto> dtoList = new ArrayList<>();
        //根据报价编号查询所有报价记明细
        List<CfetsRegFinProductDealDto> regDealList = cfetsTrdConfirmMapper.findAllIbuDealList(dtoReg.getTotleTradeId());
        for (CfetsRegFinProductDealDto regDeal : regDealList) {
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal, dtoReg);

            deal.setTradeId(baseGetBusinessId.buildBussinessCode(getBussinessCodeParameter(deal.getPs())));
            // 交易本金 = 画面.净价总额
            deal.setShareAmt(regDeal.getShareAmt());
            if (!StringUtil.isNotEmpty(regDeal.getCpriceAmt())) {
                deal.setCpriceAmt(ArithmeticUtil.multiply(dtoReg.getUnitCprice(), regDeal.getShareAmt()));
            } else {
                deal.setCpriceAmt(regDeal.getCpriceAmt());
            }
            if (!StringUtil.isNotEmpty(regDeal.getIntAmt())) {
                deal.setIntAmt(ArithmeticUtil.multiply(dtoReg.getUnitInt(), regDeal.getShareAmt()));
            } else {
                deal.setIntAmt(regDeal.getIntAmt());
            }
            deal.setFpriceAmt(regDeal.getFpriceAmt());
            deal.setTradeAmt(regDeal.getFpriceAmt());
            deal.setDealAmt(regDeal.getFpriceAmt());
            deal.setPrinAmt(deal.getCpriceAmt());
            deal.setFeeAmt(ArithmeticUtil.doAdd(regDeal.getTrdFeeAmt(), regDeal.getSettleFeeAmt()));
            deal.setFeeAmtPay(BigDecimal.ZERO);
            deal.setFeeAmtUnpay(ArithmeticUtil.doAdd(regDeal.getTrdFeeAmt(), regDeal.getSettleFeeAmt()));
            deal.setTotleTradeId(entity.getTotleTradeId());
            deal.setPortfolioId(regDeal.getPortfolioId());
            //投资目的
            Map<String, Object> map = new HashMap<>();
            // map.put("portfolioId", regDeal.getPortfolioId());
            map.put("entrustId", regDeal.getEntrustId());
            deal.setInvAim(getInvAim(map));
            deal.setTrader(entity.getTrader());
            deal.setCounterId(entity.getCounterId());
            deal.setCounterTrader(entity.getCounterTrader());
            // 是否外部交易
            deal.setIsOutTrade(DictCodeList.是否_是.getValue());
            deals.add(deal);

            // =====================划款指令信息=====================
            bussTrdTransferOrderDtoList.addAll(this.dealOrders(deal, dtoReg.getPs()));
            // 划款指令表Entity 构造结束----------------------------------------------------------
            // =====================划款指令信息=====================

            // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            trdProductDealAdd.setTradeId(deal.getTradeId()); // 交易编号
            trdProductDealAdd.setPortfolioId(regDeal.getPortfolioId()); // 投资组合代码
            trdProductDealAdd.setEntrustId(regDeal.getEntrustId()); // 委托代码
            trdProductDealAdd.setGenType(genType); // 生成方式
            trdProductDealAdd.setOurShareAmt(regDeal.getOurShareAmt());
            trdProductDealAdd.setLendinShareAmt(regDeal.getLendinShareAmt());
          /*  trdProductDealAdd.setOurCpriceAmt(deal.getCpriceAmt());
            trdProductDealAdd.setOurFpriceAmt(deal.getFpriceAmt());
            trdProductDealAdd.setOurIntAmt(deal.getIntAmt());*/

            trdProductDealAdds.add(trdProductDealAdd);

            // 组合交易表Entity 构造开始----------------------------------------------------------
            生成组合交易(regDeal, deal.getTradeId(), ptlTrades);


            // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
            生成交易扩展表(regDeal, deal.getTradeId(), paTrdProductDeals);

            trdTradeFeeDetails.addAll(生成交易Ibu费用明细数据(regDeal, deal.getTradeId()));
            //风险数据
            CfetsTrdBondInvestTradeDto dto = new CfetsTrdBondInvestTradeDto();
            BeanUtil.copyPropertiesIgnoreNull(dto, entity);
            dto.setPortfolioId(regDeal.getPortfolioId());
            dto.setTradeAmt(regDeal.getTradeAmt());
            dto.setCpriceAmt(regDeal.getCpriceAmt());
            dto.setFpriceAmt(regDeal.getFpriceAmt());
            dto.setEntrustId(regDeal.getEntrustId());
            dto.setTotleTradeId(deal.getTradeId());
            dtoList.add(dto);

        }

        entity.setPtlTrade(ptlTrades);
        // 组合交易表Entity 构造结束----------------------------------------------------------
        entity.setDtoList(dtoList);

//        // =====================划款指令信息=====================
        entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(bussTrdTransferOrderDtoList));
//        List<BussTrdTransferOrderDto> bussTrdTransferOrder = new ArrayList<>();
//        BussSettleOrderParameterDto bussSettleOrderParameterDto = new BussSettleOrderParameterDto();
//        bussSettleOrderParameterDto.setIsTotalTrade(BussinessDictCodeList.是否_是.getValue());
//        bussSettleOrderParameterDto.setBankLendingType(null);
//        // bussSettleOrderParameterDto.setPortfolioId(trdBondInvestTradeDto.getPortfolioId());
//        TrdProductDeal deal = new TrdProductDeal();
//        deal.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
//        deal.setSettleDate(entity.getSettleDate());
//        deal.setTradeDate(entity.getTradeDate());
//        deal.setPs(entity.getPs());
//        deal.setCcy(entity.getCcy());
//        deal.setTradeAmt(entity.getTradeAmt());
//        deal.setCounterId(entity.getCounterId());
//        deal.setTradeMarket(entity.getTradeMarket());
//        deal.setTradePlat(entity.getTradePlat());
//        deal.setSecManageAcctId(entity.getSecManageAcctId());
//        deal.setTradeMarketMode(entity.getTradeMarketMode());
//        bussSettleOrderParameterDto.setDeal(deal);
//        logger.debug("成交确认开始生成划款指令:" + JsonUtils.object2Json(bussTrdTransferOrder));
//        BussTrdTransferOrderDto bussTrdTransferOrderDto = bussTrdTransferOrderService.generateOrder(bussSettleOrderParameterDto);
//        logger.debug("成交确认生成划款指令:" + JsonUtils.object2Json(bussTrdTransferOrder));
//        bussTrdTransferOrderDto.setPayAcctNoList(null);
//        bussTrdTransferOrderDto.setRecAcctNoList(null);
//        bussTrdTransferOrderDto.setIsTotalTrade(BussinessDictCodeList.是否_是.getValue());
//        bussTrdTransferOrderDto.setIsNeedSend(DictCodeList.是否_是.getValue());
//        if (StringUtil.isNotEmpty(dtoReg.getSecManageAcctId())) {
//            Map<String, Object> param = new HashMap<>();
//            param.put("secAcctId", dtoReg.getSecManageAcctId());
//            String secAcctSource = cfetsTrdConfirmMapper.selectSecAcctSource(param);
//            if (StringUtil.equals(BussinessDictCodeList.账户来源_自营.getValue(), secAcctSource)
//                    && StringUtil.equals(dtoReg.getPs(), BussinessDictCodeList.交易方向_买入.getValue())) {
//                bussTrdTransferOrderDto.setIsNeedSend(BussinessDictCodeList.是否_否.getValue());
//            }
//        }
//        bussTrdTransferOrderDto.setTradeId(entity.getTotleTradeId());
//        bussTrdTransferOrderDto.setOrderRetStatus(DictCodeList.指令处理状态_处理成功.getValue());
//        bussTrdTransferOrder.add(bussTrdTransferOrderDto);
//        entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(bussTrdTransferOrder));
//        logger.debug("成交确认生成划款指令结果:" + JsonUtils.object2Json(bussTrdTransferOrder));
//        // =====================划款指令信息=====================


        entity.setPaTrdProductDeals(paTrdProductDeals);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------

        //针对报价数据，一定有下行，手工匹配或自动匹配插入下行成交匹配表
        CfetsTrdConfirmMatchDto matchDto = new CfetsTrdConfirmMatchDto();
        matchDto.setPreTradeId(totleTradeId);
        matchDto.setTrdConfStatus(DictCodeList.确认状态_已确认.getValue());
        matchDto.setUpdateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
        matchDto.setTrdId(entity.getTotleTradeId());
        entity.setCfetsTrdConfirmMatchDto(matchDto);

        return entity;
    }

    /**
     * 由画面输入的参数dto，构筑整个需要反映到回购的实体类
     *
     * @param totleTradeId 画面输入的参数
     * @see
     * @since 1.0
     */
    public CfetsTrdRepoTrade003Dto prepareRepoEntity(String totleTradeId, String genType) {
        String userName = null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "system";
        //如果是登记数据
        //根据汇总交易编号查询登记数据
        CfetsTrdConfirmRegInfoListResponse dtoReg = cfetsTrdConfirmMapper.selectRegInfoByKey(totleTradeId);

        CfetsTrdRepoTrade003Dto entity = new CfetsTrdRepoTrade003Dto();
        entity.setRegIbuId(totleTradeId);
        BeanUtil.copyPropertiesIgnoreNull(entity, dtoReg);
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            if (StringUtil.equals(dtoReg.getPs(),
                    DictCodeList.回购拆借方向_正回购.getValue())) {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
            } else {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
            }
            entity.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            entity.setTotleTradeId(IDGenerator.generate());
        }
        // 业务类型
        entity.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        // 金融产品类型（估值核算）
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);
        entity.setBranch(branchLong);
        entity.setFinprodType(dtoReg.getFinprodType2());
        entity.setbTradeId(totleTradeId);
        if (StringUtil.isNotEmpty(dtoReg.getSettleSpeed())) {
            entity.setSettleSpeed(new Long(dtoReg.getSettleSpeed()));
        }

        // 交易本金 = 画面.净价总额
        entity.setPrinAmt(dtoReg.getTradeAmt());

        // 金融产品交易Entity 构造开始----------------------------------------------------------

        // 是否结清 = 数据字典缓存.是否.是
        entity.setIsClean(DictCodeList.是否_否.getValue());
        entity.setIsCancel(DictCodeList.是否_否.getValue());

        // 是否外部交易
        entity.setIsOutTrade(DictCodeList.是否_是.getValue());
        //本方
        entity.setOuPartyId(dtoReg.getOuPartyId());
        // 本方交易员
        entity.setTrader(dtoReg.getOuTraderId());
        // 交易对手
        entity.setCounterId(dtoReg.getCounterId());
        // 对手方交易员
        entity.setCounterTrader(dtoReg.getCounterTrader());

        if (StringUtil.equals(dtoReg.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())) {
            entity.setTradePlat(DictCodeList.交易平台_外汇交易中心.getValue());
        } else {
            entity.setTradePlat(DictCodeList.交易平台_其它.getValue());
        }
        //到期结算方式
        entity.setmDeliveryType(dtoReg.getMdeliveryType());

        // 汇总扩展表
        PaTrdFinProductDealTotal productDealTotal = new PaTrdFinProductDealTotal();
        productDealTotal.setTotleTradeId(entity.getTotleTradeId());
        productDealTotal.setPaBussType(getPaBussType(entity.getPs()));
        entity.setPaTrdFinProductDealTotal(productDealTotal);

        // 汇总附属表
        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
        trdDealTotalAdd.setTotleTradeId(entity.getTotleTradeId());
        trdDealTotalAdd.setOuPartyId(dtoReg.getOuPartyId());
        entity.setTrdDealTotalAdd(trdDealTotalAdd);

        //根据汇总交易编号查询费用信息
        List<TrdTradeFeeTotal> ttfdList = getTrdTradeFeeTotles(dtoReg.getTotleTradeId(), entity.getTotleTradeId());

        entity.setFeeAmt(get费用金额(ttfdList, null, null));
        entity.setFeeAmtPay(get费用金额(ttfdList, DictCodeList.是否_是.getValue(), null));
        entity.setFeeAmtUnpay(get费用金额(ttfdList, DictCodeList.是否_否.getValue(), null));
        entity.setTrdTradeFeeTotals(ttfdList);

        // 设置交易表
        List<TrdProductDeal> deals = new ArrayList<>();
        entity.setTrdProductDeals(deals);
        List<TrdProductDealAdd> trdProductDealAdds = new ArrayList<>();
        entity.setTrdProductDealAdds(trdProductDealAdds);
        List<PaTrdProductDeal> paTrdProductDeals = new ArrayList<>();
        entity.setPaTrdProductDeals(paTrdProductDeals);
        List<PtlTrade> ptlTrades = new ArrayList<>();
        entity.setPtlTrade(ptlTrades);
        List<FinProductType> finProductTypes = new ArrayList<>();
        entity.setFinProductTypes(finProductTypes);
        List<FinTradeProductDto> finTradeProducts = new ArrayList<>();
        entity.setFinTradeProducts(finTradeProducts);
        List<FinTradeProductAdd> finTradeProductAdds = new ArrayList<>();
        entity.setFinTradeProductAdds(finTradeProductAdds);
        // 设置交易费用明细
        List<TrdTradeFeeDetail> trdTradeFeeDetails = new ArrayList<>();
        entity.setTradeFeeList(trdTradeFeeDetails);
        //设置质押券信息
        List<BussRepoSecDto> bussRepoSecs = new ArrayList<>();
        List<CfetsTrdRepoTrade003Dto> dtoList = new ArrayList();

        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtoList = new ArrayList<>();
        //根据交易编号查询所有登记明细
        List<CfetsRegFinProductDealDto> regDealList = cfetsTrdConfirmMapper.findAllRegDealList(dtoReg.getTotleTradeId());
        for (CfetsRegFinProductDealDto regDeal : regDealList) {
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal, regDeal);
            if (baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                if (StringUtil.equals(deal.getPs(),
                        DictCodeList.回购拆借方向_正回购.getValue())) {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
                } else {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
                }
                deal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            } else {
                deal.setTradeId(IDGenerator.generate());
            }
            deal.setFinprodId(deal.getTradeId());
            deal.setTotleTradeId(entity.getTotleTradeId());
            deal.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
            // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
            deal.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
            // 起息日 = 画面.起息日
            deal.setVdate(regDeal.getvSettleDate());
            // 到期日 = 画面.到期日
            deal.setMdate(regDeal.getmSettleDate());
            // 交割日 = 画面.交易日
            deal.setSettleDate(regDeal.getvSettleDate());
            // 交易份额 = 画面.本金金额
            deal.setShareAmt(regDeal.getTradeAmt());
            // 交易本金 = 画面.本金金额
            deal.setPrinAmt(regDeal.getTradeAmt());
            // 交易金额 = 画面.本金金额
            deal.setTradeAmt(regDeal.getTradeAmt());
            // 是否结清 = 数据字典.是否.是
            deal.setIsClean(DictCodeList.是否_是.getValue());
            // 是否已撤销 = 数据字典.是否.否
            deal.setIsCancel(DictCodeList.是否_否.getValue());
            // 是否外部交易
            deal.setIsOutTrade(DictCodeList.是否_是.getValue());
            // 本方交易员
            deal.setTrader(dtoReg.getOuTraderId());
            // 交易对手
            deal.setCounterId(dtoReg.getCounterId());
            // 对手方交易员
            deal.setCounterTrader(dtoReg.getCounterTrader());
            // 交易状态 = 交易状态_初始
            deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            // 单位利息
            deal.setUnitInt(regDeal.getRate());

            List<CfetsTrdConfirmRegFeeInfoDto> regFeeDetailList = cfetsTrdConfirmMapper.findAllRegFeeDetailInfoList(regDeal.getTradeId());
            deal.setFeeAmt(getDeatil费用金额(regFeeDetailList, null));
            deal.setFeeAmtPay(getDeatil费用金额(regFeeDetailList, DictCodeList.是否_是.getValue()));
            deal.setFeeAmtUnpay(getDeatil费用金额(regFeeDetailList, DictCodeList.是否_否.getValue()));
            deal.setbTradeId(regDeal.getTradeId());
            deals.add(deal);

            // =====================划款指令信息=====================
            bussTrdTransferOrderDtoList.addAll(this.dealOrders(deal, DictCodeList.交易方向_买入.getValue()));
            // 划款指令表Entity 构造结束----------------------------------------------------------

            // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
            // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            // 交易编号 = 业务编号 ※2生成的交易编号
            trdProductDealAdd.setTradeId(deal.getTradeId());
            // 投资组合代码 = 画面.组合代码
            trdProductDealAdd.setPortfolioId(regDeal.getPortfolioId());
            // 委托代码 = 画面.委托代码
            trdProductDealAdd.setEntrustId(regDeal.getEntrustId());
            // 生成方式 = 数据字典缓存.生成方式.画面录入
            trdProductDealAdd.setGenType(genType);

            trdProductDealAdds.add(trdProductDealAdd);

            // 组合交易表Entity 构造开始-----------------------------------------------------------------------
            PtlTrade ptlTrade = new PtlTrade();
            // 组合代码 = 画面.组合代码
            ptlTrade.setPortfolioId(regDeal.getPortfolioId());
            // 业务编号 = 业务编号 ※2生成的交易编号
            ptlTrade.setBusiNo(deal.getTradeId());
            // 业务表 = "金融产品交易"
            ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            // 业务明细代码 = 业务编号 ※2生成的交易编号
            ptlTrade.setBusiId(deal.getTradeId());
            // 业务类型 = 数据字典缓存.共通业务类型.回购投资交易
            ptlTrade.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
            // 证券托管户代码 = 画面.证券托管账户
            ptlTrade.setSecManageAcctId(regDeal.getSecManageAcctId());
            // 实际发生日 = 画面.交易日
            ptlTrade.setSettleDate(regDeal.getTradeDate());
            // 工作流代码
            ptlTrades.add(ptlTrade);

            // 组合交易表Entity 构造结束-----------------------------------------------------------------------


            // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
            PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
            // 交易编号 = 金融产品交易Entity.交易编号
            paTrdProductDeal.setTradeId(deal.getTradeId());
            // 平安业务类型 = 数据字典缓存.平安业务类型（按交易方向取对应值）
            if (DictCodeList.回购拆借方向_正回购.getValue().equals(deal.getPs())) {
                // 交易方向为正回购时，取数据字典缓存.平安业务类型.正回购
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_正回购.getValue());
            } else {
                // 交易方向为逆回购时，取数据字典缓存.平安业务类型.逆回购
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_逆回购.getValue());
            }
            // 回购/拆借方向 = 画面.交易方向
            paTrdProductDeal.setBackLendDirection(deal.getPs());
            // 交易所属部门 = 输出的人员信息结果DTO.本方交易员所属部门
            // paTrdProductDeal.setDeptCode(input.getTrdInvestPersonInfo().getTraderDept());
            paTrdProductDeals.add(paTrdProductDeal);
            // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
            trdTradeFeeDetails.addAll(生成交易费用明细数据(regDeal.getTradeId(), deal.getTradeId()));

            // 交易类金融产品Entity 构造开始----------------------------------------------------------
            FinTradeProductDto finTradeProduct = new FinTradeProductDto();
            // 金融产品代码 = 金融产品交易Entity.交易编号
            finTradeProduct.setFinprodId(deal.getTradeId());
            // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
            finTradeProduct.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
            //  金融产品类型 =  数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
            finTradeProduct.setFinprodType2(deal.getFinprodType2());
            finTradeProduct.setFinprodType(deal.getFinprodType());
            // 名义本金 = 画面.本金金额
            finTradeProduct.setPrin(deal.getPrinAmt());
            // 名义币种 = 画面.币种
            finTradeProduct.setCcy(deal.getCcy());
            // 起息日 = 画面.起息日
            finTradeProduct.setVdate(deal.getVdate());
            // 到期日 = 画面.到期日
            finTradeProduct.setMdate(deal.getMdate());
            finTradeProduct.setVpayDate(deal.getVdate());
            finTradeProduct.setMpayDate(deal.getMdate());
            // 查看缺失字段增加 add by renping 20191020 start
            finTradeProduct.setActCapDays(regDeal.getActRepoTerm());
            // 查看缺失字段增加 add by renping 20191020 end
            // 期限(天) = 画面.回购期限
            finTradeProduct.setTerm(regDeal.getRepoTerm());
            // 利率类型 = 数据字典缓存.利率类型.固定利率
            finTradeProduct.setIntType(DictCodeList.利率类型_固定利率.getValue());
            // 固定利率 = 画面.回购利率/100
            finTradeProduct.setIntRate(regDeal.getRate());
            // 计息基础 = 画面.计息基础
            finTradeProduct.setBasis(DictCodeList.计息基础_A_365.getValue());// 计息基础
            // 到期本金 = 画面.本金金额
            finTradeProduct.setmPrinAmt(deal.getPrinAmt());
            // 到期利息 = 画面.回购利息
            finTradeProduct.setmIntAmt(regDeal.getmIntAmt());
            // 到期金额 = 画面.本金金额+画面.回购利息
            finTradeProduct.setmTradeAmt(regDeal.getmTradeAmt());

            // 本金收益特征 = 逆回购：保本，正回购不存
            if (DictCodeList.回购拆借方向_逆回购.getValue().equals(regDeal.getPs())) {
                // ※若画面联动1.7查询出的组合为保本
                finTradeProduct.setCapiIncomeFeature(DictCodeListExt1.本金收益特征_保本.getValue());
            }
            // 交易场所 = 画面.交易场所
            finTradeProduct.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());// 交易场所
            // 交易日历 = 画面.交易日历
            finTradeProduct.setCalendarId("03");// 交易日历 银行间
            // 期限品种 = 画面.期限品种
            finTradeProduct.setTermType(regDeal.getTermType());
            // 交易对手 = 画面.交易对手
            finTradeProduct.setCounterId(regDeal.getCounterId());
            finTradeProducts.add(finTradeProduct);
            // 交易类金融产品Entity 构造结束----------------------------------------------------------

            // 交易类金融产品附属Entity 构造开始----------------------------------------------------------
            FinTradeProductAdd finTradeProductAdd = new FinTradeProductAdd();
            BeanUtil.copyPropertiesIgnoreNull(finTradeProductAdd, finTradeProduct);
            // 金融产品代码 = 金融产品交易Entity.交易编号
            finTradeProductAdd.setFinprodId(deal.getTradeId());
            // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
            finTradeProductAdd.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
            //  金融产品类型 =  数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
            finTradeProductAdd.setFinprodType2(deal.getFinprodType2());
            // 登记托管机构
            StlSecAcctInfo ptlSecAcct = stlSecAcctInfoMapper.selectByPrimaryKey(
                    regDeal.getSecManageAcctId());
            finTradeProductAdd.setRegistOrg(null != ptlSecAcct ? ptlSecAcct.getRegistOrg() : null);
            finTradeProductAdds.add(finTradeProductAdd);
            // 交易类金融产品附属Entity 构造结束----------------------------------------------------------

            // 金融产品分类Entity 构造开始----------------------------------------------------------
            FinProductType finProductType = new FinProductType();
            // 金融产品代码 = 金融产品交易Entity.交易编号
            finProductType.setFinprodId(deal.getTradeId());
            // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
            finProductType.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
            // 分类6 = 画面.回购类型
//            finProductType.setType6(regDeal.getRepoType());
            finProductType.setType6(DictCodeList.回购类型_质押式回购.getValue());
            // 分类7 = 画面.回购类型
            finProductType.setType7(regDeal.getPs());
            // 分类8 = 画面.回购类型
            finProductType.setType8(DictCodeList.平安资产类型_质押式回购.getValue());
            FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
            finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_无标的投管资产类型映射行内资产分类.getValue());
            finProductTypeConfig.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            finProductTypeConfig.setInputValue2(regDeal.getRepoType());
            finProductTypeConfig.setInputValue3(regDeal.getPs());
            finProductTypeConfig = restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
                    finProductTypeConfig, FinProductTypeConfig.class);
            finProductType.setType9(finProductTypeConfig.getOutputValue2());
            finProductType.setType10(finProductTypeConfig.getOutputValue3());
            finProductType.setType11(finProductTypeConfig.getOutputValue4());
            finProductType.setType12(finProductTypeConfig.getOutputValue5());
            finProductTypes.add(finProductType);

            //根据交易编号查询所有的质押券信息
            List<PledgeBondInfoDto> pledgeBondList = cfetsTrdConfirmMapper.findAllSecPtlsList(regDeal.getTradeId());
            //设置金融产品交易登记质押品
            BussRepoSecDto bussRepoSec = new BussRepoSecDto();
            bussRepoSec.setPageFrom(PAGE_FROM_TrdRepoTrade003);
            bussRepoSec.setPledgeBondList(pledgeBondList);
            bussRepoSec.setFinprodId(deal.getTradeId());
            bussRepoSecs.add(bussRepoSec);

//            //风险数据
//            if (StringUtil.equals(genType, DictCodeListExt1.生成方式_画面录入.getValue())) {
//                CfetsTrdRepoTrade003Dto dto = new CfetsTrdRepoTrade003Dto();
//                BeanUtil.copyPropertiesIgnoreNull(dto, regDeal);
//                dto.setPortfolioId(regDeal.getPortfolioId());
//                dto.setPrinAmt(regDeal.getTradeAmt());
//                dto.setTermTypeReback(dtoReg.getTermType());
//                dto.setRebackPrice(ArithmeticUtil.multiply(regDeal.getRate(), new BigDecimal(100)));
//                dto.setTradeAmt(regDeal.getTradeAmt());
//                dto.setCpriceAmt(regDeal.getCpriceAmt());
//                dto.setMdate(regDeal.getmSettleDate());
//                dtoList.add(dto);
//            }

        }
        entity.setBussRepoSecs(bussRepoSecs);
//        entity.setDtoList(dtoList);
        entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(bussTrdTransferOrderDtoList));

        //无下行时，插入下行成交匹配关系.有下行时，手工匹配或自动匹配插入下行成交匹配表
        String support_cstp_flag_repo = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_REPO");
        CfetsTrdConfirmMatchDto matchDto = new CfetsTrdConfirmMatchDto();
        if (StringUtil.equals(support_cstp_flag_repo, DictCodeList.是否_否.getValue())) {
            matchDto.setSeqno(IDGenerator.generate());
            matchDto.setPreTradeId(totleTradeId);
            matchDto.setTrdConfStatus(DictCodeList.确认状态_已确认.getValue());
            matchDto.setCreateUser(userName);
            matchDto.setCreateDept(CommonUtil.getDeptCode());
            matchDto.setUpdateUser(userName);
            matchDto.setTrdId(entity.getTotleTradeId());
            entity.setCfetsTrdConfirmMatchDto(matchDto);
        } else {
            matchDto.setPreTradeId(totleTradeId);
            matchDto.setTrdConfStatus(DictCodeList.确认状态_已确认.getValue());
            matchDto.setUpdateUser(userName);
            matchDto.setTrdId(entity.getTotleTradeId());
            entity.setCfetsTrdConfirmMatchDto(matchDto);
        }
        return entity;
    }

    public CfetsTrdRepoTrade003Dto prepareRepoIbuEntity(String totleTradeId, String genType) {
        //报价数据
        Map<String, Object> pageParam = new HashMap<>();
        pageParam.put("quoteId", totleTradeId);
        CfetsTrdConfirmRegInfoListResponse dtoReg = cfetsTrdConfirmMapper.selectRepoIbuInfoByKey(pageParam);
        // 成交编号
        String oritradeId = dtoReg.getExecid();
        String userName = null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "system";
        CfetsTrdRepoTrade003Dto entity = new CfetsTrdRepoTrade003Dto();
        entity.setRegIbuId(totleTradeId);
        BeanUtil.copyPropertiesIgnoreNull(entity, dtoReg);
        entity.setfTradeId(oritradeId);
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            if (StringUtil.equals(dtoReg.getPs(),
                    DictCodeList.回购拆借方向_正回购.getValue())) {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
            } else {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
            }
            entity.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            entity.setTotleTradeId(IDGenerator.generate());
        }
        if (StringUtil.equals(dtoReg.getPs(),
                DictCodeList.回购拆借方向_正回购.getValue())) {
            entity.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        } else {
            entity.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        }
        // 业务类型
        entity.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
        // 金融产品类型（估值核算）
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);
        entity.setBranch(branchLong);
        entity.setFinprodType(entity.getFinprodType2());
        entity.setbTradeId(totleTradeId);
        if (StringUtil.isNotEmpty(dtoReg.getSettleSpeed())) {
            entity.setSettleSpeed(new Long(dtoReg.getSettleSpeed()));
        }

        // 交易本金 = 画面.净价总额
        entity.setPrinAmt(dtoReg.getTradeAmt());
        // 交易场所
        entity.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());
        // 交易日历
        entity.setCalendarId("03");
        entity.setRepoType(DictCodeList.回购类型_质押式回购.getValue());
        entity.setTerm(new Long(String.valueOf(dtoReg.getTermDays())));
        // 实际占款天数
        entity.setActCapDays(new Long(String.valueOf(dtoReg.getActRepoTerm())));
        entity.setTermTypeReback(dtoReg.getTermType());
        entity.setRebackPrice(dtoReg.getRate());
        // 计息基础
        entity.setBasis(dtoReg.getBasis());
        entity.setMdateSettleAmt(dtoReg.getMtradeAmt());
        // 回购利息
        entity.setRebackIntAmt(dtoReg.getMintAmt());
        // 交易金额
        //  entity.setTradeRepAmt(dtoReg.getTradeAmt());
        // 金融产品交易Entity 构造开始----------------------------------------------------------

        // 是否结清 = 数据字典缓存.是否.是
        entity.setIsClean(DictCodeList.是否_否.getValue());
        entity.setIsCancel(DictCodeList.是否_否.getValue());

        // 是否外部交易
        entity.setIsOutTrade(DictCodeList.是否_是.getValue());
        //本方
        entity.setOuPartyId(dtoReg.getOuPartyId());
        // 本方交易员
        entity.setTrader(dtoReg.getOuTraderId());
        // 交易对手
        entity.setCounterId(dtoReg.getCounterId());
        // 对手方交易员
        entity.setCounterTrader(dtoReg.getCounterTrader());

        entity.setTradeMarketMode(BussinessDictCodeList.交易市场_二级市场.getValue());

        if (StringUtil.equals(dtoReg.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())) {
            entity.setTradePlat(DictCodeList.交易平台_外汇交易中心.getValue());
        } else {
            entity.setTradePlat(DictCodeList.交易平台_其它.getValue());
        }
        entity.setTgBusiType(getPaBussType(dtoReg.getPs()));
        entity.setmDeliveryType(dtoReg.getMdeliveryType());

        // 汇总扩展表
        PaTrdFinProductDealTotal productDealTotal = new PaTrdFinProductDealTotal();
        productDealTotal.setTotleTradeId(entity.getTotleTradeId());
        productDealTotal.setPaBussType(getPaBussType(entity.getPs()));
        entity.setPaTrdFinProductDealTotal(productDealTotal);

        // 汇总附属表
        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
        trdDealTotalAdd.setTotleTradeId(entity.getTotleTradeId());
        trdDealTotalAdd.setOuPartyId(dtoReg.getOuPartyId());
        entity.setTrdDealTotalAdd(trdDealTotalAdd);


        //根据报价数据，组装费用信息
        List<TrdTradeFeeTotal> ttfdList = getTrdTradeIbuFeeTotles(dtoReg, entity.getTotleTradeId());
        if (null != ttfdList && ttfdList.size() > 0) {
            entity.setFeeAmt(get费用金额(ttfdList, null, null));
            entity.setFeeAmtPay(get费用金额(ttfdList, DictCodeList.是否_是.getValue(), null));
            entity.setFeeAmtUnpay(get费用金额(ttfdList, DictCodeList.是否_否.getValue(), null));
        }
        entity.setTrdTradeFeeTotals(ttfdList);

        // 设置交易表
        List<TrdProductDeal> deals = new ArrayList<>();
        entity.setTrdProductDeals(deals);
        List<TrdProductDealAdd> trdProductDealAdds = new ArrayList<>();
        entity.setTrdProductDealAdds(trdProductDealAdds);
        List<PaTrdProductDeal> paTrdProductDeals = new ArrayList<>();
        entity.setPaTrdProductDeals(paTrdProductDeals);
        List<PtlTrade> ptlTrades = new ArrayList<>();
        entity.setPtlTrade(ptlTrades);
        List<FinProductType> finProductTypes = new ArrayList<>();
        entity.setFinProductTypes(finProductTypes);
        List<FinTradeProductDto> finTradeProducts = new ArrayList<>();
        entity.setFinTradeProducts(finTradeProducts);
        List<FinTradeProductAdd> finTradeProductAdds = new ArrayList<>();
        entity.setFinTradeProductAdds(finTradeProductAdds);
        // 设置交易费用明细
        List<TrdTradeFeeDetail> trdTradeFeeDetails = new ArrayList<>();
        entity.setTradeFeeList(trdTradeFeeDetails);
        //设置质押券信息
        List<BussRepoSecDto> bussRepoSecs = new ArrayList<>();

        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtoList = new ArrayList<>();
        //根据交易编号查询所有报价明细
        List<CfetsRegFinProductDealDto> regDealList = cfetsTrdConfirmMapper.findAllIbuDealList(dtoReg.getTotleTradeId());
        for (CfetsRegFinProductDealDto regDeal : regDealList) {
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal, entity);
            if (baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                if (StringUtil.equals(deal.getPs(),
                        DictCodeList.回购拆借方向_正回购.getValue())) {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
                } else {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
                }
                deal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            } else {
                deal.setTradeId(IDGenerator.generate());
            }
            deal.setFinprodId(deal.getTradeId());
            deal.setTotleTradeId(entity.getTotleTradeId());
            deal.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
            // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
            deal.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));


            // 起息日 = 画面.起息日
            deal.setVdate(dtoReg.getVsettleDate());
            // 到期日 = 画面.到期日
            deal.setMdate(dtoReg.getMsettleDate());
            // 交割日 = 画面.交易日
            deal.setSettleDate(dtoReg.getVsettleDate());

            // 交易份额 = 画面.本金金额
            deal.setShareAmt(regDeal.getFpriceAmt());
            // 交易本金 = 画面.本金金额
            deal.setPrinAmt(regDeal.getFpriceAmt());
            // 交易金额 = 画面.本金金额
            deal.setTradeAmt(regDeal.getFpriceAmt());

            // 是否结清 = 数据字典.是否.是
            deal.setIsClean(DictCodeList.是否_是.getValue());
            // 是否已撤销 = 数据字典.是否.否
            deal.setIsCancel(DictCodeList.是否_否.getValue());
            // 是否外部交易
            deal.setIsOutTrade(DictCodeList.是否_是.getValue());


            // 本方交易员
            deal.setTrader(dtoReg.getOuTraderId());
            // 交易对手
            deal.setCounterId(dtoReg.getCounterId());
            // 对手方交易员
            deal.setCounterTrader(dtoReg.getCounterTrader());

            // 交易状态 = 交易状态_初始
            deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            deal.setRemark(regDeal.getRemark());

            deal.setUnitInt(entity.getRebackPrice());
            if (StringUtils.isNotBlank(entity.getfTradeId())) {
                deal.setfTradeId(entity.getfTradeId());
            }
            //交易表存入首期交付日
            deal.setPayDate(dtoReg.getVsettleDate());
            deal.setFeeAmt(ArithmeticUtil.doAdd(regDeal.getTrdFeeAmt(), regDeal.getSettleFeeAmt()));
            deal.setFeeAmtPay(BigDecimal.ZERO);
            deal.setFeeAmtUnpay(ArithmeticUtil.doAdd(regDeal.getTrdFeeAmt(), regDeal.getSettleFeeAmt()));
            deal.setPortfolioId(regDeal.getPortfolioId());
            deal.setEntrustId(regDeal.getEntrustId());
            deals.add(deal);

            // =====================划款指令信息=====================
            bussTrdTransferOrderDtoList.addAll(this.dealOrders(deal, DictCodeList.交易方向_买入.getValue()));
            // =====================划款指令信息=====================

            // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
            // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            // 交易编号 = 业务编号 ※2生成的交易编号
            trdProductDealAdd.setTradeId(deal.getTradeId());
            // 投资组合代码 = 画面.组合代码
            trdProductDealAdd.setPortfolioId(regDeal.getPortfolioId());
            // 委托代码 = 画面.委托代码
            trdProductDealAdd.setEntrustId(regDeal.getEntrustId());
            // 生成方式 = 数据字典缓存.生成方式.画面录入
            trdProductDealAdd.setGenType(genType);

            trdProductDealAdds.add(trdProductDealAdd);

            // 组合交易表Entity 构造开始-----------------------------------------------------------------------
            PtlTrade ptlTrade = new PtlTrade();
            // 组合代码 = 画面.组合代码
            ptlTrade.setPortfolioId(regDeal.getPortfolioId());
            // 业务编号 = 业务编号 ※2生成的交易编号
            ptlTrade.setBusiNo(deal.getTradeId());
            // 业务表 = "金融产品交易"
            ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            // 业务明细代码 = 业务编号 ※2生成的交易编号
            ptlTrade.setBusiId(deal.getTradeId());
            // 业务类型 = 数据字典缓存.共通业务类型.回购投资交易
            ptlTrade.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
            // 证券托管户代码 = 画面.证券托管账户
            ptlTrade.setSecManageAcctId(dtoReg.getSecManageAcctId());
            // 实际发生日 = 画面.交易日
            ptlTrade.setSettleDate(dtoReg.getTradeDate());
            // 工作流代码
            ptlTrades.add(ptlTrade);

            // 组合交易表Entity 构造结束-----------------------------------------------------------------------


            // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
            PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
            // 交易编号 = 金融产品交易Entity.交易编号
            paTrdProductDeal.setTradeId(deal.getTradeId());
            // 平安业务类型 = 数据字典缓存.平安业务类型（按交易方向取对应值）
            if (DictCodeList.回购拆借方向_正回购.getValue().equals(deal.getPs())) {
                // 交易方向为正回购时，取数据字典缓存.平安业务类型.正回购
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_正回购.getValue());
            } else {
                // 交易方向为逆回购时，取数据字典缓存.平安业务类型.逆回购
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_逆回购.getValue());
            }
            // 回购/拆借方向 = 画面.交易方向
            paTrdProductDeal.setBackLendDirection(dtoReg.getPs());
            // 交易所属部门 = 输出的人员信息结果DTO.本方交易员所属部门
            // paTrdProductDeal.setDeptCode(input.getTrdInvestPersonInfo().getTraderDept());
            paTrdProductDeals.add(paTrdProductDeal);
            // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
            trdTradeFeeDetails.addAll(生成交易Ibu费用明细数据(regDeal, deal.getTradeId()));


            // 交易类金融产品Entity 构造开始----------------------------------------------------------
            FinTradeProductDto finTradeProduct = new FinTradeProductDto();
            // 金融产品代码 = 金融产品交易Entity.交易编号
            finTradeProduct.setFinprodId(deal.getTradeId());
            // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
            finTradeProduct.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
            //  金融产品类型 =  数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
            finTradeProduct.setFinprodType2(deal.getFinprodType2());
            finTradeProduct.setFinprodType(deal.getFinprodType());
            // 名义本金 = 画面.本金金额
            finTradeProduct.setPrin(deal.getPrinAmt());
            // 名义币种 = 画面.币种
            finTradeProduct.setCcy(deal.getCcy());
            // 起息日 = 画面.起息日
            finTradeProduct.setVdate(deal.getVdate());
            // 到期日 = 画面.到期日
            finTradeProduct.setMdate(deal.getMdate());
            finTradeProduct.setVpayDate(deal.getVdate());
            finTradeProduct.setMpayDate(deal.getMdate());
            // 查看缺失字段增加 add by renping 20191020 start
            finTradeProduct.setActCapDays(entity.getActCapDays());
            // 查看缺失字段增加 add by renping 20191020 end
            // 期限(天) = 画面.回购期限
            finTradeProduct.setTerm(entity.getTerm());
            // 利率类型 = 数据字典缓存.利率类型.固定利率
            finTradeProduct.setIntType(DictCodeList.利率类型_固定利率.getValue());
            // 固定利率 = 画面.回购利率/100
            finTradeProduct.setIntRate(entity.getRebackPrice());
            // 计息基础 = 画面.计息基础
            finTradeProduct.setBasis(DictCodeList.计息基础_A_365.getValue());// 计息基础
            // 到期本金 = 画面.本金金额
            finTradeProduct.setmPrinAmt(deal.getPrinAmt());
            // 到期利息 = 画面.回购利息
            finTradeProduct.setmIntAmt(dtoReg.getMintAmt());
            // 到期金额 = 画面.本金金额+画面.回购利息
            finTradeProduct.setmTradeAmt(dtoReg.getMtradeAmt());

            // 本金收益特征 = 逆回购：保本，正回购不存
            if (DictCodeList.回购拆借方向_逆回购.getValue().equals(dtoReg.getPs())) {
                // ※若画面联动1.7查询出的组合为保本
                finTradeProduct.setCapiIncomeFeature(DictCodeListExt1.本金收益特征_保本.getValue());
            }
            // 交易场所 = 画面.交易场所
            finTradeProduct.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());// 交易场所
            // 交易日历 = 画面.交易日历
            finTradeProduct.setCalendarId("03");// 交易日历 银行间
            // 期限品种 = 画面.期限品种
            finTradeProduct.setTermType(dtoReg.getTermType());
            // 交易对手 = 画面.交易对手
            finTradeProduct.setCounterId(dtoReg.getCounterId());
            finTradeProducts.add(finTradeProduct);
            // 交易类金融产品Entity 构造结束----------------------------------------------------------

            // 交易类金融产品附属Entity 构造开始----------------------------------------------------------
            FinTradeProductAdd finTradeProductAdd = new FinTradeProductAdd();
            BeanUtil.copyPropertiesIgnoreNull(finTradeProductAdd, finTradeProduct);
            // 金融产品代码 = 金融产品交易Entity.交易编号
            finTradeProductAdd.setFinprodId(deal.getTradeId());
            // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
            finTradeProductAdd.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
            //  金融产品类型 =  数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
            finTradeProductAdd.setFinprodType2(deal.getFinprodType2());
            // 登记托管机构
            StlSecAcctInfo ptlSecAcct = stlSecAcctInfoMapper.selectByPrimaryKey(
                    dtoReg.getSecManageAcctId());
            finTradeProductAdd.setRegistOrg(null != ptlSecAcct ? ptlSecAcct.getRegistOrg() : null);
            finTradeProductAdds.add(finTradeProductAdd);
            // 交易类金融产品附属Entity 构造结束----------------------------------------------------------

            // 金融产品分类Entity 构造开始----------------------------------------------------------
            FinProductType finProductType = new FinProductType();
            // 金融产品代码 = 金融产品交易Entity.交易编号
            finProductType.setFinprodId(deal.getTradeId());
            // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
            finProductType.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
            // 分类6 = 画面.回购类型

            finProductType.setType6(DictCodeList.回购类型_质押式回购.getValue());
            // 分类7 = 画面.回购类型
            finProductType.setType7(dtoReg.getPs());
            // 分类8 = 画面.回购类型
            finProductType.setType8(DictCodeList.平安资产类型_质押式回购.getValue());
            FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
            finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_无标的投管资产类型映射行内资产分类.getValue());
            finProductTypeConfig.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            finProductTypeConfig.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            finProductTypeConfig.setInputValue3(dtoReg.getPs());
            finProductTypeConfig = restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
                    finProductTypeConfig, FinProductTypeConfig.class);
            finProductType.setType9(finProductTypeConfig.getOutputValue2());
            finProductType.setType10(finProductTypeConfig.getOutputValue3());
            finProductType.setType11(finProductTypeConfig.getOutputValue4());
            finProductType.setType12(finProductTypeConfig.getOutputValue5());
            finProductTypes.add(finProductType);

            //根据报价编号和指令编号查询所有的质押券信息
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("quoteId", dtoReg.getTotleTradeId());
            param.put("entrustId", regDeal.getEntrustId());

            List<PledgeBondInfoDto> pledgeBondList = cfetsTrdConfirmMapper.findAllIbuSecPtlsList(param);
            //设置金融产品交易登记质押品
            BussRepoSecDto bussRepoSec = new BussRepoSecDto();
            bussRepoSec.setPageFrom(PAGE_FROM_TrdRepoTrade003);
            bussRepoSec.setPledgeBondList(pledgeBondList);
            bussRepoSec.setFinprodId(deal.getTradeId());
            bussRepoSecs.add(bussRepoSec);
        }
        entity.setBussRepoSecs(bussRepoSecs);
        entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(bussTrdTransferOrderDtoList));

        //针对报价数据，一定有下行，手工匹配或自动匹配插入下行成交匹配表
        CfetsTrdConfirmMatchDto matchDto = new CfetsTrdConfirmMatchDto();
        matchDto.setPreTradeId(totleTradeId);
        matchDto.setTrdConfStatus(DictCodeList.确认状态_已确认.getValue());
        matchDto.setUpdateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
        matchDto.setTrdId(entity.getTotleTradeId());
        entity.setCfetsTrdConfirmMatchDto(matchDto);

        return entity;
    }


    private String getPaBussType(String 交易方向) {
        // 平安业务类型(数据字典缓存.平安业务类型（按交易方向取对应值）)
        if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), 交易方向)) {
            return DictCodeListExt1.平安业务类型_债券类资产买入.getValue();
        } else if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), 交易方向)) {
            return DictCodeListExt1.平安业务类型_债券类资产卖出.getValue();
        }
        // 一体化对应etliu start
        // else if (StringUtil.equals(DictCodeList.交易方向_行权.getValue(), 交易方向)) {
        else if (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), 交易方向)
                || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), 交易方向)) {
            // 一体化对应etliu end
            return DictCodeListExt1.平安业务类型_债券类资产行权.getValue();
        } else if (DictCodeList.回购拆借方向_正回购.getValue().equals(交易方向)) {
            // 交易方向为正回购时，取数据字典缓存.平安业务类型.正回购
            return DictCodeListExt1.平安业务类型_正回购.getValue();
        } else if (DictCodeList.回购拆借方向_逆回购.getValue().equals(交易方向)) {
            // 交易方向为逆回购时，取数据字典缓存.平安业务类型.逆回购
            return DictCodeListExt1.平安业务类型_逆回购.getValue();
        }
        return null;
    }

    private PaTrdFinProductDealTotal 生成汇总扩展表(CfetsTrdBondInvestTradeDto entity) {
        PaTrdFinProductDealTotal productDealTotal = new PaTrdFinProductDealTotal();
        productDealTotal.setTotleTradeId(entity.getTotleTradeId());
        productDealTotal.setPaBussType(getPaBussType(entity.getPs()));
        return productDealTotal;
    }

    private TrdDealTotalAdd 生成汇总附属表(CfetsTrdBondInvestTradeDto entity, CfetsTrdConfirmRegInfoListResponse dtoReg) {
        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
        trdDealTotalAdd.setTotleTradeId(entity.getTotleTradeId());
        trdDealTotalAdd.setOurShareAmt(dtoReg.getOurShareAmt());
        trdDealTotalAdd.setLendinShareAmt(dtoReg.getLendinShareAmt());
        trdDealTotalAdd.setOurFpriceAmt(dtoReg.getOurFpriceAmt());
        trdDealTotalAdd.setLendinFpriceAmt(dtoReg.getLendinFpriceAmt() == null ? BigDecimal.ZERO : dtoReg.getLendinFpriceAmt());
        trdDealTotalAdd.setOurCpriceAmt(ArithmeticUtil.subtract(dtoReg.getCpriceAmt(), dtoReg.getLendinCpriceAmt()));
        trdDealTotalAdd.setLendinCpriceAmt(dtoReg.getLendinCpriceAmt() == null ? BigDecimal.ZERO : dtoReg.getLendinCpriceAmt());
        trdDealTotalAdd.setOurFpriceAmt2(dtoReg.getOwnIntAmt());
        trdDealTotalAdd.setLendinFpriceAmt2(dtoReg.getLendinIntAmt() == null ? BigDecimal.ZERO : dtoReg.getLendinIntAmt());
        // trdDealTotalAdd.setAssetRecommandOrg(dtoReg.getAssetRecommandOrg());
        //trdDealTotalAdd.setEntrustId(dtoReg.getEntrustId());
        trdDealTotalAdd.setOuPartyId(dtoReg.getOuPartyId());
        return trdDealTotalAdd;
    }

    private void 生成组合交易(CfetsRegFinProductDealDto dto, String 交易编号, List<PtlTrade> ptlTrades) {
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId()); // 组合代码
        ptlTrade.setBusiNo(交易编号); // 业务编号
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
        ptlTrade.setBusiId(dto.getFinprodId()); // 业务明细代码
        ptlTrade.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue()); // 业务类型
        ptlTrade.setInvAim(dto.getInvAim()); // 投资目的
        ptlTrade.setSecManageAcctId(dto.getSecManageAcctId()); // 证券托管户代码
        ptlTrade.setSettleDate(dto.getSettleDate()); // 实际发生日
        ptlTrade.setWorkflowId(null); // 工作流代码
        ptlTrades.add(ptlTrade);
    }

    private void 生成组合交易Bid(CfetsRegFinProductDealDto dto, String 交易编号, List<PtlTrade> ptlTrades) {
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId()); // 组合代码
        ptlTrade.setBusiNo(交易编号); // 业务编号
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
        ptlTrade.setBusiId(dto.getFinprodId()); // 业务明细代码
        ptlTrade.setBusiType(dto.getBusiType()); // 业务类型
        ptlTrade.setInvAim(dto.getInvAim()); // 投资目的
        ptlTrade.setSecManageAcctId(dto.getSecManageAcctId()); // 证券托管户代码
        ptlTrade.setSettleDate(dto.getSettleDate()); // 实际发生日
        ptlTrade.setWorkflowId(null); // 工作流代码
        ptlTrades.add(ptlTrade);
    }

    private void 生成交易扩展表(CfetsRegFinProductDealDto dto, String 交易编号,
                         List<PaTrdProductDeal> paTrdProductDeals) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(交易编号); // 交易编号
        paTrdProductDeal.setPaBussType(getPaBussType(dto.getPs()));
        paTrdProductDeal.setDeptCode(null); // 交易所属部门
        paTrdProductDeal.setWorkflowId(null); // 工作流代码
        paTrdProductDeals.add(paTrdProductDeal);
    }

    private void 生成交易扩展表Bid(CfetsRegFinProductDealDto dto, String 交易编号,
                            List<PaTrdProductDeal> paTrdProductDeals) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(交易编号); // 交易编号
        paTrdProductDeal.setPaBussType(dto.getTgBusiType());
        paTrdProductDeal.setDeptCode(null); // 交易所属部门
        paTrdProductDeal.setWorkflowId(null); // 工作流代码
        paTrdProductDeals.add(paTrdProductDeal);
    }

    private List<TrdTradeFeeDetail> 生成交易费用明细数据(String regTradeId, String tradeId) {
        List<TrdTradeFeeDetail> result = new ArrayList<>();
        //根据登记编号查询所有的费用信息
        List<CfetsTrdConfirmRegFeeInfoDto> regFeeList = cfetsTrdConfirmMapper.findAllRegFeeDetailInfoList(regTradeId);
        if (StringUtil.isEmptyList(regFeeList)) {
            return Lists.newArrayList();
        }
        for (CfetsTrdConfirmRegFeeInfoDto regFeeInfo : regFeeList) {
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            trdTradeFeeDetail.setTradeId(tradeId);
            trdTradeFeeDetail.setFeeType(regFeeInfo.getFeeType());
            trdTradeFeeDetail.setIsPayWithTrade(regFeeInfo.getIsPayWithTrade());
            trdTradeFeeDetail.setFeeAmt(regFeeInfo.getFeeAmt());
            result.add(trdTradeFeeDetail);
        }
        return result;
    }

    private List<TrdTradeFeeTotal> getTrdTradeFeeTotles(String tradeId, String totleTradeId) {
        //根据汇总交易编号查询费用信息
        List<CfetsTrdConfirmRegFeeInfoDto> regFeeList = cfetsTrdConfirmMapper.findAllRegFeeInfoList(tradeId);
        List<TrdTradeFeeTotal> ttfdList = new ArrayList<>();
        for (CfetsTrdConfirmRegFeeInfoDto regFeeInfo : regFeeList) {
            TrdTradeFeeTotal ttfd = new TrdTradeFeeTotal();
            ttfd.setTotleTradeId(totleTradeId);
            ttfd.setFeeType(regFeeInfo.getFeeType());
            ttfd.setIsPayWithTrade(regFeeInfo.getIsPayWithTrade());
            ttfd.setFeeAmt(regFeeInfo.getFeeAmt());
            ttfdList.add(ttfd);
        }
        return ttfdList;
    }

    private List<TrdTradeFeeTotal> getTrdTradeIbuFeeTotles(CfetsTrdConfirmRegInfoListResponse dto, String totleTradeId) {
        //根据汇总交易编号查询费用信息
        List<TrdTradeFeeTotal> ttfdList = new ArrayList<>();
        if (null != dto && StringUtil.isNotEmpty(dto.getTrdFeeAmt())) {
            TrdTradeFeeTotal ttfd = new TrdTradeFeeTotal();
            ttfd.setTotleTradeId(totleTradeId);
            ttfd.setFeeType(BussinessDictCodeList.费用类型_交易手续费.getValue());
            ttfd.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            ttfd.setFeeAmt(dto.getTrdFeeAmt());
            ttfdList.add(ttfd);
        }
        if (null != dto && StringUtil.isNotEmpty(dto.getSettleFeeAmt())) {
            TrdTradeFeeTotal ttfd = new TrdTradeFeeTotal();
            ttfd.setTotleTradeId(totleTradeId);
            ttfd.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
            ttfd.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            ttfd.setFeeAmt(dto.getSettleFeeAmt());
            ttfdList.add(ttfd);
        }
        return ttfdList;
    }

    private List<TrdTradeFeeDetail> 生成交易Ibu费用明细数据(CfetsRegFinProductDealDto dto, String tradeId) {
        if (!StringUtil.isNotEmpty(dto.getTrdFeeAmt()) && !StringUtil.isNotEmpty(dto.getSettleFeeAmt())) {
            return Lists.newArrayList();
        }
        List<TrdTradeFeeDetail> result = new ArrayList<>();
        //根据登记编号查询所有的费用信息
        if (StringUtil.isNotEmpty(dto.getTrdFeeAmt())) {
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            trdTradeFeeDetail.setTradeId(tradeId);
            trdTradeFeeDetail.setFeeType(BussinessDictCodeList.费用类型_交易手续费.getValue());
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            trdTradeFeeDetail.setFeeAmt(dto.getTrdFeeAmt());
            result.add(trdTradeFeeDetail);
        }
        if (StringUtil.isNotEmpty(dto.getSettleFeeAmt())) {
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            trdTradeFeeDetail.setTradeId(tradeId);
            trdTradeFeeDetail.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            trdTradeFeeDetail.setFeeAmt(dto.getSettleFeeAmt());
            result.add(trdTradeFeeDetail);
        }
        return result;
    }

    private BigDecimal getDeatil费用金额(List<CfetsTrdConfirmRegFeeInfoDto> ttfdList, String 是否随交易支付) {
        BigDecimal result = BigDecimal.ZERO;
        if (!StringUtil.isEmptyList(ttfdList)) {
            for (CfetsTrdConfirmRegFeeInfoDto trdTradeFeeDetail : ttfdList) {
                if (是否随交易支付 == null || 是否随交易支付.equals(trdTradeFeeDetail.getIsPayWithTrade())) {
                    result = ArithmeticUtil.doAdd(result, trdTradeFeeDetail.getFeeAmt());
                }
            }
        }

        return result;
    }

    private BigDecimal get费用金额(List<TrdTradeFeeTotal> ttfdList, String 是否随交易支付, BigDecimal 占比) {
        BigDecimal result = BigDecimal.ZERO;
        if (StringUtil.isEmptyList(ttfdList)) {
            return result;
        }
        for (TrdTradeFeeTotal trdTradeFeeDetail : ttfdList) {
            if (是否随交易支付 == null || 是否随交易支付.equals(trdTradeFeeDetail.getIsPayWithTrade())) {
                result = ArithmeticUtil.doAdd(result, trdTradeFeeDetail.getFeeAmt());
            }
        }
        if (占比 != null) {
            result = ArithmeticUtil.round(ArithmeticUtil.multiply(result, ArithmeticUtil.round(占比, 12, RoundingMode.HALF_UP)), 2,
                    RoundingMode.HALF_UP);
        }
        return result;
    }

    public static BussinessCodeParameter getBussinessCodeParameter(String ps) {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), ps)) {
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
        } else {
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券卖出);
        }
        return codeParameter;
    }

    /**
     * 获取投资目的
     *
     * @return 投资目的
     */
    public String getInvAim(Map<String, Object> map) {
       /* String invAim = "";
        String portfolioId = (String)map.get("portfolioId");
        PtlPortfolio ptlPortfolio = null;
        if(StringUtil.isNotEmpty(portfolioId)){
            ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(portfolioId);
        }
        invAim = BussinessDictCodeList.投资目的_公允价值变动计入当期损益.getValue();
        if(null!=ptlPortfolio){
            if(StringUtil.equals(ptlPortfolio.getProfitType(),DictCodeList.收益类型_货币型.getValue())
                    ||StringUtil.equals(ptlPortfolio.getProfitType(),DictCodeList.收益类型_收益型.getValue())){
                invAim = BussinessDictCodeList.投资目的_摊余成本类.getValue();
            }
        }*/

        return cfetsTrdConfirmMapper.selectInvAimByEntrustId(map);
    }

    public void baseTradeCommExecute(FinProductTradeDto finProductTradeDto) {
        if (finProductTradeDto == null) {
            return;
        }
        //处理CSTP下行数据
        if (finProductTradeDto.getBaseTradeProductDealDtos() != null && !finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()
                && finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal() != null &&
                StringUtil.isNotBlank(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getfTradeId())) {
            if (StringUtil.equals(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_债券类资产交易.getValue())) {
                baseTradeProductDealCommServiceImpl.updateInterfForZhaiJuan(finProductTradeDto);
            } else if (StringUtil.equals(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_质押式回购交易.getValue())) {
                baseTradeProductDealCommServiceImpl.updateInterfForZhiYaShi(finProductTradeDto);
            }
        }
        String workflowStatus = finProductTradeDto.getWorkflowStatus();
       /* if (DictCodeListExt1.交易流程状态_流程中.getValue().equals(workflowStatus)) {
            return deal流程中(finProductTradeDto);
        }
        else*/
        /* if (DictCodeListExt1.交易流程状态_有效.getValue().equals(workflowStatus)) {*/
        deal有效(finProductTradeDto);
        /* }*/
       /* else if (DictCodeListExt1.交易流程状态_撤销.getValue().equals(workflowStatus)) {
            return deal撤销(finProductTradeDto);
        }*/
        return;
    }

    private void deal有效(FinProductTradeDto finProductTradeDto) {
        if (finProductTradeDto.getBaseTradeProductDealDtos() == null
                || finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()) {
            return;
        }
        // .调用基础组件.【投资委托组件.完成委托】，写入委托完成状况信息(投资委托场合才执行此步)
        /*     if(!finProductTradeDto.isNeedHandleFinishEntrust()){*/
        baseTradeProductDealCommServiceImpl.finishEntrust(finProductTradeDto.getBaseTradeProductDealDtos(), false);
        /*}*/
        return;
    }


    /**
     * 1.8计算单位面值
     *
     * @param finprodId  金融产品代码
     * @param settleDate 交割日
     * @return 单位面值
     * @see
     * @since 1.0
     */
    public BigDecimal getUnitPrice(String finprodId, Date settleDate) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("finprodId", finprodId);
        parameters.put("settleDate", DateUtil.format(settleDate));
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getUnitPrice", parameters,
                BigDecimal.class);
    }

    /**
     * 成交确认check
     *
     * @param dto
     * @return AjaxResult
     */
    public AjaxResult trdConfirmSaveCheckMethod(CfetsTrdConfirmRegInfoListResponse dto) {
        List<CfetsTrdConfirmRegInfoListResponse> selectedItem = dto.getSelectedItem();
        AjaxResult errorRs = new AjaxResult();
        if (StringUtil.isEmptyList(selectedItem)) {
            // 未选择交易数据,请重新选择
            errorRs.setErrorList(Collections.singletonList(new ValidMessage(getMessage("COMMON_0028"))));
            return errorRs;
        }
        if (StringUtil.isNotEmpty(dto.getIsDirectTrd()) && StringUtil.equals(dto.getIsDirectTrd(), DictCodeList.是否_是.getValue())) {
            // 银行间债券交易执行是否可手工确认
            String cfets_manualconfirm_bond = CacheUtil.getSysParam("IS_CFETSSECTRD_MANUALCONFIRM_FLAG");
            // 银行间回购交易执行是否可手工确认
            String cfets_manualconfirm_repo = CacheUtil.getSysParam("IS_CFETSREPOTRD_MANUALCONFIRM_FLAG");
            // 银行间一级申购交易执行是否可手工确认
            String cfets_manualconfirm_firstBond = CacheUtil.getSysParam("IS_CFETSFIRSTSECTRD_MANUALCONFIRM_FLAG");
            if (StringUtil.isNotEmpty(selectedItem)) {
                for (CfetsTrdConfirmRegInfoListResponse item : selectedItem) {
                    if (StringUtil.equals(cfets_manualconfirm_bond, DictCodeList.是否_否.getValue())
                            && StringUtil.equals(item.getFinprodType2(), "01")
                    ) {
                        //银行间债券交易登记数据通过CSTP下行匹配进行成交匹配确认，不可手工成交确认，请重新选择！
                        errorRs.setErrorList(Collections.singletonList(new ValidMessage(getMessage("CONFIRM_MSG_002"))));
                        return errorRs;
                    }
                    if (StringUtil.equals(cfets_manualconfirm_repo, DictCodeList.是否_否.getValue())
                            && StringUtil.equals(item.getFinprodType2(), "02")
                    ) {
                        //银行间质押式回购交易登记数据通过CSTP下行匹配进行成交匹配确认，不可手工成交确认，请重新选择！
                        errorRs.setErrorList(Collections.singletonList(new ValidMessage(getMessage("CONFIRM_MSG_003"))));
                        return errorRs;
                    }
                    if (StringUtil.equals(cfets_manualconfirm_firstBond, DictCodeList.是否_否.getValue())
                            && StringUtil.equals(item.getFinprodType2(), "03")
                    ) {
                        //银行间一级申购交易登记数据通过CSTP下行匹配进行成交匹配确认，不可手工成交确认，请重新选择！
                        errorRs.setErrorList(Collections.singletonList(new ValidMessage(getMessage("CONFIRM_MSG_004"))));
                        return errorRs;
                    }
                    WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
                    wflValidInputParameter.setBusinessDataPk1(item.getTotleTradeId());
                    if (null != mstWorkflowCommonService &&
                            mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
                        //存在流程中的交易数据，请重新选择！
                        errorRs.setErrorList(Collections.singletonList(new ValidMessage(getMessage("COMMON_0027"))));
                        return errorRs;
                    }
                    wflValidInputParameter.setBusinessDataPk1(item.getTotleTradeId() + "R");
                    if (null != mstWorkflowCommonService &&
                            mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
                        //存在流程中的交易数据，请重新选择！
                        errorRs.setErrorList(Collections.singletonList(new ValidMessage(getMessage("COMMON_0027"))));
                        return errorRs;
                    }
                    //判断该数据是否已经被撤销
                    Map<String, Object> pageParam = new HashMap<>();
                    pageParam.put("totleTradeId", item.getTotleTradeId());
                    List<CfetsTrdConfirmRegInfoListResponse> flagList = cfetsTrdConfirmMapper.isExistDealInfo(pageParam);
                    if (StringUtil.isEmptyList(flagList)) {
                        //请选择有效状态的数据
                        errorRs.setErrorList(Collections.singletonList(new ValidMessage(getMessage("COMMON_0026"))));
                        return errorRs;
                    }
                    /*if(StringUtil.equals(item.getDealType(), BussinessDictCodeList.报价方式_对话报价.getValue())
                            || StringUtil.equals(item.getDealType(), BussinessDictCodeList.报价方式_请求报价.getValue())){
                        errorRs.setErrorList(Arrays.asList(new ValidMessage("上行报价数据不允许点击成交确认！")));
                        return errorRs;
                    }*/
                    if ((StringUtil.equals(item.getFinprodType2(), "01") || StringUtil.equals(item.getFinprodType2(), "02"))
                            && (StringUtil.isEmpty(item.getCounterId()) ||
                            (StringUtil.equals(item.getFinprodType2(), "01") && BigDecimalUtil.isZeroOrNull(item.getYield()))||
                            (StringUtil.equals(item.getFinprodType2(), "02") && BigDecimalUtil.isZeroOrNull(item.getTradeYield())))) {
                        //价格or对手方为空，不可手工成交
                        errorRs.setErrorList(Collections.singletonList(new ValidMessage(getMessage("CONFIRM_MSG_001"))));
                        return errorRs;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 发行失败 check
     *
     * @param dto
     * @return AjaxResult
     */
    public AjaxResult issuingFailCheckMethod(CfetsTrdConfirmRegInfoListResponse dto) {
        logger.info("发行失败check,入参:" + JsonUtils.object2Json(dto));
        List<ValidMessage> messages = new ArrayList<>();
        List<CfetsTrdConfirmRegInfoListResponse> selectedItem = dto.getSelectedItem();
        AjaxResult errorRs = new AjaxResult();
        if (StringUtil.isEmptyList(selectedItem)) {
            // 未选择交易数据,请重新选择
            errorRs.setErrorList(Collections.singletonList(new ValidMessage(getMessage("COMMON_0028"))));
            return errorRs;
        }
        if (StringUtil.isNotEmpty(selectedItem)) {
            for (CfetsTrdConfirmRegInfoListResponse item : selectedItem) {
                if (!StringUtil.equals(item.getFinprodType2(), "03")) {
                    // 仅支持对一级申购数据进行发行失败操作！
                    errorRs.setErrorList(Collections.singletonList(new ValidMessage(getMessage("CONFIRM_MSG_005"))));
                    return errorRs;
                }
                List<String> list = cfetsTrdConfirmMapper.selectWflBidDataByFinprodId(item.getFinprodId());
                if (CollectionUtils.isNotEmpty(list) && list.size() > 0) {
                    // 存在流程中指令/申购数据，请重新选择！
                    errorRs.setErrorList(Collections.singletonList(new ValidMessage(getMessage("CONFIRM_MSG_006"))));
                    return errorRs;
                }
                FinProductAdd issue = cfetsTrdConfirmMapper.selectIssueInfoByFinprodId(item.getFinprodId());
                if (StringUtil.isNotEmpty(issue) && StringUtil.equals(issue.getIssueStatus(), DictCodeListExt1.发行状态_发行成功.getValue())) {
                    // 资产【xx(xx)】为发行成功状态，请重新选择！
                    errorRs.setErrorList(Collections.singletonList(new ValidMessage(getMessage("CONFIRM_MSG_007", item.getFinprodAbbr(), item.getFinprodMarketId()))));
                    return errorRs;
                }
            }
        }
        return null;
    }

    /**
     * CSTP成交回报列表分页查询
     *
     * @param dto
     * @return Page
     * @see
     * @since 1.0
     */
    public Page<CfetsTrdConfirmCstpInfoListResponse> findAllCstpInfoList(CfetsTrdConfirmCstpInfoListRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        String finprodId = (String) pageParam.get("finprodId");
        if (StringUtil.isNotEmpty(finprodId)) {
            pageParam.put("finprodIdList", StringUtil.splitToList(GlobalConstant.COMMA, finprodId));
        }
        String acctNo = (String) pageParam.get("acctNo");
        if (StringUtil.isNotEmpty(acctNo)) {
            pageParam.put("acctNoList", StringUtil.splitToList(GlobalConstant.COMMA, acctNo));
        } else {
            String secAcctId = (String) pageParam.get("secAcctId");
            if (StringUtil.isNotEmpty(secAcctId)) {
                pageParam.put("acctNoList", StringUtil.splitToList(GlobalConstant.COMMA, secAcctId));
            }
        }
        String traderid = (String) pageParam.get("traderid");
        if (StringUtil.isNotEmpty(traderid)) {
            pageParam.put("traderidList", StringUtil.splitToList(GlobalConstant.COMMA, traderid));
        }
        String counterTrader = (String) pageParam.get("counterTrader");
        if (StringUtil.isNotEmpty(counterTrader)) {
            pageParam.put("counterTraderList", StringUtil.splitToList(GlobalConstant.COMMA, counterTrader));
        }
        pageParam.put("sysDate", iPlatformDate.getSysDate());
        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.parse(DateUtil.getSysDate()));
        int year = c.get(Calendar.YEAR);
        pageParam.put("year", year);
        pageParam.put("sort", dto.getPageMode().getSort());
        pageParam.put("direction", dto.getPageMode().getDirection());
        //针对查询可匹配成交回报
        if (!StringUtil.isEmptyList(dto.getSelectedItem())) {
            CfetsTrdConfirmRegInfoListResponse serchCstpDto = dto.getSelectedItem().get(0);
            pageParam.put("isSearchMatch", DictCodeList.是否_是.getValue());
            //针对报价
            if (StringUtil.isNotEmpty(serchCstpDto.getDealType())
                    && (StringUtil.equals(serchCstpDto.getDealType(), BussinessDictCodeList.交易类型_请求报价.getValue())
                    || StringUtil.equals(serchCstpDto.getDealType(), BussinessDictCodeList.交易类型_对话报价.getValue()))
            ) {
                pageParam.put("execid", serchCstpDto.getExecid());
            }
            if (StringUtil.equals(serchCstpDto.getFinprodType2(), "01")) {
                // 手工匹配元素查询
                Map<String, MatchDetFeatures> map = matchFeaturesService.queryManualMatchFeatures("BOND");
                //基础要素：成交日期、资产代码、交易场所、交易方向、清算速度、证券账户、成交价（净价/到期收益率/行权收益率）、券面总额、结算日期、结算方式、结算金额、对手方
                if (map.get("tradeDate") != null && StringUtil.equals(map.get("tradeDate").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getTradeDate())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    // 成交日期
                    pageParam.put("tradeDate", serchCstpDto.getTradeDate());
                }
                if (map.get("finprodMarketId") != null && StringUtil.equals(map.get("finprodMarketId").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getFinprodMarketId())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    // 资产代码
                    pageParam.put("finprodMarketId", serchCstpDto.getFinprodMarketId());
                }
                if (map.get("ps") != null && StringUtil.equals(map.get("ps").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getPs())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    // 交易方向
                    pageParam.put("ps", serchCstpDto.getPs());
                }
                if (map.get("settleSpeed") != null && StringUtil.equals(map.get("settleSpeed").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getSettleSpeed())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    // 清算速度
                    pageParam.put("settleSpeed", serchCstpDto.getSettleSpeed());
                }
                if (map.get("secManageAcctId") != null && StringUtil.equals(map.get("secManageAcctId").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getSecManageAcctId())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    // 证券账户
                    pageParam.put("secManageAcctId", serchCstpDto.getSecManageAcctId());
                }
                if (map.get("yield") != null && StringUtil.equals(map.get("yield").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getYield())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    // 成交价 净价/到期收益率
                    pageParam.put("yield", serchCstpDto.getYield());
                    pageParam.put("unitCprice", serchCstpDto.getUnitCprice());
                }
                if (map.get("faceValue") != null && StringUtil.equals(map.get("faceValue").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getFaceValue())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    // 券面总额
                    pageParam.put("faceValue", serchCstpDto.getFaceValue());
                }
                if (map.get("settleDate") != null && StringUtil.equals(map.get("settleDate").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getSettleDate())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    // 结算日期
                    pageParam.put("settleDate", serchCstpDto.getSettleDate());
                }
                if (map.get("deliveryType") != null && StringUtil.equals(map.get("deliveryType").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getDeliveryType())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    // 结算方式
                    pageParam.put("deliveryType", serchCstpDto.getDeliveryType());
                }
                if (map.get("settleAtm") != null && StringUtil.equals(map.get("settleAtm").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getTradeAmt())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    // 结算金额
                    pageParam.put("tradeAmt", serchCstpDto.getTradeAmt());
                }
                if (map.get("counterId") != null && StringUtil.equals(map.get("counterId").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getCpPartyId())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    // 对手方
                    pageParam.put("counterId", serchCstpDto.getCpPartyId());
                }
            } else if (StringUtil.equals(serchCstpDto.getFinprodType2(), "02")) {
                // 手工匹配元素查询
                Map<String, MatchDetFeatures> map = matchFeaturesService.queryManualMatchFeatures("REPO");
                //基础要素：成交日期、回购期限、交易场所、交易方向、清算速度、证券账户、回购利率、首次结算日、到期结算日、交易金额、到期结算金额、对手方
                // 质押债券代码、质押债券简称、质押债券折算比例、质押债券券面总额
                if (map.get("tradeDate") != null && StringUtil.equals(map.get("tradeDate").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getTradeDate())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //成交日期
                    pageParam.put("tradeDate", serchCstpDto.getTradeDate());
                }
                if (map.get("term") != null && StringUtil.equals(map.get("term").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getTermDays())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //回购期限
                    pageParam.put("term", serchCstpDto.getTermDays());
                }
                if (map.get("ps") != null && StringUtil.equals(map.get("ps").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getPs())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //交易方向
                    pageParam.put("ps", serchCstpDto.getPs());
                }
                if (map.get("settleSpeed") != null && StringUtil.equals(map.get("settleSpeed").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getSettleSpeed())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //清算速度
                    pageParam.put("settleSpeed", serchCstpDto.getSettleSpeed());
                }
                if (map.get("secManageAcctId") != null && StringUtil.equals(map.get("secManageAcctId").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getSecManageAcctId())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //证券账户
                    pageParam.put("secManageAcctId", serchCstpDto.getSecManageAcctId());
                }
                if (map.get("rate") != null && StringUtil.equals(map.get("rate").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getRate())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //回购利率
                    pageParam.put("rate", serchCstpDto.getRate());
                }
                if (map.get("vsettleDate") != null && StringUtil.equals(map.get("vsettleDate").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getVsettleDate())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //首次结算日
                    pageParam.put("vsettleDate", serchCstpDto.getVsettleDate());
                }
                if (map.get("msettleDate") != null && StringUtil.equals(map.get("msettleDate").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getMsettleDate())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //到期结算日
                    pageParam.put("msettleDate", serchCstpDto.getMsettleDate());
                }
                if (map.get("tradeAmt") != null && StringUtil.equals(map.get("tradeAmt").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getTradeAmt())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //首次结算金额
                    pageParam.put("tradeAmt", serchCstpDto.getTradeAmt());
                }
                if (map.get("mTradeAmt") != null && StringUtil.equals(map.get("mTradeAmt").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getMtradeAmt())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //到期结算金额
                    pageParam.put("mTradeAmt", serchCstpDto.getMtradeAmt());
                }
                if (map.get("counterId") != null && StringUtil.equals(map.get("counterId").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getCpPartyId())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //对手方
                    pageParam.put("counterId", serchCstpDto.getCpPartyId());
                }
                if (map.get("pledgeFinprodIds") != null && StringUtil.equals(map.get("pledgeFinprodIds").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getPledgeFinprodIds())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //质押券信息（债券代码）
                    pageParam.put("pledgeFinprodIds", serchCstpDto.getPledgeFinprodIds());
                }
                if (map.get("pledgeRatios") != null && StringUtil.equals(map.get("pledgeRatios").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getPledgeRatios())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //质押券信息（折算比例）
                    pageParam.put("pledgeRatios", serchCstpDto.getPledgeRatios());
                }
                if (map.get("pledgeFaceValues") != null && StringUtil.equals(map.get("pledgeFaceValues").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getPledgeFaceValues())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //质押券信息（券面总额）
                    pageParam.put("pledgeFaceValues", serchCstpDto.getPledgeFaceValues());
                }
            } else if (StringUtil.equals(serchCstpDto.getFinprodType2(), "03")) {
                // 手工匹配元素查询
                Map<String, MatchDetFeatures> map = matchFeaturesService.queryManualMatchFeatures("BID");
                // 一级申购 成交日期、资产代码、交易场所、认购量、应缴款金额、发行价格、本方、本方交易员
                if (map.get("tradeDate") != null && StringUtil.equals(map.get("tradeDate").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getTradeDate())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //成交日期
                    pageParam.put("tradeDate", DateUtil.format(serchCstpDto.getTradeDate()));
                }
                if (map.get("finprodMarketId") != null && StringUtil.equals(map.get("finprodMarketId").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getFinprodMarketId())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //资产代码
                    pageParam.put("finprodMarketId", StringUtil.substring(serchCstpDto.getFinprodMarketId(), 0, serchCstpDto.getFinprodMarketId().length() - 3));
                }
                if (map.get("ouPartyId") != null && StringUtil.equals(map.get("ouPartyId").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getOuPartyId())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //本方
                    pageParam.put("ouPartyId", serchCstpDto.getOuPartyId());
                }
                if (map.get("subscriptionAmount") != null && StringUtil.equals(map.get("subscriptionAmount").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getShareAmt())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //认购量[亿元(小数点后保留1位)]
                    pageParam.put("subscriptionAmount", ArithmeticUtil.divide(serchCstpDto.getShareAmt(), new BigDecimal(100000000), 1));
                }
                if (map.get("issuePrice") != null && StringUtil.equals(map.get("issuePrice").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getOtherAmt())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //发行价格[元(小数点后保留2位)]
                    pageParam.put("issuePrice", ArithmeticUtil.round(serchCstpDto.getOtherAmt(), 2, RoundingMode.HALF_UP));
                }
                // 一级申购 可选要素：本方交易员、应缴款金额
                if (map.get("ourTrader") != null && StringUtil.equals(map.get("ourTrader").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getTrader())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    pageParam.put("ourTrader", serchCstpDto.getTrader());//本方交易员
                }
                if (map.get("payableAmount") != null && StringUtil.equals(map.get("payableAmount").getType(), "01")) {
                    if(!StringUtil.isNotEmpty(serchCstpDto.getTradeAmt())){
                        logger.info("基础要素为空值不查询匹配");
                        return null;
                    }
                    //应缴款金额[元(小数点后保留2位)]
                    pageParam.put("payableAmount", ArithmeticUtil.round(serchCstpDto.getTradeAmt(), 2, RoundingMode.HALF_UP));
                }
            }
        }
        //交易员是否可查看全部CSTP下行成交数据 IS_QUERYALLCSTP_FLAG
        String queryAllTrdFlag = CacheUtil.getSysParam("IS_QUERYALLCSTP_FLAG");
        if (StringUtil.equals(queryAllTrdFlag, DictCodeList.是否_否.getValue())) {
            String userId = SecurityUtil.getLoginUser().getUserName();
            if (StringUtils.isNotEmpty(userId)) {
                //判断角色配置
                List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
                if (roleList.contains(DictCodeList.角色岗位_交易员.getValue())) {
                    //交易员
                    pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
                }
            }
        }
        //分品种区别是否支持CSTP下行接口
        pageParam.put("bondSupportFlag", CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BOND"));
        pageParam.put("repoSupportFlag", CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_REPO"));
        pageParam.put("bidSupportFlag", CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BONDFIRST"));
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdConfirmMapper.findAllCstpInfoList(pageParam));
    }

    /**
     * 保存提交时check检查
     *
     * @param manualMatchDto
     * @return AjaxResult
     */
    public AjaxResult checkCstpManualMatch(CfetsTrdConfirmManualMatchInfoRequest manualMatchDto) {
        AjaxResult mes = new AjaxResult();
        if (null == manualMatchDto) {
            // 未选择交易执行数据/CSTP成交回报数据！
            mes.setErrorList(Collections.singletonList(new ValidMessage(getMessage("CONFIRM_MSG_010"))));
            return mes;
        }
        if (StringUtil.isEmptyList(manualMatchDto.getSelectedCfetsTrdConfirmCstpInfoList())
                || StringUtil.isEmptyList(manualMatchDto.getSelectedCfetsTrdConfirmRegInfoList())) {
            // 未选择交易执行数据/CSTP成交回报数据！
            mes.setErrorList(Collections.singletonList(new ValidMessage(getMessage("CONFIRM_MSG_010"))));
            return mes;
        }
        if (manualMatchDto.getSelectedCfetsTrdConfirmCstpInfoList().size() > 1
                || manualMatchDto.getSelectedCfetsTrdConfirmRegInfoList().size() > 1) {
            // 不支持多条交易执行数据/CSTP成交回报数据进行匹配，请重新选择！
            mes.setErrorList(Collections.singletonList(new ValidMessage(getMessage("CONFIRM_MSG_011"))));
            return mes;
        }
        if (manualMatchDto.getSelectedCfetsTrdConfirmCstpInfoList().size() == 1
                && manualMatchDto.getSelectedCfetsTrdConfirmRegInfoList().size() == 1) {
            //进行匹配的数据
            CfetsTrdConfirmRegInfoListResponse regInfo = manualMatchDto.getSelectedCfetsTrdConfirmRegInfoList().get(0);
            CfetsTrdConfirmCstpInfoListResponse cstpInfo = manualMatchDto.getSelectedCfetsTrdConfirmCstpInfoList().get(0);
            //如果是报价数据，则要求成交编号一致，否则不允许进行匹配
            if (StringUtil.isNotEmpty(regInfo.getDealType())
                    && (StringUtil.equals(regInfo.getDealType(), BussinessDictCodeList.交易类型_请求报价.getValue())
                    || StringUtil.equals(regInfo.getDealType(), BussinessDictCodeList.交易类型_对话报价.getValue()))
            ) {
                if (!StringUtil.equals(regInfo.getExecid(), cstpInfo.getExecid())) {
                    // CSTP下行成交回报数据中的成交编号与交易执行数据中交易类型为对话/请求的报价数据的成交编号不一致，请重新选择
                    mes.setErrorList(Collections.singletonList(new ValidMessage(getMessage("CONFIRM_MSG_012"))));
                    return mes;
                }
            }
            // 手工匹配要素
            Map<String, MatchDetFeatures> matchDetFeaturesMap = matchFeaturesService.queryManualMatchFeatures(cstpInfo.getBusinesstype());
            logger.info("登记要素:{}" , JsonUtils.object2Json(regInfo) );
            logger.info("下行数据:{}" , JsonUtils.object2Json(cstpInfo));
            //判断基础要素是否一致
            if (StringUtil.isNotEmpty(cstpInfo.getBusinesstype()) && StringUtil.equals(cstpInfo.getBusinesstype(), "BOND")
                    && this.secMatchCheck(regInfo, cstpInfo, mes, matchDetFeaturesMap)) {
                return mes;
            } else if (StringUtil.isNotEmpty(cstpInfo.getBusinesstype()) && StringUtil.equals(cstpInfo.getBusinesstype(), "REPO")
                    && this.repoMatchCheck(regInfo, cstpInfo, mes, matchDetFeaturesMap)) {
                return mes;
            } else if (StringUtil.isNotEmpty(cstpInfo.getBusinesstype()) && StringUtil.equals(cstpInfo.getBusinesstype(), "BID")
                    && this.bidMatchCheck(regInfo, cstpInfo, mes, matchDetFeaturesMap)) {
                return mes;
            }
        }
        return null;
    }

    /**
     * 债券匹配要素校验
     *
     * @author jiangpeng
     * @date 2022/9/28 11:21
     */
    private boolean secMatchCheck(CfetsTrdConfirmRegInfoListResponse regInfo,
                                  CfetsTrdConfirmCstpInfoListResponse cstpInfo, AjaxResult mes, Map<String, MatchDetFeatures> matchMap) {

        StringBuilder str = new StringBuilder();
        //判断可选要素是否一致
        StringBuilder validateStr = new StringBuilder();
        // 成交日期
        if (regInfo.getTradeDate().compareTo(cstpInfo.getTradeDate()) != 0) {
            this.buildMassege(matchMap, "tradeDate",
                    str, "matchDetFeatures.tradeDate",
                    validateStr, "CONFIRM_MSG_013", DateUtil.format(cstpInfo.getTradeDate()), DateUtil.format(regInfo.getTradeDate()));
        }
        // 资产代码
        if (!StringUtil.equals(regInfo.getFinprodMarketId(), cstpInfo.getFinprodMarketId())) {
            this.buildMassege(matchMap, "finprodMarketId",
                    str, "matchDetFeatures.finprodMarketId",
                    validateStr, "CONFIRM_MSG_013", cstpInfo.getFinprodMarketId(), regInfo.getFinprodMarketId());
        }
        // 交易场所
        if (!StringUtil.equals(regInfo.getTradeMarket(), BussinessDictCodeList.交易场所_银行间.getValue())) {
            this.buildMassege(matchMap, "tradeMarket",
                    str, "matchDetFeatures.tradeMarket",
                    validateStr, "CONFIRM_MSG_013", BussinessDictCodeList.交易场所_银行间.getCodeName(), regInfo.getTradeMarketName());
        }
        // 交易方向
        if (!StringUtil.equals(regInfo.getPs(), cstpInfo.getPs())) {
            this.buildMassege(matchMap, "ps",
                    str, "matchDetFeatures.ps",
                    validateStr, "CONFIRM_MSG_013",
                    SysDataUtil.getNameFromDataDict(DictCode.交易方向, cstpInfo.getPs()), SysDataUtil.getNameFromDataDict(DictCode.交易方向, regInfo.getPs()));
        }
        // 清算速度
        if (!StringUtil.equals(regInfo.getSettleSpeed(), cstpInfo.getSettleSpeed())) {
            this.buildMassege(matchMap, "settleSpeed",
                    str, "matchDetFeatures.settleSpeed",
                    validateStr, "CONFIRM_MSG_013", cstpInfo.getSettleSpeedName(), regInfo.getSettleSpeedName());
        }
        // 证券账户
        if (!StringUtil.equals(regInfo.getSecManageAcctId(), cstpInfo.getAcctNo())) {
            this.buildMassege(matchMap, "secManageAcctId",
                    str, "matchDetFeatures.secManageAcctId",
                    validateStr, "CONFIRM_MSG_013", cstpInfo.getAcctNo(), regInfo.getSecManageAcctId());
        }
        // 净价/到期收益率
        if (StringUtil.isNotEmpty(regInfo.getYield()) && StringUtil.isNotEmpty(cstpInfo.getTradeYield())
                && BigDecimalUtil.ACompareToB(regInfo.getYield(), cstpInfo.getTradeYield(), 4) != 0) {
            DecimalFormat df = new DecimalFormat("#,##0.0000");
            this.buildMassege(matchMap, "yield",
                    str, "matchDetFeatures.yield",
                    validateStr, "CONFIRM_MSG_014", df.format(ArithmeticUtil.round(cstpInfo.getTradeYield(), 4, RoundingMode.HALF_UP))+"%",
                    df.format(ArithmeticUtil.round(regInfo.getYield(), 4, RoundingMode.HALF_UP))+"%");
        }
        // 券面总额
        if (StringUtil.isNotEmpty(regInfo.getFaceValue()) && StringUtil.isNotEmpty(cstpInfo.getLastqty())
                && regInfo.getFaceValue().compareTo(cstpInfo.getLastqty()) != 0) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            this.buildMassege(matchMap, "faceValue",
                    str, "matchDetFeatures.faceValue",
                    validateStr, "CONFIRM_MSG_014", df.format(ArithmeticUtil.round(cstpInfo.getLastqty(), 2, RoundingMode.HALF_UP)),
                    df.format(ArithmeticUtil.round(regInfo.getFaceValue(), 2, RoundingMode.HALF_UP)));
        }
        // 结算日期
        if (StringUtil.isNotEmpty(regInfo.getSettleDate()) && StringUtil.isNotEmpty(cstpInfo.getSettleDate())
                && regInfo.getSettleDate().compareTo(cstpInfo.getSettleDate()) != 0) {
            this.buildMassege(matchMap, "settleDate",
                    str, "matchDetFeatures.settleDate",
                    validateStr, "CONFIRM_MSG_013", DateUtil.format(cstpInfo.getSettleDate()), DateUtil.format(regInfo.getSettleDate()));
        }
        // 结算方式
        if (!StringUtil.equals(regInfo.getDeliveryType(), cstpInfo.getDeliveryType())) {
            this.buildMassege(matchMap, "deliveryType",
                    str, "matchDetFeatures.deliveryType",
                    validateStr, "CONFIRM_MSG_013",
                    SysDataUtil.getNameFromDataDict(DictCode.结算方式, cstpInfo.getDeliveryType())
                    , SysDataUtil.getNameFromDataDict(DictCode.结算方式, regInfo.getDeliveryType()));
        }
        // 结算金额
        if (StringUtil.isNotEmpty(regInfo.getTradeAmt()) && StringUtil.isNotEmpty(cstpInfo.getTradeAmt())
                && BigDecimalUtil.ACompareToB(regInfo.getTradeAmt(), cstpInfo.getTradeAmt(), 2) != 0) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            this.buildMassege(matchMap, "settleAtm",
                    str, "matchDetFeatures.settleAtm",
                    validateStr, "CONFIRM_MSG_014", df.format(ArithmeticUtil.round(cstpInfo.getTradeAmt(), 2, RoundingMode.HALF_UP)),
                    df.format(ArithmeticUtil.round(regInfo.getTradeAmt(), 2, RoundingMode.HALF_UP)));
        }
        // 对手方
        if (!StringUtil.equals(regInfo.getCpPartyId(), cstpInfo.getCounterId())) {
            this.buildMassege(matchMap, "counterId",
                    str, "matchDetFeatures.counterId",
                    validateStr, "CONFIRM_MSG_013", cstpInfo.getCounterIdName(), regInfo.getCounterIdName());
        }
        if (StringUtil.isNotEmpty(str) && str.length() > 0) {
            str.append(getMessage("CONFIRM_MSG_015"));
            mes.setErrorList(Collections.singletonList(new ValidMessage(str.toString())));
            return true;
        }
        if (StringUtil.isNotEmpty(validateStr) && validateStr.length() > 0) {
            validateStr.append(getMessage("CONFIRM_MSG_016"));
            mes.setMessage(validateStr.toString(), MessageType.确认消息);
            return true;
        }
        return false;
    }

    /**
     * 回购匹配要素校验
     * <p>质押式回购：成交日期、回购期限、交易场所、交易方向、清算速度、证券账户、回购利率、首次结算日、到期结算日、交易金额、
     * 到期结算金额、质押债券代码、质押债券简称、质押债券折算比例、质押债券券面总额、对手方，
     * 其中仅回购利率、到期结算金额、对手方可修改勾选状态，其他要素禁用不可修改
     *
     * @author jiangpeng
     * @date 2022/9/28 11:21
     */
    private boolean repoMatchCheck(CfetsTrdConfirmRegInfoListResponse regInfo,
                                   CfetsTrdConfirmCstpInfoListResponse cstpInfo, AjaxResult mes, Map<String, MatchDetFeatures> matchMap) {
        // 判断基础要素是否一致
        StringBuilder str = new StringBuilder();
        // 判断可选要素是否一致
        StringBuilder validateStr = new StringBuilder();
        // 回购期限
        if (StringUtil.isNotEmpty(regInfo.getTermDays()) && StringUtil.isNotEmpty(cstpInfo.getTermDays())
                && regInfo.getTermDays().compareTo(cstpInfo.getTermDays()) != 0) {
            this.buildMassege(matchMap, "termDays",
                    str, "matchDetFeatures.termDays",
                    validateStr, "CONFIRM_MSG_013", cstpInfo.getTermDays().toString(), regInfo.getTermDays().toString());
        }
        // 交易场所
        if (!StringUtil.equals(regInfo.getTradeMarket(), BussinessDictCodeList.交易场所_银行间.getValue())) {
            this.buildMassege(matchMap, "tradeMarket",
                    str, "matchDetFeatures.tradeMarket",
                    validateStr, "CONFIRM_MSG_013", BussinessDictCodeList.交易场所_银行间.getCodeName(), regInfo.getTradeMarketName());
        }
        // 交易方向
        if (!StringUtil.equals(regInfo.getPs(), cstpInfo.getPs())) {
            this.buildMassege(matchMap, "ps",
                    str, "matchDetFeatures.ps",
                    validateStr, "CONFIRM_MSG_013", cstpInfo.getPsName(), regInfo.getPsName());
        }
        // 清算速度
        if (!StringUtil.equals(regInfo.getSettleSpeed(), cstpInfo.getSettleSpeed())) {
            this.buildMassege(matchMap, "settleSpeed",
                    str, "matchDetFeatures.settleSpeed",
                    validateStr, "CONFIRM_MSG_013", cstpInfo.getSettleSpeedName(), regInfo.getSettleSpeedName());
        }

        // 证券账户
        if (!StringUtil.equals(regInfo.getSecManageAcctId(), cstpInfo.getAcctNo())) {
            this.buildMassege(matchMap, "secManageAcctId",
                    str, "matchDetFeatures.secManageAcctId",
                    validateStr, "CONFIRM_MSG_013", cstpInfo.getAcctNo(), regInfo.getSecManageAcctId());
        }
        // 回购利率
        if (StringUtil.isNotEmpty(regInfo.getRate()) && StringUtil.isNotEmpty(cstpInfo.getTradeYield())
                && BigDecimalUtil.ACompareToB(regInfo.getRate(), cstpInfo.getTradeYield(), 4) != 0) {
            DecimalFormat df = new DecimalFormat("#,##0.0000");
            this.buildMassege(matchMap, "rate",
                    str, "matchDetFeatures.rate",
                    validateStr, "CONFIRM_MSG_014", df.format(cstpInfo.getTradeYield()) + "%",
                    df.format(regInfo.getRate()) + "%");
        }
        // 首次结算日
        if (StringUtil.isNotEmpty(regInfo.getVsettleDate()) && StringUtil.isNotEmpty(cstpInfo.getVsettleDate())
                && regInfo.getVsettleDate().compareTo(cstpInfo.getVsettleDate()) != 0) {
            this.buildMassege(matchMap, "vsettleDate",
                    str, "matchDetFeatures.vsettleDate",
                    validateStr, "CONFIRM_MSG_013", cstpInfo.getSettleSpeedName(), regInfo.getSettleSpeedName());
        }
        // 到期结算日
        if (StringUtil.isNotEmpty(regInfo.getMsettleDate()) && StringUtil.isNotEmpty(cstpInfo.getMsettleDate())
                && regInfo.getMsettleDate().compareTo(cstpInfo.getMsettleDate()) != 0) {
            this.buildMassege(matchMap, "msettleDate",
                    str, "matchDetFeatures.msettleDate",
                    validateStr, "CONFIRM_MSG_013", DateUtil.format(cstpInfo.getMsettleDate()), DateUtil.format(regInfo.getMsettleDate()));
        }
        // 首次结算金额
        if (StringUtil.isNotEmpty(regInfo.getTradeAmt()) && StringUtil.isNotEmpty(cstpInfo.getTradeAmt())
                && regInfo.getTradeAmt().compareTo(cstpInfo.getTradeAmt()) != 0) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            this.buildMassege(matchMap, "tradeAmt",
                    str, "matchDetFeatures.tradeAmt",
                    validateStr, "CONFIRM_MSG_013", df.format(ArithmeticUtil.round(cstpInfo.getTradeAmt(), 2, RoundingMode.HALF_UP)),
                    df.format(ArithmeticUtil.round(regInfo.getTradeAmt(), 2, RoundingMode.HALF_UP)));
        }
        // 到期结算金额
        if (StringUtil.isNotEmpty(regInfo.getMtradeAmt()) && StringUtil.isNotEmpty(cstpInfo.getMtradeAmt())
                && regInfo.getMtradeAmt().compareTo(cstpInfo.getMtradeAmt()) != 0) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            this.buildMassege(matchMap, "mTradeAmt",
                    str, "matchDetFeatures.mTradeAmt",
                    validateStr, "CONFIRM_MSG_014", df.format(ArithmeticUtil.round(cstpInfo.getMtradeAmt(), 2, RoundingMode.HALF_UP)),
                    df.format(ArithmeticUtil.round(regInfo.getMtradeAmt(), 2, RoundingMode.HALF_UP)));
        }
        // 对手方
        if (!StringUtil.equals(regInfo.getCpPartyId(), cstpInfo.getCounterId())) {
            this.buildMassege(matchMap, "counterId",
                    str, "matchDetFeatures.counterId",
                    validateStr, "CONFIRM_MSG_013", cstpInfo.getCounterIdName(), regInfo.getCounterIdName());
        }
        StringBuilder zyMes = new StringBuilder();
        //查询登记或报价的质押券信息
        List<CfetsTrdConfirmRegInfoListResponse>
                regAndIbuPledges = cfetsTrdConfirmMapper.findAllRegAndIbuPledgesList(regInfo.getTotleTradeId());
        //查询cstp数据的质押券信息
        List<CfetsTrdConfirmCstpInfoListResponse>
                cstpPledges = cfetsTrdConfirmMapper.findAllCstpPledgesList(cstpInfo.getExecid());
        if ((StringUtil.isEmptyList(regAndIbuPledges) && !StringUtil.isEmptyList(cstpPledges))
                || (!StringUtil.isEmptyList(regAndIbuPledges) && StringUtil.isEmptyList(cstpPledges))
        ) {
            str.append(",质押券信息");
        }
        if (!StringUtil.isEmptyList(regAndIbuPledges) && !StringUtil.isEmptyList(cstpPledges)) {
            boolean flag = false;
            for (int i = 0; i < regAndIbuPledges.size(); i++) {
                CfetsTrdConfirmRegInfoListResponse regAndIbuDemo = regAndIbuPledges.get(i);
                CfetsTrdConfirmCstpInfoListResponse cstpDemo = cstpPledges.get(i);
                if (!StringUtil.equals(regAndIbuDemo.getPledgeFinprodId(), cstpDemo.getPledgeFinprodId())) {
                    this.buildMassege(matchMap, "pledgeFinprodId",
                            str, "matchDetFeatures.pledgeFinprodId",
                            validateStr, "CONFIRM_MSG_013", cstpInfo.getSettleSpeedName(), regInfo.getSettleSpeedName());
                    break;
                }
                if (StringUtil.isNotEmpty(regAndIbuDemo.getPledgeRatio()) && StringUtil.isNotEmpty(cstpDemo.getPledgeRatio())
                        && regAndIbuDemo.getPledgeRatio().compareTo(cstpDemo.getPledgeRatio()) != 0) {
                    this.buildMassege(matchMap, "pledgeRatio",
                            str, "matchDetFeatures.pledgeRatio",
                            validateStr, "CONFIRM_MSG_013", cstpInfo.getSettleSpeedName(), regInfo.getSettleSpeedName());
                    flag = true;
                }
                if (StringUtil.isNotEmpty(regAndIbuDemo.getPledgeFaceValue()) && StringUtil.isNotEmpty(cstpDemo.getPledgeFaceValue())
                        && regAndIbuDemo.getPledgeFaceValue().compareTo(cstpDemo.getPledgeFaceValue()) != 0) {
                    this.buildMassege(matchMap, "pledgeFaceValue",
                            str, "matchDetFeatures.pledgeFaceValue",
                            validateStr, "CONFIRM_MSG_013", cstpInfo.getSettleSpeedName(), regInfo.getSettleSpeedName());
                    flag = true;
                }
                if(flag){
                    break ;
                }
            }
            if (!StringUtil.isNotEmpty(zyMes) && zyMes.length() > 0) {
                str.append("质押券信息（").append(zyMes.toString().substring(1)).append("）");
            }
        }
        if (StringUtil.isNotEmpty(str) && str.length() > 0) {
            str.append(getMessage("CONFIRM_MSG_015"));
            mes.setErrorList(Collections.singletonList(new ValidMessage(str.toString())));
            return true;

        }
        if (StringUtil.isNotEmpty(validateStr) && validateStr.length() > 0) {
            validateStr.append(getMessage("CONFIRM_MSG_016"));
            mes.setMessage(validateStr.toString(), MessageType.确认消息);
            return true;
        }
        return false;
    }

    /**
     * 一级申购匹配要素校验
     * <p>成交日期、资产代码、交易场所、认购量、应缴款金额、发行价格、本方、本方交易员，
     * 其中仅应缴款金额、本方交易员可修改勾选状态，其他要素禁用不可修改
     *
     * @author jiangpeng
     * @date 2022/9/28 11:21
     */
    private boolean bidMatchCheck(CfetsTrdConfirmRegInfoListResponse regInfo, CfetsTrdConfirmCstpInfoListResponse cstpInfo,
                                  AjaxResult mes, Map<String, MatchDetFeatures> matchMap) {
        // 判断基础要素是否一致
        StringBuilder str = new StringBuilder();
        // 判断可选要素是否一致
        StringBuilder validateStr = new StringBuilder();
        // 成交日期
        if (DateUtil.compareTo(cstpInfo.getTradeDate(), regInfo.getTradeDate()) != 0) {
            this.buildMassege(matchMap, "tradeDate",
                    str, "matchDetFeatures.tradeDate",
                    validateStr, "CONFIRM_MSG_013", DateUtil.format(cstpInfo.getTradeDate()), DateUtil.format(regInfo.getTradeDate()));
        }
        // 资产代码
        if (!StringUtil.equals(cstpInfo.getFinprodId(), regInfo.getFinprodId())) {
            this.buildMassege(matchMap, "finprodMarketId",
                    str, "matchDetFeatures.finprodMarketId",
                    validateStr, "CONFIRM_MSG_013", cstpInfo.getFinprodId(), regInfo.getFinprodId());
        }
        // 交易场所
        if (!StringUtil.equals(BussinessDictCodeList.交易场所_银行间.getValue(), regInfo.getTradeMarket())) {
            this.buildMassege(matchMap, "tradeMarket",
                    str, "matchDetFeatures.tradeMarket",
                    validateStr, "CONFIRM_MSG_013", BussinessDictCodeList.交易场所_银行间.getValue(), regInfo.getTradeMarketName());
        }
        // 认购量
        if (StringUtil.isNotEmpty(regInfo.getFaceValue()) && StringUtil.isNotEmpty(cstpInfo.getLastqty())
                && regInfo.getFaceValue().compareTo(cstpInfo.getLastqty()) != 0) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            this.buildMassege(matchMap, "subscriptionAmount",
                    str, "matchDetFeatures.subscriptionAmount",
                    validateStr, "CONFIRM_MSG_014", df.format(ArithmeticUtil.round(cstpInfo.getLastqty(), 2, RoundingMode.HALF_UP)),
                    df.format(ArithmeticUtil.round(regInfo.getFaceValue(), 2, RoundingMode.HALF_UP)));
        }
        // 应缴款金额
        if (StringUtil.isNotEmpty(regInfo.getTradeAmt()) && StringUtil.isNotEmpty(cstpInfo.getTradeAmt())
                && regInfo.getTradeAmt().compareTo(cstpInfo.getTradeAmt()) != 0) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            this.buildMassege(matchMap, "payableAmount",
                    str, "matchDetFeatures.payableAmount",
                    validateStr, "CONFIRM_MSG_014", df.format(ArithmeticUtil.round(cstpInfo.getTradeAmt(), 2, RoundingMode.HALF_UP)),
                    df.format(ArithmeticUtil.round(regInfo.getTradeAmt(), 2, RoundingMode.HALF_UP)));
        }
        // 发行价格
        if (StringUtil.isNotEmpty(regInfo.getOtherAmt()) && StringUtil.isNotEmpty(cstpInfo.getOtherAmt())
                && regInfo.getOtherAmt().compareTo(cstpInfo.getOtherAmt()) != 0) {
            DecimalFormat df = new DecimalFormat("#,##0.0000");
            this.buildMassege(matchMap, "issuePrice",
                    str, "matchDetFeatures.issuePrice",
                    validateStr, "CONFIRM_MSG_014", df.format(ArithmeticUtil.round(cstpInfo.getOtherAmt(), 4, RoundingMode.HALF_UP)),
                    df.format(ArithmeticUtil.round(regInfo.getOtherAmt(), 4, RoundingMode.HALF_UP)));
        }
        // 本方
        if (!StringUtil.equals(cstpInfo.getTraderid(), regInfo.getOuPartyId())) {
            this.buildMassege(matchMap, "ouPartyId",
                    str, "matchDetFeatures.ouPartyId",
                    validateStr, "CONFIRM_MSG_013", cstpInfo.getTraderid(), regInfo.getOuPartyId());
        }
        // 本方交易员
        if (!StringUtil.equals(regInfo.getTrader(), cstpInfo.getTrader())) {
            this.buildMassege(matchMap, "ourTrader",
                    str, "matchDetFeatures.ourTrader",
                    validateStr, "CONFIRM_MSG_013", cstpInfo.getTrader(), regInfo.getTrader());
        }
        if (StringUtil.isNotEmpty(str) && str.length() > 0) {
            str.append(getMessage("CONFIRM_MSG_015"));
            mes.setErrorList(Collections.singletonList(new ValidMessage(str.toString())));
            return true;

        }
        if (StringUtil.isNotEmpty(validateStr) && validateStr.length() > 0) {
            validateStr.append(getMessage("CONFIRM_MSG_016"));
            mes.setMessage(validateStr.toString(), MessageType.确认消息);
            return true;
        }
        return false;
    }

    /**
     * 提示语拼接
     *
     * @param matchMap    匹配要素Map
     * @param paraCode    匹配要素Code
     * @param str         禁止提示语返回值
     * @param labelStr    字段名(对应国际化文件中内容)
     * @param validateStr 确认提示语返回值
     * @param messageStr  确认提示语(对应国际化文件中内容)
     * @param cstpStr     下行字段值
     * @param regStr      登记/报价字段值
     * @author jiangpeng
     * @date 2022/9/28 16:30
     */
    private void buildMassege(Map<String, MatchDetFeatures> matchMap, String paraCode,
                              StringBuilder str, String labelStr,
                              StringBuilder validateStr, String messageStr,
                              String cstpStr, String regStr) {
        // 匹配要素是否为基础要素
        boolean flag = true;
        MatchDetFeatures matchDetFeatures = matchMap.get(paraCode);
        if (matchDetFeatures != null && StringUtil.equals(matchDetFeatures.getType(), "02"))
            flag = false;
        if (flag) {
            // 基础要素 手工匹配校验提示语拼接
            if (StringUtil.isNotEmpty(str)) {
                str.append(",");
            }
            str.append(this.getMessage(labelStr));
        } else {
            if(StringUtil.isEmpty(cstpStr)){
                cstpStr = this.getMessage("matchDetFeatures.null");
            }
            if(StringUtil.isEmpty(regStr)){
                regStr = this.getMessage("matchDetFeatures.null");
            }
            // 可选要素 手工匹配校验提示语拼接
            validateStr.append(this.getMessage(messageStr,
                    this.getMessage(labelStr), cstpStr, regStr));
        }
    }

    /**
     * CSTP成交回报-手工匹配
     */
    public AjaxResult cstpManualMatchMethod(CfetsTrdConfirmManualMatchInfoRequest manualMatchDto) {
        //判断基础要素是否一致
        CfetsTrdConfirmRegInfoListResponse regInfo = manualMatchDto.getSelectedCfetsTrdConfirmRegInfoList().get(0);
        CfetsTrdConfirmCstpInfoListResponse cstpInfo = manualMatchDto.getSelectedCfetsTrdConfirmCstpInfoList().get(0);
        List<ValidMessage> messages = new ArrayList<>();
        try {
            if (StringUtil.equals(regInfo.getFinprodType2(), DictCodeListExt1.登记资产类型_债券.getValue())) {
                //对dto进行封装
                CfetsTrdBondInvestTradeDto entity = this.packageEquity(regInfo, DictCodeListExt1.生成方式_画面录入.getValue(), cstpInfo.getCstpTradeId());
                if (null != entity.getCfetsTrdConfirmMatchDto()) {
                    entity.getCfetsTrdConfirmMatchDto().setSeqno(IDGenerator.generate());
                    entity.getCfetsTrdConfirmMatchDto().setCstpTradeId(cstpInfo.getCstpTradeId());
                    entity.getCfetsTrdConfirmMatchDto().setExeId(cstpInfo.getExecid());
                    entity.getCfetsTrdConfirmMatchDto().setMatchType(DictCodeListExt3.下行交易匹配类型_手动匹配.getValue());
                    entity.getCfetsTrdConfirmMatchDto().setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
                }
                entity.setOprType(DictCodeListExt3.操作类型_手工匹配确认.getValue());
                cfetsTrdConfirm001Service.doCompleteMethod(entity, OperationType.NEW);
            } else if (StringUtil.equals(regInfo.getFinprodType2(), DictCodeListExt1.登记资产类型_质押式回购.getValue())) {
                //对dto进行封装
                CfetsTrdRepoTrade003Dto entity = this.packageRepoEquity(regInfo, DictCodeListExt1.生成方式_画面录入.getValue(), cstpInfo.getCstpTradeId());
                if (null != entity.getCfetsTrdConfirmMatchDto()) {
                    entity.getCfetsTrdConfirmMatchDto().setSeqno(IDGenerator.generate());
                    entity.getCfetsTrdConfirmMatchDto().setCstpTradeId(cstpInfo.getCstpTradeId());
                    entity.getCfetsTrdConfirmMatchDto().setExeId(cstpInfo.getExecid());
                    entity.getCfetsTrdConfirmMatchDto().setMatchType(DictCodeListExt3.下行交易匹配类型_手动匹配.getValue());
                    entity.getCfetsTrdConfirmMatchDto().setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
                }
                entity.setOprType(DictCodeListExt3.操作类型_手工匹配确认.getValue());
                //成交确认提交方法
                cfetsTrdConfirm001Service.doCompleteRepoMethod(entity, OperationType.NEW, false);
            } else if (StringUtil.equals(regInfo.getFinprodType2(), DictCodeListExt1.登记资产类型_一级申购.getValue())) {
                CfetsTrdFirstBondInvestTradeDto entity = this.prepareBidEntity(regInfo.getTotleTradeId(), DictCodeListExt1.生成方式_画面录入.getValue(),
                        cstpInfo.getCstpTradeId());
                if (null != entity.getCfetsTrdConfirmMatchDto()) {
                    entity.getCfetsTrdConfirmMatchDto().setSeqno(IDGenerator.generate());
                    entity.getCfetsTrdConfirmMatchDto().setCstpTradeId(cstpInfo.getCstpTradeId());
                    entity.getCfetsTrdConfirmMatchDto().setExeId(cstpInfo.getExecid());
                    entity.getCfetsTrdConfirmMatchDto().setMatchType(DictCodeListExt3.下行交易匹配类型_手动匹配.getValue());
                    entity.getCfetsTrdConfirmMatchDto().setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
                }
                entity.setOprType(DictCodeListExt3.操作类型_手工匹配确认.getValue());
                cfetsTrdConfirm001Service.doCompleteBidMethod(entity, OperationType.NEW, DictCodeListExt1.生成方式_画面录入.getValue());
            }
        } catch (Exception e) {
            //若插入数据异常，提示错误信息
            Message ms = PositionUtil.ajaxErrorReturn(e);
            if (null != ms && StringUtil.isNotEmpty(ms.getMessage())) {
                messages.add(new ValidMessage(getMessage("CONFIRM_MSG_009", regInfo.getTotleTradeId(), DictCodeListExt3.操作类型_手工匹配确认.getCodeName(), ms.getMessage())));
            } else {
                messages.add(new ValidMessage(getMessage("CONFIRM_MSG_008", regInfo.getTotleTradeId(), DictCodeListExt3.操作类型_手工匹配确认.getCodeName())));
            }
            e.printStackTrace();
        }
        if (!StringUtil.isEmptyList(messages) && messages.size() > 0) {
            AjaxResult res = new AjaxResult();
            res.setErrorList(messages);
            return res;
        }
        return null;

        //插入匹配表
       /* CfetsTrdConfirmMatchDto matchDto=new CfetsTrdConfirmMatchDto();
        matchDto.setSeqno(IDGenerator.generate());
        matchDto.setCstpTradeId(cstpInfo.getCstpTradeId());
        matchDto.setExeId(cstpInfo.getExecid());
        matchDto.setPreTradeId(regInfo.getTotleTradeId());
        matchDto.setMatchType(DictCodeListExt3.下行交易匹配类型_手动匹配.getValue());
        matchDto.setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
        matchDto.setTrdConfStatus(DictCodeList.确认状态_未确认.getValue());
        matchDto.setCreateUser(SecurityUtil.getLoginUser().getUserName());
        matchDto.setCreateDept(CommonUtil.getDeptCode());
        matchDto.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        cfetsTrdConfirmMapper.insertTrdQutoMatchInfo(matchDto);*/
    }

    /**
     * 交易匹配确认列表分页查询
     *
     * @param dto
     * @return Page
     * @see
     * @since 1.0
     */
    public Page<CfetsTrdConfirmTrdCstpMatchResponse> findAllTrdCstpInfoList(CfetsTrdConfirmTrdCstpMatchRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        String portfolioId = (String) pageParam.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            pageParam.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        String finprodId = (String) pageParam.get("finprodId");
        if (StringUtil.isNotEmpty(finprodId)) {
            pageParam.put("finprodIdList", StringUtil.splitToList(GlobalConstant.COMMA, finprodId));
        }

        pageParam.put("assetType", pageParam.get("finprodType2"));
        String regAssetType = (String) pageParam.get("regAssetType");
        if (StringUtil.isNotEmpty(regAssetType)) {
            if (StringUtil.equals(regAssetType, "01")) {
                pageParam.put("finprodType2", "BOND");
            } else if (StringUtil.equals(regAssetType, "02")) {
                pageParam.put("finprodType2", "REPO");
            } else if (StringUtil.equals(regAssetType, "03")) {
                pageParam.put("finprodType2", "BID");
            }
        }
        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
            pageParam.put("settleSpeed", "0");
        }
        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
            pageParam.put("settleSpeed", "1");
        }

        String secAcctId = (String) pageParam.get("secAcctId");
        if (StringUtil.isNotEmpty(secAcctId)) {
            pageParam.put("secAcctIdList", StringUtil.splitToList(GlobalConstant.COMMA, secAcctId));
        }
        String counterId = (String) pageParam.get("counterId");
        if (StringUtil.isNotEmpty(counterId)) {
            pageParam.put("counterIdList", StringUtil.splitToList(GlobalConstant.COMMA, counterId));
        }

        String trdConfStatus = (String) pageParam.get("trdConfStatus");
        if (StringUtil.isNotEmpty(trdConfStatus)) {
            pageParam.put("trdConfStatusList", StringUtil.splitToList(GlobalConstant.COMMA, trdConfStatus));
        }
        pageParam.put("sysDate", iPlatformDate.getSysDate());
        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.parse(DateUtil.getSysDate()));
        int year = c.get(Calendar.YEAR);
        pageParam.put("year", year);
        pageParam.put("sort", dto.getPageMode().getSort());
        pageParam.put("direction", dto.getPageMode().getDirection());
        String userId = SecurityUtil.getLoginUser().getUserName();
        if (StringUtils.isNotEmpty(userId)) {
            //判断角色配置
            List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
            if (roleList.contains(DictCodeList.角色岗位_交易员.getValue())) {
                //交易员
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
        }
        // 未匹配
        pageParam.put("matchStatus_02", "02");
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdConfirmMapper.findAllTrdCstpInfoList(pageParam));

    }

    /**
     * 交易匹配确认列表-交易执行债券分页查询
     *
     * @param dto
     * @return Page
     * @since 1.0
     */
    public Page<CfetsTrdConfirmExecBondInfoResponse> findAllTrdBondList(CfetsTrdConfirmExecCstpRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("sort", dto.getPageMode().getSort());
        pageParam.put("direction", dto.getPageMode().getDirection());
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdConfirmMapper.findAllTrdBondList(pageParam));
    }

    /**
     * 交易匹配确认列表-交易执行一级申购分页查询
     *
     * @param dto
     * @return Page
     * @since 1.0
     */
    public Page<CfetsTrdConfirmExecFirstBondInfoResponse> findAllTrdBidList(CfetsTrdConfirmExecCstpRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("sort", dto.getPageMode().getSort());
        pageParam.put("direction", dto.getPageMode().getDirection());
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdConfirmMapper.findAllTrdBidList(pageParam));
    }

    /**
     * 交易匹配确认列表-交易执行质押式分页查询
     *
     * @param dto
     * @return Page
     * @since 1.0
     */
    public Page<CfetsTrdConfirmExecRepoInfoResponse> findAllTrdRepoList(CfetsTrdConfirmExecCstpRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("sort", dto.getPageMode().getSort());
        pageParam.put("direction", dto.getPageMode().getDirection());
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdConfirmMapper.findAllTrdRepoList(pageParam));
    }

    /**
     * 匹配确认列表-cstp成交债券数据分页查询
     *
     * @param dto
     * @return Page
     * @since 1.0
     */
    public Page<CfetsTrdConfirmCstpBondInfoResponse> findAllCstpBondList(CfetsTrdConfirmExecCstpRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("sort", dto.getPageMode().getSort());
        pageParam.put("direction", dto.getPageMode().getDirection());
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdConfirmMapper.findAllCstpBondList(pageParam));
    }

    /**
     * 匹配确认列表-cstp成交一级申购数据分页查询
     *
     * @param dto
     * @return Page
     * @since 1.0
     */
    public Page<CfetsTrdConfirmExecFirstBondInfoResponse> findAllCstpBidList(CfetsTrdConfirmExecCstpRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("sort", dto.getPageMode().getSort());
        pageParam.put("direction", dto.getPageMode().getDirection());
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdConfirmMapper.findAllCstpBidList(pageParam));
    }

    /**
     * 匹配确认列表-cstp成交质押式分页查询
     *
     * @param dto
     * @return Page
     * @since 1.0
     */
    public Page<CfetsTrdConfirmCstpRepoInfoResponse> findAllCstpRepoList(CfetsTrdConfirmExecCstpRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("sort", dto.getPageMode().getSort());
        pageParam.put("direction", dto.getPageMode().getDirection());
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdConfirmMapper.findAllCstpRepoList(pageParam));
    }

    /**
     * 匹配确认Tab-匹配取消
     */
    public void cancelTrdMatchInfo(CfetsTrdConfirmTrdCstpMatchResponse dto) {
        List<CfetsTrdConfirmTrdCstpMatchResponse> selectedItem = dto.getSelectedItem();
        if (StringUtil.isNotEmpty(selectedItem)) {
            for (CfetsTrdConfirmTrdCstpMatchResponse item : selectedItem) {
                cfetsTrdConfirmMapper.deleteByPrimaryKey(item.getSeqno());
            }
        }
    }

    /**
     * 划款指令信息
     *
     * @param temp    交易数据
     * @param totalPs 交易方向
     * @return java.util.List<com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto>
     * @author jiangpeng
     * @date 2022/7/8 11:45
     */
    public List<BussTrdTransferOrderDto> dealOrders(TrdProductDeal temp, String totalPs) {
        // =====================划款指令信息=====================
        List<BussTrdTransferOrderDto> bussTrdTransferOrder = new ArrayList<>();
        BussSettleOrderParameterDto bussSettleOrderParameterDto = new BussSettleOrderParameterDto();
        bussSettleOrderParameterDto.setIsTotalTrade(BussinessDictCodeList.是否_否.getValue());
        bussSettleOrderParameterDto.setBankLendingType(null);
        TrdProductDeal deal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(deal, temp);
        if (StringUtil.equals(deal.getFinprodType2(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())) {
            deal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
            deal.setFinprodType2(DictCodeListExt1.金融产品类型_债券.getValue());
        }
        deal.setPs(totalPs);
        bussSettleOrderParameterDto.setPortfolioId(deal.getPortfolioId());
        bussSettleOrderParameterDto.setDeal(deal);
        BussTrdTransferOrderDto bussTrdTransferOrderDto = bussTrdTransferOrderService.generateOrder(bussSettleOrderParameterDto);

        // 付款账户
        String payAcctNo = null;
        // 收款账户
        String recAcctNo = null;
        // 一级申购 || 债券买入 || 逆回购首期
        if (StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())
                || (StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_债券.getValue())
                && StringUtil.equals(deal.getPs(), DictCodeList.交易方向_买入.getValue()))) {
            logger.info("划款指令生成 :一级申购 || 债券买入 || 逆回购首期");
            payAcctNo = EtrCommonCashPositionDealService.select组合托管户(deal.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("portfolioId", deal.getPortfolioId());
            paramMap.put("secAcctId", deal.getSecManageAcctId());
            paramMap.put("ccy", deal.getCcy());
            recAcctNo = joyinbusinessCommonService.selectDVPCapacctInfoListByAcctId(paramMap);
        }
        // 债券卖出/回售 || 正回购首期
        if (StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())
                || (StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_债券.getValue())
                && (StringUtil.equals(deal.getPs(), DictCodeList.交易方向_卖出.getValue())
                || StringUtil.equals(deal.getPs(), DictCodeList.交易方向_行权_回售.getValue())))) {
            logger.info("划款指令生成 :债券卖出/回售 || 正回购首期");
            recAcctNo = EtrCommonCashPositionDealService.select组合托管户(deal.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("portfolioId", deal.getPortfolioId());
            paramMap.put("secAcctId", deal.getSecManageAcctId());
            paramMap.put("ccy", deal.getCcy());
            payAcctNo = joyinbusinessCommonService.selectDVPCapacctInfoListByAcctId(paramMap);
        }
        // 商业银行柜台  一级申购 || 债券买入 || 逆回购首期
        if (StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_债券.getValue())
                && StringUtil.equals(deal.getPs(), DictCodeList.交易方向_买入.getValue())) {
            logger.info("划款指令生成 :一级申购 || 债券买入 || 逆回购首期");
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(deal.getFinprodId());
            if (StringUtil.equals(finProduct.getTradeMarket(), DictCodeListExt1.交易场所_商业银行柜台.getValue())) {
                // 收款账户 交易对手账户
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("counterId", deal.getCounterId());
                paramMap.put("ccy", deal.getCcy());
                recAcctNo = joyinbusinessCommonService.selectCounterCapacctInfoList(paramMap);
            }
        }
        // 商业银行柜台   债券卖出/回售 || 正回购首期
        if ((StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_债券.getValue())
                && (StringUtil.equals(deal.getPs(), DictCodeList.交易方向_卖出.getValue())
                || StringUtil.equals(deal.getPs(), DictCodeList.交易方向_行权_回售.getValue())))) {
            logger.info("划款指令生成 :商业银行柜台   债券卖出/回售 || 正回购首期");
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(deal.getFinprodId());
            if (StringUtil.equals(finProduct.getTradeMarket(), DictCodeListExt1.交易场所_商业银行柜台.getValue())) {
                // 收款账户 交易对手账户
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("counterId", deal.getCounterId());
                paramMap.put("ccy", deal.getCcy());
                payAcctNo = joyinbusinessCommonService.selectCounterCapacctInfoList(paramMap);
            }
        }
        //一级申购特殊处理
        if (StringUtil.equals(temp.getFinprodType2(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())) {
            logger.info("划款指令生成 :一级申购特殊处理");
            // 收款账户 交易对手账户
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("counterId", deal.getCounterId());
            paramMap.put("ccy", deal.getCcy());
            recAcctNo = joyinbusinessCommonService.selectCounterCapacctInfoList(paramMap);
        }
        bussTrdTransferOrderDto.setPayAcctNo(payAcctNo);
        bussTrdTransferOrderDto.setRecAcctNo(recAcctNo);
        String ps = DictCodeListExt1.收支方向_支出.getValue();
        if (StringUtil.equals(temp.getPs(), DictCodeList.交易方向_卖出.getValue())
                || StringUtil.equals(deal.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                || StringUtil.equals(temp.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
            ps = DictCodeListExt1.收支方向_收入.getValue();
        }
        bussTrdTransferOrderDto.setPs(ps);
        bussTrdTransferOrderDto.setIsTotalTrade(BussinessDictCodeList.是否_否.getValue());
        bussTrdTransferOrderDto.setOrderRetStatus(DictCodeList.指令处理状态_处理成功.getValue());
        bussTrdTransferOrderDto.setTradeId(deal.getTradeId());
        bussTrdTransferOrderDto.setPortfolioId(deal.getPortfolioId());
        bussTrdTransferOrderDto.setIsNeedSend(DictCodeList.是否_否.getValue());
        bussTrdTransferOrder.add(bussTrdTransferOrderDto);
        logger.info("划款指令信息:{}", JsonUtils.object2Json(bussTrdTransferOrder));
        return bussTrdTransferOrder;
    }
}
