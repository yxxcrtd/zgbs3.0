/*
 * 文件名：PtlManagementInfo002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.portfolio.ptlmanagementinfo.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
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

import com.joyintech.tams.common.base.dao.PtlValParamMapper;
import com.joyintech.tams.common.base.entity.PtlValParam;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.portfolio.ptlmanagementinfo.dto.NodeDto;
import com.joyintech.tams.invest.portfolio.ptlmanagementinfo.dto.PtlManagementInfoDto;
import com.joyintech.tams.invest.portfolio.ptlmanagementinfo.service.PtlManagementInfo001Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 组合管理新建
 * @author fanshuanghu
 * @version 1.0 2018年9月11日
 * @see PtlManagementInfo002Controller
 * @since 1.0
 */
@Controller
@PageId("PtlManagementInfo002")
@RequestMapping("/invest/portfolio/ptlmanagementinfo/PtlManagementInfo002")
public class PtlManagementInfo002Controller extends BaseController{
    @Autowired
    private PtlManagementInfo001Service ptlManagementInfo001Service;

    @Autowired
    private PtlValParamMapper ptlValParamMapper;

    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 界面初始化
     * {@inheritDoc}
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model){
        List<SelectOptionDto> selectOptDto = ptlManagementInfo001Service.getUserList();
//        Map<String,List<SelectOptionDto>> map1 = selectOptDto.stream().collect(JoyinCollectors.groupingBy(x->x.getObject().toString()));
//        model.addAttribute("map1",map1);
        this.setSelectOptionToModel(model, DictCode.组合类型,DictCode.收益类型
                                    ,DictCode.募集方式,DictCode.投资性质
                                    ,DictCode.开放类型,DictCode.是否
                                    ,DictCode.尾数处理方式,DictCode.组合系列);
        model.addAttribute("YES", DictCodeList.是否_是.getValue());
        String portfolioId = getParam("portfolioId");
        // 判断是否存在于留痕表
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList()&&StringUtil.isNotEmpty(workflowId)) {
            PtlManagementInfoDto dto = ptlManagementInfo001Service.getBussinessEntityByWorkflowId(
                workflowId);
            Map<String, Object> params = new HashMap<>();
            params.put("portfolioId", dto.getPortfolioId());
            PtlManagementInfoDto dto1 = ptlManagementInfo001Service.getInitInfo(params);
            if (dto.getIsGuarantee() == null) {
                dto.setIsGuarantee(dto1.getIsGuarantee());
            }
            if("N".equals(dto.getIsGuarantee())){
                dto.setIsGuarantee("false");
            }else{
                dto.setIsGuarantee("true");
            }
            if (dto.getPtlValParam() == null) {
                PtlValParam ptlValParam = ptlValParamMapper.selectByPrimaryKey(dto.getPortfolioId());
                dto.setPtlValParam(ptlValParam);
            }
            if(null!=dto.getPtlValParam()){
            	if("N".equals(dto.getPtlValParam().getIsCountingCurInt())){
                    dto.getPtlValParam().setIsCountingCurInt("false");
                }else{
                    dto.getPtlValParam().setIsCountingCurInt("true");
                }
            }
            model.addAttribute("userIdList",dto.getUserId() == null ? dto1.getUserId() : dto.getUserId());
            model.addAttribute("ptlValParam",dto.getPtlValParam());
            model.addAttribute("funDto", dto.getFundAccountInfoDto() == null ? dto1.getFundAccountInfoDto() : dto.getFundAccountInfoDto());
            model.addAttribute("secDto",dto.getSecuritiesAccountInfoDto() == null ? dto1.getSecuritiesAccountInfoDto() : dto.getSecuritiesAccountInfoDto());
            model.addAttribute("entity", dto);
            // 判断逐渐是否为空
        }
        else if (StringUtil.isNotEmpty(portfolioId)) {
            // 将金融代码和固定格式的日期字符串传入参数集合
            Map<String, Object> map = RequestUtil.getParameters(getRequest());
            // 根据估值日和金融代码进行查找
            PtlManagementInfoDto dto = ptlManagementInfo001Service.getInitInfo(map);
//            List<SelectOptionDto> list = ptlManagementInfo001Service.selectCreditORgPtl(portfolioId);
//            String str = "";
//            if(list != null){
//            	for(int i=0; i<list.size(); i++){
//                	str = str +","+ list.get(i).getId();
//                }
//            }
//            dto.setSeqno(str);
            if(dto!=null){
                if("N".equals(dto.getIsGuarantee())){
                    dto.setIsGuarantee("false");
                }
                PtlValParam ptlValParam = ptlValParamMapper.selectByPrimaryKey(portfolioId);
                if(ptlValParam != null && "N".equals(ptlValParam.getIsCountingCurInt())){
                    ptlValParam.setIsCountingCurInt("false");
                }
                model.addAttribute("userIdList", dto.getUserId());
                model.addAttribute("ptlValParam",ptlValParam);
                model.addAttribute("funDto", dto.getFundAccountInfoDto());
                model.addAttribute("secDto",dto.getSecuritiesAccountInfoDto());
                model.addAttribute("entity", dto);
            }
        }
        model.addAttribute("profitType01", DictCodeList.收益类型_净值型.getValue());
        model.addAttribute("profitType02", DictCodeList.收益类型_收益型.getValue());
        model.addAttribute("profitType03", DictCodeList.收益类型_货币型.getValue());
        model.addAttribute("loginUser", LoginUserUtil.getUserName());
        // 初始化
        return super.init(model);
    }

    /**
     * 获取树节点
     * @return 获取用户树
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getTree")
    public List<TreeNode<NodeDto>> getUser(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return ptlManagementInfo001Service.getTree(map);
    }

    /**
     * 画面点击保存按钮时check
     *
     * @param dto 实体类
     * @param result
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) PtlManagementInfoDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 保存
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) PtlManagementInfoDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(dto.getIsGuarantee() == null){
            dto.setIsGuarantee(DictCodeListExt1.本金收益特征_非保本.getValue());
        }
        //文件信息
        if (CollectionUtils.isNotEmpty(dto.getFileArray())
                && StringUtil.isNotEmpty(dto.getPortfolioId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    dto.getFileArray().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    dto.getPortfolioId());
        }
        ProcessParameter<PtlManagementInfoDto> processParameter = new ProcessParameter<PtlManagementInfoDto>();
        WorkFlowParameter<PtlManagementInfoDto> params = new WorkFlowParameter<PtlManagementInfoDto>();
        params.setPageId("PtlManagementInfo002");
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getPortfolioId());
        params.setBusinessType(DictCodeList.共通业务类型_组合管理.getValue());
        // 设置流程业务说明, 组合名称 + "(" + 组合名称 + ")" + "-"  + "组合代码" + "("  + "组合代码"+")"
        params.setComment(
            entity -> this.getMessage("PtlManagementInfo001.portfolioName") + "("
                      + entity.getPortfolioName() + ")" + "-"
                      + this.getMessage("PtlManagementInfo001.portfolioId") + "("
                      + entity.getPortfolioId() + ")" );
        processParameter.setWorkFlowParameter(params);
        String msg = ptlManagementInfo001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}
