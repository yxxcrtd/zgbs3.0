package com.joyintech.tams.invest.common.util;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * excel操作
 *
 * @author zhangnian
 * @version 1.0 2019年07月30日
 * @see ExcelUtilForRisk
 * @since 1.0
 */
public class ExcelUtilForRisk {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtilForRisk.class);

    /**
     * 去。0的格式化
     */
    public static final DecimalFormat POINT = new DecimalFormat("###################.###########");

    /**
     * Excel文件到List
     *
     * @param fileName 文件名
     * @param file 文件
     * @param inputStream 文件流
     * @param sheetIndex 工作表索引(下标从0开始)
     * @param skipRows 跳过的表头(行数)
     * @param titleRow 表头序号(下标从0开始)
     * @return 读取结果列表
     * @throws FileNotFoundException
     */
    @SuppressWarnings("deprecation")
    public static List<List<String>> readABSToList(String fileName, File file,
                                                   InputStream inputStream, int sheetIndex,
                                                   int skipRows, int titleRow) throws FileNotFoundException{
        List<List<String>> ls = new ArrayList<List<String>>();
        Workbook wb = ExcelUtil.loadWorkbook(fileName, file, inputStream);
        if (null != wb) {
            Sheet sh = wb.getSheetAt(sheetIndex);
            int rows = sh.getPhysicalNumberOfRows();
            int cells = sh.getRow(titleRow).getPhysicalNumberOfCells();
            Row tRow = sh.getRow(titleRow);
            // 列标题为空的话结束
/*            for (int c = 0; c < cells; c++ ) {
                if (tRow.getCell(c) == null
                    || StringUtil.isEmpty(getStringCellValue(tRow.getCell(c)))) {
                    cells = c;
                    break;
                }
            }*/

            for (int i = skipRows; i < rows; i++ ) {
                Row row = sh.getRow(i);
                if (row == null) {
                    break;
                }
                List<String> r = new ArrayList<String>(cells);
                boolean dataFlag = true;
                for (int c = 0; c < cells; c++ ) {
                    if (row.getCell(c) == null
                            || row.getCell(c).getCellTypeEnum() == CellType.BLANK) {
                        r.add("");
                    }
                    else {
                        dataFlag = false;
                        r.add(getStringCellValue(row.getCell(c)));
                    }
                }
                // 遇到一行全为空结束
                if (dataFlag) {
                    break;
                }
                ls.add(r);
            }
        }
        return ls;
    }

    /**
     * Excel文件到List
     *
     * @param fileName 文件名
     * @param file 文件
     * @param inputStream 文件流
     * @param sheetIndex 工作表索引(下标从0开始)
     * @param skipRows 跳过的表头(行数)
     * @param titleRow 表头序号(下标从0开始)
     * @return 读取结果列表
     * @throws FileNotFoundException
     */
    @SuppressWarnings("deprecation")
    public static List<List<String>> readToList(String fileName, File file,
                                                InputStream inputStream, int sheetIndex,
                                                int skipRows, int titleRow) throws FileNotFoundException{
        List<List<String>> ls = new ArrayList<List<String>>();
        Workbook wb = ExcelUtil.loadWorkbook(fileName, file, inputStream);
        if (null != wb) {
            Sheet sh = wb.getSheetAt(sheetIndex);
            int rows = sh.getPhysicalNumberOfRows();
            int cells = sh.getRow(titleRow).getPhysicalNumberOfCells();
            Row tRow = sh.getRow(titleRow);
            // 列标题为空的话结束
            for (int c = 0; c < cells; c++ ) {
                if (tRow.getCell(c) == null
                        || StringUtil.isEmpty(getStringCellValue(tRow.getCell(c)))) {
                    cells = c;
                    break;
                }
            }

            for (int i = skipRows; i < rows; i++ ) {
                Row row = sh.getRow(i);
                if (row == null) {
                    break;
                }
                List<String> r = new ArrayList<String>(cells);
                boolean dataFlag = true;
                for (int c = 0; c < cells; c++ ) {
                    if (row.getCell(c) == null
                            || row.getCell(c).getCellTypeEnum() == CellType.BLANK) {
                        r.add("");
                    }
                    else {
                        dataFlag = false;
                        r.add(getStringCellValue(row.getCell(c)));
                    }
                }
                // 遇到一行全为空结束
                if (dataFlag) {
                    break;
                }
                ls.add(r);
            }
        }
        return ls;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param cell 单元格
     * @return 值
     * @see
     * @since 1.0
     */
    @SuppressWarnings("deprecation")
    private static String getStringCellValue(Cell cell) {
        String value = null;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC: // 数字
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = com.joyintech.framework.util.DateUtil.format(cell.getDateCellValue(),
                            com.joyintech.framework.util.DateUtil.DEFAULT_DATE_FORMAT_EN);
                    return value;
                }
                value = POINT.format(cell.getNumericCellValue());
                break;
            case STRING: // 字符串
                value = cell.getStringCellValue();
                break;
            case FORMULA: // 公式
                cell.setCellType(CellType.STRING);
                value = cell.getStringCellValue();
                break;
            case BLANK: // 空值
                break;
            case BOOLEAN: // Boolean
                break;
            case ERROR: // 故障
                break;
            default:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = DateUtil.format(cell.getDateCellValue());
                }
                break;
        }

        return value;
    }

    /**
     * 将Excel表格中列号字母转成列索引，从1对应A开始
     *
     * @param column 列号字母
     * @return 列索引
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public static int columnToIndex(String column){
        if (!column.matches("[A-Z]+")) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        int index = 0;
        char[] chars = column.toUpperCase().toCharArray();
        for (int i = 0; i < chars.length; i++ ) {
            index += (chars[i] - 'A' + 1) * (int)Math.pow(26, chars.length - i - (double)1);
        }
        return index;
    }


    /**
     * 将excel表格中列索引转成列号字母，从A对应1开始
     *
     * @param index 列索引
     * @return 列号字母
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public static String indexToColumn(int index){
        if (index <= 0) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        index-- ;
        String column = "";
        do {
            if (column.length() > 0) {
                index-- ;
            }
            column = ((char)(index % 26 + 'A')) + column;
            index = (index - index % 26) / 26;
        }
        while (index > 0);
        return column;
    }

    /**
     * 获取列表数据
     * @param fileName 文件路径
     * @return 列表数据
     */
    public static List<List<String>> getABSTempList(String fileName){
        List<List<String>> dataList = null;
        try {
            dataList = readABSToList(
                    FileNameUtil.getUploadFilePath(fileName), null, null, 0, 0, 0);
        } catch (FileNotFoundException e1) {
            logger.error(e1.getMessage(),e1);
        }
        return dataList;
    }


    /**
     * 获取列表数据
     * @param fileName 文件路径
     * @return 列表数据
     */
    public static List<List<String>> getABSDataList(String fileName){
        List<List<String>> dataList = null;
        try {
            dataList = readABSToList(
                    FileNameUtil.getUploadFilePath(fileName), null, null, 0, 1, 0);
        } catch (FileNotFoundException e1) {
            logger.error(e1.getMessage(),e1);
        }
        return dataList;
    }

    /**
     * 获取列表数据
     * @param fileName 文件路径
     * @return 列表数据
     */
    public static List<List<String>> getTempList(String fileName){
        List<List<String>> dataList = null;
        try {
            dataList = readToList(
                    FileNameUtil.getUploadFilePath(fileName), null, null, 0, 0, 0);
        } catch (FileNotFoundException e1) {
            logger.error(e1.getMessage(),e1);
        }
        return dataList;
    }


    /**
     * 获取列表数据
     * @param fileName 文件路径
     * @return 列表数据
     */
    public static List<List<String>> getDataList(String fileName){
        List<List<String>> dataList = null;
        try {
            dataList = readToList(
                    FileNameUtil.getUploadFilePath(fileName), null, null, 0, 1, 0);
        } catch (FileNotFoundException e1) {
            logger.error(e1.getMessage(),e1);
        }
        return dataList;
    }
	
    /**
     * 修改文件名
     * wangyang1
     * @param filePath    需要修改的文件的路径
     * @param oldFileName 的文件的名称
     * @param newFileName 需要修改的文件的名称
     * @return
     */
    public static boolean fixFileName(String filePath,String oldFileName, String newFileName) {
        File file = new File(filePath+File.separator+oldFileName);
        if (!file.exists()) { // 判断原文件是否存在（防止文件名冲突）
            return false;
        }
        newFileName = newFileName.trim();
        if ("".equals(newFileName) || newFileName == null) // 文件名不能为空
            return false;
        String newFilePath = filePath + File.separator + newFileName;
        File newfile = new File(newFilePath);
        try {
            if (newfile.exists()){
                if(!newfile.delete()){}
            }
            if(file.renameTo(newfile)){} // 修改文件名
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
        return true;
    }
}
