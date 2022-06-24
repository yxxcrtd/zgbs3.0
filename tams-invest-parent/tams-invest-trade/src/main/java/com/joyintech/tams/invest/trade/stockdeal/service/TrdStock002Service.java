/*
 * 文件名：TrdStock002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：wangpeng 修改时间：2019年7月29日 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.stockdeal.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.ExchangeTransMatchCommonService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.valuationinterf.ValuationInterfService;
import com.joyintech.tams.invest.trade.stockdeal.dto.TrdStockDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 股票交易新建画面Service
 *
 * @author wangpeng
 * @version 1.0 2019年7月29日
 * @see TrdStock002Service
 * @since 1.0
 */
@Service
public class TrdStock002Service extends BaseService<TrdStockDto> implements BaseProcessService<TrdStockDto> {

    /**
     * BussTrdTransferOrderService
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * DateCalService
     */
    @Autowired(required = false)
    private DateCalService dateCalService;

    /**
     * BaseTradeProductDealCommService
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 共通Mapper的通用代理方案
     */
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired(required = false)
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * MultiApproveTradeCheckService
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;

    /**
     * jixu接口
     */
    @Autowired
    private ValuationInterfService valuationInterfService;

	@Autowired
	private CommonBusinessService commonBusinessService;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    // 调用投管接口 add by renping 20190205 start
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;

    /**
     * 标的表
     */
    @Autowired(required = false)
    private FinProductMapper finProductMapper;
    // 调用投管接口 add by renping 20190205 end
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

    @Autowired
    private ExchangeTransMatchCommonService exchangeTransMatchCommonService;

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。 {@inheritDoc}
     */
    @Override
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
     * 由画面输入的参数dto，构筑整个需要反映到数据库的实体
     *
	 * @param dto
	 *            画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void prepareEntity(TrdStockDto dto) {
        if (StringUtil.isEmpty(dto.getActPortfolioId())) {
            dto.setActPortfolioId(dto.getPortfolioId());
            dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));
        }
        // 券面总额
        // dto.setTotalFaceValue(dto.getTradeAmt());

        // 金融产品交易Entity
        // 构造开始----------------------------------------------------------
        // 业务类型
        // 自动生成反向交易 chenzhili start
        // dto.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        // 自动生成反向交易 chenzhili end
        // 金融产品类型（估值核算） TODO
        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        // ※委托场合时，由委托带出的金融产品代码;若新建场合时，选择资产代码时带出金融产品代码
        if (StringUtil.isNotEmpty(dto.getEntrustId())) {// 新建

        }
        else {// 委托

        }
        // 交易费用明细表.是否随交易支付 = “是”(默认） 页面有值 采用页面值
        if (StringUtil.isEmpty(dto.getCommissionIsPayWithTrade())) {
            dto.setCommissionIsPayWithTrade(DictCodeList.是否_是.getValue());
        }
        // 交易费用明细表.是否随交易支付 = “是”(默认） 页面有值 采用页面值
        if (StringUtil.isEmpty(dto.getStampDutyIsPayWithTrade())) {
            dto.setStampDutyIsPayWithTrade(DictCodeList.是否_是.getValue());
        }
        // 随交易支付
        // if(StringUtil.isNotEmpty(dto.getChlId())&&StringUtil.isNotEmpty(dto.getChannelFee())){
        // //通道费 默认随交易支付
        // dto.setFeeAmtPay(dto.getChannelFee());
        // //总费用
        // dto.setFeeAmt(dto.getChannelFee());
        // }else{
        // 佣金
        dto.setFeeAmtPay(BigDecimal.ZERO);
        dto.setFeeAmtUnpay(BigDecimal.ZERO);
        if (StringUtil.equals(dto.getCommissionIsPayWithTrade(), DictCodeList.是否_是.getValue())) {
            // 随交易费用
            dto.setFeeAmtPay(ArithmeticUtil.doAdd(dto.getCommission(), dto.getFeeAmtPay()));
        }
        else {
            // 不随交易支付
            dto.setFeeAmtUnpay(ArithmeticUtil.doAdd(dto.getCommission(), dto.getFeeAmtUnpay()));
        }
        // 印花
        if (StringUtil.equals(dto.getStampDutyIsPayWithTrade(), DictCodeList.是否_是.getValue())) {
            // 随交易费用
            dto.setFeeAmtPay(ArithmeticUtil.doAdd(dto.getStampDuty(), dto.getFeeAmtPay()));
        }
        else {
            // 不随交易支付
            dto.setFeeAmtUnpay(ArithmeticUtil.doAdd(dto.getStampDuty(), dto.getFeeAmtUnpay()));
        }
        // 总费用
        dto.setFeeAmt(ArithmeticUtil.doAdd(dto.getFeeAmtPay(), dto.getFeeAmtUnpay()));
        // }
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);
        dto.setBranch(branchLong);
        // 单位面值
        // dto.setParValue(this.getUnitPrice(dto.getFinprodId(), dto.getSettleDate()));
        // 交易本金 = 画面.交易数量 乘以交易价格 授信使用
        dto.setPrinAmt(ArithmeticUtil.multiply(dto.getShareAmt(), dto.getUnitCprice()));
        // 交易份额 = 画面.券面总额/100
        // dto.setShareAmt(ArithmeticUtil.divide(dto.getTotalFaceValue(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        // 交易金额 = 画面.全价总额
        // dto.setTradeAmt(dto.getFpriceAmt());

        // 是否撤销 = 数据字典缓存.是否.否
        dto.setIsCancel(DictCodeList.是否_否.getValue());

        TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
        if (personInfo != null) {
            // 本方交易员
            dto.setTrader(personInfo.getTrader());
            // 交易对手
            dto.setCounterId(personInfo.getCounterId());
            // 是否外部交易
            dto.setIsOutTrade(personInfo.getIsOutTrade());
            // 对手方交易员
            dto.setCounterTrader(personInfo.getCounterTrader());
            // 联系方式
            dto.setCounterContact(personInfo.getContactInfo());
        }
        // if(StringUtil.isNotEmpty(dto.getChannelFee())){
        // dto.setFeeAmtPay(dto.getChannelFee());
        // dto.setFeeAmt(dto.getChannelFee());
        // }else{
        // dto.setFeeAmtPay(ArithmeticUtil.doAdd(dto.getCommission(), dto.getStampDuty()));
        // dto.setFeeAmt(ArithmeticUtil.doAdd(dto.getCommission(), dto.getStampDuty()));
        // }

        // 交易编号、融产品代码、金融产品类型（投管分类）、证券管理户代码、关联系统交易编号、投资目的、交易日、
        // 起息日、到期日、交割日、单位净价、单位利息、单位全价、净价总额、交易场所、交易市场、交易平台、交易币种、交易方向、利息总额、全价总额、备注
        // 以上信息为画面上项目，不需要特殊设置
        // 金融产品交易Entity
        // 构造结束----------------------------------------------------------

        // 金融产品交易附属表Entity
        // 构造开始----------------------------------------------------------
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(dto.getTradeId()); // 交易编号
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        trdProductDealAdd.setEntrustId(dto.getEntrustId()); // 委托代码
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
        trdProductDealAdd.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        trdProductDealAdd.setDealMode(dto.getDealMode()); // 交易方式
        trdProductDealAdd.setLockMdate(dto.getLockMdate()); // 锁定期
        trdProductDealAdd.setWithLockPeriod(dto.getWithLockPeriod()); // 是否有锁定期
        trdProductDealAdd.setDealMode(dto.getDealMode()); // 交易方式
        dto.setTrdProductDealAdd(trdProductDealAdd);
        // 金融产品交易附属表Entity
        // 构造结束----------------------------------------------------------

        // 组合交易表Entity
        // 构造开始----------------------------------------------------------
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId()); // 组合代码
        ptlTrade.setBusiNo(dto.getTradeId()); // 业务编号
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
        ptlTrade.setBusiId(dto.getFinprodId()); // 业务明细代码
        // ptlTrade.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue()); // 业务类型
        // ptlTrade.setInvAim(dto.getInvAim()); // 投资目的
        ptlTrade.setSecManageAcctId(dto.getSecManageAcctId()); // 证券托管户代码
        ptlTrade.setSettleDate(dto.getSettleDate()); // 实际发生日
        ptlTrade.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        ptlTrades.add(ptlTrade);

        if (!StringUtil.equals(dto.getPortfolioId(), dto.getActPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            ptlTrade1.setPortfolioId(dto.getActPortfolioId()); // 组合代码
            ptlTrade1.setBusiNo(dto.getTradeId()); // 业务编号
            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
            ptlTrade1.setBusiId(dto.getFinprodId()); // 业务明细代码
            // ptlTrade.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue()); // 业务类型
            // ptlTrade.setInvAim(dto.getInvAim()); // 投资目的
            ptlTrade1.setSecManageAcctId(dto.getSecManageAcctId()); // 证券托管户代码
            ptlTrade1.setSettleDate(dto.getSettleDate()); // 实际发生日
            ptlTrade1.setWorkflowId(dto.getWorkflowId()); // 工作流代码
            ptlTrades.add(ptlTrade1);
        }

        dto.setPtlTrade(ptlTrades);
        // 组合交易表Entity
        // 构造结束----------------------------------------------------------

        // 划款指令表Entity
        // 构造开始----------------------------------------------------------
        if (bussTrdTransferOrderService != null) {
            // 调用划款指令共通业务组件的保存处理，根据输出结果加工形成金融产品交易DTO的要素数据
            if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getTradeId()); // 交易编号
                dto.getBussTrdTransferOrder().get(0).setOrderStatus(DictCodeListExt1.指令状态_新建.getValue()); // 指令状态
                dto.getBussTrdTransferOrder().get(0).setWorkflowId(dto.getWorkflowId()); // 工作流代码
                dto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            }
        }
        dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getTradeId());
        // 划款指令表Entity
        // 构造结束----------------------------------------------------------

        // 金融产品交易扩展Entity
        // 构造开始----------------------------------------------------------
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(dto.getTradeId()); // 交易编号
        // 平安业务类型(数据字典缓存.平安业务类型（按交易方向取对应值）)
        // if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dto.getPs())) {
        // paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        // } else if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), dto.getPs())) {
        // paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
        // }
        // // 一体化对应etliu start
        // // else if (StringUtil.equals(DictCodeList.交易方向_行权.getValue(),
        // // dto.getPs())) {
        // else if (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), dto.getPs())
        // || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), dto.getPs())) {
        // // 一体化对应etliu end
        // paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
        // }
        paTrdProductDeal.setPaBussType(DictCodeListExt2.平安业务类型_股票交易.getValue());
        paTrdProductDeal.setDeptCode(personInfo==null?null:personInfo.getTraderDept()); // 交易所属部门
        paTrdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setPaTrdProductDeal(paTrdProductDeal);
        // 金融产品交易扩展Entity
        // 构造结束----------------------------------------------------------

        // 通道交易 新增 强成西 2019/03/26 start
        // setChannelFee(dto);
        // 通道交易 新增 强成西 2019/03/26 end
    }

    /**
     * 提交时登录业务流程表
     *
	 * @param entity
	 *            画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(TrdStockDto entity) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList()) && StringUtil.isNotEmpty(entity.getTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getTradeId());
        }

        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }

        this.prepareEntity(entity);

        // 调用授信共同 sjj start
        TrdProductDeal TrdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity);
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        inputDto.setTrdProductDeal(TrdProductDeal);
        AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
        // 调用授信共同 sjj end

        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity);
        wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDealAdd());
        wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());

        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, entity.getPaTrdProductDeal());
        wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

        // 转换
        wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
        wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
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
        entity.setBusiType("TRD55");
        baseTradeProductDealDto.setTrdProductDeal(entity);
        baseTradeProductDealDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(entity.getBussTrdTransferOrder().get(0)));
        }
        finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
        // 委托代码不为空处理委托代码
        finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
            String entrustId = StringUtil.isNotEmpty(
                entity.getEntrustId()) ? entity.getEntrustId() : item.getTrdProductDealAdd() != null ? item.getTrdProductDealAdd().getEntrustId() : null;
            if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
                item.setEntrustId(entrustId);
            }
        });

        // 撤销时，子组合需要调估值共通
        if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                finProductTrade.setActPortfolioId(entity.getActPortfolioId());
            });
        }
        baseTradeProductDealCommService.execute(finProductTradeDto);
        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                    entity.getActPortfolioId()));
            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                    entity.getPortfolioId()));
        }
    }

	/*private void setChannelFee(TrdProductDealDto entity) {
		if (DictCodeList.交易方向_买入.getValue().equals(entity.getPs())) {
			entity.setTradeAmt(ArithmeticUtil.doAdd(entity.getTradeAmt(), entity.getChannelFee()));
		} else {
			entity.setTradeAmt(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getChannelFee()));
		}
		entity.setFeeAmtPay(ArithmeticUtil.doAdd(entity.getFeeAmtPay(), entity.getChannelFee()));
		entity.setFeeAmt(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getChannelFee()));
	}*/

    /**
     * 删除业务流程数据
     *
	 * @param workflowId
	 *            工作流代码
     * @see
     * @since 1.0
     */
    private void deleteBussFlowData(String workflowId) {
        // // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        // bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "WORKFLOW_ID", workflowId);
        //
        // // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        // bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD", "WORKFLOW_ID", workflowId);
        //
        // // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        // bussCommonMapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL", "WORKFLOW_ID", workflowId);
        //
        // // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        // bussCommonMapperDelegateService.deleteBySpecificKey("WFL_STL_PAY_ORDERS", "WORKFLOW_ID", workflowId);
        baseTradeProductDealCommService.deleteWflData(workflowId);
    }

    /**
     * 撤销交易doInsert
     *
	 * @param entity
	 *            参数entity
     * @see
     * @since 1.0
     */
    private void doRevokeInsert(TrdStockDto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId=" + entity.getTradeId(),
            BaseTradeProductDealDto.class);

        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baseDto.getTrdProductDeal());
                // 交易状态=“撤销”
                wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
                wflTrdProductDeal.setpTradeId(entity.getTradeId());
                // 撤销说明=画面.撤销说明
                wflTrdProductDeal.setCancelRemark(entity.getRevokeReason());
                // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
                wflTrdProductDeal.setTradeId(entity.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());

                // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseDto.getTrdProductDealAdd());
                wflTrdProductDealAdd.setTradeId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setProdId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());

                // 批量插入
                wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
                if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
                    List<WflStlPayOrders> stlList = new ArrayList<>();
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
                        wflStlPayOrders.setTradeId(wflTrdProductDeal.getTradeId());
                        wflStlPayOrders.setpTradeId(stlPayOrdersDto.getOrderId());
                        wflStlPayOrders.setProdId(wflTrdProductDeal.getTradeId());
                        wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                    }
                }
            }
        }
    }

    /**
     * 撤销doComplete
     *
	 * @param entity
	 *            参数
     * @see
     * @since 1.0
     */
    private void doCompleteRevoke(TrdStockDto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId=" + entity.getTradeId(),
            BaseTradeProductDealDto.class);

        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                //删除授信占用
                if (null != baseDto.getTrdProductDeal().getTradeId() && !"".equals(baseDto.getTrdProductDeal().getTradeId())){
                    riskCreditListCommonValidatorService.deleteCredit(baseDto.getTrdProductDeal().getTradeId());
                }
                
                baseDto.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                baseDto.getTrdProductDeal().setCancelRemark(entity.getRevokeReason());
                baseDto.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId());
                if (bussCommonMapperDelegateService != null) {
                    PaTrdProductDeal paTrdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                        PaTrdProductDealMapper.class, baseDto.getTrdProductDeal().getTradeId(), PaTrdProductDeal.class);
                    paTrdProductDeal.setTradeId(
                        paTrdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    paTrdProductDeal.setCreateUser(LoginUserUtil.getUserName());
                    paTrdProductDeal.setCreateTime(new Date());
                    paTrdProductDeal.setUpdateUser(paTrdProductDeal.getCreateUser());
                    paTrdProductDeal.setUpdateTime(paTrdProductDeal.getCreateTime());
                    paTrdProductDealMapper.insertSelective(paTrdProductDeal);
                    // bussCommonMapperDelegateService.insertBatch(PaTrdProductDealMapper.class, paTrdProductDeal);
                }
            }
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtoList);
            // 委托代码不为空处理委托代码
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
                String entrustId = StringUtil.isNotEmpty(
                    entity.getEntrustId()) ? entity.getEntrustId() : item.getTrdProductDealAdd() != null ? item.getTrdProductDealAdd().getEntrustId() : null;
                if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
                    item.setEntrustId(entrustId);
                }
            });

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommService.execute(finProductTradeDto);

            /*
             * 一体化对应etliu start 所有交易生效后调用的共通方法
             */
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(baseDto.getTrdProductDealAdd().getPortfolioId());
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                inputDto.setTrdProductDeal(baseDto.getTrdProductDeal());
                if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    inputDto.setActPortfolioId(entity.getActPortfolioId());
                }
                inputDto.setPtlTradeList(baseDto.getPtlTrade());
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            }
            // 一体化对应etliu end
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

        //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

        // 调用基础组件.【交易报送组件】删除交易报送信息
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepTrade",
            Arrays.asList(entity.getTradeId()), Boolean.class);
        
        for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
            // jixu 接口 add by wangpeng start

            List<TrdTradeFeeDetail> tradeFeeDetaillist = new ArrayList<>();
            // 佣金及印花
            TrdTradeFeeDetail trdTradeFeeDetailB05 = new TrdTradeFeeDetail();
            trdTradeFeeDetailB05.setTradeId(entity.getTradeId());
            // 交易费用明细表.费用类型 = “佣金费”
            trdTradeFeeDetailB05.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
            trdTradeFeeDetailB05.setIsPayWithTrade(entity.getCommissionIsPayWithTrade());
            // 交易费用明细表.交易金额 = 交易.佣金费
            trdTradeFeeDetailB05.setFeeAmt(entity.getCommission());
            tradeFeeDetaillist.add(trdTradeFeeDetailB05);
            TrdTradeFeeDetail trdTradeFeeDetailB02 = new TrdTradeFeeDetail();
            trdTradeFeeDetailB02.setTradeId(entity.getTradeId());
            // 交易费用明细表.费用类型 = “印花税”
            trdTradeFeeDetailB02.setFeeType(DictCodeListExt1.一次性费用类型_印花税.getValue());
            // 交易费用明细表.是否随交易支付 = “是” 页面有值 采用页面值
            trdTradeFeeDetailB02.setIsPayWithTrade(entity.getStampDutyIsPayWithTrade());
            // 交易费用明细表.交易金额 = 交易.印花税
            trdTradeFeeDetailB02.setFeeAmt(entity.getStampDuty());
            tradeFeeDetaillist.add(trdTradeFeeDetailB02);
            List<StlPayOrders> stlPayOrdersList = new ArrayList<StlPayOrders>();
            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
            StlPayOrdersDto stlPayOrders = null;
            if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
                stlPayOrders = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, entity.getBussTrdTransferOrder().get(0));
            }

            if (stlPayOrders != null) {
                stlPayOrdersList.addAll(Arrays.asList(stlPayOrders));
            }
//            TrdCommDto trdCommDto = new TrdCommDto();
//            // trdCommDto.setFinTradeProduct(entity.getFinTradeProduct());
//            // trdCommDto.setStlFinProductAcctList(stlFinProductAcctList);
//            trdCommDto.setStlPayOrdersList(stlPayOrdersList);
//            trdCommDto.setTrdProductDeal(baseDto.getTrdProductDeal());
//            trdCommDto.setTrdProductDealAdd(baseDto.getTrdProductDealAdd());
//            trdCommDto.setTradeFeeDetaillist(tradeFeeDetaillist);
//            trdCommDto.setPtlTradeList(baseDto.getPtlTrade());
//            trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_DELETE);
//            ResultContainer resultContainer = valuationInterfService.runValInvest2Jixu(trdCommDto);
//            // jixu 接口 add by wangpeng end

            // 调用投管接口 add by renping 20190205 start
            InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
            investInterfCommDto.setPageId("TrdStock002");
            investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
            investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
            investInterfCommDto.setTrdProductDeal(baseDto.getTrdProductDeal());
            investInterfCommDto.setTrdProductDealAdd(baseDto.getTrdProductDealAdd());
            investInterfCommDto.setStlPayOrders(stlPayOrders);
            investInterfCommDto.setTradeFeeDetaillist(tradeFeeDetaillist);
            investInterfCommService.doInvestInterf(investInterfCommDto);
            // 调用投管接口 add by renping 20190205 end

        }
    }

    @Override
    public void doInsert(TrdStockDto entity, OperationType operationType) {
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
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(TrdStockDto entity) {
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            // 先删除流程中数据
            this.doDelete(entity.getWorkflowId());

            // 重新insert
            this.handleBussFlowData(entity);
        }
    }

    /**
	 * 撤销/流程退回时额度占用、释放处理 Add by xkni 20190830
	 *
	 * @param entity          交易model
	 * @param isNotBottomflag 是否是底层资产交易
	 * @param isSave          是否入库
	 * @param isdeleteflag    是否是作废删除
	 * @return
	 */
	private void isNeedOccupy(TrdStockDto entity, boolean isNotBottomflag, boolean isSave, Boolean isdeleteflag) {
		// 所有交易生效后调用的共通方法(入账信息)  撤销后释放占用的授信额度  Add 20190828 by xkni
		AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
		inputDto.setProdid(entity.getPortfolioId());
		inputDto.setProdFinprodType(DictCodeListExt2.共通业务类型_股票信息.getValue());
		TrdProductDeal trdProductDeal = new TrdProductDeal();
		trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());//交易状态
		trdProductDeal.setPs(entity.getPs());//交易方向
		if(StringUtils.isNotBlank(entity.getfTradeId())){
			trdProductDeal.setfTradeId(entity.getfTradeId());
		}
		trdProductDeal.setTradeId(entity.getTradeId());//交易编号
		trdProductDeal.setFinprodId(entity.getFinprodId());//金融产品代码
		trdProductDeal.setCreateDept(entity.getCreateDept());//创建部门
		trdProductDeal.setCreateUser(entity.getCreateUser());//创建人
		trdProductDeal.setCreateTime(entity.getCreateTime());//创建时间
		trdProductDeal.setUpdateUser(entity.getUpdateUser());//更新人
		trdProductDeal.setUpdateTime(entity.getUpdateTime());//更新时间
		trdProductDeal.setTradeDate(entity.getTradeDate());//交易日期
		trdProductDeal.setShareAmt(entity.getShareAmt());//交易份额，债券数量等
		trdProductDeal.setTradeAmt(entity.getTradeAmt());//交易金额
		trdProductDeal.setPrinAmt(entity.getPrinAmt());//交易本金，债券券面等
		trdProductDeal.setFinprodType(entity.getFinprodType());//金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
		trdProductDeal.setFinprodType2(entity.getFinprodType2());//金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
		TrdInvestPersonInfo002Dto personInfo = entity.getTrdInvestPersonInfo();
		if (personInfo != null) {
			// 交易对手
			trdProductDeal.setCounterId(personInfo.getCounterId());//交易对手编号
		}

		inputDto.setTrdProductDeal(trdProductDeal);
		CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
		ceLinesUpdate.isNeedOccupyCre(inputDto, isNotBottomflag, isSave, isdeleteflag);
	}
	/**
     * {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
    }

    @Override
    public void doComplete(TrdStockDto entity, OperationType operationType) {
        this.prepareEntity(entity);

        // 7.1登录各业务表，包括金融产品交易、金融产品附属、划款指令及附件信息
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易表
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity);
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, entity.getTrdProductDealAdd());
        trdProductDealAdd.setPortfolioId(entity.getPortfolioId());

        // 金融产品交易扩展表Entity -> 金融产品交易扩展表
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, entity.getPaTrdProductDeal());

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
        StlPayOrdersDto stlPayOrders = null;
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            stlPayOrders = new StlPayOrdersDto();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, entity.getBussTrdTransferOrder().get(0));
        }

        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            List<TrdTradeFeeDetail> tradeFeeDetaillist = new ArrayList<>();

            // 通道交易 新增 强成西 2019/03/26 start
            // if (StringUtil.isNotEmpty(trdProductDeal.getChlId())) {
            // // 交易费用明细
            // trdTradeFeeDetail = AllTradeCommonUtil.genChannelTradeFeeDetail(entity.getTradeId(),
            // entity.getChannelFee());
            // bussCommonMapperDelegateService.insertBatch(TrdTradeFeeDetailMapper.class, trdTradeFeeDetail);
            // }else{
            // 佣金及印花
            TrdTradeFeeDetail trdTradeFeeDetailB05 = new TrdTradeFeeDetail();
            trdTradeFeeDetailB05.setTradeId(entity.getTradeId());
            // 交易费用明细表.费用类型 = “佣金费”
            trdTradeFeeDetailB05.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
            trdTradeFeeDetailB05.setIsPayWithTrade(entity.getCommissionIsPayWithTrade());
            // 交易费用明细表.交易金额 = 交易.佣金费
            trdTradeFeeDetailB05.setFeeAmt(entity.getCommission());
            tradeFeeDetaillist.add(trdTradeFeeDetailB05);
            if (trdTradeFeeDetailB05.getFeeAmt() != null) {
                // bussCommonMapperDelegateService.insertBatch(TrdTradeFeeDetailMapper.class, trdTradeFeeDetailB05);
                trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetailB05);
            }
            TrdTradeFeeDetail trdTradeFeeDetailB02 = new TrdTradeFeeDetail();
            trdTradeFeeDetailB02.setTradeId(entity.getTradeId());
            // 交易费用明细表.费用类型 = “印花税”
            trdTradeFeeDetailB02.setFeeType(DictCodeListExt1.一次性费用类型_印花税.getValue());
            // 交易费用明细表.是否随交易支付 = “是” 页面有值 采用页面值
            trdTradeFeeDetailB02.setIsPayWithTrade(entity.getStampDutyIsPayWithTrade());
            // 交易费用明细表.交易金额 = 交易.印花税
            trdTradeFeeDetailB02.setFeeAmt(entity.getStampDuty());
            tradeFeeDetaillist.add(trdTradeFeeDetailB02);
            if (trdTradeFeeDetailB02.getFeeAmt() != null) {
                // bussCommonMapperDelegateService.insertBatch(TrdTradeFeeDetailMapper.class, trdTradeFeeDetailB02);
                trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetailB02);
            }
            // }
            // 通道交易 新增 强成西 2019/03/26 end

            // 组合交易表Entity -> 组合交易信息表
            if (CollectionUtils.isNotEmpty(entity.getPtlTrade())) {
                 entity.getPtlTrade().forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
            }

            // 批量插入
            // bussCommonMapperDelegateService.insertBatch(PaTrdProductDealMapper.class, paTrdProductDeal);
            paTrdProductDealMapper.insertSelective(paTrdProductDeal);

            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
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
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
                String entrustId = StringUtil.isNotEmpty(
                    entity.getEntrustId()) ? entity.getEntrustId() : item.getTrdProductDealAdd() != null ? item.getTrdProductDealAdd().getEntrustId() : null;
                if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
                    item.setEntrustId(entrustId);
                }
            });

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommService.execute(finProductTradeDto);

            // 7.2删除原业务流程表中的交易记录
            String workflowId = entity.getWorkflowId();
            if (StringUtil.isBlank(workflowId)) {
                WflTrdProductDealKey wflTrdProductDealKey = new WflTrdProductDealKey();
                wflTrdProductDealKey.setTradeId(entity.getTradeId());
                wflTrdProductDealKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                WflTrdProductDeal wflTrdProductDeal = bussCommonMapperDelegateService.selectByMultiPrimaryKey(
                    WflTrdProductDealMapper.class, wflTrdProductDealKey, WflTrdProductDeal.class);

                if (wflTrdProductDeal != null) {
                    workflowId = wflTrdProductDeal.getWorkflowId();
                }
            }
            this.deleteBussFlowData(workflowId);

            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

            trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
            trdProductDeal.setBusiType(DictCodeListExt2.共通业务类型_股票投资交易.getValue());
            // 发送Murex接口处理
            // this.sendMurex(entity, false);
            // 一体化对应etliu start
            // 所有交易生效后调用的共通方法
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            inputDto.setPageId("TrdStock002");
            inputDto.setProdid(entity.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
            inputDto.setTrdProductDeal(trdProductDeal);

            // 通道交易 新增 强成西 2019/03/26 start
            inputDto.setTrdProductDealAdd(trdProductDealAdd);
            // inputDto.setTrdTradeFeeDetail(trdTradeFeeDetail);
            // 通道交易 新增 强成西 2019/03/26 end

            // 反向交易增加 start by qiangchengxi 2019年3月12日
            AllTradeCommonUtil.setDeals(inputDto, trdProductDeal, trdProductDealAdd, paTrdProductDeal,
                Arrays.asList(stlPayOrders), entity.getPtlTrade(), entity.getActPortfolioId(),tradeFeeDetaillist);
            // 反向交易增加 end by qiangchengxi 2019年3月12日
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                inputDto.setActPortfolioId(entity.getActPortfolioId());
            }
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            // 一体化对应etliu end

//            // 增加jixu接口 start wangpeng
//            List<StlPayOrders> stlPayOrdersList = new ArrayList<StlPayOrders>();
//            if (stlPayOrders != null) {
//                stlPayOrdersList.addAll(Arrays.asList(stlPayOrders));
//            }
//            TrdCommDto trdCommDto = new TrdCommDto();
//            // trdCommDto.setFinTradeProduct(entity.getFinTradeProduct());
//            // trdCommDto.setStlFinProductAcctList(stlFinProductAcctList);
//            trdCommDto.setStlPayOrdersList(stlPayOrdersList);
//            trdCommDto.setTrdProductDeal(trdProductDeal);
//            trdCommDto.setTrdProductDealAdd(trdProductDealAdd);
//            trdCommDto.setTradeFeeDetaillist(tradeFeeDetaillist);
//            trdCommDto.setPtlTradeList(entity.getPtlTrade());
//            trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_ADD);
//            ResultContainer ResultContainer = valuationInterfService.runValInvest2Jixu(trdCommDto);
//            // 增加jixu接口 end

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

            // 调用投管接口 add by renping 20190205 start
            InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
            investInterfCommDto.setPageId("TrdStock002");
            investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
            investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
            investInterfCommDto.setTrdProductDeal(trdProductDeal);
            investInterfCommDto.setTrdProductDealAdd(trdProductDealAdd);
            investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
            investInterfCommDto.setFinProduct(
                finProductMapper.selectByPrimaryKey(trdProductDeal.getFinprodId()));
            investInterfCommDto.setTradeFeeDetaillist(tradeFeeDetaillist);
            investInterfCommService.doInvestInterf(investInterfCommDto);
            // 调用投管接口 add by renping 20190205 end

            //成交录单---调用共通
            if(StringUtil.isNotEmpty(entity.getDealSqeId())) {
                exchangeTransMatchCommonService.transactionRecord(entity.getDealSqeId(),entity.getTradeId(),OperationType.NEW);
            }
        }
        // 撤销
        else {
            this.doCompleteRevoke(entity);

            //成交录单---调用共通
            exchangeTransMatchCommonService.transactionRecord(entity.getDealSqeId(),entity.getTradeId(),OperationType.DELETE);
            // 发送Murex接口处理
            // this.sendMurex(entity, true);
        }
        //        调用接口  add by zhangpuyu 2019/12/6 start
        try {
            List<String> tradeIds = new ArrayList<>();
            tradeIds.add(entity.getTradeId());
            restTemplate.postForObject(
                    ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
                    Map.class);
        }catch (Exception e){

        }
       //        调用接口  add by zhangpuyu 2019/12/6 end
    }

    /**
     * 查询委托信息
     *
	 * @param entrustId
	 *            委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    public TrdStockDto getEntrustInfo(String entrustId) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdStock/getEntrustInfo/" + entrustId, null,
            TrdStockDto.class);
    }

    /**
     * 查询证券托管户：联想框文本框
     *
	 * @param parameters
	 *            画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getSecManageAcctId(Map<String, Object> parameters) {
        List<SuggestDto> tempList = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdStock/getSecManageAcctId", parameters, SuggestDto.class);

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
     * 获取下一个工作日
     *
	 * @param parameters
	 *            画面参数
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
	/*public String getNextWorkDay(Map<String, Object> parameters) {
		if (dateCalService != null) {
			Date tradeDate = DateUtil.parse((String) parameters.get("tradeDate"));
			List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDays(tradeDate, 31),
					(String) parameters.get("calendarId"));
			Date tradingDay = dateCalService.calculationTradingDay(tradeDate, 1, holiday, "1");
			return DateUtil.format(tradingDay);
		}

		return null;
	}*/

    /**
     * 通道：联想框文本框
     *
	 * @param parameters
	 *            画面参数
     * @return 通道：联想框文本框
     * @see
     * @since 1.0
     */
	/*public List<SelectOptionDto> getChannel(Map<String, Object> parameters) {
		List<SuggestDto> tempList = restTemplate.postForList(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdStock/getChannel", parameters,
				SuggestDto.class);

		List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
		if (!StringUtil.isEmptyList(tempList)) {
			for (SuggestDto suggestDto : tempList) {
				SelectOptionDto selectOptionDto = new SelectOptionDto();
				selectOptionDto.setId(suggestDto.getCode());
				selectOptionDto.setText(suggestDto.getName());
				// if (StringUtil.isNotBlank((String)
				// parameters.get("portfolioId"))) {
				// selectOptionDto.setParentId((String)
				// parameters.get("portfolioId"));
				// }
				resultList.add(selectOptionDto);
			}
		}

		return resultList;
	}*/


    /**
     * 1.8计算单位面值
     *
	 * @param finprodId
	 *            金融产品代码
	 * @param settleDate
	 *            交割日
     * @return 单位面值
     * @see
     * @since 1.0
     */
	/*public BigDecimal getUnitPrice(String finprodId, Date settleDate) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("finprodId", finprodId);
		parameters.put("settleDate", DateUtil.format(settleDate));
		return restTemplate.postForObject(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdStock/getUnitPrice", parameters,
				BigDecimal.class);
	}*/

    /**
     * 资产代码：下拉框
     *
	 * @param parameters
	 *            画面参数
     * @return 资产代码：下拉框
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodId(Map<String, Object> parameters) {
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/TrdStock/getFinprodId", parameters,
            SelectOptionDto.class);
    }

    /**
     * 股票交易新建画面查看模式查询股票交易信息
     *
	 * @param tradeId
	 *            交易编号
     * @return 股票投资交易信息
     * @see
     * @since 1.0
     */
    public TrdStockDto getTrdProductDealDto(String tradeId) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdStock/getTrdProductDealDto/" + tradeId,
            null, TrdStockDto.class);
    }

    /**
	 * 确认提醒 检查交割日是否为节假日，交割日如果是节假日 要提醒
	 * 交易方向为买入时校验交易所证券账户（券商存管账户）余额是否足够，如不足，提醒进行银证转账
     *
	 * @param inputDto
	 *            画面输入参数
	 * @param result
	 *            valid结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    public AjaxResult checkConfirmInfo(TrdStockDto inputDto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return null;
        }

        String confirmMsg = "";
        String changeLineMark = "<br/>";

        // 检查交割日是否为节假日，交割日如果是节假日 要提醒
        if (inputDto.getSettleDate() != null && StringUtil.isNotBlank(inputDto.getCalendarId())) {
            boolean isDealCalendarHoliday = dateCalService.isDealCalendarHoliday(inputDto.getSettleDate(),
                inputDto.getCalendarId());
            if (isDealCalendarHoliday) {
                if (StringUtil.isNotBlank(confirmMsg)) {
                    confirmMsg += changeLineMark;
                }
                confirmMsg += getMessage("BUSS_COM_E_0039", getMessage("TrdStockDealInvestTrade002.settleDate"));
            }
        }

        if ((StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), inputDto.getTradeMarket())
             || StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), inputDto.getTradeMarket()))
            && StringUtil.isBlank(inputDto.getChlId())) {
            // 交易方向为买入时校验交易所证券账户（券商存管账户）余额是否足够，如不足，提醒进行银证转账
            if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), inputDto.getPs())
                && StringUtil.isNotBlank(inputDto.getSecManageAcctId()) && inputDto.getTradeAmt() != null
                && bussTrdAvailableCashQueryService != null && !StringUtil.isEmptyList(inputDto.getBussTrdTransferOrder())) {
                // 查询网点账号
                StlSecAcctInfo stlSecAcctInfo = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                    StlSecAcctInfoMapper.class, inputDto.getSecManageAcctId(), StlSecAcctInfo.class);

                // 划款指令付款账户
                String 划款指令付款账户 = inputDto.getBussTrdTransferOrder().get(0).getPayAcctNo();

                if (stlSecAcctInfo != null && StringUtil.equals(划款指令付款账户, stlSecAcctInfo.getTrdAcctId())) {
                    // 调用基础组件.【组合可用现金和实际现金查询更新组件.查询现金余额(可用)】
                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    paramMap.put("portfolioId", inputDto.getPortfolioId());
                    paramMap.put("acctId", stlSecAcctInfo.getTrdAcctId());
                    paramMap.put("tradeDate", iPlatformDate.getSysDateStr());
                    paramMap.put("ccy", inputDto.getCcy());
                    paramMap.put("calendarId", inputDto.getCalendarId());
                    paramMap.put("acctType", DictCodeListExt1.资金账户类型_券商资金账户.getValue());
                    // paramMap.put("finprodId", inputDto.getFinprodId());

                    BigDecimal 余额 = bussTrdAvailableCashQueryService.getSecManageAvailableCash(paramMap);

                    if (inputDto.getTradeAmt().compareTo(余额) > 0) {
                        if (StringUtil.isNotBlank(confirmMsg)) {
                            confirmMsg += changeLineMark;
                        }
                        confirmMsg += getMessage("TRD_C_0001");
                    }
                }
            }
        }

        if (StringUtil.isNotEmpty(inputDto.getChlId()) && DictCodeListExt2.划款方式_一次性划款.getValue().equals(inputDto.getPayType())) {
            // 调用基础组件.【组合可用现金和实际现金查询更新组件.查询现金余额(可用)】
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("portfolioId", inputDto.getPortfolioId());
            paramMap.put("tradeDate", iPlatformDate.getSysDateStr());
            paramMap.put("ccy", inputDto.getCcy());
            paramMap.put("calendarId", inputDto.getCalendarId());
            paramMap.put("chlId", inputDto.getChlId());

            Map<String, String> resultMap = bussTrdAvailableCashQueryService.getAvailableCash(paramMap);
            BigDecimal 余额 = new BigDecimal(resultMap.get("T_2"));

            if (inputDto.getTradeAmt() != null && inputDto.getTradeAmt().compareTo(余额) > 0) {
                if (StringUtil.isNotBlank(confirmMsg)) {
                    confirmMsg += changeLineMark;
                }
                confirmMsg += getMessage("TRD_C_0004",getMessage("invest.channelId"));
            }
        }

        if (StringUtil.isNotBlank(confirmMsg)) {
            AjaxResult wr = new AjaxResult();
            wr.setData(confirmMsg);
            return wr;
        }

        return null;
    }

    /**
     * 判断某一天是否是债券行权日
     *
	 * @param finprodId
	 *            金融产品代码
	 * @param paramDate
	 *            判断日期
     * @return 判断结果
     * @see
     * @since 1.0
     */
    public boolean isExerciseDate(String finprodId, String paramDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("finprodId", finprodId);
        paramMap.put("paramDate", paramDate);

        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/isExerciseDate", paramMap,
            Boolean.class);
    }

    /**
     * @title: getProAvailableCash
     * @description: 组合可用现金查询
     * @author: junYin
     * @date: 2019/06/04 0:32
     * @param: * @param param 1
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @throws:
     */
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {

        Date vdate = DateUtil.parse((String)param.get("vdate"));
        String calId = (String)param.get("calId");
        if (StringUtil.isNotEmpty(calId)) {
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(vdate)),
                DateUtil.addDay(DateUtil.parse(DateUtil.format(vdate)), 15), calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(vdate)), 1, holidayList, "1");
            param.put("vdate1", DateUtil.format(tradeDay));
        }
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdStock/getProAvailableCash", param,
            Map.class);
    }

    /**
     * 通道可用现金查询
     * @param map
     * @return BigDecimal
     * @see
     *
     */
    public BigDecimal getTongDaoCash(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdStock/getTongDaoCash", map,
            BigDecimal.class);
    }

    /**
     * @title: getAssetAvailablePosition
     * @description: 组合可用持仓查询
     * @author: junYin
     * @date: 2019/06/04 0:32
     * @param: * @param dto 1
     * @return: java.math.BigDecimal
     * @throws:
     */
    public BigDecimal getAssetAvailablePosition(PtlSecPositionFlowDto dto) {
        Date sysDate = CacheUtil.getAppCache(GlobalConstant.NOW_SYS_DATE, Date.class);
        dto.setCdate(DateUtil.parse(DateUtil.format(sysDate)));
        dto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdStock/getAssetAvailablePosition", dto,
            BigDecimal.class);
    }

    /**
     * 委托代码联动带出画面必要信息
     * 
     * @param
     * @return 外汇掉期实体类
     * @see
     * @since 1.0
     */
    public TrdStockDto getDealInfoByEntrustId(String entrustId) {
        return restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdStock/getDealInfoByEntrustId?entrustId=" + entrustId, TrdStockDto.class);
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId)
        throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        TrdStockDto dto = this.getBussinessEntity(entity);
        // 将交易方向不是卖出的，都写成买入by赵东东 2019.8.26 start
        String ps = DictCodeList.交易方向_卖出.getValue();
        // 将代码中的字符串改掉by赵东东 2019.8.26 start
        if (!StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
            ps = DictCodeList.交易方向_买入.getValue();
            // 将代码中的字符串改掉by赵东东 2019.8.26 end
        }
        // 将交易方向不是卖出的，都写成买入by赵东东 2019.8.26 end
        if (taskId != null) {
            try {
//              return commonBusinessService.是否需要上级审批(dto.getCounterId(),
//                  ArithmeticUtil.multiply(dto.getShareAmt(), dto.getUnitCprice()), LoginUserUtil.getUserName(),
//                  DictCodeListExt2.平安资产类型_股票.getValue(), dto.getFinprodType(), dto.getTradeMarket(),
//                  // 将交易方向不是卖出的，都写成买入by赵东东 2019.8.26 start
//                  ps
//                  // 将交易方向不是卖出的，都写成买入by赵东东 2019.8.26 end
//                  // 资产类型改为授权类型 add by renping 2019-08-06 start
//                  , dto.getFinprodId()
//              // 资产类型改为授权类型 add by renping 2019-08-06 end
//              );
              Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), ArithmeticUtil.multiply(dto.getShareAmt(), dto.getUnitCprice()),LoginUserUtil.getUserName(),
                  DictCodeListExt2.平安资产类型_股票.getValue(), dto.getFinprodType(), dto.getTradeMarket(), ps
                  , dto.getFinprodId(),entity.getWorkflowId());
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
    }

    /**
     * 查询债券cstp交易下拉框
     *
     * @param param 输入参数
     * @return
     */
    public List<SuggestDto> queryRepoCstpInfoList(Map<String, Object> param) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdStock/queryRepoCstpInfoList", param,
            SuggestDto.class);
    }

    /**
     * 根据成交编号查询cstp交易信息
     * @param
     * @return cstp交易信息
     */
    public TrdStockDto querySecCstpInfoByNo(String fedealno) {
        TrdStockDto forObject = restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdStock/querySecCstpInfoByNo?fedealno=" + fedealno + "&loginUserName="
                                                          + LoginUserUtil.getUserName(),
            TrdStockDto.class);
        forObject.setTradeDateStr();
        forObject.setSettleDateStr();
        return forObject;
    }


	///流程退回
	@Override
	public void doBack(TrdStockDto entity) {
		AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTradeId());
		//卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
//		if (entity.getPs().equals(DictCodeList.交易方向_买入.getValue()) || entity.getPs().equals(DictCodeList.交易方向_申购.getValue())|| entity.getPs().equals(DictCodeListExt2.交易方向_新股中签.getValue())|| entity.getPs().equals(DictCodeListExt2.交易方向_配股.getValue())|| entity.getPs().equals(DictCodeListExt2.交易方向_增发.getValue())) {
//			//交易方向为买入时，退回动作后 释放原占用的额度  Add by xkni 20191023
//			isNeedOccupy(entity, true, true, true);
//		}
		//删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
	}


	///流程退回
	@Override
	public void doRevoke(TrdStockDto entity) {
		AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTradeId());
		//卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
//		if (entity.getPs().equals(DictCodeList.交易方向_买入.getValue()) || entity.getPs().equals(DictCodeList.交易方向_申购.getValue())|| entity.getPs().equals(DictCodeListExt2.交易方向_新股中签.getValue())|| entity.getPs().equals(DictCodeListExt2.交易方向_配股.getValue())|| entity.getPs().equals(DictCodeListExt2.交易方向_增发.getValue())) {
//			//交易方向为买入时，退回动作后 释放原占用的额度  Add by xkni 20191023
//			isNeedOccupy(entity, true, true, true);
//		}
		//删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
	}
}
