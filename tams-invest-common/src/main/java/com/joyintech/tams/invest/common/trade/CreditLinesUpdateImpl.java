package com.joyintech.tams.invest.common.trade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.trade.dao.CreditLinesUpdateMapper;
import com.joyintech.tams.invest.common.trade.dto.CreditComInfoOneStepDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesOutputDto;

@Component
@Scope("prototype")
public class CreditLinesUpdateImpl extends CreditLinesUpdateAbs {

    @Autowired
    private CreditLinesUpdateMapper creditLinesDealsMapper;
    @Autowired
    private CreditLinesMapper creditLinesMapper;
    @Autowired
    private CreditUsingSeriesMapper creditUsingSeriesMapper;
    @Autowired
    private CreditUsingAstMapper creditUsingAstMapper;
    @Autowired
    private CreditUsingCustomerMapper creditUsingCustomerMapper;
    @Autowired
    private CreditUsingCustomerMstMapper creditUsingCustomerMstMapper;
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;
    @Autowired
    private FinProductMapper finProductMapper;
    @Autowired
    private FinProductAddMapper finProductAddMapper;
    @Autowired
    private MstCounterPartyMapper mstCounterPartyMapper;
    @Autowired
    private IPlatformDate iPlatformDate;

    public CreditLinesUpdateImpl(){
        // 初始化授信数据仓库
        this.creditLinesMap = new ConcurrentHashMap<>();
        this.creditUsingAstList = new ArrayList<>();
        this.creditUsingSeriesList = new ArrayList<>();
        this.creditUsingCustomerList = new ArrayList<>();
        this.creditUsingCustomerMstList = new ArrayList<>();
        this.creditLinesOutputDtoList = new ArrayList<>();
        this.CREDIT_SOFT_SKIP_MODE = CacheUtil.getSysParam(BussinessConstant.CREDIT_SOFT_SKIP_MODE);
        this.RELEASE_MODE = CacheUtil.getSysParam(BussinessConstant.RELEASE_MODE);
    }

    private void releaseBasicData(){
        occupy = true;
        creditLinesAst = null;
        oneStepInfo = null;
        creditLinesOrg = null;
        creditLinesOrgItem = null;
        compareAmtAst = null;
        compareAmtOrg = null;
        releaseAmtAst = null;
        releaseAmtOrg = null;
    }

    /**
     * 处理基础数据（资产授信数据，额度校验规则、机构授信数据）
     * @param inputDto
     */
    private void findBasicData(CreditLinesInputDto inputDto){
        // 数据重置
        this.releaseBasicData();

        if (StringUtil.equals(inputDto.getTradeStatus(), DictCodeListExt1.交易状态_撤销.getValue()) && StringUtil.isNotEmpty(inputDto.getTradeId())){
            // 如果是交易撤销，交易编号可能会有后缀“R”
            if (inputDto.getTradeId().endsWith("R")){
                inputDto.setTradeId(inputDto.getTradeId().substring(0,inputDto.getTradeId().length() - 1));
            }
            List<CreditUsingSeries> 系列额度占用明细 = creditLinesDealsMapper.findCreditUsingSeries(inputDto);
            List<CreditUsingAst> 资产额度占用明细 = creditLinesDealsMapper.getCreditUsingAstList(inputDto);
            List<CreditUsingCustomer> 机构额度占用明细 = creditLinesDealsMapper.getCreditUsingCustomerList(inputDto);
            List<CreditUsingCustomerMst> 机构分项占用明细 = creditLinesDealsMapper.getCreditUsingCustomerMsts(inputDto);
            // 资产授信
            if(!StringUtil.isEmptyList(系列额度占用明细)) {
                系列额度占用明细.forEach(item -> {
                    creditLinesAst = creditLinesMapper.selectByPrimaryKey(item.getCreditLinesId());
                    inputDto.setFinprodId(item.getFinprodId());
                    occupy = item.getAmount().compareTo(BigDecimal.ZERO) > 0;
                });
            }
            if(!StringUtil.isEmptyList(资产额度占用明细)) {
                资产额度占用明细.forEach(item -> {
                    creditLinesAst = creditLinesMapper.selectByPrimaryKey(item.getCreditLinesId());
                    inputDto.setFinprodId(item.getFinprodId());
                    occupy = item.getAmount().compareTo(BigDecimal.ZERO) > 0;
                });
            }
            if(!StringUtil.isEmptyList(机构额度占用明细)) {
                机构额度占用明细.forEach(item -> {
                    creditLinesOrg = creditLinesMapper.selectByPrimaryKey(item.getCreditLinesId());
                    inputDto.setFinprodId(item.getFinprodId());
                    if (StringUtil.isEmpty(inputDto.getCounterId())){
                        inputDto.setCounterId(item.getCustomerId());
                    }
                    occupy = item.getAmount().compareTo(BigDecimal.ZERO) > 0;
                });
            }
            if(!StringUtil.isEmptyList(机构分项占用明细)) {
                机构分项占用明细.forEach(item -> {
                    creditLinesOrgItem = creditLinesMapper.selectByPrimaryKey(item.getCreditLinesId());
                    inputDto.setFinprodId(item.getFinprodId());
                    if (StringUtil.isEmpty(inputDto.getCounterId())){
                        inputDto.setCounterId(item.getCustomerId());
                    }
                    occupy = item.getAmount().compareTo(BigDecimal.ZERO) > 0;
                });
            }
            inputDto.setTradeDate(iPlatformDate.getSysDate());
        }else {
            // 是否是占用
            occupy = isOccupy(inputDto);
        }
        // 资产授信
        if (StringUtil.isNotEmpty(inputDto.getFinprodId()) && inputDto.getTradeDate() != null){
            creditLinesAst = getAssetsCeLineList(inputDto);
        }
        // 额度校验规则
        // STEP 2 查询额度校验规则，获取授信标签信息并检查主要信息是否符合要求
        List<CreditComInfoOneStepDto> oneStepList = getCreditInfoOneStep(inputDto);
        if(oneStepList.size() != 1){
            logger.info(getMessage("==========处理数据结束,交易或指令编号:"+ inputDto.getFinprodId() + ",获取额度校验规则失败=========="));
            return;
        }

        oneStepInfo = oneStepList.get(0);

        if(StringUtil.isNotEmpty(inputDto.getZhiYaQuan())){
            if(StringUtil.equals(oneStepInfo.getDeductObjType(),DictCodeListExt3.扣减对象_交易对手.getValue())){
                inputDto.setFinprodId(inputDto.getZhiYaQuan());
                List<CreditComInfoOneStepDto> List = getCreditInfoOneStep(inputDto);
                if(List.size() != 1){
                    logger.info(getMessage("==========处理数据结束,交易或指令编号:"+ inputDto.getFinprodId() + ",获取额度校验规则失败=========="));
                    return;
                }
                oneStepInfo = List.get(0);
                oneStepInfo.setDeductObjType(DictCodeListExt3.扣减对象_发行人.getValue());
            }else {
                return;
            }
        }

        if(StringUtil.equals(DictCodeListRsk.额度分类_不扣限额.getValue(),oneStepInfo.getCreditType())){
            // 为0或者无授信返回成功结果
            logger.info(getMessage("==========处理数据结束,交易或指令编号:"+ inputDto.getFinprodId() + ",不扣限额=========="));
            return;
        }
        // 转换扣减对象为实际值
        convertCreditObj(oneStepInfo,inputDto);
        if (oneStepInfo.getDeductObj() != null){
            // STEP 3 查询机构授信
            getOrgCreditLines(inputDto,oneStepInfo);
        }
    }

    /**
     * 授信额度占用
     * @param inputDto
     * @param occupyType
     * @param 剩余占用额度
     * @param outputDto
     */
    private void creditLinesOccupy(CreditLinesInputDto inputDto, String occupyType, CreditLinesOutputDto outputDto){
        // 机构禁投校验
        if (checkOrgCreditForbidden(inputDto,creditLinesOrg,outputDto)){
            return;
        }
        // 资产禁投校验
        boolean forbidden = checkAssetCreditForbidden(inputDto,creditLinesAst,outputDto);
        // forbidden === true ,禁止投资该资产，进入下一循环
        if (forbidden){
            return;
        }
        // 债券一级投标的汇总确认和中标结果维护需要更新指令的预占额度
        if (StringUtil.equals(DictCodeListRsk.授信占用类别_实占.getValue(),inputDto.getCreditCallType())
                && StringUtil.equals(inputDto.getFinprodType2(),DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())){
            updateCreditOccupy(inputDto,compareAmtOrg);
            return;
        }
        // 针对一级指令编辑特殊处理
        if (StringUtil.equals(DictCodeListRsk.授信占用类别_预占.getValue(),inputDto.getCreditCallType())
                && StringUtil.equals(inputDto.getTradeStatus(), DictCodeListExt1.交易状态_初始.getValue())
                && StringUtil.equals(inputDto.getFinprodType2(),DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())){
//            revokeCreditOccupyF32(inputDto);
            occupyType=DictCodeListRsk.授信占用类别_预占.getValue();
        }
        // 资产授信禁投校验
        if (creditLinesAst != null){
            // 释放前置交易额度-资产授信
            if (StringUtil.isNotEmpty(inputDto.getEntrustId()) && !StringUtil.equals(inputDto.getFinprodType2(),DictCodeListExt2.共通业务类型_可转债申购交易.getValue())){
                CreditLinesInputDto creditLinesInputDto = new CreditLinesInputDto();
                BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto,inputDto);
                creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                releaseCreditLinesPre(creditLinesInputDto,compareAmtAst,DictCodeListRsk.授信占用方式_资产授信.getValue());
            }
            BigDecimal 剩余占用额度 = dealCreditOccupy(inputDto,creditLinesAst,occupyType,compareAmtAst);
            // 资产授信额度不够时，根据是否软控提示信息
            if (剩余占用额度.compareTo(BigDecimal.ZERO) > 0){
                if (StringUtil.equals(CREDIT_SOFT_SKIP_MODE,DictCodeList.是否_否.getValue())){
                    outputDto.setErrMessage(getMessage("BUSS_COM_CREDIT_TZJY_12"));
                }else if (StringUtil.equals(CREDIT_SOFT_SKIP_MODE,DictCodeList.是否_是.getValue())){
                    outputDto.getConfirmMessages().add(getMessage("BUSS_COM_CREDIT_TZJY_14"));
                }
            }
        }
        //如果存在资产授信，并且资产授信配置是否占用主题授信为否，返回不继续占用机构授信
        if(creditLinesAst!=null && "N".equals(creditLinesAst.getCreditFlag())){
            return;
        }
        //机构授信配置不扣限额，临时处理方式：交易通过,不扣机构授信额度
        if(oneStepInfo != null && StringUtil.equals(DictCodeListRsk.额度分类_不扣限额.getValue(),oneStepInfo.getCreditType())){
            logger.info(getMessage("==========处理数据结束,交易或指令编号:"+ inputDto.getFinprodId() + ",不扣限额=========="));
            return;
        }
        // 只校验分项授信（非法人主体额度）
        if (oneStepInfo != null && StringUtil.equals(oneStepInfo.getCreditType(),DictCodeListRsk.额度分类_非法人机构.getValue())){
            // 非法人主体额度 校验
            if (creditLinesOrgItem == null){
                outputDto.setTradeId(inputDto.getFinprodId());
                if (StringUtil.equals(CREDIT_SOFT_SKIP_MODE,DictCodeList.是否_否.getValue())){
                    outputDto.setErrMessage(getMessage("BUSS_COM_CREDIT_TZJY_03"));
                }else {
                    outputDto.getConfirmMessages().add(getMessage("BUSS_COM_CREDIT_TZJY_05"));
                }
                return;
            }
            // 释放前置交易额度-机构授信
            if (StringUtil.isNotEmpty(inputDto.getEntrustId()) && !StringUtil.equals(inputDto.getFinprodType2(),DictCodeListExt2.共通业务类型_可转债申购交易.getValue())){
                CreditLinesInputDto creditLinesInputDto = new CreditLinesInputDto();
                BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto,inputDto);
                creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                releaseCreditLinesPre(creditLinesInputDto,compareAmtOrg,DictCodeListRsk.授信占用方式_机构授信.getValue());
            }
            BigDecimal 剩余占用额度 = dealCreditOccupy(inputDto,creditLinesOrgItem,occupyType,compareAmtOrg);
            // 非法人主体授信额度扣减之后剩余待扣减或释放额度
            if (剩余占用额度.compareTo(BigDecimal.ZERO) > 0){
                outputDto.setTradeId(inputDto.getFinprodId());
                if (StringUtil.equals(CREDIT_SOFT_SKIP_MODE,DictCodeList.是否_否.getValue())){
                    outputDto.setErrMessage(getMessage("BUSS_COM_CREDIT_TZJY_11"));
                }else if (StringUtil.equals(CREDIT_SOFT_SKIP_MODE,DictCodeList.是否_是.getValue())){
                    outputDto.getConfirmMessages().add(getMessage("BUSS_COM_CREDIT_TZJY_13"));
                }
            }
        }else {
            // 主体授信和主体向下的分项授信同时校验
            if (creditLinesOrg == null || creditLinesOrgItem == null){
                outputDto.setTradeId(inputDto.getFinprodId());
                if (StringUtil.equals(CREDIT_SOFT_SKIP_MODE,DictCodeList.是否_否.getValue())){
                    outputDto.setErrMessage(getMessage("BUSS_COM_CREDIT_TZJY_15"));
                }else {
                    outputDto.getConfirmMessages().add(getMessage("BUSS_COM_CREDIT_TZJY_16"));
                }
                return;
            }
            // 记录机构授信占用额度
            outputDto.setCustomerId(creditLinesOrg.getCustomerId());
            outputDto.setOccupyAmtOrg(compareAmtOrg);
            // 释放前置交易额度-机构授信
            if (StringUtil.isNotEmpty(inputDto.getEntrustId()) && !StringUtil.equals(inputDto.getFinprodType2(),DictCodeListExt2.共通业务类型_可转债申购交易.getValue())){
                CreditLinesInputDto creditLinesInputDto = new CreditLinesInputDto();
                BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto,inputDto);
                creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                releaseCreditLinesPre(creditLinesInputDto,compareAmtOrg,DictCodeListRsk.授信占用方式_机构授信.getValue());
            }
            // 主体授信
            BigDecimal 剩余占用额度1 = dealCreditOccupy(inputDto,creditLinesOrg,occupyType,compareAmtOrg);
            // 主体分项授信
            BigDecimal 剩余占用额度2 = dealCreditOccupy(inputDto,creditLinesOrgItem,occupyType,compareAmtOrg);

            if (剩余占用额度1.compareTo(BigDecimal.ZERO) > 0 || 剩余占用额度2.compareTo(BigDecimal.ZERO) > 0){
                outputDto.setTradeId(inputDto.getFinprodId());
                if (StringUtil.equals(CREDIT_SOFT_SKIP_MODE,DictCodeList.是否_否.getValue())){
                    outputDto.setErrMessage(getMessage("BUSS_COM_CREDIT_TZJY_17"));
                }else if (StringUtil.equals(CREDIT_SOFT_SKIP_MODE,DictCodeList.是否_是.getValue())){
                    if(StringUtil.isNotEmpty(inputDto.getZhiYaQuan())){
                        outputDto.getConfirmMessages().add(getMessage("BUSS_COM_CREDIT_TZJY_19"));
                    }else {
                        outputDto.getConfirmMessages().add(getMessage("BUSS_COM_CREDIT_TZJY_18"));
                    }
                }
            }
        }
    }

    private boolean checkOrgCreditForbidden(CreditLinesInputDto inputDto, CreditLines creditLinesOrg, CreditLinesOutputDto outputDto) {
        if (creditLinesOrg == null ){
            return false;
        }
        // 当是否持有至到期日 = 是，不允许进行买入交易
        if (StringUtil.equals(DictCodeList.是否_是.getValue(),creditLinesOrg.getHoldMaturity())){
            outputDto.setTradeId(inputDto.getTradeId());
            outputDto.setErrMessage("该主体为持有至到期主体，禁止买入方向交易");
            return true;
        }
        return false;
    }

    private BigDecimal findUsedAmt(CreditLinesInputDto inputDto){
        BigDecimal 机构已占用额度 = BigDecimal.ZERO;
        Map<String,Object> maps = new HashMap<>();
        maps.put("finprodId",inputDto.getFinprodId());
        if (creditLinesOrgItem != null){
            maps.put("customerId",creditLinesOrgItem.getCustomerId());
            maps.put("creditMstId",creditLinesOrgItem.getCreditMstId());
            机构已占用额度 = creditLinesDealsMapper.findUsedAmtFromCustMst(maps);
        }else if (creditLinesOrg != null){
            maps.put("customerId",creditLinesOrg.getCustomerId());
            机构已占用额度 = creditLinesDealsMapper.findUsedAmtFromCust(maps);
        }
        return  机构已占用额度;
    }
    /**
     * 授信额度释放
     * @param inputDto
     * @param occupyType
     * @param outputDto
     */
    private void creditLinesRelease(CreditLinesInputDto inputDto, String occupyType, CreditLinesOutputDto outputDto){
        //
        releaseAstFirst(inputDto,occupyType,outputDto);
//        releaseOrgFirst(inputDto,occupyType,待释放额度);

    }

    private void releaseOrgFirst(CreditLinesInputDto inputDto, String occupyType, BigDecimal 剩余占用额度) {
        BigDecimal 机构已占用额度 = findUsedAmt(inputDto);
        if (剩余占用额度.compareTo(机构已占用额度) <= 0){
            // 释放前置交易占用
            if (StringUtil.isNotEmpty(inputDto.getEntrustId())){
                CreditLinesInputDto creditLinesInputDto = new CreditLinesInputDto();
                BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto,inputDto);
                creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                releaseCreditLinesPre(creditLinesInputDto,剩余占用额度,DictCodeListRsk.授信占用方式_机构授信.getValue());
            }
            if (creditLinesOrg != null && StringUtil.equals(creditLinesOrg.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
                generateDealResult(inputDto,creditLinesOrg,剩余占用额度,剩余占用额度,occupy,occupyType);
            }
            if (creditLinesOrgItem != null && StringUtil.equals(creditLinesOrgItem.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
                generateDealResult(inputDto,creditLinesOrgItem,剩余占用额度,剩余占用额度,occupy,occupyType);
            }
        }else {
            // 释放前置交易占用
            if (StringUtil.isNotEmpty(inputDto.getEntrustId())){
                CreditLinesInputDto creditLinesInputDto = new CreditLinesInputDto();
                BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto,inputDto);
                creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                releaseCreditLinesPre(creditLinesInputDto,机构已占用额度,DictCodeListRsk.授信占用方式_机构授信.getValue());
            }
            if (creditLinesOrg != null && StringUtil.equals(creditLinesOrg.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
                generateDealResult(inputDto,creditLinesOrg,机构已占用额度,机构已占用额度,occupy,occupyType);
            }
            if (creditLinesOrgItem != null && StringUtil.equals(creditLinesOrgItem.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
                generateDealResult(inputDto,creditLinesOrgItem,机构已占用额度,机构已占用额度,occupy,occupyType);
            }
            剩余占用额度 = 剩余占用额度.subtract(机构已占用额度);
        }
        if (creditLinesAst != null && StringUtil.equals(creditLinesAst.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
            if (StringUtil.equals(creditLinesAst.getCreditObj(),BussinessDictCodeList.授信对象_份额.getValue())){
                // 释放 占用额度
                generateDealResult(inputDto,creditLinesAst,剩余占用额度,剩余占用额度,occupy,occupyType);
            }else {
                // 查询资产类型
                FinProductTypeKey key = new FinProductTypeKey();
                key.setFinprodId(inputDto.getFinprodId());
                key.setBranch(0L);
                FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
                String RELEASE_MODE = CacheUtil.getSysParam(BussinessConstant.RELEASE_MODE);
                if (finProductType != null && StringUtil.equals(BussinessConstant.RELEASE_MODE_01,RELEASE_MODE)
                    && (StringUtil.equals(finProductType.getType8(),BussinessDictCodeList.平安资产类型_计划类资产_净值型.getValue())
                    || StringUtil.equals(finProductType.getType8(),BussinessDictCodeList.平安资产类型_计划类资产_货币型.getValue())
                    || StringUtil.equals(finProductType.getType8(),BussinessDictCodeList.平安资产类型_基金.getValue())
                    || StringUtil.equals(finProductType.getType8(),DictCodeListExt3.平安资产类型_权益类资产.getValue()))){
                    logger.info(getMessage("==========开始处理数据,交易或指令编号: "+inputDto.getTradeId()+",释放规则：先进先出=========="));
                    // 当前释放份额(剩余释放份额)
                    BigDecimal shareAmtTotal = inputDto.getShareAmt();
                    CreditLinesInputDto linesInputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(linesInputDto,inputDto);
                    // 释放金额
                    // 资产授信占用列表
                    List<CreditUsingAst> astList = creditLinesDealsMapper.getCreditUsingAstListByFin(inputDto);
                    for (CreditUsingAst item : astList){
                        BigDecimal releaseAmt = BigDecimal.ZERO;
                        if (shareAmtTotal.compareTo(BigDecimal.ZERO) <= 0){
                            break;
                        }
                        BigDecimal shareAmt = item.getShareAmt();
                        if (shareAmtTotal.compareTo(shareAmt) >= 0){
                            // 剩余释放份额
                            shareAmtTotal = shareAmtTotal.subtract(shareAmt);
                            item.setShareAmt(BigDecimal.ZERO);
                            // 当前释放金额
                            releaseAmt = shareAmt.multiply(item.getNetPrice());
                            // 当前释放份额
                            linesInputDto.setShareAmt(shareAmt);
                        }else {
                            // 前置交易剩余待释放份额
                            item.setShareAmt(shareAmt.subtract(shareAmtTotal));
                            // 当前释放金额
                            releaseAmt = shareAmtTotal.multiply(item.getNetPrice());
                            // 当前释放份额
                            linesInputDto.setShareAmt(shareAmtTotal);
                            // 剩余释放份额
                            shareAmtTotal = BigDecimal.ZERO;
                        }

                        if (isSave){
                            logger.info(getMessage("==========资产授信释放数据,交易或指令编号:"+item.getTradeId()+",剩余释放份额：=========" + item.getShareAmt()));
                            creditUsingAstMapper.updateByPrimaryKeySelective(item);
                        }
                        linesInputDto.setEntrustId(item.getTradeId());
                        linesInputDto.setNetPrice(item.getNetPrice());
                        // 释放 releaseAmt
                        generateDealResult(linesInputDto,creditLinesAst,releaseAmt,releaseAmt,occupy, occupyType);
                    }
                    // 资产系列授信占用列表
                    List<CreditUsingSeries> seriesList = creditLinesDealsMapper.findCreditUsingSeriesByFin(inputDto);
                    for (CreditUsingSeries item : seriesList){
                        BigDecimal releaseAmt;
                        if (shareAmtTotal.compareTo(BigDecimal.ZERO) <= 0){
                            break;
                        }
                        BigDecimal shareAmt = item.getShareAmt();
                        if (shareAmtTotal.compareTo(shareAmt) >= 0){
                            // 剩余释放份额
                            shareAmtTotal = shareAmtTotal.subtract(shareAmt);
                            item.setShareAmt(BigDecimal.ZERO);
                            // 当前释放金额
                            releaseAmt = shareAmt.multiply(item.getNetPrice());
                            // 当前释放份额
                            linesInputDto.setShareAmt(shareAmt);
                        }else {
                            // 前置交易剩余待释放份额
                            item.setShareAmt(shareAmt.subtract(shareAmtTotal));
                            // 当前释放金额
                            releaseAmt = shareAmtTotal.multiply(item.getNetPrice());
                            // 当前释放份额
                            linesInputDto.setShareAmt(shareAmtTotal);
                            // 剩余释放份额
                            shareAmtTotal = BigDecimal.ZERO;
                        }
                        if (isSave){
                            logger.info(getMessage("==========资产授信释放数据,交易或指令编号:"+item.getTradeId()+",剩余释放份额：=========" + item.getShareAmt()));
                            creditUsingSeriesMapper.updateByPrimaryKeySelective(item);
                        }
                        linesInputDto.setEntrustId(item.getTradeId());
                        linesInputDto.setNetPrice(item.getNetPrice());
                        // 释放 releaseAmt
                        generateDealResult(linesInputDto,creditLinesAst,releaseAmt,releaseAmt,occupy, occupyType);
                    }
                    // 先进先出规则未完全释放
                    if (shareAmtTotal.compareTo(BigDecimal.ZERO) > 0){
                        linesInputDto.setShareAmt(shareAmtTotal);
                        linesInputDto.setNetPrice(inputDto.getNetPrice());
                        linesInputDto.setEntrustId("");
                        BigDecimal releaseAmt = shareAmtTotal.multiply(inputDto.getNetPrice());
                        // 释放 releaseAmt
                        generateDealResult(linesInputDto,creditLinesAst,releaseAmt,releaseAmt,occupy, occupyType);
                    }
                    logger.info(getMessage("==========处理数据结束,交易或指令编号: "+inputDto.getTradeId()+",释放规则：先进先出=========="));
                }else {
                    // 释放 占用额度
                    generateDealResult(inputDto,creditLinesAst,剩余占用额度,剩余占用额度,occupy,occupyType);
                }
            }
        }
    }

    private void releaseAstFirst(CreditLinesInputDto inputDto, String occupyType, CreditLinesOutputDto outputDto) {

        if (creditLinesAst != null && StringUtil.equals(creditLinesAst.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
            if (StringUtil.equals(creditLinesAst.getCreditObj(),BussinessDictCodeList.授信对象_份额.getValue())){
                // 释放 占用额度
                BigDecimal 已占用额度 = creditLinesAst.getHapCreditAmt();
                // 已占用额度（可释放）小于 待释放额度 ，只能释放已占用额度（可释放）
                if (已占用额度.compareTo(compareAmtAst) < 0){
                    generateDealResult(inputDto,creditLinesAst,已占用额度,已占用额度,occupy,occupyType);
                }else {
                    // 释放 占用额度
                    generateDealResult(inputDto,creditLinesAst,compareAmtAst,compareAmtAst,occupy,occupyType);
                }
            }else {
                // 查询资产类型
                FinProductTypeKey key = new FinProductTypeKey();
                key.setFinprodId(inputDto.getFinprodId());
                key.setBranch(0L);
                FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
                String RELEASE_MODE = CacheUtil.getSysParam(BussinessConstant.RELEASE_MODE);
                if (finProductType != null && StringUtil.equals(BussinessConstant.RELEASE_MODE_01,RELEASE_MODE)
                    && (StringUtil.equals(finProductType.getType8(),BussinessDictCodeList.平安资产类型_计划类资产_净值型.getValue())
                    || StringUtil.equals(finProductType.getType8(),BussinessDictCodeList.平安资产类型_计划类资产_货币型.getValue())
                    || StringUtil.equals(finProductType.getType8(),BussinessDictCodeList.平安资产类型_基金.getValue())
                    || StringUtil.equals(finProductType.getType8(),DictCodeListExt3.平安资产类型_权益类资产.getValue()))){
                    logger.info(getMessage("==========开始处理数据,交易或指令编号: "+inputDto.getTradeId()+",释放规则：先进先出=========="));
                    // 当前释放份额(剩余释放份额)
                    BigDecimal shareAmtTotal = inputDto.getShareAmt();
                    BigDecimal 已占用额度 = creditLinesDealsMapper.getOccupiedQuota(inputDto);
                    CreditLinesInputDto linesInputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(linesInputDto,inputDto);
                    // 释放金额
                    // 资产授信占用列表
                    List<CreditUsingAst> astList = creditLinesDealsMapper.getCreditUsingAstListByFin(inputDto);
                    for (CreditUsingAst item : astList){
                        BigDecimal releaseAmt = BigDecimal.ZERO;
                        if (shareAmtTotal.compareTo(BigDecimal.ZERO) <= 0 || 已占用额度.compareTo(BigDecimal.ZERO) <= 0){
                            break;
                        }
                        BigDecimal shareAmt = item.getShareAmt();
                        if (shareAmtTotal.compareTo(shareAmt) >= 0){
                            // 剩余释放份额
                            shareAmtTotal = shareAmtTotal.subtract(shareAmt);
                            item.setShareAmt(BigDecimal.ZERO);
                            // 当前释放金额
                            releaseAmt = 已占用额度.compareTo(shareAmt.multiply(item.getNetPrice())) <= 0 ? 已占用额度 : shareAmt.multiply(item.getNetPrice());
                            // 当前释放份额
                            linesInputDto.setShareAmt(shareAmt);
                            已占用额度 = 已占用额度.subtract(releaseAmt);
                        }else {
                            // 前置交易剩余待释放份额
                            item.setShareAmt(shareAmt.subtract(shareAmtTotal));
                            // 当前释放金额
                            releaseAmt = 已占用额度.compareTo(shareAmtTotal.multiply(item.getNetPrice())) <= 0 ? 已占用额度 : shareAmtTotal.multiply(item.getNetPrice());
                            // 当前释放份额
                            linesInputDto.setShareAmt(shareAmtTotal);
                            已占用额度 = 已占用额度.subtract(releaseAmt);
                            // 剩余释放份额
                            shareAmtTotal = BigDecimal.ZERO;
                        }

                        if (isSave){
                            logger.info(getMessage("==========资产授信释放数据,交易或指令编号:"+item.getTradeId()+",剩余释放份额：=========" + item.getShareAmt()));
                            creditUsingAstMapper.updateByPrimaryKeySelective(item);
                        }
                        linesInputDto.setEntrustId(item.getTradeId());
                        linesInputDto.setNetPrice(item.getNetPrice());
                        // 释放 releaseAmt
                        generateDealResult(linesInputDto,creditLinesAst,releaseAmt,releaseAmt,occupy, occupyType);
                    }
                    // 资产系列授信占用列表
                    已占用额度 = creditLinesDealsMapper.getOccupiedQuota1(inputDto);
                    List<CreditUsingSeries> seriesList = creditLinesDealsMapper.findCreditUsingSeriesByFin(inputDto);
                    for (CreditUsingSeries item : seriesList){
                        BigDecimal releaseAmt;
                        if (shareAmtTotal.compareTo(BigDecimal.ZERO) <= 0 || 已占用额度.compareTo(BigDecimal.ZERO) <= 0){
                            break;
                        }
                        BigDecimal shareAmt = item.getShareAmt();
                        if (shareAmtTotal.compareTo(shareAmt) >= 0){
                            // 剩余释放份额
                            shareAmtTotal = shareAmtTotal.subtract(shareAmt);
                            item.setShareAmt(BigDecimal.ZERO);
                            // 当前释放金额
                            releaseAmt = 已占用额度.compareTo(shareAmt.multiply(item.getNetPrice())) <= 0 ? 已占用额度 : shareAmt.multiply(item.getNetPrice());
                            // 当前释放份额
                            linesInputDto.setShareAmt(shareAmt);
                            已占用额度 = 已占用额度.subtract(releaseAmt);
                        }else {
                            // 前置交易剩余待释放份额
                            item.setShareAmt(shareAmt.subtract(shareAmtTotal));
                            // 当前释放金额
                            releaseAmt = 已占用额度.compareTo(shareAmtTotal.multiply(item.getNetPrice())) <= 0 ? 已占用额度 : shareAmtTotal.multiply(item.getNetPrice());
                            // 当前释放份额
                            linesInputDto.setShareAmt(shareAmtTotal);
                            已占用额度 = 已占用额度.subtract(releaseAmt);
                            // 剩余释放份额
                            shareAmtTotal = BigDecimal.ZERO;
                        }
                        if (isSave){
                            logger.info(getMessage("==========资产授信释放数据,交易或指令编号:"+item.getTradeId()+",剩余释放份额：=========" + item.getShareAmt()));
                            creditUsingSeriesMapper.updateByPrimaryKeySelective(item);
                        }
                        linesInputDto.setEntrustId(item.getTradeId());
                        linesInputDto.setNetPrice(item.getNetPrice());
                        // 释放 releaseAmt
                        generateDealResult(linesInputDto,creditLinesAst,releaseAmt,releaseAmt,occupy, occupyType);
                    }
                    logger.info(getMessage("==========处理数据结束,交易或指令编号: "+inputDto.getTradeId()+",释放规则：先进先出=========="));
                }else {
                    // 释放 占用额度
                    BigDecimal 已占用额度 = creditLinesAst.getHapCreditAmt();
                    // 已占用额度（可释放）小于 待释放额度 ，只能释放已占用额度（可释放）
                    if (已占用额度.compareTo(compareAmtAst) < 0){
                        generateDealResult(inputDto,creditLinesAst,已占用额度,已占用额度,occupy,occupyType);
                    }else {
                        // 释放 占用额度
                        generateDealResult(inputDto,creditLinesAst,compareAmtAst,compareAmtAst,occupy,occupyType);
                    }
                }
            }
        }else if (creditLinesAst != null){
            BigDecimal 已使用额度 = creditLinesAst.getCreditAmtAdd();
            if (已使用额度.compareTo(BigDecimal.ZERO) > 0 && 已使用额度.compareTo(compareAmtAst) < 0){
                generateDealResult(inputDto,creditLinesAst,BigDecimal.ZERO,已使用额度,occupy,occupyType);
            }else if (已使用额度.compareTo(compareAmtAst) >= 0){
                generateDealResult(inputDto,creditLinesAst,BigDecimal.ZERO,compareAmtAst,occupy,occupyType);
            }
        }
        //当存在资产授信，并且资产授信配置是否占用主题授信字段为否时，返回，不释放机构授信额度
        if(creditLinesAst!=null && "N".equals(creditLinesAst.getCreditFlag())){
            return;
        }
        // 释放前置交易占用
        if (StringUtil.isNotEmpty(inputDto.getEntrustId())){
            CreditLinesInputDto creditLinesInputDto = new CreditLinesInputDto();
            BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto,inputDto);
            creditLinesInputDto.setTradeId(inputDto.getEntrustId());
            releaseCreditLinesPre(creditLinesInputDto,compareAmtOrg,DictCodeListRsk.授信占用方式_机构授信.getValue());
        }
        if (creditLinesOrg != null && StringUtil.equals(creditLinesOrg.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
            generateDealResult(inputDto,creditLinesOrg,compareAmtOrg,compareAmtOrg,occupy,occupyType);
            // 记录机构授信占用额度
            outputDto.setCustomerId(creditLinesOrg.getCustomerId());
            outputDto.setOccupyAmtOrg(compareAmtOrg.negate());
        }else if (creditLinesOrg != null){
            BigDecimal 已使用额度 = creditLinesOrg.getCreditAmtAdd();
            if (已使用额度.compareTo(BigDecimal.ZERO) > 0 && 已使用额度.compareTo(compareAmtOrg) < 0){
                generateDealResult(inputDto,creditLinesOrg,BigDecimal.ZERO,已使用额度,occupy,occupyType);
            }else if (已使用额度.compareTo(compareAmtOrg) >= 0){
                generateDealResult(inputDto,creditLinesOrg,BigDecimal.ZERO,compareAmtOrg,occupy,occupyType);
            }
        }
        if (creditLinesOrgItem != null && StringUtil.equals(creditLinesOrgItem.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
            generateDealResult(inputDto,creditLinesOrgItem,compareAmtOrg,compareAmtOrg,occupy,occupyType);
        }else if (creditLinesOrgItem != null){
            BigDecimal 已使用额度 = creditLinesOrgItem.getCreditAmtAdd();
            if (已使用额度.compareTo(BigDecimal.ZERO) > 0 && 已使用额度.compareTo(compareAmtOrg) < 0){
                generateDealResult(inputDto,creditLinesOrgItem,BigDecimal.ZERO,已使用额度,occupy,occupyType);
            }else if (已使用额度.compareTo(compareAmtOrg) >= 0){
                generateDealResult(inputDto,creditLinesOrgItem,BigDecimal.ZERO,compareAmtOrg,occupy,occupyType);
            }
        }
    }

    @Override
    protected void occupyCreditLines(List<CreditLinesInputDto> inputDtoList, String occupyType,Date tradeDate) {
        logger.info("==========批量接口处理数据开始==========");
        for (CreditLinesInputDto inputDto : inputDtoList){
            // 处理风险4合一接口，软控、硬控参数 传递 -> 授信占用额度控制是否为软控(Y 是 N 否)
            if (StringUtil.equals(inputDto.getCreditControl(),DictCodeListRsk.风险控制类别_软控.getValue())){
                CREDIT_SOFT_SKIP_MODE = DictCodeList.是否_是.getValue();
            }else if (StringUtil.equals(inputDto.getCreditControl(),DictCodeListRsk.风险控制类别_硬控.getValue())){
                CREDIT_SOFT_SKIP_MODE = DictCodeList.是否_否.getValue();
            }
            // check时无业务编号，生成随机编号，用作占用记录和输出结果做对应
            inputDto.setTradeId(IDGenerator.generate());
            CreditLinesOutputDto outputDto = dealEachTradeInfo(inputDto,occupyType);
            if (Objects.nonNull(outputDto)){
                outputDto.setTradeId(inputDto.getTradeId());
                outputDto.setOccupyType(inputDto.getCreditCallType());
                creditLinesOutputDtoList.add(outputDto);
            }
        }
        logger.info("==========批量接口处理数据结束==========");
    }

    /**
     * 资产授信禁投校验
     * @param inputDto
     * @param creditLines
     * @param outputDto
     * @return
     */
    private boolean checkAssetCreditForbidden(CreditLinesInputDto inputDto, CreditLines creditLines, CreditLinesOutputDto outputDto) {
        if (creditLines == null){
            return false;
        }
        // 查询资产类型
        FinProductTypeKey key = new FinProductTypeKey();
        key.setFinprodId(inputDto.getFinprodId());
        key.setBranch(0L);
        FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);

        if (StringUtil.equals(inputDto.getPs(),DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(inputDto.getPs(),DictCodeList.交易方向_申购.getValue())){
            // 填写了审批到期 日，则该资产仅允许在生效日至审批到期日区间内进行买入交易；卖出交易不受限制
            if (creditLines.getApprMdate() != null && inputDto.getTradeDate() != null
                    /*交易日期在生效日-审批到期日范围外，校验不通过，生成错误信息，并返回*/
                    && (inputDto.getTradeDate().before(creditLines.getVdate()) || inputDto.getTradeDate().after(creditLines.getApprMdate()))){
                outputDto.setTradeId(inputDto.getTradeId());
                outputDto.setErrMessage("买入交易不在生效日和审批到期日之间，禁止交易");
                return true;
            }
            // 当填写了首次开贷区间时，非标准化债权类资产或私募股权基金在批复生效日和到期日之间的第一笔买入交易必须在首次开贷区间内，否则不允许做买入交易；卖出交易不受限制；
            if (finProductType != null && creditLines.getCreditVdate() != null && creditLines.getCreditMdate() != null
                    && (StringUtil.equals(finProductType.getType8(),DictCodeList.平安资产类型_项目类资产.getValue())
                    || StringUtil.equals(finProductType.getType9(),BussinessDictCodeList.行内资产三级分类_私募股权基金.getValue()))){
                Date firstTradeDate = creditLinesDealsMapper.findFirstTradeDate(inputDto.getFinprodId(),inputDto.getTradeDate(),creditLines.getVdate(),creditLines.getMdate());
                /* 第一次买入交易日期不在首次开贷区间，校验不通过，生成错误信息，并返回*/
                if (firstTradeDate.before(creditLines.getCreditVdate()) || firstTradeDate.after(creditLines.getCreditMdate())){
                    outputDto.setTradeId(inputDto.getTradeId());
                    outputDto.setErrMessage("非标准化债权类资产或私募股权基金在批复生效日和到期日之间的第一笔买入交易不在首次开贷区间内，禁止交易");
                    return true;
                }
            }
            // 当是否持有至到期日 = 是，不允许进行买入交易
            if (StringUtil.equals(DictCodeList.是否_是.getValue(),creditLines.getHoldMaturity())){
                outputDto.setTradeId(inputDto.getTradeId());
                outputDto.setErrMessage("该资产为持有至到期资产，禁止买入方向交易");
                return true;
            }
        }
        return false;
    }

    /**
     * 查询主体授信记录
     * @param inputDto
     * @param oneStepInfo
     * @return
     */
    private void getOrgCreditLines(CreditLinesInputDto inputDto, CreditComInfoOneStepDto oneStepInfo) {
        // 获取授信对象信息
        oneStepInfo.setTradeDate(inputDto.getTradeDate());
        // 交易对手为非法人机构时
        if (StringUtil.equals(oneStepInfo.getCreditType(),DictCodeListRsk.额度分类_非法人机构.getValue())){
            List<CreditLines> list = creditLinesDealsMapper.getOrgCeLineItemList(oneStepInfo);
            if (!StringUtil.isEmptyList(list)){
                creditLinesOrgItem = getCreditLines(list.get(0));
            }
            return;
        }
        // 机构额度
        CreditLines creditLines = getOrgCeLineList(oneStepInfo);
        if (creditLines != null){
            // 交易对手为非法人机构时，不扣主体额度
            if (!StringUtil.equals(oneStepInfo.getCreditType(),DictCodeListRsk.额度分类_非法人机构.getValue())){
                creditLinesOrg = creditLines;
            }
            oneStepInfo.setPflshList(Collections.singletonList(creditLines.getPflsh()));
            // 机构向下分项额度
            List<CreditLines> list = creditLinesDealsMapper.getOrgCeLineItemList(oneStepInfo);
            if (!StringUtil.isEmptyList(list)){
                creditLinesOrgItem = getCreditLines(list.get(0));
            }
        }
    }


    /**
     * 债券一级投标的编辑再提交，需要将原先的占用置为无效
     * @param inputDto
     */
    private void revokeCreditOccupyF32(CreditLinesInputDto inputDto) {
        if (StringUtil.isEmpty(inputDto.getTradeId())){
            return;
        }
        // 如果是交易撤销，交易编号可能会有后缀“R”
        if (inputDto.getTradeId().endsWith("R")){
            inputDto.setTradeId(inputDto.getTradeId().substring(0,inputDto.getTradeId().length() - 1));
        }
        List<CreditUsingCustomer> 机构额度占用明细 = creditLinesDealsMapper.getCreditUsingCustomerList(inputDto);
        List<CreditUsingCustomerMst> 机构分项占用明细 = creditLinesDealsMapper.getCreditUsingCustomerMsts(inputDto);

        if(!StringUtil.isEmptyList(机构额度占用明细)){
            机构额度占用明细.forEach(item->{
                CreditLines creditLines = creditLinesMapper.selectByPrimaryKey(item.getCreditLinesId());
                if (creditLines != null){
                    reCalcuUsedAmt(creditLines,inputDto,item.getAmount().negate());
                    if (isSave){
                        // 更新占用额度
                        creditLinesDealsMapper.updateLinesByPrimaryKeySelective(creditLines);
                        item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                        creditUsingCustomerMapper.updateByPrimaryKeySelective(item);
                    }
                }
            });

        }
        if(!StringUtil.isEmptyList(机构分项占用明细)){
            机构分项占用明细.forEach(item->{
                CreditLines creditLines = creditLinesMapper.selectByPrimaryKey(item.getCreditLinesId());
                if (creditLines != null){
                    reCalcuUsedAmt(creditLines,inputDto,item.getAmount().negate());
                    if (isSave){
                        // 更新占用额度
                        creditLinesDealsMapper.updateLinesByPrimaryKeySelective(creditLines);
                        item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                        creditUsingCustomerMstMapper.updateByPrimaryKeySelective(item);

                    }
                }
            });
        }
    }
    /**
     * 债券一级投标的汇总确认和中标结果维护需要更新指令的预占额度
     * @param inputDto
     * @param 剩余占用额度
     */
    private void updateCreditOccupy(CreditLinesInputDto inputDto, BigDecimal 剩余占用额度) {
        if (StringUtil.isEmpty(inputDto.getTradeId())){
            return;
        }
        // 如果是交易撤销，交易编号可能会有后缀“R”
        if (inputDto.getTradeId().endsWith("R")){
            inputDto.setTradeId(inputDto.getTradeId().substring(0,inputDto.getTradeId().length() - 1));
        }
        List<CreditUsingCustomer> 机构额度占用明细 = creditLinesDealsMapper.getCreditUsingCustomerList(inputDto);
        List<CreditUsingCustomerMst> 机构分项占用明细 = creditLinesDealsMapper.getCreditUsingCustomerMsts(inputDto);

        if(!StringUtil.isEmptyList(机构额度占用明细)){
            机构额度占用明细.forEach(item->{
                CreditLines creditLines = creditLinesMapper.selectByPrimaryKey(item.getCreditLinesId());
                if (creditLines != null){

                    if (isSave){
                        // 更新占用额度
                        creditLinesDealsMapper.updateLinesByPrimaryKeySelective(creditLines);
                        //  一级债券投标提交时更新指令占用额度，撤销时还原指令占用额度
                        if (StringUtil.equals(inputDto.getTradeStatus(), DictCodeListExt1.交易状态_撤销.getValue())){
                            // 重新计算授信额度占用
                            reCalcuUsedAmt(creditLines,inputDto,getAmount(剩余占用额度));
                            item.setAmount(getAmount(item.getAmount()).add(getAmount(剩余占用额度)));
                        }else {
                            // 重新计算授信额度占用
                            reCalcuUsedAmt(creditLines,inputDto,getAmount(剩余占用额度).negate());
                            item.setAmount(getAmount(item.getAmount()).subtract(getAmount(剩余占用额度)));
                        }
                        creditUsingCustomerMapper.updateByPrimaryKeySelective(item);
                    }
                }
            });

        }
        if(!StringUtil.isEmptyList(机构分项占用明细)){
            机构分项占用明细.forEach(item->{
                CreditLines creditLines = creditLinesMapper.selectByPrimaryKey(item.getCreditLinesId());
                if (creditLines != null){
                    if (isSave){
                        // 更新占用额度
                        creditLinesDealsMapper.updateLinesByPrimaryKeySelective(creditLines);
                        //  一级债券投标提交时更新指令占用额度，撤销时还原指令占用额度
                        if (StringUtil.equals(inputDto.getTradeStatus(), DictCodeListExt1.交易状态_撤销.getValue())){
                            // 重新计算授信额度占用
                            reCalcuUsedAmt(creditLines,inputDto,getAmount(剩余占用额度));
                            item.setAmount(getAmount(item.getAmount()).add(getAmount(剩余占用额度)));
                        }else {
                            // 重新计算授信额度占用
                            reCalcuUsedAmt(creditLines,inputDto,getAmount(剩余占用额度).negate());
                            item.setAmount(getAmount(item.getAmount()).subtract(getAmount(剩余占用额度)));
                        }
                        creditUsingCustomerMstMapper.updateByPrimaryKeySelective(item);

                    }
                }
            });
        }
    }

    private CreditLines getOrgCeLineList(CreditComInfoOneStepDto oneStepInfo) {
        List<CreditLines> list = creditLinesDealsMapper.getOrgCeLineList(oneStepInfo);
        if (list.size() > 0){
            return getCreditLines(list.get(0));
        }
        return null;
    }


    private void convertCreditObj(CreditComInfoOneStepDto oneStepInfo, CreditLinesInputDto inputDto) {

        FinProduct finProduct = finProductMapper.selectByPrimaryKey(inputDto.getFinprodId());

        if (finProduct == null || StringUtil.equals(oneStepInfo.getDeductObjType(),DictCodeListExt3.扣减对象_交易对手.getValue())){
            // 交易对手
            convertCounterId(inputDto,oneStepInfo);
            return;
        }

        if(StringUtil.equals(oneStepInfo.getDeductObjType(),DictCodeListExt3.扣减对象_发行人.getValue())
            || StringUtil.equals(oneStepInfo.getDeductObjType(),DictCodeListExt3.扣减对象_存放行.getValue())){
            oneStepInfo.setDeductObj(finProduct.getIssuer());
        }
        else if(StringUtil.equals(oneStepInfo.getDeductObjType(),DictCodeListExt3.扣减对象_融资人.getValue())){
            oneStepInfo.setDeductObj(finProduct.getFinancier());
        }
        else if(StringUtil.equals(oneStepInfo.getDeductObjType(),DictCodeListExt3.扣减对象_担保人.getValue())){
            oneStepInfo.setDeductObj(finProduct.getSponsor());
        }else if (StringUtil.equals(oneStepInfo.getDeductObjType(),DictCodeListExt3.扣减对象_管理人.getValue())){
            oneStepInfo.setDeductObj(finProduct.getManager());
        } else if (StringUtil.equals(oneStepInfo.getDeductObjType(),DictCodeListExt3.扣减对象_合作机构.getValue())) {
            if (StringUtils.isNotEmpty(inputDto.getChlId())) {
                FinProduct chlIdProduct = finProductMapper.selectByPrimaryKey(inputDto.getChlId());
                if (chlIdProduct != null){
                    oneStepInfo.setDeductObj(chlIdProduct.getManager());
                }
            } else{
                // 交易对手
                convertCounterId(inputDto, oneStepInfo);
            }
        }
    }

    private void convertCounterId(CreditLinesInputDto creditLinesInputDto, CreditComInfoOneStepDto oneStepInfo) {
        //转换交易对手为机构id
        if (StringUtil.isNotEmpty(creditLinesInputDto.getCounterId())) {
            MstCounterParty mstCounterParty = mstCounterPartyMapper.selectByPrimaryKey(creditLinesInputDto.getCounterId());
            if (mstCounterParty == null){
                return;
            }
            if (StringUtil.equals(mstCounterParty.getCounterType(),DictCodeListExt1.交易对手类型_资管产品.getValue())){
                oneStepInfo.setCreditType(DictCodeListRsk.额度分类_非法人机构.getValue());
            }
            if (StringUtil.isNotEmpty(mstCounterParty.getLinkId())) {
                oneStepInfo.setDeductObj(mstCounterParty.getLinkId());
            }
            if (StringUtil.isNotEmpty(mstCounterParty.getManagerId())){
                oneStepInfo.setDeductObj(mstCounterParty.getManagerId());
            }
        }
    }

    private void cal占用额度(CreditLinesInputDto inputDto) {
        // 债券根据最新面值计算
        if ((DictCodeList.交易方向_买入.getValue().equals(inputDto.getPs()) || DictCodeList.交易方向_卖出.getValue().equals(inputDto.getPs()))
                && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(inputDto.getFinprodType2())){
            //最新面值
            BigDecimal newFaceValue = creditLinesDealsMapper.getFaceValue(inputDto);
            if (newFaceValue == null){
                newFaceValue = new BigDecimal(100);
            }
            if (inputDto.getShareAmt() != null) {
                inputDto.setTradeAmt(newFaceValue.multiply(inputDto.getShareAmt()));
            }
        }
        // 先根据资产授信规则初步判断 授信对象是 金额 还是 份额
        if (creditLinesAst != null && StringUtil.equals(creditLinesAst.getCreditObj(),BussinessDictCodeList.授信对象_份额.getValue())){
            compareAmtAst = inputDto.getShareAmt();
            releaseAmtAst = inputDto.getShareAmt();
        }else {
            compareAmtAst = inputDto.getTradeAmt();
            releaseAmtAst = inputDto.getTradeAmt();
        }
        // 存在机构授信(金额)
        if (creditLinesOrgItem != null){
            compareAmtOrg = inputDto.getTradeAmt();
            releaseAmtOrg = inputDto.getTradeAmt();
            if(compareAmtOrg!=null&&releaseAmtOrg!=null&&oneStepInfo!=null&&oneStepInfo.getRiskRatio()!=null){
                releaseAmtOrg=releaseAmtOrg.multiply(oneStepInfo.getRiskRatio().divide(new BigDecimal(100)));
                compareAmtOrg=compareAmtOrg.multiply(oneStepInfo.getRiskRatio().divide(new BigDecimal(100)));
            }
        }
    }

    private List<CreditComInfoOneStepDto> getCreditInfoOneStep(CreditLinesInputDto creditLinesInputDto) {
        List <CreditComInfoOneStepDto> list = new ArrayList<>();
        // 根据资产分类获取额度标签，从第5层开始找，找不到再向上逐级找
        int i = 5;
        while (StringUtil.isEmptyList(list) && i > 0){
            boolean isFinProduct = false;
            if (StringUtils.isNotEmpty(creditLinesInputDto.getFinprodId())) {
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(creditLinesInputDto.getFinprodId());
                FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(creditLinesInputDto.getFinprodId());
                FinProductTypeKey key = new FinProductTypeKey();
                key.setFinprodId(creditLinesInputDto.getFinprodId());
                key.setBranch(0L);
                FinProductType finProductType = creditLinesDealsMapper.selectByPrimaryKey(key);
                if (finProduct == null || finProductAdd == null || finProductType == null){
                    logger.info(creditLinesInputDto.getFinprodId() + ": 资产信息不存在！");
                    return list;
                }
                // 是否永续
                String isSus = StringUtil.isEmpty(finProduct.getIsSus()) ? "N" : finProduct.getIsSus();
                // 是否次级
                String secBond = "";
                if (StringUtil.equals(finProductType.getType8(),DictCodeList.平安资产类型_债券.getValue())
                    && StringUtil.equals(finProductType.getType9(),DictCodeList.WIND一级分类_含债券一级分类等_金融债.getValue())
                    && StringUtil.equals(finProductType.getType10(),DictCodeListRsk.授信资产三级分类_政策银行债.getValue())){
                    secBond = StringUtil.isEmpty(finProductAdd.getIsSecBond()) ? "N" : finProductAdd.getIsSecBond();
                }
                isFinProduct = true;
                list.addAll(creditLinesDealsMapper.findCreditTagInfo(creditLinesInputDto.getFinprodId(),isSus,secBond,String.valueOf(i--)));

            }
            if (!isFinProduct) {
                FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
                finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_无标的投管资产类型映射行内资产分类.getValue());
                finProductTypeConfig.setInputValue1(creditLinesInputDto.getTgBusiType());
                finProductTypeConfig.setInputValue2(creditLinesInputDto.getPsPayment());
                finProductTypeConfig.setInputValue3(creditLinesInputDto.getSaleCode());

                finProductTypeConfig = getAssetFiveType(finProductTypeConfig);
                Map<String, Object> map = new HashMap<String, Object>();
                if(!StringUtil.isNotEmpty(creditLinesInputDto.getTgBusiType())){
                    creditLinesInputDto.setTgBusiType("");
                }
                map.put("TYPE_8", creditLinesInputDto.getTgBusiType());
                map.put("TYPE_9", null == finProductTypeConfig.getOutputValue2()? "":finProductTypeConfig.getOutputValue2());
                map.put("TYPE_10", null == finProductTypeConfig.getOutputValue3()? "":finProductTypeConfig.getOutputValue3());
                map.put("level", String.valueOf(i--));
                list.addAll(creditLinesDealsMapper.findCreditTagInfoOther(map));
            }
        }
        return list;
    }
    private FinProductTypeConfig getAssetFiveType(FinProductTypeConfig param) {
        // 配置类型未传参时，默认投管分类
        if (StringUtil.isEmpty(param.getConfigType())) {
            param.setConfigType(DictCodeListExt2.配置类型_投管分类.getValue());
        }
        List<FinProductTypeConfig> list = creditLinesDealsMapper.getAssetFiveType(param);
        if (StringUtil.isEmptyList(list) || list.size() > 1 || list.get(0) == null) {
            return new FinProductTypeConfig();
        }
        return list.get(0);
    }
    @Override
    protected void batchInsertCredit() {
        // 处理硬控状态下，check时额度满足，占用时额度不满足的异常处理
        for (CreditLinesOutputDto outputDto: creditLinesOutputDtoList){
            if (StringUtil.isNotEmpty(outputDto.getErrMessage())){
                throw new BusinessException("MSG:" + outputDto.getErrMessage());
            }
        }

        List<CreditLines> 额度明细 = this.getCreditLinesList();

        if(!StringUtil.isEmptyList(creditUsingSeriesList)){
            creditUsingSeriesList.forEach(item->{
                creditUsingSeriesMapper.insertSelective(item);
            });
        }
        if(!StringUtil.isEmptyList(creditUsingAstList)){
            creditUsingAstList.forEach(item->{
                creditUsingAstMapper.insertSelective(item);
            });
        }
        if(!StringUtil.isEmptyList(creditUsingCustomerList)){
            creditUsingCustomerList.forEach(item->{
                creditUsingCustomerMapper.insertSelective(item);
            });
        }
        if(!StringUtil.isEmptyList(creditUsingCustomerMstList)){
            creditUsingCustomerMstList.forEach(item->{
                creditUsingCustomerMstMapper.insertSelective(item);
            });
        }
        if (!StringUtil.isEmptyList(额度明细)){
            额度明细.forEach(item ->{
                creditLinesDealsMapper.updateLinesByPrimaryKeySelective(item);
            });
        }
    }

    private BigDecimal dealCreditOccupy(CreditLinesInputDto inputDto,CreditLines creditLines, String occupyType, BigDecimal 占用额度) {

        BigDecimal 可用额度 = ArithmeticUtil.subtract(creditLines.getCreditAmt(),creditLines.getHapCreditAmt());
        // 可用额度不足,只能占用对应的可用额度，返回剩余占用额度，继续扣减对应的机构额度
        if (可用额度.compareTo(占用额度) < 0){
            generateDealResult(inputDto,creditLines,可用额度,可用额度,occupy,occupyType);
            return 占用额度.subtract(可用额度);
        }else {
            generateDealResult(inputDto,creditLines,占用额度,占用额度,occupy,occupyType);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 释放前置交易占用额度（包括预占 和 申购转申购确认、赎回转赎回确认）
     * 根据当前交易占用额度，更新前置交易占用额度
     * @param inputDto
     * @param 占用额度
     */
    private void releaseCreditLinesPre(CreditLinesInputDto inputDto, BigDecimal 占用额度,String type) {
        // 资产
        if (type.equals(DictCodeListRsk.授信占用方式_资产授信.getValue())){
            List<CreditUsingAst> 资产额度占用明细 = creditLinesDealsMapper.getCreditUsingAstList(inputDto);
            List<CreditUsingSeries> 系列额度占用明细 = creditLinesDealsMapper.findCreditUsingSeries(inputDto);
            if (!StringUtil.isEmptyList(资产额度占用明细)){
                资产额度占用明细.forEach(item -> {
                    CreditLines creditLine = creditLinesMapper.selectByPrimaryKey(item.getCreditLinesId());
                    creditLine = getCreditLines(creditLine);
                    // 前置交易为预占，需更新额度
                    BigDecimal 前置交易剩余占用额度 = item.getNewAmount().compareTo(占用额度) <= 0 ? BigDecimal.ZERO : item.getNewAmount().subtract(占用额度);
                    // 当前交易释放前置交易额度，用于更新额度管理表可用额度
                    BigDecimal resetAmt = item.getNewAmount().subtract(前置交易剩余占用额度);
                    // 重置额度管理表剩余可用额度
                    reCalcuUsedAmt(creditLine,inputDto,resetAmt.negate());
                    if (StringUtil.equals(item.getCreditOccureType(),DictCodeListRsk.授信占用类别_预占.getValue())){
                        item.setShareAmt(前置交易剩余占用额度);
                        item.setNewAmount(前置交易剩余占用额度);
                        if (occupy || StringUtil.equals(creditLine.getIsLoopAmt(), DictCodeList.是否_是.getValue())){
                            item.setAmount(前置交易剩余占用额度);
                        }
                    }
                    if (isSave){
                        // 前置交易占用额度重新赋值
//                        item.setLastAmount(ArithmeticUtil.subtract(creditLine.getCreditAmt(),creditLine.getHapCreditAmt()));
                        if (StringUtil.equals(item.getCreditOccureType(),DictCodeListRsk.授信占用类别_实占.getValue())){
                            item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                        }
                        // 更新占用额度
                        creditLinesDealsMapper.updateLinesByPrimaryKeySelective(creditLine);
                        // 更新对应的前置交易占用记录
                        creditUsingAstMapper.updateByPrimaryKeySelective(item);
                    }
                });
            }

            if(!StringUtil.isEmptyList(系列额度占用明细)){
                系列额度占用明细.forEach(item -> {
                    CreditLines creditLine = creditLinesMapper.selectByPrimaryKey(item.getCreditLinesId());
                    creditLine = getCreditLines(creditLine);
                    // 前置交易为预占，需更新额度
                    BigDecimal 前置交易剩余占用额度 = item.getNewAmount().compareTo(占用额度) <= 0 ? BigDecimal.ZERO : item.getNewAmount().subtract(占用额度);
                    // 当前交易释放前置交易额度，用于更新额度管理表可用额度
                    BigDecimal resetAmt = item.getNewAmount().subtract(前置交易剩余占用额度);
                    // 重置额度管理表剩余可用额度
                    reCalcuUsedAmt(creditLine,inputDto,resetAmt.negate());
                    if (StringUtil.equals(item.getCreditOccureType(),DictCodeListRsk.授信占用类别_预占.getValue())){
                        item.setNewAmount(前置交易剩余占用额度);
                        item.setShareAmt(前置交易剩余占用额度);
                        if (occupy || StringUtil.equals(creditLine.getIsLoopAmt(), DictCodeList.是否_是.getValue())){
                            item.setAmount(前置交易剩余占用额度);
                        }
                    }
                    if (isSave){
                        // 前置交易占用额度重新赋值
//                        item.setLastAmount(ArithmeticUtil.subtract(creditLine.getCreditAmt(),creditLine.getHapCreditAmt()));
                        if (StringUtil.equals(item.getCreditOccureType(),DictCodeListRsk.授信占用类别_实占.getValue())){
                            item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                        }
                        // 更新占用额度
                        creditLinesDealsMapper.updateLinesByPrimaryKeySelective(creditLine);
                        // 更新对应的前置交易占用记录
                        creditUsingSeriesMapper.updateByPrimaryKeySelective(item);
                    }
                });
            }
        }
        // 机构
        if (type.equals(DictCodeListRsk.授信占用方式_机构授信.getValue())){
            List<CreditUsingCustomer> 机构额度占用明细 = creditLinesDealsMapper.getCreditUsingCustomerList(inputDto);
            List<CreditUsingCustomerMst> 机构分项占用明细 = creditLinesDealsMapper.getCreditUsingCustomerMsts(inputDto);

            if(!StringUtil.isEmptyList(机构额度占用明细)){
                机构额度占用明细.forEach(item -> {
                    CreditLines creditLine = creditLinesMapper.selectByPrimaryKey(item.getCreditLinesId());
                    creditLine = getCreditLines(creditLine);
                    // 前置交易为预占，需更新额度
                    BigDecimal 前置交易剩余占用额度 = item.getNewAmount().compareTo(占用额度) <= 0 ? BigDecimal.ZERO : item.getNewAmount().subtract(占用额度);
                    // 当前交易释放前置交易额度，用于更新额度管理表可用额度
                    BigDecimal resetAmt = item.getNewAmount().subtract(前置交易剩余占用额度);
                    // 重置额度管理表剩余可用额度
                    reCalcuUsedAmt(creditLine,inputDto,resetAmt.negate());
                    if (StringUtil.equals(item.getCreditOccureType(),DictCodeListRsk.授信占用类别_预占.getValue())){
                        item.setNewAmount(前置交易剩余占用额度);
                        if (occupy || StringUtil.equals(creditLine.getIsLoopAmt(), DictCodeList.是否_是.getValue())){
                            item.setAmount(前置交易剩余占用额度);
                        }
                    }
                    if (isSave){
                        // 前置交易占用额度重新赋值
//                        item.setLastAmount(ArithmeticUtil.subtract(creditLine.getCreditAmt(),creditLine.getHapCreditAmt()));
                        if (StringUtil.equals(item.getCreditOccureType(),DictCodeListRsk.授信占用类别_实占.getValue())){
                            item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                        }
                        // 更新占用额度
                        creditLinesDealsMapper.updateLinesByPrimaryKeySelective(creditLine);
                        // 更新对应的前置交易占用记录
                        creditUsingCustomerMapper.updateByPrimaryKeySelective(item);
                    }
                });
            }

            if(!StringUtil.isEmptyList(机构分项占用明细)){
                机构分项占用明细.forEach(item -> {
                    CreditLines creditLine = creditLinesMapper.selectByPrimaryKey(item.getCreditLinesId());
                    creditLine = getCreditLines(creditLine);
                    // 前置交易为预占，需更新额度
                    BigDecimal 前置交易剩余占用额度 = item.getNewAmount().compareTo(占用额度) <= 0 ? BigDecimal.ZERO : item.getNewAmount().subtract(占用额度);
                    // 当前交易释放前置交易额度，用于更新额度管理表可用额度
                    BigDecimal resetAmt = item.getNewAmount().subtract(前置交易剩余占用额度);
                    // 重置额度管理表剩余可用额度
                    reCalcuUsedAmt(creditLine,inputDto,resetAmt.negate());
                    if (StringUtil.equals(item.getCreditOccureType(),DictCodeListRsk.授信占用类别_预占.getValue())){
                        item.setNewAmount(前置交易剩余占用额度);
                        if (occupy || StringUtil.equals(creditLine.getIsLoopAmt(), DictCodeList.是否_是.getValue())){
                            item.setAmount(前置交易剩余占用额度);
                        }
                    }
                    if (isSave){
                        // 前置交易占用额度重新赋值
//                        item.setLastAmount(ArithmeticUtil.subtract(creditLine.getCreditAmt(),creditLine.getHapCreditAmt()));
                        if (StringUtil.equals(item.getCreditOccureType(),DictCodeListRsk.授信占用类别_实占.getValue())){
                            item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                        }
                        // 更新占用额度
                        creditLinesDealsMapper.updateLinesByPrimaryKeySelective(creditLine);
                        // 更新对应的前置交易占用记录
                        creditUsingCustomerMstMapper.updateByPrimaryKeySelective(item);
                    }
                });
            }
        }
    }

    private void generateDealResult(CreditLinesInputDto creditLinesInputDto, CreditLines creditLines, BigDecimal 扣减额度, BigDecimal newAmount,boolean occupy, String occupyType) {
        if (!occupy){
            扣减额度 = 扣减额度.negate();
            newAmount = newAmount.negate();
        }
        // 重新计算剩余占用额度
        reCalcuUsedAmt(creditLines,creditLinesInputDto,扣减额度);
        if (!isSave){
            return;
        }
        if (StringUtil.equals(BussinessDictCodeList.授信配置代码_机构授信.getValue(),creditLines.getCreditMstId())){
            CreditUsingCustomer item = new CreditUsingCustomer();
            item.setSeqno(IDGenerator.generate());
            item.setCustomerId(creditLines.getCustomerId());
            item.setCdate(creditLinesInputDto.getTradeDate());
            // 记录交易实际值，用于报表统计
            item.setLastAmount(occupy ? compareAmtOrg : compareAmtOrg.negate());
            item.setAmount(扣减额度);
            // 用于交易撤销，恢复前置交易占用时，判断最多可恢复到的占用额度
            item.setAmountAdd(扣减额度);
            item.setNewAmount(newAmount);
            item.setTradeId(creditLinesInputDto.getTradeId());
            item.setFinprodId(creditLinesInputDto.getFinprodId());
            item.setBranch(0L);
            item.setIsBottom(creditLinesInputDto.getIsBottom() == null ? DictCodeList.是否_否.getValue() : creditLinesInputDto.getIsBottom());
            item.setCreditLinesId(creditLines.getSeqno());
            item.setCreditOccureType(occupyType);
            item.setStatus(DictCodeListExt1.交易状态_初始.getValue());
            item.setEntrustId(creditLinesInputDto.getEntrustId());
            item.setpFinprodId(creditLinesInputDto.getpFinprodId());
            this.creditUsingCustomerList.add(item);
        }else if (StringUtil.equals(BussinessDictCodeList.授信配置代码_资产授信.getValue(),creditLines.getCreditMstId())){
            CreditUsingAst item = new CreditUsingAst();
            item.setSeqno(IDGenerator.generate());
            item.setFinprodId(creditLinesInputDto.getFinprodId());
            item.setBranch(0L);
            item.setCdate(creditLinesInputDto.getTradeDate());
            item.setLastAmount(occupy ? compareAmtAst : compareAmtAst.negate());
            item.setAmount(扣减额度);
            // 用于交易撤销，恢复前置交易占用时，判断最多可恢复到的占用额度
            item.setAmountAdd(扣减额度);
            item.setNewAmount(newAmount);
            item.setTradeId(creditLinesInputDto.getTradeId());
            item.setIsBottom(creditLinesInputDto.getIsBottom() == null ? DictCodeList.是否_否.getValue() : creditLinesInputDto.getIsBottom());
            item.setCreditLinesId(creditLines.getSeqno());
            item.setCreditOccureType(occupyType);
            item.setEntrustId(creditLinesInputDto.getEntrustId());
            item.setNetPrice(creditLinesInputDto.getNetPrice());
            item.setShareAmt(creditLinesInputDto.getShareAmt());
            item.setStatus(DictCodeListExt1.交易状态_初始.getValue());
            item.setPortfolioId(creditLinesInputDto.getPortfolioId());
            item.setpFinprodId(creditLinesInputDto.getpFinprodId());
            this.creditUsingAstList.add(item);
        }else if (StringUtil.equals(BussinessDictCodeList.授信配置代码_系列授信.getValue(),creditLines.getCreditMstId())){
            CreditUsingSeries item = new CreditUsingSeries();
            item.setSeqno(IDGenerator.generate());
            item.setAstSeriesId(creditLines.getCustomerId());
            item.setCdate(creditLinesInputDto.getTradeDate());
            item.setLastAmount(occupy ? compareAmtAst : compareAmtAst.negate());
            item.setAmount(扣减额度);
            // 用于交易撤销，恢复前置交易占用时，判断最多可恢复到的占用额度
            item.setAmountAdd(扣减额度);
            item.setNewAmount(newAmount);
            item.setTradeId(creditLinesInputDto.getTradeId());
            item.setFinprodId(creditLinesInputDto.getFinprodId());
            item.setBranch(0L);
            item.setIsBottom(creditLinesInputDto.getIsBottom() == null ? DictCodeList.是否_否.getValue() : creditLinesInputDto.getIsBottom());
            item.setCreditLinesId(creditLines.getSeqno());
            item.setCreditOccureType(occupyType);
            item.setEntrustId(creditLinesInputDto.getEntrustId());
            item.setNetPrice(creditLinesInputDto.getNetPrice());
            item.setShareAmt(creditLinesInputDto.getShareAmt());
            item.setStatus(DictCodeListExt1.交易状态_初始.getValue());
            item.setPortfolioId(creditLinesInputDto.getPortfolioId());
            item.setpFinprodId(creditLinesInputDto.getpFinprodId());
            this.creditUsingSeriesList.add(item);
        }else {
            CreditUsingCustomerMst item = new CreditUsingCustomerMst();
            item.setSeqno(IDGenerator.generate());
            item.setCustomerId(creditLines.getCustomerId());
            item.setCreditMstId(creditLines.getCreditMstId());
            item.setCdate(creditLinesInputDto.getTradeDate());
            item.setLastAmount(occupy ? compareAmtOrg : compareAmtOrg.negate());
            item.setAmount(扣减额度);
            // 用于交易撤销，恢复前置交易占用时，判断最多可恢复到的占用额度
            item.setAmountAdd(扣减额度);
            item.setNewAmount(newAmount);
            item.setTradeId(creditLinesInputDto.getTradeId());
            item.setFinprodId(creditLinesInputDto.getFinprodId());
            item.setBranch(0L);
            item.setIsBottom(creditLinesInputDto.getIsBottom() == null ? DictCodeList.是否_否.getValue() : creditLinesInputDto.getIsBottom());
            item.setCreditLinesId(creditLines.getSeqno());
            item.setCreditOccureType(occupyType);
            item.setEntrustId(creditLinesInputDto.getEntrustId());
            item.setStatus(DictCodeListExt1.交易状态_初始.getValue());
            item.setpFinprodId(creditLinesInputDto.getpFinprodId());
            this.creditUsingCustomerMstList.add(item);
        }
    }

    private boolean isOccupy(CreditLinesInputDto creditLinesInputDto) {
        //b.根据交易方向&交易状态 判断占用/释放交易方向_卖出
        String[] arrps = {DictCodeList.交易方向_卖出.getValue(), DictCodeList.交易方向_行权_回售.getValue(),
                DictCodeList.交易方向_赎回确认.getValue(), DictCodeList.交易方向_赎回到账.getValue(), DictCodeList.交易方向_提前支取.getValue(),
                DictCodeList.交易方向_行权_赎回.getValue(), DictCodeList.交易方向_还本.getValue(), DictCodeList.交易方向_到期.getValue()
                ,DictCodeListExt3.交易方向_投资端_提前终止.getValue()};
        String[] arrps1 = {DictCodeList.交易方向_买入.getValue(), DictCodeList.交易方向_申购.getValue(),
                DictCodeList.交易方向_申购确认.getValue(),DictCodeListExt2.交易方向_认购.getValue()};
        boolean flagps = Arrays.asList(arrps).contains(creditLinesInputDto.getPs());
        boolean flagps1 = Arrays.asList(arrps1).contains(creditLinesInputDto.getPs());

        //交易方向为卖出初始状态时 或者买入撤销时候 要退回原占用的额度；交易方向为买入撤销时要释放原占用的额度  edit by xkni 20190830
        if((DictCodeListExt1.交易状态_初始.getValue().equals(creditLinesInputDto.getTradeStatus()==null? DictCodeListExt1.交易状态_初始.getValue() : creditLinesInputDto.getTradeStatus()) && flagps )
                ||(DictCodeListExt1.交易状态_撤销.getValue().equals(creditLinesInputDto.getTradeStatus()) && flagps1)){
            //待处理额度=待处理额度*-1(释放原占用)
            return false;
        }
        return true;
    }
    /**
     *  查询资产授信，资产系列授信
     * @param creditLinesInputDto
     */
    private CreditLines getAssetsCeLineList(CreditLinesInputDto creditLinesInputDto) {

        List<CreditLines> creditLinesList;
        Map<String,Object> mapParm = new HashMap<>();
        mapParm.put("finprodId",creditLinesInputDto.getFinprodId() == null ? "" : creditLinesInputDto.getFinprodId());
        mapParm.put("tradeDate",creditLinesInputDto.getTradeDate() == null ? iPlatformDate.getSysDate() : creditLinesInputDto.getTradeDate());
        mapParm.put("creditmstidZC", BussinessDictCodeList.授信配置代码_资产授信.getValue());
        mapParm.put("creditmstidXL", BussinessDictCodeList.授信配置代码_系列授信.getValue());
        // 先查询资产授信
        creditLinesList = creditLinesDealsMapper.getAssetCeLineList(mapParm);
        // 有资产授信直接返回
        if (!StringUtil.isEmptyList(creditLinesList)){
            return getCreditLines(creditLinesList.get(0));
        }
        // 无资产授信时，查询资产系列授信
        creditLinesList = creditLinesDealsMapper.getAssetSeriesCeLineList(mapParm);
        if(!StringUtil.isEmptyList(creditLinesList)){
            return getCreditLines(creditLinesList.get(0));
        }
        // 既不是资产授信，也不是资产系列授信，后续走机构授信
        return null;
    }

    /**
     * 处理指令关闭、交易撤销等方式导致的预占或实占的释放
     * 采用删除占用记录的方式，同时需要更新授信额度表的占用金额
     * @param inputDto
     * @param outputDto
     */
    private void revokeCreditLinesOccupy(CreditLinesInputDto inputDto, CreditLinesOutputDto outputDto) {
        if (StringUtil.isEmpty(inputDto.getTradeId())){
            return;
        }
        if (StringUtil.equals(inputDto.getCreditCheckType(),DictCodeListRsk.授信占用来源_接口.getValue())
            && (releaseAmtAst == null || releaseAmtAst.compareTo(BigDecimal.ZERO) == 0)
            && (releaseAmtOrg == null || releaseAmtOrg.compareTo(BigDecimal.ZERO) == 0)){
            logger.info(getMessage("==========场内交易撤销，无金额数据，无法判断是否是部分撤销!"));
            return;
        }
        // 如果是交易撤销，交易编号可能会有后缀“R”
        if (inputDto.getTradeId().endsWith("R")){
            inputDto.setTradeId(inputDto.getTradeId().substring(0,inputDto.getTradeId().length() - 1));
        }
        List<CreditUsingSeries> 系列额度占用明细 = creditLinesDealsMapper.findCreditUsingSeries(inputDto);
        List<CreditUsingAst> 资产额度占用明细 = creditLinesDealsMapper.getCreditUsingAstList(inputDto);
        List<CreditUsingCustomer> 机构额度占用明细 = creditLinesDealsMapper.getCreditUsingCustomerList(inputDto);
        List<CreditUsingCustomerMst> 机构分项占用明细 = creditLinesDealsMapper.getCreditUsingCustomerMsts(inputDto);

        if(!StringUtil.isEmptyList(机构额度占用明细)){
            机构额度占用明细.forEach(item->{
                if (creditLinesOrg != null){
                    outputDto.setCustomerId(creditLinesOrg.getCustomerId());
                    // 更新对应的交易占用记录为撤销状态
                    // 通过三合一接口调用，并且撤销金额和占用金额不相等，视为部分撤销
                    BigDecimal revokeAmt;
                    if (StringUtil.equals(inputDto.getCreditCheckType(),DictCodeListRsk.授信占用来源_接口.getValue())
                            && compareAmtOrg != null  && compareAmtOrg.compareTo(item.getLastAmount().abs()) <= 0){
                        // 部撤额度小于0 时 终止处理
                        if (releaseAmtOrg.compareTo(BigDecimal.ZERO) <= 0){
                            return;
                        }
                        // 部撤额度 >= 当前循环占用额度时，当前循环的占用记录失效，并记录撤销的机构额度
                        if (releaseAmtOrg.compareTo(item.getNewAmount().abs()) >= 0){
                            item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                            reCalcuUsedAmt(creditLinesOrg,inputDto,item.getAmount().negate());
                            outputDto.setOccupyAmtOrg(outputDto.getOccupyAmtOrg().add(item.getAmount().negate()));
                            revokeAmt = item.getNewAmount();
                        }
                        // 部撤额度 < 当前循环占用额度时，当前循环的占用额度更新，并记录撤销的机构额度
                        else {
                            item.setNewAmount(item.getNewAmount().subtract(occupy ? releaseAmtOrg : releaseAmtOrg.negate()));
                            if (occupy || StringUtil.equals(creditLinesOrg.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
                                item.setAmount(item.getNewAmount());
                                reCalcuUsedAmt(creditLinesOrg,inputDto,occupy ? releaseAmtOrg.negate(): releaseAmtOrg);
                                outputDto.setOccupyAmtOrg(outputDto.getOccupyAmtOrg().add(occupy ? releaseAmtOrg.negate(): releaseAmtOrg));
                            }
                            revokeAmt = occupy ? releaseAmtOrg: releaseAmtOrg.negate();
                        }
                    }
                    // 全部撤销时，当前循环的占用记录失效，并记录撤销的机构额度
                    else {
                        item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                        reCalcuUsedAmt(creditLinesOrg,inputDto,item.getAmount().negate());
                        outputDto.setOccupyAmtOrg(item.getAmount().negate());
                        revokeAmt = item.getNewAmount();
                    }
                    if (isSave){
                        // 更新占用额度
                        creditLinesDealsMapper.updateLinesByPrimaryKeySelective(creditLinesOrg);
                        creditUsingCustomerMapper.updateByPrimaryKeySelective(item);
                        if (StringUtil.equals(inputDto.getFinprodType2(),"ManualSettle")){
                            return;
                        }
                        // 更新前置交易占用额度和状态
                        if (StringUtil.isNotEmpty(item.getEntrustId())){
                            List<CreditUsingCustomer> list = creditLinesDealsMapper.findCreditUsingCustomerByPre(item.getEntrustId());
                            boolean first = true;
                            for (CreditUsingCustomer usingCustomer : list){
                                CreditLines lines = creditLinesMapper.selectByPrimaryKey(usingCustomer.getCreditLinesId());
                                if (first && StringUtil.equals(usingCustomer.getCreditOccureType(),DictCodeListRsk.授信占用类别_预占.getValue())){
                                    usingCustomer.setStatus(DictCodeListExt1.交易状态_初始.getValue());
                                    first = false;
                                }
                                BigDecimal amt = usingCustomer.getNewAmount().add(revokeAmt);
                                usingCustomer.setNewAmount(amt.abs().compareTo(usingCustomer.getAmountAdd().abs()) <= 0 ? amt : usingCustomer.getAmountAdd());
                                if (occupy || (lines != null && StringUtil.equals(lines.getIsLoopAmt(),DictCodeList.是否_是.getValue()))){
                                    usingCustomer.setAmount(usingCustomer.getNewAmount());
                                }
                                if (first && StringUtil.equals(usingCustomer.getCreditOccureType(),DictCodeListRsk.授信占用类别_实占.getValue())
                                        && StringUtil.equals(usingCustomer.getStatus(),DictCodeListExt1.交易状态_撤销.getValue())){
                                    usingCustomer.setStatus(DictCodeListExt1.交易状态_初始.getValue());
                                    usingCustomer.setAmount(usingCustomer.getAmountAdd());
                                    first = false;
                                }
                                creditUsingCustomerMapper.updateByPrimaryKeySelective(usingCustomer);
                            }
                        }
                    }
                }

            });

        }
        if(!StringUtil.isEmptyList(机构分项占用明细)){
            机构分项占用明细.forEach(item->{
                if (creditLinesOrgItem != null){
                    // 更新对应的交易占用记录为撤销状态
                    // 通过三合一接口调用，并且撤销金额和占用金额不相等，视为部分撤销
                    BigDecimal revokeAmt;
                    if (StringUtil.equals(inputDto.getCreditCheckType(),DictCodeListRsk.授信占用来源_接口.getValue())
                            && compareAmtOrg != null  && compareAmtOrg.compareTo(item.getLastAmount().abs()) <= 0){
                        // 部撤额度小于0 时 终止处理
                        if (releaseAmtOrg.compareTo(BigDecimal.ZERO) <= 0){
                            return;
                        }
                        // 部撤额度 >= 当前循环占用额度时，当前循环的占用记录失效，并记录撤销的机构额度
                        if (releaseAmtOrg.compareTo(item.getNewAmount().abs()) >= 0){
                            item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                            reCalcuUsedAmt(creditLinesOrgItem,inputDto,item.getAmount().negate());
                            revokeAmt = item.getNewAmount();
                        }
                        // 部撤额度 < 当前循环占用额度时，当前循环的占用额度更新，并记录撤销的机构额度
                        else {
                            item.setNewAmount(item.getNewAmount().subtract(occupy ? releaseAmtOrg : releaseAmtOrg.negate()));
                            if (occupy || StringUtil.equals(creditLinesOrgItem.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
                                item.setAmount(item.getNewAmount());
                                reCalcuUsedAmt(creditLinesOrgItem,inputDto,occupy ? releaseAmtOrg.negate(): releaseAmtOrg);
                            }
                            revokeAmt = occupy ? releaseAmtOrg: releaseAmtOrg.negate();
                        }
                    }
                    // 全部撤销时，当前循环的占用记录失效，并记录撤销的机构额度
                    else {
                        item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                        reCalcuUsedAmt(creditLinesOrgItem,inputDto,item.getAmount().negate());
                        revokeAmt = item.getNewAmount();
                    }
                    if (isSave){
                        // 更新占用额度
                        creditLinesDealsMapper.updateLinesByPrimaryKeySelective(creditLinesOrgItem);
                        creditUsingCustomerMstMapper.updateByPrimaryKeySelective(item);
                        if (StringUtil.equals(inputDto.getFinprodType2(),"ManualSettle")){
                            return;
                        }
                        // 更新前置交易占用额度
                        if (StringUtil.isNotEmpty(item.getEntrustId())){
                            List<CreditUsingCustomerMst> list = creditLinesDealsMapper.findCreditUsingCustomerMstByPre(item.getEntrustId());
                            boolean first = true;
                            for (CreditUsingCustomerMst usingCustomerMst : list){
                                CreditLines lines = creditLinesMapper.selectByPrimaryKey(usingCustomerMst.getCreditLinesId());
                                if (first && StringUtil.equals(usingCustomerMst.getCreditOccureType(),DictCodeListRsk.授信占用类别_预占.getValue())){
                                    usingCustomerMst.setStatus(DictCodeListExt1.交易状态_初始.getValue());
                                    first = false;
                                }
                                BigDecimal amt = usingCustomerMst.getNewAmount().add(revokeAmt);
                                usingCustomerMst.setNewAmount(amt.abs().compareTo(usingCustomerMst.getAmountAdd().abs()) <= 0 ? amt : usingCustomerMst.getAmountAdd());
                                if (occupy || (lines != null && StringUtil.equals(lines.getIsLoopAmt(),DictCodeList.是否_是.getValue()))){
                                    usingCustomerMst.setAmount(usingCustomerMst.getNewAmount());
                                }
                                if (first && StringUtil.equals(usingCustomerMst.getCreditOccureType(),DictCodeListRsk.授信占用类别_实占.getValue())
                                        && StringUtil.equals(usingCustomerMst.getStatus(),DictCodeListExt1.交易状态_撤销.getValue())){
                                    usingCustomerMst.setStatus(DictCodeListExt1.交易状态_初始.getValue());
                                    usingCustomerMst.setAmount(usingCustomerMst.getAmountAdd());
                                    first = false;
                                }
                                creditUsingCustomerMstMapper.updateByPrimaryKeySelective(usingCustomerMst);
                            }
                            String creditType = creditLinesDealsMapper.getCreditType(item.getCreditMstId());
                            if (StringUtil.equals(creditType,DictCodeListRsk.额度分类_非法人机构.getValue())){
                                List<CreditUsingCustomer> list1 = creditLinesDealsMapper.findCreditUsingCustomerByPre(item.getEntrustId());
                                boolean first1 = true;
                                for (CreditUsingCustomer usingCustomer : list1){
                                    CreditLines lines = creditLinesMapper.selectByPrimaryKey(usingCustomer.getCreditLinesId());
                                    if (first1 && StringUtil.equals(usingCustomer.getCreditOccureType(),DictCodeListRsk.授信占用类别_预占.getValue())){
                                        usingCustomer.setStatus(DictCodeListExt1.交易状态_初始.getValue());
                                        BigDecimal amt = usingCustomer.getNewAmount().add(revokeAmt);
                                        usingCustomer.setNewAmount(amt.abs().compareTo(usingCustomer.getAmountAdd().abs()) <= 0 ? amt : usingCustomer.getAmountAdd());
                                        if (occupy || StringUtil.equals(lines.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
                                            usingCustomer.setAmount(usingCustomer.getNewAmount());
                                        }
                                        creditUsingCustomerMapper.updateByPrimaryKeySelective(usingCustomer);
                                        first1 = false;
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
        // 待撤销的资产授信额度 = 原始撤销额度 - 机构撤销额度
//        if (StringUtil.equals(inputDto.getCreditCheckType(),DictCodeListRsk.授信占用来源_接口.getValue())){
//            releaseAmt = releaseAmt.subtract(outputDto.getOccupyAmtOrg().abs());
//        }

        if(!StringUtil.isEmptyList(系列额度占用明细)){
            系列额度占用明细.forEach(item->{
                if (creditLinesAst != null){
                    // 更新对应的交易占用记录为撤销状态
                    // 通过三合一接口调用，并且撤销金额和占用金额不相等，视为部分撤销
                    BigDecimal shareAmt;
                    BigDecimal revokeAmt;
                    if (StringUtil.equals(inputDto.getCreditCheckType(),DictCodeListRsk.授信占用来源_接口.getValue())
                        && compareAmtAst != null  && compareAmtAst.compareTo(item.getLastAmount().abs()) <= 0){
                        // 部撤额度小于0 时 终止处理
                        if (releaseAmtAst.compareTo(BigDecimal.ZERO) <= 0){
                            return;
                        }
                        // 部撤额度 >= 当前循环占用额度时，当前循环的占用记录失效
                        if (releaseAmtAst.compareTo(item.getNewAmount().abs()) >= 0){
                            releaseAmtAst = releaseAmtAst.subtract(item.getNewAmount().abs());
                            shareAmt = item.getShareAmt();
                            item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                            reCalcuUsedAmt(creditLinesAst,inputDto,item.getAmount().negate());
                            revokeAmt = item.getNewAmount();
                        }
                        // 部撤额度 < 当前循环占用额度时，当前循环的占用额度更新
                        else {
                            shareAmt = releaseAmtAst.divide(item.getNetPrice(),2,RoundingMode.HALF_UP);
                            item.setNewAmount(item.getNewAmount().subtract(occupy ? releaseAmtAst : releaseAmtAst.negate()));
                            if (occupy || StringUtil.equals(creditLinesAst.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
                                item.setAmount(item.getNewAmount());
                                reCalcuUsedAmt(creditLinesAst,inputDto,occupy ? releaseAmtAst.negate(): releaseAmtAst);
                            }
                            revokeAmt = occupy ? releaseAmtAst : releaseAmtAst.negate();
                            releaseAmtAst = BigDecimal.ZERO;
                        }
                    }
                    // 全部撤销时，当前循环的占用记录失效，并记录撤销的机构额度
                    else {
                        item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                        shareAmt = item.getShareAmt();
                        reCalcuUsedAmt(creditLinesAst,inputDto,item.getAmount().negate());
                        revokeAmt = item.getNewAmount();
                    }
                    if (isSave){
                        // 更新占用额度
                        creditLinesDealsMapper.updateLinesByPrimaryKeySelective(creditLinesAst);
                        creditUsingSeriesMapper.updateByPrimaryKeySelective(item);
                        if (StringUtil.equals(inputDto.getFinprodType2(),"ManualSettle")){
                            return;
                        }
                        // 更新前置交易占用额度
                        if (StringUtil.isNotEmpty(item.getEntrustId())){
                            List<CreditUsingSeries> list = creditLinesDealsMapper.findCreditUsingSeriesByPre(item.getEntrustId());
                            boolean first = true;
                            for (CreditUsingSeries usingSeries : list){
                                // 前置交易是预占时，需要更新前置交易占用额度
                                if (first && StringUtil.equals(usingSeries.getCreditOccureType(),DictCodeListRsk.授信占用类别_预占.getValue())){
                                    BigDecimal amt = usingSeries.getNewAmount().add(revokeAmt);
                                    usingSeries.setNewAmount(amt.abs().compareTo(usingSeries.getAmountAdd().abs()) <= 0 ? amt : usingSeries.getAmountAdd());
                                    if (occupy || StringUtil.equals(creditLinesAst.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
                                        usingSeries.setAmount(usingSeries.getNewAmount());
                                    }
                                    usingSeries.setStatus(DictCodeListExt1.交易状态_初始.getValue());
                                    first = false;
                                }
                                if (first && StringUtil.equals(usingSeries.getCreditOccureType(),DictCodeListRsk.授信占用类别_实占.getValue())
                                    && StringUtil.equals(usingSeries.getStatus(),DictCodeListExt1.交易状态_撤销.getValue())){
                                    usingSeries.setStatus(DictCodeListExt1.交易状态_初始.getValue());
                                    usingSeries.setAmount(usingSeries.getAmountAdd());
                                    first = false;
                                }
                                usingSeries.setShareAmt(ArithmeticUtil.doAdd(usingSeries.getShareAmt(),shareAmt));
                                creditUsingSeriesMapper.updateByPrimaryKeySelective(usingSeries);
                            }
                        }

                    }
                }
            });
        }
        if(!StringUtil.isEmptyList(资产额度占用明细)){
            资产额度占用明细.forEach(item->{
                if (creditLinesAst != null){
                    // 更新对应的交易占用记录为撤销状态
                    // 通过三合一接口调用，并且撤销金额和占用金额不相等，视为部分撤销
                    BigDecimal shareAmt = BigDecimal.ZERO;
                    BigDecimal revokeAmt;
                    if (StringUtil.equals(inputDto.getCreditCheckType(),DictCodeListRsk.授信占用来源_接口.getValue())
                            && compareAmtAst != null  && compareAmtAst.compareTo(item.getLastAmount().abs()) <= 0){
                        // 部撤额度小于0 时 终止处理
                        if (releaseAmtAst.compareTo(BigDecimal.ZERO) <= 0){
                            return;
                        }
                        // 部撤额度 >= 当前循环占用额度时，当前循环的占用记录失效
                        if (releaseAmtAst.compareTo(item.getNewAmount().abs()) >= 0){
                            releaseAmtAst = releaseAmtAst.subtract(item.getNewAmount().abs());
                            shareAmt = item.getShareAmt();
                            item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                            reCalcuUsedAmt(creditLinesAst,inputDto,item.getAmount().negate());
                            revokeAmt = item.getNewAmount();
                        }
                        // 部撤额度 < 当前循环占用额度时，当前循环的占用额度更新
                        else {
                            shareAmt = releaseAmtAst.divide(item.getNetPrice(),2,RoundingMode.HALF_UP);
                            item.setNewAmount(item.getNewAmount().subtract(occupy ? releaseAmtAst : releaseAmtAst.negate()));
                            if (occupy || StringUtil.equals(creditLinesAst.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
                                item.setAmount(item.getNewAmount());
                                reCalcuUsedAmt(creditLinesAst,inputDto,occupy ? releaseAmtAst.negate(): releaseAmtAst);
                            }
                            revokeAmt = occupy ? releaseAmtAst : releaseAmtAst.negate();
                            releaseAmtAst = BigDecimal.ZERO;
                        }
                    }
                    // 全部撤销时，当前循环的占用记录失效，并记录撤销的机构额度
                    else {
                        item.setStatus(DictCodeListExt1.交易状态_撤销.getValue());
                        shareAmt = item.getShareAmt();
                        reCalcuUsedAmt(creditLinesAst,inputDto,item.getAmount().negate());
                        revokeAmt = item.getNewAmount();
                    }
                    if (isSave){
                        // 更新占用额度
                        creditLinesDealsMapper.updateLinesByPrimaryKeySelective(creditLinesAst);
                        creditUsingAstMapper.updateByPrimaryKeySelective(item);
                        // 手工交收失败，不恢复前置预占
                        if (StringUtil.equals(inputDto.getFinprodType2(),"ManualSettle")){
                            return;
                        }
                        // 更新前置交易占用额度
                        if (StringUtil.isNotEmpty(item.getEntrustId())){
                            List<CreditUsingAst> list = creditLinesDealsMapper.findCreditUsingAstByPre(item.getEntrustId());
                            boolean first = true;
                            for (CreditUsingAst usingAst : list){
                                // 前置交易是预占时，需要更新前置交易占用额度
                                if (first && StringUtil.equals(usingAst.getCreditOccureType(),DictCodeListRsk.授信占用类别_预占.getValue())){
                                    BigDecimal amt = usingAst.getNewAmount().add(revokeAmt);
                                    usingAst.setNewAmount(amt.abs().compareTo(usingAst.getAmountAdd().abs()) <= 0 ? amt : usingAst.getAmountAdd());
                                    if (occupy || StringUtil.equals(creditLinesAst.getIsLoopAmt(),DictCodeList.是否_是.getValue())){
                                        usingAst.setAmount(usingAst.getNewAmount());
                                    }
                                    usingAst.setStatus(DictCodeListExt1.交易状态_初始.getValue());
                                    first = false;
                                }
                                usingAst.setShareAmt(ArithmeticUtil.doAdd(usingAst.getShareAmt(),shareAmt));
                                if (first && StringUtil.equals(usingAst.getCreditOccureType(),DictCodeListRsk.授信占用类别_实占.getValue())
                                        && StringUtil.equals(usingAst.getStatus(),DictCodeListExt1.交易状态_撤销.getValue())){
                                    usingAst.setStatus(DictCodeListExt1.交易状态_初始.getValue());
                                    usingAst.setAmount(usingAst.getAmountAdd());
                                    first = false;
                                }
                                creditUsingAstMapper.updateByPrimaryKeySelective(usingAst);
                            }
                        }
                    }
                }
            });
        }
        // 可转债申购确认交易 撤销时，所有申购环节占用记录更新成有效
        if (isSave && StringUtil.equals(inputDto.getFinprodType2(),DictCodeListExt2.共通业务类型_可转债申购交易.getValue())){
            List<CreditUsingCustomer> listCust = creditLinesDealsMapper.findCreditUsingCustomerByPre(inputDto.getEntrustId());
            List<CreditUsingCustomerMst> listMst = creditLinesDealsMapper.findCreditUsingCustomerMstByPre(inputDto.getEntrustId());
            List<CreditUsingAst> listAst = creditLinesDealsMapper.findCreditUsingAstByPre(inputDto.getEntrustId());
            List<CreditUsingSeries> listSeries = creditLinesDealsMapper.findCreditUsingSeriesByPre(inputDto.getEntrustId());

            CreditUsingCustomer customer = listCust.size() > 0 ? listCust.get(0) : null;
            if (customer != null){
                customer.setStatus(DictCodeListExt1.交易状态_初始.getValue());
                creditUsingCustomerMapper.updateByPrimaryKeySelective(customer);
            }

            CreditUsingCustomerMst customerMst = listMst.size() > 0 ? listMst.get(0) : null;
            if (customerMst != null){
                customerMst.setStatus(DictCodeListExt1.交易状态_初始.getValue());
                creditUsingCustomerMstMapper.updateByPrimaryKeySelective(customerMst);
            }

            CreditUsingAst ast = listAst.size() > 0 ? listAst.get(0) : null;
            if (ast != null){
                ast.setStatus(DictCodeListExt1.交易状态_初始.getValue());
                creditUsingAstMapper.updateByPrimaryKeySelective(ast);
            }
            CreditUsingSeries series = listSeries.size() > 0 ? listSeries.get(0) : null;
            if (series != null){
                series.setStatus(DictCodeListExt1.交易状态_初始.getValue());
                creditUsingSeriesMapper.updateByPrimaryKeySelective(series);
            }

        }
    }

    /**
     * 重新计算占用额度
     * @param creditLines
     * @param inputDto
     * @param amount
     */
    private void reCalcuUsedAmt(CreditLines creditLines, CreditLinesInputDto inputDto, BigDecimal amount) {
        // 重新计算授信额度占用
        BigDecimal usedAmt = getAmount(creditLines.getHapCreditAmt()).add(getAmount(amount));
        // 占用额度最大只能是核定额度，最小为0
        if (usedAmt.compareTo(BigDecimal.ZERO) < 0){
            creditLines.setHapCreditAmt(BigDecimal.ZERO);
        }else if (usedAmt.compareTo(getAmount(creditLines.getCreditAmt())) > 0){
            creditLines.setHapCreditAmt(getAmount(creditLines.getCreditAmt()));
        }else {
            creditLines.setHapCreditAmt(usedAmt);
        }
        creditLines.setUpdateUser(inputDto.getUpdateUser());
        creditLines.setUpdateTime(inputDto.getUpdateTime());
    }

    private BigDecimal getAmount(BigDecimal amount) {
        if (amount == null){
            return BigDecimal.ZERO;
        }
        return amount;
    }

    private CreditLinesOutputDto dealEachTradeInfo(CreditLinesInputDto inputDto, String occupyType) {
        // 内部交易时不影响授信
        if (StringUtil.isNotEmpty(inputDto.getCounterId())){
            int count = creditLinesDealsMapper.isNbDeal(inputDto.getCounterId(), DictCodeListExt1.交易对手类型_内部交易对手.getValue());
            if(count > 0) {
                return null;
            }
        }
        // TISHKS-947:子期次占用母计划额度 modify by fuyongsheng 20220506 start
        if(StringUtil.isNotEmpty(inputDto.getFinprodType2()) && DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(
                inputDto.getFinprodType2())){
            String finprodId = creditLinesDealsMapper.getJHLFinprodId(inputDto.getFinprodId());
            inputDto.setFinprodId(finprodId);
        }
        // TISHKS-947:子期次占用母计划额度 modify by fuyongsheng 20220506 end
        // 处理结果记录，输出参数实体
        CreditLinesOutputDto outputDto = new CreditLinesOutputDto();
        // STEP  1 处理基础数据（资产授信数据，额度校验规则、机构授信数据）
        findBasicData(inputDto);
        if(StringUtil.isNotEmpty(inputDto.getZhiYaQuan())){
            inputDto.setFinprodId(null);
        }
        if (creditLinesAst == null && (oneStepInfo != null && oneStepInfo.getDeductObj() == null || creditLinesOrg == null && creditLinesOrgItem == null)){
            return outputDto;
        }
        //处理 当授信对象为金额时 申购/认购 的待释放份额为0
        if (creditLinesAst!=null
                && (BussinessDictCodeList.授信对象_金额.getValue().equals(creditLinesAst.getCreditObj())||"03".equals(creditLinesAst.getCreditType()))
                && (DictCodeList.交易方向_申购.getValue().equals(inputDto.getPs())
                || DictCodeList.交易方向_认购.getValue().equals(inputDto.getPs()))) {
            inputDto.setShareAmt(BigDecimal.ZERO);
        }
        //处理 净值型、货币型、基金的申购/认购确认
        if(StringUtil.isNotEmpty(inputDto.getIsSpecial()) && "true".equals(inputDto.getIsSpecial())){
            if (creditLinesAst!=null && BussinessDictCodeList.授信对象_份额.getValue().equals(creditLinesAst.getCreditObj())) {
                if("true".equals(inputDto.getIsjijinmairu()))return outputDto;
                BigDecimal yuanShareAmt = creditLinesDealsMapper.getShareAmt(inputDto.getEntrustId());
                BigDecimal objShareAmt = inputDto.getShareAmt().subtract(yuanShareAmt);
                inputDto.setShareAmt(objShareAmt.abs());
                inputDto.setTradeAmt(BigDecimal.ZERO);
                occupy=objShareAmt.compareTo(BigDecimal.ZERO)>0?true:false;
            }else if(StringUtil.equals(BussinessConstant.RELEASE_MODE_01,CacheUtil.getSysParam(BussinessConstant.RELEASE_MODE))){
                inputDto.setTradeAmt(BigDecimal.ZERO);
            }else{
                return outputDto;
            }
            inputDto.setEntrustId(null);
        }
        // STEP  2 计算占用额度
        cal占用额度(inputDto);
        // STEP  3 判断初始交易 or 撤销
        // STEP  3.1 撤销 交易撤销或者指令关闭
        if (StringUtil.equals(inputDto.getTradeStatus(), DictCodeListExt1.交易状态_撤销.getValue())){
            logger.info(getMessage("==========开始处理数据,交易或指令编号: "+inputDto.getTradeId()+",交易撤销或者指令关闭=========="));
            revokeCreditLinesOccupy(inputDto,outputDto);
            logger.info(getMessage("==========处理数据结束,交易或指令编号: " +inputDto.getTradeId()+ " ,交易撤销或者指令关闭成功=========="));
        }else {
            // 占用
            if (occupy){
                creditLinesOccupy(inputDto,occupyType,outputDto);
                // 释放
            }else {
                creditLinesRelease(inputDto,occupyType,outputDto);
            }
        }
        return outputDto;
    }

    @Override
    public List<ValidMessage> valid(Object o) {
        return null;
    }

    @Override
    public List<Map<String, Object>> calcRelatedOccupyAmt(List<Map<String, Object>> mapList) {
        try {
            for (Map<String, Object> map : mapList){
                CreditLinesInputDto inputDto = JSONObject.parseObject(JSONObject.toJSONString(map), CreditLinesInputDto.class);
                // 调用接口
                inputDto.setTradeDate(iPlatformDate.getSysDate());
                CreditLinesOutputDto outputDto = dealEachTradeInfo(inputDto,null);
                if (outputDto == null){
                    continue;
                }
                map.put("amount",outputDto.getOccupyAmtOrg());
                map.put("customerId",outputDto.getCustomerId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mapList;
    }

    @Override
    public List<CreditLines> findCreditLinesCondition(CreditLinesInputDto inputDto) {
        try {
            // 内部交易时不影响授信
            if (StringUtil.isNotEmpty(inputDto.getCounterId())){
                int count = creditLinesDealsMapper.isNbDeal(inputDto.getCounterId(), DictCodeListExt1.交易对手类型_内部交易对手.getValue());
                if(count > 0) {
                    return null;
                }
            }
            // STEP  1 处理基础数据（资产授信数据，额度校验规则、机构授信数据）
            findBasicData(inputDto);
        }catch (Exception e){
            e.printStackTrace();
        }
        return getCreditLinesList();
    }

    public void buildRtnResult(){
        if (StringUtil.isEmptyList(creditLinesOutputDtoList)){
            return;
        }
        // 资产授信占用明细 转map
        Map<String,CreditUsingAst> usingAstMap
                = creditUsingAstList.stream().collect(Collectors.toMap(CreditUsingAst::getTradeId, Function.identity()));
        // 资产授信占用明细 转map
        Map<String,CreditUsingSeries> usingSeriesMap
                = creditUsingSeriesList.stream().collect(Collectors.toMap(CreditUsingSeries::getTradeId, Function.identity()));
        // 机构占用明细 转map
        Map<String,CreditUsingCustomerMst> usingCustomerMstMap
                = creditUsingCustomerMstList.stream().collect(Collectors.toMap(CreditUsingCustomerMst::getTradeId, Function.identity()));
        for (CreditLinesOutputDto outputDto:creditLinesOutputDtoList){
            // 资产授信
            CreditUsingAst creditUsingAst = usingAstMap.get(outputDto.getTradeId());
            if (Objects.nonNull(creditUsingAst)){
                // 获取对应的额度信息
                CreditLines creditLines = creditLinesMap.get(creditUsingAst.getCreditLinesId());
                // 资产代码
                outputDto.setAssetCode(creditUsingAst.getFinprodId());
                // 资产授信-占用前
                outputDto.setBeforeOccupyAst(getAmount(creditLines.getHapCreditAmt()).subtract(getAmount(creditUsingAst.getAmount())));
                // 资产授信-占用
                outputDto.setOccupyAssetAmt(creditUsingAst.getAmount());
                // 资产授信-占用后
                outputDto.setAfterOccupyAst(ArithmeticUtil.subtract(creditLines.getCreditAmt(),creditLines.getHapCreditAmt()));
            }
            // 系列授信
            CreditUsingSeries creditUsingSeries = usingSeriesMap.get(outputDto.getTradeId());
            if (Objects.nonNull(creditUsingSeries)){
                // 获取对应的额度信息
                CreditLines creditLines = creditLinesMap.get(creditUsingSeries.getCreditLinesId());
                // 资产代码
                outputDto.setAssetCode(creditUsingSeries.getFinprodId());
                // 资产授信-占用前
                outputDto.setBeforeOccupyAst(getAmount(creditLines.getHapCreditAmt()).subtract(getAmount(creditUsingSeries.getAmount())));
                // 资产授信-占用
                outputDto.setOccupyAssetAmt(creditUsingSeries.getAmount());
                // 资产授信-占用后
                outputDto.setAfterOccupyAst(ArithmeticUtil.subtract(creditLines.getCreditAmt(),creditLines.getHapCreditAmt()));
            }
            // 机构授信
            CreditUsingCustomerMst creditUsingCustomerMst = usingCustomerMstMap.get(outputDto.getTradeId());
            if (Objects.nonNull(creditUsingCustomerMst)){
                // 获取对应的额度信息
                CreditLines creditLines = creditLinesMap.get(creditUsingCustomerMst.getCreditLinesId());
                // 市场机构代码
                outputDto.setCustomerId(creditUsingCustomerMst.getCustomerId());
                // 机构授信-占用前
                outputDto.setBeforeOccupyOrg(getAmount(creditLines.getHapCreditAmt()).subtract(getAmount(creditUsingCustomerMst.getAmount())));
                // 机构授信-占用
                outputDto.setOccupyAssetAmt(creditUsingCustomerMst.getAmount());
                // 机构授信-占用后
                outputDto.setAfterOccupyAst(ArithmeticUtil.subtract(creditLines.getCreditAmt(),creditLines.getHapCreditAmt()));
            }
            outputDto.setRskStatus(DictCodeListRsk.试算结果_通过.getValue());
            if (StringUtil.isNotEmpty(outputDto.getErrMessage())){
                outputDto.setRskStatus(DictCodeListRsk.试算结果_不通过.getValue());
            }
        }
    }
}
