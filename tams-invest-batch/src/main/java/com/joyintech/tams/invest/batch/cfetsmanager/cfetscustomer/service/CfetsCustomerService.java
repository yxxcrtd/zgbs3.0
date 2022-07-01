package com.joyintech.tams.invest.batch.cfetsmanager.cfetscustomer.service;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.collections4.CollectionUtils;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.InterfCfetsCustomerTmp;
import com.joyintech.tams.invest.batch.cfetsmanager.cfetscustomer.dao.CfetsCustomerMapper;
import com.joyintech.tams.invest.batch.cfetsmanager.cfetscustomer.util.XmlCustomerUtil;
import com.joyintech.tams.invest.batch.cfetsmanager.util.SFTPUtil;
import com.joyintech.tams.invest.common.service.MapperDelegateService;

/**
 * JET下行基础信息-交易机构信息-主体信息
 * @author EX-ANJIAJUN001
 *
 */
@Service	
@Transactional(rollbackFor = Exception.class)
@Scope("prototype")
public class CfetsCustomerService {

	/**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(CfetsCustomerService.class);
    
    @Autowired
    private MapperDelegateService mapperDelegateService;
    /**
     * 文件名
     */
    private static String fileName = "cfetsCustomer";
    
    @Autowired
    private CfetsCustomerMapper cfetsCustomerMapper;
    
    /**
     * 加密解密工具类
     */
    @Autowired
    private StringEncryptor stringEncryptor;
    
    /**
     * 执行导入交易机构信息
     * @param batchDate  执行时间
     * @throws Exception
     * @see
     * @since 1.0
     */
    public void autoCustomerInfo(Date batchDate) throws Exception {
    	logger.info("CfetsCustomerService ---执行的操作选择的时间为：" + batchDate);
    	String date = DateUtil.format(batchDate, DateUtil.DEFAULT_OTHER_FORMAT);
    	/*// 服务器地址
        String sftpServer = CacheUtil.getSysParam("CFETS_SFTP_SERVER");
		// 服务器端口号
		String port = CacheUtil.getSysParam("CFETS_FTP_PORT");
        // 用户名
        String userName = CacheUtil.getSysParam("CFETS_SFTP_USER_NAME");
        // 服务器密码
        String password = CacheUtil.getSysParam("CFETS_SFTP_PASSWORD");
        if (StringUtil.startsWith(password, "ENC(") && StringUtil.endsWith(password, ")")) {
            password = stringEncryptor.decrypt(
                StringUtil.substring(password, 4, password.length() - 1));
        }
        logger.info("CfetsCustomerService  远端文件服务器地址为："+sftpServer+"，端口号为"+port+"，用户名为："+userName+"，密码为："+password);
        //从系统参数取得本地文件存放地址
  		String localFilePath = CacheUtil.getSysParam("CFETS_LOCAL_PATH");
  		//远端服务地址
	    String path = CacheUtil.getSysParam("CFETS_PATH");
	    //远端服务盘前盘后区分变量
	    String diskPath = CacheUtil.getSysParam("CFETS_SFTP_DISKPATH");
	    if(!StringUtil.isNotEmpty(localFilePath) && !StringUtil.isNotEmpty(path) && !StringUtil.isNotEmpty(diskPath)) {
	      	
	    	logger.error("CfetsCustomerService    未找到配置的路径");
	    	throw new Exception("CfetsCustomerService    未找到配置的路径");
	     }
	 // 所选日期对应的文件夹
        if (StringUtil.isNotEmpty(batchDate)) {
            if (StringUtil.equals(StringUtil.substring(localFilePath, localFilePath.length() - 1), "/")) {
            	localFilePath = localFilePath + date;
            }
            else {
            	localFilePath = localFilePath + "/" + date;
            }
            
            if (StringUtil.equals(StringUtil.substring(path, path.length() - 1), "/")) {
            	path = path + date + "/" + diskPath + "/本币交易成员信息_银行间";
            }
            else {
            	path = path + "/" + date + "/" + diskPath + "/本币交易成员信息_银行间";
            }
        }
  		logger.info("CfetsCustomerService   获取到下载文件本地存放路径为："+localFilePath);
	    logger.info("CfetsCustomerService    下载文件的服务器上的路径为："+path);
	    
	    //远端文件名称
	    String fileName = CacheUtil.getSysParam("CFETS_CUSTOMER_FILENAME") + "_"+ date + ".xml.zip";
	    logger.info("CfetsCustomerService    下载文件的名称为："+fileName);
	    
	    String localFileName = "customerinfo_"+date+".zip";
	    //下载文件并解压文件
	    List<File> fileList = new ArrayList<File>();
	    CFETSFileUtil fileUtil = new CFETSFileUtil();
	    fileList = fileUtil.downloadFile(sftpServer, port, userName, password, localFilePath, localFileName, path, fileName);*/
    	//从系统参数取得本地文件存放地址
  		String localFilePath = CacheUtil.getSysParam("CFETS_LOCAL_PATH");
  		//远端服务地址
	    String path = CacheUtil.getSysParam("CFETS_CUSTOMER_PATH");
  		// 所选日期对应的文件夹
        if (StringUtil.isNotEmpty(batchDate)) {
            if (StringUtil.equals(StringUtil.substring(localFilePath, localFilePath.length() - 1), "/")) {
            	localFilePath = localFilePath + date;
            }
            else {
            	localFilePath = localFilePath + "/" + date;
            }
            if (StringUtil.equals(StringUtil.substring(path, path.length() - 1), "/")) {
            	path = path + date ;
            }
            else {
            	path = path + "/" + date;
            }
        }
        //localFilePath = localFilePath + "/" + fileName + "_"+ date + ".xml";
  		logger.info("CfetsErrorMsgService   获取到下载文件本地存放路径为："+localFilePath);
  		logger.info("CfetsErrorMsgService    http请求路径为："+path);
  		//获取文件
  		SFTPUtil sftpUtil = new SFTPUtil();
  		List<File> fileList = sftpUtil.downUrlXml(path, localFilePath,fileName + "_"+ date + ".xml.zip");
  		//File file = sftpUtil.downUrlXml(path, localFilePath); 
	    //文件夹为空，不走逻辑
        if(!CollectionUtils.isEmpty(fileList)) {
        	//删除原有数据
        	cfetsCustomerMapper.deleteCustomerInfo();
        	cfetsCustomerMapper.deleteMstCustomerInfo();
        	for(File file : fileList) {
	        	File xmlFile = new File(file.getAbsolutePath());
	        	logger.info("CfetsCustomerService  执行导入机构信息的文件为：" + file.getAbsolutePath());
	        	//xml转换为JavaBean
	        	XmlCustomerUtil util = new XmlCustomerUtil();
	        	SAXParser sax = SAXParserFactory.newInstance().newSAXParser();
	        	sax.parse(xmlFile, util);
	        	List<InterfCfetsCustomerTmp> customerList = util.getInfoList();
	        	if(!CollectionUtils.isEmpty(customerList)) {
	        		mapperDelegateService.insertAutoClassifyBatch(customerList);
	        	}
        	}
        	//将中间表数据复制到业务表中
        	cfetsCustomerMapper.copyCustomerInfo();
        	//系统交易对手有则更新无责新增
        	//cfetsCustomerMapper.saveOrUpdate();
        }
    }
}
