/*
 * 文件名：YelidPrdProfitDrawProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：dongjun
 * 修改时间：2018年9月29日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.batch.expreturnprofitdrawtask.service;

import com.google.common.collect.Lists;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokBookdetailCurrent;
import com.joyintech.tams.common.base.entity.BokStatBokPosition;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.calculation.entity.CalCommonResult;
import com.joyintech.tams.valuation.batch.common.calculation.entity.PrincipalInfo;
import com.joyintech.tams.valuation.batch.common.calculation.entity.QuoteInfo;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashCalcRule;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 预期收益型子产品TA客户收益计提-账务明细处理ProcessorService
 * 
 * @author dongjun
 * @version 1.0 2018年9月29日
 * @see ExpReturnProfitDrawProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ExpReturnProfitDrawProcessorNewService {

    @Autowired
    private FinProductCommonService finProductCommonService;
    
    @Autowired
    private AccountingCommonService accountingCommonService;
    
    @Autowired
    private CalculationCommonService calculationCommonService;
    
    @Autowired
    private BookCommonLogic bookCommonLogic;
    
    @Autowired
    private BookAccountGenLogicNew bookAccountGenLogicNew;
    
    @SuppressWarnings("unchecked")
    public BookGenerateResult execute(Map<String, Object> params){
        
        BookGenerateResult result = new BookGenerateResult();
        
        StayDrawSubprdEntity stayDrawSubprdEntity = (StayDrawSubprdEntity)params.get("READER_RESULT");
        // 若reader传递的参数为 null，不进行后续处理
        if (stayDrawSubprdEntity == null) {
            return null;
        }
        //发生日
        Date cdate = (Date)params.get("cdate");
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)params.get("batchInfo");// 获取待处理任务信息
        //赋值金融产品代码finprodId
        params.put("finprodId", stayDrawSubprdEntity.getBusiId());
        //赋值账套代码booksetId
        params.put("booksetId", stayDrawSubprdEntity.getBooksetId());
        if(StringUtils.isEmpty(stayDrawSubprdEntity.getBusiDetailType())){
                return null;
        }
        //赋值业务明细类型
        params.put("busiDetailType", stayDrawSubprdEntity.getBusiDetailType());
        //分支序号
        params.put("branch", stayDrawSubprdEntity.getsBranch());
        //查询子产品待计提现金流查询_理财产品
        List<StayDrawSubprdEntity> acctEntityList=Lists.newArrayList();
        if(DictCodeListExt1.金融产品类型_理财产品.getValue().equals(stayDrawSubprdEntity.getBusiDetailType())){
            acctEntityList=accountingCommonService.getStayDrawSubprdInfoByPrd(params);
        }else if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(stayDrawSubprdEntity.getBusiDetailType())){
            acctEntityList=accountingCommonService.getStayDrawSubprdInfoByPrd(params);
        }
        //获取账务明细代码
        List<String> bokDetailIdList=Lists.newArrayList();
        for(StayDrawSubprdEntity entity:acctEntityList){
            bokDetailIdList.add(entity.getBokDetailId());
        }
        Date happenDate = (Date)params.get(BookConstant.HAPPEN_DATE); // 发生日：批处理任务执行信息.处理日（发生日）
        
        for(StayDrawSubprdEntity acctEntity_new:acctEntityList){
          //调用共通方法：账套参数查询（返回map<参数项，参数值>）
            Map<String, Object> item=accountingCommonService.getValBokBooksetExtForMap(stayDrawSubprdEntity.getBooksetId(),null);
            String isPrdIntAccr=(String) item.get(ValDictCodeList.账套配置表参数项_产品是否本系统计提.getValue());
            String prdIntAccType=(String) item.get(ValDictCodeList.账套配置表参数项_产品利息计提方式.getValue());

            if (DictCodeList.是否_否.getValue().equals(isPrdIntAccr)) {
                return result;
            }
            
            
            //调用共通方法：标的信息查询-现金流计算规则
            String finProdId = acctEntity_new.getFinprodId();
            List<TargetSearchDto> queryCriteria = new ArrayList<>();
            TargetSearchDto targetSearchDto = new TargetSearchDto();
            targetSearchDto.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
            targetSearchDto.setCashType1(acctEntity_new.getCashType1());
            targetSearchDto.setCashType2(acctEntity_new.getCashType2());
            targetSearchDto.setCashId(acctEntity_new.getCashId());
            targetSearchDto.setEffdateStart(acctEntity_new.getVdate());
            targetSearchDto.setEffdateEnd(cdate);
            targetSearchDto.setBranch(acctEntity_new.getBranch());
            queryCriteria.add(targetSearchDto);
            Map<TargetSearchDto, Object> targetInfoMap = finProductCommonService.getTargetInfo(finProdId, queryCriteria);
            //定义变量：本次计提开始日
            Date sdateStart = null;
            if(DictCodeList.计提方式_区间倒减.getValue().equals(prdIntAccType)){
                //变量.计提开始日 = 上述返回LIST.get（0）.结息日+1
                //最新结息日查询
                List<BokBookdetailCurrent> listMdate = accountingCommonService.getNewestMdate(stayDrawSubprdEntity.getBooksetId(),
                    stayDrawSubprdEntity.getBookDate(), stayDrawSubprdEntity.getBokDetailId(),
                        new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.应付利润.getValue())),
                        new ArrayList<String>(Arrays.asList(DictCodeListTableId.会计场景代码_产品_预期收益型_兑付客户收益.getValue())),
                        null, happenDate, null, null, happenDate);
                if(StringUtil.isEmptyList(listMdate)){
                    // 本次计提开始日 = DTO.起息日
                    sdateStart = acctEntity_new.getVdate(); 
                }else{
                    BokBookdetailCurrent current=listMdate.get(0);
                    if(StringUtil.isNotEmpty(current.getMdate())){
                        sdateStart=current.getMdate();
                    }else{
                        // 本次计提开始日 = DTO.起息日
                        sdateStart = acctEntity_new.getVdate(); 
                    }
                }
            }else{
                sdateStart=cdate;
            }
            
            //1.利率变更列表处理
            //利率变更列表
            List<QuoteInfo> quoList = new ArrayList<>();
            //生效利率
            BigDecimal yield = BigDecimal.ZERO;
            List<TargetCashCalcRule> targetCashCalcRuleList = (List<TargetCashCalcRule>)targetInfoMap.get(targetSearchDto);
            for(TargetCashCalcRule rule : targetCashCalcRuleList){
                if(!ArithmeticUtil.equals(yield, rule.getYield())){
                    QuoteInfo quoDto = new QuoteInfo();
                    quoDto.setIssueDate(rule.getEffDate());
                    quoDto.setInterest(rule.getYield());
                    //20181009评审  增加计息基础
                    quoDto.setAccountBasis(rule.getBasis());
                    quoList.add(quoDto); 
                    yield = rule.getYield();
                }
                
            }
            
            //2.计费基础变更列表处理
            //计费基数日期类型_当日
            //String feebaseDateType = DictCodeList.基数日期类型_当日.getValue();
            //计费基数类型_实收资本
            String feebaseType = DictCodeList.基数类型_实收资本.getValue();
            //本次计提开始日
            //Date sdateStart = stayDrawSubprdEntity.getVdate();
            //账套代码
            String booksetId = stayDrawSubprdEntity.getBooksetId();
            //入账日期
            Date bookDate = stayDrawSubprdEntity.getBookDate();
            //本位币
            String standardCcy = stayDrawSubprdEntity.getStandardCcy();
            //任务优先级
            Long taskPri = stayDrawSubprdEntity.getTaskPri();
            //共通：账套资产负债查询
            BokStatBokPositionDto position=new BokStatBokPositionDto();
            //查询日开始
            position.setSdateStart(sdateStart);
            //查询结束日
            position.setSdateEnd(cdate);
            //入账日
            position.setBookDate(cdate);
            //账套
            position.setBooksetId(booksetId);
            //分层代码
            position.setLayeringId(acctEntity_new.getLayeringId());
            //分层代码
            position.setIsSubPrd(DictCodeList.是否_是.getValue());
//            List<BokStatBokPosition> bokList = accountingCommonService.getValStatBokPosition(sdateStart, cdate, cdate, booksetId, null, acctEntity_new.getFinprodId());
            List<BokStatBokPosition> bokList = accountingCommonService.getValStatBokPosition(position);
            Map<Date,BokStatBokPosition>  bokPositionMap = new HashMap<>();
            if(bokList != null && !bokList.isEmpty()){
                for(BokStatBokPosition bok : bokList){
                    bokPositionMap.put(bok.getHappenDate(), bok);
                }
            }
            //共通：产品费用计提基数计算
            //定义变量：计费基础变更列表
            List<PrincipalInfo> basisDtoList = new ArrayList<>();
            //定义变量：上次计费基数
            BigDecimal lastFeeBasis = BigDecimal.ZERO;
            Date calcDate = sdateStart;
            while(DateUtil.compareDate(calcDate, cdate)<=0){
                PrincipalInfo baseDto = new PrincipalInfo();
                BigDecimal calcBasis = calculationCommonService.calProductFee(feebaseType, bokPositionMap.get(calcDate), booksetId, bookDate, standardCcy, cdate, calcDate, taskPri,bokDetailIdList);
                if(!ArithmeticUtil.equals(calcBasis, lastFeeBasis)){
                    baseDto.setChangeDate(calcDate);
                    baseDto.setPrinAmount(calcBasis);
                    lastFeeBasis = calcBasis;
                    basisDtoList.add(baseDto);
                }
                calcDate = DateUtil.addDay(calcDate, 1);
            }
            
            //共通：通用计算
            CalCommonResult calCommonResult = calculationCommonService.calCommon(sdateStart, DateUtil.addDay(cdate,1), basisDtoList, quoList);
          //客户收益计提金额
            BigDecimal tdyIncexpAdd = calCommonResult == null ? BigDecimal.ZERO : calCommonResult.getTotalInterestAmt();
            //共通： 科目余额查询
            if(DictCodeList.计提方式_区间倒减.getValue().equals(prdIntAccType)){
                SubjectBalanceSearchDto balanceDto = new SubjectBalanceSearchDto();
                balanceDto.setHappenDate(cdate);
                balanceDto.setBookDate(bookDate);
                balanceDto.setBokDetailId(acctEntity_new.getBokDetailId());
                balanceDto.setBooksetId(booksetId);
                List<String> baseSubjectNatureList = new ArrayList<>();
                // 设置基础科目性质
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.应付利润.getValue());
                balanceDto.setBaseSubjectNatureList(baseSubjectNatureList);
                List<SubjectBalance> balanceList = accountingCommonService.getSubjectBalanceExpectDirection(balanceDto, DictCodeList.余额方向_贷方.getValue());
                BigDecimal balance = BigDecimal.ZERO;
                if(balanceList != null && !balanceList.isEmpty()){
                    for(SubjectBalance bal : balanceList){
                        balance = ArithmeticUtil.doAdd(balance,bal.getBalance());
                    }
                }
                //客户收益计提金额
                //20181009增加精度处理
                tdyIncexpAdd = ArithmeticUtil.subtract(tdyIncexpAdd, balance, NumberEnum.MONEY.getCode(), RoundingMode.HALF_UP);
            }
            // 组装记账元素
            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();
            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            // 共通元素设值 - 设置账务引擎参数
            ad.setTreeNode(stayDrawSubprdEntity.getBookSetTreeNode());// 塞入记账场景树节点 = 会计场景

            // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
            accountSceneMap = bookCommonLogic.accrualElementAssignForTaFee(batchExecuteInfo.getTaskId(), cdate,
                stayDrawSubprdEntity);

            // 根据现金流二级类型，设置其他记账凭证要素
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_预期收益型_客户收益计提.getValue());// ad.会计场景代码;
                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK, "预期收益型TA客户收益计提登账");// 凭证说明       

            // 记账金额元素map塞值
            accountAmountMap.put(BookConstant.INTAMT, tdyIncexpAdd);// 客户收益

            // 记账金额元素币种信息塞值
            // 因为与记账金额元素map的key及数量一致，故采用循环
            for (String keyNow : accountAmountMap.keySet()) {
                accountCurrencyMap.put(keyNow, standardCcy);// value值塞入当前交易币种
            }

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

            // 记账凭证生成
            BookGenerateResult resultNowNow = bookAccountGenLogicNew.genAccount(ad, batchExecuteInfo);
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            result.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        }
        
        return result;
    }
}
