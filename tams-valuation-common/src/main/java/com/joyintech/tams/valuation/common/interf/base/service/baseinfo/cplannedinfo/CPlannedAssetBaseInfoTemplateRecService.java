/*
 * 文件名：CPlannedAssetBaseInfoTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改时间：2019年1月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.common.interf.base.service.baseinfo.cplannedinfo;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.CPlannedAssetBaseInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMngMnfBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计划类资产(货币型)基础信息模板处理 (ValMngMnfBaseInfo) 
 * 
 * @author zhangzhao
 * @version 1.0 2019年1月22日
 * @see CPlannedAssetBaseInfoTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class CPlannedAssetBaseInfoTemplateRecService {

	@Autowired
	private TemplateAnalysisForXMLService templateAnalysisForXMLService;

	@Autowired
	private InterfCommonService interfCommonService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CPlannedAssetBaseInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
			TemplateOrderInfoReaderResult acctEntity) {
		// 定义返回变量
		CPlannedAssetBaseInfoValCommonResult result = new CPlannedAssetBaseInfoValCommonResult();
		List<ValMngMnfBaseInfo> listValMngMnfBaseInfo= new ArrayList<ValMngMnfBaseInfo>();

		// 解析模板后的list以及日志list
		TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();

		// 1、文件读取解析
	    returnResult = templateAnalysisForXMLService.execute(acctEntity);

		// 2、进行数据有效性的check,并生出进入日志表的数据结构
		listValMngMnfBaseInfo = returnResult.getT(ValMngMnfBaseInfo.class);

		// 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
		Map<String, Object> map = handleValPrdInfoC(listValMngMnfBaseInfo );

		// 判断返回结果不为空
		if (map != null) {
			//标的类金融产品表
			List<FinProduct> listValFinProduct = (List<FinProduct>) map.get("ValFinProduct");
		    //标的类金融产品扩展表
			List<FinProductAdd> listValFinProductExt = (List<FinProductAdd>) map.get("ValFinProductExt");
			//金融产品分类表
			List<FinProductType> listValFinProductType = (List<FinProductType>) map.get("ValFinProductType");
			//金融产品估值参数表
			List<FinProductValuationPara> listValFinProductValuationPara = (List<FinProductValuationPara>) map.get("ValFinProductValuationPara");
            
			result.setListValFinProduct(listValFinProduct);
			result.setListValFinProductExt(listValFinProductExt);
			result.setListValFinProductType(listValFinProductType);
			result.setListValFinProductValuationPara(listValFinProductValuationPara);
		}
		// 添加日志
		result.setListValInterflog(returnResult.getValInterflog());
		return result;

	}

	/**
	 * 基础信息 
	 * @param listValMngMnfBaseInfo
	 * @return 
	 */
	private Map<String, Object> handleValPrdInfoC(List<ValMngMnfBaseInfo> listValMngMnfBaseInfo) {
		Map<String, Object> resultMap = new HashMap<>();
		List<FinProduct> valFinProductList = new ArrayList<FinProduct>();
		List<FinProductAdd> valFinProductExtList = new ArrayList<FinProductAdd>();
		List<FinProductType> valFinProductTypeList = new ArrayList<FinProductType>();
		List<FinProductValuationPara> valFinProductValuationParaList = new ArrayList<FinProductValuationPara>();
        
		// 获取基础信息Dto
		for (ValMngMnfBaseInfo dto : listValMngMnfBaseInfo) {
			// 标的类金融产品 处理
			FinProduct valFinProduct = getValFinProduct(dto);
			valFinProductList.add(valFinProduct);
			
			// 标的类金融产品扩展信息 待处理
			FinProductAdd valFinProductExt = getValFinProductExt(dto);
			 valFinProductExtList.add(valFinProductExt);
			 
			// 金融产品分类表
			 FinProductType valFinProductType = getValFinProductType(dto);
			 valFinProductTypeList.add(valFinProductType);
			// 金融产品估值参数表
			 FinProductValuationPara valFinProductValuationPara = getValFinProductValuationPara(dto);
			 valFinProductValuationParaList.add(valFinProductValuationPara);
		}
		resultMap.put("ValFinProduct", valFinProductList);
		resultMap.put("ValFinProductExt", valFinProductExtList);
		resultMap.put("ValFinProductType", valFinProductTypeList);
		resultMap.put("ValFinProductValuationPara", valFinProductValuationParaList);
       
		return resultMap;
	}

	/**
	 * 标的类金融产品表 接收 
	 * @param dto
	 * @return 标的类金融产品
	 */
	private FinProduct getValFinProduct(ValMngMnfBaseInfo dto) {
		FinProduct valFinProduct = new FinProduct();
		// 金融产品代码 = A.资产代码
		valFinProduct.setFinprodId(dto.getAssetId());
		// 金融产品类型 = 固定值(F17：计划类资产)
		valFinProduct.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
		// 金融产品简称= A.资产名称
		valFinProduct.setFinprodAbbr(dto.getAssetName());
		// 金融产品全称= A.资产名称
		valFinProduct.setFinprodName(dto.getAssetName());
		// 收益类型= 收益类型.货币
		valFinProduct.setProfitType(DictCodeList.收益类型_货币型.getValue());
		// 市场代码= A.市场代码
		valFinProduct.setFinprodMarketId(dto.getMarketCode());
		// 币种= A.币种
		valFinProduct.setCcy(dto.getCcy());
		// 境内外= A.境内外
		valFinProduct.setBlnArea(dto.getBlnArea());
		// 交易场所 = A.交易场所
		valFinProduct.setTradeMarket(dto.getTradeMarket());
		// 交易日历= 
		valFinProduct.setCalendarId(interfCommonService.getCalendarId(dto.getTradeMarket()));
		//发行日= A.发行日
		valFinProduct.setIdate(dto.getIdate());
		// 原金融产品代码 = A.资产代码
		valFinProduct.setoFinprodId(dto.getAssetId());
		// 原金融产品类型 = F17：计划类资产
//		valFinProduct.setoFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
		//本金收益特征= A.本金收益特征
		valFinProduct.setCapiIncomeFeature(dto.getCapiProfFeature());

		//add by liupengfei for 多级法人改造 S
		valFinProduct.setBelongInstitutionCode(dto.getBelongInstitutionCode());
		//add by liupengfei for 多级法人改造 E
		return valFinProduct;
	}

	/**
	 * 标的类金融产品扩展表 接收
	 * @param dto
	 * @return 标的类金融产品扩展表
	 */
	private FinProductAdd getValFinProductExt(ValMngMnfBaseInfo dto) {
		// 标的类金融产品扩展表
		FinProductAdd valFinProductExt = new FinProductAdd();		
		//金融产品代码 = A.资产代码
		valFinProductExt.setFinprodId(dto.getAssetId());
		// 金融产品类型 = 固定值(F17：计划类资产)
//		valFinProductExt.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue()); 一体化表字段不存在
		//基金结转频率= A.结转频率
//		valFinProductExt.setCarryoverFreq(dto.getCarryoverFreq()); 一体化表字段不存在
		//申购确认速度= A.申购确认速度
		valFinProductExt.setPurSpeed(dto.getPurSpeed());
		//赎回确认速度= A.赎回确认速度
		valFinProductExt.setRedSpeed(dto.getRedSpeed());
		//是否按日复利= A.是否按日复利
		valFinProductExt.setIsCompoundInt(dto.getIsCompoundInt());
		
		return valFinProductExt;
	}
	
	/**
	 * 金融产品分类表 接收
	 * @param dto
	 * @return 金融产品分类表
	 */
	private FinProductType getValFinProductType(ValMngMnfBaseInfo dto) {
		// 金融产品分类表
		FinProductType valFinProductType = new FinProductType();	
		// 金融产品代码= A.资产代码
		valFinProductType.setFinprodId(dto.getAssetId());
		// 金融产品类型= 固定值(F17：计划类资产)
//		valFinProductType.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
		// 分类1= A.资产类型
		valFinProductType.setType1(dto.getAssetType());
		// 分类2 = 调用共通方法查询核算分类
        FinProductClass fdto = new FinProductClass();
        // 金融产品类型
        fdto.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
        // 资产类型
        fdto.setAssetType(dto.getAssetType());
        /**modify by wujie on 20210510
        // 交易场所
        fdto.setTradeMarket(dto.getTradeMarket());
		 **/
        //境内外
        fdto.setBlnArea(dto.getBlnArea());
		/*add by sunshuaishuai 计划类资产添加收益类型区分 20210315 start*/
		fdto.setProfitType(DictCodeList.收益类型_货币型.getValue());
		/*add by sunshuaishuai 计划类资产添加收益类型区分 20210315 end*/
        // 分类2= 调用共通核算分类查询
		valFinProductType.setType2(interfCommonService.getFinanceProductClass(fdto)); 
		// 分类3= 调用共通方法查询税务分类
		valFinProductType.setType3(interfCommonService.getFinanceProductTaxClass(fdto));
		valFinProductType.setBranch(InterfCommonConstant.分支序号_默认);
		// 分类18 = 调用共通方法查询证券品种 add by yanya on 20210828
		valFinProductType.setType18(interfCommonService.getFinanceProductSecType(fdto));
		return valFinProductType;
	}
	
	
	/**
	 * 金融产品估值参数表 接收
	 * @param dto
	 * @return 金融产品估值参数表
	 */
	private FinProductValuationPara getValFinProductValuationPara(ValMngMnfBaseInfo dto) {
		// 金融产品估值参数表
		FinProductValuationPara valFinProductValuationPara = new FinProductValuationPara();		
		// 金融产品代码= A.资产代码
		valFinProductValuationPara.setFinprodId(dto.getAssetId());
		// 分支序号= 固定值 设置全局变量(默认为0）
		valFinProductValuationPara.setBranch(InterfCommonConstant.分支序号_默认);
		// 净值数据延迟天数= A.净值数据延迟天数
		valFinProductValuationPara.setIntAccrualMode(dto.getIntAccrualMode());
		
		return valFinProductValuationPara;
	}
	
}
