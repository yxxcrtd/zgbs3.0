/*
 * 文件名：TrdBondInvestTrade001Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：Zane.Y 修改时间：2018年9月19日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trdquery.service;

import com.google.common.collect.Lists;
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
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.system.base.dao.SysUploadFileInfoMapper;
import com.joyintech.system.base.entity.SysUploadFileInfo;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.cfets.manualsettle.dao.CfetsManualSettleMapper;
import com.joyintech.tams.cfets.manualsettle.dto.PortIntFenPeiInfoDto;
import com.joyintech.tams.cfets.trd.bond.service.CfetsBondDealQuotedPrice004Service;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.service.CfetsTrdBondFirstSub002Service;
import com.joyintech.tams.cfets.trdconfirm.dao.CfetsTrdConfirmMapper;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdConfirmMatchDto;
import com.joyintech.tams.cfets.trdquery.dao.CfetsTrdQueryMapper;
import com.joyintech.tams.cfets.trdquery.dto.CfetsTrdBondInvestTradeDto;
import com.joyintech.tams.cfets.trdquery.dto.CfetsTrdInvestPersonInfoDto;
import com.joyintech.tams.cfets.trdquery.dto.CfetsTrdQueryTradeInfoListResponse;
import com.joyintech.tams.cfets.trdquery.dto.CfetsTrdRepoTrade002Dto;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.component.business.trade.impl.BaseTradeProductDealCommServiceImpl;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.CommonResult;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.component.base.group.impl.dao.BasePtlCashPositionInfoMapper;
import com.joyintech.tams.invest.common.component.base.trade.BaseTradeProductDealService;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.BasicTradeFeeInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.cstp.service.CstpCommonService;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PtlAllocationDto;
import com.joyintech.tams.invest.common.dto.cstp.InterfTradSecdealDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.trade.service.PreTrdCommonBaseService;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.interf.service.InterfComponentService;
import com.joyintech.tams.invest.interf.service.InterfComponentService.OrderCaller;
import com.joyintech.tams.invest.trade.bond.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.trade.bond.dto.SecCpriceInfoDto;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeCSTPDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.invest.common.asset.service.BondAssetCalcService.*;

/**
 * 债券投资交易新建画面Service
 *
 * @author etliu
 * @version 1.0 2018年11月29日
 * @see CfetsTrdFirstBondInvestTrade002Service
 * @since 1.0
 */
@Service
public class CfetsTrdFirstBondInvestTrade002Service extends BaseService<CfetsTrdBondInvestTradeDto> implements BaseProcessService<CfetsTrdBondInvestTradeDto> {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CfetsTrdQueryMapper cfetsTrdQueryMapper;
    @Autowired
    private CfetsTrdConfirmMapper cfetsTrdConfirmMapper;
    /**
     * BussTrdTransferOrderService
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    @Autowired
    private BaseTradeProductDealService baseTradeProductDealService;

    @Autowired
    private TradeCommonService tradeCommonService;

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

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    /**
     * 共通Mapper的通用代理方案
     */
    @Autowired
    private MapperDelegateService mapperDelegateService;

    /**
     * 共通组件
     */
    @Autowired
    private CommonBusinessService commonBusinessService;

    // 调用投管接口 add by renping 20190205 start
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    // 调用投管接口 add by renping 20190205 end

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

    @Autowired(required = false)
    private FinProductMapper finProductMapper;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private InterfComponentService interfComponentService;

    /**
     * 费用查询
     */
    @Autowired(required = false)
    private BaseTradeFeeCommService baseTradeFeeCommService;

    /**
     * MultiApproveTradeCheckService
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;

    @Autowired(required = false)
    private SysUploadFileInfoMapper sysUploadFileInfoMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;

    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;

    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    private TrdTradeFeeTotalMapper trdTradeFeeTotalMapper;

    @Autowired
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;

    @Autowired
    private PaPtlPortfolioMapper paPtlPortfolioMapper;

    @Autowired
    private MstCounterPartyMapper mstCounterPartyMapper;

    @Autowired
    private InterfTradSecdealMapper interfTradSecdealMapper;

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;

    @Autowired
    private TrdProductDealPreMapper trdProductDealPreMapper;
    @Autowired
    private TrdProductDealAddPreMapper trdProductDealAddPreMapper;
    @Autowired
    private PreTrdCommonBaseService preTrdCommonBaseService;

    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    @Autowired
    private CstpCommonService cstpCommonService;

    @Autowired
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;

    @Autowired
    private EtrCommonCashPositionDealService etrCommonCashPositionDealService;

    @Autowired
    private BasePtlCashPositionInfoMapper basePtlCashPositionInfoMapper;

    @Autowired
    private EtrCashPositionFlowMapper etrCashPositionFlowMapper;

    @Autowired
    private PtlCashPositionFlowMapper ptlCashPositionFlowMapper;

    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;
    @Autowired(required = false)
    private BaseTradeProductDealCommServiceImpl baseTradeProductDealCommServiceImpl;
    /**
     * 流程组件
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;
    @Autowired
    private CfetsTrdBondFirstSub002Service cfetsTrdBondFirstSub002Service;

    @Autowired
    private CfetsManualSettleMapper cfetsManualSettleMapper;

    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;

    // 交易份额小数保留位数
    private int decimalPlaces_4 = 4;

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
     * @param dto 画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void prepareEntity(CfetsTrdBondInvestTradeDto dto) {
        setDealData(dto);

        // 汇总扩展表
        dto.setPaTrdFinProductDealTotal(生成汇总扩展表(dto));

        // 汇总附属表
        dto.setTrdDealTotalAdd(生成汇总附属表(dto));

        // 设置费用信息
        TrdTradeFeeTotal trdTradeFeeDetail = null;
        if (dto.getChannelFee() != null && dto.getChannelFee().compareTo(BigDecimal.ZERO) > 0) {
            trdTradeFeeDetail = AllTradeCommonUtil.genChannelTradeFeeDetailTotle(dto.getTotleTradeId(), dto.getChannelFee(),
                    dto.getCollectType(), dto.getIsOutTrade());
        }
        List<TrdTradeFeeTotal> ttfdList = getTrdTradeFeeDetails(dto);
        if (trdTradeFeeDetail != null) {
            ttfdList.add(trdTradeFeeDetail);
        }
        dto.setFeeAmt(get费用金额(ttfdList, null, null));
        dto.setFeeAmtPay(get费用金额(ttfdList, DictCodeList.是否_是.getValue(), null));
        dto.setFeeAmtUnpay(get费用金额(ttfdList, DictCodeList.是否_否.getValue(), null));
        dto.setTrdTradeFeeTotals(ttfdList);

        List<TrdProductDealAdd> trdProductDealAdds = new ArrayList<>();
        dto.setTrdProductDealAdds(trdProductDealAdds);
        List<PaTrdProductDeal> paTrdProductDeals = new ArrayList<>();
        List<PtlTrade> ptlTrades = new ArrayList<>();
        // 设置交易费用明细
        List<TrdTradeFeeDetail> trdTradeFeeDetails = new ArrayList<>();
        dto.setTrdTradeFeeDetails(trdTradeFeeDetails);

        // 指令汇总交易录入时，未区分本方券，这里做个判断，本方券设置成券面总额
        if (dto.getOwnFpriceAmt() == null) {
            dto.setOwnFpriceAmt(dto.getFpriceAmt());
        }

        if (dto.getOwnIntAmt() == null) {
            dto.setOwnIntAmt(dto.getIntAmt());
        }

        if (dto.getOwnTradeAmt() == null) {
            dto.setOwnTradeAmt(dto.getTradeAmt());
        }

        // 设置交易表
        List<TrdProductDeal> deals = new ArrayList<>();
        dto.setTrdProductDeals(deals);
        TrdProductDeal deal本方 = null;
        if (
            /* 本方券券面总额大于0，登录本方券的交易 */
                dto.getOwnTradeAmt() != null && dto.getOwnTradeAmt().compareTo(BigDecimal.ZERO) > 0) {
            deal本方 = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal本方, dto);
            // 清算速度
            deal本方.setTradeId(baseGetBusinessId.buildBussinessCode(getBussinessCodeParameter(deal本方.getPs())));
            deal本方.setCpriceAmt(ArithmeticUtil.subtract(dto.getOwnFpriceAmt(), dto.getOwnIntAmt()));
            // 交易本金 = 画面.净价总额
            deal本方.setPrinAmt(ArithmeticUtil.subtract(dto.getOwnFpriceAmt(), dto.getOwnIntAmt()));
            // 交易份额 = 画面.券面总额/100
            deal本方.setShareAmt(ArithmeticUtil.divide(dto.getOwnTradeAmt(), new BigDecimal(100), decimalPlaces_4, RoundingMode.HALF_UP));
            deal本方.setIntAmt(dto.getOwnIntAmt());
            deal本方.setFpriceAmt(dto.getOwnFpriceAmt());
            //占比 = 本方券面总额/(本方券面总额+融入券总额)
            BigDecimal 占比 = ArithmeticUtil.divide(dto.getOwnTradeAmt(),
                    ArithmeticUtil.doAdd(dto.getOwnTradeAmt(), dto.getIntegrateTradeAmt()), 12);
            deal本方.setFeeAmt(get费用金额(ttfdList, null, 占比));
            deal本方.setFeeAmtPay(get费用金额(ttfdList, DictCodeList.是否_是.getValue(), 占比));
            deal本方.setFeeAmtUnpay(get费用金额(ttfdList, DictCodeList.是否_否.getValue(), 占比));
            deal本方.setTradeAmt(get交易金额(dto.getOwnFpriceAmt(), dto.getPs(), deal本方.getFeeAmtPay()));
            deal本方.setSettleSpeed(Long.parseLong(dto.getSettleSpeedStr()));
            deals.add(deal本方);

            // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
            TrdProductDealAdd trdProductDealAdd本方 = new TrdProductDealAdd();
            trdProductDealAdd本方.setTradeId(deal本方.getTradeId()); // 交易编号
            trdProductDealAdd本方.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
            trdProductDealAdd本方.setEntrustId(dto.getEntrustId()); // 委托代码
            // trdProductDealAdd.setProdId(dto.getFinprodId()); // 理财产品代码
            trdProductDealAdd本方.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
            trdProductDealAdd本方.setWorkflowId(dto.getWorkflowId()); // 工作流代码
            // 推荐方 by zhuyongkang 20191023 start
            trdProductDealAdd本方.setAssetRecommandOrg(dto.getAssetRecommandOrg());
            // 推荐方 by zhuyongkang 20191023 start
            trdProductDealAdds.add(trdProductDealAdd本方);

            // 组合交易表Entity 构造开始----------------------------------------------------------
            生成组合交易(dto, deal本方.getTradeId(), ptlTrades);

            // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
            生成交易扩展表(dto, dto.getTrdInvestPersonInfo(), deal本方.getTradeId(), paTrdProductDeals);


            trdTradeFeeDetails.addAll(
                    生成交易费用明细数据(ttfdList, deal本方, 占比, null));
        }

        if (checkIfNeed融入券(dto)) {
            TrdProductDeal deal融入 = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal融入, dto);
            deal融入.setTradeId(baseGetBusinessId.buildBussinessCode(getBussinessCodeParameter(deal融入.getPs())));
            deal融入.setCpriceAmt(ArithmeticUtil.subtract(dto.getIntegrateFpriceAmt(), dto.getIntegrateIntAmt()));
            // 交易本金 = 画面.净价总额
            deal融入.setPrinAmt(ArithmeticUtil.subtract(dto.getIntegrateFpriceAmt(), dto.getIntegrateIntAmt()));
            // 交易份额 = 画面.券面总额/100
            deal融入.setShareAmt(ArithmeticUtil.divide(dto.getIntegrateTradeAmt(), new BigDecimal(100), decimalPlaces_4, RoundingMode.HALF_UP));
            deal融入.setIntAmt(dto.getIntegrateIntAmt());
            deal融入.setFpriceAmt(dto.getIntegrateFpriceAmt());
            deal融入.setFeeAmt(ArithmeticUtil.subtract(dto.getFeeAmt(), deal本方 == null ? null : deal本方.getFeeAmt()));
            deal融入.setFeeAmtPay(ArithmeticUtil.subtract(dto.getFeeAmtPay(), deal本方 == null ? null : deal本方.getFeeAmtPay()));
            deal融入.setFeeAmtUnpay(ArithmeticUtil.subtract(dto.getFeeAmtUnpay(), deal本方 == null ? null : deal本方.getFeeAmtUnpay()));
            deal融入.setTradeAmt(get交易金额(dto.getIntegrateFpriceAmt(), dto.getPs(), deal融入.getFeeAmtPay()));
            deal融入.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
            /** modify by yanya on 20210106 融入赋值清算速度错误**/
            //deal本方.setSettleSpeed(Long.parseLong(dto.getSettleSpeedStr()));
            deal融入.setSettleSpeed(Long.parseLong(dto.getSettleSpeedStr()));
            deals.add(deal融入);

            trdTradeFeeDetails.addAll(生成交易费用明细数据(ttfdList, deal融入, null, trdTradeFeeDetails));

            TrdProductDealAdd trdProductDealAdd融入 = new TrdProductDealAdd();
            trdProductDealAdd融入.setTradeId(deal融入.getTradeId()); // 交易编号
            trdProductDealAdd融入.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
            trdProductDealAdd融入.setEntrustId(dto.getEntrustId()); // 委托代码
            // trdProductDealAdd.setProdId(dto.getFinprodId()); // 理财产品代码
            trdProductDealAdd融入.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
            trdProductDealAdd融入.setWorkflowId(dto.getWorkflowId()); // 工作流代码
            // 推荐方 by zhuyongkang 20191023 start
            trdProductDealAdd融入.setAssetRecommandOrg(dto.getAssetRecommandOrg());
            // 推荐方 by zhuyongkang 20191023 start
            trdProductDealAdds.add(trdProductDealAdd融入);

            生成组合交易(dto, deal融入.getTradeId(), ptlTrades);

            生成交易扩展表(dto, dto.getTrdInvestPersonInfo(), deal融入.getTradeId(), paTrdProductDeals);
        }
        // 交易编号、融产品代码、金融产品类型（投管分类）、证券管理户代码、关联系统交易编号、投资目的、交易日、
        // 起息日、到期日、交割日、单位净价、单位利息、单位全价、净价总额、交易场所、交易市场、交易平台、交易币种、交易方向、利息总额、全价总额、备注
        // 以上信息为画面上项目，不需要特殊设置
        // 金融产品交易Entity 构造结束----------------------------------------------------------

        dto.setPtlTrade(ptlTrades);
        // 组合交易表Entity 构造结束----------------------------------------------------------

        // 划款指令表Entity 构造开始----------------------------------------------------------
        if (bussTrdTransferOrderService != null) {
            // 调用划款指令共通业务组件的保存处理，根据输出结果加工形成金融产品交易DTO的要素数据
            if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getTotleTradeId()); // 交易编号
                dto.getBussTrdTransferOrder().get(0).setOrderStatus(DictCodeListExt1.指令状态_新建.getValue()); // 指令状态
                dto.getBussTrdTransferOrder().get(0).setWorkflowId(dto.getWorkflowId()); // 工作流代码
                dto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            }
        }
        dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getTotleTradeId());
        // 划款指令表Entity 构造结束----------------------------------------------------------

        dto.setPaTrdProductDeals(paTrdProductDeals);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
    }

    public void setDealData(CfetsTrdBondInvestTradeDto dto) {
        // 券面总额
        dto.setTotalFaceValue(dto.getTradeAmt());
        // add by jiangpeng on 20210622 自动加券的债券id没有"F01_"的前缀 TISHKBS-2200
        if (dto.getFinprodId().indexOf("F01_") != 0) {
            dto.setFinprodId("F01_" + dto.getFinprodId());
        }
        // add by jiangpeng on 20210622 自动加券的债券id没有"F01_"的前缀 TISHKBS-2200
        // 金融产品交易Entity 构造开始----------------------------------------------------------
        // 业务类型
        // 自动生成反向交易 chenzhili start
        dto.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        // 自动生成反向交易 chenzhili end
        // 金融产品类型（估值核算）
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);
        dto.setBranch(branchLong);
        // 单位面值
        dto.setParValue(this.getUnitPrice(dto.getFinprodId(), dto.getSettleDate()));
        // 交易本金 = 画面.净价总额
        dto.setPrinAmt(dto.getCpriceAmt());
        // 交易份额 = 画面.券面总额/100
        dto.setShareAmt(ArithmeticUtil.divide(dto.getTotalFaceValue(), new BigDecimal(100), decimalPlaces_4, RoundingMode.HALF_UP));
        // modified by wangguoxian 费用设置 20190801 start
        // 交易金额 = 画面.交易金额
        dto.setTradeAmt(dto.getTradeAmtAndFee());
        // ---add by 裴家俊-------2019/03/29--------start---------
        /*
         * if(StringUtil.equals(dto.getTradeMarket(), BussinessDictCodeList.交易场所_上海证券交易所.getValue())||StringUtil.equals(dto.getTradeMarket(),
         * BussinessDictCodeList.交易场所_深圳证券交易所.getValue())){ if(StringUtil.isNotEmpty(dto.getCommissionAmt())){ if(StringUtil.equals(dto.getPs(),
         * BussinessDictCodeList.交易方向_买入.getValue())){ dto.setTradeAmt(ArithmeticUtil.doAdd(dto.getFpriceAmt(),dto.getCommissionAmt()));//全价加上佣金之后为交易金额 }
         * if(StringUtil.equals(dto.getPs(), BussinessDictCodeList.交易方向_卖出.getValue())){
         * dto.setTradeAmt(ArithmeticUtil.doSub(dto.getFpriceAmt(),dto.getCommissionAmt()));//全价减去佣金之后为交易金额 } } }
         */
        // ---add by 裴家俊-------2019/03/29--------end---------
        // modified by wangguoxian 费用设置 20190801 end
        // 到期收益率 = 画面.到期收益率/100
        if (dto.getYield() != null) {
            dto.setYield(ArithmeticUtil.divide(dto.getYield(), new BigDecimal(100), 14));
        }
        // 行权收益率 = 画面.行权收益率/100
        if (dto.getExerYield() != null) {
            dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), new BigDecimal(100), 14));
        }
        // 是否结清 = 数据字典缓存.是否.是
        dto.setIsClean(DictCodeList.是否_是.getValue());
        dto.setIsCancel(DictCodeList.是否_否.getValue());

        CfetsTrdInvestPersonInfoDto personInfo = dto.getTrdInvestPersonInfo();
        if (personInfo != null) {
            // 本方交易员
            dto.setTrader(personInfo.getTrader());
            // 交易对手
            dto.setCounterId(personInfo.getCounterId());
            // 是否外部交易
            // dto.setIsOutTrade(personInfo.getIsOutTrade());
            if (StringUtil.equals(DictCodeList.是否_否.getValue(), personInfo.getIsOutTrade())) {
                dto.setIsOutTrade(personInfo.getIsOutTrade());
            } else {
                dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            }
            // 对手方交易员
            dto.setCounterTrader(personInfo.getCounterTrader());
            // 联系方式
            dto.setCounterContact(personInfo.getContactInfo());
        }

        dto.setTgBusiType(getPaBussType(dto.getPs()));
        if (dto.getIntegrateTradeAmt() == null) {
            dto.setIntegrateTradeAmt(BigDecimal.ZERO);
        }
    }

    /**
     * 风险校验（硬控）按照交易金额计算
     *
     * @param dto
     */
    public void setDealDataForValid(CfetsTrdBondInvestTradeDto dto) {
        BigDecimal tradeAmt = dto.getTradeAmt();
        setDealData(dto);
        dto.setTradeAmt(tradeAmt);
    }

    private List<TrdTradeFeeDetail> 生成交易费用明细数据(List<TrdTradeFeeTotal> 汇总交易费用信息, TrdProductDeal 交易数据, BigDecimal 占比,
                                               List<TrdTradeFeeDetail> 已生成费用) {
        if (汇总交易费用信息 == null || 汇总交易费用信息.isEmpty()) {
            return Lists.newArrayList();
        }
        List<TrdTradeFeeDetail> result = new ArrayList<>();
        if (占比 != null) {
            汇总交易费用信息.forEach(trdTradeFeeTotal -> {
                TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
                BeanUtils.copyProperties(trdTradeFeeTotal, trdTradeFeeDetail);
                trdTradeFeeDetail.setTradeId(交易数据.getTradeId());
                trdTradeFeeDetail.setFeeAmt(ArithmeticUtil.round(
                        ArithmeticUtil.multiply(trdTradeFeeDetail.getFeeAmt(), ArithmeticUtil.round(占比, 12, RoundingMode.HALF_UP)), 2,
                        RoundingMode.HALF_UP));
                result.add(trdTradeFeeDetail);
            });
        } else if (已生成费用 != null && !已生成费用.isEmpty()) {
            汇总交易费用信息.forEach(trdTradeFeeTotal -> {
                TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
                BeanUtils.copyProperties(trdTradeFeeTotal, trdTradeFeeDetail);
                trdTradeFeeDetail.setTradeId(交易数据.getTradeId());
                已生成费用.forEach(trdTradeFeeDetail1 -> {
                    if (trdTradeFeeTotal.getFeeType().equals(trdTradeFeeDetail1.getFeeType())) {
                        trdTradeFeeDetail.setFeeAmt(
                                ArithmeticUtil.subtract(trdTradeFeeDetail.getFeeAmt(), trdTradeFeeDetail1.getFeeAmt()));
                    }
                });
                result.add(trdTradeFeeDetail);
            });
        }
        return result;
    }

    private TrdDealTotalAdd 生成汇总附属表(CfetsTrdBondInvestTradeDto dto) {
        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
        trdDealTotalAdd.setTotleTradeId(dto.getTotleTradeId());
        trdDealTotalAdd.setOurShareAmt(ArithmeticUtil.divide(dto.getOwnTradeAmt(), 100, 4));
        trdDealTotalAdd.setLendinShareAmt(ArithmeticUtil.divide(dto.getIntegrateTradeAmt(), 100, 4));
        trdDealTotalAdd.setOurFpriceAmt(dto.getOwnFpriceAmt());
        trdDealTotalAdd.setLendinFpriceAmt(dto.getIntegrateFpriceAmt() == null ? BigDecimal.ZERO : dto.getIntegrateFpriceAmt());
        trdDealTotalAdd.setOurCpriceAmt(ArithmeticUtil.subtract(dto.getCpriceAmt(), dto.getIntegrateCpriceAmt()));
        trdDealTotalAdd.setLendinCpriceAmt(dto.getIntegrateCpriceAmt() == null ? BigDecimal.ZERO : dto.getIntegrateCpriceAmt());
        trdDealTotalAdd.setOurFpriceAmt2(dto.getOwnIntAmt());
        trdDealTotalAdd.setLendinFpriceAmt2(dto.getIntegrateIntAmt() == null ? BigDecimal.ZERO : dto.getIntegrateIntAmt());
        trdDealTotalAdd.setAssetRecommandOrg(dto.getAssetRecommandOrg());
        trdDealTotalAdd.setEntrustId(dto.getEntrustId());
        return trdDealTotalAdd;
    }

    private BigDecimal get交易金额(BigDecimal 交易金额, String 交易方向, BigDecimal 费用) {
        if (DictCodeList.交易方向_买入.getValue().equals(交易方向)) {
            return ArithmeticUtil.doAdd(交易金额, 费用);
        } else if (DictCodeList.交易方向_卖出.getValue().equals(交易方向)) {
            return ArithmeticUtil.subtract(交易金额, 费用);
        } else if (DictCodeList.交易方向_行权_回售.getValue().equals(交易方向)) {
            return ArithmeticUtil.subtract(交易金额, 费用);
        } else {
            return 交易金额;
        }
    }

    private BigDecimal get费用金额(List<TrdTradeFeeTotal> ttfdList, String 是否随交易支付, BigDecimal 占比) {
        BigDecimal result = BigDecimal.ZERO;
        for (TrdTradeFeeTotal trdTradeFeeDetail : ttfdList) {
            if (是否随交易支付 == null || 是否随交易支付.equals(trdTradeFeeDetail.getIsPayWithTrade())) {
                result = ArithmeticUtil.doAdd(result, trdTradeFeeDetail.getFeeAmt());
            }
        }
        if (占比 != null) {
            result = ArithmeticUtil.round(ArithmeticUtil.multiply(result, ArithmeticUtil.round(占比, 12, RoundingMode.HALF_UP)), 2,
                    RoundingMode.HALF_UP);
        }
        return result;
    }

    private PaTrdFinProductDealTotal 生成汇总扩展表(CfetsTrdBondInvestTradeDto dto) {
        PaTrdFinProductDealTotal productDealTotal = new PaTrdFinProductDealTotal();
        productDealTotal.setTotleTradeId(dto.getTotleTradeId());
        productDealTotal.setPaBussType(getPaBussType(dto.getPs()));
        return productDealTotal;
    }

    private boolean checkIfNeed融入券(CfetsTrdBondInvestTradeDto dto) {
        return (DictCodeList.交易方向_买入.getValue().equals(dto.getPs()) || DictCodeList.交易方向_卖出.getValue().equals(dto.getPs()))
                && dto.getIntegrateTradeAmt() != null && dto.getIntegrateTradeAmt().compareTo(BigDecimal.ZERO) > 0
                && StringUtil.equals("01", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
    }

    private void 生成交易扩展表(CfetsTrdBondInvestTradeDto dto, CfetsTrdInvestPersonInfoDto personInfo, String 交易编号,
                         List<PaTrdProductDeal> paTrdProductDeals) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(交易编号); // 交易编号
        paTrdProductDeal.setPaBussType(getPaBussType(dto.getPs()));
        paTrdProductDeal.setDeptCode(personInfo.getTraderDept()); // 交易所属部门
        paTrdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        paTrdProductDeals.add(paTrdProductDeal);
    }

    private String getPaBussType(String 交易方向) {
        // 平安业务类型(数据字典缓存.平安业务类型（按交易方向取对应值）)
        if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), 交易方向)) {
            return DictCodeListExt1.平安业务类型_债券类资产买入.getValue();
        } else if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), 交易方向)) {
            return DictCodeListExt1.平安业务类型_债券类资产卖出.getValue();
        }
        // 一体化对应etliu start
        // else if (StringUtil.equals(DictCodeList.交易方向_行权.getValue(), 交易方向)) {
        else if (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), 交易方向)
                || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), 交易方向)) {
            // 一体化对应etliu end
            return DictCodeListExt1.平安业务类型_债券类资产行权.getValue();
        }
        return null;
    }

    private void 生成组合交易(CfetsTrdBondInvestTradeDto dto, String 交易编号, List<PtlTrade> ptlTrades) {
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId()); // 组合代码
        ptlTrade.setBusiNo(交易编号); // 业务编号
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
        ptlTrade.setBusiId(dto.getFinprodId()); // 业务明细代码
        ptlTrade.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue()); // 业务类型
        ptlTrade.setInvAim(dto.getInvAim()); // 投资目的
        ptlTrade.setSecManageAcctId(dto.getSecManageAcctId()); // 证券托管户代码
        ptlTrade.setSettleDate(dto.getSettleDate()); // 实际发生日
        ptlTrade.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        ptlTrades.add(ptlTrade);

        if (StringUtil.isNotEmpty(dto.getActPortfolioId()) && !StringUtil.equals(dto.getPortfolioId(), dto.getActPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            BeanUtil.copyPropertiesIgnoreNull(ptlTrade1, ptlTrade);
            ptlTrade1.setPortfolioId(dto.getActPortfolioId());
            ptlTrades.add(ptlTrade1);
        }
    }

    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(CfetsTrdBondInvestTradeDto entity) {
        // 多组合交易时对附件进行处理
        //setDzhUploadFileList(entity);

        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));

        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, entity);
        wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflTrdFinProductDealTotal.setPortfolioId(entity.getActPortfolioId());
        wflTrdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        wflTrdFinProductDealTotal.setSettleSpeed(Long.parseLong(entity.getSettleSpeedStr()));
        wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);

        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, entity.getPaTrdFinProductDealTotal());
        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);

        WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, entity.getTrdDealTotalAdd());
        wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);

        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        for (TrdProductDeal trdProductDeal : entity.getTrdProductDeals()) {
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, trdProductDeal);
            wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            wflTrdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
            wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);

            // 调用授信共同 sjj start
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            inputDto.setTrdProductDeal(trdProductDeal);
            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, true);
            // 调用授信共同 sjj end

            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            finProductTradeDto.setTotalFaceValue(entity.getTotalFaceValue());// 劵面总额
            finProductTradeDto.setNeedSum(true);
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
            entity.getTrdProductDealAdds().forEach(trdProductDealAdd -> {
                if (trdProductDeal.getTradeId() != null && trdProductDeal.getTradeId().equals(trdProductDealAdd.getTradeId())) {
                    baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);
                }
            });

            if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())
                    && !DictCodeListExt1.投资目的_卖空.getValue().equals(trdProductDeal.getInvAim())) {
                baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(entity.getBussTrdTransferOrder().get(0)));
            }
            // 委托代码不为空设置委托编号
            if (StringUtil.isNotEmpty(entity.getEntrustId())) {
                baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
            }
            baseTradeProductDealDto.setBondFirstSign(entity.getBondFirstSign());
            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            if (!StringUtil.equals(entity.getIsOfferFlag(), "01")) {//非报价信息
                String tradeId = trdProductDeal.getTradeId();
                trdProductDeal.setTradeId(entity.getTotleTradeId()); // 让生成的组合流水的tradeId是汇总表的值
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
                trdProductDeal.setTradeId(tradeId);
            }
        }

        entity.getTrdProductDealAdds().forEach(trdProductDealAdd -> {
            // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, trdProductDealAdd);
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            // 推荐方 by zhuyongkang 20191023 start
            wflTrdProductDealAdd.setAssetRecommandOrg(entity.getAssetRecommandOrg());
            // 推荐方 by zhuyongkang 20191023 start
            wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
            wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        });

        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        entity.getPaTrdProductDeals().forEach(paTrdProductDeal -> {
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, paTrdProductDeal);
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            // 批量插入
            wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
        });

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, entity.getBussTrdTransferOrder().get(0));
            wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
        }
    }

    public void setEntity(List<CfetsTrdBondInvestTradeDto> entitys) {
        entitys.forEach(entity -> {
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            if (StringUtil.isNotEmpty(entity.getBussTrdTransferOrder().get(0))) {
                entity.getBussTrdTransferOrder().get(0).setWorkflowId(entity.getWorkflowId());
            }

            this.prepareEntity(entity);

            entity.setPortfolioId(entity.getActPortfolioId());
        });
    }

    @DataSource(DataSourceType.OTHER)
    @DTXTransaction
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void setDzhUploadFileList(CfetsTrdBondInvestTradeDto entity) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())) {
            for (UploadFile file : entity.getUploadFileList()) {
                SysUploadFileInfo sysUploadFileInfo = sysUploadFileInfoMapper.selectByPrimaryKey(file.getFileNo());
                SysUploadFileInfo fileInfo = new SysUploadFileInfo();
                String uuid = UUID.randomUUID().toString().replace("-", "");
                fileInfo.setSeqNo(uuid);
                fileInfo.setFileName(sysUploadFileInfo.getFileName());
                fileInfo.setPath(sysUploadFileInfo.getPath());
                fileInfo.setBusinessId(entity.getTotleTradeId());
                fileInfo.setCreateTime(sysUploadFileInfo.getCreateTime());
                fileInfo.setCreateUser(sysUploadFileInfo.getCreateUser());
                fileInfo.setUpdateTime(sysUploadFileInfo.getUpdateTime());
                fileInfo.setUpdateUser(sysUploadFileInfo.getCreateUser());
                sysUploadFileInfoMapper.insert(fileInfo);
            }
        }
    }

    /*
     * private void setChannelFee(CfetsTrdBondInvestTradeDto entity) {
     *
     * if (DictCodeList.交易方向_买入.getValue().equals(entity.getPs())) { entity.setTradeAmt(ArithmeticUtil.doAdd(entity.getTradeAmt(),
     * entity.getChannelFee())); } else { entity.setTradeAmt(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getChannelFee())); }
     * entity.setFeeAmtPay(ArithmeticUtil.doAdd(entity.getFeeAmtPay(), entity.getChannelFee())); entity.setFeeAmt(ArithmeticUtil.doAdd(entity.getFeeAmt(),
     * entity.getChannelFee())); }
     */

    /**
     * 删除业务流程数据
     *
     * @param workflowId 工作流代码
     * @see
     * @since 1.0
     */
    private void deleteBussFlowData(String workflowId) {
        wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        wflPaTrdFinDealTotalMapper.deleteByWorkflowId(workflowId);
        wflTrdDealTotalAddMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealMapper.deleteByWorkflowId(workflowId);
        wflPaTrdProductDealMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealAddMapper.deleteByWorkflowId(workflowId);
        wflStlPayOrdersMapper.deleteByWorkflowId(workflowId);
    }

    /**
     * 7.3写入交易信息进交易报送信息表，为中债报送用
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void dealInfoCbReportLogic_7_3(CfetsTrdBondInvestTradeDto dto) {
        // 7.7写入交易信息进交易报送信息表，为中债报送用
        // 1)查询产品登记编码
        String prodRegistCode = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectProdRegistCode/" + dto.getPortfolioId(), null,
                String.class);

        if (StringUtil.isBlank(prodRegistCode)) {
            return;
        }

        // 2）写入交易报送信息
        // 交易报送信息Entity
        RepTrade repTrade = new RepTrade();
        repTrade.setRepFinprodRegId(prodRegistCode); // 产品登记编码
        repTrade.setRepBankTradeCode(dto.getTotleTradeId()); // 行内交易编码
        // 行内资产/负债编码
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
        if (finProduct != null) {
            repTrade.setRepBankAssetCode(finProduct.getFinprodMarketId());
        }
        repTrade.setRepCcy(dto.getCcy()); // 币种
        repTrade.setRepOccurMoney(dto.getFpriceAmt()); // 发生金额

        // 折算人民币金额（元） = 画面.全价总额*收盘价(利用画面.币种与交易日查询汇率行情表中最新的收盘价)
        BigDecimal 收盘价 = commonBusinessService.selectClosePrice(dto.getCcy(), DateUtil.format(dto.getTradeDate()));
        BigDecimal 折算人民币金额 = ArithmeticUtil.multiply(dto.getFpriceAmt(), 收盘价);
        repTrade.setRepMoneyCny(折算人民币金额);
        // 数量 = 画面.券面总额
        repTrade.setRepCount(dto.getTotalFaceValue());

        // 资金流动类型: 数据字典缓存.资金流动类型 ※根据交易方向判断，若买入，即资产买入，若卖出，即为资产卖出或到期
        String 资金流动类型 = DictCodeListExt2.资金流动类型_资产卖出或到期.getValue();
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
            资金流动类型 = DictCodeListExt2.资金流动类型_资产买入.getValue();
        }
        repTrade.setRepMoneyFlowType(资金流动类型);

        repTrade.setRepTradeDate(dto.getTradeDate()); // 交易日
        repTrade.setRepTradeComponent(dto.getCounterId()); // 交易对手方
        repTrade.setRepUnitPriceFull(dto.getUnitFprice()); // 单位成交价格（全价）
        repTrade.setRepUnitPriceNet(dto.getUnitCprice()); // 单位成交价格（净价）
        repTrade.setRepExpireYield(dto.getYield().multiply(new BigDecimal(100))); // 到期收益率%
        if (StringUtil.isNotEmpty(dto.getfTradeId())) {
            repTrade.setRepDelCode(dto.getfTradeId()); // 成交编号/合同号
        } else {
            repTrade.setRepDelCode(dto.getTotleTradeId()); // 成交编号/合同号
        }
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue()); // 报送状态
        repTrade.setRepStatusDate(iPlatformDate.getSysDate()); // 数据日期
        repTrade.setRepRemark(dto.getRemark()); // 备注

        // 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepTrade",
                Arrays.asList(repTrade), Boolean.class);
    }

    /**
     * 7.4更新资产负债报送状态 利用金融产品代码作为条件把资产负债报送信息表中的报送状态更改为是
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void assetDebtCbReportLogic_7_4(CfetsTrdBondInvestTradeDto dto) {
        // 利用金融产品代码作为条件把资产负债报送信息表中的报送状态更改为是
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/updateRepAssetDebt",
                dto.getFinprodId(), Boolean.class);
    }

    /**
     * 撤销交易doInsert
     *
     * @param entity 参数entity
     * @see
     * @since 1.0
     */
    private void doRevokeInsert(CfetsTrdBondInvestTradeDto entity) {
        Map<String, Object> params = new HashMap<>();
        if (entity.getTotleTradeId().lastIndexOf('R') == entity.getTotleTradeId().length() - 1) {
            entity.setTotleTradeId(entity.getTotleTradeId().substring(0, entity.getTotleTradeId().length() - 1));
        }
        params.put("bizCode", entity.getTotleTradeId());
        params.put("workflowId", entity.getWorkflowId());
        params.put("workflowStatus", DictCodeList.数据状态_删除中.getValue());
        params.put("user", SecurityUtil.getLoginUser().getUserName());
        params.put("dept", SecurityUtil.getLoginUser().getDeptCode() == null ? "YHZGB" : SecurityUtil.getLoginUser().getDeptCode());
        params.put("revokeReason", entity.getRevokeReason());
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/doRevokeInsertTotle", params,
                String.class);
    }

    /**
     * 一级申购撤销doComplete //TODO
     *
     * @param entity 参数
     * @see
     * @since 1.0
     */
    public void doCompleteBidRevoke(CfetsTrdBondInvestTradeDto entity, String settleRemark, Boolean isNeedDealPosition) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        if (entity.getTotleTradeId().lastIndexOf('R') == entity.getTotleTradeId().length() - 1) {
            entity.setTotleTradeId(entity.getTotleTradeId().substring(0, entity.getTotleTradeId().length() - 1));
        }

        List<String> totleIdList1 = Arrays.asList(entity.getTotleTradeId());
        List<String> totleIdList = new ArrayList(totleIdList1);

        List<BaseTradeProductDealDto> baseTradeProductDealDtos = baseTradeProductDealService.selectTotalDeal(totleIdList);

        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        for (BaseTradeProductDealDto baseTradeProductDealDto : baseTradeProductDealDtos) {
            TrdProductDeal trdProductDeal = baseTradeProductDealDto.getTrdProductDeal();
            String cetradeid = trdProductDeal.getTradeId();
            trdProductDeal.setTradeId(trdProductDeal.getTradeId() + 'R');
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());

            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            finProductTradeDto.setTotalFaceValue(entity.getTotalFaceValue());// 劵面总额
            finProductTradeDto.setNeedSum(true);


            // 委托代码不为空设置委托编号
            if (StringUtil.isNotEmpty(entity.getEntrustId())) {
                baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
            }

            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
            baseTradeProductDealDto.setActPortfolioId(entity.getActPortfolioId());

            trdProductDeal.setCreateUser(finProductTradeDto.getCreateUser());
            trdProductDeal.setCreateTime(finProductTradeDto.getCreateTime());
            trdProductDeal.setCreateDept(finProductTradeDto.getCreateDept());
            trdProductDeal.setUpdateTime(finProductTradeDto.getUpdateTime());
            trdProductDeal.setUpdateUser(finProductTradeDto.getUpdateUser());

            //          一级投标信息不为空
            if (StringUtil.isNotEmpty(entity.getBondFirstSign())) {
                baseTradeProductDealDto.setBondFirstSign(entity.getBondFirstSign());
            }
            String tradeId = trdProductDeal.getTradeId();
            trdProductDeal.setTradeId(entity.getTotleTradeId()); // 让生成的组合流水的tradeId是汇总表的值
            baseTradeCommExecute(finProductTradeDto);

            // 调用投管接口 add by renping 20190205 start
//            InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
//            investInterfCommDto.setPageId("CfetsTrdBondInvestTrade002");
//            investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
//            investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
//            investInterfCommDto.setRevokeReason(entity.getRevokeReason());
//            investInterfCommDto.setTrdProductDeal(trdProductDeal);
//            investInterfCommDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
//            if (!StringUtil.isEmptyList(baseTradeProductDealDto.getStlPayOrdersList())) {
//                investInterfCommDto.setStlPayOrders(
//                        baseTradeProductDealDto.getStlPayOrdersList().get(0));
//            }
//            investInterfCommDto.setTradeFeeDetaillist(
//                    baseTradeProductDealDto.getTrdTradeFeeDetailList());
//            investInterfCommService.doInvestInterf(investInterfCommDto);
            // 调用投管接口 add by renping 20190205 end
            trdProductDeal.setTradeId(tradeId);

            //删除授信占用
//            riskCreditListCommonValidatorService.deleteCredit(cetradeid);

            //插入头寸
            if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
                if (baseTradeProductDealDto.getTrdProductDealAdd() != null &&
                        StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDealAdd().getTradeId())) {
//                    String orderId = cfetsManualSettleMapper.selectRegTradeIdBytradeId(baseTradeProductDealDto.getTrdProductDealAdd().getTradeId());
//                    if (StringUtil.isNotEmpty(orderId)) {
//                        AllTradeCommonUtil.deleteRegPosition(null, orderId);
//                    }
                }

            }
        }
        // add by etliu 20210927 start
        if (entity != null) {
            if (StringUtil.isBlank(entity.getoFinprodId())
                    && StringUtil.equals(DictCodeListExt1.交易市场_一级市场.getValue(), entity.getTradeMarketMode())) {
                FinProduct finProduct = tradeCommonService.selectFinProduct(entity.getFinprodId());
                if (finProduct != null) {
                    entity.setoFinprodId(finProduct.getoFinprodId());
                }
            }
        }
        // add by etliu 20210927 end

        Map<String, Object> params = new HashMap<>();
        params.put("workflowId", entity.getWorkflowId());
        String bizCode = entity.getTotleTradeId();
        if (bizCode.lastIndexOf('R') == bizCode.length() - 1) {
            bizCode = bizCode.substring(0, bizCode.length() - 1);
        }
        params.put("bizCode", bizCode);
        params.put("updateUser", SecurityUtil.getLoginUser().getUserName());
        if (StringUtil.isNotEmpty(settleRemark)) {
            params.put("revokeReason", entity.getRevokeReason() == null ? settleRemark : "交易撤销:" + entity.getRevokeReason() + settleRemark);
        } else if (StringUtil.isNotEmpty(entity.getRevokeReason())) {
            params.put("revokeReason", "交易撤销:" + entity.getRevokeReason());
        }
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/doRevokeComplete?", params, String.class);

        //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTotleTradeId());

        // 调用基础组件.【交易报送组件】删除交易报送信息
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepTrade", Arrays.asList(entity.getTotleTradeId()), Boolean.class);

        //撤销数据为登记时，将登记数据置为已撤销。删除下行匹配关系表数据
        RegFinProductDealTotal regTotal = cfetsTrdQueryMapper.selectRegTotalInfoById(bizCode);
        if (null != regTotal) {
            String remark = StringUtil.isEmpty(regTotal.getRemark()) ? "" : regTotal.getRemark();
            if (StringUtil.isNotBlank(remark)) {
                remark = remark + "\n" + (settleRemark == null ? "" : settleRemark) + "交易撤销：" + entity.getRevokeReason();
            } else {
                remark = (settleRemark == null ? "" : settleRemark) + "交易撤销：" + entity.getRevokeReason();
            }
            regTotal.setRemark(remark);
            cfetsTrdBondFirstSub002Service.dorevoke(regTotal.getTotleTradeId(), remark, isNeedDealPosition, entity.getTgBusiType());
            //若是备注撤销内容不为空，则需要更新原登记数据的备注内容
            cfetsTrdQueryMapper.updateRegTotalByPrimaryKeySelective(regTotal);
        }
        cfetsTrdConfirmMapper.deleteByTrdId(bizCode);

        //发送头寸
        String ps = entity.getPs();
        String busiType = DictCodeListExt3.头寸业务类型_债券一级申购.getValue();
        PositionHttpRequest.dorevoke(bizCode, DictCodeListExt1.子系统代码_交易.getValue(), busiType, ps);
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
    private void isNeedOccupy(CfetsTrdBondInvestTradeDto entity, boolean isNotBottomflag, boolean isSave, Boolean isdeleteflag) {
        // 所有交易生效后调用的共通方法(入账信息) 撤销后释放占用的授信额度 Add 20190828 by xkni
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        inputDto.setProdid(entity.getPortfolioId());
        inputDto.setProdFinprodType(DictCodeList.共通业务类型_基金类资产交易.getValue());
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());// 交易状态
        trdProductDeal.setPs(entity.getPs());// 交易方向
        if (StringUtils.isNotBlank(entity.getfTradeId())) {
            trdProductDeal.setfTradeId(entity.getfTradeId());
        }
        trdProductDeal.setTradeId(entity.getTotleTradeId());// 交易编号
        trdProductDeal.setFinprodId(entity.getFinprodId());// 金融产品代码
        trdProductDeal.setCreateDept(entity.getCreateDept());// 创建部门
        trdProductDeal.setCreateUser(entity.getCreateUser());// 创建人
        trdProductDeal.setCreateTime(entity.getCreateTime());// 创建时间
        trdProductDeal.setUpdateUser(entity.getUpdateUser());// 更新人
        trdProductDeal.setUpdateTime(entity.getUpdateTime());// 更新时间
        trdProductDeal.setTradeDate(entity.getTradeDate());// 交易日期
        trdProductDeal.setShareAmt(entity.getShareAmt());// 交易份额，债券数量等
        trdProductDeal.setTradeAmt(entity.getTradeAmt());// 交易金额
        trdProductDeal.setPrinAmt(entity.getPrinAmt());// 交易本金，债券券面等
        trdProductDeal.setFinprodType(entity.getFinprodType());// 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
        trdProductDeal.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
        CfetsTrdInvestPersonInfoDto personInfo = entity.getTrdInvestPersonInfo();
        if (personInfo != null) {
            // 交易对手
            trdProductDeal.setCounterId(personInfo.getCounterId());// 交易对手编号
        }
        if (!StringUtil.isNotEmpty(trdProductDeal.getShareAmt())) {
            // 交易份额 = 画面.券面总额/100
            trdProductDeal.setShareAmt(
                    ArithmeticUtil.divide(trdProductDeal.getTradeAmt(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        }
        inputDto.setTrdProductDeal(trdProductDeal);
        CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
        ceLinesUpdate.isNeedOccupyCre(inputDto, isNotBottomflag, isSave, isdeleteflag);
    }

    @Override
    public void doInsert(CfetsTrdBondInvestTradeDto entity, OperationType operationType) {
        if (StringUtil.isNotEmpty(entity.getBondFirstSign()) && !OperationType.REVOKE.equals(operationType)) {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("tradeId", entity.getTotleTradeId());
            map1.put("bidPrice", entity.getBidPrice());
            map1.put("entrustId", entity.getEntrustId());
            restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/Etrbondbiddingresult/updateTradeId", map1,
                    Integer.class);
        }
        if ("save".equals(entity.getOperateType())) {
            return;
        }
        setWorkflowId(entity);
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
    public void doUpdate(CfetsTrdBondInvestTradeDto entity) {
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            // 先删除流程中数据
            this.deleteBussFlowData(entity.getWorkflowId());
            //流程中修改保存不会进入controller的save方法，会导致丢失数据
            if (StringUtil.isEmpty(entity.getTotleTradeId())) {
                try {
                    List<CfetsTrdBondInvestTradeDto> dtoList = getDtoList(entity);
                    setEntity(dtoList);
                    if (dtoList.size() > 0) {
                        BeanUtil.copyPropertiesIgnoreNull(entity, dtoList.remove(0));
                    }
                    //存在多条，需要新建工作流
                    if (dtoList.size() > 0) {
                        ProcessParameter<CfetsTrdBondInvestTradeDto> processParameter = new ProcessParameter<>();
                        WorkFlowParameter<CfetsTrdBondInvestTradeDto> workFlowParameter = new WorkFlowParameter<>();
                        processParameter.setWorkFlowParameter(workFlowParameter);
                        processParameter.setPageId("TrdBondInvestTrade002");
                        if (StringUtil.equals(entity.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
                            processParameter.setOperationType(OperationType.NEW);
                        } else {
                            processParameter.setOperationType(OperationType.MODIFY);
                        }
                        workFlowParameter.setPageId("TrdBondInvestTrade002");
                        //只考虑save 正常情形下submit不会到这里
                        workFlowParameter.setElementId("TrdBondInvestTrade002_btn_save");
                        workFlowParameter.setBusinessCode((dto) -> dto.getTotleTradeId());
                        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_债券类资产交易.getValue());
                        workFlowParameter.setComment((dto) -> WorkFlowUtil.concatComment(
                                getMessage("TrdBondInvestTrade002.tradeId") + GlobalConstant.COLON + dto.getTotleTradeId(),
                                getMessage("TrdBondInvestTrade002.finprodName") + GlobalConstant.COLON + dto.getFinprodName(),
                                getMessage("TrdBondInvestTrade002.tradeDate") + GlobalConstant.COLON + DateUtil.format(dto.getTradeDate())));
                        this.start(processParameter, dtoList.toArray(new CfetsTrdBondInvestTradeDto[]{}));
                    }

                } catch (Exception e) {
                    logger.error("解析dto出错：" + e);
                    e.printStackTrace();
                }
            } else {
                this.setEntity(Arrays.asList(entity));
            }

            setWorkflowId(entity);

            // 重新insert
            this.handleBussFlowData(entity);

            entity.setPortfolioId(entity.getActPortfolioId());
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(CfetsTrdBondInvestTradeDto.class, true);
                // 工作流ID
                newEntity.setWorkflowId(entity.getWorkflowId());
                newEntity.setStatus(entity.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(entity));
                // 更新业务表数据
                // 操作留痕表
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public List<CfetsTrdBondInvestTradeDto> getDtoList(CfetsTrdBondInvestTradeDto dto) throws InvocationTargetException, IllegalAccessException {
        List<CfetsTrdBondInvestTradeDto> dtoList = new ArrayList<>();
        List<BussTrdTransfenpeiDto> fenpeiList = dto.getBussTrdTransfenpeiDto();
        List<BussTrdTransferOrderDto> orderList = dto.getBussTrdTransferOrder();
        for (BussTrdTransferOrderDto bussTrdTransferOrder : orderList) {

            CfetsTrdBondInvestTradeDto tempDto = new CfetsTrdBondInvestTradeDto();
            BeanUtil.copyProperties(tempDto, dto);
            List<BussTrdTransferOrderDto> tmpOrderList = new ArrayList<BussTrdTransferOrderDto>();
            if (fenpeiList != null && fenpeiList.size() > 0) {
                for (BussTrdTransfenpeiDto fenpei : fenpeiList) {
                    if (StringUtils.equals(bussTrdTransferOrder.getPortfolioId(), fenpei.getPortfolioId())
                            && bussTrdTransferOrder.getSettleAmt().equals(fenpei.getFpriceAmt())) {
                        tmpOrderList.add(bussTrdTransferOrder);

                        tempDto.setPortfolioId(fenpei.getPortfolioId());
                        tempDto.setTradeAmt(fenpei.getTradeAmt());
                        tempDto.setCpriceAmt(fenpei.getCpriceAmt());
                        tempDto.setFpriceAmt(fenpei.getFpriceAmt());
                        tempDto.setBussTrdTransferOrder(tmpOrderList);

                        if (baseGetBusinessId != null) {
                            BussinessCodeParameter codeParameter = getBussinessCodeParameter(tempDto.getPs());
                            tempDto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
                        } else {
                            tempDto.setTotleTradeId(IDGenerator.generate());
                        }
                        dtoList.add(tempDto);
                    }
                }
            } else {
                if (baseGetBusinessId != null) {
                    BussinessCodeParameter codeParameter = getBussinessCodeParameter(tempDto.getPs());
                    tempDto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
                } else {
                    tempDto.setTotleTradeId(IDGenerator.generate());
                }
                // 将附件和当前业务对象绑定（更新附件的业务代码）
                fileAttachmentProcess.updateBussinessNo(
                        dto.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                                Collectors.toList()),
                        tempDto.getTotleTradeId());
                dtoList.add(tempDto);
            }

        }
        return dtoList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        CfetsTrdBondInvestTradeDto entity = this.getBussinessEntityByWorkflowId(
                workflowId);
        if (StringUtil.isNotEmpty(entity.getBondFirstSign()) && !"revoke".equals(entity.getOperateType())) {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("tradeId", null);
            map1.put("bidPrice", entity.getBidPrice());
            map1.put("entrustId", entity.getEntrustId());
            restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/Etrbondbiddingresult/updateTradeId", map1,
                    Integer.class);
        }
        baseTradeProductDealCommService.delete(workflowId);

    }

    /**
     * 更新CSTP的劵面总额
     *
     * @param workflowId
     */
    @DataSource(DataSourceType.OTHER)
    @DTXTransaction
    public void uodateCstpMessage(String workflowId) {
        AllTradeCommonInputDto inputDto = restTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectWflTrdProductDeal/" + workflowId,
                AllTradeCommonInputDto.class);
        // 处理cstp交易情况
        if (inputDto != null
                && inputDto.getTrdProductDeal() != null
                && StringUtils.isNotBlank(inputDto.getTrdProductDeal().getfTradeId())) {
            String fTradeId = inputDto.getTrdProductDeal().getfTradeId();
            InterfTradSecdeal interfTradSecdeal = interfTradSecdealMapper.selectByPrimaryKey(fTradeId);
            if (interfTradSecdeal != null) {
                InterfTradSecdeal record = new InterfTradSecdeal();
                record.setDealno(fTradeId);
                BigDecimal prinamt = interfTradSecdeal.getPrinamt();
                BigDecimal subtract = ArithmeticUtil.doAdd(prinamt,
                        ArithmeticUtil.multiply(inputDto.getTrdProductDeal().getShareAmt(), new BigDecimal(100)));
                if (subtract.compareTo(BigDecimal.ZERO) < 0 || subtract.compareTo(BigDecimal.ZERO) == 0) {// 小于0的话
                    record.setPrinamt(BigDecimal.ZERO);
                } else {
                    record.setPrinamt(subtract);
                }
                interfTradSecdealMapper.updateByPrimaryKeySelective(record);
            }
        }
    }

    @Override
    public void doComplete(CfetsTrdBondInvestTradeDto entity, OperationType operationType) {
        String tradeId = entity.getTotleTradeId();
        //买入含权债时，自动创建交易对手 add by wangwei5 2020-03-23 Start
        if (operationType.equals(OperationType.NEW)) {
            //含权债标记
            String str = entity.getFinprodId();
            if (!StringUtil.equals(str.substring(0, 3), "F01")) {
                entity.setFinprodId("F01_" + str);
            }
            DebtBaseInfo debtbaseinfo = trdFinProductDealTotalMapper.selectIsRight(entity.getFinprodId());
            if (StringUtil.isNotEmpty(debtbaseinfo.getIsRight()) && StringUtil.isNotEmpty(entity.getPs())) {
                if ("Y".equals(debtbaseinfo.getIsRight()) && "T01".equals(entity.getPs())) {
                    BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                    bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_交易对手管理);
                    debtbaseinfo.setCounterId(baseGetBusinessId.buildBussinessCode(bussinessCodeParameter));
                    debtbaseinfo.setCreateUser(LoginUserUtil.getUserName());
                    debtbaseinfo.setCreateDept(LoginUserUtil.getDeptCode());
                    debtbaseinfo.setUpdateUser(LoginUserUtil.getUserName());
                    //判断交易对手表中是否存在
                    int flag = trdFinProductDealTotalMapper.selectCounterName(debtbaseinfo.getCounterName());
                    if (flag == 0) {
                        trdFinProductDealTotalMapper.insertDebtBaseInfo(debtbaseinfo);
                    }
                }
            }
        }
        //买入含权债时，自动创建交易对手 add by wangwei5 2020-03-23 end
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {

        }
        // 撤销
        else {
            this.doCompleteBidRevoke(entity, "", true);

            //补充  如果是撤销将交易ID赋值为null
            Map<String, Object> map = new HashMap<>();
            map.put("totleTradeId", entity.getTotleTradeId());
            map.put("entrustId", entity.getEntrustId());
            restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/Etrbondbiddingresult/deleteTradeId", map,
                    Integer.class);
        }

    }

    private List<TrdTradeFeeTotal> getTrdTradeFeeDetails(CfetsTrdBondInvestTradeDto entity) {
        List<TrdTradeFeeTotal> ttfdList = new ArrayList<>();

        //构筑结算费经手费保存到数据库的实体
        if (entity.getSettleAmt() != null) {
            TrdTradeFeeTotal ttfd = new TrdTradeFeeTotal();
            ttfd.setTotleTradeId(entity.getTotleTradeId());
            ttfd.setFeeType(BussinessDictCodeList.费用类型_结算费.getValue());
            ttfd.setIsPayWithTrade(entity.getSettleIsPayWithTrade());
            ttfd.setFeeAmt(entity.getSettleAmt());
            ttfdList.add(ttfd);
        }
        if (entity.getExchangeAmt() != null) {
            TrdTradeFeeTotal ttfd = new TrdTradeFeeTotal();
            ttfd.setTotleTradeId(entity.getTotleTradeId());
            ttfd.setFeeType(BussinessDictCodeList.费用类型_经手费.getValue());
            ttfd.setIsPayWithTrade(entity.getExchangeIsPayWithTrade());
            ttfd.setFeeAmt(entity.getExchangeAmt());
            ttfdList.add(ttfd);
        }

        if (StringUtil.equals(entity.getTradeMarket(), BussinessDictCodeList.交易场所_银行间.getValue())) {
            if (entity.getProcedureAmt() != null) {
                TrdTradeFeeTotal ttfd = new TrdTradeFeeTotal();
                ttfd.setTotleTradeId(entity.getTotleTradeId());
                ttfd.setFeeType(BussinessDictCodeList.费用类型_交易手续费.getValue());
                ttfd.setIsPayWithTrade(BussinessDictCodeList.是否_否.getValue());
                ttfd.setFeeAmt(entity.getProcedureAmt());
                ttfdList.add(ttfd);
            }
            if (entity.getSettleServiceAmt() != null) {
                TrdTradeFeeTotal ttfd = new TrdTradeFeeTotal();
                ttfd.setTotleTradeId(entity.getTotleTradeId());
                ttfd.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
                ttfd.setIsPayWithTrade(BussinessDictCodeList.是否_否.getValue());
                ttfd.setFeeAmt(entity.getSettleServiceAmt());
                ttfdList.add(ttfd);
            }
//            if (entity.getSettleAmt() != null) {
//                TrdTradeFeeTotal ttfdJs = new TrdTradeFeeTotal();
//                ttfdJs.setTotleTradeId(entity.getTotleTradeId());
//                ttfdJs.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
//                ttfdJs.setIsPayWithTrade(BussinessDictCodeList.是否_否.getValue());
//                ttfdJs.setFeeAmt(entity.getSettleAmt());
//                ttfdList.add(ttfdJs);
//            }
        } else if (StringUtil.equals(entity.getTradeMarket(), BussinessDictCodeList.交易场所_上海证券交易所.getValue())
                || StringUtil.equals(entity.getTradeMarket(), BussinessDictCodeList.交易场所_深圳证券交易所.getValue())) {
            if (entity.getCommissionAmt() != null) {
                TrdTradeFeeTotal ttfd = new TrdTradeFeeTotal();
                ttfd.setTotleTradeId(entity.getTotleTradeId());
                ttfd.setFeeType(BussinessDictCodeList.费用类型_佣金.getValue());
                // modified by wangguoxian 费用设置 20180808 start
                ttfd.setIsPayWithTrade(entity.getIsPayWithTrade());
                // modified by wangguoxian 费用设置 20180808 end
                ttfd.setFeeAmt(entity.getCommissionAmt());
                ttfdList.add(ttfd);
            }

            // add by wangpeng 费用设置 20180818 start
            if (entity.getStampDutyAmt() != null) {
                TrdTradeFeeTotal ttfd = new TrdTradeFeeTotal();
                ttfd.setTotleTradeId(entity.getTotleTradeId());
                ttfd.setFeeType(BussinessDictCodeList.费用类型_印花税.getValue());
                ttfd.setIsPayWithTrade(entity.getStampDutyIsPayWithTrade());
                ttfd.setFeeAmt(entity.getStampDutyAmt());
                ttfdList.add(ttfd);
            }
            // add by wangpeng 费用设置 20180818 end
        }
        return ttfdList;
    }

    /**
     * 查询委托信息
     *
     * @param entrustId 委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    public BaseEtrInvestInfoDto getEntrustInfo(String entrustId) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getEntrustInfo/" + entrustId, null,
                BaseEtrInvestInfoDto.class);
    }

    /**
     * 查询委托信息多组合
     *
     * @param entrustId 委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    public List<BaseEtrInvestInfoDto> getEntrustInfos(String entrustId) {
        List<BaseEtrInvestInfoDto> list = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getEntrustInfos/" + entrustId, null,
                BaseEtrInvestInfoDto.class);
        if (CollectionUtils.isNotEmpty(list)) {
            for (BaseEtrInvestInfoDto dto : list) {
                // 设置可委托金额
                dto.setDoEntrustMoney(
                        selectDoEntrustMoney(dto.getEtrEntrust().getEntrustId(), dto.getEtrEntrust().getPortfolioId()));
            }
        }
        return list;
    }

    public BigDecimal selectDoEntrustMoney(String entrustId, String portfolioId) {
        if (StringUtils.isNotBlank(entrustId) && StringUtils.isNotBlank(portfolioId)) {
            // 总的委托额度
            BigDecimal entrustNum = tradeCommonService.getEntrustAmtByEntrustId(entrustId);
            // 有效的和流程中的委托额度
            BigDecimal wflPrinAmt = tradeCommonService.getWflPrinAmt(entrustId, portfolioId, null);
            BigDecimal subtract = ArithmeticUtil.subtract(entrustNum, wflPrinAmt);
            return subtract;
        }
        return new BigDecimal(0);
    }

    /**
     * 查询委托信息
     *
     * @param entrustId 委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    public BaseEtrInvestInfoDto getEntrustInfoTmp(String entrustId) {
        BaseEtrInvestInfoDto baseEtrInvestInfoDto = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getEntrustInfo/" + entrustId, null,
                BaseEtrInvestInfoDto.class);
        Map<String, Object> map = new HashMap<>();
        map.put("oFinprodId", baseEtrInvestInfoDto.getEtrEntrust().getFinprodId());
        List<FinProduct> finProducts = restTemplate.postForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/Etrbondbiddingresult/findFinProductByOFinprodId", map, FinProduct.class);
        EtrEntrust etrEntrust = baseEtrInvestInfoDto.getEtrEntrust();
        if (finProducts != null && !finProducts.isEmpty()) {
            FinProduct finProduct = finProducts.get(0);
            etrEntrust.setFinprodId(finProduct.getFinprodId());
            baseEtrInvestInfoDto.setFinprodName(finProduct.getFinprodName());
        }
        etrEntrust.setTradeMarketMode("01");
        return baseEtrInvestInfoDto;
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
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecManageAcctId", parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                if (StringUtil.isNotBlank((String) parameters.get("portfolioId"))) {
                    selectOptionDto.setParentId((String) parameters.get("portfolioId"));
                }
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }

    // 货币型产品投资目的默认显示摊余成本 add by liupeilin 2020-01-15 begin

    /**
     * 获取产品类型
     *
     * @param parameters 画面参数
     * @return 产品类型
     * @see
     * @since 1.0
     */
    public String getProfitType(Map<String, Object> parameters) {
        String portfolioId = (String) parameters.get("portfolioId");
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getProfitType/" + portfolioId, null, String.class);

    }
    // 货币型产品投资目的默认显示摊余成本 add by liupeilin 2020-01-15 end

    /**
     * 查询证券托管户：联想框文本框
     *
     * @param parameters 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getSecManageAccId(Map<String, Object> parameters) {
        List<SuggestDto> tempList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecManageAcctId", parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                if (StringUtil.isNotBlank((String) parameters.get("portfolioId"))) {
                    selectOptionDto.setParentId((String) parameters.get("portfolioId"));
                    // selectOptionDto.setId((String) parameters.get("portfolioId") + selectOptionDto.getId());//设置下拉框的id是组合代码加上债券账户的id
                }
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }

    /**
     * 通道：联想框文本框
     *
     * @param parameters 画面参数
     * @return 通道：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getChannel(Map<String, Object> parameters) {
        List<SuggestDto> tempList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getChannel", parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                // if (StringUtil.isNotBlank((String) parameters.get("portfolioId"))) {
                // selectOptionDto.setParentId((String) parameters.get("portfolioId"));
                // }
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }

    /**
     * 1.6计算偏离度
     *
     * @param parameters 画面参数
     * @return 偏离度
     * @see
     * @since 1.0
     */
    public String calcDeviationDegree(Map<String, Object> parameters) {
        // 平台日
        parameters.put("sysDate", DateUtil.format(iPlatformDate.getSysDate()));

        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/calcDeviationDegree",
                parameters, String.class);
    }

    /**
     * 1.7计算按钮点击事件
     *
     * @param inputDto 画面参数
     * @return 计算结果
     * @see
     * @since 1.0
     */
    public AjaxResult calculateData(CfetsTrdBondInvestTradeDto inputDto) {
        AjaxResult ajaxResult = new AjaxResult();

        // 校验输入参数
        List<ValidMessage> errorList = this.validCalcAmt(inputDto);
        if (!StringUtil.isEmptyList(errorList)) {
            ajaxResult.setErrorList(errorList);
            return ajaxResult;
        }

        // 计算逻辑
        this.calcAmt(inputDto);
        ajaxResult.setData(inputDto);

        return ajaxResult;
    }

    /**
     * 1.7计算按钮点击事件 检查输入参数是否合法
     *
     * @param dto 画面输入参数
     * @return 检查结果
     * @see
     * @since 1.0
     */
    private List<ValidMessage> validCalcAmt(CfetsTrdBondInvestTradeDto dto) {
        List<ValidMessage> validMessageList = new ArrayList<ValidMessage>();
        // 1.交割日
        if (dto.getSettleDate() == null) {
            ValidMessage validMessage = new ValidMessage("settleDate",
                    getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.settleDate")));
            validMessageList.add(validMessage);
        }
        // 2.资产代码
        if (StringUtil.isBlank(dto.getFinprodId())) {
            ValidMessage validMessage = new ValidMessage("finprodId",
                    getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.finprodId")));
            validMessageList.add(validMessage);
        }
        // 投资目的
//        if(StringUtil.equals(CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL"),"02")){
        if (StringUtil.isBlank(dto.getInvAim())) {
            ValidMessage validMessage = new ValidMessage("invAim",
                    getMessage("BUSS_COM_E_0038", getMessage("EtrBondFirstLBidding002.businessModel")));
            validMessageList.add(validMessage);
        }
//        }else{
//            if (StringUtil.isBlank(dto.getInvAim())) {
//                ValidMessage validMessage = new ValidMessage("invAim",
//                        getMessage("BUSS_COM_E_0038", getMessage("EtrBondFirstLBidding002.invAim")));
//                validMessageList.add(validMessage);
//            }
//        }
        // 3.劵面总额
        if (dto.getTradeAmt() == null) {
            ValidMessage validMessage = new ValidMessage("tradeAmt",
                    getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.tradeAmt")));
            validMessageList.add(validMessage);
        }

        if (StringUtil.isBlank(dto.getCalculateField())) {
            ValidMessage validMessage = new ValidMessage(
                    getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.calculateField")));
            validMessageList.add(validMessage);
        } else {
            switch (dto.getCalculateField()) {
                case "unitCprice":
                    if (dto.getUnitCprice() == null) {
                        ValidMessage validMessage = new ValidMessage("unitCprice",
                                getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.unitCprice")));
                        validMessageList.add(validMessage);
                    }
                    break;
                case "fpriceAmt":
                    if (dto.getFpriceAmt() == null) {
                        ValidMessage validMessage = new ValidMessage("fpriceAmt",
                                getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.fpriceAmt")));
                        validMessageList.add(validMessage);
                    }
                    break;
                case "yield":
                    if (dto.getYield() == null) {
                        ValidMessage validMessage = new ValidMessage("yield",
                                getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.yield")));
                        validMessageList.add(validMessage);
                    }
                    break;
                case "exerYield":
                    if (dto.getExerYield() == null) {
                        ValidMessage validMessage = new ValidMessage("exerYield",
                                getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.exerYield")));
                        validMessageList.add(validMessage);
                    }
                    // 根据行权收益率反算其他值的情况，若下一行权日净价未维护，则提示错误消息
                    else {
                        try {
                            Map<String, Object> tempParamMap = new HashMap<String, Object>();
                            tempParamMap.put("finprodId", dto.getFinprodId());
                            tempParamMap.put("ps", dto.getPs());
                            // d.获取债券基本资料信息
                            SecInfoModelDto secInfoModelDto = restTemplate.postForObject(
                                    ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecInfoModel", tempParamMap,
                                    SecInfoModelDto.class);

                            if (secInfoModelDto != null && secInfoModelDto.getNextExeDate() != null
                                    && secInfoModelDto.getNextExercisePrice() == null) {
                                ValidMessage validMessage = new ValidMessage("exerYield", getMessage("TRD_E_0015"));
                                validMessageList.add(validMessage);
                            }
                        } catch (Exception e) {
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        // 债券到期日<=交割日时
        if (StringUtil.isNotEmpty(dto.getSettleDate()) && StringUtil.isNotEmpty(dto.getMdate())) {
            if (DateUtil.compareDate(dto.getMdate(), dto.getSettleDate()) <= 0) {
                validMessageList.add(new ValidMessage("settleDate", "交割日期必须小于资产到期日"));
            }
        }
        // 画面.交割日<债券起息日
        if (StringUtil.equals(dto.getTradeMarketMode(), DictCodeListExt1.交易市场_二级市场.getValue())
                && StringUtil.isNotEmpty(dto.getVdate()) && StringUtil.isNotEmpty(dto.getSettleDate())) {
            if (DateUtil.compareDate(dto.getSettleDate(), dto.getVdate()) < 0) {
                validMessageList.add(new ValidMessage("settleDate", this.getMessage("BUSS_COM_E_0004",
                        this.getMessage("TrdBondInvestTrade002.settleDate"), this.getMessage("TrdBondInvestTrade002.vdate"))));
            }
        }
        return validMessageList;
    }

    /**
     * 1.8计算单位面值
     *
     * @param finprodId  金融产品代码
     * @param settleDate 交割日
     * @return 单位面值
     * @see
     * @since 1.0
     */
    public BigDecimal getUnitPrice(String finprodId, Date settleDate) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("finprodId", finprodId);
        parameters.put("settleDate", DateUtil.format(settleDate));
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getUnitPrice", parameters,
                BigDecimal.class);
    }

    /**
     * 资产代码：下拉框
     *
     * @param parameters 画面参数
     * @return 资产代码：下拉框
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodId(Map<String, Object> parameters) {
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getFinprodId", parameters,
                SelectOptionDto.class);
    }

    // 资产推荐方 新增 zhuyongkang 20191023 start

    /**
     * 构建推荐方下拉框
     *
     * @param parameters 参数
     * @return 下拉框查询结果
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getAssetRecommandOrg(Map<String, Object> parameters) {
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getAssetRecommandOrg",
                parameters, SelectOptionDto.class);
    }

    // 资产推荐方 新增 zhuyongkang 20191023 start

    /**
     * 行权收益率是否显示
     *
     * @param paramMap 金融产品代码
     * @return 行权收益率是否显示
     * @see
     * @since 1.0
     */
    public String isExerYieldDivShow(Map<String, Object> paramMap) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/isExerYieldDivShow",
                paramMap, String.class);
    }

    /**
     * 判断某一天是否是债券行权日
     *
     * @param finprodId 金融产品代码
     * @param paramDate 判断日期
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
     * 判断是否含权债
     *
     * @param finprodId 金融产品代码
     * @see
     * @since 1.0
     */
    public boolean isRight(String finprodId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("finprodId", finprodId);

        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/isRight", paramMap,
                Boolean.class);
    }

    /**
     * 获取下一个工作日
     *
     * @param parameters 画面参数
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
    public String getNextWorkDay(Map<String, Object> parameters) {
        if (dateCalService != null) {
            Date tradeDate = DateUtil.parse((String) parameters.get("tradeDate"));
            List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDays(tradeDate, 31),
                    (String) parameters.get("calendarId"));
            int settleSpeed = 0;
            if (parameters.get("settleSpeed") != null) {
                settleSpeed = Integer.parseInt((String) parameters.get("settleSpeed"));
            }
            Date tradingDay = dateCalService.calculationTradingDay(tradeDate, settleSpeed, holiday, "1");
            return DateUtil.format(tradingDay);
        }

        return null;
    }

    /**
     * 债券投资交易新建画面查看模式查询债券投资交易信息
     *
     * @param tradeId 交易编号
     * @return 债券投资交易信息
     * @see
     * @since 1.0
     */
    public CfetsTrdBondInvestTradeDto getTrdBondInvestTradeDto(String tradeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("tradeId", tradeId);
        params.put("B09", DictCodeListExt1.一次性费用类型_通道费.getValue());
        params.put("Y", DictCodeList.是否_是.getValue());
        CfetsTrdBondInvestTradeDto resultDto = cfetsTrdQueryMapper.getTrdBondInvestTradeDto(params);
        if (resultDto != null) {
            //待办事项中显示组合分配
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
            pageParam.put("feeType08", BussinessDictCodeList.费用类型_结算手续费.getValue());
            pageParam.put("totleTradeId", tradeId);
            List<PortIntFenPeiInfoDto> fenPeiList = cfetsTrdQueryMapper.findPortIntFenPeiInfoList(pageParam);
            resultDto.setCfetsBussCombDistribtList(fenPeiList);

          /*  if (resultDto.getSettleSpeed() != null) {
                resultDto.setSettleSpeedStr(String.valueOf(resultDto.getSettleSpeed()));
            }
            else if(resultDto.getSettleSpeed() == 1L){
                resultDto.setSettleSpeedStr("1");
            }else if(resultDto.getSettleSpeed() == 2L){
                resultDto.setSettleSpeedStr("2");
            }else{
                resultDto.setSettleSpeedStr(resultDto.getSettleSpeed().toString());
            }*/
           /* if(StringUtil.isNotEmpty(resultDto.getSettleSpeed())) {
                if (resultDto.getSettleSpeed() == 0L) {
                    resultDto.setSettleSpeedStr("01");
                } else if (resultDto.getSettleSpeed() == 1L) {
                    resultDto.setSettleSpeedStr("02");
                } else if (resultDto.getSettleSpeed() == 2L) {
                    resultDto.setSettleSpeedStr("03");
                }
            }*/
            // 到期收益率
            if (resultDto.getYield() != null) {
                resultDto.setYield(ArithmeticUtil.multiply(resultDto.getYield(), BigDecimal.valueOf(100)));
            }
            // 行权收益率
            if (resultDto.getExerYield() != null) {
                resultDto.setExerYield(ArithmeticUtil.multiply(resultDto.getExerYield(), BigDecimal.valueOf(100)));
            }

            // 交易手续费
            TrdTradeFeeTotalKey trdTradeFeeTotalKey = new TrdTradeFeeTotalKey();
            trdTradeFeeTotalKey.setTotleTradeId(resultDto.getTotleTradeId());
            trdTradeFeeTotalKey.setFeeType(BussinessDictCodeList.费用类型_交易手续费.getValue());
            TrdTradeFeeTotal trdTradeFeeTotal = trdTradeFeeTotalMapper.selectByPrimaryKey(trdTradeFeeTotalKey);
            if (trdTradeFeeTotal != null) {
                resultDto.setProcedureAmt(trdTradeFeeTotal.getFeeAmt());
            }

            // 结算费服务费
            trdTradeFeeTotalKey.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
            trdTradeFeeTotal = trdTradeFeeTotalMapper.selectByPrimaryKey(trdTradeFeeTotalKey);
            if (trdTradeFeeTotal != null) {
                resultDto.setSettleServiceAmt(trdTradeFeeTotal.getFeeAmt());
            }
        }
        return resultDto;
    }

    /**
     * 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量， 如取不到估值核算中前一个自然日的数据，计算方式见算法文档； 画面.应计利息=PTL_SEC_VALUTION.TDY_INTINCEXP/PTL_SEC_VALUTION.SHARE_AMT
     * （PTL_SEC_VALUTION.日期 = 画面.交割日-1 ，取不到，取最大日期对应数据）
     *
     * @param dto 参数
     * @return 应计利息
     * @see
     * @since 1.0
     */
    private void getUnitIntFromValution(CfetsTrdBondInvestTradeDto dto) {
        // 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量，如取不到估值核算中前一个自然日的数据，计算方式见算法文档；
        // 画面.应计利息=PTL_SEC_VALUTION.TDY_INTINCEXP/PTL_SEC_VALUTION.SHARE_AMT （PTL_SEC_VALUTION.日期 = 画面.交割日-1 ，取不到，取最大日期对应数据）
        Map<String, Object> paraMapForIntAmt = new HashMap<String, Object>();
        paraMapForIntAmt.put("settleDate", DateUtil.format(dto.getSettleDate()));
        paraMapForIntAmt.put("portfolioId", dto.getPortfolioId());
        paraMapForIntAmt.put("finprodId", dto.getFinprodId());
        paraMapForIntAmt.put("invAim", dto.getInvAim());
        BigDecimal 应计利息 = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getIntAmtFromValution", paraMapForIntAmt,
                BigDecimal.class);
        if (应计利息 != null) {
            dto.setUnitInt(应计利息);
        }
    }

    /**
     * 1.7计算按钮点击事件
     *
     * @param dto 画面输入参数
     * @see
     * @since 1.0
     */
    public void calcAmt(CfetsTrdBondInvestTradeDto dto) {
        // 先计算融入券
        if (dto.getIntegrateTradeAmt() != null && dto.getIntegrateTradeAmt().compareTo(BigDecimal.ZERO) > 0) {
            dto.setPrinAmt(dto.getIntegrateTradeAmt());
            calc(dto, true);
        } else {
            dto.setIntegrateIntAmt(BigDecimal.ZERO);
            dto.setIntegrateCpriceAmt(BigDecimal.ZERO);
            dto.setIntegrateFpriceAmt(BigDecimal.ZERO);
        }
        dto.setPrinAmt(dto.getTradeAmt());
        calc(dto, false);
    }

    private void calc(CfetsTrdBondInvestTradeDto dto, boolean 计算融入券) {
        if (DateUtil.compareDate(dto.getSettleDate(), dto.getVdate()) < 0) {
            dto.setSettleDate(dto.getVdate());
        }

        // 如果交易场所为上交所、深交所以外的情况
        boolean isJiaoYiSuo = true;
        if (!StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), dto.getTradeMarket())
                && !StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), dto.getTradeMarket())) {
            // // 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量
            // this.getUnitIntFromValution(dto);
            isJiaoYiSuo = false;
        }

        final int SCALE = 12;

        dto.setYield(ArithmeticUtil.divide(dto.getYield(), 100, 14));
        dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), 100, 14));

        YieldByPriceCalculator yieldByPriceCalculator = new YieldByPriceCalculator();
        PriceByYieldCalculator priceByYieldCalculator = new PriceByYieldCalculator();
        YieldPriceInParameter yieldPriceInParameter = null;
        PriceYieldInParameter priceYieldInParameter = null;
        YieldPriceOutParameter yieldPriceOutParameter = null;
        PriceYieldOutParameter priceYieldOutParameter = null;

        Map<String, Object> tempParamMap = new HashMap<String, Object>();
        String str = dto.getFinprodId();
        if (!StringUtil.equals(str.substring(0, 3), "F01")) {
            str = "F01_" + str;
            tempParamMap.put("finprodId", str);
        } else if (StringUtil.equals(str.substring(0, 3), "F01")) {
            tempParamMap.put("finprodId", dto.getFinprodId());
        }

        // b.获取计息区间投资端利率报价信息（债券的利率重置信息）
        // List<BondsIntReset> bondsIntResetList = transactionBond001Mapper.getInvRateQuo(dto);
        List<FinCashCalcRule> finCashCalcRuleList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/selectBondsIntResetList", tempParamMap,
                FinCashCalcRule.class);

        // c.获取债券的付息计划信息
        List<FinCashPlanDto> finCashPlanList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getFinCashPlan", tempParamMap, FinCashPlanDto.class);
        List<SecIntSchedule> invScheduleList = new ArrayList<>();
        List<FinCashPlanDto> intSchList = new ArrayList<FinCashPlanDto>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(), DictCodeListExt1.现金流一级类型_利息.getValue())
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
        // 计算行权收益率，非行权时，取大于当日的行权日
        tempParamMap.put("ps", dto.getPs());
        SecInfoModelDto secInfoModelDto = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecInfoModel", tempParamMap, SecInfoModelDto.class);
        SecInfoModel secInfoModel = toSecInfoModel(secInfoModelDto);
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
            if (StringUtil.equals(item.getCashType1(), DictCodeListExt1.现金流一级类型_本金.getValue())) {
                SecPrinSchedule schedule = new SecPrinSchedule();
                schedule.setAmount(item.getIntAmt());
                schedule.setFlag(StringUtil.equals(item.getIsPayInt(), DictCodeList.是否_是.getValue()));
                schedule.setPrincipal(item.getPrinAmt());
                schedule.setInterestEndDate(toLocalDate(item.getMdate()));
                secPrinScheduleList.add(schedule);
            }
        }

        // List<PaperIrReset> paperIrResetList = bondsIntResetList.stream().map(item -> {
        // PaperIrReset ret = new PaperIrReset();
        // // BeanUtil.copyPropertiesIgnoreNull(ret, item);
        // ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getBaseRateEffDate()));
        // ret.setParRate(item.getParRate());
        // ret.setParRateEffDate(BasicUtil.dateToLocal(item.getParRateEffDate()));
        // return ret;
        // }).collect(Collectors.toList());
        // if (paperIrResetList != null && paperIrResetList.size() > 0) {
        // paperIrResetList.get(0).setBaseRateEffDate(
        // BasicUtil.dateToLocal(secInfoModelDto.getStartDate()));
        // }

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
            // 多重利率则计算按浮动利率算
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
        if (StringUtil.equals(dto.getTradeMarketMode(), DictCodeListExt1.交易市场_一级市场.getValue())) {
            dto.setUnitInt(accurInt);
            dto.setIntAmt(BigDecimal.ZERO);
            if (计算融入券) {
                dto.setIntegrateIntAmt(BigDecimal.ZERO);
            }
        }
        // 交易方式为“二级市场”时，单位应计利息 =e取得的百元面值应计利息/100*a取得的当前债券面额
        else if (StringUtil.equals(dto.getTradeMarketMode(), DictCodeListExt1.交易市场_二级市场.getValue())) {
            // a.当前债券面额取得（根据还本计划计算）
            // BigDecimal curSecAmt = transactionBond001Mapper.getCurSecAmt(dto);
            // accurInt = ArithmeticUtil.round(BondsCalculation.calAccurInt(dto.getSettDate(),
            // bondsInfo,
            // bondsScheduleList, bondsIntResetList), SCALE, RoundingMode.HALF_UP);
            // 单位应计利息
            // if (dto.getUnitInt() == null) {
            // dto.setUnitInt(ArithmeticUtil.doMul(
            // ArithmeticUtil.doDiv(accurInt, new BigDecimal(100), SCALE), curSecAmt));
            // }
            // // 应计利息总额 = 单位应计利息 * 券面总额/100
            // dto.setIntAmt(ArithmeticUtil.doDiv(ArithmeticUtil.doMul(accurInt, dto.getPrinAmt()),
            // new BigDecimal(100), SCALE));

            // 交易所债券特殊处理start----------------------------------------------------------------
            Date tempSettleDate = dto.getSettleDate();
            // 如果交易场所为上交所、深交所的情况
            if (isJiaoYiSuo) {
                // 付息债、贴现债、零息债，交割日+1
                // if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())
                // || StringUtil.equals(DictCodeList.息票品种_贴现.getValue(), secInfoModelDto.getBondsType())
                // || StringUtil.equals(DictCodeList.息票品种_零息.getValue(), secInfoModelDto.getBondsType())) {
                // add by 张年 20190909 start
                // 行权-回售 行权-赎回 交割日不加1
                if (!StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                        && !StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())) {
                    tempSettleDate = DateUtil.addDay(tempSettleDate, 1);
                }
                // add by 张年 20190909 end
                // }
            }
            // 交易所债券特殊处理end----------------------------------------------------------------

            // a.当前债券面额取得（根据还本计划计算）
            Map<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("finprodId", dto.getFinprodId());
            // paraMap.put("settleDate", DateUtil.format(dto.getSettleDate()));
            paraMap.put("settleDate", DateUtil.format(tempSettleDate));
            BigDecimal curSecAmt = restTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getCurSecAmt", paraMap, BigDecimal.class);

            AccurIntCalInParameter accurIntCalInParameter = new AccurIntCalInParameter();
            // 计算日
            // accurIntCalInParameter.setCalDate(this.toLocalDate(dto.getSettleDate()));
            accurIntCalInParameter.setCalDate(toLocalDate(tempSettleDate));
            if (paperIrResetList != null && paperIrResetList.size() > 1) {
                // 多重利率则计算按浮动利率算
                secInfoModel.setIntRateType(IntRateType.浮动利率);
            }
            // 债券基本资料
            accurIntCalInParameter.setSecInfoModel(secInfoModel);
            // 付息计划
            accurIntCalInParameter.setSecIntScheduleList(invScheduleList);
            // 利率重置信息
            accurIntCalInParameter.setPaperIrResetList(paperIrResetList);

            // 交易所债券特殊处理start----------------------------------------------------------------
            AccurIntCalOutParameter out = null;
            if (DictCodeList.WIND一级分类_含债券一级分类等_资产支持证券.getValue().equals(dto.getWindtypeone())) {
                // 付息计划截止日对应的利息
                Map<String, BigDecimal> intMap = new HashMap<>();
                if (DictCodeList.WIND一级分类_含债券一级分类等_资产支持证券.getValue().equals(dto.getWindtypeone())) {
                    // 理论计息截止日
                    if (null != intSchList && !intSchList.isEmpty()) {
                        for (FinCashPlanDto intSche : intSchList) {
                            intMap.put(toLocalDate(intSche.getMdateTerm()).toString(), intSche.getIntAmt());
                        }
                    }
                    // 实际计息截止日
                    if (null != invScheduleList && !invScheduleList.isEmpty()) {
                        for (SecIntSchedule intSche : invScheduleList) {
                            intMap.put(intSche.getActualCutoffDate().toString(), intSche.getAmount());
                        }
                    }
                }
                if (intMap.containsKey(accurIntCalInParameter.getCalDate().toString())
                        && !((Variety.贴现.equals(secInfoModel.getBondsType())) || Variety.零息.equals(secInfoModel.getBondsType()))) {
                    out = new AccurIntCalOutParameter();
                    out.setInterest(
                            intMap.get(accurIntCalInParameter.getCalDate().toString()).multiply(new BigDecimal("100")).divide(
                                    curSecAmt, 12, RoundingMode.HALF_UP));
                    accurIntCalInParameter.setCalDate(accurIntCalInParameter.getCalDate());
                    AccurIntForSecheduleCalculator calculator2 = CalculatorFactory.createAccurIntForSecheduleCalculator();
                    if (accurIntCalInParameter.getPaperIrResetList() != null
                            && accurIntCalInParameter.getPaperIrResetList().size() > 1) {
                        accurIntCalInParameter.getSecInfoModel().setIntRateType(IntRateType.浮动利率);
                    }
                    out = calculator2.calculate(accurIntCalInParameter);
                }
            }
            // 如果交易场所为上交所、深交所的情况，且
            if (isJiaoYiSuo && out == null && !StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())) {
                // 付息债、贴现债、零息债，交割日+1
                // if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())
                // || StringUtil.equals(DictCodeList.息票品种_贴现.getValue(), secInfoModelDto.getBondsType())
                // || StringUtil.equals(DictCodeList.息票品种_零息.getValue(), secInfoModelDto.getBondsType())) {
                // 付息计划截止日对应的利息
                Map<String, BigDecimal> intMap = new HashMap<>();
                if (null != invScheduleList && !invScheduleList.isEmpty()) {
                    for (SecIntSchedule intSche : invScheduleList) {
                        intMap.put(intSche.getActualCutoffDate().toString(), intSche.getAmount());
                    }
                }

                if (intMap.containsKey(accurIntCalInParameter.getCalDate().toString())
                        && !((Variety.贴现.equals(secInfoModel.getBondsType())) || Variety.零息.equals(secInfoModel.getBondsType()))) {
                    out = new AccurIntCalOutParameter();
                    out.setInterest(
                            intMap.get(accurIntCalInParameter.getCalDate().toString()).multiply(new BigDecimal("100")).divide(
                                    curSecAmt, 12, RoundingMode.HALF_UP));
                    accurIntCalInParameter.setCalDate(accurIntCalInParameter.getCalDate());
                    AccurIntForSecheduleCalculator calculator2 = CalculatorFactory.createAccurIntForSecheduleCalculator();
                    if (accurIntCalInParameter.getPaperIrResetList() != null
                            && accurIntCalInParameter.getPaperIrResetList().size() > 1) {
                        accurIntCalInParameter.getSecInfoModel().setIntRateType(IntRateType.浮动利率);
                    }
                    out = calculator2.calculate(accurIntCalInParameter);
                }
                // }
            }
            // 交易所债券特殊处理end----------------------------------------------------------------

            if (out != null) {
                accurInt = ArithmeticUtil.round(out.getInterest(), SCALE, RoundingMode.HALF_UP);
            } else {
                // 从计算工厂中创建应计利息计算器
                AccurIntCalculator calc = CalculatorFactory.createAccurIntCalculator();
                // 计算利息
                AccurIntCalOutParameter accurIntCalOutParameter = calc.calculate(accurIntCalInParameter);
                accurInt = ArithmeticUtil.round(accurIntCalOutParameter.getInterest(), SCALE, RoundingMode.HALF_UP);
            }
            if (isJiaoYiSuo) {
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())) {
                    accurInt = BigDecimal.ZERO;
                }
            }
            // 单位应计利息
            if (dto.getUnitInt() == null) {
                if (isJiaoYiSuo) {
                    dto.setUnitInt(
                            ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                                    Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                            "SEC_INT_SCALE_EXCHANGE")) ? "8" : CacheUtil.getSysParam("SEC_INT_SCALE_EXCHANGE")),
                                    RoundingMode.HALF_UP));
                } else {
                    dto.setUnitInt(ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                            Integer.parseInt(StringUtils.isEmpty(
                                    CacheUtil.getSysParam("SEC_INT_SCALE_BANK")) ? "12" : CacheUtil.getSysParam("SEC_INT_SCALE_BANK")),
                            RoundingMode.HALF_UP));
                }
            }
            // 应计利息总额 = 单位应计利息 * 券面总额/100
            if (计算融入券) {
                dto.setIntegrateIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getUnitInt(), dto.getPrinAmt()), 100, 2));
            }
            dto.setIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getUnitInt(), dto.getPrinAmt()), 100, 2));

            // add by wl 交易方向选择行权时，应计利息及利息总额为0
            if ((StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), dto.getPs())
                    || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), dto.getPs()))) {
                if (dto.getUnitInt().compareTo(BigDecimal.ZERO) < 0) {
                    dto.setUnitInt(BigDecimal.ZERO);
                }
                if (dto.getIntAmt().compareTo(BigDecimal.ZERO) < 0) {
                    dto.setIntAmt(BigDecimal.ZERO);
                }
//                dto.setUnitInt(BigDecimal.ZERO);
//                dto.setIntAmt(BigDecimal.ZERO);
                if (计算融入券) {
                    dto.setIntegrateIntAmt(BigDecimal.ZERO);
                }
            }
        }
        try {
            // 单位净价
            if (StringUtil.equals(dto.getCalculateField(), "unitCprice")) {
                dto.setCpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
                        new BigDecimal(100), 2));
                if (计算融入券) {
                    dto.setIntegrateCpriceAmt(dto.getCpriceAmt());
                }
                // 全价总额 = 净价总额+ 利息总额
                dto.setFpriceAmt(ArithmeticUtil.doAdd(dto.getIntAmt(), dto.getCpriceAmt()));
                if (计算融入券) {
                    dto.setIntegrateFpriceAmt(ArithmeticUtil.doAdd(dto.getIntAmt(), dto.getCpriceAmt()));
                }
                // round（全价总额/券面总额，10）*100
                // 全价 = 单位应计利息 + 单位净价 (保持横向的一致性)
                dto.setUnitFprice(ArithmeticUtil.doAdd(dto.getUnitInt(), dto.getUnitCprice()));
            }
            // 全价总额
            else if (StringUtil.equals(dto.getCalculateField(), "fpriceAmt")) {
                // 单位全价 = 全价总额 * 100 / 券面总额
                dto.setUnitFprice(
                        ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getFpriceAmt(), new BigDecimal(100)), dto.getPrinAmt(), 8));
                dto.setCpriceAmt(ArithmeticUtil.subtract(dto.getFpriceAmt(), dto.getIntAmt()));
                if (计算融入券) {
                    dto.setIntegrateCpriceAmt(dto.getCpriceAmt());
                }
                // round（净价总额/券面总额，10）*100
                // 单位净价 = 全价-单位应计利息 (保持横向的一致性)
                dto.setUnitCprice(ArithmeticUtil.subtract(dto.getUnitFprice(), dto.getUnitInt()));
            }
            switch (dto.getCalculateField()) {
                // 单位净价
                case "unitCprice":
                    // 全价总额
                case "fpriceAmt":
                    // 全价总额==>到期收益率
                    yieldPriceInParameter.setFullPrice(dto.getUnitFprice()/* dto.getFpriceAmt() */);
                    yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                    dto.setYield(yieldPriceOutParameter.getMatYield());
                    if (secInfoModelDto.getNextExeDate() != null) {
                        if (priceYieldInParameter.getCalDate().compareTo(BasicUtil.dateToLocal(secInfoModelDto.getNextExeDate())) < 0) {
                            // 全价总额==>行权收益率
                            yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
                            yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
                            if (yieldPriceInParameter.getNextExePrice() != null) {
                                yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                                dto.setExerYield(yieldPriceOutParameter.getExeYield());
                            } else {
                                dto.setExerYield(null);
                            }
                        } else {
                            dto.setExerYield(BigDecimal.ZERO);
                        }
                    }
                    break;
                // 到期收益率
                case "yield":
                    // 到期收益率==>全价总额
                    priceYieldInParameter.setMatYield(dto.getYield());
                    priceYieldOutParameter = priceByYieldCalculator.calculate(priceYieldInParameter);
                    // dto.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                    dto.setUnitFprice(priceYieldOutParameter.getFullPrice());
                    // 全价总额==>行权收益率
                    if (secInfoModelDto.getNextExeDate() != null) {
                        if (priceYieldInParameter.getCalDate().compareTo(BasicUtil.dateToLocal(secInfoModelDto.getNextExeDate())) < 0) {
                            yieldPriceInParameter.setFullPrice(dto.getUnitFprice()/* dto.getFpriceAmt() */);
                            yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
                            yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
                            if (yieldPriceInParameter.getNextExePrice() != null) {
                                yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
//                                dto.setExerYield(yieldPriceOutParameter.getMatYield());
                                dto.setExerYield(yieldPriceOutParameter.getExeYield());
                            } else {
                                dto.setExerYield(null);
                            }
                        } else {
                            dto.setExerYield(BigDecimal.ZERO);
                        }
                    }
                    break;
                // 行权收益率
                case "exerYield":
                    // 行权收益率==>全价总额
                    if (secInfoModelDto.getNextExeDate() != null) {
                        if (priceYieldInParameter.getCalDate().compareTo(BasicUtil.dateToLocal(secInfoModelDto.getNextExeDate())) < 0) {
                            priceYieldInParameter.setMatYield(dto.getExerYield());
                            yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
                            yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
                            priceYieldOutParameter = priceByYieldCalculator.calculate(priceYieldInParameter);
                            dto.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                            if (计算融入券) {
                                dto.setIntegrateFpriceAmt(priceYieldOutParameter.getFullPrice());
                            }
                            // 全价总额==>到期收益率
                            yieldPriceInParameter.setFullPrice(dto.getFpriceAmt());
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                            dto.setYield(yieldPriceOutParameter.getMatYield());
                        } else {
                            dto.setYield(BigDecimal.ZERO);
                        }
                    }
                    break;
                default:
                    break;
            }
            // 到期收益率 或 行权收益率
            if (StringUtil.equals(dto.getCalculateField(), "yield") || StringUtil.equals(dto.getCalculateField(), "exerYield")) {
                // 全价总额 = 单位全价 * 券面总额/100
                dto.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getUnitFprice(), dto.getPrinAmt()),
                        new BigDecimal(100), 2));
                if (计算融入券) {
                    dto.setIntegrateFpriceAmt(dto.getFpriceAmt());
                }
                // 净价总额 = 全价总额 - 应计利息总额
                dto.setCpriceAmt(ArithmeticUtil.subtract(dto.getFpriceAmt(), dto.getIntAmt()));
                if (计算融入券) {
                    dto.setIntegrateCpriceAmt(dto.getCpriceAmt());
                }
                // 单位净价 = round（净价总额/券面总额，10）*100
                // 单位净价 = 全价 - 单位应计利息
                dto.setUnitCprice(ArithmeticUtil.subtract(dto.getUnitFprice(), dto.getUnitInt()));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if (dto.getYield() != null) {
            dto.setYield(ArithmeticUtil.multiply(dto.getYield(), new BigDecimal(100)));
        }
        if (dto.getExerYield() != null) {
            dto.setExerYield(ArithmeticUtil.multiply(dto.getExerYield(), new BigDecimal(100)));
        }
        // --------add by 裴家俊 2019/03/28 费用查询----start---
        try {
            BasicTradeFeeInputDto inputDto = new BasicTradeFeeInputDto();
            inputDto.setTradeAmt(dto.getTradeAmt());
            inputDto.setTradeDate(dto.getTradeDate());
            inputDto.setTradeMarket(dto.getTradeMarket());
            inputDto.setFinprodType2(BussinessDictCodeList.平安资产类型_债券.getValue());
            inputDto.setPs(dto.getPs());// 交易方向
            inputDto.setFpriceAmt(dto.getFpriceAmt());// 全价总额
            inputDto.setPrinAmt(dto.getPrinAmt());// 券面总额
            inputDto.setIsGrade("1");
            inputDto.setType6(dto.getType6());// 资产四级分类
            String type7 = restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getType7", dto, String.class);
            inputDto.setType7(type7);
            inputDto.setTradeMarketMode(dto.getTradeMarketMode());
            if (StringUtil.equals(BussinessDictCodeList.交易场所_银行间.getValue(), dto.getTradeMarket())) {
                // 登记托管机构用证券托管户的登记托管机构
                String registOrg = restTemplate.getForObject(
                        ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getRegistOrg?secAcctId="
                                + dto.getSecManageAcctId(),
                        String.class);
                inputDto.setRegistOrg(registOrg);
                // 债券一级市场无交易手续费，结算手续费仅上清所有。
                // 修改 --> 一级市场增加交易手续费，结算手续费无论中债和上清都有
                inputDto.setFeeTypeList(Arrays.asList(BussinessDictCodeList.费用类型_交易手续费.getValue(), BussinessDictCodeList.费用类型_结算手续费.getValue()));
                if (!StringUtil.isEmptyList(inputDto.getFeeTypeList())) {
                    List<TrdTradeFeeDetail> tradeFeeList = baseTradeFeeCommService.innerBankFeeDeal(inputDto);
                    if (dto.getTradeFeeList() == null) {
                        dto.setTradeFeeList(tradeFeeList);
                    } else {
                        dto.getTradeFeeList().addAll(tradeFeeList);
                    }
                }

            }
            if ((StringUtil.equals(BussinessDictCodeList.交易场所_上海证券交易所.getValue(), dto.getTradeMarket())
                    || StringUtil.equals(BussinessDictCodeList.交易场所_深圳证券交易所.getValue(), dto.getTradeMarket()))) {
                inputDto.setSecAcctId(dto.getSecManageAcctId());// 画面.证券账户
                inputDto.setFeeTypeList(Arrays.asList(BussinessDictCodeList.费用类型_佣金.getValue()));
                // 如果是二级市场或者二级交易，增加结算费经手费查询
                if (StringUtil.equals(BussinessDictCodeList.交易市场_二级市场.getValue(), dto.getTradeMarketMode())
                        || StringUtil.equals(BussinessDictCodeList.缴款方式_二级交易.getValue(), dto.getPayType())) {
                    inputDto.setFeeTypeList(Arrays.asList(BussinessDictCodeList.费用类型_佣金.getValue(),
                            BussinessDictCodeList.费用类型_结算费.getValue(),
                            BussinessDictCodeList.费用类型_经手费.getValue()));
                }
                //一级分销，根据一级市场规则计算
                if (StringUtil.equals(BussinessDictCodeList.缴款方式_一级分销.getValue(), dto.getPayType())) {
                    inputDto.setTradeMarketMode(BussinessDictCodeList.交易市场_一级市场.getValue());
                }
                //二级交易，根据二级市场规则计算
                if (StringUtil.equals(BussinessDictCodeList.缴款方式_二级交易.getValue(), dto.getPayType())) {
                    inputDto.setTradeMarketMode(BussinessDictCodeList.交易市场_二级市场.getValue());
                }
                List<TrdTradeFeeDetail> tradeFeeList = baseTradeFeeCommService.bourseFeeDeal(inputDto);
                if (StringUtil.equals(inputDto.getType6(), DictCodeList.WIND一级分类_含债券一级分类等_优先股.getValue())) {
                    inputDto.setFeeTypeList(Arrays.asList(BussinessDictCodeList.费用类型_印花税.getValue()));
                    inputDto.setSecAcctId(null);
                    List<TrdTradeFeeDetail> tradeFeeListBO2 = baseTradeFeeCommService.bourseFeeDeal(inputDto);
                    tradeFeeList.addAll(tradeFeeListBO2);
                }

                if (dto.getTradeFeeList() == null) {
                    dto.setTradeFeeList(tradeFeeList);
                } else {
                    dto.getTradeFeeList().addAll(tradeFeeList);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        // --------add by 裴家俊 2019/03/28 费用查询----end---
    }

    /**
     * 确认提醒 检查交割日是否为节假日，交割日如果是节假日 要提醒 交易方向为买入时校验交易所证券账户（券商存管账户）余额是否足够，如不足，提醒进行银证转账
     *
     * @param inputDto 画面输入参数
     * @param result   valid结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    public AjaxResult checkConfirmInfo(CfetsTrdBondInvestTradeDto inputDto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return null;
        }

        String confirmMsg = "";
        String changeLineMark = "<br/>";

        // //添加托管账户剩余现金校验 add by yjm 2019-07-03 start
        // //银行间 债券买入交易和逐笔划款的通道债券买入交易，提交时都没有判断托管户可用剩余现金是否足够
        // if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), inputDto.getPs())) {
        // if (StringUtil.equals(inputDto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())
        // || StringUtil.equals(inputDto.getPayType(), DictCodeListExt2.划款方式_逐笔划款.getValue())){
        // if (StringUtil.isNotEmpty(inputDto.getBussTrdAvailableCashQuery()) && StringUtil.isNotEmpty(inputDto.getTradeAmt())){
        // if (inputDto.getTradeAmt().compareTo(inputDto.getBussTrdAvailableCashQuery().getSecAcctBalance()) > 0){
        // if (StringUtil.isNotBlank(confirmMsg)) {
        // confirmMsg += changeLineMark;
        // }
        // confirmMsg += this.getMessage("","托管户剩余现金不足，请确认是否继续？");
        // }
        // }
        // }
        // }
        // //添加托管账户剩余现金校验 add by yjm 2019-07-03 end

        // 检查交割日是否为节假日，交割日如果是节假日 要提醒
        if (inputDto.getSettleDate() != null && StringUtil.isNotBlank(inputDto.getCalendarId())) {
            boolean isDealCalendarHoliday = dateCalService.isDealCalendarHoliday(inputDto.getSettleDate(),
                    inputDto.getCalendarId());
            if (isDealCalendarHoliday) {
                if (StringUtil.isNotBlank(confirmMsg)) {
                    confirmMsg += changeLineMark;
                }
                confirmMsg += getMessage("BUSS_COM_E_0039", getMessage("TrdBondInvestTrade002.settleDate"));
            }
        }

        if ((StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), inputDto.getTradeMarket())
                || StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), inputDto.getTradeMarket()))
                && StringUtil.isBlank(inputDto.getChlId())) {
            if (StringUtil.isNotBlank(inputDto.getZhilingtype()) && StringUtil.equals(inputDto.getZhilingtype(), "02")) {// 多组合指令
                for (BussTrdTransferOrderDto bussTrdTransferOrder : inputDto.getBussTrdTransferOrder()) {
                    if (CollectionUtils.isNotEmpty(inputDto.getBussTrdTransfenpeiDto())) {
                        for (int i = 0; i < inputDto.getBussTrdTransfenpeiDto().size(); i++) {
                            CfetsTrdBondInvestTradeDto tempDto = new CfetsTrdBondInvestTradeDto();
                            if (StringUtil.equals(inputDto.getBussTrdTransfenpeiDto().get(i).getPortfolioId(),
                                    bussTrdTransferOrder.getPortfolioId())) {
                                tempDto.setPortfolioId(bussTrdTransferOrder.getPortfolioId());
                                tempDto.setPs(inputDto.getPs());
                                tempDto.setCalendarId(inputDto.getCalendarId());
                                tempDto.setSecManageAcctId(inputDto.getBussTrdTransfenpeiDto().get(i).getSecManageAcctIdDuo());
                                tempDto.setTradeAmt(inputDto.getBussTrdTransfenpeiDto().get(i).getTradeAmt());
                                tempDto.setCcy(inputDto.getCcy());
                                //add by liuwenjuan 银证转账未提示问题改修 20200325 start
                                tempDto.setBussTrdTransferOrder(inputDto.getBussTrdTransferOrder());
                                //add by liuwenjuan 银证转账未提示问题改修 20200325 end
                                tempDto.setTradeAmtAndFee(inputDto.getBussTrdTransfenpeiDto().get(i).getTradeAmtAndFee());
                                confirmMsg = checkTradeMarket(tempDto, confirmMsg, changeLineMark, bussTrdTransferOrder);
                            }
                        }
                    }
                }
            } else {
                confirmMsg = checkTradeMarket(inputDto, confirmMsg, changeLineMark, inputDto.getBussTrdTransferOrder().get(0));
            }
        }

        if (StringUtils.equals(inputDto.getPs(), DictCodeList.交易方向_买入.getValue()) && StringUtil.isNotEmpty(inputDto.getChlId())
                && DictCodeListExt2.划款方式_一次性划款.getValue().equals(inputDto.getPayType())) {
            if (StringUtil.isNotBlank(inputDto.getZhilingtype()) && StringUtil.equals(inputDto.getZhilingtype(), "02")) {// 多组合指令
                for (BussTrdTransferOrderDto bussTrdTransferOrder : inputDto.getBussTrdTransferOrder()) {
                    if (CollectionUtils.isNotEmpty(inputDto.getBussTrdTransfenpeiDto())) {
                        for (int i = 0; i < inputDto.getBussTrdTransfenpeiDto().size(); i++) {
                            CfetsTrdBondInvestTradeDto tempDto = new CfetsTrdBondInvestTradeDto();
                            if (StringUtil.equals(inputDto.getBussTrdTransfenpeiDto().get(i).getPortfolioId(),
                                    bussTrdTransferOrder.getPortfolioId())) {
                                tempDto.setPortfolioId(bussTrdTransferOrder.getPortfolioId());
                                tempDto.setPs(inputDto.getPs());
                                tempDto.setCalendarId(inputDto.getCalendarId());
                                tempDto.setSecManageAcctId(inputDto.getBussTrdTransfenpeiDto().get(i).getSecManageAcctIdDuo());
                                tempDto.setTradeAmt(inputDto.getBussTrdTransfenpeiDto().get(i).getTradeAmt());
                                tempDto.setCcy(inputDto.getCcy());
                                tempDto.setChlId(inputDto.getChlId());
                                tempDto.setTradeAmtAndFee(inputDto.getBussTrdTransfenpeiDto().get(i).getTradeAmtAndFee());
                                confirmMsg = getYcxhk(tempDto, confirmMsg, changeLineMark);
                            }
                        }
                    }
                }
            } else {
                confirmMsg = getYcxhk(inputDto, confirmMsg, changeLineMark);
            }
        }

        // 买入交易需求要求添加组合T日现金剩余现金校验 add by yjm 2019-10-20 start
        if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), inputDto.getPs())
                && (StringUtil.isEmpty(inputDto.getChlId())
                || (StringUtil.isNotEmpty(inputDto.getChlId())
                && DictCodeListExt2.划款方式_逐笔划款.getValue().equals(inputDto.getPayType())))) {

            if (StringUtil.isNotBlank(inputDto.getZhilingtype()) && StringUtil.equals(inputDto.getZhilingtype(), "02")) {// 多组合指令
                for (BussTrdTransferOrderDto bussTrdTransferOrder : inputDto.getBussTrdTransferOrder()) {
                    if (CollectionUtils.isNotEmpty(inputDto.getBussTrdTransfenpeiDto())) {
                        for (int i = 0; i < inputDto.getBussTrdTransfenpeiDto().size(); i++) {
                            CfetsTrdBondInvestTradeDto tempDto = new CfetsTrdBondInvestTradeDto();
                            if (StringUtil.equals(inputDto.getBussTrdTransfenpeiDto().get(i).getPortfolioId(),
                                    bussTrdTransferOrder.getPortfolioId())) {
                                tempDto.setPortfolioId(bussTrdTransferOrder.getPortfolioId());
                                tempDto.setPs(inputDto.getPs());
                                tempDto.setCalendarId(inputDto.getCalendarId());
                                tempDto.setSecManageAcctId(inputDto.getBussTrdTransfenpeiDto().get(i).getSecManageAcctIdDuo());
                                tempDto.setTradeAmt(inputDto.getBussTrdTransfenpeiDto().get(i).getFpriceAmt());
                                tempDto.setCcy(inputDto.getCcy());
                                tempDto.setChlId(inputDto.getChlId());
                                tempDto.setBussTrdAvailableCashQueryavalibleCash(
                                        inputDto.getBussTrdTransfenpeiDto().get(i).getBussTrdAvailableCashQueryavalibleCash());
                                tempDto.setTradeAmtAndFee(inputDto.getBussTrdTransfenpeiDto().get(i).getTradeAmtAndFee());
                                confirmMsg = getPortfolioCanUserMoney(tempDto, confirmMsg, changeLineMark);
                            }
                        }
                    }
                }
            } else {
                // 单组合
                if (StringUtil.isNotEmpty(inputDto.getBussTrdAvailableCashQuery())
                        && StringUtil.isNotEmpty(inputDto.getTradeAmtAndFee())) {
                    if (StringUtil.isNotEmpty(inputDto.getBussTrdAvailableCashQuery().getAvalibleCash())
                            && inputDto.getTradeAmtAndFee().compareTo(inputDto.getBussTrdAvailableCashQuery().getAvalibleCash()) > 0) {
                        if (StringUtil.isNotBlank(confirmMsg)) {
                            confirmMsg += changeLineMark;
                        }
                        confirmMsg += "组合可用现金不足,当前最多可用金额为:<br/>" + inputDto.getBussTrdAvailableCashQuery().getAvalibleCash()
                                + "请确认是否继续？";
                    }
                }
            }

        }
        // 买入交易需求要求添加组合T日现金剩余现金校验 add by yjm 2019-10-20 end

        if (StringUtil.isNotBlank(confirmMsg)) {
            AjaxResult wr = new AjaxResult();
            wr.setData(confirmMsg);
            return wr;
        }

        return null;
    }

    private String getPortfolioCanUserMoney(CfetsTrdBondInvestTradeDto inputDto, String confirmMsg, String changeLineMark) {
        if (inputDto.getBussTrdAvailableCashQueryavalibleCash() != null && StringUtil.isNotEmpty(inputDto.getTradeAmtAndFee())) {
            if (inputDto.getTradeAmtAndFee().compareTo(inputDto.getBussTrdAvailableCashQueryavalibleCash()) > 0) {
                PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(inputDto.getPortfolioId());
                if (StringUtil.isNotBlank(confirmMsg)) {
                    confirmMsg += changeLineMark;
                }
                if (ptlPortfolio != null) {
                    confirmMsg += "组合(" + ptlPortfolio.getPortfolioName() + ")可用现金不足,当前最多可用金额为:<br/>"
                            + inputDto.getBussTrdAvailableCashQueryavalibleCash() + "请确认是否继续？";
                }
            }
        }
        return confirmMsg;
    }

    private String getYcxhk(CfetsTrdBondInvestTradeDto inputDto, String confirmMsg, String changeLineMark) {
        // 调用基础组件.【组合可用现金和实际现金查询更新组件.查询现金余额(可用)】
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("portfolioId", inputDto.getPortfolioId());
        paramMap.put("tradeDate", iPlatformDate.getSysDateStr());
        paramMap.put("ccy", inputDto.getCcy());
        paramMap.put("calendarId", inputDto.getCalendarId());
        paramMap.put("chlId", inputDto.getChlId());

        Map<String, String> resultMap = bussTrdAvailableCashQueryService.getAvailableCash(paramMap);
        BigDecimal 余额 = new BigDecimal(resultMap.get("T_2"));

        if (inputDto.getTradeAmtAndFee() != null && inputDto.getTradeAmtAndFee().compareTo(余额) > 0) {
            if (StringUtil.isNotBlank(confirmMsg)) {
                confirmMsg += changeLineMark;
            }
            confirmMsg += getMessage("TRD_C_0004", getMessage("invest.channelId"));
        }
        return confirmMsg;
    }

    private String checkTradeMarket(CfetsTrdBondInvestTradeDto inputDto, String confirmMsg, String changeLineMark,
                                    BussTrdTransferOrderDto bussTrdTransferOrder) {
        // 交易方向为买入时校验交易所证券账户（券商存管账户）余额是否足够，如不足，提醒进行银证转账
        if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), inputDto.getPs())
                && StringUtil.isNotBlank(inputDto.getSecManageAcctId()) && inputDto.getTradeAmtAndFee() != null
                && bussTrdAvailableCashQueryService != null && !StringUtil.isEmptyList(inputDto.getBussTrdTransferOrder())) {
            // 查询网点账号
            StlSecAcctInfo stlSecAcctInfo = stlSecAcctInfoMapper.selectByPrimaryKey(inputDto.getSecManageAcctId());

            // 划款指令付款账户
            String 划款指令付款账户 = bussTrdTransferOrder.getPayAcctNo();

            if (stlSecAcctInfo != null && StringUtil.equals(划款指令付款账户, stlSecAcctInfo.getTrdAcctId())) {
                // 调用基础组件.【组合可用现金和实际现金查询更新组件.查询现金余额(可用)】
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("portfolioId", inputDto.getPortfolioId());
                paramMap.put("acctId", stlSecAcctInfo.getTrdAcctId());
                paramMap.put("tradeDate", iPlatformDate.getSysDateStr());
                paramMap.put("ccy", inputDto.getCcy());
                paramMap.put("calendarId", inputDto.getCalendarId());
                // paramMap.put("acctType", DictCodeListExt1.资金账户类型_券商资金账户.getValue());
                // paramMap.put("finprodId", inputDto.getFinprodId());

                BigDecimal 余额 = bussTrdAvailableCashQueryService.getSecManageAvailableCash(paramMap);

                if (inputDto.getTradeAmtAndFee().compareTo(余额) > 0) {
//                    if (StringUtil.isNotBlank(confirmMsg)) {
//                        confirmMsg += changeLineMark;
//                    }
//                    confirmMsg += getMessage("TRD_C_0001");
                    confirmMsg = getMessage("TRD_C_0001");
                }
            }
        }
        return confirmMsg;
    }

    public List<SelectOptionDto> getPortfolioId(Map<String, Object> params) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getPortfolioId", params,
                SelectOptionDto.class);
    }

    public List<SelectOptionDto> selectPortfolioId(Map<String, Object> params) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/selectPortfolioId", params,
                SelectOptionDto.class);
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId)
            throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        CfetsTrdBondInvestTradeDto dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        if (taskId != null) {
            try {
//              return commonBusinessService.是否需要上级审批(dto.getCounterId(), dto.getTradeAmt(), LoginUserUtil.getUserName(),
//              DictCodeList.平安资产类型_债券.getValue(), dto.getWindtypeone(), dto.getTradeMarket(), dto.getPs()
//              // 资产类型改为授权类型 add by renping 2019-08-06 start
//              , dto.getFinprodId()
//          // 资产类型改为授权类型 add by renping 2019-08-06 end
//          );
                //多组合交易
                if (null != dto.getBussTrdTransfenpeiDto() && dto.getBussTrdTransfenpeiDto().size() > 0) {
                    dto.setPrinAmt(dto.getTotalFaceValue());
                }
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getPrinAmt(), LoginUserUtil.getUserName(),
                        DictCodeList.平安资产类型_债券.getValue(), dto.getWindtypeone(), dto.getTradeMarket(), dto.getPs()
                        , dto.getFinprodId(), entity.getWorkflowId());
                params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
                return commonBusinessService.是否需要上级审批(params);
            } catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        } else {
            return null;
        }
        // return null;
    }

    /**
     * 发送Murex接口处理
     *
     * @param entity     债券投资交易信息
     * @param revokeFlag 撤销标识
     * @see
     * @since 1.0
     */
    private void sendMurex(CfetsTrdBondInvestTradeDto entity, boolean revokeFlag) {
        if (StringUtil.isNotEmpty(entity.getPortfolioId())) {
            PaPtlPortfolio paPtlPortfolio = paPtlPortfolioMapper.selectByPrimaryKey(entity.getPortfolioId());
            if (paPtlPortfolio != null) {
                if (StringUtil.equals(paPtlPortfolio.getIsGuarantee(), DictCodeList.是否_是.getValue())) {
                    if (interfComponentService != null) {
                        List<Map<String, Object>> sendData = new ArrayList<>();
                        Map<String, Object> infoMap = new HashMap<>();

                        // 买：buy 卖：sell
                        infoMap.put("BUY_SELL_DIRECTION", "sell");// 卖出、行权
                        if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), entity.getPs())) {
                            infoMap.put("BUY_SELL_DIRECTION", "pay");// 买卖方向
                        }

                        FinProduct finProduct = finProductMapper.selectByPrimaryKey(entity.getFinprodId());
                        if (finProduct != null) {
                            // 债券定义维护时，规则为：CNY 债券代码.市场后缀
                            infoMap.put("BOND_NAME", finProduct.getCcy() + " " + finProduct.getFinprodMarketId());// 债券名称
                        }

                        // 银行间：CN CFETS;北金所：CN CFAE;深交所：CN SZSE A;上交所：CN SHSE A
                        if (StringUtil.equals(entity.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())) {
                            infoMap.put("BOND_BELONG_MARKET", "CN CFETS");// 债券所属市场
                        } else if (StringUtil.equals(entity.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                            infoMap.put("BOND_BELONG_MARKET", "CN SZSE A");
                        } else if (StringUtil.equals(entity.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                            infoMap.put("BOND_BELONG_MARKET", "CN SHSE A");
                        }

                        // lotSize=1,nominal=quantity乘以lotSzie
                        infoMap.put("CORPUS", entity.getTradeAmt().toPlainString());// 本金

                        // YYYYMMDD
                        infoMap.put("DELIVERY_DATE", entity.getSettleDateText());// 交割日期

                        infoMap.put("DELIVERY_CCY", entity.getCcy());// 交割币种

                        // 交易净价/交易全价任选其一， 如果提供计提利息，需要满足dirty price = clean price +accrued coupon
                        infoMap.put("TRAN_NET_PRICE_AMT", entity.getUnitCprice().toPlainString());// 交易净价

                        infoMap.put("ALL_PRICE", entity.getUnitFprice().toPlainString());// 全价

                        infoMap.put("DUE_ACCRUED_INTEREST", entity.getUnitInt().toPlainString());// 应记利息

                        // // 结算方式
                        // infoMap.put("SETTLE_MODE","dvp");// fop：纯券过户;dvp：券款对付

                        // // 增加的现金流
                        // infoMap.put("ADD_CASHFLOW",value);// 每条现金流用“|”分隔，现金流内元素用“；”分隔。 For
                        // Example:
                        // PAB_TEST001;C;20140602;CNY;-10000;comment;;;;;CAP;NOM;UPFR;;ADD|PAB_TEST001;C;20140603;CNY;10000;comment;;;;;CAP;NOM;NET;;ADD

                        // 交割金额
                        infoMap.put("DELIVERY_AMT", entity.getFpriceAmt().toPlainString());

                        // 交易全局流水号
                        infoMap.put("TRAN_ALL_SEQ_NO", entity.getTotleTradeId());

                        // 投组
                        infoMap.put("INVEST_GROUP", entity.getPortfolioId());

                        // 交易对手名称
                        if (entity.getTrdInvestPersonInfo() != null) {
                            if (entity.getTrdInvestPersonInfo().getCounterId() != null) {
                                MstCounterParty mstCounterParty = mstCounterPartyMapper.selectByPrimaryKey(
                                        entity.getTrdInvestPersonInfo().getCounterId());
                                if (mstCounterParty != null) {
                                    infoMap.put("COUNTER_PARTY_NAME", mstCounterParty.getCounterName());
                                }
                            }
                        }

                        // 成交日期
                        infoMap.put("TURNOVER_DATE", entity.getTradeDateText());

                        // // 上游系统
                        // infoMap.put("UPPER_SYS",value);
                        // // 清算方式
                        // infoMap.put("CLEARING_MODE",value);

                        infoMap.put("TRAN_OPERATION_TYPE", "A – NEW");// 交易操作类型
                        if (revokeFlag) {
                            infoMap.put("TRAN_OPERATION_TYPE", "R - Reverse");// 交易操作类型
                        }

                        // // 事件日期
                        // infoMap.put("EVENT_DATE",value);

                        // 交易员代码
                        if (entity.getTrdInvestPersonInfo() != null) {
                            infoMap.put("DEALER_ROLE_CODE", entity.getTrdInvestPersonInfo().getTrader());
                        }

                        // 备注
                        infoMap.put("REMARK", entity.getRemark());

                        // // 代保管标识
                        // infoMap.put("PROXY_SAVE_FLAG",value);
                        // // 自贸区特殊业务标志
                        // infoMap.put("FT_SPECIAL_BUSS_FLAG",value);
                        // // 业务模式
                        // infoMap.put("BUSS_MODEL",value);
                        // // 批复号
                        // infoMap.put("APPROVE_NO",value);
                        // // 内外交易标志
                        // infoMap.put("INSIDE_OUTSIDE_TRAN_FLAG",value);
                        // // 撮合交易标志
                        // infoMap.put("MERGER_TRAN_FLAG",value);
                        // // 费用类型
                        // infoMap.put("FEE_TYPE",value);
                        // // 费用金额
                        // infoMap.put("FEE_AMT",value);
                        // // 二级市场买卖债券手续费
                        // infoMap.put("SECONDARY_MARKET_BUY_SELL_BOND_FEE",value);
                        // // 资金账户名称
                        // infoMap.put("SEC_ACCT_NAME",value);
                        // // 资金开户行
                        // infoMap.put("SEC_OPEN_BRANCH",value);
                        // // 资金账号
                        // infoMap.put("SEC_ACCT_NO",value);
                        // // 资金开户行联行行号
                        // infoMap.put("SEC_OPEN_INTERBANK_BRANCH_ID",value);

                        sendData.add(infoMap);
                        try {
                            interfComponentService.sendMurexRequest(OrderCaller.BOND001, sendData);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }

    public String getChannelInfo(String parameters) {
        String temp = null;
        Map<String, Object> map = new HashMap<>();
        map.put("chlId", parameters);
        List<String> tempList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getChannelInfo", map, String.class);
        if (!StringUtil.isEmptyList(tempList)) {
            temp = tempList.get(0);

        }
        return temp;
    }

    /**
     * 根据成交编号查询cstp交易信息
     *
     * @param
     * @return cstp交易信息
     */
    public TrdBondInvestTradeCSTPDto querySecCstpInfoByNo(String fedealno, String entrustId, String preTradeId, String isCstpSure) {
        return restTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/querySecCstpInfoByNo?fedealno=" + fedealno
                        + "&entrustId=" + entrustId + "&preTradeId=" + preTradeId
                        + "&loginUserName=" + LoginUserUtil.getUserName() + "&isCstpSure=" + isCstpSure,
                TrdBondInvestTradeCSTPDto.class);
    }

    /**
     * 银行间成交编号联动费用设置
     *
     * @param dto 画面输入参数
     * @see
     * @since 1.0
     */
    public void calcFeeAmt(CfetsTrdBondInvestTradeDto dto) {
        // --------add by 裴家俊 2019/03/28 费用查询----start---
        try {
            dto.setPrinAmt(dto.getTradeAmt());

            BasicTradeFeeInputDto inputDto = new BasicTradeFeeInputDto();
            inputDto.setTradeDate(dto.getTradeDate());
            inputDto.setTradeMarket(dto.getTradeMarket());
            inputDto.setFinprodType2(BussinessDictCodeList.平安资产类型_债券.getValue());
            inputDto.setPs(dto.getPs());// 交易方向
            inputDto.setFpriceAmt(dto.getFpriceAmt());// 全价总额
            inputDto.setPrinAmt(dto.getPrinAmt());// 券面总额
            inputDto.setIsGrade("1");
            inputDto.setType6(dto.getType6());// 资产四级分类
            if (StringUtil.equals(BussinessDictCodeList.交易场所_银行间.getValue(), dto.getTradeMarket())) {
                // 登记托管机构用证券托管户的登记托管机构
                String registOrg = restTemplate.getForObject(
                        ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getRegistOrg?secAcctId="
                                + dto.getSecManageAcctId(),
                        String.class);
                inputDto.setRegistOrg(registOrg);
                inputDto.setFeeTypeList(
                        Arrays.asList(BussinessDictCodeList.费用类型_交易手续费.getValue(), BussinessDictCodeList.费用类型_结算手续费.getValue()));
                List<TrdTradeFeeDetail> tradeFeeList = baseTradeFeeCommService.innerBankFeeDeal(inputDto);
                dto.setTradeFeeList(tradeFeeList);
            }
            if (StringUtil.equals(BussinessDictCodeList.交易场所_上海证券交易所.getValue(), dto.getTradeMarket())
                    || StringUtil.equals(BussinessDictCodeList.交易场所_深圳证券交易所.getValue(), dto.getTradeMarket())) {
                inputDto.setSecAcctId(dto.getSecManageAcctId());// 画面.证券账户
                inputDto.setFeeTypeList(Arrays.asList(BussinessDictCodeList.费用类型_佣金.getValue()));
                List<TrdTradeFeeDetail> tradeFeeList = baseTradeFeeCommService.bourseFeeDeal(inputDto);
                if (StringUtil.equals(inputDto.getType6(), DictCodeList.WIND一级分类_含债券一级分类等_优先股.getValue())) {
                    inputDto.setFeeTypeList(Arrays.asList(BussinessDictCodeList.费用类型_印花税.getValue()));
                    inputDto.setSecAcctId(null);
                    List<TrdTradeFeeDetail> tradeFeeListBO2 = baseTradeFeeCommService.bourseFeeDeal(inputDto);
                    tradeFeeList.addAll(tradeFeeListBO2);
                }
                dto.setTradeFeeList(tradeFeeList);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        // --------add by 裴家俊 2019/03/28 费用查询----end---
    }

    /**
     * 流程退回处理
     *
     * @param entity 画面参数
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public void doBack(CfetsTrdBondInvestTradeDto entity) {
        // 对Cstp交易处理
        uodateCstpMessage(entity.getWorkflowId());
        // 根据交易编号，删除可用，质押，融入，融出，在途FLOW持仓
        //    AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
        // 卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
//        if (entity.getPs().equals(DictCodeList.交易方向_买入.getValue()) || entity.getPs().equals(DictCodeList.交易方向_申购.getValue())) {
//            // 交易方向为买入时，退回动作后 释放原占用的额度 Add by xkni 20190830
//            isNeedOccupy(entity, true, true, true);
//        }

        //根据工作流代码查询交易相关信息
        WflTrdProductDealAdd wflTrdProductDealAdd = restTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME
                        + "/common/trdcommonbase/selectWflTrdProductDealAdd/" + entity.getWorkflowId(), WflTrdProductDealAdd.class);
        //删除授信占用
        if (null != wflTrdProductDealAdd && null != wflTrdProductDealAdd.getTradeId() && !StringUtil.isNotEmpty(entity.getRevokeReason())) {
            riskCreditListCommonValidatorService.deleteCredit(wflTrdProductDealAdd.getTradeId());
        }

    }

    @Override
    public void doRevoke(CfetsTrdBondInvestTradeDto entity) {
        // 对Cstp交易处理
        uodateCstpMessage(entity.getWorkflowId());
        // 根据交易编号，删除可用，质押，融入，融出，在途FLOW持仓
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
        // 卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
//        if (entity.getPs().equals(DictCodeList.交易方向_买入.getValue()) || entity.getPs().equals(DictCodeList.交易方向_申购.getValue())) {
//            // 交易方向为买入时，退回动作后 释放原占用的额度 Add by xkni 20190830
//            isNeedOccupy(entity, true, true, true);
//        }

        //根据工作流代码查询交易相关信息
        WflTrdProductDealAdd wflTrdProductDealAdd = restTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME
                        + "/common/trdcommonbase/selectWflTrdProductDealAdd/" + entity.getWorkflowId(), WflTrdProductDealAdd.class);
        //删除授信占用
        if (null != wflTrdProductDealAdd && null != wflTrdProductDealAdd.getTradeId() && !StringUtil.isNotEmpty(entity.getRevokeReason())) {
            riskCreditListCommonValidatorService.deleteCredit(wflTrdProductDealAdd.getTradeId());
        }

    }

    /**
     * 查询债券cstp交易下拉框
     *
     * @param param 输入参数
     * @return
     */
    public Page<SelectOptionDto> querySecCstpInfoList(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/querySecCstpInfoList", param,
                SelectOptionDto.class);
    }

    /**
     * 银行间成交编号联动费用设置
     *
     * @param inputDto 画面参数
     * @return 计算结果
     * @see
     * @since 1.0
     */
    public AjaxResult calculateFeeData(CfetsTrdBondInvestTradeDto inputDto) {
        AjaxResult ajaxResult = new AjaxResult();

        // 计算逻辑
        this.calcFeeAmt(inputDto);
        ajaxResult.setData(inputDto);

        return ajaxResult;
    }

    public BigDecimal getTongDaoCash(Map<String, Object> param) {
        param.put("platDate", iPlatformDate.getSysDate());
        BigDecimal bigDecimal = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getTongDaoCash", param, BigDecimal.class);
        return bigDecimal;
    }

    public BigDecimal getBankCash(Map<String, Object> param) {
        String secManageAcctId = (String) param.get("secManageAcctId");
        // 查询网点账号
        StlSecAcctInfo stlSecAcctInfo = stlSecAcctInfoMapper.selectByPrimaryKey(secManageAcctId);
        if (stlSecAcctInfo != null) {
            // 调用基础组件.【组合可用现金和实际现金查询更新组件.查询现金余额(可用)】
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("portfolioId", (String) param.get("portfolioId"));
            paramMap.put("acctId", stlSecAcctInfo.getTrdAcctId());
            paramMap.put("tradeDate", iPlatformDate.getSysDateStr());
            paramMap.put("ccy", (String) param.get("ccy"));
            paramMap.put("calendarId", (String) param.get("calendarId"));
            // paramMap.put("acctType", DictCodeListExt1.资金账户类型_券商资金账户.getValue());
            // paramMap.put("finprodId", inputDto.getFinprodId());

            BigDecimal bigDecimal = bussTrdAvailableCashQueryService.getSecManageAvailableCash(paramMap);

            return bigDecimal;
        }
        return BigDecimal.ZERO;
    }

    public Boolean isAllTradeAmtByFTradeId(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/isAllTradeAmtByFTradeId",
                map, Boolean.class);
    }

    public Boolean isAllTradeAmtByPreTradeId(Map<String, Object> map) {
        String preTradeId = map.get("preTradeId").toString();
        String tradeId = map.get("tradeId").toString();
        if (StringUtil.isNotBlank(preTradeId) && StringUtil.isNotBlank(tradeId)) {
            TrdProductDealPreKey key = new TrdProductDealPreKey();
            key.setTradeId(preTradeId);
            key.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            TrdProductDealPre trdProductDealPre = trdProductDealPreMapper.selectByPrimaryKey(key);
            InterfTradSecdeal interfTradSecdeal = interfTradSecdealMapper.selectByPrimaryKey(tradeId);
            if (interfTradSecdeal != null && trdProductDealPre != null) {
                if (interfTradSecdeal.getPrinamt().compareTo(trdProductDealPre.getTradeAmt()) == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 查询交易页面债券的市值净价和摊销净价（吉林银行产品化需求）
     *
     * @param dto CfetsTrdBondInvestTradeDto
     * @return
     * @see
     * @since 1.0 malinlin 2019.12.2
     */
    public SecCpriceInfoDto getCprice(CfetsTrdBondInvestTradeDto dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("finprodId", dto.getFinprodId());
        // 查询债券基本信息
        SecInfoModelDto secInfoModelDto = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecInfoModel", map, SecInfoModelDto.class);
        SecInfoModel secInfoModel = toSecInfoModel(secInfoModelDto);

        // 获取债券市值净价和摊销净价
        map.put("portfolioId", dto.getPortfolioId());
        map.put("invAim", dto.getInvAim());
        map.put("chlId", dto.getChlId());
        map.put("counterId", dto.getCounterId());
        map.put("tradeDate", DateUtil.format(dto.getTradeDate()));
        SecCpriceInfoDto secCpriceInfoDto = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getCprice", map, SecCpriceInfoDto.class);
        // 若债券类型是贴现债或零息债，查询到的摊销净价实际上是全价，此时调用页面计算按钮调用的方法，根据全价反算债券摊销净价
        if (secCpriceInfoDto.getDscprice() != null
                && (Variety.贴现.equals(secInfoModel.getBondsType()) || Variety.零息.equals(secInfoModel.getBondsType()))) {
            // 校验输入参数
            List<ValidMessage> errorList = this.validCalcAmt(dto);
            // 券面总额若为空
            if (dto.getTradeAmt() == null) {
                // 存入券面总额一个固定值
                dto.setTradeAmt(new BigDecimal(1000000));
                return secCpriceInfoDto;
            }
            // 全价总额 = 全价*券面总额 /100
            dto.setFpriceAmt(secCpriceInfoDto.getDscprice().multiply(dto.getTradeAmt()).divide(new BigDecimal(100)));
            // 按照全价总额反推净价
            dto.setCalculateField("fpriceAmt");
            // 单位全价
            dto.setUnitFprice(secCpriceInfoDto.getDscprice());
            // 计算逻辑
            this.calcAmt(dto);
            // 将计算后得到的净价放入摊销净价字段中
            secCpriceInfoDto.setDscprice(dto.getUnitCprice());
        }

        return secCpriceInfoDto;
    }

    public static BussinessCodeParameter getBussinessCodeParameter(String ps) {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), ps)) {
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
        } else {
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券卖出);
        }
        return codeParameter;
    }

    /**
     * 查询交易对手是否为净值高频或者货币
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public String getCounterId(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getCounterId", map,
                String.class);
    }

    private void setWorkflowId(CfetsTrdBondInvestTradeDto entity) {
        if (entity.getPaTrdFinProductDealTotal() != null) {
            entity.getPaTrdFinProductDealTotal().setWorkflowId(entity.getWorkflowId());
        }
        if (entity.getTrdDealTotalAdd() != null) {
            entity.getTrdDealTotalAdd().setWorkflowId(entity.getWorkflowId());
        }
        if (entity.getTrdProductDeals() != null) {
            entity.getTrdProductDeals().forEach(trdProductDeal -> trdProductDeal.setWorkflowId(entity.getWorkflowId()));
        }
        if (entity.getTrdProductDealAdds() != null) {
            entity.getTrdProductDealAdds().forEach(trdProductDeal -> trdProductDeal.setWorkflowId(entity.getWorkflowId()));
        }
        if (entity.getTrdProductDealAdds() != null) {
            entity.getPaTrdProductDeals().forEach(trdProductDeal -> trdProductDeal.setWorkflowId(entity.getWorkflowId()));
        }
        if (entity.getBussTrdTransferOrder() != null) {
            entity.getBussTrdTransferOrder().get(0).setWorkflowId(entity.getWorkflowId());
        }
    }

    /**
     * 查找买入交易之后的卖出交易
     *
     * @param tradeId 交易编号
     * @return 债券投资交易信息
     * @see
     * @since 1.0
     */
    public int selectTradeT02(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/selectTradeT02/" + tradeId, null,
                Integer.class);
    }

    /**
     * 债券投资交易新建画面查看模式查询债券投资交易信息
     *
     * @param tradeId 交易编号
     * @return 债券投资交易信息
     * @see
     * @since 1.0
     */
    public String getReverseTradeDtoInvAim(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getReverseTradeDtoInvAim/" + tradeId, null,
                String.class);
    }

    /* add by msk 新增查询通道划款交易 TISHKBS-1038  20201111 start */

    /**
     * 查找通道划款交易
     *
     * @param map 交易编号
     * @return 通道划款交易信息
     * @see
     * @since 1.0
     */
    public int selectchlTrade(Map<String, Object> map) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/selectchlTrade/", map,
                Integer.class);
    }
    /* add by msk 新增查询通道划款交易 TISHKBS-1038  20201111 end */

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void fastSave(CfetsTrdBondInvestTradeDto dto) {
        //****
        prepareEntity(dto);
        dto.setActPortfolioId(dto.getPortfolioId());
        dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, dto);
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setPortfolioId(dto.getActPortfolioId());
        trdFinProductDealTotal.setSettleSpeed(Long.valueOf(dto.getSettleSpeedStr()));
        trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);

        TrdDealTotalAdd trdDealTotalAdd = 生成汇总附属表(dto);
        trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);


        StlPayOrders stlPayOrders = new StlPayOrders();
        for (BussTrdTransferOrderDto order : dto.getBussTrdTransferOrder()) {
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, order);
            String orderId = "";
            if (baseGetBusinessId != null) {
                BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                bussinessCodeParameter.setSeqNo("9998");
                orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
            } else {
                orderId = IDGenerator.generate();
            }
            stlPayOrders.setOrderId(orderId);
            stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
            stlPayOrders.setTradeId(dto.getTotleTradeId());
            stlPayOrdersMapper.insertSelective(stlPayOrders);
        }
    }

    public Map<String, Object> getTradeMarketCash(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        BigDecimal cashS = BigDecimal.ZERO;
        StlSecAcctInfo secAcct = stlSecAcctInfoMapper.selectByPrimaryKey((String) param.get("secAcctId"));
        if (secAcct != null && StringUtil.isNotEmpty(secAcct.getTrdAcctId())) {
            param.put("acctId", secAcct.getTrdAcctId());
            cashS = bussTrdAvailableCashQueryService.getSecManageAvailableCash(param);
        }
        result.put("cashS", cashS);
        return result;
    }

    /**
     * 功能描述 ：银行间债券交易自动录单
     *
     * @param * @param entity
     * @return void
     * @author zhangnian
     * @date 2021/1/21 0021
     */
    public Map<String, Object> secAutoTrade(CfetsTrdBondInvestTradeDto entity) {
        Map<String, Object> resultMap = makeBondTradeDto(entity);
        List<CfetsTrdBondInvestTradeDto> trdBondInvestTrades = (List<CfetsTrdBondInvestTradeDto>) resultMap.get("trdBondInvestTrades");
        if (!StringUtil.isEmptyList(trdBondInvestTrades)) {
            trdBondInvestTrades.stream().forEach(item -> {
                dealAutoTradeFeeDetial(item);
                prepareEntityInterf(item);
                saveAutoTrade(item);
            });
        }
        return resultMap;
    }

    /**
     * 功能描述 : 银行间下行组装交易实体类
     *
     * @param * @param CfetsTrdBondInvestTradeDto
     * @return void
     * @author zhangnian
     * @date 2021/1/21 0021
     */
    public Map<String, Object> makeBondTradeDto(CfetsTrdBondInvestTradeDto entity) {
        Map<String, Object> resultMap = new HashMap<>();
        List<CfetsTrdBondInvestTradeDto> trdBondInvestTrades = new ArrayList<>();
        // 通过成交单编号查询数据,为生成交易数据做准备
        InterfTradSecdealDto bondCstpInfo = new InterfTradSecdealDto();
        BeanUtil.copyPropertiesIgnoreNull(bondCstpInfo, entity.getInterfTradSecdeal());
        //是否多组合
        List<PtlAllocationDto> ptlAllocationDtos = new ArrayList<>();
        //全价总额
        BigDecimal fpriceAmt = bondCstpInfo.getSettamt();
        //券面总额
        BigDecimal prinamt = bondCstpInfo.getPrinamt();
        //结算手续费  交易手续费计算
        List<TrdTradeFeeDetail> trdTradeFeeDetails = calFeeDetail(entity);
        if (!StringUtil.isEmptyList(trdTradeFeeDetails)) {
            trdTradeFeeDetails.forEach(item -> {
                if (StringUtils.equals(item.getFeeType(), BussinessDictCodeList.费用类型_交易手续费.getValue())) {
                    resultMap.put("settFeeamt", item.getFeeAmt());
                    return;
                }
                if (StringUtils.equals(item.getFeeType(), BussinessDictCodeList.费用类型_结算手续费.getValue())) {
                    resultMap.put("dealFeeamt", item.getFeeAmt());
                    return;
                }
            });
        }
        if (StringUtil.isNotEmpty(entity.getQuoteId())) {
            ptlAllocationDtos = cstpCommonService.selectEtrReByQuoteId(entity.getQuoteId());
            //多组合
            if (!StringUtil.isEmptyList(ptlAllocationDtos)) {
                Map<String, Object> map = new HashMap<>();
                map.put("shareAmt", null != prinamt ? prinamt : BigDecimal.ZERO);
                map.put("fpriceAmt", null != fpriceAmt ? fpriceAmt : BigDecimal.ZERO);
                ptlAllocationDtos = cstpCommonService.组合分配拆分(ptlAllocationDtos, map);
                map.put("trdTradeFeeDetails", trdTradeFeeDetails);
                ptlAllocationDtos = cstpCommonService.交易费用拆分(ptlAllocationDtos, map);
            }
            //单组合
            else {
                PtlAllocationDto ptlAllocationDto = new PtlAllocationDto();
                ptlAllocationDto.setEntrustId(entity.getEntrustId());
                ptlAllocationDto.setFpriceAmt(fpriceAmt);
                ptlAllocationDto.setShareAmt(prinamt);
                ptlAllocationDto.setPortfolioId(entity.getPortfolioId());
                ptlAllocationDto.setTradeFeeList(trdTradeFeeDetails);
                ptlAllocationDtos.add(ptlAllocationDto);
            }
        }

        //构造交易实体
        ptlAllocationDtos.stream().forEach(item -> {
            CfetsTrdBondInvestTradeDto CfetsTrdBondInvestTradeDto = new CfetsTrdBondInvestTradeDto();
            BeanUtil.copyPropertiesIgnoreNull(CfetsTrdBondInvestTradeDto, entity);
            CfetsTrdBondInvestTradeDto.setPortfolioId(item.getPortfolioId());
            CfetsTrdBondInvestTradeDto.setEntrustId(item.getEntrustId());
            //全价总额
            BigDecimal fpriceAmt2 = item.getFpriceAmt();
            //券面总额
            BigDecimal prinamt2 = item.getShareAmt();
            //全价总额
            BigDecimal cpriceAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(bondCstpInfo.getPrice8(), prinamt2), BigDecimal.valueOf(100), 2);
            //应计利息总额
            BigDecimal intAmt = ArithmeticUtil.subtract(fpriceAmt2, cpriceAmt);
            // 交易编号
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            if (StringUtil.equals(BussinessDictCodeList.交易方向_买入.getValue(), CfetsTrdBondInvestTradeDto.getPs())) {
                codeParameter.setCommonBusinessType(BussinessDictCodeList.共同业务类型_债券买入);
            } else {
                codeParameter.setCommonBusinessType(BussinessDictCodeList.共同业务类型_债券卖出);
            }
            CfetsTrdBondInvestTradeDto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            CfetsTrdBondInvestTradeDto.setfTradeId(bondCstpInfo.getFedealno());
            CfetsTrdBondInvestTradeDto.setAutoTradeFlag(true);
            // 金融产品类型（投管分类）
            CfetsTrdBondInvestTradeDto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
            // 金融产品类型（估值核算）
            CfetsTrdBondInvestTradeDto.setFinprodType(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
            // 交易日期
            CfetsTrdBondInvestTradeDto.setTradeDate(bondCstpInfo.getDealdate());
            // 交割日期
            CfetsTrdBondInvestTradeDto.setSettleDate(bondCstpInfo.getSettdate());
            // 证券托管户
            CfetsTrdBondInvestTradeDto.setSecManageAcctId(bondCstpInfo.getOurdepositaccountnumber());
            // 交易场所
            CfetsTrdBondInvestTradeDto.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
            // 交易市场
            CfetsTrdBondInvestTradeDto.setTradeMarketMode(BussinessDictCodeList.交易市场_二级市场.getValue());
            // 交易平台
            CfetsTrdBondInvestTradeDto.setTradePlat(DictCodeList.交易平台_外汇交易中心.getValue());
            // 投资目的
            Map<String, Object> map = new HashMap<>();
            map.put("portfolioId", CfetsTrdBondInvestTradeDto.getPortfolioId());
            CfetsTrdBondInvestTradeDto.setInvAim(getInvAim(map));
            // 币种
            CfetsTrdBondInvestTradeDto.setCcy(BussinessDictCodeList.币种_人民币.getValue());
            // 交易日历
            CfetsTrdBondInvestTradeDto.setCalendarId("03");
            // 债券一级分类
            FinProductType finProductType = queryFinProductTypeByFinProdId(CfetsTrdBondInvestTradeDto.getFinprodId());
            if (finProductType != null) {
                CfetsTrdBondInvestTradeDto.setWindtypeone(finProductType.getType4());
            }
            // 清算速度
            if (DateUtil.isSameDate(bondCstpInfo.getDealdate(), bondCstpInfo.getSettdate())) {
                CfetsTrdBondInvestTradeDto.setSettleSpeedStr(BussinessDictCodeList.清算速度_T加0.getValue());
            } else {
                CfetsTrdBondInvestTradeDto.setSettleSpeedStr(BussinessDictCodeList.清算速度_T加1.getValue());
            }

            // =====================金额信息=====================
            // 券面总额、净价、应计利息、全价、净价总额、应计利息总额、全价总额
            // 交易金额
            CfetsTrdBondInvestTradeDto.setTradeAmt(prinamt2);
            CfetsTrdBondInvestTradeDto.setTotalFaceValue(prinamt2);
            // 交易金额(原交易金额被券面总额所用)
            CfetsTrdBondInvestTradeDto.setTradeRepAmt(fpriceAmt2);
            CfetsTrdBondInvestTradeDto.setTradeAmtAndFee(fpriceAmt2);
            // 净价 price8
            CfetsTrdBondInvestTradeDto.setUnitCprice(bondCstpInfo.getPrice8());
            // 设置费用费用信息
           /* CfetsTrdBondInvestTradeDto.setCalculateField("unitCprice");
            calcAmt(CfetsTrdBondInvestTradeDto);*/
            CfetsTrdBondInvestTradeDto.setTradeFeeList(item.getTradeFeeList());
            // 全价
            CfetsTrdBondInvestTradeDto.setUnitFprice(bondCstpInfo.getDirtyprice());
            // 全价总额
            CfetsTrdBondInvestTradeDto.setFpriceAmt(fpriceAmt2);
            // 净价总额 = 净价 * 券面总额 / 100
            CfetsTrdBondInvestTradeDto.setCpriceAmt(cpriceAmt);
            // 应计利息总额
            CfetsTrdBondInvestTradeDto.setIntAmt(ArithmeticUtil.subtract(fpriceAmt2, cpriceAmt));
            // 单位应计利息
            CfetsTrdBondInvestTradeDto.setUnitInt(bondCstpInfo.getAccruedinterestamt());
            // =====================金额信息=====================

            // =====================人员信息=====================
            CfetsTrdInvestPersonInfoDto trdInvestPersonInfo = new CfetsTrdInvestPersonInfoDto();
            // 本方交易员
            //trdInvestPersonInfo.setTrader(
            //       StringUtil.isNotEmpty(bondCstpInfo.getTrad()) ? bondCstpInfo.getTrad() : LoginUserUtil.getUserName());
            trdInvestPersonInfo.setTrader(CfetsTrdBondInvestTradeDto.getTrader());
            // 交易对手
            //trdInvestPersonInfo.setCounterId(bondCstpInfo.getCno());
            trdInvestPersonInfo.setCounterId(CfetsTrdBondInvestTradeDto.getCounterId());
            trdInvestPersonInfo.setIsOutTrade(BussinessDictCodeList.是否_是.getValue());
            // 对手方交易员
            trdInvestPersonInfo.setCounterTrader(CfetsTrdBondInvestTradeDto.getCounterTrader());
            //trdInvestPersonInfo.setCounterTrader(bondCstpInfo.getClienttrader());
            // 联系方式
            trdInvestPersonInfo.setContactInfo(bondCstpInfo.getCountertelephone());
            CfetsTrdBondInvestTradeDto.setTrdInvestPersonInfo(trdInvestPersonInfo);

            // =====================人员信息=====================

            // =====================划款指令信息=====================
            List<BussTrdTransferOrderDto> bussTrdTransferOrder = new ArrayList<>();
            BussSettleOrderParameterDto bussSettleOrderParameterDto = new BussSettleOrderParameterDto();
            bussSettleOrderParameterDto.setIsTotalTrade(BussinessDictCodeList.是否_否.getValue());
            bussSettleOrderParameterDto.setBankLendingType(null);
            bussSettleOrderParameterDto.setPortfolioId(CfetsTrdBondInvestTradeDto.getPortfolioId());
            TrdProductDeal deal = new TrdProductDeal();
            deal.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
            deal.setSettleDate(CfetsTrdBondInvestTradeDto.getSettleDate());
            deal.setTradeDate(CfetsTrdBondInvestTradeDto.getTradeDate());
            deal.setPs(CfetsTrdBondInvestTradeDto.getPs());
            deal.setCcy(CfetsTrdBondInvestTradeDto.getCcy());
            deal.setTradeAmt(fpriceAmt2);
            deal.setCounterId(CfetsTrdBondInvestTradeDto.getCounterId());
            deal.setTradeMarket(CfetsTrdBondInvestTradeDto.getTradeMarket());
            deal.setTradePlat(CfetsTrdBondInvestTradeDto.getTradePlat());
            deal.setSecManageAcctId(CfetsTrdBondInvestTradeDto.getSecManageAcctId());
            deal.setTradeMarketMode(CfetsTrdBondInvestTradeDto.getTradeMarketMode());
            bussSettleOrderParameterDto.setDeal(deal);
            BussTrdTransferOrderDto bussTrdTransferOrderDto = bussTrdTransferOrderService.generateOrder(bussSettleOrderParameterDto);
            bussTrdTransferOrderDto.setPayAcctNoList(null);
            bussTrdTransferOrderDto.setRecAcctNoList(null);
            bussTrdTransferOrderDto.setIsTotalTrade(BussinessDictCodeList.是否_是.getValue());
            if (StringUtil.equals(BussinessDictCodeList.账户来源_自营.getValue(), CfetsTrdBondInvestTradeDto.getSecAcctSource())
                    && StringUtil.equals(CfetsTrdBondInvestTradeDto.getPs(), BussinessDictCodeList.交易方向_买入.getValue())) {
                bussTrdTransferOrderDto.setIsNeedSend(BussinessDictCodeList.是否_否.getValue());
            }
            bussTrdTransferOrder.add(bussTrdTransferOrderDto);
            CfetsTrdBondInvestTradeDto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(bussTrdTransferOrder));
            // =====================划款指令信息=====================

            // 金融产品交易Entity 构造开始----------------------------------------------------------
            // 分支序号
            CfetsTrdBondInvestTradeDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
            // 单位面值
            CfetsTrdBondInvestTradeDto.setParValue(getUnitPrice(CfetsTrdBondInvestTradeDto.getFinprodId(), CfetsTrdBondInvestTradeDto.getSettleDate()));
            // 交易本金 = 画面.净价总额
            CfetsTrdBondInvestTradeDto.setPrinAmt(CfetsTrdBondInvestTradeDto.getCpriceAmt());
            // 交易份额 = 画面.券面总额/100
            CfetsTrdBondInvestTradeDto.setShareAmt(ArithmeticUtil.divide(prinamt2, new BigDecimal(100), 4, RoundingMode.HALF_UP));
            //下行接口自动生成交易明细   到期收益率和行权收益率  由接口带出
            // 到期收益率 = 画面.到期收益率/100
            if (null != bondCstpInfo.getYield8()) {
                CfetsTrdBondInvestTradeDto.setYield(ArithmeticUtil.multiply(bondCstpInfo.getYield8(), new BigDecimal(100)));
            }
            // 行权收益率 = 画面.行权收益率/100
            if (null != bondCstpInfo.getStrikeyield()) {
                CfetsTrdBondInvestTradeDto.setExerYield(ArithmeticUtil.multiply(bondCstpInfo.getStrikeyield(), new BigDecimal(100)));
            }
            // 是否结清 = 数据字典缓存.是否.是
            CfetsTrdBondInvestTradeDto.setIsClean(BussinessDictCodeList.是否_是.getValue());
            CfetsTrdBondInvestTradeDto.setIsCancel(BussinessDictCodeList.是否_否.getValue());
            if (trdInvestPersonInfo != null) {
                // 本方交易员
                //CfetsTrdBondInvestTradeDto.setTrader(trdInvestPersonInfo.getTrader());
                // 交易对手
                //CfetsTrdBondInvestTradeDto.setCounterId(trdInvestPersonInfo.getCounterId());
                // 是否外部交易
                CfetsTrdBondInvestTradeDto.setIsOutTrade(trdInvestPersonInfo.getIsOutTrade());
                // 对手方交易员
                //CfetsTrdBondInvestTradeDto.setCounterTrader(trdInvestPersonInfo.getCounterTrader());
                // 联系方式
                CfetsTrdBondInvestTradeDto.setCounterContact(trdInvestPersonInfo.getContactInfo());
            }

            // 清算速度
            CfetsTrdBondInvestTradeDto.setSettleSpeed(1L);
            if (StringUtil.equals(BussinessDictCodeList.清算速度_T加0.getValue(), CfetsTrdBondInvestTradeDto.getSettleSpeedStr())) {
                CfetsTrdBondInvestTradeDto.setSettleSpeed(0L);
            }
            //成交明细接口处理交易明细  默认本方券
            CfetsTrdBondInvestTradeDto.setOwnTradeAmt(prinamt2);
            CfetsTrdBondInvestTradeDto.setOwnFpriceAmt(fpriceAmt2);
            CfetsTrdBondInvestTradeDto.setOwnIntAmt(intAmt);
            // 交易对手
            //CfetsTrdBondInvestTradeDto.setCounterId(bondCstpInfo.getCno());
            //清算方式
            CfetsTrdBondInvestTradeDto.setClearType(bondCstpInfo.getClearingmethod());
            //结算方式
            CfetsTrdBondInvestTradeDto.setDeliveryType(bondCstpInfo.getDeliverytype());
            CfetsTrdBondInvestTradeDto.setTradeStatus(BussinessDictCodeList.交易状态_初始.getValue());
            // =========================流程信息=========================
            // 画面模式
            CfetsTrdBondInvestTradeDto.setPageModel("0");
            // 操作类型
            CfetsTrdBondInvestTradeDto.setOperateType("submit");
            // 是否来自代办
            CfetsTrdBondInvestTradeDto.setTodoListFlag("false");
            // 导入标识 为后面doComplete处理交易相关表判断
            CfetsTrdBondInvestTradeDto.setImportFlag(false);
            trdBondInvestTrades.add(CfetsTrdBondInvestTradeDto);
        });
        resultMap.put("ptlAllocationDtos", ptlAllocationDtos);
        resultMap.put("trdBondInvestTrades", trdBondInvestTrades);
        return resultMap;
    }

    /**
     * 功能描述: 给对应页面的佣金，结算费，手续费成员变量字段设置值
     *
     * @param * @param CfetsTrdBondInvestTradeDto
     * @return void
     * @author zhangnian
     * @date 2021/1/21 0021
     */
    public void dealAutoTradeFeeDetial(CfetsTrdBondInvestTradeDto CfetsTrdBondInvestTradeDto) {
        if (!StringUtil.isEmptyList(CfetsTrdBondInvestTradeDto.getTradeFeeList())) {
            for (TrdTradeFeeDetail tradeFeeDto : CfetsTrdBondInvestTradeDto.getTradeFeeList()) {
                if (!StringUtil.isNotEmpty(tradeFeeDto.getIsPayWithTrade())) tradeFeeDto.setIsPayWithTrade("N");
                //给对应页面的佣金，结算费，手续费成员变量字段设置值
                if (StringUtil.equals(tradeFeeDto.getFeeType(), BussinessDictCodeList.费用类型_佣金.getValue()) && tradeFeeDto.getFeeAmt() != null) {
                    CfetsTrdBondInvestTradeDto.setCommissionAmt(CfetsTrdBondInvestTradeDto.getFeeAmt());
                    if ((StringUtil.equals(CfetsTrdBondInvestTradeDto.getTradeMarket(), BussinessDictCodeList.交易场所_上海证券交易所.getValue()) ||
                            StringUtil.equals(CfetsTrdBondInvestTradeDto.getTradeMarket(), BussinessDictCodeList.交易场所_深圳证券交易所.getValue())) &&
                            StringUtil.equals(CfetsTrdBondInvestTradeDto.getTradeMarketMode(), BussinessDictCodeList.交易市场_一级市场.getValue())) {
                        CfetsTrdBondInvestTradeDto.setCommissionAmt(BigDecimal.ZERO);
                    }
                    if (StringUtil.equals(CfetsTrdBondInvestTradeDto.getPs(), BussinessDictCodeList.交易方向_买入.getValue())) {
                        CfetsTrdBondInvestTradeDto.setTradeRepAmt(CfetsTrdBondInvestTradeDto.getTradeRepAmt().add(CfetsTrdBondInvestTradeDto.getCommissionAmt()));
                    } else if (StringUtil.equals(CfetsTrdBondInvestTradeDto.getPs(), BussinessDictCodeList.交易方向_卖出.getValue())) {
                        CfetsTrdBondInvestTradeDto.setTradeRepAmt(CfetsTrdBondInvestTradeDto.getTradeRepAmt().subtract(CfetsTrdBondInvestTradeDto.getCommissionAmt()));
                    }

                }
                if (StringUtil.equals(tradeFeeDto.getFeeType(), BussinessDictCodeList.费用类型_交易手续费.getValue()) && tradeFeeDto.getFeeAmt() != null) {
                    CfetsTrdBondInvestTradeDto.setProcedureAmt(tradeFeeDto.getFeeAmt());
                    CfetsTrdBondInvestTradeDto.setProcedureAmtRate(tradeFeeDto.getFeeRate());
                }
                if (StringUtil.equals(tradeFeeDto.getFeeType(), BussinessDictCodeList.费用类型_结算手续费.getValue()) && tradeFeeDto.getFeeAmt() != null) {
//                    CfetsTrdBondInvestTradeDto.setSettleAmt(tradeFeeDto.getFeeAmt());
                    CfetsTrdBondInvestTradeDto.setSettleServiceAmt(tradeFeeDto.getFeeAmt());
                }
            }
        }
        //将甲类户和对应的交易手续费、结算费、佣金设置为0。银行间一级市场的交易手续费、结算费设置为0
        /*if(StringUtil.isNotEmpty(CfetsTrdBondInvestTradeDto.getSecManageAcctId())){
            if(bizCommonController.isJiaLeiHu(CfetsTrdBondInvestTradeDto.getSecManageAcctId())){
                CfetsTrdBondInvestTradeDto.setCommissionAmt(BigDecimal.ZERO);
                CfetsTrdBondInvestTradeDto.setProcedureAmt(BigDecimal.ZERO);
                CfetsTrdBondInvestTradeDto.setSettleAmt(BigDecimal.ZERO);
            }else{
                if(StringUtil.isNotEmpty(CfetsTrdBondInvestTradeDto.getTradeMarket())&&StringUtil.isNotEmpty(CfetsTrdBondInvestTradeDto.getTradeMarketMode())
                        &&bizCommonController.isYhjAndOneTradeMarket(CfetsTrdBondInvestTradeDto.getTradeMarketMode(), CfetsTrdBondInvestTradeDto.getTradeMarket())){
                    CfetsTrdBondInvestTradeDto.setProcedureAmt(BigDecimal.ZERO);
                    CfetsTrdBondInvestTradeDto.setSettleAmt(BigDecimal.ZERO);
                }
            }
        }*/
    }

    public void prepareEntityInterf(CfetsTrdBondInvestTradeDto dto) {
        setDealData(dto);

        // 汇总扩展表
        dto.setPaTrdFinProductDealTotal(生成汇总扩展表(dto));

        // 汇总附属表
        dto.setTrdDealTotalAdd(生成汇总附属表(dto));

        // 设置费用信息
        TrdTradeFeeTotal trdTradeFeeDetail = null;
        if (dto.getChannelFee() != null && dto.getChannelFee().compareTo(BigDecimal.ZERO) > 0) {
            trdTradeFeeDetail = AllTradeCommonUtil.genChannelTradeFeeDetailTotle(dto.getTotleTradeId(), dto.getChannelFee(),
                    dto.getCollectType(), dto.getIsOutTrade());
        }
        List<TrdTradeFeeTotal> ttfdList = getTrdTradeFeeDetails(dto);
        if (trdTradeFeeDetail != null) {
            ttfdList.add(trdTradeFeeDetail);
        }
        //dto.setFeeAmt(get费用金额(ttfdList, null, null));
        dto.setFeeAmtPay(get费用金额(ttfdList, DictCodeList.是否_是.getValue(), null));
        //dto.setFeeAmtUnpay(get费用金额(ttfdList, DictCodeList.是否_否.getValue(), null));
        dto.setTrdTradeFeeTotals(ttfdList);

        List<TrdProductDealAdd> trdProductDealAdds = new ArrayList<>();
        dto.setTrdProductDealAdds(trdProductDealAdds);
        List<PaTrdProductDeal> paTrdProductDeals = new ArrayList<>();
        List<PtlTrade> ptlTrades = new ArrayList<>();
        // 设置交易费用明细
        List<TrdTradeFeeDetail> trdTradeFeeDetails = new ArrayList<>();
        dto.setTrdTradeFeeDetails(trdTradeFeeDetails);

        // 指令汇总交易录入时，未区分本方券，这里做个判断，本方券设置成券面总额
        if (dto.getOwnFpriceAmt() == null) {
            dto.setOwnFpriceAmt(dto.getFpriceAmt());
        }

        if (dto.getOwnIntAmt() == null) {
            dto.setOwnIntAmt(dto.getIntAmt());
        }

        if (dto.getOwnTradeAmt() == null) {
            dto.setOwnTradeAmt(dto.getTradeAmt());
        }

        // 设置交易表
        List<TrdProductDeal> deals = new ArrayList<>();
        dto.setTrdProductDeals(deals);
        TrdProductDeal deal本方 = null;
        if (
            /* 本方券券面总额大于0，登录本方券的交易 */
                dto.getOwnTradeAmt() != null && dto.getOwnTradeAmt().compareTo(BigDecimal.ZERO) > 0) {
            deal本方 = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal本方, dto);
            // 清算速度
            deal本方.setTradeId(baseGetBusinessId.buildBussinessCode(getBussinessCodeParameter(deal本方.getPs())));
            deal本方.setCpriceAmt(ArithmeticUtil.subtract(dto.getOwnFpriceAmt(), dto.getOwnIntAmt()));
            // 交易本金 = 画面.净价总额
            deal本方.setPrinAmt(ArithmeticUtil.subtract(dto.getOwnFpriceAmt(), dto.getOwnIntAmt()));
            // 交易份额 = 画面.券面总额/100
            deal本方.setShareAmt(ArithmeticUtil.divide(dto.getOwnTradeAmt(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
            deal本方.setIntAmt(dto.getOwnIntAmt());
            deal本方.setFpriceAmt(dto.getOwnFpriceAmt());
            //占比 = 本方券面总额/(本方券面总额+融入券总额)
            BigDecimal 占比 = ArithmeticUtil.divide(dto.getOwnTradeAmt(),
                    ArithmeticUtil.doAdd(dto.getOwnTradeAmt(), dto.getIntegrateTradeAmt()), 12);
            //deal本方.setFeeAmt(get费用金额(ttfdList, null, 占比));
            deal本方.setFeeAmtPay(get费用金额(ttfdList, DictCodeList.是否_是.getValue(), 占比));
            //deal本方.setFeeAmtUnpay(get费用金额(ttfdList, DictCodeList.是否_否.getValue(), 占比));
            deal本方.setTradeAmt(get交易金额(dto.getOwnFpriceAmt(), dto.getPs(), deal本方.getFeeAmtPay()));
            //证券账户处理
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                Map<String, Object> secManageAcctIdMap = new HashMap<>();
                secManageAcctIdMap.put("portfolioId", dto.getPortfolioId());
                secManageAcctIdMap.put("finprodId", dto.getFinprodId());
                deal本方.setSecManageAcctId(qryPortfolioSecAcctId(secManageAcctIdMap));
            }
            //投资目的
            Map<String, Object> map = new HashMap<>();
            map.put("portfolioId", dto.getPortfolioId());
            deal本方.setInvAim(getInvAim(map));
            deals.add(deal本方);

            // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
            TrdProductDealAdd trdProductDealAdd本方 = new TrdProductDealAdd();
            trdProductDealAdd本方.setTradeId(deal本方.getTradeId()); // 交易编号
            trdProductDealAdd本方.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
            trdProductDealAdd本方.setEntrustId(dto.getEntrustId()); // 委托代码
            // trdProductDealAdd.setProdId(dto.getFinprodId()); // 理财产品代码
            trdProductDealAdd本方.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
            trdProductDealAdd本方.setWorkflowId(dto.getWorkflowId()); // 工作流代码
            // 推荐方 by zhuyongkang 20191023 start
            trdProductDealAdd本方.setAssetRecommandOrg(dto.getAssetRecommandOrg());
            // 推荐方 by zhuyongkang 20191023 start
            trdProductDealAdds.add(trdProductDealAdd本方);

            // 组合交易表Entity 构造开始----------------------------------------------------------
            生成组合交易(dto, deal本方.getTradeId(), ptlTrades);

            // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
            生成交易扩展表(dto, dto.getTrdInvestPersonInfo(), deal本方.getTradeId(), paTrdProductDeals);


            trdTradeFeeDetails.addAll(
                    生成交易费用明细数据(ttfdList, deal本方, 占比, null));
        }

        if (checkIfNeed融入券(dto)) {
            TrdProductDeal deal融入 = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal融入, dto);
            deal融入.setTradeId(baseGetBusinessId.buildBussinessCode(getBussinessCodeParameter(deal融入.getPs())));
            deal融入.setCpriceAmt(ArithmeticUtil.subtract(dto.getIntegrateFpriceAmt(), dto.getIntegrateIntAmt()));
            // 交易本金 = 画面.净价总额
            deal融入.setPrinAmt(ArithmeticUtil.subtract(dto.getIntegrateFpriceAmt(), dto.getIntegrateIntAmt()));
            // 交易份额 = 画面.券面总额/100
            deal融入.setShareAmt(ArithmeticUtil.divide(dto.getIntegrateTradeAmt(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
            deal融入.setIntAmt(dto.getIntegrateIntAmt());
            deal融入.setFpriceAmt(dto.getIntegrateFpriceAmt());
            deal融入.setFeeAmt(ArithmeticUtil.subtract(dto.getFeeAmt(), deal本方 == null ? null : deal本方.getFeeAmt()));
            deal融入.setFeeAmtPay(ArithmeticUtil.subtract(dto.getFeeAmtPay(), deal本方 == null ? null : deal本方.getFeeAmtPay()));
            deal融入.setFeeAmtUnpay(ArithmeticUtil.subtract(dto.getFeeAmtUnpay(), deal本方 == null ? null : deal本方.getFeeAmtUnpay()));
            deal融入.setTradeAmt(get交易金额(dto.getIntegrateFpriceAmt(), dto.getPs(), deal融入.getFeeAmtPay()));
            deal融入.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
            deals.add(deal融入);

            trdTradeFeeDetails.addAll(生成交易费用明细数据(ttfdList, deal融入, null, trdTradeFeeDetails));

            TrdProductDealAdd trdProductDealAdd融入 = new TrdProductDealAdd();
            trdProductDealAdd融入.setTradeId(deal融入.getTradeId()); // 交易编号
            trdProductDealAdd融入.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
            trdProductDealAdd融入.setEntrustId(dto.getEntrustId()); // 委托代码
            // trdProductDealAdd.setProdId(dto.getFinprodId()); // 理财产品代码
            trdProductDealAdd融入.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
            trdProductDealAdd融入.setWorkflowId(dto.getWorkflowId()); // 工作流代码
            // 推荐方 by zhuyongkang 20191023 start
            trdProductDealAdd融入.setAssetRecommandOrg(dto.getAssetRecommandOrg());
            // 推荐方 by zhuyongkang 20191023 start
            trdProductDealAdds.add(trdProductDealAdd融入);

            生成组合交易(dto, deal融入.getTradeId(), ptlTrades);

            生成交易扩展表(dto, dto.getTrdInvestPersonInfo(), deal融入.getTradeId(), paTrdProductDeals);
        }
        // 交易编号、融产品代码、金融产品类型（投管分类）、证券管理户代码、关联系统交易编号、投资目的、交易日、
        // 起息日、到期日、交割日、单位净价、单位利息、单位全价、净价总额、交易场所、交易市场、交易平台、交易币种、交易方向、利息总额、全价总额、备注
        // 以上信息为画面上项目，不需要特殊设置
        // 金融产品交易Entity 构造结束----------------------------------------------------------

        dto.setPtlTrade(ptlTrades);
        // 组合交易表Entity 构造结束----------------------------------------------------------

        // 划款指令表Entity 构造开始----------------------------------------------------------
        if (bussTrdTransferOrderService != null) {
            // 调用划款指令共通业务组件的保存处理，根据输出结果加工形成金融产品交易DTO的要素数据
            if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getTotleTradeId()); // 交易编号
                dto.getBussTrdTransferOrder().get(0).setOrderStatus(DictCodeListExt1.指令状态_新建.getValue()); // 指令状态
                dto.getBussTrdTransferOrder().get(0).setWorkflowId(dto.getWorkflowId()); // 工作流代码
                dto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            }
        }
        dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getTotleTradeId());
        // 划款指令表Entity 构造结束----------------------------------------------------------

        dto.setPaTrdProductDeals(paTrdProductDeals);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
    }

    /**
     * 功能描述 : 成交明细自动生成交易数据
     *
     * @param * @param CfetsTrdBondInvestTradeDto
     * @return void
     * @author zhangnian
     * @date 2021/1/18 0018
     */
    public void saveAutoTrade(CfetsTrdBondInvestTradeDto entity) {
        {
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, entity);
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            trdFinProductDealTotal.setPortfolioId(entity.getActPortfolioId());
            trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
            PaTrdFinProductDealTotal paTrdFinDealTotal = entity.getPaTrdFinProductDealTotal();
            paTrdFinProductDealTotalMapper.insertSelective(paTrdFinDealTotal);

            TrdDealTotalAdd trdDealTotalAdd = entity.getTrdDealTotalAdd();
            trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);

            entity.getTrdProductDealAdds().forEach(trdProductDealAdd -> {
                trdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
                trdProductDealAdd.setAssetRecommandOrg(entity.getAssetRecommandOrg());
                trdProductDealAddMapper.insertSelective(trdProductDealAdd);
                trdProductDealAdd.setPortfolioId(entity.getPortfolioId());
            });

            // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
            entity.getPaTrdProductDeals().forEach(paTrdProductDeal -> {
                paTrdProductDealMapper.insertSelective(paTrdProductDeal);
            });

            StlPayOrders stlPayOrders = new StlPayOrders();
            for (BussTrdTransferOrderDto dto : entity.getBussTrdTransferOrder()) {
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, dto);
                stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
                stlPayOrders.setTradeId(entity.getTotleTradeId());
                stlPayOrders.setWorkflowId(entity.getWorkflowId());
                stlPayOrdersMapper.insertSelective(stlPayOrders);
            }

            entity.getTrdTradeFeeDetails().forEach(
                    trdTradeFeeDetail -> trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail));

            entity.getPtlTrade().forEach(trdTradeFeeDetail -> ptlTradeMapper.insertSelective(trdTradeFeeDetail));

            entity.getTrdTradeFeeTotals().forEach(trdTradeFeeDetail -> {
                if (!StringUtil.isNotEmpty(trdTradeFeeDetail.getIsPayWithTrade()))
                    trdTradeFeeDetail.setIsPayWithTrade("N");
                trdTradeFeeTotalMapper.insertSelective(trdTradeFeeDetail);
            });

            this.deleteBussFlowData(entity.getWorkflowId());

            // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
            for (TrdProductDeal trdProductDeal : entity.getTrdProductDeals()) {
                trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
                trdProductDealMapper.insertSelective(trdProductDeal);


                // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
                FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                finProductTradeDto.setTotalFaceValue(entity.getTotalFaceValue());// 劵面总额
                finProductTradeDto.setNeedSum(true);
                BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
                //设置预录入的成交编号
                if (StringUtil.isNotBlank(entity.getPreTradeId())) {
                    baseTradeProductDealDto.setPreTradeId(entity.getPreTradeId());
                }
                if (StringUtil.isNotBlank(entity.getfTradeId())) {
                    baseTradeProductDealDto.setfTradeId(entity.getfTradeId());
                }
                baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
                entity.getTrdProductDealAdds().forEach(trdProductDealAdd -> {
                    if (trdProductDeal.getTradeId() != null
                            && trdProductDeal.getTradeId().equals(trdProductDealAdd.getTradeId())) {
                        baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);
                    }
                });


                if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())
                        && !DictCodeListExt1.投资目的_卖空.getValue().equals(trdProductDeal.getInvAim())) {
                    baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(entity.getBussTrdTransferOrder().get(0)));
                }

                // 委托代码不为空设置委托编号
                if (StringUtil.isNotEmpty(entity.getEntrustId())
                        && !DictCodeListExt1.投资目的_卖空.getValue().equals(trdProductDeal.getInvAim())) {
                    baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
                }

                // 一级市场标记不为空设置
                if (StringUtil.isNotEmpty(entity.getBondFirstSign())
                        && !DictCodeListExt1.投资目的_卖空.getValue().equals(trdProductDeal.getInvAim())) {
                    baseTradeProductDealDto.setBondFirstSign(entity.getBondFirstSign());
                }

                finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
                if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                        finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                    });
                }
                //String tradeId1 = trdProductDeal.getTradeId();
                trdProductDeal.setTradeId(entity.getTotleTradeId()); // 让生成的组合流水的tradeId是汇总表的值

                // add by etliu 20210128 start
                // 正常交易录单，买入的指令现金和卖出的指令持仓是在流程发起时处理的，所以下行交易自动生效时，需要单独调用指令可用持仓处理逻辑
                if (StringUtil.isNotBlank(entity.getQuoteId())) {
                    if (StringUtil.isNotEmpty(finProductTradeDto.getBussRepoSec())) {
                        baseTradeProductDealDto.setBussRepoSec(
                                finProductTradeDto.getBussRepoSec());
                    }
                    baseEtrCashPositionCommService.executeEtrCashPosition(baseTradeProductDealDto,
                            DictCodeList.持仓类型_可用, false, finProductTradeDto.getIeFlag(),
                            finProductTradeDto.getIsOverdueDeal());
                }
                // add by etliu 20210128 end

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
            //成交明细
            //不修改共通---报价生成的可用头寸  单独删除处理
            if (StringUtil.isNotEmpty(entity.getQuoteId())) {
                deletePtlPositionFlow(entity.getQuoteId());
            }
        }
    }

    public List<TrdTradeFeeDetail> calFeeDetail(CfetsTrdBondInvestTradeDto entity) {
        CfetsTrdBondInvestTradeDto CfetsTrdBondInvestTradeDto = new CfetsTrdBondInvestTradeDto();
        InterfTradSecdealDto bondCstpInfo = new InterfTradSecdealDto();
        BeanUtil.copyPropertiesIgnoreNull(bondCstpInfo, entity.getInterfTradSecdeal());
        //全价总额
        BigDecimal fpriceAmt = bondCstpInfo.getSettamt();
        //券面总额
        BigDecimal prinamt = bondCstpInfo.getPrinamt();
        BeanUtil.copyPropertiesIgnoreNull(CfetsTrdBondInvestTradeDto, entity);
        // 金融产品类型（投管分类）
        CfetsTrdBondInvestTradeDto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        // 金融产品类型（估值核算）
        CfetsTrdBondInvestTradeDto.setFinprodType(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        // 交易日期
        CfetsTrdBondInvestTradeDto.setTradeDate(bondCstpInfo.getDealdate());
        // 交割日期
        CfetsTrdBondInvestTradeDto.setSettleDate(bondCstpInfo.getSettdate());
        // 证券托管户
        CfetsTrdBondInvestTradeDto.setSecManageAcctId(bondCstpInfo.getOurdepositaccountnumber());
        // 交易场所
        CfetsTrdBondInvestTradeDto.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
        // 交易市场
        CfetsTrdBondInvestTradeDto.setTradeMarketMode(BussinessDictCodeList.交易市场_二级市场.getValue());
        // 交易平台
        CfetsTrdBondInvestTradeDto.setTradePlat(DictCodeList.交易平台_外汇交易中心.getValue());
        // 币种
        CfetsTrdBondInvestTradeDto.setCcy(BussinessDictCodeList.币种_人民币.getValue());
        // 交易日历
        CfetsTrdBondInvestTradeDto.setCalendarId("03");
        // 债券一级分类
        FinProductType finProductType = queryFinProductTypeByFinProdId(CfetsTrdBondInvestTradeDto.getFinprodId());
        if (finProductType != null) {
            CfetsTrdBondInvestTradeDto.setWindtypeone(finProductType.getType4());
        }
        // 清算速度
        if (DateUtil.isSameDate(bondCstpInfo.getDealdate(), bondCstpInfo.getSettdate())) {
            CfetsTrdBondInvestTradeDto.setSettleSpeedStr(BussinessDictCodeList.清算速度_T加0.getValue());
        } else {
            CfetsTrdBondInvestTradeDto.setSettleSpeedStr(BussinessDictCodeList.清算速度_T加1.getValue());
        }

        // =====================金额信息=====================
        // 券面总额、净价、应计利息、全价、净价总额、应计利息总额、全价总额
        // 交易金额
        CfetsTrdBondInvestTradeDto.setTradeAmt(prinamt);
        CfetsTrdBondInvestTradeDto.setTotalFaceValue(prinamt);
        // 交易金额(原交易金额被券面总额所用)
        CfetsTrdBondInvestTradeDto.setTradeRepAmt(fpriceAmt);
        CfetsTrdBondInvestTradeDto.setTradeAmtAndFee(fpriceAmt);
        // 净价 price8
        CfetsTrdBondInvestTradeDto.setUnitCprice(bondCstpInfo.getPrice8());
        // 设置费用费用信息
        CfetsTrdBondInvestTradeDto.setCalculateField("unitCprice");
        calcAmt(CfetsTrdBondInvestTradeDto);
        return CfetsTrdBondInvestTradeDto.getTradeFeeList();
    }

    /**
     * 获取投资目的
     *
     * @return 投资目的
     */
    public String getInvAim(Map<String, Object> map) {
        String invAim = "";
        String portfolioId = (String) map.get("portfolioId");
        PtlPortfolio ptlPortfolio = null;
        //PaPtlPortfolio paPtlPortfolio = null;
        if (StringUtil.isNotEmpty(portfolioId)) {
            ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(portfolioId);
            //paPtlPortfolio = paPtlPortfolioMapper.selectByPrimaryKey(portfolioId);
        }
        /*if (paPtlPortfolio != null) {
            if(StringUtil.equals(paPtlPortfolio.getValuationMethod(), BussinessDictCodeList.组合估值方法_成本法.getValue())){
                invAim = BussinessDictCodeList.投资目的_摊余成本类.getValue();
            }
            else if(StringUtil.equals(paPtlPortfolio.getValuationMethod(), BussinessDictCodeList.组合估值方法_市价法.getValue())){
                invAim = BussinessDictCodeList.投资目的_公允价值变动计入当期损益.getValue();
            }
            else if(StringUtil.equals(paPtlPortfolio.getValuationMethod(),BussinessDictCodeList.组合估值方法_其他.getValue())){
                invAim = BussinessDictCodeList.投资目的_公允价值变动计入其他综合收益.getValue();
            }
        }*/
        invAim = BussinessDictCodeList.投资目的_公允价值变动计入当期损益.getValue();
        if (null != ptlPortfolio) {
            if (StringUtil.equals(ptlPortfolio.getProfitType(), DictCodeList.收益类型_货币型.getValue())
                    || StringUtil.equals(ptlPortfolio.getProfitType(), DictCodeList.收益类型_收益型.getValue())) {
                invAim = BussinessDictCodeList.投资目的_摊余成本类.getValue();
            }
        }
        return invAim;
    }

    /**
     * 根据金融产品代码查询金融产品分类表
     *
     * @param finprodId 金融产品代码
     * @return 金融产品分类表
     */
    public FinProductType queryFinProductTypeByFinProdId(String finprodId) {
        if (StringUtil.isNotEmpty(finprodId)) {
            FinProductTypeKey key = new FinProductTypeKey();
            key.setFinprodId(finprodId);
            key.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
            return finProductTypeMapper.selectByPrimaryKey(key);
        }

        return null;
    }

    private String qryPortfolioSecAcctId(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/qryPortfolioSecAcctId", param,
                String.class);
    }

    /**
     * @param * @param quoteId
     * @return void
     * @author zhangnian
     * @date 2021/1/21 0021
     */
    public void deletePtlPositionFlow(String quoteId) {
        baseAssetCashPositionCommMapper.deleteCashPositionByOrderId(quoteId);
        baseAssetCashPositionCommMapper.deletePositionByTradeId(quoteId);
    }

    public InterfTradSecdeal getInterfTradSecdeal(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getInterfTradSecdeal",
                map, InterfTradSecdeal.class);
    }

    public List<TrdTradeFeeDetail> getAllFeeByFTradeId(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getAllFeeByFTradeId",
                map, TrdTradeFeeDetail.class);
    }

    /**
     * 根据资产代码查询是否是上市标的
     *
     * @param finprodId
     * @return 是否是上市标的，大于0则为是
     * @see
     * @since 1.0
     */
    public int selectIssueAsset(String finprodId) {
        return restTemplate.getForObject(
                ServiceApplication.ASSET_SERVICE_NAME + "/TrdBondMarketTrade/selectIssueAsset?finprodId=" + finprodId,
                Integer.class);
    }

    /**
     * 查询上一日估值净价
     *
     * @author jiangpeng
     */
    public BigDecimal getLastNetAmortizationPrice(Map<String, Object> paramMap) {
        if (StringUtil.isEmpty((String) paramMap.get("sysdate"))) {
            paramMap.put("sysdate", iPlatformDate.getSysDateStr());
        }
        String sysdate = (String) paramMap.get("sysdate");
        paramMap.put("sysdate", DateUtil.addDay(sysdate, -1));
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/getLastNetAmortizationPrice", paramMap,
                BigDecimal.class);
    }

    public void baseTradeCommExecute(FinProductTradeDto finProductTradeDto) {
        if (finProductTradeDto == null) {
            return;
        }
        //处理CSTP下行数据
       /* if(finProductTradeDto.getBaseTradeProductDealDtos() != null && !finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()
                && finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal() != null &&
                StringUtil.isNotBlank(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getfTradeId())){
            if(StringUtil.equals(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getBusiType(),DictCodeList.共通业务类型_债券类资产交易.getValue())){
                baseTradeProductDealCommServiceImpl.updateInterfForZhaiJuan(finProductTradeDto);
            }else if(StringUtil.equals(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getBusiType(),DictCodeList.共通业务类型_质押式回购交易.getValue())){
                baseTradeProductDealCommServiceImpl.updateInterfForZhiYaShi(finProductTradeDto);
            }
        }*/
        String workflowStatus = finProductTradeDto.getWorkflowStatus();
        if (DictCodeListExt1.交易流程状态_流程中.getValue().equals(workflowStatus)) {
            deal流程中(finProductTradeDto);
        } else if (DictCodeListExt1.交易流程状态_有效.getValue().equals(workflowStatus)) {
            deal有效(finProductTradeDto);
        } else if (DictCodeListExt1.交易流程状态_撤销.getValue().equals(workflowStatus)) {
            deal撤销(finProductTradeDto);
        }
        return;
    }

    /**
     * 处理流程中状态
     *
     * @param finProductTradeDto
     * @return CommonResult
     */
    private CommonResult deal流程中(FinProductTradeDto finProductTradeDto) {
        if (finProductTradeDto.getBaseTradeProductDealDtos() == null || finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()) {
            return new CommonResult(CommonResult.FAILD, "没有数据需要处理");
        }
        // 1.调用【组合可用持仓与实际持仓查询更新组件】与【组合可用现金与实际现金查询更新组件】执行持仓与现金的插入操作
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    private CommonResult deal撤销(FinProductTradeDto finProductTradeDto) {
        if (finProductTradeDto.getBaseTradeProductDealDtos() == null || finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()) {
            return new CommonResult(CommonResult.FAILD, "没有数据需要处理");
        }
        // 调用基础组件.【投资委托组件.取消完成委托】，撤销委托完成状况信息(投资委托场合才执行此步)
        baseTradeProductDealCommServiceImpl.finishEntrust(finProductTradeDto.getBaseTradeProductDealDtos(), true);
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    private CommonResult deal有效(FinProductTradeDto finProductTradeDto) {
        if (finProductTradeDto.getBaseTradeProductDealDtos() == null || finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()) {
            return new CommonResult(CommonResult.FAILD, "没有数据需要处理");
        }
        // 3.调用基础组件.【投资委托组件.完成委托】，写入委托完成状况信息(投资委托场合才执行此步)
        baseTradeProductDealCommServiceImpl.finishEntrust(finProductTradeDto.getBaseTradeProductDealDtos(), false);

        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    /**
     * 撤销提交时check检查
     *
     * @param input
     * @return
     */
    public AjaxResult checkRevoke(CfetsTrdQueryTradeInfoListResponse input) {
        List<CfetsTrdQueryTradeInfoListResponse> list = input.getSelectedItem();
        for (CfetsTrdQueryTradeInfoListResponse inputDto : list) {
            WflValidInputParameter parameter = new WflValidInputParameter();
            if (StringUtil.equals(inputDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券.getValue())) {
                parameter.setPageId("CfetsTrdBondInvestTrade002");
            } else if (StringUtil.equals(inputDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())
                    || StringUtil.equals(inputDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) {
                parameter.setPageId("CfetsTrdRepoTrade002");
            }
            parameter.setBusinessDataPk1(inputDto.getTotleTradeId() + "R");
            //parameter.setBusinessDataPk2("F01");
            if (mstWorkflowCommonService.isInWflProcess(parameter)) {
                AjaxResult m = new AjaxResult();
                m.setErrorList(Arrays.asList(new ValidMessage(this.getMessage("BUSS_COM_E_0023"))));
                return m;
            }
            if (inputDto.getTradeDate().compareTo(iPlatformDate.getSysDate()) != 0) {
                AjaxResult m = new AjaxResult();
                m.setErrorList(Arrays.asList(new ValidMessage("只可在交易生效日进行撤销操作")));
                return m;
            }
            if (StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_卖出.getValue())
                    || StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                    || StringUtil.equals(inputDto.getPs(), DictCodeList.回购拆借方向_正回购.getValue())
            ) {
                if (!StringUtil.equals(inputDto.getSettleStatus(), DictCodeListExt3.交收状态_未交收.getValue())) {
                    AjaxResult m = new AjaxResult();
                    m.setErrorList(Arrays.asList(new ValidMessage("已完成交收,不可撤销")));
                    return m;
                }
            }
            CfetsTrdBondInvestTradeDto dto = this.getTrdBondInvestTradeDto(inputDto.getTotleTradeId() + "R");
            if (StringUtil.isNotEmpty(dto)) {
                AjaxResult m = new AjaxResult();
                m.setErrorList(Arrays.asList(new ValidMessage("已撤销的交易不允许撤销")));
                return m;
            }
//            CfetsTrdRepoTrade002Dto dtoRepo = cfetsTrdRepoTrade002Service.getTrdRepoInvestTradeDto(inputDto.getTotleTradeId() + "R");
//            if (StringUtil.isNotEmpty(dtoRepo)) {
//                AjaxResult m = new AjaxResult();
//                m.setErrorList(Arrays.asList(new ValidMessage("已撤销的交易不允许撤销")));
//                return m;
//            }
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("totleTradeId", inputDto.getTotleTradeId());
            List<String> entrusIds = cfetsTrdQueryMapper.findEtrEntrustIsForcedFinish(pageParam);
            if (CollectionUtils.isNotEmpty(entrusIds)) {
                AjaxResult m = new AjaxResult();
                m.setErrorList(Arrays.asList(new ValidMessage("存在已强制完成指令的交易不允许撤销")));
                return m;
            }
        }
        return null;
    }

    /**
     * 撤销提交时check检查
     *
     * @param input
     * @return
     */
    public AjaxResult checkModifyExecId(CfetsTrdBondInvestTradeDto input) {
        if (StringUtil.isNotEmpty(input.getExecid())) {
            //判断该成交编号，是否已经被匹配
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("exeId", input.getExecid());
            boolean flag = cfetsTrdConfirmMapper.isExistMatchInfo(param);
            if (flag) {
                AjaxResult m = new AjaxResult();
                m.setErrorList(Arrays.asList(new ValidMessage("成交编号已存在，请重新填写！")));
                return m;
            }
        }
        return null;
    }

    /**
     * 修改成交编号
     *
     * @param input
     * @return
     */
    public AjaxResult modifyExecIdMethod(CfetsTrdBondInvestTradeDto input) {
        CfetsTrdConfirmMatchDto dto = new CfetsTrdConfirmMatchDto();
        dto.setPreTradeId(input.getPreTradeId());
        dto.setExeId(input.getExecid());
        cfetsTrdConfirmMapper.updateTrdQutoMatchInfo(dto);
        return null;
    }
}
