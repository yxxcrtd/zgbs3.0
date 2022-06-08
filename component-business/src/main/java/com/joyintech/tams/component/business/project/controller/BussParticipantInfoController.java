/*
 * 文件名：BussParticipantInfoController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年11月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.business.project.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.project.BussParticipantInfoService;
import com.joyintech.tams.component.business.project.BussPostInvestInfoService;
import com.joyintech.tams.component.business.project.dto.BussParticipantInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 *  参与方信息--业务组件
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年11月6日
 * @see BussParticipantInfoController
 * @since 1.0
 */
@Controller
@RequestMapping("/component/project/BussParticipantInfo")
public class BussParticipantInfoController extends BaseController{

    @Autowired
    private BussParticipantInfoService bussParticipantInfoService;
    
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;
    
    @Autowired
    private CommonBusinessService commonBusinessService;
    
    @Override
    public String init(Model model) {
        model.addAttribute("entityName", StringUtil.defaultString(this.getParam("entityName"), BussPostInvestInfoService.DEFAULT_ENTITY_NAME));
        model.addAttribute("COUNTER_TYPE", DictCodeListExt1.交易对手类型_通道计划.getValue());
        model.addAttribute("workflowId", this.getParam("workflowId"));
        model.addAttribute("pFinprodId", this.getParam("pFinprodId"));
        String finprodId = "";
        if (StringUtil.isNotEmpty(this.getParam("pFinprodId")) && 
                !StringUtil.equals("null", this.getParam("pFinprodId"))){
            finprodId = this.getParam("pFinprodId");
        }else {
            finprodId = this.getParam("finprodId");
        }
        //判断该资产代码是否已经做过交易  // Sonar Fix modify by huwei 220608
        if(null != commonBusinessService && null!=this.getPageModel() && this.getPageModel().equals("1") && StringUtil.isNotEmpty(finprodId)){
            if(commonBusinessService.isTransInAsset(finprodId)>0){
            	  model.addAttribute("isTransInAsset", "Y");
            }
          }
        // 设置画面初始值
        return "component/project/BussParticipantInfo";
    }
    
    
    /**
     * 查询参与方列表
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getParticipantInfo")
    public List<BussParticipantInfoDto> getParticipantInfo() {
        List<BussParticipantInfoDto> bussParticipantInfoList = new ArrayList<BussParticipantInfoDto>();
        String workflowId = this.getParam("workflowId");
        //子计划查询父计划的通道信息 yjm start 20190830
        String finprodId = "";
        if (StringUtil.isNotEmpty(this.getParam("pFinprodId")) && 
                !StringUtil.equals("null", this.getParam("pFinprodId"))){
            finprodId = this.getParam("pFinprodId");
        }else {
            finprodId = this.getParam("finprodId");
        }
        //子计划查询父计划的通道信息 yjm end 20190830
        String entityName = StringUtil.defaultString(this.getParam("entityName"),
            BussParticipantInfoService.DEFAULT_ENTITY_NAME);
        if (StringUtil.isNotEmpty(workflowId)) {
            BussParticipantInfoDto dto = mstWorkflowCommonService.getBusinessEntityByWorkFlowId(
                workflowId, entityName, BussParticipantInfoDto.class);
            if(null!=dto){
                bussParticipantInfoList =  dto.getBussParticipantInfoDtoList();
            }
            return bussParticipantInfoList;
        }
        else if (StringUtil.isNotEmpty(finprodId)) {
            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("finprodId", finprodId);
            paramMap.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_通道.getValue());
            paramMap.put("isChl", DictCodeList.是否_是.getValue());
            paramMap.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
            paramMap.put("cashType02c11", DictCodeListExt1.现金流二级类型_通道托管费.getValue());
            paramMap.put("cashType02c12", DictCodeListExt1.现金流二级类型_通道管理费.getValue());
            paramMap.put("cashType02c13", DictCodeListExt1.现金流二级类型_通道其他费.getValue());
            bussParticipantInfoList =  bussParticipantInfoService.queryBussParticipantInfo(paramMap);
            return bussParticipantInfoList;
        }
        return Collections.emptyList();
    }
    
    /**
     * 参与方费率行转列
     * <功能详细描述>
     * @param bussParticipantInfoList
     * @return
     * @see
     * @since 1.0
     */
    @Deprecated
    private List<BussParticipantInfoDto> hangleBussParticipantInfo(List<BussParticipantInfoDto> bussParticipantInfoList){
        List<BussParticipantInfoDto> resultList = new ArrayList<BussParticipantInfoDto>();
        List<String> counterIdList = new ArrayList<String>();
        HashSet<String> set = new HashSet<String>();
        for(BussParticipantInfoDto entity:bussParticipantInfoList){
            if (set.add(entity.getCounterId())) {
                counterIdList.add(entity.getCounterId());
            }
        }
        for(String counterId:counterIdList){
            BussParticipantInfoDto bussParticipantInfoDto = new BussParticipantInfoDto();
            for(BussParticipantInfoDto entity:bussParticipantInfoList){
                if(StringUtil.equals(counterId, entity.getCounterId())){
                   if(StringUtil.equals(entity.getCashType2(), DictCodeListExt1.现金流二级类型_通道托管费.getValue())){
                       bussParticipantInfoDto.setClientYield(entity.getYield());
                   } 
                   if(StringUtil.equals(entity.getCashType2(), DictCodeListExt1.现金流二级类型_通道管理费.getValue())){
                       bussParticipantInfoDto.setManagerYield(entity.getYield());
                   }
                   if(StringUtil.equals(entity.getCashType2(), DictCodeListExt1.现金流二级类型_通道其他费.getValue())){
                       bussParticipantInfoDto.setOtherYield(entity.getYield());
                   }
                   bussParticipantInfoDto.setCounterId(entity.getCounterId());
                   bussParticipantInfoDto.setCounterName(entity.getCounterName());
                   bussParticipantInfoDto.setManagerCustomerName(entity.getManagerCustomerName());
                   bussParticipantInfoDto.setClientCustomerName(entity.getClientCustomerName());
                   bussParticipantInfoDto.setChlLevel(entity.getChlLevel());
                   bussParticipantInfoDto.setBusiAcctNo(entity.getBusiAcctNo());
                   bussParticipantInfoDto.setContact(entity.getContact());
                   bussParticipantInfoDto.setContactWay(entity.getContactWay());
                   bussParticipantInfoDto.setFinprodId(entity.getChlFinprodId());
                   bussParticipantInfoDto.setRemark(entity.getRemark());
                }
            }
            resultList.add(bussParticipantInfoDto);
        }
        return resultList;
    }
    /**
     * 根据合作方代码查询基本信息
     * <功能详细描述>
     * @param counterId  合作方代码
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("queryAllinfoByfinprodId")
    public BussParticipantInfoDto queryAllinfoByfinprodId(String counterId) {
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("cashType02c11", DictCodeListExt1.现金流二级类型_通道托管费.getValue());
        paramMap.put("cashType02c12", DictCodeListExt1.现金流二级类型_通道管理费.getValue());
        paramMap.put("cashType02c13", DictCodeListExt1.现金流二级类型_通道其他费.getValue());
        paramMap.put("finCashBusinessType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        paramMap.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_通道.getValue());
        paramMap.put("isChl", DictCodeList.是否_是.getValue());
        paramMap.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
        paramMap.put("counterType", DictCodeListExt1.交易对手类型_通道计划.getValue());
        paramMap.put("acctType", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        paramMap.put("counterId", counterId);
        return bussParticipantInfoService.queryAllinfoByfinprodId(paramMap);
    }
    
    @ResponseBody
    @RequestMapping("selectAcctNoByCounterId")
    public List<SelectOptionDto> selectAcctNoByCounterId(String initValue,String counterId){
        Map<String,Object> map = RequestUtil.getPageParam();
        if(StringUtil.isNotEmpty(map.get("counterId"))){
            return bussParticipantInfoService.selectAcctNoByCounterId(map);
        }
        return Collections.emptyList();
    }
}
