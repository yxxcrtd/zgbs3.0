/*
 * 文件名：MstCustomerNature003Controller.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：
 * 修改时间：2018年7月23日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */
package com.joyintech.tams.invest.basicdata.mstcustomernature.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.basicdata.msttradeopponent.dto.MstTradeOpponentDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import org.apache.commons.collections.CollectionUtils;
import org.h2.util.StringUtils;
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
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.invest.basicdata.mstcustomernature.dto.MstCustomerNatureDto;
import com.joyintech.tams.invest.basicdata.mstcustomernature.service.MstCustomerNatureService;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.service.LeaveMarkProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 客户性质修改 
 * @author huwei
 * @version 1.0 2018年8月22日
 * @see MstCustomerNature003Controller
 * @since 1.0
 */
@Controller
@PageId("MstCustomerNature003")
@RequestMapping("/invest/basicdata/mstcustomernature/MstCustomerNature003")
public class MstCustomerNature003Controller extends BaseController {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    @Autowired
    private MstCustomerNatureService mstCustomerNatureService;

    
    /**
     * mstWorkflowCommonService工作流组件
     */
    @Autowired(required = false)
    private LeaveMarkProcessService leaveMarkProcessService;
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
    	setSelectOptionToModel(model, DictCode.客户性质);
        String customerId = getParam("customerId");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("customerId", customerId);
        
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
//      添加工作流代码的判断
        if (!WorkFlowUtil.isNotFromToDoList()&&StringUtil.isNotEmpty(workflowId)) {
            MstCustomerNatureDto dto = mstCustomerNatureService.getBussinessEntityByWorkflowId(
                workflowId);
            model.addAttribute("entity", dto);
            model.addAttribute("entityDate", JsonUtils.object2Json(dto));
        }
        //走自己的查询方法
        else if (StringUtil.isNotEmpty(customerId)) {
            MstCustomerNatureDto dto = mstCustomerNatureService.selectByPrimaryKey(map);
            model.addAttribute("entity", dto);
            model.addAttribute("entityDate", JsonUtils.object2Json(dto));
        }
        
        model.addAttribute("CUSTOMER_NATURE_01", DictCodeList.客户性质_发行人.getValue());
        model.addAttribute("CUSTOMER_NATURE_02", DictCodeList.客户性质_融资人.getValue());
        model.addAttribute("CUSTOMER_NATURE_03", DictCodeList.客户性质_管理人.getValue());
        model.addAttribute("CUSTOMER_NATURE_04", DictCodeList.客户性质_托管人.getValue());
        model.addAttribute("CUSTOMER_NATURE_05", DictCodeList.客户性质_担保人.getValue());
        model.addAttribute("CUSTOMER_NATURE_06", DictCodeList.客户性质_出质人.getValue());
        model.addAttribute("CUSTOMER_NATURE_07", DictCodeList.客户性质_承销商.getValue());
        model.addAttribute("CUSTOMER_NATURE_08", DictCodeList.客户性质_存放行.getValue());
        model.addAttribute("CUSTOMER_NATURE_09", DictCodeList.客户性质_投资顾问.getValue());
        model.addAttribute("CUSTOMER_NATURE_10", DictCodeListExt3.客户性质_券商.getValue());
        model.addAttribute("CUSTOMER_NATURE_12", DictCodeListExt3.客户性质_交易对手.getValue());
        model.addAttribute("CUSTOMER_NATURE_13", DictCodeListExt3.客户性质_原始权益人.getValue());
        model.addAttribute("CUSTOMER_TYPE_01", DictCodeList.客户类型_法人.getValue());
        model.addAttribute("CUSTOMER_TYPE_02", DictCodeList.客户类型_自然人.getValue());
        return super.init(model);
    }

    /**
     * 保存 
     * <一句话功能简述> 
     * <功能详细描述>
     * @param dto 实体类
     * @param br 结果集
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
        List<MstCustomerNatureDto> selectedItem = dto.getSelectedItem();
        List<String> listCustomerCharater = new ArrayList<String>();
        List<String> listCustomerType = new ArrayList<String>();
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
        //查找原客户性质配置情况；
        Map<String, Object> query = new HashMap<String, Object>();
        query.put("customerId", dto.getCustomerId());
    	MstCustomerNatureDto mstCustomerNatureOld = mstCustomerNatureService.selectByPrimaryKey(query);
    	
        
        for (MstCustomerNatureDto mstCustomerNatureDto : selectedItem) {
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
            if (DictCodeListExt3.客户性质_交易对手.getValue().equals(
                    mstCustomerNatureDto.getCustomerCharater())) {
                    listCustomerCharater.add((String)map.get("flag_12"));
            }
            if (DictCodeListExt3.客户性质_原始权益人.getValue().equals(
                    mstCustomerNatureDto.getCustomerCharater())) {
                listCustomerCharater.add((String)map.get("flag_13"));
            }
            //listCustomerCharater.add(mstCustomerNatureDto.getCustomerCharater());
            //原来不是交易对手，现在是交易对手，新建
            if(StringUtils.isNullOrEmpty(mstCustomerNatureOld.getIsCounter()) && DictCodeListExt3.客户性质_交易对手.getValue().equals(mstCustomerNatureDto.getCustomerCharater())){
            	  //add by lijie2 start
                //生成交易对手代码以及交易对手类型，默认市场机构
                  BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                  bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_交易对手管理);
                  String counterId = null;
                  if (dto.getCounterId() == null || dto.getCounterId().isEmpty()) {
                   counterId = baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) : IDGenerator.generate();
                  	dto.setCounterId(counterId);
                  }
                  dto.setCounterType(DictCodeListExt1.交易对手类型_市场机构.getValue());
                  dto.setCounterId(counterId);
                //add by lijie2 end 
            }
        }
        ////原来是交易对手，现在不再是交易对手
        if(StringUtil.isNotEmpty(mstCustomerNatureOld.getIsCounter()) && !listCustomerCharater.contains(DictCodeListExt3.客户性质_交易对手.getValue())){
        	List<MstTradeOpponentDto> lst = mstCustomerNatureService.selectCounterIdByCustomer(query);
        	if(lst != null && lst.size() > 0){
        		dto.setCounterId(lst.get(0).getCounterId());
        	}
        }
        // 将客户性质的code改为name
        if (DictCodeList.客户类型_法人.getValue().equals(dto.getCustomerType())) {
            listCustomerType.add((String)map.get("index"));
        }
        if (DictCodeList.客户类型_自然人.getValue().equals(dto.getCustomerType())) {
            listCustomerType.add((String)map.get("count"));
        }
        // 设置流程业务说明, entity即传入的dto
        // 业务说明：客户名称(客户代码) + "(" + 客户名称+"("+客户代码+")"+ ")" + "-" + "客户类型" + "-" + "客户类型"+
        // "-" + "客户性质" + "-" + "客户性质list(用逗号隔开)"
        params.setComment(
            entity -> this.getMessage("MstCustomerNature001.customerId") + "("
                      + entity.getCustomerId() + ")" + "-"
                      + this.getMessage("MstCustomerNature001.customerName") + "("
                      + entity.getCustomerName() + ")" + "-"
                      + this.getMessage("MstCustomerNature001.customerType") + "("
                      + StringUtil.joinString(listCustomerType, GlobalConstant.COMMA) + ")" + "-"
                      + this.getMessage("MstCustomerNature002.customerCharater") + "("
                      + StringUtil.joinString(listCustomerCharater, GlobalConstant.COMMA) + ")");
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
//    	Map<String, Object> param = RequestUtil.getPageParam();
//    	List<String> statusList = new ArrayList<String>();
//    	param.put("pageId",this.getPageId());
//    	param.put("customerId",dto.getCustomerId());
//    	statusList.add(DictCodeList.数据状态_新建中.getValue());
//    	statusList.add(DictCodeList.数据状态_修改中.getValue());
//    	statusList.add(DictCodeList.数据状态_删除中.getValue());
//    	param.put("statusList", statusList);
//    	int status =  mstCustomerNatureService.checkwflstatus(param);
//    	if(status > 0){
//    		 return new AjaxResult("该客户已有在流程中的数据！", MessageType.错误消息);
//    	}
//
       if (CollectionUtils.isNotEmpty( dto.getSelectedItem())) {
           List<MstCustomerNatureDto> mstCustomerNatureDtoList =  dto.getSelectedItem().stream()
                   .filter(item->StringUtil.equals(item.getCustomerCharater(),DictCodeListExt3.客户性质_交易对手.getValue())).collect(Collectors.toList());
           if (CollectionUtils.isEmpty(mstCustomerNatureDtoList)) {
//               int status = mstCustomerNatureService.selectCounter(dto.getCustomerId());
//               if (status > 0) {
//                   return new AjaxResult("选择的记录已经被使用，不允许删除客户性质为交易对手的记录！", MessageType.错误消息);
//               }
           }
       }else{
//           int status = mstCustomerNatureService.selectCounter(dto.getCustomerId());
//           if (status > 0) {
//               return new AjaxResult("选择的记录已经被使用，不允许删除客户性质为交易对手的记录！", MessageType.错误消息);
//           }
       }
        if (WorkFlowUtil.isNotFromToDoList()) {
            
            ProcessMark processMark = new ProcessMark();
            processMark.setBusinessDataPk1(dto.getCustomerId());
           
            List<String> resultList =leaveMarkProcessService.getBusinessDataInFlow(processMark);
            
            if (!StringUtil.isEmptyList(resultList)) {
                return new AjaxResult("该客户已有在流程中的数据！", MessageType.错误消息);
            }

            for(MstCustomerNatureDto entity: dto.getSelectedItem()) {
                String  customerCharater =  DictCodeList.客户性质_发行人.getValue();

                if (customerCharater.equals(entity.getCustomerCharater())) {
                    if (StringUtil.equals(dto.getCustomerType(), DictCodeList.客户类型_自然人.getValue())) {
                        return new AjaxResult("客户性质为发行人时，选择数据的客户类型不能为自然人！", MessageType.错误消息);
                    }
//                    String isFinLicenceStr = mstCustomerNatureService.checkIsFinLicenceStr(dto.getCustomerId());
//                    if(StringUtil.isEmpty(isFinLicenceStr)) {
//                        return new AjaxResult("客户性质为发行人时，机构信息中的“是否持有金融牌照”字段不可以为空！", MessageType.错误消息);
//                    }
                }
            }
        }
        return commonCheck(result, dto.getOperateType());
    }

}
