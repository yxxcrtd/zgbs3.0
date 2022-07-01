/*
 * 文件名：ValManualVoucher003Validator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年7月11日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.validator;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.BokAcctLayeringInfoMapper;
import com.joyintech.tams.common.base.dao.BokBooksetMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.dao.ValManualVoucher003Mapper;
import com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.dto.ValManualVoucher003CheckValidatorDto;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 手工凭证确认校验
 * 
 * @author weipeng
 * @version 1.0 2018年8月22日
 * @see ValManualVoucher003CheckValidator
 * @since 1.0
 */
@Component
public class ValManualVoucher003CheckValidator implements BeanValidator {

    @Autowired
    private ValManualVoucher003Mapper valManualVoucher003Mapper;

    @Autowired
    private BokBooksetMapper bokBooksetMapper;

    @Autowired
    private BokAcctLayeringInfoMapper bokAcctLayeringInfoMapper;

    /**
     * 列表确认时校验
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        ValManualVoucher003CheckValidatorDto dto = (ValManualVoucher003CheckValidatorDto)value;
        ArrayList<ValidMessage> errorList = new ArrayList<>();
        if (StringUtils.isEmpty(dto.getSubjectNo())) {
            return errorList;
        }
        
        /**add by wujie on 20201111 start remark:债券、计划类收益型资产  投资目的不能为空**/
        if(DictCodeListExt1.金融产品类型_债券.getValue().equals(dto.getFinprodType())
        		|| (DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(dto.getFinprodType())
        			&& DictCodeList.收益类型_收益型.getValue().equals(dto.getProfitType())
        			)
        		){
        	 if (StringUtils.isEmpty(dto.getInvAim())) {
        		 String msg = this.getMessage("VAL_COMMON_E_0002",
                         this.getMessage("ValManualVoucher.invAim"));
                     errorList.add(new ValidMessage(msg));
        	 }
        }
        /**add by wujie on 20201111 end remark:债券、计划类收益型资产  投资目的不能为空**/
        
        // 获取模板账套代码
        String booksetIdTmpl = null;
        BokBookset bokBookset = bokBooksetMapper.selectByPrimaryKey(dto.getBooksetId());
        if (bokBookset != null) {
            booksetIdTmpl = bokBookset.getBooksetIdTmpl();
        }
        BokSubject boksubject = valManualVoucher003Mapper.getSubjectNo(booksetIdTmpl,
            dto.getSubjectNo());
        if (boksubject == null) {
            String msg = this.getMessage("MANUAL_VOUCHER_E_008",
                this.getMessage("ValManualVoucher.subjectNo"));
            errorList.add(new ValidMessage(msg));
            return errorList;
        }
        // 记录的科目号的核算数量flag=‘Y’
        if (DictCodeList.是否_是.getValue().equals(boksubject.getAccQuaFlag())) {
            // 数量为空时
            if (dto.getHappenNumber() == null) {
                // 数量不能为空
                String msg = "科目号["+boksubject.getSubjectNo()+"]"+this.getMessage("VAL_COMMON_E_0002",
                    this.getMessage("ValManualVoucher.happenNumber"));
                errorList.add(new ValidMessage(msg));
            }
        }
        else {
            // 数量不为空时
            if (dto.getHappenNumber() != null) {
                // 数量必须为空
                String msg = "科目号["+boksubject.getSubjectNo()+"]"+this.getMessage("MANUAL_VOUCHER_E_001",
                    this.getMessage("ValManualVoucher.happenNumber"));
                errorList.add(new ValidMessage(msg));
            }
        }
        // 记录的科目号的生成四级科目=‘Y’
        if (DictCodeList.是否_是.getValue().equals(boksubject.getGenDetsubFlag())) {
            // 科目尾号为空时
            /*
             * if (StringUtils.isEmpty(dto.getDetailSubjectNo())) { // 科目尾号不能为空 String msg =
             * this.getMessage("VAL_COMMON_E_0002",
             * this.getMessage("ValManualVoucher.detailSubjectNo")); errorList.add(new
             * ValidMessage(msg)); }
             */
        }
        else {
            // 科目尾号不为空时
            if (StringUtils.isNotEmpty(dto.getDetailSubjectNo())) {
                // 科目尾号不能为空
                String msg = this.getMessage("MANUAL_VOUCHER_E_001",
                    this.getMessage("ValManualVoucher.detailSubjectNo"));
                errorList.add(new ValidMessage(msg));
            }
        }

        // 记录的科目性质=“其他应收款-本金” or “其他应收款-利息”
        if (DictCodeBaseSubjectNatrue.其他应收款_本金.getValue().equals(boksubject.getBaseSubjectNature())
            || DictCodeBaseSubjectNatrue.其他应收款_利息.getValue().equals(boksubject.getBaseSubjectNature())
            || DictCodeBaseSubjectNatrue.其他应付款_本金.getValue().equals(boksubject.getBaseSubjectNature())
            || DictCodeBaseSubjectNatrue.其他应付款_利息.getValue().equals(boksubject.getBaseSubjectNature())
            /**ADD BY WUJIE ON 20200223 START**/
            || DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue().equals(boksubject.getBaseSubjectNature())
            /**ADD BY WUJIE ON 20200223 END**/
        		) {
            //当前情况，分支序号不能为空
            if (dto.getBranch() == null || dto.getBranch().compareTo(0L) == 0) {
                // 分支序号为空时，给予提示【分支序号不能为空】
                String msg = this.getMessage("VAL_COMMON_E_0002",this.getMessage("ValManualVoucher.branch"));
                errorList.add(new ValidMessage(msg));
                return errorList;
            }
            
            String branchType = null;
            // 业务类型为“债券”“计划类资产”“存款资产”“拆出”“同业借款”时，判断页面输入的分支序号是否与库中的分支序号匹配
            // 此处逻辑不能删除，因为页面原来的分支序号为下拉框，现在为输入框
            if(DictCodeListExt1.金融产品类型_债券.getValue().equals(dto.getFinprodType())
                || DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(dto.getFinprodType())
                || DictCodeListExt1.金融产品类型_存款.getValue().equals(dto.getFinprodType())
                || DictCodeListExt1.金融产品类型_拆出.getValue().equals(dto.getFinprodType())
                || DictCodeListExt1.金融产品类型_同业借款.getValue().equals(dto.getFinprodType())
                || DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(dto.getFinprodType())){
//                //当前情况，分支序号不能为空
//                if (detail.getBranch() == null || detail.getBranch().compareTo(0L) == 0) {
//                    // 分支序号为空时，给予提示【分支序号不能为空】
//                    String msg = this.getMessage("VAL_COMMON_E_0002",this.getMessage("ValManualVoucher.branch"));
//                    errorList.add(new ValidMessage(msg));
//                    return errorList;
//                }
//                else {
                if (DictCodeBaseSubjectNatrue.其他应收款_本金.getValue().equals(
                    boksubject.getBaseSubjectNature())) {
                    branchType = DictCodeList.分支类型_应收本金.getValue();
                }
                else if (DictCodeBaseSubjectNatrue.其他应收款_利息.getValue().equals(
                    boksubject.getBaseSubjectNature())) {
                    branchType = DictCodeList.分支类型_应收利息.getValue();
                }
                /**ADD BY WUJIE ON 20200223 START**/
                else if(DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue().equals(
                		boksubject.getBaseSubjectNature())){
                	branchType = this.getBranchType(String.valueOf(dto.getBranch()));
                }
                /**ADD BY WUJIE ON 20200223 END**/
                // if 列表科目号.基础科目性质=“逾期资产应计利息” then 分支序号下拉框为3结果.金融产品代码 = 画面.资产代码的List
                // ---无需处理，直接查询即可
                List<String> branchList = new ArrayList<>();
                // 4.6 画面.业务类型=“买断式正回购”
                if(DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(dto.getFinprodType())){
                    // 查询对应买断式回购质押品
                    List<FinTradeProduct> listFinTradeProduct = valManualVoucher003Mapper.getfinDealPledge(dto.getFinprodType(), dto.getBusiId());
                    if(!CollectionUtils.isEmpty(listFinTradeProduct)){
                        branchList = valManualVoucher003Mapper.getBranch(
                            listFinTradeProduct.get(0).getFinprodType(), branchType, listFinTradeProduct.get(0).getFinprodId()).stream().map(
                                SelectOptionDto::getText).distinct().collect(Collectors.toList());
                    }
                }
                else{
                    branchList = valManualVoucher003Mapper.getBranch(
                        dto.getFinprodType(), branchType, dto.getBusiId()).stream().map(
                            SelectOptionDto::getText).distinct().collect(Collectors.toList());
                }
                // 判断页面输入的分支序号是否与库中的分支序号匹配
                if (!branchList.contains(dto.getBranch().toString())) {
                    String msg = this.getMessage("VAL_COMMON_E_0066",
                        this.getMessage("ValManualVoucher.branch"), StringUtil.joinString(branchList, ","));
                    errorList.add(new ValidMessage(msg));
                }
                    //detail.setBranch(0L);
//                }   
            }
            // 融入债券时判断
            else if(DictCodeListExt1.金融产品类型_融入债券.getValue().equals(dto.getFinprodType())){
                if (DictCodeBaseSubjectNatrue.其他应收款_本金.getValue().equals(
                    boksubject.getBaseSubjectNature()) ||
                    DictCodeBaseSubjectNatrue.其他应付款_本金.getValue().equals(
                        boksubject.getBaseSubjectNature())) {
                    branchType = DictCodeList.分支类型_应收本金.getValue();
                }
                else if (DictCodeBaseSubjectNatrue.其他应收款_利息.getValue().equals(
                    boksubject.getBaseSubjectNature()) ||
                    DictCodeBaseSubjectNatrue.其他应付款_利息.getValue().equals(
                        boksubject.getBaseSubjectNature())) {
                    branchType = DictCodeList.分支类型_应收利息.getValue();
                }
                /**ADD BY WUJIE ON 20200223 START**/
                else if(DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue().equals(
                		boksubject.getBaseSubjectNature())){
                	branchType = this.getBranchType(String.valueOf(dto.getBranch()));
                }
                /**ADD BY WUJIE ON 20200223 END**/
                // 金融产品类型为债券
                List<String> branchList = valManualVoucher003Mapper.getBranch(
                    DictCodeListExt1.金融产品类型_债券.getValue(), branchType, dto.getBusiId()).stream().map(
                        SelectOptionDto::getText).distinct().collect(Collectors.toList());
                // 判断页面输入的分支序号是否与库中的分支序号匹配
                if (!branchList.contains(dto.getBranch().toString())) {
                    String msg = this.getMessage("VAL_COMMON_E_0066",
                        this.getMessage("ValManualVoucher.branch"), StringUtil.joinString(branchList, ","));
                    errorList.add(new ValidMessage(msg));
                }
            }

        }
        // 金融产品时 科目尾部名称暂存业务明细类型
        // 画面.业务类型=“理财产品” 并且画面的资产代码对应的业务明细类型=“理财子产品”
        else if (DictCodeListExt1.金融产品类型_理财产品.getValue().equals(dto.getFinprodType())
                 && DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(dto.getBusiDetailType())) {//modify by sunshuaishuai 20210305 空指针问题
            BokAcctLayeringInfoKey key = new BokAcctLayeringInfoKey();
            key.setBooksetId(dto.getBooksetId());
            key.setBusiDetailType(dto.getBusiDetailType());
            key.setLayeringId(dto.getBusiId());
            BokAcctLayeringInfo bokAcctLayeringInfo = bokAcctLayeringInfoMapper.selectByPrimaryKey(
                key);
            // 列表科目号.分支序号 = 画面.资产代码对应的分支序号
            dto.setBranch(bokAcctLayeringInfo.getBranch());
        }
        // 记录的科目性质！=（“其他应收款-本金” or“其他应收款-利息”  or “其他应付款-融入券本金”、“其他应付款-融入券利息”）分支序号不为空
        else {
            // 当前情况，分支序号必须为空
            if (dto.getBranch() != null && dto.getBranch().compareTo(0L) != 0) {
                // 分支序号不为空时，给予提示【分支序号必须为空】
                String msg = this.getMessage("MANUAL_VOUCHER_E_001",
                    this.getMessage("ValManualVoucher.branch"));
                errorList.add(new ValidMessage(msg));
            }
        }

        if (DictCodeBaseSubjectNatrue.现金.getValue().equals(boksubject.getBaseSubjectNature())
        		/**modify by wujie on 20201116 remark:清算款科目由三级科目改为四级科目
             || DictCodeBaseSubjectNatrue.清算款.getValue().equals(
                 boksubject.getBaseSubjectNature())
                 **/
             ) {
            // 资金账户为空时
            if (StringUtil.isEmpty(dto.getAcctCode())) {
                // 分支序号不能为空
                String msg = this.getMessage("VAL_COMMON_E_0002",
                    this.getMessage("ValManualVoucher.acctCode"));
                errorList.add(new ValidMessage(msg));
            }
            else {
                List<String> acctCodeList = valManualVoucher003Mapper.getAcctId(dto.getBooksetId(),
                    null).stream().map(SelectOptionDto::getId).distinct().collect(
                        Collectors.toList());
                if (!acctCodeList.contains(dto.getAcctCode())) {
                    String msg = this.getMessage("VAL_COMMON_E_0066",
                        this.getMessage("ValManualVoucher.acctCode"),
                        StringUtil.joinString(acctCodeList, ","));
                    errorList.add(new ValidMessage(msg));
                }
            }
        }
        // 借方金额、贷方金额都不为空时
        if ((dto.getdAmt() != null)
            && (dto.getcAmt() != null)) {
            // 借方金额、贷方金额只能填写一个
            String msg = this.getMessage("MANUAL_VOUCHER_E_002",
                this.getMessage("ValManualVoucher.dAmt"),
                this.getMessage("ValManualVoucher.cAmt"));
            errorList.add(new ValidMessage(msg));
            // 借方金额、贷方金额都为空时
        }
        else if ((dto.getdAmt() == null)
                 && (dto.getcAmt() == null)) {
            // 借方金额、贷方金额不能同时为空
            String msg = this.getMessage("MANUAL_VOUCHER_E_004",
                this.getMessage("ValManualVoucher.dAmt"),
                this.getMessage("ValManualVoucher.cAmt"));
            errorList.add(new ValidMessage(msg));
        }
        return errorList;
    }

    private String getBranchType(String branchStr){
		Pattern pattern = Pattern.compile("\\d{10}");
		Matcher isNum = pattern.matcher(branchStr);
        if(!isNum.matches())
        {
              return "";
        }
        String lastStr = branchStr.substring(8,10);
        if("02".equals(lastStr)){
        	return DictCodeList.分支类型_应收利息.getValue();
        }
        else if("01".equals(lastStr)){
        	return DictCodeList.分支类型_应收本金.getValue();
        }
		return "";
	}
}
