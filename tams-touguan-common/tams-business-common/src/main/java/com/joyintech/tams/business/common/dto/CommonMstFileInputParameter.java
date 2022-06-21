package com.joyintech.tams.business.common.dto;

import com.joyintech.tams.common.base.entity.SysUploadFileInfo;

import java.util.List;

/**
 * 附件信息共通函数输入参数
 */
public class CommonMstFileInputParameter {
    /**
     * 业务编号
     */
    private String bussinessId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件List(全路径)
     */
    private List<CommonUploadFileInfo> fileList;



    /**
     * 获取 fileList
     * @return fileList.
     */
    public List<CommonUploadFileInfo> getFileList() {
        return fileList;
    }



    /**
     * 设置 fileList
     * @param fileList fileList
     */
    public void setFileList(List<CommonUploadFileInfo> fileList) {
        this.fileList = fileList;
    }


    /**
     * 获取 bussinessId
     * @return bussinessId.
     */
    public String getBussinessId() {
        return bussinessId;
    }


    /**
     * 设置 bussinessId
     * @param bussinessId bussinessId
     */
    public void setBussinessId(String bussinessId) {
        this.bussinessId = bussinessId;
    }


    /**
     * 获取 fileName
     * @return fileName.
     */
    public String getFileName() {
        return fileName;
    }



    /**
     * 设置 fileName
     * @param fileName fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
