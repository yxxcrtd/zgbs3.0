/*
 * 文件名：TrdLendInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：杨智 修改时间：2018年10月10日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdrepovaluationtrade.service;

import com.joyintech.framework.base.BaseService;

import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.RepoNStandardCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.RepurchaseDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.repdeal.RepurchaseDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.base.service.baseinfo.repoNstandard.RepDealSecSubjectTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValRepDealSec;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValRepDealSecSubject;
import com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade001Dto;
import com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade001Result;
import com.joyintech.tams.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTrade001Dto;
import com.joyintech.tams.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTrade001Result;
import com.joyintech.tams.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@Service
public class TrdRepoValuationTrade002Service extends BaseService<TrdRepoValuationTrade001Dto> implements BaseProcessService<TrdRepoValuationTrade001Dto> {

    @Autowired
    private RepurchaseDealTemplateRecService repurchaseDealTemplateRecService;

    /**
     * 债券质押式回购标的信息 模板处理Service
     */
    @Autowired
    private RepDealSecSubjectTemplateRecService repDealSecSubjectTemplateRecService;

    /**
     * 根据交易编号查询详情
     */
    public TrdRepoValuationTrade001Dto queryTradeInfoByTradeId(Map<String, Object> params) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdRepoValuationTrade001/queryTradeInfoByTradeId",
                params, TrdRepoValuationTrade001Dto.class);
    }

    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createPlanTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdRepoValuationTrade001/createPlanTradeId",
                null, String.class);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdRepoValuationTrade001Dto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdRepoValuationTrade001Result trdRepoValuationTrade001Result = new TrdRepoValuationTrade001Result();
        trdRepoValuationTrade001Result = generateEntity01(entity);

        //先操作标的接口，再操作交易接口
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdRepoValuationTrade001/insert01" , trdRepoValuationTrade001Result, Integer.class);
            TrdRepoValuationTradeResult trdRepoValuationTradeResult = new TrdRepoValuationTradeResult();
            trdRepoValuationTradeResult = generateEntity(entity);
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdRepoValuationTrade001/" + action, trdRepoValuationTradeResult, Integer.class);
        }
        else {
            action = "update";
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdRepoValuationTrade001/update01" , trdRepoValuationTrade001Result, Integer.class);
            TrdRepoValuationTradeResult trdRepoValuationTradeResult = new TrdRepoValuationTradeResult();
            trdRepoValuationTradeResult = generateEntity(entity);
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdRepoValuationTrade001/" + action, trdRepoValuationTradeResult, Integer.class);
        }

    }

    //封装实体类01
    public TrdRepoValuationTrade001Result generateEntity01(TrdRepoValuationTrade001Dto dto){
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
            entity.setTradeType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue()); //F02
        }else if(dto.getPs().equals("04")){
            entity.setTradeType("F03");  //债券质押式逆回购
        }
        entity.setDataActionSign("01");//数据动作标志
//      entity.setTermKind("R007"); //期限品种
        entity.setBusiType("04"); //业务类型 质押式回购
        entity.setVdate(dto.getVdate());//起息日
        entity.setMdate(dto.getMdate());//到期日
        entity.setBasis("A/365");  //计息基础
        entity.setAssetClass(dto.getPs()); //资产类型
        entity.setRateType("01"); //利率类型  固定
        entity.setRedRate(dto.getRebackPrice().divide(new BigDecimal(100))); //回购利率
        entity.setTradeMarket("01"); //交易市场 银行间
        entity.setCapital(dto.getPrinAmt());  //名义本金(券面总额)
        entity.setmIntAmt(dto.getIntAmt());  //到期利息   应计利息
        entity.setmPrinAmt(new BigDecimal(0)); //到期本金
        entity.setmTradeAmt(new BigDecimal(0)); //到期全价总额 本金+利息
        entity.setCcy(dto.getCcy());//币种
        entity.setCapiIncomeFeature("01"); //本金收益特征 01保本
        entity.setRegistOrg("01"); //登记托管结构
        entity.setPledgeSecNum(new Long(1)); //押品只数
        entity.setTradeMarket("01"); //交易场所  01银行间
        entity.setVpayDate(new Date());//首期交付日
        entity.setMpayDate(new Date());//到期交付日
        entity.setCounterId(dto.getCounterId());
        entity.setTargetBackType(dto.getRepoType());  //回购类型

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

        TrdRepoValuationTrade001Result rst = new TrdRepoValuationTrade001Result(result);
        rst.setTrdRepoValuationTrade001Dto(dto);
        return rst;
    }




    //封装实体类
    public TrdRepoValuationTradeResult generateEntity(TrdRepoValuationTrade001Dto dto){
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
        entity.setDataActionSign("01");//数据动作标志
         entity.setTradeId(dto.getTradeId());  //交易编号
        entity.setfTradeId(dto.getOutTradeId()); //外部成交编号
        entity.setTermKind("R007"); //期限品种
        //DictCodeList.回购期限品种_R007.getValue()
        entity.setTradeMarket(dto.getTradeMarket()); //交易市场

        entity.setPs(dto.getPs()); //交易类型
        entity.setTrader("yc"); //交易员
        entity.setTradeDate(dto.getTradeDate());//交易日
        entity.setSettleDate(dto.getSettleDate()); //交割日
        entity.setVdate(dto.getVdate());//起息日
        entity.setMdate(dto.getMdate());//到期日
        entity.setFirstAmt(new BigDecimal(0)); //首次结算金额
        entity.setEndAmt(new BigDecimal(0)); //到期结算金额 卖出、到期时：到期结算金额字段为必填项
        entity.setIntAmt(dto.getIntAmt());//利息金额
        entity.setCcy(dto.getCcy());//币种
        entity.setCapiProfFeature("01"); //本金收益特征 01保本
        entity.setCounterId(dto.getCounterId());//交易对手
        entity.setCounterType("01"); //交易对手类型 01 内部
        entity.setRegistOrg("02"); //登记托管机构
        entity.setIsTradeFee("N");
        entity.setIsStampDuty("N");
        entity.setBrokerage(dto.getBrokerage()); //经手费
        entity.setIsBrokerage("Y");
        entity.setIsCertfFee("N"); //证管费是否随交易支付
        entity.setStlAmt(dto.getStlAmt()); //结算费
        entity.setIsStlAmt("Y"); //结算费是否随交易支付
        entity.setCommission(dto.getCommission()); //佣金
        entity.setIsCommission("N"); //佣金是否随交易支付
        entity.setDealFeeAmt(dto.getDealFeeAmt()); //交易手续费
        entity.setIsDealFeeAmt("Y");//交易手续费是否随交易支付
        entity.setStlFeeAmt(dto.getSettFeeAmt()); //结算手续费
        entity.setIsStlFeeAmt("N"); //结算手续费是否随交易支付

        entity.setIsChlFeeAmt("N");//通道费是否随交易支付
        entity.setpTradeId(dto.getTradeId()); //原交易编号
        entity.setIsClean("N");//有无清算指令
        entity.setPaydate(new Date()); //交收日
        entity.setMyAcctNo(dto.getTrdAcctId()); //付款账户
        entity.setCounterAcctNo(null);//收款账户
        entity.setFundSettleDate(null);//资金交收日
        entity.setBusiType("04"); //业务类型


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

        TrdRepoValuationTradeResult rst = new TrdRepoValuationTradeResult(result);
        rst.setTrdRepoValuationTrade001Dto(dto);
        return rst;
    }

}
