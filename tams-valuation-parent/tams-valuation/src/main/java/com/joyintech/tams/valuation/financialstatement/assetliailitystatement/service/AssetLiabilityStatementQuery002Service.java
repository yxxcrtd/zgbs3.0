package com.joyintech.tams.valuation.financialstatement.assetliailitystatement.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatement.common.AssetLiabilityStatementEnum;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatement.common.AssetLiabilityStatementUtil;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatement.dao.AssetLiabilityStatementQuery001Mapper;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatement.dto.AssetLiabilityStatementQuery001Dto;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatement.dto.AssetLiabilityStatementQuery001SearchDto;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatement.dto.AssetLiabilityStatementQueryDto;

/**
 * 资产负债表-账套明细列表
 * 
 * @author QuJianZhong
 * @version 1.0 2018年11月22日
 * @see AssetLiabilityStatementQuery002Service
 * @since 1.0
 */
@Service
public class AssetLiabilityStatementQuery002Service extends CommonExportService<AssetLiabilityStatementQuery001Dto> {
	
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
	 * 获得 资产报表数据（账套明细列表查询）
	 * 
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<AssetLiabilityStatementQuery001Dto> findAssetsStatementAll(AssetLiabilityStatementQuery001SearchDto dto)
			throws ParseException {
		List<AssetLiabilityStatementQuery001Dto> retList = new ArrayList<AssetLiabilityStatementQuery001Dto>();
		// 全账套
		List<SuggestDto> accountList = dto.getAccountList();
		// 项目名
		String itemName = dto.getItemName();
		AssetLiabilityStatementQuery001Dto tempDto = null;
		/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 start**/
        String finType = CacheUtil.getSysParam("VAT_FIN_TYPE") == null ? 
            AssetLiabilityStatementUtil.FIN_TYPE_01 : CacheUtil.getSysParam("VAT_FIN_TYPE");//查询系统参数财务四表模板类型
        /**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 end**/
		for (SuggestDto account : accountList) {
			dto.setBooksetId(account.getCode());
			/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 start**/
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
			dtoList.addAll(AssetLiabilityStatementUtil.getAssetListSum(dtoList));
			
			// 找到项目对应的
			Map<String, AssetLiabilityStatementQuery001Dto> map = Maps.uniqueIndex(dtoList, new Function<AssetLiabilityStatementQuery001Dto, String>(){

				@Override
				public String apply(AssetLiabilityStatementQuery001Dto t) {
					return t.getItemName();
				}
	        	
	        });
	        tempDto = map.get(itemName);
	        tempDto.setBooksetId(account.getCode());
	        retList.add(tempDto);
		}
		return retList;
	}
	
	/**
	 * 获得 负债和所有者权益报表数据（账套明细列表查询）
	 * 
	 * @param dto
	 * @return
	 * @throws ParseException
	 */
	public List<AssetLiabilityStatementQuery001Dto> findLiabilityInterestStatementAll(
			AssetLiabilityStatementQuery001SearchDto dto) throws ParseException {
		List<AssetLiabilityStatementQuery001Dto> retList = new ArrayList<AssetLiabilityStatementQuery001Dto>();
		// 全账套
		List<SuggestDto> accountList = dto.getAccountList();
		// 项目名
		String itemName = dto.getItemName();
		AssetLiabilityStatementQuery001Dto tempDto = null;
		/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 start**/
        String finType = CacheUtil.getSysParam("VAT_FIN_TYPE") == null ? 
            AssetLiabilityStatementUtil.FIN_TYPE_01 : CacheUtil.getSysParam("VAT_FIN_TYPE");//查询系统参数财务四表模板类型
        /**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 end**/
		for (SuggestDto account : accountList) {
			dto.setBooksetId(account.getCode());
			/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 start**/
			dto.setSubjectNoList(
					AssetLiabilityStatementEnum.valuesfromDictCode(finType, DictCodeList.科目类型_负债类, DictCodeList.科目类型_所有者权益类));
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
			List<AssetLiabilityStatementQuery001Dto> dtoList = AssetLiabilityStatementUtil.getLiabilityInterestList(finType); // 获得负债和所有者权益报表表样
			/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 end**/
            // 处理特殊科目
            endListdto = dealSpeSubject(endListdto);
            initListdto = dealSpeSubject(initListdto);
            /**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 start**/
            assembleDtoList(dtoList, endListdto, AssetLiabilityStatementUtil.ENDING_BALANCE, finType);
			assembleDtoList(dtoList, initListdto, AssetLiabilityStatementUtil.INITIAL_BALANCE, finType);
			/**modify by yanya on 20190929 增加根据财务四表模板类型查询配置 end**/
			assembleLiaIntSum(dtoList);
			dtoList.addAll(AssetLiabilityStatementUtil.getLiabilityInterestListSum(dtoList));
			
			// 找到项目对应的
	        Map<String, AssetLiabilityStatementQuery001Dto> map = Maps.uniqueIndex(dtoList, new Function<AssetLiabilityStatementQuery001Dto, String>(){

				@Override
				public String apply(AssetLiabilityStatementQuery001Dto t) {
					return t.getItemName();
				}
	        	
	        });
	        tempDto = map.get(itemName);
	        tempDto.setBooksetId(account.getCode());
	        retList.add(tempDto);
		}
		return retList;
	}

    /**
     * 处理特殊科目 - （以账套维度处理，兼容多账套处理）
     * @param endList 从 DB 查到的数据
     * @return 根据需求处理获取到的数据
     */
    private List<AssetLiabilityStatementQuery001Dto> dealSpeSubject(List<AssetLiabilityStatementQuery001Dto> listDB) {
        List<AssetLiabilityStatementQuery001Dto> listResult = Lists.newArrayList();

        Map<String, List<AssetLiabilityStatementQuery001Dto>> map = listDB.stream().collect(Collectors.groupingBy(AssetLiabilityStatementQuery001Dto::getBooksetId));
        for (String key : map.keySet()) {
            List<AssetLiabilityStatementQuery001Dto> list = map.get(key);
            listResult.addAll(list);

            // 账套.核算方式=“预期收益型”时，对其他综合收益进行特殊处理 start
            if (!DictCodeList.收益类型_收益型.getValue().equals(list.get(0).getAcctMode())){
                break;
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
			calendar.set(year, 0, 1);
			return calendar.getTime();
		}
	}

	/**
	 * 组装 DTOList
	 * 
	 * @param dtoList     表样
	 * @param list        结果集
	 * @param balanceType 操作余额类型
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
	 * @param             dto.balFlag 期待方向
	 * @param amtType     使用金额类型
	 */
	private void setInitOrEnd(AssetLiabilityStatementQuery001Dto dto, AssetLiabilityStatementQuery001Dto item,
			String balanceType) {
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

}
