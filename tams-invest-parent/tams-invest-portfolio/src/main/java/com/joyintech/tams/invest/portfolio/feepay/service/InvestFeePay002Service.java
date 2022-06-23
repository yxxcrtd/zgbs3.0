/*
 * 文件名：InvestFeePay002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 
 * 描述：理财资管管理系统V2.0 
 * 修改人：malinlin 
 * 修改时间：2019年12月8日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.portfolio.feepay.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommonWflParam;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.product.dto.FinProductExpireBaseTrdDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.TradeProductDealCommService;
import com.joyintech.tams.invest.portfolio.feepay.dto.InvestFeePay001Dto;
import com.joyintech.tams.invest.portfolio.feepay.dto.InvestFeePay002Dto;
import com.joyintech.tams.invest.portfolio.feepay.dto.InvestFeePay003Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 投资端费用支付管理-新建
 *
 * @author malinlin
 * @version 1.0 2019年12月8日
 * @see InvestFeePay002Service
 * @since 1.0
 */
@Service
public class InvestFeePay002Service extends BaseService<InvestFeePay002Dto> implements BaseProcessService<InvestFeePay002Dto> {

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
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;
    @Autowired
    private PtlTradeMapper ptlTradeMapper;
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
     * 委托指令共通
     */
    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;
    
    /**
     *	 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
//    private void setFeeCashId(InvestFeePay002Dto entity) {
//        if (StringUtil.isNotEmpty(entity.getCashId())) {
//            return;
//        }
//        Map<String, Object> map = new HashMap<>();
//        map.put("feeId", entity.getFeeId());
//        map.put("finprodId", entity.getFinprodId());
//        map.put("cashType1", DictCodeListExt1.现金流一级类型_费用.getValue());
//        String cahsId = restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/selectCashId",
//            map, String.class);
//        entity.setCashId(cahsId);
//    }

    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void insertBussFlowData(InvestFeePay002Dto entity) {
//        this.setFeeCashId(entity);
        if (CollectionUtils.isNotEmpty(entity.getFileArray()) && StringUtil.isNotEmpty(entity.getTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                entity.getFileArray().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getTradeId());
        }

        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }

        TrdCommonWflParam trdCommonWflParam = new TrdCommonWflParam();
        
        // 每条组合明细循环生成一条划款指令
        List<StlPayOrders> stlPayOrdersList = this.genStlPayOrdersDto(entity);
        List<WflStlPayOrders> wflStlPayOrdersList = new ArrayList<WflStlPayOrders>();
        for (StlPayOrders stlPayOrders : stlPayOrdersList) {
         // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrders);
            wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflStlPayOrdersList.add(wflStlPayOrders);
        }
        trdCommonWflParam.setWflStlPayOrdersDtos(wflStlPayOrdersList);

        TrdFinProductDealTotal trdFinProductDealTotal = this.genTrdFinProductDealTotal(entity);
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, trdFinProductDealTotal);
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        //二级法人改造 sjj add 20200628 start
        if(MultilevelDataAuthorityUtil.isEnabled()){
            wflTrdFinProductDealTotal.setInstitutionCode(MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName()));
        }
        //二级法人改造 sjj add 20200628 end
        trdCommonWflParam.setWflTrdFinProductDealTotal(wflTrdFinProductDealTotal);

        // pa 金融汇总交易表
        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, wflTrdFinProductDealTotal);
        wflPaTrdFinDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品费用支付.getValue());
        trdCommonWflParam.setWflPaTrdFinDealTotal(wflPaTrdFinDealTotal);

        FinProductExpireBaseTrdDto finProductExpireBaseTrdDto = this.genBaseTradeDealDtoList(entity);

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
        restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/insertBussFlowData",
            trdCommonWflParam, String.class);
    }

    /**
     * 根据pageId查询所有流程中的数据逐条进行对比
     * <功能详细描述>
     * @param parameter parameter
     * @return MstBankTranFeeManageDto
     * @see
     * @since 1.0
     */
    public List<InvestFeePay002Dto> getBusinessData(WflValidInputParameter parameter) {
        return restTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME +
                        "/InvestFeePay/getBusinessData",parameter,
                InvestFeePay002Dto.class);
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
    public void doInsert(InvestFeePay002Dto entity, OperationType type) {
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
    public void doUpdate(InvestFeePay002Dto entity) {
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
    public void doComplete(InvestFeePay002Dto entity, OperationType type) {
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
     * @param entity 投资端费用支付Dto
     * @return 金融产品交易汇总
     * @see
     * @since 1.0
     */
    private TrdFinProductDealTotal genTrdFinProductDealTotal(InvestFeePay002Dto entity) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getTradeId());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_产品费用支付管理.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        trdFinProductDealTotal.setVdate(entity.getStartDate());
        trdFinProductDealTotal.setMdate(entity.getEndDate());
        trdFinProductDealTotal.setSettleDate(entity.getPayDate());
        trdFinProductDealTotal.setTradeAmt(entity.getActPayAmt());
        trdFinProductDealTotal.setCashId(entity.getCashId());
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_产品付费.getValue());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setSecManageAcctId(entity.getSecAcctId());
//        trdFinProductDealTotal.setPortfolioId(entity.getPtlId());
        trdFinProductDealTotal.setRemark(entity.getRemark());
        trdFinProductDealTotal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        trdFinProductDealTotal.setUpdateUser(entity.getUpdateUser());
        trdFinProductDealTotal.setCreateUser(entity.getCreateUser());
        trdFinProductDealTotal.setCreateDept(entity.getCreateDept());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setTgBusiType("142");
        trdFinProductDealTotal.setFinprodType(entity.getFeeType());
        trdFinProductDealTotal.setFinprodType2(entity.getFeeType());
//        trdFinProductDealTotal.setMdate(entity.getPayDate());
        trdFinProductDealTotal.setTgBusiType("142");
        //二级法人改造 sjj add 20200628 start
        if(MultilevelDataAuthorityUtil.isEnabled()){
            trdFinProductDealTotal.setInstitutionCode(MultilevelDataAuthorityUtil.getOrganizationCode(entity.getCreateUser()));
        }
        //二级法人改造 sjj add 20200628 end
        return trdFinProductDealTotal;
    }

    /**
     * 生成划款指令信息
     *
     * @param entity 投资端费用支付Dto
     * @return 划款指令信息
     * @see
     * @since 1.0
     */
    private List<StlPayOrders> genStlPayOrdersDto(InvestFeePay002Dto entity) {
        entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(entity.getBussTrdTransferOrder()));
        List<StlPayOrders> StlPayOrdersList = new ArrayList<StlPayOrders>();
        for (BussTrdTransferOrderDto bussTrdTransferOrderDto : entity.getBussTrdTransferOrder()) { 
            // 划款指令表Entity
            StlPayOrders stlPayOrders = new StlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, bussTrdTransferOrderDto);
            stlPayOrders.setTradeId(entity.getTradeId());
            stlPayOrders.setWorkflowId(entity.getWorkflowId());

            List<? extends DataDictItem> yesNoItemList = SysDataUtil.getDataDictItem(DictCode.是否.getValue());
            yesNoItemList.forEach(item -> {
                if (StringUtil.equals(item.getItemValue(), stlPayOrders.getIsNeedSend())) {
                    stlPayOrders.setIsNeedSend(item.getItemCode());
                }
            });
            stlPayOrders.setCreateTime(entity.getCreateTime());
            stlPayOrders.setUpdateTime(entity.getUpdateTime());
            StlPayOrdersList.add(stlPayOrders);
        }
        return StlPayOrdersList;
    }

    /**
     * 金融产品交易共通业务逻辑处理
     *
     * @param entity 投资端费用支付Dto
     * @return 金融产品交易共通
     * @see
     * @since 1.0
     */
    private FinProductExpireBaseTrdDto genBaseTradeDealDtoList(InvestFeePay002Dto entity) {
        FinProductExpireBaseTrdDto finProductExpireBaseTrdDto = new FinProductExpireBaseTrdDto();

        List<TrdProductDeal> trdProductDealList = new ArrayList<>();
        List<TrdProductDealAdd> trdProductDealAddList = new ArrayList<>();
        List<PaTrdProductDeal> paTrdProductDealList = new ArrayList<>();
        List<TrdTradeFeeDetail> feeDetailList = new ArrayList<TrdTradeFeeDetail>();
        // 费用明细
        if (!StringUtil.isEmptyList(entity.getFeeInfoList())) {
            for (InvestFeePay003Dto finProductFeePay003Dto : entity.getFeeInfoList()) {
                // 根据组合id查询产品信息
                InvestFeePay001Dto investFeePay001Dto = restTemplate.getForObject(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/queryByPtlId?ptlId=" + finProductFeePay003Dto.getPtlId(),
                    InvestFeePay001Dto.class);
                
                if (StringUtil.isNotEmpty(investFeePay001Dto.getFinprodId())) {
                    finProductFeePay003Dto.setFinprodId(investFeePay001Dto.getFinprodId());
                }

                // 转换金融交易共通dto
                BaseTradeProductDealDto baseTradeProductDealDto = this.convertBaseTrade(entity,
                    finProductFeePay003Dto.getFinprodId(), finProductFeePay003Dto.getActPayAmt(),
                    finProductFeePay003Dto.getPayAmt(), finProductFeePay003Dto.getIsClean(),
                    finProductFeePay003Dto.getAdjustAmt(), finProductFeePay003Dto.getPtlId());
                // 金融产品交易
                trdProductDealList.add(baseTradeProductDealDto.getTrdProductDeal());
                trdProductDealAddList.add(baseTradeProductDealDto.getTrdProductDealAdd());
                paTrdProductDealList.add(baseTradeProductDealDto.getPaTrdProductDeal());
                feeDetailList.addAll(baseTradeProductDealDto.getTrdTradeFeeDetailList());
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
     * @param entity 投资端费用支付Dto
     * @return 组合交易表信息
     * @see
     * @since 1.0
     */
    private List<PtlTrade> genPtlTrdea(InvestFeePay002Dto entity) {
        List<PtlTrade> ptlTradeList = new ArrayList<PtlTrade>();
        // 计提费用
        if (!StringUtil.isEmptyList(entity.getFeeInfoList())) {
            for (InvestFeePay003Dto finProductFeePay003Dto : entity.getFeeInfoList()) {
                PtlTrade ptlTrade = new PtlTrade();
                ptlTrade.setPortfolioId(finProductFeePay003Dto.getPtlId());
                ptlTrade.setBusiNo(entity.getTradeId());
                ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                ptlTrade.setBusiId(finProductFeePay003Dto.getPtlId());
                ptlTrade.setBusiType(DictCodeList.共通业务类型_产品费用支付管理.getValue());
                ptlTrade.setSettleDate(entity.getPayDate());
                ptlTrade.setUpdateUser(entity.getUpdateUser());
                ptlTrade.setCreateUser(entity.getCreateUser());
                ptlTrade.setCreateDept(entity.getCreateDept());
                ptlTradeList.add(ptlTrade);
            }
        } 

        return ptlTradeList;
    }

    /**
     * 新建确认doComplete
     *
     * @param entity 投资端费用支付Dto
     * @see
     * @since 1.0
     */
    private void doCompleteInsert(InvestFeePay002Dto entity) {
//        this.setFeeCashId(entity); 
        // 每条组合明细循环生成一条划款指令
        List<StlPayOrders> stlPayOrdersList = this.genStlPayOrdersDto(entity);
        for (StlPayOrders stlPayOrders : stlPayOrdersList) {
            stlPayOrdersMapper.insertSelective(stlPayOrders);
        }

        FinProductExpireBaseTrdDto finProductExpireBaseTrdDto = this.genBaseTradeDealDtoList(entity);

        // 组合交易表
        List<PtlTrade> ptlTradeList = this.genPtlTrdea(entity);

        // 金融产品交易汇总表
        TrdFinProductDealTotal trdFinProductDealTotal = this.genTrdFinProductDealTotal(entity);
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

        if (ptlTradeList != null) {
            ptlTradeList.forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
        }

        trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);

        // 组合现金流水Entity
        List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<PtlCashPositionFlowDto>();
        for (StlPayOrders stlPayOrders : stlPayOrdersList) {
            PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
            ptlCashPositionFlowDto.setPortfolioId(stlPayOrders.getPortfolioId());
            ptlCashPositionFlowDto.setAcctId(stlPayOrders.getMyAcctNo());
            ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
            ptlCashPositionFlowDto.setCdate(entity.getPayDate());
            ptlCashPositionFlowDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            ptlCashPositionFlowDto.setAmount(stlPayOrders.getSettleAmt().negate());
            ptlCashPositionFlowDto.setOrderId(stlPayOrders.getOrderId());
            ptlCashPositionFlows.add(ptlCashPositionFlowDto);
        }

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
            for (TrdProductDeal tradeDto : finProductExpireBaseTrdDto.getTrdProductDealList()) {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(entity.getFinprodId());
                if (StringUtil.isBlank(inputDto.getProdid())) {
                    inputDto.setProdid(tradeDto.getFinprodId());
                }
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                inputDto.setTrdProductDeal(tradeDto);
                if(StringUtil.equals(entity.getFeeType(), DictCodeListExt3.投资端费用品种类型_账户维护费.getValue())){
                	tradeDto.setFeeType(entity.getFeeType());
                }
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
                
                // 调用投管接口 add by cxq 20190227 start
                TrdProductDealAdd trdProductDealAdd = finProductExpireBaseTrdDto.getTrdProductDealAddList().stream().filter(
                        trdProductDealAdd1 -> tradeDto.getTradeId().equals(trdProductDealAdd1.getTradeId())).findFirst().get();
                List<TrdTradeFeeDetail> trdTradeFeeDetails = finProductExpireBaseTrdDto.getTrdTradeFeeDetailList().stream().filter(
                        trdProductDealAdd1 -> tradeDto.getTradeId().equals(trdProductDealAdd1.getTradeId())).collect(
                            Collectors.toList());
                InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                investInterfCommDto.setPageId("InvestFeePay002");
                investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
                investInterfCommDto.setPortfolioId(entity.getPtlId());
                investInterfCommDto.setTrdProductDeal(tradeDto);
                investInterfCommDto.setTrdProductDealAdd(trdProductDealAdd);
                investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
                investInterfCommDto.setTradeFeeDetaillist(trdTradeFeeDetails);
                investInterfCommService.doInvestInterf(investInterfCommDto);
                // 调用投管接口 add by cxq 20190227 end
            }
        }
        // 一体化对应etliu end
        
        //20191008新增 	
        TrdAsynHandleStatus trdAsynHandleStatus = new TrdAsynHandleStatus();
        trdAsynHandleStatus.setTradeId(trdFinProductDealTotal.getTotleTradeId());		//交易汇总id
        trdAsynHandleStatus.setTrdTableType("01");		// 交易表类型(01：金融产品交易汇总表，02：金融产品交易表)
        trdAsynHandleStatus.setHandleStatus("01");		//01	待处理
        trdAsynHandleStatusMapper.insertSelective(trdAsynHandleStatus);

        if (!(StringUtil.isNotEmpty(entity.getConfirmFlag()) && StringUtil.equals(entity.getConfirmFlag(), "confirm")) && StringUtil.isNotEmpty(entity.getStartDate())) {
            //流程生效更新
        	restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/updateTrdAccountmfConfigDto",
                    entity, String.class);
        }

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
            ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/queryTrdProductDealByTotleTradeId?tradeId=" + tradeId,
            TrdProductDeal.class);
    }

    /**
     * 撤销流程表登录
     *
     * @param entity 产品费用支付Dto
     * @see
     * @since 1.0
     */
    private void doInsertRevoke(InvestFeePay002Dto entity) {
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
                        // 调用投管接口 add by renping 20190205 start
                        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                        investInterfCommDto.setPageId("InvestFeePay002");
                        investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
                        investInterfCommDto.setPortfolioId(entity.getPtlId());
                        investInterfCommDto.setRevokeReason(entity.getRevokeReason());
                        investInterfCommDto.setTrdProductDeal(trdProductDeal);
                        investInterfCommDto.setTrdProductDealAdd(baseDto.getTrdProductDealAdd());
                        if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
                            investInterfCommDto.setStlPayOrders(baseDto.getStlPayOrdersList().get(0));
                        }
                        investInterfCommDto.setTradeFeeDetaillist(baseDto.getTrdTradeFeeDetailList());
                        investInterfCommService.doInvestInterf(investInterfCommDto);
                        // 调用投管接口 add by renping 20190205 end
                    }
                }
            }
            trdCommonWflParam.setWflTrdProductDeals(wflTrdProductDealList);
            trdCommonWflParam.setWflDealAdds(wflTrdProductDealAddList);
            trdCommonWflParam.setWflPaDeals(WflPaTrdProductDealList);
            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
            List<StlPayOrdersDto> stlPayOrdersList = genStlPayOrdersRevoke(entity.getTradeId());
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
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/doInsertRevoke", trdCommonWflParam,
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
    private void doCompleteRevoke(InvestFeePay002Dto entity) {

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
            ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/queryStlPayOrdersByTradeId?tradeId="
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
                ptlCashPositionFlowDto.setPortfolioId(stlPayOrders.getPortfolioId());
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
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/doCompleteRevoke", entity, String.class);

        // 一体化对应etliu start
        // 所有交易生效后调用的共通方法
        if (!StringUtil.isEmptyList(trdProductDealList)) {
            for (TrdProductDeal tradeDto : trdProductDealList) {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(tradeDto.getFinprodId());
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

        if (!(StringUtil.isNotEmpty(entity.getConfirmFlag()) && StringUtil.equals(entity.getConfirmFlag(), "confirm")) && StringUtil.isNotEmpty(entity.getStartDate())) {
            //流程生效更新
        	restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/updateTrdAccountmfConfigDto",
                    entity, String.class);
        }

    }

    /**
     * 查询证券账户下产品关联的费用
     *
     * @param map 参数
     * @return 关联的费用
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryFeeInfoList(Map<String, Object> map) {
        if (StringUtil.equals(map.get("operateType"), "new")) {
            return restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/queryFeeInfoListEx", map,
                SelectOptionDto.class);
        }
        else {
            return restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/queryFeeInfoList", map,
                SelectOptionDto.class);
        }
    }

    /**
     * 查询费用上次计提结束日
     *
     * @param secAcctId 证券账户
     * @param feeType 费用类型
     * @return 上次计提结束日
     * @see
     * @since 1.0
     */
    public String queryLastPayDate(String secAcctId, String feeType,String org, String operateType) {
        return restTemplate.getForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/queryLastPayDate?secAcctId="
                                         + secAcctId + "&feeType=" + feeType + "&org=" + org,
            String.class);
    }

    /**
     * 查询费用上次计提结束日
     *
     * @param secAcctId 证券账户
     * @param feeType 费用类型
     * @return 上次计提结束日
     * @see
     * @since 1.0
     */
    public String queryStartDate(String secAcctId) {
        return restTemplate.getForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/queryStartDate?secAcctId="
                        + secAcctId ,
                String.class);
    }

    /**
     * 查询费用待支付费用
     *
     * @param secAcctId 证券账户
     * @param feeType 费用类型
     * @param cdate 计提结束日
     * @param regTrustOrg 登记托管机构
     * @return 待支付费用
     * @see
     * @since 1.0
     */
    public BigDecimal queryUnpayPosition(String secAcctId, String feeType, String cdate, String regTrustOrg) {
        return restTemplate.getForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/queryUnpayPosition?secAcctId="
                                         + secAcctId + "&feeType=" + feeType + "&cdate=" + cdate + "&regTrustOrg=" + regTrustOrg,
            BigDecimal.class);
    }

    /**
     * 结算服务费、交易手续费、佣金联动带出默认费用
     *
     * @param secAcctId
     * @param feeType
     * @param cdate
     * @return
     */
    public BigDecimal queryPayTradeAmt(String secAcctId, String org, String cdate, String startDate, String feeType,String confirmFlag) {
        return restTemplate.getForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/queryPayTradeAmt?secAcctId="
                                         + secAcctId + "&org=" + org + "&cdate=" + cdate + "&startDate=" + startDate + "&feeType=" + feeType+ "&confirmFlag=" + confirmFlag,
            BigDecimal.class);
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
    public InvestFeePay002Dto queryPendingFeeInfoByCashId(String cashId, String mdate) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cashId", cashId);
        map.put("mdate", mdate);
        map.put("flag", DictCodeList.是否_否.getValue());

        map.put("userId", SecurityUtil.getLoginUser().getUserName());
        InvestFeePay001Dto finProductFeePay001Dto = restTemplate.postForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/queryPendingFeeInfoByCashId", map,
            InvestFeePay001Dto.class);
        /*
         * List<String> list = new ArrayList<String>(); list.add("AA19002"); map.put("ptlId",list); FinProductFeePay001Dto finProductFeePay001Dto1 =
         * restTemplate.postForObject( ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeePay/checkpayAmt", map, FinProductFeePay001Dto.class);
         */

        if (finProductFeePay001Dto != null) {
            InvestFeePay002Dto finProductFeePay002Dto = new InvestFeePay002Dto();
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
    public List<InvestFeePay003Dto> queryPayFeeInfoList(Map<String, Object> map) {
        List<InvestFeePay003Dto> prdList = restTemplate.getForList(
            ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/queryPayFeeInfoList?secAcctId="
                                     + map.get("secAcctId") + "&org=" + map.get("org") + "&cdate=" + map.get("calcEndDate")+"&startDate=" + map.get("calcStartDate") + "&feeType=" + map.get("feeType") + "&confirmFlag=" + map.get("confirmFlag"),
                                     InvestFeePay003Dto.class);
        return prdList;
    }

    /**
     * 根据交易编号查询费用明细信息
     *
     * @param tradeId 交易编号
     * @return 费用明细信息
     * @see
     * @since 1.0
     */
    public List<InvestFeePay003Dto> queryFeeDetailByTradeId(String tradeId, String payDate) {
        List<InvestFeePay003Dto> feeInfoList = restTemplate.getForList(
            ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/queryFeeDetailByTradeId?tradeId=" + tradeId,
            InvestFeePay003Dto.class);
        return feeInfoList;
    }

    /**
     * 查询费用明细列表
     *
     * @param secAcctId 证券账户代码
     * @param ptlId 组合代码
     * @param calcStartDate 区间开始日
     * @param calcEndDate 区间结束日
     * @param feeId 费用代码
     * @param feeType
     * @return 费用明细列表
     * @see
     * @since 1.0
     */
    public List<InvestFeePay003Dto> queryFeePositionDetail(String secAcctId, String ptlId, String calcStartDate, String calcEndDate,
                                                               String feeType,String confirmFlag,String tradeId,String org) {
        // 根据费用品种对应到交易费用明细表的费用类型
        List<InvestFeePay003Dto> positionDetailList = restTemplate.getForList(
            ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/queryFeePositionDetail?feeType=" + feeType
                                                                             + "&startDate=" + calcStartDate + "&enddate="
                                                                             + calcEndDate + "&secAcctId=" + secAcctId
                                                                             + "&ptlId=" + ptlId+ "&confirmFlag=" + confirmFlag + "&tradeId=" + tradeId + "&org=" + org,
                                                                             InvestFeePay003Dto.class);

        if (StringUtil.isNotEmpty(positionDetailList)) {
            return positionDetailList;
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
    private BaseTradeProductDealDto convertBaseTrade(InvestFeePay002Dto entity, String finprodId, BigDecimal actPayAmt,
                                                     BigDecimal payAmt, String isClean, BigDecimal adjustAmt, String ptlId) {
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

        String tempFinprodType = convertFeeTypeToCashType(entity.getFeeType());
        // modify by wm 金融产品交易表中单独存储 start 2019-08-23
        trdProductDeal.setFinprodType(convertFeeTypeToCashTypeForFinType(entity.getFeeType()));
        // modify by wm 金融产品交易表中单独存储 end2019-08-23

        trdProductDeal.setFinprodType2(trdProductDeal.getFinprodType());
        trdProductDeal.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        trdProductDeal.setSecManageAcctId(entity.getSecAcctId());
        trdProductDeal.setTradeDate(entity.getPayDate());
        trdProductDeal.setVdate(entity.getStartDate());
        trdProductDeal.setMdate(entity.getEndDate());
        trdProductDeal.setSettleDate(entity.getPayDate());
        trdProductDeal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
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
//        trdProductDeal.setMdate(entity.getPayDate());
        trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
        if(trdProductDeal.getMdate()==null){
            trdProductDeal.setIsClean("N");
        }
        // 金融产品交易附属表Entity
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
        trdProductDealAdd.setPortfolioId(ptlId);
        trdProductDealAdd.setGenType(StringUtil.isNotEmpty(entity.getConfirmFlag()) && StringUtil.equals(entity.getConfirmFlag(), "confirm") ?
                DictCodeListExt1.生成方式_批处理生成.getValue() : DictCodeListExt1.生成方式_画面录入.getValue());
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
        paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品其他费用.getValue());
        // add by gcl 2019/3/18 end
        // 交易费用明细Entity
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
        trdTradeFeeDetail.setFeeType(tempFinprodType);
        trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
        trdTradeFeeDetail.setFeeAmt(actPayAmt);
        trdTradeFeeDetail.setFeeId(entity.getFeeId());
        trdTradeFeeDetail.setUpdateUser(entity.getUpdateUser());
        trdTradeFeeDetail.setCreateUser(entity.getCreateUser());
        trdTradeFeeDetail.setCreateDept(entity.getCreateDept());
        trdTradeFeeDetail.setWorkflowId(entity.getWorkflowId());
        feeDetailList.add(trdTradeFeeDetail);

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
     * 费用类型转成现金流二级类型
     * 
     * @param feeType 费用类型
     * @return 现金流二级类型
     * @see
     * @since 1.0
     */
    public String convertFeeTypeToCashType(String feeType) {
        if (StringUtil.equals(feeType, DictCodeList.费用品种类型_销售手续费.getValue())) {
            return DictCodeListExt1.现金流二级类型_销售费.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_固定管理费.getValue())) {
            return DictCodeListExt1.现金流二级类型_固定管理费.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_托管费.getValue())) {
            return DictCodeListExt1.现金流二级类型_托管费.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_代销费.getValue())) {
            return DictCodeListExt1.现金流二级类型_代销费.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_浮动差额中收.getValue())) {
            return DictCodeListExt1.现金流二级类型_浮动管理费.getValue();
        }
     // 一体化对应etliu start
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_浮动管理费.getValue())) {
            return DictCodeListExt1.现金流二级类型_浮动管理费.getValue();
        }
     // 一体化对应etliu end
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_其他费用.getValue())) {
            return DictCodeListExt1.一次性费用类型_其他费用.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_交易手续费.getValue())) {
            // 一体化对应etliu start
//            return DictCodeListExt2.费用类型_银行手续费.getValue();
            return DictCodeListExt1.一次性费用类型_交易手续费.getValue();
            // 一体化对应etliu end
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_结算服务费.getValue())) {
         // 一体化对应etliu start
//            return DictCodeListExt2.费用类型_结算服务费.getValue();
            return DictCodeListExt1.一次性费用类型_结算手续费.getValue();
         // 一体化对应etliu end
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_账户维护费.getValue())) {
            return DictCodeListExt1.一次性费用类型_账户维护费.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_增值附加税费.getValue())) {
         // 一体化对应etliu start
//            return DictCodeListExt2.费用类型_增值附加税费.getValue();
            return DictCodeListExt1.现金流二级类型_税额.getValue();
         // 一体化对应etliu end
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_外包服务费.getValue())) {
            return DictCodeListExt1.现金流二级类型_外包服务费.getValue();
        }
        //add by wm start 2019-08-15 通道费用转换
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_通道托管费.getValue())) {
            return DictCodeListExt1.一次性费用类型_通道费.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_通道管理费.getValue())) {
            return DictCodeListExt1.一次性费用类型_通道费.getValue();
        }
      //add by wm end 2019-08-15 通道费用转换
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_佣金.getValue())) {
            return DictCodeListExt1.一次性费用类型_佣金.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeListExt1.费用品种类型_经手费.getValue())) {
            return DictCodeListExt1.一次性费用类型_经手费.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeListExt1.费用品种类型_结算费.getValue())) {
            return DictCodeListExt1.一次性费用类型_结算费.getValue();
        }
        /**add by chendijun 2019-10-15 投资顾问费转换 **/
        else if (StringUtil.equals(feeType, DictCodeListExt2.费用品种类型_投资顾问费.getValue())) {
            return DictCodeListExt2.现金流二级类型_投资顾问费.getValue();
        }
        return null;
    }
    
    /**
     * 费用类型转成现金流二级类型
     * 
     * @param feeType 费用类型
     * @return 现金流二级类型
     * @see
     * @since 1.0
     */
    public String convertFeeTypeToCashTypeForFinType(String feeType) {
        if (StringUtil.equals(feeType, DictCodeList.费用品种类型_销售手续费.getValue())) {
            return DictCodeListExt1.现金流二级类型_销售费.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_固定管理费.getValue())) {
            return DictCodeListExt1.现金流二级类型_固定管理费.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_托管费.getValue())) {
            return DictCodeListExt1.现金流二级类型_托管费.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_代销费.getValue())) {
            return DictCodeListExt1.现金流二级类型_代销费.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_浮动差额中收.getValue())) {
            return DictCodeListExt1.现金流二级类型_浮动管理费.getValue();
        }
     // 一体化对应etliu start
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_浮动管理费.getValue())) {
            return DictCodeListExt1.现金流二级类型_浮动管理费.getValue();
        }
     // 一体化对应etliu end
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_其他费用.getValue())) {
            return DictCodeListExt1.金融产品类型_交易费用.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_交易手续费.getValue())) {
            // 一体化对应etliu start
//            return DictCodeListExt2.费用类型_银行手续费.getValue();
            return DictCodeListExt1.金融产品类型_交易费用.getValue();
            // 一体化对应etliu end
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_结算服务费.getValue())) {
         // 一体化对应etliu start
//            return DictCodeListExt2.费用类型_结算服务费.getValue();
            return DictCodeListExt1.金融产品类型_交易费用.getValue();
         // 一体化对应etliu end
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_账户维护费.getValue())) {
            return DictCodeListExt1.金融产品类型_预提费用.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_增值附加税费.getValue())) {
         // 一体化对应etliu start
            return DictCodeListExt1.现金流二级类型_税额.getValue();
         // 一体化对应etliu end
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_外包服务费.getValue())) {
            return DictCodeListExt1.现金流二级类型_外包服务费.getValue();
        }
        //add by wm start 2019-08-15 通道费用转换
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_通道托管费.getValue())) {
            return DictCodeListExt1.一次性费用类型_通道费.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_通道管理费.getValue())) {
            return DictCodeListExt1.一次性费用类型_通道费.getValue();
        }
      //add by wm end 2019-08-15 通道费用转换
        else if (StringUtil.equals(feeType, DictCodeList.费用品种类型_佣金.getValue())) {
            return DictCodeListExt1.金融产品类型_交易费用.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeListExt1.费用品种类型_经手费.getValue())) {
            return DictCodeListExt1.金融产品类型_交易费用.getValue();
        }
        else if (StringUtil.equals(feeType, DictCodeListExt1.费用品种类型_结算费.getValue())) {
            return DictCodeListExt1.金融产品类型_交易费用.getValue();
        }
        /**add by chendijun 2019-10-15 投资顾问费转换 **/
        else if (StringUtil.equals(feeType, DictCodeListExt2.费用品种类型_投资顾问费.getValue())) {
            return DictCodeListExt2.现金流二级类型_投资顾问费.getValue();
        }

        return null;
    }

    public InvestFeePay002Dto selectFeeByTradeId(String tradeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("tradeId", tradeId);
        return restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/selectFeeByTradeId", map,
            InvestFeePay002Dto.class);
    }
    
    /**
     * 生成划款指令撤销数据
     * 
     * @param tradeId 交易编号
     * @return 划款指令撤销数据
     * @see
     * @since 1.0
     */
    public List<StlPayOrdersDto> genStlPayOrdersRevoke(String tradeId) {
        List<StlPayOrdersDto> stlPayOrdersList = this.queryStlPayOrdersByTradeId(tradeId);
        if (!StringUtil.isEmptyList(stlPayOrdersList)) {
            for (StlPayOrders stlPayOrders : stlPayOrdersList) {
                stlPayOrders.setpTradeId(stlPayOrders.getOrderId());
                stlPayOrders.setOrderId(StringUtil.defaultString(stlPayOrders.getOrderId()) + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                stlPayOrders.setTradeId(StringUtil.defaultString(stlPayOrders.getTradeId()) + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
            }
        }

        return stlPayOrdersList;
    }
    
    /**
     * 根据汇总交易编号划款指令信息
     * 
     * @param tradeId 汇总交易编号
     * @return 划款指令信息
     * @see
     * @since 1.0
     */
    public List<StlPayOrdersDto> queryStlPayOrdersByTradeId(String tradeId) {
        return restTemplate.getForList(ServiceApplication.PORTFOLIO_SERVICE_NAME 
            + "/InvestFeePay/queryStlPayOrdersByTradeId?tradeId=" + tradeId,
            StlPayOrdersDto.class);
    }
    
    /**
     * 查询证券账户登记托管机构
     * @param secAcctId 证券账户
     * @return 登记托管机构
     * @see
     * @since 1.0
     */
    public String querySecAcctOrg(String secAcctId) {
        return restTemplate.getForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME 
            + "/InvestFeePay/querySecAcctOrg?secAcctId=" + secAcctId,
            String.class);
    }
    
    /**
     * 划款信息
     *
     * @param bizCode 业务编号
     * @return
     */
    public List<BussTrdTransferOrderDto> selectPayOrders(String bizCode) {
        return restTemplate.postForList(ServiceApplication.SETTLE_SERVICE_NAME + "/BussSettleOrderComm/selectOrdersByBizCode",
            bizCode, BussTrdTransferOrderDto.class);
    }

    public List<SelectOptionDto> getPortFolioName(Map<String, Object> pageParam) {
        return restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/getPortFolioName",
                pageParam, SelectOptionDto.class);
    }

    /**
     * 查询所有有效的数据进行对比
     * <功能详细描述>
     * @param map
     * @return InvestFeePay001Dto
     * @see
     * @since 1.0
     */
    public List<InvestFeePay001Dto> getEffectData(Map<String, Object> map) {
        return restTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME +
                        "/InvestFeePay/getEffectData",map,
                        InvestFeePay001Dto.class);
    }

    public BigDecimal queryPayAmt(String totleTradeId) {
        return restTemplate.getForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/InvestFeePay/queryPayAmt?totleTradeId="
                        + totleTradeId, BigDecimal.class);
    }
}
