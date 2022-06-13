/*
 * 文件名：SecReceicableTradeTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：tanxuan
 * 修改时间：2019年12月26日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.sec;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecReceicableTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 债券应收款项转让交易 模板处理Service
 * 
 * @author tanxuan
 * @version 1.0 2019年12月26日
 * @see SecReceicableTradeTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecReceicableTradeTemplateRecService {

    /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;
    /**
     * 接口共通Service
     */
    @Autowired
    private InterfCommonService interfCommonService;
    /**
     * 债券应收款项转让交易 接口模板处理
     * 
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 债券应收款项转让交易 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AssetDealInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();
        List<ValSecReceicableTrade> listValSecReceicableTrade = new ArrayList<ValSecReceicableTrade>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValSecReceicableTrade = returnResult.getT(ValSecReceicableTrade.class);
        //获取数据数量
        result.setDataNum(listValSecReceicableTrade.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValSecReceicableTrade(listValSecReceicableTrade,acctEntity.getResovleDate(),params,returnResult.getValInterflog());

        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get(
            "ValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get(
            "ValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        //交易类金融产品表List
        List<FinTradeProduct> listValFinTradeProduct = (List<FinTradeProduct>)map.get(
            "ValFinTradeProduct");
        result.setListValFinTradeProduct(listValFinTradeProduct);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
            "ValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
            "ValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
            "ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
        //结算指令表List
        List<StlOrders> valOrdersList=(List<StlOrders>)map.get("valOrdersList");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("valOrdersExtList");
        result.setListValOrdersExt(valOrdersExtList);
        // 添加日志
        result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        // 返回处理结果数据
        return result;
    }

    /**
     * 03-债券应收款转让交易
     * 
     * @param listValSecReceicableTrade 
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List + 交易类金融产品表List+账务明细代码表List+结算指令表List+结算指令拓展表list
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> handleValSecReceicableTrade(List<ValSecReceicableTrade> listValSecReceicableTrade,Date resovleDate,Map<String,Object> params,
    		List<ValInterflog> valInterflog) {
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        Map<String, Object> resultMap = new HashMap<>();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        //交易类金融产品表List
        List<FinTradeProduct> listValFinTradeProduct = new ArrayList<FinTradeProduct>();
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
         //结算指令list
        List<StlOrders> valOrdersList = new ArrayList<StlOrders>();
        //结算指令拓展list
        List<StlOrdersExt> valOrdersExtList = new ArrayList<StlOrdersExt>();
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        
        Map<String, Long> maxBranchMap = new HashMap<String, Long>();
        Map<String,TargetFinProduct> assetMap = new HashMap<String,TargetFinProduct>();
        
        for (ValSecReceicableTrade dto : listValSecReceicableTrade) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
        	
            dto.setCreateTime(resovleDate);//add by chufengjie on 20180720 remark:用于去重复数据
            
            String acctSubjectType = null;
            
            TrdProductDeal valFinProductDeal = null;
            
            //本金
            if(dto.getCapital()!=null && BigDecimal.ZERO.compareTo(dto.getCapital())!=0){
            	valFinProductDeal = this.getValFinProductDeal(dto,DictCodeList.分支类型_应收本金.getValue());
                
                FinTradeProduct valFinTradeProduct = this.getValFinTradeProduct(valFinProductDeal,DictCodeList.分支类型_应收本金.getValue());

                Long maxBranch = interfCommonService.getBranch(valFinProductDeal.getMdate(),DictCodeList.分支类型_应收本金.getValue());
                valFinProductDeal.setBranch(maxBranch);
                //原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
                if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
                	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
                }
                
                String mapKey = valFinProductDeal.getFinprodId() + valFinProductDeal.getBranch();
                //查询数据库中是否存在
                Long branch = maxBranchMap.get(mapKey);
                if(branch==null){
                	branch = interfCommonService.queryValFinProductDealBranchNow(
                            valFinProductDeal.getFinprodId(), valFinProductDeal.getFinprodType(),
                            DictCodeList.分支类型_应收本金.getValue(), valFinProductDeal.getMdate());
                }
                if(branch==null || (branch != null && branch.compareTo(maxBranch) != 0)){
                	// 往 【T9: 交易类金融产品】插入数据
                    valFinTradeProduct.setBranch(maxBranch);
                    listValFinTradeProduct.add(valFinTradeProduct);  
                }
                maxBranchMap.put(mapKey, maxBranch);
                
                listValFinProductDeal.add(valFinProductDeal);
                
                // 入账信息表 账务明细表调用共通
                InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(
                    valFinProductDeal, dto.getProdId());
                
                TrdProductDealAdd valtrdProductDealExt = this.getValFinProductDealExt(dto,valFinProductDeal);
                listValFinProductDealExt.add(valtrdProductDealExt);
                
                //入账信息表List
                listValDealAccounting.add(dealAccounting.getBokDealAccounting());
                // 账务明细表List
                listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
                
                //四级科目尾号
                dealAccounting.getBokDetailId().setDetailSubjectNoTail(dto.getAssetId());
                
                TargetFinProduct item = assetMap.get(dto.getAssetId());
                if(item==null){
                	item = interfCommonService.getTargetFinProduct(dto.getAssetId());
                	//四级科目尾号名称
                	if(item!=null){
                        dealAccounting.getBokDetailId().setDetailSubnameTail(item.getFinprodName());
                        assetMap.put(dto.getAssetId(), item);
                    }
                }
                
                // 账务明细代码表
                listValBokDetailId.add(dealAccounting.getBokDetailId()) ;
                
                StlOrders stlOrders = this.getStlOrders(dto,valFinProductDeal);
                Map<String, Object> maps = interfCommonService.instructionGenerate_Interf(valFinProductDeal,stlOrders, 
                		dto.getProdId(), dealAccounting.getAcctSubjectType(),null);
                if(maps.get("ORDERS")!=null){
                	valOrdersList.addAll((List<StlOrders>)maps.get("ORDERS"));
                }
                if(maps.get("ORDERS_EXT")!=null){
                	valOrdersExtList.addAll((List<StlOrdersExt>)maps.get("ORDERS_EXT"));
                }
                if(maps.get("ACCOUNTING")!=null){
                	 listValDealAccounting.addAll((List<BokDealAccounting>)maps.get("ACCOUNTING"));
                }
                if(maps.get("ACCOUNTING_DETAI")!=null){
                	listValDealAccountingDetail.addAll(( List<BokDealAccountingDetail>)maps.get("ACCOUNTING_DETAI"));
                }
                if(maps.get("BOK_DETAIL_ID")!=null){
                	listValBokDetailId.addAll((List<BokDetailId>)maps.get("BOK_DETAIL_ID"));
                }
                
                acctSubjectType = dealAccounting.getAcctSubjectType();
            }
            
            
            if(dto.getIntAmt()!=null && BigDecimal.ZERO.compareTo(dto.getIntAmt())!=0){
            	
            	valFinProductDeal = this.getValFinProductDeal(dto,DictCodeList.分支类型_应收利息.getValue());
                
                FinTradeProduct valFinTradeProduct = this.getValFinTradeProduct(valFinProductDeal,DictCodeList.分支类型_应收利息.getValue());

                Long maxBranch = interfCommonService.getBranch(valFinProductDeal.getMdate(),
                    DictCodeList.分支类型_应收利息.getValue());
                valFinProductDeal.setBranch(maxBranch);
                //原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
                if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
                	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
                }
                
                String mapKey = valFinProductDeal.getFinprodId() + valFinProductDeal.getFinprodType()+new SimpleDateFormat("yyyyMMdd").format(valFinProductDeal.getMdate());
                //查询数据库中是否存在
                Long branch = maxBranchMap.get(mapKey);
                if(branch==null){
                	branch = interfCommonService.queryValFinProductDealBranchNow(
                            valFinProductDeal.getFinprodId(), valFinProductDeal.getFinprodType(),
                            DictCodeList.分支类型_应收利息.getValue(), valFinProductDeal.getMdate());
                }
                if(branch==null || (branch != null && branch.compareTo(maxBranch) != 0)){
                	// 往 【T9: 交易类金融产品】插入数据
                    valFinTradeProduct.setBranch(maxBranch);
                    listValFinTradeProduct.add(valFinTradeProduct);  
                }
                maxBranchMap.put(mapKey, maxBranch);
                
                listValFinProductDeal.add(valFinProductDeal);
                
                // 入账信息表 账务明细表调用共通
                InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(
                    valFinProductDeal, dto.getProdId());
                
                TrdProductDealAdd valtrdProductDealExt = this.getValFinProductDealExt(dto, valFinProductDeal);
                listValFinProductDealExt.add(valtrdProductDealExt);
                
                //入账信息表List
                listValDealAccounting.add(dealAccounting.getBokDealAccounting());
                // 账务明细表List
                listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
                
                //四级科目尾号
                dealAccounting.getBokDetailId().setDetailSubjectNoTail(dto.getAssetId());
                
                TargetFinProduct item = assetMap.get(dto.getAssetId());
                if(item==null){
                	item = interfCommonService.getTargetFinProduct(dto.getAssetId());
                	//四级科目尾号名称
                	if(item!=null){
                        dealAccounting.getBokDetailId().setDetailSubnameTail(item.getFinprodName());
                        assetMap.put(dto.getAssetId(), item);
                    }
                }
                
                // 账务明细代码表
                listValBokDetailId.add(dealAccounting.getBokDetailId()) ;
                
                StlOrders stlOrders = this.getStlOrders(dto,valFinProductDeal);
                Map<String, Object> maps = interfCommonService.instructionGenerate_Interf(valFinProductDeal,stlOrders, 
                		dto.getProdId(), dealAccounting.getAcctSubjectType(),null);
                
                if(maps.get("ORDERS")!=null){
                	valOrdersList.addAll((List<StlOrders>)maps.get("ORDERS"));
                }
                if(maps.get("ORDERS_EXT")!=null){
                	valOrdersExtList.addAll((List<StlOrdersExt>)maps.get("ORDERS_EXT"));
                }
                if(maps.get("ACCOUNTING")!=null){
                	 listValDealAccounting.addAll((List<BokDealAccounting>)maps.get("ACCOUNTING"));
                }
                if(maps.get("ACCOUNTING_DETAI")!=null){
                	listValDealAccountingDetail.addAll(( List<BokDealAccountingDetail>)maps.get("ACCOUNTING_DETAI"));
                }
                if(maps.get("BOK_DETAIL_ID")!=null){
                	listValBokDetailId.addAll((List<BokDetailId>)maps.get("BOK_DETAIL_ID"));
                }
                
                acctSubjectType = dealAccounting.getAcctSubjectType();
            	
            }
            
            
            ChannelFeeInfoDeal channelFeeInfoDeal = this.getChannelFeeInfoDeal(dto, acctSubjectType);
            if(channelFeeInfoDeal != null){
                Map<String,Object> feedealMap = interfCommonService.assetChlFeeLend(valFinProductDeal, channelFeeInfoDeal);
                
                if(feedealMap.get("TrdProductDeal")!=null){
                    listValFinProductDeal.add((TrdProductDeal)feedealMap.get("TrdProductDeal"));
                }
                if(feedealMap.get("TrdProductDealExt")!=null){
                    listValFinProductDealExt.add((TrdProductDealAdd)feedealMap.get("TrdProductDealExt"));
                }
                if(feedealMap.get("TgTradeFeeDetail")!=null){
                    listValTradeFeeDetail.add((TrdTradeFeeDetail)feedealMap.get("TgTradeFeeDetail"));
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
                    valOrdersList.addAll((List<StlOrders>)feedealMap.get("StlOrders_List"));
                }
                if(feedealMap.get("StlOrdersExt_List")!=null){
                    valOrdersExtList.addAll((List<StlOrdersExt>)feedealMap.get("StlOrdersExt_List"));
                }
            }
        }
        resultMap.put("valOrdersList", valOrdersList);
        resultMap.put("valOrdersExtList", valOrdersExtList);
        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);
        resultMap.put("ValTradeFeeDetail", listValTradeFeeDetail);
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);
        
        resultMap.put("ValFinTradeProduct", listValFinTradeProduct);
        
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        return resultMap;
    }
    
    /**
     * 接收 结算指令信息
     * @param dto
     * @return
     */
    private StlOrders getStlOrders(ValSecReceicableTrade dto, TrdProductDeal valFinProductDeal){
    	StlOrders result = new StlOrders();
        //modify by yanya on 20210204 判定为空包含空字符
        //if(dto.getMyAcctNo() == null && dto.getCounterAcctNo() == null){
        if(StringUtil.isEmpty(dto.getMyAcctNo()) && StringUtil.isEmpty(dto.getCounterAcctNo()) ){
    		return null;
    	}
    	result.setMyAcctNo(dto.getMyAcctNo());
    	result.setCounterAcctNo(dto.getCounterAcctNo());
    	result.setSettleDate(dto.getSettleDate());
    	result.setSettleAmt(valFinProductDeal.getTradeAmt());
    	return result;
    }

    /**
     * T1:金融产品交易表 接收
     * 
     * @param dto 03-应收款转让交易
     * @param dto
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValSecReceicableTrade dto,String branchType) {
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        //交易编号 =共通生成
        String tradeId=interfCommonService.getTradeid(dto.getPs(),dto.getTradeDate(),"A");
        valFinProductDeal.setTradeId(tradeId);
        // 业务类型 = 02 债券交易
        valFinProductDeal.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
        // 金融产品代码 = C.资产代码
        valFinProductDeal.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F01 债券
        valFinProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
       
        // 交易日 = C.交易日
        valFinProductDeal.setTradeDate(dto.getTradeDate());
        //起息日= C.起息日
        valFinProductDeal.setVdate(dto.getVdate());
        //到期日= C.到期日
        valFinProductDeal.setMdate(dto.getMdate());
        //交割日 = B.交割日
        valFinProductDeal.setSettleDate(dto.getEffDate());
        // 交易币种 = B.币种
        valFinProductDeal.setCcy(dto.getCcy());
        
        if(DictCodeList.分支类型_应收利息.getValue().equals(branchType)){
        	// 交易本金 = C.应收利息总额
        	valFinProductDeal.setPrinAmt(BigDecimal.ZERO);
            // 利息总额 = C.应收利息总额
        	valFinProductDeal.setIntAmt(dto.getIntAmt());
            // 交易金额 = C.应收利息总额
        	valFinProductDeal.setTradeAmt(dto.getIntAmt());
        }
        else{
        	// 交易本金 = C.本金金额
        	valFinProductDeal.setPrinAmt(dto.getCapital());
            // 利息总额 = C.应收利息总额
        	valFinProductDeal.setIntAmt(BigDecimal.ZERO);
            // 交易金额 = C.本金金额
        	valFinProductDeal.setTradeAmt( dto.getCapital());
        }
       
        //交易方向 = C.交易方向
        valFinProductDeal.setPs(dto.getPs());
        // 交易状态 = B.数据动作标志
        valFinProductDeal.setTradeStatus(dto.getDataActionSign());
        // 关联系统交易编号 = B.成交编号
        valFinProductDeal.setfTradeId(dto.getTradeId());
        
        valFinProductDeal.setInvAim(dto.getInvestAim());
        
//        //原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
//        if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
//        	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
//        }
        // 是否结清 = B.是否结清
        valFinProductDeal.setIsClean(dto.getIsClean2());
        //通道代码 = C.通道代码
        valFinProductDeal.setChlId(dto.getChannelId());
        // 创建时间 = 系统时间
        valFinProductDeal.setCreateTime(dto.getCreateTime());
        valFinProductDeal.setCreateDept(null);
        
        return valFinProductDeal;
    }

    /**
     * T2:估值核算交易扩展表 接收
     * 
     * @param dto 03 应收款转让交易
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValSecReceicableTrade dto,TrdProductDeal t1) {
        // 估值核算交易扩展表
    	TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        valFinProductDealExt.setTradeId(t1.getTradeId());
        // 投资组合代码
        valFinProductDealExt.setPortfolioId(dto.getProdId());
        //生成方式
        valFinProductDealExt.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        // 创建时间 = 系统时间
        valFinProductDealExt.setCreateTime(dto.getCreateTime());
        return valFinProductDealExt;
    }
    
    /**
     * T9:交易类金融产品表 生成
     * 
     * @param dto 03 应收款转让交易
     * @return 交易类金融产品表
     * @see
     * @since 1.0
     */
    private FinTradeProduct getValFinTradeProduct(TrdProductDeal t1,String branchType) {
        // 交易类金融产品表
    	FinTradeProduct valFinTradeProduct = new FinTradeProduct();
        // 金融产品代码 = T1.金融产品代码
    	valFinTradeProduct.setFinprodId(t1.getFinprodId());
        // 金融产品类型 = T1.金融产品类型
    	valFinTradeProduct.setFinprodType(t1.getFinprodType());
        // 分支序号 = T1.分支序号
    	valFinTradeProduct.setBranch(t1.getBranch());
        // 分支类型 = 数据字典：分支类型.应收利息
    	valFinTradeProduct.setBranchType(branchType);
        // 名义本金 = T1.交易本金
    	valFinTradeProduct.setPrin(t1.getPrinAmt());
        // 本金币种 = T1.交易币种
    	valFinTradeProduct.setCcy(t1.getCcy());
        // 起息日 = T1.起息日
    	valFinTradeProduct.setVdate(t1.getVdate());
        // 到期日 = T1.到期日
    	valFinTradeProduct.setMdate(t1.getMdate());
        // 到期利息= T1.利息总额
    	valFinTradeProduct.setmIntAmt(t1.getIntAmt());
        // 到期金额 = T1.交易金额
    	valFinTradeProduct.setmTradeAmt(t1.getTradeAmt());
        // 原金融产品代码 = T1.金融产品代码
    	valFinTradeProduct.setoFinprodId(t1.getFinprodId());
        // 原金融产品类型 = T1.金融产品类型
    	
    	// 创建时间 = 系统时间
    	valFinTradeProduct.setCreateTime(t1.getCreateTime());
    	
        return valFinTradeProduct;
    }
    
    /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValSecReceicableTrade dto,String acctSubjectType){
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
    
}
