/*
 * 文件名：FinBondTargetAssetValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trd.primarybidmanage.service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.CfetsPrimaryBidManageSaveDetailDto;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.CfetsPrimaryBidManageSaveRequest;
import com.joyintech.tams.common.base.entity.FinProductIssue;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债券标的信息业务验证
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @since 1.0
 */

@Service
public class CfetsPrimaryBidManageValidatorService implements BeanValidator{

    @Autowired
    private CfetsPrimaryBidManageService cfetsPrimaryBidManageService;

    @Override
    public List<ValidMessage> valid(Object value) {
        CfetsPrimaryBidManageSaveRequest dto = (CfetsPrimaryBidManageSaveRequest)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();

        if (StringUtil.equals(dto.getOperateType(), "submit")) {
 //       1、基本信息中【中标标位】、分配明细中最低【中标标位】大于等于标的资产的【最低标位】；
//        2、基本信息中【中标标位】、分配明细中最高【中标标位】小于等于标的资产的【最高标位】；
//        3、基本信息中【中标标位】、分配明细中【中标标位】取余标的资产的【投标步长】=0；
//        4、分配明细中【中标标位】个数小于等于标的资产的【最大投标标位数】；
//        5、分配明细中【中标标位】最高标位减去最低标位的差的绝对值小于等于标的资产的【最大投标标位差】；
//        以上必须同时满足
            if (!CollectionUtils.isNotEmpty(dto.getAllocationDetails())){
                resultList.add(new ValidMessage(getMessage("中标结果明细为空")));
                return resultList;
            }
            FinProductIssue finProductIssue = cfetsPrimaryBidManageService.getFinProductIssueDto(dto.getFinprodId());
            if (StringUtil.isNotEmpty(finProductIssue)) {
                boolean checkFlag = false;
                Collections.sort(dto.getAllocationDetails(), new Comparator<CfetsPrimaryBidManageSaveDetailDto>() {
                    @Override
                    public int compare(CfetsPrimaryBidManageSaveDetailDto o1, CfetsPrimaryBidManageSaveDetailDto o2) {
                        return o1.getWinPrice().compareTo(o2.getWinPrice());
                    }
                });
                if ((finProductIssue.getMinTamt() != null) && (dto.getWinPrice().compareTo(finProductIssue.getMinTamt()) < 0
                        || dto.getAllocationDetails().get(0).getWinPrice().compareTo(finProductIssue.getMinTamt()) < 0)) {
                    checkFlag = true;
                }
                if ((finProductIssue.getMaxTamt() != null) &&(dto.getWinPrice().compareTo(finProductIssue.getMaxTamt()) > 0
                        || dto.getAllocationDetails().get(dto.getAllocationDetails().size() - 1).getWinPrice().compareTo(finProductIssue.getMaxTamt()) > 0)) {
                    checkFlag = true;
                }
                if ((finProductIssue.getTenderUnit() != null) &&(dto.getWinPrice().divideAndRemainder(finProductIssue.getTenderUnit())[1].compareTo(BigDecimal.ZERO) != 0)) {
                    checkFlag = true;
                }
                if ((finProductIssue.getTenderUnit() != null)) {
                    for (CfetsPrimaryBidManageSaveDetailDto detailDto : dto.getAllocationDetails()) {
                        if (detailDto.getWinPrice().divideAndRemainder(finProductIssue.getTenderUnit())[1].compareTo(BigDecimal.ZERO) != 0) {
                            checkFlag = true;
                        }
                    }
                }
                if ((finProductIssue.getMaxTenderUnit() != null) && (BigDecimal.valueOf(dto.getAllocationDetails().size()).compareTo(finProductIssue.getMaxTenderUnit()) > 0)) {
                    checkFlag = true;
                }
                if (dto.getAllocationDetails().size() > 1) {
                    BigDecimal subtract = ArithmeticUtil.subtract(dto.getAllocationDetails().get(0).getWinPrice(), dto.getAllocationDetails().get(dto.getAllocationDetails().size() - 1).getWinPrice());
                    if (subtract.compareTo(finProductIssue.getMaxTenderPlus()) > 0) {
                        checkFlag = true;
                    }
                }
//                else {
//                    if ((finProductIssue.getMaxTenderPlus() != null) && (dto.getAllocationDetails().get(0).getWinPrice().compareTo(finProductIssue.getMaxTenderPlus()) < 0)) {
//                        checkFlag = true;
//                    }
//                }
                if (checkFlag) {
                    resultList.add(new ValidMessage(getMessage("中标标位不满足该资产发行要求！")));
                }
            }
//            分配明细中指令维度【实际中标券面总额】汇总>分配明细.【汇总投标券面总额】
            Map<String, List<CfetsPrimaryBidManageSaveDetailDto>> entrustIdListMap = dto.getAllocationDetails().stream().collect(JoyinCollectors.groupingBy(CfetsPrimaryBidManageSaveDetailDto::getEntrustId));
            for (String entrustId: entrustIdListMap.keySet()){
//                BigDecimal sumActWinPrinamt = entrustIdListMap.get(entrustId).stream().map(item -> item.getTheWinPrinamt()).reduce(BigDecimal.ZERO, BigDecimal::add);
                for (CfetsPrimaryBidManageSaveDetailDto detailDto:dto.getAllocationDetails()){
                    if (detailDto.getActWinPrinamt().compareTo(detailDto.getTotalBidPrinamt())>0){
                        resultList.add(new ValidMessage(getMessage("中标券面总额不能大于汇总投标券面总额！")));
                        break;
                    }
                }
            }
 //            分配明细中缴款金额 汇总金额<>基本信息中的缴款金额
            BigDecimal sumPatAmt = dto.getAllocationDetails().stream().map(item -> item.getPayAmt()).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (StringUtil.isNotEmpty(dto.getPayAmt()) && sumPatAmt.compareTo(dto.getPayAmt())!=0){
                resultList.add(new ValidMessage(getMessage("明细汇总缴款金额不等于基本信息中的缴款金额！")));
            }
//            分配明细中结算服务费 汇总金额<>基本信息中的结算服务费
            BigDecimal sumSettFeeAmt = dto.getAllocationDetails().stream().map(item -> item.getSettFeeAmt()).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (sumSettFeeAmt.compareTo(dto.getSettFeeAmt())!=0){
                resultList.add(new ValidMessage(getMessage("明细汇总结算服务费不等于基本信息中的结算服务费！")));
            }
//            分配明细中【中标标位】是否存在连续投标标位，即标位1和标位2之间仅差1个投标步长
            String bidMinUnit = CacheUtil.getSysParam("BID_MIN_UNIT");
            if (null!=bidMinUnit){
                Collections.sort(dto.getAllocationDetails(), new Comparator<CfetsPrimaryBidManageSaveDetailDto>() {
                    @Override
                    public int compare(CfetsPrimaryBidManageSaveDetailDto o1, CfetsPrimaryBidManageSaveDetailDto o2) {
                        return o1.getWinPrice().compareTo(o2.getWinPrice());
                    }
                });
                for (int i = 0; i < dto.getAllocationDetails().size()-1; i++) {
                    BigDecimal subtract = ArithmeticUtil.subtract(dto.getAllocationDetails().get(i+1).getWinPrice(), dto.getAllocationDetails().get(i).getWinPrice());
                    if (subtract.compareTo(new BigDecimal(bidMinUnit))==0){
                        resultList.add(new ValidMessage(getMessage("中标券面总额/中标标位不满足该资产发行要求！")));
                    }
                }
                for (int i = 0; i < dto.getAllocationDetails().size(); i++) {
                    BigDecimal[] remainder = dto.getAllocationDetails().get(0).getActWinPrinamt().divideAndRemainder(new BigDecimal(bidMinUnit));
                    if (remainder[1].compareTo(BigDecimal.ZERO)!=0){
                        resultList.add(new ValidMessage(getMessage("中标券面总额不满足该资产发行要求，请确认")));
                    }
                }
            }
        }
        if (StringUtil.equals(dto.getOperateType(), "revoke")) {
//            判断是否有流程数据
//            判断勾选记录中标状态
//            判断投标方式=自主投标 && 后续成交数据清算状态=清算成功
        }
        return resultList;
    }
}
