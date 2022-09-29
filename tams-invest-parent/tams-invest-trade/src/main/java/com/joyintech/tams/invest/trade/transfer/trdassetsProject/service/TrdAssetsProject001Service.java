package com.joyintech.tams.invest.trade.transfer.trdassetsProject.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.impl.BaseTradeProductDealCommServiceImpl;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.common.TrdCommonParam;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.entrust.dao.EtrTrdCommonMapper;
import com.joyintech.tams.invest.common.threadservice.JasperUtil;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.trade.transfer.assign.service.TrdAssetAssignService;
import com.joyintech.tams.invest.trade.transfer.planratetransfer.dto.PlanTransferDto;
import com.joyintech.tams.invest.trade.transfer.planratetransfer.service.TrdPlanRateInvestTradeTransfer001Service;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransferDto;
import com.joyintech.tams.invest.trade.transfer.trdassetsProject.dto.TrdAssetsProject001Dto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

//import com.joyintech.tams.component.business.core.service.impl.BaseGetBusinessIdImpl;
//import com.joyintech.tams.component.business.trade.impl.BussTrdTransferOrderImplService;
//import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;

@Service
public class TrdAssetsProject001Service extends BaseService<TrdAssetsProject001Dto> implements BaseProcessService<TrdAssetsProject001Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdAssetsProject001Service.class);

	@Autowired
    private TrdPlanRateInvestTradeTransfer001Service trdPlanRateInvestTradeTransfer001Service;
	@Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private JasperUtil jasperUtil;

    /*@Autowired
    private BaseGetBusinessIdImpl baseGetBusinessIdImpl;

    @Autowired
    private BussTrdTransferOrderImplService bussTrdTransferOrderImplService;*/

    @Autowired
    private BaseTradeProductDealCommServiceImpl baseTradeProductDealCommServiceImpl;

    /*@Autowired
    private BaseGetBussinessId baseGetBussinessId;*/

    @Autowired
    private IPlatformDate iPlatformDate;
    
    @Autowired
    private TrdAssetAssignService trdAssetAssignService;

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
    EtrTrdCommonMapper etrTrdCommonMapper;

    @Autowired(required = false)
    private BaseGetBussinessId baseGetBussinessId;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;
    
    @Override
    public void doInsert(TrdAssetsProject001Dto dto, OperationType type) {
        for (TrdAssetsProject001Dto entity:dto.getProjectListInfoList()) {
            if (type != OperationType.REVOKE && BussinessConstant.SUBMIT.equals(dto.getOperateType())) {
                if (StringUtil.isNotEmpty(entity.getBaseTradeInfoDtos())) {
                    entity.getBaseTradeInfoDtos().forEach(
                            baseTradeInfoDto -> {
                                if(StringUtil.isNotEmpty(baseTradeInfoDto.getTrdProductDeal())){
                                    baseTradeInfoDto.getTrdProductDeal().setWorkflowId(dto.getWorkflowId());
                                    if ("T01".equals(baseTradeInfoDto.getTrdProductDeal().getPs())) {
                                        baseTradeInfoDto.getTrdProductDeal().setInvAim(entity.getInvAim());
                                    } else {
                                        baseTradeInfoDto.getTrdProductDeal().setInvAim(dto.getInvAim());
                                    }
                                }
                                if(baseTradeInfoDto.getTrdProductDealAdd() != null){
                                    baseTradeInfoDto.getTrdProductDealAdd().setWorkflowId(dto.getWorkflowId());
                                }
                                if(baseTradeInfoDto.getPaTrdProductDeal() != null){
                                    baseTradeInfoDto.getPaTrdProductDeal().setWorkflowId(dto.getWorkflowId());
                                }
                                if(baseTradeInfoDto.getStlPayOrders() != null){
                                    baseTradeInfoDto.getStlPayOrders().setWorkflowId(dto.getWorkflowId());
                                }
                                if(baseTradeInfoDto.getTrdDealTotalAdd() != null){
                                    baseTradeInfoDto.getTrdDealTotalAdd().setWorkflowId(dto.getWorkflowId());
                                }
                                if(baseTradeInfoDto.getPaTrdFinProductDealTotal() != null){
                                    baseTradeInfoDto.getPaTrdFinProductDealTotal().setWorkflowId(dto.getWorkflowId());
                                }
                                if(baseTradeInfoDto.getTrdFinProductDealTotal() != null){
                                    baseTradeInfoDto.getTrdFinProductDealTotal().setWorkflowId(dto.getWorkflowId());
                                }
                            }
                    );
                }
                entity.setOperateType(dto.getOperateType());
            }
            if (!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
                saveDataWhenStartWorkflow(entity);
            }
        }
    }

    private void saveDataWhenStartWorkflow(TrdAssetsProject001Dto entity) {
        if (!com.joyintech.framework.util.StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }
        for (BaseTradeInfoDto trade:entity.getBaseTradeInfoDtos()) {
            entity.setActPortfolioId(trade.getTrdProductDealAdd().getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(trade.getTrdProductDealAdd().getPortfolioId()));
            // 调用授信共同 sjj start
            TrdProductDeal TrdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, trade.getTrdProductDeal());
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            TrdProductDeal.setCounterId(trade.getTrdProductDeal().getCounterId());
            TrdProductDeal.setTradeDate(entity.getTradeDate());
            TrdProductDeal.setTradeId(trade.getTrdProductDeal().getTradeId());
            inputDto.setTrdProductDeal(TrdProductDeal);
            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto,false);
            // 调用授信共同 sjj end

            // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, trade.getTrdProductDeal());
            wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

            // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, trade.getTrdProductDealAdd());
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());

            // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, trade.getPaTrdProductDeal());
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

            // 金融产品交易汇总信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
            WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, trade.getTrdFinProductDealTotal());
            wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

            WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, trade.getPaTrdFinProductDealTotal());
            wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
            wflPaTrdFinDealTotal.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId());

            WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, trade.getTrdDealTotalAdd());
            wflTrdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
            wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            // 转换
            wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
            wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
            wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
            wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);
            wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);
            wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);
            if (trade.getTrdTradeFeeDetail() != null) {
                for (TrdTradeFeeDetail detail : trade.getTrdTradeFeeDetail()) {
                    trdTradeFeeDetailMapper.deleteByPrimaryKey(detail);
                    trdTradeFeeDetailMapper.insertSelective(detail);
                }
            }

            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表 不是卖出时才插入流程表
            if (trade.getStlPayOrders() != null) {
                WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, trade.getStlPayOrders());
                wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
            }
        }
        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
        execute持仓共通(entity);
    }

    @Override
    public void doComplete(TrdAssetsProject001Dto dto, OperationType operationType) {
        for (TrdAssetsProject001Dto entity:dto.getProjectListInfoList()) {
            if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            	String bTradeId = "";
                for (BaseTradeInfoDto trade:entity.getBaseTradeInfoDtos()) {
                    entity.setActPortfolioId(trade.getTrdProductDealAdd().getPortfolioId());
                    entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(trade.getTrdProductDealAdd().getPortfolioId()));
                    // 7.1登录各业务表，包括金融产品交易、金融产品附属、划款指令及附件信息
                    // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易表
                    TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
                    BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, trade.getTrdFinProductDealTotal());
                    trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

                    TrdProductDeal trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, trade.getTrdProductDeal());
                    trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                    trdProductDeal.setInvAim(entity.getInvAim());
                    if ("T01".equals(trade.getTrdProductDeal().getPs())) {
                        trdProductDeal.setInvAim(entity.getInvAim());
                    } else {
                        trdProductDeal.setInvAim(dto.getInvAim());
                    }

					bTradeId = DictCodeList.交易方向_买入.getValue().equals(trdProductDeal.getPs()) ? trdProductDeal.getTradeId() : bTradeId;
                    
                    // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属表
                    TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, trade.getTrdProductDealAdd());

                    // 金融产品交易扩展表Entity -> 金融产品交易扩展表
                    PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, trade.getPaTrdProductDeal());

                    // 金融产品交易汇总扩展表Entity -> 金融产品交易汇总扩展表
                    PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
                    BeanUtil.copyPropertiesIgnoreNull(paTrdFinProductDealTotal, trade.getPaTrdFinProductDealTotal());

                    // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属表
                    TrdDealTotalAdd trdTotalAdd = new TrdDealTotalAdd();
                    BeanUtil.copyPropertiesIgnoreNull(trdTotalAdd, trade.getTrdDealTotalAdd());

                    TrdCommonParam trdCommonParam = new TrdCommonParam();
                    // 组合交易表Entity -> 组合交易信息表
                    if (CollectionUtils.isNotEmpty(trade.getPtlTrade())) {
                        trdCommonParam.setPtlTrades(trade.getPtlTrade());
                    }

                    // 批量插入
                    trdCommonParam.setPaDeals(Arrays.asList(paTrdProductDeal));
                    simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertAutoClassifyBatch",
                            trdCommonParam, String.class);

                    paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);
                    trdDealTotalAddMapper.insertSelective(trdTotalAdd);
//                    trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);


                    // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
                    StlPayOrdersDto stlPayOrders = null;
                    if (com.joyintech.framework.util.StringUtil.isNotEmpty(trade.getStlPayOrders())) {
                        stlPayOrders = new StlPayOrdersDto();
                        BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, trade.getStlPayOrders());
                    }

                    FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                    BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
                    baseTradeProductDealDto.setTrdFinProductDealTotal(trdFinProductDealTotal);//汇总交易
                    baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
                    baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
                    // 划款指令
                    if (stlPayOrders != null) {
                        baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrders));
                    }
                    finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
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

                    trdCommonParam.setStlPayOrdersDtos(Arrays.asList(stlPayOrders));

                    // 7.2删除原业务流程表中的交易记录
                    String workflowId = entity.getWorkflowId();
                    if (com.joyintech.framework.util.StringUtil.isBlank(workflowId)) {
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

                    // 7.3写入交易信息进交易报送信息表，为中债报送用
                    this.dealInfoCbReportLogic_7_3(entity,trade);

                    // 7.4更新资产负债报送状态
                    this.assetDebtCbReportLogic_7_4(entity);
                    //插入风险试算结果
                    basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getTradeId());

                    // 一体化对应etliu start
                    // 所有交易生效后调用的共通方法
                    AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                    inputDto.setProdid(entity.getPortfolioId());
                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                    inputDto.setTrdProductDeal(trdProductDeal);
                    // <!-- 通道交易 新增 强成西 2019/03/26 start -->
                    inputDto.setTrdProductDealAdd(trade.getTrdProductDealAdd());

                    TrdTradeFeeDetail detail = null;
                    if(trade.getTrdTradeFeeDetail() != null && !trade.getTrdTradeFeeDetail().isEmpty()&&StringUtil.isNotEmpty(trade.getTrdTradeFeeDetail())){
                        for(TrdTradeFeeDetail trdTradeFeeDetail :trade.getTrdTradeFeeDetail()){
                            if(StringUtil.equals(DictCodeListExt1.一次性费用类型_通道费.getValue(), trdTradeFeeDetail.getFeeType())){
                                detail =trdTradeFeeDetail;
                            }
                        }
                    }
                    inputDto.setTrdTradeFeeDetail(detail);
                    AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
                    // 一体化对应etliu end

                    //jasperExecute(trdProductDeal);
                }
				trdAssetAssignService.genAssignTrade(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue(), DictCodeList.平安资产类型_项目类资产.getValue(), bTradeId, dto.getIsNeedAssign(), dto,
						entity);

            }
        }
    }

    public void jasperExecute(TrdProductDeal trdProductDeal) {
        if("001".equals(CacheUtil.getSysParam("CUSTOMER_BANK_CODE"))){
            //生成划款指令附件:调用自定义接口项目生成Jasper报表 add by caoxue 2019-07-29 start
            jasperUtil.jasperExecute(Arrays.asList(trdProductDeal),simpleRestTemplate);
            //生成划款指令附件:调用自定义接口项目生成Jasper报表 add by caoxue 2019-07-29 end
        }
    }
    /**
     * 7.3写入交易信息进交易报送信息表，为中债报送用
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void dealInfoCbReportLogic_7_3(TrdAssetsProject001Dto dto,BaseTradeInfoDto trade) {
        // 7.7写入交易信息进交易报送信息表，为中债报送用
        // 1)查询产品登记编码
        String prodRegistCode = simpleRestTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectProdRegistCode/" + dto.getPortfolioId(), null,
                String.class);

        if (com.joyintech.framework.util.StringUtil.isBlank(prodRegistCode)) {
            return;
        }

        // 2）写入交易报送信息
        // 交易报送信息Entity
        RepTrade repTrade = new RepTrade();
        repTrade.setRepFinprodRegId(prodRegistCode); // 产品登记编码
        repTrade.setRepBankTradeCode(trade.getTrdProductDeal().getTradeId()); // 行内交易编码
        repTrade.setRepDelCode(trade.getTrdProductDeal().getTradeId()); // 行内交易编码
        repTrade.setRepBankAssetCode(dto.getFinprodMarketId()); // 行内资产/负债编码
        repTrade.setRepCcy(trade.getTrdProductDeal().getCcy()); // 币种
        repTrade.setRepOccurMoney(trade.getTrdProductDeal().getTradeAmt()); // 发生金额
        repTrade.setRepCount(trade.getTrdProductDeal().getPrinAmt());
        // 折算人民币金额（元） = 画面.全价总额*收盘价(利用画面.币种与交易日查询汇率行情表中最新的收盘价)
        BigDecimal 收盘价 = commonBusinessService.selectClosePrice(dto.getCcy(), DateUtil.format(dto.getTradeDate()));
        BigDecimal 折算人民币金额 = com.joyintech.calculation.util.ArithmeticUtil.multiply(dto.getTradeAmt(), 收盘价);
        repTrade.setRepMoneyCny(折算人民币金额);

        // 资金流动类型: 数据字典缓存.资金流动类型 ※根据交易方向判断，若买入，即资产买入，若卖出，即为资产卖出或到期
        String 资金流动类型 = DictCodeListExt2.资金流动类型_存入银行存款.getValue();
        if (com.joyintech.framework.util.StringUtil.equals(trade.getTrdProductDeal().getPs(), DictCodeList.交易方向_买入.getValue())) {
            资金流动类型 = DictCodeListExt2.资金流动类型_资产买入.getValue();
        }
        repTrade.setRepMoneyFlowType(资金流动类型);
        repTrade.setRepMoneyFlowTypeExp("无");
        repTrade.setRepUnitPriceNet(dto.getUnitCprice());
        repTrade.setRepUnitPriceFull(dto.getUnitFprice());
        repTrade.setRepTradeDate(dto.getTradeDate()); // 交易日
        repTrade.setRepTradeComponent(StringUtil.isNotEmpty(trade.getTrdProductDeal().getCounterId())?trade.getTrdProductDeal().getCounterId():""); // 交易对手方
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue()); // 报送状态
        repTrade.setRepStatusDate(iPlatformDate.getSysDate()); // 数据日期
        repTrade.setRepRemark(dto.getRemark()); // 备注

        // 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
        simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepTrade",
                Arrays.asList(repTrade), Boolean.class);
    }

    /**
     * 7.4更新资产负债报送状态 利用金融产品代码作为条件把资产负债报送信息表中的报送状态更改为是
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void assetDebtCbReportLogic_7_4(TrdAssetsProject001Dto dto) {
        // 利用金融产品代码作为条件把资产负债报送信息表中的报送状态更改为是
        simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/updateRepAssetDebt",
                dto.getFinprodId(), Boolean.class);
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

    private void execute持仓共通(TrdAssetsProject001Dto entity) {
        for (BaseTradeInfoDto trade:entity.getBaseTradeInfoDtos()) {
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            baseTradeProductDealDto.setTrdProductDeal(trade.getTrdProductDeal());
            baseTradeProductDealDto.setTrdProductDealAdd(trade.getTrdProductDealAdd());
            if (StringUtil.isNotEmpty(trade.getStlPayOrders())) {
                StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, trade.getStlPayOrders());
                baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrdersDto));
            }
            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));

            baseTradeProductDealCommService.execute(finProductTradeDto);
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
        }
    }

    /**
     * 查询交易日历
     * @param finprodMarketId 参数
     * @return
     */
    @SuppressWarnings("unchecked")
    public TrdAssetsProject001Dto getBusinessEntity(String finprodMarketId) {
        TrdAssetsProject001Dto dto= new TrdAssetsProject001Dto();
            //查询交易日历
            Map<String, Object> params = new HashMap<>();
            params.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
            params.put("initValue",finprodMarketId);
            Page<SelectOptionDto> sod =  simpleRestTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/TrdProjectInvestTrade/getFinprodMarketId", params,
                    SelectOptionDto.class);
            if(StringUtil.isNotEmpty(sod)&&StringUtil.isNotEmpty(sod.getRoot())){
                Map<String,Object> data = (Map<String,Object>)(sod.getRoot().get(0).getObject());
                dto.setCalendarId((String)data.get("calendarId"));
              long longTimeStamp1 = new Long(new Long((long) data.get("vdate")));
              Date date1 = new Date(longTimeStamp1);
              long longTimeStamp2 = new Long(new Long((long) data.get("mdate")));
              Date date2 = new Date(longTimeStamp2);
                dto.setVdate(date1);
                dto.setMdate(date2);
              dto.setProjectType((String)data.get("finprodType"));
            }
            Map<String, Object> paraMap = new HashMap<>();
            String tradeDate = DateUtil.getSysDate();
            String finprodId ="F18_"+finprodMarketId;
            paraMap.put("tradeDate", tradeDate);
            paraMap.put("finprodId", finprodId);
            PlanTransferDto info = trdPlanRateInvestTradeTransfer001Service.queryAssetInt(paraMap);
            dto.setBasis(info.getBasis());
            dto.setTradeDate(DateUtil.parse(tradeDate));
        return dto;
    }

    /**
     * 设置列表信息
     * @param param 参数
     * @return 列表dto
     */
    public TrdAssetsProject001Dto setProjectListInfoDto(Map<String, Object> param) throws IOException {
        TrdAssetsProject001Dto projListInfoDto = new TrdAssetsProject001Dto();
        TrdAssetsProject001Dto dto = BeanUtil.mapToObject(param,TrdAssetsProject001Dto.class);
        List<TrdAssetsProject001Dto> list = new ArrayList<>();
        if(StringUtil.isNotEmpty(param.get("projectListInfoList"))){
            list = JsonUtils.json2List(JsonUtils.object2Json(param.get("projectListInfoList")),TrdAssetsProject001Dto.class);
            dto.setProjectListInfoList(list);
        }
        if(StringUtil.isNotEmpty(dto)){
            projListInfoDto.setSplitCprice(dto.getSplitCprice());//净价总额
            projListInfoDto.setSplitIntAmt(dto.getSplitIntAmt());//应计利息总额
            projListInfoDto.setSplitProcedureAmt(dto.getSplitProcedureAmt());//手续费
            projListInfoDto.setSplitChannelFee(dto.getSplitChannelFee());//通道费
            projListInfoDto.setSplitTradeAmt(dto.getSplitTradeAmt());//实付金额
//            if(dto.getUnSplitPrinAmt().compareTo(dto.getSplitPrinAmt())>0){//非最后一个拆分组合
//                projListInfoDto.setSplitCprice(ArithmeticUtil.doAdd(2,RoundingMode.HALF_UP,dto.getSplitCprice(),BigDecimal.ZERO));//净价总额
//                projListInfoDto.setSplitIntAmt(ArithmeticUtil.doAdd(2,RoundingMode.HALF_UP,dto.getSplitIntAmt(),BigDecimal.ZERO));//应计利息总额
//                projListInfoDto.setSplitProcedureAmt(ArithmeticUtil.doAdd(2,RoundingMode.HALF_UP,dto.getSplitProcedureAmt(),BigDecimal.ZERO));//手续费
//                projListInfoDto.setSplitChannelFee(ArithmeticUtil.doAdd(2,RoundingMode.HALF_UP,dto.getSplitChannelFee(),BigDecimal.ZERO));//通道费
//                projListInfoDto.setSplitTradeAmt(ArithmeticUtil.doAdd(2,RoundingMode.HALF_UP,dto.getSplitTradeAmt(),BigDecimal.ZERO));//实付金额
//            }
//            else{//倒减处理尾差
//                BigDecimal sumCpriceAmt = BigDecimal.ZERO;
//                BigDecimal sumIntAmt = BigDecimal.ZERO;
//                BigDecimal sumProcedureAmt = BigDecimal.ZERO;
//                BigDecimal sumChnnelFee = BigDecimal.ZERO;
//                BigDecimal sumTrdeAmt = BigDecimal.ZERO;
//                for (TrdAssetsProject001Dto preDto:dto.getProjectListInfoList()) {
//                    sumCpriceAmt = ArithmeticUtil.doAdd(sumCpriceAmt,preDto.getSplitCprice());
//                    sumIntAmt = ArithmeticUtil.doAdd(sumIntAmt,preDto.getSplitIntAmt());
//                    sumProcedureAmt = ArithmeticUtil.doAdd(sumProcedureAmt,preDto.getSplitProcedureAmt());
//                    sumChnnelFee = ArithmeticUtil.doAdd(sumChnnelFee,preDto.getSplitChannelFee());
//                    sumTrdeAmt = ArithmeticUtil.doAdd(sumTrdeAmt,preDto.getSplitTradeAmt());
//                }
//                projListInfoDto.setSplitCprice(ArithmeticUtil.doSub(dto.getCpriceAmt(),dto.getAlSplitCprice(),sumCpriceAmt));//净价总额
//                projListInfoDto.setSplitIntAmt(ArithmeticUtil.doSub(dto.getIntAmt(),dto.getAlSplitIntAmt(),sumIntAmt));//应计利息总额
//                projListInfoDto.setSplitProcedureAmt(ArithmeticUtil.doSub(dto.getProcedureAmt(),dto.getAlSplitProcedureAmt(),sumProcedureAmt));//手续费
//                projListInfoDto.setSplitChannelFee(ArithmeticUtil.doSub(dto.getChannelFee(),dto.getAlSplitChannelFee(),sumChnnelFee));//通道费
//                projListInfoDto.setSplitTradeAmt(ArithmeticUtil.doSub(dto.getTradeAmt(),dto.getAlSplitTradeAmt(),sumTrdeAmt));//实付金额
//            }
            projListInfoDto.setPortfolioId(dto.getPortfolioId());
            projListInfoDto.setPortfolioName(dto.getPortfolioName());
            projListInfoDto.setFinprodMarketId(dto.getFinprodMarketId());
            projListInfoDto.setFinprodId(dto.getFinprodId());
            projListInfoDto.setTradeDate(dto.getTradeDate());
            projListInfoDto.setSettleDate(dto.getSettleDate());
            projListInfoDto.setPrinAmt(dto.getSplitPrinAmt());
            projListInfoDto.setInvestRate(dto.getInvestRate());
            projListInfoDto.setUnitCprice(dto.getUnitCprice());
            projListInfoDto.setUnitFprice(dto.getUnitFprice());
            projListInfoDto.setChlId(dto.getChlId());
            if (StringUtil.isBlank(projListInfoDto.getTradeId())) {
                if (baseGetBusinessId != null) {
                    BussinessCodeParameter codeParameter = genBizCode(dto);
                    projListInfoDto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
                } else {
                    projListInfoDto.setTradeId(IDGenerator.generate());
                }
            }
        }
        return projListInfoDto;
    }

    /**
     * 查询组合信息
     * @param param 参数
     * @return 组合信息
     */
    @SuppressWarnings("unchecked")
    public List<SelectOptionDto> selectPortfolioId(Map<String, Object> param) {
        return simpleRestTemplate.postForObject(                
                ServiceApplication.TRADE_SERVICE_NAME+ "/trade/transfer/trdassetsproject/TrdAssetsProject/selectPortfolioId",
                param, List.class);
    }
    public static BussinessCodeParameter genBizCode(TrdAssetsProject001Dto dto) {
        DictCodeList item = DictCodeList.共通业务类型_项目类资产买入;
        if (DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())) {
            item = DictCodeList.共通业务类型_项目类资产卖出;
        }
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(item);
        parameter.setType1(类型转换(dto.getProjectType()));
        return parameter;
    }

    private static DictCodeList 类型转换(String projectType) {
        //DictCodeList result = null;
        if (DictCodeList.项目类一级分类_非标债权.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_非标债权;
        }
        else if (DictCodeList.项目类一级分类_理财直接融资工具.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_理财直融;
        }
        else if (DictCodeList.项目类一级分类_信贷资产流转项目.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_信贷资产流转项目;
        }
        else if (DictCodeList.项目类一级分类_基础设施基金.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_基础设施基金;
        }
        else if (DictCodeList.项目类一级分类_其他产业投资基金.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_其他产业投资基金;
        }
        else if (DictCodeList.项目类一级分类_其他权益类投资.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_信托产品;
        }
        else if (DictCodeList.项目类一级分类_结构化证券.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_结构化证券;
        }
        else if (DictCodeList.项目类一级分类_股票定向增发投资.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_股票定向增发;
        }
        else if (DictCodeList.项目类一级分类_券商收益凭证.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_券商收益凭证;
        }
        else if (DictCodeList.项目类一级分类_券商债权收益权投资.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_券商资产管理计划;
        }
        else if (DictCodeList.项目类一级分类_股票质押式回购.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_股票质押式回购;
        }
        return DictCodeList.业务编号一级分类_非标债权;
    }

    /**
     * 以组合维度生成dto，就是以利息明细维度组装新的dto，作为属性保存到流程entity中<br/>
     * 汇总 <br/>
     * 汇总扩展 <br/>
     * 汇总费用明细 <br/>
     * List<Dto> | 交易 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 附属 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 扩展 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 费用明细 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 组合交易 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| List<划款指令>
     */
    public List<BaseTradeInfoDto> genFinProductTradeDto(TrdAssetsProject001Dto entity) {
        List<BaseTradeInfoDto> resultList = new ArrayList<>();
        //转出
        String portfolioId = entity.getPortfolioId();
        entity.setPs(DictCodeList.交易方向_卖出.getValue());
        //生成卖出汇总交易编号
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = genBizCode(entity);
            entity.setBizCode(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            entity.setBizCode(IDGenerator.generate());
        }
        entity.setActPortfolioId(entity.getInvTransPortf());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getInvTransPortf()));
        entity.setCounterId(portfolioId);
        BaseTradeInfoDto result = new BaseTradeInfoDto();
        TrdProductDeal trdProductDeal = gen产品交易(entity);
        result.setTrdProductDeal(trdProductDeal);

        result.setTrdProductDealAdd(gen交易附属(entity, trdProductDeal));

        result.setPtlTrade(gen组合交易(entity));

//        result.setTrdTradeFeeDetail(gen汇总交易费用明细(entity));

        result.setStlPayOrders(gen划款指令流程(entity,trdProductDeal));

        result.setPaTrdProductDeal(gen交易扩展(entity));
        TrdFinProductDealTotal trdFinProductDealTotal = gen产品汇总交易(entity);
        result.setTrdFinProductDealTotal(trdFinProductDealTotal);

        result.setPaTrdFinProductDealTotal(gen汇总交易扩展(entity));

        result.setTrdDealTotalAdd(gen汇总交易附属(entity));

        resultList.add(result);
        //转入
        entity.setPs(DictCodeList.交易方向_买入.getValue());
        entity.setActPortfolioId(portfolioId);
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(portfolioId));
        entity.setCounterId(entity.getInvTransPortf());
        //生成买入汇总交易编号
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = genBizCode(entity);
            entity.setBizCode(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            entity.setBizCode(IDGenerator.generate());
        }
        //生成交易编号
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = genBizCode(entity);
            entity.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            entity.setTradeId(IDGenerator.generate());
        }
        BaseTradeInfoDto result1 = new BaseTradeInfoDto();
        TrdProductDeal trdProductDeal1 = gen产品交易(entity);
        trdProductDeal.setrTradeId(trdProductDeal1.getTradeId());
        result1.setTrdProductDeal(trdProductDeal1);

        result1.setTrdProductDealAdd(gen交易附属(entity, trdProductDeal1));

        result1.setPtlTrade(gen组合交易(entity));

//        result1.setTrdTradeFeeDetail(gen汇总交易费用明细(entity));

        result1.setStlPayOrders(gen划款指令流程(entity,trdProductDeal1));

        result1.setPaTrdProductDeal(gen交易扩展(entity));

        TrdFinProductDealTotal trdFinProductDealTotal1 = gen产品汇总交易(entity);
        result1.setTrdFinProductDealTotal(trdFinProductDealTotal1);

        result1.setPaTrdFinProductDealTotal(gen汇总交易扩展(entity));

        result1.setTrdDealTotalAdd(gen汇总交易附属(entity));

        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_项目类资产交易);
        entity.setPortfolioId(entity.getActPortfolioId());
        resultList.add(result1);
        return resultList;
    }

    private PaTrdProductDeal gen交易扩展(TrdAssetsProject001Dto entity) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        // 交易编号 = 金融产品交易Entity.交易编号
        paTrdProductDeal.setTradeId(entity.getTradeId());
        // 工作流代码
        paTrdProductDeal.setWorkflowId(entity.getWorkflowId());
        String paBussType = DictCodeListExt1.平安业务类型_项目类资产买入.getValue();
        if (DictCodeList.交易方向_卖出.getValue().equals(entity.getPs())) {
            paBussType = DictCodeListExt1.平安业务类型_项目类资产卖出.getValue();
        }
        paTrdProductDeal.setPaBussType(paBussType);

        paTrdProductDeal.setDeptCode(LoginUserUtil.getDeptCode());
        return paTrdProductDeal;
    }

    private StlPayOrders gen划款指令流程(TrdAssetsProject001Dto entity, TrdProductDeal trdProductDeal) {
        StlPayOrders stlPayOrdersDto = new StlPayOrders();
        // 后台自动生成划款指令
        BussSettleOrderParameterDto orderParameterDto = new BussSettleOrderParameterDto();
        // 是否汇总交易 = 是否_否
        orderParameterDto.setIsTotalTrade(DictCodeList.是否_否.getValue());

        // 金融产品交易Entity = 上述构造的金融产品交易Entity
        TrdProductDeal orderParameterDeal = new TrdProductDeal();
        // 交易日期 = 画面.交易日
        orderParameterDeal.setTradeDate(entity.getTradeDate());
        // 交易方向 = 交易方向_买入
        orderParameterDeal.setPs(trdProductDeal.getPs());
        // 币种 = 画面.币种
        orderParameterDeal.setCcy(entity.getCcy());
        // 交易金额 = 画面.本金金额
        orderParameterDeal.setTradeAmt(trdProductDeal.getTradeAmt());
        // 交易对手 = 画面.交易对手
        orderParameterDeal.setCounterId(trdProductDeal.getCounterId());

        orderParameterDto.setDeal(orderParameterDeal);
        // 拆借类型 = null
        orderParameterDto.setBankLendingType(null);
        // 组合代码 = 画面.组合代码
        orderParameterDto.setPortfolioId(entity.getPortfolioId());


        /*List<BussTrdTransferOrderDto> resultOrderDto = bussTrdTransferOrderService.generateOrder(orderParameterDto);
        if (resultOrderDto != null) {
            entity.setBussTrdTransferOrder(resultOrderDto);
        }
*/
        List<BussTrdTransferOrderDto> resultOrderDto = entity.getBussTrdTransferOrder();
        if (resultOrderDto != null) {
            if ("T01".equals(trdProductDeal.getPs())) {
                resultOrderDto.get(0).setPs("02");
                resultOrderDto.get(0).setPayType("02");
                resultOrderDto.get(0).setPortfolioId(resultOrderDto.get(0).getInPortfolioId());
                String orderId = "";
                if(baseGetBussinessId != null){
                    BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                    bussinessCodeParameter.setSeqNo("9998");
                    orderId = baseGetBussinessId.buildBussinessCode(bussinessCodeParameter);
                } else {
                    orderId = IDGenerator.generate();
                }
                resultOrderDto.get(0).setOrderId(orderId);
            }
        }

        if (!StringUtil.isEmptyList(resultOrderDto)) {
            BussTrdTransferOrderDto bttod = resultOrderDto.get(0);
            // 交易编号 = 业务编号 ※2生成的交易编号
            bttod.setTradeId(entity.getBizCode());
            // 指令状态 = 指令状态_新建
            bttod.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
            // 工作流代码
            bttod.setWorkflowId(entity.getWorkflowId());
            stlPayOrdersDto = bussTrdTransferOrderService.save(resultOrderDto).get(0);
        } else {
            stlPayOrdersDto = null;
        }
        return stlPayOrdersDto;
    }

    private List<PtlTrade> gen组合交易(TrdAssetsProject001Dto entity) {
        List<PtlTrade> result = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setBusiId(entity.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_项目类资产交易.getValue());
        ptlTrade.setSettleDate(entity.getTradeDate());
        ptlTrade.setPortfolioId(entity.getPortfolioId());
        ptlTrade.setBusiNo(entity.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setWorkflowId(entity.getWorkflowId());
        result.add(ptlTrade);
        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            ptlTrade1.setBusiId(entity.getFinprodId());
            ptlTrade1.setBusiType(DictCodeList.共通业务类型_项目类资产交易.getValue());
            ptlTrade1.setSettleDate(entity.getTradeDate());
            ptlTrade1.setPortfolioId(entity.getActPortfolioId());
            ptlTrade1.setBusiNo(entity.getTradeId());
            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade1.setWorkflowId(entity.getWorkflowId());
            result.add(ptlTrade1);
        }
        return result;
    }

    @SuppressWarnings("unused")
    private PtlTrade getPtlTrade(TrdAssetsProject001Dto entity, FinAssetIntInfo finAssetIntInfo, TrdProductDeal deal收息) {
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setBusiId(entity.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_项目类资产存续期.getValue());
        ptlTrade.setPortfolioId(finAssetIntInfo.getPortfolioId());
        ptlTrade.setBusiNo(deal收息.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setWorkflowId(entity.getWorkflowId());
        return ptlTrade;
    }

//    private List<TrdTradeFeeDetail> gen汇总交易费用明细(TrdAssetsProject001Dto entity) {
//        List<TrdTradeFeeDetail> result = new ArrayList<>();
//        if (entity.getSplitProcedureAmt() != null && BigDecimal.ZERO.compareTo(entity.getSplitProcedureAmt()) < 0) {
//            TrdTradeFeeDetailKey feeDetailKey = new TrdTradeFeeDetailKey();
//            feeDetailKey.setTradeId(entity.getSplitTradeId());
//            feeDetailKey.setFeeType(BussinessDictCodeList.费用类型_交易手续费.getValue());
//            TrdTradeFeeDetail procedureAmtDetail =  bussCommonMapperDelegateService.selectByMultiPrimaryKey(TrdTradeFeeDetailMapper.class,feeDetailKey,TrdTradeFeeDetail.class);
//            procedureAmtDetail.setFeeAmt(entity.getSplitProcedureAmt());
//            result.add(procedureAmtDetail);
//        }
//        if (entity.getSplitChannelFee() != null && BigDecimal.ZERO.compareTo(entity.getSplitChannelFee()) < 0) {
//            TrdTradeFeeDetail trdTradeFeeDetail1 = new TrdTradeFeeDetail();
//            if(StringUtil.equals(DictCodeList.是否_否.getValue(), entity.getTrdProductDeal().getIsOutTrade())){
//                trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_是.getValue());
//            }else{
//                if(StringUtil.equals("01", DictCodeListExt2.收取方式_回款前扣除.getValue())){
//                    trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_是.getValue());
//                }else{
//                    trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_否.getValue());
//                }
//            }
//            trdTradeFeeDetail1.setFeeAmt(entity.getSplitChannelFee());
//            trdTradeFeeDetail1.setTradeId(entity.getTradeId());
//            trdTradeFeeDetail1.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue()); // 一次性费用类型
//            result.add(trdTradeFeeDetail1);
//        }
//        return result;
//    }
    private TrdProductDealAdd gen交易附属(TrdAssetsProject001Dto entity, TrdProductDeal trdProductDeal) {
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
        trdProductDealAdd.setPortfolioId(entity.getPortfolioId());//转入/转出产品组合.组合代码
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        trdProductDealAdd.setWorkflowId(entity.getWorkflowId());
        return trdProductDealAdd;
    }
    private TrdProductDeal gen产品交易(TrdAssetsProject001Dto entity) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(entity.getTradeId());
        trdProductDeal.setTotleTradeId(entity.getBizCode());
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_项目类资产交易.getValue());
        //trdProductDeal.setFinprodType();
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        trdProductDeal.setBranch(Long.valueOf(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        trdProductDeal.setFinprodId(entity.getFinprodId());
        trdProductDeal.setTradeDate(entity.getTradeDate());//交易日期
        trdProductDeal.setVdate(entity.getVdate());//起息日
        trdProductDeal.setMdate(entity.getMdate());//到期日
        trdProductDeal.setSettleDate(entity.getSettleDate());//交割日期
        trdProductDeal.setCcy(entity.getCcy());
        trdProductDeal.setChlId(entity.getChlId());
        trdProductDeal.setUnitCprice(entity.getUnitCprice());//单位净价
        trdProductDeal.setUnitInt(entity.getUnitIntAmt());//单位利息
        trdProductDeal.setUnitFprice(entity.getUnitFprice());//单位全价
        trdProductDeal.setParValue(null);//单位面值
        trdProductDeal.setCpriceAmt(entity.getPrinAmt());//净价总额
        trdProductDeal.setShareAmt(entity.getPrinAmt());
        trdProductDeal.setPrinAmt(entity.getPrinAmt());
        trdProductDeal.setIntAmt(entity.getSplitIntAmt());//利息总额
        trdProductDeal.setFpriceAmt(entity.getPrinAmt());
        trdProductDeal.setTradeAmt(entity.getSplitTradeAmt());//交易金额
        trdProductDeal.setPs(entity.getPs());//交易方向（转入==买入；转出==卖出）
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdProductDeal.setIsOutTrade(DictCodeList.是否_否.getValue());
        trdProductDeal.setSettleSpeed(0l);
        trdProductDeal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        //是否撤销 默认否
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        //是否结清 默认是
        trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
        trdProductDeal.setWorkflowId(entity.getWorkflowId());
        if(StringUtil.equals(entity.getPs(),DictCodeList.交易方向_卖出.getValue())){
            trdProductDeal.setFpriceAmt(null);//全价总额
            trdProductDeal.setCounterId(entity.getCounterId());//交易对手
            trdProductDeal.setRemark("项目类资产转出");
            trdProductDeal.setInvAim(entity.getTurnInvAim());
        }
        else{
            trdProductDeal.setFpriceAmt(entity.getPrinAmt());//全价总额
            trdProductDeal.setCounterId(entity.getCounterId());//交易对手
            trdProductDeal.setRemark("项目类资产转入");
            trdProductDeal.setInvAim(entity.getInvAim());
        }
        trdProductDeal.setTrader(LoginUserUtil.getUserName());
        trdProductDeal.setIsSwapTransaction(DictCodeList.是否_是.getValue());
        return trdProductDeal;
    }
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
    }
    @Override
    public void doUpdate(TrdAssetsProject001Dto entity) {
        baseTradeProductDealCommServiceImpl.deleteWflData(entity.getWorkflowId());
        Map<String, List<BussTrdTransferOrderDto>> bussList = entity.getBussTrdTransferOrder().stream().collect(Collectors.groupingBy(BussTrdTransferOrderDto::getInPortfolioId));
        for (TrdAssetsProject001Dto dto:entity.getProjectListInfoList()) {
            dto.setSplitTradeId(entity.getTradeId());
            dto.setFinprodId(dto.getFinprodId());
            dto.setInvTransPortf(entity.getInvTransPortf());
            dto.setWorkflowId(entity.getWorkflowId());
            dto.setOperateType(entity.getOperateType());
            dto.setVdate(entity.getVdate());
            dto.setMdate(entity.getMdate());
            dto.setCpriceAmt(entity.getCpriceAmt());
            dto.setUnitIntAmt(entity.getUnitIntAmt());
            dto.setUnitFprice(entity.getUnitFprice());
            dto.setUnitCprice(entity.getUnitCprice());
            dto.setProjectType(entity.getProjectType());
            dto.setBussTrdTransferOrder(bussList.get(dto.getPortfolioId()));
            dto.setTurnInvAim(entity.getInvAim());
            // TODO 取转入组合
            dto.setBaseTradeInfoDtos(genFinProductTradeDto(dto));
        }
        ProcessMark newEntity = null;
        try {
            newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdAssetsProject001Dto.class, true);
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
        for (TrdAssetsProject001Dto dto:entity.getProjectListInfoList()) {
            saveDataWhenStartWorkflow(dto);
        }
    }
    @Override
    public void doBack(TrdAssetsProject001Dto entity) {
        List<WflTrdProductDeal> trdProductDealList = null;
        if (StringUtil.isNotEmpty(entity.getWorkflowId())) {
            trdProductDealList = simpleRestTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME+ "/trade/transfer/common/TransferCommon/selectDealByworkflowId",
                    entity.getWorkflowId(), WflTrdProductDeal.class);
        }
        if (!StringUtil.isEmptyList(trdProductDealList)) {
            for (WflTrdProductDeal deal : trdProductDealList) {
                AllTradeCommonUtil.deletePosition("",deal.getTradeId());
            }
        }
    }

    private  TrdFinProductDealTotal gen产品汇总交易(TrdAssetsProject001Dto entity) {
        TrdFinProductDealTotal trdProductDealTotal = new TrdFinProductDealTotal();
        trdProductDealTotal.setTotleTradeId(entity.getBizCode());
        trdProductDealTotal.setBusiType(DictCodeList.共通业务类型_项目类资产交易.getValue());
        trdProductDealTotal.setFinprodId(entity.getFinprodId());
        trdProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        trdProductDealTotal.setCcy(entity.getCcy());
        trdProductDealTotal.setBranch(Long.valueOf(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        trdProductDealTotal.setUnitCprice(entity.getUnitCprice());
        trdProductDealTotal.setUnitInt(entity.getUnitIntAmt());
        trdProductDealTotal.setUnitFprice(entity.getUnitFprice());
        trdProductDealTotal.setCpriceAmt(entity.getCpriceAmt());
        trdProductDealTotal.setPortfolioId(entity.getPortfolioId());

        trdProductDealTotal.setCcy(entity.getCcy());
        trdProductDealTotal.setPs(entity.getPs());
        trdProductDealTotal.setTradeDate(entity.getTradeDate());
        trdProductDealTotal.setSettleDate(entity.getTradeDate());
        trdProductDealTotal.setSettleSpeed(0L);
        trdProductDealTotal.setVdate(entity.getVdate());//起息日
        trdProductDealTotal.setMdate(entity.getMdate());//到期日
        trdProductDealTotal.setPrinAmt(entity.getPrinAmt());
        trdProductDealTotal.setShareAmt(entity.getPrinAmt());
        trdProductDealTotal.setIntAmt(entity.getIntAmt());
//        trdProductDealTotal.setFpriceAmt(entity.getFpriceAmt());
        trdProductDealTotal.setTradeAmt(entity.getTradeAmt());
        trdProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdProductDealTotal.setTrader(LoginUserUtil.getUserName());
        if(StringUtil.equals(entity.getPs(),DictCodeList.交易方向_卖出.getValue())){
            trdProductDealTotal.setFpriceAmt(null);//全价总额
            trdProductDealTotal.setCounterId(entity.getPortfolioId());//交易对手
            trdProductDealTotal.setRemark("项目类资产转出");
        }
        else{
            trdProductDealTotal.setFpriceAmt(entity.getPrinAmt());//全价总额
            trdProductDealTotal.setCounterId(entity.getInvTransPortf());//交易对手
            trdProductDealTotal.setRemark("项目类资产转入");
        }//        trdProductDeal.setIsOutTrade(entity.getTrdInvestPersonInfo().getIsOutTrade());
        trdProductDealTotal.setIsOutTrade("Y");

//        trdProductDealTotal.setCounterTrader(entity.getTrdInvestPersonInfo().getCounterTrader());
//        trdProductDealTotal.setCounterContact(entity.getTrdInvestPersonInfo().getContactInfo());
        //总交易费用 = 手续费 + 通道费
//        trdProductDealTotal.setFeeAmt(ArithmeticUtil.doAdd(entity.getCommissionAmt(), entity.getChannelFee()));
        //随交易支付费用
        //内部交易对手  通道费随交易支付  外部交易对手根据通道收取方式
//        if(StringUtil.equals(trdProductDealTotal.getIsOutTrade(), DictCodeList.是否_否.getValue())){
//            trdProductDealTotal.setFeeAmtPay(ArithmeticUtil.doAdd(entity.getCommissionAmt(), entity.getChannelFee()));
//        }else{
//            if(StringUtil.equals(entity.getCollectType(), DictCodeListExt2.收取方式_回款前扣除.getValue())){
//                trdProductDealTotal.setFeeAmtPay(ArithmeticUtil.doAdd(entity.getCommissionAmt(), entity.getChannelFee()));
//            }else{
//                trdProductDealTotal.setFeeAmtPay(entity.getCommissionAmt());
//                trdProductDealTotal.setFeeAmtUnpay(entity.getChannelFee());
//            }
//        }
//		标的挂通道，通道费不随交易支付
        trdProductDealTotal.setFeeAmtPay(entity.getCommissionAmt());
        trdProductDealTotal.setFeeAmtUnpay(entity.getChannelFee());

        trdProductDealTotal.setRemark(entity.getRemark());
        trdProductDealTotal.setChlId(entity.getChlId());
//        if(entity.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
//            //还本不带息的资产才保存
//            Map<String, Object> param = new HashMap<String, Object>();
//            param.put("finprodId", entity.getFinprodId());
//            param.put("settleDate", entity.getSettleDate());
//            Map<String, Object> result = findRepayFlag(param);
//            if (result.get("repayIntFlag") != null && result.get("repayIntFlag").equals("N")) {
//                trdProductDealTotal.setIspayUnpaidint("Y");
//            }
//        }
//        trdProductDealTotal.setPayUnpaidintAmt(entity.getSplitIntAmt());//利息总额
        trdProductDealTotal.setPayUnpaidintAmt(new BigDecimal("0"));//支付未支付利息
        trdProductDealTotal.setIspayUnpaidint("N");// 是否转让还本未支付利息
        trdProductDealTotal.setPortfolioId(entity.getActPortfolioId());//存子组合
        return trdProductDealTotal;
    }

    public Map<String, Object> findRepayFlag(Map<String, Object> params) {
        if (StringUtil.isNotEmpty((java.lang.String)params.get("finprodId"))
                && params.get("settleDate") != null) {
            return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdProjectInvestTrade/findRepayFlag",
                    params, Map.class);
        }
        return null;
    }

    private static PaTrdFinProductDealTotal gen汇总交易扩展(TrdAssetsProject001Dto entity) {
        PaTrdFinProductDealTotal paTrdProductDeal = new PaTrdFinProductDealTotal();
        paTrdProductDeal.setTotleTradeId(entity.getBizCode());
        if(entity.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
            paTrdProductDeal.setTheoryInt(entity.getSplitIntAmt());//
        }
        String paBussType = DictCodeListExt1.平安业务类型_项目类资产买入.getValue();
        if (DictCodeList.交易方向_卖出.getValue().equals(entity.getPs())) {
            paBussType = DictCodeListExt1.平安业务类型_项目类资产卖出.getValue();
        }
        paTrdProductDeal.setPaBussType(paBussType);
        paTrdProductDeal.setDeptCode(LoginUserUtil.getDeptCode());
        paTrdProductDeal.setWorkflowId(entity.getWorkflowId());
        return paTrdProductDeal;
    }

    private static TrdDealTotalAdd gen汇总交易附属(TrdAssetsProject001Dto entity) {
        TrdDealTotalAdd trdProductDealAdd = new TrdDealTotalAdd();
        trdProductDealAdd.setTotleTradeId(entity.getBizCode());
//        trdProductDealAdd.setEntrustId(entity.getEntrustId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        trdProductDealAdd.setWorkflowId(entity.getWorkflowId());
        return trdProductDealAdd;
    }
	
	 /**
     * 获取处于流程中的持仓（但是未记录在在持仓流水中）
     * @return 处于流程中的持仓（但是未记录在在持仓流水中）
     * @see
     * @since 1.0
     */
    public BigDecimal selectWflAmtAsset(Map<String,Object> map){
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/trade/transfer/trdassetsproject/TrdAssetsProject/selectWflAmtAsset",map,
        		BigDecimal.class);
    }
    
    
    public List<TrdProductDeal> generateOrdersNQs(TrdAssetsProject001Dto dto, List<TrdAssetsProject001Dto> dtoList) {
        List<TrdProductDeal> trdProductDealList = new ArrayList<>();
        //托管户查询
        String portfolioId = dto.getPortfolioId();
        List<PtlCapacctInfo> ptlCapacctList = etrTrdCommonMapper.getPtlAcctInfo(portfolioId, Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()));

        for (TrdAssetsProject001Dto fundAssetDto : dtoList) {
            String portfolioIdIn = fundAssetDto.getPortfolioId();
            List<PtlCapacctInfo> ptlCapacctListIn = etrTrdCommonMapper.getPtlAcctInfo(portfolioIdIn, Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()));

            TrdProductDeal trdProductDeal = getOrder(fundAssetDto);
            if (ptlCapacctList.size() == 1 && ptlCapacctListIn.size() == 1 && ptlCapacctList.get(0).equals(ptlCapacctListIn.get(0))) {
                //※划款指令共通页面调用【业务组件.划款指令共通组件.指令生成】，页面不展示
                trdProductDeal.setStlPayOrderShow("N");
            } else if (ptlCapacctList.size() == 1 && ptlCapacctListIn.size() == 1
                    && ptlCapacctList.get(0).getAcctNo() != ptlCapacctListIn.get(0).getAcctNo()) {
                //页面展示，不做任何处理
            } else {
                //页面展示,但是划款账号设置为空
                trdProductDeal.setStlPayOrderShow("Y");
            }
            trdProductDealList.add(trdProductDeal);
        }
        return trdProductDealList;
    }

    public TrdProductDeal getOrder(TrdAssetsProject001Dto dto) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, dto);
        trdProductDeal.setTradeId(dto.getTradeId());
        trdProductDeal.setCounterId(dto.getPortfolioId());
        trdProductDeal.setPs(DictCodeList.交易方向_卖出.getValue());
        trdProductDeal.setIsOutTrade("N");
        return trdProductDeal;
    }
    

}
