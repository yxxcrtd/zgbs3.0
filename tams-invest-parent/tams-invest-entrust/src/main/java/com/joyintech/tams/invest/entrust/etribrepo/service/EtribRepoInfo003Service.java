package com.joyintech.tams.invest.entrust.etribrepo.service;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.trade.BussRepoSecService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
//import com.joyintech.tams.invest.interf.b45.service.Send006Service;
//import com.joyintech.tams.invest.interf.b45.service.SendService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etribrepo.dto.EtrIbRepoinfo002Dto;
import com.joyintech.tams.invest.entrust.etribrepo.dto.EtribRepoInfo003Dto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 多组合质押式回购指令新建
 *
 * @author wl
 * @date 2021年02月2日
 * @see EtribRepoInfo003Service
 * @since 1.0
 */
@Service
public class EtribRepoInfo003Service extends CommonExportService<EtribRepoInfo003Dto> implements BaseProcessService<EtribRepoInfo003Dto> {

    /**
     * 日期相关
     */
    @Autowired
    private DateCalService dateCalService;

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
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    
    @Autowired(required = false)
    private BussRepoSecService bussRepoSecService;

    /**
     * b45接口
     */
//    @Autowired
//    private SendService sendService;
//
//    @Autowired
//    private Send006Service send006Service;

//    /**
//     * 判断是否含权债
//     *
//     * @param finprodId 金融产品代码
//     * @see
//     * @since 1.0
//     */
//    public boolean isRight(String finprodId) {
//        Map<String, Object> paramMap = new HashMap<String, Object>();
//        paramMap.put("finprodId", finprodId);
//
//        return restTemplate.postForObject(
//                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoInvestTrade/isRight", paramMap,
//                Boolean.class);
//    }

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
        Date sysDate = DateUtil.platDateFromCache();
        param.put("sysDate", DateUtil.format(sysDate));
        // 交易日历
        String calId = (String) param.get("calId");
        if (StringUtil.isNotEmpty(calId)) {
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(sysDate)), DateUtil.addDay(DateUtil.parse(DateUtil.format(sysDate)), 15), calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(sysDate)), 1, holidayList, "1");
            // T+1日
            param.put("sysDateT1", DateUtil.format(tradeDay));
        }

        PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
//        dto.setFinprodId((String)param.get("finprodId"));
        dto.setCdate(sysDate);
        List<String> acctTypeList = new ArrayList<>();
        acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
        dto.setPortfolioId((String)param.get("portfolioId"));
        dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        Map<String, Object> result = new HashMap<>();
        dto.setAcctType(acctTypeList);
//        if (StringUtil.isBlank(dto.getPortfolioId())) {
//            result.put("parentPortfolioId",etrCommonService.selectParentPortfolioIdByPortfolioId(
//                    dto.getPortfolioId()));
//        }

        if (baseEtrCashPositionCommService != null) {
            // 组合T日可用现金
            BigDecimal cashT = BigDecimal.ZERO;
            // 托管户T日可用现金
            BigDecimal secManageAvailablePosition = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
            result.put("secManageAvailablePosition", secManageAvailablePosition); // 托管户可用
            cashT = ArithmeticUtil.doAdd(cashT, secManageAvailablePosition == null ? BigDecimal.ZERO : secManageAvailablePosition);

            acctTypeList.clear();
            acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
            BigDecimal brokerageFundAccount = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
            result.put("brokerageFundAccount", brokerageFundAccount);// 交易所资金账户可用
            cashT = ArithmeticUtil.doAdd(cashT, brokerageFundAccount == null ? BigDecimal.ZERO : brokerageFundAccount);

            if (StringUtil.isNotEmpty(param.get("chlFinprodId"))) {
                FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey((String)param.get("chlFinprodId"));
                result.put("payType", "02");
                if (finProductAdd != null) {
                    result.put("payType", finProductAdd.getPayType());
                }
                dto.setAcctType(null);
                dto.setChlFinprodId((String)param.get("chlFinprodId"));
                // 通道可用
                BigDecimal channelAvailable = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
                result.put("channelAvailable", channelAvailable == null ? BigDecimal.ZERO : channelAvailable);
                //组合T日可用现金不加通道可用现金
                //cashT = ArithmeticUtil.doAdd(cashT, cashT2);
            }
            result.put("proAvailableCash", cashT);// 组合T日可用

            if (StringUtil.isNotEmpty(param.get("sysDateT1"))) {
                PtlCashPositionFlowDto dto2 = new PtlCashPositionFlowDto();
                Date sysdateT1 = DateUtil.parse((String)param.get("sysDateT1"));
//                dto2.setFinprodId((String)param.get("finprodId"));
                dto2.setCdate(sysdateT1);
                acctTypeList.clear();
                acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());

                dto2.setAcctType(acctTypeList);
                dto2.setPortfolioId((String)param.get("portfolioId"));
                dto2.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                BigDecimal brokerageFundAccountT1 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto2);
                result.put("brokerageFundAccountT1", brokerageFundAccountT1 == null ? BigDecimal.ZERO : brokerageFundAccountT1);
                // 组合T+1 可用现金
                BigDecimal cashT1 =  BigDecimal.ZERO;
                cashT1 = ArithmeticUtil.doAdd(cashT1, brokerageFundAccountT1 == null ? BigDecimal.ZERO : brokerageFundAccountT1);
                acctTypeList.clear();
                acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
                BigDecimal secManageAvailablePositionT1 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto2);
                result.put("secManageAvailablePositionT1", secManageAvailablePositionT1 == null ? BigDecimal.ZERO : secManageAvailablePositionT1);
                cashT1 = ArithmeticUtil.doAdd(cashT1, secManageAvailablePositionT1 == null ? BigDecimal.ZERO : secManageAvailablePositionT1);
                if (StringUtil.isNotEmpty(param.get("chlFinprodId"))) {
                    FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey((String)param.get("chlFinprodId"));
                    result.put("payType", "02");
                    if (finProductAdd != null) {
                        result.put("payType", finProductAdd.getPayType());
                    }
                    dto2.setAcctType(null);
                    //组合T日可用现金不加通道可用现金
                    dto2.setChlFinprodId((String)param.get("chlFinprodId"));
                    // 通道可用
                    BigDecimal channelAvailableT1 = baseEtrCashPositionCommService.qryCommandAvailableCash(dto2);
                    result.put("channelAvailableT1", channelAvailableT1 == null ? BigDecimal.ZERO : channelAvailableT1);
                    /* cashT1 = ArithmeticUtil.doAdd(cashT1, cashT2);*/
                }
                // 组合T+1日可用
                result.put("proAvailableCashT1", cashT1 == null ? BigDecimal.ZERO : cashT1);
            }
        }
        return result;
    }

    /**
     *
     * 流程发起时调用
     */
    @Override
    public void doInsert(EtribRepoInfo003Dto entity, OperationType type){
        if ("submit".equals(entity.getOperateType())) {
            if (CollectionUtils.isNotEmpty(entity.getEtrInvestRepoInfoDtos())) {
                for (EtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
                    dto.setWorkflowId(entity.getWorkflowId());
                    doInsertFlow(dto);
                }
            }
        }
        entity.setOperateType(type.toString());
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbRepoinfo003/doInsert",entity,
                Integer.class);
    }

    /**
     *
     * 流程修改时调用
     */
    @Override
    public void doUpdate(EtribRepoInfo003Dto entity){
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbRepoinfo002/doInsert",entity,
                Integer.class);
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            // 先删除流程中数据
            doDeleteFlow(entity.getWorkflowId());
            if (CollectionUtils.isNotEmpty(entity.getEtrInvestRepoInfoDtos())) {
                for (EtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
                    // 重新insert
                    doInsertFlow(dto);
                }
            }
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(EtrIbRepoinfo002Dto.class, true);
                // 工作流ID
                newEntity.setWorkflowId(entity.getWorkflowId());
                newEntity.setStatus(entity.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(entity));
                // 更新业务表数据
                // 操作留痕表
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void doInsertFlow(EtribRepoInfo003Dto dto) {
        try {
            dto.setOperateType(dto.getOperateType());
            BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
            EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
            EtrEntrust etrEntrust = new EtrEntrust();
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
            entrustCommonDto.setBussRepoSecDto(dto.getBussRepoSec());
            dto.setFinprodType2(StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_正回购.getValue())?DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue():DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            dto.setRecStatus(DictCodeListExt3.委托状态_流程中.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            //BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(dto.getEntrustAmt());
            etrEntrust.setInvManagerId(dto.getInvManagerId());
            etrEntrust.setTradeMarket(dto.getTradeMarket());
            etrEntrust.setTradePlat(dto.getTradePlat());
            if(StringUtil.isNotEmpty(etrEntrust.getPrice())){
            	etrEntrust.setPrice(etrEntrust.getPrice().divide(new BigDecimal(100)));
            }
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            etrEntrustAdd.setVdate(dto.getVdate());
            etrEntrustAdd.setMdate(dto.getMdate());
            etrEntrustAdd.setPledgeStockId(dto.getZyqk());
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)0);
            }
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)1);
            }
            if(StringUtil.isNotEmpty(dto.getTermType())){
                etrEntrustAdd.setTermType(dto.getTermType());
            }
            if(dto.getRepoTerm()!=null){
                etrEntrustAdd.setTermDays(dto.getRepoTerm().longValue());
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            entity.setEtrPledgeList(getEtrPledgeListByBussRepoSecDto(dto.getBussRepoSec(),dto.getEntrustId()));
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            entity.setMstFileInputParameter(mstFileInputParameter);
            entity.setWorkFlowId(dto.getWorkflowId());
            restTemplate.postForObject(
                    ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/insertWithQuery", entity,
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


    private List<EtrPledge> getEtrPledgeListByBussRepoSecDto(BussRepoSecDto bussRepoSec,String bizCode){
   	 if(StringUtil.isNotEmpty(bussRepoSec)){
   		 List<EtrPledge> etrPledgeList = new ArrayList<>();
   		 for (PledgeBondInfoDto pledgeBond : bussRepoSec.getPledgeBondList()) {
                // 回购委托新建
                EtrPledge etrPledge = new EtrPledge();
                // 委托代码 = 委托代码
                etrPledge.setEntrustId(bizCode);
                // 金融产品代码 = 输出质押品DTO.金融产品代码
                etrPledge.setFinprodId(pledgeBond.getFinprodId());
                // 质押面值 = 输出质押品DTO.质押面值
                etrPledge.setPledgeFaceValue(new BigDecimal("100"));
                // 质押数量 = 输出质押品DTO.质押数量
                etrPledge.setPledgeNumber(ArithmeticUtil.divide(pledgeBond.getPledgePrin(), new BigDecimal("100"), 4));
                // 质押率 = 输出质押品DTO.质押率
                etrPledge.setPledgeRatio(ArithmeticUtil.divide(pledgeBond.getPledgeRatio(), new BigDecimal("100"), 4));
                // 质押金额 = 输出质押品DTO.质押金额
                etrPledge.setPledgeAmt(ArithmeticUtil.multiply(pledgeBond.getPledgePrin(), etrPledge.getPledgeRatio()));
                etrPledgeList.add(etrPledge);
                return etrPledgeList;
       	 }
   	 }
   	 return null;
   }
   
    /**
     * 校验方法 <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtribRepoInfo003Dto dto) {
        dto.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
        return restTemplate.postForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbRepoinfo002/check", dto,
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
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoInvestTrade/getSecManageAcctId",
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

    @Override
    public void doComplete(EtribRepoInfo003Dto entity, OperationType type) {
        entity.setOperateType(type.toString());
        if (StringUtil.equals(entity.getSettleSpreed(),
                DictCodeList.清算速度_T_0.getValue())) {
        	entity.setSettleSpreed("0");
        }
        if (StringUtil.equals(entity.getSettleSpreed(),
                DictCodeList.清算速度_T_1.getValue())) {
        	entity.setSettleSpreed("1");
        }
        if(StringUtil.isNotEmpty(entity.getSettleSpreed())){
            if(Integer.parseInt(entity.getSettleSpreed())!=0){
                List<String> holiday = dateCalService.getDealCalendarHoliday(entity.getVdate(),
                        DateUtil.addDays(entity.getVdate(), 31),entity.getCalendarId());
                Date tradingDay = dateCalService.calculationTradingDay(entity.getVdate(), Integer.parseInt(entity.getSettleSpreed()), holiday, "1");
                entity.setSettleDate(tradingDay);
                // 每个组合设置交割日
                if (CollectionUtils.isNotEmpty(entity.getEtrInvestRepoInfoDtos())) {
                    for (int i = 0; i < entity.getEtrInvestRepoInfoDtos().size(); i++) {
                        entity.getEtrInvestRepoInfoDtos().get(i).setSettleDate(tradingDay);
                    }
                }
            }
        }
        for (EtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
            basRiskMathCommService.insert(entity.getRiskTrialRetDto(), dto.getEntrustId());
        }
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbRepoinfo003/doComplete", entity,
                Integer.class);
        
        for (EtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
        	if(bussRepoSecService != null){
                bussRepoSecService.insert(dto.getBussRepoSec(),dto.getEntrustId());
            }
        }
        

        doDeleteFlow(entity.getWorkflowId());

        //向b45发送信息
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("entrustId", entity.getEntrustId());
//        sendService.sendToB45(param,send006Service);
    }

    @Override
    public void doDelete(String workflowId) {
        doDeleteFlow(workflowId);
    }

    private void doDeleteFlow(String workflowId) {
        EtribRepoInfo003Dto entity = getBussinessEntityByWorkflowId(workflowId);
        if (CollectionUtils.isNotEmpty(entity.getEtrInvestRepoInfoDtos()))  {
            for (EtribRepoInfo003Dto dto: entity.getEtrInvestRepoInfoDtos()) {
                Map<String,Object> param = new HashMap<>();
                param.put("entrustId",dto.getEntrustId());
                restTemplate.postForObject(
                        ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbRepoinfo003/doDeleteEtrFlow", param,
                        Boolean.class);

            }
        }

    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        EtribRepoInfo003Dto dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        if (taskId != null) {
            try {
            	if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH_IS_TOTAL), "Y")){
            		 return getEtrLimitSwitch(entity.getWorkflowId(), dto, commonBusinessService);
            	}else{
//            		BussinessConstant.是否需要上级审批
            		List<EtribRepoInfo003Dto> etribRepoInfo003DtoList =dto.getEtrInvestRepoInfoDtos();
            		Map<String, Object> result = new HashMap<>();
            		for(EtribRepoInfo003Dto etribRepoInfo003Dto:etribRepoInfo003DtoList){
            			result=getEtrLimitSwitch(entity.getWorkflowId(), etribRepoInfo003Dto, commonBusinessService);
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
	private Map<String, Object> getEtrLimitSwitch(String workflowId, EtribRepoInfo003Dto dto,
			CommonBusinessService commonBusinessService) throws WorkFlowException {
		if (dto.isImportFlow()) {
		     Map<String, Object> param = new HashMap<>();
		     param.put(BussinessConstant.是否需要上级审批, true);
		     return param;
		 }
		 BigDecimal amount = dto.getEntrustAmt();
		 if (amount == null) {
		     amount = dto.getEntrustNum();
		 }
		 Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), amount, LoginUserUtil.getUserName(),
		         BussinessDictCodeList.平安资产类型_回购.getValue(), dto.getAssetType1(), dto.getTradeMarket(), dto.getPs()
		         , dto.getFinprodId(), workflowId
		 );
		 // add by zhangrui1 风险审批修改 start
		 params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
		 // add by zhangrui1 风险审批修改 end
		 return commonBusinessService.是否需要上级审批_委托用(params);
	}

    @Override
    public void doBack(EtribRepoInfo003Dto entity) {
        List<EtribRepoInfo003Dto> etrInvestRepoInfoDtos = entity.getEtrInvestRepoInfoDtos();
        if(!StringUtil.isEmptyList(etrInvestRepoInfoDtos)){
            etrInvestRepoInfoDtos.forEach(item ->{
                baseEtrCashPositionCommService.entrustCloseExecute(item.getEntrustId());
            });
        }
    }
}
