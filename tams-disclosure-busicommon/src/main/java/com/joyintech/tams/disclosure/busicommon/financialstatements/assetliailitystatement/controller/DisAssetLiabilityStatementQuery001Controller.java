/*
 * 文件名：AssetLiabilityStatementQuery001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年9月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.disclosure.busicommon.financialstatements.assetliailitystatement.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.disclosure.busicommon.common.constants.DictCode;
import com.joyintech.tams.disclosure.busicommon.common.constants.DictCodeList;
import com.joyintech.tams.disclosure.busicommon.common.constants.GlobalConstant;
import com.joyintech.tams.disclosure.busicommon.common.service.DisValAssetBook001Service;
import com.joyintech.tams.disclosure.busicommon.financialstatements.assetliailitystatement.common.DisAssetLiabilityStatementUtil;
import com.joyintech.tams.disclosure.busicommon.financialstatements.assetliailitystatement.dto.DisAssetLiabilityStatementQuery001Dto;
import com.joyintech.tams.disclosure.busicommon.financialstatements.assetliailitystatement.dto.DisAssetLiabilityStatementQuery001SearchDto;
import com.joyintech.tams.disclosure.busicommon.financialstatements.assetliailitystatement.service.DisAssetLiabilityStatementQuery001Service;

@Controller
@PageId("DisAssetLiabilityStatementQuery001")
@RequestMapping("/disclosure/financialstatement/assetliailitystatement/DisAssetLiabilityStatementQuery001")
public class DisAssetLiabilityStatementQuery001Controller extends BaseController {

    @Autowired
    private DisAssetLiabilityStatementQuery001Service service;

    @Autowired
    private DisValAssetBook001Service valAssetBook001Service;
    
//    @Autowired
//    private BooksetCommonService booksetCommonService;


    @Override
    public String init(Model model) {
        super.setSelectOptionToModel(model, DictCode.报表期限类型);
        this.setSelectOptionToModel(model, DictCode.报表维度);
        return super.init(model);
    }

    /**
     * 查询条件检查
     * 
     * @return
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Valid DisAssetLiabilityStatementQuery001SearchDto dto, BindingResult result) {
    	// 当选择单账套时
        if ("01".equals(dto.getReportDimen())) {
        	// 账套代码不能为空
        	if (StringUtil.isEmpty(dto.getBooksetId())) {
        		return new AjaxResult("请输入账套代码。", MessageType.错误消息);
        	}
        	// 账套代码必须存在
        	String[] booksetIds = new String[1];
        	booksetIds[0] = dto.getBooksetId();
        	
        	//TODO
//        	List<Product> booksetList = booksetCommonService.findProductByIds(booksetIds);
//        	if (booksetList == null || booksetList.isEmpty()) {
//        		return new AjaxResult("账套代码不存在。", MessageType.错误消息);
//        	}
        }
        if (result != null && !result.hasErrors()) {// 基础验证通过
            String reportTermType = dto.getReportTermType();// 报表期限类型
            String reportTerm = dto.getReportTerm();// 报表期限明细 : 半年，季度明细

            if ((DictCodeListExt1.报表期限类型_半年报.getValue().equals(reportTermType)
                 || DictCodeListExt1.报表期限类型_季报.getValue().equals(reportTermType))
                && StringUtils.isEmpty(reportTerm)) {// 报表期限类型为半年报或季报时，报表期限不能为空
                return new AjaxResult("期限不能为空！", MessageType.错误消息);
            }
        }
        return commonCheck(result, "");
    }

    /**
     * 检索账套代码(只取理财账套)
     * 
     * @return 账套代码联想框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getBooksetId2")
    public List<SuggestDto> getBooksetId2(){
        String param = super.getRequest().getParameter("param");
        return valAssetBook001Service.getBooksetId2(param);
    }

    /**
     * 联动：报表期限类型 - 季度 半年
     * 
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("getTermLink")
    public List<SelectOptionDto> getTradeMarket(){
        Map<String, Object> param = RequestUtil.getPageParam();
        String reportTermType = (String)param.get("reportTermType");

        param.put("dict_code_parent", DictCode.报表期限类型.getValue());
        param.put("item_code_parent", param.get("reportTermType"));
        if (DictCodeListExt1.报表期限类型_半年报.getValue().equals(reportTermType)) {
            param.put("dict_code_child", DictCode.半年.getValue());
            return service.getLinkSelectOption(param);
        }
        if (DictCodeListExt1.报表期限类型_季报.getValue().equals(reportTermType)) {
            param.put("dict_code_child", DictCode.季度.getValue());
            return service.getLinkSelectOption(param);
        }
        return null;
    }

    /**
     * 获得 表头信息
     * 
     * @return
     * @throws ParseException
     */
    @ResponseBody
    @RequestMapping("getHeadInfo")
    public Map<String, String> getHeadInfo(@RequestBody DisAssetLiabilityStatementQuery001SearchDto dto)
        throws ParseException {
        Map<String, String> map = Maps.newHashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date reportDate = service.analyseBalanceDate(dto, DisAssetLiabilityStatementUtil.ENDING_BALANCE);

        map.put("statementDate", sdf.format(reportDate));
        map.put("statementType", SysDataUtil.getNameFromDataDict(DictCode.报表期限类型, dto.getReportTermType()));
       // map.put("org", CacheUtil.getSysParam(GlobalConstant.财务报表_编制单位));
        //多级法人改造start add by hxy 20200619
         if(MultilevelDataAuthorityUtil.isEnabled()) {
	        	 //联社模式
	        	 //根据选择的账套组或者账套，查询出它们的所属机构
	        	String booksetOrg;
	        	if ("02".equals(dto.getReportDimen())) {
	            	if(null!=dto && null!=dto.getAccountList() && dto.getAccountList().size()>0){
		       			booksetOrg=dto.getAccountList().get(0).getCode();
		           		String orgName=valAssetBook001Service.selectAssociationModeOrgName(booksetOrg);
		               	map.put("org", orgName);
		       		}
	        	}else{
	        		booksetOrg=dto.getBooksetId();
	        		String orgName=valAssetBook001Service.selectAssociationModeOrgName(booksetOrg);
	            	map.put("org", orgName);
	        	}
	        /*	String org =   MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
	            String orgName=CacheUtil.getAppCacheInstitutionCodeName(org).toString();
	        	map.put("org", orgName);*/
        	}else{
        		//银行模式
            	String orgName=valAssetBook001Service.selectBankModeOrgName();
            	map.put("org", orgName);
        	
           }
        //多级法人改造end add by hxy 20200619
        return map;
    }

    /**
     * 获得 资产
     * 
     * @return
     * @throws ParseException
     */
    @ResponseBody
    @RequestMapping("findAssets")
    public List<DisAssetLiabilityStatementQuery001Dto> findAssets(DisAssetLiabilityStatementQuery001SearchDto dto)
        throws ParseException {
    	List<DisAssetLiabilityStatementQuery001Dto> dtoList = new ArrayList<DisAssetLiabilityStatementQuery001Dto>();
    	Map<String, Object> paraMap = RequestUtil.getParameters(getRequest());
    	// 报表维度为单账套
    	if ("01".equals(String.valueOf(paraMap.get("reportDimen")))) {
    		dtoList = service.findAssetsStatement(dto);
    	} else {
    		// 报表维度为全账套
    		dto.setAccountList(valAssetBook001Service.getBooksetId2(super.getRequest().getParameter("param")));
    		dtoList = service.findAssetsStatementAll(dto);
    	}

        if(null!=dtoList) {
            dtoList.add(new DisAssetLiabilityStatementQuery001Dto());
            dtoList.add(new DisAssetLiabilityStatementQuery001Dto());
            dtoList.add(new DisAssetLiabilityStatementQuery001Dto());
            dtoList.add(new DisAssetLiabilityStatementQuery001Dto());
            dtoList.add(new DisAssetLiabilityStatementQuery001Dto());
            dtoList.addAll(DisAssetLiabilityStatementUtil.getAssetListSum(dtoList));

            dtoList.forEach(item -> {
                if (StringUtils.isEmpty(item.getItemName())) {
                    item.setInitialBalance(null);
                    item.setEndingBalance(null);
                }
            });
        }
        
        // 全账套情况下,添加样式
        if ("02".equals(String.valueOf(paraMap.get("reportDimen")))) {
    		service.dealStyle(dtoList);
    	}

        return dtoList;
    }

    /**
     * 获得 负债和所有者权益
     * 
     * @return
     * @throws ParseException
     */
    @ResponseBody
    @RequestMapping("findLiabilityInterest")
    public List<DisAssetLiabilityStatementQuery001Dto> findLiabilityInterest(DisAssetLiabilityStatementQuery001SearchDto dto)
        throws ParseException {
    	List<DisAssetLiabilityStatementQuery001Dto> dtoList = new ArrayList<DisAssetLiabilityStatementQuery001Dto>();
    	Map<String, Object> paraMap = RequestUtil.getParameters(getRequest());
    	// 报表维度为单账套
    	if ("01".equals(String.valueOf(paraMap.get("reportDimen")))) {
    		dtoList = service.findLiabilityInterestStatement(dto);
    	} else {
    		// 报表维度为全账套
    		dto.setAccountList(valAssetBook001Service.getBooksetId2(super.getRequest().getParameter("param")));
    		dtoList = service.findLiabilityInterestStatementAll(dto);
    	}
    	if(null!=dtoList) {
            dtoList.addAll(DisAssetLiabilityStatementUtil.getLiabilityInterestListSum(dtoList));
        }
    	
    	// 全账套情况下,添加样式
    	if ("02".equals(String.valueOf(paraMap.get("reportDimen")))) {
    		service.dealStyle(dtoList);
    	}
        return dtoList;
    }

    /**
     * 导出信息excel
     * 
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export(DisAssetLiabilityStatementQuery001SearchDto dto) throws IOException, ParseException {
    	Map<String, Object> paraMap = RequestUtil.getPageParam();
    	if ("02".equals(paraMap.get("reportDimen"))) {
    		List<SuggestDto> accountList = valAssetBook001Service.getBooksetId2(super.getRequest().getParameter("param"));
    		dto.setAccountList(accountList);
    	}
        return service.exportExcel(dto);

    }
}
