/*
 * 文件名：RskAuthorityManageController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.risk.rskauthoritymanage.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.annotation.DTXTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.risk.rskauthoritymanage.dto.RskAuthorityManageDto;
import com.joyintech.service.risk.rskauthoritymanage.service.RskAuthorityManageService;
import com.joyintech.tams.component.base.core.dto.WflValidInputParameter;

/**
 * 风险授权管理
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年11月30日
 * @see RskAuthorityManageController
 * @since 1.0
 */
@RestController("rskAuthorityManageCtl_dtx")
@RequestMapping("/RskAuthorityManage")
public class RskAuthorityManageController {
    /**
     * rskAuthorityManageService
     */
    @Autowired
    private RskAuthorityManageService rskAuthorityManageService;
    /**
     * 用户下拉框
     * @param param
     * @return
     */
    @PostMapping("/getUser/{page}/{length}")
    public Page<SelectOptionDto> getFinprodMarketIdToNew(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                         @PathVariable("length") int length) {
        return rskAuthorityManageService.getUser(param,length,page);
    }
    /**
     * 画面查询
     * @param param
     * @param page
     * @param length
     * @return
     * @throws Exception 
     * @see
     * @since 1.0
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<RskAuthorityManageDto> selectForPage(@RequestBody Map<String, Object> param,
         @PathVariable("page") int page, @PathVariable("length") int length) {
        Page<RskAuthorityManageDto> pageList = rskAuthorityManageService.selectAll(page, length, param);
        return pageList;
    }
    /**
     * 删除
     * 
     * @param dto
     *            页面选中的数据
     * @param br
     *            br
     * @return 结果
     * @throws Exception
     *             异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody RskAuthorityManageDto dto) {
        return rskAuthorityManageService.delete(dto);
    }
    
    /**
     * 插入
     * 
     * @param dto
     *            页面选中的数据
     * @param br
     *            br
     * @return 结果
     * @throws Exception
     *             异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("insert")
    public int insert(@RequestBody RskAuthorityManageDto dto) {
        return rskAuthorityManageService.insert(dto);
    }

    /**
     * 更新
     * 
     * @param dto
     *            页面选中的数据
     * @param br
     *            br
     * @return 结果
     * @throws Exception
     *             异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("update")
    public int update(@RequestBody RskAuthorityManageDto dto) {
        return rskAuthorityManageService.update(dto);
    }
    /**
     * 根据流水号查询授权管理
     * <功能详细描述>
     * @param dto dto
     * @return list
     * @see
     * @since 1.0
     */
    @PostMapping("/selectBySecNo")
    public RskAuthorityManageDto selectByFinprodIdAndEffDate(@RequestBody RskAuthorityManageDto dto) throws InvocationTargetException, IllegalAccessException {
        return rskAuthorityManageService.selectBySecNo(dto);
    }
    /**
     * 根据画面的数据判断数据库中是否已存在这条数据
     * <功能详细描述>
     * @param dto dto
     * @return list
     * @see
     * @since 1.0
     *//*
    @PostMapping("/selectByDto")
    public List<RskAuthorityManageDto> selectByDto(@RequestBody RskAuthorityManageDto dto) throws Exception {
        return rskAuthorityManageService.selectByDto(dto);
    }*/
    /**
     * 根据画面选择的用户查询数据库
     * <功能详细描述>
     * @param dto dto
     * @return list
     * @see
     * @since 1.0
     */
    @GetMapping("/selectByUserName")
    public List<RskAuthorityManageDto> selectByUserName(String existUserName) {
        return rskAuthorityManageService.selectByUserName(existUserName);
    }
    /**
     * 根据用户名查询数据
     * <功能详细描述>
     * @param dto dto
     * @return list
     * @see
     * @since 1.0
     */
    @PostMapping("/getDtoByUserName")
    public List<RskAuthorityManageDto> getDtoByUserName(@RequestBody Map<String,Object> map) {
        return rskAuthorityManageService.getDtoByUserName(map);
    }
    /**
     * 根据用户名查询真实的名字
     * <一句话功能简述>
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @GetMapping("/selectRealNameByUserName")
    public String selectRealNameByUserName(String userName){
        return rskAuthorityManageService.selectRealNameByUserName(userName);
    }
    @PostMapping("/getBusinessData")
    public List<RskAuthorityManageDto> getBusinessData(@RequestBody WflValidInputParameter parameter){
        return rskAuthorityManageService.getBusinessData(parameter);
    }
    
    /**
     * 检查是否有流程中的资产交易数据
     * <功能详细描述>
     * @param param Map<String, Object>
     * @return Integer
     * @see
     * @since 1.0
     */
    @PostMapping("/selectAssetTrade")
    public Integer selectAssetTrade(@RequestBody Map<String, Object> param) {
        return rskAuthorityManageService.selectAssetTrade(param);
    }
    /**
     * 查询已授权用户
     * @param param
     * @param page
     * @param length
     * @return
     * @throws Exception 
     * @see
     * @since 1.0
     */
    @PostMapping("getExistUser/{page}/{length}")
    public Page<RskAuthorityManageDto> getExistUser(@RequestBody Map<String, Object> param,
         @PathVariable("page") int page, @PathVariable("length") int length) {
        return rskAuthorityManageService.getExistUser(page, length, param);
    }
}
