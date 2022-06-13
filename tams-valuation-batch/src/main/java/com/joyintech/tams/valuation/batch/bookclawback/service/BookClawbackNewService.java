package com.joyintech.tams.valuation.batch.bookclawback.service;

import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokClawbackInfo;
import com.joyintech.tams.common.base.entity.BokClawbackInfoDetail;
import com.joyintech.tams.common.base.entity.BokStatBokPosition;
import com.joyintech.tams.common.base.entity.BokSubject;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.bookclawback.dao.BookClawbackMapper;
import com.joyintech.tams.valuation.batch.bookclawback.dto.BookClawbackDealAccount;
import com.joyintech.tams.valuation.batch.bookclawback.dto.BookClawbackDto;
import com.joyintech.tams.valuation.batch.bookclawback.dto.BookClawbackResult;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.entity.InterestBasisDaysParameter;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.tafloatfeeaccounts.entity.TaFloadFeeAccountsDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.constants.*;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Scope("prototype")
public class BookClawbackNewService {

    /**
     * 标的共通方法类
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 计算类共通方法
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * 账务类共通方法
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 记账凭证生成类
     */
    @Autowired
    private BookAccountGenLogicNew bookAccountGenLogicNew;

    /**
     * 回补Mapper
     */
    @Autowired
    private BookClawbackMapper bookClawbackMapper;

    // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 start
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 end

    @SuppressWarnings("unchecked")
    public BookClawbackResult execute(Map<String, Object> params) throws Exception {

        BookClawbackResult rtnResult = new BookClawbackResult();
        // 获取reader返回值
        StayDrawSubprdEntity acctEntity = (StayDrawSubprdEntity)params.get(GlobalConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }
        // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 start
        try{
            // 定义此process的返回变量
            BookGenerateResult returnResult = new BookGenerateResult();

            // 账务明细若为空赋初始值
            if (returnResult.getValBokBookdetailBatch() == null) {
                returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
            }

            // 待出账交易list
            List<BookClawbackDealAccount> bookClawbackDealAccountList = new ArrayList<>();

            // 获取从task拿到的参数
            SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)params.get("BATCHINFO");// 获取待处理任务信息
            Date happenDate = (Date)params.get("HAPPEN_DATE"); // 发生日：批处理任务执行信息.处理日（发生日）
            String taskId = batchExecuteInfo.getTaskId();

            // 获取从reader中拿到的值
            String booksetId = acctEntity.getBooksetId();// 账套代码
            String finprodId = acctEntity.getFinprodId();// 金融产品代码
            // String bokDetailId = acctEntity.getBokDetailId(); // 账务明细代码
            String cashId = acctEntity.getCashId();// 现金流代码
            String cashType1 = acctEntity.getCashType1();// 现金流一级类型
            String cashType2 = acctEntity.getCashType2();// 现金流二级类型
            Date vdate = acctEntity.getVdate();// 起息日
            Date bookDate = acctEntity.getBookDate();// 入账日
            String standardCcy = acctEntity.getStandardCcy(); // 本位币
            TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
            Long taskPri = acctEntity.getTaskPri();// 任务优先级

            // 1、调用共通方法：标的信息查询
            Map<String, Object> targetInfo = this.getTargetInfo(finprodId, cashId, cashType1, cashType2, vdate, happenDate,
                    acctEntity.getFinprodType(), acctEntity.getBranch());

            // 标的_信息
            TargetFinProduct targetFinProduct = (TargetFinProduct)targetInfo.get(ValDictCodeList.标的_信息.getValue());

            // 标的_业绩报酬计提计划
            List<TargetPerfRewardAccrsche> targetPerfRewardAccrscheList = (List<TargetPerfRewardAccrsche>)targetInfo.get(
                    ValDictCodeList.标的_业绩报酬计提计划.getValue());
            TargetPerfRewardAccrsche targetPerfRewardAccrsche = new TargetPerfRewardAccrsche();
            if (!StringUtil.isEmptyList(targetPerfRewardAccrscheList)) {
                targetPerfRewardAccrsche = targetPerfRewardAccrscheList.get(targetPerfRewardAccrscheList.size() - 1);
            }

            // 标的_业绩报酬计提规则
            List<TargetPerfRewardRule> targetPerfRewardRuleList = (List<TargetPerfRewardRule>)targetInfo.get(
                    ValDictCodeList.标的_业绩报酬计提规则.getValue());
            TargetPerfRewardRule targetPerfRewardRule = new TargetPerfRewardRule();
            if (!StringUtil.isEmptyList(targetPerfRewardRuleList)) {
                targetPerfRewardRule = targetPerfRewardRuleList.get(targetPerfRewardRuleList.size() - 1);
            }

            // 标的_业绩回补计算规则
            List<TargetFinClawbackCalcRule> targetFinClawbackCalcRuleList = (List<TargetFinClawbackCalcRule>)targetInfo.get(
                    ValDictCodeList.标的_业绩回补计算规则.getValue());
            TargetFinClawbackCalcRule targetFinClawbackCalcRule = new TargetFinClawbackCalcRule();
            if (!StringUtil.isEmptyList(targetFinClawbackCalcRuleList)) {
                targetFinClawbackCalcRule = targetFinClawbackCalcRuleList.get(targetFinClawbackCalcRuleList.size() - 1);
            }
            // 理论处理flag
            boolean theoryFlag = true;
            // 6.回补规则判断
            // 获取交易信息
            if (StringUtil.equals(targetFinClawbackCalcRule.getCawlbackType(), DictCodeList.回补方式_手动回补.getValue())) {
                Map<String, Object> maps = new HashMap<>();
                maps.put("booksetId", booksetId);
                maps.put("bookDate", bookDate);
                maps.put("busiTable", DictCodeList.业务表_金融产品表.getValue());
                maps.put("happenDate", happenDate);
                maps.put("ps", DictCodeList.交易方向_回补.getValue());

                List<BookClawbackDto> listBookClawbackDto = bookClawbackMapper.getDealInfo(maps);
                if (StringUtil.isEmptyList(listBookClawbackDto) && listBookClawbackDto.size() == 0 ){
                    theoryFlag = false;
                }
                for (BookClawbackDto dto : listBookClawbackDto) {
                    BigDecimal 回补金额 = BigDecimal.ZERO;
                    if(StringUtil.equals(dto.getFinprodId(), targetFinClawbackCalcRule.getFinprodId())) {
                        if (StringUtil.equals(dto.getTradeStatus(), DictCodeListExt1.交易状态_初始.getValue())) {
                            回补金额 = dto.getIntAmt();
                        }
                        else if (StringUtil.equals(dto.getTradeStatus(), DictCodeListExt1.交易状态_撤销.getValue())) {
                            回补金额 = ArithmeticUtil.multiply(dto.getIntAmt(), new BigDecimal(-1));
                        }

                        if (回补金额.compareTo(BigDecimal.ZERO) != 0) {
                            BookClawbackDealAccount bookClawbackDealAccount = generateBookClawbackDealAccount(回补金额, dto.getFeeType(),
                                    dto.getBokDetailId(), dto.getCcy());
                            bookClawbackDealAccountList.add(bookClawbackDealAccount);
                        }
                    }
                }
            }

            // 2、获取日期信息
            // 定义变量： 回补周期开始日
            Date clawbackDateStart = null;
            // 定义变量：回补周期结束日
            Date clawbackDateEnd = null;
            // 定义变量：期初净值日期
            Date preNetDate = null;
            // 定义变量：产品起息日 = 标的信息.起息日
            Date prdVdate = targetFinProduct.getVdate();

            // 获取上次和本次计提日
            Date lastAccrualDate = null;// 上次计提日
            Date accrualDate = null;// 本次计提日

            // update by wangcheng 20191108 计提频率getAccrCycle() 改为 计提类型getAccrType()
            if (StringUtil.equals(targetPerfRewardRule.getAccrType(), DictCodeList.计提类型_高频.getValue())) {
                clawbackDateStart = happenDate;
                clawbackDateEnd = happenDate;
                preNetDate = DateUtil.addDay(happenDate, -1);
                accrualDate = happenDate;
                lastAccrualDate = DateUtil.addDay(accrualDate, -1);// 上次计提日 = 本次计提日 - 1
            }
            else if (targetPerfRewardAccrscheList == null || targetPerfRewardAccrscheList.size() == 0) {
                // 当日无需计提
                theoryFlag = false;

            } // 标的_业绩报酬计提计划list.size == 1
            else {
                clawbackDateStart = DateUtil.addDay(targetPerfRewardAccrsche.getAccrStrDate(), 1);
                clawbackDateEnd = targetPerfRewardAccrsche.getAccrDate();
                preNetDate = targetPerfRewardAccrsche.getAccrStrDate();

                accrualDate = happenDate;
                lastAccrualDate = targetPerfRewardAccrsche.getAccrStrDate();
            }

            if (clawbackDateEnd != null && DateUtil.compareDate(clawbackDateEnd, happenDate) != 0) {
                theoryFlag = false;
            }

            if (theoryFlag) {
                // add by weipeng on 20220303：由于浮动管理费和固定管理费对应基础科目性质为一个，查询余额需要按照科目查询
                List<BokSubject> bokSubjectList = bookAccountGenLogicNew.getFeeSubjectByCode(DictCodeBaseSubjectNatrueNew.应付运营费.getValue(), booksetId, standardCcy);
                SubjectMappingConfig mappingConfig = new SubjectMappingConfig();
                mappingConfig.setCashType2(DictCodeListExt1.现金流二级类型_固定管理费.getValue());
                // 定义 变量.固定管理费科目信息
                BokSubject fixedFeeSubject = bookAccountGenLogicNew.getSubjectByInnerCode(bokSubjectList, mappingConfig);
                // 定义 变量.浮动管理费科目信息
                mappingConfig.setCashType2(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
                BokSubject floatFeeSubject = bookAccountGenLogicNew.getSubjectByInnerCode(bokSubjectList, mappingConfig);
                // 定义变量：计息基础 = 回补规则.计算基础
                String basis = targetFinClawbackCalcRule.getBasis();

                // 定义变量：区间天数 = 变量.回补周期结束日 - 变量.回补周期开始日 +1
                Long accrDays = DateUtil.getBetweenDayNumber(clawbackDateStart, clawbackDateEnd) + 1;

                // 计费基础 = 调用共通算法.计息基础天数（NULL，NULL，NULL，NULL，计息基础，NULL）
                InterestBasisDaysParameter input = new InterestBasisDaysParameter(null, null, null, null, basis, null);
                int calcBasis = calculationCommonService.calcInterestBasisDays(input);
                BigDecimal calcBasisDecimal = new BigDecimal(calcBasis);

                // 3、回补周期帐套资产负债信息获取
                // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->查询分层结构子结构
                params.put("booksetId", booksetId);
                params.put("lev", acctEntity.getLev());
                List<StayDrawSubprdEntity> stayDrawSubprdEntityList = accountingCommonService.querySubBokAcctInfo(params);
                // 账务明细代码集合
                List<String> bokDetailIdList = new ArrayList<>();
                for (StayDrawSubprdEntity entity : stayDrawSubprdEntityList) {
                    bokDetailIdList.add(entity.getBokDetailId());
                }

                // 变量.区间内帐套资产负债信息列表 = 调用共通方法查询：调用共通方法查询：帐套资产负债查询
                BokStatBokPositionDto position = new BokStatBokPositionDto();
                // 账套
                position.setBooksetId(booksetId);
                // 查询日开始
                position.setSdateStart(clawbackDateStart);
                // 查询结束日
                position.setSdateEnd(clawbackDateEnd);
                // 入账日
                position.setBookDate(bookDate);
                // 分层代码
                position.setLayeringId(acctEntity.getLayeringId());

                List<BokStatBokPosition> valStatBokPositionList = accountingCommonService.getValStatBokPosition(position);

                // 周期结束日帐套资产负债信息
                BokStatBokPosition cycEndBokStatBokPosition = new BokStatBokPosition();
                // 周期开始日帐套资产负债信息
                // BokStatBokPosition cycStartBokStatBokPosition = new BokStatBokPosition();
                for (BokStatBokPosition bokStatBokPosition : valStatBokPositionList) {
                    if (DateUtil.compareDate(bokStatBokPosition.getHappenDate(), clawbackDateEnd) == 0) {
                        cycEndBokStatBokPosition = bokStatBokPosition;
                    }
                }

                // 调用共通方法：科目余额汇总查询
                SubjectBalanceSearchDto subjectBalanceSearchDto = new SubjectBalanceSearchDto();
                subjectBalanceSearchDto.setBooksetId(booksetId);
                List<String> subjectTypeList = new ArrayList<>();
                subjectTypeList.add(DictCodeList.科目类型_损益类.getValue());
                subjectBalanceSearchDto.setSubjectTypeList(subjectTypeList);
                subjectBalanceSearchDto.setHappenDate(happenDate);
                subjectBalanceSearchDto.setBookDate(bookDate);
                if (!StringUtil.equals(acctEntity.getCurLev(), "1")) {
                    subjectBalanceSearchDto.setBokDetailIdList(bokDetailIdList);
                }
                subjectBalanceSearchDto.setTaskPri(taskPri);

                Map<String, BigDecimal> balanceMap = accountingCommonService.getSubjectBalanceCollection(subjectBalanceSearchDto,
                        DictCodeList.余额方向_贷方.getValue(),DictCodeList.是否_否.getValue());

                // 计提结束日损益类余额 = 调用方法：【详细设计-计算类共通方法设计】汇率折算（余额Map，DTO.本位币，DTO.发生日）
                BigDecimal 计提结束日损益类余额 = calculationCommonService.calcExchangeAmt(balanceMap, acctEntity.getStandardCcy(), happenDate);

                // 调用共通方法：科目余额汇总查询
                subjectTypeList.clear();
                subjectTypeList.add(DictCodeList.科目类型_所有者权益类.getValue());
                subjectBalanceSearchDto.setSubjectTypeList(subjectTypeList);
                Map<String, BigDecimal> balanceMap1 = accountingCommonService.getSubjectBalanceCollection(subjectBalanceSearchDto,
                        DictCodeList.余额方向_贷方.getValue(),DictCodeList.是否_否.getValue());

                // 计提结束日所有者权益余额 = 调用方法：【详细设计-计算类共通方法设计】汇率折算（余额Map，DTO.本位币，DTO.发生日）
                BigDecimal 计提结束日所有者权益余额 = calculationCommonService.calcExchangeAmt(balanceMap1, acctEntity.getStandardCcy(),
                        happenDate);

                // 定义 变量.计提结束日费后资产净值 = (计提结束日损益类余额 + 计提结束日所有者权益余额)
                BigDecimal 计提结束日费后资产净值 = ArithmeticUtil.doAdd(计提结束日损益类余额, 计提结束日所有者权益余额);

                // 周期结束日帐套资产负债信息.资产净值
                cycEndBokStatBokPosition.setNetAssetValue(计提结束日费后资产净值);

                // 定义账套参数查询参数项list
                List<String> paramCodeList = new ArrayList<String>();
                paramCodeList.add(ValDictCodeList.账套配置表参数项_万份收益小数位尾数处理.getValue());
                paramCodeList.add(ValDictCodeList.账套配置表参数项_万份收益小数位位数.getValue());
                paramCodeList.add(ValDictCodeList.账套配置表参数项_区间内浮动管理费计提方式.getValue());
                Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
                        acctEntity.getBooksetId(), paramCodeList);

                // 获取万份收益尾数处理方式和万分收益小数位
                String pyeildDecType = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_万份收益小数位尾数处理.getValue());// 估值小数位处理方式
                String pyeildDecDigits = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_万份收益小数位位数.getValue());// 估值小数位位数

                // 周期结束日帐套资产负债信息.万份收益
                BigDecimal pYield = ArithmeticUtil.multiply(ArithmeticUtil.divide(
                        ArithmeticUtil.subtract(cycEndBokStatBokPosition.getNetAssetValue(), cycEndBokStatBokPosition.getCapital()),
                        cycEndBokStatBokPosition.getCapital()), new BigDecimal(10000));

                cycEndBokStatBokPosition.setpYield(
                        accountingCommonService.filterByDecTypeAndDigits(pYield, pyeildDecType, pyeildDecDigits));

                //计提方式
                Object prdIntAccTypeObj =  (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_区间内浮动管理费计提方式.getValue());

                // 4.R值计算
                // 基础科目性质
                List<String> 基础科目性质 = new ArrayList<>();
                基础科目性质.add(DictCodeBaseSubjectNatrueNew.应付利润.getValue());
                // 变量.回补区间年化收益率
                BigDecimal 回补区间年化收益率 = BigDecimal.ZERO;

                BigDecimal 昨日区间浮动管理费余额 = BigDecimal.ZERO;
                //IF [账套参数.区间内浮动管理费计提方式 = 区间内每日倒减计提] AND [本次计提日 > 上次计提日 +1]
                if(prdIntAccTypeObj != null && DictCodeListExt3.区间内浮动管理费计提方式_区间内每日倒减计提.getValue().equals(prdIntAccTypeObj.toString())
                        && DateUtil.compareDate(accrualDate, DateUtil.addDay(lastAccrualDate,1)) > 0) {
                    /**
                     调用 SHEET 查询账务明细当前表发生额 方法查询浮动管理费区间余额
                     参数名
                     开始日期					上次计提日+1
                     结束日期					本次计提日-1
                     账套代码					DTO.账套代码
                     场景代码					null
                     基础科目性质					应付超额管理费
                     入账日					DTO.入账日
                     借贷方向					贷方
                     优先级					本次批处理优先级
                     返回结果
                     List<账务明细当前表> **/
                    Map<String, Object> param = new HashMap<>();
                    param.put("booksetId", booksetId);//DTO.账套代码
                    param.put("startHappenDate", DateUtil.addDay(lastAccrualDate,1));//上次计提日+1
                    param.put("endHappenDate", DateUtil.addDay(accrualDate,-1));//本次计提日-1
                    param.put("bookDate", bookDate);//入账日
                    param.put("cdFlag", DictCodeList.余额方向_贷方.getValue());//贷方
                    param.put("taskId", taskPri);//本次批处理优先级
                    param.put("baseSubjectNatureList", Arrays.asList(DictCodeBaseSubjectNatrueNew.应付运营费.getValue()));//应付超额管理费
                    // 科目号 = 变量.浮动管理费科目信息.科目号
                    param.put("subjectNo", floatFeeSubject.getSubjectNo());
                    List<TaFloadFeeAccountsDto> taFloadFeeAccountsList = bookClawbackMapper.selectBokBookdetailCurrent(param);
                    //LOOP 查询结果List<账务明细当前表>，调用账务类共通.余额方向处理_余额加发生额
                    if(!StringUtil.isEmptyList(taFloadFeeAccountsList)){
                        //变量.实际余额方向 = 贷方
                        String actCdFlag = DictCodeList.余额方向_贷方.getValue();
                        //变量.实际余额 = 0
                        BigDecimal actBalance = BigDecimal.ZERO;
                        for(TaFloadFeeAccountsDto item : taFloadFeeAccountsList) {
                            //调用账务类共通.余额方向处理_余额加发生额
                            Map<String, Object> balanceDealMap = accountingCommonService.balanceDirectDealAccrualAddBalance(DictCodeList.余额方向_贷方.getValue(),
                                    actCdFlag, actBalance, BigDecimal.ZERO, item.getCdFlag(), item.getoHappenAmt(),
                                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                            //变量.实际余额方向 = 返回结果.余额方向
                            actCdFlag = (String)balanceDealMap.get("balDirect");
                            //变量.实际余额 = 返回结果.科目余额
                            actBalance = (BigDecimal)balanceDealMap.get("balance");
                        }
                        昨日区间浮动管理费余额 = actBalance;
                        昨日区间浮动管理费余额 = ArithmeticUtil.round(昨日区间浮动管理费余额, NumberEnum.MONEY);
                    }
                }
                // DTO.核算类型=“货币型”
                if (StringUtil.equals(acctEntity.getAcctMode(), DictCodeList.收益类型_货币型.getValue())) {
                    // 周期内发生的红利再投查询
                    List<BookLatestInfoDto> bookLatestInfoDtoList = accountingCommonService.getBookLatestInfo(booksetId,
                            bookDate, null, 基础科目性质,  DictCodeListTableId.会计场景代码_产品_货币型_红利再投.getValue(),null, null, null,
                            clawbackDateStart, clawbackDateEnd);
                    sortByHappenDate(bookLatestInfoDtoList);
                    // 变量.红利再投信息筛选, 条件:账务明细.账务明细代码 in 子分层信息.账务明细代码
                    List<BookLatestInfoDto> 周期内发生的红利再投 = new ArrayList<>();
                    for (BookLatestInfoDto dto : bookLatestInfoDtoList) {
                        if (bokDetailIdList.contains(dto.getBokDetailId())) {
                            周期内发生的红利再投.add(dto);
                        }
                    }
                    // 组装区间年化收益率计算用数据
                    回补区间年化收益率 = calculationCommonService.getCysYearRate(valStatBokPositionList, clawbackDateStart, clawbackDateEnd,
                            周期内发生的红利再投, calcBasis, ValDictCodeList.费前费后区分_费后.getValue());
                }
                else if (StringUtil.equals(acctEntity.getAcctMode(), DictCodeList.收益类型_净值型.getValue())) {
                    回补区间年化收益率 = this.getNhsyl(clawbackDateEnd, preNetDate, acctEntity.getFinprodId(),
                            calcBasisDecimal, accrDays, bookDate, booksetId, prdVdate, acctEntity.getLayeringId(),
                            cycEndBokStatBokPosition,昨日区间浮动管理费余额);
                }

                // 回补利率计算
                BigDecimal 回补利率 = calculationCommonService.clawbackBenchMarkCalc(targetFinClawbackCalcRule, clawbackDateStart,
                        clawbackDateEnd, targetFinClawbackCalcRuleList);

                if (回补区间年化收益率.compareTo(回补利率) < 0) {
                    // 计费基数计算
                    // 变量.期初账套资产负债信息 = 调用共通方法查询：调用共通方法查询：帐套资产负债查询
                    // 查询日开始
                    position.setSdateStart(preNetDate);
                    // 查询结束日
                    position.setSdateEnd(preNetDate);
                    List<BokStatBokPosition> 期初账套资产负债信息 = accountingCommonService.getValStatBokPosition(position);

                    // 5.回补金额计算
                    // 定义 变量.回补周期内现金分红金额
                    BigDecimal 回补周期内红利再投金额  = BigDecimal.ZERO;
                    // 定义 变量.回补周期内应付利润
                    BigDecimal 回补周期内应付利润 = BigDecimal.ZERO;
                    // 定义 变量.回补周期内应付利润
                    BigDecimal 回补周期内现金分红金额 = BigDecimal.ZERO;
                    // 定义 变量.回补周期内应付利润
                    BigDecimal 回补周期内分红除权金额 = BigDecimal.ZERO;
                    // 变量.应回补金额
                    BigDecimal 应回补金额 = BigDecimal.ZERO;
                    // 变量.计费基础
                    BigDecimal 计费基数 = BigDecimal.ZERO;
                    // DTO.核算类型=“货币型”
                    if (StringUtil.equals(acctEntity.getAcctMode(), DictCodeList.收益类型_货币型.getValue())) {
                        // 周期内发生的红利再投查询
                        List<BookLatestInfoDto> bookLatestInfoDtoList1 = accountingCommonService.getBookLatestInfo(
                                booksetId, bookDate, null, 基础科目性质,  DictCodeListTableId.会计场景代码_产品_货币型_红利再投.getValue(),null,
                                null, null, clawbackDateStart, clawbackDateEnd);
                        sortByHappenDate(bookLatestInfoDtoList1);
                        List<BookLatestInfoDto> 周期内发生的红利再投 = new ArrayList<>();
                        for (BookLatestInfoDto dto : bookLatestInfoDtoList1) {
                            if (bokDetailIdList.contains(dto.getBokDetailId())) {
                                周期内发生的红利再投.add(dto);
                            }
                        }

                        // 周期内发生的应付利润查询
                        List<BookLatestInfoDto> bookLatestInfoDtoList2 = accountingCommonService.getBookLatestInfo(
                                booksetId, bookDate, null, 基础科目性质, DictCodeListTableId.会计场景代码_产品_货币型_应付利润结转.getValue(),null,
                                null, null, clawbackDateStart, clawbackDateEnd);
                        sortByHappenDate(bookLatestInfoDtoList2);
                        // 变量.应付利润信息筛选, 条件:账务明细.账务明细代码 in 子分层信息.账务明细代码
                        List<BookLatestInfoDto> 周期内发生的应付利润查询 = new ArrayList<>();
                        for (BookLatestInfoDto dto : bookLatestInfoDtoList2) {
                            if (bokDetailIdList.contains(dto.getBokDetailId())) {
                                周期内发生的应付利润查询.add(dto);
                            }
                        }

                        for (BookLatestInfoDto dto : 周期内发生的红利再投) {
                            //定义 变量.回补周期内红利再投金额  = 变量.回补周期内红利再投金额 +变量.红利再投信息.本位币发生额
                            回补周期内红利再投金额 = ArithmeticUtil.doAdd(回补周期内红利再投金额, dto.getbHappenAmt());
                        }


                        for (BookLatestInfoDto dto : 周期内发生的应付利润查询) {
                            回补周期内应付利润 = ArithmeticUtil.doAdd(回补周期内应付利润, dto.getbHappenAmt());
                        }

                        // 调用共通方法查询：期初资产净值计算
                        /**MODIFY BY YANYA ON 20190921 【期初资产净值计算]:增加输入参数基数类型(=数据字典：基数类型.资产净值) START **/
                        计费基数 = accountingCommonService.calStateBokPositionBegin(clawbackDateStart, clawbackDateEnd,
                                prdVdate, null, cycEndBokStatBokPosition, bokDetailIdList, bookDate, booksetId,
                                acctEntity.getAcctMode(), DictCodeList.基数类型_资产净值.getValue());
                        /**MODIFY BY YANYA ON 20190921 【期初资产净值计算]:增加输入参数基数类型(=数据字典：基数类型.资产净值) END **/
                        /**MODIFY BY chengzhong ON 20191223 净值型应回补金额公式修改 START **/
//                    if (DateUtil.compareDate(preNetDate, prdVdate) < 0) {
//                        //变量.应回补金额 = 变量.计费基数*(1+变量.回补利率*（变量.周期结束日 - 资产起息日 + 1）/计费基础）-（周期结束日帐套资产负债信息.资产净值+变量.回补周期内应付利润 - 变量.回补周期内红利再投金额）
//                        应回补金额 = ArithmeticUtil.subtract(
//                                ArithmeticUtil.multiply(计费基数, ArithmeticUtil.doAdd(BigDecimal.ONE,
//                                        ArithmeticUtil.divide(ArithmeticUtil.multiply(回补利率, ArithmeticUtil.doAdd(new BigDecimal(DateUtil.getBetweenDayNumber(prdVdate, clawbackDateEnd)),BigDecimal.ONE)),
//                                                calcBasisDecimal))),
//                                ArithmeticUtil.doSub(ArithmeticUtil.doAdd(cycEndBokStatBokPosition.getNetAssetValue(), 回补周期内应付利润),回补周期内红利再投金额));
//                    } else {

                        //变量.应回补金额 = 变量.计费基数*(1+变量.回补利率*变量.区间天数/计费基础）-（周期结束日帐套资产负债信息.资产净值+变量.回补周期内应付利润 - 变量.回补周期内红利再投金额）
                        应回补金额 = ArithmeticUtil.subtract(
                                ArithmeticUtil.multiply(计费基数, ArithmeticUtil.doAdd(BigDecimal.ONE,
                                        ArithmeticUtil.divide(ArithmeticUtil.multiply(回补利率, new BigDecimal(accrDays)),
                                                calcBasisDecimal))),
                                ArithmeticUtil.doSub(ArithmeticUtil.doAdd(cycEndBokStatBokPosition.getNetAssetValue(), 回补周期内应付利润),回补周期内红利再投金额));
//                    }
                        /**MODIFY BY chengzhong ON 20191223 净值型应回补金额公式修改 END **/

                    }
                    // DTO.核算类型=“净值型”
                    else if (StringUtil.equals(acctEntity.getAcctMode(), DictCodeList.收益类型_净值型.getValue())) {
                        // 调用共通方法查询：期初资产净值计算
                        BigDecimal 单位净值 = null;//注意：此处若取不到【单位净值】，需要塞入null,不能塞入0。 塞入null后【期初资产净值计算】中【 ArithmeticUtil.multipl】会按照1计算。
                        BigDecimal 期初资产净值 = BigDecimal.ZERO;
                        BigDecimal 期初累计分红金额 = BigDecimal.ZERO;
                        if (!StringUtil.isEmptyList(期初账套资产负债信息)) {
                            单位净值 = 期初账套资产负债信息.get(0).getNetUnitValue();
                            期初资产净值 = 期初账套资产负债信息.get(0).getNetAssetValue();
                            期初累计分红金额 = 期初账套资产负债信息.get(0).getTotBounsAmt();
                        }
                        /**MODIFY BY YANYA ON 20190921 【期初资产净值计算]:增加输入参数基数类型(=数据字典：基数类型.资产净值) START **/
                        计费基数 = accountingCommonService.calStateBokPositionBegin(clawbackDateStart, clawbackDateEnd, prdVdate, 单位净值,
                                cycEndBokStatBokPosition, bokDetailIdList, bookDate, booksetId, acctEntity.getAcctMode(),
                                DictCodeList.基数类型_资产净值.getValue());
                        /**MODIFY BY YANYA ON 20190921 【期初资产净值计算]:增加输入参数基数类型(=数据字典：基数类型.资产净值) END **/

                        /**MODIFY BY chengzhong ON 20191223 净值型应回补金额公式修改 START **/
                        // 定义 变量.现金分红信息 = 调用共通方法查询：凭证最新账务查询（场景代码=“8013”，发生开始日=“变量.回补周期开始日”，发生结束日=“变量.回补周期结束日”
                        //，基础科目性质=“应付利润”，入账日=DTO.入账日，账套代码=DTO.账套代码）
//                    List<BookLatestInfoDto> bookLatestInfoDtoList1 = accountingCommonService.getBookLatestInfo(
//                            booksetId, bookDate, null, 基础科目性质,  DictCodeListExt2.会计场景代码_产品_净值型_现金分红.getValue(),null,
//                            null, null, clawbackDateStart, clawbackDateEnd);
//                    sortByHappenDate(bookLatestInfoDtoList1);
//                    List<BookLatestInfoDto> 周期内发生的现金分红信息 = new ArrayList<>();
//                    for (BookLatestInfoDto dto : bookLatestInfoDtoList1) {
//                        if (bokDetailIdList.contains(dto.getBokDetailId())) {
//                        	周期内发生的现金分红信息.add(dto);
//                        }
//                    }
//
//                    for (BookLatestInfoDto dto : 周期内发生的现金分红信息) {
//                        //定义 变量.回补周期内现金分红金额  = 变量.回补周期内现金分红金额 +变量.现金分红信息.原币发生额
//                    	回补周期内现金分红金额 = ArithmeticUtil.doAdd(回补周期内现金分红金额, dto.getbHappenAmt());
//                    }
                        List<BookLatestInfoDto> bookLatestInfoDtoList1 = accountingCommonService.getBookLatestInfo(
                                booksetId, bookDate, null, 基础科目性质,  DictCodeListTableId.会计场景代码_产品_净值型_红利再投.getValue(),null,
                                null, null, clawbackDateStart, clawbackDateEnd);
                        sortByHappenDate(bookLatestInfoDtoList1);
                        List<BookLatestInfoDto> 周期内发生的红利再投 = new ArrayList<>();
                        for (BookLatestInfoDto dto : bookLatestInfoDtoList1) {
                            if (bokDetailIdList.contains(dto.getBokDetailId())) {
                                周期内发生的红利再投.add(dto);
                            }
                        }

                        for (BookLatestInfoDto dto : 周期内发生的红利再投) {
                            //定义 变量.回补周期内现金分红金额  = 变量.回补周期内现金分红金额 +变量.现金分红信息.原币发生额
                            回补周期内红利再投金额 = ArithmeticUtil.doAdd(回补周期内红利再投金额, dto.getbHappenAmt());
                        }

                        List<BookLatestInfoDto> bookLatestInfoDtoList2 = accountingCommonService.getBookLatestInfo(
                                booksetId, bookDate, null, 基础科目性质,  DictCodeListTableId.会计场景代码_产品_净值型_分红除权处理.getValue(),null,
                                null, null, clawbackDateStart, clawbackDateEnd);
                        sortByHappenDate(bookLatestInfoDtoList2);
                        List<BookLatestInfoDto> 周期内发生的分红除权 = new ArrayList<>();
                        for (BookLatestInfoDto dto : bookLatestInfoDtoList2) {
                            if (bokDetailIdList.contains(dto.getBokDetailId())) {
                                周期内发生的分红除权.add(dto);
                            }
                        }

                        for (BookLatestInfoDto dto : 周期内发生的分红除权) {
                            //定义 变量.回补周期内现金分红金额  = 变量.回补周期内现金分红金额 +变量.现金分红信息.原币发生额
                            回补周期内分红除权金额 = ArithmeticUtil.doAdd(回补周期内分红除权金额, dto.getbHappenAmt());
                        }

                        /*
                         * 变量.应回补金额 = 变量.计费基数*变量.回补利率*变量.区间天数/计费基础-（周期结束日帐套资产负债信息.资产净值+ 周期结束日帐套资产负债信息. 累计分红金额-期初帐套资产负债信息.资产净值-期初帐套资产负债信息.累计分红金额）
                         */
//                    if (DateUtil.compareDate(preNetDate, prdVdate) < 0) {
//                        //变量.应回补金额 = 变量.计费基数*变量.回补利率*（变量.周期结束日 - 资产起息日+1）/计费基础-（周期结束日帐套资产负债信息.资产净值+周期结束日帐套资产负债信息.累计分红金额-变量.计费基数）
//                        应回补金额 = ArithmeticUtil.subtract(
//                                ArithmeticUtil.divide(ArithmeticUtil.multiply(计费基数, 回补利率, ArithmeticUtil.doAdd(new BigDecimal(DateUtil.getBetweenDayNumber(prdVdate, clawbackDateEnd)),BigDecimal.ONE)),
//                                        calcBasisDecimal),
//                                ArithmeticUtil.doSub(ArithmeticUtil.doAdd(cycEndBokStatBokPosition.getNetAssetValue(),
//                                        cycEndBokStatBokPosition.getTotBounsAmt()), 计费基数));
//                    } else {
                        //变量.应回补金额 = 变量.计费基数*变量.回补利率*变量.区间天数/计费基础
                        //-（周期结束日帐套资产负债信息.资产净值 - 变量.计费基数+ 变量.回补周期内现金分红金额）
                        应回补金额 = ArithmeticUtil.subtract(
                                ArithmeticUtil.divide(ArithmeticUtil.multiply(计费基数, 回补利率, new BigDecimal(accrDays)),
                                        calcBasisDecimal),
                                ArithmeticUtil.doSub(ArithmeticUtil.doAdd(cycEndBokStatBokPosition.getNetAssetValue(),
                                        ArithmeticUtil.doSub(回补周期内分红除权金额, 回补周期内红利再投金额)), 计费基数));
//                    }
                        /**MODIFY BY chengzhong ON 20191223 净值型应回补金额公式修改 END **/
                    }

                    应回补金额=应回补金额.setScale(2, RoundingMode.HALF_UP);

                    if (应回补金额.compareTo(BigDecimal.ZERO) > 0) {
                        if (StringUtil.equals(targetFinClawbackCalcRule.getCawlbackRule(), DictCodeList.回补规则_使用浮动管理费回补.getValue())) {
                            // 调用共通方法：科目余额汇总查询
                            subjectTypeList.clear();
                            subjectBalanceSearchDto.setSubjectTypeList(subjectTypeList);
                            基础科目性质.clear();
                            基础科目性质.add(DictCodeBaseSubjectNatrueNew.应付运营费.getValue());
                            subjectBalanceSearchDto.setBaseSubjectNatureList(基础科目性质);
                            // 科目号 = 变量.浮动管理费科目信息.科目号
                            subjectBalanceSearchDto.setSubjectNo(floatFeeSubject.getSubjectNo());
                            BigDecimal 回补前管理费余额 = queryBalance(subjectBalanceSearchDto, DictCodeList.余额方向_贷方.getValue(), standardCcy);

                            if (回补前管理费余额.compareTo(BigDecimal.ZERO) != 0) {
                                BigDecimal 回补金额 = BigDecimal.ZERO;

                                if (应回补金额.compareTo(回补前管理费余额) < 0) {
                                    回补金额 = 应回补金额;
                                }
                                else {
                                    回补金额 = 回补前管理费余额;
                                }

                                if (回补金额.compareTo(BigDecimal.ZERO) > 0) {
                                    if (StringUtil.equals(targetFinClawbackCalcRule.getCawlbackType(),
                                            DictCodeList.回补方式_自动回补.getValue())) {
                                        BookClawbackDealAccount bookClawbackDealAccount = generateBookClawbackDealAccount(回补金额,
                                                DictCodeListExt1.现金流二级类型_浮动管理费.getValue(), acctEntity.getBokDetailId(), standardCcy);
                                        bookClawbackDealAccountList.add(bookClawbackDealAccount);
                                    }

                                    generateBokClawbackInfo(acctEntity, targetFinClawbackCalcRule.getCawlbackType(), 应回补金额, happenDate,
                                            rtnResult);
                                    generateBokClawbackInfoDetail(acctEntity, DictCodeListExt1.现金流二级类型_浮动管理费.getValue(),
                                            targetFinClawbackCalcRule.getCawlbackType(), 回补金额, happenDate, rtnResult);
                                }
                            }
                        }
                        else if (StringUtil.equals(targetFinClawbackCalcRule.getCawlbackRule(), DictCodeList.回补规则_使用固定管理费回补.getValue())) {
                            // 调用共通方法：科目余额汇总查询
                            subjectTypeList.clear();
                            subjectBalanceSearchDto.setSubjectTypeList(subjectTypeList);
                            基础科目性质.clear();
                            基础科目性质.add(DictCodeBaseSubjectNatrueNew.运营费用.getValue());
                            subjectBalanceSearchDto.setBaseSubjectNatureList(基础科目性质);
                            // 科目号 = 变量.固定管理费科目信息.科目号
                            subjectBalanceSearchDto.setSubjectNo(fixedFeeSubject.getSubjectNo());
                            BigDecimal 回补前管理费余额 = queryBalance(subjectBalanceSearchDto, DictCodeList.余额方向_贷方.getValue(), standardCcy);

                            if (回补前管理费余额.compareTo(BigDecimal.ZERO) != 0) {
                                BigDecimal 回补金额 = BigDecimal.ZERO;
                                if (应回补金额.compareTo(回补前管理费余额) < 0) {
                                    回补金额 = 应回补金额;
                                }
                                else {
                                    回补金额 = 回补前管理费余额;
                                }

                                if (回补金额.compareTo(BigDecimal.ZERO) > 0) {
                                    if (StringUtil.equals(targetFinClawbackCalcRule.getCawlbackType(),
                                            DictCodeList.回补方式_自动回补.getValue())) {
                                        BookClawbackDealAccount bookClawbackDealAccount = generateBookClawbackDealAccount(回补金额,
                                                DictCodeListExt1.现金流二级类型_固定管理费.getValue(), acctEntity.getBokDetailId(), standardCcy);
                                        bookClawbackDealAccountList.add(bookClawbackDealAccount);
                                    }

                                    generateBokClawbackInfo(acctEntity, targetFinClawbackCalcRule.getCawlbackType(), 应回补金额, happenDate,
                                            rtnResult);
                                    generateBokClawbackInfoDetail(acctEntity, DictCodeListExt1.现金流二级类型_固定管理费.getValue(),
                                            targetFinClawbackCalcRule.getCawlbackType(), 回补金额, happenDate, rtnResult);
                                }
                            }
                        }
                        else if (StringUtil.equals(targetFinClawbackCalcRule.getCawlbackRule(),
                                DictCodeList.回补规则_优先使用浮动管理费回补.getValue())) {
                            // 调用共通方法：科目余额汇总查询
                            subjectTypeList.clear();
                            subjectBalanceSearchDto.setSubjectTypeList(subjectTypeList);
                            基础科目性质.clear();
                            基础科目性质.add(DictCodeBaseSubjectNatrueNew.应付运营费.getValue());
                            subjectBalanceSearchDto.setBaseSubjectNatureList(基础科目性质);
                            // 科目号 = 变量.浮动管理费科目信息.科目号
                            subjectBalanceSearchDto.setSubjectNo(floatFeeSubject.getSubjectNo());
                            BigDecimal 浮动管理费余额 = queryBalance(subjectBalanceSearchDto, DictCodeList.余额方向_贷方.getValue(), standardCcy);
                            BigDecimal 浮动管理费回补金额 = BigDecimal.ZERO;
                            Boolean infoFlag = false;
                            if (浮动管理费余额.compareTo(BigDecimal.ZERO) != 0) {
                                if (应回补金额.compareTo(浮动管理费余额) < 0) {
                                    浮动管理费回补金额 = 应回补金额;
                                }
                                else {
                                    浮动管理费回补金额 = 浮动管理费余额;
                                }

                                if (浮动管理费回补金额.compareTo(BigDecimal.ZERO) > 0) {
                                    infoFlag = true;
                                    if (StringUtil.equals(targetFinClawbackCalcRule.getCawlbackType(),
                                            DictCodeList.回补方式_自动回补.getValue())) {
                                        BookClawbackDealAccount bookClawbackDealAccount = generateBookClawbackDealAccount(浮动管理费回补金额,
                                                DictCodeListExt1.现金流二级类型_浮动管理费.getValue(), acctEntity.getBokDetailId(), standardCcy);
                                        bookClawbackDealAccountList.add(bookClawbackDealAccount);
                                    }
                                }

                                generateBokClawbackInfoDetail(acctEntity, DictCodeListExt1.现金流二级类型_浮动管理费.getValue(),
                                        targetFinClawbackCalcRule.getCawlbackType(), 浮动管理费回补金额, happenDate, rtnResult);
                            }

                            BigDecimal 理论剩余回补金额 = ArithmeticUtil.subtract(应回补金额, 浮动管理费回补金额);
                            if (理论剩余回补金额.compareTo(BigDecimal.ZERO) != 0) {
                                subjectTypeList.clear();
                                subjectBalanceSearchDto.setSubjectTypeList(subjectTypeList);
                                基础科目性质.clear();
                                基础科目性质.add(DictCodeBaseSubjectNatrueNew.运营费用.getValue());
                                subjectBalanceSearchDto.setBaseSubjectNatureList(基础科目性质);
                                // 科目号 = 变量.固定管理费科目信息.科目号
                                subjectBalanceSearchDto.setSubjectNo(fixedFeeSubject.getSubjectNo());
                                BigDecimal 固定管理费科目余额 = queryBalance(subjectBalanceSearchDto, DictCodeList.余额方向_贷方.getValue(),
                                        standardCcy);

                                if (固定管理费科目余额.compareTo(BigDecimal.ZERO) != 0) {
                                    BigDecimal 固定管理费回补金额 = BigDecimal.ZERO;
                                    if (理论剩余回补金额.compareTo(固定管理费科目余额) < 0) {
                                        固定管理费回补金额 = 理论剩余回补金额;
                                    }
                                    else {
                                        固定管理费回补金额 = 固定管理费科目余额;
                                    }

                                    if (固定管理费回补金额.compareTo(BigDecimal.ZERO) > 0) {
                                        infoFlag = true;
                                        if (StringUtil.equals(targetFinClawbackCalcRule.getCawlbackType(),
                                                DictCodeList.回补方式_自动回补.getValue())) {
                                            BookClawbackDealAccount bookClawbackDealAccount = generateBookClawbackDealAccount(固定管理费回补金额,
                                                    DictCodeListExt1.现金流二级类型_固定管理费.getValue(), acctEntity.getBokDetailId(), standardCcy);
                                            bookClawbackDealAccountList.add(bookClawbackDealAccount);
                                        }

                                        generateBokClawbackInfoDetail(acctEntity, DictCodeListExt1.现金流二级类型_固定管理费.getValue(),
                                                targetFinClawbackCalcRule.getCawlbackType(), 固定管理费回补金额, happenDate, rtnResult);
                                    }
                                }
                            }

                            if (infoFlag) {
                                generateBokClawbackInfo(acctEntity, targetFinClawbackCalcRule.getCawlbackType(), 应回补金额, happenDate,
                                        rtnResult);
                            }

                        }
                        else if (StringUtil.equals(targetFinClawbackCalcRule.getCawlbackRule(), DictCodeList.回补规则_强制回补.getValue())) {
                            BigDecimal 回补金额 = 应回补金额;
                            if (回补金额.compareTo(BigDecimal.ZERO) != 0) {
                                if (StringUtil.equals(targetFinClawbackCalcRule.getCawlbackType(), DictCodeList.回补方式_自动回补.getValue())) {
                                    BookClawbackDealAccount bookClawbackDealAccount = generateBookClawbackDealAccount(回补金额,
                                            DictCodeListExt1.现金流二级类型_浮动管理费.getValue(), acctEntity.getBokDetailId(), standardCcy);
                                    bookClawbackDealAccountList.add(bookClawbackDealAccount);
                                }

                                generateBokClawbackInfo(acctEntity, targetFinClawbackCalcRule.getCawlbackType(), 应回补金额, happenDate,
                                        rtnResult);
                                generateBokClawbackInfoDetail(acctEntity, DictCodeListExt1.现金流二级类型_浮动管理费.getValue(),
                                        targetFinClawbackCalcRule.getCawlbackType(), 回补金额, happenDate, rtnResult);
                            }
                        }
                    }
                }

            }

            // 7.回补金额生成凭证
            for (BookClawbackDealAccount account : bookClawbackDealAccountList) {
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
                ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景

                // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
                accountSceneMap = bookCommonLogic.accrualElementAssignForTaFee(taskId, happenDate, acctEntity);
                if (targetFinProduct != null) {
                    accountSceneMap.put(BookConstant.TYPE_1, targetFinProduct.getType1());// 分类1
                    // -
                    // 变量.交易信息.分类1
                    accountSceneMap.put(BookConstant.TYPE_2, targetFinProduct.getType2());// 分类2
                    // -
                    // 变量.交易信息.分类2
                }

                // 定义基础科目性质
                // String baseSubjectNature = null;


                // 根据现金流二级类型，设置其他记账凭证要素
                if (!StringUtil.equals(targetFinClawbackCalcRule.getCawlbackRule(), DictCodeList.回补规则_强制回补.getValue())) {
                    if (DictCodeListExt1.现金流二级类型_固定管理费.getValue().equals(account.getFeeTyep())) {
                        // 设置账务引擎参数
                        ad.setTableId(DictCodeListTableId.会计场景代码_产品_固定资产管理费计提.getValue());// ad.会计场景代码;
                        // 记账场景要素map塞值
                        accountSceneMap.put(BookConstant.BOOK_REMARK, "产品-固定管理费计回补");// 凭证说明

                        // 设置基础科目性质
                        // baseSubjectNature = DictCodeBaseSubjectNatrueNew.应付管理人报酬.getValue();
                        // 增加交易编号避免同固定管理费凭证合并
                        accountSceneMap.put(BookConstant.TRADE_ID, "CLAWBACKID");
                    }
                    else if (DictCodeListExt1.现金流二级类型_浮动管理费.getValue().equals(account.getFeeTyep())) {
                        // 设置账务引擎参数
                        ad.setTableId(DictCodeListTableId.会计场景代码_产品_浮动管理费计提.getValue());// ad.会计场景代码;
                        // 记账场景要素map塞值
                        accountSceneMap.put(BookConstant.BOOK_REMARK, "产品-浮动管理费计回补");// 凭证说明

                        // 设置基础科目性质
                        // baseSubjectNature = DictCodeBaseSubjectNatrueNew.应付超额管理费.getValue();
                        // 增加交易编号避免同浮动管理费凭证合并
                        accountSceneMap.put(BookConstant.TRADE_ID, "CLAWBACKFLOATID");
                    }
                }
                else {
                    // 设置账务引擎参数
                    ad.setTableId(DictCodeListTableId.会计场景代码_产品_浮动管理费计提.getValue());// ad.会计场景代码;
                    // 记账场景要素map塞值
                    accountSceneMap.put(BookConstant.BOOK_REMARK, "产品-浮动管理费计回补");// 凭证说明

                    // 设置基础科目性质
                    // baseSubjectNature = DictCodeBaseSubjectNatrueNew.应付超额管理费.getValue();
                    // 增加交易编号避免同浮动管理费凭证合并
                    accountSceneMap.put(BookConstant.TRADE_ID, "CLAWBACKFLOATID");
                }

                // 记账金额元素map塞值
                accountAmountMap.put(BookConstant.ACCRUED_CHARDES,
                        ArithmeticUtil.round(ArithmeticUtil.multiply(account.getClawbackAmt(), new BigDecimal(-1)), NumberEnum.MONEY));// 费用计提

                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, account.getStandardCcy());// value值塞入当前交易币种
                }

                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
                // 定义 变量.辅助核算项配置
                SubjectMappingConfig mappingConfig = new SubjectMappingConfig();
                // 现金流二级类型 = 回补费用类型
                mappingConfig.setCashType2(account.getFeeTyep());
                ad.setSubjectMappingConfigDefault(mappingConfig);
                // 记账凭证生成
                BookGenerateResult resultNowNow = bookAccountGenLogicNew.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
            }
            return rtnResult;
        }
        catch (Exception e){
            logger.error("《回补批处理批处理》报错相关信息： 账套信息【"+acctEntity.getBooksetId()+"】；金融产品代码【"+acctEntity.getFinprodId()+"】",e);
            throw new Exception(e);
        }
        // add by weipeng on 20220114 增加批处理报错时相关明细日志打印 end



    }
    /**
     * 年化收益率查询
     *
     * @param accrualDate 本次计提日
     * @param lastAccrualDate 上次计提日
     * @param finprodid 金融产品代码
     * @param feeBasic 计费基础
     * @param days 区间天数
     * @param bookDate 入账日
     * @param booksetId 账套代码
     * @param zcVdate 产品起息日
     * @param cycEndBokStatBokPosition
     * @return
     * @throws BatchException
     * @see
     * @since 1.0
     */
    public BigDecimal getNhsyl(Date accrualDate, Date lastAccrualDate, String finprodid, BigDecimal feeBasic, Long days,
                               Date bookDate, String booksetId, Date zcVdate, String layeringId,
                               BokStatBokPosition cycEndBokStatBokPosition,BigDecimal 区间内的浮动管理费计提合计) {

        BigDecimal 年化收益率 = BigDecimal.ZERO;
        BigDecimal 期初的单位净值 = BigDecimal.ONE;
        BigDecimal 期初的累计净值 = BigDecimal.ONE;
        BigDecimal 期末的累计净值 = BigDecimal.ONE;
        BokStatBokPositionDto position = new BokStatBokPositionDto();
        // 查询日开始
        position.setSdateStart(lastAccrualDate);
        // 查询结束日
        position.setSdateEnd(lastAccrualDate);
        // 入账日
        position.setBookDate(bookDate);
        // 账套
        position.setBooksetId(booksetId);
        // 分层代码
        position.setLayeringId(layeringId);// modify by dongjun on 20190115
        // 定义账套参数查询参数项list
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期初累计净值小数位数.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期初累计净值尾数处理方式.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值小数位数.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值尾数处理方式.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期末累计净值小数位数.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期末累计净值尾数处理方式.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率小数位数.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率位数处理方式.getValue());
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
                booksetId, paramCodeList);
        // 小数位以及处理方式
        String floatEndtotalNetDigits = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期末累计净值小数位数.getValue());
        String floatEndtotalNetType = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期末累计净值尾数处理方式.getValue());
        String floatStarttotalNetDigits = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期初累计净值小数位数.getValue());
        String floatStarttotalNetType = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期初累计净值尾数处理方式.getValue());
        String floatStarttotalUnitnetDigits = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值小数位数.getValue());
        String floatStarttotalUnitnetType = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值尾数处理方式.getValue());
        String floatCyclyearYieldDigits = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率小数位数.getValue());
        String floatCyclyearYieldType = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率位数处理方式.getValue());

        // 上次计提日 < 变量：资产起息日
        if (DateUtil.compareDate(lastAccrualDate, zcVdate) < 0) {
            // 期初的累计净值,默认为1
            期初的累计净值 = BigDecimal.ONE;
            // 期初单位净值，默认为1
            期初的单位净值 = BigDecimal.ONE;
        }
        else {
            List<BokStatBokPosition> valStatBokPositionList = accountingCommonService.getValStatBokPosition(
                position);
            // 根据获取的负债信息，获取对应的净值信息
            if (valStatBokPositionList != null && valStatBokPositionList.size() > 0) {
                BokStatBokPosition valStatBokPosition = valStatBokPositionList.get(0);
                期初的累计净值 = ArithmeticUtil.doAdd(valStatBokPosition.getNetUnitValue(),
                    valStatBokPosition.getTotBounsNet());
                期初的累计净值 = accountingCommonService.filterByDecTypeAndDigits(期初的累计净值,floatStarttotalNetType,floatStarttotalNetDigits);
                期初的单位净值 = valStatBokPosition.getNetUnitValue();
                期初的单位净值 = accountingCommonService.filterByDecTypeAndDigits(期初的单位净值,floatStarttotalUnitnetType,floatStarttotalUnitnetDigits);
            }
        }
        // 查询日开始
        position.setSdateStart(accrualDate);
        // 查询结束日
        position.setSdateEnd(accrualDate);
        List<BokStatBokPosition> valStatBokPositionListForNowOnce = accountingCommonService.getValStatBokPosition(
            position);
        if (valStatBokPositionListForNowOnce != null
            && valStatBokPositionListForNowOnce.size() > 0) {
            BokStatBokPosition valStatBokPositionForNowOnce = valStatBokPositionListForNowOnce.get(
                0);

            // 期末的累计净值 = 本次计提日帐套资产负债信息.费前单位净值 + 本次计提日帐套资产负债信息.累计分红净值
//            期末的累计净值 = ArithmeticUtil.doAdd(valStatBokPositionForNowOnce.getNetUnitValueBef(),
            //                valStatBokPositionForNowOnce.getTotBounsNet());
            期末的累计净值 = cycEndBokStatBokPosition.getNetAssetValueBef().add(区间内的浮动管理费计提合计);
            期末的累计净值 = ArithmeticUtil.doAdd(ArithmeticUtil.divide(期末的累计净值,
                valStatBokPositionForNowOnce.getCapital()), valStatBokPositionForNowOnce.getTotBounsNet());
            期末的累计净值 = accountingCommonService.filterByDecTypeAndDigits(期末的累计净值,floatEndtotalNetType,floatEndtotalNetDigits);
        }

        // 年化收益率 = （期末的累计净值-期初的累计净值）/ 期初的单位净值 * 计息基础 / 区间天数
        年化收益率 = ArithmeticUtil.divide(
            ArithmeticUtil.multiply(
                ArithmeticUtil.divide(ArithmeticUtil.doSub(期末的累计净值, 期初的累计净值), 期初的单位净值), feeBasic),
            new BigDecimal(days));
        年化收益率 = accountingCommonService.filterByDecTypeAndDigits(年化收益率,floatCyclyearYieldType,floatCyclyearYieldDigits);

        return 年化收益率;
    }
    /**
     * 科目余额查询
     * 
     * @param dto 科目余额查询参数Dto
     * @param expectedBalFalg 期待余额方向
     * @return 科目余额汇总查询
     */
    private BigDecimal queryBalance(SubjectBalanceSearchDto dto, String expectedBalFalg, String standardCcy) {

        Map<String, BigDecimal> balanceMap = accountingCommonService.getSubjectBalanceCollection(dto, expectedBalFalg,DictCodeList.是否_否.getValue());
        if (balanceMap.get(standardCcy) == null) {
            return BigDecimal.ZERO;
        }
        return balanceMap.get(standardCcy);
    }

    /**
     * 生成待出账交易
     * 
     * @param clawbackAmt 回补金额
     * @param feeType 费用类型
     * @param bokDetailId 账务明细代码
     * @param standardCcy 本位币
     * @return
     */
    private BookClawbackDealAccount generateBookClawbackDealAccount(BigDecimal clawbackAmt, String feeType, String bokDetailId,
                                                                    String standardCcy) {
        BookClawbackDealAccount bookClawbackDealAccount = new BookClawbackDealAccount();
        bookClawbackDealAccount.setClawbackAmt(clawbackAmt);
        bookClawbackDealAccount.setFeeTyep(feeType);
        bookClawbackDealAccount.setBokDetailId(bokDetailId);
        bookClawbackDealAccount.setStandardCcy(standardCcy);
        return bookClawbackDealAccount;
    }

    /**
     * 生成帐套回补信息
     * 
     * @param dto StayDrawSubprdEntity // * @param feeType // * 费用类型
     * @param clawbackType 回补类型
     * @param toPayClawbackAmt 应回补金额
     * @param rtnResult BookClawbackResult 回补金额
     * @param happenDate 发生日
     * @return 待生成交易信息
     */
    private void generateBokClawbackInfo(StayDrawSubprdEntity dto, String clawbackType, BigDecimal toPayClawbackAmt,
                                         Date happenDate, BookClawbackResult rtnResult) {

        BokClawbackInfo bokClawbackInfo = new BokClawbackInfo();
        bokClawbackInfo.setBooksetId(dto.getBooksetId());
        bokClawbackInfo.setLayeringId(dto.getLayeringId());
        bokClawbackInfo.setFinprodId(dto.getFinprodId());
        bokClawbackInfo.setFinprodType(dto.getFinprodType());
        bokClawbackInfo.setBranch(dto.getBranch());
        bokClawbackInfo.setHappenDate(happenDate);
        bokClawbackInfo.setBookDate(dto.getBookDate());
        bokClawbackInfo.setBooksetDate(dto.getBooksetDate());
        bokClawbackInfo.setClawbackAmt(toPayClawbackAmt);
        bokClawbackInfo.setClawbackType(clawbackType);
        bokClawbackInfo.setCreateUser("batch");
        bokClawbackInfo.setCreateDept(null);
        bokClawbackInfo.setCreateTime(new Date());
        bokClawbackInfo.setUpdateUser("batch");
        bokClawbackInfo.setUpdateTime(new Date());
        rtnResult.getBokClawbackInfoList().add(bokClawbackInfo);
    }

    /**
     * 生成帐套回补信息明细
     * 
     * @param dto StayDrawSubprdEntity
     * @param feeType 费用类型
     * @param clawbackType 回补类型
     * @param toPayClawbackAmt 应回补金额
     * @param clawbackAmt 回补金额
     * @param happenDate 发生日
     * @return 待生成交易信息
     */
    private void generateBokClawbackInfoDetail(StayDrawSubprdEntity dto, String feeType, String clawbackType,
                                               BigDecimal clawbackAmt, Date happenDate, BookClawbackResult rtnResult) {
        BokClawbackInfoDetail bokClawbackInfoDetail = new BokClawbackInfoDetail();
        bokClawbackInfoDetail.setBooksetId(dto.getBooksetId());
        bokClawbackInfoDetail.setLayeringId(dto.getLayeringId());
        bokClawbackInfoDetail.setFinprodId(dto.getFinprodId());
        bokClawbackInfoDetail.setFinprodType(dto.getFinprodType());
        bokClawbackInfoDetail.setBranch(dto.getBranch());
        bokClawbackInfoDetail.setHappenDate(happenDate);
        bokClawbackInfoDetail.setBookDate(dto.getBookDate());
        bokClawbackInfoDetail.setBooksetDate(dto.getBooksetDate());
        bokClawbackInfoDetail.setClawbackAmt(clawbackAmt);
        bokClawbackInfoDetail.setFeeType(feeType);
        bokClawbackInfoDetail.setCreateUser("batch");
        bokClawbackInfoDetail.setCreateDept(null);
        bokClawbackInfoDetail.setCreateTime(new Date());
        bokClawbackInfoDetail.setUpdateUser("batch");
        bokClawbackInfoDetail.setUpdateTime(new Date());
        rtnResult.getBokClawbackInfoDetailList().add(bokClawbackInfoDetail);
    }

    /**
     * 标的信息查询
     * 
     * @param finprodId 金融产品代码
     * @param cashId 现金流代码
     * @param cashType1 现金流一级类型
     * @param cashType2 现金流二级类型
     * @param vdate 起息日
     * @param happenDate 发生日
     * @param finprodType 金融产品类型
     * @param branch 分支序号
     * @return 标的信息
     */
    private Map<String, Object> getTargetInfo(String finprodId, String cashId, String cashType1, String cashType2, Date vdate,
                                              Date happenDate, String finprodType, Long branch) {
        // 定义返回值
        Map<String, Object> returnMap = new HashMap<String, Object>();

        // 查询类型
        List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();

        // 传入[标的_信息]Dto
        TargetSearchDto 标的信息 = new TargetSearchDto();
        标的信息.setCashId(cashId);
        标的信息.setEffdateStart(vdate);
        标的信息.setEffdateEnd(happenDate);
        标的信息.setFinprodType(finprodType);
        标的信息.setBranch(branch);
        标的信息.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteria.add(标的信息);

        // 传入[标的_业绩报酬计提计划]Dto
        TargetSearchDto 业绩报酬计提计划 = new TargetSearchDto();
        业绩报酬计提计划.setCashId(cashId);
        业绩报酬计提计划.setCashType2(cashType2);
        业绩报酬计提计划.setEffdateStart(vdate);
        业绩报酬计提计划.setEffdateEnd(happenDate);
        业绩报酬计提计划.setFinprodType(finprodType);
        业绩报酬计提计划.setBranch(branch);
        业绩报酬计提计划.setQueryType(ValDictCodeList.标的_业绩报酬计提计划.getValue());
        queryCriteria.add(业绩报酬计提计划);

        // 传入[标的_业绩报酬计提规则]Dto
        TargetSearchDto 业绩报酬计提规则 = new TargetSearchDto();
        业绩报酬计提规则.setCashId(cashId);
        业绩报酬计提规则.setCashType2(cashType2);
        业绩报酬计提规则.setEffdateStart(vdate);
        业绩报酬计提规则.setEffdateEnd(happenDate);
        业绩报酬计提规则.setFinprodType(finprodType);
        业绩报酬计提规则.setBranch(branch);
        业绩报酬计提规则.setQueryType(ValDictCodeList.标的_业绩报酬计提规则.getValue());
        queryCriteria.add(业绩报酬计提规则);

        // 传入[标的_业绩回补计算规则]Dto
        TargetSearchDto 业绩回补计算规则 = new TargetSearchDto();
        业绩回补计算规则.setCashId(cashId);
        业绩回补计算规则.setCashType2(cashType2);
        业绩回补计算规则.setEffdateStart(vdate);
        业绩回补计算规则.setEffdateEnd(happenDate);
//        业绩回补计算规则.setFinprodType(finprodType);
        业绩回补计算规则.setBranch(branch);
        业绩回补计算规则.setQueryType(ValDictCodeList.标的_业绩回补计算规则.getValue());
        queryCriteria.add(业绩回补计算规则);

        // 查询标的信息
        Map<TargetSearchDto, Object> targetInfoMap = finProductCommonService.getTargetInfo(finprodId, queryCriteria);

        // 有返回值数据
        if (!MapUtils.isEmpty(targetInfoMap)) {
            returnMap.put(ValDictCodeList.标的_信息.getValue(), targetInfoMap.get(标的信息));
            returnMap.put(ValDictCodeList.标的_业绩报酬计提计划.getValue(), targetInfoMap.get(业绩报酬计提计划));
            returnMap.put(ValDictCodeList.标的_业绩报酬计提规则.getValue(), targetInfoMap.get(业绩报酬计提规则));
            returnMap.put(ValDictCodeList.标的_业绩回补计算规则.getValue(), targetInfoMap.get(业绩回补计算规则));
        }

        return returnMap;
    }

    /**
     * 凭证最新账务查询结果按照发生日升序排列
     * 
     * @param list 凭证最新账务查询结果
     */
    private void sortByHappenDate(List<BookLatestInfoDto> list) {
        if (StringUtil.isEmptyList(list)) {
            return;
        }
        Collections.sort(list, new Comparator<BookLatestInfoDto>() {

            @Override
            public int compare(BookLatestInfoDto o1, BookLatestInfoDto o2) {
                return o1.getHappenDate().compareTo(o2.getHappenDate());
            }

        });
    }
}
