package com.joyintech.tams.invest.entrust.etribrepo.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.*;
//import com.joyintech.tams.invest.interf.b45.service.Send006Service;
//import com.joyintech.tams.invest.interf.b45.service.SendService;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.trade.BussRepoSecService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.entrust.etribrepo.dto.EtrIbRepoinfo002Dto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;


/**
 * 质押式回购投资委托画面端service 〈功能详细描述〉
 */
@Service
public class EtrIbRepoinfo002Service extends CommonExportService<EtrIbRepoinfo002Dto> implements BaseProcessService<EtrIbRepoinfo002Dto> {
    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    
    @Autowired
    private CommonBusinessService commonBusinessService;
    
    @Autowired(required = false)
    private BussRepoSecService bussRepoSecService;
    
    /**
     * 委托指令共同
     */
    @Autowired(required=false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    /**
     * b45接口
     */
//    @Autowired
//    private SendService sendService;
//
//    @Autowired
//    private Send006Service send006Service;

    /**
     * 获取组合代码的联想文本框 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getentrustId(String param) {
        User user = SecurityUtil.getLoginUser();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserName());
        map.put("param", param);
        return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME
                                       + "/EtrIbRepoinfo002/getentrustId" , map,
            SuggestDto.class);
    }

    /**
     * 获取资产代码的联想文本框 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getfinprodId(String param) {
        return restTemplate.getForList(ServiceApplication.ENTRUST_SERVICE_NAME
                                       + "/EtrIbRepoinfo002/getfinprodId?finprodId=" + param,
            SuggestDto.class);

    }

    /**
     * 获取新建界面的组合代码联想下拉框框 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getentrustIdAdd(Map<String, Object> param) {
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                                       + "/EtrIbRepoinfo002/getentrustIdAdd"
                                       , param,
            SelectOptionDto.class);
    }

    /**
     * 获取新建界面的资产代码联想下拉框框
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getfinprodIdForAdd(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                                       + "/EtrIbRepoinfo002/getfinprodIdForAdd"
                                       , param,
            SelectOptionDto.class);

    }
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
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoInvestTrade/isRight", paramMap,
                Boolean.class);
    }
    /**
     * 校验方法 <功能详细描述>
     *
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrIbRepoinfo002Dto dto) {
        dto.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
        return restTemplate.postForList(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbRepoinfo002/check", dto,
            ValidMessage.class);
    }
    
    /**
     * 根据组合ID查询质押库下拉框
     * @return
     */
    public Page<SelectOptionDto> getPledgeStockIdByPortfolioId(Map<String, Object> parameters) {

        Page<SelectOptionDto> pages = restTemplate.postForPage(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbRepoinfo002/getPledgeStockIdByPortfolioId", parameters,
                SelectOptionDto.class);
         return  pages;
    }
    /**
     * 工作流完成方法
     */
    @Override
    public void doComplete(EtrIbRepoinfo002Dto dto, OperationType operationType) {
        dto.setOperateType(operationType.toString());
        Date sysDate = DateUtil.platDateFromCache();
        dto.setEntrustDate(sysDate);
        basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());
        restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbRepoinfo002/doComplete",dto,
            Integer.class);
        doDeleteFlow(dto.getWorkflowId());

        if(bussRepoSecService != null){
            bussRepoSecService.insert(dto.getBussRepoSec(),dto.getEntrustId());
        }
        
        //向b45发送信息
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("entrustId", dto.getEntrustId());
//        sendService.sendToB45(param,send006Service);
    }
    private void doInsertFlow(EtrIbRepoinfo002Dto dto) {
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
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(dto.getEntrustAmt());
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
            if(StringUtil.isNotEmpty(etrEntrust.getPrice())){
                etrEntrust.setPrice(etrEntrust.getPrice().divide(new BigDecimal(100)));
            }
            if(dto.getRepoTerm()!=null){
                etrEntrustAdd.setTermDays(dto.getRepoTerm().longValue());
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            entity.setMstFileInputParameter(mstFileInputParameter);
            entity.setWorkFlowId(dto.getWorkflowId());
            entity.setEtrPledgeList(getEtrPledgeListByBussRepoSecDto(dto.getBussRepoSec(),dto.getEntrustId()));
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
    		 if(CollectionUtils.isEmpty(bussRepoSec.getPledgeBondList())){
                 return etrPledgeList;
             }
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
        	 }
    		 return etrPledgeList;
    	 }
    	 return null;
    }
    
    @Override
    public void doDelete(String workflowId) {
        doDeleteFlow(workflowId);
    }
    private void doDeleteFlow(String workflowId) {
        Map<String,Object> param = new HashMap<>();
        param.put("workflowId",workflowId);
        restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doDeleteEtrFlow", param,
            Boolean.class);
    }
    
    /**
     *
     * 流程发起时调用
     */
     @Override
     public void doInsert(EtrIbRepoinfo002Dto dto, OperationType type){
         if ("submit".equals(dto.getOperateType())) {
             doInsertFlow(dto);
         }
         dto.setOperateType(type.toString());
         restTemplate.postForObject(
             ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbRepoinfo002/doInsert",dto,
             Integer.class);
     }

     /**
      *
      * 流程修改时调用
      */
     @Override
     public void doUpdate(EtrIbRepoinfo002Dto dto){
         restTemplate.postForObject(
             ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbRepoinfo002/doInsert",dto,
             Integer.class);
         if (!StringUtil.equals("revoke", dto.getOperateType()) && !StringUtil.equals("delete", dto.getOperateType())) {
             // 先删除流程中数据
             doDeleteFlow(dto.getWorkflowId());
             // 重新insert
             doInsertFlow(dto);
             ProcessMark newEntity;
             try {
                 newEntity = WorkFlowUtil.getBusinessObjectFromRequest(EtrIbRepoinfo002Dto.class, true);
                 // 工作流ID
                 newEntity.setWorkflowId(dto.getWorkflowId());
                 newEntity.setStatus(dto.getStatus());
                 newEntity.setBusinessData(JsonUtils.object2Json(dto));
                 // 更新业务表数据
                 // 操作留痕表
                 WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
             }
             catch (Exception e) {
                 e.printStackTrace();
             }
         }
     }
     
    /**
          * 修改/双击查看详情查询信息 <功能详细描述>
     *
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrIbRepoinfo002Dto selectByKey(String entrustId,String finprodId,String portfolioId) {
        if(!StringUtil.isNotEmpty(entrustId)){
            entrustId="";
        }
        if(!StringUtil.isNotEmpty(finprodId)){
            finprodId="";
        }
        if(!StringUtil.isNotEmpty(portfolioId)){
            portfolioId="";
        }
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                                         + "/EtrIbRepoinfo002/selectByKey?entrustId=" + entrustId+"&finprodId="+finprodId+"&portfolioId="+portfolioId,
            EtrIbRepoinfo002Dto.class);
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        EtrIbRepoinfo002Dto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {
                if (dto.isImportFlow()) { // 只有在多对多组合提交时，才为true，供弹出用户选择，选择用户备用，让需要上级审批的流程走到这个人这里，不需要的直接结束
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
                        // 资产类型改为授权类型 add by renping 2019-08-06  start
                        , null, dto.getWorkflowId()
                        // 资产类型改为授权类型 add by renping 2019-08-06  end
                        );
                params.put("term", dto.getRepoTerm());
                // add by zhangrui1 风险审批修改 start
                params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
                // add by zhangrui1 风险审批修改 end
                return commonBusinessService.是否需要上级审批_委托用(params);
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

    /**
     * 查询行权价
     * @author wangguiqi
     */
    public BigDecimal getExercisePricebyPara(Map<String, Object> paramMap) {
        paramMap.put("sysdate", iPlatformDate.getSysDateStr());
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrIbRepoinfo002/getExercisePricebyPara", paramMap,
                BigDecimal.class);
    }

    @Override
    public void doBack(EtrIbRepoinfo002Dto entity) {
    	baseEtrCashPositionCommService.entrustCloseExecute(entity.getEntrustId());
    }
}
