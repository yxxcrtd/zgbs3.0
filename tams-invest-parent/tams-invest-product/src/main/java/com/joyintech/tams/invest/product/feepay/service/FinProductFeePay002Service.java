/*
  * 文件名：FinProductFeePay002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：zhangliang 修改时间：2018年11月8日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.product.feepay.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.MstFeeinfoMapper;
import com.joyintech.tams.common.base.dao.PaTrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.PaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.dao.PtlTradeMapper;
import com.joyintech.tams.common.base.dao.StlPayOrdersMapper;
import com.joyintech.tams.common.base.dao.TrdAsynHandleStatusMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealMapper;
import com.joyintech.tams.common.base.dao.TrdTradeFeeDetailMapper;
import com.joyintech.tams.common.base.dao.WflPaTrdFinDealTotalMapper;
import com.joyintech.tams.common.base.dao.WflTrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.entity.BokPositionPrdfee;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.MstFeeinfo;
import com.joyintech.tams.common.base.entity.PaTrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import com.joyintech.tams.common.base.entity.PrdLayerParam;
import com.joyintech.tams.common.base.entity.PtlFeePosition;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdAsynHandleStatus;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.common.base.entity.WflPaTrdFinDealTotal;
import com.joyintech.tams.common.base.entity.WflPaTrdProductDeal;
import com.joyintech.tams.common.base.entity.WflStlPayOrders;
import com.joyintech.tams.common.base.entity.WflTrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.common.base.entity.WflTrdProductDealAdd;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.TrdCommonWflParam;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.product.dto.FinProductExpireBaseTrdDto;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.TradeProductDealCommService;
import com.joyintech.tams.invest.common.valuationinterf.ValuationInterfService;
import com.joyintech.tams.invest.product.common.service.ProductCommonService;
import com.joyintech.tams.invest.product.feepay.dto.FinProductFeePay001Dto;
import com.joyintech.tams.invest.product.feepay.dto.FinProductFeePay002Dto;
import com.joyintech.tams.invest.product.feepay.dto.FinProductFeePay003Dto;
import com.joyintech.tams.invest.product.feepay.dto.FinProductFeePay004Dto;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 产品费用支付管理-新建
 *
 * @author zhangliang
 * @version 1.0 2018年11月8日
 * @see FinProductFeePay002Service
 * @since 1.0
 */
@Service
public class FinProductFeePay002Service extends BaseService<FinProductFeePay002Dto> implements BaseProcessService<FinProductFeePay002Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(FinProductFeePay002Service.class);

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private TradeProductDealCommService baseTradeProductDealCommService;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    
    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    /**
     * {@linkplain}
     */
    @Autowired
    private ProductCommonService productCommonService;
    @Autowired
    private MapperDelegateService mapperDelegateService;
    /**
     * 组合可用现金和实际现金查询更新组件
     */
    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoServiceImpl;

    /**
     * {@linkplain}
     */
    @Autowired
    private BaseGetBusinessId baseGetBusinessId;
@Autowired
    private FinProductMapper finProductMapper;
    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;
    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;
    @Autowired
    private PtlTradeMapper ptlTradeMapper;
    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;
    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;
    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;
    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;
    @Autowired
    private TrdProductDealMapper trdProductDealMapper;
    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;
    @Autowired
    private MstFeeinfoMapper mstFeeinfoMapper;
    @Autowired
    private TrdAsynHandleStatusMapper trdAsynHandleStatusMapper;
    /**
     * ValuationInterfService
     */
    @Autowired
    private ValuationInterfService valuationInterfService;
    /**
     * 委托指令共通
     */
    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;
    private void setFeeCashId(FinProductFeePay002Dto entity) {
        if (StringUtil.isNotEmpty(entity.getCashId())) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("feeId", entity.getFeeId());
        map.put("finprodId", entity.getFinprodId());
        map.put("cashType1", DictCodeListExt1.现金流一级类型_费用.getValue());
        String cahsId = restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectCashId",
            map, String.class);
        entity.setCashId(cahsId);
    }

    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void insertBussFlowData(FinProductFeePay002Dto entity) {
        this.setFeeCashId(entity);
        if (CollectionUtils.isNotEmpty(entity.getFileArray()) && StringUtil.isNotEmpty(entity.getTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                entity.getFileArray().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getTradeId());
        }

        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }

        TrdCommonWflParam trdCommonWflParam = new TrdCommonWflParam();
        StlPayOrders stlPayOrders = this.genStlPayOrdersDto(entity);
        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrders);
        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        trdCommonWflParam.setWflStlPayOrdersDtos(Arrays.asList(wflStlPayOrders));

        TrdFinProductDealTotal trdFinProductDealTotal = this.genTrdFinProductDealTotal(entity, stlPayOrders.getSettleAmt());
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, trdFinProductDealTotal);
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        trdCommonWflParam.setWflTrdFinProductDealTotal(wflTrdFinProductDealTotal);

        // pa 金融汇总交易表
        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, wflTrdFinProductDealTotal);
        wflPaTrdFinDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品费用支付.getValue());
        trdCommonWflParam.setWflPaTrdFinDealTotal(wflPaTrdFinDealTotal);

        FinProductExpireBaseTrdDto finProductExpireBaseTrdDto = this.genBaseTradeDealDtoList(entity, stlPayOrders);

        List<WflTrdProductDeal> wflTrdProductDealList = new ArrayList<WflTrdProductDeal>();
        List<WflTrdProductDealAdd> wflTrdProductDealAddList = new ArrayList<WflTrdProductDealAdd>();
        List<WflPaTrdProductDeal> wflPaTrdProductDealList = new ArrayList<WflPaTrdProductDeal>();

        for (TrdProductDeal trdProductDeal : finProductExpireBaseTrdDto.getTrdProductDealList()) {
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, trdProductDeal);
            wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealList.add(wflTrdProductDeal);
        }

        for (TrdProductDealAdd trdProductDealAdd : finProductExpireBaseTrdDto.getTrdProductDealAddList()) {
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, trdProductDealAdd);
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealAddList.add(wflTrdProductDealAdd);
        }

        for (PaTrdProductDeal paTrdProductDeal : finProductExpireBaseTrdDto.getPaTrdProductDealList()) {
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, paTrdProductDeal);
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            wflPaTrdProductDealList.add(wflPaTrdProductDeal);
        }
        trdCommonWflParam.setWflTrdProductDeals(wflTrdProductDealList);
        trdCommonWflParam.setWflDealAdds(wflTrdProductDealAddList);
        trdCommonWflParam.setWflPaDeals(wflPaTrdProductDealList);
        restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/insertBussFlowData",
            trdCommonWflParam, String.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doInsert(FinProductFeePay002Dto entity, OperationType type) {
        if (!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
            this.insertBussFlowData(entity);
        }
        else {
            this.doInsertRevoke(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(FinProductFeePay002Dto entity) {
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            // 先删除流程中数据
            this.doDelete(entity.getWorkflowId());

            // 重新insert
            this.insertBussFlowData(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(FinProductFeePay002Dto entity, OperationType type) {
        if (type == OperationType.NEW) {
            this.doCompleteInsert(entity);
        }
        else if (type == OperationType.REVOKE || type == OperationType.DELETE) {
            this.doCompleteRevoke(entity);
        }
    }

    /**
     * 生成金融产品交易汇总
     *
     * @param entity 产品费用支付Dto
     * @param actPayAmt 实付金额
     * @return 金融产品交易汇总
     * @see
     * @since 1.0
     */
    private TrdFinProductDealTotal genTrdFinProductDealTotal(FinProductFeePay002Dto entity, BigDecimal actPayAmt) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getTradeId());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_产品费用支付管理.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        trdFinProductDealTotal.setTradeDate(entity.getPayDate());
        trdFinProductDealTotal.setVdate(entity.getStartDate());
        trdFinProductDealTotal.setMdate(entity.getEndDate());
        trdFinProductDealTotal.setSettleDate(entity.getPayDate());
        trdFinProductDealTotal.setTradeAmt(actPayAmt);
        trdFinProductDealTotal.setCashId(entity.getCashId());
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_产品付费.getValue());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setPortfolioId(entity.getPtlId());
        trdFinProductDealTotal.setRemark(entity.getRemark());
        trdFinProductDealTotal.setCcy(entity.getCcy());
        trdFinProductDealTotal.setUpdateUser(entity.getUpdateUser());
        trdFinProductDealTotal.setCreateUser(entity.getCreateUser());
        trdFinProductDealTotal.setCreateDept(entity.getCreateDept());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());

        trdFinProductDealTotal.setFinprodType(productCommonService.convertFeeTypeToCashTypeForFinType(entity.getFeeType()));
        trdFinProductDealTotal.setFinprodType2(trdFinProductDealTotal.getFinprodType());
        //交易手续费和结算服务费使用页面计提结束日 yzg 20200111 start
        if (StringUtil.equals(entity.getChargeType(), DictCodeList.收费类型_不规则费用.getValue()) && !StringUtil.equals(entity.getFeeType(), DictCodeList.费用品种类型_交易手续费.getValue())
        		&& !StringUtil.equals(entity.getFeeType(), DictCodeList.费用品种类型_结算服务费.getValue())) {
            trdFinProductDealTotal.setMdate(entity.getPayDate());
        }
        //交易手续费和结算服务费使用页面计提结束日 yzg 20200111 end

        return trdFinProductDealTotal;
    }

    /**
     * 生成划款指令信息
     *
     * @param entity 产品费用支付Dto
     * @return 划款指令信息
     * @see
     * @since 1.0
     */
    private StlPayOrders genStlPayOrdersDto(FinProductFeePay002Dto entity) {
        entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(entity.getBussTrdTransferOrder()));
        // 划款指令表Entity
        StlPayOrders stlPayOrders = new StlPayOrders();
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            BussTrdTransferOrderDto bussTrdTransferOrderDto = entity.getBussTrdTransferOrder().get(0);
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, bussTrdTransferOrderDto);
            stlPayOrders.setTradeId(entity.getTradeId());
            stlPayOrders.setWorkflowId(entity.getWorkflowId());

            List<? extends DataDictItem> yesNoItemList = SysDataUtil.getDataDictItem(DictCode.是否.getValue());
            yesNoItemList.forEach(item -> {
                if (StringUtil.equals(item.getItemValue(), stlPayOrders.getIsNeedSend())) {
                    stlPayOrders.setIsNeedSend(item.getItemCode());
                }
            });
        }
        stlPayOrders.setCreateTime(entity.getCreateTime());
        stlPayOrders.setUpdateTime(entity.getUpdateTime());
        return stlPayOrders;
    }

    /**
     * 金融产品交易共通业务逻辑处理
     *
     * @param entity 产品费用支付Dto
     * @param stlPayOrders 指令信息
     * @return 金融产品交易共通
     * @see
     * @since 1.0
     */
    private FinProductExpireBaseTrdDto genBaseTradeDealDtoList(FinProductFeePay002Dto entity, StlPayOrders stlPayOrders) {
        FinProductExpireBaseTrdDto finProductExpireBaseTrdDto = new FinProductExpireBaseTrdDto();

        List<TrdProductDeal> trdProductDealList = new ArrayList<>();
        List<TrdProductDealAdd> trdProductDealAddList = new ArrayList<>();
        List<PaTrdProductDeal> paTrdProductDealList = new ArrayList<>();
        List<TrdTradeFeeDetail> feeDetailList = new ArrayList<TrdTradeFeeDetail>();
        // 计提费用
        if (!StringUtil.isEmptyList(entity.getFeeInfoList())) {
            for (FinProductFeePay003Dto finProductFeePay003Dto : entity.getFeeInfoList()) {
                if (StringUtil.isNotEmpty(finProductFeePay003Dto.getsFinprodId())) {
                    finProductFeePay003Dto.setFinprodId(finProductFeePay003Dto.getsFinprodId());
                    Map<String, Object> map = new HashMap<>();
                    map.put("feeId", entity.getFeeId());
                    map.put("finprodId", finProductFeePay003Dto.getsFinprodId());
                    map.put("cashType1", DictCodeListExt1.现金流一级类型_费用.getValue());
                    String cahsId = restTemplate.postForObject(
                        ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectCashId", map, String.class);
                    entity.setCashId(cahsId);

                }

                // 转换金融交易共通dto
                BaseTradeProductDealDto baseTradeProductDealDto = this.convertBaseTrade(entity,
                    finProductFeePay003Dto.getFinprodId(), finProductFeePay003Dto.getActPayAmt(),
                    finProductFeePay003Dto.getPayAmt(), finProductFeePay003Dto.getIsClean(),
                    finProductFeePay003Dto.getAdjustAmt());
                // 金融产品交易
                trdProductDealList.add(baseTradeProductDealDto.getTrdProductDeal());
                trdProductDealAddList.add(baseTradeProductDealDto.getTrdProductDealAdd());
                paTrdProductDealList.add(baseTradeProductDealDto.getPaTrdProductDeal());
                feeDetailList.addAll(baseTradeProductDealDto.getTrdTradeFeeDetailList());
            }

        }
        // 不规则费用
        else {
            if (!StringUtil.isEmptyList(entity.getFeelayeredInfoList())) {
                // 添加不规则费用逻辑
                for (FinProductFeePay003Dto finProductFeePay003Dto : entity.getFeelayeredInfoList()) {

                    if (StringUtil.isNotEmpty(finProductFeePay003Dto.getsFinprodId())) {
                        finProductFeePay003Dto.setFinprodId(finProductFeePay003Dto.getsFinprodId());
                    }

                    // 转换金融交易共通dto
                    BaseTradeProductDealDto baseTradeProductDealDto = this.convertBaseTrade(entity,
                        finProductFeePay003Dto.getFinprodId(), finProductFeePay003Dto.getActPayAmt(),
                        finProductFeePay003Dto.getPayAmt(), finProductFeePay003Dto.getIsClean(),
                        finProductFeePay003Dto.getAdjustAmt());
                    // 金融产品交易
                    trdProductDealList.add(baseTradeProductDealDto.getTrdProductDeal());
                    trdProductDealAddList.add(baseTradeProductDealDto.getTrdProductDealAdd());
                    paTrdProductDealList.add(baseTradeProductDealDto.getPaTrdProductDeal());
                    feeDetailList.addAll(baseTradeProductDealDto.getTrdTradeFeeDetailList());
                }
            }

            BaseTradeProductDealDto baseTradeProductDealDto = this.convertBaseTrade(entity, entity.getFinprodId(),
                stlPayOrders.getSettleAmt(), null, null, null);
            if (StringUtil.isEmptyList(entity.getFeelayeredInfoList())) {
                trdProductDealList.add(baseTradeProductDealDto.getTrdProductDeal());
                trdProductDealAddList.add(baseTradeProductDealDto.getTrdProductDealAdd());
                paTrdProductDealList.add(baseTradeProductDealDto.getPaTrdProductDeal());
                feeDetailList.addAll(baseTradeProductDealDto.getTrdTradeFeeDetailList());
            }
            if (!StringUtil.isEmptyList(entity.getTaxPositionList())) {
                // 增值税列表处理
                List<PtlFeePosition> positionList = restTemplate.getForList(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryTaxUnpayPosition?ptlId=" + entity.getPtlId()
                                                                            + "&cdate=" + DateUtil.format(entity.getEndDate()),
                    PtlFeePosition.class);
                if (!StringUtil.isEmptyList(positionList)) {
                    for (PtlFeePosition ptlFeePosition : positionList) {
                        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
                        trdTradeFeeDetail.setTradeId(baseTradeProductDealDto.getTrdProductDeal().getTradeId());
                        trdTradeFeeDetail.setFeeType(ptlFeePosition.getPtlFeeType());
                        trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                        trdTradeFeeDetail.setFeeAmt(ptlFeePosition.getTdyAccumulPayFee());
                        trdTradeFeeDetail.setFeeId(entity.getFeeId());
                        trdTradeFeeDetail.setUpdateUser(entity.getUpdateUser());
                        trdTradeFeeDetail.setCreateUser(entity.getCreateUser());
                        trdTradeFeeDetail.setCreateDept(entity.getCreateDept());
                        trdTradeFeeDetail.setWorkflowId(entity.getWorkflowId());
                        feeDetailList.add(trdTradeFeeDetail);
                    }
                }
                baseTradeProductDealDto.setTrdTradeFeeDetailList(feeDetailList);
            }
        }

        finProductExpireBaseTrdDto.setTrdProductDealList(trdProductDealList);
        finProductExpireBaseTrdDto.setTrdProductDealAddList(trdProductDealAddList);
        finProductExpireBaseTrdDto.setPaTrdProductDealList(paTrdProductDealList);
        finProductExpireBaseTrdDto.setTrdTradeFeeDetailList(feeDetailList);

        return finProductExpireBaseTrdDto;
    }

    /**
     * 生成组合交易表信息
     *
     * @param entity 产品费用支付Dto
     * @return 组合交易表信息
     * @see
     * @since 1.0
     */
    private PtlTrade genPtlTrdea(FinProductFeePay002Dto entity) {
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(entity.getPtlId());
        ptlTrade.setBusiNo(entity.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(entity.getPtlId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_产品费用支付管理.getValue());
        ptlTrade.setSettleDate(entity.getPayDate());
        ptlTrade.setUpdateUser(entity.getUpdateUser());
        ptlTrade.setCreateUser(entity.getCreateUser());
        ptlTrade.setCreateDept(entity.getCreateDept());

        return ptlTrade;
    }

    /**
     * 新建确认doComplete
     *
     * @param entity 产品费用支付Dto
     * @see
     * @since 1.0
     */
    private void doCompleteInsert(FinProductFeePay002Dto entity) {
        this.setFeeCashId(entity);

        StlPayOrders stlPayOrders = this.genStlPayOrdersDto(entity);
        stlPayOrdersMapper.insertSelective(stlPayOrders);

        FinProductExpireBaseTrdDto finProductExpireBaseTrdDto = this.genBaseTradeDealDtoList(entity, stlPayOrders);

        // 组合交易表
        PtlTrade ptlTrade = this.genPtlTrdea(entity);

        // 金融产品交易汇总表
        TrdFinProductDealTotal trdFinProductDealTotal = this.genTrdFinProductDealTotal(entity, stlPayOrders.getSettleAmt());
        if (StringUtil.isNotEmpty(finProductExpireBaseTrdDto)) {
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            trdTradeFeeDetail.setTradeId(trdFinProductDealTotal.getTotleTradeId());
            trdTradeFeeDetail.setFeeType(entity.getFeeType());
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            trdTradeFeeDetail.setFeeAmt(entity.getActPayAmt());
            trdTradeFeeDetail.setFeeId(entity.getFeeId());
            trdTradeFeeDetail.setUpdateUser(entity.getUpdateUser());
            trdTradeFeeDetail.setCreateUser(entity.getCreateUser());
            trdTradeFeeDetail.setCreateDept(entity.getCreateDept());
            trdTradeFeeDetail.setWorkflowId(entity.getWorkflowId());
            trdTradeFeeDetail.setIsPrepaid(entity.getIsPerpaid());
            finProductExpireBaseTrdDto.getTrdTradeFeeDetailList().add(trdTradeFeeDetail);
            if (finProductExpireBaseTrdDto.getTrdProductDealList().size() > 0) {
                trdFinProductDealTotal.setCashId(finProductExpireBaseTrdDto.getTrdProductDealList().get(0).getCashId());
            }
        }

        // pa 金融产品交易汇总表
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(paTrdFinProductDealTotal, trdFinProductDealTotal);
        paTrdFinProductDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品费用支付.getValue());

        // 插入pa 金融产品交易汇总表
        paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);

        if (finProductExpireBaseTrdDto.getTrdProductDealList() != null) {
            finProductExpireBaseTrdDto.getTrdProductDealList().forEach(trdProductDeal -> trdProductDealMapper.insertSelective(trdProductDeal));
        }

        if (finProductExpireBaseTrdDto.getTrdProductDealAddList() != null) {
            finProductExpireBaseTrdDto.getTrdProductDealAddList().forEach(trdProductDealAdd -> trdProductDealAddMapper.insertSelective(trdProductDealAdd));
        }

        if (finProductExpireBaseTrdDto.getPaTrdProductDealList() != null) {
            finProductExpireBaseTrdDto.getPaTrdProductDealList().forEach(paTrdProductDeal -> paTrdProductDealMapper.insertSelective(paTrdProductDeal));
        }

        if (finProductExpireBaseTrdDto.getTrdTradeFeeDetailList() != null) {
            finProductExpireBaseTrdDto.getTrdTradeFeeDetailList().forEach(trdTradeFeeDetail -> trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail));
        }

        ptlTradeMapper.insertSelective(ptlTrade);

        trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);

        // 组合现金流水Entity
        List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<PtlCashPositionFlowDto>();
        PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
        ptlCashPositionFlowDto.setPortfolioId(entity.getPtlId());
        ptlCashPositionFlowDto.setAcctId(stlPayOrders.getMyAcctNo());
        ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
        ptlCashPositionFlowDto.setCdate(entity.getPayDate());
        ptlCashPositionFlowDto.setCcy(stlPayOrders.getCcy());
        ptlCashPositionFlowDto.setAmount(stlPayOrders.getSettleAmt().negate());
        ptlCashPositionFlowDto.setOrderId(stlPayOrders.getOrderId());
        ptlCashPositionFlows.add(ptlCashPositionFlowDto);

        PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
        ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);

        if (ptlPositionHoldDto.getPtlCashPositionFlows() != null
                && !ptlPositionHoldDto.getPtlCashPositionFlows().isEmpty()) {
            for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
                basePtlCashPositionInfoServiceImpl.insertCashPositionFlow(ptlCashPositionFlow);
            }
        }
//          委托指令流水共通调用
        //增加委托指令流水逻辑
        if(baseEtrCashPositionCommService!=null){
            PtlPositionHoldDto ptlHoldDto = new PtlPositionHoldDto();
            ptlHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
            baseEtrCashPositionCommService.portfolioEtrCashPosition(ptlHoldDto, true);
        }
        // 删除原业务流程记录
        this.deleteBussFlowData(entity.getWorkflowId());

        // 一体化对应etliu start
        // 所有交易生效后调用的共通方法
        if (!StringUtil.isEmptyList(finProductExpireBaseTrdDto.getTrdProductDealList())) {
            String profitType = null; // 收益类型

            // 查询组合收益类型
            PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(entity.getPtlId());
            if (ptlPortfolio != null) {
                profitType = ptlPortfolio.getProfitType();
            }

            // 查询理财产品代码
            String finprodId = valuationInterfService.selectFinprodIdByPortfolioId(entity.getPtlId());

            for (TrdProductDeal tradeDto : finProductExpireBaseTrdDto.getTrdProductDealList()) {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(entity.getFinprodId());
                if (StringUtil.isBlank(inputDto.getProdid())) {
                    inputDto.setProdid(tradeDto.getFinprodId());
                }
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                // 是否计提费用
                boolean jitiFeeFlg = false;
                TrdTradeFeeDetail feeInfo = new TrdTradeFeeDetail();
                if (finProductExpireBaseTrdDto.getTrdTradeFeeDetailList() != null
                    && finProductExpireBaseTrdDto.getTrdTradeFeeDetailList().size() > 0) {
                    for (TrdTradeFeeDetail fee : finProductExpireBaseTrdDto.getTrdTradeFeeDetailList()) {
                        if (fee.getTradeId().equals(tradeDto.getTradeId())) {
                            feeInfo = fee;
                            break;
                        }
                    }
                    if (feeInfo != null && StringUtils.isNotEmpty(feeInfo.getFeeId())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("feeId", feeInfo.getFeeId());
                        MstFeeinfo info = restTemplate.postForObject(
                            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectFeeId", map, MstFeeinfo.class);
                        if (info != null && DictCodeList.收费类型_计提式费用.getValue().equals(info.getChargeType())) {
                            jitiFeeFlg = true;
                        }
                        else {
                            jitiFeeFlg = false;
                        }
                    }
                }
                if (jitiFeeFlg && !(StringUtil.equals(DictCodeList.收益类型_收益型.getValue(), profitType)
                                    && StringUtil.equals(entity.getFeeType(), DictCodeList.费用品种类型_浮动差额中收.getValue()))) {
                    inputDto.setProdid(tradeDto.getFinprodId());
                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                }
                // 货币、净值型产品浮动管理或者增值附加税费支付：产品代码应该传理财产品代码（F16_CYFS190004）而不是投资组合代码(CYFS190004)
                else if (!StringUtil.equals(DictCodeList.收益类型_收益型.getValue(), profitType)
                         && (StringUtil.equals(entity.getFeeType(), DictCodeList.费用品种类型_浮动管理费.getValue())
                             || StringUtil.equals(entity.getFeeType(), DictCodeList.费用品种类型_增值附加税费.getValue()))) {
                    inputDto.setProdid(finprodId);
                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                }
                // 预期收益产品中收支付：产品代码应该传投资组合代码，产品类型应该传投资组合(F18）
                else if (StringUtil.equals(DictCodeList.收益类型_收益型.getValue(), profitType)
                         && StringUtil.equals(entity.getFeeType(), DictCodeList.费用品种类型_浮动差额中收.getValue())) {
                    inputDto.setProdid(entity.getPtlId());
                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                }
                inputDto.setTrdProductDeal(tradeDto);
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            }
        }
        // 一体化对应etliu end
        
        //20191008新增 	
        TrdAsynHandleStatus trdAsynHandleStatus = new TrdAsynHandleStatus();
        trdAsynHandleStatus.setTradeId(trdFinProductDealTotal.getTotleTradeId());		//交易汇总id
        trdAsynHandleStatus.setTrdTableType("01");		// 交易表类型(01：金融产品交易汇总表，02：金融产品交易表)
        trdAsynHandleStatus.setHandleStatus("01");		//01	待处理
        trdAsynHandleStatusMapper.insertSelective(trdAsynHandleStatus);
    }

    /**
     * 根据汇总交易编号查询交易信息
     *
     * @param tradeId 汇总交易编号
     * @return 交易信息
     * @see
     * @since 1.0
     */
    public List<TrdProductDeal> queryTrdProductDealByTotleTradeId(String tradeId) {
        return restTemplate.getForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/queryTrdProductDealByTotleTradeId?tradeId=" + tradeId,
            TrdProductDeal.class);
    }

    /**
     * 撤销流程表登录
     *
     * @param entity 产品费用支付Dto
     * @see
     * @since 1.0
     */
    private void doInsertRevoke(FinProductFeePay002Dto entity) {
        // 根据汇总交易编号查询交易编号
        TrdFinProductDealTotal trdFinProductDealTotal = trdFinProductDealTotalMapper.selectByPrimaryKey(entity.getTradeId());

        TrdCommonWflParam trdCommonWflParam = new TrdCommonWflParam();
        if (trdFinProductDealTotal != null) {
            WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, trdFinProductDealTotal);
            // 交易状态=“撤销”
            wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
            wflTrdFinProductDealTotal.setpTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
            wflTrdFinProductDealTotal.setTotleTradeId(
                trdFinProductDealTotal.getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
            wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());

            trdCommonWflParam.setWflTrdFinProductDealTotal(wflTrdFinProductDealTotal);

            // pa 金融汇总交易表
            WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, wflTrdFinProductDealTotal);
            wflPaTrdFinDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品费用支付.getValue());
            trdCommonWflParam.setWflPaTrdFinDealTotal(wflPaTrdFinDealTotal);
        }

        // 根据汇总交易编号查询交易编号
        List<TrdProductDeal> trdProductDealList = this.queryTrdProductDealByTotleTradeId(entity.getTradeId());
        if (!StringUtil.isEmptyList(trdProductDealList)) {
            List<WflTrdProductDeal> wflTrdProductDealList = new ArrayList<>();
            List<WflTrdProductDealAdd> wflTrdProductDealAddList = new ArrayList<>();
            List<WflPaTrdProductDeal> WflPaTrdProductDealList = new ArrayList<>();
            for (TrdProductDeal trdProductDeal : trdProductDealList) {
                List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId=" + trdProductDeal.getTradeId(),
                    BaseTradeProductDealDto.class);

                if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
                    for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
                        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baseDto.getTrdProductDeal());
                        // 交易状态=“撤销”
                        wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                        // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
                        wflTrdProductDeal.setpTradeId(baseDto.getTrdProductDeal().getTradeId());
                        // 撤销说明=画面.撤销说明
                        wflTrdProductDeal.setCancelRemark(entity.getRevokeReason());
                        // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
                        wflTrdProductDeal.setTradeId(
                            baseDto.getTrdProductDeal().getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                        wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
                        wflTrdProductDealList.add(wflTrdProductDeal);

                        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
                        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseDto.getTrdProductDealAdd());
                        wflTrdProductDealAdd.setTradeId(
                            baseDto.getTrdProductDealAdd().getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                        wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
                        wflTrdProductDealAddList.add(wflTrdProductDealAdd);

                        PaTrdProductDeal paTrdProductDeal = paTrdProductDealMapper.selectByPrimaryKey(baseDto.getTrdProductDeal().getTradeId());
                        if (paTrdProductDeal != null) {
                            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
                            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, paTrdProductDeal);
                            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                            wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
                            WflPaTrdProductDealList.add(wflPaTrdProductDeal);
                        }
                    }
                }
            }
            trdCommonWflParam.setWflTrdProductDeals(wflTrdProductDealList);
            trdCommonWflParam.setWflDealAdds(wflTrdProductDealAddList);
            trdCommonWflParam.setWflPaDeals(WflPaTrdProductDealList);
            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
            List<StlPayOrdersDto> stlPayOrdersList = productCommonService.genStlPayOrdersRevoke(entity.getTradeId());
            List<WflStlPayOrders> stlPayOrdersList1 = new ArrayList<>();
            if (!StringUtil.isEmptyList(stlPayOrdersList)) {
                for (StlPayOrders stlPayOrders : stlPayOrdersList) {
                    WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrders);
                    wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                    stlPayOrdersList1.add(wflStlPayOrders);
                }
            }
            trdCommonWflParam.setWflStlPayOrdersDtos(stlPayOrdersList1);
            restTemplate.postForObject(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/doInsertRevoke", trdCommonWflParam,
                    String.class);
        }
    }

    /**
     * 撤销doComplete
     *
     * @param entity 参数
     * @see
     * @since 1.0
     */
    private void doCompleteRevoke(FinProductFeePay002Dto entity) {

        // 根据汇总交易编号查询金融产品交易
        List<TrdProductDeal> trdProductDealList = this.queryTrdProductDealByTotleTradeId(entity.getTradeId());

        if (!StringUtil.isEmptyList(trdProductDealList)) {
            for (TrdProductDeal trdProductDeal : trdProductDealList) {
                // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
                List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
                                                                                                    + trdProductDeal.getTradeId(),
                    BaseTradeProductDealDto.class);

                if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
                    for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                        baseDto.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                        baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                        baseDto.getTrdProductDeal().setTotleTradeId(
                            baseDto.getTrdProductDeal().getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                        baseDto.getTrdProductDeal().setCancelRemark(entity.getRevokeReason());
                        // add by wm将带R的交易的创建时间与更新时间修改为最新的系统时间 start 2019-09-02
                        baseDto.getTrdProductDeal().setCreateTime(DateUtil.now());
                        baseDto.getTrdProductDeal().setUpdateTime(DateUtil.now());
                        // add by wm将带R的交易的创建时间与更新时间修改为最新的系统时间 end 2019-09-02
                    }

                    // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
                    // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
                    FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                    finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtoList);
                    baseTradeProductDealCommService.execute(finProductTradeDto);
                }
            }
        }

        // 划款指令表
        List<StlPayOrders> stlPayOrdersList = restTemplate.getForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/queryStlPayOrdersByTradeId?tradeId="
                                                                      + entity.getTradeId(),
            StlPayOrders.class);
        if (!StringUtil.isEmptyList(stlPayOrdersList)) {
            for (StlPayOrders stlPayOrders : stlPayOrdersList) {
                stlPayOrders.setOrderId(stlPayOrders.getOrderId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                stlPayOrders.setpTradeId(stlPayOrders.getOrderId());
                stlPayOrders.setTradeId(entity.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());

                // 撤销时 组合现金流水Entity
                List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<PtlCashPositionFlowDto>();
                PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
                ptlCashPositionFlowDto.setPortfolioId(entity.getPtlId());
                ptlCashPositionFlowDto.setAcctId(stlPayOrders.getMyAcctNo());
                ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
                ptlCashPositionFlowDto.setCdate(entity.getPayDate());
                ptlCashPositionFlowDto.setCcy(stlPayOrders.getCcy());
                ptlCashPositionFlowDto.setAmount(stlPayOrders.getSettleAmt());
                ptlCashPositionFlowDto.setOrderId(stlPayOrders.getOrderId());
                ptlCashPositionFlows.add(ptlCashPositionFlowDto);

                PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
                ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
                //          委托指令流水共通调用
                //增加委托指令流水逻辑
                if(baseEtrCashPositionCommService!=null){
                    PtlPositionHoldDto ptlHoldDto = new PtlPositionHoldDto();
                    ptlHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
                    baseEtrCashPositionCommService.portfolioEtrCashPosition(ptlHoldDto, true);
                }
                restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
                    ptlPositionHoldDto, Boolean.class);
            }
        }

        restTemplate.postForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/doCompleteRevoke", entity, String.class);

        // 一体化对应etliu start
        // 所有交易生效后调用的共通方法
        if (!StringUtil.isEmptyList(trdProductDealList)) {
            for (TrdProductDeal tradeDto : trdProductDealList) {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(entity.getFinprodId());
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                tradeDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                inputDto.setTrdProductDeal(tradeDto);
                tradeDto.setpTradeId(tradeDto.getTradeId());
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            }
        }
        // 一体化对应etliu end

        // 删除原业务流程记录
        this.deleteBussFlowData(entity.getWorkflowId());
        
        //20191008新增 	
        TrdAsynHandleStatus trdAsynHandleStatus = new TrdAsynHandleStatus();
        trdAsynHandleStatus.setTradeId(entity.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);		//交易汇总id
        trdAsynHandleStatus.setTrdTableType("01");		// 交易表类型(01：金融产品交易汇总表，02：金融产品交易表)
        trdAsynHandleStatus.setHandleStatus("01");		//01	待处理
        trdAsynHandleStatusMapper.insertSelective(trdAsynHandleStatus);
    }

    /**
     * 查询组合下产品关联的费用
     *
     * @param map 参数
     * @return 关联的费用
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryFeeInfoList(Map<String, Object> map) {
        if (StringUtil.equals(map.get("operateType"), "new")) {
            return restTemplate.postForList(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryFeeInfoListEx", map,
                SelectOptionDto.class);
        }
        else {
            return restTemplate.postForList(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryFeeInfoList", map,
                SelectOptionDto.class);
        }
    }

    /**
     * 查询费用上次计提结束日
     *
     * @param ptlId 组合代码
     * @param feeId 费用代码
     * @return 上次计提结束日
     * @see
     * @since 1.0
     */
    public String queryLastPayDate(String ptlId, String feeId, String operateType, String org) {

        String payCycle = "";

        if (!operateType.equals("new")) {
      //      MstFeeinfo mstFeeInfo = null;
        	MstFeeinfo mstFeeInfo = new MstFeeinfo();
            if (StringUtil.isNotEmpty(feeId)) {
                mstFeeInfo = mstFeeinfoMapper.selectByPrimaryKey(feeId);
            }

            if (StringUtil.isNotEmpty(mstFeeInfo)) {
                if (mstFeeInfo.getChargeType().equals(DictCodeList.收费类型_不规则费用.getValue())) {
                    payCycle = DictCodeList.是否_否.getValue();
                }
                else if (!mstFeeInfo.getChargeType().equals(DictCodeList.收费类型_不规则费用.getValue())) {
                    payCycle = DictCodeList.是否_是.getValue();
                }
            }
            else {
                payCycle = DictCodeList.是否_否.getValue();
            }
        }
        else {
            payCycle = DictCodeList.是否_否.getValue();
        }

        return restTemplate.getForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryLastPayDate?ptlId="
                                         + ptlId + "&feeId=" + feeId + "&payCycle=" + payCycle+ "&org=" + org,
            String.class);
    }

    /**
     * 查询费用待支付费用
     *
     * @param ptlId 组合代码
     * @param feeType 费用类型
     * @param cdate 计提结束日
     * @return 待支付费用
     * @see
     * @since 1.0
     */
    public BigDecimal queryUnpayPosition(String ptlId, String feeType, String cdate) {
        return restTemplate.getForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryUnpayPosition?ptlId="
                                         + ptlId + "&feeType=" + feeType + "&cdate=" + cdate,
            BigDecimal.class);
    }

    /**
     * 结算服务费、账户维护费联动带出默认费用
     *
     * @param ptlId
     * @param feeType
     * @param cdate
     * @return
     */
    public BigDecimal queryPayTradeAmt(String ptlId, String org, String cdate, String feeType) {
        return restTemplate.getForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryPayTradeAmt?ptlId="
                                         + ptlId + "&org=" + org + "&cdate=" + cdate + "&feeType=" + feeType,
            BigDecimal.class);
    }

    /**
     * 查询增值税待支付费用
     *
     * @param ptlId 组合代码
     * @param cdate 查询日
     * @return 增值税待支付费用
     * @see
     * @since 1.0
     */
    public List<FinProductFeePay004Dto> queryTaxUnpayPosition(String ptlId, String cdate) {
        List<FinProductFeePay004Dto> resList = new ArrayList<FinProductFeePay004Dto>();
        List<PtlFeePosition> positionList = restTemplate.getForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryTaxUnpayPosition?ptlId=" + ptlId + "&cdate="
                                                                    + cdate,
            PtlFeePosition.class);
        if (!StringUtil.isEmptyList(positionList)) {
            BigDecimal tax = BigDecimal.ZERO;
            for (PtlFeePosition ptlFeePosition : positionList) {
                // 一体化对应etliu start
                // if (StringUtil.equals(ptlFeePosition.getPtlFeeType(),
                // DictCodeListExt2.费用类型_利息收入.getValue())
                // || StringUtil.equals(ptlFeePosition.getPtlFeeType(),
                // DictCodeListExt2.费用类型_价差收入.getValue())
                // || StringUtil.equals(ptlFeePosition.getPtlFeeType(),
                // DictCodeListExt2.费用类型_红利收入.getValue())) {
                if (StringUtil.equals(ptlFeePosition.getPtlFeeType(), DictCodeList.税类型_利息收入.getValue())
                    || StringUtil.equals(ptlFeePosition.getPtlFeeType(), DictCodeList.税类型_价差收入.getValue())
                    || StringUtil.equals(ptlFeePosition.getPtlFeeType(), DictCodeList.税类型_红利收入.getValue())) {
                    // 一体化对应etliu end
                    tax = ArithmeticUtil.doAdd(tax, ptlFeePosition.getTdyAccumulPayFee());
                }
                else {
                    FinProductFeePay004Dto finProductFeePay004Dto = new FinProductFeePay004Dto();
                    finProductFeePay004Dto.setFeeName(
                        SysDataUtil.getNameFromDataDict(DictCode.税类型, ptlFeePosition.getPtlFeeType()));
                    finProductFeePay004Dto.setFeeAmt(ptlFeePosition.getTdyAccumulPayFee());
                    finProductFeePay004Dto.setActFeeAmt(ptlFeePosition.getTdyAccumulPayFee());
                    finProductFeePay004Dto.setAdjustFeeAmt(BigDecimal.ZERO);
                    resList.add(finProductFeePay004Dto);
                }
            }
            if (tax.compareTo(BigDecimal.ZERO) != 0) {
                FinProductFeePay004Dto finProductFeePay004Dto = new FinProductFeePay004Dto();
                finProductFeePay004Dto.setFeeName("增值税");
                finProductFeePay004Dto.setFeeAmt(tax);
                finProductFeePay004Dto.setActFeeAmt(tax);
                finProductFeePay004Dto.setAdjustFeeAmt(BigDecimal.ZERO);
                resList.add(0, finProductFeePay004Dto);
            }
        }
        return resList;
    }

    /**
     * 根据交易编号查询增值税信息
     *
     * @param tradeId 交易编号
     * @return 增值税信息
     * @see
     * @since 1.0
     */
    public List<FinProductFeePay004Dto> queryTaxDetailByTradeId(String tradeId) {
        List<FinProductFeePay004Dto> resList = new ArrayList<FinProductFeePay004Dto>();
        List<TrdTradeFeeDetail> taxList = restTemplate.getForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryTaxDetailByTradeId?tradeId=" + tradeId,
            TrdTradeFeeDetail.class);
        if (!StringUtil.isEmptyList(taxList)) {
            BigDecimal tax = BigDecimal.ZERO;
            for (TrdTradeFeeDetail trdTradeFeeDetail : taxList) {
                // 一体化对应etliu start
                // if (StringUtil.equals(trdTradeFeeDetail.getFeeType(),
                // DictCodeListExt2.费用类型_利息收入.getValue())
                // || StringUtil.equals(trdTradeFeeDetail.getFeeType(),
                // DictCodeListExt2.费用类型_价差收入.getValue())
                // || StringUtil.equals(trdTradeFeeDetail.getFeeType(),
                // DictCodeListExt2.费用类型_红利收入.getValue())) {
                if (StringUtil.equals(trdTradeFeeDetail.getFeeType(), DictCodeList.税类型_利息收入.getValue())
                    || StringUtil.equals(trdTradeFeeDetail.getFeeType(), DictCodeList.税类型_价差收入.getValue())
                    || StringUtil.equals(trdTradeFeeDetail.getFeeType(), DictCodeList.税类型_红利收入.getValue())) {
                    // 一体化对应etliu end
                    tax = ArithmeticUtil.doAdd(tax, trdTradeFeeDetail.getFeeAmt());
                }
                else {
                    FinProductFeePay004Dto finProductFeePay004Dto = new FinProductFeePay004Dto();
                    finProductFeePay004Dto.setFeeName(
                        SysDataUtil.getNameFromDataDict(DictCode.税类型, trdTradeFeeDetail.getFeeType()));
                    finProductFeePay004Dto.setFeeAmt(trdTradeFeeDetail.getFeeAmt());
                    finProductFeePay004Dto.setActFeeAmt(trdTradeFeeDetail.getFeeAmt());
                    finProductFeePay004Dto.setAdjustFeeAmt(BigDecimal.ZERO);
                    resList.add(finProductFeePay004Dto);
                }
            }
            if (tax.compareTo(BigDecimal.ZERO) != 0) {
                FinProductFeePay004Dto finProductFeePay004Dto = new FinProductFeePay004Dto();
                finProductFeePay004Dto.setFeeName("增值税");
                finProductFeePay004Dto.setFeeAmt(tax);
                finProductFeePay004Dto.setActFeeAmt(tax);
                finProductFeePay004Dto.setAdjustFeeAmt(BigDecimal.ZERO);
                resList.add(0, finProductFeePay004Dto);
            }
        }
        return resList;
    }

    /**
     * 根据现金流代码和到期日查询具体付费计划
     *
     * @param cashId 现金流代码
     * @param mdate 到期日
     * @return 付费计划
     * @see
     * @since 1.0
     */
    public FinProductFeePay002Dto queryPendingFeeInfoByCashId(String cashId, String mdate) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cashId", cashId);
        map.put("mdate", mdate);
        map.put("flag", DictCodeList.是否_否.getValue());

        map.put("userId", SecurityUtil.getLoginUser().getUserName());
        FinProductFeePay001Dto finProductFeePay001Dto = restTemplate.postForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryPendingFeeInfoByCashId", map,
            FinProductFeePay001Dto.class);
        /*
         * List<String> list = new ArrayList<String>(); list.add("AA19002"); map.put("ptlId",list); FinProductFeePay001Dto finProductFeePay001Dto1 =
         * restTemplate.postForObject( ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/checkpayAmt", map, FinProductFeePay001Dto.class);
         */

        if (finProductFeePay001Dto != null) {
            FinProductFeePay002Dto finProductFeePay002Dto = new FinProductFeePay002Dto();
            finProductFeePay002Dto.setPtlId(finProductFeePay001Dto.getPtlId());
            finProductFeePay002Dto.setFeeId(finProductFeePay001Dto.getFeeId());
            finProductFeePay002Dto.setFeeType(finProductFeePay001Dto.getFeeType());
            finProductFeePay002Dto.setBasis(finProductFeePay001Dto.getBasis());
            finProductFeePay002Dto.setStartDate(finProductFeePay001Dto.getVdate());
            finProductFeePay002Dto.setEndDate(finProductFeePay001Dto.getMdate());
            finProductFeePay002Dto.setPayDate(finProductFeePay001Dto.getPayDate());
            finProductFeePay002Dto.setFinprodId(finProductFeePay001Dto.getFinprodId());
            finProductFeePay002Dto.setFinprodMarketId(finProductFeePay001Dto.getFinprodMarketId());
            finProductFeePay002Dto.setFeeName(finProductFeePay001Dto.getFeeName());
            finProductFeePay002Dto.setCcy(finProductFeePay001Dto.getCcy());
            finProductFeePay002Dto.setFeeId(finProductFeePay001Dto.getFeeId());
            return finProductFeePay002Dto;
        }

        return null;
    }

    /**
     * 查询费用明细列表
     *
     * @param map 参数
     * @return 费用明细列表
     * @see
     * @since 1.0
     */
    public List<FinProductFeePay003Dto> queryPayFeeInfoList(Map<String, Object> map) {
        if (StringUtil.equals(map.get("chargeType"), DictCodeList.收费类型_不规则费用.getValue())) {
            List<PrdLayerParam> prdList = restTemplate.getForList(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryPrdLayerListByPid?finprodId="
                                                                  + map.get("finprodId"),
                PrdLayerParam.class);
            List<FinProductFeePay003Dto> feePayerList = new ArrayList<FinProductFeePay003Dto>();
            if (!StringUtil.isEmptyList(prdList)) {
                for (PrdLayerParam prdLayerParam : prdList) {
                    FinProductFeePay003Dto finProductFeePay003Dto = new FinProductFeePay003Dto();
                    finProductFeePay003Dto.setsFinprodId(prdLayerParam.getFinprodId());
                    feePayerList.add(finProductFeePay003Dto);
                }
            }
            return feePayerList;
        }
        else {
            FinProductFeePay003Dto finProductFeePay003Dto = BeanUtil.mapToObject(map, FinProductFeePay003Dto.class);
            // 新建模式选择三费处理
            if (true) {

                String cashType2 = restTemplate.getForObject(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectCashType2?finprodId="
                                                             + map.get("finprodId") + "&feeId=" + map.get("feeId"),
                    String.class);

                // 如果产品无数据可能是分层产品
                if (!StringUtil.isNotEmpty(cashType2)) {
                    cashType2 = restTemplate.getForObject(
                        ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectLayyerdCashType2?finprodId="
                                                          + map.get("finprodId") + "&feeId=" + map.get("feeId"),
                        String.class);
                }

                if (StringUtil.equals(map.get("feeType"), DictCodeList.费用品种类型_浮动差额中收.getValue())
                    || StringUtil.equals(map.get("feeType"), DictCodeList.费用品种类型_浮动管理费.getValue())) {
                    cashType2 = DictCodeListExt1.现金流二级类型_浮动管理费.getValue();
                }
                if (StringUtil.isNotEmpty(cashType2)) {
                    map.put("feeType", cashType2);
                }

                if (!StringUtil.equals(map.get("operateType"), "new")) {
                    MstFeeinfo mstFeeInfo = null;
                    if (StringUtil.isNotEmpty(map.get("feeId"))) {
                        mstFeeInfo = mstFeeinfoMapper.selectByPrimaryKey(map.get("feeId").toString());
                    }
                    // String PayCycle = queryPayCycleByCashId(map.get("cashId").toString(),map.get("calcEndDate").toString());
                    if (StringUtil.isNotEmpty(mstFeeInfo)) {
                        if (StringUtil.equals(mstFeeInfo==null?null:mstFeeInfo.getChargeType(), DictCodeList.收费类型_不规则费用.getValue()))
                         {
                            map.put("flag", DictCodeList.是否_否.getValue());
                        }
    //                    else if (!mstFeeInfo.getChargeType().equals(DictCodeList.收费类型_不规则费用.getValue())) {
                        else {    map.put("flag", DictCodeList.是否_是.getValue());
                        }
                    }
                    else {
                        map.put("flag", DictCodeList.是否_否.getValue());
                    }
                }
                else {
                    map.put("flag", DictCodeList.是否_否.getValue());
                }

                // 判断费用是否是确认

                /*
                 * List<FinProductFeePay003Dto> resList = restTemplate.postForList( ServiceApplication.PRODUCT_SERVICE_NAME +
                 * "/FinProductFeePay/queryFeePositionByPtlId", map, FinProductFeePay003Dto.class);
                 */

                List<FinProductFeePay003Dto> resList = restTemplate.postForList(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryFeePositionByPtlLayeredId", map,
                    FinProductFeePay003Dto.class);

                if (!StringUtil.isEmptyList(resList)) {
                    for (FinProductFeePay003Dto dto : resList) {
                        // FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                        // FinProductMapper.class, dto.getFinprodId(), FinProduct.class);
                        // if (finProduct != null) {
                        // dto.setFinprodMarketId(finProduct.getFinprodMarketId());
                        // }
                        // dto.setFeeId(finProductFeePay003Dto.getFeeId());
                        // MstFeeinfo mstFeeinfo = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                        // MstFeeinfoMapper.class, finProductFeePay003Dto.getFeeId(),
                        // MstFeeinfo.class);
                        // if (mstFeeinfo != null) {
                        // dto.setFeeName(mstFeeinfo.getFeeName());
                        // dto.setFeeType(mstFeeinfo.getFeeType());
                        // }
//                         dto.setCalcStartDate(finProductFeePay003Dto.getCalcStartDate());
                        // modify by jiangpeng on 20210508 start
                        // TISHKBS-2117 【产品费用支付】-双击查看待确认的费用的计提明细 ，理论费用少计提了一天，错误
                        dto.setCalcEndDate(finProductFeePay003Dto.getCalcEndDate());
                        // modify by jiangpeng on 20210508 end
                        // dto.setPayAmt(dto.getActPayAmt());
                        dto.setAdjustAmt(BigDecimal.ZERO);
                        BigDecimal remainAmt = restTemplate.getForObject(
                            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/calResiPrincipal?ptlId="
                                                                         + map.get("ptlId") + "&payDate=" + map.get("payDate"),

                            BigDecimal.class);
                        dto.setRemainAmt(remainAmt);
                        String sFinprodId = dto.getsFinprodId();
                        if (StringUtil.isEmpty(sFinprodId)) {
                            sFinprodId = dto.getFinprodId();
                        }
                        BigDecimal unclearAmt = restTemplate.getForObject(
                            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryUnCleanFee?ptlId="
                                                                          + map.get("ptlId") + "&feeId=" + map.get("feeId")
                                                                          + "&payDate=" + map.get("payDate") + "&finprodId="
                                                                          + sFinprodId,

                            BigDecimal.class);
                        dto.setUnclearAmt(unclearAmt);
//                        dto.setCalcStartDate(finProductFeePay003Dto.getCalcStartDate());
                        if(dto.getCalcStartDate()==null){
                            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                dto.setCalcStartDate(sd.parse((String)map.get("calcStartDate")));
                            } catch (ParseException e) {
                                logger.error(e.getMessage(),e);
                            }
                        }
                    }
                }

                return resList;
            }
            else {
                MstFeeinfo mstFeeinfo = mstFeeinfoMapper.selectByPrimaryKey(finProductFeePay003Dto.getFeeId());
                if (mstFeeinfo != null) {
                    finProductFeePay003Dto.setFeeName(mstFeeinfo.getFeeName());
                }
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(finProductFeePay003Dto.getFinprodId());
                if (finProduct != null) {
                    finProductFeePay003Dto.setFinprodMarketId(finProduct.getFinprodMarketId());
                }

                String cashType2 = restTemplate.getForObject(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectCashType2?finprodId="
                                                             + map.get("finprodId") + "&feeId=" + map.get("feeId"),
                    String.class);
                // 查询产品费用头寸表
                BigDecimal payAmt = restTemplate.getForObject(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryFeeAmtByFeeType?feeType=" + cashType2
                                                              + "&cdate="
                                                              + DateUtil.format(finProductFeePay003Dto.getCalcEndDate())
                                                              + "&finprodId=" + finProductFeePay003Dto.getFinprodId() + "&branch="
                                                              + CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE),
                    BigDecimal.class);
                finProductFeePay003Dto.setPayAmt(payAmt);
                finProductFeePay003Dto.setActPayAmt(payAmt);
                finProductFeePay003Dto.setAdjustAmt(BigDecimal.ZERO);
                // 查询剩余现金
                BigDecimal remainAmt = restTemplate.getForObject(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/calResiPrincipal?ptlId=" + map.get("ptlId")
                                                                 + "&payDate=" + map.get("payDate"),

                    BigDecimal.class);
                finProductFeePay003Dto.setRemainAmt(remainAmt);
                // 查询累计未付
                BigDecimal unclearAmt = restTemplate.getForObject(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryUnCleanFee?ptlId=" + map.get("ptlId")
                                                                  + "&feeId=" + map.get("feeId") + "&payDate="
                                                                  + map.get("payDate"),

                    BigDecimal.class);
                finProductFeePay003Dto.setUnclearAmt(unclearAmt);

                List<FinProductFeePay003Dto> list = new ArrayList<FinProductFeePay003Dto>();
                list.add(finProductFeePay003Dto);
                return list;
            }
        }
    }

    /**
     * 根据交易编号查询费用明细信息
     *
     * @param tradeId 交易编号
     * @return 费用明细信息
     * @see
     * @since 1.0
     */
    public List<FinProductFeePay003Dto> queryFeeDetailByTradeId(String tradeId, String ptlId, String payDate) {
        List<FinProductFeePay003Dto> feeInfoList = restTemplate.getForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryFeeDetailByTradeId?tradeId=" + tradeId,
            FinProductFeePay003Dto.class);

        // if (!StringUtil.isEmptyList(feeInfoList)) {
        // for (FinProductFeePay003Dto finProductFeePay003Dto : feeInfoList) {
        // String finprodId = finProductFeePay003Dto.getFinprodId();
        // if(StringUtil.isNotEmpty(finProductFeePay003Dto.getsFinprodId())){
        // finprodId = finProductFeePay003Dto.getsFinprodId();
        // }
        //
        // String cashType2 = restTemplate.getForObject(
        // ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectCashType2?finprodId="
        // + finprodId
        // + "&feeId="
        // + finProductFeePay003Dto.getFeeId(),
        // String.class);
        // // 查询产品费用头寸表
        // BigDecimal payAmt = restTemplate.getForObject(
        // ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryFeeAmtByFeeType?feeType="
        // + cashType2
        // + "&cdate="
        // + DateUtil.format(
        // finProductFeePay003Dto.getCalcEndDate())
        // + "&finprodId="
        // + finprodId
        // + "&branch="
        // + CacheUtil.getSysParam(
        // BussinessConstant.NOBRANCHNOVALUE),
        // BigDecimal.class);
        //
        // finProductFeePay003Dto.setPayAmt(payAmt);
        // //modify by wm 调整金额修改为实付-理论 start 2019-07-12
        //// finProductFeePay003Dto.setAdjustAmt(
        //// ArithmeticUtil.subtract(payAmt, finProductFeePay003Dto.getActPayAmt()));
        // if(!finProductFeePay003Dto.getIsClean().equals(DictCodeList.是否_否.getValue())){
        // finProductFeePay003Dto.setAdjustAmt(
        // ArithmeticUtil.subtract(finProductFeePay003Dto.getActPayAmt(), payAmt));
        // }
        // //modify by wm 调整金额修改为实付-理论 end 2019-07-12
        // }
        // }

        return feeInfoList;
    }

    /**
     * 查询费用明细列表
     *
     * @param finprodId 金融产品代码
     * @param calcStartDate 区间开始日
     * @param calcEndDate 区间结束日
     * @param feeId 费用代码
     * @param feeType
     * @return 费用明细列表
     * @see
     * @since 1.0
     */
    public List<FinProductFeePay003Dto> queryFeePositionDetail(String finprodId, String calcStartDate, String calcEndDate,
                                                               String feeId, String branchs, String feeType) {
    	//费用基础使用产品新建保存字段 yzg 20191211 start
    	//MstFeeinfo mstFeeinfo = mstFeeinfoMapper.selectByPrimaryKey(feeId);
    	MstFeeinfo mstFeeinfo  = restTemplate.getForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectMstFeeInfo?finprodId=" + finprodId + "&feeId="
                        + feeId + "&enddate=" + calcEndDate,
                MstFeeinfo.class);
    	//费用基础使用产品新建保存字段 yzg 20191211 end
        if (mstFeeinfo != null) {
            // 查询产品费用头寸明细
            String cashType2 = restTemplate.getForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectCashType2?finprodId=" + finprodId + "&feeId="
                                                         + feeId,
                String.class);

            // 如果产品无数据可能是分层产品
            if (!StringUtil.isNotEmpty(cashType2)) {
                cashType2 = restTemplate.getForObject(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectLayyerdCashType2?finprodId=" + finprodId
                                                      + "&feeId=" + feeId,
                    String.class);
            }
            if (StringUtil.isEmpty(cashType2)) {// 浮动差额中收在现金流表中不保存id，这里特殊处理，目前只有浮动差额中收，后续可能还有这样的转换
                if (DictCodeList.费用品种类型_浮动差额中收.getValue().equals(feeType)) {
                    cashType2 = DictCodeListExt1.现金流二级类型_浮动管理费.getValue();
                }
            }
            List<BokPositionPrdfee> positionDetailList = restTemplate.getForList(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryFeePositionDetail?feeType=" + cashType2
                                                                                 + "&startDate=" + calcStartDate + "&enddate="
                                                                                 + calcEndDate + "&finprodId=" + finprodId
                                                                                 + "&branch=" + branchs,
                BokPositionPrdfee.class);

            if (StringUtil.isNotEmpty(positionDetailList)) {
                List<FinProductFeePay003Dto> detailList = new ArrayList<FinProductFeePay003Dto>();
                // 根据分支序号分组
                Map<Long, List<BokPositionPrdfee>> branchDetailMap = positionDetailList.stream().collect(
                    JoyinCollectors.groupingBy(BokPositionPrdfee::getBranch));

                if (StringUtil.equals(cashType2, DictCodeListExt1.现金流二级类型_浮动管理费.getValue())
                    && !DictCodeList.费用品种类型_浮动差额中收.getValue().equals(feeType)) {
                    for (Long branch : branchDetailMap.keySet()) {
                        List<BokPositionPrdfee> branchDetailList = branchDetailMap.get(branch);
                        // 根据头寸日进行排序
                        branchDetailList.sort((positionA, positionB) -> positionA.getCdate().compareTo(positionB.getCdate()));
                        for (int i = 0; i < branchDetailList.size(); i++ ) {

                            BokPositionPrdfee current = branchDetailList.get(i);

                            FinProductFeePay003Dto feeDetailDto = new FinProductFeePay003Dto();
                            feeDetailDto.setBranch(branch);
                            feeDetailDto.setFeeName(mstFeeinfo.getFeeName());
                            feeDetailDto.setFeeType(SysDataUtil.getNameFromDataDict(DictCode.费用品种类型, mstFeeinfo.getFeeType()));
                            Date accrStrDate = restTemplate.getForObject(
                                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryAccrStrDate?finprodId="
                                                                         + current.getFinprodId() + "&cdate="
                                                                         + DateUtil.format(current.getCdate()),
                                Date.class);
                            //查询出来计提开始日为空 即为高频开放产品。 设置计提开始日即计提结束日
                            if(!StringUtil.isNotEmpty(accrStrDate)) {
                            	accrStrDate = current.getCdate();
                            }
                            feeDetailDto.setCalcStartDate(accrStrDate);
                            feeDetailDto.setCalcEndDate(current.getCdate());
                            feeDetailDto.setPrinAmt(current.getTdyPosition());
                            feeDetailDto.setBasis(mstFeeinfo.getBasis());
                            feeDetailDto.setFeeRate(ArithmeticUtil.multiply(current.getTdyFeeRate(), new BigDecimal(100)));
                            feeDetailDto.setPayAmt(current.getTdyIntincexpAdd());
                            feeDetailDto.setFinprodId(current.getFinprodId());
                            detailList.add(feeDetailDto);
                        }
                    }
                }
                else {
                    for (Long branch : branchDetailMap.keySet()) {
                        List<BokPositionPrdfee> branchDetailList = branchDetailMap.get(branch);

                        if (!StringUtil.isEmptyList(branchDetailList) && branchDetailList.size() == 1) {
                            FinProductFeePay003Dto feeDetailDto = new FinProductFeePay003Dto();
                            feeDetailDto.setBranch(branch);
                            feeDetailDto.setFeeName(mstFeeinfo.getFeeName());
                            feeDetailDto.setFeeType(SysDataUtil.getNameFromDataDict(DictCode.费用品种类型, mstFeeinfo.getFeeType()));
                            feeDetailDto.setCalcStartDate(branchDetailList.get(0).getCdate());
                            feeDetailDto.setCalcEndDate(DateUtil.addDay(branchDetailList.get(0).getCdate(), 1));
                            feeDetailDto.setPrinAmt(branchDetailList.get(0).getTdyPosition());
                            feeDetailDto.setBasis(mstFeeinfo.getBasis());
                            feeDetailDto.setFeeRate(
                                ArithmeticUtil.multiply(branchDetailList.get(0).getTdyFeeRate(), new BigDecimal(100)));
                            feeDetailDto.setPayAmt(branchDetailList.get(0).getTdyIntincexpAdd());
                            feeDetailDto.setFinprodId(branchDetailList.get(0).getFinprodId());
                            detailList.add(feeDetailDto);
                        }
                        else {
                            // 根据头寸日进行排序
                            branchDetailList.sort((positionA, positionB) -> positionA.getCdate().compareTo(positionB.getCdate()));

                            BokPositionPrdfee first = branchDetailList.get(0);
                            BigDecimal payAmt = first.getTdyIntincexpAdd();
                            for (int i = 1; i < branchDetailList.size(); i++ ) {
                                if (payAmt.compareTo(BigDecimal.ZERO) == 0) {
                                    payAmt = first.getTdyIntincexpAdd();
                                }
                                BokPositionPrdfee next = branchDetailList.get(i);
                                if (first.getTdyPosition().compareTo(next.getTdyPosition()) == 0
                                    && first.getTdyFeeRate().compareTo(next.getTdyFeeRate()) == 0) {
                                    // if (i != branchDetailList.size() - 1) {
                                    payAmt = ArithmeticUtil.doAdd(payAmt, next.getTdyIntincexpAdd());
                                    // }
                                }
                                else {
                                    FinProductFeePay003Dto feeDetailDto = new FinProductFeePay003Dto();
                                    feeDetailDto.setBranch(branch);
                                    feeDetailDto.setFeeName(mstFeeinfo.getFeeName());
                                    feeDetailDto.setFeeType(
                                        SysDataUtil.getNameFromDataDict(DictCode.费用品种类型, mstFeeinfo.getFeeType()));
                                    feeDetailDto.setCalcStartDate(first.getCdate());
                                    feeDetailDto.setCalcEndDate(next.getCdate());
                                    feeDetailDto.setPrinAmt(first.getTdyPosition());
                                    feeDetailDto.setBasis(mstFeeinfo.getBasis());
                                    feeDetailDto.setFeeRate(ArithmeticUtil.multiply(first.getTdyFeeRate(), new BigDecimal(100)));
                                    feeDetailDto.setPayAmt(payAmt);
                                    feeDetailDto.setFinprodId(first.getFinprodId());
                                    detailList.add(feeDetailDto);

                                    first = next;
                                    payAmt = BigDecimal.ZERO;
                                }
                                if (i == branchDetailList.size() - 1) {
                                    FinProductFeePay003Dto feeDetailDto = new FinProductFeePay003Dto();
                                    feeDetailDto.setBranch(branch);
                                    feeDetailDto.setFeeName(mstFeeinfo.getFeeName());
                                    feeDetailDto.setFeeType(
                                        SysDataUtil.getNameFromDataDict(DictCode.费用品种类型, mstFeeinfo.getFeeType()));
                                    feeDetailDto.setCalcStartDate(first.getCdate());
                                    feeDetailDto.setCalcEndDate(DateUtil.addDay(next.getCdate(), 1));
                                    feeDetailDto.setPrinAmt(first.getTdyPosition());
                                    feeDetailDto.setBasis(mstFeeinfo.getBasis());
                                    feeDetailDto.setFinprodId(first.getFinprodId());
                                    feeDetailDto.setFeeRate(ArithmeticUtil.multiply(first.getTdyFeeRate(), new BigDecimal(100)));
                                    feeDetailDto.setPayAmt(
                                        payAmt.compareTo(BigDecimal.ZERO) != 0 ? payAmt : first.getTdyIntincexpAdd());
                                    detailList.add(feeDetailDto);

                                    first = next;
                                    payAmt = BigDecimal.ZERO;
                                }
                            }
                        }
                    }

                }

                return detailList;
            }
        }

        return Collections.emptyList();
    }

    /**
     * 根据交易编号查询组合交易
     *
     * @param tradeId 交易编号
     * @return 组合交易
     * @see
     * @since 1.0
     */
    public PtlTrade queryPtlTradeByTradeId(String tradeId) {
        return restTemplate.getForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryPtlTradeByTradeId?tradeId=" + tradeId,
            PtlTrade.class);
    }

    /**
     * 根据交易编号查询费用明细信息
     *
     * @param tradeId 交易编号
     * @return 费用明细信息
     * @see
     * @since 1.0
     */
    public List<TrdTradeFeeDetail> queryTrdTradeFeeDetailByTradeId(String tradeId) {
        return restTemplate.getForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryTrdTradeFeeDetailByTradeId?tradeId=" + tradeId,
            TrdTradeFeeDetail.class);
    }

    /**
     * 转换金融交易共通dto
     *
     * @param entity 费用明细
     * @param finprodId 金融产品代码
     * @param actPayAmt 实付金额
     * @return 交易共通dto
     * @see
     * @since 1.0
     */
    private BaseTradeProductDealDto convertBaseTrade(FinProductFeePay002Dto entity, String finprodId, BigDecimal actPayAmt,
                                                     BigDecimal payAmt, String isClean, BigDecimal adjustAmt) {
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        List<TrdTradeFeeDetail> feeDetailList = new ArrayList<TrdTradeFeeDetail>();
        // 金融产品交易Entity
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_产品费用支付管理);
            trdProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_产品费用支付管理.getValue());
        trdProductDeal.setFinprodId(finprodId);

        // trdProductDeal.setFinprodType(
        // productCommonService.convertFeeTypeToCashType(entity.getFeeType()));
        String tempFinprodType = productCommonService.convertFeeTypeToCashType(entity.getFeeType());
        // modify by wm 金融产品交易表中单独存储 start 2019-08-23
        // trdProductDeal.setFinprodType(tempFinprodType);
        trdProductDeal.setFinprodType(productCommonService.convertFeeTypeToCashTypeForFinType(entity.getFeeType()));
        // modify by wm 金融产品交易表中单独存储 end2019-08-23

        trdProductDeal.setFinprodType2(trdProductDeal.getFinprodType());
        trdProductDeal.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        trdProductDeal.setTradeDate(entity.getPayDate());
        trdProductDeal.setVdate(entity.getStartDate());
        trdProductDeal.setMdate(entity.getEndDate());
        trdProductDeal.setSettleDate(entity.getPayDate());
        //币种取界面的币种
//        trdProductDeal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        trdProductDeal.setCcy(entity.getCcy());
        trdProductDeal.setPrinAmt(actPayAmt);
        trdProductDeal.setFpriceAmt(actPayAmt);
        trdProductDeal.setTradeAmt(actPayAmt);
        trdProductDeal.setFeeAmt(actPayAmt);
        trdProductDeal.setPs(DictCodeList.交易方向_产品付费.getValue());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        trdProductDeal.setIsClean(isClean);
        trdProductDeal.setTotleTradeId(entity.getTradeId());
        trdProductDeal.setCashId(entity.getCashId());
        trdProductDeal.setRegistOrg(entity.getRegTrustOrg());
        trdProductDeal.setRemark(entity.getRemark());
        trdProductDeal.setUpdateUser(entity.getUpdateUser());
        trdProductDeal.setCreateUser(entity.getCreateUser());
        trdProductDeal.setCreateDept(entity.getCreateDept());
        trdProductDeal.setWorkflowId(entity.getWorkflowId());
        //交易手续费和结算服务费使用页面计提结束日 yzg 20200111 start
        if (StringUtil.equals(entity.getChargeType(), DictCodeList.收费类型_不规则费用.getValue()) && !StringUtil.equals(entity.getFeeType(), DictCodeList.费用品种类型_交易手续费.getValue())
        		&& !StringUtil.equals(entity.getFeeType(), DictCodeList.费用品种类型_结算服务费.getValue())) {
            trdProductDeal.setMdate(entity.getPayDate());
            trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
        }
        //不规则费用的是否结清字段
        else if (StringUtil.equals(entity.getChargeType(), DictCodeList.收费类型_不规则费用.getValue())) {
            trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
        }
        //交易手续费和结算服务费使用页面计提结束日 yzg 20200111 end
        // 金融产品交易附属表Entity
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
        trdProductDealAdd.setPortfolioId(entity.getPtlId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        trdProductDealAdd.setUpdateUser(entity.getUpdateUser());
        trdProductDealAdd.setCreateUser(entity.getCreateUser());
        trdProductDealAdd.setCreateDept(entity.getCreateDept());
        trdProductDealAdd.setWorkflowId(entity.getWorkflowId());
        // 金融产品交易扩展Entity
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(trdProductDeal.getTradeId());
        paTrdProductDeal.setTheoryAmt(payAmt);
        paTrdProductDeal.setRemainPrinAmt(adjustAmt);
        paTrdProductDeal.setTheoryInt(payAmt);
        // add by gcl 2019/3/18 start
        paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_产品费用支付.getValue());
        if (StringUtil.equals(DictCodeListExt1.一次性费用类型_投管_交易手续费.getValue(), entity.getFeeType())) {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品销售费支付.getValue());
        }
        else if (StringUtil.equals(DictCodeList.费用品种类型_固定管理费.getValue(), entity.getFeeType())) {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品管理费支付.getValue());
        }
        else if (StringUtil.equals(DictCodeList.费用品种类型_托管费.getValue(), entity.getFeeType())) {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品托管费支付.getValue());
        }
        else {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品其他费用.getValue());

        }
        // add by gcl 2019/3/18 end
        // 交易费用明细Entity
        if (StringUtil.isEmptyList(entity.getTaxPositionList())) {
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
            // trdTradeFeeDetail.setFeeType(entity.getFeeType());
            trdTradeFeeDetail.setFeeType(tempFinprodType);
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            trdTradeFeeDetail.setFeeAmt(actPayAmt);
            trdTradeFeeDetail.setFeeId(entity.getFeeId());
            trdTradeFeeDetail.setUpdateUser(entity.getUpdateUser());
            trdTradeFeeDetail.setCreateUser(entity.getCreateUser());
            trdTradeFeeDetail.setCreateDept(entity.getCreateDept());
            trdTradeFeeDetail.setWorkflowId(entity.getWorkflowId());
            //增加是否待摊字段
            trdTradeFeeDetail.setIsPrepaid(entity.getIsPerpaid());
            feeDetailList.add(trdTradeFeeDetail);
        }
        else {
            // 增值税列表处理
            List<PtlFeePosition> positionList = restTemplate.getForList(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryTaxUnpayPosition?ptlId=" + entity.getPtlId()
                                                                        + "&cdate=" + DateUtil.format(entity.getEndDate()),
                PtlFeePosition.class);
            if (!StringUtil.isEmptyList(positionList)) {
                for (PtlFeePosition ptlFeePosition : positionList) {
                    TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
                    // trdTradeFeeDetail.setTradeId(
                    // baseTradeProductDealDto.getTrdProductDeal().getTradeId());
                    trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                    trdTradeFeeDetail.setFeeType(ptlFeePosition.getPtlFeeType());
                    trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                    trdTradeFeeDetail.setFeeAmt(ptlFeePosition.getTdyAccumulPayFee());
                    trdTradeFeeDetail.setFeeId(entity.getFeeId());
                    trdTradeFeeDetail.setUpdateUser(entity.getUpdateUser());
                    trdTradeFeeDetail.setCreateUser(entity.getCreateUser());
                    trdTradeFeeDetail.setCreateDept(entity.getCreateDept());
                    trdTradeFeeDetail.setWorkflowId(entity.getWorkflowId());
                    //增加是否待摊字段
                    trdTradeFeeDetail.setIsPrepaid(entity.getIsPerpaid());
                    feeDetailList.add(trdTradeFeeDetail);
                }
            }
        }

        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
        baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品交易附属表
        baseTradeProductDealDto.setPaTrdProductDeal(paTrdProductDeal);// 金融产品交易扩展
        baseTradeProductDealDto.setTrdTradeFeeDetailList(feeDetailList);// 交易费用明细表

        return baseTradeProductDealDto;
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
    }

    /**
     * queryPayCycleByCashId
     *
     * @param cashId cashId
     * @param mDate mDate
     * @return PayCycle
     * @see
     * @since 1.0
     */
    public String queryPayCycleByCashId(String cashId, String mDate) {
        Map<String, Object> map = new HashMap();
        map.put("cashId", cashId);
        map.put("mDate", mDate);
        String payCycle = restTemplate.postForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryPayCycleByCashId", map, String.class);
        return payCycle;
    }

    /**
     * 查询组合产品的产品代码以及募集币种
     *
     * @param ptlId 组合代码
     * @return 募集币种
     * @see
     * @since 1.0
     */
    public FinProductFeePay001Dto queryByPtlId(String ptlId) {
        return restTemplate.getForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryByPtlId?ptlId=" + ptlId,
            FinProductFeePay001Dto.class);
    }

    public FinProductFeePay002Dto selectFeeByTradeId(String tradeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("tradeId", tradeId);
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectFeeByTradeId", map,
            FinProductFeePay002Dto.class);
    }
    
    /**
     * 查询组合剩余现金
     * @param param
     * @return
     */
    public Map<String, Object> getProAvailableCash(Map<String, Object> param){
    	PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
    	Date vdate = DateUtil.parse((String)param.get("vdate"));
        dto.setCdate(vdate);
        List<String> acctTypeList = new ArrayList<>();
        acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
        acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        dto.setAcctType(acctTypeList);
        dto.setPortfolioId((String)param.get("portfolioId"));
        dto.setCcy((String)param.get("ccy"));
        BigDecimal cashT = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("cashT", cashT);
        return result;
    }

	/**
	 * 查询组合起息日
	 * @param ptlId
	 * @return
	 */
	public String queryVdate(String ptlId) {
		return restTemplate.getForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryVdate?ptlId="
                + ptlId,String.class);
	}

}
