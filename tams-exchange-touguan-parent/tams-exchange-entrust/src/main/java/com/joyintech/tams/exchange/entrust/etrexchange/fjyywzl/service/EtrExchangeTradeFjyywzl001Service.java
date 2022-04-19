package com.joyintech.tams.exchange.entrust.etrexchange.fjyywzl.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.common.base.dao.FinProductConvertibleMapper;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.common.base.entity.FinProductConvertible;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dao.EtrExchangeTradeCommonMapper;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradeCommonRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradePositionsCommonResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.PsOptionsRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon002Service;
import com.joyintech.tams.exchange.entrust.etrexchange.fjyywzl.dao.EtrExchangeTradeFjyywzl001Mapper;
import com.joyintech.tams.exchange.entrust.etrexchange.fjyywzl.dto.EtrExchangeTradeFjyywzlZqzyDto;
import com.joyintech.tams.exchange.entrust.etrexchange.fjyywzl.dto.EtrExchangeTradeZqzyRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.EtrExchangeTradeQuotationHqResponse;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.constant.TempConstant;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecDto;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeCommonervice;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.TrdOthCodeInfoDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EtrExchangeTradeFjyywzl001Service extends CommonExportService<EtrExchangeTradeFjyywzlZqzyDto> {

    @Autowired
    private EtrExchangeTradeCommon001Service etrExchangeTradeCommon001Service;
    @Autowired
    private EtrExchangeTradeCommon002Service etrExchangeTradeCommon002Service;
    @Autowired
    private EtrExchangeTradeCommonMapper etrExchangeTradeCommonMapper;

    @Autowired
    private FinProductConvertibleMapper finProductConvertibleMapper;

    @Autowired
    private EtrExchangeTradeFjyywzl001Mapper etrExchangeTradeFjyywzl001Mapper;

    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    @Autowired
    private EtrTradeCommonervice etrTradeCommonervice;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public Page<EtrExchangeTradeFjyywzlZqzyDto> findZqzyPositionsPage(EtrExchangeTradeZqzyRequest request) {
        try {
            Map<String, Object> map = BeanUtil.object2Map(request);
            map.put("search_isNumberFlag", BaseMap.isNumeric(request.getSearch()));
            String userId = SecurityUtil.getLoginUser().getUserName();
            if (StringUtils.isNotEmpty(userId)) {
                logger.info("请求用户【{}】", "userId = " + userId);
                map.put("userId", SecurityUtil.getLoginUser().getUserName());
                getZqzyPositionParam(map);
                Page<EtrExchangeTradeFjyywzlZqzyDto> result = super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(),
                        () -> etrExchangeTradeFjyywzl001Mapper.findZqzyPositionsPageNew(map));
                List<EtrExchangeTradeFjyywzlZqzyDto> returnList=new ArrayList<>();
                if(result!=null&&!CollectionUtils.isEmpty(result.getRoot())){
                    // 计算浮动盈亏
                    for(EtrExchangeTradeFjyywzlZqzyDto entity : result.getRoot()){
                        //标准券数量T日标准券额度（交易）,单位：万元
                        BigDecimal secNum=ArithmeticUtil.divide(entity.getSecNum(),new BigDecimal(100),4, RoundingMode.DOWN);
                        entity.setSecNum(secNum);
                        //可用数量T日可入库持仓（指令）单位：万元
                        PositionCashSecDto secDto=new PositionCashSecDto();
                        BeanUtil.copyPropertiesIgnoreNull(secDto,entity);
                        secDto.setTradePlat(TempConstant.NO_TRADE_BUSINESS_ETR);
                        BigDecimal canRepoNum=etrTradeCommonervice.selectAvailableNum(secDto);
                        canRepoNum=ArithmeticUtil.divide(canRepoNum,new BigDecimal(100),2, RoundingMode.DOWN);
                        entity.setCanRepoNum(canRepoNum);
                        //过滤掉标准券和质押数量都等于0
                        if(entity.getSecNum().compareTo(BigDecimal.ZERO)==0&&canRepoNum.compareTo(BigDecimal.ZERO)==0){
                            continue;
                        }
                        //持仓数量，取交易的净额结算可用持仓（交易），单位：万元
                        BigDecimal  positionsNum=selectActNum(DictCodeListExt3.资产类型_资产账户持仓.getValue(),secDto);
                        positionsNum=ArithmeticUtil.divide(positionsNum,new BigDecimal(100),2, RoundingMode.DOWN);
                        entity.setPositionsNum(positionsNum);
                        //质押数量，取交易的T日质押券入库持仓（交易），单位：万元
                        BigDecimal  repoNum=selectActNum(DictCodeListExt3.资产类型_质押式通用回购入库质押券持仓.getValue(),secDto);
                        repoNum=ArithmeticUtil.divide(repoNum,new BigDecimal(100),2, RoundingMode.DOWN);
                        entity.setRepoNum(repoNum);
                        returnList.add(entity);
                    }
                    result.setRoot(returnList);
                }
                return result;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info(ex.getMessage());
        }
        return null;
    }

    private void getZqzyPositionParam(Map<String, Object> pageParam) {
        pageParam.put("POSITION_TYPE_03", BussinessDictCodeList.持仓类型_实际.getValue());
        pageParam.put("POSITION_TYPE_04", BussinessDictCodeList.持仓类型_质押.getValue());
        pageParam.put("POSITION_TYPE_05", BussinessDictCodeList.持仓类型_融入.getValue());
        pageParam.put("POSITION_TYPE_06", BussinessDictCodeList.持仓类型_融出.getValue());
        pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        pageParam.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        pageParam.put("PS_T01", BussinessDictCodeList.交易方向_买入.getValue());
        pageParam.put("PS_T02", BussinessDictCodeList.交易方向_卖出.getValue());
        pageParam.put("PS_T03", BussinessDictCodeList.交易方向_行权回售.getValue());
        pageParam.put("PS_T05", BussinessDictCodeList.交易方向_申购确认.getValue());
        pageParam.put("PS_T19", BussinessDictCodeList.交易方向_赎回确认.getValue());
        pageParam.put("PS_T46", DictCodeListExt2.交易方向_配股.getValue());
        pageParam.put("PS_T48", DictCodeListExt2.交易方向_新股中签.getValue());
        pageParam.put("PS_T49", DictCodeListExt3.交易方向_债转股.getValue());
        pageParam.put("PS_T97", DictCodeListExt3.交易方向_配债.getValue());
        pageParam.put("PS_T04", DictCodeList.交易方向_申购.getValue());
        pageParam.put("LIBRARY_DIRECTION_01", DictCodeListExt3.交易方向_质押券入库.getValue());
        pageParam.put("TRADE_PLACE_03", BussinessDictCodeList.交易场所_深圳证券交易所.getValue());
        pageParam.put("TRADE_PLACE_04", BussinessDictCodeList.交易场所_上海证券交易所.getValue());
        pageParam.put("MORE_CONVRATE", 0.9);
        pageParam.put("A_158", DictCodeListExt3.资产类型_通用回购融资券折算前额度.getValue());
        pageParam.put("A_038", DictCodeListExt3.资产类型_债券本方券上交所净额结算可用持仓.getValue());
        pageParam.put("A_040", DictCodeListExt3.资产类型_债券本方券深交所净额结算竞价可用持仓.getValue());
        if (StringUtil.isNotEmpty(pageParam.get("date"))) {
            pageParam.put("date", DateUtil.parse((String) pageParam.get("date")));
        } else {
            pageParam.put("date", iPlatformDate.getSysDate());
        }
    }

    /**
     * 查询上交所和深交所标准券数量
     *
     * @param dto 页面参数
     * @return 上交所和深交所标准券数量
     * @see
     * @since 1.0
     */
    public Map<String, Object> loadSecNum(PositionCashSecDto dto) {
        Map<String, Object> resultMap = new HashMap<>();
        BigDecimal secNumSh=BigDecimal.ZERO;
        BigDecimal canNumSh=BigDecimal.ZERO;
        String rateSh="";
        BigDecimal secNumZh=BigDecimal.ZERO;
        BigDecimal canNumZh=BigDecimal.ZERO;
        String rateZh="";
        BigDecimal rateShTemp=BigDecimal.ZERO;
        BigDecimal rateSZhTemp=BigDecimal.ZERO;
        String canNumShColor="";
        String canNumZhColor="";
        String rateShColor="";
        String rateZhColor="";
        if(StringUtil.isNotEmpty(dto.getPortfolioId())&&StringUtil.isNotEmpty(dto.getAcctId())){
            Map<String,Object> map=new HashMap<>();
            map.put("portfolioId",dto.getPortfolioId());
            map.put("acctNo",dto.getAcctId());
            List<SelectOptionDto> ptlSecAcctInfoList=
            restTemplate.postForList(
                    ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getPtlSecAcctInfo", map,
                    SelectOptionDto.class);
            if(!CollectionUtils.isEmpty(ptlSecAcctInfoList)){
                SelectOptionDto selectOptionDto=ptlSecAcctInfoList.get(0);
                List<LinkedHashMap<String,String>>  acctSecList = (List<LinkedHashMap<String,String>>) selectOptionDto.getObject();
                List<PositionCashSecDto> shProList=new ArrayList<>();
                List<PositionCashSecDto> szProList=new ArrayList<>();
                for(LinkedHashMap<String,String> linkedHashMap:acctSecList){
                    PositionCashSecDto cashSecDto=new PositionCashSecDto();
                    cashSecDto.setPortfolioId(dto.getPortfolioId());
                    cashSecDto.setFinprodId("XN_PRODID999");
                    if(StringUtil.equals(BussinessDictCodeList.交易场所_上海证券交易所.getValue(),linkedHashMap.get("tradeMarket"))){
                        cashSecDto.setSecAcctId(linkedHashMap.get("secAcctId"));
                        shProList.add(cashSecDto);
                    }else   if(StringUtil.equals(BussinessDictCodeList.交易场所_深圳证券交易所.getValue(),linkedHashMap.get("tradeMarket"))){
                        cashSecDto.setSecAcctId(linkedHashMap.get("secAcctId"));
                        szProList.add(cashSecDto);
                    }
                }
                //筛选出当前组合下所有持仓
                canNumSh=etrExchangeTradeCommon002Service.queryProPosition(DictCodeListExt3.资产类型_通用回购融资券可用持仓.getValue(),shProList);
                secNumSh=etrExchangeTradeCommon002Service.queryProPosition(DictCodeListExt3.资产类型_质押式通用回购已使用融资券总额度.getValue(),shProList);
                if(secNumSh!=null&&secNumSh.compareTo(BigDecimal.ZERO) != 0){
                    BigDecimal sumNumSh=canNumSh.add(secNumSh);
                    rateShTemp=ArithmeticUtil.divide(secNumSh,sumNumSh,2);
                    rateShTemp=ArithmeticUtil.multiply(rateShTemp,new BigDecimal(100));
                    rateSh=rateShTemp+"%";
                }else{
                    rateSh="0%";
                }
                canNumZh=etrExchangeTradeCommon002Service.queryProPosition(DictCodeListExt3.资产类型_通用回购融资券可用持仓.getValue(),szProList);
                secNumZh=etrExchangeTradeCommon002Service.queryProPosition(DictCodeListExt3.资产类型_质押式通用回购已使用融资券总额度.getValue(),szProList);
                if(secNumZh!=null&&secNumZh.compareTo(BigDecimal.ZERO) != 0){
                    BigDecimal sumNumZh=canNumZh.add(secNumZh);
                    rateSZhTemp=ArithmeticUtil.divide(secNumZh,sumNumZh,2);
                    rateSZhTemp=ArithmeticUtil.multiply(rateSZhTemp,new BigDecimal(100));
                    rateZh=rateSZhTemp+"%";
                }else{
                    rateZh="0%";
                }
            }

        }
        //单位修改万元
        canNumSh=ArithmeticUtil.divide(canNumSh,new BigDecimal(100),2,RoundingMode.DOWN);
        secNumSh=ArithmeticUtil.divide(secNumSh,new BigDecimal(100),2,RoundingMode.DOWN);
        canNumZh=ArithmeticUtil.divide(canNumZh,new BigDecimal(100),2,RoundingMode.DOWN);
        secNumZh=ArithmeticUtil.divide(secNumZh,new BigDecimal(100),2,RoundingMode.DOWN);
        if(canNumSh.compareTo(BigDecimal.ZERO)>0){
            canNumShColor="green";
        }else if(canNumSh.compareTo(BigDecimal.ZERO)<0){
            canNumShColor="red";
        }
        if(canNumZh.compareTo(BigDecimal.ZERO)>0){
            canNumZhColor="green";
        }else if(canNumZh.compareTo(BigDecimal.ZERO)<0){
            canNumZhColor="red";
        }
        if(rateShTemp.compareTo(new BigDecimal(100))<0){
            rateShColor="green";
        }else if(rateShTemp.compareTo(new BigDecimal(100))>0){
            rateShColor="red";
        }
        if(rateSZhTemp.compareTo(BigDecimal.ZERO)<0){
            rateZhColor="green";
        }else if(rateSZhTemp.compareTo(BigDecimal.ZERO)>0){
            rateZhColor="red";
        }
        resultMap.put("canNumSh",canNumSh+"万元");
        resultMap.put("secNumSh",secNumSh+"万元");
        resultMap.put("rateSh",rateSh);
        resultMap.put("canNumZh",canNumZh+"万元");
        resultMap.put("secNumZh",secNumZh+"万元");
        resultMap.put("rateZh",rateZh);
        resultMap.put("canNumShColor",canNumShColor);
        resultMap.put("canNumZhColor",canNumZhColor);
        resultMap.put("rateShColor",rateShColor);
        resultMap.put("rateZhColor",rateShColor);
        return resultMap;
    }

    /**
     * 获取交易方向
     *
     * @param request 画面参数 SelectDto selectDto
     * @return 偏离度
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPsSelectOptions(PsOptionsRequest request, SelectDto selectDto) {
        Map<String, Object> param = JsonUtils.object2Map(request);
        List<SelectOptionDto> selectOptionDtos = new ArrayList<>();
        Map<String, Object> map = new HashMap<String, Object>();
        Date sysDate = DateUtil.platDateFromCache();
        param.put("date", DateUtil.format(sysDate));
        String othCodeType=request.getOthCodeType();
        List<TrdOthCodeInfoDto> list = etrExchangeTradeCommon001Service.getTrdOthCodeInfos(param);
        if (null != list && list.size() > 0) {
            for (TrdOthCodeInfoDto dto : list) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                if ( !StringUtils.equals(dto.getOthCodeType(), DictCodeListExt3.金融产品其他代码类型_配债代码.getValue())) {
                    // 只能选择申购，其他委托方向不可选
                    String ps = OthTypeMapToPs(dto.getOthCodeType());
                    if (StringUtils.equals(ps, DictCodeList.交易方向_申购.getValue())) {
                        map.clear();
                        map.put(ps, dto.getDeclaredPrice());
                        selectOptionDtos = selectDto.getOptions().stream().filter(option -> {
                            return StringUtil.equals(option.getId(), ps);
                        }).collect(Collectors.toList());
                        for (SelectOptionDto option : selectOptionDtos) {
                            option.setObject(map.get(option.getId()));
                        }
                        return selectOptionDtos;
                    } else if (StringUtils.equals(ps, DictCodeListExt3.交易方向_质押券入库.getValue())) {
                        String converseRatio = etrExchangeTradeCommon001Service.selectStdsecConverseRatio(param);
                        if (StringUtil.isNotEmpty(converseRatio)) {
                            map.put(DictCodeListExt3.交易方向_质押券入库.getValue(), converseRatio);
                            map.put(DictCodeListExt3.交易方向_质押券出库.getValue(), converseRatio);
                        }
                    } else {
                        map.put(ps, dto.getDeclaredPrice());
                    }
                } else {
                    if (StringUtils.equals(othCodeType, DictCodeListExt3.金融产品其他代码类型_配债代码.getValue())) { //过滤普通债券
                        map.put(DictCodeListExt3.交易方向_配债.getValue(), dto.getDeclaredPrice());
                    }
                }
            }
        }
        if(map==null||map.size()<=0){
            if (!map.containsKey(DictCodeListExt3.交易方向_质押券入库.getValue())) {
                String converseRatio = etrExchangeTradeCommon001Service.selectStdsecConverseRatio(param);
                if (StringUtil.isNotEmpty(converseRatio)) {
                    map.put(DictCodeListExt3.交易方向_质押券入库.getValue(), converseRatio);
                    map.put(DictCodeListExt3.交易方向_质押券出库.getValue(), converseRatio);
                }
            }
            String finprodId = (String) param.get("finprodId");
            if (!map.containsKey(DictCodeList.交易方向_行权_回售.getValue())) {
                List<FinExercisePlan> exercisePlanList = this.selectFinExercisePlanData(finprodId, sysDate);
                if (!StringUtil.isEmptyList(exercisePlanList)) {
                    map.put(DictCodeList.交易方向_行权_回售.getValue(), exercisePlanList.get(0).getExercisePrice());
                }
            }
            if (!map.containsKey(DictCodeListExt1.交易方向_债转股.getValue())) {
                FinProductConvertible finProductConvertible = this.selectConverseShareData(finprodId, sysDate);
                if (finProductConvertible != null) {
                    map.put(DictCodeListExt1.交易方向_债转股.getValue(), finProductConvertible.getConPrice());
                }
            }
        }

        // add by etliu 20211207 start
        // 交易所债券非交易类业务迁移

        selectOptionDtos = selectDto.getOptions().stream().filter(option -> {
            return map.containsKey(option.getId());
        }).collect(Collectors.toList());
        for (SelectOptionDto option : selectOptionDtos) {
            option.setObject(map.get(option.getId()));
        }
        return selectOptionDtos;
    }

    /**
     * 申报类型-》交易方向的映射
     *
     * @param othCodeType
     * @return
     */
    private String OthTypeMapToPs(String othCodeType) {
        if (org.apache.commons.lang.StringUtils.equals(othCodeType, DictCodeListExt3.金融产品其他代码类型_可转债信用申购代码.getValue())) {
            return DictCodeList.交易方向_申购.getValue();
        } else if (org.apache.commons.lang.StringUtils.equals(othCodeType, DictCodeListExt3.金融产品其他代码类型_回售代码.getValue())) {
            return DictCodeList.交易方向_行权_回售.getValue();
        } else if (org.apache.commons.lang.StringUtils.equals(othCodeType, DictCodeListExt3.金融产品其他代码类型_可转债转股代码.getValue())) {
            return DictCodeListExt1.交易方向_债转股.getValue();
        } else if (org.apache.commons.lang.StringUtils.equals(othCodeType, DictCodeListExt3.金融产品其他代码类型_质押代码.getValue())) {
            return DictCodeListExt3.交易方向_质押券入库.getValue();
        }
        return null;
    }

    /**
     * @return 行权回售数据
     * @Description //查询行权回售数据
     * @Param [map]
     * @version 1.0
     * @see
     **/
    public List<FinExercisePlan> selectFinExercisePlanData(String finprodId, Date paramDate) {
        Map<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("sysDate", paramDate);
        map.put("exerciseType", DictCodeList.行权方式_回售.getValue());
        return etrExchangeTradeCommonMapper.selectFinExercisePlanData(map);
    }

    /**
     * 查询转股信息
     *
     * @param finprodId finprodId
     * @param paramDate paramDate
     * @return 转股信息
     */
    public FinProductConvertible selectConverseShareData(String finprodId, Date paramDate) {
        if (StringUtils.isNotEmpty(finprodId)) {
            FinProductConvertible finProductConvertible = finProductConvertibleMapper.selectByPrimaryKey(finprodId);
            if (finProductConvertible != null && finProductConvertible.getConVdate() != null
                    && DateUtil.compareDate(finProductConvertible.getConVdate(), paramDate) <= 0) {
                return finProductConvertible;
            }
        }
        return null;
    }

   



    //持仓数量,质押数量
    private BigDecimal selectActNum(String holdingType,PositionCashSecDto dto) {
        BigDecimal canSec = BigDecimal.ZERO; // 可用持仓
        List<PositionCashSecDto> secList = new ArrayList<>();
        if (StringUtil.isNotEmpty(dto.getFinprodId()) && StringUtil.isNotEmpty(dto.getSecAcctId())) {
            PositionCashSecDto cashSecDto = new PositionCashSecDto();
            BeanUtil.copyPropertiesIgnoreNull(cashSecDto, dto); //复制资产信息
            cashSecDto.setHoldingType(holdingType);
            secList.add(cashSecDto);
        }
        BigDecimal newSecAmount = etrTradeCommonervice.queryPosition(secList, "04", "");
        if (newSecAmount != null) {
            canSec = canSec.add(newSecAmount);
        }
        return canSec;
    }


  
}
