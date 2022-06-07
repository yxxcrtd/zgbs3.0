/*
 * 文件名：CfetsBondDealQuotedPrice002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trdredeem.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.BigDecimalUtil;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondMapper;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealDbDto;
import com.joyintech.tams.cfets.trd.bond.dto.PtlAllocationDto;
import com.joyintech.tams.cfets.trd.bond.service.IbuQuoteBondService;
import com.joyintech.tams.cfets.trdredeem.dao.CfetsTrdRedeemMapper;
import com.joyintech.tams.cfets.trdredeem.dto.CfetsTrdRedeemCheckRequst;
import com.joyintech.tams.cfets.trdredeem.dto.CfetsTrdRedeemDealCommonDto;
import com.joyintech.tams.cfets.trdredeem.dto.CfetsTrdRedeemDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trdredeem.dto.CfetsTrdRedeemRegInfoListResponse;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.trade.entrust.EtrFinishStatusDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service.getBussinessCodeParameter;

/**
 * 赎回登记
 */
@Service("CfetsTrdRedeem001Service")
public class CfetsTrdRedeem001Service extends BaseService<CfetsTrdRedeemDealRegisterQuoteDto> implements BaseProcessService<CfetsTrdRedeemDealRegisterQuoteDto> {

    @Autowired
    private IPlatformDate iPlatformDate;
    @Autowired
    private FinProductMapper finProductMapper;
    @Autowired
    private CfetsIbuQuoteBondMapper cfetsIbuQuoteBondMapper;
    @Autowired
    private CfetsTrdRedeemMapper cfetsTrdRedeemMapper;

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private RegFinProductDealTotalMapper regFinProductDealTotalMapper;

    @Autowired
    private RegFinProductDealTotalAddMapper regFinProductDealTotalAddMapper;

    @Autowired
    private RegFinProductDealMapper regFinProductDealMapper;

    @Autowired
    private RegFinProductDealAddMapper regFinProductDealAddMapper;

    @Autowired
    private WflRegFinProductDealTotalMapper wflRegFinProductDealTotalMapper;

    @Autowired
    private WflRegFinProDealTotalAddMapper wflRegFinProDealTotalAddMapper;

    @Autowired
    private WflRegTradeFeeTotalMapper wflRegTradeFeeTotalMapper;

    @Autowired
    private WflRegFinProductDealMapper wflRegFinProductDealMapper;

    @Autowired
    private WflRegFinProductDealAddMapper wflRegFinProductDealAddMapper;

    @Autowired
    private WflRegTradeFeeMapper wflRegTradeFeeMapper;

    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoServiceImpl;

    @Autowired
    private FinProductAddMapper finProductAddMapper;

    @Autowired
    private DateCalService dateCalService;

    /**
     * 提交保存check 必填项
     * @param entity
     * @return
     */
    public AjaxResult checkNotEmpty(CfetsTrdRedeemDealCommonDto entity) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        String ps = entity.getPs();
        if(StringUtil.isEmpty(ps)){
            errorList.add(new ValidMessage("请输入交易方向"));
        }
        if(StringUtil.isEmpty(entity.getFinprodId())){
            errorList.add(new ValidMessage("请输入资产代码"));
        }

        if(CollectionUtils.isEmpty(entity.getPtlAllocatios())){
            errorList.add(new ValidMessage("组合分配不可为空"));
        }
        //查询该资产在这个理论行权日是否已经有数据了
        if(StringUtil.isNotEmpty(entity.getFinprodId()) && StringUtil.isNotEmpty(entity.getTheoreticalExerciseDate())){
            Map<String,Object> map = new HashMap<>();
            map.put("finprodId",entity.getFinprodId());
            map.put("settleDate",entity.getTheoreticalExerciseDate());
            List<CfetsTrdRedeemRegInfoListResponse> regList=cfetsTrdRedeemMapper.isExistRegInfo(map);
            if(!StringUtil.isEmptyList(regList) && regList.size()>0){
                errorList.add(new ValidMessage("该资产在该行权区间内存在流程中或有效数据，不允许重复提交！"));
            }/*else{
                //保存时，流程中数据
                WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
                wflValidInputParameter.setPageId("CfetsTrdRedeem001");
                wflValidInputParameter.setBusinessDataPk2(entity.getFinprodId());
                wflValidInputParameter.setBusinessDataPk3(DateUtil.format(entity.getTheoreticalExerciseDate()));
                if(null != mstWorkflowCommonService &&
                        mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)){
                    errorList.add(new ValidMessage("该资产在该行权区间内存在流程中数据，不允许重复提交！"));
                }
            }*/
        }
        if(!StringUtil.isNotEmpty(entity.getRedeemRate())){
            errorList.add(new ValidMessage("赎回比例不可为空。"));
        }
        //判断赎回比例是否大于0小于100%
        if(StringUtil.isNotEmpty(entity.getRedeemRate())
                && (entity.getRedeemRate().compareTo(BigDecimal.ZERO)<=0 || entity.getRedeemRate().compareTo(BigDecimal.ONE)>0)){
            errorList.add(new ValidMessage("赎回比例必须大于0小于等于100%。"));
        }
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * 设置流程业务说明
     */
    @Override
    public String generateTitle(CfetsTrdRedeemDealRegisterQuoteDto entity, OperationType type) {
        return  "登记日期"+ ":" + DateUtil.format(entity.getTradeDate())+"-"+
                "资产简称" + ":" + entity.getFinprodAbbr();
    }

    /**
     * 初始化登记
     * @param cfetsBondDealQuoteCommonDto
     * @return
     */
    public CfetsTrdRedeemDealCommonDto initReg(CfetsTrdRedeemDealCommonDto cfetsBondDealQuoteCommonDto){
        String quoteId = cfetsBondDealQuoteCommonDto.getQuoteId();
        if(StringUtil.equals(cfetsBondDealQuoteCommonDto.getValidStatus(), BussinessDictCodeList.有效状态_流程中.getValue())){
            cfetsBondDealQuoteCommonDto = null;//cfetsIbuQuoteBondMapper.getWflCfetsBondDealQuoteCommonDto(quoteId);
            if(null!=cfetsBondDealQuoteCommonDto){
                cfetsBondDealQuoteCommonDto.setPtlAllocatios(cfetsIbuQuoteBondMapper.listWflPtlAllocationDto(quoteId));
            }
        }else{
            cfetsBondDealQuoteCommonDto =null;// cfetsIbuQuoteBondMapper.getCfetsBondDealQuoteCommonDto(quoteId);
            if(null!=cfetsBondDealQuoteCommonDto){
                cfetsBondDealQuoteCommonDto.setPtlAllocatios(cfetsIbuQuoteBondMapper.listPtlAllocationDto(quoteId));
            }
        }
        return cfetsBondDealQuoteCommonDto;
    }

    /**
     * 插入操作
     */
    @Override
    public void doInsert(CfetsTrdRedeemDealRegisterQuoteDto entity,OperationType type){
        if ("save".equals(entity.getOperatorType())) {
            return;
        }
        if (!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
            CfetsBondDealDbDto cfetsBondDealDbDto = prepareEntity(entity,type);
            //登录业务流程
            insertWfl(cfetsBondDealDbDto);
            //头寸
          //  PositionHttpRequest.send(preparePosition(cfetsBondDealDbDto,type));
        }
        //履历表 -> AOP

    }

	@Override
    public void doUpdate(CfetsTrdRedeemDealRegisterQuoteDto entity){
        if ("save".equals(entity.getOperatorType())) {
            return;
        }
        Map<String,Object> map = new HashMap<>();
        if (!StringUtil.equals("revoke", entity.getOperatorType()) && !StringUtil.equals("delete", entity.getOperatorType())) {
            // 先删除流程中数据
            this.deleteBussFlowData(entity.getWorkflowId());
            CfetsBondDealDbDto cfetsBondDealDbDto = prepareEntity(entity,StringUtil.equals(entity.getOperatorType(),"revoke")?OperationType.REVOKE:OperationType.NEW);
            //登录业务流程
            insertWfl(cfetsBondDealDbDto);
            ProcessMark newEntity;
            //头寸
          //  PositionHttpRequest.send(preparePosition(cfetsBondDealDbDto,OperationType.NEW));
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(CfetsTrdRedeemDealRegisterQuoteDto.class, true);
                // 工作流ID
                newEntity.setWorkflowId(entity.getWorkflowId());
                newEntity.setStatus(entity.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(entity));
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(CfetsTrdRedeemDealRegisterQuoteDto entity, OperationType operationType) {
        CfetsBondDealDbDto cfetsBondDealDbDto = prepareEntity(entity,operationType);
        //删除业务流程数据
        deleteBussFlowData(entity.getWorkflowId());
        //登录db
        insert(cfetsBondDealDbDto,operationType);
        // 更新摘牌日
        updateSecDelistingDate(entity,operationType);
        //头寸
        /*if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
            PositionHttpRequest.sendRevoke(cfetsBondDealDbDto.getRegFinProductDealTotal().getTotleTradeId(),DictCodeListExt1.子系统代码_交易.getValue(),"Bond001");
        }else{
            PositionHttpRequest.send(preparePosition(cfetsBondDealDbDto,operationType));
        }*/
        //处理指令执行进度
        //insertEtrFinishStatus(cfetsBondDealDbDto,operationType);
    }

    /**
     * 更新摘牌日
     *
     * @param entity
     * @param operationType
     * @author jiangpeng
     * @date 2022/6/6 18:00
     */
    private void updateSecDelistingDate(CfetsTrdRedeemDealRegisterQuoteDto entity, OperationType operationType) {
        // 如果赎回比例!=100% 不用处理
        if (BigDecimalUtil.ACompareToB(entity.getRedeemRate(), new BigDecimal(100), 4) != 0) {
            return;
        }
        Date theoreticalExerciseDate = entity.getTheoreticalExerciseDate();
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            // 1.赎回登记交易提交并审批生效后，更新债券标的的摘牌日：0
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(entity.getFinprodId());
            // 1.1 更新原摘牌日
            FinProductAdd finProductAdd = new FinProductAdd();
            finProductAdd.setFinprodId(entity.getFinprodId());
            finProductAdd.setOldDelistingDate(DateUtil.format(finProduct.getDelistingDate()));
            finProductAddMapper.updateByPrimaryKey(finProductAdd);
            // 1.2 更新摘牌日
            // 交易日历取得
            List<String> dealCalendarHolidayList = dateCalService.getDealCalendarHoliday(DateUtil.addDay(theoreticalExerciseDate, -8),
                    theoreticalExerciseDate, "03");
            Date tempdate = DateUtil.addDay(theoreticalExerciseDate, -1);
            String dateStr = DateUtil.format(tempdate);
            while (dealCalendarHolidayList.indexOf(dateStr) >= 0) {
                tempdate = DateUtil.addDay(tempdate, -1);
                dateStr = DateUtil.format(tempdate);
            }
            finProduct.setDelistingDate(tempdate);
            finProductMapper.updateByPrimaryKeySelective(finProduct);
        } else {
            // 2.赎回登记交易撤销并审批生效后，需恢复债券标的的摘牌日：
            FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(entity.getFinprodId());
            // 1.1 更新摘牌日
            FinProduct finProduct = new FinProduct();
            finProduct.setFinprodId(entity.getFinprodId());
            finProduct.setDelistingDate(DateUtil.parse(finProductAdd.getOldDelistingDate()));
            finProductMapper.updateByPrimaryKeySelective(finProduct);
            // 1.2 更新原摘牌日
            finProductAdd.setOldDelistingDate(null);
            finProductAddMapper.updateByPrimaryKey(finProductAdd);
        }
    }

    /**
     * 处理指令完成度
     * @param cfetsBondDealDbDto
     * @param operationType
     */
    public void insertEtrFinishStatus(CfetsBondDealDbDto cfetsBondDealDbDto,OperationType operationType){
        RegFinProductDealTotal regFinProductDealTotal = cfetsBondDealDbDto.getRegFinProductDealTotal();
        List<RegFinProductDeal> regFinProductDeals = cfetsBondDealDbDto.getRegFinProductDeals();
        if(CollectionUtils.isNotEmpty(regFinProductDeals)){
            List<EtrFinishStatusDto> etrFinishStatusDtos = new ArrayList<>();
            for (RegFinProductDeal regFinProductDeal : regFinProductDeals) {
                EtrFinishStatusDto status = new EtrFinishStatusDto();
                status.setEntrustId(regFinProductDeal.getEntrustId());
                status.setTradeId(regFinProductDealTotal.getTotleTradeId());
                status.setCstpTradeId(regFinProductDealTotal.getTotleTradeId());
                status.setFinishDate(regFinProductDealTotal.getTradeDate());
                EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(regFinProductDeal.getEntrustId());
                if(etrEntrustAdd != null && StringUtil.isNotEmpty(etrEntrustAdd.getTargetType())
                        && StringUtil.equals(DictCodeListExt3.目标类型_金额.getValue(),etrEntrustAdd.getTargetType())){
                    status.setEntFinishAmt(regFinProductDeal.getTradeAmt());
                }
                else {
                    status.setEntFinishAmt(regFinProductDeal.getShareAmt());
                }
                status.setTrader(regFinProductDealTotal.getOuTraderId());
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    baseEtrInvestInfoServiceImpl.cancelCompleteDelegate(status.getEntrustId(),status.getTradeId());
                }
                etrFinishStatusDtos.add(status);

            }
            if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
                baseEtrInvestInfoServiceImpl.completeDelegate(etrFinishStatusDtos);
            }
        }
    }


    @Override
    public void doDelete(String workflowId) {
        
        deleteBussFlowData(workflowId);
       // CfetsTrdRedeemDealRegisterQuoteDto  entity = getBussinessEntityByWorkflowId(workflowId);
        //发送头寸
       // PositionHttpRequest.deleteMethod(entity.getTotleTradeId(),DictCodeListExt1.子系统代码_交易.getValue());

    }

    /**
     * 组装实体
     * @param entity
     * @return
     */
    public CfetsBondDealDbDto prepareEntity(CfetsTrdRedeemDealRegisterQuoteDto entity,OperationType operationType) {
        CfetsBondDealDbDto result = new CfetsBondDealDbDto();
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
            result.setWorkflowId(entity.getWorkflowId());
            result.setOperationType(entity.getOperatorType());
            result.setRegFinProductDealTotal(regFinProductDealTotalMapper.selectByPrimaryKey(entity.getTotleTradeId()));
            if(StringUtil.isNotEmpty(result.getRegFinProductDealTotal())){
                String remark=result.getRegFinProductDealTotal().getRemark();
                if(StringUtil.isNotEmpty(remark)){
                    result.getRegFinProductDealTotal().setRemark(result.getRegFinProductDealTotal().getRemark()+"。赎回登记撤销："+entity.getRevokeReason());
                }else{
                    result.getRegFinProductDealTotal().setRemark("赎回登记撤销："+entity.getRevokeReason());
                }
                result.setRemark(result.getRegFinProductDealTotal().getRemark());
            }
            result.setRegFinProductDealTotalAdd(regFinProductDealTotalAddMapper.selectByPrimaryKey(entity.getTotleTradeId()));
          //  result.setRegTradeFeeTotals(cfetsIbuQuoteBondMapper.listRegTradeFeeTotal(entity.getTotleTradeId()));
            result.setRegFinProductDeals(cfetsIbuQuoteBondMapper.listRegFinProductDeal(entity.getTotleTradeId()));
            result.setRegFinProductDealAdds(cfetsIbuQuoteBondMapper.listRegFinProductDealAdd(entity.getTotleTradeId()));
         //   result.setRegTradeFees(cfetsIbuQuoteBondMapper.listRegTradeFee(entity.getTotleTradeId()));
        }else{
            //登记日
            entity.setTradeDate(iPlatformDate.getSysDate());
            //理论行权日
            entity.setSettleDate(entity.getTheoreticalExerciseDate());
            entity.setOuTraderId(entity.getMySideTrader());

            //entity.setOuPartyId(entity.getMySide());

          //  entity.setDeliveryType(entity.getSettleMethod());
           // entity.setClearType(entity.getSettleType());
        //    entity.setUnitCprice(entity.getPrice());
         //   entity.setYield(entity.getMatureYield());
           Long settleSpreed = 0L;
             /*if (StringUtil.equals(entity.getSettleSpreedCode(), BussinessDictCodeList.清算速度_T加0.getValue())) {
                settleSpreed = 0L;
            } else if (StringUtil.equals(entity.getSettleSpreedCode(), BussinessDictCodeList.清算速度_T加1.getValue())) {
                settleSpreed = 1L;
            } else if (StringUtil.equals(entity.getSettleSpreedCode(), BussinessDictCodeList.清算速度_T加2.getValue())) {
                settleSpreed = 2L;
            }*/
            //交易方向
            String ps = entity.getPs();
            //投管业务类型
            String tgBusiType =DictCodeListExt3.共通业务类型_赎回登记.getValue();
            //金融产品交易汇总登记
            RegFinProductDealTotal regFinProductDealTotal = new RegFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(regFinProductDealTotal,entity);
           // regFinProductDealTotal.setTradeAmt(cpriceAmt);
            regFinProductDealTotal.setSettleSpeed(settleSpreed);
            regFinProductDealTotal.setQuoteType(entity.getQuoteStyle());
            regFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            regFinProductDealTotal.setFaceValue(ArithmeticUtil.multiply( entity.getShareAmt(),new BigDecimal(10000)));
            regFinProductDealTotal.setShareAmt(ArithmeticUtil.multiply(entity.getShareAmt(),new BigDecimal(100)));
            regFinProductDealTotal.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            regFinProductDealTotal.setDipType(entity.getDistribution());
            regFinProductDealTotal.setBranch(0L);
            regFinProductDealTotal.setTgBusiType(tgBusiType);
            //金融产品交易汇总登记附属
            RegFinProductDealTotalAdd regFinProductDealTotalAdd = new RegFinProductDealTotalAdd();
            BeanUtil.copyPropertiesIgnoreNull(regFinProductDealTotalAdd,entity);
            /*if(StringUtil.equals(ps,DictCodeList.交易方向_买入.getValue())){
                regFinProductDealTotalAdd.setLendinFpriceAmt(entity.getSaleinFpriceAmt());
                regFinProductDealTotalAdd.setLendinIntAmt(entity.getSaleinIntAmt());
                regFinProductDealTotalAdd.setLendinShareAmt(ArithmeticUtil.divide(entity.getSellAbleNum(),new BigDecimal(100),2));
            }
            if(StringUtil.equals(ps,DictCodeList.交易方向_卖出.getValue())){
                regFinProductDealTotalAdd.setLendinShareAmt(ArithmeticUtil.divide(entity.getLendinShareAmt(),new BigDecimal(100),2));
            }*/
            regFinProductDealTotalAdd.setOurShareAmt(regFinProductDealTotal.getShareAmt());
            //汇总登记费用明细表
         //  List<RegTradeFeeTotal> regTradeFeeTotals = new ArrayList<>();
            /* if(null!=entity.getDealFeeAmt()){
                regTradeFeeTotals.add(ibuQuoteBondService.prepareRegTradeFeeTotal(entity.getTotleTradeId(),BussinessDictCodeList.费用类型_交易手续费.getValue(),entity.getDealFeeAmt()));
            }
            if(null!=entity.getSettFeeAmt()){
                //交易手续费
                regTradeFeeTotals.add(ibuQuoteBondService.prepareRegTradeFeeTotal(entity.getTotleTradeId(),BussinessDictCodeList.费用类型_结算手续费.getValue(),entity.getSettFeeAmt()));
            }*/
            //交易登记明细
            List<RegFinProductDeal> regFinProductDeals = new ArrayList<>();
            List<RegFinProductDealAdd> regFinProductDealAdds = new ArrayList<>();
          //  List<RegTradeFee> regTradeFees = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(entity.getPtlAllocatios())){
                for (PtlAllocationDto ptlAllocatio : entity.getPtlAllocatios()){
                    RegFinProductDeal regFinProductDeal = new RegFinProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(regFinProductDeal,regFinProductDealTotal);
                    BussinessCodeParameter codeParameter = getBussinessCodeParameter(entity.getPs());
                    regFinProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
                    regFinProductDeal.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
                    regFinProductDeal.setEntrustId(ptlAllocatio.getEntrustId());
                    regFinProductDeal.setPortfolioId(ptlAllocatio.getPortfolioId());
                    regFinProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                    regFinProductDeal.setShareAmt(ArithmeticUtil.multiply(ptlAllocatio.getShareAmt(),new BigDecimal(100)));
                    BigDecimal cpriceAmt=ArithmeticUtil.multiply(ptlAllocatio.getShareAmt(),new BigDecimal(100),entity.getRedeemRate(),entity.getExercisePrice());

                    regFinProductDeal.setFaceValue(ArithmeticUtil.multiply(ptlAllocatio.getShareAmt(),new BigDecimal(10000)));
                    regFinProductDeal.setTradeAmt(cpriceAmt);
                   // regFinProductDeal.setFpriceAmt(ptlAllocatio.getFpriceAmt());
                   // regFinProductDeal.setCpriceAmt(ptlAllocatio.getCpriceAmt());
                   // regFinProductDeal.setIntAmt(ptlAllocatio.getIntAmt());
                    regFinProductDeal.setTgBusiType(tgBusiType);
                    regFinProductDeal.setSettleSpeed(settleSpreed);
                    regFinProductDeal.setInvAim(ptlAllocatio.getBusinessModel());
                    regFinProductDeal.setTradeMarket(entity.getTradeMarket());
                    regFinProductDeal.setSecManageAcctId(ptlAllocatio.getSecManageAcctId());
                    //附属
                    RegFinProductDealAdd regFinProductDealAdd = new RegFinProductDealAdd();
                    regFinProductDealAdd.setTradeId(regFinProductDeal.getTradeId());
                  //  regFinProductDealAdd.setOurShareAmt(ArithmeticUtil.divide(ptlAllocatio.getOurEntNum(),new BigDecimal(100),2));
                    regFinProductDealAdd.setOurShareAmt(regFinProductDeal.getShareAmt());

                    // regFinProductDealAdd.setLendinShareAmt(ArithmeticUtil.divide(ptlAllocatio.getLendinEntNum(),new BigDecimal(100),2));
                    regFinProductDealAdd.setActSetteDate(entity.getActSetteDate());
                  /*  if(StringUtil.equals(ps,DictCodeList.交易方向_买入.getValue())){
                        regFinProductDealAdd.setLendinShareAmt(ArithmeticUtil.divide(ptlAllocatio.getSellAbleNum(),new BigDecimal(100),2));
                    }*/
                   /* if(null!=ptlAllocatio.getDealFeeAmt()){
                        regTradeFees.add(ibuQuoteBondService.prepareRegTradeFee(regFinProductDeal.getTradeId(),BussinessDictCodeList.费用类型_交易手续费.getValue(),ptlAllocatio.getDealFeeAmt()));
                    }
                    if(null!=ptlAllocatio.getSettFeeAmt()){
                        regTradeFees.add(ibuQuoteBondService.prepareRegTradeFee(regFinProductDeal.getTradeId(),BussinessDictCodeList.费用类型_结算手续费.getValue(),ptlAllocatio.getSettFeeAmt()));
                    }*/
                    regFinProductDeals.add(regFinProductDeal);
                    regFinProductDealAdds.add(regFinProductDealAdd);
                }
            }
            result.setWorkflowId(entity.getWorkflowId());
            result.setOperationType(entity.getOperatorType());
            result.setRegFinProductDealTotal(regFinProductDealTotal);
            result.setRegFinProductDealTotalAdd(regFinProductDealTotalAdd);
         //   result.setRegTradeFeeTotals(regTradeFeeTotals);
            result.setRegFinProductDeals(regFinProductDeals);
            result.setRegFinProductDealAdds(regFinProductDealAdds);
           // result.setRegTradeFees(regTradeFees);
        }
        return result;
    }

    private void insert(CfetsBondDealDbDto entity,OperationType operationType){
        RegFinProductDealTotal regFinProductDealTotal = entity.getRegFinProductDealTotal();
        String totleTradeId = regFinProductDealTotal.getTotleTradeId();
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
            regFinProductDealTotal.setTotleTradeId(StringUtil.join(totleTradeId,"R"));
            regFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            regFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            regFinProductDealTotal.setpTradeId(totleTradeId);
        }else{
            regFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            regFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        }
        regFinProductDealTotalMapper.insert(regFinProductDealTotal);
        entity.getRegFinProductDealTotalAdd().setTotleTradeId(regFinProductDealTotal.getTotleTradeId());
        regFinProductDealTotalAddMapper.insert(entity.getRegFinProductDealTotalAdd());
       /* List<RegTradeFeeTotal> regTradeFeeTotals = entity.getRegTradeFeeTotals();
        if(CollectionUtils.isNotEmpty(regTradeFeeTotals)){
            regTradeFeeTotals.forEach(item ->{
                item.setTradeId(regFinProductDealTotal.getTotleTradeId());
                regTradeFeeTotalMapper.insert(item);
            });
        }*/
        List<RegFinProductDeal> regFinProductDeals = entity.getRegFinProductDeals();
        if(CollectionUtils.isNotEmpty(regFinProductDeals)){
            regFinProductDeals.forEach(item ->{
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    String tradeId = item.getTradeId();
                    item.setTradeId(StringUtil.join(tradeId,"R"));
                    item.setTotleTradeId(StringUtil.join(totleTradeId,"R"));
                    item.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    item.setIsCancel(DictCodeList.是否_否.getValue());
                    item.setpTradeId(tradeId);
                }else{
                    item.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                    item.setIsCancel(DictCodeList.是否_否.getValue());
                }
                regFinProductDealMapper.insert(item);
            });
        }
        List<RegFinProductDealAdd> regFinProductDealAdds = entity.getRegFinProductDealAdds();
        if(CollectionUtils.isNotEmpty(regFinProductDealAdds)){
            regFinProductDealAdds.forEach(item ->{
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    item.setTradeId(StringUtil.join(item.getTradeId(),"R"));
                }
                regFinProductDealAddMapper.insert(item);
            });
        }
        /*List<RegTradeFee> regTradeFees = entity.getRegTradeFees();
        if(CollectionUtils.isNotEmpty(regTradeFees)){
            regTradeFees.forEach(item ->{
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    item.setTradeId(StringUtil.join(item.getTradeId(),"R"));
                }
                regTradeFeeMapper.insert(item);
            });
        }*/
        //更新原登记数据状态
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
            //汇总
            cfetsIbuQuoteBondMapper.updateRegFinProductDealTotal(totleTradeId,entity.getRemark());
            //明细
            cfetsIbuQuoteBondMapper.updateRegFinProductDeal(totleTradeId,entity.getRemark());
        }
    }
    /**
     * 插入业务流程表
     * @param entity
     */
    private void insertWfl(CfetsBondDealDbDto entity){
        String workflowId = entity.getWorkflowId();
        WflRegFinProductDealTotal wflRegFinProductDealTotal = new WflRegFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflRegFinProductDealTotal,entity.getRegFinProductDealTotal());
        wflRegFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflRegFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        wflRegFinProductDealTotal.setWorkflowId(workflowId);
        wflRegFinProductDealTotal.setCreateUser(LoginUserUtil.getUserName()==null?GlobalConstant.SYSTEM_USER:LoginUserUtil.getUserName());
        wflRegFinProductDealTotal.setUpdateUser(LoginUserUtil.getUserName()==null?GlobalConstant.SYSTEM_USER:LoginUserUtil.getUserName());
        wflRegFinProductDealTotal.setCreateTime(new Date());
        wflRegFinProductDealTotal.setUpdateTime(new Date());
        wflRegFinProductDealTotal.setCreateDept(LoginUserUtil.getDeptCode());
        wflRegFinProductDealTotalMapper.insert(wflRegFinProductDealTotal);
        WflRegFinProDealTotalAdd wflRegFinProDealTotalAdd = new WflRegFinProDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflRegFinProDealTotalAdd,entity.getRegFinProductDealTotalAdd());
        wflRegFinProDealTotalAdd.setWorkflowId(workflowId);
        wflRegFinProDealTotalAddMapper.insert(wflRegFinProDealTotalAdd);
       /* List<RegTradeFeeTotal> regTradeFeeTotals = entity.getRegTradeFeeTotals();
        if(CollectionUtils.isNotEmpty(regTradeFeeTotals)){
            regTradeFeeTotals.forEach(item ->{
                WflRegTradeFeeTotal wflRegTradeFeeTotal = new WflRegTradeFeeTotal();
                BeanUtil.copyPropertiesIgnoreNull(wflRegTradeFeeTotal,item);
                wflRegTradeFeeTotal.setWorkflowId(workflowId);
                wflRegTradeFeeTotalMapper.insert(wflRegTradeFeeTotal);
            });
        }*/
        List<RegFinProductDeal> regFinProductDeals = entity.getRegFinProductDeals();
        if(CollectionUtils.isNotEmpty(regFinProductDeals)){
            regFinProductDeals.forEach(item ->{
                WflRegFinProductDeal wflRegFinProductDeal = new WflRegFinProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflRegFinProductDeal,item);
                wflRegFinProductDeal.setWorkflowId(workflowId);
                wflRegFinProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                wflRegFinProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
                wflRegFinProductDealMapper.insert(wflRegFinProductDeal);
            });
        }
        List<RegFinProductDealAdd> regFinProductDealAdds = entity.getRegFinProductDealAdds();
        if(CollectionUtils.isNotEmpty(regFinProductDealAdds)){
            regFinProductDealAdds.forEach(item ->{
                WflRegFinProductDealAdd wflRegFinProductDealAdd = new WflRegFinProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflRegFinProductDealAdd,item);
                wflRegFinProductDealAdd.setWorkflowId(workflowId);
                wflRegFinProductDealAddMapper.insert(wflRegFinProductDealAdd);
            });
        }
       /* List<RegTradeFee> regTradeFees = entity.getRegTradeFees();
        if(CollectionUtils.isNotEmpty(regTradeFees)){
            regTradeFees.forEach(item ->{
                WflRegTradeFee wflRegTradeFee = new WflRegTradeFee();
                BeanUtil.copyPropertiesIgnoreNull(wflRegTradeFee,item);
                wflRegTradeFee.setWorkflowId(workflowId);
                wflRegTradeFeeMapper.insert(wflRegTradeFee);
            });
        }*/
    }

    /**
     * 删除业务流程数据
     * @param workflowId
     */
    private void deleteBussFlowData(String workflowId) {
        wflRegFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        wflRegFinProDealTotalAddMapper.deleteByWorkflowId(workflowId);
        wflRegTradeFeeTotalMapper.deleteByWorkflowId(workflowId);
        wflRegFinProductDealMapper.deleteByWorkflowId(workflowId);
        wflRegFinProductDealAddMapper.deleteByWorkflowId(workflowId);
        wflRegTradeFeeMapper.deleteByWorkflowId(workflowId);
    }

    /**
     * 处理头寸
     * @param entity
     * @return
     */
    private Map<String,Object> preparePosition(CfetsBondDealDbDto entity,OperationType operationType){
        Map<String,Object> result = new HashMap<>();
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
            result.put("interfaceType","Bond001");
            result.put("totalTradeId",entity.getRegFinProductDealTotal().getTotleTradeId());
            result.put("modelType",DictCodeListExt1.子系统代码_交易.getValue());
            result.put("userId", SecurityUtil.getLoginUser().getUserName());
        }else{
            String ps = entity.getRegFinProductDealTotal().getPs();
            result.put("interfaceType","Bond001");
            result.put("userId", SecurityUtil.getLoginUser().getUserName());
            result.put("totalTradeId",entity.getRegFinProductDealTotal().getTotleTradeId());
            result.put("flowStatus",DictCodeListExt3.流程节点_流程提交.getValue());
            result.put("modelType",DictCodeListExt1.子系统代码_交易.getValue());
            String balanceCheck="Y";
            if(StringUtil.isNotEmpty(entity.getSoftSkipMode()) && StringUtil.equals(entity.getSoftSkipMode(),"Y")){
                balanceCheck="N";
            }
            result.put("balanceCheck",balanceCheck);
            result.put("busiType",DictCodeListExt3.头寸业务类型_债券行权交易.getValue());
            result.put("busiAct",DictCodeListExt3.业务动作_交易登记.getValue());
            result.put("ps",ps);
            result.put("settleSpeed",entity.getRegFinProductDealTotal().getSettleSpeed());
            result.put("tradePlace",BussinessDictCodeList.交易场所_银行间.getValue());
            result.put("systemAct",DictCodeListExt3.系统动作_新建.getValue());
            result.put("cdate",DateUtil.format(entity.getRegFinProductDealTotal().getTradeDate()));
            result.put("ccy",entity.getRegFinProductDealTotal().getCcy());
            result.put("finprodId",entity.getRegFinProductDealTotal().getFinprodId());
            if(CollectionUtils.isNotEmpty(entity.getRegFinProductDeals())){
                List<Map<String,Object>> ptlList = new ArrayList<>();
                Map<String,List<RegFinProductDealAdd>> regFinProductDealAddMap = new HashMap<>();
                if(CollectionUtils.isNotEmpty(entity.getRegFinProductDealAdds())){
                    entity.getRegFinProductDealAdds().stream().collect(Collectors.groupingBy(RegFinProductDealAdd::getTradeId,Collectors.toList()))
                            .forEach(regFinProductDealAddMap::put);
                }
                for (RegFinProductDeal regFinProductDeal : entity.getRegFinProductDeals()) {
                    Map<String,Object> pltMap = new HashMap<>();
                    String portfolioId = regFinProductDeal.getPortfolioId();
                    pltMap.put("portfolioId", portfolioId);
                    pltMap.put("tradeId",regFinProductDeal.getTradeId());
                    pltMap.put("etrId",regFinProductDeal.getEntrustId());
                    pltMap.put("cashAcctId",cfetsIbuQuoteBondMapper.getACCTNO(portfolioId,BussinessDictCodeList.资金账户类型_托管账户.getValue()));
                    pltMap.put("secAcctId",cfetsIbuQuoteBondMapper.getSecAcctId(portfolioId,regFinProductDeal.getFinprodId(),regFinProductDeal.getTradeMarket()));
                    pltMap.put("secAmount",ArithmeticUtil.multiply(regFinProductDeal.getShareAmt(),new BigDecimal(100))); //券面总额
                    pltMap.put("cashAmount",regFinProductDeal.getTradeAmt());
                    RegFinProductDealAdd regFinProductDealAdd = regFinProductDealAddMap.get(regFinProductDeal.getTradeId()).get(0);
                    pltMap.put("integrateSecAmount",ArithmeticUtil.multiply(regFinProductDealAdd.getLendinShareAmt()==null?BigDecimal.ZERO:regFinProductDealAdd.getLendinShareAmt(),new BigDecimal(100)));
                    pltMap.put("ownSecAmount",ArithmeticUtil.multiply(regFinProductDealAdd.getOurShareAmt()==null?BigDecimal.ZERO:regFinProductDealAdd.getOurShareAmt(),new BigDecimal(100)));
                    ptlList.add(pltMap);
                }
                result.put("ptlList",ptlList);
            }
        }
        return result;
    }

    /**
     * 查询该资产对应的组合分配列表
     * @return
     */
    public List<PtlAllocationDto> queryCfetsBussCombDisList(String secId) {
        if(!StringUtil.isNotEmpty(secId)){
            return null;
        }
        Map<String, Object> param =new HashMap<>();
        param.put("platDate",DateUtil.platDateFromCache());
        param.put("A_001", DictCodeListExt3.资产类型_资产账户持仓.getValue());
        param.put("finprodId",secId);
        return cfetsTrdRedeemMapper.queryCfetsBussCombDisList(param);
    }
    /**
     * 当日登记撤销检查
     * @param cfetsBondRegCheckRequst
     * @return
     */
    public AjaxResult checkDayRegRevokeCheck(CfetsTrdRedeemCheckRequst cfetsBondRegCheckRequst){
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        //是否存在流程中数据
         cfetsBondRegCheckRequst.getCfetsBondRegCheckDetaileRequsts().forEach(item ->{
           if(!StringUtil.equals(item.getEffectiveStatus(),BussinessDictCodeList.有效状态_有效.getValue())){
                errorList.add(new ValidMessage("非有效状态数据不支持撤销!业务编号:"+item.getBusiCode()));
            }
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setPageId("CfetsTrdRedeem001");
            wflValidInputParameter.setBusinessDataPk1(item.getBusiCode());
            if(null != mstWorkflowCommonService &&
                    mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)){
                errorList.add(
                        new ValidMessage(this.getMessage("COMMON_E_0012",item.getBusiCode())));
            }
            //赎回登记数据，在理论行权日前一交易日（包含前一交易日）之前均支持赎回登记撤销操作，支持隔夜撤销。理论行权日之后（包含理论行权日）赎回登记不支持手动撤销
            if(StringUtil.equals(item.getPs(), com.joyintech.tams.constant.DictCodeList.交易方向_行权_赎回.getValue())){
                RegFinProductDealTotal regFinProductDealTotal = regFinProductDealTotalMapper.selectByPrimaryKey(item.getBusiCode());
                if(iPlatformDate.getSysDate().compareTo(item.getExerciseDate())>=0){
                    errorList.add(new ValidMessage("理论行权日之后(包含理论行权日)赎回登记不支持手动撤销!业务编号:"+item.getBusiCode()));
                }
                //赎回登记数据只能是本人进行撤销
                if(!StringUtil.equals(regFinProductDealTotal.getOuTraderId(),SecurityUtil.getLoginUser().getUserName())){
                    errorList.add(new ValidMessage("赎回登记数据只能是本人进行撤销!业务编号:"+item.getBusiCode()));
                }
            }
            if(CollectionUtils.isNotEmpty(errorList)){
                return;
            }
        });
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

}
