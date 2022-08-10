/*
 * 文件名：TrdInvestDealQuery001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangkang
 * 修改时间：2018年9月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.period.trdqueryduration.controller;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.period.trdqueryduration.dto.TrdQueryDuration001Dto;
import com.joyintech.tams.invest.period.trdqueryduration.dto.TrdQueryDurationDto;
import com.joyintech.tams.invest.period.trdqueryduration.service.TrdQueryDuration001Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 存续期交易查询页面
 * 〈功能详细描述〉
 * @author wangyang1
 * @version 1.0 2021年2月1日
 * @see TrdQueryDuration001Controller
 * @since 1.0
 */
@Controller
@PageId("TrdQueryDuration001")
@RequestMapping("/invest/period/trdqueryduration/TrdQueryDuration001")
public class TrdQueryDuration001Controller extends BaseController{
	/**
	 * 交易查询service
	 */
	@Autowired
	private TrdQueryDuration001Service trdQueryDuration001Service;

	@Override
	@RequestMapping(PAGE_INIT)
	public String init(Model model) {
		setSelectOptionToModel(model,DictCode.画面数据状态,DictCode.平安资产类型,DictCode.平安业务类型);
		//XYHGFXSJ-1001 【存续期交易查询】查询条件业务类型缺少
		List<SelectOptionDto> PA_TYPES = JSON.parseArray(Objects.requireNonNull(model.getAttribute(DictCode.平安业务类型.getValue())).toString(),SelectOptionDto.class);
        List<? extends DataDictItem> items = CacheUtil.getDictCode(DictCode.投管业务类型.getValue()).getItems();
        for (DataDictItem item : items) {
            PA_TYPES.add(new SelectOptionDto(){{
                setId(item.getItemCode());
                setText(item.getItemValue());
                setCol(DictCode.平安业务类型.getValue());
            }});
        }
        //XYHGFXSJ-1001 【存续期交易查询】查询条件业务类型缺少
        model.addAttribute(DictCode.平安业务类型.getValue(),JSON.toJSONString(PA_TYPES));
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
		model.addAttribute("CHANCLE_STATUS",DictCodeListExt1.画面数据状态_撤销.getValue());
		model.addAttribute("DEPO_ASSET_TYPE_03", DictCodeListExt3.存款类资产类型_活期存放.getValue());
        model.addAttribute("FUND_DEAL_TYPE", CacheUtil.getSysParam(BussinessConstant.FUND_DEAL_TYPE));
		List<SelectOptionDto> psList = makeAllFloatRate();
        model.addAttribute("psList", JsonUtils.objectArray2Json(psList.toArray()));
        model.addAttribute("platDate", DateUtil.format(DateUtil.platDateFromCache())+" - "+DateUtil.format(DateUtil.platDateFromCache()));
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
       return listFloatRate;
   }
	/**
	 * 交易查询
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("findPage")
	public Page<TrdQueryDuration001Dto> selectForPage() {
		Map<String,Object> param = RequestUtil.getParameters(getRequest());
		return trdQueryDuration001Service.selectForPage(param);
	}

    /**
     * 组合代码联想文本框
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/getPortfolioIds")
    public Page<SelectOptionDto> getPortfolioId() {
        return trdQueryDuration001Service.getPortfolioId(RequestUtil.getPageParam());
    }

    /**
     * 证券账户联想文本框
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/getSecAcctIdAndChlIds")
    public Page<SelectOptionDto> getSecAcctIdAndChlIds(){
        Page<SelectOptionDto> page = trdQueryDuration001Service.getSecAcctIdAndChlIds(RequestUtil.getPageParam());
        return page;
    }

    /**
     * 资产代码联想文本框
     *
     * @param finprodMarketId
     * @return
     * @throws Exception
     */
    @GetMapping("/getFinprodMarketIds")
    public List<SuggestDto> getFinprodMarketIds(String finprodMarketId){
        return trdQueryDuration001Service.getFinprodMarketIds(finprodMarketId);
    }


    /**
	 * 导出
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("export")
	public ResponseEntity<byte[]> export() {
	 	return trdQueryDuration001Service.export(this.getPageId(),RequestUtil.getParameters(getRequest()));
	}


    /**
     * 导出check
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Valid TrdQueryDurationDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }


    @ResponseBody
    @RequestMapping("getType6")
    public String getType6(@RequestParam("finprodId") String finprodId) {
    	return trdQueryDuration001Service.getType6(finprodId);
    }

}
