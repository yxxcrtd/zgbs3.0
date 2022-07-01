package com.joyintech.tams.valuation.component.business.service;

import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.valuation.component.business.dao.MstFileCommonServiceMapperImpl;
import com.joyintech.tams.valuation.component.business.dto.MstFileInputParameter;
import com.joyintech.tams.valuation.component.business.dto.MstFileOutputParameter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ComponentWorkflowCommonService
 * @Description: 业务组件工作流相关service
 * @Author yanya
 * @Date 2021/3/4 0004 
 * @Version V1.0
 **/
@Service("valuationComponentMstFileCommonService")
@Transactional(rollbackFor = Exception.class)
public class MstFileCommonService {

    /**
     * 附件信息共通mapper
     */
    @Autowired(required = false)
    MstFileCommonServiceMapperImpl mstFileCommonServiceMapper;
    /**
     *
     * 更新文件业务代码
     * @param mstFileInputParameter 函数输入参数
     * @return 文件列表
     * @see
     * @since 1.0
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public MstFileOutputParameter updateBusinessId(MstFileInputParameter mstFileInputParameter) {
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
    public MstFileOutputParameter getFileInfoByBusiId(String bussinessId) {
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setBussinessId(bussinessId);
        return this.getUploadInfo(mstFileInputParameter);
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
     * 获取附件(通过文件名)
     * <功能详细描述>
     * @param fileName 文件名
     * @return 文件信息
     * @see
     * @since 1.0
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public MstFileOutputParameter getFileInfoByFileName(String fileName) {
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setFileName(fileName);
        return this.getUploadInfo(mstFileInputParameter);
    }
}
