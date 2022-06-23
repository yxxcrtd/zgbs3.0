package com.joyintech.tams.common;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.common.base.entity.ChbInterfConfigDto;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.svreport.interf.chb.baseinfo.service.SvrCustomerInfoFileImpService;

/**
 * @Description 市场机构接口service
 * @author jiangxiao
 * @version 1.7 2019年5月10日
 * @since 1.7
 */
@Service
@Order(2)
public class CustomerInfoFileImpService extends AbstractFileImpService {

	@Autowired
	private SvrCustomerInfoFileImpService svrCustomerInfoFileImpService;
	
	/**
	 * 日志
	 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public String getInfoByInterf(String currentDate) {
		String success = "success";
		// 获得文件
		List<InterfFile> fileLists = svrCustomerInfoFileImpService.queryAndDownloadFiles(currentDate,
				Arrays.asList(InterfDefine.市场机构数据.getCode()));
		if (CollectionUtils.isEmpty(fileLists)) {
            logger.error("找不到接口文件");
            return "success";
		}
		// 分线程处理中债与信批(若该接口只有一个系统使用，则不需要分线程)
//		List<FileImpService> serviceList = new ArrayList<FileImpService>();
//		serviceList.add(svrCustomerInfoFileImpService);

        String result = "success";
        ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(InterfDefine.市场机构数据.getCode());
		for (InterfFile interfFile : fileLists) {
			Map<String, Object> parseResult;
			try {
				// 添加文件类型和接口类型
                interfFile.setInterfType(DictCodeListExt2.上游接口文件类型_市场机构信息.getValue());
                interfFile.setFileType(interfConfig.getContentSrc());
				parseResult = svrCustomerInfoFileImpService.parseFile(interfFile);
                String parseFailResult = (String) parseResult.get("remoteDir");
                if ("fail".equals(parseFailResult)){
//                    // 清除文件
//                    interfFile.setMoveRemovePath("fail");
//                    svrCustomerInfoFileImpService.clearFile(interfFile);
                    result = "fail";
                    // 继续下一个文件解析
                    continue;
                }
			} catch (FileNotFoundException e) {
                logger.error(e.getMessage(),e);
                // 清除文件
                interfFile.setMoveRemovePath("fail");
                svrCustomerInfoFileImpService.clearFile(interfFile);
                result = "fail";
                // 继续下一个文件解析
                continue;
			}
			@SuppressWarnings("unchecked")
			List<Map<String, String>> dataList = (List<Map<String, String>>) parseResult.get("dataList");
			// 移除文件
			success = svrCustomerInfoFileImpService.importDB(dataList, interfFile, currentDate);
			interfFile.setMoveRemovePath(success);
			svrCustomerInfoFileImpService.clearFile(interfFile);
		}
        if ("fail".equals(result)){
            return "fail";
        }
		return success;
	}
}
