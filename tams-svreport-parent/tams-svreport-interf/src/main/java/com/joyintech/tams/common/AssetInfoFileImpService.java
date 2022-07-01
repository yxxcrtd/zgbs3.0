package com.joyintech.tams.common;

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

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.common.base.entity.ChbInterfConfigDto;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.framework.thread.JoyinThreadPoolExecutor;
import com.joyintech.tams.svreport.interf.chb.assetinfo.service.SvrAssetInfoFileImpService;
import com.joyintech.tams.svreport.interf.dis.assetinfo.service.DisRepAssetInfoService;

/**
 * 功能：资产要素 外部数据文件接口入口定义类
 * @author 魏玉强
 * @version 1.0 2019年5月07日
 * @see AssetInfoFileImpService
 * @since 1.7
 */
@Service
public class AssetInfoFileImpService extends AbstractFileImpService {

	private static String[] CONTAINSFILE = {InterfDefine.资产要素信息接口数据.getCode()};
	
	@Autowired
	private SvrAssetInfoFileImpService svrAssetInfoFileImpService;
	@Autowired
	private DisRepAssetInfoService disRepAssetInfoService;

	/**
	 * 日志
	 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 功能:从外部文件中接入资产要素信息
     * 
     * @since 1.7.0
     * @version 1.7.0
     * @author: zhangzhongliang 2019年4月29日
     * @param currentDate
     *            解析文件日期
     * @return String
     */
    @Override
    public String getInfoByInterf(String currentDate) {
        Boolean success = true;
        // 获得文件
        List<InterfFile> fileLists = svrAssetInfoFileImpService.queryAndDownloadFiles(currentDate,Arrays.asList(CONTAINSFILE));
        if (CollectionUtils.isEmpty(fileLists)) {
            logger.error("找不到接口文件");
            return "success";
        }
        // 分线程处理中债与信批(若该接口只有一个系统使用，则不需要分线程)
        // 读取开关设置是否跑中债接口和信批接口三个数字 前面俩个数字只要有一个是1就跑中债接口第三个数字为1才跑信批接口
        ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(InterfDefine.资产要素信息接口数据.getCode());
        List<FileImpService> serviceList = new ArrayList<FileImpService>();
        if(interfConfig.getSysScope() != null && interfConfig.getSysScope().length()==3 && interfConfig.getSysScope().substring(0,2).contains("1")){
            serviceList.add(svrAssetInfoFileImpService);
        }
        if(interfConfig.getSysScope() != null && interfConfig.getSysScope().length()==3 && "1".equals(interfConfig.getSysScope().substring(2))){
            serviceList.add(disRepAssetInfoService);
        }
        String result = "success";
        for(InterfFile interfFile: fileLists){
            Map<String, Object> parseResult;
            try {
                // 添加文件类型和接口类型
                interfFile.setInterfType(DictCodeListExt2.上游接口文件类型_资产要素信息.getValue());
                interfFile.setFileType(interfConfig.getContentSrc());
                parseResult = svrAssetInfoFileImpService.parseFile(interfFile);
                String parseFailResult = (String) parseResult.get("remoteDir");
                if ("fail".equals(parseFailResult)){
                    // 清除文件
                    interfFile.setMoveRemovePath("fail");
                    svrAssetInfoFileImpService.clearFile(interfFile);
                    result = "fail";
                    // 继续下一个文件解析
                    continue;
                }
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(),e);
//                // 清除文件
//                interfFile.setMoveRemovePath("fail");
//                svrAssetInfoFileImpService.clearFile(interfFile);
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
                svrAssetInfoFileImpService.clearFile(interfFile);
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
                    svrAssetInfoFileImpService.clearFile(interfFile);
                    result = "fail";
                    Thread.currentThread().interrupt();
                    // 继续下一个文件解析
                    continue;
                }
            }
            // 任务结束，停止线程池的所有线程
            taskpool.shutdown();
            // 清除文件
            String isSuccess = success ? "success" : "fail";
            interfFile.setMoveRemovePath(isSuccess);
            svrAssetInfoFileImpService.clearFile(interfFile);
            success=true;
        }
        if ("fail".equals(result)){
            return "fail";
        }
        return success ? "success" : "fail";
    }
}
