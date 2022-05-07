/*
 * 文件名：TotalCheck001Controller.java
 * 版权：Copyright 2019-2020 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：魏玉强
 * 修改时间：2019年4月23日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */

package com.joyintech.tams.svreport.chb.datacheck.totalcheck.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.report.common.service.SvrBaseCommonService;
import com.joyintech.tams.svreport.chb.datacheck.totalcheck.dto.ChbTotalCheckDetailDto;
import com.joyintech.tams.svreport.chb.datacheck.totalcheck.dto.ChbTotalCheckDto;
import com.joyintech.tams.svreport.chb.datacheck.totalcheck.service.TotalCheck001Service;
import com.joyintech.tams.svreport.chb.datacheck.totalcheck.service.TotalCheckMService;

/**
 * 功能:总体核查
 * @since 1.7.0
 * @version 1.7.0
 * @作者：weiyuqiang
 * @创建时间：2019年4月23日
 * @修改记录：
 */
@Controller
@PageId("SvrTotalCheck001")
@RequestMapping("/chb/datacheck/totalcheck/TotalCheck001")
public class TotalCheck001Controller extends BaseController {
    
    @SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 共通Service
     */
    @Autowired
    private SvrBaseCommonService svrBaseCommonService;
    
    /**
     * 总体核查Service
     */
    @Autowired
    private TotalCheck001Service totalCheck001Service;
    
    /**
     * 总体核查Service
     */
    @Autowired
    private TotalCheckMService totalCheckMService;
    
	/**
	 * 页面初始化方法
	 */
	@Override
    @RequestMapping(PAGE_INIT)
	public String init(Model model) {
        Date queryDate = svrBaseCommonService.getSysDate();
        model.addAttribute("QUERY_DATE",DateUtil.format(queryDate));
		return super.init(model);
	}
	
    /**
     * 功能:查询阈值参数配置列表
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月23日
     * @return 
     * Map<String,String[]>
     */
    @ResponseBody
    @RequestMapping("/readConfig")
    public Map<String,String[]>  readConfig() {
        Map<String,String[]> resultMap=new HashMap<String,String[]>();
        resultMap = totalCheck001Service.readConfig();
        return resultMap;
    }
    
    /**
     * 功能:总体核查查询
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月24日
     * @return 
     * Page<AssetInfoRegist001Dto>
     */
    @ResponseBody
    @RequestMapping(SVReportGlobalConstant.SEARCH_PAGE)
    public Map<String,ChbTotalCheckDto> search() {
        Map<String, Object> param = RequestUtil.getPageParam();
        return totalCheckMService.searchData(param);
    }

    /**
     * 功能:总体核查查询明细列表
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月24日
     * @return 
     * Page<DelayVerification002Dto>
     */
    @ResponseBody
    @RequestMapping("/searchTable")
    public Page<ChbTotalCheckDetailDto> searchTable(){
        return totalCheckMService.searchTable(RequestUtil.getPageParam());
    }
    

    /**
     * 机构代码下拉框
     * @return 产品名称列表
     */
    @RequestMapping("/getIssuePartyidList")
    @ResponseBody
    public List<SelectOptionDto> getIssuePartyidList() {
//        String issuePartyId = CommonUtil.selectBankCode();
//        String issuePartyName = CommonUtil.selectBankCode();
        String issuePartyName = CacheUtil.getSysParam("SVR_ISSUE_PARTY_NAME");
        String issuePartyId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
        List<SelectOptionDto> dtoList = new ArrayList<SelectOptionDto>();
        SelectOptionDto issueParty = new SelectOptionDto();
        issueParty.setId(issuePartyId);
        issueParty.setText(issuePartyName);
        issueParty.setCol(issuePartyName);
        dtoList.add(issueParty);
        return dtoList;
    }
}
