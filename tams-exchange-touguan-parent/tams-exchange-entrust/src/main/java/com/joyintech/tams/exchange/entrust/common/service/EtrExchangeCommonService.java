package com.joyintech.tams.exchange.entrust.common.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.base.dao.SysPageDictlistMapper;
import com.joyintech.system.base.entity.SysPageDictlist;
import com.joyintech.system.user.dao.UserMapper;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.bond.BusiFinExercisePlanService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.exchange.entrust.common.dto.EtrExchangePortfolioAllocationRequest;
import com.joyintech.tams.exchange.entrust.common.dto.EtrExchangePortfolioAllocationResponse;
import com.joyintech.tams.exchange.entrust.common.dto.EtrExchangeProtfolioDetailRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto.EtrExchangeAgreRepTrade001Dto;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.CreditLinesDealsMapper;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.entrust.common.dto.EtrFinProductCommonDto;
import com.joyintech.tams.invest.entrust.etrautomanagementaccount.dto.EtrAutoManagementAccountDto;
import com.joyintech.tams.invest.entrust.etrbondfirstlbidding.dto.EtrBondFirstLBiddingDto;
import com.joyintech.tams.invest.entrust.etrboutrepo.dto.EtrBoutRepoDto;
import com.joyintech.tams.invest.entrust.etrbrowdepoinfo.dto.EtrBrowDepoInfoDto;
import com.joyintech.tams.invest.entrust.etrconbond.dto.EtrConBondDto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.EtrExchangeTrade001Dto;
import com.joyintech.tams.invest.entrust.etrfund.dto.EtrFundDto;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.EtrIbBondinfo002Dto;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.EtribBondInfo003Dto;
import com.joyintech.tams.invest.entrust.etribrepo.dto.EtrIbRepoinfo002Dto;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.EtrInvestBondInfoDto;
import com.joyintech.tams.invest.entrust.etrlendspecinfo.dto.EtrLendSpecInfoDto;
import com.joyintech.tams.invest.entrust.etrplanassetc.dto.EtrPlanAssetCDto;
import com.joyintech.tams.invest.entrust.etrplanassetn.dto.EtrPlanAssetNDto;
import com.joyintech.tams.invest.entrust.etrplanassetr.dto.EtrPlanAssetRDto;
import com.joyintech.tams.invest.entrust.etrproject.dto.EtrProjectDto;
import com.joyintech.tams.invest.entrust.etrprojectnet.dto.EtrProjectNetDto;
import com.joyintech.tams.invest.entrust.etrrepo.dto.EtrRepoDto;
import com.joyintech.tams.invest.entrust.etrseclend.dto.EtrSecLendDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.ActivitiType;
import com.joyintech.workflow.engine.dao.WorkflowMapper;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.entity.WorkflowEntity;
import com.joyintech.workflow.engine.service.ProcessService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: 2.1.0
 * @description: 委托公用service
 * @author: 强成西
 * @create: 2019-10-15 09:06
 **/
@Service
public class EtrExchangeCommonService extends BaseService<ITableRow> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Map<当前处理人,List<下一节点处理人>>
     */
    public static Map<String, List<String>> userMap = new ConcurrentHashMap<>();

    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    @Autowired(required = false)
    private FinProductAddMapper finProductAddMapper;
    
    @Autowired
    private FinProductMapper finProductMapper;
    @Autowired
    private CreditLinesDealsMapper creditLinesDealsMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private BusiFinExercisePlanService busiFinExercisePlanService;

    /**
     * 日期计算
     */
    @Autowired
    private DateCalService dateCalService;

    @Autowired
    private SysPageDictlistMapper sysPageDictlistMapper;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired(required = false)
    private FinProductConvertibleMapper finProductConvertibleMapper;

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;

    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    @Autowired
    private WorkflowMapper workflowMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 金融产品分类
     */
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;
    /**
     * 组合指令可用现金查询
     *
     * @param param
     * @return cashT 组合T日可用, cashT1 组合T+1日可用, cashT3 托管户可用, cashT2 通道可用， cashT4 交易所资金账户可用
     * @see
     * @since 1.0
     */
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {        
        PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
        Date sysdate = DateUtil.parse(DateUtil.getSysDate());
        if (StringUtil.isNotEmpty(param.get("acctId"))){
            dto.setAcctId((String)param.get("acctId"));
        }
        if (StringUtil.isNotEmpty(param.get("finprodId"))){
            FinProduct finProduct = finProductMapper.selectByPrimaryKey((String)param.get("finprodId"));
            List<String> holidayList = new ArrayList<>();
            if (Objects.nonNull(finProduct)) {
                holidayList = dateCalService.getDealCalendarHoliday(sysdate,DateUtil.addDay(sysdate, 15),finProduct.getCalendarId());
            }
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(sysdate).toString()), 1, holidayList, "1");
            param.put("sysDateT1", DateUtil.format(tradeDay));
        }else {
            String calId = (String)param.get("calId");
            Date vdate = DateUtil.parse((String)param.get("vdate"));
            if(StringUtil.isNotEmpty(calId)&&vdate!=null){
                List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(vdate)),DateUtil.addDay(DateUtil.parse(DateUtil.format(vdate)), 15),calId);
                Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(vdate)), 1, holidayList, "1");
                param.put("sysDateT1",DateUtil.format(tradeDay));
            }
        }
        
        dto.setCdate(sysdate);
        List<String> acctTypeList = new ArrayList<>();
        acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
        dto.setPortfolioId((String)param.get("portfolioId"));
        dto.setCcy((String)param.get("ccy"));
        Map<String, Object> result = new HashMap<>();
        dto.setAcctType(acctTypeList);

        if (baseEtrCashPositionCommService != null) {
            BigDecimal cashT = BigDecimal.ZERO;
            //期货保证金单独处理
            if(param.containsKey("acctType")&&StringUtil.equals(param.get("acctType"),DictCodeListExt1.资金账户类型_期货资金账户.getValue())){
                acctTypeList.clear();
                acctTypeList.add(DictCodeListExt1.资金账户类型_期货资金账户.getValue());
                BigDecimal cashQH = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
                cashT = ArithmeticUtil.doAdd(cashT, cashQH == null ? BigDecimal.ZERO : cashQH);
            }else{
                BigDecimal cashT3 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
                result.put("cashT3", cashT3); // 托管户可用
                cashT = ArithmeticUtil.doAdd(cashT, cashT3 == null ? BigDecimal.ZERO : cashT3);

                acctTypeList.clear();
                acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
                BigDecimal cashT4 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
                result.put("cashT4", cashT4);// 交易所资金账户可用
                cashT = ArithmeticUtil.doAdd(cashT, cashT4 == null ? BigDecimal.ZERO : cashT4);
            }

            if (StringUtil.isNotEmpty(param.get("chlFinprodId"))) {
                FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey((String)param.get("chlFinprodId"));
                result.put("payType", "02");
                if (finProductAdd != null) {
                    result.put("payType", finProductAdd.getPayType());
                }
                dto.setAcctType(null);
                dto.setChlFinprodId((String)param.get("chlFinprodId"));
                BigDecimal cashT2 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
                result.put("cashT2", cashT2 == null ? BigDecimal.ZERO : cashT2);// 通道可用
                //组合T日可用现金不加通道可用现金
                //cashT = ArithmeticUtil.doAdd(cashT, cashT2);
            }
            result.put("cashT", cashT);// 组合T日可用

            if (StringUtil.isNotEmpty(param.get("sysDateT1"))) {
                PtlCashPositionFlowDto dto2 = new PtlCashPositionFlowDto();
                Date sysdateT1 = DateUtil.parse((String)param.get("sysDateT1"));
                dto2.setCdate(sysdateT1);
                acctTypeList.clear();
                acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
                acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
                dto2.setAcctType(acctTypeList);
                dto2.setPortfolioId((String)param.get("portfolioId"));
                dto2.setCcy((String)param.get("ccy"));
                BigDecimal cashT1 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto2);
                if (StringUtil.isNotEmpty(param.get("chlFinprodId"))) {
                    FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey((String)param.get("chlFinprodId"));
                    result.put("payType", "02");
                    if (finProductAdd != null) {
                        result.put("payType", finProductAdd.getPayType());
                    }
                    dto2.setAcctType(null);
                    //组合T日可用现金不加通道可用现金
                    dto2.setChlFinprodId((String)param.get("chlFinprodId"));
                    BigDecimal cashT2 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto2);
                    result.put("cashT21", cashT2 == null ? BigDecimal.ZERO : cashT2);// 通道T+1可用
                }
                result.put("cashT1", cashT1 == null ? result.get("cashT") : cashT1);// 组合T+1日可用
                
                acctTypeList.clear();
                acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
                BigDecimal cashTT1 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto2);//组合托管户T+1可用
                result.put("cashTT1", cashTT1 == null ? BigDecimal.ZERO : cashTT1);// 组合托管户T+1可用
                
            }
        }

        //查询新表可用现金
        PtlCashPositionFlowDto dtoNew = new PtlCashPositionFlowDto();
        dtoNew.setHodingType(DictCodeListExt3.资产类型_汇总可用现金.getValue());
        dtoNew.setCdate(dto.getCdate());
        dtoNew.setPortfolioId((String)param.get("portfolioId"));
        dtoNew.setCcy((String)param.get("ccy"));
        dtoNew.setDateType(0);
        BigDecimal cashT0_new = baseEtrCashPositionCommService.queryEtrAvailableCashNew(dtoNew);
        BigDecimal cashT= new BigDecimal(result.get("cashT").toString());
        if(StringUtil.isNotEmpty(cashT0_new)){
            result.put("cashT",  ArithmeticUtil.doAdd(cashT, cashT0_new));// 组合T日可用
        }
        dtoNew.setDateType(1);
        BigDecimal cashT1_new = baseEtrCashPositionCommService.queryEtrAvailableCashNew(dtoNew);
        if(null!=result.get("cashT1")){
            BigDecimal cashT1= new BigDecimal(result.get("cashT1").toString());
            if(StringUtil.isNotEmpty(cashT1_new)){
                result.put("cashT1",  ArithmeticUtil.doAdd(cashT1, cashT1_new));// 组合T+1日可用
            }
        }
        return result;
    }

    public BigDecimal getAssetAvailablePosition(EtrSecPositionDto dto) {
        if (baseEtrCashPositionCommService != null) {
            dto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
            return baseEtrCashPositionCommService.qryAvailablePosition(dto);
        }
        return null;
    }

    /**
     * check多对多指令字段用,空判断
     * 
     * @param cellData cellData
     * @param labelName labelName
     * @param indexId indexId
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public String checkIsBlank(String cellData, String labelName, String indexId) {
        // 判断是否是空
        if (StringUtil.isBlank(cellData)) {
            return getMessage("ETR_E_0015", getMessage(labelName), indexId);
        }
        return "";
    }

    public String checkMarketQx(String cellData1,String cellData2,String indexId){
        if (cellData1.equals ("09:OTC场外") && ( StringUtil.isNotEmpty(cellData2))){
                return "交易场所为OTC场外时，期限品种不可填(行号为"+indexId+")";
        }
        return "";
    }

    public String checkMarketQs(String cellData1,String cellData3,String indexId){
        if (cellData1.equals ("09:OTC场外") && cellData3.equals ("02:T+1")){
            return "交易场所为OTC场外时，清算速度只能选T+0 (行号为"+indexId+")";
        }
        return "";
    }


    public String checkQDQ(String cellData1,String cellData2,String cellData3,String indexId){
        Date cellDateResult1 = DateUtil.parse (cellData1);
        Date cellDateResult2 = DateUtil.parse (cellData2);
        if(cellDateResult1 != null && cellData2 != null){
            long days = DateUtil.getBetweenDayNumber(cellDateResult1,cellDateResult2);
            if(cellData3.equals (String.valueOf (days)) == false){
                return "起息日与到期日的间隔天数与期限不同 (行号为"+indexId+")";
            }
            if (DateUtil.format (DateUtil.parse (cellData2),"yyyy-MM-dd").equals (DateUtil.addDay(DateUtil.format (DateUtil.parse (cellData1),"yyyy-MM-dd"),Integer.parseInt (cellData3))) == false){
                return "起息日与期限相加不等于到期日 (行号为"+indexId+")";
            }
        }
        return "";
    }

    /**
     * check多对多指令字段用,日期格式
     * 
     * @param cellData cellData
     * @param labelName labelName
     * @param indexId indexId
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public String checkIsDate(String cellData, String labelName, String indexId) {
        // 判断是否是正确的日期格式
        if (StringUtil.isNotBlank(cellData)) {
            try {
                Date result = DateUtil.parse(cellData);
                if (result == null) {
                    return getMessage("ETR_E_0016", getMessage(labelName), indexId);
                }
            }
            catch (RuntimeException e) {
                return getMessage("ETR_E_0016", getMessage(labelName), indexId);
            }
        }
        return "";
    }

    /**
     * check多对多指令字段用,组合代码是否存在
     * 
     * @param cellData cellData
     * @param labelName labelName
     * @param indexId indexId
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public String checkPortfolioIdExist(String cellData, String labelName, String indexId) {
        // 判断是否是正确的组合代码
        if (StringUtil.isNotBlank(cellData)) {
            Map<String, Object> param = new HashMap<>();
            param.put("act", true);
            param.put("initValue", cellData);
            param.put("portType01", DictCodeListExt1.组合类型_投资类组合.getValue());
            param.put("loginUserName", LoginUserUtil.getUserName());
            param.put("page", 1);
            param.put("length", 20);
            Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/selectPortfolioId", param,
                SelectOptionDto.class);
            if (selectOptionDtoPage.getRoot().isEmpty()) {
                return getMessage("ETR_E_0017", getMessage(labelName), indexId);
            }
        }
        return "";
    }
    
    /**
     * check多对多指令字段用,交易日历是否存在
     * 
     * @param cellData cellData
     * @param labelName labelName
     * @param indexId indexId
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public String checkCalendarIdExist(String cellData, String labelName, String indexId) {
        // 判断是否是正确交易日历
        if (StringUtil.isNotBlank(cellData)) {
            Map<String, Object> param = new HashMap<>();
            param.put("initValue", cellData);
            if(cellData.contains(":")){
            	param.put("initValue", cellData.split(":")[0]);
            }
            param.put("page", 1);
            param.put("length", 20);
            Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/selectCalendarId", param,
                SelectOptionDto.class);
            if (selectOptionDtoPage.getRoot().isEmpty()) {
                return getMessage("ETR_E_0017", getMessage(labelName), indexId);
            }
        }
        return "";
    }

    /**
     * check多对多指令字段用,正确的数值格式
     * 
     * @param cellData cellData
     * @param labelName labelName
     * @param indexId indexId
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public String checkIsNumber(String cellData, String labelName, String indexId) {
        // 判断是否是正确的数值格式
        if (StringUtil.isNotBlank(cellData)) {
            try {
                Double.parseDouble(cellData.replace(",", ""));
            } catch (NumberFormatException e) {
                return getMessage("ETR_E_0016", getMessage(labelName), indexId);
            }
        }
        return "";
    }

    /**
     * check多对多指令字段用,数值大于0
     * 
     * @param cellData cellData
     * @param labelName labelName
     * @param indexId indexId
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public String checkIsGtZero(String cellData, String labelName, String indexId) {
        // 判断是否数值大于0
        if (StringUtil.isNotBlank(cellData)){
            try {
                double num = Double.parseDouble(cellData.replace(",", ""));
                BigDecimal result = BigDecimal.valueOf(num);
                if (result.compareTo(BigDecimal.ZERO) <= 0) {
                    return getMessage("ETR_E_0019", getMessage(labelName), indexId);
                }
            } catch (NumberFormatException e) {
                return getMessage("ETR_E_0016", getMessage(labelName), indexId);
            }
        }
        return "";
    }

    /**
     * check多对多指令字段用,数值是否是整数
     *
     * @param cellData cellData
     * @param labelName labelName
     * @param indexId indexId
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public String checkIsZNum(String cellData, String labelName, String indexId) {
        // 判断是否数值为整数
        if (StringUtil.isNotBlank(cellData)){
            try {
                double num = Double.parseDouble(cellData.replace(",", ""));
                BigDecimal result = BigDecimal.valueOf(num);
                if (result.compareTo(BigDecimal.ZERO) > 0) {
                    if(cellData.indexOf(".") > -1) {
                        return getMessage("ETR_E_0044", getMessage(labelName), indexId);
                    }
                }

            } catch (NumberFormatException e) {
                // 在校验大小时不要判断格式不正确，直接打印异常日志，正常返回即可
                logger.error(getMessage("ETR_E_0016", getMessage(labelName), indexId), e);
                return "";
            }
        }
        return "";
    }
    /**
     * check多对多指令字段用,数值是否大于0小于等于100
     *
     * @param cellData cellData
     * @param labelName labelName
     * @param indexId indexId
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public String checkIsGtZeroLtHundred(String cellData, String labelName, String indexId) {
        // 判断是否数值大于0小于等于100
        if (StringUtil.isNotBlank(cellData)){
            try {
                double num = Double.parseDouble(cellData.replace(",", ""));
                BigDecimal result = BigDecimal.valueOf(num);
                if (result.compareTo(BigDecimal.ZERO) <= 0 && result.compareTo(BigDecimal.valueOf(100)) > 0){
                    return getMessage("ETR_E_0019", getMessage(labelName), indexId);
                }
            } catch (NumberFormatException e) {
                return getMessage("ETR_E_0016", getMessage(labelName), indexId);
            }
        }
        return "";
    }

    /**
     * check多对多指令字段用,数值是否小于等于100
     *
     * @param cellData cellData
     * @param labelName labelName
     * @param indexId indexId
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public String checkLtHundred(String cellData, String labelName, String indexId) {
        // 判断是否数值小于等于100
        if (StringUtil.isNotBlank(cellData)){
            try {
                double num = Double.parseDouble(cellData.replace(",", ""));
                BigDecimal result = BigDecimal.valueOf(num);
                if (result.compareTo(BigDecimal.valueOf(100)) > 0){
                    return getMessage("ETR_E_0042", getMessage(labelName), indexId);
                }
            } catch (NumberFormatException e) {
                // 在校验大小时不要判断格式不正确，直接打印异常日志，正常返回即可
                logger.error(getMessage("ETR_E_0016", getMessage(labelName), indexId), e);
                return "";
            }
        }
        return "";
    }

    /**
     * check多对多指令字段用,错误消息拼接
     * 
     * @param errorMessage 原错误信息
     * @param addMessage 新增错误信息
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public String StringAddByBr(String errorMessage, String addMessage) {
        // 错误信息拼接
        if (StringUtil.isNotBlank(errorMessage) && StringUtil.isNotBlank(addMessage)) {
            return errorMessage + "<br />" + addMessage;
        }
        else if (StringUtil.isNotBlank(errorMessage)) {
            return errorMessage;
        }
        else {
            return addMessage;
        }
    }

    /**
     * 错误消息拼接
     * @param errorMsg 原错误信息
     * @param addMsg 新增错误信息
     * @since 1.0
     */
    public void spliceMessage(StringBuilder errorMsg, String addMsg) {
        // 错误信息拼接
        errorMsg.append(StringUtil.isEmpty(addMsg)||StringUtil.isEmpty(errorMsg.toString())?addMsg:"<br />"+addMsg );
    }

	    /***
     * 验证指令能否撤销(指令状态变更接口)
     * @param selectItem
     * @param entrustStatus  06 已关闭 07 已撤回
     * @return
     */
    public ValidMessage validEtrClose(List<? extends EtrEntrust> selectItem,String entrustStatus) {
        ValidMessage message = null;
        logger.info("调用交易系统能否撤销指令接口【{}】  接口URL："+ServiceApplication.EXCHANGE_SERVICE_NAME +
                "/joyin/instruction/cancel");
        for(EtrEntrust temp:selectItem) {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("user_id", LoginUserUtil.getUserName());
            paramMap.put("instruction_Id", temp.getEntrustId());
            paramMap.put("entrustStatus", entrustStatus);
            logger.info("请求报文【{}】","instruction_Id = "+ temp.getEntrustId());
            Map<String, Object> result = restTemplate.postForObject(ServiceApplication.EXCHANGE_SERVICE_NAME +
                            "/joyin/instruction/cancel?instruction_Id="+temp.getEntrustId()+"&entrustStatus="+entrustStatus,
                    paramMap, Map.class);
            logger.info("应答报文【{}】", "errorCode:"+(Integer)result.get("errorCode")+";errorMsg:"+(String)result.get("errorMsg"));
            if(result != null ) {
                Integer errorCode = (Integer)result.get("errorCode");
                if(errorCode == 4004 && StringUtil.equals(DictCodeList.委托状态_已撤回.getValue(),entrustStatus)) {//返回失败不可以撤销
                    message = new ValidMessage("指令有订单，不允许撤销！");
                    break;
                }
            }
        }
        return message;
    }

    // add by etliu 20210421 权益类资产相关代码合并 start

    /**
     * 检查投资指令开始日与投资指令结束日都必须为平台日，且投资指令开始日与结束日要相等
     * @param startDate 开始日
     * @param endDate 结束日
     * @param indexId 文件行号
     * @return 提示信息
     */
    public static String checkIsPlateDate(String startDate, String endDate, String indexId){

        // 估值日必须等于平台日
        String platedate = DateUtil.getSysDate();

        // modify by dubaojun 2020-07-06 缺陷SNSTZJY-124对应：输入的日期格式不合法时直接跳过  start
        try {
            if (DateUtil.compareDate(platedate,startDate) != 0
                    || DateUtil.compareDate(platedate,endDate) != 0){

                return "指令日期应为【"+platedate+"】(行号为"+indexId+")";

            }else {

                if (DateUtil.compareDate(startDate,startDate) != 0){
                    return "指令日期终止日应为【"+platedate+"】(行号为"+indexId+")";
                }else {
                    return "";
                }

            }
        } catch (RuntimeException e) {
            return "";
        }
        // modify by dubaojun 2020-07-06 缺陷SNSTZJY-124对应：输入的日期格式不合法时直接跳过  end

    }


    public static String checkQ(String startDate, String endDate, String indexId){
        if (DateUtil.compareDate(endDate,startDate) != 0){
            return "起息日不等于指令开始日(行号为"+indexId+")";
        }
        return "";

    }

    public String checkDictItemVal(String pageId,String dictId,String dictItemVal,String labelName,String indexId) {
        SysPageDictlist param = new SysPageDictlist();
        param.setPageId(pageId);
        param.setSubPageId("999999");
        param.setDictId(dictId);
        param.setDictItemVal(dictItemVal);
        SysPageDictlist sysPageDictlist = this.sysPageDictlistMapper.selectByPrimaryKey(param);

        if(sysPageDictlist==null) {
            return getMessage("ETR_E_0017", getMessage(labelName), indexId);
        } else {
            return "";
        }
    }

    /**
     * check多对多指令字段用,数值大于0
     *
     * @param cellData cellData
     * @param labelName labelName
     * @param indexId indexId
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public String checkIsGtZeroWithNum(String cellData, String labelName, String indexId) {
        // 判断是否数值大于0
        if (StringUtil.isNotBlank(cellData) && StringUtil.isBlank(this.checkIsNumber(cellData,labelName,indexId))){
            try {
                double num = Double.parseDouble(cellData.replace(",", ""));
                BigDecimal result = BigDecimal.valueOf(num);
                if (result.compareTo(BigDecimal.ZERO) <= 0) {
                    return getMessage("ETR_E_0019", getMessage(labelName), indexId);
                }
            } catch (NumberFormatException e) {
                return getMessage("ETR_E_0016", getMessage(labelName), indexId);
            }
        }
        return "";
    }

    public BigDecimal select券面面值(String finprodId, String sysDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("finprodId", finprodId);
        params.put("sysDate", sysDate);
        return commonBusinessService.selectFaceValue(params);
    }

    public AjaxResult executeOccupyCreditLines(Object object, String status, String method) {
        AjaxResult ar = new AjaxResult();
        if (StringUtil.equals(BussinessDictCodeList.监控授信开关_关.getValue(), CacheUtil.getSysParam(BussinessConstant.ISCREDIT))){
            return ar;
        }
        List<CreditLinesInputDto> inputDtoList = new ArrayList<>();
        if (object != null){
            // 构建接口参数
            generateInputDtoList(object,status,method,inputDtoList,null);
            if (inputDtoList.size() > 0){
                if ("check".equals(method)){
                    ar = AllTradeCommonUtil.checkBeforeTradeComplete(inputDtoList);
                }else {
                    AllTradeCommonUtil.handleBeforeTradeComplete(inputDtoList,DictCodeListRsk.授信占用类别_预占.getValue());
                }
            }
        }
        return ar;
    }

    /**
     * 生成调用接口list参数
     * @param object
     * @param status
     * @param method
     * @param inputDtoList
     */
    private void generateInputDtoList(Object object, String status, String method, List<CreditLinesInputDto> inputDtoList,Integer rownum) {
        // 多对多指令 或者 多组合指令check
        if (object instanceof  List<?>){
            int num = 0;
            for (Object obj : (List<?>)object){
                num++;
                generateInputDtoList(obj,status,method,inputDtoList,num);
            }
        }else if (object instanceof String && (method.equals("close") || method.equals("doBack"))){
            // 指令关闭
            CreditLinesInputDto creditLinesInputDto = new CreditLinesInputDto();
            creditLinesInputDto.setTradeId(String.valueOf(object));
            creditLinesInputDto.setTradeStatus(status);
            inputDtoList.add(creditLinesInputDto);
        }else if (object instanceof EtrEntrust && (method.equals("close") || method.equals("doBack"))){
            // 指令关闭
            CreditLinesInputDto creditLinesInputDto = new CreditLinesInputDto();
            creditLinesInputDto.setTradeId(((EtrEntrust) object).getEntrustId());
            if(StringUtil.isEmpty(creditLinesInputDto.getTradeId()) && object instanceof EtribBondInfo003Dto)
                creditLinesInputDto.setTradeId(((EtribBondInfo003Dto)object).getTotalEtrustId());
            creditLinesInputDto.setTradeStatus(status);
            inputDtoList.add(creditLinesInputDto);
        }else {
            CreditLinesInputDto creditLinesInputDto = generateInputDto(object,status,method);
            if (creditLinesInputDto != null){
                creditLinesInputDto.setRownum(rownum);
                if (!StringUtil.isEmptyList(creditLinesInputDto.getInputDtoList())){
                    inputDtoList.addAll(creditLinesInputDto.getInputDtoList());
                }else {
                    creditLinesInputDto.setRownum(rownum);
                    inputDtoList.add(creditLinesInputDto);
                }

            }
        }
    }

    /**
     * 构建接口入参
     * @param object
     * @param status
     * @param method
     * @return
     */
    private CreditLinesInputDto generateInputDto(Object object, String status, String method){
        CreditLinesInputDto creditLinesInputDto = null;
        // 交易所指令
        if (object instanceof EtrExchangeProtfolioDetailRequest){
            EtrExchangeProtfolioDetailRequest inputDto = (EtrExchangeProtfolioDetailRequest) object;
            //债转股
            if (inputDto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                    && StringUtil.equals(DictCodeListExt1.交易方向_债转股.getValue(),inputDto.getPs())){
                FinProductConvertible finProductConvertible = finProductConvertibleMapper.selectByPrimaryKey(inputDto.getFinprodId());
                if (finProductConvertible != null) {
                    creditLinesInputDto = new CreditLinesInputDto();
                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                    creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                    creditLinesInputDto.setFinprodId(finProductConvertible.getConFinprodId());
                    creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
                    creditLinesInputDto.setTradeDate(inputDto.getVdate());
                    creditLinesInputDto.setTradeStatus(status);
                    creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
                    creditLinesInputDto.setCounterId(inputDto.getCounterId());
                    creditLinesInputDto.setTradeAmt(inputDto.getEntrustAmt());
                    creditLinesInputDto.setShareAmt(inputDto.getEntrustNum());
                }
            }
            // 股票指令
            if (inputDto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue())
                    && (StringUtil.equals(DictCodeList.交易方向_买入.getValue(),inputDto.getPs())
                    || StringUtil.equals(DictCodeList.交易方向_申购.getValue(),inputDto.getPs())
                    || StringUtil.equals(DictCodeListExt2.交易方向_配股.getValue(),inputDto.getPs()))){
                creditLinesInputDto = new CreditLinesInputDto();
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
                creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
                creditLinesInputDto.setTradeDate(inputDto.getVdate());
                creditLinesInputDto.setTradeStatus(status);
                creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
                creditLinesInputDto.setCounterId(inputDto.getCounterId());
                creditLinesInputDto.setTradeAmt(inputDto.getEntrustAmt());
                creditLinesInputDto.setShareAmt(inputDto.getEntrustNum());
            }
            // 债券指令
            if (inputDto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                && (StringUtil.equals(DictCodeList.交易方向_买入.getValue(),inputDto.getPs())
                || StringUtil.equals(DictCodeList.交易方向_申购.getValue(),inputDto.getPs())
                || StringUtil.equals(DictCodeListExt3.交易方向_配债.getValue(),inputDto.getPs()))){
                creditLinesInputDto = new CreditLinesInputDto();
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
                creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                creditLinesInputDto.setTradeDate(inputDto.getVdate());
                creditLinesInputDto.setTradeStatus(status);
                creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
                creditLinesInputDto.setCounterId(inputDto.getCounterId());
                creditLinesInputDto.setShareAmt(inputDto.getEntrustNum());

                //最新面值
                BigDecimal newFaceValue = creditLinesDealsMapper.getFaceValue(creditLinesInputDto);
                if (newFaceValue == null){
                    newFaceValue = new BigDecimal(100);
                }
                if (creditLinesInputDto.getShareAmt() != null) {
                    creditLinesInputDto.setTradeAmt(newFaceValue.multiply(creditLinesInputDto.getShareAmt()));
                }else {
                    creditLinesInputDto.setTradeAmt(inputDto.getEntrustAmt());
                }

            }
            // 基金指令
            if (inputDto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue())
                && (StringUtil.equals(DictCodeList.交易方向_买入.getValue(),inputDto.getPs())
                    || StringUtil.equals(DictCodeList.交易方向_申购.getValue(),inputDto.getPs()))){
                creditLinesInputDto = new CreditLinesInputDto();
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
                creditLinesInputDto.setTradeDate(inputDto.getVdate());
                creditLinesInputDto.setTradeStatus(status);
                creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
                creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
                creditLinesInputDto.setCounterId(inputDto.getCounterId());
                creditLinesInputDto.setTradeAmt(inputDto.getEntrustAmt());
                creditLinesInputDto.setShareAmt(inputDto.getEntrustNum());
                //最新净值
                BigDecimal lastPrice = creditLinesDealsMapper.getLastPrice(inputDto.getFinprodId(),iPlatformDate.getSysDate());
                if (lastPrice == null){
                    lastPrice = BigDecimal.ONE;
                }
                creditLinesInputDto.setNetPrice(lastPrice);
            }
        }

        // 交易所协议回购指令（逆回购、正向）
        if (object instanceof EtrExchangeAgreRepTrade001Dto){
            EtrExchangeAgreRepTrade001Dto inputDto = (EtrExchangeAgreRepTrade001Dto) object;
            // 质押式回购正回购不占授信
            if (StringUtil.equals(DictCodeList.回购拆借方向_正回购.getValue(),inputDto.getRepPs())){
                return null;
            }
            // 逆回购首期
            if (StringUtil.equals(inputDto.getPs(),DictCodeListExt3.交易方向_逆回购.getValue())){
                creditLinesInputDto = new CreditLinesInputDto();
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                creditLinesInputDto.setTradeDate(DateUtil.parse(inputDto.getVdate()));
                creditLinesInputDto.setTradeStatus(status);
                creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
                creditLinesInputDto.setPsPayment(DictCodeList.回购类型_质押式协议回购.getValue());
                creditLinesInputDto.setSaleCode(inputDto.getPs());
                creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
                creditLinesInputDto.setCounterId(inputDto.getCounterId());
                if(riskCreditListCommonValidatorService.checkCallCredit(inputDto.getFinprodId())){
                    creditLinesInputDto.setTradeAmt(inputDto.getTradeAmt());
                    creditLinesInputDto.setShareAmt(inputDto.getTradeAmt());
                }
            }
            // 逆回购，到期续做 -> 新的首期
            if (StringUtil.equals(inputDto.getPs(),DictCodeListExt3.交易方向_到期续做.getValue())
                && StringUtil.equals(inputDto.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())){
                creditLinesInputDto = new CreditLinesInputDto();
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                creditLinesInputDto.setTradeDate(DateUtil.parse(inputDto.getVdate()));
                creditLinesInputDto.setTradeStatus(status);
                creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
                creditLinesInputDto.setPsPayment(DictCodeList.回购类型_质押式协议回购.getValue());
                creditLinesInputDto.setSaleCode(inputDto.getRepPs());
                creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
                creditLinesInputDto.setCounterId(inputDto.getCounterId());
                if(riskCreditListCommonValidatorService.checkCallCredit(inputDto.getFinprodId())){
                    creditLinesInputDto.setTradeAmt(inputDto.getTradeAmt());
                    creditLinesInputDto.setShareAmt(inputDto.getTradeAmt());
                }
            }

        }
        if (object instanceof EtrExchangeTradeSaveSecRepoRequest){
            EtrExchangeTradeSaveSecRepoRequest inputDto = (EtrExchangeTradeSaveSecRepoRequest) object;
            // 债券指令
            if (inputDto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                    && (StringUtil.equals(DictCodeList.交易方向_买入.getValue(),inputDto.getPs()))){
                creditLinesInputDto = new CreditLinesInputDto();
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
                creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                creditLinesInputDto.setTradeDate(inputDto.getVdate());
                creditLinesInputDto.setTradeStatus(status);
                creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
                creditLinesInputDto.setCounterId(inputDto.getCounterId());
                creditLinesInputDto.setShareAmt(inputDto.getEntrustNum());
                //最新面值
                BigDecimal newFaceValue = creditLinesDealsMapper.getFaceValue(creditLinesInputDto);
                if (newFaceValue == null){
                    newFaceValue = new BigDecimal(100);
                }
                if (creditLinesInputDto.getShareAmt() != null) {
                    creditLinesInputDto.setTradeAmt(newFaceValue.multiply(creditLinesInputDto.getShareAmt()));
                }else {
                    creditLinesInputDto.setTradeAmt(inputDto.getEntrustAmt());
                }
            }
        }

        // 校验是否需要调用接口
        boolean isCallCreditInterf = checkCallCreditInterf(creditLinesInputDto,status,method);
        if (isCallCreditInterf){
            return creditLinesInputDto;
        }
        return null;
    }

    private boolean checkCallCreditInterf(CreditLinesInputDto creditLinesInputDto, String status, String method) {
        if (creditLinesInputDto == null){
            return false;
        }
        // 买入、申购，提交到流程中，占用授信
        if ("doInsert".equals(method) || "doUpdate".equals(method)){
            if (DictCodeList.交易方向_买入.getValue().equals(creditLinesInputDto.getPs())
                    || DictCodeList.交易方向_申购.getValue().equals(creditLinesInputDto.getPs())
                    || DictCodeListExt2.交易方向_认购.getValue().equals(creditLinesInputDto.getPs())){
                return true;
            }
        }
        if ("doComplete".equals(method)){
            // 撤销生效时，下列交易方向调用
            if (DictCodeListExt1.交易状态_撤销.getValue().equals(status)){
                if (DictCodeList.交易方向_买入.getValue().equals(creditLinesInputDto.getPs())
                        || DictCodeList.交易方向_卖出.getValue().equals(creditLinesInputDto.getPs())
                        || DictCodeList.交易方向_申购.getValue().equals(creditLinesInputDto.getPs())
                        || DictCodeListExt2.交易方向_认购.getValue().equals(creditLinesInputDto.getPs())
                        || DictCodeList.交易方向_赎回确认.getValue().equals(creditLinesInputDto.getPs())){
                    return true;
                }
            }
        }
        // 买入、申购，退回时，释放授信
        if ("doBack".equals(method)){
            if (DictCodeList.交易方向_买入.getValue().equals(creditLinesInputDto.getPs())
                    || DictCodeList.交易方向_申购.getValue().equals(creditLinesInputDto.getPs())
                    || DictCodeListExt2.交易方向_认购.getValue().equals(creditLinesInputDto.getPs())){
                return true;
            }
        }
        if ("check".equals(method)){
            if (DictCodeList.交易方向_买入.getValue().equals(creditLinesInputDto.getPs())
                    || DictCodeList.交易方向_申购.getValue().equals(creditLinesInputDto.getPs())
                    || DictCodeListExt2.交易方向_认购.getValue().equals(creditLinesInputDto.getPs())){
                return true;
            }
        }
        return false;
    }

    /**
     * 请求市场行情数据接口
     * @param url
     * @return
     */
    public Map<String,Object> getMarketInfo(String url){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1000);
        factory.setReadTimeout(1000);
        RestTemplate template = new RestTemplate(factory);
        try {
            Map<String, Object> quoteMap = template.getForObject(url, Map.class);
            return quoteMap;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
 	* 银行间指令 关闭check
 	* @param param
 	* @return
 	*/
 	public List<ValidMessage> entrustCheckCloseList(Map<String, Object> param) {
 		List<ValidMessage> validMessageList = restTemplate.postForList(
 				ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustBankCommon/entrustCheckCloseList", param,
 				ValidMessage.class);
 		return validMessageList;
 	}

    public String selectByDynId(Map<String, Object> param) {
        String url = ServiceApplication.COMMON_SERVICE_NAME + "/dynamicConfig/selectByDynId";
        String str = restTemplate.postForObject(url, param, String.class);
        return str;
    }

    public Integer updateByParam(Map<String, Object> param) {
        String url = ServiceApplication.COMMON_SERVICE_NAME + "/dynamicConfig/updateByParam";
        return restTemplate.postForObject(url, param, Integer.class);
    }


    /**
     * 银行间债券持仓信息
     * @param paramMap
     * @return
     */
    public EtrFinProductCommonDto findSecAmountInfo(Map<String, Object> paramMap) {
        EtrFinProductCommonDto dto = new EtrFinProductCommonDto();
        //银行间日历
        if(!StringUtil.isNotEmpty(paramMap.get("calId"))){
            paramMap.put("calId", "03");
        }

        // 获取平台日
        Date tradeDate = DateUtil.platDateFromCache();
        paramMap.put("sysDate",DateUtil.format(tradeDate));
        Map<String, Object> result = new HashMap<>();

        if(!StringUtil.isNotEmpty(paramMap.get("finprodId"))){
            /**
             * 当单选组合时，使用组合的币种
             */
            PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(paramMap.get("portfolioId") + "");
            if(ptlPortfolio != null && StringUtil.isNotEmpty(ptlPortfolio.getCcy())){
                paramMap.put("ccy", ptlPortfolio.getCcy());
            }
        }
        // 可用现金
        Map<String, Object> cash = this.getProAvailableCash(paramMap);
        // 组合T日可用现金
        BigDecimal proAvailableCash = cash.get("cashT")==null?BigDecimal.ZERO: (BigDecimal)cash.get("cashT");
        // 净价
        BigDecimal cprice = paramMap.get("cprice")==null?new BigDecimal(100): (BigDecimal)cash.get("cprice");

        // 可买数量
        BigDecimal canBuyAmount = null;
        if(cprice!=null&&BigDecimal.ZERO.compareTo(cprice)!=0){
            canBuyAmount = ArithmeticUtil.divide(proAvailableCash,cprice,2,RoundingMode.HALF_UP);
        }

        // 成本价
        BigDecimal costPrice = this.getCostPrice(paramMap);

        if (StringUtil.isNotEmpty((String)paramMap.get("finprodId"))) {
            // 资产可用券面总额
            EtrSecPositionDto etrSecPositionDto= new EtrSecPositionDto();
            etrSecPositionDto.setCdate(tradeDate);
            etrSecPositionDto.setFinprodId((String)paramMap.get("finprodId"));
            etrSecPositionDto.setPortfolioId((String)paramMap.get("portfolioId"));

            etrSecPositionDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());

            etrSecPositionDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
            result.put("canSellAmount", this.getAssetAvailablePosition(etrSecPositionDto));
            // 资产持仓券面总额
            PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
            ptlSecPositionFlowDto.setCdate(tradeDate);
            ptlSecPositionFlowDto.setPortfolioId((String)paramMap.get("portfolioId"));

            ptlSecPositionFlowDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
            ptlSecPositionFlowDto.setFinprodId((String)paramMap.get("finprodId"));
            ptlSecPositionFlowDto.setEntrust(true);
            FinProduct finProduct = finProductMapper.selectByPrimaryKey((String)paramMap.get("finprodId"));
            BigDecimal amount03 = basePtlSecPositionInfoService.queryOtherSecPosition(ptlSecPositionFlowDto);
            ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_融入.getValue());
            BigDecimal amount05 = basePtlSecPositionInfoService.queryOtherSecPosition(ptlSecPositionFlowDto);
            ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_融出.getValue());
            BigDecimal amount06 = basePtlSecPositionInfoService.queryOtherSecPosition(ptlSecPositionFlowDto);
            BigDecimal assetPositionEntrustAmt = ArithmeticUtil.doAdd(amount03, amount05);
            assetPositionEntrustAmt = ArithmeticUtil.subtract(assetPositionEntrustAmt, amount06);

            if (finProduct != null && StringUtil.equals(finProduct.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                assetPositionEntrustAmt = ArithmeticUtil.multiply(assetPositionEntrustAmt,BigDecimal.valueOf(100));
            }

            result.put("positionAmount", assetPositionEntrustAmt);
        } else {
            result.put("canSellAmount", BigDecimal.ZERO);
            result.put("positionAmount", BigDecimal.ZERO);
        }

        dto.setCanBuyAmount(canBuyAmount);
        dto.setCostPrice(costPrice);
        dto.setPositionAmount(result.get("positionAmount") == null ? BigDecimal.ZERO : (BigDecimal) result.get("positionAmount"));
        dto.setCanSellAmount(result.get("canSellAmount") == null ? BigDecimal.ZERO : (BigDecimal) result.get("canSellAmount"));
        dto.setCashAmount(proAvailableCash);
        return dto;
    }


    /**
     * 组合现金信息
     * @param paramMap
     * @return
     */
    public EtrFinProductCommonDto findCashAmountInfo(Map<String, Object> paramMap) {
        EtrFinProductCommonDto dto = new EtrFinProductCommonDto();

        //银行间日历
        if(!StringUtil.isNotEmpty(paramMap.get("calId"))){
            paramMap.put("calId", "03");
        }
        Map<String, Object> result = new HashMap<>();
        /**
         * 当单选组合时，使用组合的币种
         */
        PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(paramMap.get("portfolioId") + "");
        if(ptlPortfolio != null && StringUtil.isNotEmpty(ptlPortfolio.getCcy())){
            paramMap.put("ccy", ptlPortfolio.getCcy());
        }
        //add by chenjiaxing 币种为空时默认为人民币 start
        if(StringUtil.isEmpty(ptlPortfolio.getCcy())){
            paramMap.put("ccy", "CNY");
        }
        //add by chenjiaxing 币种为空时默认为人民币 start
        // 可用现金
        Map<String, Object> cash = this.getProAvailableCash(paramMap);
        //组合T日可用现金
        result.put("proAvailableCash",cash.get("cashT"));
        //组合T+1日可用现金
        result.put("proAvailableCashT1", cash.get("cashT1"));
        //托管户T日可用现金
        result.put("secManageAvailablePosition",cash.get("cashT3"));
        //托管户T+1日可用现金
        result.put("secManageAvailablePositionT1", cash.get("cashTT1"));

        //通道可用现金
        if (StringUtil.isNotEmpty(paramMap.get("chlId"))) {
            //通道T日可用现金
            result.put("channelAvailable", cash.get("cashT2"));
            //通道T+1日可用现金
            result.put("channelAvailableT1",cash.get("cashT21"));
        }
        dto.setT1Cash(result.get("proAvailableCashT1") == null ? BigDecimal.ZERO : (BigDecimal) result.get("proAvailableCashT1"));
        dto.setTgAcctCash(result.get("secManageAvailablePosition") == null ? BigDecimal.ZERO : (BigDecimal) result.get("secManageAvailablePosition"));
        dto.setSecAcctCash(cash.get("cashT4") == null ? BigDecimal.ZERO : (BigDecimal) cash.get("cashT4"));
        return dto;
    }


    /**
     * 根据资产代码 组合代码 日期 获取债券成本价
     * @param paramMap
     * @return
     */
    public  BigDecimal getCostPrice(Map<String, Object> paramMap){
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbBondinfo001/getCostPrice", paramMap,
                BigDecimal.class);
    }


    public String assetIsTestBySppi(Map<String, Object> paramMap){
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustCommon/assetIsTestBySppi", paramMap,
                String.class);
    }



    public Map<String,Object> getSppiInfoByY1(Map<String, Object> paramMap){
        Map<String,Object> result = new HashMap<>();
        String finprodId = paramMap.get("finprodId").toString();
        if(!StringUtil.isNotEmpty(paramMap.get("price"))){
            result.put("result","0");
            return result;
        }
        BigDecimal price = new BigDecimal(paramMap.get("price").toString());
        Date platDate = DateUtil.platDateFromCache();

        Boolean sinal = true;
        //1 查询资产是否有行权计划
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("finprodId", finprodId);
        param.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
        param.put("cashType2", DictCodeListExt1.现金流二级类型_本金.getValue());
        param.put("execType01", DictCodeList.行权状态_未行权.getValue());
        param.put("execType02", DictCodeList.行权状态_已行权.getValue());
        List<FinExercisePlan> finExercisePlanList = busiFinExercisePlanService.getfinExercisePlanByProductId(param);

        for(FinExercisePlan finExercisePlan:finExercisePlanList) {
            int count = 0;
            if ((StringUtil.equals(finExercisePlan.getExerciseType(), "01") || StringUtil.equals(finExercisePlan.getExerciseType(), "02")) && DateUtil.compareDate(platDate, finExercisePlan.getActExerciseDate()) <= 0) {
                count++;
              break;
            }
            if(count==0){
                result.put("result","0");
                return result;
            }
        }
        //2 查询sppi中债中的使权不重大的购入价条件（小于第二个数字）
        String bidPriceCon = restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME + "/SppiTest001/getSppiInfoByY1?finprodId="
                + finprodId, String.class);
        if(StringUtil.isNotEmpty(bidPriceCon)) {
            try{
                bidPriceCon = bidPriceCon.split(",")[1];
            }catch (Exception e){
                result.put("result","1");
                return result;
            }
            BigDecimal bidPriceCon2 = new BigDecimal(bidPriceCon);
            if (price.compareTo(bidPriceCon2) < 0) {
                result.put("result","2");
                result.put("bidPriceCon",bidPriceCon2);
                return result;
            }
        }
        result.put("result","0");
        return result;
    }

    /**
     * 组合现金信息
     * @param paramMap
     * @return
     */
    public EtrFinProductCommonDto calCanBuyAmount(Map<String, Object> paramMap) {
        EtrFinProductCommonDto dto = new EtrFinProductCommonDto();
        //银行间日历
        if(!StringUtil.isNotEmpty(paramMap.get("calId"))){
            paramMap.put("calId", "03");
        }

        // 获取平台日
        Date tradeDate = DateUtil.platDateFromCache();
        paramMap.put("sysDate",DateUtil.format(tradeDate));

        if(!StringUtil.isNotEmpty(paramMap.get("finprodId"))){
            /**
             * 当单选组合时，使用组合的币种
             */
            PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(paramMap.get("portfolioId") + "");
            if(ptlPortfolio != null && StringUtil.isNotEmpty(ptlPortfolio.getCcy())){
                paramMap.put("ccy", ptlPortfolio.getCcy());
            }
        }
        // 可用现金
        Map<String, Object> cash = this.getProAvailableCash(paramMap);
        // 组合T日可用现金
        BigDecimal proAvailableCash = cash.get("cashT")==null?BigDecimal.ZERO: (BigDecimal)cash.get("cashT");
        // 净价
        BigDecimal cprice = !StringUtil.isNotEmpty(paramMap.get("price"))?new BigDecimal(100): new BigDecimal(paramMap.get("price").toString());

        // 可买数量
        BigDecimal canBuyAmount = BigDecimal.ZERO;
        if(StringUtil.isNotEmpty(paramMap.get("price"))&&cprice!=null&&BigDecimal.ZERO.compareTo(cprice)!=0){
            canBuyAmount = ArithmeticUtil.divide(proAvailableCash,cprice,2,RoundingMode.HALF_UP);
        }

        dto.setCanBuyAmount(canBuyAmount);
        return dto;
    }

    public String checkAuthUser(WorkflowEntity workflowEntity, FlowEntity entity){
        String processKey = entity.getProcessKey();
        String authModuleName = CacheUtil.getSysParam(BussinessConstant.AUTH_MODULE_NAME);
        if (StringUtil.equals(processKey, authModuleName)) {
            List<String> allStatedUsers = new ArrayList<>();
            // 流程ID
            String workflowId = entity.getWorkflowId();
            if (StringUtil.isNotEmpty(workflowId)) {

                String processInstanceId = entity.getProcessInstanceId();
                try {
                    ProcessService processService = WorkflowContext.getProcessService();
                    Task task = processService.getActiveTaskId(processInstanceId,LoginUserUtil.getUserName());
                    // 业务变量
                    Map<String, Object> variables = null;
                    // 判断processKey是否有设置条件
                    if (WorkflowContext.getWorkFlowService().isConditionModule(processKey)
                            && ActivitiType.排他网关.getValue().equals(processService.getNextActType(processInstanceId))) {
                        // 获取业务参数
                        variables = WorkflowContext.getBusinessVariables(entity, task.getId());
                    }
                    String nextTaskId = processService.getNextTaskDefinitionId(task.getId(),variables);
                    // checkEntrustAmt 是工作流配置中对应授权金额有权人审批节点的id
                    if (!(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH) == null
                            || BussinessConstant.ETRLIMIT_SWITCH.equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH))
                            || DictCodeListExt1.是或否_否.getValue().equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH)))
                            && StringUtil.equals("checkEntrustAmt", nextTaskId)) {

                        // 查询下一处理节点对应的所有处理人
                        Map<String,Object> mapParam = new HashMap<>();
                        mapParam.put("searchType","role");
                        mapParam.put("nodeId",nextTaskId);
                        mapParam.put("moduleId",processKey);
                        allStatedUsers = userMapper.findAllUserByRole(workflowMapper.getValueByNodeId(mapParam), null);
                        List<String> result = riskAuthFilter(workflowEntity, allStatedUsers);
                        List<String> lastUser = userMap.get(entity.getNextUser()+entity.getBusinessType());
                        // 当前处理人 存在处理集合
                        if (StringUtil.isEmptyList(lastUser)){
                            userMap.put(entity.getNextUser()+entity.getBusinessType(),result);
                            return "";
                        }
                        // 校验当前指令和上一个指令的授权审批人是否是同一批人，是:返回 "" ,继续下一次验证
                        if (lastUser.containsAll(result) && result.containsAll(lastUser)){
                            return "";
                        }else {
                            // 不是: 返回错误信息，并清空当前处理人勾选的指令记录对应的 下一节点审批人
                            return "审批金额存在跨区间审批人，不能批量审批，请调整。";
                        }
                    }
                } catch (Exception ex) {
                    logger.error("UserTaskHandlerUserSelectorImpl.selectHandlerUser error {templateKey:"+ processKey + ",function:riskAuthFilter,msg:审批人授权额度过滤方法错误}");
                }
            }
        }
        return "";
    }

    /**
     * 授权额度过滤
     *
     * @param entity
     * @param allStatedUsers
     * @return
     */
    private List<String> riskAuthFilter(WorkflowEntity entity, List<String> allStatedUsers) {
        if ((entity instanceof EtrEntrust)||(entity instanceof EtrEntrustWw)) {
            EtrEntrust entrust =new EtrEntrust();
            if(entity instanceof EtrEntrust){
                entrust = (EtrEntrust) entity;
            }else{
                BeanUtil.copyPropertiesIgnoreNull(entrust, entity);
            }
            Map<String, Object> params = new HashMap<>();
            BigDecimal amount = BigDecimal.ZERO;
            String assetSecType = "";
            // 查询组合权限用户
            String portfolioId = entrust.getPortfolioId();
            Map<String,Object> portMap = new HashMap<>();
            portMap.put("portfolioIds", Collections.singletonList(portfolioId));
            portMap.put("allStatedUsers", allStatedUsers);
            List<String> result = restTemplate.postForObject(
                    ServiceApplication.ENTRUST_SERVICE_NAME
                            + "/common/etrApprovalAuth/getAllStatedUsers", portMap, List.class);
            // 过滤掉当前登录用户
            result.remove(SecurityUtil.getLoginUser().getUserName());
            if (entity instanceof EtrInvestBondInfoDto) {// 债券二级指令
                EtrInvestBondInfoDto bondInfoDto = (EtrInvestBondInfoDto) entity;
                params.put("assetType", BussinessDictCodeList.平安资产类型_债券.getValue());
                assetSecType = bondInfoDto.getAssetType1();
                amount = bondInfoDto.getEntrustNum();
            }else if (entity instanceof EtrIbBondinfo002Dto) {// 债券二级指令 新页面
                EtrIbBondinfo002Dto bondInfoDto = (EtrIbBondinfo002Dto) entity;
                params.put("assetType", BussinessDictCodeList.平安资产类型_债券.getValue());
                assetSecType = bondInfoDto.getAssetType1();
                amount = bondInfoDto.getEntrustNum();
            } else if (entity instanceof EtrBondFirstLBiddingDto) {// 债券一级指令
                EtrBondFirstLBiddingDto dto = (EtrBondFirstLBiddingDto) entity;
                amount = dto.getEntrustNum() == null ? dto.getEntrustAmt() : dto.getEntrustNum();
                params.put("assetType", DictCodeListExt2.平安资产类型_一级市场债.getValue());
                assetSecType = dto.getAssetType1();
            } else if (entity instanceof EtrRepoDto) {// 质押式回购指令
                EtrRepoDto dto = (EtrRepoDto) entity;
                amount = dto.getEntrustAmt() == null ? dto.getEntrustNum() : dto.getEntrustAmt();
                params.put("assetType", BussinessDictCodeList.平安资产类型_回购.getValue());
                assetSecType = dto.getAssetType1();
            }else if (entity instanceof EtrIbRepoinfo002Dto) {// 质押式回购指令
                EtrIbRepoinfo002Dto dto = (EtrIbRepoinfo002Dto) entity;
                amount = dto.getEntrustAmt() == null ? dto.getEntrustNum() : dto.getEntrustAmt();
                params.put("assetType", BussinessDictCodeList.平安资产类型_回购.getValue());
                assetSecType = dto.getAssetType1();
            } else if (entity instanceof EtrBrowDepoInfoDto) {// 存款类指令
                EtrBrowDepoInfoDto dto = (EtrBrowDepoInfoDto) entity;
                amount = dto.getEntrustAmt() == null ? dto.getEntrustNum() : dto.getEntrustAmt();
                params.put("assetType", BussinessDictCodeList.平安资产类型_存款.getValue());
                assetSecType = dto.getAssetType1();
            } else if (entity instanceof EtrLendSpecInfoDto) {// 拆借指令
                EtrLendSpecInfoDto dto = (EtrLendSpecInfoDto) entity;
                amount = dto.getEntrustAmt() == null ? dto.getEntrustNum() : dto.getEntrustAmt();
                params.put("assetType", BussinessDictCodeList.平安资产类型_拆借.getValue());
                assetSecType = dto.getAssetType1();
            } else if (entity instanceof EtrFundDto) {// 公募基金指令
                EtrFundDto dto = (EtrFundDto) entity;
                amount = dto.getEntrustAmt();
                if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    amount = dto.getEntrustNum();
                }
                params.put("assetType", BussinessDictCodeList.平安资产类型_基金.getValue());
                assetSecType = dto.getAssetType1();
            } else if (entity instanceof EtrPlanAssetNDto) {// 计划类净值型指令
                EtrPlanAssetNDto dto = (EtrPlanAssetNDto) entity;
                amount = dto.getEntrustAmt() == null ? dto.getEntrustNum() : dto.getEntrustAmt();
                params.put("assetType", BussinessDictCodeList.平安资产类型_计划类资产_净值型.getValue());
                assetSecType = dto.getAssetType1();
            } else if (entity instanceof EtrPlanAssetRDto) {// 计划类利率型指令
                EtrPlanAssetRDto dto = (EtrPlanAssetRDto) entity;
                amount = dto.getEntrustAmt();
                if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    amount = dto.getEntrustNum();
                }
                params.put("assetType", BussinessDictCodeList.平安资产类型_计划类资产_利率型.getValue());
                assetSecType = dto.getAssetType1();
            } else if (entity instanceof EtrPlanAssetCDto) {// 计划类货币型指令
                EtrPlanAssetCDto dto = (EtrPlanAssetCDto) entity;
                amount = dto.getEntrustAmt();
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                    amount = dto.getEntrustAmt2();
                }
                if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    amount = dto.getEntrustNum();
                }
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                    amount = dto.getEntrustNum2();
                }
                params.put("assetType", BussinessDictCodeList.平安资产类型_计划类资产_货币型.getValue());
                assetSecType = dto.getFinprodType2();
            } else if (entity instanceof EtrProjectDto) {// 非标债权指令
                EtrProjectDto dto = (EtrProjectDto) entity;
                amount = dto.getEntrustAmt();
                if (amount == null) {
                    amount = dto.getEntrustNum();
                }
                params.put("assetType", BussinessDictCodeList.平安资产类型_项目类资产.getValue());
                assetSecType = dto.getAssetType1();
            } else if (entity instanceof EtrBoutRepoDto) {// 买断式回购指令
                EtrBoutRepoDto dto = (EtrBoutRepoDto) entity;
                amount = dto.getEntrustAmt();
                if (amount == null) {
                    amount = dto.getEntrustNum();
                }
                params.put("assetType", BussinessDictCodeList.平安资产类型_买断式回购.getValue());
                assetSecType = dto.getFinprodType2();
            } else if (entity instanceof EtrProjectNetDto) {// 权益类指令
                EtrProjectNetDto dto = (EtrProjectNetDto) entity;
                amount = dto.getEntrustAmt();
                if (amount == null) {
                    amount = dto.getEntrustNum();
                }
                params.put("assetType", DictCodeListExt3.平安资产类型_权益类资产.getValue());
                assetSecType = dto.getAssetType1();
            } else if (entity instanceof EtrSecLendDto) {// 债券借贷指令
                EtrSecLendDto dto = (EtrSecLendDto) entity;
                amount = dto.getTotalAmt().multiply(new BigDecimal(10000));
                params.put("assetType", DictCodeList.平安资产类型_债券.getValue());
                // 根据资产类型取得授权类型
                FinProductTypeConfig finProductTypeConfig = getRskAuthorizeType(dto.getFinprodId(), DictCodeList.平安资产类型_债券.getValue());
                assetSecType = finProductTypeConfig == null ? "" : finProductTypeConfig.getOutputValue2();
            } else if (entity instanceof EtrExchangeTrade001Dto) {//场内交易指令
                EtrExchangeTrade001Dto dto = (EtrExchangeTrade001Dto) entity;
                amount = dto.getEntrustAmt();
                if (amount == null) {
                    amount = dto.getEntrustNum();
                }
                //债券
                if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    params.put("assetType", DictCodeList.平安资产类型_债券.getValue());
                }
                else if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {//基金
                    params.put("assetType", DictCodeList.平安资产类型_基金.getValue());
                } else if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {//股票
                    params.put("assetType", DictCodeListExt2.平安资产类型_股票.getValue());
                } else if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue())) {//权益类
                    params.put("assetType", DictCodeListExt3.平安资产类型_权益类资产.getValue());
                }
                else {//非标债权
                    params.put("assetType", DictCodeList.平安资产类型_质押式回购.getValue());
                }
                assetSecType = null;
            }else if (entity instanceof EtrConBondDto) {// 可转债委托
                EtrConBondDto etrConBondDto = (EtrConBondDto) entity;
                params.put("assetType", BussinessDictCodeList.平安资产类型_债券.getValue());
                assetSecType = etrConBondDto.getAssetType1();
                amount = etrConBondDto.getPurchBondTotal();
            }else if (entity instanceof EtrAutoManagementAccountDto) {//自主管理
                EtrAutoManagementAccountDto dto = (EtrAutoManagementAccountDto) entity;
                amount = dto.getEntrustAmt();
                if (amount == null) {
                    amount = dto.getEntrustNum();
                }
                if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    params.put("assetType", DictCodeList.平安资产类型_债券.getValue());
                } else if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
                    params.put("assetType", DictCodeList.平安资产类型_基金.getValue());
                } else if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                    params.put("assetType", DictCodeListExt2.平安资产类型_股票.getValue());
                } else {
                    params.put("assetType", BussinessDictCodeList.平安资产类型_项目类资产.getValue());
                }
                assetSecType = null;
            }
            // add by etliu 20210112 start
            // 授权分类应使用资产二级类型
            // 根据指令中的金融产品代码查询该资产的分类，进而查询对应的授权分类
            if (StringUtil.isNotBlank(entrust.getFinprodId())) {
                // 根据资产类型取得授权类型
                FinProductTypeConfig finProductTypeConfig = getRskAuthorizeType(
                        entrust.getFinprodId(), null);
                assetSecType = finProductTypeConfig == null ? "" : finProductTypeConfig.getOutputValue2();
            }
            // add by etliu 20210112 end
            params.put("entrustAmt", amount);
            params.put("assetSecType", assetSecType);
            params.put("tradeMarket", StringUtil.isEmpty(
                    entrust.getTradeMarket()) ? "999999" : entrust.getTradeMarket());
            params.put("tradeDirection", entrust.getPs());
            // {参数字典.授权类型.授权、参数字典.授权类型.无限授权}
            params.put("AUTHORIZE_TYPE_02", DictCodeList.授权类型_授权.getValue());
            params.put("AUTHORIZE_TYPE_03", DictCodeList.授权类型_无限授权.getValue());
            params.put("allStatedUsers", result);
            List<Map<String, Object>> limits = restTemplate.postForObject(
                    ServiceApplication.ENTRUST_SERVICE_NAME
                            + "/common/etrApprovalAuth/getAuthorizeLimitAmt", params, List.class);
            if (!StringUtil.isEmptyList(limits)) {
                List<String> users = new ArrayList<String>();
                BigDecimal limit = new BigDecimal(
                        limits.get(0).get("AUTHORIZE_LIMIT_AMT").toString());
                for (Map<String, Object> map : limits) {
                    BigDecimal limit1 = new BigDecimal(map.get("AUTHORIZE_LIMIT_AMT").toString());
                    if (limit.compareTo(limit1) == 0) {
                        users.add(map.get("USER_NAME").toString());
                    }
                }
                return users;
            } else {
                return new ArrayList<>();
            }
        }
        return allStatedUsers;
    }

    /**
     * 根据资产类型取得授权类型
     *
     * @param assetType 资产类型
     * @param assetId 资产代码
     * @see
     * @since 1.0
     */
    private FinProductTypeConfig getRskAuthorizeType(String assetId, String assetType) {
        if (assetId != null && assetId.length() > 0) {
            FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
            finProductTypeKey.setFinprodId(assetId);
            finProductTypeKey.setBranch(0L);
            FinProductType finProductType = finProductTypeMapper
                    .selectByPrimaryKey(finProductTypeKey);
            FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
            finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_授权分类.getValue());
            finProductTypeConfig.setInputValue1(finProductType.getType8());
            finProductTypeConfig.setInputValue2(finProductType.getType9());
            finProductTypeConfig.setInputValue3(finProductType.getType10());
            finProductTypeConfig.setInputValue4(finProductType.getType11());
            finProductTypeConfig.setInputValue5(finProductType.getType12());
            finProductTypeConfig = restTemplate.postForObject(
                    ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
                    finProductTypeConfig, FinProductTypeConfig.class);
            return finProductTypeConfig;
        } else if (StringUtil.isNotEmpty(assetType)) {
            FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
            finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_授权分类.getValue());
            finProductTypeConfig.setInputValue1(assetType);
            finProductTypeConfig = restTemplate.postForObject(
                    ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
                    finProductTypeConfig, FinProductTypeConfig.class);
            return finProductTypeConfig;
        }
        return null;
    }

    public void removeCheckAuthUser(String key) {
        userMap.remove(key);
    }


    /**
     * add by xxg 20210811
     * 根据组合代码获取组合全称
     */
    public String getPortfolioName(String portfolioId){
        PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(portfolioId);
        String portfolioName = "";
        if(ptlPortfolio != null){
            portfolioName = ptlPortfolio.getPortfolioName();
        }
        return portfolioName;
    }

    public String getZyqk(String zyqkName){
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustCommon/getZyqk", zyqkName,
                String.class);
    }

    public String checkZyqkExist(String cellData, String labelName, String indexId) {
        // 判断是否是正确的组合代码
        if (StringUtil.isNotBlank(cellData)) {
             String zyqk =restTemplate.postForObject(
                    ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustCommon/getZyqk", cellData,
                    String.class);
            if (!StringUtil.isNotEmpty(zyqk)) {
                return getMessage("ETR_E_0017", getMessage(labelName), indexId);
            }
        }
        return "";
    }

    /**
     * 组合指令可用现金查询
     *
     * @param param
     * @return cashT 组合T日可用, cashT1 组合T+1日可用, cashT3 托管户可用, cashT2 通道可用， cashT4 交易所资金账户可用
     * @see
     * @since 1.0
     */
    public Map<String, Object> getProAvailableCashExchange(Map<String, Object> param) {
        PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
        Date sysdate = DateUtil.parse(DateUtil.getSysDate());
        if (StringUtil.isNotEmpty(param.get("acctId"))){
            dto.setAcctId((String)param.get("acctId"));
        }
        if (StringUtil.isNotEmpty(param.get("finprodId"))){
            FinProduct finProduct = finProductMapper.selectByPrimaryKey((String)param.get("finprodId"));
            List<String> holidayList = new ArrayList<>();
            if (Objects.nonNull(finProduct)) {
                holidayList = dateCalService.getDealCalendarHoliday(sysdate,DateUtil.addDay(sysdate, 15),finProduct.getCalendarId());
            }
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(sysdate).toString()), 1, holidayList, "1");
            param.put("sysDateT1", DateUtil.format(tradeDay));
        }else {
            String calId = (String)param.get("calId");
            Date vdate = DateUtil.parse((String)param.get("vdate"));
            if(StringUtil.isNotEmpty(calId)&&vdate!=null){
                List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(vdate)),DateUtil.addDay(DateUtil.parse(DateUtil.format(vdate)), 15),calId);
                Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(vdate)), 1, holidayList, "1");
                param.put("sysDateT1",DateUtil.format(tradeDay));
            }
        }

        dto.setCdate(sysdate);
        List<String> acctTypeList = new ArrayList<>();
        acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
        dto.setPortfolioId((String)param.get("portfolioId"));
        dto.setCcy((String)param.get("ccy"));
        Map<String, Object> result = new HashMap<>();
        dto.setAcctType(acctTypeList);

        if (baseEtrCashPositionCommService != null) {
            BigDecimal cashT = BigDecimal.ZERO;
            //期货保证金单独处理
            if(param.containsKey("acctType")&&StringUtil.equals(param.get("acctType"),DictCodeListExt1.资金账户类型_期货资金账户.getValue())){
                acctTypeList.clear();
                acctTypeList.add(DictCodeListExt1.资金账户类型_期货资金账户.getValue());
                BigDecimal cashQH = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
                cashT = ArithmeticUtil.doAdd(cashT, cashQH == null ? BigDecimal.ZERO : cashQH);
            }else{
                BigDecimal cashT3 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
                result.put("cashT3", cashT3); // 托管户可用
                cashT = ArithmeticUtil.doAdd(cashT, cashT3 == null ? BigDecimal.ZERO : cashT3);

                acctTypeList.clear();
                acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
                BigDecimal cashT4 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
                result.put("cashT4", cashT4);// 交易所资金账户可用
                cashT = ArithmeticUtil.doAdd(cashT, cashT4 == null ? BigDecimal.ZERO : cashT4);
            }

            if (StringUtil.isNotEmpty(param.get("chlFinprodId"))) {
                FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey((String)param.get("chlFinprodId"));
                result.put("payType", "02");
                if (finProductAdd != null) {
                    result.put("payType", finProductAdd.getPayType());
                }
                dto.setAcctType(null);
                dto.setChlFinprodId((String)param.get("chlFinprodId"));
                BigDecimal cashT2 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
                result.put("cashT2", cashT2 == null ? BigDecimal.ZERO : cashT2);// 通道可用
                //组合T日可用现金不加通道可用现金
                //cashT = ArithmeticUtil.doAdd(cashT, cashT2);
            }
            result.put("cashT", cashT);// 组合T日可用

            if (StringUtil.isNotEmpty(param.get("sysDateT1"))) {
                PtlCashPositionFlowDto dto2 = new PtlCashPositionFlowDto();
                Date sysdateT1 = DateUtil.parse((String)param.get("sysDateT1"));
                dto2.setCdate(sysdateT1);
                acctTypeList.clear();
                acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
                acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
                dto2.setAcctType(acctTypeList);
                dto2.setPortfolioId((String)param.get("portfolioId"));
                dto2.setCcy((String)param.get("ccy"));
                BigDecimal cashT1 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto2);
                if (StringUtil.isNotEmpty(param.get("chlFinprodId"))) {
                    FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey((String)param.get("chlFinprodId"));
                    result.put("payType", "02");
                    if (finProductAdd != null) {
                        result.put("payType", finProductAdd.getPayType());
                    }
                    dto2.setAcctType(null);
                    //组合T日可用现金不加通道可用现金
                    dto2.setChlFinprodId((String)param.get("chlFinprodId"));
                    BigDecimal cashT2 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto2);
                    result.put("cashT21", cashT2 == null ? BigDecimal.ZERO : cashT2);// 通道T+1可用
                }
                result.put("cashT1", cashT1 == null ? result.get("cashT") : cashT1);// 组合T+1日可用

                acctTypeList.clear();
                acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
                BigDecimal cashTT1 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto2);//组合托管户T+1可用
                result.put("cashTT1", cashTT1 == null ? BigDecimal.ZERO : cashTT1);// 组合托管户T+1可用

            }
        }

        return result;
    }

    /**
     * 查找组合对应的证券账户
     *
     */
    public EtrExchangePortfolioAllocationResponse selectEtrPortfolioAllocationList(EtrExchangePortfolioAllocationRequest dto) {
        EtrExchangePortfolioAllocationResponse response=new EtrExchangePortfolioAllocationResponse();
        BigDecimal minStep=dto.getMinStep();
        BigDecimal entrustNum=dto.getEntrustNum();
        BigDecimal entrustAmt=dto.getEntrustAmt();
        BigDecimal entrustPrice=dto.getEntrustPrice()==null?BigDecimal.ZERO:dto.getEntrustPrice();
        BigDecimal leftEntrustNum=entrustNum;
        //初始化为0
        dto.getPortfolioList().stream().forEach(item->{
            item.setEntrustNum(BigDecimal.ZERO);
            item.setEntrustAmt(BigDecimal.ZERO);
        });
        boolean flag=true;
        //目标类型是数量安装步长分配
        List<EtrExchangeProtfolioDetailRequest> portfolioList = dto.getPortfolioList();
        int sumSize = dto.getPortfolioList().size();
        if (StringUtil.equals(DictCodeListExt3.目标类型_数量.getValue(), dto.getTargetType())) {
            while (flag) {
                for (int i = 0; i < sumSize; i++) {
                    EtrExchangeProtfolioDetailRequest item = portfolioList.get(i);
                        if (leftEntrustNum.compareTo(minStep) > 0) {
                            item.setEntrustNum(ArithmeticUtil.doAdd(item.getEntrustNum(), minStep));
                            item.setEntrustAmt(ArithmeticUtil.multiply(item.getEntrustNum(), entrustPrice));
                            leftEntrustNum = ArithmeticUtil.subtract(leftEntrustNum, minStep);
                        } else {
                            item.setEntrustNum(ArithmeticUtil.doAdd(item.getEntrustNum(), leftEntrustNum));
                            item.setEntrustAmt(ArithmeticUtil.multiply(item.getEntrustNum(), entrustPrice));
                            flag = false;
                            break;
                        }
                }
            }
        }else{ //目标类型是金额还是平均分配
            for (int i = 0; i < sumSize; i++) {
                EtrExchangeProtfolioDetailRequest item = portfolioList.get(i);
                if (i != sumSize - 1) {
                    item.setEntrustNum(ArithmeticUtil.divide(entrustNum, sumSize, 0));
                    item.setEntrustAmt(ArithmeticUtil.divide(entrustAmt, sumSize, 2));
                } else {
                    BigDecimal tempEntrustNum = dto.getPortfolioList().stream().map(EtrExchangeProtfolioDetailRequest::getEntrustNum).reduce(BigDecimal::add).get();
                    BigDecimal tempEntrustAmt = dto.getPortfolioList().stream().map(EtrExchangeProtfolioDetailRequest::getEntrustAmt).reduce(BigDecimal::add).get();
                    item.setEntrustNum(ArithmeticUtil.subtract(entrustNum, tempEntrustNum));
                    item.setEntrustAmt(ArithmeticUtil.subtract(entrustAmt, tempEntrustAmt));
                }
            }
        }
        BeanUtil.copyPropertiesIgnoreNull(response, dto);
        return response;
    }
}
