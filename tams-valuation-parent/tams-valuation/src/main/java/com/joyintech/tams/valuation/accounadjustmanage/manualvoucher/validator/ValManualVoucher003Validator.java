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
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.BokAcctLayeringInfoMapper;
import com.joyintech.tams.common.base.dao.BokBooksetDateMapper;
import com.joyintech.tams.common.base.dao.BokBooksetMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListNatureGroup;
import com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.dao.ValManualVoucher003Mapper;
import com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.dto.ValManualVoucher003AddDto;
import com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.dto.ValManualVoucher003Dto;
import com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.service.ValManualVoucher003Service;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 手工凭证新建校验
 * @author weipeng
 * @version 1.0 2018年7月11日
 * @see ValManualVoucher003Validator
 * @since 1.0
 */
@Component
public class ValManualVoucher003Validator implements BeanValidator{
    
    @Autowired
    private ValManualVoucher003Mapper valManualVoucher003Mapper;
    
    @Autowired
    private BokBooksetDateMapper bokBooksetDateMapper;
    @Autowired
    private BokBooksetMapper bokBooksetMapper;
    
    @Autowired
    private ValManualVoucher003Service valManualVoucher003Service;
    
    @Autowired
    private BokAcctLayeringInfoMapper bokAcctLayeringInfoMapper;
    
    /**
     * 新建页面提交时校验
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        ValManualVoucher003Dto dto = (ValManualVoucher003Dto) value;
        ArrayList<ValidMessage> errorList = new ArrayList<>();
        // 如果必须项为空，后面check不继续处理
        if (dto.getBookDate() == null || StringUtils.isEmpty(dto.getBooksetId())) {
            return errorList;
        }
        // 账套日
        BokBooksetDate valBokBooksetDate = bokBooksetDateMapper.selectByPrimaryKey(
            dto.getBooksetId());
        // 记账日期 != 账套日期.当前日期
        if (!DateUtil.isSameDate(dto.getBookDate(),valBokBooksetDate.getBooksetDate())) {
            // 数据录入非当前日期
            String msg = this.getMessage("MANUAL_VOUCHER_E_010");
            errorList.add(new ValidMessage(msg));
            return errorList;
        }
        // 列表为空
        if(CollectionUtils.isEmpty(dto.getListAddDto())){
            // 凭证分录不能为空
            String msg = this.getMessage("VAL_COMMON_E_0002",
                this.getMessage("ValManualVoucher.listAddDto"));
            errorList.add(new ValidMessage(msg));
            return errorList;
        } else {
        	
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
        	
            // 基础科目性质查询-非摊余成本对应科目
            List<String> acout_kmcx = valManualVoucher003Mapper.getValSubjectNatureList(
        		DictCodeListNatureGroup.分组代码_非摊余成本对应科目.getValue());
            // 基础科目性质查询-非公允价值对应科目
            List<String> fvtplout_kmcx = valManualVoucher003Mapper.getValSubjectNatureList(
        		DictCodeListNatureGroup.分组代码_非公允价值对应科目.getValue());
            // 基础科目性质查询-非综合收益对应科目
            List<String> fvociout_kmcx = valManualVoucher003Mapper.getValSubjectNatureList(
        		DictCodeListNatureGroup.分组代码_非综合收益对应科目.getValue());
            // 基础科目性质查询-摊余成本对应科目
            List<String> ac_kmcx = valManualVoucher003Mapper.getValSubjectNatureList(
        		DictCodeListNatureGroup.分组代码_摊余成本对应科目.getValue());
            // 基础科目性质查询-公允价值对应科目
            List<String> fvtpl_kmcx = valManualVoucher003Mapper.getValSubjectNatureList(
        		DictCodeListNatureGroup.分组代码_公允价值对应科目.getValue());
            // 基础科目性质查询-综合收益对应科目
            List<String> fvoci_kmcx = valManualVoucher003Mapper.getValSubjectNatureList(
        		DictCodeListNatureGroup.分组代码_综合收益对应科目.getValue());
            // 币种，用于判断币种是否相同
            String ccy = null;
            // 定义借贷金额求和变量
            BigDecimal damtsum = BigDecimal.ZERO, camtsum = BigDecimal.ZERO;
            List<String> acctCodeList = valManualVoucher003Mapper.getAcctId(dto.getBooksetId(), null).stream().map(SelectOptionDto::getId).distinct().collect(Collectors.toList());
            for (ValManualVoucher003AddDto detail : dto.getListAddDto()) {
                // 如果科目号为空
                if (StringUtils.isEmpty(detail.getSubjectNo())) {
                    String msg = this.getMessage("VAL_COMMON_E_0002",
                        this.getMessage("ValManualVoucher.subjectNo"));
                    errorList.add(new ValidMessage(msg));
                    return errorList;
                }
                // 获取模板账套代码
                String booksetIdTmpl = null;
                BokBookset bokBookset = bokBooksetMapper.selectByPrimaryKey(dto.getBooksetId());
                if(bokBookset!=null){
                    booksetIdTmpl = bokBookset.getBooksetIdTmpl();
                }
                // 科目信息查询
                BokSubject boksubject = valManualVoucher003Mapper.getSubjectNo(booksetIdTmpl, detail.getSubjectNo());
                
                if(boksubject == null) {
                    String msg = this.getMessage("MANUAL_VOUCHER_E_008",
                        this.getMessage("ValManualVoucher.subjectNo"));
                    errorList.add(new ValidMessage(msg));
                    return errorList;
                }
                // 记录的科目号的核算数量flag=‘Y’
                if (DictCodeList.是否_是.getValue().equals(boksubject.getAccQuaFlag())) {
                    // 数量为空时
                    if (detail.getHappenNumber() == null) {
                        // 数量不能为空
                        String msg ="科目号["+boksubject.getSubjectNo()+"]"+this.getMessage("VAL_COMMON_E_0002",
                            this.getMessage("ValManualVoucher.happenNumber"));
                        errorList.add(new ValidMessage(msg));
                        return errorList;
                    }
                } else {
                    // 数量必须为空
                    if (detail.getHappenNumber() != null) {
                        String msg = "科目号["+boksubject.getSubjectNo()+"]"+this.getMessage("MANUAL_VOUCHER_E_001",
                            this.getMessage("ValManualVoucher.happenNumber"));
                        errorList.add(new ValidMessage(msg));
                        return errorList;
                    }
                }
                // 记录的科目号的生成四级科目=‘Y’
                if (DictCodeList.是否_是.getValue().equals(boksubject.getGenDetsubFlag())) {
                    // 科目尾号为空时
                    /*if (StringUtils.isEmpty(detail.getDetailSubjectNo())) {
                        // 科目尾号不能为空
                        String msg = this.getMessage("VAL_COMMON_E_0002",
                            this.getMessage("ValManualVoucher.detailSubjectNo"));
                        errorList.add(new ValidMessage(msg));
                        return errorList;
                    }*/
                	
                	// 科目尾号不为空时
                    if (StringUtils.isEmpty(detail.getDetailSubjectNo())) {
                        // 科目尾号不能为空
                        String msg = "["+detail.getSubjectNo()+"]"+this.getMessage("VAL_COMMON_E_0002",
                            this.getMessage("ValManualVoucher.detailSubjectNo"));
                        errorList.add(new ValidMessage(msg));
                    }
                    
                } else {
                    // 科目尾号不为空时
                    if (StringUtils.isNotEmpty(detail.getDetailSubjectNo())) {
                        // 科目尾号必须为空
                        String msg = this.getMessage("MANUAL_VOUCHER_E_001",
                            this.getMessage("ValManualVoucher.detailSubjectNo"));
                        errorList.add(new ValidMessage(msg));
                        return errorList;
                    }
                }

                // 记录的科目性质=“其他应收款-本金” or“其他应收款-利息” or"其他应付款-融入券本金"  or “其他应付款-融入券利息” or “逾期资产应计利息”
                if ( DictCodeBaseSubjectNatrue.其他应收款_本金.getValue().equals(boksubject.getBaseSubjectNature())
                    || DictCodeBaseSubjectNatrue.其他应收款_利息.getValue().equals(boksubject.getBaseSubjectNature())
                    || DictCodeBaseSubjectNatrue.其他应付款_本金.getValue().equals(boksubject.getBaseSubjectNature())
                    || DictCodeBaseSubjectNatrue.其他应付款_利息.getValue().equals(boksubject.getBaseSubjectNature())
                    /**ADD BY WUJIE ON 20200223 START**/
                    || DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue().equals(boksubject.getBaseSubjectNature())
                    /**ADD BY WUJIE ON 20200223 END**/
                		) {
                    //当前情况，分支序号不能为空
                    if (detail.getBranch() == null || detail.getBranch().compareTo(0L) == 0) {
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
//                        //当前情况，分支序号不能为空
//                        if (detail.getBranch() == null || detail.getBranch().compareTo(0L) == 0) {
//                            // 分支序号为空时，给予提示【分支序号不能为空】
//                            String msg = this.getMessage("VAL_COMMON_E_0002",this.getMessage("ValManualVoucher.branch"));
//                            errorList.add(new ValidMessage(msg));
//                            return errorList;
//                        }
//                        else {
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
                            /**modify BY yanya ON 20200224 分支序号取明细列表中的**/
                        	branchType = this.getBranchType(String.valueOf(detail.getBranch()));
                        }
                        /**ADD BY WUJIE ON 20200223 END**/
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
                        if (!branchList.contains(detail.getBranch().toString())) {
                            String msg = this.getMessage("VAL_COMMON_E_0066",
                                this.getMessage("ValManualVoucher.branch"), StringUtil.joinString(branchList, ","));
                            errorList.add(new ValidMessage(msg));
                        }
                            //detail.setBranch(0L);
//                        }   
                    }
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
                            /**modify BY yanya ON 20200224 分支序号取明细列表中的**/
                        	branchType = this.getBranchType(String.valueOf(detail.getBranch()));
                        }
                        /**ADD BY WUJIE ON 20200223 END**/
                        List<String> branchList = valManualVoucher003Mapper.getBranch(
                            DictCodeListExt1.金融产品类型_债券.getValue(), branchType, dto.getBusiId()).stream().map(
                                SelectOptionDto::getText).distinct().collect(Collectors.toList());
                        // 判断页面输入的分支序号是否与库中的分支序号匹配
                        if (!branchList.contains(detail.getBranch().toString())) {
                            String msg = this.getMessage("COMMON_E_0066",
                                this.getMessage("ValManualVoucher.branch"), StringUtil.joinString(branchList, ","));
                            errorList.add(new ValidMessage(msg));
                        }
                    }
                    
                }
                // 记录的科目性质！=（“其他应收款-本金” or“其他应收款-利息”  or “其他应付款-融入券本金”、“其他应付款-融入券利息”）分支序号不为空
                else {
                    //当前情况，分支序号必须为空
                    if(detail.getBranch() != null && detail.getBranch().compareTo(0L) != 0){
                        // 分支序号不为空时，给予提示【分支序号必须为空】
                        String msg = this.getMessage("MANUAL_VOUCHER_E_001",this.getMessage("ValManualVoucher.branch"));
                        errorList.add(new ValidMessage(msg));
                        return errorList;
                    }
                    
                    //为空的情况，给予默认为0
                    detail.setBranch(0L);
                    // 主表.分支序号 = 画面.资产代码对应的分支序号
                    dto.setBranch(0L);
                }
                
                // 画面的资产代码对应的业务明细类型=“理财子产品”
                if(dto.getBusiDetailType() == null){
                    String msg = this.getMessage("VAL_COMMON_I_0010",dto.getBusiId());
                    errorList.add(new ValidMessage(msg));
                } else{
                if (dto.getBusiDetailType().equals(DictCodeListExt1.金融产品类型_理财子产品.getValue())) {
                    BokAcctLayeringInfoKey key = new BokAcctLayeringInfoKey();
                    key.setBooksetId(dto.getBooksetId());
                    key.setBusiDetailType(dto.getBusiDetailType());
                    key.setLayeringId(dto.getBusiId());
                    BokAcctLayeringInfo bokAcctLayeringInfo = bokAcctLayeringInfoMapper.selectByPrimaryKey(
                        key);
                    // 画面.业务类型=“理财产品” 并且画面的资产代码对应的业务明细类型=“理财子产品”
                    if(DictCodeListExt1.金融产品类型_理财产品.getValue().equals(dto.getFinprodType())){
                        //列表科目号.分支序号 = 画面.资产代码对应的分支序号
                        detail.setBranch(bokAcctLayeringInfo.getBranch());
                    }
                    // 主表.分支序号 = 画面.资产代码对应的分支序号
                    dto.setBranch(bokAcctLayeringInfo.getBranch());
                    // 画面.资产代码对应的业务明细代码 
                    dto.setBusiDetailId(bokAcctLayeringInfo.getBusiId());
                    // 画面.资产代码对应的业务明细类型
                    dto.setBusiDetailType(bokAcctLayeringInfo.getBusiDetailType());
                    }
                }
                
                if (DictCodeBaseSubjectNatrue.现金.getValue().equals(boksubject.getBaseSubjectNature())
                		/**modify by wujie on 20201116 remark:清算款科目由三级科目调整为四级科目
                    || DictCodeBaseSubjectNatrue.清算款.getValue().equals(boksubject.getBaseSubjectNature())**/
                    ) {
                    // 资金账户为空时
                    if (StringUtil.isEmpty(detail.getAcctCode())) {
                        // 分支序号不能为空
                        String msg = this.getMessage("VAL_COMMON_E_0002",
                            this.getMessage("ValManualVoucher.acctCode"));
                        errorList.add(new ValidMessage(msg));
                    }
                    else {
                        if(!acctCodeList.contains(detail.getAcctCode())) {
                            String msg = this.getMessage("VAL_COMMON_E_0066",
                                this.getMessage("ValManualVoucher.acctCode"), StringUtil.joinString(acctCodeList, ","));
                            errorList.add(new ValidMessage(msg));
                        }
                    }
                }
                
                // 处理投资目的
//                String invAim = dto.getInvAim();
                String invAim = null;
                // if 画面.业务类型 = “债券”
                if(DictCodeListExt1.金融产品类型_债券.getValue().equals(dto.getFinprodType())
                		||(DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(dto.getFinprodType())
                        		&& DictCodeList.收益类型_收益型.getValue().equals(dto.getProfitType())
                				)
                		){
                    // 基础科目性质 in “摊余成本类成本组”
                    if (ac_kmcx.contains(boksubject.getBaseSubjectNature())) {
                        invAim = DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue();
                    // 基础科目性质 in “公允价值损益类-成本组”
                    } else if (fvtpl_kmcx.contains(boksubject.getBaseSubjectNature())) {
                        invAim = DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue();
                    // 基础科目性质 in “综合收益类成本组”
                    } else if (fvoci_kmcx.contains(boksubject.getBaseSubjectNature())) {
                        invAim = DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue();
                    }
                    // 设置投资目的
                    dto.setInvAim(dto.getInvAim()==null?invAim:dto.getInvAim());
                }
                // 投资目的=“AC” 并且 基本科目性质 IN 非摊余成本对应科目
                if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(invAim)
                    && acout_kmcx.contains(invAim)) {
                    // 投资目的不相同
                    String msg = this.getMessage("MANUAL_VOUCHER_E_003",
                        this.getMessage("ValManualVoucher.invAim"));
                    errorList.add(new ValidMessage(msg));
                    return errorList;
                }
                // 投资目的=“FVTPL” 并且 基本科目性质 IN 非公允价值对应科目
                if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(invAim)
                    && fvtplout_kmcx.contains(invAim)) {
                    // 投资目的不相同
                    String msg = this.getMessage("MANUAL_VOUCHER_E_003",
                        this.getMessage("ValManualVoucher.invAim"));
                    errorList.add(new ValidMessage(msg));
                    return errorList;
                }
                // 投资目的=“FVOCI” 并且 基本科目性质 IN 非综合收益对应科目
                if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(invAim)
                    && fvociout_kmcx.contains(invAim)) {
                    // 投资目的不相同
                    String msg = this.getMessage("MANUAL_VOUCHER_E_003",
                        this.getMessage("ValManualVoucher.invAim"));
                    errorList.add(new ValidMessage(msg));
                    return errorList;
                }
                // 借方金额、贷方金额都不为空时
                if ((detail.getdAmt() != null)
                    && (detail.getcAmt() != null)) {
                    // 借方金额、贷方金额只能填写一个
                    String msg = this.getMessage("MANUAL_VOUCHER_E_002",
                        this.getMessage("ValManualVoucher.dAmt"),
                        this.getMessage("ValManualVoucher.cAmt"));
                    errorList.add(new ValidMessage(msg));
                    return errorList;
                // 借方金额、贷方金额都为空时
                } else if ((detail.getdAmt() == null )
                    && (detail.getcAmt() == null )) {
                    // 借方金额、贷方金额不能同时为空
                    String msg = this.getMessage("MANUAL_VOUCHER_E_004",
                        this.getMessage("ValManualVoucher.dAmt"),
                        this.getMessage("ValManualVoucher.cAmt"));
                    errorList.add(new ValidMessage(msg));
                    return errorList;
                }
                
                 //框架js中joyin-table-lay.js v2.1.3中，
                //下拉框中注释（cacheData[field + 'Val'] = ids.join(','); //缓存中添加值），直接改为cacheData[field] = ids.join(','); // 修改field值
                // 取得科目联动处理数据
                
//                // 取得列表币种
//                if (StringUtils.isEmpty(detail.getCcyVal())) {
//                    // 分支序号不能为空
//                    String msg = this.getMessage("VAL_COMMON_E_0002",
//                        this.getMessage("ValManualVoucher.ccy"));
//                    errorList.add(new ValidMessage(msg));
//                    return errorList;
//                }
//                // 第一条记录给币种变量赋值
//                if (ccy == null) {
//                    ccy = detail.getCcyVal();
//                }
//                // 币种不同
//                if (!detail.getCcyVal().equals(ccy)) {
//                    // 币种必须一致
//                    String msg = this.getMessage("MANUAL_VOUCHER_E_005",
//                        this.getMessage("ValManualVoucher.ccy"));
//                    errorList.add(new ValidMessage(msg));
//                    return errorList;
//                }
                
                // 取得列表币种
                if (StringUtils.isEmpty(detail.getCcy())) {
                    // 分支序号不能为空
                    String msg = this.getMessage("VAL_COMMON_E_0002",
                        this.getMessage("ValManualVoucher.ccy"));
                    errorList.add(new ValidMessage(msg));
                    return errorList;
                }
			  // 第一条记录给币种变量赋值
			  if (ccy == null) {
			      ccy = detail.getCcy();
			  }
			  // 币种不同
			  if (!detail.getCcy().equals(ccy)) {
			      // 币种必须一致
				  String msg = this.getMessage("MANUAL_VOUCHER_E_005",
				      this.getMessage("ValManualVoucher.ccy"));
				      errorList.add(new ValidMessage(msg));
				      return errorList;
			  }
                
                // 计算借贷金额和
                if (detail.getdAmt() != null) {
                    damtsum = damtsum.add(detail.getdAmt());
                }
                if (detail.getcAmt() != null) {
                    camtsum = camtsum.add(detail.getcAmt());
                }
            }
            // 画面.分录类型=“正常记账” and 借方金额之和！=贷方金额之和
            if (DictCodeListExt1.分录类型_正常记账.getValue().equals(dto.getRecordType())
                && damtsum.compareTo(camtsum) != 0) {
                // 借贷不相等，请重新输入
                String msg = this.getMessage("MANUAL_VOUCHER_E_007");
                errorList.add(new ValidMessage(msg));
                return errorList;
            }
        }
        
        //判断当前流程是否存在
        if(StringUtil.isEmpty(dto.getWorkflowId()) && valManualVoucher003Service.checkBusinessDataInFlow(dto)){
            //流程存在情况
            errorList.add(new ValidMessage(getMessage("COMMON_E_0021","标识",dto.getSeqno())));
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
