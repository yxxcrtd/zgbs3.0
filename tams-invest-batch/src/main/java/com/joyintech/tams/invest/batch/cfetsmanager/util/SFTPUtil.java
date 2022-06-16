package com.joyintech.tams.invest.batch.cfetsmanager.util;

/*
 * 文件名：SFTPUtil.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2019年3月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.h2.util.New;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * SFTP文件上传工具类
 * 
 * @author fanshuanghu
 * @version 1.0 2019年3月28日
 * @see SFTPUtil
 * @since 1.0
 */
public class SFTPUtil {

	/**
	 * 日志
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ChannelSftp sftp;

	private Session session;

	/* SFTP登录用户 */
	private String username;
	/* SFTP登录密码 */
	private String password;
	/* 私钥 */
	private String privateKey;
	/* SFTP服务器IP地址 */
	private String host;
	/* SFTP端口 */
	private int port;

	/*
	 * 构造基于密码认证的sftp对象
	 */
	public SFTPUtil(String username, String password, String host, int port) {
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
	}

	/*
	 * 构造基于密钥认证的sftp对象
	 */
	public SFTPUtil(String username, String host, int port, String privateKey) {
		this.username = username;
		this.privateKey = privateKey;
		this.host = host;
		this.port = port;
	}

	public SFTPUtil() {
	}

	/*
	 * 连接sftp服务器
	 */
	public void login() {
		logger.info("SFTPUtil" + "开始链接SFTP服务器！");
		try {
			JSch jsch = new JSch();
			if (privateKey != null) {
				jsch.addIdentity(privateKey); // 设置私钥
			}
			session = jsch.getSession(username, host, port);
			if (password != null) {
				session.setPassword(password);
			}
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");

			session.setConfig(config);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();

			sftp = (ChannelSftp) channel;
		} catch (JSchException e) {
			logger.error("连接sftp服务器失败！连接配置信息：" + username + "/" + password);
		}
	}

	/*
	 * 关闭sftp服务器
	 */
	public void logout() {
		if (sftp != null) {
			if (sftp.isConnected()) {
				sftp.disconnect();
			}
		}
		if (session != null) {
			if (session.isConnected()) {
				session.disconnect();
			}
		}
	}

	/**
	 * 上传文件流
	 * 
	 * @param sftpBasePath  服务器基础路径
	 * @param sftpDirectory 上传到该目录
	 * @param sftpFileName  sftp端文件名
	 * @param localInput    输入流
	 * @throws SftpException
	 */
	public void upload(String sftpBasePath, String sftpDirectory, String sftpFileName, InputStream localInput)
			throws SftpException {
		try {
			logger.info("SFTPUtil" + "远程目录:" + sftpBasePath + "/" + sftpDirectory + "/" + sftpFileName);
			sftp.cd(sftpBasePath);
			// Sonar Fix modify by huwei 220609
			if (StringUtils.isNotEmpty(sftpDirectory)) {
				sftp.cd(sftpDirectory);
			}
		} catch (SftpException e) {
			// 目录不存在，新建文件夹
			logger.error("SFTP服务器目录不存在！开始创建目录");
			String[] dirs = sftpDirectory.split("/");
			String tempPath = sftpBasePath;
			for (String dir : dirs) {
				if (null == dir || "".equals(dir)) {
					continue;
				}
				tempPath += "/" + dir;
				try {
					sftp.cd(dir);
				} catch (SftpException ex) {
					logger.error("SFTP目录不存在:" + tempPath);
					try {
						logger.info("SFTP开始创建目录:" + tempPath);
						sftp.mkdir(dir);
						sftp.cd(dir);
					} catch (SftpException ex2) {
						sftp.cd(dir);
						logger.error("SFTP创建目录失败:" + tempPath);
					}
				}
			}
		}
		try {
			logger.error("开始上传文件，文件名：" + sftpFileName);
			sftp.put(localInput, sftpFileName);
			logger.info("上传文件成功！文件名：" + sftpFileName);
		} catch (SftpException fe) {
			logger.error("SFTPUtil" + "上传文件异常！文件名：" + sftpFileName);
		}

	}

	/**
	 * 上传文件 支持多个文件上传，文件名用逗号分割，上传文件名不变
	 * 
	 * @param sftpBasePath   服务器基础路径
	 * @param sftpDirectory  上传到该目录
	 * @param sftpFileName   sftp端文件名
	 * @param localBasePath  本地基础路径
	 * @param localDirectory 本地文件目录
	 * @param localFileName  本地文件名
	 * @throws SftpException
	 * @throws IOException
	 */
	public void upload(String sftpBasePath, String sftpDirectory, String sftpFileName, String localBasePath,
			String localDirectory, String localFileName) throws SftpException, IOException {
		String tempFilePath = null;

		if (StringUtils.isEmpty(localFileName)) {
			logger.info("SFTPUtil" + "SFTP本地文件名不能为空！");
			throw new IOException("SFTP本地文件名不能为空！");
		}

		if (StringUtils.isEmpty(localBasePath)) {
			logger.info("SFTPUtil" + "SFTP本地文件基本路径为空！");
			if (StringUtils.isEmpty(localDirectory)) {
				logger.info("SFTPUtil" + "SFTP本地文件相对目录为空！");
				tempFilePath = "";
			} else {
				tempFilePath = localDirectory;
			}
		} else {
			if (StringUtils.isEmpty(localDirectory)) {
				logger.info("SFTPUtil" + "SFTP本地文件相对目录为空！");
				tempFilePath = localBasePath;
			} else {
				tempFilePath = localBasePath + "/" + localDirectory;
			}
		}

		String[] localFileNameList = localFileName.split(",");
		for (int i = 0; i < localFileNameList.length; i++) {
			if (localFileNameList[i].startsWith("/")) {
				tempFilePath += localFileNameList[i];
			} else {
				tempFilePath += "/" + localFileNameList[i];
			}
			logger.info("SFTP本地文件:" + tempFilePath);
			File file = new File(tempFilePath);

			try {
				if (!file.exists()) {
					logger.info("SFTPUtil" + "SFTP本地文件不存在！");
					throw new IOException("SFTP本地文件不存在！");
				}
				InputStream input = new FileInputStream(file);
				// 远程文件名暂时设置为与本地文件名一致
				// sftpFileName = localFileNameList[i];

				this.upload(sftpBasePath, sftpDirectory, sftpFileName, input);
			} catch (IOException e) {
				logger.info("文件上传失败！");
			}
		}
	}

	public void uploadSVAfiles(String sftpPath, String sftpFileName, InputStream localInput) throws SftpException {
		try {
			sftp.cd(sftpPath);
		} catch (SftpException e) {
			// 目录不存在，新建文件夹
			logger.info("SFTP服务器目录不存在！开始创建目录");
			String[] dirs = sftpPath.split("/");
			String tempPath = null;
			for (String dir : dirs) {
				if (null == dir || "".equals(dir)) {
					continue;
				}
				tempPath += "/" + dir;
				try {
					sftp.cd(dir);
				} catch (SftpException ex) {
					logger.info("SFTP服务器：" + tempPath + "目录不存在。");
					try {
						logger.info("开始创建" + tempPath + "目录。");
						sftp.mkdir(dir);
						sftp.cd(dir);
					} catch (SftpException ex2) {
						sftp.cd(dir);
						logger.error("创建" + tempPath + "目录失败。");
					}
				}
			}
		}
		try {
			logger.info("开始上传文件，文件名：" + sftpFileName);
			sftp.put(localInput, sftpFileName);
			logger.info("上传文件成功！文件名：" + sftpFileName);
		} catch (SftpException fe) {
			logger.error("SVA上传文件异常！文件名：" + sftpFileName);
		}
	}

	/**
	 * sftp文件下载
	 * 
	 * @param tmpFile
	 */
	public boolean download(String sftpPath, String sftpFileName, FileOutputStream fileOutputStream)
			throws SftpException {
		// 判断文件是否存在
		boolean flag = true;
		try {
			sftp.cd(sftpPath);
			sftp.get(sftpFileName, fileOutputStream);
		} catch (SftpException e) {
			// 目录不存在，
			logger.info("SFTP服务器目录不存在！错误信息：" + e.getMessage());
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 通过http下载文件
	 * @param url
	 * @param downPath 问价下载地址
	 * @return
	 */
	public List<File> downUrlXml(String url,String downPath,String fileName) {
		try {
			File file = new File(downPath);
			if(!file.isDirectory()) {
				file .mkdirs();
			}

			//创建URL资源
			URL urlResource = new URL(url);
			
			//建立http连接
			HttpURLConnection connection = (HttpURLConnection)urlResource.openConnection();
			
			//设置允许输出
			connection.setDoOutput(true);
			
			//设置允许输入
			connection.setDoInput(true);
			
			//设置不用缓存
			connection.setUseCaches(false);
			
			//设置请求方式
			connection.setRequestMethod("GET");
			
			//设置文件字符集
			connection.setRequestProperty("Charset", "UTF-8");
			
			//设置维持长连接
			connection.setRequestProperty("Connection", "Keep-Alive");
			
			//设置文件类型
			connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			
			//连接超时时间
			connection.setConnectTimeout(10 * 1000);

			//响应时间
			connection.setReadTimeout(120 * 1000);
			
			//开始请求
			connection.connect();
			
			// 获取connection对象对应的输出流
			InputStream is = connection.getInputStream();
			//InputStream is = new FileInputStream(new File("D:\\cfetsData\\20201228\\机构主从关系信息_20201228.xml.zip"));
			//创建存放文件内容的数组
	        byte[] buff =new byte[1024];
	        //所读取的内容使用n来接收
	        int n;
	        //当没有读取完时,继续读取,循环
			try (OutputStream out = new FileOutputStream(new File(downPath+"/"+fileName))) {
				while((n=is.read(buff))!=-1){
					//将字节数组的数据全部写入到输出流中
					out.write(buff,0,n);
				}
				//强制将缓存区的数据进行输出
				out.flush();
			} catch (Exception e) {
				e.getStackTrace();
			}

	        is.close();
	        CFETSFileUtil cfetsFileUtil = new CFETSFileUtil();
	        return cfetsFileUtil.unzip(downPath+"/"+fileName, downPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("文件创建失败");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("文件解析失败");
			return null;
		}
	}
}
