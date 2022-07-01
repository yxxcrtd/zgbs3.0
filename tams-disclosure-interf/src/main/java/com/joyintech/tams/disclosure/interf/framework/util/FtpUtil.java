package com.joyintech.tams.disclosure.interf.framework.util;

import com.joyintech.framework.file.SafelyFile;
import com.joyintech.framework.util.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;
import java.util.*;

/**
 * ftp工具类 apache FTP 处理
 *
 * @author 何阳阳
 * @version 1.0 2017年11月20日
 * @see FtpUtil
 * @since 1.0
 */
public class FtpUtil {
	private static Logger logger = LoggerFactory.getLogger(FtpUtil.class);
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpUtil.class);

    /**
     * ftp客户端
     */
    private static FTPClient FTP_CLIENT = new FTPClient();

    /**
     * 编码
     */
    private static String ENCODING = System.getProperty("file.encoding")==null?"":System.getProperty("file.encoding");

    /**
     * 路径分隔符
     */
    private static String SEPARATOR = "/";


    public static int getFtpConnect(String url, int port, String username, String password) throws SocketException, IOException {
        int flag = ftpConnect(url, port, username, password);
        return flag;
    }

    public static FTPClient getFTPClient() {
        return FTP_CLIENT;
    }

    public static void setFTPClient(FTPClient ftpClient) {
        FTP_CLIENT = ftpClient;
    }

    public static boolean changePath(String path) throws IOException {
        boolean change = FTP_CLIENT.changeWorkingDirectory(path);
        FTP_CLIENT.setFileType(FTP.BINARY_FILE_TYPE);
        if (!change) {
            FTP_CLIENT.makeDirectory(path);
        }
        change = FTP_CLIENT.changeWorkingDirectory(path);
        return change;
    }

    public static boolean changePath(FTPClient ftpClient, String path) throws IOException {
        boolean change = ftpClient.changeWorkingDirectory(path);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        if (!change) {
            ftpClient.makeDirectory(path);
        }
        change = ftpClient.changeWorkingDirectory(path);
        return change;
    }

    public static boolean uploadFile(String filename, InputStream input) throws IOException {
        boolean result = false;
        try {
            FTP_CLIENT.enterLocalPassiveMode();
            result = FTP_CLIENT.storeFile(
                    new String(filename.getBytes(ENCODING), "iso-8859-1"), input);
            if (result) {
                LOGGER.info("上传成功");
            }
        } catch (Exception ex) {
        	logger.error(ex.getMessage(),ex);
        } finally {
            input.close();
        }
        return result;
    }

    public static boolean uploadFile(FTPClient ftpClient, String filename, InputStream input) throws IOException {
        boolean result = false;
        try {
            ftpClient.enterLocalPassiveMode();
            result = ftpClient.storeFile(
                    new String(filename.getBytes(ENCODING), "iso-8859-1"), input);
            if (result) {
                LOGGER.info("上传成功");
            }
        } catch (Exception ex) {
        	logger.error(ex.getMessage(),ex);
        } finally {
            input.close();
        }
        return result;
    }

    public static void ftpCloseForBatch() {
        ftpClose();
    }

    /**
     * Description: 向FTP服务器上传文件
     *
     * @param url      FTP服务器hostname
     * @param port     FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param path     FTP服务器保存目录,如果是根目录则为“/”
     * @param filename 上传到FTP服务器上的文件名
     * @param input    本地文件输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(String url, int port, String username, String password,
                                     String path, String filename, InputStream input) {
        boolean result = false;
        try {
            ftpConnect(url, port, username, password);
            // 转移工作目录至指定目录下
            boolean change = FTP_CLIENT.changeWorkingDirectory(path);
            FTP_CLIENT.setFileType(FTP.BINARY_FILE_TYPE);
            if (!change) {
                FTP_CLIENT.makeDirectory(path);
            }
            change = FTP_CLIENT.changeWorkingDirectory(path);
            if (change) {
                // 每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据，防止某些端口未开启，出现阻塞
                FTP_CLIENT.enterLocalPassiveMode();
                result = FTP_CLIENT.storeFile(
                        new String(filename.getBytes(ENCODING), "iso-8859-1"), input);
                if (result) {
//                    System.out.println("上传成功!");
                    logger.info("上传成功!");
                }
            }
            input.close();
            FTP_CLIENT.logout();
        } catch (IOException e) {
        	logger.error("发送失败:", e);
            result = false;
        } finally {
            ftpClose();
        }
        return result;
    }

    /**
     * Description: 向FTP服务器上传文件
     *
     * @param url      FTP服务器hostname
     * @param port     FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param path     FTP服务器保存目录,如果是根目录则为“/”
     * @param filename 上传到FTP服务器上的文件名
     * @param input    本地文件输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(FTPClient ftpClient,
                                     String path, String filename, InputStream input) {
        boolean result = false;
        try {
            // 转移工作目录至指定目录下
            boolean change = ftpClient.changeWorkingDirectory(path);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            if (!change) {
                ftpClient.makeDirectory(path);
            }
            change = ftpClient.changeWorkingDirectory(path);
            if (change) {
                // 每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据，防止某些端口未开启，出现阻塞
                ftpClient.enterLocalPassiveMode();
                result = ftpClient.storeFile(
                        new String(filename.getBytes(ENCODING), "iso-8859-1"), input);
                if (result) {
//                    System.out.println("上传成功!");
                    logger.info("上传成功!");
                }
            }
            input.close();
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * Description: 从FTP服务器下载文件
     *
     * @param url        FTP服务器hostname
     * @param port       FTP服务器端口
     * @param username   FTP登录账号
     * @param password   FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param fileName   要下载的文件名
     * @param localPath  下载后保存到本地的路径
     * @return 处理标识
     */
    public static boolean downFile(String url, int port, String username, String password,
                                   String remotePath, String fileName, String localPath) {
        boolean result = false;
        try {
            ftpConnect(url, port, username, password);
            // 转移到FTP服务器目录至指定的目录下
            FTP_CLIENT.changeWorkingDirectory(
                    new String(remotePath.getBytes(ENCODING), "iso-8859-1"));
            // 每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据，防止某些端口未开启，出现阻塞
            FTP_CLIENT.enterLocalPassiveMode();
            // 获取文件列表
            FTPFile[] fs = FTP_CLIENT.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    File localFile = new SafelyFile(localPath + SEPARATOR + ff.getName());
                    OutputStream is = null;
                    try {
                        is = new FileOutputStream(localFile);
                        FTP_CLIENT.retrieveFile(ff.getName(), is);
                    }
                    catch (Exception e)
                    {
                        logger.error(e.getMessage(),e);
                    }
                    finally {
                        if(is!=null)
                        {
                            is.close();
                        }

                    }
                }
            }

            FTP_CLIENT.logout();
            result = true;
        } catch (IOException e) {
            result = false;
        } finally {
            ftpClose();
        }
        return result;
    }

    /**
     * Description: 从FTP服务器删除文件
     *
     * @param url        FTP服务器hostname
     * @param port       FTP服务器端口
     * @param username   FTP登录账号
     * @param password   FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param fileName   要下载的文件名
     * @return 处理标识
     */
    public static boolean deleteFile(String url, int port, String username, String password,
                                     String remotePath, String fileName) {
        boolean result = false;
        try {
            ftpConnect(url, port, username, password);
            // 转移到FTP服务器目录至指定的目录下
            FTP_CLIENT.changeWorkingDirectory(
                    new String(remotePath.getBytes(ENCODING), "iso-8859-1"));
            // 获取文件列表
            FTP_CLIENT.deleteFile(fileName);

            FTP_CLIENT.logout();
            result = true;
        } catch (IOException e) {
            result = false;
        } finally {
            ftpClose();
        }
        return result;
    }

    /**
     * 验证ftp登录和文件存在
     *
     * @param url        FTP URL
     * @param port       端口
     * @param username   用户名
     * @param password   密码
     * @param remotePath 服务器地址
     * @param fileName   文件名
     * @return 处理标识
     */
    public static boolean checkLoginAndFile(String url, int port, String username, String password,
                                            String remotePath, String fileName) {
        boolean result = false;
        try {
            ftpConnect(url, port, username, password);
            // 转移到FTP服务器目录至指定的目录下
            FTP_CLIENT.changeWorkingDirectory(
                    new String(remotePath.getBytes(ENCODING), "iso-8859-1"));
            // 每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据，防止某些端口未开启，出现阻塞
            FTP_CLIENT.enterLocalPassiveMode();
            // 获取文件列表
            boolean vfileFlag = false;
            FTPFile[] fs = FTP_CLIENT.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    vfileFlag = true;
                    break;
                }
            }

            FTP_CLIENT.logout();
            if (!vfileFlag) {
                result = false;
            } else {
                result = true;
            }
        } catch (IOException e) {
            result = false;
        } finally {
            ftpClose();
        }
        return result;
    }

    /**
     * Description: 从FTP服务器拷贝文件并重命名
     *
     * @param url        FTP服务器hostname
     * @param port       FTP服务器端口
     * @param username   FTP登录账号
     * @param password   FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param oaFilePath OA文件路径
     * @param fileName   本地UUID文件名称
     * @return 文件复制后的信息
     */
    public static Map<String, String> copyFile(String url, int port, String username,
                                               String password, String remotePath,
                                               String oaFilePath, String fileName) {
        String result = "false";
        String fileSize = "0";
        Map<String, String> rslt = new HashMap<String, String>();
        rslt.put("flag", result);
        try {
            ftpConnect(url, port, username, password);
            // 转移到FTP服务器目录至指定的目录下
            FTP_CLIENT.changeWorkingDirectory(
                    new String(remotePath.getBytes(ENCODING), "iso-8859-1"));
            // 每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据，防止某些端口未开启，出现阻塞
            FTP_CLIENT.enterLocalPassiveMode();
            // 获取文件列表
            FTPFile[] fs = FTP_CLIENT.listFiles(oaFilePath);
            InputStream inputStream = null;
            for (FTPFile ff : fs) {
                if (ff != null && ff.isFile()) {
                    fileSize = ff.getSize() + "";
                    inputStream = FTP_CLIENT.retrieveFileStream(oaFilePath);
                    // 复制文件时掉用了retrieveFileStream方法
                    // 调用完之后必须调用completePendingCommand释放,否则FTP会断开连接
                    if (!FTP_CLIENT.completePendingCommand()) {
                        return rslt;
                    }
                    // 如果读取的文件流不为空则复制文件
                    if (inputStream != null) {
                        boolean sResult = FTP_CLIENT.storeFile(remotePath + SEPARATOR + fileName,
                                inputStream);
                        // 关闭文件流
                        inputStream.close();
                        if (!sResult) {
                            return rslt;
                        }
                    }
                    break;
                }
            }

            FTP_CLIENT.logout();
            result = "true";
        } catch (IOException e) {
        	logger.error("error:",e);
            result = "false";
        } finally {
            ftpClose();
            rslt.put("flag", result);
            rslt.put("fileSize", fileSize);
        }
        return rslt;
    }

    /**
     * Description: 从FTP服务器拷贝文件并重命名
     *
     * @param url          FTP服务器IP
     * @param port         FTP服务器端口
     * @param username     FTP登录账号
     * @param password     FTP登录密码
     * @param remotePath   FTP服务器上的相对路径
     * @param ctlFileName  控制文件名称
     * @param dataFileName 数据文件名称
     * @param localPath    本地文件路径
     * @return 文件命令拷贝返回信息
     */
    public static Map<String, String> downFileByCtlFile(String url, int port, String username,
                                                        String password, String remotePath,
                                                        String ctlFileName, String dataFileName,
                                                        String localPath) {

        Map<String, String> rslt = new HashMap<String, String>();
        boolean flag = false;
        String msg = null;
        rslt.put("flag", String.valueOf(flag));
        try {
            int reply;
            FTP_CLIENT.setControlEncoding(ENCODING);

            FTP_CLIENT.connect(url, port);
            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            FTP_CLIENT.login(username, password);// 登录
            // 设置文件传输类型为二进制
            FTP_CLIENT.setFileType(FTPClient.BINARY_FILE_TYPE);
            // 获取ftp登录应答代码
            reply = FTP_CLIENT.getReplyCode();
            // 验证是否登陆成功
            if (!FTPReply.isPositiveCompletion(reply)) {
                msg = "FTP服务器拒绝连接";
            } else {
                // 转移到FTP服务器目录至指定的目录下
                boolean changeFlag = FTP_CLIENT.changeWorkingDirectory(
                        new String(remotePath.getBytes(ENCODING), "iso-8859-1"));
                if (!changeFlag) {
                    msg = "数据文件目录不存在";
                } else {
                    // 每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据，防止某些端口未开启，出现阻塞
                    FTP_CLIENT.enterLocalPassiveMode();
                    // 判断控制文件是否存在
                    String ctlFilePath = remotePath + SEPARATOR + ctlFileName;
                    FTPFile[] ctlFile = FTP_CLIENT.listFiles(ctlFilePath);
                    boolean ctlFileExistFlag = false;
                    for (FTPFile ctl : ctlFile) {
                        if (ctl != null && ctl.isFile()) {
                            ctlFileExistFlag = true;
                            break;
                        }
                    }
                    if (!ctlFileExistFlag) {
                        msg = "控制文件不存在";
                    } else {
                        // 数据文件下载
                        // 获取文件列表
                        FTPFile[] fs = FTP_CLIENT.listFiles();
                        for (FTPFile ff : fs) {
                            if (ff.getName().equals(dataFileName)) {
                                File localFile = new SafelyFile(localPath + SEPARATOR + ff.getName());
                                OutputStream is = null;
                                try {
                                    is = new FileOutputStream(localFile);
                                    FTP_CLIENT.retrieveFile(ff.getName(), is);
                                }catch (Exception e)
                                {
                                    logger.error(e.getMessage(),e);
                                }
                                finally {
                                    if(is!=null)
                                    {
                                        is.close();
                                    }

                                }
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            msg = "数据文件不存在";
                        } else {
                            msg = "下载成功";
                        }
                    }
                }
                FTP_CLIENT.logout();
            }
        } catch (IOException e) {
            msg = "FTP服务器文件下载出现异常";
        } finally {
            if (FTP_CLIENT.isConnected()) {
                try {
                    FTP_CLIENT.disconnect();
                } catch (IOException ioe) {
                    msg = "服务器关闭出现异常";
                }
            }
            rslt.put("flag", String.valueOf(flag));
            rslt.put("message", msg);
        }
        return rslt;
    }

    /**
     * Description: 从FTP服务器下载文件
     *
     * @param ftpType      ftp类型（01：FTP，02：SFTP，03：本地文件）
     * @param url          FTP服务器hostname
     * @param port         FTP服务器端口
     * @param username     FTP登录账号
     * @param password     FTP登录密码
     * @param remotePath   FTP服务器上的相对路径
     * @param localPath    本地路径
     * @param containsFile 下载文件名包含指定字符串的文件
     * @return 下载后的本地文件列表
     */
    public static List<File> downXMLFile(String url, int port, String username, String password,
                                         String remotePath, String localPath,
                                         List<String> containsFile) {
        List<File> localFiles = new ArrayList<>();
        try {
            ftpConnect(url, port, username, password);
            // 转移到FTP服务器目录至指定的目录下
            FTP_CLIENT.changeWorkingDirectory(
                    new String(remotePath.getBytes(ENCODING), "iso-8859-1"));
            // 每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据，防止某些端口未开启，出现阻塞
            FTP_CLIENT.enterLocalPassiveMode();
            // 获取文件列表
            FTPFile[] fs = FTP_CLIENT.listFiles();
            for (FTPFile ff : fs) {
                if (!ff.isDirectory() && CollectionUtils.exists(containsFile, name -> {
                    return ff.getName().contains((String) name);
                })) {
                    File localFile = new SafelyFile(localPath + SEPARATOR + ff.getName());
                    if (!localFile.exists()) {
                        localFile.createNewFile();

                        OutputStream out = null;
                        try {
                             out = new FileOutputStream(localFile);
                            FTP_CLIENT.retrieveFile(ff.getName(), out);
                        }catch (Exception e)
                        {
                            logger.error(e.getMessage(),e);
                        }finally {
                            if(out!=null)
                            {
                                out.close();
                            }

                        }

                    }
                    localFiles.add(localFile);
                }
            }
            FTP_CLIENT.logout();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } finally {
            ftpClose();
        }
        return localFiles;
    }

    /**
     * Description: 从FTP服务器下载文件
     *
     * @param url        FTP服务器hostname
     * @param port       FTP服务器端口
     * @param username   FTP登录账号
     * @param password   FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param fileName   要下载的文件名
     * @return 解析后获取需要的格式
     */
    public static InputStream updateFileName(String url, int port, String username,
                                             String password, String remotePath, String fileName) {
        InputStream result = null;
        try {
            ftpConnect(url, port, username, password);
            // 转移到FTP服务器目录至指定的目录下
            remotePath = remotePath + DateUtil.getCurrentDate();
            FTP_CLIENT.changeWorkingDirectory(
                    new String(remotePath.getBytes(ENCODING), "iso-8859-1"));
            // 每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据，防止某些端口未开启，出现阻塞
            FTP_CLIENT.enterLocalPassiveMode();
            // 获取文件列表
            FTPFile[] fs = FTP_CLIENT.listFiles();
            // 按规则生成修改后的文件名
            int i = 0;
            for (FTPFile ff : fs) {
                if (ff.getName().contains(fileName)) {
                    i = i + 1;
                }
            }
            String newFlieName = fileName + "_" + String.valueOf(i) + ".xml";

            fileName = fileName + ".xml";
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    boolean flag = FTP_CLIENT.rename(fileName, newFlieName);
//                    System.out.println(flag);
                    logger.info(String.valueOf(flag));
                }
            }
            FTP_CLIENT.logout();
        } catch (IOException e) {
        } finally {
            if (FTP_CLIENT.isConnected()) {
                try {
                    FTP_CLIENT.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return result;
    }

    /**
     * Description: FTP移动文件到指定目录
     *
     * @param url           FTP服务器hostname
     * @param port          FTP服务器端口
     * @param username      FTP登录账号
     * @param password      FTP登录密码
     * @param remotePath    FTP服务器上的相对路径
     * @param fileName      需要被移动的文件名
     * @param newRemotePath 移入目录的路径
     */
    public static void moveFiles(String url, int port, String username, String password,
                                 String remotePath, String fileName, String newRemotePath) {
        try {
            // 连接ftp
            ftpConnect(url, port, username, password);
            // 文件扩展名
            String ext = "." + FilenameUtils.getExtension(fileName);
            // 不包含扩展名的文件名
            fileName = FilenameUtils.getBaseName(fileName);

            // 转移到FTP服务器目录至指定的目录下
            if (!newRemotePath.startsWith(SEPARATOR)) {
                newRemotePath = remotePath + SEPARATOR + newRemotePath;
            }
            // 创建目录
            createMultiDirectory(newRemotePath);
            FTP_CLIENT.changeWorkingDirectory(
                    new String(remotePath.getBytes(ENCODING), "iso-8859-1"));
            // 每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据，防止某些端口未开启，出现阻塞
            FTP_CLIENT.enterLocalPassiveMode();
            // 获取文件列表
            FTPFile[] fs = FTP_CLIENT.listFiles();
            // 按规则生成修改后的文件名
            int i = 0;
            String tmpFileName = DateUtil.format(new Date(),
                    DateUtil.DATETIME_FORMAT_WITH_SSS_YEAR1);
            for (FTPFile ff : fs) {
                if (!ff.isDirectory() && ff.getName().contains(fileName)) {
                    String newFlieName = FilenameUtils.getBaseName(ff.getName()) + "_"
                            + tmpFileName + "_" + (++i) + ext;
                    FTP_CLIENT.rename(ff.getName(), newRemotePath + SEPARATOR + newFlieName);
                }
            }
            FTP_CLIENT.logout();
        } catch (IOException e) {
            LOGGER.error("移动ftp文件出错", e);
        } finally {
            ftpClose();
        }
    }

    /**
     * 远程FTP服务器创建多级目录，创建目录失败或发生异常则返回false
     *
     * @param multiDirectory 需要被创建的目录
     * @return 创建成功还是失败的标识
     */
    @SuppressWarnings("finally")
    private static boolean createMultiDirectory(String multiDirectory) {
        boolean bool = false;
        try {
            String[] dirs = multiDirectory.split(SEPARATOR);
            FTP_CLIENT.changeWorkingDirectory(SEPARATOR);
            // 按顺序检查目录是否存在，不存在则创建目录
            for (int i = 1; dirs != null && i < dirs.length; i++) {
                if (!FTP_CLIENT.changeWorkingDirectory(dirs[i])) {
                    if (FTP_CLIENT.makeDirectory(dirs[i])) {
                        if (!FTP_CLIENT.changeWorkingDirectory(dirs[i])) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            bool = true;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } finally {

        }
        return bool;
    }

    /**
     * 连接ftp服务器
     *
     * @param url      连接地址
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @return reply code
     * @throws SocketException 异常
     * @throws IOException     异常
     */
    private static int ftpConnect(String url, int port, String username, String password)
            throws SocketException, IOException {
        FTP_CLIENT.setControlEncoding(ENCODING);
        FTP_CLIENT.connect(url, port);
        // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
        // 登录
        FTP_CLIENT.login(username, password);
        // 设置文件传输类型为二进制
        FTP_CLIENT.setFileType(FTPClient.BINARY_FILE_TYPE);
        // 获取ftp登录应答代码
        int reply = FTP_CLIENT.getReplyCode();
        // 验证是否登陆成功
        if (!FTPReply.isPositiveCompletion(reply)) {
            FTP_CLIENT.disconnect();
            throw new IOException("FTP server refused connection.");
        }
        return reply;
    }

    /**
     * 连接ftp服务器
     *
     * @param url      连接地址
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @return reply code
     * @throws SocketException 异常
     * @throws IOException     异常
     */
    public static FTPClient getFtpConnectClient(String url, int port, String username, String password)
            throws SocketException, IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding(ENCODING);
        ftpClient.enterLocalPassiveMode();
        ftpClient.connect(url, port);
        // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
        // 登录
        ftpClient.login(username, password);
        // 设置文件传输类型为二进制
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        // 获取ftp登录应答代码
        int reply = ftpClient.getReplyCode();
        // 验证是否登陆成功
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new IOException("FTP server refused connection.");
        }
        return ftpClient;
    }

    /**
     * 端口FTP连接
     */
    private static void ftpClose() {
        try {
            if (FTP_CLIENT.isConnected()) {
                FTP_CLIENT.disconnect();
            }
        } catch (IOException e) {
            LOGGER.error("从FTP服务器断开连接失败." + e);
        }
    }
}
