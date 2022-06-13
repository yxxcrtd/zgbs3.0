package com.joyintech.tams.settle.batch.datagather.processor.bond;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.settle.batch.datagather.constant.StlDataGatherConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.util.JoyinCollectors;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datagather.dao.StlDataGatherMapper;
import com.joyintech.tams.settle.batch.datagather.dto.InvestTradeReaderDto;
import com.joyintech.tams.settle.batch.datagather.dto.InvestTradeWriteDto;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.dto.TotalTrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.dto.TrdProductDealDto;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import com.joyintech.tams.settle.common.util.DateUtil;

/**
 * 债券存续期处理
 * @author zznn
 * @date 2021/11/1
 */
@Service
@Lazy
public class StlDataGatherBondDurationProcessor extends BaseProcessor<NeedOperateEntity, InvestTradeWriteDto> {

    @Autowired
    private StlDataGatherMapper stlDataGatherMapper;

    @Autowired
    private SettleCommonService settleCommonService;

    @Override
    public InvestTradeWriteDto process (NeedOperateEntity operateEntity) throws Exception {
        InvestTradeWriteDto investTradeWriteDto = new InvestTradeWriteDto();
        InvestTradeReaderDto item = (InvestTradeReaderDto) operateEntity;
        Date executeDate = this.getBatchExecuteInfo().getExecuteDate();
        // 根据InvestTradeReaderDto中的成交数据明细数据列表，构建一个InvestTradeWriteDto
        List<SettleTransDataDto> settleTransDataDtos = item.getSettleTransDataList();
        if (CollectionUtils.isNotEmpty(settleTransDataDtos)) {
            //汇总交易
            // List<TotalTrdProductDealDto> finProductDealTotalList = new ArrayList<>();
            //组合交易
            List<PtlTrade> ptlTradeList = new ArrayList<>();
            //投管交易关系
            List<SettleTransTgMapping>  settleTransTgMappingList = new ArrayList<>();
            //划款指令
            List<StlPayOrders> stlPayOrdersList = new ArrayList<>();
            //金融产品交易汇总表
            List<TotalTrdProductDealDto> totalProductDealList = new ArrayList<>();
            //金融产品交易明细
            List<TrdProductDealDto> productDealList = new ArrayList<>();
            //金融产品汇总附属交易
            List<TrdProductDealAdd> trdProductDealAddList = new ArrayList<>();
            //金融产品汇总附属交易
            List<TrdDealTotalAdd> trdDealTotalAddList = new ArrayList<>();
            //持仓分配
            Map<String,Object> param = new HashMap<>();
            // ZGZGST-4216: 查询持仓时日期使用付息截止日前一日 fuyongsheng 20211214 modify start
            // param.put("dealDate",executeDate);
            Date mdate2 = settleTransDataDtos.get(0).getMdate2();
            List<PtlSecPosition> ptlSecPositions = Lists.newArrayList();
            if(null != mdate2){
                param.put("dealDate", DateUtil.parse(DateUtil.getYesterDayDate(mdate2)));
                // ZGZGST-4216: 查询持仓时日期使用付息截止日前一日 fuyongsheng 20211214 modify end
                param.put("hodingType",DictCodeList.持仓类型_实际.getValue());
                // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 start
                // 新老头寸开关(Y:新头寸,N:老头寸)
                String newSecPositionSwitch = CacheUtil.getSysParam("NEW_SEC_POSITION_SWITCH");
                // 开关配置N查老头存
                if(DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)){
                    ptlSecPositions = stlDataGatherMapper.listPtlSecPosition(param);
                }else{ // 默认查新头寸
                    param.put("hodingType", DictCodeListExt3.资产类型_资产账户持仓.getValue());
                    param.put("dateType", StlDataGatherConstant.DATE_TYPE_T_0);
                    ptlSecPositions = stlDataGatherMapper.listPtlSecPositionRule(param);
                }
                // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 end
            }
            //按金融产品代码分组
            Map<String,List<PtlSecPosition> > finprodIdMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(ptlSecPositions)) {
                ptlSecPositions.stream().collect(Collectors.groupingBy(PtlSecPosition::getFinprodId, Collectors.toList())).forEach(finprodIdMap::put);
            }
            //按照证券代码，交易方向，证券账户代码，席位号汇总
            Map<String,List<SettleTransDataDto> > SettleTransDataMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(settleTransDataDtos)) {
                settleTransDataDtos.stream().collect(JoyinCollectors.groupingBy(i -> i.getSecType()+","
                       + i.getPs()+","+i.getSecAcctId()+","+i.getSeatNumber())).forEach(SettleTransDataMap::put);
            }
            for (SettleTransDataDto settleTransData : settleTransDataDtos) {
               // GNS-860:还原债券存续期 modify by fuyongsheng 20220214 start
               handTrdProductDeal(Arrays.asList(settleTransData));
               // GNS-860:还原债券存续期 modify by fuyongsheng 20220214 end
                //汇总交(资产维度)
                TotalTrdProductDealDto totalTrdProductDealDto = new TotalTrdProductDealDto();
                // ZGZGST-4216: 交易份额使用持仓汇总数量 fuyongsheng 20211214 modify start
                // totalTrdProductDealDto.setShareAmt(settleTransData.getShareAmt());
                totalTrdProductDealDto.setPs(settleTransData.getPs());
                if(finprodIdMap.containsKey(settleTransData.getFinprodId())){
                    List<PtlSecPosition> ptlSecPositionsByFinprodId = finprodIdMap.get(settleTransData.getFinprodId());
                    // ZGZGST-4246:过滤自己证券账户下的持仓 modify by fuyongsheng start
                    ptlSecPositionsByFinprodId.removeIf(position -> !settleTransData.getSecAcctId().equals(position.getSecAcctId()));
                    // ZGZGST-4246:过滤自己证券账户下的持仓 modify by fuyongsheng end
                    BigDecimal amountTotal = ptlSecPositionsByFinprodId.stream().map(PtlSecPosition::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    totalTrdProductDealDto.setShareAmt(amountTotal);
                }else{
                    totalTrdProductDealDto.setShareAmt(BigDecimal.ZERO);
                }
                // ZGZGST-4216: 交易份额使用持仓汇总数量 fuyongsheng 20211214 modify end
                totalTrdProductDealDto.setTradeAmt(settleTransData.getTradeAmt());
                totalTrdProductDealDto.setIntAmt(settleTransData.getIntAmt());
                totalTrdProductDealDto.setFpriceAmt(settleTransData.getFpriceAmt());
                totalTrdProductDealDto.setTradeAmt(settleTransData.getTradeAmt());

                // modify by etliu 20220227 start
                // 广农商二阶段 GNS-901：【广农商场内清算】场内债券存续期-涉及到还本的交易，“利息明细信息”中未登录还本金额
                totalTrdProductDealDto.setPrinAmt(settleTransData.getPrinAmt());
                totalTrdProductDealDto.setTheoryInt(totalTrdProductDealDto.getIntAmt());
                totalTrdProductDealDto.setTheoryprinAmt(totalTrdProductDealDto.getPrinAmt());
                // modify by etliu 20220227 end

                createTotalDeal(totalTrdProductDealDto,settleTransData);
                // finProductDealTotalList.add(totalTrdProductDealDto);
               totalProductDealList.add(totalTrdProductDealDto);
               //投管交易关系
                List<String> tradeIdList = settleTransData.getTradeIdList();
                for (String tradeId : tradeIdList) {
                    SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
                    settleTransTgMapping.setTradeId(tradeId);
                    settleTransTgMapping.setTradeIdTg(totalTrdProductDealDto.getTotleTradeId());
                    settleTransTgMapping.setTgDataStatus("02");
                    // add by fuyongsheng 对成交与投管关系执行时间赋值 fuyongsheng 20211217 start
                    settleTransTgMapping.setDealDate(getBatchExecuteInfo().getExecuteDate());
                    // add by fuyongsheng 对成交与投管关系执行时间赋值 fuyongsheng 20211217 end
                    settleTransTgMappingList.add(settleTransTgMapping);
                }
                //汇总交易(资产+组合维度)
                if (finprodIdMap.containsKey(settleTransData.getFinprodId())) {
                    List<PtlSecPosition> ptlSecPositionsByFinprodId = finprodIdMap.get(settleTransData.getFinprodId());
                    if(CollectionUtils.isNotEmpty(ptlSecPositionsByFinprodId)) {
                        BigDecimal amountTotal = ptlSecPositionsByFinprodId.stream().map(item2 -> item2.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        // 已分配利息总额
                        BigDecimal assignIntAmt = BigDecimal.ZERO;
                        for (PtlSecPosition ptlSecPosition : ptlSecPositionsByFinprodId) {
                            //汇总交易
                            TotalTrdProductDealDto totalTrdProductDealDtoDetaile = new TotalTrdProductDealDto();
                            totalTrdProductDealDtoDetaile.setpTotleTradeId(totalTrdProductDealDto.getTotleTradeId());
                            //交易份额
                            totalTrdProductDealDtoDetaile.setShareAmt(ptlSecPosition.getAmount());
                            //交易本金
                            totalTrdProductDealDtoDetaile.setPrinAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransData.getPrinAmt(), ptlSecPosition.getAmount()), amountTotal, 2));
                            //利息总额
                            totalTrdProductDealDtoDetaile.setIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransData.getIntAmt(),ptlSecPosition.getAmount()),amountTotal,2));
                            assignIntAmt = assignIntAmt.add(totalTrdProductDealDtoDetaile.getIntAmt());
                            //全价总额
                            totalTrdProductDealDtoDetaile.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransData.getFpriceAmt(), ptlSecPosition.getAmount()), amountTotal, 2));
                            //交易金额
                            totalTrdProductDealDtoDetaile.setTradeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransData.getTradeAmt(), ptlSecPosition.getAmount()), amountTotal, 2));
                            totalTrdProductDealDtoDetaile.setPortfolioId(ptlSecPosition.getPortfolioId());

                            // modify by etliu 20220227 start
                            // 广农商二阶段 GNS-901：【广农商场内清算】场内债券存续期-涉及到还本的交易，“利息明细信息”中未登录还本金额
                            totalTrdProductDealDtoDetaile.setTheoryInt(totalTrdProductDealDtoDetaile.getIntAmt());
                            totalTrdProductDealDtoDetaile.setTheoryprinAmt(totalTrdProductDealDtoDetaile.getPrinAmt());
                            // modify by etliu 20220227 end

                            // modify by etliu 20220227 start
                            // 广农商二阶段 GNS-975：【广农商场内清算】场内债券存续期-行权赎回交易生成时，处理相关头寸有误
                            totalTrdProductDealDtoDetaile.setInvAim(ptlSecPosition.getInvAim());
                            // modify by etliu 20220227 end

                            createTotalDeal(totalTrdProductDealDtoDetaile, settleTransData);
                            totalProductDealList.add(totalTrdProductDealDtoDetaile);
                            //划款指令
                            stlPayOrdersList.add(createStlPayOrders(totalTrdProductDealDtoDetaile, settleTransData.getAcctNo()));
                            //交易明细(还本、收息)
                            List<TrdProductDealDto> trdProductDealList = settleTransData.getTrdProductDealList();
                            for (TrdProductDealDto trdProductDealDto : trdProductDealList) {
                                TrdProductDealDto trdProductDeal = new TrdProductDealDto();
                                trdProductDeal.setPs(trdProductDealDto.getPs());
                                //交易份额
                                trdProductDeal.setShareAmt(ptlSecPosition.getAmount());
                                //交易本金
                                trdProductDeal.setPrinAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(trdProductDealDto.getPrinAmt(), ptlSecPosition.getAmount()), amountTotal, 2));
                                //利息总额
                                trdProductDeal.setIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(trdProductDealDto.getIntAmt(), ptlSecPosition.getAmount()), amountTotal, 2));
                                //全价总额
                                trdProductDeal.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(trdProductDealDto.getFpriceAmt(), ptlSecPosition.getAmount()), amountTotal, 2));
                                //交易金额
                                trdProductDeal.setTradeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(trdProductDealDto.getTradeAmt(), ptlSecPosition.getAmount()), amountTotal, 2));
                                trdProductDeal.setTotleTradeId(totalTrdProductDealDtoDetaile.getTotleTradeId());
                                createDeal(trdProductDeal, settleTransData);
                                trdProductDeal.setPortfolioId(ptlSecPosition.getPortfolioId());

                                // modify by etliu 20220227 start
                                // 广农商二阶段 GNS-975：【广农商场内清算】场内债券存续期-行权赎回交易生成时，处理相关头寸有误
                                trdProductDeal.setInvAim(ptlSecPosition.getInvAim());
                                // modify by etliu 20220227 end

                                TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
                                trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
                                trdProductDealAdd.setPortfolioId(ptlSecPosition.getPortfolioId());
                                productDealList.add(trdProductDeal);
                                //组合交易
                                PtlTrade ptlTrade = new PtlTrade();
                                ptlTrade.setPortfolioId(ptlSecPosition.getPortfolioId());
                                ptlTradeList.add(createPtlTrade(ptlTrade, trdProductDeal));
                                trdProductDealAddList.add(trdProductDealAdd);
                            }
                        }
                        // 分配利息差额(利息差额 = 利息总额 - 已分配利息总额)
                        BigDecimal intAmtCe = settleTransData.getIntAmt().subtract(assignIntAmt);
                        if(intAmtCe.compareTo(BigDecimal.ZERO) != 0){
                            // 最大一笔持仓数量
                            BigDecimal maxAmout = ptlSecPositionsByFinprodId.stream().max(Comparator.comparing(
                                    PtlSecPosition::getAmount)).get().getAmount();

                            // modify by etliu 20220227 start
                            // 利息分配尾差处理，其他字段也要处理
                            Map<String, TrdProductDeal> trdProductDealMap = productDealList.stream().collect(JoyinCollectors.toMap(x -> x.getTotleTradeId(), x -> x));
                            Map<String, StlPayOrders> stlPayOrdersMap = stlPayOrdersList.stream().collect(JoyinCollectors.toMap(x -> x.getTradeId(), x -> x));
                            // modify by etliu 20220227 end

                            // 将利息差额补到(资产+组合维度)最大持仓数量的数据上
                            for(TotalTrdProductDealDto finProductDealTotal : totalProductDealList){
                                // 过滤汇总交(资产维度)数据
                                if(StringUtil.isEmpty(finProductDealTotal.getpTotleTradeId())){
                                    continue;
                                }
                                // 如果当前数据持仓数量是最大持仓数量,则将利息差额补到该条数据上,跳出循环
                                if(finProductDealTotal.getShareAmt().equals(maxAmout)){
                                    finProductDealTotal.setIntAmt(finProductDealTotal.getIntAmt().add(intAmtCe));

                                    // modify by etliu 20220227 start
                                    // 利息分配尾差处理，其他字段也要处理
                                    finProductDealTotal.setTheoryInt(finProductDealTotal.getIntAmt());
                                    finProductDealTotal.setFpriceAmt(ArithmeticUtil.doAdd(finProductDealTotal.getFpriceAmt(), intAmtCe));
                                    finProductDealTotal.setTradeAmt(ArithmeticUtil.doAdd(finProductDealTotal.getTradeAmt(), intAmtCe));

                                    if (trdProductDealMap.containsKey(finProductDealTotal.getTotleTradeId())) {
                                        TrdProductDeal tempTrdProductDeal = trdProductDealMap.get(finProductDealTotal.getTotleTradeId());
                                        tempTrdProductDeal.setIntAmt(ArithmeticUtil.doAdd(tempTrdProductDeal.getIntAmt(), intAmtCe));
                                        tempTrdProductDeal.setFpriceAmt(ArithmeticUtil.doAdd(tempTrdProductDeal.getFpriceAmt(), intAmtCe));
                                        tempTrdProductDeal.setTradeAmt(ArithmeticUtil.doAdd(tempTrdProductDeal.getTradeAmt(), intAmtCe));
                                    }
                                    if (stlPayOrdersMap.containsKey(finProductDealTotal.getTotleTradeId())) {
                                        StlPayOrders stlPayOrders = stlPayOrdersMap.get(finProductDealTotal.getTotleTradeId());
                                        stlPayOrders.setSettleAmt(ArithmeticUtil.doAdd(stlPayOrders.getSettleAmt(), intAmtCe));
                                    }
                                    // modify by etliu 20220227 end

                                    break;
                                }
                            }
                        }
                    }
                }
            }
            // GNS-869: 删除金融产品汇总交易表重复赋值 fuyongsheng 20220216 start
            // investTradeWriteDto.setFinProductDealTotalList(finProductDealTotalList);
            // GNS-869: 删除金融产品汇总交易表重复赋值 fuyongsheng 20220216 end
            investTradeWriteDto.setPtlTradeList(ptlTradeList);
            investTradeWriteDto.setStlPayOrdersList(stlPayOrdersList);
            investTradeWriteDto.setProductDealList(productDealList);
            investTradeWriteDto.setFinProductDealTotalList(totalProductDealList);
            investTradeWriteDto.setTrdProductDealAddList(trdProductDealAddList);
            investTradeWriteDto.setTrdDealTotalAddList(trdDealTotalAddList);
            // ZGZGST-4219: 对SettleTransTgMappingList赋值,解决撤销问题 fuyongsheng 20211217 add start
            investTradeWriteDto.setSettleTransTgMappingList(settleTransTgMappingList);
            // ZGZGST-4219: 对SettleTransTgMappingList赋值,解决撤销问题 fuyongsheng 20211217 add end
            investTradeWriteDto.setSecType(DictCodeListExt1.证券品种_债券.getValue());
        }
        return investTradeWriteDto;
    }

    /**
     * 构建汇总交易
     * @param dealTotal
     * @param settleTransData
     */
    private void createTotalDeal(TotalTrdProductDealDto dealTotal,SettleTransDataDto settleTransData){
        String totalTradeId = settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易);
        dealTotal.setTotleTradeId(totalTradeId);
        // 业务类型
        // ZGZGST-4231,ZGZGST-4232:业务类型为债券资产存续期 fuyongsheng 20211217 modify start
        // dealTotal.setBusiType(DictCodeList.共通业务类型_资产存续期交易.getValue());
        dealTotal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        // ZGZGST-4231,ZGZGST-4232:业务类型为债券资产存续期 fuyongsheng 20211217 modify end
        // 金融产品代码
        dealTotal.setFinprodId(settleTransData.getFinprodId());
        // 金融产品类型（投管分类）
        dealTotal.setFinprodType2(settleTransData.getFinprodType2());
        // 分支序号
        dealTotal.setBranch(0L);
        // 交易日
        dealTotal.setTradeDate(settleTransData.getTradeDate());
        // 起息日
        dealTotal.setVdate(settleTransData.getVdate());
        // 到期日
        dealTotal.setMdate(settleTransData.getMdate2());
        // 交割日
        dealTotal.setSettleDate(settleTransData.getSettleDate());
        // 交易币种
        dealTotal.setCcy(settleTransData.getCcy());
        // 单位净价
        dealTotal.setUnitCprice(settleTransData.getUnitCprice());
        // 单位利息
        dealTotal.setUnitInt(settleTransData.getUnitInt());
        // 单位全价
        dealTotal.setUnitFprice(settleTransData.getUnitFprice());
        // 单位面值
        dealTotal.setParValue(settleTransData.getParValue());
        // 净价总额
        dealTotal.setCpriceAmt(settleTransData.getCpriceAmt());
        // 交易份额
        //dealTotal.setShareAmt(settleTransData.getShareAmt());
        // 交易本金
        //dealTotal.setTradeAmt(settleTransData.getTradeAmt());
        // 利息总额
        //dealTotal.setIntAmt(settleTransData.getIntAmt());
        // 全价总额
        //dealTotal.setFpriceAmt(settleTransData.getFpriceAmt());
        // 交易金额
        //dealTotal.setTradeAmt(settleTransData.getTradeAmt());
        // 交收方式
        dealTotal.setSettlemenType(settleTransData.getSettleMode());
        // 清算速度
        dealTotal.setSettleSpeed(settleTransData.getSettleSpeed());
        // 交易方向
        dealTotal.setPs(settleTransData.getPs());
        // 投资目的
        dealTotal.setInvAim(settleTransData.getInvAim());
        // 交易状态
        dealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 是否已撤销
        dealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        //是否结清
        dealTotal.setIsClean(DictCodeList.是否_是.getValue());
        // 证券管理户代码
        dealTotal.setSecManageAcctId(settleTransData.getSecAcctId());
        //是否外部交易
        dealTotal.setIsOutTrade(DictCodeList.是否_是.getValue());
        //是否逾期
        dealTotal.setIsOverdueDeal(DictCodeList.是否_否.getValue());
        // 交易场所
        dealTotal.setTradeMarket(settleTransData.getTradeMarket());
        // 交易平台
        dealTotal.setTradePlat(settleTransData.getTradePlat());
        // 交易市场
        dealTotal.setTradeMarketMode(settleTransData.getTradeMarketMode());
        // 投资组合代码
        //dealTotal.setPortfolioId(settleTransData.getPortfolioId());
        // 投管业务类型
        dealTotal.setTgBusiType(settleTransData.getBusiType());
        // 席位号
        dealTotal.setSeatId(settleTransData.getSeatNumber());
        // 利率
        dealTotal.setRate(null);
        // 回购期限(天)
        dealTotal.setRepoTerm(null);
        // 期限品种
        dealTotal.setTermType(null);
        // 委托代码
        dealTotal.setEntrustId(settleTransData.getEntrustId());
    }

    private PtlTrade createPtlTrade(PtlTrade ptlTrade,TrdProductDealDto trdProductDealDto) {
            // 组合代码
            //ptlTrade.setPortfolioId(trdProductDealDto.getPortfolioId());
            // 业务编号
            ptlTrade.setBusiNo(trdProductDealDto.getTradeId());
            // 业务表
            ptlTrade.setBusiTable(DictCodeListStl.业务表_金融产品交易表.getValue());
            // 业务明细代码
            ptlTrade.setBusiId(trdProductDealDto.getFinprodId());
            // 投资目的
            ptlTrade.setInvAim(trdProductDealDto.getInvAim());
            // 证券托管户代码
            ptlTrade.setSecManageAcctId(trdProductDealDto.getSecManageAcctId());
            // 实际发生日
            ptlTrade.setSettleDate(trdProductDealDto.getSettleDate());
            // 业务类型
            ptlTrade.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
            return ptlTrade;
    }

    /**
     * 构建交易明细
     * @param trdProductDeal
     * @param settleTransData
     */
    private void createDeal(TrdProductDealDto trdProductDeal, SettleTransDataDto settleTransData){
        trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易));
        // 业务类型
        // 业务类型为债券资产存续期 fuyongsheng 20211217 modify start
        // trdProductDeal.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        // ZGZGST-4231,ZGZGST-4232:业务类型为债券资产存续期 fuyongsheng 20211217 modify end
        // 金融产品代码
        trdProductDeal.setFinprodId(settleTransData.getFinprodId());
        // 金融产品类型（投管分类）
        trdProductDeal.setFinprodType2(settleTransData.getFinprodType2());
        // 金融产品类型
        trdProductDeal.setFinprodType(settleTransData.getFinprodType());
        // 分支序号
        trdProductDeal.setBranch(0L);
        // 交易日
        trdProductDeal.setTradeDate(settleTransData.getTradeDate());
        // ZGZGST-4231:赋值起息日和到期日 fuyongsheng modify start
        // 起息日
        // trdProductDeal.setVdate(null);
        trdProductDeal.setVdate(settleTransData.getVdate());
        // 到期日
        // trdProductDeal.setMdate(null);
        trdProductDeal.setMdate(settleTransData.getMdate2());
        // ZGZGST-4231:赋值起息日和到期日 fuyongsheng modify end
        // 交付日
        trdProductDeal.setPayDate(settleTransData.getVpayDate());
        // 交割日
        trdProductDeal.setSettleDate(settleTransData.getSettleDate());
        // 交易币种
        trdProductDeal.setCcy(settleTransData.getCcy());
        // 净值日期
        trdProductDeal.setCpriceDate(null);
        // 投资目的
        trdProductDeal.setInvAim(settleTransData.getInvAim());
        // 交易状态
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 是否已撤销
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        // 原交易编号
        trdProductDeal.setpTradeId(null);
        // 是否结清
        trdProductDeal.setIsClean(null);
        // 汇总交易编号（父类中设置）
        // 反向交易编号
        trdProductDeal.setrTradeId(null);
        // 通道代码
        trdProductDeal.setChlId(null);
        // 交易对手
        trdProductDeal.setCounterId(null);
        // 是否外部交易
        trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 交易场所
        trdProductDeal.setTradeMarket(settleTransData.getTradeMarket());
        // 交易平台
        trdProductDeal.setTradePlat(settleTransData.getTradePlat());
        // 交易市场
        trdProductDeal.setTradeMarketMode(settleTransData.getTradeMarketMode());
        // 本方交易员
        trdProductDeal.setTrader(null);
        // 对手方交易员
        trdProductDeal.setCounterTrader(null);
        // 对手方联系方式
        trdProductDeal.setCounterContact(null);
        // 证券管理户代码
        trdProductDeal.setSecManageAcctId(settleTransData.getSecAcctId());
        // 关联系统交易编号
        trdProductDeal.setfTradeId(null);
        // 现金流代码
        trdProductDeal.setCashId(null);
        // 登记托管机构
        trdProductDeal.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
        // 备注
        trdProductDeal.setRemark(null);
        // 撤销说明
        trdProductDeal.setCancelRemark(null);
        // 销售代码
        trdProductDeal.setSaleCode(null);
        // 计提份额
        trdProductDeal.setAccrNum(null);
        // 交收方式
        trdProductDeal.setSettlemenType(settleTransData.getSettleMode());
        // 席位号
        trdProductDeal.setSeatId(settleTransData.getSeatNumber());
        // 投资组合代码
        trdProductDeal.setPortfolioId(settleTransData.getPortfolioId());
        // 委托代码
        trdProductDeal.setEntrustId(settleTransData.getOrderId());
        // 成交编号（金额计算时已设置）
        // 权益登记日
        trdProductDeal.setRegDate(null);
        // 权益登记日份额
        trdProductDeal.setRegDateAmt(null);
        // 是否优先配售
        trdProductDeal.setIsPurchTypeYx(settleTransData.getIsPurchTypeYx());
        // 委托代码
        trdProductDeal.setEntrustId(settleTransData.getOrderId());
        // 资金账户
        trdProductDeal.setAcctNo(settleTransData.getAcctNo());
        trdProductDeal.setSettleSpeed(settleTransData.getSettleSpeed());
        /*//交易本金
        trdProductDeal.setPrinAmt(settleTransData.getPrinAmt());
        //利息总额
        trdProductDeal.setIntAmt(settleTransData.getIntAmt());
        //全价总额
        trdProductDeal.setFpriceAmt(settleTransData.getFpriceAmt());
        //交易金额
        trdProductDeal.setTradeAmt(settleTransData.getTradeAmt());*/
        //净价总额
        trdProductDeal.setCpriceAmt(ArithmeticUtil.subtract(settleTransData.getFpriceAmt(),settleTransData.getIntAmt()));
        // 单位净价
        trdProductDeal.setUnitCprice(settleTransData.getUnitCprice());
        // 单位利息
        trdProductDeal.setUnitInt(settleTransData.getUnitInt());
        // 单位全价
        trdProductDeal.setUnitFprice(settleTransData.getUnitFprice());
        // 交易方向
        // ZGZGST-4219:交易方向已在外层处理 fuyongsheng 20211216 modify start
        // 交易方向
        // trdProductDeal.setPs(settleTransData.getPs());
        // ZGZGST-4219:交易方向已在外层处理 fuyongsheng 20211216 modify end
        // 结算方式 券款兑付
        trdProductDeal.setSettMode("03");
        if(StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_收息到账.getValue())){
            trdProductDeal.setTgBusiType( DictCodeListExt1.平安业务类型_债券中间收息.getValue());
        }else{
            trdProductDeal.setTgBusiType( DictCodeListExt1.平安业务类型_债券中间还本.getValue());
        }
    }


    /**
     * 划款指令
     * @param total
     * @param acctNo
     * @return
     */
    private  StlPayOrders createStlPayOrders(TrdFinProductDealTotal total,String acctNo) {
        // 指令
        StlPayOrders stlPayOrders = new StlPayOrders();
        // 指令编号
        stlPayOrders.setOrderId(settleCommonService.buildBussinessIdBySeqno("9998"));
        // 指令类型
        stlPayOrders.setOrderType(null);
        // 结算日期
        stlPayOrders.setSettleDate(total.getSettleDate());
        // 本方资金账户
        stlPayOrders.setMyAcctNo(acctNo);
        // 结算金额
        stlPayOrders.setSettleAmt(total.getTradeAmt());
        // 币种
        stlPayOrders.setCcy(total.getCcy());
        // 是否需要发送
        stlPayOrders.setIsNeedSend(DictCodeList.是否_否.getValue());
        // 是否汇总交易
        stlPayOrders.setIsTotalTrade(DictCodeList.是否_是.getValue());
        // 交易编号
        stlPayOrders.setTradeId(total.getTotleTradeId());
        // 指令状态
        stlPayOrders.setOrderType(DictCodeListExt1.交易状态_初始.getValue());
        // 投资组合代码
        stlPayOrders.setPortfolioId(total.getPortfolioId());
        // 备注
        stlPayOrders.setRemark("数据清算自动处理");
        // 划款类型
        stlPayOrders.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
        return stlPayOrders;
    }

    /**
     * 处理还本付息
     * @param settleTransDataDtos
     */
    private void handTrdProductDeal(List<SettleTransDataDto> settleTransDataDtos){
        for (SettleTransDataDto settleTransDataDto : settleTransDataDtos) {
            //还本收息
            List<TrdProductDealDto> trdProductDeals = new ArrayList<>();
            String ps = settleTransDataDto.getPs();
            if(StringUtil.equals(ps, DictCodeList.交易方向_行权_赎回.getValue())){
                settleTransDataDto.setBusiType(DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
                // GNS-896:生成行权交易数据 modify by fuyongsheng 20220217 start
                TrdProductDealDto trdProductDealDto = new TrdProductDealDto();
                trdProductDealDto.setPs(DictCodeList.交易方向_行权_赎回.getValue());
                trdProductDealDto.setUnitInt(settleTransDataDto.getUnitInt());
                trdProductDealDto.setShareAmt(settleTransDataDto.getShareAmt());
                trdProductDealDto.setIntAmt(settleTransDataDto.getIntAmt());
                trdProductDealDto.setFpriceAmt(settleTransDataDto.getFpriceAmt());
                trdProductDealDto.setTradeAmt(settleTransDataDto.getTradeAmt());
                trdProductDeals.add(trdProductDealDto);
                // GNS-896:生成行权交易数据 modify by fuyongsheng 20220217 end
                settleTransDataDto.setTrdProductDealList(trdProductDeals);
                continue;
            }
            if(StringUtil.equals(ps,DictCodeList.交易方向_还本.getValue())||StringUtil.equals(ps,DictCodeList.交易方向_到期.getValue())){
                settleTransDataDto.setBusiType(StringUtil.equals(ps,DictCodeList.交易方向_到期.getValue())?DictCodeListExt1.平安业务类型_债券到期.getValue():DictCodeListExt1.平安业务类型_债券中间还本.getValue());
                if(null!=settleTransDataDto.getIntAmt()&&settleTransDataDto.getIntAmt().compareTo(BigDecimal.ZERO)!=0){
                    TrdProductDealDto trdProductDealDto = new TrdProductDealDto();
                    BigDecimal intAmt = settleTransDataDto.getIntAmt();
                    trdProductDealDto.setPs(DictCodeList.交易方向_收息到账.getValue());
                    trdProductDealDto.setParValue(settleTransDataDto.getParValue());
                    trdProductDealDto.setUnitInt(settleTransDataDto.getUnitInt());
                    trdProductDealDto.setShareAmt(settleTransDataDto.getShareAmt());
                    trdProductDealDto.setIntAmt(intAmt);
                    trdProductDealDto.setFpriceAmt(intAmt);
                    trdProductDealDto.setTradeAmt(intAmt);
                    trdProductDeals.add(trdProductDealDto);
                }
                if(null!=settleTransDataDto.getPrinAmt()&&settleTransDataDto.getPrinAmt().compareTo(BigDecimal.ZERO)!=0){
                    TrdProductDealDto trdProductDealDto = new TrdProductDealDto();
                    BigDecimal prinAmt = settleTransDataDto.getPrinAmt();
                    trdProductDealDto.setPs(DictCodeList.交易方向_还本到账.getValue());
                    trdProductDealDto.setParValue(settleTransDataDto.getParValue());
                    trdProductDealDto.setUnitInt(settleTransDataDto.getUnitInt());
                    trdProductDealDto.setShareAmt(settleTransDataDto.getShareAmt());
                    trdProductDealDto.setIntAmt(BigDecimal.ZERO);
                    trdProductDealDto.setFpriceAmt(BigDecimal.ZERO);
                    trdProductDealDto.setPrinAmt(prinAmt);
                    trdProductDealDto.setTradeAmt(prinAmt);
                    trdProductDeals.add(trdProductDealDto);
                }
                settleTransDataDto.setTrdProductDealList(trdProductDeals);
                continue;
            }
            if(StringUtil.equals(ps, DictCodeList.交易方向_收息.getValue())){
                settleTransDataDto.setBusiType(DictCodeListExt1.平安业务类型_债券中间收息.getValue());
                TrdProductDealDto trdProductDealDto = new TrdProductDealDto();
                BigDecimal intAmt = settleTransDataDto.getIntAmt();
                trdProductDealDto.setPs(DictCodeList.交易方向_收息到账.getValue());
                trdProductDealDto.setParValue(settleTransDataDto.getParValue());
                trdProductDealDto.setUnitInt(settleTransDataDto.getUnitInt());
                trdProductDealDto.setShareAmt(settleTransDataDto.getShareAmt());
                trdProductDealDto.setIntAmt(intAmt);
                trdProductDealDto.setFpriceAmt(intAmt);
                trdProductDealDto.setTradeAmt(intAmt);
                trdProductDeals.add(trdProductDealDto);
                settleTransDataDto.setTrdProductDealList(trdProductDeals);
                continue;
            }
        }
    }
}

