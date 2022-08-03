/*
 * 文件名：MstFileOutputParameter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：dingzhipeng
 * 修改时间：2018年8月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.dto;

import java.util.List;

import com.joyintech.tams.common.base.entity.SysUploadFileInfo;

/**
 * 附件信息共通函数输出参数
 * 〈功能详细描述〉
 * @author dingzhipeng
 * @version 1.0 2018年8月27日
 * @see MstFileOutputParameter
 * @since 1.0
 */
public class MstFileOutputParameter {

    /**
     * 业务代码
     */
    private String businessId;
    /**
     * 文件列表
     */
    private List<SysUploadFileInfo> uploadFileList;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    /**
     * 获取 uploadFileList
     * @return uploadFileList.
     */
    public List<SysUploadFileInfo> getUploadFileList() {
        return uploadFileList;
    }


    /**
     * 设置 uploadFileList
     * @param uploadFileList uploadFileList
     */
    public void setUploadFileList(List<SysUploadFileInfo> uploadFileList) {
        this.uploadFileList = uploadFileList;
    }
}
