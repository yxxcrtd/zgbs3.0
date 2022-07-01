/*
 * 文件名：InterfSendCommonWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：王波
 * 修改时间：2018年11月19日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.send.common.writer;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.valuation.common.interf.base.constant.DictCodeInterfList;
import com.joyintech.tams.valuation.common.interf.base.constant.TempConstant;
import com.joyintech.tams.valuation.common.interf.base.util.TxtParserHander;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.core.entity.InterfExecuteInfo;
import com.joyintech.tams.valuation.interf.valuation.send.assetmangementsys.vatflow.dto.ValAddTaxFlowprocessorResult;
import com.joyintech.tams.valuation.interf.valuation.send.common.service.InterfSendFtpService;
import com.joyintech.tams.valuation.interf.valuation.send.xmlCommon.util.XmlUtil;
@Service
@Scope("prototype")
public class InterfSendCommonWriter extends InterfBaseWriter<ValAddTaxFlowprocessorResult> {

    @Autowired
    private XmlUtil xmlUtil;

//    private Logger log = Logger.getLogger(getClass());
    private static Logger log = LoggerFactory.getLogger(InterfSendCommonWriter.class);

    @Autowired
    private InterfSendFtpService interfSendFtpService;

    @Override
    public void write(List<? extends ValAddTaxFlowprocessorResult> items) throws Exception {
        // TODO Auto-generated method stub
        InterfExecuteInfo executeInfo = (InterfExecuteInfo)getParams().get("executeInfo");
        Date sysDate = new Date();
        String filePath = executeInfo.getLocalRootPath() + File.separator
                + executeInfo.getSystemId() + File.separator + DateUtil.format(
                executeInfo.getInterfDate(), DateUtil.DEFAULT_OTHER_FORMAT);

        File fFilePath = new File(filePath);
        if (!fFilePath.exists()) {
            boolean b = fFilePath.mkdirs();
            if (!b) {
                log.error("无法创建文件夹：" + filePath);
            }
        }
        for (ValAddTaxFlowprocessorResult item : items) {
            if(TempConstant.数据回传_增值税流水.equals(item.getTmpEName())){
                if(item.getDatasMap() != null){
                    for(String key : item.getDatasMap().keySet()){
                        xmlUtil.transfer2XmlByBookSetId(item.getDatasMap().get(key), item.getTmpEName(), filePath, executeInfo.getInterfDate(), key, sysDate);
                        if(DictCodeInterfList.接口服务器类型_SFTP_IDENTITY.getValue().equals(executeInfo.getServerType())){
                            interfSendFtpService.doSendMyByBooksetId(executeInfo,item.getTmpEName(),executeInfo.getInterfDate(),key,sysDate);
                        }
                        else if(DictCodeInterfList.接口服务器类型_SFTP.getValue().equals(executeInfo.getServerType())){
                            interfSendFtpService.doSendByBooksetId(executeInfo,item.getTmpEName(),executeInfo.getInterfDate(),key,sysDate);
                        }
                    }
                }
            }else{
                xmlUtil.transfer2Xml(item.getDatas(), item.getTmpEName(), filePath, executeInfo.getInterfDate());
                if (item.getDatas() != null && item.getDatas().size() > 0
                        && DictCodeInterfList.接口服务器类型_SFTP.getValue().equals(executeInfo.getServerType())) {
                    interfSendFtpService.doSend(executeInfo, item);
                }
            }
        }
    }
}
