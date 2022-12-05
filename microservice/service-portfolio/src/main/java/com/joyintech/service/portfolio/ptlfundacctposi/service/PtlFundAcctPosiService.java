/*
 * 文件名：PtlFundAcctPosiService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2019年2月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.portfolio.ptlfundacctposi.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.service.portfolio.ptlactivityperiod.dto.PtlActivityPeriod001Dto;
import com.joyintech.service.portfolio.ptlfundacctposi.dao.PtlFundAcctPosiMapper;
import com.joyintech.service.portfolio.ptlfundacctposi.dto.PtlFundAcctPosiDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 
 * 组合资金账户头寸查询
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2019年2月20日
 * @see PtlFundAcctPosiService
 * @since 1.0
 */
@Service("backPtlFundAcctPosiService")
public class PtlFundAcctPosiService extends BaseService<PtlFundAcctPosiDto>{
	
	@Autowired
	private PtlFundAcctPosiMapper ptlFundAcctPosiMapper;
	
	/**
     * 分页查询
     * @param map 分页查询条件
     * @return page对象
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<PtlFundAcctPosiDto> selectForPage(int page,int length,Map<String,Object> pageParam)
	{
        // add by caiqun 20200819 东莞 工单YHZGCPB20200814003  start
        //pageParam.put("cdate", DateUtil.parse((String)pageParam.get("cdate")));
        pageParam.put("startCDate", DateUtil.parse((String)pageParam.get("startCDate")));
        pageParam.put("endCDate", DateUtil.parse((String)pageParam.get("endCDate")));
        // add by caiqun 20200819 东莞 工单YHZGCPB20200814003  end
    	//  bug fixed:TISHKBS-75  只统计资金账户、券商账户余额，经代码核实只查询账户编码为A01/A07账户
		pageParam.put("TGZH", DictCodeListExt1.资金账户类型_产品托管户.getValue());
	    pageParam.put("JYSZJZH", DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        if(MultilevelDataAuthorityUtil.isEnabled()) {
        	pageParam.put("isMultiLevel", "01");
        }
        Page<PtlFundAcctPosiDto> pageResult = super.queryForPage(page, length,() -> ptlFundAcctPosiMapper.select(pageParam));
        
        //多级法人下有数据隔离，汇总需特别处理,银行模式下直接在查询中汇总
        if(MultilevelDataAuthorityUtil.isEnabled()) {
        	//1. 获取总共的汇总行-主页面查询需根据币种汇总 
        	//1.1 首先获取所有可能的币种
        	List<String> ccys = ptlFundAcctPosiMapper.getAllCcy(pageParam);
        	//1.2 对所有币种，逐个进行分页查询，剔除无查询结果的币种
        	Iterator<String> iterator= ccys.iterator();
        	while(iterator.hasNext()) {
        		pageParam.put("ccy", iterator.next());
        		Page<PtlFundAcctPosiDto> ccyPage = super.queryForPage(1, 1,() -> ptlFundAcctPosiMapper.select(pageParam));
        		if(ccyPage.getRecords()==0) {
        			iterator.remove();
        		}
        	}
        	//2  更新查询结果的总records
        	int addRecords = ccys.size()>0?ccys.size():1;
        	pageResult.setRecords(pageResult.getRecords()+addRecords);
        	//3 重新计算总页数
        	int total = (int)pageResult.getRecords()/length;
        	if(pageResult.getRecords()%length>0) {
        		total++;
        	}
        	pageResult.setTotal(total);
        	//4 添加汇总行
        	//4.1 判断是否需要添加汇总行
        	if(pageResult.getRoot()==null||pageResult.getRoot().size()<length) {
        		//4.2 避免空指针错误
        		if(pageResult.getRoot()==null) {
        			pageResult.setRoot(new ArrayList<PtlFundAcctPosiDto>());
        		}
        		//4.3 计算需要添加汇总的 index 范围
        		int startIndex,endIndex;
        		//4.3.1 该页不为空页
        		if(pageResult.getRoot().size()>0) {
        			startIndex = 0;
        			endIndex = addRecords>(length-pageResult.getRoot().size())?length-pageResult.getRoot().size():addRecords;
        		}
        		//4.3.2 该页为空页
        		else {
        			startIndex = (int) ((page-1)*length+addRecords-pageResult.getRecords());
        			endIndex = total>page?startIndex+length:addRecords;
        		}
        		//4.4 添加汇总
        		PtlFundAcctPosiDto summaryDto;
        		//4.4.1 如果没有查询结果,则添加一个总和为0的汇总行
        		if(ccys.size()==0) {
        			summaryDto = new PtlFundAcctPosiDto();
        			summaryDto.setIsSummary(DictCodeListExt1.是或否_是.getValue());
        			summaryDto.setAmount(BigDecimal.ZERO);
        			summaryDto.setAmount1(BigDecimal.ZERO);
        			pageResult.getRoot().add(summaryDto);
        		}
        		//4.4.2 如果有币种信息，则需要分币种汇总
        		else {
        			//4.4.2.1 首先对币种排序，防止因币种顺序错误导致多页时结果重复
        			ccys.sort(Comparator.naturalOrder());
        			//4.4.2.2 分币种进行汇总
        			int summaryLength = 1000;//单次查询1000
        			for(int i = startIndex;i<endIndex;i++) {
        				//设置查询参数
        				pageParam.put("ccy", ccys.get(i));
        				//初始化汇总对象
            			summaryDto = new PtlFundAcctPosiDto();
            			summaryDto.setIsSummary(DictCodeListExt1.是或否_是.getValue());
            			summaryDto.setCcy(ccys.get(i));
            			//初始化循环变量
            			BigDecimal amount = BigDecimal.ZERO, amount1 = BigDecimal.ZERO;
        				int totalPage = -1;
        				for(int thisPage = 1;totalPage<0||thisPage<=totalPage;thisPage++) {
        					//查询
        					Page<PtlFundAcctPosiDto> tempPage = super.queryForPage(thisPage, summaryLength,() -> ptlFundAcctPosiMapper.select(pageParam));
        					//更新页数信息
        					if(totalPage<0) {
        						totalPage = tempPage.getTotal();
        					}
        					//汇总
        					for(PtlFundAcctPosiDto tempDto:tempPage.getRoot()) {
        						amount =  ArithmeticUtil.doAdd(amount,tempDto.getAmount());
        						amount1 =  ArithmeticUtil.doAdd(amount1,tempDto.getAmount1());
        					}
        				}
        				//更新汇总信息
        				summaryDto.setAmount(amount);
        				summaryDto.setAmount1(amount1);
        				//插入汇总行
        				pageResult.getRoot().add(summaryDto);
        			}
        		}
        	}
        	
        }
        //银行模式下-查询结果为空，需要添加空的汇总行
        else{
        	if(pageResult.getRecords()==0) {
        		if(pageResult.getRoot()==null) {
        			pageResult.setRoot(new ArrayList<PtlFundAcctPosiDto>());
        		}
        		PtlFundAcctPosiDto summaryDto = new PtlFundAcctPosiDto();
    			summaryDto.setIsSummary(DictCodeListExt1.是或否_是.getValue());
    			summaryDto.setAmount(BigDecimal.ZERO);
    			summaryDto.setAmount1(BigDecimal.ZERO);
    			pageResult.getRoot().add(summaryDto);
    			pageResult.setRecords(1);
    			pageResult.setTotal(1);
        	}
        }
        return pageResult;
    }
	
    /**
     * 分页查询
     * @param map 分页查询条件
     * @return page对象
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<PtlFundAcctPosiDto> selectForPage1(int page,int length,Map<String,Object> pageParam)
	{
		pageParam.put("cdate", DateUtil.parse((String)pageParam.get("cdate")));
		//银行模式，无数据隔离，直接在sql中进行汇总
        if(MultilevelDataAuthorityUtil.isEnabled()) {
        	pageParam.put("isMultiLevel", "01");
        }
		Page<PtlFundAcctPosiDto> pages = super.queryForPage(page, length,() -> ptlFundAcctPosiMapper.select1(pageParam));
		//联社模式在外面进行汇总
        if(MultilevelDataAuthorityUtil.isEnabled()) {
        	//1. 查询是否需要添加汇总行
        	if(pages.getRoot()==null||pages.getRoot().size()<length) {
        		//2. 防止空指针
        		if(pages.getRoot()==null) {
        			pages.setRoot(new ArrayList<PtlFundAcctPosiDto>());
        		}
        		//3. 初始化汇总对象
        		PtlFundAcctPosiDto summaryDto = new PtlFundAcctPosiDto();
        		summaryDto.setIsSummary(DictCodeListExt1.是或否_是.getValue());
        		BigDecimal summary = BigDecimal.ZERO;
        		//4. 首先汇总本页
        		for(PtlFundAcctPosiDto tempDto:pages.getRoot()) {
        			summary = ArithmeticUtil.doAdd(summary,tempDto.getAmount());
        		}
        		//5. 循环汇总剩余结果
        		//5.1 设置循环参数
        		int summaryLength = 1000;//单次查询1000
        		long summarySize = pages.getRecords() - pages.getRoot().size();
        		//5.2 剩余结果逐页查询
        		int i;
        		for(i=1;i*summaryLength<summarySize;i++) {
        			Page<PtlFundAcctPosiDto> tempPage = super.queryForPage(i, summaryLength,() -> ptlFundAcctPosiMapper.select1(pageParam));
            		for(PtlFundAcctPosiDto dto:tempPage.getRoot()) {
            			summary = ArithmeticUtil.doAdd(summary,dto.getAmount());
            		}
        		}
        		//5.3 处理最后一页数据
        		int lastLength = (int)summarySize-(i-1)*summaryLength;
        		if(lastLength>0) {
        			Page<PtlFundAcctPosiDto> tempPage = super.queryForPage(i, summaryLength,() -> ptlFundAcctPosiMapper.select1(pageParam));
            		for(i=0;i<lastLength;i++) {
            			PtlFundAcctPosiDto dto = tempPage.getRoot().get(i);
            			summary = ArithmeticUtil.doAdd(summary,dto.getAmount());
            		}
        		}
        		//6 设置汇总数据
        		summaryDto.setAmount(summary);
        		pages.getRoot().add(summaryDto);
        	}
        	//8.总记录数需添加 汇总
        	pages.setRecords(pages.getRecords()+1);
        	//9.如果不含汇总行时为整页，则需要加页
        	if(pages.getRecords()%length==1) {
        		pages.setTotal(pages.getTotal()+1);
        	}
        }
        return pages;
    }
    
    /**
     * 分页查询
     * @param map 分页查询条件
     * @return page对象
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<PtlFundAcctPosiDto> selectForPage2(int page,int length,Map<String,Object> pageParam) {
    	pageParam.put("cdate", DateUtil.parse((String)pageParam.get("cdate")));
    	pageParam.put("startCdate", DateUtil.parse((String)pageParam.get("startCdate")));
		pageParam.put("endCdate", DateUtil.parse((String)pageParam.get("endCdate")));
    	Page<PtlFundAcctPosiDto> pages = super.queryForPage(page, length,() -> ptlFundAcctPosiMapper.select2(pageParam));
        return pages;
    }
    
    /**
     * 
     * 组合代码下拉框
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getPortfolioId(Map<String, Object> param,int page,int length){
        return super.queryForPageForSelectReturn(page,length,()->ptlFundAcctPosiMapper.getPortfolioId(param));
	}

	/**
	 * 获得托管机构信息
	 * <功能详细描述>
	 * @return
	 * @see
	 * @since 1.0
	 */
	public Page<SelectOptionDto> getOriginalInterestHolder(@PathVariable("page") int page,
														   @PathVariable("length") int length,
														   @RequestBody Map<String, Object> paramMap) {
		return queryForPageForSelectReturn(page, length,
				() -> ptlFundAcctPosiMapper.getOriginalInterestHolder(paramMap));
	}
}
