package com.joyintech.tams.valuation.basicdata.commarketmanagement.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.progressbar.ProgressBarService;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.dto.ExcelValidDto;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.tams.valuation.basicdata.commarketmanagement.dto.ComMarketManagementDto;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComMarketManagement001Service extends CommonExportService<ComMarketManagementDto>
implements BaseProcessService<ComMarketManagementDto>{

	/**
	 * 上传控件进度条Service
	 */
	@Autowired(required = false)
	@Qualifier("commonProgressBarService")
	private ProgressBarService progressBarService;
	 /**
     * 分页查询
     * 
     * @param pageParam 传入的查询条件
     * @return Page<MstInterestRateQuotationInfoDto> page对象
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
	  public Page<ComMarketManagementDto> selectForPage(Map<String, Object> pageParam){
/*	        pageParam.put("customerType", DictCodeList.客户类型_法人.getValue());
*/	        return restTemplate.postForPage(
	            ServiceApplication.VALUATION_BASICDATA_SERVICE_NAME + "/ComMarketManagement/selectForPage",
	            pageParam, ComMarketManagementDto.class);
	    }

	@Override
	public void doComplete(ComMarketManagementDto arg0, OperationType arg1) {
		 
		
	}

	 /**
     * 
     * 客户名称联想文本框service层实现
     * <功能详细描述>
     * @param map 联想文本框查询信息
     * @return 查询结果
     * @see
     * @since 1.0
     */

	public List<SuggestDto> selectFinprodName(Map<String, Object> param) {
		 return restTemplate.postForList(
		            ServiceApplication.VALUATION_BASICDATA_SERVICE_NAME + "/ComMarketManagement/selectFinprodName",
		            param, SuggestDto.class);
	}

	 

	/*public Page<SelectOptionDto> getGradeResult(Map<String, Object> param) {
	        return restTemplate.postForPage(
	        		 ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketManagement/getGradeResult",
	                 param,SelectOptionDto.class);
		}
	 */
	/**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
                "/com/joyintech/tams/valuation/basicdata/commarketmanagement/conf/export_conf.xml")
        .dataSource(DataSourceType.MASERT);
    }
    
    @Override
    public Page<ComMarketManagementDto> selectPageData(Map<String, Object> selectParam) {
    	return selectForPage(selectParam);
    }

	/**
	 * 评级信息导入
	 * @param flieName
	 * @return
	 */
	public List<ExcelValidDto> importData(String flieName) throws Exception {
		String pageId = "ComMarketManagement";
		String barId = ProgressBarService.FILE_UPLOAD_ID;
		try {
			// 检查文件名是否为空
			if (StringUtil.isNotEmpty(flieName)) {
				if (flieName.indexOf("评级信息") != -1) {
					List<List<String>> dataList = dataAnalysis(flieName);
					progressBarService.updatePercent(pageId, barId, 0.6D, false);//更新百分百
					if (null != dataList && dataList.size() > 0) {
						List<ComMarketManagementDto> datas = new ArrayList<>();
						// 将excel表格中的数据转化为dto
						List<ExcelValidDto> checkResult = checkAndParseExcelData(dataList, datas);
						progressBarService.updatePercent(pageId, barId, 0.7D, false);//更新百分百
						if (StringUtil.isEmptyList(checkResult)) {
							importDatas(datas);
							progressBarService.updatePercent(pageId, barId, 0.95D, false);//更新百分百
						}
						progressBarService.updatePercent(pageId, barId, 1D, false);//更新百分百
						return checkResult;
					} else {
						List<ExcelValidDto> checkResult = new ArrayList<>();
						checkResult.add(new ExcelValidDto(StringUtil.EMPTY, StringUtil.EMPTY, "导入文件中无数据！"));
						progressBarService.updatePercent(pageId, barId, 1D, false);//更新百分百
						return checkResult;
					}
				} else {
					List<ExcelValidDto> checkResult = new ArrayList<>();
					checkResult.add(new ExcelValidDto(StringUtil.EMPTY, StringUtil.EMPTY, "文件名称错误，请下载模板后上传！"));
					progressBarService.updatePercent(pageId, barId, 1D, false);//更新百分百
					return checkResult;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			List<ExcelValidDto> checkResult = new ArrayList<>();
			checkResult.add(new ExcelValidDto(StringUtil.EMPTY, StringUtil.EMPTY, "导入文件过程中出现错误！"));
			progressBarService.updatePercent(pageId, barId, 1D, false);//更新百分百
			return checkResult;
		}
		progressBarService.updatePercent(pageId, barId, 1D, false);//更新百分百
		return null;
	}

	/**
	 * excel导入数据整理
	 * @param flieName 文件名称
	 * @return 检验list
	 * @throws Exception 异常
	 * @see
	 * @since 1.0
	 */
	public List<List<String>> dataAnalysis(String flieName) throws Exception {
		String prefix = CacheUtil.getSysParam(GlobalConstant.COM_PROP_UPLOAD_PATH);
		if(!StringUtil.startsWith(flieName, prefix)
				&& !StringUtil.startsWith(flieName, prefix.replace("/", File.separator))) {
			flieName = prefix + File.separator + flieName;
		}
		ExcelUtil excelUtil = ExcelUtil.build(flieName, null);
		List<List<String>> dataList = excelUtil.readToList(0, 1, 0);
		return dataList;
	}

	/**
	 * 检验数据
	 * @param dataList
	 * @param datas
	 * @return
	 * @throws ParseException
	 */
	private List<ExcelValidDto> checkAndParseExcelData(List<List<String>> dataList, List<ComMarketManagementDto> datas) throws ParseException {
		List<ExcelValidDto> excelValidList = new ArrayList<>();
		for (int i = 0; i < dataList.size(); i++ ) {
			ComMarketManagementDto dto = converToBeanSingleRow(dataList.get(i), i, excelValidList);
			if(dto != null) {
				datas.add(dto);
			}
		}
		if(StringUtil.isEmptyList(excelValidList)) {
			checkRepeatData(datas, excelValidList);
			if(StringUtil.isEmptyList(excelValidList)) {
				checkEffData(datas, excelValidList);
			}
		}
		return excelValidList;
	}

	/**
	 * excel行转换为实体类
	 * @param list
	 * @param i
	 * @return
	 */
	private ComMarketManagementDto converToBeanSingleRow(List<String> list, int i, List<ExcelValidDto> excelValidList) {
		if (null == list || list.size() < 10) {
			ExcelValidDto excelValidDto = new ExcelValidDto(StringUtil.EMPTY, String.valueOf(i+2), "数据列数错误");
			excelValidList.add(excelValidDto);
			return null;
		}
		List<String> checkList = new ArrayList<>();
		ComMarketManagementDto dto = new ComMarketManagementDto();
		//对象类型
		int index = 0;
		if (StringUtil.isNotEmpty(list.get(index))) {
			String lastStr = getEffDictKey(list.get(index).trim(), DictCode.评级类型);
			if (StringUtil.isNotEmpty(lastStr)) {
				dto.setoType(lastStr);
			}
			else {
				checkList.add("不是有效的对象类型！");
			}
		}
		else {
			checkList.add("对象类型为空！");
		}
		index++;
		//评级机构
		if (StringUtil.isNotEmpty(list.get(index))) {
			dto.setGradeOrgId(list.get(index).trim());
			String lastStr = getEffDictKey(list.get(index).trim(), DictCode.评级机构);
			if (StringUtil.isNotEmpty(lastStr)) {
				dto.setGradeOrgId(lastStr);
			}
			else {
				checkList.add("不是有效的评级机构！");
			}
		}
		else {
			checkList.add("评级机构为空！");
		}
		index++;
		//对象代码
		if (StringUtil.isNotEmpty(list.get(index))) {
			dto.setoCode(list.get(index).trim());
		}
		else {
			checkList.add("对象代码为空！");
		}
		index++;
		//对象名称
		if (StringUtil.isNotEmpty(list.get(index))) {
			dto.setoCodeName(list.get(index).trim());
		}
		index++;
		//公告日期
		if (StringUtil.isNotEmpty(list.get(index))) {
			try {
				Date date = DateUtil.parse(list.get(index).trim());
				dto.setNoticeDate(DateUtil.format(date, DateUtil.DEFAULT_DATE_FORMAT_EN));
			}
			catch (Exception e) {
				checkList.add("公告日期必须为日期格式！");
			}

		}
		index++;
		//评级日期
		if (StringUtil.isNotEmpty(list.get(index))) {
			try {
				Date date = DateUtil.parse(list.get(index).trim());
				dto.setGradeDate(DateUtil.format(date, DateUtil.DEFAULT_DATE_FORMAT_EN));
			}
			catch (Exception e) {
				checkList.add("评级日期必须为日期格式！");
			}
		}
		else {
			checkList.add("评级日期为空！");
		}
		index++;
		//评级类型 长短期
		if (StringUtil.isNotEmpty(list.get(index))) {
			String lastStr = getEffDictKey(list.get(index).trim(), DictCode.长期短期);
			if (StringUtil.isNotEmpty(lastStr)) {
				dto.setShortLongTerm(lastStr);
			}
			else {
				checkList.add("不是有效的评级类型！");
			}
		}
		index++;
		//评级结果
		if (StringUtil.isNotEmpty(list.get(index))) {
			String lastStr = getEffDictKey(list.get(index).trim(), DictCode.评级结果);
//			String lastStr = list.get(index).trim();
			if (StringUtil.isNotEmpty(lastStr)) {
				dto.setGradeResult(lastStr);
			}
			else {
				checkList.add("不是有效的评级结果！");
			}
		}
		index++;
		//评级展望
		if (StringUtil.isNotEmpty(list.get(index))) {
			String lastStr = getEffDictKey(list.get(index).trim(), DictCode.评级展望);
			if (StringUtil.isNotEmpty(lastStr)) {
				dto.setGradeExpectation(lastStr);
			}
			else {
				checkList.add("不是有效的评级展望！");
			}
		}
		index++;
		//变动方向
		if (StringUtil.isNotEmpty(list.get(index))) {
			String lastStr = getEffDictKey(list.get(index).trim(), DictCode.变动方向);
			if (StringUtil.isNotEmpty(lastStr)) {
				dto.setDirectionChange(lastStr);
			}
			else {
				checkList.add("不是有效的变动方向！");
			}
		}
		index++;

		dto.setInputType(DictCodeListExt3.估值数据来源_手工.getValue());
		dto.setCreateUser(CommonUtil.getUserName());
		dto.setCreateDept(CommonUtil.getDeptCode());
		dto.setUpdateUser(CommonUtil.getUserName());
		if(!StringUtil.isEmptyList(checkList)) {
			ExcelValidDto excelValidDto = new ExcelValidDto(list.get(2), String.valueOf(i + 2), StringUtil.joinString(checkList,""));
			excelValidList.add(excelValidDto);
		}
		return dto;
	}

	/**
	 * 按照空格分隔字符，且获取值是否在指定字典值中
	 * @param str
	 * @param dictCode
	 * @return
	 */
	private String getEffDictKey(String str, DictCode dictCode) {
		if (StringUtil.isNotEmpty(str)) {
			String lastStr = StringUtil.split(str, StringUtil.SPACE)[0];
			String keyValue = SysDataUtil.getNameFromDataDict(dictCode, lastStr);
			if (StringUtil.isNotEmpty(keyValue)) {
				return lastStr;
			}
			else {
				return null;
			}
		}
		else {
			return StringUtil.EMPTY;
		}
	}

	/**
	 * 检验重复数据
	 * @param datas
	 * @param excelValidList
	 */
	private void checkRepeatData(List<ComMarketManagementDto> datas,  List<ExcelValidDto> excelValidList) {
		for (int i = 0; i < datas.size(); i++ ) {
			String oCode = datas.get(i).getoCode();
			String gradeOrgId = datas.get(i).getGradeOrgId();
			String gradeDate = datas.get(i).getGradeDate();
			String oType = datas.get(i).getoType();
			boolean repeatFlag = false;
			for (int j = i+1; j < datas.size(); j++ ) {
				String oCodeOther = datas.get(j).getoCode();
				String gradeOrgIdOther = datas.get(j).getGradeOrgId();
				String gradeDateOther = datas.get(j).getGradeDate();
				String oTypeOther = datas.get(j).getoType();
				if(StringUtil.equals(oCode, oCodeOther) && StringUtil.equals(gradeOrgId, gradeOrgIdOther)
					&& StringUtil.equals(gradeDate, gradeDateOther) && StringUtil.equals(oType, oTypeOther)) {
					ExcelValidDto excelValidDto = new ExcelValidDto(oCode, String.valueOf(i + 2), "与第" + String.valueOf(j + 2) + "重复！");
					excelValidList.add(excelValidDto);
					ExcelValidDto excelValidDtoOther = new ExcelValidDto(oCode, String.valueOf(j + 2), "与第" + String.valueOf(i + 2) + "重复！");
					excelValidList.add(excelValidDtoOther);
					break;
				}
			}
		}
	}

	/**
	 * 判定的对象代码是否存在
	 * @param datas
	 * @param excelValidList
	 */
	private void checkEffData(List<ComMarketManagementDto> datas, List<ExcelValidDto> excelValidList) {
		Map<String, List<ComMarketManagementDto>> maps = datas.stream().collect(Collectors.groupingBy(ComMarketManagementDto::getoType));
		// 定义 变量.市场代码集合
		List<ComMarketManagementDto> secIdList = new ArrayList<ComMarketManagementDto>();
		List<ComMarketManagementDto> finprodList = maps.get(DictCodeList.评级类型_债项评级.getValue());
		// 债项评级不为空，先存入市场代码集合
		if(CollectionUtils.isNotEmpty(finprodList)){
			secIdList.addAll(finprodList);
		}
		List<ComMarketManagementDto> customList = maps.get(DictCodeList.评级类型_主体评级.getValue());

		// 定义 变量.机构信息Map集合
		Map<String, List<ComMarketManagementDto>> customMap = new HashMap<>();
		// 定义 变量.标的信息Map集合
		Map<String, List<ComMarketManagementDto>> finprodMap = new HashMap<>();
		// 先取主体评级对应评级机构信息，对应进行分组
		if(CollectionUtils.isNotEmpty(customList)){
			List<ComMarketManagementDto> existCustomList = getCustomerLists(customList);
			if(!StringUtil.isEmptyList(existCustomList)) {
				customMap = existCustomList.stream().collect(Collectors.groupingBy(ComMarketManagementDto::getoCode));
			}
			// 存入 变量.金融产品代码集合，由于债券主体代码对应市场代码，故债券还需对比标的信息
			secIdList.addAll(customList);
		}
		// 变量.市场代码集合 不为空
		if(CollectionUtils.isNotEmpty(secIdList)){
			// 根据市场代码查询对应标的信息
			List<ComMarketManagementDto> existFinprodList = getFinprodLists(secIdList);
			if(!StringUtil.isEmptyList(existFinprodList)) {
				finprodMap = existFinprodList.stream().collect(Collectors.groupingBy(ComMarketManagementDto::getoCode));
			}
		}
		for (int i = 0; i < datas.size(); i++ ) {
			if(StringUtil.equals(DictCodeList.评级类型_主体评级.getValue(), datas.get(i).getoType())) {
				// 判断机构信息Map集合 和 标的信息Map集合是否都不包含
				if(!customMap.containsKey(datas.get(i).getoCode()) && !finprodMap.containsKey(datas.get(i).getoCode())) {
					ExcelValidDto excelValidDto = new ExcelValidDto(datas.get(i).getoCode(), String.valueOf(i + 2), "对象代码不存在！");
					excelValidList.add(excelValidDto);
				}
			}
			else {
				if(!finprodMap.containsKey(datas.get(i).getoCode())) {
					ExcelValidDto excelValidDto = new ExcelValidDto(datas.get(i).getoCode(), String.valueOf(i + 2), "对象代码不存在！");
					excelValidList.add(excelValidDto);
				}
			}
		}
	}

	/**
	 * 根据资产代码获取债券
	 * @param list
	 * @return
	 */
	public List<ComMarketManagementDto> getFinprodLists(List<ComMarketManagementDto> list) {
		Map<String, Object> param = new HashMap<>();
		param.put("FINPROD_TYPE_F01", DictCodeListExt1.金融产品类型_债券.getValue());
		param.put("list", list);
		return restTemplate.postForList(
				ServiceApplication.VALUATION_BASICDATA_SERVICE_NAME + "/ComMarketManagement/getFinprodLists",
				param, ComMarketManagementDto.class);
	}

	/**
	 * 根据评级结构获取机构
	 * @param list
	 * @return
	 */
	public List<ComMarketManagementDto> getCustomerLists(List<ComMarketManagementDto> list) {
		Map<String, Object> param = new HashMap<>();
		param.put("list", list);
		return restTemplate.postForList(
				ServiceApplication.VALUATION_BASICDATA_SERVICE_NAME + "/ComMarketManagement/getCustomerLists",
				param, ComMarketManagementDto.class);
	}

	/**
	 * 导入数据
	 * @param list
	 * @return
	 */
	public Integer importDatas(List<ComMarketManagementDto> list) {
		return restTemplate.postForObject(
				ServiceApplication.VALUATION_BASICDATA_SERVICE_NAME + "/ComMarketManagement/importDatas",
				list, Integer.class);
	}

}
