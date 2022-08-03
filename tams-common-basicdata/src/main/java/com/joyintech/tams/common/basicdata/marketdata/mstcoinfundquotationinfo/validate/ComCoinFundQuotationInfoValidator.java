/*
 * 文件名：MstCoinFundQuotationInfoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年8月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.common.basicdata.marketdata.mstcoinfundquotationinfo.validate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.joyintech.tams.common.basicdata.marketdata.mstcoinfundquotationinfo.dto.ComCoinFundQuotationInfo001Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.basicdata.marketdata.mstcoinfundquotationinfo.service.ComCoinFundQuotationInfo001Service;

/**
 * 实体类判断
 * 
 * @author fanshuanghu
 * @version 1.0 2018年8月3日
 * @see ComCoinFundQuotationInfoValidator
 * @since 1.0
 */
@Transactional
@Component
public class ComCoinFundQuotationInfoValidator implements BeanValidator {

    /**
     * 注入实体类的mapper
     */
    @Autowired
    private ComCoinFundQuotationInfo001Service comCoinFundQuotationInfo001Service;

    /**
     * 传入参数检查 {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        ComCoinFundQuotationInfo001Dto dto = (ComCoinFundQuotationInfo001Dto)value;// 获取实体类
        ArrayList<ValidMessage> errorList = new ArrayList<>();// 生成错误集合
        Date sysDate = DateUtil.platDateFromCache();// 获取平台日期
            // 判断操作类型
            if (StringUtil.equals(dto.getOperateType(), "delete")) {
                // 判断是否获取到删除的项目
                if (dto.getSelectedItem() != null || dto.getSelectedItem().size() > 0) {
                    // 遍历选择的项目
                    for (ComCoinFundQuotationInfo001Dto cur : dto.getSelectedItem()) {
					/*
					 * // 判断估值时间是否小于平台时间 if (cur.getValDate().compareTo(sysDate) < 0) { // 添加错误信息
					 * errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0015",
					 * getMessage("ComCoinFundQuotationInfo002.valuationTime"),
					 * getMessage("ComCoinFundQuotationInfo002.date")))); }
					 */
                    	  //add wangwei 1 YUCHUBS-521债券估值行情、基金估值行情、计划类资产估值行情：功能优化
                        cur.setFinprodId(cur.getFinprodMarketId());
                        //修改/删除时，仅能修改/删除导入日为平台日的数据
                        if (StringUtil.isNotEmpty(cur.getImportDay()) && DateUtil.compareDate(DateUtil.format(cur.getImportDay()), DateUtil.format(sysDate))  != 0) {
                        	errorList.add(new ValidMessage("修改/删除时,仅能修改/删除导入日为平台日的数据!"));
                        }
                        
                    }
                }
                // 判断操作类型
            }
            else if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
                // 判断是否存在空值，直接返回交给注解处理
            if (dto.getFinprodId() == null || dto.getFinprodId().isEmpty()
                || dto.getValDate() == null || dto.getValuation() == null
                    // TISHKS-1503:cshares字段不是必填项,无需校验 modify by fuyongsheng 20220630 start
                    // || dto.getcShares() == null
                    // TISHKS-1503:cshares字段不是必填项,无需校验 modify by fuyongsheng 20220630 end
            ) {

                }
                else if (dto.getFinprodType().isEmpty()) {
                    errorList.add(new ValidMessage(getMessage("FW_E_0006",
                        getMessage("ComCoinFundQuotationInfo002.finprodType"))));
                }
                else if (comCoinFundQuotationInfo001Service.selectByIdAndValDate(dto.getFinprodId(),
                DateUtil.format(dto.getValDate())) != null
                    && !comCoinFundQuotationInfo001Service.selectByIdAndValDate(
                        dto.getFinprodId(), DateUtil.format(dto.getValDate())).isEmpty()) {
                    // TISHKS-1503:修改拼接信息 modify by fuyongsheng 20220630 start
                    // 拼接错误信息
//                   StringBuffer sb = new StringBuffer();
//                   sb.append(getMessage("ComCoinFundQuotationInfo001.finprodId")).append(
//                        GlobalConstant.COLON).append(dto.getFinprodMarketId()).append(
//                            GlobalConstant.COMMA).append(
//                                getMessage("ComCoinFundQuotationInfo002.valuationTime")).append(
//                                GlobalConstant.COLON).append(DateUtil.format(dto.getValDate()));
                    StringBuffer sb1 = new StringBuffer();
                    sb1.append(getMessage("ComCoinFundQuotationInfo001.finprodId")).append(
                            GlobalConstant.COLON).append(dto.getFinprodMarketId()).append(GlobalConstant.COMMA);
                    StringBuffer sb2 = new StringBuffer();
                    sb2.append(getMessage("ComCoinFundQuotationInfo002.valuationTime")).append(
                                GlobalConstant.COLON).append(DateUtil.format(dto.getValDate()));
                    // 添加错误信息
                    errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0006", sb1.toString(), sb2.toString())));
                    // TISHKS-1503:修改拼接信息 modify by fuyongsheng 20220630 end
                }
                else {
                    // 判断估值时间是否小于平台时间
                /*if (dto.getValDate().compareTo(sysDate) < 0) {
                    errorList.add(new ValidMessage(getMessage(
                        "BUSS_COM_E_0004", getMessage("ComCoinFundQuotationInfo002.valuationTime")
                                           + DateUtil.format(dto.getValDate()),
                            DateUtil.format(sysDate))));
                }*/
                    }
                }
        else if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)) {
                if (dto.getValuation() == null) {
                    // 判断估值时间是否小于平台时间
                }
                //add wangwei 1 YUCHUBS-521债券估值行情、基金估值行情、计划类资产估值行情：功能优化
                //修改/删除时，仅能修改/删除导入日为平台日的数据
                if (StringUtil.isNotEmpty(dto.getImportDay()) && DateUtil.compareDate(DateUtil.format(dto.getImportDay()), DateUtil.format(sysDate))  != 0) {
                	errorList.add(new ValidMessage("修改/删除时,仅能修改/删除导入日为平台日的数据!"));
                }
			/*
			 * else if (dto.getValDate().compareTo(sysDate) < 0) { errorList.add(new
			 * ValidMessage(getMessage( "BUSS_COM_E_0018",
			 * getMessage("ComCoinFundQuotationInfo002.valuationTime") +
			 * DateUtil.format(dto.getValDate()), DateUtil.format(sysDate))));
			 * 
			 * // ValidMessage(this.getMessage("BUSS_COM_E_0004", //
			 * getMessage("MstBondQuotationInfo001.valueDate") + //
			 * DateUtil.format(dto.getValueDate()) , // "平台日" +
			 * DateUtil.format(DateUtil.parse((String)SecurityUtil.getFromSession( //
			 * GlobalConstant.SESSION_SYS_DATE)))))); }
			 */
            }
        return errorList;
    }
}
