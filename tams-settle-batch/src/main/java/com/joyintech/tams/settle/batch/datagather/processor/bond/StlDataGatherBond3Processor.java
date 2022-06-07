package com.joyintech.tams.settle.batch.datagather.processor.bond;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.util.JoyinCollectors;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.settle.batch.datagather.constant.StlDataGatherConstant;
import com.joyintech.tams.settle.batch.datagather.dto.*;
import com.joyintech.tams.settle.batch.datagather.processor.StlDataGatherProcessor;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import com.joyintech.tams.settle.common.util.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债券申购确认交易取值逻辑
 * @author 何阳阳
 * @date 2020/7/17
 * @see StlDataGatherBond3Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherBond3Processor extends StlDataGatherProcessor {

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
            //按照证券代码，交易方向，证券账户代码，席位号汇总
            Map<String,List<SettleTransDataDto> > SettleTransDataMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(settleTransDataDtos)) {
                settleTransDataDtos.stream().collect(JoyinCollectors.groupingBy(i -> i.getSecType()+","
                        + i.getPs()+","+i.getSecAcctId()+","+i.getSeatNumber())).forEach(SettleTransDataMap::put);
            }
            for (SettleTransDataDto settleTransData : settleTransDataDtos) {
                TotalTrdProductDealDto totalTrdProductDeal = new TotalTrdProductDealDto();
                String totalTradeId = settleCommonService.buildBussinessCodeExt(DictCodeListExt3.共通业务类型_可转债申购交易);
                // 构造 金融产品交易明细
                List<SettleTransDataDto> settleTransDataList = SettleTransDataMap.get(settleTransData.getSecType()+","
                        + settleTransData.getPs()+","+settleTransData.getSecAcctId()+","+settleTransData.getSeatNumber());
                BigDecimal shareNumTotal = settleTransDataList.stream().map(i -> i.getShareNum()).reduce(BigDecimal.ZERO, BigDecimal::add);
                TrdProductDealDto trdProductDeal = new TrdProductDealDto();
                createDeal(trdProductDeal, settleTransData);
//                        交易份额		=	成交明细.交易份额*成交明细.申购交易份额/SUM（成交明细.申购交易份额）※按照证券代码，交易方向，证券账户代码，席位号汇总													※差额放在成交明细.申购交易份额最大的一笔上
//                        净价总额		=	成交明细.净价总额*成交明细.申购交易份额/SUM（成交明细.申购交易份额）※按照证券代码，交易方向，证券账户代码，席位号汇总													※差额放在成交明细.申购交易份额最大的一笔上
//                        交易本金		=	成交明细.交易本金*成交明细.申购交易份额/SUM（成交明细.申购交易份额）※按照证券代码，交易方向，证券账户代码，席位号汇总													※差额放在成交明细.申购交易份额最大的一笔上
//                        利息总额		=	0
//                        全价总额		=	成交明细.交易本金*成交明细.申购交易份额/SUM（成交明细.申购交易份额）※按照证券代码，交易方向，证券账户代码，席位号汇总													※差额放在成交明细.申购交易份额最大的一笔上
//                        交易金额		=	成交明细.交易本金*成交明细.申购交易份额/SUM（成交明细.申购交易份额）※按照证券代码，交易方向，证券账户代码，席位号汇总													※差额放在成交明细.申购交易份额最大的一笔上
                trdProductDeal.setPs(settleTransData.getPs());
                //交易份额
                trdProductDeal.setShareAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransData.getShareAmt(),settleTransData.getShareNum()),shareNumTotal,2));
                //净价总额
                trdProductDeal.setCpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransData.getCpriceAmt(),settleTransData.getShareNum()),shareNumTotal,2));
                //交易本金
                trdProductDeal.setPrinAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransData.getPrinAmt(), settleTransData.getShareNum()), shareNumTotal, 2));
                //利息总额
                trdProductDeal.setIntAmt(BigDecimal.ZERO);
                //全价总额
                trdProductDeal.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransData.getPrinAmt(), settleTransData.getShareNum()), shareNumTotal, 2));
                //交易金额
                trdProductDeal.setTradeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransData.getPrinAmt(), settleTransData.getShareNum()), shareNumTotal, 2));
                //是否结清
                trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
                //前置交易编号
                trdProductDeal.setbTradeId(settleTransData.getTradeId2());
                // 关联系统交易编号
                trdProductDeal.setfTradeId(settleTransData.getTradeId());
                // 汇总交易编号
                trdProductDeal.setTotleTradeId(totalTradeId);
                // 投管业务类型
                trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
                // 成交明细.委托代码
                trdProductDeal.setEntrustId(settleTransData.getEntrustId());
                //
                trdProductDeal.setCounterId(settleTransData.getPurchCounter());
                String tradeId = settleCommonService.buildBussinessCodeExt(DictCodeListExt3.共通业务类型_可转债申购交易);
                trdProductDeal.setTradeId(tradeId);
                // GZYHTZJY-1043: 生成可转债申购确认汇总交易 modify by fuyongsheng 20220110 start
                // 构造金融交易汇总表数据
                BeanUtil.copyProperties(totalTrdProductDeal, trdProductDeal);
                // 汇总交易编号
                totalTrdProductDeal.setTotleTradeId(totalTradeId);
                totalTrdProductDeal.setbTradeId(settleTransData.getTradeTotalId());
                totalProductDealList.add(totalTrdProductDeal);

                // GZYHTZJY-1043: 生成可转债申购确认汇总交易 modify by fuyongsheng 20220110 end
                //构造 金融产品交易明细附属
                TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
                trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
                trdProductDealAdd.setPortfolioId(settleTransData.getPortfolioId());
                trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
                trdProductDealAdd.setPurchModle(settleTransData.getPurchModle());
                trdProductDealAdd.setPurchType(DictCodeListExt2.申购类型_普通申购.getValue());
                productDealList.add(trdProductDeal);
                trdProductDealAddList.add(trdProductDealAdd);

                TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
                BeanUtil.copyProperties(trdDealTotalAdd, trdProductDealAdd);
                trdDealTotalAdd.setEntrustId(settleTransData.getEntrustId());
                trdDealTotalAdd.setTotleTradeId(totalTradeId);
                trdDealTotalAdd.setPurAmt(settleTransData.getPurchAmt());
                trdDealTotalAdd.setPurchDate(settleTransData.getPurchDate());
                trdDealTotalAddList.add(trdDealTotalAdd);

                //构造 成交数据与投管交易关系
                SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
                settleTransTgMapping.setTradeId(settleTransData.getTradeId());
                settleTransTgMapping.setTradeIdTg(totalTradeId);
                settleTransTgMapping.setTgDataStatus("02");
                settleTransTgMapping.setDealDate(getBatchExecuteInfo().getExecuteDate());
                settleTransTgMappingList.add(settleTransTgMapping);
            }
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
        // 清算速度
        trdProductDeal.setSettleSpeed(settleTransData.getSettleSpeed());
        //净价总额
        trdProductDeal.setCpriceAmt(ArithmeticUtil.subtract(settleTransData.getFpriceAmt(),settleTransData.getIntAmt()));
        // 单位净价
        trdProductDeal.setUnitCprice(settleTransData.getUnitCprice());
        // 单位利息
        trdProductDeal.setUnitInt(settleTransData.getUnitInt());
        // 单位全价
        trdProductDeal.setUnitFprice(settleTransData.getUnitFprice());
        // 结算方式 券款兑付
        trdProductDeal.setSettMode("03");
    }

    @Override
    protected String getSecType() {
        return DictCodeListExt1.证券品种_债券.getValue();
    }

    @Override
    protected List<String> getFinprodType() {return Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券.getValue());}
}
