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

import com.joyintech.tams.common.base.entity.ChbInterfConfigDto;
import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.svreport.interf.chb.cprdbalance.service.SvrCprodBalanceFileImpService;

/**
 * 周期型产品余额表 外部数据文件接口入口定义类
 * 
 * @author yuluan
 * @version 1.0 2019年11月05日
 * @see CprodBalanceFileImpService
 * @since 1.7
 */
@Service
public class CprodBalanceFileImpService extends AbstractFileImpService {

	private static String[] CONTAINSFILE = {"CHB_CPROD_BALANCE"};
	
	@Autowired
	private SvrCprodBalanceFileImpService svrCprodBalanceFileImpService;

	/**
	 * 日志
	 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 功能:从外部文件中接入周期型产品余额表
     * 
     * @since 1.7.0
     * @version 1.7.0
     * @author: yuluan 2019年11月05日
     * @param currentDate
     *            解析文件日期
     * @return String
     */
    @Override
    public String getInfoByInterf(String currentDate) {
        String success = "success";
        // 获得文件
        List<InterfFile> fileLists = svrCprodBalanceFileImpService.queryAndDownloadFiles(currentDate,Arrays.asList(CONTAINSFILE));
        if (CollectionUtils.isEmpty(fileLists)) {
            logger.error("找不到接口文件");
            return "success";
        }
        String result = "success";
        ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(InterfDefine.周期型产品余额接口数据.getCode());
        for (InterfFile interfFile : fileLists) {
            Map<String, Object> parseResult;
            try {
            	interfFile.setInterfType(DictCodeListExt2.上游接口文件类型_周期型产品余额.getValue());
                interfFile.setFileType(interfConfig.getContentSrc());
                parseResult = svrCprodBalanceFileImpService.parseFile(interfFile);
                String parseFailResult = (String) parseResult.get("remoteDir");
                if ("fail".equals(parseFailResult)){
                    // 清除文件
                    interfFile.setMoveRemovePath("fail");
                    svrCprodBalanceFileImpService.clearFile(interfFile);
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
            success = svrCprodBalanceFileImpService.importDB(dataList,interfFile, currentDate);
            interfFile.setMoveRemovePath(success);
            svrCprodBalanceFileImpService.clearFile(interfFile);
        }
        if ("fail".equals(result)){
            return "fail";
        }
        return success;
    }

}
