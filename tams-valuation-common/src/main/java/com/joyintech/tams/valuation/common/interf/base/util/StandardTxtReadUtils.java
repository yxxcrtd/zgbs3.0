/*
 * 文件名：StandardTxtReadUtils.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：dongjun
 * 修改时间：2018年10月20日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateColMatchDTO;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderDTO;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;

/**
 * 解析标准txt接口文件
 * 
 * @author dongjun
 * @version 1.0 2018年10月20日
 * @see StandardTxtReadUtils
 * @since 1.0
 */
@Service
@Scope("prototype")
public class StandardTxtReadUtils {

	private String entityPath = "com.joyintech.tams.valuation.common.interf.tempTables.entity.";

	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(StandardTxtReadUtils.class);

	/**
	 * txt类型接口文档解析
	 * 
	 * @param xmlPath
	 *            文件路径
	 * @param tmpEName
	 *            模板名称
	 * @param tempNo
	 *            预留字段，暂为空
	 * @see
	 * @since 1.0
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> TemplateCheckReturnResult readTxt(TemplateOrderInfoReaderResult acctEntity) {

		TemplateCheckReturnResult result = new TemplateCheckReturnResult();
		// 文件路径
		String filePath = acctEntity.getFilePath();
		// 模板英文名
		String tmpEName = acctEntity.getTmpEName();
		// DTO名称
		String dtoName = acctEntity.getDtoName();
		// 将匹配关系表封装成Map
		Map<String, TemplateColMatchDTO> matchs = createMatchMap(acctEntity.getListTemplateColMatchDTO());

		List<TemplateOrderInfoReaderDTO> listTemplateOrderInfoDTO = acctEntity.getListTemplateOrderInfoDTO();
		List<String> dataList = new ArrayList<>();
		List list = new ArrayList();
		List<ValInterflog> valInterflogList = new ArrayList<>();
		// 获取文件绝对路径
		List<String> pathList = SearchFilePath.queryFilePath(filePath, tmpEName);
		for (String path : pathList) {
			try {

				String entityClassName = entityPath + dtoName;
				File file = new File(path);
				if (file.isFile() && file.exists()) {
					InputStreamReader isr = null;
					BufferedReader br = null;
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(file);
						isr = new InputStreamReader(fis, "utf-8");
						br = new BufferedReader(isr);
						String lineTxt = null; // 读取文件的方法
						while (!StringUtil.isEmpty(lineTxt = br.readLine())) {
							dataList.add(lineTxt);
						}
					} catch (Exception e) {
						logger.error("", e);
					} finally {
						try {
							if (br != null) {
								br.close();
							}
						} catch (Exception e) {
							logger.error("", e);
						}
						try {
							if (isr != null) {
								isr.close();
							}
							if (fis != null){
							   fis.close();
							}
						} catch (Exception e) {
							logger.error("", e);
						}						
					}
					if (!dataList.isEmpty() && dataList.size() > 1) {
						TxtParserHander hander = new TxtParserHander();
						boolean flag = hander.init(entityClassName, dataList, listTemplateOrderInfoDTO, acctEntity,
								matchs);
						if (flag) {
							hander.txtParse();
							if (hander.getReturnList() != null && !hander.getReturnList().isEmpty()) {
								list.addAll(hander.getReturnList());
							}
						}
						if (hander.getLogResult() != null && !hander.getLogResult().isEmpty()) {
							valInterflogList.addAll(hander.getLogResult());
						}
					}
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		result.setT(list);
		result.setValInterflog(valInterflogList);
		return result;
	}

	private Map<String, TemplateColMatchDTO> createMatchMap(List<TemplateColMatchDTO> matchList) {
		Map<String, TemplateColMatchDTO> map = new HashMap<String, TemplateColMatchDTO>();
		if (CollectionUtils.isEmpty(matchList)) {
			return map;
		}
		for (TemplateColMatchDTO dto : matchList) {
			map.put(dto.getDtoColEName(), dto);
		}
		return map;
	}
}
