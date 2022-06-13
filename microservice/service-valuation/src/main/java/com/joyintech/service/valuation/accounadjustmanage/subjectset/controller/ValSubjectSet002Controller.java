/*
 * 文件名：ValSubjectSet002.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2018年3月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.accounadjustmanage.subjectset.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.service.valuation.accounadjustmanage.subjectset.dto.ValSubjectHandDto;
import org.jasypt.commons.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.service.valuation.accounadjustmanage.subjectset.dto.ValBokSubjectDto;
import com.joyintech.service.valuation.accounadjustmanage.subjectset.service.ValSubjectSet001Service;
import com.joyintech.tams.common.base.dao.BokSubjectMapper;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;

/**
 * 模板示例<br>
 * 账套管理
 * 
 * @author 何阳阳
 * @version 1.0 2018年3月13日
 * @see ValSubjectSet002Controller
 * @since 1.0
 */
//@RestController(value="serviceValSubjectSet002Controller")
@RestController("valSubjectSet002Ctl_dtx")
@RequestMapping("ValSubjectSet002")
public class ValSubjectSet002Controller extends BaseController {
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    @Autowired
    private ValSubjectSet001Service valSubjectSet001Service;

//    /**
//     * <意义，目的和功能，以及可能被用到的地方>
//     */
//    @Autowired
//    private BokSubjectMapper valBokSubjectMapper;
//    @Override
//    public String init(Model model) {
//        this.setSelectOptionToModel(model, DictCode.余额方向, DictCode.是否, DictCode.科目类型);
//        String booksetId = getParam("booksetId");
//        String subjectNo = getParam("subjectNo");
//        if (StringUtil.isNotEmpty(booksetId) && StringUtil.isNotEmpty(subjectNo)) {
//            ValBokSubjectDto valBokSubjectDto = valSubjectSet001Service.selectValBokSubject(
//                booksetId, subjectNo);
//            model.addAttribute("entity", valBokSubjectDto);
//        }
//        return super.init(model);
//    }
    

    /**
     * @author  殷亚
     * @description 根据主键检索科目
     * @date 2022-2-9 14:30
     * @param dto:
     * @return com.joyintech.service.valuation.accounadjustmanage.subjectset.dto.ValBokSubjectDto
     */
    @PostMapping("/selectValBokSubject")
    public ValBokSubjectDto selectValBokSubject(@RequestBody ValBokSubjectDto dto) {
        return valSubjectSet001Service.selectValBokSubject(dto);
    }
//    /**
//     * 保存按钮点击时的check
//     *
//     * @param dto dto
//     * @param result BindingResult
//     * @param operateType 操作类型
//     * @return check结果
//     * @see
//     * @since 1.0
//     */
//    @ResponseBody
//    @RequestMapping("check")
//    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) ValBokSubjectDto dto, BindingResult result) {
//        return commonCheck(result, "save");
//    }
    /**
     * 保存科目
     * 
     * @param dto 保存的数据
     * @param br check结果
     * @return 保存结果
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("/save")
    public boolean save(@RequestBody ValBokSubjectDto dto){
        
        valSubjectSet001Service.save(dto);
        return true;
    }




   /**
    * @author  殷亚
    * @description 根据内部科目编码检索科目
    * @date 2022-1-27 16:31
     * @param dto:
     * @return java.util.List<com.joyintech.service.valuation.accounadjustmanage.subjectset.dto.ValBokSubjectDto>
    */
    @PostMapping("/selectValBokSubjectByInCode")
    public List<ValBokSubjectDto> selectValBokSubjectByInCode(@RequestBody ValBokSubjectDto dto) {
        return valSubjectSet001Service.selectValBokSubjectByInCode(dto);
    }

    /*add by sunshuaishuai 辅助核算项修改 20220406 start*/
    /**
     * 根据基础科目性质查询辅助核算项
     *
     * @return check结果
     * @see
     * @since 1.0
     */
    @PostMapping("/getHandCode")
    public List<ValSubjectHandDto> getHandCode(@RequestBody Map<String,Object> param){
        return valSubjectSet001Service.getHandCode(param);
    }

    /**
     * 根据基础科目性质联动辅助核算项下拉框
     *
     * @return check结果
     * @see
     * @since 1.0
     */
    @PostMapping("/getType")
    public List<SelectOptionDto> getType(@RequestBody Map<String,Object> param){
        return valSubjectSet001Service.getType(param);
    }
    /*add by sunshuaishuai 辅助核算项修改 20220406 end*/
}
