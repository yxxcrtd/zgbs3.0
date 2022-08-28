/*
 * 文件名：MstCommissionSet002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年7月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.basicdata.mstcommissionset.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.TrdFeeConfigDetail;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.invest.basicdata.mstcommissionset.dto.MstCommissionSetDto;
import com.joyintech.tams.invest.basicdata.mstcommissionset.service.MstCommissionSetService;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 〈功能详细描述〉
 * 
 * @author 裴家俊
 * @version 1.0 2019年3月22日
 * @see MstCommissionSet002Controller
 * @since 1.0
 */
@Controller
@PageId("MstCommissionSet002")
@RequestMapping("/invest/basicdata/mstcommissionset/MstCommissionSet002")
public class MstCommissionSet002Controller extends BaseController {

    /**
     * MstCommissionSetService
     */
    @Autowired
    private MstCommissionSetService mstCommissionSetService;

    /**
     * 业务编号组件
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 初始化的查询条件
     */
    private static final IDictCode[] DICT_CODES = new BussinessDictCode[] {BussinessDictCode.交易场所,
        BussinessDictCode.计费类型, BussinessDictCode.计费基数, BussinessDictCode.期限品种,
        BussinessDictCode.费用类型, BussinessDictCode.是否
        ,BussinessDictCode.交易平台
    };

    /**
     * add by hudongyang 初始化查询条件
     */
    private static final IDictCode[] DICT_CODES1 = new DictCode[] {
            DictCode.交易所费用资产分类一, DictCode.交易所费用资产分类二, DictCode.佣金计算方式, DictCode.分档条件, DictCode.授信资产类型
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            MstCommissionSetDto dto = mstCommissionSetService.getBussinessEntityByWorkflowId(
                workflowId);
            model.addAttribute("pageModel", dto.getPageModel());
            model.addAttribute(GlobalConstant.ENTITY, dto);
        }
        else if (WorkFlowUtil.isNotFromToDoList()
                 && StringUtil.isNotEmpty(this.getParam("batchSeq"))) {
            // 通过主键查询数据
            MstCommissionSetDto dto = mstCommissionSetService.getBussinessEntityBySeqNo(
                this.getParam("batchSeq"));
            model.addAttribute(GlobalConstant.ENTITY, dto);
        }
        this.setSelectOptionToModel(model, DICT_CODES);
        this.setSelectOptionToModel(model, DICT_CODES1);//add by hudongyang 增加初始化
        List<SelectOptionDto> listFloatRate = new ArrayList<SelectOptionDto>();
        SelectDto selectDto1 = this.getSelectDataFromDataDic(BussinessDictCode.交易方向, null);
        for (SelectOptionDto dto : selectDto1.getOptions()) {
            dto.setId(dto.getId());
            listFloatRate.add(dto);
        }
        //add by longpeng start SNSTZJY-2438产品化-交易所费用设置--新建数据，交易品种为“质押式回购”时，“交易方向”下拉框枚举值显示重复
        /*SelectDto selectDto2 = this.getSelectDataFromDataDic(BussinessDictCode.回购拆借方向, null);
        for (SelectOptionDto dto : selectDto2.getOptions()) {
            dto.setId(dto.getId());
            listFloatRate.add(dto);
        }*/
      //add by longpeng end SNSTZJY-2438产品化-交易所费用设置--新建数据，交易品种为“质押式回购”时，“交易方向”下拉框枚举值显示重复
        model.addAttribute("PS", JsonUtils.objectArray2Json(listFloatRate.toArray()));
        model.addAttribute("FEE_TYPE_B02", BussinessDictCodeList.费用类型_印花税.getValue());
        model.addAttribute("FEE_TYPE_B05", BussinessDictCodeList.费用类型_佣金.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F01", BussinessDictCodeList.平安资产类型_债券.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F11", BussinessDictCodeList.平安资产类型_股票.getValue());
        // 场内基金改造 add by renping 20191117 start
        model.addAttribute("PINAN_ASSET_TYPE_F02", BussinessDictCodeList.平安资产类型_基金.getValue());
        // 场内基金改造 add by renping 20191117 end
        model.addAttribute("CHARGE_KIND_1", BussinessDictCodeList.计费类型_固定费率.getValue());
        model.addAttribute("CHARGE_BASIS_02", BussinessDictCodeList.计费基数_成交金额.getValue());
        
        SelectDto temp = this.getSelectDataFromDataDic(DictCode.一次性费用类型, null);
        List<SelectOptionDto> listFeeTypeQs = new ArrayList<SelectOptionDto>();
        for (SelectOptionDto dto : temp.getOptions()) {
        	if (!StringUtil.equals(dto.getId(), DictCodeListExt1.一次性费用类型_佣金.getValue())) {
        		listFeeTypeQs.add(dto);
        	}
        }
        model.addAttribute("FEE_TYPE_QS", JsonUtils.objectArray2Json(listFeeTypeQs.toArray()));
        return super.init(model);
    }

    /**
     * 
     * 获取证券账户下拉框选项
     * @return 证券账户下拉框选项
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getSecAcctName")
    public List<SelectOptionDto> getInstitutionName() {
        Map<String, Object> map = new HashMap<String, Object>();
        String param = super.getRequest().getParameter("search");
        map.put("secAcctId", param);
        List<SelectOptionDto> temp = mstCommissionSetService.getSecAcctName(map);

        return temp;
    }

    /**
     * 画面点击保存按钮时check
     * 
     * @param dto 实体类
     * @param result 校验结果
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) MstCommissionSetDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 保存
     * 
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws WorkFlowException 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) MstCommissionSetDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<MstCommissionSetDto> processParameter = new ProcessParameter<MstCommissionSetDto>();
        WorkFlowParameter<MstCommissionSetDto> params = new WorkFlowParameter<MstCommissionSetDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        if (StringUtil.isEmpty(dto.getBatchSeq())) {
            if(baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                codeParameter.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_交易所交易费用设置));
                dto.setBatchSeq(baseGetBusinessId.buildBussinessCode(codeParameter));
            }
            else {
                dto.setBatchSeq(IDGenerator.generate());
            }
        }
        String businessCode = dto.getBatchSeq();
        params.setBusinessCode(businessCode);
        params.setBusinessType(DictCodeListExt2.共通业务类型_交易所交易费用设置.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment(getMessage("MstCommissionSet002.batchSeq"),
                GlobalConstant.OPEN_PARENTHESIS, businessCode, GlobalConstant.CLOSE_PARENTHESIS,
                getMessage("MstCommissionSet002.tradeType"), GlobalConstant.OPEN_PARENTHESIS,
                    SysDataUtil.getNameFromDataDict(DictCode.授信资产类型, entity.getTradeType()), GlobalConstant.CLOSE_PARENTHESIS);
        });
        processParameter.setWorkFlowParameter(params);
        String msg = mstCommissionSetService.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * add by hudongyang
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<TrdFeeConfigDetail> selectAll() {
        Page<TrdFeeConfigDetail> page = mstCommissionSetService.select(
                RequestUtil.getPageParam());
        return page;
    }

    /**
     * add by hudongyang
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/getMstCustomerInfo")
    public List<SelectOptionDto> getMstCustomerInfo()
            throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        String customerId = super.getRequest().getParameter("search");
        param.put("customerId", customerId);
        List<SelectOptionDto> temp = mstCommissionSetService.getMstCustomerInfo(param);

        return temp;
    }

    @ResponseBody
    @RequestMapping("/getMstAcctInfo")
    public Page<SelectOptionDto> getMstAcctInfo(){
        Map<String, Object> params = RequestUtil.getPageParam();
        params.put("brokerId",RequestUtil.getPageParam().get("customerId"));
        Page<SelectOptionDto> temp = mstCommissionSetService.getMstAcctInfo(params);
        return temp;
    }
}
