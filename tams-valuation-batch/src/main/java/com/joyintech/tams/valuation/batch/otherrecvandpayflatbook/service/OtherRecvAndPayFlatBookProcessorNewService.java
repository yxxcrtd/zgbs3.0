/*
 * 文件名：secDealAccountsProcessServices.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年5月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.otherrecvandpayflatbook.service;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.common.base.dao.BokDetailIdMapper;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokDetailIdKey;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 其他应收应付平账账务处理
 * @author weipeng
 * @version 1.0 2019年4月11日
 * @see OtherRecvAndPayFlatBookProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class OtherRecvAndPayFlatBookProcessorNewService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    /**
     * 公用方法services
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;
    
    /**
     * 记账公共类
     */
    @Autowired
    private BokDetailIdMapper bokDetailIdMapper;

    /**
     * process 数据处理
     * 
     * @param param1 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResultWithBokSummary execute(Map<String, Object> param1) {
        // 获取reader返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult)param1.get(
            GlobalConstant.READER_RESULT);
        // 定义返回值
        BookGenerateResultWithBokSummary returnResult = new BookGenerateResultWithBokSummary();
        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }
        
        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return returnResult;
        }

        // 获取从reader中拿到的值
        String booksetId = acctEntity.getBooksetId();// 账套代码
        TreeNode bookSetTreeNod = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDate = acctEntity.getHappenDate();// 发生日期
        Date bookDate = acctEntity.getBookDate();// 发生日
        // 计提基础信息列表
        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTO = acctEntity.getListDrawBaseInfoSelectComDTO(); 
        
        // 获取账务明细代码
        BokDetailIdKey bokDetailIdKey = new BokDetailIdKey();
        bokDetailIdKey.setBokDetailId(acctEntity.getBokDetailId());
        bokDetailIdKey.setBooksetId(booksetId);
        BokDetailId bokDetailId = bokDetailIdMapper.selectByPrimaryKey(bokDetailIdKey);
        // IF 账务明细代码.金融产品类型 != ‘债券’ || 账务明细代码.投资目的 ！= 卖空
        if(bokDetailId == null || !DictCodeListExt1.金融产品类型_债券.getValue().equals(bokDetailId.getBusiDetailType())
            || !DictCodeListExt1.投资目的_卖空.getValue().equals(bokDetailId.getInvAim())){
            return returnResult;
        }
        
        // 定义变量
        // 变量.其他应收款-本金
        BigDecimal otherCostAmt = BigDecimal.ZERO;
        // 变量.其他应收款-利息
        BigDecimal otherIntAmt = BigDecimal.ZERO;
        // 变量.其他应付款-融入券本金
        BigDecimal otherPayCostAmt = BigDecimal.ZERO;
        // 变量.其他应付款-融入券利息
        BigDecimal otherPayIntAmt = BigDecimal.ZERO; 
        
        // 循环金融产品交易信息list,进行逻辑处理
        for (DrawBaseInfoSelectComDTO dto : listDrawBaseInfoSelectComDTO) {
            // 定义 变量.其他应收款-融入券本金/利息科目余额方向
            String otherBalFlag = "";
            // 定义 变量.其他应付款-融入券本金/利息科目余额方向 = 明细.余额方向（发生日）
            String otherPayBalFlag = dto.getBalFlagHappenDate();
            // IF 计提类账务处理结果.基础科目性质 = 其他应付款_融入券本金
            if(DictCodeBaseSubjectNatrueNew.其他应付款_本金.getValue().equals(dto.getBaseSubjectNature())){
                // 1.1 变量定义
                // 其他应付款-融入券本金 = DTO.科目余额
                otherPayCostAmt = dto.getoAmtHappenDate();
                // 2.1.其他收类科目余额
                // 调用共通设计->变量.详细设计-账务类共通方法设计.xlsx->科目余额查询-期待方向SHEET
                // 变量.其他应付款-融入券本金 = DTO.科目余额 
                SubjectBalance subjectBalance1 = this.getSubjectBalance(booksetId,
                    acctEntity.getBokDetailId(), happenDate, bookDate,
                    DictCodeList.余额方向_贷方.getValue(), DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), null, dto.getBranch());
                otherCostAmt = subjectBalance1.getBalance();
                // 变量.其他应收款-本金科目余额方向 = DTO.科目余额方向
                otherBalFlag = subjectBalance1.getBalFlagHappenDate();
            }
            else{
                // 1.1 变量定义
                // 其他应付款-融入券利息 = DTO.科目余额
                otherPayIntAmt = dto.getoAmtHappenDate();
                
                // 2.1.其他收类科目余额
                // 调用共通设计->变量.详细设计-账务类共通方法设计.xlsx->科目余额查询-期待方向SHEET
                SubjectBalance subjectBalance2 = this.getSubjectBalance(booksetId,
                    acctEntity.getBokDetailId(), happenDate, bookDate,
                    DictCodeList.余额方向_贷方.getValue(), DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(), null, dto.getBranch());
                // 变量.其他应收款-利息 = DTO.科目余额 
                otherIntAmt = subjectBalance2.getBalance();
                // 变量.其他应收款-利息科目余额方向 = DTO.科目余额方向
                otherBalFlag = subjectBalance2.getBalFlagHappenDate();
            }
            
            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();
            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();
            //基础科目性质
            Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
            Map<String, Object> accountNatrueBranchMap = new HashMap<>();

            // **********共通元素设值 - 设置账务引擎参数**********
            // ad.setAccountType(DictCodeList.账务记账类型_正常记账.getValue());//ad.凭证类型=#正常记账#;
            // ad.setTradeId(deal.getTradeId());//ad.交易编号=交易编号
            ad.setTreeNode(bookSetTreeNod);// 塞入记账场景树节点 = 会计场景
            // ad.setAccountType(DictCodeList.账务记账类型_正常记账.getValue());//ad.凭证类型=#正常记账#;
            // 场景代码
            ad.setTableId(DictCodeListTableId.会计场景代码_其他应收应付平账.getValue());

            // ********** accountSceneMap设置如下**********
            accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity, dto);
            // 备注 <- "非货币基金公允价值变动" REMARK
            accountSceneMap.put(BookConstant.BOOK_REMARK, "其他应收应付平账处理");

            // ********** accountAmountMap设置如下**********
            // IF 计提类账务处理结果.基础科目性质 = 其他应付款_融入券本金
            if(DictCodeBaseSubjectNatrueNew.其他应付款_本金.getValue().equals(dto.getBaseSubjectNature())){
                // *账务明细代码为DTO.账务明细代码，分支序号为DTO.分支序号
                // 分支序号共通设值需要通过基础科目性质map赋值
                accountNatrueBranchMap.put(BookConstant.BRANCH, dto.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), accountNatrueBranchMap);
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.其他应付款_本金.getValue(), accountNatrueBranchMap);
                // 1） 变量.其他应收款-本金.abs()≥变量.其他应付款-融入券本金.abs()时:
                if(otherCostAmt.abs().compareTo(otherPayCostAmt.abs()) >= 0){
                    // a)   变量.其他应付款-融入券本金余额在借方时，
                    if(DictCodeList.余额方向_借方.getValue().equals(otherPayBalFlag)){
                        // 其他应收款-本金_借 -> 变量.其他应付款-融入券本金科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_COST_D, otherPayCostAmt.abs());
                        // 其他应付款-融入券本金_贷 -> 变量.其他应付款-融入券本金科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_COST1_C, otherPayCostAmt.abs());
                    }
                    // a)   变量.其他应付款-融入券本金余额在贷方时，
                    if(DictCodeList.余额方向_贷方.getValue().equals(otherPayBalFlag)){
                        // 其他应付款-融入券本金_借 -> 变量.其他应付款-融入券本金科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_COST1_D, otherPayCostAmt.abs());
                        // 其他应收款-本金_贷 -> 变量.其他应付款-融入券本金科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_COST_C, otherPayCostAmt.abs());
                    }
                }
                // 2） 凭证前科目变量.其他应收款-本金.abs()<变量.其他应付款-融入券本金.abs()时:
                if(otherCostAmt.abs().compareTo(otherPayCostAmt.abs()) < 0){
                    // b)   变量.其他应收款-本金余额在借方时，
                    if(DictCodeList.余额方向_借方.getValue().equals(otherBalFlag)){
                        // 其他应付款-融入券本金_借 -> 变量.其他应收款-本金科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_COST1_D, otherCostAmt.abs());
                        // 其他应收款-本金_贷 -> 变量.其他应收款-本金科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_COST_C, otherCostAmt.abs());
                        
                    }
                    // b)   变量.其他应收款-本金余额在贷方时，
                    if(DictCodeList.余额方向_贷方.getValue().equals(otherBalFlag)){
                        // 其他应收款-本金_借 -> 变量.其他应收款-本金科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_COST_D, otherCostAmt.abs());
                        // 其他应付款-融入券本金_贷 -> 变量.其他应收款-本金科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_COST1_C, otherCostAmt.abs());
                    }
                }
            }
            // 变量.其他应收款-利息、其他应付款-融入券利息同理。
            else{
                // *账务明细代码为DTO.账务明细代码，分支序号为DTO.分支序号
                // 分支序号共通设值需要通过基础科目性质map赋值
                accountNatrueBranchMap.put(BookConstant.BRANCH, dto.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(), accountNatrueBranchMap);
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.其他应付款_利息.getValue(), accountNatrueBranchMap);
                // 1） 变量.其他应收款-利息.abs()>变量.其他应付款-融入券利息.abs()时:
                if(otherIntAmt.abs().compareTo(otherPayIntAmt.abs()) > 0){
                    // a)   变量.其他应付款-融入券利息余额在借方时，
                    if(DictCodeList.余额方向_借方.getValue().equals(otherPayBalFlag)){
                        // 其他应收款-利息_借 -> 变量.其他应付款-融入券利息科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_INT_D, otherPayIntAmt.abs());
                        // 其他应付款-融入券利息_贷 -> 变量.其他应付款-融入券利息科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_INT1_C, otherPayIntAmt.abs());
                    }
                    // a)   变量.其他应付款-融入券利息余额在贷方时，
                    if(DictCodeList.余额方向_贷方.getValue().equals(otherPayBalFlag)){
                        // 其他应付款-融入券利息_借 -> 变量.其他应付款-融入券利息科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_INT1_D, otherPayIntAmt.abs());
                        // 其他应收款-利息_贷 -> 变量.其他应付款-融入券利息科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_INT_C, otherPayIntAmt.abs());
                    }
                }
                // 2） 凭证前科目变量.其他应收款-利息.abs()<=变量.其他应付款-融入券利息.abs()时:
                if(otherIntAmt.abs().compareTo(otherPayIntAmt.abs()) <= 0){
                    // b)   变量.其他应收款-利息余额在借方时，
                    if(DictCodeList.余额方向_借方.getValue().equals(otherBalFlag)){
                        // 其他应付款-融入券利息_借 -> 变量.其他应收款-利息科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_INT1_D, otherIntAmt.abs());
                        // 其他应收款-利息_贷 -> 变量.其他应收款-利息科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_INT_C, otherIntAmt.abs());
                        
                    }
                    // b)   变量.其他应收款-利息余额在贷方时，
                    if(DictCodeList.余额方向_贷方.getValue().equals(otherBalFlag)){
                        // 其他应收款-利息_借 -> 变量.其他应收款-利息科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_INT_D, otherIntAmt.abs());
                        // 其他应付款-融入券利息_贷 -> 变量.其他应收款-利息科目余额.abs()
                        accountAmountMap.put(BookConstant.OTHER_INT1_C, otherIntAmt.abs());
                    }
                }
            }

            // 记账金额元素币种信息塞值
            // 因为与记账金额元素map的key及数量一致，故采用循环
            for (String key : accountAmountMap.keySet()) {
                accountCurrencyMap.put(key, dto.getoCcy());// value值塞入当前交易币种 （当前为获取计提里面的原币）
            }

            

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
            ad.setAccountNatrueMap(accountNatrueMap); // 基础科目性质要素
            // 记账凭证生成
            BookGenerateResult result = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(
                result.getValBokBookdetailBatch());
        }
        return returnResult;
    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     * 
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDate 发生日
     * @param bookDate 入账日
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param invAim 投资目的
     * @param taskPri 任务优先级
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId, Date happenDate,
                                             Date bookDate, String expBalanceDirect,
                                             String baseSubjectNature, String invAim,
                                             Long branch) {

        // 定义返回的model
        SubjectBalance subjectBalance = new SubjectBalance();
        subjectBalance.setBalance(BigDecimal.ZERO);

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 塞入投资目的
        List<String> invAimList = null;
        if (invAim != null && invAim.length() > 0) {
            invAimList = new ArrayList<String>();
            invAimList.add(invAim);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        searchDto.setBranch(branch);
        searchDto.setInvAimList(invAimList);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);
        if (subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }
        return subjectBalance;
    }

}
