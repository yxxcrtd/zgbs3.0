/*
 * 文件名：BankCalBookProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：Apr 11, 2019
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.batch.bankcalbook.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.BokBankBalance;
import com.joyintech.tams.common.base.entity.BokBankSceConfig;
import com.joyintech.tams.common.base.entity.BokSubject;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.bankcalbook.dto.BankCalBookComResult;
import com.joyintech.tams.valuation.batch.bankcalbook.dto.BankCalBookGenerateResult;
import com.joyintech.tams.valuation.batch.bankcalbook.dto.BokVouchDetailDto;
import com.joyintech.tams.valuation.batch.bankcalbook.dto.BokVouchMainDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 银行账计提账务处理 ProcessorService
 * 
 * @author zhangwei
 * @version 1.0 Apr 11, 2019
 * @see BankCalBookProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class BankCalBookProcessorService {

    @Autowired
    private AccountingCommonService accountingCommonService;

    @Autowired
    private DBUtil dBUtil;

    // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 start
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 end

    public BankCalBookGenerateResult execute(Map<String, Object> param) throws Exception {
        // 初始化返回值
        BankCalBookGenerateResult returnResult = new BankCalBookGenerateResult();
        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取reader返回值
        BankCalBookComResult readerEntity = (BankCalBookComResult)param.get(GlobalConstant.READER_RESULT);
        if (readerEntity == null) {// 若reader传递的参数为 null，不进行后续处理
            return null;
        }
        // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 start
        try{
            // 1. 处理场景查询
            // 1.1 调用共通【银行账场景配置】
            List<BokBankSceConfig> listBokBankSceConfig = accountingCommonService.getBokBankSceConfig(
                    readerEntity.getBooksetId(), DictCodeList.计提区分_计提.getValue(), null);

            // 1.2 调用共通【科目查询】
            Map<String, Object> pMap = Maps.newHashMap();
            pMap.put("booksetId", readerEntity.getBooksetId());
            List<BokSubject> listBokSubject = accountingCommonService.getSubjectInfo(pMap);

            // 2.处理
            // 变量.金融产品代码 = 产品列表.金融产品代码
            String finprodId = readerEntity.getFinprodId();
            // 变量.分支序号= 产品列表.分值序号
            Long branch = readerEntity.getBranch();
            // 变量.业务明细代码 = 产品列表.业务明细代码
            String busiId = readerEntity.getBusiId();
            // 变量.币种 = 产品列表.本位币
            String ccy = readerEntity.getCcy();

            // list<DTO<凭证信息，凭证子表<凭证明细list>>>
            List<BokVouchMainDto> listBokVouchMainDto = Lists.newArrayList();

            // LOOP 银行账场景配置列表
            for (BokBankSceConfig bokBankSceConfig : listBokBankSceConfig) {

                // 变量.银行账场景代码 = 银行账场景配置列表.场景代码
                String bankTableId = bokBankSceConfig.getTableId();
                // if 银行账场景配置列表.场景代码！=“产品公允价值变动”
                if (!DictCodeList.银行账场景代码_产品公允价值变动.getValue().equals(bokBankSceConfig.getTableId())) {
                    // 调用共通方法查询：科目余额查询，参照共通处理sheet
                    // 变量.本行费用计提金额 = 返回结果.当日发生额
                    List<String> listSubjectType = Lists.newArrayList(new String[] {DictCodeList.科目类型_损益类.getValue()});
                    List<String> listBaseSubjectNature = Lists.newArrayList(
                            new String[] {bokBankSceConfig.getBaseNature()});
                    BigDecimal bankFeeCalBookAmt = accountingCommonService.findSubjectBalance(
                            readerEntity.getBokBooksetId(), listSubjectType, readerEntity.getBookDate(), listBaseSubjectNature);

                    // list<DTO<凭证信息，凭证子表<凭证明细list>>>

                    BokVouchMainDto bokVouchMain = new BokVouchMainDto();
                    // 凭证主表DTO.凭证编号 = 自动生成
                    bokVouchMain.setVouchNum(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
                    // 业务凭证编号
                    bokVouchMain.setBusVouchnum(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));

                    // 凭证主表DTO.账套代码 = DTO.账套代码
                    bokVouchMain.setBooksetId(readerEntity.getBooksetId());
                    // 凭证主表DTO.合并场景代码 = 变量.银行账场景代码
                    bokVouchMain.setComTableId(bankTableId);
                    // 凭证主表DTO.入账日期 = DTO.入账日
                    bokVouchMain.setBookDate(readerEntity.getBookDate());
                    // 凭证主表DTO.凭证说明=变量.银行账场景代码对应的数据字典名称
                    bokVouchMain.setVouchRemark(SysDataUtil.getNameFromDataDict(DictCode.银行账场景代码, bankTableId));

                    BokVouchDetailDto bokVouchDetail = new BokVouchDetailDto();
                    // 凭证明细.凭证子表编号 = 自动生成
                    bokVouchDetail.setVouchSubnum(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
                    bokVouchDetail.setVouchNum(bokVouchMain.getVouchNum());

                    // if 银行账场景配置列表.基础科目性质 = “托管费”
                    if (DictCodeBaseSubjectNatrue.托管费.getValue().equals(bokBankSceConfig.getBaseNature())) {
                        // LOOP 科目列表
                        for (BokSubject bokSubject : listBokSubject) {
                            // if 科目列表.科目性质 ！= “代收代付手续费”
                            // continue；
                            if (!DictCodeBankBaseSubjectNatrue.代收代付手续费.getValue().equals(
                                    bokSubject.getBaseSubjectNature())) {
                                continue;
                            }
                            // 凭证明细.科目号 = 科目列表.科目号
                            bokVouchDetail.setSubjectNo(bokSubject.getSubjectNo());
                            // break；
                            break;
                        }
                    }
                    // if 银行账场景配置列表.基础科目性质 = “外包服务费”
                    else if (DictCodeBaseSubjectNatrue.外包服务费.getValue().equals(bokBankSceConfig.getBaseNature())) {
                        // LOOP 科目列表
                        for (BokSubject bokSubject : listBokSubject) {
                            // if 科目列表.科目性质 ！= “代收代付外包服务费”
                            // continue；
                            if (!DictCodeBankBaseSubjectNatrue.代收代付外包服务费.getValue().equals(
                                    bokSubject.getBaseSubjectNature())) {
                                continue;
                            }
                            // 凭证明细.科目号 = 科目列表.科目号
                            bokVouchDetail.setSubjectNo(bokSubject.getSubjectNo());
                            // break；
                            break;
                        }
                    }
                    else {
                        // LOOP 科目列表
                        for (BokSubject bokSubject : listBokSubject) {
                            // if 科目列表.科目性质 ！= “手续费收入”
                            // continue；
                            if (!DictCodeBankBaseSubjectNatrue.手续费收入.getValue().equals(bokSubject.getBaseSubjectNature())) {
                                continue;
                            }
                            // 凭证明细.科目号 = 科目列表.科目号
                            bokVouchDetail.setSubjectNo(bokSubject.getSubjectNo());
                            // break；
                            break;
                        }
                    }
                    // 凭证明细.发生日期 = DTO.发生日
                    bokVouchDetail.setHappenDate(readerEntity.getHappenDate());
                    // 凭证明细.场景代码 = 变量.银行账场景代码
                    bokVouchDetail.setTableId(bankTableId);
                    // 凭证明细.借贷方向 = ‘贷方’
                    bokVouchDetail.setCdFlag(DictCodeList.借贷方向_贷.getValue());
                    // 凭证明细.发生额 = 变量.本行费用计提金额
                    bokVouchDetail.setHappenAmt(bankFeeCalBookAmt == null ? BigDecimal.ZERO : bankFeeCalBookAmt);
                    // 调用共通方法查询：银行账计提余额
                    // 银行计提余额列表
                    List<BokBankBalance> listBankBokBankBalance = accountingCommonService.findBokBankBalance(finprodId,
                            branch, readerEntity.getBooksetId(), bankTableId, readerEntity.getBookDate(),
                            bokVouchDetail.getSubjectNo());
                    // 凭证明细.发生额 = 凭证明细.发生额 - 银行计提余额列表.余额
                    if (CollectionUtils.isNotEmpty(listBankBokBankBalance)) {
                        bokVouchDetail.setHappenAmt(
                                bokVouchDetail.getHappenAmt().subtract(listBankBokBankBalance.get(0).getbAmtBookDate()));
                    }
                    // 凭证明细.币种=变量.币种
                    bokVouchDetail.setCcy(ccy);
                    // 凭证明细.金融产品代码=变量.金融产品代码
                    bokVouchDetail.setFinprodId(finprodId);
                    // 凭证明细.分支序号=变量.分支序号
                    bokVouchDetail.setBranch(branch);
                    // 凭证明细.业务明细代码=变量.业务明细代码
                    bokVouchDetail.setBusiId(busiId);
                    // 凭证明细.凭证编号 = 凭证主表DTO.凭证编号
                    bokVouchDetail.setVouchNum(bokVouchMain.getVouchNum());
                    // 凭证主表DTO.凭证子表.add(凭证明细)
                    bokVouchMain.getListBokVouchDetailDto().add(bokVouchDetail);

                    // 凭证明细copy（）；
                    BokVouchDetailDto newBokVouchDetail = new BokVouchDetailDto();
                    BeanUtils.copyProperties(bokVouchDetail, newBokVouchDetail);
                    // 凭证明细.凭证子表编号 = 自动生成
                    newBokVouchDetail.setVouchSubnum(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
                    newBokVouchDetail.setVouchNum(bokVouchMain.getVouchNum());
                    // LOOP 科目列表
                    for (BokSubject bokSubject : listBokSubject) {
                        // if 科目列表.科目性质 ！= “代理理财业务公允价值变动”
                        if (!DictCodeBankBaseSubjectNatrue.代理理财业务公允价值变动.getValue().equals(
                                bokSubject.getBaseSubjectNature())) {
                            // continue；
                            continue;
                        }
                        // 凭证明细.科目号 = 科目列表.科目号
                        newBokVouchDetail.setSubjectNo(bokSubject.getSubjectNo());

                    }
                    // 凭证明细.借贷方向 = ‘借方’
                    newBokVouchDetail.setCdFlag(DictCodeList.借贷方向_借.getValue());
                    // 凭证主表DTO.凭证子表.add(凭证明细)
                    bokVouchMain.getListBokVouchDetailDto().add(newBokVouchDetail);
                    // 待出账list.add（凭证主表DTO）
                    listBokVouchMainDto.add(bokVouchMain);
                }
                else if (DictCodeList.银行账场景代码_产品公允价值变动.getValue().equals(bokBankSceConfig.getTableId())) {

                    // // 变量.公允价值变动
                    BigDecimal floatAmt = BigDecimal.ZERO;

                    // 调用共通方法【账套参数查询】
                    Map<String, Object> bokParamMap = accountingCommonService.getValBokBooksetExtForMap(
                            readerEntity.getBooksetId(),
                            Lists.newArrayList(new String[] {ValDictCodeList.账套配置表参数项_其他综合收益是否影响公允价值变动.getValue()}));
                    // 变量.其他综合收益是否影响公允价值变动 = 返回值
                    String isIncOci = (String)bokParamMap.get(ValDictCodeList.账套配置表参数项_其他综合收益是否影响公允价值变动.getValue());

                    // 调用共通方法【基础科目性质查询】 银行账公允价值查询对应科目（BANKGYJZBDZC_KMCX）
                    // 变量.科目性质列表 = 返回值
                    List<String> listBaseSubjectNature = accountingCommonService.getValSubjectNatureList(
                            DictCodeListExt1.分组代码_银行账公允价值查询对应科目.getValue());

                    // if 变量.其他综合收益是否影响公允价值变动 =“是”
                    if (DictCodeList.是否_是.getValue().equals(isIncOci)) {
                        // 调用共通方法查询：公允价值变动科目余额查询，参照共通处理sheet
                        List<String> listSubjectType = Lists.newArrayList(new String[] {DictCodeList.科目类型_损益类.getValue()});
                        // 变量.公允价值变动 = 返回结果.当日发生额 (是由理财账套模板映射的)
                        floatAmt = accountingCommonService.findFloatSubjectBalance(readerEntity.getBokBooksetId(),
                                listSubjectType, readerEntity.getBookDate(), listBaseSubjectNature,
                                DictCodeList.是否_是.getValue());

                    }
                    else {
                        // 调用共通方法查询：公允价值变动科目余额查询，参照共通处理sheet
                        List<String> listSubjectType = Lists.newArrayList(new String[] {DictCodeList.科目类型_损益类.getValue()});
                        // 变量.公允价值变动 = 返回结果.当日发生额 (是由理财账套模板映射的)
                        floatAmt = accountingCommonService.findFloatSubjectBalance(readerEntity.getBokBooksetId(),
                                listSubjectType, readerEntity.getBookDate(), listBaseSubjectNature,
                                DictCodeList.是否_否.getValue());
                    }
                    floatAmt = floatAmt == null ? BigDecimal.ZERO : floatAmt;

                    BokVouchMainDto bokVouchMain = new BokVouchMainDto();
                    // 凭证主表DTO.凭证编号 = 自动生成
                    bokVouchMain.setVouchNum(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
                    // 业务凭证编号
                    bokVouchMain.setBusVouchnum(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
                    // 凭证主表DTO.账套代码 = DTO.账套代码
                    bokVouchMain.setBooksetId(readerEntity.getBooksetId());
                    // 凭证主表DTO.合并场景代码 = 变量.银行账场景代码
                    bokVouchMain.setComTableId(bankTableId);
                    // 凭证主表DTO.入账日期 = DTO.入账日
                    bokVouchMain.setBookDate(readerEntity.getBookDate());
                    // 凭证主表DTO.凭证说明=变量.银行账场景代码对应的数据字典名称
                    bokVouchMain.setVouchRemark(SysDataUtil.getNameFromDataDict(DictCode.银行账场景代码, bankTableId));

                    BokVouchDetailDto bokVouchDetail = new BokVouchDetailDto();
                    // 凭证明细.凭证子表编号 = 自动生成
                    bokVouchDetail.setVouchSubnum(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
                    bokVouchDetail.setVouchNum(bokVouchMain.getVouchNum());
                    // LOOP 科目列表
                    for (BokSubject bokSubject : listBokSubject) {
                        // if 科目列表.科目性质 ！= “代理理财投资公允价值变动”
                        // continue；
                        if (!DictCodeBankBaseSubjectNatrue.代理理财投资公允价值变动.getValue().equals(
                                bokSubject.getBaseSubjectNature())) {
                            continue;
                        }
                        // 凭证明细.科目号 = 科目列表.科目号
                        bokVouchDetail.setSubjectNo(bokSubject.getSubjectNo());
                        // break；
                        break;
                    }
                    // 凭证明细.发生日期 = DTO.发生日
                    bokVouchDetail.setHappenDate(readerEntity.getHappenDate());
                    // 凭证明细.场景代码 = 变量.银行账场景代码
                    bokVouchDetail.setTableId(bankTableId);

                    // 调用共通方法查询：银行账计提余额
                    // 银行计提余额列表 = 返回值
                    List<BokBankBalance> listBankBokBankBalance = accountingCommonService.findBokBankBalance(finprodId,
                            branch, readerEntity.getBooksetId(), bankTableId, readerEntity.getBookDate(),
                            bokVouchDetail.getSubjectNo());

                    // 首次计提时为空
                    if (CollectionUtils.isNotEmpty(listBankBokBankBalance)) {
                        // 变量.公允价值变动余额
                        BigDecimal floatBalAmt = BigDecimal.ZERO;
                        // if 银行计提余额列表.get（0）.余额方向 = 借方
                        if (DictCodeList.借贷方向_借.getValue().equals(listBankBokBankBalance.get(0).getBalFlagBookDate())) {
                            // 变量.公允价值变动余额 = 银行计提余额列表.get（0）.余额
                            floatBalAmt = listBankBokBankBalance.get(0).getbAmtBookDate();
                        }
                        else {
                            // 变量.公允价值变动余额 = 银行计提余额列表.get（0）.余额*-1
                            floatBalAmt = listBankBokBankBalance.get(0).getbAmtBookDate().negate();
                        }

                        // 变量.公允价值变动 = 变量.公允价值变动 - 变量.公允价值变动余额
                        floatAmt = floatAmt.subtract(floatBalAmt);
                    }

                    // if 变量.公允价值变动 >0
                    if (floatAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 凭证明细.借贷方向 = ‘借方’
                        bokVouchDetail.setCdFlag(DictCodeList.借贷方向_借.getValue());
                    }
                    else {
                        // 凭证明细.借贷方向 = ‘贷方’
                        bokVouchDetail.setCdFlag(DictCodeList.借贷方向_贷.getValue());
                    }

                    // 凭证明细.发生额 = abs(变量.公允价值变动 )
                    bokVouchDetail.setHappenAmt(floatAmt.abs());
                    // 凭证明细.币种=变量.币种
                    bokVouchDetail.setCcy(ccy);
                    // 凭证明细.金融产品代码=变量.金融产品代码
                    bokVouchDetail.setFinprodId(finprodId);
                    // 凭证明细.业务明细代码=变量.业务明细代码
                    bokVouchDetail.setBusiId(busiId);
                    // 凭证明细.分支序号=变量.分支序号
                    bokVouchDetail.setBranch(branch);
                    // 凭证明细.凭证编号 = 凭证主表DTO.凭证编号
                    bokVouchDetail.setVouchNum(bokVouchMain.getVouchNum());
                    // 凭证主表DTO.凭证子表.add(凭证明细)
                    bokVouchMain.getListBokVouchDetailDto().add(bokVouchDetail);

                    // 凭证明细copy（）；
                    BokVouchDetailDto newBokVouchDetail = new BokVouchDetailDto();
                    BeanUtils.copyProperties(bokVouchDetail, newBokVouchDetail);
                    // 凭证明细.凭证子表编号 = 自动生成
                    newBokVouchDetail.setVouchSubnum(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
                    newBokVouchDetail.setVouchNum(bokVouchMain.getVouchNum());
                    // LOOP 科目列表
                    for (BokSubject bokSubject : listBokSubject) {
                        // if 科目列表.科目性质 ！= “代理理财业务公允价值变动”
                        if (!DictCodeBankBaseSubjectNatrue.代理理财业务公允价值变动.getValue().equals(
                                bokSubject.getBaseSubjectNature())) {
                            // continue；
                            continue;
                        }
                        // 凭证明细.科目号 = 科目列表.科目号
                        newBokVouchDetail.setSubjectNo(bokSubject.getSubjectNo());
                        break;
                    }
                    // if 变量.公允价值变动 >0
                    if (floatAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 凭证明细.借贷方向 = ‘贷方’
                        newBokVouchDetail.setCdFlag(DictCodeList.借贷方向_贷.getValue());
                    }
                    else {
                        // 凭证明细.借贷方向 = ‘借方’
                        newBokVouchDetail.setCdFlag(DictCodeList.借贷方向_借.getValue());
                    }

                    // 凭证主表DTO.凭证子表.add(凭证明细)
                    bokVouchMain.getListBokVouchDetailDto().add(newBokVouchDetail);
                    // 待出账list.add（凭证主表DTO）
                    listBokVouchMainDto.add(bokVouchMain);
                }
            }
            // 待出账list.add（凭证主表DTO）
            returnResult.setListBokVouchMainDto(listBokVouchMainDto);
            return returnResult;
        }
        catch (Exception e){
            logger.error("《银行账计提账务处理》报错相关信息： 账套信息【"+readerEntity.getBooksetId()+"】；金融产品代码【"+readerEntity.getFinprodId()+"】",e);
            throw new Exception(e);
        }
        // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 end
    }

}
