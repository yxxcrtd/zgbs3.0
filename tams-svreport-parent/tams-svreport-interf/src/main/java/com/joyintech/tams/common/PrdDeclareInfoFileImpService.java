package com.joyintech.tams.common;

import java.io.FileNotFoundException;
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
import com.joyintech.tams.svreport.interf.chb.assetbasis.service.SvrAssetBasisFileImpService;
import com.joyintech.tams.svreport.interf.chb.bottomassetposition.service.SvrBotAstPosFileImpService;
import com.joyintech.tams.svreport.interf.chb.proddeclare.service.ChbProdDeclareFileImpService;
import com.joyintech.tams.svreport.interf.dis.repassetindexInterf.service.RepassetIndexInterfService;


/**
 * 产品申报要素文件接入service
 * @author yuluan
 *
 */
@Service
public class PrdDeclareInfoFileImpService extends AbstractFileImpService{
	
	private static String[] CONTAINSFILE = {InterfDefine.产品申报信息接口数据.getCode()};
	

	@Autowired
	private ChbProdDeclareFileImpService chbProdDeclareFileImpService;
	/**
	 * 日志
	 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public String getInfoByInterf(String currentDate) {
        String success = "success";
        // 获得文件
        List<InterfFile> fileLists = chbProdDeclareFileImpService.queryAndDownloadFiles(currentDate,Arrays.asList(CONTAINSFILE));
        if (CollectionUtils.isEmpty(fileLists)) {
            logger.error("找不到接口文件");
            return "success";
        }
        String result = "success";
        ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(InterfDefine.产品申报信息接口数据.getCode());
        for (InterfFile interfFile : fileLists) {
            Map<String, Object> parseResult;
            try {
            	interfFile.setInterfType(DictCodeListExt2.上游接口文件类型_产品申报登记信息.getValue());
                interfFile.setFileType(interfConfig.getContentSrc());
                parseResult = chbProdDeclareFileImpService.parseFile(interfFile);
                String parseFailResult = (String) parseResult.get("remoteDir");
                if ("fail".equals(parseFailResult)){
                    // 清除文件
                    interfFile.setMoveRemovePath("fail");
                    chbProdDeclareFileImpService.clearFile(interfFile);
                    result = "fail";
                    // 继续下一个文件解析
                    continue;
                }
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(),e);
//                // 清除文件
//                interfFile.setMoveRemovePath("fail");
//                svrBotAstPosFileImpService.clearFile(interfFile);
                result = "fail";
                // 继续下一个文件解析
                continue;
            }
            @SuppressWarnings("unchecked")
            List<Map<String, String>> dataList = (List<Map<String, String>>) parseResult.get("dataList");
            success = chbProdDeclareFileImpService.importDB(dataList,interfFile, currentDate);
            interfFile.setMoveRemovePath(success);
            chbProdDeclareFileImpService.clearFile(interfFile);
        }
        if ("fail".equals(result)){
            return "fail";
        }
        return success;
    }

}
