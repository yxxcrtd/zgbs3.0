package com.joyintech.tams.invest.common.exportpdf.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.progressbar.ProgressBarService;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.exportpdf.model.*;
import com.joyintech.tams.invest.common.util.XmlParseUtil;
import com.joyintech.tools.util.ListUtils;
import com.sun.javafx.font.FontFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 导出PDF共通
 *
 * @author zengchengfa
 * @version 1.0
 * @date 2021-07-21
 */
@Service
public class CommonExportPdfService<T> {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 数据展示方式 1.横向展示
     */
    private final String DATA_SHOW_TYPE_1 = "1";

    /**
     * 数据展示方式 2.纵向展示
     */
    private final String DATA_SHOW_TYPE_2 = "2";

    /**
     * 进度条ID
     */
    private static final String PROCESS_KEY = "exportProcessBar";

    /**
     * 进度条接口
     */
    @Autowired
    private ProgressBarService progressBarService;


    /**
     * 导出pdf
     * @param pageId
     * @param configFile
     * @param list
     * @return
     */
    public ResponseEntity<byte[]> exportPdf(String pageId ,String configFile,List<T> list){
        // 设置当前画面进度为0
        progressBarService.updatePercent(pageId, PROCESS_KEY, 0D, false);
        File outFile = null;
        String exportFileName = "";
        try {
            //读取导出pdf配置
            if(configFile.startsWith("file:")){
                configFile = configFile.replace("file:","");
            }else if(configFile.startsWith("jar:file:")){
                configFile = configFile.replace("jar:file:","");
            }
            WorkPage workPage = readXmlToJavaBean(configFile);
            //ZGZGST-2869:修改pdf导出文件临时路径 fuyongsheng 20210918 edit start
            // String filePath = CacheUtil.getSysParam("export_pdf_path");
            String filePath = CacheUtil.getSysParam("access_exportfilepath");
            //ZGZGST-2869:修改pdf导出文件临时路径 fuyongsheng 20210918 edit end
            exportFileName = workPage.getName()+"("+DateUtil.getSysDate()+").pdf";
            String fileName = filePath+"/"+ exportFileName;
            createPdfTable(workPage,list);
            // 设置当前画面进度为50
            progressBarService.updatePercent(pageId, PROCESS_KEY, 50D, false);
            outFile = new File(fileName);
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            // 删除临时目录下的配置文件
            deleteTempConfigFile(configFile);
        }catch (Exception e){
            throw new BusinessException(e);
        }finally {
            progressBarService.removePercent(pageId, PROCESS_KEY);
        }
        return ExportUtil.export(exportFileName, outFile, true);
    }

    /**
     * 创建pdf表格
     * @param workPage
     * @param list
     * @throws Exception
     */
    public void createPdfTable(WorkPage workPage,List<T> list) throws Exception{
        String fileName = "";
        if(StringUtil.isNotEmpty(workPage)){
            List<JSONObject> datas = beanToJson(list);
            //创建文件
            Document document = new Document();
            //配置页面大小
            if(StringUtil.isNotEmpty(workPage.getHeight()) && StringUtil.isNotEmpty(workPage.getWidth())){
                //PageSize.A4.getHeight(), PageSize.A4.getWidth();
                Rectangle pageSize = new Rectangle(workPage.getWidth(),workPage.getHeight());
                pageSize.rotate();
                document.setPageSize(pageSize);
            }
            //pdf文件生成目录
            //ZGZGST-2869:修改pdf导出文件临时路径 fuyongsheng 20210918 edit start
//            String filePath = CacheUtil.getSysParam("export_pdf_path");
            String filePath = CacheUtil.getSysParam("access_exportfilepath");
            //ZGZGST-2869:修改pdf导出文件临时路径 fuyongsheng 20210918 edit end
            //建立一个书写器
            fileName = filePath+"/"+workPage.getName()+"("+DateUtil.getSysDate()+").pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            //打开文件
            document.open();
            //设置本地字体-宋体
            BaseFont baseFont_resources = BaseFont.createFont("font/simsun.ttf",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
            Font font;
            //设置字体大小
            if(StringUtil.isNotEmpty(workPage.getFontSize())){
                font = new Font(baseFont_resources,Float.valueOf(workPage.getFontSize()));
            }else{
                font = new Font(baseFont_resources);
            }
            //将list切分，每页显示wokePage.getSize条数据
            List<List<JSONObject>> pageList;
            if(StringUtil.isNotEmpty(workPage.getSize())){
                pageList = ListUtils.splitList(datas,workPage.getSize()-1);
            }else{
                pageList = ListUtils.splitList(datas);
            }
            for (int p = 0; p < pageList.size(); p++) {
                //切换下一页
                if(p > 0){
                    document.newPage();
                }
                //添加标题-读取配置的name属性
                if(StringUtil.isNotEmpty(workPage.getName())){
                    Paragraph title = new Paragraph(workPage.getName(),font);
                    title.setAlignment(1);
                    // 前间距
                    title.setSpacingBefore(15f);
                    document.add(title);
                }
                //表格列数
                List<JSONObject> pageData = pageList.get(p);
                //获取表格配置
                Table table = workPage.getTable();
                //表格列数
                int cellNum = table.getSpan();
                PdfPTable pdfPTable = new PdfPTable(cellNum);
                pdfPTable.setSplitLate(false);
                pdfPTable.setSplitRows(true);
                // 宽度100%填充
                pdfPTable.setWidthPercentage(100);
                // 前间距
                pdfPTable.setSpacingBefore(10f);
                // 后间距
                pdfPTable.setSpacingAfter(10f);
                //读取行配置
                List<Row> rows = table.getRows();
                //表格行
                List<PdfPRow> listRow = pdfPTable.getRows();
                if(!StringUtil.isEmptyList(rows)){
                    //处理行数据
                    for (Row row : rows) {
                        List<Cell> cells = row.getCells();
                        if(DATA_SHOW_TYPE_1.equalsIgnoreCase(table.getType())){
                            //横向展示数据
                            //生成标题行
                            PdfPCell pdfPCells[] = new PdfPCell[cellNum];
                            PdfPRow pdfPRowRow = new PdfPRow(pdfPCells);
                            //水平样式
                            String horizontal = table.getHorizontal();
                            //垂直样式
                            String vertical = table.getVertical();
                            //内边距
                            String padding = table.getPadding();
                            for (int i = 0; i < cells.size(); i++) {
                                //设置字体大小
                                if(StringUtil.isNotEmpty(table.getFontSize())){
                                    font = new Font(baseFont_resources,Float.valueOf(table.getFontSize()));
                                }else{
                                    font = new Font(baseFont_resources);
                                }
                                String str = cells.get(i).getName() == null?"":cells.get(i).getName();
                                pdfPCells[i] = new PdfPCell(new Paragraph(str,font));
                                //背景色
                                pdfPCells[i].setBackgroundColor(new BaseColor(128, 128, 128));
                                //水平样式
                                if(StringUtil.isNotEmpty(horizontal)){
                                    pdfPCells[i].setHorizontalAlignment(Integer.valueOf(horizontal));
                                }
                                //垂直样式
                                if(StringUtil.isNotEmpty(vertical)){
                                    pdfPCells[i].setVerticalAlignment(Integer.valueOf(vertical));
                                }
                                //内边距
                                if(StringUtil.isNotEmpty(padding)){
                                    pdfPCells[i].setPadding(Integer.valueOf(padding));
                                }
                                //跨列
                                if(StringUtil.isNotEmpty(cells.get(i).getColspan())){
                                    pdfPCells[i].setColspan(cells.get(i).getColspan());
                                }
                            }
                            if(pdfPRowRow != null){
                                listRow.add(pdfPRowRow);
                            }
                            //生成数据行
                            //水平样式
                            String horizontal1 = row.getHorizontal();
                            //垂直样式
                            String vertical1 = row.getVertical();
                            //内边距
                            String padding1 = row.getPadding();
                            for (JSONObject data : pageData) {
                                PdfPCell dataCells[] = new PdfPCell[cellNum];
                                PdfPRow dataRow = new PdfPRow(dataCells);
                                //设置字体大小
                                if(StringUtil.isNotEmpty(row.getFontSize())){
                                    font = new Font(baseFont_resources,Float.valueOf(row.getFontSize()));
                                }else{
                                    font = new Font(baseFont_resources);
                                }
                                for (int i = 0; i < cells.size(); i++) {
                                    String str = data.get(cells.get(i).getValue()) == null?"":data.get(cells.get(i).getValue()).toString();
                                    dataCells[i] = new PdfPCell(new Paragraph(str,font));
                                    //水平样式
                                    if(StringUtil.isNotEmpty(horizontal1)){
                                        dataCells[i].setHorizontalAlignment(Integer.valueOf(horizontal1));
                                    }
                                    //垂直样式
                                    if(StringUtil.isNotEmpty(vertical1)){
                                        dataCells[i].setVerticalAlignment(Integer.valueOf(vertical1));
                                    }
                                    //内边距
                                    if(StringUtil.isNotEmpty(padding1)){
                                        dataCells[i].setPadding(Integer.valueOf(padding1));
                                    }
                                    //跨列
                                    if(StringUtil.isNotEmpty(cells.get(i).getColspan())){
                                        dataCells[i].setColspan(cells.get(i).getColspan());
                                    }
                                }
                                if(pdfPRowRow != null){
                                    listRow.add(dataRow);
                                }
                            }
                        }else {
                            //纵向展示数据-循环xml配置cell,逐行处理数据
                            int num = row.getCells().size();
                            for (int i = 0; i < num; i++) {
                                PdfPCell pdfPCells[] = new PdfPCell[cellNum];
                                PdfPRow pdfPRowRow = new PdfPRow(pdfPCells);
                                //每行固定生成workPage.span列
                                for (int j = 0; j < cellNum; j++) {
                                    String str = "";
                                    //水平样式
                                    String horizontal ="";
                                    //垂直样式
                                    String vertical ="";
                                    //内边距
                                    String padding ="";
                                    if(j == 0){
                                        //标题列-取xml配置的name属性
                                        str = cells.get(i).getName() == null?"":cells.get(i).getName();
                                        //字体样式
                                        if(StringUtil.isNotEmpty(row.getFontSize())){
                                            font = new Font(baseFont_resources,Float.valueOf(row.getFontSize()));
                                        }
                                        horizontal = table.getHorizontal();
                                        //垂直样式
                                        vertical = table.getVertical();
                                        //内边距
                                        padding = table.getPadding();
                                        //内容
                                        pdfPCells[j] = new PdfPCell(new Paragraph(str,font));
                                        //背景色
                                        //pdfPCells[j].setBackgroundColor(new BaseColor(128, 128, 128));
                                        //水平样式
                                        if(StringUtil.isNotEmpty(horizontal)){
                                            pdfPCells[j].setHorizontalAlignment(Integer.valueOf(horizontal));
                                        }
                                        //垂直样式
                                        if(StringUtil.isNotEmpty(vertical)){
                                            pdfPCells[j].setVerticalAlignment(Integer.valueOf(vertical));
                                        }
                                        //内边距
                                        if(StringUtil.isNotEmpty(padding)){
                                            pdfPCells[j].setPadding(Integer.valueOf(padding));
                                        }
                                        //跨列
                                        if(StringUtil.isNotEmpty(cells.get(i).getColspan())){
                                            pdfPCells[j].setColspan(cells.get(i).getColspan());
                                        }
                                    }else{
                                        //内容列-当前列数小于数据条数时读取数据，否则显示空字符串
                                        if(pageData.size() >= j){
                                            JSONObject data = pageData.get(j-1);
                                            str = data.get(cells.get(i).getValue()) == null?"":data.get(cells.get(i).getValue()).toString();
                                        }
                                        //字体样式
                                        if(StringUtil.isNotEmpty(row.getFontSize())){
                                            font = new Font(baseFont_resources,Float.valueOf(row.getFontSize()));
                                        }
                                        horizontal = row.getHorizontal();
                                        //垂直样式
                                        vertical = row.getVertical();
                                        //内边距
                                        padding = row.getPadding();
                                        //内容
                                        if(StringUtil.isNotEmpty(cells.get(i).getType()) && cells.get(i).getType().equalsIgnoreCase("2")){
                                            String[] keys = cells.get(i).getValue().split(",");
                                            if(keys != null && keys.length > 0){
                                                String[] sides = new String[keys.length];
                                                String[] horizontals = new String[keys.length];
                                                if(StringUtil.isNotEmpty(cells.get(i).getBorderSide())){
                                                    sides = cells.get(i).getBorderSide().split(",");
                                                }
                                                if(StringUtil.isNotEmpty(cells.get(i).getHorizontal())){
                                                    horizontals = cells.get(i).getHorizontal().split(",");
                                                }
                                                PdfPTable pcellTable = new PdfPTable(1);
                                                List<PdfPRow> cellRows = pcellTable.getRows();
                                                for (int k = 0; k < keys.length ; k++) {
                                                    PdfPCell cell[] = new PdfPCell[1];
                                                    PdfPRow pcellRow =  new PdfPRow(cell);
                                                    if(pageData.size() >= j){
                                                        JSONObject data1 = pageData.get(j-1);
                                                        str = data1.get(keys[k]) == null?"":data1.get(keys[k]).toString();
                                                    }
                                                    cell[0] = new PdfPCell(new Paragraph(str,font));
                                                    //配置表格边框
                                                    if(StringUtil.isNotEmpty(sides[k])){
                                                        cell[0].disableBorderSide(Integer.valueOf(sides[k]));
                                                    }
                                                    //水平样式
                                                    if(StringUtil.isNotEmpty(horizontals[k])){
                                                        cell[0].setHorizontalAlignment(Integer.valueOf(horizontals[k]));
                                                    }else if(StringUtil.isNotEmpty(horizontal)){
                                                        cell[0].setHorizontalAlignment(Integer.valueOf(horizontal));
                                                    }
                                                    //垂直样式
                                                    if(StringUtil.isNotEmpty(vertical)){
                                                        cell[0].setVerticalAlignment(Integer.valueOf(vertical));
                                                    }
                                                    //内边距
                                                    if(StringUtil.isNotEmpty(padding)){
                                                        cell[0].setPadding(Integer.valueOf(padding));
                                                    }
                                                    //跨列
                                                    if(StringUtil.isNotEmpty(cells.get(i).getColspan())){
                                                        cell[0].setColspan(cells.get(i).getColspan());
                                                    }
                                                    if(pcellRow != null){
                                                        cellRows.add(pcellRow);
                                                    }
                                                }
                                                pdfPCells[j] = new PdfPCell(pcellTable);
                                            }
                                        }else{
                                            pdfPCells[j] = new PdfPCell(new Paragraph(str,font));
                                            //水平样式
                                            if(StringUtil.isNotEmpty(horizontal)){
                                                pdfPCells[j].setHorizontalAlignment(Integer.valueOf(horizontal));
                                            }
                                            //垂直样式
                                            if(StringUtil.isNotEmpty(vertical)){
                                                pdfPCells[j].setVerticalAlignment(Integer.valueOf(vertical));
                                            }
                                            //内边距
                                            if(StringUtil.isNotEmpty(padding)){
                                                pdfPCells[j].setPadding(Integer.valueOf(padding));
                                            }
                                            //跨列
                                            if(StringUtil.isNotEmpty(cells.get(i).getColspan())){
                                                pdfPCells[j].setColspan(cells.get(i).getColspan());
                                            }
                                        }
                                    }
                                    //pdfPCells[j] = new PdfPCell(new Paragraph(str,font));
                                }
                                if(pdfPRowRow != null){
                                    listRow.add(pdfPRowRow);
                                }
                            }
                        }
                    }
                    //设置列宽
                    //float[] columnWidths = { 1f, 1f, 1f,1f,1f,1f };
                    //table.setWidths(columnWidths);
                    //把表格添加到文件中
                    document.add(pdfPTable);

                }
            }
            //关闭文档
            document.close();
            //关闭书写器
            writer.close();
        }
    }

    /**
     * bean转json
     * @param list
     * @return
     */
    public List<JSONObject> beanToJson(List<T> list){
        return JSONArray.parseArray(JSONArray.toJSONString(list),JSONObject.class);
    }

    /**
     * 根据xml路径解析pdf配置
     * @param configFilePath
     * @return
     */
    private WorkPage readXmlToJavaBean(String configFilePath){
        return XmlParseUtil.readXmlToJavaBean(configFilePath, WorkPage.class);
    }

    /**
     * 创建临时目录下配置文件
     * @param in
     * @param tempPath
     * @param tempConfigFilePath
     * @return
     */
    public void createTempConfigFile(InputStream in, String tempPath, String tempConfigFilePath){
        if(null != in){
            File tempPathDir = new File(tempPath);
            if (!tempPathDir.exists()) {
                tempPathDir.mkdirs();
            }
            File tempFile = new File(tempConfigFilePath);
            String encoding = System.getProperty("file.encoding");
            logger.info("当前系统编码格式为：" + encoding);
            InputStreamReader reader = new InputStreamReader(in, Charset.forName(encoding));
            FileOutputStream outputStream = null;
            OutputStreamWriter oStreamWriter = null;
            try {
                outputStream = new FileOutputStream(tempFile);
                oStreamWriter = new OutputStreamWriter(outputStream, Charset.forName(encoding));
                char[] buf = new char[1024*8];
                int len = 0;
                while ((len = reader.read(buf)) != -1) {
                    oStreamWriter.write(new String(buf, 0 ,len));
                }
            } catch (IOException e) {

            }finally {
                try {
                    if(oStreamWriter != null){
                        oStreamWriter.close();
                    }
                    if(outputStream != null){
                        outputStream.close();
                    }
                    if(reader != null){
                        reader.close();
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除临时目录下的配置文件
     * @param tempConfigFilePath
     * @return
     */
    public void deleteTempConfigFile(String tempConfigFilePath){
        File tempFile = new File(tempConfigFilePath);
        if(tempFile.exists()){
            if (!tempFile.delete()) {
                // file delete failed; take appropriate action
            }
        }
    }

}
