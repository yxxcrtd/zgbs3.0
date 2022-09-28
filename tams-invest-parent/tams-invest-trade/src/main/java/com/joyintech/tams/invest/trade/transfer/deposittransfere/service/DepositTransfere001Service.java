package com.joyintech.tams.invest.trade.transfer.deposittransfere.service;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
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
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.entrust.dao.EtrTrdCommonMapper;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.trade.transfer.deposittransfere.dto.DepositTransfere001Dto;
import com.joyintech.tams.invest.trade.transfer.deposittransfere.dto.DepositTransfere002Dto;
import com.joyintech.tams.invest.trade.transfer.deposittransfere.dto.TrdDepoDuration001Dto;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransferDto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class DepositTransfere001Service extends CommonExportService<DepositTransfere001Dto> implements BaseProcessService<DepositTransfere001Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(DepositTransfere001Service.class);

    /**
     * 通用Mapper共同代理方法
     */
    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * 金融产品交易实体类
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Autowired
    EtrTrdCommonMapper etrTrdCommonMapper;

    @Autowired
    private RepAssetDeptMapper repAssetDeptMapper;
    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;
    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;
    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;
    @Autowired
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 金融产品Mapper
     */
    @Autowired(required = false)
    private FinProductMapper finProductMapper;

    /**
     * 划款指令生成
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * 划款指令业务流程表Mapper
     */
    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    /**
     * 查询组合代码
     *
     * @param pageParam
     * @return
     */
    public List<SelectOptionDto> selectPortoId(Map<String, Object> pageParam) {
        pageParam.put("userId", LoginUserUtil.getUserName());
        return simpleRestTemplate.postForList(ServiceApplication.REPORT_SERVICE_NAME + "/DepositTransfere001/queryProdIdInfo",
                pageParam, SelectOptionDto.class);
    }

    /**
     * 查询利息信息
     *
     * @param param
     * @return
     */
    @SuppressWarnings("unchecked")
    public DepositTransfere001Dto queryAssetInt(Map<String, Object> param) {
        return simpleRestTemplate.postForObject(ServiceApplication.REPORT_SERVICE_NAME + "/DepositTransfere001/queryAssetInt",
                param, DepositTransfere001Dto.class);
    }

    /**
     * 插入业务流程表
     *
     * @param entity 存款类实体类
     * @see
     * @since 1.0
     */
    public void beforeWorkFlow(DepositTransfere001Dto entity) {
        if (!StringUtil.isEmptyList(entity.getDepositTransfere002Dto())) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            entity.getDepositTransfere002Dto().forEach(DepositTransfere002Dto -> {
//                // 调用授信共同 sjj start
//                TrdProductDeal TrdProductDeal = new TrdProductDeal();
                FinProductTradeDto finProductTradeDto = DepositTransfere002Dto.getFinProductTradeDto();
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                if (!StringUtil.isEmptyList(finProductTradeDto.getBaseTradeProductDealDtos())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {
                        TrdProductDeal trdProductDeal = baseTradeProductDealDto.getTrdProductDeal();
                        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                        inputDto.setTrdProductDeal(trdProductDeal);
                        AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
                        // 调用授信共同 sjj end

                        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, trdProductDeal);
                        wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
                        if (wflTrdProductDeal != null) {
                            wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
                        }
                        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseTradeProductDealDto.getTrdProductDealAdd());
                        wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
                        if (StringUtil.equals(trdProductDeal.getPs(), DictCodeList.交易方向_买入.getValue())) {
                            wflTrdProductDealAdd.setPortfolioId(DepositTransfere002Dto.getActPortfolioId());
                        } else {
                            wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
                        }
                        if (wflTrdProductDealAdd != null) {
                            wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
                        }
                        List<StlPayOrdersDto> list = baseTradeProductDealDto.getStlPayOrdersList();
                        if (list != null && list.size() > 0) {
                            for (StlPayOrdersDto stlPayOrdersDto : list) {
                                WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                                BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                                wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                                wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                                if (wflStlPayOrders.getOrderId() != null) {
                                    wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                                }
                            }
                        }
                    });
                }
                WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
                List<PaTrdProductDeal> paTrdProductDeals = DepositTransfere002Dto.getPaTrdProductDeals();
                paTrdProductDeals.forEach(paTrdProductDeal -> {
                    BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, paTrdProductDeal);
                    wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
                    if (wflPaTrdProductDeal != null) {
                        wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
                    }
                });
                //        委托代码不为空处理委托代码
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
                    String entrustId = StringUtil.isNotEmpty(entity.getEntrustId()) ? entity.getEntrustId() : item.getTrdProductDealAdd() != null ? item.getTrdProductDealAdd().getEntrustId() : null;
                    if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
                        item.setEntrustId(entrustId);
                    }
                });

                baseTradeProductDealCommService.execute(finProductTradeDto);
                //若带转出产品组合是子组合，插入对应持仓
                if (StringUtil.isNotEmpty(entity.getPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
                        if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                            baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId());
                        }
                    });
                    baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
                        if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                            baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId());
                        }
                    });
                }
                //若带转入产品组合是子组合，插入对应持仓
                if (StringUtil.isNotEmpty(DepositTransfere002Dto.getPortfolioId())
                        && !DepositTransfere002Dto.getActPortfolioId().equals(DepositTransfere002Dto.getPortfolioId())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
                        if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                            baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(DepositTransfere002Dto.getActPortfolioId());
                        }
                    });
                    baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
                        if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                            baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(DepositTransfere002Dto.getPortfolioId());
                        }
                    });
                }
            });
        }
    }

    @Override
    public void doComplete(DepositTransfere001Dto entity, OperationType type) {
        String workflowId = entity.getWorkflowId();
        baseTradeProductDealCommService.deleteWflData(workflowId);
        if (!StringUtil.isEmptyList(entity.getDepositTransfere002Dto())) {
            entity.getDepositTransfere002Dto().forEach(DepositTransfere002Dto -> {
                FinProductTradeDto finProductTradeDto = DepositTransfere002Dto.getFinProductTradeDto();
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                //        委托代码不为空处理委托代码
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
                    String entrustId = StringUtil.isNotEmpty(entity.getEntrustId()) ? entity.getEntrustId() : item.getTrdProductDealAdd() != null ? item.getTrdProductDealAdd().getEntrustId() : null;
                    if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
                        item.setEntrustId(entrustId);
                    }
                });
                basRiskMathCommService.insert(DepositTransfere002Dto.getRiskTrialRetDto(), DepositTransfere002Dto.getTradeId());
                entity.setOperateType(type.toString());
                simpleRestTemplate.postForObject(ServiceApplication.REPORT_SERVICE_NAME
                        + "/DepositTransfere001/doComplete", DepositTransfere002Dto, String.class);
                // 所有交易生效后调用的共通方法
                if (!StringUtil.isEmptyList(finProductTradeDto.getBaseTradeProductDealDtos())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {
                        TrdProductDeal trdProductDeal = baseTradeProductDealDto.getTrdProductDeal();
                        TrdProductDealAdd trdProductDealAdd = baseTradeProductDealDto.getTrdProductDealAdd();
                        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                        inputDto.setProdid(trdProductDealAdd.getPortfolioId());
                        inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                        inputDto.setTrdProductDeal(trdProductDeal);
                        inputDto.setTrdProductDealAdd(trdProductDealAdd);
                        inputDto.setTrdTradeFeeDetail(null);
                        AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
                        afterWorkFlow(entity, baseTradeProductDealDto);
                    });
                }

                baseTradeProductDealCommService.execute(finProductTradeDto);
                //若带转出产品组合是子组合，插入对应持仓
                if (StringUtil.isNotEmpty(entity.getPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
                        if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                            baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId());
                        }
                    });
                    baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
                        if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                            baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId());
                        }
                    });
                }
                //若带转入产品组合是子组合，插入对应持仓
                if (StringUtil.isNotEmpty(DepositTransfere002Dto.getPortfolioId())
                        && !DepositTransfere002Dto.getActPortfolioId().equals(DepositTransfere002Dto.getPortfolioId())) {
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
                        if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                            baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(DepositTransfere002Dto.getActPortfolioId());
                        }
                    });
                    baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                    finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
                        if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                            baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(DepositTransfere002Dto.getPortfolioId());
                        }
                    });
                }
            });

        }

    }

    @Override
    public void doInsert(DepositTransfere001Dto entity, OperationType type) {
        if (type.toString().equals("NEW") && entity.getOperateType().equals("submit")) {
            beforeWorkFlow(entity);
        }
    }

    @Override
    public void doUpdate(DepositTransfere001Dto entity) {
        if (entity.getOperateType().equals("submit")) {
            doDelete(entity.getWorkflowId());
            if (!StringUtil.isEmptyList(entity.getDepositTransfere002Dto())) {
                entity.getDepositTransfere002Dto().forEach(DepositTransfere002Dto -> {
                    BussinessCodeParameter bussinessCodeParameter = this.genTradeId(entity.getFinprodId());
                    DepositTransfere002Dto.setTradeId(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) :
                            IDGenerator.generate());
                    DepositTransfere002Dto.setTradeIdOut(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) :
                            IDGenerator.generate());
                    this.setEntity(entity, DepositTransfere002Dto);
                });
            }
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(DepositTransfere001Dto.class, true);
                // 工作流ID
                newEntity.setWorkflowId(entity.getWorkflowId());
                newEntity.setStatus(entity.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(entity));
                // 更新业务表数据
                // 操作留痕表
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            beforeWorkFlow(entity);
        }
    }

    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
    }

    /**
     * 合成各实体类
     *
     * @param dto 存款类投资交易拆分实体类
     * @return 存款类投资交易拆分实体类
     * @see
     * @since 1.0
     */
    public void setEntity(DepositTransfere001Dto dto, DepositTransfere002Dto DepositTransfere002Dto) {
        //组合代码转为母组合代码
        DepositTransfere002Dto.setActPortfolioId(DepositTransfere002Dto.getPortfolioId());
        DepositTransfere002Dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(DepositTransfere002Dto.getPortfolioId()));
        dto.setActPortfolioId(dto.getTransferPortfolioId());
        dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getTransferPortfolioId()));

        List<BaseTradeProductDealDto> list = new ArrayList<BaseTradeProductDealDto>();
        FinProductTradeDto finDto = new FinProductTradeDto();
        //转入交易
        BaseTradeProductDealDto baseDtoIn = new BaseTradeProductDealDto();
        TrdProductDealAdd trdProductDealAdd = this.setTrdProductDealAdd(dto, DepositTransfere002Dto);
        baseDtoIn.setTrdProductDealAdd(trdProductDealAdd);
        TrdProductDeal trdProductDeal = this.getEntity(dto, DepositTransfere002Dto);
        baseDtoIn.setTrdProductDeal(trdProductDeal);
        //转出交易
        BaseTradeProductDealDto baseDtoOut = new BaseTradeProductDealDto();
        TrdProductDealAdd trdProductDealAddOut = new TrdProductDealAdd();
        TrdProductDeal trdProductDealOut = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDealAddOut, trdProductDealAdd);
        BeanUtil.copyPropertiesIgnoreNull(trdProductDealOut, trdProductDeal);
        trdProductDealAddOut.setPortfolioId(dto.getPortfolioId());
        trdProductDealAddOut.setTradeId(DepositTransfere002Dto.getTradeIdOut());
        trdProductDealOut.setTradeId(DepositTransfere002Dto.getTradeIdOut());
        trdProductDealOut.setRemark("存款类转出");
        trdProductDealOut.setrTradeId(DepositTransfere002Dto.getTradeId());
        trdProductDealOut.setPs(DictCodeList.交易方向_卖出.getValue());
        trdProductDealOut.setCounterId(DepositTransfere002Dto.getPortfolioId());
        baseDtoOut.setTrdProductDeal(trdProductDealOut);
        baseDtoOut.setTrdProductDealAdd(trdProductDealAddOut);
        //生成划款指令
//        List<StlPayOrdersDto> list1 = new ArrayList<>();
//        StlPayOrdersDto stlPayOrdersDto = convertToSPOEntity(trdProductDeal);
//        StlPayOrdersDto stlPayOrdersDtoOut = convertToSPOEntity(trdProductDealOut);
//        list1.add(stlPayOrdersDto);
//        list1.add(stlPayOrdersDtoOut);
        //划款指令
        gen划款指令_BY_交易(baseDtoIn, DictCodeListExt1.指令状态_新建.getValue());
        gen划款指令_BY_交易(baseDtoOut, DictCodeListExt1.指令状态_新建.getValue());
        list.add(baseDtoIn);
        list.add(baseDtoOut);
//        baseDtoIn.setStlPayOrdersList(list1);
        finDto.setBaseTradeProductDealDtos(list);
        DepositTransfere002Dto.setPtlTrade(this.setPtlTrade(dto, DepositTransfere002Dto));
        List<PaTrdProductDeal> paTrdProductDeal = this.setPaTrdProductDeal(dto, DepositTransfere002Dto);
        DepositTransfere002Dto.setPaTrdProductDeals(paTrdProductDeal);
        DepositTransfere002Dto.setFinProductTradeDto(finDto);
        //风险试算
        if (!StringUtil.isEmptyList(dto.getRiskTrialRetDto())) {
            for (RiskTrialRetDto riskTrialRetDto : dto.getRiskTrialRetDto()) {
                if (StringUtil.equals(riskTrialRetDto.getPortfolioId(), DepositTransfere002Dto.getPortfolioId())) {
                    DepositTransfere002Dto.setRiskTrialRetDto(null);
                    List<RiskTrialRetDto> riskTrialRetDtos = new ArrayList<>();
                    riskTrialRetDtos.add(riskTrialRetDto);
                    DepositTransfere002Dto.setRiskTrialRetDto(riskTrialRetDtos);
                }
            }
        }
    }

    /**
     * 获取划款指令list
     *
     * @return 划款指令list
     * @see
     * @since 1.0
     */
    public void gen划款指令_BY_交易(BaseTradeProductDealDto baseTradeProductDealDto, String mode) {
        BussSettleOrderParameterDto param = new BussSettleOrderParameterDto();
        param.setIsTotalTrade(DictCodeList.是否_否.getValue());
        param.setPortfolioId(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId());
        param.setDeal(baseTradeProductDealDto.getTrdProductDeal());
        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtos = Arrays.asList(bussTrdTransferOrderService.generateOrder(param));
        if (bussTrdTransferOrderDtos != null) {
            for (BussTrdTransferOrderDto ret : bussTrdTransferOrderDtos) {
                if (StringUtil.equals(OperationType.REVOKE.toString(), mode)) {
                    ret.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                }
                if (StringUtil.equals(ret.getPs(), DictCodeListExt1.收支方向_支出.getValue())) {
                    ret.setMyAcctNo(ret.getPayAcctNo());
                    ret.setCounterAcctNo(ret.getRecAcctNo());
                } else {
                    ret.setMyAcctNo(ret.getRecAcctNo());
                    ret.setCounterAcctNo(ret.getPayAcctNo());
                }
                if (StringUtil.isNotEmpty(ret.getIsNeedSend())) {
                    if (DictCodeList.是否_是.getCodeName().equals(ret.getIsNeedSend())) {
                        ret.setIsNeedSend(DictCodeList.是否_是.getValue());
                    } else if (DictCodeList.是否_否.getCodeName().equals(ret.getIsNeedSend())) {
                        ret.setIsNeedSend(DictCodeList.是否_否.getValue());
                    }
                }
            }
            StlPayOrdersDto stl = gen划款指令流程(bussTrdTransferOrderDtos, baseTradeProductDealDto.getTrdProductDealAdd(), null);
            if (!StringUtil.isNotEmpty(stl.getSettleDate())) {
                stl.setSettleDate(baseTradeProductDealDto.getTrdProductDeal().getSettleDate());
            }
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stl));
        }
    }

    /**
     * 生成划款指令（根据划款指令列表分配划款指令）
     * <功能详细描述>
     *
     * @param stlPayOrdersList 划款指令list
     * @param dealAdd          交易附属
     * @param workflowId       工作流代码
     * @return 生成结果
     * @see
     * @since 1.0
     */
    public StlPayOrdersDto gen划款指令流程(List<BussTrdTransferOrderDto> stlPayOrdersList, TrdProductDealAdd dealAdd,
                                     String workflowId) {
        StlPayOrdersDto stlPayOrders = null;
        for (BussTrdTransferOrderDto dto : stlPayOrdersList) {
            if (dto.getPortfolioId().equals(dealAdd.getPortfolioId())) {
                stlPayOrders = dto;
                stlPayOrders.setOrderStatus(DictCodeList.清算操作类型_新建.getValue());
                stlPayOrders.setTradeId(dealAdd.getTradeId());
                stlPayOrders.setWorkflowId(workflowId);
            }
        }
        return stlPayOrders;
    }

    /**
     * 转换成 划款指令表实体
     */
    public StlPayOrdersDto convertToSPOEntity(TrdProductDeal trdProductDeal) {
        StlPayOrdersDto spoEntity = new StlPayOrdersDto();
        spoEntity.setTradeId(trdProductDeal.getTradeId());
        String orderId = null;
        if (baseGetBusinessId != null) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
            orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
        } else {
            orderId = IDGenerator.generate();
        }
        spoEntity.setOrderId(orderId);
        spoEntity.setMyAcctNo(trdProductDeal.getCounterId());
        spoEntity.setCcy(trdProductDeal.getCcy());
        spoEntity.setSettleAmt(trdProductDeal.getTradeAmt());
        return spoEntity;
    }

    /**
     * 组合交易表
     *
     * @param dto 存款类投资交易实体类
     * @return 组合交易实体类
     * @see
     * @since 1.0
     */
    public List<PtlTrade> setPtlTrade(DepositTransfere001Dto dto, DepositTransfere002Dto DepositTransfere002Dto) {
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        PtlTrade ptlTradeOut = new PtlTrade();
        ptlTrade.setPortfolioId(DepositTransfere002Dto.getPortfolioId());
        ptlTrade.setBusiNo(DepositTransfere002Dto.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(dto.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_存款类资产交易.getValue());
        ptlTrade.setSettleDate(dto.getTradeDate());
        ptlTrades.add(ptlTrade);
        BeanUtil.copyPropertiesIgnoreNull(ptlTradeOut, ptlTrade);
        ptlTradeOut.setBusiNo(DepositTransfere002Dto.getTradeIdOut());
        ptlTradeOut.setPortfolioId(dto.getPortfolioId());
        ptlTrades.add(ptlTradeOut);
        if (!dto.getPortfolioId().equals(dto.getActPortfolioId()) && StringUtil.isNotEmpty(dto.getActPortfolioId())) {
            PtlTrade ptlTrade1Out = new PtlTrade();
            ptlTrade1Out.setPortfolioId(dto.getActPortfolioId());
            ptlTrade1Out.setBusiNo(DepositTransfere002Dto.getTradeIdOut());
            ptlTrade1Out.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade1Out.setBusiId(dto.getFinprodId());
            ptlTrade1Out.setBusiType(DictCodeList.共通业务类型_存款类资产交易.getValue());
            ptlTrade1Out.setSettleDate(dto.getTradeDate());
            ptlTrades.add(ptlTrade1Out);
        }
        if (!DepositTransfere002Dto.getPortfolioId().equals(DepositTransfere002Dto.getActPortfolioId())
                && StringUtil.isNotEmpty(DepositTransfere002Dto.getActPortfolioId())) {
            PtlTrade ptlTrade1In = new PtlTrade();
            ptlTrade1In.setPortfolioId(DepositTransfere002Dto.getActPortfolioId());
            ptlTrade1In.setBusiNo(DepositTransfere002Dto.getTradeId());
            ptlTrade1In.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade1In.setBusiId(dto.getFinprodId());
            ptlTrade1In.setBusiType(DictCodeList.共通业务类型_存款类资产交易.getValue());
            ptlTrade1In.setSettleDate(dto.getTradeDate());
            ptlTrades.add(ptlTrade1In);
        }
        return ptlTrades;
    }

    /**
     * 转入产品生成金融产品附属表
     *
     * @param dto 存款类投资交易实体类
     * @return 金融产品附属类
     * @see
     * @since 1.0
     */
    public TrdProductDealAdd setTrdProductDealAdd(DepositTransfere001Dto dto, DepositTransfere002Dto DepositTransfere002Dto) {
        TrdProductDealAdd addDto = new TrdProductDealAdd();
        addDto.setTradeId(DepositTransfere002Dto.getTradeId());
        addDto.setPortfolioId(DepositTransfere002Dto.getPortfolioId());
        addDto.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        return addDto;
    }

    /**
     * 金融产品实体类生成
     *
     * @param dto 存款类投资交易拆分实体类
     * @return 金融产品实体类
     * @see
     * @since 1.0
     */
    public TrdProductDeal getEntity(DepositTransfere001Dto dto, DepositTransfere002Dto DepositTransfere002Dto) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(DepositTransfere002Dto.getTradeId());
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_存款类资产交易.getValue());
        trdProductDeal.setFinprodId(dto.getFinprodId());
        trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        trdProductDeal.setTradeDate(dto.getTradeDate());
        trdProductDeal.setVdate(dto.getVdate());
        trdProductDeal.setMdate(dto.getMdate());
        trdProductDeal.setSettleDate(dto.getSettleDate());
        trdProductDeal.setCcy(dto.getCcy());
        trdProductDeal.setCpriceAmt(DepositTransfere002Dto.getPrinAmt());
        trdProductDeal.setShareAmt(DepositTransfere002Dto.getPrinAmt());
        trdProductDeal.setPrinAmt(DepositTransfere002Dto.getPrinAmt());
        trdProductDeal.setIntAmt(DepositTransfere002Dto.getIntAmt());
        trdProductDeal.setFpriceAmt(DepositTransfere002Dto.getPrinAmt());
        trdProductDeal.setTradeAmt(DepositTransfere002Dto.getTransferTradeAmt());
        trdProductDeal.setCounterId(dto.getPortfolioId());
        trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        trdProductDeal.setIsOutTrade(DictCodeList.是否_否.getValue());
        trdProductDeal.setRemark("存款类转入");
        trdProductDeal.setCreateUser(dto.getCreateUser());
        trdProductDeal.setCreateTime(new Date());
        trdProductDeal.setUpdateUser(dto.getUpdateUser());
        trdProductDeal.setUpdateTime(new Date());
        trdProductDeal.setChlId(dto.getChlId());
        //add by liuwei for huaxing 20200330 start
        trdProductDeal.setTrader(trdProductDeal.getCreateUser());
        //add by liuwei for huaxing 20200330 end
        trdProductDeal.setIsSwapTransaction(DictCodeList.是否_是.getValue());
        return trdProductDeal;
    }

    /**
     * 金融产品交易扩展
     *
     * @param dto 存款类投资交易实体类
     * @return 金融产品交易附属表实体类
     * @see
     * @since 1.0
     */
    public List<PaTrdProductDeal> setPaTrdProductDeal(DepositTransfere001Dto dto, DepositTransfere002Dto depositTransfere002Dto) {
        List<PaTrdProductDeal> paTrdProductDeals = new ArrayList<>();
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        PaTrdProductDeal paTrdProductDealOut = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(depositTransfere002Dto.getTradeId());
        paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_存款类资产买入.getValue());
        paTrdProductDeal.setCreateUser(dto.getCreateUser());
        paTrdProductDeal.setCreateTime(new Date());
        paTrdProductDeal.setUpdateUser(dto.getUpdateUser());
        paTrdProductDeal.setUpdateTime(new Date());
        paTrdProductDeal.setDeptCode(LoginUserUtil.getDeptCode());
        BeanUtil.copyPropertiesIgnoreNull(paTrdProductDealOut, paTrdProductDeal);
        paTrdProductDealOut.setPaBussType(DictCodeListExt1.平安业务类型_存款类资产卖出.getValue());
        paTrdProductDealOut.setTradeId(depositTransfere002Dto.getTradeIdOut());
        paTrdProductDeals.add(paTrdProductDeal);
        paTrdProductDeals.add(paTrdProductDealOut);
        return paTrdProductDeals;
    }

    /**
     * 流程结束数据处理
     *
     * @param entity
     * @see
     * @since 1.0
     */
    public void afterWorkFlow(DepositTransfere001Dto entity, BaseTradeProductDealDto baseTradeProductDealDto) {
        TrdProductDeal trdProductDeal = baseTradeProductDealDto.getTrdProductDeal();
        TrdProductDealAdd trdProductDealAdd = baseTradeProductDealDto.getTrdProductDealAdd();
        String prodRegistCode = this.getProdRegistCode(trdProductDealAdd.getPortfolioId());
//        if (prodRegistCode != null && !prodRegistCode.isEmpty()) {
//            RepTrade repTrade = this.setRepTrade(trdProductDeal, this.getProdRegistCode(trdProductDealAdd.getPortfolioId()));
//            // 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
//            simpleRestTemplate.postForObject(ServiceApplication.REPORT_SERVICE_NAME + "/DepositTransfere001/insertRepTrade",
//                    Arrays.asList(repTrade), Boolean.class);
//        }
//        RepAssetDept repAssetDept = new RepAssetDept();
//        repAssetDept.setRepFinprodId(entity.getFinprodId());
//        repAssetDept.setRepSendStatus(DictCodeList.是否_是.getValue());
//        repAssetDeptMapper.updateByPrimaryKeySelective(repAssetDept);
    }

    /**
     * 获取登记编号
     *
     * @param portfolioId 组合代码
     * @return 登记编号
     * @see
     * @since 1.0
     */
    public String getProdRegistCode(String portfolioId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("portfolioId", portfolioId);
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        return simpleRestTemplate.postForObject(ServiceApplication.REPORT_SERVICE_NAME
                + "/DepositTransfere001/getProdRegistCode", map, String.class);
    }

    /**
     * 设置报送信息
     *
     * @param dto            存款实体类
     * @param prodRegistCode 登记编号
     * @return 报送实体类
     * @see
     * @since 1.0
     */
    public RepTrade setRepTrade(TrdProductDeal dto, String prodRegistCode) {
        RepTrade repTrade = new RepTrade();
        repTrade.setRepFinprodRegId(prodRegistCode);
        repTrade.setRepBankTradeCode(dto.getTradeId());
        if (bussCommonMapperDelegateService != null) {
            FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, dto.getFinprodId(), FinProduct.class);
            if (finProduct != null) {
                repTrade.setRepBankAssetCode(finProduct.getFinprodMarketId());
            }
        } else {
            repTrade.setRepBankAssetCode(dto.getFinprodId());
        }
        repTrade.setRepCcy(dto.getCcy());
        repTrade.setRepOccurMoney(dto.getTradeAmt());
        repTrade.setRepMoneyCny(dto.getTradeAmt().multiply(commonBusinessService.selectClosePrice(dto.getCcy(), DateUtil.format(dto.getTradeDate()))));
        // 一体化对应etliu start
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
            // 一体化对应etliu end
            repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_资产买入.getValue());
        } else {
            repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_资产卖出或到期.getValue());
        }
        repTrade.setRepMoneyFlowTypeExp("无");
        repTrade.setRepCount(dto.getPrinAmt());
        repTrade.setRepTradeDate(dto.getTradeDate());
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
        repTrade.setRepStatusDate(dto.getTradeDate());
        repTrade.setRepRemark(dto.getRemark());
        repTrade.setRepDelCode(dto.getTradeId());
        if (StringUtil.isNotEmpty(repTrade.getRepMoneyCny()) && StringUtil.isNotEmpty(repTrade.getRepCount())) {
//              ArithmeticUtil.di
            BigDecimal temp = ArithmeticUtil.multiply(ArithmeticUtil.divide(repTrade.getRepMoneyCny(), repTrade.getRepCount(), 4), new BigDecimal("100"));
            repTrade.setRepUnitPriceFull(temp);
        }
        repTrade.setRepUnitPriceNet(new BigDecimal("100"));
        return repTrade;
    }

    /**
     * 获取业务编号生成规则参数
     *
     * @param finprodId 金融产品代码
     * @return 业务编号生成规则参数
     * @see
     * @since 1.0
     */
    public BussinessCodeParameter genTradeId(String finprodId) {
        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        FinProduct depoInfo = finProductMapper.selectByPrimaryKey(finprodId);
        bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_存款录入);
        if (DictCodeList.存款类资产类型_同业借款.getValue().equals(depoInfo.getFinprodType())) {
            bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_同业借款);
        } else {
            bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_存放同业);
        }
        return bussinessCodeParameter;
    }

    @Override
    public void doBack(DepositTransfere001Dto entity) {
        if (!StringUtil.isEmptyList(entity.getDepositTransfere002Dto())) {
            entity.getDepositTransfere002Dto().forEach(depositTransfere002Dto -> {
                if (!StringUtil.isEmptyList(depositTransfere002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos())) {
                    depositTransfere002Dto.getFinProductTradeDto().getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {
                        AllTradeCommonUtil.deletePosition("", baseTradeProductDealDto.getTrdProductDeal().getTradeId());
                    });
                }
            });
        }
    }

    /**
     * 查询方法
     *
     * @param params 画面查询参数
     * @return 返回结果
     */
    public Page<TrdDepoDuration001Dto> findPage(Map<String, Object> params) {
        if (params.get("statusValid") != null) {
            params.put("statusValid",
                    StringUtil.splitToList(",", (String) params.get("statusValid")));
        } else {
            params.put("statusValid", new ArrayList<>());
        }
        //多选下拉框处理（合作资产）
        Object chlFinprodIdObj = params.get("chlId");
        if (chlFinprodIdObj != null) {
            params.put("chlIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, (String) chlFinprodIdObj));
        } else {
            params.put("chlIdList", new ArrayList<>());
        }
        params.put("userName", LoginUserUtil.getUserName());
        String cashType = (String) params.get("cashType");
        if (StringUtil.isNotEmpty(cashType)) {
            if (StringUtil.equals(cashType, DictCodeListExt1.平安业务类型_存款提前支取.getValue())) {
                params.put("cashTypeC", "cashTypeC");
            } else if (StringUtil.equals(cashType, DictCodeListExt1.平安业务类型_存款中间付息.getValue())) {
                params.put("cashTypeC", DictCodeList.现金流类型_中间收息.getValue());
            } else {
                params.put("cashTypeC", DictCodeList.现金流类型_到期还本.getValue());
            }
        }
        params.put("userName", LoginUserUtil.getUserName());
        return restTemplate.postForPage(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/findPage", params,
                TrdDepoDuration001Dto.class);
    }

    public List<TrdProductDeal> generateOrdersYQs(DepositTransfere001Dto transferOutDto, List<DepositTransfere002Dto> transferInDtoList) {
        List<TrdProductDeal> trdProductDealList = new ArrayList<>();

        //托管户查询
        String portfolioId = transferOutDto.getPortfolioId();
        List<PtlCapacctInfo> ptlCapacctList = etrTrdCommonMapper.getPtlAcctInfo(portfolioId, Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()));

        for (DepositTransfere002Dto secTransferDto : transferInDtoList) {
            String portfolioIdIn = secTransferDto.getPortfolioId();
            List<PtlCapacctInfo> ptlCapacctListIn = etrTrdCommonMapper.getPtlAcctInfo(portfolioIdIn, Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()));

            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, secTransferDto);
            trdProductDeal.setTradeId(secTransferDto.getTradeId());
            trdProductDeal.setCcy(transferOutDto.getCcy());
            trdProductDeal.setPs(DictCodeList.交易方向_卖出.getValue());
            trdProductDeal.setCounterId(portfolioIdIn);
            trdProductDeal.setFinprodType2("F07");
            trdProductDeal.setIsOutTrade("N");
            trdProductDeal.setTradeAmt(secTransferDto.getTransferTradeAmt());


            if (ptlCapacctList.size() == 1 && ptlCapacctListIn.size() == 1 && ptlCapacctList.get(0).equals(ptlCapacctListIn.get(0))) {
                //※划款指令共通页面调用【业务组件.划款指令共通组件.指令生成】，页面不展示
                trdProductDeal.setStlPayOrderShow("N");
            } else if (ptlCapacctList.size() == 1 && ptlCapacctListIn.size() == 1 && ptlCapacctList.get(0).getAcctNo() != ptlCapacctListIn.get(0).getAcctNo()) {
                //页面展示, 不做任何处理
            } else {
                //页面展示,但是划款账号设置为空
                trdProductDeal.setStlPayOrderShow("Y");
            }
            trdProductDealList.add(trdProductDeal);
        }
        return trdProductDealList;
    }
}
