/*
 * 文件名：TrdRepoTrade002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.invest.trade.trdrepotrade.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.util.DateUtil.DurationType;
import com.joyintech.system.base.dao.SysUploadFileInfoMapper;
import com.joyintech.system.base.entity.SysUploadFileInfo;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussRepoSecService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.BasicTradeFeeInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.*;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;
import com.joyintech.tams.invest.interf.service.InterfComponentService;
import com.joyintech.tams.invest.interf.service.InterfComponentService.OrderCaller;
import com.joyintech.tams.invest.trade.lend.dto.FinTradeProductDto;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTrade003Dto;
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

@Service
public class TrdRepoTrade003Service extends BaseService<TrdRepoTrade003Dto> implements BaseProcessService<TrdRepoTrade003Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdRepoTrade003Service.class);

    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

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
    private SysUploadFileInfoMapper sysUploadFileInfoMapper;

    /**
     * 共同
     */
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    /**
     * 共同
     */
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

    /**
     * 共同
     */
    @Autowired
    private TrdProductDealMapper trdProductDealMapper;
    @Autowired
    private PtlTradeMapper ptlTradeMapper;
    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;
    @Autowired
    private FinTradeProductAddMapper finTradeProductAddMapper;
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;
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
    public void doInsert(TrdRepoTrade003Dto entity, OperationType operationType) {
        // 新建交易
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            this.handleBussFlowData(entity);
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
    public void doUpdate(TrdRepoTrade003Dto input) {
        if (!StringUtil.equals("revoke", input.getOperateType())
                && !StringUtil.equals("delete", input.getOperateType())) {
            // 先删除流程中数据
            this.doDelete(input.getWorkflowId());

            // 重新insert
            this.handleBussFlowData(input);
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
        TrdRepoTrade003Dto entity = this.getBussinessEntityByWorkflowId(workflowId);
        this.deletePtlSecPositionFlow(entity.getTradeId());
    }

    /**
     * 工作流审核完成doComplete
     *
     * @param entity         entity
     * @param operationType operationType
     */
    @Override
    public void doComplete(TrdRepoTrade003Dto entity, OperationType operationType) {
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            // 构造Entity
            this.prepareEntity(entity);

            // 7.1登录各业务表，包括金融产品交易、金融产品附属、划款指令及附件信息
            // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易表
            entity.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

            // 金融产品交易扩展表Entity -> 金融产品交易扩展表
            PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, entity.getWflPaTrdProductDeal());

            // 组合交易表Entity -> 组合交易信息表
//            if (CollectionUtils.isNotEmpty(input.getPtlTrade())) {
//                input.getPtlTrade().forEach(ptlTrade -> bussCommonMapperDelegateService.insertBatch(PtlTradeMapper.class,
//                        ptlTrade));
//            }
//            // 批量插入
//            bussCommonMapperDelegateService.insertBatch(PaTrdProductDealMapper.class,
//                    paTrdProductDeal);
            if(entity.getPtlTrade().size()>0){
                entity.getPtlTrade().forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
            }
            paTrdProductDealMapper.insertSelective(paTrdProductDeal);
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

            // OPCIS表Entity TODO

            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());

            List<BaseTradeProductDealDto> lstPostion = new ArrayList<>();

            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
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
            // add by wangguoxian 费用设置 20190722 end
            finProductTradeDto.setAssetType2(entity.getRepoType());
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
            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommService.execute(finProductTradeDto);

            // 7.2 删除原业务流程表中的交易记录
            String workflowId = entity.getWorkflowId();
            if (StringUtil.isBlank(workflowId)) {
                WflTrdProductDealKey wflTrdProductDealKey = new WflTrdProductDealKey();
                wflTrdProductDealKey.setTradeId(entity.getTradeId());
                wflTrdProductDealKey.setWorkflowStatus(
                        DictCodeListExt1.交易流程状态_流程中.getValue());
                WflTrdProductDeal wflTrdProductDeal = bussCommonMapperDelegateService.selectByMultiPrimaryKey(
                        WflTrdProductDealMapper.class, wflTrdProductDealKey, WflTrdProductDeal.class);

                if (wflTrdProductDeal != null) {
                    workflowId = wflTrdProductDeal.getWorkflowId();
                }
            }

            // 通道交易 新增 强成西 2019/03/26 start
            TrdTradeFeeDetail trdTradeFeeDetail = null;
            if (StringUtil.isNotEmpty(entity.getTrdProductDeal().getChlId())) {
                // 交易费用明细
                trdTradeFeeDetail = AllTradeCommonUtil.genChannelTradeFeeDetail(entity.getTrdProductDeal().getTradeId(), entity.getChannelFee());
                trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
//                bussCommonMapperDelegateService.insertBatch(TrdTradeFeeDetailMapper.class, trdTradeFeeDetail);
            }
            // 通道交易 新增 强成西 2019/03/26 end

            // 7.3 写入资产信息进交易类金融产品表
            finTradeProductMapper.insertSelective(entity.getFinTradeProduct());
//            bussCommonMapperDelegateService.insertBatch(FinTradeProductMapper.class, input.getFinTradeProduct());
            // 7.4 写入资产信息进交易类金融产品附属表
//            bussCommonMapperDelegateService.insertBatch(FinTradeProductAddMapper.class, input.getFinTradeProductAdd());
            finTradeProductAddMapper.insertSelective(entity.getFinTradeProductAdd());

            // add by wangpeng 获取估值分类123 start
            ValItemValueGenUtil.genTypeForFinProductType(entity.getFinTradeProduct(), entity.getFinProductType());
            // add by wangpeng end

            // 7.4写入回购类型进金融产品分类表
//            bussCommonMapperDelegateService.insertBatch(FinProductTypeMapper.class, input.getFinProductType());
            finProductTypeMapper.insertSelective(entity.getFinProductType());

            // 7.5写入现金流相关信息表
            this.cashFlowLogic(entity);

            // 7.6写入回购质押品信息
            bussRepoSecService.insert(entity.getBussRepoSec(), entity.getTradeId());

            // 7.7写入交易信息进交易报送信息表，为中债报送用
            this.dealInfoCbReportLogic_7_7(entity);

            // 风险试算
            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());
            
            // 发送Murex接口处理
            this.sendMurex(entity, false);

            // 一体化对应etliu start
            // 所有交易生效后调用的共通方法
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            inputDto.setProdid(entity.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            inputDto.setTrdProductDeal(entity.getTrdProductDeal());
            // 通道交易 新增 强成西 2019/03/26 start
            inputDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
            inputDto.setTrdTradeFeeDetail(trdTradeFeeDetail);
            // 通道交易 新增 强成西 2019/03/26 end
            inputDto.setRepoType(entity.getRepoType());
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                inputDto.setActPortfolioId(entity.getActPortfolioId());
            }
            inputDto.setPtlTradeList(entity.getPtlTrade());
            if (stlPayOrders != null && !stlPayOrders.isEmpty()) {
                StlPayOrders order = new StlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(order, stlPayOrders.get(0));
                inputDto.setStlPayOrders(order);
            }
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            // 一体化对应etliu end
        }
        // 撤销
        else {
            this.doRevokeComplete(entity);
            
            // 发送Murex接口处理
//            this.sendMurex(input, true);
        }
        
        // 7.2删除原业务流程表中的交易记录
        String workflowId = entity.getWorkflowId();
        if (StringUtil.isBlank(workflowId)) {
            WflTrdProductDealKey wflTrdProductDealKey = new WflTrdProductDealKey();
            wflTrdProductDealKey.setTradeId(entity.getTradeId());
            wflTrdProductDealKey.setWorkflowStatus(
                DictCodeListExt1.交易流程状态_流程中.getValue());
            WflTrdProductDeal wflTrdProductDeal = bussCommonMapperDelegateService.selectByMultiPrimaryKey(
                WflTrdProductDealMapper.class, wflTrdProductDealKey, WflTrdProductDeal.class);

            if (wflTrdProductDeal != null) {
                workflowId = wflTrdProductDeal.getWorkflowId();
            }
        }
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
     // 增加调用接口 by 赵东东  20191206  end
    }

    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(TrdRepoTrade003Dto entity) {
        //多组合交易时对附件进行处理
        setDzhUploadFileList(entity);
//        if (CollectionUtils.isNotEmpty(input.getUploadFileList())
//                && StringUtil.isNotEmpty(input.getTradeId())) {
//            // 将附件和当前业务对象绑定（更新附件的业务代码）
//            fileAttachmentProcess.updateBussinessNo(
//                    input.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
//                            Collectors.toList()),
//                    input.getTradeId());
//        }
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

        // 批量插入
        wflTrdProductDealMapper.insertSelective(entity.getWflTrdProductDeal());
        wflTrdProductDealAddMapper.insertSelective(entity.getWflTrdProductDealAdd());
        wflPaTrdProductDealMapper.insertSelective(entity.getWflPaTrdProductDeal());
        wflTrdProductDealOthMapper.insertSelective(entity.getWflTrdProductDealOth());

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

        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        finProductTradeDto.setAssetType2(entity.getRepoType());
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDto.setTrdProductDeal(entity.getTrdProductDeal());
        baseTradeProductDealDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());

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



    @DataSource(DataSourceType.OTHER)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void setDzhUploadFileList(TrdRepoTrade003Dto entity) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())) {
            for (UploadFile file:entity.getUploadFileList()) {
                com.joyintech.system.base.entity.SysUploadFileInfo sysUploadFileInfo = sysUploadFileInfoMapper.selectByPrimaryKey(file.getFileNo());
                com.joyintech.system.base.entity.SysUploadFileInfo fileInfo = new SysUploadFileInfo();
                String uuid = UUID.randomUUID().toString().replace("-", "");
                fileInfo.setSeqNo(uuid);
                fileInfo.setFileName(sysUploadFileInfo.getFileName());
                fileInfo.setPath(sysUploadFileInfo.getPath());
                fileInfo.setBusinessId(entity.getTradeId());
                fileInfo.setCreateTime(sysUploadFileInfo.getCreateTime());
                fileInfo.setCreateUser(sysUploadFileInfo.getCreateUser());
                fileInfo.setUpdateTime(sysUploadFileInfo.getUpdateTime());
                fileInfo.setUpdateUser(sysUploadFileInfo.getCreateUser());
                sysUploadFileInfoMapper.insert(fileInfo);
            }
        }
    }

    /**
     * 构造Entity
     *
     * @param input input
     */
    public void prepareEntity(TrdRepoTrade003Dto input) {
        input.setActPortfolioId(input.getPortfolioId());
        input.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(input.getPortfolioId()));
        // 金融产品交易Entity 构造开始-----------------------------------------------------------------------
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        // 交易编号 = 2生成的交易编号
        wflTrdProductDeal.setTradeId(input.getTradeId());
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
        input.setWflTrdProductDeal(wflTrdProductDeal);

        //交易表存入首期交付日
        if(StringUtil.isNotEmpty(input.getVpayDate())){
        	wflTrdProductDeal.setPayDate(input.getVpayDate());
        }
        
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
        if(StringUtils.isNotBlank(input.getSecAcctId())){
            // 登记托管机构用证券托管户的登记托管机构
            String registOrg = restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME
                            + "/TrdBondInvestTrade/getRegistOrg?secAcctId="
                            + input.getSecAcctId(),
                    String.class);
            finTradeProductAdd.setRegistOrg(registOrg);
        }
        input.setFinTradeProductAdd(finTradeProductAdd);
        // 交易类金融产品附属Entity 构造结束----------------------------------------------------------

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
    private void doInsertRevoke(TrdRepoTrade003Dto input) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
                        + input.getTradeId(),
                BaseTradeProductDealDto.class);

        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baseDto.getTrdProductDeal());
                // 交易状态=“撤销”
                wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
                wflTrdProductDeal.setpTradeId(input.getTradeId());
                // 撤销说明=画面.撤销说明
                wflTrdProductDeal.setCancelRemark(input.getRevokeReason());
                // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
                wflTrdProductDeal.setTradeId(input.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDeal.setWorkflowId(input.getWorkflowId());
                wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);

                // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseDto.getTrdProductDealAdd());
                wflTrdProductDealAdd.setTradeId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setProdId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDealAdd.setWorkflowId(input.getWorkflowId());
                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);

                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
                if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
                    for (StlPayOrdersDto stlPayOrdersDto : baseDto.getStlPayOrdersList()) {
                        WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        wflStlPayOrders.setWorkflowId(input.getWorkflowId());

                        String orderId;
                        if (baseGetBusinessId != null) {
                            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                            bussinessCodeParameter.setSeqNo("9998");
                            orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
                        } else {
                            orderId = IDGenerator.generate();
                        }
                        wflStlPayOrders.setOrderId(orderId);
                        wflStlPayOrders.setTradeId(wflTrdProductDeal.getTradeId());
                        wflStlPayOrders.setpTradeId(stlPayOrdersDto.getOrderId());
                        wflStlPayOrders.setProdId(wflTrdProductDeal.getTradeId());
                        wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
//                        bussCommonMapperDelegateService.insertBatch(WflStlPayOrdersMapper.class, wflStlPayOrders);
                    }
                }
            }
        }
    }

    /**
     * 撤销doRevokeComplete
     *
     * @param entity input
     * @see
     * @since 1.0
     */
    private void doRevokeComplete(TrdRepoTrade003Dto entity) {
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
                if (bussCommonMapperDelegateService != null) {
                    PaTrdProductDeal paTrdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                            PaTrdProductDealMapper.class, baseDto.getTrdProductDeal().getTradeId(),
                            PaTrdProductDeal.class);
                    paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    paTrdProductDeal.setCreateUser(LoginUserUtil.getUserName());
                    paTrdProductDeal.setCreateTime(new Date());
                    paTrdProductDeal.setUpdateUser(paTrdProductDeal.getCreateUser());
                    paTrdProductDeal.setUpdateTime(paTrdProductDeal.getCreateTime());
//                    bussCommonMapperDelegateService.insertBatch(PaTrdProductDealMapper.class,
//                            paTrdProductDeal);
                    paTrdProductDealMapper.insertSelective(paTrdProductDeal);
                }
              
            }

            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtoList);

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommService.execute(finProductTradeDto);
            if(!StringUtil.isNotEmpty(entity.getActPortfolioId())) {
                entity.setActPortfolioId(entity.getPortfolioId());
                entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            }
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
        }

//        // 撤销委托/回购交易时，删除回购质押品信息
//        bussRepoSecService.deletePledgeBond(input.getTradeId(), input.getBussRepoSec().getPageFrom());

        // 写入质押品的组合持仓流水
        this.ptlSecPositionFlowLogic(entity, true);

        //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

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
        
//        // 一体化对应etliu start
//        // 所有交易生效后调用的共通方法
//        for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
//            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//            inputDto.setProdid(baseDto.getTrdProductDealAdd().getPortfolioId());
//            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//            inputDto.setTrdProductDeal(baseDto.getTrdProductDeal());
//            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
//        }
//        // 一体化对应etliu end
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
    private boolean ptlSecPositionFlowLogic(TrdRepoTrade003Dto input, boolean isNegate) {
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
                    if(CollectionUtils.isNotEmpty(avaMap.get(pledgeBond.getBondCode()))){
                        for (PledgeBondInfoDto avaBond : avaMap.get(pledgeBond.getBondCode())) {
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
    private void cashFlowLogic(TrdRepoTrade003Dto input) {
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
        finCashPlan.setIntAmt(bussCoreCalculateService.calc投资交易计算回购利息(input.getPrinAmt(),
                ArithmeticUtil.divide(input.getRebackPrice(), new BigDecimal("100"), 8),
                input.getVdate(), input.getMdate(), input.getBasis()));
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
    private void dealInfoCbReportLogic_7_7(TrdRepoTrade003Dto input) {
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
    private void assetDebtCbReportLogic_7_8_1(TrdRepoTrade003Dto input) {
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
       /* // 调用资产负债信息报送组件
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
    private void assetDebtCbReportLogic_7_8_2(TrdRepoTrade003Dto input) {
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
    public AjaxResult checkConfirm(TrdRepoTrade003Dto inputDto, BindingResult result) {
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
            if(StringUtil.isNotBlank(inputDto.getZhilingtype())&&StringUtil.equals(inputDto.getZhilingtype(),"02")){//多组合
                if(CollectionUtils.isNotEmpty(inputDto.getBussRepoSecPtl().getPledgeBondList())){
                    for(int i=0;i<inputDto.getBussRepoSecPtl().getPledgeBondList().size();i++){
                        confirmMsg = getPortfolioCanUserMoney(inputDto.getBussRepoSecPtl().getPledgeBondList().get(i), confirmMsg, changeLineMark);
                    }
                }
            }else{
                if (inputDto.getPrinAmt() != null && inputDto.getBussTrdAvailableCashQuery() != null
                        && inputDto.getBussTrdAvailableCashQuery().getAvalibleCash() != null
                        && inputDto.getPrinAmt().compareTo(inputDto.getBussTrdAvailableCashQuery().getAvalibleCash()) > 0) {
                    if (StringUtil.isNotBlank(confirmMsg)) {
                        confirmMsg += changeLineMark;
                    }
                    confirmMsg += getMessage("TRD_C_0002");
                }
            }
        }
        if (StringUtil.isNotBlank(confirmMsg)) {
            AjaxResult wr = new AjaxResult();
            wr.setData(confirmMsg);
            return wr;
        }

        return null;
    }

    private String getPortfolioCanUserMoney(PledgeBondInfoDto inputDto, String confirmMsg, String changeLineMark) {
        if (inputDto.getPrinAmt() != null && inputDto.getAvalibleCash() != null
                && inputDto.getPrinAmt().compareTo(inputDto.getAvalibleCash()) > 0) {
            PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(inputDto.getPortfolioId());
            if (StringUtil.isNotBlank(confirmMsg)) {
                confirmMsg += changeLineMark;
            }
            confirmMsg += "组合("+ptlPortfolio.getPortfolioName()+")可用现金不足,当前最多可用金额为:<br/>" +
                    inputDto.getAvalibleCash() +
                    "请确认是否继续？";
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
    public TrdRepoTrade003Dto getTrdRepoTradeDto(String tradeId) {
        Map<String, Object> param = new HashMap<>();
        param.put("tradeId", tradeId);
        param.put("BRANCH_0", CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        TrdRepoTrade003Dto resultDto = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/getTrdRepoTradeDto",
                param, TrdRepoTrade003Dto.class);
        //获取费用信息
        TrdTradeFeeDetailKey feeDetailKey = new TrdTradeFeeDetailKey();
        feeDetailKey.setTradeId(tradeId);
        feeDetailKey.setFeeType(BussinessDictCodeList.费用类型_交易手续费.getValue());
        TrdTradeFeeDetail procedureAmtDetail =  bussCommonMapperDelegateService.selectByMultiPrimaryKey(TrdTradeFeeDetailMapper.class,feeDetailKey,TrdTradeFeeDetail.class);
        if(procedureAmtDetail != null){
            resultDto.setProcedureAmt(procedureAmtDetail.getFeeAmt());
            resultDto.setProcedureAmtRate(procedureAmtDetail.getFeeRate());
        }
        feeDetailKey.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
        TrdTradeFeeDetail settleAmtDetail =  bussCommonMapperDelegateService.selectByMultiPrimaryKey(TrdTradeFeeDetailMapper.class,feeDetailKey,TrdTradeFeeDetail.class);
        if (settleAmtDetail != null){
            resultDto.setSettleAmt(settleAmtDetail.getFeeAmt());
            resultDto.setSettleAmtRate(settleAmtDetail.getFeeRate());
        }
        feeDetailKey.setFeeType(BussinessDictCodeList.费用类型_佣金.getValue());
        TrdTradeFeeDetail commitAmtDetail =  bussCommonMapperDelegateService.selectByMultiPrimaryKey(TrdTradeFeeDetailMapper.class,feeDetailKey,TrdTradeFeeDetail.class);
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
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                        + "/TrdRepoTrade/getAvaStandardStockPosition/",
                params, BigDecimal.class);
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
    public List<PledgeBondInfoDto> selectAvaPledgeWithInvAim(TrdRepoTrade003Dto input) {
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
    private void sendMurex(TrdRepoTrade003Dto entity, boolean revokeFlag) {
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
        TrdRepoTrade003Dto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
        	  try {
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
    public TrdRepoTrade003Dto calFee(TrdRepoTrade003Dto dto) {
        List<TrdTradeFeeDetail> tradeFeeList = new ArrayList<>();
        TrdRepoTrade003Dto resultDto = new TrdRepoTrade003Dto();
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
    private List<TrdTradeFeeDetail> setTrdFeeDetail(TrdRepoTrade003Dto entity) {
        List<TrdTradeFeeDetail> tradeFeeList = new ArrayList<>();
        if (entity.getSettleAmt() != null){
            TrdTradeFeeDetail settleAmtDetail = getTrdTradeFeeDetail(entity,BussinessDictCodeList.费用类型_结算手续费.getValue());
            tradeFeeList.add(settleAmtDetail);
        }
        if (entity.getProcedureAmt() != null){
            TrdTradeFeeDetail procedureAmtDetail = getTrdTradeFeeDetail(entity,BussinessDictCodeList.费用类型_交易手续费.getValue());
            tradeFeeList.add(procedureAmtDetail);
        }
        if (entity.getCommissionAmt() != null){
            TrdTradeFeeDetail commissionAmtDetail = getTrdTradeFeeDetail(entity,BussinessDictCodeList.费用类型_佣金.getValue());
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
    private TrdTradeFeeDetail getTrdTradeFeeDetail(TrdRepoTrade003Dto entity,String feeType){
        TrdTradeFeeDetail amtDetail = new TrdTradeFeeDetail();
        if (StringUtil.equals(feeType,BussinessDictCodeList.费用类型_佣金.getValue())){
            amtDetail.setFeeAmt(entity.getCommissionAmt());
            amtDetail.setFeeRate(entity.getCommissionAmtRate());
            amtDetail.setIsPayWithTrade(entity.getIsPayWithTrade());
        }
        else if (StringUtil.equals(feeType,BussinessDictCodeList.费用类型_交易手续费.getValue())){
            amtDetail.setFeeAmt(entity.getProcedureAmt());
            amtDetail.setFeeRate(entity.getProcedureAmtRate());
            amtDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
        }
        else if (StringUtil.equals(feeType,BussinessDictCodeList.费用类型_结算手续费.getValue())){
            amtDetail.setFeeAmt(entity.getSettleAmt());
            amtDetail.setFeeRate(entity.getSettleAmtRate());
            amtDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
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
            }else{
                mdate = DateUtil.format(tradeDate);
            }
            
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
     * 根据委托代码处理委托信息
     * @param entrustIds 委托代码
     * @param dto
     */
    public void dealEntrustInfo(String entrustIds, TrdRepoTrade003Dto dto) {
        List<String> entrustIdList = StringUtil.splitToList(GlobalConstant.COMMA,entrustIds);
        List<TrdRepoTrade003Dto> res = new ArrayList<>();
        BaseEtrInvestInfoDto  basedto = getEntrustInfo(entrustIdList.get(0));
    }

    /**
     * 根据组合代码获取组合编号
     * @param map
     * @return
     */
    public String getPortfolioNameById(Map<String,Object> map) {
        Map<String,Object> ret = restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/portfolioName",
                map, Map.class);
        return (String)ret.get("portfolioName");
    }

    /**
     * 获取质押券其他信息
     * @param othInputMap 参数
     * @return 质押券信息
     */
    public PledgeBondInfoDto getPledgeBondInfoDto(Map<String, Object> othInputMap) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/getPledgeBondInfoDto",
                othInputMap, PledgeBondInfoDto.class);
    }
    
    /**
     * 查询委托过来的剩余委托额度
     */
    public BigDecimal selectDoEntrustMoney(String entrustId) {
        String[] entrustIdList = entrustId.split("\\,");
        Map<String, Object> param = new HashMap<>();
        param.put("entrustIdList", entrustIdList);
        
        BigDecimal EntrustMoney = restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/selectDoEntrustMoney", param, BigDecimal.class);
        return EntrustMoney;
    }
    
    @Override
    public void doBack(TrdRepoTrade003Dto entity) {
    	// TISHKBS-747 【投资交易-质押式逆回购】质押式逆回购多组合交易，流程中数据退回并作废后，剩余现金没有增加 add by lizhi 20200629 start
    	AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTradeId());
    	// TISHKBS-747 【投资交易-质押式逆回购】质押式逆回购多组合交易，流程中数据退回并作废后，剩余现金没有增加 add by lizhi 20200629 end    	
        //删除授信占用
        if (null != entity.getTradeId() && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }
    
    @Override
    public void doRevoke(TrdRepoTrade003Dto entity) {
        //删除授信占用
        if (null != entity.getTradeId() && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }

    
}
