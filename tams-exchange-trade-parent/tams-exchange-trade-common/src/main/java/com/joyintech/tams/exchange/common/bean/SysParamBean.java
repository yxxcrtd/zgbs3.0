package com.joyintech.tams.exchange.common.bean;

import com.google.common.base.MoreObjects;

/**
 * @ClassName SysParamBean
 * @Description TODO
 * @Author swwan
 * @Date 2020/9/9 16:26
 * @Version 1.0
 **/
public class SysParamBean {
    private String paraCode;
    private String systemCode;

    public String getParaCode() {
        return paraCode;
    }

    public void setParaCode(String paraCode) {
        this.paraCode = paraCode;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public SysParamBean(String paraCode, String systemCode) {
        this.paraCode = paraCode;
        this.systemCode = systemCode;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("paraCode", paraCode)
                .add("systemCode", systemCode)
                .toString();
    }
}
