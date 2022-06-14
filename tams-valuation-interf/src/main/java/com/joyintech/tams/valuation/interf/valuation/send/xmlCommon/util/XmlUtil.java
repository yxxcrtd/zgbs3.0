package com.joyintech.tams.valuation.interf.valuation.send.xmlCommon.util;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.interf.valuation.send.xmlCommon.model.Body;
import com.joyintech.tams.valuation.interf.valuation.send.xmlCommon.model.Field;
import com.joyintech.tams.valuation.interf.valuation.send.xmlCommon.model.Header;
import com.joyintech.tams.valuation.interf.valuation.send.xmlCommon.model.Info;
import com.joyintech.tams.valuation.interf.valuation.send.xmlCommon.model.Transaction;

/**
 * @author maodongjin
 *
 */
@Service
@Scope("prototype")
public class XmlUtil{

//	private Logger log = Logger.getLogger(getClass());
	private static Logger log = LoggerFactory.getLogger(XmlUtil.class);

	private final String TEMPLATE_PATH = "templates/";

	/**
	 * 根据数据和模版名称生成XML文件
	 *
	 * @param datas
	 * 	DTO集合
	 * @param templateName
	 * 	模版名称
	 * @param destDir
	 * 	文件存储路径
	 * @return
	 */
	public void transfer2Xml(List<? extends Object> datas, String templateName, String destDir, Date execDate) {

		if (StringUtil.isEmptyList(datas)) {
			log.info("没有查到相关数据");
		} else {
			Transaction transaction = getTransaction(templateName, datas);
			generateFile(destDir, transaction, execDate);
		}
//		return "";
	}


	/**
	 * 根据数据和模版名及账套代码称生成XML文件(邮储增值税流水回传文件名个性化)
	 *
	 * @param datas
	 * 	DTO集合
	 * @param templateName
	 * 	模版名称
	 * @param destDir
	 * 	文件存储路径
	 * @return
	 */
	public void transfer2XmlByBookSetId(List<? extends Object> datas, String templateName, String destDir, Date execDate,
										String bookSetId, Date sysDate) {

		if (StringUtil.isEmptyList(datas)) {
			log.info("没有查到相关数据");
		} else {
			Transaction transaction = getTransaction(templateName, datas);
			generateFileByBookSetId(destDir, transaction, execDate, bookSetId, sysDate);
		}
	}

	/**
	 * 生成文件(邮储增值税流水回传文件名个性化)
	 *
	 * @param destDir
	 * @param transaction
	 */
	private void generateFileByBookSetId(String destDir, Transaction transaction, Date execDate, String bookSetId, Date sysDate) {
		File destList = new File(destDir);
		// 找出今天生成的同一模板的所有文件
		final String prefix = new StringBuilder(transaction.getHeader().getModel()).toString();
		File newFile = new File(destList, prefix + "_" + bookSetId + "_" + DateUtil.format(execDate, DateUtil.DEFAULT_OTHER_FORMAT) + "_" +
				DateUtil.format(sysDate, "HHmmss") + ".xml");
		JAXB.marshal(transaction, newFile);
		log.info("文件生成成功：" + newFile.getAbsolutePath());
	}

	/**
	 * 组织Transaction元素信息
	 * @param templateId
	 * @param list
	 * @return
	 */
	private Transaction getTransaction(String templateId, List<? extends Object> list) {
		Transaction transaction = getTransactionTemplate(templateId, list.size());
		List<Info> infoList = transaction.getBody().getInfo();
		List<Object> infoFieldList = getInfoTemplate(templateId).getAny();
		for (Object data : list)
			infoList.add(objectToInfo(infoFieldList, data));
		return transaction;
	}

	/**
	 * 获取模板
	 *
	 * @param templateId
	 * @param count
	 * @return
	 */
	private Transaction getTransactionTemplate(String templateId, int count) {
		Transaction transaction = new Transaction();
		Header head = new Header();
		head.setDate(DateUtil.getSysDate()); // 数据日期
		head.setModel(templateId); // 接口代码
		head.setNumber(count); // 记录数
		head.setVer("1.0"); // 版本号
		transaction.setHeader(head);
		transaction.setBody(new Body());
		return transaction;
	}

	/**
	 * 获取模板
	 *
	 * @param templateId
	 * @return
	 */
	private Info getInfoTemplate(String templateId) {
		Info info = null;
		URL templateXmlURL = getClass().getClassLoader().getResource(TEMPLATE_PATH + templateId + ".xml");
		if (templateXmlURL == null)
			throw new BusinessException("模板未定义: " + templateId + ".xml");
		info = JAXB.unmarshal(templateXmlURL, Info.class);
		infoElementListToFieldList(info);
		return info;
	}

	/**
	 * Info ElementList to FieldList
	 *
	 * @param info
	 */
	private void infoElementListToFieldList(Info info) {
		List<Object> fieldList = info.getAny();
		for (int i = 0; i < fieldList.size(); i++)
			fieldList.set(i, elementToField((Element) fieldList.get(i)));
	}

	/**
	 * Element to field
	 *
	 * @param e
	 * @return
	 */
	private Field elementToField(Element e) {
		Field field = new Field();
		field.setType(e.getNodeName());
		field.setName(e.getAttribute("name"));
		field.setValue(e.getAttribute("value"));
		field.setLength(StringUtil.toInt(e.getAttribute("length")));
		field.setScale(StringUtil.toInt(e.getAttribute("scale")));
		field.setRequired(e.getAttribute("required").equals("true")?true:false);
		field.setDescription(e.getAttribute("description"));
		return field;
	}



	/**
	 * Map数据转换成info
	 *
	 * @param fieldList
	 * @param map
	 */
	private Info objectToInfo(List<Object> fieldList,Object obj) {
		Info info = new Info();
		List<Object> infoFieldList = info.getAny();
		List<String> emptyList = new ArrayList<String>();
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			map = Bean2Map.obj2Map(obj);
		} catch (IllegalAccessException e1) {
			log.error("JAVA BEAN 转为 MAP失败："+e1);
		}
		for (Object field : fieldList) {
			Field e = (Field) field;
			Object value = map.get(e.getValue());
			if (e.isRequired() && (value == null || value.toString().isEmpty()))
				emptyList.add(e.getName() + "(" + e.getDescription() + ")");
			else
				infoFieldList.add(newElement(e.getName(), value));
		}
		if (!emptyList.isEmpty())
			throw new BusinessException(StringUtils.join(emptyList, ", ") + "：这些为必输项但值为空");
		return info;
	}

//
//	/**
//	 * Map数据转换成info
//	 * 
//	 * @param fieldList
//	 * @param map
//	 */
//	private Info mapToInfo(List<Object> fieldList, Map<String, Object> map) {
//		Info info = new Info();
//		List<Object> infoFieldList = info.getAny();
//		List<String> emptyList = new ArrayList<String>();
//		for (Object field : fieldList) {
//			Field e = (Field) field;
//			Object value = map.get(e.getValue());
//			if (e.isRequired() && (value == null || value.toString().isEmpty()))
//				emptyList.add(e.getName() + "(" + e.getDescription() + ")");
//			else
//				infoFieldList.add(newElement(e.getName(), value));
//		}
//		if (!emptyList.isEmpty())
//			throw new RuntimeException(StringUtils.join(emptyList, ", ") + "：这些为必输项但值为空");
//		return info;
//	}

	/**
	 * 新建Element
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	private Object newElement(String name, Object value) {
		String val = value == null ? ""
				: value instanceof java.util.Date ? new SimpleDateFormat("yyyy-MM-dd").format(value) : value.toString();
		return new JAXBElement<String>(new QName(name), String.class, val);
	}

	/**
	 * 生成文件
	 *
	 * @param destDir
	 * @param transaction
	 */
	private void generateFile(String destDir, Transaction transaction, Date execDate) {
		File destList = new File(destDir);
		// 找出今天生成的同一模板的所有文件
		final String prefix = new StringBuilder(transaction.getHeader().getModel()).toString();
		File newFile = new File(destList, prefix + "_" + DateUtil.format(execDate, DateUtil.DEFAULT_OTHER_FORMAT) + "" + ".xml");
		JAXB.marshal(transaction, newFile);
		log.info("文件生成成功：" + newFile.getAbsolutePath());
	}
}
