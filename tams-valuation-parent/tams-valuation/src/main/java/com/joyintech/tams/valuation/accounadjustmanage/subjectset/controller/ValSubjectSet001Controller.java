/*
 * 文件名：ValSubjectSet001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2018年3月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.accounadjustmanage.subjectset.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.valuation.accounadjustmanage.subjectset.dto.ValBokSubjectDto;
import com.joyintech.tams.valuation.accounadjustmanage.subjectset.service.ValSubjectSet001Service;

/**
 * 模板示例<br>
 * 账套管理
 * 
 * @author 何阳阳
 * @version 1.0 2018年3月13日
 * @see ValSubjectSet001Controller
 * @since 1.0
 */
@Controller
@PageId("ValSubjectSet001")
@RequestMapping("/valuation/accounadjustmanage/subjectset/ValSubjectSet001")
public class ValSubjectSet001Controller extends BaseController {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    @Autowired
    private ValSubjectSet001Service valSubjectSet001Service;

//    @Autowired
//    private ValAssetBook001Service valAssetBook001Service;


    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.科目类型);
        this.setSelectOptionToModel(model, DictCode.科目级别);
        this.setSelectOptionToModel(model, DictCode.余额方向);
        return super.init(model);
    }

    /**
     * 检索上级科目代码
     * 
     * @return 账套上级科目代码联想框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getFSubjectId")
    public List<SuggestDto> getFSubjectId()
         {
//        String param = super.getRequest().getParameter("param");
        return valSubjectSet001Service.getFSubjectId2(RequestUtil.getPageParam());
    }

    /**
     * 检索科目代码
     * 
     * @return 账套科目代码联想框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getSubjectId")
    public List<SuggestDto> getSubjectId()
         {
        String param = super.getRequest().getParameter("param");
        return valSubjectSet001Service.getSubjectId(param);
    }

    // /**
    // * 检索科目代码 支持联动
    // *
    // * @return 账套科目代码联想框内容
    // * @throws Exception
    // * @see
    // * @since 1.0
    // */
    // @ResponseBody
    // @RequestMapping("/getSubjectId2")
    // public List<SuggestDto> getSubjectId2()
    // throws Exception {
    // return valSubjectSet001Service.getSubjectId2(RequestUtil.getPageParam());
    // }

    /**
     * 检索科目代码 支持联动
     * 
     * @return 账套科目代码联想框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getSubjectId2")
    public List<SelectOptionDto> getSubjectId2()
         {
        return valSubjectSet001Service.getSubjectId2(RequestUtil.getPageParam());
    }

    /**
     * 检索科目级别 支持联动
     * 
     * @return 账套科目代码联想框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getSubjectLevel")
    public List<SelectOptionDto> getSubjectLevel()
         {
        List<SelectOptionDto> list = Lists.newArrayList();
        String subjectNo = getParam("subjectNo");
        if (StringUtils.isEmpty(subjectNo)) {
            list = getSelectDataFromDataDic(DictCode.科目级别, getParam("search")).getOptions();
        }
        else {
            list = valSubjectSet001Service.getSubjectLevel(RequestUtil.getPageParam());
            if(CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    item.setText(SysDataUtil.getNameFromDataDict(DictCode.科目级别, item.getId()));
                });
            }
        }
        return list;
    }

    /**
     * 检索账套代码
     * 
     * @return 账套代码联想框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getBooksetId")
    public List<SuggestDto> getBooksetId()
         {
//        String param = super.getRequest().getParameter("param");
        return valSubjectSet001Service.getBooksetId(RequestUtil.getPageParam());
//        return valAssetBook001Service.getBooksetIdByBooksetType(param);
    }

    /**
     * 所属科目下拉框获取
     *
     * @return 返回的数据
     * @exception Exception 异常
     * @since 1.0
     */
    @RequestMapping("/queryValBokSubject")
    @ResponseBody
    public List<SelectOptionDto> queryValBokSubject(HttpServletRequest request) {
        Map<String, Object> param = RequestUtil.getPageParam();
        return valSubjectSet001Service.queryValBokSubject(param);
    }

    /**
     * 检索页面列表
     * 
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<ValBokSubjectDto> selectAllSubject()
         {
        Page<ValBokSubjectDto> page = valSubjectSet001Service.selectForPage(RequestUtil.getPageParam());
        return page;
    }

    /**
     * 删除按钮点击时的check
     *
     * @param dto dto
     * @param result BindingResult
     * @param operateType 操作类型
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ValidateGroupDelete.class}) ValBokSubjectDto dto,
                            BindingResult result) {
        return commonCheck(result, "delete");
    }

    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Validated({ValidateGroupDelete.class}) ValBokSubjectDto dto,
                             BindingResult br) {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        valSubjectSet001Service.deleteByBatch(dto.getSelectedItem());
        
        return new AjaxResult(getMessage("COMMON_I_0011"));
    }
}
