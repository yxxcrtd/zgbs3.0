/*
 * 文件名：AssetLiabilityStatementQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年9月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.accounadjustmanage.valbatchexport.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.*;
import com.joyintech.tams.constant.DictCodeListExt1;
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

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.valuation.accounadjustmanage.valbatchexport.common.ValBatchExport001Util;
import com.joyintech.tams.valuation.accounadjustmanage.valbatchexport.dto.ValBatchExport001Dto;
import com.joyintech.tams.valuation.accounadjustmanage.valbatchexport.dto.ValBatchExport001SearchDto;
import com.joyintech.tams.valuation.accounadjustmanage.valuationtablegeneration.dto.ExportFileDto;
import com.joyintech.tools.util.ExcelTools;

/**
 * 资产负债表
 * 
 * @author huanghao
 * @version 1.0 2020年5月31日
 * @see ValBatchExport001Service
 * @since 1.0
 */
@Service
public class ValBatchExport001Service extends CommonExportService<ValBatchExport001Dto> {
	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(ValBatchExport001Service.class);

	/**
	 * 针对weblogic部署无法获取模版问题
	 */
	private static final String TMP_DIR = "/tpl/";

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
						+ "/ValBatchExport001/getLinkSelectOption"
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
	public List<ValBatchExport001Dto> findBokBalance(ValBatchExport001SearchDto dto) throws ParseException {
		List<ValBatchExport001Dto> list = restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
						+ "/ValBatchExport001/selectBokBalance"
				, dto,
				ValBatchExport001Dto.class);
		return list;
	}

	/**
	 * 查询科目余额表
	 * 
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<ValBatchExport001Dto> findBokBalanceAll(ValBatchExport001SearchDto dto) throws ParseException {
		List<ValBatchExport001Dto> list = restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
						+ "/ValBatchExport001/selectBokBalanceAll"
				, dto,
				ValBatchExport001Dto.class);
		return list;
	}

	/**
	 * 获得 资产报表数据（单账套）
	 * 
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<ValBatchExport001Dto> findAssetsStatement(ValBatchExport001SearchDto dto) throws ParseException {

		dto.setItemTypes(Arrays.asList(DictCodeListExt2.科目类别_资产类.getValue()));// 科目类别
																			// 资产类
		dto.setBalanceDate(analyseBalanceDate(dto, ValBatchExport001Util.ENDING_BALANCE));// 设置期末日期
		List<ValBatchExport001Dto> endList = findBokBalance(dto); // 期末余额列表

		dto.setBalanceDate(analyseBalanceDate(dto, ValBatchExport001Util.INITIAL_BALANCE));// 设置年初日期
		List<ValBatchExport001Dto> initList = findBokBalance(dto); // 年初余额列表

		List<ValBatchExport001Dto> dtoList = doMakeDtoDate(endList, initList);
		return dtoList;
	}

	/**
	 * <合并期末余额与年初余额> <将两次查询的结果集组装成ValBatchExport001Dto>
	 * 
	 * @param endList
	 *            期末余额
	 * @param initList
	 *            年初余额
	 * @return
	 * @see
	 * @since 1.0
	 */
	private List<ValBatchExport001Dto> doMakeDtoDate(List<ValBatchExport001Dto> endList,
			List<ValBatchExport001Dto> initList) {

		List<ValBatchExport001Dto> list = new ArrayList<ValBatchExport001Dto>();
		if (null == endList || endList.size() < 1) {// 如果期末余额为空,则直接返回
			return list;
		} else {
			for (ValBatchExport001Dto end : endList) {
				// end.setEndingBalance(end.getoAmt());
				// end.setItemName(end.getSubjectName());
				if (null != initList && initList.size() > 0) {// 如果年初余额为空,则直接返回为空
					for (ValBatchExport001Dto init : initList) {
						init.setSubjectName(init.getItemName());
						if (null != init.getItemName() && end.getSubjectName().equals(init.getItemName())) {
							end.setInitialBalance(init.getEndingBalance());
						}
					}
				}
			}
			list.addAll(endList);

		}

		return list;
	}

	/**
	 * 获得 资产报表数据（全账套）
	 * 
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<ValBatchExport001Dto> findAssetsStatementAll(ValBatchExport001SearchDto dto) throws ParseException {
		dto.setItemTypes(Arrays.asList(DictCodeList.科目类型_资产类.getValue()));// 科目类别
																			// 资产类

		dto.setBalanceDate(analyseBalanceDate(dto, ValBatchExport001Util.ENDING_BALANCE));// 设置期末日期
		List<ValBatchExport001Dto> endList = findBokBalanceAll(dto); // 期末余额列表

		dto.setBalanceDate(analyseBalanceDate(dto, ValBatchExport001Util.INITIAL_BALANCE));// 设置年初日期
		List<ValBatchExport001Dto> initList = findBokBalanceAll(dto); // 年初余额列表

		List<ValBatchExport001Dto> dtoList = doMakeDtoDate(endList, initList);
		return dtoList;
	}

	/**
	 * 处理样式
	 * 
	 * @param list
	 *            报表维度
	 * @see
	 * @since 1.0
	 */
	public void dealStyle(List<ValBatchExport001Dto> list) {
		for (ValBatchExport001Dto dto : list) {
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
	public List<ValBatchExport001Dto> findLiabilityInterestStatement(ValBatchExport001SearchDto dto)
			throws ParseException {

		dto.setBalanceDate(analyseBalanceDate(dto, ValBatchExport001Util.ENDING_BALANCE));// 设置期末日期
		List<ValBatchExport001Dto> endListALL = makeLiabilityLnterestList(dto, false);// 期末负债和所有者权益报表数据

		dto.setBalanceDate(analyseBalanceDate(dto, ValBatchExport001Util.INITIAL_BALANCE));// 设置年初日期
		List<ValBatchExport001Dto> initListALL = makeLiabilityLnterestList(dto, false);// 年初负债和所有者权益报表数据

		List<ValBatchExport001Dto> dtoList = doMakeDtoDate(endListALL, initListALL);

		return dtoList;
	}

	/**
	 * <组装负债以及所有者收益数据> <查询不同日期的负债所有者收益数据，按照时间,账套类型进行区别> isItems为true :单账套
	 * isItems为false :全账套
	 * 
	 * @return
	 * @throws ParseException
	 * @see
	 * @since 1.0
	 */
	private List<ValBatchExport001Dto> makeLiabilityLnterestList(ValBatchExport001SearchDto dto, boolean isItems)
			throws ParseException {

		BigDecimal endingBalance = BigDecimal.ZERO;
		BigDecimal initialBalance = BigDecimal.ZERO;

		List<ValBatchExport001Dto> ListALL = new ArrayList<ValBatchExport001Dto>();// 期末负债和所有者权益报表数据汇总
		dto.setItemTypes(Arrays.asList(DictCodeListExt2.科目类别_负债类.getValue()));// 设置科目类别为负债类
		List<ValBatchExport001Dto> List_liability = new ArrayList<ValBatchExport001Dto>();
		if (isItems) {
			List_liability = findBokBalanceAll(dto); // 期末负债列表(全账套)
		} else {
			List_liability = findBokBalance(dto); // 期末负债列表(单账套)
		}
		if(null != List_liability){
			ListALL.addAll(List_liability);// 汇总负债类别
		}
		if (null != List_liability && List_liability.size() > 0) {// 如果为空的话就不加统计记录了
			for (ValBatchExport001Dto item : List_liability) {
				if (null != item.getItemType() && DictCodeListExt2.科目类别_负债类.getValue().equals(item.getItemType())) {// 选择负债类型的科目
					endingBalance = endingBalance.add(item.getoAmt());
					initialBalance = initialBalance.add(item.getoAmt());
				}
			}
			if (endingBalance.compareTo(BigDecimal.ZERO) == 0) {// 判断是否为0
				endingBalance = BigDecimal.ZERO;// 为0的情况下显示为0.00的格式，否则表格中显示为空
			}
			if (initialBalance.compareTo(BigDecimal.ZERO) == 0) {// 判断是否为0
				initialBalance = BigDecimal.ZERO; // 为0的情况下显示为0.00的格式，否则表格中显示为空
			}

			ValBatchExport001Dto query001Dto = new ValBatchExport001Dto(ValBatchExport001Util.LIABILITY_SUM,
					endingBalance, initialBalance);
			query001Dto.setSubjectName(ValBatchExport001Util.LIABILITY_SUM);
			ListALL.add(query001Dto);// 添加负债总计
		}

		endingBalance = BigDecimal.ZERO;
		initialBalance = BigDecimal.ZERO;
		dto.setItemTypes(Arrays.asList(DictCodeList.科目类型_所有者权益类.getValue()));// 设置科目类别为科目类别_所有者权益类
		List<ValBatchExport001Dto> List_interest = new ArrayList<ValBatchExport001Dto>();
		if (isItems) {
			List_interest = findBokBalanceAll(dto);// 期末所有者权益类列表(全帐套)
		} else {
			List_interest = findBokBalance(dto); // 期末所有者权益类列表(单账套)
		}
		if(null != List_interest){
			ListALL.addAll(List_interest);// 汇总所有者权益类列表
		}
		if (null != List_interest && List_interest.size() > 0) {// 如果为空的话就不加统计记录了
			for (ValBatchExport001Dto item : List_interest) {
				if (null != item.getItemType() && DictCodeList.科目类型_所有者权益类.getValue().equals(item.getItemType())) {// 选择所有者权益类型的科目
					endingBalance = endingBalance.add(item.getoAmt());
					initialBalance = initialBalance.add(item.getoAmt());
				}
			}
			if (endingBalance.compareTo(BigDecimal.ZERO) == 0) {// 判断是否为0
				endingBalance = BigDecimal.ZERO;// 为0的情况下显示为0.00的格式，否则表格中显示为空
			}
			if (initialBalance.compareTo(BigDecimal.ZERO) == 0) {// 判断是否为0
				initialBalance = BigDecimal.ZERO; // 为0的情况下显示为0.00的格式，否则表格中显示为空
			}
			ValBatchExport001Dto query001Dto = new ValBatchExport001Dto(ValBatchExport001Util.INTEREST_SUM,
					endingBalance, initialBalance);
			query001Dto.setSubjectName(ValBatchExport001Util.INTEREST_SUM);
			ListALL.add(query001Dto);// 添加负债总计
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
	public List<ValBatchExport001Dto> findLiabilityInterestStatementAll(ValBatchExport001SearchDto dto)
			throws ParseException {

		dto.setBalanceDate(analyseBalanceDate(dto, ValBatchExport001Util.ENDING_BALANCE));// 设置期末日期
		List<ValBatchExport001Dto> endListALL = makeLiabilityLnterestList(dto, true);// 期末负债和所有者权益报表数据

		dto.setBalanceDate(analyseBalanceDate(dto, ValBatchExport001Util.INITIAL_BALANCE));// 设置年初日期
		List<ValBatchExport001Dto> initListALL = makeLiabilityLnterestList(dto, true);// 年初负债和所有者权益报表数据

		List<ValBatchExport001Dto> dtoList = doMakeDtoDate(endListALL, initListALL);

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
	public Date analyseBalanceDate(ValBatchExport001SearchDto dto, String balanceType) throws ParseException {
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

		if (ValBatchExport001Util.ENDING_BALANCE.equals(balanceType)) {// 期末
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
	public ResponseEntity<byte[]> exportExcel(ValBatchExport001SearchDto dto) throws ParseException, IOException {
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
	public ExportFileDto createExcel(ValBatchExport001SearchDto dto) throws ParseException, IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date reportDate = analyseBalanceDate(dto, ValBatchExport001Util.ENDING_BALANCE);
		List<ValBatchExport001Dto> assetList = new ArrayList<ValBatchExport001Dto>();
		String prefix = "";
		if (DictCodeListExt2.批处理账套类型_账套.getValue().equals(dto.getReportDimen())) {
			assetList = findAssetsStatement(dto);
			prefix = dto.getBooksetId()[0];
		} else {
			assetList = findAssetsStatementAll(dto);
			prefix = "账套组";
		}
		if (CollectionUtils.isNotEmpty(assetList)) {// 设置总计
			assetList.addAll(ValBatchExport001Util.getAssetListSum(assetList));
		}
		List<ValBatchExport001Dto> liabilityInterestList = new ArrayList<ValBatchExport001Dto>();
		if (DictCodeListExt2.批处理账套类型_账套.getValue().equals(dto.getReportDimen())) {
			liabilityInterestList = findLiabilityInterestStatement(dto);
		} else {
			liabilityInterestList = findLiabilityInterestStatementAll(dto);
		}
		if (CollectionUtils.isNotEmpty(liabilityInterestList)) {// 设置总计
			// assembleLiaIntSum(liabilityInterestList);
			liabilityInterestList.addAll(ValBatchExport001Util.getLiabilityInterestListSum(liabilityInterestList));
		}
		// 合并List
		List<ValBatchExport001Dto> mergeList = mergeList(assetList, liabilityInterestList);

		// 解决报表数据为空导出模板公式的问题
		if (mergeList.isEmpty()) {
			ValBatchExport001Dto emptyDto = new ValBatchExport001Dto();
			emptyDto.setInitialBalance(null);
			emptyDto.setInitialBalance1(null);
			emptyDto.setEndingBalance(null);
			emptyDto.setEndingBalance1(null);
			mergeList.add(emptyDto);
		}

		String exportFilePath = CacheUtil.getSysParam(GlobalConstant.COM_PROP_ACCESS_EXPORTPATH);
		String fileName = prefix + "_" + sdf.format(reportDate) + "_资产负债表" + ".xls";
		String tempName = "AssetLiabilityStatement.xls";
		String tplFlodPath = "/financialstatement/assetliailitystatement/AssetLiabilityStatement.xls";
		String tplPath = FileNameUtil.getTplFile(tplFlodPath);
		String outPath = exportFilePath + File.separator + fileName;

		ExportFileDto exportFileDto = new ExportFileDto();
		InputStream stream = null;
		Map<String, Object> map = new HashMap<>();
		map.put("org", CacheUtil.getSysParam(GlobalConstant.财务报表_编制单位));
		map.put("mergeList", mergeList);
		map.put("statementDate", sdf.format(reportDate));
		map.put("statementType", SysDataUtil.getNameFromDataDict(DictCode.报表期限类型, dto.getReportTermType()));
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
	private List<ValBatchExport001Dto> mergeList(List<ValBatchExport001Dto> assetList,
			List<ValBatchExport001Dto> liabilityInterestList) {
		boolean assetMax = false;
		List<ValBatchExport001Dto> maxList = null;
		List<ValBatchExport001Dto> minList = null;
		List<ValBatchExport001Dto> mergeList = new ArrayList<ValBatchExport001Dto>();
		if (assetList.size() > liabilityInterestList.size()) {
			maxList = assetList;
			minList = liabilityInterestList;
			assetMax = true;
		} else {
			maxList = liabilityInterestList;
			minList = assetList;
		}
		ValBatchExport001Dto dto = null;
		ValBatchExport001Dto minLastDto = null;
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
	 * @return ValBatchExport001SearchDto
	 */
	public ValBatchExport001SearchDto getDateInfo(Map<String, Object> pageParam) {
		// String booksetId = String.valueOf(pageParam.get("booksetId"));
		String reportTermType = String.valueOf(pageParam.get("reportTermType"));
		ValBatchExport001SearchDto datedto = new ValBatchExport001SearchDto();
		datedto.setReportDate(DateUtil.getSysDate());
		// if(booksetId != "null" && booksetId != ""){
		// datedto = mapper.getDateInfo1(pageParam);
		// }else{
		// List<ValBatchExport001SearchDto> dateList =
		// mapper.getDateInfo2(pageParam);
		// for(ValBatchExport001SearchDto item:dateList){
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
}
