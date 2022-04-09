package com.joyintech.tams.valuation.batch.common.log.util;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.joyintech.framework.file.SafelyFile;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tools.util.FileUtil;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * Sftp 工具
 * 
 * @author 王波
 * @version 1.0 2018年7月25日
 * @see SftpTools
 * @since 1.0
 */
public class SFTPTool {

	/**
	 * sftp句柄
	 */
	private ChannelSftp sftp = new ChannelSftp();

	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(SFTPTool.class);

	private int downLoadNums = 0;

	/**
	 * ssh Session
	 */
	private Session sshSession;

	public SFTPTool(String userName, String password, String host, int port) throws JSchException {
		loginByPassword(userName, password, host, port);
	}

	/**
	 * Sftp 登录
	 * 
	 * @param userName      用户名
	 * @param password      【密码】或者【秘钥地址】
	 * @param host          主机名
	 * @param port          端口
	 * @param sftpLoginType 【SFTP登录方式：01:密码登录，02:私钥登录；】【系统参数【SFTP_LOGIN_TYPE】维护】
	 * @throws Exception
	 * @throws JSchException
	 */
	public void login(String userName, String password, String host, int port, String sftpLoginType) throws JSchException {

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
	 * @throws JSchException
	 */
	public void loginByPassword(String userName, String password, String host, int port) throws JSchException {
		// 接口 SFTP 服务配置
		JSch jsch = new JSch();
		sshSession = jsch.getSession(userName, host, port);
		com.jcraft.jsch.Logger jschLogger = new SettleLogger();
		JSch.setLogger(jschLogger);
		sshSession.setPassword(password);
		Properties sshConfig = new Properties();
		sshConfig.put("StrictHostKeyChecking", "no");
		sshSession.setConfig(sshConfig);
		sshSession.connect();
		Channel channel = sshSession.openChannel("sftp");
		channel.connect();
		sftp = (ChannelSftp) channel;
	}

	/**
	 * Sftp 登录(私钥登录ssh服务)
	 * 
	 * @param userName 用户名
	 * @param identity 私钥
	 * @param host     主机名
	 * @param port     端口
	 * @throws JSchException
	 */
	public void loginById(String userName, String identity, String host, int port) {
		try {
			// 接口 SFTP 服务配置
			JSch jsch = new JSch();
			com.jcraft.jsch.Logger jschLogger = new SettleLogger();
			JSch.setLogger(jschLogger);
			jsch.addIdentity(identity);
			sshSession = jsch.getSession(userName, host, port);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
		} catch (JSchException e) {
			logger.error("", e);
			throw new ServiceException("登录文件服务器失败");
		}
	}

	/**
	 * 关闭SFTP连接 <一句话功能简述> <功能详细描述>
	 */
	public void close() {
		if (sftp.isConnected()) {
			sftp.disconnect();
			logger.info("成功关闭连接SFTP！");
		}
		if (sshSession != null) {
			sshSession.disconnect();
			sshSession = null;
		}
	}

	/**
	 * sftp将本地文件上传到远程服务器
	 * 
	 * @param local  本地文件路径
	 * @param remote 远程文件路径
	 * @throws SftpException
	 */
	public void putFile(String local, String remote) throws SftpException {
		try {
			sftp.ls(remote);
		} catch (SftpException e) {
			logger.info("服务器上没有该目录：" + remote);
			mkdir(remote);
		}
		sftp.put(local, remote);
//        logger.info("文件上传成功");
	}

	/**
	 * 支持在SFTP 服务器上创建多级目录
	 * 
	 * @param remote
	 * @throws SftpException
	 * @see
	 * @since 1.0
	 */
	public void mkdir(String remote) throws SftpException {
		String curdir = sftp.pwd();
		if (remote == null) {
			logger.error("路径不能为空！");
			return;
		}
		String[] remotes = remote.split("/");
		if (remote.startsWith("/")) {
			// 绝对路径从/下开始递归创建
			sftp.cd("/");
		}
		for (String dir : remotes) {
			if (dir == null || dir.equals("")) {
				continue;
			}
			try {
				sftp.ls(dir);
			} catch (SftpException e) {
//				System.out.println("服务器上没有该目录：" + dir);
				logger.info("服务器上没有该目录：" + dir);
				sftp.mkdir(dir);
			}
			sftp.cd(dir);
		}
		sftp.cd(curdir);
	}

	/**
	 * sftp将本地文件上传到远程服务器(新建个日期目录，如20180911)
	 * 
	 * @param local  本地文件路径
	 * @param remote 远程文件路径
	 * @throws SftpException
	 */
	public void putFileDate(String local, String remote, String date) throws SftpException {
		String curDir = "";
		String curDir1 = "";
		try {
			sftp.ls(remote);
		} catch (SftpException e) {
			logger.info("服务器上没有该目录：" + remote);
			mkdir(remote);
		}
		curDir = sftp.pwd();
		sftp.cd(remote);
		curDir1 = sftp.pwd();
		try {
			sftp.ls(date);
		} catch (SftpException e) {
			logger.info("服务器上没有该目录：" + curDir + '/' + date);
			mkdir(date);
		}
		sftp.cd(date);
		sftp.cd(curDir1);
		sftp.put(local, date);
		logger.info("文件上传成功");
	}

	/**
	 * 下載文件
	 * 
	 * @param remote
	 * @param localDir
	 */
	public void getFile(String remotePath, String remoteFile, String localDir) throws SftpException {
		/**
		 * 本地文件夹
		 */
		File temp = new SafelyFile(localDir);
		if (!temp.exists()) {
			temp.mkdirs();
		}
		File file = new SafelyFile(remoteFile);
		try {
			sftp.ls(remoteFile);
		} catch (SftpException e) {
			logger.error("服务器上没有该目录：" + remoteFile);
			return;
		}
		File localfile = new SafelyFile(localDir + File.separator + file.getName());
		String localfileName = localDir + File.separator + file.getName();
		if (!localfile.exists()) {
			sftp.get(remoteFile, localfileName);
		} else {
			logger.debug("开始删除文件");
			if (localfile.delete()) {
				logger.debug("文件已经删除");
				sftp.get(remoteFile, localfileName);
			} else {
				throw new ServiceException("无法删除本地文件: " + localfileName);
			}
		}
	}

	/**
	 * 远程备份移动文件
	 * 
	 * @param string
	 */
	private void rsBackup(String backupPath, String oldFileName, String backupPathName) throws SftpException {
		try {
			sftp.ls(backupPath);
		} catch (SftpException e) {
			logger.info("服务器上没有该目录：" + backupPath);
			logger.info("开始创建目录：" + backupPath);
			mkdir(backupPath);
		}

		try {
			sftp.rename(oldFileName, backupPathName);
			logger.info("文件备份成功:");
		} catch (SftpException e) {
			logger.info("文件备份失败！");
			return;
		}
	}

	/**
	 * 本地备份文件
	 * 
	 * @param backupPath
	 * @param oldFileName
	 * @param backupPathName
	 */
	private void lsBackup(String backupPath, String oldFileName, String backupPathName) {

		File temp = new SafelyFile(backupPath);
		File backupName = new SafelyFile(backupPathName);
		File oldName = new SafelyFile(oldFileName);
		if (!temp.exists()) {
			temp.mkdirs();
		}
		oldName.renameTo(backupName);
	}

	/**
	 * 下载文件或文件夹
	 * 
	 * @param remoteFileName
	 * @param localDir
	 * @param backupPath
	 */
	public void downFileOrDir(String remoteFileName, String localDir, String backupPath) {
		try {
			String curDir = "";
			File file = new SafelyFile(remoteFileName);
			File temp = new SafelyFile(localDir);
			if (!temp.exists()) {
				temp.mkdirs();
			}
			try {
				sftp.ls(remoteFileName);
			} catch (SftpException e) {
				logger.info("服务器没有该目录" + remoteFileName);
				return;
			}
			if (isDir(remoteFileName)) {
				List<String> names = getSFtpFile(remoteFileName);
				curDir = sftp.pwd();
				sftp.cd(remoteFileName);
				for (int i = 0; i < names.size(); i++) {
					String fileName = names.get(i);
					logger.info("下载文件：" + names.get(i));
					if (isDir(names.get(i))) {
						// 文件夹
						sftp.cd(curDir);
						downFileOrDir(remoteFileName + '/' + names.get(i), localDir + '/' + names.get(i), backupPath);
					} else {
						String localfileName = localDir + File.separator + fileName;
						File localfile = new SafelyFile(localfileName);
						if (!localfile.exists()) {
							sftp.get(names.get(i), localfileName);
							downLoadNums++;
							logger.info("文件已被下载: " + names.get(i) + ": " + localfileName);
							if (StringUtil.isNotEmpty(backupPath)) {
								logger.info("开始备份文件: " + names.get(i) + "到 " + backupPath);
								rsBackup(backupPath, remoteFileName + '/' + names.get(i),
										backupPath + '/' + names.get(i));
							}
						} else {
							logger.info("开始删除文件: " + localfileName);
							if (localfile.delete()) {
								logger.info("文件已经删除: " + localfileName);
								sftp.get(names.get(i), localfileName);
								downLoadNums++;
								logger.info("文件已被下载: " + names.get(i) + ": " + localfileName);
								if (StringUtil.isNotEmpty(backupPath)) {
									logger.info("开始备份文件: " + names.get(i) + "到 " + backupPath);
									rsBackup(backupPath, remoteFileName + '/' + names.get(i),
											backupPath + '/' + names.get(i));
								}
							} else {
								throw new ServiceException("无法删除本地文件: " + localfileName);
							}
						}
					}
				}
			} else {
				try {
					sftp.ls(remoteFileName);
				} catch (SftpException e) {
					logger.info("服务器没有该目录" + remoteFileName);
					return;
				}
				File localfile = new SafelyFile(localDir + File.separator + file.getName());
				String localfileName = localDir + File.separator + file.getName();
				if (!localfile.exists()) {
					sftp.get(file.getName(), localfileName);
					downLoadNums++;
					if (StringUtil.isNotEmpty(backupPath)) {
						logger.info("开始备份文件: " + "到 " + backupPath);
						rsBackup(backupPath, remoteFileName + '/' + file.getName(), backupPath + '/' + file.getName());
					}
				} else {
					logger.info("开始删除本地文件: " + localfileName);
					if (localfile.delete()) {
						logger.debug("文件已经删除: " + localfileName);
						sftp.get(file.getName(), localfileName);
						downLoadNums++;
						logger.info("文件已被下载: " + file.getName() + ": " + localfileName);
						if (StringUtil.isNotEmpty(backupPath)) {
							logger.info("开始备份文件: " + "到 " + backupPath);
							rsBackup(backupPath, remoteFileName + '/' + file.getName(),
									backupPath + '/' + file.getName());
						}
					} else {
						throw new ServiceException("无法删除本地文件: " + localfileName);
					}
				}
				sftp.cd("..");
			}
			logger.info("下载成功！");
		} catch (SftpException e) {
			logger.error("SFTP操作失败", e);
		} finally {
			close();
		}
	}

	/**
	 * 通过进入目录方式判断是否为目录
	 * 
	 * @param path
	 * @return
	 */
	private boolean isDir(String path) {
		String curDir = "";
		try {
			curDir = sftp.pwd();
		} catch (SftpException e1) {
			logger.error("sftp 操作失败：" + e1.getMessage(), e1);
		}
		try {
			sftp.cd(path);
		} catch (SftpException e) {
			return false;
		}
		try {
			sftp.cd(curDir);
		} catch (SftpException e) {
			logger.error("", e);
		}
		return true;
	}

	/**
	 * 列出目录下的文件
	 * 
	 * @param directory 要列出的目录 /opt/wind/20141215/SRC_BONDENCASHYIELD20141215.txt
	 * @return
	 * @throws SftpException
	 */
	@SuppressWarnings("unchecked")
	public List<String> getSFtpFile(String pathname) throws SftpException {
		Vector<LsEntry> vector = null;
		List<String> res = new ArrayList<>();
		try {
			vector = sftp.ls(pathname);
		} catch (SftpException e) {
			logger.error("未找到文件:" + pathname, e);
		}
		if (vector != null) {
			for (LsEntry lsEntry : vector) {
				if (!".".equals(lsEntry.getFilename()) && !"..".equals(lsEntry.getFilename())) {
					res.add(lsEntry.getFilename());
				}
			}
		}
		return res;
	}

	/**
	 * 日志工具类
	 */
	private class SettleLogger implements com.jcraft.jsch.Logger {

		public boolean isEnabled(int level) {
			return true;
		}

		public void log(int level, String msg) {
			logger.info(msg);
		}
	}

	/**
	 * sftp将本地文件上传到远程服务器(净值)
	 * 
	 * @param local    本地文件路径
	 * @param remote   远程文件路径
	 * @param prodName 对应产品的文件
	 */
	public void putFileTransfer(String local, String remote, String prodName) {
		String curDir = "";
		String curDir1 = "";
		try {
			try {
				sftp.ls(remote);
			} catch (SftpException e) {
				logger.info("服务器上没有该目录：" + remote);
				mkdir(remote);
			}
			curDir = sftp.pwd();
			sftp.cd(remote);
			curDir1 = sftp.pwd();
			try {
				sftp.ls(prodName);
			} catch (SftpException e) {
				logger.info("服务器上没有该目录：" + curDir + '/' + prodName);
				mkdir(prodName);
			}
			sftp.cd(curDir1);
			sftp.put(local, prodName);
			logger.info("文件上传成功");
		} catch (SftpException e) {
			logger.error("文件上传失败", e);
		}
	}

	/**
	 * 下載文件
	 * 
	 * @param remotePath 附件存放路径
	 * @param fileName   文件名称
	 * @param localDir   本地下载路径
	 * @param localDir   产品唯一标识
	 * @throws IOException
	 */
	public File getFileTransfer(String remote, String fileName, String localDir, String prodName) throws IOException {
		String curDir = "";
		File temp = new SafelyFile(localDir);
		File localfile = null;
		FileOutputStream output = null;
		if (!temp.exists()) {
			temp.mkdirs();
		}
		try {
			try {
				sftp.ls(remote);
			} catch (SftpException e) {
//				System.out.println("服务器上没有该目录：" + remote);
				logger.info("服务器上没有该目录：" + remote);
				mkdir(remote);
				return null;
			}
			sftp.cd(remote);
			curDir = sftp.pwd();
			sftp.cd(curDir);
			try {
				localfile = new SafelyFile(localDir + File.separator + fileName);
				output = new FileOutputStream(localfile);
				sftp.get(fileName, output);
			} catch (SftpException e) {
				logger.error("", e);
			} finally {
				try {
					if (output != null) {
						output.close();
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			logger.info("文件下载成功");
		} catch (SftpException e) {
			logger.error("文件下载失败", e);
		}
		return localfile;
	}

	public int getDownLoadNums() {
		return downLoadNums;
	}




}