
package com.joyintech.tams.invest.trade.pretrdrepotrade.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussRepoSecService;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.*;
import com.joyintech.tams.invest.common.dto.common.*;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.PreTrdCommonDto;
import com.joyintech.tams.invest.common.trade.service.PreTrdCommonBaseService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.trade.conbond.dto.TrdConBondDto;
import com.joyintech.tams.invest.trade.pretrdrepotrade.dto.PreTrdRepoTrade002Dto;
import com.joyintech.tams.invest.trade.pretrdrepotrade.dto.PreTrdRepoTrade002Dto;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PreTrdRepoTrade002Service extends BaseService<PreTrdRepoTrade002Dto> implements BaseProcessService<PreTrdRepoTrade002Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(PreTrdRepoTrade002Service.class);

    @Autowired(required = false)
    private BussRepoSecService bussRepoSecService;
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;
    @Autowired
    private MapperDelegateService mapperDelegateService;
    @Autowired(required = false)
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;
    /**
     * 平安银行算法组件
     */
    @Autowired
    private BussCoreCalculateService bussCoreCalculateService;
    @Autowired
    private WflTrdProductDealAddPreMapper wflTrdProductDealAddPreMapper;
    @Autowired
    private WflTrdProductDealPreMapper wflTrdProductDealPreMapper;
    @Autowired
    private WflTrdProductDealOthPreMapper wflTrdProductDealOthPreMapper;
    @Autowired
    private TrdFinProductDealTotalMapper TrdFinProductDealTotalMapper;
    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private DateCalService dateCalService;
    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    @Autowired
    private TrdProductDealPreMapper TrdProductDealPreMapper;
    @Autowired
    private TrdProductDealAddPreMapper TrdProductDealAddPreMapper;
    @Autowired
    private EtrCommonCashPositionDealService etrCommonCashPositionDealService;
    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;
    @Autowired
    private PreTrdCommonBaseService preTrdCommonBaseService;
    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    @Override
    public void doUpdate(PreTrdRepoTrade002Dto entity){
        if (!StringUtil.equals("revoke", entity.getOperateType())
                && !StringUtil.equals("delete", entity.getOperateType())) {
            // 删除质押品数据
            deleteFinDealPledgePre(entity.getWorkflowId());
            //删除组合及指令头寸
            preTrdCommonBaseService.deletePosition(entity.getWorkflowId());
            // 先删除流程中数据
            deleteWflByWorkflowId(entity.getWorkflowId());
            this.prepareEntity(entity);
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(PreTrdRepoTrade002Dto.class, true);
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
            // 重新insert
            this.handleBussFlowData(entity);

        }
    }

    private void deleteFinDealPledgePre(String workflowId) {
        preTrdCommonBaseService.deleteFinDealPledgePre(workflowId);
    }

    @Override
    public void doDelete(String workflowId){
        preTrdCommonBaseService.deletePosition(workflowId);
        //删除质押品
        preTrdCommonBaseService.deleteFinDealPledgePre(workflowId);

        deleteWflByWorkflowId(workflowId);
    }
    /**
     * 工作流审核完成doComplete
     *
     * @param input         entity
     * @param operationType operationType
     */
    @Override
    public void doComplete(PreTrdRepoTrade002Dto input, OperationType operationType) {
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            TrdFinProductDealTotal TrdFinProductDealTotal = new TrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(TrdFinProductDealTotal,input.getTrdFinProductDealTotal());
            TrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            TrdFinProductDealTotal.setWorkflowId(input.getWorkflowId());
            // 是否撤销入否
            TrdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            //插入交易汇总表
            TrdFinProductDealTotalMapper.insertSelective(TrdFinProductDealTotal);

            //List<金融产品交易（预录入）dto>
            List<TrdProductDealPre> trdProductDealPreList = input.getTrdProductDealPreList();
            mapperDelegateService.batchInsertSelective(TrdProductDealPreMapper.class,trdProductDealPreList);
            //List<金融产品交易（预录入）附属表dto>
            List<TrdProductDealAddPre> trdProductDealAddPreList = input.getTrdProductDealAddPreList();
            mapperDelegateService.batchInsertSelective(TrdProductDealAddPreMapper.class,trdProductDealAddPreList);
            //更新委托完成度
            PreTrdCommonDto preTrdCommonDto = new PreTrdCommonDto();
            preTrdCommonDto.setTrdProductDealPreList(input.getTrdProductDealPreList());
            preTrdCommonDto.setTrdProductDealAddPreList(input.getTrdProductDealAddPreList());
            preTrdCommonBaseService.updateEtrEntrustAmt(preTrdCommonDto,false);
        }else {
            this.doCompleteRevoke(input);
            //处理组合及指令头寸
            PreTrdCommonDto preTrdCommonDto = new PreTrdCommonDto();
            preTrdCommonDto.setTrdProductDealPreList(input.getTrdProductDealPreList());
            preTrdCommonDto.setTrdProductDealAddPreList(input.getTrdProductDealAddPreList());
            preTrdCommonBaseService.excutePosition(preTrdCommonDto,true);

            //更新委托完成度
            preTrdCommonBaseService.updateEtrEntrustAmt(preTrdCommonDto,true);
        }
        // 7.2 删除原业务流程表中的交易记录
        deleteWflByWorkflowId(input.getWorkflowId());
    }

    private void doCompleteRevoke(PreTrdRepoTrade002Dto input) {
        Map<String, Object> params = new HashMap<>();
        params.put("workflowId", input.getWorkflowId());
        params.put("workflowStatus",DictCodeListExt1.数据状态_删除.getValue());
        params.put("bizCode", input.getTotleTradeId());
        params.put("user", SecurityUtil.getLoginUser().getUserName());
        params.put("dept", SecurityUtil.getLoginUser().getDeptCode());
        params.put("time", new Date());
        params.put("revokeReason", input.getRevokeReason());

        preTrdCommonBaseService.doRevokeComplete(params);
    }

    private String deleteWflByWorkflowId(String workflowId) {
        wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealPreMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealOthPreMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealAddPreMapper.deleteByWorkflowId(workflowId);
        return Boolean.TRUE.toString();
    }

    /**
     * 1、检查到期日是否为节假日
     * 到期日如果是节假日 要提醒
     * <p>
     * 2、逆回购时，本金金额 〉 组合可用剩余现金，提示
     *
     * @param inputDto 画面输入参数
     * @param result   valid结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    public AjaxResult checkConfirm(PreTrdRepoTrade002Dto inputDto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return null;
        }

        String confirmMsg = "";
        String changeLineMark = "<br/>";

        // 到期日如果是节假日 要提醒
        boolean isDealCalendarHoliday = dateCalService.isDealCalendarHoliday(
                inputDto.getMdate(), inputDto.getCalendarId());
        if (isDealCalendarHoliday) {
            if (StringUtil.isNotBlank(confirmMsg)) {
                confirmMsg += changeLineMark;
            }
            confirmMsg += getMessage("BUSS_COM_E_0039", getMessage("TrdRepoTrade002.mdate"));
        }


        if (StringUtil.isNotBlank(confirmMsg)) {
            AjaxResult wr = new AjaxResult();
            wr.setData(confirmMsg);
            return wr;
        }

        return null;
    }
    /**
     * 工作流doIdoInsert
     *
     * @param
     * @param operationType operationType
     */
    @Override
    public void doInsert(PreTrdRepoTrade002Dto dto, OperationType operationType) {
        // 新建交易
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            if (dto != null && StringUtil.equals("",dto.getWflTrdFinProductDealTotal().getWorkflowId())){
                dto.getWflTrdFinProductDealTotal().setWorkflowId(dto.getWorkflowId());
                dto.getWflTrdProductDealPreList().forEach(item ->{
                    item.setWorkflowId(dto.getWorkflowId());
                });
                dto.getWflTrdProductDealAddPreList().forEach(item -> {
                    item.setWorkflowId(dto.getWorkflowId());
                });
                dto.getWflTrdProductDealOthPreList().forEach(item ->{
                    item.setWorkflowId(dto.getWorkflowId());
                });
            }
            this.handleBussFlowData(dto);
        }// 撤销交易
        else {
            this.doRevokeInsert(dto);
        }
    }

    private void doRevokeInsert(PreTrdRepoTrade002Dto dto) {
        Map<String, Object> params = new HashMap<>();
        params.put("bizCode", dto.getTotleTradeId());
        params.put("workflowId", dto.getWorkflowId());
        params.put("workflowStatus", DictCodeList.数据状态_删除中.getValue());
        params.put("user", SecurityUtil.getLoginUser().getUserName());
        params.put("dept", SecurityUtil.getLoginUser().getDeptCode());
        params.put("revokeReason", dto.getRevokeReason());
        params.put("time", new Date());
        //撤销流程中
        preTrdCommonBaseService.insertWflTable(params);
    }

    /**
     * 提交时登录业务流程表
     *
     * @param input 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(PreTrdRepoTrade002Dto input) {
        if (!StringUtil.equals("submit", input==null?null:input.getOperateType())) {
            return;
        }
        // 构建金融产品交易汇总业务流程Dto insert
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = input.getWflTrdFinProductDealTotal();
        wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);
        //List<金融产品交易（预录入）业务流程dto>
        List<WflTrdProductDealPre> wflTrdProductDealPreList = input.getWflTrdProductDealPreList();
        mapperDelegateService.batchInsertSelective(WflTrdProductDealPreMapper.class,wflTrdProductDealPreList);
        //List<金融产品交易（预录入）业务流程附属表dto>
        List<WflTrdProductDealAddPre> wflTrdProductDealAddPreList = input.getWflTrdProductDealAddPreList();
        mapperDelegateService.batchInsertSelective(WflTrdProductDealAddPreMapper.class,wflTrdProductDealAddPreList);
        //List<金融产品交易（预录入）业务流程其他信息dto>
        List<WflTrdProductDealOthPre>  wflTrdProductDealOthPreList = input.getWflTrdProductDealOthPreList();
        mapperDelegateService.batchInsertSelective(WflTrdProductDealOthPreMapper.class,wflTrdProductDealOthPreList);
        //预录入交易回购质押品
        List<FinDealPledgePre> finDealPledgePreList = input.getFinDealPledgePreList();
        mapperDelegateService.batchInsertSelective(FinDealPledgePreMapper.class,finDealPledgePreList);
        //处理组合及指令头寸
        PreTrdCommonDto preTrdCommonDto = new PreTrdCommonDto();
        preTrdCommonDto.setTrdProductDealPreList(input.getTrdProductDealPreList());
        preTrdCommonDto.setTrdProductDealAddPreList(input.getTrdProductDealAddPreList());
        preTrdCommonBaseService.excutePosition(preTrdCommonDto,false);
        //调用授信共通
        List<TrdProductDealPre> trdProductDealPreList = input.getTrdProductDealPreList();
        if (!StringUtil.isEmptyList(trdProductDealPreList)){
            trdProductDealPreList.forEach(item ->{
                TrdProductDeal trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, item);
                trdProductDeal.setfTradeId(item.getTradeId());
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setRepoType(input.getRepoType());
                inputDto.setTrdProductDeal(trdProductDeal);
                AllTradeCommonUtil.handleBeforeTradeComplete(inputDto,false);
            });
        }
    }
    /**
     * 构造Entity
     *
     * @param input input
     */
    public void prepareEntity(PreTrdRepoTrade002Dto input) {
        //组合信息和质押券信息处理
        input.setPledgeBondList(input.getBussRepoSecPtl().getPledgeBondList());
        input.setPledgeBondListDetailed(input.getBussRepoSecPtl().getPledgeBondListDetailed());

        //构建金融产品交易汇总业务流程Dto add by daizhiwei  ---start----
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal,input);
        if (StringUtil.isNotEmpty(input.getTradeDate())&&StringUtil.isNotEmpty(input.getEffTimeStr())){
            wflTrdFinProductDealTotal.setEffTime(DateUtil.parse(DateUtil.format(input.getTradeDate()) + " "+ input.getEffTimeStr() + ":00"));
        }
        wflTrdFinProductDealTotal.setFinprodId(wflTrdFinProductDealTotal.getTotleTradeId());
        wflTrdFinProductDealTotal.setBusiType(DictCodeListExt1.共通业务类型_质押式回购交易预录入.getValue());
        if (StringUtil.equals(input.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
            wflTrdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
            wflTrdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        } else {
            wflTrdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
            wflTrdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        }
        wflTrdFinProductDealTotal.setBranch(new Long(0));
        wflTrdFinProductDealTotal.setSettleDate(input.getTradeDate());
        if (StringUtil.equals(input.getSettleSpeedText(),DictCodeList.清算速度_T_0.getValue())){
            wflTrdFinProductDealTotal.setSettleSpeed(new Long(0));
        }else if (StringUtil.equals(input.getSettleSpeedText(),DictCodeList.清算速度_T_1.getValue())){
            wflTrdFinProductDealTotal.setSettleSpeed(new Long(1));
        }
        wflTrdFinProductDealTotal.setTradePlat(DictCodeListExt1.交易场所_银行间.getValue());
        wflTrdFinProductDealTotal.setTradeAmt(input.getMdateSettleAmt());
        wflTrdFinProductDealTotal.setShareAmt(input.getPrinAmt());
        wflTrdFinProductDealTotal.setPrinAmt(input.getPrinAmt());
        wflTrdFinProductDealTotal.setFeeAmtPay(BigDecimal.ZERO);
        wflTrdFinProductDealTotal.setFeeAmt(BigDecimal.ZERO);
        wflTrdFinProductDealTotal.setIntAmt(input.getRebackIntAmt());
        wflTrdFinProductDealTotal.setRate(ArithmeticUtil.divide(input.getEntrustPrice(),new BigDecimal(100),4));
        wflTrdFinProductDealTotal.setSecManageAcctId(input.getSecAcctId());
        wflTrdFinProductDealTotal.setIsOutTrade(DictCodeListExt1.是否_是.getValue());
        wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdFinProductDealTotal.setIsCancel(DictCodeListExt1.是否_否.getValue());
        wflTrdFinProductDealTotal.setIsClean(DictCodeListExt1.是否_是.getValue());
        wflTrdFinProductDealTotal.setWorkflowId(input.getWorkflowId());
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        input.setWflTrdFinProductDealTotal(wflTrdFinProductDealTotal);
        //构建金融产品交易汇总业务流程Dto add by daizhiwei  ---end----
        //构建金融产品交易汇总Dto add by daizhiwei  ---start----
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal,wflTrdFinProductDealTotal);
        input.setTrdFinProductDealTotal(trdFinProductDealTotal);
        //构建金融产品交易汇总Dto add by daizhiwei  ---end----

        //List<金融产品交易（预录入）业务流程dto>
        List<WflTrdProductDealPre> wflTrdProductDealPreList = new ArrayList<>();
        input.setWflTrdProductDealPreList(wflTrdProductDealPreList);
        //List<金融产品交易（预录入）业务流程附属表dto>
        List<WflTrdProductDealAddPre> wflTrdProductDealAddPreList = new ArrayList<>();
        input.setWflTrdProductDealAddPreList(wflTrdProductDealAddPreList);
        //List<金融产品交易（预录入）业务流程其他信息dto>
        List<WflTrdProductDealOthPre>  wflTrdProductDealOthPreList = new ArrayList<>();
        input.setWflTrdProductDealOthPreList(wflTrdProductDealOthPreList);
        //List<金融产品交易（预录入）dto>
        List<TrdProductDealPre> trdProductDealPreList = new ArrayList<>();
        input.setTrdProductDealPreList(trdProductDealPreList);
        //List<金融产品交易（预录入）附属表dto>
        List<TrdProductDealAddPre> trdProductDealAddPreList = new ArrayList<>();
        input.setTrdProductDealAddPreList(trdProductDealAddPreList);
        //预录入交易回购质押品
        List<FinDealPledgePre> finDealPledgePreList = new ArrayList<>();
        input.setFinDealPledgePreList(finDealPledgePreList);

        if (!StringUtil.isEmptyList(input.getPledgeBondList())){
            for (PledgeBondInfoDto pledgeBondInfoDto : input.getPledgeBondList()){
                String tradeId = "";
                if (baseGetBusinessId != null) {
                    BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                    if (StringUtil.equals(input.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
                        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
                    } else {
                        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
                    }
                    tradeId = baseGetBusinessId.buildBussinessCode(codeParameter);
                } else {
                    tradeId = IDGenerator.generate();
                }
                //金融产品交易（预录入）业务流程dto
                WflTrdProductDealPre wflTrdProductDealPre = new WflTrdProductDealPre();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealPre,wflTrdFinProductDealTotal);
                wflTrdProductDealPre.setTradeId(tradeId);
                wflTrdProductDealPre.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId());
                wflTrdProductDealPre.setTradeAmt(pledgeBondInfoDto.getPrinAmt());
                wflTrdProductDealPre.setShareAmt(pledgeBondInfoDto.getPrinAmt());
                wflTrdProductDealPre.setPrinAmt(pledgeBondInfoDto.getPrinAmt());
                //预录入交易回购质押品
                for (PledgeBondInfoDto pledgeBondInfo : input.getPledgeBondListDetailed()){
                    if (StringUtil.equals(pledgeBondInfoDto.getPortfolioId(),pledgeBondInfo.getPortfolioId())){
                        FinDealPledgePre finDealPledgePre = new FinDealPledgePre();
                        finDealPledgePre.setTradeId(tradeId);
                        finDealPledgePre.setPledgeFinprodId(pledgeBondInfo.getBondCode());
                        finDealPledgePre.setPledgeFaceValue(new BigDecimal(100));
                        finDealPledgePre.setPledgeAmt(pledgeBondInfo.getPledgePrin());
                        finDealPledgePre.setPledgeNumber(ArithmeticUtil.divide(pledgeBondInfo.getPledgePrin(),new BigDecimal(100),2));
                        finDealPledgePre.setPledgeRatio(ArithmeticUtil.divide(pledgeBondInfo.getPledgeRatio(),new BigDecimal(100),2));
                        finDealPledgePreList.add(finDealPledgePre);
                        wflTrdProductDealPre.setFinprodId(pledgeBondInfo.getFinprodId());
                    }
                }
                //金融产品交易（预录入）业务流程附属表dto
                WflTrdProductDealAddPre wflTrdProductDealAddPre = new WflTrdProductDealAddPre();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAddPre,wflTrdProductDealPre);
                wflTrdProductDealAddPre.setTradeId(tradeId);
                wflTrdProductDealAddPre.setPortfolioId(pledgeBondInfoDto.getPortfolioId());
                wflTrdProductDealAddPre.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
                wflTrdProductDealAddPre.setEntrustId(pledgeBondInfoDto.getEntrustId());
                wflTrdProductDealAddPre.setRegDateAmt(new BigDecimal(pledgeBondInfoDto.getEntrustSurAmt() == null ? "0" : pledgeBondInfoDto.getEntrustSurAmt()));
                wflTrdProductDealAddPre.setWorkflowId(input.getWorkflowId());
                wflTrdProductDealAddPre.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                //金融产品交易（预录入）业务流程其他信息dto
                WflTrdProductDealOthPre wflTrdProductDealOthPre = new WflTrdProductDealOthPre();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealOthPre,wflTrdProductDealPre);
                wflTrdProductDealOthPre.setTradeId(tradeId);
                wflTrdProductDealOthPre.setPledgeFinprodId(wflTrdProductDealPre.getFinprodId());
                wflTrdProductDealOthPre.setPledgeFinprodType(wflTrdProductDealPre.getFinprodType2());
                wflTrdProductDealOthPre.setBasis(input.getBasis());
                wflTrdProductDealOthPre.setRate(ArithmeticUtil.divide(input.getEntrustPrice(), new BigDecimal("100"), 8));
                wflTrdProductDealOthPre.setTerm(input.getRepoTerm());
                wflTrdProductDealOthPre.setmTradeAmt(pledgeBondInfoDto.getPrinAmt());
                wflTrdProductDealOthPre.setTermType(input.getTermType());
                wflTrdProductDealOthPre.setWorkflowId(input.getWorkflowId());
                wflTrdProductDealOthPre.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                //金融产品交易（预录入）dto
                TrdProductDealPre trdProductDealPre = new TrdProductDealPre();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDealPre,wflTrdProductDealPre);
                trdProductDealPre.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                //金融产品交易（预录入）附属表dto
                TrdProductDealAddPre trdProductDealAddPre = new TrdProductDealAddPre();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDealAddPre,wflTrdProductDealAddPre);
                //构建list集合
                wflTrdProductDealPreList.add(wflTrdProductDealPre);
                wflTrdProductDealAddPreList.add(wflTrdProductDealAddPre);
                wflTrdProductDealOthPreList.add(wflTrdProductDealOthPre);
                trdProductDealPreList.add(trdProductDealPre);
                trdProductDealAddPreList.add(trdProductDealAddPre);
            }
        }
    }


    public PreTrdRepoTrade002Dto getPreTrdRepoTradeDto(String tradeId) {
        Map<String,Object> params = new HashMap<>();
        if (tradeId.endsWith("R")){
            params.put("tradeId",tradeId.substring(0,tradeId.length()-1));
        }else {
            params.put("tradeId",tradeId);
        }
        PreTrdRepoTrade002Dto dto = restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/PreTrdRepoTrade001/getPreTrdRepoTradeDto",params, PreTrdRepoTrade002Dto.class);
        dto.setActCapDays(dto.getRepoTerm());
        dto.setEntrustPrice(dto.getRate());
        Long settleSpeed = dto.getSettleSpeed();
        if (settleSpeed != null && Long.compare(settleSpeed,new Long(0)) == 0){
            dto.setSettleSpeedText(DictCodeList.清算速度_T_0.getValue());
        }else {
            dto.setSettleSpeedText(DictCodeList.清算速度_T_1.getValue());
        }
//        //计算金额信息
//        BigDecimal rebackIntAmt = new BigDecimal(0);
//        if (StringUtil.isNotEmpty(dto.getActCapDays()) && StringUtil.isNotEmpty(dto.getPrinAmt()) && StringUtil.isNotEmpty(dto.getEntrustPrice())){
//            BigDecimal number = ArithmeticUtil.multiply(dto.getPrinAmt(),dto.getEntrustPrice());
//            BigDecimal num = ArithmeticUtil.multiply(ArithmeticUtil.divide(number,100,8),BigDecimal.valueOf(dto.getActCapDays()));
//            rebackIntAmt = ArithmeticUtil.divide(num,365,2);
//            dto.setRebackIntAmt(rebackIntAmt);
//            dto.setMdateSettleAmt(ArithmeticUtil.doAdd(2, RoundingMode.HALF_UP,rebackIntAmt,dto.getPrinAmt()));
//        }
        //构建组合信息和质押券信息
        List<PledgeBondInfoDto> listDeailed = dto.getPledgeBondList();
        List<PledgeBondInfoDto> list = new ArrayList<>();
        if (!StringUtil.isEmptyList(listDeailed)){
            BussRepoSecDto bussRepoSecPtl = new BussRepoSecDto();
            bussRepoSecPtl.setPledgeBondListDetailed(listDeailed);
            Map<String,PledgeBondInfoDto> map = new HashMap<>();
            listDeailed.forEach(item -> {
                item.setPledgeAmt(item.getPledgePrin());
                item.setPledgeRatio(ArithmeticUtil.multiply(item.getPledgeRatio(), new BigDecimal(100)));
                if (StringUtil.isNotEmpty(map.get(item.getPortfolioId()))){
                    PledgeBondInfoDto pledgeBondInfoDto = map.get(item.getPortfolioId());
                    if (StringUtil.isEmpty(pledgeBondInfoDto.getEntrustId())){
                        pledgeBondInfoDto.setEntrustSurAmt("");
                    }
                    pledgeBondInfoDto.setPledgeAmt(ArithmeticUtil.doAdd(item.getPledgeAmt(),pledgeBondInfoDto.getPledgeAmt()));
                    item.setPortfolioId(item.getPortfolioId()+1);
                    map.put(item.getPortfolioId(),item);
                }else {
                    map.put(item.getPortfolioId(),item);
                }
                String finprodAbbr = preTrdCommonBaseService.getFinprodAbbr(item.getFinprodId());
                item.setBondAbbrName(finprodAbbr);
            });
            list = map.values().stream().collect(Collectors.toList());
            bussRepoSecPtl.setPledgeBondList(list);
            dto.setBussRepoSecPtl(bussRepoSecPtl);
        }
        return dto;
    }

    public PreTrdRepoTrade002Dto getPreTrdRepoTradeDtoFromEtr(String entrustId) {
        PreTrdRepoTrade002Dto preTrdRepoTrade002Dto = new PreTrdRepoTrade002Dto();
        BaseEtrInvestInfoDto etrInvestInfoDto = baseEtrInvestInfoService.queryActiveMandate(entrustId);
        if (!StringUtil.isNotEmpty(etrInvestInfoDto)){
            return preTrdRepoTrade002Dto;
        }
        List<BaseEtrInvestInfoDto> etrInvestInfoDtoList = new ArrayList<>();
        if(StringUtil.isEmptyList(etrInvestInfoDto.getBaseEtrInvestInfoDtoList())){
            etrInvestInfoDtoList.add(etrInvestInfoDto);
        }else {
            etrInvestInfoDtoList = etrInvestInfoDto.getBaseEtrInvestInfoDtoList();
        }
        preTrdRepoTrade002Dto.setPs(etrInvestInfoDtoList.get(0).getEtrEntrust().getPs());
        preTrdRepoTrade002Dto.setRepoTerm(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getTermDays());
        //清算速度
        if (Long.compare(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getSettleSpreed(), new Long(0)) == 0){
            preTrdRepoTrade002Dto.setSettleSpeedText(DictCodeList.清算速度_T_0.getValue());
        }else if (Long.compare(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getSettleSpreed(), new Long(1)) == 0){
            preTrdRepoTrade002Dto.setSettleSpeedText(DictCodeList.清算速度_T_1.getValue());
        }
        preTrdRepoTrade002Dto.setEntrustPrice(ArithmeticUtil.multiply(etrInvestInfoDtoList.get(0).getEtrEntrust().getPrice(),new BigDecimal("100")));
        preTrdRepoTrade002Dto.setCounterId(etrInvestInfoDtoList.get(0).getEtrEntrust().getCounterId());
        preTrdRepoTrade002Dto.setCounterTrader(etrInvestInfoDtoList.get(0).getEtrEntrust().getCounterTrader());
        preTrdRepoTrade002Dto.setSecManageAcctId(etrInvestInfoDtoList.get(0).getEtrEntrustAdd().getSecAcctId());
        preTrdRepoTrade002Dto.setTrader(LoginUserUtil.getUserName());
        //交易金额
        BigDecimal amount = new BigDecimal(0);
        BigDecimal EntrustSurAmtBig = new BigDecimal(0);
        //组合信息
        List<PledgeBondInfoDto> list = new ArrayList<>();
        List<PledgeBondInfoDto> listDetail = new ArrayList();
        for (BaseEtrInvestInfoDto baseEtrInvestInfoDto : etrInvestInfoDtoList){
            //组合信息
            PledgeBondInfoDto pledgeBondInfoDto = new PledgeBondInfoDto();
            pledgeBondInfoDto.setSecAcctId(baseEtrInvestInfoDto.getEtrEntrustAdd().getSecAcctId());
            pledgeBondInfoDto.setEntrustId(baseEtrInvestInfoDto.getEtrEntrust().getEntrustId());
            pledgeBondInfoDto.setPortfolioId(baseEtrInvestInfoDto.getEtrEntrust().getPortfolioId());
            String portfolioName = preTrdCommonBaseService.getPortfolioName(baseEtrInvestInfoDto.getEtrEntrust().getPortfolioId());
            BigDecimal entrustAurAmt = preTrdCommonBaseService.getEntrustSurAmt(baseEtrInvestInfoDto.getEtrEntrust().getEntrustId());
            pledgeBondInfoDto.setEntrustSurAmt(entrustAurAmt.toString());
            pledgeBondInfoDto.setPrinAmt(baseEtrInvestInfoDto.getEtrEntrust().getEntrustAmt());
            // 将交易金额由委托金额汇总改为由未完成委托金额汇总 by 赵东东 20200326 start
            if(StringUtil.isNotEmpty(pledgeBondInfoDto.getEntrustSurAmt())) {
             EntrustSurAmtBig = new BigDecimal(pledgeBondInfoDto.getEntrustSurAmt());
            }
            amount = ArithmeticUtil.doAdd(amount,EntrustSurAmtBig);
            // 将交易金额由委托金额汇总改为由未完成委托金额汇总 by 赵东东 20200326 start
            List<EtrPledge> etrPledgeList = baseEtrInvestInfoDto.getEtrPledgeList();
            BigDecimal pledgeAmt = BigDecimal.ZERO;
            for (EtrPledge etrPledge : etrPledgeList){
                //质押券汇总信息
                PledgeBondInfoDto pledgeBond = new PledgeBondInfoDto();
                BeanUtil.copyPropertiesIgnoreNull(pledgeBond,pledgeBondInfoDto);
                BeanUtil.copyPropertiesIgnoreNull(pledgeBond,etrPledge);
                pledgeBond.setPledgePrin(etrPledge.getPledgeAmt());
                pledgeBond.setPledgeRatio(ArithmeticUtil.multiply(etrPledge.getPledgeRatio(),new BigDecimal("100")));
                pledgeBond.setPortfolioName(portfolioName);
                String finprodAbbr = preTrdCommonBaseService.getFinprodAbbr(etrPledge.getFinprodId());
                pledgeBond.setBondCode(etrPledge.getFinprodId());
                pledgeBond.setBondAbbrName(finprodAbbr);
                listDetail.add(pledgeBond);
                pledgeAmt = ArithmeticUtil.doAdd(pledgeAmt,ArithmeticUtil.multiply(etrPledge.getPledgeAmt(),etrPledge.getPledgeRatio()));
            }
            pledgeBondInfoDto.setPledgeAmt(pledgeAmt);
            //组合可用现金
            if (bussTrdAvailableCashQueryService != null) {
                Map<String, Object> param = new HashMap<>();

                Date tradeDate = DateUtil.platDateFromCache();
                String dateId = "03"; //银行间
                List<String> holidayList = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDay(tradeDate, 15), dateId);
                param.put("calendarId",dateId);
                param.put("tradeDate",DateUtil.format(tradeDate));
                param.put("ccy", BussinessDictCodeList.币种_人民币.getValue());
                param.put("portfolioId",pledgeBondInfoDto.getPortfolioId());
                param.put("acctTypeList", DictCodeListExt1.资金账户类型_产品托管户.getValue());
                param.put("tradeDateNext", DateUtil.format(dateCalService.calculationTradingDay(tradeDate, 1, holidayList, "1")));
                Map<String,Object> resultMap = bussTrdAvailableCashQueryService.getAvailableCash(param);
                pledgeBondInfoDto.setAvalibleCash(new BigDecimal(String.valueOf(resultMap.get("T_0"))));
                pledgeBondInfoDto.setAvalibleCashT1(new BigDecimal(String.valueOf(resultMap.get("T_1"))));
            }
            list.add(pledgeBondInfoDto);
        }
        BussRepoSecDto bussRepoSecPtl = new BussRepoSecDto();
        bussRepoSecPtl.setPledgeBondListDetailed(listDetail);
        bussRepoSecPtl.setPledgeBondList(list);
        preTrdRepoTrade002Dto.setBussRepoSecPtl(bussRepoSecPtl);
        preTrdRepoTrade002Dto.setPrinAmt(amount);
        return preTrdRepoTrade002Dto;
    }
}
