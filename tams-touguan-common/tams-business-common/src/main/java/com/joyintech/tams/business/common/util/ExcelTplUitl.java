package com.joyintech.tams.business.common.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.service.DoQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExcelTplUitl {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUitl.class);

    public static File createFile(CfetsExportRequestDto requestDto,String excelName,String sheetName,String  tplfilename, DoQuery doQuery){
        File excelFile = null;
        List<BaseMap> header = requestDto.getHeader();
        List dataList = requestDto.getData();
        if (dataList == null || dataList.isEmpty()){
            List mapList = new ArrayList();
            dataList = doQuery.query();
            if (dataList != null && !dataList.isEmpty()){
                dataList.forEach(e -> {
                    if (e instanceof BaseMap) {
                        mapList.add(e);
                    } else {
                        mapList.add(BeanUtil.object2Map(e));
                    }
                });
            }
            excelFile = createFile2(excelName,sheetName, header, mapList,tplfilename);
        }else {
            List<BaseMap> dataResultList = requestDto.getData();
            try{
                Set<String> dateSet = new HashSet<String>();
                header.forEach(item ->{
                    String colCode = (String)item.get("colCode");
                    if(item.containsKey("extendCfg")){
                        Map<String, Object> extendCfgMap = (Map)JsonUtils.json2Object((String)item.get("extendCfg"),Map.class);
                        if(extendCfgMap.containsKey("dataType")&&StringUtil.equals((String)extendCfgMap.get("dataType"),"date")){
                            dateSet.add(colCode);
                        }
                    }
                });
                dataResultList.forEach(item -> {
                    for (Map.Entry<String, Object> m : item.entrySet()) {
                        if(dateSet.contains(m.getKey())&&null!=m.getValue()){
                            item.put(m.getKey(),new Date((Long)m.getValue()));
                        }
                    }
                });
            }catch (Exception e){
                logger.error("导出格式化日期异常");
            }
            excelFile = createFile2(excelName,sheetName, header, dataResultList,tplfilename);
        }
        return excelFile;
    }

    public static File createFileRepo(CfetsExportRequestDto requestDto,String excelName,String sheetName, DoQuery doQuery){
        File excelFile = null;
        List<BaseMap> header = requestDto.getHeader();
        List dataList = requestDto.getData();
        if (dataList == null || dataList.isEmpty()){
            List mapList = new ArrayList();
            dataList = doQuery.query();
            if (dataList != null && !dataList.isEmpty()){
                dataList.forEach(e -> {
                    if (e instanceof BaseMap) {
                        mapList.add(e);
                    } else {
                        mapList.add(BeanUtil.object2Map(e));
                    }
                });
            }
            excelFile = createFileNpTpl(excelName,sheetName, header, mapList);
        }else {
            List<BaseMap> dataResultList = requestDto.getData();
            try{
                Set<String> dateSet = new HashSet<String>();
                header.forEach(item ->{
                    String colCode = (String)item.get("colCode");
                    if(item.containsKey("extendCfg")){
                        Map<String, Object> extendCfgMap = (Map)JsonUtils.json2Object((String)item.get("extendCfg"),Map.class);
                        if(extendCfgMap.containsKey("dataType")&&StringUtil.equals((String)extendCfgMap.get("dataType"),"date")){
                            dateSet.add(colCode);
                        }
                    }
                });
                dataResultList.forEach(item -> {
                    for (Map.Entry<String, Object> m : item.entrySet()) {
                        if(dateSet.contains(m.getKey())&&null!=m.getValue()){
                            item.put(m.getKey(),new Date((Long)m.getValue()));
                        }
                    }
                });
            }catch (Exception e){
                logger.error("导出格式化日期异常");
            }
            excelFile = createFileNpTpl(excelName,sheetName, header, dataResultList);
        }
        return excelFile;
    }


    public static File createFile2(String fileName,String sheetName, List<BaseMap> heads, List dataList,String  tplfilename){
        File excelFile = null;
        String dir = UUID.randomUUID().toString();
        File path = null;
        /**
         * 设置格式配置信息，后面监听并加入到excel中
         */
        Map<String, BaseMap> styleMap = new HashMap<>();
        Iterator<BaseMap> it = heads.iterator();
        while (it.hasNext()){
            BaseMap one = it.next();
            String extendCfg = one.getString("extendCfg");
            Map oneMap = JsonUtils.json2Object(extendCfg, Map.class);
            oneMap.put("maxLen", one.getString("maxLen"));
            styleMap.put("{." + one.getString("colCode") + "}", BaseMap.newMap(oneMap));
        }

        StringBuffer buf = new StringBuffer();
        List<ExcelColumn> columns = new ArrayList<ExcelColumn>();
        for (Map lineMap : heads) {
            columns.add(new ExcelColumn(lineMap.get("colCode")+"", lineMap.get("colName")+""));
        }

        /**
         * 需要使用当前目录，防止生产时目录找不到导出报错
         */
        path = new File(dir);
        if (!path.exists()){
            path.mkdir();
        }
        excelFile = new File(dir + File.separator + fileName +".xlsx");
        //copy一份模板,服务器上easyexcel找不到jar的文件(FileUtils.openInputStream 报文件不存在)
        File copytplfile = new File(dir + File.separator+tplfilename);
        copyFileUsingFileStreams(tplfilename,copytplfile);
        exportAllData(excelFile, sheetName , columns, dataList, styleMap,copytplfile);
        if(copytplfile.exists()){
            copytplfile.delete();
        }
        return excelFile;
    }

    public static File createFileNpTpl(String fileName,String sheetName, List<BaseMap> heads, List dataList){
        File excelFile = null;
        String dir = UUID.randomUUID().toString();
        File path = null;
        /**
         * 设置格式配置信息，后面监听并加入到excel中
         */
        Map<String, BaseMap> styleMap = new HashMap<>();
        Iterator<BaseMap> it = heads.iterator();
        while (it.hasNext()){
            BaseMap one = it.next();
            String extendCfg = one.getString("extendCfg");
            Map oneMap = JsonUtils.json2Object(extendCfg, Map.class);
            oneMap.put("maxLen", one.getString("maxLen"));
            styleMap.put("{." + one.getString("colCode") + "}", BaseMap.newMap(oneMap));
        }

        StringBuffer buf = new StringBuffer();
        List<ExcelColumn> columns = new ArrayList<ExcelColumn>();
        for (Map lineMap : heads) {
            columns.add(new ExcelColumn(lineMap.get("colCode")+"", lineMap.get("colName")+""));
        }

        /**
         * 需要使用当前目录，防止生产时目录找不到导出报错
         */
        path = new File(dir);
        if (!path.exists()){
            path.mkdir();
        }
        excelFile = new File(dir + File.separator + fileName +".xlsx");
        long s = System.currentTimeMillis();
        exportAllData(excelFile, sheetName , columns, dataList, styleMap, null);
        return excelFile;
    }


    public static void doExport(HttpServletResponse response,List<File> files) {
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode( "本币交易导出.zip", "UTF-8"));
            ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
            byte[] buffer = new byte[1024];
            files.forEach(item ->{
                int len;
                try {
                    FileInputStream fis = new FileInputStream(item);
                    out.putNextEntry(new ZipEntry(item.getName()));
                // 读入需要下载的文件的内容，打包到zip文件
                while ((len = fis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    item.delete();
                }
            });
            out.closeEntry();
            out.close();
            // 写出响应
            //os.write(File2byte(excelFile));
            //os.flush();
        } catch (Exception e) {
            logger.error("doExport错误...", e);
            throw new RuntimeException(e.getMessage());
        } finally {
            try {os.close();} catch (IOException e) {e.printStackTrace();}
        }
    }

    public static void createExcelTemplate(File excelTemplateFile,String sheetName,List<ExcelColumn> columns, Map<String, BaseMap> styleMap) {
        List<List<String>> list = new ArrayList<List<String>>(2);
        List<String> head = new ArrayList<String>(columns.size());
        List<String> data = new ArrayList<String>(columns.size());
        for (ExcelColumn column : columns) {
            head.add(column.getName());
            data.add("{."+column.getCode()+"}");
        }
        list.add(head);
        list.add(data);

        EasyExcel.write(excelTemplateFile).inMemory(true).sheet(sheetName).registerWriteHandler(new CellWriteHandler() {
            @Override
            public void afterCellDataConverted(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, WriteCellData<?> cellData, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
                String key = cellData.getStringValue();
                /**
                 * CFG_DATATABLE_SHOW列表字段配置信息
                 */
                BaseMap cfg = styleMap.get(key);
                if (cfg == null){
                    // 表头信息
                    Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
                    CellStyle cellStyle = workbook.createCellStyle();
                    //单元格边框
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    cellStyle.setBorderTop(BorderStyle.THIN);

                    //背景色
                    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                    //对齐方式
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);

                    Font cellFont = workbook.createFont();
                    //加粗
                    cellFont.setBold(true);
                    //字体
                    cellFont.setFontName("微软雅黑");
                    //字体大小
                    cellFont.setFontHeightInPoints((short) 11);
                    cellStyle.setFont(cellFont);

                    cell.setCellStyle(cellStyle);
                }else{
                    Integer width = cfg.getInteger("maxLen"); // 单元格宽度
                    String dataType = cfg.getString("dataType"); // 设置数据类型
                    String cloAlign = cfg.getString("colAlign"); // 水平对齐方式

                    Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
                    CellStyle cellStyle = workbook.createCellStyle();
                    DataFormat df = workbook.createDataFormat();

                    //单元格边框
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    cellStyle.setBorderTop(BorderStyle.THIN);

                    //对齐方式
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);

                    Font cellFont = workbook.createFont();
                    //字体
                    cellFont.setFontName("微软雅黑");
                    //字体大小
                    cellFont.setFontHeightInPoints((short) 11);
                    cellStyle.setFont(cellFont);

                    if (width != null){
                        // 设置宽度与分辨率比例计算,34倍正好
                        writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), width*34);
                    }

                    if (StringUtil.isNotEmpty(dataType) || StringUtil.isNotEmpty(cloAlign)){
                        // 自定义设置单元格格式
                        // 数据类型设置 rate、amount、date
                        // 水平对齐方式设置
                        cellStyle = setCellStyleSelf(cfg, dataType, cloAlign, cellStyle, df);
                    }
                    cell.setCellStyle(cellStyle);
                }
                CellWriteHandler.super.afterCellDataConverted(writeSheetHolder, writeTableHolder, cellData, cell, head, relativeRowIndex, isHead);
            }
        }).doWrite(list);

    }

    /**
     * 自定义设置单元格格式
     * dataType "rate|amount|date", // 数据类型, rate-利率型，amount - 金额，date-日期
     * format: "yyyy-MM-dd|yyyy-MM-dd HH:mm:ss|...", // 日期格式
     * unit: "1|2|3", //金额类型：1 - 元, 2 - 万元, 3 - 亿元
     * precision: 2|3|4... // 利率型保留小数位，整数
     * @param cfg
     */
    private static CellStyle setCellStyleSelf(BaseMap cfg, String dataType, String cloAlign, CellStyle cellStyle, DataFormat df) {
        try {

            if (StringUtil.isNotEmpty(dataType)) {
                if (StringUtil.equals("rate", dataType)) {
                    // 利率型
                    if (StringUtil.equals("2", cfg.getString("precision"))) {
                        cellStyle.setDataFormat(df.getFormat("0.00"));
                    } else if (StringUtil.equals("3", cfg.getString("precision"))) {
                        cellStyle.setDataFormat(df.getFormat("0.000"));
                    } else if (StringUtil.equals("4", cfg.getString("precision"))) {
                        cellStyle.setDataFormat(df.getFormat("0.0000"));
                    } else { // 默认保留两位
                        cellStyle.setDataFormat(df.getFormat("0.00"));
                    }
                } else if (StringUtil.equals("amount", dataType)) {
                    // 金额
                    if (StringUtil.equals("1", cfg.getString("precision"))) {
                        cellStyle.setDataFormat(df.getFormat("#,##0"));
                    }else if (StringUtil.equals("2", cfg.getString("precision"))) {
                        cellStyle.setDataFormat(df.getFormat("#,##0.00"));
                    } else if (StringUtil.equals("3", cfg.getString("precision"))) {
                        cellStyle.setDataFormat(df.getFormat("#,##0.000"));
                    } else if (StringUtil.equals("4", cfg.getString("precision"))) {
                        cellStyle.setDataFormat(df.getFormat("#,##0.0000"));
                    } else { // 默认保留两位
                        cellStyle.setDataFormat(df.getFormat("#,##0.00"));
                    }
                } else if (StringUtil.equals("date", dataType)) {
                    // 日期
                    cellStyle.setDataFormat(df.getFormat(cfg.getString("format")));
                }
            }

            // 设置对齐方式
            if (StringUtil.isNotEmpty(cloAlign)) {
                if (StringUtil.equals("left", cloAlign)) {
                    HorizontalAlignment align = HorizontalAlignment.LEFT;
                    cellStyle.setAlignment(align);
                } else if (StringUtil.equals("right", cloAlign)) {
                    HorizontalAlignment align = HorizontalAlignment.RIGHT;
                    cellStyle.setAlignment(align);
                } else if (StringUtil.equals("center", cloAlign)) {
                    HorizontalAlignment align = HorizontalAlignment.CENTER;
                    cellStyle.setAlignment(align);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return cellStyle;
    }

    public static <T> void exportAllData(File excelFile,String sheetName,List<ExcelColumn> columns,List<T> data, Map<String, BaseMap> styleMap,File excelTemplateFile) {
        if(null==excelTemplateFile){
            excelTemplateFile = new File(excelFile.getParentFile(),Thread.currentThread().getName()+excelFile.getName());
            if(StringUtil.isNotEmpty(sheetName)&&sheetName.length()>31){
                sheetName = sheetName.substring(0,30);
            }
            createExcelTemplate(excelTemplateFile, sheetName, columns, styleMap);
        }
        EasyExcel.write(excelFile).withTemplate(excelTemplateFile).sheet(sheetName)
                .registerConverter(new Converter<Date>() {
                    @Override
                    public Class<?> supportJavaTypeKey() {
                        return Date.class;
                    }

                    @Override
                    public Date convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
                        return Converter.super.convertToJavaData(cellData, contentProperty, globalConfiguration);
                    }

                    @Override
                    public WriteCellData<?> convertToExcelData(Date value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
                        return new WriteCellData<>(DateUtil.format(value));
                    }
                }).doFill(data);
    }

    public static class DataAppendUtil<T> {

        private File excelTemplateFile;
        private File excelFile;
        private String sheetName;
        private ExcelWriter excelWriter;
        private WriteSheet writeSheet;

        public DataAppendUtil(File excelTemplateFile, File excelFile, String sheetName) {
            this.excelTemplateFile = excelTemplateFile;
            this.excelFile = excelFile;
            this.sheetName = sheetName;
            this.excelWriter = EasyExcel.write(excelFile).withTemplate(excelTemplateFile).build();
            this.writeSheet = EasyExcel.writerSheet(sheetName).build();
        }

        public void append(List<T> data) {
            excelWriter.fill(data, writeSheet);
        }

        public void close() {
            if (null!=excelWriter) {
                excelWriter.finish();
            }
        }

        public File getExcelTemplateFile() {
            return excelTemplateFile;
        }

        public void setExcelTemplateFile(File excelTemplateFile) {
            this.excelTemplateFile = excelTemplateFile;
        }

        public File getExcelFile() {
            return excelFile;
        }

        public void setExcelFile(File excelFile) {
            this.excelFile = excelFile;
        }

        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public ExcelWriter getExcelWriter() {
            return excelWriter;
        }

        public void setExcelWriter(ExcelWriter excelWriter) {
            this.excelWriter = excelWriter;
        }

        public WriteSheet getWriteSheet() {
            return writeSheet;
        }

        public void setWriteSheet(WriteSheet writeSheet) {
            this.writeSheet = writeSheet;
        }

    }

    @ApiModel(value="Excel导出指定的列",description="Excel导出指定的列")
    public static class ExcelColumn{

        @ApiModelProperty(required = false, name="code",value="列的字段名",example="portfolioName")
        private String code;

        @ApiModelProperty(required = false, name="name",value="列的中文名",example="组合名称")
        private String name;

        public ExcelColumn() {
            super();
        }

        public ExcelColumn(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static byte[] File2byte(File tradeFile){
        byte[] buffer = null;
        try
        {
            FileInputStream fis = new FileInputStream(tradeFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer;
    }

    public static class Data{
        private long id;
        private String name;
        private BigDecimal number;
        private Date createTime;

        public Data(long id, String name, BigDecimal number, Date createTime) {
            this.id = id;
            this.name = name;
            this.number = number;
            this.createTime = createTime;
        }
        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Date getCreateTime() {
            return createTime;
        }
        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }
        public BigDecimal getNumber() {
            return number;
        }
        public void setNumber(BigDecimal number) {
            this.number = number;
        }
    }

    public static void main(String[] args) {
        String temp = "兆尹/n科技";
        System.out.println(temp);
    }

    public static String getTplFile(String templateFile) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] res = resolver.getResources("classpath*:tpl/" + templateFile);
            return res[0].getURL().getFile();
        } catch (IOException var3) {
            return null;
        }
    }

    private static void copyFileUsingFileStreams(String sourceName, File dest){
        InputStream input = null;
        OutputStream output = null;
        try {
            File file = new File(ExcelTplUitl.getTplFile(sourceName));
           if(file.exists()){
                input = new FileInputStream(file);
            }
            else{
                input = ExcelTplUitl.class.getResourceAsStream("/tpl/" + sourceName);
            }
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } catch (IOException e){}
        finally {
            try{input.close();
                output.close();
            }
            catch (IOException e){}
        }
    }
}



