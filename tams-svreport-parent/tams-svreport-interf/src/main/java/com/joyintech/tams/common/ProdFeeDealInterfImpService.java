package com.joyintech.tams.common;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.AbstractFileImpService;
import com.joyintech.tams.common.InterfFile;
import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.common.base.entity.ChbInterfConfigDto;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.framework.thread.JoyinThreadPoolExecutor;
import com.joyintech.tams.common.InterfDefine;
import com.joyintech.tams.svreport.interf.chb.proddeal.service.SvrProdFeeDealFileImpService;
import com.joyintech.tams.svreport.interf.dis.repfeetrdinterf.service.DisRepFeeTrdInterfService;
@Service
public class ProdFeeDealInterfImpService extends AbstractFileImpService{
	@Autowired
	private SvrProdFeeDealFileImpService prodFeeDealInterfImpService;
	@Autowired
	private DisRepFeeTrdInterfService disRepFeeTrdInterfService;
	/**
	 * 日志
	 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * CHB_PRD_DEAL_NAME
	 */
//	private static final String CHB_PRD_FEEDEAL = "CHB_PRD_FEEDEAL";
	@Override
	public String getInfoByInterf(String currentDate) {
		Boolean success = true;
		List<String> containsFile=new ArrayList<String>();
		containsFile.add(InterfDefine.产品费用交易接口数据.getCode());
		List<InterfFile> interfiles =	prodFeeDealInterfImpService.queryAndDownloadFiles(currentDate, containsFile);
		if (CollectionUtils.isEmpty(interfiles)) {
            logger.error("找不到接口文件");
            return "success";
		}
		ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(InterfDefine.产品费用交易接口数据.getCode());
		List<FileImpService> serviceList = new ArrayList<FileImpService>();
		if(interfConfig.getSysScope() != null && interfConfig.getSysScope().length()==3 && interfConfig.getSysScope().substring(0,2).contains("1")){
			serviceList.add(prodFeeDealInterfImpService);
		}
		if(interfConfig.getSysScope() != null && interfConfig.getSysScope().length()==3 && "1".equals(interfConfig.getSysScope().substring(2))){
			serviceList.add(disRepFeeTrdInterfService);
		}
        String result = "success";
		for(InterfFile interfile:interfiles){
			Map<String, Object>	paseMap;
			try {
				 // 添加文件类型和接口类型
				interfile.setFileType(interfConfig.getContentSrc());
				interfile.setInterfType(DictCodeListExt2.上游接口文件类型_产品费用交易信息.getValue());
				paseMap=prodFeeDealInterfImpService.parseFile(interfile);
                String parseFailResult = (String) paseMap.get("remoteDir");
                if ("fail".equals(parseFailResult)){
                    // 清除文件
                    interfile.setMoveRemovePath("fail");
                    prodFeeDealInterfImpService.clearFile(interfile);
                    result = "fail";
                    // 继续下一个文件解析
                    continue;
                }
//				String remoteDir=paseMap.get("remoteDir").toString();
//				if("fail".equals(remoteDir)){
//					return "fail";
//				}
				
			} catch (FileNotFoundException e) {
                logger.error(e.getMessage(),e);
//                // 清除文件
//                interfile.setMoveRemovePath("fail");
//                prodFeeDealInterfImpService.clearFile(interfile);
                result = "fail";
                // 继续下一个文件解析
                continue;
			}
			Object value=paseMap.get("dataList");
			@SuppressWarnings("unchecked")
			List<Map<String, String>> dataList=(List<Map<String, String>>) value;
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
									success  = service.importDB(service.copyDataList(dataList),interfile, currentDate);
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
			                interfile.setMoveRemovePath("fail");
			                prodFeeDealInterfImpService.clearFile(interfile);
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
			                    interfile.setMoveRemovePath("fail");
			                    prodFeeDealInterfImpService.clearFile(interfile);
			                    result = "fail";
			                    Thread.currentThread().interrupt();
			                    // 继续下一个文件解析
			                    continue;
							}
						}
						// 任务结束，停止线程池的所有线程
						taskpool.shutdown();
			interfile.setMoveRemovePath(success ? "success" : "fail");
			prodFeeDealInterfImpService.clearFile(interfile);
			success=true;
		}
        if ("fail".equals(result)){
            return "fail";
        }
		return success ? "success" : "fail";
		
	}

}
