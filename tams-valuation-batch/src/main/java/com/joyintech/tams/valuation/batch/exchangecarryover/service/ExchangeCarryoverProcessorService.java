package com.joyintech.tams.valuation.batch.exchangecarryover.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.EncryptUtil;
import com.joyintech.tams.common.base.entity.BokBussDetsub;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.exchangecarryover.dao.ExchangeCarryoverMapper;
import com.joyintech.tams.valuation.batch.exchangecarryover.dto.CcyBalanceEntity;
import com.joyintech.tams.valuation.batch.exchangecarryover.dto.ExchangeCarryoverEntity;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingSubject;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.service.FinProductCommonBatchService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 汇兑损益结转
 *
 * @author zhangwei
 * @createTime 2019-07-11 13:43
 */
@Service
public class ExchangeCarryoverProcessorService {

    /**
     * 账务类共通
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 计算类共通
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    @Autowired
    BookAccountGenLogic bookAccountGenLogic;

    @Autowired
    private DBUtil dBUtil;

    /**
     * 汇兑损益结转 共通 mapper
     */
    @Autowired
    private ExchangeCarryoverMapper exchangeCarryoverMapper;

    @Autowired
    private FinProductCommonBatchService finProductCommonBatchService;

    @SuppressWarnings("unchecked")
    public BookGenerateResult execute(Map<String, Object> params) {
        // 此 process 返回值
        BookGenerateResult returnResult = new BookGenerateResult();

        // 获取 reader 方法的返回值
        ExchangeCarryoverEntity acctEntity = (ExchangeCarryoverEntity)params.get(
                GlobalConstant.READER_RESULT);

        SysBatchTask sysBatchTask = (SysBatchTask) params.get("batchTask");

        // 若reader传递的参数为 null，不进行后续处理
        if (acctEntity == null) {
            return null;
        }

        // 1.获取参数
        Map<String, SubjectBalance> mapExchange = Maps.newHashMap();
        // 定义 变量.汇兑损益科目集合Map Map<String,SubjectBalance>
        Map<String, List<ProfitAndLossAccountingSubject>> mapSubject = Maps.newHashMap();

        /** add by yanya on 20200219 增加期权汇总损益 start**/
        //定义 变量.累计公允价值变动本币更新Map	Map<String,SubjectBalance>	key:账务明细代码+原币  value:科目余额
        Map<String,ValBokBookdetailBatch> 累计公允价值变动本币更新Map = new HashMap<>();
        //定义 变量.当日统计分析账务明细汇总信息List	List<BokStatDetPosiiton>
        List<BokStatDetPosiiton> 当日统计分析账务明细汇总信息List = new ArrayList<>();
        //定义 全局静态变量 公允价值变动列表：买入期权-公允价值变动、卖出期权-公允价值变动
        List<String> 公允价值变动列表 = Arrays.asList(DictCodeBaseSubjectNatrue.买入期权公允价值变动.getValue(),
                DictCodeBaseSubjectNatrue.卖出期权公允价值变动.getValue());
        /** add by yanya on 20200219 增加期权汇总损益 end**/

        // 1.3 本期汇兑损益待结转科目余额
        // 定义 变量.基础科目性质List
        // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->基础科目性质查询SHEET
        List<String> listBaseSubjectNature = accountingCommonService.getValSubjectNatureList(
                "HDSY_KMCX");
        // 定义 变量.科目类型List
        List<String> listSubjectType = Lists.newArrayList();
        // 变量.科目类型List.add(数据字典：科目类型.资产类）
        listSubjectType.add(DictCodeList.科目类型_资产类.getValue());
        // 变量.科目类型List.add(数据字典：科目类型.负债类）
        listSubjectType.add(DictCodeList.科目类型_负债类.getValue());
        // 变量.科目类型List.add(数据字典：科目类型.资产负债共同类）
        listSubjectType.add(DictCodeList.科目类型_资产负债共同类.getValue());

        // 参照共通【科目余额查询】方法，参照【详细设计-账务类共通方法设计.xlsx的【科目余额查询】sheet】
        SubjectBalanceSearchDto searchCurrDto = new SubjectBalanceSearchDto();
        searchCurrDto.setBooksetId(acctEntity.getBooksetId());
        searchCurrDto.setHappenDate(acctEntity.getHappenDate());
        searchCurrDto.setBookDate(acctEntity.getBookDate());
        searchCurrDto.setSubjectTypeList(listSubjectType);
        searchCurrDto.setBaseSubjectNatureList(listBaseSubjectNature);
        searchCurrDto.setBranchHandleFlag("01");
        List<SubjectBalance> listCurrBalance = accountingCommonService.getSubjectLatestBalance(
                searchCurrDto);

        // 1.6 本期汇兑损益科目余额
        // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
        SubjectBalanceSearchDto searchExDto = new SubjectBalanceSearchDto();
        searchExDto.setHappenDate(acctEntity.getHappenDate());
        searchExDto.setBookDate(acctEntity.getBookDate());
        searchExDto.setBooksetId(acctEntity.getBooksetId());
        searchExDto.setBaseSubjectNatureList(Arrays.asList(DictCodeBaseSubjectNatrue.汇兑损益.getValue()));
        List<SubjectBalance> listCurrExBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchExDto, DictCodeList.借贷方向_贷.getValue());

        // LOOP 返回值 entity
        for (SubjectBalance item : listCurrExBalance) {
            // 定义 变量.科目余额List = 变量.汇兑损益科目余额Map.get(entity.账务明细代码+entity.原币)
            SubjectBalance subjectExBalance = mapExchange.get(
                    item.getBokDetailId() + item.getOCcy());
            if (subjectExBalance == null) {
                // 变量.汇兑损益科目余额Map.put(entity.账务明细代码+entity.原币,entity)
                mapExchange.put(item.getBokDetailId() + item.getOCcy(), item);
            }
        }

        // 1.7 汇兑损益科目集合
        Map<String, Object> exParam = Maps.newHashMap();
        exParam.put("booksetId", acctEntity.getBooksetId());
        exParam.put("listNature", Arrays.asList(DictCodeBaseSubjectNatrue.汇兑损益.getValue()));
        exParam.put("listSubjectType", Arrays.asList(DictCodeList.科目类型_损益类.getValue()));
        List<ProfitAndLossAccountingSubject> listExBalance = exchangeCarryoverMapper.getExBalance(
                exParam);

        // LOOP 查询结果 entity
        for (ProfitAndLossAccountingSubject item : listExBalance) {
            // 定义 变量.损益科目List = 变量.汇兑损益科目集合Map.get(entity.基础科目性质)
            List<ProfitAndLossAccountingSubject> list = mapSubject.get(
                    item.getBaseSubjectNature());
            // IF 变量.损益科目List IS NULL
            if (CollectionUtils.isEmpty(list)) {
                // 变量.损益科目List = new ArrayList<ProfitAndLossAccountingSubject>()
                list = Lists.newArrayList();
            }
            list.add(item);
            mapSubject.put(item.getBaseSubjectNature(), list);
        }

        // 2.未实现汇兑损益结转处理
        // 定义 变量.账务明细入集合 List<批处理用账务明细>
        List<ValBokBookdetailBatch> listBokDetail = Lists.newArrayList();
        // 定义 变量.币种余额Map
        Map<String, CcyBalanceEntity> mapCcyBalance = Maps.newHashMap();
        // 定义 变量.本期汇总损益Map_本位币 (未使用)

        // LOOP 循环 1-3.本期汇兑损益待结转科目余额，循环对象 损益entity
        for (SubjectBalance item : listCurrBalance) {
            // IF 损益entity.原币 eq 输入.汇兑损益数据.本位币 OR 损益entity.原币 IS NULL
            if (StringUtils.isEmpty(acctEntity.getStandardCcy())
                    || acctEntity.getStandardCcy().equals(item.getOCcy())) {
                continue;
            }
            // 2.1 结转日日终的本币余额计算
            // 定义 变量.余额MAP MAP(KEY:币种，Value：余额)
            // 变量.余额MAP = new HashMap<String,BigDecimal>()
            Map<String, BigDecimal> mapBalance = Maps.newHashMap();
            // 变量.余额MAP.put(损益entity.原币,损益entity.原币余额)
            mapBalance.put(item.getOCcy(), item.getBalance());
            // 调用共通设计->【详细设计-计算类共通方法设计.xlsx】->汇率折算SHEET (不可用)
            // 标的类共通方法设计 - 汇率查询
            List<CurrencyMarketDto> currencyMarket = finProductCommonBatchService.getCurrencyMarket(acctEntity.getBookDate(),
                    acctEntity.getBooksetId(), acctEntity.getStandardCcy(), item.getOCcy());
            // 定义 变量.结转汇兑损益余额
            BigDecimal exchangeCoAmt = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(currencyMarket)) {
                exchangeCoAmt = ArithmeticUtil.round(currencyMarket.get(0).getClosePrice().multiply(item.getBalance()), NumberEnum.MONEY);
            } else {
                exchangeCoAmt = item.getBalance();
            }
            // 定义 变量.结转汇兑损益余额 = DTO - 损益entity.本币余额
            exchangeCoAmt = exchangeCoAmt.subtract(item.getbAmt());

            // 2.2 待结转损益：生成账务明细数据
            ValBokBookdetailBatch bokDetail = new ValBokBookdetailBatch();
            // 账务明细.场景代码 = 会计场景代码_汇兑损益结转
            bokDetail.setTableId(DictCodeListTableId.会计场景代码_汇兑损益结转.getValue());
            // IF 变量.结转汇兑损益余额  == 0
            if (exchangeCoAmt.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }else if (exchangeCoAmt.compareTo(BigDecimal.ZERO) > 0) {
                // IF 变量.结转汇兑损益余额 > 0
                // 账务明细.借贷方向 = 损益entity.余额方向
                /** modify by yanya on 20200409 设计中取余额方向，此处变更**/
                //bokDetail.setCdFlag(item.getBalFlag());
                bokDetail.setCdFlag(item.getBalFlagHappenDate());
            }
            else {
                // 账务明细.借贷方向 = 损益entity.余额方向 取反
                /** modify by yanya on 20200409 设计中取余额方向，此处变更**/
                //String cdFlag = DictCodeList.借贷方向_借.getValue().equals(
                //        item.getBalFlag()) ? DictCodeList.借贷方向_贷.getValue() : DictCodeList.借贷方向_借.getValue();
                String cdFlag = DictCodeList.借贷方向_借.getValue().equals(
                        item.getBalFlagHappenDate()) ? DictCodeList.借贷方向_贷.getValue() : DictCodeList.借贷方向_借.getValue();
                bokDetail.setCdFlag(cdFlag);
            }
            // 账务明细.原币发生额 = 0
            bokDetail.setoHappenAmt(BigDecimal.ZERO);
            // 账务明细.原币 = 损益entity.原币
            bokDetail.setoCcy(item.getOCcy());
            // 账务明细.发生数量 = 0
            bokDetail.setHappenNumber(BigDecimal.ZERO);
            // 账务明细.本币发生额 = 变量.结转汇兑损益余额.abs()
            bokDetail.setbHappenAmt(exchangeCoAmt.abs());
            // 账务明细.本位币 = 损益entity.本位币
            bokDetail.setbCcy(item.getbCcy());
            // 账务明细.业务明细代码 = 损益entity.业务明细代码
            bokDetail.setBusiId(item.getBusiId());
            // 账务明细.基础科目性质 = 损益entity.基础科目性质
            bokDetail.setBaseSubjectNature(item.getBaseSubjectNature());
            // 账务明细.账务明细代码 = 损益entity.账务明细代码
            bokDetail.setBokDetailId(item.getBokDetailId());
            // 账务明细.分支序号 = 损益entity.分支序号 (没有)
            bokDetail.setBranch(item.getBranch()==null?0L:item.getBranch());
            // 账务明细.科目号 = 损益entity.科目号
            bokDetail.setSubjectNo(item.getSubjectNo());
            // 账务明细.四级科目号 = 损益entity.四级科目号
            bokDetail.setDetailSubjectNo(item.getDetailSubjectNo());

            // 调用共通处理 余额方向处理_发生额加余额
            Map<String, Object> mapBalDirectAndBal = accountingCommonService.balanceDirectDealAccrualAddBalance(
                    item.getBalFlagHappenDate(), item.getBalFlagHappenDate(), item.getBalance(), item.getNum(),
                    bokDetail.getCdFlag(), BigDecimal.ZERO, BigDecimal.ZERO, item.getbAmt(), bokDetail.getbHappenAmt());
            // 账务明细.原币余额（发生日） = 返回值.科目余额.
            bokDetail.setoAmtHappenDate((BigDecimal)mapBalDirectAndBal.get("balance"));
            // 账务明细.本币余额（发生日） = 返回值.本币科目余额.
            bokDetail.setbAmtHappenDate((BigDecimal)mapBalDirectAndBal.get("benbalance"));
            // 账务明细. 数量余额（发生日） = 返回值.数量.
            bokDetail.setNumAmtHappenDate((BigDecimal)mapBalDirectAndBal.get("num"));
            // 账务明细.余额方向（发生日） = 返回值.余额方向.
            bokDetail.setBalFlagHappenDate((String)mapBalDirectAndBal.get("balDirect"));
            //账务明细.本币余额（发生日） = 返回值.本币科目余额.
            bokDetail.setBookRemark("日终汇兑损益");
            // 变量.账务明细入集合.add(账务明细)
            listBokDetail.add(bokDetail);

            /** add by yanya on 20200219 增加期权汇总损益 start**/
            //IF 账务明细.基础科目性质 in (全局静态常量.公允价值变动列表)
            if(公允价值变动列表.contains(bokDetail.getBaseSubjectNature())) {
                //变量.累计公允价值变动本币更新Map.put(账务明细.账务明细代码+账务明细.原币,账务明细)
                累计公允价值变动本币更新Map.put(bokDetail.getBokDetailId() + bokDetail.getoCcy(), bokDetail);
            }
            /** add by yanya on 20200219 增加期权汇总损益 end**/

            // 2.3 汇兑损益日终结转发生额
            // 定义 变量.KEY = 输入.汇兑损益数据.账务明细代码
            String ccyBalKey = acctEntity.getBokDetailId();
            // 变量.币种余额Dto = 变量.币种余额Map.get(变量.KEY)
            CcyBalanceEntity ccyBalanceEntity = mapCcyBalance.get(ccyBalKey);
            // IF 变量.币种余额Dto IS NULL
            if (ccyBalanceEntity == null) {
                ccyBalanceEntity = new CcyBalanceEntity();
                // 变量.币种余额Dto.未实现汇兑损益余额 = 0
                ccyBalanceEntity.setUnRealBalance(BigDecimal.ZERO);
                // 变量.币种余额Dto.未实现汇兑损益_余额方向 = 账务明细.借贷方向
                ccyBalanceEntity.setUnRealBalFlag(bokDetail.getCdFlag());
            }
            // IF 变量.币种余额Dto.未实现汇兑损益_余额方向 = 账务明细.借贷方向
            if (ccyBalanceEntity.getUnRealBalFlag().equals(bokDetail.getCdFlag())) {
                // 变量.币种余额Dto.未实现汇兑损益余额 += 账务明细.本币发生额
                ccyBalanceEntity.setUnRealBalance(
                        ccyBalanceEntity.getUnRealBalance().add(bokDetail.getbHappenAmt()));
            }
            else {
                // 变量.币种余额Dto.未实现汇兑损益余额 -= 账务明细.本币发生额
                ccyBalanceEntity.setUnRealBalance(
                        ccyBalanceEntity.getUnRealBalance().subtract(bokDetail.getbHappenAmt()));
            }
            // 变量.币种余额Map.put(变量.KEY,币种余额Dto)
            mapCcyBalance.put(ccyBalKey, ccyBalanceEntity);
        }

        // 3.未实现汇兑损益结转生成账务明细数据
        // LOOP 变量.币种余额Map.keySet() 余额key
        BokBussDetsub detsub = new BokBussDetsub();
        for (String key : mapCcyBalance.keySet()) {
            // 汇兑损益科目：生成账务明细数据
            // 定义 变量.币种余额ENTITY = 变量.币种余额Map.get(余额key)
            ValBokBookdetailBatch bokDetail = new ValBokBookdetailBatch();
            CcyBalanceEntity ccyBalanceEntity = mapCcyBalance.get(key);
            // 账务明细.场景代码 = 会计场景代码_汇兑损益结转
            bokDetail.setTableId(DictCodeListTableId.会计场景代码_汇兑损益结转.getValue());
            // IF 变量.币种余额ENTITY.未实现汇兑损益余额 == 0
            if (ccyBalanceEntity.getUnRealBalance().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            } else if (ccyBalanceEntity.getUnRealBalance().compareTo(BigDecimal.ZERO) > 0) {
                // IF 变量.币种余额ENTITY.未实现汇兑损益余额 > 0
                // 账务明细.借贷方向 = 变量.币种余额ENTITY.未实现汇兑损益_余额方向 取反
                String cdFlag = DictCodeList.借贷方向_借.getValue().equals(
                        ccyBalanceEntity.getUnRealBalFlag()) ? DictCodeList.借贷方向_贷.getValue() : DictCodeList.借贷方向_借.getValue();
                bokDetail.setCdFlag(cdFlag);
            }
            else {
                // 账务明细.借贷方向 = 变量.币种余额ENTITY.未实现汇兑损益_余额方向
                bokDetail.setCdFlag(ccyBalanceEntity.getUnRealBalFlag());
            }
            // 账务明细.原币发生额 = 0
            bokDetail.setoHappenAmt(BigDecimal.ZERO);
            // 账务明细.原币 = 输入.汇兑损益数据.本位币
            bokDetail.setoCcy(acctEntity.getStandardCcy());
            // 账务明细.本币 = 输入.汇兑损益数据.本位币
            bokDetail.setbCcy(acctEntity.getStandardCcy());
            // 账务明细.本币发生额 = 变量.币种余额ENTITY.未实现汇兑损益余额.abs()
            bokDetail.setbHappenAmt(ccyBalanceEntity.getUnRealBalance().abs());
            // 账务明细.发生数量 = 0
            bokDetail.setHappenNumber(BigDecimal.ZERO);
            // 账务明细.业务明细代码 = 输入.汇兑损益数据.核算主体代码
            bokDetail.setBusiId(acctEntity.getAcctSubjectId());
            // 账务明细.基础科目性质 = “汇兑损益”
            bokDetail.setBaseSubjectNature(DictCodeBaseSubjectNatrue.汇兑损益.getValue());
            // 账务明细.账务明细代码 = 输入.汇兑损益数据.账务明细代码
            bokDetail.setBokDetailId(acctEntity.getBokDetailId());
            // 账务明细.分支序号 = 0
            bokDetail.setBranch(0L);
            // 账务明细.科目号 = 变量.汇兑损益科目集合Map.get(“汇兑损益”).get(0)
            bokDetail.setSubjectNo(
                    mapSubject.get(DictCodeBaseSubjectNatrue.汇兑损益.getValue()).get(0).getSubjectNo());
            // 调用共通处理生成业务明细科目,参数:科目号,科目名称,账务明细代码 .
            String detailSubjectNo = bokDetail.getDetailSubjectNo();
            // 调用共通处理生成业务明细科目,参数:科目号,科目名称,账务明细代码
            // 账务明细.四级科目号 = 业务明细科目.四级科目号
            if (StringUtils.isEmpty(detailSubjectNo)) {
                detsub = bookAccountGenLogic.genBokBussDetsub(bokDetail);
                if (detsub != null) {
                    bokDetail.setDetailSubjectNo(detsub.getDetailSubjectNo());
                }
            }
            else {
                bokDetail.setDetailSubjectNo(detailSubjectNo);
            }
            // 业务明细科目入集合List<明细科目>(不重复录入) - writer 共通 处理
            // 定义 变量.本期汇兑损益科目余额DTO = 变量.汇兑损益科目余额Map.get(账务明细.账务明细代码+账务明细.原币)
            SubjectBalance currExchangeBalDto = mapExchange.get(
                    bokDetail.getBokDetailId() + bokDetail.getoCcy());
            // IF 变量.本期汇兑损益科目余额DTO IS NULL
            if (currExchangeBalDto == null) {
                currExchangeBalDto = new SubjectBalance();
                // 变量.本期汇兑损益科目余额DTO.原币余额 = 0
                currExchangeBalDto.setBalance(BigDecimal.ZERO);
                // 变量.本期汇兑损益科目余额DTO.余额方向（发生日） = “贷方”
                currExchangeBalDto.setBalFlagHappenDate(DictCodeList.借贷方向_贷.getValue());
                // 变量.本期汇兑损益科目余额DTO.数量 = 0
                currExchangeBalDto.setNum(BigDecimal.ZERO);
                // 变量.本期汇兑损益科目余额DTO.借贷方向 = “贷方”
                currExchangeBalDto.setBalFlag(DictCodeList.借贷方向_贷.getValue());
            }

            // 调用共通处理 余额方向处理_发生额加余额
            Map<String, Object> mapBalDirectAndBal = accountingCommonService.balanceDirectDealAccrualAddBalance(
                    currExchangeBalDto.getBalFlagHappenDate(), currExchangeBalDto.getBalFlagHappenDate(),
                    currExchangeBalDto.getBalance(), currExchangeBalDto.getNum(),
                    bokDetail.getCdFlag(), BigDecimal.ZERO, BigDecimal.ZERO, currExchangeBalDto.getbAmt(), bokDetail.getbHappenAmt());
            // 账务明细.原币余额（发生日） = 返回值.科目余额.
            bokDetail.setoAmtHappenDate((BigDecimal)mapBalDirectAndBal.get("balance"));
            // 账务明细.本币余额（发生日） = 返回值.本币科目余额.
            bokDetail.setbAmtHappenDate((BigDecimal)mapBalDirectAndBal.get("benbalance"));
            // 账务明细. 数量余额（发生日） = 返回值.数量.
            bokDetail.setNumAmtHappenDate((BigDecimal)mapBalDirectAndBal.get("num"));
            // 账务明细.余额方向（发生日） = 返回值.余额方向.
            bokDetail.setBalFlagHappenDate((String)mapBalDirectAndBal.get("balDirect"));//
            // 账务明细.本币余额（发生日） = 返回值.本币科目余额.
            bokDetail.setBookRemark("日终汇兑损益");
            // 变量.账务明细入集合.add(账务明细)
            listBokDetail.add(bokDetail);

        }

        // 5.账务明细共通字段赋值
        // LOOP 变量.账务明细集合 ENTITY
        for (ValBokBookdetailBatch item : listBokDetail) {
            // 流水号
            item.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
            // 处理序列 <- 自动生成的序列号20位
            item.setBookOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));
            // 账套代码 汇兑损益数据.账套代码
            item.setBooksetId(acctEntity.getBooksetId());
            // 任务代码 汇兑损益数据.任务代码
            item.setTaskId(acctEntity.getTaskId());
            // 任务优先级 汇兑损益数据.任务优先级
            item.setTaskPri(sysBatchTask.getTaskPri());
            // 发生日期 read处理结果.发生日期
            item.setHappenDate(acctEntity.getHappenDate());
            // 入账日期 read处理结果.入账日期
            item.setBookDate(acctEntity.getBookDate());
            // 账套日期 read处理结果.账套日期
            item.setBooksetDate(acctEntity.getBooksetDate());
            // 凭证编号 (场景代码 + 发生日 + "汇兑损益结转" + 冲销标识)md5加密处理
            String bookId = item.getBooksetId() + item.getTableId() + item.getHappenDate()
                    + BookConstant.DEF_BOK_DETAIL_ID + item.getOffsetFlag();
            bookId = EncryptUtil.getInstance().md5(bookId);
            // 凭证说明 会计场景代码对应名称
            item.setBookId(bookId);
            // 冲正标识 正常交易
            item.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
            // 凭证类型 "日终"
            item.setBookType(DictCodeList.凭证类型_日终.getValue());
            // 本位币余额（发生日） 参考process处理 （未处理）
        }

        returnResult.setValBokBookdetailBatch(listBokDetail);
        /** add by yanya on 20200219 start**/
        //6.累计公允价值变动本币更新
        /**
        调用方法【累计公允价值变动本币更新】链接
            参数名：					参数值
            累计公允价值变动本币更新Map					变量.累计公允价值变动本币更新Map
        返回值：					DTO(List<BokStatDetPosiiton>)
         **/
        List<BokStatDetPosiiton> bokStatDetPosiitonList = this.getBokStatDetPosiiton(累计公允价值变动本币更新Map);
        //IF DTO IS NOT NULL AND DTO.SIZE>0
        if(bokStatDetPosiitonList.size() > 0) {
            //变量.当日统计分析账务明细汇总信息List.addAll(DTO)
            returnResult.getValBokStatDetPosiitonBatch().addAll(bokStatDetPosiitonList);

        }
        /** add by yanya on 20200219 end**/
        // 7.返回变量.账务明细入集合
        return returnResult;
    }


    /**
     * 累计公允价值变动本币更新
     * add by yanya on 20200219
     * @param subjectBalanceMap
     * @return
     */
    private List<BokStatDetPosiiton> getBokStatDetPosiiton(Map<String,ValBokBookdetailBatch> bokDetailMap) {
        //1.定义变量
        //定义 变量.当日统计分析账务明细汇总信息List
        List<BokStatDetPosiiton> bokStatDetPosiitonList = new ArrayList<>();
        //2.处理详情
        //LOOP 输入.累计公允价值变动本币更新Map.keySet() KEY
        for(String key : bokDetailMap.keySet()) {
            //定义 变量.账务明细DTO = 输入.累计公允价值变动本币更新Map.get(KEY)
            ValBokBookdetailBatch bokDetail = bokDetailMap.get(key);
            /**调用共通
            参数名：					统计分析账务明细汇总查询
                查询开始日					变量.账务明细DTO.发生日
                查询结束日					变量.账务明细DTO.发生日
                账套代码					变量.账务明细DTO.账套代码
                账务明细代码					变量.账务明细DTO.账务明细代码
                入账日					变量.账务明细DTO.入账日
                返回值：					统计分析账务明细汇总信息
             **/
            //定义 变量.当日统计分析账务明细汇总信息 = 上述返回值
            // 变量.统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
            BokStatDetPosiiton bokSummaryTable = accountingCommonService.getCountAnalysis(
                    bokDetail.getHappenDate(), bokDetail.getHappenDate(), bokDetail.getBookDate(),
                    bokDetail.getBooksetId(), bokDetail.getBokDetailId(), null, null);

            if(bokSummaryTable == null) {
                continue;
            }
            //定义 变量.余额方向
            String 余额方向 = null;
            //IF "买入期权-公允价值变动" eq 变量.账务明细DTO.基础科目性质
            if(DictCodeBaseSubjectNatrue.买入期权公允价值变动.getValue().equals(bokDetail.getBaseSubjectNature())) {
                //变量.余额方向 = "借方"
                余额方向 = DictCodeList.余额方向_借方.getValue();
            }
            //ELSE IF "卖出期权-公允价值变动" eq 变量.账务明细DTO.基础科目性质
            else if(DictCodeBaseSubjectNatrue.卖出期权公允价值变动.getValue().equals(bokDetail.getBaseSubjectNature())) {
                //变量.余额方向 = "贷方"
                余额方向 = DictCodeList.余额方向_贷方.getValue();
            }
            // 变量.余额方向 IS NULL
            if(余额方向 == null) {
                continue;
            }
            //IF 变量.余额方向 eq 变量.账务明细DTO.借贷方向
            if(余额方向.equals(bokDetail.getCdFlag())) {
                //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动_本位币 += 变量.账务明细DTO.本币发生额
                bokSummaryTable.setTdyFloatIngplExp(
                        ArithmeticUtil.doAdd(bokSummaryTable.getTdyFloatIngplExp(),bokDetail.getbHappenAmt()));
            }
            else {
                //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动_本位币 -=	变量.账务明细DTO.本币发生额
                bokSummaryTable.setTdyFloatIngplExp(
                        ArithmeticUtil.doSub(bokSummaryTable.getTdyFloatIngplExp(),bokDetail.getbHappenAmt()));
            }
            //变量.当日统计分析账务明细汇总信息List.add(变量.当日统计分析账务明细汇总信息)
            bokStatDetPosiitonList.add(bokSummaryTable);
        }
        return bokStatDetPosiitonList;
    }
}
