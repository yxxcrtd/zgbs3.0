package com.joyintech.tams.business.common.service;

import com.joyintech.framework.file.SafelyFile;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.systemtools.dao.JoyinFSMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 文件下载处理服务
 *
 * @author etliu
 * @version 1.0 2022年03月01日
 * @see FileDownloadHelperService
 * @since 1.0
 */
@Service
public class FileDownloadHelperService {

    /**
     * JoyinFSMapper
     */
    @Autowired
    private JoyinFSMapper joyinFSMapper;

    /**
     * 文件下载
     *
     * @param seqNo    附件编号
     * @param response response
     * @throws FileNotFoundException
     */
    public void downloadFile(String seqNo, HttpServletResponse response) throws FileNotFoundException {
        ServletOutputStream out = null;
        FileInputStream ips = null;
        try {
            //获取文件存放的路径
            SysUploadFileInfo fileInfo = joyinFSMapper.selectByPrimaryKey(seqNo);
            File file = new SafelyFile(fileInfo.getPath());
            String fileName = fileInfo.getFileName();
            //获取到文字 数据库里对应的附件名字加上老的文件名字：filename 截取到后面的文件类型 例：txt 组成一个新的文件名字：newFileName
            if (!file.exists()) {
                //如果文件不存在就跳出
                return;
            }
            ips = new FileInputStream(file);
            response.reset();
            response.setContentType("application/octet-stream");

            //为文件重新设置名字，采用数据库内存储的文件名称
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "utf-8"));
            response.addHeader("Content-Length", "" + file.length());
            out = response.getOutputStream();
            //读取文件流
            int len = 0;
            byte[] buffer = new byte[1024 * 10];
            while ((len = ips.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
        } finally {
            try {
                out.close();
                ips.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
