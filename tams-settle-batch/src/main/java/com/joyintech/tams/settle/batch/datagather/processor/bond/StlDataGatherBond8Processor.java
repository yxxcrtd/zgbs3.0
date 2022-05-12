package com.joyintech.tams.settle.batch.datagather.processor.bond;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.util.JoyinCollectors;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.position.dao.CommonPtlPositionMapper;
import com.joyintech.tams.invest.common.position.service.CommonPositionService;
import com.joyintech.tams.settle.batch.datadeal.service.StlDataDealProcessCommonService;
import com.joyintech.tams.settle.batch.datagather.constant.StlDataGatherConstant;
import com.joyintech.tams.settle.batch.datagather.dao.StlDataGatherMapper;
import com.joyintech.tams.settle.batch.datagather.dto.*;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 债券一级投标
 * @author fuyongsheng
 * @date 2022年05月12日
 * @see StlDataGatherBond8Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherBond8Processor extends BaseProcessor<NeedOperateEntity, InvestTradeWriteDto> {

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
        List<SettleTransDataTotal> settleTransDataTotalDtos = item.getSettleTransDataTotalList();
        if (CollectionUtils.isNotEmpty(settleTransDataTotalDtos)) {
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

            for (SettleTransDataTotal settleTransDataTotal : settleTransDataTotalDtos) {
                TotalTrdProductDealDto totalTrdProductDeal = new TotalTrdProductDealDto();
                String totalTradeId = settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易);
                TrdProductDealDto trdProductDeal = new TrdProductDealDto();
                createDeal(trdProductDeal, null,null, null);
                trdProductDeal.setPs(DictCodeList.交易方向_上市流通.getValue());
                //交易份额
                trdProductDeal.setTotleTradeId(totalTradeId);
                productDealList.add(trdProductDeal);
                TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
                trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
                trdProductDealAdd.setPortfolioId(trdProductDeal.getPortfolioId());
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
                settleTransTgMapping.setDealDate(getBatchExecuteInfo().getExecuteDate());
                settleTransTgMappingList.add(settleTransTgMapping);

                //组合交易
                PtlTrade ptlTrade = new PtlTrade();
                ptlTrade.setPortfolioId(trdProductDeal.getPortfolioId());
                ptlTradeList.add(createPtlTrade(ptlTrade, trdProductDeal));
            }
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
