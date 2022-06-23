/*
 * 文件名：OtherBaseInfoRecvWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：王波
 * 修改时间：2018年6月26日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.other.writer;

import com.github.pagehelper.util.StringUtil;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.security.data.MultilevelDataAuthorityService;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.MstCounterParty;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMstBondCreditRating;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.other.entity.OtherBaseInfoForAssetProcessorResult;
import com.joyintech.tams.valuation.interf.valuation.recv.common.service.AssetmanageRecvCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Scope("prototype")
public class OtherBaseInfoRecvWriter extends InterfBaseWriter<BaseEntity> {
    /**
     * 市场机构信息BatchMapper
     */
    @Autowired
    private ValCustomerInfoBatchMapper valCustomerInfoBatchMapper;

    /**
     * 交易对手BatchMapper
     */
    @Autowired
    private ValCounterPartyBatchMapper valCounterPartyBatchMapper;

    /**
     * 评级信息BatchMapper
     */
    @Autowired
    private ValMstBondCreditRatingBatchMapper valMstBondCreditRatingBatchMapper;

    /**
     * 清算账户信息BatchMapper
     */
    @Autowired
    private ValMstCapacctInfoBatchMapper valMstCapacctInfoBatchMapper;

    /**
     * 金融产品账户BatchMapper
     */
    @Autowired
    private ValFinProductAcctBatchMapper valFinProductAcctBatchMapper;

    /**
     * 日历信息BatchMapper
     */
    @Autowired
    private ValCalendarInfoBatchMapper valCalendarInfoBatchMapper;

    /**
     * 交易场所日历BatchMapper
     */
    @Autowired
    private ValTradeCalendarBatchMapper valTradeCalendarBatchMapper;

    /**
     * 日历节假BatchMapper
     */
    @Autowired
    private ValHolidayBatchMapper valHolidayBatchMapper;

    /**
     * 交易场所日历BatchMapper
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;
    
    /**
     * 金融产品账户BatchMapper
     */
    @Autowired
    private ValBooksetCapacctInfoBatchMapper valBooksetCapacctInfoBatchMapper;
    
    /**
     * 多级法人数据权限接口 add by tanxuan on 20210310 多级法人权限控制改造
     */
    @Autowired
    private MultilevelDataAuthorityService multilevelDataAuthorityService;
    
    /**
     * 现金流信息表
     */
    @Autowired
    private ValCashInfoBatchMapper valCashInfoBatchMapper;

    //add by liupengfei for 多级法人改造 S
    @Autowired
    private AssetmanageRecvCommonService assetmanageRecvCommonService;
    //add by liupengfei for 多级法人改造 E
    
    /**
     * 金融产品分类
     */
    @Autowired
    private ValFinProductTypeBatchMapper valFinProductTypeBatchMapper;

    /** add by yanya on 20210831 增加客户信息附属表 start**/
    @Autowired
    private ValCustomerInfoAddBatchMapper valCustomerInfoAddBatchMapper;
    /** add by yanya on 20210831 增加客户信息附属表 end**/

    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;
    
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> items)
        throws Exception {

        // 获取process返回组合的list值
        List<OtherBaseInfoForAssetProcessorResult> listResult = (List<OtherBaseInfoForAssetProcessorResult>)items;
        for (OtherBaseInfoForAssetProcessorResult result : listResult) {
            // 市场机构信息
            List<MstCustomerInfo> listValCustomerInfo = result.getValCustomerInfoList();
            // 入库
            if (CollectionUtils.isNotEmpty(listValCustomerInfo)) {
                
                //定义一个固定大小插入存放集合
                List<MstCustomerInfo> listInsert =  new ArrayList<MstCustomerInfo>();
                for(MstCustomerInfo e :listValCustomerInfo){
                   //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valCustomerInfoBatchMapper.deleteBatch(listInsert); 
                        valCustomerInfoBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valCustomerInfoBatchMapper.deleteBatch(listInsert); 
                    valCustomerInfoBatchMapper.insertBatch(listInsert);
                }
            }



            // 交易对手
            List<MstCounterParty> listValCounterParty = result.getValCounterPartyList();
            // 入库
            if (CollectionUtils.isNotEmpty(listValCounterParty)) {

                //定义一个固定大小插入存放集合
                List<MstCounterParty> listInsert =  new ArrayList<MstCounterParty>();
                for(MstCounterParty e :listValCounterParty){
                    //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e);
                    }else if(listInsert.size() >= maxSqlNum){
                        valCounterPartyBatchMapper.deleteBatch(listInsert);
                        valCounterPartyBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valCounterPartyBatchMapper.deleteBatch(listInsert);
                    valCounterPartyBatchMapper.insertBatch(listInsert);
                }
            }

            // 评级信息
            List<ValMstBondCreditRating> listValBondCreditRating = result.getValBondCreditRatingList();
            // 入库
            if (CollectionUtils.isNotEmpty(listValBondCreditRating)) {

                //定义一个固定大小插入存放集合
                List<ValMstBondCreditRating> listInsert =  new ArrayList<ValMstBondCreditRating>();
                for(ValMstBondCreditRating e :listValBondCreditRating){
                    //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e);
                    }else if(listInsert.size() >= maxSqlNum){
                        valMstBondCreditRatingBatchMapper.deleteBatch(listInsert);
                        valMstBondCreditRatingBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valMstBondCreditRatingBatchMapper.deleteBatch(listInsert);
                    valMstBondCreditRatingBatchMapper.insertBatch(listInsert);
                }
            }


            /** add by yanya on 20210831 增加客户信息附属表 start**/
            List<MstCustomerInfoAddWithBLOBs> listValCustomerInfoAdd = result.getValCustomerInfoAddList();
            // 入库
            if (CollectionUtils.isNotEmpty(listValCustomerInfoAdd)) {
                //定义一个固定大小插入存放集合
                List<MstCustomerInfoAddWithBLOBs> listInsert =  new ArrayList<MstCustomerInfoAddWithBLOBs>();
                for(MstCustomerInfoAddWithBLOBs e :listValCustomerInfoAdd){
                    //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e);
                    }else if(listInsert.size() >= maxSqlNum){
                        valCustomerInfoAddBatchMapper.deleteBatch(listInsert);
                        valCustomerInfoAddBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valCustomerInfoAddBatchMapper.deleteBatch(listInsert);
                    valCustomerInfoAddBatchMapper.insertBatch(listInsert);
                }
            }
            /** add by yanya on 20210831 增加客户信息附属表 end**/

            // 清算账户信息
            List<StlCapacctInfo> listValMstCapacctInfo = result.getValMstCapacctInfoList();
            /**add by wujie on 20180828 start**/
            if (CollectionUtils.isNotEmpty(listValMstCapacctInfo)) {
            	List<StlCapacctInfo> listValMstCapacctInfoExt = new ArrayList<StlCapacctInfo>();
            	Map<String,StlCapacctInfo> resultMap = new HashMap<String,StlCapacctInfo>();
            	for(StlCapacctInfo stl :listValMstCapacctInfo){
            		if(resultMap.get(stl.getAcctNo()+stl.getCcy()) ==null){
            			listValMstCapacctInfoExt.add(stl);
                    }
            		resultMap.put(stl.getAcctNo()+stl.getCcy(), stl);
            	}
//            	if(listValMstCapacctInfoExt.size()<1000){
//            		valMstCapacctInfoBatchMapper.deleteBatch(listValMstCapacctInfoExt);
//            		valMstCapacctInfoBatchMapper.insertBatch(listValMstCapacctInfoExt);
//	              }else{
//	                  int times = (int)Math.ceil( listValMstCapacctInfoExt.size()/1000.0 );
//	                  for(int i=0; i<times; i++ ){
//	                	  valMstCapacctInfoBatchMapper.deleteBatch(listValMstCapacctInfoExt.subList(i*1000, Math.min((i+1)*1000, listValMstCapacctInfoExt.size()-1)));
//	                	  valMstCapacctInfoBatchMapper.insertBatch(listValMstCapacctInfoExt.subList(i*1000, Math.min((i+1)*1000, listValMstCapacctInfoExt.size()-1)));
//	                  }
//	              }
                // 入库
                if (CollectionUtils.isNotEmpty(listValMstCapacctInfoExt)) {
                    //定义一个固定大小插入存放集合
                    List<StlCapacctInfo> listInsert =  new ArrayList<StlCapacctInfo>();
                    for(StlCapacctInfo e :listValMstCapacctInfoExt){
                       //小于最大值
                        if(listInsert.size() < maxSqlNum){
                            listInsert.add(e); 
                        }else if(listInsert.size() >= maxSqlNum){
                            valMstCapacctInfoBatchMapper.deleteBatch(listInsert); 
                            valMstCapacctInfoBatchMapper.insertBatch(listInsert); 
                            listInsert.clear();
                            listInsert.add(e);
                        }
//                        //add by liupengfei for 多级法人改造 S
//                        // 记录二级法人权限
//                        if(MultilevelDataAuthorityUtil.isEnabled()){
//                            assetmanageRecvCommonService.insertDataAuthInst(e.getSeqNo(),e.getBelongInstitutionCode(), DictCodeList.共通业务类型_资金账户管理.getValue());
//                        }
//                        //add by liupengfei for 多级法人改造 E
                    }
                    //如果循环结束，小于最大值
                    if(CollectionUtils.isNotEmpty(listInsert)){
                        valMstCapacctInfoBatchMapper.deleteBatch(listInsert); 
                        valMstCapacctInfoBatchMapper.insertBatch(listInsert);
                    }
                    if(DictCodeListExt3.权限模式_联社模式.getValue().equals(CacheUtil.getSysParam("AUTHORITY_TYPE"))){
                    	List<String> editOrgCodeList = new ArrayList<>();
                    	for (StlCapacctInfo e :listValMstCapacctInfoExt) {
                    		editOrgCodeList.clear();
                    		if(StringUtil.isNotEmpty(e.getBelongInstitutionCode())){
                    			editOrgCodeList.add(e.getBelongInstitutionCode());
                    		}
                    		multilevelDataAuthorityService.saveForOrganization(e.getAcctNo(), null, editOrgCodeList,
                        			DictCodeList.共通业务类型_资金账户管理.getValue());
                        }
                    }
                }
            }
            /**add by wujie on 20180828 end**/
            /**add by wujie on 20180814 start remark:活期利息改造**/
            
            //删除现金流信息表
            if(result.getParamMap()!=null && result.getParamMap().get("FinCashInfo_no")!=null){
            	List<FinCashInfo> listValCashInfo_no = (List<FinCashInfo>)result.getParamMap().get("FinCashInfo_no");
//            	if (CollectionUtils.isNotEmpty(listValCashInfo_no)) {
//                    if(listValCashInfo_no.size()<1000){
//                        valCashInfoBatchMapper.deleteBatch(listValCashInfo_no);
//                    }else{
//                        int times = (int)Math.ceil( listValCashInfo_no.size()/1000.0 );
//                        for(int i=0; i<times; i++ ){
//                            valCashInfoBatchMapper.deleteBatch(listValCashInfo_no.subList(i*1000, Math.min((i+1)*1000, listValCashInfo_no.size()-1)));
//                        }
//                    }
//                }
            	// 入库
                if (CollectionUtils.isNotEmpty(listValCashInfo_no)) {
                    //定义一个固定大小插入存放集合
                    List<FinCashInfo> listInsert =  new ArrayList<FinCashInfo>();
                    for(FinCashInfo e :listValCashInfo_no){
                       //小于最大值
                        if(listInsert.size() < maxSqlNum){
                            listInsert.add(e); 
                        }else if(listInsert.size() >= maxSqlNum){
                            valCashInfoBatchMapper.deleteBatch(listInsert);  
                            listInsert.clear();
                            listInsert.add(e);
                        }
                    }
                    //如果循环结束，小于最大值
                    if(CollectionUtils.isNotEmpty(listInsert)){
                        valCashInfoBatchMapper.deleteBatch(listInsert); 
                    }
                }
            }
            //删除金融产品分类表
            if(result.getParamMap()!=null && result.getParamMap().get("FinProductType_no")!=null){
            	List<FinProductType> listValFinProductType_no = (List<FinProductType>)result.getParamMap().get("FinProductType_no");
//            	if (CollectionUtils.isNotEmpty(listValFinProductType_no)) {
//                    if(listValFinProductType_no.size()<1000){
//                        valFinProductTypeBatchMapper.deleteBatch(listValFinProductType_no);
//                    }else{
//                        int times = (int)Math.ceil( listValFinProductType_no.size()/1000.0 );
//                        for(int i=0; i<times; i++ ){
//                            valFinProductTypeBatchMapper.deleteBatch(listValFinProductType_no.subList(i*1000, Math.min((i+1)*1000, listValFinProductType_no.size()-1)));
//                        }
//                    }
//                }
            	
                // 入库
                if (CollectionUtils.isNotEmpty(listValFinProductType_no)) {
                    //定义一个固定大小插入存放集合
                    List<FinProductType> listInsert =  new ArrayList<FinProductType>();
                    for(FinProductType e :listValFinProductType_no){
                       //小于最大值
                        if(listInsert.size() < maxSqlNum){
                            listInsert.add(e); 
                        }else if(listInsert.size() >= maxSqlNum){
                            valFinProductTypeBatchMapper.deleteBatch(listInsert);  
                            listInsert.clear();
                            listInsert.add(e);
                        }
                    }
                    //如果循环结束，小于最大值
                    if(CollectionUtils.isNotEmpty(listInsert)){
                        valFinProductTypeBatchMapper.deleteBatch(listInsert); 
                    }
                }
            }
            // 现金流信息表
            List<FinCashInfo> listValCashInfo = result.getFinCashInfoList();
            Map<String,FinCashInfo> mapValCashInfo = new HashMap<String,FinCashInfo>();
            List<FinCashInfo> rstValCashInfo= new ArrayList<FinCashInfo>();
            for(FinCashInfo obj :listValCashInfo){
                if(mapValCashInfo.get(obj.getCashId()) ==null){
                    rstValCashInfo.add(obj);
                }
                mapValCashInfo.put(obj.getCashId(), obj);
            }
//            // 入库
//            if (CollectionUtils.isNotEmpty(rstValCashInfo)) {
//                if(rstValCashInfo.size()<1000){
//                    valCashInfoBatchMapper.deleteBatch(rstValCashInfo);
//                    valCashInfoBatchMapper.insertBatch(rstValCashInfo);
//                }else{
//                    int times = (int)Math.ceil( rstValCashInfo.size()/1000.0 );
//                    for(int i=0; i<times; i++ ){
//                        valCashInfoBatchMapper.deleteBatch(rstValCashInfo.subList(i*1000, Math.min((i+1)*1000, rstValCashInfo.size()-1)));
//                        valCashInfoBatchMapper.insertBatch(rstValCashInfo.subList(i*1000, Math.min((i+1)*1000, rstValCashInfo.size()-1)));
//                    }
//                }
//            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstValCashInfo)) {
                //定义一个固定大小插入存放集合
                List<FinCashInfo> listInsert =  new ArrayList<FinCashInfo>();
                for(FinCashInfo e :rstValCashInfo){
                   //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valCashInfoBatchMapper.deleteBatch(listInsert); 
                        valCashInfoBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valCashInfoBatchMapper.deleteBatch(listInsert); 
                    valCashInfoBatchMapper.insertBatch(listInsert);
                }
            }
            // 金融产品分类
            List<FinProductType> listValFinProductType = result.getFinProductTypeList();
            Map<String,FinProductType> mapValFinProductType = new HashMap<String,FinProductType>();
            List<FinProductType>   rstValFinProductType= new ArrayList<FinProductType>();
//            for(FinProductType obj :listValFinProductType){
//                if(mapValFinProductType.get(obj.getFinprodId()+obj.getFinprodType()) ==null){
//                    rstValFinProductType.add(obj);
//                }
//                mapValFinProductType.put(obj.getFinprodId()+obj.getFinprodType(), obj);
//            }
            
            for(FinProductType obj :listValFinProductType){
                if(mapValFinProductType.get(obj.getFinprodId()) ==null){
                    rstValFinProductType.add(obj);
                }
                mapValFinProductType.put(obj.getFinprodId(), obj);
            }
//            // 入库
//            if (CollectionUtils.isNotEmpty(rstValFinProductType)) {
//                if(rstValFinProductType.size()<1000){
//                    valFinProductTypeBatchMapper.deleteBatch(rstValFinProductType);
//                    valFinProductTypeBatchMapper.insertBatch(rstValFinProductType);
//                }else{
//                    int times = (int)Math.ceil( rstValFinProductType.size()/1000.0 );
//                    for(int i=0; i<times; i++ ){
//                        valFinProductTypeBatchMapper.deleteBatch(rstValFinProductType.subList(i*1000, Math.min((i+1)*1000, rstValFinProductType.size()-1)));
//                        valFinProductTypeBatchMapper.insertBatch(rstValFinProductType.subList(i*1000, Math.min((i+1)*1000, rstValFinProductType.size()-1)));
//                    }
//                }
//            }
            
            // 入库
            if (CollectionUtils.isNotEmpty(rstValFinProductType)) {
                //定义一个固定大小插入存放集合
                List<FinProductType> listInsert =  new ArrayList<FinProductType>();
                for(FinProductType e :rstValFinProductType){
                   //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valFinProductTypeBatchMapper.deleteBatch(listInsert); 
                        valFinProductTypeBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valFinProductTypeBatchMapper.deleteBatch(listInsert); 
                    valFinProductTypeBatchMapper.insertBatch(listInsert);
                }
            }
            
            /**add by wujie on 20180814 end**/
            
            // 金融产品账户
	          List<StlFinProductAcct> listValFinProductAcct = result.getValFinProductAcctList();
            // 入库
            if (CollectionUtils.isNotEmpty(listValFinProductAcct)) {
              //定义一个固定大小插入存放集合
                List<StlFinProductAcct> listInsert =  new ArrayList<StlFinProductAcct>();
                for(StlFinProductAcct e :listValFinProductAcct){
                    //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valFinProductAcctBatchMapper.deleteBatch(listInsert); 
                        valFinProductAcctBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valFinProductAcctBatchMapper.deleteBatch(listInsert); 
                    valFinProductAcctBatchMapper.insertBatch(listInsert);
                }
            }
            
            
            // 账套清算账户
            List<BokBooksetCapacctInfo> listValBooksetCapacctInfo = result.getValBooksetCapacctInfoList();
            // 入库
            if (CollectionUtils.isNotEmpty(listValBooksetCapacctInfo)) {
                //定义一个固定大小插入存放集合
                List<BokBooksetCapacctInfo> listInsert =  new ArrayList<BokBooksetCapacctInfo>();
                for(BokBooksetCapacctInfo e :listValBooksetCapacctInfo){
                    //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valBooksetCapacctInfoBatchMapper.deleteBatch(listInsert); 
                        valBooksetCapacctInfoBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valBooksetCapacctInfoBatchMapper.deleteBatch(listInsert); 
                    valBooksetCapacctInfoBatchMapper.insertBatch(listInsert);
                }
            }
            
            
            // 日历信息表
            List<MstCalendarInfo> listValCalendarInfo = result.getValCalendarInfoList();
            // 入库
            if (CollectionUtils.isNotEmpty(listValCalendarInfo)) {
                //定义一个固定大小插入存放集合
                List<MstCalendarInfo> listInsert =  new ArrayList<MstCalendarInfo>();
                for(MstCalendarInfo e :listValCalendarInfo){
                    //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valCalendarInfoBatchMapper.deleteBatch(listInsert); 
                        valCalendarInfoBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valCalendarInfoBatchMapper.deleteBatch(listInsert); 
                    valCalendarInfoBatchMapper.insertBatch(listInsert);
                }
            }
            
            
            // 交易场所日历
            List<MstTradeCalendar> listValTradeCalendar = result.getValTradeCalendarList();
            // 入库
            if (CollectionUtils.isNotEmpty(listValTradeCalendar)) {
                //定义一个固定大小插入存放集合
                List<MstTradeCalendar> listInsert =  new ArrayList<MstTradeCalendar>();
                for(MstTradeCalendar e :listValTradeCalendar){
                    //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valTradeCalendarBatchMapper.deleteBatch(listInsert); 
                        valTradeCalendarBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valTradeCalendarBatchMapper.deleteBatch(listInsert); 
                    valTradeCalendarBatchMapper.insertBatch(listInsert);
                }
            //   valTradeCalendarBatchMapper.insertBatch(listValTradeCalendar);
            }
            
            // 交易场所日历
            List<MstHoliday> listValHoliday = result.getValHolidayList();
            // 入库
            if (CollectionUtils.isNotEmpty(listValHoliday)) {
                //定义一个固定大小插入存放集合
                List<MstHoliday> listInsert =  new ArrayList<MstHoliday>();
                for(MstHoliday e :listValHoliday){
                    //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valHolidayBatchMapper.deleteBatch(listInsert); 
                        valHolidayBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valHolidayBatchMapper.deleteBatch(listInsert); 
                    valHolidayBatchMapper.insertBatch(listInsert);
                }
            }
            // 日志
            List<ValInterflog> valInterflog = result.getValInterflog();
            // 入库
            if (CollectionUtils.isNotEmpty(valInterflog)) {
              //定义一个固定大小插入存放集合
                List<ValInterflog> listInsert =  new ArrayList<>();
                for(ValInterflog e :valInterflog){
                   //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valInterflogBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valInterflogBatchMapper.insertBatch(listInsert);
                }

            }
        }
    }

}
