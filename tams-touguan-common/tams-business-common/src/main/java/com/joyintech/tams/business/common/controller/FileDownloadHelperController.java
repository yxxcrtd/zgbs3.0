package com.joyintech.tams.business.common.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.service.FileDownloadHelperService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 文件下载处理服务
 * @author etliu
 * @version 1.0 2022年03月01日
 * @see FileDownloadHelperController
 * @since 1.0
 */
@Controller
@PageId("FileDownloadHelper")
@RequestMapping("/common/FileDownloadHelper")
@Api(tags = "文件下载")
public class FileDownloadHelperController extends BaseController {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * FileDownloadHelperService
     */
    @Autowired
    private FileDownloadHelperService fileDownloadHelperService;

    /**
     * 附件下载
     * @param seqNo  入参 附件编号
     * @return 处理结果
     * @throws IOException 异常信息
     */
    @ApiOperation(value = "附件下载", notes = "附件下载")
    @ResponseBody
    @PostMapping( "/downloadFile/{seqNo}")
    public void downloadFile(@PathVariable("seqNo") String seqNo, HttpServletResponse request) {
        try {
            logger.info("文件下载开始");
            seqNo = seqNo.replaceAll("\"", "");
            if (StringUtil.isNotEmpty(seqNo)) {
                fileDownloadHelperService.downloadFile(seqNo, request);
            }
            logger.info("文件下载结束");
        }catch (Exception e){
            logger.error("文件下载异常", e);
        }
        return;
    }
}
