/*
 * 文件名：EtrInvestBondInfoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrseclend.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.entrust.common.validator.EtrCommonValidator;
import com.joyintech.tams.invest.entrust.etrseclend.dto.EtrSecLendDto;
import com.joyintech.tams.invest.entrust.etrseclend.service.EtrSecLendService;

/**
 * 债券投资委托校验 〈功能详细描述〉
 *
 * @author wangzhuangzhuang
 * @version 1.0 2019年7月5日
 * @see EtrSecLendValidator
 * @since 1.0
 */
@Component
public class EtrSecLendValidator implements EtrCommonValidator {

    /**
     * 获取service
     */
    @Autowired
    private EtrSecLendService etrSecLendService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {
        List<ValidMessage> errorList = new ArrayList<>();
        EtrSecLendDto dto = (EtrSecLendDto)value;
        if (StringUtil.equals(dto.getOperateType(), "delete")) {
            List<EtrSecLendDto> listDto = dto.getSelectedItem();
            for (EtrSecLendDto entity : listDto) {
                entity.setOperateType(DictCodeListExt1.委托操作类型_删除.getValue());
                errorList.addAll(etrSecLendService.check(entity));
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "close")) {
            List<EtrSecLendDto> listDto = dto.getSelectedItem();
            for (EtrSecLendDto entity : listDto) {
                entity.setOperateType(DictCodeListExt1.委托操作类型_关闭.getValue());
                errorList.addAll(etrSecLendService.check(entity));
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "recall")) {
            List<EtrSecLendDto> listDto = dto.getSelectedItem();
            for (EtrSecLendDto entity : listDto) {
                entity.setOperateType(DictCodeListExt1.委托操作类型_撤回.getValue());
                errorList.addAll(etrSecLendService.check(entity));
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "edit")) {
            dto.setOperateType(DictCodeListExt1.委托操作类型_修改.getValue());
            
            errorList.addAll(etrSecLendService.check(dto));
        }
        else {
            Date vdate = dto.getVdate();
            Date mdate = dto.getMdate();
            if (vdate != null && mdate != null) {
                if (vdate.after(mdate)) {
                    errorList.add(new ValidMessage("vdate", this.getMessage("ETR_X_005")));
                }
            }
            BigDecimal totalAmt=dto.getTotalAmt();
            if(totalAmt == null){
                errorList.add(new ValidMessage("请输入券面总额。"));
            }
            BigDecimal objectAvailablePosition = dto.getObjectAvailablePosition();
            // 交易方向为融出时，显示可用持仓，券面总额不可大于可用持仓；
//            if (StringUtil.equals(DictCodeList.回购拆借方向_融出.getValue(), dto.getBusiPs())) {
//                if (totalAmt != null && objectAvailablePosition != null
//                    && totalAmt.compareTo(objectAvailablePosition) > 0) {
//                        errorList.add(new ValidMessage("totalAmt",
//                            getMessage("BUSS_COM_E_0003",
//                                getMessage("EtrSecLend.totalAmt"),
//                                getMessage("EtrSecLend.objectAvailablePosition"))));
//                }
//
//                if(StringUtil.isEmpty(dto.getSecAcctId())){
//                    errorList.add(new ValidMessage("secAcctId", this.getMessage("ETR_X_012",this.getMessage("EtrSecLend.secAcctId"))));
//                }
//            }
            // 交易方向为入时，需指定质押券
            if (StringUtil.equals(DictCodeList.回购拆借方向_融入.getValue(), dto.getBusiPs())) {
                if(!StringUtil.isNotEmpty(dto.getBussRepoSec())||StringUtil.isEmptyList(dto.getBussRepoSec().getPledgeBondList())){
                    errorList.add(new ValidMessage("交易方向为融入时，需指定质押券!"));
                }
            }
        }

        return errorList;
    }
}
