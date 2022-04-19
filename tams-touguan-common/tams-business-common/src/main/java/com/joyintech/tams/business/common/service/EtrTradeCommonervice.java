//package com.joyintech.tams.business.common.service;
//
//import com.joyintech.framework.base.BaseService;
//import com.joyintech.framework.util.*;
//import com.joyintech.tams.business.common.dao.EtrTrdCommonMapper;
//import com.joyintech.tams.business.common.dto.*;
//import com.joyintech.tams.business.common.exception.PositionException;
//import com.joyintech.tams.business.common.pojo.BaseMap;
//import com.joyintech.tams.constant.DictCodeList;
//import com.joyintech.tams.constant.DictCodeListExt1;
//import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
//import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
//import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
//import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
//import com.joyintech.tams.systemtools.interf.IPlatformDate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * 公共方法service
// */
//@Service("etrTradeCommonervice")
//public class EtrTradeCommonervice extends BaseService {
//
//    @Autowired
//    private EtrCommonService etrCommonService;
//
//    @Autowired(required = false)
//    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;
//    @Autowired
//    private IPlatformDate iPlatformDate;
//    @Autowired
//    private EtrTrdCommonMapper etrTrdCommonMapper;
//
//
//    /**
//     * 场内指令，头寸信息
//     * @return
//     */
//    public List<BaseMap> queryPositionInfo(PositionCashSecRequest requestDto) {
//
//        // 条件非空校验
//        posiInfoCheckCondition(requestDto);
//
//        if (requestDto.getPtlList() == null || requestDto.getPtlList().size() == 0){
//            return nullData();
//        }
//
//
//        // 从原投管系统取
//        BigDecimal portfolioCash = BigDecimal.ZERO; // 组合现金可用
//        BigDecimal canCash = BigDecimal.ZERO; // 现金可用
//        BigDecimal realCash = BigDecimal.ZERO; // 现金可取
//        BigDecimal canSec = BigDecimal.ZERO; // 可用持仓
//        BigDecimal realSec = BigDecimal.ZERO; // 持仓数量
//        List<PositionCashSecDto> list = requestDto.getPtlList();
//        String flowType= requestDto.getFlowType();
//        flowType=StringUtil.equals("etr",flowType)?"01":"02";
//
//        if (list != null) {
//            //查询组合下所有现金账户
//
//            List<PositionCashSecDto> sumPortfolioAcctNoList = new ArrayList<>();
//            for (PositionCashSecDto dto : list){
//                if(StringUtil.isEmpty(dto.getCdate())){
//                    dto.setCdate(iPlatformDate.getSysDateStr());
//                }
//                List<Map<String,Object>> acctNoList=etrTrdCommonMapper.getPtlAcctInfo(dto.getPortfolioId());
//                if(!CollectionUtils.isEmpty(acctNoList)){
//                    for(Map<String,Object> tempMap:acctNoList){
//                        PositionCashSecDto cashSecDto = new PositionCashSecDto();
//                        BeanUtil.copyPropertiesIgnoreNull(cashSecDto, dto); //复制资产信息
//                        cashSecDto.setAcctId(tempMap.get("acctNo").toString());
//                        sumPortfolioAcctNoList.add(cashSecDto);
//                    }
//                }
//            }
//            //组合资金可用 1、老资金相关表
//            if(!CollectionUtils.isEmpty(sumPortfolioAcctNoList)){
//                for (PositionCashSecDto dto : sumPortfolioAcctNoList){
//                    PositionCashSecData data=querySingleCash(dto);
//                    if(data!=null&&data.getCashT()!=null){
//                        portfolioCash=portfolioCash.add(data.getCashT());
//                    }
//                }
//                //组合资金可用 2、新头寸引擎
//                BigDecimal newPortfolioAmount=queryPosition(sumPortfolioAcctNoList,flowType, BussinessDictCodeList.持仓类型_可用.getValue());
//                if(newPortfolioAmount!=null){
//                    portfolioCash=portfolioCash.add(newPortfolioAmount);
//                }
//            }
//            //资金可用
//            //筛选出有资金账户的组合
//            List<PositionCashSecDto> sumAcctNoList = new ArrayList<>();
//            for (PositionCashSecDto dto : list){
//                if(StringUtil.isNotEmpty(dto.getAcctId())){
//                    PositionCashSecDto cashSecDto = new PositionCashSecDto();
//                    BeanUtil.copyPropertiesIgnoreNull(cashSecDto, dto); //复制资产信息
//                    sumAcctNoList.add(cashSecDto);
//                }
//            }
//            //资金可用金额
//            if(!CollectionUtils.isEmpty(sumAcctNoList)&&sumAcctNoList.size()>0){
//                //1、老资金相关表
//                for (PositionCashSecDto dto : list){
//                    PositionCashSecData data=querySingleCash(dto);
//                    if(data!=null&&data.getCashT()!=null){
//                        canCash=canCash.add(data.getCashT());
//                        realCash=realCash.add(data.getRealCash());
//                    }
//                }
//                //组合资金可用 2、新头寸引擎
//                BigDecimal newCashAmount=queryPosition(sumAcctNoList,flowType, BussinessDictCodeList.持仓类型_可用.getValue());
//                if(newCashAmount!=null){
//                    canCash=canCash.add(newCashAmount);
//                }
//                BigDecimal newRealCashAmount=queryPosition(sumAcctNoList,flowType, BussinessDictCodeList.持仓类型_实际.getValue());
//                if(newRealCashAmount!=null){
//                    realCash=realCash.add(newRealCashAmount);
//                }
//            }
//            //持仓可用
//            flowType=StringUtil.equals("etr",flowType)?"03":"04";
//            //筛选出有资金账户的组合
//            List<PositionCashSecDto> secList = new ArrayList<>();
//            for (PositionCashSecDto dto : list){
//                if(StringUtil.isNotEmpty(dto.getFinprodId())&&StringUtil.isNotEmpty(dto.getSecAcctId())){
//                    PositionCashSecDto cashSecDto = new PositionCashSecDto();
//                    BeanUtil.copyPropertiesIgnoreNull(cashSecDto, dto); //复制资产信息
//                    secList.add(cashSecDto);
//                }
//            }
//            //资金可用金额
//            if(!CollectionUtils.isEmpty(secList)&&secList.size()>0){
//                BigDecimal newSecAmount=queryPosition(secList,flowType, BussinessDictCodeList.持仓类型_可用.getValue());
//                if(newSecAmount!=null){
//                    canSec=canSec.add(newSecAmount);
//                }
//                BigDecimal newRealSecAmount=queryPosition(secList,flowType, BussinessDictCodeList.持仓类型_实际.getValue());
//                if(newRealSecAmount!=null){
//                    realSec=realSec.add(newRealSecAmount);
//                }
//            }
//
//
//        }
//        return assemble(portfolioCash,canCash,realCash,canSec,realSec);
//    }
//
//    /**
//     * 场内指令，头寸信息
//     * @return
//     */
//    public List<BaseMap> queryPositionInfoSingle(PositionCashSecRequest requestDto) a{
//
//        // 条件非空校验
//        posiInfoCheckCondition(requestDto);
//
//        if (requestDto.getPtlList() == null || requestDto.getPtlList().size() == 0){
//            return nullData();
//        }
//        List<PositionCashSecDto> list = requestDto.getPtlList();
//        if (list != null) {
//
//        String flowType= requestDto.getFlowType();
//        flowType=StringUtil.equals("etr",flowType)?"01":"02";
//        for (PositionCashSecDto dto : list){
//           // 从原投管系统取
//           BigDecimal portfolioCash = BigDecimal.ZERO; // 组合现金可用
//           BigDecimal canCash = BigDecimal.ZERO; // 现金可用
//           BigDecimal realCash = BigDecimal.ZERO; // 现金可取
//           BigDecimal canSec = BigDecimal.ZERO; // 可用持仓
//           BigDecimal realSec = BigDecimal.ZERO; // 持仓数量
//           if(StringUtil.isEmpty(dto.getCdate())){
//               dto.setCdate(iPlatformDate.getSysDateStr());
//           }
//            //查询组合下所有现金账户
//
//            List<PositionCashSecDto> sumPortfolioAcctNoList = new ArrayList<>();
//            List<Map<String,Object>> acctNoList=etrTrdCommonMapper.getPtlAcctInfo(dto.getPortfolioId());
//            if(!CollectionUtils.isEmpty(acctNoList)){
//                for(Map<String,Object> tempMap:acctNoList){
//                    PositionCashSecDto cashSecDto = new PositionCashSecDto();
//                    BeanUtil.copyPropertiesIgnoreNull(cashSecDto, dto); //复制资产信息
//                    cashSecDto.setAcctId(tempMap.get("acctNo").toString());
//                    sumPortfolioAcctNoList.add(cashSecDto);
//                }
//            }
//            //组合资金可用 1、老资金相关表
//            if(!CollectionUtils.isEmpty(sumPortfolioAcctNoList)){
//                for (PositionCashSecDto tempDto : sumPortfolioAcctNoList){
//                    PositionCashSecData data=querySingleCash(tempDto);
//                    if(data!=null&&data.getCashT()!=null){
//                        portfolioCash=portfolioCash.add(data.getCashT());
//                    }
//                }
//                //组合资金可用 2、新头寸引擎
//                BigDecimal newPortfolioAmount=queryPosition(sumPortfolioAcctNoList,flowType, BussinessDictCodeList.持仓类型_可用.getValue());
//                if(newPortfolioAmount!=null){
//                    portfolioCash=portfolioCash.add(newPortfolioAmount);
//                }
//            }
//            //资金可用
//            //筛选出有资金账户的组合
//            List<PositionCashSecDto> sumAcctNoList = new ArrayList<>();
//            for (PositionCashSecDto dto : list){
//                if(StringUtil.isNotEmpty(dto.getAcctId())){
//                    PositionCashSecDto cashSecDto = new PositionCashSecDto();
//                    BeanUtil.copyPropertiesIgnoreNull(cashSecDto, dto); //复制资产信息
//                    sumAcctNoList.add(cashSecDto);
//                }
//            }
//            //资金可用金额
//            if(!CollectionUtils.isEmpty(sumAcctNoList)&&sumAcctNoList.size()>0){
//                //1、老资金相关表
//                for (PositionCashSecDto dto : list){
//                    PositionCashSecData data=querySingleCash(dto);
//                    if(data!=null&&data.getCashT()!=null){
//                        canCash=canCash.add(data.getCashT());
//                        realCash=realCash.add(data.getRealCash());
//                    }
//                }
//                //组合资金可用 2、新头寸引擎
//                BigDecimal newCashAmount=queryPosition(sumAcctNoList,flowType, BussinessDictCodeList.持仓类型_可用.getValue());
//                if(newCashAmount!=null){
//                    canCash=canCash.add(newCashAmount);
//                }
//                BigDecimal newRealCashAmount=queryPosition(sumAcctNoList,flowType, BussinessDictCodeList.持仓类型_实际.getValue());
//                if(newRealCashAmount!=null){
//                    realCash=realCash.add(newRealCashAmount);
//                }
//            }
//            //持仓可用
//            flowType=StringUtil.equals("etr",flowType)?"03":"04";
//            //筛选出有资金账户的组合
//            List<PositionCashSecDto> secList = new ArrayList<>();
//            for (PositionCashSecDto dto : list){
//                if(StringUtil.isNotEmpty(dto.getFinprodId())&&StringUtil.isNotEmpty(dto.getSecAcctId())){
//                    PositionCashSecDto cashSecDto = new PositionCashSecDto();
//                    BeanUtil.copyPropertiesIgnoreNull(cashSecDto, dto); //复制资产信息
//                    secList.add(cashSecDto);
//                }
//            }
//            //资金可用金额
//            if(!CollectionUtils.isEmpty(secList)&&secList.size()>0){
//                BigDecimal newSecAmount=queryPosition(secList,flowType, BussinessDictCodeList.持仓类型_可用.getValue());
//                if(newSecAmount!=null){
//                    canSec=canSec.add(newSecAmount);
//                }
//                BigDecimal newRealSecAmount=queryPosition(secList,flowType, BussinessDictCodeList.持仓类型_实际.getValue());
//                if(newRealSecAmount!=null){
//                    realSec=realSec.add(newRealSecAmount);
//                }
//            }
//            assemble(portfolioCash,canCash,realCash,canSec,realSec);
//
//
//        }
//        return null;
//    }
//
//    private void posiInfoCheckCondition(PositionCashSecRequest req) {
//        if (req == null){
//            throw new RuntimeException("入参不空！");
//        }
//    }
//
//    private List<BaseMap> nullData() {
//        List<BaseMap> resp = new ArrayList<>();
//
//        /*组合资金可用*/
//        resp.add(BaseMap.newMap()
//                .put("field", "porUsableCash")
//                .put("name", "组合资金可用（元）")
//                .put("type", "xj")
//                .put("amount", "--"));
//
//        /*账户资金可用*/
//        resp.add(BaseMap.newMap()
//                .put("field", "acctNoUsableCash")
//                .put("name", "账户资金可用（元）")
//                .put("type", "xj")
//                .put("amount", "--"));
//        /*账户资金可取*/
//        resp.add(BaseMap.newMap()
//                .put("field", "acctNoCanCash")
//                .put("name", "账户资金可取（元）")
//                .put("type", "xj")
//                .put("amount", "--"));
//        /*可用数量*/
//        resp.add(BaseMap.newMap()
//                .put("field", "usableAmount")
//                .put("name", "可用数量（股）")
//                .put("type", "cc")
//                .put("amount", "--"));
//        /*持仓数量*/
//        resp.add(BaseMap.newMap()
//                .put("field", "usableSec")
//                .put("name", "可用数量（股）")
//                .put("type", "cc")
//                .put("amount", "--"));
//
//
//        return resp;
//    }
//    private List<BaseMap> assemble(BigDecimal portfolioCash, BigDecimal canCash,BigDecimal realCash,BigDecimal canSec, BigDecimal realSec) {
//        String porUsableCash="porUsableCash",acctNoUsableCash="acctNoUsableCash",acctNoCanCash="acctNoCanCash",usableAmount="usableAmount",usableSec="usableSec";
//        // 初始化返回集合
//        List<BaseMap> resp = new ArrayList<>();
//        BaseMap bm = BaseMap.newMap(new HashMap());
//        /**
//         * 若老投管现金存在需累计
//         */
//        if (portfolioCash != null){
//            bm.put(porUsableCash, portfolioCash);
//        }else{
//            bm.put(porUsableCash, 0);
//        }
//        if (canCash != null){
//            bm.put(acctNoUsableCash, canCash);
//        }else{
//            bm.put(acctNoUsableCash, 0);
//        }
//        if (realCash != null){
//            bm.put(acctNoCanCash, realCash);
//        }else{
//            bm.put(acctNoCanCash, 0);
//        }
//        if (canSec != null){
//            bm.put(usableAmount, canSec);
//        }else{
//            bm.put(usableAmount, 0);
//        }
//        if (realSec != null){
//            bm.put(usableSec, realSec);
//        }else{
//            bm.put(usableSec, 0);
//        }
//
//        /*组合资金可用*/
//        resp.add(BaseMap.newMap()
//                .put("field",porUsableCash)
//                .put("name", "组合资金可用（元）")
//                .put("type", "xj")
//                .put("amount", bm.get(porUsableCash)));
//
//        /*账户资金可用*/
//        resp.add(BaseMap.newMap()
//                .put("field", acctNoUsableCash)
//                .put("name", "账户资金可用（元）")
//                .put("type", "xj")
//                .put("amount", bm.get(acctNoUsableCash)));
//        /*账户资金可取*/
//        resp.add(BaseMap.newMap()
//                .put("field",acctNoCanCash)
//                .put("name", "账户资金可取（元）")
//                .put("type", "xj")
//                .put("amount", bm.get(acctNoUsableCash)));
//        /*可用数量*/
//        resp.add(BaseMap.newMap()
//                .put("field", "usableSec")
//                .put("name", "可用数量（股）")
//                .put("type", "cc")
//                .put("amount",bm.get(usableSec)==null?0:bm.get(usableSec)));
//        /*持仓数量*/
//        resp.add(BaseMap.newMap()
//                .put("field", "usableSec")
//                .put("name", "可用数量（股）")
//                .put("type", "cc")
//                .put("amount", bm.get(usableSec)==null?0:bm.get(usableSec)));
//
//        return resp;
//    }
//    private String addToStr(BigDecimal a, BigDecimal b){
//        if (a == null && b== null){
//            return "--";
//        }
//        return decimalToStr(ArithmeticUtil.doAdd(a,b),"0.00");
//    }
//    /**
//     * 返回前端格式保留小数位
//     * @param b
//     * @param pattern
//     * @return
//     */
//    private String decimalToStr(BigDecimal b, String pattern){
//        if (b == null){
//            return "--";
//        }
//        DecimalFormat df1 = new DecimalFormat(pattern);
//        return df1.format(b);
//    }
//
//    public PositionCashSecData querySingleCash(PositionCashSecDto dto){
//        // 从原投管系统取
//        PositionCashSecData positionCashSecData=new PositionCashSecData();
//        BigDecimal cashT = BigDecimal.ZERO; // 指令t日可用
//        BigDecimal realCash = BigDecimal.ZERO; // 账户现金
//        String calId = StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())?DictCodeList.交易日历_上海交易所日历.getValue():DictCodeList.交易日历_深圳交易所日历.getValue();
//        BaseMap paramMap = BaseMap.newMap()
//                .put("finprodId", dto.getFinprodId())
//                .put("portfolioId", dto.getPortfolioId())
//                .put("ccy", DictCodeList.币种_人民币_CNY.getValue())
//                .put("vdate", dto.getCdate())
//                .put("calId", calId)
//                .put("acctId",dto.getAcctId())
//                ;
//
//        Map<String, Object> mapRst = etrCommonService.getProAvailableCash(paramMap);
//        if (mapRst != null){ // 指令t日可用，指令t1日可用
//            cashT = ArithmeticUtil.doAdd(cashT, BaseMap.newMap(mapRst).getBigDecimal("cashT"));
//        }
//        PtlCashPositionFlowDto positionQueryDto = new PtlCashPositionFlowDto();
//        positionQueryDto.setPortfolioId(dto.getPortfolioId());
//        positionQueryDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
//        positionQueryDto.setCdate(iPlatformDate.getSysDate());
//        // 实际现金（账户现金）
//        realCash = ArithmeticUtil.doAdd(realCash, basePtlCashPositionInfoService.queryUsableCashPosition(positionQueryDto));
//        positionCashSecData.setCashT(cashT);
//        positionCashSecData.setRealCash(realCash);
//        return positionCashSecData;
//    }
//    /**
//     * 从头寸引擎查询现金持仓余额
//     * @param ptList
//     * @return
//     */
//    private  BigDecimal queryPosition(List<PositionCashSecDto> ptList,String flowType,String positionType) {
//        Map<String, Object> map=new HashMap<>();
//        BigDecimal sumAmount=null;
//        map.put("ptList",buildPosition(ptList,flowType,positionType));
//        try {
//                String msg=PositionHttpRequest.queryPosition(map);
//                Map message = JsonUtils.json2Object(msg, Map.class);
//                if (message != null&&StringUtil.equals("success", message.get("code"))) {
//                    List<PositionQueryAmountDto> list=JsonUtils.json2List(message.get("data").toString(),PositionQueryAmountDto.class);
//                    sumAmount=list.stream().map(PositionQueryAmountDto::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
//                }
//            } catch (IOException e) {
//
//         }
//        return sumAmount;
//    }
//    /**
//     * 从头寸引擎查询现金持仓余额
//     * @param ptlList
//     * @return
//     */
//    private  List<PositionQueryDto> buildPosition(List<PositionCashSecDto>  ptlList,String flowType,String positionType) {
//        if (StringUtil.isNotEmpty(ptlList)) {
//            List<PositionQueryDto> list = new ArrayList<>();
//            for (PositionCashSecDto temp : ptlList) {
//                PositionQueryDto dto = new PositionQueryDto();
//                dto.setSystemAct(temp.getPortfolioId());
//                dto.setAcctId(temp.getAcctId());
//                dto.setCdate(temp.getCdate());
//                dto.setCcy(temp.getCcy());
//                dto.setFlowType(flowType);
//                dto.setPositionType(positionType);
//                //现金
//                if (StringUtil.equals(flowType, "01") || StringUtil.equals(flowType, "02")) {
//                    dto.setPs(temp.getPs());
//                }
//                //持仓
//                if (StringUtil.equals(flowType, "03") || StringUtil.equals(flowType, "04")) {
//                    dto.setSecAcctId(temp.getSecAcctId());
//                    dto.setFinprodId(temp.getFinprodId());
//                }
//                list.add(dto);
//            }
//            return list;
//        }
//        return null;
//    }
//
//
//}
