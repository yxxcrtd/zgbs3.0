package com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.validator;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto.MskCreditInfoProSaveRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.service.MskCreditMarketInfoPro001Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MskCreditMarketInfoValidator implements BeanValidator {

    /**
     * MskCreditMarketInfoPro001Service
     */
    @Autowired
    private MskCreditMarketInfoPro001Service mskCreditMarketInfoPro001Service;
    
    @Override
    public List<ValidMessage> valid(Object value) {
        MskCreditInfoProSaveRequest dto = (MskCreditInfoProSaveRequest)value;
        Date platDate = DateUtil.platDateFromCache();
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        if (StringUtil.equals(dto.getOperateType(), "delete")) {
            // 删除check
            List<MskCreditInfoProSaveRequest> list = dto.getSelectedItem();
            for (MskCreditInfoProSaveRequest mst : list) {
                
				/*// 判断估值日期是否大于平台日期
				 * if (mst.getValueDate().compareTo(platDate) < 0) { resultList.add(new
				 * ValidMessage(this.getMessage("BUSS_COM_E_0015",
				 * getMessage("MstBondQuotationInfo001.valueDate") +
				 * DateUtil.format(dto.getValueDate()), "平台日" + DateUtil.format(platDate)))); }
				 */
            	   //add wangwei 1 YUCHUBS-521债券估值行情、基金估值行情、计划类资产估值行情：功能优化
                //修改/删除时，仅能修改/删除导入日为平台日的数据
//                if (StringUtil.isNotEmpty(mst.getImportDay()) && DateUtil.compareDate(DateUtil.format(mst.getImportDay()), DateUtil.format(platDate)) != 0) {
//                    resultList.add(new ValidMessage("修改/删除时,仅能修改/删除导入日为平台日的数据!"));
//                }
                
                if(mskCreditMarketInfoPro001Service.selectByKey(mst.getSecid(), DateUtil.format(mst.getCdate()),mst.getValueSource()) == null){
                    resultList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0022")));
                }
                if(StringUtil.isNotEmpty(mst.getProcessState())){
                    resultList.add(new ValidMessage("流程中不能修改!"));
                }
            }

        }
        else {
            if (StringUtil.equals(dto.getPageMode(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)) {
				/*
				 * // 判断估值日期是否大于平台日期 if (dto.getValueDate().compareTo(platDate) < 0) {
				 * resultList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0018",
				 * getMessage("MstBondQuotationInfo001.valueDate") +
				 * DateUtil.format(dto.getValueDate()), "平台日" + DateUtil.format(platDate)))); }
				 */
                //add wangwei 1 YUCHUBS-521债券估值行情、基金估值行情、计划类资产估值行情：功能优化
            	//修改/删除时，仅能修改/删除导入日为平台日的数据
                //修改和删除的校验在估值子系统中使用软校验，即提示：请确认是否修改/删除
//                if ( StringUtil.isNotEmpty(dto.getImportDay()) && DateUtil.compareDate(DateUtil.format(dto.getImportDay()), DateUtil.format(platDate))  != 0) {
//                    resultList.add(new ValidMessage("修改/删除时,仅能修改/删除导入日为平台日的数据!"));
//                }
                if(StringUtil.isNotEmpty(dto.getProcessState())){
                    resultList.add(new ValidMessage("流程中不能修改!"));
                }
            }
            else {
                // 保存check
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("secId", dto.getSecId());
//                map.put("valueDate", dto.getValueDate());
                // 判断新建的数据是否存在
                if (mskCreditMarketInfoPro001Service.selectByKey(dto.getSecid(), DateUtil.format(dto.getCdate()),dto.getValueSource()) != null) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(getMessage("MskCreditMarketInfo001.secId")).append(GlobalConstant.COLON).append(
                        dto.getSecid()).append(GlobalConstant.COMMA).append(getMessage("MskCreditMarketInfo001.valueDate")).append(
                            GlobalConstant.COLON).append(DateUtil.format(dto.getCdate())).append(GlobalConstant.COMMA).append(
                                getMessage("MskCreditMarketInfo001.valueSource")).append(GlobalConstant.COLON).append(dto.getValueSourceStr());
                    resultList.add(
                        new ValidMessage("secid", getMessage("BUSS_COM_E_0006", sb.toString())));
                }

                // 判断估值日期是否大于平台日期
				/* add wangwei1 YUCHUBS-521债券估值行情、基金估值行情、计划类资产估值行情：功能优化
				 * 新建时，去掉估值日期与平台日期的判断
				 * if (StringUtil.isNotEmpty(dto.getValueDate()) &&
				 * DateUtil.compareTo(dto.getValueDate(),platDate) < 0) { resultList.add(new
				 * ValidMessage(this.getMessage("BUSS_COM_E_0004",
				 * getMessage("MstBondQuotationInfo001.valueDate") +
				 * DateUtil.format(dto.getValueDate()) , "平台日" + DateUtil.format(platDate)))); }
				 */
            }

        }
        return resultList;
    }

}
