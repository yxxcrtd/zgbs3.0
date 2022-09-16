/*
 * 文件名：EtrSecLend001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yjm
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrseclend.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.util.EntrustCompleteUtil;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.etrseclend.dto.EtrSecLendDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.entrust.etrseclend.service.EtrSecLendService;

/**
 * 债券投资委托查询
 * 〈功能详细描述〉
 * @author wangzhuangzhuang
 * @version 1.0 2019年7月5日
 * @see EtrSecLend001Controller
 * @since 1.0
 */
@Controller
@PageId("EtrSecLend001")
@RequestMapping("/invest/entrust/etrseclend/EtrSecLend001")
public class EtrSecLend001Controller extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 债券借贷投资委托service
     */
    @Autowired
    private EtrSecLendService etrSecLendService;
    /**
     * 初始化001查询页面
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.币种);
        this.setSelectOptionToModel(model, DictCode.回购拆借方向);
        this.setSelectOptionToModel(model, DictCode.交易场所);
        this.setSelectOptionToModel(model, DictCode.委托状态);
        return super.init(model);
    }
    /**
     *
     * 债券拆借投资委托分页查询
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<EtrSecLendDto> selectForPage(){
        Page<EtrSecLendDto> page = etrSecLendService.selectForPage(
                RequestUtil.getPageParam());
        return page;
    }
 
    /**
     * 按钮点击时的check
     *
     * @param dto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupDelete.class}) EtrSecLendDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    /**
     * 撤回委托 <功能详细描述>
     *
     * @param dto 实体类
     * @return boolean
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("recall")
    public AjaxResult recallDelegate(@RequestBody EtrSecLendDto dto) throws Exception {
        List<EtrSecLendDto> dtolist = dto.getSelectedItem();
        EntrustRdsCommonAop.doListPreOccupy(dtolist, DictCodeListExt2.平安资产类型_债券借贷.getValue(),
                BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(), false, null);
        if(dtolist != null && dtolist.size()>0){
            for(EtrSecLendDto entity : dtolist){
                etrSecLendService.recallDelegate(entity);
            }
            return new AjaxResult(this.getMessage("ETR_X_001"));
        }
        return new AjaxResult(this.getMessage("ETR_X_002"));
    }
    /**
     * 关闭委托
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("close")
    public AjaxResult closeDelegate(@RequestBody EtrSecLendDto dto) throws Exception {
        AjaxResult ajaxResult = new AjaxResult();
        List<EtrSecLendDto> dtolist = dto.getSelectedItem();
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        List<String> entrustIdList = new ArrayList<>();
        if(dtolist != null && dtolist.size()>0){
            for(EtrSecLendDto entity : dtolist){
                Boolean flag = false;
                try{
                    flag= etrSecLendService.closeDelegate(entity, entity.getCloseWay());
                }catch (Exception e){
                    logger.error("关闭委托报错",e);
                }
                if(flag){
                    ajaxResult.setMessage(this.getMessage("ETR_X_003"));
                    entrustIdList.add(entity.getEntrustId());
                }else{
                    errorList.add(new ValidMessage(getMessage("组合代码:"+entity.getPortfolioId())+"委托关闭失败"));
                    ajaxResult.setErrorList(errorList);
                }

            }
            if (!CollectionUtils.isEmpty(entrustIdList)) {
                Map<String, BigDecimal> etrFinishMap = EntrustCompleteUtil.getEtrFinishAmtList(entrustIdList);
                List<EtrSecLendDto> recallEntrustIdList = new ArrayList<>();
                List<String> closeEntrustIdList = new ArrayList<>();
                for(EtrSecLendDto etrDto : dtolist) {
                    if(StringUtil.isNotEmpty(etrFinishMap) && StringUtil.isNotEmpty(etrFinishMap.get(etrDto.getEntrustId()))
                            && BigDecimal.ZERO.compareTo(etrFinishMap.get(etrDto.getEntrustId()))<0) {
                        closeEntrustIdList.add(etrDto.getEntrustId());
                    }
                    else if(entrustIdList.contains(etrDto.getEntrustId())){
                        recallEntrustIdList.add(etrDto);
                    }
                }
                if(!StringUtil.isEmptyList(recallEntrustIdList)) {
                    EntrustRdsCommonAop.doListPreOccupy(recallEntrustIdList, DictCodeListExt2.平安资产类型_债券借贷.getValue(),
                            BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(), false, null);
                }
                if(!StringUtil.isEmptyList(closeEntrustIdList)) {
                    EntrustCompleteUtil.doComplete(closeEntrustIdList);
                }
            }
        }
        return ajaxResult;
    }

    /**
     *
     * 删除投资委托
     * <功能详细描述>
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("delete")
    public AjaxResult deleteDelegate(@RequestBody EtrSecLendDto dto) {
        List<EtrSecLendDto> list = dto.getSelectedItem();
        if(list!=null && list.size()>0){
            for(EtrSecLendDto entity : list){
                String entrustId = entity.getEntrustId();
                etrSecLendService.deleteDelegate(entrustId);
            }
            return new AjaxResult("删除成功");
        }
        return new AjaxResult("删除失败");
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
        return etrSecLendService.export(this.getPageId(),
                RequestUtil.getParameters(getRequest()));
    }
}
