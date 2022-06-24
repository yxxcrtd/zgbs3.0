package com.joyintech.tams.invest.trade.bondtostock.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.PaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductBarterMapper;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessJixuConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.valuationinterf.ValuationInterfService;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.bondtostock.dao.TrdBondToStockMapper;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.ExchangeTransMatchCommonService;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.trade.bondtostock.dto.TrdBondToStock001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 可转债转股
 *
 * @author 丁志鹏
 * @version 1.0 2019年11月14日
 * @see TrdBondToStock002Service 可转债转股管理画面001
 * @since 1.0
 */
@Service
public class TrdBondToStock002Service extends BaseService<TrdBondToStock001Dto> implements BaseProcessService<TrdBondToStock001Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdBondToStock002Service.class);

    /**
     * MultiApproveTradeCheckService
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;
    /**
     * 划款指令
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    /**
     * 可转债转股Dao
     */
    @Autowired
    private TrdBondToStockMapper trdBondToStockMapper;
    /**
     * 业务编号共通
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;
    /**
     * 金融产品交易共通业务
     */
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommService;
    /**
     * 金融产品交易共通业务
     */
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
    /**
     * 金融产品交易共通业务
     */
    @Autowired
    private TrdCommonBaseService trdCommonBaseService;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    /**
     *
     */
    @Autowired
    private TrdFinProductBarterMapper trdFinProductBarterMapper;
    /**
     * 估值接口
     */
    @Autowired
    private ValuationInterfService valuationInterfService;
    /**
     * 投资委托信息Service
     */
    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;
    
//    @Autowired
//    private DealCommonService dealCommonService;
//
//    /**
//     * InterfCommonService
//     */
//    @Autowired
//    private InterfCommonService interfCommonService;

    // 调用投管接口 add by renping 20190205 start
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    // 调用投管接口 add by renping 20190205 end

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;
    
    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;
    
    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;

    @Autowired
    private ExchangeTransMatchCommonService exchangeTransMatchCommonService;
    
    /**
     * 获取业务编号
     * @return 业务编号
     */
    public String gen业务编号(){
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_可转债转股交易));
            return baseGetBusinessId.buildBussinessCode(codeParameter);
        }
        else {
            return IDGenerator.generate();
        }
    }
    @Override
    @DataSource(DataSourceType.MASERT)
    @DTXTransaction
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void doInsert(TrdBondToStock001Dto entity, OperationType type) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && StringUtil.isNotEmpty(entity.getBusiCode())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getBusiCode());
        }
    // 新建交易
        if (!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
//            直接提交时无工作流代码
            TrdCommonBaseService.setWorkflowId(entity.getBaseTotalTradeInfoDto(),entity.getWorkflowId());
            handleBussFlowData(entity);
        }
        // 撤销交易
        else {
            this.doRevokeInsert(entity);
        }
    }
    @DataSource(DataSourceType.MASERT)
    @DTXTransaction
    public void handleBussFlowData(TrdBondToStock001Dto entity) {
        if (entity.getBussTrdTransferOrder()!=null && entity.getBussTrdTransferOrder().size()>0&&StringUtil.isNotEmpty(entity.getBussTrdTransferOrder().get(0))) {
            entity.getBussTrdTransferOrder().get(0).setWorkflowId(entity.getWorkflowId());
        }
        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }
        if(StringUtil.isNotEmpty(entity.getWorkflowId())){
            trdCommonBaseService.deleteByWorkflowId(entity.getWorkflowId());
        }
//        入库共通
        trdCommonBaseService.doInsert(entity.getBaseTotalTradeInfoDto());

        //提交占用授信
        if (StringUtil.equals("submit", entity.getOperateType())) {
                TrdProductDeal 股票买入deal = gen股票买入交易(entity);
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                股票买入deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                inputDto.setTrdProductDeal(股票买入deal);
                AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
        }
        // 调用授信共同 sjj end


//        BaseTradeInfoDto baseDto = entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0);

//        TrdProductDeal comDdeal = new TrdProductDeal();
//        BeanUtil.copyPropertiesIgnoreNull(comDdeal,baseDto.getTrdProductDeal());
//        TrdProductDealAdd comDdealAdd = new TrdProductDealAdd();
//        BeanUtil.copyPropertiesIgnoreNull(comDdealAdd,baseDto.getTrdProductDealAdd());
//        TrdProductDealAdd comDdealAdd = new TrdProductDealAdd();
//        BeanUtil.copyPropertiesIgnoreNull(comDdealAdd,baseDto.getTrdProductDealAdd());

//        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//        inputDto.setProdid(entity.getPortfolioId());
//        inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//        comDdeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
////        设置交易
//        inputDto.setTrdProductDeal(comDdeal);
//        inputDto.setTrdProductDealAdd(comDdealAdd);
////        inputDto.setRepoType(type6);
//        List<PtlTrade> ptlTradeList = baseDto.getPtlTrade();
//        String actPortfolioId = entity.getActPortfolioId();
//        inputDto.setPtlTradeList(ptlTradeList);
//        AllTradeCommonUtil.handleAfterTradeComplete(inputDto);


        // 调用授信共同 sjj end
        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        TrdProductDeal dealCom = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(dealCom,entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0).getTrdProductDeal());
        baseTradeProductDealDto.setTrdProductDeal(dealCom);
        TrdProductDealAdd comAdd = new TrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(comAdd,entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0).getTrdProductDealAdd());
        baseTradeProductDealDto.setTrdProductDealAdd(comAdd);
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            baseTradeProductDealDto.setStlPayOrdersList(
                    Arrays.asList(entity.getBussTrdTransferOrder().get(0)));
        }
        //        委托代码不为空设置委托编号
        if(StringUtil.isNotEmpty(entity.getEntrustId())){
            baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
        }
        finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
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
    @DataSource(DataSourceType.MASERT)
    @DTXTransaction
    public void doRevokeInsert(TrdBondToStock001Dto entity) {
        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondDuration/doRevoke", entity,
                String.class);
    }
    @DataSource(DataSourceType.MASERT)
    @DTXTransaction
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void doUpdate(TrdBondToStock001Dto entity) {
        if (!StringUtil.equals("revoke", entity.getOperateType())
                && !StringUtil.equals("delete", entity.getOperateType())) {
            if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                    && StringUtil.isNotEmpty(entity.getBusiCode())) {
                // 将附件和当前业务对象绑定（更新附件的业务代码）
                fileAttachmentProcess.updateBussinessNo(
                        entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                                Collectors.toList()),
                        entity.getBusiCode());
            }
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            prepareEntity(entity);
            entity.setPortfolioId(entity.getActPortfolioId());
            // 先删除流程中数据
            doDelete(entity.getWorkflowId());
            // 重新insert
            handleBussFlowData(entity);
            ProcessMark newEntity = null;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdBondToStock001Dto.class, true);
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
        }
    }
    @DataSource(DataSourceType.MASERT)
    @DTXTransaction
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void doComplete(TrdBondToStock001Dto entity, OperationType type) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
//        this.prepareEntity(entity);
        if (!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setNeedSum(true);
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal,entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0).getTrdProductDeal());
            baseTradeProductDealDto.setTrdProductDeal(deal); // 金融产品交易
            TrdProductDealAdd dealAdd = new TrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(dealAdd,entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0).getTrdProductDealAdd());
            baseTradeProductDealDto.setTrdProductDealAdd(dealAdd); // 金融产品附属
//            金融产品实物交易Entity
            TrdFinProductBarter barterDeal = new TrdFinProductBarter();
            BeanUtil.copyPropertiesIgnoreNull(barterDeal,entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0).getTrdFinProductBarter());
            baseTradeProductDealDto.setTrdFinProductBarterList(Arrays.asList(barterDeal));
//            增加组合交易传值
            baseTradeProductDealDto.setPtlTrade(entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0).getPtlTrade());
            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
            StlPayOrdersDto stlPayOrders = null;
            if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
                stlPayOrders = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,
                        entity.getBussTrdTransferOrder().get(0));
            }
            // 划款指令
            if (stlPayOrders != null) {
                stlPayOrders.setTradeId(entity.getBusiCode());
                baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrders));
            }
            //      调用jixu接口
            sendToValuation(baseTradeProductDealDto,type,entity.getActPortfolioId());
            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
            //        委托代码不为空处理委托代码
            if(StringUtil.isNotEmpty(entity.getEntrustId())){
                baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
            }

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
//            //        委托代码不为空设置委托编号
//            if(StringUtil.isNotEmpty(entity.getEntrustId())){
//                baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
//            }
            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommService.execute(finProductTradeDto);
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
            //        委托代码不为空处理委托代码
//          if(StringUtil.isNotEmpty(entity.getEntrustId())){
//          baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
//          // 调用投资委托组件的更新委托状态接口，将委托状态设置为“已完成”
//          etrInvestInfoServiceImpl.updateDelegateStatus(entity.getEntrustId(),
//                  DictCodeList.委托状态_已完成.getValue());
//      }
            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getBusiCode());
//            交易入生效库
            trdCommonBaseService.doInsertAndDelete(entity.getBaseTotalTradeInfoDto(),entity.getUpdateUser(),entity.getUpdateTime(),entity.getWorkflowId());
            // 一体化对应etliu start
            // 所有交易生效后调用的共通方法

//            BaseTradeInfoDto baseTradeInfoDto = entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0);
//            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//            inputDto.setProdid(entity.getPortfolioId());
//            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//            inputDto.setTrdProductDeal(baseTradeInfoDto.getTrdProductDeal());
//            // <!-- 通道交易 新增 强成西 2019/03/26 start -->
//            inputDto.setTrdProductDealAdd(baseTradeInfoDto.getTrdProductDealAdd());
//            if(baseTradeInfoDto.getTrdTradeFeeDetail() != null && baseTradeInfoDto.getTrdTradeFeeDetail().size()>0){
//                inputDto.setTrdTradeFeeDetail(baseTradeInfoDto.getTrdTradeFeeDetail().get(0));
//            }
//            // <!-- 通道交易 新增 强成西 2019/03/26 end -->
//            // 场内基金改造 add by renping 20191117 start
//            if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
//                inputDto.setStlPayOrders(entity.getBussTrdTransferOrder().get(0));
//            }
//            // 场内基金改造 add by renping 20191117 end
//            // 反向交易增加 start by qiangchengxi 2019年3月12日
//            AllTradeCommonUtil.setDeals(inputDto, baseTradeInfoDto.getTrdProductDeal(), baseTradeInfoDto.getTrdProductDealAdd(), baseTradeInfoDto.getPaTrdProductDeal(),
//                    entity.getBussTrdTransferOrder(), baseTradeInfoDto.getPtlTrade(), entity.getActPortfolioId());
//            // 反向交易增加 end by qiangchengxi 2019年3月12日
//
//            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
//                inputDto.setActPortfolioId(entity.getActPortfolioId());
//            }
//            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
//            // 一体化对应etliu end

            //成交录单---调用共通
            if(StringUtil.isNotEmpty(entity.getDealSqeId())) {
                exchangeTransMatchCommonService.transactionRecord(entity.getDealSqeId(),StringUtil.isNotEmpty(entity.getTotleTradeId())?entity.getTotleTradeId():deal.getTradeId(),OperationType.NEW);
            }
        }
        // 撤销
        else {
            this.doCompleteRevoke(entity);
        }
     // 调接口 by 赵东东 20191206 start
      List<String> tradeIds = new ArrayList<String>();
      tradeIds.add(entity.getBusiCode());
      try {
      restTemplate.postForObject(
      		ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
      		Map.class);
      }catch(Exception e) {
    	  
      }
      // 调接口 by 赵东东 20191206  end
    }

    /**
     * 发送估值
     * @param baseDto 基础dto
     * @param operationType 操作类型
     * @param actPortfolioId 实际交易组合
     */
    @DataSource(DataSourceType.MASERT)
    @DTXTransaction
    public void sendToValuation(BaseTradeProductDealDto baseDto,OperationType operationType,String actPortfolioId){
        // jixu接口 start
        TrdCommDto trdCommDto = new TrdCommDto();
        if (OperationType.DELETE == operationType||OperationType.REVOKE == operationType) {
            trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_DELETE);;
        }
        else if (OperationType.NEW == operationType) {
            trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_ADD);
        }
        else {
            trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_UPDATE);
        }
        trdCommDto.setHeader(BussinessJixuConstant.消息头_股票交易_买卖);
        TrdProductDeal comDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(comDeal,baseDto.getTrdProductDeal());
        //add by  wangpeng 传入jixu 需要原交易编号，jixu根据此删除数据
        TrdProductDeal comDeal1 = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(comDeal1,baseDto.getTrdProductDeal());
        if (OperationType.DELETE == operationType||OperationType.REVOKE == operationType) {
        	comDeal1.setTradeId(comDeal1.getpTradeId());
        }
        trdCommDto.setTrdProductDeal(comDeal1);
        //add by wangpeng
        TrdProductDealAdd comDealAdd = new TrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(comDealAdd,baseDto.getTrdProductDealAdd());
        trdCommDto.setPtlTradeList(baseDto.getPtlTrade());
        trdCommDto.setTrdProductDealAdd(comDealAdd);
        trdCommDto.setTradeFeeDetaillist(baseDto.getTrdTradeFeeDetailList());
        TrdFinProductBarter comBarter = new TrdFinProductBarter();
        BeanUtil.copyPropertiesIgnoreNull(comBarter,baseDto.getTrdFinProductBarterList().get(0));
        trdCommDto.setTrdFinProductBarter(comBarter);
        if(baseDto.getStlPayOrdersList()!=null){
            List<StlPayOrders> stlPayOrdersList = new ArrayList<>(baseDto.getStlPayOrdersList());
            trdCommDto.setStlPayOrdersList(stlPayOrdersList);
        }
//        valuationInterfService.runValInvest2Jixu(trdCommDto);
//        重新的初始化dto 防止被jixu 修改
        BeanUtil.copyPropertiesIgnoreNull(comDeal,baseDto.getTrdProductDeal());
        BeanUtil.copyPropertiesIgnoreNull(comDealAdd,baseDto.getTrdProductDealAdd());
        comDeal.setPrinAmt(ArithmeticUtil.multiply(comDeal.getShareAmt(),new BigDecimal(100)));
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        inputDto.setProdid(baseDto.getTrdProductDealAdd().getPortfolioId());
        inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
        inputDto.setTrdProductDeal(comDeal);
        inputDto.setTrdProductDealAdd(comDealAdd);
//        增加实物交易
        inputDto.setTrdFinProductBarter(comBarter);
//        增加组合交易数据
        inputDto.setPtlTradeList(baseDto.getPtlTrade());
        if(baseDto.getTrdTradeFeeDetailList() != null && baseDto.getTrdTradeFeeDetailList().size()>0){
            inputDto.setTrdTradeFeeDetail(baseDto.getTrdTradeFeeDetailList().get(0));
        }
        // <!-- 通道交易 新增 强成西 2019/03/26 end -->
        // 场内基金改造 add by renping 20191117 start
        if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
            inputDto.setStlPayOrders(baseDto.getStlPayOrdersList().get(0));
        }
        // 场内基金改造 add by renping 20191117 end
        // 反向交易增加 start by qiangchengxi 2019年3月12日
//        AllTradeCommonUtil.setDeals(inputDto, comDeal, comDealAdd, baseDto.getPaTrdProductDeal(),
//                baseDto.getStlPayOrdersList(), baseDto.getPtlTrade(), actPortfolioId);
        // 反向交易增加 end by qiangchengxi 2019年3月12日
        if (actPortfolioId != null && !actPortfolioId.equals(baseDto.getTrdProductDealAdd().getPortfolioId())) {
            inputDto.setActPortfolioId(actPortfolioId);
        }
        AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

        // 调用投管接口 add by renping 20190205 start
        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        investInterfCommDto.setPageId("TrdBondToStock002");
        if (OperationType.REVOKE == operationType) {
            investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
        }
        else {
            investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
        }
        investInterfCommDto.setPortfolioId(actPortfolioId);
        investInterfCommDto.setTrdProductDeal(inputDto.getTrdProductDeal());
        investInterfCommDto.setTrdProductDealAdd(inputDto.getTrdProductDealAdd());
        investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
        investInterfCommDto.setTradeFeeDetaillist(baseDto.getTrdTradeFeeDetailList());
        investInterfCommDto.setTrdFinProductBarter(comBarter);
        investInterfCommService.doInvestInterf(investInterfCommDto);
        // 调用投管接口 add by renping 20190205 end
    }
    @DataSource(DataSourceType.MASERT)
    @DTXTransaction
    public void doCompleteRevoke(TrdBondToStock001Dto entity) {
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        tradeDto.setWorkflowStatus(DictCodeListExt1.交易状态_撤销.getValue());
        tradeDto.setWorkflowId(entity.getWorkflowId());
        //        prepareEntity(entity);
        List<BaseTradeProductDealDto> baseTradeProductDealDtos1 = trdCommonBaseService.findEffectiveDeal(entity.getBusiCode(),DictCodeListExt1.交易状态_撤销);
        for (BaseTradeProductDealDto baseTradeProductDealDto : baseTradeProductDealDtos1) {
        	//设置委托id
        	if(StringUtil.isNotEmpty(entity.getEntrustId())){
        		baseTradeProductDealDto.getTrdProductDealAdd().setEntrustId(entity.getEntrustId());
        	}
        	if(!StringUtil.equals(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId(),entity.getPortfolioId())){
                baseTradeProductDealDtos1.remove(baseTradeProductDealDto);
                continue;
            }
            TrdProductDeal deal1 = baseTradeProductDealDto.getTrdProductDeal();
            TrdFinProductBarter key = new TrdFinProductBarter();
            key.setTradeId(deal1.getTradeId());
            key.setFinprodId(entity.getShareFinprodId());
            key.setFinprodType(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
            TrdFinProductBarter barter = trdFinProductBarterMapper.selectByPrimaryKey(key);
            baseTradeProductDealDto.setTrdFinProductBarterList(Arrays.asList(barter));
//            deal1.setTradeId(entity.getBusiCode());
            deal1.setCreateUser(tradeDto.getCreateUser());
            deal1.setCreateTime(tradeDto.getCreateTime());
            deal1.setCreateDept(tradeDto.getCreateDept());
            deal1.setUpdateTime(tradeDto.getUpdateTime());
            deal1.setUpdateUser(tradeDto.getUpdateUser());
            deal1.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            String oldTradeId = deal1.getTradeId();
            String newTradeId = deal1.getTradeId()+'R';
            deal1.setTradeId(newTradeId);
            barter.setTradeId(newTradeId);
            deal1.setpTradeId(oldTradeId);
            if(baseTradeProductDealDto.getTrdProductDealAdd()!=null){
                baseTradeProductDealDto.getTrdProductDealAdd().setTradeId(newTradeId);
            }
            if(baseTradeProductDealDto.getStlPayOrdersList()!=null && baseTradeProductDealDto.getStlPayOrdersList().size()>0){
                baseTradeProductDealDto.getStlPayOrdersList().forEach(
                        x->{
                            x.setTradeId(x.getTradeId()+'R');
                        }
                );
            }
            else if(entity.getBaseTotalTradeInfoDto()!=null && entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0)!=null &&
                    entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0).getStlPayOrders()!= null){
                StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto,entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0).getStlPayOrders());
                stlPayOrdersDto.setTradeId(stlPayOrdersDto.getTradeId()+'R');
                baseTradeProductDealDto.getStlPayOrdersList().add(stlPayOrdersDto);
            }
            List<PtlTrade> ptlList = trdCommonBaseService.findPtlTradeByBusiNo(oldTradeId);
            if(ptlList!= null && ptlList.size()>0){
                ptlList.forEach(item->{
                    item.setBusiNo(item.getBusiNo()+'R');
                });
                baseTradeProductDealDto.setPtlTrade(entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0).getPtlTrade());
            }
            //      调用jixu接口
            sendToValuation(baseTradeProductDealDto,OperationType.REVOKE,entity.getActPortfolioId());
//            释放股票占用的授信（债券的上面的已经释放）
            isNeedOccupy(entity, true, true, true);
        }
        tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos1);
        // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
        baseTradeProductDealCommService.execute(tradeDto);
        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            tradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
            baseTradeProductDealCommService.insertPosition(tradeDto);
            tradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
        }
        //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getBusiCode()+'R');
        trdCommonBaseService.revoke汇总和交易相关表(entity.getBusiCode(),
                entity.getUpdateUser(), entity.getRevokeReason(), null, entity.getActPortfolioId());
//        删除流程数据
        doDelete(entity.getWorkflowId());

        String tradeId = exchangeTransMatchCommonService.getTradeIdByTotalTradeId(entity.getTotleTradeId());
        if(StringUtil.isNotEmpty(tradeId)){
            entity.setTotleTradeId(tradeId);
        }
        //成交录单---调用共通
        exchangeTransMatchCommonService.transactionRecord(entity.getDealSqeId(),entity.getTotleTradeId(),OperationType.REVOKE);

    }

    @Override
    @DataSource(DataSourceType.MASERT)
    @DTXTransaction
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void doBack(TrdBondToStock001Dto entity) {
        //  根据交易编号，删除可用，质押，融入，融出，在途FLOW持仓
        String tradeId = entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0).getTrdProductDeal().getTradeId();
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), tradeId);
        isNeedOccupy(entity, true, true, true);
    }

    @Override
    @DataSource(DataSourceType.MASERT)
    @DTXTransaction
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void doRevoke(TrdBondToStock001Dto entity) {
        //  根据交易编号，删除可用，质押，融入，融出，在途FLOW持仓
        String tradeId = entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0).getTrdProductDeal().getTradeId();
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), tradeId);
        isNeedOccupy(entity, true, true, true);
    }
    private TrdProductDeal gen股票买入交易(TrdBondToStock001Dto entity){
        TrdFinProductBarter barterDeal = entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0).getTrdFinProductBarter();
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());//交易状态
        trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());//交易方向
        if(StringUtils.isNotBlank(entity.getfTradeId())){
            trdProductDeal.setfTradeId(entity.getfTradeId());
        }
//        股票占用金额
        trdProductDeal.setPrinAmt(ArithmeticUtil.multiply(entity.getFirstUnitCprice3(),entity.getConNum()));
        trdProductDeal.setTradeId(entity.getBusiCode());//交易编号
        trdProductDeal.setFinprodId(barterDeal.getFinprodId());//金融产品代码
        trdProductDeal.setCreateDept(entity.getCreateDept());//创建部门
        trdProductDeal.setCreateUser(entity.getCreateUser());//创建人
        trdProductDeal.setCreateTime(entity.getCreateTime());//创建时间
        trdProductDeal.setUpdateUser(entity.getUpdateUser());//更新人
        trdProductDeal.setUpdateTime(entity.getUpdateTime());//更新时间
        trdProductDeal.setTradeDate(entity.getTradeDate());//交易日期
        trdProductDeal.setShareAmt(barterDeal.getShareAmt());//交易份额，债券数量等
        trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());//金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());//金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
        TrdInvestPersonInfo002Dto personInfo = entity.getTrdInvestPersonInfo();
        if (personInfo != null) {
            // 交易对手
            trdProductDeal.setCounterId(personInfo.getCounterId());//交易对手编号
        }
        trdProductDeal.setTradeAmt(entity.getTradeAmt());
        return trdProductDeal;
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
    @DataSource(DataSourceType.MASERT)
    @DTXTransaction
    public void isNeedOccupy(TrdBondToStock001Dto entity, boolean isNotBottomflag, boolean isSave, Boolean isdeleteflag) {
        // 所有交易生效后调用的共通方法(入账信息)  撤销后释放占用的授信额度  Add 20190828 by xkni
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        inputDto.setProdid(entity.getPortfolioId());
        inputDto.setProdFinprodType(DictCodeListExt2.共通业务类型_股票信息.getValue());
        inputDto.setTrdProductDeal(gen股票买入交易(entity));
        CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
        ceLinesUpdate.isNeedOccupyCre(inputDto, isNotBottomflag, isSave, isdeleteflag);
    }
    @Override
    @DataSource(DataSourceType.MASERT)
    @DTXTransaction
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
        //根据工作流代码查询交易相关信息
        WflTrdProductDealAdd wflTrdProductDealAdd = restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME
                    + "/common/trdcommonbase/selectWflTrdProductDealAdd/" + workflowId, WflTrdProductDealAdd.class);
        //删除授信占用
        if (null != wflTrdProductDealAdd && null != wflTrdProductDealAdd.getTradeId()){
            riskCreditListCommonValidatorService.deleteCredit(wflTrdProductDealAdd.getTradeId());
        }
    }

    /**
     * 由画面输入的参数dto，构筑整个需要反映到数据库的实体
     *
     * @param dto 画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void prepareEntity(TrdBondToStock001Dto dto) {
        BaseTotalTradeInfoDto result = new BaseTotalTradeInfoDto();
        dto.setBaseTotalTradeInfoDto(result);
        // 汇总表
        TrdFinProductDealTotal total = gen汇总信息(dto);
        result.setTrdFinProductDealTotal(total);
        // 汇总扩展表
        PaTrdFinProductDealTotal paTotal = gen汇总扩展信息(dto, total);
        result.setPaTrdFinProductDealTotal(paTotal);
        // 汇总附属表
        TrdDealTotalAdd totalAdd = gen汇总附属信息(dto, total);
        result.setTrdDealTotalAdd(totalAdd);
        // 实物汇总
        TrdFinProductBarterTotal barterTotal = gen实物汇总信息(dto, total);
        result.setTrdFinProductBarterTotal(barterTotal);
        List<BaseTradeInfoDto> baseTradeInfoDtoList = new ArrayList<>();
        result.setBaseTradeInfoDtoList(baseTradeInfoDtoList);
        BaseTradeInfoDto baseTradeInfoDto = new BaseTradeInfoDto();
        baseTradeInfoDtoList.add(baseTradeInfoDto);
//        交易
        TrdProductDeal deal = gen交易信息(dto, total);
        baseTradeInfoDto.setTrdProductDeal(deal);
//      交易扩展
        PaTrdProductDeal paDeal = gen交易扩展(dto,deal);
        baseTradeInfoDto.setPaTrdProductDeal(paDeal);
//        交易附属
        TrdProductDealAdd dealAdd = gen交易附属(dto,deal);
        baseTradeInfoDto.setTrdProductDealAdd(dealAdd);
        //实物交易
        TrdFinProductBarter barDeal = gen实物交易(dto,deal);
        baseTradeInfoDto.setTrdFinProductBarter(barDeal);
        //组合交易
        PtlTrade ptlDeal = gen组合交易(dto,deal);
        List<PtlTrade> ptlDealList = new ArrayList<>();
        baseTradeInfoDto.setPtlTrade(ptlDealList);
        ptlDealList.add(ptlDeal);
//        母组合交易
        if(!StringUtil.equals(dto.getPortfolioId(),dto.getActPortfolioId())){
            PtlTrade ptlDealAct = new PtlTrade();
            BeanUtil.copyPropertiesIgnoreNull(ptlDealAct,ptlDeal);
            ptlDealAct.setPortfolioId(dto.getActPortfolioId());
            ptlDealList.add(ptlDealAct);
        }
//      划款指令处理
// 划款指令表Entity 构造开始----------------------------------------------------------
        if (bussTrdTransferOrderService != null) {
            // 调用划款指令共通业务组件的保存处理，根据输出结果加工形成金融产品交易DTO的要素数据
            if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getBusiCode()); // 交易编号
                dto.getBussTrdTransferOrder().get(0).setOrderStatus(
                        DictCodeListExt1.指令状态_新建.getValue()); // 指令状态
                dto.getBussTrdTransferOrder().get(0).setWorkflowId(dto.getWorkflowId()); // 工作流代码
                dto.setBussTrdTransferOrder(
                        bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
                baseTradeInfoDto.setStlPayOrders(gen划款指令流程(dto.getBussTrdTransferOrder(),total,dto.getWorkflowId()));
            }
        }
    }
    /**
     * 一个交易明细一个划款指令
     *
     * @param stlPayOrdersList 划款指令list
     * @param total 汇总交易entity
     * @param workflowId 工作流编号
     * @return 生成结果
     */
    public StlPayOrders gen划款指令流程(List<BussTrdTransferOrderDto> stlPayOrdersList, TrdFinProductDealTotal total,
                                  String workflowId) {

        StlPayOrders stlPayOrders = null;
        for (BussTrdTransferOrderDto dto : stlPayOrdersList) {
            stlPayOrders = dto;
            if(StringUtil.isNotEmpty(stlPayOrders.getSettleAmt())){
                stlPayOrders.setSettleAmt(StringUtil.isNotEmpty(total.getFpriceAmt())?total.getFpriceAmt():total.getTradeAmt());
            }
            stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
            stlPayOrders.setTradeId(total.getTotleTradeId());
            stlPayOrders.setWorkflowId(workflowId);
        }
        return stlPayOrders;
    }
    private PtlTrade gen组合交易(TrdBondToStock001Dto dto, TrdProductDeal deal) {
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId()); // 组合代码
        ptlTrade.setBusiNo(deal.getTradeId()); // 业务编号
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
        ptlTrade.setBusiId(dto.getFinprodId()); // 业务明细代码
        ptlTrade.setBusiType(DictCodeListExt3.共通业务类型_可转债转股交易.getValue()); // 业务类型
        ptlTrade.setInvAim(dto.getInvAim()); // 投资目的
        ptlTrade.setSecManageAcctId(dto.getSecManageAcctId()); // 证券托管户代码
        ptlTrade.setSettleDate(dto.getSettleDate()); // 实际发生日
        ptlTrade.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        return ptlTrade;
    }

    private TrdFinProductBarter gen实物交易(TrdBondToStock001Dto dto, TrdProductDeal deal) {
        /*
        	画面.正股代码对应的金融产品代码：金融产品代码
        	0：分支序号
        	画面.转股数:交易份额
        	画面.转股价：单位净价
        	画面.转股数*画面.转股价（保留2位小数）：交易本金
         */
        TrdFinProductBarter barDeal = new TrdFinProductBarter();
        barDeal.setFinprodType(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        barDeal.setFinprodId(dto.getShareFinprodId());
        barDeal.setTradeId(deal.getTradeId());
        barDeal.setBranch(0l);
        barDeal.setInvAim(dto.getInvAim());
        barDeal.setShareAmt(dto.getConNum());
        barDeal.setUnitCprice(dto.getConPrice());
        barDeal.setPrinAmt(ArithmeticUtil.multiply(dto.getConNum(), dto.getConPrice()));
        barDeal.setWorkflowId(dto.getWorkflowId());
        return barDeal;
    }

    private PaTrdProductDeal gen交易扩展(TrdBondToStock001Dto dto, TrdProductDeal deal) {
//        	债转股：平安业务类型
        PaTrdProductDeal paDeal = new PaTrdProductDeal();
        paDeal.setTradeId(deal.getTradeId());
        paDeal.setPaBussType(DictCodeListExt3.平安业务类型_债转股.getValue());
        paDeal.setWorkflowId(dto.getWorkflowId());
        return paDeal;
    }

    /**
     *  gen交易附属
     * @param dto 业务entity
     * @param deal 交易dto
     * @return 生成结果
     */
    private TrdProductDealAdd gen交易附属(TrdBondToStock001Dto dto, TrdProductDeal deal) {
        /*
        	画面. 零股数量、正股价、转股价值、转股溢价率、纯债溢价率：委托指令附属表.对应同名字段。
         */
        TrdProductDealAdd dealAdd =new TrdProductDealAdd();
        dealAdd.setTradeId(deal.getTradeId());
        if (StringUtil.isNotEmpty(dto.getEntrustId())) {
            dealAdd.setEntrustId(dto.getEntrustId());
        }
        if(StringUtil.isNotEmpty(dto.getChlId())) {
    		String channelLedgerCode = dto.getPortfolioId();
    		//组合信息DTO
//    		PtlPortfolio ptlPortfolio = new PtlPortfolio();
//    		ptlPortfolio = interfCommonService.getPtlPortfolo(channelLedgerCode);
            PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(channelLedgerCode);
    		//IF 数据字典：组合类型.子组合 eq  变量.组合信息DTO.组合类型 OR 数据字典：收益类型.预期收益 eq 变量.组合信息DTO.收益类型 
    		if(DictCodeListExt1.组合类型_子组合.getValue().equals(ptlPortfolio.getPortfolioType()) || 
    				DictCodeListExt2.收益类型_预期收益.getValue().equals(ptlPortfolio.getProfitType())) {
    			//变量.核算通道账套代码  = 变量.核算通道账套代码 + "_" + 变量.通道代码
    			channelLedgerCode = channelLedgerCode + "_" + dto.getChlId();
    		}else {
    			FinProduct finProduct = trdCommonBaseService.queryFinProductByPortfolidId(ptlPortfolio.getPortfolioId());
    			if(finProduct.getFinprodId() != null && finProduct!=null) {
    				//变量.核算通道账套代码 = DTO.金融产品代码
    				channelLedgerCode = finProduct.getFinprodId();
    			}
    			//变量.核算通道账套代码 =变量.核算通道账套代码 + "_" + 变量.通道代码
    			channelLedgerCode = channelLedgerCode+ "_" + dto.getChlId();
    		}
        	 dealAdd.setPortfolioId(channelLedgerCode);	
        }
        else {
        	 dealAdd.setPortfolioId(dto.getPortfolioId());	
        }
        dealAdd.setPreb(dto.getPreb());
        dealAdd.setSpoShareNum(dto.getSpoShareNum());
        dealAdd.setFirstUnitCprice3(dto.getFirstUnitCprice3());
        dealAdd.setExpConValue(dto.getExpConValue());
        dealAdd.setWorkflowId(dto.getWorkflowId());
        return dealAdd;
    }

    private TrdProductDeal gen交易信息(TrdBondToStock001Dto dto, TrdFinProductDealTotal total) {
        /*
        	画面.转债代码 ：金融产品代码
        	画面.转股券面总额/100：交易份额
        	画面.现金到账日：交割日
        	画面.零股折算现金：交易金额
        	零股折算现金：交易金额
        	交易方向：数据字典交易方向.债转股
        	其他字段参看其他交易
        	债转股：投管业务类型
         */
        TrdProductDeal deal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(deal, total);
        deal.setSettleDate(total.getSettleDate()==null?total.getTradeDate():total.getSettleDate());
        deal.setTradeId(gen业务编号());
        deal.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        deal.setWorkflowId(dto.getWorkflowId());
        return deal;
    }

    private TrdFinProductBarterTotal gen实物汇总信息(TrdBondToStock001Dto dto, TrdFinProductDealTotal total) {
        /*
         *  画面.正股代码对应的金融产品代码：金融产品代码  0：分支序号  画面.转股数:交易份额  画面.转股价：单位净价  画面.转股数*画面.转股价（保留2位小数）：交易本金
         */
        TrdFinProductBarterTotal barterTotal = new TrdFinProductBarterTotal();
        barterTotal.setFinprodId(dto.getShareFinprodId());
        barterTotal.setFinprodType(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        barterTotal.setTradeId(total.getTotleTradeId());
        barterTotal.setBranch(0l);
        barterTotal.setInvAim(dto.getInvAim());
        barterTotal.setShareAmt(dto.getConNum());
        barterTotal.setUnitCprice(dto.getConPrice());
        barterTotal.setPrinAmt(ArithmeticUtil.multiply(dto.getConNum(), dto.getConPrice()));
        barterTotal.setWorkflowId(dto.getWorkflowId());
        return barterTotal;
    }
    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        TrdBondToStock001Dto dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        if (taskId != null) {
            try {
//              return commonBusinessService.是否需要上级审批(dto.getCounterId(), dto.getTradeAmt(), LoginUserUtil.getUserName(),
//                      DictCodeList.平安资产类型_债券.getValue(), null, dto.getTradeMarket(), DictCodeList.交易方向_卖出.getValue()
//                      // 资产类型改为授权类型 add by renping 2019-08-06  start
//                      , dto.getFinprodId()
//                      // 资产类型改为授权类型 add by renping 2019-08-06  end
//              );
              
              Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getTradeAmt(), LoginUserUtil.getUserName(),
                  DictCodeList.平安资产类型_债券.getValue(), null, dto.getTradeMarket(), DictCodeList.交易方向_卖出.getValue()
                  , dto.getFinprodId(),entity.getWorkflowId());
              params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
              return commonBusinessService.是否需要上级审批(params);
          }catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        } else {
            return null;
        }
//        return null;
    }
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
    private TrdDealTotalAdd gen汇总附属信息(TrdBondToStock001Dto dto, TrdFinProductDealTotal total) {
        /*
         *  画面. 零股数量、正股价、转股价值、转股溢价率、纯债溢价率：委托指令附属表.对应同名字段。
         */
        TrdDealTotalAdd totalAdd = new TrdDealTotalAdd();
        if (StringUtil.isNotEmpty(dto.getEntrustId())) {
            totalAdd.setEntrustId(dto.getEntrustId());
        }
        totalAdd.setTotleTradeId(total.getTotleTradeId());
        totalAdd.setSpoShareNum(dto.getSpoShareNum());
        totalAdd.setFirstUnitCprice3(dto.getFirstUnitCprice3());
        totalAdd.setExpConValue(dto.getExpConValue());
        totalAdd.setWorkflowId(dto.getWorkflowId());
        totalAdd.setPreb(dto.getPreb());
        totalAdd.setPrec(dto.getPrec());
        return totalAdd;
    }

    private PaTrdFinProductDealTotal gen汇总扩展信息(TrdBondToStock001Dto dto, TrdFinProductDealTotal total) {
        PaTrdFinProductDealTotal paTotal = new PaTrdFinProductDealTotal();
        paTotal.setTotleTradeId(total.getTotleTradeId());
        //  债转股：平安业务类型
        paTotal.setPaBussType(DictCodeListExt3.平安业务类型_债转股.getValue());
        paTotal.setWorkflowId(dto.getWorkflowId());
        return paTotal;
    }

    public TrdFinProductDealTotal gen汇总信息(TrdBondToStock001Dto dto) {
        /*
         *  画面.转债代码 ：金融产品代码  画面.转股券面总额/100：交易份额  画面.现金到账日：交割日  画面.零股折算现金：交易金额  零股折算现金：交易金额  交易方向：数据字典交易方向.债转股  其他字段参看其他交易  债转股：投管业务类型
         */
        TrdFinProductDealTotal total = new TrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(total, dto);
        total.setTotleTradeId(dto.getBusiCode());
        total.setBusiType(DictCodeListExt3.共通业务类型_可转债转股交易.getValue());
        total.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        total.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        total.setPs(DictCodeListExt3.交易方向_债转股.getValue());
        total.setBranch(0l);
        total.setShareAmt(ArithmeticUtil.divide(dto.getShareAmt(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        total.setWorkflowId(dto.getWorkflowId());
        total.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        total.setIsCancel(DictCodeListExt1.是否_否.getValue());
        total.setTgBusiType(DictCodeListExt3.平安业务类型_债转股.getValue());
        total.setPortfolioId(dto.getActPortfolioId());
        total.setUnitCprice(dto.getConPrice());
     // 单位面值
        total.setParValue(trdBondInvestTrade002Service.getUnitPrice(dto.getFinprodId(), dto.getSettleDate()));
        if(dto.getTrdInvestPersonInfo()!=null){
            total.setTrader(dto.getTrdInvestPersonInfo().getTrader());
            total.setCounterId(dto.getTrdInvestPersonInfo().getCounterId());
            total.setCounterTrader(dto.getTrdInvestPersonInfo().getCounterTrader());
            total.setCounterContact(dto.getTrdInvestPersonInfo().getContactInfo());
            total.setIsOutTrade(dto.getTrdInvestPersonInfo().getIsOutTrade());
        }
        return total;
    }

    /**
     * 查询委托信息
     *
     * @param entrustId 委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    public TrdBondToStock001Dto getEntrustInfo(String entrustId) {
//        TrdBondToStock001Dto entity = new TrdBondToStock001Dto();
//        BaseEtrInvestInfoDto baseEntrustInfo =  restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
//                        + "/TrdBondInvestTrade/getEntrustInfo/" + entrustId,
//                null, BaseEtrInvestInfoDto.class);
//        BeanUtil.copyPropertiesIgnoreNull(entity,baseEntrustInfo.getEtrEntrust());
//        BeanUtil.copyPropertiesIgnoreNull(entity,baseEntrustInfo.getEtrEntrustAdd());
        Map<String,Object>map = new HashMap<>();
        map.put("entrustId",entrustId);
        return trdBondToStockMapper.findTradeInfoByEntrustId(map);
    }

    /**
     * 查询交易表的信息数据
     * @param busiCode 交易代码
     * @return 查询的有效交易信息
     */
    public TrdBondToStock001Dto findEffectData(String busiCode) {
        Map<String,Object> map = new HashMap<>();
        TrdBondToStock001Service.setParam(map);
        map.put("busiCode",busiCode);
        return trdBondToStockMapper.findEffectData(map);
    }

    /**
     * 获取债券持仓
     * @param pageParam 画面参数
     * @return
     */
    public Map<String, Object> getPositionInfo(Map<String, Object> pageParam) {
        Map<String, Object> ret = new HashMap<>();
        pageParam.put("hodingType",DictCodeList.持仓类型_实际.getValue());
//        持有
        BigDecimal holdetrPriceTotal = trdBondToStockMapper.getPositionInfo(pageParam);
//        可转
        pageParam.put("hodingType",DictCodeList.持仓类型_可用.getValue());
        BigDecimal conetrPriceTotal = trdBondToStockMapper.getPositionInfo(pageParam);
        ret.put("holdetrPriceTotal",holdetrPriceTotal==null?BigDecimal.ZERO:holdetrPriceTotal);
        ret.put("conetrPriceTotal",conetrPriceTotal==null?BigDecimal.ZERO:conetrPriceTotal);
        return ret;
    }
}
