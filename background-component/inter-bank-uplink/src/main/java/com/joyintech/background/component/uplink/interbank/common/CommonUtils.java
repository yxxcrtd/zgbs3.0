/*
 * 文件名：CommonUtils.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Jiaoxujin
 * 修改时间：2020年5月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.background.component.uplink.interbank.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import com.joyintech.background.component.uplink.interbank.business.dto.*;
import com.joyintech.framework.util.CacheUtil;
import org.apache.commons.lang3.StringUtils;

import com.github.pagehelper.PageHelper;
import com.joyintech.background.component.common.pack.common.util.StringUtil;

/**
 * 银行间上行组件共通函数
 * @author Jiaoxujin
 * @version 1.0 2020年5月9日
 * @see CommonUtils
 * @since 1.0
 */
public class CommonUtils {
    /**
     * 根据银行间上行操作类型取得对话报价的交易状态和发送状态编码
     * @param operationType String 银行间上行操作类型
     * @return Map<String, String>
     */
    public static Map<String, String> getBondDialogueQuoteStatus(String operationType) {
        Map<String, String> result = new HashMap<>();

        String isUpLinkSend =  CacheUtil.getSysParam("IS_NEED_UPLINK");
        if(StringUtil.equals(DictCodeList.是否_否.getValue(),isUpLinkSend)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), StringUtils.EMPTY);
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_待发送.getValue());
            return result;
        }
       if (DictCodeList.银行间上行操作类型_本方报价新增.getValue().equals(operationType)) {
           result.put(IbuDictCode.银行间上行报价状态.getValue(), StringUtils.EMPTY);
           result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_待发送.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方新增发送.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_新增中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方新增发送成功.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已新增.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方新增发送失败.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_新增中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方报价修改.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), StringUtils.EMPTY);
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_待发送.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方修改发送.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_修改中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方修改发送成功.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已修改.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方修改发送失败.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_修改中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        /*else if (DictCodeList.银行间上行操作类型_本方修改发送.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_修改中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方修改发送失败.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_修改中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方修改发送成功.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已修改.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }*/
        else if (DictCodeList.银行间上行操作类型_本方报价撤回.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_撤回中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方报价撤回失败.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_撤回中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方报价撤回成功.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已撤回.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_对手方回复成交.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已成交.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if (DictCodeList.银行间上行操作类型_对手方回复拒绝.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已终止.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if (DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_待回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if (DictCodeList.银行间上行操作类型_对手方报价修改.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_待回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if (DictCodeList.银行间上行操作类型_对手方报价撤回.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已撤回.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if (DictCodeList.银行间上行操作类型_本方回复成交.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_成交中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方回复成交成功.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已成交.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方回复成交失败.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_成交中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方回复拒绝.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_终止中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方回复拒绝成功.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已终止.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方回复拒绝失败.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_终止中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_本方报价回退.getValue().equals(operationType)) {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), StringUtils.EMPTY);
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_待发送.getValue());
        }
        else if (DictCodeList.银行间上行操作类型_撤销.getValue().equals(operationType)){
        	result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已撤销.getValue());
        	result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if (DictCodeList.银行间上行操作类型_对手方交谈新增.getValue().equals(operationType)) {
        	result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_待回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if (DictCodeList.银行间上行操作类型_对手方交谈修改.getValue().equals(operationType)) {
        	result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_待回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        // 默认值
        else {
            result.put(IbuDictCode.银行间上行报价状态.getValue(), StringUtils.EMPTY);
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_待发送.getValue());
        }

        return result;
    }

    /**
     * 根据银行间上行操作类型取得请求报价的交易状态和发送状态编码
     * @param operationType String 银行间上行操作类型
     * @return Map<String, String>
     */
    public static Map<String, String> getBondRequestQuoteStatus(String operationType) {
        Map<String, String> result = new HashMap<>();
        String isUpLinkSend =  CacheUtil.getSysParam("IS_NEED_UPLINK");
        if(StringUtil.equals(DictCodeList.是否_否.getValue(),isUpLinkSend)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), StringUtils.EMPTY);
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_待发送.getValue());
            return result;
        }
        if (DictCodeList.银行间上行操作类型_本方请求报价新增.getValue().equals(operationType)) {
        	result.put(IbuDictCode.银行间上行报价状态.getValue(), StringUtils.EMPTY);
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_待发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方新增请求报价发送.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_新增中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方新增请求报价发送成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_待回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方新增请求报价发送失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_新增中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方请求报价撤回.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_撤回中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方请求报价撤回成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已撤回.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方请求报价撤回失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_撤回中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if(operationType == null){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_接收全拒绝请求报价.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已终止.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_本方请求回复成交.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_成交中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方请求回复成交成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已成交.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方请求回复成交失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_成交中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_接收已过期.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已过期.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        
        else if(DictCodeList.银行间上行操作类型_对手方新增请求报价.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_待回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_对手方撤销请求报价.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已撤回.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_接收请求回复成交成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已成交.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_本方拒绝请求报价.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_拒绝中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方拒绝请求报价成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已拒绝.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_本方拒绝请求报价失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_拒绝中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方新增请求回复.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_回复中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        
        else if(DictCodeList.银行间上行操作类型_本方新增请求回复成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_本方新增请求回复失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_回复中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方修改请求回复.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_修改中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方修改请求回复成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已修改.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_本方修改请求回复失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_修改中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方撤销请求回复.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_撤回中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方撤销请求回复成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_待回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_本方撤销请求回复失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_撤回中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_对手方拒绝请求报价.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已拒绝.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_对手方新增请求回复.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_对手方修改请求回复.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_对手方撤销请求回复.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已撤回.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if (DictCodeList.银行间上行操作类型_撤销.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已撤销.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        return result;
    }
    
    /**
     * 根据银行间上行操作类型取得质押式回购的交易状态和发送状态编码
     * @param operationType String 银行间上行操作类型
     * @return Map<String, String>
     */
    public static Map<String, String> getRepoDialogueQuoteStatus(String operationType) {
        Map<String, String> result = new HashMap<>();
        String isUpLinkSend =  CacheUtil.getSysParam("IS_NEED_UPLINK");
        if(StringUtil.equals(DictCodeList.是否_否.getValue(),isUpLinkSend)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), StringUtils.EMPTY);
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_待发送.getValue());
            return result;
        }
        if(DictCodeList.银行间上行操作类型_本方报价新增.getValue().equals(operationType)){
        	result.put(IbuDictCode.银行间上行报价状态.getValue(), StringUtils.EMPTY);
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_待发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方新增发送.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_新增中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方新增发送成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已新增.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方新增发送失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_新增中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        
        // add by lijie2 
        else if(DictCodeList.银行间上行操作类型_本方报价修改.getValue().equals(operationType)||DictCodeList.银行间上行操作类型_本方修改发送.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_修改中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方修改发送成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已修改.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方修改发送失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_修改中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
     // add by etliu 20210106 start
        else if(DictCodeList.银行间上行操作类型_本方交谈修改.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_交谈中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
     // add by etliu 20210106 end
        else if(DictCodeList.银行间上行操作类型_本方交谈修改发送.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_交谈中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方交谈修改发送成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已交谈.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方交谈修改发送失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_交谈中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方交谈发送.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_交谈中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方交谈发送成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已交谈.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方交谈发送失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_交谈中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        // add by lijie2 
        else if(DictCodeList.银行间上行操作类型_对手方交谈修改.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_待回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_对手方交谈新增.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_待回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
/*        else if(DictCodeList.银行间上行操作类型_询价方交谈修改.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_待回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_询价方交谈新增.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_待回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }*/
        else if(DictCodeList.银行间上行操作类型_本方回复成交.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_成交中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方回复成交成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已成交.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        /*else if(DictCodeList.银行间上行操作类型_本方回复成交失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_成交中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }*/
        else if(DictCodeList.银行间上行操作类型_本方回复成交.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_成交中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方回复拒绝.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_终止中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方回复拒绝成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已终止.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方回复拒绝失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_终止中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方报价撤回.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_撤回中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_已发送.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方报价撤回成功.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已撤回.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_成功.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_本方报价撤回失败.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_撤回中.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), DictCodeList.银行间上行发送状态_失败.getValue());
        }
        else if(DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_待回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if(DictCodeList.银行间上行操作类型_对手方报价修改.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_待回复.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        else if (DictCodeList.银行间上行操作类型_撤销.getValue().equals(operationType)){
            result.put(IbuDictCode.银行间上行报价状态.getValue(), DictCodeList.银行间上行报价状态_已撤销.getValue());
            result.put(IbuDictCode.银行间上行发送状态.getValue(), StringUtils.EMPTY);
        }
        return result;
    }

    /**
     * 通过IbuQuoteBond(现券银行间报价)和IbuQuoteBondDet(现券银行间报价明细)设置基础报价信息对象BaseQuoteDto
     * @param baseQuote BaseQuoteDto
     * @param quoteBond IbuQuoteBondDto
     * @param quoteBondDet IbuQuoteBondDetDto
     */
    public static void setBaseQuoteDto(BaseQuoteDto baseQuote, IbuQuoteBondDto quoteBond,
                                       IbuQuoteBondDetDto quoteBondDet) {
        if (quoteBond != null) {
            baseQuote.setQuoteId(quoteBond.getQuoteId());
            baseQuote.setQuoteType(quoteBond.getQuoteType());
            baseQuote.setQuoteRoleType(quoteBond.getQuoteRoleType());
            baseQuote.setpQuoteId(quoteBond.getpQuoteId());
            baseQuote.setFinprodId(quoteBond.getFinprodId());
            baseQuote.setFinprodType2(quoteBond.getFinprodType2());
            baseQuote.setBranch(quoteBond.getBranch());
            baseQuote.setPortfolioId(quoteBond.getPortfolioId());
            baseQuote.setEntrustId(quoteBond.getEntrustId());
            baseQuote.setQuoteDate(quoteBond.getQuoteDate());
            baseQuote.setSettCcy(quoteBond.getSettCcy());
            baseQuote.setTradeStatus(StringUtils.isBlank(quoteBond.getTradeStatus()) ? 
                StringUtil.EMPTY : quoteBond.getTradeStatus());
            baseQuote.setSendStatus(StringUtils.isBlank(quoteBond.getSendStatus()) ? 
                StringUtil.EMPTY : quoteBond.getSendStatus());
            baseQuote.setEffTime(quoteBond.getEffTime());
            baseQuote.setOuPartyId(quoteBond.getOuPartyId());
            baseQuote.setOuTraderId(quoteBond.getOuTraderId());
            baseQuote.setPs(quoteBond.getPs());
        }

        if (quoteBondDet != null) {
            baseQuote.setShareAmt(quoteBondDet.getShareAmt());
            baseQuote.setPrinAmt(quoteBondDet.getPrinAmt());
            baseQuote.setCpriceAmt(quoteBondDet.getCpriceAmt());
            baseQuote.setIntAmt(quoteBondDet.getIntAmt());
            baseQuote.setFpriceAmt(quoteBondDet.getFpriceAmt());
            //baseQuote.setPs(quoteBondDet.getPs());
            baseQuote.setYield(quoteBondDet.getYield());
            baseQuote.setExerYield(quoteBondDet.getExerYield());
        }
    }

    /**
     * distinct by 任意字段
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T,Object> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    static final long[] byteTable = createLookupTable();
    static final long HSTART = 0xBB40E64DA205B064L;
    static final long HMULT = 7664345821815920749L;

    private static final long[] createLookupTable() {
        long[] byteTable = new long[256];
        long h = 0x544B2FBACAAF1684L;
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 31; j++) {
                h = (h >>> 7) ^ h;
                h = (h << 11) ^ h;
                h = (h >>> 10) ^ h;
            }
            byteTable[i] = h;
        }
        return byteTable;
    }
    
    /**
     * 针对中文、大小写、特殊字符优化了HashCode算法，减少重复
     */
    public static long hashCode(CharSequence cs) {
        long h = HSTART;
        final long hmult = HMULT;
        final long[] ht = byteTable;
        final int len = cs.length();
        for (int i = 0; i < len; i++) {
            char ch = cs.charAt(i);
            h = (h * hmult) ^ ht[ch & 0xff];
            h = (h * hmult) ^ ht[(ch >>> 8) & 0xff];
        }
        return h;
    }

    /**
     * 添加排序功能<br />
     * @param orderField String
     * @param orderDirection String
     */
    public static void sortList(String orderField, String orderDirection) {
        if (StringUtil.isNotEmpty(orderField)) {
            String orderString = orderField;
            if (StringUtil.isNotEmpty(orderDirection)) {
                orderString += StringUtil.SPACE + orderDirection;
            }
            //System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, "testData"));//TEST_DATA
            PageHelper.orderBy(orderString);
        }
    }


    /**
     *功能描述 是否操作历史表(发送状态为成功||交易状态为待回复||本方报价新增)
     * @author zhangnian
     * @date 2021/1/14 0014
     * @param  * @param inputBean
     * @return boolean
    */
    public static boolean isOperationHis(RepoDialogueQuoteDto inputBean){
        if(StringUtils.equals(inputBean.getIbuQuoteCrepDto().getSendStatus(),DictCodeList.银行间上行发送状态_成功.getValue())
            ||StringUtils.equals(inputBean.getIbuQuoteCrepDto().getTradeStatus(),DictCodeList.银行间上行报价状态_待回复.getValue())
            ||StringUtils.equals(inputBean.getIbuQuoteCrepDto().getExeType(),DictCodeList.银行间上行操作类型_本方新增发送.getValue())
        ){
            return true;
        }
        return false;
    }
}
