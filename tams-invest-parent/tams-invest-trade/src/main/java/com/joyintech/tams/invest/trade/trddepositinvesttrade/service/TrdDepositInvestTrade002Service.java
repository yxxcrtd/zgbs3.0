/*
 * 文件名：TrdDepositInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：fanshuanghu
 * 修改时间：2018年10月18日 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.trddepositinvesttrade.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.threadservice.JasperUtil;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.trade.trddepositinvesttrade.dto.TrdDepositInvestTrade001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 存款类新建Service
 * 
 * @author fanshuanghu
 * @version 1.0 2018年10月18日
 * @see TrdDepositInvestTrade002Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdDepositInvestTrade002Service extends CommonExportService<TrdDepositInvestTrade001Dto> implements BaseProcessService<TrdDepositInvestTrade001Dto> {

    /**
     * 金融产品交易Service
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    /**
     * 业务共通service
     */
    @Autowired(required = false)
    private CommonBusinessService commonBusinessService;

    /**
     * 注入业务主键生产类
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    // /**
    // * 金融产品交易业务流程表
    // */
    // @Autowired
    // private WflTrdProductDealMapper wflTrdProductDealMapper;
    // /**
    // * 金融产品交易扩展业务流程表
    // */
    // @Autowired
    // private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;
    // /**
    // * 金融产品附属业务流程表
    // */
    // @Autowired
    // private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;
    /**
     * 标的类金融产品Mapper
     */
    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * 系统日期相关
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    // /**
    // * 划款指令表
    // */
    // @Autowired
    // private WflStlPayOrdersMapper wflStlPayOrdersMapper;
    // /**
    // * 注入中债Mapper
    // */
    // @Autowired
    // private RepTradeMapper repTradeMapper;
    // /**
    // * 报送负债
    // */
    // @Autowired
    // private RepAssetDeptMapper repAssetDeptMapper ;
    /**
     * 交易日历推算类
     */
    @Autowired(required = false)
    private com.joyintech.tams.component.business.common.service.DateCalService dateCalService;

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

    @Autowired(required = false)
    private CeLinesUpdate ceLinesUpdate;

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候检查service
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;
    @Autowired
    private FinCashInfoMapper finCashInfoMapper;
    @Autowired
    private FinCashPayRuleMapper finCashPayRuleMapper;
    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
    @Autowired
    private TrdProductDealMapper trdProductDealMapper;
    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;
    @Autowired
    private PtlTradeMapper ptlTradeMapper;
    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;
    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;
    @Autowired
    private PrdSaleParamMapper prdSaleParamMapper;
    @Autowired
    private FinProductAddMapper finProductAddMapper;
    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;
    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;
    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;
    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;
    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private RepDurationMapper repDurationMapper;
    @Autowired
    private RepAssetDeptMapper repAssetDeptMapper;
    @Autowired
    private RepTradeMapper repTradeMapper;
    /**
   	 * 	投管接口共通组件
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
            if (!multiApproveTradeCheckService.multiApproveTradeCheck(tradeId)) {
                return getMessage("BUSS_COM_E_0100", tradeId);
            }
        }
        return null;
    }
    
    /**
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrEntrust getEtrInfo(String entrustId) {
        if (bussCommonMapperDelegateService != null) {
            return bussCommonMapperDelegateService.selectBySinglePrimaryKey(EtrEntrustMapper.class, entrustId, EtrEntrust.class);
        }
        return null;
    }

    /**
     * 资产提供行
     * 
     * @return 下拉框实体类集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getAssetProvider(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getAssetProvider", map,
            SelectOptionDto.class);
    }

    /**
     * 金融产品代码联动带出
     * 
     * @param finprodId 资产代码
     * @return 存款类实体类
     * @see
     * @since 1.0
     */
    public TrdDepositInvestTrade001Dto getFinInfo(String finprodId) {
        return restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getFinInfo?finprodId=" + finprodId,
            TrdDepositInvestTrade001Dto.class);
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
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getTrader", map,
            String.class);
    }

    /**
     * 划款指令生成实体类
     * 
     * @param dto 存款类投资交易实体类
     * @return 金融产品交易实体类
     * @see
     * @since 1.0
     */
    public TrdProductDeal getEntity(TrdDepositInvestTrade001Dto dto) {
        return this.setTrdProductDeal(dto);
    }

    /**
     * 是否保本
     * 
     * @param portfolioId 组合代码
     * @return 是否
     * @see
     * @since 1.0
     */
    public String getIsGuarantee(String portfolioId) {
        return restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getIsGuarantee?portfolioId=" + portfolioId,
            String.class);
    }

    /**
     * 获取持仓、可用现金等
     * 
     * @param map 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> available(Map<String, Object> map) {
        
        // 只有持仓及可用现金计算用的基础项目都录入的情况下才计算
        if (!StringUtil.isNotEmpty(map.get("finprodId")) || !StringUtil.isNotEmpty(map.get("tradeDate"))) {
            return new ArrayList<SuggestDto>();
        }
        
        FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class,
            map.get("finprodId").toString(), FinProduct.class) != null ? bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                FinProductMapper.class, map.get("finprodId").toString(),
                FinProduct.class) : finProductMapper.selectByPrimaryKey(map.get("finprodId").toString());
        String dateId = finProduct.getCalendarId();
        List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(map.get("tradeDate").toString()),
            DateUtil.addDay(DateUtil.parse(map.get("tradeDate").toString()), 15), dateId);
        Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(map.get("tradeDate").toString()), 1, holidayList,
            "1");
        map.put("tradeDay", DateUtil.format(tradeDay, "yyyy-MM-dd"));
        List<SuggestDto> list = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/available", map, SuggestDto.class);
        List<String> tradeList = dateCalService.getDealCalendarHoliday(DateUtil.parse(map.get("tradeDate").toString()),
            DateUtil.parse(map.get("tradeDate").toString()), dateId);
        SuggestDto sugDto = new SuggestDto();
        if (tradeList != null && tradeList.size() > 0 && 
                StringUtil.joinString( tradeList, ",").indexOf(map.get("tradeDate").toString()) > 0) {            
                sugDto.setCode("isHoliday");
                sugDto.setName("Y");            
        }
        else {
            sugDto.setCode("isHoliday");
            sugDto.setName("N");
        }
        list.add(sugDto);
        return list;
    }

    /**
     * 获取利率信息
     * 
     * @param map 查询条件
     * @return 利率
     * @see
     * @since 1.0
     */
    public String getIntAmt(Map<String, Object> map) {
        BigDecimal intAmt = null;
        Date vdate = DateUtil.parse(map.get("vdate").toString());
        Date mdate = DateUtil.parse(map.get("mdate").toString());
        BigDecimal loadRate = new BigDecimal(map.get("loadRate").toString());
        BigDecimal prinAmt = new BigDecimal(map.get("prinAmt").toString());
        Long days = DateUtil.getBetweenDayNumber(vdate, mdate);
        if (DictCodeList.计息基础_A_360.getValue().equals(map.get("basis"))) {
            intAmt = prinAmt.multiply(loadRate.multiply(BigDecimal.valueOf(days))).multiply(BigDecimal.valueOf(1.0 / 360));
        }
        else {
            intAmt = prinAmt.multiply(loadRate.multiply(BigDecimal.valueOf(days))).multiply(BigDecimal.valueOf(1.0 / 365));
        }
        String intAmtStr = intAmt.toString();
        int index = intAmtStr.indexOf(".");
        if (index >= 14) {

        }
        else if (intAmtStr.length() > index + 2) {
            intAmtStr = intAmtStr.substring(0, index + 3);
        }
        return intAmtStr;
    }

    /**
     * 获取下拉框
     * 
     * @return 市场代码
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getFinprodMarketId2(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getFinprodMarketId2",
            map, SelectOptionDto.class);
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
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getPortfolioIdSug", map, SuggestDto.class);
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
    public StlPayOrdersDto stlInfo(String tradeId) {
        return restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/stlInfo?tradeId=" + tradeId,
            StlPayOrdersDto.class);
    }

    /**
     * 获取opics指令信息
     * 
     * @param tradeId 交易编号
     * @return opics参数集合
     * @see
     * @since 1.0
     */
    public List<InterfSendOpics> getInterfSendOpics(String tradeId) {
        return restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getInterfSendOpics?tradeId=" + tradeId,
            InterfSendOpics.class);
    }

    /**
     * 获取opics下拉框
     * 
     * @param map 请求类型
     * @return 下拉框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getSelectOptions(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getSelectOptions", map,
            SelectOptionDto.class);
    }

    /**
     * 流程中数据处理
     * 
     * @param dto 存款类投资交易实体类
     * @see
     * @since 1.0
     */
    public void beforeWorkFlow(TrdDepositInvestTrade001Dto dto) {
        if (dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0
            && dto.getBussTrdTransferOrder().get(0) != null && !dto.getBussTrdTransferOrder().get(0).toString().isEmpty()) {
            if (bussTrdTransferOrderService != null) {
                dto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            }
            StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, dto.getBussTrdTransferOrder().get(0));
            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
            stlPayOrdersDto.setTradeId(dto.getTradeId());
            list.add(stlPayOrdersDto);
            dto.setStlPayOrdersList(list);
        }
        if (dto.getOperateType().equals("revoke")) {
            FinProductTradeDto finProductTradeDto = dto.getFinProductTradeDto();
            List<BaseTradeProductDealDto> baseDto = finProductTradeDto.getBaseTradeProductDealDtos();
            for (BaseTradeProductDealDto baDto : baseDto) {
                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baDto.getTrdProductDeal());
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                wflTrdProductDeal.setWorkflowId(dto.getWorkflowId());
                wflTrdProductDeal.setpTradeId(wflTrdProductDeal.getTradeId());
                wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + "R");
                // wflTrdProductDealMapper.insert(wflTrdProductDeal);
                WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
                // BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal,);
                if (baDto.getTrdProductDeal().getPs() != null
                    && baDto.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                    wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_存款类资产买入.getValue());
                }
                else if (baDto.getTrdProductDeal().getPs() != null
                         && baDto.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                    wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_存款类资产卖出.getValue());
                }
                wflPaTrdProductDeal.setTradeId(dto.getTradeId());
                wflPaTrdProductDeal.setTradeId(wflPaTrdProductDeal.getTradeId() + "R");
                wflPaTrdProductDeal.setDeptCode(LoginUserUtil.getDeptCode());
                // wflPaTrdProductDeal.setPaBussType(paBussType);
                wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflPaTrdProductDeal.setWorkflowId(dto.getWorkflowId());
                // wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baDto.getTrdProductDealAdd());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflTrdProductDealAdd.setWorkflowId(dto.getWorkflowId());
                wflTrdProductDealAdd.setTradeId(wflTrdProductDealAdd.getTradeId() + "R");
//                bussCommonMapperDelegateService.insertBatch(WflTrdProductDealMapper.class, wflTrdProductDeal,
//                    WflPaTrdProductDealMapper.class, wflPaTrdProductDeal, WflTrdProductDealAddMapper.class, wflTrdProductDealAdd);

                wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
                wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
                if (dto.getStlPayOrdersList() != null && dto.getStlPayOrdersList().size() > 0) {
                    for (StlPayOrdersDto stlPayOrdersDto : dto.getStlPayOrdersList()) {
                        WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                        wflStlPayOrders.setWorkflowId(dto.getWorkflowId());
                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                        wflStlPayOrders.setTradeId(dto.getTradeId() + "R");
                        wflStlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                        if (wflStlPayOrders.getOrderId() != null) {
                             wflStlPayOrdersMapper.insert(wflStlPayOrders);
//                            bussCommonMapperDelegateService.insertBatch(WflStlPayOrdersMapper.class, wflStlPayOrders);
                        }
                    }
                }
                // dto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                // baseTradeProductDealCommService.execute(dto.getFinProductTradeDto());
            }
        }
        else {
            TrdDepositInvestTrade001Dto entity = this.setEntity(dto);

            // 调用授信共同 sjj start
            TrdProductDeal TrdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity);
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            TrdProductDeal.setCounterId(entity.getTrdInvestPersonInfo002Dto().getCounterId());
            // add by wanshiwei ZGZGST-3595 存款类资产“利率%”取值错误，取资产的计提利率 20210928 start
            TrdProductDeal.setUnitInt(new BigDecimal(dto.getLoadRate()));
            // add by wanshiwei ZGZGST-3595 存款类资产“利率%”取值错误，取资产的计提利率 20210928 end
            inputDto.setTrdProductDeal(TrdProductDeal);
            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto,false);
            // 调用授信共同 sjj end
            
            FinProductTradeDto finProductTradeDto = entity.getFinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            // baseTradeProductDealCommService.execute(finProductTradeDto);
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, this.setTrdProductDeal(entity));
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            // bussCommonMapperDelegateService.insert(WflTrdProductDealMapper.class, wflTrdProductDeal);
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, this.setTrdProductDealAdd(entity));
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
            wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
            // wflTrdProductDealAddMapper.insert(wflTrdProductDealAdd);
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, this.setPaTrdProductDeal(entity));
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            if (entity.getPs() != null && entity.getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_存款类资产买入.getValue());
            }
            else if (entity.getPs() != null && entity.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_存款类资产卖出.getValue());
            }
             wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
             
             //update by wangpeng 通道信息不登表
             String chlid=wflTrdProductDeal.getChlId();
             if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
             	wflTrdProductDeal.setChlId(null);
             }
             
            wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
            wflTrdProductDeal.setChlId(chlid);
            //update by wnagpeng end
            wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);

            if (entity.getStlPayOrdersList() != null && entity.getStlPayOrdersList().size() > 0) {
                List<WflStlPayOrders>stlPayOrdersDtos = new ArrayList();
                for (StlPayOrdersDto stlPayOrdersDto : entity.getStlPayOrdersList()) {
                    WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                    wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                    // wflStlPayOrdersMapper.insert(wflStlPayOrders);
                    if (wflStlPayOrders.getOrderId() != null) {
                        wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                    }
                }
            }

            entity.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            //            不为删除时处理
            //        委托代码不为空处理委托代码
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(item->{
                String entrustId = StringUtil.isNotEmpty(entity.getEntrustId())?entity.getEntrustId():item.getTrdProductDealAdd()!=null?item.getTrdProductDealAdd().getEntrustId():null;
                if(StringUtil.isEmpty(item.getEntrustId())&&StringUtil.isNotEmpty(entrustId)){
                    item.setEntrustId(entrustId);
                }
            });

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            baseTradeProductDealCommService.execute(entity.getFinProductTradeDto());
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
     * 流程结束数据处理
     * 
     * @param entity 存款类投资交易实体类
     * @see
     * @since 1.0
     */
    public void afterWorkFlow(TrdDepositInvestTrade001Dto entity) {
        if (entity.getOperateType().equals("NEW")) {
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, this.setTrdProductDeal(entity));
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//            bussCommonMapperDelegateService.deleteByPrimaryKey(WflTrdProductDealMapper.class, wflTrdProductDeal);
            wflTrdProductDealMapper.deleteByPrimaryKey(wflTrdProductDeal);
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, this.setTrdProductDealAdd(entity));
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            // wflTrdProductDealAddMapper.deleteByPrimaryKey(wflTrdProductDealAdd);
//            bussCommonMapperDelegateService.deleteByPrimaryKey(WflTrdProductDealAddMapper.class, wflTrdProductDealAdd);
            wflTrdProductDealAddMapper.deleteByPrimaryKey(wflTrdProductDealAdd);
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, this.setPaTrdProductDeal(entity));
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//            bussCommonMapperDelegateService.deleteByPrimaryKey(WflPaTrdProductDealMapper.class, wflPaTrdProductDeal);
            wflPaTrdProductDealMapper.deleteByPrimaryKey(wflPaTrdProductDeal);
            // wflPaTrdProductDealMapper.deleteByPrimaryKey(wflPaTrdProductDeal);
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            if (entity.getStlPayOrdersList() != null && entity.getStlPayOrdersList().size() > 0) {
                for (StlPayOrdersDto stlPayOrdersDto : entity.getStlPayOrdersList()) {
                    BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                    wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    if (wflStlPayOrders.getOrderId() != null) {
                        // wflStlPayOrdersMapper.deleteByPrimaryKey(wflStlPayOrders);
//                        bussCommonMapperDelegateService.deleteByPrimaryKey(WflStlPayOrdersMapper.class, wflStlPayOrders);
                        wflStlPayOrdersMapper.deleteByPrimaryKey(wflStlPayOrders);
                    }
                }
            }

            String prodRegistCode = this.getProdRegistCode(entity.getPortfolioId());

            if (prodRegistCode != null && !prodRegistCode.isEmpty()) {
                RepTrade repTrade = this.setRepTrade(entity, this.getProdRegistCode(entity.getPortfolioId()));
                // 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
                restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepTrade",
                    Arrays.asList(repTrade), Boolean.class);
            }
            RepAssetDept repAssetDept = new RepAssetDept();
            repAssetDept.setRepFinprodId(entity.getFinprodId());
            repAssetDept.setRepSendStatus(DictCodeList.是否_是.getValue());
//            bussCommonMapperDelegateService.updateByPrimaryKeySelective(RepAssetDeptMapper.class, repAssetDept);
            repAssetDeptMapper.updateByPrimaryKeySelective(repAssetDept);
        }
        else {
            FinProductTradeDto finProductTradeDto = entity.getFinProductTradeDto();
            List<BaseTradeProductDealDto> baseDto = finProductTradeDto.getBaseTradeProductDealDtos();
            for (BaseTradeProductDealDto baDto : baseDto) {
                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baDto.getTrdProductDeal());
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + "R");
//                bussCommonMapperDelegateService.deleteByPrimaryKey(WflTrdProductDealMapper.class, wflTrdProductDeal);
                wflTrdProductDealMapper.deleteByPrimaryKey(wflTrdProductDeal);
                WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, baDto.getTrdProductDeal());
                wflPaTrdProductDeal.setTradeId(wflPaTrdProductDeal.getTradeId() + "R");
                wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
//                bussCommonMapperDelegateService.deleteByPrimaryKey(WflPaTrdProductDealMapper.class, wflPaTrdProductDeal);
                wflPaTrdProductDealMapper.deleteByPrimaryKey(wflPaTrdProductDeal);
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                wflTrdProductDealAdd.setTradeId(wflTrdProductDealAdd.getTradeId() + "R");
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baDto.getTrdProductDealAdd());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                // wflTrdProductDealAddMapper.deleteByPrimaryKey(wflTrdProductDealAdd);
//                bussCommonMapperDelegateService.deleteByPrimaryKey(WflTrdProductDealAddMapper.class, wflTrdProductDealAdd);
                wflTrdProductDealAddMapper.deleteByPrimaryKey(wflTrdProductDealAdd);
                if (baDto.getStlPayOrdersList() != null && baDto.getStlPayOrdersList().size() > 0) {
                    for (StlPayOrdersDto stlPayOrdersDto : baDto.getStlPayOrdersList()) {
                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                        if (wflStlPayOrders.getOrderId() != null) {
                            // wflStlPayOrdersMapper.deleteByPrimaryKey(wflStlPayOrders);
//                            bussCommonMapperDelegateService.deleteByPrimaryKey(WflStlPayOrdersMapper.class, wflStlPayOrders);
                            wflStlPayOrdersMapper.deleteByPrimaryKey(wflStlPayOrders);
                        }
                    }
                }
                RepTradeKey repTradeKey = new RepTradeKey();
                repTradeKey.setRepBankTradeCode(baDto.getTrdProductDeal().getTradeId());
                repTradeKey.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_存入银行存款.getValue());
                // repTradeMapper.deleteByPrimaryKey(repTradeKey);
//                bussCommonMapperDelegateService.deleteByPrimaryKey(RepTradeMapper.class, repTradeKey);
                repTradeMapper.deleteByPrimaryKey(repTradeKey);
            }
        }
    }

    /**
     * 作废操作,清空业务表 {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
        
//        restTemplate.getForList(
//            ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/doDelete?workflowId=" + workflowId, Integer.class);
    }

    /**
     * 新建界面提交时插入持仓流水 {@inheritDoc}
     */
    @Override
    public void doInsert(TrdDepositInvestTrade001Dto entity, OperationType type) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        if (type.toString().equals("NEW") && entity.getOperateType().equals("submit")) {
        	
        	// 加个开关  20200506---wj   if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
        	if(!StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.WFL_AUTO), "02")){
        		beforeWorkFlow(entity);
        	}
        }
        else if (type.toString().equals("REVOKE")) {
            List<BaseTradeProductDealDto> baseTradeProductDealDto = restTemplate.getForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getFinDto?tradeId=" + entity.getTradeId(),
                BaseTradeProductDealDto.class);
            FinProductTradeDto dto = new FinProductTradeDto();
            entity.setFinProductTradeDto(dto);
            entity.getFinProductTradeDto().setBaseTradeProductDealDtos(baseTradeProductDealDto);
            entity.setOperateType("revoke");
            beforeWorkFlow(entity);
        }
    }

    /**
     * 保存中进行提交时进行性插入持仓流水 {@inheritDoc}
     */
    @Override
    public void doUpdate(TrdDepositInvestTrade001Dto entity) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        if (CollectionUtils.isNotEmpty(entity.getFileArray()) && StringUtil.isNotEmpty(entity.getTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                entity.getFileArray().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getTradeId());
        }
        if (entity.getOperateType().equals("submit")) {
            doDelete(entity.getWorkflowId());
            beforeWorkFlow(entity);
        }
		entity.setPortfolioId(entity.getActPortfolioId());
        // else if(entity.getOperateType().equals("delete")){
        // List<BaseTradeProductDealDto> baseTradeProductDealDto =restTemplate.getForList(ServiceApplication.TRADE_SERVICE_NAME
        // + "/TrdDepositInvestTrade001/getFinDto?tradeId="+entity.getTradeId(),BaseTradeProductDealDto.class);
        // FinProductTradeDto dto = new FinProductTradeDto();
        // entity.setFinProductTradeDto(dto);
        // entity.getFinProductTradeDto().setBaseTradeProductDealDtos(baseTradeProductDealDto);
        // beforeWorkFlow(entity);
        // }
    }

    /**
     * 流程 {@inheritDoc}
     */
    @Override
    public void doComplete(TrdDepositInvestTrade001Dto entity, OperationType type) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        if (entity.getBussTrdTransferOrder() != null && entity.getBussTrdTransferOrder().size() > 0
            && entity.getBussTrdTransferOrder().get(0) != null && !entity.getBussTrdTransferOrder().get(0).toString().isEmpty()) {
            if (bussTrdTransferOrderService != null) {
                entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(entity.getBussTrdTransferOrder()));
            }
            StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, entity.getBussTrdTransferOrder().get(0));
            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
            list.add(stlPayOrdersDto);
            entity.setStlPayOrdersList(list);
        }

        // 提前查询判断  是否  撤销 并且是 有转仓交易 并且转仓交易方向ps为买入
        String reverseTradeId = "";
        if(type == OperationType.REVOKE) {
            TrdProductDeal reverseTrdProductDeal = trdCommonBaseService.selectZCDealByTid(entity.getTradeId(),false);
            if (reverseTrdProductDeal != null && StringUtil.equals(DictCodeList.交易方向_买入.getValue(), reverseTrdProductDeal.getPs())) {
                reverseTradeId = reverseTrdProductDeal.getTradeId();
            }
        }

        // TODO Auto-generated method stub
        TrdDepositInvestTrade001Dto dto = new TrdDepositInvestTrade001Dto();
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        if (type.toString().equals("REVOKE")) {
            List<BaseTradeProductDealDto> baseTradeProductDealDto = restTemplate.getForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getFinDto?tradeId=" + entity.getTradeId(),
                BaseTradeProductDealDto.class);
            for (BaseTradeProductDealDto dts : baseTradeProductDealDto) {
                // dts.getTrdProductDeal().setpTradeId(dts.getTrdProductDeal().getTradeId());
                dts.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                dts.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                dts.getTrdProductDeal().setCancelRemark(entity.getCancelRemark());
                if(StringUtil.isNotEmpty( dts.getTrdProductDeal().getFinprodId())){
                	Map<String, Object> pageParam = new HashMap<>();
                	pageParam.put("finprodId",  dts.getTrdProductDeal().getFinprodId());
                	List<SelectOptionDto> selectOptionDto = restTemplate.postForList(ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/getChannelIdAndCollectTypeForAsset",
                			pageParam , SelectOptionDto.class);
                	if(!StringUtil.isEmptyList(selectOptionDto)){
                		//设置通道id  撤销通道持仓
                		dts.getTrdProductDeal().setChlId(selectOptionDto.get(0).getId());
                	}
                }
                if (bussCommonMapperDelegateService != null) {
                    PaTrdProductDeal paTrdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                        PaTrdProductDealMapper.class, dts.getTrdProductDeal().getTradeId(), PaTrdProductDeal.class);
                    paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId() + "R");
//                    bussCommonMapperDelegateService.insertBatch(PaTrdProductDealMapper.class, paTrdProductDeal);
                    paTrdProductDealMapper.insert(paTrdProductDeal);
                }
            }

            PtlTrade ptlTrade = new PtlTrade();
            ptlTrade.setBusiNo(entity.getTradeId());
            ptlTrade.setPortfolioId(entity.getPortfolioId());
            ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade = bussCommonMapperDelegateService.selectByMultiPrimaryKey(PtlTradeMapper.class, ptlTrade, PtlTrade.class);
            ptlTrade.setBusiNo(ptlTrade.getBusiNo() + "R");
//            bussCommonMapperDelegateService.insertBatch(PtlTradeMapper.class, ptlTrade);
            ptlTradeMapper.insert(ptlTrade);
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                ptlTrade.setBusiNo(entity.getTradeId());
                ptlTrade.setPortfolioId(entity.getActPortfolioId());
                ptlTrade = bussCommonMapperDelegateService.selectByMultiPrimaryKey(PtlTradeMapper.class, ptlTrade, PtlTrade.class);
                ptlTrade.setBusiNo(ptlTrade.getBusiNo() + "R");
//                bussCommonMapperDelegateService.insertBatch(PtlTradeMapper.class, ptlTrade);
                ptlTradeMapper.insert(ptlTrade);
            }

            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId() + "R");
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDto);
            entity.setFinProductTradeDto(finProductTradeDto);
            
            //删除授信占用
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
            // 调用授信共同 sjj start

//            TrdProductDeal TrdProductDeal = new TrdProductDeal();
//            BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity);
//            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//            TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
//            TrdProductDeal.setCounterId(entity.getTrdInvestPersonInfo002Dto().getCounterId());
//            inputDto.setTrdProductDeal(TrdProductDeal);
//            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto,false);
            // 调用授信共同 sjj end
        }
        else {
            dto = this.setEntity(entity);
            dto.getFinProductTradeDto().setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());

            // 处理opics表
//            if (entity.getInterfSendOpicsList() != null) {
//                if (bussCommonMapperDelegateService != null) {
//                    for (InterfSendOpics interfSendOpics : entity.getInterfSendOpicsList()) {
                        // BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                        // bussinessCodeParameter.setSeqNo("9999");
                        // String UUID = (baseGetBusinessId == null) ? baseGetBusinessId.buildBussinessCode(
                        // bussinessCodeParameter).substring(0, 31) : IDGenerator.generate().substring(0, 31);;
                        // interfSendOpics.setUuid(UUID);
                        // interfSendOpics.setAssetpool(entity.getPortfolioId());
                        // interfSendOpics.setBusinessid(entity.getTradeId());
                        // interfSendOpics.setCashno(entity.getTradeId());
                        // interfSendOpics.setSendmodule("TrdDepositInvestTrade002");
                        // interfSendOpics.setSendmode("ESP");
                        // interfSendOpics.setSenddirection("OPICS");
                        // interfSendOpics.setVdate(DateUtil.format(new Date(Long.valueOf(interfSendOpics.getVdate()))));
                        // interfSendOpics.setMdate(DateUtil.format(new Date(Long.valueOf(interfSendOpics.getMdate()))));
                        // interfSendOpics.setDealdate(DateUtil.format(new Date(Long.valueOf(interfSendOpics.getDealdate()))));
                        // interfSendOpics.setVdate(interfSendOpics.getVdate().replaceAll("-", ""));
                        // interfSendOpics.setMdate(interfSendOpics.getMdate().replaceAll("-", ""));
                        // interfSendOpics.setDealdate(interfSendOpics.getDealdate().replaceAll("-", ""));
                        // interfSendOpics.setSn(entity.getTradeId() + DateUtil.format(new Date(), "yyyyMMddHHmmss"));
                        // bussCommonMapperDelegateService.insertBatch(InterfSendOpicsMapper.class, interfSendOpics);
//                    }
//                }
//            }
            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());
            finProductTradeDto = entity.getFinProductTradeDto();
//            不为删除时处理
            //        委托代码不为空处理委托代码
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(item->{
                String entrustId = StringUtil.isNotEmpty(entity.getEntrustId())?entity.getEntrustId():item.getTrdProductDealAdd()!=null?item.getTrdProductDealAdd().getEntrustId():null;
                if(StringUtil.isEmpty(item.getEntrustId())&&StringUtil.isNotEmpty(entrustId)){
                    item.setEntrustId(entrustId);
                }
            });
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

        dto.setOperateType(type.toString());
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/doComplete", dto,
            Integer.class);
        afterWorkFlow(entity);

        // 通道交易 新增 强成西 2019/03/26 start
        TrdTradeFeeDetail trdTradeFeeDetail = null;
        if (!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
        	if (StringUtil.isNotEmpty(entity.getChlId())&&StringUtil.isNotEmpty(entity.getChannelFee())) {
        		// 交易费用明细
        		//2019-12-24 通道费改造 标的挂通道  通道费不参与计算
        		if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
        			trdTradeFeeDetail = AllTradeCommonUtil.genChannelTradeFeeDetail(entity.getTradeId(), entity.getChannelFee(),entity.getCollectType(),dto.getIsOutTrade());
        			
        		}else{
        			trdTradeFeeDetail = new TrdTradeFeeDetail();
                    trdTradeFeeDetail.setTradeId(entity.getTradeId());
                    // 交易费用明细表.费用类型 = “通道费”
                    trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
                    // 交易费用明细表.是否随交易支付 
                        trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                    // 交易费用明细表.交易金额 = 交易.通道费
                    trdTradeFeeDetail.setFeeAmt(entity.getChannelFee());
        		}
        		//        		bussCommonMapperDelegateService.insertBatch(TrdTradeFeeDetailMapper.class, trdTradeFeeDetail
//        				);
                trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
        	}
        }
        // 通道交易 新增 强成西 2019/03/26 end

        // 一体化对应etliu start
        // 所有交易生效后调用的共通方法
        if (!type.toString().equals("DELETE")) {
            for (BaseTradeProductDealDto baseDto : finProductTradeDto.getBaseTradeProductDealDtos()) {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(entity.getPortfolioId());
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                inputDto.setTrdProductDeal(baseDto.getTrdProductDeal());

                // 通道交易 新增 强成西 2019/03/26 start
                inputDto.setTrdProductDealAdd(baseDto.getTrdProductDealAdd());
                inputDto.setTrdTradeFeeDetail(trdTradeFeeDetail);
                // 通道交易 新增 强成西 2019/03/26 end

                // 反向交易增加 start by qiangchengxi 2019年3月12日
                AllTradeCommonUtil.setDeals(inputDto, baseDto.getTrdProductDeal(), baseDto.getTrdProductDealAdd(),
                    baseDto.getPaTrdProductDeal(), baseDto.getStlPayOrdersList(), entity.getPtlTrade(), entity.getActPortfolioId());
                // 反向交易增加 end by qiangchengxi 2019年3月12日

                if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    inputDto.setActPortfolioId(entity.getActPortfolioId());
                }
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
                if (type.toString().equals("NEW")) {
                    // 调用投管接口 add by cxq 20190214 start
                    InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                    investInterfCommDto.setPageId("TrdDepositInvestTrade002");
                    investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
                    investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
                    investInterfCommDto.setTrdProductDeal(this.setTrdProductDeal(entity));
                    investInterfCommDto.setTrdProductDealAdd(this.setTrdProductDealAdd(entity));
                    investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
                    investInterfCommDto.setTradeFeeDetaillist(baseDto.getTrdTradeFeeDetailList());
                    investInterfCommService.doInvestInterf(investInterfCommDto);
                    // 调用投管接口 add by cxq 20190214 end
				}

            }
            jasperExecute(entity);
        }
        // 一体化对应etliu end

        if(StringUtil.isNotEmpty(reverseTradeId)){

            TrdProductDeal trdProductDeal = new TrdProductDeal();
            trdProductDeal.setTradeId(reverseTradeId);
            trdProductDeal.setCancelRemark(entity.getCancelRemark());
            commonBusinessService.revokeReverseTrade(trdProductDeal,false);

        }

        // 调接口 by 赵东东 20191206 start
        List<String> tradeIds = new ArrayList<String>();
        tradeIds.add(entity.getTradeId());
        try {
        restTemplate.postForObject(
        		ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
        		Map.class);
        }catch(Exception e) {
        	
        }
        // 调接口 by 赵东东 20191206  end
    }

    @Autowired
    private JasperUtil jasperUtil;

    public void jasperExecute(TrdDepositInvestTrade001Dto entity) {
        if("001".equals(CacheUtil.getSysParam("CUSTOMER_BANK_CODE"))){
            //生成划款指令附件:调用自定义接口项目生成Jasper报表 add by zhangJin 2019-07-29 start
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            trdProductDeal.setTradeId(entity.getTradeId());
            trdProductDeal.setPs(entity.getPs());
            trdProductDeal.setFinprodId(entity.getFinprodId());
            jasperUtil.jasperExecute(Arrays.asList(trdProductDeal), restTemplate);
            //生成划款指令附件:调用自定义接口项目生成Jasper报表 add by zhangJin 2019-07-29 end
        }
    }

    /**
     * 获取opics参数
     * 
     * @param tradeId 交易编号
     * @return opics参数列表
     * @see
     * @since 1.0
     */
    public List<InterfSendOpics> getOpics(String tradeId) {
        return restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getOpics?tradeId=" + tradeId,
            InterfSendOpics.class);

    }

    /**
     * 合成各实体类
     * 
     * @param dto 存款类投资交易实体类
     * @return 存款类投资交易实体类
     * @see
     * @since 1.0
     */
    public TrdDepositInvestTrade001Dto setEntity(TrdDepositInvestTrade001Dto dto) {
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
        dto.setPtlTrade(this.setPtlTrade(dto));
        dto.setPaTrdProductDeal(this.setPaTrdProductDeal(dto));
        dto.setFinProductTradeDto(finDto);

        // <!-- 通道交易 新增 强成西 2019/03/26 start -->
        dto.setTradeAmt(dto.getTradeAmtAndFee());
//        dto.setFeeAmtPay(ArithmeticUtil.doAdd(dto.getFeeAmtPay(), dto.getChannelFee()));
//        dto.setFeeAmt(ArithmeticUtil.doAdd(dto.getFeeAmt(), dto.getChannelFee()));
        // <!-- 通道交易 新增 强成西 2019/03/26 end -->
        if(StringUtil.isNotEmpty(dto.getChlId())&&StringUtil.isNotEmpty(dto.getChannelFee())){
//        	if(StringUtil.equals(DictCodeList.是否_否.getValue(),dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())){
//        		dto.setFeeAmtPay(ArithmeticUtil.doAdd(dto.getFeeAmtPay(), dto.getChannelFee()));
//        	}else{
//	        	if(StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), dto.getCollectType())){
//	        		dto.setFeeAmtPay(ArithmeticUtil.doAdd(dto.getFeeAmtPay(), dto.getChannelFee()));
//	        	}else{
//	        		dto.setFeeAmtUnpay(ArithmeticUtil.doAdd(dto.getFeeAmtUnpay(), dto.getChannelFee()));
//	        	}
//        	}
//        	dto.setFeeAmt(ArithmeticUtil.doAdd(dto.getFeeAmt(), dto.getChannelFee()));
        	//2019-12-24 通道费改造 标的挂通道  通道费不参与计算
        	if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
        		if(StringUtil.equals(DictCodeList.是否_否.getValue(),dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())){
            		dto.setFeeAmtPay(ArithmeticUtil.doAdd(dto.getFeeAmtPay(), dto.getChannelFee()));
            	}else{
    	        	if(StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), dto.getCollectType())){
    	        		dto.setFeeAmtPay(ArithmeticUtil.doAdd(dto.getFeeAmtPay(), dto.getChannelFee()));
    	        	}else{
    	        		dto.setFeeAmtUnpay(ArithmeticUtil.doAdd(dto.getFeeAmtUnpay(), dto.getChannelFee()));
    	        	}
            	}
            	dto.setFeeAmt(ArithmeticUtil.doAdd(dto.getFeeAmt(), dto.getChannelFee()));
        	}else{
        		dto.setFeeAmtUnpay(ArithmeticUtil.doAdd(dto.getFeeAmtUnpay(), dto.getChannelFee()));
            	dto.setFeeAmt(ArithmeticUtil.doAdd(dto.getFeeAmt(), dto.getChannelFee()));
        	}
        }
        dto.setIsOutTrade(baseDto.getTrdProductDeal().getIsOutTrade());
        return dto;
    }

    /**
     * 合成金融产品交易实体类
     * 
     * @param dto 存款类投资交易实体类
     * @return 金融产品交易实体类
     * @see
     * @since 1.0
     */
    public TrdProductDeal setTrdProductDeal(TrdDepositInvestTrade001Dto dto) {
        TrdProductDeal trdDto = new TrdProductDeal();
        trdDto.setTradeId(dto.getTradeId());// 塞入交易编号
        trdDto.setBusiType(DictCodeList.共通业务类型_存款类资产交易.getValue());// 设置业务类型
        trdDto.setFinprodId(dto.getFinprodId());
        trdDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        trdDto.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue"))/* Long.valueOf(0) */);
        trdDto.setCpriceAmt(dto.getPrinAmt());
        trdDto.setCcy(dto.getCcy());
        trdDto.setPs(dto.getPs());
        trdDto.setTradeDate(dto.getTradeDate());
        trdDto.setVdate(dto.getVdate());
        trdDto.setMdate(dto.getMdate());
        trdDto.setSettleDate(dto.getTradeDate());
        trdDto.setShareAmt(dto.getPrinAmt());
        // trdDto.setTradeAmt(dto.getPrinAmt());
        trdDto.setIntAmt(dto.getIntAmt());
        // if(dto.getIntAmt() != null && dto.getPrinAmt() != null){
        // trdDto.setFpriceAmt(dto.getIntAmt().add(dto.getPrinAmt()));
        // }else{
        trdDto.setFpriceAmt(dto.getPrinAmt());
        // }
        if (dto.getIntAmt() != null && dto.getPrinAmt() != null) {
            trdDto.setTradeAmt(dto.getIntAmt().add(dto.getPrinAmt()));
        }
        else {
            trdDto.setTradeAmt(dto.getPrinAmt());
        }
        trdDto.setPrinAmt(dto.getPrinAmt());
        trdDto.setIsClean(dto.getIsClean());
        trdDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdDto.setRemark(dto.getRemark());
        trdDto.setCounterId(dto.getTrdInvestPersonInfo002Dto().getCounterId());
        trdDto.setTrader(dto.getTrdInvestPersonInfo002Dto().getTrader());
        trdDto.setCounterContact(dto.getTrdInvestPersonInfo002Dto().getContactInfo());
        trdDto.setCounterTrader(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
//        trdDto.setIsOutTrade(dto.getTrdInvestPersonInfo002Dto().getIsOutTrade());
        if(StringUtil.equals(DictCodeList.是否_否.getValue(),dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())){
        	dto.setIsOutTrade(dto.getTrdInvestPersonInfo002Dto().getIsOutTrade());
        }else{
        	dto.setIsOutTrade(DictCodeList.是否_是.getValue());
        }
        trdDto.setCounterTrader(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
        trdDto.setIsCancel("N");
        // <!-- 通道交易 新增 强成西 2019/03/26 end -->
        trdDto.setChlId("999999".equals(dto.getChlId()) ? null : dto.getChlId());
//        trdDto.setFeeAmt(ArithmeticUtil.doAdd(trdDto.getFeeAmt(), dto.getChannelFee()));
//        trdDto.setFeeAmtPay(ArithmeticUtil.doAdd(trdDto.getFeeAmtPay(), dto.getChannelFee()));
        if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
        	if(StringUtil.isNotEmpty(dto.getChannelFee())){
            	if(StringUtil.equals(DictCodeList.是否_否.getValue(),dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())){
            		trdDto.setFeeAmtPay(ArithmeticUtil.doAdd(trdDto.getFeeAmtPay(), dto.getChannelFee()));
            	}else{
            		if(StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), dto.getCollectType())){
                		trdDto.setFeeAmtPay(ArithmeticUtil.doAdd(trdDto.getFeeAmtPay(), dto.getChannelFee()));
                	}else{
                		trdDto.setFeeAmtUnpay(ArithmeticUtil.doAdd(trdDto.getFeeAmtUnpay(), dto.getChannelFee()));
                	}
            	}
            	trdDto.setFeeAmt(ArithmeticUtil.doAdd(trdDto.getFeeAmt(), dto.getChannelFee()));
            }
        }else{
        	trdDto.setFeeAmtUnpay(ArithmeticUtil.doAdd(trdDto.getFeeAmtUnpay(), dto.getChannelFee()));
        	trdDto.setFeeAmt(ArithmeticUtil.doAdd(trdDto.getFeeAmt(), dto.getChannelFee()));
        }

        //交易费用 本金+利息-随交易支付费用
        trdDto.setTradeAmt(ArithmeticUtil.subtract(trdDto.getTradeAmt(), trdDto.getFeeAmtPay()));
        // add by wanshiwei ZGZGST-3595 存款类资产“利率%”取值错误，取资产的计提利率 20210928 start
        trdDto.setUnitInt(ArithmeticUtil.divide(new BigDecimal(dto.getLoadRate()), 100, 8));
        // add by wanshiwei ZGZGST-3595 存款类资产“利率%”取值错误，取资产的计提利率 20210928 end

//        if (DictCodeList.交易方向_买入.getValue().equals(dto.getPs())) {
//            trdDto.setTradeAmt(ArithmeticUtil.doAdd(trdDto.getTradeAmt(), dto.getChannelFee()));
//        }
//        else {
//            trdDto.setTradeAmt(ArithmeticUtil.subtract(trdDto.getTradeAmt(), dto.getChannelFee()));
//        }
        // <!-- 通道交易 新增 强成西 2019/03/26 end -->
        return trdDto;
    }

    /**
     * 生成金融产品附属表
     * 
     * @param dto 存款类投资交易实体类
     * @return 金融产品附属类
     * @see
     * @since 1.0
     */
    public TrdProductDealAdd setTrdProductDealAdd(TrdDepositInvestTrade001Dto dto) {
        TrdProductDealAdd addDto = new TrdProductDealAdd();
        addDto.setTradeId(dto.getTradeId());
        addDto.setPortfolioId(dto.getPortfolioId());
        addDto.setEntrustId(dto.getEntrustId());
        // addDto.setProdId(dto.getFinprodId());
        addDto.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        return addDto;
    }

    /**
     * 组合交易表
     * 
     * @param dto 存款类投资交易实体类
     * @return
     * @see
     * @since 1.0
     */
    public List<PtlTrade> setPtlTrade(TrdDepositInvestTrade001Dto dto) {
        List<PtlTrade> result = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        ptlTrade.setBusiNo(dto.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(dto.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_存款类资产交易.getValue());
        ptlTrade.setSettleDate(dto.getTradeDate());
        result.add(ptlTrade);

        if(StringUtil.isNotEmpty(dto.getActPortfolioId()) && !StringUtil.equals(dto.getPortfolioId(), dto.getActPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            ptlTrade1.setPortfolioId(dto.getActPortfolioId());
            ptlTrade1.setBusiNo(dto.getTradeId());
            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade1.setBusiId(dto.getFinprodId());
            ptlTrade1.setBusiType(DictCodeList.共通业务类型_存款类资产交易.getValue());
            ptlTrade1.setSettleDate(dto.getTradeDate());
            result.add(ptlTrade1);
        }
        return result;
    }

    /**
     * 金融产品交易扩展
     * 
     * @param dto 存款类投资交易实体类
     * @return 金融产品交易实体类
     * @see
     * @since 1.0
     */
    public PaTrdProductDeal setPaTrdProductDeal(TrdDepositInvestTrade001Dto dto) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(dto.getTradeId());
        if (dto.getPs() != null && !dto.getPs().isEmpty()) {
            if (dto.getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_存款类资产买入.getValue());
            }
            else if (dto.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_存款类资产卖出.getValue());
            }
        }
        paTrdProductDeal.setDeptCode(LoginUserUtil.getDeptCode());
        return paTrdProductDeal;
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
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getProdRegistCode",
            map, String.class);
    }

    /**
     * 设置报送信息
     * 
     * @param dto 存款实体类
     * @param prodRegistCode 登记编号
     * @return 报送实体类
     * @see
     * @since 1.0
     */
    public RepTrade setRepTrade(TrdDepositInvestTrade001Dto dto, String prodRegistCode) {
        RepTrade repTrade = new RepTrade();
        repTrade.setRepFinprodRegId(prodRegistCode);
        repTrade.setRepBankTradeCode(dto.getTradeId());
        if (bussCommonMapperDelegateService != null) {
            FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, dto.getFinprodId(),
                FinProduct.class);
            if (finProduct != null) {
                repTrade.setRepBankAssetCode(dto.getFinprodMarketId());
            }
        }
        else {
            repTrade.setRepBankAssetCode(dto.getFinprodId());
        }
        repTrade.setRepStatusDate(iPlatformDate.getSysDate());
        if (dto.getTrdInvestPersonInfo002Dto() != null) {
            repTrade.setRepTradeComponent(dto.getTrdInvestPersonInfo002Dto().getCounterId());
        }
        repTrade.setRepCcy(dto.getCcy());
        repTrade.setRepCount(dto.getPrinAmt());
        repTrade.setRepOccurMoney(dto.getPrinAmt());
        repTrade.setRepMoneyCny(dto.getPrinAmt().add(this.getClosePrice(dto)));
        if (StringUtil.equals(dto.getFinprodType(), DictCodeList.存款类资产类型_存放同业.getValue())
            || StringUtil.equals(dto.getFinprodType2(), DictCodeList.存款类资产类型_存放同业.getValue())) {
            if (StringUtil.equals(dto.getPs(), DictCodeListExt1.平安业务类型_存款类资产买入.getValue())) {
                repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_存入银行存款.getValue());
            }
            else {
                repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_银行存款到期.getValue());
            }

        }
        else {
            if (StringUtil.equals(dto.getPs(), DictCodeListExt1.平安业务类型_存款类资产买入.getValue())) {
                repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_拆放同业和买入返售首期.getValue());
            }
            else {
                repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_拆放同业和买入返售到期.getValue());
            }
        }
        repTrade.setRepMoneyFlowTypeExp("无");
        repTrade.setRepTradeDate(dto.getTradeDate());
        repTrade.setRepTradeComponent(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
        repTrade.setRepRemark(dto.getRemark());
        repTrade.setRepDelCode(dto.getTradeId());
        if (StringUtil.isNotEmpty(repTrade.getRepMoneyCny()) && StringUtil.isNotEmpty(repTrade.getRepCount())) {
            BigDecimal temp = ArithmeticUtil.multiply(ArithmeticUtil.divide(repTrade.getRepMoneyCny(), repTrade.getRepCount(), 4),
                new BigDecimal("100"));
            repTrade.setRepUnitPriceFull(temp);
        }
        repTrade.setRepUnitPriceNet(new BigDecimal("100"));
        return repTrade;
    }

    /**
     * 汇总利率收盘价
     * 
     * @param dto 存款实体类
     * @return 收盘价
     * @see
     * @since 1.0
     */
    public BigDecimal getClosePrice(TrdDepositInvestTrade001Dto dto) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("quotCcy", DictCodeList.币种_人民币_CNY.getValue());
        map.put("basisCcy", dto.getCcy());
        map.put("tradeDate", DateUtil.format(dto.getTradeDate()));
        if (StringUtil.equals(map.get("basisCcy"), DictCodeList.币种_人民币_CNY.getValue())) {
            return new BigDecimal("1");
        }
        else {
            return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getClosePrice",
                map, BigDecimal.class);
        }
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        TrdDepositInvestTrade001Dto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {
//              return commonBusinessService.是否需要上级审批(dto.getCounterId(), dto.getPrinAmt(), LoginUserUtil.getUserName(),
//                  DictCodeList.平安资产类型_存款.getValue(), dto.getFinprodType(), dto.getTradeMarket(), dto.getPs()
//                  // 资产类型改为授权类型 add by renping 2019-08-06  start
//                  , dto.getFinprodId()
//                  // 资产类型改为授权类型 add by renping 2019-08-06  end
//                  );
              Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getPrinAmt(),LoginUserUtil.getUserName(),
                  DictCodeList.平安资产类型_存款.getValue(), dto.getFinprodType(), dto.getTradeMarket(), dto.getPs()
                  , dto.getFinprodId(),entity.getWorkflowId());
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

    @Override
    public void doBack(TrdDepositInvestTrade001Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTradeId());
        
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getCancelRemark())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
        //卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
//        if (entity.getPs().equals(DictCodeList.交易方向_买入.getValue()) || entity.getPs().equals(DictCodeList.交易方向_申购.getValue())) {
//            TrdProductDeal TrdProductDeal = new TrdProductDeal();
//            BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity);
//            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//            TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
//            TrdProductDeal.setCounterId(entity.getTrdInvestPersonInfo002Dto().getCounterId());
//            inputDto.setTrdProductDeal(TrdProductDeal);
//            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
//        }
    }

    @Override
    public void doRevoke(TrdDepositInvestTrade001Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTradeId());
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        //授信释放
        //卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
//        if (entity.getPs().equals(DictCodeList.交易方向_买入.getValue()) || entity.getPs().equals(DictCodeList.交易方向_申购.getValue())) {
//            TrdProductDeal TrdProductDeal = new TrdProductDeal();
//            BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity);
//            TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
//            TrdProductDeal.setCounterId(entity.getTrdInvestPersonInfo002Dto().getCounterId());
//            inputDto.setTrdProductDeal(TrdProductDeal);
//            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
//        }
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getCancelRemark())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
        
        // 调用投管接口 add by cxq 20190214 start
        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        investInterfCommDto.setPageId("TrdDepositInvestTrade002");
        investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
        investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
        investInterfCommDto.setRevokeReason(entity.getCancelRemark());
        investInterfCommDto.setTrdProductDeal(this.setTrdProductDeal(entity));
        investInterfCommDto.setTrdProductDealAdd(this.setTrdProductDealAdd(entity));
        investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
        investInterfCommDto.setTradeFeeDetaillist(inputDto.getListTrdTradeFeeDetail());
        investInterfCommService.doInvestInterf(investInterfCommDto);
        // 调用投管接口 add by cxq 20190214 end
    }

    /**
     * 获取当日应记利息
     * @param map 请求参数
     * @return 当日应记利息
     */
    public BigDecimal getTdyIntincexp(Map<String,Object> map){
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/getTdyIntincexp",
                map, BigDecimal.class);
    }

    /**
     * 验证同一组合同一标的的交易
     * @param dto 请求参数
     * @return 个数
     */
    public Integer checkTradeById(TrdDepositInvestTrade001Dto dto){
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/checkTradeById",
                dto, Integer.class);
    }
    
    /**
     * 验证标的是否做过终止协议交易
     * @param dto 请求参数
     * @return 个数
     */
    public Integer checkIsCleanById(TrdDepositInvestTrade001Dto dto){
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdDepositInvestTrade001/checkIsCleanById",
                dto, Integer.class);
    }
    
    public void fastSave(TrdDepositInvestTrade001Dto dto) {
    	//设置划款指令
        if(dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0 && dto.getBussTrdTransferOrder().get(0) != null && !dto.getBussTrdTransferOrder().get(0).toString().isEmpty()){
            dto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, dto.getBussTrdTransferOrder().get(0));
            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
            String orderId = "";
    		if (baseGetBusinessId != null) {
    			BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
    			bussinessCodeParameter.setSeqNo("9998");
    			orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
    		} else {
    			orderId = IDGenerator.generate();
    		}
    		stlPayOrdersDto.setOrderId(orderId);
            list.add(stlPayOrdersDto);
            dto.setStlPayOrdersList(list);
        }
        
        //数据处理
        dto.setActPortfolioId(dto.getPortfolioId());
        dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));
        TrdDepositInvestTrade001Dto entity = this.setEntity(dto);
        entity.getFinProductTradeDto().setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
        
        for(BaseTradeProductDealDto baseTradeProductDealDto:entity.getFinProductTradeDto().getBaseTradeProductDealDtos()) {
        	trdProductDealMapper.insert(baseTradeProductDealDto.getTrdProductDeal());
        	trdProductDealAddMapper.insert(baseTradeProductDealDto.getTrdProductDealAdd());
            if(baseTradeProductDealDto.getStlPayOrdersList() != null){
                for(StlPayOrders stlPayOrders:baseTradeProductDealDto.getStlPayOrdersList()){
                    stlPayOrdersMapper.insert(stlPayOrders);
                }
            }
            if (entity.getPtlTrade() != null && !entity.getPtlTrade().isEmpty()) {
            	entity.getPtlTrade().forEach(ptlTrade -> ptlTradeMapper.insert(ptlTrade));
            }
            paTrdProductDealMapper.insert(entity.getPaTrdProductDeal());
        }
    }
}
