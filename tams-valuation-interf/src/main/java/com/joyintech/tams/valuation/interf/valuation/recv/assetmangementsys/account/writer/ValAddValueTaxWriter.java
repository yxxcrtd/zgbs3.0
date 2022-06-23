package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.account.writer;

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
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValBokBookdetailCurrentBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValBokDetailIdBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.account.dto.ValueAddedTaxRecProcessorResult;
import com.joyintech.tams.common.base.entity.BokBookdetailCurrent;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.ValInterflog;

/**
 * 增值税信息接口Writer
* @author dongxiangyang
* @version 1.0 2018年7月06日
* @see ValAddValueTaxWriter
* @since 1.0
*/
@Service
@Scope("prototype")
public class ValAddValueTaxWriter extends InterfBaseWriter<BaseEntity>{
	/**
     * 增值税账务明细表mapper
     */
    @Autowired
    private ValBokBookdetailCurrentBatchMapper batchMapper;
    /**
     * 增值税账务明细代码表mapper
     */
    @Autowired
    private ValBokDetailIdBatchMapper idBatchMapper;
	/**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;
    
    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;


    /**
     * 更新数据 {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends BaseEntity> items) throws Exception {
    	 // 获取process返回组合的list值
    	List<ValueAddedTaxRecProcessorResult>  resultList=(List<ValueAddedTaxRecProcessorResult>)items;
    	
    	  // 循环processor结果DTO，读取所有的入库数据结构LIST，并进行入库操作。
    	for(ValueAddedTaxRecProcessorResult result:resultList){
    		//增值税账务明细
    		List<BokBookdetailCurrent> valBokBookList=result.getListValBokBookdetailCurrent();
    		  // 入库
            if (CollectionUtils.isNotEmpty(valBokBookList)){
                List<BokBookdetailCurrent> listInsert =  new ArrayList<>();
                for (BokBookdetailCurrent e : valBokBookList) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    } else if (listInsert.size() >= maxSqlNum) {
                        batchMapper.deleteBatch(listInsert);
                        batchMapper.insert(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                    
                }
                if (CollectionUtils.isNotEmpty(listInsert)){
                    batchMapper.deleteBatch(listInsert);
                    batchMapper.insert(listInsert);
                }
            }
            //增值税涨幅明细代码表
            List<BokDetailId> listValBokDetailId=result.getListValBokDetailId();
            Map<String,BokDetailId> map = new HashMap<String,BokDetailId>();
            List<BokDetailId> rst = new ArrayList<BokDetailId>();
            for(BokDetailId obj :listValBokDetailId){
            	if(map.get(obj.getBokDetailId()+obj.getBooksetId()) ==null){
            		rst.add(obj);
            	}
            	map.put(obj.getBokDetailId()+obj.getBooksetId(), obj);
            }
            //入库
            if (CollectionUtils.isNotEmpty(rst)){
            	idBatchMapper.deleteBatch(rst);
            	idBatchMapper.insertBatch(rst);
            }
            
            // 日志
            List<ValInterflog> valInterflog = result.getListValInterflog();
            // 入库
            insertValInterflog(valInterflog);
            
    	}
    	
	}
    /**
     * 日志批量入库
     * @param valInterflog
     */
    public void insertValInterflog(List<ValInterflog> valInterflog){
    	//判断是否非空
    	if(CollectionUtils.isNotEmpty(valInterflog)){
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
