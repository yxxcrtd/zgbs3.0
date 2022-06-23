/*
 * 文件名：ValPublicDataImport001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2018年3月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
*/
package com.joyintech.tams.valuation.dailyoperation.dailyaccountmake.valdataimport.controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.entity.SysBatchGrpPrdlink;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.accounadjustmanage.booksetdateswitch.dto.ValBooksetDateSwitch001Dto;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.service.BaseCommonService;
import com.joyintech.tams.valuation.dailyoperation.dailyaccountmake.valaccounthandle.service.ValAccountHandle001Service;
import com.joyintech.tams.valuation.dailyoperation.dailyaccountmake.valaccounthandle.service.ValAccountHandleService;
import com.joyintech.tams.valuation.dailyoperation.dailyaccountmake.valdataimport.dto.ValPublicDataImport001LogDto;
import com.joyintech.tams.valuation.dailyoperation.dailyaccountmake.valdataimport.service.ValPublicDataImport001Service;
import com.joyintech.tams.valuation.interf.inertfStartUp.service.ValInertfStartUp001Service;

/**
 * 模板示例<br>
 * 账套管理
 * @author 何阳阳
 * @version 1.0 2018年3月13日
 * @see ValTransactionDataImport001
 * @since 1.0
 */

@Controller
@PageId("ValTransactionDataImport001")
@RequestMapping("/valuation/dailyoperation/dailyaccountmake/valdataimport/ValTransactionDataImport001")
public class ValTransactionDataImport001 extends BaseController {
	
	//代表交易查询
	private static String InterfFlag = "InterfDeal";
	
	@Autowired
	private ValPublicDataImport001Service valPublicDataImport001Service;
	@Autowired
	private ValInertfStartUp001Service valInertfStartUp001Service;
	@Autowired
	private ValAccountHandle001Service valAccountHandle001Service;
    @Autowired
    private BaseCommonService baseCommonService;
    
    @Autowired
    private InterfCommonService interfCommonService;
    
	@Autowired
	private ValAccountHandleService valAccountHandleService;
    
	/**
     * 页面初始化，显示画面<br>
     * 
     * @param model Model
     * @return 初始化路径
     * @see
     * @since 1.0
     */
    public String init(Model model) {
        String sysDate=baseCommonService.getSysDateString();
        if(sysDate==null||"".equals(sysDate)){
            sysDate=DateUtil.format(new Date());
        }
        model.addAttribute("sysDate", sysDate);
        this.setSelectOptionToModel(model, DictCode.批处理账套类型);
        return super.init(model);
    }
    
    
    @RequestMapping("checkExecute")
    @ResponseBody
    public int checkExecute(){
        Map<String,Object> map = RequestUtil.getPageParam();
        String booksetId = (String)map.get("booksetId");
        
        String startDate = (String) map.get("startDate");
        if(GlobalConstant.BATCH_EXECUTE_TYPE_GROUP.equals(map.get("batchBooksetType"))) {
        	Map<String, Object> paramMap = Maps.newHashMap();
        	paramMap.put("booksetGroup", map.get("booksetGroup"));
        	List<SysBatchGrpPrdlink> list = valAccountHandle001Service.findAllProduct(paramMap);
        	booksetId = list.stream().map(SysBatchGrpPrdlink::getProductId).distinct().collect(Collectors.joining(","));
        }else {
        	booksetId = (String) map.get("booksetId");
        }
//        if("INTERF_MODEL_SINGLE".equals(map.get("valModel")) && StringUtil.isNotEmpty(startDate)) {// startDate
    	Map<String, Object> paramMap = Maps.newHashMap();
    	paramMap.put("booksetId", booksetId);
    	paramMap.put("booksetType", DictCodeList.账套类别_理财账套.getValue());
    	List<ValBooksetDateSwitch001Dto> list = valAccountHandleService.findBooksetDate(paramMap);
    	list = list.stream().filter(entity -> !startDate.equals(entity.getBooksetDate())).collect(Collectors.toList());
    	if(CollectionUtils.isNotEmpty(list)) {
    		return -1; // 如果交易导入日期与账套或者账套组中任何一个产品账套日期不一致，不允许导入
    	}
//        }
        return valAccountHandle001Service.checkExecute(map);
    }
    
    @RequestMapping("/getInterGroup")
    @ResponseBody
    public List<TreeNode> getInterGroup(){
    	Map<String,Object> map = RequestUtil.getPageParam();
    	map.put("batchName", getParam("TmpCName"));
    	map.put("taskGrpType", InterfFlag);
    	List<TreeNode> treeList = valAccountHandle001Service.getTreeNodeList(map);
    	return treeList;
    }
    
    @RequestMapping("/getInterGroupCode")
    @ResponseBody
    public List<SelectOptionDto> getInterGroupCode(){
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("TmpCName", getParam("TmpCName"));
    	List<SelectOptionDto>  list = valPublicDataImport001Service.getInterGroupCode(map);
    	return list;
    }
    
    
    @RequestMapping("/findLogPage")
    @ResponseBody
    public List<ValPublicDataImport001LogDto> findLogPage(){
    	List<ValPublicDataImport001LogDto>  list = new ArrayList<>();
    	Map<String,Object> map = RequestUtil.getPageParam();
    	//map.put("createDate", DateUtil.parse("20180815"));
    	map.put("status", this.getParam("status"));
    	map.put("execTaskId", this.getParam("execTaskId"));
    	if(StringUtil.isNotEmpty(map.get("exDate"))){
        	map.put("createDate", new Date(Long.valueOf(map.get("exDate").toString())));
    		list = valPublicDataImport001Service.getInterActionLog(map);
    	}
    	return list;
    }
    
    
    @RequestMapping("/stopInterf")
    @ResponseBody
    public int stopInterf(){
    	Map<String, Object> map = new HashMap<>();
    	return valPublicDataImport001Service.stopInterLog(map);
    }
    
    
    /**
     * 接口启动
     * 
     * @throws IOException
     * @see
     * @since 1.0
     */
    @RequestMapping("runAs")
    @ResponseBody
    public String runAs()
            throws IOException {
        JsonObject json = new JsonObject();
        int check = 0;
        Date date = new Date();
        Map<String,Object> map = new HashMap<String,Object>();
        String execTaskId = interfCommonService.getUUid();
        //检查当前是否有正在运行中的批处理
        if(valPublicDataImport001Service.checkInterLog(map)==0){
            String datas = this.getParam("datas");
            String startDate = this.getParam("startDate");
            String endDate = this.getParam("endDate");
            String url = valInertfStartUp001Service.getUrl(null);
            String valModel = this.getParam("valModel");
            /**modify by wujie on 20190521 start
             String booksetId = StringUtils.EMPTY;
             if(GlobalConstant.BATCH_EXECUTE_TYPE_GROUP.equals(getParam("batchBooksetType"))) {
             Map<String, Object> paramMap = Maps.newHashMap();
             paramMap.put("booksetGroup", getParam("booksetGroup"));
             List<SysBatchGrpPrdlink> list = valAccountHandle001Service.findAllProduct(paramMap);
             List<String> bookSetIds = new ArrayList<>();
             if(!StringUtil.isEmptyList(list)){
             list.forEach(i -> bookSetIds.add(i.getProductId()));
             }
             booksetId = valAccountHandle001Service.getBooksetIdbyLayeringInfo(bookSetIds).stream().collect(Collectors.joining(","));
             }else {
             List<String> bookSetIds = Arrays.asList(this.getParam("booksetId").split(","));
             booksetId = valAccountHandle001Service.getBooksetIdbyLayeringInfo(bookSetIds).stream().collect(Collectors.joining(","));
             }
             Map<String, String> urlParams = valPublicDataImport001Service.getParm(datas, DateUtil.formatDate(startDate),
             DateUtil.formatDate(endDate),booksetId,valModel);
             **/
            String batchBooksetType = getParam("batchBooksetType");
            String booksetIds = StringUtils.EMPTY;
            if(GlobalConstant.BATCH_EXECUTE_TYPE_GROUP.equals(getParam("batchBooksetType"))) {
                booksetIds = getParam("booksetGroup");
            }else{
                booksetIds = getParam("booksetId");
            }
            Map<String, String> urlParams = valPublicDataImport001Service.getParm(datas, DateUtil.formatDate(startDate),
                    DateUtil.formatDate(endDate),null,valModel,batchBooksetType,booksetIds);
            /**modify by wujie on 20190521 end**/
            urlParams.put(InterfCommonConstant.接口_任务执行ID, execTaskId);
            valInertfStartUp001Service.runInterf(url, urlParams);
            check=1;
        }

        json.addProperty("isCheck", check);
        json.addProperty("createDate", date.getTime());
        json.addProperty("execTaskId", execTaskId);

        return json.toString();
    }

    /**
     * 产品下拉框数据选择
     *
     * @return 返回的数据
     * @exception Exception
     *                异常
     * @since 1.0
     */
    @RequestMapping("/getProdId")
    @ResponseBody
    public Page<SelectOptionDto> getProdId() {
    	/**modify by wujie on 20190325 start
        return valAccountHandle001Service.getProdId(RequestUtil.getPageParam());**/
        Map<String,Object> parmasMap = RequestUtil.getPageParam();
        parmasMap.put("acctSubjectTypeNo", DictCodeListExt1.金融产品类型_通道.getValue());
        return valAccountHandle001Service.getProdId(parmasMap);
        /**modify by wujie on 20190325 end**/
    }

    /**
     * 查询账套组
     * @return 套账日期
     * @throws Exception
     * @see
     */
    @RequestMapping("findBooksetGroup")
    @ResponseBody
    public Page<SelectOptionDto> findBooksetGroup()  {
        return valAccountHandle001Service.findBooksetGroup(RequestUtil.getPageParam());
    }
    
}