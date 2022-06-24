/*
 * 文件名：MstCalendarType001Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年8月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.basicdata.mstcalendartype.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.basicdata.mstcalendartype.dto.MstCalendarType001Dto;

/**
 * 日历类型信息管理Mapper
 * 
 * @author liuwei
 * @version 1.0 2018年8月15日
 * @see MstCalendarTypeMapper
 * @since 1.0
 */
@Repository
public interface MstCalendarTypeMapper {

    /**
     * 包含日历类型的下拉框查询
     * 查询界面的包含日历类型的下拉框查询
     * @param param 查询的参数
     * @return 包含日历类型
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getCalendarType(Map<String, Object> param);

    /**
     * 交易日历名称联想文本框查询
     * 查询界面的交易日历名称联想文本框查询
     * @param map 查询的参数
     * @return 交易日历名称
     * @see
     * @since 1.0
     */
    List<SuggestDto> getCalendarNameList(Map<String, Object> map);

    /**
     * 查询操作
     * 通过页面输入的参数查询相关数据
     * @param pageParam 页面参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<MstCalendarType001Dto> select(Map<String, Object> pageParam);

    /**
     * 节假日查询界面交易日历名称的下拉框查询
     * @param param 查询参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getCalendarName(Map<String, Object> param);

    /**
     * 节假日查询界面的联动事件
     * 通过输入的交易日历名称和查询日期查询是否是节假日
     * @param map 查询参数
     * @return 结果
     * @see
     * @since 1.0
     */
    int getResult(Map<String, Object> map);

    /**
     * 交易日历表的删除
     * 
     * @param dto 删除参数
     * @return 结果
     * @see
     * @since 1.0
     */
    int deleteInTradeCalendar(MstCalendarType001Dto dto);

    /**
     * 日历信息表删除
     * 
     * @param dto 删除参数
     * @return 结果
     * @see
     * @since 1.0
     */
    int deleteInCalendar(MstCalendarType001Dto dto);

    /**
     * 双击查看界面和编辑界面初始化
     * 通过点击数据的信息查询交易日历信息完成查看界面的初始化
     * @param param 查询参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<MstCalendarType001Dto> selectCalendar(Map<String, Object> param);

    /**
     * 交易日历的插入事件
     * 
     * @param dto 插入参数
     * @return 结果
     * @see
     * @since 1.0
     */
    int insertTradeCalendar(MstCalendarType001Dto dto);
    
    /**
     * 日历信息表的插入事件
     * 
     * @param dto 插入参数
     * @return 结果
     * @see
     * @since 1.0
     */
    int insertCalendar(MstCalendarType001Dto dto);

    /**
     * 日历信息表的修改事件
     * 
     * @param dto 修改参数
     * @return 结果
     * @see
     * @since 1.0
     */
    int updateCalendar(MstCalendarType001Dto dto);

    /**
     * 日历名称明细查询
     * 
     * @param map 查询参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<MstCalendarType001Dto> selectCanlendarDetail(Map<String, Object> map);
    /**
     * 交易日历下拉框---模糊查询--存款标的信息
     * @param  map 参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<SuggestDto> getTradeCalendarName(Map<String, Object> map);

    // TISHKS-725:增加日历类型是否被使用校验 modify by fuyongsheng 20220507 start
    Integer hasCalendarName(@Param("calendarName") String calendarName);
    // TISHKS-725:增加日历类型是否被使用校验 modify by fuyongsheng 20220507 end
}
