package com.joyintech.tams.invest.common.trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.common.trade.dto.CreditComInfoOneStepDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesOutputDto;


public abstract class CreditLinesUpdateAbs implements CreditLinesUpdate, BeanValidator {

    /**
     * 日志
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected boolean isSave = false;

    protected boolean occupy = true;
    /**
     * 授信软控 or 硬控
     */
    String CREDIT_SOFT_SKIP_MODE;
    /**
     * 释放模式：先进先出、实时金额
     */
    String RELEASE_MODE;
    /**
     * 记录资产授信的占用金额或者份额
     */
    BigDecimal compareAmtAst;
    /**
     * 记录机构授信的占用金额
     */
    BigDecimal compareAmtOrg;
    /**
     * 记录资产授信占用金额或者份额、场内部撤时比对用
     */
    BigDecimal releaseAmtAst;
    /**
     * 记录机构授信占用金额或者份额、场内部撤时比对用
     */
    BigDecimal releaseAmtOrg;
    /**
     * 处理结果集合
     */
    List<CreditLinesOutputDto> creditLinesOutputDtoList;
    /**
     * 资产授信
     */
    CreditLines creditLinesAst;
    /**
     * 机构授信
     */
    CreditLines creditLinesOrg;
    /**
     * 机构分项授信
     */
    CreditLines creditLinesOrgItem;

    /**
     * 额度校验规则
     */
    CreditComInfoOneStepDto oneStepInfo;
    /**
     * 授信额度仓库
     */
    Map<String, CreditLines> creditLinesMap;
    /**
     * 资产授信占用明细
     */
    List<CreditUsingAst> creditUsingAstList;
    /**
     * 资产系列占用明细
     */
    List<CreditUsingSeries> creditUsingSeriesList;
    /**
     * 机构额度占用明细
     */
    List<CreditUsingCustomer> creditUsingCustomerList;
    /**
     * 机构分项额度占用明细
     */
    List<CreditUsingCustomerMst> creditUsingCustomerMstList;

    protected abstract void occupyCreditLines(List<CreditLinesInputDto> inputDtoList,String occupyType,Date tradeDate);

    protected abstract void batchInsertCredit();

    protected abstract void buildRtnResult();

    List<CreditLines> getCreditLinesList(){
        return new ArrayList<>(this.creditLinesMap.values());
    }

    /**
     * 批量预占接口
     * @param inputDtoList
     */
    public List<CreditLinesOutputDto> occupyCreditLinesCheck(List<CreditLinesInputDto> inputDtoList){
        logger.info("==========批量Check接口调用 start==========");
        this.isSave = false;
        occupyCreditLines(inputDtoList, DictCodeListRsk.授信占用类别_预占.getValue(),null);
        buildRtnResult();
        logger.info("==========批量Check接口调用 end==========");
        return creditLinesOutputDtoList;
    }

    /**
     * 批量预占接口
     * @param inputDtoList
     */
    public void occupyCreditLinesEtr(List<CreditLinesInputDto> inputDtoList, Date tradeDate){
        logger.info("==========批量预占接口调用 start==========");
        this.isSave = true;
        occupyCreditLines(inputDtoList, DictCodeListRsk.授信占用类别_预占.getValue(),tradeDate);
        batchInsertCredit();
        logger.info("==========批量预占接口调用 end==========");
    }

    /**
     * 批量实占接口
     * @param inputDtoList
     */
    public void occupyCreditLinesTrd(List<CreditLinesInputDto> inputDtoList, Date tradeDate){
        logger.info("==========批量实占接口调用 start==========");
        this.isSave = true;
        occupyCreditLines(inputDtoList, DictCodeListRsk.授信占用类别_实占.getValue(),tradeDate);
        batchInsertCredit();
        logger.info("==========批量实占接口调用 end==========");
    }

    /**
     * 从授信额度仓库中取数据，如果不存在，则放进去
     * @param creditLines
     * @return
     */
    CreditLines getCreditLines(CreditLines creditLines) {
        if (creditLines == null){
            return null;
        }
        if (creditLinesMap.get(creditLines.getSeqno()) != null){
            return creditLinesMap.get(creditLines.getSeqno());
        }else {
            creditLinesMap.put(creditLines.getSeqno(),creditLines);
            return creditLines;
        }
    }
}
