package com.joyintech.tams.business.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

public class ExportCsvUtil {

    /** CSV文件列分隔符 */
    private static final String CSV_COLUMN_SEPARATOR = ",";

    /** CSV文件列分隔符 */
    private static final String CSV_RN = "\r\n";
    
    /**
     * 导出csv
     * @param dataList
     * @return
     */
    public static void doExportCsv(String fileName, List<Map> heads, List<Map> dataList, HttpServletResponse response) {
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            // 读取字符编码
//            String fileName = "测试导出";
            String fn = fileName + ".csv";

            String name = URLEncoder.encode(fn, "UTF-8");
            System.out.println(name);
            // 设置响应
            response.setContentType("text/csv;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "max-age=30");
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fn, "UTF-8"));

            StringBuffer buf = new StringBuffer();
            for (Map lineMap : heads) {
                buf.append(lineMap.get("name")).append(CSV_COLUMN_SEPARATOR);
            }
//            buf.append("电影名").append(CSV_COLUMN_SEPARATOR);
//            buf.append("导员").append(CSV_COLUMN_SEPARATOR);
//            buf.append("语言").append(CSV_COLUMN_SEPARATOR);
//            buf.append("开放时间").append(CSV_COLUMN_SEPARATOR);
            buf.append(CSV_RN);

//            if (null != dataList) { // 输出数据
//                for (int i = 0; i < dataList.size(); i++) {
//                    Map m = dataList.get(i);
//                    buf.append(m.getName()).append(CSV_COLUMN_SEPARATOR);
//                    buf.append(m.getDirector()).append(CSV_COLUMN_SEPARATOR);
//                    buf.append(m.getLanguage()).append(CSV_COLUMN_SEPARATOR);
//                    buf.append(m.getOpenday()).append(CSV_COLUMN_SEPARATOR);
//                    buf.append(CSV_RN);
//                }
//            }

            if (null != dataList) { // 输出数据
                for (int i = 0; i < dataList.size(); i++) {
                    Map m = dataList.get(i);
                    for (Map lineMap : heads) {
                        buf.append(m.get(lineMap.get("field"))).append(CSV_COLUMN_SEPARATOR);
                    }
//                    buf.append(m.getName()).append(CSV_COLUMN_SEPARATOR);
                    buf.append(CSV_RN);
                }
            }

            // 写出响应
            os.write(buf.toString().getBytes("gbk"));
            os.flush();
        } catch (Exception e) {
//                logger.error("doExport错误...", e);
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 从request中获得参数Map，并返回可读的Map
     *
     * @param request
     * @return
     */
    public static Map getParameterMap(HttpServletRequest request) {
        // 参数Map
        Map properties = request.getParameterMap();
        // 返回值Map
        Map returnMap = new HashMap();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if(null == valueObj){
                value = "";
            }else if(valueObj instanceof String[]){
                String[] values = (String[])valueObj;
                for(int i=0;i<values.length;i++){
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length()-1);
            }else{
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }
}
