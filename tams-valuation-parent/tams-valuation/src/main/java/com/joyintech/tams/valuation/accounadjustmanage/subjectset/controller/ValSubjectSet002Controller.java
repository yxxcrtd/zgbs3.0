/*
 * 文件名：ValSubjectSet002.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2018年3月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.accounadjustmanage.subjectset.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.valuation.accounadjustmanage.assistaccout.dto.ValAssistHandDto;
import com.joyintech.tams.valuation.accounadjustmanage.assistaccout.service.ValAssistAccounting001Service;
import com.joyintech.tams.valuation.accounadjustmanage.subjectset.dto.ValBokSubjectDto;
import com.joyintech.tams.valuation.accounadjustmanage.subjectset.dto.ValSubjectHandDto;
import com.joyintech.tams.valuation.accounadjustmanage.subjectset.service.ValSubjectSet001Service;
import com.joyintech.tams.valuation.common.constants.SubjectConfigCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板示例<br>
 * 账套管理
 *
 * @author 何阳阳
 * @version 1.0 2018年3月13日
 * @see ValSubjectSet002Controller
 * @since 1.0
 */
@Controller
@PageId("ValSubjectSet002")
@RequestMapping("/valuation/accounadjustmanage/subjectset/ValSubjectSet002")
public class ValSubjectSet002Controller extends BaseController {
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    @Autowired
    private ValSubjectSet001Service valSubjectSet001Service;
    @Autowired
    private ValAssistAccounting001Service valAssistAccounting001Service;

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
//    @Autowired
//    private BokSubjectMapper valBokSubjectMapper;
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.余额方向, DictCode.是否, DictCode.科目类型, DictCode.币种,
                SubjectConfigCode.证券品种, SubjectConfigCode.债券类型, SubjectConfigCode.交易市场, SubjectConfigCode.账户类型,
                SubjectConfigCode.费用类型, SubjectConfigCode.税类型
        );
        String booksetId = getParam("booksetId");
        String subjectNo = getParam("subjectNo");
        if (StringUtil.isNotEmpty(booksetId) && StringUtil.isNotEmpty(subjectNo)) {
            ValBokSubjectDto dto=new ValBokSubjectDto();
            dto.setBooksetId(booksetId);
            dto.setSubjectNo(subjectNo);
            ValBokSubjectDto valBokSubjectDto = valSubjectSet001Service.selectValBokSubject(dto);
//            ValAssistHandDto dto=new ValAssistHandDto();
//            dto.setBooksetId(valBokSubjectDto.getBooksetId());
//            dto.setSubjectInternalCode(valBokSubjectDto.getSubjectInternalCode());
//            dto.setStatus("Y");
//            List<ValAssistHandDto> hands=valAssistAccounting001Service.findHandsByCondition(dto);
//            String handstr="";
//            hands.forEach(i->{
//                handstr=handstr+i.getHandCode();
//            });
//            valBokSubjectDto.setHands(handstr);

            model.addAttribute("entity", valBokSubjectDto);
        }
        return super.init(model);
    }

    /*add by sunshuaishuai 辅助核算项修改 20220406 start*/
    /**
     * 根据基础科目性质联动辅助核算项下拉框
     *
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getType")
    public List<SelectOptionDto> getType(){
        return valSubjectSet001Service.getType(RequestUtil.getPageParam());
    }

    /**
     * 根据基础科目性质查询辅助核算项
     *
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getHandCode")
    public List<ValSubjectHandDto> getHandCode(){
        return valSubjectSet001Service.getHandCode(RequestUtil.getPageParam());
    }
    /*add by sunshuaishuai 辅助核算项修改 20220406 start*/

    /**
     * 保存按钮点击时的check
     *
     * @param dto         dto
     * @param result      BindingResult
     * @param operateType 操作类型
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) ValBokSubjectDto dto, BindingResult result) {
        AjaxResult wt = commonCheck(result, "save");
        // 根据配置项表达式匹配
        List<ValBokSubjectDto> list = valSubjectSet001Service.selectValBokSubjectByInCode(dto);
        List<ValBokSubjectDto> haslist = new ArrayList<ValBokSubjectDto>();
        StringBuffer sb=new StringBuffer();
        boolean has = false;
        list.stream().forEach(p -> {
            if (dto.getSubjectHandFelCode().contains(p.getSubjectHandFelCode()) || p.getSubjectHandFelCode().contains(dto.getSubjectHandFelCode())) {
                haslist.add(p);
            }
        });
        if(CollectionUtils.isNotEmpty(haslist)){
            sb.append("该内部科目[" + dto.getSubjectInternalCode() + "]已经存在匹配辅助核算表达式！<br/>");
        }
        haslist.stream().forEach(p->{
            sb.append("[科目号：" + p.getSubjectNo() + " 辅助核算表达式：" + p.getSubjectHandFelCodeName() + "]<br/>");
        });
        if(StringUtils.isEmpty(sb.toString())){
            return wt;
        }

        wt.setMessage(sb.toString());
        wt.setMessageType(MessageType.确认消息);
        wt.setStatuCode(HttpStatus.OK.value());
        return wt;
    }

    /**
     * 保存科目
     *
     * @param dto 保存的数据
     * @param br  check结果
     * @return 保存结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) ValBokSubjectDto dto, BindingResult br) {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        valSubjectSet001Service.save(dto);
//        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)){
//            valBokSubjectMapper.updateByPrimaryKey(dto);
//        }
//        else{
//            valBokSubjectMapper.insert(dto);
//        }
        return new AjaxResult(getMessage("COMMON_I_0011"));
    }

    @ResponseBody
    @RequestMapping("findHandsByCondition")
    public List<ValAssistHandDto> findHandsByCondition() {
        return valAssistAccounting001Service.findHandsByCondition(RequestUtil.getPageParam());
    }
}
