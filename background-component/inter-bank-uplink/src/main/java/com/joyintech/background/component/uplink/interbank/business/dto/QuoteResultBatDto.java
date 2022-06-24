/*
 * 文件名：resultDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：caoguo
 * 修改时间：2020年5月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.background.component.uplink.interbank.business.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

/**
 * 批量处理结果 
 * 
 * 
 * @author caoguo
 * @version 1.0 2020年5月7日
 * @see QuoteResultBatDto
 * @since 1.0
 */
public class QuoteResultBatDto {

    /**
     * 成功条数
     */
    private long successAccout;
    
    /**
     * 失败条数
     */
    private long failAccout;

    /**
     * 错误信息
     */
    private List<String> errMsgList;

    /**
     * 已处理成功报价信息
     */
    private List<QuoteResultDto> quoteResultList;

    /**
     * 获取 successAccout
     * @return successAccout.
     */
    public long getSuccessAccout() {
        return successAccout;
    }

    /**
     * 设置 successAccout
     * @param successAccout successAccout
     */
    public void setSuccessAccout(long successAccout) {
        this.successAccout = successAccout;
    }

    /**
     * 获取 failAccout
     * @return failAccout.
     */
    public long getFailAccout() {
        return failAccout;
    }

    /**
     * 设置 failAccout
     * @param failAccout failAccout
     */
    public void setFailAccout(long failAccout) {
        this.failAccout = failAccout;
    }

    /**
     * 获取 errMsgList
     * @return errMsgList.
     */
    public List<String> getErrMsgList() {
        return errMsgList;
    }

    /**
     * 设置 errMsgList
     * @param errMsgList errMsgList
     */
    public void setErrMsgList(List<String> errMsgList) {
        this.errMsgList = errMsgList;
    }

    /**
     * 获取 quoteResultList
     * @return quoteResultList.
     */
    public List<QuoteResultDto> getQuoteResultList() {
        return quoteResultList;
    }

    /**
     * 设置 quoteResultList
     * @param quoteResultList quoteResultList
     */
    public void setQuoteResultList(List<QuoteResultDto> quoteResultList) {
        this.quoteResultList = quoteResultList;
    }

    /**
     * 添加“处理结果”到“处理结果列表”对象中
     * @param quoteResult QuoteResultDto
     */
    public void addQuoteResult(QuoteResultDto quoteResult) {
        if (CollectionUtils.isEmpty(this.quoteResultList)) {
            this.quoteResultList = new ArrayList<>();
        }
        this.quoteResultList.add(quoteResult);
    }
    
    public int getQuoteResultListSize() {
        if (CollectionUtils.isEmpty(this.quoteResultList)) {
            return 0;
        }
        return this.quoteResultList.size();
    }
}
