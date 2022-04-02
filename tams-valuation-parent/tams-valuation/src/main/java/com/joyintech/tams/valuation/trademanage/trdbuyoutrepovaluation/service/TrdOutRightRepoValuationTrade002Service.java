/*
 * 文件名：TrdLendInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：杨智 修改时间：2018年10月10日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.service;

import com.joyintech.calculation.bonds.constant.Basis;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.RepoNStandardCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.RepurchaseDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.repdeal.RepurchaseDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.base.service.baseinfo.repoNstandard.RepDealAssetTemplateRecService;
import com.joyintech.tams.valuation.common.interf.base.service.baseinfo.repoNstandard.RepDealSecSubjectTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValRepDealAsset;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValRepDealSec;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValRepDealSecSubject;
import com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade001Dto;
import com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade001Result;
import com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade002Result;
import com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TrdOutRightRepoValuationTrade002Service extends BaseService<TrdOutRightRepoValuationTrade001Dto> implements BaseProcessService<TrdOutRightRepoValuationTrade001Dto> {


    @Autowired
    private RepurchaseDealTemplateRecService repurchaseDealTemplateRecService;

    /**
     * 债券质押式回购标的信息 模板处理Service
     */
    @Autowired
    private RepDealSecSubjectTemplateRecService repDealSecSubjectTemplateRecService;

    /**
     * 债券回购质押品信息 模板处理Service
     */
    @Autowired
    private RepDealAssetTemplateRecService repDealAssetTemplateRecService;
    /**
     * 产品代码下拉框
     * @auther: Administrator
     * @date: 2021/12/29 17:10
     * @param:
     * @return:
     */
    public Page<SelectOptionDto> findPortfolioId(Map<String, Object> params) {
        return restTemplate.postForPage(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOutRightRepoValuationTrade001/findPortfolioId",
                params, SelectOptionDto.class);
    }

    /**
     * 资产代码下拉框
     * @auther: Administrator
     * @date: 2021/12/29 17:10
     * @param:
     * @return:
     */
    public Page<SelectOptionDto> findFinprodId(Map<String, Object> params) {
        return restTemplate.postForPage(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOutRightRepoValuationTrade001/findFinprodId",
                params, SelectOptionDto.class);
    }


    /**
     * 根据交易编号查询详情
     */
    public TrdOutRightRepoValuationTrade001Dto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOutRightRepoValuationTrade001/queryTradeInfoByTradeId",
                tradeId, TrdOutRightRepoValuationTrade001Dto.class);
    }

    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createPlanTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOutRightRepoValuationTrade001/createPlanTradeId",
                null, String.class);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdOutRightRepoValuationTrade001Dto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdOutRightRepoValuationTrade001Result trdOutRightRepoValuationTrade001Result = new TrdOutRightRepoValuationTrade001Result();
        trdOutRightRepoValuationTrade001Result = generateEntity01(entity);
        TrdOutRightRepoValuationTrade002Result trdOutRightRepoValuationTrade002Result = new TrdOutRightRepoValuationTrade002Result();
        trdOutRightRepoValuationTrade002Result = generateEntity02(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdOutRightRepoValuationTrade001/insert01" , trdOutRightRepoValuationTrade001Result, Integer.class);
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdOutRightRepoValuationTrade001/insert02" , trdOutRightRepoValuationTrade002Result, Integer.class);
            TrdOutRightRepoValuationTradeResult trdOutRightRepoValuationTradeResult = new TrdOutRightRepoValuationTradeResult();
            trdOutRightRepoValuationTradeResult = generateEntity(entity);
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdOutRightRepoValuationTrade001/" + action, trdOutRightRepoValuationTradeResult, Integer.class);
        }
        else {
            action = "update";
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdOutRightRepoValuationTrade001/update01" , trdOutRightRepoValuationTrade001Result, Integer.class);
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdOutRightRepoValuationTrade001/update02" , trdOutRightRepoValuationTrade002Result, Integer.class);
            TrdOutRightRepoValuationTradeResult trdOutRightRepoValuationTradeResult = new TrdOutRightRepoValuationTradeResult();
            trdOutRightRepoValuationTradeResult = generateEntity(entity);
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdOutRightRepoValuationTrade001/" + action, trdOutRightRepoValuationTradeResult, Integer.class);
        }
    }

    //封装实体类01
    public TrdOutRightRepoValuationTrade001Result generateEntity01(TrdOutRightRepoValuationTrade001Dto dto){
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        //将页面输入值赋值给接口实体类

        List<ValRepDealSecSubject> listValRepDealSecSubject = new ArrayList<>();
        ValRepDealSecSubject entity = new ValRepDealSecSubject();
        BeanUtil.copyPropertiesIgnoreNull(entity,dto);
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setTradeId(dto.getTradeId());  //交易编号
        if(dto.getPs().equals("03")){
            entity.setTradeType(DictCodeListExt1.金融产品类型_债券买断式正回购.getValue());
        }else if(dto.getPs().equals("04")){
            entity.setTradeType("F05");
        }
        entity.setDataActionSign("01");//数据动作标志
//        entity.setTermKind("R007"); //期限品种
        entity.setBusiType("05"); //业务类型 买断式回购
        entity.setVdate(dto.getVdate());//起息日
        entity.setMdate(dto.getMdate());//到期日
        entity.setBasis("A/365");  //计息基础
        entity.setAssetClass(dto.getPs()); //资产类型
        entity.setRateType("01"); //利率类型  固定
        entity.setRedRate(dto.getRebackPrice().divide(new BigDecimal(100))); //回购利率
        entity.setTradeMarket("01"); //交易市场 银行间
        entity.setCapital(dto.getPrinAmt());  //名义本金(券面总额)
        entity.setmIntAmt(dto.getIntAmt());  //到期利息   回购利息
        entity.setmPrinAmt(new BigDecimal(0)); //到期本金
        entity.setmTradeAmt(dto.getMfprice()); //到期全价总额 本金+利息
        entity.setCcy(dto.getCcy());//币种
        entity.setCapiIncomeFeature("01"); //本金收益特征 01保本

        entity.setRegistOrg("01"); //登记托管结构
        entity.setPledgeSecNum(new Long(1)); //押品只数
        entity.setTradeMarket("01"); //交易场所  01银行间
        entity.setVpayDate(new Date());//首期交付日
        entity.setMpayDate(new Date());//到期交付日
        entity.setCounterId(dto.getCounterId());




        listValRepDealSecSubject.add(entity);

        Map<String, Object> map = repDealSecSubjectTemplateRecService.handleValBotAstInfo(listValRepDealSecSubject);

        // 定义返回变量
        RepoNStandardCommonResult result = new RepoNStandardCommonResult();

        // 交易类金融产品List
        List<FinTradeProduct> listValTradeFinProduct = (List<FinTradeProduct>)map.get("ValTradeFinProduct");
        result.setListValTradeFinProduct(listValTradeFinProduct);
        // 定义 金融产品分类List
        List<FinProductType> listValFinProductType = (List<FinProductType>)map.get("ValFinProductType");
        result.setListValFinProductType(listValFinProductType);

        // 定义 交易类金融产品附属表List
        List<FinTradeProductAdd> listFinTradeProductAdd = (List<FinTradeProductAdd>)map.get("FinTradeProductAdd");
        result.setListFinTradeProductAdd(listFinTradeProductAdd);
        /**add by wujie on 20190420 start**/
        //金融产品估值参数表
        List<FinProductValuationPara> listFinProductValuationPara = (List<FinProductValuationPara>) map.get("FinProductValuationPara");
        result.setListFinProductValuationPara(listFinProductValuationPara);
        /**add by wujie on 20190420 end**/

        TrdOutRightRepoValuationTrade001Result rst = new TrdOutRightRepoValuationTrade001Result(result);
        rst.setTrdOutRightRepoValuationTrade001Dto(dto);
        return rst;
    }



    //封装实体类02
    public TrdOutRightRepoValuationTrade002Result generateEntity02(TrdOutRightRepoValuationTrade001Dto dto){
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        //将页面输入值赋值给接口实体类
        List<ValRepDealAsset> listValRepDealAsset=new ArrayList<>();
        ValRepDealAsset entity = new ValRepDealAsset();
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setTradeId(dto.getTradeId());  //交易编号
        entity.setDataActionSign("01"); //数据标记动作 新建
        if(dto.getPs().equals("03")){
            entity.setTradeType(DictCodeListExt1.金融产品类型_债券买断式正回购.getValue());
        }else if(dto.getPs().equals("04")){
            entity.setTradeType("F05");
        }
        entity.setAssetId(dto.getFinprodMarketId()); //资产代码
        entity.setPledgeFaceValue(new BigDecimal(0)); //债券当前面值
        entity.setPledgeNumber(new BigDecimal(0)); //债券数量
        entity.setPledgeRatio(new BigDecimal(0)); //质押率
        entity.setPledgeAmt(new BigDecimal(0)); //质押金额
        listValRepDealAsset.add(entity);
        Map<String, Object> map=repDealAssetTemplateRecService.handleValBotAstInfo(listValRepDealAsset);
        // 定义返回变量
        RepoNStandardCommonResult result = new RepoNStandardCommonResult();
        // 质押品信息List
        List<FinDealPledge> ListFinDealPledge = (List<FinDealPledge>)map.get("listFinDealPledge");
        result.setListFinDealPledge(ListFinDealPledge);

        TrdOutRightRepoValuationTrade002Result rst = new TrdOutRightRepoValuationTrade002Result(result);
        rst.setTrdOutRightRepoValuationTrade001Dto(dto);
        return rst;
    }

    //封装实体类
    public TrdOutRightRepoValuationTradeResult generateEntity(TrdOutRightRepoValuationTrade001Dto dto){
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        //将页面输入值赋值给接口实体类
        List<ValRepDealSec> listValBotAstTrade=new ArrayList<>();
        ValRepDealSec entity = new ValRepDealSec();
        BeanUtil.copyPropertiesIgnoreNull(entity,dto);
        //比较界面和接口字段差异，并手动给接口字段赋值

        entity.setProdId(dto.getPortfolioId()); //产品代码
        entity.setfTradeId(dto.getOutTradeId()); //外部交易编号
        entity.setDataActionSign("01");//数据动作标志
        entity.setTradeId(dto.getTradeId());  //交易编号
//        entity.setTermKind("R007"); //期限品种
        //DictCodeList.回购期限品种_R007.getValue()
        entity.setTradeMarket("01"); //交易市场 银行间
        entity.setPs(dto.getPs()); //交易类型
        entity.setTrader("yc"); //交易员
        entity.setTradeDate(dto.getTradeDate());//交易日
        entity.setSettleDate(dto.getTradeDate()); //交割日
        entity.setVdate(dto.getVdate());//起息日
        entity.setMdate(dto.getMdate());//到期日
        entity.setFirstAmt(dto.getFprice()); //首次结算金额
        entity.setEndAmt(dto.getMfprice()); //到期结算金额 卖出、到期时：到期结算金额字段为必填项
        entity.setIntAmt(dto.getIntAmt());//利息金额
        entity.setCcy(dto.getCcy());//币种
        entity.setCapiProfFeature("01"); //本金收益特征 01保本
        entity.setCounterId(dto.getCounterId());//交易对手
        entity.setCounterType("01"); //交易对手类型 01 内部
        entity.setRegistOrg("01"); //登记托管机构 01
        entity.setIsTradeFee("N");
        entity.setIsStampDuty("N");
        entity.setIsBrokerage("N");
        entity.setIsCertfFee("N");
        entity.setIsStlAmt("N");
        entity.setIsCommission("N");
        entity.setDealFeeAmt(dto.getDealFeeAmt()); //交易手续费
        entity.setIsDealFeeAmt("N");
        entity.setStlFeeAmt(dto.getSettFeeAmt()); //结算手续费
        entity.setIsStlFeeAmt("N");
        entity.setIsChlFeeAmt("N");//通道费是否随交易支付
        entity.setpTradeId(dto.getTradeId()); //原交易编号
        entity.setIsClean("N");//有无清算指令
        entity.setPaydate(new Date()); //交收日
        entity.setMyAcctNo(dto.getTrdAcctId()); //付款账户
        entity.setCounterAcctNo(null);//收款账户
        entity.setFundSettleDate(null);//资金交收日
        entity.setBusiType("05"); //业务类型
        listValBotAstTrade.add(entity);
        Map<String, Object> map = repurchaseDealTemplateRecService.handleValBotAstTrade(listValBotAstTrade,new Date(),params,null);

        // 定义返回变量
        RepurchaseDealInfoValCommonResult result = new RepurchaseDealInfoValCommonResult();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get("ValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get("ValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get("ValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get("ValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail");
        result.setListValTradeFeeDetail(listValTradeFeeDetail);
        // 账务明细代码表
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get("ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
        // 结算指令
        List<StlOrders> listValOrders = (List<StlOrders>)map.get("listValOrders");
        List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get("listValOrdersExt");
        result.setListValOrders(listValOrders);
        result.setListValOrdersExt(listValOrdersExt);

        TrdOutRightRepoValuationTradeResult rst = new TrdOutRightRepoValuationTradeResult(result);
        rst.setTrdOutRightRepoValuationTrade001Dto(dto);
        return rst;
    }
}
