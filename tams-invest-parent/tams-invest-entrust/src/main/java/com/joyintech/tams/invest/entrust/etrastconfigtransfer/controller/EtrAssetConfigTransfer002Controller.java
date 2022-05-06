/*
 * 文件名：EtrAssetConfigTransfer002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2021年7月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrastconfigtransfer.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.entrust.etrastconfigtransfer.dto.EtrAssetConfigDto;
import com.joyintech.tams.invest.entrust.etrastconfigtransfer.service.EtrAssetConfigTransfer002Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


/**
 * 资产配置快速转仓指令
 * 资产配置
 * @author 裴家俊
 * @version 2021年7月20日
 * @see EtrAssetConfigTransfer002Controller
 * @since 1.0
 */

@Controller
@PageId("EtrAssetConfigTransfer002")
@RequestMapping("/invest/entrust/etrastconfigtransfer/EtrAssetConfigTransfer002")
public class EtrAssetConfigTransfer002Controller extends BaseController {

    /**
     * 资产配置快速转仓指令service
     */
    @Autowired
    private EtrAssetConfigTransfer002Service etrAssetConfigTransfer002Service;

    private final static String[] KEY_ARRAY=new String[]{"portfolioId","ISSUE_AMT"};
    /**
     * 点击[资产配置]进入的画面
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model,new DictCode[]{DictCode.金融产品类型_投管用,DictCode.发行方式,DictCode.投资目的});
        model.addAttribute("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("INV_AIM_FVTPL", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
        model.addAttribute("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        model.addAttribute("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // 交易平台
        model.addAttribute("SHARES_TRADE_PLATFORM_02", DictCodeList.交易平台_竞价撮合平台.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_03", DictCodeList.交易平台_固定收益平台.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_04", DictCodeList.交易平台_大宗交易平台.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_05", DictCodeList.交易平台_综合协议交易平台.getValue());
        model.addAttribute("PORTFOLIO_ID", getParam(KEY_ARRAY[0]));
        return super.init(model);
    }

    @ResponseBody
    @RequestMapping("/getAstPosition")
    public List<EtrAssetConfigDto> getAstPosition(){
        return etrAssetConfigTransfer002Service.getAstPosition(RequestUtil.getPageParam());
    }

    /**
     * 选择转入组合联动信息
     * @param paramMap
     * @return
     */
    @ResponseBody
    @RequestMapping("/selectPortfolioInfo")
    public Map selectPortfolioInfo(@RequestBody Map<String,Object> paramMap){
        return etrAssetConfigTransfer002Service.selectPortfolioInfo(paramMap);
    }

    /**
     * 试算转仓资产
     * @param paramDto
     * @return
     */
    @ResponseBody
    @RequestMapping("/trialPosition")
    public  List<EtrAssetConfigDto> trialPosition(@RequestBody EtrAssetConfigDto paramDto){
        return etrAssetConfigTransfer002Service.trialPosition(paramDto);
    }

    /**
     * 快速生成指令组装数据
     * @param paramDto
     * @return
     */
    @ResponseBody
    @RequestMapping("/assembleImportData")
    public  Map<String,Object> assembleImportData(@RequestBody EtrAssetConfigDto paramDto){
        return etrAssetConfigTransfer002Service.assembleImportData(paramDto);
    }
}
