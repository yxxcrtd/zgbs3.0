/*
 * 文件名：TrdFundInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：xiexinting
 * 修改时间：2018年10月19日 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.fund.service;

import com.joyintech.calculation.util.ArithmeticUtil;
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
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.BasicTradeFeeInputDto;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.BasicTradeFeeInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.threadservice.JasperUtil;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.ExchangeTransMatchCommonService;
import com.joyintech.tams.invest.trade.fund.dto.TrdFundInvestTradeConfirmDto;
import com.joyintech.tams.invest.trade.fund.dto.TrdFundInvestTradeDto;
import com.joyintech.tams.invest.trade.fund.dto.WorkFlowDto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.dto.WorkFlowTaskDto;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.listener.BusinessTaskListener;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 基金投资交易画面端新建界面service 〈功能详细描述〉
 * 
 * @author xiexinting
 * @version 1.0 2018年10月19日
 * @see TrdFundInvestTrade002Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdFundInvestTrade002Service extends BaseService<TrdFundInvestTradeDto> implements BaseProcessService<TrdFundInvestTradeDto>, BusinessTaskListener {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdFundInvestTrade002Service.class);

    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    @Autowired
    private JasperUtil jasperUtil;

    /**
     * 业务编号组件
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private TrdFundInvestTrade003Service trdFundInvestTrade003Service;

    /**
     * 划款指令业务组件service
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * 日期相关
     */
    @Autowired
    private DateCalService dateCalService;

    /**
     * 金融产品交易共通
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候检查service
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;

    // 场内基金改造 add by renping 20191117 start
    /**
     * 业务共通组件.交易费用组件
     */
    @Autowired(required = false)
    private BaseTradeFeeCommService baseTradeFeeCommService;
    // 场内基金改造 add by renping 20191117 end
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

    @Autowired
    private ExchangeTransMatchCommonService exchangeTransMatchCommonService;

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。 {@inheritDoc}
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
     * 工作流完成
     */
    @Override
    public void doComplete(TrdFundInvestTradeDto entity, OperationType type) {

        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        if (StringUtil.equals(entity.getOperateType(), "submit")) {
            if (entity.getTrdFinProductDealTotal() != null) {
                finProductTradeDto.setNeedSum(true);
            }
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
            BaseTradeProductDealDto dto = new BaseTradeProductDealDto();
            // 费用明细
            List<TrdTradeFeeDetail> ttfdList = new ArrayList<>();
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_买入.getValue())) {
                TrdTradeFeeDetail ttfd = new TrdTradeFeeDetail();
                ttfd.setTradeId(entity.getTradeId());
                ttfd.setFeeType(DictCodeListExt1.一次性费用类型_申购费.getValue());
                ttfd.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                ttfd.setFeeAmt(entity.getFeeAmtPay());
                ttfdList.add(ttfd);
            }
            else if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_卖出.getValue())
                     || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回到账.getValue())) {
                TrdTradeFeeDetail ttfd = new TrdTradeFeeDetail();
                ttfd.setTradeId(entity.getTradeId());
                ttfd.setFeeType(DictCodeListExt1.一次性费用类型_赎回费.getValue());
                ttfd.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                ttfd.setFeeAmt(entity.getFeeAmtPay());
                ttfdList.add(ttfd);
            }

            if (StringUtil.isNotEmpty(entity.getChlId()) && entity.getChannelAmt() != null
                && entity.getChannelAmt().compareTo(BigDecimal.ZERO) > 0) {
                // 2.2 费用明细表
                TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
                trdTradeFeeDetail.setTradeId(entity.getTradeId());
                // 交易费用明细表.费用类型 = “通道费”
                trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
                // 交易费用明细表.是否随交易支付 = “是”
                if (StringUtil.equals(entity.getCollectType(), DictCodeListExt2.收取方式_与回款无关.getValue())) {
                    trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                }
                else {
                    trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                }
                // 交易费用明细表.交易金额 = 交易.通道费
                trdTradeFeeDetail.setFeeAmt(entity.getChannelAmt());
                ttfdList.add(trdTradeFeeDetail);


                //存入通道费信息  后续调用估值使用
                entity.setTrdTradeFeeDetail(trdTradeFeeDetail);

            }
            // 场内基金改造 add by renping 20191117 start
            if (entity.getCommissionFee() != null && entity.getCommissionFee().compareTo(BigDecimal.ZERO) > 0) {
                // 2.2 费用明细表(佣金)
                TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
                trdTradeFeeDetail.setTradeId(entity.getTradeId());
                // 交易费用明细表.费用类型 = “佣金”
                trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
                // 交易费用明细表.是否随交易支付
                if (StringUtil.isEmpty(entity.getIsPayWithTrade())) {
                    trdTradeFeeDetail.setIsPayWithTrade(DictCodeListExt1.是否_是.getValue());
                }
                else {
                    trdTradeFeeDetail.setIsPayWithTrade(entity.getIsPayWithTrade());
                }
                // 交易费用明细表.交易金额 = 交易.佣金
                trdTradeFeeDetail.setFeeAmt(entity.getCommissionFee());
                ttfdList.add(trdTradeFeeDetail);
            }
            entity.setTrdTradeFeeDetailList(ttfdList);
            // 场内基金改造 add by renping 20191117 end

            dto.setTrdTradeFeeDetailList(ttfdList);
            dto.setTrdProductDeal(entity);
            // if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_买入.getValue())) {
            // dto.getTrdProductDeal().setTradeAmt(null);
            // }
            dto.setTrdProductDealAdd(entity.getTrdProductDealAdds().get(0));
            // dto.setTrdProductDeal(entity);
            // dto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
            // 划款指令
            List<StlPayOrdersDto> spodList = new ArrayList<>();
            for (BussTrdTransferOrderDto bttod : entity.getBussTrdTransferOrder()) {
                spodList.add(bttod);
                dto.setStlPayOrdersList(spodList);
            }

            // <!-- 通道交易 新增 强成西 2019/03/26 start -->
            setChannelFee(entity);
            // <!-- 通道交易 新增 强成西 2019/03/26 end -->
            // 场内基金改造 画面交易金额即实际交易金额 delete by 20191117 renping start
            // // 交易金额修正(卖出及赎回确认时候)
            // if (StringUtil.equals(dto.getTrdProductDeal().getPs(), DictCodeList.交易方向_卖出.getValue())
            // || StringUtil.equals(dto.getTrdProductDeal().getPs(), DictCodeList.交易方向_赎回确认.getValue())) {
            // BigDecimal tradeAmt = new BigDecimal(0);
            // if (dto.getTrdProductDeal().getPrinAmt() != null) {
            // tradeAmt = tradeAmt.add(dto.getTrdProductDeal().getPrinAmt());
            // }
            // if (dto.getTrdProductDeal().getIntAmt() != null) {
            // tradeAmt = tradeAmt.add(dto.getTrdProductDeal().getIntAmt());
            // }
            // if (dto.getTrdProductDeal().getFeeAmtPay() != null) {
            // tradeAmt = tradeAmt.subtract(dto.getTrdProductDeal().getFeeAmtPay());
            // }
            // dto.getTrdProductDeal().setTradeAmt(tradeAmt);
            // }
            // 场内基金改造 画面交易金额即实际交易金额 delete by 20191117 renping end
            baseTradeProductDealDtos.add(dto);
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);

            // add by etliu 20210104 start
            // GZYHTZJY-856: 场内公募基金交易,买入交易审批后，双击查看风险试算信息为空
            if (!StringUtil.isEmptyList(entity.getRiskTrialRetDto()) && StringUtil.isNotBlank(entity.getFinprodId())) {
                for (RiskTrialRetDto riskTrialRetDto : entity.getRiskTrialRetDto()) {
                    riskTrialRetDto.setAssetCode(entity.getFinprodId());
                }
            }
            basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());
            // add by etliu 20210104 end

            // 委托代码不为空设置委托编号
            if (StringUtil.isNotEmpty(entity.getEntrustId())) {
                dto.setEntrustId(entity.getEntrustId());
            }
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));

            baseTradeProductDealCommService.execute(finProductTradeDto);

            if (StringUtil.isNotEmpty(entity.getActPortfolioId())
                && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                        entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                        entity.getPortfolioId()));
            }

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/doComplete", entity,
                Integer.class);

            // // 一体化对应etliu start
            // // 所有交易生效后调用的共通方法
            // for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtos) {
            // AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            // inputDto.setProdid(entity.getPortfolioId());
            // inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            // inputDto.setTrdProductDeal(baseDto.getTrdProductDeal());
            // AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            // }
            // // 一体化对应etliu end
            // 场内基金改造 modify by renping 20191117 start
            // 实时申赎货币基金申购申请生效后，自动生成一笔申购确认交易，确认份额为申购金额*100，单位净值为0.01；
            // 实时申赎货币基金赎回申请生效后，自动生成赎回确认和赎回到账交易，金额为份额*0.01，单位净值0.01；
             // 单组合申请交易，流程结束才生成确认流程
            //if (StringUtil.isEmpty(entity.getTotleTradeId())) {
				if ((StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购.getValue())
                 || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回.getValue()))
                && StringUtil.equals(DictCodeListExt2.交易所基金类型_实时申赎货币基金.getValue(), entity.getTrmFundType())) {
                TrdFundInvestTradeConfirmDto confirmDto = new TrdFundInvestTradeConfirmDto();
                BeanUtil.copyPropertiesIgnoreNull(confirmDto, entity);
                setTrdFundInvestTradeConfirmDto(confirmDto);
                trdFundInvestTrade003Service.doComplete(confirmDto, type);
			  }
				else if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购.getValue())
						 || StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回.getValue())
						 || StringUtil.equals(entity.getPs(), DictCodeListExt2.交易方向_认购.getValue())) {
					TrdFundInvestTradeConfirmDto confirmDto = new TrdFundInvestTradeConfirmDto();
					BeanUtil.copyPropertiesIgnoreNull(confirmDto, entity);
                    /**
                     * 挡流程退回至发起人A时，A提交给B，流程钟dto创建人未B，会导致代办生成在B中
                     */
                    //confirmDto.setCreateUser(LoginUserUtil.getUserName());
                    //TISHKBS-971 add by wj 上面一行代码错误 是上个bug  版本修改错误  回退并修复
                    //CLONE - 公募基金在申购交易审批流程走完后，份额确认的交易在代办事项中显示在最后一级审批人的代办事项中
                    confirmDto.setCreateUser(entity.getCreateUser());



					confirmDto.setTradeDate(entity.getPurCfmDate());
					confirmDto.setChannelAmt(confirmDto.getFeeAmtPay());
					confirmDto.setFeeAmtPay(null);
					if(StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购.getValue())){
					    //生成的申购确认记录需要带出交易份额
                        confirmDto.setShareAmt(confirmDto.getTradeAmt());
                        if(confirmDto.getType6().equals(DictCodeList.基金类资产类型_非货币基金.getValue())){
                            if(confirmDto.getUnitCprice() !=null) {
                                confirmDto.setShareAmt(confirmDto.getTradeAmt().divide(confirmDto.getUnitCprice(), 2, RoundingMode.HALF_UP));
                            }else{
                                confirmDto.setShareAmt(null);
                            }
                        }
                    }
					ProcessParameter<TrdFundInvestTradeConfirmDto> workFlowPatameter = getWorkFlowPatameter(confirmDto);
					try {
                        WorkFlowUtil.startProcess(workFlowPatameter.getWorkFlowParameter(), "", "", confirmDto.getCreateUser(),
                                OperationType.NEW, false, confirmDto);
                    }
					catch (Exception e) {
                        logger.error(e.getMessage(),e);
					}
				}
			//}
            // 场内基金改造 modify by renping 20191117 end

            jasperExecute(entity);

            //成交录单---调用共通
            if(StringUtil.isNotEmpty(entity.getDealSqeId())) {
                exchangeTransMatchCommonService.transactionRecord(entity.getDealSqeId(),entity.getTradeId(),OperationType.NEW);
            }
        }
        else if (StringUtil.equals(entity.getOperateType(), "REVOKE")) {

            Map<String, Object> param = new HashMap<>();
            param.put("state", DictCodeList.流程状态_新建.getValue());
            param.put("tradeId", entity.getTradeId());
            List<WorkFlowDto> workflowIds = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/selectTradeAfter", param, WorkFlowDto.class);
            if (workflowIds != null && workflowIds.size() > 0) {
                for (WorkFlowDto flow : workflowIds) {
                    try {
                        WorkFlowUtil.deleteProcess(flow.getSeqNo(), flow.getWorkflowId());
                    }
                    catch (WorkFlowException e) {
                    }
                }
            }
            
            //删除授信占用
            if (null != entity.getTradeId() && !"".equals(entity.getTradeId())){
                riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
            }

            //成交录单---调用共通
            exchangeTransMatchCommonService.transactionRecord(entity.getDealSqeId(),entity.getTradeId(),OperationType.REVOKE);

        }
        // 调接口 by 赵东东 20191206 start
        try {
            List<String> tradeIds = new ArrayList<>();
            tradeIds.add(entity.getTradeId());
            restTemplate.postForObject(
                ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
                Map.class);
        }
        catch (Exception e) {

        }
        // 调接口 by 赵东东 20191206 end
    }

    public void jasperExecute(TrdFundInvestTradeDto entity) {
        if ("001".equals(CacheUtil.getSysParam("CUSTOMER_BANK_CODE"))) {
            // 生成划款指令附件:调用自定义接口项目生成Jasper报表 add by zhangJin 2019-07-29 start
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            trdProductDeal.setTradeId(entity.getTradeId());
            trdProductDeal.setPs(entity.getPs());
            trdProductDeal.setFinprodId(entity.getFinprodId());

            jasperUtil.jasperExecute(Arrays.asList(trdProductDeal), restTemplate);
            // 生成划款指令附件:调用自定义接口项目生成Jasper报表 add by zhangJin 2019-07-29 end
        }
    }

    // 场内基金改造 add by renping 20191117 start
    /**
     * 实时申赎确认交易信息做成
     * 
     * @param dto
     */
    public void setTrdFundInvestTradeConfirmDto(TrdFundInvestTradeConfirmDto dto) {
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
            dto.setPs(DictCodeList.交易方向_申购确认.getValue());
            // 实时申赎货币基金申购申请生效后，自动生成一笔申购确认交易，确认份额为申购金额*100，单位净值为0.01；
            dto.setShareAmt(ArithmeticUtil.multiply(dto.getTradeAmt(), new BigDecimal(100)));
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
            dto.setPs(DictCodeList.交易方向_赎回确认.getValue());
            // 实时申赎货币基金赎回申请生效后，自动生成赎回确认和赎回到账交易，金额为份额*0.01，单位净值0.01；
            dto.setTradeAmt(ArithmeticUtil.multiply(dto.getShareAmt(), BigDecimal.valueOf(0.01)));
        }
        dto.setUnitCprice(BigDecimal.valueOf(0.01));
        dto.setbTradeId(dto.getTradeId());
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_货币基金申购);
            }
            else {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_货币基金赎回);
            }
            dto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        else {
            dto.setTradeId(IDGenerator.generate());
        }
        dto.setPortfolioId(dto.getActPortfolioId());

        dto.getPtlTrade().forEach(ptlTrade -> {
            ptlTrade.setBusiNo(dto.getTradeId());
        });

        dto.getTrdProductDealAdds().forEach(trdProductDealAdd -> {
            trdProductDealAdd.setTradeId(dto.getTradeId());
        });

        dto.getPaTrdProductDeals().forEach(paTrdProductDeal -> {
            paTrdProductDeal.setTradeId(dto.getTradeId());
        });
    }

    /**
     * 实时申赎到账交易信息做成
     * 
     * @param dto
     */
    public void setTrdFundInvestTradeAccountDto(TrdFundInvestTradeConfirmDto dto) {
        dto.setPs(DictCodeList.交易方向_赎回到账.getValue());
        // 实时申赎货币基金赎回申请生效后，自动生成赎回确认和赎回到账交易，金额为份额*0.01，单位净值0.01；
        dto.setTradeAmt(ArithmeticUtil.multiply(dto.getShareAmt(), BigDecimal.valueOf(0.01)));
        dto.setUnitCprice(BigDecimal.valueOf(0.01));
        dto.setbTradeId(dto.getTradeId());
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_货币基金赎回);
            dto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        else {
            dto.setTradeId(IDGenerator.generate());
        }
        dto.setPortfolioId(dto.getActPortfolioId());
    }
    // 场内基金改造 add by renping 20191117 end

    /**
     * 设置流程参数
     * 
     * @param dto
     * @return
     */
    public ProcessParameter<TrdFundInvestTradeConfirmDto> getWorkFlowPatameter(TrdFundInvestTradeConfirmDto dto) {
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
            dto.setPs(DictCodeList.交易方向_申购确认.getValue());
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
            dto.setPs(DictCodeList.交易方向_赎回确认.getValue());
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_认购.getValue())) {
            dto.setPs(DictCodeList.交易方向_认购确认.getValue());
        }
        dto.setbTradeId(dto.getTradeId());
        dto.setWorkflowId(null);
        dto.setOperateType("save");
        dto.setSingle("01");
        // js已经考虑调整与节假日问题，此处重复 delete by renping 20191117 start
        // if (!StringUtil.equals(dto.getPs(), DictCodeList.交易方向_认购确认.getValue()) && ("0".equals(dto.getSettleSpreed())
        // || "1".equals(dto.getSettleSpreed()))) {
        // int speedCount = Integer.parseInt(dto.getSettleSpreed());
        // // dto.setTradeDate(DateUtil.addDay(dto.getTradeDate(), 1));
        // Date tradeDate = dto.getTradeDate();
        // List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(tradeDate)),
        // DateUtil.addDay(DateUtil.parse(DateUtil.format(tradeDate)), 15), dto.getCalendarId());
        // Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(tradeDate)), speedCount,
        // holidayList, "1");
        // dto.setTradeDate(tradeDay);
        // }
        // js已经考虑调整与节假日问题，此处重复 delete by renping 20191117 end
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            // 场内基金改造 modify by renping 20191117 start
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_认购确认.getValue())) {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_货币基金申购);
            }
            else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
                // 场内基金改造 modify by renping 20191117 end
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_货币基金申购);
            }
            else {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_货币基金赎回);
            }
            dto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        else {
            dto.setTradeId(IDGenerator.generate());
        }
        ProcessParameter<TrdFundInvestTradeConfirmDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdFundInvestTradeConfirmDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId("TrdFundInvestTrade003");
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            processParameter.setOperationType(OperationType.NEW);
        }
        else {
            processParameter.setOperationType(OperationType.MODIFY);
        }
        workFlowParameter.setPageId("TrdFundInvestTrade003");
        workFlowParameter.setElementId("TrdFundInvestTrade003_btn_save");
        workFlowParameter.setBusinessCode((entity) -> entity.getTradeId());
        workFlowParameter.setServiceName("trdFundInvestTrade003Service");
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_基金类资产交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(getMessage("TrdFundInvestTrade002.tradeId"),
            entity.getTradeId(), getMessage("TrdFundInvestTrade002.finprodName"), entity.getFinprodName(),
            getMessage("TrdFundInvestTrade002.tradeDate"), DateUtil.format(entity.getTradeDate())));
        dto.setPortfolioId(dto.getActPortfolioId());
        if(null!=dto.getTradeDate()){
        	workFlowParameter.setCrtDate(dto.getTradeDate());
        }
        return processParameter;
    }

    private void setChannelFee(TrdFundInvestTradeDto entity) {
        // 场内基金改造 modify by renping 20191117 start
        // if (DictCodeList.交易方向_买入.getValue().equals(entity.getPs()) || DictCodeList.交易方向_申购.getValue().equals(entity.getPs())
        // || DictCodeListExt2.交易方向_认购.getValue().equals(entity.getPs())) {
        // entity.setTradeAmt(ArithmeticUtil.doAdd(entity.getTradeAmt(), entity.getChannelAmt()));
        // }
        // else {
        // entity.setTradeAmt(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getChannelAmt()));
        // }
        if (StringUtil.isNotEmpty(entity.getChlId()) && entity.getCollectType() != null
            && StringUtil.equals(entity.getCollectType(), DictCodeListExt2.收取方式_回款前扣除.getValue())) {
            entity.setFeeAmtPay(ArithmeticUtil.doAdd(entity.getFeeAmtPay(), entity.getChannelAmt()));
            entity.setFeeAmt(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getChannelAmt()));
        }
        else if (StringUtil.isNotEmpty(entity.getChlId())) {
            entity.setFeeAmtUnpay(ArithmeticUtil.doAdd(entity.getFeeAmtUnpay(), entity.getChannelAmt()));
            entity.setFeeAmt(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getChannelAmt()));
        }

        // 佣金
        if (entity.getIsPayWithTrade() != null && StringUtil.equals(entity.getIsPayWithTrade(), DictCodeList.是否_否.getValue())) {
            entity.setFeeAmtUnpay(ArithmeticUtil.doAdd(entity.getFeeAmtUnpay(), entity.getCommissionFee()));
        }
        else {
            entity.setFeeAmtPay(ArithmeticUtil.doAdd(entity.getFeeAmtPay(), entity.getCommissionFee()));
        }
        entity.setFeeAmt(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getCommissionFee()));
        // 场内基金改造 modify by renping 20191117 end
    }

    /**
     * 提交流程启动时
     */
    @Override
    public void doInsert(TrdFundInvestTradeDto entity, OperationType type) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/doInsert", entity, Integer.class);
        if (StringUtil.equals(entity.getOperateType(), "submit")) {
            // if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_卖出.getValue())) {
            // doInsertRedemption(entity);
            // }
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
            BaseTradeProductDealDto dto = new BaseTradeProductDealDto();
            if (!(entity.getChlId() == null || "".equals(entity.getChlId()))) {
                setChannelFee(entity);
            }
            dto.setTrdProductDeal(entity);
            dto.setTrdProductDealAdd(entity.getTrdProductDealAdds().get(0));
            List<StlPayOrdersDto> spodList = new ArrayList<>();
            for (BussTrdTransferOrderDto bttod : entity.getBussTrdTransferOrder()) {
                spodList.add(bttod);
                dto.setStlPayOrdersList(spodList);
            }

            baseTradeProductDealDtos.add(dto);
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
            // 委托代码不为空处理委托代码
            if (StringUtil.isNotEmpty(entity.getEntrustId())) {
                dto.setEntrustId(entity.getEntrustId());
            }

            if (entity.getTrdFinProductDealTotal() != null) {
                finProductTradeDto.setNeedSum(true);
            }
            baseTradeProductDealCommService.execute(finProductTradeDto);

            if (StringUtil.isNotEmpty(entity.getActPortfolioId())
                && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                        entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                        entity.getPortfolioId()));
            }
            
//            //调用授信
//            TrdProductDeal trdProductDeal = new TrdProductDeal();
//            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity);
//            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
//            inputDto.setTrdProductDeal(trdProductDeal);
//            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
        }
    }

    /**
     * 代办中提交流程启动时
     */
    @Override
    public void doUpdate(TrdFundInvestTradeDto entity) {
        if (StringUtil.equals(entity.getOperateType(), "save")) {
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/doUpdate", entity,
                Integer.class);
        }
        else if (StringUtil.equals(entity.getOperateType(), "submit")) {
            // doDelete(entity.getWorkflowId());
            prepareEntity(entity);
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdFundInvestTradeDto.class, true);
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

            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/doUpdate", entity,
                Integer.class);
            // if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_卖出.getValue())) {
            // doInsertRedemption(entity);
            // }
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
            BaseTradeProductDealDto dto = new BaseTradeProductDealDto();
            dto.setTrdProductDeal(entity);
            dto.setTrdProductDealAdd(entity.getTrdProductDealAdds().get(0));
            List<StlPayOrdersDto> spodList = new ArrayList<>();

            for (BussTrdTransferOrderDto bttod : entity.getBussTrdTransferOrder()) {
                spodList.add(bttod);
                dto.setStlPayOrdersList(spodList);

            }
            baseTradeProductDealDtos.add(dto);
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
            // 委托代码不为空处理委托代码
            if (StringUtil.isNotEmpty(entity.getEntrustId())) {
                dto.setEntrustId(entity.getEntrustId());
            }

            if (entity.getTrdFinProductDealTotal() != null) {
                finProductTradeDto.setNeedSum(true);
            }
            baseTradeProductDealCommService.execute(finProductTradeDto);

            if (StringUtil.isNotEmpty(entity.getActPortfolioId())
                && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                        entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
                        entity.getPortfolioId()));
            }
            
//            //调用授信
//            TrdProductDeal trdProductDeal = new TrdProductDeal();
//            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity);
//            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
//            inputDto.setTrdProductDeal(trdProductDeal);
//            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
        }
    }

    /**
     * 插入交易到账信息 <功能详细描述>
     * 
     * @param entity
     * @see
     * @since 1.0
     */
    public void doInsertRedemption(TrdFundInvestTradeDto entity) {
        UploadFile file = null;
        List<SysUploadFileInfo> syfiList = entity.getFileArray();
        for (SysUploadFileInfo sufi : syfiList) {
            file = fileAttachmentProcess.fetch(sufi.getSeqNo());
            String generateId = IDGenerator.generate();
            file.setFileNo(generateId);
            fileAttachmentProcess.upload(file);
            sufi.setSeqNo(generateId);
        }
        TrdFundInvestTradeDto redEntity = new TrdFundInvestTradeDto();
        redEntity = entity;
        redEntity.setbTradeId(entity.getTradeId());
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_货币基金赎回);
            redEntity.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        else {
            redEntity.setTradeId(IDGenerator.generate());
        }
        redEntity.setPs(DictCodeList.交易方向_赎回到账.getValue());
        prepareEntity(redEntity);
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/doInsertRedemption", redEntity,
            void.class);
    }

    /**
     * 流程作废时删除业务流程表 <功能详细描述>
     * 
     * @param workflowId
     * @see
     * @since 1.0
     */
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
        
        //根据工作流代码查询交易相关信息
//        WflTrdProductDealAdd wflTrdProductDealAdd = restTemplate.getForObject(
//            ServiceApplication.TRADE_SERVICE_NAME
//                    + "/common/trdcommonbase/selectWflTrdProductDealAdd/" + workflowId, WflTrdProductDealAdd.class);
//        //删除授信占用
//        if (null != wflTrdProductDealAdd && null != wflTrdProductDealAdd.getTradeId()){
//            riskCreditListCommonValidatorService.deleteCredit(wflTrdProductDealAdd.getTradeId());
//        }
    }

    /**
     * 资产代码下拉框 <功能详细描述>
     * 
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodId(Map<String, Object> params) {
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/getFinprodId", params,
            SelectOptionDto.class);
    }

    // 资产推荐方 新增 zhuyongkang 20191023 start
    /**
     * 构建推荐方下拉框
     *
     * @param parameters 参数
     * @return 下拉框查询结果
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getAssetRecommandOrg(Map<String, Object> parameters) {
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/getAssetRecommandOrg",
            parameters, SelectOptionDto.class);
    }
    // 资产推荐方 新增 zhuyongkang 20191023 start

    /**
     * 查询组合可用现金
     *
     * @param param
     * @return
     */
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
        Date vdate = DateUtil.parse((String)param.get("vdate"));
        String calId = (String)param.get("calId");
        if (StringUtil.isNotEmpty(calId)) {
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(vdate)),
                DateUtil.addDay(DateUtil.parse(DateUtil.format(vdate)), 15), calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(vdate)), 1, holidayList, "1");
            param.put("vdate1", DateUtil.format(tradeDay));
        }
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/getProAvailableCash",
            param, Map.class);
    }

    /**
     * 查询组合可用持仓 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getPtlSecPosition(PtlSecPositionFlowDto dto) {
        dto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/getPtlSecPosition", dto,
            BigDecimal.class);
    }

    /**
     * 查询资产管理人 <功能详细描述>
     * 
     * @param issuer
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, String> selectManeger(String issuer) {
        String id = restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/selectManeger?issuer=" + issuer, String.class);
        Map<String, String> result = new HashMap<>();
        result.put("id", id);
        return result;
    }

    /**
     * 查询反显数据 <功能详细描述>
     * 
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    public TrdFundInvestTradeDto getTrdFundDto(Map<String, Object> params) {
        return restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/getTrdFundDto", params,
            TrdFundInvestTradeDto.class);
    }

    /**
     * 查询投资委托DTO <功能详细描述>
     * 
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public BaseEtrInvestInfoDto getEtrustDto(String entrustId) {
        return restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/getEtrustDto?entrustId=" + entrustId,
            BaseEtrInvestInfoDto.class);
    }

    /**
     * 查询委托可用金额 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> selectEntrustAmt(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/selectEntrustAmt", param,
            Map.class);
    }

    /**
     * 处理基金投资交易自定义dto <功能详细描述>
     *
     * @param dto
     * @see
     * @since 1.0
     */
    public void prepareEntity(TrdFundInvestTradeDto dto) {
        dto.setActPortfolioId(dto.getPortfolioId());
        dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));
        // dto.setTradeAmt(dto.getPrinAmt());
        dto.setYield(ArithmeticUtil.divide(dto.getYield(), new BigDecimal(100)));
        dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), new BigDecimal(100)));
        dto.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue")));
        dto.setIsCancel(DictCodeList.是否_否.getValue());
        dto.setBusiType(DictCodeList.共通业务类型_基金类资产交易.getValue());
        dto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
//        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
//            dto.setIntAmt(new BigDecimal(0));
//        }
        if (StringUtil.equals(dto.getSettleSpreed(), DictCodeList.清算速度_T_0.getValue())) {
            dto.setSettleSpeed((long)0);
        }
        else if (StringUtil.equals(dto.getSettleSpreed(), DictCodeList.清算速度_T_1.getValue())) {
            dto.setSettleSpeed((long)1);
        }
        else if (StringUtil.equals(dto.getSettleSpreed(), DictCodeList.清算速度_T_2.getValue())) {
            dto.setSettleSpeed((long)2);
        }
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
            dto.setIsClean(DictCodeList.是否_是.getValue());
            dto.setFeeAmt(dto.getFeeAmtPay());
            dto.setIssuePartyId(CacheUtil.getSysParam(BussinessConstant.ISSUE_PARTY_ID));
        }
        if (StringUtil.isNotEmpty(dto.getRedCfmDate()) && StringUtil.equals(DictCodeList.交易方向_赎回到账.getValue(), dto.getPs())) {
            dto.setSettleDate(dto.getRedCfmDate());
        }
        else {
            dto.setSettleDate(dto.getTradeDate());
        }
        TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
        if (personInfo != null) {
            dto.setTrader(personInfo.getTrader());
            dto.setCounterId(personInfo.getCounterId());
            dto.setIsOutTrade(personInfo.getIsOutTrade());
            dto.setCounterTrader(personInfo.getCounterTrader());
            dto.setCounterContact(personInfo.getContactInfo());
        }

        // 金融产品交易附属表
        List<TrdProductDealAdd> adds = new ArrayList<>();
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        // add by zhangnian 20190919 start
        if (StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_认购.getValue())
            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())
            || StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_转换申请.getValue())) {
            trdProductDealAdd.setPurCfmDate(dto.getPurCfmDate());
        }
        // add by zhangnian 20190919 end
        /*
         * if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())){
         *
         * Map<String, Object> param = new HashMap<String, Object>(); param.put("finprodId", dto.getFinprodId()); Map<String, Object> result =
         * trdFundInvestTrade003Service.getPurSpeed(param); String speed = (String)result.get("SPEED"); String calId = (String)result.get("CALID");
         * if(StringUtil.equals(speed, "1") || StringUtil.equals(speed, "2")) { int speedCount = Integer.parseInt(speed); Date tradeDate = dto.getTradeDate();
         * List<String> holidayList =
         * dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(tradeDate)),DateUtil.addDay(DateUtil.parse(DateUtil.format(tradeDate)),
         * 15),calId); Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(tradeDate)), speedCount, holidayList, "1");
         * trdProductDealAdd.setPurCfmDate(tradeDay); } trdProductDealAdd.setPurCfmDate(dto.getPurCfmDate()); }else{
         * trdProductDealAdd.setRedCfmDate(dto.getRedCfmDate()); }
         */
        trdProductDealAdd.setTradeId(dto.getTradeId());
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId());
        trdProductDealAdd.setEntrustId(dto.getEntrustId());
        // trdProductDealAdd.setProdId(dto.getFinprodId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        trdProductDealAdd.setrFinprodId(dto.getrFinprodId());
        trdProductDealAdd.setAssetRecommandOrg(dto.getAssetRecommandOrg());
        adds.add(trdProductDealAdd);
        dto.setTrdProductDealAdds(adds);
        // 组合交易表
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        ptlTrade.setBusiNo(dto.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(dto.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_基金类资产交易.getValue());
        ptlTrade.setSettleDate(dto.getSettleDate());

        ptlTrades.add(ptlTrade);

        if (!dto.getPortfolioId().equals(dto.getActPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            ptlTrade1.setPortfolioId(dto.getActPortfolioId());
            ptlTrade1.setBusiNo(dto.getTradeId());
            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade1.setBusiId(dto.getFinprodId());
            ptlTrade1.setBusiType(DictCodeList.共通业务类型_基金类资产交易.getValue());
            ptlTrade1.setSettleDate(dto.getSettleDate());
            ptlTrades.add(ptlTrade1);
        }

        dto.setPtlTrade(ptlTrades);
        // 划款指令
        if (dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0) {
            dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getTradeId());
            dto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            dto.getBussTrdTransferOrder().get(0).setIsTotalTrade(DictCodeList.是否_否.getValue());
        }
        // 金融产品交易扩展
        List<PaTrdProductDeal> paTrdProductDeals = new ArrayList<>();
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(dto.getTradeId());
        String paBussType = null;
        if (StringUtil.isNotEmpty(dto.getType6())) {
            if (StringUtil.equals(dto.getType6(), DictCodeList.基金类资产类型_货币基金.getValue())) {
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
                    paBussType = DictCodeListExt1.平安业务类型_货币基金申购申请.getValue();
                }
                else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
                    paBussType = DictCodeListExt3.平安业务类型_货币基金买入.getValue();
                }
                else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    paBussType = DictCodeListExt1.平安业务类型_货币基金赎回申请.getValue();
                }
                else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                    paBussType = DictCodeListExt3.平安业务类型_货币基金卖出.getValue();
                }
                else if (StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_认购.getValue())) {
                    paBussType = DictCodeListExt3.平安业务类型_货币基金认购申请.getValue();
                }
                else if (StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_转换申请.getValue())) {
                    paBussType = DictCodeListExt1.平安业务类型_货币基金赎回申请.getValue();
                }
            }
            else if (StringUtil.equals(dto.getType6(), DictCodeList.基金类资产类型_非货币基金.getValue())) {
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
                    paBussType = DictCodeListExt1.平安业务类型_非货币基金申购申请.getValue();
                }
                else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
                    paBussType = DictCodeListExt3.平安业务类型_非货币基金买入.getValue();
                }
                else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    paBussType = DictCodeListExt1.平安业务类型_非货币基金赎回申请.getValue();
                }
                else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                    paBussType = DictCodeListExt3.平安业务类型_非货币基金卖出.getValue();
                }
                else if (StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_认购.getValue())) {
                    paBussType = DictCodeListExt3.平安业务类型_非货币基金认购申请.getValue();
                }
                else if (StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_转换申请.getValue())) {
                    paBussType = DictCodeListExt1.平安业务类型_非货币基金赎回申请.getValue();
                }
            }
        }
        paTrdProductDeal.setPaBussType(paBussType);
        dto.setTgBusiType(paBussType);
        paTrdProductDeal.setDeptCode(personInfo==null?null:personInfo.getTraderDept());
        paTrdProductDeals.add(paTrdProductDeal);
        dto.setPaTrdProductDeals(paTrdProductDeals);
        dto.setPortfolioId(dto.getActPortfolioId());

        if (StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_转换申请.getValue())) {
            基金转换特殊处理(dto);
        }
    }

    private void 基金转换特殊处理(TrdFundInvestTradeDto dto) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, dto);
        dto.setTrdFinProductDealTotal(trdFinProductDealTotal);

        TrdDealTotalAdd dealTotalAdd = new TrdDealTotalAdd();
        dealTotalAdd.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        dealTotalAdd.setAssetRecommandOrg(dto.getAssetRecommandOrg());
        dealTotalAdd.setEntrustId(dto.getEntrustId());
        dealTotalAdd.setPurCfmDate(dto.getPurCfmDate());
        dto.setDealTotalAdd(dealTotalAdd);

        dto.setPs(DictCodeListExt2.交易方向_转换赎回申请.getValue());
    }

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId)
        throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        TrdFundInvestTradeDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            BigDecimal amt = dto.getTradeAmt();
            if (amt == null || amt.compareTo(BigDecimal.ZERO) <= 0) {
                amt = dto.getShareAmt();
            }
            try {
//              return commonBusinessService.是否需要上级审批(dto.getCounterId(), amt, LoginUserUtil.getUserName(),
//                  DictCodeList.平安资产类型_基金.getValue(), dto.getType6(), dto.getTradeMarket(), dto.getPs()
//                  // 资产类型改为授权类型 add by renping 2019-08-06 start
//                  , dto.getFinprodId()
//              // 资产类型改为授权类型 add by renping 2019-08-06 end
//              );
              Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), amt,LoginUserUtil.getUserName(),
                  DictCodeList.平安资产类型_基金.getValue(), dto.getType6(), dto.getTradeMarket(), dto.getPs()
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
        // return null;
    }

    /**
     * 通道：联想框文本框
     *
     * @param parameters 画面参数
     * @return 通道：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getChannel(Map<String, Object> parameters) {
        List<SuggestDto> tempList = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getChannel", parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                // if (StringUtil.isNotBlank((String) parameters.get("portfolioId"))) {
                // selectOptionDto.setParentId((String) parameters.get("portfolioId"));
                // }
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }

    public String getChannelInfo(String parameters) {
        String temp = null;
        Map<String, Object> map = new HashMap<>();
        map.put("chlId", parameters);
        List<String> tempList = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getChannelInfo", map, String.class);
        if (!StringUtil.isEmptyList(tempList)) {
            temp = tempList.get(0);

        }
        return temp;
    }

    @Override
    public void doBack(TrdFundInvestTradeDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTradeId());
        // 卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
//        if (entity.getPs().equals(DictCodeListExt2.交易方向_认购.getValue()) || entity.getPs().equals(DictCodeList.交易方向_买入.getValue())
//            || entity.getPs().equals(DictCodeList.交易方向_申购.getValue())) {
//            // 交易方向为买入时，退回动作后 释放原占用的额度 Add by xkni 20191023
//            isNeedOccupy(entity, true, true, true);
//        }
        
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }

    @Override
    public void doRevoke(TrdFundInvestTradeDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTradeId());
        // 卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
//        if (entity.getPs().equals(DictCodeListExt2.交易方向_认购.getValue()) || entity.getPs().equals(DictCodeList.交易方向_买入.getValue())
//            || entity.getPs().equals(DictCodeList.交易方向_申购.getValue())) {
//            // 交易方向为买入时，退回动作后 释放原占用的额度 Add by xkni 20191023
//            isNeedOccupy(entity, true, true, true);
//        }
        
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }

    /**
     * 撤销/流程退回时额度占用、释放处理 Add by xkni 20191028
     *
     * @param entity 交易model
     * @param isNotBottomflag 是否是底层资产交易
     * @param isSave 是否入库
     * @param isdeleteflag 是否是作废删除
     * @return
     */
    private void isNeedOccupy(TrdFundInvestTradeDto entity, boolean isNotBottomflag, boolean isSave, Boolean isdeleteflag) {
        // 所有交易生效后调用的共通方法(入账信息) 撤销后释放占用的授信额度 Add 20190828 by xkni
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        inputDto.setProdid(entity.getPortfolioId());
        inputDto.setProdFinprodType(DictCodeListExt2.共通业务类型_股票信息.getValue());
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());// 交易状态
        trdProductDeal.setPs(entity.getPs());// 交易方向
        if (StringUtils.isNotBlank(entity.getfTradeId())) {
            trdProductDeal.setfTradeId(entity.getfTradeId());
        }
        trdProductDeal.setTradeId(entity.getTradeId());// 交易编号
        trdProductDeal.setFinprodId(entity.getFinprodId());// 金融产品代码
        trdProductDeal.setCreateDept(entity.getCreateDept());// 创建部门
        trdProductDeal.setCreateUser(entity.getCreateUser());// 创建人
        trdProductDeal.setCreateTime(entity.getCreateTime());// 创建时间
        trdProductDeal.setUpdateUser(entity.getUpdateUser());// 更新人
        trdProductDeal.setUpdateTime(entity.getUpdateTime());// 更新时间
        trdProductDeal.setTradeDate(entity.getTradeDate());// 交易日期
        trdProductDeal.setShareAmt(entity.getShareAmt());// 交易份额，债券数量等
        trdProductDeal.setTradeAmt(entity.getTradeAmt());// 交易金额
        trdProductDeal.setPrinAmt(entity.getPrinAmt());// 交易本金，债券券面等
        trdProductDeal.setFinprodType(entity.getFinprodType());// 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
        trdProductDeal.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
        TrdInvestPersonInfo002Dto personInfo = entity.getTrdInvestPersonInfo();
        if (personInfo != null) {
            // 交易对手
            trdProductDeal.setCounterId(personInfo.getCounterId());// 交易对手编号
        }

        inputDto.setTrdProductDeal(trdProductDeal);
        CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
        ceLinesUpdate.isNeedOccupyCre(inputDto, isNotBottomflag, isSave, isdeleteflag);
    }

    /// 强制退回 额度处理
    @Override
    public void excute(WorkFlowTaskDto workFlowTaskDto) {
        TrdFundInvestTradeDto entity = getBussinessEntityByWorkflowId(workFlowTaskDto.getWorkflowId());
        if (workFlowTaskDto.getProcessType().name().equals("REVOKE") && workFlowTaskDto.getTaskName().equals("领导审批")) {
            // 卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
            if (entity.getPs().equals(DictCodeList.交易方向_买入.getValue()) || entity.getPs().equals(DictCodeList.交易方向_申购.getValue())) {
                // 交易方向为买入时，退回动作后 释放原占用的额度 Add by xkni 20191023
                isNeedOccupy(entity, true, true, true);
            }
        }
    }

    // 场内基金改造 add by renping 20191117 start
    /**
     * 通道：联想框文本框
     *
     * @param dto TrdFundInvestTradeDto
     * @return TrdTradeFeeDetail
     * @see
     * @since 1.0
     */
    public TrdTradeFeeDetail getCommissionFeeInfo(TrdFundInvestTradeDto dto) {
        BasicTradeFeeInputDto inputDto = new BasicTradeFeeInputDto();
        inputDto.setTradeDate(dto.getTradeDate());
        inputDto.setTradeMarket(dto.getTradeMarket());
        inputDto.setFinprodType2(DictCodeList.平安资产类型_基金.getValue());
        inputDto.setPs(dto.getPs());// 交易方向
        inputDto.setType6(dto.getType6());// 资产分类
        inputDto.setType7(dto.getType7());
        inputDto.setFeeTypeList(Arrays.asList(DictCodeListExt1.一次性费用类型_佣金.getValue()));
        inputDto.setIsGrade("1");
        inputDto.setSecAcctId(dto.getSecManageAcctId());
        List<TrdTradeFeeDetail> tradeFeeList = baseTradeFeeCommService.bourseFeeDeal(inputDto);
        if (StringUtil.isEmptyList(tradeFeeList)) {
            return new TrdTradeFeeDetail();
        }
        return tradeFeeList.get(0);
    }
    // 场内基金改造 add by renping 20191117 end

    /* add by msk 新增查询通道划款交易 TISHKBS-1038  20201111 start */
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
    /* add by msk 新增查询通道划款交易 TISHKBS-1038  20201111 end */


    public Map<String, Object> getSpeedByFinprodId(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/getSpeedByFinprodId", param,
                Map.class);
    }
}
