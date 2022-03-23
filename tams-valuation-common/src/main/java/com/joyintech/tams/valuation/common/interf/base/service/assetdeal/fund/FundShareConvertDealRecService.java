/*
 * 文件名：FundBuyAndSellDealRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValFundShareConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 08_基金份额折算交易
 * 
 * @author yanya
 * @version 1.0 2019年11月16日
 * @see FundShareConvertDealRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FundShareConvertDealRecService {

    @Autowired
    private InterfCommonService interfCommonService;

    /**
     * TemplateAnalysisForXMLService
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;

    /**
     * FundDealInfoForInterfRecCommonResult
     *
     * @param templateSourceFlgDTO TemplateSourceFlgDTO
     * @param acctEntity TemplateOrderInfoReaderResult
     * @return FundDealInfoForInterfRecCommonResult
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public FundDealInfoForInterfRecCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                        TemplateOrderInfoReaderResult acctEntity) {
        FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
        List<ValFundShareConvert> listValFundTrade = new ArrayList<ValFundShareConvert>();
        Map<String,Object> params = acctEntity.getParams();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValFundTrade = returnResult.getT(ValFundShareConvert.class);

        // 获取数据数量
        result.setDataNum(listValFundTrade.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态, DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = this.handleValFundTrade(listValFundTrade,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
        if (!map.isEmpty()) {
            result.setListValFinProductDeal(
                (List<TrdProductDeal>)map.get("ValFinProductDeal"));
            result.setListValFinProductDealExt(
                (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
            /**modify by wujie on 20190118 start
            result.setListInterfProductDealBookCommonDto(
                (List<InterfProductDealBookCommonDto>)map.get(
                    "listInterfProductDealBookCommonDto"));
                    **/
            List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
                "ValDealAccounting");
            result.setListValDealAccounting(listValDealAccounting);

            List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                    "ValDealAccountingDetail");
            result.setListValDealAccountingDetail(listValDealAccountingDetail);

            List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
                    "ValBokDetailId");
            result.setListValBokDetailId(listValBokDetailId);
            /**modify by wujie on 20190118 end**/

            //结算指令
            List<StlOrders> listValOrders = (List<StlOrders>)map.get(
                "listValOrders");
            List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get(
                "listValOrdersExt");
            result.setListValOrders(listValOrders);
            result.setListValOrdersExt(listValOrdersExt);
         // 日志
            result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        return result;
    }

    /**
     * 更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
     *
     * @param listValFundTrade List<ValFundTrade>
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> handleValFundTrade(List<ValFundShareConvert> listValFundTrade,Date resovleDate,Map<String,Object> params,
    		List<ValInterflog> valInterflog) {
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        Map<String, Object> resultMap = new HashMap<>();
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();

        //结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();

        List<ValInterflog> valInterflogList =  new ArrayList<>();

        for (ValFundShareConvert item : listValFundTrade) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(item.getProdId())){
        		continue;
        	}
        	item.setCreateTime(resovleDate);
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(item);
            listValFinProductDeal.add(valFinProductDeal);

            InterfProductDealBookCommonDto interfProductDealBookCommonDto = interfCommonService.dealAccounting(
                    valFinProductDeal, item.getProdId());
            listValDealAccounting.add(interfProductDealBookCommonDto.getBokDealAccounting());
            listValDealAccountingDetail.add(interfProductDealBookCommonDto.getBokDealAccountingDetail());

            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(item,valFinProductDeal,
            		interfProductDealBookCommonDto.getAcctSubjectType());
            valFinProductDealExt.setTradeId(valFinProductDeal.getTradeId());
            listValFinProductDealExt.add(valFinProductDealExt);

            BokDetailId valBokDetailId = interfProductDealBookCommonDto.getBokDetailId();
            //四级科目尾号
            valBokDetailId.setDetailSubjectNoTail(item.getAssetId());
            //四级科目尾号名称
            valBokDetailId.setDetailSubnameTail(item.getAssetName());
            listValBokDetailId.add(valBokDetailId);
            
            Map<String, Object> map = new HashMap<String,Object>();
            if(valFinProductDeal.getTradeAmt() != null && BigDecimal.ZERO.compareTo(valFinProductDeal.getTradeAmt())!= 0){
                map = interfCommonService.instructionGenerate_Interf(valFinProductDeal,null,
                        item.getProdId(), interfProductDealBookCommonDto.getAcctSubjectType(),null);
            }else{
            	map = interfCommonService.instructionGenerate(valFinProductDeal,
                        item.getProdId(), interfProductDealBookCommonDto.getAcctSubjectType(),null);
            }
            if(map.get("ORDERS")!=null){
                listValOrders.addAll((List<StlOrders>)map.get("ORDERS"));
            }
            if(map.get("ORDERS_EXT")!=null){
                listValOrdersExt.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT"));
            }
            if(map.get("ACCOUNTING")!=null){
                listValDealAccounting.addAll((List<BokDealAccounting>)map.get("ACCOUNTING"));
            }
            if(map.get("ACCOUNTING_DETAI")!=null){
                listValDealAccountingDetail.addAll(( List<BokDealAccountingDetail>)map.get("ACCOUNTING_DETAI"));
            }
            if(map.get("BOK_DETAIL_ID")!=null){
                listValBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID"));
            }
            
            ChannelFeeInfoDeal channelFeeInfoDeal = this.getChannelFeeInfoDeal(item, interfProductDealBookCommonDto.getAcctSubjectType());
            if(channelFeeInfoDeal != null){
                Map<String,Object> feedealMap = interfCommonService.assetChlFeeLend(valFinProductDeal, channelFeeInfoDeal);

                if(feedealMap.get("TrdProductDeal")!=null){
                    listValFinProductDeal.add((TrdProductDeal)feedealMap.get("TrdProductDeal"));
                }

                if(feedealMap.get("TrdProductDealExt")!=null){
                    listValFinProductDealExt.add((TrdProductDealAdd)feedealMap.get("TrdProductDealExt"));
                }

                if(feedealMap.get("BokDealAccounting_List")!=null){
                    listValDealAccounting.addAll((List<BokDealAccounting>)feedealMap.get("BokDealAccounting_List"));
                }

                if(feedealMap.get("BokDealAccountingDetail_List")!=null){
                    listValDealAccountingDetail.addAll(( List<BokDealAccountingDetail>)feedealMap.get("BokDealAccountingDetail_List"));
                }

                if(feedealMap.get("BokDetailId_list")!=null){
                    listValBokDetailId.addAll((List<BokDetailId>)feedealMap.get("BokDetailId_list"));
                }

                if(feedealMap.get("StlOrders_List")!=null){
                    listValOrders.addAll((List<StlOrders>)feedealMap.get("StlOrders_List"));
                }
                if(feedealMap.get("StlOrdersExt_List")!=null){
                    listValOrdersExt.addAll((List<StlOrdersExt>)feedealMap.get("StlOrdersExt_List"));
                }
            }
        }
        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);

        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);

        resultMap.put("listValOrders", listValOrders);
        resultMap.put("listValOrdersExt", listValOrdersExt);

        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);

        return resultMap;
    }

    /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValFundShareConvert dto,String acctSubjectType){
        //接口.通道代码为空
        if(dto.getChannelId() == null){
            return null;
        }
        //接口.通道费用空
        if(dto.getChlFeeAmt()==null || dto.getChlFeeAmt().compareTo(BigDecimal.ZERO)==0){
            return null;
        }
        ChannelFeeInfoDeal deal = new ChannelFeeInfoDeal();
        //通道费用 = 接口.通道费用
        deal.setChlFeeAmt(dto.getChlFeeAmt());
        //是否随交易支付 = 接口.通道费是否随交易支付
        deal.setIsPayWithTrade(dto.getIsChlFeeAmt());
        //通道代码 = 接口.通道代码
        deal.setChannelId(dto.getChannelId());
        //产品代码 = 接口.产品代码
        deal.setProdId(dto.getProdId());

        deal.setAcctSubjectId(dto.getProdId() + FinalString.LAYERING_SPLIT + dto.getChannelId());

        deal.setAcctSubjectType(acctSubjectType);

        return deal;
    }

    /**
     * 接收 金融产品交易 T1
     *
     * @param dto ValFundShareConvert
     * @return ValFinProductDeal
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValFundShareConvert dto) {
        TrdProductDeal result = new TrdProductDeal();
        // 交易编号 = A.成交编号
        String tradeId=interfCommonService.getTradeid(dto.getPs(),dto.getTradeDate(),"A");
        result.setTradeId(tradeId);

        // 业务类型 = A.业务类型
        result.setBusiType(dto.getBusiType());
        // 金融产品代码 = A.资产代码
        result.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F06 基金
    	String finprodType = DictCodeListExt1.金融产品类型_基金.getValue();
    	TargetFinProduct item = interfCommonService.getTargetFinProduct(dto.getAssetId());
    	if(item!=null){
    		finprodType = item.getFinprodType();
    	}
    	result.setFinprodType(finprodType);

        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日 = A.交易日
        result.setTradeDate(dto.getTradeDate());
        // 交割日 = A.交易日
        result.setSettleDate(dto.getTradeDate());
        // 交易币种 = A.币种
        result.setCcy(dto.getCcy());
        // 交易份额 = A.交易份额
        result.setShareAmt(dto.getCfmShare());
        // 交易方向 = A.交易方向
        result.setPs(dto.getPs());
        // 交易状态 = A.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 设置默认值
        result.setIsClean(InterfCommonConstant.是否结清_默认);
        // 交易场所=A.交易场所
        result.setTradeMarket(dto.getTradeMarket());
        // 关联系统交易编号 = A.成交编号
        result.setfTradeId(dto.getTradeId());
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        if(result.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	result.setpTradeId(interfCommonService.getFTradeId(result));
        }
        //通道代码 = a.通道代码
        result.setChlId(dto.getChannelId());
        /*add by sunshuaishuai 交易管理 20220323 start*/
        //资金账户
        result.setMyAcctNo(dto.getMyAcctNo());
        /*add by sunshuaishuai 20220323 end*/
        return result;
    }

    /**
     * 接收 估值核算交易扩展表 T2
     *
     * @param dto ValFundShareConvert
     * @return ValFinProductDealExt
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValFundShareConvert dto,TrdProductDeal deal,String finprodType) {
    	TrdProductDealAdd result = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        result.setTradeId(deal.getTradeId());
        // 理财产品代码 = A.产品代码
        /** modify by wujie on 20181115 start remark:投资组合投资时应赋值[表.投资组合代码]字段
        result.setProdId(dto.getProdId());
        **/
        result.setPortfolioId(dto.getProdId());
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)){
//        	result.setPortfolioId(dto.getProdId());
//        }else{
//        	result.setProdId(dto.getProdId());
//        }
        /**modify by wujie on 20181115 end remark:投资组合投资时应赋值[表.投资组合代码]字段**/

        // 是否有结算指令 =A.有无清算指令
//        result.setHasOrder(dto.getIsClean());
        //生成方式 = 接口接入
        result.setGenType(InterfCommonConstant.接入方式);
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        result.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
        return result;
    }


}
