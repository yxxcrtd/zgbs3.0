package com.joyintech.tams.invest.product.publish.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.framework.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.product.publish.dto.ProductPublishCounting001Dto;

/**
 * 产品发行规划试算查询Service
 * @version 1.0 2018年12月18日
 * @see ProductPublishCounting001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class ProductPublishCounting001Service extends CommonExportService<ProductPublishCounting001Dto> {
	
	public Page<ProductPublishCounting001Dto> findPrdPage(Map<String, Object> map) {
		return restTemplate.postForPage(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/ProductPublishCounting/findPrdPage",
					map,ProductPublishCounting001Dto.class
		);
	}
	
	public Page<ProductPublishCounting001Dto> findAssetPage(Map<String, Object> map) {
		return restTemplate.postForPage(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/ProductPublishCounting/findAssetPage",
					map,ProductPublishCounting001Dto.class
		);
	}

	public Page<SelectOptionDto> getPrdIssueplanCode(Map<String, Object> map) {
		return restTemplate.postForPage(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/ProductPublishCounting/getPrdIssueplanCode",
					map,SelectOptionDto.class
		);
	}
	
	@Override
	protected void buildExportParam(ExportParam exportParam) {
		String elementId = (String) exportParam.getSelectParam().get("elementId");
		if("ProductPublishCounting001_prd_export".equals(elementId)){
			exportParam.config(
					"/com/joyintech/tams/invest/product/publish/conf/export_conf.xml")
					.dataSource(DataSourceType.MASERT);
		}
		if("ProductPublishCounting001_ast_export".equals(elementId)){
			exportParam.config(
					"/com/joyintech/tams/invest/product/publish/conf/export_conf1.xml")
					.dataSource(DataSourceType.MASERT);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<ProductPublishCounting001Dto> selectPageData(Map<String, Object> selectParam) {
		String elementId = (String) selectParam.get("elementId");
		List<Object> list = JSONObject.parseObject(selectParam.get("data").toString(), List.class);

		if(list.size() > 0){
			Page<ProductPublishCounting001Dto> page = new Page<>();
			List<ProductPublishCounting001Dto> dtoList = new ArrayList<>();
			for (Object obj : list) {
				ProductPublishCounting001Dto dto = JSONObject.parseObject(obj.toString(), ProductPublishCounting001Dto.class);
				dtoList.add(dto);
			}
			page.setRoot(dtoList);
			page.setRecords(dtoList.size());
			return page;
		}

		if("ProductPublishCounting001_prd_export".equals(elementId)) {
			//BSTOT-368 guyunfei 20200109
			Page<ProductPublishCounting001Dto> page = findPrdPage(selectParam);
			//数据处理
			translateData(page);
			return page;

		}else {
			//BSTOT-368 guyunfei 20200109
			Page<ProductPublishCounting001Dto> page = findAssetPage(selectParam);
			//数据处理
			translateData(page);
			return page;
		}
	}

	/**
	 * 把查出来的数据造成同前台页面tree的数据结构
	 * @param page
	 */
	private void translateData(Page<ProductPublishCounting001Dto> page){
		List<ProductPublishCounting001Dto> root = page.getRoot();
		//根节点List
		List<ProductPublishCounting001Dto> treeList = new ArrayList<>();
		root.forEach(e->{
			if(StringUtil.equals(e.getParentInfo(),"root")){
				treeList.add(findChildren(e,root));
			}
		});
		//导出Excel的List
		List<ProductPublishCounting001Dto> resultList = new ArrayList<>();
		treeList.forEach(e->{
			//根节点也需作为一条数据导出
			resultList.add(e);
			//根节点下的子节点数据
			List<ProductPublishCounting001Dto> listData = new ArrayList<>();
			addChilden(e,listData);
			//根节点下子节点数据的顺序处理
			Collections.reverse(listData);
			resultList.addAll(listData);
		});

		page.setRoot(resultList);
	}

	/**
	 * 递归生成tree
	 * @param tree
	 * @param list
	 * @return
	 */
	private ProductPublishCounting001Dto findChildren(ProductPublishCounting001Dto tree, List<ProductPublishCounting001Dto> list) {
		for (ProductPublishCounting001Dto node : list) {
			if (StringUtil.equals(tree.getChildInfo(),node.getParentInfo())) {
				if (tree.getChildren() == null) {
					tree.setChildren(new ArrayList<>());
				}
				tree.getChildren().add(findChildren(node, list));
			}
		}
		return tree;
	}

	/**
	 * 将tree的每个根节点下的数据放到一个List中
	 * @param dto
	 * @param list
	 */
	private void addChilden(ProductPublishCounting001Dto dto, List<ProductPublishCounting001Dto> list){
		if(CollectionUtils.isNotEmpty(dto.getChildren())){
			List<ProductPublishCounting001Dto> children = dto.getChildren();
			children.forEach(e->{
				if(CollectionUtils.isNotEmpty(dto.getChildren())){
					addChilden(e,list);
				}
				list.add(e);
			});
		}
	}

}
