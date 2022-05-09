package com.joyintech.service.bankuplink.common.dto;

import com.joyintech.framework.base.BaseEntity;

/**
 * 一级申购下行报文
 *
 * @author jiangpeng
 * @date 2022/5/7 11:40
 */
public class CfetsQuotedTradeFirstBondDto extends BaseEntity {

    /**
     * 报文类型
     */
    private String msgType;

    /**
     * 报文序列号
     */
    private String msgSeqNum;

    /**
     * 发送时间
     */
    private String sendingTime;

    /**
     * 报文最初发送方
     */
    private String onBehalfOfCompID;

    /**
     * 业务发生时间
     */
    private String transactTime;

    /**
     * 成交状态
     */
    private String execType;
    /*
     * 协议包版本号
     */
    private String beginString;
    /*
     * 报文体长度
     */
    private String bodyLength;
    /*
     * 报文类型
     */
    private String msgScqNum;
    /*
     * 发送方标识
     */
    private String senderCompID;
    /*
     * 接受方标识
     */
    private String targetCompID;
    /*
     * 报文发送时间
     */
    private String senderTime;
    /*
     * 最终接收方标识
     */
    private String deliverToCompID;
    /*
     * 最终接受方子标识
     */
    private String deliverToSubID;
    /*
     * 币种
     */
    private String currency;
    /*
     * 工具类型
     */
    private String securityType;
    /*
     * 工具全称
     */
    private String securityDefinitionType;
    /*
     * 工具全称
     */
    private String fullSymbol;
    /*
     * 工具简称
     */
    private String sybol;
    /*
     * 息票类型
     */
    private String couponRateType;
    /*
     * 发行方式
     */
    private String issueMethod;
    /*
     * 工具状态
     */
    private String securityStatus;
    /*
     * 发行价格
     */
    private String issuePx;
    /*
     * 计划发行量(亿元)
     */
    private String issueSizeForPlan;
    /*
     * 实际发行量(亿元)
     */
    private String issueSize;
    /*
     * 利差	浮息时传值
     */
    private String spread;
    /*
     * 票面利率	固息时传值
     */
    private String couponRate;
    /*
     * 协议包版本号
     */
    private String partyDetailAlt;

    /**
     * 工具代码  债券代码
     */
    private String securityID;

    /**
     * 债券名称
     */
    private String symbol;

    /**
     * 交易方向
     */
    private String side;

    /**
     * 失败原因
     */
    private String text;

}

