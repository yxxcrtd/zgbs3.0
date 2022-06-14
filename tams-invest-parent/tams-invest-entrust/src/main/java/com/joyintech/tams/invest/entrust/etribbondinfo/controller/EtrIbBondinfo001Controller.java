/*
 * 文件名：EtrTradePlace001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etribbondinfo.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.entrust.etrexchangetrade.service.EtrExchangeTrade002Service;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.EtrFinProductDto;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.EtrIbBondinfo001Dto;
import com.joyintech.tams.invest.entrust.etribbondinfo.service.EtrIbBondinfo001Service;
import com.joyintech.tams.invest.entrust.home.service.HomeService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 银行间债券指令
 * @author chengzhong
 * @version 1.0 2021年1月19日
 * @see EtrIbBondinfo001Controller
 * @since 1.0
 */

@Controller
@PageId("EtrIbBondinfo001")
@RequestMapping("/invest/entrust/etribbondinfo/EtrIbBondinfo001")
public class EtrIbBondinfo001Controller extends BaseController{


    /**
     * 外汇交易中心CMDS现券行情接口地址
     */
    @Value("${cmdsWebSocketUrl}")
    private String cmdsWebSocketUrl;

    /**
     * 债券持仓查询
     */
    @Autowired
    private EtrIbBondinfo001Service etrIbBondinfo001Service;

    /**
     * 可转债委托处理详细
     */
    @Autowired
    private EtrExchangeTrade002Service etrExchangeTrade002Service;

    @Autowired
    private HomeService homeService;
    
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.WIND一级分类_含债券一级分类等);
        this.setSelectOptionToModel(model, DictCode.金融产品类型_投管用);
        this.setSelectOptionToModel(model, DictCode.交易方向);
        this.setSelectOptionToModel(model, DictCode.委托状态);
        this.setSelectOptionToModel(model, DictCode.价格模式);
        this.setSelectOptionToModel(model, DictCode.清算速度);
        model.addAttribute("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());

        // 定制化配置信息控制页面展示
        model.addAttribute("DYN_INFO", homeService.getChecked("EtrIbBondinfoMain"));
        return super.init(model);
    }
    
    /**
     * 查询持仓
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPositionsPage")
    public Page<EtrIbBondinfo001Dto> findPositionsPage(){
        return etrIbBondinfo001Service.findPositionsPage(RequestUtil.getPageParam());
    }

    /**
     * 查询债券质押持仓
     * 
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findZqzyPositionsPage")
    public Page<EtrIbBondinfo001Dto> findZqzyPositionsPage(){
        return etrIbBondinfo001Service.findZqzyPositionsPage(RequestUtil.getPageParam());
    }

    /**
     * 导出
     *
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export()
            throws Exception {
        Map<String, Object> searchParams = RequestUtil.getPageParam();
        String fileName = "银行间债券指令（指令管理）" + "(" + DateUtil.getCurrentDate() + ").xls";
        if (searchParams.get("isDvp") != null && Boolean.parseBoolean((String)searchParams.get("isDvp"))) {
            fileName = "银行间债券指令（持仓）" + "(" + DateUtil.getCurrentDate() + ").xls";
        }
        return etrIbBondinfo001Service.export(fileName, RequestUtil.getParameters(getRequest()));
    }

    /***
     * 初始化行情
     * @return
     */
    @RequestMapping(value = "/initHq")
    public String initHq(Model model){
        model.addAttribute("cmdsWebSocketUrl", cmdsWebSocketUrl);
        return "invest/entrust/etribbondinfo/EtrIbBondinfoHq";
    }

    /**
     * 查询债券信息
     *
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findSecinfo")
    public EtrFinProductDto findSecinfo(@RequestBody Map<String, Object> param){
        return etrIbBondinfo001Service.findSecinfo(param);
    }

    /**
     * 查询持仓信息
     *
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPositionInfo")
    public EtrFinProductDto findPositionInfo(@RequestBody Map<String, Object> param){
        return etrIbBondinfo001Service.findPositionInfo(param);
    }
    @ResponseBody
    @RequestMapping("queryAssetCode")
    public Page<SelectOptionDto> queryAssetCode() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("finprodType", BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        return etrIbBondinfo001Service.queryAssetCode(param);
    }
    
    @RequestMapping(value="findQuotations")
    @ResponseBody
    public Map<String, Object> findQuotations() {
    	return etrIbBondinfo001Service.findQuotations(RequestUtil.getPageParam());
    }

}
