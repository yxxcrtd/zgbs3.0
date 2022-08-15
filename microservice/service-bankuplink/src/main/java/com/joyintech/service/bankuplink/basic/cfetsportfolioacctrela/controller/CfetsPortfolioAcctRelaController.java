/*
 * 文件名：CfetsPortfolioAcctRelaController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yufei
 * 修改时间：2020年5月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.bankuplink.basic.cfetsportfolioacctrela.controller;

import java.util.ArrayList;
import java.util.Map;

import com.joyintech.background.component.common.pack.common.constant.DictCodeList;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.util.EncryptUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.workflow.engine.WorkflowContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.bankuplink.basic.cfetsportfolioacctrela.dto.CfetsPortfolioAcctRela001Dto;
import com.joyintech.service.bankuplink.basic.cfetsportfolioacctrela.service.CfetsPortfolioAcctRelaService;
import com.joyintech.tams.common.base.entity.IbuPortfolioAcctRela;

/**
 * CFETS账户关联
 * 
 * @author yufei
 * @version 1.0 2020年5月12日
 * @see CfetsPortfolioAcctRelaController
 * @since 1.0
 */
@RestController("backCfetsPortfolioAcctRelaController")
@RequestMapping("/CfetsPortfolioAcctRela")
public class CfetsPortfolioAcctRelaController {

    /**
     * CFETS账户关联
     */
    @Autowired
    private CfetsPortfolioAcctRelaService cfetsPortfolioAcctRelaService;

    /**
     * 查询机构信息 <功能详细描述>
     * 
     * @param param 查询参数
     * @param page 页数
     * @param length 条数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @RequestMapping("selectMstCfetsCustomerInfo/{page}/{length}")
    public Page<SelectOptionDto> selectCommonSelect(@RequestBody Map<String, Object> param,
                                                    @PathVariable("page") int page,
                                                    @PathVariable("length") int length) {
        return cfetsPortfolioAcctRelaService.selectCommonSelect(page, length, param);
    }

    /**
     * 新建
     * 
     * @param dto 操作对象
     * @throws Exception 异常信息
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doInsert")
    public void doInsert(@RequestBody CfetsPortfolioAcctRela001Dto dto)
        throws Exception {
    	String portfolioIds = dto.getPortfolioId();
    	for(String portfolioId:portfolioIds.split(",")) {
    		dto.setPortfolioId(portfolioId);
            dto.setPortfolioIdMd5(EncryptUtil.getInstance().md5(dto.getPortfolioId()));
    		cfetsPortfolioAcctRelaService.doInsert(dto);
    		// 处理为留痕dto
            ProcessMark processMark = WorkflowContext.getProcessMarkService().getParameterByAnnotation(dto);
            // 设置工作流
            processMark.setWorkflowId(dto.getWorkflowId());
            // 设置画面ID
            processMark.setPageId("CfetsPortfolioAcctRela002");
            processMark.setStatus(DictCodeList.数据状态_新建.getValue());
            // 业务entity
            processMark.setBusinessData(JsonUtils.object2Json(dto));
            WorkflowContext.getProcessMarkService().insertIntoProcessMark(processMark);
    	}
    }

    /**
     * 修改
     * 
     * @param dto 操作对象
     * @throws Exception 异常信息
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doUpdate")
    public void doUpdate(@RequestBody CfetsPortfolioAcctRela001Dto dto)
        throws Exception {
    	String portfolioIds = dto.getPortfolioId();
    	for(String portfolioId:portfolioIds.split(",")) {
    		dto.setPortfolioId(portfolioId);
    		cfetsPortfolioAcctRelaService.doUpdate(dto);
    	}
    }

    /**
     * 删除
     * 
     * @param dto 操作对象
     * @throws Exception 异常信息
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doDelete")
    public void doDelete(@RequestBody CfetsPortfolioAcctRela001Dto dto)
        throws Exception {
        cfetsPortfolioAcctRelaService.doDelete(dto);
    }

    /**
     * CFETS账户关联-查询 <功能详细描述>
     * 
     * @param param 查询参数
     * @param page 页数
     * @param length 条数
     * @return 查询结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("selectForPage/{page}/{length}")
    public Page<CfetsPortfolioAcctRela001Dto> selectForPage(@RequestBody Map<String, Object> param,
                                                            @PathVariable("page") int page,
                                                            @PathVariable("length") int length)
        throws Exception {
    	
    	Object portfolioIdObj = param.get("portfolioId");
        if (portfolioIdObj != null) {
        	param.put("portfolioIdList",
                StringUtil.splitToList(GlobalConstant.COMMA, (String)portfolioIdObj));
        }
        else {
        	param.put("portfolioIdList", new ArrayList<>());
        }
    	
        return cfetsPortfolioAcctRelaService.selectForPage(page, length, param);
    }

    /**
     * 根据主键查询CFETS账户关联
     * 
     * @param param 入参
     * @return 报价信息
     * @see
     * @since 1.0
     */
    @PostMapping("queryInfoByPrimaryKey")
    public CfetsPortfolioAcctRela001Dto queryInfoByPrimaryKey(@RequestBody Map<String, Object> param) {
        return cfetsPortfolioAcctRelaService.queryInfoByPrimaryKey(param);
    }
    
    /**
     * -根据组合代码和账户类型查询记录是否存在
     * @param ibuPortfolioAcctRela cfets账户关联对象
     * @return 记录数量
     */
    @PostMapping("checkPortfolioIdAcctTypeExists")
    public int checkPortfolioIdAcctTypeExists(@RequestBody IbuPortfolioAcctRela ibuPortfolioAcctRela) {
    	return cfetsPortfolioAcctRelaService.checkPortfolioIdAcctTypeExists(ibuPortfolioAcctRela);
    }
}
