/*
 * 文件名：MstCustomerNature002Controller.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：
 * 修改时间：2018年7月23日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */
package com.joyintech.tams.invest.basicdata.mstcustomernature.controller;
import java.util.*;

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
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.basicdata.mstcustomernature.dto.MstCustomerNatureDto;
import com.joyintech.tams.invest.basicdata.mstcustomernature.service.MstCustomerNatureService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import sun.reflect.generics.tree.Tree;

/**
 * 客户性质配置
 * @author huwei
 * @version 1.0 2018年8月14日
 * @see MstCustomerNature002Controller
 * @since 1.0
 */
@Controller
@PageId("MstCustomerNature002")
@RequestMapping("/invest/basicdata/mstcustomernature/MstCustomerNature002")
public class MstCustomerNature002Controller extends BaseController {

    /**
     * 客服性质管理业务类
     */
    @Autowired
    private MstCustomerNatureService mstCustomerNatureService;

    /**
     * 日期组件
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 流程组件
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;
    
    
    /**
     * 注入业务主键生产类
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 画面初始化
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        setSelectOptionToModel(model, DictCode.客户类型);
        model.addAttribute("typeValue_01", DictCodeList.客户性质_发行人.getValue());
        model.addAttribute("typeValue_02", DictCodeList.客户性质_融资人.getValue());
        model.addAttribute("typeValue_03", DictCodeList.客户性质_管理人.getValue());
        model.addAttribute("typeValue_04", DictCodeList.客户性质_托管人.getValue());
        model.addAttribute("typeValue_05", DictCodeList.客户性质_担保人.getValue());
        model.addAttribute("typeValue_06", DictCodeList.客户性质_出质人.getValue());
        model.addAttribute("typeValue_07", DictCodeList.客户性质_承销商.getValue());
        model.addAttribute("typeValue_08", DictCodeList.客户性质_存放行.getValue());
        model.addAttribute("typeValue_09", DictCodeList.客户性质_投资顾问.getValue());
        model.addAttribute("typeValue_10", DictCodeListExt3.客户性质_券商.getValue());
        model.addAttribute("typeValue_12", DictCodeListExt3.客户性质_交易对手.getValue());
        model.addAttribute("typeValue_13", DictCodeListExt3.客户性质_原始权益人.getValue());
        model.addAttribute("typeName_01", DictCodeList.客户性质_发行人.getCodeName());
        model.addAttribute("typeName_02", DictCodeList.客户性质_融资人.getCodeName());
        model.addAttribute("typeName_03", DictCodeList.客户性质_管理人.getCodeName());
        model.addAttribute("typeName_04", DictCodeList.客户性质_托管人.getCodeName());
        model.addAttribute("typeName_05", DictCodeList.客户性质_担保人.getCodeName());
        model.addAttribute("typeName_06", DictCodeList.客户性质_出质人.getCodeName());
        model.addAttribute("typeName_07", DictCodeList.客户性质_承销商.getCodeName());
        model.addAttribute("typeName_08", DictCodeList.客户性质_存放行.getCodeName());
        model.addAttribute("typeName_09", DictCodeList.客户性质_投资顾问.getCodeName());
        model.addAttribute("typeName_10", DictCodeListExt3.客户性质_券商.getCodeName());
        model.addAttribute("typeName_12", DictCodeListExt3.客户性质_交易对手.getCodeName());
        model.addAttribute("typeName_13", DictCodeListExt3.客户性质_原始权益人.getCodeName());


        model.addAttribute("CUSTOMER_TYPE_01", DictCodeList.客户类型_法人.getValue());
        model.addAttribute("CUSTOMER_TYPE_02", DictCodeList.客户类型_自然人.getValue());
        // 判断是否来自工作流
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            MstCustomerNatureDto dto = mstCustomerNatureService.getBussinessEntityByWorkflowId(
                workflowId);
            model.addAttribute("entity", JsonUtils.object2Json(dto));
            model.addAttribute("formTodoList", true);
        }
        return super.init(model);
    }

    /**
     * 代码的联想文本框的查询方法
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getCustomerId")
    public List<SuggestDto> getCustomerId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("customerId", super.getRequest().getParameter("param"));
        List<SuggestDto> list = mstCustomerNatureService.getCustomerId(param);;
        return list;
    }

    /**
     * 名称的联想文本框的查询方法
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getName")
    public List<SuggestDto> getName() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("customerName", super.getRequest().getParameter("param"));
        List<SuggestDto> list = mstCustomerNatureService.getName(param);;
        return list;
    }

    /**
     * 客服性质配置查询按钮的查询方法 
     * <一句话功能简述> 
     * <功能详细描述>
     * @return Page<MstCustomerNatureDto>
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("searchMstCustomerNature")
    public Page<MstCustomerNatureDto> search() {
        Page<MstCustomerNatureDto> page = mstCustomerNatureService.searchMstCustomerNature(
            RequestUtil.getPageParam());
        return page;
    }

    /**
     * 保存 
     * <一句话功能简述> 
     * <功能详细描述>
     * @param dto 实体类
     * @param br 接口
     * @return AjaxResult
     * @throws WorkFlowException 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) MstCustomerNatureDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<MstCustomerNatureDto> processParameter = new ProcessParameter<MstCustomerNatureDto>();
        WorkFlowParameter<MstCustomerNatureDto> params = new WorkFlowParameter<MstCustomerNatureDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        if (StringUtil.isEmpty(dto.getCustomerId())) {
            dto.setCustomerId(IDGenerator.generate());
        }
        params.setBusinessCode(item -> item.getCustomerId());
        params.setBusinessType(DictCodeList.共通业务类型_客户性质管理.getValue());
        //客户代码list
        List<String> listCustomerId = new ArrayList<String>();
        //客户性质list
        TreeSet<String> listCustomerCharater = new TreeSet<String>();
        //客户名称list
        List<String> listCustomerName = new ArrayList<String>();
        //客户类型list
        List<String> listCustomerType = new ArrayList<String>();
        List<MstCustomerNatureDto> selectedItem = dto.getSelectedItem();
        for (MstCustomerNatureDto mstCustomerNatureDto : selectedItem) {
            Map<String, Object> map = new HashMap<String, Object>();
            //将数据字典里的值放入map中
            map.put("flag_01", DictCodeList.客户性质_发行人.getCodeName());
            map.put("flag_02", DictCodeList.客户性质_融资人.getCodeName());
            map.put("flag_03", DictCodeList.客户性质_管理人.getCodeName());
            map.put("flag_04", DictCodeList.客户性质_托管人.getCodeName());
            map.put("flag_05", DictCodeList.客户性质_担保人.getCodeName());
            map.put("flag_07", DictCodeList.客户性质_承销商.getCodeName());
            map.put("flag_08", DictCodeList.客户性质_存放行.getCodeName());
            map.put("flag_09", DictCodeList.客户性质_投资顾问.getCodeName());
            map.put("flag_06", DictCodeList.客户性质_出质人.getCodeName());
            map.put("flag_10", DictCodeListExt3.客户性质_券商.getCodeName());
            map.put("flag_12", DictCodeListExt3.客户性质_交易对手.getCodeName());
            map.put("flag_13", DictCodeListExt3.客户性质_原始权益人.getCodeName());

            map.put("index", DictCodeList.客户类型_法人.getCodeName());
            map.put("count", DictCodeList.客户类型_自然人.getCodeName());
            //将客户性质的code改成name值放入map中
            if (DictCodeList.客户性质_发行人.getValue().equals(
                mstCustomerNatureDto.getCustomerCharater())) {
                listCustomerCharater.add((String)map.get("flag_01"));
            }
            if (DictCodeList.客户性质_融资人.getValue().equals(
                mstCustomerNatureDto.getCustomerCharater())) {
                listCustomerCharater.add((String)map.get("flag_02"));
            }
            if (DictCodeList.客户性质_管理人.getValue().equals(
                mstCustomerNatureDto.getCustomerCharater())) {
                listCustomerCharater.add((String)map.get("flag_03"));
            }
            if (DictCodeList.客户性质_托管人.getValue().equals(
                mstCustomerNatureDto.getCustomerCharater())) {
                listCustomerCharater.add((String)map.get("flag_04"));
            }
            if (DictCodeList.客户性质_担保人.getValue().equals(
                mstCustomerNatureDto.getCustomerCharater())) {
                listCustomerCharater.add((String)map.get("flag_05"));
            }
            if (DictCodeList.客户性质_出质人.getValue().equals(
                mstCustomerNatureDto.getCustomerCharater())) {
                listCustomerCharater.add((String)map.get("flag_06"));
            }
            if (DictCodeList.客户性质_承销商.getValue().equals(
                mstCustomerNatureDto.getCustomerCharater())) {
                listCustomerCharater.add((String)map.get("flag_07"));
            }
            if (DictCodeList.客户性质_存放行.getValue().equals(
                mstCustomerNatureDto.getCustomerCharater())) {
                listCustomerCharater.add((String)map.get("flag_08"));
            }
            if (DictCodeList.客户性质_投资顾问.getValue().equals(
                mstCustomerNatureDto.getCustomerCharater())) {
                listCustomerCharater.add((String)map.get("flag_09"));
            }
            if (DictCodeListExt3.客户性质_券商.getValue().equals(
                    mstCustomerNatureDto.getCustomerCharater())) {
                    listCustomerCharater.add((String)map.get("flag_10"));
            }
            if (DictCodeListExt3.客户性质_原始权益人.getValue().equals(
                    mstCustomerNatureDto.getCustomerCharater())) {
                listCustomerCharater.add((String)map.get("flag_13"));
            }
            if (DictCodeListExt3.客户性质_交易对手.getValue().equals(
                    mstCustomerNatureDto.getCustomerCharater())) {
                    listCustomerCharater.add((String)map.get("flag_12"));
                  //add by lijie2 start
                  //生成交易对手代码以及交易对手类型，默认市场机构
                    BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                    bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_交易对手管理);
                    if (mstCustomerNatureDto.getCounterId() == null || mstCustomerNatureDto.getCounterId().isEmpty()) {
                    	mstCustomerNatureDto.setCounterId(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(
                            bussinessCodeParameter) : IDGenerator.generate());
                    }
                    mstCustomerNatureDto.setCounterType(DictCodeListExt1.交易对手类型_市场机构.getValue());
                  //add by lijie2 end 
            }
            //将客户代码加入list中，不能重复
            if (listCustomerId.size()<=7 &&  !listCustomerId.contains(mstCustomerNatureDto.getCustomerId()) ) {
                listCustomerId.add(mstCustomerNatureDto.getCustomerId());
            }
            if (listCustomerName.size()<=7 && !listCustomerName.contains(mstCustomerNatureDto.getCustomerName())) {
                listCustomerName.add(mstCustomerNatureDto.getCustomerName());
            }
            //将客户类型code值改成name值
            if (DictCodeList.客户类型_法人.getValue().equals(
                mstCustomerNatureDto.getCustomerType())
                && !listCustomerType.contains((String)map.get("index"))) {
                listCustomerType.add((String)map.get("index"));
            }
            if (DictCodeList.客户类型_自然人.getValue().equals(
                mstCustomerNatureDto.getCustomerType())
                && !listCustomerType.contains((String)map.get("count"))) {
                listCustomerType.add((String)map.get("count"));
            }
        }
        // 设置流程业务说明, entity即传入的dto
        // 业务说明：客户名称(客户代码) + "(" + 客户名称+"("+客户代码+")"+ ")" + "-" + "客户类型" + "-" + "客户类型"+
        // "-" + "客户性质" + "-" + "客户性质list(用逗号隔开)"+ "操作人" + "-" + "环节操作人"+ "日期" + "-" + "new Date()"
        List<String> newListCustomerCharater = new ArrayList<>(listCustomerCharater);

        params.setComment(
            entity -> this.getMessage("MstCustomerNature001.customerId") + "("
                      + StringUtil.joinString(listCustomerId, GlobalConstant.COMMA) + ")" + "-"
                      + "(" + this.getMessage("MstCustomerNature001.customerName") + "("
                      + StringUtil.joinString(listCustomerName, GlobalConstant.COMMA) + ")" + ")"
                      + "-" + this.getMessage("MstCustomerNature001.customerType") + "("
                      + StringUtil.joinString(listCustomerType, GlobalConstant.COMMA) + ")" + "-"
                      + this.getMessage("MstCustomerNature002.customerCharater") + "("
                      + StringUtil.joinString(newListCustomerCharater, GlobalConstant.COMMA) + ")"
                      + "-" + this.getMessage("MstCustomerNature002.operator") + "-"
                      + entity.getUpdateUser() + "-" + this.getMessage("MstCustomerNature002.date")
                      + "-" + DateUtil.format(
                          iPlatformDate != null ? iPlatformDate.getSysDate() : DateUtil.now()));
        processParameter.setWorkFlowParameter(params);
        String msg = mstCustomerNatureService.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * 对前台传入数据进行验证
     * @param dto 实体类
     * @param result 结果集
     * @return AjaxResult
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupDelete.class}) MstCustomerNatureDto dto, BindingResult result) {
    	String  customerCharater =  DictCodeList.客户性质_发行人.getValue();
    	for(MstCustomerNatureDto entity: dto.getSelectedItem())
    	{
            if (customerCharater.equals(entity.getCustomerCharater())
                    && StringUtil.equals(entity.getCustomerType(), DictCodeList.客户类型_自然人.getValue())) {
                return new AjaxResult("客户性质为发行人时，选择数据的客户类型不能为自然人！", MessageType.错误消息);
            }
//	    	String isFinLicenceStr =  mstCustomerNatureService.checkIsFinLicenceStr(entity.getCustomerId());

//	    	if( customerCharater.equals(entity.getCustomerCharater()) && (isFinLicenceStr == null || " ".equals(isFinLicenceStr))){
//	    		 return new AjaxResult("客户性质为发行人时，机构信息中的“是否持有金融牌照”字段不可以为空！", MessageType.错误消息);
//	    	}
	    	//校验是否存在待确认的流程
	    	Map<String, Object> param = RequestUtil.getPageParam();
	        WflValidInputParameter parameter = new WflValidInputParameter();
	        parameter.setPageId(this.getPageId());
	        parameter.setBusinessDataPk1(entity.getCustomerId());
	        if ((param.get("isTodoList") == null || !"true".equals(param.get("isTodoList")))
	            && mstWorkflowCommonService.isInWflProcess(parameter)) {
	    		 return new AjaxResult("该客户已有在流程中的数据！", MessageType.错误消息);
	    	}
    	}
        return commonCheck(result, dto.getOperateType());
    }

}
