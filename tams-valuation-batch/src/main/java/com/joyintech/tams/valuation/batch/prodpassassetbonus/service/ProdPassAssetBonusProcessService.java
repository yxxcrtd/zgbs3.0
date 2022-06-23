/*
 * 文件名：ProdPassAssetBonusProcessService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2019年3月15日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.prodpassassetbonus.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.common.base.entity.BokAcctLayeringInfo;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokStatBokPosition;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.ValBooksetTodoDto;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.interf.base.service.common.ChannelCommonService;

/**
 * 产品资产通道自动红利再投Process逻辑处理
 *
 * @author weipeng
 * @version 1.0 2019年3月15日
 * @see ProdPassAssetBonusProcessService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ProdPassAssetBonusProcessService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogic bookLogic;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 共通方法service
     */
    @Autowired
    private AccountingCommonService accountingCommonService;
    @Autowired
    private ChannelCommonService channelCommonService;
    @Autowired
    private FinProductCommonService finProductCommonService;
    /**
     * process 数据处理
     *
     * @param param 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param) {
        // 获取reader返回值
        ValBooksetTodoDto acctEntity = (ValBooksetTodoDto)param.get(GlobalConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }

        // 定义此process的返回变量
        BookGenerateResult returnResult = new BookGenerateResult();

        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }
        //查询子账套信息
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("booksetId", acctEntity.getBooksetId());
//        BokAcctLayeringInfo bokAcctLayeringInfo =new BokAcctLayeringInfo();
        List<BokAcctLayeringInfo> bokAcctLayeringInfoList= channelCommonService.queryBokBooksetReList(map);
        if(bokAcctLayeringInfoList == null || bokAcctLayeringInfoList.size() == 0){
            // 子账套信息为空时，直接返回
            return returnResult;
        }
        // 组装记账元素
        // 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = Maps.newHashMap();
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = Maps.newHashMap();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = Maps.newHashMap();
        List<ValBokBookdetailBatch> listAll = new ArrayList<ValBokBookdetailBatch>();
        for (BokAcctLayeringInfo bokAcctLayeringInfo : bokAcctLayeringInfoList) {
            /**
             * add by wujie on 20190716 start
             */
            // 初始化定义 - 存放该笔记账的场景要素 map定义
            accountSceneMap = Maps.newHashMap();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            accountAmountMap = Maps.newHashMap();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            accountCurrencyMap = Maps.newHashMap();
            /**
             * add by wujie on 20190716 end
             */

            map.clear();
            map.put("busiId", bokAcctLayeringInfo.getBooksetId());
            map.put("busiDetailType", DictCodeListExt1.金融产品类型_计划类资产.getValue());
            //获取账务明细代码
            BokDetailId bokDetailId=new BokDetailId();
            List<BokDetailId> bokDetailIdList=bookCommonLogic.queryBokDetailIdList(map);
            BigDecimal bonusIncome = BigDecimal.ZERO;
            TargetFinProduct targetFinProduct = null;
            if(bokDetailIdList.size()>0){
                bokDetailId=bokDetailIdList.get(0);
                //获取标的信息
                Map<String, Object> targetInfoMap=this.getTargetInfo(bokAcctLayeringInfo.getBooksetId(), DictCodeListExt1.金融产品类型_计划类资产.getValue(),null);
                targetFinProduct =(TargetFinProduct) targetInfoMap.get(ValDictCodeList.标的_信息.getValue());

                returnResult.setProdId(acctEntity.getBooksetId());
                // 1.数据准备
                // 1.1 应收红利科目最新余额查询
                SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
                // 账套代码 = 账务处理结果.账套代码
                searchDto.setBooksetId(acctEntity.getBooksetId());
                // 发生日 = 账务处理结果.发生日
                searchDto.setHappenDate(acctEntity.getHappenDate());
                // 入账日 = 账务处理结果.入账日
                searchDto.setBookDate(acctEntity.getBookDate());
                searchDto.setBaseSubjectNatureList(
                        new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.应收红利.getValue())));
                searchDto.setBokDetailId(bokDetailId.getBokDetailId());// 账务明细代码
                SubjectBalance subjectBalance = this.getSubjectBalance(searchDto,
                        DictCodeList.余额方向_借方.getValue());
                // 定义 变量.红利收入 = 返回值.原币余额（发生日）
                if (subjectBalance != null) {
                    bonusIncome = subjectBalance.getBalance();
                }
            }
            // 1.2 每个账套的本日收益查询
            BokStatBokPositionDto position = new BokStatBokPositionDto();
            // 查询日开始
            position.setSdateStart(acctEntity.getHappenDate());
            // 查询结束日
            position.setSdateEnd(acctEntity.getHappenDate());
            // 入账日
            position.setBookDate(acctEntity.getBookDate());
            // 账套
            position.setBooksetId(bokAcctLayeringInfo.getBooksetId());
            // 分层代码
            position.setLayeringId(bokAcctLayeringInfo.getLayeringId());
            // 调用共通方法查询：帐套资产负债查询
            List<BokStatBokPosition> listBokStatBokPosition = accountingCommonService.getValStatBokPosition(
                    position);
            // 定义 变量.红利再投金额
            BigDecimal bonusReinvest = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(listBokStatBokPosition)) {
                // 变量.红利再投金额 = 账套资产负债信息(返回LIST.get(0)).当日应付利润
                bonusReinvest = listBokStatBokPosition.get(0).getProfitAmt()==null? BigDecimal.ZERO:listBokStatBokPosition.get(0).getProfitAmt();
            }
            // 记账场景要素map塞值,(调用共通方法-计提类场景元素赋值)
            
            /**ADD BY WUJIE ON 20190902 START**/
            acctEntity.setFinprodId(bokAcctLayeringInfo.getBooksetId());
            acctEntity.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
            /**ADD BY WUJIE ON 20190902 END**/
            
            //场景账务明细代码
            acctEntity.setBokDetailId(bokDetailId.getBokDetailId());
            accountSceneMap = bookCommonLogic.accrualElementAssignForPass(acctEntity);
            accountSceneMap.put(BookConstant.BOOK_REMARK, "公允价值损益类货币型资产-红利再投");
            accountSceneMap.put(BookConstant.TASK_PRI, param.get(BookConstant.TASK_PRI));
            if(targetFinProduct!=null){
                accountSceneMap.put(DictCode.境内外.getValue(), targetFinProduct.getBlnArea());
            }else{
                accountSceneMap.put(DictCode.境内外.getValue(), DictCodeList.境内外_境内.getValue());
            }

            // 会计场景设置
            ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_红利再投.getValue()); // ad.会计场景代码="公允价值损益类货币型资产_红利再投"
            // 塞入记账场景树节点 = 会计场景
            ad.setTreeNode(acctEntity.getBookSetTreeNode());
            //发生数量
            accountSceneMap.put(BookConstant.HAPPEN_NUMBER,
                    ArithmeticUtil.round(bonusReinvest, NumberEnum.MONEY));

            // 计提金额 <- 变量.红利收入 调用金额共通方法设置有效小数位
            accountAmountMap.put(BookConstant.INTAMT,
                    ArithmeticUtil.round(bonusIncome, NumberEnum.MONEY));
            // 红利再投金额 <- 变量.红利再投金额 调用金额共通方法设置有效小数位
            accountAmountMap.put(BookConstant.TRADE_QTY,
                    ArithmeticUtil.round(bonusReinvest, NumberEnum.MONEY));

            // if 变量.红利再投金额 - 变量.红利收入 >0
            if (bonusReinvest.compareTo(bonusIncome) > 0) {
                // 轧差金额（贷） = （变量.红利再投金额 - 变量.红利收入）
                accountAmountMap.put(BookConstant.NETAMT_C, ArithmeticUtil.subtract(bonusReinvest,
                        bonusIncome, NumberEnum.MONEY.getCode(), null));
            }
            else if(bonusReinvest.compareTo(bonusIncome) < 0){
                // 轧差金额（借） = （变量.红利再投金额 - 变量.红利收入）*-1
                accountAmountMap.put(BookConstant.NETAMT_D, ArithmeticUtil.subtract(bonusIncome,
                        bonusReinvest, NumberEnum.MONEY.getCode(), null));
            }

            // 记账金额元素币种信息塞值--key是随记账金额map的
            for (String key : accountAmountMap.keySet()) {
                accountCurrencyMap.put(key, acctEntity.getStandardCcy());
            }

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
            // 获取待处理任务信息
            SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");
            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            listAll.addAll(resultNowNow.getValBokBookdetailBatch());

        }

        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(listAll);

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
     * @param taskPri 任务优先级
     * @return subjectBalance 科目余额信息
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(SubjectBalanceSearchDto searchDto,
                                             String expBalanceDirect) {
        // 定义返回实体类
        SubjectBalance subjectBalance = null;
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDto, expBalanceDirect);
        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }

        return subjectBalance;
    }

    /**
     * 获取标的相关信息
     *
     * @param acctEntity reader 返回值
     * @param happenDate 发生日：批处理任务执行信息.处理日（发生日）
     * @return Map<String, Object>
     */
    private Map<String, Object> getTargetInfo(String finprodId,String finprodType,Long branch) {
        Map<String, Object> returnMap = Maps.newHashMap(); // 返回值

        List<TargetSearchDto> queryCriteria = Lists.newArrayList(); // 共同查询类型

        // 传入[标的_信息]Dto
        TargetSearchDto targetSearchDto5 = new TargetSearchDto();
        targetSearchDto5.setFinprodId(finprodId);
        targetSearchDto5.setFinprodType(finprodType);
        targetSearchDto5.setQueryType(ValDictCodeList.标的_信息.getValue());
        targetSearchDto5.setBranch(branch);
        queryCriteria.add(targetSearchDto5);

        // 获取标的信息Map，共通 标的信息查询-现金流计算规则
        Map<TargetSearchDto, Object> map = finProductCommonService.getTargetInfo(finprodId, queryCriteria);

        if (MapUtils.isNotEmpty(map)) {
            returnMap.put(ValDictCodeList.标的_信息.getValue(), map.get(targetSearchDto5)); // 获取标的信息
        }

        return returnMap;
    }
}
