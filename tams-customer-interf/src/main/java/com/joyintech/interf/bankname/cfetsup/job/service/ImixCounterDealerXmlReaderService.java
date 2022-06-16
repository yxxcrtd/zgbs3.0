package com.joyintech.interf.bankname.cfetsup.job.service;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.joyintech.framework.file.SafelyFile;
import com.joyintech.interf.bankname.cfetsup.exception.FTPDirectoryAccessException;
import com.joyintech.interf.bankname.cfetsup.exception.FTPDownloadException;
import com.joyintech.interf.bankname.cfetsup.exception.FTPLoginException;
import com.joyintech.interf.bankname.cfetsup.job.dao.ImixServiceMapper;
import com.joyintech.interf.bankname.cfetsup.job.dto.FxImixBondinfoDto;
import com.joyintech.interf.bankname.cfetsup.job.dto.FxImixErrorInfoDto;
import com.joyintech.interf.bankname.cfetsup.job.dto.FxImixMemInfoDto;
import com.joyintech.interf.bankname.cfetsup.job.dto.FxImixMemMailInfoDto;
import com.joyintech.interf.bankname.cfetsup.job.dto.FxJgzcgxDto;
import com.joyintech.interf.bankname.cfetsup.utils.FTPHelper;
import com.joyintech.interf.bankname.cfetsup.utils.ZipUtils;
@Service
public class ImixCounterDealerXmlReaderService {
	/** 日志 */
//	private static Logger log = Logger
//			.getLogger(ImixCounterDealerXmlReaderService.class);
	private static Logger log = LoggerFactory.getLogger(ImixCounterDealerXmlReaderService.class);
	
	/**
	 * server
	 */
	@Autowired
	private ImixServiceMapper imixServiceMapper;
	/**
	 * 文件本地保存的临时目录D:\test
	 */
	 //private static final String TEMPFOLDER = "D:/test/"; //测试用
	private static final String TEMPFOLDER = "/nfs/rdi/rditempfile/";

	private static String[] downloadFiles = null;

	/**
	 * 需要下载的文件的前缀
	 * 
	 * @author wot_weipingwen_zy
	 * 
	 */
	private static class Prefix {
		/**
		 * 本币成员文件
		 */
		public static final String COUNTERFILE = "交易成员基本信息_银行间_";
		/**
		 * 交易员通讯录
		 */
		public static final String DEALERSFILE = "交易成员通讯录_银行间_";
		/**
		 * 错误代码对照表
		 */
		public static final String ERRORMAPFILE = "错误代码对照表_";
		/**
		 * 债务信息
		 */
		public static final String INVESTMENTFILE = "债券信息_银行间_";
		/**
		 * 机构主从关系信息
		 */
		public static final String JGZCGXFILE = "机构主从关系信息_";
		public static final String[] files = new String[] { COUNTERFILE,
				DEALERSFILE, ERRORMAPFILE, INVESTMENTFILE,JGZCGXFILE };

	}

	private String uri;

	private String ftpusername;

	private String ftpword;

	public void doBusiness() throws FTPLoginException, ArchiveException, IOException, InterruptedException {
		log.info("开始导入本币交易成员信息批处理任务");
		// 查询ftp文件存储的位置
		log.info("读取rdi服务信息");
		Map<String, String> rdiFileInfo = imixServiceMapper.getRdiFileInfo();
		if (rdiFileInfo != null) {
			uri = rdiFileInfo.containsKey("FtpRdiFilePath") ? rdiFileInfo
					.get("FtpRdiFilePath") : null;
			ftpusername = rdiFileInfo.containsKey("FtpRdiName") ? rdiFileInfo
					.get("FtpRdiName")
					: null;
			ftpword = rdiFileInfo.containsKey("FtpRdiPassword") ? rdiFileInfo
					.get("FtpRdiPassword")
					: null;
		}
		//uri = this.TEMPFOLDER; //测试用
		log.info("uri : " + uri);
		log.info("ftpusername : " + ftpusername);
		log.info("ftppassword : ******");
		// String uri = "ftp://10.7.54.143:21"; || D:/test

		// 本次要下载的文件 ftp 远程下载文件名多后缀名.ZIP
		Date accDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		downloadFiles = new String[Prefix.files.length];
		for (int i = 0; i < Prefix.files.length; i++) {
			downloadFiles[i] = Prefix.files[i] + sdf.format(accDate)
					+ ".xml.zip";
		}

		// Sonar Fix modify by huwei 220607
		File[] membersFile = getMembersFile();
		if (StringUtils.isEmpty(membersFile)) {
			membersFile = new File[]{};
		}

		ExecutorService es = Executors.newCachedThreadPool();
		
		List<Future<Boolean>> list = new ArrayList<Future<Boolean>>();
		
		// 解析文件
		for (int i = 0; i < membersFile.length; i++) {
			File file = membersFile[i];
			if (file == null) {
				continue;
			}
			if (file.getName().contains(Prefix.COUNTERFILE)) {
				list.add(es.submit(new ParseCounterFile(file)));
			} else if (file.getName().contains(Prefix.DEALERSFILE)) {
				list.add(es.submit(new ParseMemberFile(file)));
			} else if (file.getName().contains(Prefix.ERRORMAPFILE)) {
				list.add(es.submit(new ParseErrorMapperFile(file)));
			} else if (file.getName().contains(Prefix.INVESTMENTFILE)) {
				list.add(es.submit(new ParseBondFile(file)));
			} else if (file.getName().contains(Prefix.JGZCGXFILE)) {
				list.add(es.submit(new ParseJgzcFile(file)));
			} else {
				log.warn("未实现对此文件[" + file.getName() + "]的解析方式");
			}
		}
		
		int ct = 0;
		for (Future<Boolean> future : list) {
			try {
				if (future.get()) {
					ct++;
				}
			} catch (ExecutionException e) {
				e.printStackTrace();
				// Sonar Fix modify by huwei 220607
				Thread.currentThread().interrupt();
			}
		}
		es.shutdown();
		es.awaitTermination(10, TimeUnit.MINUTES);
		log.info("解析文件成功数:" + ct);
		//批量修改本币交易成员通讯表 Start
//		try {
//			imixService.updateBbjycytxl();
//		} catch (RuntimeException e) {
//			log.error("批量修改本币交易成员通讯表失败!");
//		}
		//批量修改本币交易成员通讯表 end
		log.info("任务结束!");
	}
	private class ParseBondFile implements Callable<Boolean> {
		private File membersFile;

		public ParseBondFile(File membersFile) {
			super();
			this.membersFile = membersFile;
		}

		@Override
		public Boolean call() throws IOException, SAXException, ParserConfigurationException {
			this.parseBondFile(membersFile);
			return true;
		}

		/**
		 * 
		 * @param 'file'
		 * @throws ParserConfigurationException
		 * @throws SAXException
		 * @throws IOException
		 */
		private void parseBondFile(File bondFile) throws ParserConfigurationException,SAXException, IOException {
			// 初始化参数信息
			long t1 = new Date().getTime();
			List<FxImixBondinfoDto> data = new ArrayList<FxImixBondinfoDto>();
			// 解析文件
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			//------start---------2020-11-25 周敏 添加 
			//禁用DTDs (doctypes),几乎可以防御所有xml实体攻击
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); //首选
	        //如果不能禁用DTDs,可以使用下两项，必须两项同时存在
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);        //防止外部实体POC 
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);   //防止参数实体POC
			//------end---------
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = null;
			document = builder.parse(bondFile);
			Element element = document.getDocumentElement();
			NodeList SecDef = element.getElementsByTagName("SecDef");

			log.info("--------------开始读取外汇接口债券信息-------------");
			
			for(int i = 0; i < SecDef.getLength(); i++){
				String secid = "";//债券id
				String specialdebt = "";//是否特殊债
				Element ptyDetlListRptElement = (Element)SecDef.item(i);
				specialdebt = ptyDetlListRptElement.getAttribute("SpecialDebt");
				//PartyListGrp
				NodeList partyListGrp = ptyDetlListRptElement.getElementsByTagName("Instrmt");
				for(int j = 0; j< partyListGrp.getLength() ; j++){
					Element partyListGrpElement = (Element)partyListGrp.item(j);
					secid = partyListGrpElement.getAttribute("ID");//债券id
				}
				FxImixBondinfoDto dealer = new FxImixBondinfoDto();
				dealer.setSeqno((i + 1) + "");
				dealer.setSecid(secid);
				dealer.setSpecialdebt(specialdebt);//是否特殊债
				
				
				data.add(dealer);
				//打印对应的日志方便验证
				//log.info("读取到:外汇债券信息债券代码:" + secid + " 是否特殊债:" + specialdebt );
			}
			long t2 = new Date().getTime();
			log.info("解析外汇债券信息文件结束，耗时" + (t2 -t1 ) / 1000 + "秒");
			if (data.size() != 0) {
				// 插入数据库
				long t3 = new Date().getTime();
			    // add by weipingwen @20200108 start
				try {
					imixServiceMapper.clearImixBondDealers();
				} catch ( Exception e ) {
					log.error(e.getMessage(),e);
				}
				for (int i = 0; i < data.size(); i+=500) {
					List<FxImixBondinfoDto> saImixBondinfoVos = data.subList(i, i + 500 <= data.size() ? i + 500 : data.size());
					try {
						imixServiceMapper.saveImixBondDealers(saImixBondinfoVos);
					} catch ( Exception e ) {
						log.error(e.getMessage(),e);
					}
				}
				// add by weipingwen @20200108 end
				long t4 = new Date().getTime();
				log.info("插入外汇债券信息数据，耗时" + (t4 -t3 ) / 1000 + "秒,共"+ data.size() + "条数据！");
			}
		}

	}
	/**
	 * 主从机构信息
	 * 
	 *
	 */
	private class ParseJgzcFile implements Callable<Boolean> {
		private File membersFile;

		public ParseJgzcFile(File membersFile) {
			super();
			this.membersFile = membersFile;
		}
		@Override
		public Boolean call() throws IOException, SAXException, ParserConfigurationException {
			this.parseJgzcFile(membersFile);
			return true;
		}
		/**
		 * 
		 * @param 'file'
		 * @throws ParserConfigurationException
		 * @throws SAXException
		 * @throws IOException
		 */
		private void parseJgzcFile(File jgzcFile) throws ParserConfigurationException, SAXException, IOException {
			// 初始化参数信息
			long t1 = new Date().getTime();
			List<FxJgzcgxDto> data = new ArrayList<FxJgzcgxDto>();
			// 解析文件
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			//------start---------2020-11-25 周敏 添加 
			//禁用DTDs (doctypes),几乎可以防御所有xml实体攻击
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); //首选
	        //如果不能禁用DTDs,可以使用下两项，必须两项同时存在
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);        //防止外部实体POC 
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);   //防止参数实体POC
			//------end---------
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(jgzcFile);
			Element element = document.getDocumentElement();
			//PtyDetlListRpt
			NodeList PtyDetlListRpt = element.getElementsByTagName("PtyDetlListRpt");

			log.info("--------------开始读取外汇接口机构主从关系信息-------------");

			for(int i = 0; i < PtyDetlListRpt.getLength(); i++){
				 String zjgmc="";   //主机构名称
				 String zjglwm="";  //主机构6位码
				 String cjgmc="";   //从机构名称
				 String cjglwm="";  //从机构6位码
				 String zgxlxmc=""; //主关系类型名称
				 String cgxlxmc=""; //从关系类型名称

				Element ptyDetlListRptElement = (Element)PtyDetlListRpt.item(i);
				//PartyListGrp
				NodeList partyListGrp = ptyDetlListRptElement.getElementsByTagName("PartyListGrp");
				for(int j = 0; j < partyListGrp.getLength() ; j++){
					Element partyListGrpElement = (Element)partyListGrp.item(j);
					if(0==j){
						zjglwm=partyListGrpElement.getAttribute("ID");//主机构6位码
					}else if(1==j){
						cjglwm=partyListGrpElement.getAttribute("ID");//从机构6位码
					}
					 NodeList PtysSubGrpList = partyListGrpElement.getElementsByTagName("PtysSubGrp");
					 for(int m =0;m<PtysSubGrpList.getLength();m++){
						 Element item = (Element)PtysSubGrpList.item(m);
						 if(0==j){
							 if(0==m){
								  zjgmc = item.getAttribute("ID");//添加主机构名称
							 }else if(1==m){
								 zgxlxmc = item.getAttribute("ID");//添加主关系类型名称
							 }
						 }else if(1==j){
							 if(0==m){
								 cjgmc = item.getAttribute("ID");//添加从机构名称
							 }else if(1==m){
								 cgxlxmc = item.getAttribute("ID");//添加从关系类型名称
							 }
						 }
					 }

				}
				FxJgzcgxDto dealer = new FxJgzcgxDto();
				dealer.setSeqno((i + 1) + "");
				dealer.setZjgmc(zjgmc);     //主机构名称
				dealer.setZjglwm(zjglwm);   //主机构6位码
				dealer.setZgxlxmc(zgxlxmc); //主关系类型名称
				dealer.setCjgmc(cjgmc);     //从机构名称
				dealer.setCgxlxmc(cgxlxmc); //从机构6位码
				dealer.setCjglwm(cjglwm);   //从关系类型名称
				data.add(dealer);
				//打印对应的日志方便验证
				/*log.info("读取到:外汇主机构名称:" + zjgmc + " 主机构6位码:" +
						zjglwm +"主关系类型名称:"+zgxlxmc+"从机构名称:"+cjgmc+"从机构6位码:"+cgxlxmc+"从关系类型名称"+cjglwm);*/
			}
			long t2 = new Date().getTime();
			log.info("解析外汇机构主从关系信息文件结束，耗时" + (t2 -t1 ) / 1000 + "秒");
			if (data.size() != 0) {
				// 插入数据库
				long t3 = new Date().getTime();
				// add by weipingwen @20200108 start
				try {
					imixServiceMapper.clearImixJgzcgx();
				} catch ( Exception e ) {
					log.error(e.getMessage(),e);
				}
				for (int i = 0; i < data.size(); i+=500) {
					List<FxJgzcgxDto> saJgzcgxVos = data.subList(i, i + 500 <= data.size() ? i + 500 : data.size());
					try {
						imixServiceMapper.saveJgzcgx(saJgzcgxVos);
					} catch ( Exception e ) {
						log.error(e.getMessage(),e);
					}
				}
				// add by weipingwen @20200108 end


				long t4 = new Date().getTime();
				log.info("插入外汇机构主从关系信息数据，耗时" + (t4 -t3 ) / 1000 + "秒,共"+ data.size() + "条数据！");
			}
		}

	}

	private class ParseErrorMapperFile implements Callable<Boolean> {
		private File membersFile;

		public ParseErrorMapperFile(File membersFile) {
			super();
			this.membersFile = membersFile;
		}

		@Override
		public Boolean call() throws IOException, SAXException, ParserConfigurationException {
			this.parseErrorMapperFile(membersFile);
			return true;
		}

		private void parseErrorMapperFile(File membersFile)
				throws ParserConfigurationException, SAXException, IOException {
			// 初始化参数信息
			long t1 = new Date().getTime();
			List<FxImixErrorInfoDto> data = new ArrayList<FxImixErrorInfoDto>();
			// 解析文件
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			//------start---------2020-11-25 周敏 添加 
			//禁用DTDs (doctypes),几乎可以防御所有xml实体攻击
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); //首选
	        //如果不能禁用DTDs,可以使用下两项，必须两项同时存在
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);        //防止外部实体POC 
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);   //防止参数实体POC
			//------end---------
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(membersFile);
			Element element = document.getDocumentElement();
			// PtyDetlListRpt
			log.info("--------------开始读取外汇接口错误代码信息-------------");
			NodeList parameterDefinition = element
					.getElementsByTagName("ParameterDefinition");
			for (int i = 0; i < parameterDefinition.getLength(); i++) {
				String marketIndicator = "";
				String applErrorCode = "";
				String applErrorDesc = "";
				Element parameterDefinitionElement = (Element) parameterDefinition
						.item(i);
				NodeList instrmt = parameterDefinitionElement
						.getElementsByTagName("Instrmt");
				for (int m = 0; m < instrmt.getLength(); m++) {
					Element instrmtElement = (Element) instrmt.item(m);
					marketIndicator = instrmtElement
							.getAttribute("MarketIndicator");// 市场描述
				}
				NodeList messagingControl = parameterDefinitionElement
						.getElementsByTagName("MessagingControl");
				for (int n = 0; n < messagingControl.getLength(); n++) {
					Element messagingControlElement = (Element) messagingControl
							.item(n);
					applErrorCode = messagingControlElement
							.getAttribute("ApplErrorCode");// code
					applErrorDesc = messagingControlElement
							.getAttribute("ApplErrorDesc");// 描述
				}

				FxImixErrorInfoDto dealer = new FxImixErrorInfoDto();
				dealer.setId((i+1)+ "");
				dealer.setMarketindicator(marketIndicator);// 市场描述
				dealer.setApplerrorcode(applErrorCode);// code
				dealer.setApplerrordesc(applErrorDesc);// 描述
				data.add(dealer);
				/*log.info("读取到:市场:" + marketIndicator + " 错误代码:" + applErrorCode
						+ " 错误描述:" + applErrorDesc);*/
			}
			long t2 = new Date().getTime();
			log.info("解析错误代码对照表结束，耗时" + (t2 - t1) / 1000 + "秒");
			if (data.size() != 0) {
				// 打印对应的日志方便验证
				for (int p = 0; p < data.size(); p++) {
					/*log.info(p + "市场描述:" + data.get(p).getMarketindicator()
							+ "  错误代码:" + data.get(p).getApplerrorcode()
							+ "  错误描述：" + data.get(p).getApplerrordesc());*/
				}
				// 插入数据库
				long t3 = new Date().getTime();
				// add by weipingwen @20200108 start
				try {
					imixServiceMapper.clearErrorInfo();
				} catch ( Exception e ) {
					log.error(e.getMessage(),e);
				}
				for (int i = 0; i < data.size(); i+=500) {
					List<FxImixErrorInfoDto> saImixErrorInfoVOS = data.subList(i, i + 500 <= data.size() ? i + 500 : data.size());
					try {
						imixServiceMapper.saveErrorInfo(saImixErrorInfoVOS);
					} catch ( Exception e ) {
						log.error(e.getMessage(),e);
					}
				}
				// add by weipingwen @20200108 end
				long t4 = new Date().getTime();
				log.info("插入错误代码对照表数据，耗时" + (t4 - t3) / 1000 + "秒,共"
						+ data.size() + "条数据！");
			}
		}
	}

	private class ParseMemberFile implements Callable<Boolean> {
		private File membersFile;

		public ParseMemberFile(File membersFile) {
			super();
			this.membersFile = membersFile;
		}

		@Override
		public Boolean call() throws IOException, SAXException, ParserConfigurationException {
			this.parseMemberFile(membersFile);
			return true;
		}

		private void parseMemberFile(File membersFile)
				throws ParserConfigurationException, SAXException, IOException {
			// 初始化参数信息
			long t1 = new Date().getTime();
			List<FxImixMemMailInfoDto> data = new ArrayList<FxImixMemMailInfoDto>();
			// 解析文件
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			//------start---------2020-11-25 周敏 添加 
			//禁用DTDs (doctypes),几乎可以防御所有xml实体攻击
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); //首选
	        //如果不能禁用DTDs,可以使用下两项，必须两项同时存在
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);        //防止外部实体POC 
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);   //防止参数实体POC
			//------end---------
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(membersFile);
			Element element = document.getDocumentElement();
			// PtyDetlListRpt
			NodeList ptyDetlListRpt = element
					.getElementsByTagName("PtyDetlListRpt");

			log.info("--------------开始读取外汇接口本币成员通讯录信息-------------");

			for (int i = 0; i < ptyDetlListRpt.getLength(); i++) {
				String jycyid = "";// 交易成员内部ID
				String jycy = "";// 交易成员
				String jyyid = "";// 交易员内部ID
				String jyyxm = "";// 交易员姓名
				String lxdh = "";// 联系电话
				String cz = "";// 传真
				String email = "";// Email
				String msn = "";// MSN
				String txdz = "";// 通讯地址
				String yzbm = "";// 邮政编码
				Element ptyDetlListRptElement = (Element) ptyDetlListRpt
						.item(i);
				// PartyListGrp
				NodeList partyListGrp = ptyDetlListRptElement
						.getElementsByTagName("PartyListGrp");
				for (int j = 0; j < partyListGrp.getLength(); j++) {
					Element partyListGrpElement = (Element) partyListGrp
							.item(j);
					jycyid = partyListGrpElement.getAttribute("ID");// 交易成员内部ID
					// ptysSubGrp
					NodeList ptysSubGrp = ptyDetlListRptElement
							.getElementsByTagName("PtysSubGrp");
					for (int m = 0; m < ptysSubGrp.getLength(); m++) {
						Element ptysSubGrpElement = (Element) ptysSubGrp
								.item(m);
						if ("124".equals(ptysSubGrpElement.getAttribute("Typ"))) {
							jycy = ptysSubGrpElement.getAttribute("ID");// 交易成员
						}
						if ("2".equals(ptysSubGrpElement.getAttribute("Typ"))) {
							jyyid = ptysSubGrpElement.getAttribute("ID");// 交易员内部ID
						}
						if ("126".equals(ptysSubGrpElement.getAttribute("Typ"))) {
							jyyxm = ptysSubGrpElement.getAttribute("ID");// 交易员姓名
						}

					}
					NodeList contactInfoGrp = ptyDetlListRptElement
							.getElementsByTagName("ContactInfoGrp");
					for (int n = 0; n < contactInfoGrp.getLength(); n++) {
						Element contactInfoGrpElement = (Element) contactInfoGrp
								.item(n);
						if ("6".equals(contactInfoGrpElement
								.getAttribute("ContactInfoIDType"))) {
							lxdh = contactInfoGrpElement
									.getAttribute("ContactInfoID");// 联系电话
						}
						if ("8".equals(contactInfoGrpElement
								.getAttribute("ContactInfoIDType"))) {
							cz = contactInfoGrpElement
									.getAttribute("ContactInfoID");// 传真
						}
						if ("10".equals(contactInfoGrpElement
								.getAttribute("ContactInfoIDType"))) {
							email = contactInfoGrpElement
									.getAttribute("ContactInfoID");// EMail
						}
						if ("11".equals(contactInfoGrpElement
								.getAttribute("ContactInfoIDType"))) {
							msn = contactInfoGrpElement
									.getAttribute("ContactInfoID");// MSN
						}
						if ("0".equals(contactInfoGrpElement
								.getAttribute("ContactInfoIDType"))) {
							txdz = contactInfoGrpElement
									.getAttribute("ContactInfoID");// 通讯地址
						}
						if ("5".equals(contactInfoGrpElement
								.getAttribute("ContactInfoIDType"))) {
							yzbm = contactInfoGrpElement
									.getAttribute("ContactInfoID");// 邮政编码
						}
					}
				}
				FxImixMemMailInfoDto dealer = new FxImixMemMailInfoDto();
				dealer.setId((i+1)+"");
				dealer.setJycyid(jycyid);// 交易成员内部ID
				dealer.setJycy(jycy);// 交易成员
				dealer.setJyyid(jyyid);// 交易员内部ID
				dealer.setJyyxm(jyyxm);// 交易员姓名
				dealer.setLxdh(lxdh);// 联系电话
				dealer.setCz(cz);// 传真
				dealer.setEmail(email);// EMail
				dealer.setMsn(msn);// MSN
				dealer.setTxdz(txdz);// 通讯地址
				dealer.setYzbm(yzbm);// 邮政编码
				data.add(dealer);
				// 打印对应的日志方便验证
				/*log.info("读取到:交易成员内部ID:" + jycyid + " 交易成员:" + jycy
						+ "  交易员内部ID：" + jyyid + "  交易员姓名:" + jyyxm + " 联系电话:"
						+ lxdh + " 传真:" + cz + " EMail:" + email + " 通讯地址:"
						+ txdz + " 邮政编码:" + yzbm);*/
			}
			long t2 = new Date().getTime();
			log.info("解析交易通讯录文件结束，耗时" + (t2 - t1) / 1000 + "秒");
			if (data.size() != 0) {
				// 插入数据库
				long t3 = new Date().getTime();
				//  add by weipingwen @20200108 start
				try {
					imixServiceMapper.clearImixDealers();
				} catch ( Exception e ) {
					log.error(e.getMessage(),e);
				}
				for (int i = 0; i < data.size(); i+=500) {
					List<FxImixMemMailInfoDto> saImixMemMailInfoVOS = data.subList(i, i + 500 <= data.size() ? i + 500 : data.size());
					try {
						imixServiceMapper.saveImixDealers(saImixMemMailInfoVOS);
					} catch ( Exception e ) {
						log.error(e.getMessage(),e);
					}
				}
				//  add by weipingwen @20200108 end
				long t4 = new Date().getTime();
				log.info("插入交易通讯录数据，耗时" + (t4 - t3) / 1000 + "秒,共"
						+ data.size() + "条数据！");
			}
		}

	}

	/**
	 * 解析成员数据
	 * 
	 * @author wot_weipingwen_zy
	 * 
	 */
	private class ParseCounterFile implements Callable<Boolean> {
		private File membersFile;

		public ParseCounterFile(File membersFile) {
			super();
			this.membersFile = membersFile;
		}

		@Override
		public Boolean call() throws IOException, SAXException, ParserConfigurationException {
			this.parseCounterFile(membersFile);
			return true;
		}

		private void parseCounterFile(File membersFile)
				throws ParserConfigurationException, SAXException, IOException {
			// 初始化参数信息
			long t1 = new Date().getTime();
			List<FxImixMemInfoDto> data = new ArrayList<FxImixMemInfoDto>();
			// 解析文件
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			//------start---------2020-11-25 周敏 添加 
			//禁用DTDs (doctypes),几乎可以防御所有xml实体攻击
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); //首选
	        //如果不能禁用DTDs,可以使用下两项，必须两项同时存在
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);        //防止外部实体POC 
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);   //防止参数实体POC
			//------end---------
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(membersFile);
			Element element = document.getDocumentElement();
			// PtyDetlListRpt
			log.info("--------------开始读取外汇接口交易成员基本信息--------------");
			NodeList ptyDetlListRpt = element
					.getElementsByTagName("PtyDetlListRpt");
			for (int i = 0; i < ptyDetlListRpt.getLength(); i++) {
				String jgdm = "";
				String jycyqc = "";
				String zwjc = "";
				String ywqc = "";
				String ywjc = "";
				String jycyid = "";
				Element ptyDetlListRptElement = (Element) ptyDetlListRpt
						.item(i);
				// PartyListGrp
				NodeList partyListGrp = ptyDetlListRptElement
						.getElementsByTagName("PartyListGrp");
				for (int j = 0; j < partyListGrp.getLength(); j++) {
					Element partyListGrpElement = (Element) partyListGrp
							.item(j);
					jgdm = partyListGrpElement.getAttribute("ID");// 机构代码
					// ptysSubGrp
					NodeList ptysSubGrp = ptyDetlListRptElement
							.getElementsByTagName("PtysSubGrp");
					for (int m = 0; m < ptysSubGrp.getLength(); m++) {
						Element ptysSubGrpElement = (Element) ptysSubGrp
								.item(m);
						if ("124".equals(ptysSubGrpElement.getAttribute("Typ"))) {
							jycyqc = ptysSubGrpElement.getAttribute("ID");// 交易成员全称
						}
						if ("125".equals(ptysSubGrpElement.getAttribute("Typ"))) {
							zwjc = ptysSubGrpElement.getAttribute("ID");// 中文简称
						}
						if ("5".equals(ptysSubGrpElement.getAttribute("Typ"))) {
							ywqc = ptysSubGrpElement.getAttribute("ID");// 英文全称
						}
						if ("102".equals(ptysSubGrpElement.getAttribute("Typ"))) {
							ywjc = ptysSubGrpElement.getAttribute("ID");// 英文简称
						}
						if ("2".equals(ptysSubGrpElement.getAttribute("Typ"))) {
							jycyid = ptysSubGrpElement.getAttribute("ID");// 交易成员ID
						}
					}
				}
				FxImixMemInfoDto mem = new FxImixMemInfoDto();
				mem.setId((i+1)+"");
				mem.setJgdm(jgdm);// 机构代码
				mem.setJycyqc(jycyqc);// 交易成员全称
				mem.setZwjc(zwjc);// 中文简称
				mem.setYwqc(ywqc);// 英文全称
				mem.setYwjc(ywjc);// 英文简称
				mem.setJycyid(jycyid);// 交易成员ID
				data.add(mem);
				/*log.info("读取到:交易机构: " + jgdm + " 交易成员全程: " + jycyqc + " 中文简称: "
						+ zwjc + " 英文全称:" + ywqc + " 英文简称:" + ywjc + " 交易成员id："
						+ jycyid);*/
			}
			long t2 = new Date().getTime();
			log.info("解析本币交易成员信息文件结束，耗时" + (t2 - t1) / 1000 + "秒");
			if (data.size() != 0) {
				// 插入数据库
				long t3 = new Date().getTime();
				// add by weipingwen @20200106 start
				// 删除本币交易成员信息表
				try {
					imixServiceMapper.clearImixMems();
				} catch ( Exception e ) {
					log.error(e.getMessage(),e);
				}
				for (int i = 0; i < data.size(); i+=500) {
					List<FxImixMemInfoDto> saImixMemInfoVOS = data.subList(i, i + 500 <= data.size() ? i + 500 : data.size());
					try {
						imixServiceMapper.saveImixMems(saImixMemInfoVOS);
					} catch ( Exception e ) {
						log.error(e.getMessage(),e);
					}
				}
				// add by weipingwen @20200106 end
				long t4 = new Date().getTime();
				log.info("插入本币交易成员信息，耗时" + (t4 - t3) / 1000 + "秒,共"
						+ data.size() + "条数据！");
			}
		}
	}
	/**
	 * 获取本币交易成员文件
	 * 
	 * @param 'uri'
	 *
	 * @return 结果
	 * @throws Exception
	 *             e
	 */
	private File[] getMembersFile() throws IOException, FTPLoginException, ArchiveException {

		if (uri == null) {
			return null;
		}
		if (uri.startsWith("ftp:")) {
			// 清理缓存文件
			File tempfolder = new File(TEMPFOLDER);
			if (tempfolder.exists() && tempfolder.isDirectory()) {
				FileUtils.deleteDirectory(new File(TEMPFOLDER));
			}

			Assert.hasText(this.ftpusername, "ftp服务器用户名未配置!");
			Assert.hasText(this.ftpword, "ftp服务器密码未配置!");

			// 获取文件
			return getFilesThroughFtp(uri);
		} else {
			return getLocalFiles(uri);
		}
	}

	private File[] getFilesThroughFtp(String uri) throws FTPLoginException, IOException, ArchiveException {
		log.info("通过ftp获取文件");
		// 查询ftp的用户名和密码
		// String username = "download";
		// String passwd = "UTF3MmUzcjQ="; //Q1w2e3r4

		// 连接ftp
		log.info("创建ftp连接! uri : " + uri);
		FTPHelper helper = new FTPHelper();
		int indexPort = uri.lastIndexOf(":");
		String ip = uri.substring(6, indexPort);
		Integer port = Integer.parseInt(uri.substring(indexPort + 1));
		helper.connectServer(ftpusername, ftpword, ip, port);
		log.info("下载ftp文件!");
		// 下载文件
		try {
			helper.downloadFiles("/", TEMPFOLDER, downloadFiles);
		} catch ( FTPDirectoryAccessException e) {
			log.error("",e);
			log.error("没有对对于文件的访问权限");
		} catch (FTPDownloadException e) {
			log.error("",e);
			log.error("文件下载出错");
		} catch (IOException e) {
			log.error("",e);
		}
		// 解压文件
		log.info("解压文件!");
		File tempFolder = new File(TEMPFOLDER);
		File[] subFiles = tempFolder.listFiles();
		for (File file : subFiles) {
				ZipUtils.unZip(file, TEMPFOLDER);
				log.info("..." + file.getName() + "解压成功!");
		}
		// 关闭ftp连接
		helper.closeServer();
		log.info("关闭连接");
		// 返回查找到的文件列表
		return getLocalFiles(TEMPFOLDER);
	}

	private static File[] getLocalFiles(String filepath) {
		log.info("从本地文件夹获取文件 filepath = " + filepath);
		File[] files = new File[downloadFiles.length];
		File folder = new SafelyFile(filepath);
		if (folder.exists() && folder.isDirectory()) {
			// find files under folder
			for (int i = 0; i < downloadFiles.length; i++) {
				File f = new SafelyFile(TEMPFOLDER
						+ downloadFiles[i].replaceAll(".zip", ""));
				if (f.exists()) {
					files[i] = f;
					//log.info("找到" + f.getName());
				} else {
					//log.info("未找到" + f.getName());
				}
			}
		}
		return files;
	}
}
