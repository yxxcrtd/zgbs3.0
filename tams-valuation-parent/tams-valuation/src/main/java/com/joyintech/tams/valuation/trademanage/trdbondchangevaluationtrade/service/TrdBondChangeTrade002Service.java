package com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.FundDealInfoForInterfRecCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund.FundChangeDealRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.sec.SecVestAssetDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValFundChangeDeal;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecVestAssetDeal;
import com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTradeDto;
import com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTradeFundResult;
import com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TrdBondChangeTrade002Service extends CommonExportService<TrdBondChangeTradeDto> implements BaseProcessService<TrdBondChangeTradeDto> {

    @Autowired
    private SecVestAssetDealTemplateRecService secVestAssetDealTemplateRecService;

    @Autowired
    private FundChangeDealRecService fundChangeDealRecService;

    /**
     * 通过资产代码查询资产简称和交易场所
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public FinProduct selectInfoBySecId(Map<String, Object> map) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondChangeTrade002/selectInfoBySecId", map,
                FinProduct.class);
    }

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public TrdBondChangeTradeDto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondChangeTrade002/queryTradeInfoByTradeId", tradeId,
                TrdBondChangeTradeDto.class);
    }

    /**
     *
     * 交易方向为债转股时，转出证券代码下拉框显示可转债资产
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getSecExgId(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondChangeTrade002/getSecExgId" , map,
                SelectOptionDto.class);
    }

    /**
     *
     * 交易方向为基金转换时，转出证券代码下拉框显示基金
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFundId(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondChangeTrade002/getFundId" , map,
                SelectOptionDto.class);
    }

    /**
     * 展示股票代码和基金
     *
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getCAssetId(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME
                        + "/TrdBondChangeTrade002/getCAssetId" , map,
                SelectOptionDto.class);
    }

    //自增序列号
    public int querySequences(){
        return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdBondChangeTrade002/querySequences", null,Integer.class);
    }

    @Override
    public void doComplete(TrdBondChangeTradeDto trdBondChangeTradeDto, OperationType operationType) {
        String action = StringUtil.EMPTY;

        TrdBondChangeTradeResult result = null;
        TrdBondChangeTradeFundResult rst = null;

        if(DictCodeListExt1.交易方向_债转股.getValue().equals(trdBondChangeTradeDto.getPs())){
            if (OperationType.NEW.equals(operationType)) {
                action = "insert";
            }
            else {
                action = "update";
            }
            result = generateEntity(trdBondChangeTradeDto);
            restTemplate.postForObject(
                    ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondChangeTrade002/" + action, result,
                    Integer.class);
        }else if(DictCodeList.交易方向_上市流通.getValue().equals(trdBondChangeTradeDto.getPs())){

        }else if(DictCodeListExt3.交易方向_转换申购确认.getValue().equals(trdBondChangeTradeDto.getPs())){
            if (OperationType.NEW.equals(operationType)) {
                action = "insertFund";
            }
            else {
                action = "updateFund";
            }
            rst = generateEntity1(trdBondChangeTradeDto);
            restTemplate.postForObject(
                    ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondChangeTrade002/" + action, rst,
                    Integer.class);
        }
    }

    //封装实体类(债转股)
    public TrdBondChangeTradeResult generateEntity(TrdBondChangeTradeDto dto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getProdId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        // 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();

        //将页面输入值赋值给接口实体类
        List<ValSecVestAssetDeal> listValSecVestAssetDeal = new ArrayList<ValSecVestAssetDeal>();
        ValSecVestAssetDeal entity = new ValSecVestAssetDeal();
        //BeanUtil.copyPropertiesIgnoreNull(entity,dto);
        entity.setTradeId(dto.getTradeId());//交易编号
        entity.setProdId(dto.getProdId());//产品代码
        entity.setSecId(dto.getoAssetId());//债券代码
        entity.setSecAbbr(dto.getoAssetAbbr());//债券简称
        entity.setDealDate(dto.getTradeDate());//交易日
        entity.setSettdateDate(dto.getTradeDate());//todo
        entity.setPs(dto.getPs());//交易方向
        entity.setDealNum(dto.getoTradeNum());//成交数量
        entity.setCcy(dto.getCcy());//币种
        entity.setInvestAim(dto.getInvAim());//投资目的
        entity.setTradeAmt(dto.getoTradeAmt());//交易金额 todo
        entity.setSharesCode(dto.getcAssetId());//转入证券代码
        entity.setSharesNum(dto.getcTradeNum());//转入数量
        entity.setExpConValue(dto.getcTradeAmt());//转入金额
        entity.setMyAcctNo(dto.getAcctNo());//资金账户
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setAssetId("F01_"+dto.getoAssetId());//资产代码
        entity.setDataActionSign("01");//数据动作标志
        entity.setTradeMarket("01");//交易场所
        entity.setStlForm(null);//结算方式
        entity.setParValue(new BigDecimal(0));//单位面值
        entity.setFeeAmtUnpay(new BigDecimal(0));//未随交易支付费用
        entity.setYield(new BigDecimal(0));//到期收益率
        entity.setCounterId(null);//交易对手
        entity.setCounterType(null);//交易对手类型
        entity.setTrader(null);//交易员
        entity.setSecManageAcctId(null);//证券托管户
        entity.setCprice(new BigDecimal(0));//净价
        entity.setFprice(new BigDecimal(0));//全价
        entity.setUnitInt(new BigDecimal(0));//应收利息
        entity.setIntAmt(new BigDecimal(0));//应收利息总额 todo
        entity.setCpriceAmt(new BigDecimal(0));//净价总额 todo
        entity.setFpriceAmt(new BigDecimal(0));//全价总额 todo
        entity.setIsStampDuty("Y");//印花税是否随交易支付
        entity.setIsBrokerage("Y");//经手费是否随交易支付
        entity.setIsCertfFee("Y");//证管费是否随交易支付
        entity.setIsStlAmt("Y");//结算费是否随交易支付
        entity.setIsCommission("Y");//佣金是否随交易支付
        entity.setIsDealFeeAmt("N");//交易手续费是否随交易支付
        entity.setIsStlFeeAmt("N");//结算手续费是否随交易支付
        entity.setIsChlFeeAmt("Y");//通道费是否随交易支付
        entity.setRegistAgency(null);//登记结算机构
        entity.setIsClean("N");//有无清算指令
        entity.setExeYield(new BigDecimal(0));//行权收益率
        entity.setSharesTradeMarket(null);//流通市场
        entity.setSharesType("F10");//正股类型

        listValSecVestAssetDeal.add(entity);

        //调用接口处理方法
        Map<String, Object> map = secVestAssetDealTemplateRecService.handleValSecVestAssetDeal(listValSecVestAssetDeal,new Date(),params,null);

        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get(
                "ValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get(
                "ValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        //金融产品实物交易
        List<TrdFinProductBarter> listTrdFinProductBarter = (List<TrdFinProductBarter>)map.get(
                "ValFinProductBarter");
        result.setListValFinProductBarter(listTrdFinProductBarter);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
                "ValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                "ValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        /**add by wujie on 20180712 start**/
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
                "ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
        /**add by wujie on 20180712 end**/

        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>)map.get(
                "ValTradeFeeDetail");
        result.setListValTradeFeeDetail(listValTradeFeeDetail);
        //结算指令表List
        List<StlOrders> valOrdersList=(List<StlOrders>)map.get("valOrdersList");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("valOrdersExtList");
        result.setListValOrdersExt(valOrdersExtList);

        TrdBondChangeTradeResult rst = new TrdBondChangeTradeResult(result);
        rst.setTrdBondChangeTradeDto(dto);
        return rst;

    }

    //封装实体类(基金转换)
    public TrdBondChangeTradeFundResult generateEntity1(TrdBondChangeTradeDto dto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getProdId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();

        List<ValFundChangeDeal> listValFundChangeDeal = new ArrayList<ValFundChangeDeal>();
        ValFundChangeDeal entity = new ValFundChangeDeal();
       //BeanUtil.copyPropertiesIgnoreNull(valFundChangeDeal,dto);
        entity.setTradeId(dto.getTradeId());
        entity.setProdId(dto.getProdId());
        entity.setAssetId(dto.getoAssetId());
        entity.setAssetName(dto.getoAssetAbbr());
        entity.setCcy(dto.getCcy());
        entity.setPs(dto.getPs());
        entity.setTradeDate(dto.getTradeDate());
        entity.setShareAmt(dto.getoTradeNum());
        entity.setPrinAmt(dto.getoTradeAmt());
        entity.setTradeAmt(dto.getoTradeAmt());
        entity.setcAssetId(dto.getcAssetId());
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setDataActionSign("01");
        entity.setTradeMarket("01");
        entity.setUnitCprice(new BigDecimal(1));
        entity.setIntAmt(new BigDecimal(100));

        listValFundChangeDeal.add(entity);

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = fundChangeDealRecService.handleValFundChangeDeal(listValFundChangeDeal,new Date(),params,null);
        if (!map.isEmpty()) {
            // 金融产品交易List
            result.setListValFinProductDeal( (List<TrdProductDeal>)map.get("ValFinProductDeal"));
            // 估值核算交易扩展表List
            result.setListValFinProductDealExt((List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
            // 入账信息表List
            List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get("ValDealAccounting");
            result.setListValDealAccounting(listValDealAccounting);
            // 账务明细表List
            List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get("ValDealAccountingDetail");
            result.setListValDealAccountingDetail(listValDealAccountingDetail);
            // 账务明细代码表List
            List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get( "ValBokDetailId");
            result.setListValBokDetailId(listValBokDetailId);
            //结算指令
            List<StlOrders> listValOrders = (List<StlOrders>)map.get("listValOrders");
            List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get("listValOrdersExt");
            result.setListValOrders(listValOrders);
            result.setListValOrdersExt(listValOrdersExt);
            //交易费用明细表
            result.setListValTradeFeeDetail((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));
        }

        TrdBondChangeTradeFundResult rst = new TrdBondChangeTradeFundResult(result);
        rst.setTrdBondChangeTradeDto(dto);
        return rst;
    }

}
