/*
 * 文件名：MstMarketInstitutionsValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年8月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.basicdata.mstmarketinstitutions.validator;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.MstBondCreditRating;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.basicdata.mstmarketinstitutions.dto.MstMarketInstitutionsDto;
import com.joyintech.tams.invest.basicdata.mstmarketinstitutions.service.MstMarketInstitutionsService;
import com.joyintech.tams.invest.common.service.InvDataCheckService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author huwenbin
 * @version 1.0 2018年8月20日
 * @see MstMarketInstitutionsValidator
 * @since 1.0
 */
@Component
public class MstMarketInstitutionsValidator implements BeanValidator {

    /**
     * 市场机构管理service注入
     */
    @Autowired
    private MstMarketInstitutionsService mstMarketInstitutionsService;

    @Autowired
    private InvDataCheckService chbInvDataCheckService;

    @Override
    public List<ValidMessage> valid(Object value) {
        MstMarketInstitutionsDto dto = (MstMarketInstitutionsDto)value;
        List<ValidMessage> errorList = new ArrayList<>();
        if (WorkFlowUtil.isNotFromToDoList()) {
            if (StringUtil.equals(dto.getOperateType(), "delete")) {
                // 行删除按钮与删除按钮校验
                List<MstMarketInstitutionsDto> listDto = dto.getSelectedItem();
                List<MstMarketInstitutionsDto> templistDto = mstMarketInstitutionsService.checkAllCodeIsUse(
                    listDto);
                // 批量删除时检查数据是否进行过客户性质匹配
                List<MstMarketInstitutionsDto> deleteCheckList = mstMarketInstitutionsService.deleteCheck(
                        listDto);
                if (templistDto.size() > 0) {
                    for (MstMarketInstitutionsDto mstMarketInstitutionsDto : templistDto) {
                        errorList.add(new ValidMessage("",
                            getMessage("COMMON_E_0030",
                                getMessage("MarketOrganManageInfo002.marketOrganCode"),
                                mstMarketInstitutionsDto.getCustomerId())));
                    }
                }else if (!StringUtil.isEmptyList(deleteCheckList)){
                	for (MstMarketInstitutionsDto mstMarketInstitutionsDto : deleteCheckList) {
                        errorList.add(new ValidMessage("",
                            getMessage("MST_MARKET_INSTITUTIONS_0001",
                                getMessage("MarketOrganManageInfo002.marketOrganCode"),
                                mstMarketInstitutionsDto.getCustomerId())));
                    }
                }
            }
            else {
            	if(StringUtil.equals(DictCodeList.机构所属行业一级分类_金融业.getValue(), dto.getIndustry())){
            		if (!StringUtil.isNotEmpty(dto.getHsType())) {
            		errorList.add(new ValidMessage("hsType", "所属行业是金融业时，核算分类必须输入。"));
            		}
            	}
                // 市场机构名称唯一性check
                if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
                    /*市场机构名称流程中check add by lishuping 20191023 start*/
                    if (mstMarketInstitutionsService.checkBusinessDataInFlow(dto)) {
                        errorList.add(new ValidMessage(this.getMessage("COMMON_E_0012", dto.getCustomerName())));
                    }
                    /*市场机构名称流程中check add by lishuping 20191023 end*/
                    List<MstMarketInstitutionsDto> list = mstMarketInstitutionsService.customerNameCheck(dto.getCustomerName());
                    for (MstMarketInstitutionsDto mstMarketInstitutionsDto : list) {
                        if (dto.getCustomerName().equals(
                            mstMarketInstitutionsDto.getCustomerName())) {
                            errorList.add(new ValidMessage("", "市场机构名称已存在！"));
                        }
                    }
                    // add by swwan 2022-06-27 start TISHKS-1391
                    // 组织机构代码/统一社会信用代码唯一性check
                    if (mstMarketInstitutionsService.insitituteCodeCheck(dto.getInsitituteCode()) > 0) {
                        errorList.add(new ValidMessage("", "组织机构代码/统一社会信用代码已存在！"));
                    }
                    // add by swwan 2022-06-27 end
                    // 新建校验
                    List<MstBondCreditRating> mstBondCreditRatingList = dto.getMstBondCreditRating();
                    //判断传入的字段是否为空
                    for(MstBondCreditRating mstBondCreditRating : mstBondCreditRatingList){
                        if(!StringUtil.isNotEmpty(mstBondCreditRating.getGradeDate())){
                            errorList.add(new ValidMessage("评级日期不能为空！"));
                        } 
                        if(StringUtil.isEmpty(mstBondCreditRating.getShortLongTerm())){
                            errorList.add(new ValidMessage("评级类型不能为空！"));
                        } 
                        if(StringUtil.isEmpty(mstBondCreditRating.getGradeResult())){
                            errorList.add(new ValidMessage("评级结果不能为空！"));
                        }
                        if(StringUtil.isEmpty(mstBondCreditRating.getDirectionChange())){
                            errorList.add(new ValidMessage("变动方向不能为空！"));
                        }
                        if(StringUtil.isEmpty(mstBondCreditRating.getGradeOrgId())){
                            errorList.add(new ValidMessage("评级机构不能为空！"));
                        }
                    }
                    // 同一评级日期、同一评级机构只能添加一条记录
                    for (int i = 0; i < mstBondCreditRatingList.size(); i++ ) {
                        for (int j = i + 1; j < mstBondCreditRatingList.size(); j++ ) {
                            if (mstBondCreditRatingList.get(i).getGradeDate().equals(
                            		mstBondCreditRatingList.get(j).getGradeDate())
                                && mstBondCreditRatingList.get(i).getGradeOrgId().equals(
                                		mstBondCreditRatingList.get(j).getGradeOrgId())) {
                                errorList.add(new ValidMessage("同一评级日期、同一评级机构只能添加一条记录！"));
                            }
                        }
                    }
                    //是否集团客户为是，必填所属集团客户名称
                    if(StringUtil.isNotBlank(dto.getIsGroupCustomer())
                        && StringUtil.equals(dto.getIsGroupCustomer(),DictCodeList.是否_是.getValue())
                            && StringUtil.isBlank(dto.getParentCustomerName())){
                        errorList.add(new ValidMessage("是否集团客户为是,所属集团客户名称不能为空！"));
                    }
                }
                else if (StringUtil.equals(dto.getPageModel(),
                    GlobalConstant.DETAIL_PAGE_MODEL_EDIT)) {
                    List<MstMarketInstitutionsDto> list = mstMarketInstitutionsService.customerNameCheck(dto.getCustomerName());
                    for (MstMarketInstitutionsDto mstMarketInstitutionsDto : list) {
                        if (dto.getCustomerName().equals(
                            mstMarketInstitutionsDto.getCustomerName())) {
                            if (!StringUtil.equals(dto.getCustomerId(),
                                mstMarketInstitutionsDto.getCustomerId())) {
                                    errorList.add(new ValidMessage("", "市场机构名称已存在！"));
                                }
                            }
                        }
                    List<MstBondCreditRating> mstBondCreditRatingList = dto.getMstBondCreditRating();
                    //判断传入的字段是否为空
                    for(MstBondCreditRating mstBondCreditRating : mstBondCreditRatingList){
                        if(!StringUtil.isNotEmpty(mstBondCreditRating.getGradeDate())){
                            errorList.add(new ValidMessage("评级日期不能为空！"));
                        } 
                        if(StringUtil.isEmpty(mstBondCreditRating.getShortLongTerm())){
                            errorList.add(new ValidMessage("评级类型不能为空！"));
                        } 
                        if(StringUtil.isEmpty(mstBondCreditRating.getGradeResult())){
                            errorList.add(new ValidMessage("评级结果不能为空！"));
                        }
                        if(StringUtil.isEmpty(mstBondCreditRating.getDirectionChange())){
                            errorList.add(new ValidMessage("变动方向不能为空！"));
                        }
                        if(StringUtil.isEmpty(mstBondCreditRating.getGradeOrgId())){
                            errorList.add(new ValidMessage("评级机构不能为空！"));
                        }
                    }
                    // 同一评级日期、同一评级机构只能添加一条记录
                    for (int i = 0; i < mstBondCreditRatingList.size(); i++ ) {
                        for (int j = i + 1; j < mstBondCreditRatingList.size(); j++ ) {
                            if (mstBondCreditRatingList.get(i).getGradeDate().equals(
                            		mstBondCreditRatingList.get(j).getGradeDate())
                                && mstBondCreditRatingList.get(i).getGradeOrgId().equals(
                                		mstBondCreditRatingList.get(j).getGradeOrgId())) {
                                errorList.add(new ValidMessage("同一评级日期、同一评级机构只能添加一条记录！"));
                            }
                        }
                    }
                }
            }
        }
        else {
            // 市场机构名称唯一性check
            if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
                List<MstMarketInstitutionsDto> list = mstMarketInstitutionsService.customerNameCheck(dto.getCustomerName());
                for (MstMarketInstitutionsDto mstMarketInstitutionsDto : list) {
                    if (dto.getCustomerName().equals(mstMarketInstitutionsDto.getCustomerName())) {
                        errorList.add(new ValidMessage("", "市场机构名称已存在！"));
                    }
                }
                List<MstBondCreditRating> mstBondCreditRatingList = dto.getMstBondCreditRating();
                //判断传入的字段是否为空
                for(MstBondCreditRating mstBondCreditRating : mstBondCreditRatingList){
                    if(!StringUtil.isNotEmpty(mstBondCreditRating.getGradeDate())){
                        errorList.add(new ValidMessage("评级日期不能为空！"));
                    } 
                    if(StringUtil.isEmpty(mstBondCreditRating.getShortLongTerm())){
                        errorList.add(new ValidMessage("评级类型不能为空！"));
                    } 
                    if(StringUtil.isEmpty(mstBondCreditRating.getGradeResult())){
                        errorList.add(new ValidMessage("评级结果不能为空！"));
                    }
                    if(StringUtil.isEmpty(mstBondCreditRating.getDirectionChange())){
                        errorList.add(new ValidMessage("变动方向不能为空！"));
                    }
                    if(StringUtil.isEmpty(mstBondCreditRating.getGradeOrgId())){
                        errorList.add(new ValidMessage("评级机构不能为空！"));
                    }
                }
                // 同一评级日期、同一评级机构只能添加一条记录
                for (int i = 0; i < mstBondCreditRatingList.size(); i++ ) {
                    for (int j = i + 1; j < mstBondCreditRatingList.size(); j++ ) {
                        if (mstBondCreditRatingList.get(i).getGradeDate().equals(
                        		mstBondCreditRatingList.get(j).getGradeDate())
                            && mstBondCreditRatingList.get(i).getGradeOrgId().equals(
                            		mstBondCreditRatingList.get(j).getGradeOrgId())) {
                            errorList.add(new ValidMessage("同一评级日期、同一评级机构只能添加一条记录！"));
                        }
                    }
                }
            }
            else if (StringUtil.equals(dto.getPageModel(),
                GlobalConstant.DETAIL_PAGE_MODEL_EDIT)) {
                List<MstMarketInstitutionsDto> list = mstMarketInstitutionsService.customerNameCheck(dto.getCustomerName());
                int a = 0;
                for (MstMarketInstitutionsDto mstMarketInstitutionsDto : list) {
                    if (dto.getCustomerName().equals(mstMarketInstitutionsDto.getCustomerName())) {
                        if (dto.getCustomerId().equals(mstMarketInstitutionsDto.getCustomerId())) {

                        }
                        else {
                            a++ ;
                            if (a > 0) {
                                errorList.add(new ValidMessage("", "市场机构名称已存在！"));
                            }
                        }
                    }
                }
                List<MstBondCreditRating> mstBondCreditRatingList = dto.getMstBondCreditRating();
                //判断传入的字段是否为空
                for(MstBondCreditRating mstBondCreditRating : mstBondCreditRatingList){
                    if(!StringUtil.isNotEmpty(mstBondCreditRating.getGradeDate())){
                        errorList.add(new ValidMessage("评级日期不能为空！"));
                    } 
                    if(StringUtil.isEmpty(mstBondCreditRating.getShortLongTerm())){
                        errorList.add(new ValidMessage("评级类型不能为空！"));
                    } 
                    if(StringUtil.isEmpty(mstBondCreditRating.getGradeResult())){
                        errorList.add(new ValidMessage("评级结果不能为空！"));
                    }
                    if(StringUtil.isEmpty(mstBondCreditRating.getDirectionChange())){
                        errorList.add(new ValidMessage("变动方向不能为空！"));
                    }
                    if(StringUtil.isEmpty(mstBondCreditRating.getGradeOrgId())){
                        errorList.add(new ValidMessage("评级机构不能为空！"));
                    }
                }
                // 同一评级日期、同一评级机构只能添加一条记录
                for (int i = 0; i < mstBondCreditRatingList.size(); i++ ) {
                    for (int j = i + 1; j < mstBondCreditRatingList.size(); j++ ) {
                        if (mstBondCreditRatingList.get(i).getGradeDate().equals(
                        		mstBondCreditRatingList.get(j).getGradeDate())
                            && mstBondCreditRatingList.get(i).getGradeOrgId().equals(
                            		mstBondCreditRatingList.get(j).getGradeOrgId())) {
                            errorList.add(new ValidMessage("同一评级日期、同一评级机构只能添加一条记录！"));
                        }
                    }
                }
            }
        }

        if (!StringUtil.equals(dto.getOperateType(), "delete")) {
            errorList = chbInvDataCheckService.chbCheckCommon(errorList, dto, "MstMarketInstitutions002", null);
        }

        return errorList;
    }

}
