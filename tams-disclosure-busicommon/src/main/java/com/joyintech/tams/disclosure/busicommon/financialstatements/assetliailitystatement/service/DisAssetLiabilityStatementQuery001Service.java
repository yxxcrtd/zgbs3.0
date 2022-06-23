/*
 * 文件名：AssetLiabilityStatementQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年9月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.disclosure.busicommon.financialstatements.assetliailitystatement.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.joyintech.framework.file.SafelyFile;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.disclosure.busicommon.common.constants.DictCode;
import com.joyintech.tams.disclosure.busicommon.common.constants.DictCodeList;
import com.joyintech.tams.disclosure.busicommon.common.constants.GlobalConstant;
import com.joyintech.tams.disclosure.busicommon.common.dao.DisValAssetBook001Mapper;
import com.joyintech.tams.disclosure.busicommon.common.dto.BokBalance;
import com.joyintech.tams.disclosure.busicommon.common.dto.ExportFileDto;
import com.joyintech.tams.disclosure.busicommon.financialstatements.assetliailitystatement.common.DisAssetLiabilityStatementEnum;
import com.joyintech.tams.disclosure.busicommon.financialstatements.assetliailitystatement.common.DisAssetLiabilityStatementUtil;
import com.joyintech.tams.disclosure.busicommon.financialstatements.assetliailitystatement.dao.DisAssetLiabilityStatementQuery001Mapper;
import com.joyintech.tams.disclosure.busicommon.financialstatements.assetliailitystatement.dto.DisAssetLiabilityStatementQuery001Dto;
import com.joyintech.tams.disclosure.busicommon.financialstatements.assetliailitystatement.dto.DisAssetLiabilityStatementQuery001SearchDto;
import com.joyintech.tams.disclosure.busicommon.financialstatements.valprofitquery.dto.DisValProfitQuery001Dto;
import com.joyintech.tams.disclosure.busicommon.financialstatements.valprofitquery.dto.DisProfitCalculationDto;
import com.joyintech.tools.util.ExcelTools;

/**
 * 资产负债表
 * 
 * @author zhangwei
 * @version 1.0 2018年9月25日
 * @see AssetLiabilityStatementQuery001Service
 * @since 1.0
 */
@Service
public class DisAssetLiabilityStatementQuery001Service extends CommonExportService<DisAssetLiabilityStatementQuery001Dto> {

	@Autowired
	DisAssetLiabilityStatementQuery001Mapper mapper;
	
    @Autowired
    private  DisValAssetBook001Mapper disValAssetBook001Mapper;
	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(DisAssetLiabilityStatementQuery001Service.class);
	
    /**
     * 针对weblogic部署无法获取模版问题
     */
    private static final String TMP_DIR = "/tpl/";

	/**
	 * 通用联动查询
	 * 
	 * @param param : dict_code_parent,dict_code_child,item_code_parent
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@DataSource(DataSourceType.OTHER)
	public List<SelectOptionDto> getLinkSelectOption(Map<String, Object> param) {
		return mapper.getLinkSelectOption(param);
	}

	/**
	 * 查询科目余额表
	 * 
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<DisAssetLiabilityStatementQuery001Dto> findBokBalance(DisAssetLiabilityStatementQuery001SearchDto dto)
			throws ParseException {
		List<DisAssetLiabilityStatementQuery001Dto> list = mapper.selectBokBalance(dto);
		return list;
	}
	
	/**
	 * 查询科目余额表
	 * 
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<DisAssetLiabilityStatementQuery001Dto> findBokBalanceAll(DisAssetLiabilityStatementQuery001SearchDto dto)
			throws ParseException {
		List<DisAssetLiabilityStatementQuery001Dto> list = mapper.selectBokBalanceAll(dto);
		return list;
	}

	/**
	 * 获得 资产报表数据（单账套）
	 * 
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<DisAssetLiabilityStatementQuery001Dto> findAssetsStatement(DisAssetLiabilityStatementQuery001SearchDto dto)
			throws ParseException {
		dto.setSubjectNoList(DisAssetLiabilityStatementEnum.valuesfromDictCode(DictCodeList.科目类别_资产类));
		dto.setBalanceDate(analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.ENDING_BALANCE));// 设置期末日期
		List<DisAssetLiabilityStatementQuery001Dto> endList = findBokBalance(dto); // 期末余额列表

		dto.setBalanceDate(analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.INITIAL_BALANCE));// 设置年初日期
		List<DisAssetLiabilityStatementQuery001Dto> initList = findBokBalance(dto); // 年初余额列表

		List<DisAssetLiabilityStatementQuery001Dto> dtoList = DisAssetLiabilityStatementUtil.getAssetList(); // 获得资产表样
		assembleDtoList(dtoList, endList, DisAssetLiabilityStatementUtil.ENDING_BALANCE, true);
		assembleDtoList(dtoList, initList, DisAssetLiabilityStatementUtil.INITIAL_BALANCE, true);
		return dtoList;
	}
	/**
	 * 获得 资产报表数据可配置（单账套）
	 * 
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<DisValProfitQuery001Dto> findAssetsStatementPz(DisAssetLiabilityStatementQuery001SearchDto dto)
			throws ParseException{
		dto.setBalanceDate(analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.ENDING_BALANCE));// 设置期末日期
		List<BokBalance>  listBokBalance = mapper.selectBalance(dto); // 期末余额列表
		// 查询表样
    	List<DisProfitCalculationDto> profitCalculationDtoList = mapper.selectProfitCalculationDtoList();
        Map<String, DisProfitCalculationDto> profitCalculationMap = profitCalculationDtoList.stream().collect(Collectors.toMap(DisProfitCalculationDto::getProjCode, Function.identity()));
        Map<String, DisValProfitQuery001Dto> dtoMap = initReportElement(profitCalculationMap);;
        //计算期末
        calValue(listBokBalance, profitCalculationMap, true);
        
        // 画面赋值
        setValue(profitCalculationMap, dtoMap);
        List<DisValProfitQuery001Dto> list = new ArrayList<DisValProfitQuery001Dto>();
        list.addAll(dtoMap.values());	
        list.sort(new Comparator<DisValProfitQuery001Dto>() {

            @Override
            public int compare(DisValProfitQuery001Dto arg0, DisValProfitQuery001Dto arg1) {
                if (arg0.getSortNum() < arg1.getSortNum()) {
                    return -1;
                }
                return 1;
            }
        });
        if(list.size()==0){
        	return null;
        }
		return list;
	}
	
	/**   add by  dongxiangyang   20200211 start*/
	public List<DisValProfitQuery001Dto> findAssetsStatementPzYth(DisAssetLiabilityStatementQuery001SearchDto dto)
			throws ParseException{
		dto.setBalanceDate(analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.ENDING_BALANCE));// 设置期末日期
		List<BokBalance>  listBokBalance = mapper.selectBalance(dto); // 期末余额列表
		// 查询表样
    	List<DisProfitCalculationDto> profitCalculationDtoList = mapper.selectProfitCalculationDtoListYth();
        Map<String, DisProfitCalculationDto> profitCalculationMap = profitCalculationDtoList.stream().collect(Collectors.toMap(DisProfitCalculationDto::getProjCode, Function.identity()));
        Map<String, DisValProfitQuery001Dto> dtoMap = initReportElement(profitCalculationMap);;
        //计算期末
        calValue(listBokBalance, profitCalculationMap, true);
        
        // 画面赋值
        setValue(profitCalculationMap, dtoMap);
        List<DisValProfitQuery001Dto> list = new ArrayList<DisValProfitQuery001Dto>();
        list.addAll(dtoMap.values());	
        list.sort(new Comparator<DisValProfitQuery001Dto>() {

            @Override
            public int compare(DisValProfitQuery001Dto arg0, DisValProfitQuery001Dto arg1) {
                if (arg0.getSortNum() < arg1.getSortNum()) {
                    return -1;
                }
                return 1;
            }
        });
        if(list.size()==0){
        	return null;
        }
		return list;
	}
	/**   add by  dongxiangyang   20200211 end*/

	/**
     * 将计算结果复制到返回画面结果集
     * 
     * @param profitCalculationMap
     * @param dtoMap
     */
    public void setValue(Map<String, DisProfitCalculationDto> profitCalculationMap,
			Map<String, DisValProfitQuery001Dto> dtoMap) {
		for (String key : profitCalculationMap.keySet()) {
			if (dtoMap.containsKey(profitCalculationMap.get(key).getProjName())) {
				dtoMap.get(profitCalculationMap.get(key).getProjName()).setThisTermAmt(profitCalculationMap.get(key).getThisTermAmt());
				dtoMap.get(profitCalculationMap.get(key).getProjName()).setLastTermAmt(profitCalculationMap.get(key).getLastTermAmt());
			}
		}
	}
	
	 /**
     * 初始化报表项
     * 
     * @param profitCalculationMap
     * @return
     */
    public Map<String, DisValProfitQuery001Dto> initReportElement(Map<String, DisProfitCalculationDto> profitCalculationMap) {
    	Map<String, DisValProfitQuery001Dto> retMap = new HashMap<String, DisValProfitQuery001Dto>();
    	DisValProfitQuery001Dto DisValProfitQuery001Dto = null; 
		for (String key : profitCalculationMap.keySet()) {
			DisProfitCalculationDto configDto = profitCalculationMap.get(key);
			// 是报表展示项
			if (DictCodeList.是否_是.getValue().equals(configDto.getShowFlg())) {
				if (!retMap.containsKey(configDto.getProjName())) {
					DisValProfitQuery001Dto = new DisValProfitQuery001Dto();
					DisValProfitQuery001Dto.setSortNum(configDto.getShowOrder());
					DisValProfitQuery001Dto.setProject(configDto.getProjName());
					DisValProfitQuery001Dto.setThisTermAmt(BigDecimal.ZERO);
					DisValProfitQuery001Dto.setLastTermAmt(BigDecimal.ZERO);
					retMap.put(configDto.getProjName(), DisValProfitQuery001Dto);
				}
			}
		}
		return retMap;
	}
	/**
     * 计算利润表各项目值
     * 
     * @param termData
     * @param profitCalculationMap
     * @param thisTermFlag
     */
	public void calValue(List<BokBalance> termData, Map<String, DisProfitCalculationDto> profitCalculationMap, boolean thisTermFlag) {
		// 汇总项key集合
		List<String> keyList = new ArrayList<String>();
		// 计算明细项目
		for (String key : profitCalculationMap.keySet()) {
			DisProfitCalculationDto dto = profitCalculationMap.get(key);
			if (1== dto.getProjLevel()) {
				// 汇总项，暂不计算。将key保存到汇总项key集合待用
				keyList.add(dto.getProjCode());
				continue;
			}
			// 计算当期值
			BigDecimal termAmt = calcTermAmt(termData, dto);
			if (thisTermFlag) {
				profitCalculationMap.get(key).setThisTermAmt(profitCalculationMap.get(key).getThisTermAmt().add(termAmt));
			} else {
				profitCalculationMap.get(key).setLastTermAmt(profitCalculationMap.get(key).getLastTermAmt().add(termAmt));
			}
		}
		// 计算汇总项目
		Collections.sort(keyList, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o2.length() - o1.length();
			}
			
		});
		for (String key : keyList) {
			// 计算汇总项数值
			calcTotalTermAmt(profitCalculationMap, key);
		}
		
	}
	
	/**
	 * 根据报表项目配置信息，计算项目值，目前计算公式只支持加减，且计算公式不能带左右括号
	 * @param termData
	 * @param dto
	 * @return
	 */
	private BigDecimal calcTermAmt(List<BokBalance> termData, DisProfitCalculationDto dto) {
		// 计算必要信息缺失，直接返回0
		if (termData == null || dto == null || termData.isEmpty() || 
				StringUtils.isEmpty(dto.getSubjectNo()) || StringUtils.isEmpty(dto.getFormula())) {
			return BigDecimal.ZERO;
		}
		BigDecimal termAmt = BigDecimal.ZERO;
		// 标点符号
		String regex = "\\p{Punct}";
		// 计算公式
		String formula = dto.getFormula();
		// 参与计算的字段
		String[] fieldArr = new String[]{formula};
		// 参与计算的运算符
		String punct = "";
		Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(formula);
        while(m.find()){  
        	punct = punct + m.group(0);
        }
        char[] punctArr = punct.toCharArray();
        try {
			for (BokBalance balance : termData) {
				if (balance.getSubjectNo().equals(dto.getSubjectNo())) {
					Method method = balance.getClass().getMethod("get" + fieldArr[0]);
			        Object obj = method.invoke(balance);
			        termAmt = termAmt.add((BigDecimal)(obj == null ? BigDecimal.ZERO : obj));
			        for (int i = 0; i < punctArr.length; i ++) {
			        	Method method1 = balance.getClass().getMethod("get" + fieldArr[i + 1]);
			            Object obj1 = method1.invoke(balance);
			            if ('+' == punctArr[i]) {
			            	termAmt = termAmt.add((BigDecimal)(obj1 == null ? BigDecimal.ZERO : obj1));
			            }
			            if ('-' == punctArr[i]) {
			            	termAmt = termAmt.subtract((BigDecimal)(obj1 == null ? BigDecimal.ZERO : obj1));
			            }
			        }
				}
			}
        } catch (Exception e) {
		}
		return termAmt;
	}
	/**
	 * 根据报表项目配置信息，计算汇总项目值，目前计算公式只支持加减，且计算公式不能带左右括号
	 * @param termData
	 * @param dto
	 * @return
	 */
	private void calcTotalTermAmt(Map<String, DisProfitCalculationDto> profitCalculationMap, String parentKey) {
		DisProfitCalculationDto dto = profitCalculationMap.get(parentKey);
		BigDecimal thisTermAmt = BigDecimal.ZERO;
		BigDecimal lastTermAmt = BigDecimal.ZERO;
		try {
			// 汇总项计算公式
			String formula = dto.getFormula();
			// 参与运算的子项key
			StringBuilder childKeyBuilder = new StringBuilder();
			// 参与计算的运算符
			StringBuilder punctBuilder = new StringBuilder();
			char[] arr = formula.toCharArray();
			for (int i = 0; i < arr.length; i++) {
				if ('+' == arr[i] || '-' == arr[i]) {
					childKeyBuilder.append('@');
					punctBuilder.append(arr[i]);
				} else {
					childKeyBuilder.append(arr[i]);
				}
			}
			
	        char[] punctArr = punctBuilder.toString().toCharArray();
	        String[] childKeyArr = childKeyBuilder.toString().split("@");
	        thisTermAmt = profitCalculationMap.get(childKeyArr[0]).getThisTermAmt();
	        lastTermAmt = profitCalculationMap.get(childKeyArr[0]).getLastTermAmt();
	        for (int i = 0; i < punctArr.length; i ++) {
	        	if ('+' == punctArr[i]) {
	        		thisTermAmt = thisTermAmt.add(profitCalculationMap.get(childKeyArr[i + 1]).getThisTermAmt());
	        		lastTermAmt = lastTermAmt.add(profitCalculationMap.get(childKeyArr[i + 1]).getLastTermAmt());
	        	}
	        	if ('-' == punctArr[i]) {
	        		thisTermAmt = thisTermAmt.subtract(profitCalculationMap.get(childKeyArr[i + 1]).getThisTermAmt());
	        		lastTermAmt = lastTermAmt.subtract(profitCalculationMap.get(childKeyArr[i + 1]).getLastTermAmt());
	        	}
	        }
			profitCalculationMap.get(parentKey).setThisTermAmt(thisTermAmt);
			profitCalculationMap.get(parentKey).setLastTermAmt(lastTermAmt);
		} catch (Exception e) {
		}
	}
	
	/**
	 * 获得 资产报表数据（单账套）
	 * 
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<DisAssetLiabilityStatementQuery001Dto> findAssetsStatementJasper(DisAssetLiabilityStatementQuery001SearchDto dto)
			throws ParseException{
		dto.setSubjectNoList(DisAssetLiabilityStatementEnum.valuesfromDictCode(DictCodeList.科目类别_资产类));
		dto.setBalanceDate(analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.ENDING_BALANCE));// 设置期末日期
		List<DisAssetLiabilityStatementQuery001Dto> endList = findBokBalance(dto); // 期末余额列表

		dto.setBalanceDate(analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.INITIAL_BALANCE));// 设置年初日期
		List<DisAssetLiabilityStatementQuery001Dto> initList = findBokBalance(dto); // 年初余额列表
        return  null;
	}
	
	/**
	 * 获得 资产报表数据（全账套）
	 * 
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<DisAssetLiabilityStatementQuery001Dto> findAssetsStatementAll(DisAssetLiabilityStatementQuery001SearchDto dto)
			throws ParseException {
		dto.setSubjectNoList(DisAssetLiabilityStatementEnum.valuesfromDictCode(DictCodeList.科目类别_资产类));
		dto.setBalanceDate(analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.ENDING_BALANCE));// 设置期末日期
		List<DisAssetLiabilityStatementQuery001Dto> endList = findBokBalanceAll(dto); // 期末余额列表

		dto.setBalanceDate(analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.INITIAL_BALANCE));// 设置年初日期
		List<DisAssetLiabilityStatementQuery001Dto> initList = findBokBalanceAll(dto); // 年初余额列表

		List<DisAssetLiabilityStatementQuery001Dto> dtoList = DisAssetLiabilityStatementUtil.getAssetList(); // 获得资产表样
		assembleDtoList(dtoList, endList, DisAssetLiabilityStatementUtil.ENDING_BALANCE, true);
		assembleDtoList(dtoList, initList, DisAssetLiabilityStatementUtil.INITIAL_BALANCE, true);
		return dtoList;
	}
	
	/**
     * 处理样式
     * @param list
     * @param style
     * @param reportDimen 报表维度
     * @see
     * @since 1.0
     */
    public void dealStyle(List<DisAssetLiabilityStatementQuery001Dto> list) {
        for (DisAssetLiabilityStatementQuery001Dto dto : list) {
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
	public List<DisAssetLiabilityStatementQuery001Dto> findLiabilityInterestStatement(
			DisAssetLiabilityStatementQuery001SearchDto dto) throws ParseException {
		dto.setSubjectNoList(
				DisAssetLiabilityStatementEnum.valuesfromDictCode(DictCodeList.科目类别_负债类, DictCodeList.科目类别_所有者权益类));
		dto.setBalanceDate(analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.ENDING_BALANCE));// 设置期末日期
		List<DisAssetLiabilityStatementQuery001Dto> endList = findBokBalance(dto); // 期末余额列表

		dto.setBalanceDate(analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.INITIAL_BALANCE));// 设置年初日期
		List<DisAssetLiabilityStatementQuery001Dto> initList = findBokBalance(dto); // 年初余额列表

			List<DisAssetLiabilityStatementQuery001Dto> dtoList = DisAssetLiabilityStatementUtil.getLiabilityInterestList(); // 获得负债和所有者权益报表表样
			assembleDtoList(dtoList, endList, DisAssetLiabilityStatementUtil.ENDING_BALANCE, false);
			assembleDtoList(dtoList, initList, DisAssetLiabilityStatementUtil.INITIAL_BALANCE, false);
			assembleLiaIntSum(dtoList);
			return dtoList;
	}
	
	/**
	 * 获得 负债和所有者权益报表数据（全账套）
	 * 
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<DisAssetLiabilityStatementQuery001Dto> findLiabilityInterestStatementAll(
			DisAssetLiabilityStatementQuery001SearchDto dto) throws ParseException {
		dto.setSubjectNoList(
				DisAssetLiabilityStatementEnum.valuesfromDictCode(DictCodeList.科目类别_负债类, DictCodeList.科目类别_所有者权益类));
		dto.setBalanceDate(analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.ENDING_BALANCE));// 设置期末日期
		List<DisAssetLiabilityStatementQuery001Dto> endList = findBokBalanceAll(dto); // 期末余额列表

		dto.setBalanceDate(analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.INITIAL_BALANCE));// 设置年初日期
		List<DisAssetLiabilityStatementQuery001Dto> initList = findBokBalanceAll(dto); // 年初余额列表

		List<DisAssetLiabilityStatementQuery001Dto> dtoList = DisAssetLiabilityStatementUtil.getLiabilityInterestList(); // 获得负债和所有者权益报表表样
		assembleDtoList(dtoList, endList, DisAssetLiabilityStatementUtil.ENDING_BALANCE, false);
		assembleDtoList(dtoList, initList, DisAssetLiabilityStatementUtil.INITIAL_BALANCE, false);
		assembleLiaIntSum(dtoList);
		return dtoList;
	}

	/**
	 * 分析余额日期
	 * 
	 * @param dto         查询条件
	 * @param balanceType 期末、年初
	 * @return
	 * @throws ParseException
	 */
	public Date analyseBalanceDate(DisAssetLiabilityStatementQuery001SearchDto dto, String balanceType)
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

		if (DisAssetLiabilityStatementUtil.ENDING_BALANCE.equals(balanceType)) {// 期末
			calendar.set(year, month, day);
			return calendar.getTime();
		} else {// 年初
			calendar.set(year - 1, 11, 31);
			return calendar.getTime();
		}
	}

	/**
	 * 组装 DTOList
	 * 
	 * @param dtoList     表样
	 * @param list        结果集
	 * @param balanceType 操作余额类型
	 * @param assetFlag 
	 * @return
	 */
	private void assembleDtoList(List<DisAssetLiabilityStatementQuery001Dto> dtoList,
			List<DisAssetLiabilityStatementQuery001Dto> list, String balanceType, boolean assetFlag) {
		for (DisAssetLiabilityStatementQuery001Dto dto : dtoList) {
			List<DisAssetLiabilityStatementEnum> subjectTypeList = dto.getSubjectTypeList();
			for (DisAssetLiabilityStatementQuery001Dto item : list) {
				List<DisAssetLiabilityStatementEnum> enumList = DisAssetLiabilityStatementEnum
						.enumsfromValue(item.getSubjectNo());
				enumList.retainAll(subjectTypeList);
				if (enumList.size() > 0) {
					setInitOrEnd(dto, item, balanceType, assetFlag);
					dto.setSubjectNo(item.getSubjectNo());
				}
			}
		}
	}

	/**
	 * 资产总计和所有者权益总计
	 * 
	 * @return
	 */
	private void assembleLiaIntSum(List<DisAssetLiabilityStatementQuery001Dto> dtoList) {
		BigDecimal liabilityEndSum = BigDecimal.ZERO;
		BigDecimal liabilityInitSum = BigDecimal.ZERO;
		BigDecimal interestEndSum = BigDecimal.ZERO;
		BigDecimal interestInitSum = BigDecimal.ZERO;
		List<String> liabilityList = DisAssetLiabilityStatementEnum.valuesfromDictCode(DictCodeList.科目类别_负债类);
		List<String> interestList = DisAssetLiabilityStatementEnum.valuesfromDictCode(DictCodeList.科目类别_所有者权益类);
		for (DisAssetLiabilityStatementQuery001Dto dto : dtoList) {
			if (liabilityList.contains(dto.getSubjectNo())) {
				liabilityEndSum = liabilityEndSum.add(dto.getEndingBalance());
				liabilityInitSum = liabilityInitSum.add(dto.getInitialBalance());
			}
			if (DisAssetLiabilityStatementUtil.LIABILITY_SUM.equals(dto.getItemName())) {
				dto.setEndingBalance(liabilityEndSum);
				dto.setInitialBalance(liabilityInitSum);
			}
			if (interestList.contains(dto.getSubjectNo())) {
				interestEndSum = interestEndSum.add(dto.getEndingBalance());
				interestInitSum = interestInitSum.add(dto.getInitialBalance());
			}
			if (DisAssetLiabilityStatementUtil.INTEREST_SUM.equals(dto.getItemName())) {
				dto.setEndingBalance(interestEndSum);
				dto.setInitialBalance(interestInitSum);
			}
		}
	}

	/**
	 * 设置年初或期末余额
	 * 
	 * @param dto         容器
	 * @param item        结果项
	 * @param balanceType 操作余额类型
	 * @param assetFlag 
	 * @param             dto.balFlag 期待方向
	 * @param amtType     使用金额类型
	 * 
	 */
	private void setInitOrEnd(DisAssetLiabilityStatementQuery001Dto dto, DisAssetLiabilityStatementQuery001Dto item,
			String balanceType, boolean assetFlag) {
		String amtType = dto.getUseAmtType();
		boolean specialFlag = false; // 特殊处理 表示
		if (DisAssetLiabilityStatementUtil.ENDING_BALANCE.equals(balanceType)) {

			if (DisAssetLiabilityStatementUtil.O_AMT.equals(amtType)) {
				// 应付账款: 设计变更，特殊处理。（单个项目下包含多子科目时，存在一个使用金额与其他不同）
				if (DisAssetLiabilityStatementEnum.资产类_其他资产_应付账款.getValue().equals(item.getSubjectNo())
						|| DisAssetLiabilityStatementEnum.负债类_其他负债_应付账款.getValue().equals(item.getSubjectNo())) {
					specialFlag = true;
					if (DictCodeList.余额方向_借方.getValue().equals(dto.getBalFlag())) {
						item.setEndingBalance(item.getoDAmt());
					}
					if (DictCodeList.余额方向_贷方.getValue().equals(dto.getBalFlag())) {
						item.setEndingBalance(item.getoCAmt());
					}
				} else {
					item.setEndingBalance(item.getoAmt());
				}
			}
			
			// 资产、负债特殊项处理
			if (assetFlag) {
				if (inSpecialSubject(item.getSubjectNo())) {
					if (DictCodeList.余额方向_借方.getValue().equals(item.getBalFlag())) {
						item.setEndingBalance(item.getoAmt());
					} else {
						item.setEndingBalance(BigDecimal.ZERO);
					}
				}
			} else {
				if (inSpecialSubject(item.getSubjectNo())) {
					if (DictCodeList.余额方向_贷方.getValue().equals(item.getBalFlag())) {
						item.setEndingBalance(item.getoAmt());
					} else {
						item.setEndingBalance(BigDecimal.ZERO);
					}
				}
			}
			if (!StringUtils.equals(item.getBalFlag(), dto.getBalFlag()) && 
					DisAssetLiabilityStatementUtil.O_AMT.equals(amtType) && 
						!specialFlag && !inSpecialSubject(item.getSubjectNo())) {// 实际余额方向与期望不同时 *-1
																				// (仅当取值为原币余额时)
				dto.setEndingBalance(dto.getEndingBalance().add(item.getEndingBalance().negate()));
			} else {
				dto.setEndingBalance(dto.getEndingBalance().add(item.getEndingBalance()));
			}
		} else {
			if (DisAssetLiabilityStatementUtil.O_AMT.equals(amtType)) {
				// 应付账款: 设计变更，特殊处理。（单个项目下包含多子科目时，存在一个使用金额与其他不同）
				if (DisAssetLiabilityStatementEnum.资产类_其他资产_应付账款.getValue().equals(item.getSubjectNo())
						|| DisAssetLiabilityStatementEnum.负债类_其他负债_应付账款.getValue().equals(item.getSubjectNo())) {
					specialFlag = true;
					if (DictCodeList.余额方向_借方.getValue().equals(dto.getBalFlag())) {
						item.setInitialBalance(item.getoDAmt());
					}
					if (DictCodeList.余额方向_贷方.getValue().equals(dto.getBalFlag())) {
						item.setInitialBalance(item.getoCAmt());
					}
				} else {
					item.setInitialBalance(item.getoAmt());
				}
			}
			
			// 资产、负债特殊项处理
			if (assetFlag) {
				if (inSpecialSubject(item.getSubjectNo())) {
					if (DictCodeList.余额方向_借方.getValue().equals(item.getBalFlag())) {
						item.setInitialBalance(item.getoAmt());
					} else {
						item.setInitialBalance(BigDecimal.ZERO);
					}
				}
			} else {
				if (inSpecialSubject(item.getSubjectNo())) {
					if (DictCodeList.余额方向_贷方.getValue().equals(item.getBalFlag())) {
						item.setInitialBalance(item.getoAmt());
					} else {
						item.setInitialBalance(BigDecimal.ZERO);
					}
				}
			}

			if (!StringUtils.equals(dto.getBalFlag(), item.getBalFlag()) && 
					DisAssetLiabilityStatementUtil.O_AMT.equals(amtType) && 
						!specialFlag && !inSpecialSubject(item.getSubjectNo())) {// 实际余额方向与期望不同时 *-1
																				// (仅当取值为原币余额时)
				dto.setInitialBalance(dto.getInitialBalance().add(item.getInitialBalance().negate()));
			} else {
				dto.setInitialBalance(dto.getInitialBalance().add(item.getInitialBalance()));
			}
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
	public ResponseEntity<byte[]> exportExcel(DisAssetLiabilityStatementQuery001SearchDto dto)
			throws ParseException, IOException {
		ExportFileDto exportFileDto = createExcel(dto);
		File file = new SafelyFile(exportFileDto.getFilePath());
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
	public ExportFileDto createExcel(DisAssetLiabilityStatementQuery001SearchDto dto) throws ParseException, IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date reportDate = analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.ENDING_BALANCE);
		List<DisAssetLiabilityStatementQuery001Dto> assetList = new ArrayList<DisAssetLiabilityStatementQuery001Dto>();
		String prefix = "";
		if (DictCodeListExt1.报表维度_单账套.getValue().equals(dto.getReportDimen())) {
			assetList = findAssetsStatement(dto);
			prefix = dto.getBooksetId();
		} else {
			assetList = findAssetsStatementAll(dto);
			prefix = "全账套";
		}
		if (null!=assetList) {// 设置总计
			assetList.addAll(DisAssetLiabilityStatementUtil.getAssetListSum(assetList));
		}
		List<DisAssetLiabilityStatementQuery001Dto> liabilityInterestList = new ArrayList<DisAssetLiabilityStatementQuery001Dto>();
		if (DictCodeListExt1.报表维度_单账套.getValue().equals(dto.getReportDimen())) {
			liabilityInterestList = findLiabilityInterestStatement(dto);
		} else {
			liabilityInterestList = findLiabilityInterestStatementAll(dto);
		}
		if (null!=liabilityInterestList) {// 设置总计
			assembleLiaIntSum(liabilityInterestList);
			liabilityInterestList
					.addAll(DisAssetLiabilityStatementUtil.getLiabilityInterestListSum(liabilityInterestList));
		}
		// 合并List
		List<DisAssetLiabilityStatementQuery001Dto> mergeList = mergeList(assetList, liabilityInterestList);
		
		// 解决报表数据为空导出模板公式的问题
		if (mergeList.isEmpty()) {
			DisAssetLiabilityStatementQuery001Dto emptyDto = new DisAssetLiabilityStatementQuery001Dto();
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
		//map.put("org",CacheUtil.getSysParam(GlobalConstant.财务报表_编制单位));
		 //多级法人改造start add by hxy 20200619
        if(MultilevelDataAuthorityUtil.isEnabled()) {
        	 //联社模式,查询登录人的机构
        	/*String org =   MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
            String orgName=CacheUtil.getAppCacheInstitutionCodeName(org).toString();
        	map.put("org", orgName);*/
        	 //联社模式
       	   //根据选择的账套组或者账套，查询出它们的所属机构
	       	String booksetOrg;
	       	if ("02".equals(dto.getReportDimen())) {
	       		if(null!=dto && null!=dto.getAccountList() && dto.getAccountList().size()>0){
	       			booksetOrg=dto.getAccountList().get(0).getCode();
	           		String orgName=disValAssetBook001Mapper.selectAssociationModeOrgName(booksetOrg);
	               	map.put("org", orgName);
	       		}
	       	}else{
	       		booksetOrg=dto.getBooksetId();
	       		String orgName=disValAssetBook001Mapper.selectAssociationModeOrgName(booksetOrg);
	           	map.put("org", orgName);
	       	}
        }else{
        	//银行模式
        	String orgName=disValAssetBook001Mapper.selectBankModeOrgName();
        	map.put("org", orgName);
        	
        }
        //多级法人改造end add by hxy 20200619
		map.put("mergeList", mergeList);
		map.put("statementDate", sdf.format(reportDate));
		map.put("statementType", SysDataUtil.getNameFromDataDict(DictCode.报表期限类型, dto.getReportTermType()));
		exportFileDto.setFileName(fileName);
		exportFileDto.setFilePath(outPath);
		try {
		    
            //针对weblogic环境部署，FileNameUtil.getTplFile无法获取tpl文件夹下模版。
            File templeteFile = new SafelyFile(tplPath);
            //文件存在情况,直接该路径获取文件流
            if (templeteFile.exists()) {
                stream = new FileInputStream(templeteFile);
            }
            //文件不存在情况，即weblogic部署情况，需要换方式获取文件流。
            else {
                stream = this.getClass().getResourceAsStream(TMP_DIR + tplFlodPath);
            }
			
			ExcelTools.writeExcel(stream, tempName, map, outPath);
		} catch (IOException e) {
			logger.error("",e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					logger.error("",e);
				}
			}
		}
		return exportFileDto;
	}

	/**
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @param assetList
	 * @param liabilityInterestList
	 * @return
	 * @see
	 * @since 1.0
	 */
	private List<DisAssetLiabilityStatementQuery001Dto> mergeList(List<DisAssetLiabilityStatementQuery001Dto> assetList,
			List<DisAssetLiabilityStatementQuery001Dto> liabilityInterestList) {
		boolean assetMax = false;
		List<DisAssetLiabilityStatementQuery001Dto> maxList = null;
		List<DisAssetLiabilityStatementQuery001Dto> minList = null;
		List<DisAssetLiabilityStatementQuery001Dto> mergeList = new ArrayList<DisAssetLiabilityStatementQuery001Dto>();
		if (assetList.size() > liabilityInterestList.size()) {
			maxList = assetList;
			minList = liabilityInterestList;
			assetMax = true;
		} else {
			maxList = liabilityInterestList;
			minList = assetList;
		}
		DisAssetLiabilityStatementQuery001Dto dto = null;
		DisAssetLiabilityStatementQuery001Dto minLastDto = null;
		for (int i = 0; i < maxList.size(); i ++) {
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
				if (minLastDto!=null && assetMax) {
					dto.setInitialBalance1(minLastDto.getInitialBalance());
					dto.setEndingBalance1(minLastDto.getEndingBalance());
					dto.setItemName1(minLastDto.getItemName());
				} else {
					dto.setInitialBalance1(dto.getInitialBalance());
					dto.setEndingBalance1(dto.getEndingBalance());
					dto.setItemName1(dto.getItemName());

					if(minLastDto!=null)
					{
						dto.setInitialBalance(minLastDto.getInitialBalance());
						dto.setEndingBalance(minLastDto.getEndingBalance());
						dto.setItemName(minLastDto.getItemName());
					}

				}
			}
			mergeList.add(dto);
		}
		return mergeList;
	}

	private boolean inSpecialSubject(String subjectNo) {
		return DisAssetLiabilityStatementEnum.所有者权益类_未分配利润_管理人报酬.getValue().equals(subjectNo) ||
					DisAssetLiabilityStatementEnum.所有者权益类_未分配利润_托管费.getValue().equals(subjectNo) ||
						DisAssetLiabilityStatementEnum.所有者权益类_未分配利润_销售服务费.getValue().equals(subjectNo) || 
									DisAssetLiabilityStatementEnum.所有者权益类_未分配利润_交易费用.getValue().equals(subjectNo) ||
										DisAssetLiabilityStatementEnum.所有者权益类_未分配利润_利息支出.getValue().equals(subjectNo)||
										DisAssetLiabilityStatementEnum.所有者权益类_未分配利润_资产损失.getValue().equals(subjectNo) ||
										DisAssetLiabilityStatementEnum.所有者权益类_未分配利润_其他费用.getValue().equals(subjectNo)||
										DisAssetLiabilityStatementEnum.所有者权益类_未分配利润_以前年度损益调整.getValue().equals(subjectNo)||
										DisAssetLiabilityStatementEnum.所有者权益类_未分配利润_税金及附加.getValue().equals(subjectNo)||
										DisAssetLiabilityStatementEnum.资产类_可供出售国家债券投资减值准备.getValue().equals(subjectNo)||
										DisAssetLiabilityStatementEnum.资产类_可供出售中央银行债券投资减值准备.getValue().equals(subjectNo)||
										DisAssetLiabilityStatementEnum.资产类_可供出售政策性银行债券投资减值准备.getValue().equals(subjectNo)||
										DisAssetLiabilityStatementEnum.资产类_可供出售其他金融债券投资减值准备.getValue().equals(subjectNo)||
										DisAssetLiabilityStatementEnum.资产类_可供出售其他债券减值准备.getValue().equals(subjectNo)||
										DisAssetLiabilityStatementEnum.资产类_可供出售股权投资减值准备.getValue().equals(subjectNo)||
										DisAssetLiabilityStatementEnum.资产类_可供出售非标准化债权投资减值准备.getValue().equals(subjectNo)||
										DisAssetLiabilityStatementEnum.资产类_可供出售其他金融资产减值准备.getValue().equals(subjectNo)
										;
	}
}
