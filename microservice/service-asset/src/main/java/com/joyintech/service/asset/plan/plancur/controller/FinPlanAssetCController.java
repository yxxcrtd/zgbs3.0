/*
 * 文件名：FinPlanAssetCController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2019年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.asset.plan.plancur.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.service.asset.plan.plancur.dto.FinPlanAssetCDto;
import com.joyintech.service.asset.plan.plancur.service.FinPlanAssetCService;

/**
 * 计划类资产（货币型）
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2019年1月7日
 * @see FinPlanAssetCController
 * @since 1.0
 */
@RestController("FinPlanAssetCController_dtx")
@RequestMapping("/FinPlanAssetC")
public class FinPlanAssetCController {
    @Autowired
    private FinPlanAssetCService finPlanAssetCService;
    /**
     * 画面查询方法
     * 
     * @param param 画面参数
     * @param page 页码数
     * @param length 页面长度
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<FinPlanAssetCDto> selectAll(@RequestBody Map<String, Object> param,
                                              @PathVariable("page") int page, @PathVariable("length") int length) {
        return finPlanAssetCService.selectForPage(page, length, param);
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
    public int delete(@RequestBody FinPlanAssetCDto dto) throws Exception {
        return finPlanAssetCService.delete(dto);
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
    public int insert(@RequestBody FinPlanAssetCDto dto)  throws Exception {
        return finPlanAssetCService.insert(dto);
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
    public int update(@RequestBody FinPlanAssetCDto dto) throws Exception {
        return finPlanAssetCService.update(dto);
    }
    /**
     * 初始化查询数据
     * 
     * @param param 画面参数
     * @return 查询结果
     * @throws Exception 异常
     */
    @PostMapping("getBasicInfoByFinprodId")
    public FinPlanAssetCDto getBasicInfoByFinprodId(@RequestBody Map<String, Object> param) throws Exception {
        return finPlanAssetCService.getBasicInfoByFinprodId(param);
    }
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 start*/
    @GetMapping("getFinprodId")
    public String getFinprodId(String finprodMarketId){
        return finPlanAssetCService.getFinprodId(finprodMarketId);
    }
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 end*/
    /*增加资产代码重复校验 add by zhuyongkang 20191021 start*/
    /**
     * 查询市场代码
     * @param finprodMarketId 市场代码
     */
    @GetMapping("selectFinprodMarketId")
    public int selectFinprodMarketId( String finprodMarketId){
        return finPlanAssetCService.selectFinprodMarketId(finprodMarketId);
    }
    /*增加资产代码重复校验 add by zhuyongkang 20191021 end*/
}
