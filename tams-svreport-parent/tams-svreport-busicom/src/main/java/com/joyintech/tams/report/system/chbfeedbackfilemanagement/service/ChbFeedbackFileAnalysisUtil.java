package com.joyintech.tams.report.system.chbfeedbackfilemanagement.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.joyintech.component.svrreport.chb.dto.XbrliDownLoadXml;
import com.joyintech.component.svrreport.chb.feedbackmsg.service.ChbFeedbackMsgService;
import com.joyintech.component.svrreport.chb.util.ChbXmlUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析中债反馈文件工具类，实际代码与以下类一致
 * com.joyintech.component.svrreport.chb.feedbackmsg.service.ChbFeedbackMsgService
 * @see ChbFeedbackMsgService
 */
@Component
public class ChbFeedbackFileAnalysisUtil {

    @Value("${application.chb.reportCbFeedBackHandleCountOneTime:10000}")
    private int reportCbFeedBackHandleCountOneTime;

    @Value("${application.chb.download-file-path-movepath:}")
    private String downloadFilePathMovePath;

    /**
     * 解析下载文件(数据条数过多时，分批处理)
     *
     * @param filePath 文件路径
     * @param fileName 压缩包名称
     * @return 解析结果
     * @see
     * @since 1.0
     */
    public List<XbrliDownLoadXml> analysisXmlFileList(String filePath, String fileName) {
        List<XbrliDownLoadXml> xmlList = new ArrayList<XbrliDownLoadXml>();

        File outFile = ZipUtil.unzip(filePath + fileName, filePath);
        List<String> files = CollectionUtil.toList(outFile.list());
        if (CollectionUtils.isEmpty(files)) {
            return xmlList;
        }

        List<String> datas = null;
        try {
            datas = FileUtil.readLines(filePath + fileName.replace(".zip",".xml"), "UTF-8");
        }
        catch (Exception e) {
            // 文件解析失败
            return xmlList;
        }
        if (CollectionUtils.isEmpty(datas)) {
            return xmlList;
        }

        List<String> contentList = new ArrayList<String>();

        StringBuffer headSb = new StringBuffer();
        String footSb = "</root>";
        StringBuffer contentSb = new StringBuffer();
        int contentCount = 0;

        for (String data : datas) {
            if (data.contains("</xbrli:xbrl>")) {
                continue;
            }

            if (data.contains("<wemax-ret:ErrorInformationTuple>")
                    || data.contains("<wemax-ret:CorrectInformationTuple>")) {
                contentSb.append(this.formatRowStr(data));
            }
            else if (contentSb.length() == 0 && contentList.size() == 0) {
                headSb.append(this.formatRowStr(data));
            }
            else {
                contentSb.append(this.formatRowStr(data));

                if (data.contains("</wemax-ret:ErrorInformationTuple>")
                        || data.contains("</wemax-ret:CorrectInformationTuple>")) {
                    contentCount++;

                    if (contentCount == reportCbFeedBackHandleCountOneTime) {
                        StringBuffer xmlSb = new StringBuffer();
                        contentList.add(
                                xmlSb.append(headSb).append(contentSb).append(footSb).toString());
                        contentSb.setLength(0);
                        contentCount = 0;
                    }
                }
            }
        }

        if (contentSb.length() > 0) {
            StringBuffer xmlSb = new StringBuffer();
            contentList.add(
                    xmlSb.append(headSb).append(contentSb).append(footSb).toString());
            contentSb.setLength(0);
            xmlSb.setLength(0);
        }

        for (String content : contentList) {
            XbrliDownLoadXml xml = ChbXmlUtil.convertToJavaBean(content, XbrliDownLoadXml.class);

            xmlList.add(xml);
        }

        return xmlList;
    }

    /**
     * 格式化反馈报文单行内容
     *
     * @param rowStr 反馈报文单行内容
     * @return 格式化后内容
     * @see
     * @since 1.0
     */
    private String formatRowStr(String rowStr) {
        String resultStr = "";
        if (StrUtil.isBlank(rowStr)) {
            return resultStr;
        }
        resultStr = rowStr.replaceAll("xbrli:xbrl", "root").replaceAll(":", "").trim();

        return resultStr;
    }

    /**
     * 获取下载文件解释后的移植路径
     * @return 下载文件解释后的移植路径
     */
    public String getDownloadFilePathMovePath() {
        if (StrUtil.isEmpty(downloadFilePathMovePath)) {
            return  System.getProperty("user.home") + File.separator + "chb" +  File.separator + "download" + File.separator + "bak" + File.separator;
        }
        return downloadFilePathMovePath;
    }
}
