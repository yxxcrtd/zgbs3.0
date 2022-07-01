package com.joyintech.tams.valuation.common.interf.base.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.dao.BokBooksetDateMapper;
import com.joyintech.tams.common.base.dao.BokBooksetMapper;
import com.joyintech.tams.common.base.entity.BokAcctLayeringInfo;
import com.joyintech.tams.common.base.entity.BokAcctSubjectInfo;
import com.joyintech.tams.common.base.entity.BokBookset;
import com.joyintech.tams.common.base.entity.BokBooksetCapacctInfo;
import com.joyintech.tams.common.base.entity.BokBooksetDate;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokBooksetPlan;
import com.joyintech.tams.common.base.entity.BokBooksetRe;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductValuationPara;
import com.joyintech.tams.common.base.entity.StlFinProductAcct;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.BokBooksetPlanBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValAcctLayeringInfoBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValAcctSubjectInfoBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValBokBooksetExtBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValBokBooksetReMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValBokDetailIdBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValBooksetCapacctInfoBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashCalcRuleBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashInfoBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductAcctBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductAddBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductTypeBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductValuationParaBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dao.ChannelCommonMapper;
import com.joyintech.tams.valuation.common.interf.base.dto.BokBooksetSonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.ValChannelProdDealDto;

/**
 * 通道共同
 * 
 * @author wanggengyun
 * @version 1.0 2019-03-13
 * @see InterfCommonService
 * @since 1.0
 */
@Service
public class ChannelCommonService{

	@Autowired
	private ChannelCommonMapper channelCommonMapper;	
	
    @Autowired
    private ValBokBooksetExtBatchMapper valBokBooksetExtBatchMapper;
    
    @Autowired
    private BokBooksetDateMapper bokBooksetDateMapper;
    
    @Autowired
    private BokBooksetMapper bokBooksetMapper;
    
    @Autowired
    private ValFinProductAddBatchMapper valFinProductExtBatchMapper;
    
    @Autowired
    private ValCashInfoBatchMapper valCashInfoBatchMapper;
    
    @Autowired
    private ValFinProductBatchMapper valFinProductBatchMapper;
    
    @Autowired
    private ValFinProductTypeBatchMapper valFinProductTypeBatchMapper;
    
    @Autowired
    private ValFinProductValuationParaBatchMapper valFinProductValuationParaBatchMapper;
    
    @Autowired
    private ValBokBooksetReMapper valBokBooksetReMapper;
    
    @Autowired
    private ValAcctSubjectInfoBatchMapper valAcctSubjectInfoBatchMapper;
    
    @Autowired
    private ValAcctLayeringInfoBatchMapper valAcctLayeringInfoBatchMapper;
    
    @Autowired
    private BokBooksetPlanBatchMapper bokBooksetPlanBatchMapper;
    
    @Autowired
    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;
    
    @Autowired
    private ValCashCalcRuleBatchMapper valCashCalcRuleBatchMapper;
    
    @Autowired
    private ValFinProductAcctBatchMapper valFinProductAcctBatchMapper;
    
    @Autowired
    private ValBooksetCapacctInfoBatchMapper valBooksetCapacctInfoBatchMapper;
    
    /**
     * 设定insert、delete删除条数
     * */
    private int maxSqlNum=InterfCommonConstant.maxSqlNum;
    
    
    /**
     * 标的类金融产品表入库
     * */
    public void insertValFinProductBatch(List<FinProduct> finProductList){
    	/**modify by wujie on 20190321 start
        valFinProductBatchMapper.insertBatch(finProductList);
        **/
        if (CollectionUtils.isNotEmpty(finProductList)) {
        	this.deleteBatchValFinProductBatch(finProductList);
		    // 定义一个固定大小插入存放集合
		    List<FinProduct> listInsert = new ArrayList<FinProduct>();
		    for(FinProduct e : finProductList){
		        // 小于最大值
		        if (listInsert.size() < maxSqlNum) {
		            listInsert.add(e);
		        } else if (listInsert.size() >= maxSqlNum) {
		        	valFinProductBatchMapper.insertBatch(listInsert);
		            listInsert.clear();
		            listInsert.add(e);
		        }    
		    }
		    // 如果循环结束，小于最大值
		    if (CollectionUtils.isNotEmpty(listInsert)) {
		    	valFinProductBatchMapper.insertBatch(listInsert);	        
		    }
		}
		/**modify by wujie on 20190321 end **/
    }
    
    /**
     * 标的类金融产品表入库删除
     * add by wujie on 20190321
     * */
    public void deleteBatchValFinProductBatch(List<FinProduct> finProductList){
        if (CollectionUtils.isNotEmpty(finProductList)) {
		    // 定义一个固定大小插入存放集合
		    List<FinProduct> listInsert = new ArrayList<FinProduct>();
		    for(FinProduct e : finProductList){
		        // 小于最大值
		        if (listInsert.size() < maxSqlNum) {
		            listInsert.add(e);
		        } else if (listInsert.size() >= maxSqlNum) {
		        	valFinProductBatchMapper.deleteBatch(listInsert);
		            listInsert.clear();
		            listInsert.add(e);
		        }    
		    }
		    // 如果循环结束，小于最大值
		    if (CollectionUtils.isNotEmpty(listInsert)) {
		    	valFinProductBatchMapper.deleteBatch(listInsert);	        
		    }
		}
    }
    
    /**
     * 标的金融产品扩展入库
     * */
    public void insertFinProductExtBatch(List<FinProductAdd> finProductExtList){
    	/**modify by wujie on 20190321
    	valFinProductExtBatchMapper.insertBatch(finProductExtList);
    	**/
		if (CollectionUtils.isNotEmpty(finProductExtList)) {
			this.deleteBatchFinProductExtBatch(finProductExtList);
		    // 定义一个固定大小插入存放集合
		    List<FinProductAdd> listInsert = new ArrayList<FinProductAdd>();
		    for(FinProductAdd e : finProductExtList){
		        // 小于最大值
		        if (listInsert.size() < maxSqlNum) {
		            listInsert.add(e);
		        } else if (listInsert.size() >= maxSqlNum) {
		        	valFinProductExtBatchMapper.insertBatch(listInsert);
		            listInsert.clear();
		            listInsert.add(e);
		        }    
		    }
		    // 如果循环结束，小于最大值
		    if (CollectionUtils.isNotEmpty(listInsert)) {
	        	valFinProductExtBatchMapper.insertBatch(listInsert);	        
		    }
		}
		/**modify by wujie on 20190321 end **/
    }
    
    /**
     * 标的金融产品扩展入库删除
     * add by wujie on 20190321
     * */
    public void deleteBatchFinProductExtBatch(List<FinProductAdd> finProductExtList){
		if (CollectionUtils.isNotEmpty(finProductExtList)) {
		    // 定义一个固定大小插入存放集合
		    List<FinProductAdd> listInsert = new ArrayList<FinProductAdd>();
		    for(FinProductAdd e : finProductExtList){
		        // 小于最大值
		        if (listInsert.size() < maxSqlNum) {
		            listInsert.add(e);
		        } else if (listInsert.size() >= maxSqlNum) {
		        	valFinProductExtBatchMapper.deleteBatch(listInsert);
		            listInsert.clear();
		            listInsert.add(e);
		        }    
		    }
		    // 如果循环结束，小于最大值
		    if (CollectionUtils.isNotEmpty(listInsert)) {
		    	valFinProductExtBatchMapper.deleteBatch(listInsert);
		    }
		}
    }
    
   
    /**
     * 金融产品分类表入库
     * */
    public void insertValFinProductTypeBatch(List<FinProductType> finProductTypeList){
    	/**modify by wujie on 20190321 start
    	valFinProductTypeBatchMapper.insertBatch(finProductTypeList);
    	**/
    	if (CollectionUtils.isNotEmpty(finProductTypeList)) {
    		this.deleteBatchValFinProductTypeBatch(finProductTypeList);
		    // 定义一个固定大小插入存放集合
		    List<FinProductType> listInsert = new ArrayList<FinProductType>();
		    for(FinProductType e : finProductTypeList){
		        // 小于最大值
		        if (listInsert.size() < maxSqlNum) {
		            listInsert.add(e);
		        } else if (listInsert.size() >= maxSqlNum) {
		        	valFinProductTypeBatchMapper.insertBatch(listInsert);
		            listInsert.clear();
		            listInsert.add(e);
		        }    
		    }
		    // 如果循环结束，小于最大值
		    if (CollectionUtils.isNotEmpty(listInsert)) {
		    	valFinProductTypeBatchMapper.insertBatch(listInsert);
		    }
		}
    	/**modify by wujie on 20190321 end**/
    }
    
    /**
     * 金融产品分类表入库删除
     * add by wujie on 20190321
     * */
    public void deleteBatchValFinProductTypeBatch(List<FinProductType> finProductTypeList){
    	if (CollectionUtils.isNotEmpty(finProductTypeList)) {
		    // 定义一个固定大小插入存放集合
		    List<FinProductType> listInsert = new ArrayList<FinProductType>();
		    for(FinProductType e : finProductTypeList){
		        // 小于最大值
		        if (listInsert.size() < maxSqlNum) {
		            listInsert.add(e);
		        } else if (listInsert.size() >= maxSqlNum) {
		        	valFinProductTypeBatchMapper.deleteBatch(listInsert);
		            listInsert.clear();
		            listInsert.add(e);
		        }    
		    }
		    // 如果循环结束，小于最大值
		    if (CollectionUtils.isNotEmpty(listInsert)) {
		    	valFinProductTypeBatchMapper.deleteBatch(listInsert);
		    }
		}
    }
    
    
    /**
     * 金融产品估值参数表_新增
     * add by wujie on 20190325
     * */
    public void insertBatchFinProductValuationPara(List<FinProductValuationPara> finProductValuationParaList){
    	if (CollectionUtils.isNotEmpty(finProductValuationParaList)) {
    		this.deleteBatchFinProductValuationPara(finProductValuationParaList);
		    // 定义一个固定大小插入存放集合
		    List<FinProductValuationPara> listInsert = new ArrayList<FinProductValuationPara>();
		    for(FinProductValuationPara e : finProductValuationParaList){
		        // 小于最大值
		        if (listInsert.size() < maxSqlNum) {
		            listInsert.add(e);
		        } else if (listInsert.size() >= maxSqlNum) {
		        	valFinProductValuationParaBatchMapper.insertBatch(listInsert);
		            listInsert.clear();
		            listInsert.add(e);
		        }    
		    }
		    // 如果循环结束，小于最大值
		    if (CollectionUtils.isNotEmpty(listInsert)) {
		    	valFinProductValuationParaBatchMapper.insertBatch(listInsert);
		    }
		}
    }
    
    /**
     * 金融产品估值参数表_删除
     * add by wujie on 20190325
     * */
    public void deleteBatchFinProductValuationPara(List<FinProductValuationPara> finProductValuationParaList){
    	if (CollectionUtils.isNotEmpty(finProductValuationParaList)) {
		    // 定义一个固定大小插入存放集合
		    List<FinProductValuationPara> listInsert = new ArrayList<FinProductValuationPara>();
		    for(FinProductValuationPara e : finProductValuationParaList){
		        // 小于最大值
		        if (listInsert.size() < maxSqlNum) {
		            listInsert.add(e);
		        } else if (listInsert.size() >= maxSqlNum) {
		        	valFinProductValuationParaBatchMapper.deleteBatch(listInsert);
		            listInsert.clear();
		            listInsert.add(e);
		        }    
		    }
		    // 如果循环结束，小于最大值
		    if (CollectionUtils.isNotEmpty(listInsert)) {
		    	valFinProductValuationParaBatchMapper.deleteBatch(listInsert);
		    }
		}
    }
    
    /**
     * 账套信息入库
     * add by wujie on 20190322
     * */
    public void insertBatchBokBookset(List<BokBookset> bokBooksetReList){
    	if (CollectionUtils.isNotEmpty(bokBooksetReList)) {
		    for(BokBookset e : bokBooksetReList){
		    	bokBooksetMapper.deleteByPrimaryKey(e.getBooksetId());
		    	bokBooksetMapper.insert(e);
		    }
    	}
    }
    
    
    /**
     * 母子账套关系入库
     * */
    public void insertBokBooksetReBatch(List<BokBooksetRe> bokBooksetReList){
    	/**modify by wujie on 20190321 start
    	valBokBooksetReMapper.insertBatch(bokBooksetReList);
    	**/
    	if (CollectionUtils.isNotEmpty(bokBooksetReList)) {
    		this.deleteBatchBokBooksetReBatch(bokBooksetReList);
		    // 定义一个固定大小插入存放集合
		    List<BokBooksetRe> listInsert = new ArrayList<BokBooksetRe>();
		    for(BokBooksetRe e : bokBooksetReList){
		        // 小于最大值
		        if (listInsert.size() < maxSqlNum) {
		            listInsert.add(e);
		        } else if (listInsert.size() >= maxSqlNum) {
		        	valBokBooksetReMapper.insertBatch(listInsert);
		            listInsert.clear();
		            listInsert.add(e);
		        }    
		    }
		    // 如果循环结束，小于最大值
		    if (CollectionUtils.isNotEmpty(listInsert)) {
		    	valBokBooksetReMapper.insertBatch(listInsert);
		    }
		}
    	/**modify by wujie on 20190321 end**/
    }
    
    /**
     * 母子账套关系入库删除
     * add by wujie on 20190321
     * */
    public void deleteBatchBokBooksetReBatch(List<BokBooksetRe> bokBooksetReList){
    	if (CollectionUtils.isNotEmpty(bokBooksetReList)) {
		    // 定义一个固定大小插入存放集合
		    List<BokBooksetRe> listInsert = new ArrayList<BokBooksetRe>();
		    for(BokBooksetRe e : bokBooksetReList){
		        // 小于最大值
		        if (listInsert.size() < maxSqlNum) {
		            listInsert.add(e);
		        } else if (listInsert.size() >= maxSqlNum) {
		        	valBokBooksetReMapper.deleteBatch(listInsert);
		            listInsert.clear();
		            listInsert.add(e);
		        }    
		    }
		    // 如果循环结束，小于最大值
		    if (CollectionUtils.isNotEmpty(listInsert)) {
		    	valBokBooksetReMapper.deleteBatch(listInsert);
		    }
		}
    }
    
    /**
     * 账套日期入库
     * */
    public void insertBokBooksetDateBatch(List<BokBooksetDate> bokBooksetDateList){
        if (CollectionUtils.isNotEmpty(bokBooksetDateList)) {
            for (BokBooksetDate valBokBooksetDate : bokBooksetDateList) {
                // 模板账套日期
                if (valBokBooksetDate != null) {
                	bokBooksetDateMapper.deleteByPrimaryKey(valBokBooksetDate.getBooksetId());
                	bokBooksetDateMapper.insert(valBokBooksetDate);
                }
            }
        }
    }
    
    /**
     * 账套配置List入库
     * @param list
     */
    public void insertBokBooksetExtBatch(List<BokBooksetExt> list){
        if (CollectionUtils.isNotEmpty(list)) {
        	/**modify by wujie on 20190321
        	valBokBooksetExtBatchMapper.deleteBatch(list);
        	valBokBooksetExtBatchMapper.insertBatch(list);
        	**/
        	this.deleteBatchBokBooksetExtBatch(list);
        	 // 定义一个固定大小插入存放集合
		    List<BokBooksetExt> listInsert = new ArrayList<BokBooksetExt>();
		    for(BokBooksetExt e : list){
		        // 小于最大值
		        if (listInsert.size() < maxSqlNum) {
		            listInsert.add(e);
		        } else if (listInsert.size() >= maxSqlNum) {
		        	valBokBooksetExtBatchMapper.insertBatch(listInsert);
		            listInsert.clear();
		            listInsert.add(e);
		        }    
		    }
		    // 如果循环结束，小于最大值
		    if (CollectionUtils.isNotEmpty(listInsert)) {
		        valBokBooksetExtBatchMapper.insertBatch(listInsert);			        
		    }
        }
    }
    
    /**
     * 账套配置List批量删除
     * @param list
     * add by wujie on 20190322
     */
    public void deleteBatchBokBooksetExtBatch(List<BokBooksetExt> list){
    	if (CollectionUtils.isNotEmpty(list)) {
        	 // 定义一个固定大小插入存放集合
		    List<BokBooksetExt> listInsert = new ArrayList<BokBooksetExt>();
		    for(BokBooksetExt e : list){
		        // 小于最大值
		        if (listInsert.size() < maxSqlNum) {
		            listInsert.add(e);
		        } else if (listInsert.size() >= maxSqlNum) {
		        	valBokBooksetExtBatchMapper.deleteBatchById(listInsert);
		            listInsert.clear();
		            listInsert.add(e);
		        }    
		    }
		    // 如果循环结束，小于最大值
		    if (CollectionUtils.isNotEmpty(listInsert)) {
		    	valBokBooksetExtBatchMapper.deleteBatchById(listInsert);
		    }
        }
    }
    
    
    /**
     * 核算主体代码入库
     * */
    public void insertBokAcctSubjectInfoBatch(List<BokAcctSubjectInfo> bokAcctSubjectInfoList){
    	 //核算主体代码
		if (CollectionUtils.isNotEmpty(bokAcctSubjectInfoList)) {
		    // 定义一个固定大小插入存放集合
		    List<BokAcctSubjectInfo> listInsert = new ArrayList<BokAcctSubjectInfo>();
		    for(BokAcctSubjectInfo e : bokAcctSubjectInfoList){
		        // 小于最大值
		        if (listInsert.size() < maxSqlNum) {
		            listInsert.add(e);
		        } else if (listInsert.size() >= maxSqlNum) {
		            valAcctSubjectInfoBatchMapper.deleteBatch(listInsert);
		            valAcctSubjectInfoBatchMapper.insert(listInsert);
		            listInsert.clear();
		            listInsert.add(e);
		        }    
		    }
		    // 如果循环结束，小于最大值
		    if (CollectionUtils.isNotEmpty(listInsert)) {
		        valAcctSubjectInfoBatchMapper.deleteBatch(listInsert);
                valAcctSubjectInfoBatchMapper.insert(listInsert);			        
		    }
		}
    }
    /**
     * 核算主分层入库
     * */
    public void insertBokAcctLayeringInfoBatch(List<BokAcctLayeringInfo> bokAcctLayeringInfoList){
    	//入库 
        if (CollectionUtils.isNotEmpty(bokAcctLayeringInfoList)) {
            // 定义一个固定大小插入存放集合
            List<BokAcctLayeringInfo> listInsert = new ArrayList<BokAcctLayeringInfo>();
            for(BokAcctLayeringInfo e : bokAcctLayeringInfoList){
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                } else if (listInsert.size() >= maxSqlNum) {
                    valAcctLayeringInfoBatchMapper.deleteBatch(listInsert);
                    valAcctLayeringInfoBatchMapper.insert(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }    
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valAcctLayeringInfoBatchMapper.deleteBatch(listInsert);
                valAcctLayeringInfoBatchMapper.insert(listInsert);                   
            }
            
        }
    }
    /**
     * 账套估值方案入库(税务方案)
     * */
    public void insertBokBooksetPlanBatch(List<BokBooksetPlan> bokBooksetPlanList){
        if (CollectionUtils.isNotEmpty(bokBooksetPlanList)) {
        	/**add by wujie on 20190321 start**/
        	this.deleteBatchBokBooksetPlanBatch(bokBooksetPlanList);
        	/**add by wujie on 20190321 end**/
            // 定义一个固定大小插入存放集合
            List<BokBooksetPlan> listInsert = new ArrayList<BokBooksetPlan>();
            for (BokBooksetPlan e : bokBooksetPlanList) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                	/**modify by wujie on 20190321
                    bokBooksetPlanBatchMapper.deleteBatch(listInsert);**/
                    bokBooksetPlanBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
            	/**modify by wujie on 20190321
                bokBooksetPlanBatchMapper.deleteBatch(listInsert);**/
                bokBooksetPlanBatchMapper.insertBatch(listInsert);
            }
        }
    }
    
    /**
     * 账套估值方案入库(税务方案)-批量删除
     * add by wujie on 20190321
     * */
    public void deleteBatchBokBooksetPlanBatch(List<BokBooksetPlan> bokBooksetPlanList){
    	if (CollectionUtils.isNotEmpty(bokBooksetPlanList)) {
    		Map<String,BokBooksetPlan> delMap = new HashMap<String,BokBooksetPlan>();
        	List<BokBooksetPlan> delList = new ArrayList<BokBooksetPlan>();
        	for (BokBooksetPlan e : bokBooksetPlanList) {
        		 String key = e.getBooksetId();
        		 if(delMap.get(key) ==null){
        			 delList.add(e);
        		 }
        		 delMap.put(key, e);
        	}
        	
        	// 定义一个固定大小插入存放集合
            List<BokBooksetPlan> listInsert = new ArrayList<BokBooksetPlan>();
            for (BokBooksetPlan e : delList) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    bokBooksetPlanBatchMapper.deleteBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                bokBooksetPlanBatchMapper.deleteBatch(listInsert);
            }
    	}
    }
    
    
    
    
    /**
     * 账务明细代码
     * */
    public void insertBokDetailIdBatch(List<BokDetailId> bokDetailIdList){
    	Map<String, BokDetailId> map = new HashMap<String, BokDetailId>();
        List<BokDetailId> rst = new ArrayList<BokDetailId>();
        for (BokDetailId obj : bokDetailIdList) {
            if (map.get(obj.getBokDetailId() + obj.getBooksetId()) == null) {
                rst.add(obj);
            }
            map.put(obj.getBokDetailId() + obj.getBooksetId(), obj);
        }
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
    }
    /**
     * 子账套通道现金流信息
     * */
    public void insertFinCashInfoBatch(List<FinCashInfo> finCashInfoList){
    	// 入库
        if (CollectionUtils.isNotEmpty(finCashInfoList)) {
        	
        	/**add by wujie on 20190321 start**/
        	this.deleteBatchFinCashInfoBatch(finCashInfoList);
        	/**add by wujie on 20190321 end**/
        	
            // 定义一个固定大小插入存放集合
            List<FinCashInfo> listInsert = new ArrayList<FinCashInfo>();
            for (FinCashInfo e : finCashInfoList) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                	/**modify by wujie on 20190321
                    valCashInfoBatchMapper.deleteBatch(listInsert);**/
                    valCashInfoBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
            	/**modify by wujie on 20190321
                valCashInfoBatchMapper.deleteBatch(listInsert);**/
                valCashInfoBatchMapper.insertBatch(listInsert);
            }

        }
    }
    
    /**
     * 子账套通道现金流信息删除
     * add by wujie on 20190321
     * */
    public void deleteBatchFinCashInfoBatch(List<FinCashInfo> finCashInfoList){
    	// 入库
        if (CollectionUtils.isNotEmpty(finCashInfoList)) {
            // 定义一个固定大小插入存放集合
            List<FinCashInfo> listInsert = new ArrayList<FinCashInfo>();
            for (FinCashInfo e : finCashInfoList) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                	valCashInfoBatchMapper.deleteBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
            	valCashInfoBatchMapper.deleteBatch(listInsert);
            }

        }
    }
    
    
    /**
     * 子账套通道现金流计算规则
     * */
    public void insertFinCashCalcRuleBatch(List<FinCashCalcRule> finCashCalcRuleList){
    	Map<String, FinCashCalcRule> mapValCashCalcRule = new HashMap<String, FinCashCalcRule>();
        List<FinCashCalcRule> rstValCashCalcRule = new ArrayList<FinCashCalcRule>();
        for (FinCashCalcRule obj : finCashCalcRuleList) {
            if (mapValCashCalcRule.get(obj.getCashId() + obj.getEffDate()) == null) {
                rstValCashCalcRule.add(obj);
            }
            mapValCashCalcRule.put(obj.getCashId() + obj.getEffDate(), obj);
        }
        // 入库
        if (CollectionUtils.isNotEmpty(rstValCashCalcRule)) {
        	/**add by wujie on 20190321 start**/
        	this.deleteBatchFinCashCalcRuleBatch(rstValCashCalcRule);
        	/**add by wujie on 20190321 end**/
        	
            // 定义一个固定大小插入存放集合
            List<FinCashCalcRule> listInsert = new ArrayList<FinCashCalcRule>();
            for (FinCashCalcRule e : rstValCashCalcRule) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                	/**modify by wujie on 20190321
                    valCashCalcRuleBatchMapper.deleteBatch(listInsert);**/
                    valCashCalcRuleBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
            	/**modify by wujie on 20190321
                valCashCalcRuleBatchMapper.deleteBatch(listInsert);**/
                valCashCalcRuleBatchMapper.insertBatch(listInsert);
            }

        }
    }
    /**
     * 子账套通道现金流计算规则删除
     * add by wujie on 20190321
     * */
    public void deleteBatchFinCashCalcRuleBatch(List<FinCashCalcRule> finCashCalcRuleList){
    	if (CollectionUtils.isNotEmpty(finCashCalcRuleList)) {
    		// 定义一个固定大小插入存放集合
            List<FinCashCalcRule> listInsert = new ArrayList<FinCashCalcRule>();
            for (FinCashCalcRule e : finCashCalcRuleList) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valCashCalcRuleBatchMapper.deleteBatchC(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valCashCalcRuleBatchMapper.deleteBatchC(listInsert);
            }
    	}
    }
    
    
    /**
     * 金融产品账户
     * */
    public void insertStlFinProductAcctBatch(List<StlFinProductAcct> stlFinProductAcctList){
	      // 入库
	      if (CollectionUtils.isNotEmpty(stlFinProductAcctList)) {
	    	  /**add by wujie on 20190321 start**/
	    	  this.deleteBatchStlFinProductAcctBatch(stlFinProductAcctList);
	    	  /**add by wujie on 20190321 end**/
	        //定义一个固定大小插入存放集合
	          List<StlFinProductAcct> listInsert =  new ArrayList<StlFinProductAcct>();
	          for(StlFinProductAcct e :stlFinProductAcctList){
	              //小于最大值
	              if(listInsert.size() < maxSqlNum){
	                  listInsert.add(e); 
	              }else if(listInsert.size() >= maxSqlNum){
	            	  /**modify by wujie on 20190321
	                  valFinProductAcctBatchMapper.deleteBatch(listInsert); **/
	                  valFinProductAcctBatchMapper.insertBatch(listInsert); 
	                  listInsert.clear();
	                  listInsert.add(e);
	              }
	          }
	          //如果循环结束，小于最大值
	          if(CollectionUtils.isNotEmpty(listInsert)){
	        	  /**modify by wujie on 20190321
	              valFinProductAcctBatchMapper.deleteBatch(listInsert); **/
	              valFinProductAcctBatchMapper.insertBatch(listInsert);
	          }
	      }
    }
    
    /**
     * 金融产品账户删除
     * add by wujie on 20190321
     * */
    public void deleteBatchStlFinProductAcctBatch(List<StlFinProductAcct> stlFinProductAcctList){
	      // 入库
	      if (CollectionUtils.isNotEmpty(stlFinProductAcctList)) {
	        //定义一个固定大小插入存放集合
	          List<StlFinProductAcct> listInsert =  new ArrayList<StlFinProductAcct>();
	          for(StlFinProductAcct e :stlFinProductAcctList){
	              //小于最大值
	              if(listInsert.size() < maxSqlNum){
	                  listInsert.add(e); 
	              }else if(listInsert.size() >= maxSqlNum){
	                  valFinProductAcctBatchMapper.deleteBatch(listInsert); 
	                  listInsert.clear();
	                  listInsert.add(e);
	              }
	          }
	          //如果循环结束，小于最大值
	          if(CollectionUtils.isNotEmpty(listInsert)){
	              valFinProductAcctBatchMapper.deleteBatch(listInsert); 
	          }
	      }
    }
    
    /**
     * 账套清算账户表
     * */
    public void insertBokBooksetCapacctInfoBatch(List<BokBooksetCapacctInfo> bokBooksetCapacctInfoList){
        // 入库
        if (CollectionUtils.isNotEmpty(bokBooksetCapacctInfoList)) {
        	 /**add by wujie on 20190321 start**/
        	this.deleteBatchBokBooksetCapacctInfoBatch(bokBooksetCapacctInfoList);
        	 /**add by wujie on 20190321 end**/
            //定义一个固定大小插入存放集合
            List<BokBooksetCapacctInfo> listInsert =  new ArrayList<BokBooksetCapacctInfo>();
            for(BokBooksetCapacctInfo e :bokBooksetCapacctInfoList){
                //小于最大值
                if(listInsert.size() < maxSqlNum){
                    listInsert.add(e); 
                }else if(listInsert.size() >= maxSqlNum){
                	/**modify by wujie on 20190321
                    valBooksetCapacctInfoBatchMapper.deleteBatch(listInsert); **/
                    valBooksetCapacctInfoBatchMapper.insertBatch(listInsert); 
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            //如果循环结束，小于最大值
            if(CollectionUtils.isNotEmpty(listInsert)){
            	/**modify by wujie on 20190321
                valBooksetCapacctInfoBatchMapper.deleteBatch(listInsert); **/
                valBooksetCapacctInfoBatchMapper.insertBatch(listInsert);
            }
        }
    }
    
    /**
     * 账套清算账户表删除
     * add by wujie on 20190321
     * */
    public void deleteBatchBokBooksetCapacctInfoBatch(List<BokBooksetCapacctInfo> bokBooksetCapacctInfoList){
        // 入库
        if (CollectionUtils.isNotEmpty(bokBooksetCapacctInfoList)) {
            //定义一个固定大小插入存放集合
            List<BokBooksetCapacctInfo> listInsert =  new ArrayList<BokBooksetCapacctInfo>();
            for(BokBooksetCapacctInfo e :bokBooksetCapacctInfoList){
                //小于最大值
                if(listInsert.size() < maxSqlNum){
                    listInsert.add(e); 
                }else if(listInsert.size() >= maxSqlNum){
                    valBooksetCapacctInfoBatchMapper.deleteBatch(listInsert); 
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            //如果循环结束，小于最大值
            if(CollectionUtils.isNotEmpty(listInsert)){
                valBooksetCapacctInfoBatchMapper.deleteBatch(listInsert); 
            }
        }
    }

    /**
     * 查询子账套信息
     */
	public List<BokBooksetSonDto> queryBokBooksetSonInfo(Map<String, Object> map) {
		return channelCommonMapper.queryBokBooksetSonInfo(map);
	}

    /**
     *  删除标的类金融产品表信息
     * */
	public void delValFinProductBatch(List<ValChannelProdDealDto> dtoList) {
		//删除标的类金融产品表信息
		channelCommonMapper.delValFinProductBatch(dtoList);
		//删除标的类金融产品扩展表信息
		channelCommonMapper.delValFinProductExtBatch(dtoList);
		//删除标的类金融产品类型表信息
		channelCommonMapper.delValFinProductTypeBatch(dtoList);
		/**add by wujie on 20190325 start**/
		//删除金融产品估值参数表
		channelCommonMapper.delFinProductValuationPara(dtoList);
		/**add by wujie on 20190325 end**/
	}
    /**
     * 	初始化通道子账套信息
     * */

	public void delBokBooksetReBatch(List<ValChannelProdDealDto> dtoList) {
		//删除母子账套关系表信息
		channelCommonMapper.delBokBooksetReBatch(dtoList);
		//删除账套日期表信息
		channelCommonMapper.delBokBooksetDateBatch(dtoList);
		//删除核算主体信息表信息
		channelCommonMapper.delBokAcctSubjectInfoBatch(dtoList);
		//删除核算分层信息表信息
		channelCommonMapper.delBokAcctLayeringInfoBatch(dtoList);
		/**add by wujie on 20190322 start**/
		//删除账套信息表
		for(ValChannelProdDealDto obj:dtoList){
			bokBooksetMapper.deleteByPrimaryKey(obj.getFinprodId());
		}
		/**add by wujie on 20190322 end**/
	}
	/**
	 * 账套配置List删除
	 * @param list
	 */
	public void delBokBooksetExtBatch(List<ValChannelProdDealDto> dtoList){
		if(CollectionUtils.isNotEmpty(dtoList)){
			channelCommonMapper.delBokBooksetExtBatch(dtoList);
		}
	}
	
	/**
     * 	初始化估值方案、税务方案
     * */

	public void delBokBooksetPlanBatch(List<ValChannelProdDealDto> dtoList) {
		//删除估值方案、税务方案
		channelCommonMapper.delBokBooksetPlanBatch(dtoList);
	}
	/**
     * 	初始化通道现金流信息
     * */
	public void delFinCashInfoBatch(List<ValChannelProdDealDto> dtoList) {
		//删除现金流信息表信息(费用、增值税)
		channelCommonMapper.delFinCashInfoBatch(dtoList);
		//删除现金流信息计算规则表信息（费用）
		channelCommonMapper.delfinCashCalcRuleBatch(dtoList);
	}
	/**
     * 	初始化资金账户信息
     * */
	public void delStlFinProductAcctBatch(List<ValChannelProdDealDto> dtoList) {
		//删除金融产品账户表
		channelCommonMapper.delStlFinProductAcctBatch(dtoList);
		//删除账套清算款账户表
		channelCommonMapper.delBokBooksetCapacctInfoBatch(dtoList);
	}
    /**
     * 查询子账套信息
     */
	public List<BokBooksetSonDto> queryBokBooksetReInfo(Map<String, Object> map) {
		return channelCommonMapper.queryBokBooksetReInfo(map);
	}
    /**
     * 更新子账套的账套日期信息
     */
	public void updateBokBooksetReInfo(Map<String, Object> map) {
		channelCommonMapper.updateBokBooksetReInfo(map);
		
	}
    /**
     * 	更新子账套核算分层信息
     */

	public void updateBokAcctSubjectInfo(Map<String, Object> map) {
		channelCommonMapper.updateBokAcctSubjectInfo(map);
		
	}
	/**
     * 获取默认方案
     */

	public BokBooksetPlan queryBokBooksetPlan() {
		return channelCommonMapper.queryBokBooksetPlan();
	}
	/**
     * 	税务方案信息-前台推送
     */
	public BokBooksetPlan queryFinProductValuationPara(String chlId) {
		return channelCommonMapper.queryFinProductValuationPara(chlId);
	}
	/**
     * 查询现金流信息
     */

	public List<FinCashInfo> queryFinCashInfoList(String finprodId, String finprodType,String cashType1) {
		
		return null;
	}

	/**
     * 	查询通道交易
     */
	public ValChannelProdDealDto queryChannelProdDeal(Map<String, Object> map) {
		return channelCommonMapper.queryChannelProdDeal(map);
	}
	/**
     * 查询接口平台日期更新的通道现金流计算规则信息
     */
	public List<FinCashInfo> queryFinCashCalcRuleList(Map<String, Object> map) {
		return channelCommonMapper.queryFinCashCalcRuleList(map);
	}
	/**
	 * 通道账套对应的母账套查询
	 */
	public List<BokAcctLayeringInfo> queryBokBooksetReList(Map<String, Object> map) {
		return channelCommonMapper.queryBokBooksetReList(map);
	}

	/**
	 * 查询母子账套关系
	 * @param map
	 * @return
	 */
	/**add by yanya on 20190104**/
	public List<BokBooksetSonDto> queryBokBooksetRelationship(Map<String, Object> map) {
		return channelCommonMapper.queryBokBooksetRelationship(map);
	}
}
