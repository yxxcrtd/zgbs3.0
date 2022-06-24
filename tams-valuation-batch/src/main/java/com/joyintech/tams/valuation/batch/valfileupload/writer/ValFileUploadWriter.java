/*
 * 文件名：BookAccountGenerateDelWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：杨智
 * 修改时间：2018年6月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.valfileupload.writer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.SftpException;
import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.valuation.batch.valfileupload.dto.ValFileUploadProcessorDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.utils.SftpTools;

/**
 * 批处理文件上传Writer
 * @author weipeng
 * @version 1.0 2018年12月20日
 * @see ValFileUploadWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ValFileUploadWriter extends BaseWriter<NeedToWriteEntity> {
    
//    @Autowired
//    private BokFileGenMapper bokFileGenMapper;
    
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends NeedToWriteEntity> items){
        List<ValFileUploadProcessorDto> listDto = (List<ValFileUploadProcessorDto>) items;
        if (CollectionUtils.isNotEmpty(listDto)) {
            // 上传文件到文件服务器上
            SftpTools sftpTools = new SftpTools();
            String ip = CacheUtil.getSysParam("FILESERVER_HOST");// 主机IP
            Integer port = Integer.parseInt(CacheUtil.getSysParam("FILESERVER_PORT"));// 主机端口
            String username = CacheUtil.getSysParam("FILESERVER_USERNAME");// 主机用户名
            String password = CacheUtil.getSysParam("FILESERVER_PASSWORD");// 主机密码或秘钥地址
            String sftpLoginType = CacheUtil.getSysParam("SFTP_LOGIN_TYPE");// SFTP登录方式
            String remotePath = CacheUtil.getSysParam("FILESERVER_REMOTE_PATH");
            try {
                sftpTools.login(username, password, ip, port, sftpLoginType);
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            // 估值模板选择
            String schemeMode = CacheUtil.getSysParam("SCHEME_MODE");
            for(ValFileUploadProcessorDto dto : listDto){
                try {
                    if (StringUtils.isEmpty(schemeMode)
                        || DictCodeList.估值模板_基金模板.getValue().equals(schemeMode)) {
                        sftpTools.putFile(dto.getFilePath(), remotePath);
                    }
                    else {
                        sftpTools.putFileDate(dto.getFilePath(), remotePath,
                            DateUtil.format(dto.getGenDate(), "yyyyMMdd"));
                    }
//                    BokFileGen bokFileGen = dto.toBokFileGen();
//                    // 上传状态设为 01：未上传
//                    bokFileGen.setUploadStatus(GlobalConstant.TYPE_01);
//                    // 更新文件上传表，上传状态为Y：已上传
//                    bokFileGenMapper.updateByPrimaryKey(bokFileGen);
                }
                catch (SftpException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            sftpTools.close();
        }
       
    }

}
