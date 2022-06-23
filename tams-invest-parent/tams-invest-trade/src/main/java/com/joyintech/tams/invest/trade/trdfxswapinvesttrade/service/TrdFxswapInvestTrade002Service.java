/*
 * 文件名：TrdFxswapInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：renping
 * 修改时间：2019年6月25日 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.trdfxswapinvesttrade.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.calculator.AdjustedTradeDayCal;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;
import com.joyintech.tams.invest.common.valuationinterf.ValuationInterfService;
import com.joyintech.tams.invest.trade.trdfxswapinvesttrade.dto.TrdFxswapInvestTrade001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 外汇掉期新建Service
 * 
 * @author renping
 * @version 1.0 2019年6月25日
 * @see TrdFxswapInvestTrade002Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdFxswapInvestTrade002Service extends CommonExportService<TrdFxswapInvestTrade001Dto> implements BaseProcessService<TrdFxswapInvestTrade001Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdFxswapInvestTrade002Service.class);

    /**
     * 节假日计算用和日期关联的Service
     */
    @Autowired
    private DateCalService dateCalService;

    /**
     * 金融产品交易Service
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * 通用Mapper共同代理方法
     */
    @Autowired
    private  BussCommonMapperDelegateService bussCommonMapperDelegateService;
    /**
     * 划款指令
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候检查service
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 估值接口
     */
    @Autowired
    private ValuationInterfService valuationInterfService;

    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

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
	 * 投管接口共通组件
	 */
	@Autowired
	private InvestInterfCommService investInterfCommService;
	
	@Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;
    
    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。 {@inheritDoc}
     */
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

    /**
     * 交易编号联动带出画面全部信息
     * 
     * @param tradeId 资产代码
     * @return 外汇掉期实体类
     * @see
     * @since 1.0
     */
    public TrdFxswapInvestTrade001Dto getDealInfo(String tradeId, String tradeStatus) {
        return restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdFxswapInvestTrade001/getDealInfo?tradeId="
                                         + tradeId + "&tradeStatus=" + tradeStatus,
            TrdFxswapInvestTrade001Dto.class);
    }

    /**
     * 委托代码联动带出画面必要信息
     * 
     * @param entrustId 资产代码
     * @return 外汇掉期实体类
     * @see
     * @since 1.0
     */
    public TrdFxswapInvestTrade001Dto getDealInfoByEntrustId(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME
                                         + "/TrdFxswapInvestTrade001/getDealInfoByEntrustId?entrustId="
                                         + entrustId,
            TrdFxswapInvestTrade001Dto.class);
    }

    /**
     * 查询交易员
     * 
     * @param entrustId 委托代码
     * @return 交易员
     * @see
     * @since 1.0
     */
    public String getTrader(String entrustId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("entrustId", entrustId);
        map.put("entrustStatus", DictCodeList.委托状态_已接收.getValue());
        return restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdFxswapInvestTrade001/getTrader", map,
            String.class);
    }

    /**
     * 将一笔外汇掉期交易转化成两笔划款指令共通用的交易
     * 
     * @param dto 参数
     * @return 两个币种的两笔交易数据
     * @see
     * @since 1.0
     */
    public List<TrdProductDeal> getEntity(TrdFxswapInvestTrade001Dto dto) {
        List<TrdProductDeal> resultList = new ArrayList<>();
        if (StringUtil.isEmpty(dto.getCurPair()) || StringUtil.isEmpty(dto.getPs())
            || StringUtil.isEmpty(dto.getDealCur()) || StringUtil.isEmpty(dto.getCorCcy())
            || dto.getVdate() == null || dto.getnDealCurAmt() == null) {
            return resultList;
        }
        String[] arr = dto.getCurPair().split("/");
        // 买/卖的场合
        if (StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_买卖.getValue())) {
            if (arr[0].equals(dto.getDealCur())) {
                resultList = setStlDealData(dto, DictCodeList.交易方向_卖出.getValue(),
                    DictCodeList.交易方向_买入.getValue());
            }
            else {
                resultList = setStlDealData(dto, DictCodeList.交易方向_买入.getValue(),
                    DictCodeList.交易方向_卖出.getValue());
            }
        }
        else {
            if (arr[0].equals(dto.getDealCur())) {
                resultList = setStlDealData(dto, DictCodeList.交易方向_买入.getValue(),
                    DictCodeList.交易方向_卖出.getValue());
            }
            else {
                resultList = setStlDealData(dto, DictCodeList.交易方向_卖出.getValue(),
                    DictCodeList.交易方向_买入.getValue());
            }
        }
        return resultList;
    }

    // modified by wangguoxian 外汇即远掉期集中净额清算模式集中账户设置 20190805 start
    /**
     * 将一笔外汇掉期交易转化成两笔划款指令共通用的交易
     * 
     * @param dto 参数
     * @param psDeal 交易货币交易方向
     * @param psCor 对应货币交易方向
     * @return 两个币种的两笔交易数据
     * @see
     * @since 1.0
     */
    private List<TrdProductDeal> setStlDealData(TrdFxswapInvestTrade001Dto dto, String psDeal,
                                                String psCor) {
        List<TrdProductDeal> resultList = new ArrayList<>();
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
        trdProductDeal.setPs(psDeal);
        trdProductDeal.setTradeDate(dto.getVdate());
        trdProductDeal.setTradeAmt(dto.getnDealCurAmt());
        trdProductDeal.setCcy(dto.getDealCur());
        trdProductDeal.setCounterId(dto.getTrdInvestPersonInfo002Dto().getCounterId());
        trdProductDeal.setDeliveryType(dto.getSettMode());
        resultList.add(trdProductDeal);
        trdProductDeal = new TrdProductDeal();
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
        trdProductDeal.setPs(psCor);
        trdProductDeal.setTradeDate(dto.getVdate());
        trdProductDeal.setTradeAmt(dto.getnCorCurAmt());
        trdProductDeal.setCcy(dto.getCorCcy());
        trdProductDeal.setCounterId(dto.getTrdInvestPersonInfo002Dto().getCounterId());
        trdProductDeal.setDeliveryType(dto.getSettMode());
        resultList.add(trdProductDeal);
        // 远端指令生成用交易信息 start
        trdProductDeal = new TrdProductDeal();
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
        trdProductDeal.setPs(psCor);
        trdProductDeal.setTradeDate(dto.getMdate());
        trdProductDeal.setTradeAmt(dto.getfDealCurAmt());
        trdProductDeal.setCcy(dto.getDealCur());
        trdProductDeal.setCounterId(dto.getTrdInvestPersonInfo002Dto().getCounterId());
        trdProductDeal.setDeliveryType(dto.getSettMode());
        resultList.add(trdProductDeal);
        trdProductDeal = new TrdProductDeal();
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
        trdProductDeal.setPs(psDeal);
        trdProductDeal.setTradeDate(dto.getMdate());
        trdProductDeal.setTradeAmt(dto.getfCorCurAmt());
        trdProductDeal.setCcy(dto.getCorCcy());
        trdProductDeal.setCounterId(dto.getTrdInvestPersonInfo002Dto().getCounterId());
        trdProductDeal.setDeliveryType(dto.getSettMode());
        resultList.add(trdProductDeal);
        // 远端指令生成用交易信息 end
        return resultList;
    }
    // modified by wangguoxian 外汇即远掉期集中净额清算模式集中账户设置 20190805 end

    /**
     * 获取可用现金等
     * 
     * @param map 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> available(Map<String, Object> map) {
        // 只有持仓及可用现金计算用的基础项目都录入的情况下才计算
        if (!StringUtil.isNotEmpty(map.get("tradeDate"))) {
            return new ArrayList<SuggestDto>();
        }
        List<SuggestDto> list = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdFxswapInvestTrade001/available", map,
            SuggestDto.class);
        return list;
    }

    /**
     * 获取组合代码
     * 
     * @return 联想文本框
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getPortfolioId(Map<String, Object> map) {
        List<SuggestDto> list = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdFxswapInvestTrade001/getPortfolioIdSug",
            map, SuggestDto.class);
        return list;
    }

    /**
     * 获取交易的划款指令信息
     * 
     * @param tradeId 投资交易实体类
     * @return 划款指令实体类
     * @see
     * @since 1.0
     */
    public List<StlPayOrdersDto> stlInfo(String tradeId) {
        return restTemplate.getForList(ServiceApplication.TRADE_SERVICE_NAME
                                       + "/TrdFxswapInvestTrade001/stlInfo?tradeId=" + tradeId,
            StlPayOrdersDto.class);
    }

    /**
     * 流程中数据处理
     * 
     * @param dto 外汇掉期投资交易实体类
     * @see
     * @since 1.0
     */
    private void insertWorkFlowData(TrdFxswapInvestTrade001Dto dto) {
        if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
            dto.setBussTrdTransferOrder(
                bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
            for (StlPayOrdersDto item : dto.getBussTrdTransferOrder()) {
                StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, item);
                list.add(stlPayOrdersDto);
            }
            dto.setStlPayOrdersList(list);
        }
        if (dto.getOperateType().equals("delete")) {
            FinProductTradeDto finProductTradeDto = dto.getFinProductTradeDto();
            List<BaseTradeProductDealDto> baseDto = finProductTradeDto.getBaseTradeProductDealDtos();
            for (BaseTradeProductDealDto baDto : baseDto) {
                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baDto.getTrdProductDeal());
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflTrdProductDeal.setWorkflowId(dto.getWorkflowId());
                wflTrdProductDeal.setpTradeId(wflTrdProductDeal.getTradeId());
                wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + "R");
                WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
                wflPaTrdProductDeal.setPaBussType(DictCodeListExt2.平安业务类型_外汇掉期起息.getValue());
                wflPaTrdProductDeal.setTradeId(wflTrdProductDeal.getpTradeId());
                wflPaTrdProductDeal.setTradeId(wflPaTrdProductDeal.getTradeId() + "R");
                wflPaTrdProductDeal.setDeptCode(LoginUserUtil.getDeptCode());
                wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflPaTrdProductDeal.setWorkflowId(dto.getWorkflowId());
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd,
                    baDto.getTrdProductDealAdd());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflTrdProductDealAdd.setWorkflowId(dto.getWorkflowId());
                if(StringUtil.isNotEmpty(baDto.getActPortfolioId())) {
                    wflTrdProductDealAdd.setPortfolioId(baDto.getActPortfolioId());
                }
                wflTrdProductDealAdd.setTradeId(wflTrdProductDealAdd.getTradeId() + "R");

                wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
                wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
//                bussCommonMapperDelegateService.insertBatch(WflTrdProductDealMapper.class,
//                    wflTrdProductDeal, WflPaTrdProductDealMapper.class, wflPaTrdProductDeal,
//                    WflTrdProductDealAddMapper.class, wflTrdProductDealAdd);
                if (baDto.getStlPayOrdersList() != null && baDto.getStlPayOrdersList().size() > 0) {
                    for (StlPayOrdersDto stlPayOrdersDto : baDto.getStlPayOrdersList()) {
                        WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                        wflStlPayOrders.setWorkflowId(dto.getWorkflowId());
                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                        wflStlPayOrders.setTradeId(wflTrdProductDeal.getpTradeId() + "R");
                        wflStlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                        if (wflStlPayOrders.getOrderId() != null) {
//                            bussCommonMapperDelegateService.insertBatch(
//                                WflStlPayOrdersMapper.class, wflStlPayOrders);
                            wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                        }
                    }
                }
            }
        }
        else {
            TrdFxswapInvestTrade001Dto entity = this.setEntity(dto);

            // 调用授信共同 start
            TrdProductDeal TrdProductDeal = this.setTrdProductDeal(entity);
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            TrdProductDeal.setCounterId(entity.getTrdInvestPersonInfo002Dto().getCounterId());
            inputDto.setTrdProductDeal(TrdProductDeal);
            // TODO 授信是否需要？
            // AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
            // 调用授信共同 end

            FinProductTradeDto finProductTradeDto = entity.getFinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, this.setTrdProductDeal(entity));
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd,
                this.setTrdProductDealAdd(entity));
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal,
                this.setPaTrdProductDeal(entity));
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            wflPaTrdProductDeal.setPaBussType(DictCodeListExt2.平安业务类型_外汇掉期起息.getValue());
            //add by wangpeng 20190919 流程中 交易方向存入回购交易方向字段
            wflPaTrdProductDeal.setBackLendDirection(entity.getPs());
            //add end 
            WflTrdProductDealOth wflTrdProductDealOth = this.setWflTrdProductDealOth(entity);
            wflTrdProductDealOth.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealOth.setWorkflowId(entity.getWorkflowId());

            wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
            wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
            wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
            wflTrdProductDealOthMapper.insertSelective(wflTrdProductDealOth);
//            bussCommonMapperDelegateService.insertBatch(WflTrdProductDealMapper.class,
//                wflTrdProductDeal, WflTrdProductDealAddMapper.class, wflTrdProductDealAdd,
//                WflPaTrdProductDealMapper.class, wflPaTrdProductDeal,
//                WflTrdProductDealOthMapper.class, wflTrdProductDealOth);
            if (entity.getStlPayOrdersList() != null && entity.getStlPayOrdersList().size() > 0) {
                for (StlPayOrdersDto stlPayOrdersDto : entity.getStlPayOrdersList()) {
                    WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                    wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                    if (wflStlPayOrders.getOrderId() != null) {
//                        bussCommonMapperDelegateService.insertBatch(WflStlPayOrdersMapper.class,
//                            wflStlPayOrders);
                        wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                    }
                }
            }
            entity.setTradeStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
          //设置母组合代码和子组合代码--范双虎
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            baseTradeProductDealCommService.execute(entity.getFinProductTradeDto());
            //子组合插入流水
            if(StringUtil.isNotEmpty(entity.getActPortfolioId()) && !StringUtil.equals(entity.getActPortfolioId(), entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
        }
    }

    /**
     * 作废操作,清空业务表 {@inheritDoc}
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
     * 新建界面提交时插入持仓流水 {@inheritDoc}
     */
    @Override
    public void doInsert(TrdFxswapInvestTrade001Dto entity, OperationType type) {
        if (type.toString().equals("NEW") && entity.getOperateType().equals("submit")) {
            insertWorkFlowData(entity);
        }
        else if (type.toString().equals("DELETE")) {
            List<BaseTradeProductDealDto> baseTradeProductDealDto = restTemplate.getForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdFxswapInvestTrade001/getFinDto?tradeId="
                                                                                            + entity.getTradeId(),
                BaseTradeProductDealDto.class);
            FinProductTradeDto dto = new FinProductTradeDto();
            entity.setFinProductTradeDto(dto);
            entity.getFinProductTradeDto().setBaseTradeProductDealDtos(baseTradeProductDealDto);
            entity.setOperateType("delete");
            insertWorkFlowData(entity);
        }
    }

    /**
     * 保存中进行提交时进行性插入持仓流水 {@inheritDoc}
     */
    @Override
    public void doUpdate(TrdFxswapInvestTrade001Dto entity) {
        if (CollectionUtils.isNotEmpty(entity.getFileArray())
            && StringUtil.isNotEmpty(entity.getTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                entity.getFileArray().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                entity.getTradeId());
        }
        if (entity.getOperateType().equals("submit")) {
            doDelete(entity.getWorkflowId());
            insertWorkFlowData(entity);
        }
    }

    /**
     * 流程 {@inheritDoc}
     */
    @Override
    public void doComplete(TrdFxswapInvestTrade001Dto entity, OperationType type) {
        //设置母组合代码和子组合代码--范双虎
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));

        TrdFxswapInvestTrade001Dto dto = new TrdFxswapInvestTrade001Dto();
        FinTradeProduct finTradeProduct = null;
        TrdFxswapInvestTrade001Dto entity2 = null;
        if (!type.toString().equals("DELETE")) {
            dto = this.setEntity(entity);
            finTradeProduct = this.setFinTradeProduct(entity);
            entity.setTrdProductDeal(dto.getFinProductTradeDto().getBaseTradeProductDealDtos().get(
                0).getTrdProductDeal());
            entity.setTrdProductDealAdd(
                dto.getFinProductTradeDto().getBaseTradeProductDealDtos().get(
                    0).getTrdProductDealAdd());
            entity.setFinTradeProduct(finTradeProduct);

            // 到期交易entity
            entity2 = restTemplate.postForObject(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdExchangeDuration/doCreateEndDeal",
                entity, TrdFxswapInvestTrade001Dto.class);
            entity2.getPtlTrade().setPortfolioId(entity.getActPortfolioId());
            entity2.setActPortfolioId(entity.getActPortfolioId());
            entity2.setPortfolioId(entity.getPortfolioId());
        }

        if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
            entity.setBussTrdTransferOrder(
                bussTrdTransferOrderService.save(entity.getBussTrdTransferOrder()));
            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
            List<StlPayOrdersDto> list2 = new ArrayList<StlPayOrdersDto>();
            if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
                for (StlPayOrdersDto item : entity.getBussTrdTransferOrder()) {
                    StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
                    BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, item);
                    if (stlPayOrdersDto.getSettleDate().compareTo(entity.getVdate()) == 0) {
                        list.add(stlPayOrdersDto);
                        stlPayOrdersDto.setTradeId(entity.getTradeId());
                    }
                    else if (entity2 != null) {
                        list2.add(stlPayOrdersDto);
                        stlPayOrdersDto.setTradeId(entity2.getTrdProductDeal().getTradeId());
                    }
                }
            }
            entity.setStlPayOrdersList(list);
            if (entity2 != null) {
                entity2.setStlPayOrdersList(list2);
            }
        }
        // jixu 接口 start
        List<StlPayOrders> stlPayOrdersList = new ArrayList<StlPayOrders>();
        if(entity.getStlPayOrdersList()!=null) {
            stlPayOrdersList.addAll(entity.getStlPayOrdersList());
        }
        TrdCommDto trdCommDto = new TrdCommDto();
        // jixu 接口 end
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        TrdTradeFeeDetail trdTradeFeeDetail = null;
        if (type.toString().equals("DELETE")) {
        	finTradeProduct = this.setFinTradeProduct(entity);
            List<BaseTradeProductDealDto> baseTradeProductDealDto = restTemplate.getForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdFxswapInvestTrade001/getFinDto?tradeId="
                                                                                            + entity.getTradeId(),
                BaseTradeProductDealDto.class);
            for (BaseTradeProductDealDto dts : baseTradeProductDealDto) {
                dts.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                dts.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                dts.getTrdProductDeal().setCancelRemark(entity.getCancelRemark());
                if (bussCommonMapperDelegateService != null) {
                    PaTrdProductDeal paTrdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                        PaTrdProductDealMapper.class, dts.getTrdProductDeal().getTradeId(),
                        PaTrdProductDeal.class);
                    paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId() + "R");
                    paTrdProductDealMapper.insertSelective(paTrdProductDeal);
                }
                
                //删除授信占用
                if (null != dts.getTrdProductDeal().getTradeId() && !"".equals(dts.getTrdProductDeal().getTradeId())){
                    riskCreditListCommonValidatorService.deleteCredit(dts.getTrdProductDeal().getTradeId());
                }
            }
            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId() + "R");
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDto);
            entity.setFinProductTradeDto(finProductTradeDto);
            // jixu接口
            trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_DELETE);
        }
        else {
            dto.getFinProductTradeDto().getBaseTradeProductDealDtos().get(0).setStlPayOrdersList(
                entity.getStlPayOrdersList());
            dto.getFinProductTradeDto().setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());

            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());
            finProductTradeDto = entity.getFinProductTradeDto();
            // 金融产品分类
            FinProductType finProductType = entity.getFinProductType();
            // add by wangpeng 获取估值分类123 start
            ValItemValueGenUtil.genTypeForFinProductType(this.setFinTradeProduct(entity), finProductType);
            // add by wangpeng end

            finTradeProductMapper.insertSelective(finTradeProduct);
            finProductTypeMapper.insertSelective(finProductType);
            if (entity.getFeeAmt() != null && BigDecimal.ZERO.compareTo(entity.getFeeAmt()) < 0) {
                // 交易费用明细
                trdTradeFeeDetail = setTrdTradeFeeDetail(dto);
                trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
            }

            // 到期交易生成 start
            BaseTradeProductDealDto baseDto = new BaseTradeProductDealDto();
            if (entity2 != null){
                baseDto.setTrdProductDealAdd(entity2.getTrdProductDealAdd());
                baseDto.setTrdProductDeal(entity2.getTrdProductDeal());
                baseDto.setStlPayOrdersList(entity2.getStlPayOrdersList());
                List<PtlTrade> ptlTradeList2 = new ArrayList<PtlTrade>();
                ptlTradeList2.add(entity2.getPtlTrade());

                String pPortfolioId = AllTradeCommonUtil.exchangePortfolio(entity2.getPtlTrade().getPortfolioId());
                if(StringUtil.isNotEmpty(pPortfolioId) && !pPortfolioId.equals(entity.getPtlTrade().getPortfolioId())){
                    PtlTrade ptlTrade2 = new PtlTrade();
                    BeanUtil.copyPropertiesIgnoreNull(ptlTrade2, entity2.getPtlTrade());
                    ptlTrade2.setPortfolioId(pPortfolioId);
                    ptlTradeList2.add(ptlTrade2);
                }
                baseDto.setPtlTrade(ptlTradeList2);
            }
            finProductTradeDto.getBaseTradeProductDealDtos().add(baseDto);
            // 到期交易生成end
          // jixu接口
          trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_ADD);
        }

        // 撤销时，子组合需要调估值共通
        if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                finProductTrade.setActPortfolioId(entity.getActPortfolioId());
            });
        }
        baseTradeProductDealCommService.execute(finProductTradeDto);
        //子组合插入流水
        if(StringUtil.isNotEmpty(entity.getActPortfolioId()) && !StringUtil.equals(entity.getActPortfolioId(), entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
        }
        dto.setOperateType(type.toString());
        restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdFxswapInvestTrade001/doComplete", dto,
            Integer.class);
        if (entity2 != null) {
            entity2.setOperateType(type.toString());
            restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdFxswapInvestTrade001/doComplete",
                entity2, Integer.class);
        }
        baseTradeProductDealCommService.deleteWflData(entity.getWorkflowId());

//        // jixu接口 start
//        if (finProductTradeDto.getBaseTradeProductDealDtos() != null) {
//            BaseTradeProductDealDto baseTradeProductDealDto = finProductTradeDto.getBaseTradeProductDealDtos().get(
//                0);
//            trdCommDto.setFinTradeProduct(this.setFinTradeProduct(entity));
//            trdCommDto.setStlPayOrdersList(stlPayOrdersList);
//            trdCommDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
//            if (StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDealAdd())
//                && StringUtil.isNotEmpty(entity.getPortfolioId())
//                && !StringUtil.equals(entity.getPortfolioId(),
//                    baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())) {
//                baseTradeProductDealDto.getTrdProductDealAdd().setPortfolioId(
//                    entity.getPortfolioId());
//            }
//            trdCommDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
//            trdCommDto.setPtlTradeList(baseTradeProductDealDto.getPtlTrade());
//            if (baseTradeProductDealDto.getTrdProductDeal() != null
//                && StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getPs(),
//                    DictCodeList.交易方向_买入.getValue())) {
//                valuationInterfService.runValInvest2Jixu(trdCommDto);
//            }
//        }
//        // jixu接口 end
        // 一体化对应 start
        // 所有交易生效后调用的共通方法
        if (!type.toString().equals("DELETE")) {
            for (BaseTradeProductDealDto baseDto : finProductTradeDto.getBaseTradeProductDealDtos()) {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(entity.getPortfolioId());
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                inputDto.setTrdProductDeal(baseDto.getTrdProductDeal());

                // 交易费用
                inputDto.setTrdProductDealAdd(baseDto.getTrdProductDealAdd());
                inputDto.setTrdTradeFeeDetail(trdTradeFeeDetail);

//                List<PtlTrade> ptlTrades = new ArrayList<>();
//                ptlTrades.add(entity.getPtlTrade());
//
//                String actPortfolioId = AllTradeCommonUtil.exchangePortfolio(entity.getPtlTrade().getPortfolioId());
//                if (StringUtil.isNotEmpty(actPortfolioId) && !actPortfolioId.equals(entity.getPtlTrade().getPortfolioId())) {
//                    PtlTrade ptlTrade = new PtlTrade();
//                    BeanUtil.copyPropertiesIgnoreNull(ptlTrade, entity.getPtlTrade());
//                    ptlTrade.setPortfolioId(actPortfolioId);
//                    ptlTrades.add(ptlTrade);
//                }

                // 反向交易增加 start
                AllTradeCommonUtil.setDeals(inputDto, baseDto.getTrdProductDeal(),
                    baseDto.getTrdProductDealAdd(), baseDto.getPaTrdProductDeal(),
                    baseDto.getStlPayOrdersList(), baseDto.getPtlTrade(), entity.getActPortfolioId());
                // 反向交易增加 end

                if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    inputDto.setActPortfolioId(entity.getActPortfolioId());
                }
                inputDto.setPtlTradeList(baseDto.getPtlTrade());
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            }
        }
        // 一体化对应 end
        //        调用接口  add by zhangpuyu 2019/12/6 start
        try {
            List<String> tradeIds = new ArrayList<>();
            tradeIds.add(entity.getTradeId());
            restTemplate.postForObject(
                    ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
                    Map.class);
        }catch (Exception e){

        }
        //        调用接口  add by zhangpuyu 2019/12/6 end
        // 调用投管接口 add by wqd 20190212 start
        if(entity != null ) {
        	InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
    		investInterfCommDto.setPageId("TrdFxswapInvestTrade002.01");
    		if(!type.toString().equals("DELETE")) {
    			investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
    		}else {
    			investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
    		}
    		investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
    		investInterfCommDto.setTrdProductDeal(trdCommDto.getTrdProductDeal());
    		investInterfCommDto.setTrdProductDealAdd(trdCommDto.getTrdProductDealAdd());
    		investInterfCommDto.setStlPayOrdersList(stlPayOrdersList);
			investInterfCommDto.setTradeFeeDetaillist(trdCommDto.getTradeFeeDetaillist());
			investInterfCommDto.setFinTradeProduct(trdCommDto.getFinTradeProduct());
			if (finTradeProduct != null){
                investInterfCommDto.setOtherTypeForConvert(finTradeProduct.getExcPs());
            }
    		investInterfCommService.doInvestInterf(investInterfCommDto);
        }
        if(entity2 != null ) {
        	InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
    		investInterfCommDto.setPageId("TrdFxswapInvestTrade002.02");
    		if(!type.toString().equals("DELETE")) {
    			investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
    		}else {
    			investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
    		}
    		investInterfCommDto.setPortfolioId(entity2.getActPortfolioId());
    		investInterfCommDto.setTrdProductDeal(trdCommDto.getTrdProductDeal());
    		investInterfCommDto.setTrdProductDealAdd(trdCommDto.getTrdProductDealAdd());
    		investInterfCommDto.setStlPayOrdersList(stlPayOrdersList);
            investInterfCommDto.setOtherTypeForConvert(entity.getFinTradeProduct()==null?"":entity.getFinTradeProduct().getExcPs());
            investInterfCommService.doInvestInterf(investInterfCommDto);
        }
        
    }

    /**
     * 合成各实体类
     * 
     * @param dto 外汇掉期投资交易实体类
     * @return 外汇掉期投资交易实体类
     * @see
     * @since 1.0
     */
    public TrdFxswapInvestTrade001Dto setEntity(TrdFxswapInvestTrade001Dto dto) {
        List<BaseTradeProductDealDto> list = new ArrayList<BaseTradeProductDealDto>();
        FinProductTradeDto finDto = new FinProductTradeDto();
        BaseTradeProductDealDto baseDto = new BaseTradeProductDealDto();
        baseDto.setTrdProductDealAdd(this.setTrdProductDealAdd(dto));
        baseDto.setTrdProductDeal(this.setTrdProductDeal(dto));
        if (dto.getStlPayOrdersList() != null) {
            for (StlPayOrders sDto : dto.getStlPayOrdersList()) {
                sDto.setTradeId(dto.getTradeId());
            }
        }
        baseDto.setStlPayOrdersList(dto.getStlPayOrdersList());
        list.add(baseDto);
        finDto.setBaseTradeProductDealDtos(list);
        // 组合表
        dto.setPtlTrade(this.setPtlTrade(dto));
        List<PtlTrade> ptlTradeList = new ArrayList<>();
        ptlTradeList.add(dto.getPtlTrade());
        baseDto.setPtlTrade(ptlTradeList);
        String pPortfolioId = AllTradeCommonUtil.exchangePortfolio(dto.getPtlTrade().getPortfolioId());
        if(StringUtil.isNotEmpty(pPortfolioId) && !pPortfolioId.equals(dto.getPtlTrade().getPortfolioId())){
            PtlTrade ptlTrade2 = new PtlTrade();
            BeanUtil.copyPropertiesIgnoreNull(ptlTrade2, dto.getPtlTrade());
            ptlTrade2.setPortfolioId(pPortfolioId);
            baseDto.getPtlTrade().add(ptlTrade2);
        }
        // 金融产品交易扩展
        dto.setPaTrdProductDeal(this.setPaTrdProductDeal(dto));
        dto.setFinProductTradeDto(finDto);
        // 交易产品分类表Entity 构造开始----------------------------------------------------------
        FinProductType finProductType = new FinProductType();
        // 金融产品代码 交易编号、
        finProductType.setFinprodId(dto.getTradeId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finProductType.setBranch(Long.parseLong((String) CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        // 分类1
        finProductType.setType8(DictCodeListExt2.平安资产类型_外汇掉期.getValue());
        /*添加五级分类映射 lishuping 2019/08/13 start*/
        FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
        finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_无标的投管资产类型映射行内资产分类.getValue());
        finProductTypeConfig.setInputValue1(DictCodeListExt2.平安资产类型_外汇掉期.getValue());
        finProductTypeConfig = restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
                finProductTypeConfig, FinProductTypeConfig.class);
        finProductType.setType9(finProductTypeConfig.getOutputValue2());
        finProductType.setType10(finProductTypeConfig.getOutputValue3());
        finProductType.setType11(finProductTypeConfig.getOutputValue4());
        finProductType.setType12(finProductTypeConfig.getOutputValue5());
        /*添加五级分类映射 lishuping 2019/08/13 end*/

        dto.setFinProductType(finProductType);
        // 交易产品分类表Entity 构造结束----------------------------------------------------------
        return dto;
    }

    /**
     * 生成金融产品交易表
     * 
     * @param dto 外汇掉期投资交易实体类
     * @return 金融产品交易实体类
     * @see
     * @since 1.0
     */
    public TrdProductDeal setTrdProductDeal(TrdFxswapInvestTrade001Dto dto) {
        TrdProductDeal trdDto = new TrdProductDeal();
        trdDto.setTradeId(dto.getTradeId());// 塞入交易编号
        trdDto.setBusiType(DictCodeListExt2.共通业务类型_外汇掉期投资交易.getValue());// 设置业务类型
        trdDto.setFinprodId(dto.getTradeId());
        trdDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
        trdDto.setFinprodType(DictCodeListExt1.金融产品类型_掉期.getValue());
        trdDto.setBranch(
            Long.valueOf(CacheUtil.getSysParam("noBranchNoValue"))/* Long.valueOf(0) */);
        trdDto.setUnitCprice(dto.getSpotRate());
        trdDto.setUnitInt(dto.getnPoint());
        trdDto.setUnitFprice(dto.getnFprice());
        trdDto.setCcy(dto.getCorCcy());
        trdDto.setPs(DictCodeList.交易方向_买入.getValue());
        trdDto.setTradeDate(dto.getTradeDate());
        trdDto.setSettleDate(dto.getVdate());
        trdDto.setPrinAmt(dto.getnDealCurAmt());
        trdDto.setFpriceAmt(dto.getnCorCurAmt());
        trdDto.setTradeAmt(dto.getnCorCurAmt());
        trdDto.setFeeAmt(dto.getFeeAmt());
        trdDto.setFeeAmtUnpay(dto.getFeeAmt());
        trdDto.setDeliveryType(dto.getSettMode());
        trdDto.setIsClean(DictCodeList.是否_是.getValue());
        trdDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdDto.setRemark(dto.getRemark());
        trdDto.setCounterId(dto.getTrdInvestPersonInfo002Dto().getCounterId());
        trdDto.setTrader(dto.getTrdInvestPersonInfo002Dto().getTrader());
        trdDto.setCounterContact(dto.getTrdInvestPersonInfo002Dto().getContactInfo());
        trdDto.setCounterTrader(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
        trdDto.setIsOutTrade(dto.getTrdInvestPersonInfo002Dto().getIsOutTrade());
        trdDto.setIsCancel(DictCodeList.是否_否.getValue());
        trdDto.setParValue(BigDecimal.ONE);;
        return trdDto;
    }

    /**
     * 生成金融产品交易附属表
     * 
     * @param dto 外汇掉期投资交易实体类
     * @return 金融产品附属类
     * @see
     * @since 1.0
     */
    public TrdProductDealAdd setTrdProductDealAdd(TrdFxswapInvestTrade001Dto dto) {
        TrdProductDealAdd addDto = new TrdProductDealAdd();
        addDto.setTradeId(dto.getTradeId());
        addDto.setPortfolioId(dto.getPortfolioId());
        addDto.setEntrustId(dto.getEntrustId());
        addDto.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        addDto.setExcDealType(dto.getExcDealType());
        addDto.setCurPair(dto.getCurPair());
        addDto.setUsdAmt(dto.getnUsdAmt());
        addDto.setTermType(dto.getnTermTypeExc());
        addDto.setOurRole(dto.getOurRole());
        addDto.setDealMode(dto.getDealMode());
        return addDto;
    }

    /**
     * 交易费用明细表
     * 
     * @param dto 外汇掉期投资交易实体类
     * @return 交易费用明细表
     * @see
     * @since 1.0
     */
    public TrdTradeFeeDetail setTrdTradeFeeDetail(TrdFxswapInvestTrade001Dto dto) {
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        trdTradeFeeDetail.setTradeId(dto.getTradeId());;
        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_交易手续费.getValue());
        trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
        trdTradeFeeDetail.setCcy(DictCodeListExt2.外汇交易类型_人民币外汇交易.getValue().equals(
            dto.getExcDealType()) ? DictCodeList.币种_人民币_CNY.getValue() : dto.getDealCur());
        trdTradeFeeDetail.setFeeAmt(dto.getFeeAmt());
        return trdTradeFeeDetail;
    }

    /**
     * 组合交易表
     * 
     * @param dto 外汇掉期投资交易实体类
     * @return 组合交易表
     * @see
     * @since 1.0
     */
    public PtlTrade setPtlTrade(TrdFxswapInvestTrade001Dto dto) {
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(StringUtil.isNotEmpty(dto.getActPortfolioId()) ? dto.getActPortfolioId() : dto.getPortfolioId());
        ptlTrade.setBusiNo(dto.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(dto.getTradeId());
        ptlTrade.setBusiType(DictCodeListExt2.共通业务类型_外汇掉期投资交易.getValue());
        ptlTrade.setSettleDate(dto.getTradeDate());
        return ptlTrade;
    }

    /**
     * 金融产品交易扩展
     * 
     * @param dto 外汇掉期投资交易实体类
     * @return 金融产品交易实体类
     * @see
     * @since 1.0
     */
    public PaTrdProductDeal setPaTrdProductDeal(TrdFxswapInvestTrade001Dto dto) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(dto.getTradeId());
        paTrdProductDeal.setPaBussType(DictCodeListExt2.平安业务类型_外汇掉期起息.getValue());
        paTrdProductDeal.setDeptCode(LoginUserUtil.getDeptCode());
        return paTrdProductDeal;
    }

    /**
     * 交易类金融产品表
     * 
     * @param dto 外汇掉期投资交易实体类
     * @return 交易类金融产品表
     * @see
     * @since 1.0
     */
    public FinTradeProduct setFinTradeProduct(TrdFxswapInvestTrade001Dto dto) {
        FinTradeProduct finTradeProduct = new FinTradeProduct();
        finTradeProduct.setFinprodId(dto.getTradeId());;
        finTradeProduct.setBranch(
            Long.valueOf(CacheUtil.getSysParam("noBranchNoValue"))/* Long.valueOf(0) */);
        finTradeProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
        finTradeProduct.setPrin(dto.getfDealCurAmt());
        finTradeProduct.setCcy(dto.getDealCur());
        finTradeProduct.setVdate(dto.getTradeDate());
        finTradeProduct.setMdate(dto.getMdate());
        finTradeProduct.setmPrinAmt(dto.getfDealCurAmt());
        finTradeProduct.setmTradeAmt(dto.getfCorCurAmt());
        finTradeProduct.setCalendarId(dto.getCalendarId());
        finTradeProduct.setTermType(dto.getfTermTypeExc());
        finTradeProduct.setCounterId(dto.getTrdInvestPersonInfo002Dto().getCounterId());
        finTradeProduct.setmUnitCprice(dto.getSpotRate());
        finTradeProduct.setmUnitInt(dto.getfPoint());
        finTradeProduct.setmUnitFprice(dto.getfFprice());
        finTradeProduct.setExcPs(dto.getPs());
        finTradeProduct.setCurPair(dto.getCurPair());
        finTradeProduct.setExcFxsTermType(dto.getExcFxsTermType());
        finTradeProduct.setUsdAmt(dto.getfUsdAmt());
        // 持有目的 中债新增 20200409
 		finTradeProduct.setHoldPurpose(dto.getHoldPurpose());
        return finTradeProduct;
    }

    /**
     * 金融产品交易业务流程其他信息表
     * 
     * @param dto 外汇掉期投资交易实体类
     * @return 金融产品交易业务流程其他信息表
     * @see
     * @since 1.0
     */
    public WflTrdProductDealOth setWflTrdProductDealOth(TrdFxswapInvestTrade001Dto dto) {
        WflTrdProductDealOth wflTrdProductDealOth = new WflTrdProductDealOth();
        wflTrdProductDealOth.setTradeId(dto.getTradeId());
        wflTrdProductDealOth.setExcPs(dto.getPs());
        wflTrdProductDealOth.setExcFxsTermType(dto.getExcFxsTermType());
        wflTrdProductDealOth.setTermType(dto.getfTermTypeExc());
        wflTrdProductDealOth.setfMdate(dto.getMdate());
        wflTrdProductDealOth.setFwdPoint(dto.getfPoint());
        wflTrdProductDealOth.setFwdFprice(dto.getfFprice());
        wflTrdProductDealOth.setfDealAmt(dto.getfDealCurAmt());
        wflTrdProductDealOth.setfCorAmt(dto.getfCorCurAmt());
        wflTrdProductDealOth.setfUsdAmt(dto.getfUsdAmt());
        wflTrdProductDealOth.setDealCur(dto.getDealCur());
        return wflTrdProductDealOth;
    }

    /**
     * 交易费用计算
     * 
     * @param dto 参数
     * @return 费用金额
     * @see
     * @since 1.0
     */
    public BigDecimal getFeeAmt(TrdFxswapInvestTrade001Dto dto) {
        BigDecimal feeAmt = null;
        boolean over1WeekFlag = false;
        // 交易日历取得
        List<String> dealCalendarHolidayList = dateCalService.getDealCalendarHoliday(
            dto.getTradeDate(), DateUtil.addDay(dto.getTradeDate(), 30), dto.getCalendarId());
        // T+2日调整后
        String vdate = AdjustedTradeDayCal.getAdjustedTradeDay(
            DateUtil.format(DateUtil.addDay(dto.getTradeDate(), 2)),
            DictCodeList.营业日准则_向后调整.getValue(), dealCalendarHolidayList);
        // T+2+7日调整后
        vdate = AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.addDay(vdate, 7),
            DictCodeList.营业日准则_向后调整.getValue(), dealCalendarHolidayList);
        if (DateUtil.parse(vdate).before(dto.getMdate())) {
            over1WeekFlag = true;
        }
        if (StringUtil.equals(DictCodeListExt2.外汇交易类型_人民币外汇交易.getValue(), dto.getExcDealType())) {
            // 交易货币为人民币时
            if (StringUtil.equals(DictCodeList.币种_人民币_CNY.getValue(), dto.getDealCur())) {
                // 远端交割日在1W对应的交割日之后的（不含），交易费用=人民币交易金额*0. 1/10000
                if (over1WeekFlag) {
                    feeAmt = ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(dto.getnDealCurAmt(), BigDecimal.valueOf(0.1)), 10000);
                }
                // 远端交割日在1W对应的交割日之前的（含），交易费用=人民币交易金额*0.01/10000
                else {
                    feeAmt = ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(dto.getnDealCurAmt(), BigDecimal.valueOf(0.01)),
                        10000);
                }
            }
            // 交易货币为外币时，对应货币为人民币
            else {
                // 远端交割日在1W对应的交割日之后的（不含），交易费用=人民币交易金额*0. 1/10000
                if (over1WeekFlag) {
                    feeAmt = ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(dto.getnCorCurAmt(), BigDecimal.valueOf(0.1)), 10000);
                }
                // 远端交割日在1W对应的交割日之前的（含），交易费用=人民币交易金额*0.01/10000
                else {
                    feeAmt = ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(dto.getnCorCurAmt(), BigDecimal.valueOf(0.01)), 10000);
                }
            }
        }
        // 外币对交易
        else {
            // 远端交割日在1W对应的交割日之后的（不含），交易费用=交易货币金额*0. 05/10000
            if (over1WeekFlag) {
                feeAmt = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(dto.getnDealCurAmt(), BigDecimal.valueOf(0.05)), 10000);
            }
            // 远端交割日在1W对应的交割日之前的（含），交易费用=交易货币金额*0.005/10000；
            else {
                feeAmt = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(dto.getnDealCurAmt(), BigDecimal.valueOf(0.005)), 10000);
            }
        }
        return feeAmt;
    }

    /** 获取业务参数
     * @param entity 业务实体
     * @param taskId 任务代码
     * @return 参数
     * @throws WorkFlowException 工作流异常
     */
    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        TrdFxswapInvestTrade001Dto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {
                BigDecimal prinAmt = null;
                if (dto.getCurPair().contains(DictCodeList.币种_人民币_CNY.getValue())) {
                    if (StringUtil.equals(dto.getDealCur(), DictCodeList.币种_人民币_CNY.getValue())) {
                        prinAmt = dto.getnDealCurAmt();
                    } else {
                        prinAmt = dto.getnCorCurAmt();
                    }
                } else {
                    CommonBusinessService commonBusinessService = SpringContextUtil
                            .getBean(CommonBusinessService.class);
                    if (commonBusinessService != null) {
                        prinAmt = commonBusinessService.calExchangeAmt(dto.getnDealCurAmt(),
                                DateUtil.addDay(dto.getTradeDate(), -1), dto.getDealCur(),
                                DictCodeList.币种_人民币_CNY.getValue());
                    }
                }
//              return commonBusinessService.是否需要上级审批(dto.getCounterId(), prinAmt, LoginUserUtil.getUserName(),
//              DictCodeListExt2.平安资产类型_外汇掉期.getValue(), dto.getFinprodType(), dto.getTradeMarket(), dto.getPs()
//              // 资产类型改为授权类型 add by renping 2019-08-06  start
//              , null
//              // 资产类型改为授权类型 add by renping 2019-08-06  end
//              );
      Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), prinAmt,LoginUserUtil.getUserName(),
          DictCodeListExt2.平安资产类型_外汇掉期.getValue(), dto.getFinprodType(), dto.getTradeMarket(), dto.getPs()
          , null,entity.getWorkflowId());
      params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
      params.put("prinAmt", dto.getPrinAmt());
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
    }
    /*添加交割日与期限联动规则 gaopeng 2019/10/08 start*/
    /**
     * 推算远端交割日
     * @return 远端交割日
     */
	public Map<String, Object> getmdate(Map<String, Object> map) {
		CommonBusinessService commonBusinessService = SpringContextUtil
                .getBean(CommonBusinessService.class);
		String termType = (String)map.get("fTermTypeExc");
        String tradeDateString = (String)map.get("tradeDate");
        //获取当月最后一天
        LocalDate day = DateUtil.parseLocalDate(tradeDateString);
        String lastDay =day.withDayOfMonth(day.lengthOfMonth()).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String calendarId = (String)map.get("calendarId");
        String mdate = "";
        if(DictCodeListExt2.外汇期限_TODAY.getValue().equals(termType)) {
        	mdate=tradeDateString;
        	map.put("mdate", mdate);
        }else if(DictCodeListExt2.外汇期限_TOM.getValue().equals(termType)) {
        	map.put("tradeDate", tradeDateString);
        	map.put("days",1);
        	map.put("flag","1");
            map.put("mdate", this.getWorkDay(map));
        }else if(DictCodeListExt2.外汇期限_SPOT.getValue().equals(termType)) {
        	map.put("tradeDate", tradeDateString);
        	map.put("days",2);
        	map.put("flag","1");
            map.put("mdate", this.getWorkDay(map));
        }else {
        	String term = commonBusinessService.getTerm(termType);
        	if(StringUtil.isNotEmpty(tradeDateString) && StringUtil.isNotEmpty(term)){
        		//获取增加俩个工作日后的日期
        		map.put("tradeDate", tradeDateString);
        		map.put("days",2);
        		map.put("flag","1");
        		String addDay = this.getWorkDay(map);
        		//增加俩个工作日后增加期限
        		Date tradeDate =DateUtil.addDay(DateUtil.parse(addDay), Integer.parseInt(term));
                if(StringUtil.isNotEmpty(calendarId) && addDay != null){
                	//若期限大于1M
                    if(Integer.parseInt(term)>=30) {
                    	//判断交易日期+2是否为当月最后一个交易日
                    	map.put("lastDay", lastDay);
                    	try {
    						String lastWorkDay = commonBusinessService.getNextLastWorkDay(map);
    						if(addDay.equals(lastWorkDay)) {
    							String theDay = DateUtil.format(tradeDate);
    							//获取对应月的最后一天
    							LocalDate theLastday = DateUtil.parseLocalDate(theDay);
    							  String theMouthLastday =theLastday.withDayOfMonth(theLastday.lengthOfMonth()).format(DateTimeFormatter.ISO_LOCAL_DATE);
    					        map.put("lastDay", theMouthLastday);
    					      //获取对应月的最后一个工作日
    					        mdate =  commonBusinessService.getNextLastWorkDay(map);
    						}else {
    	                    	map.put("tradeDate", DateUtil.format(tradeDate));
    	                    	map.put("flag","2");
    	                    	mdate = this.getNextWorkDay(map);
    	                    }
    					} catch (Exception e) {
                            logger.error(e.getMessage(),e);
    					}
                    }else {
                    	map.put("tradeDate", DateUtil.format(tradeDate));
                    	map.put("flag","1");
                    	mdate = this.getNextWorkDay(map);
                    }
                }
                map.put("mdate", mdate);
            }
        }
        return map;
	}
	
	/**
     * 推算近端交割日
     * @return 近端交割日
     */
	public Map<String, Object> getvdate(Map<String, Object> map) {
		CommonBusinessService commonBusinessService = SpringContextUtil
                .getBean(CommonBusinessService.class);
		String termType = (String)map.get("nTermTypeExc");
        String tradeDateString = (String)map.get("tradeDate");
        //获取当月最后一天
        LocalDate day = DateUtil.parseLocalDate(tradeDateString);
        String lastDay =day.withDayOfMonth(day.lengthOfMonth()).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String calendarId = (String)map.get("calendarId");
        String vdate = "";
        if(DictCodeListExt2.外汇期限_TODAY.getValue().equals(termType)) {
        	vdate=tradeDateString;
        	map.put("vdate", vdate);
        }else if(DictCodeListExt2.外汇期限_TOM.getValue().equals(termType)) {
             map.put("tradeDate", tradeDateString);
             map.put("days",1);
             map.put("flag","1");
             map.put("vdate", this.getWorkDay(map));
        }else if(DictCodeListExt2.外汇期限_SPOT.getValue().equals(termType)) {
        	 map.put("tradeDate", tradeDateString);
             map.put("days",2);
             map.put("flag","1");
             map.put("vdate", this.getWorkDay(map));
        }else {
        	String term = commonBusinessService.getTerm(termType);
        	if(StringUtil.isNotEmpty(tradeDateString) && StringUtil.isNotEmpty(term)){
        		//获取增加俩个工作日后的日期
        		map.put("tradeDate", tradeDateString);
        		map.put("days",2);
        		map.put("flag","1");
        		String addDay = this.getWorkDay(map);
        		//增加俩个工作日后增加期限
        		Date tradeDate =DateUtil.addDay(DateUtil.parse(addDay), Integer.parseInt(term));
                if(StringUtil.isNotEmpty(calendarId) && addDay != null){
                	//若期限大于1M
                    if(Integer.parseInt(term)>=30) {
                    	//判断交易日期+2是否为当月最后一个交易日
                    	map.put("lastDay", lastDay);
                    	try {
    						String lastWorkDay = commonBusinessService.getNextLastWorkDay(map);
    						if(addDay.equals(lastWorkDay)) {
    							String theDay = DateUtil.format(tradeDate);
    							//获取对应月的最后一天
    							LocalDate theLastday = DateUtil.parseLocalDate(theDay);
    					        String theMouthLastday =theLastday.withDayOfMonth(theLastday.lengthOfMonth()).format(DateTimeFormatter.ISO_LOCAL_DATE);
    					        map.put("lastDay", theMouthLastday);
    					      //获取对应月的最后一个工作日
    					        vdate =  commonBusinessService.getNextLastWorkDay(map);
    						}else {
    	                    	map.put("tradeDate", DateUtil.format(tradeDate));
    	                    	map.put("flag","2");
    	                    	vdate = this.getNextWorkDay(map);
    	                    }
    					} catch (Exception e) {
                            logger.error(e.getMessage(),e);
    					}
                    }else {
                    	map.put("tradeDate", DateUtil.format(tradeDate));
                    	map.put("flag","1");
                    	vdate = this.getNextWorkDay(map);
                    }
                }
                map.put("vdate", vdate);
            }
        }
        return map;
	}
	
	/**
     * 获取下一个工作日
     *
     * @param parameters 画面参数
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
    public String getWorkDay(Map<String, Object> parameters) {
        if (dateCalService != null) {
            Date tradeDate = DateUtil.parse((String)parameters.get("tradeDate"));
            String flag = (String)parameters.get("flag");
            int days = (int) parameters.get("days");
            List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDays(tradeDate, 31),
                (String)parameters.get("calendarId"));
            Date tradingDay = dateCalService.calculationTradingDay(tradeDate, days, holiday, flag);
            return DateUtil.format(tradingDay);
        }
        return null;
    }
    /**
	 * 获取下一个工作日
	 *
	 * @param parameters
	 *            画面参数
	 * @return 下一个工作日
	 * @see
	 * @since 1.0
	 */
	public String getNextWorkDay(Map<String, Object> parameters) {
		if (dateCalService != null) {
			Date tradeDate = DateUtil.parse((String) parameters.get("tradeDate"));
            String flag = (String)parameters.get("flag");
			List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDays(tradeDate, 31),
					(String) parameters.get("calendarId"));
			if(!holiday.contains((String)parameters.get("tradeDate"))) {
				return (String)parameters.get("tradeDate");
			}else {
				Date tradingDay = dateCalService.calculationTradingDay(tradeDate, 1, holiday, flag);
				return DateUtil.format(tradingDay);
			}
		}
		return null;
	}
    /*添加交割日与期限联动规则 gaopeng 2019/10/08 end*/

    @Override
    public void doBack(TrdFxswapInvestTrade001Dto entity) {
        //切面处理风险接口退回
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getCancelRemark())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }
    
    @Override
    public void doRevoke(TrdFxswapInvestTrade001Dto entity) {
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getCancelRemark())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }
}
