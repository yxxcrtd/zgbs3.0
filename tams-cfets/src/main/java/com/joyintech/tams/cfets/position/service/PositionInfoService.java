package com.joyintech.tams.cfets.position.service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.cfets.position.dao.PositionInfoMapper;
import com.joyintech.tams.cfets.position.dto.PositionCashRequestDetailDto;
import com.joyintech.tams.cfets.position.dto.PositionCashSecDto;
import com.joyintech.tams.cfets.position.dto.PositionInfoRequestDto;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.position.PositionQueryDto;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PositionInfoService {

    public static final String FLOW_TYPE_ETR = "etr";
    public static final String FLOW_TYPE_TRD = "trd";

    /**
     * 委托共通
     */
    @Autowired
    private EtrCommonService etrCommonService;
    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    @Autowired
    private PositionInfoMapper positionInfoMapper;

    /**
     * 日期计算
     */
    @Autowired
    private DateCalService dateCalService;

    public List<BaseMap> queryTrdPositionHead(PositionInfoRequestDto requestDto) throws Exception {
        // 卖空模式
        String sellFlag = CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED");

        // 条件非空校验
        posiInfoCheckCondition(requestDto);

        if (requestDto.getPortfolioIdList() == null || requestDto.getPortfolioIdList().size() == 0) {
            return nullData(sellFlag);
        }

        // 现金 map.cashT、map.cashT1、map.realCash
        BaseMap cashMap = queryTrdAvailableCash(requestDto.getPortfolioIdList(), requestDto.getCdate(), requestDto.getAcctId(), null, null, null, null, null);
        // 查询头寸信息
        BaseMap positionMap = queryTrdPosition(requestDto.getPortfolioIdList(), requestDto.getCdate(), requestDto.getFinprodId());

        return assemble(sellFlag, positionMap, cashMap);
    }

    /**
     * 查询指令t0、t1、账户现金
     *
     * @param ptlList 组合列表
     * @param ccy
     * @param cdate
     * @param calId           默认使用03银行间，其它交易日历需指定
     * @param acctId          指定账户查询，非必填
     * @return map.cashT、map.cashT1、map.realCash
     */
    public BaseMap queryEtrAvailableCash(List<PositionCashRequestDetailDto> ptlList, String cdate, String acctId, String ccy, String calId, String finprodId) {
        BaseMap rs = new BaseMap();

        BigDecimal cashT = BigDecimal.ZERO; // 指令t日可用
        BigDecimal cashT1 = BigDecimal.ZERO; // 指令t1日可用
        BigDecimal realCash = BigDecimal.ZERO; // 账户现金
        BigDecimal secDesirableCash = BigDecimal.ZERO; // 证券户可取现金

        for (PositionCashRequestDetailDto list : ptlList){
            if (StringUtil.isEmpty(list.getPortfolioId())) {
                return rs;
            }
        }
        if (StringUtil.isEmpty(ccy)) {
            ccy = DictCodeList.币种_人民币_CNY.getValue();
        }
        if (StringUtil.isEmpty(calId)) {
            calId = "03"; // 默认取03银行间
        }
        if (StringUtil.isEmpty(cdate)) {
            cdate = iPlatformDate.getSysDateStr();
        }

//        for (String portfolioId : portfolioIdList) {
//            BaseMap paramMap = BaseMap.newMap()
//                    .put("portfolioId", portfolioId)
//                    .put("ccy", ccy)
//                    .put("vdate", cdate)
//                    .put("calId", calId);
//            Map<String, Object> mapRst = etrCommonService.getProAvailableCash(paramMap);
//
//            if (mapRst != null) { // 指令t日可用，指令t1日可用
//                cashT = ArithmeticUtil.doAdd(cashT, BaseMap.newMap(mapRst).getBigDecimal("cashT"));
//                cashT1 = ArithmeticUtil.doAdd(cashT1, BaseMap.newMap(mapRst).getBigDecimal("cashT1"));
//            }
//
//            PtlCashPositionFlowDto positionQueryDto = new PtlCashPositionFlowDto();
//            positionQueryDto.setPortfolioId(portfolioId);
//            positionQueryDto.setCcy(ccy);
//            positionQueryDto.setCdate(iPlatformDate.getSysDate());
//            // 实际现金（账户现金）
//            realCash = ArithmeticUtil.doAdd(realCash, basePtlCashPositionInfoService.queryUsableCashPosition(positionQueryDto));
//        }
//
//
//        BaseMap inMap = BaseMap.newMap()
//                .put("S_004", DictCodeListExt3.资产类型_汇总可用现金.getValue())
//                .put("C_01", DictCodeListExt3.资产类型_现金账户持仓.getValue())
//                .put("portfolioIdList", portfolioIdList)
//                .put("cdate", cdate)
//                .put("acctId", acctId);
//        Map map = positionInfoMapper.queryEtrAvailableCash(inMap);
//        BaseMap outMap = BaseMap.newMap(map);

        // 从原投管系统取

        if (CollectionUtils.isNotEmpty(ptlList)) {
            for (PositionCashRequestDetailDto list : ptlList){
                BaseMap paramMap = BaseMap.newMap()
                        .put("finprodId", finprodId)
                        .put("portfolioId", list.getPortfolioId())
                        .put("ccy", ccy)
                        .put("vdate", cdate)
                        .put("calId", calId) // 银行间
                        ;
                Map<String, Object> mapRst = etrCommonService.getProAvailableCash(paramMap);
                if (mapRst != null){ // 指令t日可用，指令t1日可用
                    cashT = ArithmeticUtil.doAdd(cashT, BaseMap.newMap(mapRst).getBigDecimal("cashT"));
                    cashT1 = ArithmeticUtil.doAdd(cashT1, BaseMap.newMap(mapRst).getBigDecimal("cashT1"));
                }
                PtlCashPositionFlowDto positionQueryDto = new PtlCashPositionFlowDto();
                positionQueryDto.setPortfolioId(list.getPortfolioId());
                positionQueryDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                positionQueryDto.setCdate(iPlatformDate.getSysDate());
                // 实际现金（账户现金）
                realCash = ArithmeticUtil.doAdd(realCash, basePtlCashPositionInfoService.queryUsableCashPosition(positionQueryDto));
            }
        }

//        BaseMap inMap = BaseMap.newMap()
//                .put("S_004", DictCodeListExt3.资产类型_汇总可用现金.getValue())
//                .put("C_01", DictCodeListExt3.资产类型_现金账户持仓.getValue())
//                .put("portfolioIdList", portfolioIdList)
//                .put("cdate", cdate)
//                .put("acctId", acctId);
//        Map map = positionInfoMapper.queryEtrAvailableCash(inMap);
//        BaseMap outMap = BaseMap.newMap(map);

        //调用新头寸引擎查询证券户可取现金
        if (CollectionUtils.isNotEmpty(ptlList)) {
            String flowType = "01";// 01 指令现金 02 指令持仓 03 交易现金 04 交易持仓
            //筛选出有资金账户的组合
            List<PositionCashSecDto> sumAcctNoList = new ArrayList<>();
            for (PositionCashRequestDetailDto list : ptlList) {
                Map<String, String> secIdMap = new HashMap<String, String>();
                List<String> secAcctIds = new ArrayList<>();
                if (StringUtil.isNotEmpty(list.getAcctNo())) {
                    secAcctIds.add(list.getAcctNo());
                }else {
                    secIdMap.put("portfolioId", list.getPortfolioId());
                    secAcctIds = this.qryPortfolioSecAcctIdAll(secIdMap);// 证券账户
                }

                String finalCcy = ccy;
                String finalCdate = cdate;
                secAcctIds.forEach(secAcctId -> {
                    PositionCashSecDto positionCashSecDto = new PositionCashSecDto();
                    positionCashSecDto.setCcy(finalCcy);
                    positionCashSecDto.setPortfolioId(list.getPortfolioId());
                    positionCashSecDto.setCdate(finalCdate);
                    positionCashSecDto.setAcctId(secAcctId);
                    sumAcctNoList.add(positionCashSecDto);
                });

            }

            //资金可用金额
            if (!CollectionUtils.isEmpty(sumAcctNoList) && sumAcctNoList.size() > 0) {
                BigDecimal newRealCashAmount = queryPosition(sumAcctNoList, flowType, DictCodeListExt3.资产类型_证券户可取资金.getValue());
                secDesirableCash = ArithmeticUtil.doAdd(secDesirableCash, newRealCashAmount);
            }

        }
        // rs.put("cashT", ArithmeticUtil.doAdd(cashT, outMap.getBigDecimal("T0KYXJ")));
        // rs.put("cashT1", ArithmeticUtil.doAdd(cashT1, outMap.getBigDecimal("T1KYXJ")));
        rs.put("cashT", cashT);
        rs.put("cashT1", cashT1);
        rs.put("realCash", realCash);
        rs.put("secDesirableCash", secDesirableCash);
        return rs;
    }

    /**
     * 查询证券账户
     *
     * @param map
     * @return
     */
    private String qryPortfolioSecAcctId(Map<String, String> map) {
        return positionInfoMapper.qryPortfolioSecAcctId(map);
    }

    /**
     * 查询交易所证券账户资金账户
     *
     * @param map
     * @return
     */
    public List<String> qryPortfolioSecAcctIdAll(Map<String, String> map) {
        return positionInfoMapper.qryPortfolioSecAcctIdAll(map);
    }

    /**
     * 从头寸引擎查询现金持仓余额
     *
     * @param ptList
     * @return
     */
    public BigDecimal queryPosition(List<PositionCashSecDto> ptList, String flowType, String positionType) {
        Map<String, Object> map = new HashMap<>();
        BigDecimal sumAmount = BigDecimal.ZERO;
        map.put("ptlList", buildPosition(ptList, flowType, positionType));
        String msg = PositionHttpRequest.queryPosition(map);
        BaseMap bm = JsonUtils.json2Object(msg, BaseMap.class);
        if (bm != null && StringUtil.equals("success", bm.getString("code"))) {
            List<LinkedHashMap> list = (List<LinkedHashMap>) bm.get("data");
            for (LinkedHashMap linkedHashMap : list) {
                if (linkedHashMap.get("AMOUNT") != null) {
                    BigDecimal bd = new BigDecimal(linkedHashMap.get("AMOUNT").toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    sumAmount = sumAmount.add(bd);
                    //债券持仓除以100
                    if (ptList != null && ptList.size() > 0
                            && StringUtil.isNotEmpty(ptList.get(0).getFinprodType2())
                            && StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), ptList.get(0).getFinprodType2())
                            && (StringUtil.equals(flowType, "02") || StringUtil.equals(flowType, "04"))) {
                        sumAmount = ArithmeticUtil.divide(sumAmount, 100, 0);
                    }
                }
            }
        }
        return sumAmount;
    }

    /**
     * 从头寸引擎查询现金持仓余额
     *
     * @param ptlList
     * @return
     */
    private List<PositionQueryDto> buildPosition(List<PositionCashSecDto> ptlList, String flowType, String positionType) {
        if (StringUtil.isNotEmpty(ptlList)) {
            List<PositionQueryDto> list = new ArrayList<>();
            for (PositionCashSecDto temp : ptlList) {
                PositionQueryDto dto = new PositionQueryDto();
                if (StringUtil.isEmpty(temp.getCcy())) {
                    dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                } else {
                    dto.setCcy(temp.getCcy());
                }
                dto.setPortfolioId(temp.getPortfolioId());
                dto.setFlowType(flowType);
                if (StringUtil.isNotEmpty(temp.getHoldingType())) {
                    dto.setPositionType(temp.getHoldingType());
                } else {
                    dto.setPositionType(positionType);
                }
                //现金 01 指令现金 02 指令持仓 03 交易现金 04 交易持仓
                if (StringUtil.equals(flowType, "01") || StringUtil.equals(flowType, "03")) {
                    dto.setCashAcctId(temp.getAcctId());
                }
                //持仓
                if ((StringUtil.equals(flowType, "02") || StringUtil.equals(flowType, "04"))) {
                    dto.setSecAcctId(temp.getSecAcctId());
                    dto.setFinprodId(temp.getFinprodId());
                }
                list.add(dto);
            }
            return list;
        }
        return null;
    }

    /**
     * 查询交易t0、t1、账户现金
     *
     * @@return map.cashT、map.cashT1、map.realCash
     */
    public BaseMap queryTrdAvailableCash(List<String> portfolioIdList, String cdate, String acctId, String ccy, String calId, List<String> acctTypeList, List<String> acctTypes, String finprodId) {
        BaseMap rs = new BaseMap();
        BigDecimal cashT = BigDecimal.ZERO; // 指令t日可用
        BigDecimal cashT1 = BigDecimal.ZERO; // 指令t1日可用
        BigDecimal secDesirableCash = BigDecimal.ZERO; // 证券户可取现金
        if (CollectionUtils.isEmpty(acctTypes)) {
            acctTypes = new ArrayList<>();
        }
        List<String> filtered = acctTypes.stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filtered)) {
            acctTypes.add("A01");
        }
        if (StringUtil.isEmpty(ccy)) {
            ccy = DictCodeList.币种_人民币_CNY.getValue();
        }
        if (StringUtil.isEmpty(calId)) {
            calId = "03"; // 默认取03银行间
        }
        if (StringUtil.isEmpty(cdate)) {
            cdate = iPlatformDate.getSysDateStr();
        }
        //账户类型
        if (portfolioIdList != null) {
            String flowType = "03";// 01 指令现金 02 指令持仓 03 交易现金 04 交易持仓
            //筛选出有资金账户的组合
            portfolioIdList = portfolioIdList.stream()
                    .distinct()
                    .collect(Collectors.toList());
            List<PositionCashSecDto> sumAcctNoList = new ArrayList<>();
            for (String portfolioId : portfolioIdList) {
                PtlCashPositionFlowDto positionQueryDto = new PtlCashPositionFlowDto();
                positionQueryDto.setPortfolioId(portfolioId);
                positionQueryDto.setCcy(ccy);
                positionQueryDto.setCdate(DateUtil.parse(cdate));
                positionQueryDto.setAcctType(acctTypeList);
                // 实际现金（账户现金）
                cashT = ArithmeticUtil.doAdd(cashT, basePtlCashPositionInfoService.queryUsableCashPosition(positionQueryDto));

                List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(cdate), DateUtil.addDay(DateUtil.parse(cdate), 15), calId);
                Date t1Day = dateCalService.calculationTradingDay(DateUtil.parse(cdate), 1, holidayList, "1");
                PtlCashPositionFlowDto positionQueryDto1 = new PtlCashPositionFlowDto();
                positionQueryDto1.setPortfolioId(portfolioId);
                positionQueryDto1.setCcy(ccy);
                positionQueryDto1.setCdate(t1Day);
                positionQueryDto.setAcctType(acctTypeList);
                cashT1 = ArithmeticUtil.doAdd(cashT1, basePtlCashPositionInfoService.queryUsableCashPosition(positionQueryDto1));
                Map<String, String> secIdMap = new HashMap<>();
                Map<String, String> map = new HashMap<>();
                map.put("portfolioId", portfolioId);
                List<String> secAcctIds = this.qryPortfolioSecAcctIdAll(map);// 证券账户
                String finalCcy = ccy;
                String finalCdate = cdate;
                secAcctIds.forEach(secAcctId -> {
                    PositionCashSecDto positionCashSecDto = new PositionCashSecDto();
                    positionCashSecDto.setCcy(finalCcy);
                    positionCashSecDto.setPortfolioId(portfolioId);
                    positionCashSecDto.setCdate(finalCdate);
                    positionCashSecDto.setAcctId(secAcctId);
                    sumAcctNoList.add(positionCashSecDto);
                });
            }
            //资金可用金额
            if (!CollectionUtils.isEmpty(sumAcctNoList) && sumAcctNoList.size() > 0) {
                BigDecimal newRealCashAmount = queryPosition(sumAcctNoList, flowType, DictCodeListExt3.资产类型_证券户可取资金.getValue());
                secDesirableCash = ArithmeticUtil.doAdd(secDesirableCash, newRealCashAmount);
            }
        }

        BaseMap inMap = BaseMap.newMap()
                .put("S_004", DictCodeListExt3.资产类型_汇总可用现金.getValue())
                .put("C_01", DictCodeListExt3.资产类型_现金账户持仓.getValue())
                .put("portfolioIdList", portfolioIdList)
                .put("cdate", cdate)
                .put("acctId", acctId)
                .put("acctTypes", acctTypes);
        Map map = positionInfoMapper.queryTrdAvailableCash(inMap);
        BaseMap outMap = BaseMap.newMap(map);

        rs.put("cashT", ArithmeticUtil.doAdd(cashT, outMap.getBigDecimal("T0KYXJ")));
        rs.put("cashT1", ArithmeticUtil.doAdd(cashT1, outMap.getBigDecimal("T1KYXJ")));
        rs.put("realCash", ArithmeticUtil.doAdd(cashT, outMap.getBigDecimal("ZHXJ")));
        rs.put("secDesirableCash", secDesirableCash);
        return rs;
    }

    /**
     * 查询指令持仓
     *
     * @param portfolioIdList
     * @param cdate
     * @param finprodId
     * @return map.RRQMKDGH 融入券卖空待购回持仓
     * map.ZCTRKYCC 资产T日可用持仓
     * map.ZCT1RKYCC 资产T+1日可用持仓
     * map.BFQTRKYCC 本方券T日可用持仓
     * map.BFQT1RKYCC 本方券T+1日可用持仓
     * map.RRQTRKYCC 融入券T日可用持仓
     * map.RRQT1RKYCC 融入券T+1日可用持仓
     */
    public BaseMap queryEtrPosition(List<String> portfolioIdList, String cdate, String finprodId) {
        BaseMap bm = BaseMap.newMap()
                .put("S_001", DictCodeListExt3.资产类型_指令汇总可用持仓.getValue())
                .put("S_003", DictCodeListExt3.资产类型_融入券卖空待购回持仓.getValue())
                .put("A_004", DictCodeListExt3.资产类型_债券本方券可用持仓.getValue())
                .put("A_005", DictCodeListExt3.资产类型_债券融入券可用持仓.getValue())
                .put("portfolioIdList", portfolioIdList)
                .put("cdate", cdate)
                .put("finprodId", finprodId);
        // 查询头寸信息
        Map map = positionInfoMapper.queryEtrPosition(bm);
        return BaseMap.newMap(map);
    }

    /**
     * 查询交易持仓
     *
     * @param portfolioIdList
     * @param cdate
     * @param finprodId
     * @return map.RRQMKDGH 融入券卖空待购回持仓
     * map.ZCTRKYCC 资产T日可用持仓
     * map.ZCT1RKYCC 资产T+1日可用持仓
     * map.BFQTRKYCC 本方券T日可用持仓
     * map.BFQT1RKYCC 本方券T+1日可用持仓
     * map.RRQTRKYCC 融入券T日可用持仓
     * map.RRQT1RKYCC 融入券T+1日可用持仓
     */
    public BaseMap queryTrdPosition(List<String> portfolioIdList, String cdate, String finprodId) {
        BaseMap bm = BaseMap.newMap()
                .put("S_007", DictCodeListExt3.资产类型_交易汇总可用持仓.getValue())
                .put("S_005", DictCodeListExt3.资产类型_指令汇总可用现金.getValue())
                .put("S_003", DictCodeListExt3.资产类型_融入券卖空待购回持仓.getValue())
                .put("A_004", DictCodeListExt3.资产类型_债券本方券可用持仓.getValue())
                .put("A_005", DictCodeListExt3.资产类型_债券融入券可用持仓.getValue())
                .put("portfolioIdList", portfolioIdList)
                .put("cdate", cdate)
                .put("finprodId", finprodId);
        // 查询头寸信息
        Map map = positionInfoMapper.queryTrdPosition(bm);
        return BaseMap.newMap(map);
    }

    private void posiInfoCheckCondition(PositionInfoRequestDto requestDto) {
        if (requestDto == null) {
            throw new RuntimeException("入参不空！");
        }

        if (StringUtil.isEmpty(requestDto.getCdate())) {
            requestDto.setCdate(iPlatformDate.getSysDateStr());
        }
    }

    private List<BaseMap> nullData(String flag) {
        List<BaseMap> resp = new ArrayList<>();

        /*T日可用现金*/
        resp.add(BaseMap.newMap()
                .put("field", "T0KYXJ")
                .put("name", "T日可用现金（元）")
                .put("amount", "--")
                .put("type", "xj"));
        /*T+1日可用现金*/
        resp.add(BaseMap.newMap()
                .put("field", "T1KYXJ")
                .put("name", "T+1日可用现金（元）")
                .put("amount", "--")
                .put("type", "xj"));
        /*账户现金*/
        resp.add(BaseMap.newMap()
                .put("field", "ZHXJ")
                .put("name", "账户现金（元）")
                .put("amount", "--")
                .put("type", "xj"));

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式一、模式二时显示此字段
            /*融入券卖空待购回持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "RRQMKDGHCC")
                    .put("name", "融入券卖空待购回持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "03")) {
            // 卖空模式设置为模式一、模式三时显示此字段
            /*资产T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "ZCTRKYCC")
                    .put("name", "资产T日可用持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "03")) {
            // 卖空模式设置为模式一、模式三时显示此字段
            /*资产T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "ZCT1RKYCC")
                    .put("name", "资产T+1日可用持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式二时显示此字段
            /*本方券T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "BFQTRKYCC")
                    .put("name", "本方券T日可用持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式二时显示此字段
            /*本方券T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "BFQT1RKYCC")
                    .put("name", "本方券T+1日可用持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式二时显示此字段
            /*融入券T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "RRQTRKYCC")
                    .put("name", "融入券T日可用持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式二时显示此字段
            /*融入券T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "RRQT1RKYCC")
                    .put("name", "融入券T+1日可用持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }
        return resp;
    }

    /**
     * 封装现金结果集
     *
     * @param cashMap
     * @return
     */
    public List<BaseMap> assembleCash(BaseMap cashMap) {
        // 初始化返回集合
        List<BaseMap> resp = new ArrayList<>();

        /*T日可用现金*/
        resp.add(BaseMap.newMap()
                .put("field", "T0KYXJ")
                .put("name", "T日可用现金（元）")
                .put("amount", formatStr(cashMap.getBigDecimal("cashT")))
                .put("type", "xj"));
        /*T+1日可用现金*/
        resp.add(BaseMap.newMap()
                .put("field", "T1KYXJ")
                .put("name", "T+1日可用现金（元）")
                .put("amount", formatStr(cashMap.getBigDecimal("cashT1")))
                .put("type", "xj"));
        /*账户现金*/
        resp.add(BaseMap.newMap()
                .put("field", "ZHXJ")
                .put("name", "托管户账户现金（元）")
                .put("amount", formatStr(cashMap.getBigDecimal("realCash")))
                .put("type", "xj"));
        /*证券户可取现金*/
        resp.add(BaseMap.newMap()
                .put("field", "ZQHKQXJ")
                .put("name", "券商户可取现金（元）")
                .put("amount", formatStr(cashMap.getBigDecimal("secDesirableCash")))
                .put("type", "xj"));
        return resp;
    }

    /**
     * 封装头寸结果集
     *
     * @param
     * @return
     */
    public List<BaseMap> assembleSec(String flag, BaseMap bm) {
        String T0KYXJ = "T0KYXJ", T1KYXJ = "T1KYXJ", ZHXJ = "ZHXJ", RRQMKDGHCC = "RRQMKDGHCC", ZCTRKYCC = "ZCTRKYCC";
        String ZCT1RKYCC = "ZCT1RKYCC", BFQTRKYCC = "BFQTRKYCC", BFQT1RKYCC = "BFQT1RKYCC", RRQTRKYCC = "RRQTRKYCC", RRQT1RKYCC = "RRQT1RKYCC";
        if (bm == null) {
            bm = BaseMap.newMap();
        }
        // 初始化返回集合
        List<BaseMap> resp = new ArrayList<>();

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式一、模式二时显示此字段
            /*融入券卖空待购回持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", RRQMKDGHCC)
                    .put("name", "融入券卖空待购回持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(RRQMKDGHCC)))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "03")) {
            // 卖空模式设置为模式一、模式三时显示此字段
            /*资产T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", ZCTRKYCC)
                    .put("name", "资产T日可用持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(ZCTRKYCC)))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "03")) {
            // 卖空模式设置为模式一、模式三时显示此字段
            /*资产T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", ZCT1RKYCC)
                    .put("name", "资产T+1日可用持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(ZCT1RKYCC)))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式二时显示此字段
            /*本方券T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", BFQTRKYCC)
                    .put("name", "本方券T日可用持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(BFQTRKYCC)))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式二时显示此字段
            /*本方券T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", BFQT1RKYCC)
                    .put("name", "本方券T+1日可用持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(BFQT1RKYCC)))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式二时显示此字段
            /*融入券T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", RRQTRKYCC)
                    .put("name", "融入券T日可用持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(RRQTRKYCC)))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式二时显示此字段
            /*融入券T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", RRQT1RKYCC)
                    .put("name", "融入券T+1日可用持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(RRQT1RKYCC)))
                    .put("type", "cc"));
        }
        return resp;
    }

    /**
     * 封装现金+头寸结果集
     *
     * @param flag
     * @param bm
     * @param cashMap
     * @return
     */
    public List<BaseMap> assemble(String flag, BaseMap bm, BaseMap cashMap) {
        String T0KYXJ = "T0KYXJ", T1KYXJ = "T1KYXJ", ZHXJ = "ZHXJ",ZQHKQXJ="ZQHKQXJ", RRQMKDGHCC = "RRQMKDGHCC", ZCTRKYCC = "ZCTRKYCC";
        String ZCT1RKYCC = "ZCT1RKYCC", BFQTRKYCC = "BFQTRKYCC", BFQT1RKYCC = "BFQT1RKYCC", RRQTRKYCC = "RRQTRKYCC", RRQT1RKYCC = "RRQT1RKYCC";
        if (bm == null) {
            bm = BaseMap.newMap();
        }
        // 初始化返回集合
        List<BaseMap> resp = new ArrayList<>();

        /*T日可用现金*/
        resp.add(BaseMap.newMap()
                .put("field", T0KYXJ)
                .put("name", "T日可用现金（元）")
                .put("amount", formatStr(cashMap.getBigDecimal("cashT")))
                .put("type", "xj"));
        /*T+1日可用现金*/
        resp.add(BaseMap.newMap()
                .put("field", T1KYXJ)
                .put("name", "T+1日可用现金（元）")
                .put("amount", formatStr(cashMap.getBigDecimal("cashT1")))
                .put("type", "xj"));
        /*账户现金*/
        resp.add(BaseMap.newMap()
                .put("field", ZHXJ)
                .put("name", "托管户账户现金（元）")
                .put("amount", formatStr(cashMap.getBigDecimal("realCash")))
                .put("type", "xj"));
        /*证券户可取现金*/
        resp.add(BaseMap.newMap()
                .put("field", ZQHKQXJ)
                .put("name", "券商户可取现金（元）")
                .put("amount", formatStr(cashMap.getBigDecimal("secDesirableCash")))
                .put("type", "xj"));

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式一、模式二时显示此字段
            /*融入券卖空待购回持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", RRQMKDGHCC)
                    .put("name", "卖空待回购持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(RRQMKDGHCC)))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "03")) {
            // 卖空模式设置为模式一、模式三时显示此字段
            /*资产T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", ZCTRKYCC)
                    .put("name", "资产T日可用持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(ZCTRKYCC)))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "03")) {
            // 卖空模式设置为模式一、模式三时显示此字段
            /*资产T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", ZCT1RKYCC)
                    .put("name", "资产T+1日可用持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(ZCT1RKYCC)))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式二时显示此字段
            /*本方券T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", BFQTRKYCC)
                    .put("name", "本方券T日可用持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(BFQTRKYCC)))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式二时显示此字段
            /*本方券T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", BFQT1RKYCC)
                    .put("name", "本方券T+1日可用持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(BFQT1RKYCC)))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式二时显示此字段
            /*融入券T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", RRQTRKYCC)
                    .put("name", "融入券T日可用持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(RRQTRKYCC)))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")) {
            // 卖空模式设置为模式二时显示此字段
            /*融入券T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", RRQT1RKYCC)
                    .put("name", "融入券T+1日可用持仓（元）")
                    .put("amount", formatStr(bm.getBigDecimal(RRQT1RKYCC)))
                    .put("type", "cc"));
        }
        return resp;
    }

    private String formatStr(BigDecimal a) {
        if (a == null) {
            return "--";
        }
        return decimalToStr(a, "0.00");
    }

    /**
     * 返回前端格式保留小数位
     *
     * @param b
     * @param pattern
     * @return
     */
    private String decimalToStr(BigDecimal b, String pattern) {
        if (b == null) {
            return "--";
        }
        DecimalFormat df1 = new DecimalFormat(pattern);
        return df1.format(b);
    }
}
