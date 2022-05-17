package com.joyintech.tams.exchange.service.trade.management.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.OrderPlaceBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeList;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt1;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.InstructionDetail;
import com.joyintech.tams.exchange.common.db.domain.PortfolioDetail;
import com.joyintech.tams.exchange.common.dto.PortfolioInstructionContextDto;
import com.joyintech.tams.exchange.common.enums.exchange.PortfolioDetailBusiTypeEnum;
import com.joyintech.tams.exchange.common.microservice.ServiceApplication;
import com.joyintech.tams.exchange.common.microservice.TradeServiceURL;
import com.joyintech.tams.exchange.common.request.PortfolioInfoRequest;
import com.joyintech.tams.exchange.common.response.PortfolioAllocationResponse;
import com.joyintech.tams.exchange.common.response.PortfolioCalculateResponse;
import com.joyintech.tams.exchange.common.response.PortfolioInfoResponse;
import com.joyintech.tams.exchange.common.utils.*;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionDetailService;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionService;
import com.joyintech.tams.exchange.service.trade.processor.portfolio.PortfolioAssignHelper;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecDto;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecRequest;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecSingleResp;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeCommonervice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.exchange.common.constant.joyin.BussinessDictCodeList.币种_人民币;
import static com.joyintech.tams.exchange.common.constant.joyin.BussinessDictCodeList.是否_否;

/**
 * 组合信息Service
 *
 * @author swwan
 * @date 2021/11/23 - 10:12
 */
@Service
public class PortfolioManager extends BaseService {

    public static final String AMOUNT = "amount";
    private static final Logger log = LoggerFactory.getLogger(PortfolioManager.class);
    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextPath.single:false}")
    private boolean isSingle;

    @Autowired
    private EtrTradeCommonervice etrTradeCommonervice;
    @Autowired
    private InstructionService instructionService;
    @Autowired
    private InstructionDetailService instructionDetailService;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private PortfolioAssignHelper portfolioAssignHelper;

    public List<BaseMap> queryPortfolioInfo(PositionCashSecRequest bean) {
        bean.setFlowType(EtrTradeCommonervice.FLOW_TYPE_TRD);
        return etrTradeCommonervice.queryPositionInfo(bean);
    }

    private List<PositionCashSecSingleResp> queryPortfolioInfoByTg(List<InstructionDetail> instructionDetails, Instruction instruction) {
        List<PositionCashSecDto> positionCashSecDtos = new ArrayList<>(instructionDetails.size());
        PositionCashSecDto dto;
        // 组装组合信息查询请求对象
        for (InstructionDetail detail : instructionDetails) {
            dto = new PositionCashSecDto();

            dto.setPortfolioId(detail.getPortfolioId());
            dto.setAcctId(instruction.getAcctNo());
            dto.setPs(instruction.getPs());
            dto.setTradeMarket(instruction.getTradeMarket());
            dto.setSecAcctId(instruction.getSecAcctId());
            dto.setFinprodId(instruction.getFinprodId());
            dto.setCcy(币种_人民币.getValue());
            dto.setCdate(DateUtil.getSysDate());
            dto.setDateType(null);
            dto.setFinprodType2(instruction.getFinprodType2());
            dto.setTradePlat(instruction.getTradePlat());

            positionCashSecDtos.add(dto);
        }

        PositionCashSecRequest request = new PositionCashSecRequest();
        request.setFlowType(EtrTradeCommonervice.FLOW_TYPE_TRD);
        request.setPtlList(positionCashSecDtos);

        List<PositionCashSecSingleResp> resps = etrTradeCommonervice.queryPositionInfoSingle(request);
        log.debug("查询头寸信息：request = [{}], response = [{}]", JSONUtil.toJsonStr(request), JSONUtil.toJsonStr(resps));

        return resps;
    }

    /**
     * 查询组合信息
     */
    public ResultBean queryPortfolioInfo(PortfolioInfoRequest request) {
        log.info("queryPortfolioInfo() called with parameters => 【request = {}】", request.toString());

        // 1. 获取组合列表
        String[] portfolioIdArray = StringUtil.split(request.getPortfolioId(), StrUtil.COMMA);

        // 返回的map： key 组合代码； value 组合现金持仓
        Map<String, PortfolioInfoResponse> result = new HashMap<>(8);

        // 查询入参
        Map<String, Object> paramMap = BeanUtil.beanToMap(request);
        paramMap.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
        paramMap.put("acctId", request.getAcctNo());
        String calId = this.getCalId(request.getTradeMarket());
        if (StrUtil.isBlank(calId)) {
            return ResultBean.error(ErrorCode.REQ_PARAMETER_ERROR);
        }
        paramMap.put("calId", calId);

        // 2. 遍历查询组合信息
        try {
            for (String s : portfolioIdArray) {
                paramMap.put("portfolioId", s);

                PortfolioInfoResponse portfolioInfoResponse = ((PortfolioInfoResponse) queryPortfolioCash(paramMap).getData());
                PortfolioInfoResponse positionPortfolioInfoResponse = ((PortfolioInfoResponse) queryPortfolioPosition(paramMap).getData());

                portfolioInfoResponse.setProAvailablePositionT0(positionPortfolioInfoResponse.getProAvailablePositionT0());
                portfolioInfoResponse.setProAvailablePositionT1(positionPortfolioInfoResponse.getProAvailablePositionT1());

                result.put(s, portfolioInfoResponse);
            }
        } catch (Exception e) {
            log.error("queryPortfolioInfo() called with exception => 【request = {}】", request);
            log.error("exception info : ", e);
            return ResultBean.error(ErrorCode.API_RETURN_ERROR);
        }
        return ResultBean.success(result);
    }

    /**
     * 查询组合现金信息
     *
     * @param portfolioIds 组合代码
     * @param acctNo       资金账户账号
     * @return 组合现金信息
     */
    public ResultBean queryPortfolioCash(String portfolioIds, String acctNo) {
        log.info("queryPortfolioCash() called with parameters => 【portfolioIds = {}】, 【acctNo = {}】", portfolioIds, acctNo);

        // 1. 获取组合列表
        String[] portfolioIdArray = StringUtil.split(portfolioIds, StrUtil.COMMA);

        // 返回的map： key 组合代码； value 组合现金信息
        Map<String, PortfolioInfoResponse> result = new HashMap<>(8);

        // 查询入参
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
        paramMap.put("acctId", acctNo);

        // 2. 遍历查询组合现金
        try {
            for (String s : portfolioIdArray) {
                paramMap.put("portfolioId", s);
                PortfolioInfoResponse portfolioInfoResponse = ((PortfolioInfoResponse) queryPortfolioCash(paramMap).getData());
                result.put(s, portfolioInfoResponse);
            }
        } catch (Exception e) {
            log.error("queryPortfolioCash() called with exception => 【portfolioIds = {}】, 【acctNo = {}】", portfolioIds, acctNo);
            log.error("exception info : ", e);
            return new ResultBean(ErrorCode.API_RETURN_ERROR);
        }

        return ResultBean.success(result);
    }

    /**
     * 查询组合持仓信息
     *
     * @param portfolioIds 组合代码
     * @param finprodId    产品代码
     * @param secAcctId    证券账户
     * @param tradeMarket  交易场所
     * @return 组合持仓信息
     */
    public ResultBean queryPortfolioPosition(String portfolioIds, String finprodId, String secAcctId, String tradeMarket) {
        log.info("queryPortfolioPosition() called with parameters => 【portfolioIds = {}】, 【finprodId = {}】, 【secAcctId = {}】, 【tradeMarket = {}】",
                portfolioIds, finprodId, secAcctId, tradeMarket);

        // 1. 获取组合列表
        String[] portfolioIdArray = StringUtil.split(portfolioIds, StrUtil.COMMA);

        // 返回的map： key 组合代码； value 组合持仓
        Map<String, PortfolioInfoResponse> result = new HashMap<>();

        // 查询入参
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("finprodId", finprodId);
        paramMap.put("secAcctId", secAcctId);
        String calId = this.getCalId(tradeMarket);
        if (StrUtil.isBlank(calId)) {
            return ResultBean.error(ErrorCode.REQ_PARAMETER_ERROR);
        }
        paramMap.put("calId", calId);

        // 2. 遍历查询组合持仓
        try {
            for (String s : portfolioIdArray) {
                paramMap.put("portfolioId", s);
                PortfolioInfoResponse portfolioInfoResponse = ((PortfolioInfoResponse) queryPortfolioPosition(paramMap).getData());
                result.put(s, portfolioInfoResponse);
            }
        } catch (Exception e) {
            log.error("queryPortfolioPosition() called with exception => 【portfolioIds = {}】, 【finprodId = {}】, 【secAcctId = {}】, 【tradeMarket = {}】",
                    portfolioIds, finprodId, secAcctId, tradeMarket);
            log.error("exception info : ", e);
            return new ResultBean(ErrorCode.API_RETURN_ERROR);
        }

        return ResultBean.success(result);
    }

    /**
     * 微服务查询组合现金信息
     *
     * @param paramMap 入参
     * @return 组合信息对象
     */
    private ResultBean queryPortfolioCash(Map<String, Object> paramMap) {
        // 查询URL
        String url = ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.getPortfolioCash;

        // 查询结果
        Map<String, Object> resultMap = postForMap(url, paramMap);
        if (Objects.isNull(resultMap) || resultMap.size() == 0) {
            log.warn("调用投管接口失败: url = [{}], param = [{}]", url, JSONUtil.toJsonStr(paramMap));
        }

        // 封装结果
        PortfolioInfoResponse portfolioInfoResponse = new PortfolioInfoResponse();
        portfolioInfoResponse.setPortfolioId((String) paramMap.get("portfolioId"));
        portfolioInfoResponse.setProAvailableCashT0(getValueFromMap(resultMap, "cashT"));
        portfolioInfoResponse.setProAvailableCashT1(getValueFromMap(resultMap, "cashT1"));
        portfolioInfoResponse.setSecManageAvailableCashT0(getValueFromMap(resultMap, "cashT3"));
        portfolioInfoResponse.setSecManageAvailableCashT1(getValueFromMap(resultMap, "cashTT1"));

        return ResultBean.success(portfolioInfoResponse);
    }

    /**
     * 微服务查询组合持仓信息
     *
     * @param paramMap 入参
     * @return 组合信息对象
     */
    public ResultBean queryPortfolioPosition(Map<String, Object> paramMap) {
        log.info("queryPortfolioPosition() called with parameters => 【paramMap = {}】", paramMap);

        // 查询URL
        String url = ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.getPortfolioPosition;

        // 查询结果
        Map<String, Object> resultMap = postForMap(url, paramMap);
        if (Objects.isNull(resultMap) || resultMap.size() == 0) {
            log.warn("调用投管接口失败: url = [{}], param = [{}]", url, JSONUtil.toJsonStr(paramMap));
        }

        // 封装结果
        PortfolioInfoResponse portfolioInfoResponse = new PortfolioInfoResponse();
        portfolioInfoResponse.setPortfolioId((String) paramMap.get("portfolioId"));
        portfolioInfoResponse.setProAvailablePositionT0(getValueFromMap(resultMap, "position"));
        portfolioInfoResponse.setProAvailablePositionT1(getValueFromMap(resultMap, "positionT1"));

        return ResultBean.success(portfolioInfoResponse);
    }

    private BigDecimal getValueFromMap(Map<String, Object> resultMap, String code) {
        if (resultMap == null) {
            return BigDecimal.ZERO;
        }
        return Objects.isNull(resultMap.get(code)) ? BigDecimal.ZERO : new BigDecimal((String) resultMap.get(code));
    }

    /**
     * 调用微服务
     *
     * @param url      接口url
     * @param paramMap 入参
     * @return 查询结果
     */
    private Map postForMap(String url, Map<String, Object> paramMap) {

        if (isSingle) {
            return simpleRestTemplate.postForObject(url, paramMap, Map.class);
        }
        return restTemplate.postForObject(url, paramMap, Map.class);
    }

    /**
     * 根据交易场所获取交易日历
     *
     * @param tradeMarket 交易场所
     * @return 交易日历
     */
    private String getCalId(String tradeMarket) {
        if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(tradeMarket)) {
            return "04";
        } else if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(tradeMarket)) {
            return "05";
        }
        return StrUtil.EMPTY;
    }

    /**
     * 组合计算
     */
    public ResultBean queryPortfolioCalculate(OrderPlaceBean bean) {
        return queryPortfolioCalculate(bean, true);
    }

    /**
     * 组合计算
     *
     * @param bean     下单对象
     * @param infoFlag 是否查询组合信息
     * @return 组合计算结果
     */
    /*public ResultBean queryPortfolioCalculateV1(OrderPlaceBean bean, boolean infoFlag) {
        log.info("queryPortfolioCalculate() called with parameters => 【bean = {}】", bean);
        // 获取指令
        String instructionId = bean.getInstructionId();
        Instruction instruction = instructionService.findById(instructionId);
        if (Objects.isNull(instruction)) {
            log.warn("未找到指令信息: InstructionId [{}]", instructionId);
            return ResultBean.error(ErrorCode.INSTRUCTIONS_NOT_EXIST);
        }
        BigDecimal orginEntrustPrice = bean.getEntrustPrice();
        // 债券交易类，使用全价进行计算
//        coverPrice(instruction, order);
        // 获取委托属性
        String entrustProp = EntrustPropUtils.getEntrustProp(instruction, bean.getEntrustProp());

        // 获取多组合
        List<InstructionDetail> instructionDetails = instructionDetailService.queryInstructionDetailsByInstructionId(instructionId);

        if (Objects.isNull(instructionDetails) || instructionDetails.size() == 0) {
            // 单组合时处理
            InstructionDetail instructionDetail = new InstructionDetail();
            instructionDetail.setEntrustId(instruction.getEntrustId());
            instructionDetail.setPortfolioId(instruction.getPortfolioId());
            instructionDetail.setPortfolioName(instruction.getPortfolioName());
            instructionDetail.setDisRate(BigDecimal.valueOf(1.00));
            instructionDetail.setEntrustAmt(instruction.getEntrustAmt());
            instructionDetail.setEntrustNum(new BigDecimal(instruction.getEntrustNum()));
            instructionDetail.setTargetType(instruction.getEntrustNum() == 0 ? "02" : "01");
            instructionDetails.add(instructionDetail);
        } else if (instructionDetails.size() == 1) {
            InstructionDetail instructionDetail = instructionDetails.get(0);
            if (instructionDetail.getDisRate() == null || instructionDetail.getDisRate().compareTo(new BigDecimal("0")) == 0) {
                instructionDetail.setDisRate(BigDecimal.valueOf(1.00));
            }
        }

        Map<String, PortfolioInfoResponse> portfolioInfoResponseMap = Maps.newHashMap();
        if (infoFlag) {
            // modify by swwan 调用投管查询头寸方法 start
            List<PositionCashSecSingleResp> positionCashSecSingleResps = this.queryPortfolioInfoByTg(instructionDetails, instruction);
            PortfolioInfoResponse infoResponse;
            for (PositionCashSecSingleResp item : positionCashSecSingleResps) {
                List<BaseMap> baseMapList = item.getBaseMapList();

                infoResponse = new PortfolioInfoResponse();
                infoResponse.setPortfolioId(item.getPortfolioId());
                infoResponse.setProAvailablePositionT0((BigDecimal) baseMapList.get(3).get(AMOUNT));
                infoResponse.setProAvailablePositionT1(null);
                infoResponse.setProAvailableCashT0((BigDecimal) baseMapList.get(0).get(AMOUNT));
                infoResponse.setProAvailableCashT1(null);
                infoResponse.setSecManageAvailableCashT0((BigDecimal) baseMapList.get(1).get(AMOUNT));
                infoResponse.setSecManageAvailableCashT1(null);

                portfolioInfoResponseMap.put(infoResponse.getPortfolioId(), infoResponse);
            }
//            // - 查询组合信息
//            PortfolioInfoRequest portfolioInfoRequest = new PortfolioInfoRequest();
//            List<String> portfolios = instructionDetails.stream().map(InstructionDetail::getPortfolioId).collect(Collectors.toList());
//            portfolioInfoRequest.setPortfolioId(ArrayUtil.join(portfolios.toArray(), StrUtil.COMMA));
//            portfolioInfoRequest.setFinprodId(instruction.getFinprodId());
//            portfolioInfoRequest.setSecAcctId(instruction.getSecAcctId());
//            portfolioInfoRequest.setAcctNo(instruction.getAcctNo());
//            portfolioInfoRequest.setTradeMarket(instruction.getTradeMarket());
//            portfolioInfoRequest.setVdate(DateUtil.platDateFromCache());
//
//            ResultBean portfolioInfoResult = this.queryPortfolioInfo(portfolioInfoRequest);
//            portfolioInfoResponseMap.putAll((Map<String, PortfolioInfoResponse>) portfolioInfoResult.getData());
            // modify by swwan 调用投管查询头寸方法 end
        }

        // 计算结果列表
        List<PortfolioCalculateResponse> list = new ArrayList<>(8);
        // 计算结果
        PortfolioCalculateResponse calculateResponse = null;

        // 最小交易单元：整数倍
        BigDecimal minStep = this.getMinStep(instruction);

        //市价委托填充反向涨跌停价
        BigDecimal price = bean.getEntrustPrice();
        if (EntrustProp.isMarketPrice(entrustProp)) {
            ResultBean tickBean = quoteService.getTick(bean.getStockCode());
//            QuoteProto.TickBean tick = quoteTickService.getTickFromCache(bean.getStockCode());
            if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
                TickBean tick = (TickBean) tickBean.getData();
                if (bean.getEntrustBs().equals(EntrustBs.SELL.value)) {
                    price = TradeUtils.getBigDecimal(tick.getLowerLimitPrice(), 4);
                } else {
                    price = TradeUtils.getBigDecimal(tick.getUpperLimitPrice(), 4);
                }
            }
        }
        // 特殊处理 质押式回购交易，将委托价格设置为100
        if (EntrustUtils.isPledgeRepurchase(instruction)) {
            price = BigDecimal.valueOf(100L);
        }

        if (null == price) {
            price = BigDecimal.ZERO;
        }

        String portfolioId;
        BigDecimal entrustVolume = BigDecimal.ZERO;
        BigDecimal entrustAmt = BigDecimal.ZERO;
        BigDecimal disRate = BigDecimal.ZERO;

        // 订单类型：数量单，指令单
        boolean isVolume = instruction.getEntrustNum() > 0L;
        // 订单数量
        BigDecimal orderVolume = BigDecimal.valueOf(bean.getEntrustVolume());
        // 订单金额
        BigDecimal orderAmt = NumberUtil.mul(price, orderVolume);

        for (InstructionDetail detail : instructionDetails) {
            calculateResponse = new PortfolioCalculateResponse();

            portfolioId = detail.getPortfolioId();
            calculateResponse.setPortfolioInfo(portfolioInfoResponseMap.get(portfolioId));
            calculateResponse.setPortfolioId(portfolioId);
            calculateResponse.setPortfolioName(detail.getPortfolioName());
            calculateResponse.setFinprodMarketId(instruction.getFinprodMarketId());
//            calculateResponse.setEntrustPrice(bean.getEntrustPrice().compareTo(BigDecimal.ZERO) == 0 ? price : bean.getEntrustPrice());
            calculateResponse.setEntrustPrice(price);
            calculateResponse.setFinprodName(instruction.getFinprodName());
            calculateResponse.setDisRate(detail.getDisRate());
            calculateResponse.setEntrustProp(entrustProp);
//            calculateResponse.setInstructionLeftAmt(isVolume ? BigDecimal.ZERO : ArithmeticUtil.subtract(ArithmeticUtil.subtract(instruction.getEntrustAmt(), instruction.getEntrustLeftAmt()), instruction.getFrozenQuantity()));
//            calculateResponse.setInstructionLeftNum(isVolume ? instruction.getEntrustNum() - instruction.getEntrustLeftAmt().longValue() - instruction.getFrozenQuantity().longValue() : 0L);
//            calculateResponse.setInstructionTotalAmt(instruction.getEntrustAmt());
            calculateResponse.setInstructionTotalNum(detail.getEntrustAmt().longValue());
//            calculateResponse.setInstructionTotalNum(instruction.getEntrustNum());
            calculateResponse.setInstructionTotalNum(detail.getEntrustNum().longValue());
            if (Objects.nonNull(bean.getFullPrice())) {
                calculateResponse.setFullPrice(bean.getFullPrice());
            }
            calculateResponse.setPriceMode(instruction.getPriceMode());
            calculateResponse.setAcctNo(instruction.getAcctName());

            // - 计算组合委托额度
            disRate = isVolume ? (detail.getEntrustNum().divide(BigDecimal.valueOf(instruction.getEntrustNum()), 8, RoundingMode.HALF_UP)) : (detail.getEntrustAmt().divide(instruction.getEntrustAmt(), 8, RoundingMode.HALF_UP));
            // 数量
            if (TradeUtils.bigThanZero(orderVolume) && TradeUtils.bigThanZero(disRate)) {
                // 根据比例计算委托数量
                entrustVolume = isVolume ?
                        ((orderVolume.multiply(detail.getEntrustNum())).divide(BigDecimal.valueOf(instruction.getEntrustNum()), 8, RoundingMode.HALF_UP)) :
                        ((orderVolume.multiply(detail.getEntrustAmt())).divide(instruction.getEntrustAmt(), 8, RoundingMode.HALF_UP));
                if (TradeUtils.lessThan(entrustVolume, minStep)) {
                    // 小于最小交易单位时的处理，置为0
                    entrustVolume = BigDecimal.ZERO;
                } else {
                    // 大于等于最小交易单位时的处理，最小交易单位整数倍
                    entrustVolume = TradeUtils.subtractMod(entrustVolume, minStep);
                }
//                entrustAmt = NumberUtil.mul(entrustVolume, price);
                entrustAmt = entrustVolume.multiply(price).setScale(2, RoundingMode.HALF_UP);
            }

            log.debug("根据分配比例计算，组合名称 = [{}]， 组合编号 = [{}]，订单数量 = [{}]， 分配比例 = [{}]， 委托数量 = [{}]，委托金额 = [{}]",
                    calculateResponse.getPortfolioName(),
                    calculateResponse.getPortfolioId(),
                    orderVolume,
                    disRate,
                    entrustVolume,
                    entrustAmt);
            calculateResponse.setEntrustNum(entrustVolume.longValue());
            calculateResponse.setEntrustAmt(entrustAmt);

            list.add(calculateResponse);
        }

        // 尾差处理
        PortfolioCalculateResponse maxPortfolioCalculateResponse = list.stream().max(Comparator.comparing(PortfolioCalculateResponse::getDisRate)).get();
        long sum = list.stream().filter(response -> !StrUtil.equals(response.getPortfolioId(), maxPortfolioCalculateResponse.getPortfolioId()))
                .mapToLong(PortfolioCalculateResponse::getEntrustNum)
                .sum();
        long tailVolume = bean.getEntrustVolume() - sum;
        BigDecimal tailAmt = price.multiply(BigDecimal.valueOf(tailVolume)).setScale(2, RoundingMode.HALF_UP);
        log.debug("尾插处理，组合名称 = [{}], 组合编号 = [{}], 原委托数量 = [{}], 现委托数量 = [{}]，现委托金额 = [{}]",
                maxPortfolioCalculateResponse.getPortfolioName(),
                maxPortfolioCalculateResponse.getPortfolioId(),
                maxPortfolioCalculateResponse.getEntrustNum(),
                tailVolume,
                tailAmt);
        maxPortfolioCalculateResponse.setEntrustNum(tailVolume);
        maxPortfolioCalculateResponse.setEntrustAmt(tailAmt);

        if (isVolume && DictCodeListExt3.交易方向_债转股.getValue().equals(instruction.getPs())) {
            long bondPaperValue = 100L;
            long totalConNum = list.get(0).getInstructionLeftNum() * bondPaperValue / list.get(0).getEntrustPrice().longValue();
            for (PortfolioCalculateResponse response : list) {
                response.setConNum(totalConNum * response.getEntrustNum() / instruction.getEntrustNum());
            }
        }

        // 处理指令剩余数量、指令剩余金额
        BigDecimal leftAmt = isVolume ? BigDecimal.ZERO : NumberUtil.sub(instruction.getEntrustAmt(), instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity());
        long leftNum = isVolume ? NumberUtil.sub(instruction.getEntrustNum(), instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity()).longValue() : 0L;
        for (PortfolioCalculateResponse item : list) {
            calculateResponse.setInstructionLeftAmt(NumberUtil.div(NumberUtil.mul(leftAmt, item.getEntrustAmt()), orderAmt));
            calculateResponse.setInstructionLeftNum(NumberUtil.div(leftNum * item.getEntrustNum(), orderVolume).longValue());
        }
        // 债券交易类，使用全价进行计算后恢复订单价格
//        recoverPrice(instruction, list, orginEntrustPrice, entrustProp);

        log.debug("queryPortfolioCalculate() called with result => 【list = {}】", JSONUtil.toJsonStr(list));
        return ResultBean.success(list);
    }*/


    /**
     * 组合计算
     *
     * @param order    下单对象
     * @param infoFlag 是否查询组合信息
     * @return 组合计算结果
     */
    public ResultBean queryPortfolioCalculate(OrderPlaceBean order, boolean infoFlag) {
        log.info("queryPortfolioCalculate() called with parameters => 【order = {}】", order);

        // 获取指令
        String instructionId = order.getInstructionId();
        Instruction instruction = instructionService.findById(instructionId);

        //处理委托价格
        coverPrice(instruction, order);

        ResultBean assignResult = portfolioAssignHelper.queryPortfolioAssign(order.getInstructionId(),
                PortfolioDetailBusiTypeEnum.ORDER, BigDecimal.valueOf(order.getEntrustVolume()),
                order.getEntrustPrice(), order.getEntrustProp());
        if (assignResult.isFail()) {
            return assignResult;
        }
        Map<String, PortfolioDetail> portfolioDetailMap = (Map<String, PortfolioDetail>) assignResult.getData();

        List<InstructionDetail> instructionDetails = instructionDetailService.queryInstructionDetailsByInstructionId(instructionId);

        Map<String, PortfolioInfoResponse> portfolioInfoResponseMap = Maps.newHashMap();
        if (infoFlag) {
            // modify by swwan 调用投管查询头寸方法 start
            List<PositionCashSecSingleResp> positionCashSecSingleResps = this.queryPortfolioInfoByTg(instructionDetails, instruction);
            PortfolioInfoResponse infoResponse;
            for (PositionCashSecSingleResp item : positionCashSecSingleResps) {
                List<BaseMap> baseMapList = item.getBaseMapList();

                infoResponse = new PortfolioInfoResponse();
                infoResponse.setPortfolioId(item.getPortfolioId());
                infoResponse.setProAvailablePositionT0((ObjectUtil.isNotNull(baseMapList.get(3).get(AMOUNT))) ? (BigDecimal) baseMapList.get(3).get(AMOUNT) : BigDecimal.ZERO);
                infoResponse.setProAvailablePositionT1(BigDecimal.ZERO);
                infoResponse.setProAvailableCashT0((ObjectUtil.isNotNull(baseMapList.get(0).get(AMOUNT))) ? (BigDecimal) baseMapList.get(0).get(AMOUNT) : BigDecimal.ZERO);
                infoResponse.setProAvailableCashT1(BigDecimal.ZERO);
                infoResponse.setSecManageAvailableCashT0((ObjectUtil.isNotNull(baseMapList.get(1).get(AMOUNT))) ? (BigDecimal) baseMapList.get(1).get(AMOUNT) : BigDecimal.ZERO);
                infoResponse.setSecManageAvailableCashT1(BigDecimal.ZERO);

                portfolioInfoResponseMap.put(infoResponse.getPortfolioId(), infoResponse);
            }
        }

        // 计算结果列表
        List<PortfolioCalculateResponse> list = new ArrayList<>(8);
        // 计算结果
        PortfolioCalculateResponse calculateResponse = null;
        for (InstructionDetail detail : instructionDetails) {
            calculateResponse = new PortfolioCalculateResponse();

            String portfolioId = detail.getPortfolioId();
            calculateResponse.setPortfolioInfo(portfolioInfoResponseMap.get(portfolioId));
            calculateResponse.setPortfolioId(portfolioId);
            calculateResponse.setPortfolioName(detail.getPortfolioName());
            calculateResponse.setFinprodMarketId(instruction.getFinprodMarketId());
            calculateResponse.setFinprodName(instruction.getFinprodName());
            calculateResponse.setDisRate(detail.getDisRate());
            calculateResponse.setInstructionTotalAmt(detail.getEntrustAmt());
            calculateResponse.setInstructionTotalNum(detail.getEntrustNum().longValue());
            if (Objects.nonNull(order.getFullPrice())) {
                calculateResponse.setFullPrice(order.getFullPrice());
            }
            calculateResponse.setPriceMode(instruction.getPriceMode());
            calculateResponse.setAcctNo(instruction.getAcctName());

            PortfolioDetail portfolioDetail = portfolioDetailMap.get(portfolioId);
            if (ObjectUtil.isNotNull(portfolioDetail)) {
                calculateResponse.setEntrustNum(portfolioDetail.getTargetNum().longValue());
                calculateResponse.setEntrustAmt(portfolioDetail.getTargetAmt());
                calculateResponse.setEntrustPrice(NumberUtil.div(calculateResponse.getEntrustAmt(), calculateResponse.getEntrustNum()));
            }

            list.add(calculateResponse);
        }

        // 订单类型：数量单，指令单
        boolean isVolume = instruction.volumeInstruction();
        // 订单数量
        BigDecimal orderVolume = BigDecimal.valueOf(order.getEntrustVolume());
        // 订单金额
        BigDecimal orderAmt = NumberUtil.mul(list.get(0).getEntrustPrice(), orderVolume);

        if (isVolume && DictCodeListExt3.交易方向_债转股.getValue().equals(instruction.getPs())) {
            long bondPaperValue = 100L;
            long totalConNum = list.get(0).getInstructionLeftNum() * bondPaperValue / list.get(0).getEntrustPrice().longValue();
            for (PortfolioCalculateResponse response : list) {
                response.setConNum(totalConNum * response.getEntrustNum() / instruction.getEntrustNum());
            }
        }

        // 处理指令剩余数量、指令剩余金额
        BigDecimal leftAmt = isVolume ? BigDecimal.ZERO : NumberUtil.sub(instruction.getEntrustAmt(), instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity());
        long leftNum = isVolume ? NumberUtil.sub(instruction.getEntrustNum(), instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity()).longValue() : 0L;
        for (PortfolioCalculateResponse item : list) {
            calculateResponse.setInstructionLeftAmt(NumberUtil.div(NumberUtil.mul(leftAmt, item.getEntrustAmt()), orderAmt));
            calculateResponse.setInstructionLeftNum(NumberUtil.div(leftNum * item.getEntrustNum(), orderVolume).longValue());
        }

        log.debug("queryPortfolioCalculate() called with result => 【list = {}】", JSONUtil.toJsonStr(list));
        return ResultBean.success(list);
    }

    private void coverPrice(Instruction instruction, OrderPlaceBean order) {
        if (InvestmentAssetType.BOND.codeEquals(instruction.getAssetType())
                && EnumUtil.in(instruction.getPs(), InstructionPs.BUY, InstructionPs.SELL)) {
            BigDecimal fullPrice = order.getFullPrice();
            if (ObjectUtil.isNull(fullPrice) || fullPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR, StrUtil.format("全价不正确 =>【fullPrice = {}】", fullPrice));
            }
            order.setEntrustPrice(fullPrice);
        }
    }

    private void recoverPrice(Instruction instruction, List<PortfolioCalculateResponse> list, BigDecimal price, String entrustProp) {
        if (InvestmentAssetType.BOND.codeEquals(instruction.getAssetType())
                && EnumUtil.in(instruction.getPs(), InstructionPs.BUY, InstructionPs.SELL)) {
            list.forEach(item -> item.setEntrustPrice(price));
        }
    }

    /**
     * 组合计算——兼容公平模式
     */
    public ResultBean queryPortfolioCalculateWithFair(OrderPlaceBean bean) {
        log.info("queryPortfolioCalculateWithFair() called with parameters => 【bean = {}】", bean);

        // 方法返回
        PortfolioAllocationResponse result = new PortfolioAllocationResponse();

        // 获取指令
        String instructionId = bean.getInstructionId();
        Instruction instruction = instructionService.findById(instructionId);
        if (Objects.isNull(instruction)) {
            log.warn("未找到指令信息: InstructionId [{}]", instructionId);
            result.setInfoLevel("1");
            result.setInfoDetails(ErrorCode.getStr(ErrorCode.INSTRUCTIONS_NOT_EXIST));
            return ResultBean.success(result);
        }

        // 过滤公平相关指令
        // 1. 同交易员 2. 同证券编码 3. 同交易方向；交易方向是【买入，卖出，正回购，逆回购】
        // 4. 指令状态——已接收 5. 交易平台——沪深竞价撮合平台,新债平台（匹配成交||未指定） 6. 指令进度——未100%
        List<Instruction> simpleFiltingList = instructionService.queryInstructionByUserIdAndStockCode(instruction.getTradeId(), instruction.getFinprodMarketId())
                .stream().filter(item ->
                        instruction.getPs().equals(item.getPs())
                                && InvestmentUtils.filterFairInstructionByPs(item.getPs())
                                && InstructionStatus.ALREADY_RECEIVE.value.equals(item.getEntrustStatus())
                                && InvestmentUtils.filterFairInstructionByTradePlatAndDealMode(item.getTradePlat(), item.getDealMode())
                                && !InvestmentUtils.isInstructionProcessComplete(item))
                .collect(Collectors.toList());
        log.debug("初步过滤出公平相关指令：{}", JSONUtil.toJsonStr(simpleFiltingList));
        result.setList(simpleFiltingList);

        // 判断进入公平交易
        if (!EntrustUtils.isFairOrder(bean, instruction)) {
            log.warn("非公平交易模式，无法进行公平交易！param = {}", JSONUtil.toJsonStr(bean));
            result.setInfoLevel("1");
            result.setInfoDetails(ErrorCode.getStr(ErrorCode.NOT_FAIR_INSTRUCTION_ERROR));
            return ResultBean.success(result);
        }

        // 行情数据
        ResultBean tickBean = quoteService.getTick(bean.getStockCode());
        if (ErrorCode.isFail(tickBean.getErrorCode())) {
            if (!ErrorCode.isSuccess(tickBean.getErrorCode())) {
                log.warn("未找到行情信息: stockId [{}]", instruction.getFinprodMarketId());
                result.setInfoLevel("1");
                result.setInfoDetails(ErrorCode.getStr(ErrorCode.QUOTE_NOT_EXIT));
                return ResultBean.success(result);
            }
        }

        // 系统参数：未接收指令的公平处理模式 01：是；02：否
        /*
            01 若存在部分指令未接收则提示“存在需要执行公平交易但未接收的指令，请先接收指令！”，
                需要等所有指令均接收之后，方可下达委托;
            02 忽略未接收的部分指令
         */
        if (EntrustUtils.isfairModeWithUnacceptInstructionForbid()) {
            List<Instruction> waitReceiveList =
                    instructionService.queryInstructionByUserIdAndStockCode(instruction.getTradeId(), instruction.getFinprodMarketId())
                            .stream()
                            .filter(item ->
                                    instruction.getPs().equals(item.getPs())
                                            && InvestmentUtils.filterFairInstructionByPs(item.getPs())
                                            && InstructionStatus.WAIT_RECEIVE.value.equals(item.getEntrustStatus())
                                            && InvestmentUtils.filterFairInstructionByTradePlatAndDealMode(item.getTradePlat(), instruction.getDealMode())
                                            && !InvestmentUtils.isInstructionProcessComplete(item))
                            .collect(Collectors.toList());

            if (waitReceiveList.size() > 0) {
                log.warn("未接收且影响公平交易的相关指令: {}", JsonUtils.getJsonFromObject(waitReceiveList));
                result.setInfoLevel("1");
                result.setInfoDetails(ErrorCode.getStr(ErrorCode.FAIR_INSTRUCTIONS_UNACCEPT_ERROR));
                return ResultBean.success(result);
            }
        }

        // 获取公平交易相关指令
        TickBean tick = (TickBean) tickBean.getData();
        List<PortfolioInstructionContextDto> fairList = getFairList(bean, instruction, tick, result);
        log.debug(JSONUtil.toJsonStr(fairList));

        // 获取多组合的指令
        List<Instruction> instructionList = getFairInstructionList(fairList, result);
        result.setList(instructionList);

        return ResultBean.success(result);

    }

    /**
     * 获取公平交易相关指令
     */
    public List<PortfolioInstructionContextDto> getFairList(OrderPlaceBean bean, Instruction instruction, TickBean
            tick, PortfolioAllocationResponse result) {

        // 涨停价
        BigDecimal upperLimitPrice = TradeUtils.getBigDecimal(tick.getUpperLimitPrice(), 4);
        // 跌停价
        BigDecimal lowerLimitPrice = TradeUtils.getBigDecimal(tick.getLowerLimitPrice(), 4);

        // 构造返回列表
        List<PortfolioInstructionContextDto> resultList = new ArrayList<>();

        // 获取买卖方向
        String entrustBs = EntrustBsProp.getEntrustBs(instruction.getPs());
        // 获取委托属性
//        String entrustProp = EntrustPropUtils.getEntrustProp(instruction, bean.getEntrustProp());
        // 获取委托价格
        BigDecimal entrustPrice = bean.getEntrustPrice();
        // 调整后的价格
        BigDecimal adjustedPrice = entrustPrice;
        // 市价订单
        // 如果是市价指令+限价订单时，委托价格不为0
        if (EntrustUtils.isMarketPrice(instruction) && TradeUtils.isZero(entrustPrice)) {
            adjustedPrice = this.getMarketPrice(tick, entrustBs);
        }
        result.setPrice(adjustedPrice);

        // 过滤公平相关指令
        // 1. 同交易员 2. 同证券编码 3. 同交易方向
        // 4. 指令状态——已接收 5. 交易平台——沪深竞价撮合平台,新债平台（匹配成交||未指定） 6. 指令进度——未100%
        List<Instruction> questList = instructionService.queryInstructionByUserIdAndStockCode(instruction.getTradeId(), instruction.getFinprodMarketId())
                .stream().filter(item ->
                        instruction.getPs().equals(item.getPs())
                                && InvestmentUtils.filterFairInstructionByPs(item.getPs())
                                && InstructionStatus.ALREADY_RECEIVE.value.equals(item.getEntrustStatus())
                                && InvestmentUtils.filterFairInstructionByTradePlatAndDealMode(item.getTradePlat(), item.getDealMode())
                                && !InvestmentUtils.isInstructionProcessComplete(item))
                .collect(Collectors.toList());
        log.debug("初步过滤出公平相关指令：{}", JSONUtil.toJsonStr(questList));

        // questList 是从内存拿出的数据，不能直接使用
        List<Instruction> tempList = new ArrayList<>(questList.size());
        Instruction tempIns = null;
        for (Instruction ins : questList) {
            tempIns = new Instruction();
            BeanUtil.copyProperties(ins, tempIns);
            tempList.add(tempIns);
        }

        // 委托价格为0的指令（市价指令），将涨跌停价填充到委托价格
        for (Instruction ins : tempList) {
            if (isZeroPriceMarketInstruction(ins)) {
                ins.setEntrustPrice(this.getMarketPrice(tick, entrustBs));
                log.debug("将涨跌停价[{}]填充到委托价格为0的市价指令[{}]", ins.getEntrustPrice(), ins.getEntrustId());
            }
        }

        // 系统参数：委托价格不优于指令价格时的处理模式
        if (EntrustUtils.isfairModeEntrustPriceBestThanInstructionForbid()
                // 首次调用，特殊处理
                || TradeUtils.isZero(entrustPrice)) {
            // 01 若输入委托价格不是优于所有指令价格时，
            // 提示：“委托价格必须优于所有指令价格”，
            // 并自动将委托价格调整为能够满足所有指令价格的最低（高）价格

            // 获取指令列表中最低（高）价格
            if (EntrustBs.isBuy(entrustBs)) {
                BigDecimal min = tempList.stream()
                        .map(Instruction::getEntrustPrice)
                        .min(Comparator.naturalOrder())
                        .orElse(upperLimitPrice);
                if (min.compareTo(upperLimitPrice) > 0) {
                    adjustedPrice = upperLimitPrice;
                } else {
                    adjustedPrice = min;
                }

                // 不能比委托价格更高
                if (entrustPrice.compareTo(BigDecimal.ZERO) != 0 && adjustedPrice.compareTo(entrustPrice) > 0) {
                    adjustedPrice = entrustPrice;
                }

            } else {
                BigDecimal max = tempList.stream()
                        .map(Instruction::getEntrustPrice)
                        .max(Comparator.naturalOrder())
                        .orElse(TradeUtils.getBigDecimal(tick.getLowerLimitPrice(), 4));
                if (max.compareTo(lowerLimitPrice) < 0) {
                    adjustedPrice = lowerLimitPrice;
                } else {
                    adjustedPrice = max;
                }

                // 不能比委托价格更低
                if (entrustPrice.compareTo(BigDecimal.ZERO) != 0 && adjustedPrice.compareTo(entrustPrice) < 0) {
                    adjustedPrice = entrustPrice;
                }
            }

            // 提示：“委托价格必须优于所有指令价格”
            if (adjustedPrice.compareTo(entrustPrice) != 0) {
                // 首次调用，特殊处理
                if (!TradeUtils.isZero(entrustPrice)) {
                    result.setInfoLevel("2");
                    result.setInfoDetails("委托价格必须优于所有指令价格");
                }
                result.setPrice(adjustedPrice);
            }
        } else {
            // 02时，剔除该指令并继续时
            BigDecimal orderPrice = adjustedPrice;
            for (Instruction ins : tempList) {
                if (EntrustBs.isBuy(entrustBs)) {
                    // 买入，过滤价格小于指令价格的指令
                    if (orderPrice.compareTo(ins.getEntrustPrice()) > 0) {
                        ins.setEntrustClass(是否_否.getValue());
                    }
                } else {
                    // 卖出，过滤价格大于指令价格的指令
                    if (orderPrice.compareTo(ins.getEntrustPrice()) < 0) {
                        ins.setEntrustClass(是否_否.getValue());
                    }
                }
            }

            // 系统参数“委托价格不优于指令价格时的处理模式”为2.剔除该指令并继续时，
            // 若委托价格选择市价，则限价指令数量调整为0，并不参与数量分配；
            if (EntrustUtils.isMarketPriceByEntrustProp(bean.getEntrustProp())) {
                for (Instruction ins : tempList) {
                    if (!EntrustUtils.isMarketPrice(ins)) {
                        ins.setEntrustClass(是否_否.getValue());
                    }
                }
            }
        }
        /*
            a)计算每条指令剩余数量：
                数量指令，指令剩余数量即为实际指令剩余数量；
                金额指令，根据指令剩余金额÷委托价格计算得到，需要取整；
                    若委托价格模式为非限价时，股票、基金交易委托价格取涨跌停价格，
                        买入取涨停价，
                        卖出取跌停价；
            b)计算总的指令剩余数：
                每条指令剩余数量之和；
            c)计算每条指令比例：
                每条指令剩余数量÷总的指令剩余数量；
            d)计算每条指令委托数量：
                交易员输入的委托数量×每条指令比例，
                以四舍五入的方式按最小交易单位取整，如股票计算得到的数量为555股，则实际委托数量为600股；若计算得到的委托数量大于指令剩余数量，则取委托数量取指令剩余数量；
         */
        PortfolioInstructionContextDto dto = null;
        for (Instruction ins : tempList) {
            dto = new PortfolioInstructionContextDto();
            dto.setInstruction(ins);
            dto.setEntrustPrice(adjustedPrice);

            if (EntrustUtils.isVolume(ins)) {
                dto.setLeftVolume(ins.getEntrustNum() - ins.getEntrustLeftAmt().longValue() - ins.getFrozenQuantity().longValue());
            } else {
                dto.setLeftVolume(TradeUtils.divide(TradeUtils.subtract(ins.getEntrustAmt(), ins.getEntrustLeftAmt(), ins.getFrozenQuantity(), 2), adjustedPrice, 2).longValue());
            }
            if (是否_否.getValue().equals(ins.getEntrustClass())) {
                dto.setLeftVolume(0L);
            }
            resultList.add(dto);
        }
        // 总剩余指令数量
        Long totalLeftVolume = getTotalLeftVolume(resultList);
        result.setTotalLeftVolume(totalLeftVolume);
        // 首次调用，特殊处理
        if (bean.getEntrustVolume() == 0L) {
            bean.setEntrustVolume(totalLeftVolume);
        }

        // 计算指令的委托数量
        for (PortfolioInstructionContextDto context : resultList) {
            Long realVolume = 0L;
            if (bean.getEntrustVolume() != 0L) {
                if (EntrustUtils.isBond(context.getInstruction())) {
                    realVolume = getEntrustVolume(totalLeftVolume, bean.getEntrustVolume(), context.getLeftVolume(), 1);
                } else if (EntrustUtils.isPledgeRepurchase(context.getInstruction())) {
                    if (Market.SZSE.investValue.equals(context.getInstruction().getTradeMarket())) {
                        realVolume = getEntrustVolume(totalLeftVolume, bean.getEntrustVolume(), context.getLeftVolume(), 1);
                    } else {
                        realVolume = getEntrustVolume(totalLeftVolume, bean.getEntrustVolume(), context.getLeftVolume(), 3);
                    }
                } else if (EntrustUtils.isFutures(context.getInstruction())) {
                    realVolume = getEntrustVolume(totalLeftVolume, bean.getEntrustVolume(), context.getLeftVolume(), 0);
                } else {
                    realVolume = getEntrustVolume(totalLeftVolume, bean.getEntrustVolume(), context.getLeftVolume(), 2);
                }
            }
            context.setEntrustVolume(Math.min(context.getLeftVolume(), realVolume));
            // 分配到指令的委托数量，借用转股数量字段
            context.getInstruction().setConNum(BigDecimal.valueOf(Math.min(context.getLeftVolume(), realVolume)));
        }

        // 计算总委托数量和总委托金额
        long sumEntrustVolume = getTotalEntrustVolume(resultList);
        result.setTotalEntrustVolume(sumEntrustVolume);
        // 特殊处理 质押式回购交易，委托金额=委托数量*100
        if (EntrustUtils.isPledgeRepurchase(instruction)) {
            result.setTotalEntrustAmt(BigDecimal.valueOf(100L).multiply(BigDecimal.valueOf(sumEntrustVolume)));
        } else {
            result.setTotalEntrustAmt(result.getPrice().multiply(BigDecimal.valueOf(sumEntrustVolume)));
        }
        // 首次调用，特殊处理
        if (bean.getEntrustVolume() == 0L) {
            result.setTotalEntrustVolume(result.getTotalLeftVolume());
            // 特殊处理 质押式回购交易，委托金额=委托数量*100
            if (EntrustUtils.isPledgeRepurchase(instruction)) {
                result.setTotalEntrustAmt(BigDecimal.valueOf(100L).multiply(BigDecimal.valueOf(result.getTotalLeftVolume())));
            } else {
                result.setTotalEntrustAmt(result.getPrice().multiply(BigDecimal.valueOf(result.getTotalLeftVolume())));
            }
        }
        for (PortfolioInstructionContextDto context : resultList) {
            // 分配到指令的委托数量比例，借用转股价格字段
            context.getInstruction().setConPrice(ArithmeticUtil.divide(BigDecimal.valueOf(context.getEntrustVolume()), BigDecimal.valueOf(sumEntrustVolume), 4));
        }
        log.debug("公平委托相关指令: {}", JsonUtils.getJsonFromObject(resultList));

        return resultList;
    }

    private boolean isZeroPriceMarketInstruction(Instruction instruction) {
        return EntrustUtils.isMarketPrice(instruction) && BigDecimal.ZERO.compareTo(instruction.getEntrustPrice()) == 0;
    }

    private BigDecimal getMarketPrice(TickBean tick, String entrustBs) {
        if (EntrustBs.isBuy(entrustBs)) {
            return TradeUtils.getBigDecimal(tick.getUpperLimitPrice(), 4);
        } else {
            return TradeUtils.getBigDecimal(tick.getLowerLimitPrice(), 4);
        }
    }

    /**
     * 计算总委托数量——试算数量
     */
    private long getTotalEntrustVolume(List<PortfolioInstructionContextDto> resultList) {
        return resultList.stream().mapToLong(PortfolioInstructionContextDto::getEntrustVolume).sum();
    }

    /**
     * 计算总剩余委托数量
     */
    private long getTotalLeftVolume(List<PortfolioInstructionContextDto> list) {
        return list.stream().mapToLong(PortfolioInstructionContextDto::getLeftVolume).sum();
    }

    private Long getEntrustVolume(Long totalLeftVolume, Long orderVolume, Long leftVolume, int unit) {
        if (totalLeftVolume == 0L) {
            return 0L;
        }
        return TradeUtils.formatLong(orderVolume.doubleValue() * leftVolume.doubleValue() / totalLeftVolume.doubleValue(), -unit, RoundingMode.UP);
    }

    private Long getEntrustVolume(BigDecimal disRate, BigDecimal volume, int unit) {
        return TradeUtils.formatLong(disRate.doubleValue() * volume.doubleValue(), -unit, RoundingMode.UP);
    }


    private List<Instruction> getFairInstructionList(List<PortfolioInstructionContextDto> fairList, PortfolioAllocationResponse result) {

        List<Instruction> list = new ArrayList<>();

        // 获取多组合
        for (PortfolioInstructionContextDto context : fairList) {
            Instruction ins = context.getInstruction();
            List<InstructionDetail> instructionDetails = instructionDetailService.queryInstructionDetailsByInstructionId(ins.getEntrustId());

            if (Objects.isNull(instructionDetails) || instructionDetails.size() == 0 || instructionDetails.size() == 1) {
                // 处理单组合
                handleFields(result, ins);
                list.add(ins);
                continue;
            }

            // 订单类型：数量单，指令单
            boolean isVolume = ins.getEntrustNum() > 0L;
            // 订单数量
            BigDecimal orderVolume = BigDecimal.valueOf(context.getEntrustVolume());
            // 委托数量
            BigDecimal entrustVolume = BigDecimal.ZERO;
            // 分配比例
            BigDecimal disRate = BigDecimal.ONE;
            // 最小步长
            BigDecimal minStep = this.getMinStep(ins);
            // 单指令多组合扩展列表
            List<Instruction> singleList = new ArrayList<>();
            for (InstructionDetail detail : instructionDetails) {
                // 生成多条指令，替换组合信息，将分配比例存入转股价格（conPrice字段）
                Instruction copyInstruction = new Instruction();
                BeanUtil.copyProperties(ins, copyInstruction);
                copyInstruction.setPortfolioId(detail.getPortfolioId());
                copyInstruction.setPortfolioName(detail.getPortfolioName());

                // - 计算组合委托额度
                disRate = isVolume ? (detail.getEntrustNum().divide(BigDecimal.valueOf(ins.getEntrustNum()), 8, RoundingMode.HALF_UP)) : (detail.getEntrustAmt().divide(ins.getEntrustAmt(), 8, RoundingMode.HALF_UP));
                copyInstruction.setConPrice(ins.getConPrice().multiply(disRate));

//                Long realVolume = 0L;
//                if (EntrustUtils.isBond(copyInstruction)) {
//                    realVolume = getEntrustVolume(copyInstruction.getConPrice(), copyInstruction.getConNum(), 1);
//                } else if (EntrustUtils.isPledgeRepurchase(copyInstruction)) {
//                    if (Market.SZSE.investValue.equals(copyInstruction.getTradeMarket())) {
//                        realVolume = getEntrustVolume(copyInstruction.getConPrice(), copyInstruction.getConNum(), 1);
//                    } else {
//                        realVolume = getEntrustVolume(copyInstruction.getConPrice(), copyInstruction.getConNum(), 3);
//                    }
//                } else if (EntrustUtils.isFutures(copyInstruction)) {
//                    realVolume = getEntrustVolume(copyInstruction.getConPrice(), copyInstruction.getConNum(), 0);
//                } else {
//                    realVolume = getEntrustVolume(copyInstruction.getConPrice(), copyInstruction.getConNum(), 2);
//                }
//                copyInstruction.setConNum(BigDecimal.valueOf(realVolume));

                // 数量
                if (TradeUtils.bigThanZero(orderVolume) && TradeUtils.bigThanZero(disRate)) {
                    // 根据比例计算委托数量
                    entrustVolume = isVolume ?
                            ((orderVolume.multiply(detail.getEntrustNum())).divide(BigDecimal.valueOf(ins.getEntrustNum()), 8, RoundingMode.HALF_UP)) :
                            ((orderVolume.multiply(detail.getEntrustAmt())).divide(ins.getEntrustAmt(), 8, RoundingMode.HALF_UP));
                    if (TradeUtils.lessThan(entrustVolume, minStep)) {
                        // 小于最小交易单位时的处理，置为0
                        entrustVolume = BigDecimal.ZERO;
                    } else {
                        // 大于等于最小交易单位时的处理，最小交易单位整数倍
                        entrustVolume = TradeUtils.subtractMod(entrustVolume, minStep);
                    }
                }
                copyInstruction.setConNum(entrustVolume);

                singleList.add(copyInstruction);
            }

            // 尾插处理
            Instruction maxInstruction = singleList.stream().max(Comparator.comparing(Instruction::getConPrice)).get();
            long totalExceptMaxVolume = singleList.stream().filter(item -> !item.getPortfolioId().equals(maxInstruction.getPortfolioId()))
                    .mapToLong(item -> item.getConNum().longValue()).sum();
            Long tailVolume = ins.getConNum().longValue() - totalExceptMaxVolume;
            BigDecimal tailAmt = NumberUtil.mul(tailVolume, result.getPrice());
            log.debug("尾插处理，组合名称 = [{}], 组合编号 = [{}], 原委托数量 = [{}], 现委托数量 = [{}]，现委托金额 = [{}]",
                    maxInstruction.getPortfolioName(),
                    maxInstruction.getPortfolioId(),
                    maxInstruction.getConNum(),
                    tailVolume,
                    tailAmt);
            maxInstruction.setConNum(BigDecimal.valueOf(tailVolume));
            // 重新计算占比；赋值委托数量，委托金额
            for (Instruction item : singleList) {
                item.setConPrice(ArithmeticUtil.divide(item.getConNum(), BigDecimal.valueOf(result.getTotalEntrustVolume()), 4));
                handleFields(result, item);
            }

            list.addAll(singleList);
        }
        return list;
    }

    /**
     * 配置参数
     */
    private void handleFields(PortfolioAllocationResponse result, Instruction ins) {
        // 借用termDays存放委托数量
        ins.setTermDays(ins.getConNum());
        // 借用minAmt存放委托金额
        // 特殊处理 质押式回购交易，将委托价格设置为100
        if (EntrustUtils.isPledgeRepurchase(ins)) {
            ins.setMinAmt(ArithmeticUtil.multiply(ins.getConNum(), BigDecimal.valueOf(100L)));
        } else {
            ins.setMinAmt(ArithmeticUtil.multiply(ins.getConNum(), result.getPrice()));
        }
        if (EntrustUtils.isVolume(ins)) {
            // 借用mIntAmt存放剩余总数量
            BigDecimal leftNum = BigDecimal.valueOf(ins.getEntrustNum() - ins.getEntrustLeftAmt().longValue() - ins.getFrozenQuantity().longValue());
            ins.setmIntAmt(leftNum.compareTo(BigDecimal.ZERO) > 0 ? leftNum : BigDecimal.ZERO);
        } else {
            // 借用mTradeAmt存放剩余总金额
            BigDecimal leftAmt = TradeUtils.subtract(ins.getEntrustAmt(), ins.getEntrustLeftAmt(), ins.getFrozenQuantity(), 2);
            ins.setmTradeAmt(leftAmt.compareTo(BigDecimal.ZERO) > 0 ? leftAmt : BigDecimal.ZERO);
        }
    }

    private BigDecimal getMinStep(Instruction instruction) {
        // 最小交易单元：整数倍
        BigDecimal minStep = BigDecimal.ONE;
        if (instruction.getMinPriceAndMinTrdUnitLimitBean() != null
                && ErrorCode.isSuccess(instruction.getMinPriceAndMinTrdUnitLimitBean().getErrorCode())
                && instruction.getMinPriceAndMinTrdUnitLimitBean().getMinTrdUnitLimitList() != null
                && !instruction.getMinPriceAndMinTrdUnitLimitBean().getMinTrdUnitLimitList().isEmpty()) {
            MinTrdUnitLimit minTrdUnitLimit = instruction.getMinPriceAndMinTrdUnitLimitBean().getMinTrdUnitLimitList().get(0);

            if (Objects.nonNull(minTrdUnitLimit.getMinStep())) {
                minStep = BigDecimal.valueOf(minTrdUnitLimit.getMinStep());
            }
        }
        return minStep;
    }

    private BigDecimal getMinNum(Instruction instruction, String entrustProp) {
        // 最低交易数量：大于等于
        BigDecimal minNum = BigDecimal.ONE;
        if (instruction.getMinPriceAndMinTrdUnitLimitBean() != null
                && ErrorCode.isSuccess(instruction.getMinPriceAndMinTrdUnitLimitBean().getErrorCode())
                && instruction.getMinPriceAndMinTrdUnitLimitBean().getMinTrdUnitLimitList() != null
                && !instruction.getMinPriceAndMinTrdUnitLimitBean().getMinTrdUnitLimitList().isEmpty()) {
            MinTrdUnitLimit minTrdUnitLimit = instruction.getMinPriceAndMinTrdUnitLimitBean().getMinTrdUnitLimitList().get(0);
            if (EntrustProp.isMarketPrice(entrustProp)) {
                if (Objects.nonNull(minTrdUnitLimit.getMaxNumMarket())) {
                    minNum = BigDecimal.valueOf(minTrdUnitLimit.getMinNumMarket());
                }
            } else {
                if (Objects.nonNull(minTrdUnitLimit.getMinNum())) {
                    minNum = BigDecimal.valueOf(minTrdUnitLimit.getMinNum());
                }
            }
        }
        return minNum;
    }

    private BigDecimal getMinAmt(Instruction instruction) {
        // 最低交易金额：大于等于
        BigDecimal minAmt = BigDecimal.ZERO;
        if (instruction.getMinPriceAndMinTrdUnitLimitBean() != null
                && ErrorCode.isSuccess(instruction.getMinPriceAndMinTrdUnitLimitBean().getErrorCode())
                && instruction.getMinPriceAndMinTrdUnitLimitBean().getMinTrdUnitLimitList() != null
                && !instruction.getMinPriceAndMinTrdUnitLimitBean().getMinTrdUnitLimitList().isEmpty()) {
            MinTrdUnitLimit minTrdUnitLimit = instruction.getMinPriceAndMinTrdUnitLimitBean().getMinTrdUnitLimitList().get(0);

            if (Objects.nonNull(minTrdUnitLimit.getMinAmt())) {
                minAmt = minTrdUnitLimit.getMinAmt();
            }
        }
        return minAmt;
    }

    /**
     * 获取组合计算信息
     *
     * @param instructionId 指令代码
     * @param entrustPrice  委托价格
     * @param fullPrice     债券全价
     * @param entrustNum    委托数量
     * @return 组合计算信息
     */
    public List<PortfolioCalculateResponse> getCalculateList(String instructionId, BigDecimal
            entrustPrice, BigDecimal fullPrice, Long entrustNum) {
        // 组装下单对象
        OrderPlaceBean orderPlaceBean = new OrderPlaceBean();
        orderPlaceBean.setInstructionId(instructionId);
        orderPlaceBean.setEntrustPrice(entrustPrice);
        orderPlaceBean.setFullPrice(fullPrice);
        orderPlaceBean.setEntrustVolume(entrustNum);

        // 调用组合计算接口，得出各组合分配到的数量金额
        ResultBean resultBean = this.queryPortfolioCalculate(orderPlaceBean, false);
        if (resultBean.getErrorCode() == 0) {
            return (List<PortfolioCalculateResponse>) resultBean.getData();
        }
        return ListUtil.empty();
    }
}

