/*
 * 文件名：IrsCalBookNewService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5
 * 修改人：殷亚
 * 修改时间：2022年4月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.batch.irscalbook.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.MstValIrs;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.calculation.entity.CalCommonResult;
import com.joyintech.tams.valuation.batch.common.calculation.entity.PrincipalInfo;
import com.joyintech.tams.valuation.batch.common.calculation.entity.QuoteInfo;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.investratechange.dto.InvestRateChangeResult;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.dto.DfDealDto;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 利率互换计提账务处理（新） - processor
 *
 * @author 殷亚
 * @version 1.0
 * @date 2022/4/25 15:32
 * @see
 */
@Service
@Scope("prototype")
public class IrsCalBookNewService {

    /**
     * 公用方法services
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 标的共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 计算类共通
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;

    /**
     * process 数据处理
     *
     * @param param1 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public InvestRateChangeResult execute(Map<String, Object> param1) {
        // 获取reader返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult) param1.get(
                GlobalConstant.READER_RESULT);

        List<IrsCalValDto> resultList=new ArrayList<IrsCalValDto>();
        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        String bokDetailIdNow = acctEntity.getBokDetailId();// 账务明细代码
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期
        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTONow = acctEntity.getListDrawBaseInfoSelectComDTO(); // 计提基础信息列表
        Long taskPri = listDrawBaseInfoSelectComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）
        String standardCcy = listDrawBaseInfoSelectComDTONow.get(0).getStandardCcy(); // 本位币币种

        // 1. 数据准备
        // 1.1 定义变量
        BigDecimal costBalance = BigDecimal.ZERO;// 变量.成本科目余额
        BigDecimal interest = BigDecimal.ZERO;// 变量.应收利息
        BigDecimal costNum = BigDecimal.ZERO;// 变量.数量
        BigDecimal interestBlanceForYstVar = BigDecimal.ZERO;// 变量.应收利息前一日余额
        // 账套核算参数查询
        List<String> paramCodeList = new ArrayList<String>();
        // IRS_ACCR_TYPE 账套配置表参数项_利率互换计提方式
        paramCodeList.add(ValDictCodeList.账套配置表参数项_利率互换计提方式.getValue());
        // 调用账务类共通（DTO.账套代码, 参数项：如下）
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService
                .getValBokBooksetExtForMap(acctEntity.getBooksetId(), paramCodeList);
        // 变量.计提方式 = 账套配置表参数项_利率互换计提方式 对应的计提方式
        String calType = "";
        String calTypetab = "";
        if (getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_利率互换计提方式.getValue()) != null) {
            calType = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_利率互换计提方式.getValue());
            calTypetab = calType;
        }
        // 3. 处理交易
        // 3.1、循环处理计提明细
        // 循环 计提基础信息列表 进行逻辑处理
        for (DrawBaseInfoSelectComDTO dto : listDrawBaseInfoSelectComDTONow) {
            // 获取标的信息.金融产品类型
            TargetFinProduct targetFinProduct = this.getTargetFinProduct(dto.getBusiId());
            Date mdateYsdWithTarget = this.getYesterday(targetFinProduct.getMdate());
            // 调用共通设计-> 标的类共通：标的信息查询（现金流计算规则）
            TargetSearchDto targetSearchDto = new TargetSearchDto();
            targetSearchDto.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
            if (DictCodeBaseSubjectNatrueNew.应收利率互换.getValue().equals(dto.getBaseSubjectNature())) {
                targetSearchDto.setCashType2(DictCodeListExt1.现金流二级类型_收取.getValue());
            } else if (DictCodeBaseSubjectNatrueNew.应付利率互换.getValue().equals(dto.getBaseSubjectNature())) {
                targetSearchDto.setCashType2(DictCodeListExt1.现金流二级类型_支付.getValue());
            }
            List<TargetSearchDto> queryCriteria = new ArrayList<>();
            queryCriteria.add(targetSearchDto);
            // 获取 现金流计算规则列表（报价列表）
            Map<TargetSearchDto, Object> targetInfoMap = finProductCommonService.getTargetInfo(
                    dto.getBusiId(), queryCriteria);
            List<TargetCashCalcRule> targetCashCalcRuleList = new ArrayList<TargetCashCalcRule>();
            if (targetInfoMap != null && targetInfoMap.size() > 0
                    && targetInfoMap.get(targetSearchDto) != null) {
                targetCashCalcRuleList = (List<TargetCashCalcRule>) targetInfoMap.get(
                        targetSearchDto);
            }
            // 3.1 利息计提
            // if (标的信息.起息日 > 计提类账务处理结果.发生日 )
            if (DateUtil.compareDate(targetFinProduct.getVdate(), happenDateNow) > 0) {
                return null;
            }
            if (DateUtil.compareDate(happenDateNow, mdateYsdWithTarget) <= 0) {


                if (DictCodeBaseSubjectNatrueNew.应收利率互换.getValue().equals(dto.getBaseSubjectNature())) {
                    SubjectBalance subjectBalance = this.getSubjectBalanceEx(booksetIdNow, bokDetailIdNow,
                            happenDateNow, bookDateNow, DictCodeList.余额方向_借方.getValue(),
                            DictCodeBaseSubjectNatrueNew.应收利率互换.getValue(), taskPri);
                    costBalance = subjectBalance.getBalance();
                    // 变量.当日成本科目数量
                    costNum = subjectBalance.getNum();
                    interestBlanceForYstVar = this.getSubjectBalance(dto.getBooksetId(),
                            dto.getBokDetailId(), happenDateNow, bookDateNow,
                            DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.利率互换应收利息.getValue(),
                            taskPri);
                } else if (DictCodeBaseSubjectNatrueNew.应付利率互换.getValue().equals(dto.getBaseSubjectNature())) {
                    SubjectBalance subjectBalance = this.getSubjectBalanceEx(booksetIdNow, bokDetailIdNow,
                            happenDateNow, bookDateNow, DictCodeList.余额方向_借方.getValue(),
                            DictCodeBaseSubjectNatrueNew.应付利率互换.getValue(), taskPri);
                    costBalance = subjectBalance.getBalance();
                    // 变量.当日成本科目数量
                    costNum = subjectBalance.getNum();
                    interestBlanceForYstVar = this.getSubjectBalance(dto.getBooksetId(),
                            dto.getBokDetailId(), happenDateNow, bookDateNow,
                            DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.利率互换应付利息.getValue(),
                            taskPri);
                }
                if (DictCodeList.计提方式_区间倒减.getValue().equals(calType)) {
                    // 1、计算上次付息日到发生日的应收利息
                    // 调用共通设计->【标的类共通方法】->当前付息区间查询
                    List<TargetCashPlan> listCashPlan = finProductCommonService.getCurrentIntRange(
                            dto.getBusiId(), happenDateNow);
                    TargetCashPlan targetCashPlan = listCashPlan.get(0);
                    // 变量.计息开始日 = 返回值.计息开始日
                    Date vdate = targetCashPlan.getVdate();
                    // 定义 变量.本金列表
                    List<PrincipalInfo> prinList = new ArrayList<>();
                    // 本金列表赋值
                    PrincipalInfo principalInfo = new PrincipalInfo();
                    principalInfo.setChangeDate(vdate);
                    principalInfo.setPrinAmount(costBalance);
                    prinList.add(principalInfo);
                    // 定义 变量.报价列表
                    List<QuoteInfo> quoteList = new ArrayList<>();
                    // 报价列表赋值
                    targetCashCalcRuleList.forEach(item -> {
                        QuoteInfo quoteInfo = new QuoteInfo();
                        quoteInfo.setAccountBasis(item.getBasis());
                        quoteInfo.setInterest(item.getYield());
                        quoteInfo.setIssueDate(item.getEffDate());
                        ;
                        quoteList.add(quoteInfo);
                    });
                    CalCommonResult calCommonResult = calculationCommonService.calCommon(vdate,
                            DateUtil.addDay(happenDateNow, 1), prinList, quoteList);
                    // 变量.应收利息 = 返回值.总利息金额
                    interest = ArithmeticUtil.round(calCommonResult.getTotalInterestAmt(),
                            NumberEnum.MONEY);
                    interest = ArithmeticUtil.doSub(interest, interestBlanceForYstVar);
                } else {
                    //每日新增暂不实现

                }

                if (DictCodeBaseSubjectNatrueNew.应付利率互换.getValue().equals(dto.getBaseSubjectNature())) {
                    interest = interest.negate();
                }
            } else {
                interest = BigDecimal.ZERO;
            }
            IrsCalValDto valDto=new IrsCalValDto();
            valDto.setFinProdId(targetFinProduct.getFinprodId());
            valDto.setCcy(targetFinProduct.getCcy());
            valDto.setIntrest(interest);
            resultList.add(valDto);

        }

        //根据金融产品和币种，对金额合并
        List<IrsCalValDto> newList=combineList(resultList);
        List<MstValIrs> mstValIrsList = getMstValIrsList(newList, acctEntity.getHappenDate());
        InvestRateChangeResult investRateChangeResult = new InvestRateChangeResult();
        investRateChangeResult.setMstValIrsList(mstValIrsList);
        return investRateChangeResult;

    }

    private List<MstValIrs> getMstValIrsList(List<IrsCalValDto> list, Date happenDate){
        List<MstValIrs> mstValIrsList = new ArrayList<>();
        for(IrsCalValDto dfDealDto : list) {
            MstValIrs mstValIrs = new MstValIrs();
            mstValIrs.setFinprodId2(dfDealDto.finProdId);
            mstValIrs.setDatasourceType(DictCodeListRsk.估值来源_行内.getValue());//数据来源行内
            mstValIrs.setValDate(happenDate);	//估值日期
            mstValIrs.setValuation(dfDealDto.getIntrest());
            mstValIrs.setValmodelId(DictCodeListExt4.利率互换估值模型_收付轧差.getValue());		//估值模型-收付轧差
            mstValIrs.setCcy(dfDealDto.getCcy());		//币种
            mstValIrs.setInputType(DictCodeListExt1.录入方式_批处理生成.getValue());
            mstValIrsList.add(mstValIrs);
        }
        return mstValIrsList;
    }

    private List<IrsCalValDto> combineList(List<IrsCalValDto> oList){
        IrsCalValDto tmp = null;
        Map<String, IrsCalValDto> map = new HashMap<String, IrsCalValDto>();//合并后的User存在map中
        for(IrsCalValDto oDto:oList) {
            tmp = map.get(oDto.getFinProdId());//获取某key对应value
            if (tmp != null) {
                tmp.setIntrest(tmp.getIntrest().add(oDto.getIntrest()));  //Intrest合并
            } else {
                map.put(oDto.getFinProdId(), oDto);
            }
        }

        List<IrsCalValDto> nlist = new ArrayList<IrsCalValDto>();
        for (String in : map.keySet()) {//遍历map
            //map.keySet()返回的是所有key的值
            IrsCalValDto u = map.get(in);//得到每个key对应value的值
            nlist.add(u);
        }
        return nlist;

    }


    /**
     * 内部组装实体类
     * @author 殷亚
     * @date 2022/4/27 9:30
     * @see
     * @version 1.0
     */
    class IrsCalValDto{
        private String finProdId;
        private String ccy;
        private BigDecimal intrest;

        public String getFinProdId() {
            return finProdId;
        }

        public void setFinProdId(String finProdId) {
            this.finProdId = finProdId;
        }

        public String getCcy() {
            return ccy;
        }

        public void setCcy(String ccy) {
            this.ccy = ccy;
        }

        public BigDecimal getIntrest() {
            return intrest;
        }

        public void setIntrest(BigDecimal intrest) {
            this.intrest = intrest;
        }
    }

    /**
     * 调用共通方法：[标的类共通方法-查询交易信息]，获取标的交易信息
     *
     * @param finprodId   金融产品代码【必输项】
     * @param finprodType 金融产品类型【必输项】
     * @param branch      分支序号
     * @return ValFinProductDeal 标的查询
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unused")
    private TradeInfoDto getTargetInfo(String finprodId, String finprodType, Long branch) {
        // 定义返回值 - 标的交易信息
        TradeInfoDto tradeInfoDtoForReturn = null;

        // 调用共通方法：[标的类共通方法 - 交易信息查询]，获取标的交易信息
        List<TradeInfoDto> tradeInfoDtoList = finProductCommonService.getTradeInfoDtoList(
                finprodId, finprodType, branch);

        // 当查询结果不为空
        if (tradeInfoDtoList != null && tradeInfoDtoList.size() > 0) {
            // 默认取第一条标的信息
            tradeInfoDtoForReturn = tradeInfoDtoList.get(0);
        }

        return tradeInfoDtoForReturn;
    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     *
     * @param booksetId         账套代码
     * @param bokDetailId       账务明细代码
     * @param happenDate        发生日
     * @param bookDate          入账日
     * @param expBalanceDirect  期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param taskPri           任务优先级
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private BigDecimal getSubjectBalance(String booksetId, String bokDetailId, Date happenDate,
                                         Date bookDate, String expBalanceDirect,
                                         String baseSubjectNature, Long taskPri) {
        // 定义返回的科目余额
        BigDecimal balance = BigDecimal.ZERO;

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            balance = subjectBalanceList.get(0).getBalance();
        }

        return ArithmeticUtil.round(balance, NumberEnum.MONEY);
    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     *
     * @param booksetId         账套代码
     * @param bokDetailId       账务明细代码
     * @param happenDate        发生日
     * @param bookDate          入账日
     * @param expBalanceDirect  期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param taskPri           任务优先级
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalanceEx(String booksetId, String bokDetailId,
                                               Date happenDate, Date bookDate,
                                               String expBalanceDirect, String baseSubjectNature,
                                               Long taskPri) {
        // 定义返回的科目余额
        SubjectBalance balance = new SubjectBalance();

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            balance = subjectBalanceList.get(0);
        } else {
            balance.setBalance(BigDecimal.ZERO);
            balance.setNum(BigDecimal.ZERO);
        }

        return balance;
    }

    /**
     * 获取前一天时间
     *
     * @param nowDate 传递时间
     * @return Date
     * @see
     * @since 1.0
     */
    private Date getYesterday(Date nowDate) {
        // 非空判断
        if (nowDate == null) {
            return null;
        }

        // 获取科目余额数据
        Calendar calendar = Calendar.getInstance();// 获取日历实例
        calendar.setTime(nowDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1); // 设置为前一天
        Date yesterday = calendar.getTime();// 获得前一天

        return yesterday;
    }

    /**
     * 获取标的基本信息
     *
     * @param bokDetailId 账务明细代码
     * @return TargetFinProduct 标的基本信息
     * @see
     * @since 1.0
     */
    private TargetFinProduct getTargetFinProduct(String bokDetailId) {
        // 定义查询list
        List<TargetSearchDto> targetSearchDtoList = new ArrayList<TargetSearchDto>();
        // 定义查询类型 实体类
        TargetSearchDto targetSearchDto = new TargetSearchDto();
        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
        // 查询实体类塞入查询实体类List中
        targetSearchDtoList.add(targetSearchDto);

        // 获取标的信息
        Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(bokDetailId,
                targetSearchDtoList);
        TargetFinProduct targetFinProduct = (TargetFinProduct) targetMap.get(targetSearchDto);

        // 确保不为null
        if (targetFinProduct == null) {
            targetFinProduct = new TargetFinProduct();
        }

        return targetFinProduct;
    }

}
