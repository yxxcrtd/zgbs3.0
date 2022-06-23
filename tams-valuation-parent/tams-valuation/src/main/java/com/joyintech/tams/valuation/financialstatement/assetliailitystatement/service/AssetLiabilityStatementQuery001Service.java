/*
 * 文件名：AssetLiabilityStatementQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年9月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.financialstatement.assetliailitystatement.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.joyintech.framework.file.SafelyFile;
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
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.accounadjustmanage.valuationtablegeneration.dto.ExportFileDto;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.dao.BooksetComMapper;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatement.common.AssetLiabilityStatementEnum;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatement.common.AssetLiabilityStatementUtil;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatement.dao.AssetLiabilityStatementQuery001Mapper;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatement.dto.AssetLiabilityStatementQuery001Dto;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatement.dto.AssetLiabilityStatementQuery001SearchDto;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatement.dto.AssetLiabilityStatementQueryDto;
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
public class AssetLiabilityStatementQuery001Service extends CommonExportService<AssetLiabilityStatementQuery001Dto> {

	@Autowired
	AssetLiabilityStatementQuery001Mapper mapper;
    @Autowired
    private BooksetComMapper  booksetComMapper;
	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(AssetLiabilityStatementQuery001Service.class);

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
	public List<AssetLiabilityStatementQueryDto> findBokBalance(AssetLiabilityStatementQuery001SearchDto dto) {
		return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/AssetLiabilityStatementQuery001/findBokBalance",
                dto, AssetLiabilityStatementQueryDto.class);

	}

	/**
	 * 查询科目余额表
	 *
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<AssetLiabilityStatementQueryDto> findBokBalanceAll(AssetLiabilityStatementQuery001SearchDto dto) {
		return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/AssetLiabilityStatementQuery001/findBokBalanceAll",
                dto, AssetLiabilityStatementQueryDto.class);
	}

	/**
	 * 获得 资产报表数据（单账套）
	 *
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<AssetLiabilityStatementQuery001Dto> findAssetsStatement(AssetLiabilityStatementQuery001SearchDto dto)
			throws ParseException {
	    /**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 start**/
	    String finType = CacheUtil.getSysParam("VAT_FIN_TYPE") == null ? 
	        AssetLiabilityStatementUtil.FIN_TYPE_01 : CacheUtil.getSysParam("VAT_FIN_TYPE");//查询系统参数财务四表模板类型
	    dto.setSubjectNoList(AssetLiabilityStatementEnum.valuesfromDictCode(finType, DictCodeList.科目类型_资产类));
		/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 end**/
		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementUtil.ENDING_BALANCE));// 设置期末日期
		List<AssetLiabilityStatementQueryDto> endList = findBokBalance(dto); // 期末余额列表
		List<AssetLiabilityStatementQuery001Dto> endListdto  =new ArrayList<>();
		for(AssetLiabilityStatementQueryDto item:endList){
			AssetLiabilityStatementQuery001Dto dtoend =new AssetLiabilityStatementQuery001Dto();
			BeanUtil.copyPropertiesIgnoreNull(dtoend, item);
			endListdto.add(dtoend);

		}


		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementUtil.INITIAL_BALANCE));// 设置年初日期
		List<AssetLiabilityStatementQueryDto> initList = findBokBalance(dto); // 年初余额列表
		List<AssetLiabilityStatementQuery001Dto> initListdto  =new ArrayList<>();
		for(AssetLiabilityStatementQueryDto item:initList){
			AssetLiabilityStatementQuery001Dto dtoinit =new AssetLiabilityStatementQuery001Dto();
			BeanUtil.copyPropertiesIgnoreNull(dtoinit, item);
			initListdto.add(dtoinit);

		}
		/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 start**/
		List<AssetLiabilityStatementQuery001Dto> dtoList = AssetLiabilityStatementUtil.getAssetList(finType); // 获得资产表样
		assembleDtoList(dtoList, endListdto, AssetLiabilityStatementUtil.ENDING_BALANCE, finType);
		assembleDtoList(dtoList, initListdto, AssetLiabilityStatementUtil.INITIAL_BALANCE, finType);
		/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 end**/
		return dtoList;
	}

	/**
	 * 获得 资产报表数据（全账套）
	 *
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<AssetLiabilityStatementQuery001Dto> findAssetsStatementAll(AssetLiabilityStatementQuery001SearchDto dto)
			throws ParseException {
	    /**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 start**/
        String finType = CacheUtil.getSysParam("VAT_FIN_TYPE") == null ? 
            AssetLiabilityStatementUtil.FIN_TYPE_01 : CacheUtil.getSysParam("VAT_FIN_TYPE");//查询系统参数财务四表模板类型
		dto.setSubjectNoList(AssetLiabilityStatementEnum.valuesfromDictCode(finType, DictCodeList.科目类型_资产类));
		/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 end**/
		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementUtil.ENDING_BALANCE));// 设置期末日期
		List<AssetLiabilityStatementQueryDto> endList = findBokBalanceAll(dto); // 期末余额列表
		List<AssetLiabilityStatementQuery001Dto> endListdto  =new ArrayList<>();
		for(AssetLiabilityStatementQueryDto item:endList){
			AssetLiabilityStatementQuery001Dto dtoend =new AssetLiabilityStatementQuery001Dto();
			BeanUtil.copyPropertiesIgnoreNull(dtoend, item);
			endListdto.add(dtoend);

		}
		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementUtil.INITIAL_BALANCE));// 设置年初日期
		List<AssetLiabilityStatementQueryDto> initList = findBokBalanceAll(dto); // 年初余额列表
		List<AssetLiabilityStatementQuery001Dto> initListdto  =new ArrayList<>();
		for(AssetLiabilityStatementQueryDto item:initList){
			AssetLiabilityStatementQuery001Dto dtoinit =new AssetLiabilityStatementQuery001Dto();
			BeanUtil.copyPropertiesIgnoreNull(dtoinit, item);
			initListdto.add(dtoinit);

		}
		/**modify by yanya on 20190927 增加根据财务四表模板类型查询配置 start**/
		List<AssetLiabilityStatementQuery001Dto> dtoList = AssetLiabilityStatementUtil.getAssetList(finType); // 获得资产表样
		assembleDtoList(dtoList, endListdto, AssetLiabilityStatementUtil.ENDING_BALANCE, finType);
		assembleDtoList(dtoList, initListdto, AssetLiabilityStatementUtil.INITIAL_BALANCE, finType);
		/**modify by yanya on 20190927 增加根据财务四表模板类型查询配置 end**/
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
    public void dealStyle(List<AssetLiabilityStatementQuery001Dto> list) {
        for (AssetLiabilityStatementQuery001Dto dto : list) {
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
	public List<AssetLiabilityStatementQuery001Dto> findLiabilityInterestStatement(
			AssetLiabilityStatementQuery001SearchDto dto) throws ParseException {
	    /**modify by yanya on 20190927 增加根据财务四表模板类型查询配置 start**/
	    String finType = CacheUtil.getSysParam("VAT_FIN_TYPE") == null ? 
            AssetLiabilityStatementUtil.FIN_TYPE_01 : CacheUtil.getSysParam("VAT_FIN_TYPE");//查询系统参数财务四表模板类型
		dto.setSubjectNoList(
				AssetLiabilityStatementEnum.valuesfromDictCode(finType, DictCodeList.科目类型_负债类, DictCodeList.科目类型_所有者权益类));
		/**modify by yanya on 20190927 增加根据财务四表模板类型查询配置 end**/
		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementUtil.ENDING_BALANCE));// 设置期末日期
		List<AssetLiabilityStatementQueryDto> endList = findBokBalance(dto); // 期末余额列表
		List<AssetLiabilityStatementQuery001Dto> endListdto  =new ArrayList<>();
		for(AssetLiabilityStatementQueryDto item:endList){
			AssetLiabilityStatementQuery001Dto dtoend =new AssetLiabilityStatementQuery001Dto();
			BeanUtil.copyPropertiesIgnoreNull(dtoend, item);
			endListdto.add(dtoend);

		}
		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementUtil.INITIAL_BALANCE));// 设置年初日期
		List<AssetLiabilityStatementQueryDto> initList = findBokBalance(dto); // 年初余额列表
		List<AssetLiabilityStatementQuery001Dto> initListdto  =new ArrayList<>();
		for(AssetLiabilityStatementQueryDto item:initList){
			AssetLiabilityStatementQuery001Dto dtoinit =new AssetLiabilityStatementQuery001Dto();
			BeanUtil.copyPropertiesIgnoreNull(dtoinit, item);
			initListdto.add(dtoinit);

		}
		/**modify by yanya on 20190927 增加根据财务四表模板类型查询配置 start**/
        List<AssetLiabilityStatementQuery001Dto> dtoList = AssetLiabilityStatementUtil.getLiabilityInterestList(finType); // 获得负债和所有者权益报表表样
        /**modify by yanya on 20190927 增加根据财务四表模板类型查询配置 end**/
        // 20200417 吉林银行修改-注释4003科目的特殊处理 wangwei start
        // 处理特殊科目
//        endListdto = dealSpeSubject(endListdto);
//        initListdto = dealSpeSubject(initListdto);
		// 20200417 吉林银行修改-注释4003科目的特殊处理 wangwei end
        
        /**modify by yanya on 20190927 增加根据财务四表模板类型查询配置 start**/
        assembleDtoList(dtoList, endListdto, AssetLiabilityStatementUtil.ENDING_BALANCE, finType);
        assembleDtoList(dtoList, initListdto, AssetLiabilityStatementUtil.INITIAL_BALANCE, finType);
        /**modify by yanya on 20190927 增加根据财务四表模板类型查询配置 end**/
        assembleLiaIntSum(dtoList);
        return dtoList;
	}


    /**
     * 处理特殊科目 - （以账套维度处理，兼容多账套处理）
     * @param endList 从 DB 查到的数据
     * @return 根据需求处理后数据
     */
    private List<AssetLiabilityStatementQuery001Dto> dealSpeSubject(List<AssetLiabilityStatementQuery001Dto> listDB) {
        List<AssetLiabilityStatementQuery001Dto> listResult = Lists.newArrayList();

        Map<String, List<AssetLiabilityStatementQuery001Dto>> map = listDB.stream().collect(Collectors.groupingBy(AssetLiabilityStatementQuery001Dto::getBooksetId));
        for (String key : map.keySet()) {
            List<AssetLiabilityStatementQuery001Dto> list = map.get(key);
            listResult.addAll(list);

            // 账套.核算方式=“预期收益型”时，对其他综合收益进行特殊处理 start
            if (!DictCodeList.收益类型_收益型.getValue().equals(list.get(0).getAcctMode())){
                continue;
            }
            List<AssetLiabilityStatementQuery001Dto> list4003 = list.stream()
                .filter(item -> AssetLiabilityStatementEnum.所有者权益类_其他综合收益.getValue().equals(item.getSubjectNo()))
                .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(list4003)) {
                List<AssetLiabilityStatementQuery001Dto> listD4003 = copyList(list4003);
                for (AssetLiabilityStatementQuery001Dto item : listD4003) {
                    item.setSubjectNo(AssetLiabilityStatementEnum.特殊处理D_所有者权益类_其他综合收益.getValue());
                    if (DictCodeList.余额方向_贷方.getValue().equals(item.getBalFlag())){
                        // 因为是相减，这里取反
                        item.setbAmt(item.getbAmt().negate());
                    }
                }
                // 在结果集中手动添加特殊科目
                listResult.addAll(listD4003);

                List<AssetLiabilityStatementQuery001Dto> listC4003 = copyList(list4003);
                for (AssetLiabilityStatementQuery001Dto item : listC4003) {
                    item.setSubjectNo(AssetLiabilityStatementEnum.特殊处理C_所有者权益类_其他综合收益.getValue());
                }
                // 在结果集中手动添加特殊科目
                listResult.addAll(listC4003);
            }
            // 账套.核算方式=“预期收益型”时，对其他综合收益进行特殊处理 end
        }

        return listResult;
    }

    /**
     * 拷贝 list
     * @param list4003
     * @return
     */
    private <T> List<AssetLiabilityStatementQuery001Dto> copyList(List<T> listOrig) {
        List<AssetLiabilityStatementQuery001Dto> listDest = Lists.newArrayList();
        for (T item : listOrig) {
            AssetLiabilityStatementQuery001Dto dest = new AssetLiabilityStatementQuery001Dto();
            BeanUtil.copyPropertiesIgnoreNull(dest, item);
            listDest.add(dest);
        }
        return listDest;
    }

    /**
	 * 获得 负债和所有者权益报表数据（全账套）
	 *
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<AssetLiabilityStatementQuery001Dto> findLiabilityInterestStatementAll(
			AssetLiabilityStatementQuery001SearchDto dto) throws ParseException {
	    /**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 start**/
        String finType = CacheUtil.getSysParam("VAT_FIN_TYPE") == null ? 
            AssetLiabilityStatementUtil.FIN_TYPE_01 : CacheUtil.getSysParam("VAT_FIN_TYPE");//查询系统参数财务四表模板类型
		dto.setSubjectNoList(
				AssetLiabilityStatementEnum.valuesfromDictCode(finType, DictCodeList.科目类型_负债类, DictCodeList.科目类型_所有者权益类));
		/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 end**/
		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementUtil.ENDING_BALANCE));// 设置期末日期
		List<AssetLiabilityStatementQueryDto> endList = findBokBalanceAll(dto); // 期末余额列表
		List<AssetLiabilityStatementQuery001Dto> endListdto  =new ArrayList<>();
		for(AssetLiabilityStatementQueryDto item:endList){
			AssetLiabilityStatementQuery001Dto dtoend =new AssetLiabilityStatementQuery001Dto();
			BeanUtil.copyPropertiesIgnoreNull(dtoend, item);
			endListdto.add(dtoend);

		}
		dto.setBalanceDate(analyseBalanceDate(dto, AssetLiabilityStatementUtil.INITIAL_BALANCE));// 设置年初日期
		List<AssetLiabilityStatementQueryDto> initList = findBokBalanceAll(dto); // 年初余额列表
		List<AssetLiabilityStatementQuery001Dto> initListdto  =new ArrayList<>();
		for(AssetLiabilityStatementQueryDto item:initList){
			AssetLiabilityStatementQuery001Dto dtoinit =new AssetLiabilityStatementQuery001Dto();
			BeanUtil.copyPropertiesIgnoreNull(dtoinit, item);
			initListdto.add(dtoinit);

		}
		/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 start**/
		List<AssetLiabilityStatementQuery001Dto> dtoList = AssetLiabilityStatementUtil.getLiabilityInterestList(finType); // 获得负债和所有者权益报表表样
		/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 end**/
		// 20200417 吉林银行修改-注释4003科目的特殊处理 wangwei start
//        // 处理特殊科目
//        endListdto = dealSpeSubject(endListdto);
//        initListdto = dealSpeSubject(initListdto);
		// 20200417 吉林银行修改-注释4003科目的特殊处理 wangwei end
        /**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 start**/
		assembleDtoList(dtoList, endListdto, AssetLiabilityStatementUtil.ENDING_BALANCE, finType);
		assembleDtoList(dtoList, initListdto, AssetLiabilityStatementUtil.INITIAL_BALANCE, finType);
		/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 end**/
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
	public Date analyseBalanceDate(AssetLiabilityStatementQuery001SearchDto dto, String balanceType)
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

		if (AssetLiabilityStatementUtil.ENDING_BALANCE.equals(balanceType)) {// 期末
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
	 * @param finType 模板类型
	 * @return
	 */
	private void assembleDtoList(List<AssetLiabilityStatementQuery001Dto> dtoList,
			List<AssetLiabilityStatementQuery001Dto> list, String balanceType, String finType) {
		for (AssetLiabilityStatementQuery001Dto dto : dtoList) {
			List<AssetLiabilityStatementEnum> subjectTypeList = dto.getSubjectTypeList();
			for (AssetLiabilityStatementQuery001Dto item : list) {
			    /**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 start**/
				List<AssetLiabilityStatementEnum> enumList = AssetLiabilityStatementEnum
						.enumsfromValue(item.getSubjectNo(), finType);
				/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 end**/
				enumList.retainAll(subjectTypeList);
				if (enumList.size() > 0) {
					// 证券清算款为借方时，为资产，贷方时，为负债，且只会出现在一方为正值
					setInitOrEnd(dto, item, balanceType);
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
	private void assembleLiaIntSum(List<AssetLiabilityStatementQuery001Dto> dtoList) {
		BigDecimal liabilityEndSum = BigDecimal.ZERO;
		BigDecimal liabilityInitSum = BigDecimal.ZERO;
		BigDecimal interestEndSum = BigDecimal.ZERO;
		BigDecimal interestInitSum = BigDecimal.ZERO;
		/**modify by yanya on 20190927 增加根据财务四表模板类型查询配置 start**/
        String finType = CacheUtil.getSysParam("VAT_FIN_TYPE") == null ? 
            AssetLiabilityStatementUtil.FIN_TYPE_01 : CacheUtil.getSysParam("VAT_FIN_TYPE");//查询系统参数财务四表模板类型
		List<String> liabilityList = AssetLiabilityStatementEnum.valuesfromDictCode(finType, DictCodeList.科目类型_负债类);
		List<String> interestList = AssetLiabilityStatementEnum.valuesfromDictCode(finType, DictCodeList.科目类型_所有者权益类);
		/**modify by yanya on 20190927 增加根据财务四表模板类型查询配置 end**/
		for (AssetLiabilityStatementQuery001Dto dto : dtoList) {
			if (liabilityList.contains(dto.getSubjectNo())) {
				liabilityEndSum = liabilityEndSum.add(dto.getEndingBalance());
				liabilityInitSum = liabilityInitSum.add(dto.getInitialBalance());
			}
			if (AssetLiabilityStatementUtil.LIABILITY_SUM.equals(dto.getItemName())) {
				dto.setEndingBalance(liabilityEndSum);
				dto.setInitialBalance(liabilityInitSum);
			}
			if (interestList.contains(dto.getSubjectNo())) {
				interestEndSum = interestEndSum.add(dto.getEndingBalance());
				interestInitSum = interestInitSum.add(dto.getInitialBalance());
			}
			if (AssetLiabilityStatementUtil.INTEREST_SUM.equals(dto.getItemName())) {
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
	 */
	private void setInitOrEnd(AssetLiabilityStatementQuery001Dto dto, AssetLiabilityStatementQuery001Dto item,
			String balanceType) {
		// 公共科目检查 （目前只针对 3 开头的科目）: 根据正负只出现在资产和负债一边
		// 不符合条件的跳出 : 3 开头的科目，资产且正，负债且负
		if (item.getSubjectNo().startsWith("3")){
			// 证券清算款特殊处理：当查询出的余额中证券清算款的余额方向为借方时，为资产，否则为负债
			// 即当余额方向与资产/负债中的证券清算款的方向取值一致时，才会赋值,否则跳出不显示
			if(item.getSubjectNo().equals(AssetLiabilityStatementEnum.资产类_应收证券清算款.getValue())){
				if(!item.getBalFlag().equals(dto.getBalFlag())){
					return;
				}
			}
			/**MODIFY BY WUJIE ON 20200222 START
			else if(!hasCommonCheck(dto, item)) {
				return;
			}
			**/
			else{
				boolean result = hasCommonCheck(dto, item);
				if(!result){
					if(!(item.getSubjectNo().equals(AssetLiabilityStatementEnum.资产类_衍生金融资产_股票期权.getValue())
							|| item.getSubjectNo().equals(AssetLiabilityStatementEnum.资产类_衍生金融资产_买入个股期权.getValue())
							|| item.getSubjectNo().equals(AssetLiabilityStatementEnum.资产类_衍生金融资产_买入指数期权.getValue())
							|| item.getSubjectNo().equals(AssetLiabilityStatementEnum.资产类_衍生金融资产_买入期货期权.getValue())
							|| item.getSubjectNo().equals(AssetLiabilityStatementEnum.负债类_衍生金融负债_股票期权.getValue())
							|| item.getSubjectNo().equals(AssetLiabilityStatementEnum.负债类_衍生金融负债_卖出个股期权.getValue())
							|| item.getSubjectNo().equals(AssetLiabilityStatementEnum.负债类_衍生金融负债_卖出指数期权.getValue())
							|| item.getSubjectNo().equals(AssetLiabilityStatementEnum.负债类_衍生金融负债_卖出期货期权.getValue())
							)){
						return;
					}
				}
			}
			/**MODIFY BY WUJIE ON 20200222 END**/
		}

		String amtType = dto.getUseAmtType();
		if (AssetLiabilityStatementUtil.ENDING_BALANCE.equals(balanceType)) {

			if (!StringUtils.equals(item.getBalFlag(), dto.getBalFlag()) &&
					AssetLiabilityStatementUtil.B_AMT.equals(amtType)) {// 实际余额方向与期望不同时 *-1
																				// (仅当取值为本币余额时)
				dto.setEndingBalance(dto.getEndingBalance().add(item.getbAmt().negate()));
			} else {
				dto.setEndingBalance(dto.getEndingBalance().add(item.getbAmt()));
			}
		} else {

			if (!StringUtils.equals(dto.getBalFlag(), item.getBalFlag()) &&
					AssetLiabilityStatementUtil.B_AMT.equals(amtType)) {// 实际余额方向与期望不同时 *-1
																				// (仅当取值为本币余额时)
				dto.setInitialBalance(dto.getInitialBalance().add(item.getbAmt().negate()));
			} else {
				dto.setInitialBalance(dto.getInitialBalance().add(item.getbAmt()));
			}
		}
	}

	/**
	 * 公共科目检查 （目前只针对 3 开头的科目）: 根据正负只出现在资产和负债一边
	 * @param dto 容器
	 * @param item db 数据
	 * @return 资产且正，负债且负 则返回 true，反之 false
	 */
	private boolean hasCommonCheck(AssetLiabilityStatementQuery001Dto dto, AssetLiabilityStatementQuery001Dto item) {
		boolean result = false;

		DictCodeList subjectType = dto.getSubjectTypeList().get(0).getDictCodeList();
		if (DictCodeList.科目类型_资产类.equals(subjectType)
				&& item.getbAmt().compareTo(BigDecimal.ZERO) > 0) {
			result = true;
		} else if (DictCodeList.科目类型_负债类.equals(subjectType)
				&& item.getbAmt().compareTo(BigDecimal.ZERO) < 0) {
			result = true;
		}
		return result;
	}


	/**
	 * 导出 资产负债表 excel
	 *
	 * @param dto
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public ResponseEntity<byte[]> exportExcel(AssetLiabilityStatementQuery001SearchDto dto)
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
	public ExportFileDto createExcel(AssetLiabilityStatementQuery001SearchDto dto) throws ParseException, IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date reportDate = analyseBalanceDate(dto, AssetLiabilityStatementUtil.ENDING_BALANCE);
		List<AssetLiabilityStatementQuery001Dto> assetList = new ArrayList<AssetLiabilityStatementQuery001Dto>();
		String prefix = "";
		if (StringUtil.equals(dto.getReportDimen(),"1")) {
			assetList = findAssetsStatement(dto);
			prefix = dto.getBooksetId();
		} else {
			assetList = findAssetsStatementAll(dto);
			prefix = "账套组";
		}
		if (CollectionUtils.isNotEmpty(assetList)) {// 设置总计
			assetList.addAll(AssetLiabilityStatementUtil.getAssetListSum(assetList));
		}
		List<AssetLiabilityStatementQuery001Dto> liabilityInterestList = new ArrayList<AssetLiabilityStatementQuery001Dto>();
		if (DictCodeListExt1.报表维度_单账套.getValue().equals(dto.getReportDimen())) {
			liabilityInterestList = findLiabilityInterestStatement(dto);
		} else {
			liabilityInterestList = findLiabilityInterestStatementAll(dto);
		}
		if (CollectionUtils.isNotEmpty(liabilityInterestList)) {// 设置总计
			assembleLiaIntSum(liabilityInterestList);
			liabilityInterestList
					.addAll(AssetLiabilityStatementUtil.getLiabilityInterestListSum(liabilityInterestList));
		}
		// 合并List
		List<AssetLiabilityStatementQuery001Dto> mergeList = mergeList(assetList, liabilityInterestList);

		// 解决报表数据为空导出模板公式的问题
		if (mergeList.isEmpty()) {
			AssetLiabilityStatementQuery001Dto emptyDto = new AssetLiabilityStatementQuery001Dto();
			emptyDto.setInitialBalance(null);
			emptyDto.setInitialBalance1(null);
			emptyDto.setEndingBalance(null);
			emptyDto.setEndingBalance1(null);
			mergeList.add(emptyDto);
		}

		String exportFilePath = CacheUtil.getSysParam(GlobalConstant.COM_PROP_ACCESS_EXPORTPATH);
		String fileName = prefix + "_" + sdf.format(reportDate) + "_资产负债表" + ".xls";
		String tempName = "AssetLiabilityStatement.xls";
		String tplFlodPath = "financialstatement" + File.separator + "assetliailitystatement"  + File.separator +  "AssetLiabilityStatement.xls";
		String tplPath = FileNameUtil.getTplFile(tplFlodPath);
		String outPath = exportFilePath + File.separator + fileName;

		ExportFileDto exportFileDto = new ExportFileDto();
		InputStream stream = null;
		Map<String, Object> map = new HashMap<>();
		//map.put("org",CacheUtil.getSysParam(GlobalConstant.财务报表_编制单位));
		 //多级法人改造start add by hxy 20200612
        if(MultilevelDataAuthorityUtil.isEnabled()) {
        	 //联社模式
        	 //根据选择的账套组或者账套，查询出它们的所属机构
        	String booksetOrg;
        	if (DictCodeListExt2.批处理账套类型_账套组.getValue().equals(dto.getReportDimen())) {
        		booksetOrg=dto.getBooksetGroup();
        		Map<String, Object> orgmap=new HashMap<String, Object>();
        		orgmap.put("booksetOrg", booksetOrg);
        		orgmap.put("dataType", "V08");
        		String orgName=booksetComMapper.selectAssociationModeOrgGrpName(orgmap);
            	map.put("org", orgName);
        	}else{
        		booksetOrg=dto.getBooksetId();
        		Map<String, Object> orgmap=new HashMap<String, Object>();
        		orgmap.put("booksetOrg", booksetOrg);
        		orgmap.put("dataType", "V08");
        		String orgName=booksetComMapper.selectAssociationModeOrgName(orgmap);
            	map.put("org", orgName);
        	}
        	}else{
        	//银行模式
        	String orgName=booksetComMapper.selectBankModeOrgName();
        	map.put("org", orgName);
        	
        }
        //多级法人改造end add by hxy 20200612
       
		
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
	private List<AssetLiabilityStatementQuery001Dto> mergeList(List<AssetLiabilityStatementQuery001Dto> assetList,
			List<AssetLiabilityStatementQuery001Dto> liabilityInterestList) {
		boolean assetMax = false;
		List<AssetLiabilityStatementQuery001Dto> maxList = null;
		List<AssetLiabilityStatementQuery001Dto> minList = null;
		List<AssetLiabilityStatementQuery001Dto> mergeList = new ArrayList<AssetLiabilityStatementQuery001Dto>();
		if (assetList.size() > liabilityInterestList.size()) {
			maxList = assetList;
			minList = liabilityInterestList;
			assetMax = true;
		} else {
			maxList = liabilityInterestList;
			minList = assetList;
		}
		AssetLiabilityStatementQuery001Dto dto = null;
		AssetLiabilityStatementQuery001Dto minLastDto = null;
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
				if (assetMax) {
					dto.setInitialBalance1(minLastDto==null?null:minLastDto.getInitialBalance());
					dto.setEndingBalance1(minLastDto==null?null:minLastDto.getEndingBalance());
					dto.setItemName1(minLastDto==null?null:minLastDto.getItemName());
				} else {
					dto.setInitialBalance1(dto.getInitialBalance());
					dto.setEndingBalance1(dto.getEndingBalance());
					dto.setItemName1(dto.getItemName());

					dto.setInitialBalance(minLastDto==null?null:minLastDto.getInitialBalance());
					dto.setEndingBalance(minLastDto==null?null:minLastDto.getEndingBalance());
					dto.setItemName(minLastDto==null?null:minLastDto.getItemName());
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
     * @return AssetLiabilityStatementQuery001SearchDto
     */
    public AssetLiabilityStatementQuery001SearchDto getDateInfo(Map<String, Object> pageParam) {
        return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME
                                          + "/AssetLiabilityStatementQuery001/getDateInfo",
            pageParam, AssetLiabilityStatementQuery001SearchDto.class);
    }

}
