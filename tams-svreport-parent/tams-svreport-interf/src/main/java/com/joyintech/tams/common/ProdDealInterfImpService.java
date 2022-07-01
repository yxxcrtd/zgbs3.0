package com.joyintech.tams.common;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.ChbInterfConfigDto;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.svreport.interf.chb.proddeal.service.SvrProdDealFileImpService;
/**
 * 投资者交易信息（产品交易） 外部数据文件接口入口
 * 
 * @author yuluan
 * @version 1.0 2019年5月08日
 * @see ProdDealInterfImpService
 * @since 1.7
 */
@Service
public class ProdDealInterfImpService extends AbstractFileImpService{
	@Autowired
	private SvrProdDealFileImpService svrProdDealFileImpService;
    /**
     * 日志
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * CHB_PRD_DEAL_NAME
	 */
	private static final String FILE_NAME_FX_CHB_PRD_FEEDEAL = "BTA_CLIENT_AREA";
	@Override
	public String getInfoByInterf(String currentDate) {
		String dealFileType = SvrDataUtil.getSysParam("CHB_PRD_DEAL_FILE_TYPE");
		String result="success";
		List<String> containsFile=new ArrayList<String>();
		List<InterfFile> interfiles;
		if(dealFileType.equals(SVReportGlobalConstant.CHB_PRD_DEAL_FILE_TYPE_02)){
			Date tempDate=DateUtil.parse(currentDate);
        	currentDate=DateUtil.format(tempDate, DateUtil.DEFAULT_OTHER_FORMAT);
        	containsFile.add(FILE_NAME_FX_CHB_PRD_FEEDEAL);
        	interfiles = svrProdDealFileImpService.queryAndDownloadFiles(currentDate, containsFile,SVReportGlobalConstant.IS_NEED_OK_02);
		}else{
			containsFile.add(InterfDefine.投资者交易信息接口数据.getCode());
			interfiles =	svrProdDealFileImpService.queryAndDownloadFiles(currentDate, containsFile);
		}
		if (CollectionUtils.isEmpty(interfiles)) {
            logger.error("找不到接口文件");
            return "success";
		}
		ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(InterfDefine.投资者交易信息接口数据.getCode());
//        String resulttask="success";
		for(InterfFile interfile:interfiles){
			Map<String, Object>	paseMap;
			try {
				 // 添加文件类型和接口类型
				interfile.setFileType(interfConfig.getContentSrc());
				interfile.setInterfType(DictCodeListExt2.上游接口文件类型_投资者交易信息.getValue());
				paseMap=svrProdDealFileImpService.parseFile(interfile);
                String parseFailResult = (String) paseMap.get("remoteDir");
                if ("fail".equals(parseFailResult)){
                    // 清除文件
                    interfile.setMoveRemovePath("fail");
                    svrProdDealFileImpService.clearFile(interfile);
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
//                svrProdDealFileImpService.clearFile(interfile);
                result = "fail";
                // 继续下一个文件解析
                continue;
			}
			Object value=paseMap.get("dataList");
			@SuppressWarnings("unchecked")
			List<Map<String, String>> dataList=(List<Map<String, String>>) value;
			if(dataList!=null){
				String oneResult=svrProdDealFileImpService.importDB(dataList, interfile, currentDate);
				if("fail".equals(oneResult)){
					result=oneResult;
				}
			}else{
				result="fail";
			}
			interfile.setMoveRemovePath(result);
			svrProdDealFileImpService.clearFile(interfile);
		}
        if ("fail".equals(result)){
            return "fail";
        }
		return result;
	}

}
