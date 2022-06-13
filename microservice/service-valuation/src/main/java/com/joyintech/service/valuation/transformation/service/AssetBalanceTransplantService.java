package com.joyintech.service.valuation.transformation.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.tams.valuation.common.batch.finproduct.dao.FinProductCommonMapper;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dao.InterfCommonMapper;
import com.joyintech.tams.valuation.common.interf.base.dto.BalanceTransplantDto;
import com.joyintech.tams.valuation.common.interf.base.util.InterfHelper;
import com.joyintech.service.valuation.datatransplant.dto.TemplateCheckDto;
import com.joyintech.service.valuation.transformation.constants.TransConstant;
import com.joyintech.service.valuation.transformation.dto.AssetBalanceDto;
import com.joyintech.service.valuation.transformation.dto.ResultData;
import com.joyintech.tams.common.base.dao.BokBooksetMapper;
import com.joyintech.tams.common.base.entity.BokBookset;
import com.joyintech.tams.common.base.entity.BokBooksetCapacctInfo;
import com.joyintech.tams.common.base.entity.BokSubject;
import com.joyintech.tams.common.base.entity.TrsSubjectRelation;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.service.DataTransplantCommonService;
import com.joyintech.tams.valuation.common.utils.ValStringUtil;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.calculation.util.ArithmeticUtil;

@Service(value="serviceAssetBalanceTransplantService")
@Transactional(rollbackFor = Exception.class)
public class AssetBalanceTransplantService extends CommonExportService<BalanceTransplantDto> {

	@Autowired
    private InterfCommonMapper interfCommonMapper;
	
	@Autowired
	private FinProductCommonMapper finProductCommonMapper;
	
	@Autowired
    private BokBooksetMapper valBokBooksetMapper;
	
	@Autowired
	private DataTransplantCommonService dataTransplantCommonService;

	@Value("${tams.properties.maxSqlNum:500}")
	private int maxSqlNum;
	
	
	//数据移植科目对应关系
	Map<String,TrsSubjectRelation> subRelMap;
			

	@SuppressWarnings({ "unchecked"})
	public ResultData execute(List<List<String>> sheetDataList) {
		ResultData result = new ResultData();
		
		List<AssetBalanceDto> listAssetBalanceDto = new ArrayList<AssetBalanceDto>();
		listAssetBalanceDto = convertToDtoOway(sheetDataList);
		
		//数据移植科目对应关系
		this.setSubRelMap(this.getTrsSubjectRelationMap());
		
		// 存储校验信息
		List<TemplateCheckDto> checkList = new ArrayList<TemplateCheckDto>();
		
		// 存储转换结果
		List<BalanceTransplantDto> balanceList = new ArrayList<BalanceTransplantDto>();
		
		// 类型
		String typeError = "错误";
		// String typeWarn = "警告";
		// 错误条数
		int number = 0;
		// 账务余额模板校验
		Map<String, Object> map = checkTransplantTemple(listAssetBalanceDto, checkList,
				typeError, number);
		checkList = (List<TemplateCheckDto>) map.get("checkList");
		number = (int) map.get("number");
		
		if (number == 0) {
			String batchSeqno = InterfHelper.getUUid();
			// 数据循环处理
			for (AssetBalanceDto dto : listAssetBalanceDto) {
				//账务余额模板DTO
				//业务明细代码、业务明细类型、投资目的
				BalanceTransplantDto rst = new BalanceTransplantDto();
				//导入批次号
				rst.setBatchSeqno(batchSeqno);
				//账套代码
				rst.setBooksetId(dto.getBooksetId());
				//币种
				rst.setoCcy(dto.getoCcy());
				//日期
				rst.setBalanceDate(DateUtil.format(dto.getCdate()));
				//业务明细代码
				rst.setBusiId(dto.getAssetCode());
				//业务明细类型
				rst.setBusiDetailType(dto.getAssetTypeName());
				//投资目的
				rst.setInvAim(dto.getInvAimName());
				//分支序号 --默认为空
				//结息日-默认为空 
				
				//科目号、余额方向、原币余额、数量余额
				//成本科目
				BalanceTransplantDto result_subject = this.getBalanceTransplantDto(rst, dto,
						TransConstant.资产余额报表_成本科目);
				if(result_subject!=null){
					balanceList.add(result_subject);
				}
				//应收利息科目
				result_subject = this.getBalanceTransplantDto(rst, dto,
						TransConstant.资产余额报表_应计利息科目);
				if(result_subject!=null){
					balanceList.add(result_subject);
				}
				//公允价值变动科目 
				result_subject = this.getBalanceTransplantDto(rst, dto,
						TransConstant.资产余额报表_公允价值变动);
				if(result_subject!=null){
					balanceList.add(result_subject);
				}
				
				//利息调整科目
				result_subject = this.getBalanceTransplantDto(rst, dto,
						TransConstant.资产余额报表_利息调整科目);
				if(result_subject!=null){
					balanceList.add(result_subject);
				}
			}
			result.setBatchSeqno(batchSeqno);//导入批次
			dataTransplantCommonService.insertTrsBalanceTransplant(balanceList);
			result.setRstCode(TransConstant.解析状态_成功);
		}else{
			result.setRstCode(TransConstant.解析状态_失败);
		}
		result.setCheckList(checkList);
		return result;
	}
	
	/**
	 * 根据账套ID、资产类型映射业务明细代码值
	 * @param booksetId
	 * @param assetType
	 * @return
	 */
	private void setBalanceTransplantMapping(AssetBalanceDto dto){
		//业务明细类型
		String busiDetailType = null;
		//投资目的
		String invAim = null;
		//投资目的名称
		String invAimName = null;
		//收益类型
		String profitType = null; 
		//业务明细类型名称
		String busiDetailTypeName=null;
		
		if("非标债权".equals(dto.getAssetType())){
			busiDetailType = DictCodeListExt1.金融产品类型_计划类资产.getValue();
			profitType = DictCodeList.收益类型_收益型.getValue();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+ DictCodeListExt1.金融产品类型_计划类资产.getCodeName();
		}
		else if("正回购".equals(dto.getAssetType())){
			busiDetailType = DictCodeListExt1.金融产品类型_债券质押式正回购.getValue();
			profitType = DictCodeList.收益类型_收益型.getValue();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+DictCodeListExt1.金融产品类型_债券质押式正回购.getCodeName();
		}
		else if("逆回购".equals(dto.getAssetType())){
			busiDetailType = DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue();
			profitType = DictCodeList.收益类型_收益型.getValue();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+DictCodeListExt1.金融产品类型_债券质押式逆回购.getCodeName();
		}
		else if("同业借款".equals(dto.getAssetType())){
			busiDetailType = DictCodeListExt1.金融产品类型_同业借款.getValue();
			profitType = DictCodeList.收益类型_收益型.getValue();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+DictCodeListExt1.金融产品类型_同业借款.getCodeName();
		}
		else if("债券".equals(dto.getAssetType())){
			busiDetailType = DictCodeListExt1.金融产品类型_债券.getValue();
			invAim = DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue();
			profitType = DictCodeList.收益类型_收益型.getValue();
			invAimName = invAim+":"+DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getCodeName();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+DictCodeListExt1.金融产品类型_债券.getCodeName();
		}
		else if("自定义资产".equals(dto.getAssetType())){
			busiDetailType = DictCodeListExt1.金融产品类型_计划类资产.getValue();
			profitType = DictCodeList.收益类型_收益型.getValue();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+DictCodeListExt1.金融产品类型_计划类资产.getCodeName();
		}
		else if("活期存款".equals(dto.getAssetType())){
			busiDetailType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
			profitType = DictCodeList.收益类型_收益型.getValue();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+DictCodeListExt1.资金账户类型_产品托管户.getCodeName();
		}
		else if("净值型资产".equals(dto.getAssetType())){
			busiDetailType = DictCodeListExt1.金融产品类型_计划类资产.getValue();
			profitType = DictCodeList.收益类型_净值型.getValue();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+DictCodeListExt1.金融产品类型_计划类资产.getCodeName();
		}
		else if("特殊目的载体".equals(dto.getAssetType())){
			busiDetailType = DictCodeListExt1.金融产品类型_拆入.getValue();
			profitType = DictCodeList.收益类型_收益型.getValue();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+DictCodeListExt1.金融产品类型_拆入.getCodeName();
		}
		else if("货币基金".equals(dto.getAssetType())){
			busiDetailType = DictCodeListExt1.金融产品类型_基金.getValue();
			profitType = DictCodeList.收益类型_货币型.getValue();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+DictCodeListExt1.金融产品类型_基金.getCodeName();
		}
		else if("公募基金".equals(dto.getAssetType())){
			busiDetailType = DictCodeListExt1.金融产品类型_基金.getValue();
			profitType = DictCodeList.收益类型_净值型.getValue();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+DictCodeListExt1.金融产品类型_基金.getCodeName();
		}
		else if("理财直融工具".equals(dto.getAssetType())){
			/**modify by wujie on 20190130 start
			busiDetailType = DictCodeListExt1.金融产品类型_债券.getValue();
			invAim = DictCodeListExt1.投资目的_摊余成本类.getValue();
			profitType = DictCodeList.收益类型_收益型.getValue();
			invAimName = invAim+":"+DictCodeListExt1.投资目的_摊余成本类.getCodeName();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+DictCodeListExt1.金融产品类型_债券.getCodeName();
			**/
			busiDetailType = DictCodeListExt1.金融产品类型_计划类资产.getValue();
			profitType = DictCodeList.收益类型_收益型.getValue();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+DictCodeListExt1.金融产品类型_计划类资产.getCodeName();
			/**modify by wujie on 20190130 end**/
		}
		else if("存放同业".equals(dto.getAssetType())){
			busiDetailType = DictCodeListExt1.金融产品类型_存款.getValue();
			profitType = DictCodeList.收益类型_收益型.getValue();
			busiDetailTypeName = busiDetailType+GlobalConstant.分号+DictCodeListExt1.金融产品类型_存款.getCodeName();
		}else{
			busiDetailType = null;
		}
		dto.setAssetType(busiDetailType);
		dto.setAssetTypeName(busiDetailTypeName);
		dto.setInvAim(invAim);
		dto.setInvAimName(invAimName);
		dto.setProfitType(profitType);
	}

	/**
	 * 将excel中的数据转换为实体类
	 * 
	 * @param sheetDataList
	 * @return
	 * @see
	 * @since 1.0
	 */
	private List<AssetBalanceDto> convertToDtoOway(List<List<String>> sheetDataList) {
		// 定义返回实体类
		List<AssetBalanceDto> dtoList = new ArrayList<AssetBalanceDto>();
		// 循环进行数据处理
		for (List<String> row : sheetDataList) {
			// 定义实体类
			AssetBalanceDto dto = new AssetBalanceDto();
			
			int column = 0;
			// 账套代码
			if (StringUtils.isNotEmpty(row.get(column))) {
				dto.setBooksetId(row.get(column).trim());
			}
			
			column++;
			
			//资产类型
			if (StringUtils.isNotEmpty(row.get(column))) {
				dto.setAssetType(row.get(column).trim());
			}
			//保留模板中的资产类型
			dto.setTemplateAssetType(dto.getAssetType());
			
			column++;
			
			// 资产代码
			if (StringUtils.isNotEmpty(row.get(column))) {
				dto.setAssetCode(row.get(column).trim());
			}
			
			column++;
			
			// 初始本金
			if (StringUtils.isNotEmpty(row.get(column))) {
				String prinamt = String.valueOf(row.get(column));
				if(!"--".equals(prinamt)){
					dto.setPrincipal(new BigDecimal(row.get(column).trim()));
				}
			}
			
			column++;
			
			//当前余额（份额）
			if (StringUtils.isNotEmpty(row.get(column))) {
				dto.setShare(new BigDecimal(row.get(column).trim()));
			}
			
			column++;
			
			//币种
			if (StringUtils.isNotEmpty(row.get(column))) {
				dto.setoCcy(row.get(column).trim());
			}
			
			column++;
			
			//查询日
			if (StringUtils.isNotEmpty(row.get(column))) {
				dto.setCdate(row.get(column) == null ? null : DateUtil.parse(row.get(column).trim()));
			}
			
			column++;
			
			//应收利息
			if (StringUtils.isNotEmpty(row.get(column))) {
				dto.setIntAmt(new BigDecimal(row.get(column).trim()));
			}
			
			column++;
			
			//资产估值（成本法）
			if (StringUtils.isNotEmpty(row.get(column))) {
				dto.setAssetValuationCB(new BigDecimal(row.get(column).trim()));
			}
			
			column++;
			
			//资产估值（市价法）
			if (StringUtils.isNotEmpty(row.get(column))) {
				dto.setAssetValuationSJ(new BigDecimal(row.get(column).trim()));
			}
			
			column++;
			
			//公允价值
			if (StringUtils.isNotEmpty(row.get(column))) {
				dto.setFairValue(new BigDecimal(row.get(column).trim()));
			}
			
			
			column++;
			
			//面值
			if (StringUtils.isNotEmpty(row.get(column))) {
				dto.setNetValue(new BigDecimal(row.get(column).trim()));
			}
			
			// 塞入list
			dtoList.add(dto);

		}

		return dtoList;
	}
	
	/**
	 * 模板校验
	 */
	private Map<String, Object> checkTransplantTemple(List<AssetBalanceDto> listAssetBalanceDto, List<TemplateCheckDto> checkList,
			String typeError, int number) {
		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < listAssetBalanceDto.size(); i++) {
			AssetBalanceDto dto = listAssetBalanceDto.get(i);
			// 判断是否有错误
			boolean error = false;
			// 校验账套表中是否存在该账套
			if (StringUtil.isNotEmpty(dto.getBooksetId())) {
				BokBookset valBokBookset = valBokBooksetMapper.selectByPrimaryKey(dto.getBooksetId());
				if(valBokBookset==null){
					TemplateCheckDto templateCheckDto = new TemplateCheckDto();
					templateCheckDto.setErrorLineNum(i + 2);
					templateCheckDto.setErrorType(typeError);
					templateCheckDto.setErrorMessage("账套表中不存在该账套");
					checkList.add(templateCheckDto);
					error = true;
				}else{
					dto.setBooksetIdTmpl(valBokBookset.getBooksetIdTmpl());
				}
			}
			
			String assetType = dto.getAssetType();
			if (!StringUtil.isNotEmpty(assetType)) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
                templateCheckDto.setErrorLineNum(i + 2);
                templateCheckDto.setErrorType(typeError);
                templateCheckDto.setErrorMessage("资产类型不能为空");
                checkList.add(templateCheckDto);
                error = true;
			}else{
				if("活期存款".equals(assetType)){
					//根据账套代码、资金账户类型查询托管户
					Map<String,Object> paramsMap = new HashMap<String,Object>();
					paramsMap.put("booksetId", dto.getBooksetId());
					paramsMap.put("acctType", DictCodeListExt1.资金账户类型_产品托管户.getValue());
					BokBooksetCapacctInfo capaccInfo = interfCommonMapper.queryBokBooksetCapacctInfo(paramsMap);
					if(capaccInfo != null){
						dto.setAssetCode(capaccInfo.getAcctNo());
						
						this.setBalanceTransplantMapping(dto);
						
					}else{
						TemplateCheckDto templateCheckDto = new TemplateCheckDto();
						templateCheckDto.setErrorLineNum(i + 2);
						templateCheckDto.setErrorType(typeError);
						templateCheckDto.setErrorMessage("账套["+dto.getBooksetId()+"],资产类型["+assetType+"]：无对应的托管户");
						checkList.add(templateCheckDto);
						error = true;
					}
				}else{
					
					this.setBalanceTransplantMapping(dto);
					
					if (!StringUtil.isNotEmpty(dto.getAssetType())) {
						TemplateCheckDto templateCheckDto = new TemplateCheckDto();
		                templateCheckDto.setErrorLineNum(i + 2);
		                templateCheckDto.setErrorType(typeError);
		                templateCheckDto.setErrorMessage("资产类型["+assetType+"]无对应操作");
		                checkList.add(templateCheckDto);
		                error = true;
					}else{
						if (!StringUtil.isNotEmpty(dto.getAssetCode())) {
							TemplateCheckDto templateCheckDto = new TemplateCheckDto();
			                templateCheckDto.setErrorLineNum(i + 2);
			                templateCheckDto.setErrorType(typeError);
			                templateCheckDto.setErrorMessage("资产代码不能为空");
			                checkList.add(templateCheckDto);
			                error = true;
						}else{
							//资产信息
							Map<String,Object> paMap = new HashMap<String,Object>();
							paMap.put("finprodId", dto.getAssetCode());
							paMap.put("finprodType", dto.getAssetType());
							
							paMap.put("branch", InterfCommonConstant.分支序号_默认);
							
							List<TargetFinProduct> targetFinProductList = finProductCommonMapper.getTargetFinProduct(paMap);
			                TargetFinProduct targetFinProduct = null;
			                if (targetFinProductList != null && targetFinProductList.size() > 0) {
			                	targetFinProduct = targetFinProductList.get(0);
			                }
							if(targetFinProduct==null){
								TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				                templateCheckDto.setErrorLineNum(i + 2);
				                templateCheckDto.setErrorType(typeError);
				                templateCheckDto.setErrorMessage("资产代码["+dto.getAssetCode()+"]不存在");
				                checkList.add(templateCheckDto);
				                error = true;
							}else{
								//境内外
								dto.setBlnArea(targetFinProduct.getBlnArea());
								//核算分类
								dto.setType2(targetFinProduct.getType2());
								//交易场所
								dto.setTradeMarket(targetFinProduct.getTradeMarket());
							}
						}
						
					}
				}
			}
			
			// 币种:校验枚举字段是否有效
			if (StringUtil.isNotEmpty(dto.getoCcy())) {
				String ccy = TransConstant.币种Map.get(dto.getoCcy());
				if (!StringUtil.isNotEmpty(ccy)) {
					TemplateCheckDto templateCheckDto = new TemplateCheckDto();
					templateCheckDto.setErrorLineNum(i + 2);
					templateCheckDto.setErrorType(typeError);
					templateCheckDto.setErrorMessage(
							"币种：" + dto.getoCcy() + "目前不支持");
					checkList.add(templateCheckDto);
					error = true;
				}else{
					dto.setoCcy(ccy);
				}
			}
			
			//判断查询日
			if (!StringUtil.isNotEmpty(dto.getCdate())) {
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("查询日不能为空");
				checkList.add(templateCheckDto);
				error = true;
			}
			
			if(dto.getAssetType()==null){
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("资产类型["+assetType+"]无对应操作");
				checkList.add(templateCheckDto);
				error = true;
			}
			
			TrsSubjectRelation trsSubjectRelation = this.getSubRelMap().get(getMapKey(dto));
			if(trsSubjectRelation==null){
				TemplateCheckDto templateCheckDto = new TemplateCheckDto();
				templateCheckDto.setErrorLineNum(i + 2);
				templateCheckDto.setErrorType(typeError);
				templateCheckDto.setErrorMessage("数据移植科目对应关系未配置");
				checkList.add(templateCheckDto);
				error = true;
			}else{
				dto.setTrsSubjectRelation(trsSubjectRelation);
			}
			if (error) {
				number++;
			}

		}
		map.put("checkList", checkList);
		map.put("number", number);

		return map;
	}


	/**
	 * 数据移植科目对应关系Map
	 */
	private Map<String,TrsSubjectRelation> getTrsSubjectRelationMap(){
		Map<String,TrsSubjectRelation> map = new HashMap<String,TrsSubjectRelation>();
		List<TrsSubjectRelation> list = dataTransplantCommonService.getTrsSubjectRelationAll();
		if (CollectionUtils.isNotEmpty(list)) {
			for(TrsSubjectRelation o :list){
				//账套代码+核算分类+境内外+业务明细类型+收益类型+投资目的
				String key = ValStringUtil.getNotNullStr(o.getBooksetId())+
						ValStringUtil.getNotNullStr(o.getBookType())+
						ValStringUtil.getNotNullStr(o.getBlnArea())+
						ValStringUtil.getNotNullStr(o.getBusiDetailType())+
						ValStringUtil.getNotNullStr(o.getProfitType())+
						ValStringUtil.getNotNullStr(o.getInvAim());
				map.put(key, o);
			}
		}
		return map;
	}

	/**
	 * 数据移植科目对应关系Map的key值设置
	 * @param dto
	 * @return
	 */
	private String getMapKey(AssetBalanceDto dto){
		String key = "";
		if(DictCodeListExt1.金融产品类型_债券.getValue().equals(dto.getAssetType())){
			//账套代码+核算分类+业务明细类型+收益类型+投资目的
			key = ValStringUtil.getNotNullStr(dto.getBooksetIdTmpl())+
					ValStringUtil.getNotNullStr(dto.getType2())+
					ValStringUtil.getNotNullStr(dto.getAssetType())+
					ValStringUtil.getNotNullStr(dto.getProfitType())+
					ValStringUtil.getNotNullStr(dto.getInvAim());
		}
		else if(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(dto.getAssetType())
				|| DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue().equals(dto.getAssetType())
				|| DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(dto.getAssetType())
				|| DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(dto.getAssetType())){
			//账套代码+核算分类(交易场所)+业务明细类型+收益类型
			key = ValStringUtil.getNotNullStr(dto.getBooksetIdTmpl())+
					ValStringUtil.getNotNullStr(dto.getTradeMarket())+
					ValStringUtil.getNotNullStr(dto.getAssetType())+
					ValStringUtil.getNotNullStr(dto.getProfitType());
		}
		else if(DictCodeListExt1.金融产品类型_存款.getValue().equals(dto.getAssetType())
				|| DictCodeListExt1.金融产品类型_拆出.getValue().equals(dto.getAssetType())
				|| DictCodeListExt1.金融产品类型_同业借款.getValue().equals(dto.getAssetType())){
			//账套代码+核算分类+业务明细类型+收益类型
			key = ValStringUtil.getNotNullStr(dto.getBooksetIdTmpl())+
					ValStringUtil.getNotNullStr(dto.getType2())+
					ValStringUtil.getNotNullStr(dto.getAssetType())+
					ValStringUtil.getNotNullStr(dto.getProfitType());
		}
		else if(DictCodeListExt1.金融产品类型_基金.getValue().equals(dto.getAssetType())){
			if(DictCodeList.收益类型_货币型.getValue().equals(dto.getProfitType())){
				//账套代码+境内外+业务明细类型+收益类型
				key = ValStringUtil.getNotNullStr(dto.getBooksetIdTmpl())+
						ValStringUtil.getNotNullStr(dto.getBlnArea())+
						ValStringUtil.getNotNullStr(dto.getAssetType())+
						ValStringUtil.getNotNullStr(dto.getProfitType());
						
			}else{
				if(DictCodeList.境内外_境内.getValue().equals(dto.getBlnArea())){
					//账套代码+核算分类+境内外+业务明细类型+收益类型
					key = ValStringUtil.getNotNullStr(dto.getBooksetIdTmpl())+
							ValStringUtil.getNotNullStr(dto.getType2())+
							ValStringUtil.getNotNullStr(dto.getBlnArea())+
							ValStringUtil.getNotNullStr(dto.getAssetType())+
							ValStringUtil.getNotNullStr(dto.getProfitType());
				}else{
					//账套代码+境内外+业务明细类型+收益类型
					key = ValStringUtil.getNotNullStr(dto.getBooksetIdTmpl())+
							ValStringUtil.getNotNullStr(dto.getBlnArea())+
							ValStringUtil.getNotNullStr(dto.getAssetType())+
							ValStringUtil.getNotNullStr(dto.getProfitType());
				}
			}
		}
		else if(DictCodeListExt1.金融产品类型_拆入.getValue().equals(dto.getAssetType())){
			//账套代码+业务明细类型+收益类型
			key = ValStringUtil.getNotNullStr(dto.getBooksetIdTmpl())+
					ValStringUtil.getNotNullStr(dto.getAssetType())+
					ValStringUtil.getNotNullStr(dto.getProfitType());
		}
		else if(DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(dto.getAssetType())){
			//账套代码+境内外+业务明细类型+收益类型
			key = ValStringUtil.getNotNullStr(dto.getBooksetIdTmpl())+
					ValStringUtil.getNotNullStr(dto.getBlnArea())+
					ValStringUtil.getNotNullStr(dto.getAssetType())+
					ValStringUtil.getNotNullStr(dto.getProfitType());
		}
		else if(DictCodeListExt1.资金账户类型_产品托管户.getValue().equals(dto.getAssetType())){
			//账套代码+业务明细类型
			key = ValStringUtil.getNotNullStr(dto.getBooksetIdTmpl())+
					ValStringUtil.getNotNullStr(dto.getAssetType());
		}
		return key;
	}
	
	/**
	 * 设置科目号、余额方向、原币余额、数量余额
	 * @param dto
	 * @param booksetId
	 * @param subjectType
	 * @return
	 */
	private BalanceTransplantDto getBalanceTransplantDto(BalanceTransplantDto dto,AssetBalanceDto template,String subjectType){
		String subjectNo = null;
		BigDecimal happenAmt = null;
		BigDecimal happenNumber = null;
		String balFlag = null;
		/**
		 * 成本科目 
		 * 资产类型为非债券的处理逻辑
		 *   原币余额 = 模板.初始本金
		 *   数量余额 = 模板.当前余额（份额）
		 * 资产类型为债券的处理逻辑
		 *   原币余额 = 模板.资产估值（成本法）
		 *   数量余额 = 模板.当前余额（份额）/100
		 */
		if(TransConstant.资产余额报表_成本科目.equals(subjectType)){
			subjectNo = template.getTrsSubjectRelation().getCbSubno();
			BokSubject bokSubject = dataTransplantCommonService.findSubjectInfo(template.getBooksetId(), subjectNo);
			balFlag = TransConstant.资产余额报表_科目余额方向.get(subjectType+template.getAssetType());
			if(DictCodeListExt1.金融产品类型_债券.getValue().equals(template.getAssetType())){
				//原币余额
				happenAmt = template.getShare();
				if(DictCodeList.是否_是.getValue().equals(bokSubject.getAccQuaFlag())){
					//数量余额
					happenNumber = ArithmeticUtil.divide(template.getShare(), new BigDecimal(100));
				}
				BigDecimal netValue = template.getNetValue()==null?new BigDecimal(100):template.getNetValue();
				netValue = ArithmeticUtil.divide(netValue, new BigDecimal(100));
				happenAmt = ArithmeticUtil.multiply(happenAmt,netValue);
			}
			else if((DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(template.getAssetType())
					|| DictCodeListExt1.金融产品类型_基金.getValue().equals(template.getAssetType()))
					&& DictCodeList.收益类型_净值型.getValue().equals(template.getProfitType())
					){
				//原币余额 = 成本法估值
				happenAmt = template.getAssetValuationCB();
				if(DictCodeList.是否_是.getValue().equals(bokSubject.getAccQuaFlag())){
					//数量余额 = 当前份额
					happenNumber = template.getShare();
				}
			}
			else{
				//原币余额
				happenAmt = template.getShare();
				if(DictCodeList.是否_是.getValue().equals(bokSubject.getAccQuaFlag())){
					//数量余额
					happenNumber = template.getShare();
				}
			}
		}
		/**
		 * 应计利息科目 
		 * 原币余额 = 模板.应收利息
		 */
		else if(TransConstant.资产余额报表_应计利息科目.equals(subjectType)){
			subjectNo = template.getTrsSubjectRelation().getYslxSubno();
			balFlag = TransConstant.资产余额报表_科目余额方向.get(subjectType+template.getAssetType());
			
			//原币余额
			happenAmt = template.getIntAmt();
		}
		/**
		 * 公允价值变动
		 * 原币余额 = 模板.公允价值
		 */
		else if(TransConstant.资产余额报表_公允价值变动.equals(subjectType)){
			subjectNo = template.getTrsSubjectRelation().getGyjzbdSubno();
			balFlag = TransConstant.资产余额报表_科目余额方向.get(subjectType+template.getAssetType());
			//原币余额
			happenAmt = template.getFairValue();
		}
		/**
		 * 利息调整
		 * 原币余额 = 资产估值-当前份额
		 */
		else if(TransConstant.资产余额报表_利息调整科目.equals(subjectType)){
			if("债券".equals(template.getTemplateAssetType())){
				subjectNo = template.getTrsSubjectRelation().getLxtzSubno();
				balFlag = TransConstant.资产余额报表_科目余额方向.get(subjectType+template.getAssetType());
				//原币余额
				BigDecimal netValue = template.getNetValue()==null?new BigDecimal(100):template.getNetValue();
				netValue = ArithmeticUtil.divide(netValue, new BigDecimal(100));
				
				happenAmt = ArithmeticUtil.subtract(template.getAssetValuationCB(), ArithmeticUtil.multiply(template.getShare(),netValue));
			}
		}
		if (!StringUtil.isNotEmpty(subjectNo)) {
			return null;
		}
		if(happenAmt==null || happenAmt.compareTo(BigDecimal.ZERO) == 0){
			return null;
		}
		
		BalanceTransplantDto newDto = dto.clone();
		//科目号
		newDto.setSubjectNo(subjectNo);
		//余额方向
		balFlag = balFlag+GlobalConstant.分号+
				InterfHelper.getDictColdeList_ItemName(DictCode.借贷方向.getValue(),balFlag);
		newDto.setCdFlag(balFlag);
		
		//原币余额
		newDto.setoHappenAmt(ArithmeticUtil.multiply(happenAmt,
				TransConstant.资产余额报表_科目余额金额.get(subjectType+template.getAssetType())));
		if(happenNumber != null){
			//数量余额
			newDto.setHappenNumber(ArithmeticUtil.multiply(happenNumber,
					TransConstant.资产余额报表_科目余额金额.get(subjectType+template.getAssetType())));
		}
		return newDto;
	}
	
	

	/**
	 * @return the subRelMap
	 */
	public Map<String, TrsSubjectRelation> getSubRelMap() {
		return subRelMap;
	}

	/**
	 * @param subRelMap the subRelMap to set
	 */
	public void setSubRelMap(Map<String, TrsSubjectRelation> subRelMap) {
		this.subRelMap = subRelMap;
	}

}
