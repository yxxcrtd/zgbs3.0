package com.joyintech.tams.invest.common.cstp.service;

import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.cstp.dao.CstpCommonMapper;
import com.joyintech.tams.invest.common.cstp.dao.CstpCommonMatchMapper;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.PtlAllocationDto;
import com.joyintech.tams.invest.common.dto.cstp.CfetsRepoDealQuotedPriceDto;
import com.joyintech.tams.invest.common.dto.cstp.CfetsRepoPledgeBondDto;
import com.joyintech.tams.invest.common.dto.cstp.IbuQuoteEntCrepPledgeCommonDto;
import javafx.scene.layout.BackgroundImage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 上下行计算拆分
 * @auther zhangnian
 * @date 2021/3/15
 */
@Service
public class CstpCommonService{




    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    protected JoyinRestTemplate restTemplate;

    @Autowired
    private CstpCommonMapper cstpCommonMapper;

    /**
     *  银行间上下行  多组合列表
     */
    public List<PtlAllocationDto> selectEtrReByQuoteId(String  quoteId){
        Map<String,Object> param = new HashMap<>();
        param.put("quoteId",quoteId);
        return  restTemplate.postForList(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsRepoDealQuotedPrice001/selectEtrReByQuoteId", param,
                PtlAllocationDto.class);
    }

    /**
     * 组合分配列表拆分
     * @param ptlAllocations
     * @param map(shareAmt:代表现券本金或者质押式本金)；fpriceAmt代表现券全价总额或者质押券到期结算金额
     * @return
     */
    public List<PtlAllocationDto> 组合分配拆分(List<PtlAllocationDto> ptlAllocations,Map<String,Object> map) {
        if (!StringUtil.isEmptyList(ptlAllocations)) {
            if (map.containsKey("isKtg") && StringUtil.equals((String) map.get("isKtg"), "Y")) {
                return 组合分配拆分跨托管(ptlAllocations, map);
            } else {
                return 组合分配拆分不跨托管(ptlAllocations, map);
            }
        }
        return ptlAllocations;
    }

    public List<PtlAllocationDto> 组合分配拆分跨托管(List<PtlAllocationDto> ptlAllocations,Map<String,Object> map){
        if(!StringUtil.isEmptyList(ptlAllocations)){
            BigDecimal cbfpriceAmtTotal = map.containsKey("cbfpriceAmt")?(BigDecimal)map.get("cbfpriceAmt"):BigDecimal.ZERO;
            BigDecimal scfpriceAmtTotal = map.containsKey("scfpriceAmt")?(BigDecimal)map.get("scfpriceAmt"):BigDecimal.ZERO;
            BigDecimal 中债总额尾差= cbfpriceAmtTotal; BigDecimal 上清总额尾差= scfpriceAmtTotal;
            //按比例重新计算交易金额  尾差交给最大金额
            //最大交易金额
            PtlAllocationDto ptlAllocationDtoMaxcb = ptlAllocations.stream().max(Comparator.comparing(PtlAllocationDto::getCbFpriceAmt)).get();
            PtlAllocationDto ptlAllocationDtoMaxsc = ptlAllocations.stream().max(Comparator.comparing(PtlAllocationDto::getScFpriceAmt)).get();
            //组合分配列表总金额
            BigDecimal cbfpriceAmtTotalFP = ptlAllocations.stream().map(PtlAllocationDto::getCbFpriceAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal scfpriceAmtTotalFP = ptlAllocations.stream().map(PtlAllocationDto::getScFpriceAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
            for(PtlAllocationDto ptlAllocationDto : ptlAllocations){
                BigDecimal cbfpriceAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(cbfpriceAmtTotal,ptlAllocationDto.getCbFpriceAmt()),cbfpriceAmtTotalFP,2, RoundingMode.HALF_DOWN);
                BigDecimal scfpriceAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(scfpriceAmtTotal,ptlAllocationDto.getScFpriceAmt()),scfpriceAmtTotalFP,2,RoundingMode.HALF_DOWN);
                ptlAllocationDto.setCbFpriceAmt(cbfpriceAmt);
                ptlAllocationDto.setScFpriceAmt(scfpriceAmt);
                中债总额尾差 = ArithmeticUtil.subtract(中债总额尾差,cbfpriceAmt);
                上清总额尾差 = ArithmeticUtil.subtract(上清总额尾差,scfpriceAmt);
            }
            //尾差处理  尾差放在指令的交易金额最大的记录
            for(PtlAllocationDto item : ptlAllocations){
                if(中债总额尾差.compareTo(BigDecimal.ZERO)>0&&StringUtil.equals(item.getEntrustId(),ptlAllocationDtoMaxcb.getEntrustId())){
                    item.setShareAmt(ArithmeticUtil.doAdd(item.getCbFpriceAmt(),中债总额尾差));
                }
                if(上清总额尾差.compareTo(BigDecimal.ZERO)>0&&StringUtil.equals(item.getEntrustId(),ptlAllocationDtoMaxsc.getEntrustId())){
                    item.setFpriceAmt(ArithmeticUtil.doAdd(item.getFpriceAmt(),上清总额尾差));
                }
                BigDecimal sum = ArithmeticUtil.doAdd(item.getCbFpriceAmt(),item.getScFpriceAmt());
                item.setFpriceAmt(sum);
                item.setShareAmt(sum);
            }
        }
        return ptlAllocations;
    }

    public List<PtlAllocationDto> 组合分配拆分不跨托管(List<PtlAllocationDto> ptlAllocations,Map<String,Object> map){
        if(!StringUtil.isEmptyList(ptlAllocations)){
            BigDecimal shareCtAmtTotal = map.containsKey("shareCtAmt")?(BigDecimal)map.get("shareCtAmt"):BigDecimal.ZERO;
            BigDecimal shareAmtTotal = map.containsKey("shareAmt")?(BigDecimal)map.get("shareAmt"):BigDecimal.ZERO;
            BigDecimal fpriceAmtTotal = map.containsKey("fpriceAmt")?(BigDecimal)map.get("fpriceAmt"):BigDecimal.ZERO;
            BigDecimal 交易份额尾差= shareAmtTotal;
            BigDecimal 折算交易份额尾差= shareCtAmtTotal;
            BigDecimal 全价总额尾差= fpriceAmtTotal;
            String ps = (String) map.get("ps");
            //按比例重新计算交易金额  尾差交给最大金额
            //最大交易金额
            PtlAllocationDto ptlAllocationDtoMax = ptlAllocations.stream().max(Comparator.comparing(PtlAllocationDto::getFpriceAmt)).get();
            //组合分配列表总金额
            BigDecimal shareAmtTotalFP = null;
            BigDecimal shareCtAmtTotalFP = null;
            shareAmtTotalFP = ptlAllocations.stream().map(PtlAllocationDto::getShareAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
            shareCtAmtTotalFP = ptlAllocations.stream().map(PtlAllocationDto::getShareCtAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal fpriceAmtTotalFP = ptlAllocations.stream().map(PtlAllocationDto::getFpriceAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
            for(PtlAllocationDto ptlAllocationDto : ptlAllocations){
                BigDecimal fpriceAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(fpriceAmtTotal,ptlAllocationDto.getFpriceAmt()),fpriceAmtTotalFP,2,RoundingMode.HALF_DOWN);
                ptlAllocationDto.setFpriceAmt(fpriceAmt);
                全价总额尾差 = ArithmeticUtil.subtract(全价总额尾差,fpriceAmt);
                BigDecimal shareCtAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(shareCtAmtTotal,ptlAllocationDto.getShareCtAmt()),shareCtAmtTotalFP,2, RoundingMode.HALF_DOWN);
                折算交易份额尾差 = ArithmeticUtil.subtract(折算交易份额尾差,shareCtAmt);
                ptlAllocationDto.setShareCtAmt(shareCtAmt);
                BigDecimal shareAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(shareAmtTotal,ptlAllocationDto.getShareAmt()),shareAmtTotalFP,2, RoundingMode.HALF_DOWN);
                交易份额尾差 = ArithmeticUtil.subtract(交易份额尾差,shareAmt);
                ptlAllocationDto.setShareAmt(shareAmt);
            }
            //尾差处理  尾差放在指令的交易金额最大的记录
            for(PtlAllocationDto item : ptlAllocations){
                if(全价总额尾差.compareTo(BigDecimal.ZERO)>0&&StringUtil.equals(item.getEntrustId(),ptlAllocationDtoMax.getEntrustId())){
                    item.setFpriceAmt(ArithmeticUtil.doAdd(item.getFpriceAmt(),全价总额尾差));
                }
                if(折算交易份额尾差.compareTo(BigDecimal.ZERO)>0&&StringUtil.equals(item.getEntrustId(),ptlAllocationDtoMax.getEntrustId())){
                    item.setShareCtAmt(ArithmeticUtil.doAdd(item.getShareCtAmt(),折算交易份额尾差));
                }
                if(交易份额尾差.compareTo(BigDecimal.ZERO)>0&&StringUtil.equals(item.getEntrustId(),ptlAllocationDtoMax.getEntrustId())){
                    item.setShareAmt(ArithmeticUtil.doAdd(item.getShareAmt(),交易份额尾差));
                }
            }
        }
        return ptlAllocations;
    }
    /**
     *  质押券列表 按委托+组合的交易金额比例拆分
     *  增加跨托管场景
     * */
    public List<IbuQuoteEntCrepPledgeCommonDto> 质押券拆分(CfetsRepoDealQuotedPriceDto cfetsRepoDealQuotedPriceDto) {
        if(StringUtils.equals(cfetsRepoDealQuotedPriceDto.getIsKtg(),"Y")){
            return 质押券拆分跨托管(cfetsRepoDealQuotedPriceDto);
        }
        else{
            return 质押券拆分不跨托管(cfetsRepoDealQuotedPriceDto);
        }
    }

    public List<IbuQuoteEntCrepPledgeCommonDto> 质押券拆分不跨托管(CfetsRepoDealQuotedPriceDto cfetsRepoDealQuotedPriceDto) {
        List<IbuQuoteEntCrepPledgeCommonDto> ibuQuoteEntCrepPledges = new ArrayList<>();
        List<CfetsRepoPledgeBondDto>  pledgeBondLists =   cfetsRepoDealQuotedPriceDto.getPledgeBondList();
        String isDzh = cfetsRepoDealQuotedPriceDto.getIsDzh();
        String  ps = cfetsRepoDealQuotedPriceDto.getPs();
        String isMq = cfetsRepoDealQuotedPriceDto.getIsMq(); //从接口接入数据是  质押券没有关联组合  key处理同逆回购
        if(StringUtil.equals(isDzh,"Y")){
            List<PtlAllocationDto> ptlAllocations = cfetsRepoDealQuotedPriceDto.getPtlAllocationList();
            //默认比例分配 尾差给交易金额最大
            //Map<String,Object> fpriceAmtTotalGroupByPtlMap = new HashMap<>();
            Map<String,Object> pledgePrinTotalByPtlMap = new HashMap<>();
            if (!StringUtil.isEmptyList(pledgeBondLists)) {
                //交易金额按组合分组
                BigDecimal fpriceAmtTotal = ptlAllocations.stream().map(PtlAllocationDto::getFpriceAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                /*Map<String, List<PtlAllocationDto>> map = ptlAllocations.stream().collect(Collectors.groupingBy(PtlAllocationDto::getPortfolioId));
                for (Map.Entry<String, List<PtlAllocationDto>> entry : map.entrySet()) {
                    fpriceAmtTotalGroupByPtlMap.put(entry.getKey(),entry.getValue().stream().map(PtlAllocationDto::getFpriceAmt).reduce(BigDecimal.ZERO, BigDecimal::add));
                }*/
                //券面总额(正回购 按组合+债分组   逆回购  按债分组)
                Map<String, List<CfetsRepoPledgeBondDto>> map2 = pledgeBondLists.stream().collect(Collectors.groupingBy((a -> StringUtil.equals(ps, BussinessDictCodeList.回购拆借方向_正回购.getValue())&&StringUtils.isEmpty(isMq)?a.getPortfolioId()+a.getFinprodId():a.getFinprodId())));
                for (Map.Entry<String, List<CfetsRepoPledgeBondDto>> entry : map2.entrySet()) {
                    pledgePrinTotalByPtlMap.put(entry.getKey(),entry.getValue().stream().map(CfetsRepoPledgeBondDto::getPledgePrin).reduce(BigDecimal.ZERO, BigDecimal::add));
                }
                //每个组合 交易金额最大(按委托代码和组合代码分组)
                Map<String, PtlAllocationDto> maxFpriceAmtMap = ptlAllocations.parallelStream().collect(Collectors.groupingBy(a -> a.getEntrustId()+a.getPortfolioId()
                        ,Collectors.collectingAndThen(Collectors.reducing((c1, c2) -> (c1.getFpriceAmt().compareTo(c2.getFpriceAmt())>=0) ? c1 : c2), Optional::get)));
                for(PtlAllocationDto item : ptlAllocations){
                    //BigDecimal fpriceAmtTotal  = (BigDecimal) fpriceAmtTotalGroupByPtlMap.get(item.getPortfolioId());
                    for(CfetsRepoPledgeBondDto item2 : pledgeBondLists){
                        if((StringUtils.equals(ps,BussinessDictCodeList.回购拆借方向_正回购.getValue())&&(StringUtils.equals(isMq,"Y")||StringUtils.equals(item.getPortfolioId(),item2.getPortfolioId())))
                        ||StringUtils.equals(ps,BussinessDictCodeList.回购拆借方向_逆回购.getValue())){
                            String key = StringUtil.equals(ps, BussinessDictCodeList.回购拆借方向_正回购.getValue())&&StringUtils.isEmpty(isMq)?item2.getPortfolioId()+item2.getFinprodId():item2.getFinprodId();
                            IbuQuoteEntCrepPledgeCommonDto ibuQuoteEntCrepPledge = new IbuQuoteEntCrepPledgeCommonDto();
                            ibuQuoteEntCrepPledge.setQuoteId(cfetsRepoDealQuotedPriceDto.getQuoteId());
                            ibuQuoteEntCrepPledge.setVersion(StringUtil.isNotEmpty(cfetsRepoDealQuotedPriceDto.getVersion())?cfetsRepoDealQuotedPriceDto.getVersion():-1L);// 版本号
                            ibuQuoteEntCrepPledge.setEntrustId(item.getEntrustId());
                            ibuQuoteEntCrepPledge.setPortfolioId(item.getPortfolioId());
                            ibuQuoteEntCrepPledge.setPledgeFinprodId(item2.getFinprodId());
                            ibuQuoteEntCrepPledge.setBondCode(item2.getBondCode());
                            ibuQuoteEntCrepPledge.setBranch(0L);// 分支*/
                            BigDecimal pledgeNumber  = ArithmeticUtil.divide(ArithmeticUtil.multiply(item2.getPledgePrin(),item.getFpriceAmt()),fpriceAmtTotal,2,RoundingMode.HALF_DOWN);
                            //尾差处理
                            if(pledgePrinTotalByPtlMap.containsKey(key)){
                                BigDecimal pledgePrinTotal = new BigDecimal(pledgePrinTotalByPtlMap.get(key).toString());
                                if(pledgePrinTotal.compareTo(BigDecimal.ZERO)>=0){
                                    pledgePrinTotalByPtlMap.put(key,ArithmeticUtil.subtract(pledgePrinTotal,pledgeNumber));
                                }
                            }
                            ibuQuoteEntCrepPledge.setPledgeNumber(pledgeNumber.setScale(2));// 质押数量
                            ibuQuoteEntCrepPledge.setPledgeRatio(ArithmeticUtil.divide(item2.getPledgeRate(), new BigDecimal(100), 4));// 质押率*/
                            ibuQuoteEntCrepPledge.setUnitCprice(item2.getNetPrice());
                            ibuQuoteEntCrepPledges.add(ibuQuoteEntCrepPledge);
                        }
                    }
                }
                //尾差处理  尾差放在指令的交易金额最大的记录
                ibuQuoteEntCrepPledges.stream().forEach(item -> {
                    if(maxFpriceAmtMap.containsKey(item.getEntrustId()+item.getPortfolioId())){
                        String key = StringUtil.equals(ps, BussinessDictCodeList.回购拆借方向_正回购.getValue())&&StringUtils.isEmpty(isMq)?item.getPortfolioId()+item.getPledgeFinprodId():item.getPledgeFinprodId();
                        if (pledgePrinTotalByPtlMap.containsKey(key)) {
                            item.setPledgeNumber(ArithmeticUtil.divide(ArithmeticUtil.doAdd(item.getPledgeNumber(),new BigDecimal(pledgePrinTotalByPtlMap.get(key).toString())),new BigDecimal(100),2));
                        }
                    }
                });
            }
        }
        return ibuQuoteEntCrepPledges;
    }

    /**
     * 01代表中债  02 代表上清
     * */
    public List<IbuQuoteEntCrepPledgeCommonDto> 质押券拆分跨托管(CfetsRepoDealQuotedPriceDto cfetsRepoDealQuotedPriceDto) {
        List<IbuQuoteEntCrepPledgeCommonDto> ibuQuoteEntCrepPledges = new ArrayList<>();
        List<CfetsRepoPledgeBondDto>  pledgeBondLists =   cfetsRepoDealQuotedPriceDto.getPledgeBondList();
        String isDzh = cfetsRepoDealQuotedPriceDto.getIsDzh();
        String  ps = cfetsRepoDealQuotedPriceDto.getPs();
        String isMq = cfetsRepoDealQuotedPriceDto.getIsMq();
        if(StringUtil.equals(isDzh,"Y")){
            List<PtlAllocationDto> ptlAllocations = cfetsRepoDealQuotedPriceDto.getPtlAllocationList();
            //默认比例分配 尾差给交易金额最大
            Map<String,Object> fpriceAmtTotalGroupByPtlMap = new HashMap<>();
            Map<String,Object> pledgePrinTotalByPtlMap = new HashMap<>();
            if (!StringUtil.isEmptyList(pledgeBondLists)) {
                //交易金额按组合分组
                Map<String, List<PtlAllocationDto>> map = ptlAllocations.stream().collect(Collectors.groupingBy(PtlAllocationDto::getPortfolioId));
                for (Map.Entry<String, List<PtlAllocationDto>> entry : map.entrySet()) {
                    fpriceAmtTotalGroupByPtlMap.put(entry.getKey()+"01",entry.getValue().stream().map(PtlAllocationDto::getCbFpriceAmt).reduce(BigDecimal.ZERO, BigDecimal::add));
                    fpriceAmtTotalGroupByPtlMap.put(entry.getKey()+"02",entry.getValue().stream().map(PtlAllocationDto::getScFpriceAmt).reduce(BigDecimal.ZERO, BigDecimal::add));
                }
                //券面总额(正回购 按组合+债分组   逆回购  按债分组)
                Map<String, List<CfetsRepoPledgeBondDto>> map2 = pledgeBondLists.stream().collect(Collectors.groupingBy((a -> StringUtil.equals(ps, BussinessDictCodeList.回购拆借方向_正回购.getValue())&&StringUtils.isEmpty(isMq)?a.getPortfolioId()+a.getFinprodId():a.getFinprodId())));
                for (Map.Entry<String, List<CfetsRepoPledgeBondDto>> entry : map2.entrySet()) {
                    pledgePrinTotalByPtlMap.put(entry.getKey(),entry.getValue().stream().map(CfetsRepoPledgeBondDto::getPledgePrin).reduce(BigDecimal.ZERO, BigDecimal::add));
                }
                //每个组合 交易金额最大(按委托代码和组合代码分组)
                Map<String, PtlAllocationDto> maxcbFpriceAmtMap = ptlAllocations.parallelStream().collect(Collectors.groupingBy(a -> a.getEntrustId()+a.getPortfolioId()
                        ,Collectors.collectingAndThen(Collectors.reducing((c1, c2) -> (c1.getCbFpriceAmt().compareTo(c2.getCbFpriceAmt())>=0) ? c1 : c2), Optional::get)));
                Map<String, PtlAllocationDto> maxscFpriceAmtMap = ptlAllocations.parallelStream().collect(Collectors.groupingBy(a -> a.getEntrustId()+a.getPortfolioId()
                        ,Collectors.collectingAndThen(Collectors.reducing((c1, c2) -> (c1.getScFpriceAmt().compareTo(c2.getScFpriceAmt())>=0) ? c1 : c2), Optional::get)));
                for(PtlAllocationDto item : ptlAllocations){
                    BigDecimal cbFpriceAmtTotal  = (BigDecimal) fpriceAmtTotalGroupByPtlMap.get(item.getPortfolioId()+"01");
                    BigDecimal scFpriceAmtTotal  = (BigDecimal) fpriceAmtTotalGroupByPtlMap.get(item.getPortfolioId()+"02");
                    for(CfetsRepoPledgeBondDto item2 : pledgeBondLists){
                        String key = StringUtil.equals(ps, BussinessDictCodeList.回购拆借方向_正回购.getValue())&&StringUtils.isEmpty(isMq)?item2.getPortfolioId()+item2.getFinprodId():item2.getFinprodId();
                        IbuQuoteEntCrepPledgeCommonDto ibuQuoteEntCrepPledge = new IbuQuoteEntCrepPledgeCommonDto();
                        ibuQuoteEntCrepPledge.setQuoteId(cfetsRepoDealQuotedPriceDto.getQuoteId());
                        ibuQuoteEntCrepPledge.setVersion(StringUtil.isNotEmpty(cfetsRepoDealQuotedPriceDto.getVersion())?cfetsRepoDealQuotedPriceDto.getVersion():-1L);// 版本号
                        ibuQuoteEntCrepPledge.setEntrustId(item.getEntrustId());
                        ibuQuoteEntCrepPledge.setPortfolioId(item.getPortfolioId());
                        ibuQuoteEntCrepPledge.setPledgeFinprodId(item2.getFinprodId());
                        ibuQuoteEntCrepPledge.setBondCode(item2.getBondCode());
                        ibuQuoteEntCrepPledge.setBranch(0L);// 分支*/
                        BigDecimal pledgeNumber =BigDecimal.ZERO;
                        if(StringUtils.equals(item2.getRepRegistOrg(),"01")){
                            pledgeNumber  = ArithmeticUtil.divide(ArithmeticUtil.multiply(item2.getPledgePrin(),item.getCbFpriceAmt()),cbFpriceAmtTotal,2,RoundingMode.HALF_DOWN);
                        }
                        if(StringUtils.equals(item2.getRepRegistOrg(),"02")){
                            pledgeNumber  = ArithmeticUtil.divide(ArithmeticUtil.multiply(item2.getPledgePrin(),item.getScFpriceAmt()),scFpriceAmtTotal,2,RoundingMode.HALF_DOWN);
                        }
                        //尾差处理
                        if(pledgePrinTotalByPtlMap.containsKey(key)){
                            BigDecimal pledgePrinTotal = new BigDecimal(pledgePrinTotalByPtlMap.get(key).toString());
                            if(pledgePrinTotal.compareTo(BigDecimal.ZERO)>=0){
                                pledgePrinTotalByPtlMap.put(key,ArithmeticUtil.subtract(pledgePrinTotal,pledgeNumber));
                            }
                        }
                        ibuQuoteEntCrepPledge.setPledgeNumber(pledgeNumber.setScale(2));// 质押数量
                        ibuQuoteEntCrepPledge.setPledgeRatio(ArithmeticUtil.divide(item2.getPledgeRate(), new BigDecimal(100), 4));// 质押率*/
                        ibuQuoteEntCrepPledge.setUnitCprice(item2.getNetPrice());
                        ibuQuoteEntCrepPledge.setRepRegistOrg(item2.getRepRegistOrg());
                        ibuQuoteEntCrepPledges.add(ibuQuoteEntCrepPledge);
                    }
                }
                //尾差处理  尾差放在指令的交易金额最大的记录
                ibuQuoteEntCrepPledges.stream().forEach(item -> {
                    String key = StringUtil.equals(ps, BussinessDictCodeList.回购拆借方向_正回购.getValue())&&StringUtils.isEmpty(isMq)?item.getPortfolioId()+item.getPledgeFinprodId():item.getPledgeFinprodId();
                    if(StringUtils.equals(item.getRepRegistOrg(),"01")&&maxcbFpriceAmtMap.containsKey(item.getEntrustId()+item.getPortfolioId())){
                        if (pledgePrinTotalByPtlMap.containsKey(key)) {
                            item.setPledgeNumber(ArithmeticUtil.divide(ArithmeticUtil.doAdd(item.getPledgeNumber(),new BigDecimal(pledgePrinTotalByPtlMap.get(key).toString())),new BigDecimal(100),2));
                        }
                    }
                    if(StringUtils.equals(item.getRepRegistOrg(),"02")&&maxscFpriceAmtMap.containsKey(item.getEntrustId()+item.getPortfolioId())){
                        if (pledgePrinTotalByPtlMap.containsKey(key)) {
                            item.setPledgeNumber(ArithmeticUtil.divide(ArithmeticUtil.doAdd(item.getPledgeNumber(),new BigDecimal(pledgePrinTotalByPtlMap.get(key).toString())),new BigDecimal(100),2));
                        }
                    }
                });
            }
        }
        return ibuQuoteEntCrepPledges;
    }
    /**
     * 总费用就是按成交编号计算，计算结果结算手续费按金额拆分，交易手续费按照指令个数拆
     * @param ptlAllocations
     * @param map
     * @return
     */
    public List<PtlAllocationDto> 交易费用拆分(List<PtlAllocationDto> ptlAllocations,Map<String,Object> map) {
        List<TrdTradeFeeDetail> trdTradeFeeDetails= map.containsKey("trdTradeFeeDetails")?(List<TrdTradeFeeDetail>)map.get("trdTradeFeeDetails"):null;
        if(!StringUtil.isEmptyList(ptlAllocations)&&!StringUtil.isEmptyList(trdTradeFeeDetails)){
            Map<String, List<TrdTradeFeeDetail>> mapByFeeType = trdTradeFeeDetails.stream().collect(Collectors.groupingBy((a -> a.getFeeType())));
            //交易手续费实体
            TrdTradeFeeDetail trdTradeFeeDetailJy = mapByFeeType.containsKey(BussinessDictCodeList.费用类型_交易手续费.getValue())? ((List<TrdTradeFeeDetail>)mapByFeeType.get(BussinessDictCodeList.费用类型_交易手续费.getValue())).get(0):null;
            //结算手续费实体
            TrdTradeFeeDetail trdTradeFeeDetailJs = mapByFeeType.containsKey(BussinessDictCodeList.费用类型_结算手续费.getValue())? ((List<TrdTradeFeeDetail>)mapByFeeType.get(BussinessDictCodeList.费用类型_结算手续费.getValue())).get(0):null;
            //按比例重新计算交易金额  尾差交给最大金额
            //最大交易金额
            PtlAllocationDto ptlAllocationDtoMax = ptlAllocations.stream().max(Comparator.comparing(PtlAllocationDto::getShareAmt)).get();
            BigDecimal 交易手续费尾差 =null!=trdTradeFeeDetailJy? trdTradeFeeDetailJy.getFeeAmt():BigDecimal.ZERO;
            BigDecimal 结算手续费尾差 = null!=trdTradeFeeDetailJs?trdTradeFeeDetailJs.getFeeAmt():BigDecimal.ZERO;
            for(PtlAllocationDto ptlAllocationDto : ptlAllocations){
                List<TrdTradeFeeDetail> trdTradeFeeDetails2 = new ArrayList<>();
                if(null!=trdTradeFeeDetailJy){

                    //组合分配列表总金额
                    BigDecimal shareAmtTotalFP = ptlAllocations.stream().map(PtlAllocationDto::getShareAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal feeTotal = trdTradeFeeDetailJy.getFeeAmt();
                    TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
                    BeanUtil.copyPropertiesIgnoreNull(trdTradeFeeDetail,trdTradeFeeDetailJy);
                    BigDecimal fee = ArithmeticUtil.divide(ArithmeticUtil.multiply(feeTotal,ptlAllocationDto.getShareAmt()),shareAmtTotalFP,2, RoundingMode.HALF_DOWN);
                    trdTradeFeeDetail.setFeeAmt(fee);
                    交易手续费尾差 = ArithmeticUtil.subtract(交易手续费尾差,fee);
                    trdTradeFeeDetails2.add(trdTradeFeeDetail);
                }
                if(null!=trdTradeFeeDetailJs){
                    //组合分配列表总金额
                    //组合分配列表总金额
                    BigDecimal total = new BigDecimal(ptlAllocations.size());
                    BigDecimal feeTotal = trdTradeFeeDetailJs.getFeeAmt();
                    TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
                    BeanUtil.copyPropertiesIgnoreNull(trdTradeFeeDetail,trdTradeFeeDetailJs);
                    BigDecimal fee = ArithmeticUtil.divide(ArithmeticUtil.multiply(feeTotal,new BigDecimal(1)),total,2, RoundingMode.HALF_DOWN);
                    trdTradeFeeDetail.setFeeAmt(fee);
                    结算手续费尾差 = ArithmeticUtil.subtract(结算手续费尾差,fee);
                    trdTradeFeeDetails2.add(trdTradeFeeDetail);
                }
                ptlAllocationDto.setTradeFeeList(trdTradeFeeDetails2);
            }
            //尾差处理  尾差放在指令的交易金额最大的记录
            for(PtlAllocationDto item : ptlAllocations){
                if(!StringUtil.isEmptyList(item.getTradeFeeList())){
                    for(TrdTradeFeeDetail feeItem : item.getTradeFeeList()) {
                        if(交易手续费尾差.compareTo(BigDecimal.ZERO)!=0&&StringUtil.equals(item.getEntrustId(),ptlAllocationDtoMax.getEntrustId())
                        &&StringUtils.equals(feeItem.getFeeType(),BussinessDictCodeList.费用类型_交易手续费.getValue())){
                            feeItem.setFeeAmt(ArithmeticUtil.doAdd(feeItem.getFeeAmt(),交易手续费尾差));
                        }
                        if(结算手续费尾差.compareTo(BigDecimal.ZERO)!=0&&StringUtil.equals(item.getEntrustId(),ptlAllocationDtoMax.getEntrustId())
                                &&StringUtils.equals(feeItem.getFeeType(),BussinessDictCodeList.费用类型_结算手续费.getValue())){
                            feeItem.setFeeAmt(ArithmeticUtil.doAdd(feeItem.getFeeAmt(),结算手续费尾差));
                        }
                    }
                }
            }
        }
        return ptlAllocations;
    }

    public boolean excuteQuotePositionData(String entrustId,String execId,boolean isRevoke,String tradeId){
    	if(!StringUtil.isNotEmpty(entrustId)||!StringUtil.isNotEmpty(execId)){
    		return false;
    	}
    	String quoteId = cstpCommonMapper.selectQuoId(execId);
    	if(!StringUtil.isNotEmpty(quoteId)){
    		return false;
    	}
    	Map<String,Object> map = new HashMap<>();
    	map.put("entrustId", entrustId);
    	map.put("tradeId", quoteId);
    	List<PtlSecPositionFlowDto> ptlSecPositionFlowDtos =cstpCommonMapper.selectPtlSecPositionFlows(map);
    	List<PtlCashPositionFlowDto> ptlCashPositionFlowDtos =cstpCommonMapper.selectPtlCashPositionFlows(map);
    	for(PtlSecPositionFlowDto ptlSecPositionFlowDto:ptlSecPositionFlowDtos){
    		ptlSecPositionFlowDto.setTradeId(tradeId);
    		if(!isRevoke){//非撤销取反
    			ptlSecPositionFlowDto.setAmount(ptlSecPositionFlowDto.getAmount().negate());
    		}
    	}
    	for(PtlCashPositionFlowDto ptlCashPositionFlowDto:ptlCashPositionFlowDtos){
    		ptlCashPositionFlowDto.setOrderId(tradeId);
    		if(!isRevoke){//非撤销取反
    			ptlCashPositionFlowDto.setAmount(ptlCashPositionFlowDto.getAmount().negate());
    		}
    	}
    	
    	PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
        ptlPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlowDtos);
        ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlowDtos);
        return restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
                ptlPositionHoldDto, Boolean.class);
    }

    public List<IbuQuoteEntCrepPledgeCommonDto> listIbuQuoteEntCrepPledge(String quoteId){
        return restTemplate.getForList(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/listIbuQuoteEntCrepPledge?quoteId="+quoteId,
                 IbuQuoteEntCrepPledgeCommonDto.class);
    }
}

