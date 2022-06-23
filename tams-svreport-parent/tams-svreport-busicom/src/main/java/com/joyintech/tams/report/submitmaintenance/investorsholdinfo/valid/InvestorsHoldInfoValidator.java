/*
 * 文件名：InvestorsHoldInfoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：天津滨海农商行理财资管系统
 * 修改人：dingzhipeng
 * 修改时间：2018年5月9日
 * 修改内容：新建
 * 系统名称：天津滨海农商行理财资管系统
 */

package com.joyintech.tams.report.submitmaintenance.investorsholdinfo.valid;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.ChbInvestholdInfoSub;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.tams.report.submitmaintenance.investorsholdinfo.dto.InvestorsHoldInfo001Dto;
import com.joyintech.tams.report.submitmaintenance.investorsholdinfo.service.InvestorsHoldInfo001Service;
import com.joyintech.tams.report.submitmanagement.investorsholdinforegist.dto.InvestorsHoldInfoRegist001Dto;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 验证类
 * 〈功能详细描述〉
 * @author dingzhipeng
 * @version 1.0 2018年5月9日
 * @see InvestorsHoldInfoValidator
 * @since 1.0
 */
@Transactional
@Component
public class InvestorsHoldInfoValidator implements BeanValidator{

    /**
     * bean
     */
    @Autowired
    private InvestorsHoldInfo001Service investorsHoldInfo001Service;
    /**
	 * logger
	 */
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        InvestorsHoldInfo001Dto dto= (InvestorsHoldInfo001Dto)value;
        ArrayList<ValidMessage> errorList = new ArrayList<>();
        if(StringUtil.equals(dto.getOperateType(), "delete")){
            String baseStatus = dto.getSelectedItem().get(0).getStatusCode();
            for(InvestorsHoldInfo001Dto item :dto.getSelectedItem()){
                /*if(!(StringUtil.equals(item.getDataSource(), DictCodeListExt2.数据来源_EXCEL导入.getValue())
                    ||StringUtil.equals(item.getDataSource(), DictCodeListExt2.数据来源_系统新建.getValue()))){
                    errorList.add(new ValidMessage("所选数据中存在接口数据！"));
                    break;
                }*/
                if(StringUtil.equals(item.getStatusCode(),DictCodeListExt1.维护数据状态_删除.getValue())){
                    errorList.add(new ValidMessage("所选数据中存在已删除数据！"));
                    break;
                }
                if(!StringUtil.equals(item.getStatusCode(),DictCodeListExt1.维护数据状态_有效.getValue())
                    &&!StringUtil.equals(item.getStatusCode(),DictCodeListExt1.维护数据状态_待处理.getValue())){
                    errorList.add(new ValidMessage("只能删除有效、待处理数据，请重新选择！"));
                    break;
                }
                if(!StringUtil.equals(baseStatus, item.getStatusCode())){
                    errorList.add(new ValidMessage("只能删除一种数据状态的数据！"));
                    break;
                }
            }
        }
        else if(StringUtil.equals(dto.getOperateType(), "edit")){
            if(StringUtil.equals(dto.getStatusCode(),DictCodeListExt1.维护数据状态_删除.getValue())){
                errorList.add(new ValidMessage("不能操作已经被删除的数据!"));
            }
            //反馈状态是发送成功不能修改
            List<ChbInvestholdInfoSub> subListUp = new ArrayList<>();
            ChbInvestholdInfoSub subDto = new ChbInvestholdInfoSub();
            subDto.setIndentifyMark(dto.getIndentifyMark());
            subDto.setProdid(dto.getProdid());
            subDto.setCcy(dto.getCcy());
            subDto.setHoldDate(dto.getHoldDate());
            subDto.setRegistBankCode(dto.getRegistBankCode());
            subListUp.add(subDto);
            List<InvestorsHoldInfoRegist001Dto> submitFeedbackStatusList = investorsHoldInfo001Service.getSubmitFeedbackStatusList(subListUp);
            if(CollectionUtils.isNotEmpty(submitFeedbackStatusList)){
            	for(InvestorsHoldInfoRegist001Dto action : submitFeedbackStatusList){
						 if ("01".equals(action.getSubmitFeedbackStatus())
									|| "02".equals(action.getSubmitFeedbackStatus())
									|| "05".equals(action.getSubmitFeedbackStatus())
									|| StringUtil.isBlank(action.getSubmitFeedbackStatus())) {
	            			 errorList.add(new ValidMessage("该数据存在中债报送流程中的数据，不允许修改，请稍后再试！"));
						 }
            	}
            }
        }
        else if(StringUtil.equals(dto.getOperateType(), "save")){
            if(StringUtil.isNotEmpty(dto.getPrdRegistCode())&&!(investorsHoldInfo001Service.countProdReg(dto.getPrdRegistCode())>0)){
                errorList.add(new ValidMessage(this.getMessage("REPORT_E_0015","产品登记编码")));
            }
            Map<String,String>map= new HashMap<>();
            map.put("INDENTIFY_MARK",dto.getIndentifyMark());
            String registBankCode = dto.getRegistBankCode();
            if (StringUtil.isEmpty(registBankCode)) {
                registBankCode = CommonUtil.getUserBankCode(dto.getOrgCode());
            }
//            map.put("REGIST_BANK_CODE",dto.getRegistBankCode());
            map.put("REGIST_BANK_CODE", registBankCode);
            if(StringUtil.isNotEmpty(dto.getIndentifyMark())&&investorsHoldInfo001Service.countInvestor(map)==0){
                errorList.add(new ValidMessage(this.getMessage("REPORT_E_0015","识别标识")));
            };
            if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
                try {
                    //判断主键标识是否重复
                    map.put("HOLD_DATE", new SimpleDateFormat("yyyyMMdd").format(dto.getHoldDate()));
                    map.put("CCY", dto.getCcy());
                    map.put("PRD_REGIST_CODE", dto.getPrdRegistCode());
                    if(investorsHoldInfo001Service.countPrimaryKey(map)>0){
                        //该记录已经存在，则弹出提示
                        errorList.add(new ValidMessage(this.getMessage("REPORT_COMMON_E_0211")));
                    }
                }
                catch (Exception e) {
                	logger.error(e.getMessage(), e);
                }

                try {
                    // 持有日期应小于等于当前系统日期
                    Date currDate = DateUtil.now();// 当前日期
                    if (DateUtil.compareDate(currDate, dto.getHoldDate()) < 0) {
                        errorList.add(new ValidMessage("持有日期应小于等于当前系统日期"));
                    }
                }
                catch (Exception e) {
                	logger.error(e.getMessage(), e);
                }
            }
            try {
                // 检查持有份额、持有金额、折算人民币金额（元）必须大于0
                BigDecimal qty = dto.getQty();
                if (qty != null && qty.compareTo(BigDecimal.ZERO) <= 0) {
                    String message = this.getMessage("REPORT_COMMON_E_0024", "持有份额", "0");
                    errorList.add(new ValidMessage("qty", message));
                }

                BigDecimal amount = dto.getAmount();
                if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
                    String message = this.getMessage("REPORT_COMMON_E_0024", "持有金额", "0");
                    errorList.add(new ValidMessage("amount", message));
                }

                BigDecimal cnyAmt = dto.getCnyAmt();
                if (cnyAmt != null && cnyAmt.compareTo(BigDecimal.ZERO) <= 0) {
                    String message = this.getMessage("REPORT_COMMON_E_0024", "折算人民币金额（元）", "0");
                    errorList.add(new ValidMessage("cnyAmt", message));
                }
                //如果币种是人民币时，持有金额和折算人民币金额必须保持一致
                if(StringUtil.equals(dto.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue())){
                	if(amount != null || cnyAmt != null){
                		if (cnyAmt != null&&cnyAmt.compareTo(amount) != 0) {
                            String message = this.getMessage("REPORT_COMMON_FILE_E_0025");
                            errorList.add(new ValidMessage("cnyAmt", message));
                        }
                	}
                }
            }
            catch (Exception e) {
            	logger.error(e.getMessage(), e);
            }

        }

        return errorList;
    }

}
