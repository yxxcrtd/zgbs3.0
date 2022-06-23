/*
 * 文件名：EtruibBondInfo003Service.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月21日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.invest.entrust.etribbondinfo.service;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.SecPrinSchedule;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.shell.AccurIntCalculator;
import com.joyintech.calculation.shell.AccurIntForSecheduleCalculator;
import com.joyintech.calculation.shell.PriceByYieldCalculator;
import com.joyintech.calculation.shell.YieldByPriceCalculator;
import com.joyintech.calculation.shell.param.*;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.EtribBondInfo003Dto;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.FinCashPlanDto;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.SecInfoModelDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 多组合债券指令新建
 *
 * @author 王波
 * @date 2021年01月21日
 * @see EtribBondInfo003Service
 * @since 1.0
 */
@Service
public class EtribBondInfo003Service extends CommonExportService<EtribBondInfo003Dto> implements BaseProcessService<EtribBondInfo003Dto> {

    /**
     * 日期相关
     */
    @Autowired
    private DateCalService dateCalService;
    
    @Autowired
    private FinProductMapper finProductMapper;

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;

    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    @Autowired(required = false)
    private FinProductAddMapper finProductAddMapper;

    @Autowired
    private EtrCommonService etrCommonService;

    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * 组合可用现金和实际现金查询更新组件
     */
    @Autowired(required = false)
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 判断是否含权债
     *
     * @param finprodId 金融产品代码
     * @see
     * @since 1.0
     */
    public boolean isRight(String finprodId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("finprodId", finprodId);

        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/isRight", paramMap,
                Boolean.class);
    }

    public PtlPortfolio findPortfolio(String counterId) {
        return ptlPortfolioMapper.selectByPrimaryKey(counterId);
    }
    /**
     * 获取组合分配相关数据
     *
     * @param param
     * @return
     */
    public Map<String, Object> getPortfolioAllocationValues(Map<String, Object> param) {
        // 获取平台日
        Date tradeDate = DateUtil.platDateFromCache();
        // 交易日历
        String calId = StringUtil.isNotEmpty(param.get("calId"))?(String) param.get("calId"):BussinessDictCodeList.wind交易场所_银行间.getValue();
        Date tradeDateNext = DateUtil.addDay(tradeDate, 1);
        if (StringUtil.isNotEmpty(calId)) {
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(tradeDate)), DateUtil.addDay(DateUtil.parse(DateUtil.format(tradeDate)), 15), calId);
            tradeDateNext = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(tradeDate)), 1, holidayList, "1");
        }
        Map<String, Object> result = new HashMap<>();

        if(!StringUtil.isNotEmpty(param.get("finprodId"))){
            /**
             * 当单选组合时，使用组合的币种
             */
            PtlPortfolio ptlPortfolio = findPortfolio(param.get("portfolioId") + "");
            if(ptlPortfolio != null && StringUtil.isNotEmpty(ptlPortfolio.getCcy())){
                param.put("ccy", ptlPortfolio.getCcy());
            }
        }
        // 组合信息没有币种 导致入参币种为空 取不到现金  现如果为空 默认人民币
        if(!StringUtil.isNotEmpty(param.get("ccy"))){
            param.put("ccy",DictCodeList.币种_人民币_CNY.getValue());
        }
        // 可用现金
        Map<String, Object> cash = etrCommonService.getProAvailableCash(param);
        //组合T日可用现金
        result.put("proAvailableCash",cash.get("cashT"));
        //组合T+1日可用现金
        result.put("proAvailableCashT1", cash.get("cashT1"));
        //托管户T日可用现金
        result.put("secManageAvailablePosition",cash.get("cashT3"));
        //托管户T+1日可用现金
        result.put("secManageAvailablePositionT1", cash.get("cashTT1"));
        //通道可用现金
        if (StringUtil.isNotEmpty(param.get("chlId"))) {
        	//通道T日可用现金
        	result.put("channelAvailable", cash.get("cashT2"));
        	//通道T+1日可用现金
            result.put("channelAvailableT1",cash.get("cashT21"));
        }
//        if (baseEtrCashPositionCommService != null) {
//            EtrCashPositionFlowDto dto = new EtrCashPositionFlowDto();
//            dto.setPortfolioId((String)param.get("portfolioId"));
//            if (StringUtil.isBlank(dto.getPortfolioId())) {
//                result.put("parentPortfolioId",etrCommonService.selectParentPortfolioIdByPortfolioId(
//                        dto.getPortfolioId()));
//            }
//            dto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
//            dto.setCdate(tradeDate);
//            dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
//            //组合T日可用现金
//            BigDecimal proAvailableCash = baseEtrCashPositionCommService.queryOtherCashPosition(dto);
//            //调整现金
//            PtlCashPositionFlowDto positionQueryDtoParam = new PtlCashPositionFlowDto();
//            positionQueryDtoParam.setPortfolioId((String)param.get("portfolioId"));
//            positionQueryDtoParam.setHodingType(DictCodeListExt1.持仓类型_人工调整.getValue());
//            positionQueryDtoParam.setCdate(tradeDate);
//            positionQueryDtoParam.setCcy(DictCodeList.币种_人民币_CNY.getValue());
//            BigDecimal tiaozhengT = basePtlCashPositionInfoService.queryOtherCashPositionDangri(positionQueryDtoParam);
//            result.put("proAvailableCash", ArithmeticUtil.doAdd(proAvailableCash, tiaozhengT));
//            //组合T+1日可用现金
//            dto.setCdate(tradeDateNext);
//            BigDecimal proAvailableCashT1 = baseEtrCashPositionCommService.queryOtherCashPosition(dto);
//            positionQueryDtoParam.setCdate(tradeDateNext);
//            BigDecimal tiaozhengT1 = basePtlCashPositionInfoService.queryOtherCashPositionDangri(positionQueryDtoParam);
//            result.put("proAvailableCashT1", ArithmeticUtil.doAdd(proAvailableCashT1, tiaozhengT1));
//            //托管户T日可用现金
//            dto.setCdate(tradeDate);
//            List<String> acctTypeList = new ArrayList<>();
//            acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
//            dto.setAcctType(acctTypeList);
//            BigDecimal secManageAvailablePosition = baseEtrCashPositionCommService.queryOtherCashPosition(dto);
//            positionQueryDtoParam.setCdate(tradeDate);
//            positionQueryDtoParam.setAcctType(acctTypeList);
//            BigDecimal tiaozhengMT = basePtlCashPositionInfoService.queryOtherCashPositionDangri(positionQueryDtoParam);
//            result.put("secManageAvailablePosition", ArithmeticUtil.doAdd(secManageAvailablePosition, tiaozhengMT));
//            //托管户T+1日可用现金
//            dto.setCdate(tradeDateNext);
//            BigDecimal secManageAvailablePositionT1 = baseEtrCashPositionCommService.queryOtherCashPosition(dto);
//            positionQueryDtoParam.setCdate(tradeDateNext);
//            BigDecimal tiaozhengMT1 = basePtlCashPositionInfoService.queryOtherCashPositionDangri(positionQueryDtoParam);
//            result.put("secManageAvailablePositionT1", ArithmeticUtil.doAdd(secManageAvailablePositionT1, tiaozhengMT1));
//
//            // 通道不为空
//            if (StringUtil.isNotEmpty(param.get("chlId"))) {
//                //通道T日可用现金
//                dto.setCdate(tradeDate);
//                dto.setAcctType(null);
//                dto.setChlFinprodId((String) param.get("chlId"));
//                result.put("channelAvailable", baseEtrCashPositionCommService.queryOtherCashPosition(dto));
//                //通道T+1日可用现金
//                dto.setCdate(tradeDateNext);
//                result.put("channelAvailableT1", baseEtrCashPositionCommService.queryOtherCashPosition(dto));
//            }
//        }

        if (StringUtil.isNotEmpty((String)param.get("finprodId"))) {
            // 资产可用券面总额
            EtrSecPositionDto etrSecPositionDto= new EtrSecPositionDto();
            etrSecPositionDto.setCdate(tradeDate);
            etrSecPositionDto.setFinprodId((String)param.get("finprodId"));
            etrSecPositionDto.setPortfolioId((String)param.get("portfolioId"));

            etrSecPositionDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());

            etrSecPositionDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
            result.put("assetAvailableEntrustAmt", etrCommonService.getAssetAvailablePosition(etrSecPositionDto).divide(new BigDecimal(100)));
            // 资产持仓券面总额
            PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
            ptlSecPositionFlowDto.setCdate(tradeDate);
            ptlSecPositionFlowDto.setPortfolioId((String)param.get("portfolioId"));
//            if (StringUtil.isBlank(ptlSecPositionFlowDto.getPortfolioId())) {
//                result.put("parentPortfolioId",etrCommonService.selectParentPortfolioIdByPortfolioId(
//                        ptlSecPositionFlowDto.getPortfolioId()));
//            }
            ptlSecPositionFlowDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
            ptlSecPositionFlowDto.setFinprodId((String)param.get("finprodId"));
            ptlSecPositionFlowDto.setEntrust(true);
            BigDecimal amount03 = basePtlSecPositionInfoService.queryOtherSecPosition(ptlSecPositionFlowDto).divide(new BigDecimal(100));
            ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_融入.getValue());
            BigDecimal amount05 = basePtlSecPositionInfoService.queryOtherSecPosition(ptlSecPositionFlowDto).divide(new BigDecimal(100));
            ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_融出.getValue());
            BigDecimal amount06 = basePtlSecPositionInfoService.queryOtherSecPosition(ptlSecPositionFlowDto).divide(new BigDecimal(100));
            BigDecimal assetPositionEntrustAmt = ArithmeticUtil.doAdd(amount03, amount05);
            assetPositionEntrustAmt = ArithmeticUtil.subtract(assetPositionEntrustAmt, amount06);
            result.put("assetPositionEntrustAmt", assetPositionEntrustAmt);
        } else {
            result.put("assetAvailableEntrustAmt", BigDecimal.ZERO);
            result.put("assetPositionEntrustAmt", BigDecimal.ZERO);
        }
        return result;
    }

    /**
     *
     * 流程发起时调用
     */
    @Override
    public void doInsert(EtribBondInfo003Dto entity, OperationType type){
//        if ("submit".equals(entity.getOperateType())) {
//            if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondInfoDtos())) {
//                for (EtribBondInfo003Dto dto : entity.getEtrInvestBondInfoDtos()) {
//                    doInsertFlow(dto);
//                }
//            }
//        }
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbBondinfo003/doInsert",entity,
                Integer.class);
    }

    @Override
    public void doUpdate(EtribBondInfo003Dto entity) {
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbBondinfo003/doUpdate",entity,
                Integer.class);
    }

    @Override
    public void doBack(EtribBondInfo003Dto entity) {
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbBondinfo003/doBack",entity,
                Integer.class);
    }

    private void doInsertFlow(EtribBondInfo003Dto dto) {
        try {
            dto.setOperateType(dto.getOperateType());
            BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
            EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
            EtrEntrust etrEntrust = new EtrEntrust();
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            dto.setRecStatus(DictCodeListExt3.委托状态_流程中.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            //BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(null);
            etrEntrust.setEntrustNum(dto.getEntrustNum().divide(new BigDecimal(100)));
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            //etrEntrustAdd.setOurEntNum(com.joyintech.framework.util.ArithmeticUtil.divide(dto.getEntrustOwnNum(), 100, 4));
            //etrEntrustAdd.setLendinEntNum(com.joyintech.framework.util.ArithmeticUtil.divide(dto.getEntrustIntegrateNum(), 100, 4));
            etrEntrustAdd.setFullPrice(dto.getFpriceAmt());
            if (dto.getExerYield() != null) {
                etrEntrustAdd.setExerYield(dto.getExerYield().divide(new BigDecimal(100)));
            }
            if (dto.getMatureYield() != null) {
                etrEntrustAdd.setMatureYield(dto.getMatureYield().divide(new BigDecimal(100)));
            }
            if (StringUtil.equals(dto.getSettleSpeed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)0);
            }
            if (StringUtil.equals(dto.getSettleSpeed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)1);
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            entity.setMstFileInputParameter(mstFileInputParameter);
            entity.setWorkFlowId(dto.getWorkflowId());
            restTemplate.postForObject(
                    ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doInsertEtrFlow", entity,
                    Boolean.class);
            if(baseEtrCashPositionCommService!=null){
                baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 校验方法 <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtribBondInfo003Dto dto) {
        dto.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
        return restTemplate.postForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/check", dto,
                ValidMessage.class);
    }

    /**
     * 查询证券托管户：联想框文本框
     *
     * @param parameters 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getSecManageAcctId(Map<String, Object> parameters) {
        List<SuggestDto> tempList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecManageAcctId",
                parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                if (StringUtil.isNotBlank((String)parameters.get("portfolioId"))) {
                    selectOptionDto.setParentId((String)parameters.get("portfolioId"));
                }
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }

    /**
     *
     * 净价、行权收益率、到期收益率联动
     * <功能详细描述>
     * @param dto
     * @see
     * @since 1.0
     */
    public void calcAmt(EtribBondInfo003Dto dto) {
        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
            dto.setSettleSpeed(String.valueOf(0));
        }
        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
            dto.setSettleSpeed(String.valueOf(1));
        }
        if (dto == null || dto.getVdate() == null || dto.getMdate() == null
                || DateUtil.compareDate(dto.getVdate(), dto.getMdate()) != 0) {
            EtribBondInfo003Dto nullDto = new EtribBondInfo003Dto();
            try {
                nullDto.setPrice(dto.getPrice());
                nullDto.setExerYield(dto.getExerYield());
                nullDto.setMatureYield(dto.getMatureYield());
                BeanUtil.copyProperties(dto, nullDto);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        dto.setSettleDate(dto.getVdate());

        if(StringUtil.isNotEmpty(dto.getSettleSpeed())){
            if(Integer.parseInt(dto.getSettleSpeed())!=0){
                List<String> holiday = dateCalService.getDealCalendarHoliday(dto.getVdate(),
                        DateUtil.addDays(dto.getVdate(), 31),dto.getCalendarId());
                Date tradingDay = dateCalService.calculationTradingDay(dto.getVdate(), Integer.parseInt(dto.getSettleSpeed()), holiday, "1");
                dto.setSettleDate(tradingDay);
            }
        }

        if (DateUtil.compareDate(dto.getSettleDate(), dto.getFinvdate()) < 0) {
            dto.setSettleDate(dto.getFinvdate());
        }

        // 如果交易场所为上交所、深交所以外的情况
        boolean isJiaoYiSuo = true;
        if (!StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), dto.getTradeMarket())
                && !StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), dto.getTradeMarket())) {
            // 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量
            this.getUnitIntFromValution(dto);
            isJiaoYiSuo = false;
        }

        final int SCALE = 12;

        //到期收益率
        dto.setMatureYield(com.joyintech.calculation.util.ArithmeticUtil.divide(dto.getMatureYield(), 100, 14));
        //行权收益率
        dto.setExerYield(com.joyintech.calculation.util.ArithmeticUtil.divide(dto.getExerYield(), 100, 14));

        YieldByPriceCalculator yieldByPriceCalculator = new YieldByPriceCalculator();
        PriceByYieldCalculator priceByYieldCalculator = new PriceByYieldCalculator();
        YieldPriceInParameter yieldPriceInParameter = null;
        PriceYieldInParameter priceYieldInParameter = null;
        YieldPriceOutParameter yieldPriceOutParameter = null;
        PriceYieldOutParameter priceYieldOutParameter = null;

        Map<String, Object> tempParamMap = new HashMap<String, Object>();
        tempParamMap.put("finprodId", dto.getFinprodId());
        tempParamMap.put("ps", dto.getPs());
        // b.获取计息区间投资端利率报价信息（债券的利率重置信息）
//         List<BondsIntReset> bondsIntResetList = transactionBond001Mapper.getInvRateQuo(dto);
        List<FinCashCalcRule> finCashCalcRuleList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/selectBondsIntResetList",
                tempParamMap, FinCashCalcRule.class);

        // c.获取债券的付息计划信息
        List<FinCashPlanDto> finCashPlanList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getFinCashPlan",
                tempParamMap, FinCashPlanDto.class);
        List<SecIntSchedule> invScheduleList = new ArrayList<>();
        List<FinCashPlanDto> intSchList = new ArrayList<FinCashPlanDto>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(),
                    DictCodeListExt1.现金流一级类型_利息.getValue())
                    && item.getVdateUnadjust() != null) {
                SecIntSchedule schedule = new SecIntSchedule();
                schedule.setTheoryStartDate(toLocalDate(item.getVdateUnadjust()));
                schedule.setTheoryCutoffDate(toLocalDate(item.getMdateUnadjust()));
                schedule.setActualStartDate(toLocalDate(item.getVdate()));
                schedule.setActualCutoffDate(toLocalDate(item.getMdate()));
                schedule.setAmount(item.getIntAmt());
                invScheduleList.add(schedule);

                intSchList.add(item);
            }
        }

        // d.获取债券基本资料信息
        SecInfoModelDto secInfoModelDto = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecInfoModel",
                tempParamMap, SecInfoModelDto.class);
        SecInfoModel secInfoModel = this.toSecInfoModel(secInfoModelDto);
        // 如果交易场所为上交所、深交所的情况
        if (isJiaoYiSuo) {
            // 付息债的计息基础使用A_365F
            if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
                secInfoModel.setBasis(Basis.A_365F);
            }
        }

        // e.获取债券的还本计划信息
        List<SecPrinSchedule> secPrinScheduleList = new ArrayList<SecPrinSchedule>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(),
                    DictCodeListExt1.现金流一级类型_本金.getValue())) {
                SecPrinSchedule schedule = new SecPrinSchedule();
                schedule.setAmount(item.getIntAmt());
                schedule.setFlag(
                        StringUtil.equals(item.getIsPayInt(), DictCodeList.是否_是.getValue()));
                schedule.setPrincipal(item.getPrinAmt());
                schedule.setInterestEndDate(toLocalDate(item.getMdate()));
                secPrinScheduleList.add(schedule);
            }
        }

        List<PaperIrReset> paperIrResetList = finCashCalcRuleList.stream().map(item -> {
            PaperIrReset ret = new PaperIrReset();
            // BeanUtil.copyPropertiesIgnoreNull(ret, item);
            ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            ret.setParRate(item.getYield());
            ret.setParRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            return ret;
        }).collect(Collectors.toList());
        if (paperIrResetList != null && paperIrResetList.size() > 0) {
            paperIrResetList.get(0).setBaseRateEffDate(
                    BasicUtil.dateToLocal(secInfoModelDto.getStartDate()));
        }
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            //多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        yieldPriceInParameter = new YieldPriceInParameter();
        yieldPriceInParameter.setCalDate(toLocalDate(dto.getSettleDate()));
        yieldPriceInParameter.setSecInfoModel(secInfoModel);
        yieldPriceInParameter.setSecIntScheduleList(invScheduleList);
        yieldPriceInParameter.setSecPrinScheduleList(secPrinScheduleList);
        yieldPriceInParameter.setPaperIrResetList(paperIrResetList);
        priceYieldInParameter = new PriceYieldInParameter();
        priceYieldInParameter.setCalDate(toLocalDate(dto.getSettleDate()));
        priceYieldInParameter.setSecInfoModel(secInfoModel);
        priceYieldInParameter.setSecIntScheduleList(invScheduleList);
        priceYieldInParameter.setSecPrinScheduleList(secPrinScheduleList);
        priceYieldInParameter.setPaperIrResetList(paperIrResetList);
        yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
            if (isNotFomartSec(intSchList, secInfoModelDto)) {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                priceYieldInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
            } else {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
            }
        }
        yieldPriceInParameter.setCalMethod(secInfoModel.getCalMethod());
        priceYieldInParameter.setCalMethod(secInfoModel.getCalMethod());
        // 后续区间利息计算方案
        if (IntRateType.浮动利率.equals(secInfoModel.getIntRateType())) {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
        } else {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
        }
        // 百元面值应计利息
        BigDecimal accurInt = BigDecimal.ZERO;
        // 交易方式为“一级市场”时，单位应计利息=应计利息总额=0
        if (StringUtil.equals(dto.getTradeMarketMode(),
                DictCodeListExt1.交易市场_一级市场.getValue())) {
            dto.setIntAmt(BigDecimal.ZERO);
            dto.setUnitInt(accurInt);
        }
        // 交易方式为“二级市场”时，单位应计利息 =e取得的百元面值应计利息/100*a取得的当前债券面额
        else if (StringUtil.equals(dto.getTradeMarketMode(),
                DictCodeListExt1.交易市场_二级市场.getValue())) {

            //交易所债券特殊处理start----------------------------------------------------------------
            Date tempSettleDate = dto.getSettleDate();
            // 如果交易场所为上交所、深交所的情况
            if (isJiaoYiSuo) {
                if (!StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                        && !StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())) {
                    tempSettleDate = DateUtil.addDay(tempSettleDate, 1);
                }
            }
            //交易所债券特殊处理end----------------------------------------------------------------

            // a.当前债券面额取得（根据还本计划计算）
            Map<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("finprodId", dto.getFinprodId());
//            paraMap.put("settleDate", DateUtil.format(dto.getSettleDate()));
            paraMap.put("settleDate", DateUtil.format(tempSettleDate));
            BigDecimal curSecAmt = restTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getCurSecAmt",
                    paraMap, BigDecimal.class);

            AccurIntCalInParameter accurIntCalInParameter = new AccurIntCalInParameter();
            // 计算日
//            accurIntCalInParameter.setCalDate(this.toLocalDate(dto.getSettleDate()));
            accurIntCalInParameter.setCalDate(this.toLocalDate(tempSettleDate));
            // 债券基本资料
            accurIntCalInParameter.setSecInfoModel(secInfoModel);
            // 付息计划
            accurIntCalInParameter.setSecIntScheduleList(invScheduleList);
            // 利率重置信息
            accurIntCalInParameter.setPaperIrResetList(paperIrResetList);

            //交易所债券特殊处理start----------------------------------------------------------------
            AccurIntCalOutParameter out = null;
            // 如果交易场所为上交所、深交所的情况
            if (isJiaoYiSuo) {
                Map<String, BigDecimal> intMap = new HashMap<>();
                if(null != invScheduleList && !invScheduleList.isEmpty()) {
                    for(SecIntSchedule intSche : invScheduleList) {
                        intMap.put(intSche.getActualCutoffDate().toString(), intSche.getAmount());
                    }
                }

                if(intMap.containsKey(accurIntCalInParameter.getCalDate().toString()) &&
                        !((Variety.贴现.equals(secInfoModel.getBondsType())) || Variety.零息.equals(secInfoModel.getBondsType()))) {
                    out = new AccurIntCalOutParameter();
                    out.setInterest(intMap.get(accurIntCalInParameter.getCalDate().toString()).multiply(new BigDecimal("100")
                    ).divide(curSecAmt, 12, RoundingMode.HALF_UP));
                    accurIntCalInParameter.setCalDate(accurIntCalInParameter.getCalDate());
                    AccurIntForSecheduleCalculator calculator2 = CalculatorFactory.createAccurIntForSecheduleCalculator();
                    if (accurIntCalInParameter.getPaperIrResetList() != null && accurIntCalInParameter.getPaperIrResetList().size() > 1) {
                        accurIntCalInParameter.getSecInfoModel().setIntRateType(IntRateType.浮动利率);
                    }
                    out = calculator2.calculate(accurIntCalInParameter);
                }
//                }
            }
            //交易所债券特殊处理end----------------------------------------------------------------

            if (out != null) {
                accurInt = com.joyintech.calculation.util.ArithmeticUtil.round(out.getInterest(), SCALE, RoundingMode.HALF_UP);
            }
            else {
                // 从计算工厂中创建应计利息计算器
                AccurIntCalculator calc = CalculatorFactory.createAccurIntCalculator();
                // 计算利息
                AccurIntCalOutParameter accurIntCalOutParameter = calc.calculate(accurIntCalInParameter);
                accurInt = com.joyintech.calculation.util.ArithmeticUtil.round(accurIntCalOutParameter.getInterest(), SCALE, RoundingMode.HALF_UP);
            }

            // 单位应计利息
            if (dto.getUnitInt() == null) {
                if (isJiaoYiSuo) {
                    dto.setUnitInt(com.joyintech.calculation.util.ArithmeticUtil.divide(com.joyintech.calculation.util.ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                            Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_EXCHANGE")) ? "8" : CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_EXCHANGE")), RoundingMode.HALF_UP));
                }
                else {
                    dto.setUnitInt(com.joyintech.calculation.util.ArithmeticUtil.divide(com.joyintech.calculation.util.ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                            Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_BANK")) ? "12" : CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_BANK")), RoundingMode.HALF_UP));
                }
            }
        }
        try {
            // 单位净价
            if (StringUtil.equals(dto.getCalflag(), "price")) {
                // 单位全价 = 单位净价 + 单位应计利息
                dto.setUnitFprice(com.joyintech.calculation.util.ArithmeticUtil.doAdd(dto.getPrice(), dto.getUnitInt()));
                // 全价总额 = 单位全价 * 券面总额/100
                dto.setFpriceAmt(com.joyintech.calculation.util.ArithmeticUtil.divide(
                        com.joyintech.calculation.util.ArithmeticUtil.multiply(dto.getUnitFprice(), dto.getEntrustNum()),
                        new BigDecimal(100), SCALE));
//                // 净价总额 = 单位净价 * 券面总额/100
//                dto.setCpriceAmt(ArithmeticUtil.divide(
//                    ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
//                    new BigDecimal(100), SCALE));
            }
//            // 全价总额
//            else if (StringUtil.equals(dto.getCalculateField(), "fpriceAmt")) {
//                // 单位全价 = 全价总额 * 100 / 券面总额
//                dto.setUnitFprice(ArithmeticUtil.divide(
//                    ArithmeticUtil.multiply(dto.getFpriceAmt(), new BigDecimal(100)),
//                    dto.getPrinAmt(), SCALE));
//                // 单位净价 = 单位全价 - 单位应计利息
//                dto.setUnitCprice(ArithmeticUtil.doSub(dto.getUnitFprice(), dto.getUnitInt()));
//                // 净价总额 = 单位净价 * 券面总额/100
//                dto.setCpriceAmt(ArithmeticUtil.divide(
//                    ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
//                    new BigDecimal(100), SCALE));
//            }
            switch (dto.getCalflag()) {
                // 单位净价
                case "price":
                    // 全价总额
                case "fpriceAmt":
                    // 全价总额==>到期收益率
                    yieldPriceInParameter.setFullPrice(
                            dto.getUnitFprice()/* dto.getFpriceAmt() */);
                    yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                            yieldPriceInParameter);
                    dto.setMatureYield(yieldPriceOutParameter.getMatYield());
                    if (secInfoModelDto.getNextExeDate() != null) {
                        // 全价总额==>行权收益率
                        yieldPriceInParameter.setNextExeDate(
                                toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(
                                secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                    yieldPriceInParameter);
                            dto.setExerYield(yieldPriceOutParameter.getExeYield());
                        }
                        else {
                            dto.setExerYield(null);
                        }
                    }
                    break;
                // 到期收益率
                case "matureYield":
                    // 到期收益率==>全价总额
                    priceYieldInParameter.setMatYield(dto.getMatureYield());
                    priceYieldOutParameter = priceByYieldCalculator.calculate(
                            priceYieldInParameter);
                    // dto.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                    dto.setUnitFprice(priceYieldOutParameter.getFullPrice());
                    // 全价总额==>行权收益率
                    if (secInfoModelDto.getNextExeDate() != null) {
                        yieldPriceInParameter.setFullPrice(
                                dto.getUnitFprice()/* dto.getFpriceAmt() */);
                        yieldPriceInParameter.setNextExeDate(
                                toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(
                                secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                    yieldPriceInParameter);
                            dto.setExerYield(yieldPriceOutParameter.getMatYield());
                        }
                        else {
                            dto.setExerYield(null);
                        }
                    }
                    break;
                // 行权收益率
                case "exerYield":
                    // 行权收益率==>全价总额
                    if (secInfoModelDto.getNextExeDate() != null) {
//                        priceYieldInParameter.setMatYield(dto.getMatureYield());
                        priceYieldInParameter.setMatYield(dto.getExerYield());//行权收益
                        yieldPriceInParameter.setNextExeDate(
                                toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(
                                secInfoModelDto.getNextExercisePrice());
                        priceYieldOutParameter = priceByYieldCalculator.calculate(
                                priceYieldInParameter);
                        dto.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                        dto.setUnitFprice(priceYieldOutParameter.getFullPrice());
                        // 全价总额==>到期收益率
                        yieldPriceInParameter.setFullPrice(dto.getFpriceAmt());
                        yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                yieldPriceInParameter);
                        dto.setMatureYield(yieldPriceOutParameter.getMatYield());
                    }
                    break;
                default:
                    break;
            }
            // 到期收益率 或 行权收益率
            if (StringUtil.equals(dto.getCalflag(), "matureYield")
                    || StringUtil.equals(dto.getCalflag(), "exerYield")) {
                // 单位全价 = 全价总额 * 100 / 券面总额
                // dto.setUnitFprice(ArithmeticUtil.doDiv(
                // ArithmeticUtil.doMul(dto.getFpriceAmt(), new BigDecimal(100)),
                // dto.getPrinAmt(), 12));
                // 全价总额 = 单位全价 * 券面总额/100
                dto.setFpriceAmt(com.joyintech.calculation.util.ArithmeticUtil.divide(
                        com.joyintech.calculation.util.ArithmeticUtil.multiply(dto.getUnitFprice(), dto.getEntrustNum()),
                        new BigDecimal(100), SCALE));
                // 单位净价 = 单位全价 - 单位应计利息
                dto.setPrice(com.joyintech.calculation.util.ArithmeticUtil.doSub(dto.getUnitFprice(), dto.getUnitInt()));
                // 净价总额 = 单位净价 * 券面总额/100
//                dto.setCpriceAmt(ArithmeticUtil.divide(
//                    ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
//                    new BigDecimal(100), SCALE));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (dto.getMatureYield() != null) {
            dto.setMatureYield(com.joyintech.calculation.util.ArithmeticUtil.multiply(dto.getMatureYield(), new BigDecimal(100)));
        }
        if (dto.getExerYield() != null) {
            dto.setExerYield(com.joyintech.calculation.util.ArithmeticUtil.multiply(dto.getExerYield(), new BigDecimal(100)));
        }
    }

    /**
     * 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量，
     * 如取不到估值核算中前一个自然日的数据，计算方式见算法文档；
     * 画面.应计利息=PTL_SEC_VALUTION.TDY_INTINCEXP/PTL_SEC_VALUTION.SHARE_AMT
     * （PTL_SEC_VALUTION.日期 = 画面.交割日-1 ，取不到，取最大日期对应数据）
     *
     * @param dto 参数
     * @return 应计利息
     * @see
     * @since 1.0
     */
    private void getUnitIntFromValution(EtribBondInfo003Dto dto) {
        // 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量，如取不到估值核算中前一个自然日的数据，计算方式见算法文档；
        // 画面.应计利息=PTL_SEC_VALUTION.TDY_INTINCEXP/PTL_SEC_VALUTION.SHARE_AMT  （PTL_SEC_VALUTION.日期 = 画面.交割日-1 ，取不到，取最大日期对应数据）
        Map<String, Object> paraMapForIntAmt = new HashMap<String, Object>();
        paraMapForIntAmt.put("settleDate", DateUtil.format(dto.getSettleDate()));
        paraMapForIntAmt.put("portfolioId", dto.getPortfolioId());
        paraMapForIntAmt.put("finprodId", dto.getFinprodId());
        paraMapForIntAmt.put("invAim", dto.getInvAim());
        paraMapForIntAmt.put("secAcctId", dto.getChlId());
        BigDecimal 应计利息 = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getIntAmtFromValution",
                paraMapForIntAmt, BigDecimal.class);
        if (应计利息 != null) {
            dto.setUnitInt(应计利息);
        }
    }

    /**
     * 转换数据
     *
     * @param secInfoModelDto 债券基本信息SecInfoModelDto
     * @return 债券基本信息SecInfoModel
     * @see
     * @since 1.0
     */
    private SecInfoModel toSecInfoModel(SecInfoModelDto secInfoModelDto) {
        SecInfoModel secInfoModel = new SecInfoModel();

        secInfoModel.setStartDate(toLocalDate(secInfoModelDto.getStartDate()));
        secInfoModel.setEndDate(toLocalDate(secInfoModelDto.getEndDate()));
        secInfoModel.setFirstPayDate(toLocalDate(secInfoModelDto.getFirstPayDate()));
        // 利率类型
        if (StringUtil.equals(DictCodeList.利率类型_固定利率.getValue(),
                secInfoModelDto.getIntRateType())) {
            secInfoModel.setIntRateType(IntRateType.固定利率);
        }
        else {
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }

        if (StringUtil.isNotBlank(secInfoModelDto.getBasis())) {
            String basis = StringUtil.splitToList(GlobalConstant.COMMA, secInfoModelDto.getBasis()).get(0);
            DictCodeList basisDict = DictCodeList.from(DictCode.计息基础, basis);
            if (basisDict != null){
                switch (basisDict) {
                    case 计息基础_30_360:
                        secInfoModel.setBasis(Basis._30_360);
                        break;
                    case 计息基础_A_360:
                        secInfoModel.setBasis(Basis.A_360);
                        break;
                    case 计息基础_A_365:
                        secInfoModel.setBasis(Basis.A_365);
                        break;
                    case 计息基础_A_365F:
                        secInfoModel.setBasis(Basis.A_365F);
                        break;
                    case 计息基础_A_A:
                    case 计息基础_A_A_平均分配:
                    case 计息基础_A_A_按实际天数分配:
                        secInfoModel.setBasis(Basis.A_A);
                        break;
                }
                if (basisDict.equals(DictCodeList.计息基础_A_A_平均分配)) {
                    secInfoModel.setAllocationType(AllocationType.平均分配);
                }
                else {
                    secInfoModel.setAllocationType(AllocationType.按实际天数分配);
                }
            }else {
                DictCodeListExt3 basisDict3 = DictCodeListExt3.from(DictCode.计息基础, basis);
                if (basisDict3 != null){
                    switch (basisDict3) {
                        case 计息基础_30U_360:
                            secInfoModel.setBasis(Basis._30U_360);
                            break;
                    }

                }
            }
        }
        DictCodeList bondsTypeDict = DictCodeList.from(DictCode.息票品种,
                secInfoModelDto.getBondsType());
        switch (bondsTypeDict) {
            case 息票品种_零息:
                secInfoModel.setBondsType(Variety.零息);
                break;
            case 息票品种_利随本清:
                secInfoModel.setBondsType(Variety.利随本清);
                break;
            case 息票品种_贴现:
                secInfoModel.setBondsType(Variety.贴现);
                break;
            case 息票品种_附息:
                secInfoModel.setBondsType(Variety.附息);
                break;
        }

        // 一体化对应etliu start
//        if (StringUtil.equals(DictCodeList.推算方法_起息日向后.getValue(),
        if (StringUtil.equals(DictCodeList.推算方式_起息日向后.getValue(),
                secInfoModelDto.getCalMethod())) {
            secInfoModel.setCalMethod(SecIntSchCalMethod.起息日向后);
        }
//        else if (StringUtil.equals(DictCodeList.推算方法_到期日向前.getValue(),
        else if (StringUtil.equals(DictCodeList.推算方式_到期日向前.getValue(),
                secInfoModelDto.getCalMethod())) {
            secInfoModel.setCalMethod(SecIntSchCalMethod.到期日向前);
        }
        else {
            secInfoModel.setCalMethod(SecIntSchCalMethod.首次付息日);
        }

//        if (StringUtil.equals(DictCodeList.支付频率_付息频率_月付.getValue(),
        if (StringUtil.equals(DictCodeList.频率_月.getValue(),
                secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.月付);
        }
//        else if (StringUtil.equals(DictCodeList.支付频率_付息频率_季付.getValue(),
        else if (StringUtil.equals(DictCodeList.频率_季.getValue(),
                secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.季付);
        }
//        else if (StringUtil.equals(DictCodeList.支付频率_付息频率_半年付.getValue(),
        else if (StringUtil.equals(DictCodeList.频率_半年.getValue(),
                // 一体化对应etliu end
                secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.半年付);
        }
        else {
            secInfoModel.setYearIntRate(IntFrequency.年付);
        }

        secInfoModel.setIssuePrice(secInfoModelDto.getIssuePrice());

        return secInfoModel;
    }

    public boolean isNotFomartSec(List<FinCashPlanDto> secIntScheduleList, SecInfoModelDto secInfoModelDto) {
        // 根据【实际起始日】排序付息计划
        Collections.sort(secIntScheduleList, new Comparator<FinCashPlanDto>() {
            @Override
            public int compare(FinCashPlanDto o1, FinCashPlanDto o2) {
                return o1.getVdateUnadjust().compareTo(o2.getVdateUnadjust());
            }
        });
        if (StringUtil.equals(DictCodeList.频率_月.getValue(),
                secInfoModelDto.getYearIntRate())) {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 1);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        }
        else if (StringUtil.equals(DictCodeList.频率_季.getValue(),
                secInfoModelDto.getYearIntRate())) {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 3);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        }
        else if (StringUtil.equals(DictCodeList.频率_半年.getValue(),
                secInfoModelDto.getYearIntRate())) {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 6);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        }
        else {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addYear(she.getVdateUnadjust(), 1);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 转换数据
     *
     * @param date 日期
     * @return LocalDate
     * @see
     * @since 1.0
     */
    private LocalDate toLocalDate(Date date) {
        if (date != null) {
            return LocalDate.parse(DateUtil.format(date));
        }
        return null;
    }

    @Override
    public void doComplete(EtribBondInfo003Dto entity, OperationType type) {
        entity.setOperateType(type.toString());
        entity.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        if(StringUtil.isNotEmpty(entity.getSettleSpeed())){
            if(Integer.parseInt(entity.getSettleSpeed())!=0){
                List<String> holiday = dateCalService.getDealCalendarHoliday(entity.getVdate(),
                        DateUtil.addDays(entity.getVdate(), 31),entity.getCalendarId());
                Date tradingDay = dateCalService.calculationTradingDay(entity.getVdate(), Integer.parseInt(entity.getSettleSpeed()), holiday, "1");
                entity.setSettleDate(tradingDay);
                // 每个组合设置交割日
                if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondInfoDtos())) {
                    for (int i = 0; i < entity.getEtrInvestBondInfoDtos().size(); i++) {
                        entity.getEtrInvestBondInfoDtos().get(i).setSettleDate(tradingDay);
                    }
                }
            }
        }
        for (EtribBondInfo003Dto dto : entity.getEtrInvestBondInfoDtos()) {
            basRiskMathCommService.insert(entity.getRiskTrialRetDto(), dto.getEntrustId());
        }
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbBondinfo003/doComplete", entity,
                Integer.class);

//        doDeleteFlow(entity.getWorkflowId());
    }

    @Override
    public void doDelete(String workflowId) {
        doDeleteFlow(workflowId);
    }

    private void doDeleteFlow(String workflowId) {
        EtribBondInfo003Dto entity = getBussinessEntityByWorkflowId(workflowId);
        if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondInfoDtos()))  {
            for (EtribBondInfo003Dto dto: entity.getEtrInvestBondInfoDtos()) {
                Map<String,Object> param = new HashMap<>();
                param.put("entrustId",dto.getEntrustId());
                restTemplate.postForObject(
                        ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbBondinfo003/doDeleteEtrFlow", param,
                        Boolean.class);

            }
        }

    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        EtribBondInfo003Dto dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        if (taskId != null) {
            try {
            	if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH_IS_TOTAL), "Y")){
           		 	return getEtrLimitSwitch(entity.getWorkflowId(), dto, commonBusinessService);
            	}else{
//           		BussinessConstant.是否需要上级审批
	           		List<EtribBondInfo003Dto> etribRepoInfo003DtoList =dto.getEtrInvestBondInfoDtos();
	           		Map<String, Object> result = new HashMap<>();
	           		for(EtribBondInfo003Dto etribBondInfo003Dto:etribRepoInfo003DtoList){
	           			result=getEtrLimitSwitch(entity.getWorkflowId(), etribBondInfo003Dto, commonBusinessService);
	           			if((boolean)result.get(BussinessConstant.是否需要上级审批)==true){
	           				return result;
	           			}
	           		}
	           		return result;
           		}
            }
            catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        else {
            return null;
        }
    }
    
    private Map<String, Object> getEtrLimitSwitch(String workflowId, EtribBondInfo003Dto dto,
			CommonBusinessService commonBusinessService) throws WorkFlowException {
    	if (dto.isImportFlow()) {
            Map<String, Object> param = new HashMap<>();
            param.put(BussinessConstant.是否需要上级审批, true);
            return param;
        }
        BigDecimal amount = dto.getEntrustNum();
        if (amount == null) {
            amount = dto.getEntrustAmt();
        }
        Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getEntrustNum(), LoginUserUtil.getUserName(),
                BussinessDictCodeList.平安资产类型_债券.getValue(), dto.getAssetType1(), dto.getTradeMarket(), dto.getPs()
                , dto.getFinprodId(), workflowId
        );
        // add by zhangrui1 风险审批修改 start
        params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
        // add by zhangrui1 风险审批修改 end
        return commonBusinessService.是否需要上级审批_委托用(params);
	}
}
