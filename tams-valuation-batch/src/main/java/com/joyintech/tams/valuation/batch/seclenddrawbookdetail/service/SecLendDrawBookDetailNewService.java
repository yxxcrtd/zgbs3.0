/*
 * 文件名：BondDebitCreditCalBookService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年10月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.batch.seclenddrawbookdetail.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.constant.DictCode;
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
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TradeInfoDto;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 债券借贷计提账务处理 process相关业务处理
 * 
 * @author zhangwei
 * @version 1.0 2018年10月20日
 * @see BondDebitCreditCalBookService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecLendDrawBookDetailNewService {

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
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * process 数据处理
     * 
     * @param param
     * @return
     */
    public BookGenerateResultWithBokSummary execute(Map<String, Object> param) {

        // 获取reader返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult)param.get(
            GlobalConstant.READER_RESULT);

        // 定义返回值
        BookGenerateResultWithBokSummary returnResult = new BookGenerateResultWithBokSummary();

        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");
        // 获取从reader中拿到的值
        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTONow = acctEntity.getListDrawBaseInfoSelectComDTO(); // 计提基础信息列表
        Long taskPri = listDrawBaseInfoSelectComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）

        // 1. 数据准备
        // 1.1 定义变量
        BigDecimal costBalance = BigDecimal.ZERO;// 变量.成本科目余额
        BigDecimal interest = BigDecimal.ZERO;// 变量.应计利息
        BigDecimal interestBlance = BigDecimal.ZERO;// 变量.应计利息余额

        // 1.2 汇总信息查询
        // 变量.昨日统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
//        BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
//            DateUtil.addDay(acctEntity.getHappenDate(), -1),
//            DateUtil.addDay(acctEntity.getHappenDate(), -1), acctEntity.getBookDate(),
//            acctEntity.getBooksetId(), acctEntity.getBokDetailId(), null, null);

        // 2. 调用共通查询

        // 账套核算参数查询
        List<String> paramCodeList = Lists.newArrayList();
        // SECLEND_ACCR_TYPE 债券借贷计提方式
        paramCodeList.add(ValDictCodeList.账套配置表参数项_债券借贷计提方式.getValue());
        // 调用账务类共通
        List<BokBooksetExt> listBooksetExt = accountingCommonService.getValBokBooksetExt(
            acctEntity.getBooksetId(), paramCodeList);

        // 计提方式 = 账套配置表参数项_债券借贷计提方式 对应的计提方式
        String calType = StringUtils.EMPTY;
        if (!CollectionUtils.isEmpty(listBooksetExt)) {
            // 获取第一条即可
            calType = listBooksetExt.get(0).getParamValue();
        }

        // 3. 处理交易
        // 3.1、循环处理计提明细
        // for（计提明细 ：计提类账务处理结果.计提明细列表）
        for (DrawBaseInfoSelectComDTO dto : listDrawBaseInfoSelectComDTONow) {

            // 3.1、取交易标的信息
            // 调用共通设计 -> 标的类共通方法设计
            TradeInfoDto tradeInfoDto = this.getTargetInfo(dto.getBusiId(),
                dto.getBusiDetailType(), dto.getBranch());

            // 3.1.1 入账处理
            // 获取初始成本科目余额
            String baseSubjectNatureBj = DictCodeBaseSubjectNatrueNew.期付债券本金.getValue();
            if(DictCodeListExt1.金融产品类型_债券借贷融入.getValue().equals(tradeInfoDto.getOFinprodType())){
                baseSubjectNatureBj = DictCodeBaseSubjectNatrueNew.期收债券本金.getValue();
            }
            SubjectBalance subjectBalance = this.getSubjectBalanceEx(acctEntity.getBooksetId(),
                acctEntity.getBokDetailId(), acctEntity.getHappenDate(), acctEntity.getBookDate(),
                DictCodeList.借贷方向_借.getValue(), baseSubjectNatureBj, taskPri);

            costBalance = subjectBalance.getBalance(); // 变量.成本科目余额
            BigDecimal costNum = subjectBalance.getNum(); // 变量.数量余额
            // 最新余额：应计利息类科目余额
            String baseSubjectNature = StringUtils.EMPTY; // 基本科目属性
            String expBalanceDirect = StringUtils.EMPTY; // 期待余额方向
            // 基本科目属性： case '债券借贷融入' then '贷方' else '借方'
            // 期待余额方向： case '债券借贷融入' then '应付债券借贷借入费' else '应收债券借贷费用
            if (DictCodeListExt1.金融产品类型_债券借贷融入.getValue().equals(dto.getBusiDetailType())) {
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.应付利息_融入.getValue();
                expBalanceDirect = DictCodeList.余额方向_贷方.getValue();
            }
            else {
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.应收利息_融出.getValue();
                expBalanceDirect = DictCodeList.余额方向_借方.getValue();
            }
            interestBlance = this.getSubjectBalance(acctEntity.getBooksetId(),
                acctEntity.getBokDetailId(), acctEntity.getHappenDate(), acctEntity.getBookDate(),
                expBalanceDirect, baseSubjectNature, taskPri);

            // if 发生日 < 交易标的DTO.到期日
            if (DateUtil.compareDate(acctEntity.getHappenDate(), tradeInfoDto.getMdate()) < 0) {
                // 调用共通设计-> 质押式回购计提
                BigDecimal[] interests = calculationCommonService.calRepo(tradeInfoDto,
                    costBalance, costBalance, DateUtil.addDay(acctEntity.getHappenDate(), 1),
                    calType, interestBlance);
                // 变量.应计利息 = 返回值.发生应计利息
                interest = ArithmeticUtil.round(interests[1], NumberEnum.MONEY); // 调用金额共通方法设置有效小数位
            }
            else {
                interest = BigDecimal.ZERO;
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
            // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
            accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity, dto);
            // 分类1 <- 变量.交易标的DTO.分类1
            accountSceneMap.put(BookConstant.TYPE_1, tradeInfoDto.getType1());
            // 分类2 <- 变量.交易标的DTO.分类2
            accountSceneMap.put(BookConstant.TYPE_2, tradeInfoDto.getType2());
            // 备注 <- 金融产品类型+"每日计提"
            String finprodTypeName = SysDataUtil.getNameFromDataDict(DictCode.金融产品类型,dto.getBusiDetailType());
            accountSceneMap.put(BookConstant.BOOK_REMARK, finprodTypeName + "每日计提");

            // 会计场景设置
            // if 交易标的DTO.金融产品类型 = “债券借贷融入”
            if (DictCodeListExt1.金融产品类型_债券借贷融入.getValue().equals(dto.getBusiDetailType())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_债券借贷_借入费计提.getValue()); // ad.会计场景代码="债券借贷-融入费计提"
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_债券借贷_借出费计提.getValue()); // ad.会计场景代码="债券借贷-融出费计提"
            }
            // 塞入记账场景树节点 = 会计场景
            ad.setTreeNode(acctEntity.getBookSetTreeNode());
            // 应收收入 <- 变量.应计利息
            // 应收支出 <- 变量.应计利息
            accountAmountMap.put(BookConstant.INCAMT, interest);
            accountAmountMap.put(BookConstant.EXPAMT, interest);

            // 应收收入 <- 计提明细.交易币种
            // 应收支出 <- 计提明细.交易币种
            accountCurrencyMap.put(BookConstant.INCAMT, dto.getoCcy());
            accountCurrencyMap.put(BookConstant.EXPAMT, dto.getoCcy());

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

            // add by weipeng on 20220321：增加辅助核算项配置 start
            ad.setSubjectMappingConfigDefault(dto.getSubjectMappingConfig());
            // add by weipeng on 20220321：增加辅助核算项配置 end

            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(
                resultNowNow.getValBokBookdetailBatch());

            // 3.3 统计分析账务明细汇总信息赋值
            // 定义当日统计分析账务明细汇总信息
            BokStatDetPosiiton bokSummaryTableNew = new BokStatDetPosiiton();
            // 变量.当日统计分析账务明细汇总信息.当前持仓 = 变量.数量余额
            bokSummaryTableNew.setPosAmt(costNum);
            // 变量.当日统计分析账务明细汇总信息.计提利率 = 变量.计提利率
            bokSummaryTableNew.setIntRate(tradeInfoDto.getIntRate());
            // 变量.当日统计分析账务明细汇总信息.日摊销收益率 =NULL
            bokSummaryTableNew.setDailydscYield(null);
            // 变量.当日统计分析账务明细汇总信息.当日发生应计利息 = 变量.应计利息
            bokSummaryTableNew.setTdyIntincexpAdd(interest);
            // // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.昨日统计分析账务明细汇总信息.当日累计应计利息余 + 变量.应计利息
            // BigDecimal tdyintincexpNow = BigDecimal.ZERO;
            // if (bokSummaryTableYestdy != null) {
            // tdyintincexpNow = ArithmeticUtil.doAdd(bokSummaryTableYestdy.getTdyIntincexp(),
            // interest);
            // }
            // else {
            // tdyintincexpNow = interest;
            // }
            // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.应计利息余额+ 变量.应计利息
            bokSummaryTableNew.setTdyIntincexp(ArithmeticUtil.doAdd(interestBlance, interest));
            // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.数量余额
            bokSummaryTableNew.setTdyPosition(costNum);
            // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的
            bokSummaryTableNew.setInvAim(acctEntity.getInvAim());
            // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.成本科目余额
            bokSummaryTableNew.setTdyCostAmt(costBalance);
            // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 交易标的DTO.金融产品类型
            bokSummaryTableNew.setFinprodType(dto.getBusiDetailType());
            // 账套代码
            bokSummaryTableNew.setBooksetId(acctEntity.getBooksetId());
            // 发生日期
            bokSummaryTableNew.setHappenDate(acctEntity.getHappenDate());
            // 入账日期
            bokSummaryTableNew.setBookDate(acctEntity.getBookDate());
            // 账务明细代码
            bokSummaryTableNew.setBokDetailId(acctEntity.getBokDetailId());
            // 账套日期
            bokSummaryTableNew.setBooksetDate(dto.getBooksetDate());
            // 处理序列
            bokSummaryTableNew.setBookSummaryOrder(
                new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列)));
            // 变量.当日统计分析账务明细汇总信息.起息日 = 交易标的DTO.起息日
            bokSummaryTableNew.setVdate(tradeInfoDto.getVdate());
            // 变量.当日统计分析账务明细汇总信息.到期日 = 交易标的DTO.实际到期日
            bokSummaryTableNew.setMdate(tradeInfoDto.getMdate());
            
            bokSummaryTableNew.setCcy(tradeInfoDto.getCcy());
            if(null != tradeInfoDto.getMdate()){
                // 变量.当日统计分析账务明细汇总信息.剩余期限 = 变量.标的信息.实际到期日-计提类账务处理结果.发生日
                bokSummaryTableNew.setEndDays1(new BigDecimal(DateUtil.getBetweenDayNumber(
                    acctEntity.getHappenDate(), tradeInfoDto.getMdate())));
                // 变量.当日统计分析账务明细汇总信息.剩余存续期 = 变量.标的信息.实际到期日-计提类账务处理结果.发生日
                bokSummaryTableNew.setEndDays2(new BigDecimal(DateUtil.getBetweenDayNumber(
                    acctEntity.getHappenDate(), tradeInfoDto.getMdate())));
            }
            // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
            bokSummaryTableNew.setBusiId(acctEntity.getBusiId());


            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== beign
            //本位币
            bokSummaryTableNew.setbCcy(bokSummaryTableNew.getCcy());
            //当日发生应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpAddB(bokSummaryTableNew.getTdyIntincexpAdd());
            // 当日成本_本位币
            bokSummaryTableNew.setTdyCostAmtB(bokSummaryTableNew.getTdyCostAmt());
            // 当日公允价值变动_本位币
            bokSummaryTableNew.setTdyFloatIngplB(bokSummaryTableNew.getTdyFloatIngpl());
            // 当日累计公允价值变动_本位币
            bokSummaryTableNew.setTdyFloatIngplExpB(bokSummaryTableNew.getTdyFloatIngplExp());
            // 当日应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpB(bokSummaryTableNew.getTdyIntincexp());
            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== end



            // 添加到返回值中
            returnResult.getBokSummaryList().add(bokSummaryTableNew);

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
     * @param taskPri 任务优先级
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
        if (StringUtils.isNotEmpty(baseSubjectNature)) {
            baseSubjectNatureList = Lists.newArrayList();
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
        if (CollectionUtils.isNotEmpty(subjectBalanceList)) {
            balance = subjectBalanceList.get(0).getBalance();
        }

        return balance;
    }

    /**
     * 调用共通方法：[标的类共通方法-查询交易信息]，获取标的交易信息
     * 
     * @param finprodId 金融产品代码【必输项】
     * @param finprodType 金融产品类型【必输项】
     * @param branch 分支序号
     * @return ValFinProductDeal 标的查询
     * @see
     * @since 1.0
     */
    private TradeInfoDto getTargetInfo(String finprodId, String finprodType, Long branch) {
        // 定义返回值 - 标的交易信息
        TradeInfoDto tradeInfoDtoForReturn = new TradeInfoDto();

        // 调用共通方法：[标的类共通方法 - 交易信息查询]，获取标的交易信息
        List<TradeInfoDto> tradeInfoDtoList = finProductCommonService.getTradeInfoDtoList(
            finprodId, finprodType, branch);

        // 当查询结果不为空
        if (CollectionUtils.isNotEmpty(tradeInfoDtoList)) {
            // 默认取第一条标的信息
            tradeInfoDtoForReturn = tradeInfoDtoList.get(0);
        }

        return tradeInfoDtoForReturn;
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
        if (StringUtils.isNotEmpty(baseSubjectNature)) {
            baseSubjectNatureList = Lists.newArrayList();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId); // 账套代码
        searchDto.setBokDetailId(bokDetailId); // 账务明细代码
        searchDto.setHappenDate(happenDate); // 发生日
        searchDto.setBookDate(bookDate); // 入账日
        searchDto.setTaskPri(taskPri); // 任务优先级
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList); // 基础科目性质
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);

        // 获取科目余额
        if (CollectionUtils.isNotEmpty(subjectBalanceList)) {
            balance = subjectBalanceList.get(0);
        }
        else {
            balance.setBalance(BigDecimal.ZERO);
            balance.setNum(BigDecimal.ZERO);
        }

        return balance;
    }

}
