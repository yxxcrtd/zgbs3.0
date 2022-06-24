package com.joyintech.tams.settle.batch.datagather.processor.bond;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.position.dao.CommonPtlPositionMapper;
import com.joyintech.tams.invest.common.position.service.CommonPositionService;
import com.joyintech.tams.settle.batch.datagather.constant.StlDataGatherConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.util.JoyinCollectors;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.service.StlDataDealProcessCommonService;
import com.joyintech.tams.settle.batch.datagather.dao.StlDataGatherMapper;
import com.joyintech.tams.settle.batch.datagather.dto.InvestTradeReaderDto;
import com.joyintech.tams.settle.batch.datagather.dto.InvestTradeWriteDto;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.dto.TotalTrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.dto.TrdProductDealDto;
import com.joyintech.tams.settle.common.service.SettleCommonService;

/**
 * 债券股票上市流通
 * @author wangyang1
 * @date 2021年12月9日
 * @see StlDataGatherBond7Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherBond7Processor extends BaseProcessor<NeedOperateEntity, InvestTradeWriteDto> {

    @Autowired
    private StlDataGatherMapper stlDataGatherMapper;

    @Autowired
    private SettleCommonService settleCommonService;

    @Autowired
    private FinProductMapper finProductMapper;

    @Autowired
    private StlDataDealProcessCommonService stlDataDealProcessCommonService;

    @Autowired
    private CommonPositionService commonPositionService;

    @Autowired
    private CommonPtlPositionMapper commonPtlPositionMapper;

    @Override
    public InvestTradeWriteDto process (NeedOperateEntity operateEntity) throws Exception {
        InvestTradeWriteDto investTradeWriteDto = new InvestTradeWriteDto();
        InvestTradeReaderDto item = (InvestTradeReaderDto) operateEntity;
        Date executeDate = this.getBatchExecuteInfo().getExecuteDate();
        // 根据InvestTradeReaderDto中的成交数据明细数据列表，构建一个InvestTradeWriteDto
        List<SettleTransDataDto> settleTransDataDtos = item.getSettleTransDataList();
        if (CollectionUtils.isNotEmpty(settleTransDataDtos)) {
            //组合持仓流水
            List<PtlSecPositionFlow> ptlSecPositionFlowList = new ArrayList<>();
            //指令持仓流水
            List<EtrSecPositionFlow> etrSecPositionFlowList = new ArrayList<>();
            //组合交易
            List<PtlTrade> ptlTradeList = new ArrayList<>();
            //金融产品交易明细
            List<TrdProductDealDto> productDealList = new ArrayList<>();
            //投管交易关系
            List<SettleTransTgMapping> settleTransTgMappingList = new ArrayList<>();
            //金融产品交易汇总表
            List<TotalTrdProductDealDto> totalProductDealList = new ArrayList<>();
            //金融产品汇总附属交易
            List<TrdProductDealAdd> trdProductDealAddList = new ArrayList<>();
            //持仓分配
            Map<String,Object> param = new HashMap<>();
            param.put("dealDate",executeDate);
            // 新老头寸开关(Y:新头寸,N:老头寸)
            String newSecPositionSwitch = CacheUtil.getSysParam("NEW_SEC_POSITION_SWITCH");
            List<PtlSecPosition> ptlSecPositions;
            List<PtlSecPositionFlow> ptlSecPositionFlows;
            // 开关配置N查老头存
            if(DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)){
                param.put("hodingType", DictCodeListExt3.持仓类型_场内上市前冻结.getValue());
                ptlSecPositions = stlDataGatherMapper.listPtlSecPosition(param);
                ptlSecPositionFlows = stlDataGatherMapper.listPtlSecPositionFolw(param);
            }else{ // 默认查新头寸
                param.put("hodingTypeList", Arrays.asList(DictCodeListExt3.资产类型_债券本方券冻结持仓.getValue(),
                        DictCodeListExt3.资产类型_冻结持仓.getValue()));
                param.put("dateType", StlDataGatherConstant.DATE_TYPE_T_0);
                ptlSecPositions = stlDataGatherMapper.listPtlSecPositionRule(param);
                ptlSecPositionFlows = stlDataGatherMapper.listPtlSecPositionFolwRule(param);
            }
            Map<String, List<PtlSecPositionFlow>> ptlSecPositionFlowMap = ptlSecPositionFlows.stream().collect(JoyinCollectors.groupingBy(i -> i.getFinprodId() + "," + i.getSecAcctId() + "," + i.getInvAim()));
            //按金融产品代码分组
            Map<String,List<PtlSecPosition> > finprodIdMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(ptlSecPositions)) {
                ptlSecPositions.stream().collect(Collectors.groupingBy(PtlSecPosition::getFinprodId, Collectors.toList())).forEach(finprodIdMap::put);
            }
            for (SettleTransDataDto settleTransData : settleTransDataDtos) {
                Map<String, Object> map = new HashMap<>();
                map.put("finprodId", settleTransData.getFinprodId());
                List<FinProductOthCode> finProductOthCodes =  commonPtlPositionMapper.selectFinProductOthCodes(map);
                FinProductOthCode temp = new FinProductOthCode();
                temp.setFinprodId(settleTransData.getFinprodId());
                temp.setOthCode(settleTransData.getFinprodId());
                finProductOthCodes.add(temp);
                if(CollectionUtils.isNotEmpty(finProductOthCodes)){
                    for(FinProductOthCode finProductOthCode : finProductOthCodes){
                        String stockId = finProductOthCode.getOthCode();
                        //汇总交易(资产+组合维度)
                        //if (finprodIdMap.containsKey(settleTransData.getFinprodId())) {
                        // 兼容新头寸引擎的申报代码和金融产品代码  modify by fuyongsheng 20220328 start
                        if (finprodIdMap.containsKey(stockId) || finprodIdMap.containsKey(settleTransData.getFinprodId())) {
                            // 兼容新头寸引擎 modify by fuyongsheng 20220328 end
                            // List<PtlSecPosition> ptlSecPositionsByFinprodId = finprodIdMap.get(settleTransData.getFinprodId());
                            List<PtlSecPosition> ptlSecPositionsByFinprodId = new ArrayList<>();
                            ptlSecPositionsByFinprodId = finprodIdMap.get(stockId);
                            if(CollectionUtils.isEmpty(ptlSecPositionsByFinprodId)){
                                ptlSecPositionsByFinprodId = finprodIdMap.get(settleTransData.getFinprodId());
                            }
                            // 兼容新头寸引擎 modify by fuyongsheng 20220328 end
                            FinProduct finProduct = finProductMapper.selectByPrimaryKey(settleTransData.getFinprodId());
                            //BigDecimal amountTotal = ptlSecPositionsByFinprodId.stream().map(item2 -> item2.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
                            for (PtlSecPosition ptlSecPosition : ptlSecPositionsByFinprodId) {
                                // 业务类型
                                // 新头寸债券需要除以100
                                if(!DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)){
                                    if (StringUtil.equals(finProduct.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                                        ptlSecPosition.setAmount(ArithmeticUtil.divide(ptlSecPosition.getAmount(),
                                                new BigDecimal(100), 4, RoundingMode.HALF_UP));
                                    }
                                }
                                TotalTrdProductDealDto totalTrdProductDeal = new TotalTrdProductDealDto();
                                String totalTradeId = settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易);
                                TrdProductDealDto trdProductDeal = new TrdProductDealDto();
                                createDeal(trdProductDeal, settleTransData,finProduct, ptlSecPosition);
                                trdProductDeal.setRemark(stockId);
                                trdProductDeal.setPs(DictCodeList.交易方向_上市流通.getValue());
                                //交易份额
                                trdProductDeal.setShareAmt(ptlSecPosition.getAmount());
                                trdProductDeal.setTotleTradeId(totalTradeId);
                                productDealList.add(trdProductDeal);
                                TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
                                trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
                                trdProductDealAdd.setPortfolioId(ptlSecPosition.getPortfolioId());
                                trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
                                trdProductDealAddList.add(trdProductDealAdd);

                                // 构造金融交易汇总表数据
                                BeanUtil.copyProperties(totalTrdProductDeal, trdProductDeal);
                                // 汇总交易编号
                                totalTrdProductDeal.setTotleTradeId(totalTradeId);
                                totalProductDealList.add(totalTrdProductDeal);

                                //投管交易关系
                                SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
                                settleTransTgMapping.setTradeId(totalTradeId);
                                settleTransTgMapping.setTradeIdTg(totalTradeId);
                                settleTransTgMapping.setTgDataStatus("02");
                                // modify by etliu 20220222 start
                                // 广农商二阶段 GNS-931:【广农商场内清算】上/深交所 基金存续期重复跑批，生成数据有误
//                        settleTransTgMapping.setDealDate(settleTransData.getDealDate());
                                settleTransTgMapping.setDealDate(getBatchExecuteInfo().getExecuteDate());
                                // modify by etliu 20220222 end
                                settleTransTgMappingList.add(settleTransTgMapping);

                                //组合交易
                                PtlTrade ptlTrade = new PtlTrade();
                                ptlTrade.setPortfolioId(ptlSecPosition.getPortfolioId());
                                ptlTradeList.add(createPtlTrade(ptlTrade, trdProductDeal));
                                //只需要处理指令、组合持仓 组合持仓流水PTL_SEC_POSITION_FLOW
                                PtlSecPositionFlow ptlSecPositionFlow = new PtlSecPositionFlow();
                                //流水号
                                ptlSecPositionFlow.setSeqno(IDGenerator.generate());
                                //组合代码
                                ptlSecPositionFlow.setPortfolioId(ptlTrade.getPortfolioId());
                                //证券管理账户/通道代码
                                ptlSecPositionFlow.setSecAcctId(ptlTrade.getSecManageAcctId());
                                //金融产品代码
                                ptlSecPositionFlow.setFinprodId(ptlTrade.getBusiId());
                                //分支序号
                                ptlSecPositionFlow.setBranch(0L);
                                //投资目的
                                ptlSecPositionFlow.setInvAim(ptlTrade.getInvAim());
                                //持仓类型
                                ptlSecPositionFlow.setHodingType(DictCodeList.持仓类型_实际.getValue());
                                //日期
                                ptlSecPositionFlow.setCdate(ptlTrade.getSettleDate());
                                //币种
                                ptlSecPositionFlow.setCcy(trdProductDeal.getCcy());
                                //数量
                                ptlSecPositionFlow.setAmount(trdProductDeal.getShareAmt());
                                //母金融产品代码
                                ptlSecPositionFlow.setpFinprodId(null);
                                //交易编号
                                ptlSecPositionFlow.setTradeId(trdProductDeal.getTradeId());
                                //冲销业务编号
                                ptlSecPositionFlow.setOffsetId(null);
                                //委托编号
                                ptlSecPositionFlow.setEntrustId(null);
                                ptlSecPositionFlowList.add(ptlSecPositionFlow);
                                //重新
                                List<PtlSecPositionFlow> positionFlows = ptlSecPositionFlowMap.get(ptlSecPosition.getFinprodId() + "," + ptlSecPosition.getSecAcctId() + "," + ptlSecPosition.getInvAim());
                                for (PtlSecPositionFlow positionFlow : positionFlows){
                                    // GNS-932:判断是否同一组合 modify by fuyongsheng 20220225 start
                                    if(!ptlSecPosition.getPortfolioId().equals(positionFlow.getPortfolioId())){
                                        continue;
                                    }
                                    // GNS-932:判断是否同一组合 modify by fuyongsheng 20220225 end
                                    //只需要处理指令、组合持仓 组合持仓流水PTL_SEC_POSITION_FLOW
                                    PtlSecPositionFlow ptlSecPositionFlowFrozen = new PtlSecPositionFlow();
                                    //流水号
                                    ptlSecPositionFlowFrozen.setSeqno(IDGenerator.generate());
                                    //组合代码
                                    ptlSecPositionFlowFrozen.setPortfolioId(positionFlow.getPortfolioId());
                                    //证券管理账户/通道代码
                                    ptlSecPositionFlowFrozen.setSecAcctId(positionFlow.getSecAcctId());
                                    //金融产品代码
                                    // ptlSecPositionFlowFrozen.setFinprodId(positionFlow.getFinprodId());
                                    ptlSecPositionFlowFrozen.setFinprodId(settleTransData.getFinprodId());
                                    //分支序号
                                    ptlSecPositionFlowFrozen.setBranch(0L);
                                    //投资目的
                                    ptlSecPositionFlowFrozen.setInvAim(positionFlow.getInvAim());
                                    //持仓类型
                                    // GZYHTZJY-1084:冲销掉上市前冻结的头寸 modify fuyongsheng 20220112 start
                                    // ptlSecPositionFlowFrozen.setHodingType(DictCodeList.持仓类型_实际.getValue());
                                    ptlSecPositionFlowFrozen.setHodingType(DictCodeListExt3.持仓类型_场内上市前冻结.getValue());
                                    // GZYHTZJY-1084:冲销掉上市前冻结的头寸 modify fuyongsheng 20220112 end
                                    //日期
                                    ptlSecPositionFlowFrozen.setCdate(ptlTrade.getSettleDate());
                                    //币种
                                    ptlSecPositionFlowFrozen.setCcy(positionFlow.getCcy());
                                    //数量
                                    ptlSecPositionFlowFrozen.setAmount(ArithmeticUtil.multiply(positionFlow.getAmount(),new BigDecimal(-1)));
                                    //母金融产品代码
                                    ptlSecPositionFlowFrozen.setpFinprodId(null);
                                    //交易编号
                                    ptlSecPositionFlowFrozen.setTradeId(trdProductDeal.getTradeId());
                                    //冲销业务编号
                                    ptlSecPositionFlowFrozen.setOffsetId(positionFlow.getTradeId());
                                    //委托编号
                                    ptlSecPositionFlowFrozen.setEntrustId(positionFlow.getEntrustId());
                                    //冲正标识
                                    ptlSecPositionFlowFrozen.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
                                    ptlSecPositionFlowList.add(ptlSecPositionFlowFrozen);
                                }
                                // 	指令持仓流水	ETR_SEC_POSITION_FLOW  流水号 =	自动生成
                                EtrSecPositionFlow etrSecPositionFlow = new EtrSecPositionFlow();
                                //流水号
                                etrSecPositionFlow.setSeqno(IDGenerator.generate());
                                //组合代码
                                etrSecPositionFlow.setPortfolioId(ptlTrade.getPortfolioId());
                                //证券管理账户/通道代码
                                etrSecPositionFlow.setSecAcctId(StringUtil.isNotEmpty(ptlTrade.getSecManageAcctId())?ptlTrade.getSecManageAcctId():"999999");
                                //金融产品代码
                                etrSecPositionFlow.setFinprodId(ptlTrade.getBusiId());
                                //分支序号
                                etrSecPositionFlow.setBranch(0L);
                                //投资目的
                                etrSecPositionFlow.setInvAim(ptlTrade.getInvAim());
                                //持仓类型
                                etrSecPositionFlow.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                                //日期
                                etrSecPositionFlow.setCdate(ptlTrade.getSettleDate());
                                //币种
                                etrSecPositionFlow.setCcy(trdProductDeal.getCcy());
                                //数量
                                etrSecPositionFlow.setAmount(trdProductDeal.getShareAmt());
                                //母金融产品代码
                                etrSecPositionFlow.setpFinprodId(null);
                                //交易编号
                                etrSecPositionFlow.setTradeId(trdProductDeal.getTradeId());
                                //冲销业务编号
                                etrSecPositionFlow.setOffsetFlag(null);
                                //委托编号
                                etrSecPositionFlow.setOffsetId(null);
                                etrSecPositionFlowList.add(etrSecPositionFlow);
                            }
                        }
                    }
                }
            }
            investTradeWriteDto.setEtrSecPositionFlowList(etrSecPositionFlowList);
            investTradeWriteDto.setPtlSecPositionFlowList(ptlSecPositionFlowList);
            investTradeWriteDto.setPtlTradeList(ptlTradeList);
            investTradeWriteDto.setProductDealList(productDealList);
            investTradeWriteDto.setFinProductDealTotalList(totalProductDealList);
            investTradeWriteDto.setSettleTransTgMappingList(settleTransTgMappingList);
            investTradeWriteDto.setTrdProductDealAddList(trdProductDealAddList);
            investTradeWriteDto.setSecType(DictCodeListExt1.证券品种_债券.getValue());
        }
        return investTradeWriteDto;
    }

    private PtlTrade createPtlTrade(PtlTrade ptlTrade,TrdProductDealDto trdProductDealDto) {
        // 业务编号
        ptlTrade.setBusiNo(trdProductDealDto.getTradeId());
        // 业务表
        ptlTrade.setBusiTable(DictCodeListStl.业务表_金融产品交易表.getValue());
        // 业务明细代码
        ptlTrade.setBusiId(trdProductDealDto.getFinprodId());
        // 业务类型
        ptlTrade.setBusiType(trdProductDealDto.getBusiType());
        // 投资目的
        ptlTrade.setInvAim(trdProductDealDto.getInvAim());
        // 证券托管户代码
        ptlTrade.setSecManageAcctId(trdProductDealDto.getSecManageAcctId());
        // 实际发生日
        ptlTrade.setSettleDate(trdProductDealDto.getSettleDate());
        return ptlTrade;
    }


    /**
     * 构建交易明细
     * @param trdProductDeal
     * @param settleTransData
     * @param finProduct
     */
    private void createDeal(TrdProductDealDto trdProductDeal, SettleTransDataDto settleTransData, FinProduct finProduct, PtlSecPosition ptlSecPosition){
        trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易));
        // 业务类型
        if (StringUtil.equals(finProduct.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
            trdProductDeal.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        }else if (StringUtil.equals(finProduct.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
            trdProductDeal.setBusiType(DictCodeListExt2.共通业务类型_股票投资交易.getValue());
        }
        // 金融产品代码
        trdProductDeal.setFinprodId(settleTransData.getFinprodId());
        // 金融产品类型（投管分类）
        trdProductDeal.setFinprodType2(finProduct.getFinprodType2());
        // 金融产品类型
        trdProductDeal.setFinprodType(finProduct.getFinprodType());
        // 分支序号
        trdProductDeal.setBranch(0L);
        // 交易日
        trdProductDeal.setTradeDate(stlDataDealProcessCommonService.getNextWorkDay(settleTransData.getDealDate(),finProduct.getCalendarId()));
        // 起息日
        trdProductDeal.setVdate(null);
        // 到期日
        trdProductDeal.setMdate(null);
        // 交割日
        trdProductDeal.setSettleDate(trdProductDeal.getTradeDate());
        // 交易币种
        trdProductDeal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        // 净值日期
        trdProductDeal.setCpriceDate(null);
        // 投资目的
        trdProductDeal.setInvAim(ptlSecPosition.getInvAim());
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
        trdProductDeal.setTradeMarket(finProduct.getTradeMarket());
        // 交易平台
        trdProductDeal.setTradePlat(null);
        // 交易市场
        trdProductDeal.setTradeMarketMode(null);
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
        trdProductDeal.setRegistOrg(null);
        // 备注
        trdProductDeal.setRemark(null);
        // 撤销说明
        trdProductDeal.setCancelRemark(null);
        // 销售代码
        trdProductDeal.setSaleCode(null);
        // 计提份额
        trdProductDeal.setAccrNum(null);
        // 交收方式
        trdProductDeal.setSettlemenType(null);
        // 席位号
        trdProductDeal.setSeatId(null);
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
        //净价总额
        trdProductDeal.setCpriceAmt(null);
        // 单位净价
        trdProductDeal.setUnitCprice(null);
        // 单位利息
        trdProductDeal.setUnitInt(null);
        // 单位全价
        trdProductDeal.setUnitFprice(null);
        // 结算方式 券款兑付
        trdProductDeal.setSettMode(null);
        //备注
        trdProductDeal.setRemark("数据清算自动处理");
        //债券上市流通
        trdProductDeal.setTgBusiType( DictCodeListExt3.平安业务类型_债券上市流通.getValue());
    }
}
