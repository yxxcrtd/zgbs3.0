package com.joyintech.tams.exchange.common.bean.dto;

import com.joyintech.tams.exchange.common.enums.IEnum;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author swwan
 * @date 2022/4/14 - 14:39
 */
public class OpenCloseTime {

    /**
     * 申报价格范围类型
     */
    @ApiModelProperty(value="申报价格范围类型")
    private String priceTimeType;

    /**
     * 开始时间
     */
    @ApiModelProperty(value="开始时间")
    private String openTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value="结束时间")
    private String closeTime;

    public OpenCloseTime() {
    }

    public OpenCloseTime(String priceTimeType, String openTime, String closeTime) {
        this.priceTimeType = priceTimeType;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public String getPriceTimeType() {
        return priceTimeType;
    }

    public void setPriceTimeType(String priceTimeType) {
        this.priceTimeType = priceTimeType;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    @Override
    public String toString() {
        return "OpenCloseTime{" +
                "priceTimeType='" + priceTimeType + '\'' +
                ", openTime='" + openTime + '\'' +
                ", closeTime='" + closeTime + '\'' +
                '}';
    }

    /**
     * 价格申报范围类型枚举
     */
    public enum PriceTimeTypeEnum implements IEnum<String> {
        OPEN("01", "开盘集合竞价"),
        KEEP("02", "连续竞价"),
        CLOSE("03", "收盘集合竞价"),
        STOP("04", "开市停牌阶段"),
        ;

        private final String code;
        private final String description;


        PriceTimeTypeEnum(String code, String description) {
            this.code = code;
            this.description = description;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}
