package com.joyintech.tams.valuation.common.utils;

import com.jcraft.jsch.SftpException;
import com.joyintech.framework.file.SafelyFile;
import com.joyintech.framework.util.StringUtil;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;

//import com.joyintech.interf.base.ApplicationConfig;


/**
 * Sftp 工具
 *
 * @author 王波
 * @version 1.0 2018年7月25日
 * @see FTPTool
 * @since 1.0
 */
public class FTPTool {

    /**
     * sftp句柄
     */
    private FTPClient ftp;

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(FTPTool.class);

    private int downLoadNums = 0;
    private boolean isLogin = false;
    private final String ENCODE_UTF8 = "UTF-8";
    private final String ENCODE_ISO = "iso-8859-1";

//    public FTPTool(String userName, String password, String host, int port) throws IOException, FTPNotLoginException {
//    	loginByPassword(userName, password, host, port);
//	}

    /**
     * Sftp 登录
     *
     * @param userName      用户名
     * @param password      【密码】或者【秘钥地址】
     * @param host          主机名
     * @param port          端口
     * @param sftpLoginType 【SFTP登录方式：01:密码登录，02:私钥登录；】【系统参数【SFTP_LOGIN_TYPE】维护】
     * @throws IOException,FTPNotLoginException
     */
    public void login(String userName, String password, String host, int port, String sftpLoginType) throws IOException, FTPNotLoginException {

        // 私钥登录ssh服务
        if ("02".equals(sftpLoginType)) {
            this.loginById(userName, password, host, port);
        }
        // 默认密码登录
        else {
            this.loginByPassword(userName, password, host, port);
        }

    }


    /**
     * Sftp 登录(密码登录)
     *
     * @param userName 用户名
     * @param password 密码
     * @param host     主机名
     * @param port     端口
     * @throws IOException,FTPNotLoginException
     */
    public void loginByPassword(String userName, String password, String host, int port)
            throws IOException, FTPNotLoginException {

        ftp = new FTPClient();
        ftp.connect(host, port);
        isLogin = ftp.login(userName, password);
        if (!isLogin) {
            throw new FTPNotLoginException("登录ftp服务失败！");
        }
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftp.setControlEncoding(ENCODE_UTF8);
        ftp.enterLocalPassiveMode();
    }

    /**
     * Sftp 登录(私钥登录ssh服务)
     *
     * @param userName 用户名
     * @param identity 私钥
     * @param host     主机名
     * @param port     端口
     */
    public void loginById(String userName, String identity, String host, int port) {
//        try {
//            // 接口 SFTP 服务配置
//            JSch jsch = new JSch();
//            com.jcraft.jsch.Logger jschLogger = new SettleLogger();
//            JSch.setLogger(jschLogger);
//            jsch.addIdentity(identity);
//            sshSession = jsch.getSession(userName, host, port);
//            Properties sshConfig = new Properties();
//            sshConfig.put("StrictHostKeyChecking", "no");
//            sshSession.setConfig(sshConfig);
//            sshSession.connect();
//            Channel channel = sshSession.openChannel("sftp");
//            channel.connect();
//            sftp = (ChannelSftp) channel;
//        } catch (JSchException e) {
//            logger.error("", e);
//            throw new Exception("登录文件服务器失败");
//        }
    }

    /**
     * 关闭SFTP连接 <一句话功能简述> <功能详细描述>
     */
    public void close() throws IOException {
        if (ftp.isConnected()) {
            // 退出登录状态
            ftp.logout();
            // 销毁连接
            ftp.disconnect();
            // 登录标识设置否
            isLogin = false;
        }
    }

    /**
     * sftp将本地文件上传到远程服务器
     *
     * @param local  本地文件路径
     * @param remote 远程文件路径
     * @throws SftpException
     */
    public void putFile(String local, String remote) throws IOException {

        File dir = new SafelyFile(local);
//		// 上传扫FTP的目录
//		String ftpFileDir = new String(
//				(ftpConfig.getFileDir() + nowStr + "/").getBytes(ENCODE_UTF8),
//				ENCODE_ISO);
        if (!ftp.changeWorkingDirectory(remote)) {
        }

        File[] files = dir.listFiles();
        // 单个文件流上传
        for (File file : files) {
            if (file.isFile()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    String fileName = new String(file.getName().getBytes(
                            ENCODE_UTF8), ENCODE_ISO);
                    String remoteFile = remote + file.getName();
                    boolean flag4 = ftp.storeFile(fileName, fis);
                    if (flag4) {
                        logger.info("上传文件成功：" + remoteFile);
                    } else {
                        logger.error("上传文件失败：" + remoteFile);
                    }
                } finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
            }
        }
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
    public void mkdir(String remote) throws IOException {
        String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/")
                && !ftp.changeWorkingDirectory(directory)) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            while (true) {
                String subDirectory = new String(remote.substring(start, end)
                        .getBytes(ENCODE_UTF8), ENCODE_ISO);
                if (!ftp.changeWorkingDirectory(subDirectory)) {
                    if (ftp.makeDirectory(subDirectory)) {
                        ftp.changeWorkingDirectory(subDirectory);
                    } else {
                    }
                }
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
    }


    /**
     * sftp将本地文件上传到远程服务器(新建个日期目录，如20180911)
     *
     * @param local  本地文件路径
     * @param remote 远程文件路径
     * @throws SftpException
     */
    public void putFileDate(String local, String remote, String date) {
//    	String remote = 
//    			ApplicationConfig.get("toSaleFileDir") + filename;
//    		StringBuffer bfFile = new StringBuffer();
//    		 
//    		// 将文件转换为字符数组
//    		byte[] fileData = convert(data);
//    		
//    		// 设置以二进制方式传输
//    		InputStream localInput = new ByteArrayInputStream(fileData);
//    		//先删除文件
//    		ftp.deleteFile(remote);
//    		//建临时文件，后缀.tmp
//    		boolean flag = ftp.storeFile(remote+".tmp", localInput);
//    		//给文件重命名
//    		ftp.rename(remote+".tmp", remote);
//    		
//    		
//
//    		if(!flag){
////    			throw new FssException("上传文件失败！");
//    		}
//    		localInput.close();
//        logger.info("文件上传成功");
    }

    /**
     * 字符串转换为字节数组
     *
     * @param value 字符串
     * @return 字节数组
     */
    public static byte[] convert(String value) {
        try {
            if (value != null) {
                return value.getBytes("GBK");
            } else {
                return new byte[0];
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    /**
     * 下载服务器上的文件或者文件夹
     *
     * @param ftpFileName 服务器上的文件夹名或者文件名
     * @param localDir    下载到设备上的路径
     */
    public File getFileTransfer(String ftpPath,String ftpFileName, String localDir) throws IOException {
        //本地输出流记得手动关闭，系统调用不会关闭本地流
        FileOutputStream fos = null;
        File localfile = null;
        try {
            File file = new SafelyFile(ftpFileName);
            if (!file.exists()) {
                logger.info("服务器上没有该目录：" + ftpFileName);
            }
            String pwd=ftp.printWorkingDirectory();
            boolean a=ftp.changeWorkingDirectory(ftpPath);

            File temp = new SafelyFile(localDir);
            if (!temp.exists()) {
                temp.mkdirs();
            }
            // 判断是否是目录
            if (isDir(ftpFileName)) {
                String[] names = ftp.listNames();
                for (int i = 0; i < names.length; i++) {
                    if (isDir(names[i])) {
                        getFileTransfer(ftpPath,ftpFileName + '/' + names[i], localDir
                                + '/' + names[i]);
                        downLoadNums++;
                        ftp.changeToParentDirectory();
                    } else {
                        localfile = new SafelyFile(localDir + '/'
                                + names[i]);
                        if (localfile.exists()) {
                            localfile.delete();
                        }

                        fos = new FileOutputStream(localfile);
                        ftp.retrieveFile(names[i], fos);
                        if (fos != null) {
                            fos.close();
                        }
                        downLoadNums++;
                    }
                }
            } else {

                localfile = new SafelyFile(localDir + '/'
                        + file.getName());
                if (localfile.exists()) {
                    localfile.delete();
                }
                fos = new FileOutputStream(localfile);
                ftp.retrieveFile(ftpFileName, fos);
                downLoadNums++;
                ftp.changeToParentDirectory();

            }
        } catch (SocketException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        return localfile;
    }

    // 判断是否是目录
    public boolean isDir(String fileName) {
        try {
            // 切换目录，若当前是目录则返回true,否则返回true。
            boolean falg = ftp.changeWorkingDirectory(fileName);
            return falg;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }


    public int getDownLoadNums() {
        return downLoadNums;
    }

    /**
     * @description: 未登录FTP服务器异常
     */
    public class FTPNotLoginException extends Exception {

        private static final long serialVersionUID = 1L;

        public FTPNotLoginException(String msg) {
            super(msg);
        }
    }


    /**
     * 远程备份移动文件
     */
    private void rsBackup(String backupPath, String oldFileName, String backupPathName) throws IOException {
        //当前路径
        String currPath = ftp.printWorkingDirectory();
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        if (!ftp.changeWorkingDirectory(backupPath)) {
            createDir(backupPath);
        }
        ftp.changeWorkingDirectory(currPath);
        logger.info("当前文件路径: " + ftp.printWorkingDirectory());
        logger.info("开始备份文件: " + oldFileName + "到" + backupPath);

        try {
            ftp.rename(oldFileName, backupPathName);
            logger.info("文件备份成功: " + backupPathName);
        } catch (Exception e) {
            logger.info("文件备份失败！");
            return;
        }
    }

    private void createDir(String directory) throws IOException {
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/")
                && !ftp.changeWorkingDirectory(directory)) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            if (!directory.endsWith("/")) {
                directory = directory + "/";
            }

            end = directory.indexOf("/", start);
            while (true) {
                String subDirectory = new String(directory.substring(start, end)
                        .getBytes(ENCODE_UTF8), ENCODE_ISO);
                String currPath = directory.substring(0, end);
                if (!ftp.changeWorkingDirectory(currPath)) {
                    if (ftp.makeDirectory(subDirectory)) {
                        ftp.changeWorkingDirectory(currPath);
                    }
                }
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
    }





}

