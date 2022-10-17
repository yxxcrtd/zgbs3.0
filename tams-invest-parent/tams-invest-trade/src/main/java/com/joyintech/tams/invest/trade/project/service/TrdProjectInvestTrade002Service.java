package com.joyintech.tams.invest.trade.project.service;

import static com.joyintech.tams.invest.common.trade.AllTradeCommonUtil.copyFileInfoByFileNo;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.trade.dao.TrdCommonBaseMapper;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.BasePtlRecePositionService;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.dto.PtlRecePositionFlowDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.trade.BaseTradeProductDealService;
import com.joyintech.tams.invest.common.component.base.trade.impl.dao.BaseTradeProductDealMapper;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.MstFileOutputParameter;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.risktrialrds.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.threadservice.JasperUtil;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.trade.service.TradeProductDealCommService;
import com.joyintech.tams.invest.trade.bond.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.trade.project.dto.TrdProjectInvestTrade002Dto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.period.trdbondduration.service
 * @Description: 债券存续期管理-查询
 * @date Date : 2018年09月21日 14:32
 */
@Service
public class TrdProjectInvestTrade002Service extends BaseService<TrdProjectInvestTrade002Dto> implements BaseProcessService<TrdProjectInvestTrade002Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdProjectInvestTrade002Service.class);

    @Autowired
    private TradeProductDealCommService baseTradeProductDealCommServiceImpl;

    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderImplService;

    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;

    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * 共通Mapper的通用代理方案
     */
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    /**
     * DateCalService
     */
    @Autowired(required = false)
    private DateCalService dateCalService;

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候检查service
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;
    

    @Autowired(required = false)
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;

    @Autowired
    PtlTradeMapper ptlTradeMapper;

    @Autowired
    private TrdCommonBaseMapper trdCommonBaseMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    @Autowired
    private BasePtlRecePositionService basePtlRecePositionService;
    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
    @Autowired
    private BaseTradeProductDealService baseTradeProductDealService;
    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private  PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;
    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;
    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;
    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;
    @Autowired
    private  MstFileCommonService mstFileCommonService;
    @Autowired
    private BaseTradeProductDealMapper baseTradeProductDealMapper;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    /**
     *	 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    
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
//           交易撤销时交易编号无R结尾 20190413 start
            if(OperationType.REVOKE.equals(entity.getOperType())&&!tradeId.endsWith("R")){
                tradeId+="R";
            }
//           交易撤销时交易编号无R结尾 20190413 end
            if (!multiApproveTradeCheckService.multiApproveTradeCheck(tradeId)) {
                return getMessage("BUSS_COM_E_0100", tradeId);
            }
        }
        return null;
    }

    public TrdProjectInvestTrade002Dto findEffectiveData(Map<String, Object> pageParam) {
        /** modify by yanya on 20201229 项目交易资产端数据字典项变更，以估值侧为准 start**/
        //pageParam.put("feeType06",DictCodeList.费用品种类型_交易手续费.getValue());
        pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        /** modify by yanya on 20201229 项目交易资产端数据字典项变更，以估值侧为准 end**/
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdProjectInvestTrade/findEffectiveData",
            pageParam, TrdProjectInvestTrade002Dto.class);
    }

    @Override
    public void doComplete(TrdProjectInvestTrade002Dto entity, OperationType operationType) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            // 反向交易生成汇总表数据
            String autoGenerateNegateDealFlag = restTemplate.getForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectParam/" + "COM" + "/"
                            + BussinessConstant.IS_AUTO_GENERATE_NEGATE_DEAL,
                    String.class);
            String 反向交易编号 = null;
            if (DictCodeList.是否_是.getValue().equals(autoGenerateNegateDealFlag)
                    && (DictCodeList.交易方向_买入.getValue().equals(entity.getPs())
                    || DictCodeList.交易方向_卖出.getValue().equals(entity.getPs()))) {
                PtlPortfolio mcp = ptlPortfolioMapper.selectByPrimaryKey(entity.getTrdInvestPersonInfo().getCounterId());
                if (mcp != null) {
                    反向交易编号 = getTradeId(entity.getPs(),entity.getProjectType());
                }
            }
            // 7.1登录各业务表，包括金融产品交易、金融产品附属、划款指令及附件信息
            // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易表
            BaseTradeInfoDto trade = entity.getBaseTradeInfoDto();
            TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, trade.getTrdFinProductDealTotal());
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdFinProductDealTotal.setBusinessModel(entity.getBusinessModel());
            if(!StringUtil.isNotEmpty(trdFinProductDealTotal.getInvAim())) {
                if (StringUtil.equals("Y1", trdFinProductDealTotal.getBusinessModel())) {
                    trdFinProductDealTotal.setInvAim("AC");
                 } else if (StringUtil.equals("Y2", trdFinProductDealTotal.getBusinessModel())) {
                    trdFinProductDealTotal.setInvAim("FVTPL");
                }
            }
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, trade.getTrdProductDeal());
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

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

            // 组合交易表Entity -> 组合交易信息表
            if (CollectionUtils.isNotEmpty(trade.getPtlTrade())) {
                trade.getPtlTrade().forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
            }

            // 插入风险试算结果
            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), paTrdProductDeal.getTradeId());

            // 批量插入
            paTrdProductDealMapper.insertSelective(paTrdProductDeal);
            paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);
            trdDealTotalAddMapper.insertSelective(trdTotalAdd);
            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
            StlPayOrdersDto stlPayOrders = null;
            if (!com.joyintech.framework.util.StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
                stlPayOrders = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, trade.getStlPayOrders());
            }

            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            baseTradeProductDealDto.setTrdFinProductDealTotal(trdFinProductDealTotal);//汇总交易
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
            baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
            //        委托代码不为空处理委托代码
            if(StringUtil.isNotEmpty(entity.getEntrustId())){
                baseTradeProductDealDto.setEntrustId(entity.getEntrustId());
            }
            // 划款指令
            if (stlPayOrders != null) {
                baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrders));
            }
            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
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
           // stlPayOrdersMapper.insertSelective(stlPayOrders);

            if(entity.getTrdProductDealList()!=null && entity.getTrdProductDealList().size()>0){//有转入转出交易
                List<BaseTradeProductDealDto> list=new ArrayList<>();
                FinProductTradeDto finDto = new FinProductTradeDto();
                finDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                for(TrdProductDeal trdDeal:entity.getTrdProductDealList()){
                    TrdProductDealAdd trdDealAdd = new TrdProductDealAdd();
                    trdDealAdd=this.setReceTrdProductDealAdd(entity.getPortfolioId(),
                            entity.getEntrustId(),trdDeal.getTradeId());
                    trdDealAdd.setWorkflowId(entity.getWorkflowId());
                    PaTrdProductDeal paTrdPDeal = new PaTrdProductDeal();
                    paTrdPDeal=gen交易扩展(entity);
                    paTrdPDeal.setTradeId(trdDeal.getTradeId());
                    paTrdPDeal.setWorkflowId(entity.getWorkflowId());
                    BaseTradeProductDealDto baseDto = new BaseTradeProductDealDto();
                    baseDto.setRecType(DictCodeListExt1.应收款类型_还本未收利息.getValue());
                    baseDto.setPayDate(getPayDate(trdDeal.getFinprodId(),trdDeal.getBranch()));
                    baseDto.setTrdProductDeal(trdDeal);
                    baseDto.setTrdProductDealAdd(trdDealAdd);
                    baseDto.setPaTrdProductDeal(paTrdPDeal);
                    baseDto.setPtlTrade(this.setRecePtlTrade(entity,trdProductDeal.getTradeId()));
                    if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                        baseDto.setActPortfolioId(entity.getActPortfolioId());//子组合
                    }
                    list.add(baseDto);
                }
                finDto.setBaseTradeProductDealDtos(list);

                entity.setFinRecdProductTradeDto(finDto);//反向交易生成
                basePtlRecePositionService.execute(finDto);
                if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    finDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                    basePtlRecePositionService.insertPosition(finDto);
                    finDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
                }

            }

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

//            if (!DictCodeList.交易方向_卖出.getValue().equals(trade.getTrdProductDeal().getPs())) {
                // 7.3写入交易信息进交易报送信息表，为中债报送用
                this.dealInfoCbReportLogic_7_3(entity);

                // 7.4更新资产负债报送状态
                this.assetDebtCbReportLogic_7_4(entity);
//            }
//            else {
//                String tradeId = baseGetBusinessIdImpl.buildBussinessCode(genBizCode(entity));
//                // 插入卖出到账的流程中的数据
//                trade.getTrdProductDeal().setPs(DictCodeList.交易方向_卖出到账.getValue());
//                trade.getTrdProductDeal().setWorkflowId(null);
//                trade.getTrdProductDeal().setbTradeId(trade.getTrdProductDeal().getTradeId());
//                trade.getTrdProductDeal().setTradeId(tradeId);
//                trade.getPaTrdProductDeal().setPaBussType(DictCodeListExt1.平安业务类型_项目类资产卖出到账.getValue());
//                trade.getPaTrdProductDeal().setWorkflowId(null);
//                trade.getPaTrdProductDeal().setTradeId(tradeId);
//              //卖出到账不需要完成委托额度
//                trade.getTrdProductDealAdd().setEntrustId(null);
//                trade.getTrdProductDealAdd().setWorkflowId(null);
//                trade.getTrdProductDealAdd().setTradeId(tradeId);
//                trade.getStlPayOrders().setTradeId(tradeId);
//
//                if (CollectionUtils.isNotEmpty(trade.getPtlTrade())) {
//                    trade.getPtlTrade().forEach(ptlTrade -> ptlTrade.setBusiNo(tradeId));
//                }
//                //卖出到账手续费不入库问题
//                if(trade.getTrdTradeFeeDetail()!=null&&!trade.getTrdTradeFeeDetail().isEmpty()){
//                	for(TrdTradeFeeDetail fee:trade.getTrdTradeFeeDetail()){
//                		fee.setTradeId(tradeId);
//                	}
//                }
//
//                PtlPortfolio portfolio = bussCommonMapperDelegateService.selectBySinglePrimaryKey(PtlPortfolioMapper.class,
//                        trade.getTrdProductDeal().getCounterId(), PtlPortfolio.class);
//                // 内部交易直接生效卖出到账的交易
//                //卖出到账不需要完成委托额度
//                entity.setEntrustId(null);
//                if (portfolio != null) {
//                    doComplete(entity, operationType);
//                }
//                else {
//                    // 否则，生成流程中的卖出到账的交易
//                    saveDataWhenStartWorkflow(entity);
//                }
//            }
            // 一体化对应etliu start
            // 所有交易生效后调用的共通方法
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            inputDto.setProdid(entity.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            inputDto.setTrdProductDeal(trdProductDeal);
            // <!-- 通道交易 新增 强成西 2019/03/26 start -->
            inputDto.setTrdProductDealAdd(trade.getTrdProductDealAdd());
//          if (entity.getBaseTradeInfoDto().getTrdTradeFeeDetail() != null && !entity.getBaseTradeInfoDto().getTrdTradeFeeDetail().isEmpty()) {
//              TrdTradeFeeDetail detail = entity.getBaseTradeInfoDto().getTrdTradeFeeDetail().stream().filter(
//                      trdTradeFeeDetail -> DictCodeListExt1.一次性费用类型_通道费.getValue().equals(trdTradeFeeDetail.getFeeType())).findFirst().get();
//              inputDto.setTrdTradeFeeDetail(detail);
//          }
            TrdTradeFeeDetail detail = null;
            if(entity.getBaseTradeInfoDto().getTrdTradeFeeDetail() != null && !entity.getBaseTradeInfoDto().getTrdTradeFeeDetail().isEmpty()&&StringUtil.isNotEmpty(entity.getBaseTradeInfoDto().getTrdTradeFeeDetail())){
            	for(TrdTradeFeeDetail trdTradeFeeDetail :entity.getBaseTradeInfoDto().getTrdTradeFeeDetail()){
            		if(StringUtil.equals(DictCodeListExt1.一次性费用类型_通道费.getValue(), trdTradeFeeDetail.getFeeType())){
            			detail =trdTradeFeeDetail;
            		}
            	}
            }
            inputDto.setTrdTradeFeeDetail(detail);

            inputDto.setTurnInvAim(entity.getTurnInvAim());

            // <!-- 通道交易 新增 强成西 2019/03/26 end -->
            if(反向交易编号!=null){
                inputDto.getTrdProductDeal().setTotleTradeId(反向交易编号);
                if(stlPayOrders!=null){
                    stlPayOrders.setTradeId(反向交易编号);
                    stlPayOrders.setPortfolioId(entity.getTrdInvestPersonInfo().getCounterId());
                }
                if(entity.getFinRecdProductTradeDto()!=null){
                    inputDto.setTotleTradeId(反向交易编号);
                    inputDto.setBaseReceTradeProductDealDtoList(entity.getFinRecdProductTradeDto().getBaseTradeProductDealDtos());//应收款转出转入
                }
            }
            AllTradeCommonUtil.setDeals(inputDto, trdProductDeal, trdProductDealAdd, paTrdProductDeal, Arrays.asList(stlPayOrders),
                    trade.getPtlTrade(), entity.getActPortfolioId());
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                inputDto.setActPortfolioId(entity.getActPortfolioId());
            }
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            // 一体化对应etliu end
         // 调用投管接口 add by cxq 20190217 start
            InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
            investInterfCommDto.setPageId("TrdProjectInvestTrade002");
            investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
            investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
            investInterfCommDto.setTrdProductDeal(trdProductDeal);
            investInterfCommDto.setTrdProductDealAdd(trdProductDealAdd);
            investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
            investInterfCommDto.setTradeFeeDetaillist(gen汇总交易费用明细(entity));
            investInterfCommService.doInvestInterf(investInterfCommDto);
            // 调用投管接口 add by cxq 20190217 end
            if (反向交易编号 != null) {
                String 交易对手_子组合 = entity.getTrdInvestPersonInfo().getCounterId();// 子组合交易对手
                String 交易对手的母组合 = AllTradeCommonUtil.exchangePortfolio(entity.getTrdInvestPersonInfo().getCounterId());// 交易对手的母组合，设置成反向交易的组合代码

                // 组合代码与交易对手对调
                // 交易方向为买入；
                // 其他字段与原始交易相同；
                // String 原交易母组合代码 = trdProductDealAdd.getPortfolioId();
                String 原交易子组合代码 = entity.getActPortfolioId();

                TrdFinProductDealTotal trdProductDealTotal = new TrdFinProductDealTotal();
                trdProductDealTotal=gen产品汇总交易(entity);
                trdProductDealTotal.setTotleTradeId(反向交易编号);
                trdProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                trdProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
                trdProductDealTotal.setCounterId(原交易子组合代码);
                trdProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
                trdProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
                trdProductDealTotal.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue")));
                trdProductDealTotal.setPs(DictCodeList.交易方向_买入.getValue());
                trdProductDealTotal.setPortfolioId(交易对手_子组合);//原来交易的交易对手
                trdProductDealTotal.setIspayUnpaidint(null);//是否转让
                trdProductDealTotal.setBusinessModel(entity.getBusinessModel());

                trdProductDealTotal.setInvAim(entity.getTurnInvAim());
                trdFinProductDealTotalMapper.insertSelective(trdProductDealTotal);

                PaTrdFinProductDealTotal paTrdFinDealTotal1 = gen汇总交易扩展(entity);
                paTrdFinDealTotal1.setTotleTradeId(反向交易编号);
                paTrdFinDealTotal1.setPaBussType(DictCodeListExt1.平安业务类型_项目类资产买入.getValue());
                paTrdFinProductDealTotalMapper.insertSelective(paTrdFinDealTotal1);

                TrdDealTotalAdd trdDealTotalAdd1 = setTrdDealTotalAdd(entity);
                trdDealTotalAdd1.setTotleTradeId(反向交易编号);
                trdDealTotalAddMapper.insertSelective(trdDealTotalAdd1);

                if (!StringUtil.isEmptyList(entity.getUploadFileList())) {
                    for (UploadFile uploadFile : entity.getUploadFileList()) {
                        copyFileInfoByFileNo(uploadFile.getFileNo(), 反向交易编号);
                    }
                }
            }
            jasperExecute(trdProductDeal);
        }
        // 撤销
        else {
            this.doCompleteRevoke(entity);
        }
//        调用接口  add by zhuyongkang 20191127 start
        List<String> tradeIds = new ArrayList<>();
        tradeIds.add(entity.getBizCode());
        try {
            restTemplate.postForObject(
                    ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
                    Map.class);
        }catch (Exception e){

        }
//        调用接口  add by zhuyongkang 20191127 end
    }

    @Autowired
    private JasperUtil jasperUtil;

    public void jasperExecute(TrdProductDeal trdProductDeal) {
        if("001".equals(CacheUtil.getSysParam("CUSTOMER_BANK_CODE"))){
            //生成划款指令附件:调用自定义接口项目生成Jasper报表 add by caoxue 2019-07-29 start
            jasperUtil.jasperExecute(Arrays.asList(trdProductDeal), restTemplate);
            //生成划款指令附件:调用自定义接口项目生成Jasper报表 add by caoxue 2019-07-29 end
        }
    }

    /**
     * 撤销doComplete
     *
     * @param entity 参数
     * @see
     * @since 1.0
     */
    private void doCompleteRevoke(TrdProjectInvestTrade002Dto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTotelTradeInfo?tradeId=" + entity.getBizCode(),
            BaseTradeProductDealDto.class);
        //如果是通过通道的交易，也需要撤销带通道的交易编号


        // 提前查询判断  是否  撤销 并且是 有转仓交易 并且转仓交易方向ps为买入
        String reverseTradeId = "";

        TrdProductDeal reverseTrdProductDeal = trdCommonBaseService.selectZCDealByTid(entity.getTradeId(),true);
        if (reverseTrdProductDeal != null && StringUtil.equals(DictCodeList.交易方向_买入.getValue(), reverseTrdProductDeal.getPs())) {
            reverseTradeId = reverseTrdProductDeal.getTradeId();
        }

        if(null!=entity.getChlId()){
            List<TrdProductDeal>  chlList=baseTradeProductDealService.selectTrdProductChlDealsByTotalTradeId(entity.getTradeId());//根据交易编号，查询通道交易编号
            if(null!=chlList && chlList.size()>0){
                for(TrdProductDeal chlDeal:chlList){
                    //更新，是否已撤销 = 数据字典缓存.是否.是
                    Map<String,Object> map = new HashMap<String,Object>();
                    map.put("tradeStatus", DictCodeListExt1.交易状态_撤销.getValue());
                    map.put("isCancle", DictCodeList.是否_是.getValue());
                    map.put("trdProductDealList", chlDeal.getTradeId());
                    baseTradeProductDealMapper.updateChlTrdProDeal(map);
                    //修改指令状态
                    baseTradeProductDealMapper.updateStlTrdProDeal(map);

                }
            }
        }

        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        if (!com.joyintech.framework.util.StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                baseDto.getTrdFinProductDealTotal().setWorkflowId(entity.getWorkflowId());
                baseDto.getTrdFinProductDealTotal().setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                baseDto.getTrdFinProductDealTotal().setpTotleTradeId(baseDto.getTrdFinProductDealTotal().getTotleTradeId());
                baseDto.getTrdFinProductDealTotal().setTotleTradeId(baseDto.getTrdFinProductDealTotal().getTotleTradeId() + 'R');
                baseDto.getTrdFinProductDealTotal().setRemark(entity.getRevokeReason());
                trdFinProductDealTotalMapper.insert(baseDto.getTrdFinProductDealTotal());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("isCancle", DictCodeList.是否_是.getValue());
                map.put("totalTradeId", entity.getBizCode());
                baseTradeProductDealMapper.updateTrdProdTotalDeal(map);
                baseDto.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                baseDto.getTrdProductDeal().setCancelRemark(entity.getRevokeReason());
                baseDto.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId());
                entity.setTradeId(baseDto.getTrdProductDeal().getTradeId());//撤销时交易的主键
                if(StringUtil.isNotEmpty( baseDto.getTrdProductDeal().getFinprodId())){
                	Map<String, Object> pageParam = new HashMap<>();
                	pageParam.put("finprodId",  baseDto.getTrdProductDeal().getFinprodId());
                	List<SelectOptionDto> selectOptionDto = restTemplate.postForList(ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/getChannelIdAndCollectTypeForAsset",
                			pageParam , SelectOptionDto.class);
                	if(!StringUtil.isEmptyList(selectOptionDto)){
                		//设置通道id  撤销通道持仓
                		baseDto.getTrdProductDeal().setChlId(selectOptionDto.get(0).getId());
                	}
                }
                if (bussCommonMapperDelegateService != null) {
                    PaTrdProductDeal paTrdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                            PaTrdProductDealMapper.class, baseDto.getTrdProductDeal().getTradeId(), PaTrdProductDeal.class);
                    paTrdProductDeal.setTradeId(
                            paTrdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    paTrdProductDeal.setCreateUser(LoginUserUtil.getUserName());
                    paTrdProductDeal.setCreateTime(new Date());
                    paTrdProductDeal.setUpdateUser(paTrdProductDeal.getCreateUser());
                    paTrdProductDeal.setUpdateTime(paTrdProductDeal.getCreateTime());
//                    bussCommonMapperDelegateService.insertBatch(PaTrdProductDealMapper.class, paTrdProductDeal);
                    paTrdProductDealMapper.insertSelective(paTrdProductDeal);

                    PtlTrade ptlTrade = new PtlTrade();
                    ptlTrade.setBusiNo(entity.getTradeId());
                    ptlTrade.setPortfolioId(entity.getPortfolioId());
                    ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                    ptlTrade = bussCommonMapperDelegateService.selectByMultiPrimaryKey(PtlTradeMapper.class, ptlTrade, PtlTrade.class);
                    ptlTrade.setBusiNo(ptlTrade.getBusiNo() + "R");
                    ptlTradeMapper.insertSelective(ptlTrade);
//                    bussCommonMapperDelegateService.insertBatch(PtlTradeMapper.class, ptlTrade);
                    if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                        PtlTrade actPtlTrade = new PtlTrade();
                        BeanUtil.copyPropertiesIgnoreNull(actPtlTrade, ptlTrade);
                        actPtlTrade.setBusiNo(entity.getTradeId());
                        actPtlTrade.setPortfolioId(entity.getActPortfolioId());
                        actPtlTrade = bussCommonMapperDelegateService.selectByMultiPrimaryKey(PtlTradeMapper.class, actPtlTrade, PtlTrade.class);
                        actPtlTrade.setBusiNo(ptlTrade.getBusiNo());
//                        bussCommonMapperDelegateService.insertBatch(PtlTradeMapper.class, ptlTrade);
                        ptlTradeMapper.insertSelective(actPtlTrade);
                    }
                }
                TrdDealTotalAdd totalAdd = trdDealTotalAddMapper.selectByPrimaryKey(entity.getBizCode());
                if (totalAdd != null) {
                    totalAdd.setTotleTradeId(
                            totalAdd.getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    totalAdd.setCreateUser(LoginUserUtil.getUserName());
                    totalAdd.setCreateTime(new Date());
                    totalAdd.setUpdateUser(totalAdd.getCreateUser());
                    totalAdd.setUpdateTime(totalAdd.getCreateTime());
                    totalAdd.setWorkflowId(entity.getWorkflowId());
                    trdDealTotalAddMapper.insertSelective(totalAdd);
                }
                PaTrdFinProductDealTotal paTrdFinProductDealTotal = paTrdFinProductDealTotalMapper.selectByPrimaryKey(entity.getBizCode());
                if (paTrdFinProductDealTotal != null) {
                    paTrdFinProductDealTotal.setTotleTradeId(
                            paTrdFinProductDealTotal.getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    paTrdFinProductDealTotal.setCreateUser(LoginUserUtil.getUserName());
                    paTrdFinProductDealTotal.setCreateTime(new Date());
                    paTrdFinProductDealTotal.setUpdateUser(paTrdFinProductDealTotal.getCreateUser());
                    paTrdFinProductDealTotal.setUpdateTime(paTrdFinProductDealTotal.getCreateTime());
                    paTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
                    paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);
                }
                if (baseDto.getStlPayOrdersList() != null && baseDto.getStlPayOrdersList().size() > 0) {
                    for (StlPayOrders stlPayOrders : baseDto.getStlPayOrdersList()) {
                        stlPayOrders.setOrderId(stlPayOrders.getOrderId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                        stlPayOrders.setpTradeId(stlPayOrders.getTradeId());
                        stlPayOrders.setTradeId(entity.getBizCode() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                        stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                        stlPayOrdersMapper.insertSelective(stlPayOrders);
                    }
                }

                //交易费用明细表插入
                if (baseDto.getTrdTradeFeeDetailList()!=null && baseDto.getTrdTradeFeeDetailList().size()>0){
                for (TrdTradeFeeDetail TrdTradeFeeDetail : baseDto.getTrdTradeFeeDetailList()) {
                    TrdTradeFeeDetail.setTradeId(TrdTradeFeeDetail.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
          //          trdTradeFeeDetailMapper.insert(TrdTradeFeeDetail);
                }
            }

                MstFileOutputParameter mstFileOutputParameter = mstFileCommonService.getFileInfoByBusiId(entity.getBizCode());
                if(mstFileOutputParameter!=null) {
                    MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
                    mstFileInputParameter.setFileList(mstFileOutputParameter.getUploadFileList());
                    mstFileInputParameter.setBussinessId(entity.getBizCode() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    //框架共通组件.附件组件
                    mstFileCommonService.updateBusinessId(mstFileInputParameter);
                }
                
                //删除授信占用
                if (null != baseDto.getTrdProductDeal().getTradeId() && !"".equals(baseDto.getTrdProductDeal().getTradeId())){
                    riskCreditListCommonValidatorService.deleteCredit(baseDto.getTrdProductDeal().getTradeId());
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
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
            FinProductTradeDto finProductTradeRevoke = new FinProductTradeDto();
            finProductTradeRevoke.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());

                List<TrdProductDeal>  trdProductDealLists=
                        baseTradeProductDealService.selectTrdProductDealsByTotalTradeId(entity.getBizCode());//汇总交易编号
                if(trdProductDealLists!=null && trdProductDealLists.size()>0){
                    List<BaseTradeProductDealDto> list=new ArrayList<>();
                    for(TrdProductDeal deals:trdProductDealLists) {
                        BaseTradeProductDealDto reVoke=new BaseTradeProductDealDto();
                        reVoke.setRecType(DictCodeListExt1.应收款类型_还本未收利息.getValue());
                        deals.setWorkflowId(entity.getWorkflowId());
                        deals.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                        deals.setCancelRemark(entity.getRevokeReason());
                        if(StringUtil.isNotEmpty( deals.getFinprodId())){
                        	Map<String, Object> pageParam = new HashMap<>();
                        	pageParam.put("finprodId",  deals.getFinprodId());
                        	List<SelectOptionDto> selectOptionDto = restTemplate.postForList(ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/getChannelIdAndCollectTypeForAsset",
                        			pageParam , SelectOptionDto.class);
                        	if(!StringUtil.isEmptyList(selectOptionDto)){
                        		//设置通道id  撤销通道持仓
                        		deals.setChlId(selectOptionDto.get(0).getId());
                        	}
                        }
                        //deals.setTotleTradeId(entity.getBizCode() + "R");
                        TrdProductDealAdd add = trdProductDealAddMapper.selectByPrimaryKey(deals.getTradeId());
                        if(add!=null){
                            add.setWorkflowId(entity.getWorkflowId());
                            reVoke.setTrdProductDealAdd(add);
                        }
                        reVoke.setTrdProductDeal(deals);
                        PaTrdProductDeal pTPDeal=paTrdProductDealMapper.selectByPrimaryKey(deals.getTradeId());
                        if(pTPDeal!=null){
                            pTPDeal.setWorkflowId(entity.getWorkflowId());
                            reVoke.setPaTrdProductDeal(pTPDeal);
                        }
                        PtlTradeKey key = new PtlTradeKey();
                        key.setBusiNo(deals.getTradeId());
                        key.setPortfolioId(add==null?null:add.getPortfolioId());
                        PtlTrade ptlTrade=ptlTradeMapper.selectByPrimaryKey(key);
                        List<PtlTrade> ptlTradeList =new ArrayList<>();
                        if(ptlTrade!=null){
                            ptlTrade.setBusiNo(deals.getTradeId()+"R");
                            ptlTradeMapper.insertSelective(ptlTrade);
                        }
                        if(StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                            key.setPortfolioId(entity.getActPortfolioId());
                            ptlTrade = ptlTradeMapper.selectByPrimaryKey(key);
                            if (ptlTrade != null) {
                                ptlTrade.setBusiNo(deals.getTradeId() + "R");
                                ptlTradeMapper.insertSelective(ptlTrade);
                            }
                        }
                        reVoke.setPayDate(getPayDate(deals.getFinprodId(),deals.getBranch()));
                        list.add(reVoke);
                    }

                    finProductTradeRevoke.setBaseTradeProductDealDtos(list);
                    basePtlRecePositionService.execute(finProductTradeRevoke);
                    if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                        finProductTradeRevoke.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                        basePtlRecePositionService.insertPosition(finProductTradeRevoke);
                        finProductTradeRevoke.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
                    }
                    if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                        finProductTradeRevoke.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                            finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                        });
                    }
                }

        }

        // 插入风险试算结果
        //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getBizCode());
        // 删除原业务流程记录
        this.deleteBussFlowData(entity.getWorkflowId());

        // 调用基础组件.【交易报送组件】删除交易报送信息
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepTrade",
            Arrays.asList(entity.getBizCode()), Boolean.class);


        if(StringUtil.isNotEmpty(reverseTradeId)){
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            trdProductDeal.setTradeId(reverseTradeId);
            trdProductDeal.setCancelRemark(entity.getRevokeReason());
            commonBusinessService.revokeReverseTrade(trdProductDeal,true);
        }

        // // 一体化对应etliu start
        // // 所有交易生效后调用的共通方法
        // for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
        // AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        // inputDto.setProdid(baseDto.getTrdProductDealAdd().getPortfolioId());
        // inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
        // inputDto.setTrdProductDeal(baseDto.getTrdProductDeal());
        // AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
        //}
        // // 一体化对应etliu end
        // 调用投管接口 add by  cxq 20190217 start
        for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        investInterfCommDto.setPageId("TrdProjectInvestTrade002");
        investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
        investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
        investInterfCommDto.setRevokeReason(entity.getRevokeReason());
        investInterfCommDto.setTrdProductDeal(baseDto.getTrdProductDeal());
        investInterfCommDto.setTrdProductDealAdd(baseDto.getTrdProductDealAdd());
        if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
            investInterfCommDto.setStlPayOrders(baseDto.getStlPayOrdersList().get(0));
        }
        investInterfCommDto.setTradeFeeDetaillist(baseDto.getTrdTradeFeeDetailList());
        investInterfCommService.doInvestInterf(investInterfCommDto);
        // 调用投管接口 add by  cxq 20190217 end
    }
    }

    /**
     * 7.4更新资产负债报送状态 利用金融产品代码作为条件把资产负债报送信息表中的报送状态更改为是
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void assetDebtCbReportLogic_7_4(TrdProjectInvestTrade002Dto dto) {
        // 利用金融产品代码作为条件把资产负债报送信息表中的报送状态更改为是
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/updateRepAssetDebt",
            dto.getFinprodId(), Boolean.class);
    }

    /**
     * 共通组件
     */
    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 7.3写入交易信息进交易报送信息表，为中债报送用
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void dealInfoCbReportLogic_7_3(TrdProjectInvestTrade002Dto dto) {
        // 7.7写入交易信息进交易报送信息表，为中债报送用
        // 1)查询产品登记编码
        String prodRegistCode = restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectProdRegistCode/" + dto.getPortfolioId(), null,
            String.class);

        if (com.joyintech.framework.util.StringUtil.isBlank(prodRegistCode)) {
            return;
        }

        // 2）写入交易报送信息
        // 交易报送信息Entity
        RepTrade repTrade = new RepTrade();
        repTrade.setRepFinprodRegId(prodRegistCode); // 产品登记编码
        repTrade.setRepBankTradeCode(dto.getBizCode()); // 行内交易编码
        repTrade.setRepDelCode(dto.getBizCode()); // 行内交易编码
        repTrade.setRepBankAssetCode(dto.getFinprodMarketId()); // 行内资产/负债编码
        repTrade.setRepCcy(dto.getCcy()); // 币种
        repTrade.setRepOccurMoney(dto.getTradeAmt()); // 发生金额
        repTrade.setRepCount(dto.getPrinAmt());
        // 折算人民币金额（元） = 画面.全价总额*收盘价(利用画面.币种与交易日查询汇率行情表中最新的收盘价)
        BigDecimal 收盘价 = commonBusinessService.selectClosePrice(dto.getCcy(), dto.getTradeDate());
        BigDecimal 折算人民币金额 = com.joyintech.calculation.util.ArithmeticUtil.multiply(dto.getTradeAmt(), 收盘价);
        repTrade.setRepMoneyCny(折算人民币金额);

        // 资金流动类型: 数据字典缓存.资金流动类型 ※根据交易方向判断，若买入，即资产买入，若卖出，即为资产卖出或到期
        String 资金流动类型 = DictCodeListExt2.资金流动类型_存入银行存款.getValue();
        if (com.joyintech.framework.util.StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
            资金流动类型 = DictCodeListExt2.资金流动类型_资产买入.getValue();
        }
        repTrade.setRepMoneyFlowType(资金流动类型);
        repTrade.setRepMoneyFlowTypeExp("无");
        repTrade.setRepUnitPriceNet(dto.getUnitCprice());
        repTrade.setRepUnitPriceFull(dto.getUnitFprice());
        repTrade.setRepTradeDate(DateUtil.parse(dto.getTradeDate())); // 交易日
        repTrade.setRepTradeComponent(dto.getTrdInvestPersonInfo().getCounterId()); // 交易对手方
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue()); // 报送状态
        repTrade.setRepStatusDate(iPlatformDate.getSysDate()); // 数据日期
        repTrade.setRepRemark(dto.getRemark()); // 备注

        // 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepTrade",
            Arrays.asList(repTrade), Boolean.class);
    }

    /**
     * 删除业务流程数据
     *
     * @param workflowId 工作流代码
     * @see
     * @since 1.0
     */
    private void deleteBussFlowData(String workflowId) {
//        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
//        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "WORKFLOW_ID", workflowId);
//
//        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
//        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD", "WORKFLOW_ID", workflowId);
//
//        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
//        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL", "WORKFLOW_ID", workflowId);
//
//        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
//        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_STL_PAY_ORDERS", "WORKFLOW_ID", workflowId);
        baseTradeProductDealCommService.deleteWflData(workflowId);
    }

    @Override
    public void doInsert(TrdProjectInvestTrade002Dto entity, OperationType type) {
        if (type != OperationType.REVOKE && BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
            if (entity.getBaseTradeInfoDto() != null
                && entity.getBaseTradeInfoDto().getTrdProductDeal().getWorkflowId() == null) {
                entity.getBaseTradeInfoDto().getTrdFinProductDealTotal().setWorkflowId(entity.getWorkflowId());
                entity.getBaseTradeInfoDto().getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                entity.getBaseTradeInfoDto().getTrdProductDealAdd().setWorkflowId(entity.getWorkflowId());
                entity.getBaseTradeInfoDto().getPaTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                entity.getBaseTradeInfoDto().getStlPayOrders().setWorkflowId(entity.getWorkflowId());
                entity.getBaseTradeInfoDto().getTrdDealTotalAdd().setWorkflowId(entity.getWorkflowId());
                entity.getBaseTradeInfoDto().getPaTrdFinProductDealTotal().setWorkflowId(entity.getWorkflowId());

            }
        }
        if (!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
            saveDataWhenStartWorkflow(entity);
        }
        else if (type == OperationType.REVOKE) {
            doRevokeInsert(entity);
        }
    }

    private void updateSetWflByTradeId(BaseTradeInfoDto tradeInfoDto) {
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdProjectInvestTrade/updateSetWflByTradeId",
            tradeInfoDto, String.class);
    }

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
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    private void saveDataWhenStartWorkflow(TrdProjectInvestTrade002Dto entity) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
            && com.joyintech.framework.util.StringUtil.isNotEmpty(entity.getBizCode())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getBizCode());
        }

        if (!com.joyintech.framework.util.StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }

        // 调用授信共同 sjj start
        TrdProductDeal TrdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity);
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        TrdProductDeal.setCounterId(entity.getTrdInvestPersonInfo().getCounterId());
        TrdProductDeal.setTradeDate(DateUtil.parse(entity.getTradeDate()));
        TrdProductDeal.setTradeId(entity.getBaseTradeInfoDto().getTrdProductDeal().getTradeId());
        inputDto.setTrdProductDeal(TrdProductDeal);
        AllTradeCommonUtil.handleBeforeTradeComplete(inputDto,false);
        // 调用授信共同 sjj end

        // 插入Entity到对应的业务流程DB中
        BaseTradeInfoDto trade = entity.getBaseTradeInfoDto();
        // 金融产品交易汇总信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, trade.getTrdFinProductDealTotal());
        wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

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

        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, trade.getPaTrdFinProductDealTotal());
        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
        wflPaTrdFinDealTotal.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId());

        WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, trade.getTrdDealTotalAdd());
        wflTrdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
        wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//        转换

        wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);

        ////update by wangpeng 通道信息不登表
        String chlid = wflTrdProductDeal.getChlId();
        wflTrdProductDeal.setChlId(null);
        wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
        wflTrdProductDeal.setChlId(chlid);
        ////update by wangpeng 通道信息不登表 end
        
        wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
        wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);
        wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);

        if (trade.getTrdTradeFeeDetail() != null) {
            for (TrdTradeFeeDetail detail : trade.getTrdTradeFeeDetail()) {
                trdTradeFeeDetailMapper.deleteByPrimaryKey(detail);
                trdTradeFeeDetailMapper.insertSelective(detail);
            }
        }

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表 不是卖出时才插入流程表
        if (entity.getBaseTradeInfoDto().getStlPayOrders() != null
            && !DictCodeList.交易方向_卖出.getValue().equals(trade.getTrdProductDeal().getPs())) {
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, entity.getBaseTradeInfoDto().getStlPayOrders());
            wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
        }
        // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
        execute持仓共通(entity);
        //应收款转入/转出多笔交易
        if(entity.getTrdProductDealList()!=null && entity.getTrdProductDealList().size()>0) {
            for (TrdProductDeal trdProductDeal : entity.getTrdProductDealList()) {
                WflTrdProductDeal wfldeal = new WflTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wfldeal, trdProductDeal);
                wfldeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wfldeal.setWorkflowId(entity.getWorkflowId());
                trdProductDeal.setWorkflowId(entity.getWorkflowId());

                WflTrdProductDealAdd wflTrdDealAdd = new WflTrdProductDealAdd();
                TrdProductDealAdd trdDealAdd = new TrdProductDealAdd();
                trdDealAdd = this.setReceTrdProductDealAdd(entity.getPortfolioId(),
                        entity.getEntrustId(), wfldeal.getTradeId());
                BeanUtil.copyPropertiesIgnoreNull(wflTrdDealAdd, trdDealAdd);
                wflTrdDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdDealAdd.setWorkflowId(entity.getWorkflowId());
                trdDealAdd.setWorkflowId(entity.getWorkflowId());
                trdDealAdd.setPortfolioId(entity.getPortfolioId());
                wflTrdProductDealMapper.insertSelective(wfldeal);
                wflTrdProductDealAddMapper.insertSelective(wflTrdDealAdd);
                List<BaseTradeProductDealDto> list = new ArrayList<BaseTradeProductDealDto>();
                FinProductTradeDto finProductTrades = new FinProductTradeDto();
                finProductTrades.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                BaseTradeProductDealDto baseDto = new BaseTradeProductDealDto();
                baseDto.setRecType(DictCodeListExt1.应收款类型_还本未收利息.getValue());
                baseDto.setPayDate(getPayDate(trdProductDeal.getFinprodId(),trdProductDeal.getBranch()));
                baseDto.setTrdProductDeal(trdProductDeal);
                baseDto.setTrdProductDealAdd(trdDealAdd);
                list.add(baseDto);
                finProductTrades.setBaseTradeProductDealDtos(list);
                if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    finProductTrades.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                        finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                    });
                }
                basePtlRecePositionService.execute(finProductTrades);
                if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    finProductTrades.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                    basePtlRecePositionService.insertPosition(finProductTrades);
                    finProductTrades.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
                }
            }
        }
    }

    private void execute持仓共通(TrdProjectInvestTrade002Dto entity) {
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDto.setTrdProductDeal(entity.getBaseTradeInfoDto().getTrdProductDeal());
        baseTradeProductDealDto.setTrdProductDealAdd(entity.getBaseTradeInfoDto().getTrdProductDealAdd());
        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, entity.getBaseTradeInfoDto().getStlPayOrders());
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrdersDto));
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
    }

    /**
     * 撤销交易doInsert
     *
     * @param entity 参数entity
     * @see
     * @since 1.0
     */
    private void doRevokeInsert(TrdProjectInvestTrade002Dto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTotelTradeInfo?tradeId=" + entity.getBizCode(),
            BaseTradeProductDealDto.class);

        if (!com.joyintech.framework.util.StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, baseDto.getTrdFinProductDealTotal());
                // 交易状态=“撤销”
                wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
                wflTrdFinProductDealTotal.setpTradeId(entity.getBizCode());
                // 撤销说明=画面.撤销说明
                wflTrdFinProductDealTotal.setRemark(entity.getRevokeReason());
                // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
                wflTrdFinProductDealTotal.setTotleTradeId(entity.getBizCode() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
                // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baseDto.getTrdProductDeal());
                // 交易状态=“撤销”
                wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
                wflTrdProductDeal.setpTradeId(entity.getTradeId());
                // 撤销说明=画面.撤销说明
                wflTrdProductDeal.setCancelRemark(entity.getRevokeReason());
                // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
                wflTrdProductDeal.setTradeId(entity.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
                wflTrdProductDeal.setTotleTradeId(entity.getBizCode() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());

                // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseDto.getTrdProductDealAdd());
                wflTrdProductDealAdd.setTradeId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setProdId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());

                // 金融产品交易信息DTO.金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
                WflPaTrdProductDeal paTrdProductDeal = new WflPaTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, baseDto.getPaTrdProductDeal());
                paTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId());
                paTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                paTrdProductDeal.setWorkflowId(entity.getWorkflowId());

                // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
                WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
                TrdDealTotalAdd  totalAdd=trdDealTotalAddMapper.selectByPrimaryKey(entity.getBizCode());
                if(totalAdd!=null){
                    BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, totalAdd);
                    wflTrdDealTotalAdd.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId());
                    wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflTrdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
                }

                // 金融产品交易信息DTO.金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
                WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
                PaTrdFinProductDealTotal paTrdFinDealTotal=paTrdFinProductDealTotalMapper.selectByPrimaryKey(entity.getBizCode());
                if(paTrdFinDealTotal!=null) {
                    BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, paTrdFinDealTotal);
                    wflPaTrdFinDealTotal.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId());
                    wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
                }

                // 批量插入
//                bussCommonMapperDelegateService.insertBatch(WflTrdProductDealMapper.class, wflTrdProductDeal,
//                    WflTrdProductDealAddMapper.class, wflTrdProductDealAdd, WflPaTrdProductDealMapper.class, paTrdProductDeal);
                wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);
                wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
                wflPaTrdProductDealMapper.insertSelective(paTrdProductDeal);
                wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);
                wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);

                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
//              StlPayOrders order = entity.getBaseTradeInfoDto().getStlPayOrders();
                if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
                    List<WflStlPayOrders>stlPayOrdersDtos= new ArrayList<>();
                    for (StlPayOrdersDto stlPayOrdersDto : baseDto.getStlPayOrdersList()) {
                    	WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                    	BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                    	wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    	wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                    	wflStlPayOrders.setTradeId(wflTrdFinProductDealTotal.getTotleTradeId());
                    	wflStlPayOrders.setpTradeId(stlPayOrdersDto.getOrderId());
                    	wflStlPayOrders.setProdId(wflTrdProductDeal.getTradeId());
                    	wflStlPayOrders.setOrderId(stlPayOrdersDto.getOrderId() + "R");
                    	wflStlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                    	
                        wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                    }
                }
            }
        }
    }

    private TrdProductDeal getWflTrdProductDeal(TrdProjectInvestTrade002Dto entity, BussinessCodeParameter parameter,
                                                FinAssetIntInfo finAssetIntInfo, DictCodeList 交易方向) {
        TrdProductDeal deal = new TrdProductDeal();
        deal.setTradeId(entity.getTradeId());
        deal.setBusiType(DictCodeList.共通业务类型_项目类资产存续期.getValue());
        deal.setFinprodId(entity.getFinprodId());
        deal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        // deal.setFinprodType(); TODO 估值核算 金融产品类型
        deal.setCcy(entity.getCcy());
        // deal.setVdate(DateTime.parse(entity.getvDate()).toDate());
        // deal.setMdate(DateTime.parse(entity.getmDate()).toDate());
        deal.setSettleDate(deal.getTradeDate());
        deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        deal.setIsCancel(DictCodeList.是否_否.getValue());
        deal.setWorkflowId(entity.getWorkflowId());
        deal.setTotleTradeId(entity.getBizCode());
        deal.setPs(交易方向.getValue());
        // deal.setBranch(changePsToBranch(entity.getmDate(), 交易方向.getValue()));

        if (DictCodeList.交易方向_收息 == 交易方向) {
            // deal.setCashId(getCashIdByCashType(entity, DictCodeList.现金流类型_中间收息));
            deal.setShareAmt(finAssetIntInfo.getQty());
            deal.setIntAmt(ArithmeticUtil.subtract(finAssetIntInfo.getFpriceAmt(), finAssetIntInfo.getReturnAmount()));
            deal.setFpriceAmt(deal.getIntAmt());
            deal.setTradeAmt(deal.getIntAmt());
        }
        else if (DictCodeList.交易方向_到期 == 交易方向) {
            // deal.setCashId(getCashIdByCashType(entity, DictCodeList.现金流类型_到期还本));
            deal.setShareAmt(finAssetIntInfo.getReturnAmount());
            deal.setPrinAmt(finAssetIntInfo.getReturnAmount());
            deal.setFpriceAmt(finAssetIntInfo.getReturnAmount());
            deal.setTradeAmt(finAssetIntInfo.getReturnAmount());
        }
        return deal;
    }

    private static TrdProductDealAdd gen交易附属(TrdProjectInvestTrade002Dto entity, TrdProductDeal trdProductDeal) {
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
        trdProductDealAdd.setPortfolioId(entity.getPortfolioId());
        trdProductDealAdd.setEntrustId(entity.getEntrustId());
        // trdProductDealAdd.setProdId(entity.getFinprodId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        trdProductDealAdd.setWorkflowId(entity.getWorkflowId());
        return trdProductDealAdd;
    }

    private static TrdDealTotalAdd gen汇总交易附属(TrdProjectInvestTrade002Dto entity) {
        TrdDealTotalAdd trdProductDealAdd = new TrdDealTotalAdd();
        trdProductDealAdd.setTotleTradeId(entity.getBizCode());
        trdProductDealAdd.setEntrustId(entity.getEntrustId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        trdProductDealAdd.setWorkflowId(entity.getWorkflowId());
        return trdProductDealAdd;
    }

    private static TrdProductDeal gen产品交易(TrdProjectInvestTrade002Dto entity) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(entity.getTradeId());
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_项目类资产交易.getValue());
        trdProductDeal.setFinprodId(entity.getFinprodId());
        // trdProductDeal.setFinprodType(); TODO 估值核算 金融产品类型
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        trdProductDeal.setCcy(entity.getCcy());
        trdProductDeal.setBranch(Long.valueOf(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        trdProductDeal.setUnitCprice(entity.getUnitCprice());
        // modify by wanshiwei ZGZGST-3595 项目类资产“利率%”取值错误，取资产的计提利率 20210928 start
        trdProductDeal.setUnitInt(ArithmeticUtil.divide(entity.getInvestRate(), 100, 8));
        // trdProductDeal.setUnitInt(entity.getUnitIntAmt());
        // modify by wanshiwei ZGZGST-3595 项目类资产 “利率%”取值错误，取资产的计提利率 20210928 end
        trdProductDeal.setUnitFprice(entity.getUnitFprice());
        trdProductDeal.setCpriceAmt(entity.getCpriceAmt());
        trdProductDeal.setCcy(entity.getCcy());
        trdProductDeal.setPs(entity.getPs());
       // trdProductDeal.setTradeDate(DateTime.parse(entity.getTradeDate()).toDate());
        trdProductDeal.setTradeDate(DateTime.parse(entity.getSettleDate()).toDate());
        trdProductDeal.setSettleDate(DateTime.parse(entity.getSettleDate()).toDate());
        trdProductDeal.setSettleSpeed(DictCodeList.清算速度_T_0.getValue().equals(entity.getSettleSpeed()) ? 0 : 1l);
        trdProductDeal.setPrinAmt(entity.getPrinAmt());
        trdProductDeal.setShareAmt(entity.getPrinAmt());
        trdProductDeal.setIntAmt(entity.getIntAmt());
        trdProductDeal.setFpriceAmt(entity.getFpriceAmt());
        trdProductDeal.setTradeAmt(entity.getTradeAmt());
        trdProductDeal.setWorkflowId(entity.getWorkflowId());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        trdProductDeal.setTrader(entity.getTrdInvestPersonInfo().getTrader());
        trdProductDeal.setCounterId(entity.getTrdInvestPersonInfo().getCounterId());
//        trdProductDeal.setIsOutTrade(entity.getTrdInvestPersonInfo().getIsOutTrade());
        if(StringUtil.equals(DictCodeList.是否_否.getValue(), entity.getTrdInvestPersonInfo().getIsOutTrade())){
        	trdProductDeal.setIsOutTrade(entity.getTrdInvestPersonInfo().getIsOutTrade());
        }else{
        	trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
        }
        trdProductDeal.setCounterTrader(entity.getTrdInvestPersonInfo().getCounterTrader());
        trdProductDeal.setCounterContact(entity.getTrdInvestPersonInfo().getContactInfo());
        //总交易费用 = 手续费 + 通道费
        trdProductDeal.setFeeAmt(ArithmeticUtil.doAdd(entity.getCommissionAmt(), entity.getChannelFee()));
        //随交易支付费用 
        //内部交易对手  通道费随交易支付  外部交易对手根据通道收取方式
//        if(StringUtil.equals(trdProductDeal.getIsOutTrade(), DictCodeList.是否_否.getValue())){
//        	trdProductDeal.setFeeAmtPay(ArithmeticUtil.doAdd(entity.getCommissionAmt(), entity.getChannelFee()));
//        }else{
//        	if(StringUtil.equals(entity.getCollectType(), DictCodeListExt2.收取方式_回款前扣除.getValue())){
//            	trdProductDeal.setFeeAmtPay(ArithmeticUtil.doAdd(entity.getCommissionAmt(), entity.getChannelFee()));
//            }else{
//            	trdProductDeal.setFeeAmtPay(entity.getCommissionAmt());
//            	trdProductDeal.setFeeAmtUnpay(entity.getChannelFee());
//            }
//        }
      //2019-12-24 通道费改造 标的挂通道  通道费不参与计算
        trdProductDeal.setFeeAmtPay(entity.getCommissionAmt());
        trdProductDeal.setFeeAmtUnpay(entity.getChannelFee());
        
        trdProductDeal.setRemark(entity.getRemark());
        trdProductDeal.setChlId(entity.getChlId());
        trdProductDeal.setTotleTradeId(entity.getBizCode());
        //投资目的 add by yzg start 20201019 
        trdProductDeal.setInvAim(entity.getInvAim());
        //投资目的 add by yzg end 20201019
        return trdProductDeal;
    }

    @Override
    public void doUpdate(TrdProjectInvestTrade002Dto entity) {
        baseTradeProductDealCommServiceImpl.deleteWflData(entity.getWorkflowId());
        entity.setBaseTradeInfoDto(genFinProductTradeDto(entity));
        ProcessMark newEntity = null;
        try {
            newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdProjectInvestTrade002Dto.class, true);
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
        saveDataWhenStartWorkflow(entity);
    }

    /**
     * BaseTradeProductDealCommService
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
        
//        //根据工作流代码查询交易相关信息
//        WflTrdProductDealAdd wflTrdProductDealAdd = restTemplate.getForObject(
//            ServiceApplication.TRADE_SERVICE_NAME
//                    + "/common/trdcommonbase/selectWflTrdProductDealAdd/" + workflowId, WflTrdProductDealAdd.class);
//        //删除授信占用
//        if (null != wflTrdProductDealAdd && null != wflTrdProductDealAdd.getTradeId()){
//            riskCreditListCommonValidatorService.deleteCredit(wflTrdProductDealAdd.getTradeId());
//        }
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
    public BaseTradeInfoDto genFinProductTradeDto(TrdProjectInvestTrade002Dto entity) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        BaseTradeInfoDto result = new BaseTradeInfoDto();
        TrdFinProductDealTotal trdFinProductDealTotal = gen产品汇总交易(entity);
        result.setTrdFinProductDealTotal(trdFinProductDealTotal);
        TrdProductDeal trdProductDeal = gen产品交易(entity);
        result.setTrdProductDeal(trdProductDeal);

        result.setTrdProductDealAdd(gen交易附属(entity, trdProductDeal));

        result.setPtlTrade(gen组合交易(entity));

        result.setTrdTradeFeeDetail(gen汇总交易费用明细(entity));

        result.setStlPayOrders(gen划款指令流程(entity).get(0));

        result.setPaTrdProductDeal(gen交易扩展(entity));

        result.setPaTrdFinProductDealTotal(gen汇总交易扩展(entity));

        result.setTrdDealTotalAdd(gen汇总交易附属(entity));

        if(StringUtil.isNotEmpty(entity.getIspayUnpaidint())
                && entity.getIspayUnpaidint().equals(DictCodeList.是否_是.getValue()) && entity.getPs().equals(DictCodeList.交易方向_卖出.getValue())
        && entity.getPayUnpaidintAmt()!=null && entity.getPayUnpaidintAmt().compareTo(BigDecimal.ZERO)>0){//是否结转为是
            //获取应收款转出的交易信息
            List<TrdProductDeal> dealList=getReceTrdDeal(entity);
            entity.setTrdProductDealList(dealList);//应收款转出/转入
        }

        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_项目类资产交易);
        entity.setPortfolioId(entity.getActPortfolioId());
        return result;
    }

    private static PaTrdProductDeal gen交易扩展(TrdProjectInvestTrade002Dto entity) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(entity.getTradeId());
        String paBussType = DictCodeListExt1.平安业务类型_项目类资产买入.getValue();
        if (DictCodeList.交易方向_卖出.getValue().equals(entity.getPs())) {
            paBussType = DictCodeListExt1.平安业务类型_项目类资产卖出.getValue();
        }
//        else if (DictCodeList.交易方向_卖出到账.getValue().equals(entity.getPs())) {
//            paBussType = DictCodeListExt1.平安业务类型_项目类资产卖出到账.getValue();
//        }
        paTrdProductDeal.setPaBussType(paBussType);
        paTrdProductDeal.setDeptCode(entity.getTrdInvestPersonInfo().getTraderDept());
        paTrdProductDeal.setWorkflowId(entity.getWorkflowId());
        return paTrdProductDeal;
    }


    private static PaTrdFinProductDealTotal gen汇总交易扩展(TrdProjectInvestTrade002Dto entity) {
        PaTrdFinProductDealTotal paTrdProductDeal = new PaTrdFinProductDealTotal();
        paTrdProductDeal.setTotleTradeId(entity.getBizCode());
        if(entity.getPs()!=null && entity.getIspayUnpaidint()!=null && entity.getIspayUnpaidint().equals(DictCodeList.是否_是.getValue()) && entity.getPayUnpaidintAmt()!=null && entity.getPayUnpaidintAmt().compareTo(BigDecimal.ZERO)>0
                && entity.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
            paTrdProductDeal.setTheoryInt(entity.getPayUnpaidintAmt());//
        }
        String paBussType = DictCodeListExt1.平安业务类型_项目类资产买入.getValue();
        if (DictCodeList.交易方向_卖出.getValue().equals(entity.getPs())) {
            paBussType = DictCodeListExt1.平安业务类型_项目类资产卖出.getValue();
        }
        paTrdProductDeal.setPaBussType(paBussType);
        paTrdProductDeal.setDeptCode(entity.getTrdInvestPersonInfo().getTraderDept());
        paTrdProductDeal.setWorkflowId(entity.getWorkflowId());
        return paTrdProductDeal;
    }

    private List<StlPayOrders> gen划款指令流程(TrdProjectInvestTrade002Dto entity) {
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderImplService.save(entity.getBussTrdTransferOrder());
        List<StlPayOrders> stlPayOrdersDtos = new ArrayList<>();
        if (stlPayOrdersList != null) {
            stlPayOrdersList.forEach(stlPayOrders -> {
                StlPayOrders dto = new StlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrders);
                dto.setTradeId(entity.getBizCode());
                dto.setPortfolioId(entity.getPortfolioId());
                dto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
                dto.setWorkflowId(entity.getWorkflowId());
                stlPayOrdersDtos.add(dto);
            });
        }
        return stlPayOrdersDtos;
    }

    private static List<PtlTrade> gen组合交易(TrdProjectInvestTrade002Dto entity) {
        List<PtlTrade> result = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setBusiId(entity.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_项目类资产交易.getValue());
        ptlTrade.setSettleDate(DateUtil.parse(entity.getTradeDate()));
        ptlTrade.setPortfolioId(entity.getPortfolioId());
        ptlTrade.setBusiNo(entity.getTradeId());//交易编号
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setWorkflowId(entity.getWorkflowId());
        result.add(ptlTrade);
        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            ptlTrade1.setBusiId(entity.getFinprodId());
            ptlTrade1.setBusiType(DictCodeList.共通业务类型_项目类资产交易.getValue());
            ptlTrade1.setSettleDate(DateUtil.parse(entity.getTradeDate()));
            ptlTrade1.setPortfolioId(entity.getActPortfolioId());
            ptlTrade1.setBusiNo(entity.getTradeId());
            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade1.setWorkflowId(entity.getWorkflowId());
            result.add(ptlTrade1);
        }
        return result;
    }

    private void gen利息明细汇总表和扩展表(FinAssetIntInfo finAssetIntInfo, TrdProjectInvestTrade002Dto entity,
                                BaseTradeInfoDto baseTradeInfoDto) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(
            baseGetBusinessIdImpl.buildBussinessCode(new BussinessCodeParameter(DictCodeList.共通业务类型_项目类资产交易)));
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_项目类资产交易.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        // trdProductDeal.setFinprodType(); TODO 估值核算 金融产品类型
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        trdFinProductDealTotal.setCcy(entity.getCcy());
        // trdFinProductDealTotal.setPs(change现金流类型To交易方向(entity.getCashType()));
        // trdFinProductDealTotal.setTradeDate(DateTime.parse(entity.getArrivalDate()).toDate());
        trdFinProductDealTotal.setVdate(DateTime.parse(finAssetIntInfo.getvDate()).toDate());
        trdFinProductDealTotal.setMdate(DateTime.parse(finAssetIntInfo.getmDate()).toDate());
        // trdFinProductDealTotal.setSettleDate(DateTime.parse(entity.getArrivalDate()).toDate());
        trdFinProductDealTotal.setShareAmt(finAssetIntInfo.getReturnAmount());
        trdFinProductDealTotal.setPrinAmt(finAssetIntInfo.getReturnAmount());
        trdFinProductDealTotal.setIntAmt(
            ArithmeticUtil.subtract(finAssetIntInfo.getFpriceAmt(), finAssetIntInfo.getReturnAmount()));
        trdFinProductDealTotal.setFpriceAmt(finAssetIntInfo.getFpriceAmt());
        trdFinProductDealTotal.setTradeAmt(
            ArithmeticUtil.subtract(finAssetIntInfo.getFpriceAmt(), finAssetIntInfo.getTaxAmount()));
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setpTotleTradeId(entity.getBizCode());
        baseTradeInfoDto.setTrdFinProductDealTotal(trdFinProductDealTotal);

        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        paTrdFinProductDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        paTrdFinProductDealTotal.setTheoryAmt(finAssetIntInfo.getTheoryAmt());
        // paTrdFinProductDealTotal.setPaBussType(entity.getPaBussType());
        baseTradeInfoDto.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);
    }

    private PtlTrade getPtlTrade(TrdProjectInvestTrade002Dto entity, FinAssetIntInfo finAssetIntInfo, TrdProductDeal deal收息) {
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setBusiId(entity.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_项目类资产存续期.getValue());
        // ptlTrade.setSettleDate(DateTime.parse(entity.getArrivalDate()).toDate());
        ptlTrade.setPortfolioId(finAssetIntInfo.getPortfolioId());
        ptlTrade.setBusiNo(deal收息.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setWorkflowId(entity.getWorkflowId());
        return ptlTrade;
    }

    private static List<TrdTradeFeeDetail> gen汇总交易费用明细(TrdProjectInvestTrade002Dto entity) {
        List<TrdTradeFeeDetail> result = new ArrayList<>();
        if (entity.getCommissionAmt() != null && BigDecimal.ZERO.compareTo(entity.getCommissionAmt()) < 0) {
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            trdTradeFeeDetail.setFeeAmt(entity.getCommissionAmt());
            if(StringUtil.isNotEmpty(entity.getCommissionRate())) {
                trdTradeFeeDetail.setFeeRate(entity.getCommissionRate().divide(new BigDecimal(100)));
            }
            trdTradeFeeDetail.setTradeId(entity.getTradeId());
            /** modify by yanya on 20201229 项目交易资产端数据字典项变更，以估值侧为准 start**/
            //trdTradeFeeDetail.setFeeType(DictCodeList.费用品种类型_交易手续费.getValue()); // 一次性费用类型
            trdTradeFeeDetail.setFeeType(BussinessDictCodeList.费用类型_交易手续费.getValue()); // 一次性费用类型
            /** modify by yanya on 20201229 项目交易资产端数据字典项变更，以估值侧为准 end**/
            result.add(trdTradeFeeDetail);
        }
        if (entity.getChannelFee() != null && BigDecimal.ZERO.compareTo(entity.getChannelFee()) < 0) {
            TrdTradeFeeDetail trdTradeFeeDetail1 = new TrdTradeFeeDetail();
//            if(StringUtil.equals(DictCodeList.是否_否.getValue(), entity.getTrdInvestPersonInfo().getIsOutTrade())){
//            	trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_是.getValue());
//            }else{
//	            if(StringUtil.equals(entity.getCollectType(), DictCodeListExt2.收取方式_回款前扣除.getValue())){
//	        		trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_是.getValue());
//	        	}else{
//	        		trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_否.getValue());
//	        	}
//            }
          //2019-12-24 通道费改造 标的挂通道  通道费不参与计算
            trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            trdTradeFeeDetail1.setFeeAmt(entity.getChannelFee());
            trdTradeFeeDetail1.setTradeId(entity.getTradeId());
            trdTradeFeeDetail1.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue()); // 一次性费用类型
            result.add(trdTradeFeeDetail1);
        }
        return result;
    }

    public Map<String, Object> findInvestRate(Map<String, Object> params) {
        if (StringUtil.isNotEmpty((java.lang.String)params.get("finprodId"))
            && StringUtil.isNotEmpty((java.lang.String)params.get("settleDate"))) {
            return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdProjectInvestTrade/findInvestRate",
                params, Map.class);
        }
        return null;
    }

    public static BussinessCodeParameter genBizCode(TrdProjectInvestTrade002Dto dto) {
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
        DictCodeList result = null;
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

    public int countFollowDeal(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdProjectInvestTrade/countFollowDeal",
                map, Integer.class);
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        TrdProjectInvestTrade002Dto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {
//              return commonBusinessService.是否需要上级审批(dto.getTrdInvestPersonInfo().getCounterId(), dto.getPrinAmt(),
//LoginUserUtil.getUserName(),
//                      DictCodeList.平安资产类型_项目类资产.getValue(), dto.getProjectType(), null, dto.getPs()
//                      // 资产类型改为授权类型 add by renping 2019-08-06  start
//                      , dto.getFinprodId()
//                      // 资产类型改为授权类型 add by renping 2019-08-06  end
//                      );
              Map<String, Object> params = CommonBusinessService.setParams(dto.getTrdInvestPersonInfo().getCounterId(), dto.getPrinAmt(),LoginUserUtil.getUserName(),
                  DictCodeList.平安资产类型_项目类资产.getValue(), dto.getProjectType(), null, dto.getPs()
                  , dto.getFinprodId() ,entity.getWorkflowId());
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
     * 确认提醒
     * 检查交割日是否为节假日，交割日如果是节假日 要提醒
     *
     * @param inputDto 画面输入参数
     * @param result valid结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    public AjaxResult checkConfirmInfo(TrdProjectInvestTrade002Dto inputDto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return null;
        }
        
        String confirmMsg = "";
        String changeLineMark = "<br/>";
        
        // 可用现金软校验
        if (StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_买入.getValue())
            && inputDto.getBussTrdAvailableCashQuery() != null && StringUtil.isEmpty(inputDto.getWorkflowId())) {
            //退回的流程重新提交的时候不用再进行金额校验 yjm  20190725
            if (inputDto.getTradeAmt() != null
                && inputDto.getBussTrdAvailableCashQuery().getAvalibleCash() != null
                && inputDto.getTradeAmt().compareTo(
                    inputDto.getBussTrdAvailableCashQuery().getAvalibleCash()) == 1) {
//               AjaxResult ar = new AjaxResult();
//               ar.setData(this.getMessage("TRD_C_0002"));
//               return ar;
               
               if (StringUtil.isNotBlank(confirmMsg)) {
                   confirmMsg += changeLineMark;
               }
               confirmMsg += this.getMessage("TRD_C_0002");
           }
        }

        // 检查交割日是否为节假日，交割日如果是节假日 要提醒
        if (inputDto.getSettleDate() != null && StringUtil.isNotBlank(inputDto.getCalendarId())) {
            boolean isDealCalendarHoliday = dateCalService.isDealCalendarHoliday(
                inputDto.getSettleDate(), inputDto.getCalendarId());
            if (isDealCalendarHoliday) {
                if (StringUtil.isNotBlank(confirmMsg)) {
                    confirmMsg += changeLineMark;
                }
                confirmMsg += getMessage("BUSS_COM_E_0039", getMessage("TrdBondInvestTrade002.settleDate"));
            }
        }
        if (StringUtils.equals(inputDto.getPs(), DictCodeList.交易方向_买入.getValue()) &&
                StringUtil.isNotEmpty(inputDto.getChlId()) && DictCodeListExt2.划款方式_一次性划款.getValue().equals(inputDto.getPayType())) {
            // 调用基础组件.【组合可用现金和实际现金查询更新组件.查询现金余额(可用)】
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("portfolioId", inputDto.getPortfolioId());
            paramMap.put("tradeDate", iPlatformDate.getSysDateStr());
            paramMap.put("ccy", inputDto.getCcy());
            paramMap.put("calendarId", inputDto.getCalendarId());
            paramMap.put("chlId", inputDto.getChlId());

            Map<String, String> resultMap = bussTrdAvailableCashQueryService.getAvailableCash(paramMap);
            BigDecimal 余额 = new BigDecimal(resultMap.get("T_2"));

            if (inputDto.getTradeAmt() != null && inputDto.getTradeAmt().compareTo(余额) > 0) {
                if (StringUtil.isNotBlank(confirmMsg)) {
                    confirmMsg += changeLineMark;
                }
                confirmMsg += getMessage("TRD_C_0004",getMessage("invest.channelId"));
            }
        }
        //外部交易对手买入，校验持仓+本金 < 融资金额或者发行金额
        if (StringUtils.equals(inputDto.getPs(), DictCodeList.交易方向_买入.getValue()) &&
                StringUtil.equals(inputDto.getTrdInvestPersonInfo().getIsOutTrade(), "S")){
            // 调用基础组件.【组合可用现金和实际现金查询更新组件.查询持仓(可用)】
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("portfolioId", inputDto.getPortfolioId());
            paramMap.put("tradeDate", inputDto.getTradeDate());
            paramMap.put("ccy", inputDto.getCcy());
            paramMap.put("calendarId", inputDto.getCalendarId());
            paramMap.put("chlId", inputDto.getChlId());
            paramMap.put("finprodId", inputDto.getFinprodId());
            //获得资产维度的持仓
            Map<String, Object>  resultMap = bussTrdAvailableCashQueryService.getFinAvaPosition(paramMap);

            BigDecimal position = new BigDecimal((String) resultMap.get("POSITION"));
            //融资金额
            BigDecimal issueAmt = new BigDecimal((String)resultMap.get("ISSUEAMT"));
            position = ArithmeticUtil.doAdd(position, inputDto.getTradeAmt());
            //资产 融资金额、发行金额
            if (issueAmt.compareTo(position) < 0) {
                if (StringUtil.isNotBlank(confirmMsg)) {
                    confirmMsg += changeLineMark;
                }
                confirmMsg += getMessage("TRD_C_0005");
            }
        }

        if (StringUtil.isNotBlank(confirmMsg)) {
            AjaxResult wr = new AjaxResult();
            wr.setData(confirmMsg);
            return wr;
        }
        
        return null;
    }

    @Override
    public void doBack(TrdProjectInvestTrade002Dto entity) {
        // 是否转仓交易  - 转仓交易退回不做任何处理
        FinProductAdd finProductAdd = trdCommonBaseMapper.selectFinProductAdd(entity.getFinprodId());
        if (null != finProductAdd && "Y".equals(finProductAdd.getIsInnerAdv())) {
            return;
        }
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBizCode());
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTradeId());
        
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }

    /**
     * 累计还本不带息的金额
     *
     * @param map 请求参数
     * @return
     * @see
     * @since 1.0
     */
    public String getUnpayIntAmt(Map<String, Object> map) {
        String intAmtStr = "";
        BigDecimal unpayIntAmt = new BigDecimal("0");
        unpayIntAmt=basePtlRecePositionService.unpayTotalIntAmt(map);
        if(unpayIntAmt!=null) {
            DecimalFormat df2 = new DecimalFormat("####################0.0000000#");
            intAmtStr = df2.format(ArithmeticUtil.round(unpayIntAmt, 6, RoundingMode.HALF_UP));
        }
        return intAmtStr;
    }

    private  TrdFinProductDealTotal gen产品汇总交易(TrdProjectInvestTrade002Dto entity) {
        TrdFinProductDealTotal trdProductDealTotal = new TrdFinProductDealTotal();
        trdProductDealTotal.setTotleTradeId(entity.getBizCode());
        trdProductDealTotal.setBusiType(DictCodeList.共通业务类型_项目类资产交易.getValue());
        trdProductDealTotal.setFinprodId(entity.getFinprodId());
        trdProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        trdProductDealTotal.setCcy(entity.getCcy());
        trdProductDealTotal.setBranch(Long.valueOf(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        trdProductDealTotal.setUnitCprice(entity.getUnitCprice());
        // modify by wanshiwei ZGZGST-3595 项目类资产“利率%”取值错误，取资产的计提利率 20210928 start
        trdProductDealTotal.setUnitInt(ArithmeticUtil.divide(entity.getInvestRate(), 100, 8));
        // trdProductDeal.setUnitInt(entity.getUnitIntAmt());
        // modify by wanshiwei ZGZGST-3595 项目类资产 “利率%”取值错误，取资产的计提利率 20210928 end
        trdProductDealTotal.setUnitFprice(entity.getUnitFprice());
        trdProductDealTotal.setCpriceAmt(entity.getCpriceAmt());
        trdProductDealTotal.setCcy(entity.getCcy());
        trdProductDealTotal.setPs(entity.getPs());
        trdProductDealTotal.setTradeDate(DateTime.parse(entity.getTradeDate()).toDate());
        trdProductDealTotal.setSettleDate(DateTime.parse(entity.getSettleDate()).toDate());
        trdProductDealTotal.setSettleSpeed(DictCodeList.清算速度_T_0.getValue().equals(entity.getSettleSpeed()) ? 0 : 1l);
        trdProductDealTotal.setPrinAmt(entity.getPrinAmt());
        trdProductDealTotal.setShareAmt(entity.getPrinAmt());
        trdProductDealTotal.setIntAmt(entity.getIntAmt());
        trdProductDealTotal.setFpriceAmt(entity.getFpriceAmt());
        trdProductDealTotal.setTradeAmt(entity.getTradeAmt());
        trdProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdProductDealTotal.setTrader(entity.getTrdInvestPersonInfo().getTrader());
        trdProductDealTotal.setCounterId(entity.getTrdInvestPersonInfo().getCounterId());
//        trdProductDeal.setIsOutTrade(entity.getTrdInvestPersonInfo().getIsOutTrade());
        if(StringUtil.equals(DictCodeList.是否_否.getValue(), entity.getTrdInvestPersonInfo().getIsOutTrade())){
            trdProductDealTotal.setIsOutTrade(entity.getTrdInvestPersonInfo().getIsOutTrade());
        }else{
            trdProductDealTotal.setIsOutTrade(DictCodeList.是否_是.getValue());
        }
        trdProductDealTotal.setCounterTrader(entity.getTrdInvestPersonInfo().getCounterTrader());
        trdProductDealTotal.setCounterContact(entity.getTrdInvestPersonInfo().getContactInfo());
        //总交易费用 = 手续费 + 通道费
        trdProductDealTotal.setFeeAmt(ArithmeticUtil.doAdd(entity.getCommissionAmt(), entity.getChannelFee()));
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
        if(entity.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
            //还本不带息的资产才保存
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("finprodId", entity.getFinprodId());
            param.put("settleDate", entity.getSettleDate());
            Map<String, Object> result = findRepayFlag(param);
            if (result.get("repayIntFlag") != null && result.get("repayIntFlag").equals("N")) {
                trdProductDealTotal.setIspayUnpaidint(entity.getIspayUnpaidint());
            }
        }
        trdProductDealTotal.setPayUnpaidintAmt(entity.getPayUnpaidintAmt());
        trdProductDealTotal.setPortfolioId(entity.getActPortfolioId());//存子组合
        //投资目的 add by yzg start 20201019 
        trdProductDealTotal.setInvAim(entity.getInvAim());
        //投资目的 add by yzg end 20201019
        return trdProductDealTotal;
    }


    public List<TrdProductDeal> getReceTrdDeal(TrdProjectInvestTrade002Dto dto){
        //调共通【应收款持仓共通.区间中应收款可用持仓查询】
        Map<String,Object> parmap=new HashMap<>();
        parmap.put("finprodId",dto.getFinprodId());
        parmap.put("portfolioId",dto.getPortfolioId());
        parmap.put("cDate",dto.getTradeDate());
        parmap.put("secAcctId",dto.getChlId());
        String payDate=dto.getTradeDate();
        String payDates=DateUtil.formatDate(payDate);
        parmap.put("payDate",payDates);
        List<PtlRecePositionFlow> flowList=basePtlRecePositionService.findUnpayPtlRecePosition(parmap);
        List<TrdProductDeal> trdDealList = new ArrayList<TrdProductDeal>();
        for (PtlRecePositionFlow recedto : flowList) {
            TrdProductDeal trdProductDeal = gen产品交易(dto);//获取交易信息
            BigDecimal tradeAmt=recedto.getAmount();
            trdProductDeal.setTradeId(getTradeId(dto.getPs(),dto.getProjectType()));
            if(dto.getIntAmt()!=null){
                trdProductDeal.setUnitInt(trdProductDeal.getIntAmt().subtract(tradeAmt));
            }
            trdProductDeal.setFpriceAmt(tradeAmt);
            trdProductDeal.setTradeAmt(tradeAmt);//应收款流水金额
            trdProductDeal.setShareAmt(tradeAmt);
            trdProductDeal.setUnitInt(null);//单位利息
            trdProductDeal.setCpriceAmt(null);//净价总额
            trdProductDeal.setPrinAmt(BigDecimal.ZERO);
            trdProductDeal.setIntAmt(tradeAmt);
            trdProductDeal.setFeeAmt(null);
            trdProductDeal.setFeeAmtPay(null);
            trdProductDeal.setFeeAmtUnpay(null);
            if (dto.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                trdProductDeal.setPs(DictCodeList.交易方向_应收款项转出.getValue());
            } else {
                trdProductDeal.setPs(DictCodeList.交易方向_应收款项转入.getValue());
            }
            trdProductDeal.setBranch(recedto.getBranch());//应收款流水BRANCH
            trdDealList.add(trdProductDeal);//组装交易信息
        }
        return trdDealList;
    }

    public String getTradeId(String ps,String projectType){
        DictCodeList item = DictCodeList.共通业务类型_项目类资产买入;
        if (DictCodeList.交易方向_卖出.getValue().equals(ps)) {
            item = DictCodeList.共通业务类型_项目类资产卖出;
        }
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(item);
        parameter.setType1(类型转换(projectType));
        String tradeId = baseGetBusinessIdImpl.buildBussinessCode(parameter);
        return tradeId;
    }


    /**
     * 生成金融产品附属表
     *
     * @param portfolioId 计划类（利率型）投资交易实体类
     * @return 金融产品附属类
     * @see
     * @since 1.0
     */
    public TrdProductDealAdd setReceTrdProductDealAdd(String portfolioId,String entrustId,String tradeId) {
        TrdProductDealAdd addDto = new TrdProductDealAdd();
        addDto.setTradeId(tradeId);
        addDto.setPortfolioId(portfolioId);
        addDto.setEntrustId(entrustId);
        addDto.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        return addDto;
    }



    @Override
    public void doRevoke(TrdProjectInvestTrade002Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBizCode());
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTradeId());
        
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }


    public Date getPayDate(String findprodId,Long branch){
        //根据资产代码和branch查询支付日
        PtlRecePositionFlowDto flowDto =new PtlRecePositionFlowDto();
        flowDto.setFinprodId(findprodId);
        flowDto.setBranch(branch);
        return basePtlRecePositionService.getPayDate(flowDto);//该资产还本计划的支付日
    }


    /**
     * 组合交易表
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 组合交易实体类
     * @see
     * @since 1.0
     */
    public List<PtlTrade> setRecePtlTrade(TrdProjectInvestTrade002Dto dto, String tradeId) {
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        ptlTrade.setBusiNo(tradeId);
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(dto.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_项目类资产交易.getValue());
        ptlTrade.setSettleDate(DateUtil.parse(dto.getTradeDate()));
        ptlTrades.add(ptlTrade);

        if (!dto.getPortfolioId().equals(dto.getActPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            ptlTrade1.setPortfolioId(dto.getActPortfolioId());
            ptlTrade1.setBusiNo(tradeId);
            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade1.setBusiId(dto.getFinprodId());
            ptlTrade1.setBusiType(DictCodeList.共通业务类型_项目类资产交易.getValue());
            ptlTrade1.setSettleDate(DateUtil.parse(dto.getTradeDate()));
            ptlTrades.add(ptlTrade1);
        }
        return ptlTrades;
    }

    /**
     * 生成金融产品交易汇总附属表
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 金融产品附属类
     * @see
     * @since 1.0
     */
    public TrdDealTotalAdd setTrdDealTotalAdd(TrdProjectInvestTrade002Dto dto) {
        TrdDealTotalAdd addDto = new TrdDealTotalAdd();
        addDto.setTotleTradeId(dto.getBizCode());
        addDto.setEntrustId(dto.getEntrustId());
        addDto.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        return addDto;
    }


    public Map<String, Object> findRepayFlag(Map<String, Object> params) {
        if (StringUtil.isNotEmpty((java.lang.String)params.get("finprodId"))
                && StringUtil.isNotEmpty((java.lang.String)params.get("settleDate"))) {
            return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdProjectInvestTrade/findRepayFlag",
                    params, Map.class);
        }
        return null;
    }

    public List<BaseEtrInvestInfoDto> getEntrustInfos(String entrustId) {
        List<BaseEtrInvestInfoDto> list = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getEntrustInfos/" + entrustId, null,
            BaseEtrInvestInfoDto.class);
        if (CollectionUtils.isNotEmpty(list)) {
            for (BaseEtrInvestInfoDto dto : list) {
                // 设置可委托金额
                dto.setDoEntrustMoney(
                    selectDoEntrustMoney(dto.getEtrEntrust().getEntrustId()));
            }
        }
        return list;
    }
    
    /**
     * 查询剩余委托额度
     * @param entrustId
     * @return
     */
    public BigDecimal selectDoEntrustMoney(String entrustId) {
    	Map<String, Object> param = new HashMap<>();
    	param.put("entrustId", entrustId);
    	param.put("TRADE_STATUS", DictCodeListExt1.交易状态_初始.getValue());
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdProjectInvestTrade/getDoEntrustMoneyByEntrustId/", param,
                BigDecimal.class);
    }
    
    /**
     * 查询交易是否已被撤销
     * @param tradeId
     * @return
     */
    public int findRevokeDeal(String tradeId) {
        Map<String, Object> param = new HashMap<>();
        param.put("tradeId", tradeId+"R");
        param.put("TRADE_STATUS", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        int i = 0;
        i = restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdProjectInvestTrade/findRevokeDeal", param,
            Integer.class);
        return i;
    }

    public String getReverseTradeDtoInvAim(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getReverseTradeDtoInvAim/" + tradeId, null,
                String.class);
    }
    
}
