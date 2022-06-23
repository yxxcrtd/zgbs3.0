package com.joyintech.tams.invest.bankuplink.reponew.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.tams.invest.bankuplink.reponew.dto.CfetsRepoDealQuotedPrFileDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

/**
 * 附件信息--业务组件
 * @author chenjiaixng
 * @version 1.0 2021年8月17日
 * @see CfetsRepoDealQuotedPrFileController
 * @since 1.0
 */
@Controller
@PageId("CfetsRepoDealQuotedPrFile")
@RequestMapping("/invest/bankuplink/reponew/CfetsRepoDealQuotedPrFile")
public class CfetsRepoDealQuotedPrFileController extends BaseController{

    /**
     * FileAttachmentProcess
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    
    @Override
    public String init(Model model) {
        String businessId = getParam("businessId");
        model.addAttribute("businessId", businessId);
        return super.init(model);
    }

    /**
     * 文件与业务编号关联
     * @param fileDto
     * @param br
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody CfetsRepoDealQuotedPrFileDto fileDto, BindingResult br)
            throws Exception {
        // 附件不为空
        if (fileDto != null && CollectionUtils.isNotEmpty(fileDto.getFileArray())) {
            // 将文件与业务编号关联
            fileAttachmentProcess.updateBussinessNo(
                    fileDto.getFileArray().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    fileDto.getBusinessId());
        }
        return new AjaxResult("附件上传成功");
    }
}
