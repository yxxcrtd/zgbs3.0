/*
 * 文件名：ValBatchLog001Service.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：ww1
 * 修改时间：2018年2月23日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */
 
package com.joyintech.tams.valuation.system.log.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.joyintech.tams.valuation.system.log.dto.ValBatchLogDetailExDto;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DynamicDataSourceHolder;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.system.log.dao.ValBatchLog001Mapper;
import com.joyintech.tams.valuation.system.log.dao.ValBatchLog002Mapper;
import com.joyintech.tams.valuation.system.log.dto.ValBatchLog001Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 批处理日志service	
 * 
 * @author ww1
 * @version 1.0 2018年2月23日
 * @see ValBatchLog001Service
 * @since 1.0
 */
@Service
public class ValBatchLog001Service extends CommonExportService<ValBatchLog001Dto> {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    @Autowired
    private ValBatchLog001Mapper valBatchLog001Mapper;
    
    @Autowired
    private ValBatchLog002Mapper valBatchLog002Mapper;
    
    
    /**
     * table page
     * 
     * @param pageParam 参数
     * @return 分页数据
     * @throws Exception 异常
     */
    
    public Page<ValBatchLog001Dto> selectForPage(Map<String, Object> pageParam) {
    	
    	//是否警告
    	String isWarn=  ((String)pageParam.get("isWarn")).equals("true")?DictCodeList.是否_否.getValue():DictCodeList.是否_是.getValue();
    	if(DictCodeList.是否_否.getValue().equals(isWarn)){
    		
    		//查询警告数据时，需要查询 所有数据
    		DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
    		List<ValBatchLog001Dto> list = valBatchLog001Mapper.select(pageParam);
    		DynamicDataSourceHolder.clearDataSource();
    		
    		//处理警告信息数据
    		Page<ValBatchLog001Dto> queryForPage =new Page<ValBatchLog001Dto>();
    		List<ValBatchLog001Dto> formatResult = formatResult(list,isWarn);
    		queryForPage.setRoot(formatResult);
    		queryForPage.setRecords(formatResult.size());
    		return  queryForPage;
    	}else{
    		DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
    		Page<ValBatchLog001Dto>  queryForPageList = super.queryForPage(ValBatchLog001Mapper.class, "select", pageParam);
    		DynamicDataSourceHolder.clearDataSource();
    		queryForPageList.setRoot(formatResult(queryForPageList.getRoot(),isWarn));
    		return queryForPageList;
    	}
    }
    
    /**
     *处理警告数据
     */
    private List<ValBatchLog001Dto> formatResult(List<ValBatchLog001Dto> list, String isWarn) {
    	List<ValBatchLog001Dto> newList =new ArrayList<ValBatchLog001Dto>();
        for (ValBatchLog001Dto dto : list) {
            int i =valBatchLog002Mapper.selectWarn(dto.getBatchExedetailSeqNo(),dto.getTaskId(),isWarn.equals(DictCodeList.是否_否.getValue())?DictCodeList.是否_否.getValue():"");
        	if(i>0){
        		dto.setIsWarn("警告");
        		newList.add(dto);
        	}
        }
        //如果是否 警告选中 只会展示有警告数据
        if(DictCodeList.是否_否.getValue().equals(isWarn)){
        	return newList;
        }else{
        	return list;
        }
	}
    
	/**
     * 导出报表一览
     * 
     * @param pageParam 参数
     * @return list
     */
    public List<ValBatchLog001Dto> selectForExport(Map<String, Object> pageParam) {
    	DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
    	//处理警告信息   
    	String isWarn=  ((String)pageParam.get("isWarn")).equals("true")?DictCodeList.是否_否.getValue():DictCodeList.是否_是.getValue();
     	List<ValBatchLog001Dto> list = valBatchLog001Mapper.select(pageParam);
    	 DynamicDataSourceHolder.clearDataSource();
    	 return formatResult(list,isWarn);
    }

    /**
     * 取得任务组信息
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getTaskGroup(String param) {
    	DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
    	List<SuggestDto> list=valBatchLog001Mapper.getTaskGroup(param);
    	DynamicDataSourceHolder.clearDataSource();	
        return list;
    }
    
    /**
     * 取得任务信息
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getTask(String param) {
    	DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
    	List<SuggestDto> list=valBatchLog001Mapper.getTask(param);
    	DynamicDataSourceHolder.clearDataSource();	
        return list;
    }
    
    /**
     * 取得产品组信息
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getPrdGroup(String param) {
     	DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
     	
    	//多极法人改造  start  add by hxy 20200618
     	List<String> institutionlist=null;
  	    if(MultilevelDataAuthorityUtil.isEnabled()) {
       	 //联社模式
       	 //根据选择的账套组或者账套，查询出它们的所属机构
  	    	institutionlist=LoginUserUtil.getTreeInstCode();
  	    }
    	List<SuggestDto> list=valBatchLog001Mapper.getPrdGroup(param,institutionlist);
    	DynamicDataSourceHolder.clearDataSource();	
        return list;
        
    }

    public ValBatchLog001Dto findLogDetail(String logSeqNo) {
    	DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
    	ValBatchLog001Dto dto= valBatchLog001Mapper.findLogDetail(logSeqNo);
    	DynamicDataSourceHolder.clearDataSource();	
        return dto;
    }
    
    
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
    	if(StringUtil.equals(propName, "processTime")){
    		cell.setCellValue((Long)value / 1000);
    	}
    }

	public Page<ValBatchLogDetailExDto> findDetai( String seqNo) {
		List<ValBatchLogDetailExDto> newList = new ArrayList<ValBatchLogDetailExDto>();
		Page<ValBatchLogDetailExDto> pageList=new Page<ValBatchLogDetailExDto>();
		ValBatchLog001Dto dto = valBatchLog001Mapper.findLogDetail(seqNo);
		if (dto != null) {
			String detailEx = dto.getDetailEx();
			//String detailEx = "[{'业务代码':'ABDGDG','业务类型':'债券','业务明细代码':'21212121','交易代码':'123','账务场景代码':'1401'}]";
			JSONArray array = JSONArray.parseArray(detailEx);
			for (Object mapList : array) {
				for (Object entry : ((Map) mapList).entrySet()) {
					ValBatchLogDetailExDto detail = new ValBatchLogDetailExDto();
					detail.setRemark((String) ((Map.Entry) entry).getKey());
					detail.setValue((String) ((Map.Entry) entry).getValue());
					newList.add(detail);
				}
			}
		}
		pageList.setCode(0);
		pageList.setRoot(newList);
		pageList.setRecords(newList.size());
		return pageList;
	}



}
