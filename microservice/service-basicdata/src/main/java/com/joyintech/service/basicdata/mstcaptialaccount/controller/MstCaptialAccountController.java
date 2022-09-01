/*
 * 文件名：FundAccountManagementController001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年7月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.basicdata.mstcaptialaccount.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.RequestUtil;
import com.joyintech.tams.common.base.entity.FinInitrateDatadict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.basicdata.mstcaptialaccount.dto.MstCaptialAccountDto;
import com.joyintech.service.basicdata.mstcaptialaccount.dto.MstCaptialAccountDto2;
import com.joyintech.service.basicdata.mstcaptialaccount.service.MstCaptialAccountService;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;

/**
 * 资金账户服务类
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年7月23日
 * @see MstCaptialAccountController
 * @since 1.0
 */
@RestController("MstCaptialAccountCtr_dtx")
@RequestMapping("/MstCaptialAccount")
public class MstCaptialAccountController {
    /**
     * {@Autowired}
     */
    @Autowired
    private MstCaptialAccountService mstCaptialAccountService;
    /**
     * 使用给定的acctName检索分页数据。返回类型是Service的泛型。
     *
     * @param pageParam 查询方法
     * @return Page类
     * @throws Exception 异常信息
     */
    @PostMapping("/selectForPage/{page}/{length}")
    public Page<MstCaptialAccountDto> selectForPage(@RequestBody Map<String, Object> pageParam,@PathVariable("page") int page, @PathVariable("length") int length){
        return mstCaptialAccountService.selectForPage(page,length,pageParam);
    }
    /**
     * 联想文本框service层实现
     *
     * @param acctName 检索参数
     * @return SuggestDto
     */
    @GetMapping("/getAcctName")
    public List<SuggestDto> getAcctName(String acctName) {
        return mstCaptialAccountService.getAcctName(acctName);
    }
    /**
     * 根据网点账号与币种查询详细信息 双击修改页面
     * 
     * @param map 网点账号与币种
     * @return 资金账户实体
     * @see
     * @since 1.0
     */
    @PostMapping("/selectDetail")
    public MstCaptialAccountDto selectDetail(@RequestBody Map<String, Object> map){
        return mstCaptialAccountService.selectDetail(map);
    }
    
    /**
     * 删除检查数据是否被使用过 <功能详细描述>
     * 
     * @param dto 业务实体
     * @return 查询条数
     * @see
     * @since 1.0
     */
    @PostMapping("/checkCodeIsUse")
    public int checkCodeIsUse(@RequestBody MstCaptialAccountDto dto){
        return mstCaptialAccountService.checkCodeIsUse(dto);
    }
    /**
     * 批量删除检查数据是否被使用过 <功能详细描述>
     * 
     * @param item 资金账户实体集合
     * @return 返回查询的所有条数
     * @see
     * @since 1.0
     */
    @PostMapping("/checkAllCodeIsUse")
    public List<MstCaptialAccountDto> checkAllCodeIsUse(@RequestBody List<MstCaptialAccountDto> item){
        return mstCaptialAccountService.checkAllCodeIsUse(item);
    }
    /**
     * 
     * 查询上传文件
     * <功能详细描述>
     * @param businessId businessId
     * @return List<FileArray>
     * @see
     * @since 1.0
     */
    @GetMapping("/selectFileByBusinessId")
    public List<SysUploadFileInfo> selectFileByBusinessId(String businessId){
        return mstCaptialAccountService.selectFileByBusinessId(businessId);
    }
    /**
     * 
     * 工作流完成操作
     * <功能详细描述>
     * @param dto 业务实体
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("/doComplete")
    public void doComplete(@RequestBody MstCaptialAccountDto dto) {
         mstCaptialAccountService.doComplete(dto);
    }
    /**
     * 查询
     * <功能详细描述>
     * @param map Map
     * @return list
     * @see
     * @since 1.0
     */
    @PostMapping("/select")
    public List<MstCaptialAccountDto> select(@RequestBody Map<String, Object> map) {
        return mstCaptialAccountService.select(map);
    }
    @PostMapping("/selectCheck")
    public List<MstCaptialAccountDto> selectCheck(@RequestBody Map<String, Object> map) {
    	return mstCaptialAccountService.selectCheck(map);
    }

    /**
     * //BSTOT-1123 资金账户-修改为外币账户，流程中币种未更新 guyunfei
     * @param map
     * @return
     */
    @PostMapping("/updateTitle")
    public int updateTitle(@RequestBody Map<String, Object> map){
        return mstCaptialAccountService.updateTitle(map);

    }

    @PostMapping("/getUniqueAcctNoPayNo")
    public int getUniqueAcctNoPayNo(@RequestBody Map<String, Object> map){
        return mstCaptialAccountService.getUniqueAcctNoPayNo(map);
    }
    /**
     * 
     * 查询流程当前节点信息
     * <功能详细描述>
     * @param workflowId
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("getSysWflBusinessList")
    public SysWflBusinessList getSysWflBusinessList(@RequestBody String workflowId){
        return mstCaptialAccountService.getSysWflBusinessList(workflowId); 
    }
	
	    
    @PostMapping("/selectForInterf")
    public List<MstCaptialAccountDto2> selectForInterf(@RequestBody Map<String, Object> map) {
        return mstCaptialAccountService.selectForInterf(map);
    }
    
    /**
     * 
     */
    @PostMapping("/getOwnDept/{page}/{length}")
    public Page<SelectOptionDto> getOwnDept(@RequestBody Map<String,Object> map, @PathVariable int page, @PathVariable int length) {
        return mstCaptialAccountService.getOwnDept(map, page, length);
    }



    /**
     * add by xxg 20210726
     * 根据所属柜台获取第一次设置的利率
     * @param ownDept  所属柜台
     * @return
     */
    @PostMapping("/getFirstRateByOwnDept")
    public FinInitrateDatadict getFirstRateByOwnDept(@RequestBody String ownDept){
        return mstCaptialAccountService.getFirstRateByOwnDept(ownDept);
    }



    @PostMapping("/getAcctInfo/{page}/{length}")
    public Page<SelectOptionDto> getAcctInfo(@RequestBody Map<String,Object> param, @PathVariable int page, @PathVariable int length){
        return mstCaptialAccountService.getAcctInfo(param, page, length);
    }

}
