/*
 * 文件名：AssetLiabilityStatementQuery001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年9月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.financialstatement.assetliailitystatementnew.controller;

import com.google.common.collect.Maps;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.Product;
import com.joyintech.framework.base.entity.SysBatchGrpPrdlink;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.accounadjustmanage.assetbook.service.ValAssetBook001Service;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.service.BooksetCommonService;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatementnew.common.AssetLiabilityStatementNewUtil;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatementnew.dto.AssetLiabilityStatementNewQuery001Dto;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatementnew.dto.AssetLiabilityStatementNewQuery001SearchDto;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatementnew.service.AssetLiabilityStatementNewQuery001Service;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author huanghao
 *
 */
@Controller
@PageId("AssetLiabilityStatementNewQuery001")
@RequestMapping("/valuation/financialstatement/assetliailitystatementnew/AssetLiabilityStatementNewQuery001")
public class AssetLiabilityStatementNewQuery001Controller extends BaseController {

	@Autowired
	private AssetLiabilityStatementNewQuery001Service service;

	@Autowired
	private ValAssetBook001Service valAssetBook001Service;

	@Autowired
	private BooksetCommonService booksetCommonService;
	@Override
	public String init(Model model) {
		super.setSelectOptionToModel(model, DictCode.报表期限类型);
		this.setSelectOptionToModel(model, DictCode.报表维度);

		model.addAttribute("reportDimen", DictCodeListExt2.批处理账套类型_账套组.getValue());
		super.setSelectOptionToModel(model, DictCode.批处理账套类型); // 账套维度
		return super.init(model);
	}

	/**
	 * 查询条件检查
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping("check")
	public AjaxResult check(@RequestBody @Valid AssetLiabilityStatementNewQuery001SearchDto dto, BindingResult result) {
		// 当选择账套时
		if (DictCodeListExt2.批处理账套类型_账套.getValue().equals(dto.getReportDimen())) {
			// 账套代码不能为空
			if (StringUtil.isEmpty(dto.getBooksetId())) {
				return new AjaxResult("请输入账套代码。", MessageType.错误消息);
			}
			// 账套代码必须存在
			String[] booksetIds = new String[1];
			booksetIds[0] = dto.getBooksetId();
			List<Product> booksetList = booksetCommonService.findProductByIds(booksetIds);
			if (booksetList == null || booksetList.isEmpty()) {
				return new AjaxResult("账套代码不存在。", MessageType.错误消息);
			}
		}else{ // 当选择账套组时
			if (!StringUtil.isNotEmpty(dto.getBooksetGroup())){
				return new AjaxResult("请输入账套组。", MessageType.错误消息);
			}
		}
		if (result != null && !result.hasErrors()) {// 基础验证通过
			String reportTermType = dto.getReportTermType();// 报表期限类型
			String reportTerm = dto.getReportTerm();// 报表期限明细 : 半年，季度明细

			if ((DictCodeListExt1.报表期限类型_半年报.getValue().equals(reportTermType)
					|| DictCodeListExt1.报表期限类型_季报.getValue().equals(reportTermType))
					&& StringUtils.isEmpty(reportTerm)) {// 报表期限类型为半年报或季报时，报表期限不能为空
				return new AjaxResult("期限不能为空！", MessageType.错误消息);
			}
		}
		return commonCheck(result, "");
	}

	/**
	 * 检索账套代码(只取理财账套)
	 *
	 * @return 账套代码联想框内容
	 * @throws Exception
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("/getBooksetId2")
	public List<SuggestDto> getBooksetId2() throws Exception {
		String param = super.getRequest().getParameter("param");
		Map<String, Object> params = new HashMap<>();
		params.put("param",param);
		return valAssetBook001Service.getBooksetId2(params);
	}

	/**
	 * 查询账套组
	 * @return 套账日期
	 * @throws Exception
	 * @see
	 */
	@RequestMapping("findBooksetGroup")
	@ResponseBody
	public Page<SelectOptionDto> findBooksetGroup() throws Exception {
		return valAssetBook001Service.findBooksetGroup(RequestUtil.getPageParam());
	}

	/**
	 * 所属账套下拉框获取
	 *
	 * @return 返回的数据
	 * @exception Exception
	 *                异常
	 * @since 1.0
	 */
	@RequestMapping("/findBooksetId")
	@ResponseBody
	public Page<SelectOptionDto> findBooksetId(){
		return valAssetBook001Service.findBooksetIdByPage(RequestUtil.getPageParam());
	}

	/**
	 * 联动：报表期限类型 - 季度 半年
	 *
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getTermLink")
	public List<SelectOptionDto> getTradeMarket() throws Exception {
		Map<String, Object> param = RequestUtil.getPageParam();
		String reportTermType = (String) param.get("reportTermType");

		param.put("dict_code_parent", DictCode.报表期限类型.getValue());
		param.put("item_code_parent", param.get("reportTermType"));
		if (DictCodeListExt1.报表期限类型_半年报.getValue().equals(reportTermType)) {
			param.put("dict_code_child", DictCode.半年.getValue());
			return service.getLinkSelectOption(param);
		}
		if (DictCodeListExt1.报表期限类型_季报.getValue().equals(reportTermType)) {
			param.put("dict_code_child", DictCode.季度.getValue());
			return service.getLinkSelectOption(param);
		}
		return null;
	}

	/**
	 * 获得 表头信息
	 *
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping("getHeadInfo")
	public Map<String, String> getHeadInfo(@RequestBody AssetLiabilityStatementNewQuery001SearchDto dto)
			throws ParseException {
		Map<String, String> map = Maps.newHashMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date reportDate = service.analyseBalanceDate(dto, AssetLiabilityStatementNewUtil.ENDING_BALANCE);

		map.put("statementDate", sdf.format(reportDate));
		map.put("statementType", SysDataUtil.getNameFromDataDict(DictCode.报表期限类型, dto.getReportTermType()));
		map.put("org", CacheUtil.getSysParam(GlobalConstant.财务报表_编制单位));
		return map;
	}

	/**
	 * 获得 资产
	 *
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping("findAssets")
	public List<AssetLiabilityStatementNewQuery001Dto> findAssets(AssetLiabilityStatementNewQuery001SearchDto dto)
			throws ParseException {
		List<AssetLiabilityStatementNewQuery001Dto> dtoList = new ArrayList<AssetLiabilityStatementNewQuery001Dto>();
		Map<String, Object> paraMap = RequestUtil.getParameters(getRequest());
		// 报表维度为账套
		if (DictCodeListExt2.批处理账套类型_账套.getValue().equals(String.valueOf(paraMap.get("reportDimen")))) {
			dtoList = service.findAssetsStatement(dto);
		} else {
			// 报表维度为账套组
			List<SysBatchGrpPrdlink> list = valAssetBook001Service.findAllProduct(paraMap);
			dto.setBooksetIds(list.stream().map(SysBatchGrpPrdlink::getProductId).distinct().collect(Collectors.joining(",")));
			dto.setAccountList(valAssetBook001Service.getBooksetId2(paraMap));
			dtoList = service.findAssetsStatementAll(dto);
			/*List<AssetLiabilityStatementNewQuery001Dto> tmpList = service.handleDuplication(dtoList);
			dtoList = new ArrayList<>();
			dtoList = tmpList;
			dtoList.sort(Comparator.comparing(AssetLiabilityStatementNewQuery001Dto::getItemCode));*/
		}

		if (CollectionUtils.isNotEmpty(dtoList)) {
			/*dtoList.add(new AssetLiabilityStatementNewQuery001Dto());
			dtoList.add(new AssetLiabilityStatementNewQuery001Dto());
			dtoList.add(new AssetLiabilityStatementNewQuery001Dto());
			dtoList.add(new AssetLiabilityStatementNewQuery001Dto());*/
			dtoList.addAll(AssetLiabilityStatementNewUtil.getAssetListSum(dtoList));

			dtoList.forEach(item -> {
				if (StringUtils.isEmpty(item.getItemName())) {
					item.setInitialBalance(null);
					item.setEndingBalance(null);
				}
			});
		}

		// 全账套情况下,添加样式
		if (DictCodeListExt2.批处理账套类型_账套组.getValue().equals(String.valueOf(paraMap.get("reportDimen")))) {
			service.dealStyle(dtoList);
		}

		return dtoList;
	}

	/**
	 * 获得 负债和所有者权益
	 *
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping("findLiabilityInterest")
	public List<AssetLiabilityStatementNewQuery001Dto> findLiabilityInterest(
			AssetLiabilityStatementNewQuery001SearchDto dto) throws ParseException {
		List<AssetLiabilityStatementNewQuery001Dto> dtoList = new ArrayList<AssetLiabilityStatementNewQuery001Dto>();
		Map<String, Object> paraMap = RequestUtil.getParameters(getRequest());
		// 报表维度为账套
		if (DictCodeListExt2.批处理账套类型_账套.getValue().equals(String.valueOf(paraMap.get("reportDimen")))) {
			dtoList = service.findLiabilityInterestStatement(dto);
		} else {
			// 报表维度为账套组
			List<SysBatchGrpPrdlink> list = valAssetBook001Service.findAllProduct(paraMap);
			dto.setBooksetIds(list.stream().map(SysBatchGrpPrdlink::getProductId).distinct().collect(Collectors.joining(",")));
			dto.setAccountList(valAssetBook001Service.getBooksetId2(paraMap));
			dtoList = service.findLiabilityInterestStatementAll(dto);
			/*List<AssetLiabilityStatementNewQuery001Dto> tmpList = service.handleDuplication(dtoList);
			dtoList = new ArrayList<>();
			dtoList = tmpList;
			dtoList.sort(Comparator.comparing(AssetLiabilityStatementNewQuery001Dto::getItemCode));*/
		}
		if (CollectionUtils.isNotEmpty(dtoList)) {
			dtoList.add(new AssetLiabilityStatementNewQuery001Dto());
			dtoList.add(new AssetLiabilityStatementNewQuery001Dto());
			dtoList.add(new AssetLiabilityStatementNewQuery001Dto());
			dtoList.add(new AssetLiabilityStatementNewQuery001Dto());
			dtoList.add(new AssetLiabilityStatementNewQuery001Dto());
			dtoList.addAll(AssetLiabilityStatementNewUtil.getLiabilityInterestListSum(dtoList));
		}

		// 全账套情况下,添加样式
		if (DictCodeListExt2.批处理账套类型_账套组.getValue().equals(String.valueOf(paraMap.get("reportDimen")))) {
			service.dealStyle(dtoList);
		}
		return dtoList;
	}

	/**
	 * 导出信息excel
	 *
	 * @return excel
	 * @throws Exception
	 *             异常
	 * @see
	 * @since 1.0
	 */
	@RequestMapping(ACTION_EXPORT)
	public ResponseEntity<byte[]> export(AssetLiabilityStatementNewQuery001SearchDto dto) throws Exception {
		Map<String, Object> paraMap = RequestUtil.getPageParam();
		if (DictCodeListExt2.批处理账套类型_账套组.getValue().equals(paraMap.get("reportDimen"))) {
			List<SysBatchGrpPrdlink> list = valAssetBook001Service.findAllProduct(paraMap);
			dto.setBooksetIds(list.stream().map(SysBatchGrpPrdlink::getProductId).distinct().collect(Collectors.joining(",")));
			String profitType = (String) paraMap.get("profitType");
			dto.setProfitType(profitType);
		}
		return service.exportExcel(dto);

	}

	/**
	 * 获取默认日期信息
	 *
	 * @return AssetLiabilityStatementQuery001SearchDto
	 * @throws Exception
	 *             exception
	 */
	@ResponseBody
	@RequestMapping("getDateInfo")
	public AssetLiabilityStatementNewQuery001SearchDto getDateInfo() throws Exception {
		return service.getDateInfo(RequestUtil.getPageParam());
	}
}
