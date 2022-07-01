package com.joyintech.tams.invest.trade.option.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseFinProductCashInputDto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;
import com.joyintech.tams.invest.trade.option.dto.TrdOptionTrade002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 期权交易新增service
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
public class TrdOptionTrade002Service extends BaseService<TrdOptionTrade002Dto> implements BaseProcessService<TrdOptionTrade002Dto> {

    @Autowired
    private CommonBusinessService commonBusinessService;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    @Autowired
    private DateCalService dateCalService;

    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    /**
     * 划款指令
     */
    @Autowired(required = false)
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;
    
    @Autowired(required = false)
    private StlPayOrdersMapper stlPayOrdersMapper;
    /**
     * 金融产品交易汇总业务流程表
     */
    @Autowired(required = false)
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;
    /**
     * 金融产品交易汇总附属业务流程表
     */
    @Autowired(required = false)
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;
    /**
     * 金融产品交易业务流程表
     */
    @Autowired(required = false)
    private WflTrdProductDealMapper wflTrdProductDealMapper;
    /**
     * 金融产品交易附属业务流程表
     */
    @Autowired(required = false)
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;
    /**
     * 金融产品交易汇总扩展业务流程表
     */
    @Autowired(required = false)
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;
    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    @Autowired(required = false)
    private PtlTradeMapper ptlTradeMapper;

    /**
     * 利率结构明细
     */
    @Autowired
    private FinRateInfoStructureMapper finRateInfoStructureMapper;

    /**
     * 行权规则
     */
    @Autowired
    private FinExerciseRuleMapper finExerciseRuleMapper;

    /**
     * 行权计划
     */
    @Autowired
    private FinExercisePlanMapper finExercisePlanMapper;

    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候检查service
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;
    /**
     * 金融产品分类
     */
    @Autowired(required = false)
    private FinProductTypeMapper finProductTypeMapper;
    /**
     * 交易类金融产品
     */
    @Autowired(required = false)
    private FinTradeProductMapper finTradeProductMapper;
    /**
     * 交易类金融产品附属
     */
    @Autowired(required = false)
    private FinTradeProductAddMapper finTradeProductAddMapper;
    /**
     * 金融交易产品汇总mapper
     */
    @Autowired(required = false)
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
    /**
     * 金融交易产品汇总附属mapper
     */
    @Autowired(required = false)
    private TrdDealTotalAddMapper trdDealTotalAddMapper;
    /**
     * 金融交易产品汇总扩展mapper
     */
    @Autowired(required = false)
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;
    /**
     * 投管接口共通组件
     * add by cxq 20200212
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    /**
     *
     */
    @Autowired
    private TrdProductDealMapper trdProductDealMapper;
    /**
    *
    */
   @Autowired
   private TrdProductDealAddMapper trdProductDealAddMapper;
    /**
     * 投资委托信息Service
     */
    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;
    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。 {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.MASERT)
    public String checkForSubmit(FlowEntity entity, boolean isMutiApproval) {
        if (isMutiApproval) {
            // 业务编号
            String tradeId = entity.getTradeId();
            if (!multiApproveTradeCheckService.multiApproveTradeCheck(tradeId)) {
                return getMessage("BUSS_COM_E_0100", tradeId);
            }
        }
        return null;
    }


    /**
     * 检查到期日是否为节假日 到期日如果是节假日 要提醒
     *
     * @param inputDto 画面输入参数
     * @param result valid结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    public AjaxResult checkMdate(TrdOptionTrade002Dto inputDto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return null;
        }

        if (inputDto.getFinTradeProduct() == null || inputDto.getFinTradeProduct().getMdate() == null
                || StringUtil.isBlank(inputDto.getFinTradeProduct().getCalendarId())) {
            return null;
        }

        boolean isDealCalendarHoliday = dateCalService.isDealCalendarHoliday(inputDto.getFinTradeProduct().getMdate(),
                inputDto.getFinTradeProduct().getCalendarId());
        if (isDealCalendarHoliday) {
            AjaxResult wr = new AjaxResult();
            wr.setData(getMessage("BUSS_COM_E_0039", getMessage("TrdOptionTrade001.mdate")));
            return wr;
        }

        return null;
    }

    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(TrdOptionTrade002Dto entity) {
        if (CollectionUtils.isNotEmpty(entity.getFileArray()) && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getFileArray().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getTotleTradeId());
        }

        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        this.prepareEntity(entity);

        // 金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, entity);
        wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflTrdFinProductDealTotal.setPortfolioId(entity.getActPortfolioId());
        wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);

        // 金融产品交易汇总附属Entity -> 金融产品交易附属业务流程表
        WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, entity.getTrdDealTotalAdd());
        wflTrdDealTotalAdd.setEntrustId(entity.getEntrustId());//委托编号
        wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflTrdDealTotalAdd.setContractType(entity.getContractType());//合约类型
        wflTrdDealTotalAdd.setTargetId(entity.getTargetId());//合约标的
        wflTrdDealTotalAdd.setExerciseType(entity.getExerciseType());//行权规则
        wflTrdDealTotalAdd.setTerm(entity.getTerm());//期限
        wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);

        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity.getTrdProductDeal());
        wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        // 一体化对应etliu start
        wflTrdProductDeal.setIntAmt(null);
        // 一体化对应etliu end

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDealAdd());
        wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, entity.getPaTrdFinProductDealTotal());
        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

//        转换
        wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
        wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, entity.getBussTrdTransferOrder().get(0));
            wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
        }

        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDto.setTrdProductDeal(entity.getTrdProductDeal());
        baseTradeProductDealDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(entity.getBussTrdTransferOrder().get(0)));
        }
        //        委托代码不为空处理委托代码
        if(StringUtil.isNotEmpty(entity.getEntrustId())){
            baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
        }
        finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));

        // 撤销时，子组合需要调估值共通
        if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                finProductTrade.setActPortfolioId(entity.getActPortfolioId());
            });
        }
        baseTradeProductDealCommService.execute(finProductTradeDto);

        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
        }
    }

    /**
     * 基础dto信息整备
     * @param dto
     */
    public void prepareEntity(TrdOptionTrade002Dto dto) {
        dto.setOptFee(dto.getOptFee() == null ? new BigDecimal(0) : dto.getOptFee());//设置期权费为null时存0
        // 金融产品交易汇总Entity 构造开始----------------------------------------------------------
        dto.setFinprodId(dto.getTotleTradeId()); // 金融产品代码
        // 金融产品类型（估值核算）
        dto.setFinprodType(DictCodeListExt3.金融产品类型_投管用_期权.getValue());
        // 金融产品类型（投管分类）
        dto.setFinprodType2(DictCodeListExt3.金融产品类型_投管用_期权.getValue());
        // 金融产品类型（估值核算）
        dto.setBusiType(DictCodeListExt3.业务类型_场外期权.getValue());
        dto.setShareAmt(dto.getAmount());//交易份额
        dto.setFpriceAmt(dto.getOptFee());//全价总额-->期权费
        dto.setTradeMarket(DictCodeListExt1.交易场所_OTC场外.getValue());//交易场所
        dto.setTradeAmt(dto.getOptFee());//交易金额
        dto.setSettleDate(dto.getPayDate());//交割日
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);
        dto.setBranch(branchLong);
        dto.setIsClean(DictCodeList.是否_是.getValue()); // 是否结清
        dto.setIsCancel(DictCodeList.是否_否.getValue());
        if(StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dto.getPs())){
            dto.setTgBusiType(DictCodeListExt3.平安业务类型_期权买入.getValue());
        }else{
            dto.setTgBusiType(DictCodeListExt3.平安业务类型_期权卖出.getValue());
        }

        TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
        if (personInfo != null) {
            dto.setTrader(personInfo.getTrader()); // 本方交易员
            dto.setCounterId(personInfo.getCounterId()); // 交易对手
            dto.setIsOutTrade(personInfo.getIsOutTrade()); // 是否外部交易
            dto.setCounterTrader(personInfo.getCounterTrader()); // 对手方交易员
            dto.setCounterContact(personInfo.getContactInfo()); // 联系方式
        }
        // 金融产品交易Entity 构造结束----------------------------------------------------------

        // 金融产品交易汇总附属表Entity 构造开始----------------------------------------------------------
        //dto包含委托代码
        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
        trdDealTotalAdd.setEntrustId(dto.getEntrustId()); // 委托代码
        trdDealTotalAdd.setTotleTradeId(dto.getTotleTradeId()); // 汇总交易编号
        trdDealTotalAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
        trdDealTotalAdd.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setTrdDealTotalAdd(trdDealTotalAdd);
        // 金融产品交易汇总附属表Entity 构造结束----------------------------------------------------------

        // 金融产品交易汇总扩展Entity 构造开始----------------------------------------------------------
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        paTrdFinProductDealTotal.setTotleTradeId(dto.getTotleTradeId()); // 汇总交易编号
        paTrdFinProductDealTotal.setPaBussType(dto.getTgBusiType());
        paTrdFinProductDealTotal.setDeptCode(personInfo.getTraderDept()); // 交易所属部门
        paTrdFinProductDealTotal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------

        // 金融产品交易表Entity 构造开始----------------------------------------------------------
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(dto.getTradeId()); // 交易编号
        trdProductDeal.setTotleTradeId(dto.getTotleTradeId()); // 汇总交易编号
        trdProductDeal.setFinprodId(dto.getTotleTradeId()); // 金融产品代码
        trdProductDeal.setFinprodType2(DictCodeListExt3.金融产品类型_投管用_期权.getValue());
        trdProductDeal.setFinprodType(DictCodeListExt3.金融产品类型_期权.getValue());
        trdProductDeal.setBusiType(DictCodeListExt3.业务类型_场外期权.getValue()); // 业务类型
        trdProductDeal.setBranch(branchLong);//序号
        trdProductDeal.setCcy(dto.getCcy());//币种
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());//交易状态
        trdProductDeal.setVdate(dto.getVdate());//起息日
        trdProductDeal.setMdate(dto.getMdate());//到期日
        trdProductDeal.setTradeDate(dto.getTradeDate());//交易日期
        trdProductDeal.setSettleDate(dto.getPayDate());//交割日
        trdProductDeal.setPayDate(dto.getPayDate());//支付日期
//        2020/02/23 首期交易的交易表交易方向只有买入  画面对应的交易方向为交易类金融产品存值 start
        trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());//交易方向
//        2020/02/23  end
        trdProductDeal.setFpriceAmt(dto.getOptFee());//全价总额
        trdProductDeal.setTradeAmt(dto.getOptFee());//交易金额
        trdProductDeal.setShareAmt(dto.getAmount());//数量
        trdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        trdProductDeal.setTgBusiType(dto.getTgBusiType());
        dto.setTrdProductDeal(trdProductDeal);
        // 金融产品交易表Entity 构造结束----------------------------------------------------------

        // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
        //dto包含委托代码
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(dto.getTradeId()); // 交易编号
        trdProductDealAdd.setEntrustId(dto.getEntrustId());//委托编号
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
        trdProductDealAdd.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId());//组合代码
        dto.setTrdProductDealAdd(trdProductDealAdd);
        // 金融产品交易附属表Entity 构造结束----------------------------------------------------------

        // 组合交易表Entity 构造开始----------------------------------------------------------
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setBusiNo(dto.getTradeId()); // 业务编号
        ptlTrade.setPortfolioId(dto.getPortfolioId()); // 组合代码
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
        ptlTrade.setBusiId(dto.getTotleTradeId()); // 业务明细代码
        ptlTrade.setBusiType(DictCodeListExt3.共通业务类型_期权交易.getValue()); // 业务类型
        ptlTrade.setSettleDate(dto.getPayDate()); // 实际发生日
        ptlTrade.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setPtlTrade(ptlTrade);
        // 组合交易表Entity 构造结束----------------------------------------------------------

        // 划款指令表Entity 构造开始----------------------------------------------------------
        if (bussTrdTransferOrderService != null) {
            if (StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                // 后台自动生成划款指令
                BussSettleOrderParameterDto orderParameterDto = new BussSettleOrderParameterDto();
                orderParameterDto.setIsTotalTrade(DictCodeList.是否_是.getValue());
                orderParameterDto.setPortfolioId(dto.getPortfolioId());

                TrdProductDeal orderParameterDeal = new TrdProductDeal();
                orderParameterDeal.setFinprodType2(DictCodeListExt3.金融产品类型_投管用_期权.getValue());
                orderParameterDeal.setTradeDate(dto.getTradeDate());
                orderParameterDeal.setPs(dto.getPs());
                orderParameterDeal.setCcy(dto.getFinTradeProduct().getCcy());
                orderParameterDeal.setTradeAmt(dto.getFinTradeProduct().getPrin());
                orderParameterDeal.setCounterId(personInfo==null?null:personInfo.getCounterId());

                orderParameterDto.setDeal(orderParameterDeal);

                BussTrdTransferOrderDto resultOrderDto = bussTrdTransferOrderService.generateOrder(orderParameterDto);
                if (resultOrderDto != null) {
                    dto.setBussTrdTransferOrder(Arrays.asList(resultOrderDto));
                }
            }

            if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getTotleTradeId()); // 交易编号
                dto.getBussTrdTransferOrder().get(0).setOrderStatus(DictCodeListExt1.指令状态_新建.getValue()); // 指令状态
                dto.getBussTrdTransferOrder().get(0).setWorkflowId(dto.getWorkflowId()); // 工作流代码
                dto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            }
        }
        // 划款指令表Entity 构造结束----------------------------------------------------------

        // 交易类金融产品Entity 构造开始----------------------------------------------------------
        dto.getFinTradeProduct().setFinprodId(dto.getTotleTradeId()); // 金融产品代码
        dto.getFinTradeProduct().setBranch(branchLong); // 分支序号
        dto.getFinTradeProduct().setFinprodType(DictCodeListExt3.金融产品类型_期权.getValue()); // 金融产品类型（投管分类）
        dto.getFinTradeProduct().setFinprodType2(DictCodeListExt3.金融产品类型_投管用_期权.getValue()); // 金融产品类型（投管分类）
        dto.getFinTradeProduct().setIntType(DictCodeListExt3.利率类型_结构性.getValue()); // 利率类型
        dto.getFinTradeProduct().setTradeMarket(DictCodeListExt1.交易场所_OTC场外.getValue());//交易场所
        dto.getFinTradeProduct().setCounterId(dto.getCounterId()); // 交易对手
        // 利息总额
        dto.getFinTradeProduct().setmPrinAmt(dto.getAmount());
        dto.getFinTradeProduct().setmTradeAmt(ArithmeticUtil.doAdd(dto.getPrinAmt(), dto.getIntAmt()));
        dto.getFinTradeProduct().setVdate(dto.getVdate());//起息日
        dto.getFinTradeProduct().setMdate(dto.getMdate());//到期日 = 起息日 + 期限 按交易日历的节假日顺延
        dto.getFinTradeProduct().setExcPs(dto.getPs());//外汇交易方向
        dto.getFinTradeProduct().setPrin(dto.getAmount());//名义本金
        dto.getFinTradeProduct().setCcy(dto.getCcy());//本金币种
        dto.getFinTradeProduct().setTerm(dto.getTerm());//期限(天)
        dto.getFinTradeProduct().setCalendarId(dto.getCalendarId());//交易日历
        dto.getFinTradeProduct().setHoldPurpose(dto.getHoldPurpose());//持有方式 中债新增 20200409
        dto.getFinTradeProductAdd().setFinprodId(dto.getTotleTradeId());//交易类金融产品附属金融产品代码
        dto.getFinTradeProductAdd().setBranch(branchLong);//交易类金融产品附属分支序号
        dto.getFinTradeProductAdd().setOptFeePayType(dto.getOptFeePayType());//期权费类型
        // 金融产品类型（估值核算）
        dto.getFinTradeProductAdd().setFinprodType(DictCodeListExt3.金融产品类型_期权.getValue());
        // 金融产品类型（投管分类）
        dto.getFinTradeProductAdd().setFinprodType2(DictCodeListExt3.金融产品类型_投管用_期权.getValue());
        dto.getFinTradeProductAdd().setOptFeeAmt(dto.getOptFee());//期权费
        // 交易类金融产品Entity 构造结束----------------------------------------------------------

        // 金融产品分类Entity 构造开始----------------------------------------------------------
        FinProductType finProductType = new FinProductType();
        finProductType.setFinprodId(dto.getTotleTradeId()); // 金融产品代码
        finProductType.setBranch(branchLong); // 分支序号
        //根据标的编号查询标的类型
        String targetType = restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                        + "/TrdOptionTrade/selectTargetType/" + dto.getTargetId(), null, String.class);
        finProductType.setType6(targetType); //标的类型
        finProductType.setType8(DictCodeListExt3.平安资产类型_期权.getValue());
        finProductType.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setFinProductType(finProductType);
        // 金融产品分类Entity 构造结束----------------------------------------------------------

        // 利率结构明细Entity 构造开始----------------------------------------------------------
        dto.getFinRateInfoStructure().setEffDate(dto.getVdate());//生效日
        dto.getFinRateInfoStructure().setStructureType(dto.getContractType());//结构类型
        dto.getFinRateInfoStructure().setExecBaseDate(dto.getExerciseDate());//行权价基准日
        //dto中包含高障碍价、低障碍价、高行权价、低行权价、执行价、敲出价
        // 利率结构明细Entity 构造结束----------------------------------------------------------

        // 行权规则Entity 构造开始----------------------------------------------------------
        FinExerciseRule finExerciseRule = new FinExerciseRule();
        finExerciseRule.setFinprodId(dto.getTotleTradeId());//金融产品代码
        finExerciseRule.setBranch(branchLong);//分支序号
        finExerciseRule.setFinprodType(DictCodeListExt3.金融产品类型_投管用_期权.getValue());//金融产品类型（投管分类）
        finExerciseRule.setExerciseType(dto.getExerciseType());
        dto.setFinExerciseRule(finExerciseRule);
        // 行权规则Entity 构造结束----------------------------------------------------------

        // 行权计划Entity 构造开始----------------------------------------------------------
        String exerciseType = dto.getExerciseType();
        FinExercisePlan finExercisePlan = new FinExercisePlan();
        finExercisePlan.setExerciseType(exerciseType);//行权方式
        finExercisePlan.setBranch(branchLong);//分支序号
        finExercisePlan.setFinprodId(dto.getTotleTradeId());//金融产品代码
        // 申请开始日
        if(StringUtil.equals(DictCodeListExt3.行权方式_欧式.getValue(), exerciseType)){
            finExercisePlan.setVdate(dto.getMdate());
        }
        else if(StringUtil.equals(DictCodeListExt3.行权方式_美式.getValue(), exerciseType)){
            finExercisePlan.setVdate(dto.getVdate());
        }
        finExercisePlan.setMdate(dto.getMdate());//申请结束日
        finExercisePlan.setExerciseDate(dto.getMdate());//计划行权日
        finExercisePlan.setActExerciseDate(dto.getMdate());//实际行权日
        dto.setFinExercisePlan(finExercisePlan);
        // 行权计划Entity 构造结束----------------------------------------------------------
    }

    @Override
    public void doComplete(TrdOptionTrade002Dto entity, OperationType operationType) {
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
                this.prepareEntity(entity);
                // 金融产品交易汇总Entity -> 金融产品交易汇总表
                TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
                BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, entity);
                trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                trdFinProductDealTotal.setPs(DictCodeList.交易方向_买入.getValue());//交易方向
                trdFinProductDealTotal.setPortfolioId(entity.getActPortfolioId());//组合代码

                // 金融产品交易汇总附属表Entity -> 金融产品交易汇总附属表
                TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
                BeanUtil.copyPropertiesIgnoreNull(trdDealTotalAdd, entity.getTrdDealTotalAdd());

                // 金融产品交易表Entity -> 金融产品交易表
                TrdProductDeal trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity.getTrdProductDeal());
                trdProductDealMapper.insertSelective(trdProductDeal);
                trdProductDeal.setPs(entity.getPs());
                // 金融产品交易附属表Entity -> 金融产品交易附属表
                TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, entity.getTrdProductDealAdd());
                trdProductDealAddMapper.insertSelective(trdProductDealAdd);
                
                // 写入资产信息进交易类金融产品表
                FinTradeProduct finTradeProduct = entity.getFinTradeProduct();
                FinTradeProductAdd finTradeProductAdd = entity.getFinTradeProductAdd();
                // 写入合约类型进金融产品分类表
                FinProductType finProductType = entity.getFinProductType();
                FinProduct finProduct = new FinProduct();
                finProduct.setFinprodType2(DictCodeListExt3.金融产品类型_投管用_期权.getValue());
                finProduct.setTradeMarket(DictCodeListExt1.交易场所_OTC场外.getValue());
                ValItemValueGenUtil.genTypeForFinProductType(finProduct, finProductType);

                finProductTypeMapper.insertSelective(finProductType);
                finTradeProductMapper.insertSelective(finTradeProduct);
                //交易类金融产品附属表数据入库库
                finTradeProductAddMapper.insertSelective(finTradeProductAdd);
                // 批量插入
                List<PtlTrade> ptlTradeList = new ArrayList<>();

                // 组合交易表Entity -> 组合交易信息表
                PtlTrade ptlTrade = entity.getPtlTrade();
                ptlTradeList.add(ptlTrade);
                ptlTradeMapper.insertSelective(ptlTrade);

                if (StringUtil.isNotEmpty(entity.getActPortfolioId())
                        && !StringUtil.equals(entity.getPortfolioId(), entity.getActPortfolioId())) {
                    PtlTrade ptlTrade1 = new PtlTrade();
                    BeanUtil.copyPropertiesIgnoreNull(ptlTrade1, ptlTrade);
                    ptlTrade1.setPortfolioId(entity.getActPortfolioId());
                    ptlTradeMapper.insertSelective(ptlTrade1);
                    ptlTradeList.add(ptlTrade1);
                }
                // 金融产品交易汇总信息DTO.划款指令表Entity -> 划款指令表
                StlPayOrdersDto stlPayOrders = null;
                if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
                    stlPayOrders = new StlPayOrdersDto();
                    BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, entity.getBussTrdTransferOrder().get(0));
                }
                
                FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                finProductTradeDto.setNeedSum(true);
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
                baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
                baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
                // 划款指令
                if (stlPayOrders != null) {
                    baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrders));
                }
                finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));

                // 委托代码不为空处理委托代码
                if (StringUtil.isNotEmpty(entity.getEntrustId())) {
                    baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
                }

                // 撤销时，子组合需要调估值共通
                if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                        finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                    });
                }
                // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
                // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
                baseTradeProductDealCommService.execute(finProductTradeDto);
                
                for (BussTrdTransferOrderDto dto : entity.getBussTrdTransferOrder()) {
                    BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, dto);
                    if (stlPayOrders != null){
                        stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
                        stlPayOrders.setTradeId(entity.getTotleTradeId());
                        stlPayOrders.setWorkflowId(entity.getWorkflowId());
                    }
                    stlPayOrdersMapper.insertSelective(stlPayOrders);
                }
                if (StringUtil.isNotEmpty(entity.getActPortfolioId())
                        && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                                    entity.getActPortfolioId()));
                    baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                                    entity.getPortfolioId()));
                }

                // 7.2删除原业务流程表中的交易记录
                String workflowId = entity.getWorkflowId();
                if (StringUtil.isBlank(workflowId)) {
                    WflTrdFinProductDealTotalKey wflTrdFinProductDealTotalKey = new WflTrdFinProductDealTotalKey();
                    wflTrdFinProductDealTotalKey.setTotleTradeId(entity.getTotleTradeId());
                    wflTrdFinProductDealTotalKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    WflTrdFinProductDealTotal wflTrdFinProductDealTotal = bussCommonMapperDelegateService.selectByMultiPrimaryKey(
                            WflTrdFinProductDealTotalMapper.class, wflTrdFinProductDealTotalKey, WflTrdFinProductDealTotal.class);

                    if (wflTrdFinProductDealTotal != null) {
                        workflowId = wflTrdFinProductDealTotal.getWorkflowId();
                    }
                }

                // 写入现金流相关信息表
                this.cashFlowLogic(entity);

                // 插入风险试算结果
                //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTotleTradeId());

                // 所有交易生效后调用的共通方法
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(entity.getPortfolioId());
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                inputDto.setTrdProductDeal(trdProductDeal);
                inputDto.setTrdProductDealAdd(trdProductDealAdd);
                if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    inputDto.setActPortfolioId(entity.getActPortfolioId());
                }
                inputDto.setPtlTradeList(ptlTradeList);
                if (stlPayOrders != null) {
                    inputDto.setStlPayOrders(stlPayOrders);
                }
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

                trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
                trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
                paTrdFinProductDealTotalMapper.insertSelective(entity.getPaTrdFinProductDealTotal());

                // 金融产品交易扩展表Entity -> 金融产品交易扩展表
                PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
                BeanUtil.copyPropertiesIgnoreNull(paTrdFinProductDealTotal, entity.getPaTrdFinProductDealTotal());

                // 调用投管接口
                InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                investInterfCommDto.setPageId("TrdOptionTrade002");
                investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
                investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
                investInterfCommDto.setTrdProductDeal(trdProductDeal);
                investInterfCommDto.setTrdProductDealAdd(trdProductDealAdd);
                investInterfCommDto.setFinTradeProductAdd(finTradeProductAdd);
                investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
                investInterfCommDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
                investInterfCommDto.setExercisePlan(entity.getFinExercisePlan());
                investInterfCommDto.setFinRateInfoStructure(entity.getFinRateInfoStructure());
                investInterfCommService.doInvestInterf(investInterfCommDto);
                // 调用投管接口

                this.deleteBussFlowData(workflowId);
            }
            // 撤销
            else {
                this.doCompleteRevoke(entity);
            }
            // 调接口
            try {
                List<String> tradeIds = new ArrayList<String>();
                tradeIds.add(entity.getTradeId());
                restTemplate.postForObject(
                        ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
                        Map.class);
            }catch(Exception e) {

        }
            // 调接口
    }

    /**
     * 撤销doComplete
     *
     * @param entity 参数
     * @see
     * @since 1.0
     */
    private void doCompleteRevoke(TrdOptionTrade002Dto entity) {
        // 调用基础组合.【查询金融产品交易汇总信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealTotalDtoList = restTemplate.getForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTotelTradeInfo?tradeId=" + entity.getTotleTradeId(),
                BaseTradeProductDealDto.class);

        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        if (!StringUtil.isEmptyList(baseTradeProductDealTotalDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealTotalDtoList) {
                baseDto.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                baseDto.getTrdProductDeal().setPs(entity.getPs());
                baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                baseDto.getTrdProductDeal().setCancelRemark(entity.getRevokeReason());
                if (bussCommonMapperDelegateService != null) {
                    //更新金融产品交易汇总表有效数据并插入一条撤销数据
                    TrdFinProductDealTotal trdFinProductDealTotal =  new TrdFinProductDealTotal();
                    trdFinProductDealTotal.setIsCancel(DictCodeList.是否_是.getValue());
                    trdFinProductDealTotal.setTotleTradeId(entity.getTotleTradeId());
                    trdFinProductDealTotalMapper.updateByPrimaryKeySelective(trdFinProductDealTotal);

                    baseDto.getTrdFinProductDealTotal().setTotleTradeId(entity.getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    baseDto.getTrdFinProductDealTotal().setIsCancel(DictCodeList.是否_否.getValue());
                    baseDto.getTrdFinProductDealTotal().setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    trdFinProductDealTotalMapper.insertSelective(baseDto.getTrdFinProductDealTotal());
                    TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
                    trdDealTotalAdd.setEntrustId(entity.getEntrustId());
                    trdDealTotalAdd.setTotleTradeId(baseDto.getTrdFinProductDealTotal().getTotleTradeId());
                    trdDealTotalAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
                    trdDealTotalAdd.setWorkflowId(entity.getWorkflowId()); // 工作流代码
                    trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);

                    //设置金融产品交易表的交易状态
                    baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());

                    PtlTrade ptlTrade = new PtlTrade();
                    ptlTrade.setPortfolioId(entity.getPortfolioId());
                    ptlTrade.setBusiNo(baseDto.getTrdProductDeal().getTradeId());
                    ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                    ptlTrade = bussCommonMapperDelegateService.selectByMultiPrimaryKey(PtlTradeMapper.class, ptlTrade,
                            PtlTrade.class);
                    ptlTrade.setBusiNo(ptlTrade.getBusiNo() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    ptlTradeMapper.insertSelective(ptlTrade);
                    if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                        PtlTrade actPtlTrade = new PtlTrade();
                        BeanUtil.copyPropertiesIgnoreNull(actPtlTrade, ptlTrade);
                        actPtlTrade.setPortfolioId(entity.getActPortfolioId());
                        actPtlTrade.setBusiNo(baseDto.getTrdProductDeal().getTradeId());
                        actPtlTrade = bussCommonMapperDelegateService.selectByMultiPrimaryKey(PtlTradeMapper.class, actPtlTrade,
                                PtlTrade.class);
                        actPtlTrade.setBusiNo(ptlTrade.getBusiNo());
                        ptlTradeMapper.insertSelective(actPtlTrade);
                    }
                }
                // 调用投管接口
                InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                investInterfCommDto.setPageId("TrdOptionTrade002");
                investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
                investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
                investInterfCommDto.setRevokeReason(entity.getRevokeReason());
                investInterfCommDto.setTrdProductDeal(baseDto.getTrdProductDeal());
                investInterfCommDto.setTrdProductDealAdd(baseDto.getTrdProductDealAdd());
                TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
                BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, entity);
                investInterfCommDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
                if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
                    investInterfCommDto.setStlPayOrders(
                    		baseDto.getStlPayOrdersList().get(0));
                }
                investInterfCommDto.setTradeFeeDetaillist(
                		baseDto.getTrdTradeFeeDetailList());
                investInterfCommDto.setExercisePlan(entity.getFinExercisePlan());
                investInterfCommDto.setFinRateInfoStructure(entity.getFinRateInfoStructure());
                investInterfCommService.doInvestInterf(investInterfCommDto);
                // 调用投管接口

            }
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealTotalDtoList);

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommService.execute(finProductTradeDto);

            if (StringUtil.isNotEmpty(entity.getActPortfolioId())
                    && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                                entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                                entity.getPortfolioId()));
            }
        }

        // 删除原业务流程记录
        this.deleteBussFlowData(entity.getWorkflowId());

        // 插入风险试算结果
        //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTotleTradeId());

    }

    /**
     * 现金流相关逻辑
     *
     * @param dto 参数
     * @see
     * @since 1.0
     */
    private void cashFlowLogic(TrdOptionTrade002Dto dto) {
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);

        BaseFinProductCashInputDto inputDto = new BaseFinProductCashInputDto();

        // List<现金流信息Entity>
        List<FinCashInfo> finCashInfoList = new ArrayList<>();
        // 现金流信息Entity-本金
        FinCashInfo finCashInfoPrin = new FinCashInfo();
        finCashInfoPrin.setCashId(commonBusinessService.generateCashFlowCode(dto.getTotleTradeId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong, DictCodeListExt1.现金流二级类型_本金.getValue()));
        finCashInfoPrin.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        finCashInfoPrin.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
        finCashInfoPrin.setCcy(dto.getCcy());
        finCashInfoPrin.setFinprodId(dto.getTotleTradeId());
        finCashInfoPrin.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        finCashInfoPrin.setBranch(branchLong);
        finCashInfoPrin.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finCashInfoList.add(finCashInfoPrin);


        // 行权规则表Entity -> 行权规则表
        FinExerciseRule finExerciseRule = dto.getFinExerciseRule();
        finExerciseRule.setCashId(finCashInfoPrin.getCashId());
        finExerciseRuleMapper.insertSelective(finExerciseRule);
        // 行权计划表Entity -> 行权计划表
        FinExercisePlan finExercisePlan = dto.getFinExercisePlan();
        finExercisePlan.setCashId(finCashInfoPrin.getCashId());
        finExercisePlanMapper.insertSelective(finExercisePlan);

        // 现金流信息Entity-利息
        FinCashInfo finCashInfoInt = new FinCashInfo();
        finCashInfoInt.setCashId(commonBusinessService.generateCashFlowCode(dto.getTotleTradeId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong, DictCodeListExt1.现金流二级类型_利息.getValue()));
        finCashInfoInt.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        finCashInfoInt.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
        finCashInfoInt.setCcy(dto.getCcy());
        finCashInfoInt.setFinprodId(dto.getTotleTradeId());
        finCashInfoInt.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        finCashInfoInt.setBranch(branchLong);
        finCashInfoInt.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finCashInfoList.add(finCashInfoInt);

        inputDto.setFinCashInfoList(finCashInfoList);

        // 利率结构明细表Entity -> 利率结构明细表
        FinRateInfoStructure finRateInfoStructure = dto.getFinRateInfoStructure();
        finRateInfoStructure.setCashId(finCashInfoInt.getCashId());
        finRateInfoStructureMapper.insertSelective(finRateInfoStructure);

        // List<现金流支付规则表Entity>
        List<FinCashPayRule> finCashPayRuleList = new ArrayList<>();

        // 现金流支付规则表Entity-利息
        FinCashPayRule finCashPayRule = new FinCashPayRule();
        finCashPayRule.setCashId(finCashInfoInt.getCashId());
        finCashPayRule.setEffDate(dto.getVdate());

        // 一体化对应etliu start
        finCashPayRule.setPayCycle(DictCodeList.频率_到期一次.getValue());
        // 一体化对应etliu end

        // 一体化对应etliu
        finCashPayRule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());

        finCashPayRule.setFirstPayDate(dto.getMdate());
        finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        finCashPayRule.setFinprodId(dto.getTotleTradeId());
        finCashPayRule.setFinprodType(dto.getFinprodType());
        finCashPayRule.setFinprodType2(dto.getFinprodType2());
        finCashPayRule.setBranch(branchLong);
        finCashPayRule.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finCashPayRuleList.add(finCashPayRule);

        inputDto.setFinCashPayRuleList(finCashPayRuleList);

        // List<现金流计算规则表Entity>
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();

        // 现金流计算规则表Entity-利息
        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
        finCashCalcRule.setCashId(finCashInfoInt.getCashId());
        finCashCalcRule.setEffDate(dto.getVdate()); // 生效日期
        // 一体化对应etliu
        finCashCalcRule.setCalcType(DictCodeList.利率类型_固定利率.getValue());
        finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
        finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
        finCashCalcRule.setBasis(dto.getFinTradeProduct().getBasis());
        finCashCalcRule.setYield(dto.getFinTradeProduct().getIntRate());
        finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
        finCashCalcRule.setFinprodId(dto.getTotleTradeId()); // 金融产品代码
        finCashCalcRule.setFinprodType(dto.getFinprodType());
        finCashCalcRule.setFinprodType2(dto.getFinprodType2());
        finCashCalcRule.setBranch(branchLong);
        finCashCalcRule.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finCashCalcRuleList.add(finCashCalcRule);

        inputDto.setFinCashCalcRuleList(finCashCalcRuleList);

        // List<现金流利率信息表Entity>
        List<FinRateInfo> finRateInfoList = new ArrayList<>();

        // 现金流利率信息表Entity-利息
        FinRateInfo finRateInfo = new FinRateInfo();
        finRateInfo.setCashId(finCashInfoInt.getCashId()); // 现金流代码
        finRateInfo.setEffDate(dto.getVdate());
        // 一体化对应etliu
        finRateInfo.setIntType(DictCodeListExt3.利率类型_结构性.getValue());
        finRateInfo.setBasis(dto.getFinTradeProduct().getBasis());
        finRateInfo.setFinprodId(dto.getTotleTradeId()); // 金融产品代码
        finRateInfo.setFinprodType(DictCodeListExt3.金融产品类型_期权.getValue());
        finRateInfo.setFinprodType2(DictCodeListExt3.金融产品类型_投管用_期权.getValue());
        finRateInfo.setBranch(branchLong);
        finRateInfo.setBenchmarkId(dto.getTargetId());//基准编号-->合约标的
        finRateInfo.setBenchmarkType(DictCodeListExt3.基准类型_合约标的.getValue());//基准类型
        finRateInfo.setRemark(dto.getRemark());//备注
        finRateInfo.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finRateInfoList.add(finRateInfo);

        inputDto.setFinRateInfoList(finRateInfoList);

        // List<现金流计划表Entity>
        List<FinCashPlan> finCashPlanList = new ArrayList<>();

        // 现金流计划表Entity 到期还本
        FinCashPlan finCashPlan = new FinCashPlan();
        finCashPlan.setCashId(finCashInfoPrin.getCashId());
        finCashPlan.setCashNum(1L);
        finCashPlan.setCashType(DictCodeList.现金流类型_到期还本.getValue());
        finCashPlan.setVdateUnadjust(dto.getVdate());
        finCashPlan.setMdateUnadjust(dto.getMdate());
        finCashPlan.setPayDateUnadjust(dto.getMdate());
        finCashPlan.setVdate(dto.getVdate());
        finCashPlan.setMdate(dto.getMdate());
        finCashPlan.setPayDate(dto.getMdate());
        finCashPlan.setTermdays(DateUtil.getBetweenDayNumber(dto.getVdate(), dto.getMdate()) + 1);
        finCashPlan.setPrinAmt(dto.getPrinAmt());
        finCashPlan.setIntPrinAmt(dto.getPrinAmt());
        finCashPlan.setIntAmt(BigDecimal.ZERO);
        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        finCashPlan.setCashAmt(dto.getPrinAmt());
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        finCashPlan.setFinprodId(dto.getTotleTradeId());
        finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
        finCashPlan.setFinprodType(dto.getFinprodType());
        finCashPlan.setFinprodType2(dto.getFinprodType2());
        finCashPlan.setBranch(branchLong);
        finCashPlan.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finCashPlanList.add(finCashPlan);

        // 现金流计划表Entity 到期付息
        finCashPlan = new FinCashPlan();
        finCashPlan.setCashId(finCashInfoInt.getCashId());
        finCashPlan.setCashNum(2L);
        finCashPlan.setCashType(DictCodeList.现金流类型_到期收息.getValue());
        finCashPlan.setVdateUnadjust(dto.getVdate());
        finCashPlan.setMdateUnadjust(dto.getMdate());
        finCashPlan.setPayDateUnadjust(dto.getMdate());
        finCashPlan.setVdate(dto.getVdate());
        finCashPlan.setMdate(dto.getMdate());
        finCashPlan.setPayDate(dto.getMdate());
        finCashPlan.setTermdays(DateUtil.getBetweenDayNumber(dto.getVdate(), dto.getMdate()) + 1);
        finCashPlan.setPrinAmt(BigDecimal.ZERO);
        finCashPlan.setIntPrinAmt(dto.getPrinAmt());

        // 计划支付利息
        // ※调用业务组件.【平安银行算法组件.投资交易回购利息】，拆借算法同回购一样，可参考回购利息算法
        finCashPlan.setIntAmt(dto.getIntAmt());

        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        finCashPlan.setCashAmt(finCashPlan.getIntAmt());
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        finCashPlan.setFinprodId(dto.getTotleTradeId());
        finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
        finCashPlan.setFinprodType(dto.getFinprodType());
        finCashPlan.setFinprodType2(dto.getFinprodType2());
        finCashPlan.setBranch(branchLong);
        finCashPlan.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        finCashPlanList.add(finCashPlan);

        inputDto.setFinCashPlanList(finCashPlanList);

        // 调用共通登录现金流相关数据
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertFinProductCashData",
                inputDto, Boolean.class);
    }

    /**
     * 删除业务流程数据
     *
     * @param workflowId 工作流代码
     * @see
     * @since 1.0
     */
    private void deleteBussFlowData(String workflowId) {
        baseTradeProductDealCommService.deleteWflData(workflowId);
        wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        wflTrdDealTotalAddMapper.deleteByWorkflowId(workflowId);
        wflPaTrdFinDealTotalMapper.deleteByWorkflowId(workflowId);
        wflStlPayOrdersMapper.deleteByWorkflowId(workflowId);
    }

    @Override
    public void doInsert(TrdOptionTrade002Dto entity, OperationType operationType) {
        if (CollectionUtils.isNotEmpty(entity.getFileArray())
                && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getFileArray().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getTotleTradeId());
        }
        // 新建交易
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            this.handleBussFlowData(entity);
        }
        // 撤销交易
        else {
            this.doRevokeInsert(entity);
        }
    }

    /**
     * 撤销交易doInsert
     *
     * @param entity 参数entity
     * @see
     * @since 1.0
     */
    private void doRevokeInsert(TrdOptionTrade002Dto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易汇总信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealTotalDtoList = restTemplate.getForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTotelTradeInfo?tradeId=" + entity.getTotleTradeId(),
                BaseTradeProductDealDto.class);

        if (!StringUtil.isEmptyList(baseTradeProductDealTotalDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealTotalDtoList) {
                //金融产品交易汇总业务流程表
                WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, baseDto.getTrdFinProductDealTotal());
                // 交易状态=“撤销”
                wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                // 原交易编号=金融产品交易汇总Entity.交易编号
                wflTrdFinProductDealTotal.setpTradeId(entity.getTotleTradeId());
                // 撤销说明=画面.撤销说明
                //wflTrdFinProductDealTotal.setCancelRemark(entity.getRevokeReason());
                // 金融产品交易汇总Entity.交易编号+“R”
                wflTrdFinProductDealTotal.setTotleTradeId(entity.getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());

                // 金融产品交易汇总附属业务流程表
                if (baseDto.getTrdProductDealAdd() != null) {
                WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, baseDto.getTrdProductDealAdd());
                wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdDealTotalAdd.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId());
                wflTrdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
                wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);
                }
                // 转换
                wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);
                

                // 金融产品交易汇总信息DTO.划款指令表Entity -> 划款指令业务流程表
                if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
                    for (StlPayOrdersDto stlPayOrdersDto : baseDto.getStlPayOrdersList()) {
                        WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        wflStlPayOrders.setWorkflowId(entity.getWorkflowId());

                        String orderId;
                        if (baseGetBusinessId != null) {
                            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                            bussinessCodeParameter.setSeqNo("9998");
                            orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
                        }
                        else {
                            orderId = IDGenerator.generate();
                        }
                        wflStlPayOrders.setOrderId(orderId);
                        wflStlPayOrders.setTradeId(wflTrdFinProductDealTotal.getTotleTradeId());
                        wflStlPayOrders.setpTradeId(stlPayOrdersDto.getOrderId());
                        wflStlPayOrders.setProdId(wflTrdFinProductDealTotal.getTotleTradeId());
                        wflStlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                        wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                    }
                }
            }
        }
    }


    @Override
    public void doUpdate(TrdOptionTrade002Dto entity) {
        if (CollectionUtils.isNotEmpty(entity.getFileArray())
                && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getFileArray().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getTotleTradeId());
        }
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            // 先删除流程中数据
            baseTradeProductDealCommService.deleteWflData(entity.getWorkflowId());

            // 重新insert
            this.handleBussFlowData(entity);
        }
    }

    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId)
            throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        TrdOptionTrade002Dto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
        	  try {
//                return commonBusinessService.是否需要上级审批(dto.getCounterId(), dto.getFinTradeProduct().getPrin(),
//                        LoginUserUtil.getUserName(), DictCodeListExt3.平安资产类型_期权.getValue(), dto.getFinProductType().getType6(), dto.getTradeMarket(),
//                        dto.getPs()
//                        // 资产类型改为授权类型 add by renping 2019-08-06 start
//                        , null
//                        // 资产类型改为授权类型 add by renping 2019-08-06 end
//                );
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getFinTradeProduct().getPrin(),LoginUserUtil.getUserName(),
                    DictCodeListExt3.平安资产类型_期权.getValue(), dto.getFinProductType().getType6(), dto.getTradeMarket(), dto.getPs()
                    , null ,entity.getWorkflowId());
                params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
                return commonBusinessService.是否需要上级审批(params);
            }
            catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        else {
            return null;
        }
        // return null;
    }

    @Override
    public void doBack(TrdOptionTrade002Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
        deleteBussFlowData(entity.getWorkflowId());
    }

    @Override
    public void doRevoke(TrdOptionTrade002Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
    }

    /**
     * 根据工作流代码查询交易编号
     *
     * @param workflowId 工作流代码
     * @return 交易编号
     * @see
     * @since 1.0
     */
    public String selectTradeIdByWorkflowId(String workflowId) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdOptionTrade/selectTradeIdByWorkflowId/" + workflowId, null,
                String.class);
    }

    /**
     * 根据汇总交易编号查询数据信息
     * @param tradeId 汇总交易编号
     * @return
     */
    public TrdOptionTrade002Dto getTrdOptionTradeDto(String tradeId) {
        String BRANCH_0 = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdOptionTrade/getTrdOptionTradeDto/" + tradeId + "/" + BRANCH_0,
                null, TrdOptionTrade002Dto.class);
    }

    /**
     * 获取交易编号
     * @return 交易编号
     */
    public String gen交易编号(){
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_期权交易));
            return baseGetBusinessId.buildBussinessCode(codeParameter);
        }
        else {
            return IDGenerator.generate();
        }
    }

    /**
     * 查询委托信息
     *
     * @param entrustId 委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    public TrdOptionTrade002Dto getEntrustInfo(String entrustId) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdOptionTrade/findTradeInfoByEntrustId/" + entrustId, null,
                TrdOptionTrade002Dto.class);
    }

    /**
     * 查询委托所做交易的期权费总额
     *
     * @param entrustId 委托编号
     * @return 期权费
     * @see
     * @since 1.0
     */
    public BigDecimal getPrinAmt(String entrustId) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdOptionTrade/getPrinAmt/" + entrustId, null,
                BigDecimal.class);
    }

}
