package com.joyintech.tams.common;

import com.joyintech.tams.common.base.entity.ChbInterfConfigDto;
import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.framework.thread.JoyinThreadPoolExecutor;
import com.joyintech.tams.svreport.interf.chb.saleinst.service.SvrSaleIderFileImpService;
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
 * 销售人员身份信息接口数据 外部数据文件接口入口定义类
 * 
 * @author
 * @version 3.0
 * @see SaleIderFileImpService
 * @since
 */
@Service
public class SaleIderFileImpService extends AbstractFileImpService {

	private static String[] CONTAINSFILE = {InterfDefine.销售人员身份信息接口数据.getCode()};
	
	@Autowired
	private SvrSaleIderFileImpService svrSaleIderFileImpService;

	/**
	 * 日志
	 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	
	/**
	 * 功能:从外部文件中接入 销售人员身份信息接口数据
	 * @return String
	 */
	@Override
	public String getInfoByInterf(String currentDate) {

		Boolean success = true;
		// 获得文件
		List<InterfFile> fileLists = svrSaleIderFileImpService.queryAndDownloadFiles(currentDate,Arrays.asList(CONTAINSFILE));
		if (CollectionUtils.isEmpty(fileLists)) {
            logger.error("找不到接口文件");
            return "success";
		}
		// 分线程处理中债与信批(若该接口只有一个系统使用，则不需要分线程)
		List<FileImpService> serviceList = new ArrayList<FileImpService>();
		ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(InterfDefine.销售人员身份信息接口数据.getCode());
		serviceList.add(svrSaleIderFileImpService);
        String result = "success";
		for(InterfFile interfFile: fileLists){
			Map<String, Object> parseResult;
			try {
				 // 添加文件类型和接口类型
				interfFile.setFileType(interfConfig.getContentSrc());
				interfFile.setInterfType(DictCodeListExt2.上游接口文件类型_销售人员身份信息.getValue());
				parseResult = svrSaleIderFileImpService.parseFile(interfFile);
                String parseFailResult = (String) parseResult.get("remoteDir");
                if ("fail".equals(parseFailResult)){
                    // 清除文件
                    interfFile.setMoveRemovePath("fail");
					svrSaleIderFileImpService.clearFile(interfFile);
                    result = "fail";
                    // 继续下一个文件解析
                    continue;
                }
			} catch (FileNotFoundException e) {
                logger.error(e.getMessage(),e);
                result = "fail";
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
						String success = "success";
						success  = service.importDB(service.copyDataList(dataList),interfFile, currentDate);
						return "success".equals(success)?true:false;
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
				svrSaleIderFileImpService.clearFile(interfFile);
                Thread.currentThread().interrupt();
                result = "fail";
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
					svrSaleIderFileImpService.clearFile(interfFile);
                    result = "fail";
                    Thread.currentThread().interrupt();
                    // 继续下一个文件解析
                    continue;
				}
			}
			// 任务结束，停止线程池的所有线程
			taskpool.shutdown();
			interfFile.setMoveRemovePath(success ? "success" : "fail");
			svrSaleIderFileImpService.clearFile(interfFile);
			
			success=true;
		}
        if ("fail".equals(result)){
            return "fail";
        }
		
		return success ? "success" : "fail";
	
	}

	
}
