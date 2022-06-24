package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.standard.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValBokDetailIdBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValBooksetCapacctInfoBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductAcctBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.standard.entity.StandardInfoProcessorResult;
import com.joyintech.tams.common.base.entity.BokBooksetCapacctInfo;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.StlFinProductAcct;
import com.joyintech.tams.common.base.entity.ValInterflog;

@Service
@Scope("prototype")
public class StandardInfoRecWriter extends InterfBaseWriter<BaseEntity>{


    /**
     * 金融产品账户BatchMapper
     */
    @Autowired
    private ValFinProductAcctBatchMapper valFinProductAcctBatchMapper;

    /**
     * 金融产品账户BatchMapper
     */
    @Autowired
    private ValBooksetCapacctInfoBatchMapper valBooksetCapacctInfoBatchMapper;
    
    /**
     * 账务明细代码表
     */
    @Autowired
    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;
    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;
    
    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;
    
    @SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends BaseEntity> items) throws Exception {

        // 获取process返回组合的list值
        List<StandardInfoProcessorResult> listResult = (List<StandardInfoProcessorResult>)items;
		
        for(StandardInfoProcessorResult result:listResult){
        	 // 金融产品账户
            List<StlFinProductAcct> listValFinProductAcct = result.getValFinProductAcctList();
            
            Map<String,StlFinProductAcct> mapValFinProductAcct = new HashMap<String,StlFinProductAcct>();
            List<StlFinProductAcct> rstValFinProductAcct= new ArrayList<StlFinProductAcct>();
            for(StlFinProductAcct obj :listValFinProductAcct){
                if(mapValFinProductAcct.get(obj.getFinprodId()+obj.getFinprodType()+obj.getAcctNo()+obj.getCcy()) ==null){
                    rstValFinProductAcct.add(obj);
                }
                mapValFinProductAcct.put(obj.getFinprodId()+obj.getFinprodType()+obj.getAcctNo()+obj.getCcy(), obj);
            }
//            // 入库
//            if (CollectionUtils.isNotEmpty(rstValFinProductAcct)) {
//                if(rstValFinProductAcct.size()<1000){
//                    valFinProductAcctBatchMapper.deleteBatch(rstValFinProductAcct);
//                    valFinProductAcctBatchMapper.insertBatch(rstValFinProductAcct);
//                }else{
//                    int times = (int)Math.ceil( rstValFinProductAcct.size()/1000.0 );
//                    for(int i=0; i<times; i++ ){
//                        valFinProductAcctBatchMapper.deleteBatch(rstValFinProductAcct.subList(i*1000, Math.min((i+1)*1000, rstValFinProductAcct.size()-1)));
//                        valFinProductAcctBatchMapper.insertBatch(rstValFinProductAcct.subList(i*1000, Math.min((i+1)*1000, rstValFinProductAcct.size()-1)));
//                    }
//                }
//            }
            
            if (CollectionUtils.isNotEmpty(rstValFinProductAcct)) {
                // 定义一个固定大小插入存放集合
                List<StlFinProductAcct> listInsert = new ArrayList<StlFinProductAcct>();
                for (StlFinProductAcct e : rstValFinProductAcct) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valFinProductAcctBatchMapper.deleteBatch(listInsert);
                        valFinProductAcctBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinProductAcctBatchMapper.deleteBatch(listInsert);
                    valFinProductAcctBatchMapper.insertBatch(listInsert);
                }
            }
            
            
            // 账套清算账户
            List<BokBooksetCapacctInfo> listValBooksetCapacctInfo = result.getValBooksetCapacctInfoList();
            Map<String,BokBooksetCapacctInfo> mapValBooksetCapacctInfo = new HashMap<String,BokBooksetCapacctInfo>();
            List<BokBooksetCapacctInfo> rstValBooksetCapacctInfo= new ArrayList<BokBooksetCapacctInfo>();
            for(BokBooksetCapacctInfo obj :listValBooksetCapacctInfo){
                if(mapValBooksetCapacctInfo.get(obj.getBooksetId()+obj.getAcctNo()+obj.getCcy()) ==null){
                    rstValBooksetCapacctInfo.add(obj);
                }
                mapValBooksetCapacctInfo.put(obj.getBooksetId()+obj.getAcctNo()+obj.getCcy(), obj);
            }
//            // 入库
//            if (CollectionUtils.isNotEmpty(rstValBooksetCapacctInfo)) {
//                if(rstValBooksetCapacctInfo.size()<1000){
//                    valBooksetCapacctInfoBatchMapper.deleteBatch(rstValBooksetCapacctInfo);
//                    valBooksetCapacctInfoBatchMapper.insertBatch(rstValBooksetCapacctInfo);
//                }else{
//                    int times = (int)Math.ceil( rstValBooksetCapacctInfo.size()/1000.0 );
//                    for(int i=0; i<times; i++ ){
//                        valBooksetCapacctInfoBatchMapper.deleteBatch(rstValBooksetCapacctInfo.subList(i*1000, Math.min((i+1)*1000, rstValBooksetCapacctInfo.size()-1)));
//                        valBooksetCapacctInfoBatchMapper.insertBatch(rstValBooksetCapacctInfo.subList(i*1000, Math.min((i+1)*1000, rstValBooksetCapacctInfo.size()-1)));
//                    }
//                }
//                
//            }
            
            if (CollectionUtils.isNotEmpty(rstValBooksetCapacctInfo)) {
                // 定义一个固定大小插入存放集合
                List<BokBooksetCapacctInfo> listInsert = new ArrayList<BokBooksetCapacctInfo>();
                for (BokBooksetCapacctInfo e : rstValBooksetCapacctInfo) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valBooksetCapacctInfoBatchMapper.deleteBatch(listInsert);
                        valBooksetCapacctInfoBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valBooksetCapacctInfoBatchMapper.deleteBatch(listInsert);
                    valBooksetCapacctInfoBatchMapper.insertBatch(listInsert);
                }
            }
            /**add by wujie on 20180814 start remark:活期利息改造**/
//            if(result.getParamMap()!=null && result.getParamMap().get("BokDetailId_no")!=null){
//            	List<BokDetailId> listValBokDetailId_no = (List<BokDetailId>)result.getParamMap().get("BokDetailId_no");
//                // 定义一个固定大小插入存放集合
//            	  if (CollectionUtils.isNotEmpty(listValBokDetailId_no)) {
//                      List<BokDetailId> listInsert = new ArrayList<BokDetailId>();
//                      for (BokDetailId e : listValBokDetailId_no) {
//                          // 小于最大值
//                          if (listInsert.size() < maxSqlNum) {
//                              listInsert.add(e);
//                          }
//                          else if (listInsert.size() >= maxSqlNum) {
//                              valBokDetailIdBatchMapper.deleteBatch(listInsert);
//                              listInsert.clear();
//                              listInsert.add(e);
//                          }
//                      }
//                      // 如果循环结束，小于最大值
//                      if (CollectionUtils.isNotEmpty(listInsert)) {
//                          valBokDetailIdBatchMapper.deleteBatch(listInsert);
//                      }
//            	  }
//            }
            // 账务明细代码表
            List<BokDetailId> listValBokDetailId = result.getBokDetailIdList()==null?new ArrayList<BokDetailId>():result.getBokDetailIdList();
            Map<String,BokDetailId> map = new HashMap<String,BokDetailId>();
            List<BokDetailId> rst = new ArrayList<BokDetailId>();
            for(BokDetailId obj :listValBokDetailId){
            	if(map.get(obj.getBokDetailId()+obj.getBooksetId()) ==null){
            		rst.add(obj);
            	}
            	map.put(obj.getBokDetailId()+obj.getBooksetId(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rst)) {
                // 定义一个固定大小插入存放集合
                List<BokDetailId> listInsert = new ArrayList<BokDetailId>();
                for (BokDetailId e : rst) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valBokDetailIdBatchMapper.deleteBatch(listInsert);
                        valBokDetailIdBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valBokDetailIdBatchMapper.deleteBatch(listInsert);
                    valBokDetailIdBatchMapper.insertBatch(listInsert);
                }
            }
            /**add by wujie on 20180814 end**/
            
            // 入库
            List<ValInterflog> valInterflog = result.getValInterflog();
//            if (CollectionUtils.isNotEmpty(valInterflog)) {
//                if(valInterflog.size()<1000){
//                    valInterflogBatchMapper.insertBatch(valInterflog);
//                }else{
//                    int times = (int)Math.ceil( valInterflog.size()/1000.0 );
//                    for(int i=0; i<times; i++ ){
//                            valInterflogBatchMapper.insertBatch(valInterflog.subList(i*1000, Math.min((i+1)*1000, valInterflog.size()-1)));
//                    }
//                }
//            }
            // 入库
            if (CollectionUtils.isNotEmpty(valInterflog)) {
                // 定义一个固定大小插入存放集合
                List<ValInterflog> listInsert = new ArrayList<>();
                for (ValInterflog e : valInterflog) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valInterflogBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valInterflogBatchMapper.insertBatch(listInsert);
                }
            }
        }
	}
    
}
