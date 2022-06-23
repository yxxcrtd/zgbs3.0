/*
 * 文件名：AssetLiabilityStatementQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年9月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.financialstatement.assetliailitystatementnew.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.grid.Page;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.accounadjustmanage.assetbook.service.ValAssetBook001Service;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.valuation.accounadjustmanage.valuationtablegeneration.dto.ExportFileDto;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatementnew.common.AssetLiabilityStatementNewUtil;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatementnew.dto.AssetLiabilityStatementNewQuery001Dto;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatementnew.dto.AssetLiabilityStatementNewQuery001SearchDto;
import com.joyintech.tools.util.ExcelTools;

/**
 * 资产负债表
 *
 * @author huanghao
 * @version 1.0 2020年5月31日
 * @see AssetLiabilityStatementNewQuery001Service
 * @since 1.0
 */
@Service
public class AssetLiabilityStatementNewQuery001Service
		extends CommonExportService<AssetLiabilityStatementNewQuery001Dto> {

	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(AssetLiabilityStatementNewQuery001Service.class);

	/**
	 * 针对weblogic部署无法获取模版问题
	 */
	private static final String TMP_DIR = "/tpl/";

	@Autowired
	private ValAssetBook001Service valAssetBook001Service;

	/**
	 * 通用联动查询
	 *
	 * @param param
	 *            : dict_code_parent,dict_code_child,item_code_parent
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	//@DataSource(DataSourceType.OTHER)
	public List<SelectOptionDto> getLinkSelectOption(Map<String, Object> param) {
		List<SelectOptionDto> list = restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
						+ "/AssetLiabilityStatementNewQuery001/getLinkSelectOption"
				, param,
				SelectOptionDto.class);
		return list;
	}

	/**
	 * 查询科目余额表
	 *
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<AssetLiabilityStatementNewQuery001Dto> findBokBalance(AssetLiabilityStatementNewQuery001SearchDto dto)
			throws ParseException {
		List<AssetLiabilityStatementNewQuery001Dto> list = restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
						+ "/AssetLiabilityStatementNewQuery001/selectBokBalance"
				, dto,
				AssetLiabilityStatementNewQuery001Dto.class);
		return list;
	}

	/**
	 * 查询科目余额表
	 *
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<AssetLiabilityStatementNewQuery001Dto> findBokBalanceAll(
			AssetLiabilityStatementNewQuery001SearchDto dto) throws ParseException {
		List<AssetLiabilityStatementNewQuery001Dto> list = restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
						+ "/AssetLiabilityStatementNewQuery001/selectBokBalanceAll"
				, dto,
				AssetLiabilityStatementNewQuery001Dto.class);
		return list;
	}

	/**
	 * 获得 资产报表数据（单账套）
	 *
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<AssetLiabilityStatementNewQuery001Dto> findAssetsStatement(
			AssetLiabilityStatementNewQuery001SearchDto dto) throws ParseException {

		dto.setItemTypes(Arrays.asList(DictCodeList.科目类型_资产类.getValue()));// 科目类型
		// 资产类
		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementNewUtil.ENDING_BALANCE));// 设置期末日期
		List<AssetLiabilityStatementNewQuery001Dto> endList = findBokBalance(dto); // 期末余额列表

		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementNewUtil.INITIAL_BALANCE));// 设置年初日期
		List<AssetLiabilityStatementNewQuery001Dto> initList = findBokBalance(dto); // 年初余额列表

		List<AssetLiabilityStatementNewQuery001Dto> dtoList = doMakeDtoDate(endList, initList);
		return dtoList;
	}

	/**
	 * 负债类和所有者权益类 合并期末余额与年初余额
	 *
	 * @param endList
	 *            期末余额
	 * @param initList
	 *            年初余额
	 * @return
	 * @see
	 * @since 1.0
	 */
	public List<AssetLiabilityStatementNewQuery001Dto> doMakeDtoDate(
			List<AssetLiabilityStatementNewQuery001Dto> endList, List<AssetLiabilityStatementNewQuery001Dto> initList) {

		List<AssetLiabilityStatementNewQuery001Dto> list = new ArrayList<AssetLiabilityStatementNewQuery001Dto>();

		if (null == endList || endList.size() < 1) {// 如果期末余额为空,则直接返回
			return list;
		} else {
			if (null != initList && initList.size() > 0) {// 如果年初余额为空,则直接返回为空
				for (AssetLiabilityStatementNewQuery001Dto end : endList) {
					for (AssetLiabilityStatementNewQuery001Dto init : initList) {
						init.setSubjectName(init.getItemName());
						if (null != init.getItemName() && end.getSubjectName().equals(init.getItemName()) && end.getItemCode().equals(init.getItemCode())) {
							end.setInitialBalance(init.getEndingBalance());
						}
					}
				}
			}
			list.addAll(endList);
			return list;
		}
	}

	/**
	 * 获得 资产报表数据（全账套）
	 *
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<AssetLiabilityStatementNewQuery001Dto> findAssetsStatementAll(
			AssetLiabilityStatementNewQuery001SearchDto dto) throws ParseException {
		dto.setItemTypes(Arrays.asList(DictCodeList.科目类型_资产类.getValue()));// 科目类型
		// 资产类

		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementNewUtil.ENDING_BALANCE));// 设置期末日期
		List<AssetLiabilityStatementNewQuery001Dto> endList = findBokBalanceAll(dto); // 期末余额列表
		List<AssetLiabilityStatementNewQuery001Dto> tmpendList = this.handleDuplication(endList);
		endList = new ArrayList<>();
		endList = tmpendList;
		endList.sort(Comparator.comparing(AssetLiabilityStatementNewQuery001Dto::getItemCode));

		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementNewUtil.INITIAL_BALANCE));// 设置年初日期
		List<AssetLiabilityStatementNewQuery001Dto> initList = findBokBalanceAll(dto); // 年初余额列表
		List<AssetLiabilityStatementNewQuery001Dto> tmpInitList = this.handleDuplication(initList);
		initList = new ArrayList<>();
		initList = tmpInitList;
		initList.sort(Comparator.comparing(AssetLiabilityStatementNewQuery001Dto::getItemCode));

		List<AssetLiabilityStatementNewQuery001Dto> dtoList = doMakeDtoDate(endList, initList);
		return dtoList;
	}

	/**
	 * 处理样式
	 *
	 * @param list
	 * @param style
	 * @param reportDimen
	 *            报表维度
	 * @see
	 * @since 1.0
	 */
	public void dealStyle(List<AssetLiabilityStatementNewQuery001Dto> list) {
		for (AssetLiabilityStatementNewQuery001Dto dto : list) {
			if (StringUtils.isNotEmpty(dto.getItemName())) {
				dto.setItemName("<label style='color:blue;'>" + dto.getItemName() + "</label>");
			}
		}
	}

	/**
	 * 获得 负债和所有者权益报表数据(单账套)
	 *
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<AssetLiabilityStatementNewQuery001Dto> findLiabilityInterestStatement(
			AssetLiabilityStatementNewQuery001SearchDto dto) throws ParseException {

		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementNewUtil.ENDING_BALANCE));// 设置期末日期
		List<AssetLiabilityStatementNewQuery001Dto> endListALL = makeLiabilityLnterestList(dto, false);// 期末负债和所有者权益报表数据

		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementNewUtil.INITIAL_BALANCE));// 设置年初日期
		List<AssetLiabilityStatementNewQuery001Dto> initListALL = makeLiabilityLnterestList(dto, false);// 年初负债和所有者权益报表数据

		List<AssetLiabilityStatementNewQuery001Dto> dtoList = doMakeDtoDate(endListALL, initListALL);

		return dtoList;
	}

	/**
	 * <组装负债以及所有者收益数据> <查询不同日期的负债所有者收益数据，按照时间,账套类型进行区别>
	 * @param dto 入参查询条件
	 * @param isItems isItems为true :单账套 isItems为false :全账套
	 * @return
	 * @throws ParseException
	 * @see
	 * @since 1.0
	 */
	public List<AssetLiabilityStatementNewQuery001Dto> makeLiabilityLnterestList(
			AssetLiabilityStatementNewQuery001SearchDto dto, boolean isItems) throws ParseException {

		// 期末负债和所有者权益报表数据汇总
		List<AssetLiabilityStatementNewQuery001Dto> ListALL = new ArrayList<AssetLiabilityStatementNewQuery001Dto>();
		// 设置科目类型为负债类
		List<String> itemTypes = new ArrayList<>();
		itemTypes.add(DictCodeList.科目类型_负债类.getValue());
		itemTypes.add(DictCodeList.科目类型_所有者权益类.getValue());
		dto.setItemTypes(itemTypes);
		List<AssetLiabilityStatementNewQuery001Dto> listbalance = new ArrayList<AssetLiabilityStatementNewQuery001Dto>();
		if (isItems) {
			listbalance = findBokBalanceAll(dto); // 期末负债列表(账套组)
		} else {
			listbalance = findBokBalance(dto); // 期末负债列表(账套)
		}
		//资产负债Map
		Map<String, List<AssetLiabilityStatementNewQuery001Dto>> assetLiabilityMap = listbalance.stream().collect(Collectors.groupingBy(AssetLiabilityStatementNewQuery001Dto::getBooksetId));
		if (StringUtil.isNotEmpty(assetLiabilityMap) && !assetLiabilityMap.isEmpty()) {
			for (Map.Entry<String, List<AssetLiabilityStatementNewQuery001Dto>> entry : assetLiabilityMap.entrySet()) {
				// 定义 变量.负债类合计科目余额
				BigDecimal liabilitySumAmt = BigDecimal.ZERO;
				// 定义 变量.所有者权益类合计科目余额
				BigDecimal interestSumAmt = BigDecimal.ZERO;
				// 定义 变量.负债类列表
				List<AssetLiabilityStatementNewQuery001Dto> List_liability = new ArrayList<AssetLiabilityStatementNewQuery001Dto>();
				// 定义 变量.所有者权益类列表
				List<AssetLiabilityStatementNewQuery001Dto> List_interest = new ArrayList<AssetLiabilityStatementNewQuery001Dto>();
				//String booksetId = entry.getKey();
				List<AssetLiabilityStatementNewQuery001Dto> liabilityStatementList = entry.getValue();
				if (!StringUtil.isEmptyList(liabilityStatementList)) {
					for (AssetLiabilityStatementNewQuery001Dto item : liabilityStatementList) {
						// 赋值期末余额
						item.setEndingBalance(item.getbAmt());
						// 负债类资产
						if (StringUtil.equals(DictCodeList.科目类型_负债类.getValue(), item.getItemType())) {
							liabilitySumAmt = ArithmeticUtil.doAdd(liabilitySumAmt, item.getbAmt());
							List_liability.add(item);
						}
						// 所有者权益类资产
						if (StringUtil.equals(DictCodeList.科目类型_所有者权益类.getValue(),item.getItemType())) {
							interestSumAmt = ArithmeticUtil.doAdd(interestSumAmt, item.getbAmt());
							List_interest.add(item);
						}
					}
				}
				ListALL.addAll(List_liability);
				//负债总
				AssetLiabilityStatementNewQuery001Dto dto1 = new AssetLiabilityStatementNewQuery001Dto();
				dto1.setItemName("负债合计");
				dto1.setSubjectName("负债合计");
				dto1.setItemCode("FZ_15");
				dto1.setEndingBalance(liabilitySumAmt);
				dto1.setBooksetId(liabilityStatementList.get(0).getBooksetId());
				ListALL.add(dto1);
				ListALL.addAll(List_interest);
				//所有者权益总
				AssetLiabilityStatementNewQuery001Dto dto2 = new AssetLiabilityStatementNewQuery001Dto();
				dto2.setItemName("净资产合计");
				dto2.setSubjectName("净资产合计");
				dto2.setItemCode("SYZQY_04");
				dto2.setEndingBalance(interestSumAmt);
				dto2.setBooksetId(liabilityStatementList.get(0).getBooksetId());
				ListALL.add(dto2);
			}
		}

		return ListALL;
	}

	/**
	 * 获得 负债和所有者权益报表数据（全账套）
	 *
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<AssetLiabilityStatementNewQuery001Dto> findLiabilityInterestStatementAll(
			AssetLiabilityStatementNewQuery001SearchDto dto) throws ParseException {

		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementNewUtil.ENDING_BALANCE));// 设置期末日期
		List<AssetLiabilityStatementNewQuery001Dto> endListALL = makeLiabilityLnterestList(dto, true);// 期末负债和所有者权益报表数据
		List<AssetLiabilityStatementNewQuery001Dto> tmpEndListALL = this.handleDuplication(endListALL);
		endListALL = new ArrayList<>();
		endListALL = tmpEndListALL;
		endListALL.sort(Comparator.comparing(AssetLiabilityStatementNewQuery001Dto::getItemCode));

		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementNewUtil.INITIAL_BALANCE));// 设置年初日期
		List<AssetLiabilityStatementNewQuery001Dto> initListALL = makeLiabilityLnterestList(dto, true);// 年初负债和所有者权益报表数据
		List<AssetLiabilityStatementNewQuery001Dto> tmpInitListALL = this.handleDuplication(initListALL);
		initListALL = new ArrayList<>();
		initListALL = tmpInitListALL;
		initListALL.sort(Comparator.comparing(AssetLiabilityStatementNewQuery001Dto::getItemCode));


		List<AssetLiabilityStatementNewQuery001Dto> dtoList = doMakeDtoDate(endListALL, initListALL);

		return dtoList;
	}

	/**
	 * 分析余额日期
	 *
	 * @param dto
	 *            查询条件
	 * @param balanceType
	 *            期末、年初
	 * @return
	 * @throws ParseException
	 */
	public Date analyseBalanceDate(AssetLiabilityStatementNewQuery001SearchDto dto, String balanceType)
			throws ParseException {
		int year = 0;
		int month = -1;
		int day = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);// 时
		calendar.set(Calendar.MINUTE, 0);// 分
		calendar.set(Calendar.SECOND, 0);// 秒
		calendar.set(Calendar.MILLISECOND, 0);// 毫秒

		String[] strArr = dto.getReportDate().split(",")[0].split("-");
		if (DictCodeListExt1.报表期限类型_年报.getValue().equals(dto.getReportTermType())) {// 年底
			year = Integer.valueOf(strArr[0]);
			month += 12;
			day = 31;
		}
		if (DictCodeListExt1.报表期限类型_半年报.getValue().equals(dto.getReportTermType())) {
			year = Integer.valueOf(strArr[0]);
			if (DictCodeListExt1.半年_上半年.getValue().equals(dto.getReportTerm())) {
				month += 6;
				day = 30;
			} else {
				month += 12;
				day = 31;
			}
		}
		if (DictCodeListExt1.报表期限类型_季报.getValue().equals(dto.getReportTermType())) {
			year = Integer.valueOf(strArr[0]);
			if (DictCodeListExt1.季度_第一季度.getValue().equals(dto.getReportTerm())) {
				month += 3;
				day = 31;
			} else if (DictCodeListExt1.季度_第二季度.getValue().equals(dto.getReportTerm())) {
				month += 6;
				day = 30;
			} else if (DictCodeListExt1.季度_第三季度.getValue().equals(dto.getReportTerm())) {
				month += 9;
				day = 30;
			} else {
				month += 12;
				day = 31;
			}
		}
		if (DictCodeListExt1.报表期限类型_月报.getValue().equals(dto.getReportTermType())) {// 月底：当前月的下个月的第0天
			year = Integer.valueOf(strArr[0]);
			month += Integer.valueOf(strArr[1]) + 1;
			day = 0;
		}
		if (DictCodeListExt1.报表期限类型_日报.getValue().equals(dto.getReportTermType())) {
			year = Integer.valueOf(strArr[0]);
			month += Integer.valueOf(strArr[1]);
			day = Integer.valueOf(strArr[2]);
		}

		if (AssetLiabilityStatementNewUtil.ENDING_BALANCE.equals(balanceType)) {// 期末
			calendar.set(year, month, day);
			return calendar.getTime();
		} else {// 年初
			calendar.set(year, 0, 1);
			// return calendar.getTime();
			// 取去年年末余额
			return DateUtil.addDay(calendar.getTime(), -1);
		}
	}

	/**
	 * 导出 资产负债表 excel
	 *
	 * @param dto
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public ResponseEntity<byte[]> exportExcel(AssetLiabilityStatementNewQuery001SearchDto dto)
			throws ParseException, IOException {
		ExportFileDto exportFileDto = createExcel(dto);
		File file = new File(exportFileDto.getFilePath());
		return ExportUtil.export(exportFileDto.getFileName(), file, true);
	}

	/**
	 * 资产负债表 导出逻辑
	 *
	 * @param dto
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public ExportFileDto createExcel(AssetLiabilityStatementNewQuery001SearchDto dto)
			throws ParseException, IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date reportDate = analyseBalanceDate(dto, AssetLiabilityStatementNewUtil.ENDING_BALANCE);
		List<AssetLiabilityStatementNewQuery001Dto> assetList = new ArrayList<AssetLiabilityStatementNewQuery001Dto>();
		String prefix = "";
		if (DictCodeListExt2.批处理账套类型_账套.getValue().equals(dto.getReportDimen())) {
			assetList = findAssetsStatement(dto);
			prefix = dto.getBooksetId();
		} else {
			assetList = findAssetsStatementAll(dto);
			String profitType = dto.getProfitType();
			Map<String, Object> pageParam = new HashMap<>();
			pageParam.put("booksetGroup",dto.getBooksetGroup());
			Page<SelectOptionDto> booksetGroup = valAssetBook001Service.findBooksetGroup(pageParam);
			SelectOptionDto optionDto = new SelectOptionDto();
			if (StringUtil.isNotEmpty(booksetGroup) && !StringUtil.isEmptyList(booksetGroup.getRoot())){
				optionDto = booksetGroup.getRoot().get(0);
			}
			if(StringUtil.isNotEmpty(profitType)){
				profitType =  profitType.replaceAll("01", "净值型");
				profitType =  profitType.replaceAll("02", "预期收益型");
				profitType =  profitType.replaceAll("03", "货币型");
				profitType =  profitType.replaceAll(",", "、");
				prefix = "("+profitType+")"+optionDto.getText();
			}else{
				prefix = optionDto.getText();
			}
		}
		//System.out.println("prefix----1>"+prefix);
		/*List<AssetLiabilityStatementNewQuery001Dto> tmpAssetList = this.handleDuplication(assetList);
		assetList = new ArrayList<>();
		assetList = tmpAssetList;
		assetList.sort(Comparator.comparing(AssetLiabilityStatementNewQuery001Dto::getItemCode));*/
		if (CollectionUtils.isNotEmpty(assetList)) {// 设置总计
			assetList.addAll(AssetLiabilityStatementNewUtil.getAssetListSum(assetList));
		}
		List<AssetLiabilityStatementNewQuery001Dto> liabilityInterestList = new ArrayList<AssetLiabilityStatementNewQuery001Dto>();
		if (DictCodeListExt2.批处理账套类型_账套.getValue().equals(dto.getReportDimen())) {
			liabilityInterestList = findLiabilityInterestStatement(dto);
		} else {
			liabilityInterestList = findLiabilityInterestStatementAll(dto);
		}
		/*List<AssetLiabilityStatementNewQuery001Dto> tmpliabilityInterestList = this.handleDuplication(liabilityInterestList);
		liabilityInterestList = new ArrayList<>();
		liabilityInterestList = tmpliabilityInterestList;
		liabilityInterestList.sort(Comparator.comparing(AssetLiabilityStatementNewQuery001Dto::getItemCode));*/
		if (CollectionUtils.isNotEmpty(liabilityInterestList)) {// 设置总计
			liabilityInterestList
					.addAll(AssetLiabilityStatementNewUtil.getLiabilityInterestListSum(liabilityInterestList));
		}
		// 合并List
		List<AssetLiabilityStatementNewQuery001Dto> mergeList = mergeList(assetList, liabilityInterestList);

		for (AssetLiabilityStatementNewQuery001Dto temp:mergeList
		) {
			temp.setItemName(temp.getItemName().replaceAll("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;","      "));
		}
		// 解决报表数据为空导出模板公式的问题
		if (mergeList.isEmpty()) {
			AssetLiabilityStatementNewQuery001Dto emptyDto = new AssetLiabilityStatementNewQuery001Dto();
			emptyDto.setInitialBalance(null);
			emptyDto.setInitialBalance1(null);
			emptyDto.setEndingBalance(null);
			emptyDto.setEndingBalance1(null);
			mergeList.add(emptyDto);
		}

		String exportFilePath = CacheUtil.getSysParam(GlobalConstant.COM_PROP_ACCESS_EXPORTPATH);
		String fileName = prefix + "_" + sdf.format(reportDate) + "_资产负债表" + ".xls";
		String tempName = "AssetLiabilityNewStatement.xls";
		String tplFlodPath = "financialstatement/assetliailitystatementnew/AssetLiabilityNewStatement.xls";
		String tplPath = FileNameUtil.getTplFile(tplFlodPath);
		String outPath = exportFilePath + File.separator + fileName;

		ExportFileDto exportFileDto = new ExportFileDto();
		InputStream stream = null;
		Map<String, Object> map = new HashMap<>();
		//资产类合计
		BigDecimal ZCHJ = BigDecimal.ZERO;
		//负债类合计
		BigDecimal FZZJ = BigDecimal.ZERO;
		//实收理财金额
		BigDecimal SYLCJE = BigDecimal.ZERO;
		//资产
		for (AssetLiabilityStatementNewQuery001Dto item : assetList
		) {
			String itemCode = item.getItemCode();
			BigDecimal endingBalance = item.getEndingBalance();
			if (StringUtil.equals("ZCHJ",itemCode)){
				ZCHJ = endingBalance;
			}
		}
		//负债和净资产
		for (AssetLiabilityStatementNewQuery001Dto item : liabilityInterestList
		) {
			String itemCode = item.getItemCode();
			BigDecimal endingBalance = item.getEndingBalance();
			if (StringUtil.equals("FZ_15",itemCode)){
				FZZJ = endingBalance;
			}
			if (StringUtil.equals("SYZQY_01",itemCode)){
				SYLCJE = endingBalance;
			}
		}
		//理财产品份额净值 = 资产类合计-负债类合计
		map.put("valuation", ZCHJ.subtract(FZZJ).divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
		//理财产品份额总额份 = 实收理财资金
		map.put("sumprice", SYLCJE.divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
		map.put("org", CacheUtil.getSysParam(GlobalConstant.财务报表_编制单位));
		map.put("mergeList", mergeList);
		map.put("statementDate", sdf.format(reportDate));
		map.put("statementType", SysDataUtil.getNameFromDataDict(DictCode.报表期限类型, dto.getReportTermType()));
		//资产负债表(新)-导出增加产品代码(账套代码)字段
		//System.out.println("prefix----2>"+prefix);
		map.put("prefix",prefix);
		//资产负债表(新)-导出增加产品代码(账套代码)字段
		exportFileDto.setFileName(fileName);
		exportFileDto.setFilePath(outPath);
		try {

			// 针对weblogic环境部署，FileNameUtil.getTplFile无法获取tpl文件夹下模版。
			File templeteFile = new File(tplPath);
			// 文件存在情况,直接该路径获取文件流
			if (templeteFile.exists()) {
				stream = new FileInputStream(templeteFile);
			}
			// 文件不存在情况，即weblogic部署情况，需要换方式获取文件流。
			else {
				stream = this.getClass().getResourceAsStream(TMP_DIR + tplFlodPath);
			}

			ExcelTools.writeExcel(stream, tempName, map, outPath);
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
		return exportFileDto;
	}

	/**
	 * <一句话功能简述> <功能详细描述>
	 *
	 * @param assetList
	 * @param liabilityInterestList
	 * @return
	 * @see
	 * @since 1.0
	 */
	private List<AssetLiabilityStatementNewQuery001Dto> mergeList(List<AssetLiabilityStatementNewQuery001Dto> assetList,
																  List<AssetLiabilityStatementNewQuery001Dto> liabilityInterestList) {
		boolean assetMax = false;
		List<AssetLiabilityStatementNewQuery001Dto> maxList = null;
		List<AssetLiabilityStatementNewQuery001Dto> minList = null;
		List<AssetLiabilityStatementNewQuery001Dto> mergeList = new ArrayList<AssetLiabilityStatementNewQuery001Dto>();
		if (assetList.size() > liabilityInterestList.size()) {
			maxList = assetList;
			minList = liabilityInterestList;
			assetMax = true;
		} else {
			maxList = liabilityInterestList;
			minList = assetList;
		}
		AssetLiabilityStatementNewQuery001Dto dto = null;
		AssetLiabilityStatementNewQuery001Dto minLastDto = null;
		for (int i = 0; i < maxList.size(); i++) {
			dto = maxList.get(i);
			if (i < maxList.size() - 1) {
				if (assetMax) {
					if (i < minList.size() - 1) {
						dto.setInitialBalance1(minList.get(i).getInitialBalance());
						dto.setEndingBalance1(minList.get(i).getEndingBalance());
						dto.setItemName1(minList.get(i).getItemName());
					}
					if (i == minList.size() - 1) {
						minLastDto = minList.get(i);
					}
				} else {
					dto.setInitialBalance1(dto.getInitialBalance());
					dto.setEndingBalance1(dto.getEndingBalance());
					dto.setItemName1(dto.getItemName());
					if (i < minList.size() - 1) {
						dto.setInitialBalance(minList.get(i).getInitialBalance());
						dto.setEndingBalance(minList.get(i).getEndingBalance());
						dto.setItemName(minList.get(i).getItemName());
					} else {
						dto.setInitialBalance(null);
						dto.setEndingBalance(null);
						dto.setItemName(null);
					}
					if (i == minList.size() - 1) {
						minLastDto = minList.get(i);
					}
				}
			}

			if (i == maxList.size() - 1) {
				if (assetMax) {
					dto.setInitialBalance1(minLastDto.getInitialBalance());
					dto.setEndingBalance1(minLastDto.getEndingBalance());
					dto.setItemName1(minLastDto.getItemName());
				} else {
					dto.setInitialBalance1(dto.getInitialBalance());
					dto.setEndingBalance1(dto.getEndingBalance());
					dto.setItemName1(dto.getItemName());

					dto.setInitialBalance(minLastDto.getInitialBalance());
					dto.setEndingBalance(minLastDto.getEndingBalance());
					dto.setItemName(minLastDto.getItemName());
				}
			}
			mergeList.add(dto);
		}
		return mergeList;
	}

	/**
	 * 获取默认日期信息
	 *
	 * @param pageParam
	 * @return AssetLiabilityStatementNewQuery001SearchDto
	 */
	public AssetLiabilityStatementNewQuery001SearchDto getDateInfo(Map<String, Object> pageParam) {
		// String booksetId = String.valueOf(pageParam.get("booksetId"));
		String reportTermType = String.valueOf(pageParam.get("reportTermType"));
		AssetLiabilityStatementNewQuery001SearchDto datedto = new AssetLiabilityStatementNewQuery001SearchDto();
		datedto.setReportDate(DateUtil.getSysDate());
		// if(booksetId != "null" && booksetId != ""){
		// datedto = mapper.getDateInfo1(pageParam);
		// }else{
		// List<AssetLiabilityStatementNewQuery001SearchDto> dateList =
		// mapper.getDateInfo2(pageParam);
		// for(AssetLiabilityStatementNewQuery001SearchDto item:dateList){
		// if(datedto.getReportDate() == null || datedto.getReportDate() == ""){
		// datedto.setReportDate(item.getReportDate());
		// }else{
		// if(datedto.getReportDate().compareTo(item.getReportDate()) > 0){
		// datedto.setReportDate(item.getReportDate());
		// }
		// }
		// }
		// }
		if (DictCodeListExt1.报表期限类型_日报.getValue().equals(reportTermType)) {
			// datedto.setReportDate(DateUtil.format(DateUtil.addDate(DateUtil.parse(datedto.getReportDate()),
			// 0, 0, -1)));
		} else if (DictCodeListExt1.报表期限类型_月报.getValue().equals(reportTermType)) {
			datedto.setReportDate(
					DateUtil.format(DateUtil.addDate(DateUtil.parse(datedto.getReportDate()), 0, -1, 0), "yyyy-MM"));
		} else if (DictCodeListExt1.报表期限类型_季报.getValue().equals(reportTermType)) {
			int season = DateUtil.getSeason(DateUtil.parse(datedto.getReportDate()));
			if (season == 1) {
				datedto.setReportTerm(DictCodeListExt1.季度_第四季度.getValue());
				datedto.setReportDate(
						DateUtil.format(DateUtil.addDate(DateUtil.parse(datedto.getReportDate()), -1, 0, 0), "yyyy"));
			} else if (season == 2) {
				datedto.setReportTerm(DictCodeListExt1.季度_第一季度.getValue());
				datedto.setReportDate(DateUtil.format(DateUtil.parse(datedto.getReportDate()), "yyyy"));
			} else if (season == 3) {
				datedto.setReportTerm(DictCodeListExt1.季度_第二季度.getValue());
				datedto.setReportDate(DateUtil.format(DateUtil.parse(datedto.getReportDate()), "yyyy"));
			} else if (season == 4) {
				datedto.setReportTerm(DictCodeListExt1.季度_第三季度.getValue());
				datedto.setReportDate(DateUtil.format(DateUtil.parse(datedto.getReportDate()), "yyyy"));
			}
		} else if (DictCodeListExt1.报表期限类型_半年报.getValue().equals(reportTermType)) {
			int month = DateUtil.getMonth(datedto.getReportDate());
			if (month <= 6) {
				datedto.setReportTerm(DictCodeListExt1.半年_下半年.getValue());
				datedto.setReportDate(
						DateUtil.format(DateUtil.addDate(DateUtil.parse(datedto.getReportDate()), -1, 0, 0), "yyyy"));
			} else {
				datedto.setReportTerm(DictCodeListExt1.半年_上半年.getValue());
				datedto.setReportDate(DateUtil.format(DateUtil.parse(datedto.getReportDate()), "yyyy"));
			}
		} else if (DictCodeListExt1.报表期限类型_年报.getValue().equals(reportTermType)) {
			datedto.setReportDate(
					DateUtil.format(DateUtil.addDate(DateUtil.parse(datedto.getReportDate()), -1, 0, 0), "yyyy"));
		}
		return datedto;
	}

	/**
	 * <批量导出功能返回文件路径> <功能详细描述>
	 *
	 * @param asdto
	 * @throws IOException
	 * @throws ParseException
	 * @see
	 * @since 1.0
	 */
	public List<String> exportExcelBatch(AssetLiabilityStatementNewQuery001SearchDto asdto)
			throws ParseException, IOException {
		List<String> paths = new ArrayList<>();
		List<SuggestDto> suggestDtos = asdto.getAccountList();// 获取所有的账套
		for (SuggestDto dto : suggestDtos) {// 因需要按单账套为单纯导出，所以分解为单账套
			asdto.setBooksetId(dto.getCode());
			ExportFileDto exportFileDto = createExcel(asdto);
			paths.add(exportFileDto.getFilePath());
		}
		return paths;

	}

	/**
	 * 检索账套代码列表（只取理财账套）
	 *
	 * @param  过滤参数
	 * @return 上级科目代码列表
	 * @see
	 * @since 1.0
	 */
	public List<SuggestDto> getBooksetId(String profitType) {
		String booksetType = DictCodeList.账套类别_理财账套.getValue();
		Map<String,Object> param  = new HashMap<>();
		param.put("profitType",profitType);
		param.put("booksetType",booksetType);

		List<SuggestDto> list = restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
						+ "/AssetLiabilityStatementNewQuery001/getBooksetId"
				, param,
				SuggestDto.class);
		return list;
	}

	/**
	 * 处理重复的资产[不同的账套代码]
	 * @param dtoList
	 * @return
	 */
	public List<AssetLiabilityStatementNewQuery001Dto> handleDuplication(List<AssetLiabilityStatementNewQuery001Dto> dtoList){
		Map<String, List<AssetLiabilityStatementNewQuery001Dto>> dtoListMap = dtoList.stream().collect(Collectors.groupingBy(AssetLiabilityStatementNewQuery001Dto::getItemCode));
		dtoList.clear();
		if (StringUtil.isNotEmpty(dtoListMap) && !dtoListMap.isEmpty()) {
			for (Map.Entry<String, List<AssetLiabilityStatementNewQuery001Dto>> entry : dtoListMap.entrySet()) {
				List<AssetLiabilityStatementNewQuery001Dto> valueList = entry.getValue();
				AssetLiabilityStatementNewQuery001Dto tmpDto = new AssetLiabilityStatementNewQuery001Dto();
				//本期/年金额
				BigDecimal endingBalance = BigDecimal.ZERO;
				//期/年初余额
				BigDecimal initialBalance = BigDecimal.ZERO;
				if (!StringUtil.isEmptyList(valueList)){
					tmpDto = valueList.get(0);
				}
				for (AssetLiabilityStatementNewQuery001Dto item:valueList
				) {
					endingBalance = endingBalance.add(item.getEndingBalance());
					initialBalance = initialBalance.add(item.getInitialBalance());
				}
				tmpDto.setInitialBalance(initialBalance);
				tmpDto.setEndingBalance(endingBalance);
				dtoList.add(tmpDto);
			}
		}
		return dtoList;
	}

}
