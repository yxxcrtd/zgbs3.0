/*
 * 文件名：MstCalendarType001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年8月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.basicdata.mstcalendartype.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.basicdata.mstcalendartype.dao.MstCalendarTypeMapper;
import com.joyintech.service.basicdata.mstcalendartype.dto.MstCalendarType001Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 日历类型管理Service
 * 
 * @author liuwei
 * @version 1.0 2018年8月15日
 * @see MstCalendarTypeService
 * @since 1.0
 */

@Service
@Transactional
public class MstCalendarTypeService extends BaseService<MstCalendarType001Dto>{
    /**
     * 日历类型管理Mapper
     */
    @Autowired
    private MstCalendarTypeMapper mstCalendarType001mapper;

    /**
     * 日历类型下拉框查询
     * 
     * @param param 查询参数
     * @return 日历类型
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCalendarType(Map<String, Object> param) {
        return mstCalendarType001mapper.getCalendarType(param);
    }

    /**
     * 删除操作
     */
    public int delete(MstCalendarType001Dto entity) {
        //删除交易日历表信息
        mstCalendarType001mapper.deleteInTradeCalendar(entity);
        //将交易日历代码赋值给日历信息表的日历代码
        entity.setCalendarCode(entity.getTradeCalendarId());
        //默认删除日历信息表的数据为交易日历
        entity.setIsTradeCalendar(DictCodeList.是否_是.getValue());
        //删除日历信息表信息
        return mstCalendarType001mapper.deleteInCalendar(entity);
    }

    /**
     * 插入操作
     */
    public int insert(MstCalendarType001Dto dto) {
        dto.setIsTradeCalendar(DictCodeList.是否_是.getValue());
        //将多选框获得的日历类型按逗号分隔成list
        List<String> calendarTypeList=StringUtil.splitToList(GlobalConstant.COMMA, dto.getCalendarType());
        dto.setCalendarTypeList(calendarTypeList);
        //将数据插入到交易日历表
        mstCalendarType001mapper.insertTradeCalendar(dto);
        dto.setCalendarCode(dto.getTradeCalendarId());
        //默认插入日历信息表的数据为交易日历
        dto.setIsTradeCalendar(DictCodeList.是否_是.getValue());
        //将数据插入到日历信息表
        return mstCalendarType001mapper.insertCalendar(dto);
    }

    /**
     * 更新操作
     */
    public int update(MstCalendarType001Dto dto) {
        String tradeCalendarId=dto.getTradeCalendarId();
        //先删除该条交易日历数据
        mstCalendarType001mapper.deleteInTradeCalendar(dto);
        dto.setTradeCalendarId(tradeCalendarId);
        dto.setIsTradeCalendar(DictCodeList.是否_是.getValue());
        List<String> calendarTypeList=StringUtil.splitToList(GlobalConstant.COMMA, dto.getCalendarType());
        dto.setCalendarTypeList(calendarTypeList);
        //将修改后的数据插入到交易日历表
        mstCalendarType001mapper.insertTradeCalendar(dto);
        dto.setCalendarCode(tradeCalendarId);
        //同时更新日历信息表的备注信息
        return mstCalendarType001mapper.updateCalendar(dto);
    }

    /**
     * 交易日历名称的联想文本框事件
     * 查询界面的交易日历名称的联想文本框事件
     * @param map 查询参数
     * @return 交易日历名称
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getCalendarNameList(Map<String, Object> map) {
        return  mstCalendarType001mapper.getCalendarNameList(map);
    }

    /**
     * 查询操作
     * 通过输入的数据进行查询
     * @param length 长度 
     * @param page 页数
     * @param pageParam 页面参数
     * @return 结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<MstCalendarType001Dto> selectForPage(int page, int length, Map<String, Object> pageParam) {
        return super.queryForPage(page,length,()-> mstCalendarType001mapper.select(pageParam));
    }

    /**
     * 交易日历名称的下拉框查询
     * 节假日查询界面的交易日历名称的下拉框查询
     * @param param 查询参数
     * @return 交易日历名称
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCalendarName(Map<String, Object> param) {
        return mstCalendarType001mapper.getCalendarName(param);
    }

    /**
     * 节假日查询
     * 通过输入的交易日历名称和查询日期查询是否是节假日
     * @param map 查询参数
     * @return 结果
     * @see
     * @since 1.0
     */
    public int getResult(Map<String, Object> map) {
        return mstCalendarType001mapper.getResult(map);
    }

    /**
     * 双击查看界面和修改界面的初始化
     * 通过点击的数据信息查询相关数据
     * @param param 查询参数
     * @return 结果
     * @see
     * @since 1.0
     */
    public MstCalendarType001Dto selectCalendar(Map<String, Object> param) {
        List<MstCalendarType001Dto> resultList = mstCalendarType001mapper.selectCalendar(param);        
        List<String> calendarType = new ArrayList<>();
        resultList.forEach(item->{
            calendarType.add(item.getCalendarType());
        });
        MstCalendarType001Dto resultDto = resultList.get(0);
        resultDto.setCalendarType(StringUtil.joinString(calendarType, GlobalConstant.COMMA));
        return resultDto;
    }

    /**
     * 交易日历名称的明细查询
     * 
     * @param map 查询参数
     * @return 结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<MstCalendarType001Dto> selectCanlendarDetail(int page, int length, Map<String, Object> map)
    {
        return super.queryForPage(page,length,()-> mstCalendarType001mapper.selectCanlendarDetail(map));
    }

    /**
     * 交易日历下拉框---模糊查询--存款标的信息
     * @param map
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getTradeCalendarName(Map<String, Object> map) {
        return mstCalendarType001mapper.getTradeCalendarName(map);
    }

    // TISHKS-725:增加日历类型是否被使用校验 modify by fuyongsheng 20220507 start
    public Integer hasCalendarName(String calendarName){
        return mstCalendarType001mapper.hasCalendarName(calendarName);
    }
    // TISHKS-725:增加日历类型是否被使用校验 modify by fuyongsheng 20220507 end
}
