package com.joyintech.tams.invest.product.productelement.periodicmanagement.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.product.productelement.periodicmanagement.dto.PeriodicManagement001Dto;
import com.joyintech.tams.invest.product.productelement.periodicmanagement.service.PeriodicManagement001Service;

@Transactional
@Component
public class PeriodicManagement001Validator implements BeanValidator {

    @Autowired
    private PeriodicManagement001Service periodicManagement001Service;

    @Autowired
    private FinProductMapper finProductMapper;

    @Override
    public List<ValidMessage> valid(Object value) {
        ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
        PeriodicManagement001Dto dto = (PeriodicManagement001Dto) value;
        //修改 bug 产品运维OPEX-504 20191217
        if (!StringUtil.isNotEmpty(dto.getFinprodId())) {
            errorList.add(new ValidMessage("finprodId", getMessage("产品代码不能为空")));
        }
        //修改 bug BS2.1.0BSTOZ-990 add by wangcheng 20191030 start
        if (StringUtil.equals(dto.getOperateType(), "edit")) {
            //投资周期必填校验 yzg 20191122 start
//			if(StringUtil.isNotEmpty(dto.getTermType())){
//				if(StringUtil.equals(dto.getTermType(), DictCodeListExt2.期限类型_多固定期限.getValue())){
//					if(StringUtil.isEmpty(dto.getInvestCycle())) {
//						errorList.add(new ValidMessage("investCycle",getMessage("请输入投资周期")));
//					}
//				}else{
//					if(StringUtil.isEmpty(dto.getInvestCycle2())) {
//						errorList.add(new ValidMessage("investCycle2",getMessage("请输入投资周期")));
//					}
//				}
//			}
            //投资周期必填校验 yzg 20191122 end
            if (StringUtil.equals(dto.getValidStatus(), "待处理")) {
                errorList.add(new ValidMessage(getMessage("待处理的数据不能修改")));
            } else if (periodicManagement001Service.isTrade(dto)) {
                errorList.add(new ValidMessage(getMessage("已发生交易的期次应该不可修改")));
            }
            // 提示信息检查
            if (periodicManagement001Service.getTrdProductDealByPeriodId(dto.getPeriodId())) {
                errorList.add(new ValidMessage(getMessage("已存在交易的期次不允许修改")));
            }
        }
        //修改 bug BS2.1.0BSTOZ-990 add by wangcheng 20191030 end
        if (StringUtil.equals(dto.getOperateType(), "delete")) {
            if (StringUtil.equals(dto.getValidStatus(), "待处理")) {
                errorList.add(new ValidMessage(getMessage("待处理的数据不能删除")));
            }
            if (periodicManagement001Service.isTrade(dto)) {
                errorList.add(new ValidMessage(getMessage("已发生交易的期次应该不可删除")));
            }
            // 提示信息检查
            if (periodicManagement001Service.getTrdProductDealByPeriodId(dto.getPeriodId())) {
                errorList.add(new ValidMessage(getMessage("已存在交易的期次不允许删除")));
            }
        }
        if (StringUtil.equals(dto.getOperateType(), "new")) {
            //投资周期必填校验 yzg 20191122 start
            if (StringUtil.isNotEmpty(dto.getTermType())) {
                if (StringUtil.equals(dto.getTermType(), DictCodeListExt2.期限类型_多固定期限.getValue())) {
                    if (StringUtil.isEmpty(dto.getInvestCycle())) {
                        errorList.add(new ValidMessage("investCycle", getMessage("请输入投资周期")));
                    }
                } else {
                    if (StringUtil.isEmpty(dto.getInvestCycle2())) {
                        errorList.add(new ValidMessage("investCycle2", getMessage("请输入投资周期")));
                    } else {
//                        if (StringUtil.equals(dto.getTermType(), DictCodeListExt2.期限类型_无固定期限.getValue())
//                                && StringUtil.isNotEmpty(dto.getInvestCycle2()) && dto.getInvestCycle2().length() > 4) {
//                            errorList.add(new ValidMessage(getMessage("投资周期格式错误,请输入4位以内正整数")));
//                        }
                        if (StringUtil.equals(dto.getTermType(), DictCodeListExt2.期限类型_无固定期限.getValue())
                                && !(Pattern.matches("[1-9]*\\d{3}", dto.getInvestCycle2())
                                || Pattern.matches("[1-9]", dto.getInvestCycle2())
                                || Pattern.matches("[1-9]*\\d{1}", dto.getInvestCycle2())
                                || Pattern.matches("[1-9]*\\d{2}", dto.getInvestCycle2())
                        )) {
                            errorList.add(new ValidMessage(getMessage("投资周期格式错误,请输入4位以内正整数")));
                        }
                    }
                }
            }
            //投资周期必填校验 yzg 20191122 end
            //校验期次代码唯一性 只有在新建的时候才能修改
            if (StringUtil.equals(GlobalConstant.DETAIL_PAGE_MODEL_NEW, dto.getPageModel())) {
                if (periodicManagement001Service.checkPeriodId(dto.getPeriodId())) {
                    errorList.add(new ValidMessage(getMessage("期次代码已存在")));
                }
                if (dto.getEndDate() != null &&
                        dto.getStartDate().compareTo(DateUtil.platDateFromCache()) < 0) {
                    errorList.add(new ValidMessage(getMessage("期次开始日应大于等于平台日期")));
                }
            }
            if (!StringUtil.isNotEmpty(dto.getStartDate())) {
                errorList.add(new ValidMessage(getMessage("请输入期次开始日")));
            }
            if (!StringUtil.isNotEmpty(dto.getEndDate())
                    // TISHKBS-818 add by yujiaping 2020-11-19 期次结束日，若为单一固定期限或者多固定期限，必填；若为无固定期限，则选填start
                    && !StringUtil.equals(dto.getTermType(), DictCodeListExt2.期限类型_无固定期限.getValue())) {
                // TISHKBS-818 add by yujiaping 2020-11-19 期次结束日，若为单一固定期限或者多固定期限，必填；若为无固定期限，则选填end
                errorList.add(new ValidMessage(getMessage("请输入期次结束日")));
            }

            if (dto.getStartDate() != null && dto.getEndDate() != null
                    && dto.getStartDate().compareTo(dto.getEndDate()) >= 0) {
                errorList.add(new ValidMessage(getMessage("期次结束日应大于期次开始日")));
            }
            FinProduct prod = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
            if (dto.getEndDate() != null && StringUtil.isNotEmpty(prod) && StringUtil.isNotEmpty(prod.getMdate())
                    && dto.getEndDate().compareTo(prod.getMdate()) > 0) {
                errorList.add(new ValidMessage(getMessage("期次结束日应小于等于产品到期日")));
            }

        }

        return errorList;
    }

}