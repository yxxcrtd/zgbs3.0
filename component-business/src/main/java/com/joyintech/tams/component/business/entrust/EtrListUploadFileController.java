package com.joyintech.tams.component.business.entrust;

import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/component/entrust/EtrListUploadFile")
public class EtrListUploadFileController extends BaseController {

    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;

    @Override
    public String init(Model model) {
        String rowData = getParam("rowData");
        String businessId = getParam("businessId");

        model.addAttribute("rowData", rowData);
        model.addAttribute("businessId", businessId);
        return "component/entrust/EtrListUploadFile";
    }

    /**
     * 获取文件的唯一标识
     *
     * @return businessId
     */
    @ResponseBody
    @RequestMapping("/getBusinessId")
    public Map<String, String> getBusinessId(@RequestBody List<SysUploadFileInfo> fileArray) {
        //uuid 去掉连接线，防止值过大
        String generateId = IDGenerator.generate();
        //文件信息
        if (CollectionUtils.isNotEmpty(fileArray)) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    fileArray.stream().map(SysUploadFileInfo::getSeqNo).collect(
                            Collectors.toList()), generateId);
        }
        Map<String, String> map = new HashMap<>();
        map.put("businessId", generateId);
        return map;
    }

}
