/*
 * 文件名：TrdRepoTrade002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.cfets.trdquery.service;

import com.joyintech.background.component.uplink.interbank.business.dto.BaseQuoteDto;
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
import com.joyintech.framework.util.DateUtil.DurationType;
import com.joyintech.service.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.cfets.trd.repo.service.CfetsRepoQuotedPrice004Service;
import com.joyintech.tams.cfets.manualsettle.dao.CfetsManualSettleMapper;
import com.joyintech.tams.cfets.manualsettle.dto.PortIntFenPeiInfoDto;
import com.joyintech.tams.cfets.trdconfirm.dao.CfetsTrdConfirmMapper;
import com.joyintech.tams.cfets.trdquery.dao.CfetsTrdQueryMapper;
import com.joyintech.tams.cfets.trdquery.dto.CfetsPledgeInfoResponse;
import com.joyintech.tams.cfets.trdquery.dto.CfetsTrdRepoTrade002Dto;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussRepoSecService;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.component.base.trade.BaseTradeProductDealService;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.BasicTradeFeeInputDto;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.TrdTradeFeeAddRateDetail;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.cstp.service.CstpCommonService;
import com.joyintech.tams.invest.common.dto.*;
import com.joyintech.tams.invest.common.dto.common.*;
import com.joyintech.tams.invest.common.dto.cstp.*;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.dto.PreTrdCommonDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.trade.service.PreTrdCommonBaseService;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;
import com.joyintech.tams.invest.interf.dto.CreditAmtSelDto;
import com.joyintech.tams.invest.interf.service.InterfComponentService;
import com.joyintech.tams.invest.interf.service.InterfComponentService.OrderCaller;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.ExchangeTransMatchCommonService;
import com.joyintech.tams.invest.trade.lend.dto.FinTradeProductDto;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTrade002Dto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService.getSeqNo;

@Service
public class CfetsTrdRepoTrade002Service extends BaseService<CfetsTrdRepoTrade002Dto> implements BaseProcessService<CfetsTrdRepoTrade002Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(CfetsTrdRepoTrade002Service.class);

    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;
    @Autowired
    private CfetsTrdBondInvestTrade002Service cfetsTrdBondInvestTrade002Service;

    @Autowired
    private CfetsRepoQuotedPrice004Service cfetsRepoQuotedPrice004Service;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 平台日期组件接口
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 平安银行算法组件
     */
    @Autowired
    private BussCoreCalculateService bussCoreCalculateService;

    /**
     * DateCalService
     */
    @Autowired
    private DateCalService dateCalService;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * 添加质押券组件Service
     */
    @Autowired(required = false)
    private BussRepoSecService bussRepoSecService;
    
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
     * 交易及存续期交易，保存进待办，点批量审批时候检查service
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;
    @Autowired
    private InterfOpicsTradRepoMapper interfOpicsTradRepoMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    private FinTradeProductAddMapper finTradeProductAddMapper;

    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealOthMapper wflTrdProductDealOthMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired(required = false)
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;

    @Autowired
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;
    @Autowired
    private CfetsTrdConfirmMapper cfetsTrdConfirmMapper;
//    //调用投管接口 add by chenxiaoqiu 20190206 start
//    /**
//     *	 投管接口共通组件
//     */
//    @Autowired
//    private InvestInterfCommService investInterfCommService;
//    @Autowired(required = false)
//    private FinProductMapper finProductMapper;
//    // 调用投管接口 add by chenxiaoqiu 20190206 end

    @Autowired
    private TrdProductDealPreMapper trdProductDealPreMapper;
    @Autowired
    private TrdProductDealAddPreMapper trdProductDealAddPreMapper;
    @Autowired
    private PreTrdCommonBaseService preTrdCommonBaseService;
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;

	/**
	 * 投管接口共通组件
	 */
	@Autowired
	private InvestInterfCommService investInterfCommService;
	
	@Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    @Autowired
    private CstpCommonService cstpCommonService;

    @Autowired
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    private final static String SJXY = "SJXY";//深交所协议回购期限品种

    @Autowired
    private ExchangeTransMatchCommonService exchangeTransMatchCommonService;
    @Autowired
    private BaseTradeProductDealService baseTradeProductDealService;
    @Autowired
    private CfetsTrdQueryMapper cfetsTrdQueryMapper;
    @Autowired
    private TrdTradeFeeTotalMapper trdTradeFeeTotalMapper;

    @Autowired
    private CfetsManualSettleMapper cfetsManualSettleMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    /**
     * 组合可用持仓和实际持仓查询更新
     */
    @Autowired(required = false)
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;
    /**
     * 设置流程业务说明
     */
    @Override
    public String generateTitle(CfetsTrdRepoTrade002Dto entity, OperationType type) {
        return  "交易编号："+entity.getTotleTradeId()+"-"+"交易日期"+ ":" + DateUtil.format(entity.getTradeDate());
    }
	
    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.MASERT)
    @Override
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
     * 工作流doInsert
     *
     * @param entity        entity
     * @param operationType operationType
     */
    @Override
    public void doInsert(CfetsTrdRepoTrade002Dto entity, OperationType operationType) {
        // 新建交易
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
          //  this.handleBussFlowData(entity);
        }
        // 撤销交易
        else {
            this.doInsertRevoke(entity);
        }
    }

    /**
     * 工作流中doUpdate
     *
     * @param input input
     */
    @Override
    public void doUpdate(CfetsTrdRepoTrade002Dto input) {
        if (!StringUtil.equals("revoke", input.getOperateType())
                && !StringUtil.equals("delete", input.getOperateType())) {
            // 先删除流程中数据
         //   this.doDelete(input.getWorkflowId());
             // 重新insert
           // this.handleBussFlowData(input);
        }
    }

    /**
     * 工作流中doDelete
     *
     * @param workflowId workflowId
     */
    @Override
    public void doDelete(String workflowId) {
        // OPCIS表Entity TODO
        baseTradeProductDealCommService.delete(workflowId);
     //   CfetsTrdRepoTrade002Dto entity = this.getBussinessEntityByWorkflowId(workflowId);
       // this.deletePtlSecPositionFlow(entity.getTradeId());
        
//        //根据工作流代码查询交易相关信息
//        WflTrdProductDealAdd wflTrdProductDealAdd = restTemplate.getForObject(
//            ServiceApplication.TRADE_SERVICE_NAME
//                    + "/common/trdcommonbase/selectWflTrdProductDealAdd/" + workflowId, WflTrdProductDealAdd.class);
//        //删除授信占用
//        if (null != wflTrdProductDealAdd && null != wflTrdProductDealAdd.getTradeId()){
//            riskCreditListCommonValidatorService.deleteCredit(wflTrdProductDealAdd.getTradeId());
//        }
    }

    @DataSource(DataSourceType.OTHER)
    @DTXTransaction
    public void uodateCstpMessage(String workflowId){
        AllTradeCommonInputDto inputDto = restTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME
                        + "/common/trdcommonbase/selectWflTrdProductDeal/" + workflowId, AllTradeCommonInputDto.class);
        //处理cstp交易情况
        String fTradeId = inputDto.getTrdProductDeal().getfTradeId();
        if(StringUtils.isNotBlank(fTradeId)){
            InterfOpicsTradRepo interfOpicsTradRepo = interfOpicsTradRepoMapper.selectByPrimaryKey(fTradeId);
            if(interfOpicsTradRepo!=null){
                InterfOpicsTradRepo record = new InterfOpicsTradRepo();
                record.setDealno(fTradeId);
                record.setTradeid(fTradeId);
                BigDecimal prinamt = interfOpicsTradRepo.getRepoamt();
                BigDecimal subtract = com.joyintech.calculation.util.ArithmeticUtil.doAdd(prinamt,
                        inputDto.getTrdProductDeal().getPrinAmt());
                if(subtract.compareTo(BigDecimal.ZERO)==-1||subtract.compareTo(BigDecimal.ZERO)==0){//小于0的话
                    record.setRepoamt(BigDecimal.ZERO);
                }else{
                    record.setRepoamt(subtract);
                }
                interfOpicsTradRepoMapper.updateByPrimaryKeySelective(record);
            }
        }
    }
    /**
     * 工作流审核完成doComplete
     *
     * @param entity         entity
     * @param operationType operationType
     */
    @Override
    public void doComplete(CfetsTrdRepoTrade002Dto entity, OperationType operationType) {
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {

        }
        // 撤销
        else {
            this.doRevokeComplete(entity,"",true);
            
            // 发送Murex接口处理
//            this.sendMurex(input, true);
            //成交录单---调用共通
           //exchangeTransMatchCommonService.transactionRecord(entity.getDealSqeId(),entity.getTradeId(),OperationType.REVOKE);

        }
        
        // 7.2删除原业务流程表中的交易记录
        String workflowId = entity.getWorkflowId();
   /*     if (StringUtil.isBlank(workflowId)) {
            WflTrdProductDealKey wflTrdProductDealKey = new WflTrdProductDealKey();
            wflTrdProductDealKey.setTradeId(entity.getTradeId());
            wflTrdProductDealKey.setWorkflowStatus(
                DictCodeListExt1.交易流程状态_流程中.getValue());
            WflTrdProductDeal wflTrdProductDeal = bussCommonMapperDelegateService.selectByMultiPrimaryKey(
                WflTrdProductDealMapper.class, wflTrdProductDealKey, WflTrdProductDeal.class);

            if (wflTrdProductDeal != null) {
                workflowId = wflTrdProductDeal.getWorkflowId();
            }
        }*/
        this.deleteBussFlowData(workflowId);
        // 增加调用接口 by 赵东东 start 20191206
        try {
        List<String> tradeIds = new ArrayList<String>();
        tradeIds.add(entity.getTradeId());
        restTemplate.postForObject(
        		ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
        		Map.class);
        }catch(Exception e) {
        	
        }
     // 增加调用接口 by 赵东东 end 20191206
    }


    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(TrdRepoTrade002Dto entity) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && StringUtil.isNotEmpty(entity.getTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getTradeId());
        }

        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }

        // 构造Entity
        this.prepareEntity(entity);
        
        // 调用授信共同 sjj start
        TrdProductDeal TrdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity.getTrdProductDeal());
        TrdProductDeal.setInputValue1(entity.getInputValue1());
        TrdProductDeal.setInputValue2(entity.getInputValue2());
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        inputDto.setRepoType(entity.getRepoType());
        inputDto.setTrdProductDeal(TrdProductDeal);
        AllTradeCommonUtil.handleBeforeTradeComplete(inputDto,false);
        // 调用授信共同 sjj end

        // 金融产品交易业务流程信息
        entity.getWflTrdProductDeal().setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        // 金融产品交易附属业务流程表
        entity.getWflTrdProductDealAdd().setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        // 金融产品交易扩展业务流程表
        entity.getWflPaTrdProductDeal().setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        // 金融产品交易业务流程其他信息
        entity.getWflTrdProductDealOth().setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//        转换
        wflTrdProductDealMapper.insertSelective(entity.getWflTrdProductDeal());
        wflTrdProductDealAddMapper.insertSelective(entity.getWflTrdProductDealAdd());
        wflPaTrdProductDealMapper.insertSelective(entity.getWflPaTrdProductDeal());
        wflTrdProductDealOthMapper.insertSelective(entity.getWflTrdProductDealOth());
        // 批量插入
//        bussCommonMapperDelegateService.insertBatch(WflTrdProductDealMapper.class,
//                input.getWflTrdProductDeal(), WflTrdProductDealAddMapper.class, input.getWflTrdProductDealAdd(),
//                WflPaTrdProductDealMapper.class, input.getWflPaTrdProductDeal(),
//                WflTrdProductDealOthMapper.class, input.getWflTrdProductDealOth());

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders,
                    entity.getBussTrdTransferOrder().get(0));
            wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//            bussCommonMapperDelegateService.insertBatch(WflStlPayOrdersMapper.class, wflStlPayOrders);
            wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
        }
        // add by jiangpeng on 20210413 ZGBSS-1393 start
        //【质押式回购自动到期-估值表】【质押式回购自动到期-估值表】0301zy03，2021-03-01，估值表中资产的成本、数量都不对。
        if (StringUtils.equals(DictCodeList.回购类型_质押式协议回购.getValue(), entity.getRepoType())
                && StringUtils.equals(DictCodeList.交易流通场所_深圳证券交易所.getValue(), entity.getTradeMarket())
                && StringUtils.equals("04", entity.getPs())
                && entity.getFinTradeProduct() != null) {
            entity.getFinTradeProduct().setTermType(SJXY);
        }
        // add by jiangpeng on 20210413 ZGBSS-1393 end
        finTradeProductMapper.insertSelective(entity.getFinTradeProduct());

        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        finProductTradeDto.setAssetType2(entity.getRepoType());
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDto.setTrdProductDeal(entity.getTrdProductDeal());
        baseTradeProductDealDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
        baseTradeProductDealDto.setAssetType2(entity.getRepoType());
        baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtoList = bussTrdTransferOrderService.save(
                entity.getBussTrdTransferOrder());
        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
        StlPayOrdersDto stlPayOrder = null;
        List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
        if (!StringUtil.isEmptyList(bussTrdTransferOrderDtoList)) {
            for (BussTrdTransferOrderDto bttod : bussTrdTransferOrderDtoList) {
                stlPayOrder = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrder, bttod);
                stlPayOrders.add(stlPayOrder);
        }
        }
        if (!StringUtil.isEmptyList(stlPayOrders)) {
            baseTradeProductDealDto.setStlPayOrdersList(stlPayOrders);
        }
        finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));

        // 撤销时，子组合需要调估值共通
        if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                finProductTrade.setActPortfolioId(entity.getActPortfolioId());
            });
        }
        finProductTradeDto.setBussRepoSec(entity.getBussRepoSec());
        String isExecute =restTemplate.getForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                + "/CfetsQuotedCommon/checkExecIdHasQuote?execId="
                + entity.getfTradeId(),String.class);
        if(StringUtil.equals(isExecute, DictCodeList.是否_是.getValue())){
            //存在上行报价时，不在处理流程中流水
            return;
        }
        baseTradeProductDealCommService.execute(finProductTradeDto);
        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
        }

        // 写入质押品的组合持仓流水
        this.ptlSecPositionFlowLogic(entity, false);
    }

    /**
     * 构造Entity
     *
     * @param input input
     */
    public void prepareEntity(TrdRepoTrade002Dto input) {
        input.setActPortfolioId(input.getPortfolioId());
        input.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(input.getPortfolioId()));
        // 金融产品交易Entity 构造开始-----------------------------------------------------------------------
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        // 交易编号 = 2生成的交易编号
        wflTrdProductDeal.setTradeId(input.getTradeId());
        wflTrdProductDeal.setfTradeId(input.getfTradeId());
        // 业务类型 = 数据字典缓存.共通业务类型.回购投资交易
        wflTrdProductDeal.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
        // 金融产品代码 = ※2生成的交易编号
        wflTrdProductDeal.setFinprodId(input.getTradeId());
        // 金融产品类型（投管分类）
        String finprodType2;
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典（根据交易方向与回购类型来综合判断）
        if (StringUtil.equals(input.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
            finprodType2 = DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue();
            wflTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
        } else {
            finprodType2 = DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue();
            wflTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
        }

        wflTrdProductDeal.setFinprodType2(finprodType2);
        // 金融产品类型（估值核算） TODO（等待共通）
        // 证券管理户代码 = 画面.证券托管账户
        wflTrdProductDeal.setSecManageAcctId(input.getSecAcctId());
        // 关联系统交易编号 = 画面.成交编号
        wflTrdProductDeal.setfTradeId(input.getfTradeId());

        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        wflTrdProductDeal.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        // 交易币种 = 画面.币种
        wflTrdProductDeal.setCcy(input.getCcy());
        // 交易方向 = 数据字典缓存.交易方向.买入
        wflTrdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
        // 交易日 = 画面.交易日
        wflTrdProductDeal.setTradeDate(input.getTradeDate());
        // 交易场所 = 画面.交易场所
        wflTrdProductDeal.setTradeMarket(input.getTradeMarket());
        if (DictCodeList.交易流通场所_上海证券交易所.getValue().equals(input.getTradeMarket())
                || DictCodeList.交易流通场所_深圳证券交易所.getValue().equals(input.getTradeMarket())) {
            wflTrdProductDeal.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        }
        // 起息日 = 画面.起息日
        wflTrdProductDeal.setVdate(input.getVdate());
        // 到期日 = 画面.到期日
        wflTrdProductDeal.setMdate(input.getMdate());
        // 交割日 = 画面.交易日
        wflTrdProductDeal.setSettleDate(input.getVdate());
        // 清算速度 = 画面.清算速度
        if (StringUtil.equals(DictCodeList.清算速度_T_0.getValue(), input.getSettleSpeedStr())) {
            wflTrdProductDeal.setSettleSpeed(0L);
        } else {
            wflTrdProductDeal.setSettleSpeed(1L);
        }
        // 交易份额 = 画面.本金金额
        wflTrdProductDeal.setShareAmt(input.getPrinAmt());
        // 交易本金 = 画面.本金金额
        wflTrdProductDeal.setPrinAmt(input.getPrinAmt());
        // 利息总额 = 回购利息 ※调用业务组件.【平安银行算法组件.投资交易回购利息】，算法同回购一样，可参考回购利息算法
        // ※调用业务组件.【平安银行算法组件.投资交易回购利息】，拆借算法同回购一样，可参考回购利息算法
        // 根据Jira bug：PABXZG-1606上记载，此处利息不存库
//        wflTrdProductDeal.setIntAmt(bussCoreCalculateService.calc投资交易计算回购利息(input.getPrinAmt(),
//                ArithmeticUtil.divide(input.getRebackPrice(), new BigDecimal("100"), 8),
//                input.getVdate(), input.getMdate(), input.getBasis()));
        // 交易金额 = 画面.本金金额
        wflTrdProductDeal.setTradeAmt(input.getPrinAmt());
        // 是否结清 = 数据字典.是否.是
        wflTrdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
        // 是否已撤销 = 数据字典.是否.否
        wflTrdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        // 交易员 = 输出的人员信息结果DTO.本方交易员
        wflTrdProductDeal.setTrader(input.getTrdInvestPersonInfo().getTrader());
        // 交易对手 = 输出的人员信息结果DTO.交易对手
        wflTrdProductDeal.setCounterId(input.getTrdInvestPersonInfo().getCounterId());
        // 是否外部交易 = 输出的人员信息结果DTO.是否外部交易
        wflTrdProductDeal.setIsOutTrade(input.getTrdInvestPersonInfo().getIsOutTrade());
        // 对手方交易员 = 输出的人员信息结果DTO.对手方交易员
        wflTrdProductDeal.setCounterTrader(input.getTrdInvestPersonInfo().getCounterTrader());
        // 联系方式 = 输出的人员信息结果DTO.联系方式
        wflTrdProductDeal.setCounterContact(input.getTrdInvestPersonInfo().getContactInfo());
        // 交易状态 = 交易状态_初始
        wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 工作流代码
        wflTrdProductDeal.setWorkflowId(input.getWorkflowId());
        // 备注
        wflTrdProductDeal.setRemark(input.getRemark());
        // <!-- 通道交易 新增 强成西 2019/03/26 start -->
        wflTrdProductDeal.setChlId(input.getChlId());
        if (DictCodeList.回购拆借方向_逆回购.getValue().equals(input.getPs())) {
            wflTrdProductDeal.setTradeAmt(ArithmeticUtil.doAdd(wflTrdProductDeal.getTradeAmt(), input.getChannelFee()));
        }
        else {
            wflTrdProductDeal.setTradeAmt(ArithmeticUtil.subtract(wflTrdProductDeal.getTradeAmt(), input.getChannelFee()));
        }
        wflTrdProductDeal.setFeeAmtPay(ArithmeticUtil.doAdd(wflTrdProductDeal.getFeeAmtPay(), input.getChannelFee()));
        wflTrdProductDeal.setFeeAmt(ArithmeticUtil.doAdd(wflTrdProductDeal.getFeeAmt(), input.getChannelFee()));
        // <!-- 通道交易 新增 强成西 2019/03/26 end -->
        // add by wangguoxian 费用设置 2019/08/07 start
        if (StringUtil.equals(BussinessDictCodeList.是否_是.getValue(),input.getIsPayWithTrade())){
            if (DictCodeList.回购拆借方向_逆回购.getValue().equals(input.getPs()) ) {
                wflTrdProductDeal.setTradeAmt(ArithmeticUtil.doAdd(wflTrdProductDeal.getTradeAmt(), input.getCommissionAmt()));
            }
            else {
                wflTrdProductDeal.setTradeAmt(ArithmeticUtil.subtract(wflTrdProductDeal.getTradeAmt(), input.getCommissionAmt()));
            }
            wflTrdProductDeal.setFeeAmtPay(ArithmeticUtil.doAdd(wflTrdProductDeal.getFeeAmtPay(), input.getCommissionAmt()));
            wflTrdProductDeal.setFeeAmt(ArithmeticUtil.doAdd(wflTrdProductDeal.getFeeAmt(), input.getCommissionAmt()));
        }

        // add by wangguoxian 费用设置 2019/08/07 end
        // add by wangpeng 登记托管机构 2019/08/17 start
        wflTrdProductDeal.setRegistOrg(input.getRegistOrg());
        // add by wangpeng 登记托管机构 2019/08/17 end
        // add by wanshiwei ZGZGST-3595 回购类资产“利率%”取值错误，取资产的回购利率 20210928 start
        wflTrdProductDeal.setUnitInt(ArithmeticUtil.divide(input.getRebackPrice(), 100, 8));
        // add by wanshiwei ZGZGST-3595 回购类资产 “利率%”取值错误，取资产的回购利率 20210928 end
        
        //交易表存入首期交付日
        if(StringUtil.isNotEmpty(input.getVpayDate())){
        	wflTrdProductDeal.setPayDate(input.getVpayDate());
        }
        input.setWflTrdProductDeal(wflTrdProductDeal);

        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, input.getWflTrdProductDeal());
        if(StringUtils.isNotBlank(input.getfTradeId())){
            trdProductDeal.setfTradeId(input.getfTradeId());
        }
        input.setTrdProductDeal(trdProductDeal);
        // 金融产品交易Entity 构造开始-----------------------------------------------------------------------

        // 金融产品交易附属表Entity 构造开始-----------------------------------------------------------------------
        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        // 交易编号 = 业务编号 ※2生成的交易编号
        trdProductDealAdd.setTradeId(input.getTradeId());
        // 投资组合代码 = 画面.组合代码
        trdProductDealAdd.setPortfolioId(input.getPortfolioId());
        // 委托代码 = 画面.委托代码
        trdProductDealAdd.setEntrustId(input.getEntrustId());
        // 生成方式 = 数据字典缓存.生成方式.画面录入
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        // 工作流代码
        trdProductDealAdd.setWorkflowId(input.getWorkflowId());
        input.setTrdProductDealAdd(trdProductDealAdd);

        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, input.getTrdProductDealAdd());
        // 理财产品代码 = 业务编号 ※2生成的交易编号
        wflTrdProductDealAdd.setProdId(input.getTradeId());
        wflTrdProductDealAdd.setPortfolioId(input.getActPortfolioId());
        input.setWflTrdProductDealAdd(wflTrdProductDealAdd);
        // 金融产品交易附属表Entity 构造结束-----------------------------------------------------------------------

        // 组合交易表Entity 构造开始-----------------------------------------------------------------------
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        // 组合代码 = 画面.组合代码
        ptlTrade.setPortfolioId(input.getPortfolioId());
        // 业务编号 = 业务编号 ※2生成的交易编号
        ptlTrade.setBusiNo(input.getTradeId());
        // 业务表 = "金融产品交易"
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        // 业务明细代码 = 业务编号 ※2生成的交易编号
        ptlTrade.setBusiId(input.getTradeId());
        // 业务类型 = 数据字典缓存.共通业务类型.回购投资交易
        ptlTrade.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
        // 证券托管户代码 = 画面.证券托管账户
        ptlTrade.setSecManageAcctId(input.getSecAcctId());
        // 实际发生日 = 画面.交易日
        ptlTrade.setSettleDate(input.getTradeDate());
        // 工作流代码
        ptlTrade.setWorkflowId(input.getWorkflowId());
        ptlTrades.add(ptlTrade);
        if (!input.getPortfolioId().equals(input.getActPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            // 组合代码 = 画面.组合代码
            ptlTrade1.setPortfolioId(input.getActPortfolioId());
            // 业务编号 = 业务编号 ※2生成的交易编号
            ptlTrade1.setBusiNo(input.getTradeId());
            // 业务表 = "金融产品交易"
            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            // 业务明细代码 = 业务编号 ※2生成的交易编号
            ptlTrade1.setBusiId(input.getTradeId());
            // 业务类型 = 数据字典缓存.共通业务类型.回购投资交易
            ptlTrade1.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
            // 证券托管户代码 = 画面.证券托管账户
            ptlTrade1.setSecManageAcctId(input.getSecAcctId());
            // 实际发生日 = 画面.交易日
            ptlTrade1.setSettleDate(input.getTradeDate());
            // 工作流代码
            ptlTrade1.setWorkflowId(input.getWorkflowId());
            ptlTrades.add(ptlTrade1);
        }
        input.setPtlTrade(ptlTrades);
        // 组合交易表Entity 构造结束-----------------------------------------------------------------------

        // 划款指令表Entity 构造开始----------------------------------------------------------
        if (StringUtil.isBlank(input.getIsGuarantee())) {
            // 查询组合是否保本
            PaPtlPortfolio paPtlPortfolio = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                    PaPtlPortfolioMapper.class, input.getPortfolioId(), PaPtlPortfolio.class);
            if (paPtlPortfolio != null) {
                input.setIsGuarantee(paPtlPortfolio.getIsGuarantee());
            } else {
                input.setIsGuarantee(DictCodeList.是否_否.getValue());
            }
        }
        if (bussTrdTransferOrderService != null) {
            if (StringUtil.isEmptyList(input.getBussTrdTransferOrder())) {
                // 后台自动生成划款指令
                BussSettleOrderParameterDto orderParameterDto = new BussSettleOrderParameterDto();
                // 是否汇总交易 = 是否_否
                orderParameterDto.setIsTotalTrade(DictCodeList.是否_否.getValue());

                // 金融产品交易Entity = 上述构造的金融产品交易Entity
                TrdProductDeal orderParameterDeal = new TrdProductDeal();
                // 交易日期 = 画面.交易日
                orderParameterDeal.setTradeDate(input.getTradeDate());
                // 交易方向 = 交易方向_买入
                orderParameterDeal.setPs(DictCodeList.交易方向_买入.getValue());
                // 币种 = 画面.币种
                orderParameterDeal.setCcy(input.getCcy());
                // 交易金额 = 画面.本金金额
                orderParameterDeal.setTradeAmt(input.getPrinAmt());
                // 交易对手 = 画面.交易对手
                orderParameterDeal.setCounterId(input.getTrdInvestPersonInfo().getCounterId());
                orderParameterDto.setDeal(orderParameterDeal);

                // 拆借类型 = null
                orderParameterDto.setBankLendingType(null);
                // 组合代码 = 画面.组合代码
                orderParameterDto.setPortfolioId(input.getPortfolioId());

                BussTrdTransferOrderDto resultOrderDto = bussTrdTransferOrderService.generateOrder(orderParameterDto);
                if (resultOrderDto != null) {
                    input.setBussTrdTransferOrder(Arrays.asList(resultOrderDto));
                }
            }
            if (!StringUtil.isEmptyList(input.getBussTrdTransferOrder())) {
                BussTrdTransferOrderDto bttod = input.getBussTrdTransferOrder().get(0);
                // 交易编号 = 业务编号 ※2生成的交易编号
                bttod.setTradeId(input.getTradeId());
                // 指令状态 = 指令状态_新建
                bttod.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
                // 工作流代码
                bttod.setWorkflowId(input.getWorkflowId());
                input.setBussTrdTransferOrder(bussTrdTransferOrderService.save(input.getBussTrdTransferOrder()));
            }
        }
        // 划款指令表Entity 构造结束----------------------------------------------------------

        // 交易类金融产品Entity 构造开始----------------------------------------------------------
        FinTradeProductDto finTradeProduct = new FinTradeProductDto();
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finTradeProduct.setFinprodId(input.getTradeId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finTradeProduct.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        //  金融产品类型 =  数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finTradeProduct.setFinprodType2(finprodType2);
        // 名义本金 = 画面.本金金额
        finTradeProduct.setPrin(input.getPrinAmt());
        // 名义币种 = 画面.币种
        finTradeProduct.setCcy(input.getCcy());
        // 起息日 = 画面.起息日
        finTradeProduct.setVdate(input.getVdate());
        // 到期日 = 画面.到期日
        finTradeProduct.setMdate(input.getMdate());
        finTradeProduct.setVpayDate(input.getVpayDate());
        finTradeProduct.setMpayDate(input.getMpayDate());
        // 查看缺失字段增加 add by renping 20191020 start
        finTradeProduct.setActCapDays(input.getActCapDays());
        // 查看缺失字段增加 add by renping 20191020 end
        // 期限(天) = 画面.回购期限
        finTradeProduct.setTerm(input.getTerm());
        // 利率类型 = 数据字典缓存.利率类型.固定利率
        finTradeProduct.setIntType(DictCodeList.利率类型_固定利率.getValue());
        // 固定利率 = 画面.回购利率/100
        finTradeProduct.setIntRate(
                ArithmeticUtil.divide(input.getRebackPrice(), BigDecimal.valueOf(100), 8));
        // 计息基础 = 画面.计息基础
        finTradeProduct.setBasis(input.getBasis());
        // 到期本金 = 画面.本金金额
        finTradeProduct.setmPrinAmt(input.getPrinAmt());
        // 到期利息 = 画面.回购利息
        finTradeProduct.setmIntAmt(input.getRebackIntAmt());
        // 到期金额 = 画面.本金金额+画面.回购利息
        finTradeProduct.setmTradeAmt(ArithmeticUtil.doAdd(input.getPrinAmt(), input.getRebackIntAmt()));

        // 本金收益特征 = 逆回购：保本，正回购不存
        if (DictCodeList.回购拆借方向_逆回购.getValue().equals(input.getPs())) {
            // ※若画面联动1.7查询出的组合为保本
            finTradeProduct.setCapiIncomeFeature(DictCodeListExt1.本金收益特征_保本.getValue());
        }
        // 交易场所 = 画面.交易场所
        finTradeProduct.setTradeMarket(input.getTradeMarket());
        // 交易日历 = 画面.交易日历
        finTradeProduct.setCalendarId(input.getCalendarId());
        // 期限品种 = 画面.期限品种
        finTradeProduct.setTermType(input.getTermTypeReback());
        // 交易对手 = 画面.交易对手
        finTradeProduct.setCounterId(input.getTrdInvestPersonInfo().getCounterId());
        // 工作流代码
        finTradeProduct.setWorkflowId(input.getWorkflowId());
        input.setFinTradeProduct(finTradeProduct);
        // 交易类金融产品Entity 构造结束----------------------------------------------------------

        // 交易类金融产品附属Entity 构造开始----------------------------------------------------------
        FinTradeProductAdd finTradeProductAdd = new FinTradeProductAdd();
        BeanUtil.copyPropertiesIgnoreNull(finTradeProductAdd, input.getFinTradeProduct());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finTradeProductAdd.setFinprodId(input.getTradeId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finTradeProductAdd.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        //  金融产品类型 =  数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finTradeProductAdd.setFinprodType2(finprodType2);
        // 登记托管机构
        finTradeProductAdd.setRegistOrg(input.getRegistOrg());
        input.setFinTradeProductAdd(finTradeProductAdd);
        // 交易类金融产品附属Entity 构造结束----------------------------------------------------------

        // 金融产品分类Entity 构造开始----------------------------------------------------------
        FinProductType finProductType = new FinProductType();
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finProductType.setFinprodId(input.getTradeId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finProductType.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        // 分类6 = 画面.回购类型
        finProductType.setType6(input.getRepoType());
        // 分类7 = 画面.回购类型
        finProductType.setType7(input.getPs());
        // 分类8 = 画面.回购类型
        finProductType.setType8(DictCodeList.平安资产类型_质押式回购.getValue());
        /*添加五级分类映射 lishuping 2019/08/13 start*/
        FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
        finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_无标的投管资产类型映射行内资产分类.getValue());
        finProductTypeConfig.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
        finProductTypeConfig.setInputValue2(input.getRepoType());
        finProductTypeConfig.setInputValue3(input.getPs());
        finProductTypeConfig = restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
                finProductTypeConfig, FinProductTypeConfig.class);
        finProductType.setType9(finProductTypeConfig.getOutputValue2());
        finProductType.setType10(finProductTypeConfig.getOutputValue3());
        finProductType.setType11(finProductTypeConfig.getOutputValue4());
        finProductType.setType12(finProductTypeConfig.getOutputValue5());
        /*添加五级分类映射 lishuping 2019/08/13 end*/
        // 工作流代码
        finProductType.setWorkflowId(input.getWorkflowId());
        input.setFinProductType(finProductType);
        // 金融产品分类Entity 构造结束----------------------------------------------------------

        // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
        WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
        // 交易编号 = 金融产品交易Entity.交易编号
        wflPaTrdProductDeal.setTradeId(input.getTradeId());
        // 平安业务类型 = 数据字典缓存.平安业务类型（按交易方向取对应值）
        if (DictCodeList.回购拆借方向_正回购.getValue().equals(input.getPs())) {
            // 交易方向为正回购时，取数据字典缓存.平安业务类型.正回购
            wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_正回购.getValue());
        } else {
            // 交易方向为逆回购时，取数据字典缓存.平安业务类型.逆回购
            wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_逆回购.getValue());
        }
        // 回购/拆借方向 = 画面.交易方向
        wflPaTrdProductDeal.setBackLendDirection(input.getPs());
        // 交易所属部门 = 输出的人员信息结果DTO.本方交易员所属部门
        wflPaTrdProductDeal.setDeptCode(input.getTrdInvestPersonInfo().getTraderDept());
        // 工作流代码
        wflPaTrdProductDeal.setWorkflowId(input.getWorkflowId());
        input.setWflPaTrdProductDeal(wflPaTrdProductDeal);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------

        // 金融产品交易业务流程其他信息Entity 构造开始----------------------------------------------------------
        WflTrdProductDealOth wflTrdProductDealOth = new WflTrdProductDealOth();
        // 交易编号 = 金融产品交易Entity.交易编号
        wflTrdProductDealOth.setTradeId(input.getTradeId());
        // 计息基础 = 画面.计息基础
        wflTrdProductDealOth.setBasis(input.getBasis());
        // 利率 = 画面.回购利率/100
        wflTrdProductDealOth.setRate(ArithmeticUtil.divide(input.getRebackPrice(), new BigDecimal("100"), 8));
        // 期限(天) = 画面.期限(天)
        wflTrdProductDealOth.setTerm(input.getTerm());
        // 期限品种 = 画面.期限品种
        wflTrdProductDealOth.setTermType(input.getTermTypeReback());
        // 到期金额
        wflTrdProductDealOth.setmTradeAmt(input.getPrinAmt());
        // 回购类型 = 画面.回购类型
        wflTrdProductDealOth.setRepoType(input.getRepoType());
        // 数据状态 = 数据字典缓存.数据状态.新建
        wflTrdProductDealOth.setStatus(DictCodeList.数据状态_新建.getValue());
        // 工作流代码
        wflTrdProductDealOth.setWorkflowId(input.getWorkflowId());
        input.setWflTrdProductDealOth(wflTrdProductDealOth);
        // 金融产品交易业务流程其他信息Entity 构造结束----------------------------------------------------------

        // OPCIS表Entity 构造开始----------------------------------------------------------
        // TODO
        if (!StringUtil.equals(input.getIsGuarantee(), DictCodeList.是否_是.getValue())) {

        }
        // 表OPCISEntity 构造结束----------------------------------------------------------
    }

    /**
     * 撤销交易doInsertRevoke
     *
     * @param input 参数input
     * @see
     * @since 1.0
     */
    private void doInsertRevoke(CfetsTrdRepoTrade002Dto entity) {
        Map<String, Object> params = new HashMap<>();
        if (entity.getTotleTradeId().lastIndexOf('R') == entity.getTotleTradeId().length() - 1) {
            entity.setTotleTradeId(entity.getTotleTradeId().substring(0, entity.getTotleTradeId().length() - 1));
        }
        params.put("bizCode", entity.getTotleTradeId());
        params.put("workflowId", entity.getWorkflowId());
        params.put("workflowStatus", DictCodeList.数据状态_删除中.getValue());
        params.put("user", SecurityUtil.getLoginUser().getUserName());
        params.put("dept", SecurityUtil.getLoginUser().getDeptCode()==null?"YHZGB":SecurityUtil.getLoginUser().getDeptCode());
        params.put("revokeReason", entity.getRevokeReason());
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/doRevokeInsertTotle", params,
                String.class);
    }

    /**
     * 撤销doRevokeComplete
     *
     * @param entity input
     * @see
     * @since 1.0
     */
    public void doRevokeComplete(CfetsTrdRepoTrade002Dto input,String settleRemark,Boolean isNeedDealPosition) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        if (input.getTotleTradeId().lastIndexOf('R') == input.getTotleTradeId().length() - 1) {
            input.setTotleTradeId(input.getTotleTradeId().substring(0, input.getTotleTradeId().length() - 1));
        }
        List<String> tradeDetailLists = cfetsTrdQueryMapper.selectAllTrdDetailListById(input.getTotleTradeId());
        String quoteId = null;
        String ps = null;

        for(String item : tradeDetailLists){
            //根据交易编号查询信息
            TrdRepoTrade002Dto entity=this.getTrdRepoTradeDto(item);
            //entity.setActPortfolioId(entity.getTrda());
            //entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
            List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
                            + entity.getTradeId(),
                    BaseTradeProductDealDto.class);
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
                for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                    baseDto.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                    baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    baseDto.getTrdProductDeal().setCancelRemark(entity.getRevokeReason());
                    baseDto.setEntrustId(entity.getEntrustId());
                   /* if (bussCommonMapperDelegateService != null) {
                        PaTrdProductDeal paTrdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                                PaTrdProductDealMapper.class, baseDto.getTrdProductDeal().getTradeId(),
                                PaTrdProductDeal.class);
                        paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                        paTrdProductDeal.setCreateUser(LoginUserUtil.getUserName());
                        paTrdProductDeal.setCreateTime(new Date());
                        paTrdProductDeal.setUpdateUser(paTrdProductDeal.getCreateUser());
                        paTrdProductDeal.setUpdateTime(paTrdProductDeal.getCreateTime());
                        paTrdProductDealMapper.insertSelective(paTrdProductDeal);

                        PtlTrade ptlTrade = new PtlTrade();
                        ptlTrade.setPortfolioId(entity.getPortfolioId());
                        ptlTrade.setBusiNo(baseDto.getTrdProductDeal().getTradeId());
                        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                        ptlTrade = bussCommonMapperDelegateService.selectByMultiPrimaryKey(PtlTradeMapper.class, ptlTrade, PtlTrade.class);
                        ptlTrade.setBusiNo(ptlTrade.getBusiNo() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                        ptlTradeMapper.insertSelective(ptlTrade);
                    }*/
                }
                finProductTradeDto.setAssetType2(entity.getRepoType());
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtoList);

                // 查询质押券信息
                if(entity.getBussRepoSec()!=null) {
                    finProductTradeDto.setBussRepoSec(entity.getBussRepoSec());
                }else {
                    Map<String, Object> initSearchParam = new HashMap<>();
                    initSearchParam.put("portfolioId", entity.getPortfolioId());
                    initSearchParam.put("ps", entity.getPs());
                    initSearchParam.put("bizCode", entity.getTradeId());
                    initSearchParam.put("SEC_ACCT_ID", entity.getSecAcctId() != null ? entity.getSecAcctId() : entity.getChlId());
                    finProductTradeDto.setBussRepoSec(bussRepoSecService.selectBussRepoSec(initSearchParam));
                }
                cfetsTrdBondInvestTrade002Service.baseTradeCommExecute(finProductTradeDto);
                // 报价编号
                quoteId = cfetsManualSettleMapper.selectQuoteIdBytradeId(entity.getTradeId());
                // 是否报价标识
                boolean isQuote = false;
                if (StringUtil.isNotEmpty(quoteId)) {
                    isQuote = true;
                    ps = entity.getPs();
                }
                this.insertOldRepoPosition(entity, finProductTradeDto, isQuote);
            }
//        // 撤销委托/回购交易时，删除回购质押品信息
            if(StringUtil.isNotEmpty(entity.getBussRepoSec())){
                entity.getBussRepoSec().setPageFrom("TrdRepoTrade003");
                if(!"T02".equals(entity.getPs()) && Objects.nonNull(entity.getBussRepoSec())){
                    bussRepoSecService.deletePledgeBond(entity.getTradeId(), entity.getBussRepoSec().getPageFrom());
                }
                bussRepoSecService.insert(entity.getBussRepoSec(), entity.getTradeId()+ BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
            }

            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);

            // 调用基础组件.【交易报送组件】删除交易报送信息
            restTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepTrade",
                    Arrays.asList(entity.getTradeId()), Boolean.class);

            // 调用资产负债信息报送组件 删除拆借报送信息
            restTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepAssetDebt",
                    Arrays.asList(entity.getTradeId()), Boolean.class);
            restTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepLiabilities",
                    Arrays.asList(entity.getTradeId()), Boolean.class);



            for (BaseTradeProductDealDto baseTradeProductDealDto : baseTradeProductDealDtoList) {
                // 调用投管接口 add by wm 20190218 start
                InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                investInterfCommDto.setPageId("TrdRepoTrade002");
                investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
                investInterfCommDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
                investInterfCommDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
                investInterfCommDto.setTradeFeeDetaillist(setTrdFeeDetail(entity));
                investInterfCommDto.setFinTradeProduct(entity.getFinTradeProduct());
//     		investInterfCommDto.setStlPayOrders(entity.getStlPayOrders());
                investInterfCommService.doInvestInterf(investInterfCommDto);
            }

            //删除授信占用
           /* if (null != entity.getTradeId()){
                riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
            }*/

        }
        // 交易撤销时,不会撤销报价交易数据,需要回退实际头寸并生成可用头寸;
        // 交收失败时,报价需要回退实际头寸
        if (StringUtil.equals(ps, DictCodeList.回购拆借方向_逆回购.getValue()) && StringUtil.isNotEmpty(quoteId)
                && StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            String hodingTypeOld = null;
            if(!isNeedDealPosition){
                hodingTypeOld = DictCodeList.持仓类型_可用.getValue();
            }
            cfetsRepoQuotedPrice004Service.revokePtlCashPosition(quoteId, DictCodeList.持仓类型_实际.getValue(), hodingTypeOld);
        }

        Map<String, Object> params = new HashMap<>();
        params.put("workflowId", input.getWorkflowId());
        String bizCode = input.getTotleTradeId();
        String totleTradeId = cfetsManualSettleMapper.selectIbuQuoteIdByTotleTradeId(bizCode);
        if (bizCode.lastIndexOf('R') == bizCode.length() - 1) {
            bizCode = bizCode.substring(0, bizCode.length() - 1);
        }
        params.put("bizCode", bizCode);
        params.put("updateUser", SecurityUtil.getLoginUser().getUserName());
        if(StringUtil.isNotEmpty(settleRemark)){
            params.put("revokeReason", input.getRevokeReason()==null?settleRemark :"交易撤销:"+ input.getRevokeReason()+settleRemark);
        }else if(StringUtil.isNotEmpty(input.getRevokeReason())){
            params.put("revokeReason", "交易撤销:"+ input.getRevokeReason());
        }
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/doRevokeComplete?", params,
                String.class);

        //撤销数据为登记时，将登记数据置为已撤销。删除下行匹配关系表数据
        RegFinProductDealTotal regTotal = cfetsTrdQueryMapper.selectRegTotalInfoById(bizCode);
        if(null!=regTotal){
            String remark=StringUtil.isEmpty(regTotal.getRemark()) ? "" : regTotal.getRemark();
            if (StringUtil.isNotBlank(remark)) {
                remark = remark + "\n" + (settleRemark == null ? "" : settleRemark) + "交易撤销：" + input.getRevokeReason();
            } else {
                remark = (settleRemark == null ? "" : settleRemark) + "交易撤销：" + input.getRevokeReason();
            }
            regTotal.setRemark(remark);
            //regTotal.setTradeStatus(DictCodeListExt1.有效状态_撤销.getValue());
            cfetsRepoQuotedPrice004Service.revokeForOut(regTotal.getTotleTradeId(),remark);
            cfetsTrdQueryMapper.updateRegTotalByPrimaryKeySelective(regTotal);
        }
        cfetsTrdConfirmMapper.deleteByTrdId(bizCode);

        //发送头寸
        if(isNeedDealPosition){
            PositionHttpRequest.dorevoke(bizCode, DictCodeListExt1.子系统代码_交易.getValue(), DictCodeListExt3.头寸业务类型_质押式回购首期.getValue(), input.getPs());

            //PositionHttpRequest.deleteMethod(bizCode,DictCodeListExt1.子系统代码_交易.getValue(),"REPO001");
        }else {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("totleTradeId", totleTradeId);
            paramMap.put("tradeStatus", BussinessDictCodeList.银行间上行报价状态_已撤销.getValue());
            cfetsTrdQueryMapper.updateIbuRepoByPrimaryKeySelective(paramMap);
            // 删除指令完成度
            BaseQuoteDto baseQuoteDto = new BaseQuoteDto();
            baseQuoteDto.setQuoteId(totleTradeId);
            cfetsQuotedCommonService.deleteByQuoteId(baseQuoteDto);
            List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();
            List<PtlSecPositionFlow> positionFlows = baseAssetCashPositionCommMapper.selectAllPositionByOffsetFlag(totleTradeId,
                    com.joyintech.tams.constant.DictCodeList.冲正标识_正常交易.getValue());
            for (PtlSecPositionFlow ptlSecPositionFlow : positionFlows) {
                if (StringUtil.equals(ptlSecPositionFlow.getHodingType(), com.joyintech.tams.constant.DictCodeList.持仓类型_质押.getValue())) {
                    PtlSecPositionFlowDto flowDto = new PtlSecPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(flowDto, ptlSecPositionFlow);
                    flowDto.setAmount(flowDto.getAmount().negate());
                    flowDto.setOffsetFlag(com.joyintech.tams.constant.DictCodeList.冲正标识_冲销交易.getValue());
                    flowDto.setOffsetId(ptlSecPositionFlow.getTradeId());
                    basePtlSecPositionInfoService.inertSecPositionFlow(flowDto);
                }
            }
        }
    }

    /**
     * 交收确认撤销时,重新组装质押式回购插入旧头寸
     *
     * @param entity
     * @param finProductTradeDto
     * @param isQuote 是否报价
     * @author jiangpeng
     * @date 2022/3/14 10:23
     */
    public void insertOldRepoPosition(TrdRepoTrade002Dto entity, FinProductTradeDto finProductTradeDto,boolean isQuote) {
        if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())
                && StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            String orderId = cfetsManualSettleMapper.selectRegTradeIdBytradeId(entity.getTradeId());
            finProductTradeDto.setIsExistNew(DictCodeList.是否_是.getValue());
            FinProductTradeDto finProductTradeDtoTemp = new FinProductTradeDto();
            BeanUtil.copyPropertiesIgnoreNull(finProductTradeDtoTemp, finProductTradeDto);
            if (CollectionUtils.isNotEmpty(finProductTradeDtoTemp.getBaseTradeProductDealDtos())) {
                for (BaseTradeProductDealDto baseDto : finProductTradeDtoTemp.getBaseTradeProductDealDtos()) {
//                    if (CollectionUtils.isNotEmpty(baseDto.getStlPayOrdersList())) {
//                        for (StlPayOrdersDto stlPayOrdersDto : baseDto.getStlPayOrdersList()) {
//                            if (StringUtil.isEmpty(stlPayOrdersDto.getMyAcctNo())) {
//                                String acctId = EtrCommonCashPositionDealService.select组合托管户(entity.getPortfolioId(), entity.getCcy(),
//                                        DictCodeListExt1.资金账户类型_产品托管户.getValue());
//                                stlPayOrdersDto.setMyAcctNo(acctId);
//                            }
//                        }
//                    }
                    baseDto.setStlPayOrdersList(null);
                }
            }
            baseTradeProductDealCommService.insertPosition(finProductTradeDtoTemp);
            // 登记数据会连同登记交易数据一起撤销,只用回退实际头寸
            if(!isQuote) {
                cfetsRepoQuotedPrice004Service.revokePtlCashPosition(orderId, DictCodeList.持仓类型_实际.getValue(), null);
            }
        }
    }

    /**
     * 根据交易编号删除组合持仓流水（持仓类型为融入融出）
     *
     * @param bizCode 交易编号
     * @return List<PtlSecPositionFlowDto>
     * @see
     * @since 1.0
     */
    public long deletePtlSecPositionFlow(String bizCode) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/deletePtlSecPositionFlow/" + bizCode,
                DictCodeList.持仓类型_质押.getValue(), Long.class);
    }

    /**
     * 写入质押品的组合持仓流水
     *
     * @param input    画面参数Dto
     * @param isNegate 是否冲销
     */
    private boolean ptlSecPositionFlowLogic(TrdRepoTrade002Dto input, boolean isNegate) {
        List<PtlSecPositionFlowDto> lstPtlSecPositionFlowDto = new ArrayList<>();
        // 冲销时（撤销）
        if (isNegate) {
            restTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/updatePledgeBondForRevoke/" + input.getTradeId(),
                    DictCodeList.持仓类型_质押.getValue(), Long.class);
        } else {
            // List<组合持仓流水DTO>(若上记的List<回购质押品DTO>为空，则此为空，反之循环构造DTO)
            // 通过上记输出的List<输出的质押品DTO>，循环该List构造组合持仓流水DTO
            if (!StringUtil.isEmptyList(input.getBussRepoSec().getPledgeBondList())
                    && DictCodeList.回购拆借方向_正回购.getValue().equals(input.getPs())) {
                // 可质押券信息（含投资目的）
                Map<String, List<PledgeBondInfoDto>> avaMap = this.selectAvaPledgeWithInvAim(input)
                        .stream().collect(JoyinCollectors.groupingBy(x -> x.getBondCode()));
                avaMap.entrySet().stream().forEach(entry -> {
                    Collections.sort(entry.getValue(), (o1, o2) -> o1.getInvAimSort().compareTo(o2.getInvAimSort()));
                });
                PtlSecPositionFlowDto resultDto;
                for (PledgeBondInfoDto pledgeBond : input.getBussRepoSec().getPledgeBondList()) {
                    // 待分配投资目的的质押券面总额
                    BigDecimal pledgePrin = pledgeBond.getPledgePrin();
                    List<PledgeBondInfoDto> pledgeBondInfoDtos = avaMap.get(pledgeBond.getBondCode());
                    if(!StringUtil.isEmptyList(pledgeBondInfoDtos)){
                        for (PledgeBondInfoDto avaBond : pledgeBondInfoDtos) {
                            resultDto = new PtlSecPositionFlowDto();

                            // 当前投资目的可分配券面总额
                            BigDecimal avaPrin = avaBond.getAvaPledgePrin();
                            // 当前投资目的决定分配的券面总额
                            BigDecimal prin = pledgePrin.compareTo(avaPrin) > 0 ? avaPrin : pledgePrin;

                            // 组合代码 = 画面.组合代码
                            resultDto.setPortfolioId(input.getPortfolioId());
                            // 证券管理账户/通道代码 = nvl（nvl（金融产品交易Entity.证券托管账户，金融产品交易Entity.通道代码），999999）
    //                        resultDto.setSecAcctId(StringUtil.isEmpty(input.getTrdProductDeal().getSecManageAcctId()) ? "999999" : input.getTrdProductDeal().getSecManageAcctId());
                            resultDto.setSecAcctId(StringUtil.isEmpty(input.getTrdProductDeal().getSecManageAcctId()) ?
                                    (StringUtil.isEmpty(input.getChlId()) ? "999999": input.getChlId()) :
                                            input.getSecAcctId());
                            // 金融产品代码 = 输出质押品DTO.金融产品代码
                            resultDto.setFinprodId(pledgeBond.getFinprodId());
                            // 分支序号 = 系统固定参数，默认为0
                            resultDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
                            // 持仓类型
                            resultDto.setHodingType(DictCodeList.持仓类型_质押.getValue());
                            // 日期 = 金融产品交易Entity.交易日
                            resultDto.setCdate(input.getTrdProductDeal().getTradeDate());
                            // 数量 = 输出质押品DTO.质押数量
                            resultDto.setAmount(ArithmeticUtil.divide(prin, new BigDecimal("100"), 4));
                            // 币种 = 金融产品交易Entity.币种
                            resultDto.setCcy(input.getTrdProductDeal().getCcy());
                            // 交易编号 = 金融产品交易Entity.交易编号
                            resultDto.setTradeId(input.getTrdProductDeal().getTradeId());
                            // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
                            resultDto.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());

                            if (avaPrin.compareTo(BigDecimal.ZERO) > 0) {
                                if (StringUtil.isNotEmpty(input.getActPortfolioId()) && !StringUtil.equals(input.getActPortfolioId(), input.getPortfolioId())) {
                                    PtlSecPositionFlowDto subResultDto = new PtlSecPositionFlowDto();
                                    BeanUtil.copyPropertiesIgnoreNull(subResultDto, resultDto);
                                    subResultDto.setPortfolioId(input.getActPortfolioId());
                                    lstPtlSecPositionFlowDto.add(subResultDto);
                                }
                                lstPtlSecPositionFlowDto.add(resultDto);
                            }
                            // 分配完毕后,停止可质押券信息（含投资目的）的循环
                            pledgePrin = pledgePrin.subtract(prin);
                            if (pledgePrin.compareTo(BigDecimal.ZERO) <= 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (StringUtil.isEmptyList(lstPtlSecPositionFlowDto)) {
            return true;
        } else {
            PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
            ptlPositionHoldDto.setPtlSecPositionFlows(lstPtlSecPositionFlowDto);
            return restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
                    ptlPositionHoldDto, Boolean.class);
        }
    }


    /**
     * 写入现金流相关信息表
     *
     * @param input 画面参数Dto
     * @see
     * @since 1.0
     */
    private void cashFlowLogic(TrdRepoTrade002Dto input) {
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);

        BaseFinProductCashInputDto inputDto = new BaseFinProductCashInputDto();

        // List<现金流信息Entity>
        List<FinCashInfo> finCashInfoList = new ArrayList<>();
        // 现金流信息Entity-本金
        FinCashInfo finCashInfoPrin = new FinCashInfo();
        // 现金流代码 = 调用【各种业务共通组件.现金流代码生成】生成现金流代码。输入参数如下
        // 业务代码 = 金融产品交易Entity.交易编号
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.本金
        finCashInfoPrin.setCashId(commonBusinessService.generateCashFlowCode(input.getTradeId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong,
                DictCodeListExt1.现金流二级类型_本金.getValue()));
        // 现金流一级类型 = 数据字典缓存.现金流一级类型.本金
        finCashInfoPrin.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.本金
        finCashInfoPrin.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
        // 币种 = 画面.币种
        finCashInfoPrin.setCcy(input.getCcy());
        // 业务代码 = 金融产品交易Entity.交易编号
        finCashInfoPrin.setFinprodId(input.getTradeId());
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        finCashInfoPrin.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashInfoPrin.setBranch(branchLong);
        // 工作流代码
        finCashInfoPrin.setWorkflowId(input.getWorkflowId());
        finCashInfoList.add(finCashInfoPrin);

        // 现金流信息Entity-利息
        FinCashInfo finCashInfoInt = new FinCashInfo();
        // 现金流代码 = 调用【各种业务共通组件.现金流代码生成】生成现金流代码。输入参数如下
        // 业务代码 = 金融产品交易Entity.交易编号
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.利息
        finCashInfoInt.setCashId(commonBusinessService.generateCashFlowCode(input.getTradeId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong,
                DictCodeListExt1.现金流二级类型_利息.getValue()));
        // 现金流一级类型 = 数据字典缓存.现金流一级类型.利息
        finCashInfoInt.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.利息
        finCashInfoInt.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
        // 币种 = 画面.币种
        finCashInfoInt.setCcy(input.getCcy());
        // 业务代码 = 金融产品交易Entity.交易编号
        finCashInfoInt.setFinprodId(input.getTradeId());
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        finCashInfoInt.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashInfoInt.setBranch(branchLong);
        // 工作流代码
        finCashInfoInt.setWorkflowId(input.getWorkflowId());
        finCashInfoList.add(finCashInfoInt);

        inputDto.setFinCashInfoList(finCashInfoList);

        // List<现金流支付规则表Entity>
        List<FinCashPayRule> finCashPayRuleList = new ArrayList<>();

        // 现金流支付规则表Entity-利息
        FinCashPayRule finCashPayRule = new FinCashPayRule();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finCashPayRule.setCashId(finCashInfoInt.getCashId());
        // 生效日期 = 画面.起息日
        finCashPayRule.setEffDate(input.getVdate());
        // 支付频率 = 数据字典缓存.支付频率(付息频率).到期支付
        
     // 一体化对应etliu start
//        finCashPayRule.setPayCycle(DictCodeList.支付频率_付息频率_到期支付.getValue());
        finCashPayRule.setPayCycle(DictCodeList.频率_到期一次.getValue());
     // 一体化对应etliu end
        
     // 一体化对应etliu
//        finCashPayRule.setCalcRule(DictCodeList.推算方法_起息日向后.getValue());
        finCashPayRule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());
        
        // 首次支付日 = 画面.到期日
        finCashPayRule.setFirstPayDate(input.getMdate());
        // 现金流单位 = 数据字典缓存.现金流单位.总额
        finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashPayRule.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashPayRule.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashPayRule.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashPayRule.setBranch(branchLong);
        // 工作流代码
        finCashPayRule.setWorkflowId(input.getWorkflowId());
        finCashPayRuleList.add(finCashPayRule);

        inputDto.setFinCashPayRuleList(finCashPayRuleList);

        // List<现金流计算规则表Entity>
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();

        // 现金流计算规则表Entity-利息
        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finCashCalcRule.setCashId(finCashInfoInt.getCashId());
        // 生效日期 = 画面.起息日
        finCashCalcRule.setEffDate(input.getVdate());
        // 公布日期 = 画面.起息日
     // 一体化对应etliu
//        finCashCalcRule.setRelaeseDate(dto.getVdate()); // 公布日期
        // 计算类型 = 数据字典缓存.利率类型.固定利率
        finCashCalcRule.setCalcType(DictCodeList.利率类型_固定利率.getValue());
        // 基数类型 = 数据字典缓存.基数类型.本金
        finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
        // 基数日期类型 = 数据字典缓存.基数日期类型.当日
        finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
        // 计算基础 = 画面.计息基础
        finCashCalcRule.setBasis(input.getBasis());
        // 生效利率 = 画面.回购利率/100
        finCashCalcRule.setYield(ArithmeticUtil.divide(input.getRebackPrice(), new BigDecimal("100"), 8));
        // 是否初始利率 = 数据字典缓存.是否.是
        finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashCalcRule.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashCalcRule.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashCalcRule.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashCalcRule.setBranch(branchLong);
        // 工作流代码
        finCashCalcRule.setWorkflowId(input.getWorkflowId());
        finCashCalcRuleList.add(finCashCalcRule);

        inputDto.setFinCashCalcRuleList(finCashCalcRuleList);

        // List<现金流利率信息表Entity>
        List<FinRateInfo> finRateInfoList = new ArrayList<>();

        // 现金流利率信息表Entity-利息
        FinRateInfo finRateInfo = new FinRateInfo();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finRateInfo.setCashId(finCashInfoInt.getCashId());
        // 生效日 = 画面.起息日
        finRateInfo.setEffDate(input.getVdate());
        // 利率类型 = 数据字典缓存.利率类型.固定利率
        // 一体化对应etliu
//        finRateInfo.setRateType(DictCodeList.利率类型_固定利率.getValue());
        finRateInfo.setIntType(DictCodeList.利率类型_固定利率.getValue());
        // 计算基础 = 画面.计息基础
        finRateInfo.setBasis(input.getBasis());
        // 初始利率 = 画面.回购利率/100
        finRateInfo.setRate(finCashCalcRule.getYield());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finRateInfo.setFinprodId(input.getTradeId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finRateInfo.setBranch(branchLong);
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finRateInfo.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finRateInfo.setFinprodType2(input.getFinprodType2());
        // 工作流代码
        finRateInfo.setWorkflowId(input.getWorkflowId());
        finRateInfoList.add(finRateInfo);

        inputDto.setFinRateInfoList(finRateInfoList);

        // List<现金流计划表Entity>
        List<FinCashPlan> finCashPlanList = new ArrayList<>();

        // 现金流计划表Entity 到期还本
        FinCashPlan finCashPlan = new FinCashPlan();
        // 现金流代码 = 现金流信息Entity-本金.现金流代码
        finCashPlan.setCashId(finCashInfoPrin.getCashId());
        // 现金流序号 = 1
        finCashPlan.setCashNum(1L);
        // 现金流类型 = 数据字典缓存.现金流类型.到期还本
        finCashPlan.setCashType(DictCodeList.现金流类型_到期还本.getValue());
        // 未调整的计算开始日 = 画面.起息日
        finCashPlan.setVdateUnadjust(input.getVdate());
        // 未调整的计算结束日 = 画面.到期日
        finCashPlan.setMdateUnadjust(input.getMdate());
        // 未调整的计划支付日 = 画面.到期日
        finCashPlan.setPayDateUnadjust(input.getMdate());
        // 调整后的计算开始日 = 画面.起息日
        finCashPlan.setVdate(input.getVdate());
        // 调整后的计算结束日 = 画面.到期日
        finCashPlan.setMdate(input.getMdate());
        // 调整后的计划支付日 = 画面.到期日
        finCashPlan.setPayDate(input.getMdate());
        // 计算周期天数 = 画面.到期日-画面.起息日
        finCashPlan.setTermdays(DateUtil.getBetweenDayNumber(input.getVdate(), input.getMdate()));
        // 计划支付本金 = 画面.本金金额
        finCashPlan.setPrinAmt(input.getPrinAmt());
        // 计息本金 = 画面.本金金额
        finCashPlan.setIntPrinAmt(input.getPrinAmt());
        // 计划支付利息 = 0
        finCashPlan.setIntAmt(BigDecimal.ZERO);
        // 是否支付利息 = 数据字典缓存.是否.是
        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        // 计划支付现金流 = 画面.本金金额
        finCashPlan.setCashAmt(input.getPrinAmt());
        // 现金流单位 = 数据字典缓存.现金流单位.总额
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashPlan.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashPlan.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashPlan.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 固定值，系统参数
        finCashPlan.setBranch(branchLong);
        // 支付方式 = 数据字典缓存.分红方式.现金分红
        finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
        // 工作流代码
        finCashPlan.setWorkflowId(input.getWorkflowId());
        finCashPlanList.add(finCashPlan);

        // 现金流计划表Entity 到期付息
        finCashPlan = new FinCashPlan();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finCashPlan.setCashId(finCashInfoInt.getCashId());
        // 现金流序号 = 2
        finCashPlan.setCashNum(2L);
        // 现金流类型 = 数据字典缓存.现金流类型.到期付息
        finCashPlan.setCashType(DictCodeList.现金流类型_到期收息.getValue());
        // 未调整的计算开始日 = 画面.起息日
        finCashPlan.setVdateUnadjust(input.getVdate());
        // 未调整的计算开始日 = 画面.到期日
        finCashPlan.setMdateUnadjust(input.getMdate());
        // 未调整的计划支付日 = 画面.到期日
        finCashPlan.setPayDateUnadjust(input.getMdate());
        // 调整后的计算开始日 = 画面.起息日
        finCashPlan.setVdate(input.getVdate());
        // 调整后的计算结束日 = 画面.到期日
        finCashPlan.setMdate(input.getMdate());
        // 调整后的计划支付日 = 画面.到期日
        finCashPlan.setPayDate(input.getMdate());
        // 计算周期天数 = 画面.到期日-画面.起息日
        finCashPlan.setTermdays(DateUtil.getBetweenDayNumber(input.getVdate(), input.getMdate()));
        // 计划支付本金 = 0
        finCashPlan.setPrinAmt(BigDecimal.ZERO);
        // 计息本金 = 画面.本金金额
        finCashPlan.setIntPrinAmt(input.getPrinAmt());
        // 计划支付利息 = 回购利息 ※调用业务组件.【平安银行算法组件.投资交易回购利息】，拆借算法同回购一样，可参考回购利息算法
         /*finCashPlan.setIntAmt(bussCoreCalculateService.calc投资交易计算回购利息(input.getPrinAmt(),
                ArithmeticUtil.divide(input.getRebackPrice(), new BigDecimal("100"), 8),
                input.getVdate(), input.getMdate(), input.getBasis()));*/
        //update by xr for TISHKBS-655 start
        //深交所质押式竞价正回购，到期的时候带出的回购利息错误
        finCashPlan.setIntAmt(input.getRebackIntAmt());
        //update by xr for TISHKBS-655 end
        // 是否支付利息 数据字典缓存.是否.是
        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        // 计划支付现金流 = 回购利息
        finCashPlan.setCashAmt(finCashPlan.getIntAmt());
        // 现金流单位 = 数据字典缓存.现金流单位.总额
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashPlan.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashPlan.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashPlan.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 固定值，系统参数
        finCashPlan.setBranch(branchLong);
        // 支付方式 = 数据字典缓存.分红方式.现金分红
        finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
        // 工作流代码
        finCashPlan.setWorkflowId(input.getWorkflowId());
        finCashPlanList.add(finCashPlan);

        inputDto.setFinCashPlanList(finCashPlanList);

        // 调用共通登录现金流相关数据
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                        + "/common/trdcommonbase/insertFinProductCashData",
                inputDto, Boolean.class);
    }


    /**
     * 7.7写入交易信息进交易报送信息表，为中债报送用
     *
     * @param input 画面参数dto
     * @see
     * @since 1.0
     */
    private void dealInfoCbReportLogic_7_7(TrdRepoTrade002Dto input) {
        // 7.7写入交易信息进交易报送信息表，为中债报送用
        // 1)查询产品登记编码
        String prodRegistCode = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectProdRegistCode/"
                        + input.getPortfolioId(),
                null, String.class);
        // 若产品登记编码不为空则继续执行2），反之操作结束
        if (StringUtil.isBlank(prodRegistCode)) {
            return;
        }
        // 2）写入交易报送信息
        // 交易报送信息Entity
        RepTrade repTrade = new RepTrade();
        // 产品登记编码 = 1）输出的产品登记编码
        repTrade.setRepFinprodRegId(prodRegistCode);
        // 行内交易编码 = 金融产品交易Entity.交易编号
        repTrade.setRepBankTradeCode(input.getTradeId());
        // 行内资产/负债编码 = 金融产品交易Entity.交易编号
        repTrade.setRepBankAssetCode(input.getTradeId());
        // 币种 = 画面.币种
        repTrade.setRepCcy(input.getCcy());
        // 发生金额 = 画面.本金金额
        repTrade.setRepOccurMoney(input.getPrinAmt());
        // 数量 = 画面.本金金额
        repTrade.setRepCount(input.getPrinAmt());
        BigDecimal 收盘价 = commonBusinessService.selectClosePrice(input.getCcy(),
                DateUtil.format(input.getTradeDate()));
        BigDecimal 折算人民币金额 = ArithmeticUtil.multiply(input.getPrinAmt(), 收盘价);
        // 折算人民币金额（元） = 画面.本金金额*收盘价(利用画面.币种与交易日查询汇率行情表中最新的收盘价)
        repTrade.setRepMoneyCny(折算人民币金额);

        // 资金流动类型 = 数据字典缓存.资金流动类型 ※根据交易方向判断，若正回购，即拆入和正回购首期，若逆回购，即为拆出和逆回购首期
        String 资金流动类型 = null;
        if (StringUtil.equals(input.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
            资金流动类型 = DictCodeListExt2.资金流动类型_同业拆入和卖出回购首期.getValue();
        } else if (StringUtil.equals(input.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
            资金流动类型 = DictCodeListExt2.资金流动类型_拆放同业和买入返售首期.getValue();
        }
        repTrade.setRepMoneyFlowType(资金流动类型);
        // 资金流动类型说明 = "无"
        repTrade.setRepMoneyFlowTypeExp("无");
        // 交易日 = 画面.交易日
        repTrade.setRepTradeDate(input.getTradeDate());
        // 交易对手方 = 输出的人员信息结果DTO.交易对手
        repTrade.setRepTradeComponent(input.getTrdInvestPersonInfo().getCounterId());
        // 成交编号/合同号 = 画面.银行间成交编号
        repTrade.setRepDelCode(input.getfTradeId());
        // 报送状态 = 数据字典缓存.是否.是
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
        // 数据日期 = 系统平台日期
        repTrade.setRepStatusDate(iPlatformDate.getSysDate());
        // 备注 = 画面.备注
        repTrade.setRepRemark(input.getRemark());

        // 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
        restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepTrade",
                Arrays.asList(repTrade), Boolean.class);

        // 7.8写入负债(同业拆入/卖出回购)报送表，为中债报送用
        // 资产负债报送信息Entity
        this.assetDebtCbReportLogic_7_8_1(input);

        // 负债(同业拆入/卖出回购)报送信息Entity
        this.assetDebtCbReportLogic_7_8_2(input);
    }

    /**
     * 调用资产负债信息报送组件
     * 写入资产负债报送信息
     *
     * @param input 画面参数dto
     * @see
     * @since 1.0
     */
    private void assetDebtCbReportLogic_7_8_1(TrdRepoTrade002Dto input) {
        // 资产负债报送信息Entity
        RepAssetDept repAssetDept = new RepAssetDept();
        // 金融产品代码 = 金融产品交易Entity.交易编号
        repAssetDept.setRepFinprodId(input.getTradeId());
        // 市场代码 = 金融产品交易Entity.交易编号
        repAssetDept.setFinprodMarketId(input.getTradeId());
        // 发行机构代码 = 系统参数缓存.发行机构代码
        repAssetDept.setRepIssuePartyId(CacheUtil.getSysParam(BussinessConstant.ISSUE_PARTY_ID));
        // 资产/负债类别
        if (StringUtil.equals(input.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
        	repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_卖出回购.getValue());
        } else {
        	repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_买入返售.getValue());
        }
        // 币种 = 画面.币种
        repAssetDept.setRepCcy(input.getCcy());
        // 报送状态 = 数据字典缓存.是否.是
        repAssetDept.setRepSendStatus(DictCodeList.是否_是.getValue());
        // 交易流通场所
        if (StringUtil.equals(input.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())) {
        	repAssetDept.setRepTradeMarket(DictCodeListExt2.交易流通场所_中债_银行间债券市场.getValue());
        } else if (StringUtil.equals(input.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
        	repAssetDept.setRepTradeMarket(DictCodeListExt2.交易流通场所_中债_上海证券交易所.getValue());
        } else {
        	repAssetDept.setRepTradeMarket(DictCodeListExt2.交易流通场所_中债_深圳证券交易所.getValue());
        }
        /*【回购】 通道交易 买断式回购，质押式回购，REP_ASSET_DEPT 不应存值 add by hxy 20200413*/
        if (StringUtil.isEmpty(input.getTrdProductDeal().getChlId())) {
            // 调用资产负债信息报送组件
            restTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepAssetDebt",
                    Arrays.asList(repAssetDept), Boolean.class);
        }
     /*   // 调用资产负债信息报送组件
        restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepAssetDebt",
                Arrays.asList(repAssetDept), Boolean.class);*/
    }

    /**
     * 调用负债(同业拆入卖出回购)报送信息组件
     * 写入负债(同业拆入卖出回购)报送信息
     *
     * @param input 画面参数dto
     * @see
     * @since 1.0
     */
    private void assetDebtCbReportLogic_7_8_2(TrdRepoTrade002Dto input) {
        // 拆入、正回购 -> 负债(拆入或正回购)类要素登记
        if (StringUtil.equals(input.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
            // 负债(同业拆入/卖出回购)报送信息Entity
            RepLiabilities repLiabilities = new RepLiabilities();
            // 金融产品代码 = 金融产品交易Entity.交易编号
            repLiabilities.setRepFinprodId(input.getTradeId());
            // 起息日 = 画面.起息日
            repLiabilities.setRepValueDate(input.getVdate());
            // 到期日 = 画面.到期日
            repLiabilities.setRepExpiringDate(input.getMdate());
            // 对手方 = 输出的人员信息结果DTO.交易对手
            repLiabilities.setRepCounterparty(input.getTrdInvestPersonInfo().getCounterId());
            // 年利率% = 画面.利率
            repLiabilities.setRepAnnualInterestRate(input.getRebackPrice());
            // 计息基础 = 画面.计息基础
            repLiabilities.setRepBasis(input.getBasis());
            // 备注 = 画面.备注
            repLiabilities.setRepRemark(input.getRemark());
            // 回购标的类别 = 回购标的类型_中债_债券
            // update by caiqun 20210310 合并TISHKBS-1754 中债报送-资产要素登记，“回购标的的类别”不正确。导出数据为02股票，实际应为01债券  start
            //repLiabilities.setRepTargetType(DictCodeListExt1.回购标的类型_质押式正回购.getValue());
            repLiabilities.setRepTargetType("01");
            // update by caiqun 20210310 合并TISHKBS-1754 中债报送-资产要素登记，“回购标的的类别”不正确。导出数据为02股票，实际应为01债券  end
            // 回购标的金额 = 本金金额
            repLiabilities.setRepBuyBackMoney(input.getPrinAmt());

            // 调用负债(同业拆入卖出回购)报送信息组件
            restTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepLiabilities",
                    Arrays.asList(repLiabilities), Boolean.class);
        }
        // 拆出、逆回购 -> 拆放同业及买入返售
        else {
            RepAssetInterLend repAssetInterLend = new RepAssetInterLend();
            // 金融产品代码 = 金融产品交易Entity.交易编号
            repAssetInterLend.setRepFinprodId(input.getTradeId());
            // 起息日 = 画面.起息日
            repAssetInterLend.setRepVdate(input.getVdate());
            // 到期日 = 画面.到期日
            repAssetInterLend.setRepMdate(input.getMdate());
            // 对手方 = 输出的人员信息结果DTO.交易对手
            repAssetInterLend.setRepTradeTarget(input.getTrdInvestPersonInfo().getCounterId());
            // 年利率% = 画面.利率
            repAssetInterLend.setRepRateYear(input.getRebackPrice());
            // 计息基础 = 画面.计息基础
            repAssetInterLend.setRepBasis(input.getBasis());
            // 备注 = 画面.备注
            repAssetInterLend.setRepRemark(input.getRemark());
            // 回购标的类别 = 回购标的类型_中债_债券
            // update by caiqun 20210310 合并TISHKBS-1754 中债报送-资产要素登记，“回购标的的类别”不正确。导出数据为02股票，实际应为01债券  start
            //repAssetInterLend.setRepTargetType(DictCodeListExt1.回购标的类型_质押式逆回购.getValue());
            repAssetInterLend.setRepTargetType("01");
            // update by caiqun 20210310 合并TISHKBS-1754 中债报送-资产要素登记，“回购标的的类别”不正确。导出数据为02股票，实际应为01债券  end
            // 回购标的金额 = 本金金额
            repAssetInterLend.setRepTargetAmt(input.getPrinAmt());

            // 调用负债(同业拆入卖出回购)报送信息组件
            restTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepDlRepoInfo",
                    Arrays.asList(repAssetInterLend), Boolean.class);
        }
    }

    /**
     * 根据组合代码查询证券托管户：联想框文本框
     *
     * @param parameters 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getSecManageAcctId(Map<String, Object> parameters) {
        List<SuggestDto> tempList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/getSecManageAcctId",
                parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            SelectOptionDto selectOptionDto;
            for (SuggestDto suggestDto : tempList) {
                selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                selectOptionDto.setObject(suggestDto.getObject());
                if (StringUtil.isNotBlank((String) parameters.get("portfolioId"))) {
                    selectOptionDto.setParentId((String) parameters.get("portfolioId"));
                }
                resultList.add(selectOptionDto);
            }
        }
        return resultList;
    }

    /**
     * 根据交易编号检索组合持仓流水（持仓类型为实际）
     *
     * @param bizCode 交易编号
     * @return List<PtlSecPositionFlowDto>
     * @see
     * @since 1.0
     */
    public List<PtlSecPositionFlow> selectPtlSecPositionFlow(String bizCode) {
        return restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/selectPtlSecPositionFlow/" + bizCode,
                null, PtlSecPositionFlow.class);
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
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/selectTradeIdByWorkflowId/"
                        + workflowId,
                null, String.class);
    }

    /**
     * 1、检查到期日是否为节假日
     * 到期日如果是节假日 要提醒
     * <p>
     * 2、逆回购时，本金金额 〉 组合可用剩余现金，提示
     *
     * @param inputDto 画面输入参数
     * @param result   valid结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    public String checkConfirm(TrdRepoTrade002Dto inputDto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return null;
        }

        String confirmMsg = "";
        String changeLineMark = "<br/>";

        // 到期日如果是节假日 要提醒
        boolean isDealCalendarHoliday = dateCalService.isDealCalendarHoliday(
                inputDto.getMdate(), inputDto.getCalendarId());
        if (isDealCalendarHoliday) {
            if (StringUtil.isNotBlank(confirmMsg)) {
                confirmMsg += changeLineMark;
            }
            confirmMsg += getMessage("BUSS_COM_E_0039", getMessage("TrdRepoTrade002.mdate"));
        }

        // 逆回购时，本金金额 〉 组合可用剩余现金，提示
        if (inputDto != null
                && DictCodeList.回购拆借方向_逆回购.getValue().equals(inputDto.getPs())) {
            if (inputDto.getPrinAmt() != null && inputDto.getBussTrdAvailableCashQuery() != null
                    && inputDto.getBussTrdAvailableCashQuery().getAvalibleCash() != null
                    && StringUtil.isEmpty(inputDto.getChlId())
                    && inputDto.getPrinAmt().compareTo(inputDto.getBussTrdAvailableCashQuery().getAvalibleCash()) > 0
            ) {
                if (StringUtil.isNotBlank(confirmMsg)) {
                    confirmMsg += changeLineMark;
                }
                confirmMsg += getMessage("TRD_C_0002");
            }
        }
        if ((StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), inputDto.getTradeMarket())
                || StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), inputDto.getTradeMarket()))
                && StringUtil.isBlank(inputDto.getChlId())) {
            confirmMsg = checkTradeMarket(inputDto, confirmMsg, changeLineMark,inputDto.getBussTrdTransferOrder().get(0));
        }
//        if (StringUtil.isNotBlank(confirmMsg)) {
////            AjaxResult wr = new AjaxResult();
////            wr.setData(confirmMsg);
//            return confirmMsg;
//        }

        return confirmMsg;
    }

    private String checkTradeMarket(TrdRepoTrade002Dto inputDto, String confirmMsg, String changeLineMark,BussTrdTransferOrderDto bussTrdTransferOrder) {
        // 交易方向为买入时校验交易所证券账户（券商存管账户）余额是否足够，如不足，提醒进行银证转账
        if (StringUtil.equals(DictCodeList.回购拆借方向_逆回购.getValue(), inputDto.getPs())
                && StringUtil.isNotBlank(inputDto.getSecAcctId())
                && inputDto.getTradeAmtAndFee() != null && bussTrdAvailableCashQueryService != null
                && !StringUtil.isEmptyList(inputDto.getBussTrdTransferOrder())) {
            // 查询网点账号
            StlSecAcctInfo stlSecAcctInfo = stlSecAcctInfoMapper.selectByPrimaryKey(inputDto.getSecAcctId());

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
//                    paramMap.put("acctType", DictCodeListExt1.资金账户类型_券商资金账户.getValue());
//                paramMap.put("finprodId", inputDto.getFinprodId());

                BigDecimal 余额 = bussTrdAvailableCashQueryService.getSecManageAvailableCash(paramMap);

                if (inputDto.getTradeAmtAndFee().compareTo(余额) > 0) {
                    if (StringUtil.isNotBlank(confirmMsg)) {
                        confirmMsg += changeLineMark;
                    }
                    confirmMsg += getMessage("TRD_C_0001");
                }
            }
        }
        return confirmMsg;
    }

    /**
     * 回购资产交易新建画面，查看模式：查询回购资产交易信息
     *
     * @param tradeId 交易编号
     * @return 回购资产交易信息
     * @see
     * @since 1.0
     */
    public TrdRepoTrade002Dto getTrdRepoTradeDto(String tradeId) {
        Map<String, Object> param = new HashMap<>();
        param.put("tradeId", tradeId);
        param.put("BRANCH_0", CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        TrdRepoTrade002Dto resultDto = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/getTrdRepoTradeDto",
                param, TrdRepoTrade002Dto.class);
        //获取费用信息
        TrdTradeFeeDetailKey feeDetailKey = new TrdTradeFeeDetailKey();
        feeDetailKey.setTradeId(tradeId);
        feeDetailKey.setFeeType(BussinessDictCodeList.费用类型_交易手续费.getValue());
        TrdTradeFeeDetail procedureAmtDetail =  bussCommonMapperDelegateService.selectByMultiPrimaryKey(TrdTradeFeeDetailMapper.class,feeDetailKey, TrdTradeFeeDetail.class);
        if(procedureAmtDetail != null){
            resultDto.setProcedureAmt(procedureAmtDetail.getFeeAmt());
            resultDto.setProcedureAmtRate(procedureAmtDetail.getFeeRate());
        }
        feeDetailKey.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
        TrdTradeFeeDetail settleAmtDetail =  bussCommonMapperDelegateService.selectByMultiPrimaryKey(TrdTradeFeeDetailMapper.class,feeDetailKey, TrdTradeFeeDetail.class);
        if (settleAmtDetail != null){
            resultDto.setSettleAmt(settleAmtDetail.getFeeAmt());
            resultDto.setSettleAmtRate(settleAmtDetail.getFeeRate());
        }
        feeDetailKey.setFeeType(BussinessDictCodeList.费用类型_佣金.getValue());
        TrdTradeFeeDetail commitAmtDetail =  bussCommonMapperDelegateService.selectByMultiPrimaryKey(TrdTradeFeeDetailMapper.class,feeDetailKey, TrdTradeFeeDetail.class);
        if (commitAmtDetail != null){
            resultDto.setCommissionAmt(commitAmtDetail.getFeeAmt());
            resultDto.setCommissionAmtRate(commitAmtDetail.getFeeRate());
            resultDto.setIsPayWithTrade(commitAmtDetail.getIsPayWithTrade());
        }
        return resultDto;

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
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                        + "/TrdRepoTrade/getEntrustInfo/" + entrustId,
                null, BaseEtrInvestInfoDto.class);
    }

    /**
     * 组合代码、证券托管户、交易日、到期日改变时，查询可使用标准券额度
     *
     * @param params params
     * @return 可使用标准券额度
     * @see
     * @since 1.0
     */
    public BigDecimal getAvaStandardStockPosition(Map<String, Object> params) {
        params.put("BRANCH_0", CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        params.put("manageId",params.get("secAcctId"));
//        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
//                        + "/TrdRepoTrade/getAvaStandardStockPosition/",
//                params, BigDecimal.class);
        return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrRepo/selectStandardAmt",params,
                BigDecimal.class);
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
            List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate,
                    DateUtil.addDays(tradeDate, 31), (String) parameters.get("calendarId"));
            Date tradingDay = dateCalService.calculationTradingDay(tradeDate, 1, holiday, "1");
            return DateUtil.format(tradingDay);
        }
        return null;
    }

    /**
     * 查询副总裁/总裁助理授权额度
     *
     * @param params params
     * @return 可使用标准券额度
     * @see
     * @since 1.0
     */
    public BigDecimal getVPOrPAuthAmount(Map<String, Object> params) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                        + "/TrdRepoTrade/getVPOrPAuthAmount/",
                params, BigDecimal.class);
    }

    /**
     * 根据画面条件检索可质押券信息（含投资目的）
     *
     * @param input 画面参数
     * @return List<PledgeBondInfoDto>
     */
    public List<PledgeBondInfoDto> selectAvaPledgeWithInvAim(TrdRepoTrade002Dto input) {
        Map<String, Object> param = new HashMap<>();
        param.put("portfolioId", input.getPortfolioId());
        param.put("ps", input.getPs());
        param.put("isWithInvAim", true);
        param.put("INV_AIM_AC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        param.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue());
        param.put("INV_AIM_FVTPL", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
        param.put("secAcctId", input.getSecAcctId());
        param.put("chlId", input.getChlId());
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/selectAvaPledgeBondDetailList",
                param, PledgeBondInfoDto.class);
    }

    // -----add by 裴家俊 2019/03/29-----start------
    /**
     * 计算费用信息
     *
     * @param inputDto 画面参数
     * @return 计算结果
     */
    public List<TrdTradeFeeAddRateDetail> calculateFeeData(TrdRepoTrade002Dto dto) {
        List<TrdTradeFeeAddRateDetail> retList = new ArrayList<TrdTradeFeeAddRateDetail>();
        try {
            BasicTradeFeeInputDto inputDto = new BasicTradeFeeInputDto();
            inputDto.setTradeDate(dto.getTradeDate());
            inputDto.setTradeMarket(dto.getTradeMarket());
            inputDto.setFinprodType2(BussinessDictCodeList.平安资产类型_回购.getValue());
            inputDto.setPs(dto.getPs());// 交易方向
            inputDto.setFpriceAmt(dto.getFpriceAmt());// 全价总额
            inputDto.setPrinAmt(dto.getPrinAmt());// 券面总额
            inputDto.setTermType(dto.getTermTypeReback());// 期限品种
            inputDto.setIsGrade(dto.getIsGrade());// 是否多个质押券
            if (StringUtil.equals(BussinessDictCodeList.交易场所_银行间.getValue(),
                    dto.getTradeMarket())) {
                StlSecAcctInfo ptlSecAcct = stlSecAcctInfoMapper.selectByPrimaryKey(
                        dto.getSecManageAcctId());
                inputDto.setRegistOrg(null != ptlSecAcct ? ptlSecAcct.getRegistOrg() : null);// 债券登记托管机构
                inputDto.setFeeTypeList(Arrays.asList(BussinessDictCodeList.费用类型_交易手续费.getValue(),
                        BussinessDictCodeList.费用类型_结算手续费.getValue(),
                        BussinessDictCodeList.费用类型_过户费.getValue()));
                //共通改动太大，单独改
                List<TrdTradeFeeDetail> trdTradeFeeDetails= baseTradeFeeCommService.innerBankFeeDeal(inputDto);
                for(TrdTradeFeeDetail item : trdTradeFeeDetails){
                    TrdTradeFeeAddRateDetail trdTradeFeeAddRateDetail = new TrdTradeFeeAddRateDetail();
                    BeanUtil.copyPropertiesIgnoreNull(trdTradeFeeAddRateDetail,item);
                    retList.add(trdTradeFeeAddRateDetail);
                };
            }
            if (StringUtil.equals(BussinessDictCodeList.交易场所_上海证券交易所.getValue(),
                    dto.getTradeMarket())
                    || StringUtil.equals(BussinessDictCodeList.交易场所_深圳证券交易所.getValue(),
                    dto.getTradeMarket())) {
                inputDto.setSecAcctId(dto.getSecManageAcctId());// 画面.证券账户
                inputDto.setFeeTypeList(Arrays.asList(BussinessDictCodeList.费用类型_佣金.getValue()));
                //共通改动太大，单独改
                List<TrdTradeFeeDetail> trdTradeFeeDetails= baseTradeFeeCommService.bourseFeeDeal(inputDto);
                for(TrdTradeFeeDetail item : trdTradeFeeDetails){
                    TrdTradeFeeAddRateDetail trdTradeFeeAddRateDetail = new TrdTradeFeeAddRateDetail();
                    BeanUtil.copyPropertiesIgnoreNull(trdTradeFeeAddRateDetail,item);
                    retList.add(trdTradeFeeAddRateDetail);
                };
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return retList;
    }
    // -----add by 裴家俊 2019/03/29--------end----

    /**
     * 删除业务流程数据
    *
    * @param workflowId 工作流代码
    * @see
    * @since 1.0
    */
   private void deleteBussFlowData(String workflowId) {
//       // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
//       bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "WORKFLOW_ID",
//           workflowId);
//
//       // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
//       bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD",
//           "WORKFLOW_ID", workflowId);
//
//       // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
//       bussCommonMapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL",
//           "WORKFLOW_ID", workflowId);
//
//       // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
//       bussCommonMapperDelegateService.deleteBySpecificKey("WFL_STL_PAY_ORDERS", "WORKFLOW_ID",
//           workflowId);
       baseTradeProductDealCommService.deleteWflData(workflowId);
   }
    
    /**
     * 发送Murex接口处理
     * 
     * @param entity 回购投资交易信息
     * @param revokeFlag 撤销标识
     * @see
     * @since 1.0
     */
    private void sendMurex(TrdRepoTrade002Dto entity, boolean revokeFlag) {
        if (StringUtil.equals(entity.getIsGuarantee(), DictCodeList.是否_否.getValue())) {
            if (interfComponentService != null) {
                List<Map<String, Object>> sendData = new ArrayList<>();
                Map<String, Object> infoMap = new HashMap<>();

                if (StringUtil.equals(entity.getPs(),
                    DictCodeList.回购拆借方向_正回购.getValue())) {
                    infoMap.put("BUY_BACK_DIR", "borrow");// 回购方向
                }
                else if (StringUtil.equals(entity.getPs(),
                    DictCodeList.回购拆借方向_逆回购.getValue())) {
                    infoMap.put("BUY_BACK_DIR", "lend");// 回购方向
                }

                infoMap.put("EXPIRE_DATE", DateUtil.format(entity.getMdate()));// 到期日

                infoMap.put("INT_EFFECT_DATE", DateUtil.format(entity.getVdate()));// 起息日

                infoMap.put("BUY_BACK_INT_RATE", ArithmeticUtil.divide(entity.getRebackPrice(),
                    BigDecimal.valueOf(100), 8).toPlainString());// 回购利率

                infoMap.put("TRAN_AMT", entity.getPrinAmt().toPlainString());// 交易金额

                infoMap.put("SETTLE_CCY", entity.getCcy());// 结算币种

                // infoMap.put("INTEREST_RULE","borrow");//计息规则
                // infoMap.put("EVAL_DISC_FORMULAE","borrow");//估值折扣计算公式
                // infoMap.put("PLEDGE_BOND_CHANGE_ARRANGE","borrow");//质押券置换安排
                // infoMap.put("PLEDGE_BOND_INFO","borrow");//质押券信息
                // infoMap.put("SETTLE_TYPE","borrow");//结算方式

                infoMap.put("OUTER_TRAN_NO", entity.getTradeId());// 外部交易号

                infoMap.put("INVEST_GROUP", entity.getPortfolioId());// 投组

                if (entity.getTrdInvestPersonInfo() != null) {
                    if (entity.getTrdInvestPersonInfo().getCounterId() != null) {
                        MstCounterParty mstCounterParty = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                            MstCounterPartyMapper.class,
                            entity.getTrdInvestPersonInfo().getCounterId(), MstCounterParty.class);
                        if (mstCounterParty != null) {
                            infoMap.put("COUNTER_PARTY_NAME", mstCounterParty.getCounterName());// 交易对手名称
                        }
                    }
                }

                infoMap.put("DEAL_DATE", DateUtil.format(entity.getTradeDate()));// 成交日

                // infoMap.put("UPPER_SYS","borrow");//上游系统
                // infoMap.put("CLEARING_MODE","borrow");//清算方式

                infoMap.put("TRAN_OPERATION_TYPE", "A – NEW");// 交易操作类型
                if (revokeFlag) {
                    infoMap.put("TRAN_OPERATION_TYPE", "R - Reverse");// 交易操作类型
                }

                // infoMap.put("EVENT_DATE","borrow");//事件日期

                infoMap.put("DEALER_ROLE", entity.getTrdInvestPersonInfo().getTrader());// 交易员

                infoMap.put("REMARK", entity.getRemark());// 备注

                // infoMap.put("APPROVE_NO","borrow");//批复号
                // infoMap.put("SEC_ACCT_NAME","borrow");//资金账户名称
                // infoMap.put("SEC_OPEN_BRANCH","borrow");//资金开户行
                // infoMap.put("SEC_ACCT_NO","borrow");//资金账号

                infoMap.put("EXPIRE_SETTLE_AMT", entity.getMdateSettleAmt().toPlainString());// 到期结算金额

                // infoMap.put("SEC_OPEN_INTERBANK_BRANCH_ID","borrow");//资金开户行联行行号

                sendData.add(infoMap);
                try {
                    interfComponentService.sendMurexRequest(OrderCaller.REPO002, sendData);
                }
                catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }
    }


    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        CfetsTrdRepoTrade002Dto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
        	 try {
//               return commonBusinessService.是否需要上级审批(dto.getCounterId(), dto.getPrinAmt(), LoginUserUtil.getUserName(),
//                       DictCodeList.平安资产类型_质押式回购.getValue(), dto.getRepoType(), dto.getTradeMarket(), dto.getPs()
//                       // 资产类型改为授权类型 add by renping 2019-08-06  start
//                       , null
//                       // 资产类型改为授权类型 add by renping 2019-08-06  end
//                       );
               Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getPrinAmt(),LoginUserUtil.getUserName(),
                   DictCodeList.平安资产类型_质押式回购.getValue(), dto.getRepoType(), dto.getTradeMarket(), dto.getPs()
                   , null,entity.getWorkflowId());
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
//        return null;
    }

    /**
     *
     * 计算交易费用，结算费，佣金
     * @param dto 查询参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    public TrdRepoTrade002Dto calFee(TrdRepoTrade002Dto dto) {
        List<TrdTradeFeeDetail> tradeFeeList = new ArrayList<>();
        TrdRepoTrade002Dto resultDto = new TrdRepoTrade002Dto();
        BasicTradeFeeInputDto inputDto=new BasicTradeFeeInputDto();
        inputDto.setTradeDate(dto.getTradeDate());
        inputDto.setFinprodType2(BussinessDictCodeList.平安资产类型_回购.getValue());
        inputDto.setPs(dto.getPs());
        inputDto.setFpriceAmt(dto.getPrinAmt());
        inputDto.setPrinAmt(dto.getPrinAmt());
        String tradeMarketBank = BussinessDictCodeList.交易场所_银行间.getValue();
        String tradeMarket = dto.getTradeMarket();
        List<String> feeTypeList = new ArrayList<>();
        // 银行间费用设置
        if(StringUtil.equals(tradeMarketBank, tradeMarket)) {
            inputDto.setTradeMarket(tradeMarketBank);
            feeTypeList.add(BussinessDictCodeList.费用类型_交易手续费.getValue());
            feeTypeList.add(BussinessDictCodeList.费用类型_结算手续费.getValue());
            inputDto.setFeeTypeList(feeTypeList);
            // 登记托管机构用证券托管户的登记托管机构
            String registOrg = restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME
                                                         + "/TrdBondInvestTrade/getRegistOrg?secAcctId="
                                                         + dto.getSecAcctId(),
                String.class);
            inputDto.setRegistOrg(registOrg);
            inputDto.setTermType(dto.getTermTypeReback());
            if (dto.getBussRepoSec() != null
//                && dto.getBussRepoSec().getAvaPledgeBondDetailList() != null
//                && dto.getBussRepoSec().getAvaPledgeBondDetailList().size() > 1) {
                    && dto.getBussRepoSec().getPledgeBondList() != null
                    && dto.getBussRepoSec().getPledgeBondList().size() > 1) {
                inputDto.setIsGrade(BussinessDictCodeList.是否_是.getValue());
            }
            else {
                inputDto.setIsGrade(BussinessDictCodeList.是否_否.getValue());
            }
            tradeFeeList = baseTradeFeeCommService.innerBankFeeDeal(inputDto);
        }
        // 交易所费用设置
        else {
            inputDto.setTradeMarket(tradeMarket);
            feeTypeList.add(BussinessDictCodeList.费用类型_佣金.getValue());
            inputDto.setFeeTypeList(feeTypeList);
            inputDto.setSecAcctId(dto.getSecAcctId());
            inputDto.setTermType(dto.getTermTypeReback());
            tradeFeeList = baseTradeFeeCommService.bourseFeeDeal(inputDto);
        }
        resultDto.setTradeFeeList(tradeFeeList);
        return resultDto;
    }

    /**
     * 设置交易费用明细
     *
     * @param entity 查询参数明细
     * @return 费用明细列表
     * @see
     * @since 1.0
     */
    private List<TrdTradeFeeDetail> setTrdFeeDetail(TrdRepoTrade002Dto entity) {
        List<TrdTradeFeeDetail> tradeFeeList = new ArrayList<>();
        if (entity.getSettleAmt() != null){
            TrdTradeFeeDetail settleAmtDetail = getTrdTradeFeeDetail(entity, BussinessDictCodeList.费用类型_结算手续费.getValue());
            tradeFeeList.add(settleAmtDetail);
        }
        if (entity.getProcedureAmt() != null){
            TrdTradeFeeDetail procedureAmtDetail = getTrdTradeFeeDetail(entity, BussinessDictCodeList.费用类型_交易手续费.getValue());
            tradeFeeList.add(procedureAmtDetail);
        }
        if (entity.getCommissionAmt() != null){
            TrdTradeFeeDetail commissionAmtDetail = getTrdTradeFeeDetail(entity, BussinessDictCodeList.费用类型_佣金.getValue());
            tradeFeeList.add(commissionAmtDetail);
        }

        return tradeFeeList;

    }

    /**
     * 获取交易费用详细
     * @param entity 画面输入实体
     * @param feeType 费用类型
     * @return 交易费用详细
     */
    private TrdTradeFeeDetail getTrdTradeFeeDetail(TrdRepoTrade002Dto entity, String feeType){
        TrdTradeFeeDetail amtDetail = new TrdTradeFeeDetail();
        if (StringUtil.equals(feeType, BussinessDictCodeList.费用类型_佣金.getValue())){
            amtDetail.setFeeAmt(entity.getCommissionAmt());
            amtDetail.setFeeRate(entity.getCommissionAmtRate());
            amtDetail.setIsPayWithTrade(entity.getIsPayWithTrade());
        }
        else if (StringUtil.equals(feeType, BussinessDictCodeList.费用类型_交易手续费.getValue())){
            amtDetail.setFeeAmt(entity.getProcedureAmt());
            amtDetail.setFeeRate(entity.getProcedureAmtRate());
            amtDetail.setIsPayWithTrade(BussinessDictCodeList.是否_否.getValue());
        }
        else if (StringUtil.equals(feeType, BussinessDictCodeList.费用类型_结算手续费.getValue())){
            amtDetail.setFeeAmt(entity.getSettleAmt());
            amtDetail.setFeeRate(entity.getSettleAmtRate());
            amtDetail.setIsPayWithTrade(BussinessDictCodeList.是否_否.getValue());
        }
        amtDetail.setFeeType(feeType);
        amtDetail.setCcy(entity.getCcy());
        amtDetail.setTradeId(entity.getTradeId());
        amtDetail.setCreateDept(entity.getCreateDept());
        amtDetail.setCreateTime(entity.getCreateTime());
        amtDetail.setCreateUser(entity.getCreateUser());
        amtDetail.setStatus(entity.getStatus());
        amtDetail.setUpdateTime(entity.getUpdateTime());
        amtDetail.setUpdateUser(entity.getUpdateUser());
        amtDetail.setWorkflowId(entity.getWorkflowId());
        return amtDetail;
    }
    // add by wangguoxian 费用设置 20190801 end
    /**
     * 推算到期日、实际占款天数
     * @return 到期日
     */
    public Map<String,Object> getMdate(Map<String,Object> map){
        String term = (String)map.get("term");
        String vdateString = (String)map.get("vdate");
        String calendarId = (String)map.get("calendarId");
        String mdate = "";
        if(StringUtil.isNotEmpty(vdateString) && StringUtil.isNotEmpty(term)){
            Date vdate;
            vdate = DateUtil.parse(vdateString);
            Date tradeDate = DateUtil.addDay(vdate, Integer.parseInt(term));
            if(StringUtil.isNotEmpty(calendarId) && tradeDate != null){
                map.put("tradeDate", DateUtil.format(tradeDate));
                mdate = this.getNextWorkDay(map);
            }
            mdate = DateUtil.format(tradeDate);
        }
        mdate = null;
        if(StringUtil.isNotEmpty(mdate)){
            map.put("mdate", mdate);
            Long cashholdingdays = DateUtil.getBetweenDate(vdateString, mdate, DurationType.天);
            map.put("cashholdingdays", cashholdingdays);
        }
        return map;
    }
    /**
     * 计算两个日期之间天数
     * @return 日期区间
     */
    public String getBetweenDays(Map<String,Object> map){
        String vdateString = (String)map.get("vdate");
        String mdateString = (String)map.get("mdate");
        Long cashholdingdays = null;
        if(StringUtil.isNotEmpty(vdateString) && StringUtil.isNotEmpty(mdateString)){
            cashholdingdays = DateUtil.getBetweenDate(vdateString, mdateString, DurationType.天);
        }
        return cashholdingdays != null ? cashholdingdays.toString() : null;
    }


    /**
     * 查询债券cstp交易下拉框
     *
     * @param param 输入参数
     * @return
     */
    public Page<SelectOptionDto> queryRepoCstpInfoList(Map<String, Object> param) {
        Page<SelectOptionDto> suggestDtoPage = restTemplate.postForPage(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/queryRepoCstpInfoList", param,
                SelectOptionDto.class);
        return suggestDtoPage;
    }

    /**
     * 根据成交编号查询cstp交易信息
     *
     * @param fedealno 成交编号
     * @return cstp交易信息
     */
    public TrdRepoTradeCSTPDto queryRepoCstpInfoByNo(Map<String,Object> mapInit) {
        String fedealno = (String)mapInit.get("fedealno");
        //isCstpSure = true  由cstp下行交易跳转
        boolean isCstpSure = Boolean.valueOf(mapInit.containsKey("isCstpSure")?(String) mapInit.get("isCstpSure"):"false");
        BigDecimal   zzdPrinAmt = BigDecimal.ZERO;
        BigDecimal   sqsPrinAmt = BigDecimal.ZERO;
        TrdRepoTradeCSTPDto trdRepoTradeCSTPDto = restTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/queryRepoCstpInfoByNo?fedealno="
                        + fedealno
                        + "&loginUserName="
                        + LoginUserUtil.getUserName(),
                TrdRepoTradeCSTPDto.class);
        List<PledgeBondInfoDto> pledgeBondInfoDtoList = new ArrayList<PledgeBondInfoDto>();
        if (trdRepoTradeCSTPDto != null) {
            List<InterfTradRepoasstDto> interfTradRepoasstList = new ArrayList<>();
            //处理匹配数据
            if(isCstpSure){
                interfTradRepoasstList = restTemplate.postForList(
                        ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/getMatchpledgeBondInfo",mapInit,
                        InterfTradRepoasstDto.class);
            }
            else{
                interfTradRepoasstList = restTemplate.getForList(
                        ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/queryRepoAssetByNo?fedealno="
                                + fedealno,
                        InterfTradRepoasstDto.class);
            }
            if (!StringUtil.isEmptyList(interfTradRepoasstList)) {
                String market = trdRepoTradeCSTPDto.getMarket();
                if (StringUtil.equals(trdRepoTradeCSTPDto.getMarket(),
                        BussinessDictCodeList.交易场所_银行间.getValue())) {
                    market = BussinessDictCodeList.wind交易场所_银行间.getValue();
                    trdRepoTradeCSTPDto.setCalendarId("03");
                }
                else if (StringUtil.equals(trdRepoTradeCSTPDto.getMarket(),
                        BussinessDictCodeList.交易场所_上海证券交易所.getValue())) {
                    market = BussinessDictCodeList.wind交易场所_上交所.getValue();
                    trdRepoTradeCSTPDto.setCalendarId("04");
                }
                else if (StringUtil.equals(trdRepoTradeCSTPDto.getMarket(),
                        BussinessDictCodeList.交易场所_深圳证券交易所.getValue())) {
                    market = BussinessDictCodeList.wind交易场所_深交所.getValue();
                    trdRepoTradeCSTPDto.setCalendarId("05");
                }else{
                    trdRepoTradeCSTPDto.setCalendarId("03");
                }
                Map<String,Object> map = new HashMap<>();
                List<String> finprodMarketIdList = new ArrayList<>();
                for (InterfTradRepoasstDto interfTradRepoasst : interfTradRepoasstList) {
                    if(isCstpSure){
                        trdRepoTradeCSTPDto.setPortfolioId(interfTradRepoasst.getPortfolioId());
                        finprodMarketIdList.add(interfTradRepoasst.getFinprodMarketId());
                    }
                    else{
                        finprodMarketIdList.add(interfTradRepoasst.getSecid() + GlobalConstant.POINT + market);
                    }
                }
                if(StringUtil.isNotEmpty(trdRepoTradeCSTPDto.getPortfolioId())){
                    map.put("portfolioId",trdRepoTradeCSTPDto.getPortfolioId());
                    map.put("platDate",StringUtil.isNotEmpty(trdRepoTradeCSTPDto.getDealdateStr())?trdRepoTradeCSTPDto.getDealdateStr():iPlatformDate.getSysDateStr());
                }
                map.put("finprodMarketIdList",finprodMarketIdList);
                map.put("platDate",iPlatformDate.getSysDateStr());
                //处理托管机构
                List<FinProduct> finProducts = restTemplate.postForList(
                        ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/selectFinProduct",map,
                        FinProduct.class);
                String finprodId = StringUtil.EMPTY;
                String finprodMarketId = StringUtil.EMPTY;
                for (InterfTradRepoasst interfTradRepoasst : interfTradRepoasstList) {
                    finprodMarketId = interfTradRepoasst.getSecid() + GlobalConstant.POINT + market;
                    finprodId = DictCodeListExt1.金融产品类型_投管用_债券.getValue()+"_"+finprodMarketId;
                    PledgeBondInfoDto pledgeBondInfoDto = new PledgeBondInfoDto();
                    pledgeBondInfoDto.setBondCode(finprodMarketId);
                    pledgeBondInfoDto.setPledgeRatio(ArithmeticUtil.multiply(interfTradRepoasst.getConvrate(),new BigDecimal(100)));
                    pledgeBondInfoDto.setPledgePrin(interfTradRepoasst.getFacetotalamt());
                    //交易匹配提交交易 不需要乘以折算率
                    BigDecimal pledgePrin =  ArithmeticUtil.multiply(pledgeBondInfoDto.getPledgePrin(),interfTradRepoasst.getConvrate());
                    pledgeBondInfoDto.setFirstNetPrice(new BigDecimal(100));
                    if(StringUtil.isNotEmpty(finProducts)){
                        for(FinProduct item : finProducts){
                            if(StringUtil.equals(item.getFinprodId(),finprodId)){
                                if(StringUtil.equals(item.getRegistOrg(),DictCodeList.登记托管机构_中央国债登记结算有限责任公司.getValue())){
                                    zzdPrinAmt = ArithmeticUtil.doAdd(zzdPrinAmt,pledgePrin);
                                }
                                if(StringUtil.equals(item.getRegistOrg(),DictCodeList.登记托管机构_上海清算所.getValue())){
                                    sqsPrinAmt = ArithmeticUtil.doAdd(sqsPrinAmt,pledgePrin);
                                }
                                pledgeBondInfoDto.setAvaPledgePrin(item.getIssueAmt());
                                pledgeBondInfoDto.setFirstNetPrice(null!=item.getIssuePrice()?item.getIssuePrice():new BigDecimal(100));
                            }
                        }
                    }
                    pledgeBondInfoDto.setBondAbbrName(interfTradRepoasst.getSecname());
                    pledgeBondInfoDto.setFinprodId(finprodId);
                    pledgeBondInfoDtoList.add(pledgeBondInfoDto);
                    finprodMarketIdList.add(pledgeBondInfoDto.getBondCode());
                }
                trdRepoTradeCSTPDto.setPledgeBondInfoDtoList(pledgeBondInfoDtoList);
                trdRepoTradeCSTPDto.setZzdPrinAmt(zzdPrinAmt);
                trdRepoTradeCSTPDto.setSqsPrinAmt(sqsPrinAmt);
                trdRepoTradeCSTPDto.setFacetotalamt(ArithmeticUtil.doAdd(zzdPrinAmt,sqsPrinAmt));
            }
            //清算速度处理
            if(!StringUtil.equals(trdRepoTradeCSTPDto.getVdateStr(),trdRepoTradeCSTPDto.getDealdateStr())){
                trdRepoTradeCSTPDto.setSettleSpeed(DictCodeList.清算速度_T_1.getValue());
            }else{
                trdRepoTradeCSTPDto.setSettleSpeed(DictCodeList.清算速度_T_0.getValue());
            }
        }
        return trdRepoTradeCSTPDto;
    }

    @Override
    public void doBack(CfetsTrdRepoTrade002Dto entity) {
        //uodateCstpMessage(entity.getWorkflowId());
        //AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTradeId());
        
        //删除授信占用
        if (null != entity.getTotleTradeId() && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTotleTradeId());
        }
       // doBackUpdateEtrPosition(entity);
    }

    @Override
    public void doRevoke(CfetsTrdRepoTrade002Dto entity) {
        uodateCstpMessage(entity.getWorkflowId());
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTradeId());
        
        //删除授信占用
        if (null != entity.getTradeId() && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }

    /**
     * 查询债券信息
     *
     * @param param 输入参数
     * @return
     */
    public FinProduct querySecinfo(Map<String, Object> param) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/querySecinfo", param,
                FinProduct.class);
    }

    public Boolean isAllTradeAmtByFTradeId(Map<String, Object> map){
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/isAllTradeAmtByFTradeId",
                map, Boolean.class);
    }

    public Boolean isAllTradeAmtByPreTradeId(Map<String, Object> map){
        String preTradeId = map.get("preTradeId").toString();
        String tradeId = map.get("tradeId").toString();
        if(StringUtil.isNotBlank(preTradeId)&&StringUtil.isNotBlank(tradeId)){
            TrdProductDealPreKey key = new TrdProductDealPreKey();
            key.setTradeId(preTradeId);
            key.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            TrdProductDealPre trdProductDealPre = trdProductDealPreMapper.selectByPrimaryKey(key);
            InterfOpicsTradRepo interfOpicsTradRepo = interfOpicsTradRepoMapper.selectByPrimaryKey(tradeId);
            if(interfOpicsTradRepo!=null&&trdProductDealPre!=null){
                if(interfOpicsTradRepo.getRepoamt().compareTo(trdProductDealPre.getTradeAmt())==0){
                    return false;
                }else{
                    return true;
                }
            }
        }
        return false;
    }

    public FinProduct getFinProduct(String finprodMarketId){
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/getFinProduct",
                finprodMarketId, FinProduct.class);
    }

    public BigDecimal get融入交易融入的券面总额(Map<String, Object> params) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/queryLendinQty", params,
                BigDecimal.class);
    }

    /* add by msk 新增查询通道划款交易 20201111 start */
    /**
     * 查找通道划款交易
     *
     * @param map 交易编号
     * @return 通道划款交易信息
     * @see
     * @since 1.0
     */
    public int selectchlTrade(Map<String,Object> map) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/selectchlTrade/", map,
                Integer.class);
    }
    /* add by msk 新增查询通道划款交易 20201111 end */
/**
     *功能描述 : 成交明细自动生成交易
     * @author zhangnian
     * @date 2021/1/21 0021
     * @param  * @param entity
     * @return void
    */
    public Map<String,Object> repoAutoTrade(TrdRepoTrade002Dto entity){
        Map<String,Object> resultMap = makeRepoTradeDto(entity);
        List<TrdRepoTrade002Dto> trdRepoTrade002s =   (List<TrdRepoTrade002Dto>)resultMap.get("trdRepoTrade002s");
        if(!StringUtil.isEmptyList(trdRepoTrade002s)){
            trdRepoTrade002s.stream().forEach(item -> {
                prepareEntity(item);
                saveAutoTrade(item);
            });
        }
        return resultMap;
    }


    public void saveAutoTrade(TrdRepoTrade002Dto entity){
        // 7.1登录各业务表，包括金融产品交易、金融产品附属、划款指令及附件信息
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易表
        entity.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

        // 金融产品交易扩展表Entity -> 金融产品交易扩展表
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, entity.getWflPaTrdProductDeal());

        // 组合交易表Entity -> 组合交易信息表
        List<PtlTrade> ptlTrades = entity.getPtlTrade();
//          转换
        paTrdProductDealMapper.insertSelective(paTrdProductDeal);

        // 批量插入
        if (CollectionUtils.isNotEmpty(ptlTrades)) {
            ptlTrades.forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
        }
        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtoList = bussTrdTransferOrderService.save(
                entity.getBussTrdTransferOrder());

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
        StlPayOrdersDto stlPayOrder = null;
        List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
        if (!StringUtil.isEmptyList(bussTrdTransferOrderDtoList)) {
            for (BussTrdTransferOrderDto bttod : bussTrdTransferOrderDtoList) {
                stlPayOrder = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrder, bttod);
                stlPayOrders.add(stlPayOrder);
            }
        }

        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());

        List<BaseTradeProductDealDto> lstPostion = new ArrayList<>();

        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        //设置预录入的成交编号
        if(StringUtil.isNotBlank(entity.getPreTradeId())){
            baseTradeProductDealDto.setPreTradeId(entity.getPreTradeId());
        }
        if(StringUtil.isNotBlank(entity.getfTradeId())){
            baseTradeProductDealDto.setfTradeId(entity.getfTradeId());
        }
        // 金融产品交易
        baseTradeProductDealDto.setTrdProductDeal(entity.getTrdProductDeal());
        // 金融产品附属
        baseTradeProductDealDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
        // 划款指令
        if (stlPayOrders != null) {
            baseTradeProductDealDto.setStlPayOrdersList(stlPayOrders);
        }
        // add by wangguoxian 费用设置 20190722 start
        List<TrdTradeFeeDetail> feeDetailList = setTrdFeeDetail(entity);
        if (feeDetailList != null) {
            baseTradeProductDealDto.setTrdTradeFeeDetailList(feeDetailList);
        }
        baseTradeProductDealDto.setEntrustId(entity.getEntrustId());

        // add by wangguoxian 费用设置 20190722 end
        finProductTradeDto.setAssetType2(entity.getRepoType());
        finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
        // 撤销时，子组合需要调估值共通
        if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                finProductTrade.setActPortfolioId(entity.getActPortfolioId());
            });
        }
        // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
        finProductTradeDto.setBussRepoSec(entity.getBussRepoSec());
        
        // add by etliu 20210128 start
        // 正常交易录单，买入的指令现金和卖出的指令持仓是在流程发起时处理的，所以下行交易自动生效时，需要单独调用指令可用持仓处理逻辑
        if (StringUtil.isNotBlank(entity.getQuoteId())) {
            baseTradeProductDealDto.setQuoteId(entity.getQuoteId());

            if (StringUtil.isNotEmpty(finProductTradeDto.getBussRepoSec())) {
                baseTradeProductDealDto.setBussRepoSec(finProductTradeDto.getBussRepoSec());
            }
            baseEtrCashPositionCommService.executeEtrCashPosition(baseTradeProductDealDto,
                DictCodeList.持仓类型_可用, false, finProductTradeDto.getIeFlag(),
                finProductTradeDto.getIsOverdueDeal());
        }
        // add by etliu 20210128 end
        
        baseTradeProductDealCommService.execute(finProductTradeDto);

        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
        }
        //查询是否存在预交易
        if (StringUtils.isNotEmpty(entity.getTrdProductDeal().getfTradeId())) {
            Map<String, Object> paramPre = new HashMap<String, Object>();
            paramPre.put("CSTP_TRADE_ID", entity.getTrdProductDeal().getfTradeId());
            // 获预交易编号
            List<TrdMatchInfo> preTradeIdList = restTemplate.postForList(
                    ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/selectPreTradeId", paramPre,
                    TrdMatchInfo.class);
            if (preTradeIdList != null && preTradeIdList.size() > 0) {
                for (TrdMatchInfo trdMatchInfo : preTradeIdList) {
                    TrdProductDealPreKey key = new TrdProductDealPreKey();
                    key.setTradeId(trdMatchInfo.getPreTradeId());
                    key.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                    TrdProductDealPre trdProductDealPre = trdProductDealPreMapper.selectByPrimaryKey(key);
                    if (trdProductDealPre != null) {
                        // 处理组合及指令头寸
                        PreTrdCommonDto preTrdCommonDto = new PreTrdCommonDto();
                        List<TrdProductDealPre> listTrdProductDealPre = new ArrayList<TrdProductDealPre>();
                        listTrdProductDealPre.add(trdProductDealPre);
                        List<TrdProductDealAddPre> listTrdProductDealAddPre = new ArrayList<TrdProductDealAddPre>();
                        TrdProductDealAddPre trdProductDealAddPre = trdProductDealAddPreMapper
                                .selectByPrimaryKey(trdMatchInfo.getPreTradeId());
                        listTrdProductDealAddPre.add(trdProductDealAddPre);
                        preTrdCommonDto.setTrdProductDealPreList(listTrdProductDealPre);
                        preTrdCommonDto.setTrdProductDealAddPreList(listTrdProductDealAddPre);
                        preTrdCommonBaseService.excutePosition(preTrdCommonDto, true);
                    }
                }
            }
        }

        // 通道交易 新增 强成西 2019/03/26 start
        TrdTradeFeeDetail trdTradeFeeDetail = null;
        if (StringUtil.isNotEmpty(entity.getTrdProductDeal().getChlId())) {
            // 交易费用明细
            trdTradeFeeDetail = AllTradeCommonUtil.genChannelTradeFeeDetail(entity.getTrdProductDeal().getTradeId(), entity.getChannelFee());
            trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
        }
        // 通道交易 新增 强成西 2019/03/26 end

        // add by wangpeng 获取估值分类123 start
        ValItemValueGenUtil.genTypeForFinProductType(entity.getFinTradeProduct(), entity.getFinProductType());
        // add by wangpeng end
        finTradeProductMapper.insertSelective(entity.getFinTradeProduct());
        // 7.4 写入资产信息进交易类金融产品表
        finTradeProductAddMapper.insertSelective(entity.getFinTradeProductAdd());
        // 7.5写入回购类型进金融产品分类表
        finProductTypeMapper.insertSelective(entity.getFinProductType());

        // 7.6写入现金流相关信息表
        this.cashFlowLogic(entity);

        // 7.7写入回购质押品信息
        bussRepoSecService.insert(entity.getBussRepoSec(), entity.getTradeId());



        // 写入质押品的组合持仓流水
        this.ptlSecPositionFlowLogic(entity, false);
        // 风险试算
        //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());
        //成交明细
        //不修改共通---报价生成的可用头寸  单独删除处理
        if(StringUtil.isNotEmpty(entity.getQuoteId())){
            deletePtlPositionFlow(entity.getQuoteId());
        }
    }

    public Map<String,Object> makeRepoTradeDto(TrdRepoTrade002Dto trdRepoTrade002Dto){
        Map<String,Object> resultMap = new HashMap<>();
        List<TrdRepoTrade002Dto> trdRepoTrade002s = new ArrayList<>();
        String ps = trdRepoTrade002Dto.getPs();
        trdRepoTrade002Dto.setAutoTradeFlag(true);
        // 通过成交单编号查询交易数据
        TrdRepoTradeCSTPDto repoCstpInfo = new TrdRepoTradeCSTPDto();
        // 获取质押券信息
        List<PledgeBondInfoDto> pledgeBondInfoDtoList = new ArrayList<>();
        //是否多组合
        List<PtlAllocationDto> ptlAllocationDtos = new ArrayList<>();
        //输出
        List<PledgeBondInfoDto> pledgeBondInfoDtoResultList = new ArrayList<>();
        if(null!=trdRepoTrade002Dto.getInterfOpicsTradRepo()){
            BeanUtil.copyPropertiesIgnoreNull(repoCstpInfo,trdRepoTrade002Dto.getInterfOpicsTradRepo());
        }
        //到期结算金额
        BigDecimal mdateSettleAmt = repoCstpInfo.getMdealamt();
        //本金金额
        BigDecimal prinAmt = repoCstpInfo.getRepoamt();
        //质押券差费后按委托编号分组
        Map<String,List<PledgeBondInfoDto>> mapPledgeBondInfoDtoByEntrustId = new HashMap<>();
        trdRepoTrade002Dto.setMdateSettleAmt(repoCstpInfo.getMdealamt());
        if(null!=trdRepoTrade002Dto.getInterfTradRepoassts()&&!trdRepoTrade002Dto.getInterfTradRepoassts().isEmpty()){
            for(InterfTradRepoasstDto item : trdRepoTrade002Dto.getInterfTradRepoassts()){
                PledgeBondInfoDto pledgeBondInfoDto = new PledgeBondInfoDto();
                pledgeBondInfoDto.setFinprodId(item.getFinprodId());
                pledgeBondInfoDto.setBondCode(item.getSecid()+".IB");
                pledgeBondInfoDto.setBondAbbrName(item.getSecname());
                pledgeBondInfoDto.setAvaPledgePrin(item.getFacetotalamt());
                pledgeBondInfoDto.setPledgePrin(item.getFacetotalamt());
                pledgeBondInfoDto.setFirstNetPrice(item.getCprice());
                //共通里除以了100，不改共通
                pledgeBondInfoDto.setPledgeRatio(ArithmeticUtil.multiply(item.getConvrate(),new BigDecimal(100)));
                //证券账户处理
                if(StringUtil.isNotEmpty(trdRepoTrade002Dto.getPortfolioId())&&StringUtil.isNotEmpty(item.getFinprodId())){
                    Map<String,Object> secManageAcctIdMap = new HashMap<>();
                    secManageAcctIdMap.put("portfolioId",trdRepoTrade002Dto.getPortfolioId());
                    secManageAcctIdMap.put("finprodId",item.getFinprodId());
                    pledgeBondInfoDto.setSecAcctId(qryPortfolioSecAcctId(secManageAcctIdMap));
                }
                pledgeBondInfoDtoList.add(pledgeBondInfoDto);
            };
        }
        //结算手续费  交易手续费计算
        // 构建对象计算手续费和结算费
        List<TrdTradeFeeDetail> trdTradeFeeDetails = new ArrayList<>();
        TrdRepoTrade002Dto feeDataInputDto = new TrdRepoTrade002Dto();
        feeDataInputDto.setFpriceAmt(prinAmt);
        feeDataInputDto.setTradeDate(repoCstpInfo.getDealdate());
        feeDataInputDto.setPs(ps);
        feeDataInputDto.setTermTypeReback(repoCstpInfo.getSecid());
        feeDataInputDto.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());
        feeDataInputDto.setSecAcctId(repoCstpInfo.getOurdepositaccountnumber());
        feeDataInputDto.setSecManageAcctId(repoCstpInfo.getOurdepositaccountnumber());
        if (pledgeBondInfoDtoList.size() > 1) {
            feeDataInputDto.setIsGrade("2");
        } else if (pledgeBondInfoDtoList.size() == 0) {
            feeDataInputDto.setIsGrade("0");
        } else {
            feeDataInputDto.setIsGrade("1");
        }
        List<TrdTradeFeeAddRateDetail> feeDataInfo = calculateFeeData(feeDataInputDto);
        trdTradeFeeDetails.addAll(feeDataInfo);
        if(!StringUtil.isEmptyList(trdTradeFeeDetails)){
            trdTradeFeeDetails.forEach(item -> {
                if(StringUtils.equals(item.getFeeType(),BussinessDictCodeList.费用类型_交易手续费.getValue())){
                    resultMap.put("settFeeamt",item.getFeeAmt());
                    return;
                }
                if(StringUtils.equals(item.getFeeType(),BussinessDictCodeList.费用类型_结算手续费.getValue())){
                    resultMap.put("dealFeeamt",item.getFeeAmt());
                    return;
                }
            });
        }
        if(StringUtil.isNotEmpty(trdRepoTrade002Dto.getQuoteId())){
            ptlAllocationDtos =  cstpCommonService.selectEtrReByQuoteId(trdRepoTrade002Dto.getQuoteId());
            //多组合
            if(!StringUtil.isEmptyList(ptlAllocationDtos)){
                Map<String,Object> map = new HashMap<>();
                map.put("shareAmt",prinAmt);
                map.put("fpriceAmt",mdateSettleAmt);
                ptlAllocationDtos = cstpCommonService.组合分配拆分(ptlAllocationDtos,map);
                map.put("trdTradeFeeDetails",trdTradeFeeDetails);
                ptlAllocationDtos = cstpCommonService.交易费用拆分(ptlAllocationDtos,map);
                CfetsRepoDealQuotedPriceDto cfetsRepoDealQuotedPriceDto = new CfetsRepoDealQuotedPriceDto();
                cfetsRepoDealQuotedPriceDto.setIsDzh("Y");
                cfetsRepoDealQuotedPriceDto.setPs(ps);
                cfetsRepoDealQuotedPriceDto.setPtlAllocationList(ptlAllocationDtos);
                if(!StringUtil.isEmptyList(pledgeBondInfoDtoList)){
                    List<CfetsRepoPledgeBondDto> cfetsRepoPledgeBonds = new ArrayList<>();
                    pledgeBondInfoDtoList.stream().forEach(item -> {
                        CfetsRepoPledgeBondDto cfetsRepoPledgeBondDto = new CfetsRepoPledgeBondDto();
                        cfetsRepoPledgeBondDto.setFinprodId(item.getFinprodId());
                        cfetsRepoPledgeBondDto.setNetPrice(item.getFirstNetPrice());
                        cfetsRepoPledgeBondDto.setPledgeRate(item.getPledgeRatio());
                        cfetsRepoPledgeBondDto.setPledgePrin(item.getPledgePrin());
                        cfetsRepoPledgeBonds.add(cfetsRepoPledgeBondDto);
                    });
                    cfetsRepoDealQuotedPriceDto.setPledgeBondList(cfetsRepoPledgeBonds);
                }
                //下行成交报文回来  直接取分配表信息 不需要重新拆分质押券
                //List<IbuQuoteEntCrepPledgeCommonDto> ibuQuoteEntCrepPledgeCommons = cstpCommonService.质押券拆分(cfetsRepoDealQuotedPriceDto);
                List<IbuQuoteEntCrepPledgeCommonDto> ibuQuoteEntCrepPledgeCommons = cstpCommonService.listIbuQuoteEntCrepPledge(trdRepoTrade002Dto.getQuoteId());
                //质押券差费后按委托编号分组
                if(!StringUtil.isEmptyList(ibuQuoteEntCrepPledgeCommons)){
                    Map<String, List<IbuQuoteEntCrepPledgeCommonDto>> mapByEntrustId = ibuQuoteEntCrepPledgeCommons.stream().collect(Collectors.groupingBy(IbuQuoteEntCrepPledgeCommonDto::getEntrustId));
                    for(PtlAllocationDto ptlAllocationDto:ptlAllocationDtos){
                        List<PledgeBondInfoDto> pledgeBondInfoDtoListdzh = new ArrayList<>();
                        List<IbuQuoteEntCrepPledgeCommonDto> ibuQuoteEntCrepPledgeCommons2 = (List<IbuQuoteEntCrepPledgeCommonDto>)mapByEntrustId.get(ptlAllocationDto.getEntrustId());
                        for(IbuQuoteEntCrepPledgeCommonDto item:ibuQuoteEntCrepPledgeCommons2){
                            PledgeBondInfoDto pledgeBondInfoDto = new PledgeBondInfoDto();
                            pledgeBondInfoDto.setFinprodId(item.getFinprodId());
                            pledgeBondInfoDto.setBondCode(item.getBondCode());
                            BigDecimal pledgeNumber = ArithmeticUtil.multiply(item.getPledgeNumber(),new BigDecimal(100));
                            pledgeBondInfoDto.setAvaPledgePrin(pledgeNumber);
                            pledgeBondInfoDto.setPledgePrin(pledgeNumber);
                            pledgeBondInfoDto.setFirstNetPrice(item.getUnitCprice());
                            //共通里除以了100，不改共通
                            pledgeBondInfoDto.setPledgeRatio(ArithmeticUtil.multiply(item.getPledgeRatio(),new BigDecimal(100)));
                            //证券账户处理
                            if(StringUtil.isNotEmpty(trdRepoTrade002Dto.getPortfolioId())&&StringUtil.isNotEmpty(item.getPledgeFinprodId())){
                                Map<String,Object> secManageAcctIdMap = new HashMap<>();
                                secManageAcctIdMap.put("portfolioId",ptlAllocationDto.getPortfolioId());
                                secManageAcctIdMap.put("finprodId",item.getPledgeFinprodId());
                                pledgeBondInfoDto.setSecAcctId(qryPortfolioSecAcctId(secManageAcctIdMap));
                            }
                            pledgeBondInfoDto.setEntrustId(ptlAllocationDto.getEntrustId());
                            pledgeBondInfoDtoListdzh.add(pledgeBondInfoDto);
                            pledgeBondInfoDtoResultList.addAll(pledgeBondInfoDtoListdzh);
                        };
                        mapPledgeBondInfoDtoByEntrustId.put(ptlAllocationDto.getEntrustId(),pledgeBondInfoDtoListdzh);
                    };
                }
            }
            //单组合
            else{
                PtlAllocationDto ptlAllocationDto = new PtlAllocationDto();
                ptlAllocationDto.setEntrustId(trdRepoTrade002Dto.getEntrustId());
                ptlAllocationDto.setFpriceAmt(mdateSettleAmt);
                ptlAllocationDto.setShareAmt(prinAmt);
                ptlAllocationDto.setPortfolioId(trdRepoTrade002Dto.getPortfolioId());
                ptlAllocationDtos.add(ptlAllocationDto);
                ptlAllocationDto.setTradeFeeList(trdTradeFeeDetails);
                mapPledgeBondInfoDtoByEntrustId.put(trdRepoTrade002Dto.getEntrustId(),pledgeBondInfoDtoList);
            }
        }
        for(PtlAllocationDto item : ptlAllocationDtos){
            TrdRepoTrade002Dto entity = new TrdRepoTrade002Dto();
            BeanUtil.copyPropertiesIgnoreNull(entity,trdRepoTrade002Dto);
            entity.setPortfolioId(item.getPortfolioId());
            entity.setEntrustId(item.getEntrustId());
            //到期结算金额
            BigDecimal mdateSettleAmt2 = item.getFpriceAmt();
            //本金金额
            BigDecimal prinAmt2 = item.getShareAmt();
            List<PledgeBondInfoDto> pledgeBondInfoDtoByEntrustIdList = new ArrayList<>();
            pledgeBondInfoDtoByEntrustIdList = (List<PledgeBondInfoDto>)mapPledgeBondInfoDtoByEntrustId.get(item.getEntrustId());
            //中债登交易金额
            entity.setZzdPrinAmt(entity.getCbFpriceAmt());
            //上清交易金额
            entity.setSqsPrinAmt(entity.getScFpriceAmt());
            TrdProductDeal deal = new TrdProductDeal();
            if(StringUtil.equals(entity.getPs(),DictCodeList.回购拆借方向_正回购.getValue())){
                entity.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券质押式正回购.getValue());
                deal.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券质押式正回购.getValue());
            }
            else if(StringUtil.equals(entity.getPs(),DictCodeList.回购拆借方向_逆回购.getValue())){
                entity.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券质押式逆回购.getValue());
                deal.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券质押式逆回购.getValue());
            }
            // 构建对象计算手续费和结算费
            /*TrdRepoTrade002Dto feeDataInputDto = new TrdRepoTrade002Dto();
            feeDataInputDto.setFpriceAmt(prinAmt2);
            feeDataInputDto.setTradeDate(repoCstpInfo.getDealdate());
            feeDataInputDto.setPs(entity.getPs());
            feeDataInputDto.setTermTypeReback(repoCstpInfo.getSecid());
            feeDataInputDto.setTradeMarket(repoCstpInfo.getMarket());
            feeDataInputDto.setSecAcctId(repoCstpInfo.getOurdepositaccountnumber());
            feeDataInputDto.setSecManageAcctId(repoCstpInfo.getOurdepositaccountnumber());
            if (pledgeBondInfoDtoByEntrustIdList.size() > 1) {
                feeDataInputDto.setIsGrade("2");
            } else if (pledgeBondInfoDtoByEntrustIdList.size() == 0) {
                feeDataInputDto.setIsGrade("0");
            } else {
                feeDataInputDto.setIsGrade("1");
            }
            List<TrdTradeFeeAddRateDetail> feeDataInfo = calculateFeeData(feeDataInputDto);*/
            if(!StringUtil.isEmptyList(item.getTradeFeeList())){
                for (TrdTradeFeeDetail fee : item.getTradeFeeList()) {
                    TrdTradeFeeAddRateDetail trdTradeFeeAddRateDetail = new TrdTradeFeeAddRateDetail();
                    BeanUtil.copyPropertiesIgnoreNull(trdTradeFeeAddRateDetail,item);
                    // B08 结算费
                    if (StringUtil.equals(BussinessDictCodeList.费用类型_结算手续费.getValue(), fee.getFeeType())) {
                        entity.setSettleAmt(fee.getFeeAmt());
                    }
                    // B06 交易手续费
                    if (StringUtil.equals(BussinessDictCodeList.费用类型_交易手续费.getValue(), fee.getFeeType())) {
                        entity.setProcedureAmt(fee.getFeeAmt());
                    }
                }
            }
            // 银行间成交编号：EXCEL.委托批号
            entity.setfTradeId(entity.getfTradeId());
            // 交易编号
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            if (StringUtil.equals(entity.getPs(), BussinessDictCodeList.回购拆借方向_正回购.getValue())) {
                codeParameter.setCommonBusinessType(BussinessDictCodeList.共同业务类型_正回购录入);
            }
            else {
                codeParameter.setCommonBusinessType(BussinessDictCodeList.共同业务类型_逆回购录入);
            }
            entity.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            // 交易场所
            entity.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());
            // 交易日历
            entity.setCalendarId("03");
            // 证券托管户
            entity.setSecAcctId(repoCstpInfo.getOurdepositaccountnumber());
            // 回购类型：默认“质押式协议回购”
            //entity.setRepoType(BussinessDictCodeList.回购类型_质押式协议回购.getValue());
            entity.setRepoType(DictCodeList.回购类型_质押式回购.getValue());
            // 清算速度 T+0
            entity.setSettleSpeedStr(BussinessDictCodeList.清算速度_T加0.getValue());
            // 交易日
            entity.setTradeDate(repoCstpInfo.getDealdate());
            // 期限品种
            entity.setTermTypeReback(repoCstpInfo.getTermType());
            // 起息日
            entity.setVdate(repoCstpInfo.getVdate());
            entity.setVpayDate(repoCstpInfo.getVdate());
            // 到期日
            entity.setMdate(repoCstpInfo.getMdate());
            entity.setMpayDate(repoCstpInfo.getMdate());
            // 期限
            entity.setTerm(repoCstpInfo.getTerm());
            // 实际占款天数
            entity.setActCapDays(repoCstpInfo.getCashHoldingDays().longValue());
            // 本金金额
            entity.setPrinAmt(prinAmt2);
            // 币种(默认人民币)
            entity.setCcy(BussinessDictCodeList.币种_人民币.getValue());
            // 回购利率%
            entity.setRebackPrice(ArithmeticUtil.multiply(repoCstpInfo.getReporate(),new BigDecimal(100)));
            // 计息基础
            entity.setBasis(repoCstpInfo.getBasis());
            // 到期结算金额
            entity.setMdateSettleAmt(mdateSettleAmt2);
            // 回购利息
            entity.setRebackIntAmt(ArithmeticUtil.subtract(mdateSettleAmt2,prinAmt2));
            // 交易金额
            entity.setTradeRepAmt(prinAmt2);
            //交易对手
            //entity.setCounterId(repoCstpInfo.getSeller());
            entity.setImportFlag(false);

            // 交易对手
            // 判断交易对手是否存在,不存在则新建
            TrdInvestPersonInfo002Dto personInfo = new TrdInvestPersonInfo002Dto();
            personInfo.setCounterId(entity.getCounterId());
            //personInfo.setTrader(StringUtil.isNotEmpty(repoCstpInfo.getBuytrader()) ? repoCstpInfo.getBuytrader() : LoginUserUtil.getUserName());
            personInfo.setTrader(entity.getTrader());
            personInfo.setCounterTrader(entity.getCounterTrader());
            personInfo.setIsOutTrade(BussinessDictCodeList.是否_是.getValue());
            personInfo.setContactInfo(repoCstpInfo.getCountertelephone());
            entity.setTrdInvestPersonInfo(personInfo);

            // 画面相关
            entity.setPageModel(GlobalConstant.DETAIL_PAGE_MODEL_NEW);
            entity.setOperateType("submit");
            entity.setTodoListFlag("false");

            List<BussTrdTransferOrderDto> bussTrdTransferOrder = new ArrayList<>();
            BussSettleOrderParameterDto bussSettleOrderParameterDto = new BussSettleOrderParameterDto();
            bussSettleOrderParameterDto.setIsTotalTrade(BussinessDictCodeList.是否_否.getValue());
            bussSettleOrderParameterDto.setBankLendingType(null);
            bussSettleOrderParameterDto.setPortfolioId(entity.getPortfolioId());
            deal.setSettleDate(entity.getSettleDate());
            deal.setTradeDate(entity.getTradeDate());
            deal.setPs(BussinessDictCodeList.交易方向_买入.getValue());
            deal.setBranch(Long.valueOf(0));
            deal.setCcy(entity.getCcy());
            deal.setTradeAmt(entity.getTradeRepAmt());
            deal.setCounterId(repoCstpInfo.getSeller());
            deal.setTradeMarket(entity.getTradeMarket());
            deal.setTradePlat(entity.getTradePlat());
            deal.setSecManageAcctId(repoCstpInfo.getOurdepositaccountnumber());
            deal.setTradeMarketMode(entity.getTradeMarketMode());
            deal.setFinprodType2(entity.getFinprodType2());
            bussSettleOrderParameterDto.setDeal(deal);
            BussTrdTransferOrderDto bussTrdTransferOrderDto = bussTrdTransferOrderService
                    .generateOrder(bussSettleOrderParameterDto);
            bussTrdTransferOrderDto.setPayAcctNoList(null);
            bussTrdTransferOrderDto.setRecAcctNoList(null);
            /*if (StringUtil.equals(BussinessDictCodeList.账户来源_自营.getValue(), repoTradeDto.getSecAcctSource())) {
                bussTrdTransferOrderDto.setIsNeedSend(BussinessDictCodeList.是否_是.getValue());
            } else {
                bussTrdTransferOrderDto.setIsNeedSend(BussinessDictCodeList.是否_否.getValue());
            }*/
            bussTrdTransferOrder.add(bussTrdTransferOrderDto);
            entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(bussTrdTransferOrder));

            // 质押品信息
            BussRepoSecDto repoSecDto = new BussRepoSecDto();
            repoSecDto.setPs(repoCstpInfo.getPs());
            repoSecDto.setExchangePledge("N");
            repoSecDto.setPageFrom("TrdRepoTrade002");
            // 汇总质押券的全面总额
            BigDecimal totalPrimAmt = BigDecimal.ZERO;
            if(null!=pledgeBondInfoDtoByEntrustIdList&&!StringUtil.isEmptyList(pledgeBondInfoDtoByEntrustIdList)){
                for (PledgeBondInfoDto pledgeBond : pledgeBondInfoDtoByEntrustIdList) {
                    totalPrimAmt = totalPrimAmt.add(pledgeBond.getPledgePrin());
                }
            }
            repoSecDto.setTotalPrinAmt(totalPrimAmt);
            repoSecDto.setPledgeBondList(pledgeBondInfoDtoByEntrustIdList);
            entity.setBussRepoSec(repoSecDto);

            // 额度验证结果
            entity.setCreditList(getResult(entity));
            trdRepoTrade002s.add(entity);
        };
        resultMap.put("trdRepoTrade002s",trdRepoTrade002s);
        resultMap.put("ptlAllocationDtos",ptlAllocationDtos);
        resultMap.put("pledgeBondInfoDtos",pledgeBondInfoDtoResultList);
        return resultMap;
    }

    /**
     *功能描述:处理报价流水
     * @author zhangnian
     * @date 2021/1/21 0021
     * @param  * @param quoteId
     * @return void
    */
    public void deletePtlPositionFlow(String quoteId){
        baseAssetCashPositionCommMapper.deleteCashPositionByOrderId(quoteId);
        baseAssetCashPositionCommMapper.deletePositionByTradeId(quoteId);
    }

    /**ma
     *功能描述 获取证券账户
     */
    private String qryPortfolioSecAcctId(Map<String,Object> param){
        return  restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/qryPortfolioSecAcctId", param,
                String.class);
    }

    /**
     * 调用可用额度查询接口（将返回实体类加入到流程中）
     *
     * @param inputDto 交易实体类
     * @return 返回结果
     * @see
     * @since 1.0
     */
    public CreditAmtSelDto getResult(TrdRepoTrade002Dto inputDto) {
        if (this.checkBankAmtLimt(inputDto.getSecAcctId(), inputDto.getPs(), inputDto.getSecAcctSource(),inputDto.getTradeMarket())) {
            if (StringUtil.isNotEmpty(inputDto.getBussRepoSec())
                    && !StringUtil.isEmptyList(inputDto.getBussRepoSec().getPledgeBondList())) {
                List<CreditAmtSelDto> dtoList = new ArrayList<>();
                for (PledgeBondInfoDto bondDto : inputDto.getBussRepoSec().getPledgeBondList()) {
                    CreditAmtSelDto creDtoTemp = new CreditAmtSelDto();
                    creDtoTemp.setIblsClientNo(bondDto.getBondCode());
                    creDtoTemp.setSecId(bondDto.getBondCode());
                    dtoList.add(creDtoTemp);
                }

                CreditAmtSelDto dto = new CreditAmtSelDto();
                dto.setCreditList(dtoList);
                dto.setInterfType("1");
                try {
                    return interfComponentService.creditAmtSel(dto);
                }
                catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 判断是否同业限额占用
     *
     * @param secAcctId 证券托管户
     * @param ps 交易方向
     * @param secAcctSource 账户来源
     * @return 是否同业限额占用
     * @see
     * @since 1.0
     */
    public boolean checkBankAmtLimt(String secAcctId, String ps, String secAcctSource,String tradePlace) {
        //fanshuanghu 20191111 添加是否占用标识
        boolean occr = false;

        //fanshuanghu 20191111 保证账户来源和帐号不同时为空
        if(!StringUtil.isNotEmpty(secAcctSource) && !StringUtil.isNotEmpty(secAcctId)){
            return false;
        }

        if (StringUtil.equals(ps, BussinessDictCodeList.回购拆借方向_逆回购.getValue())) {
            if (StringUtil.equals(secAcctSource, BussinessDictCodeList.账户来源_自营.getValue())){
                occr = true;
            }
            else if (StringUtil.isNotEmpty(secAcctId)){
                StlSecAcctInfo info = stlSecAcctInfoMapper.selectByPrimaryKey(secAcctId);
                if (StringUtil.isNotEmpty(info) && StringUtil.equals(info.getSecAcctSource(),
                        BussinessDictCodeList.账户来源_自营.getValue())) {
                    occr = true;
                }else if(!StringUtil.isNotEmpty(info)){
                    //范双虎 20191111 账户来源为空且账户代码查询不到相关信息,直接返回false
                    return false;
                }
            }

            //fanshuanghu 20191111 增加已类户额度占用 interfFlag_SEND_CREDIT_SEL_FINA:理财,interfFlag_SEND_CREDIT_SEL:自营
            if(occr && StringUtil.equals(CacheUtil.getSysParam("interfFlag_SEND_CREDIT_SEL"), "01")
                    || (!occr && StringUtil.equals(CacheUtil.getSysParam("interfFlag_SEND_CREDIT_SEL_FINA"), "01") &&
                    StringUtil.equals(BussinessDictCodeList.交易场所_银行间.getValue(), tradePlace))){
                occr = true;
            }else{
                occr = false;
            }
        }

        return occr;
    }

    /**
     * 委托竞价式正回购退回时,标准券持仓更新
     * @param entity
     */
    private void doBackUpdateEtrPosition(CfetsTrdRepoTrade002Dto entity) {
        if (StringUtil.isNotEmpty(entity.getEntrustId())
                && StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_正回购.getValue())
                && StringUtil.equals(entity.getRepoType(), DictCodeList.回购类型_质押式竞价回购.getValue())) {
            // 指令持仓和现金流水处理
            List<EtrSecPositionFlowDto> etrFlows = baseAssetCashPositionCommMapper.selectAllEtrPosition(entity.getTradeId());
            if (etrFlows != null) {
                etrFlows.forEach(
                        etrSecPositionFlowDto -> etrSecPositionFlowDto.setSeqno(getSeqNo()));
                EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
                etrPositionHoldDto.setPtlSecPositionFlows(etrFlows);
                EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
            }
        }
    }
    /**
     * 回购资产交易新建画面，查看模式：查询回购资产交易信息
     *
     * @param tradeId 交易编号
     * @return 回购资产交易信息
     * @see
     * @since 1.0
     */
    public CfetsTrdRepoTrade002Dto getTrdRepoInvestTradeDto(String totleTradeId) {
        Map<String, Object> param = new HashMap<>();
        param.put("totleTradeId", totleTradeId);
        param.put("BRANCH_0", CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        param.put("B09", DictCodeListExt1.一次性费用类型_通道费.getValue());
        param.put("Y", DictCodeList.是否_是.getValue());
    /*    CfetsTrdRepoTrade002Dto resultDto = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/getTrdRepoTradeDto",
                param, TrdRepoTrade002Dto.class);*/
        CfetsTrdRepoTrade002Dto resultDto= cfetsTrdQueryMapper.getTrdRepoInvestTradeDto(param);
        if (resultDto != null) {
            //待办事项中显示组合分配
            Map<String,Object> pageParam = new HashMap<>();
            pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
            pageParam.put("feeType08", BussinessDictCodeList.费用类型_结算手续费.getValue());
            pageParam.put("totleTradeId",totleTradeId);
            List<PortIntFenPeiInfoDto> fenPeiList=cfetsTrdQueryMapper.findPortIntFenPeiInfoList(pageParam);
            resultDto.setCfetsBussCombDistribtList(fenPeiList);

            //待办事项中显示质押券
            pageParam.put("TRADEPLACE01", DictCodeListExt1.交易场所_银行间.getValue());
            pageParam.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
            pageParam.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
            pageParam.put("platDate", iPlatformDate.getSysDateStr());
            List<CfetsPledgeInfoResponse> cfetsPledgeInfoList=cfetsTrdQueryMapper.findPledgeInfoList(pageParam);
            resultDto.setCfetsPledgeInfoList(cfetsPledgeInfoList);


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


}
