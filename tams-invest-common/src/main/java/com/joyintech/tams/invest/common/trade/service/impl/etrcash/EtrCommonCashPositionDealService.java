package com.joyintech.tams.invest.common.trade.service.impl.etrcash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.tams.constant.DictCodeListExt3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.shell.AccurIntCalculator;
import com.joyintech.calculation.shell.param.AccurIntCalInParameter;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.shell.param.SecInfoModel;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.EtrCommonCashPositionMapper;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.trade.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;
import com.joyintech.tams.invest.common.trade.service.BussRepoSecService;
import org.springframework.util.CollectionUtils;

/**
 * @program: 2.1.0
 * @description:
 * @author: 强成西
 * @create: 2019-10-12 15:06
 **/
@Service
@Lazy(false)
public class EtrCommonCashPositionDealService extends BaseService {

    static Logger logger = LoggerFactory.getLogger("EtrCommonCashPositionDealService");

    /**
     * 现金流水DAO
     */
    private static EtrCashPositionFlowMapper etrCashPositionFlowMapper;

    /**
     * 持仓流水DAO
     */
    private static EtrSecPositionFlowMapper etrSecPositionFlowMapper;

    /**
     * 现金DAO
     */
    private static EtrCashPositionMapper etrCashPositionMapper;

    /**
     * 持仓DAO
     */
    private static EtrSecPositionMapper etrSecPositionMapper;

    /**
     * 持仓DAO
     */
    private static EtrCommonCashPositionMapper etrCommonCashPositionMapper;

    private static BussRepoSecService bussRepoSecService;

    private static PtlCapacctInfoMapper ptlCapacctInfoMapper;

    private static EtrEntrustMapper etrEntrustMapper;

    private static EtrEntrustAddMapper etrEntrustAddMapper;

    private static EtrEntrustDetailMapper etrEntrustDetailMapper;

    private static WflEtrEntrustMapper wflEtrEntrustMapper;

    private static WflEtrEntrustAddMapper wflEtrEntrustAddMapper;

    private static WflEtrEntrustDetailMapper wflEtrEntrustDetailMapper;
    
    @Autowired
	public void setWflEtrEntrustMapper(WflEtrEntrustMapper wflEtrEntrustMapper) {
		EtrCommonCashPositionDealService.wflEtrEntrustMapper = wflEtrEntrustMapper;
	}

	@Autowired
	public void setWflEtrEntrustAddMapper(WflEtrEntrustAddMapper wflEtrEntrustAddMapper) {
		EtrCommonCashPositionDealService.wflEtrEntrustAddMapper = wflEtrEntrustAddMapper;
	}

	@Autowired
    public void setPtlCapacctInfoMapper(PtlCapacctInfoMapper ptlCapacctInfoMapper) {
        EtrCommonCashPositionDealService.ptlCapacctInfoMapper = ptlCapacctInfoMapper;
    }


    
    /**
     * 设置 etrEntrustMapper
     *
     * @param etrEntrustMapper etrEntrustMapper
     */
    @Autowired
    public void setEtrEntrustMapper(EtrEntrustMapper etrEntrustMapper) {
        EtrCommonCashPositionDealService.etrEntrustMapper = etrEntrustMapper;
    }

    /**
     * 设置 etrEntrustAddMapper
     *
     * @param etrEntrustAddMapper etrEntrustAddMapper
     */
    @Autowired
    public void setEtrEntrustAddMapper(EtrEntrustAddMapper etrEntrustAddMapper) {
        EtrCommonCashPositionDealService.etrEntrustAddMapper = etrEntrustAddMapper;
    }

    /**
     * 设置 etrEntrustDetailMapper
     *
     * @param etrEntrustDetailMapper etrEntrustDetailMapper
     */
    @Autowired
    public void setEtrEntrustDetailMapper(EtrEntrustDetailMapper etrEntrustDetailMapper) {
        EtrCommonCashPositionDealService.etrEntrustDetailMapper = etrEntrustDetailMapper;
    }

    /**
     * 设置 wflEtrEntrustDetailMapper
     *
     * @param wflEtrEntrustDetailMapper wflEtrEntrustDetailMapper
     */
    @Autowired
    public void setWflEtrEntrustDetailMapper(WflEtrEntrustDetailMapper wflEtrEntrustDetailMapper) {
        EtrCommonCashPositionDealService.wflEtrEntrustDetailMapper = wflEtrEntrustDetailMapper;
    }

    /**
     * 根据债券信息和日期获取最新市价
     * 
     * @param map 参数
     * @return 最新市价
     */
    public static BigDecimal find最新市价(Map<String, Object> map) {
        BigDecimal ret = etrCommonCashPositionMapper.findLatestMarketPrice(map);
        if (ret == null) {
            ret = etrCommonCashPositionMapper.findLatestInterfMarketPrice(map);
        }
        return ret;
    }

    public static Collection<? extends EtrSecPositionFlowDto> findNegateSecFlowByEntrustId(String entrustId, String tradeId) {
        List<EtrSecPositionFlowDto> flows = etrCommonCashPositionMapper.findNegateSecFlowByEntrustId(entrustId);
        if (flows != null && !flows.isEmpty()) {
            flows.forEach(flow -> {
                flow.setSeqno(IDGenerator.generate());
                flow.setbTradeId(flow.getTradeId());
                flow.setTradeId(tradeId);
                flow.setUpdateTime(new Date());
                flow.setCreateTime(new Date());
            });
        }
        return flows;
    }

    /**
     * 查询债券基本信息
     *
     * @param finprodId 金融产品代码
     * @return 债券基本信息
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public SecInfoModelDto getSecInfoModel(String finprodId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        map.put("sysDate", DateUtil.platDateFromCache());
        map.put("YES_NO_Y", DictCodeList.是否_是.getValue());
        return etrCommonCashPositionMapper.getSecInfoModel(map);
    }
    /**
     * 查询债券利率重置信息
     *
     * @param finprodId 金融产品代码
     * @return 债券利率重置信息
     * @see
     * @since 1.0
     */
    public List<FinCashCalcRule> selectBondsIntResetList(String finprodId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("finprodId", finprodId);
        param.put("finprodType", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        return etrCommonCashPositionMapper.selectBondsIntResetList(param);
    }
    /**
     * 获取债券付息计划
     *
     * @param finprodId 金融产品代码
     * @return 债券付息计划
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public List<FinCashPlanDto> getFinCashPlan(String finprodId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        return etrCommonCashPositionMapper.getFinCashPlan(map);
    }
    /**
     * a.当前债券面额取得（根据还本计划计算）
     *
     * @param param 参数
     * @return a.当前债券面额取得（根据还本计划计算）
     * @see
     * @since 1.0
     */
    public BigDecimal getCurSecAmt(Map<String, Object> param) {
        param.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());
        param.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        String settleDate = (String) param.get("settleDate");
        param.put("settleDate", DateUtil.parse(settleDate));
        return etrCommonCashPositionMapper.getCurSecAmt(param);
    }
    /**
     *
     * @return
     */
    public BigDecimal gen应计利息(String finprodId,Date settleDate){
        Map<String, Object> tempParamMap = new HashMap<String, Object>();
        tempParamMap.put("finprodId", finprodId);
        // b.获取计息区间投资端利率报价信息（债券的利率重置信息）
//         List<BondsIntReset> bondsIntResetList = transactionBond001Mapper.getInvRateQuo(dto);
        List<FinCashCalcRule> finCashCalcRuleList = selectBondsIntResetList(finprodId);

        // c.获取债券的付息计划信息
        List<FinCashPlanDto> finCashPlanList = getFinCashPlan(finprodId);
        // d.获取债券基本资料信息
        SecInfoModelDto secInfoModelDto = getSecInfoModel(finprodId);
        SecInfoModel secInfoModel = this.toSecInfoModel(secInfoModelDto);
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
        List<PaperIrReset> paperIrResetList = finCashCalcRuleList.stream().map(item -> {
            PaperIrReset ret = new PaperIrReset();
            // BeanUtil.copyPropertiesIgnoreNull(ret, item);
            ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            ret.setParRate(item.getYield());
            ret.setParRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            return ret;
        }).collect(Collectors.toList());
        if (paperIrResetList != null && paperIrResetList.size() > 0) {
            paperIrResetList.get(0).setBaseRateEffDate(BasicUtil.dateToLocal(secInfoModelDto.getStartDate()));
        }
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            //多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        AccurIntCalInParameter accurIntCalInParameter = new AccurIntCalInParameter();
        // 计算日
//            accurIntCalInParameter.setCalDate(this.toLocalDate(dto.getSettleDate()));
//        accurIntCalInParameter.setCalDate(this.toLocalDate(tempSettleDate));
        accurIntCalInParameter.setCalDate(this.toLocalDate(settleDate));
        // 债券基本资料
        accurIntCalInParameter.setSecInfoModel(secInfoModel);
        // 付息计划
        accurIntCalInParameter.setSecIntScheduleList(invScheduleList);
        // 利率重置信息
        accurIntCalInParameter.setPaperIrResetList(paperIrResetList);
        // 从计算工厂中创建应计利息计算器
        AccurIntCalculator calc = CalculatorFactory.createAccurIntCalculator();
        // 计算利息
        AccurIntCalOutParameter accurIntCalOutParameter = calc.calculate(accurIntCalInParameter);
        // 百元面值应计利息
        BigDecimal accurInt = BigDecimal.ZERO;
        final int SCALE = 12;
        accurInt = ArithmeticUtil.round(accurIntCalOutParameter.getInterest(), SCALE, RoundingMode.HALF_UP);
        // a.当前债券面额取得（根据还本计划计算）
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("finprodId", finprodId);
//            paraMap.put("settleDate", DateUtil.format(dto.getSettleDate()));
        paraMap.put("settleDate", DateUtil.format(settleDate));
        BigDecimal curSecAmt = getCurSecAmt(paraMap);
//        单位应计利息
        BigDecimal 单位应计利息 = ArithmeticUtil.multiply(ArithmeticUtil.divide(accurInt, new BigDecimal(100), SCALE), curSecAmt);
        return  单位应计利息;
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
    /**
     * 设置 etrCashPositionFlowMapper
     *
     * @param etrCashPositionFlowMapper etrCashPositionFlowMapper
     */
    @Autowired
    public void setEtrCashPositionFlowMapper(EtrCashPositionFlowMapper etrCashPositionFlowMapper) {
        EtrCommonCashPositionDealService.etrCashPositionFlowMapper = etrCashPositionFlowMapper;
    }

    /**
     * 设置 etrSecPositionFlowMapper
     *
     * @param etrSecPositionFlowMapper etrSecPositionFlowMapper
     */
    @Autowired
    public void setEtrSecPositionFlowMapper(EtrSecPositionFlowMapper etrSecPositionFlowMapper) {
        EtrCommonCashPositionDealService.etrSecPositionFlowMapper = etrSecPositionFlowMapper;
    }

    /**
     * 设置 etrCashPositionMapper
     *
     * @param etrCashPositionMapper etrCashPositionMapper
     */
    @Autowired
    public void setEtrCashPositionMapper(EtrCashPositionMapper etrCashPositionMapper) {
        EtrCommonCashPositionDealService.etrCashPositionMapper = etrCashPositionMapper;
    }

    /**
     * 设置 etrSecPositionMapper
     *
     * @param etrSecPositionMapper etrSecPositionMapper
     */
    @Autowired
    public void setEtrSecPositionMapper(EtrSecPositionMapper etrSecPositionMapper) {
        EtrCommonCashPositionDealService.etrSecPositionMapper = etrSecPositionMapper;
    }

    /**
     * 设置 etrCommonCashPositionMapper
     *
     * @param etrCommonCashPositionMapper etrCommonCashPositionMapper
     */
    @Autowired
    public void setEtrCommonCashPositionMapper(EtrCommonCashPositionMapper etrCommonCashPositionMapper) {
        EtrCommonCashPositionDealService.etrCommonCashPositionMapper = etrCommonCashPositionMapper;
    }

    @Autowired
    public void setBussRepoSecService(BussRepoSecService bussRepoSecService) {
        EtrCommonCashPositionDealService.bussRepoSecService = bussRepoSecService;
    }

    public static String select组合托管户(String portfolioId, String ccy, String capacctType) {
        List<StlCapacctInfo> stlCapacctInfos = etrCommonCashPositionMapper.selectProdCapacctInfoList(portfolioId, ccy,
            capacctType);
        if (stlCapacctInfos != null && !stlCapacctInfos.isEmpty()) {
            return stlCapacctInfos.get(0).getAcctNo();
        }
        logger.error("该组合（" + portfolioId + "）没有这个币种（" + ccy + "）托管户");
        return null;
    }

    public static String select通道资金账户(String chlId) {
        List<StlCapacctInfo> stlCapacctInfos = etrCommonCashPositionMapper.selectChlCapacctInfoList(chlId);
        if (stlCapacctInfos != null && !stlCapacctInfos.isEmpty()) {
            return stlCapacctInfos.get(0).getAcctNo();
        }
        logger.error("该通道（" + chlId + "）没有资金账户");
        return null;
    }

    /**
     * 检索交易关联资金账户共通
     * 
     * @param secManageAcctId
     * @param ccy
     * @return
     */
    public static String select资金账户(String secManageAcctId, String ccy) {
        if (StringUtil.isNotEmpty(secManageAcctId)) {
            List<StlCapacctInfo> stlCapacctInfos = etrCommonCashPositionMapper.selectDealCapacctInfoList(secManageAcctId, ccy);
            if (stlCapacctInfos != null && !stlCapacctInfos.isEmpty()) {
                return stlCapacctInfos.get(0).getAcctNo();
            }
        }
        logger.error("该证券托管户（" + secManageAcctId + "）没有这个币种（" + ccy + "）的资金账户");
        return null;
    }

    /**
     * 维护流水入数据库并更新表
     * 
     * @param etrPositionHoldDto 持久化参数
     */
    public static void 持久化流水Dto(EtrPositionHoldDto etrPositionHoldDto) {
        if (etrPositionHoldDto != null && etrPositionHoldDto.getPtlCashPositionFlows() != null
            && !etrPositionHoldDto.getPtlCashPositionFlows().isEmpty()) {
            List<EtrCashPositionFlowDto> 母组合流水 = new ArrayList<>();
            etrPositionHoldDto.getPtlCashPositionFlows().forEach(etrCashFlowDto -> {
                if (StringUtil.isNotEmpty(etrCashFlowDto.getAcctId())) {
                    etrCashPositionFlowMapper.insertSelective(etrCashFlowDto);
                }
                // 影响母组合持仓的情况
                if (etrPositionHoldDto.isDealEtr()) {
                    String 母组合代码 = AllTradeCommonUtil.exchangePortfolio(etrCashFlowDto.getPortfolioId());
                    if (StringUtil.isNotEmpty(母组合代码) && !母组合代码.equals(etrCashFlowDto.getPortfolioId())) {
                        EtrCashPositionFlowDto dto = new EtrCashPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(dto, etrCashFlowDto);
                        dto.setSeqno(getSeqNo());
                        dto.setPortfolioId(母组合代码);
                        母组合流水.add(dto);
                        etrCashPositionFlowMapper.insertSelective(dto);
                    }
                }
            });

            // 处理逻辑 现金流水
            dealCommandBalanceList(etrPositionHoldDto.getPtlCashPositionFlows());

            if (!母组合流水.isEmpty()) {
                dealCommandBalanceList(母组合流水);
            }
        }
        if (etrPositionHoldDto != null && etrPositionHoldDto.getPtlSecPositionFlows() != null
            && !etrPositionHoldDto.getPtlSecPositionFlows().isEmpty()) {
            List<EtrSecPositionFlowDto> 母组合流水 = new ArrayList<>();
            etrPositionHoldDto.getPtlSecPositionFlows().stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()->
                    new TreeSet<>(Comparator.comparing(item->item.getSeqno()))),ArrayList::new)).forEach(etrSecFlowDto -> {
                etrSecPositionFlowMapper.insertSelective(etrSecFlowDto);
                // 影响母组合持仓的情况
                if (etrPositionHoldDto.isDealEtr()) {
                    String 母组合代码 = AllTradeCommonUtil.exchangePortfolio(etrSecFlowDto.getPortfolioId());
                    if (StringUtil.isNotEmpty(母组合代码) && !母组合代码.equals(etrSecFlowDto.getPortfolioId())) {
                        EtrSecPositionFlowDto dto = new EtrSecPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(dto, etrSecFlowDto);
                        dto.setPortfolioId(母组合代码);
                        dto.setSeqno(getSeqNo());
                        母组合流水.add(dto);
                        etrSecPositionFlowMapper.insertSelective(dto);
                    }
                }
            });
            // 处理逻辑 持仓流水
            dealCommandPositionList(etrPositionHoldDto.getPtlSecPositionFlows());
            if (!母组合流水.isEmpty()) {
                dealCommandPositionList(母组合流水);
            }
        }
    }

    public static EtrSecPositionFlowDto getEtrSecPositionFlowDtoFromEtr(EntrustCommonDto entrustCommonDto) {
        EtrSecPositionFlowDto ret = new EtrSecPositionFlowDto();
        ret.setSeqno(getSeqNo());
        String secAcctId = null;
        if (StringUtil.isNotEmpty(entrustCommonDto.getEtrEntrustAdd())) {
            secAcctId = entrustCommonDto.getEtrEntrustAdd().getSecAcctId();
        }
        if (StringUtil.isEmpty(secAcctId) && StringUtil.isEmpty(entrustCommonDto.getEtrEntrust().getChlId())) {
            secAcctId = "999999";
        }
        else if (StringUtil.isNotEmpty(entrustCommonDto.getEtrEntrust().getChlId())) {
            secAcctId = entrustCommonDto.getEtrEntrust().getChlId();
        }

        ret.setSecAcctId(secAcctId);
        ret.setInvAim(StringUtil.isEmpty(
            entrustCommonDto.getEtrEntrust().getInvAim()) ? "999999" : entrustCommonDto.getEtrEntrust().getInvAim());
        ret.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
        ret.setTradeId(entrustCommonDto.getEtrEntrust().getEntrustId());
        ret.setFinprodId(entrustCommonDto.getEtrEntrust().getFinprodId());
        ret.setPortfolioId(entrustCommonDto.getEtrEntrust().getPortfolioId());
        ret.setBranch(0l);
        ret.setCcy(entrustCommonDto.getEtrEntrust().getCcy());
        ret.setCdate(entrustCommonDto.getEtrEntrust().getVdate());
        return ret;
    }

    public static EtrCashPositionFlowDto getEtrCashPositionFlowDtoFromEtr(EntrustCommonDto entrustCommonDto) {
        EtrCashPositionFlowDto ret = new EtrCashPositionFlowDto();
        ret.setSeqno(getSeqNo());
        ret.setCcy(entrustCommonDto.getEtrEntrust().getCcy());
        ret.setOrderId(entrustCommonDto.getEtrEntrust().getEntrustId());
        ret.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
        ret.setChlFinprodId(entrustCommonDto.getEtrEntrust().getChlId());
        ret.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());

        ret.setPortfolioId(entrustCommonDto.getEtrEntrust().getPortfolioId());
        ret.setCdate(entrustCommonDto.getEtrEntrust().getVdate());
        return ret;
    }

    public static BigDecimal getEtrFinishAmt(String entrustId) {
        return etrCommonCashPositionMapper.getEtrFinishAmt(entrustId);
    }

    /**
     * 判断是否是内部交易
     * @param counterId 交易对手
     * @return 是否是内部交易
     */
    public static boolean isInTrade(String counterId){
        if (StringUtil.isEmpty(counterId)) {
            return false;
        }
        Map<String,Object>param = new HashMap<>();
        param.put("COUNTER_TYPE", DictCodeListExt1.交易对手类型_内部交易对手.getValue());
        param.put("YES_NO_Y", DictCodeList.是否_是.getValue());
        param.put("YES_NO_N", DictCodeList.是否_否.getValue());
        param.put("counterId", counterId);
        String counterType = etrCommonCashPositionMapper.getCounterType(param);
        return StringUtil.equals(counterType,"N");
    }
    /**
     * 获取通道性质
     * 
     * @param chlId 通道代码
     * @return 通道性质
     */
    public static String queryChlType(String chlId) {
        if (StringUtil.isNotEmpty(chlId)) {
            FinProductAddMapper finProductMapperAdd = SpringContextUtil.getBean(FinProductAddMapper.class);
            FinProductAdd payType = finProductMapperAdd.selectByPrimaryKey(chlId);
            if (StringUtil.isNotEmpty(payType)) {
                return payType.getPayType();
            }
        }
        return null;
    }

    /**
     * 判断是否是净额结算（结算品种判断） type1:金融产品类型（投管分类） ,必填 type2:交易方式 ,非必填，股票必填 type3:交易场所,非必填，基金必填 type4:交易方向,非必填，基金必填 type5:交易所基金类型,非必填，基金必填 code:金融产品代码 ,必填
     * 
     * @return 是否
     */
    public boolean isNetSettlement(String type1, String type2, String type3, String type4, String type5, String code) {
        /*
         * 债券 除私募债、资产支持证券、123号段的公司债外的其他债券 股票 除交易方式为大宗交易专场外的其他股票交易 场内基金 基金买卖、实时申赎货币基金的申购赎回 回购 质押式竞价回购（首期和到期）
         */
        // 债券
        if (StringUtil.equals(type1, DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
            String finprodId = etrCommonCashPositionMapper.selectFinProduct(code);// code传金融产品代码
            if (finprodId != null) {
                return false;
            }
            return true;
        }// 债券质押式正回购,债券质押式逆回购
        else if (StringUtil.equals(type1, DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                 || StringUtil.equals(type1, DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
            String finprodId = etrCommonCashPositionMapper.selectFinTradeProduct(code);// code传金融产品代码
            if (finprodId != null) {
                return true;
            }
            return false;
        }
        // 股票
        else if (StringUtil.equals(type1, DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
            if ("04".equals(type2)) {// 大宗交易
                return false;
            }
            return true;
        }
        // 基金
        else if (StringUtil.equals(type1, DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
            if ("03".equals(type3) || "04".equals(type3)) {// 如果是上交所或深交所
                if ("05".equals(type5)) {// 交易所基金类型为实时申赎货币基金
                    if ("T04".equals(type4) || "T06".equals(type4)) {// 交易方向为申购或赎回
                        return true;
                    }
                    return false;
                }
                else {
                    if ("T01".equals(type2) || "T02".equals(type2)) {// 交易方向为买入或卖出
                        return true;
                    }
                    return false;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * 获取债券面值
     * 
     * @param assetCode 债券代码
     * @param tradeDate 交易日期
     * @return 债券面值
     */
    public BigDecimal gen债券面值(String assetCode, Date tradeDate) {
        // TODO
        return BigDecimal.ONE;
    }

    /**
     * 查询可用持仓 <功能详细描述>
     * 
     * @param etrSecPositionDto
     * @return
     * @see
     * @since 1.0
     */
    public static BigDecimal selectAvailablePositionAmount(EtrSecPositionDto etrSecPositionDto) {
        BigDecimal bigDecimal = etrCommonCashPositionMapper.selectAvailablePositionAmount(etrSecPositionDto);
        return bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
    }

    /**
     * 根据list处理持仓流水 <功能详细描述>
     * 
     * @param list
     * @see
     * @since 1.0
     */
    public static void dealCommandPositionList(List<EtrSecPositionFlowDto> list) {
        for (EtrSecPositionFlowDto dto : list) {
            dealCommandPosition(dto);
        }
    }

    /**
     * 插入持仓流水 <功能详细描述>
     * 
     * @param positionFlowDto
     * @return
     * @see
     * @since 1.0
     */
    public static void dealCommandPosition(EtrSecPositionFlowDto positionFlowDto) {
        // 流水号
        // 输入参数.持仓类型 IN (数据字典缓存.持仓类型.委托指令可用)
        if (DictCodeListExt1.持仓类型_委托指令可用.getValue().equals(positionFlowDto.getHodingType())) {
            EtrSecPosition position = getPtlSecPosition(positionFlowDto);
            BigDecimal amount = etrCommonCashPositionMapper.selectSecPosition(positionFlowDto);
            if (amount == null) {
                // 如果当天的持仓数据没有，取当天之前最近一天的数据
                // 查询最近的那一天
                Date maxCdate = etrCommonCashPositionMapper.selectSecPositionMaxCdate(positionFlowDto);
                if (maxCdate == null) {
                    // modify by etliu 汇总表更新并发问题解决 20210511 start
                    try {
                        etrSecPositionMapper.insertSelective(position);
                    } catch (Exception e) {
                        etrCommonCashPositionMapper.updateByPrimaryKeySelectiveForSecFlow(position);
                    }
                    // modify by etliu 汇总表更新并发问题解决 20210511 end

                }
                else {
                    EtrSecPositionFlowDto tempPositionFlowDto = new EtrSecPositionFlowDto();
                    BeanUtils.copyProperties(positionFlowDto, tempPositionFlowDto);
                    tempPositionFlowDto.setCdate(maxCdate);

                    // modify by etliu 汇总表更新并发问题解决 20210511 start
                    BigDecimal flowAmount = position.getAmount();
                    BigDecimal tempAmount = etrCommonCashPositionMapper.selectSecPosition(tempPositionFlowDto);
                    position.setAmount(ArithmeticUtil.doAdd(position.getAmount(), tempAmount));
                    try {
                        etrSecPositionMapper.insertSelective(position);
                    } catch (Exception e) {
                        position.setAmount(flowAmount);
                        etrCommonCashPositionMapper.updateByPrimaryKeySelectiveForSecFlow(position);
                    }
                    // modify by etliu 汇总表更新并发问题解决 20210511 end
                }
            }
            else {
                // modify by etliu 汇总表更新并发问题解决 20210511 start
//                position.setAmount(ArithmeticUtil.doAdd(position.getAmount(), amount));
//                etrSecPositionMapper.updateByPrimaryKeySelective(position);
                etrCommonCashPositionMapper.updateByPrimaryKeySelectiveForSecFlow(position);
                // modify by etliu 汇总表更新并发问题解决 20210511 end

            }

            // 2.2.2 检索组合持仓的最大日期
            String maxCdate = etrCommonCashPositionMapper.selectSecMaxCdate(positionFlowDto);
            String cDate = DateUtil.format(positionFlowDto.getCdate());
            // 2.2.3 上面检索出的最大日期 <> 输入参数.日期，将输入参数.日期到2.2.2检索出来的最大日期的数据全部更新
            if (maxCdate != null && !maxCdate.equals(cDate)) {
                String minDate = maxCdate.compareTo(cDate) > 0 ? DateUtil.addDay(cDate, 1) : maxCdate;
                String maxDate = maxCdate.compareTo(cDate) < 0 ? DateUtil.addDay(cDate, -1) : maxCdate;
                Map<String, Object> params = BeanUtil.object2Map(positionFlowDto);
                params.put("minDate", minDate);
                params.put("maxDate", maxDate);
                etrCommonCashPositionMapper.updateSecPosition(params);
            }
        }
    }

    private static EtrSecPosition getPtlSecPosition(EtrSecPositionFlowDto positionFlowDto) {
        EtrSecPosition position = new EtrSecPosition();
        position.setAmount(positionFlowDto.getAmount());
        position.setCcy(positionFlowDto.getCcy());
        position.setpFinprodId(positionFlowDto.getpFinprodId());
        position.setPortfolioId(positionFlowDto.getPortfolioId());
        position.setSecAcctId(positionFlowDto.getSecAcctId());
        position.setFinprodId(positionFlowDto.getFinprodId());
        position.setBranch(positionFlowDto.getBranch());
        position.setInvAim(positionFlowDto.getInvAim());
        position.setHodingType(positionFlowDto.getHodingType());
        position.setCdate(positionFlowDto.getCdate());
        position.setStatus(positionFlowDto.getStatus());
        position.setCreateTime(positionFlowDto.getCreateTime());
        position.setUpdateUser(positionFlowDto.getUpdateUser());
        position.setUpdateTime(positionFlowDto.getUpdateTime());
        position.setWorkflowId(positionFlowDto.getWorkflowId());
        position.setCreateUser(positionFlowDto.getCreateUser());
        position.setCreateDept(positionFlowDto.getCreateDept());
        position.setIsExistNew(positionFlowDto.getIsExistNew());
        return position;
    }

    /**
     * 根据持仓流水dto获取指令持仓 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public static EtrSecPosition getEtrSecPositionInfo(EtrSecPositionFlowDto dto) {
        EtrSecPosition etrSecPosition = new EtrSecPosition();
        // 组合代码
        etrSecPosition.setPortfolioId(dto.getPortfolioId());
        // 证券管理账户/通道代码
        etrSecPosition.setSecAcctId(dto.getSecAcctId());
        // 金融产品代码
        etrSecPosition.setFinprodId(dto.getFinprodId());
        // 分支序号
        etrSecPosition.setBranch(dto.getBranch());
        // 持仓类型
        etrSecPosition.setHodingType(dto.getHodingType());
        // 日期
        etrSecPosition.setCdate(dto.getCdate());
        // 币种
        etrSecPosition.setCcy(dto.getCcy());
        // 数量
        etrSecPosition.setAmount(dto.getAmount());
        // 母金融产品代码
        etrSecPosition.setpFinprodId(dto.getpFinprodId());
        // 单位面值
        etrSecPosition.setFaceValue(null);
        etrSecPosition.setInvAim(dto.getInvAim());
        return etrSecPosition;
    }

    /**
     * 查询指令可用现金
     * 
     * @param dto
     * @return
     */
    public BigDecimal selectCommandAvailableCash(PtlCashPositionFlowDto dto) {
        BigDecimal val = BigDecimal.ZERO;
        String hodingType = DictCodeListExt1.持仓类型_委托指令可用.getValue();
        dto.setHodingType(hodingType);
       //BigDecimal availableCash = etrCommonCashPositionMapper.selectCommandAvailableCash(dto);
        BigDecimal availableCash = etrCommonCashPositionMapper.selectCommandAvailableCashFlowSum(dto);
        BigDecimal advanceCash = selectCommandAdvanceCash(dto);
        dto.setHodingType(DictCodeListExt1.持仓类型_人工调整.getValue());
        BigDecimal 人工调整流水 = etrCommonCashPositionMapper.selectManualCashFlow(dto);
        if (availableCash != null) {
            val = ArithmeticUtil.doAdd(val, availableCash);
        }
        if (advanceCash != null) {
            val = ArithmeticUtil.doAdd(val, advanceCash);
        }
        if (availableCash == null && advanceCash == null){
            return null;    
        }
        return ArithmeticUtil.doAdd(val, 人工调整流水);
    }

    /**
     * 查询指令预增现金
     * 
     * @param dto
     * @return
     */
    public BigDecimal selectCommandAdvanceCash(PtlCashPositionFlowDto dto) {
        String hodingType = DictCodeListExt1.持仓类型_委托指令预增.getValue();
        dto.setHodingType(hodingType);
        //return etrCommonCashPositionMapper.selectCommandAvailableCash(dto);
        return etrCommonCashPositionMapper.selectCommandAvailableCashFlowSum(dto);
    }

    /**
     * 根据list处理现金流水 <功能详细描述>
     * 
     * @param list
     * @see
     * @since 1.0
     */
    public static void dealCommandBalanceList(List<EtrCashPositionFlowDto> list) {
        for (EtrCashPositionFlowDto dto : list) {
            dealCommandBalance(dto);
        }
    }

    /**
     * 插入现金流水 <功能详细描述>
     * 
     * @param positionFlowDto
     * @see
     * @since 1.0
     */
    public static void dealCommandBalance(EtrCashPositionFlowDto positionFlowDto) {
        EtrCashPosition position = new EtrCashPosition();
        BeanUtil.copyPropertiesIgnoreNull(position, positionFlowDto);
        positionFlowDto.setNeedAcctType(false);
        BigDecimal amount = etrCommonCashPositionMapper.selectCashPosition(positionFlowDto);
        if (amount == null) {
            // 如果当天的持仓数据没有，取当天之前最近一天的数据
            // 查询最近的那一天
            Date maxCdate = etrCommonCashPositionMapper.selectCashPositionMaxCdate(positionFlowDto);
            if (maxCdate == null) {
                // add by etliu 汇总表更新并发问题解决 20210511 start
                try {
                    etrCashPositionMapper.insert(position);
                } catch (Exception e) {
                    etrCommonCashPositionMapper.updateByPrimaryKeySelectiveForFlow(position);
                }
                // add by etliu 汇总表更新并发问题解决 20210511 end
            }
            else {
                EtrCashPositionFlowDto tempPositionFlowDto = new EtrCashPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(tempPositionFlowDto, positionFlowDto);
                tempPositionFlowDto.setCdate(maxCdate);

                // add by etliu 汇总表更新并发问题解决 20210511 start
                BigDecimal flowAmount = position.getAmount();
                BigDecimal tempAmount = etrCommonCashPositionMapper.selectCashPosition(tempPositionFlowDto);
                position.setAmount(ArithmeticUtil.doAdd(position.getAmount(), tempAmount));
                try {
                    etrCashPositionMapper.insert(position);
                } catch (Exception e) {
                    position.setAmount(flowAmount);
                    etrCommonCashPositionMapper.updateByPrimaryKeySelectiveForFlow(position);
                }
                // add by etliu 汇总表更新并发问题解决 20210511 end

            }
        }
        else {
            // add by etliu 汇总表更新并发问题解决 20210511 start
//            position.setAmount(ArithmeticUtil.doAdd(position.getAmount(), amount));
//            etrCashPositionMapper.updateByPrimaryKeySelective(position);
            etrCommonCashPositionMapper.updateByPrimaryKeySelectiveForFlow(position);
            // add by etliu 汇总表更新并发问题解决 20210511 end

        }

        // 2.2.2 检索组合持仓的最大日期
        String maxCdate = etrCommonCashPositionMapper.selectMaxCdate(positionFlowDto);
        String cDate = DateUtil.format(positionFlowDto.getCdate());
        // 2.2.3 上面检索出的最大日期 <> 输入参数.日期，将输入参数.日期到2.2.2检索出来的最大日期的数据全部更新
        if (maxCdate != null && !maxCdate.equals(cDate)) {
            if (StringUtil.isNotEmpty(positionFlowDto.getAmount())) {
                String minDate = maxCdate.compareTo(cDate) > 0 ? DateUtil.addDay(cDate, 1) : maxCdate;
                String maxDate = maxCdate.compareTo(cDate) < 0 ? DateUtil.addDay(cDate, -1) : maxCdate;
                Map<String, Object> params = BeanUtil.object2Map(positionFlowDto);
                params.put("minDate", minDate);
                params.put("maxDate", maxDate);
                etrCommonCashPositionMapper.updatePosition(params);
            }
        }
        // //流水号
        // //dto.setSeqno(getSeqNo());
        // //etrCashPositionFlowMapper.insertSelective(dto);
        // //检索指令现金余额数据，存在的情况下更新，不存在的情况下插入
        // List<EtrCashPosition> list = etrCommonCashPositionMapper.selectCommandBalance(dto);
        // if(!StringUtil.isEmptyList(list)) {
        // etrCommonCashPositionMapper.updateCommandBalance(dto);
        // }
        // else {
        // EtrCashPosition etrCashPosition = getEtrCashPositionInfo(dto);
        // etrCashPositionMapper.insertSelective(etrCashPosition);
        // }
        // //检索出的最大日期
        // Date maxCdate = etrCommonCashPositionMapper.selectCommandBalanceMaxCdate(dto);
        // //输入参数.日期，将输入参数.日期到2.2.2检索出来的最大日期的数据全部更新
        // if(maxCdate != null && maxCdate.compareTo(dto.getCdate()) != 0) {
        // etrCommonCashPositionMapper.updateCommandBalanceByCDate(dto.getPortfolioId(), dto.getAcctId(),
        // dto.getChlFinprodId(), dto.getHodingType(), dto.getCcy(),
        // dto.getAmount(), dto.getCdate(), maxCdate);
        // }
    }

    /**
     * 根据现金流水dto获取指令现金余额 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public static EtrCashPosition getEtrCashPositionInfo(EtrCashPositionFlowDto dto) {
        EtrCashPosition etrCashPosition = new EtrCashPosition();
        // 组合代码
        etrCashPosition.setPortfolioId(dto.getPortfolioId());
        // 账号
        etrCashPosition.setAcctId(dto.getAcctId());
        // 持仓类型
        etrCashPosition.setHodingType(dto.getHodingType());
        // 日期
        etrCashPosition.setCdate(dto.getCdate());
        // 币种
        etrCashPosition.setCcy(dto.getCcy());
        // 数量
        etrCashPosition.setAmount(dto.getAmount());
        // 通道金融代码
        etrCashPosition.setChlFinprodId(dto.getChlFinprodId());
        return etrCashPosition;
    }

    /**
     * 获取持仓流水dto(组装持仓流水dto) 持仓日期 默认交易日 持仓类型 默认可用
     * 
     * @param finProductTradeDto
     * @return
     */
    public static EtrSecPositionFlowDto getEtrSecPositionFlowDto(BaseTradeProductDealDto finProductTradeDto) {
        // TODO
        // SEQNO VARCHAR(32) not null,
        // PORTFOLIO_ID VARCHAR(32) not null,
        // SEC_ACCT_ID VARCHAR(32),
        // FINPROD_ID VARCHAR(50) not null,
        // BRANCH NUMERIC(10),
        // INV_AIM VARCHAR(50),
        // HODING_TYPE VARCHAR(50) not null,
        // CDATE DATE not null,
        // CCY VARCHAR(50) not null,
        // AMOUNT NUMERIC(30, 4) not null,
        // P_FINPROD_ID VARCHAR(50),
        // TRADE_ID VARCHAR(32) not null,
        // TODO
        EtrSecPositionFlowDto ret = new EtrSecPositionFlowDto();
        ret.setSeqno(getSeqNo());
        String secAcctId = finProductTradeDto.getTrdProductDeal().getSecManageAcctId();
        if (StringUtil.isEmpty(secAcctId) && (StringUtil.isEmpty(finProductTradeDto.getTrdProductDeal().getChlId())
            || "null".equalsIgnoreCase(finProductTradeDto.getTrdProductDeal().getChlId()))) {
            secAcctId = "999999";
        }
        else if (StringUtil.isNotEmpty(finProductTradeDto.getTrdProductDeal().getChlId())
            && !"null".equalsIgnoreCase(finProductTradeDto.getTrdProductDeal().getChlId())) {
            secAcctId = finProductTradeDto.getTrdProductDeal().getChlId();
        }

        ret.setSecAcctId(secAcctId);
        ret.setInvAim((StringUtil.isEmpty(finProductTradeDto.getTrdProductDeal().getInvAim())
                || "null".equalsIgnoreCase(finProductTradeDto.getTrdProductDeal().getInvAim()))
                ? "999999" : finProductTradeDto.getTrdProductDeal().getInvAim());
        ret.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
        ret.setTradeId(finProductTradeDto.getTrdProductDeal().getTradeId());
        ret.setFinprodId(finProductTradeDto.getTrdProductDeal().getFinprodId());
        ret.setPortfolioId(finProductTradeDto.getTrdProductDealAdd().getPortfolioId());
        ret.setBranch(0l);
        ret.setCdate(finProductTradeDto.getTrdProductDeal().getTradeDate());
        ret.setCcy(finProductTradeDto.getTrdProductDeal().getCcy());
        return ret;
    }

    /**
     * 生成流水号获取
     * 
     * @return
     */
    public static String getSeqNo() {
        return IDGenerator.generate();
    }

    /**
     * 获取现金流水dto(组装现金流水dto)
     * 
     * @param finProductTradeDto
     * @return
     */
    public static EtrCashPositionFlowDto getEtrCashPositionFlowDto(BaseTradeProductDealDto finProductTradeDto) {
        // TODO
        EtrCashPositionFlowDto ret = new EtrCashPositionFlowDto();
        // SEQNO VARCHAR(32) not null,
        // PORTFOLIO_ID VARCHAR(32) not null,
        // ACCT_ID VARCHAR(50) not null,
        // HODING_TYPE VARCHAR(50) not null,
        // CDATE DATE not null,
        // CCY VARCHAR(50),
        // AMOUNT NUMERIC(30, 2),
        // ORDER_ID VARCHAR(32) not null,
        // CHL_FINPROD_ID VARCHAR(50),
        // OFFSET_FLAG VARCHAR(50) not null,
        // OFFSET_ID VARCHAR(32) not null,
        // 币种
        // 流水号
        ret.setSeqno(getSeqNo());
        ret.setCcy(finProductTradeDto.getTrdProductDeal().getCcy());
        ret.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
        // 组合代码
        ret.setChlFinprodId(finProductTradeDto.getTrdProductDeal().getChlId());
        ret.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
        ret.setPortfolioId(finProductTradeDto.getTrdProductDealAdd().getPortfolioId());
        ret.setOrderId(finProductTradeDto.getTrdProductDeal().getTradeId());
        ret.setOffsetId(finProductTradeDto.getTrdProductDealAdd().getEntrustId());
        // 确认日
        ret.setCdate(finProductTradeDto.getTrdProductDeal().getTradeDate());
        return ret;
    }

    /**
     * gen冲销现金流水生成
     * 
     * @param etrCashPositionFlowDto 正常流水
     * @param entrustId 委托代码
     * @return 冲销流水
     */
    public static EtrCashPositionFlowDto gen冲销现金流水生成(EtrCashPositionFlowDto etrCashPositionFlowDto, String entrustId) {
        EtrCashPositionFlowDto ret = new EtrCashPositionFlowDto();
        BeanUtil.copyPropertiesIgnoreNull(ret, etrCashPositionFlowDto);
        ret.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
        ret.setOffsetId(entrustId);
        ret.setSeqno(getSeqNo());
        return ret;
    }

    public static EntrustCommonDto findEntrust(String entrustId) {
    	EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustId);
        List<EtrEntrustDetail> etrEntrustDetailList=null;
        if(etrEntrust!=null&&etrEntrust.getEntrustClass()!=null&&StringUtil.equals(DictCodeListExt3.投资指令类型_多组合单资产指令.getValue(),etrEntrust.getEntrustClass())) {
            etrEntrustDetailList=etrEntrustDetailMapper.selectList(entrustId);
        }
        //流程中委托查询流程表
        if(etrEntrust == null) {
        	WflEtrEntrustKey wflEtrEntrustKey = new WflEtrEntrustKey();
        	wflEtrEntrustKey.setEntrustId(entrustId);
        	wflEtrEntrustKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        	WflEtrEntrust wflEtrEntrust = wflEtrEntrustMapper.selectByPrimaryKey(wflEtrEntrustKey);
        	if(wflEtrEntrust != null) {
        		etrEntrust = new EtrEntrust();
        		BeanUtil.copyPropertiesIgnoreNull(etrEntrust,wflEtrEntrust );
        	}
        }
        if(etrEntrustAdd == null) {
        	WflEtrEntrustAddKey wflEtrEntrustAddKey = new WflEtrEntrustAddKey();
        	wflEtrEntrustAddKey.setEntrustId(entrustId);
        	wflEtrEntrustAddKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        	WflEtrEntrustAdd wflEtrEntrustAdd = wflEtrEntrustAddMapper.selectByPrimaryKey(wflEtrEntrustAddKey);
        	if(wflEtrEntrustAdd != null) {
        		etrEntrustAdd = new EtrEntrustAdd();
        		BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd,wflEtrEntrustAdd );
        	}
        }
        if(etrEntrust!=null&&etrEntrust.getEntrustClass()!=null&&StringUtil.equals(DictCodeListExt3.投资指令类型_多组合单资产指令.getValue(),etrEntrust.getEntrustClass())&&
            CollectionUtils.isEmpty(etrEntrustDetailList)) {
            WflEtrEntrustDetailKey wflEtrEntrustDetailKey = new WflEtrEntrustDetailKey();
            wflEtrEntrustDetailKey.setEntrustId(entrustId);
            wflEtrEntrustDetailKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            List<WflEtrEntrustDetail> wflEtrEntrustDetailList = wflEtrEntrustDetailMapper.selectList(wflEtrEntrustDetailKey);
            if(!CollectionUtils.isEmpty(etrEntrustDetailList)) {
                etrEntrustDetailList=new ArrayList<>();
                wflEtrEntrustDetailList.stream().forEach(item-> {
                    EtrEntrustDetail etrEntrustDetail=new EtrEntrustDetail();
                    BeanUtil.copyPropertiesIgnoreNull(etrEntrustDetail, item);
                });
            }
        }
        EntrustCommonDto result = new EntrustCommonDto();
        result.setEtrEntrust(etrEntrust);
        result.setEtrEntrustAdd(etrEntrustAdd);
        result.setEtrEntrustDetailList(etrEntrustDetailList);
        return result;
    }

    public static BigDecimal getEtrSurplusAmt(String entrustId, String portfolioId) {
        Map<String, Object> map = new HashMap<>();
        map.put("entrustId", entrustId);
        map.put("portfolioId", portfolioId);
        map.put("OFFSET_FLAG_ZC", DictCodeList.冲正标识_正常交易.getValue());
        map.put("OFFSET_FLAG_CX", DictCodeList.冲正标识_冲销交易.getValue());
        return etrCommonCashPositionMapper.getEtrSurplusAmt(map);
    }

    public static BigDecimal getClosePrice(String finprodId, Date cdate) {
        BigDecimal 最新收盘价 = getLatestPrice(finprodId, cdate);
        if (最新收盘价 == null) {
            最新收盘价 = BigDecimal.ZERO;
        }
        return 最新收盘价.multiply(new BigDecimal("1.1"));
    }

    public static BigDecimal getLatestPrice(String finprodId, Date cdate) {
        BigDecimal 最新收盘价 = etrCommonCashPositionMapper.getClosePrice(finprodId, cdate);
        if (最新收盘价 == null) {
            最新收盘价 = etrCommonCashPositionMapper.getInterfClosePrice(finprodId, cdate);
        }
        return 最新收盘价;
    }

    /**
     * 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量， 如取不到估值核算中前一个自然日的数据，计算方式见算法文档； 画面.应计利息=PTL_SEC_VALUTION.TDY_INTINCEXP/PTL_SEC_VALUTION.SHARE_AMT
     * （PTL_SEC_VALUTION.日期 = 画面.交割日-1 ，取不到，取最大日期对应数据）
     *
     * @param ptlSecValutionKey 参数
     * @return 应计利息
     * @see
     * @since 1.0
     */
    public static BigDecimal get应计利息FromValution(PtlSecValutionKey ptlSecValutionKey) {
        if (ptlSecValutionKey.getCdate() == null || StringUtil.isBlank(ptlSecValutionKey.getPortfolioId())
            || StringUtil.isBlank(ptlSecValutionKey.getFinprodId()) || StringUtil.isBlank(ptlSecValutionKey.getInvAim())
            || StringUtil.isBlank(ptlSecValutionKey.getSecAcctId())) {
            return null;
        }
        BigDecimal 应计利息 = null;
        PtlSecValution ptlSecValution = etrCommonCashPositionMapper.selectByPrimaryKey(ptlSecValutionKey);
        if (ptlSecValution == null) {
            ptlSecValution = etrCommonCashPositionMapper.selectPtlSecValutionData(ptlSecValutionKey);
        }

        if (ptlSecValution != null) {
            应计利息 = ArithmeticUtil.divide(ptlSecValution.getTdyIntincexp(), ptlSecValution.getShareAmt(), 12);
        }

        return 应计利息;
    }

    /**
     * 通过委托代码获取首次委托最大净价
     *
     * @param entrustId 参数
     * @return 应计利息
     * @see
     * @since 1.0FIRST_UNIT_CPRICE2
     */
    public static BigDecimal get首次回购最大净价(String entrustId) {
        return etrCommonCashPositionMapper.selectFirstUnitCpriceMax(entrustId);
    }

    public static List<RepoInfo> findRepoInfo(String finprodId) {
        return etrCommonCashPositionMapper.findRepoInfo(finprodId);
    }

    /**
     * 根据画面条件检索可质押券信息（含投资目的）
     *
     * @param input 画面参数
     * @return List<PledgeBondInfoDto>
     */
    public List<PledgeBondInfoDto> selectAvaPledgeWithInvAim(EntrustCommonDto input) {
        Map<String, Object> param = new HashMap<>();
        param.put("portfolioId", input.getEtrEntrust().getPortfolioId());
        param.put("ps", input.getEtrEntrust().getPs());
        param.put("isWithInvAim", true);
        param.put("INV_AIM_AC", DictCodeListExt1.投资目的_摊余成本类.getValue());
        param.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue());
        param.put("INV_AIM_FVTPL", DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue());
        param.put("secAcctId", input.getEtrEntrustAdd().getSecAcctId());
        param.put("chlId", input.getEtrEntrust().getChlId());
        return bussRepoSecService.selectAvaPledgeBondDetailList(param);
    }

    /**
     * 根据画面条件检索可质押券信息（含投资目的）
     *
     * @param input 画面参数
     * @return List<PledgeBondInfoDto>
     */
    public List<PledgeBondInfoDto> selectAvaPledgeWithInvAim(BaseTradeProductDealDto input) {
        Map<String, Object> param = new HashMap<>();
        param.put("portfolioId", input.getTrdProductDealAdd().getPortfolioId());
        param.put("ps", StringUtil.equals(input.getTrdProductDeal().getFinprodType2(),
                DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue()) ?
                DictCodeList.回购拆借方向_正回购.getValue() : DictCodeList.回购拆借方向_逆回购.getValue());
        param.put("isWithInvAim", true);
        param.put("INV_AIM_AC", DictCodeListExt1.投资目的_摊余成本类.getValue());
        param.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue());
        param.put("INV_AIM_FVTPL", DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue());
        param.put("secAcctId", input.getTrdProductDeal().getSecManageAcctId());
        param.put("chlId", input.getTrdProductDeal().getChlId());
        return bussRepoSecService.selectAvaPledgeBondDetailList(param);
    }

    /**
     * 通过entrustId/bizCode检索已质押券信息
     *
     * @param param 检索参数
     * @return List<BussRepoSecDto>
     */
    public BussRepoSecDto selectBussRepoSec(Map<String, Object> param) {
        return bussRepoSecService.selectBussRepoSec(param);
    }

    /**
     * 取得股票撤销记录
     * @param tradeId
     * @return
     */
    public static EtrPositionHoldDto getRevokePosition(String tradeId) {
        List<EtrSecPositionFlowDto> flows = etrCommonCashPositionMapper.selectAllPositionByTotle(tradeId);
        List<EtrCashPositionFlowDto> cashFlows = etrCommonCashPositionMapper.selectAllCashPositionByTotle(tradeId);
        if (flows != null && !flows.isEmpty()) {
            for (EtrSecPositionFlowDto ptlSecPositionFlow : flows) {
                ptlSecPositionFlow.setAmount(ptlSecPositionFlow.getAmount().negate());
                ptlSecPositionFlow.setSeqno(getSeqNo());
            }
        }
        if (cashFlows != null && !cashFlows.isEmpty()) {
            for (EtrCashPositionFlowDto ptlCashPositionFlow : cashFlows) {
                ptlCashPositionFlow.setAmount(ptlCashPositionFlow.getAmount().negate());
                ptlCashPositionFlow.setSeqno(getSeqNo());
            }
        }
        EtrPositionHoldDto holdDto = new EtrPositionHoldDto();
        holdDto.setPtlCashPositionFlows(cashFlows);
        holdDto.setPtlSecPositionFlows(flows);
        return holdDto;
    }

    /**
     * 获取计划类净值型最新净值
     * @param finprodId
     * @param dealDate
     * @return
     */
    public static BigDecimal getPlanNetValue(String finprodId, Date dealDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        FinProduct finProduct = SpringContextUtil.getBean(FinProductMapper.class).selectByPrimaryKey(finprodId);
        if(null!=finProduct&&StringUtil.equals(finProduct.getFinprodType2(), DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue())){
            params.put("FIN_PROD_TYPE_2_F37", DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue());
        }
        else{
            params.put("FIN_PROD_TYPE_F17", BussinessDictCodeList.金融产品类型_计划类资产.getValue());
            params.put("FIN_PROD_TYPE_2_F17", BussinessDictCodeList.金融产品类型_投管用_计划类资产.getValue());
        }
        params.put("finprodId", finprodId);
        params.put("dealDate", dealDate);
        BigDecimal netValue = etrCommonCashPositionMapper.selectPlanNetValue(params);
        if (netValue == null) {
            netValue = BigDecimal.ONE;
        }
        return netValue;
    }
    
    /**
     * 回购到期时，查询原交易指令持仓并修改生效日和额度
     * @param tradeId
     * @return
     */
    public static List<EtrSecPositionFlowDto> selectAllPositionByTotle(String tradeId) {
        List<EtrSecPositionFlowDto> flows = etrCommonCashPositionMapper.selectAllPositionByTotle(tradeId);
        return flows;
    }

    public List<PtlSecPositionFlow> selectSecFlow(Map<String, Object> params) {
        return etrCommonCashPositionMapper.selectSecFlow(params);
    }

    /**
     * 检索交易关联资金账户共通
     *
     * @param secManageAcctId
     * @param ccy
     * @return
     */
    public static String selectZQAcctId(String portfolioId, String ccy,String secManageAcctId) {
        PtlCapacctInfoKey key=new PtlCapacctInfoKey();
        key.setAcctNo(secManageAcctId);
        key.setCcy(ccy);
        key.setPortfolioId(portfolioId);
        PtlCapacctInfo ptlCapacctInfo = ptlCapacctInfoMapper.selectByPrimaryKey(key);
        if(ptlCapacctInfo != null){
            if(StringUtil.equals(ptlCapacctInfo.getAcctType(),DictCodeListExt1.资金账户类型_券商资金账户.getValue())){
                return ptlCapacctInfo.getAcctNo();
            }else{
                return null;
            }
        }
        return null;
    }

    public static String select证券托管户(String secAcctId) {
        return etrCommonCashPositionMapper.selectAcctId(secAcctId);
    }

	    /**
     * 场内指令已完成金额或者额度
     * @param entrustId
     * @param portfolioId
     * @return
     * @see
     * @since 1.0
     */
    public static BigDecimal getEtrExchangeFinishAmt(Map<String, Object> map) {
        map.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        map.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        map.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        return etrCommonCashPositionMapper.getEtrExchangeFinishAmt(map);
    }
    
	/**
	 * 合计标准券数量
	 * @param entrustId
	 * @return
	 */
	public BigDecimal selectSumStandardTicket(String entrustId,String ps){
		Map<String, Object> param = new HashMap<String,Object>();
		param.put("entrustId", entrustId);
		param.put("ps", ps);
		return etrCommonCashPositionMapper.selectSumStandardTicket(param);
	}


	/**
	 * 合计交易份额
	 *
	 * @param entrustId
	 * @return
	 */
	public BigDecimal selectSumShareAmt(String entrustId) {
		Map<String, Object> param = new HashMap<String,Object>();
		param.put("entrustId", entrustId);
		return etrCommonCashPositionMapper.selectSumShareAmt(param);
	}

	/**
	 * 合计交易金额
	 *
	 * @param entrustId
	 * @return
	 */
	public BigDecimal selectSumTradeAmt(String entrustId) {
		Map<String, Object> param = new HashMap<String,Object>();
		param.put("entrustId", entrustId);
		return etrCommonCashPositionMapper.selectSumTradeAmt(param);
	}

    public static BigDecimal getHkseClosePrice(String finprodId, Date cdate) {
        BigDecimal 最新收盘价 = getLatestPrePrice(finprodId, cdate);
        if (最新收盘价 == null) {
            最新收盘价 = BigDecimal.ZERO;
        }
        return 最新收盘价;
    }

    public static BigDecimal getLatestPrePrice(String finprodId, Date cdate) {
        BigDecimal 最新收盘价 = etrCommonCashPositionMapper.getPreClosePrice(finprodId, cdate);
        if (最新收盘价 == null) {
            最新收盘价 = etrCommonCashPositionMapper.getInterfPreClosePrice(finprodId, cdate);
        }
        return 最新收盘价;
    }

    //封装系列户组合
    public static List<EntrustCommonDto> findEntrustMulti(String entrustId) {
        List<EntrustCommonDto> entrustCommonDtoList=new ArrayList<>();
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustId);
        List<EtrEntrustDetail> etrEntrustDetailList=etrEntrustDetailMapper.selectList(entrustId);
        //流程中委托查询流程表
        if(etrEntrust == null) {
            WflEtrEntrustKey wflEtrEntrustKey = new WflEtrEntrustKey();
            wflEtrEntrustKey.setEntrustId(entrustId);
            wflEtrEntrustKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            WflEtrEntrust wflEtrEntrust = wflEtrEntrustMapper.selectByPrimaryKey(wflEtrEntrustKey);
            if(wflEtrEntrust != null) {
                etrEntrust = new EtrEntrust();
                BeanUtil.copyPropertiesIgnoreNull(etrEntrust,wflEtrEntrust );
            }
        }
        if(etrEntrustAdd == null) {
            WflEtrEntrustAddKey wflEtrEntrustAddKey = new WflEtrEntrustAddKey();
            wflEtrEntrustAddKey.setEntrustId(entrustId);
            wflEtrEntrustAddKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            WflEtrEntrustAdd wflEtrEntrustAdd = wflEtrEntrustAddMapper.selectByPrimaryKey(wflEtrEntrustAddKey);
            if(wflEtrEntrustAdd != null) {
                etrEntrustAdd = new EtrEntrustAdd();
                BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd,wflEtrEntrustAdd );
            }
        }
        if(StringUtil.equals(DictCodeListExt3.投资指令类型_多组合单资产指令.getValue(),etrEntrust.getEntrustClass())&&
                CollectionUtils.isEmpty(etrEntrustDetailList)) {
            WflEtrEntrustDetailKey wflEtrEntrustDetailKey = new WflEtrEntrustDetailKey();
            wflEtrEntrustDetailKey.setEntrustId(entrustId);
            wflEtrEntrustDetailKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            List<WflEtrEntrustDetail> wflEtrEntrustDetailList = wflEtrEntrustDetailMapper.selectList(wflEtrEntrustDetailKey);
            if(!CollectionUtils.isEmpty(etrEntrustDetailList)) {
                etrEntrustDetailList=new ArrayList<>();
                for(int i=0;i<wflEtrEntrustDetailList.size();i++) {
                    EtrEntrustDetail etrEntrustDetail = new EtrEntrustDetail();
                    BeanUtil.copyPropertiesIgnoreNull(etrEntrustDetail, wflEtrEntrustDetailList.get(i));
                    etrEntrustDetailList.add(etrEntrustDetail);
                }
            }
        }
        if(StringUtil.equals(DictCodeListExt3.投资指令类型_多组合单资产指令.getValue(),etrEntrust.getEntrustClass())){
            for(int i=0;i<etrEntrustDetailList.size();i++) {
                EtrEntrustDetail etrEntrustDetail =etrEntrustDetailList.get(i);
                EtrEntrust newEtrEntrust=new EtrEntrust();
                BeanUtil.copyPropertiesIgnoreNull(newEtrEntrust,etrEntrust);
                etrEntrust.setPortfolioId(etrEntrustDetail.getPortfolioId());
                etrEntrust.setEntrustNum(etrEntrustDetail.getEntrustNum());
                etrEntrust.setEntrustAmt(etrEntrustDetail.getEntrustAmt());
                EntrustCommonDto result = new EntrustCommonDto();
                result.setEtrEntrust(etrEntrust);
                result.setEtrEntrustAdd(etrEntrustAdd);
                entrustCommonDtoList.add(result);
            }
        }else{
            EntrustCommonDto result = new EntrustCommonDto();
            result.setEtrEntrust(etrEntrust);
            result.setEtrEntrustAdd(etrEntrustAdd);
            entrustCommonDtoList.add(result);
        }
        return entrustCommonDtoList;
    }


    /**
     * 场内指令已完成金额或者额度(支持多组合)
     * @return
     * @see
     * @since 1.0
     */
    public static BigDecimal getEtrExchangeFinishAmtMuilt(Map<String, Object> map) {
        BigDecimal bigDecimal=null;
        if(StringUtil.isNotEmpty(map.get("entrustClass"))&&StringUtil.equals(map.get("entrustClass"),DictCodeList.是否_是.getValue()) ) {
            bigDecimal=etrCommonCashPositionMapper.getEtrExchangeFinishAmtMuilt(map);
        }else {
            bigDecimal=getEtrExchangeFinishAmt(map);
        }
        return bigDecimal;
    }
    /**
     * 查询指令可用现金(新表)
     *
     * @param dto
     * @return
     */
    public BigDecimal queryEtrAvailableCashNew(PtlCashPositionFlowDto dto) {
        return etrCommonCashPositionMapper.queryEtrAvailableCashNew(dto);
    }

}
