package com.joyintech.tams.common;

import com.joyintech.tams.common.base.entity.ChbInterfConfigDto;
import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.framework.thread.JoyinThreadPoolExecutor;
import com.joyintech.tams.svreport.interf.chb.investmentsinfo.service.SvrChbAssetRateFileImpService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @Description 资产利率基本信息接口Service
 * @author zcw
 * @version 2021年11月10日
 * @since
 */
@Service
public class ChbAssetRateFileImpService extends AbstractFileImpService {
	
	@Autowired
	private SvrChbAssetRateFileImpService svrChbAssetRateFileImpService;

	/**
	 * 日志
	 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public String getInfoByInterf(String currentDate) {
		Boolean success = true;
		// 获得文件
		List<InterfFile> fileLists = svrChbAssetRateFileImpService.queryAndDownloadFiles(currentDate, Arrays.asList(InterfDefine.资产利率基本信息接口数据.getCode()));
		if (CollectionUtils.isEmpty(fileLists)) {
            logger.error("找不到接口文件");
            return "success";
		}
		ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(InterfDefine.资产利率基本信息接口数据.getCode());

		// 分线程处理中债与信批(若该接口只有一个系统使用，则不需要分线程)
		List<FileImpService> serviceList = new ArrayList<FileImpService>();
		if(interfConfig.getSysScope() != null && interfConfig.getSysScope().length()==3 && interfConfig.getSysScope().substring(0,2).contains("1")){
			serviceList.add(svrChbAssetRateFileImpService);
		}
        String result = "success";
		for(InterfFile interfFile: fileLists){
			Map<String, Object> parseResult;
			try {
				// 添加文件类型和接口类型
                interfFile.setInterfType(DictCodeListExt2.上游接口文件类型_资产利率基本信息.getValue());
                interfFile.setFileType(interfConfig.getContentSrc());
				parseResult = svrChbAssetRateFileImpService.parseFile(interfFile);
                String parseFailResult = (String) parseResult.get("remoteDir");
                if ("fail".equals(parseFailResult)){
                    // 清除文件
                    interfFile.setMoveRemovePath("fail");
                    svrChbAssetRateFileImpService.clearFile(interfFile);
                    result = "fail";
                    // 继续下一个文件解析
                    continue;
                }
			} catch (FileNotFoundException e) {
                logger.error(e.getMessage(),e);
//                // 清除文件
//                interfFile.setMoveRemovePath("fail");
//                svrChbAssetRateFileImpService.clearFile(interfFile);
                result = "fail";
                // 继续下一个文件解析
                continue;
			}
			@SuppressWarnings("unchecked")
			List<Map<String, String>> dataList = (List<Map<String, String>>) parseResult.get("dataList");
			
			// 创建线程池
			ExecutorService taskpool = new JoyinThreadPoolExecutor(serviceList.size());
			List<Callable<Boolean>> tasks = new ArrayList<>();
			// 创建任务
			for (int i = 0;i<serviceList.size();i++) {
				FileImpService service = serviceList.get(i);
				tasks.add(new Callable<Boolean>() {
					@Override
					public Boolean call() throws ParseException {
						Boolean res = true;
						if ("fail".equals(service.importDB(service.copyDataList(dataList),interfFile, currentDate))) {
							res = false;
						}
						return res;
					}
				});
			}
			// 为线程池添加任务
			List<Future<Boolean>> taskResult;
			try {
				taskResult = taskpool.invokeAll(tasks);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
                // 清除文件
                interfFile.setMoveRemovePath("fail");
                svrChbAssetRateFileImpService.clearFile(interfFile);
                result = "fail";
                Thread.currentThread().interrupt();
                // 继续下一个文件解析
                continue;
			}
			// 获取任务执行结果
			for (Future<Boolean> res : taskResult) {
				try {
					success = success && res.get();
				} catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage(), e);
                    // 清除文件
                    interfFile.setMoveRemovePath("fail");
                    svrChbAssetRateFileImpService.clearFile(interfFile);
                    result = "fail";
                    Thread.currentThread().interrupt();
                    // 继续下一个文件解析
                    continue;
				}
			}
			// 任务结束，停止线程池的所有线程
			taskpool.shutdown();
			// 移除文件
			String isSuccess = success ? "success" : "fail";
			interfFile.setMoveRemovePath(isSuccess);
			svrChbAssetRateFileImpService.clearFile(interfFile);
			
			success=true;
		}
        if ("fail".equals(result)){
            return "fail";
        }
		return success ? "success" : "fail";
	}

}
