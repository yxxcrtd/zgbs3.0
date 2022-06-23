/*
 * 文件名：MstFileCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：dingzhipeng
 * 修改时间：2018年8月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.common.component.base.core.impl;

import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.core.impl.dao.MstFileCommonServiceMapperImpl;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.MstFileOutputParameter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 附件信息共通
 * 〈功能详细描述〉
 * @author dingzhipeng
 * @version 1.0 2018年8月27日
 * @see MstFileCommonServiceImpl
 * @since 1.0
 */
@Component
public class MstFileCommonServiceImpl implements MstFileCommonService {
    
    /**
     * 附件信息共通mapper
     */
    @Autowired(required = false)
    private MstFileCommonServiceMapperImpl mstFileCommonServiceMapper;
    
    /**
     * 附件信息共通mapper
     */
    @Autowired(required = false)
    private MstFileCommonServiceImpl mstFileCommonImplService;
    
    /**
     * 
     * 上传附件
     * <功能详细描述>
     * @param mstFileInputParameter 函数输入参数
     * @return 文件列表
     * @see
     * @since 1.0
     */
    @Override
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public MstFileOutputParameter updateBusinessId(MstFileInputParameter mstFileInputParameter){
        if(mstFileInputParameter != null && CollectionUtils.isNotEmpty(mstFileInputParameter.getFileList())){
        	List<SysUploadFileInfo> uploadFileList = new ArrayList<>();
            mstFileInputParameter.getFileList().forEach(item->{
                if(StringUtil.isNotEmpty(item.getSeqNo())) {
                    SysUploadFileInfo uploadFile = new SysUploadFileInfo();
                    uploadFile.setBusinessId(StringUtil.getOrElse(item.getBusinessId(), mstFileInputParameter.getBussinessId()));
                    uploadFile.setSeqNo(item.getSeqNo());
                    uploadFileList.add(uploadFile);
                }
            });
            MstFileOutputParameter result = new MstFileOutputParameter();
            if (!uploadFileList.isEmpty()) {
                result.setUploadFileList(uploadFileList);
                mstFileCommonServiceMapper.updateFileBusinessId(result);
            }
            return result;
        }
        return null;
    }
    /**
     * 根据参数获取文件信息
     * <功能详细描述>
     * @param mstFileInputParameter 参数
     * @return 文件列表
     * @see
     * @since 1.0
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public MstFileOutputParameter getUploadInfo(MstFileInputParameter mstFileInputParameter) {
        MstFileOutputParameter result = new MstFileOutputParameter();
        List<SysUploadFileInfo> uploadFileList = mstFileCommonServiceMapper.getUploadInfo(mstFileInputParameter);
        result.setUploadFileList(uploadFileList);
        return result;
    }

    /**
     * 
     * 获取附件(业务编码)
     * <功能详细描述>
     * @param bussinessId 业务编号
     * @return 文件列表
     * @see
     * @since 1.0
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    @Override
    public MstFileOutputParameter getFileInfoByBusiId(String bussinessId){
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setBussinessId(bussinessId);
        return mstFileCommonImplService.getUploadInfo(mstFileInputParameter);
    }
    
    /**
     * 
     * 获取附件(通过文件名)
     * <功能详细描述>
     * @param fileName 文件名
     * @return 文件信息
     * @see
     * @since 1.0
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    @Override
    public MstFileOutputParameter getFileInfoByFileName(String fileName){
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setFileName(fileName);
        return mstFileCommonImplService.getUploadInfo(mstFileInputParameter);
    }
}
