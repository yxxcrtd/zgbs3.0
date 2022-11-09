package com.joyintech.tams.invest.period.trdbuyoutduration.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.WflPaTrdFinDealTotalMapper;
import com.joyintech.tams.common.base.dao.WflStlPayOrdersMapper;
import com.joyintech.tams.common.base.dao.WflTrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.WflTrdProductDealMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.autoperiod.repo.AutoPeriodRepoCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommonDeleteDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdbuyoutduration.dto.TrdRepoBuyoutDuration002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.period.trdbondduration.service
 * @Description: 债券存续期管理-查询
 * @date Date : 2018年09月21日 14:32
 */
@Service
public class TrdRepoBuyoutDuration002Service extends BaseService<TrdRepoBuyoutDuration002Dto> implements BaseProcessService<TrdRepoBuyoutDuration002Dto> {
    private static final Logger logger = LoggerFactory.getLogger(TrdRepoBuyoutDuration002Service.class);
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;

    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderImplService;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private InvestInterfCommService investInterfCommService;

    /**
     * AutoPeriodRepoCommonService
     */
    @Autowired
    private AutoPeriodRepoCommonService autoPeriodRepoCommonService;
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

    public TrdRepoBuyoutDuration002Dto findEffectiveData(Map<String, Object> pageParam) {
        pageParam.put("userName", LoginUserUtil.getUserName());
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoBuyoutDuration/findEffectiveData",
                pageParam, TrdRepoBuyoutDuration002Dto.class);
    }

    @Override
    public void doComplete(TrdRepoBuyoutDuration002Dto entity, OperationType operationType) {
        FinProductTradeDto tradeDto = genFinProductTradeDto(entity, operationType);
        tradeDto.setNeedSum(true);
        if (OperationType.REVOKE == operationType) {
            tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            entity.setOperateType(BussinessConstant.REVOKE);
            
            //根据工作流代码查询交易相关信息
//            System.out.println("tradeid-----------" + entity.getBusiCode());
            logger.info("tradeid-----------" + entity.getBusiCode());
            String tradeId = restTemplate.postForObject(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoDuration/findCeTradeIdByTotleTradeId/" + entity.getBusiCode(), null,
                String.class);
            //删除授信占用
            if (null != tradeId){
                riskCreditListCommonValidatorService.deleteCredit(tradeId);
            }
        } else {
            tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
        }

        tradeDto.getBaseTradeProductDealDtos().forEach(item->{
            if (OperationType.REVOKE == operationType) {
                getInterfInputDto(item, DictCodeListExt1.交易流程状态_撤销);
            }
            else {
                getInterfInputDto(item, DictCodeListExt1.交易流程状态_有效);
            }
        });
        baseTradeProductDealCommServiceImpl.execute(tradeDto);
        // 新建交易撤销数据 或 插入Entity到对应的业务DB中, 并删除流程表中的数据
        entity.setIssuePartyId(CacheUtil.getSysParam("issue_party_id"));
        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoBuyoutDuration/doComplete", entity, String.class);

        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            tradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> {
                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId());
                        if (OperationType.REVOKE == operationType) {
                            getInterfInputDto(baseTradeProductDealDto1, DictCodeListExt1.交易流程状态_撤销);
                        }
                        else {
                            getInterfInputDto(baseTradeProductDealDto1, DictCodeListExt1.交易流程状态_有效);
                        }
                    });
            baseTradeProductDealCommServiceImpl.insertPosition(tradeDto);
            tradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
        }
    }

    private void getInterfInputDto(BaseTradeProductDealDto baseTradeProductDealDto,
                                   DictCodeListExt1 交易流程状态) {
        if (baseTradeProductDealDto.getTrdProductDealAdd() == null
                || StringUtil.isEmpty(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())
                || AllTradeCommonUtil.isNotActPortfolio(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())) {
            return;
        }
        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        investInterfCommDto.setPageId("TrdRepoBuyoutDuration002");
        investInterfCommDto.setTradeStatus(交易流程状态.getValue());
        baseTradeProductDealDto.getTrdProductDeal().setPs(DictCodeList.交易方向_到期.getValue());
        investInterfCommDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
        investInterfCommDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
        if (baseTradeProductDealDto.getStlPayOrdersList() != null
                && !baseTradeProductDealDto.getStlPayOrdersList().isEmpty()) {
            investInterfCommDto.setStlPayOrders(baseTradeProductDealDto.getStlPayOrdersList().get(0));
        }
        investInterfCommService.doInvestInterf(investInterfCommDto);
    }


    private FinProductTradeDto genFinProductTradeDto(TrdRepoBuyoutDuration002Dto trdBondDuration001Dto, OperationType operationType) {
        // 5.构造调用组件的输入参数DTO
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDtos.add(baseTradeProductDealDto);

        TrdProductDeal trdProductDeal = getTrdProductDeal(trdBondDuration001Dto);
        if (operationType == OperationType.REVOKE) {
            trdProductDeal.setTradeId(trdProductDeal.getTradeId() + "R");
        }
        //有通道费 入库费用明细表
        if(StringUtil.isNotEmpty(trdBondDuration001Dto.getChannelFee())){
        	TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
            // 交易费用明细表.费用类型 = “通道费”
            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
            // 交易费用明细表.是否随交易支付  根据收取方式判断
        	if(StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), trdBondDuration001Dto.getCollectType())){
            	trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            }else{
            	trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            }
            // 交易费用明细表.交易金额 = 交易.通道费
            trdTradeFeeDetail.setFeeAmt(trdBondDuration001Dto.getChannelFee());
            baseTradeProductDealDto.setTrdTradeFeeDetailList(Arrays.asList(trdTradeFeeDetail));
        }
        // 划款指令表Entity
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderImplService.save(
                trdBondDuration001Dto.getBussTrdTransferOrder());
        List<StlPayOrdersDto> stlPayOrdersDtos = new ArrayList<>();
        if (stlPayOrdersList != null) {
            stlPayOrdersList.forEach(stlPayOrders -> {
                StlPayOrdersDto dto = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrders);
                if (operationType == OperationType.REVOKE) {
                    dto.setTradeId(dto.getTradeId() + "R");
                }
                stlPayOrdersDtos.add(dto);
            });
        }
        TrdProductDealAdd add = new TrdProductDealAdd();
        add.setPortfolioId(trdBondDuration001Dto.getPortfolioId());

        baseTradeProductDealDto.setTrdProductDealAdd(add);
        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
        baseTradeProductDealDto.setStlPayOrdersList(stlPayOrdersDtos);
        finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        return finProductTradeDto;
    }

    /**
     * 为调用共通组件组装的entity
     *
     * @param entity
     * @return
     */
    public static TrdProductDeal getTrdProductDeal(TrdRepoBuyoutDuration002Dto entity) {
        return AutoPeriodRepoCommonService.getTrdProductDeal(entity);
    }

    @Override
    public void doInsert(TrdRepoBuyoutDuration002Dto entity, OperationType type) {
        saveDataWhenStartWorkflow(entity, type);
    }

    private void saveDataWhenStartWorkflow(TrdRepoBuyoutDuration002Dto entity, OperationType type) {
        if (type == OperationType.REVOKE || "submit".equals(entity.getOperateType())) {
            // 金融产品交易信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
            WflTrdFinProductDealTotal trdFinProductDealTotal = gen汇总流程表(entity);
            trdFinProductDealTotal.setPortfolioId(entity.getActPortfolioId());
            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
            List<WflStlPayOrders> stlPayOrdersDtos = gen划款指令流程(entity, type);

            // 金融产品交易汇总扩展Entity -> 金融产品交易汇总扩展业务流程表
            WflPaTrdFinDealTotal wflPaTrdFinDealTotal = gen汇总扩展业务流程(entity, trdFinProductDealTotal);

            // 金融产品交易Entity -> 金融产品交易业务流程表
            List<WflTrdProductDeal> wflTrdProductDeals = gen交易业务流程(entity);

            if (type == OperationType.REVOKE) {
                trdFinProductDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId() + 'R');
                wflPaTrdFinDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId() + 'R');
                trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                wflTrdProductDeals.forEach(wflTrdProductDeal -> {
                    wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + 'R');
                });
            }
            wflTrdProductDeals.forEach(wflTrdProductDeal -> wflTrdProductDealMapper.insertSelective(wflTrdProductDeal));
            wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);
            wflTrdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
            stlPayOrdersDtos.forEach(wflStlPayOrders -> wflStlPayOrdersMapper.insertSelective(wflStlPayOrders));

            if (type != OperationType.REVOKE) {
                // 调用业务组件.【金融产品交易共通.交易产品接口】
                FinProductTradeDto tradeDto = genFinProductTradeDto(entity, OperationType.NEW);
                tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                baseTradeProductDealCommServiceImpl.execute(tradeDto);

                if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    tradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                    baseTradeProductDealCommServiceImpl.insertPosition(tradeDto);
                    tradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
                }
            }
        }
    }

    @Override
    public void doUpdate(TrdRepoBuyoutDuration002Dto entity) {
        baseTradeProductDealCommServiceImpl.delete(entity.getWorkflowId());
        saveDataWhenStartWorkflow(entity, OperationType.NEW);
    }

    private List<WflTrdProductDeal> gen交易业务流程(TrdRepoBuyoutDuration002Dto entity) {
        return autoPeriodRepoCommonService.gen交易业务流程(entity);
    }

    private WflPaTrdFinDealTotal gen汇总扩展业务流程(TrdRepoBuyoutDuration002Dto entity, WflTrdFinProductDealTotal trdFinProductDealTotal) {
        return AutoPeriodRepoCommonService.gen汇总扩展业务流程(entity, trdFinProductDealTotal);
    }

    private List<WflStlPayOrders> gen划款指令流程(TrdRepoBuyoutDuration002Dto entity, OperationType type) {
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderImplService.save(entity.getBussTrdTransferOrder());
        List<WflStlPayOrders> stlPayOrdersDtos = new ArrayList<>();
        if (stlPayOrdersList != null) {
            stlPayOrdersList.forEach(stlPayOrders -> {
                WflStlPayOrders dto = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrders);
                dto.setTradeId(entity.getBusiCode());
                dto.setWorkflowId(entity.getWorkflowId());
                dto.setStatus(DictCodeList.数据状态_新建中.getValue());
                dto.setCreateDept(entity.getCreateDept());
                dto.setUpdateTime(entity.getUpdateTime());
                dto.setCreateUser(entity.getCreateUser());
                dto.setCreateTime(entity.getCreateTime());
                dto.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
                if (type == OperationType.REVOKE) {
                    dto.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                    dto.setTradeId(dto.getTradeId() + "R");
                } else {
                    dto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
                }
                stlPayOrdersDtos.add(dto);
            });
        }
        return stlPayOrdersDtos;
    }

    private static WflTrdFinProductDealTotal gen汇总流程表(TrdRepoBuyoutDuration002Dto entity) {
        return AutoPeriodRepoCommonService.gen汇总流程表(entity, DictCodeList.共通业务类型_买断式回购存续期.getValue());
    }

    @Autowired
    private PeriodCommonService periodCommonService;

    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommServiceImpl.delete(workflowId);
        TrdRepoBuyoutDuration002Dto entity = getBussinessEntityByWorkflowId(workflowId);
        TrdCommonDeleteDto trdCommonDeleteDto = new TrdCommonDeleteDto();
        trdCommonDeleteDto.setWorkflowId(workflowId);
        if (DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue().equals(entity.getFinprodType2())) {
            trdCommonDeleteDto.setWflTrdFinProductDealTotal(gen汇总流程表(entity));
        }

        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoBuyoutDuration/doDelete", trdCommonDeleteDto,
                String.class);
    }

    public Page<RepoInfo> findRepoInfo() {
        return new Page<>(restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoBuyoutDuration/findRepoInfo",
                RequestUtil.getPageParam(), RepoInfo.class));
    }

    @Override
    public void doBack(TrdRepoBuyoutDuration002Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBusiCode());
    }

    @Override
    public void doRevoke(TrdRepoBuyoutDuration002Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBusiCode());
    }
}
