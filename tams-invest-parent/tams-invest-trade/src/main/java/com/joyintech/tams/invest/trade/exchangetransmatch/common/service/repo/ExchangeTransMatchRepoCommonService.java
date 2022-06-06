package com.joyintech.tams.invest.trade.exchangetransmatch.common.service.repo;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.joyintech.background.component.common.pack.common.base.BaseService;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.TrdProductDealDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.position.dto.EtrEntrustAddDto;
import com.joyintech.tams.invest.common.position.dto.FinTradeProductDto;
import com.joyintech.tams.invest.common.position.dto.PositionBaseData;
import com.joyintech.tams.invest.common.position.dto.PostionParameter;
import com.joyintech.tams.invest.common.position.service.CommonPositionService;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDto;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dao.ExchangeTransMatchCommonMapper;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dto.CommonTrdTempDto;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dto.ExchangeTransMatchBaseTradeDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExchangeTransMatchRepoCommonService extends BaseService {
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired(required = false)
    private ExchangeTransMatchCommonMapper exchangeTransMatchCommonMapper;

    @Autowired
    private SettleTransDataPledgeMapper settleTransDataPledgeMapper;

    // modify by etliu 20220110 start
    // GZYHTZJY-1032: 协议回购的头寸处理，需要走PTL_POSITION_FLOW_CFG配置
    @Autowired
    private CommonPositionService commonPositionService;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    /**
     * 交易类金融产品表
     */
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    private static final Logger logger = LoggerFactory.getLogger(ExchangeTransMatchRepoCommonService.class);
    // modify by etliu 20220110 end

    public List<ExchangeTransMatchBaseTradeDto> genFinProductTradeDto(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto){
        return null;
    }

    /**
     * 生成金融产品交易汇总信息
     * @param dealDto
     * @param entrustDto
     * @return
     */
    public TrdFinProductDealTotal genTrdFinProductDealTotal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto) {
        return null;
    }

    /**
     * 生成金融产品交易明细
     */

    public TrdProductDeal genTrdProductDeal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        return null;
    }


    /**
     * 生成金融产品交易明细
     */
    protected TrdProductDealDto createProductDeal(ExchangeTransMatchCommonDto settleTransData) {
        TrdProductDealDto trdProductDeal = new TrdProductDealDto();
        // 交易编号，按现有的交易编号生成规则，自动生成
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), settleTransData.getFinprodType2())) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
            trdProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(bussinessCodeParameter));
        }   else {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
            trdProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(bussinessCodeParameter));
        }

        // 金融产品类型
        trdProductDeal.setFinprodType(settleTransData.getFinprodType());
        // 分支序号
        trdProductDeal.setBranch(0L);
        // 交易日
        trdProductDeal.setTradeDate(settleTransData.getTradeDate());
        // 起息日
        trdProductDeal.setVdate(settleTransData.getVdate());
        // 交易币种
        trdProductDeal.setCcy(settleTransData.getCcy());
        // 净值日期
        trdProductDeal.setCpriceDate(null);
        // 单位利息
        trdProductDeal.setUnitInt(null);
        // 单位全价
        trdProductDeal.setUnitFprice(null);
        // 单位面值
        trdProductDeal.setParValue(null);
        // 净价总额
        trdProductDeal.setCpriceAmt(null);
        // 投资目的
        trdProductDeal.setInvAim(null);

        // 交易状态
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 是否已撤销
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        // 原交易编号
        trdProductDeal.setpTradeId(null);
        // 是否结清
        trdProductDeal.setIsClean(null);
        // 反向交易编号
        trdProductDeal.setrTradeId(null);
        // 通道代码
        trdProductDeal.setChlId(null);
        // 是否外部交易
        trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 交易场所
        trdProductDeal.setTradeMarket(settleTransData.getTradeMarket());
        // 交易平台
        trdProductDeal.setTradePlat(settleTransData.getTradePlat());
        // 交易市场
        trdProductDeal.setTradeMarketMode(settleTransData.getTradeMarketMode());

        if(StringUtil.equals(settleTransData.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
            trdProductDeal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
        }
        else if(StringUtil.equals(settleTransData.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
            trdProductDeal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
        }
        // 本方交易员
        trdProductDeal.setTrader(settleTransData.getTrader());
        // 对手方交易员
        trdProductDeal.setCounterTrader(settleTransData.getCounterTrader());
        // 对手方联系方式
        trdProductDeal.setCounterContact(null);
        // 证券管理户代码
        trdProductDeal.setSecManageAcctId(settleTransData.getSecAcctId());
        // 关联系统交易编号
        // trdProductDeal.setfTradeId(settleTransData.getApplyTradeId());
        trdProductDeal.setfTradeId(settleTransData.getfCjbh());
        // 现金流代码
        trdProductDeal.setCashId(null);
        // 登记托管机构
        trdProductDeal.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
        // 备注
        trdProductDeal.setRemark("数据清算自动处理");
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

        return trdProductDeal;
    }

    /**
     * 金融产品交易附属表
     */
    public TrdProductDealAdd genTrdProductDealAdd(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, trdProductDeal);
        trdProductDealAdd.setEntrustId(entrustDto.getEntrustId());
        trdProductDealAdd.setPortfolioId(entrustDto.getPortfolioId());

        if(StringUtil.equals(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue(),dealDto.getMatchType())) {
            trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_批处理生成.getValue());
        }
        else {
            trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        }
        // 本方交易商
        trdProductDealAdd.setDealerNo(dealDto.getDealerNo());
        // 对手方交易商
        trdProductDealAdd.setOutDealerNo(dealDto.getOutDealerNo());
        // 本方交易主体
        trdProductDealAdd.setDealerPartyNo(dealDto.getDealerPartyNo());
        // 对手方交易主体
        trdProductDealAdd.setOutDealerPartyNo(dealDto.getOutDealerPartyNo());
        return trdProductDealAdd;
    }

    /**
     * 金融产品交易附属表
     */
    public PaTrdProductDeal genPaTrdProductDeal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, trdProductDeal);
        paTrdProductDeal.setPaBussType(entrustDto.getEntrustId());
        if (StringUtil.equals(dealDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_正回购.getValue());
            paTrdProductDeal.setBackLendDirection(DictCodeList.回购拆借方向_正回购.getValue());
        }
        else {
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_逆回购.getValue());
            paTrdProductDeal.setBackLendDirection(DictCodeList.回购拆借方向_逆回购.getValue());
        }
        return paTrdProductDeal;
    }

    public List<PtlTrade> generatePtlTrade(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDealDto) {
            List<PtlTrade> ptlTradeList = new ArrayList<>();
            PtlTrade ptlTrade = new PtlTrade();
            // 组合代码
            ptlTrade.setPortfolioId(entrustDto.getPortfolioId());
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
            ptlTrade.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
            ptlTradeList.add(ptlTrade);
            return ptlTradeList;
    }

    /**
     * 金融产品交易费用明细
     * @param
     * @return
     */
    public List<TrdTradeFeeDetail> genTrdTradeFeeDetailList(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        List<TrdTradeFeeDetail> feeDetailList = new ArrayList<>(7);
            if (dealDto.getJsfAmt() != null && dealDto.getJsfAmt().compareTo(new BigDecimal(0)) > 0) {
                // 经手费
                feeDetailList.add(buildTrdTradeFeeDetail(trdProductDeal.getTradeId(),DictCodeListExt1.一次性费用类型_经手费.getValue(),DictCodeList.是否_是.getValue(),dealDto.getJsfAmt(),entrustDto.getMarchQty(),dealDto.getRemainNum(),trdProductDeal.getRegistOrg()));
            }
            if (dealDto.getGhfAmt() != null && dealDto.getGhfAmt().compareTo(new BigDecimal(0)) > 0) {
                // 过户费
                feeDetailList.add(buildTrdTradeFeeDetail(trdProductDeal.getTradeId(),DictCodeListExt1.一次性费用类型_过户费.getValue(),DictCodeList.是否_是.getValue(),dealDto.getGhfAmt(),entrustDto.getMarchQty(),dealDto.getRemainNum(),trdProductDeal.getRegistOrg()));
            }
            if (dealDto.getZgfAmt() != null && dealDto.getZgfAmt().compareTo(new BigDecimal(0)) > 0) {
                // 证管费
                feeDetailList.add(buildTrdTradeFeeDetail(trdProductDeal.getTradeId(),DictCodeListExt1.一次性费用类型_证管费.getValue(),DictCodeList.是否_是.getValue(),dealDto.getZgfAmt(),entrustDto.getMarchQty(),dealDto.getRemainNum(),trdProductDeal.getRegistOrg()));
            }
            if (dealDto.getSxfAmt() != null && dealDto.getSxfAmt().compareTo(new BigDecimal(0)) > 0) {
                // 手续费
                feeDetailList.add(buildTrdTradeFeeDetail(trdProductDeal.getTradeId(),DictCodeListExt1.一次性费用类型_交易手续费.getValue(),DictCodeList.是否_是.getValue(),dealDto.getSxfAmt(),entrustDto.getMarchQty(),dealDto.getRemainNum(),trdProductDeal.getRegistOrg()));
            }
            if (dealDto.getSettleFee() != null && dealDto.getSettleFee().compareTo(new BigDecimal(0)) > 0) {
                // 结算费
                feeDetailList.add(buildTrdTradeFeeDetail(trdProductDeal.getTradeId(),DictCodeListExt1.一次性费用类型_结算费.getValue(),DictCodeList.是否_是.getValue(),dealDto.getSettleFee(),entrustDto.getMarchQty(),dealDto.getRemainNum(),trdProductDeal.getRegistOrg()));
            }
            if (dealDto.getStampFee() != null && dealDto.getStampFee().compareTo(new BigDecimal(0)) > 0) {
                // 樱花谁
                feeDetailList.add(buildTrdTradeFeeDetail(trdProductDeal.getTradeId(),DictCodeListExt1.一次性费用类型_印花税.getValue(),DictCodeList.是否_是.getValue(),dealDto.getStampFee(),entrustDto.getMarchQty(),dealDto.getRemainNum(),trdProductDeal.getRegistOrg()));
            }
            // 佣金
            if (dealDto.getCommissionAmt() != null && dealDto.getCommissionAmt().compareTo(new BigDecimal(0)) > 0) {
                feeDetailList.add(buildTrdTradeFeeDetail(trdProductDeal.getTradeId(),DictCodeListExt1.一次性费用类型_佣金.getValue(),dealDto.getIsPayWithTrade(),dealDto.getCommissionAmt(),entrustDto.getMarchQty(),dealDto.getRemainNum(),trdProductDeal.getRegistOrg()));
            }
        return feeDetailList;
    }

    /**
     * 划款指令明细list
     */
    public List<StlPayOrdersDto> generateStlPayOrdersList(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<>();
        if(trdProductDeal.getTradeAmt() == null){
            return stlPayOrdersList;
        }
        StlPayOrdersDto stlPayOsrdersDto = createStlPayOrders(dealDto, entrustDto, trdProductDeal);
        // 收付方向
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), dealDto.getFinprodType2())) {
            stlPayOsrdersDto.setPs(DictCodeListExt1.收付方向_支出.getValue());
        } else {
            stlPayOsrdersDto.setPs(DictCodeList.收付方向_收入.getValue());
        }
        stlPayOrdersList.add(stlPayOsrdersDto);
        return stlPayOrdersList;
    }

    /**
     * 根据交易明细创建指令
     * @param dealDto 交易明细
     * @return
     */
    protected StlPayOrdersDto createStlPayOrders(ExchangeTransMatchCommonDto dealDto,ExchangeTransMatchCommonEntrustDto entrustDto,TrdProductDeal trdProductDeal) {
        // 指令
        StlPayOrdersDto stlPayOrders = new StlPayOrdersDto();
        // 指令编号
        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        bussinessCodeParameter.setSeqNo("9998");
        stlPayOrders.setOrderId(baseGetBusinessId.buildBussinessCode(bussinessCodeParameter));
        // 结算日期
        stlPayOrders.setSettleDate(trdProductDeal.getSettleDate());
        // 本方资金账户
        stlPayOrders.setMyAcctNo(dealDto.getAcctNo());
        // 结算金额
        stlPayOrders.setSettleAmt(trdProductDeal.getTradeAmt());
        // 币种
        stlPayOrders.setCcy(trdProductDeal.getCcy());
        // 是否需要发送
        stlPayOrders.setIsNeedSend(DictCodeList.是否_否.getValue());
        // 是否汇总交易
        stlPayOrders.setIsTotalTrade(DictCodeList.是否_是.getValue());
        // 交易编号
        stlPayOrders.setTradeId(trdProductDeal.getTradeId());
        // 指令状态
        stlPayOrders.setOrderType(DictCodeListExt1.交易状态_初始.getValue());
        // 投资组合代码
        stlPayOrders.setPortfolioId(entrustDto.getPortfolioId());
        // 理财产品代码
        stlPayOrders.setProdId(trdProductDeal.getFinprodId());
        // 备注
        stlPayOrders.setRemark("匹配交收处理");
        // 划款类型
        stlPayOrders.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
        if(StringUtil.equals(trdProductDeal.getPs(),DictCodeList.交易方向_收息到账.getValue())
        || StringUtil.equals(trdProductDeal.getPs(),DictCodeList.交易方向_还本到账.getValue())) {
            if(StringUtil.equals(dealDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                stlPayOrders.setPs(DictCodeListExt1.收付方向_支出.getValue());
            }
            else {
                stlPayOrders.setPs(DictCodeList.收付方向_收入.getValue());
            }
        }

        return stlPayOrders;
    }

    /**
     * 创建费用明细
     * @param tradeId 交易编号
     * @param feeType 费用类型
     * @param payWithTrade 是否随交易支付
     * @param feeAmt 费用金额
     * @return 费用明细
     */
    private TrdTradeFeeDetail buildTrdTradeFeeDetail(String tradeId, String feeType, String payWithTrade, BigDecimal feeAmt, BigDecimal marchQty,BigDecimal remainNum,String registOrg) {
        // 经手费
        TrdTradeFeeDetail feeDetail = new TrdTradeFeeDetail();
        // 交易编号
        feeDetail.setTradeId(tradeId);
        // 费用类型
        feeDetail.setFeeType(feeType);
        // 是否随交易支付
        feeDetail.setIsPayWithTrade(payWithTrade);
        // 费用金额
        feeDetail.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(marchQty,remainNum,4),feeAmt));
        // 登记托管机构
        feeDetail.setRegistOrg(registOrg);
        return feeDetail;
    }

    /**
     * 获取回购首期交易
     * @param fCjbh
     * @param vdate
     * @param seatNumber
     * @param secAcctId
     * @return
     */
    public TrdProductDeal getFirstRepoTrade(String fCjbh, Date vdate, String seatNumber, String secAcctId) {
        return trdCommonBaseService.getFirstRepoTrade(fCjbh, seatNumber, secAcctId, vdate);
    }

    /**
     * 获取回购首期质押品
     * @param fCjbh
     * @param vdate
     * @param seatNumber
     * @param secAcctId
     * @return
     */
    public List<FinDealPledge> getFirstRepoFinDealPledge(String fCjbh, Date vdate, String seatNumber, String secAcctId) {
        // 查询首期清算数据
        List<SettleTransData> settleTransDatas = trdCommonBaseService.selectSqSettleTransDatas(fCjbh, seatNumber,
                secAcctId, vdate);
        if(CollectionUtils.isNotEmpty(settleTransDatas)){
            // 查询首期交易质押品
            Map<String,Object> queryMap = new HashMap<>();
            queryMap.put("fCjbh",settleTransDatas.get(0).getTradeId());
            return exchangeTransMatchCommonMapper.getFirstRepoFinDealPledge(queryMap);
        }
        return new ArrayList<>();
    }

    public TrdDealTotalAdd generateDealTotalAdd(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        TrdDealTotalAdd trdDealTotalAdd =  new TrdDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(trdDealTotalAdd, trdFinProductDealTotal);
        trdDealTotalAdd.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        trdDealTotalAdd.setEntrustId(entrustDto.getEntrustId());
        // 本方交易商
        trdDealTotalAdd.setDealerNo(dealDto.getDealerNo());
        // 对手方交易商
        trdDealTotalAdd.setOutDealerNo(dealDto.getOutDealerNo());
        // 本方交易主体
        trdDealTotalAdd.setDealerPartyNo(dealDto.getDealerPartyNo());
        // 对手方交易主体
        trdDealTotalAdd.setOutDealerPartyNo(dealDto.getOutDealerPartyNo());
        return trdDealTotalAdd;
    }

    /**
     * 质押券置换
     * @param trdFinProductDealTotal
     * @return
     */
    public FinPledgeDisplace genFinPledgeDisplace(TrdFinProductDealTotal trdFinProductDealTotal) {
        FinPledgeDisplace finPledgeDisplace = new FinPledgeDisplace();
        //业务编号 = 同汇总交易.汇总交易编号
        finPledgeDisplace.setTradeId(trdFinProductDealTotal.getTotleTradeId());
        //原始业务编号 = 同汇总交易.金融产品代码
        finPledgeDisplace.setfTradeId(trdFinProductDealTotal.getFinprodId());
        //置换日期 = 同汇总交易.交易日
        finPledgeDisplace.setTradeDate(trdFinProductDealTotal.getTradeDate());
        //置换费
        finPledgeDisplace.setExchangeFee(BigDecimal.ZERO);
        //备注
        finPledgeDisplace.setRemark("数据清算自动处理");
        return finPledgeDisplace;
    }

    /**
     * 质押券置换明细
     * @param finPledgeDisplace
     * @param trdProductDeal
     * @return
     */
    public FinPledgeDisplaceDetail genFinPledgeDisplaceDetail(FinPledgeDisplace finPledgeDisplace,TrdProductDeal trdProductDeal) {
        FinPledgeDisplaceDetail finPledgeDisplaceDetail = new FinPledgeDisplaceDetail();
        //流水号 = 同金融产品交易.交易编号
        finPledgeDisplaceDetail.setSeqNo(trdProductDeal.getTradeId());
        //业务编号 = 质押券置换.业务编号
        finPledgeDisplaceDetail.setTradeId(finPledgeDisplace.getTradeId());
        //金融产品代码 = 同金融产品交易.金融产品代码
        finPledgeDisplaceDetail.setFinprodId(trdProductDeal.getFinprodId());
        //分支序号 = 同金融产品交易.分支序号
        finPledgeDisplaceDetail.setBranch(BigDecimal.ZERO);
        //置换方向 = 同金融产品交易.交易方向
        finPledgeDisplaceDetail.setTradeType(trdProductDeal.getPs());
        //换出面额 = 同金融产品交易.交易份额
        // finPledgeDisplaceDetail.setPledgeDisplaceAmt(trdProductDeal.getShareAmt());
        finPledgeDisplaceDetail.setPledgeDisplaceAmt(ArithmeticUtil.multiply(trdProductDeal.getShareAmt(), new BigDecimal(100)));
        //折算比例 = 同金融产品交易.单位净价
        // finPledgeDisplaceDetail.setScale(trdProductDeal.getUnitCprice());

        return finPledgeDisplaceDetail;
    }


    /**
     * 生成质押券置换结果
     * @param dealDto
     * @param commonEntrustDto
     * @param total
     * @param firstFinDealPledgeList
     * @param updateFinDealPledges
     * @return
     */
    public List<FinPledgeDisplaceResult> genFinPledgeDisplaceResult(ExchangeTransMatchCommonDto dealDto,
                                                                    ExchangeTransMatchCommonEntrustDto commonEntrustDto,
                                                                    TrdFinProductDealTotal total,
                                                                    List<FinDealPledge> firstFinDealPledgeList,
                                                                    List<FinDealPledge> updateFinDealPledges){
        if(DictCodeListStl.业务场景分类明细_债券协议正回购_换券_换钱.getValue().equals(dealDto.getBusiSceneCalss())){
            return new ArrayList<>();
        }
        if(CollectionUtils.isEmpty(firstFinDealPledgeList)){
            return new ArrayList<>();
        }
        List<FinPledgeDisplaceResult> finPledgeDisplaceResultList = new ArrayList<>();
        // 获取质押品置换结果-置换前信息
        List<FinPledgeDisplaceResult> beforedisplaceResult = this.getBeforeDisplaceResult(firstFinDealPledgeList, total);
        finPledgeDisplaceResultList.addAll(beforedisplaceResult);

        // 获取质押品置换结果-置换后信息
        if(DictCodeListExt3.交易方向_中途换券.getValue().equals(commonEntrustDto.getPs())){
            List<FinPledgeDisplaceResult> afterReplaceResult = this.getAfterDisplaceResult(dealDto, total, updateFinDealPledges);
            finPledgeDisplaceResultList.addAll(afterReplaceResult);
        }
        return finPledgeDisplaceResultList;
    }

    /**
     * 获取质押品置换结果-置换前信息
     * @param firstFinDealPledgeList
     * @param total
     * @return
     */
    private List<FinPledgeDisplaceResult> getBeforeDisplaceResult(List<FinDealPledge> firstFinDealPledgeList, TrdFinProductDealTotal total){
        List<FinPledgeDisplaceResult> beforeDisplaceResult = new ArrayList<>();
        for(FinDealPledge finDealPledge : firstFinDealPledgeList) {
            FinPledgeDisplaceResult finPledgeDisplaceResult = new FinPledgeDisplaceResult();
            finPledgeDisplaceResult.setBranch(new BigDecimal(0));
            //业务编号 = 汇总交易表.汇总交易编号
            finPledgeDisplaceResult.setTradeId(total.getTotleTradeId());
            //金融产品代码 = 首期.质押金融产品代码
            finPledgeDisplaceResult.setFinprodId(finDealPledge.getPledgeFinprodId());
            // 结果类型 = 置换前
            finPledgeDisplaceResult.setResultType(DictCodeListExt3.质押券置换_置换结果_置换前.getValue());
            // 质押券面总额 = 首期.质押券面总额
            finPledgeDisplaceResult.setPledgeAmt(finDealPledge.getPledgeAmt());
            // 折算比例 = 首期.质押率
            finPledgeDisplaceResult.setScale(finDealPledge.getPledgeRatio());
            beforeDisplaceResult.add(finPledgeDisplaceResult);
        }
        return beforeDisplaceResult;
    }

    /**
     * 获取质押品置换结果-置换后信息
     * @param dealDto
     * @param total
     * @param updateFinDealPledges
     * @return
     */
    private List<FinPledgeDisplaceResult> getAfterDisplaceResult(ExchangeTransMatchCommonDto dealDto,
                                                                 TrdFinProductDealTotal total,
                                                                 List<FinDealPledge> updateFinDealPledges) {
        List<FinPledgeDisplaceResult> afterDisplaceResult = new ArrayList<>();
        for(FinDealPledge finDealPledge : updateFinDealPledges) {
            FinPledgeDisplaceResult finPledgeDisplaceResult = new FinPledgeDisplaceResult();
            finPledgeDisplaceResult.setBranch(new BigDecimal(0));
            //业务编号 = 汇总交易表.汇总交易编号
            finPledgeDisplaceResult.setTradeId(total.getTotleTradeId());
            //金融产品代码 = 更新后质押品.质押金融产品代码
            finPledgeDisplaceResult.setFinprodId(finDealPledge.getPledgeFinprodId());
            // 结果类型 = 置换后
            finPledgeDisplaceResult.setResultType(DictCodeListExt3.质押券置换_置换结果_置换后.getValue());
            // 质押券面总额 = 更新后质押品.质押券面总额
            finPledgeDisplaceResult.setPledgeAmt(finDealPledge.getPledgeAmt());
            // 质押率 = 更新后质押品.质押率
            finPledgeDisplaceResult.setScale(finDealPledge.getPledgeRatio());
            afterDisplaceResult.add(finPledgeDisplaceResult);
        }
        return afterDisplaceResult;
    }

        /**
         * 生成金融产品交易标的
         * 对于交易方向为‘买入’的数据生成1条数据
         * @param trdProductDeal
         * @return
         */
    public FinTradeProduct genFinTradeProduct(SettleTransData settleTransData,TrdProductDeal trdProductDeal) {
        FinTradeProduct finTradeProduct = new FinTradeProduct();
        finTradeProduct.setFinprodId(trdProductDeal.getFinprodId());
        finTradeProduct.setBranch(trdProductDeal.getBranch());
        finTradeProduct.setFinprodType(trdProductDeal.getFinprodType());
        finTradeProduct.setFinprodType2(trdProductDeal.getFinprodType2());
        finTradeProduct.setProfitType(DictCodeList.收益类型_收益型.getValue());
        finTradeProduct.setPrin(trdProductDeal.getPrinAmt());
        finTradeProduct.setCcy(trdProductDeal.getCcy());
        finTradeProduct.setVdate(trdProductDeal.getVdate());
        finTradeProduct.setMdate(trdProductDeal.getMdate());
        finTradeProduct.setVpayDate(settleTransData.getVpayDate());
        finTradeProduct.setMpayDate(settleTransData.getMpayDate());
        finTradeProduct.setTermType(settleTransData.getTermType());
        if(null != trdProductDeal.getVdate() && null != trdProductDeal.getMdate()){
            finTradeProduct.setTerm(DateUtil.getBetweenDayNumber(trdProductDeal.getVdate(),trdProductDeal.getMdate()));
        }
        if(null != trdProductDeal.getVdate() && null != trdProductDeal.getMdate()){
            finTradeProduct.setActCapDays(DateUtil.getBetweenDayNumber(settleTransData.getVpayDate(),settleTransData.getMpayDate()));
        }
        finTradeProduct.setIntType(DictCodeList.利率类型_固定利率.getValue());
        finTradeProduct.setIntRate(settleTransData.getUnitCprice());
        finTradeProduct.setBasis(DictCodeList.计息基础_A_365.getValue());
        finTradeProduct.setmDeliveryType("DVP");
        finTradeProduct.setmPrinAmt(trdProductDeal.getShareAmt());
        BigDecimal mIntAmt = ArithmeticUtil.multiply(ArithmeticUtil.multiply(finTradeProduct.getmPrinAmt(), finTradeProduct.getIntRate()),
                ArithmeticUtil.divide(new BigDecimal(DateUtil.getBetweenDayNumber(finTradeProduct.getVpayDate(), finTradeProduct.getMpayDate())), 365, 14));
        finTradeProduct.setmIntAmt(mIntAmt);
        //GNS-835:使用ArithmeticUtil计算,防止null值 modify by fuyongsheng 20220210 start
        // finTradeProduct.setmTradeAmt(finTradeProduct.getmPrinAmt().add(finTradeProduct.getmIntAmt()));
        finTradeProduct.setmTradeAmt(ArithmeticUtil.doAdd(finTradeProduct.getmPrinAmt(),finTradeProduct.getmIntAmt()));
        //GNS-835:使用ArithmeticUtil计算,防止null值 modify by fuyongsheng 20220210 end
//        finTradeProduct.setmIntAmt(settleTransData.getmIntAmt());
//        finTradeProduct.setmPrinAmt(settleTransData.getmPrinAmt());
        finTradeProduct.setTradeMarket(trdProductDeal.getTradeMarket());
        finTradeProduct.setCounterId(trdProductDeal.getCounterId());
        finTradeProduct.setCalendarId(StringUtil.equals(trdProductDeal.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())?DictCodeList.交易日历_上海交易所日历.getValue():DictCodeList.交易日历_深圳交易所日历.getValue());
        finTradeProduct.setTermType(settleTransData.getTermType());
        if(null != finTradeProduct.getVpayDate() && null != finTradeProduct.getMpayDate()){
            finTradeProduct.setActCapDays(DateUtil.getBetweenDayNumber(finTradeProduct.getVpayDate(),finTradeProduct.getMpayDate()));
        }
        return finTradeProduct;
    }

    public FinProductType genFinProductType(ExchangeTransMatchCommonDto dealDto,TrdProductDeal trdProductDeal) {
        FinProductType finProductType = new FinProductType();
        finProductType.setFinprodId(trdProductDeal.getFinprodId());
        finProductType.setBranch(trdProductDeal.getBranch());
        finProductType.setType6(dealDto.getSettleTransDataTotalAdd().getRepoType());
        if(StringUtil.equals(dealDto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
            finProductType.setType7(DictCodeList.回购拆借方向_正回购.getValue());
        }
        else {
            finProductType.setType7(DictCodeList.回购拆借方向_逆回购.getValue());
        }

        finProductType.setType8(DictCodeList.平安资产类型_质押式回购.getValue());
        return finProductType;
    }

    // modify by etliu 20220110 start
    // GZYHTZJY-1032: 协议回购的头寸处理，需要走PTL_POSITION_FLOW_CFG配置
    /**
     * 处理协议回购头寸
     * @param dealDto 交收数据
     * @param entrustDto 指令数据
     * @param baseTradeProductDealDtos 组装后交易数据
     * @param finPledgeDisplaceDetailHuanChu 质押券置换换出信息
     * @param firstFinDealPledgeTempList 首期质押券信息
     * @param finPledgeDisplaceResultTempList 中途换券结果列表
     */
    public void handlePositionOfXyhg(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto,
                                     List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtos,
                                     FinPledgeDisplaceDetail finPledgeDisplaceDetailHuanChu,
                                     List<FinDealPledge> firstFinDealPledgeTempList,
                                     List<FinPledgeDisplaceResult> finPledgeDisplaceResultTempList) {
        CommonTrdTempDto trdTempDto = new CommonTrdTempDto();
        trdTempDto.setPs(entrustDto.getPs());
        trdTempDto.setTradeMarket(entrustDto.getTradeMarket());
        trdTempDto.setPortfolioId(entrustDto.getPortfolioId());
        trdTempDto.setSecManageAcctId(dealDto.getSecAcctId());
        trdTempDto.setCcy(dealDto.getCcy());
        trdTempDto.setEntrustId(entrustDto.getEntrustId());

        InfContractDetailfile infContractDetailfile = new InfContractDetailfile();
        infContractDetailfile.setEntrustId(entrustDto.getEntrustId());

        if (StringUtil.equals(DictCodeListExt3.交易方向_正回购.getValue(), trdTempDto.getPs())
                || StringUtil.equals(DictCodeListExt3.交易方向_逆回购.getValue(), trdTempDto.getPs())) {
            trdTempDto.setTradeAmt(baseTradeProductDealDtos.get(0).getTrdProductDeal().getTradeAmt());
            trdTempDto.setTradeId(baseTradeProductDealDtos.get(0).getTrdProductDeal().getTradeId());
            trdTempDto.setFinprodId(baseTradeProductDealDtos.get(0).getTrdProductDeal().getFinprodId());
            trdTempDto.setHgShareAmt(baseTradeProductDealDtos.get(0).getTrdProductDeal().getShareAmt());
            infContractDetailfile.setShareAmt(baseTradeProductDealDtos.get(0).getTrdProductDeal().getShareAmt());
            infContractDetailfile.setTradeAmt(baseTradeProductDealDtos.get(0).getTrdProductDeal().getTradeAmt());
//            infContractDetailfile.setFinprodId(entrustDto.getFinprodId());
            infContractDetailfile.setDealNo(baseTradeProductDealDtos.get(0).getTrdProductDeal().getFinprodId());
            infContractDetailfile.setvTradeId(baseTradeProductDealDtos.get(0).getTrdProductDeal().getFinprodId());
        }
        else {
            if (baseTradeProductDealDtos.get(0).getTrdFinProductDealTotal() != null) {
                trdTempDto.setTradeAmt(baseTradeProductDealDtos.get(0).getTrdFinProductDealTotal().getTradeAmt());
                trdTempDto.setTradeId(baseTradeProductDealDtos.get(0).getTrdFinProductDealTotal().getTotleTradeId());
                trdTempDto.setFinprodId(baseTradeProductDealDtos.get(0).getTrdFinProductDealTotal().getFinprodId());
                trdTempDto.setHgShareAmt(baseTradeProductDealDtos.get(0).getTrdFinProductDealTotal().getShareAmt());
                infContractDetailfile.setShareAmt(baseTradeProductDealDtos.get(0).getTrdFinProductDealTotal().getShareAmt());
                infContractDetailfile.setTradeAmt(baseTradeProductDealDtos.get(0).getTrdFinProductDealTotal().getTradeAmt());
//                infContractDetailfile.setFinprodId(entrustDto.getFinprodId());
                infContractDetailfile.setDealNo(baseTradeProductDealDtos.get(0).getTrdFinProductDealTotal().getFinprodId());
                infContractDetailfile.setvTradeId(baseTradeProductDealDtos.get(0).getTrdFinProductDealTotal().getFinprodId());
            }
        }

        if (!StringUtil.isEmptyList(baseTradeProductDealDtos.get(0).getStlPayOrdersList())) {
            BussTrdTransferOrderDto bussTrdTransferOrderDto = new BussTrdTransferOrderDto();
            BeanUtil.copyPropertiesIgnoreNull(bussTrdTransferOrderDto, baseTradeProductDealDtos.get(0).getStlPayOrdersList().get(0));

            List<BussTrdTransferOrderDto> bussTrdTransferOrderList = new ArrayList<>();
            bussTrdTransferOrderList.add(bussTrdTransferOrderDto);
            trdTempDto.setBussTrdTransferOrder(bussTrdTransferOrderList);
        }

//        // 质押券信息
//        FinDealPledgeKey finDealPledgeKey = new FinDealPledgeKey();
//        finDealPledgeKey.setFinprodId(entrustDto.getFinprodId());
//        finDealPledgeKey.setBranch(0L);
//        FinDealPledge finDealPledge = exchangeTransMatchCommonMapper.selectFinDealPledgeByPrimaryKey(finDealPledgeKey);
//        trdTempDto.setFinDealPledge(finDealPledge);

        // 质押券置换换出信息
        if (finPledgeDisplaceDetailHuanChu != null) {
            List<FinPledgeDispDetailTemp> finPledgeDispDetailTempList = new ArrayList<>();
            FinPledgeDispDetailTemp finPledgeDispDetailTemp = new FinPledgeDispDetailTemp();
            BeanUtil.copyPropertiesIgnoreNull(finPledgeDispDetailTemp, finPledgeDisplaceDetailHuanChu);
            finPledgeDispDetailTempList.add(finPledgeDispDetailTemp);
            trdTempDto.setFinPledgeDispDetailTempList(finPledgeDispDetailTempList);
        }

        this.buildPositionBaseData(trdTempDto, infContractDetailfile, baseTradeProductDealDtos, firstFinDealPledgeTempList, finPledgeDisplaceResultTempList);

        // 场景参数设置
        PostionParameter parameter = new PostionParameter();
        parameter.setPs(trdTempDto.getPs());
        parameter.setTradeMarket(trdTempDto.getTradeMarket());
        parameter.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
        parameter.setOperationType("NEW");
        parameter.setTradeType("03");
        parameter.setBusinessType(DictCodeList.回购类型_质押式协议回购.getValue().concat(trdTempDto.getFinprodType2()));
//        // 数据来源设置
//        if (StringUtil.equals(trdTempDto.getPs(), DictCodeListExt3.交易方向_解除质押.getValue())){
//            InfContractDetailfile infContractDetailfile = (InfContractDetailfile)trdTempDto.getData();
//            BigDecimal faceValue = trdCommonBaseService.getFaceValue(infContractDetailfile.getFinprodId(), infContractDetailfile.getTradeDate());
//            trdTempDto.setTradeAmt((faceValue == null ? BigDecimal.valueOf(100) : faceValue).multiply(infContractDetailfile.getShareAmt()));
//        }
        trdTempDto.setIsFinish(DictCodeList.是否_是.getValue());
        trdTempDto.setTradeDate(iPlatformDate.getSysDate());
        if (StringUtil.equals(trdTempDto.getPs(),DictCodeListExt3.交易方向_到期续做.getValue()) && StringUtil.isNotEmpty(trdTempDto.getRedoType())){
            parameter.setPs(parameter.getPs()+trdTempDto.getRedoType());
        }

        // add by etliu 20220116 start
        // GZYHTZJY-1145: 【场内对账清结算1231】深交所协议回购首期交易，日终头寸更新有误【解决日期：2022-01-14】】
        if (StringUtil.isNotBlank(trdTempDto.getFinprodId())) {
            trdTempDto.setPledgeInvAim(commonPositionService.selectHoldInvAim(trdTempDto.getPortfolioId(), trdTempDto.getFinprodId()));
        }
        if (trdTempDto.getFinDealPledge() != null && StringUtil.isNotBlank(trdTempDto.getFinDealPledge().getPledgeFinprodId())) {
            trdTempDto.setFinDealPledgePledgeInvAim(commonPositionService.selectHoldInvAim(trdTempDto.getPortfolioId(), trdTempDto.getFinDealPledge().getPledgeFinprodId()));
        }
        if (trdTempDto.getData() != null && StringUtil.isNotBlank(((InfContractDetailfile) trdTempDto.getData()).getFinprodId())) {
            trdTempDto.setDataPledgeInvAim(commonPositionService.selectHoldInvAim(trdTempDto.getPortfolioId(), ((InfContractDetailfile) trdTempDto.getData()).getFinprodId()));
        }
        if (!StringUtil.isEmptyList(trdTempDto.getFinPledgeDispDetailTempList())
                && StringUtil.isNotBlank(trdTempDto.getFinPledgeDispDetailTempList().get(0).getFinprodId())) {
            trdTempDto.setFinPledgeDispDetailPledgeInvAim(commonPositionService.selectHoldInvAim(trdTempDto.getPortfolioId(), trdTempDto.getFinPledgeDispDetailTempList().get(0).getFinprodId()));
        }
        // add by etliu 20220116 end

        parameter.setCustom(trdTempDto);
        logger.info("持仓请求报文:" + JsonUtils.object2Json(parameter));
        commonPositionService.process(parameter);
    }

    private void buildPositionBaseData(CommonTrdTempDto trdTempDto, InfContractDetailfile infContractDetailfile,
                                       List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtos,
                                       List<FinDealPledge> firstFinDealPledgeTempList,
                                       List<FinPledgeDisplaceResult> finPledgeDisplaceResultTempList) {
        // 构造开始----------------------------------------------------------
        BeanUtil.copyPropertiesIgnoreNull(trdTempDto, infContractDetailfile);
        trdTempDto.setAssetType(DictCodeList.回购类型_质押式协议回购.getValue());
        trdTempDto.setData(infContractDetailfile);
        PositionBaseData positionBaseData = new PositionBaseData();
        trdTempDto.setPositionBaseData(positionBaseData);
        // 首期成交编号
        String finprodId = infContractDetailfile.getvTradeId();
        // 指令编号
        String entrustId = infContractDetailfile.getEntrustId();
        // 指令信息
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        positionBaseData.setEtrEntrust(etrEntrust);
        // 交易对手
        if (StringUtil.isEmpty(trdTempDto.getCounterId()) && etrEntrust != null){
            trdTempDto.setCounterId(etrEntrust.getCounterId());
        }
        // 回购拆借方向
        if (etrEntrust != null){
            trdTempDto.setFinprodType2(etrEntrust.getFinprodType2());
        }else {
            trdTempDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        }
        // 指令附属信息
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustId);
        EtrEntrustAddDto etrEntrustAddDto = new EtrEntrustAddDto();
        if (etrEntrustAdd != null){
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAddDto,etrEntrustAdd);
//            if (etrEntrustAdd.getTradeId() != null) {
//                finprodId = etrEntrustAdd.getTradeId();
//            }
        }
        etrEntrustAddDto.setmTradeAmt(trdTempDto.getTradeAmt());
        positionBaseData.setEtrEntrustAddDto(etrEntrustAddDto);
        // 首期主键为空
        if (StringUtil.isEmpty(finprodId)) {
            return;
        }
        // 首期信息
//        trdTempDto.setFinprodId(finprodId);
        // 查询首期交易类金融产品数据
        FinTradeProductKey finTradeProductKey = new FinTradeProductKey();
        finTradeProductKey.setFinprodId(finprodId);
        finTradeProductKey.setBranch(0L);
        FinTradeProduct firstFinTradeProduct = finTradeProductMapper.selectByPrimaryKey(finTradeProductKey);
        FinTradeProductDto finTradeProductDto = new FinTradeProductDto();

//        String hgsqFinprodId = null;

        if (firstFinTradeProduct != null){
            BeanUtil.copyPropertiesIgnoreNull(finTradeProductDto,firstFinTradeProduct);
        }
//        else {
//            // 根据首期成交编号，查询首期交易的交易编号
//            Map<String, Object> paramMap = new HashMap<>();
//            paramMap.put("fTradeId", finprodId);
//            hgsqFinprodId = exchangeTransMatchCommonMapper.selectHgsqFinprodId(paramMap);
//            if (StringUtil.isNotBlank(hgsqFinprodId)) {
//                trdTempDto.setFinprodId(hgsqFinprodId);
//                finprodId = hgsqFinprodId;
//
//                finTradeProductKey.setFinprodId(hgsqFinprodId);
//                firstFinTradeProduct = finTradeProductMapper.selectByPrimaryKey(finTradeProductKey);
//                if (firstFinTradeProduct != null){
//                    BeanUtil.copyPropertiesIgnoreNull(finTradeProductDto,firstFinTradeProduct);
//                }
//            }
//        }
        positionBaseData.setFinTradeProductDto(finTradeProductDto);
        // 质押券信息
        FinDealPledgeKey finDealPledgeKey = new FinDealPledgeKey();
        finDealPledgeKey.setFinprodId(finprodId);
        finDealPledgeKey.setBranch(0L);
        FinDealPledge finDealPledge = exchangeTransMatchCommonMapper.selectFinDealPledgeByPrimaryKey(finDealPledgeKey);
        if (finDealPledge == null && !StringUtil.isEmptyList(firstFinDealPledgeTempList)) {
            finDealPledge = firstFinDealPledgeTempList.get(0);
        }
        positionBaseData.setFinDealPledge(finDealPledge);

        if (finDealPledge != null) {
            infContractDetailfile.setFinprodId(finDealPledge.getPledgeFinprodId());
            infContractDetailfile.setShareAmt(finDealPledge.getPledgeNumber());
        }

        trdTempDto.setFinDealPledge(finDealPledge);

        // 正回购到期续做
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), trdTempDto.getFinprodType2())
                && StringUtil.equals(DictCodeListExt3.交易方向_到期续做.getValue(), trdTempDto.getPs())) {
            this.handleRepoT56(trdTempDto, infContractDetailfile, baseTradeProductDealDtos, finTradeProductDto);
        }
        // 逆回购到期续做
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), trdTempDto.getFinprodType2())
                && StringUtil.equals(DictCodeListExt3.交易方向_到期续做.getValue(), trdTempDto.getPs())) {
            if (etrEntrustAddDto != null){
                // 首期和其他方做，到期续做和本方做,
                if (StringUtil.equals(etrEntrustAddDto.getRedoType(),"1") || StringUtil.isEmpty(firstFinTradeProduct.getFinprodId())) {
                    trdTempDto.setRedoType("_1");
                }
                else {
                    // 首期和本方做，到期续做和本方做
                    trdTempDto.setRedoType("_0");
                    this.handleRepoT56(trdTempDto, infContractDetailfile, baseTradeProductDealDtos, finTradeProductDto);
                }
            }
        }
    }

    /**
     * 处理到期续做逻辑
     * @param trdTempDto
     * @param infContractDetailfile
     * @param baseTradeProductDealDtos
     */
    private void handleRepoT56(CommonTrdTempDto trdTempDto, InfContractDetailfile infContractDetailfile,
                               List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtos,
                               FinTradeProductDto finTradeProductDto) {
        // 到期结算金额需要取交易表中交易方向为收息到账、还本到账的数据中的交易金额的汇总
        BigDecimal mTradeAmt = BigDecimal.ZERO;
        for (ExchangeTransMatchBaseTradeDto x : baseTradeProductDealDtos) {
            if (StringUtil.equals(DictCodeList.交易方向_收息到账.getValue(), x.getTrdProductDeal().getPs())
                    || StringUtil.equals(DictCodeList.交易方向_还本到账.getValue(), x.getTrdProductDeal().getPs())) {
                mTradeAmt = ArithmeticUtil.doAdd(mTradeAmt, x.getTrdProductDeal().getTradeAmt());
            }
            else if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), x.getTrdProductDeal().getPs())) {
                infContractDetailfile.setDealNo(x.getTrdProductDeal().getFinprodId());
                trdTempDto.setHgShareAmt(x.getTrdProductDeal().getShareAmt());
                if (!StringUtil.isEmptyList(x.getFinDealPledgeList())) {
                    infContractDetailfile.setFinprodId(x.getFinDealPledgeList().get(0).getPledgeFinprodId());
                    infContractDetailfile.setShareAmt(x.getFinDealPledgeList().get(0).getPledgeNumber());
                }
                infContractDetailfile.setTradeAmt(x.getTrdProductDeal().getTradeAmt());

                if (!StringUtil.isEmptyList(x.getStlPayOrdersList())) {
                    BussTrdTransferOrderDto bussTrdTransferOrderDto = new BussTrdTransferOrderDto();
                    BeanUtil.copyPropertiesIgnoreNull(bussTrdTransferOrderDto, x.getStlPayOrdersList().get(0));

                    List<BussTrdTransferOrderDto> bussTrdTransferOrderList = new ArrayList<>();
                    bussTrdTransferOrderList.add(bussTrdTransferOrderDto);
                    trdTempDto.setBussTrdTransferOrder(bussTrdTransferOrderList);
                }
            }
        }
        finTradeProductDto.setmTradeAmt(mTradeAmt);
    }
    // modify by etliu 20220110 end
}
