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
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealDbDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInitRequest;
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
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.impl.dao.BasePtlCashPositionInfoMapper;
import com.joyintech.tams.invest.common.component.base.trade.BaseTradeProductDealService;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.cstp.service.CstpCommonService;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.trade.entrust.EtrFinishStatusDto;
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
import org.apache.commons.collections.CollectionUtils;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CfetsTrdQueryMapper cfetsTrdQueryMapper;
    @Autowired
    private CfetsTrdConfirmMapper cfetsTrdConfirmMapper;
    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    /**
     * BaseTradeProductDealCommService
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private CfetsTrdBondFirstSub002Service cfetsTrdBondFirstSub002Service;

    @Autowired(required = false)
    private FinProductAddMapper finProductAddMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoServiceImpl;

    // 交易份额小数保留位数
    private final int decimalPlaces_4 = 4;

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
        if (CollectionUtils.isNotEmpty(entity.getTrdProductDeals())
                && StringUtil.isNotEmpty(entity.getTrdProductDeals().get(0).getFinprodId())) {
            FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(entity.getTrdProductDeals().get(0).getFinprodId());
            finProductAdd.setIssueStatus(null);
            finProductAddMapper.updateByPrimaryKey(finProductAdd);
        }
        this.insertEtrFinishStatus(entity.getTrdProductDeals(),OperationType.REVOKE);
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
            finProductTradeDto.setBaseTradeProductDealDtos(Collections.singletonList(baseTradeProductDealDto));
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
     * @param totleTradeId
     * @return com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdFirstBondInvestTradeDto
     * @author jiangpeng
     * @date 2022/6/27 13:44
     */
    public CfetsTrdFirstBondInvestTradeDto getTrdFirstBondInvestTradeDto(String totleTradeId) {
        CfetsTrdFirstBondInvestTradeDto resultDto = new CfetsTrdFirstBondInvestTradeDto();
        CfetsTrdBondFirstSubInitRequest request = new CfetsTrdBondFirstSubInitRequest();
        request.setBidId(cfetsTrdQueryMapper.getTradeId(totleTradeId));
        CfetsTrdBondFirstSubInfoDto result = cfetsTrdBondFirstSub002Service.viewPage(request);
        resultDto.setIssuer(cfetsTrdBondFirstSub002Service.getIssuer(result.getFinprodId()));
        BeanUtil.copyPropertiesIgnoreNull(resultDto, result);
        TrdFinProductDealTotal total = trdFinProductDealTotalMapper.selectByPrimaryKey(totleTradeId);
        BeanUtil.copyPropertiesIgnoreNull(resultDto, total);
        List<TrdProductDeal> list = cfetsTrdQueryMapper.selectTrdProductDealsByTotleTradeId(totleTradeId);
        resultDto.setTrdProductDeals(list);
        return resultDto;
    }

    /**
     * 处理指令完成度
     *
     * @param operationType
     */
    public void insertEtrFinishStatus(List<TrdProductDeal> list, OperationType operationType) {
        if (CollectionUtils.isNotEmpty(list)) {
            List<EtrFinishStatusDto> etrFinishStatusDtos = new ArrayList<>();
            for (TrdProductDeal trdProductDeal : list) {
                EtrFinishStatusDto status = new EtrFinishStatusDto();
                TrdProductDealAdd trdProductDealAdd = trdProductDealAddMapper.selectByPrimaryKey(trdProductDeal.getTradeId());
                status.setEntrustId(trdProductDealAdd.getEntrustId());
                status.setTradeId(trdProductDeal.getTradeId());
                status.setFinishDate(trdProductDeal.getTradeDate());
                EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(trdProductDealAdd.getEntrustId());
                if (etrEntrustAdd != null && StringUtil.isNotEmpty(etrEntrustAdd.getTargetType())
                        && StringUtil.equals(DictCodeListExt3.目标类型_金额.getValue(), etrEntrustAdd.getTargetType())) {
                    status.setEntFinishAmt(trdProductDeal.getTradeAmt());
                } else {
                    status.setEntFinishAmt(trdProductDeal.getShareAmt());
                }
                status.setTrader(trdProductDeal.getTrader());
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    baseEtrInvestInfoServiceImpl.cancelCompleteDelegate(status.getEntrustId(), trdProductDeal.getTradeId());
                }
                etrFinishStatusDtos.add(status);
            }
            if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
                baseEtrInvestInfoServiceImpl.completeDelegate(etrFinishStatusDtos);
            }
        }
    }

}
