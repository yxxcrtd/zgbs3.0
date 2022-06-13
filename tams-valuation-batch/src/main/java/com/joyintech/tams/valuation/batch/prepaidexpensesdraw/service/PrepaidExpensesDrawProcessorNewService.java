package com.joyintech.tams.valuation.batch.prepaidexpensesdraw.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.*;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TradeInfoDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 待摊费用计提入账批处理
 */
@Service
@Scope("prototype")
public class PrepaidExpensesDrawProcessorNewService {

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
     * 公用方法services
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

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

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }

        // 获取从 task 拿到的参数
        SysBatchExecInfo batchExecInfo = (SysBatchExecInfo)param1.get("BATCHINFO"); // 待处理任务信息

        // 定义返回值
        BookGenerateResultWithBokSummary returnResult = new BookGenerateResultWithBokSummary();

        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景

        //1、获取计提参数
        // 账套核算参数查询
        List<String> paramCodeList = new ArrayList<String>();
        // UNAMO_ACCR_TYPE 账套配置表参数项_待摊费用计提方式
        //-----------------------------------------------------------
        paramCodeList.add(ValDictCodeList.账套配置表参数项_待摊费用计提方式.getValue());
        // 调用账务类共通（DTO.账套代码, 参数项：如下）
        List<BokBooksetExt> listBooksetExt = accountingCommonService.getValBokBooksetExt(
                booksetIdNow, paramCodeList);
        //定义 变量.计提方式 = 返回值
        String calType = "";
        if (!CollectionUtils.isEmpty(listBooksetExt)) {
            // 获取第一条即可
            calType = listBooksetExt.get(0).getParamValue();
        }


        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTO = acctEntity.getListDrawBaseInfoSelectComDTO();
        if(listDrawBaseInfoSelectComDTO == null || listDrawBaseInfoSelectComDTO.size() == 0){
            return  returnResult;
        }

        for (DrawBaseInfoSelectComDTO dto : listDrawBaseInfoSelectComDTO) {
            //2、调用系统共通方法
            // 2.1 交易类标的信息查询
            TradeInfoDto dealInfoForTarget = this.getTargetInfo(dto.getBusiId(),dto.getBusiDetailType(),dto.getBranch());
            // 上述返回结果为空
            if (dealInfoForTarget == null || dealInfoForTarget.getVdate() ==null || dealInfoForTarget.getMdate() == null
                || dealInfoForTarget.getVdate().after(acctEntity.getHappenDate()) || dealInfoForTarget.getMdate().before(acctEntity.getHappenDate())) {
                continue;
            }

            //2.2 变量.待摊费用余额
            //调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
            SubjectBalance subjectBalance = this.getSubjectBalance(booksetIdNow,
                    acctEntity.getBokDetailId(), happenDateNow, bookDateNow,
                    DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.待摊费用.getValue(), null, dto.getTaskPri());
            //定义 变量.待摊费用本币余额 = 待摊费用DTO.本币余额
            BigDecimal prepaidBalance_BCcy = subjectBalance.getbAmt();
            //定义 变量.待摊费用余额 = 待摊费用DTO.科目余额
            BigDecimal prepaidBalance = subjectBalance.getBalance();
            //名义本金
            BigDecimal prin = dealInfoForTarget.getPrin();


            //2.3 调用共通
            // 变量.昨日统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
            BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
            DateUtil.addDay(acctEntity.getHappenDate(), -1),
            DateUtil.addDay(acctEntity.getHappenDate(), -1), acctEntity.getBookDate(),
            acctEntity.getBooksetId(), acctEntity.getBokDetailId(), null, null);
            if(bokSummaryTableYestdy == null){
                bokSummaryTableYestdy = new BokStatDetPosiiton();
            }

            //3、计算当日摊销费用
            //变量.当日待摊费用
            BigDecimal prepaidFeeAmt = BigDecimal.ZERO;
            //变量.当日待摊费用_本币
            BigDecimal prepaidFeeAmt_BCcy = BigDecimal.ZERO;
            //DTO.入账日-DTO.起息日+1）
            BigDecimal A =new BigDecimal(DateUtil.getBetweenDayNumber(dealInfoForTarget.getVdate(), bookDateNow) + 1);
            //(DTO.到期日-DTO.起息日+1）
            BigDecimal B = new BigDecimal(DateUtil.getBetweenDayNumber(dealInfoForTarget.getVdate(), dealInfoForTarget.getMdate()) + 1);
            // if 变量.计提方式 = 【数据字典】.计提方式."区间倒减"
            if (DictCodeList.计提方式_区间倒减.getValue().equals(calType)) {
                //变量.累计摊销额 = 标的信息.名义本金*（DTO.入账日-DTO.起息日+1）/(DTO.到期日-DTO.起息日+1） 调用金额共通方法设置2位有效小数位
                BigDecimal addMoney = ArithmeticUtil.divide(ArithmeticUtil.multiply(prin, A), B, NumberEnum.MONEY.getCode());
                //变量.每日摊销额总和 = 标的信息.名义本金 - 变量.待摊费用余额
                BigDecimal dayMoneySum = ArithmeticUtil.subtract(prin, prepaidBalance);
                //变量.当日待摊费用 = 变量.累计摊销额 - 变量.每日摊销额总和
                prepaidFeeAmt = ArithmeticUtil.subtract(addMoney, dayMoneySum);
                //if(DTO.入账日 = DTO.到期日)
                if(DateUtil.compareDate(bookDateNow, dealInfoForTarget.getMdate()) == 0){
                    //变量.当日待摊费用_本币 = 变量.待摊费用本币余额
                    prepaidFeeAmt_BCcy = prepaidBalance_BCcy;
                } else {
                    //变量.当日待摊费用_本币 = 变量.当日待摊费用 / 变量.待摊费用余额 * 变量.待摊费用本币余额   调用金额共通方法设置2位有效小数位
                    prepaidFeeAmt_BCcy = ArithmeticUtil.multiply(NumberEnum.MONEY.getCode(), null, ArithmeticUtil.divide(prepaidFeeAmt, prepaidBalance), prepaidBalance_BCcy) ;
                }
            } else {
                //if(DTO.入账日 = DTO.到期日)
                if(DateUtil.compareDate(bookDateNow, dealInfoForTarget.getMdate()) == 0){
                    //变量.当日待摊费用 = 变量.待摊费用余额
                    prepaidFeeAmt = prepaidBalance;
                    //变量.当日待摊费用_本币 = 变量.待摊费用本币余额
                    prepaidFeeAmt_BCcy = prepaidBalance_BCcy;
                } else {
                    //变量.当日待摊费用 = 标的信息.名义本金/(DTO.到期日-DTO.起息日+1）     调用金额共通方法设置2位有效小数位
                    prepaidFeeAmt = ArithmeticUtil.divide(prin, B, NumberEnum.MONEY.getCode());
                    //变量.当日待摊费用_本币 = 变量.当日待摊费用 / 变量.待摊费用余额 * 变量.待摊费用本币余额        调用金额共通方法设置2位有效小数位
                    prepaidFeeAmt_BCcy = ArithmeticUtil.multiply(NumberEnum.MONEY.getCode(), null, ArithmeticUtil.divide(prepaidFeeAmt, prepaidBalance), prepaidBalance_BCcy);
                }
            }

            //4、生成记账凭证

            AccountDriver ad = new AccountDriver();
            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放交易金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();
            ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
            //tableId = 变量.待摊费用计提.场景代码
            ad.setTableId(DictCodeListTableId.会计场景代码_费用摊销.getValue());
            //accountAmountMap设置如下：
            // 调用共通场景元素赋值-计提类场景元素
            accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity, dto);
            //登记托管机构 DTO.登记托管机构
            accountSceneMap.put(BookConstant.REGIST_ORG, dealInfoForTarget.getRegistOrg());
            //TYPE_1 <- 标的信息.TYPE_1
            accountSceneMap.put(BookConstant.TYPE_1, dealInfoForTarget.getType1());
            //备注  DictCodeList.from(DictCode.会计场景代码, tableId).getCodeName()
            accountSceneMap.put(BookConstant.BOOK_REMARK, "产品-费用待摊");
            //待摊费用  <- 变量.当日待摊费用
            accountAmountMap.put(BookConstant.FEEAMT, prepaidFeeAmt);
            //待摊费用_本位币  <- 变量.当日待摊费用_本位币
            accountAmountMap.put(BookConstant.FEEAMT+BookConstant.BCCYAMT, prepaidFeeAmt_BCcy);
            if(StringUtil.equals(DictCodeListExt1.一次性费用类型_交易手续费.getValue(),dealInfoForTarget.getType1())
            || StringUtil.equals(DictCodeListExt1.一次性费用类型_结算手续费.getValue(),dealInfoForTarget.getType1())){
                accountAmountMap.put(BookConstant.FEEAMT_JY, prepaidFeeAmt);
                //待摊费用_本位币  <- 变量.当日待摊费用_本位币
                accountAmountMap.put(BookConstant.FEEAMT_JY+BookConstant.BCCYAMT, prepaidFeeAmt_BCcy);
            }
            else{
                accountAmountMap.put(BookConstant.FEEAMT_QT, prepaidFeeAmt);
                //待摊费用_本位币  <- 变量.当日待摊费用_本位币
                accountAmountMap.put(BookConstant.FEEAMT_QT+BookConstant.BCCYAMT, prepaidFeeAmt_BCcy);
            }


            //accountCurrencyMap设置如下：
            // 因为与记账金额元素map的key及数量一致，故采用循环
            for (String keyNow : accountAmountMap.keySet()) {
                accountCurrencyMap.put(keyNow, dto.getoCcy());// 币种 - 交易明细.交易币种
            }
            // 本位币设置
            accountCurrencyMap.put(BookConstant.B_CCY, dto.getStandardCcy());
            
            ad.setAccountSceneMap(accountSceneMap);
            ad.setAccountAmountMap(accountAmountMap);
            ad.setAccountCurrencyMap(accountCurrencyMap);

            // add by weipeng on 20220307:会计引擎升级改造，增加辅助核算项入参 start
            SubjectMappingConfig mappingConfig = new SubjectMappingConfig();
            // 费用类型
            mappingConfig.setFeeType(dealInfoForTarget.getType1());
            // 登记托管机构
            mappingConfig.setRegistOrg(dealInfoForTarget.getRegistOrg());
            // 证券品种
            mappingConfig.setSecType(dealInfoForTarget.getType2());
            // 交易场所
            mappingConfig.setTradeMarket(DictCodeListExt1.估值交易市场_银行间.getValue());
            ad.setSubjectMappingConfigDefault(mappingConfig);
            // add by weipeng on 20220307:会计引擎升级改造，增加辅助核算项入参 end

            // 记账凭证生成 : 调用记账引擎公共记账方法，生成记账凭证
            BookGenerateResult bookGenerateResult = bookLogicNew.genAccount(ad, batchExecInfo);
            returnResult.getValBokBookdetailBatch().addAll(bookGenerateResult.getValBokBookdetailBatch());

            //5. 统计分析账务明细汇总信息赋值
            // 定义当日统计分析账务明细汇总信息
            BokStatDetPosiiton bokSummaryTableNew = new BokStatDetPosiiton();
            //变量.当日统计分析账务明细汇总信息.当前持仓 =  变量.待摊费用余额 - 变量.当日待摊费用
            bokSummaryTableNew.setPosAmt(ArithmeticUtil.subtract(prepaidBalance, prepaidFeeAmt));
            //变量.当日统计分析账务明细汇总信息.当日发生应计利息 = 变量.当日待摊费用
            bokSummaryTableNew.setTdyIntincexpAdd(prepaidFeeAmt);
            //变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 标的信息.名义本金 - 变量.待摊费用余额 + 变量.当日待摊费用
            BigDecimal tdyintincexpNow = ArithmeticUtil.doAdd(bokSummaryTableYestdy.getTdyIntincexp(), prepaidFeeAmt);
            bokSummaryTableNew.setTdyIntincexp(tdyintincexpNow);
            //变量.当日统计分析账务明细汇总信息.当日份额 =  变量.待摊费用余额 - 变量.当日待摊费用
            bokSummaryTableNew.setTdyPosition(ArithmeticUtil.subtract(prepaidBalance, prepaidFeeAmt));
            //变量.当日统计分析账务明细汇总信息.当日成本 = 变量.待摊费用余额 - 变量.当日待摊费用
            bokSummaryTableNew.setTdyCostAmt(ArithmeticUtil.subtract(prepaidBalance, prepaidFeeAmt));
            //变量.当日统计分析账务明细汇总信息.起息日 = 变量.标的信息.起息日
            bokSummaryTableNew.setVdate(dealInfoForTarget.getVdate());
            //变量.当日统计分析账务明细汇总信息.到期日 = 变量.标的信息.实际到期日
            bokSummaryTableNew.setMdate(dealInfoForTarget.getMdate());
            //变量.当日统计分析账务明细汇总信息.金融产品类型 = dto.业务明细代码
            bokSummaryTableNew.setFinprodType(dto.getBusiDetailType());
            //变量.当日统计分析账务明细汇总信息.剩余期限 = 0
            bokSummaryTableNew.setEndDays1(BigDecimal.ZERO);
            //变量.当日统计分析账务明细汇总信息.剩余存续期 = 0
            bokSummaryTableNew.setEndDays2(BigDecimal.ZERO);
            //变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
            bokSummaryTableNew.setBusiId(dto.getBusiId());
            //变量.当日统计分析账务明细汇总信息.币种= 计提类账务处理结果.原币币种
            bokSummaryTableNew.setCcy(dto.getoCcy());
            //变量.当日统计分析账务明细汇总信息.当日发生应计利息_本位币 = 变量.待摊费用余额_本位币
            bokSummaryTableNew.setTdyIntincexpAddB(subjectBalance.getbAmt());
            //变量.当日统计分析账务明细汇总信息.当日累计应计利息余额_本位币 =变量.昨日统计分析账务明细汇总信息.当日累计应计利息余额_本位币 + 变量.当日待摊费用_本币
            bokSummaryTableNew.setTdyIntincexpB(ArithmeticUtil.doAdd(bokSummaryTableYestdy.getTdyIntincexpB(),prepaidFeeAmt_BCcy));
            //变量.当日统计分析账务明细汇总信息.当日成本_本位币= 变量.待摊费用本币余额 - 变量.当日待摊费用_本币
            bokSummaryTableNew.setTdyCostAmtB(ArithmeticUtil.subtract(prepaidBalance_BCcy, prepaidFeeAmt_BCcy));
            //变量.当日统计分析账务明细汇总信息.本位币= 计提类账务处理结果.本位币币种
            bokSummaryTableNew.setbCcy(dto.getStandardCcy());
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
     * @param invAim 投资目的
     * @param taskPri 任务优先级
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId, Date happenDate,
                                             Date bookDate, String expBalanceDirect,
                                             String baseSubjectNature, String invAim,
                                             Long taskPri) {

        // 定义返回的model
        SubjectBalance subjectBalance = new SubjectBalance();

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 塞入投资目的
        /*List<String> invAimList = null;
        if (invAim != null && invAim.length() > 0) {
            invAimList = new ArrayList<String>();
            invAimList.add(invAim);
        }*/

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        //searchDto.setInvAimList(invAimList);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);
        if (subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }

        return subjectBalance;
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
        TradeInfoDto tradeInfoDtoForReturn = null;

        // 调用共通方法：[标的类共通方法 - 交易信息查询]，获取标的交易信息
        List<TradeInfoDto> tradeInfoDtoList = finProductCommonService.getTradeInfoDtoList(finprodId, finprodType, branch);

        // 当查询结果不为空
        if (tradeInfoDtoList != null && tradeInfoDtoList.size() > 0) {
            // 默认取第一条标的信息
            tradeInfoDtoForReturn = tradeInfoDtoList.get(0);
        }

        return tradeInfoDtoForReturn;
    }

}
