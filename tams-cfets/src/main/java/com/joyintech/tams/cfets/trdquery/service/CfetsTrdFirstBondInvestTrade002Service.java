/*
 * 文件名：TrdBondInvestTrade001Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：Zane.Y 修改时间：2018年9月19日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trdquery.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.system.base.dao.SysUploadFileInfoMapper;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.cfets.manualsettle.dao.CfetsManualSettleMapper;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.service.CfetsTrdBondFirstSub002Service;
import com.joyintech.tams.cfets.trdconfirm.dao.CfetsTrdConfirmMapper;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdFirstBondInvestTradeDto;
import com.joyintech.tams.cfets.trdquery.dao.CfetsTrdQueryMapper;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.impl.BaseTradeProductDealCommServiceImpl;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.group.impl.dao.BasePtlCashPositionInfoMapper;
import com.joyintech.tams.invest.common.component.base.trade.BaseTradeProductDealService;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.cstp.service.CstpCommonService;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.trade.service.PreTrdCommonBaseService;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.interf.service.InterfComponentService;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 债券投资交易新建画面Service
 *
 * @author etliu
 * @version 1.0 2018年11月29日
 * @see CfetsTrdFirstBondInvestTrade002Service
 * @since 1.0
 */
@Service
public class CfetsTrdFirstBondInvestTrade002Service extends BaseService<CfetsTrdFirstBondInvestTradeDto> implements BaseProcessService<CfetsTrdFirstBondInvestTradeDto> {

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
    @Autowired(required = false)
    private FinProductAddMapper finProductAddMapper;

    // 交易份额小数保留位数
    private int decimalPlaces_4 = 4;

    /**
     * 一级申购撤销doComplete
     *
     * @param entity 参数
     * @see
     * @since 1.0
     */
    public CfetsTrdBondFirstSubInfoDto doCompleteBidRevoke(CfetsTrdFirstBondInvestTradeDto entity, String settleRemark, Boolean isNeedDealPosition) {
        CfetsTrdBondFirstSubInfoDto tradeDto = null;
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
        // 调用基础组件.【交易报送组件】删除交易报送信息
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepTrade", Arrays.asList(entity.getTotleTradeId()), Boolean.class);
        //撤销数据为登记时，将登记数据置为已撤销。删除下行匹配关系表数据
        RegFinProductDealTotal regTotal = cfetsTrdQueryMapper.selectBidOrderById(bizCode);
        if (null != regTotal) {
            String remark = StringUtil.isEmpty(regTotal.getRemark()) ? "" : regTotal.getRemark();
            if (StringUtil.isNotBlank(remark)) {
                remark = remark + "\n" + (settleRemark == null ? "" : settleRemark) + "交易撤销：" + entity.getRevokeReason();
            } else {
                remark = (settleRemark == null ? "" : settleRemark) + "交易撤销：" + entity.getRevokeReason();
            }
            regTotal.setRemark(remark);
            tradeDto = cfetsTrdBondFirstSub002Service.dorevoke(regTotal.getTotleTradeId(), remark, isNeedDealPosition, entity.getTgBusiType());
        }
        // 删除匹配关系表
        cfetsTrdConfirmMapper.deleteByTrdId(bizCode);
        // 处理老头寸
        this.dealOldBidPosition(entity, true);

        FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(entity.getFinprodId());
        finProductAdd.setIssueStatus(null);
        finProductAddMapper.updateByPrimaryKey(finProductAdd);
        //发送头寸
        String ps = DictCodeList.交易方向_买入.getValue();
        String busiType = DictCodeListExt3.头寸业务类型_债券一级申购.getValue();
        PositionHttpRequest.dorevoke(bizCode, DictCodeListExt1.子系统代码_交易.getValue(), busiType, ps);

        return tradeDto;
    }

    /**
     * 处理一级申购老头寸
     *
     * @param entity
     * @param isRevoke 是否撤销
     * @author jiangpeng
     * @date 2022/5/26 17:22
     */
    public void dealOldBidPosition(CfetsTrdFirstBondInvestTradeDto entity, boolean isRevoke) {
        if (StringUtil.equals(DictCodeList.是否_否.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            return;
        }
        for (TrdProductDeal deal : entity.getTrdProductDeals()) {
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            if (!isRevoke) {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            } else {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            }
            finProductTradeDto.setAssetType2("ISSUE_CLASS_01");
            finProductTradeDto.setIsExistNew(DictCodeList.是否_是.getValue());
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            baseTradeProductDealDto.setEntrustId(deal.getEntrustId());
            baseTradeProductDealDto.setStage("02");
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, deal);
            trdProductDeal.setFinprodId(entity.getFinprodId());
            trdProductDeal.setFinprodType2(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
            trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
            trdProductDeal.setSettleDate(entity.getTradeDate());
            trdProductDeal.setPrinAmt(deal.getShareAmt());
            trdProductDeal.setTradeDate(deal.getTradeDate());
            trdProductDeal.setSettleDate(deal.getTradeDate());
            trdProductDeal.setSecManageAcctId(deal.getSecManageAcctId());
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
            TrdProductDealAdd add = new TrdProductDealAdd();
            add.setPortfolioId(deal.getPortfolioId());
            add.setTradeId(deal.getTradeId());
            add.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
            add.setEntrustId(deal.getEntrustId());
            baseTradeProductDealDto.setTrdProductDealAdd(add);
            //处理指令完成情况
//            List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
//            StlPayOrdersDto stlPayOrder = new StlPayOrdersDto();
//            FinProduct finProduct = finProductMapper.selectByPrimaryKey(entity.getFinprodId());
//            String acctId = EtrCommonCashPositionDealService.select组合托管户(deal.getPortfolioId(), finProduct.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
//            stlPayOrder.setMyAcctNo(acctId);
//            stlPayOrder.setOrderId(entity.getTotleTradeId());
//            stlPayOrder.setCcy(finProduct.getCcy());
//            stlPayOrder.setSettleAmt(deal.getTradeAmt());
//            stlPayOrder.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
//            stlPayOrder.setPs(DictCodeListExt1.收支方向_支出.getValue());
//            stlPayOrder.setSettleDate(entity.getTradeDate());
//            stlPayOrder.setpTradeId(entity.getTotleTradeId());
//            stlPayOrders.add(stlPayOrder);
//            baseTradeProductDealDto.setStlPayOrdersList(stlPayOrders);
            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
        }
    }


    @Override
    public void doComplete(CfetsTrdFirstBondInvestTradeDto entity, OperationType operationType) {
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
            SpringContextUtil.getApplicationContext().getBean(CfetsTrdFirstBondInvestTrade002Service.class).doCompleteBidRevoke(entity,"",true);
        }
    }

    /**
     * 根据汇总编号查询汇总表及交易详情表数据
     *
     * @param totleTradeId 交易编号
     * @return 债券投资交易信息
     * @see
     * @since 1.0
     */
    public CfetsTrdFirstBondInvestTradeDto getTrdFirstBondInvestTradeDto(String totleTradeId) {
        CfetsTrdFirstBondInvestTradeDto resultDto = new CfetsTrdFirstBondInvestTradeDto();
        TrdFinProductDealTotal total = trdFinProductDealTotalMapper.selectByPrimaryKey(totleTradeId);
        BeanUtil.copyPropertiesIgnoreNull(resultDto, total);
        List<TrdProductDeal> list = cfetsTrdQueryMapper.selectTrdProductDealsByTotleTradeId(totleTradeId);
        resultDto.setTrdProductDeals(list);
        return resultDto;
    }


}
