/*
 * 文件名：MstSecuritiesAccount001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangjun
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.basicdata.mstsecuritiesaccount.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.collections4.CollectionUtils;
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
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.basicdata.mstsecuritiesaccount.dto.MstSecuritiesAccountDto;
import com.joyintech.tams.invest.basicdata.mstsecuritiesaccount.service.MstSecuritiesAccountService;
import com.joyintech.workflow.engine.constant.WflConstants;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.ProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import com.joyintech.tams.invest.basicdata.mstsaledepartment.service.MstSaleDepartmentService;
import com.joyintech.framework.util.JsonUtils;


/**
 * 证券账户管理
 * 〈功能详细描述〉
 * @author zhangjun
 * @version 1.0 2018年9月11日
 * @see MstSecuritiesAccount002Controller
 * @since 1.0
 */
@Controller
@PageId("MstSecuritiesAccount002")
@RequestMapping("/invest/basicdata/mstsecuritiesaccount/MstSecuritiesAccount002")
public class MstSecuritiesAccount002Controller extends BaseController{
    /**
     * 证券管理Service
     */
    @Autowired
    private MstSecuritiesAccountService mstSecuritiesAccountService;

    /**
     * 附件处理接口
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
	
   /**
	 * 营业部Service获取券商
	 */
	@Autowired
	private MstSaleDepartmentService mstSaleDepartmentService;

    @Autowired
    private ProcessService processService;

    /**
     * 新建页面初始化
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        //单选框枚举值设置
        this.setSelectOptionToModel(model, DictCode.登记托管机构,DictCode.交易场所,DictCode.账户来源,DictCode.是否,DictCode.是否开通港股通);
		List<SelectOptionDto> brokerIds = getMstCustomerInfo();
		model.addAttribute("brokerIds", JsonUtils.objectArray2Json(brokerIds.toArray()));
        String secAcctId = getParam("secAcctId");
        String workflowId = getParam("workflowId");
        //是否双录（01：是，02：否）
        String isDoubleRecord = CacheUtil.getSysParam("DOUBLE_RECORD");
        //是否为第一个审批节点
        String isFirstApprovalNode = DictCodeList.是否_否.getValue();

        model.addAttribute("isDoubleRecord",isDoubleRecord);
      //不来自于代办
        if(WorkFlowUtil.isNotFromToDoList()&& StringUtil.isNotEmpty(secAcctId)){
            //根据主键获取业务实体
            MstSecuritiesAccountDto entity = mstSecuritiesAccountService.selectBySecAcctId(secAcctId);
            model.addAttribute("entity",entity);
        }
        //来自代办
        else if(StringUtil.isNotEmpty(workflowId)){
            //根据工作流与留痕处理提供的共通方法获取业务实体
            MstSecuritiesAccountDto entity = mstSecuritiesAccountService.getBussinessEntityByWorkflowId(workflowId);
            SysWflBusinessList workFlowData = mstSecuritiesAccountService.getWorkFlowData(workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);
            if(!StringUtil.equals(workFlowData.getActionType(),"delete")) {//删除不开启双录
	            //双录
	            try {
	                //获取当前节点信息
	                //工作流ID
	                String taskId = this.getParam(WflConstants.PROCESS_TASK_ID);
	
	                String proDefKey = processService.getProDefKeyByProcessInstanceId(workFlowData.getWorkflowId());
	
	                ProcessMark processMark = mstSecuritiesAccountService.getBusinessInfo(workflowId);
	                String secondTaskDefinitionId = processService.getSecondTaskDefinitionId(proDefKey,
	                        mstSecuritiesAccountService.getRuntimeVariables(processMark, taskId));
	                isFirstApprovalNode = StringUtil.equals(workFlowData.getCurNodeId(), secondTaskDefinitionId)
	                        ?DictCodeList.是否_是.getValue():DictCodeList.是否_否.getValue();
	            } catch (WorkFlowException e) {
	                e.printStackTrace();
	            }
            }
        }
        model.addAttribute("isFirstApprovalNode",isFirstApprovalNode);
        return super.init(model);
    }
    /**
     * 回款账户以及资金账户联想下拉框 <功能详细描述>
     * @param search String
     * @return List
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getBusiAcctNo")
    public List<SelectOptionDto> getBusiAcctNo(String search){
        List<SelectOptionDto> temp = mstSecuritiesAccountService.getBusiAcctNo(search);
        return temp;
    }
    
    /**
     * 券商名称联想下拉框
     * @param search
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/getBrokerName")
    public List<SelectOptionDto> getBrokerName(String search)throws Exception{
    	List<SelectOptionDto> temp=mstSecuritiesAccountService.getBrokerName(search);
    	return temp;
    }
    
    /**
     * 保存按钮点击时的check
     *
     * @param dto 业务实体
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) MstSecuritiesAccountDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    /**
     * 
     * 保存
     * <功能详细描述>
     * @param dto 业务实体
     * @param br AjaxResult
     * @return AjaxResul
     * @throws WorkFlowException WorkFlowException
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) MstSecuritiesAccountDto dto, BindingResult br) throws WorkFlowException {
        if(br.hasErrors()){
            return new AjaxResult(br);
        }
     // 如果文件编号为空的情况下，需要自动生成一个文件编号，也就是新建，修改时是有这个文件编号的，唯一
//        if (StringUtil.isEmpty(dto.getSeqNo())) {
//            BussinessCodeParameter param = new BussinessCodeParameter();
//            param.setCommonBusinessType(DictCodeList.共通业务类型_证券账户管理);
//            String code = bussGetBusinessIdService != null ? bussGetBusinessIdService.buildBussinessCode(
//                param) : IDGenerator.generate();;
//            dto.setSeqNo(code);
//        }
        dto.setBussinessId(dto.getSeqNo());
        dto.setSecAcctType(DictCodeListExt3.证券账户类型_证券账户.getValue());
        ProcessParameter<MstSecuritiesAccountDto> processParameter = new ProcessParameter<MstSecuritiesAccountDto>();
        WorkFlowParameter<MstSecuritiesAccountDto> params = new WorkFlowParameter<MstSecuritiesAccountDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item->item.getSecAcctId());
        params.setBusinessType(DictCodeList.共通业务类型_证券账户管理.getValue());

        if (CollectionUtils.isNotEmpty(dto.getFileArray())
            && StringUtil.isNotEmpty(dto.getSecAcctId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                dto.getFileArray().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                dto.getSecAcctId());
        }
        
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
            entity -> SpringContextUtil.getTextValue("MstSecuritiesAccount001.secAcctId")+ "(" +entity.getSecAcctId()+ ")" 
                +  "-" + SpringContextUtil.getTextValue("MstSecuritiesAccount001.secAcctName")+ "(" + entity.getSecAcctName() + ")"  +  "-" + 
                SpringContextUtil.getTextValue("MstSecuritiesAccount001.registOrg")+ 
                "(" + SysDataUtil.getNameFromDataDict(DictCode.登记托管机构,entity.getRegistOrg()) + ")");
        processParameter.setWorkFlowParameter(params);
        String msg = mstSecuritiesAccountService.start(processParameter, dto);
        return new AjaxResult(msg);
    }
//    /**
//     * 文件上传
//     *
//     * @param request 请求
//     * @param response 响应
//     * @param model ModelAndView
//     * @return String
//     * @see
//     * @since 1.0
//     */
//    @RequestMapping("uploadFile")
//    @ResponseBody
//    public String uploadFile(HttpServletRequest request,
//            HttpServletResponse response, ModelMap model) throws Exception {
//        progressBarService.updatePercent(this.getPageId(), ProgressBarService.FILE_UPLOAD_ID, 0.5D, false);
//        progressBarService.removePercent(this.getPageId(), ProgressBarService.FILE_UPLOAD_ID);
//        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
//        mstFileCommonService.uploadFile(mstFileInputParameter);
//        return "Y";
//    }
    

//    /**
//     * 查询上传的文件
//     *
//     * @param businessId String
//     * @return String
//     * @see
//     * @since 1.0
//     */
//    @RequestMapping("selectFileByBusinessId")
//    @ResponseBody
//    public FileDto selectFileByBusinessId(String businessId) throws Exception {
//        return mstSecuritiesAccountService.selectFileByBusinessId(businessId);
//    }
//    /**
//     * 插入附件文件
//     *
//     * @param dto MstSecuritiesAccountDto
//     * @return String
//     * @see
//     * @since 1.0
//     */
//    @RequestMapping("insertIntoFile")
//    @ResponseBody
//    public int insertIntoFile(FileDto dto) throws Exception {
//        return mstSecuritiesAccountService.insertIntoFile(dto);
//    }
//    /**
//     * 删除上传的附件文件
//     *
//     * @param businessId String
//     * @return String
//     * @see
//     * @since 1.0
//     */
//    @RequestMapping("deleteFileByBusinessId")
//    @ResponseBody
//    public int deleteFileByBusinessId(String businessId) throws Exception {
//        return mstSecuritiesAccountService.deleteFileByBusinessId(businessId);
//    }
	/**
	 * 券商
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SelectOptionDto> getMstCustomerInfo(){
		List<SelectOptionDto> brokerLst = new ArrayList<SelectOptionDto>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			brokerLst = mstSaleDepartmentService.getMstCustomerInfo(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return brokerLst;
	}
	
    /**
    *
    * @Description //期货公司下拉框
    * @Param [search]
    * @return java.util.List<com.joyintech.framework.select.SelectOptionDto>
    * @version 1.0
    * @see
   **/
   @ResponseBody
   @RequestMapping("/getFuturesCompany")
   public List<SelectOptionDto> getFuturesCompany(String search)
           throws Exception {
       List<SelectOptionDto> temp = mstSecuritiesAccountService.getFuturesCompany(search);
       return temp;
   }
}
