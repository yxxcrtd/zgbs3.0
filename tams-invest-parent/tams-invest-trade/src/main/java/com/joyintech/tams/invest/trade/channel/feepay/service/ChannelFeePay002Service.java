/*
 * 文件名：ChannelAssetQuery001Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：yangjm 修改时间：2019年3月13日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.channel.feepay.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.invest.common.dto.channel.ChannelFeeInfoDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.settle.BussSettleOrderCommService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.product.addreduce.ProductTradeInfoDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.trade.channel.feepay.dto.ChannelFeePay002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 通道费用支付新建
 * 
 * @author Johnnie
 * @version 1.0 2019年3月13日
 * @see ChannelFeePay002Service
 * @since 1.0
 */
@Service("ChannelFeePay002Service")
@Transactional(rollbackFor = Exception.class)
public class ChannelFeePay002Service extends BaseService<ChannelFeePay002Dto> implements BaseProcessService<ChannelFeePay002Dto> {

    @Autowired
    private ChannelFeePay002Service channelFeePay002Service;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;

    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderImplService;

    @Autowired
    private BussSettleOrderCommService bussSettleOrderCommService;

    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;

    /**
     * BaseTradeProductDealCommService
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;
    @Autowired
    private PtlTradeMapper ptlTradeMapper;
    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。
     * {@inheritDoc}
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

    @Override
    public void doInsert(ChannelFeePay002Dto entity, OperationType type) {

        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && StringUtil.isNotEmpty(entity.getTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getTradeId());
        }
        String totleTradeId = entity.getTradeId();
        if (type == OperationType.DELETE || "submit".equals(entity.getOperateType())) {
            WflTrdFinProductDealTotal totle = new WflTrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(totle, entity);
            totle.setTotleTradeId(totleTradeId);
            totle.setBusiType(DictCodeList.业务类型_产品费用交易.getValue());
            totle.setFinprodType(DictCodeListExt1.一次性费用类型_通道费.getValue());
            totle.setFinprodType2(DictCodeListExt1.一次性费用类型_通道费.getValue());
            totle.setPs(DictCodeList.交易方向_付费.getValue());
            totle.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue")));
            totle.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            totle.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            totle.setIsCancel(DictCodeList.是否_否.getValue());
            totle.setTradeDate(entity.getSettleDate());

            if (type == OperationType.DELETE) {
                String rTotleTradeId = totleTradeId+"R";
                totle.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                totle.setTotleTradeId(rTotleTradeId);
                totle.setRemark(entity.getRevokeReason());
                totle.setpTradeId(totleTradeId);
            }

            wflTrdFinProductDealTotalMapper.insertSelective(totle);

            for(int i=0;i<entity.getFeeInfoList().size();i++) {

                WflTrdProductDeal wflDeal = new WflTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflDeal, entity);
                if(type == OperationType.DELETE){
                    String tradeId = restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/ChannelFeePay/getTradeId?totleTradeId="+totleTradeId+"&portfolioId="+entity.getFeeInfoList().get(i).getPortfolioId(),
                            String.class);
                    wflDeal.setTradeId(tradeId);
                }else{
                    BussinessCodeParameter parameter = new BussinessCodeParameter(DictCodeList.共通业务类型_通道费用支付);
                    wflDeal.setTradeId(baseGetBusinessIdImpl.buildBussinessCode(parameter));
                }
                wflDeal.setTotleTradeId(totleTradeId);
                wflDeal.setBusiType(DictCodeList.业务类型_产品费用交易.getValue());
                wflDeal.setFinprodType(DictCodeListExt1.一次性费用类型_通道费.getValue());
                wflDeal.setFinprodType2(DictCodeListExt1.一次性费用类型_通道费.getValue());
                wflDeal.setPs(DictCodeList.交易方向_付费.getValue());
                wflDeal.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue")));
                wflDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                wflDeal.setIsCancel(DictCodeList.是否_否.getValue());
                wflDeal.setTradeDate(entity.getSettleDate());
                wflDeal.setTradeAmt(entity.getFeeInfoList().get(i).getActPayAmt());
                // wflDeal.setIsClean(DictCodeList.是否_是.getValue());

                WflTrdProductDealAdd wflDealAdd = new WflTrdProductDealAdd();
                wflDealAdd.setTradeId(wflDeal.getTradeId());
                wflDealAdd.setPortfolioId(entity.getFeeInfoList().get(i).getPortfolioId());
                wflDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflDealAdd.setWorkflowId(wflDeal.getWorkflowId());

                WflPaTrdProductDeal wflPaDeal = new WflPaTrdProductDeal();
                wflPaDeal.setTradeId(wflDeal.getTradeId());
                wflPaDeal.setPaBussType(DictCodeListExt1.平安业务类型_通道费用支付.getValue());
                wflPaDeal.setTheoryInt(entity.getFeeInfoList().get(i).getPayAmt());
                wflPaDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflPaDeal.setWorkflowId(wflDeal.getWorkflowId());

                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
//                List<WflStlPayOrders> stlPayOrdersDtos = gen划款指令流程(entity, type);
                WflStlPayOrders stlPayOrdersDto = gen划款指令流程(entity, type,wflDeal.getTradeId(),i);

                if (type == OperationType.DELETE) {
                    String rTradeId = wflDeal.getTradeId() + "R";
                    String rTotleTradeId = wflDeal.getTotleTradeId()+"R";
                    wflDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    wflDeal.setTradeId(rTradeId);
                    wflDeal.setTotleTradeId(rTotleTradeId);
                    wflDeal.setCancelRemark(entity.getRevokeReason());
                    wflDealAdd.setTradeId(rTradeId);
                    wflPaDeal.setTradeId(rTradeId);
                }
                // 插入Entity对应的业务流程DB中
//            bussCommonMapperDelegateService.insertAutoClassifyBatch(wflDeal, wflDealAdd, wflPaDeal, stlPayOrdersDtos);
                wflTrdProductDealMapper.insertSelective(wflDeal);
                wflTrdProductDealAddMapper.insertSelective(wflDealAdd);
                wflPaTrdProductDealMapper.insertSelective(wflPaDeal);
//                stlPayOrdersDtos.forEach(wflStlPayOrders -> wflStlPayOrdersMapper.insertSelective(wflStlPayOrders));
                wflStlPayOrdersMapper.insertSelective(stlPayOrdersDto);
                if (type != OperationType.DELETE) {
                    TrdProductDealAdd dealAdd = new TrdProductDealAdd();
                    BeanUtil.copyPropertiesIgnoreNull(dealAdd, wflDealAdd);

                    TrdProductDeal deal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(deal, wflDeal);

                    StlPayOrdersDto orders = new StlPayOrdersDto();
                    BeanUtil.copyPropertiesIgnoreNull(orders, stlPayOrdersDto);

                    execute共通(dealAdd, deal, orders, DictCodeListExt1.交易流程状态_流程中,entity,i);
                }
            }
        }
    }

    private void execute共通(TrdProductDealAdd dealAdd, TrdProductDeal deal, StlPayOrdersDto stlPayOrders, DictCodeListExt1 交易流程状态,ChannelFeePay002Dto entity,int i) {
        List<StlPayOrdersDto> wflStlPayOrdersDto = new ArrayList<>();
        wflStlPayOrdersDto.add(stlPayOrders);
        // 调用业务组件.【金融产品交易共通.交易产品接口】
        // 5.构造调用组件的输入参数DTO
        if (DictCodeListExt1.交易流程状态_撤销 == 交易流程状态) {
            String tradeId = deal.getTradeId();
            if(tradeId.endsWith("R")){
                tradeId = tradeId.substring(0,tradeId.length()-1);
            }
            if(deal.getTotleTradeId().endsWith("R")){
                deal.setTotleTradeId(deal.getTotleTradeId().substring(0,deal.getTotleTradeId().length()-1));
            }
            deal.setTradeId(tradeId);
            dealAdd.setTradeId(tradeId);
            stlPayOrders.setTradeId(tradeId);
        }
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDtos.add(baseTradeProductDealDto);
        baseTradeProductDealDto.setTrdProductDealAdd(dealAdd);
        baseTradeProductDealDto.setTrdProductDeal(deal);
        baseTradeProductDealDto.setStlPayOrdersList(wflStlPayOrdersDto);
        finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        finProductTradeDto.setWorkflowStatus(交易流程状态.getValue());
        finProductTradeDto.setNeedSum(false);
        baseTradeProductDealCommService.execute(finProductTradeDto);
    }
    private WflStlPayOrders gen划款指令流程(ChannelFeePay002Dto entity, OperationType type,String tradeId,int i) {
        if (type == OperationType.DELETE) {
            List<BussTrdTransferOrderBase> list = bussSettleOrderCommService.selectOrdersByBizCode(tradeId);
            BussTrdTransferOrderBase item = list.get(0);
            if (StringUtil.equals(item.getPs(), DictCodeListExt1.收支方向_支出.getValue())) {
                item.setMyAcctNo(item.getPayAcctNo());
                item.setCounterAcctNo(item.getRecAcctNo());
            }
            else {
                item.setMyAcctNo(item.getRecAcctNo());
                item.setCounterAcctNo(item.getPayAcctNo());
            }
            WflStlPayOrders wflOrder = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflOrder, item);
            wflOrder.setTradeId(wflOrder.getTradeId() + "R");
            wflOrder.setOrderId(wflOrder.getOrderId()+"R");
            wflOrder.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflOrder.setWorkflowId(entity.getWorkflowId());
            return wflOrder;
        }
        WflStlPayOrders dto = new WflStlPayOrders();
        BussTrdTransferOrderDto objDto = new BussTrdTransferOrderDto();
        for(BussTrdTransferOrderDto dto1:entity.getBussTrdTransferOrder()){
            if(dto1.getPortfolioId().equals(entity.getFeeInfoList().get(i).getPortfolioId()))objDto=dto1;
        }
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderImplService.save(new ArrayList<BussTrdTransferOrderDto>(Arrays.asList(objDto)));
        if (stlPayOrdersList != null) {
            BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrdersList.get(0));
            dto.setTradeId(tradeId);
            dto.setWorkflowId(entity.getWorkflowId());
            dto.setStatus(DictCodeList.数据状态_新建中.getValue());
            dto.setCreateDept(entity.getCreateDept());
            dto.setUpdateTime(entity.getUpdateTime());
            dto.setCreateUser(entity.getCreateUser());
            dto.setCreateTime(entity.getCreateTime());
            dto.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            dto.setWorkflowId(entity.getWorkflowId());
            if (type == OperationType.REVOKE) {
                dto.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                dto.setTradeId(dto.getTradeId() + "R");
            }
            else {
                dto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
            }
        }
        return dto;
    }
//    private List<WflStlPayOrders> gen划款指令流程(ChannelFeePay002Dto entity, OperationType type) {
//        if (type == OperationType.DELETE) {
//            List<BussTrdTransferOrderBase> list = bussSettleOrderCommService.selectOrdersByBizCode(
//                    entity.getTradeId());
//            List<WflStlPayOrders> wflStlPayOrdersList = new ArrayList<>();
//            for(int i=0;i<list.size();i++){
//                BussTrdTransferOrderBase item = list.get(i);
//                if (StringUtil.equals(item.getPs(), DictCodeListExt1.收支方向_支出.getValue())) {
//                    item.setMyAcctNo(item.getPayAcctNo());
//                    item.setCounterAcctNo(item.getRecAcctNo());
//                }
//                else {
//                    item.setMyAcctNo(item.getRecAcctNo());
//                    item.setCounterAcctNo(item.getPayAcctNo());
//                }
//                WflStlPayOrders wflOrder = new WflStlPayOrders();
//                BeanUtil.copyPropertiesIgnoreNull(wflOrder, item);
//                wflOrder.setTradeId(wflOrder.getTradeId() + "R");
//                wflOrder.setOrderId(wflOrder.getOrderId()+"R");
//                wflOrder.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//                wflOrder.setWorkflowId(entity.getWorkflowId());
//                wflStlPayOrdersList.add(wflOrder);
//            }
//            return wflStlPayOrdersList;
//        }
//        List<WflStlPayOrders> stlPayOrdersDtos = new ArrayList<>();
//        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderImplService.save(entity.getBussTrdTransferOrder());
//        if (stlPayOrdersList != null) {
//            stlPayOrdersList.forEach(stlPayOrders -> {
//                WflStlPayOrders dto = new WflStlPayOrders();
//                BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrders);
//                dto.setTradeId(entity.getTradeId());
//                dto.setWorkflowId(entity.getWorkflowId());
//                dto.setStatus(DictCodeList.数据状态_新建中.getValue());
//                dto.setCreateDept(entity.getCreateDept());
//                dto.setUpdateTime(entity.getUpdateTime());
//                dto.setCreateUser(entity.getCreateUser());
//                dto.setCreateTime(entity.getCreateTime());
//                dto.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
//                dto.setWorkflowId(entity.getWorkflowId());
//                if (type == OperationType.REVOKE) {
//                    dto.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
//                    dto.setTradeId(dto.getTradeId() + "R");
//                }
//                else {
//                    dto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
//                }
//                stlPayOrdersDtos.add(dto);
//            });
//        }
//        return stlPayOrdersDtos;
//    }

    @Override
    public void doUpdate(ChannelFeePay002Dto entity) {
        if (!StringUtil.equals("revoke", entity.getOperateType())
                && !StringUtil.equals("delete", entity.getOperateType())) {
            // 先删除流程中数据
            this.doDelete(entity.getWorkflowId());

            // 重新insert
            this.doInsert(entity, OperationType.NEW);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
    }


    @Override
    public void doComplete(ChannelFeePay002Dto entity, OperationType operationType) {
        String tradeId = entity.getTradeId();
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(
                OperationType.DELETE)) {
            entity.setTradeId(entity.getTradeId() + "R");
        }
        ProductTradeInfoDto allTrade = restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/ChannelFeePay/findAllTrade", entity.getTradeId(),
            ProductTradeInfoDto.class);

        // 插入业务表
        saveDataWhenCompleteWorkflow(allTrade, entity,
            operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE));
        // 删除工作流表
        baseTradeProductDealCommService.deleteWflData(entity.getWorkflowId());
        StlPayOrdersDto ordersDto = new StlPayOrdersDto();
        for(int i=0;i<allTrade.getTrdProductDeals().size();i++){
            BeanUtil.copyPropertiesIgnoreNull(ordersDto, allTrade.getStlPayOrders().get(i));
            execute共通(allTrade.getTrdProductDealAdds().get(i), allTrade.getTrdProductDeals().get(i), ordersDto,
                    operationType.equals(OperationType.REVOKE) || operationType.equals(
                            OperationType.DELETE) ? DictCodeListExt1.交易流程状态_撤销 : DictCodeListExt1.交易流程状态_有效,entity,i);

            if (!operationType.equals(OperationType.DELETE) && !operationType.equals(OperationType.REVOKE)) {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(allTrade.getTrdProductDealAdds().get(i).getPortfolioId());
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                inputDto.setTrdProductDeal(allTrade.getTrdProductDeals().get(i));
                inputDto.setTrdProductDealAdd(allTrade.getTrdProductDealAdds().get(i));
                inputDto.setPtlTradeList(allTrade.getPtlTrades());
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            }
        }
    }

    private void saveDataWhenCompleteWorkflow(ProductTradeInfoDto allTrade, ChannelFeePay002Dto entity, boolean isRevoke) {
        BigDecimal tradeAmt = new BigDecimal(0);
        for(int i=0;i<allTrade.getTrdProductDeals().size();i++) {
            TrdProductDeal deal = allTrade.getTrdProductDeals().get(i);
            TrdProductDealAdd dealAdd = allTrade.getTrdProductDealAdds().get(i);
            PaTrdProductDeal paDeal = allTrade.getPaTrdProductDeals().get(i);

            PtlTrade ptlTrade = new PtlTrade();
            ptlTrade.setPortfolioId(dealAdd.getPortfolioId());
            ptlTrade.setBusiNo(deal.getTradeId());
            ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade.setBusiId(deal.getChlId());
            ptlTrade.setBusiType(DictCodeList.业务类型_产品费用交易.getValue());
            ptlTrade.setSettleDate(deal.getSettleDate());
            paTrdProductDealMapper.insertSelective(paDeal);
//            if (isRevoke) {
//                ptlTradeMapper.deleteByPrimaryKey(ptlTrade);
//            bussCommonMapperDelegateService.insertAutoClassifyBatch(paDeal);
//            } else {
//            bussCommonMapperDelegateService.insertAutoClassifyBatch(paDeal, ptlTrade);
            ptlTradeMapper.insertSelective(ptlTrade);
//            }
            tradeAmt = tradeAmt.add(deal.getTradeAmt());
        }
        TrdFinProductDealTotal dealTotal = new TrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(dealTotal, allTrade.getTrdProductDeals().get(0));
        dealTotal.setTradeAmt(tradeAmt);
        if (MultilevelDataAuthorityUtil.isEnabled()) {
            dealTotal.setInstitutionCode(MultilevelDataAuthorityUtil.getOrganizationCode(entity.getCreateUser()));
        }
        String totleTradeId = allTrade.getTrdProductDeals().get(0).getTotleTradeId();
        TrdFinProductDealTotal dealTotal0 = new TrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(dealTotal0, dealTotal);
        if(isRevoke){
            totleTradeId = totleTradeId.endsWith("R")?totleTradeId.substring(0,totleTradeId.length()-1):totleTradeId;
            dealTotal.setpTradeId(totleTradeId);

            dealTotal0.setTotleTradeId(totleTradeId);
            dealTotal0.setIsCancel("Y");
            trdFinProductDealTotalMapper.updateByPrimaryKeySelective(dealTotal0);
        }
        trdFinProductDealTotalMapper.insertSelective(dealTotal);

    }

    public ChannelFeePay002Dto searchTheoryAmt(Map<String, Object> pageParam) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/ChannelFeePay/searchTheoryAmt", pageParam,
            ChannelFeePay002Dto.class);
    }

    public ChannelFeePay002Dto getEffectiveData(String tradeId) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/ChannelFeePay/getEffectiveData", tradeId,
                ChannelFeePay002Dto.class);
    }

    public String findChannelFeePaied(String finprodId, Date mdate, String portfolioId) {
        Map<String, Object> pageParam = new HashMap<>();
        pageParam.put("finprodId", finprodId);
        pageParam.put("mdate", mdate);
        pageParam.put("portfolioId", portfolioId);
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/ChannelFeePay/findChannelFeePaied", pageParam,
                String.class);
    }

	public String getCollectType(String finprodMarketId) {
    	Map<String, Object> pageParam = new HashMap<>();
    	pageParam.put("finprodMarketId", finprodMarketId);
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/ChannelFeePay/getCollectType", pageParam, String.class);
    }
    public List<BussTrdTransferOrderDto> selectPayOrders(String bizCode) {
        return restTemplate.postForList(ServiceApplication.SETTLE_SERVICE_NAME + "/BussSettleOrderComm/selectOrdersByBizCode",
                bizCode, BussTrdTransferOrderDto.class);
    }

    public String findPortfolioName(String portfolioId) {
        return restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/ChannelFeePay/findPortfolioName?portfolioId="+portfolioId, String.class);
    }

    @Override
    public void doBack(ChannelFeePay002Dto entity) {
        List<String> tradeIdList = restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/ChannelFeePay/getTradeId1?totleTradeId=" + entity.getTradeId(),
                List.class);
        for(String tradeId:tradeIdList) {
            AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), tradeId);
        }
    }

    public int queryTrade(String totleTradeId) {
        return restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME + "/ChannelFeePay/queryTrade?totleTradeId=" + totleTradeId,
                int.class);
    }
}
