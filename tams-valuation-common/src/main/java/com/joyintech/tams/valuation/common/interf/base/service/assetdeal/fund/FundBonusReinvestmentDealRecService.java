/*
 * 文件名：FundBonusReinvestmentDealRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：HuangJian
 * 修改时间：2018年6月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.joyintech.framework.util.StringUtil;

import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.FundDealInfoForInterfRecCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.TargetFinProduct;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValBonusResTrade;

/**
 * 06红利再投交易
 * 
 * @author HuangJian
 * @version 1.0 2018年6月27日
 * @see FundBonusReinvestmentDealRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FundBonusReinvestmentDealRecService {

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
        List<ValBonusResTrade> listValBonusResTrade = new ArrayList<ValBonusResTrade>();
        Map<String,Object> params = acctEntity.getParams();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();

        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValBonusResTrade = returnResult.getT(ValBonusResTrade.class);
        
        // 获取数据数量
        result.setDataNum(listValBonusResTrade.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/
        
        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构

        Map<String, Object> map = this.handleValBonusResTrade(listValBonusResTrade,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
        if (!map.isEmpty()) {
            result.setListValFinProductDeal(
                (List<TrdProductDeal>)map.get("ValFinProductDeal"));
            result.setListValFinProductDealExt(
                (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
            
            result.setListValDealAccounting((List<BokDealAccounting>)map.get(
                    "ValDealAccounting"));
            
            result.setListValDealAccountingDetail((List<BokDealAccountingDetail>)map.get(
                    "ValDealAccountingDetail"));
            
            result.setListValBokDetailId((List<BokDetailId>)map.get(
                    "ValBokDetailId"));
            
            // 日志
            result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        return result;
    }

    /**
     * 更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
     * 
     * @param listValBonusResTrade List<ValBonusResTrade>
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> handleValBonusResTrade(List<ValBonusResTrade> listValBonusResTrade,Date resovleDate,Map<String,Object> params,
    		List<ValInterflog> valInterflog) {
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        Map<String, Object> resultMap = new HashMap<>();
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        
        for (ValBonusResTrade item : listValBonusResTrade) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(item.getProdId())){
        		continue;
        	}
        	item.setCreateTime(resovleDate);
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(item);
            listValFinProductDeal.add(valFinProductDeal);
            
            InterfProductDealBookCommonDto interfProductDealBookCommonDto = interfCommonService.dealAccounting(
                valFinProductDeal, item.getProdId());
            
            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(item,valFinProductDeal,
            		interfProductDealBookCommonDto.getAcctSubjectType());
            listValFinProductDealExt.add(valFinProductDealExt);
            
            //四级科目尾号
            interfProductDealBookCommonDto.getBokDetailId().setDetailSubjectNoTail(item.getAssetId());
            //四级科目尾号名称
            interfProductDealBookCommonDto.getBokDetailId().setDetailSubnameTail(item.getAssetName());
            
            listValDealAccounting.add(interfProductDealBookCommonDto.getBokDealAccounting());
            listValDealAccountingDetail.add(interfProductDealBookCommonDto.getBokDealAccountingDetail());
            listValBokDetailId.add(interfProductDealBookCommonDto.getBokDetailId());
            
            
            /**杭州银行专属：非货币基金红利再投时，生成一笔除权登记交易
             * add by wujie on 20180731 start
             */
            if(!DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue().equals(valFinProductDeal.getBusiType())){
            	valFinProductDeal = this.getValFinProductDealCQ(item,valFinProductDeal.getFinprodType());
            	/**modify by wujie on 20190123  remark:重复导入删除逻辑调整
            	cqFtradeIds.add(valFinProductDeal.getfTradeId());//add by wujie on 20180809 remark:用于除权登记交易去重
                **/
                listValFinProductDeal.add(valFinProductDeal);
                
                interfProductDealBookCommonDto = interfCommonService.dealAccounting(
                    valFinProductDeal, item.getProdId());
                
                valFinProductDealExt = this.getValFinProductDealExt(item,valFinProductDeal,
                		interfProductDealBookCommonDto.getAcctSubjectType());
                listValFinProductDealExt.add(valFinProductDealExt);
                
                //四级科目尾号
                interfProductDealBookCommonDto.getBokDetailId().setDetailSubjectNoTail(item.getAssetId());
                //四级科目尾号名称
                interfProductDealBookCommonDto.getBokDetailId().setDetailSubnameTail(item.getAssetName());
                
                listValDealAccounting.add(interfProductDealBookCommonDto.getBokDealAccounting());
                listValDealAccountingDetail.add(interfProductDealBookCommonDto.getBokDealAccountingDetail());
                listValBokDetailId.add(interfProductDealBookCommonDto.getBokDetailId());
            }
            /***add by wujie on 20180731 end***/
            
        }
        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);
        
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);
        
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        
        return resultMap;
    }
    /********************************add by wujie on 20180731 start***********************************/
    private TrdProductDeal getValFinProductDealCQ(ValBonusResTrade dto,String finprodType) {
        TrdProductDeal result = new TrdProductDeal();
        // 交易编号 
        String tradeId=interfCommonService.getTradeid(DictCodeList.交易方向_除权登记.getValue(),dto.getBonusDate(),"A");
        result.setTradeId(tradeId);
        // 业务类型 
        result.setBusiType(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
        // 金融产品代码 = F.资产代码
        result.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F06 基金
        /**modify by wujie on 20181203 start
        result.setFinprodType(DictCodeListExt1.金融产品类型_基金.getValue());**/
        result.setFinprodType(finprodType);
        /**modify by wujie on 20181203 end**/
        
        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日= F.红利再投日
        result.setTradeDate(dto.getBonusDate());
        // 交割日  = F.红利再投日
        result.setSettleDate(dto.getBonusDate());
//        // 单位净价 = F.红利再投净值
//        result.setUnitCprice(dto.getBonusNet());
//        // 交易份额 = F.红利再投份额
//        result.setShareAmt(dto.getBonusShare());
//        // 交易金额 = F.红利再投金额
//        result.setTradeAmt(dto.getBonusAmt());
        // 交易方向 = 红利再投
        result.setPs(DictCodeList.交易方向_除权登记.getValue());
        // 交易状态 = F.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());
        // 是否结清 =设置默认值
        result.setIsClean(InterfCommonConstant.是否结清_默认);
        // 关联系统交易编号 = F.成交编号
        result.setfTradeId(dto.getTradeId());
        
        //20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(result.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	result.setpTradeId(interfCommonService.getFTradeId(result));
        }
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
//        //交易本金=红利再投金
//        result.setPrinAmt(dto.getBonusAmt());
        //利息总额=红利再投金
        result.setIntAmt(dto.getBonusAmt());
        // add by chengzhong for 除权登记加币种  2018-12-25 start 
        result.setCcy(dto.getCcy());
        // add by chengzhong for 除权登记加币种  2018-12-25 end 
      //通道代码 = a.通道代码
        result.setChlId(dto.getChannelId());
        return result;
    }
    /********************************add by wujie on 20180731 end***********************************/
    /**
     * 接收 金融产品交易 T1
     * 
     * @param dto ValBonusResTrade
     * @return ValFinProductDeal
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValBonusResTrade dto) {
        TrdProductDeal result = new TrdProductDeal();
        // 交易编号 
        String tradeId=interfCommonService.getTradeid(DictCodeList.交易方向_红利再投.getValue(),dto.getBonusDate(),"A");
        result.setTradeId(tradeId);
        
        /** modify by wujie on 20181203
        // 业务类型 
        if(DictCodeList.基金类型_货币基金.getValue().equals(dto.getFundType())){
        	result.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
        }else{
        	result.setBusiType(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
        }
        **/
        // 金融产品代码 = F.资产代码
        result.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F06 基金
        /**add by wujie on 20181203 start remark:根据资产代码获取资产对应的金融产品类型，默认为基金**/
        //result.setFinprodType(DictCodeListExt1.金融产品类型_基金.getValue());
	  	String finprodType = DictCodeListExt1.金融产品类型_基金.getValue();
	  	String profitType = DictCodeList.收益类型_货币型.getValue();
	  	result.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
	  	TargetFinProduct item = interfCommonService.getTargetFinProduct(dto.getAssetId());
	  	if(item!=null){
	  		finprodType = item.getFinprodType();
	  		profitType = item.getProfitType();
	  	}
	  	if(DictCodeListExt1.金融产品类型_基金.getValue().equals(finprodType)
	  			&& DictCodeList.收益类型_货币型.getValue().equals(profitType)){
	  		result.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
	  	}else{
	  		result.setBusiType(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
	  	}
	  	result.setFinprodType(finprodType);
	  	/**add by wujie on 20181203 end**/
        
        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        /**add by wujie on 20181008 start**/
        //分红截止日 = F.到期日
        result.setMdate(dto.getMdate());        
        /**add by wujie on 20181008 end**/
        // 交易日= F.红利再投日
        result.setTradeDate(dto.getBonusDate());
        // 交割日  = F.红利再投日
        result.setSettleDate(dto.getBonusDate());
        // 单位净价 = F.红利再投净值
        result.setUnitCprice(dto.getBonusNet());
        // 交易份额 = F.红利再投份额
        result.setShareAmt(dto.getBonusShare());
        /**
         * 
         * modify by wujie on 20181008 start
        // 交易金额 = F.红利再投金额
        result.setTradeAmt(dto.getBonusAmt());
        **/
        //全价金额 = F.红利再投份额
        result.setFpriceAmt(dto.getBonusShare());
        
        /**modify by wujie on 20181008 end**/
        // 交易方向 = 红利再投
        result.setPs(DictCodeList.交易方向_红利再投.getValue());
        // 交易状态 = F.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());
        // 是否结清 =设置默认值
        result.setIsClean(InterfCommonConstant.是否结清_默认);
        // 关联系统交易编号 = F.成交编号
        result.setfTradeId(dto.getTradeId());
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        //交易本金=红利再投金
        result.setPrinAmt(dto.getBonusAmt());
        //ADD JINMAODONG 20180915 红利再投交易增加币种字段
        result.setCcy(dto.getCcy());
        
        // 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(result.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
            result.setpTradeId(interfCommonService.getFTradeId(result));
        }
      //通道代码 = a.通道代码
        result.setChlId(dto.getChannelId());
        return result;
    }

    /**
     * 接收 估值核算交易扩展表 T2
     * 
     * @param dto ValBonusResTrade
     * @return ValFinProductDealExt
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValBonusResTrade dto,TrdProductDeal deal,String finprodType) {
    	TrdProductDealAdd result = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        result.setTradeId(deal.getTradeId());
        // 理财产品代码 = F.产品代码
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
        // 是否有结算指令 = F.有无清算指令
//        result.setHasOrder(dto.getIsClean());
        // 创建时间 = 系统时间
        result.setCreateTime(new Date());
        //生成方式
        result.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        return result;
    }
}
