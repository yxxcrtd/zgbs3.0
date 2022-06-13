/*
 * 文件名：ProfitDistributionBookProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年6月5日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.profitdistribution.service;

import com.google.common.collect.Maps;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokStatBokPosition;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.profitdistribution.dto.ProfitDistributionReaderDto;
import com.joyintech.tams.valuation.batch.profitdistribution.dto.ProfitDistributionResult;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.service.BookAccountCommonService;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 利润分配批处理 - Service
 * 
 * @author chengzhong
 * @version 1.0 2018年6月5日
 * @see ProfitDistributionBookProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ProfitDistributionBookProcessorNewService {

    /**
     * 批处理账务相关公用方法
     */
    @Autowired
    private AccountingCommonService accountCommonService;

    @Autowired
    private BookAccountCommonService bookAccountCommonService;

    /**
     * mapper
     */
//    @Autowired
//    private ProfitDistributionBookMapper profitDistributionBookMapper;
    
    /**
     * 记账公共类
     */
//    @Autowired
//    private BookCommonLogic bookCommonLogic;
    
    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    @Autowired
    private DBUtil dBUtil;

    /**
     * process 数据处理
     * 
     * @return BookGenerateResult 入账结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public ProfitDistributionResult execute(Map<String, Object> params)
         {
        // 获取reader返回值
        ProfitDistributionReaderDto acctEntity = (ProfitDistributionReaderDto)params.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }

        // 定义此process的返回变量（因为浮动管理费要塞入帐套资产负债插入及更新，故用此实体类）
        ProfitDistributionResult result = new ProfitDistributionResult();
        BookGenerateResult returnResult = new BookGenerateResult();

        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }
        returnResult.setProdId(acctEntity.getBooksetId());
         // 1.数据准备
         // 1.1 未分配利润_已实现利润科目最新余额查询
         SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
         // 账套代码 = 利润分配账务处理结果.账套代码
         searchDto.setBooksetId(acctEntity.getBooksetId());
         // 发生日 = 利润分配账务处理结果.发生日
         searchDto.setHappenDate(acctEntity.getHappenDate());
         // 入账日 = 利润分配账务处理结果.入账日
         searchDto.setBookDate(acctEntity.getBookDate());
         searchDto.setBaseSubjectNatureList(
                 new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.利润分配_未分配利润_已实现.getValue())));
         searchDto.setBokDetailId(acctEntity.getBokDetailId());//账务明细代码
         List<SubjectBalance> listSubjectBalance_未分配利润已实现 = accountCommonService.getSubjectLatestBalance(
                 searchDto);

         searchDto.setBaseSubjectNatureList(
                 new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.利润分配_未分配利润_未实现.getValue())));
         searchDto.setBokDetailId(acctEntity.getBokDetailId());//账务明细代码
         List<SubjectBalance> listSubjectBalance_未分配利润未实现 = accountCommonService.getSubjectLatestBalance(
                 searchDto);

         searchDto.setBaseSubjectNatureList(
                 new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.利润分配_应付利润.getValue())));
         List<SubjectBalance> listSubjectBalance_利润分配 = accountCommonService.getSubjectLatestBalance(
                 searchDto);
         BokStatBokPositionDto position=new BokStatBokPositionDto();

         //查询日开始
         position.setSdateStart(acctEntity.getHappenDate());
         //查询结束日
         position.setSdateEnd(acctEntity.getHappenDate());
         //入账日
         position.setBookDate( acctEntity.getBookDate());
         //账套
         position.setBooksetId(acctEntity.getBooksetId());

         //分层代码
         position.setLayeringId(acctEntity.getLayeringId());

         // 1.3 当日账套资产负债信息查询
         // 变量.当日帐套资产负债信息列表 =调用共通方法查询：帐套资产负债查询
         // （利润分配账务处理结果.发生日，利润分配账务处理结果.发生日，利润分配账务处理结果.帐套代码，利润分配账务处理结果.入账日）
         List<BokStatBokPosition> listBokStatBokPosition = accountCommonService.getValStatBokPosition(position);

         listBokStatBokPosition.forEach(item -> {
             item.setProfitAmt(BigDecimal.ZERO);
             // 增加分层代码
             item.setLayeringId(acctEntity.getLayeringId());
         });

         List<ValBokBookdetailBatch> listValBokBookdetailBatch = new ArrayList<ValBokBookdetailBatch>();
         // 2. 利润分配处理
         if (listSubjectBalance_未分配利润已实现 != null && listSubjectBalance_未分配利润已实现.size() > 0) {
             for (SubjectBalance subjectBalance : listSubjectBalance_未分配利润已实现) {//FIXME ADD BY DENGYU一定有已实现，如果只有未实现，没有已实现该出账会有bug，且此科目余额list必须只有一个数据
                 // 初始化定义 - 记账场景要素管理
                 AccountDriver ad = new AccountDriver();
                 // 初始化定义 - 存放该笔记账的场景要素 map定义
                 Map<String, Object> accountSceneMap = Maps.newHashMap();
                 // 初始化定义 - 存放该笔记账的相关金额要素 map定义
                 Map<String, BigDecimal> accountAmountMap = Maps.newHashMap();
                 // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
                 Map<String, Object> accountCurrencyMap = Maps.newHashMap();
                 // 记账场景要素map塞值,(调用共通方法-计提类场景元素赋值)
                 // 账套代码 -> 交易类账务处理结果.账套代码
                 accountSceneMap.put(BookConstant.BOOKSET_ID, acctEntity.getBooksetId());
                 // 发生日 -> 交易类账务处理结果.发生日
                 accountSceneMap.put(BookConstant.HAPPEN_DATE, acctEntity.getHappenDate());
                 // 入账日 -> 交易类账务处理结果.入账日
                 accountSceneMap.put(BookConstant.BOOK_DATE, acctEntity.getBookDate());
                 // 账套日期 -> 交易明细.账套代码
                 accountSceneMap.put(BookConstant.BOOKSET_DATE, acctEntity.getBooksetDate());
                 // 任务代码 -> 交易明细.任务代码
                 accountSceneMap.put(BookConstant.TASK_ID, acctEntity.getTaskId());
                 // 任务优先级 -> 交易明细.任务优先级
                 accountSceneMap.put(BookConstant.TASK_PRI, acctEntity.getTaskPri());
                 // 凭证类型 -> 【日终:01】
                 accountSceneMap.put(BookConstant.BOOK_TYPE, DictCodeList.凭证类型_日终.getValue());
                 // 本位币 -> 交易明细.本位币
                 accountSceneMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                 // 账务明细代码1->交易类账务处理结果.账务明细代码
                 BokDetailId valBokDetailId = new BokDetailId();
                 valBokDetailId.setBusiId(acctEntity.getFinprodId());
                 valBokDetailId.setBusiDetailType(acctEntity.getFinprodType());
                 valBokDetailId.setBokDetailId(acctEntity.getBokDetailId());
                 accountSceneMap.put(BookConstant.BOK_DETAIL_ID1,valBokDetailId);
                 // 是否累计余额
                 accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_是.getValue());
                 // 交易编号 - > (计提类生成的账户明细，不需要塞交易编号)
                 accountSceneMap.put(BookConstant.TRADE_ID, null);
                 // 冲正标识 - （往账务明细（当前表））默认塞入正常
                 accountSceneMap.put(BookConstant.OFFSET_FLAG, DictCodeList.冲正标识_正常交易.getValue());
                 // 是否0入账
                 accountSceneMap.put(BookConstant.IS_ZERO_ACCOUNT, null);
                 //追加remark
                 accountSceneMap.put(BookConstant.BOOK_REMARK,  "产品(货币型)-应付利润结转");
                 // 会计场景设置
                 // if 交易标的DTO.金融产品类型 = “债券借贷融入”
                 // ad.会计场景代码="产品_货币型_应付利润结转"
                 ad.setTableId(DictCodeListTableId.会计场景代码_产品_货币型_应付利润结转.getValue());
                 // 塞入记账场景树节点 = 会计场景
                 ad.setTreeNode(acctEntity.getBookSetTreeNode());

                 // 01 未分配利润已实现-本币余额处理
                 BigDecimal dfplrb = BigDecimal.ZERO;
                 if (DictCodeList.借贷方向_贷.getValue().equals(subjectBalance.getBalFlagHappenDate())) {
                     dfplrb = subjectBalance.getbAmt();
                 } else {
                     dfplrb = subjectBalance.getbAmt().negate();
                 }

                 // 02 利润分配-本币余额处理
                 BigDecimal yflrb = dfplrb;
                 if (listSubjectBalance_利润分配 != null && listSubjectBalance_利润分配.size() > 0) {
                     SubjectBalance balancelrfp = listSubjectBalance_利润分配.get(0);
                     if (DictCodeList.借贷方向_贷.getValue().equals(balancelrfp.getBalFlagHappenDate())) {
                         yflrb = dfplrb.add(balancelrfp.getbAmt());
                     }
                     else {
                         yflrb = dfplrb.subtract(balancelrfp.getbAmt());
                     }
                 }

                 // 03 未分配利润未实现-本币余额处理
                 BigDecimal wfplrb = BigDecimal.ZERO;
                 if(listSubjectBalance_未分配利润未实现 != null && listSubjectBalance_未分配利润未实现.size() > 0) {
                     SubjectBalance balancewfplr = listSubjectBalance_未分配利润未实现.get(0);
                     if (DictCodeList.借贷方向_贷.getValue().equals(balancewfplr.getBalFlagHappenDate())) { // 贷
                         wfplrb = balancewfplr.getbAmt();
                         yflrb = yflrb.add(wfplrb);
                     } else {//未分配的余额是负数，就不结转,如果需要结转则放开
                         //wfplrb = balancewfplr.getbAmt().negate();
                         //yflrb=yflrb.subtract(balancewfplr.getbAmt());
                     }
                 }

                 // 应收收入 <- 变量.应计利息
                 accountAmountMap.put(BookConstant.DIVIDENDAMT, BigDecimal.ZERO);//原币都0
                 accountAmountMap.put(BookConstant.DIVIDENDAMT + BookConstant.BCCYAMT, dfplrb);//本币

                 // 应收支出 <- 变量.应计利息
                 accountAmountMap.put(BookConstant.DIVIDENDAMT_INT, BigDecimal.ZERO);//原币都0
                 accountAmountMap.put(BookConstant.DIVIDENDAMT_INT + BookConstant.BCCYAMT, yflrb);//本币

                 // 未分配利润-未实现
                 accountAmountMap.put(BookConstant.UN_DIVIDENDAMT_INT, BigDecimal.ZERO);//原币都0
                 accountAmountMap.put(BookConstant.UN_DIVIDENDAMT_INT + BookConstant.BCCYAMT, wfplrb);//本币


                 accountCurrencyMap.put(BookConstant.DIVIDENDAMT + BookConstant.BCCYAMT, acctEntity.getStandardCcy());
                 accountCurrencyMap.put(BookConstant.DIVIDENDAMT_INT + BookConstant.BCCYAMT, acctEntity.getStandardCcy());
                 accountCurrencyMap.put(BookConstant.UN_DIVIDENDAMT_INT + BookConstant.BCCYAMT, acctEntity.getStandardCcy());
                 // 应收收入 <- 计提明细.交易币种
                 // 应收支出 <- 计提明细.交易币种
                 accountCurrencyMap.put(BookConstant.DIVIDENDAMT, acctEntity.getStandardCcy());
                 accountCurrencyMap.put(BookConstant.DIVIDENDAMT_INT, acctEntity.getStandardCcy());
                 accountCurrencyMap.put(BookConstant.UN_DIVIDENDAMT_INT, acctEntity.getStandardCcy());

                 // map塞入场景要素管理中
                 ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                 ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                 ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                 // 获取待处理任务信息
                 SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)params.get("BATCHINFO");
                 // 记账凭证生成
                 BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
                 // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                 returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

                 // 5.账套资产负债信息更新
                 if (!CollectionUtils.isEmpty(listBokStatBokPosition)) {
                     // 5.3 资产负债信息赋值
                     for (BokStatBokPosition item : listBokStatBokPosition) {
                         // 变量.当日帐套资产负债信息列表.应付利润 = 返回.余额
                         item.setProfitAmt(ArithmeticUtil.round(yflrb, NumberEnum.MONEY));

                         // 设值科目余额查询参数Dto
                         SubjectBalanceSearchDto search = new SubjectBalanceSearchDto();
                         search.setHappenDate(acctEntity.getHappenDate());//发生日
                         search.setBookDate(acctEntity.getBookDate());//入账日
                         search.setBokDetailId(acctEntity.getBokDetailId());//账务明细代码
                         search.setBooksetId(acctEntity.getBooksetId());//账套代码
                         List<String> baseSubjectNatureList = new ArrayList<String>();
                         baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.应付利润.getValue());
                         search.setBaseSubjectNatureList(baseSubjectNatureList);//基础科目性质
                         search.setTaskPri(acctEntity.getTaskPri());//任务优先级
                         // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
                         List<SubjectBalance> subjectBalanceList = accountCommonService.getSubjectBalanceExpectDirection(search, DictCodeList.余额方向_贷方.getValue());

                         // 获取科目余额
                         if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
                             // 变量.累计应付利润 = DTO.科目余额
                             BigDecimal profitamtNow = subjectBalanceList.get(0).getbAmt();
                             // 变量.当日帐套资产负债信息列表.累计应付利润 = 变量.累计应付利润
                             item.setTotProfitAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), RoundingMode.HALF_UP, yflrb, profitamtNow));
                         } else {
                             item.setTotProfitAmt(ArithmeticUtil.round(yflrb, NumberEnum.MONEY));
                         }

                         // 变量.当日帐套资产负债信息列表.资产净值 = 变量.当日帐套资产负债信息列表.资产净值 -变量.当日帐套资产负债信息列表.应付利润
                         item.setNetAssetValue(ArithmeticUtil.round(ArithmeticUtil.subtract(item.getNetAssetValue(),
                                 item.getProfitAmt()), NumberEnum.MONEY));
                     }
                 }
             }
         }
         // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
         returnResult.getValBokBookdetailBatch().addAll(listValBokBookdetailBatch);
         result.setBookGenerateResult(returnResult);
         result.setListBokStatBokPosition(listBokStatBokPosition);

         return result;
    }
}
