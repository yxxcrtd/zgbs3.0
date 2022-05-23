package com.joyintech.tams.systemtools.listener;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.util.StringUtil;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.file.SafelyFile;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.listener.FileUploadListener;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.systemtools.dao.JoyinFSMapper;
import com.joyintech.tams.systemtools.dfs.JoyinDFSTemplate;
import com.joyintech.tools.util.FileUtil;

/**
 * @description: 文件系统存储监听
 * @author: 王波
 * @date : 2019/9/4 11:22
 */
@Component
@Primary
public class JoyinFSListener implements FileUploadListener, FileAttachmentProcess {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    /**
     * 文件服务模板
     */
    @Autowired
    private JoyinDFSTemplate joyinDFSTemplate;

    /**
     * joyinFSMapper
     */
    @Autowired
    private JoyinFSMapper joyinFSMapper;

    @Override
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UploadFile fetch(String fileNo) {
        // 查询附件
        SysUploadFileInfo file = null;
        file = joyinFSMapper.selectByPrimaryKey(fileNo);
        if (file != null) {
            UploadFile uploadFile = new UploadFile(null, fileNo, new SafelyFile(file.getPath()));
            uploadFile.setFileName(file.getFileName());
            joyinDFSTemplate.fetch(file);
            return uploadFile;
        }
        return null;
    }

    @Override
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void upload(UploadFile fileInfo) {
        SysUploadFileInfo file = new SysUploadFileInfo();
        file.setSeqNo(fileInfo.getFileNo());
        file.setFileName(FileNameUtil.formatFileName(fileInfo.getTargetFile().getName()));
        file.setPath(fileInfo.getTargetFile().getPath());
        // 2.2.0发版的时候要记得打开
//        if (fileInfo.getPageId().equals("UserManual")){
//            file.setBusinessId("UserManual");
//        }
        joyinDFSTemplate.upload(file);
        joyinFSMapper.insert(file);
    }

    @Override
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int remove(String fileNo) {
        UploadFile file = this.fetch(fileNo);
        if (null != file) {
            FileUtil.deleteFile(file.getTargetFile().getPath());
            return joyinFSMapper.deleteByPrimaryKey(fileNo);
        }
        return 0;
    }

    @Override
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int removeByBusinessId(String businessId) {
        return joyinFSMapper.removeByBusinessCode(businessId);
    }

    @Override
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int updateBussinessNo(List<String> fileNoList, String businessCode) {
        int result = 0;
        if (null != fileNoList && fileNoList.size() > 0) {
            SysUploadFileInfo file = new SysUploadFileInfo();
            file.setBusinessId(businessCode);
            for (String fileNo : fileNoList) {
                if (StringUtil.isNotEmpty(fileNo)) {
                    file.setSeqNo(fileNo);
                    result += joyinFSMapper.updateByPrimaryKeySelective(file);
                }
            }
        }
        return result;
    }

    @Override
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<UploadFile> fetchByBusinessCode(String businessCode) {
    	if (StringUtil.isNotEmpty(businessCode)) {
	        List<SysUploadFileInfo> files = joyinFSMapper.getUploadInfo(businessCode);
	        List<UploadFile> uploadFiles = new ArrayList<>(files.size());
	        files.forEach(file -> {
	            UploadFile f = new UploadFile(null, file.getSeqNo(), new SafelyFile(file.getPath()));
	            f.setFileName(file.getFileName());
	            joyinDFSTemplate.fetch(file);
	            uploadFiles.add(f);
	        });
	        return uploadFiles;
    	}
    	else {
    		return new ArrayList<UploadFile>();
    	}
    }

    @Override
    public void uploaded(UploadFile fileInfo) {
        File file = fileInfo.getTargetFile();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("文件{}已上传", file.getPath());
        }
        String os = System.getProperty("os.name");
        if(!os.toLowerCase().startsWith("win")){
            // 设置权限 755
            Set<PosixFilePermission> perms = new HashSet<>();
            // 设置所有者的读取权限
            perms.add(PosixFilePermission.OWNER_READ);
            // 设置所有者的写权限
            perms.add(PosixFilePermission.OWNER_WRITE);
            // 设置所有者的执行权限
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            // 设置组的读取权限
            perms.add(PosixFilePermission.GROUP_READ);
            // 设置组的写权限
            perms.add(PosixFilePermission.OWNER_WRITE);
            // 设置其他的读取权限
            perms.add(PosixFilePermission.OTHERS_READ);
            // 设置其他的写权限
            perms.add(PosixFilePermission.OWNER_WRITE);
            try {
                // 设置文件和文件夹的权限
                Path pathParent = Paths.get(file.getParentFile().getAbsolutePath());
                Path pathDest = Paths.get(file.getAbsolutePath());
                // 修改文件夹路径的权限
                Files.setPosixFilePermissions(pathParent, perms);
                // 修改文件的权限
                Files.setPosixFilePermissions(pathDest, perms);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }


    @Override
    public String beforeUpload(HttpServletRequest request, String fileName) {
        // 定义上传路径
        String prexFileName = FileNameUtil.getBaseName(fileName);
        StringBuilder builder = new StringBuilder(FileNameUtil.createUploadFilePath(prexFileName));
        builder.append(GlobalConstant.UNDER_LINE).append(
                FileNameUtil.createFileName(FileNameUtil.getExtension(fileName)));
        return builder.toString();
    }
}
