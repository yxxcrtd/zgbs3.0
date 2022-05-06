/*
 * 文件名：EtrTradePlaceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：meifaxin
 * 修改时间：2021年12月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.common.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.service.entrust.etrexchangetrade.dto.EtrExchangeTrade002Dto;
import com.joyintech.tams.business.common.dao.JoyinbusinessCommonMapper;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dao.EtrExchangeTradeCommonMapper;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.EtrExchangeTradeQuotationHqResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.*;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.service.EtrExchangeTradeJj001Service;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.TempConstant;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecDto;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeCommonervice;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 *
 * @author meifaxin
 * @version 1.0 2021年12月20日
 * @see EtrExchangeTradeCommon002Service
 * @since 1.0
 */
@Service
public class EtrExchangeTradeCommon002Service extends BaseService {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private EtrExchangeTradeCommonMapper etrExchangeTradeCommonMapper;
    @Autowired
    private JoyinbusinessCommonMapper joyinbusinessCommonMapper;
    @Autowired
    private EtrTradeCommonervice etrTradeCommonervice;
    @Autowired
    private EtrExchangeTradeJj001Service exchangeTradeJj001Service;
    @Autowired
    private EtrExchangeTradeCommon001Service etrExchangeTradeCommon001Service;

    /**
     * 分页查询产品持仓信息
     * <功能详细描述>
     *
     * @param dto 查询参数
     * @return 分页结果
     * @see
     * @since 1.0
     */
    public Page<EtrExchangeTradePositionsCommonResponse> findPositionsPage(EtrExchangeTradePositionsCommonRequest dto) {
        try {
            String platDate = iPlatformDate.getSysDateStr();
            PageMode pageMode = null;
            if (StringUtil.isNotEmpty(dto.getPageMode())
                    && (dto.getPageMode().getSort().equals("floating") || dto.getPageMode().getSort().equals("floatingPL"))) {
                pageMode = dto.getPageMode();
                PageMode pageMode2 = new PageMode();
                pageMode2.setPageNo(pageMode.getPageNo());
                pageMode2.setPageSize(pageMode.getPageSize());
                pageMode2.setSort("");
                pageMode2.setDirection("");
                dto.setPageMode(pageMode2);
            }
            Map<String, Object> param = BeanUtil.object2Map(dto);
            String userId = SecurityUtil.getLoginUser().getUserName();
            if (StringUtils.isNotEmpty(userId)) {
                logger.info("请求用户【{}】", "userId = " + userId);
                param.put("userId", SecurityUtil.getLoginUser().getUserName());
                String portfolioId = (String) param.get("portfolioId");
                if (StringUtil.isNotEmpty(portfolioId)) {
                    param.put("portfolioIdList",
                            StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
                }
                if (StringUtil.isNotEmpty(portfolioId)) {
                    param.put("portfolioIdList",
                            StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
                }
                param.put("sysDate", iPlatformDate.getSysDate());
                param.put("POSITION_TYPE_03", BussinessDictCodeList.持仓类型_实际.getValue());
                param.put("POSITION_TYPE_04", BussinessDictCodeList.持仓类型_质押.getValue());
                param.put("POSITION_TYPE_05", BussinessDictCodeList.持仓类型_融入.getValue());
                param.put("POSITION_TYPE_06", BussinessDictCodeList.持仓类型_融出.getValue());
                param.put("VALUATION_SOURCE_CSD", BussinessDictCodeList.估值来源_中证登.getValue());
                param.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                param.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
                param.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
                param.put("PS_T01", BussinessDictCodeList.交易方向_买入.getValue());
                param.put("PS_T02", BussinessDictCodeList.交易方向_卖出.getValue());
                param.put("PS_T03", BussinessDictCodeList.交易方向_行权回售.getValue());
                param.put("PS_T05", BussinessDictCodeList.交易方向_申购确认.getValue());
                param.put("PS_T19", BussinessDictCodeList.交易方向_赎回确认.getValue());
                param.put("PS_T46", DictCodeListExt2.交易方向_配股.getValue());
                param.put("PS_T48", DictCodeListExt2.交易方向_新股中签.getValue());
                param.put("PS_T49", DictCodeListExt3.交易方向_债转股.getValue());
                param.put("PS_T97", DictCodeListExt3.交易方向_配债.getValue());
                param.put("PS_T04", DictCodeList.交易方向_申购.getValue());
                param.put("TRADE_PLACE_03", BussinessDictCodeList.交易场所_上海证券交易所.getValue());
                param.put("TRADE_PLACE_04", BussinessDictCodeList.交易场所_深圳证券交易所.getValue());
                param.put("search_isNumberFlag", CommonUtil.isNumeric(dto.getSearch()));
                if (StringUtil.isNotEmpty((String) param.get("finprodMarketIds"))) {
                    param.put("finprodMarketIds", StringUtil.splitToList(GlobalConstant.COMMA, param.get("finprodMarketIds").toString()));
                } else {
                    param.put("finprodMarketIds", null);
                }
                param.put("A_001", DictCodeListExt3.资产类型_资产账户持仓.getValue());
                param.put("NO_TRADE_BUSINESS_ETR", TempConstant.NO_TRADE_BUSINESS_ETR);
                param.put("SHARES_TRADE_PLATFORM_03", DictCodeList.交易平台_固定收益平台.getValue());
                param.put("TYPE_6", DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue());
                // 浮动盈亏计算修改 modify by yufei 20200806 start
                //Page<EtrExchangeTradePositionsCommonResponse> result = super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> etrExchangeTradeCommonMapper.findPositionsPage(param));
                Page<EtrExchangeTradePositionsCommonResponse> result = new Page<>();
                List<EtrExchangeTradePositionsCommonResponse> queryList = etrExchangeTradeCommonMapper.findPositionsPage(param);
                List<EtrExchangeTradePositionsCommonResponse> returnList = new ArrayList<>();
                if (!StringUtil.isEmptyList(queryList)) {
                    // 计算浮动盈亏
                    for (EtrExchangeTradePositionsCommonResponse entity : queryList) {
                        //可用数量
                        PositionCashSecDto secDto = new PositionCashSecDto();
                        BeanUtil.copyPropertiesIgnoreNull(secDto, entity);
                        secDto.setTradePlat(dto.getTradePlat());
                        BigDecimal sellNum = etrTradeCommonervice.selectAvailableNum(secDto);
                        entity.setSellNum(sellNum);
                        //过滤掉实际持仓和可用持仓都等于0
                        if (entity.getPositionsNum().compareTo(BigDecimal.ZERO) == 0 && sellNum.compareTo(BigDecimal.ZERO) == 0) {
                            continue;
                        }
                        //最新价从行情取
                        EtrExchangeTradeQuotationHqResponse hqResponse = exchangeTradeJj001Service.findQuotations(entity.getFinprodMarketId());
                        if (hqResponse != null) {
                            BigDecimal lastPrice = hqResponse.getLastPrice() != null ? hqResponse.getLastPrice().setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0.00);
                            entity.setPrice(lastPrice);
                        }
                        returnList.add(entity);
                    }
                    result.setRoot(returnList);
                }
                // 根据查询结果查询估值、交易数据，用来加工单位成本等字段
                final List pList = new ArrayList<String>();
                final List fList = new ArrayList<String>();
                if (!StringUtil.isEmptyList(returnList)) {
                    result.getRoot().stream().forEach(c -> {
                        if (!pList.contains(c.getPortfolioId())) {
                            pList.add(c.getPortfolioId());
                        }
                        if (!fList.contains(c.getFinprodId())) {
                            fList.add(c.getFinprodId());
                        }
                    });
                }
                result.setRoot(returnList);
                // 查询单位成本数据
                Map<String, EtrExchangeUnitCostDto> unitCostMap = calcEachCostAmtMap(DateUtil.parse(platDate), pList, fList, "");
                // 计算单位成本、计算持仓成本、最新净价、持仓市值、浮动盈亏
                calculationAmount(unitCostMap, result, platDate);
                //非交易指令单位修改
                if(StringUtil.equals(TempConstant.NO_TRADE_BUSINESS_ETR,dto.getTradePlat())){
                    if (result != null && result.getRoot() != null && !result.getRoot().isEmpty()) {
                        result.getRoot().forEach(r -> {
                            //持仓数量		N2			万元
                            BigDecimal positionsNum=ArithmeticUtil.divide(r.getPositionsNum(),new BigDecimal(100),2, RoundingMode.DOWN);
                            r.setPositionsNum(positionsNum);
                            //可卖数量		N2			万元
                            BigDecimal sellNum=ArithmeticUtil.divide(r.getSellNum(),new BigDecimal(100),2, RoundingMode.DOWN);
                            r.setSellNum(sellNum);
                        });
                    }
                }
                return result;
//				return restTemplate.postForPage(
//						ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/findPositionsPage", param,
//						EtrExchangeTrade002Dto.class);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info(ex.getMessage());
        }
        return null;
    }

    // modify by etliu 债券质押持仓查询只有非交易业务指令使用，代码删除 20220308 start
//	/**
//	 * 分页查询债券质押持仓信息
//	 *
//	 * @param request
//	 *            查询参数
//	 * @return 分页结果
//	 * @see
//	 * @since 1.0
//	 */
//	public Page<EtrExchangeTradePositionsCommonResponse> findZqzyPositionsPage(EtrExchangeTradePositionsCommonRequest request) {
//		try {
//			Map<String, Object> pageParam = BeanUtil.object2Map(request);
//			String userId = SecurityUtil.getLoginUser().getUserName();
//			if (StringUtils.isNotEmpty(userId)) {
//				logger.info("请求用户【{}】", "userId = " + userId);
//				pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
//				pageParam.put("POSITION_TYPE_03", BussinessDictCodeList.持仓类型_实际.getValue());
//				pageParam.put("POSITION_TYPE_04", BussinessDictCodeList.持仓类型_质押.getValue());
//				pageParam.put("POSITION_TYPE_05", BussinessDictCodeList.持仓类型_融入.getValue());
//				pageParam.put("POSITION_TYPE_06", BussinessDictCodeList.持仓类型_融出.getValue());
//				pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
//				pageParam.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
//				pageParam.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
//				pageParam.put("PS_T01", BussinessDictCodeList.交易方向_买入.getValue());
//				pageParam.put("PS_T02", BussinessDictCodeList.交易方向_卖出.getValue());
//				pageParam.put("PS_T03", BussinessDictCodeList.交易方向_行权回售.getValue());
//				pageParam.put("PS_T05", BussinessDictCodeList.交易方向_申购确认.getValue());
//				pageParam.put("PS_T19", BussinessDictCodeList.交易方向_赎回确认.getValue());
//				pageParam.put("PS_T46", DictCodeListExt2.交易方向_配股.getValue());
//				pageParam.put("PS_T48", DictCodeListExt2.交易方向_新股中签.getValue());
//				pageParam.put("PS_T49", DictCodeListExt3.交易方向_债转股.getValue());
//				pageParam.put("PS_T97", DictCodeListExt3.交易方向_配债.getValue());
//				pageParam.put("PS_T04", DictCodeList.交易方向_申购.getValue());
//				pageParam.put("LIBRARY_DIRECTION_01", DictCodeListExt3.交易方向_质押券入库.getValue());
//				pageParam.put("TRADE_PLACE_03", BussinessDictCodeList.交易场所_深圳证券交易所.getValue());
//				pageParam.put("TRADE_PLACE_04", BussinessDictCodeList.交易场所_上海证券交易所.getValue());
//				pageParam.put("MORE_CONVRATE", 0.9);
//				if(StringUtil.isNotEmpty(pageParam.get("date"))) {
//					pageParam.put("date", DateUtil.parse((String) pageParam.get("date")));
//				}
//				else {
//					pageParam.put("date", iPlatformDate.getSysDate());
//				}
//				if(StringUtil.isNotEmpty((String)pageParam.get("finprodMarketIds"))){
//					pageParam.put("finprodMarketIds",StringUtil.splitToList(GlobalConstant.COMMA, pageParam.get("finprodMarketIds").toString()));
//				}else{
//					pageParam.put("finprodMarketIds", null);
//				}
//				Page<EtrExchangeTradePositionsCommonResponse> result = super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(),
//						() -> etrExchangeTradeCommonMapper.findZqzyPositionsPage(pageParam));
//				return result;
////				return restTemplate.postForPage(
////						ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/findZqzyPositionsPage", param,
////						EtrExchangeTrade002Dto.class);
//			}
//		} catch (Exception ex) {
//			logger.info(ex.getMessage());
//		}
//		return null;
//	}
    // modify by etliu 债券质押持仓查询只有非交易业务指令使用，代码删除 20220308 end

    /**
     * 查询证券代码联想框文本框
     *
     * @param request 画面参数
     * @return 证券代码联想框文本框
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getSecAcctId(SelectOptionRequestDto request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        Page<SelectOptionDto> tempList = super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(), () -> etrExchangeTradeCommonMapper.getSecAcctId(param));
        return tempList;
    }

    /**
     * 查询上交所和深交所标准券数量
     *
     * @param request 页面参数
     * @return 上交所和深交所标准券数量
     * @see
     * @since 1.0
     */
    public Map<String, String> loadSecNum(SelectOptionRequestDto request) {
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("secNum_sh", "0");
        resultMap.put("secNum_color_sh", "red");
        resultMap.put("secNum_sz", "0");
        resultMap.put("secNum_color_sz", "red");
        List<EtrExchangeTrade002Dto> dtoList = etrExchangeTradeCommonMapper.selectSecNum(pageParam);
        for (EtrExchangeTrade002Dto dto : dtoList) {
            if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                java.text.DecimalFormat format = new java.text.DecimalFormat("###,###,###");
                resultMap.put("secNum_sh", format.format(dto.getSecNum()));
                if (dto.getSecNum().compareTo(BigDecimal.ZERO) < 0) {
                    resultMap.put("secNum_color_sh", "green");
                }
            } else if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                java.text.DecimalFormat format = new java.text.DecimalFormat("###,###,###");
                resultMap.put("secNum_sz", format.format(dto.getSecNum()));
                if (dto.getSecNum().compareTo(BigDecimal.ZERO) < 0) {
                    resultMap.put("secNum_color_sz", "green");
                }
            }
        }
        return resultMap;
    }


    public Page<EtrExchangeTradeEtrExchangeCommonResponse> selectEtrExchange(EtrExchangeTradeEtrExchangeCommonRequest request) {
//		if (StringUtil.isNotEmpty(request.getPageMode())){
//			PageMode pageMode =request.getPageMode();
//			pageMode.setPageNo(1);
//			request.setPageMode(pageMode);
//		}
        Map<String, Object> param = BeanUtil.object2Map(request);
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        param.put("sysDate", iPlatformDate.getSysDateStr());
        param.put("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
        param.put("PS_04", DictCodeListExt3.交易方向_逆回购.getValue());
        param.put("PS_T68", DictCodeListExt3.交易方向_质押券入库.getValue());
        param.put("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
        param.put("PS_10", DictCodeListExt3.上农商_指令业务类型_配股指令.getValue());
        param.put("PS_11", DictCodeListExt3.上农商_指令业务类型_新股申购指令.getValue());
        param.put("PS_12", DictCodeListExt3.上农商_指令业务类型_配债指令.getValue());
        String portfolioId = (String) param.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            param.put("portfolioIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        if (CollectionUtils.isNotEmpty(request.getEntrustTypeList())) {
            param.put("entrustTypeList", request.getEntrustTypeList());
        }
        param.put("search_isNumberFlag", BaseMap.isNumeric(request.getSearch()));
        List<String> tradePlatList = new ArrayList<>();
        if (StringUtil.isNotEmpty(request.getTradePlat())) {
            if ((DictCodeList.交易平台_大宗交易平台.getValue()).equals(request.getTradePlat())) {
                tradePlatList.add(request.getTradePlat());
                tradePlatList.add(DictCodeList.交易平台_综合协议交易平台.getValue());
            } else {
                tradePlatList.add(request.getTradePlat());
            }
        }
        param.put("tradePlatList", tradePlatList);
        if (StringUtil.isNotEmpty((String) param.get("finprodMarketIds"))) {
            param.put("finprodMarketIds", StringUtil.splitToList(GlobalConstant.COMMA, param.get("finprodMarketIds").toString()));
        } else {
            param.put("finprodMarketIds", null);
        }
        //申报类型 - 模糊查询
        if (StringUtils.isNotEmpty((String) param.get("search"))) {
            List<String> declaTypeList = new ArrayList<>();
            List<? extends DataDictItem> list = SysDataUtil.getDataDictItem(DictCode.申报类型.getValue());
            if (!list.isEmpty() && list.size() > 0) {
                for (DataDictItem t :
                        list) {
                    String typeValue = t.getItemValue();
                    String typeId = t.getItemCode();
                    if (typeValue.indexOf((String) param.get("search")) != -1 && typeId.equals("PRM14")) {
                        declaTypeList.add(t.getItemCode());
                    }
                }
                param.put("declaTypeList", declaTypeList);
            }
        } else {
            param.put("declaTypeList", null);
        }
        return super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(), () -> etrExchangeTradeCommonMapper.selectEtrExchange(param));
//		return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/selectEtrExchange",
//				param, EtrExchangeTrade003Dto.class);
    }

    /* 指令导出履历处理
     *
     * @param dataList 数据列表
     * @author jiangpeng
     * @date 2021/12/20 14:33
     */
    public void handExportTimes(List<EtrExchangeTradeEtrExchangeCommonRequest> dataList) {
        List<CfetsOprHisInfoRequest> result = new ArrayList<>();
        dataList.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getEntrustId())) {
                CfetsOprHisInfoRequest cfetsOprHisInfoRequest = new CfetsOprHisInfoRequest();
                cfetsOprHisInfoRequest.setEntrustId(item.getEntrustId());
                cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_导出.getValue());
                cfetsOprHisInfoRequest.setSeqNo(IDGenerator.generate(32));
                cfetsOprHisInfoRequest.setOperDate(DateUtil.platDateFromCache());
                cfetsOprHisInfoRequest.setOperTime(DateUtil.platDateFromCache());
                cfetsOprHisInfoRequest.setUserId(SecurityUtil.getLoginUser().getUserName());
                cfetsOprHisInfoRequest.setCreateUser(SecurityUtil.getLoginUser().getUserName());
                cfetsOprHisInfoRequest.setCreateDept(CommonUtil.getDeptCode());
                cfetsOprHisInfoRequest.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
                result.add(cfetsOprHisInfoRequest);
            }
        });
        joyinbusinessCommonMapper.mergeCfetsOprHis(result);
    }

    /**
     * 查询指令编号联想框文本框
     *
     * @param request 画面参数
     * @return 证券代码联想框文本框
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getId(SelectOptionRequestDto request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        Page<SelectOptionDto> tempList = super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(), () -> etrExchangeTradeCommonMapper.getId(param));
        return tempList;
    }

    /**
     * 查询前一日持仓指标
     *
     * @param cdate           交易日期
     * @param portfolioIdList 组合代码
     * @param secType1        债券一级分类
     */
    public List<BaseMap> selectLastCostAmt(Date cdate, List<String> portfolioIdList, List<String> finprodIdList, String secType1) {
        BaseMap reqMap = BaseMap.newMap()
                .put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue())    // 债券
                .put("SVR_TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue())
                .put("SVR_TRADE_MARKET_03", DictCodeListExt1.交易场所_上海证券交易所.getValue())
                .put("SVR_TRADE_MARKET_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue())
                .put("cdate", DateUtil.addDay(cdate, -1));

        if (portfolioIdList != null && !portfolioIdList.isEmpty()) reqMap.put("portfolioIdList", portfolioIdList);
        if (finprodIdList != null && !finprodIdList.isEmpty()) reqMap.put("finprodIdList", finprodIdList);
        if (StringUtil.isNotEmpty(secType1)) reqMap.put("secType1", secType1);

        List<BaseMap> list = etrExchangeTradeCommonMapper.selectLastCostAmt(reqMap);
        /**
         * 返回结果
         * PORTFOLIO_ID、FINPROD_ID、INV_AIM、
         * BUY_COST_AMT 成本
         * SHARE_AMT    数量
         */
        return list;
    }

    /**
     * 当日交易数据查询
     *
     * @param cdate           交易日期
     * @param portfolioIdList 组合代码
     * @param secType1        债券一级分类
     */
    public List<BaseMap> selectCurrTrdData(Date cdate, List<String> portfolioIdList, List<String> finprodIdList, String secType1) {
        BaseMap reqMap = BaseMap.newMap()
                .put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue())    // 债券
                .put("SVR_TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue())           // 银行间
                .put("PS_T01", DictCodeList.交易方向_买入.getValue())
                .put("PS_T02", DictCodeList.交易方向_卖出.getValue())
                .put("PS_T03", DictCodeList.交易方向_行权_回售.getValue())
                .put("PS_T04", DictCodeList.交易方向_申购.getValue())
                .put("PS_T49", DictCodeListExt1.交易方向_债转股.getValue())
                .put("SVR_TRADE_MARKET_03", DictCodeListExt1.交易场所_上海证券交易所.getValue())
                .put("SVR_TRADE_MARKET_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue())
                .put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue())
                .put("YN_N", DictCodeList.是否_否.getValue())
                .put("cdate", cdate);

        if (portfolioIdList != null && !portfolioIdList.isEmpty()) reqMap.put("portfolioIdList", portfolioIdList);
        if (finprodIdList != null && !finprodIdList.isEmpty()) reqMap.put("finprodIdList", finprodIdList);
        if (StringUtil.isNotEmpty(secType1)) reqMap.put("secType1", secType1);

        List<BaseMap> list = etrExchangeTradeCommonMapper.selectCurrTrdData(reqMap);
        /**
         *  PORTFOLIO_ID、FINPROD_ID、INV_AIM、PS
         *  PRICE,  净价
         *  AMT     数量
         */
        return list;
    }

    /**
     * @return
     */
    public Map<String, EtrExchangeUnitCostDto> calcEachCostAmtMap(Date cdate, List<String> portfolioIdList, List<String> finprodIdList, String secType1) {
        /** 返回结果
         * KEY:组合+资产代码+投资目的
         * VALUE:持仓单位成本dto
         */
        Map<String, EtrExchangeUnitCostDto> resultMap = new HashMap<>();

        // 查询前一日持仓指标
        List<BaseMap> lastCostAmt = selectLastCostAmt(cdate, portfolioIdList, finprodIdList, "");

        if (lastCostAmt != null) {
            lastCostAmt.forEach(e -> {
                if (BigDecimal.ZERO.compareTo(e.getBigDecimal("SHARE_AMT")) != 0) {
                    // 数量不为0时生成单位净价
                    EtrExchangeUnitCostDto dto = new EtrExchangeUnitCostDto();

                    dto.setBuyCostAmt(e.getBigDecimal("BUY_COST_AMT"));
                    dto.setShareAmt(e.getBigDecimal("SHARE_AMT"));
                    // 单位成本=成本/数量， （EACH_COST = BUY_COST_AMT / SHARE_AMT）
                    dto.setUnitCost(ArithmeticUtil.divide(
                            e.getBigDecimal("BUY_COST_AMT"),
                            e.getBigDecimal("SHARE_AMT"),
                            4));
                    resultMap.put(getKey(e), dto);
                }
            });
        }

        // 当日交易数据查询
        List<BaseMap> currTrdData = selectCurrTrdData(cdate, portfolioIdList, finprodIdList, secType1);
        if (currTrdData != null) {
            currTrdData.forEach(i -> {
                String key = getKey(i);
                EtrExchangeUnitCostDto dto = resultMap.get(key);
                if (dto == null) {
                    dto = new EtrExchangeUnitCostDto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                    resultMap.put(key, dto);
                }

                if (StringUtil.equals(i.getString("PS"), DictCodeList.交易方向_买入.getValue())) {
                    // 成本
                    BigDecimal thisBuyCostAmt = dto.getBuyCostAmt()
                            .add(ArithmeticUtil.multiply(i.getBigDecimal("PRICE"), i.getBigDecimal("AMT")));
                    dto.setBuyCostAmt(thisBuyCostAmt);
                    // 数量
                    dto.setShareAmt(dto.getShareAmt().add(i.getBigDecimal("AMT")));
                    // 单位成本 = 成本/数量
                    dto.setUnitCost(ArithmeticUtil.divide(dto.getBuyCostAmt(), dto.getShareAmt(), 4));
                } else if (StringUtil.equals(i.getString("PS"), DictCodeList.交易方向_申购.getValue())) {
                    // 成本
                    BigDecimal thisBuyCostAmt = dto.getBuyCostAmt()
                            .add(ArithmeticUtil.multiply(i.getBigDecimal("PRICE"), i.getBigDecimal("AMT")));
                    dto.setBuyCostAmt(thisBuyCostAmt);
                    // 数量
                    dto.setShareAmt(dto.getShareAmt().add(i.getBigDecimal("AMT")));
                    // 单位成本 = 成本/数量
                    dto.setUnitCost(ArithmeticUtil.divide(dto.getBuyCostAmt(), dto.getShareAmt(), 4));
                } else { // 卖出或行权回售
                    // 成本
                    BigDecimal thisBuyCostAmt = dto.getBuyCostAmt()
                            .subtract(ArithmeticUtil.multiply(i.getBigDecimal("PRICE"), i.getBigDecimal("AMT")));
                    dto.setBuyCostAmt(thisBuyCostAmt);
                    // 数量
                    dto.setShareAmt(dto.getShareAmt().subtract(i.getBigDecimal("AMT")));
                    // 单位成本 = 成本/数量
                    dto.setUnitCost(ArithmeticUtil.divide(dto.getBuyCostAmt(), dto.getShareAmt(), 2));
                }

                resultMap.put(key, dto);
            });
        }
        return resultMap;
    }

    public static String getKey(BaseMap bm) {
        return bm.getString("PORTFOLIO_ID") + bm.get("FINPROD_ID");
    }

    private Page<EtrExchangeTradePositionsCommonResponse> calculationAmount(Map<String, EtrExchangeUnitCostDto> unitCostMap, Page<EtrExchangeTradePositionsCommonResponse> pages, String platDate) {
        if (pages != null && pages.getRoot() != null && !pages.getRoot().isEmpty()) {
            pages.getRoot().forEach(r -> {
                EtrExchangeUnitCostDto dto = unitCostMap.get(r.getPortfolioId() + r.getFinprodId());
                if (dto != null) {
                    // 1.持仓成本（元）
                    r.setPositionsAmt(dto.getBuyCostAmt());
                    // 2.持仓成本价（元） 取单位成本
                    //r.setUnitCost(dto.getUnitCost());
                }
                // 最新净价*组合持仓.数量 - 持仓成本 as 浮动盈亏
                BigDecimal floatFd = ArithmeticUtil.multiply(r.getPrice(), r.getPositionsNum());
                floatFd = ArithmeticUtil.subtract(floatFd, r.getPositionsAmt());
                r.setFloating(floatFd);
                BigDecimal floatPL = ArithmeticUtil.divide(floatFd, ArithmeticUtil.multiply(r.getPrice(), r.getPositionsNum()), 2);
                r.setFloatingPL(floatPL);

            });
        }
        return pages;
    }

    /**
     * 场内指令-标准券可用持仓可用查询
     *
     * @param secDto
     * @return
     */
    public BigDecimal querySecInCanUse(PositionCashSecDto secDto) {
        secDto.setTradePlat(TempConstant.NO_TRADE_BUSINESS_ETR);
        BigDecimal ticketCanUse = etrTradeCommonervice.selectAvailableNum(secDto);
        ticketCanUse = ArithmeticUtil.divide(ticketCanUse, new BigDecimal(100),2, RoundingMode.DOWN); //单位改成万元
        return ticketCanUse;
    }

    /**
     * 场内指令-质押券出库持仓
     *
     * @param req
     * @return
     */
    public BigDecimal querySecOutCanUse(EtrExchangeTradeCommonRequest req) {
        //1、查询标准券可用
        PositionCashSecDto cashSecDto = new PositionCashSecDto();
        BeanUtil.copyPropertiesIgnoreNull(cashSecDto, req); //复制资产信息
        List<PositionCashSecDto> proList = new ArrayList<>();
        cashSecDto.setFinprodId("XN_PRODID999");
        proList.add(cashSecDto);

        BigDecimal secNum = queryProPosition(DictCodeListExt3.资产类型_通用回购融资券可用持仓.getValue(), proList);
        //2、标准券可用/再折算率
        Map<String, Object> param = new HashMap<>();
        param.put("portfolioId", req.getPortfolioId());
        param.put("finprodId", req.getFinprodId());
        param.put("acctId", req.getAcctId());
        param.put("MORE_CONVRATE", 1);
        param.put("date", iPlatformDate.getSysDate());
        Map<String, Object> scaleMap = etrExchangeTradeCommonMapper.findTandardRate(param);
        BigDecimal scale = new BigDecimal(1);
        BigDecimal tandardRate = new BigDecimal(1);
        if (scaleMap != null) {
            if (scaleMap.get("TANDARDRATE") != null) {
                tandardRate = new BigDecimal(scaleMap.get("TANDARDRATE").toString());//再折算率
            }
            if (scaleMap.get("SCALE") != null) {
                scale = new BigDecimal(scaleMap.get("SCALE").toString());//折算率
            }
        }
        //secNum= ArithmeticUtil.divide(secNum, 100, 0);
        secNum = ArithmeticUtil.divide(secNum, tandardRate, 0);
        //3、A对应的再折算前持仓
        cashSecDto.setFinprodId(req.getFinprodId());
        BigDecimal canSecNum = queryProPosition(DictCodeListExt3.资产类型_通用回购融资券折算前额度.getValue(), proList);
        BigDecimal faceValue=etrExchangeTradeCommon001Service.selectSecFaceValue(param);
        //canSecNum=ArithmeticUtil.multiply(canSecNum,faceValue);
        //4、标准券可用/折算率和 A对应的再折算前持仓 比较
        //canSecNum = ArithmeticUtil.divide(canSecNum, scale, 0);
        BigDecimal miniSecNum = canSecNum;
        if (secNum.compareTo(canSecNum) == -1) {
            miniSecNum = secNum;
        }
        //5、较小值/债券的折算率
        miniSecNum = ArithmeticUtil.divide(miniSecNum, faceValue, 0);
        miniSecNum = ArithmeticUtil.divide(miniSecNum, scale, 0);
        miniSecNum = ArithmeticUtil.multiply(miniSecNum,new BigDecimal(100));
        miniSecNum=ArithmeticUtil.divide(miniSecNum, new BigDecimal(100),2, RoundingMode.DOWN); //单位改成万元
        return miniSecNum;
    }

    //共通
    public BigDecimal queryProPosition(String holdingType, List<PositionCashSecDto> list) {
        List<PositionCashSecDto> secList = new ArrayList<>();
        for (PositionCashSecDto temp : list) {
            temp.setHoldingType(holdingType);
            secList.add(temp);
        }
        BigDecimal newSecAmount = etrTradeCommonervice.queryPosition(secList, "02", "");
//        if (newSecAmount != null) {
//            newSecAmount = ArithmeticUtil.divide(newSecAmount, 100, 0);
//        }
        return newSecAmount;

    }

}
