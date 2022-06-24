package com.joyintech.tams.invest.product.rddesign.feasibilityanalysis.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.product.rddesign.feasibilityanalysis.dto.FinProductFeaAna002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class FinProductFeaAna002Service extends BaseService<FinProductFeaAna002Dto> implements BaseProcessService<FinProductFeaAna002Dto> {

    /**
     * 附件上传类
     */
    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 根据产品需求编号查询
     * @param requirementId
     * @return
     */
    public FinProductFeaAna002Dto queryByRequirementId(String requirementId){
        return restTemplate.getForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeaAna/queryByRequirementId?id=" + requirementId,
                FinProductFeaAna002Dto.class
        );
    }

    @Override
    public void doComplete(FinProductFeaAna002Dto dto, OperationType type) {
        //维护分析结论、可行需求分类
        restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeaAna/update",dto,Integer.class);
    }

    @Override
    public void doUpdate(FinProductFeaAna002Dto entity) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())) {
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getRequirementId());
        }
    }

    public AjaxResult delete(String requirementId){
        Integer i = restTemplate.getForObject(ServiceApplication.PRODUCT_SERVICE_NAME +"/FinProductFeaAna/delete?requirementId=" + requirementId,Integer.class) ;
        AjaxResult result = new AjaxResult();
        if(i > 0){
            result.setMessage("数据删除成功！");
            result.setMessageType(MessageType.提示消息);
        }else {
            result.setMessage("数据删除失败！");
            result.setMessageType(MessageType.错误消息);
        }
        return result;
    }
}
