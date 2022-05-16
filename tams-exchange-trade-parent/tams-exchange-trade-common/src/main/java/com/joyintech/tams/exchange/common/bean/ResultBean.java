package com.joyintech.tams.exchange.common.bean;


import com.joyintech.tams.exchange.common.constant.ErrorCode;

public class ResultBean {
    private int errorCode;
    private String errorMsg;
    private Object data;

    public ResultBean() {

    }

    public ResultBean(ErrorCode errorCode) {
        this.errorCode = errorCode.value();
        this.errorMsg = errorCode.msg();
    }

    public ResultBean(ErrorCode errorCode, Object data) {
        this.errorCode = errorCode.value();
        this.errorMsg = errorCode.msg();
        this.data = data;
    }

    public ResultBean(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ResultBean(int errorCode, String errorMsg, Object data) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.data = data;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode.value();
        this.errorMsg = errorCode.msg();
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static ResultBean success() {
        return new ResultBean(ErrorCode.SUCCESS);
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static ResultBean success(Object data) {
        return new ResultBean(ErrorCode.SUCCESS, data);
    }

    /**
     * 返回警告消息
     *
     * @return 警告消息
     */
    public static ResultBean warn(ErrorCode errorCode, Object data) {
        return new ResultBean(errorCode, data);
    }

    public static ResultBean warn(ErrorCode errorCode) {
        return new ResultBean(errorCode);
    }

    public static ResultBean warn(int errorCode, String errorMsg) {
        return new ResultBean(errorCode, errorMsg);
    }

    public static ResultBean warn(int errorCode, String errorMsg, Object data) {
        return new ResultBean(errorCode, errorMsg, data);
    }
    
    /**
     * 返回错误消息
     *
     * @return 错误消息
     */
    public static ResultBean error(ErrorCode errorCode, Object data) {
        return new ResultBean(errorCode, data);
    }

    public static ResultBean error(ErrorCode errorCode) {
        return new ResultBean(errorCode);
    }

    public static ResultBean error(int errorCode, String errorMsg) {
        return new ResultBean(errorCode, errorMsg);
    }

    public static ResultBean error(int errorCode, String errorMsg, Object data) {
        return new ResultBean(errorCode, errorMsg, data);
    }

    public boolean isSuccess() {
        return this.getErrorCode() == ErrorCode.SUCCESS.getCode();
    }

    public boolean isFail() {
        return !isSuccess();
    }
}
