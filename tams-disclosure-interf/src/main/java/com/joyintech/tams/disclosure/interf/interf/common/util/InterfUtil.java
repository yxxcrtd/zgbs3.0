package com.joyintech.tams.disclosure.interf.interf.common.util;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.joyintech.framework.file.SafelyFile;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.disclosure.interf.framework.util.FtpUtil;
import com.joyintech.tams.systemtools.util.CommonUtil;

public class InterfUtil {
	private static Logger logger = LoggerFactory.getLogger(InterfUtil.class);
    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断list是否为空
     *
     * @param list
     * @return
     */
    public static boolean isEmptyList(List<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * 解析XML格式的报文，取出相应的节点所对应的值
     *
     * @param xml:XMl格式报文
     * @param nodename:节点名称
     * @return String:节点名称对应的值
     */
    public static String getnode(String xml, String nodename) {
        String val = "";
        if (xml.contains("<" + nodename + ">")) {
            val = xml.substring(
                xml.indexOf("<" + nodename + ">") + ("<" + nodename + ">").length(),
                xml.indexOf("</" + nodename + ">"));
        }
        if (val.equals("")) {
            val = null;
        }
        return val;
    }

    /**
     * String → BigDecimal
     *
     * @param xml
     * @return
     */
    public static BigDecimal str2BigDecimal(String str) {
        if (isEmpty(str)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(str);
    }

    /**
     * 根据格式化字符串格式化日期
     *
     * @param date 格式化前日期
     * @param pattern 格式化字符串,如：yyyy-MM-dd
     * @return 格式化后日期字符串
     */
    public static String format(Date date, String pattern) {
        String s = "";
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            s = sdf.format(date);
        }
        return s;
    }

    /**
     * 根据格式化字符串格式化日期
     *
     * @param date 格式化前日期
     * @return 格式化后日期字符串（yyyy-MM-dd）
     */
    public static String formatDateWithQuotation(Date date) {
        String str = format(date, DateUtil.DEFAULT_DATE_FORMAT_EN);
        if (!str.isEmpty()) {
            str = "\"" + str + "\"";
        }
        return str;
    }

    /**
     * 根据格式化字符串格式化日期
     *
     * @param date 格式化前日期
     * @return 格式化后日期字符串（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTimeWithQuotation(Date date) {
        String str = format(date, DateUtil.DEFAULT_DATETIME_FORMAT);
        if (!str.isEmpty()) {
            str = "\"" + str + "\"";
        }
        return str;
    }

    /**
     * 输出对象的字符串表示
     *
     * @param value
     * @return
     */
    public static String getTxtDataString(Object value) {
        return GlobalConstant.VERTICAL_LINE + CommonUtil.getNotNullString(value);
    }

    /**
     * 输出对象的字符串表示
     *
     * @param value
     * @return
     */
    public static String getTxtDataString(Object value, String defaultValue) {
        return GlobalConstant.VERTICAL_LINE + StringUtil.getOrElse(value, defaultValue);
    }

    /**
     * Description: 从FTP服务器下载文件
     *
     * @param ftpType ftp类型（01：FTP，02：SFTP，03：本地文件）
     * @param url FTP服务器hostname
     * @param port FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param localPath 本地路径
     * @param containsFile 下载文件名包含指定字符串的文件
     * @return 下载后的本地文件列表
     * @throws Exception 异常
     */
    public static List<File> downInterfFile(String ftpType, String url, int port, String username,
                                            String password, String remotePath, String localPath,
                                            List<String> containsFile) {
        final List<File> localFiles = new ArrayList<>();
        try {
            // 本地文件系统方式
            if (StringUtil.equals(ftpType, "03")) {
                File remoteDir = new SafelyFile(remotePath);
                if (remoteDir.exists() && remoteDir.isDirectory()) {
                    Collection<File> files = FileUtils.listFiles(remoteDir,
                        new String[] {"xml", "txt"}, false);
                    for (File ff : files) {
                        String fileName = ff.getName();
                        if (containsFile == null || CollectionUtils.exists(containsFile, name -> {
                            return fileName.contains((String)name);
                        })) {
                            File lf = new SafelyFile(localPath + File.separatorChar + fileName);
                            if (!lf.exists()) {
                                lf.createNewFile();
                                FileUtils.copyFile(ff, lf);
                            }
                            localFiles.add(lf);
                        }
                    }
                }
            }
            // SFTP方式
            else if (StringUtil.equals(ftpType, "02")) {
                SftpUtil.SFTPconnect(username, url, port, password);
                Vector<?> vector = SftpUtil.getSFtpFile(remotePath);
                if (null != vector) {
                    for (Object item : vector) {
                        LsEntry entry = (LsEntry)item;
                        String fileName = entry.getFilename();
                        if (containsFile == null || CollectionUtils.exists(containsFile, name -> {
                            return fileName.contains((String)name);
                        })) {
                            File lf = new SafelyFile(localPath + File.separatorChar + fileName);
                            if (!lf.exists()) {
                                SftpUtil.downloadSftp(remotePath, entry.getFilename(), localPath);
                            }
                            localFiles.add(lf);
                        }
                    }
                }
            }
            // FTP方式
            else if (StringUtil.equals(ftpType, "01")) {
                localFiles.addAll(FtpUtil.downXMLFile(url, port, username, password, remotePath,
                    localPath, containsFile));
            }
        }
        catch (Exception e) {
        	logger.error("error:",e);
        }
        finally {
            SftpUtil.SFTPDisConnect();
        }
        return localFiles;
    }

    /**
     * Description: 从FTP服务器下载文件
     *
     * @param ftpType ftp类型（01：FTP，02：SFTP，03：本地文件）
     * @param url FTP服务器hostname
     * @param port FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param localPath 本地目录
     * @return ok文件名列表
     * @throws Exception 异常
     */
    public static List<String> downInterfOKFile(String ftpType, String url, int port, String username,
                                            String password, String remotePath, String localPath, String reportInterfOkFileExtension) {
        final List<String> okFileNameList = new ArrayList<>();
        String okFileExtension = "." + reportInterfOkFileExtension;
        try {
            // 本地文件系统方式
            if (StringUtil.equals(ftpType, "03")) {
                File remoteDir = new SafelyFile(remotePath);
                if (remoteDir.exists() && remoteDir.isDirectory()) {
                    Collection<File> files = FileUtils.listFiles(remoteDir,
                        new String[] {reportInterfOkFileExtension}, false);
                    for (File ff : files) {
                        okFileNameList.add(
                            StringUtil.splitToList(GlobalConstant.POINT, ff.getName()).get(0));
                    }
                }
            }
            // SFTP方式
            else if (StringUtil.equals(ftpType, "02")) {
                SftpUtil.SFTPconnect(username, url, port, password);
                Vector<?> vector = SftpUtil.getSFtpFile(remotePath);
                if (null != vector) {
                    for (Object item : vector) {
                        LsEntry entry = (LsEntry)item;
                        String fileName = entry.getFilename();
                        if (StringUtil.endsWith(fileName, okFileExtension)) {
                            okFileNameList.add(
                                StringUtil.splitToList(GlobalConstant.POINT, fileName).get(0));
                        }
                    }
                }
            }
            // FTP方式
            else if (StringUtil.equals(ftpType, "01")) {
                List<String> containsFile = new ArrayList<String>();
                containsFile.add(okFileExtension);
                List<File> okFileList = FtpUtil.downXMLFile(url, port, username, password, remotePath,
                    localPath, containsFile);

                for (File file : okFileList) {
                    okFileNameList.add(
                        StringUtil.splitToList(GlobalConstant.POINT, file.getName()).get(0));
                }
            }
        }
        catch (Exception e) {
        	logger.error("error:",e);
        }
        finally {
            SftpUtil.SFTPDisConnect();
        }

        return okFileNameList;
    }

    /**
     * 移动文件
     *
     * @param ftpType ftp类型（01：FTP，02：SFTP，03：本地文件）
     * @param url FTP服务器hostname
     * @param port FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param fileName 需要被移动的文件路径
     * @param newRemotePath 新路径
     * @return 移动结果
     * @throws Exception 异常
     */
    public static boolean moveFiles(String ftpType, String url, int port, String username,
                                    String password, String remotePath, String fileName,
                                    String newRemotePath) {
        try {
            // 本地文件系统方式
            if (StringUtil.equals(ftpType, "03")) {
                File remoteDir = new SafelyFile(remotePath);
                if (remoteDir.exists() && remoteDir.isDirectory()) {
                    Collection<File> files = FileUtils.listFiles(remoteDir,
                        new String[] {"xml", "txt"}, false);
                    for (File ff : files) {
                        String name = ff.getName();
                        if (StringUtil.equals(name, fileName)) {
                            FileUtils.moveFileToDirectory(ff,
                                new SafelyFile(remotePath + File.separatorChar + newRemotePath), true);
                        }
                    }
                }
            }
            // SFTP方式
            else if (StringUtil.equals(ftpType, "02")) {
                SftpUtil.SFTPconnect(username, url, port, password);
                Vector<?> vector = SftpUtil.getSFtpFile(remotePath);
                for (Object item : vector) {
                    LsEntry entry = (LsEntry)item;
                    String name = entry.getFilename();
                    if (StringUtil.equals(name, fileName)) {
                        SftpUtil.rename(remotePath + File.separatorChar + name,
                            remotePath + File.separatorChar + newRemotePath, name);
                    }
                }
            }
            // FTP方式
            else if (StringUtil.equals(ftpType, "01")) {
                FtpUtil.moveFiles(url, port, username, password, remotePath, fileName,
                    newRemotePath);
            }
            return true;
        }
        catch (Exception e) {
        	logger.error("error:",e);
        }
        finally {
            SftpUtil.SFTPDisConnect();
        }
        return false;
    }

    /**
     * 删除本地临时文件
     *
     * @param localPath 本地目录
     * @see
     * @since 1.0
     */
    public static void deleteLocalTmpFile(String localPath) {
        try {
            File localDir = new SafelyFile(localPath);
            if (localDir.exists() && localDir.isDirectory()) {
                Collection<File> files = FileUtils.listFiles(localDir,
                    new String[] {"xml", "txt", "tmp"}, false);

                for (File file : files) {
                    try {
                        if (file.exists() && file.isFile()) {
                            file.delete();
                        }
                    }
                    catch (Exception e) {
                    }
                }
            }
        }
        catch (Exception e) {
        }
    }
}
