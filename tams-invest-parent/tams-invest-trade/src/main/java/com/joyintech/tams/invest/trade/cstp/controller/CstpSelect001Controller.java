/*
 * 文件名：CSTPSelect.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年12月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.trade.cstp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.trade.investtrade.commandreceive.dto.TrdCommandReceive001Dto;
import com.joyintech.tams.invest.trade.investtrade.commandreceive.service.TrdCommandReceive001Service;
import com.sun.org.apache.xpath.internal.operations.Bool;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.trade.cstp.dto.CstpSelectDto;
import com.joyintech.tams.invest.trade.cstp.service.CstpSelect001Service;
import com.joyintech.tams.invest.trade.cstptrdmatchinfo.service.CstpTrdMatchInfoService;

/**
 * CSTP下行交易查询
 * @author wangxuelei
 * @version 1.0 2018年12月3日
 * @see CstpSelect001Controller
 * @since 1.0
 */
@Controller
@PageId("CSTPSelect001")
@RequestMapping("/invest/trade/cstp/CSTPSelect001")
public class CstpSelect001Controller extends BaseController{
    /**
     * cstp下行交易Service
     */
    @Autowired
    private CstpSelect001Service cstpSelect001Service;
    @Autowired
    private TrdCommandReceive001Service trdCommandReceive001Service;
    @Autowired
    private CstpTrdMatchInfoService cstpTrdMatchInfoService;
    /**
     * 初始化方法
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        List<SelectOptionDto> csptPs = new ArrayList<SelectOptionDto>();
        SelectDto psDto = this.getSelectDataFromDataDic(DictCode.交易方向, null);
        SelectDto backLendDirectionDto= this.getSelectDataFromDataDic(DictCode.回购拆借方向, null);
        for(SelectOptionDto dto : psDto.getOptions()){
            SelectOptionDto dto1 = new SelectOptionDto();
            dto1.setId(dto.getId());
            dto1.setText(dto.getText());
            csptPs.add(dto1);
        }
        for(SelectOptionDto dto : backLendDirectionDto.getOptions()){
            SelectOptionDto dto1 = new SelectOptionDto();
            dto1.setId(dto.getId());
            dto1.setText(dto.getText());
            csptPs.add(dto1);
        }
        model.addAttribute("IRS_TYPE", DictCode.互换方式.getValue());
        //add by wangpeng end
        model.addAttribute("entrustStatus", "99");
        model.addAttribute("CSPT_PS", JsonUtils.object2Json(csptPs.toArray()));
        model.addAttribute("PINAN_ASSET_TYPE_F01", DictCodeList.平安资产类型_债券.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F07", DictCodeList.平安资产类型_质押式回购.getValue());
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("entrustStatus", "99");
        model.addAttribute("NUMBER", trdCommandReceive001Service.otherSelectList(map,false).size());
        model.addAttribute("NUMBERRECEIVE", trdCommandReceive001Service.otherSelectList(map,true).size());
        model.addAttribute("ISTRADERECOMMANDrECEIVE",trdCommandReceive001Service.isTradeRdCommandReceive());
        this.setSelectOptionToModel(model, DictCode.匹配状态,DictCode.交易类型_CSTP,DictCode.交易场所,DictCode.平安资产类型,DictCode.交易类型,DictCode.下行交易匹配类型);
        return super.init(model);
    }
    /**
     * 分页查询
     * @return 分页查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<CstpSelectDto> findPage(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return cstpSelect001Service.findPage(map);
    }

    @ResponseBody
    @RequestMapping("selectForPageFromCstp")
    public Page<TrdCommandReceive001Dto> selectForPageFromCstp(){
        Map<String,Object> param = RequestUtil.getParameters(getRequest());
        param.put("cstpType","cstp");
        param.put("entrustStatus", DictCodeList.委托状态_已接收.getValue());
        String isCstp = (String) param.get("isCstp");
            Page<TrdCommandReceive001Dto> trdCommandReceive001DtoPage = trdCommandReceive001Service.selectForPageFromCstp(param);
//        if(Boolean.parseBoolean(isCstp)){
//            cstpSelect001Service.findEntrustPageByCstp(param,trdCommandReceive001DtoPage);
//        }
        return trdCommandReceive001DtoPage;
    }


    /**
     * 画面导出功能实现
     * 
     * @return ResponseEntity 确认的实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export()
         {
        return cstpSelect001Service.export(this.getPageId(),
            RequestUtil.getParameters(getRequest()));
    }
    /**
     * 导出
     * @return
     * @throws Exception
     */
    @RequestMapping("exportEntrust")
    public ResponseEntity<byte[]> exportEntrust() {
        Map<String, Object> parameters = RequestUtil.getParameters(getRequest());
        parameters.put("cstpType","cstp");
        return trdCommandReceive001Service.export(this.getPageId(),parameters);
    }
    
    /**
     * 画面导出功能实现
     * 
     * @return ResponseEntity 确认的实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("exportExcelQueDing")
    public ResponseEntity<byte[]> exportExcelQueDing()
         {
        return cstpTrdMatchInfoService.export(this.getPageId(),
            RequestUtil.getParameters(getRequest()));
    }

    /**
     * 成交编号
     * @return 成交编号下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getExecid")
    public Page<SelectOptionDto> getExecid(){
        Map<String,Object> map = RequestUtil.getPageParam();
        Page<SelectOptionDto> execid = cstpSelect001Service.getExecid(map);
        return execid;
    }
    
    /**
     * 报价信息 委托  预交易
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getBusiCode")
    public Page<SelectOptionDto> getBusiCode(){
        Map<String,Object> map = RequestUtil.getPageParam();
        Page<SelectOptionDto> execid = cstpSelect001Service.getBusiCode(map);
        return execid;
    }
    
    /**
     * 获取资产代码下拉框
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getFinprodId")
    public List<SuggestDto> getFinprodId(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return cstpSelect001Service.getFinprodId(map);
    }

}
