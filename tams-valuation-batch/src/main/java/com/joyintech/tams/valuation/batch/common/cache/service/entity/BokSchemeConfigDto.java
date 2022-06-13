package com.joyintech.tams.valuation.batch.common.cache.service.entity;

/**
 * @author 何阳阳
 * @date 2022/3/14
 */
public class BokSchemeConfigDto {
    /**
     * 配置项代码
     */
    private String configId;

    /**
     * 取数据字典值
     */
    private String dictCode;

    /**
     * 配置项值
     */
    private String configValue;

    /**
     * 账套代码
     */
    private String booksetId;

    /**
     *
     */
    private String schemeType;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getBooksetId() {
        return booksetId;
    }

    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }

    public String getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(String schemeType) {
        this.schemeType = schemeType;
    }
}
