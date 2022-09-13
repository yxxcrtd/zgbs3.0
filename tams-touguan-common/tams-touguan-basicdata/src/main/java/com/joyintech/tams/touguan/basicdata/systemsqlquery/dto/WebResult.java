package com.joyintech.tams.touguan.basicdata.systemsqlquery.dto;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.grid.GridRowDto;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
/**
 * 返回结果类 web 返回结果的定义类
 * 
 * @author 何阳阳
 * @version 1.0 2016年10月19日
 * @see WebResult
 * @since 1.0
 */
public class WebResult implements java.io.Serializable {

    /**
     * 开启feild排序
     */
    private boolean sortable = false;
    
    public static final String EMPTY = "";

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 成功时的状态码
     */
    public static final String STATUS_200 = "200";

    /**
     * 发生异常时的状态码
     */
    public static final String STATUS_500 = "500";

    /**
     * 状态码，默认为200
     */
    private String statuCode = STATUS_200;

    /**
     * 消息
     */
    private String message;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 数据对象
     */
    private Object data;

    /**
     * 验证出错的消息列表
     */
    private List<ValidMessage> errorList = new ArrayList<ValidMessage>();

    /**
     * 取得statuCode的值
     *
     * @return statuCode值.
     */
    public String getStatuCode() {
        return statuCode;
    }

    /**
     * 设定statuCode的值
     *
     * @param statuCode 设定值
     */
    public void setStatuCode(String statuCode) {
        this.statuCode = statuCode;
    }

    public WebResult() {
        this.statuCode = STATUS_200;
    }

    public WebResult(String message) {
        super();
        this.statuCode = STATUS_200;
        this.message = message;
        this.setMessageType(MessageType.提示消息);
    }

    public WebResult(String message, MessageType messageType) {
        super();
        this.statuCode = STATUS_200;
        this.message = message;
        this.setMessageType(messageType);
        if (MessageType.错误消息.equals(messageType)) {
            this.statuCode = STATUS_500;
        }
    }

    public WebResult(BindingResult result) {
        super();
        this.statuCode = STATUS_200;
        if (null != result && !result.getAllErrors().isEmpty()) {
            for (ObjectError error : result.getAllErrors()) {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError)error;
                    String field = fieldError.getField();

                    ValidMessage validMessage = new ValidMessage(fieldError.getField(),
                        fieldError.getDefaultMessage());
                    // 判断是否为list属性
                    if (StringUtil.containsAny(field, '[', ']')) {
                        Object value = result.getFieldValue(this.splitTwo(field, ".")[0]);
                        validMessage.setField(field.substring(0, field.indexOf("[")));
                        validMessage.setColumn(field.substring(field.indexOf("]") + 2));
                        if (value instanceof GridRowDto) {
                            GridRowDto rowData = (GridRowDto)value;
                            validMessage.setRowId(rowData.getRowId());
                            validMessage.setOrder(rowData.getOrder());
                            validMessage.setMessage(SpringContextUtil.getTextValue("VAL_COMMON_E_0041",
                                "tableName", "rowIndex") + validMessage.getMessage());
                        }
                        else {
                            String index = field.substring(field.indexOf("[")+1, field.indexOf("]"));
                            validMessage.setMessage(SpringContextUtil.getTextValue("VAL_COMMON_E_0041",
                                "tableName", String.valueOf(StringUtil.toInt(index) + 1)) + validMessage.getMessage());
                            validMessage.setOrder(StringUtil.toInt(index));
                        }
                    }
                    this.errorList.add(validMessage);
                }
                else {
                    this.errorList.add(new ValidMessage(error.getDefaultMessage()));
                }
            }
            this.statuCode = STATUS_500;
            this.setMessageType(MessageType.错误消息);
            // 消息排序
            sortErrorList(result.getTarget().getClass());
        }
    }
    /**
     * 将字符串根据指定字符串分隔为两个字符串
     *
     * @param old 源字符串
     * @param splitor 分隔符
     * @return 新的字符串数组
     */
    public  String[] splitTwo(String old, String splitor) {
        String[] result = new String[2];
        if (isNotEmpty(old)) {
            int index = old.indexOf(splitor);
            if (index > -1) {
                result[0] = old.substring(0, index);
                result[1] = old.substring(index + splitor.length());
            }
            else {
                result[0] = old;
                result[1] = EMPTY;
            }
        }
        return result;
    }
    /**
     * 判断对象是否为空
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNotEmpty(Object str) {
        boolean flag = true;
        if (str != null && !str.equals("")) {
            if (str.toString().length() > 0) {
                flag = true;
            }
        }
        else {
            flag = false;
        }
        return flag;
    }
    /**
     * 取得message的值
     *
     * @return message值.
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设定message的值
     *
     * @param message 设定值
     */
    public void setMessage(String message) {
        this.message = message;
        this.setMessageType(MessageType.提示消息);
        this.statuCode = STATUS_200;
    }

    /**
     * 设定message的值
     *
     * @param message 设定值
     * @param messageType 消息类型
     */
    public void setMessage(String message, MessageType messageType) {
        this.message = message;
        this.setMessageType(messageType);
        if (MessageType.错误消息.equals(messageType)) {
            this.statuCode = STATUS_500;
        }
    }

    /**
     * 获取 errorList
     * 
     * @return errorList.
     */
    public List<ValidMessage> getErrorList() {
        return errorList;
    }

    /**
     * 设置 errorList
     * 
     * @param errorList errorList
     */
    public void setErrorList(List<ValidMessage> errorList) {
        this.errorList = errorList;
        resetStatuCode();
    }

    /**
     * 获取 data
     * 
     * @return data.
     */
    public Object getData() {
        return data;
    }

    /**
     * 设置 data
     * 
     * @param data data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 获取 messageType
     * 
     * @return messageType.
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * 设置 messageType
     * 
     * @param messageType messageType
     */
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType.getCode();
    }

    /**
     * 添加验证消息
     * 
     * @param validMessage 验证消息
     * @return WebResult
     */
    public WebResult appendMessage(ValidMessage validMessage) {
        this.errorList.add(validMessage);
        resetStatuCode();
        return this;
    }

    /**
     * 添加验证消息
     * 
     * @param validMessageList 验证消息列表
     * @return WebResult
     */
    public WebResult appendMessageList(List<ValidMessage> validMessageList) {
        this.errorList.addAll(validMessageList);
        resetStatuCode();
        return this;
    }

    /**
     * errorList变更后重新设置状态码和消息类型
     * 
     * @see
     * @since 1.0
     */
    public void resetStatuCode() {
        if (!StringUtil.isEmptyList(this.errorList)) {
            this.statuCode = STATUS_500;
            this.setMessageType(MessageType.错误消息);
        }
        else {
            this.statuCode = STATUS_200;
            this.setMessageType(MessageType.提示消息);
        }
    }

    /**
     * 获取 sortable
     * 
     * @return sortable.
     */
    public boolean isSortable() {
        return sortable;
    }

    /**
     * 设置 sortable
     * 
     * @param sortable sortable
     */
    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    /**
     * 获取对象的所有属性
     * 
     * @param cls Class
     * @return 属性列表
     * @throws ClassNotFoundException 异常
     * @see
     * @since 1.0
     */
    private List<String> getFieldName(Class<?> cls)
        throws ClassNotFoundException {
        List<String> fieldList = new ArrayList<>();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            fieldList.add(field.getName());
            String genericTypeName = field.getType().getName();
            if (StringUtil.equalsIgnoreCase(genericTypeName, "java.util.List")) {
                ParameterizedType type = (ParameterizedType)field.getGenericType();
                Type subType = type.getActualTypeArguments()[0];
                fieldList.addAll(getFieldName(Class.forName(subType.getTypeName())));
            }
            else if (StringUtil.contains(genericTypeName, "com.joyin")) {
                fieldList.addAll(getFieldName(Class.forName(genericTypeName)));
            }
        }
        return fieldList;
    }

    /**
     * 错误消息排序
     * 
     * @param cls 验证对象
     */
    private void sortErrorList(Class<?> cls) {
        if (sortable && !errorList.isEmpty()) {
            try {
                List<String> fields = getFieldName(cls);
                Collections.sort(errorList, new Comparator<ValidMessage>() {

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public int compare(ValidMessage o1, ValidMessage o2) {
                        return fields.indexOf(o1.getField()) - fields.indexOf(o2.getField());
                    }
                });
            }
            catch (ClassNotFoundException e) {
            }
            for (int i = 0; i < errorList.size(); i++ ) {
                ValidMessage message = errorList.get(i);
                if (null != message) {
                    message.setOrder((i + 1));
                }
            }
        }
    }
}
