/*
 * 文件名：EtrFund001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年9月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrfund.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.util.EntrustCompleteUtil;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.entrust.etrfund.dto.EtrFundDto;
import com.joyintech.tams.invest.entrust.etrfund.service.EtrFundService;

/**
 * 基金委托
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年9月29日
 * @see EtrFund001Controller
 * @since 1.0
 */
@Controller
@PageId("EtrFund001")
@RequestMapping("/invest/entrust/etrfund/EtrFund001")
public class EtrFund001Controller extends BaseController{
    
    @Autowired
    private EtrFundService eteFundService;
    
    /**
     * 页面初始化
     */
    @Override
    public String init(Model model) {
        /*List<SelectOptionDto> list = new ArrayList<SelectOptionDto>();
        SelectOptionDto dto = new SelectOptionDto();
        dto.setId(DictCodeList.交易方向_买入.getValue());
        dto.setText(DictCodeList.交易方向_买入.getCodeName());
        SelectOptionDto dto1 = new SelectOptionDto();
        dto1.setId(DictCodeList.交易方向_卖出.getValue());
        dto1.setText(DictCodeList.交易方向_卖出.getCodeName());
        list.add(dto);
        list.add(dto1);
        model.addAttribute("PS", JsonUtils.objectArray2Json(list.toArray()));*/
        this.setSelectOptionToModel(model, DictCode.币种, DictCode.交易方向, DictCode.委托状态, DictCode.基金类资产类型);
        this.setSelectOptionToModel(model,DictCode.交易所基金类型, DictCode.交易场所);
        //新增资产二、三、四级分类	王贵奇	2019/7/29	开始
        this.setSelectOptionToModel(model, DictCode.行内资产二级分类);
        this.setSelectOptionToModel(model, DictCode.行内资产三级分类);
        this.setSelectOptionToModel(model, DictCode.行内资产四级分类);
        //新增资产二、三、四级分类	王贵奇	2019/7/29	结束
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        return super.init(model);
    }
    
    /**
     * 
     * 基金委托分页查询
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<EtrFundDto> selectForPage(){
        Page<EtrFundDto> page = eteFundService.selectForPage(
            RequestUtil.getPageParam());
        return page;
    }
    
    /**
     * 
     * 组合代码联想文本框
     * <功能详细描述>
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getPortfolioId")
    public List<SuggestDto> getPortfolioId() {
        String portfolioId = super.getRequest().getParameter("param");
        return eteFundService.getPortfolioId(portfolioId);
    }
    
    /**
     * 
     * 资产代码联想文本框
     * <功能详细描述>
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getFinprodMarketId")
    public List<SuggestDto> getFinprodMarketId() {
        String finprodId = super.getRequest().getParameter("param");
        return eteFundService.getFinprodMarketId(finprodId, this.getParam("pageId"));
    }
    
    /**
     * 
     * 删除、撤销、关闭委托check
     * <功能详细描述>
     * @param dto EtrFundDto
     * @param result BindingResult
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupDelete.class}) EtrFundDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    
    /**
     * 撤回委托 
     * <功能详细描述>
     * @param dto EtrFundDto
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("recall")
    public AjaxResult recallDelegate(@RequestBody EtrFundDto dto) throws Exception{
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        List<EtrFundDto> list = dto.getSelectedItem();
        for(EtrFundDto etrFundDto:list){
            if(eteFundService.recallDelegate(etrFundDto.getEntrustId())){
                ajaxResult.setMessage(this.getMessage("ETR_X_001"));
            }else{
                errorList.add(new ValidMessage(getMessage("委托代码:"+etrFundDto.getPortfolioId())+"委托撤销失败"));
                ajaxResult.setErrorList(errorList);
            }
        }
        // add by yushilong 批量撤销预占 start
        if (CollectionUtils.isEmpty(ajaxResult.getErrorList())) {
            List<EtrFundDto> oriList = new ArrayList<>();
            for (EtrFundDto etrFundDto : list) {
                ProcessMark param = new ProcessMark();
                param.setPageId("EtrFund002");
                param.setBusinessDataPk1(etrFundDto.getEntrustId());
                EtrFundDto bussinessEntityByPk = eteFundService.getBussinessEntityByPk(param);
                if(null != bussinessEntityByPk){
                    oriList.add(bussinessEntityByPk);
                }else {
                    oriList.add(etrFundDto);
                }
            }
            EntrustRdsCommonAop.doListPreOccupy(oriList, DictCodeList.平安资产类型_基金.getValue(),
                    BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(), false, null);
        }
        // add by yushilong 批量撤销预占 end
        return ajaxResult;
    }
    /**
     * 关闭委托 
     * 
     * @param dto EtrFundDto
     * @return AjaxResult
     * @see 
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("close")
    public AjaxResult closeDelegate(@RequestBody EtrFundDto dto) throws Exception{
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        List<EtrFundDto> list = dto.getSelectedItem();
        // add by zhangrui1 批量委托关闭 start
        List<String> entrustIdList = new ArrayList<>();
        // add by zhangrui1 批量委托关闭 end
        for(EtrFundDto etrFundDto:list){
            if(eteFundService.closeDelegate(etrFundDto.getEntrustId(), etrFundDto.getCloseWay())){
                ajaxResult.setMessage(this.getMessage("ETR_X_003"));
                // add by yushilong 批量委托关闭 start
                entrustIdList.add(etrFundDto.getEntrustId());
                // add by yushilong 批量委托关闭 end
            }else{
                errorList.add(new ValidMessage(getMessage("组合代码:"+etrFundDto.getPortfolioId())+"委托关闭失败"));
                ajaxResult.setErrorList(errorList);
            }
        }
        // add by zhangrui1 批量委托关闭 start
        // 若预占下没有实占则预占信息清除，否则预占信息保留，撤销交易时可恢复原状态
        if (!CollectionUtils.isEmpty(entrustIdList)) {
            Map<String, BigDecimal> etrFinishMap = EntrustCompleteUtil.getEtrFinishAmtList(entrustIdList);
            //委托没有做过交易的，和撤销相同逻辑，删除预占
            List<EtrFundDto> recallEntrustIdList = new ArrayList<>();
            //委托已有完成额度，做过交易，走关闭完成的逻辑，更新预占
            List<String> closeEntrustIdList = new ArrayList<>();
            for(EtrFundDto etrDto : list) {
                if(StringUtil.isNotEmpty(etrFinishMap) && StringUtil.isNotEmpty(etrFinishMap.get(etrDto.getEntrustId()))
                        && BigDecimal.ZERO.compareTo(etrFinishMap.get(etrDto.getEntrustId()))<0) {
                    closeEntrustIdList.add(etrDto.getEntrustId());
                }
                else if(entrustIdList.contains(etrDto.getEntrustId())){
                    recallEntrustIdList.add(etrDto);
                }
            }

            //没有做过交易，直接关闭的
            if(!StringUtil.isEmptyList(recallEntrustIdList)) {
                // add by yushilong 批量撤销预占 start
                EntrustRdsCommonAop.doListPreOccupy(recallEntrustIdList, DictCodeList.平安资产类型_基金.getValue(),
                        BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(), false, null);
                // add by yushilong 批量撤销预占 end
            }

            //已经有委托完成度的
            if(!StringUtil.isEmptyList(closeEntrustIdList)) {
                EntrustCompleteUtil.doComplete(closeEntrustIdList);
            }
        }
        // add by zhangrui1 批量委托关闭 end
        return ajaxResult;
    }
    
    /**
     * 
     * 删除投资委托
     * <功能详细描述>
     * @param dto EtrFundDto
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("delete")
    public AjaxResult deleteDelegate(@RequestBody EtrFundDto dto) throws Exception{
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        List<EtrFundDto> list = dto.getSelectedItem();
        for(EtrFundDto etrFundDto:list){
            if(eteFundService.deleteDelegate(etrFundDto.getEntrustId())){
                
            }else{
                errorList.add(new ValidMessage(getMessage("组合代码:"+etrFundDto.getPortfolioId())+"委托删除失败"));
            }
        }
        // add by yushilong 批量撤销预占 start
        EntrustRdsCommonAop.doListPreOccupy(list, DictCodeList.平安资产类型_基金.getValue(),
                BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(), false, null);
        // add by yushilong 批量撤销预占 end
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }
    
    /**
     * 文件导出 <功能详细描述>
     * 
     * @return 导出返回参数
     * @see
     * @since 1.0
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export(){
    	Map<String, Object> param = RequestUtil.getParameters(getRequest());
    	param.put("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE"));
    	 return eteFundService.export(this.getPageId(),param);
}
}
