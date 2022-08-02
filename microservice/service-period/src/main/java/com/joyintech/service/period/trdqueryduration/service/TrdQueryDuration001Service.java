/*
 * 文件名：TrdCommandDispatcher001Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangkang
 * 修改时间：2018年9月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.period.trdqueryduration.service;

import java.util.*;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.service.period.trdqueryduration.dao.TrdQueryDuration001Mapper;
import com.joyintech.service.period.trdqueryduration.dto.TrdQueryDurationDto;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;

@Service("backTrdQueryDuration001Service")
@Transactional(rollbackFor=Exception.class)
public class TrdQueryDuration001Service extends BaseService<TrdQueryDurationDto>{
	/**
	 * 手动书写mapper
	 */
	@Autowired
	private TrdQueryDuration001Mapper trdQueryDuration001Mapper;
	/**
	 * 标的类金融产品mapper
	 */
	@Autowired
	private FinProductMapper finProductMapper;

	@Autowired
	private IPlatformDate platformDateImpl;

	public Page<TrdQueryDurationDto> findPage(int page, int length, Map<String, Object> param) {
		List<String> statusValid = param.get("statusValid") == null ? null : (List)param.get("statusValid");
		setParams(param);
		this.setParamForFindAllData(param);
		param.put("isNewfundDealType",CacheUtil.getSysParam(BussinessConstant.FUND_DEAL_TYPE));
		Page<TrdQueryDurationDto> result = null;
		//update by wangcheng SHBS-2192 查询条件输入 资金到账日  ，有效状态选择 待处理  报错
		if(com.github.pagehelper.util.StringUtil.isNotEmpty((String)param.get("tradeDateStart"))&&((statusValid.size()==1) && statusValid.contains("01"))){
			result = new Page<TrdQueryDurationDto>();
		}else{
			result = queryForPageForSelectReturn(page, length, () -> trdQueryDuration001Mapper.selectForPage(param));
		}
		return result;
	}

	private void setParams(Map<String, Object> param) {
		param.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
		param.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
		param.put("PAGE_DATE_STATUS01", DictCodeListExt1.画面数据状态_待处理.getValue());
		param.put("PAGE_DATE_STATUS03", DictCodeListExt1.画面数据状态_流程中.getValue());
		param.put("PAGE_DATE_STATUS05", DictCodeListExt1.画面数据状态_有效.getValue());
		param.put("PAGE_DATE_STATUS06", DictCodeListExt1.画面数据状态_撤销.getValue());
		param.put("CASH_CLASS_TYPE01", DictCodeList.现金流类型_中间收息.getValue());
		param.put("CASH_CLASS_TYPE02", DictCodeList.现金流类型_中间还本.getValue());
		param.put("CASH_CLASS_TYPE03", DictCodeList.现金流类型_到期还本.getValue());
		param.put("CASH_CLASS_TYPE04", DictCodeList.现金流类型_到期收息.getValue());
		// 数据字典缓存.交易状态.初始
		param.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
		param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
		param.put("YESNO_N", DictCodeList.是否_否.getValue());
		param.put("YESNO_Y", DictCodeList.是否_是.getValue());
		// 数据字典缓存.金融产品类型 投管分类.债券
		param.put("FINPROD_TYPE_TGF01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
		param.put("TRD10", DictCodeList.共通业务类型_债券类资产存续期.getValue());
		param.put("STATUS_N", DictCodeList.数据状态_新建.getValue());
		param.put("adjustMDate", DateUtil.parse((String)param.get("mDate")));
		String plateDate = platformDateImpl.getSysDateStr();
		param.put("PLATEFORM_DATE", DateUtil.parse(plateDate));
		if (param.get(BussinessConstant.PLUS_DAYS) != null) {
			param.put("PLATEFORM_DATE_OTHER", DateUtil.plusDays(plateDate, (Integer)param.get(BussinessConstant.PLUS_DAYS)));
		}
		else {
			param.put("PLATEFORM_DATE_OTHER", DateUtil.plusDays(plateDate, 90));
		}
		if(MultilevelDataAuthorityUtil.isEnabled()) {
			//当前用户所属机构代码
			String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
			param.put("institutionCode",createInst);
			//获取当前用户的机构树
			List<String> instCode = LoginUserUtil.getTreeInstCode();
			param.put("isMultilevel", true);
			param.put("instTree", instCode);
		}
		param.put("userName",LoginUserUtil.getUserName());
		param.put("COMMON_BUSINESS_TYPETRD16", DictCodeList.共通业务类型_质押式回购存续期.getValue());
		param.put("COMMON_BUSINESS_TYPETRD08", DictCodeList.业务类型_债券质押式回购交易.getValue());
		param.put("FINPROD_TYPE_TGF03", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
		param.put("FINPROD_TYPE_TGF02", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
		param.put("PS_T01", DictCodeList.交易方向_买入.getValue());
		param.put("PS_T07", DictCodeList.交易方向_还本到账.getValue());
		param.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
		param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
		param.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
		param.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
		param.put("C02", DictCodeListExt1.现金流二级类型_利息.getValue());
		param.put("PST07", DictCodeList.交易方向_到期.getValue());
		param.put("PST56", DictCodeList.交易方向_到期续做.getValue());
		param.put("PS_TQZZ", DictCodeListExt3.交易方向_投资端_提前终止.getValue());
		param.put("PS_YQFS", DictCodeListExt3.交易方向_投资端_逾期返售.getValue());
		param.put("PST22", DictCodeList.交易方向_收息到账.getValue());
		param.put("PST23", DictCodeList.交易方向_还本到账.getValue());
		param.put("PST24", DictCodeList.交易方向_赎回到账.getValue());
		param.put("FEE_TYPE_JSF", DictCodeListExt1.一次性费用类型_结算费.getValue());
		param.put("SysDate", platformDateImpl.getSysDate());
		param.put("TRADE_PLACE_01",DictCodeListExt1.交易场所_银行间.getValue());

		/* 现金流二级类型.资产推荐费 */
		param.put("CASH_TYPE_2_C08", DictCodeListExt1.现金流二级类型_资产推荐费.getValue());
		param.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
		param.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
		// 一览查询bug对应 add by renping 20191027 start
		param.put("PS_T38", DictCodeList.交易方向_提前支取.getValue());
		// 一览查询bug对应 add by renping 20191027 end
		// 数据字典缓存.交易状态.初始
		param.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
		param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
		param.put("YESNO_N", DictCodeList.是否_否.getValue());
		param.put("YESNO_Y", DictCodeList.是否_是.getValue());
		// 数据字典缓存.持仓类型.实际持仓
		param.put("POSITION_TYPE03", DictCodeList.持仓类型_实际.getValue());
		// 数据字典缓存.金融产品类型_投管用_存款
		param.put("FINPROD_TYPE_TGF07", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
		param.put("TRD10", DictCodeList.共通业务类型_债券类资产存续期.getValue());
		param.put("STATUS_A", DictCodeList.数据状态_新建.getValue());
		param.put("ACCT_TYPE_A01", DictCodeListExt1.资金账户类型_产品托管户.getValue());
		param.put("PS_C7", DictCodeList.交易方向_中间付息.getValue());
		param.put("COMMON_BUSINESS_TYPETRD11", DictCodeList.共通业务类型_拆借存续期交易.getValue());
		param.put("FINPROD_TYPE_TGF08", DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
		param.put("FINPROD_TYPE_TGF09", DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
		param.put("FINPROD_TYPE_TGF25", DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue());
		param.put("PS_T01", DictCodeList.交易方向_买入.getValue());
		param.put("PS_T07", DictCodeList.交易方向_到期.getValue());
		param.put("PS_T09", DictCodeList.交易方向_收息.getValue());
		param.put("PS_HBDZ", DictCodeList.交易方向_还本到账.getValue());
		param.put("PST07", DictCodeList.交易方向_到期.getValue());
		param.put("COM_TYPE_TRD03", DictCodeList.业务类型_拆借交易.getValue());

		// 数据字典缓存.持仓类型.实际持仓
		param.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
		param.put("finprodTypeTg", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
		param.put("PS_T11", DictCodeList.交易方向_现金分红.getValue());
		param.put("PS_T12", DictCodeList.交易方向_红利再投.getValue());
		param.put("PS_T13", DictCodeList.交易方向_除权登记.getValue());
		param.put("FUND_ASSET_TYPE_F01", DictCodeList.基金类资产类型_货币基金.getValue());
		param.put("FUND_ASSET_TYPE_F02", DictCodeList.基金类资产类型_非货币基金.getValue());
		param.put("STATUS_A", DictCodeList.数据状态_新建中.getValue());
		param.put("BONUS_MODE_01", DictCodeList.分红方式_现金分红.getValue());
		param.put("BONUS_MODE_02", DictCodeList.分红方式_红利再投.getValue());
		param.put("BUSI_TABLE", "TRD_PRODUCT_DEAL");
		param.put("ACCT_TYPE_A01", DictCodeListExt1.资金账户类型_产品托管户.getValue());
		param.put("PROFIT_TYPE_JZ", DictCodeList.收益类型_净值型.getValue());
		param.put("FINPROD_TYPE_TG_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
		param.put("finprodTypeTg", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
		param.put("PROFIT_TYPE_HB", DictCodeList.收益类型_货币型.getValue());

		param.put("CASH_CLASS_TYPE01", DictCodeList.现金流类型_中间收息.getValue());
		param.put("CASH_CLASS_TYPE02", DictCodeList.现金流类型_中间还本.getValue());
		param.put("CASH_CLASS_TYPE03", DictCodeList.现金流类型_到期还本.getValue());
		param.put("CASH_CLASS_TYPE04", DictCodeList.现金流类型_到期收息.getValue());
		param.put("PS_T07", DictCodeList.交易方向_到期.getValue());
		param.put("PS_T08", DictCodeList.交易方向_还本.getValue());
		param.put("PS_T09", DictCodeList.交易方向_收息.getValue());
		param.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
		param.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
		// 数据字典缓存.持仓类型.实际持仓
		param.put("POSITION_TYPE03", DictCodeList.持仓类型_实际.getValue());
		param.put("FINPROD_TYPE_TGF17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
		param.put("TRD15", DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue());
		param.put("PROFIT_TYPE02", DictCodeList.收益类型_收益型.getValue());

		param.put("COMMON_BUSINESS_TYPETRD16", DictCodeList.共通业务类型_买断式回购存续期.getValue());
		param.put("COMMON_BUSINESS_TYPETRD08", DictCodeList.共通业务类型_买断式回购交易.getValue());
		param.put("FINPROD_TYPE_TGF05", DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
		param.put("FINPROD_TYPE_TGF04", DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());

		// 数据字典缓存.持仓类型.实际持仓
		//param.put("POSITION_TYPE03", DictCodeList.持仓类型_实际.getValue());
		param.put("FINPROD_TYPE_TGF18", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
		param.put("TRD17", DictCodeList.共通业务类型_项目类资产存续期.getValue());

		param.put("COMMON_BUSINESS_TYPETRD51", DictCodeListExt2.共通业务类型_债券借贷交易.getValue());
		param.put("FINPROD_TYPE_TGF29", DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
		param.put("FINPROD_TYPE_TGF30", DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
		param.put("PS_T01", DictCodeList.交易方向_买入.getValue());
		param.put("C02", DictCodeListExt1.现金流二级类型_利息.getValue());
		param.put("F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
		param.put("FINPROD_TYPE_TGF14", DictCodeListExt1.金融产品类型_投管用_利率互换.getValue());

		param.put("psR", DictCodeList.交易方向_收取.getValue());
		param.put("psP", DictCodeList.交易方向_支付.getValue());
		param.put("irsType01", DictCodeListExt2.互换方式_收取固定.getValue());
		param.put("irsType02", DictCodeListExt2.互换方式_收取浮动.getValue());
		param.put("pridBussType01", DictCodeListExt2.利率互换存续期类型_中间收付息.getValue());
		param.put("pridBussType02", DictCodeListExt2.利率互换存续期类型_到期.getValue());
		param.put("pridBussType02", DictCodeListExt2.利率互换存续期类型_到期.getValue());
		param.put("irsPsIntType01", DictCodeListExt2.利率互换收付息类型_收取固定.getValue());
		param.put("irsPsIntType02", DictCodeListExt2.利率互换收付息类型_支付固定.getValue());
		param.put("irsPsIntType03", DictCodeListExt2.利率互换收付息类型_收取浮动.getValue());
		param.put("irsPsIntType04", DictCodeListExt2.利率互换收付息类型_支付浮动.getValue());
		param.put("irsPsIntType05", DictCodeListExt2.利率互换收付息类型_收取固定_支付浮动.getValue());
		param.put("irsPsIntType06", DictCodeListExt2.利率互换收付息类型_收取浮动_支付固定.getValue());
		param.put("cashBussType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
		param.put("rateTypeFix", DictCodeList.利率类型_固定利率.getValue());
		param.put("rateTypeFloat", DictCodeList.利率类型_浮动利率.getValue());
		param.put("busiTable", DictCodeList.业务表_金融产品表.getValue());
		param.put("irsType01", DictCodeListExt2.互换方式_收取固定.getValue());
		param.put("TG_BUSI_TYPE_beforeEnd", DictCodeListExt2.平安业务类型_利率互换提前终止.getValue());
		param.put("F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
		param.put("F35", DictCodeListExt1.金融产品类型_投管用_期权.getValue());
		param.put("PS_T59", DictCodeListExt1.交易方向_提前行权.getValue());
		param.put("PS_T60", DictCodeListExt1.交易方向_到期行权.getValue());
		param.put("PS_T61", DictCodeListExt1.交易方向_放弃行权.getValue());
		param.put("PAGE_DATA_STATUS01", DictCodeListExt1.画面数据状态_待处理.getValue());
		param.put("PAGE_DATA_STATUS02", DictCodeListExt1.画面数据状态_流程中.getValue());
		param.put("PAGE_DATA_STATUS03", DictCodeListExt1.画面数据状态_有效.getValue());
		param.put("PAGE_DATA_STATUS04", DictCodeListExt1.画面数据状态_撤销.getValue());
		param.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
	}

	private void setParamForFindAllData(Map<String, Object> param) {
		List<String> statusValid = param.get("statusValid") == null ? null : (List)param.get("statusValid");

		String cashType = param.get("cashType") == null ? null : (String)param.get("cashType");
		param.put("paBizType", cashType);
		StringBuilder cashTypeSb = new StringBuilder();
		if (cashType != null) {
			if (cashType.contains(DictCodeListExt1.平安业务类型_债券中间收息.getValue())
					|| cashType.contains(DictCodeListExt1.平安业务类型_债券中间还本.getValue())) {
				cashTypeSb.append(DictCodeList.现金流类型_中间收息.getValue());
				cashTypeSb.append(DictCodeList.现金流类型_中间还本.getValue());
			}
			if (cashType.contains(DictCodeListExt1.平安业务类型_债券到期.getValue())) {
				cashTypeSb.append(DictCodeList.现金流类型_到期收息.getValue());
				cashTypeSb.append(DictCodeList.现金流类型_到期还本.getValue());
			}

			if (cashType.contains(DictCodeListExt1.平安业务类型_债券中间收息.getValue())) {
				param.put("cashType", DictCodeList.现金流类型_中间收息.getValue());
			}
			else if (cashType.contains(DictCodeListExt1.平安业务类型_债券到期.getValue())) {
				param.put("cashType", DictCodeList.现金流类型_到期还本.getValue());
			}
			else if (cashType.contains(DictCodeListExt1.平安业务类型_债券中间还本.getValue())) {
				param.put("cashType", DictCodeList.现金流类型_中间还本.getValue());
			}
			if (cashType.contains(DictCodeListExt1.平安业务类型_计划类资产_利率型_中间付息.getValue())
					|| cashType.contains(DictCodeListExt1.平安业务类型_计划类资产_利率型_中间还本.getValue())) {
				cashTypeSb.append(DictCodeList.现金流类型_中间收息.getValue());
				cashTypeSb.append(DictCodeList.现金流类型_中间还本.getValue());
			}
			if (cashType.contains(DictCodeListExt1.平安业务类型_计划类资产_利率型_到期.getValue())) {
				cashTypeSb.append(DictCodeList.现金流类型_到期收息.getValue());
				cashTypeSb.append(DictCodeList.现金流类型_到期还本.getValue());
			}
			if (cashType.contains(DictCodeListExt1.平安业务类型_计划类资产_利率型_中间付息.getValue())) {
				param.put("cashType", DictCodeList.现金流类型_中间收息.getValue());
			}
			else if (cashType.contains(DictCodeListExt1.平安业务类型_计划类资产_利率型_中间还本.getValue())) {
				param.put("cashType", DictCodeList.现金流类型_中间还本.getValue());
			}
			else if (cashType.contains(DictCodeListExt1.平安业务类型_计划类资产_利率型_到期.getValue())) {
				param.put("cashType", DictCodeList.现金流类型_到期还本.getValue());
			}

			if (cashType.contains(DictCodeListExt1.平安业务类型_项目类资产中间付息.getValue())
					|| cashType.contains(DictCodeListExt1.平安业务类型_项目类资产中间还本.getValue())) {
				cashTypeSb.append(DictCodeList.现金流类型_中间收息.getValue());
				cashTypeSb.append(DictCodeList.现金流类型_中间还本.getValue());
			}
			if (cashType.contains(DictCodeListExt1.平安业务类型_项目类资产到期.getValue())) {
				cashTypeSb.append(DictCodeList.现金流类型_到期收息.getValue());
				cashTypeSb.append(DictCodeList.现金流类型_到期还本.getValue());
			}
			if (cashType.contains(DictCodeListExt1.平安业务类型_项目类资产中间付息.getValue())) {
				param.put("cashType", DictCodeList.现金流类型_中间收息.getValue());
			}
			else if (cashType.contains(DictCodeListExt1.平安业务类型_项目类资产中间还本.getValue())) {
				param.put("cashType", DictCodeList.现金流类型_中间还本.getValue());
			}
			else if (cashType.contains(DictCodeListExt1.平安业务类型_项目类资产到期.getValue())) {
				param.put("cashType", DictCodeList.现金流类型_到期还本.getValue());
			}
			param.put("cashTypeSb", cashTypeSb.toString());
			param.put("cashTypeSb", cashTypeSb.toString());
		}
	}


	
	/*private void setParams(Map<String, Object> pageParam) {
        pageParam.put("LEND_01", DictCodeList.回购拆借方向_拆入.getValue());
        pageParam.put("LEND_02", DictCodeList.回购拆借方向_拆出.getValue());
        pageParam.put("LEND_03", DictCodeList.回购拆借方向_正回购.getValue());
        pageParam.put("LEND_04", DictCodeList.回购拆借方向_逆回购.getValue());
        pageParam.put("LEND_05", DictCodeList.回购拆借方向_融入.getValue());
        pageParam.put("LEND_06", DictCodeList.回购拆借方向_融出.getValue());
        pageParam.put("F08", DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
        pageParam.put("F09", DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
        pageParam.put("F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        pageParam.put("F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        pageParam.put("F04", DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
        pageParam.put("F05", DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
        pageParam.put("F29", DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
        pageParam.put("F30", DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
    }*/

	/**
	 * 证券账户/合作资产联想框
	 * @return
	 */
	public Page<SelectOptionDto> getSecAcctIdAndChlIds(int page, int length, Map<String, Object> param) {
		param.put("Y", DictCodeList.是否_是.getValue());
		param.put("F28", DictCodeListExt1.金融产品类型_投管用_通道.getValue());
		return queryForPageForSelectReturn(page, length, () -> trdQueryDuration001Mapper.getSecAcctIdAndChlIds(param));
	}

	/**
	 * 资产代码联想框
	 * @param portfolidId
	 * @return
	 */
	public List<SuggestDto> getFinprodMarketIds(String portfolidId){
		return trdQueryDuration001Mapper.getFinprodMarketIds(portfolidId);
	}

	/**
	 * 组合代码联想框
	 * @param portfolioId
	 * @return
	 */
	public Page<SelectOptionDto> getPortfolioIds(int page, int length, Map<String, Object> map){
		map.put("portType", DictCodeListExt1.组合类型_投资类组合.getValue());
		map.put("portType1", DictCodeListExt1.组合类型_子组合.getValue());
		return super.queryForPageForSelectReturn(page, length, () -> trdQueryDuration001Mapper.getPortfolioIds(map));
	}
	
	/**
	 * 获取资产标的Type6
	 * @param finprodId
	 * @return
	 */
	public String getType6(String finprodId) {
		return trdQueryDuration001Mapper.getType6(finprodId);
	}
}
