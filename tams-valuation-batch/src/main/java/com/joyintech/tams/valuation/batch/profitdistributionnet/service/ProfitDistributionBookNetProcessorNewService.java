/*
 * 文件名：ProfitDistributionBookNetProcessorService.java
 * 版权：Copyright 2020-2021 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.1
 * 修改人：WUJIE
 * 修改时间：2020年4月1日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.1
 */
package com.joyintech.tams.valuation.batch.profitdistributionnet.service;

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
import com.joyintech.tams.valuation.batch.profitdistributionnet.dto.ProfitDistributionNetReaderDto;
import com.joyintech.tams.valuation.batch.profitdistributionnet.dto.ProfitDistributionNetResult;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 净值型产品利润分配批处理 - Service
 * 
 * @author WUJIE
 * @version 1.0 2020年4月1日
 * @see ProfitDistributionBookNetProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ProfitDistributionBookNetProcessorNewService {

    /**
     * 批处理账务相关公用方法
     */
    @Autowired
    private AccountingCommonService accountCommonService;
    
    /**
     * 公用方法services
     */
    @Autowired
    private AccountingCommonService accountingCommonService;


    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    /**
     * process 数据处理
     * 
     * @return BookGenerateResult 入账结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public ProfitDistributionNetResult execute(Map<String, Object> params)
        throws Exception {
        // 获取reader返回值
        ProfitDistributionNetReaderDto acctEntity = (ProfitDistributionNetReaderDto)params.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }
        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)params.get("BATCHINFO");
        // 定义此process的返回变量
        ProfitDistributionNetResult result = new ProfitDistributionNetResult();

        BookGenerateResult returnResult = new BookGenerateResult();

        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }
        returnResult.setProdId(acctEntity.getBooksetId());


        // 1.定义变量
        //定义 变量.当日帐套资产负债信息
        BokStatBokPosition bokStatBokPosition;
        //定义 变量.待分配利润
        BigDecimal profitAmt = BigDecimal.ZERO;
        //定义 变量.分红净值
        BigDecimal 分红净值 = BigDecimal.ZERO;

        //定义 变量.账务明细代码1
        BokDetailId valBokDetailId1 = new BokDetailId();
        valBokDetailId1.setBusiId(acctEntity.getFinprodId());
        valBokDetailId1.setBusiDetailType(acctEntity.getFinprodType());
        valBokDetailId1.setBokDetailId(acctEntity.getBokDetailId());


        //2.调用共通查询
        //2.1 查询当日账套资产负债信息
        BokStatBokPositionDto position = new BokStatBokPositionDto();
        // 查询日开始
        position.setSdateStart(acctEntity.getHappenDate());
        // 查询结束日
        position.setSdateEnd(acctEntity.getHappenDate());
        // 入账日
        position.setBookDate(acctEntity.getBookDate());
        // 账套
        position.setBooksetId(acctEntity.getBooksetId());
        // 分层代码
        position.setLayeringId(acctEntity.getLayeringId());
        List<BokStatBokPosition> bokPositionList = accountCommonService.getValStatBokPosition(
            position);
        if (CollectionUtils.isEmpty(bokPositionList)) {
        	return null;
        }
        bokStatBokPosition = bokPositionList.get(0);

        //【单位净值】小于等于1时不做净值归一
        if(bokStatBokPosition.getNetUnitValue() == null || BigDecimal.ONE.compareTo(bokStatBokPosition.getNetUnitValue())>=0){
        	return null;
        }

        //2.2 查询所有者权益(不含实收资本)的科目余额
        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(acctEntity.getHappenDate());
        searchDao.setBookDate(acctEntity.getBookDate());
        //2.2.1 调用共通方法：科目余额汇总查询 所有者权益
        searchDao.setSubjectTypeList(new ArrayList<String>(
                    Arrays.asList(DictCodeList.科目类型_所有者权益类.getValue())));
        List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_贷方.getValue());
        BigDecimal 所有者权益余额 = null;
        if(!CollectionUtils.isEmpty(listSubjectBalance)){
        	for(SubjectBalance dto :listSubjectBalance){
        		所有者权益余额 = ArithmeticUtil.doAdd(所有者权益余额,dto.getbAmt()) ;
        	}
        }
        //2.2.2 调用共通方法：科目余额汇总查询 实收资本
        searchDao.setSubjectTypeList(null);
        searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.实收资本.getValue())));
        listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_贷方.getValue());
        BigDecimal 实收资本余额 = null;
        if(!CollectionUtils.isEmpty(listSubjectBalance)){
        	实收资本余额 = listSubjectBalance.get(0).getbAmt();
        }
        //变量.待分配利润 =变量.所有者权益余额 - 变量.实收资本科目余额
        profitAmt = ArithmeticUtil.round(ArithmeticUtil.subtract(所有者权益余额, 实收资本余额),NumberEnum.MONEY);

        //变量.分红净值 =	变量.待分配利润/变量.实收资本科目余额
        分红净值 = ArithmeticUtil.divide(profitAmt, 实收资本余额);


        //3.账务处理
        // 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
         // 会计场景设置
        ad.setTableId(DictCodeListTableId.会计场景代码_产品_净值型_分红除权处理.getValue());
        // 塞入记账场景树节点 = 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());

        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        //accountSceneMap设置如下：
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
        // 账务明细代码1-> 交易类账务处理结果.账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID1,valBokDetailId1);
        // 是否累计余额
        accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_是.getValue());
        // 交易编号 - > (计提类生成的账户明细，不需要塞交易编号)
        accountSceneMap.put(BookConstant.TRADE_ID, null);
        // 冲正标识 - （往账务明细（当前表））默认塞入正常
        accountSceneMap.put(BookConstant.OFFSET_FLAG, DictCodeList.冲正标识_正常交易.getValue());
        // 是否0入账
        accountSceneMap.put(BookConstant.IS_ZERO_ACCOUNT, null);
        //备注
        accountSceneMap.put(BookConstant.BOOK_REMARK,  "净值型产品利润分配");

        //accountAmountMap设置如下：
        //分红金额	<-	变量.待分配利润
        accountAmountMap.put(BookConstant.DIVIDENDAMT, profitAmt);

        //accountCurrencyMap设置如下：
        //币种	<-	利润分配账务处理结果.本位币
        accountCurrencyMap.put(BookConstant.DIVIDENDAMT, acctEntity.getStandardCcy());

        // map塞入场景要素管理中
        ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
        ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
        ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

             // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

        //4.账套资产负债信息更新
        //变量.当日帐套资产负债信息.当日分红金额 +=变量.待分配利润
        bokStatBokPosition.setTdyBounsAmt(ArithmeticUtil.doAdd(bokStatBokPosition.getTdyBounsAmt(),profitAmt));
        //变量.当日帐套资产负债信息.累计分红金额  +=变量.待分配利润
        bokStatBokPosition.setTotBounsAmt(ArithmeticUtil.doAdd(bokStatBokPosition.getTotBounsAmt(),profitAmt));
        //变量.当日帐套资产负债信息.当日分红净值  +=变量.分红净值
        bokStatBokPosition.setTdyBounsNet(ArithmeticUtil.doAdd(bokStatBokPosition.getTdyBounsNet(),分红净值));
        //变量.当日帐套资产负债信息.累计分红净值  +=变量.分红净值
        bokStatBokPosition.setTotBounsNet(ArithmeticUtil.doAdd(bokStatBokPosition.getTotBounsNet(),分红净值));

        result.setBookGenerateResult(returnResult);
        result.setBokStatBokPosition(bokStatBokPosition);
        return result;
    }
}
