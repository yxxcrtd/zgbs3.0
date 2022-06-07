/*
 * 文件名：EtrPlanAssetC001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2019年1月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrplanassetc.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.util.EntrustCompleteUtil;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.etrplanassetc.dto.EtrPlanAssetCDto;
import com.joyintech.tams.invest.entrust.etrplanassetc.service.EtrPlanAssetCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 计划类货币查询
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2019年1月3日
 * @see EtrPlanAssetC001Controller
 * @since 1.0
 */
@Controller
@PageId("EtrPlanAssetC001")
@RequestMapping("/invest/entrust/etrplanassetc/EtrPlanAssetC001")
public class EtrPlanAssetC001Controller extends BaseController {

    /**
     * 计划类货币委托service
     */
    @Autowired
    private EtrPlanAssetCService etrPlanAssetCService;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;
    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向);
        this.setSelectOptionToModel(model, DictCode.币种);
        /*添加二-四级分类的数据字典 lishuping 2019/8/2 start*/
        this.setSelectOptionToModel(model, DictCode.计划类资产一级分类, DictCode.行内资产二级分类,
                DictCode.行内资产三级分类, DictCode.行内资产四级分类);
        /*添加二-四级分类的数据字典 lishuping 2019/8/2 end*/
        this.setSelectOptionToModel(model, DictCode.委托状态);
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        return super.init(model);
    }
    
    /**
     * 
     * 计划类货币委托分页查询
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<EtrPlanAssetCDto> selectForPage(){
        Page<EtrPlanAssetCDto> page = etrPlanAssetCService.selectForPage(
            RequestUtil.getPageParam());
        return page;
    }
    
    /**
     * 
     * 获取组合代码联想文本框信息
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getentrustId")
    public List<SuggestDto> getentrustId(String param){
        List<SuggestDto> temp = etrPlanAssetCService.getentrustId(param);
        return temp;
    }
    /**
     * 
     * 获取资产代码联想文本框信息
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getfinprodId")
    public List<SuggestDto> getfinprodId(String param){
        List<SuggestDto> temp = etrPlanAssetCService.getfinprodId(param);
        return temp;
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
        ValidateGroupDelete.class}) EtrPlanAssetCDto dto, BindingResult result) {
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
    public AjaxResult recallDelegate(@RequestBody EtrPlanAssetCDto dto)
    // modify by yushilong start
            throws Exception {
        // modify by yushilong end
        List<EtrPlanAssetCDto> dtolist = dto.getSelectedItem();
        if(dtolist != null && dtolist.size()>0){
            for(EtrPlanAssetCDto entity : dtolist){
                etrPlanAssetCService.recallDelegate(entity);
                EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entity.getEntrustId());
                entity.setCounterId(etrEntrust.getCounterId());
            }
            // add by yushilong 批量撤销预占 start
            EntrustRdsCommonAop.doListPreOccupy(dtolist, DictCodeList.平安资产类型_计划类资产_货币型.getValue(),
                    BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(), false, null);
            // add by yushilong 批量撤销预占 end
            return new AjaxResult("撤销成功");
        }
        return new AjaxResult("撤销失败");
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
    public AjaxResult closeDelegate(@RequestBody EtrPlanAssetCDto dto) {
        List<EtrPlanAssetCDto> dtolist = dto.getSelectedItem();
        if(dtolist != null && dtolist.size()>0){
            for(EtrPlanAssetCDto entity : dtolist){
                etrPlanAssetCService.closeDelegate(entity.getEntrustId(), entity.getCloseWay());
            }
            // add by zhangrui1 批量委托关闭 start
            // 若预占下没有实占则预占信息清除，否则预占信息保留，撤销交易时可恢复原状态
            List<String> entrustIdList = dtolist.stream().map(
                    EtrPlanAssetCDto::getEntrustId).collect(Collectors.toList());
            EntrustCompleteUtil.doComplete(entrustIdList);
            // add by zhangrui1 批量委托关闭 end
            return new AjaxResult("关闭成功");
        }
        return new AjaxResult("关闭失败");
    }
    
    /**
     * 文件导出 <功能详细描述>
     * 
     * @return 导出返回参数
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export(){
    	Map<String, Object> param = RequestUtil.getParameters(getRequest());
    	param.put("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE"));
        return etrPlanAssetCService.export(this.getPageId(),
        		param);
    }
}
