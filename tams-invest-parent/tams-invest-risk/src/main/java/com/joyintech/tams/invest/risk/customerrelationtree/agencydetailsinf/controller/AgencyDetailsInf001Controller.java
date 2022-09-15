/*
 * 文件名：AgencyDetailsInf001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangkang
 * 修改时间：2019年12月06日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.dto.AgencyDetailsInf001Dto;
import com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.dto.TreeNode;
import com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.service.AgencyDetailsInf001Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 机构详细信息页面
 * 〈功能详细描述〉
 * @author wangkang
 * @version 1.0 2019年12月06日
 * @see AgencyDetailsInf001Controller
 * @since 1.0
 */
@Controller
@PageId("AgencyDetailsInf001")
@RequestMapping("/invest/risk/customerrelationtree/agencydetailsinf/AgencyDetailsInf001")
public class AgencyDetailsInf001Controller extends BaseController{

    /**
     * 机构详细信息service注入
     */
    @Autowired
    private AgencyDetailsInf001Service agencyDetailsInf001Service;

    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        // TODO Auto-generated method stub
        model.addAttribute("agencyId", getParam("agencyId"));
        return super.init(model);
    }
    
    /**
     * 持仓信息查询
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<AgencyDetailsInf001Dto> selectForPage()
    {
        return agencyDetailsInf001Service.selectForPage(
            RequestUtil.getParameters(getRequest()));
    }
    
    /**
     * 机构联想框
     * @param search
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getAgency")
    public Page<SelectOptionDto> getAgency(){
        return agencyDetailsInf001Service.getAgency(RequestUtil.getParameters(getRequest()));
    }
    
    /**
     * 机构树查询
     * @return
     */
    @ResponseBody
    @RequestMapping("getTreeData")
    public TreeNode getTreeData(){
        return agencyDetailsInf001Service.getTreeData(getParam("agencyId"));
    }
    
    /**
     * 删除节点
     * @return
     */
    @ResponseBody
    @RequestMapping("delNode")
    public int delNode(){
        return agencyDetailsInf001Service.delNode(RequestUtil.getParam("seqNo"));
    }
    
    /**
     * 导出
     * @return
     * @throws Exception
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export() {
        return agencyDetailsInf001Service.export(this.getPageId(),RequestUtil.getParameters(getRequest()));
    }
    /**
     * 操作的check
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody AgencyDetailsInf001Dto dto){
        AjaxResult result=new AjaxResult();
//        result.appendMessageList(agencyDetailsInf001Service.check(dto));
        return result;
    }

}
