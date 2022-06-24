/*
 * 文件名：ProductTradeDealQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年8月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.producttradedeal.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.valuation.trademanage.producttradedeal.dao.ProductTradeDealQuery001Mapper;
import com.joyintech.service.valuation.trademanage.producttradedeal.dto.ProductTradeDealQuery001Dto;


/**
 * TA交易-汇总查询
 * @author zhangwei
 * @version 1.0 2018年8月7日
 * @see ProductTradeDealQuery001Service
 * @since 1.0
 */
@Service(value="serviceProductTradeDealQuery001Service")
public class ProductTradeDealQuery001Service extends CommonExportService<ProductTradeDealQuery001Dto>{

    @Autowired
    private ProductTradeDealQuery001Mapper productTradeDealQuery001Mapper;
    
    /**
     * 根据条件分页查询
     * @param params
     * @return
     * @throws Exception
     */
    public Page<ProductTradeDealQuery001Dto> findByPage(int pageNum, int length, Map<String, Object> params) {
    	Page<ProductTradeDealQuery001Dto> page = super.queryForPage(pageNum, length, ()->productTradeDealQuery001Mapper.selectList(params));
    	List<ProductTradeDealQuery001Dto> list = page.getRoot();
    	
    	//组装 Dto
    	assembleDtoList(list);
    	
//    	String dimension = (String) params.get("dimension"); // 维度：parent 、 son
//    	if(CollectionUtils.isNotEmpty(list)) {
//    		for (ProductTradeDealQuery001Dto dto : list) {
//        		if( "parent".equals(dimension)) {// 如果查询维度为母产品，则隐藏折叠树；反之子产品或默认都可以展开树
//        			dto.setLeaf(true); // 伪叶子节点不可展开
//            	}
//    			if(DictCodeList.是否_是.getValue().equals(dto.getHasChildren())) {
//    				dto.setFinprodAbbr("母产品：" + dto.getFinprodAbbr());
//    			}
//    			if(DictCodeList.是否_否.getValue().equals(dto.getHasChildren()) && !"root".equals(dto.getpFinprodId())) {
//    				dto.setFinprodAbbr("子产品：" + dto.getFinprodAbbr());
//    			}
//    		}
//    	}
        return page;
    }
    
    /**
     * 根据条件查询所有
     * @param params
     * @return
     * @throws Exception
     */
    public List<ProductTradeDealQuery001Dto> findAll(Map<String, Object> params) {
//    	导出数据不对： 默认导出不会查父节点（WHERE .. AND DECODE(cp.p_finprod_id, NULL, 'root', cp.p_finprod_id) = root）
    	params.put("parentId", "root");// 手动添加：只查根节点
    	List<ProductTradeDealQuery001Dto> list = productTradeDealQuery001Mapper.selectList(params);
    	
    	//组装 Dto
    	assembleDtoList(list);

//    	if(CollectionUtils.isNotEmpty(list)) {
//    		for (ProductTradeDealQuery001Dto dto : list) {
//    			if(DictCodeList.是否_是.getValue().equals(dto.getHasChildren())) {
//    				dto.setFinprodAbbr("母产品：" + dto.getFinprodAbbr());
//    			}
//    			if(DictCodeList.是否_否.getValue().equals(dto.getHasChildren()) && !"root".equals(dto.getpFinprodId())) {
//    				dto.setFinprodAbbr("子产品：" + dto.getFinprodAbbr());
//    			}
//    		}
//    	}
        return list;
    }
    
    

    /**
     * 组装 DtoList(添加 上一日份额 和 当日剩余份额)
     */
    private void assembleDtoList(List<ProductTradeDealQuery001Dto> list) {
    	if(CollectionUtils.isNotEmpty(list)) {
    		BigDecimal shareLastDay;// 上一日份额
    		BigDecimal shareRemainderDay;// 当日剩余份额
//    		for (ProductTradeDealQuery001Dto dto : list) {// 平均用时：1.60 s
//				shareLastDay = productTradeDealQuery001Mapper.selectShareLastDay(dto.getFinprodId(), dto.getSettleDate());
//				shareRemainderDay = productTradeDealQuery001Mapper.selectShareRemainderDay(dto.getFinprodId(), dto.getSettleDate());
//				
//				dto.setShareLastDay(shareLastDay);
//				dto.setShareRemainderDay(shareRemainderDay);
//			}
    		list.parallelStream().forEach(item -> {// 平均用时：602 ms
				item.setShareLastDay(productTradeDealQuery001Mapper.selectShareLastDay(item.getFinprodId(), item.getSettleDate()));
				item.setShareRemainderDay(productTradeDealQuery001Mapper.selectShareRemainderDay(item.getFinprodId(), item.getSettleDate()));
				//ADD LEIPENG UAT-453 分红/付息金额值全错误,用详情中的付息金额相加。
				BigDecimal intAmt = productTradeDealQuery001Mapper.selectIntAmt(item.getFinprodId(), item.getSettleDate());
				//item.setDivIntAmt(item.getDivIntAmt().add(intAmt!=null?intAmt:BigDecimal.ZERO));
				item.setDivIntAmt(intAmt!=null?intAmt:BigDecimal.ZERO);
    		});
    	}
    }


	/**
     * 下拉框获取：资产
     * 
     * @param params
     * @return select节点对象
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getAssets(Map<String, Object> params) {
        params.put("finprodType", "F06");// 仅查询基金型资产信息
        List<ProductTradeDealQuery001Dto> resultList = productTradeDealQuery001Mapper.selectList(params);
        List<SelectOptionDto> selectOptionDtoList = new ArrayList<>();
        for (ProductTradeDealQuery001Dto single : resultList) { // 组装select节点对象
            SelectOptionDto selectOptionDto = new SelectOptionDto();
            selectOptionDto.setId(single.getFinprodId());
            selectOptionDto.setText(single.getFinprodAbbr() + "(" + single.getFinprodId() + ")");
            selectOptionDtoList.add(selectOptionDto);
        }
        return selectOptionDtoList;
    }
    
    /**
     * 检索账套代码列表
     * 
     * @param param 过滤参数
     * @return 账套代码列表
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getFinprod(Map<String,Object> params) {
        List<SuggestDto> list= productTradeDealQuery001Mapper.getFinprod(params);
        return queryForSuggest(()->{
            return productTradeDealQuery001Mapper.getFinprod(params);
        }, null, null);
    }
    
    
    /**
     * 获取TA交易查询中 产品下拉框数据（通道和理财产品）
     * 
     * @param param 过滤参数
     * @return 账套代码列表
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getFinprodWithTd(Map<String,Object> params) {
        return queryForSuggest(()->{
            return productTradeDealQuery001Mapper.getFinprodWithTd(params);
        }, null, null);
    }
}
