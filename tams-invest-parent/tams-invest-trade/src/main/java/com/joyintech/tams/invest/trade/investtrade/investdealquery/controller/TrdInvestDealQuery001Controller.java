/*
 * 文件名：TrdInvestDealQuery001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangkang
 * 修改时间：2018年9月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.investtrade.investdealquery.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.trade.investtrade.investdealquery.dto.TrdDealQueryDto;
import com.joyintech.tams.invest.trade.investtrade.investdealquery.dto.TrdInvestDealQuery001Dto;
import com.joyintech.tams.invest.trade.investtrade.investdealquery.service.AssetTransferExport001Service;
import com.joyintech.tams.invest.trade.investtrade.investdealquery.service.TrdInvestDealQuery001Service;

/**
 * 交易查询页面
 * 〈功能详细描述〉
 * @author wangkang
 * @version 1.0 2018年9月29日
 * @see TrdInvestDealQuery001Controller
 * @since 1.0
 */
@Controller
@PageId("TrdInvestDealQuery001")
@RequestMapping("/invest/trade/investtrade/investdealquery/TrdInvestDealQuery001")
public class TrdInvestDealQuery001Controller extends BaseController{
	/**
	 * 交易查询service
	 */
	@Autowired
	private TrdInvestDealQuery001Service trdInvestDealQuery001Service;
	
	/**
	 * 转移定价service
	 */
	@Autowired
	private AssetTransferExport001Service assetTransferExport001Service;
	
	
	@Override
	@RequestMapping(PAGE_INIT)
	public String init(Model model) {
		// TODO Auto-generated method stub
		setSelectOptionToModel(model,DictCode.画面数据状态,DictCode.平安资产类型,DictCode.交易场所,DictCode.交易市场,DictCode.交易方向,DictCode.是否);
        model.addAttribute("PINAN_ASSET_TYPE_F01", DictCodeList.平安资产类型_债券.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F02", DictCodeList.平安资产类型_基金.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F03", DictCodeList.平安资产类型_计划类资产_利率型.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F04", DictCodeList.平安资产类型_计划类资产_净值型.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F05", DictCodeList.平安资产类型_存款.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F06", DictCodeList.平安资产类型_项目类资产.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F07", DictCodeList.平安资产类型_质押式回购.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F08", DictCodeList.平安资产类型_拆借.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F10", DictCodeList.平安资产类型_买断式回购.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F09", DictCodeList.平安资产类型_计划类资产_货币型.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F11", DictCodeListExt2.平安资产类型_股票.getValue());

		model.addAttribute("PINAN_ASSET_TYPE_F13", DictCodeListExt2.平安资产类型_外汇即期.getValue());
		model.addAttribute("PINAN_ASSET_TYPE_F14", DictCodeListExt2.平安资产类型_外汇远期.getValue());
		model.addAttribute("PINAN_ASSET_TYPE_F15", DictCodeListExt2.平安资产类型_外汇掉期.getValue());
		model.addAttribute("PINAN_ASSET_TYPE_F12", DictCodeListExt2.平安资产类型_债券借贷.getValue());
		model.addAttribute("PINAN_ASSET_TYPE_F16", DictCodeListExt2.平安资产类型_利率互换.getValue());
		model.addAttribute("PINAN_ASSET_TYPE_F17", BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
		model.addAttribute("PINAN_ASSET_TYPE_F18", DictCodeListExt3.平安资产类型_期权.getValue());
		// add by etliu 20210421 权益类资产相关代码合并 start
		model.addAttribute("PINAN_ASSET_TYPE_F19", DictCodeListExt3.平安资产类型_权益类资产.getValue());
		// add by etliu 20210421 权益类资产相关代码合并 end
		model.addAttribute("PINAN_ASSET_TYPE_F37", DictCodeList.平安资产类型_信用保护凭证.getValue());

		List<SelectOptionDto> psList = makeAllFloatRate();
        model.addAttribute("psList", JsonUtils.objectArray2Json(psList.toArray()));

		model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("PS_T04", DictCodeList.交易方向_申购.getValue());
        model.addAttribute("PS_T05", DictCodeList.交易方向_申购确认.getValue());
        model.addAttribute("PS_T06", DictCodeList.交易方向_赎回.getValue());
        model.addAttribute("PS_T19", DictCodeList.交易方向_赎回确认.getValue());
        model.addAttribute("PS_T24", DictCodeList.交易方向_赎回到账.getValue());
        model.addAttribute("PS_T49", DictCodeListExt3.交易方向_债转股.getValue());
        model.addAttribute("PS_T58", DictCodeListExt3.交易方向_退款到账.getValue());
        /* update by zhangpuyu 2019/12/10 start */
        model.addAttribute("PS_T52", DictCodeListExt2.交易方向_认购.getValue());
        model.addAttribute("PS_T18", DictCodeList.交易方向_认购确认.getValue());
        /* update by zhangpuyu 2019/12/10 end */
        model.addAttribute("IRS_TYPE", DictCode.互换方式.getValue());
        //add by yzg 是否查询资产减值清理交易 20201022 start
        model.addAttribute("IS_QUERY_ASSETS_LOSS", CacheUtil.getSysParam("IS_QUERY_ASSETS_LOSS"));
        //add by yzg 是否查询资产减值清理交易 20201022 end
        return super.init(model);
	}
	/**
    *
    * 组交易方向下拉框值
    * <功能详细描述>
    * @return
    * @see
    * @since 1.0
    */
   private List<SelectOptionDto> makeAllFloatRate() {
       List<SelectOptionDto> listFloatRate = new ArrayList<SelectOptionDto>();

       SelectDto selectDto1 = this.getSelectDataFromDataDic(DictCode.交易方向, null);
       for (SelectOptionDto dto : selectDto1.getOptions()) {
           listFloatRate.add(dto);
       }
       SelectDto selectDto2 = this.getSelectDataFromDataDic(DictCode.回购拆借方向, null);
       for (SelectOptionDto dto : selectDto2.getOptions()) {
           listFloatRate.add(dto);
       }
       SelectDto selectDto3 = this.getSelectDataFromDataDic(DictCode.互换方式, null);
       for (SelectOptionDto dto : selectDto3.getOptions()) {
           listFloatRate.add(dto);
       }
       //如果为是交易方向添加清理核销
       if(StringUtil.equals(CacheUtil.getSysParam("IS_QUERY_ASSETS_LOSS"), "01")) {
	       SelectDto selectDto = getSelectDataFromDataDic(DictCode.交易方向,null);
	       SelectOptionDto selectOptionDto = new SelectOptionDto();
	       selectOptionDto.setId(DictCodeListExt3.交易方向_清理核销.getValue());
	       selectOptionDto.setText(DictCodeListExt3.交易方向_清理核销.getCodeName());
	       selectOptionDto.setCol(DictCode.交易方向.getValue());
	       listFloatRate.add(selectOptionDto);
       }
       return listFloatRate;
   }
	/**
	 * 交易查询
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("findPage")
	public Page<TrdInvestDealQuery001Dto> selectForPage() {
		Map<String,Object> param = RequestUtil.getParameters(getRequest());
		return trdInvestDealQuery001Service.selectForPage(param);
	}
	
	/**
	 * 委托代码联想文本框
	 * @return
	 * @throws Exception
	 */
	 @ResponseBody
	 @RequestMapping("/getEntrustIds")
	 public List<SuggestDto> getEntrustIds() {
	 	String entrustId=super.getRequest().getParameter("param");
	 	return trdInvestDealQuery001Service.getEntIds(entrustId);
	 }
	/**
     * 组合代码联想文本框
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/getPortfolioIds")
    public List<SuggestDto> getPortfolioIds() {
    	String portfolioId=super.getRequest().getParameter("param");
    	return trdInvestDealQuery001Service.getPortfolioIds(portfolioId);
    }
    /**
     * 市场代码联想文本框
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/getFinprodMarketIds")
    public List<SuggestDto> getFinprodMarketIds() {
    	String finprodMarketId=super.getRequest().getParameter("param");
    	return trdInvestDealQuery001Service.getFinprodMarketIds(finprodMarketId);
    }
    /**
	 * 导出
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("export")
	public ResponseEntity<byte[]> export() {
	 	return trdInvestDealQuery001Service.export(this.getPageId(),RequestUtil.getParameters(getRequest()));
	}
	
	
	/**
	 * 导出
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("newSingleExportExcel")
	public ResponseEntity<byte[]> newSingleExportExcel() {
		
		Map<String,Object> param = RequestUtil.getPageParam();
        TrdDealQueryDto dto = JsonUtils.json2Object((String)param.get("dtoStr"),TrdDealQueryDto.class);
	 	
		return	assetTransferExport001Service.newSingleExportExcel(this.getPageId(),dto);
		
	}
	
	/**
	 * 导出
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("newBatchExportExcel")
	public ResponseEntity<byte[]> newBatchExportExcel() {
	 	//return assetTransferExport001Service.export(this.getPageId(),RequestUtil.getParameters(getRequest()));
//		Map<String, Object> pageParam = RequestUtil.getParameters(getRequest());
		
		Map<String,Object> param = RequestUtil.getPageParam();
        TrdDealQueryDto dto = JsonUtils.json2Object((String)param.get("dtoStr"),TrdDealQueryDto.class);

	 	return assetTransferExport001Service.newBatchExportExcel(this.getPageId(),dto);
	}
	
    /**
     * 导出check
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Valid TrdDealQueryDto dto, BindingResult result) {
        return commonCheck(result, "");
    } 

    /**
     * 单笔导出转移定价审批表
     *
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("/singleExport")
    public ResponseEntity<byte[]> singleExport()
         {
        Map<String,Object> param = RequestUtil.getPageParam();
        TrdDealQueryDto dto = JsonUtils.json2Object((String)param.get("dtoStr"),TrdDealQueryDto.class);
        return trdInvestDealQuery001Service.singleExport(dto);
    }
    
    /**
     *批量导出转移定价审批表
     *
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("/batchExportExcel")
    public ResponseEntity<byte[]> batchExportExcel()
         {
        Map<String,Object> param = RequestUtil.getPageParam();
        TrdDealQueryDto dto = JsonUtils.json2Object((String)param.get("dtoStr"),TrdDealQueryDto.class);
        return trdInvestDealQuery001Service.batchExportExcel(dto);
    }

	/**
	 * 导出pdf add by zengchengfa 20210803
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("exportPdf")
	public ResponseEntity<byte[]> exportPdf(){
		return trdInvestDealQuery001Service.exportPdf(this.getPageId(),RequestUtil.getPageParam());
	}
}
