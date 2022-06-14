package com.joyintech.tams.invest.common.ptlrealtimevaluation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;


import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokTradeOrder;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.dto.mock.asset.MockAssetInfoDto;
import com.joyintech.tams.invest.common.dto.mock.assetvaluation.MockCalcAssetValuationResult;
import com.joyintech.tams.invest.common.dto.mock.assetvaluation.MockCashChangingKey;
import com.joyintech.tams.invest.common.dto.mock.assetvaluation.MockSingleAssetChangingKey;
import com.joyintech.tams.invest.common.dto.mock.basicdata.MockMstExchargeQuoteDto;
import com.joyintech.tams.invest.common.dto.mock.portfolio.LiabilitiesChanging;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockPtlFeePositionDto;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockPtlPortfolioDto;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockPtlPortfolioValuationDto;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockPtlSecValuationDto;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockSingleAssetChanging;
import com.joyintech.tams.invest.common.dto.mock.trade.MockTrdProductDealDto;
import com.joyintech.tams.invest.common.ptlrealtimevaluation.dao.RealTimeValCommonMapper;
import com.joyintech.tams.invest.common.ptlrealtimevaluation.multithread.RealTimeValuationMultiThreadService;
import com.joyintech.tams.invest.common.ptlrealtimevaluation.multithread.RealTimeValuationTask;
import com.joyintech.tools.collectors.JoyinCollectors;

/**
 * @program: tams-disclosure-busicommon
 * @description: 组合资产估值-主方法
 * @author: 强成西
 * @create: 2020-04-16 21:00
 **/
@Component
public class RealTimeValMainService {

    /**
     * @param 资产基本信息列表 资产基本信息列表 数据结构：List<资产基本信息DTO> 参考【数据结构定义】定义
     * @param 组合基本信息DTO 组合基本信息DTO 数据结构：参考【数据结构定义】定义
     * @param 组合资产估值列表 组合资产估值列表 数据结构：List<组合资产估值扩展Entity>
     * @param 组合估值列表 组合估值列表 数据结构：List<组合估值DTO> 参考【数据结构定义】定义
     * @param 组合费用头寸扩展列表 组合费用头寸扩展列表 数据结构：List<组合费用头寸Entity>
     * @param 资产端交易列表 资产端交易列表 数据结构：List<金融产品交易扩展>
     * @param 负债端交易列表 负债端交易列表 数据结构：List<金融产品交易扩展>
     * @param 费用交易列表 费用交易列表 数据结构：List<金融产品交易扩展>
     * @param 资产端T_N交易列表 资产端T+N交易列表 数据结构：List<金融产品交易扩展> 说明：查询对象交易日<平台日 且 交割日>= 平台日
     * @param 负债端T_N交易列表 负债端T+N交易列表 数据结构：List<金融产品交易扩展> 说明：查询对象交易日<平台日 且 交割日>= 平台日
     * @param 组合现金Map 组合现金Map 数据结构：参考【数据结构定义】定义 说明：不含本次传入金融产品交易 对现金的影响
     * @param 测算开始日 测算开始日
     * @param 测算结束日 测算结束日
     * @param 基准日期 基准日期
     * @param 汇率行情列表 汇率行情列表
     * @param 融资成本 融资成本
     * @param 再投收益率 再投收益率
     * @param 综合费率 综合费率
     * @param t_N资产变化 T+N资产变化
     * @param t_N现金变化 T+N现金变化
     * @param t_N负债变化 T+N负债变化
     */
    public static MockCalcAssetValuationResult execute(List<MockAssetInfoDto> 资产基本信息列表,
                                                       MockPtlPortfolioDto 组合基本信息DTO,
                                                       List<MockPtlSecValuationDto> 组合资产估值列表,
                                                       List<MockPtlPortfolioValuationDto> 组合估值列表,
                                                       List<MockPtlFeePositionDto> 组合费用头寸扩展列表,
                                                       List<MockTrdProductDealDto> 资产端交易列表,
                                                       List<MockTrdProductDealDto> 负债端交易列表,
                                                       List<MockTrdProductDealDto> 费用交易列表,
                                                       List<MockTrdProductDealDto> 资产端T_N交易列表,
                                                       List<MockTrdProductDealDto> 负债端T_N交易列表,
                                                       Map<String, BigDecimal> 组合现金Map,
                                                       String 测算开始日, String 测算结束日, String 基准日期,
                                                       List<MockMstExchargeQuoteDto> 汇率行情列表,
                                                       BigDecimal 融资成本, BigDecimal 再投收益率,
                                                       BigDecimal 综合费率,
                                                       Map<MockSingleAssetChangingKey, Map<String, MockSingleAssetChanging>> t_N资产变化,
                                                       Map<MockCashChangingKey, BigDecimal> t_N现金变化,
                                                       Map<String, LiabilitiesChanging> t_N负债变化) {
        /**
         * 划款类交易方向
         */
        final List<String> 划款类交易方向 = Arrays.asList(DictCodeList.交易方向_银证转账.getValue(),
            DictCodeList.交易方向_证银转账.getValue(), DictCodeList.交易方向_托管户_上清DVP.getValue(),
            DictCodeList.交易方向_托管户_中债DVP.getValue(), DictCodeList.交易方向_上清DVP_托管户.getValue(),
            DictCodeList.交易方向_中债DVP_托管户.getValue(), DictCodeList.交易方向_上清DVP_中债DVP.getValue(),
            DictCodeList.交易方向_中债DVP_上清DVP.getValue(), DictCodeListExt1.交易方向_保证金划款.getValue(),
            DictCodeListExt1.交易方向_保证金回款.getValue());

        // 交易拆分 资产端交易列表
        // 将以下交易方向的交易从资产端交易列表中移出，并放入到变量.划款类交易列表中
        List<MockTrdProductDealDto> 划款类交易列表 = new ArrayList<MockTrdProductDealDto>();
        Iterator<MockTrdProductDealDto> it资产端交易列表 = 资产端交易列表.iterator();
        while (it资产端交易列表.hasNext()) {
            MockTrdProductDealDto mockTrdProductDealDto = it资产端交易列表.next();
            if (划款类交易方向.contains(mockTrdProductDealDto.getPs())) {
                划款类交易列表.add(mockTrdProductDealDto);
                it资产端交易列表.remove();
            }
        }

        // 交易拆分 资产端T+N交易列表
        // 将以下交易方向的交易从资产端交易T+N列表中移出，并放入到变量.划款类T+N交易列表中
        List<MockTrdProductDealDto> 划款类T_N交易列表 = new ArrayList<MockTrdProductDealDto>();
        Iterator<MockTrdProductDealDto> it资产端T_N交易列表 = 资产端T_N交易列表.iterator();
        while (it资产端T_N交易列表.hasNext()) {
            MockTrdProductDealDto mockTrdProductDealDto = it资产端T_N交易列表.next();
            if (划款类交易方向.contains(mockTrdProductDealDto.getPs())) {
                划款类T_N交易列表.add(mockTrdProductDealDto);
                it资产端T_N交易列表.remove();
            }
        }

        // 将List<组合资产估值扩展Entity> 转换成变量.组合资产估值Map
        Map<String, Map<String, MockPtlSecValuationDto>> 组合资产估值Map = new HashMap<String, Map<String, MockPtlSecValuationDto>>();
        组合资产估值列表.forEach(entity -> {
            Map<String, MockPtlSecValuationDto> 组合资产估值Map_投资目的 = 组合资产估值Map.get(entity.getFinprodId());
            if (组合资产估值Map_投资目的 == null) {
                组合资产估值Map_投资目的 = new HashMap<String, MockPtlSecValuationDto>();
            }
            组合资产估值Map_投资目的.put(entity.getInvAim() == null ? "999999" : entity.getInvAim(),
                    entity);
            组合资产估值Map.put(entity.getFinprodId(), 组合资产估值Map_投资目的);
        });

        Map<String,Object> 资产map = new HashMap<>();
        if(资产基本信息列表!=null){
            资产map = 资产基本信息列表.stream().collect(Collectors.toMap(MockAssetInfoDto::getFinprodId, MockAssetInfoDto->MockAssetInfoDto));
        }

        Map<String, Object> final资产map = 资产map;
        // 组合资产估值数据 取值活期利息
        String 组合币种 = StringUtil.isEmpty(组合基本信息DTO.getCcy())?DictCodeList.币种_人民币_CNY.getValue():组合基本信息DTO.getCcy();
        组合资产估值列表.forEach(entity -> {
            if(!final资产map.containsKey(entity.getFinprodId())){
                // 因为组合的组合现金在进入方法前就已经进行币种转换了 所以此次也需要转成组合币种  不然取不到该值
                Map<String, MockPtlSecValuationDto> 组合资产估值Map_投资目的 = 组合资产估值Map.get(组合币种+ RealTimeValCommonUtil.现金类型_再投);
                MockPtlSecValuationDto 组合资产估值 = new MockPtlSecValuationDto();
                if (组合资产估值Map_投资目的 == null||组合资产估值Map_投资目的.get(RealTimeValCommonUtil.投资目的)==null) {
                    组合资产估值Map_投资目的 = new HashMap<String, MockPtlSecValuationDto>();
                }else{
                    组合资产估值 = 组合资产估值Map_投资目的.get(RealTimeValCommonUtil.投资目的);
                }
                BigDecimal  tdyIntincexp = entity.getTdyIntincexp();
                if(!StringUtil.equals(entity.getCcy(),组合币种)){
                    BigDecimal 汇率 = RealTimeValCommonUtil.汇率获取(entity.getCcy(), 组合币种,
                            汇率行情列表);
                    tdyIntincexp = ArithmeticUtil.multiply(tdyIntincexp, 汇率);
                }
                组合资产估值.setAccrualInt(ArithmeticUtil.doAdd(组合资产估值.getAccrualInt(),tdyIntincexp));
                组合资产估值.setTdyIntincexp(ArithmeticUtil.doAdd(组合资产估值.getTdyIntincexp(),tdyIntincexp));
                组合资产估值Map_投资目的.put(RealTimeValCommonUtil.投资目的,
                        组合资产估值);
                组合资产估值Map.put(组合币种+ RealTimeValCommonUtil.现金类型_再投, 组合资产估值Map_投资目的);
            }
        });

        // 将资产端交易列表转换成变量.资产端交易Map
        Map<String, Map<String, List<MockTrdProductDealDto>>> 资产端交易Map = new HashMap<String, Map<String, List<MockTrdProductDealDto>>>();
        tansferList2Map(资产端交易列表, 资产端交易Map);

        // 将资产端T+N交易列表转换成变量.资产端T+N交易Map
        Map<String, Map<String, List<MockTrdProductDealDto>>> 资产端T_N交易Map = new HashMap<String, Map<String, List<MockTrdProductDealDto>>>();
        tansferList2Map(资产端T_N交易列表, 资产端T_N交易Map);

        // 3.全局变量定义 （定义在RealTimeValCommonUtil）

        ConcurrentHashMap<MockSingleAssetChangingKey, Map<String, MockSingleAssetChanging>> t_N资产变化_内部 = new ConcurrentHashMap<MockSingleAssetChangingKey, Map<String, MockSingleAssetChanging>>();
        if (t_N资产变化 != null) {
            t_N资产变化_内部.putAll(t_N资产变化);
        }

        ConcurrentHashMap<MockCashChangingKey, BigDecimal> t_N现金变化_内部 = new ConcurrentHashMap<MockCashChangingKey, BigDecimal>();
        if (t_N现金变化 != null) {
            t_N现金变化_内部.putAll(t_N现金变化);
        }

        // 交易排序规则
        RealTimeValCommonMapper realTimeValCommonMapper = SpringContextUtil.getBean(
            RealTimeValCommonMapper.class);
        List<BokTradeOrder> 交易排序规则 = realTimeValCommonMapper.selectValTradeOrder(
            DictCodeListExt2.会计准则_企业准则.getValue());

        MockCalcAssetValuationResult result = new MockCalcAssetValuationResult();
        if(资产基本信息列表!=null) {
            result = 资产估值及产品交易处理(资产基本信息列表, 组合基本信息DTO, 组合资产估值Map, 资产端交易Map,
                    负债端交易列表, 费用交易列表, 负债端T_N交易列表, t_N资产变化_内部, t_N现金变化_内部, t_N负债变化, 资产端T_N交易Map, 测算开始日,
                    测算结束日, 基准日期, 汇率行情列表, 组合费用头寸扩展列表, 组合估值列表, 划款类交易列表, 划款类T_N交易列表, 交易排序规则);
        }

        // 现金及费用计提处理
        现金及费用计提处理(组合基本信息DTO, 组合资产估值Map, result, 组合现金Map, 汇率行情列表, 融资成本, 再投收益率, 综合费率, 测算开始日, 测算结束日,
            基准日期);

        return result;
    }

    /**
     * 现金及费用计提处理 <功能详细描述>
     * 
     * @param 组合基本信息dto
     * @param 组合资产估值Map
     * @param result 变量.处理后组合资产估值扩展列表 变量.处理后组合费用头寸列表 变量.处理后组合估值DTO列表 入参.T+N现金变化
     * @param 组合现金Map
     * @param 汇率行情列表
     * @param 融资成本
     * @param 再投收益率
     * @param 综合费率
     * @param 测算开始日
     * @param 测算结束日
     * @param 基准日期
     * @return
     * @see
     * @since 1.0
     */
    private static MockCalcAssetValuationResult 现金及费用计提处理(MockPtlPortfolioDto 组合基本信息dto,
                                                          Map<String, Map<String, MockPtlSecValuationDto>> 组合资产估值Map,
                                                          MockCalcAssetValuationResult result,
                                                          Map<String, BigDecimal> 组合现金Map,
                                                          List<MockMstExchargeQuoteDto> 汇率行情列表,
                                                          BigDecimal 融资成本, BigDecimal 再投收益率,
                                                          BigDecimal 综合费率, String 测算开始日,
                                                          String 测算结束日, String 基准日期) {
        List<MockPtlSecValuationDto> 最终组合资产估值扩展列表 = result.getMockPtlSecValuationDtoList();
        List<MockPtlFeePositionDto> 最终组合费用头寸扩展列表 = result.getMockPtlFeePositionDtoList();
//        List<MockPtlPortfolioValuationDto> 最终组合估值扩展列表 = result.getMockPtlPortfolioValuationDtoList();
//        List<MockPtlPortfolioValuationDto> 最终组合估值扩展列表 = new ArrayList<>();
        MockCalcAssetValuationResult 现金估值result = CashValuationService.excute(
            result.getCashChanging(), 组合资产估值Map, 组合现金Map, 融资成本, 再投收益率, 组合基本信息dto, 汇率行情列表, 测算开始日,
            测算结束日, 组合基本信息dto.getCcy());
        最终组合资产估值扩展列表.addAll(现金估值result.getMockPtlSecValuationDtoList());
        result.setMockPtlCashPositionMap(现金估值result.getMockPtlCashPositionMap());
        // 费前资产汇总
        Map<String, MockPtlPortfolioValuationDto> 资产负债合计Map = new HashMap<String, MockPtlPortfolioValuationDto>();
        Map<String, BigDecimal> 待清算款合计Map = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> 现金成本合计Map = new HashMap<String, BigDecimal>();//产品剩余现金不够资产投资，现金作为负债端融资，计算万分收益时这部分资产要扣除
        //回购交易费用合计
        BigDecimal feeamt=BigDecimal.ZERO;
        for (MockPtlSecValuationDto mockPtlSecValuationDto : result.getMockPtlSecValuationDtoList()) {
            String cdate = DateUtil.format(mockPtlSecValuationDto.getCdate());
            if (StringUtil.equals(RealTimeValCommonUtil.资产负债标识_资产,
                mockPtlSecValuationDto.getAssetLiabilitiesMark())) {
                if(cdate.compareTo("2019-09-10")==0){
                    BigDecimal aa=mockPtlSecValuationDto.getAssetValue();
                }
                //获取待清算款的值
                if(mockPtlSecValuationDto.getDelayPayAmt()!=null && mockPtlSecValuationDto.getDelayPayAmt().compareTo(BigDecimal.ZERO)<0){
                    BigDecimal tempDelayAmt=mockPtlSecValuationDto.getDelayPayAmt().negate();
                    if (待清算款合计Map.get(cdate) == null) {
                        待清算款合计Map.put(cdate, tempDelayAmt);
                    }else{
                        BigDecimal amt=待清算款合计Map.get(cdate);
                        amt=ArithmeticUtil.doAdd(amt,tempDelayAmt);
                        待清算款合计Map.put(cdate,amt);
                    }
                }
                if (资产负债合计Map.get(cdate) == null) {
                    MockPtlPortfolioValuationDto mockPtlPortfolioValuationDto2 = new MockPtlPortfolioValuationDto();
                    mockPtlPortfolioValuationDto2.setAssetSum(
                            mockPtlSecValuationDto.getAssetValue());
                   /* if(StringUtil.equals(DictCodeList.平安资产类型_买断式回购.getValue(),mockPtlSecValuationDto.getTgAssetType())
                            ||StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(),mockPtlSecValuationDto.getTgAssetType())
                    ){
                        mockPtlPortfolioValuationDto2.setAssetSumNoFeeamt( mockPtlSecValuationDto.getAssetValueNoFeeamt());
                    }else{
                        mockPtlPortfolioValuationDto2.setAssetSumNoFeeamt( mockPtlSecValuationDto.getAssetValue());
                    }*/
                    feeamt=ArithmeticUtil.doAdd(mockPtlSecValuationDto.getRepFeeamt());


                    资产负债合计Map.put(cdate, mockPtlPortfolioValuationDto2);
                }
                else {
                    资产负债合计Map.get(cdate).setAssetSum(
                        ArithmeticUtil.doAdd(资产负债合计Map.get(cdate).getAssetSum(),
                            mockPtlSecValuationDto.getAssetValue()));

                    if(StringUtil.equals(DictCodeList.平安资产类型_买断式回购.getValue(),mockPtlSecValuationDto.getTgAssetType())
                            /*||StringUtil.equals(DictCodeList.平安资产类型_买断式回购.getValue(),mockPtlSecValuationDto.getTgAssetType())*/
                    ){
                        资产负债合计Map.get(cdate).setAssetSumNoFeeamt(
                                ArithmeticUtil.doAdd(资产负债合计Map.get(cdate).getAssetSumNoFeeamt(),
                                        mockPtlSecValuationDto.getAssetValueNoFeeamt()));
                    }else{
                        资产负债合计Map.get(cdate).setAssetSumNoFeeamt(
                                ArithmeticUtil.doAdd(资产负债合计Map.get(cdate).getAssetSumNoFeeamt(),
                                        mockPtlSecValuationDto.getAssetValue()));
                    }

                }
            }
            if (StringUtil.equals(RealTimeValCommonUtil.资产负债标识_负债,
                mockPtlSecValuationDto.getAssetLiabilitiesMark())) {
                //获取待清算款的值
                if(mockPtlSecValuationDto.getFinprodId()!=null && mockPtlSecValuationDto.getFinprodId().equals("CNY01")
                        ){
                    //融资成本的fixed WZZGBS-607,统计融资成本统计到负债端的
                    BigDecimal tempAssetValue=mockPtlSecValuationDto.getAssetValue();
                    if (现金成本合计Map.get(cdate) == null) {
                        现金成本合计Map.put(cdate, tempAssetValue);
                    }else{
                        BigDecimal amt=现金成本合计Map.get(cdate);
                        amt=ArithmeticUtil.doAdd(amt,tempAssetValue);
                        现金成本合计Map.put(cdate,amt);
                    }
                }
                if (资产负债合计Map.get(cdate) == null) {
                    MockPtlPortfolioValuationDto mockPtlPortfolioValuationDto2 = new MockPtlPortfolioValuationDto();
                    mockPtlPortfolioValuationDto2.setLiabilitiesSum(
                           mockPtlSecValuationDto.getAssetValue());
                    资产负债合计Map.put(cdate, mockPtlPortfolioValuationDto2);
                }
                else {
                    资产负债合计Map.get(cdate).setLiabilitiesSum(
                        ArithmeticUtil.doAdd(资产负债合计Map.get(cdate).getLiabilitiesSum(),
                            mockPtlSecValuationDto.getAssetValue()));
                }
            }
        }


        Map<String, BigDecimal> 费用合计map = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> 费用map = new HashMap<String, BigDecimal>();
        for (MockPtlFeePositionDto mockPtlFeePositionDto : result.getMockPtlFeePositionDtoList()) {
            String cdate = DateUtil.format(mockPtlFeePositionDto.getCdate());
            if (费用合计map.get(cdate) == null) {
                费用合计map.put(cdate, mockPtlFeePositionDto.getAssetValue());
            }
            else {
                费用合计map.put(cdate,
                        ArithmeticUtil.doAdd(费用合计map.get(cdate),mockPtlFeePositionDto.getAssetValue()));
            }

            if (StringUtil.equals(RealTimeValCommonUtil.费用类型_综合费率,
                    mockPtlFeePositionDto.getPtlFeeType())) {
                if (费用map.get(cdate) == null) {
                    费用map.put(cdate, mockPtlFeePositionDto.getAssetValue());
                }
                else {
                    费用map.put(cdate,
                            ArithmeticUtil.doAdd(费用map.get(cdate),mockPtlFeePositionDto.getAssetValue()));
                }
            }

        }

//        BigDecimal 费用合计 = BigDecimal.ZERO;
//        BigDecimal 费用 = BigDecimal.ZERO;
//        for (MockPtlFeePositionDto mockPtlFeePositionDto : result.getMockPtlFeePositionDtoList()) {
//            费用合计 = ArithmeticUtil.doAdd(费用合计, mockPtlFeePositionDto.getAssetValue());
//            if (StringUtil.equals(RealTimeValCommonUtil.费用类型_综合费率,
//                mockPtlFeePositionDto.getPtlFeeType())) {
//                费用 = mockPtlFeePositionDto.getTdyIntervalPayFee();
//            }
//        }

        Map<String, MockPtlPortfolioValuationDto> 组合估值Map = result.getMockPtlPortfolioValuationDtoList().stream().collect(
            JoyinCollectors.toMap(x -> DateUtil.format(x.getCdate()), x -> x));

        MockPtlPortfolioValuationDto 昨日组合估值 = new MockPtlPortfolioValuationDto();
        if (组合估值Map.containsKey(DateUtil.addDay(测算开始日, -1))) {
            BeanUtil.copyPropertiesIgnoreNull(昨日组合估值, 组合估值Map.get(DateUtil.addDay(测算开始日, -1)));
        }
        else {
            昨日组合估值.setPortfolioId(组合基本信息dto.getPortfolioId());
            昨日组合估值.setCcy(组合基本信息dto.getCcy());
            昨日组合估值.setCdate(DateUtil.parse(DateUtil.addDay(测算开始日, -1)));
            昨日组合估值.setTdyShareAmt(BigDecimal.ZERO);
            昨日组合估值.setDelayPayAmt(BigDecimal.ZERO);
        }
            
        String 测算日期 = 测算开始日;
        BigDecimal 费用 = BigDecimal.ZERO;
        // 应付客户收益合计 取值昨日的未分配利润
        BigDecimal 应付客户收益合计 = 昨日组合估值.getTdyUnpaidProfit();
//                ArithmeticUtil.doSub(昨日组合估值.getTdyAssetValue(), 昨日组合估值.getTdyShareAmt());
//
        while (DateUtil.compareDate(测算日期, 测算结束日) <= 0) {
            MockPtlPortfolioValuationDto 当日组合估值 = 组合估值Map.get(测算日期);
//            if (当日组合估值 == null) {
//                当日组合估值 = new MockPtlPortfolioValuationDto();
//                if (昨日组合估值 != null) {
//                    BeanUtil.copyPropertiesIgnoreNull(当日组合估值, 昨日组合估值);
//                    当日组合估值.setCdate(DateUtil.parse(测算日期));
//                }
//                else {
//                    当日组合估值.setPortfolioId(组合基本信息dto.getPortfolioId());
//                    当日组合估值.setCcy(组合基本信息dto.getCcy());
//                    当日组合估值.setCdate(DateUtil.parse(测算日期));
//                    当日组合估值.setTdyShareAmt(BigDecimal.ZERO);
//                    当日组合估值.setDelayPayAmt(BigDecimal.ZERO);
//                }
//            }
            
            BigDecimal 当日发生费用 = BigDecimal.ZERO;
            BigDecimal 计费本金 = BigDecimal.ZERO;
            if (StringUtil.equals(组合基本信息dto.getProfitType(), DictCodeList.收益类型_货币型.getValue())) {
                计费本金 = 当日组合估值.getTdyShareAmt();
                当日发生费用 = ArithmeticUtil.divide(ArithmeticUtil.multiply(计费本金, 综合费率),
                    new BigDecimal(365), RealTimeValCommonUtil.AMOUNT_SCALE);
            }
            else if (StringUtil.equals(组合基本信息dto.getProfitType(),
                DictCodeList.收益类型_净值型.getValue())) {
/*                //浮动管理费支持区间倒减计提
                BigDecimal 昨日区间浮动管理费余额 = BigDecimal.ZERO;
                //IF [账套参数.区间内浮动管理费计提方式 = 区间内每日倒减计提] AND [本次计提日 > 上次计提日 +1]
                if(prdIntAccTypeObj != null && DictCodeListExt3.区间内浮动管理费计提方式_区间内每日倒减计提.getValue().equals(prdIntAccTypeObj.toString())
                        && DateUtil.compareDate(测算日期, DateUtil.addDay(lastAccrualDate,1)) > 0) {
                    *//**
                     调用 SHEET 查询账务明细当前表发生额 方法查询浮动管理费区间余额
                     参数名
                     开始日期					上次计提日+1
                     结束日期					本次计提日-1
                     账套代码					DTO.账套代码
                     场景代码					null
                     基础科目性质					应付超额管理费
                     入账日					DTO.入账日
                     借贷方向					贷方
                     优先级					本次批处理优先级
                     返回结果
                     List<账务明细当前表> **//*
                    Map<String, Object> param = new HashMap<>();
                    param.put("booksetId", booksetId);//DTO.账套代码
                    param.put("startHappenDate", DateUtil.addDay(lastAccrualDate,1));//上次计提日+1
                    param.put("endHappenDate", DateUtil.addDay(accrualDate,-1));//本次计提日-1
                    param.put("bookDate", 测算日期);//入账日
                    param.put("cdFlag", DictCodeList.余额方向_贷方.getValue());//贷方
                    param.put("", Arrays.asList(DictCodeBaseSubjectNatrue.应付超额管理费.getValue()));//应付超额管理费
                    List<TaFloadFeeAccountsDto> taFloadFeeAccountsList = taFloadFeeAccountsMapper.selectBokBookdetailCurrent(param);
                    //LOOP 查询结果List<账务明细当前表>，调用账务类共通.余额方向处理_余额加发生额
                    if(!StringUtil.isEmptyList(taFloadFeeAccountsList)){
                        //变量.实际余额方向 = 贷方
                        String actCdFlag = DictCodeList.余额方向_贷方.getValue();
                        //变量.实际余额 = 0
                        BigDecimal actBalance = BigDecimal.ZERO;
                        for(TaFloadFeeAccountsDto item : taFloadFeeAccountsList) {
                            //调用账务类共通.余额方向处理_余额加发生额
                            Map<String, Object> balanceDealMap = accountCommonService.balanceDirectDealAccrualAddBalance(DictCodeList.余额方向_贷方.getValue(),
                                    actCdFlag, actBalance, BigDecimal.ZERO, item.getCdFlag(), item.getoHappenAmt(),
                                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                            //变量.实际余额方向 = 返回结果.余额方向
                            actCdFlag = (String)balanceDealMap.get("balDirect");
                            //变量.实际余额 = 返回结果.科目余额
                            actBalance = (BigDecimal)balanceDealMap.get("balance");
                        }
                        昨日区间浮动管理费余额 = actBalance;
                        昨日区间浮动管理费余额 = ArithmeticUtil.round(昨日区间浮动管理费余额, NumberEnum.MONEY);
                    }
                }*/
//                if (昨日组合估值 != null) {
//                    计费本金 = 昨日组合估值.getTdyAssetNetvalue();
//                    当日发生费用 = ArithmeticUtil.divide(ArithmeticUtil.multiply(计费本金, 综合费率),
//                        new BigDecimal(365), RealTimeValCommonUtil.AMOUNT_SCALE);
//                }
                BigDecimal delayAmt=待清算款合计Map.get(测算日期);
                计费本金 = ArithmeticUtil.doAdd(当日组合估值.getDelayPayAmt(), 资产负债合计Map.get(
                        测算日期) == null ? BigDecimal.ZERO : 资产负债合计Map.get(测算日期).getAssetSum());
                计费本金 = ArithmeticUtil.doSub(计费本金,资产负债合计Map.get(
                        测算日期) == null ? BigDecimal.ZERO : 资产负债合计Map.get(测算日期).getLiabilitiesSum(),delayAmt);
                当日发生费用 = ArithmeticUtil.divide(ArithmeticUtil.multiply(计费本金, 综合费率),
                    new BigDecimal(365), RealTimeValCommonUtil.AMOUNT_SCALE);
            }
            费用 = ArithmeticUtil.doAdd(费用,费用map.get(测算日期),当日发生费用);
            // 初始化：变量.组合费用头寸
            MockPtlFeePositionDto 组合费用头寸 = new MockPtlFeePositionDto();
            组合费用头寸.setPortfolioId(组合基本信息dto.getPortfolioId());
            组合费用头寸.setCdate(DateUtil.parse(测算日期));
            组合费用头寸.setPtlFeeType(RealTimeValCommonUtil.费用类型_综合费率);
            组合费用头寸.setPtlFeePlace("999999");
            组合费用头寸.setTdyFeeCapital(计费本金);
            组合费用头寸.setTdyFeeRate(综合费率);
            组合费用头寸.setTdyIntervalPayFee(费用);
            最终组合费用头寸扩展列表.add(组合费用头寸);
            if (StringUtil.equals(组合基本信息dto.getProfitType(), DictCodeList.收益类型_货币型.getValue())) {
                //计算净值用 扣回回购成本费用的成本 add xr S
                BigDecimal assetSumNofeeamt=ArithmeticUtil.doAdd(当日组合估值.getDelayPayAmt(), 资产负债合计Map.get(
                        测算日期) == null ? BigDecimal.ZERO : 资产负债合计Map.get(测算日期).getAssetSumNoFeeamt());
                //计算净值用 扣回回购成本费用的成本 add xr end
                当日组合估值.setAssetSum(ArithmeticUtil.doAdd(当日组合估值.getDelayPayAmt(), 资产负债合计Map.get(
                    测算日期) == null ? BigDecimal.ZERO : 资产负债合计Map.get(测算日期).getAssetSum()));

                当日组合估值.setLiabilitiesSum(ArithmeticUtil.doAdd(资产负债合计Map.get(
                        测算日期) == null ? BigDecimal.ZERO : 资产负债合计Map.get(测算日期).getLiabilitiesSum(),
                        费用合计map.get(测算日期), 费用,应付客户收益合计));
                //计算净值用 扣回回购成本费用的成本 add xr S
                BigDecimal tdyAssetNetvalueNoFeeamt=ArithmeticUtil.doSub(assetSumNofeeamt, 当日组合估值.getLiabilitiesSum());
                //计算净值用 扣回回购成本费用的成本 add xr end

                当日组合估值.setTdyAssetNetvalue(
                    ArithmeticUtil.doSub(当日组合估值.getAssetSum(), 当日组合估值.getLiabilitiesSum()));
                //融资成本的fixed WZZGBS-607,统计融资成本统计到负债端的在万分收益扣除掉 add by zhouxiuping at 20201001
                BigDecimal cashAmt=现金成本合计Map.get(
                        测算日期) == null ? BigDecimal.ZERO : 现金成本合计Map.get(测算日期);
                BigDecimal 昨日cashAmt=现金成本合计Map.get(DateUtil.addDay(测算日期,-1)) ;
                当日组合估值.setTdyAssetValue(ArithmeticUtil.doAdd(当日组合估值.getAssetSum()));
                if (昨日组合估值 != null) {//fixed WZZGBS-595 bug add by zhouxiuping at 2020.09.17  begin
                    //当日产品资产净值（费后）=当日资产合计-当日份额-费用-当日负债成本
                    //update by 熊燃 当日产品资产净值（费后）=当日资产合计-当日份额-费用-当日负债成本-回购费用（因回购费用在资产合计中已统计了）
                    BigDecimal 当日累计amt=ArithmeticUtil.doSub(当日组合估值.getAssetSum(),
                            当日组合估值.getTdyShareAmt(),当日发生费用,cashAmt,feeamt); //融资成本的fixed WZZGBS-607,统计融资成本统计到负债端的在万分收益扣除掉 add by zhouxiuping at 20201001
                    //昨日产品资产净值（费后）=昨日资产合计-昨日份额
                    BigDecimal 昨日累计amt=ArithmeticUtil.doSub(昨日组合估值.getTdyAssetValue(),昨日组合估值.getTdyShareAmt(),昨日cashAmt);
                    //当日产品净值产=当日累计amt-昨日累计amt
                    BigDecimal 当日净amt=ArithmeticUtil.doSub(当日累计amt,昨日累计amt);
                    if(测算日期.compareTo("2019-09-13")==0){
                        BigDecimal aa=当日累计amt;
                    }
                    //万分收益=当日产品净值产*10000/当日份额
                    当日组合估值.setTdyUnitValue(
                            ArithmeticUtil.divide(
                                    ArithmeticUtil.multiply(new BigDecimal(10000),
                                            当日净amt),
                                    当日组合估值.getTdyShareAmt(), RealTimeValCommonUtil.YIELD_SCALE));

                }else {//起息日当天
                    当日组合估值.setTdyUnitValue(
                            ArithmeticUtil.divide(
                                    ArithmeticUtil.multiply(new BigDecimal(10000),
                                            ArithmeticUtil.doSub(当日组合估值.getTdyAssetValue(),
                                                    当日组合估值.getTdyShareAmt(),feeamt)),
                                    当日组合估值.getTdyShareAmt(), RealTimeValCommonUtil.YIELD_SCALE));
                }//fixed WZZGBS-595 bug add by zhouxiuping at 2020.09.17  end
                当日组合估值.setTdyUnpaidIntincexp(
                    ArithmeticUtil.doSub(当日组合估值.getTdyAssetNetvalue(), 当日组合估值.getTdyShareAmt()));
                当日组合估值.setLiabilitiesSum(
                        ArithmeticUtil.doAdd(当日组合估值.getLiabilitiesSum(), 当日组合估值.getTdyUnpaidIntincexp()));
                当日组合估值.setTdyAssetNetvalue(
                        ArithmeticUtil.doSub(当日组合估值.getTdyAssetNetvalue(), 当日组合估值.getTdyUnpaidIntincexp()));
                应付客户收益合计 = ArithmeticUtil.doAdd(应付客户收益合计, 当日组合估值.getTdyUnpaidIntincexp());
            }
            else if (StringUtil.equals(组合基本信息dto.getProfitType(),
                DictCodeList.收益类型_净值型.getValue())) {
                //当日资产负债合计+待清算款
                BigDecimal tdyDelayAmt=待清算款合计Map.get(测算日期);//定义待清算款
                当日组合估值.setAssetSum(ArithmeticUtil.doAdd(资产负债合计Map.get(
                    测算日期) == null ? BigDecimal.ZERO : 资产负债合计Map.get(测算日期).getAssetSum()));

                当日组合估值.setLiabilitiesSum(ArithmeticUtil.doAdd(资产负债合计Map.get(
                    测算日期) == null ? BigDecimal.ZERO : 资产负债合计Map.get(测算日期).getLiabilitiesSum(),
                        费用合计map.get(测算日期), 费用,当日组合估值.getTdyUnpaidIntincexp(),tdyDelayAmt));
                // fixed WZZGBS-596BUG,待清算款为负增加到负债类合计 add by  zhouxiuping 负债类减清算款
                当日组合估值.setTdyAssetNetvalue(
                    ArithmeticUtil.doSub(当日组合估值.getAssetSum(), 当日组合估值.getLiabilitiesSum()));
                当日组合估值.setTdyAssetValue(当日组合估值.getAssetSum());
                当日组合估值.setTdyUnitValue(ArithmeticUtil.divide(当日组合估值.getTdyAssetNetvalue(),
                    当日组合估值.getTdyShareAmt(), RealTimeValCommonUtil.UNITCPRICE_SCALE));
                if (昨日组合估值 != null)
                    当日组合估值.setTotBounsAmt(ArithmeticUtil.doAdd(当日组合估值.getTotBounsAmt(),昨日组合估值.getTotBounsAmt()));
//                当日组合估值.setTdyUnitValueSum(ArithmeticUtil.divide(
//                        ArithmeticUtil.doAdd(当日组合估值.getTdyAssetNetvalue(), 当日组合估值.getTotBounsAmt()),
//                        当日组合估值.getTdyShareAmt(), RealTimeValCommonUtil.UNITCPRICE_SCALE));
                // 当日累计分红金额 字段  存值为当日累计分红净值  修改计算公式
                当日组合估值.setTdyUnitValueSum(
                    ArithmeticUtil.doAdd(ArithmeticUtil.divide(当日组合估值.getTdyAssetNetvalue(),
                            当日组合估值.getTdyShareAmt(), RealTimeValCommonUtil.UNITCPRICE_SCALE), 当日组合估值.getTotBounsAmt()));
            }
//            最终组合估值扩展列表.add(当日组合估值);
            if (昨日组合估值 == null) {
                昨日组合估值 = new MockPtlPortfolioValuationDto();
            }
            BeanUtil.copyPropertiesIgnoreNull(昨日组合估值, 当日组合估值);
            测算日期 = DateUtil.addDay(测算日期, 1);
        }
        
//        result.setMockPtlPortfolioValuationDtoList(最终组合估值扩展列表);

        return result;
    }

    /**
     * 资产估值及产品交易处理 <功能详细描述>
     * 
     * @param 资产基本信息列表
     * @param 组合基本信息dto
     * @param 组合资产估值Map
     * @param 资产端交易Map
     * @param 负债端交易列表
     * @param 费用交易列表
     * @param t_N资产变化
     * @param t_N现金变化
     * @param 资产端t_N交易Map
     * @param 测算开始日
     * @param 测算结束日
     * @param 基准日期
     * @param 汇率行情列表
     * @param 组合费用头寸扩展列表
     * @param 组合估值列表
     * @param 划款类t_N交易列表
     * @param 划款类交易列表
     * @see
     * @since 1.0
     */
    private static MockCalcAssetValuationResult 资产估值及产品交易处理(List<MockAssetInfoDto> 资产基本信息列表,
                                                            MockPtlPortfolioDto 组合基本信息dto,
                                                            Map<String, Map<String, MockPtlSecValuationDto>> 组合资产估值Map,
                                                            Map<String, Map<String, List<MockTrdProductDealDto>>> 资产端交易Map,
                                                            List<MockTrdProductDealDto> 负债端交易列表,
                                                            List<MockTrdProductDealDto> 费用交易列表,
                                                            List<MockTrdProductDealDto> 负债端T_N交易列表,
                                                            ConcurrentHashMap<MockSingleAssetChangingKey, Map<String, MockSingleAssetChanging>> t_N资产变化,
                                                            ConcurrentHashMap<MockCashChangingKey, BigDecimal> t_N现金变化,
                                                            Map<String, LiabilitiesChanging> t_N负债变化,
                                                            Map<String, Map<String, List<MockTrdProductDealDto>>> 资产端t_N交易Map,
                                                            String 测算开始日, String 测算结束日,
                                                            String 基准日期,
                                                            List<MockMstExchargeQuoteDto> 汇率行情列表,
                                                            List<MockPtlFeePositionDto> 组合费用头寸扩展列表,
                                                            List<MockPtlPortfolioValuationDto> 组合估值列表,
                                                            List<MockTrdProductDealDto> 划款类交易列表,
                                                            List<MockTrdProductDealDto> 划款类t_N交易列表,
                                                            List<BokTradeOrder> 交易排序规则) {
        MockCalcAssetValuationResult result = new MockCalcAssetValuationResult();
        /**
         * 处理后组合资产估值拓展
         */
        List<MockPtlSecValuationDto> 处理后组合资产估值拓展 = Collections.synchronizedList(
            new ArrayList<MockPtlSecValuationDto>());

        /**
         * 处理后组合费用头寸拓展
         */
        List<MockPtlFeePositionDto> 处理后组合费用头寸拓展 = Collections.synchronizedList(
            new ArrayList<MockPtlFeePositionDto>());

        /**
         * 处理后组合现金
         */
        ConcurrentHashMap<String, BigDecimal> 处理后组合现金 = new ConcurrentHashMap<String, BigDecimal>();

        /**
         * 处理后组合估值列表
         */
        List<MockPtlPortfolioValuationDto> 处理后组合估值列表 = Collections.synchronizedList(
                组合估值列表);

        // 线程池任务列表
        List<RealTimeValuationTask> taskList = new ArrayList<RealTimeValuationTask>();
        // 遍历资产基本信息列表
        资产基本信息列表.forEach(资产基本信息 -> {
            // 得到该资产的组合资产估值map,资产 端交易map,资产端T+N交易map
            Map<String, MockPtlSecValuationDto> 组合资产估值MapEx = 组合资产估值Map.get(
                资产基本信息.getFinprodId()) == null ? new HashMap<>() : 组合资产估值Map.get(
                    资产基本信息.getFinprodId());
            Map<String, List<MockTrdProductDealDto>> 资产端交易MapEx = 资产端交易Map.get(
                资产基本信息.getFinprodId()) == null ? new HashMap<>() : 资产端交易Map.get(
                    资产基本信息.getFinprodId());
            Map<String, List<MockTrdProductDealDto>> 资产端t_N交易MapEx = 资产端t_N交易Map.get(
                资产基本信息.getFinprodId()) == null ? new HashMap<>() : 资产端t_N交易Map.get(
                    资产基本信息.getFinprodId());

            // 将变量.估值Map和变量.交易Map 的keyset 去重后，循环 投资目的
            if (组合资产估值MapEx != null) {
                组合资产估值MapEx.keySet().forEach(投资目的 -> {
                    投资目的 = 投资目的 == null ? "999999" : 投资目的;
                    RealTimeValuationTask RealTimeValuationTask = new RealTimeValuationTask(
                        RealTimeValCommonUtil.估值类型_资产估值, 投资目的, 资产基本信息, 组合基本信息dto,
                        组合资产估值MapEx.get(投资目的), 资产端交易MapEx.get(投资目的), 资产端t_N交易MapEx.get(投资目的),
                        t_N资产变化.get(new MockSingleAssetChangingKey(资产基本信息.getFinprodId(), 投资目的)),
                        测算开始日, 测算结束日, 基准日期, 汇率行情列表, 处理后组合资产估值拓展, t_N资产变化, t_N现金变化, 交易排序规则);
                    taskList.add(RealTimeValuationTask);

                });
            }
            if (资产端交易MapEx != null) {
                资产端交易MapEx.keySet().forEach(投资目的 -> {
                    投资目的 = 投资目的 == null ? "999999" : 投资目的;
                    if (组合资产估值MapEx == null || 组合资产估值MapEx.get(投资目的) == null) {
                        RealTimeValuationTask RealTimeValuationTask = new RealTimeValuationTask(
                            RealTimeValCommonUtil.估值类型_资产估值, 投资目的, 资产基本信息, 组合基本信息dto,
                            组合资产估值MapEx.get(投资目的), 资产端交易MapEx.get(投资目的), 资产端t_N交易MapEx.get(投资目的),
                            t_N资产变化.get(
                                new MockSingleAssetChangingKey(资产基本信息.getFinprodId(), 投资目的)),
                            测算开始日, 测算结束日, 基准日期, 汇率行情列表, 处理后组合资产估值拓展, t_N资产变化, t_N现金变化, 交易排序规则);
                        taskList.add(RealTimeValuationTask);
                    }
                });
            }

            if (资产端t_N交易MapEx != null) {
                资产端t_N交易MapEx.keySet().forEach(投资目的 -> {
                    投资目的 = 投资目的 == null ? "999999" : 投资目的;
                    if ((组合资产估值MapEx == null || 组合资产估值MapEx.get(投资目的) == null)
                        &&(资产端交易MapEx == null || 资产端交易MapEx.get(投资目的) == null)) {
                        RealTimeValuationTask RealTimeValuationTask = new RealTimeValuationTask(
                                RealTimeValCommonUtil.估值类型_资产估值, 投资目的, 资产基本信息, 组合基本信息dto,
                                组合资产估值MapEx.get(投资目的), 资产端交易MapEx.get(投资目的), 资产端t_N交易MapEx.get(投资目的),
                                t_N资产变化.get(
                                        new MockSingleAssetChangingKey(资产基本信息.getFinprodId(), 投资目的)),
                                测算开始日, 测算结束日, 基准日期, 汇率行情列表, 处理后组合资产估值拓展, t_N资产变化, t_N现金变化, 交易排序规则);
                        taskList.add(RealTimeValuationTask);
                    }
                });
            }
        });

        // 划款交易
        划款类交易列表.addAll(划款类t_N交易列表);
        划款类交易列表.forEach(entity -> {
            RealTimeValuationTask RealTimeValuationTask = new RealTimeValuationTask(
                RealTimeValCommonUtil.估值类型_划款交易, t_N现金变化, entity, 基准日期);
            taskList.add(RealTimeValuationTask);
        });

        // 获取前一日组合估值
        MockPtlPortfolioValuationDto 组合估值 = null;
        Map<Date, MockPtlPortfolioValuationDto> 组合估值Map = new HashMap<>();
        if (!StringUtil.isEmptyList(组合估值列表)) {
            组合估值Map = 组合估值列表.stream().collect(JoyinCollectors.toMap(x -> x.getCdate(), x -> x));
        }
        Date 测算开始日前一日 = DateUtil.addDay(DateUtil.parse(测算开始日), -1);
        if (组合估值Map.containsKey(测算开始日前一日)) {
            组合估值 = 组合估值Map.get(测算开始日前一日);
        }
        else {
            组合估值 = new MockPtlPortfolioValuationDto();
            组合估值.setPortfolioId(组合基本信息dto.getPortfolioId());
            组合估值.setCcy(组合基本信息dto.getCcy());
            组合估值.setCdate(测算开始日前一日);
            组合估值.setTdyShareAmt(BigDecimal.ZERO);
            组合估值.setDelayPayAmt(BigDecimal.ZERO);
        }
        
//        for (MockPtlPortfolioValuationDto entity : 组合估值列表) {
//            if (entity != null) {
//                if (DateUtil.compareDate(entity.getCdate(),
//                    DateUtil.addDay(DateUtil.parse(测算开始日), -1)) == 0) {
//                    组合估值 = entity;
//                    break;
//                }
//            }
//        }
        if (组合估值 != null) {
            // 产品存续期交易
            RealTimeValuationTask 产品存续期交易RealTimeValuationTask = new RealTimeValuationTask(
                RealTimeValCommonUtil.估值类型_产品存续期交易, 组合估值, 组合基本信息dto, 负债端交易列表, 负债端T_N交易列表, t_N负债变化,
                测算开始日, 测算结束日, 基准日期, t_N现金变化, 处理后组合估值列表);
            taskList.add(产品存续期交易RealTimeValuationTask);
        }

        // 费用交易
        RealTimeValuationTask 费用交易RealTimeValuationTask = new RealTimeValuationTask(
            RealTimeValCommonUtil.估值类型_费用交易, 组合费用头寸扩展列表, 组合基本信息dto, 费用交易列表, 测算开始日, 测算结束日, 基准日期,
            汇率行情列表, t_N现金变化, 处理后组合费用头寸拓展);
        taskList.add(费用交易RealTimeValuationTask);

        // 多线程启动
        RealTimeValuationMultiThreadService realTimeValuationMultiThreadService = SpringContextUtil.getBean(
            RealTimeValuationMultiThreadService.class);
        realTimeValuationMultiThreadService.execute(taskList);

        // 组合资产估值拓展
        result.setMockPtlSecValuationDtoList(处理后组合资产估值拓展);
        // 组合费用头寸拓展
        result.setMockPtlFeePositionDtoList(处理后组合费用头寸拓展);
        // T+N资产变化
        result.setAssetChanging(t_N资产变化);
        // T+N现金变化
        result.setCashChanging(t_N现金变化);
        // T+N负债变化
        result.setLiabilitiesChanging(t_N负债变化);
        // 组合现金
        result.setMockPtlCashPositionMap(处理后组合现金);
        // 组合估值列表
        result.setMockPtlPortfolioValuationDtoList(处理后组合估值列表);

        return result;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     * 
     * @param list
     * @param map
     * @see
     * @since 1.0
     */
    private static void tansferList2Map(List<MockTrdProductDealDto> list,
                                        Map<String, Map<String, List<MockTrdProductDealDto>>> map) {
        if (StringUtil.isEmptyList(list)) {
            return;
        }
        
        list.forEach(entity -> {
            if (entity != null) {
                if (map.get(entity.getFinprodId()) == null) {
                    Map<String, List<MockTrdProductDealDto>> map_投资目的 = new HashMap<String, List<MockTrdProductDealDto>>();
                    List<MockTrdProductDealDto> list_投资目的 = new ArrayList<MockTrdProductDealDto>();
                    list_投资目的.add(entity);
                    map_投资目的.put(entity.getInvAim() == null ? "999999" : entity.getInvAim(),
                        list_投资目的);
                    map.put(entity.getFinprodId(), map_投资目的);
                }
                else if (map.get(entity.getFinprodId()).get(
                    entity.getInvAim() == null ? "999999" : entity.getInvAim()) == null) {
                    List<MockTrdProductDealDto> list_投资目的 = new ArrayList<MockTrdProductDealDto>();
                    list_投资目的.add(entity);
                    map.get(entity.getFinprodId()).put(
                        entity.getInvAim() == null ? "999999" : entity.getInvAim(), list_投资目的);
                }
                else {
                    map.get(entity.getFinprodId()).get(
                        entity.getInvAim() == null ? "999999" : entity.getInvAim()).add(entity);
                }
            }
        });
    }
}
