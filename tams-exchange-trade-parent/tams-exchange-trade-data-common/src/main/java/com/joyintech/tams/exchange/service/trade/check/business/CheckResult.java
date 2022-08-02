package com.joyintech.tams.exchange.service.trade.check.business;

import java.io.Serializable;
import java.util.Map;

/**
 * 检查结果
 *
 * @author lifangpeng
 * @date 2022/4/2 10:10
 */
public class CheckResult implements Serializable {
    private static final long serialVersionUID = 6616307751788414626L;

    private int code;
    private String msg;
    private Map<String, Object> data;

    public CheckResult() {

    }

    public CheckResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CheckResult(int code, String msg, Map<String, Object> data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
