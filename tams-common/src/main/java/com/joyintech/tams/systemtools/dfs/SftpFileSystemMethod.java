package com.joyintech.tams.systemtools.dfs;

import com.jcraft.jsch.*;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.file.SafelyFile;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.Properties;

/**
 * @description: 使用Sftp方式存储文件
 * @author: 王波
 * @date : 2019/9/4 9:44
 */
@Component
public class SftpFileSystemMethod implements JoyinFileSystemMethod {

    private static Logger logger = LoggerFactory.getLogger(SftpFileSystemMethod.class);

    private final Integer LOGIN_TYPE_PASSWORD = 1;

    private final Integer LOGIN_TYPE_NOPASSWORD = 2;


    @Override
    public void upload(SysUploadFileInfo uploadFile) {
        String remotePathPrefix = CacheUtil.getSysParam("SYS_FS_REMOTE_PATH_PREFIX");
        String date = DateUtil.format(new Date(), DateUtil.DEFAULT_ANOTHER_FORMAT);
        String remote = remotePathPrefix + "/" + date;
        SftpDto dto = null;
        try {
            dto = login();
            putFile(dto, uploadFile.getPath(), remote);
            close(dto);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @Override
    public UploadFile fecth(SysUploadFileInfo file) {
        String remotePathPrefix = CacheUtil.getSysParam("SYS_FS_REMOTE_PATH_PREFIX");
        File f = new SafelyFile(file.getPath());
        if (f.exists()) {
            // 本地文件存在不去下载
            UploadFile uploadFile = new UploadFile(null, file.getSeqNo(), f);
            uploadFile.setFileName(file.getFileName());
            return uploadFile;
        }
        String path = file.getPath();
        int begin = Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\"));
        String fileName = path.substring(begin + 1, path.length());
        String date = DateUtil.format(file.getCreateTime(), DateUtil.DEFAULT_ANOTHER_FORMAT);
        String remote = remotePathPrefix + "/" + date + "/" + fileName;
        SftpDto dto = null;
        try {
            dto = login();
            getFile(dto, remote, path.substring(0, begin));
            close(dto);
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * 下载文件
     *
     * @param remoteFile
     * @param localDir
     * @throws Exception
     * @see
     * @since 1.0
     */
    private void getFile(SftpDto dto, String remoteFile, String localDir) throws SftpException {
        File localFile = new SafelyFile(localDir);
        if (localFile.exists()) {
            // 本地目录不是目录，先删除
            if (!localFile.isDirectory()) {
                boolean rec =localFile.delete();
                if(rec){
                    logger.debug("");
                }
            }
        } else {
            // 创建本地目录
            localFile.mkdirs();
        }
        try {
            dto.getSftp().get(remoteFile, localDir);
        } catch (SftpException e) {
            logger.error("连接失败！", e);
            throw new SftpException(1,"下载文件失败");
        }
    }

    /**
     * 日志工具类
     */
    public class SettleLogger implements com.jcraft.jsch.Logger {
        public boolean isEnabled(int level) {
            return true;
        }

        public void log(int level, String msg) {
            logger.info(msg);
        }
    }


    private SftpDto login() throws JSchException {
        String ip = CacheUtil.getSysParam("SYS_FS_SFTP_IP");
        String port = CacheUtil.getSysParam("SYS_FS_SFTP_PORT");
        Integer logintype = Integer.valueOf(CacheUtil.getSysParam("SYS_FS_SFTP_LOGINTYPE"));
        String password = CacheUtil.getSysParam("SYS_FS_SFTP_PASSWORD");
        String username = CacheUtil.getSysParam("SYS_FS_SFTP_USERNAME");
        SftpDto res = new SftpDto();
        Session sshSession = null;
        try {
            // 接口 SFTP 服务配置
            JSch jsch = new JSch();
            sshSession = jsch.getSession(username, ip, Integer.valueOf(port));
            com.jcraft.jsch.Logger jschLogger = new SettleLogger();
            JSch.setLogger(jschLogger);
            if (LOGIN_TYPE_NOPASSWORD.equals(logintype)) {
                jsch.addIdentity(password);
            } else {
                sshSession.setPassword(password);
            }
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            res.setSshSession(sshSession);
            ChannelSftp sftp = (ChannelSftp) channel;
            res.setSftp(sftp);
        } catch (JSchException e) {
            logger.error("", e);
            throw new JSchException("登录文件服务器失败");
        }
        return res;
    }

    /**
     * 关闭SFTP连接
     */
    private void close(SftpDto dto) {
        if (dto.getSftp().isConnected()) {
            dto.getSftp().disconnect();
            logger.info("成功关闭连接SFTP！");
        }
        if (dto.getSshSession() != null) {
            dto.getSshSession().disconnect();
            dto.setSshSession(null);
        }
    }

    /**
     * sftp将本地文件上传到远程服务器
     *
     * @param local 本地文件路径
     * @param remote 远程文件路径
     * @throws SftpException
     */
    public void putFile(SftpDto dto, String local, String remote) throws SftpException {
        try {
            dto.getSftp().ls(remote);
        }
        catch (SftpException e) {
            logger.info("服务器上没有该目录：" + remote);
            mkdir(dto, remote);
        }
        dto.getSftp().put(local, remote);
        logger.info("文件上传成功");
    }

    /**
     * 支持在SFTP 服务器上创建多级目录
     *
     * @param remote
     * @throws SftpException
     * @see
     * @since 1.0
     */
    private void mkdir(SftpDto dto, String remote) throws SftpException {
        String curdir = dto.getSftp().pwd();
        if (remote == null) {
            logger.error("路径不能为空！");
            return;
        }
        String[] remotes = remote.split("/");
        if (remote.startsWith("/")) {
            // 绝对路径从/下开始递归创建
            dto.getSftp().cd("/");
        }
        for (String dir : remotes) {
            if (StringUtil.isEmpty(dir)) {
                continue;
            }
            try {
                dto.getSftp().ls(dir);
            }
            catch (SftpException e) {
                logger.error("服务器上没有该目录：" + dir);
                dto.getSftp().mkdir(dir);
            }
            dto.getSftp().cd(dir);
        }
        dto.getSftp().cd(curdir);
    }

    private class SftpDto {
        private ChannelSftp sftp;
        private Session sshSession;

        public ChannelSftp getSftp() {
            return sftp;
        }

        public void setSftp(ChannelSftp sftp) {
            this.sftp = sftp;
        }

        public Session getSshSession() {
            return sshSession;
        }

        public void setSshSession(Session sshSession) {
            this.sshSession = sshSession;
        }
    }
}
