/*
 * 文件名：PtlManagementInfo001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.portfolio.ptlmanagementinfo.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.portfolio.ptlmanagementinfo.dto.*;
import com.joyintech.service.portfolio.ptlmanagementinfo.service.PtlManagementInfo001Service;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.MstUserAndRoleService;
import com.joyintech.tams.component.base.core.dto.MstUserAndRoleOutputParameter;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockPtlPortfolioDto;
//import com.joyintech.tams.valuation.common.interf.common.ResultCode;
//import com.joyintech.tams.valuation.common.interf.common.ResultObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
/**
 * 组合管理查询
 * @author fanshuanghu
 * @version 1.0 2018年9月7日
 * @see PtlManagementInfo001Controller
 * @since 1.0
 */
@RestController("ptlManagementInfo001Controller_dtx")
@RequestMapping("/ptlmanagementinfo")
public class PtlManagementInfo001Controller extends BaseService<PtlManagementInfoDto>{
    /**注入service
     * 
     */
    @Autowired
    private PtlManagementInfo001Service ptlManagementInfo001Service;
    /**
     * 注入用户service
     */
    @Autowired(required = false)
    private MstUserAndRoleService mstUserAndRoleService;
    /**
     * 注入输出实体类
     */
    private MstUserAndRoleOutputParameter mstUserAndRoleOutputParameter = null;
    /**
     * 获取用户权限下拉框列表
     * @return 用户下拉框列表
     * @see
     * @since 1.0
     */
    @GetMapping("getUserList")
    public List<SelectOptionDto> getUserList(){
        return ptlManagementInfo001Service.getUserList();
    }
    /**
     * 获取组合下拉框列表
     * @param map 请求参数
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    @PostMapping("getPortfolioId")
    public List<SelectOptionDto> getPortfolioId(@RequestBody Map<String,Object> map){
        return ptlManagementInfo001Service.getPortfolioId(map);
    }
    /**
     * 获取已分配用户
     * @param map 组合代码
     * @return 已分配用户集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @PostMapping("selectUserNameList")
    public List<PtlManagementInfoDto> selectUserNameList(@RequestBody Map<String,Object> map){
        return ptlManagementInfo001Service.selectUserNameList(map);
    }
    
    /**
     * 画面查询方法
     * @param param 请求参数
     * @param page 页面
     * @param length 长度
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<PtlManagementInfoDto> selectAll(@RequestBody Map<String, Object> param, 
                                                @PathVariable("page") int page, @PathVariable("length") int length) {
        return ptlManagementInfo001Service.selectForPage(page,length,param);
    }
    /**
     * 获取组合列表集合
     * @param map 请求参数
     * @return 分页结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("getPortPage")
    public List<PtlManagementInfoDto> getPortPage(@RequestBody Map<String, Object> map) {
        return ptlManagementInfo001Service.getPortPage(map);
    }
    /**
     *  删除
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public boolean delete(@RequestBody PtlManagementInfoDto dto)
    {
        ptlManagementInfo001Service.delete(dto);
        return true;
    }
    /**
     * 证券账号信息删除
     * @param portfolioId 组合代码
     * @return 是否
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @GetMapping("secInfoDelete")
    public int secInfoDelete(String portfolioId){
        return ptlManagementInfo001Service.secInfoDelete(portfolioId);
    }
    /**
     * 插入
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("insert")
    public boolean insert(@RequestBody PtlManagementInfoDto dto)
    {
        ptlManagementInfo001Service.insert(dto);
//        return ptlManagementInfo001Service.insert(dto);
        return true;
    }

    /**
     * 更新
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("update")
    public boolean update(@RequestBody PtlManagementInfoDto dto)
    {
        ptlManagementInfo001Service.update(dto);
//        return ptlManagementInfo001Service.update(dto);
        return true;
    }

    /**
     * 更新
     * @param dto 页面选中的数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("updateInsertPortfolio")
    public boolean updateInsertPortfolio(@RequestBody PtlManagementInfoDto dto)
    {
//    	ResultObject resultObject = new ResultObject(ResultCode.SUCCESS);
        if (!StringUtil.equals(dto.getPortfolioType(), DictCodeListExt1.组合类型_子组合.getValue())) {
            ptlManagementInfo001Service.deleteUserId(dto.getPortfolioId());
            ptlManagementInfo001Service.insertAuthority(dto);
        }
        ptlManagementInfo001Service.insertPtlParam(dto);
        ptlManagementInfo001Service.delete(dto);
//        resultObject = ptlManagementInfo001Service.insert(dto);
        ptlManagementInfo001Service.insert(dto);
//        return resultObject;
        return true;
    }
    /**
     * 获取资金账号
     * @param param 请求参数
     * @return 下拉框实体类
     * @see
     * @since 1.0
     */
    @PostMapping("getAcctNo/{page}/{length}")
    public Page<SelectOptionDto> getAcctNo(@RequestBody Map<String, Object> param,
    		 @PathVariable("page") int page, @PathVariable("length") int length){
    	Page<SelectOptionDto> list = ptlManagementInfo001Service.getAcctNo(param,page,length);
        return list;
    }
    /**
     * 获取资金账号信息
     * @param acctNo 账号
     * @return 资金账号信息
     * @see
     * @since 1.0
     */
    @GetMapping("getCctInfo")
    public StlCapacctInfo getCctInfo(String acctNo){
        return ptlManagementInfo001Service.getCctInfo(acctNo);
    }
    /**
     * 获取证券账号
     * @param param 模糊查询参数
     * @return 下拉框实体类
     * @see
     * @since 1.0
     */
    @PostMapping("getSecAcctId")
    public List<SelectOptionDto> getSecAcctId(@RequestBody Map<String, Object> param){
        List<SelectOptionDto> list = ptlManagementInfo001Service.getSecAcctId(param);
        return list;
    }
    /**
     * 获取证券账户信息
     * @param secAcctNo 组合证券账号
     * @return 证券实习实体类
     * @see
     * @since 1.0
     */
    @GetMapping("getSecAcctInfo")
    public SecuritiesAccountInfoDto getSecAcctInfo(String secAcctNo){
        SecuritiesAccountInfoDto dto = ptlManagementInfo001Service.getSecAcctInfo(secAcctNo);
        return dto;
    }
    /**
     * 获取出事化信息
     * @param map 初始化map
     * @return 组合实体类
     * @see
     * @since 1.0
     */
    @PostMapping("getInitInfo")
    public PtlManagementInfoDto getInitInfo(@RequestBody Map<String,Object> map){ 
        PtlManagementInfoDto dto = ptlManagementInfo001Service.getInitInfo(map); 
        return dto;
    }
    /*add by liupengfei for 多级法人改造 默认选中 上级管理员用户和本级管理员 start*/
    @PostMapping("getAdminUserList")
    public List<String> getAdminUserList(@RequestBody Map<String,Object> param){
        return ptlManagementInfo001Service.getAdminUserList(param);
    }
    /*add by liupengfei for 多级法人改造 默认选中 上级管理员用户和本级管理员 end*/
    
    //TISHKBS-1941 设置默认用户为机构下所有用户，不止管理员
    //获取机构下所有用户
    @PostMapping("getUserList")
    public List<String> getUserList(@RequestBody Map<String,Object> param){
        return ptlManagementInfo001Service.getUserList(param);
    }
   
    //获取用户所在机构-银行模式下无法调用多级法人接口
    @PostMapping("getUserInstitution")
    public String getUserInstitution(@RequestBody String userName){
        return ptlManagementInfo001Service.getUserInstitution(userName);
    }
    
    
    /**
     * 资金账户信息删除
     * @param portfolioId 组合代码
     * @return 是否
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @GetMapping("funInfoDelete")
    public int funInfoDelete(String portfolioId){
        return ptlManagementInfo001Service.funInfoDelete(portfolioId);
    }
    /**
     * 资金信息插入
     * @param dto 实体类
     * @return 是否
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("funInfoInsert")
    public int funInfoInsert(@RequestBody FundAccountInfoDto dto){
        return ptlManagementInfo001Service.funInfoInsert(dto);
    }
    /**
     * 通过用户名删除
     * @param userId 用户名
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @GetMapping("deleteByUserId")
    public void deleteByUserId(String userId){
        ptlManagementInfo001Service.deleteByUserId(userId);
    }
    /**
     * 证券信息插入
     * @param dto 证券实体类
     * @return 是否
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("secInfoInsert")
    public int secInfoInsertf(@RequestBody SecuritiesAccountInfoDto dto){
        return ptlManagementInfo001Service.secInfoInsert(dto);
    }
    /**
     * 删除用户（通过组合）
     * @param portfolioId 组合代码
     * @return 是否
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @GetMapping("deleteUserId")
    public int deleteUserId(String portfolioId){
        return ptlManagementInfo001Service.deleteUserId(portfolioId);
    }
    @Transactional
    @DTXTransaction
    @DeleteMapping("deleteAction")
    public String deleteAction(String portfolioId){
        return ptlManagementInfo001Service.deleteAction(portfolioId);
    }
    @Transactional
    @DTXTransaction
    @PostMapping("insertAction")
    public String insertAction(@RequestBody PtlManagementInfoDto entity){
        return ptlManagementInfo001Service.insertAction(entity);
    }

    @Transactional
    @DTXTransaction
    @PostMapping("insertPtlParam")
    public String insertPtlParam(@RequestBody PtlManagementInfoDto entity){
        return ptlManagementInfo001Service.insertPtlParam(entity);
    }
    /**
     * 获取组合集合
     * @param map 请求集合
     * @return 组合集合
     * @see
     * @since 1.0
     */
    @PostMapping("getSelectedport")
    public List<PtlPortfolio> getSelectedport(@RequestBody Map<String,Object> map){
        return ptlManagementInfo001Service.getSelectedport(map);
    }
    /**
     * 
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("deleteCheck")
    public boolean deleteCheck(@RequestBody PtlManagementInfoDto dto){
        return ptlManagementInfo001Service.deleteCheck(dto);
    }


    
    @PostMapping("getInstitu")
    public List<NodeDto> getInstitu(@RequestBody Map<String,Object> map){
        return ptlManagementInfo001Service.getInstitu(map);
    }
    
    @PostMapping("getDep")
    public List<NodeDto> getDep(@RequestBody Map<String,Object> map){
        return ptlManagementInfo001Service.getDep(map);
    }
    
    @PostMapping("getUser")
    public List<NodeDto> getUser(@RequestBody Map<String,Object> map){
        return ptlManagementInfo001Service.getUser(map);
	}
    @Transactional
    @DTXTransaction
    @GetMapping("deleteFinCashInfoByFinprodId")
    public int deleteFinCashInfoByFinprodId(String finprodId){
        return ptlManagementInfo001Service.deleteFinCashInfoByFinprodId(finprodId);
    }

    @GetMapping("getFundAccountInfo")
    public List<PtlCapacctInfo> getFundAccountInfo(String portfolioId){
        return ptlManagementInfo001Service.getFundAccountInfo(portfolioId);
    }
    @GetMapping("getSecuritiesAccountInfo")
    public List<PtlSecAcct> getSecuritiesAccountInfo(String portfolioId){
        return ptlManagementInfo001Service.getSecuritiesAccountInfo(portfolioId);
    }
    @GetMapping("getAuthority")
    public List<PtlPortfolioAuthority> getAuthority(String portfolioId){
        return ptlManagementInfo001Service.getAuthority(portfolioId);
    }

    /**
     * 获取投资策略
     * @param map 请求参数
     * @return 投资策略下拉框集合
     * @see
     * @since 1.0
     */
    @PostMapping("/getStrategy")
    public List<SuggestDto> getStrategy(@RequestBody Map<String,Object> map){
        return ptlManagementInfo001Service.getStrategy(map);
    }
    /**
     * 获取子组合
     * @param portfolioId 请求参数
     * @return 子组合集合
     * @see
     * @since 1.0
     */
    @GetMapping("getChildPortfolio")
    public List<PtlManagementInfoDto> getChildPortfolio(String portfolioId){
        return ptlManagementInfo001Service.getChildPortfolio(portfolioId);
    }

    @PostMapping("/getPortfolio")
    public PtlPortfolio getPortfolio(@RequestBody Map<String,Object> map){
        return ptlManagementInfo001Service.getPortfolio(map.get("portfolioId").toString());
    }

    /**
     * 修改子组合信息
     * @param map
     */
    @Transactional
    @DTXTransaction
    @PostMapping("/updateChildPortfolio")
    public int updateChildPortfolio(@RequestBody Map<String,Object> map){
        return ptlManagementInfo001Service.updateChildPortfolio(map);
    }

    /**
     * 创建子组合代码
     * @param map 序列名
     * @return
     */
    @Transactional
    @DTXTransaction
    @PostMapping("/createChildPortfolioId")
    public String createChildPortfolioId(@RequestBody Map<String,Object> map){
        return ptlManagementInfo001Service.createChildPortfolioId(map.get("portfolioId").toString());
    }
    @Transactional
    @DTXTransaction
    @PostMapping("/updateInvestYesorno")
    public int updateInvestYesorno(@RequestBody Map<String,Object> map) {
        return ptlManagementInfo001Service.updateInvestYesorno(map);
    }
    @Transactional
    @DTXTransaction
    @PostMapping("/setParentPortfolioId")
    public List<PtlManagementInfoDto> setParentPortfolioId(@RequestBody Map<String,Object> map) {
        return ptlManagementInfo001Service.setParentPortfolioId(map);
    }

    @PostMapping("/selectPortfolioId")
    public List<SelectOptionDto> selectPortfolioId(@RequestBody Map<String,Object> map) {
        return ptlManagementInfo001Service.selectPortfolioId(map);
    }

    /**
     * 添加用户权限
     * @param entity
     * @return
     */
    @Transactional
    @DTXTransaction
    @PostMapping("/insertAuthority")
    public int insertAuthority(@RequestBody PtlManagementInfoDto entity){
        return ptlManagementInfo001Service.insertAuthority(entity);
    }

    /**
     * 生成内部交易对手
     * @param entity
     * @return
     */
    @Transactional
    @DTXTransaction
    @PostMapping("/createCounter")
    public int createCounter(@RequestBody PtlManagementInfoDto entity){
        return ptlManagementInfo001Service.createCounter(entity);
    }
    /**
     * 删除内部交易对手
     * @param entity
     * @return
     */
    @Transactional
    @DTXTransaction
    @PostMapping("/deleteByCounterId")
    public int deleteByCounterId(@RequestBody PtlManagementInfoDto entity){
        return ptlManagementInfo001Service.deleteByCounterId(entity);
    }
    @Transactional
    @DTXTransaction
    @PostMapping("/doInsertAction")
    public String  doInsertAction(@RequestBody PtlManagementInfoDto entity){
        return ptlManagementInfo001Service.doInsertAction(entity);
    }
    @Transactional
    @DTXTransaction
    @PostMapping("/doDeleteAction")
    public String  doDeleteAction(@RequestBody PtlManagementInfoDto entity){
        return ptlManagementInfo001Service.doDeleteAction(entity);
    }
    @Transactional
    @DTXTransaction
    @PostMapping("/doComplete003")
    public String doComplete003(@RequestBody PtlManagementInfo003DataDto entity){
        return ptlManagementInfo001Service.doComplete003(entity);
    }
    @Transactional
    @DTXTransaction
    @PostMapping("/insertUser")
    public String insertUser(@RequestBody PtlManagementInfo003DataDto entity){
        return ptlManagementInfo001Service.insertUser(entity);
    }

    @Transactional
    @DTXTransaction
    @PostMapping("/autoRelate")
    public void  autoRelate(@RequestBody PtlManagementInfoDto entity){
        ptlManagementInfo001Service.autoRelate(entity);
    }

    /**
     * 投管接口（组合信息输入接口）
     * 
     * @param entity 参数
     * @return 是否成功
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("/insertPortfolioInfo")
    public String insertPortfolioInfo(@RequestBody PtlManagementInfoDto entity) {
        return ptlManagementInfo001Service.insertPortfolioInfo(entity);
    }
    @Transactional
    @DTXTransaction
    @PostMapping("/insertPtlSecAcct")
    public String insertPtlSecAcct(@RequestBody PtlSecAcct entity) {
        return ptlManagementInfo001Service.insertPtlSecAcct(entity);
    }
    @Transactional
    @DTXTransaction
    @PostMapping("/insertPtlCapacctInfo")
    public String insertPtlCapacctInfo(@RequestBody PtlCapacctInfo entity) {
        return ptlManagementInfo001Service.insertPtlCapacctInfo(entity);
    }

    /**
     * 资金账户已经有通道挂的话，不允许再挂
     * @param map
     * @return
     */
    @PostMapping("/getReAcct")
    public int getReAcct(@RequestBody Map<String, Object> map){
        return ptlManagementInfo001Service.getReAcct(map);
    }
	
	/**
     *
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("selectAllBaseRule")
    public List<FinBaseRuleInfo> selectAllBaseRule(@RequestBody Map<String, Object> param)
    {
        return ptlManagementInfo001Service.selectAllBaseRule(param);
    }

    /**
     * 模拟试算 组合信息基本服务
     * 参数：portfolioId： 组合代码（String）
     * 参数：paramDate： 计算日期（String yyyy-MM-dd）
     *
     * @param param 参数
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("selectPortfolioDetailInfo")
    public MockPtlPortfolioDto selectPortfolioDetailInfo(@RequestBody Map<String, Object> param)
    {
        return ptlManagementInfo001Service.selectPortfolioDetailInfo(param);
    }

    /**
     * 模拟试算
     * 查询基准来源为利率时所有的业绩基准
     * @param param 参数map
     * @return 实体类
     * @see
     * @since 1.0
     */
    @PostMapping("selectAlllv")
    public List<MstIntRateInfo> selectAlllv(@RequestBody Map<String, Object> param)
    {
        return ptlManagementInfo001Service.selectAlllv(param);
    }

    @PostMapping("getPtlAuthInfo")
    public Map<String, Object>  getPtlAuthInfo(@RequestBody Map<String, Object> param){
        return ptlManagementInfo001Service.getPtlAuthInfo(param);
    }

}
