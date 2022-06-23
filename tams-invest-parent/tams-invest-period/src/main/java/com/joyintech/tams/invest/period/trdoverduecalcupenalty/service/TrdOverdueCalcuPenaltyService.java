package com.joyintech.tams.invest.period.trdoverduecalcupenalty.service;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.period.trdoverduecalcupenalty.dto.TrdOverdueCalcuPenaltyDto;
import com.joyintech.tams.invest.period.trdsecoverdueduration.dto.TrdSecOverdueDuration001Dto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName TrdOverdueCalcuPenaltyService
 * @Description: TODO 计息要素维护
 * @Author wangxuelei
 * @Date 2019/12/10 0010
 * @Version V1.0
 **/
@Service
public class TrdOverdueCalcuPenaltyService extends CommonExportService<TrdOverdueCalcuPenaltyDto>
        implements BaseProcessService<TrdOverdueCalcuPenaltyDto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdOverdueCalcuPenaltyService.class);

    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;
    @Autowired
    private FinTradeProductAddMapper finTradeProductAddMapper;
    @Autowired
    private FinCashInfoMapper finCashInfoMapper;
    @Autowired
    private FinCashPlanMapper finCashPlanMapper;
    @Autowired
    private FinCashCalcRuleMapper finCashCalcRuleMapper;
    @Autowired(required=false)
    private CommonBusinessService commonBusinessService;

    @Override
    public void doInsert(TrdOverdueCalcuPenaltyDto entity, OperationType type) {
    }
    @Override
    public void doUpdate(TrdOverdueCalcuPenaltyDto entity) {
        ProcessMark newEntity = null;
        try {
            newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdOverdueCalcuPenaltyDto.class, true);
            // 工作流ID
            newEntity.setWorkflowId(entity.getWorkflowId());
            newEntity.setStatus(entity.getStatus());
            newEntity.setBusinessData(JsonUtils.object2Json(entity));
            // 更新业务表数据
            // 操作留痕表
            WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommServiceImpl.delete(workflowId);
    }

    @Override
    public void doComplete(TrdOverdueCalcuPenaltyDto entity, OperationType type) {
        String bjBranch = "";
        String liBranch = "";
        String generate1 ="";
        String generate2 ="";
        //如果是存在了计息信息维护的数据就删了
        TrdOverdueCalcuPenaltyDto bussinessEntityByWorkflowId = this.getBussinessEntityByWorkflowId(entity.getWorkflowId());
        if(bussinessEntityByWorkflowId!=null){
            entity =   bussinessEntityByWorkflowId;
        }
        if(StringUtil.isNoneBlank(entity.getOldBjBranch())||
                StringUtil.isNoneBlank(entity.getOldLiBranch())){
            String cashIds = entity.getOldCashIds();
            if(StringUtil.isNoneBlank(cashIds)){
                String[] split = cashIds.split(",");
                FinCashCalcRule rule = new FinCashCalcRule();
                FinCashPlan plan = new FinCashPlan();
                if(StringUtil.isNotBlank(entity.getOldPenaltyBaseType())){
                    if(StringUtil.equals(entity.getOldPenaltyBaseType(), DictCodeListExt3.罚息计提基数_逾期本金.getValue())){
                        updateAndDeleteByOld(entity,Long.valueOf(entity.getOldBjBranch()));
                    }else if(StringUtil.equals(entity.getOldPenaltyBaseType(), DictCodeListExt3.罚息计提基数_逾期利息.getValue())){
                        updateAndDeleteByOldWithoutBranch(entity,Long.valueOf(entity.getOldLiBranch()));
                    }else{
                        updateAndDeleteByOld(entity,Long.valueOf(entity.getOldBjBranch()));
                        updateAndDeleteByOldWithoutBranch(entity,Long.valueOf(entity.getOldLiBranch()));
                    }
                }else{
                    if(StringUtil.isNotBlank(entity.getOldBjBranch())){
                        updateAndDeleteByOld(entity,Long.valueOf(entity.getOldBjBranch()));
                    }else if(StringUtil.isNotBlank(entity.getOldLiBranch())){
                        updateAndDeleteByOldWithoutBranch(entity,Long.valueOf(entity.getOldLiBranch()));
                    }
                }
                for (String str:split) {
                    finCashInfoMapper.deleteByPrimaryKey(str);
                    rule.setCashId(str);
                    plan.setCashId(str);
                    rule.setEffDate(DateUtil.parse(entity.getOldMdate()));
                    plan.setMdate(DateUtil.parse(entity.getOldFxmdate()));
                    finCashCalcRuleMapper.deleteByPrimaryKey(rule);
                    finCashPlanMapper.deleteByPrimaryKey(plan);
                }
            }
        }
        if(StringUtil.isNoneBlank(entity.getBjBranch())){
            bjBranch = entity.getBjBranch();
            //本金现金流代码
             generate1 = commonBusinessService.generateCashFlowCode(entity.getFinprodId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),
                    Long.valueOf(bjBranch), DictCodeListExt1.现金流二级类型_本金.getValue());
        }
        if(StringUtil.isNoneBlank(entity.getLiBranch())){
            liBranch = entity.getLiBranch();
            //利息现金流代码
             generate2 = commonBusinessService.generateCashFlowCode(entity.getFinprodId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),
                    Long.valueOf(liBranch), DictCodeListExt1.现金流二级类型_利息.getValue());
        }

        List<FinTradeProductAdd> trdDealTotalAdds = entity.getFinTradeProductAdds();
        List<FinTradeProduct> finTradeProducts = entity.getFinTradeProducts();
        List<FinCashInfo> finCashInfos = entity.getFinCashInfos();
        List<FinCashPlan> finCashPlans = entity.getFinCashPlans();
        List<FinCashCalcRule> finCashCalcRules = entity.getFinCashCalcRules();
        if(CollectionUtils.isNotEmpty(trdDealTotalAdds)){
            for (FinTradeProductAdd add:trdDealTotalAdds) {
                FinTradeProductAdd finTradeProductAdd = finTradeProductAddMapper.selectByPrimaryKey(add);
                if(finTradeProductAdd!=null){
                    finTradeProductAddMapper.updateByPrimaryKeySelective(add);
                }else{
                    finTradeProductAddMapper.insertSelective(add);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(finTradeProducts)){
            for (FinTradeProduct add:finTradeProducts) {
                finTradeProductMapper.updateByPrimaryKeySelective(add);
            }
        }
        if(CollectionUtils.isNotEmpty(finCashInfos)&&StringUtil.equals(entity.getIsAccrPenaltyInt(), DictCodeList.是否_是.getValue())){
            for (FinCashInfo finCashInfo:finCashInfos) {
                if(StringUtil.isNotBlank(entity.getBjBranch())&&
                        finCashInfo.getBranch()==Long.parseLong(entity.getBjBranch())){
                    finCashInfo.setCashId(generate1);
                }else if(StringUtil.isNotBlank(entity.getLiBranch())&&
                        finCashInfo.getBranch()==Long.parseLong(entity.getLiBranch())){
                    finCashInfo.setCashId(generate2);
                }else{//还本不带息
                    String  cashId  = commonBusinessService.generateCashFlowCode(entity.getFinprodId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),
                           finCashInfo.getBranch(), DictCodeListExt1.现金流二级类型_利息.getValue());
                    finCashInfo.setCashId(cashId);
                }
                finCashInfo.setCashType2(DictCodeListExt3.现金流二级类型_罚息.getValue());
                finCashInfoMapper.insertSelective(finCashInfo);
            }
        }
        if(CollectionUtils.isNotEmpty(finCashPlans)&&StringUtil.equals(entity.getIsAccrPenaltyInt(), DictCodeList.是否_是.getValue())){
            for (FinCashPlan finCashPlan:finCashPlans) {
                if(StringUtil.isNotBlank(entity.getBjBranch())&&
                        finCashPlan.getBranch()==Long.parseLong(entity.getBjBranch())){
                    finCashPlan.setCashId(generate1);
                }else if(StringUtil.isNotBlank(entity.getLiBranch())&&
                        finCashPlan.getBranch()==Long.parseLong(entity.getLiBranch())){
                    finCashPlan.setCashId(generate2);
                }else{//还本不带息
                    String  cashId  = commonBusinessService.generateCashFlowCode(entity.getFinprodId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),
                            finCashPlan.getBranch(), DictCodeListExt1.现金流二级类型_利息.getValue());
                    finCashPlan.setCashId(cashId);
                }
                finCashPlan.setCashType(DictCodeListExt3.现金流类型_罚息收息.getValue());
                finCashPlanMapper.insertSelective(finCashPlan);
            }
        }
        if(CollectionUtils.isNotEmpty(finCashCalcRules)&&StringUtil.equals(entity.getIsAccrPenaltyInt(), DictCodeList.是否_是.getValue())){
            for (FinCashCalcRule finRule:finCashCalcRules) {
                if(StringUtil.isNotBlank(entity.getBjBranch())&&
                        finRule.getBranch()==Long.parseLong(entity.getBjBranch())){
                    finRule.setCashId(generate1);
                }else if(StringUtil.isNotBlank(entity.getLiBranch())&&
                        finRule.getBranch()==Long.parseLong(entity.getLiBranch())){
                    finRule.setCashId(generate2);
                }else{//还本不带息
                    String  cashId  = commonBusinessService.generateCashFlowCode(entity.getFinprodId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),
                            finRule.getBranch(), DictCodeListExt1.现金流二级类型_利息.getValue());
                    finRule.setCashId(cashId);
                }
                finRule.setCalcType(DictCodeListExt1.计算类型_固定利率.getValue());
                finRule.setIsInitial("Y");
                finCashCalcRuleMapper.insertSelective(finRule);
            }
        }

    }

    public TrdSecOverdueDuration001Dto selectJxwuFromFinprodIdAndBranch(String finProdId, Long bjBranch, Long liBranch, String penaltyBaseType){
        Map<String, Object> param = new HashMap<>();
        param.put("finprodId",finProdId);
        List<Long> branchs = new ArrayList<>();
        if(bjBranch!=null){
            branchs.add(bjBranch);
        }
        if(liBranch!=null){
            branchs.add(liBranch);
        }
        param.put("branchs",branchs);
        if(bjBranch!=null&&liBranch!=null){
            if(StringUtil.isNotBlank(penaltyBaseType)){
                if(StringUtil.equals(penaltyBaseType, DictCodeListExt3.罚息计提基数_逾期本金.getValue())){
                    param.put("bjBranch",bjBranch);
                }else if(StringUtil.equals(penaltyBaseType, DictCodeListExt3.罚息计提基数_逾期利息.getValue())){
                    param.put("liBranch",liBranch);
                }else if(StringUtil.equals(penaltyBaseType, DictCodeListExt3.罚息计提基数_逾期本息和.getValue())){
                    param.put("bjBranch",bjBranch);
                }
            }else{
                param.put("bjBranch",bjBranch);
            }
        }else{
            if(bjBranch!=null){
                param.put("bjBranch",bjBranch);
            }
            if(liBranch!=null){
                param.put("liBranch",liBranch);
            }
        }
        /* add by jiangpeng on 20210430 TISHKBS-2089 运营端-逾期标的信息导入报错 */
        param.put("cashType2",DictCodeListExt1.现金流二级类型_利息.getValue());
        /* add by jiangpeng on 20210430 TISHKBS-2089 运营端-逾期标的信息导入报错 */
        TrdSecOverdueDuration001Dto trdSecOverdueDurationDto = restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/selectJxwuFromFinprodId",
                param, TrdSecOverdueDuration001Dto.class);
        return trdSecOverdueDurationDto;

    }


    public void updateAndDeleteByOld(TrdOverdueCalcuPenaltyDto entity,Long branch){
        FinTradeProductAddKey key = new FinTradeProductAddKey();
        FinTradeProduct finTradeProduct = new FinTradeProduct();
        key.setFinprodId(entity.getFinprodId());
        key.setBranch(branch);
        finTradeProductAddMapper.deleteByPrimaryKey(key);
        finTradeProduct.setFinprodId(entity.getFinprodId());
        finTradeProduct.setBranch(branch);
        finTradeProduct.setIsRushBack("");
        finTradeProductMapper.updateByPrimaryKeySelective(finTradeProduct);
    }

    public void updateAndDeleteByOldWithoutBranch(TrdOverdueCalcuPenaltyDto entity,Long branch){
        updateAndDeleteByOld(entity,branch);
        if(StringUtil.isNotBlank(entity.getOldwithoutBranch())){
            String[] split = entity.getOldwithoutBranch().split(",");
            for (String bran:split) {
                updateAndDeleteByOld(entity,Long.parseLong(bran));
            }
        }

    }

    public TrdSecOverdueDuration001Dto selectCashIds(String finProdId, Long bjBranch, Long liBranch,String withoutBranch){
        Map<String, Object> param = new HashMap<>();
        List<Long> branchs = new ArrayList<>();
        param.put("cashIds","true");
        param.put("finprodId",finProdId);
        if(bjBranch!=null){
            branchs.add(bjBranch);
        }
        if(liBranch!=null){
            branchs.add(liBranch);
        }
        if(StringUtil.isNotBlank(withoutBranch)){
            String[] withoutBranchs = withoutBranch.split(",");
            for (String branch:withoutBranchs) {
                branchs.add(Long.parseLong(branch));
            }
            param.put("branchs",branchs);
        }
        TrdSecOverdueDuration001Dto trdSecOverdueDurationDto = restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/selectCashIdsByBranchAll",
                param, TrdSecOverdueDuration001Dto.class);
        return trdSecOverdueDurationDto;
    }

    public TrdSecOverdueDuration001Dto selectWithoutBranch(String finProdId,Long liBranch){
        Map<String, Object> param = new HashMap<>();
        param.put("finprodId",finProdId);
        if(liBranch!=null){
            param.put("liBranch",liBranch);
        }
        TrdSecOverdueDuration001Dto trdSecOverdueDurationDto = restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/selectWithOutBranchByLiBranch",
                param, TrdSecOverdueDuration001Dto.class);
        return trdSecOverdueDurationDto;
    }

    public TrdOverdueCalcuPenaltyDto getTrdOverdueCalcuPenaltyDto(Long bjBranch, Long liBranch, String penaltyBaseType, String withOutBranchs, Map<String, Object> param, TrdSecOverdueDuration001Dto withOutBranch) {
        TrdOverdueCalcuPenaltyDto dto;
        dto =  new TrdOverdueCalcuPenaltyDto();
        if(param.containsKey("bjBranch")){
            bjBranch = Long.valueOf(param.get("bjBranch").toString());
        }
        if(param.containsKey("liBranch")){
            liBranch = Long.valueOf(param.get("liBranch").toString());
        }
        TrdSecOverdueDuration001Dto finprodId = this.selectJxwuFromFinprodIdAndBranch(param.get("finprodId").toString(),bjBranch,liBranch,penaltyBaseType);
        if(finprodId!=null){
            try {
                BeanUtil.copyProperties(dto,finprodId);
                if(bjBranch!=null){
                    dto.setBjBranch(param.get("bjBranch").toString());
                }
                if(liBranch!=null){
                    dto.setLiBranch(param.get("liBranch").toString());
                    withOutBranch = this.selectWithoutBranch(param.get("finprodId").toString(), liBranch);
                }
                dto.setVdate(finprodId.getVdateStr());
                dto.setMdate(finprodId.getMdateStr());
                dto.setFxvdate(finprodId.getFxvdateStr());
                if(StringUtil.isNotBlank(finprodId.getFxmdateStr())){
                    dto.setFxmdate(finprodId.getFxmdateStr());
                }
                if(StringUtil.isNotBlank(finprodId.getFxYield())){
                    dto.setYield(new BigDecimal(Double.valueOf(finprodId.getFxYield())));
                }
                if(withOutBranch!=null){
                    dto.setWithoutBranch(withOutBranch.getWithoutBranch());//设置还本不带息
                    withOutBranchs = withOutBranch.getWithoutBranch();
                }
                if(StringUtil.isNotBlank(penaltyBaseType)&&
                        (StringUtil.equals(penaltyBaseType, DictCodeListExt3.罚息计提基数_逾期本息和.getValue())||
                                StringUtil.equals(penaltyBaseType, DictCodeListExt3.罚息计提基数_逾期利息.getValue()))){
                    TrdSecOverdueDuration001Dto dtos = this.selectCashIds(param.get("finprodId").toString(),bjBranch,liBranch,withOutBranchs);
                    dto.setCashIds(dtos.getCashIds());
                    // add by jiangpeng on 20210428 start
                    // TISHKBS-2061 CLONE - 投管系统本身审批报错，计划类存续期应收款查询，点击计息要素维护，计划类利率型可以2次提交至审批人
                    dto.setOldCashIds(dtos.getCashIds());
                    // add by jiangpeng on 20210428 end
                }else{
                    dto.setCashIds(finprodId.getCashIds());
                    // add by jiangpeng on 20210428 start
                    // TISHKBS-2061 CLONE - 投管系统本身审批报错，计划类存续期应收款查询，点击计息要素维护，计划类利率型可以2次提交至审批人
                    dto.setOldCashIds(finprodId.getCashIds());
                    // add by jiangpeng on 20210428 end
                }
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
        }
        return dto;
    }

}
