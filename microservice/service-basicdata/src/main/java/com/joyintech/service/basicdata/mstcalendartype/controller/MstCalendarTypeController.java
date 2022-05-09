/*
 * 文件名：MstCalendarType001Controller.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：Administrator
 * 修改时间：2018年7月26日 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.basicdata.mstcalendartype.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.basicdata.mstcalendartype.dto.MstCalendarType001Dto;
import com.joyintech.service.basicdata.mstcalendartype.service.MstCalendarTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 日历类型信息管理MstCalendarType001Controller
 * 
 * @author liuwei
 * @version 1.0 2018年8月18日
 * @see MstCalendarTypeController
 * @since 1.0
 */
@RestController("MstCalendarTypeCtr_dtx")
@RequestMapping("/MstCalendarType")
public class MstCalendarTypeController {

    /**
     * 日历类型信息管理Service
     */
    @Autowired
    private MstCalendarTypeService mstCalendarTypeService;

    /**
     * 交易日历类型的下拉框事件
     * 
     * @param map 页面参数
     * @return 查询的交易日历类型列表
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("/getCalendarType")
    public List<SelectOptionDto> getCalendarType(@RequestBody Map<String, Object> map) {
        return mstCalendarTypeService.getCalendarType(map);
    }

    /**
     * 删除
     *
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody MstCalendarType001Dto dto)
    {
        return mstCalendarTypeService.delete(dto);
    }

    /**
     * 插入
     *
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("insert")
    public int insert(@RequestBody MstCalendarType001Dto dto)
    {
        return mstCalendarTypeService.insert(dto);
    }

    /**
     * 更新
     *
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("update")
    public int update(@RequestBody MstCalendarType001Dto dto)
    {
        return mstCalendarTypeService.update(dto);
    }

    /**
     * 交易日历名称的联想文本框事件
     * 
     * @param map 页面参数
     * @return 查询的交易日历代码和交易日历名称列表
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("/getCalendarNameList")
    public List<SuggestDto> getCalendarNameList(@RequestBody Map<String, Object> map)
    {
        List<SuggestDto> temp = mstCalendarTypeService.getCalendarNameList(map);
        return temp;
    }

    /**
     * 查询操作
     * 
     * @param map 页面参数
     * @return 查询结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("/selectForPage/{page}/{length}")
    public Page<MstCalendarType001Dto> selectForPage(@RequestBody Map<String, Object> map, @PathVariable("page") int page,
                                                     @PathVariable("length") int length)
    {
        return mstCalendarTypeService.selectForPage(page, length, map);
    }

    /**
     * 交易日历名称的下拉框查询 节假日查询界面的交易日历名称的下拉框查询
     * 
     * @param param 查询参数
     * @return 交易日历名称
     * @see
     * @since 1.0
     */
    @PostMapping("/getCalendarName")
    public List<SelectOptionDto> getCalendarName(@RequestBody Map<String, Object> param) {
        return mstCalendarTypeService.getCalendarName(param);
    }

    /**
     * 查询结果画面联动 通过输入的交易日历名称和查询日期联动出查询结果
     * 
     * @param map 画面参数
     * @return dto
     * @see
     * @since 1.0
     */
    @PostMapping("/getResult")
    public int getResult(@RequestBody Map<String, Object> map) {
        // 将需要的数据字典和参数添加到map中
        return mstCalendarTypeService.getResult(map);
    }

    /**
     * 双击查看界面和修改界面的初始化 通过点击的数据信息查询相关数据
     * 
     * @param param 查询参数
     * @return 结果
     * @see
     * @since 1.0
     */
    @PostMapping("/selectCalendar")
    public MstCalendarType001Dto selectCalendar(@RequestBody Map<String, Object> param) {
        return mstCalendarTypeService.selectCalendar(param);
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
    @PostMapping("/selectCanlendarDetail/{page}/{length}")
    public Page<MstCalendarType001Dto> selectCanlendarDetail(@RequestBody Map<String, Object> map, @PathVariable("page") int page,
                                                             @PathVariable("length") int length)
    {
        return mstCalendarTypeService.selectCanlendarDetail(page, length, map);
    }

    /**
     * 交易日历下拉框---模糊查询--存款标的信息
     * 
     * @param map 参数
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    @PostMapping("/getTradeCalendarName")
    public List<SuggestDto> getTradeCalendarName(@RequestBody Map<String, Object> map) {
        return mstCalendarTypeService.getTradeCalendarName(map);
    }

    // TISHKS-725:增加日历类型是否被使用校验 modify by fuyongsheng 20220507 start
    /**
     * 查询日历名称
     * @param calendarName 日历名称
     */
    @GetMapping("/hasCalendarName")
    public Integer hasCalendarName(String calendarName){
        return mstCalendarTypeService.hasCalendarName(calendarName);
    }
    // TISHKS-725:增加日历类型是否被使用校验 modify by fuyongsheng 20220507 end
}
