/*
 * 文件名：NPrdBaseinfoTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：qiangchengxi
 * 修改时间：2018年6月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.nprddeal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.NProdDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValPrdDelDealN;
import com.joyintech.framework.util.StringUtil;

/**
 * 净值型产品分红除权表 模板处理Service(VAL_PRD_DEL_DEAL_N)
 * 
 * @author 魏鹏
 * @version 1.0 2018年6月25日
 * @see NPrdDelDealService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class NPrdDelDealService {

    /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;
    
    @Autowired
    private  InterfCommonService interfCommonService;


    /**
     * 净值型产品交易分红交易 接口模板处理
     * 
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 净值型产品交易 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public NProdDealInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
        NProdDealInfoValCommonResult result = new NProdDealInfoValCommonResult();
        List<ValPrdDelDealN> listValPrdDelDealN = new ArrayList<ValPrdDelDealN>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValPrdDelDealN = returnResult.getT(ValPrdDelDealN.class);
        result.setDatanum(listValPrdDelDealN==null?0:listValPrdDelDealN.size());
        if (listValPrdDelDealN == null) {
        	listValPrdDelDealN = new ArrayList<ValPrdDelDealN>();
        }

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValPrdDelDealN(listValPrdDelDealN,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
        if(map != null){
        	// 金融产品交易List
            List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get(
                "ValFinProductDeal");
            result.setListValFinProductDeal(listValFinProductDeal);
            // 估值核算交易扩展表List
            List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get(
                "ValFinProductDealExt");
            result.setListValFinProductDealExt(listValFinProductDealExt);
            // 入账信息表List
            List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
                "ValDealAccounting");
            result.setListValDealAccounting(listValDealAccounting);
            // 账务明细表List
            List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                "ValDealAccountingDetail");
            result.setListValDealAccountingDetail(listValDealAccountingDetail);
            //账务明细代码表
            List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
                "ValBokDetailId");
            result.setListValBokDetailId(listValBokDetailId);
            
            // 添加日志
            result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        // 返回处理结果数据
        return result;
    }

    /**
     * 02-净值型产品分红除权
     * 
     * @param listValPrdDelDealN 净值型产品存续期交易
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    private Map<String, Object> handleValPrdDelDealN(List<ValPrdDelDealN> listValPrdDelDealN,Date resovleDate, Map<String, Object> params,
    		List<ValInterflog> valInterflog) {
        Map<String, Object> resultMap = new HashMap<>();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        //账务明细代码表
        List<BokDetailId> listValBokDetailId=new ArrayList<BokDetailId>();
          
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        List<ValInterflog> valInterflogList =  new ArrayList<>();
          
        for (ValPrdDelDealN dto : listValPrdDelDealN) {
    		if(StringUtil.equals(valModel, InterfCommonConstant.接口模式_指定账套) && !bookSetIds.contains(dto.getProdId())){
    			continue;
    		}
        	dto.setCreateTime(resovleDate);
            // 金融产品交易
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(dto);
            listValFinProductDeal.add(valFinProductDeal);

            // 估值核算交易扩展表
            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(dto,
                valFinProductDeal);
            listValFinProductDealExt.add(valFinProductDealExt);
            
            // 入账信息表   账务明细表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting_Layering(valFinProductDeal, 
            		dto.getProdId(),dto.getProdVdate(),dto.getFinprodType());
            /**
             * modify by wujie on 20181023 start remark:改为调用分层入账信息共通
            listValDealAccounting.add(dealAccounting.getBokDealAccounting());
            listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
            listValBokDetailId.add(dealAccounting.getBokDetailId());
            */
            if (CollectionUtils.isNotEmpty(dealAccounting.getBokDealAccountingList())) {
            	listValDealAccounting.addAll(dealAccounting.getBokDealAccountingList());
            }
            if (CollectionUtils.isNotEmpty(dealAccounting.getBokDealAccountingDetailList())) {
            	listValDealAccountingDetail.addAll(dealAccounting.getBokDealAccountingDetailList());
            }
            if (CollectionUtils.isNotEmpty(dealAccounting.getBokDetailIdList())) {
            	//设置账务明细代码表的四级科目号
            	String detailSubjectNoTail = dto.getProdId();
                String detailSubnameTail = dto.getProdName();
                if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(dto.getFinprodType())){
                    detailSubjectNoTail += new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate());
                    detailSubnameTail += new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate());
                }
            	/**add by wujie on 20190103 start**/
            	detailSubjectNoTail = detailSubjectNoTail+FinalString.LAYERING_SPLIT+dto.getFinprodType();
            	/**add by wujie on 20190103 end**/
                interfCommonService.setDetailSubjectNoTail(dealAccounting.getBokDetailIdList(), 
                		detailSubjectNoTail, detailSubnameTail);
            	listValBokDetailId.addAll(dealAccounting.getBokDetailIdList());
            }
            /**
             * modify by wujie on 20181023 end remark:改为调用分层入账信息共通
             */
            
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
    
    /**
     * T1:金融产品交易表 接收
     * 
     * @param dto 02-净值型产品分红除权
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValPrdDelDealN dto) {
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        //20181012 leipeng 交易编号 = 调用编号生成共通方法
        valFinProductDeal.setTradeId(interfCommonService.getTradeid(DictCodeList.交易方向_产品除权登记.getValue(), dto.getSettDate(), "A"));
        // 业务类型 = 17 TA交易
        valFinProductDeal.setBusiType(DictCodeList.业务类型_净值型TA交易.getValue());
        // 金融产品代码 = B.产品代码
        valFinProductDeal.setFinprodId(dto.getProdId());
        // 金融产品类型 = F16 理财产品
        valFinProductDeal.setFinprodType(dto.getFinprodType());
        // 分支序号 = 设置全局变量(默认为0）
        Long branch = null; 
        if(StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品.getValue())){
            branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate()));
        } else {
            branch = InterfCommonConstant.分支序号_默认;
        }
        valFinProductDeal.setBranch(branch);
        // 交易日 = B.除权日
        valFinProductDeal.setTradeDate(dto.getDelDate());
        // 交易币种 = B.币种
        valFinProductDeal.setCcy(dto.getCcy());
        // 单位净价 = B.单位分红金额
        valFinProductDeal.setUnitCprice(dto.getUnitBonusAmt());
        
        /**modify by wujie on 20180827 start**/
        //全价总额 = B.分红总金额
        valFinProductDeal.setFpriceAmt(dto.getBonusTotalAmt());
//        // 交易金额 = B.分红总金额
//        valFinProductDeal.setTradeAmt(dto.getBonusTotalAmt());
        /**modify by wujie on 20180827 end**/
        
        // 交易方向 = 数据字典.交易方向.除权登记
        valFinProductDeal.setPs(DictCodeList.交易方向_产品除权登记.getValue());
        // 交易状态 = B.数据动作标志
        valFinProductDeal.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 固定值: ---收息付费时用
        valFinProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
        // 关联系统交易编号 = B.成交编号
        valFinProductDeal.setfTradeId(dto.getTradeNo());
        //20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
            valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
        }
        // 创建时间 = 系统时间
        valFinProductDeal.setCreateTime(dto.getCreateTime());
        return valFinProductDeal;
    }

    /**
     * T2:估值核算交易扩展表 接收
     * 
     * @param dto 02-净值型产品分红除权
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValPrdDelDealN dto,
                                                         TrdProductDeal t1) {
        // 估值核算交易扩展表
    	TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        valFinProductDealExt.setTradeId(t1.getTradeId());
        // 理财产品代码 = B.产品代码
//        valFinProductDealExt.setProdId(dto.getProdId());
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(dto.getFinprodType())){
//            valFinProductDealExt.setPortfolioId(dto.getProdId());
//        }else{
////            valFinProductDealExt.setProdId(dto.getProdId());
//        }
        
        valFinProductDealExt.setPortfolioId(dto.getProdId());
        // 是否有结算指令 = B.有无清算指令
//        valFinProductDealExt.setHasOrder(dto.getIsClean());
        // 创建时间 = 系统时间
        valFinProductDealExt.setCreateTime(dto.getCreateTime());
        //生成方式
        valFinProductDealExt.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        return valFinProductDealExt;
    }

}
