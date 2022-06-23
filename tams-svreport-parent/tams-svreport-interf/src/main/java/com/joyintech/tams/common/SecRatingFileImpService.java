package com.joyintech.tams.common;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.common.base.entity.ChbInterfConfigDto;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.svreport.interf.chb.assetinfo.service.SvrSecRatingFileImpService;

/**
 * @Description 评级信息接口service
 * @author jiangxiao
 * @version 1.7 2019年5月10日
 * @since 1.7
 */
@Service
public class SecRatingFileImpService extends AbstractFileImpService {

	@Autowired
	private SvrSecRatingFileImpService svrSecRatingFileImpService;
	
	/**
	 * 日志
	 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public String getInfoByInterf(String currentDate) {
		String success = "success";
		// 获得文件
		List<InterfFile> fileLists = svrSecRatingFileImpService.queryAndDownloadFiles(currentDate,
				Arrays.asList(InterfDefine.债券评级数据.getCode()));
		if (CollectionUtils.isEmpty(fileLists)) {
            logger.error("找不到接口文件");
            return "success";
		}
        String result = "success";
        ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(InterfDefine.债券评级数据.getCode());
		for (InterfFile interfFile : fileLists) {
			Map<String, Object> parseResult;
			try {
				// 添加文件类型和接口类型
                interfFile.setInterfType(DictCodeListExt2.上游接口文件类型_债券评级信息.getValue());
                interfFile.setFileType(interfConfig.getContentSrc());
                logger.debug("解析评级文件--------------------开始-------------");
				parseResult = svrSecRatingFileImpService.parseFile(interfFile);
				logger.debug("解析评级文件--------------------结束-------------");
                String parseFailResult = (String) parseResult.get("remoteDir");
                if ("fail".equals(parseFailResult)){
                    // 清除文件
                    interfFile.setMoveRemovePath("fail");
                    svrSecRatingFileImpService.clearFile(interfFile);
                    result = "fail";
                    // 继续下一个文件解析
                    continue;
                }
			} catch (FileNotFoundException e) {
                logger.error(e.getMessage(),e);
//                // 清除文件
//                interfFile.setMoveRemovePath("fail");
//                svrSecRatingFileImpService.clearFile(interfFile);
                result = "fail";
                // 继续下一个文件解析
                continue;
			}
			@SuppressWarnings("unchecked")
			List<Map<String, String>> dataList = (List<Map<String, String>>) parseResult.get("dataList");
			// 移除文件
			success = svrSecRatingFileImpService.importDB(dataList, interfFile, currentDate);
			interfFile.setMoveRemovePath(success);
			svrSecRatingFileImpService.clearFile(interfFile);
		}
        if ("fail".equals(result)){
            return "fail";
        }
		return success;
	}

}
