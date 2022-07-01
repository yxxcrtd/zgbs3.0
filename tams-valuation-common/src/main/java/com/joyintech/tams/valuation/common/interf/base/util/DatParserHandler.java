package com.joyintech.tams.valuation.common.interf.base.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateColMatchDTO;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderDTO;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;

public class DatParserHandler {

	private static Logger logger = LoggerFactory.getLogger(DatParserHandler.class);
	 
//    Logger logger = Logger.getLogger(DefaultHandler.class);

    private Class<?> T;

    private ValInterflog checkLog;

    private List<Map<String, String>> colVluList;

    private Field[] fields;

    private Map<String, TemplateOrderInfoReaderDTO> check;

    private StringBuffer checkError;

    private TemplateOrderInfoReaderResult acctEntity;

    private StringBuffer info;

    private int bookIndex = 0;

    private List<ValInterflog> logResult;

    private List<String> colList;

    private List<Object> returnList;

    private Map<String, TemplateColMatchDTO> matchs;

    public boolean init(String _className, List<String> _dataList,
                        List<TemplateOrderInfoReaderDTO> _check,
                        TemplateOrderInfoReaderResult _acctEntity,
                        Map<String, TemplateColMatchDTO> _matchs,
                        Map<String,List<String>> _map) {
        if (_className == null || _className.isEmpty()) {
            return false;
        }
        try {
            T = Class.forName(_className);
            fields = this.T.getDeclaredFields();
            check = buildCheckIndex(_check);
            acctEntity = _acctEntity;
            colList = _map.get("colsList");
            colVluList = new ArrayList<>();
            List<String> lengthList = _map.get("lengthList");
            for (int i = 0; i < _dataList.size(); i++ ) {
                int begin = 0,count;
                byte [] b = _dataList.get(i).getBytes("GB2312");
                Map<String, String> dataMap = new HashMap<>();
                for (int j = 0; j < lengthList.size(); j++) {
                    if(j==0){
                        count=Integer.parseInt(lengthList.get(0));
                    }else{
                        count = Integer.parseInt(lengthList.get(j))-Integer.parseInt(lengthList.get(j-1));
                        begin = Integer.parseInt(lengthList.get(j-1));
                    }
                    dataMap.put(colList.get(j), new String(subBytes(b, begin, count),"GB2312").trim());
                }
                //提供的数据文件暂时缺少动作标志字段，此段代码前期测试使用
                if(!dataMap.containsKey("DATAACTIONSIGN")){
                    dataMap.put("DATAACTIONSIGN","01");
                }
                colVluList.add(dataMap);
            }
            //提供的数据文件暂时缺少动作标志字段，此段代码前期测试使用
            if(!colList.contains("DATAACTIONSIGN")){
                colList.add(3,"DATAACTIONSIGN");
            }
            returnList = new ArrayList<>();
            logResult = new ArrayList<>();
            matchs = _matchs;
        }
        catch (Exception e) {
            logger.error("",e);
        }
        return true;
    }

    public void txtParse() {
        // 模版中缺少字段，全部数据丢弃
        if (colList.size() < check.size()) {
            checkLog = new ValInterflog();
            checkError = new StringBuffer();
            info = new StringBuffer();
            // 模版中缺少字段，全部数据丢弃
            checkLog.setIsCheck(DictCodeList.是否_否.getValue());
            StringBuffer lostCol = new StringBuffer();
            for (Map.Entry<String, TemplateOrderInfoReaderDTO> entry : check.entrySet()) {
                if (!colList.contains(entry.getValue().getColEName().toUpperCase())) {
                    lostCol.append(entry.getValue().getColEName() + ";");
                }
            }
            checkError.append(
                    acctEntity.getTmpCName() + " 接口文件中缺少字段：" + lostCol + "，该文件所有数据将被丢弃，请联系管理员立即处理.");
            logger.error(
                    acctEntity.getTmpCName() + " 接口文件中缺少字段：" + lostCol + "，该文件所有数据将被丢弃，请联系管理员立即处理.");
            setCheckLog();
            logResult.add(checkLog);
        }
        else {
            logger.debug("SAX解析开始");
            for (int i = 0; i < colVluList.size(); i++ ) {
                checkLog = new ValInterflog();
                checkError = new StringBuffer();
                info = new StringBuffer();
                bookIndex++ ;
                checkLog.setIsCheck(DictCodeList.是否_是.getValue());
                Map<String, String> colVlu = colVluList.get(i);
                try {
                    Object result = T.newInstance();
                    for (Field f : fields) {
                        String tmpEName = f.getName();
                        if(matchs!=null && matchs.size()>0){
                            tmpEName = matchs.get(f.getName()).getTmpColEName();
                        }

                        if (colVlu.containsKey(tmpEName.toUpperCase())) {
                            try {
                                info.append(f.getName() + "-" + colVlu.get(tmpEName) + ";");
                                valueFilter(f, result, colVlu.get(tmpEName.toUpperCase()), tmpEName);
                            }
                            catch (IllegalArgumentException e) {
                                logger.error("",e);
                            }
                            catch (IllegalAccessException e) {
                                logger.error("",e);
                            }
                        }
                    }
                    
                    /**add by wujie on 20190301 start**/
                    setCheckLog();
                    logResult.add(checkLog);
                    /**add by wujie on 20190301 end**/
                    
                    if (DictCodeList.是否_是.getValue().equals(checkLog.getIsCheck())) {
                    	
                    	/**add by wujie on 20190301 start**/
            			InterfHelper.setValInterflogKey(result, InterfHelper.getInterfLogKey(checkLog));
            			/**add by wujie on 20190301 end**/
                    	
                        returnList.add(result);
                    }
                    /**modify by wujie on 20190301
                    setCheckLog();
                    logResult.add(checkLog);**/
                }
                catch (InstantiationException e) {
                    logger.error("",e);
                }
                catch (IllegalAccessException e) {
                    logger.error("",e);
                }

            }
            logger.debug("SAX解析结束,总共解析数据为：" + bookIndex);
        }
    }

    /**
     * 日志初始化
     */
    private void setCheckLog() {
        checkLog.setTmpName(acctEntity.getTmpEName());// 模板名称
        checkLog.setFileName(acctEntity.getTmpCName());// 文件名称
        checkLog.setExDate(new Date());// 日期
        checkLog.setParentSeqno(acctEntity.getParentSeqno());// 主副日志表连接键
        checkLog.setDetail(info.toString());
        checkLog.setNum((long)bookIndex);
        checkLog.setRemark(checkError.toString());
    }

    /**
     *
     * 数据过滤
     * @param _f Dto内部变量
     * @param _obj 返回值
     * @param _v 解析值
     * @param _col 模板字段英文名
     */
    private void valueFilter(Field _f, Object _obj, String _v, String _col)
            throws IllegalArgumentException, IllegalAccessException {
        _f.setAccessible(true);
        Class<?> t = _f.getType();
        if (t.toString().contains("java.util.Date")) {
            Date _d = checkDateValue(_v, _col);
            _f.set(_obj, _d);
        }
        else if (t.toString().contains("java.math.BigDecimal")) {
            BigDecimal _b = checkBigValue(_v, _col);
            _f.set(_obj, _b);
        }
        else if (t.toString().contains("java.lang.Integer")) {
            Integer _i = checkIntValue(_v, _col);
            _f.set(_obj, _i);
        }
        else if (t.toString().contains("java.lang.Long")) {
            Long _l = checkLonValue(_v, _col);
            _f.set(_obj, _l);
        }
        else if (t.toString().contains("java.lang.String")) {
            _v = checkStrValue(_v, _col);
            _f.set(_obj, _v);
        }
        else {
            logger.warn("字段类型无法识别，请联系系统管理员.");
        }

    }

    /**
     * Date类型数据校验
     *
     * @param _v
     * @param colName
     * @return
     */
    private Date checkDateValue(String _v, String colName) {
        Date l = null;
        if (check.containsKey(colName)) {
            TemplateOrderInfoReaderDTO _c = check.get(colName);
            // 非空检查
            if (DictCodeList.是否_是.getValue().equals(_c.getIsNull())) {
                if (_v == null || _v.isEmpty()) {
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName + "：" + "字段不可为空;");
                    logger.warn(colName + "：" + "字段不可为空;");
                    return null;
                }
            }
            // 长度检查
            if (_v != null && !_v.isEmpty()) {
                BigDecimal _len = new BigDecimal(_v.length() + 1);
                if (_len.compareTo(_c.getMaxLength()) > 0) {
                    if (_v == null || _v.isEmpty()) {
                        checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                        checkError.append(
                                colName + "：字段长度为" + _len + "超过指定长度" + _c.getMaxLength() + ";");
                        logger.warn(
                                colName + "：字段长度为" + _len + "超过指定长度" + _c.getMaxLength() + ";");
                        return null;
                    }
                }
            }
            // 格式检查
            try {
                if (_v == null || _v.trim().isEmpty()) {
                    l = null;
                }
                else {
                    l = toDate(_v);
                }
            }
            catch (Exception e) {
                checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                checkError.append(colName + "：Date数值错误——" + _v + ";");
                logger.warn(colName + "：Date数值错误——" + _v + ";");
            }
        }
        else {
            checkLog.setIsCheck(DictCodeList.是否_否.getValue());
            checkError.append(colName + "：字段不存在校验规则，需要联系管理员进行配置.");
            logger.warn(colName + "：字段不存在校验规则，需要联系管理员进行配置.");
            return null;
        }
        return l;
    }

    /**
     * BigDecimal数据校验
     *
     * @param _v
     * @param colName
     * @return
     */
    private BigDecimal checkBigValue(String _v, String colName) {
        BigDecimal l = null;
        if (check.containsKey(colName)) {
            TemplateOrderInfoReaderDTO _c = check.get(colName);
            // 非空检查
            if (DictCodeList.是否_是.getValue().equals(_c.getIsNull())) {
                if (_v == null || _v.isEmpty()) {
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName + "：" + "字段不可为空;");
                    logger.warn(colName + "：" + "字段不可为空;");
                    return null;
                }
            }
            // 长度检查
            if (_v != null && !_v.isEmpty()) {
                BigDecimal _len = new BigDecimal(_v.length() + 1);
                if (_len.compareTo(_c.getMaxLength()) > 0) {
                    if (_v == null || _v.isEmpty()) {
                        checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                        checkError.append(
                                colName + "：字段长度为" + _len + "超过指定长度" + _c.getMaxLength() + ";");
                        logger.warn(
                                colName + "：字段长度为" + _len + "超过指定长度" + _c.getMaxLength() + ";");
                        return null;
                    }
                }
            }
            // 格式检查
            try {
                if (_v == null || _v.trim().isEmpty()) {
                    l = null;
                }
                else {
                    l = new BigDecimal(_v);
                }
            }
            catch (Exception e) {
                checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                checkError.append(colName + "：BigDecimal数值错误——" + _v + ";");
                logger.warn(colName + "：BigDecimal数值错误——" + _v + ";");
            }
        }
        else {
            checkLog.setIsCheck(DictCodeList.是否_否.getValue());
            checkError.append(colName + "：字段不存在校验规则，需要联系管理员进行配置.");
            logger.warn(colName + "：字段不存在校验规则，需要联系管理员进行配置.");
            return null;
        }
        return l;
    }

    /**
     * Integer数据校验
     *
     * @param _v
     * @param colName
     * @return
     */
    private Integer checkIntValue(String _v, String colName) {
        Integer l = null;
        if (check.containsKey(colName)) {
            TemplateOrderInfoReaderDTO _c = check.get(colName);
            // 非空检查
            if (DictCodeList.是否_是.getValue().equals(_c.getIsNull())) {
                if (_v == null || _v.isEmpty()) {
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName + "：" + "字段不可为空;");
                    logger.warn(colName + "：" + "字段不可为空;");
                    return null;
                }
            }
            // 长度检查
            if (_v != null && !_v.isEmpty()) {
                BigDecimal _len = new BigDecimal(_v.length() + 1);
                if (_len.compareTo(_c.getMaxLength()) > 0) {
                    if (_v == null || _v.isEmpty()) {
                        checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                        checkError.append(
                                colName + "：字段长度为" + _len + "超过指定长度" + _c.getMaxLength() + ";");
                        logger.warn(
                                colName + "：字段长度为" + _len + "超过指定长度" + _c.getMaxLength() + ";");
                        return null;
                    }
                }
            }
            // 格式检查
            try {
                if (_v == null || _v.trim().isEmpty()) {
                    l = null;
                }
                else {
                    l = new Integer(_v);
                }
            }
            catch (Exception e) {
                checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                checkError.append(colName + "：Integer数值错误——" + _v + ";");
                logger.warn(colName + "：Integer数值错误——" + _v + ";");
            }
        }
        else {
            checkLog.setIsCheck(DictCodeList.是否_否.getValue());
            checkError.append(colName + "：字段不存在校验规则，需要联系管理员进行配置.");
            logger.warn(colName + "：字段不存在校验规则，需要联系管理员进行配置.");
            return null;
        }
        return l;
    }

    /**
     * Long类型数据校验
     *
     * @param _v
     * @param colName
     * @return
     */
    private Long checkLonValue(String _v, String colName) {
        Long l = null;
        if (check.containsKey(colName)) {
            TemplateOrderInfoReaderDTO _c = check.get(colName);
            // 非空检查
            if (DictCodeList.是否_是.getValue().equals(_c.getIsNull())) {
                if (_v == null || _v.isEmpty()) {
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName + "：" + "字段不可为空;");
                    logger.warn(colName + "：" + "字段不可为空;");
                    return null;
                }
            }
            // 长度检查
            if (_v != null && !_v.isEmpty()) {
                BigDecimal _len = new BigDecimal(_v.length() + 1);
                if (_len.compareTo(_c.getMaxLength()) > 0) {
                    if (_v == null || _v.isEmpty()) {
                        checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                        checkError.append(
                                colName + "：字段长度为" + _len + "超过指定长度" + _c.getMaxLength() + ";");
                        logger.warn(
                                colName + "：字段长度为" + _len + "超过指定长度" + _c.getMaxLength() + ";");
                        return null;
                    }
                }
            }
            // 格式检查
            try {
                if (_v == null || _v.trim().isEmpty()) {
                    l = null;
                }
                else {
                    l = new Long(_v);
                }
            }
            catch (Exception e) {
                checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                checkError.append(colName + "：Long数值错误——" + _v + ";");
                logger.warn(colName + "：Long数值错误——" + _v + ";");
            }
        }
        else {
            checkLog.setIsCheck(DictCodeList.是否_否.getValue());
            checkError.append(colName + "：字段不存在校验规则，需要联系管理员进行配置.");
            logger.warn(colName + "：字段不存在校验规则，需要联系管理员进行配置.");
            return null;
        }
        return l;
    }

    /**
     * String类型数据校验
     *
     * @param _v
     * @param colName
     * @return
     */
    private String checkStrValue(String _v, String colName) {
        if (check != null && check.containsKey(colName)) {
            TemplateOrderInfoReaderDTO _c = check.get(colName);
            // 非空检查
            if (DictCodeList.是否_是.getValue().equals(_c.getIsNull())) {
                if (_v == null || _v.isEmpty()) {
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName + "：" + "字段不可为空;");
                    return null;
                }
            }
            // 长度检查
            if (_v != null && !_v.isEmpty()) {
                BigDecimal _len = new BigDecimal(_v.length() + 1);
                if (_len.compareTo(_c.getMaxLength()) > 0) {
                    if (_v == null || _v.isEmpty()) {
                        checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                        checkError.append(
                                colName + "：字段长度为" + _len + "超过指定长度" + _c.getMaxLength());
                        return null;
                    }
                }
            }
            /**add by dongjun on 20181105 start remark:数据字典验证**/
            if(_v!=null && !_v.isEmpty() && _v.trim().length()!=0){
                if(_c.getDictCode()!=null && !_c.getDictCode().isEmpty()){
                    int result = InterfHelper.isValiDateDict(_c.getDictCode(),_v);
                    if(result == 0){
                        checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                        checkError.append(colName+"：字段传值"+_v+"不在数据字典["+_c.getDictCode()+"]设置范围内.");
                        return null;
                    }
                    else if(result == -1){
                        checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                        checkError.append(colName+"：字段对应的数据字典["+_c.getDictCode()+"]不存在，需要联系管理员进行配置.");
                        return null;
                    }
                }
            }
            /**add by dongjun on 20181105 end**/
        }
        else {
            checkLog.setIsCheck(DictCodeList.是否_否.getValue());
            checkError.append(colName + "：字段不存在校验规则，需要联系管理员进行配置.");
            logger.warn(colName + "：字段不存在校验规则，需要联系管理员进行配置.");
            return null;
        }
        return _v;
    }

    /**
     * 构建校验信息字典
     */
    private Map<String, TemplateOrderInfoReaderDTO> buildCheckIndex(List<TemplateOrderInfoReaderDTO> _check) {
        Map<String, TemplateOrderInfoReaderDTO> checkIndex = new HashMap<String, TemplateOrderInfoReaderDTO>();
        if (_check != null && !_check.isEmpty()) {
            for (TemplateOrderInfoReaderDTO _c : _check) {
                checkIndex.put(_c.getColEName(), _c);
            }
        }
        return checkIndex;
    }

    /**
     * 字节数据读取
     * @param src   目标源byte数组
     * @param begin 下标起始位
     * @param count 个数
     * @return
     */
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    /**
     * 日期格式转换
     * @param dateTime  时间字符串
     * @return
     */
    public static Date toDate(String dateTime){
        if(StringUtils.isNotBlank(dateTime)){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime ldt = LocalDateTime.parse(dateTime,dtf);
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = ldt.atZone(zoneId);
            return Date.from(zdt.toInstant());
        }
        return null;
    }

    /**
     * 获取 returnList
     *
     * @return returnList.
     */
    public List<Object> getReturnList() {
        return returnList;
    }

    /**
     * 设置 returnList
     *
     * @param returnList returnList
     */
    public void setReturnList(List<Object> returnList) {
        this.returnList = returnList;
    }

    /**
     * 获取 logResult
     *
     * @return logResult.
     */
    public List<ValInterflog> getLogResult() {
        return logResult;
    }

    /**
     * 设置 logResult
     *
     * @param logResult logResult
     */
    public void setLogResult(List<ValInterflog> logResult) {
        this.logResult = logResult;
    }

}


