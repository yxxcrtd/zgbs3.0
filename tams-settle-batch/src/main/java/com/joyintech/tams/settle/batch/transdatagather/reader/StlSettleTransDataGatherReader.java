package com.joyintech.tams.settle.batch.transdatagather.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleTransData;
import com.joyintech.tams.common.base.entity.SettleTransDataTotal;
import com.joyintech.tams.common.base.entity.SettleTransDataTotalAdd;
import com.joyintech.tams.common.base.entity.SettleTransDataTotalPledge;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.settle.batch.datadeal.service.StlDataDealProcessCommonService;
import com.joyintech.tams.settle.batch.transdatagather.dto.StlSettleTransDataGatherDto;
import com.joyintech.tams.settle.batch.transdatagather.dto.StlSettleTransDataGatherParamDto;
import com.joyintech.tams.settle.batch.transdatagather.service.StlSettleTransDataGatherService;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author meifaxin
 * @date 2021/10/29
 * @see StlSettleTransDataGatherReader
 * @since 1.0
 */
@Service
@Lazy
public class StlSettleTransDataGatherReader extends BaseReader<StlSettleTransDataGatherParamDto> {
    @Autowired
    private StlSettleTransDataGatherService stlSettleTransDataGatherService;

    @Autowired
    private StlDataDealProcessCommonService stlDataDealProcessCommonService;

    @Autowired(required = false)
    private SettleCommonService settleCommonService;

    @Override
    public List<StlSettleTransDataGatherParamDto> createList() {
        // 批处理执行信息
        SysBatchExecInfo execInfo = getBatchExecuteInfo();
        // 日期
        Date tradeDate = execInfo.getExecuteDate();
        //1、删除
        stlSettleTransDataGatherService.deleteTotalTradeAddByLastDeal(tradeDate);
        stlSettleTransDataGatherService.deleteTotalTradeByLastDeal(tradeDate);
        stlSettleTransDataGatherService.deleteTotalTradeStatusByLastDeal();
        //2、查询对账一致或者不需要对账的成交数据明细表数据
       List<StlSettleTransDataGatherDto> selectNeedTotalDataNewList = stlSettleTransDataGatherService.selectNeedTotalDataList(tradeDate);
        //3、
        //1)成交明细数量.回购类型 = 数据字典.回购类型.质押式竞价回购的数据组合list，后续逻辑参照【详细处理-协议回购sheet】
        List<StlSettleTransDataGatherParamDto> returnList=new ArrayList<>();
        List<StlSettleTransDataGatherDto> agreRepList= selectNeedTotalDataNewList.stream().filter(item->DictCodeList.回购类型_质押式协议回购.getValue().equals(item.getRepoType())).collect(Collectors.toList());
        // 处理协议回购数据
        this.createAgreRepList(agreRepList,tradeDate,returnList);
        //2)成交明细数量.交易方向 = 数据字典.交易方向.债转股的数据组合list，后续逻辑参照【详细处理-债转股】sheet
        List<StlSettleTransDataGatherDto> bondToStockList= selectNeedTotalDataNewList.stream().filter(item->DictCodeListExt3.交易方向_债转股.getValue().equals(item.getPs())).collect(Collectors.toList());
        // 处理债转股数据
        this.createBondToStockList(bondToStockList,tradeDate,returnList);
        //3)其他的还是下面的逻辑	 查询得到的数据list按照 席位号 + 申请编号 的维度进行分组 得到List<Map<席位号 + 申请编号,StlSettleTransDataGatherDto>>
        List<StlSettleTransDataGatherDto> selectNeedTotalDataList= selectNeedTotalDataNewList.stream().filter(item->!DictCodeListExt3.交易方向_债转股.getValue().equals(item.getPs())&&
                        !DictCodeList.回购类型_质押式协议回购.getValue().equals(item.getRepoType())).collect(Collectors.toList());
        Map<String,List<StlSettleTransDataGatherDto>> mapList=selectNeedTotalDataList
                .stream()
                .collect(Collectors.groupingBy(dto ->
                        dto.getApplyTradeId() + "_" + dto.getSeatNumber()));
        //4、循环当前循环的map的value
        List<SettleTransDataTotal> settleTransDataList=new ArrayList<>();
        List<SettleTransDataTotalAdd> settleTransDataAddList=new ArrayList<>();
        mapList.forEach((key, list) -> {
            SettleTransDataTotal dealTotal=new SettleTransDataTotal();
            StlSettleTransDataGatherDto settleTransDataDto=list.get(0);
//            if(StringUtil.equals(settleTransDataDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
//                    || StringUtil.equals(settleTransDataDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
//                    || (StringUtil.equals(settleTransDataDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
//                        &&StringUtil.equals(settleTransDataDto.getPs(), DictCodeListExt1.交易方向_债转股.getValue()))){
//                for (StlSettleTransDataGatherDto tempData : list) {
//                    String checkResult = tempData.getCheckResult();
//                    if (StringUtil.isEmpty(checkResult)
//                            || StringUtil.equals(checkResult, DictCodeListExt3.对账状态结果_一致.getValue())
//                            || StringUtil.equals(checkResult, DictCodeListExt3.对账状态结果_手工完成.getValue())) {
//                        dealTotal = new SettleTransDataTotal();
//                        BeanUtils.copyProperties(tempData, dealTotal);
//                        //  汇总交易编号
//                        dealTotal.setSeqId(IDGenerator.generate());
//                        // 回购需要将成交明细表的成交编号登到交易汇总表的原始成交编号 fuyongsheng add 20211215 start
//                        if(StringUtil.equals(settleTransDataDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
//                                || StringUtil.equals(settleTransDataDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())){
//                            dealTotal.setTradeIdRaw(tempData.getCjbh());
//                        }
//                        // 回购需要将成交明细表的成交编号登到交易汇总表的原始成交编号 fuyongsheng add 20211215 end
//                        settleTransDataList.add(dealTotal);
//                        //当笔成交数据明细.附属表流水号 非空
//                        if(StringUtil.isNotEmpty(tempData.getTradeIdAdd())){
//                            SettleTransDataTotalAdd settleTransDataTotalAdd = new SettleTransDataTotalAdd();
//                            BeanUtils.copyProperties(tempData, settleTransDataTotalAdd);
//                            settleTransDataTotalAdd.setTradeId(dealTotal.getSeqId());
//                            settleTransDataAddList.add(settleTransDataTotalAdd);
//                        }
//                    }
//                }
//            }else {
                //对账结果核对if 变量.temp_当前成交数据明细数据list 中所有数据都满足【对账结果 为空 或者为 对账一致 或者为 手工完成】
                boolean flag = this.getCheckResultFlag(list);
                if(flag){
                    //  汇总交易编号
                    dealTotal.setSeqId(IDGenerator.generate());
                    //执行日期
                    dealTotal.setDealDate(tradeDate);
                    //证券品种
                    dealTotal.setSecType(settleTransDataDto.getSecType());
                    // 金融产品代码
                    dealTotal.setFinprodId(settleTransDataDto.getFinprodId());
                    // 金融产品类型（估值核算）
                    dealTotal.setFinprodType(settleTransDataDto.getFinprodType());
                    // 金融产品类型（投管分类）
                    dealTotal.setFinprodType2(settleTransDataDto.getFinprodType2());
                    // 分支序号
                    dealTotal.setBranch(settleTransDataDto.getBranch());
                    // 业务类型
                    dealTotal.setBusiType(settleTransDataDto.getBusiType());
                    // 交易日
                    dealTotal.setTradeDate(settleTransDataDto.getTradeDate());
                    // 交割日
                    dealTotal.setSettleDate(settleTransDataDto.getSettleDate());
                    // 起息日
                    dealTotal.setVdate(settleTransDataDto.getVdate());
                    // 到期日
                    dealTotal.setMdate(settleTransDataDto.getMdate());
                    //首期交付日
                    dealTotal.setVpayDate(settleTransDataDto.getVpayDate());
                    //到期交付日
                    dealTotal.setMpayDate(settleTransDataDto.getMpayDate());
                    //期限品种
                    dealTotal.setTermType(settleTransDataDto.getTermType());
                    //清算速度
                    dealTotal.setSettleSpeed(settleTransDataDto.getSettleSpeed());
                    // 交易币种
                    dealTotal.setCcy(settleTransDataDto.getCcy());
                    // 交收结果
                    dealTotal.setDeliveryResult(settleTransDataDto.getDeliveryResult());
                    //单位净价
                    BigDecimal unitCprice=BigDecimal.ZERO;
                    //单位利息
                    BigDecimal unitInt=BigDecimal.ZERO;
                    //单位全价
                    BigDecimal unitFprice=BigDecimal.ZERO;
                    //净价总额
                    BigDecimal totalCpriceAmt=BigDecimal.ZERO;
                    //份额
                    BigDecimal totalShareAmt=BigDecimal.ZERO;
                    //利息总额
                    BigDecimal totalIntAmt=BigDecimal.ZERO;
                    //全价总额
                    BigDecimal totalFpriceamt=BigDecimal.ZERO;
                    //交易本金
                    BigDecimal prinAmt=BigDecimal.ZERO;
                    //总交易本金
                    BigDecimal totalPrinAmt=BigDecimal.ZERO;
                    //总印花税
                    BigDecimal totalStampFee=BigDecimal.ZERO;
                    //总经手费
                    BigDecimal totalJsfAmt=BigDecimal.ZERO;
                    //总过户费
                    BigDecimal totalGhfAmt=BigDecimal.ZERO;
                    //总证管费
                    BigDecimal totalZgfAmt=BigDecimal.ZERO;
                    //总手续费
                    BigDecimal totalSxfAmt=BigDecimal.ZERO;
                    //总结算费
                    BigDecimal totalSettleFee=BigDecimal.ZERO;
                    //佣金
                    BigDecimal totalCommissionAmt=BigDecimal.ZERO;
                    //总交易金额
                    BigDecimal totalTradeAmt=BigDecimal.ZERO;
                    //总到期本金
                    BigDecimal totalmPrinAmt=BigDecimal.ZERO;
                    //总到期利息
                    BigDecimal totalmIntAmt=BigDecimal.ZERO;
                    //总到期金额
                    BigDecimal totalmTradeAmt=BigDecimal.ZERO;
                    //总印花税_原始
                    BigDecimal totalStampFeeRaw=BigDecimal.ZERO;
                    //总过户费_原始
                    BigDecimal totalJsfAmtRaw=BigDecimal.ZERO;
                    //总证管费_原始
                    BigDecimal totalZgfAmtRaw=BigDecimal.ZERO;
                    //总经手费_原始
                    BigDecimal totalSxfAmtRaw=BigDecimal.ZERO;
                    //总结算费_原始
                    BigDecimal totalSettleFeeRaw=BigDecimal.ZERO;
                    //总佣金_原始
                    BigDecimal totalCommissionAmtRaw=BigDecimal.ZERO;
                    //交易金额_原始
                    BigDecimal totaltradeAmtRaw=BigDecimal.ZERO;
                    for (StlSettleTransDataGatherDto item : list) {
                        if(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(settleTransDataDto.getFinprodType2())
                                || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(settleTransDataDto.getFinprodType2())){
                            totalCpriceAmt = ArithmeticUtil.doAdd(totalCpriceAmt, ArithmeticUtil.multiply(item.getUnitCprice(),
                                    item.getShareAmt()));
                        }else{
                            totalCpriceAmt = ArithmeticUtil.doAdd(totalCpriceAmt, item.getCpriceAmt());
                        }
                        totalShareAmt = ArithmeticUtil.doAdd(totalShareAmt, item.getShareAmt());
                        totalIntAmt = ArithmeticUtil.doAdd(totalIntAmt, item.getIntAmt());
                        totalPrinAmt = ArithmeticUtil.doAdd(totalPrinAmt, item.getPrinAmt());
                        totalFpriceamt = ArithmeticUtil.doAdd(totalFpriceamt, item.getFpriceAmt());
                        totalStampFee= ArithmeticUtil.doAdd(totalStampFee, item.getStampFee());
                        totalJsfAmt= ArithmeticUtil.doAdd(totalJsfAmt, item.getJsfAmt());
                        totalGhfAmt= ArithmeticUtil.doAdd(totalGhfAmt, item.getGhfAmt());
                        totalZgfAmt= ArithmeticUtil.doAdd(totalZgfAmt, item.getZgfAmt());
                        totalSxfAmt= ArithmeticUtil.doAdd(totalSxfAmt, item.getSxfAmt());
                        totalSettleFee= ArithmeticUtil.doAdd(totalSettleFee, item.getSettleFee());
                        totalTradeAmt= ArithmeticUtil.doAdd(totalTradeAmt, item.getTradeAmt());
                        totalmPrinAmt= ArithmeticUtil.doAdd(totalmPrinAmt, item.getmPrinAmt());
                        totalmIntAmt= ArithmeticUtil.doAdd(totalmIntAmt, item.getmIntAmt());
                        totalmTradeAmt= ArithmeticUtil.doAdd(totalmTradeAmt, item.getmTradeAmt());
                        // ZGZGST-4156:计算佣金汇总 fuyongsheng 20211208 add start
                        totalCommissionAmt = ArithmeticUtil.doAdd(totalCommissionAmt, item.getCommissionAmt());
                        // ZGZGST-4156:计算佣金汇总 fuyongsheng 20211208 add end
                        totalStampFeeRaw=ArithmeticUtil.doAdd(totalStampFeeRaw, item.getStampFeeRaw());
                        totalJsfAmtRaw=ArithmeticUtil.doAdd(totalJsfAmtRaw, item.getJsfAmtRaw());
                        totalZgfAmtRaw=ArithmeticUtil.doAdd(totalZgfAmtRaw, item.getZgfAmtRaw());
                        totalSxfAmtRaw=ArithmeticUtil.doAdd(totalSxfAmtRaw, item.getSxfAmtRaw());
                        totalSettleFeeRaw=ArithmeticUtil.doAdd(totalSettleFeeRaw, item.getSettleFeeRaw());
                        // ZGZGST-4156:计算佣金汇总 fuyongsheng 20211208 modify start
                        // totalCommissionAmtRaw=ArithmeticUtil.doAdd(totalCommissionAmtRaw, item.getCommissionAmt());
                        totalCommissionAmtRaw=ArithmeticUtil.doAdd(totalCommissionAmtRaw, item.getCommissionAmtRaw());
                        // ZGZGST-4156:计算佣金汇总 fuyongsheng 20211208 modify end
                        totaltradeAmtRaw=ArithmeticUtil.doAdd(totaltradeAmtRaw, item.getTradeAmtRaw());
                    }
                    if(StringUtil.equals(settleTransDataDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                            && (StringUtil.equals(settleTransDataDto.getPs(), BussinessDictCodeList.交易方向_买入.getValue())
                            || StringUtil.equals(settleTransDataDto.getPs(), BussinessDictCodeList.交易方向_卖出.getValue())
                            ||StringUtil.equals(settleTransDataDto.getPs(), BussinessDictCodeList.交易方向_行权回售.getValue())
                            // GNS-879:配债UNIT_CPRIE,CPRICE_AMT未登录 modify by fuyongsheng 20220217 start
                            ||StringUtil.equals(settleTransDataDto.getPs(), DictCodeListExt3.交易方向_配债.getValue())
                            // GNS-879:配债UNIT_CPRIE,CPRICE_AMT未登录 modify by fuyongsheng 20220217 end
                    )){
                        if(StringUtil.equals(settleTransDataDto.getPs(), DictCodeListExt3.交易方向_配债.getValue())){
                            dealTotal.setFpriceAmt(totalFpriceamt);
                            dealTotal.setUnitFprice(settleTransDataDto.getUnitFprice());
                        } else{
                            unitCprice = ArithmeticUtil.divide(totalCpriceAmt, totalShareAmt, 8);
                            dealTotal.setCpriceAmt(totalCpriceAmt);
                            dealTotal.setUnitCprice(unitCprice);
                            unitInt=settleTransDataDto.getUnitInt();
                            dealTotal.setUnitInt(unitInt);
                            dealTotal.setUnitFprice(ArithmeticUtil.doAdd(unitCprice,unitInt));
                        }
                        prinAmt=totalCpriceAmt;
                    }
                    else if(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(settleTransDataDto.getFinprodType2())
                            || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(settleTransDataDto.getFinprodType2())){
                        dealTotal.setUnitCprice(ArithmeticUtil.divide(totalCpriceAmt, totalShareAmt, 4));
                        dealTotal.setUnitInt(null);
                        dealTotal.setUnitFprice(null);
                        prinAmt = totalPrinAmt;
                    }
                    else  if(StringUtil.equals(settleTransDataDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                            && (StringUtil.equals(settleTransDataDto.getPs(), BussinessDictCodeList.交易方向_申购.getValue()))){
                        dealTotal.setUnitCprice(new BigDecimal(100));
                        dealTotal.setUnitFprice(new BigDecimal(100));
                        dealTotal.setCpriceAmt(totalCpriceAmt);
                        prinAmt=totalCpriceAmt;
                    }else  if(StringUtil.equals(settleTransDataDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())
                            && (StringUtil.equals(settleTransDataDto.getPs(), DictCodeListExt2.交易方向_新股中签.getValue())
                            ||StringUtil.equals(settleTransDataDto.getPs(), DictCodeListExt2.交易方向_增发.getValue())
                            ||StringUtil.equals(settleTransDataDto.getPs(), DictCodeListExt2.交易方向_配股.getValue())
                            ||StringUtil.equals(settleTransDataDto.getPs(), BussinessDictCodeList.交易方向_买入.getValue())
                            ||StringUtil.equals(settleTransDataDto.getPs(), BussinessDictCodeList.交易方向_卖出.getValue()))
                            // GZYHTZJY-1016:成交明细汇总表未登录单位净价导致交易表金额为0 modify by fuyongsheng 20211231 start
                            ||StringUtil.equals(settleTransDataDto.getPs(), BussinessDictCodeList.交易方向_申购.getValue())
                            // GZYHTZJY-1016:成交明细汇总表未登录单位净价导致交易表金额为0 modify by fuyongsheng 20211231 end
                    ){
                        unitCprice = ArithmeticUtil.divide(totalFpriceamt, totalShareAmt, 8);
                        dealTotal.setUnitCprice(unitCprice);
                        // ZGTFCP-917:对页面成交价格进行赋值 modify by fuyongsheng 20220314 start
                        dealTotal.setUnitFprice(unitCprice);
                        // ZGTFCP-917:对页面成交价格进行赋值 modify by fuyongsheng 20220314 end
                        dealTotal.setCpriceAmt(totalFpriceamt);
                        prinAmt=totalFpriceamt;
                    }else  if(StringUtil.equals(settleTransDataDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())
                            && (StringUtil.equals(settleTransDataDto.getPs(), BussinessDictCodeList.交易方向_买入.getValue())
                            ||StringUtil.equals(settleTransDataDto.getPs(), BussinessDictCodeList.交易方向_卖出.getValue()))) {
                        unitCprice = ArithmeticUtil.divide(totalFpriceamt, totalShareAmt, 8);
                        dealTotal.setUnitCprice(unitCprice);
                        dealTotal.setUnitFprice(unitCprice);
                    }
                    //单位面值  空
                    // 交易份额
                    dealTotal.setShareAmt(totalShareAmt);
                    // 交易本金=净价总额
                    if(prinAmt.compareTo(BigDecimal.ZERO)!=0){
                        dealTotal.setPrinAmt(prinAmt);
                    }
                    // 利息总额
                    dealTotal.setIntAmt(totalIntAmt);
                    // 全价总额
                    dealTotal.setFpriceAmt(totalFpriceamt);
                    //印花税
                    dealTotal.setStampFee(totalStampFee);
                    //经手费
                    dealTotal.setJsfAmt(totalJsfAmt);
                    //过户费
                    dealTotal.setGhfAmt(totalGhfAmt);
                    //证管费
                    dealTotal.setZgfAmt(totalZgfAmt);
                    //手续费
                    dealTotal.setSxfAmt(totalSxfAmt);
                    //结算费
                    dealTotal.setJsfAmt(totalJsfAmt);
                    //佣金
                    dealTotal.setCommissionAmt(totalCommissionAmt);
                    //佣金是否随交易支付
                    dealTotal.setIsPayWithTrade(settleTransDataDto.getIsPayWithTrade());
                    // 交易金额
                    dealTotal.setTradeAmt(totalTradeAmt);
                    // 交易场所
                    dealTotal.setTradeMarket(settleTransDataDto.getTradeMarket());
                    //交易市场
                    dealTotal.setTradeMarketMode(settleTransDataDto.getTradeMarketMode());
                    //交易平台
                    dealTotal.setTradePlat(settleTransDataDto.getTradePlat());
                    //席位号
                    dealTotal.setSeatNumber(settleTransDataDto.getSeatNumber());
                    //组合代码
                    //证券账户
                    dealTotal.setSecAcctId(settleTransDataDto.getSecAcctId());
                    //资金账户
                    dealTotal.setAcctNo(settleTransDataDto.getAcctNo());
                    //批次号
                    dealTotal.setBatchNumber(settleTransDataDto.getBatchNumber());
                    //到期本金
                    dealTotal.setmPrinAmt(totalmPrinAmt);
                    //到期利息
                    dealTotal.setmIntAmt(totalmIntAmt);
                    //到期金额
                    dealTotal.setmIntAmt(totalmTradeAmt);
                    //利率 空
                    //计息基础
                    dealTotal.setBasis(settleTransDataDto.getBasis());
                    //业务场景分类
                    dealTotal.setBusiSceneCalss(settleTransDataDto.getBusiSceneCalss());
                    //交收方式
                    dealTotal.setSettleMode(settleTransDataDto.getSettleMode());
                    //印花税_原始
                    dealTotal.setStampFeeRaw(totalStampFeeRaw);
                    //过户费_原始
                    dealTotal.setJsfAmtRaw(totalJsfAmtRaw);
                    //证管费_原始
                    dealTotal.setZgfAmtRaw(totalZgfAmtRaw);
                    //经手费_原始
                    dealTotal.setSxfAmtRaw(totalSxfAmtRaw);
                    //结算费_原始
                    dealTotal.setSettleFeeRaw(totalSettleFeeRaw);
                    //佣金_原始
                    dealTotal.setCommissionAmtRaw(totalCommissionAmtRaw);
                    //交易金额_原始
                    dealTotal.setTradeAmtRaw(totaltradeAmtRaw);
                    //成交编号_原始
                    dealTotal.setTradeIdRaw(settleTransDataDto.getTradeIdRaw());
                    //临时表流水号
                    dealTotal.setSeqNo(settleTransDataDto.getSeqNo());
                    //是否优先配售
                    dealTotal.setIsPurchTypeYx(settleTransDataDto.getIsPurchTypeYx());
                    dealTotal.setApplyTradeId(settleTransDataDto.getApplyTradeId());
                    dealTotal.setPs(settleTransDataDto.getPs());
                    settleTransDataList.add(dealTotal);
                }
        });
        StlSettleTransDataGatherParamDto dataGatherParamDto = new StlSettleTransDataGatherParamDto(settleTransDataList,settleTransDataAddList,null);
        returnList.add(dataGatherParamDto);
        return returnList;
    }

    /**
     * 获取对账结果标识
     * @param list
     * @return
     */
    private boolean getCheckResultFlag(List<StlSettleTransDataGatherDto> list){
        boolean flag=true;
        //对账结果核对 当前成交数据明细数据list 中所有数据需都满足【对账结果 为空 或者为 对账一致 或者为 手工完成】
        for (StlSettleTransDataGatherDto item : list) {
            String checkResult=item.getCheckResult();
            if(!(StringUtil.isEmpty(checkResult)
                    || StringUtil.equals(checkResult, DictCodeListExt3.对账状态结果_一致.getValue())
                    || StringUtil.equals(checkResult, DictCodeListExt3.对账状态结果_手工完成.getValue())
            )){
                flag=false;
                break;
            }
        }
        return flag;
    }

    //成交明细数量.回购类型 = 数据字典.回购类型.质押式竞价回购的数据组合list，后续逻辑参照【详细处理-协议回购sheet】
    private List<StlSettleTransDataGatherParamDto> createAgreRepList(List<StlSettleTransDataGatherDto> selectNeedTotalDataList,Date tradeDate,List<StlSettleTransDataGatherParamDto> returnList) {

        //3、成交明细数量.回购类型 = 数据字典.回购类型.质押式竞价回购的数据组合list，后续逻辑参照【详细处理-协议回购sheet】
        Map<String,List<StlSettleTransDataGatherDto>> mapList=selectNeedTotalDataList
                .stream()
                .collect(Collectors.groupingBy(dto ->
                        dto.getApplyTradeId() + "_" + dto.getSeatNumber()+"_" + dto.getPs()));
        //4、循环当前循环的map的value
        List<SettleTransDataTotal> settleTransDataList=new ArrayList<>();
        List<SettleTransDataTotalAdd> settleTransDataAddList=new ArrayList<>();
        List<SettleTransDataTotalPledge> settleTransDataPledgeList=new ArrayList<>();
        mapList.forEach((key, list) -> {
            SettleTransDataTotal dealTotal=new SettleTransDataTotal();
            StlSettleTransDataGatherDto settleTransDataDto=list.get(0);
            if(list.size()==1){
                // 处理单条协议回购成交明细数据
                this.handleSingleH01SettleTransData(settleTransDataDto, settleTransDataList, settleTransDataAddList,
                        settleTransDataPledgeList);
            }else{
                //对账结果核对if 变量.temp_当前成交数据明细数据list 中所有数据都满足【对账结果 为空 或者为 对账一致 或者为 手工完成】
                boolean flag=true;
                for (StlSettleTransDataGatherDto item : list) {
                    String checkResult=item.getCheckResult();
                    if(!(StringUtil.isEmpty(checkResult)
                            || StringUtil.equals(checkResult, DictCodeListExt3.对账状态结果_一致.getValue())
                            || StringUtil.equals(checkResult, DictCodeListExt3.对账状态结果_手工完成.getValue())
                    )){
                        flag=false;
                        break;
                    }
                }
                if(flag) { //list 中所有数据都满足【对账结果 为空 或者为 对账一致 或者为 手工完成
                    for (StlSettleTransDataGatherDto item : list) { //核对交易方向  not in （中途换券， 解除质押）
                        String ps = item.getPs();
                        if (!(!StringUtil.equals(ps, DictCodeListExt3.交易方向_中途换券.getValue())
                                && !StringUtil.equals(ps, DictCodeListExt3.交易方向_解除质押.getValue())
                        )) {
                            flag = false;
                            break;
                        }
                    }
                    if(flag){ //交易方向  not in （中途换券， 解除质押）
                        //  汇总交易编号
                        dealTotal.setSeqId(IDGenerator.generate());
                        //执行日期
                        dealTotal.setDealDate(tradeDate);
                        //证券品种
                        dealTotal.setSecType(settleTransDataDto.getSecType());
                        //申请编号
                        dealTotal.setApplyTradeId(settleTransDataDto.getApplyTradeId());
                        // 金融产品代码
                        dealTotal.setFinprodId(settleTransDataDto.getFinprodId());
                        // 金融产品类型（估值核算）
                        dealTotal.setFinprodType(settleTransDataDto.getFinprodType());
                        // 金融产品类型（投管分类）
                        dealTotal.setFinprodType2(settleTransDataDto.getFinprodType2());
                        // 分支序号
                        dealTotal.setBranch(settleTransDataDto.getBranch());
                        // 业务类型
                        dealTotal.setBusiType(settleTransDataDto.getBusiType());
                        // 交易日
                        dealTotal.setTradeDate(settleTransDataDto.getTradeDate());
                        // 交割日
                        dealTotal.setSettleDate(settleTransDataDto.getSettleDate());
                        //清算速度
                        dealTotal.setSettleSpeed(settleTransDataDto.getSettleSpeed());
                        for (StlSettleTransDataGatherDto item : list) {
                            if(StringUtil.equals(item.getBusiSceneCalss(), DictCodeListStl.业务场景分类明细_债券协议正回购_首期_买入.getValue())
                                    ||StringUtil.equals(item.getBusiSceneCalss(), DictCodeListStl.业务场景分类明细_债券协议逆回购_首期_买入.getValue())){
                                // 起息日
                                dealTotal.setVdate(item.getVdate());
                                // 到期日
                                dealTotal.setMdate(item.getMdate());
                                //首期交付日
                                dealTotal.setVpayDate(item.getVpayDate());
                                //期限品种
                                dealTotal.setTermType(item.getTermType());
                                // 交易币种
                                dealTotal.setCcy(item.getCcy());
                                // 交易份额
                                dealTotal.setShareAmt(item.getShareAmt());
                                // 利息总额
                                dealTotal.setIntAmt(item.getIntAmt());
                                // 全价总额
                                dealTotal.setFpriceAmt(item.getFpriceAmt());
                                //印花税
                                dealTotal.setStampFee(item.getStampFee());
                                //经手费
                                dealTotal.setJsfAmt(item.getJsfAmt());
                                //过户费
                                dealTotal.setGhfAmt(item.getGhfAmt());
                                //证管费
                                dealTotal.setZgfAmt(item.getZgfAmt());
                                //手续费
                                dealTotal.setSxfAmt(item.getSxfAmt());
                                //结算费
                                dealTotal.setJsfAmt(item.getJsfAmt());
                                // 清算费
                                dealTotal.setSettleFee(item.getSettleFee());
                                //佣金
                                dealTotal.setCommissionAmt(item.getCommissionAmt());
                                //佣金是否随交易支付
                                dealTotal.setIsPayWithTrade(item.getIsPayWithTrade());
                                // 交易金额
                                dealTotal.setTradeAmt(item.getTradeAmt());
                                //利率
                                dealTotal.setIntRate(item.getIntRate());
                                //印花税_原始
                                dealTotal.setStampFeeRaw(item.getStampFeeRaw());
                                //过户费_原始
                                dealTotal.setJsfAmtRaw(item.getJsfAmtRaw());
                                //证管费_原始
                                dealTotal.setZgfAmtRaw(item.getZgfAmtRaw());
                                //经手费_原始
                                dealTotal.setSxfAmtRaw(item.getSxfAmtRaw());
                                //结算费_原始
                                dealTotal.setSettleFeeRaw(item.getSettleFeeRaw());
                                //佣金_原始
                                dealTotal.setCommissionAmtRaw(item.getCommissionAmtRaw());
                                //交易金额_原始
                                dealTotal.setTradeAmtRaw(item.getTradeAmtRaw());
                                //单位净价
                                // GZYHTZJY-1088:单位净价赋值  modify by fuyongsheng 20220111 start
                                dealTotal.setUnitCprice(item.getUnitCprice());
                                // GZYHTZJY-1088:单位净价赋值 modify by fuyongsheng 20220111 end
                                //本金金额
                                dealTotal.setPrinAmt(item.getPrinAmt());
                                // 首期成交编号
                                // modify by fuyongsheng 协议回购首期的成交明细数据只有一条,不存在list>1的情况 20220414 start
//                                if(!DictCodeList.交易方向_到期续做.getValue().equals(settleTransDataDto.getPs())){
//                                    dealTotal.setfCjbh(item.getCjbh());
//                                }
                                // modify by fuyongsheng 协议回购首期的成交明细数据只有一条,不存在list>1的情况 20220414 end
                            }
                            if(StringUtil.equals(item.getBusiSceneCalss(), DictCodeListStl.业务场景分类明细_债券协议正回购_到期.getValue())
                                    ||StringUtil.equals(item.getBusiSceneCalss(), DictCodeListStl.业务场景分类明细_债券协议逆回购_到期.getValue())
                                    || StringUtil.equals(item.getBusiSceneCalss(), DictCodeListStl.业务场景分类明细_债券协议逆回购_到期续作.getValue())
                                    || StringUtil.equals(item.getBusiSceneCalss(), DictCodeListStl.业务场景分类明细_债券协议正回购_到期续作.getValue())){
                                // 深交所的到期续作需特殊处理,需要查询首期的起息日
                                if(DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(item.getTradeMarket())
                                        && DictCodeList.交易方向_到期续做.getValue().equals(item.getPs())){
                                    // 查询首期
                                    List<SettleTransData> sqSettleTransDatas = stlSettleTransDataGatherService.selectSqSettleTransDatas(item);
                                    if(CollectionUtils.isNotEmpty(sqSettleTransDatas)){
                                        //到期交付日
                                        dealTotal.setMpayDate(sqSettleTransDatas.get(0).getVdate());
                                    }
                                }else{
                                    //到期交付日
                                    dealTotal.setMpayDate(item.getVdate());
                                }
                                //到期本金
                                dealTotal.setmPrinAmt(item.getPrinAmt());
                                //到期利息
                                dealTotal.setmIntAmt(item.getIntAmt());
                                //到期利息
                                dealTotal.setIntAmt(item.getIntAmt());
                                //到期金额
                                dealTotal.setmTradeAmt(item.getmTradeAmt());
                                //首期成交编号
                                dealTotal.setfCjbh(item.getfCjbh());
                            }
                            //当笔成交数据明细.质押品信息流水号 非空
                            if(StringUtil.isNotEmpty(item.getPledgeTradeId())){
                                SettleTransDataTotalPledge settleTransDataTotalPledge = new SettleTransDataTotalPledge();
                                settleTransDataTotalPledge.setTradeId(dealTotal.getSeqId());
                                settleTransDataTotalPledge.setBranch(item.getBranch());
                                settleTransDataTotalPledge.setPledgeFinprodId(item.getPledgeFinprodId());
                                // settleTransDataTotalPledge.setPledgeFaceValue(item.getPledgeFaceValue());
                                settleTransDataTotalPledge.setPledgeFaceValue(stlDataDealProcessCommonService
                                        .getUnitPrice(item.getPledgeFinprodId(), item.getTradeDate()));
                                settleTransDataTotalPledge.setPledgeNumber(item.getPledgeNumber());
                                settleTransDataTotalPledge.setPledgeRatio(item.getPledgeRatio());
                                settleTransDataTotalPledge.setPledgeAmt(item.getPledgeAmt());
                                settleTransDataTotalPledge.setDisplaceDirection(item.getDisplaceDirection());
                                settleTransDataPledgeList.add(settleTransDataTotalPledge);
                            }
                        }
                        //交易方向
                        dealTotal.setPs(settleTransDataDto.getPs());
                        // 交易场所
                        dealTotal.setTradeMarket(settleTransDataDto.getTradeMarket());
                        //交易市场
                        dealTotal.setTradeMarketMode(settleTransDataDto.getTradeMarketMode());
                        //交易平台
                        dealTotal.setTradePlat(settleTransDataDto.getTradePlat());
                        //押品只数
                        dealTotal.setPledgeSecNum(1L);
                        //席位号
                        dealTotal.setSeatNumber(settleTransDataDto.getSeatNumber());
                        //组合代码
                        //证券账户
                        dealTotal.setSecAcctId(settleTransDataDto.getSecAcctId());
                        //资金账户
                        dealTotal.setAcctNo(settleTransDataDto.getAcctNo());
                        //批次号
                        dealTotal.setBatchNumber(settleTransDataDto.getBatchNumber());
                        //计息基础
                        dealTotal.setBasis(settleTransDataDto.getBasis());
                        //业务场景分类
                        dealTotal.setBusiSceneCalss(settleTransDataDto.getBusiSceneCalss());
                        //交收方式
                        dealTotal.setSettleMode(settleTransDataDto.getSettleMode());
                        //成交编号_原始
                        dealTotal.setTradeIdRaw(settleTransDataDto.getTradeIdRaw());
                        //临时表流水号
                        dealTotal.setSeqNo(settleTransDataDto.getSeqNo());
                        //是否优先配售
                        dealTotal.setIsPurchTypeYx(settleTransDataDto.getIsPurchTypeYx());
                        //创建人
                        dealTotal.setCreateUser(LoginUserUtil.getUserName());
                        //创建部门
                        dealTotal.setCreateDept(LoginUserUtil.getDeptCode());
                        Date date= DateUtil.now();
                        //创建时间
                        dealTotal.setCreateTime(date);
                        //更新人
                        dealTotal.setUpdateUser(LoginUserUtil.getUserName());
                        //更新时间
                        dealTotal.setUpdateTime(date);
                        settleTransDataList.add(dealTotal);
                        //当笔成交数据明细.附属表流水号 非空
                        if(StringUtil.isNotEmpty(settleTransDataDto.getTradeIdAdd())){
                            SettleTransDataTotalAdd settleTransDataTotalAdd = new SettleTransDataTotalAdd();
                            settleTransDataTotalAdd.setTradeId(dealTotal.getSeqId());
                            settleTransDataTotalAdd.setRepoType(settleTransDataDto.getRepoType());
                            settleTransDataTotalAdd.setSeRepDayType(settleTransDataDto.getSeRepDayType());
                            settleTransDataAddList.add(settleTransDataTotalAdd);
                        }
                    }else{
                        flag=true;
                        for (StlSettleTransDataGatherDto item : list) { // 交易方向 in （中途换券， 解除质押）
                            String ps = item.getPs();
                            if (!(StringUtil.equals(ps, DictCodeListExt3.交易方向_中途换券.getValue())
                                    || StringUtil.equals(ps, DictCodeListExt3.交易方向_解除质押.getValue())
                            )) {
                                flag = false;
                                break;
                            }
                        }
                        if(flag){
                            //  汇总交易编号
                            dealTotal.setSeqId(IDGenerator.generate());
                            //执行日期
                            dealTotal.setDealDate(tradeDate);
                            //证券品种
                            dealTotal.setSecType(settleTransDataDto.getSecType());
                            //申请编号
                            dealTotal.setApplyTradeId(settleTransDataDto.getApplyTradeId());
                            // 金融产品代码
                            dealTotal.setFinprodId(settleTransDataDto.getFinprodId());
                            // 金融产品类型（估值核算）
                            dealTotal.setFinprodType(settleTransDataDto.getFinprodType());
                            // 金融产品类型（投管分类）
                            dealTotal.setFinprodType2(settleTransDataDto.getFinprodType2());
                            // 分支序号
                            dealTotal.setBranch(settleTransDataDto.getBranch());
                            // 业务类型
                            dealTotal.setBusiType(settleTransDataDto.getBusiType());
                            // 交易日
                            dealTotal.setTradeDate(settleTransDataDto.getTradeDate());
                            // 交割日
                            dealTotal.setSettleDate(settleTransDataDto.getSettleDate());
                            // 起息日
                            dealTotal.setVdate(settleTransDataDto.getVdate());
                            // 到期日
                            dealTotal.setMdate(settleTransDataDto.getMdate());
                            //首期交付日
                            dealTotal.setVpayDate(settleTransDataDto.getVpayDate());
                            //到期交付日
                            dealTotal.setMpayDate(settleTransDataDto.getMpayDate());
                            //期限品种
                            dealTotal.setTermType(settleTransDataDto.getTermType());
                            //清算速度
                            dealTotal.setSettleSpeed(settleTransDataDto.getSettleSpeed());
                            // 交易币种
                            dealTotal.setCcy(settleTransDataDto.getCcy());
                            //总印花税
                            BigDecimal totalStampFee=BigDecimal.ZERO;
                            //总经手费
                            BigDecimal totalJsfAmt=BigDecimal.ZERO;
                            //总过户费
                            BigDecimal totalGhfAmt=BigDecimal.ZERO;
                            //总证管费
                            BigDecimal totalZgfAmt=BigDecimal.ZERO;
                            //总手续费
                            BigDecimal totalSxfAmt=BigDecimal.ZERO;
                            //总结算费
                            BigDecimal totalSettleFee=BigDecimal.ZERO;
                            //佣金
                            BigDecimal totalCommissionAmt=BigDecimal.ZERO;
                            //总交易金额
                            BigDecimal totalTradeAmt=BigDecimal.ZERO;
                            //总到期本金
                            BigDecimal totalmPrinAmt=BigDecimal.ZERO;
                            //总到期利息
                            BigDecimal totalmIntAmt=BigDecimal.ZERO;
                            //总到期金额
                            BigDecimal totalmTradeAmt=BigDecimal.ZERO;
                            //总印花税_原始
                            BigDecimal totalStampFeeRaw=BigDecimal.ZERO;
                            //总过户费_原始
                            BigDecimal totalJsfAmtRaw=BigDecimal.ZERO;
                            //总证管费_原始
                            BigDecimal totalZgfAmtRaw=BigDecimal.ZERO;
                            //总经手费_原始
                            BigDecimal totalSxfAmtRaw=BigDecimal.ZERO;
                            //总结算费_原始
                            BigDecimal totalSettleFeeRaw=BigDecimal.ZERO;
                            //总佣金_原始
                            BigDecimal totalCommissionAmtRaw=BigDecimal.ZERO;
                            //交易金额_原始
                            BigDecimal totaltradeAmtRaw=BigDecimal.ZERO;
                            for (StlSettleTransDataGatherDto item : list) {
                                dealTotal.setfCjbh(item.getfCjbh());
                                if(DictCodeListExt3.质押券置换_置换方向_换入.getValue().equals(item.getDisplaceDirection())){
                                    dealTotal.setShareAmt(item.getShareAmt());
                                }
                                totalStampFee= ArithmeticUtil.doAdd(totalStampFee, item.getStampFee());
                                totalJsfAmt= ArithmeticUtil.doAdd(totalJsfAmt, item.getJsfAmt());
                                totalGhfAmt= ArithmeticUtil.doAdd(totalGhfAmt, item.getGhfAmt());
                                totalZgfAmt= ArithmeticUtil.doAdd(totalZgfAmt, item.getZgfAmt());
                                totalSxfAmt= ArithmeticUtil.doAdd(totalSxfAmt, item.getSxfAmt());
                                totalSettleFee= ArithmeticUtil.doAdd(totalSettleFee, item.getSettleFee());
                                totalTradeAmt= ArithmeticUtil.doAdd(totalTradeAmt, item.getTradeAmt());
                                totalmPrinAmt= ArithmeticUtil.doAdd(totalmPrinAmt, item.getmPrinAmt());
                                totalmIntAmt= ArithmeticUtil.doAdd(totalmIntAmt, item.getmIntAmt());
                                totalmTradeAmt= ArithmeticUtil.doAdd(totalmTradeAmt, item.getmTradeAmt());
                                totalStampFeeRaw=ArithmeticUtil.doAdd(totalStampFeeRaw, item.getStampFeeRaw());
                                totalJsfAmtRaw=ArithmeticUtil.doAdd(totalJsfAmtRaw, item.getJsfAmtRaw());
                                totalZgfAmtRaw=ArithmeticUtil.doAdd(totalZgfAmtRaw, item.getZgfAmtRaw());
                                totalSxfAmtRaw=ArithmeticUtil.doAdd(totalSxfAmtRaw, item.getSxfAmtRaw());
                                totalSettleFeeRaw=ArithmeticUtil.doAdd(totalSettleFeeRaw, item.getSettleFeeRaw());
                                totalCommissionAmtRaw=ArithmeticUtil.doAdd(totalCommissionAmtRaw, item.getCommissionAmt());
                                totaltradeAmtRaw=ArithmeticUtil.doAdd(totaltradeAmtRaw, item.getTradeAmtRaw());

                                //当笔成交数据明细.质押品信息流水号 非空
                                if(StringUtil.isNotEmpty(item.getPledgeTradeId())){
                                    SettleTransDataTotalPledge settleTransDataTotalPledge = new SettleTransDataTotalPledge();
                                    settleTransDataTotalPledge.setTradeId(dealTotal.getSeqId());
                                    settleTransDataTotalPledge.setBranch(item.getBranch());
                                    settleTransDataTotalPledge.setPledgeFinprodId(item.getPledgeFinprodId());
                                    // settleTransDataTotalPledge.setPledgeFaceValue(item.getPledgeFaceValue());
                                    settleTransDataTotalPledge.setPledgeFaceValue(stlDataDealProcessCommonService
                                            .getUnitPrice(item.getPledgeFinprodId(), item.getTradeDate()));
                                    settleTransDataTotalPledge.setPledgeNumber(item.getPledgeNumber());
                                    settleTransDataTotalPledge.setPledgeRatio(item.getPledgeRatio());
                                    settleTransDataTotalPledge.setPledgeAmt(item.getPledgeAmt());
                                    settleTransDataTotalPledge.setDisplaceDirection(item.getDisplaceDirection());
                                    settleTransDataPledgeList.add(settleTransDataTotalPledge);
                                }
                            }
                            //单位面值  空
                            // 交易份额 空
                            //交易本金
                            dealTotal.setPrinAmt(settleTransDataDto.getPrinAmt());
                            // 利息总额
                            dealTotal.setIntAmt(settleTransDataDto.getIntAmt());
                            // 全价总额
                            dealTotal.setFpriceAmt(settleTransDataDto.getFpriceAmt());
                            //印花税
                            dealTotal.setStampFee(totalStampFee);
                            //经手费
                            dealTotal.setJsfAmt(totalJsfAmt);
                            //过户费
                            dealTotal.setGhfAmt(totalGhfAmt);
                            //证管费
                            dealTotal.setZgfAmt(totalZgfAmt);
                            //手续费
                            dealTotal.setSxfAmt(totalSxfAmt);
                            //结算费
                            dealTotal.setJsfAmt(totalJsfAmt);
                            //佣金
                            dealTotal.setCommissionAmt(BigDecimal.ZERO);
                            //佣金是否随交易支付 空
                            // 交易金额 空
                            dealTotal.setPs(settleTransDataDto.getPs());
                            // 交易场所
                            dealTotal.setTradeMarket(settleTransDataDto.getTradeMarket());
                            //交易市场
                            dealTotal.setTradeMarketMode(settleTransDataDto.getTradeMarketMode());
                            //交易平台
                            dealTotal.setTradePlat(settleTransDataDto.getTradePlat());
                            //押品只数空
                            //席位号
                            dealTotal.setSeatNumber(settleTransDataDto.getSeatNumber());
                            //组合代码
                            //证券账户
                            dealTotal.setSecAcctId(settleTransDataDto.getSecAcctId());
                            //资金账户
                            dealTotal.setAcctNo(settleTransDataDto.getAcctNo());
                            //批次号
                            dealTotal.setBatchNumber(settleTransDataDto.getBatchNumber());
                            //到期本金 空
                            //到期利息 空
                            //到期金额 空
                            //利率 空
                            //计息基础 空
                            //业务场景分类 空
                            //交收方式
                            dealTotal.setSettleMode(settleTransDataDto.getSettleMode());
                            //印花税_原始
                            dealTotal.setStampFeeRaw(totalStampFeeRaw);
                            //过户费_原始
                            dealTotal.setJsfAmtRaw(totalJsfAmtRaw);
                            //证管费_原始
                            dealTotal.setZgfAmtRaw(totalZgfAmtRaw);
                            //经手费_原始
                            dealTotal.setSxfAmtRaw(totalSxfAmtRaw);
                            //结算费_原始
                            dealTotal.setSettleFeeRaw(totalSettleFeeRaw);
                            //佣金_原始
                            dealTotal.setCommissionAmtRaw(BigDecimal.ZERO);
                            //交易金额_原始 空
                            //成交编号_原始
                            dealTotal.setTradeIdRaw(settleTransDataDto.getTradeIdRaw());
                            //临时表流水号
                            dealTotal.setSeqNo(settleTransDataDto.getSeqNo());
                            //是否优先配售
                            dealTotal.setIsPurchTypeYx(settleTransDataDto.getIsPurchTypeYx());
                            //创建人
                            dealTotal.setCreateUser(LoginUserUtil.getUserName());
                            //创建部门
                            dealTotal.setCreateDept(LoginUserUtil.getDeptCode());
                            Date date= DateUtil.now();
                            //创建时间
                            dealTotal.setCreateTime(date);
                            //更新人
                            dealTotal.setUpdateUser(LoginUserUtil.getUserName());
                            //更新时间
                            dealTotal.setUpdateTime(date);
                            settleTransDataList.add(dealTotal);
                            //当笔成交数据明细.附属表流水号 非空
                            if(StringUtil.isNotEmpty(settleTransDataDto.getTradeIdAdd())){
                                SettleTransDataTotalAdd settleTransDataTotalAdd = new SettleTransDataTotalAdd();
                                settleTransDataTotalAdd.setTradeId(dealTotal.getSeqId());
                                settleTransDataTotalAdd.setRepoType(settleTransDataDto.getRepoType());
                                settleTransDataTotalAdd.setSeRepDayType(settleTransDataDto.getSeRepDayType());
                                settleTransDataAddList.add(settleTransDataTotalAdd);
                            }
                        }
                    }
                }
            }
        });
        StlSettleTransDataGatherParamDto dataGatherParamDto = new StlSettleTransDataGatherParamDto(settleTransDataList,settleTransDataAddList,settleTransDataPledgeList);
        returnList.add(dataGatherParamDto);
        return returnList;
    }

    /**
     * 处理单条协议回购成交明细数据
     * @param settleTransDataDto
     * @param settleTransDataList
     * @param settleTransDataAddList
     * @param settleTransDataPledgeList
     */
    private void handleSingleH01SettleTransData(StlSettleTransDataGatherDto settleTransDataDto,
                                                List<SettleTransDataTotal> settleTransDataList,
                                                List<SettleTransDataTotalAdd> settleTransDataAddList,
                                                List<SettleTransDataTotalPledge> settleTransDataPledgeList){
        String checkResult = settleTransDataDto.getCheckResult();
        if (StringUtil.isEmpty(checkResult)
                || StringUtil.equals(checkResult, DictCodeListExt3.对账状态结果_一致.getValue())
                || StringUtil.equals(checkResult, DictCodeListExt3.对账状态结果_手工完成.getValue())) {
            SettleTransDataTotal dealTotal = new SettleTransDataTotal();
            BeanUtils.copyProperties(settleTransDataDto, dealTotal);
            // GZYHTZJY-1100:深交所的到期续作到期交付日特殊处理 modify fuyongsheng 20220113 start
            if(DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(settleTransDataDto.getTradeMarket())
                    && DictCodeList.交易方向_到期续做.getValue().equals(settleTransDataDto.getPs())){
                // 查询首期
                List<SettleTransData> sqSettleTransDatas = stlSettleTransDataGatherService.selectSqSettleTransDatas(settleTransDataDto);
                if(CollectionUtils.isNotEmpty(sqSettleTransDatas)){
                    //到期交付日
                    dealTotal.setMpayDate(sqSettleTransDatas.get(0).getVdate());
                }
            }
            // GZYHTZJY-1100:深交所的到期续作到期交付日特殊处理 modify fuyongsheng 20220113 end
            //  汇总交易编号
            dealTotal.setSeqId(IDGenerator.generate());
            if(DictCodeList.交易方向_买入.getValue().equals(dealTotal.getPs())){
                // modify by fuyongsheng 协议回购首期的FCjbh存明细表的主键 start
                // dealTotal.setfCjbh(settleTransDataDto.getCjbh());
                dealTotal.setfCjbh(settleTransDataDto.getTradeId());
                // modify by fuyongsheng 协议回购首期的FCjbh存明细表的主键 end
            }
            settleTransDataList.add(dealTotal);
            //当笔成交数据明细.附属表流水号 非空
            if(StringUtil.isNotEmpty(settleTransDataDto.getTradeIdAdd())){
                SettleTransDataTotalAdd settleTransDataTotalAdd = new SettleTransDataTotalAdd();
                BeanUtils.copyProperties(settleTransDataDto, settleTransDataTotalAdd);
                settleTransDataTotalAdd.setTradeId(dealTotal.getSeqId());
                settleTransDataAddList.add(settleTransDataTotalAdd);
            }
            //当笔成交数据明细.质押品信息流水号 非空
            if(StringUtil.isNotEmpty(settleTransDataDto.getPledgeTradeId())){
                SettleTransDataTotalPledge settleTransDataTotalPledge = new SettleTransDataTotalPledge();
                settleTransDataTotalPledge.setTradeId(dealTotal.getSeqId());
                settleTransDataTotalPledge.setBranch(dealTotal.getBranch());
                settleTransDataTotalPledge.setPledgeFinprodId(settleTransDataDto.getPledgeFinprodId());
                // settleTransDataTotalPledge.setPledgeFaceValue(settleTransDataDto.getPledgeFaceValue());
                settleTransDataTotalPledge.setPledgeFaceValue(stlDataDealProcessCommonService
                        .getUnitPrice(settleTransDataDto.getPledgeFinprodId(), settleTransDataDto.getTradeDate()));
                settleTransDataTotalPledge.setPledgeNumber(settleTransDataDto.getPledgeNumber());
                settleTransDataTotalPledge.setPledgeRatio(settleTransDataDto.getPledgeRatio());
                settleTransDataTotalPledge.setPledgeAmt(settleTransDataDto.getPledgeAmt());
                settleTransDataTotalPledge.setDisplaceDirection(settleTransDataDto.getDisplaceDirection());
                settleTransDataPledgeList.add(settleTransDataTotalPledge);
            }
        }
    }

    //成交明细数量.交易方向 = 数据字典.交易方向.债转股的数据组合list，后续逻辑参照【详细处理-债转股】sheet
    private List<StlSettleTransDataGatherParamDto> createBondToStockList(List<StlSettleTransDataGatherDto> selectNeedTotalDataList,Date tradeDate,List<StlSettleTransDataGatherParamDto> returnList) {
        //3、成交明细数量.交易方向 = 数据字典.交易方向.债转股的数据组合list，后续逻辑参照【详细处理-债转股】sheet
        Map<String,List<StlSettleTransDataGatherDto>> mapList=selectNeedTotalDataList
                .stream()
                .collect(Collectors.groupingBy(dto ->
                        dto.getApplyTradeId() + "_" + dto.getSeatNumber()));
        //4、循环当前循环的map的value
        List<SettleTransDataTotal> settleTransDataList=new ArrayList<>();
        List<SettleTransDataTotalAdd> settleTransDataAddList=new ArrayList<>();
        List<SettleTransDataTotalPledge> settleTransDataPledgeList=new ArrayList<>();
        mapList.forEach((key, list) -> {
            SettleTransDataTotal dealTotal=new SettleTransDataTotal();
                //对账结果核对if 变量.temp_当前成交数据明细数据list 中所有数据都满足【对账结果 为空 或者为 对账一致 或者为 手工完成】
                boolean flag=true;
                for (StlSettleTransDataGatherDto item : list) {
                    String checkResult=item.getCheckResult();
                    if(!(StringUtil.isEmpty(checkResult)
                            || StringUtil.equals(checkResult, DictCodeListExt3.对账状态结果_一致.getValue())
                            || StringUtil.equals(checkResult, DictCodeListExt3.对账状态结果_手工完成.getValue())
                    )){
                        flag=false;
                        break;
                    }
                }
                if(flag){
                    //  汇总交易编号
                    dealTotal.setSeqId(IDGenerator.generate());
                    //执行日期
                    dealTotal.setDealDate(tradeDate);
                    for (StlSettleTransDataGatherDto item : list) {
                        if(StringUtil.equals(item.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
                            //证券品种
                            dealTotal.setSecType(item.getSecType());
                           //申请编号
                            dealTotal.setApplyTradeId(item.getApplyTradeId());
                            // 金融产品代码
                            dealTotal.setFinprodId(item.getFinprodId());
                            // 金融产品类型（估值核算）
                            dealTotal.setFinprodType(item.getFinprodType());
                            // 金融产品类型（投管分类）
                            dealTotal.setFinprodType2(item.getFinprodType2());
                            // 分支序号
                            dealTotal.setBranch(item.getBranch());
                            // 业务类型
                            dealTotal.setBusiType(item.getBusiType());
                            // 交易日
                            dealTotal.setTradeDate(item.getTradeDate());
                            // 交割日
                            dealTotal.setSettleDate(item.getSettleDate());
                            // 起息日
                            dealTotal.setVdate(item.getVdate());
                            // 到期日
                            dealTotal.setMdate(item.getMdate());
                            //首期交付日
                            dealTotal.setVpayDate(item.getVpayDate());
                            //到期交付日
                            dealTotal.setMpayDate(item.getMpayDate());
                            //期限品种
                            dealTotal.setTermType(item.getTermType());
                            //清算速度
                            dealTotal.setSettleSpeed(item.getSettleSpeed());
                            // 交易币种
                            dealTotal.setCcy(item.getCcy());
                            //单位面值
                            dealTotal.setParValue(item.getParValue());
                            // 交易份额
                            dealTotal.setShareAmt(item.getShareAmt());
                            // 交易本金
                            dealTotal.setPrinAmt(item.getPrinAmt());
                            // 利息总额
                            dealTotal.setIntAmt(item.getIntAmt());
                            // 交易场所
                            dealTotal.setTradeMarket(item.getTradeMarket());
                            //交易方向
                            dealTotal.setPs(item.getPs());
                            //交易市场
                            dealTotal.setTradeMarketMode(item.getTradeMarketMode());
                            //交易平台
                            dealTotal.setTradePlat(item.getTradePlat());
                            //席位号
                            dealTotal.setSeatNumber(item.getSeatNumber());
                            //组合代码
                            dealTotal.setPortfolioId(item.getPortfolioId());
                            //证券账户
                            dealTotal.setSecAcctId(item.getSecAcctId());
                            //资金账户
                            dealTotal.setAcctNo(item.getAcctNo());
                            //批次号
                            dealTotal.setBatchNumber(item.getBatchNumber());
                            //到期本金
                            dealTotal.setmPrinAmt(item.getmPrinAmt());
                            //到期利息
                            dealTotal.setmIntAmt(item.getmIntAmt());
                            //到期金额
                            dealTotal.setmTradeAmt(item.getmTradeAmt());
                            //利率
                            dealTotal.setmTradeAmt(item.getIntRate());
                            //计息基础
                            dealTotal.setBasis(item.getBasis());
                            //业务场景分类
                            dealTotal.setBusiSceneCalss(item.getBusiSceneCalss());
                            //交收方式
                            dealTotal.setSettleMode(item.getSettleMode());
                            //印花税_原始
                            dealTotal.setStampFeeRaw(item.getStampFeeRaw());
                            //过户费_原始
                            dealTotal.setJsfAmtRaw(item.getJsfAmtRaw());
                            //证管费_原始
                            dealTotal.setZgfAmtRaw(item.getZgfAmtRaw());
                            //经手费_原始
                            dealTotal.setSxfAmtRaw(item.getSxfAmtRaw());
                            //结算费_原始
                            dealTotal.setSettleFeeRaw(item.getSettleFeeRaw());
                            //佣金_原始
                            dealTotal.setCommissionAmtRaw(item.getCommissionAmtRaw());
                            //交易金额_原始
                            dealTotal.setTradeAmtRaw(item.getTradeAmtRaw());
                            //成交编号_原始
                            dealTotal.setTradeIdRaw(item.getTradeIdRaw());
                            //临时表流水号
                            dealTotal.setSeqNo(item.getSeqNo());
                            //是否优先配售
                            dealTotal.setIsPurchTypeYx(item.getIsPurchTypeYx());
                        }
                        if(StringUtil.equals(item.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_股票.getValue())){
                            // modify by etliu 20220228 start
                            // 广农商二阶段 GNS-920:【广农商场内清算】广农商场内债转股清算交易生成后，对应的持仓流水更新有误 上交所的零股现金未登录
//                            // 全价总额
//                            dealTotal.setFpriceAmt(item.getFpriceAmt());
                            // modify by etliu 20220228 end
                            //印花税
                            dealTotal.setStampFee(item.getStampFee());
                            //经手费
                            dealTotal.setJsfAmt(item.getJsfAmt());
                            //过户费
                            dealTotal.setGhfAmt(item.getGhfAmt());
                            //证管费
                            dealTotal.setZgfAmt(item.getZgfAmt());
                            //手续费
                            dealTotal.setSxfAmt(item.getSxfAmt());
                            //结算费
                            dealTotal.setJsfAmt(item.getJsfAmt());
                            //佣金
                            dealTotal.setCommissionAmt(item.getCommissionAmt());
                            //佣金是否随交易支付
                            dealTotal.setIsPayWithTrade(item.getIsPayWithTrade());

                            // modify by etliu 20220228 start
                            // 广农商二阶段 GNS-920:【广农商场内清算】广农商场内债转股清算交易生成后，对应的持仓流水更新有误 上交所的零股现金未登录
                            // 交易金额
//                            dealTotal.setTradeAmt(item.getTradeAmt());
//                            if(StringUtil.equals(item.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())
//                                    && item.getShareAmt().compareTo(BigDecimal.ZERO) == 0){
//                                dealTotal.setUnitCprice(item.getUnitCprice());
//                                if(dealTotal.getTradeAmt().compareTo(BigDecimal.ZERO) == 0){
//                                    dealTotal.setTradeAmt(item.getTradeAmt());
//                                }
//                            }
//                            if(StringUtil.equals(item.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
//                                dealTotal.setUnitCprice(item.getUnitCprice());
//                            }
                            if (StringUtil.equals(item.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                                if (item.getShareAmt().compareTo(BigDecimal.ZERO) == 0) {
                                    dealTotal.setUnitCprice(item.getUnitCprice());
                                    dealTotal.setTradeAmt(item.getTradeAmt());
                                    dealTotal.setFpriceAmt(item.getFpriceAmt());
                                }
                            }
                            else {
                                dealTotal.setUnitCprice(item.getUnitCprice());
                                dealTotal.setTradeAmt(item.getTradeAmt());
                                dealTotal.setFpriceAmt(item.getFpriceAmt());
                            }
                            // modify by etliu 20220228 end

                            dealTotal.setUnitFprice(dealTotal.getUnitCprice());
                        }
                    }
                    //创建人
                    dealTotal.setCreateUser(LoginUserUtil.getUserName());
                    //创建部门
                    dealTotal.setCreateDept(LoginUserUtil.getDeptCode());
                    Date date= DateUtil.now();
                    //创建时间
                    dealTotal.setCreateTime(date);
                    //更新人
                    dealTotal.setUpdateUser(LoginUserUtil.getUserName());
                    //更新时间
                    dealTotal.setUpdateTime(date);
                    settleTransDataList.add(dealTotal);
                    StlSettleTransDataGatherDto settleTransDataDto=list.get(0);
                    //当笔成交数据明细.附属表流水号 非空
                    if(StringUtil.isNotEmpty(settleTransDataDto.getTradeIdAdd())){
                        SettleTransDataTotalAdd settleTransDataTotalAdd = new SettleTransDataTotalAdd();
                        settleTransDataTotalAdd.setTradeId(dealTotal.getSeqId());
                        for(StlSettleTransDataGatherDto item:list) {
                            if(StringUtil.equals(settleTransDataDto.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())
                                    &&StringUtil.equals(item.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_股票.getValue())
                                    && item.getShareAmt().compareTo(BigDecimal.ZERO) == 0){
                                settleTransDataTotalAdd.setConPrice(item.getUnitCprice());
                            }
                            if(StringUtil.equals(settleTransDataDto.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())
                            && StringUtil.equals(item.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_股票.getValue())){
                                settleTransDataTotalAdd.setConPrice(item.getUnitCprice());
                            }
                        }
                        for(StlSettleTransDataGatherDto item:list) {
                            if(StringUtil.equals(item.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_股票.getValue())
                                    && item.getShareAmt().compareTo(BigDecimal.ZERO) > 0){
                                settleTransDataTotalAdd.setConAmt(item.getShareAmt());
                                settleTransDataTotalAdd.setrFinprodId(item.getFinprodId());
                            }
                        }
                        if(settleTransDataTotalAdd.getConAmt()!=null&&settleTransDataTotalAdd.getConPrice()!=null) {
                            settleTransDataTotalAdd.setConPrinAmt(settleTransDataTotalAdd.getConAmt().multiply(settleTransDataTotalAdd.getConPrice()));
                        }
                        settleTransDataAddList.add(settleTransDataTotalAdd);
                    }
                }

        });
        StlSettleTransDataGatherParamDto dataGatherParamDto = new StlSettleTransDataGatherParamDto(settleTransDataList,settleTransDataAddList,null);
        returnList.add(dataGatherParamDto);
        return returnList;
    }
}
