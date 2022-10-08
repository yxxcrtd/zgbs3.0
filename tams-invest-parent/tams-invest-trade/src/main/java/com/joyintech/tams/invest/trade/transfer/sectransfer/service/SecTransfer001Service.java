package com.joyintech.tams.invest.trade.transfer.sectransfer.service;

import static com.joyintech.tams.constant.DictCodeList.估值来源_中债登_CBC;
import static com.joyintech.tams.constant.DictCodeList.估值来源_中证登_CSD;
import static com.joyintech.tams.constant.DictCodeListExt1.交易场所_上海证券交易所;
import static com.joyintech.tams.constant.DictCodeListExt1.交易场所_深圳证券交易所;
import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_修改;
import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_新建;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.SecPrinSchedule;
import com.joyintech.calculation.bonds.constant.AllocationType;
import com.joyintech.calculation.bonds.constant.Basis;
import com.joyintech.calculation.bonds.constant.CurrIntCalMethod;
import com.joyintech.calculation.bonds.constant.DiscountScheme;
import com.joyintech.calculation.bonds.constant.FlowCashScheme;
import com.joyintech.calculation.bonds.constant.IntFrequency;
import com.joyintech.calculation.bonds.constant.IntRateType;
import com.joyintech.calculation.bonds.constant.NextIntCalMethod;
import com.joyintech.calculation.bonds.constant.SecIntSchCalMethod;
import com.joyintech.calculation.bonds.constant.Variety;
import com.joyintech.calculation.shell.AccurIntCalculator;
import com.joyintech.calculation.shell.AccurIntForSecheduleCalculator;
import com.joyintech.calculation.shell.PriceByYieldCalculator;
import com.joyintech.calculation.shell.YieldByPriceCalculator;
import com.joyintech.calculation.shell.param.AccurIntCalInParameter;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.shell.param.PriceYieldInParameter;
import com.joyintech.calculation.shell.param.PriceYieldOutParameter;
import com.joyintech.calculation.shell.param.SecInfoModel;
import com.joyintech.calculation.shell.param.YieldPriceInParameter;
import com.joyintech.calculation.shell.param.YieldPriceOutParameter;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.system.base.dao.SysUploadFileInfoMapper;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysUploadFileInfo;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.PaTrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.PaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.PtlTradeMapper;
import com.joyintech.tams.common.base.dao.TrdDealTotalAddMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.WflPaTrdFinDealTotalMapper;
import com.joyintech.tams.common.base.dao.WflPaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.WflStlPayOrdersMapper;
import com.joyintech.tams.common.base.dao.WflTrdDealTotalAddMapper;
import com.joyintech.tams.common.base.dao.WflTrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.WflTrdProductDealAddMapper;
import com.joyintech.tams.common.base.dao.WflTrdProductDealMapper;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.PaTrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import com.joyintech.tams.common.base.entity.PtlCapacctInfo;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.RepTrade;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdDealTotalAdd;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.WflPaTrdFinDealTotal;
import com.joyintech.tams.common.base.entity.WflPaTrdProductDeal;
import com.joyintech.tams.common.base.entity.WflStlPayOrders;
import com.joyintech.tams.common.base.entity.WflTrdDealTotalAdd;
import com.joyintech.tams.common.base.entity.WflTrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.common.base.entity.WflTrdProductDealAdd;
import com.joyintech.tams.common.base.entity.WflTrdProductDealKey;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.entrust.dao.EtrTrdCommonMapper;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.trade.bondlend.dto.FinCashPlanDto;
import com.joyintech.tams.invest.trade.bondlend.dto.SecInfoModelDto;
import com.joyintech.tams.invest.trade.transfer.assign.service.TrdAssetAssignService;
import com.joyintech.tams.invest.trade.transfer.common.dto.BondValuationDto;
import com.joyintech.tams.invest.trade.transfer.common.dto.DeviationDegreeDto;
import com.joyintech.tams.invest.trade.transfer.netplantransfer.dto.NetPlanTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.position.service.PositionRequestServiceSecTransfer001DtoRequest;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.PtlFundAllocationDto;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecResoluteDto;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransferDto;
import com.joyintech.tams.position.constant.PositionConstant;
import com.joyintech.tams.position.exception.PositionException;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 债券交易转仓
 *
 * @author zcw
 * @version 1.0 2019年11月21日
 * @see SecTransfer001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SecTransfer001Service extends CommonExportService<SecTransfer001Dto> implements BaseProcessService<SecTransfer001Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(SecTransfer001Service.class);

    /**
     * 通用Mapper共同代理方法
     */
    //@Autowired(required = false)
    //private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    /**
     * 金融产品Mapper
     */
    @Autowired(required = false)
    private FinProductMapper finProductMapper;

    /**
     * 注入业务主键生产类
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    EtrTrdCommonMapper etrTrdCommonMapper;

    //@Autowired
    //private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;

    /**
     * BussTrdTransferOrderService
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired(required = false)
    private SysUploadFileInfoMapper sysUploadFileInfoMapper;

    /**
     * BaseTradeProductDealCommService
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    //@Autowired
    //private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Resource(name = "ptlFundAllocation002ServiceEx")
    private PtlFundAllocation002Service ptlFundAllocation002Service;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * 共通Mapper的通用代理方案
     */
    @Autowired
    private MapperDelegateService mapperDelegateService;

    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 共通组件
     */
    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 业务编号生成类
     */
    @Autowired(required = false)
    public BaseGetBusinessId baseGetBussinessId;

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
    private PositionRequestServiceSecTransfer001DtoRequest positionRequestService;
    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;
    @Autowired
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    /**
     * 获取下拉框
     *
     * @return 市场代码
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getFinprodMarketId(Map<String, Object> map) {
        return simpleRestTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/SecTransfer/getFinprodMarketId",
                map, SelectOptionDto.class);
    }

    /**
     * 获取资产信息
     *
     * @return 金融产品代码
     * @see
     * @since 1.0
     */
    public SecTransfer001Dto selectTradeInfo(Map<String, Object> map) {
        return simpleRestTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/SecTransfer/selectTradeInfo", map,
                SecTransfer001Dto.class);
    }

    /**
     * 获取拆分信息—组合代码
     *
     * @return 联想文本框实体类集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortfolioId(Map<String, Object> map) {
        return simpleRestTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/SecTransfer/getPortfolioId",
                map, SelectOptionDto.class);
    }

    /**
     * 查询组合托管户
     * <功能详细描述>
     *
     * @param map 查询条件
     * @see
     * @since 1.0
     */
    public String getAccountInfo(Map<String, Object> map) {
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/SecTransfer/getAccountInfo", map,
                String.class);
    }

    /**
     * 查询组合券商资金账户
     * <功能详细描述>
     *
     * @param map 查询条件
     * @see
     * @since 1.0
     */
    public String getCounterAcctInfo(Map<String, Object> map) {
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/SecTransfer/getCounterAcctInfo", map,
                String.class);
    }

    /**
     * 转换成 金融产品交易实体
     */
    public TrdProductDeal convertToTPDEntity(SecTransferDto secResoluteDto, SecTransfer001Dto secResolute001Dto) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        //生成转仓交易编号
        if (secResoluteDto.getTradeId() == null || secResoluteDto.getTradeId().isEmpty()) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            //if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), secResolute001Dto.getPs())) {
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
            //}
            // else {
            // codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券卖出);
            //}
            secResoluteDto.setTradeId((baseGetBusinessId != null && baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) != null) ?
                    baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) : IDGenerator.generate().substring(0, 30));
        }
        trdProductDeal.setTradeId(secResoluteDto.getTradeId());
        //净价总额
        trdProductDeal.setCpriceAmt(secResoluteDto.getCpriceAmt());
        //交易份额
        trdProductDeal.setShareAmt(ArithmeticUtil.divide(secResoluteDto.getTradeAmt(), new BigDecimal(100)));
        //交易本金
        trdProductDeal.setPrinAmt(secResoluteDto.getCpriceAmt());
        //利息总额
        trdProductDeal.setIntAmt(secResoluteDto.getIntAmt());
        //全价总额
        trdProductDeal.setFpriceAmt(secResoluteDto.getFpriceAmt());
        //交易金额
        trdProductDeal.setTradeAmt(secResoluteDto.getTradeRepAmt());
        //交易状态
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        //币种
        trdProductDeal.setCcy(secResolute001Dto.getCcy());
        //单位净价
        trdProductDeal.setUnitCprice(secResoluteDto.getUnitCprice());
        //单位全价
        trdProductDeal.setUnitFprice(secResoluteDto.getUnitFprice());
        //单位利息
        trdProductDeal.setUnitInt(secResoluteDto.getUnitInt());
        //单位面值
        trdProductDeal.setParValue(secResoluteDto.getParValue());
        //交易对手,转出组合代码
        trdProductDeal.setCounterId(secResolute001Dto.getInvestPortfolioId());
        trdProductDeal.setPs(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        trdProductDeal.setFinprodId(secResoluteDto.getFinprodId());
        trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        trdProductDeal.setVdate(secResolute001Dto.getVdate());
        trdProductDeal.setMdate(secResolute001Dto.getMdate());
        trdProductDeal.setRemark("债券类资产交易转仓");
        return trdProductDeal;
    }

    /**
     * 转换成 金融产品交易附属表实体
     */
    public TrdProductDealAdd convertToTPDAEntity(SecTransferDto secResoluteDto, SecTransfer001Dto secResolute001Dto) {
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        if (StringUtil.isNotEmpty(secResoluteDto.getTradeId())) {
            trdProductDealAdd.setTradeId(secResoluteDto.getTradeId());
        }
        trdProductDealAdd.setPortfolioId(secResoluteDto.getPortfolioId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        return trdProductDealAdd;
    }

    /**
     * 转换成 金融产品交易扩展表实体
     */
    public PaTrdProductDeal convertToPTPDEntity(SecTransferDto secResoluteDto, SecTransfer001Dto secResolute001Dto) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        if (StringUtil.isNotEmpty(secResoluteDto.getTradeId())) {
            paTrdProductDeal.setTradeId(secResoluteDto.getTradeId());
        }
        paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        //paTrdProductDeal.setDeptCode();交易所属部门
        return paTrdProductDeal;
    }

    /**
     * 转换成 划款指令表实体
     */
    public StlPayOrdersDto convertToSPOEntity(SecTransferDto secResoluteDto, SecTransfer001Dto secResolute001Dto) {
        StlPayOrders stlPayOrders = new StlPayOrders();
    	/*if(StringUtil.isNotEmpty(secResolute001Dto.getTradeId())) {
        	stlPayOrders = this.selectStlPayOrder(secResolute001Dto.getTradeId());
        }*/
        // 后台自动生成划款指令
        BussSettleOrderParameterDto orderParameterDto = new BussSettleOrderParameterDto();
        // 是否汇总交易 = 是否_否
        orderParameterDto.setIsTotalTrade(DictCodeList.是否_否.getValue());

        // 金融产品交易Entity = 上述构造的金融产品交易Entity
        TrdProductDeal orderParameterDeal = new TrdProductDeal();
        // 交易日期 = 画面.交易日
        orderParameterDeal.setTradeDate(secResoluteDto.getTradeDate());
        // 交易方向 = 交易方向_买入
        orderParameterDeal.setPs(secResoluteDto.getTrdProductDeal().getPs());
        // 币种 = 画面.币种
        orderParameterDeal.setCcy(secResolute001Dto.getCcy());
        // 交易金额 
        orderParameterDeal.setTradeAmt(secResoluteDto.getTradeAmt());
        // 交易对手 = 画面.交易对手
        orderParameterDeal.setCounterId(secResoluteDto.getTrdProductDeal().getCounterId());
        orderParameterDto.setDeal(orderParameterDeal);
        // 组合代码 = 画面.组合代码
        orderParameterDto.setPortfolioId(secResoluteDto.getPortfolioId());

        BussTrdTransferOrderDto resultOrderDtos = bussTrdTransferOrderService.generateOrder(orderParameterDto);
        if (resultOrderDtos != null) {
            secResoluteDto.setBussTrdTransferOrder(Arrays.asList(resultOrderDtos));
        }

        if (!StringUtil.isEmptyList(secResoluteDto.getBussTrdTransferOrder())) {
            BussTrdTransferOrderDto bttod = secResoluteDto.getBussTrdTransferOrder().get(0);
            // 交易编号 = 业务编号 ※2生成的交易编号
            bttod.setTradeId(secResoluteDto.getTradeId());
            // 指令状态 = 指令状态_新建
            bttod.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
            // 工作流代码
            bttod.setWorkflowId(secResoluteDto.getWorkflowId());
            stlPayOrders = bttod;
        }
        StlPayOrdersDto spoEntity = new StlPayOrdersDto();
        if (stlPayOrders != null) {
            BeanUtil.copyPropertiesIgnoreNull(spoEntity, stlPayOrders);
            String orderId = null;
            if (baseGetBusinessId != null) {
                BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                bussinessCodeParameter.setSeqNo("9998");
                orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
            } else {
                orderId = IDGenerator.generate();
            }
            spoEntity.setOrderId(orderId);
            spoEntity.setSettleAmt(secResoluteDto.getTradeAmt());
            if (StringUtil.isNotEmpty(secResoluteDto.getTradeId())) {
                spoEntity.setTradeId(secResoluteDto.getTradeId());
            }
        }
        return spoEntity;
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        SecTransfer001Dto dto = this.getBussinessEntity(entity);
        Map<String, Object> variables = new HashMap<>();
        variables.put("needRiskCheck", StringUtil.equals(dto.getRiskFlag(), DictCodeListExt1.是否_是.getValue()));
        return variables;
    }

    @Override
    public void doInsert(SecTransfer001Dto entity, OperationType operationType) {
        // 新建交易
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            this.handleBussFlowData(entity);
            //自动生成银证转账交易
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter(DictCodeList.共通业务类型_资金划拨管理);
            BussinessCodeParameter orderIdparam = new BussinessCodeParameter(DictCodeList.共通业务类型_DVP清算指令管理);
            if (StringUtil.equals("YES", entity.getIsNeedAuto())) {
                for (PtlFundAllocationDto pfa : entity.getPtlFundAllocationDtos()) {
                    this.insertOtherData(pfa);
                    PtlFundAllocationDto ptlFundAllocationDto = new PtlFundAllocationDto();
                    ptlFundAllocationDto.setPortfolioName(selectPortfolioName(entity.getInvestPortfolioId()));
                    Map<String, Object> map = new HashMap<>();
                    map.put("groupId", entity.getInvestPortfolioId());
                    map.put("ccy", entity.getCcy());
                    map.put("portfolioId", entity.getInvestPortfolioId());
                    ptlFundAllocationDto.setMyAcctNo(getCounterAcctInfo(map));
                    ptlFundAllocationDto.setCounterAcctNo(getAccountInfo(map));
                    ptlFundAllocationDto.setSettleDate(pfa.getSettleDate());
                    ptlFundAllocationDto.setSettleAmt(pfa.getSettleAmt());
                    ptlFundAllocationDto.setCcy(pfa.getCcy());
                    ptlFundAllocationDto.setPayType(DictCodeListExt1.指令类型_银证转账_证转银.getValue());
                    ptlFundAllocationDto.setPortfolioId(entity.getInvestPortfolioId());
                    List<BussTrdTransferOrderDto> list = new ArrayList<>();
                    BussTrdTransferOrderDto bussTrdTransferOrderDto = new BussTrdTransferOrderDto();
                    bussTrdTransferOrderDto.setIsNeedSend(DictCodeList.是否_是.getValue());
                    bussTrdTransferOrderDto.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
                    bussTrdTransferOrderDto.setSettleDate(ptlFundAllocationDto.getSettleDate());
                    bussTrdTransferOrderDto.setSettleAmt(ptlFundAllocationDto.getSettleAmt());
                    bussTrdTransferOrderDto.setCcy(ptlFundAllocationDto.getCcy());
                    bussTrdTransferOrderDto.setPayAcctNo(ptlFundAllocationDto.getMyAcctNo());
                    bussTrdTransferOrderDto.setRecAcctNo(ptlFundAllocationDto.getCounterAcctNo());
                    bussTrdTransferOrderDto.setPortfolioId(ptlFundAllocationDto.getPortfolioId());
                    bussTrdTransferOrderDto.setPs(DictCodeListExt1.收支方向_收入.getValue());
                    bussTrdTransferOrderDto.setSettleStatus(DictCodeList.结算状态_已结算.getValue());
                    bussTrdTransferOrderDto.setIsTotalTrade(DictCodeList.是否_否.getValue());
                    bussTrdTransferOrderDto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
                    bussTrdTransferOrderDto.setOrderId(baseGetBussinessId.buildBussinessCode(orderIdparam));
                    ptlFundAllocationDto.setOrderId(bussTrdTransferOrderDto.getOrderId());
                    ptlFundAllocationDto.setTradeId(baseGetBussinessId.buildBussinessCode(bussinessCodeParameter));
                    list.add(bussTrdTransferOrderDto);
                    ptlFundAllocationDto.setBussTrdTransferOrder(list);
                    //ptlFundAllocationDto.setMyAcctNo(ptlFundAllocationDto.getMyAcctNo());
                    this.insertOtherData(ptlFundAllocationDto);
                }
            }
            //增加调用头寸引擎代码start
            try {
                String decisionAct = 系统动作_新建.getValue();
                String systemAct = 系统动作_新建.getValue();
                positionRequestService.sendSecTransfer001DtoRequest(entity, decisionAct, systemAct, DictCodeListExt3.流程节点_流程提交.getValue());
            } catch (PositionException e) {
                Message message = PositionUtil.ajaxErrorReturn(e);
                BaseMap bm = (BaseMap) message.getData();
                if (bm == null) {
                    WorkFlowUtil.showMessage(message.getMessage(), MessageType.错误消息);
                }
                String code = (String) bm.get("code");
                StringBuffer messageStr = (StringBuffer) bm.get("message");
                if (StringUtil.equals(PositionConstant.CODE_BUSI_RK, code)) {
                    WorkFlowUtil.showMessage(messageStr.toString(), MessageType.确认消息);
                }
                if (StringUtil.equals(PositionConstant.CODE_BUSI_YK, code)) {
                    WorkFlowUtil.showMessage(messageStr.toString(), MessageType.错误消息);
                }
            }
            //增加调用头寸引擎代码end
        }
        // 撤销交易
        else {
//            this.doRevokeInsert(entity);
        }


    }

    /**
     * 生成银证转账交易
     *
     * @see
     * @since 1.0
     */
    private void insertOtherData(PtlFundAllocationDto dto) {
        ProcessParameter<PtlFundAllocationDto> processParameter = new ProcessParameter<PtlFundAllocationDto>();
        WorkFlowParameter<PtlFundAllocationDto> params = new WorkFlowParameter<PtlFundAllocationDto>();
        params.setPageId("PtlFundAllocation002");
        params.setElementId("PtlFundAllocation002_btn_save");
        params.setServiceName("PtlFundAllocation002Service");
        params.setBusinessCode(item -> item.getPortfolioId());
        params.setBusinessType(DictCodeList.共通业务类型_资金划拨管理.getValue());
        // 设置流程业务说明, 组合名称 + "(" + 组合名称 + ")" + "-"  + "组合代码" + "("  + "组合代码"+")"
        params.setComment(
                entity -> this.getMessage("PtlFundAllocation001.portfolioId") + "("
                        + entity.getPortfolioId() + ")" + "-"
                        + this.getMessage("PtlFundAllocation001.portfolioName") + "("
                        + entity.getPortfolioName() + ")"
                        + this.getMessage("PtlFundAllocation001.orderId") + "("
                        + entity.getBussTrdTransferOrder().get(0).getOrderId() + ")");
        processParameter.setWorkFlowParameter(params);
        try {
//            ptlFundAllocation002Service.start(processParameter, dto);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(SecTransfer001Dto entity) {
        if (!StringUtil.equals("revoke", entity.getOperateType())
                && !StringUtil.equals("delete", entity.getOperateType())) {
            // 先删除流程中数据
            baseTradeProductDealCommService.delete(entity.getWorkflowId());
            String bTotalTradeId = entity.getZcId();

            // 重新insert
            this.handleBussFlowData(entity);
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(NetPlanTransfer001Dto.class, true);
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


            //保存不需要处理头寸
            if (!StringUtil.equals(entity.getOperateType(), "save")) {
                //增加调用头寸引擎代码start
                String decisionAct = 系统动作_修改.getValue();
                String systemAct = 系统动作_修改.getValue();
                SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
                if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
                    systemAct = 系统动作_新建.getValue();
                    decisionAct = 系统动作_修改.getValue();
                    entity.setbTotalTradeId(bTotalTradeId);
                } else {
                    systemAct = 系统动作_新建.getValue();
                    decisionAct = 系统动作_新建.getValue();
                }
                if (StringUtil.equals("1", RequestUtil.getParam("confirmOk"))) {
                    entity.setSoftSkipMode("Y");
                }
                try {
                    positionRequestService.sendSecTransfer001DtoRequest(entity, decisionAct, systemAct, DictCodeListExt3.流程节点_流程提交.getValue());
                } catch (PositionException e) {
                    Message message = PositionUtil.ajaxErrorReturn(e);
                    if (StringUtil.isNotBlank(message.getMessage())) {
                        WorkFlowUtil.showMessage(message.getMessage(), MessageType.错误消息);
                    } else {
                        if (!StringUtil.equals("1", RequestUtil.getParam("confirmOk"))) {
                            BaseMap bm = (BaseMap) message.getData();
                            StringBuffer messageStr = (StringBuffer) bm.get("message");
                            WorkFlowUtil.showMessage(messageStr.toString(), MessageType.确认消息);
                        }
                    }
                }
                //增加调用头寸引擎代码end
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
        //增加调用头寸引擎代码start
        ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
        if (StringUtil.isNotEmpty(processMark)
                && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
            SecTransfer001Dto entity = getBussinessEntityByWorkflowId(workflowId);
            positionRequestService.sendDelete(entity);
        }
        //增加调用头寸引擎代码end
    }

    @Override
    public void doComplete(SecTransfer001Dto secResolute001Dto, OperationType operationType) {

        for (SecTransferDto entity : secResolute001Dto.getSecResoluteDtoList()) {
            entity.getBaseTradeInfoDtos().forEach(baseTradeInfoDto -> {

                entity.setWorkflowId(secResolute001Dto.getWorkflowId());
                entity.setActPortfolioId(entity.getPortfolioId());
                entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));

                if (null != baseTradeInfoDto.getTrdFinProductDealTotal()) {
                    TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
                    BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, baseTradeInfoDto.getTrdFinProductDealTotal());
                    trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
                }

                if (null != baseTradeInfoDto.getPaTrdFinProductDealTotal()) {
                    PaTrdFinProductDealTotal paTrdFinDealTotal = baseTradeInfoDto.getPaTrdFinProductDealTotal();
                    paTrdFinProductDealTotalMapper.insertSelective(paTrdFinDealTotal);
                }

                if (null != baseTradeInfoDto.getTrdDealTotalAdd()) {
                    TrdDealTotalAdd trdDealTotalAdd = baseTradeInfoDto.getTrdDealTotalAdd();
                    trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
                }

                TrdProductDeal trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, baseTradeInfoDto.getTrdProductDeal());

                TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, baseTradeInfoDto.getTrdProductDealAdd());

                PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, baseTradeInfoDto.getPaTrdProductDeal());

                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
                StlPayOrdersDto stlPayOrders = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, baseTradeInfoDto.getStlPayOrders());
                //wflStlPayOrdersMapper.insertSelective(stlPayOrders);

                //如果中标结果进来 add by gejiaao 2019年8月6日 start
                String tradeId = entity.getTradeId();
                //如果中标结果进来 add by gejiaao 2019年8月6日 end

                if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
                    // 组合交易表Entity -> 组合交易信息表
                    List<PtlTrade> ptlTrades = baseTradeInfoDto.getPtlTrade();
                    if (ptlTrades != null) {
                        ptlTrades.forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
                    }
                    // 批量插入
                    // 费用明细表
                    paTrdProductDealMapper.insertSelective(paTrdProductDeal);
                    // -------add by 裴家俊-----start----2019/03/29------费用明细
                    // modified by wangguoxian 为空的费用不入库 20190806 start
                    // -------add by 裴家俊-----end-------2019/03/29---费用明细
                    FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                    BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
                    baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
                    baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
                    // 划款指令
                    if (stlPayOrders != null) {
                        baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrders));
                    }
                    finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
                    //        委托代码不为空处理委托代码
                    if (StringUtil.isNotEmpty(entity.getTrdProductDealAdd().getEntrustId())) {
                        baseTradeProductDealDto.setEntrustId(entity.getTrdProductDealAdd().getEntrustId());
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

                    // 7.2删除原业务流程表中的交易记录
                    this.deleteBussFlowData(baseTradeInfoDto.getTrdProductDeal().getTradeId());
                    this.deleteBussFlowData1(baseTradeInfoDto.getTrdProductDeal().getTotleTradeId());

                    // 7.3写入交易信息进交易报送信息表，为中债报送用
                    this.dealInfoCbReportLogic_7_3(entity);

                    // 7.4更新资产负债报送状态
                    this.assetDebtCbReportLogic_7_4(entity);

                    basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

                    // 发送Murex接口处理
                    // this.sendMurex(entity, false);
                    // 一体化对应etliu start
                    // 所有交易生效后调用的共通方法
                    AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                    inputDto.setProdid(entity.getPortfolioId());
                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                    inputDto.setTrdProductDeal(trdProductDeal);

                    // 通道交易 新增 强成西 2019/03/26 start
                    inputDto.setTrdProductDealAdd(trdProductDealAdd);
                    inputDto.setTrdTradeFeeDetail(null);
                    // 通道交易 新增 强成西 2019/03/26 end
                    inputDto.setStlPayOrders(stlPayOrders);
                    AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
                    // 一体化对应etliu end
                    trdAssetAssignService.genAssignTrade(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), DictCodeList.平安资产类型_债券.getValue(), entity.getTradeId(), secResolute001Dto.getIsNeedAssign(),
                            secResolute001Dto, entity);
                }
                // 撤销
                else {
                    this.doCompleteRevoke(entity);
                    //如果是撤销将交易ID赋值为null add by gejiaao 2019年8月6日 start
                    tradeId = null;
                    //如果是撤销将交易ID赋值为null add by gejiaao 2019年8月6日 end

                    // 发送Murex接口处理
                    // this.sendMurex(entity, true);
                }
                // 如果中标结果进来 add by gejiaao 2019年8月6日 start
                if (StringUtil.isNotEmpty(entity.getBondFirstSign())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("tradeId", tradeId);
                    map.put("winPrice", entity.getYield());
                    map.put("entrustId", entity.getEntrustId());
                    simpleRestTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/Etrbondbiddingresult/updateTradeId", map, Integer.class);
                }
                // 如果中标结果进来 add by gejiaao 2019年8月6日 end

            });
        }
        //增加调用头寸引擎代码start
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            //新建转仓
            String decisionAct = 系统动作_新建.getValue();
            String systemAct = 系统动作_新建.getValue();
            positionRequestService.sendSecTransfer001DtoRequest(secResolute001Dto, decisionAct, systemAct, DictCodeListExt3.流程节点_审批生效.getValue());
        } else {
            // TODO 转仓撤销  应该是不需要的代码 待确认后删除
            positionRequestService.sendRevoke(secResolute001Dto.getTradeId());
        }
        //增加调用头寸引擎代码end
    }

    //@Override
    public void doComplete2(SecTransfer001Dto secResolute001Dto, OperationType operationType) {

        for (SecTransferDto entity : secResolute001Dto.getSecResoluteDtoList()) {

            entity.setWorkflowId(secResolute001Dto.getWorkflowId());
//    	secResolute001Dto.getSecResoluteDtoList().forEach(entity ->{
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
//            this.prepareEntity (entity, secResolute001Dto);

            // 7.1登录各业务表，包括金融产品交易、金融产品附属、划款指令及附件信息
            // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易表


            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, entity);
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            trdFinProductDealTotal.setPortfolioId(entity.getActPortfolioId());
            trdFinProductDealTotal.setSettleSpeed(0L);
            trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);

            PaTrdFinProductDealTotal paTrdFinDealTotal = entity.getPaTrdFinProductDealTotal();
            paTrdFinProductDealTotalMapper.insertSelective(paTrdFinDealTotal);

            TrdDealTotalAdd trdDealTotalAdd = entity.getTrdDealTotalAdd();
            trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);


            String 反向交易编号 = null;
            if (StringUtil.isBlank(反向交易编号)) {
                if (baseGetBusinessId != null) {
                    BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
                    反向交易编号 = baseGetBusinessId.buildBussinessCode(codeParameter);
                } else {
                    反向交易编号 = IDGenerator.generate().substring(0, 30);
                }
            }
            TrdFinProductDealTotal trdFinProductDealTotal1 = new TrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal1, entity);
            trdFinProductDealTotal1.setTotleTradeId(反向交易编号);
            trdFinProductDealTotal1.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdFinProductDealTotal1.setIsCancel(DictCodeList.是否_否.getValue());
//            trdFinProductDealTotal1.setCounterId(entity.getActPortfolioId());
            trdFinProductDealTotal1.setCounterId(secResolute001Dto.getInvestPortfolioId());
            /*处理反向交易方向*/
            if (DictCodeList.交易方向_买入.getValue().equals(trdFinProductDealTotal1.getPs())) {
                trdFinProductDealTotal1.setPs(DictCodeList.交易方向_卖出.getValue());
            } else if (DictCodeList.交易方向_卖出.getValue().equals(trdFinProductDealTotal1.getPs())) {
                trdFinProductDealTotal1.setPs(DictCodeList.交易方向_买入.getValue());
            }
            //add by yujiaping 2020-12-07 BS-1501  内部转仓转转仓，清算速度不做特殊处理，卖出是什么买入就是什么start
            trdFinProductDealTotal1.setSettleSpeed(0L);
            //add by yujiaping 2020-12-07 BS-1501  内部转仓转转仓，清算速度不做特殊处理，卖出是什么买入就是什么end

            // 上面copyPropertiesIgnoreNull时 错误，重新赋值
            trdFinProductDealTotal1.setInvAim(entity.getTurnInvAim());

//            trdFinProductDealTotal1.setPortfolioId(secResolute001Dto.getInvestPortfolioId());
            trdFinProductDealTotal1.setPortfolioId(entity.getCounterId());
            trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal1);

            PaTrdFinProductDealTotal paTrdFinDealTotal1 = entity.getPaTrdFinProductDealTotal();
            if (DictCodeListExt1.平安业务类型_债券类资产买入.getValue().equals(paTrdFinDealTotal1.getPaBussType())) {
                paTrdFinDealTotal1.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
            } else if (DictCodeListExt1.平安业务类型_债券类资产卖出.getValue().equals(paTrdFinDealTotal1.getPaBussType())) {
                paTrdFinDealTotal1.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
            }
            paTrdFinDealTotal1.setTotleTradeId(反向交易编号);
            paTrdFinProductDealTotalMapper.insertSelective(paTrdFinDealTotal1);

            TrdDealTotalAdd trdDealTotalAdd1 = entity.getTrdDealTotalAdd();
            trdDealTotalAdd1.setTotleTradeId(反向交易编号);
            trdDealTotalAddMapper.insertSelective(trdDealTotalAdd1);

            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity);
//            if(StringUtils.isNotBlank(entity.getfTradeId())){//成交编号
            trdProductDeal.setfTradeId("");
//            }
//            	trdProductDeal.setTradeAmt(entity.getTradeAmt());
            trdProductDeal.setTradeAmt(entity.getTradeRepAmt());
            trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
            trdProductDeal.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());//交易状态
            trdProductDeal.setTotleTradeId(entity.getTotleTradeId());
            trdProductDeal.setIsSwapTransaction(DictCodeList.是否_是.getValue());


            // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属表
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, entity.getTrdProductDealAdd());

            // 金融产品交易扩展表Entity -> 金融产品交易扩展表
            PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, entity.getPaTrdProductDeal());

            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令表
            StlPayOrdersDto stlPayOrders = null;
            if (!StringUtil.isEmptyList(entity.getStlPayOrdersList())) {
                stlPayOrders = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,
                        entity.getStlPayOrdersList().get(0));
            }
            //如果中标结果进来 add by gejiaao 2019年8月6日 start
            String tradeId = entity.getTradeId();
            //如果中标结果进来 add by gejiaao 2019年8月6日 end

            if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
                // 通道交易 新增 强成西 2019/03/26 start
//                TrdTradeFeeDetail trdTradeFeeDetail = null;
//                if (StringUtil.isNotEmpty(trdProductDeal.getChlId())) {
//                    // 交易费用明细
//                    trdTradeFeeDetail = AllTradeCommonUtil.genChannelTradeFeeDetail(entity.getTradeId(),
//                    		entity.getChannelFee(),DictCodeListExt2.收取方式_回款前扣除.getValue(),trdProductDeal.getIsOutTrade());
//                    trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
//                }
                // 通道交易 新增 强成西 2019/03/26 end

                // 组合交易表Entity -> 组合交易信息表
                List<PtlTrade> ptlTrades = entity.getPtlTrade();
                if (ptlTrades != null) {
                    ptlTrades.forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
                }
                // 批量插入
                // 费用明细表
                paTrdProductDealMapper.insertSelective(paTrdProductDeal);
                // -------add by 裴家俊-----start----2019/03/29------费用明细
                // modified by wangguoxian 为空的费用不入库 20190806 start
                // -------add by 裴家俊-----end-------2019/03/29---费用明细
                FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
                baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
                baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
                // 划款指令
                if (stlPayOrders != null) {
                    baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrders));
                }
                finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
                //        委托代码不为空处理委托代码
                if (StringUtil.isNotEmpty(entity.getTrdProductDealAdd().getEntrustId())) {
                    baseTradeProductDealDto.setEntrustId(entity.getTrdProductDealAdd().getEntrustId());
                }

//                gen划款指令_BY_交易(baseTradeProductDealDto,OperationType.NEW.toString());

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

                // 7.2删除原业务流程表中的交易记录
                String workflowId = entity.getWorkflowId();
                if (StringUtil.isBlank(workflowId)) {
                    WflTrdProductDealKey wflTrdProductDealKey = new WflTrdProductDealKey();
                    wflTrdProductDealKey.setTradeId(entity.getTradeId());
                    wflTrdProductDealKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    WflTrdProductDeal wflTrdProductDeal = wflTrdProductDealMapper.selectByPrimaryKey(wflTrdProductDealKey);

                    if (wflTrdProductDeal != null) {
                        workflowId = wflTrdProductDeal.getWorkflowId();
                    }
                }
                this.deleteBussFlowData(entity.getTradeId());
                this.deleteBussFlowData1(entity.getTotleTradeId());

                // 7.3写入交易信息进交易报送信息表，为中债报送用
                this.dealInfoCbReportLogic_7_3(entity);
//
//                // 7.4更新资产负债报送状态
                this.assetDebtCbReportLogic_7_4(entity);

                basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

                // 发送Murex接口处理
                // this.sendMurex(entity, false);
                // 一体化对应etliu start
                // 所有交易生效后调用的共通方法
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(entity.getPortfolioId());
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                inputDto.setTrdProductDeal(trdProductDeal);

                // 通道交易 新增 强成西 2019/03/26 start
                inputDto.setTrdProductDealAdd(trdProductDealAdd);
                inputDto.setTrdTradeFeeDetail(null);
                // 通道交易 新增 强成西 2019/03/26 end

                // 反向交易增加 start by qiangchengxi 2019年3月12日
                TrdProductDeal trdProductDealOut = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDealOut, trdProductDeal);
                //转出交易的投资目的=原债券投资目的  转仓投资目的=转入交易的投资目的
//                trdProductDealOut.setInvAim(secResolute001Dto.getInvAim());
                secResolute001Dto.setTurnInvAim(trdProductDeal.getInvAim());
                trdProductDealOut.setInvAim(entity.getTurnInvAim());
                trdProductDealOut.setTotleTradeId(反向交易编号);

                //trdProductDealOut.setSplitTradeId(trdProductDeal.getTradeId());
                trdProductDealOut.setIsSwapTransaction(DictCodeList.是否_是.getValue());

                trdProductDealOut.setRemark("债券类资产转入交易");
                AllTradeCommonUtil.setDeals(inputDto, trdProductDealOut, trdProductDealAdd, paTrdProductDeal, baseTradeProductDealDto.getStlPayOrdersList(), ptlTrades,
                        entity.getActPortfolioId());
                // 反向交易增加 end by qiangchengxi 2019年3月12日
                //inputDto.getBaseTradeProductDealDtoList().get(0).setActPortfolioId(entity.getActPortfolioId());
                inputDto.setStlPayOrders(stlPayOrders);
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
                // 一体化对应etliu end
                trdAssetAssignService.genAssignTrade(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), DictCodeList.平安资产类型_债券.getValue(), entity.getTradeId(), secResolute001Dto.getIsNeedAssign(),
                        secResolute001Dto, entity);
                //throw new RuntimeException();
            }
            // 撤销
            else {
                this.doCompleteRevoke(entity);
                //如果是撤销将交易ID赋值为null add by gejiaao 2019年8月6日 start
                tradeId = null;
                //如果是撤销将交易ID赋值为null add by gejiaao 2019年8月6日 end

                // 发送Murex接口处理
                // this.sendMurex(entity, true);
            }
            // 如果中标结果进来 add by gejiaao 2019年8月6日 start
            if (StringUtil.isNotEmpty(entity.getBondFirstSign())) {
                Map<String, Object> map = new HashMap<>();
                map.put("tradeId", tradeId);
                map.put("winPrice", entity.getYield());
                map.put("entrustId", entity.getEntrustId());
                simpleRestTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/Etrbondbiddingresult/updateTradeId", map, Integer.class);
            }
            // 如果中标结果进来 add by gejiaao 2019年8月6日 end
//    	});

            //增加调用头寸引擎代码start
            if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
                //新建转仓
                String decisionAct = 系统动作_新建.getValue();
                String systemAct = 系统动作_新建.getValue();
                positionRequestService.sendSecTransfer001DtoRequest(secResolute001Dto, decisionAct, systemAct, DictCodeListExt3.流程节点_审批生效.getValue());
            } else {
                //转仓撤销
                positionRequestService.sendRevoke(entity.getTradeId());
            }
            //增加调用头寸引擎代码end
        }
    }

    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(SecTransfer001Dto entity) {
        if (!StringUtil.isEmptyList(entity.getSecResoluteDtoList())) {
            for (SecTransferDto secResoluteDto : entity.getSecResoluteDtoList()) {
                if (null == secResoluteDto.getBaseTradeInfoDtos()) {  //退回重新建转入组合
                    prepareEntity(secResoluteDto, entity);
                    secResoluteDto.setBaseTradeInfoDtos(genFinProductTradeDto(secResoluteDto, entity));
                }
                secResoluteDto.getBaseTradeInfoDtos().forEach(baseTradeInfoDto -> {
                    if (!StringUtil.equals("submit", entity.getOperateType())) {
                        return;
                    }
                    entity.setActPortfolioId(baseTradeInfoDto.getTrdProductDealAdd().getPortfolioId());
                    entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(baseTradeInfoDto.getTrdProductDealAdd().getPortfolioId()));

                    if (null != baseTradeInfoDto.getTrdFinProductDealTotal()) {
                        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
                        BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, baseTradeInfoDto.getTrdFinProductDealTotal());
                        wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                        wflTrdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
                        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        // TODO
                        wflTrdFinProductDealTotal.setPortfolioId(secResoluteDto.getActPortfolioId());
                        wflTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
                        wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);
                    }

                    if (null != baseTradeInfoDto.getPaTrdFinProductDealTotal()) {
                        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
                        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, baseTradeInfoDto.getPaTrdFinProductDealTotal());
                        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
                        wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);
                    }

                    if (null != baseTradeInfoDto.getTrdDealTotalAdd()) {
                        WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
                        BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, baseTradeInfoDto.getTrdDealTotalAdd());
                        wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        wflTrdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
                        wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);
                    }

                    // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
                    WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baseTradeInfoDto.getTrdProductDeal());
                    wflTrdProductDeal.setfTradeId("");
                    wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                    wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
                    wflTrdProductDeal.setIsSwapTransaction(DictCodeList.是否_是.getValue());

                    //此业务流程表中暂存卖出的投资目的
                    // wflTrdProductDeal.setInvAim(entity.getInvAim());

                    // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
                    WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                    BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseTradeInfoDto.getTrdProductDealAdd());
                    wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());

                    // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
                    WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, baseTradeInfoDto.getPaTrdProductDeal());
                    wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());

                    // 批量插入
                    wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
                    wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
                    wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);

                    // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
                    WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, baseTradeInfoDto.getStlPayOrders());
                    wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                    wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);

                    // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
                    FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    finProductTradeDto.setWorkflowId(entity.getWorkflowId());
                    BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
                    baseTradeProductDealDto.setTrdFinProductDealTotal(baseTradeInfoDto.getTrdFinProductDealTotal());
                    baseTradeProductDealDto.setTrdProductDealAdd(baseTradeInfoDto.getTrdProductDealAdd());
                    TrdProductDeal trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, baseTradeInfoDto.getTrdProductDeal());
                    baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
                    if (null != baseTradeInfoDto.getStlPayOrders()) {
                        StlPayOrdersDto stlPayOrders = new StlPayOrdersDto();
                        BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, baseTradeInfoDto.getStlPayOrders());
                        baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrders));
                    }
                    //        委托代码不为空设置委托编号
                    if (StringUtil.isNotEmpty(secResoluteDto.getEntrustId())) {
                        baseTradeProductDealDto.setEntrustId(secResoluteDto.getTrdProductDealAdd().getEntrustId());
                    }
                    finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
                    baseTradeProductDealCommService.execute(finProductTradeDto);
                    if (StringUtil.isNotEmpty(secResoluteDto.getActPortfolioId()) && !secResoluteDto.getActPortfolioId().equals(secResoluteDto.getPortfolioId())) {
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(secResoluteDto.getActPortfolioId()));
                        baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(secResoluteDto.getPortfolioId()));
                    }
                });

            }
        }
    }

    /**
     * 撤销交易
     *
     * @param entity 参数entity
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unused")
    private void doRevokeInsert(SecResoluteDto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = simpleRestTemplate.getForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
                        + entity.getTradeId(),
                BaseTradeProductDealDto.class);

        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
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
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());

                // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseDto.getTrdProductDealAdd());
                wflTrdProductDealAdd.setTradeId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setProdId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
                wflTrdProductDealAdd.setPortfolioId(baseDto.getActPortfolioId());
                //        推荐方 by zhuyongkang 20191023 start
                wflTrdProductDealAdd.setAssetRecommandOrg(entity.getAssetRecommandOrg());
                //        推荐方 by zhuyongkang 20191023 start

                // 批量插入
                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
                wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);

                // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
                if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
                    for (StlPayOrdersDto stlPayOrdersDto : baseDto.getStlPayOrdersList()) {
                        WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        wflStlPayOrders.setWorkflowId(entity.getWorkflowId());

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
                    }
                }
            }
        }
    }

    public void setDzhUploadFileList(SecResoluteDto entity) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())) {
            for (UploadFile file : entity.getUploadFileList()) {
                SysUploadFileInfo sysUploadFileInfo = sysUploadFileInfoMapper.selectByPrimaryKey(file.getFileNo());
                SysUploadFileInfo fileInfo = new SysUploadFileInfo();
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

    public List<BaseTradeInfoDto> genFinProductTradeDto(SecTransferDto entity, SecTransfer001Dto secResolute001Dto) {
        List<BaseTradeInfoDto> resultList = new ArrayList<>();
        entity.setWorkflowId(secResolute001Dto.getWorkflowId());
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));

        /**
         * 转出
         */
        BaseTradeInfoDto result = new BaseTradeInfoDto();
        result.setTrdProductDeal(gen产品交易(entity, ""));
        result.setTrdProductDealAdd(gen交易附属(entity));
        result.setPtlTrade(gen组合交易(entity, secResolute001Dto));
        result.setStlPayOrders(gen新划款指令流程(entity));
        result.setPaTrdProductDeal(gen交易扩展(entity));
        result.setTrdFinProductDealTotal(gen产品汇总交易(entity, secResolute001Dto));
        result.setPaTrdFinProductDealTotal(生成汇总扩展表(entity));
        result.setTrdDealTotalAdd(生成汇总附属表(entity));

        resultList.add(result);

        String rTradeId = entity.getTradeId();

        /**
         * 买入
         */
        //生成买入汇总交易编号
        entity.setPs(DictCodeList.交易方向_买入.getValue());
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券卖出);
            entity.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            entity.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            entity.setTradeId(IDGenerator.generate().substring(0, 30));
            entity.setTotleTradeId(IDGenerator.generate().substring(0, 30));
        }
        BaseTradeInfoDto result1 = new BaseTradeInfoDto();
        result1.setTrdProductDeal(gen产品交易(entity, rTradeId));
        result1.setTrdProductDealAdd(gen交易附属(entity));
        result1.setPtlTrade(gen组合交易(entity, secResolute001Dto));
        result1.setStlPayOrders(gen新划款指令流程(entity));
        result1.setPaTrdProductDeal(gen交易扩展(entity));
        result1.setTrdFinProductDealTotal(gen产品汇总交易(entity, secResolute001Dto));
        result1.setPaTrdFinProductDealTotal(生成汇总扩展表(entity));
        result1.setTrdDealTotalAdd(生成汇总附属表(entity));

        resultList.add(result1);

        return resultList;
    }

    private TrdFinProductDealTotal gen产品汇总交易(SecTransferDto entity, SecTransfer001Dto secResolute001Dto) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, entity);
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setPortfolioId(entity.getActPortfolioId());
        trdFinProductDealTotal.setSettleSpeed(0L);

        if ("T01".equals(entity.getPs())) {
            trdFinProductDealTotal.setTotleTradeId(entity.getTotleTradeId());
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            trdFinProductDealTotal.setCounterId(secResolute001Dto.getInvestPortfolioId());
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_买入.getValue());
            trdFinProductDealTotal.setSettleSpeed(0L);
            // 上面copyPropertiesIgnoreNull时 错误，重新赋值
            trdFinProductDealTotal.setInvAim(entity.getTurnInvAim());
            trdFinProductDealTotal.setPortfolioId(entity.getCounterId());
        }

        return trdFinProductDealTotal;
    }

    private PaTrdProductDeal gen交易扩展(SecTransferDto dto) {
        // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(dto.getTradeId()); // 交易编号
        paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
        if ("T01".equals(dto.getPs())) {
            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        }
        paTrdProductDeal.setDeptCode("ZYKJ"); // 交易所属部门
        paTrdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
        return paTrdProductDeal;
    }

    private StlPayOrders gen新划款指令流程(SecTransferDto entity) {
        StlPayOrders stlPayOrdersDto = new StlPayOrders();
        BussTrdTransferOrderDto bussTrdTransferOrderDto = entity.getBussTrdTransferOrder().stream().
                collect(Collectors.groupingBy(BussTrdTransferOrderDto::getTradeId)).get(entity.getStlPayId()).get(0);
        BussTrdTransferOrderDto resultOrderDto = new BussTrdTransferOrderDto();
        BeanUtil.copyPropertiesIgnoreNull(resultOrderDto, bussTrdTransferOrderDto);
        if (resultOrderDto == null) {
            return stlPayOrdersDto;
        }
        if ("T01".equals(entity.getPs())) {
            resultOrderDto.setPs(DictCodeListExt1.收付方向_支出.getValue());
            resultOrderDto.setPayType("01");
            resultOrderDto.setPortfolioId(resultOrderDto.getInPortfolioId());
            String orderId = "";
            if (baseGetBussinessId != null) {
                BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                bussinessCodeParameter.setSeqNo("9998");
                orderId = baseGetBussinessId.buildBussinessCode(bussinessCodeParameter);
            } else {
                orderId = IDGenerator.generate();
            }
            resultOrderDto.setOrderId(orderId);
        }
        if (DictCodeList.收付方向_收入.getValue().equals(resultOrderDto.getPs())) {
            resultOrderDto.setIsNeedSend("N");
        }
        resultOrderDto.setTradeId(entity.getTradeId());
        return bussTrdTransferOrderService.save(Arrays.asList(resultOrderDto)).get(0);
    }

    private List<PtlTrade> gen组合交易(SecTransferDto dto, SecTransfer001Dto secResolute001Dto) {
        // 组合交易表Entity 构造开始----------------------------------------------------------
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId()); // 组合代码
        if ("T01".equals(dto.getPs())) {
            ptlTrade.setPortfolioId(dto.getPortfolioIdIn());
        }
        ptlTrade.setBusiNo(dto.getTradeId()); // 业务编号
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
        ptlTrade.setBusiId(dto.getFinprodId()); // 业务明细代码
        ptlTrade.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue()); // 业务类型
        ptlTrade.setInvAim(secResolute001Dto.getInvAim()); // 投资目的
        ptlTrade.setSecManageAcctId(secResolute001Dto.getSecManageAcctId()); // 证券托管户代码
        ptlTrade.setSettleDate(dto.getSettleDate()); // 实际发生日
        ptlTrade.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        ptlTrades.add(ptlTrade);

        if (StringUtil.isNotEmpty(dto.getActPortfolioId()) && !StringUtil.equals(dto.getPortfolioId(), dto.getActPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            BeanUtil.copyPropertiesIgnoreNull(ptlTrade1, ptlTrade);
            ptlTrade1.setPortfolioId(dto.getActPortfolioId());
            ptlTrades.add(ptlTrade1);
        }

        // 组合交易表Entity 构造结束----------------------------------------------------------
        return ptlTrades;
    }

    private TrdProductDealAdd gen交易附属(SecTransferDto dto) {
        // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(dto.getTradeId()); // 交易编号
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        if ("T01".equals(dto.getPs())) {
            trdProductDealAdd.setPortfolioId(dto.getPortfolioIdIn());
        }
        trdProductDealAdd.setEntrustId(dto.getEntrustId()); // 委托代码
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
        trdProductDealAdd.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        trdProductDealAdd.setAssetRecommandOrg(dto.getAssetRecommandOrg()); //推荐方
        // 金融产品交易附属表Entity 构造结束----------------------------------------------------------
        return trdProductDealAdd;
    }

    private TrdProductDeal gen产品交易(SecTransferDto entity, String rTradeId) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity);
        trdProductDeal.setfTradeId("");
        trdProductDeal.setTradeAmt(entity.getTradeRepAmt());
        trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());//交易状态
        trdProductDeal.setTotleTradeId(entity.getTotleTradeId());
        trdProductDeal.setIsSwapTransaction(DictCodeList.是否_是.getValue());

        if ("T01".equals(entity.getPs())) {
            trdProductDeal.setInvAim(entity.getTurnInvAim());
            //trdProductDeal.setTotleTradeId(反向交易编号);
            trdProductDeal.setRemark("债券类资产转入交易");
            trdProductDeal.setrTradeId(rTradeId); // 反向交易编号
            trdProductDeal.setCounterId(entity.getPortfolioId());
        }
        return trdProductDeal;
    }

    /**
     * 由画面输入的参数dto，构筑整个需要反映到数据库的实体
     *
     * @param dto 画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void prepareEntity(SecTransferDto dto, SecTransfer001Dto secResolute001Dto) {

        if (StringUtil.isBlank(dto.getTradeId())) {
            if (baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券卖出);
                dto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
                dto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            } else {
                dto.setTradeId(IDGenerator.generate().substring(0, 30));
                dto.setTotleTradeId(IDGenerator.generate().substring(0, 30));
            }
        }
        dto.setPs(DictCodeList.交易方向_卖出.getValue());
        dto.setTradePlat(dto.getTradeMarket());
        // 券面总额
        dto.setTotalFaceValue(dto.getTradeAmt());

        // 金融产品交易Entity 构造开始----------------------------------------------------------
        // 业务类型
        // 自动生成反向交易 chenzhili start
        dto.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        dto.setIsOutTrade(DictCodeList.是否_否.getValue());
        // 自动生成反向交易 chenzhili end
        // 金融产品类型（估值核算） TODO
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);
        dto.setBranch(branchLong);
        // 单位面值
        dto.setParValue(this.getUnitPrice(dto.getFinprodId(), dto.getSettleDate()));
        // 交易本金 = 画面.净价总额
        dto.setPrinAmt(dto.getCpriceAmt());
        // 交易份额 = 画面.券面总额/100
        dto.setShareAmt(
                ArithmeticUtil.divide(dto.getTotalFaceValue(), new BigDecimal(100), 4, RoundingMode.HALF_UP));
        dto.setTradeAmt(dto.getTradeAmt());
        dto.setRemark("债券类资产转出交易");
        // modified by wangguoxian 费用设置 20190801 start
        // 交易金额 = 画面.交易金额
//        dto.setTradeAmt(dto.getTradeAmtAndFee());
        //---add by 裴家俊-------2019/03/29--------start---------
        /*if(StringUtil.equals(dto.getTradeMarket(), BussinessDictCodeList.交易场所_上海证券交易所.getValue())||StringUtil.equals(dto.getTradeMarket(), BussinessDictCodeList.交易场所_深圳证券交易所.getValue())){
            if(StringUtil.isNotEmpty(dto.getCommissionAmt())){
                if(StringUtil.equals(dto.getPs(), BussinessDictCodeList.交易方向_买入.getValue())){
                    dto.setTradeAmt(ArithmeticUtil.doAdd(dto.getFpriceAmt(),dto.getCommissionAmt()));//全价加上佣金之后为交易金额
                }
                if(StringUtil.equals(dto.getPs(), BussinessDictCodeList.交易方向_卖出.getValue())){
                    dto.setTradeAmt(ArithmeticUtil.doSub(dto.getFpriceAmt(),dto.getCommissionAmt()));//全价减去佣金之后为交易金额
                }
            }
        }*/
        //---add by 裴家俊-------2019/03/29--------end---------
        // modified by wangguoxian 费用设置 20190801 end
        // 到期收益率 = 画面.到期收益率/100
        if (dto.getYield() != null) {
            /*modify by jiangpeng on 20210512 ZGBSSE-258*/
            dto.setYield(ArithmeticUtil.divide(dto.getYield(), new BigDecimal(100)));
            dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), new BigDecimal(100)));
            /*modify by jiangpeng on 20210512 ZGBSSE-258*/
        } else {
            dto.setYield(ArithmeticUtil.divide(dto.getExerYield(), new BigDecimal(100)));
            //add by xuzhihao start
            dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), new BigDecimal(100)));
            //add by xuzhihao end
        }
        // 行权收益率 = 画面.行权收益率/100
   /*    del by xr start
    *  if (dto.getExerYield() != null) {
            dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), new BigDecimal(100)));
        }  del by xr  end*/
        // 是否结清 = 数据字典缓存.是否.是
        dto.setIsClean(DictCodeList.是否_是.getValue());
        dto.setIsCancel(DictCodeList.是否_否.getValue());
        dto.setSecManageAcctId(secResolute001Dto.getSecManageAcctId());
        dto.setChlId(secResolute001Dto.getChlId());

        //TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
        /**
         * 因画面未付初始值，本身是转仓交易，特在此重新赋值 add by xr
         */
        //  personInfo.setTrader(secResolute001Dto.getCreateUser());
//        personInfo.setCounterId(dto.getCounterId());
//        personInfo.setIsOutTrade(dto.getIsOutTrade());
//        personInfo.setCounterTrader(secResolute001Dto.getCreateUser());
//        personInfo.setContactInfo("");
//        if (personInfo != null) {
        // 本方交易员
        dto.setTrader(secResolute001Dto.getCreateUser());
        // 交易对手
//            dto.setCounterId(secResolute001Dto.getInvestPortfolioId());
        /**
         * 转仓功能，转入组合被覆盖，新头寸处查不到，新增portfolioIdIn字段
         */
        dto.setPortfolioIdIn(dto.getPortfolioId());
        dto.setPortfolioId(secResolute001Dto.getInvestPortfolioId());
        dto.setInvAim(secResolute001Dto.getInvAim());
        // 是否外部交易
//            dto.setIsOutTrade(personInfo.getIsOutTrade());
//            if(StringUtil.equals(DictCodeList.是否_否.getValue(), personInfo.getIsOutTrade())){
//            	dto.setIsOutTrade(personInfo.getIsOutTrade());
//            }else{
//            	dto.setIsOutTrade(DictCodeList.是否_是.getValue());
//            }
        // 对手方交易员
        dto.setCounterTrader(secResolute001Dto.getCreateUser());
        // 联系方式
        dto.setCounterContact("");
//        }

        // 清算速度
//        if (StringUtil.equals(DictCodeList.清算速度_T_0.getValue(), dto.getSettleSpeedStr())) {
        dto.setSettleSpeed(0L);
//        } else {
//            dto.setSettleSpeed(1L);
//        }

        // 交易编号、融产品代码、金融产品类型（投管分类）、证券管理户代码、关联系统交易编号、投资目的、交易日、
        // 起息日、到期日、交割日、单位净价、单位利息、单位全价、净价总额、交易场所、交易市场、交易平台、交易币种、交易方向、利息总额、全价总额、备注
        // 以上信息为画面上项目，不需要特殊设置
        // 金融产品交易Entity 构造结束----------------------------------------------------------

        // 金融产品交易附属表Entity 构造开始----------------------------------------------------------
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(dto.getTradeId()); // 交易编号
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId()); // 投资组合代码
        trdProductDealAdd.setEntrustId(dto.getEntrustId()); // 委托代码
        //trdProductDealAdd.setProdId(dto.getFinprodId()); // 理财产品代码
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue()); // 生成方式
        trdProductDealAdd.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        //        推荐方 by zhuyongkang 20191023 start
        trdProductDealAdd.setAssetRecommandOrg(dto.getAssetRecommandOrg());
        //        推荐方 by zhuyongkang 20191023 start
        dto.setTrdProductDealAdd(trdProductDealAdd);
        // 金融产品交易附属表Entity 构造结束----------------------------------------------------------
//        WflTrdProductDealAdd wflTrdProductDealAdd =new WflTrdProductDealAdd();
//        wflTrdProductDealAdd.setTradeId(dto.getTradeId());
//        wflTrdProductDealAdd.setAssetRecommandOrg(dto.getAssetRecommandOrg());
//        dto.setWflTrdProductDealAdd(wflTrdProductDealAdd);
//        wflTrdProductDealAddMapper.insert(dto.getWflTrdProductDealAdd());

        // 组合交易表Entity 构造开始----------------------------------------------------------
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId()); // 组合代码
        ptlTrade.setBusiNo(dto.getTradeId()); // 业务编号
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue()); // 业务表
        ptlTrade.setBusiId(dto.getFinprodId()); // 业务明细代码
        ptlTrade.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue()); // 业务类型
        ptlTrade.setInvAim(secResolute001Dto.getInvAim()); // 投资目的
        ptlTrade.setSecManageAcctId(secResolute001Dto.getSecManageAcctId()); // 证券托管户代码
        ptlTrade.setSettleDate(dto.getSettleDate()); // 实际发生日
        ptlTrade.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        ptlTrades.add(ptlTrade);

        if (StringUtil.isNotEmpty(dto.getActPortfolioId()) && !StringUtil.equals(dto.getPortfolioId(), dto.getActPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            BeanUtil.copyPropertiesIgnoreNull(ptlTrade1, ptlTrade);
            ptlTrade1.setPortfolioId(dto.getActPortfolioId());
            ptlTrades.add(ptlTrade1);
        }

        dto.setPtlTrade(ptlTrades);
        // 组合交易表Entity 构造结束----------------------------------------------------------

        // 划款指令表Entity 构造开始----------------------------------------------------------
        dto.setBussTrdTransferOrder(secResolute001Dto.getBussTrdTransferOrder());
        gen划款指令_BY_交易(dto, dto.getTrdProductDealAdd(), OperationType.NEW.toString());
        /** 临时del  因前端无划款指令信息       del by xr
         dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getTradeId());
         */
        // 划款指令表Entity 构造结束----------------------------------------------------------

        // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(dto.getTradeId()); // 交易编号
        // 平安业务类型(数据字典缓存.平安业务类型（按交易方向取对应值）)
//        if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dto.getPs())) {
        paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
//        } else if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), dto.getPs())) {
//            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
//        }
        // 一体化对应etliu start
//        else if (StringUtil.equals(DictCodeList.交易方向_行权.getValue(), dto.getPs())) {
//        else if (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), dto.getPs())
//                || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), dto.getPs())) {
//            // 一体化对应etliu end
//            paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
//        }
        paTrdProductDeal.setDeptCode("ZYKJ"); // 交易所属部门
        paTrdProductDeal.setWorkflowId(dto.getWorkflowId()); // 工作流代码
        dto.setPaTrdProductDeal(paTrdProductDeal);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
        // 汇总扩展表
        dto.setPaTrdFinProductDealTotal(生成汇总扩展表(dto));
        // 汇总附属表
        dto.setTrdDealTotalAdd(生成汇总附属表(dto));

        // 通道交易 新增 强成西 2019/03/26 start
//        setChannelFee(dto);
        // 通道交易 新增 强成西 2019/03/26 end
    }

    /**
     * 1.8计算单位面值
     *
     * @param finprodId  金融产品代码
     * @param settleDate 交割日
     * @return 单位面值
     * @see
     * @since 1.0
     */
    public BigDecimal getUnitPrice(String finprodId, Date settleDate) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("finprodId", finprodId);
        parameters.put("settleDate", DateUtil.format(settleDate));
        return simpleRestTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getUnitPrice", parameters,
                BigDecimal.class);
    }

    /**
     * 撤销doComplete
     *
     * @param entity 参数
     * @see
     * @since 1.0
     */
    private void doCompleteRevoke(SecTransferDto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = simpleRestTemplate.getForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
                        + entity.getTradeId(),
                BaseTradeProductDealDto.class);

        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                baseDto.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                baseDto.getTrdProductDeal().setCancelRemark(entity.getRevokeReason());
                PaTrdProductDeal paTrdProductDeal = paTrdProductDealMapper.selectByPrimaryKey(baseDto.getTrdProductDeal().getTradeId());
                paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                paTrdProductDeal.setCreateUser(LoginUserUtil.getUserName());
                paTrdProductDeal.setCreateTime(new Date());
                paTrdProductDeal.setUpdateUser(paTrdProductDeal.getCreateUser());
                paTrdProductDeal.setUpdateTime(paTrdProductDeal.getCreateTime());
                paTrdProductDealMapper.insertSelective(paTrdProductDeal);
            }
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtoList);

            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommService.execute(finProductTradeDto);
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId())
                );
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }

            if (entity.getPtlTrade() != null) {
                entity.getPtlTrade().forEach(ptlTrade -> {
                    ptlTrade.setBusiNo(ptlTrade.getBusiNo() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    ptlTradeMapper.insertSelective(ptlTrade);
                });
            }

//            // 一体化对应etliu start
//            // 所有交易生效后调用的共通方法
//            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
//                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//                inputDto.setProdid(baseDto.getTrdProductDealAdd().getPortfolioId());
//                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//                inputDto.setTrdProductDeal(baseDto.getTrdProductDeal());
//                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
//            }
//            // 一体化对应etliu end
        }

        // 删除原业务流程记录
        this.deleteBussFlowData(entity.getTradeId());
        this.deleteBussFlowData1(entity.getTotleTradeId());


        basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

        // 调用基础组件.【交易报送组件】删除交易报送信息
        simpleRestTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepTrade",
                Arrays.asList(entity.getTradeId()), Boolean.class);
        //撤销时额度占用、释放处理  Add by xkni 20190830
        isNeedOccupy(entity, true, true, true);

    }

    /**
     * 删除业务流程数据
     *
     * @see
     * @since 1.0
     */
    private void deleteBussFlowData(String tradeid) {
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "TRADE_ID", tradeid);

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD",
                "TRADE_ID", tradeid);

        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL",
                "TRADE_ID", tradeid);

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_STL_PAY_ORDERS", "TRADE_ID", tradeid);
    }

    /**
     * 删除业务流程数据
     *
     * @see
     * @since 1.0
     */
    private void deleteBussFlowData1(String totaltradeid) {
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_FIN_PRODUCT_DEAL_TOTAL", "TOTLE_TRADE_ID", totaltradeid);
        mapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_FIN_DEAL_TOTAL", "TOTLE_TRADE_ID", totaltradeid);
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_DEAL_TOTAL_ADD", "TOTLE_TRADE_ID", totaltradeid);
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "TOTLE_TRADE_ID", totaltradeid);
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
    private void isNeedOccupy(SecTransferDto entity, boolean isNotBottomflag, boolean isSave, Boolean isdeleteflag) {
        // 所有交易生效后调用的共通方法(入账信息)  撤销后释放占用的授信额度  Add 20190828 by xkni
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        inputDto.setProdid(entity.getPortfolioId());
        inputDto.setProdFinprodType(DictCodeList.共通业务类型_基金类资产交易.getValue());
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());//交易状态
        trdProductDeal.setPs(entity.getPs());//交易方向
        if (StringUtils.isNotBlank(entity.getfTradeId())) {
            trdProductDeal.setfTradeId(entity.getfTradeId());
        }
        trdProductDeal.setTradeId(entity.getTradeId());//交易编号
        trdProductDeal.setFinprodId(entity.getFinprodId());//金融产品代码
        trdProductDeal.setCreateDept(entity.getCreateDept());//创建部门
        trdProductDeal.setCreateUser(entity.getCreateUser());//创建人
        trdProductDeal.setCreateTime(entity.getCreateTime());//创建时间
        trdProductDeal.setUpdateUser(entity.getUpdateUser());//更新人
        trdProductDeal.setUpdateTime(entity.getUpdateTime());//更新时间
        trdProductDeal.setTradeDate(entity.getTradeDate());//交易日期
        trdProductDeal.setShareAmt(entity.getShareAmt());//交易份额，债券数量等
        trdProductDeal.setTradeAmt(entity.getTradeAmt());//交易金额
        trdProductDeal.setPrinAmt(entity.getPrinAmt());//交易本金，债券券面等
        trdProductDeal.setFinprodType(entity.getFinprodType());//金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
        trdProductDeal.setFinprodType2(entity.getFinprodType2());//金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
        TrdInvestPersonInfo002Dto personInfo = entity.getTrdInvestPersonInfo();
        if (personInfo != null) {
            // 交易对手
            trdProductDeal.setCounterId(personInfo.getCounterId());//交易对手编号
        }
        if (!StringUtil.isNotEmpty(trdProductDeal.getShareAmt())) {
            // 交易份额 = 画面.券面总额/100
            trdProductDeal.setShareAmt(ArithmeticUtil.divide(trdProductDeal.getTradeAmt(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        }
        inputDto.setTrdProductDeal(trdProductDeal);
        CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
        ceLinesUpdate.isNeedOccupyCre(inputDto, isNotBottomflag, isSave, isdeleteflag);
    }

    /**
     * 7.3写入交易信息进交易报送信息表，为中债报送用
     *
     * @see
     * @since 1.0
     */
    private void dealInfoCbReportLogic_7_3(SecTransferDto secResoluteDto) {
        // 7.7写入交易信息进交易报送信息表，为中债报送用
        // 1)查询产品登记编码
        String prodRegistCode = simpleRestTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectProdRegistCode/"
                        + secResoluteDto.getPortfolioId(),
                null, String.class);

        if (StringUtil.isBlank(prodRegistCode)) {
            return;
        }

        // 2）写入交易报送信息
        // 交易报送信息Entity
        RepTrade repTrade = new RepTrade();
        repTrade.setRepFinprodRegId(prodRegistCode); // 产品登记编码
        repTrade.setRepBankTradeCode(secResoluteDto.getTradeId()); // 行内交易编码
        // 行内资产/负债编码
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(secResoluteDto.getFinprodId());
        if (finProduct != null) {
            repTrade.setRepBankAssetCode(finProduct.getFinprodMarketId());
        }
        repTrade.setRepCcy(secResoluteDto.getCcy()); // 币种
        repTrade.setRepOccurMoney(secResoluteDto.getTradeAmt()); // 发生金额

        // 折算人民币金额（元） = 画面.全价总额*收盘价(利用画面.币种与交易日查询汇率行情表中最新的收盘价)
        BigDecimal 收盘价 = commonBusinessService.selectClosePrice(secResoluteDto.getCcy(),
                DateUtil.format(secResoluteDto.getTradeDate()));
        BigDecimal 折算人民币金额 = ArithmeticUtil.multiply(secResoluteDto.getTradeAmt(), 收盘价);
        repTrade.setRepMoneyCny(折算人民币金额);
        // 数量 = 画面.券面总额
        repTrade.setRepCount(secResoluteDto.getTradeAmt());

        // 资金流动类型: 数据字典缓存.资金流动类型 ※根据交易方向判断，若买入，即资产买入，若卖出，即为资产卖出或到期
        String 资金流动类型 = DictCodeListExt2.资金流动类型_资产卖出或到期.getValue();
        if (StringUtil.equals(secResoluteDto.getPs(), DictCodeList.交易方向_买入.getValue())) {
            资金流动类型 = DictCodeListExt2.资金流动类型_资产买入.getValue();
        }
        repTrade.setRepMoneyFlowType(资金流动类型);

        repTrade.setRepTradeDate(secResoluteDto.getTradeDate()); // 交易日
        repTrade.setRepTradeComponent(StringUtil.isNotEmpty(secResoluteDto.getCounterId()) ? secResoluteDto.getCounterId() : ""); // 交易对手方
        repTrade.setRepUnitPriceFull(secResoluteDto.getUnitFprice()); // 单位成交价格（全价）
        repTrade.setRepUnitPriceNet(secResoluteDto.getUnitCprice()); // 单位成交价格（净价）
        repTrade.setRepExpireYield(secResoluteDto.getYield().multiply(new BigDecimal(100))); // 到期收益率%
        if (StringUtil.isNotEmpty(secResoluteDto.getfTradeId())) {
            repTrade.setRepDelCode(secResoluteDto.getfTradeId()); // 成交编号/合同号
        } else {
            repTrade.setRepDelCode(secResoluteDto.getTradeId()); // 成交编号/合同号
        }
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue()); // 报送状态
        repTrade.setRepStatusDate(iPlatformDate.getSysDate()); // 数据日期
        repTrade.setRepRemark(secResoluteDto.getRemark()); // 备注

        // 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
        simpleRestTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepTrade",
                Arrays.asList(repTrade), Boolean.class);
    }

    /**
     * 7.4更新资产负债报送状态
     * 利用金融产品代码作为条件把资产负债报送信息表中的报送状态更改为是
     *
     * @param dto 画面参数dto
     * @see
     * @since 1.0
     */
    private void assetDebtCbReportLogic_7_4(SecTransferDto dto) {
        // 利用金融产品代码作为条件把资产负债报送信息表中的报送状态更改为是
        simpleRestTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/updateRepAssetDebt",
                dto.getFinprodId(), Boolean.class);
    }

    /**
     * 流程退回处理
     *
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public void doBack(SecTransfer001Dto secResolute001Dto) {
        secResolute001Dto.getSecResoluteDtoList().forEach(entity -> {
            entity.getBaseTradeInfoDtos().forEach(dto -> {
                //  根据交易编号，删除可用，质押，融入，融出，在途FLOW持仓
                AllTradeCommonUtil.deletePosition("", dto.getTrdProductDeal().getTradeId());
                //卖出是提交最后审核通过后才入庫，所以这里退回不做额度占用处理
                //entity.setPs(DictCodeList.交易方向_买入.getValue());
                //交易方向为买入时，退回动作后 释放原占用的额度  Add by xkni 20190830
                isNeedOccupy(entity, true, true, true);
            });

        });
    }

    /**
     * 1.7计算按钮点击事件
     *
     * @param inputDto 画面参数
     * @return 计算结果
     * @see
     * @since 1.0
     */
    public AjaxResult calculateData(SecTransfer001Dto inputDto) {
        AjaxResult ajaxResult = new AjaxResult();

        // 校验输入参数
        //List<ValidMessage> errorList = this.validCalcAmt(inputDto);
       /* if (!StringUtil.isEmptyList(errorList)) {
            ajaxResult.setErrorList(errorList);
            return ajaxResult;
        }*/

        // 计算逻辑
        this.calcAmt(inputDto);
        ajaxResult.setData(inputDto);

        return ajaxResult;
    }

    /**
     * 1.7计算按钮点击事件
     *
     * @param dto 画面输入参数
     * @see
     * @since 1.0
     */
    public void calcAmt(SecTransfer001Dto dto) {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资产转仓);
        dto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));

        if (DateUtil.compareDate(dto.getSettleDate(), dto.getVdate()) < 0) {
            dto.setSettleDate(dto.getVdate());
        }

        // 如果交易场所为上交所、深交所以外的情况
        boolean isJiaoYiSuo = true;
        if (!StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), dto.getTradeMarket())
                && !StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), dto.getTradeMarket())) {
//            // 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量
//            this.getUnitIntFromValution(dto);
            isJiaoYiSuo = false;
        }

        dto.setPrinAmt(dto.getTradeAmt());

        final int SCALE = 12;

        dto.setYield(ArithmeticUtil.divide(dto.getYield(), 100, 14));
        dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), 100, 14));

        YieldByPriceCalculator yieldByPriceCalculator = new YieldByPriceCalculator();
        PriceByYieldCalculator priceByYieldCalculator = new PriceByYieldCalculator();
        YieldPriceInParameter yieldPriceInParameter = null;
        PriceYieldInParameter priceYieldInParameter = null;
        YieldPriceOutParameter yieldPriceOutParameter = null;
        PriceYieldOutParameter priceYieldOutParameter = null;

        Map<String, Object> tempParamMap = new HashMap<String, Object>();
        tempParamMap.put("finprodId", dto.getFinprodId());

        // b.获取计息区间投资端利率报价信息（债券的利率重置信息）
//         List<BondsIntReset> bondsIntResetList = transactionBond001Mapper.getInvRateQuo(dto);
        List<FinCashCalcRule> finCashCalcRuleList = simpleRestTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/selectBondsIntResetList",
                tempParamMap, FinCashCalcRule.class);

        // c.获取债券的付息计划信息
        List<FinCashPlanDto> finCashPlanList = simpleRestTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getFinCashPlan",
                tempParamMap, FinCashPlanDto.class);
        List<SecIntSchedule> invScheduleList = new ArrayList<>();
        List<FinCashPlanDto> intSchList = new ArrayList<FinCashPlanDto>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(),
                    DictCodeListExt1.现金流一级类型_利息.getValue())
                    && item.getVdateUnadjust() != null) {
                SecIntSchedule schedule = new SecIntSchedule();
                schedule.setTheoryStartDate(toLocalDate(item.getVdateUnadjust()));
                schedule.setTheoryCutoffDate(toLocalDate(item.getMdateUnadjust()));
                schedule.setActualStartDate(toLocalDate(item.getVdate()));
                schedule.setActualCutoffDate(toLocalDate(item.getMdate()));
                schedule.setAmount(item.getIntAmt());
                invScheduleList.add(schedule);
                intSchList.add(item);
            }
        }

        // d.获取债券基本资料信息
        // 计算行权收益率，非行权时，取大于当日的行权日
        tempParamMap.put("ps", dto.getPs());
        SecInfoModelDto secInfoModelDto = simpleRestTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecInfoModel",
                tempParamMap, SecInfoModelDto.class);
        SecInfoModel secInfoModel = this.toSecInfoModel(secInfoModelDto);
        // 如果交易场所为上交所、深交所的情况
        if (isJiaoYiSuo) {
            // 付息债的计息基础使用A_365F
            if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
                secInfoModel.setBasis(Basis.A_365F);
            }
        }

        // e.获取债券的还本计划信息
        List<SecPrinSchedule> secPrinScheduleList = new ArrayList<SecPrinSchedule>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(),
                    DictCodeListExt1.现金流一级类型_本金.getValue())) {
                SecPrinSchedule schedule = new SecPrinSchedule();
                schedule.setAmount(item.getIntAmt());
                schedule.setFlag(
                        StringUtil.equals(item.getIsPayInt(), DictCodeList.是否_是.getValue()));
                schedule.setPrincipal(item.getPrinAmt());
                schedule.setInterestEndDate(toLocalDate(item.getMdate()));
                secPrinScheduleList.add(schedule);
            }
        }
        List<PaperIrReset> paperIrResetList = finCashCalcRuleList.stream().map(item -> {
            PaperIrReset ret = new PaperIrReset();
            // BeanUtil.copyPropertiesIgnoreNull(ret, item);
            ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            ret.setParRate(item.getYield());
            ret.setParRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            return ret;
        }).collect(Collectors.toList());
        if (paperIrResetList != null && paperIrResetList.size() > 0) {
            paperIrResetList.get(0).setBaseRateEffDate(
                    BasicUtil.dateToLocal(secInfoModelDto.getStartDate()));
        }
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            //多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        yieldPriceInParameter = new YieldPriceInParameter();
        yieldPriceInParameter.setCalDate(toLocalDate(dto.getSettleDate()));
        yieldPriceInParameter.setSecInfoModel(secInfoModel);
        yieldPriceInParameter.setSecIntScheduleList(invScheduleList);
        yieldPriceInParameter.setSecPrinScheduleList(secPrinScheduleList);
        yieldPriceInParameter.setPaperIrResetList(paperIrResetList);
        priceYieldInParameter = new PriceYieldInParameter();
        priceYieldInParameter.setCalDate(toLocalDate(dto.getSettleDate()));
        priceYieldInParameter.setSecInfoModel(secInfoModel);
        priceYieldInParameter.setSecIntScheduleList(invScheduleList);
        priceYieldInParameter.setSecPrinScheduleList(secPrinScheduleList);
        priceYieldInParameter.setPaperIrResetList(paperIrResetList);
        yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
            if (isNotFomartSec(intSchList, secInfoModelDto)) {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                priceYieldInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
            } else {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
            }
        }
        yieldPriceInParameter.setCalMethod(secInfoModel.getCalMethod());
        priceYieldInParameter.setCalMethod(secInfoModel.getCalMethod());
        // 后续区间利息计算方案
        if (IntRateType.浮动利率.equals(secInfoModel.getIntRateType())) {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
        } else {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
        }

        // 百元面值应计利息
        BigDecimal accurInt = BigDecimal.ZERO;
        dto.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        // 交易方式为“一级市场”时，单位应计利息=应计利息总额=0
        if (StringUtil.equals(dto.getTradeMarketMode(),
                DictCodeListExt1.交易市场_一级市场.getValue())) {
            dto.setIntAmt(BigDecimal.ZERO);
            dto.setUnitInt(accurInt);
        }
        // 交易方式为“二级市场”时，单位应计利息 =e取得的百元面值应计利息/100*a取得的当前债券面额
        else if (StringUtil.equals(dto.getTradeMarketMode(),
                DictCodeListExt1.交易市场_二级市场.getValue())) {
            //交易所债券特殊处理start----------------------------------------------------------------
            Date tempSettleDate = dto.getSettleDate();
            // 如果交易场所为上交所、深交所的情况
            if (isJiaoYiSuo) {
                // 付息债、贴现债、零息债，交割日+1
                if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())
                        || StringUtil.equals(DictCodeList.息票品种_贴现.getValue(), secInfoModelDto.getBondsType())
                        || StringUtil.equals(DictCodeList.息票品种_零息.getValue(), secInfoModelDto.getBondsType())) {
                    //add by  张年  20190909  start
                    //行权-回售  行权-赎回  交割日不加1
                    if (!StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                            && !StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())) {
                        tempSettleDate = DateUtil.addDay(tempSettleDate, 1);
                    }
                    //add by  张年  20190909  end
                }
            }
            //交易所债券特殊处理end----------------------------------------------------------------

            // a.当前债券面额取得（根据还本计划计算）
            Map<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("finprodId", dto.getFinprodId());
            paraMap.put("settleDate", DateUtil.format(tempSettleDate));
            BigDecimal curSecAmt = simpleRestTemplate.postForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getCurSecAmt",
                    paraMap, BigDecimal.class);

            AccurIntCalInParameter accurIntCalInParameter = new AccurIntCalInParameter();
            // 计算日
            accurIntCalInParameter.setCalDate(this.toLocalDate(tempSettleDate));
            if (paperIrResetList != null && paperIrResetList.size() > 1) {
                //多重利率则计算按浮动利率算
                secInfoModel.setIntRateType(IntRateType.浮动利率);
            }
            // 债券基本资料
            accurIntCalInParameter.setSecInfoModel(secInfoModel);
            // 付息计划
            accurIntCalInParameter.setSecIntScheduleList(invScheduleList);
            // 利率重置信息
            accurIntCalInParameter.setPaperIrResetList(paperIrResetList);

            //交易所债券特殊处理start----------------------------------------------------------------
            AccurIntCalOutParameter out = null;
            // 如果交易场所为上交所、深交所的情况
            if (isJiaoYiSuo) {
                // 付息债、贴现债、零息债，交割日+1
                if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())
                        || StringUtil.equals(DictCodeList.息票品种_贴现.getValue(), secInfoModelDto.getBondsType())
                        || StringUtil.equals(DictCodeList.息票品种_零息.getValue(), secInfoModelDto.getBondsType())) {
                    // 付息计划截止日对应的利息
                    Map<String, BigDecimal> intMap = new HashMap<>();
                    if (null != invScheduleList && !invScheduleList.isEmpty()) {
                        for (SecIntSchedule intSche : invScheduleList) {
                            intMap.put(intSche.getActualCutoffDate().toString(), intSche.getAmount());
                        }
                    }

                    if (intMap.containsKey(accurIntCalInParameter.getCalDate().toString()) &&
                            !((Variety.贴现.equals(secInfoModel.getBondsType())) || Variety.零息.equals(secInfoModel.getBondsType()))) {
                        out = new AccurIntCalOutParameter();
                        out.setInterest(intMap.get(accurIntCalInParameter.getCalDate().toString()).multiply(new BigDecimal("100")
                        ).divide(curSecAmt, 12, RoundingMode.HALF_UP));
                        accurIntCalInParameter.setCalDate(accurIntCalInParameter.getCalDate());
                        AccurIntForSecheduleCalculator calculator2 = CalculatorFactory.createAccurIntForSecheduleCalculator();
                        if (accurIntCalInParameter.getPaperIrResetList() != null && accurIntCalInParameter.getPaperIrResetList().size() > 1) {
                            accurIntCalInParameter.getSecInfoModel().setIntRateType(IntRateType.浮动利率);
                        }
                        out = calculator2.calculate(accurIntCalInParameter);
                    }
                }
            }
            //交易所债券特殊处理end----------------------------------------------------------------

            if (out != null) {
                accurInt = ArithmeticUtil.round(out.getInterest(), SCALE, RoundingMode.HALF_UP);
            } else {
                // 从计算工厂中创建应计利息计算器
                AccurIntCalculator calc = CalculatorFactory.createAccurIntCalculator();
                // 计算利息
                AccurIntCalOutParameter accurIntCalOutParameter = calc.calculate(accurIntCalInParameter);
                accurInt = ArithmeticUtil.round(accurIntCalOutParameter.getInterest(), SCALE, RoundingMode.HALF_UP);
            }

            // 单位应计利息
            if (dto.getUnitInt() == null) {
                if (isJiaoYiSuo) {
                    dto.setUnitInt(ArithmeticUtil.multiply(ArithmeticUtil.divide(accurInt, new BigDecimal(100), 8), curSecAmt));
                } else {
                    dto.setUnitInt(ArithmeticUtil.multiply(ArithmeticUtil.divide(accurInt, new BigDecimal(100), SCALE), curSecAmt));
                }
            }
            // 应计利息总额 = 单位应计利息 * 券面总额/100
            dto.setIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getUnitInt(), dto.getPrinAmt()),
                    new BigDecimal(100), SCALE));
            //add by wl 交易方向选择行权时，应计利息及利息总额为0
            if ((StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), dto.getPs())
                    || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), dto.getPs()))) {
                dto.setIntAmt(BigDecimal.ZERO);
                dto.setUnitInt(BigDecimal.ZERO);
            }
        }
        dto.setCalculateField("unitCprice");
        try {
            // 单位净价
            if (StringUtil.equals(dto.getCalculateField(), "unitCprice")) {
                // 单位全价 = 单位净价 + 单位应计利息
                dto.setUnitFprice(ArithmeticUtil.doAdd(dto.getUnitCprice(), dto.getUnitInt()));
                // 全价总额 = 单位全价 * 券面总额/100
                dto.setFpriceAmt(ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(dto.getUnitFprice(), dto.getPrinAmt()),
                        new BigDecimal(100), SCALE));
                // 净价总额 = 单位净价 * 券面总额/100
                dto.setCpriceAmt(ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
                        new BigDecimal(100), SCALE));
            }
            // 全价总额
          /*  else if (StringUtil.equals(dto.getCalculateField(), "fpriceAmt")) {
                // 单位全价 = 全价总额 * 100 / 券面总额
                dto.setUnitFprice(ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(dto.getFpriceAmt(), new BigDecimal(100)),
                        dto.getPrinAmt(), SCALE));
                // 单位净价 = 单位全价 - 单位应计利息
                dto.setUnitCprice(ArithmeticUtil.doSub(dto.getUnitFprice(), dto.getUnitInt()));
                // 净价总额 = 单位净价 * 券面总额/100
                dto.setCpriceAmt(ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
                        new BigDecimal(100), SCALE));
            }*/
            switch (dto.getCalculateField()) {
                // 单位净价
                case "unitCprice":
                    // 全价总额
                case "fpriceAmt":
                    // 全价总额==>到期收益率
                    yieldPriceInParameter.setFullPrice(
                            dto.getUnitFprice()/* dto.getFpriceAmt() */);
                    yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                            yieldPriceInParameter);
                    dto.setYield(yieldPriceOutParameter.getMatYield());
                    if (secInfoModelDto.getNextExeDate() != null) {
                        // 全价总额==>行权收益率
                        yieldPriceInParameter.setNextExeDate(
                                toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(
                                secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                    yieldPriceInParameter);
                            dto.setExerYield(yieldPriceOutParameter.getExeYield());
                        } else {
                            dto.setExerYield(null);
                        }
                    }
                    break;
                // 到期收益率
                case "yield":
                    // 到期收益率==>全价总额
                    priceYieldInParameter.setMatYield(dto.getYield());
                    priceYieldOutParameter = priceByYieldCalculator.calculate(
                            priceYieldInParameter);
                    // dto.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                    dto.setUnitFprice(priceYieldOutParameter.getFullPrice());
                    // 全价总额==>行权收益率
                    if (secInfoModelDto.getNextExeDate() != null) {
                        yieldPriceInParameter.setFullPrice(
                                dto.getUnitFprice()/* dto.getFpriceAmt() */);
                        yieldPriceInParameter.setNextExeDate(
                                toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(
                                secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                    yieldPriceInParameter);
                            dto.setExerYield(yieldPriceOutParameter.getMatYield());
                        } else {
                            dto.setExerYield(null);
                        }
                    }
                    break;
                // 行权收益率
                case "exerYield":
                    // 行权收益率==>全价总额
                    if (secInfoModelDto.getNextExeDate() != null) {
                        priceYieldInParameter.setMatYield(dto.getYield());
                        yieldPriceInParameter.setNextExeDate(
                                toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(
                                secInfoModelDto.getNextExercisePrice());
                        priceYieldOutParameter = priceByYieldCalculator.calculate(
                                priceYieldInParameter);
                        dto.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                        // 全价总额==>到期收益率
                        yieldPriceInParameter.setFullPrice(dto.getFpriceAmt());
                        yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                yieldPriceInParameter);
                        dto.setYield(yieldPriceOutParameter.getMatYield());
                    }
                    break;
                default:
                    break;
            }
            dto.setFpriceAmt(ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(dto.getUnitFprice(), dto.getPrinAmt()),
                    new BigDecimal(100), SCALE));
            // 单位净价 = 单位全价 - 单位应计利息
            dto.setUnitCprice(ArithmeticUtil.doSub(dto.getUnitFprice(), dto.getUnitInt()));
            // 净价总额 = 单位净价 * 券面总额/100
            dto.setCpriceAmt(ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(dto.getUnitCprice(), dto.getPrinAmt()),
                    new BigDecimal(100), SCALE));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if (dto.getYield() != null) {
            dto.setYield(ArithmeticUtil.multiply(dto.getYield(), new BigDecimal(100)));
        }
        if (dto.getExerYield() != null) {
            dto.setExerYield(ArithmeticUtil.multiply(dto.getExerYield(), new BigDecimal(100)));
        }

    }

    /**
     * 转换数据
     *
     * @param date 日期
     * @return LocalDate
     * @see
     * @since 1.0
     */
    private LocalDate toLocalDate(Date date) {
        if (date != null) {
            return LocalDate.parse(DateUtil.format(date));
        }
        return null;
    }

    /**
     * 转换数据
     *
     * @param secInfoModelDto 债券基本信息SecInfoModelDto
     * @return 债券基本信息SecInfoModel
     * @see
     * @since 1.0
     */
    private SecInfoModel toSecInfoModel(SecInfoModelDto secInfoModelDto) {
        SecInfoModel secInfoModel = new SecInfoModel();
        secInfoModel.setStartDate(toLocalDate(secInfoModelDto.getStartDate()));
        secInfoModel.setEndDate(toLocalDate(secInfoModelDto.getEndDate()));
        secInfoModel.setFirstPayDate(toLocalDate(secInfoModelDto.getFirstPayDate()));
        // 利率类型
        if (StringUtil.equals(DictCodeList.利率类型_固定利率.getValue(),
                secInfoModelDto.getIntRateType())) {
            secInfoModel.setIntRateType(IntRateType.固定利率);
        } else {
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }

        if (StringUtil.isNotBlank(secInfoModelDto.getBasis())) {
            String basis = StringUtil.splitToList(GlobalConstant.COMMA, secInfoModelDto.getBasis()).get(0);
            DictCodeList basisDict = DictCodeList.from(DictCode.计息基础, basis);
            switch (basisDict) {
                case 计息基础_30_360:
                    secInfoModel.setBasis(Basis._30_360);
                    break;
                case 计息基础_A_360:
                    secInfoModel.setBasis(Basis.A_360);
                    break;
                case 计息基础_A_365:
                    secInfoModel.setBasis(Basis.A_365);
                    break;
                case 计息基础_A_365F:
                    secInfoModel.setBasis(Basis.A_365F);
                    break;
                case 计息基础_A_A:
                case 计息基础_A_A_平均分配:
                case 计息基础_A_A_按实际天数分配:
                    secInfoModel.setBasis(Basis.A_A);
                    break;
                default:
                    break;
            }

            if (basisDict.equals(DictCodeList.计息基础_A_A_平均分配)) {
                secInfoModel.setAllocationType(AllocationType.平均分配);
            } else {
                secInfoModel.setAllocationType(AllocationType.按实际天数分配);
            }
        }
        DictCodeList bondsTypeDict = DictCodeList.from(DictCode.息票品种,
                secInfoModelDto.getBondsType());
        switch (bondsTypeDict) {
            case 息票品种_零息:
                secInfoModel.setBondsType(Variety.零息);
                break;
            case 息票品种_利随本清:
                secInfoModel.setBondsType(Variety.利随本清);
                break;
            case 息票品种_贴现:
                secInfoModel.setBondsType(Variety.贴现);
                break;
            case 息票品种_附息:
                secInfoModel.setBondsType(Variety.附息);
                break;
            default:
                break;
        }
        if (StringUtil.equals(DictCodeList.推算方式_起息日向后.getValue(),
                secInfoModelDto.getCalMethod())) {
            secInfoModel.setCalMethod(SecIntSchCalMethod.起息日向后);
        } else if (StringUtil.equals(DictCodeList.推算方式_到期日向前.getValue(),
                secInfoModelDto.getCalMethod())) {
            secInfoModel.setCalMethod(SecIntSchCalMethod.到期日向前);
        } else {
            secInfoModel.setCalMethod(SecIntSchCalMethod.首次付息日);
        }
        if (StringUtil.equals(DictCodeList.频率_月.getValue(),
                secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.月付);
        } else if (StringUtil.equals(DictCodeList.频率_季.getValue(),
                secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.季付);
        } else if (StringUtil.equals(DictCodeList.频率_半年.getValue(),
                secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.半年付);
        }
        // 一体化对应etliu end
        else {
            secInfoModel.setYearIntRate(IntFrequency.年付);
        }

        secInfoModel.setIssuePrice(secInfoModelDto.getIssuePrice());

        return secInfoModel;
    }

    public boolean isNotFomartSec(List<FinCashPlanDto> secIntScheduleList, SecInfoModelDto secInfoModelDto) {
        // 根据【实际起始日】排序付息计划
        Collections.sort(secIntScheduleList, new Comparator<FinCashPlanDto>() {
            @Override
            public int compare(FinCashPlanDto o1, FinCashPlanDto o2) {
                return o1.getVdateUnadjust().compareTo(o2.getVdateUnadjust());
            }
        });
        if (StringUtil.equals(DictCodeList.频率_月.getValue(),
                secInfoModelDto.getYearIntRate())) {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 1);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        } else if (StringUtil.equals(DictCodeList.频率_季.getValue(),
                secInfoModelDto.getYearIntRate())) {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 3);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        } else if (StringUtil.equals(DictCodeList.频率_半年.getValue(),
                secInfoModelDto.getYearIntRate())) {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 6);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        } else {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addYear(she.getVdateUnadjust(), 1);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 获取划款指令list
     *
     * @return 划款指令list
     * @see
     * @since 1.0
     */
    public void gen划款指令_BY_交易(SecTransferDto dto, TrdProductDealAdd trdProductDealAdd, String mode) {
        BussSettleOrderParameterDto param = new BussSettleOrderParameterDto();
        param.setIsTotalTrade(DictCodeList.是否_否.getValue());
        param.setPortfolioId(trdProductDealAdd.getPortfolioId());
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, dto);
// add by zoujiamin  ZGBSSE-256 转仓时划款指令表交易金额取了券面总额，因为dto中交易金额应该是getTradeRepAmt，但复制的时候传入了trade_mount
        trdProductDeal.setTradeAmt(dto.getTradeRepAmt());
// add by zoujiamin  ZGBSSE-256 转仓时划款指令表交易金额取了券面总额，因为dto中交易金额应该是getTradeRepAmt，但复制的时候传入了trade_mount

        param.setDeal(trdProductDeal);
        Map<String, List<BussTrdTransferOrderDto>> bussTrdTransferOrder = dto.getBussTrdTransferOrder().stream().collect(Collectors.groupingBy(BussTrdTransferOrderDto::getTradeId)); //bussTrdTransferOrderService.generateOrder(param);
        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtos = bussTrdTransferOrder.get(dto.getStlPayId());
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
            StlPayOrdersDto stl = gen划款指令流程(bussTrdTransferOrderDtos, trdProductDealAdd, null, dto.getTotleTradeId());
            dto.setStlPayOrdersList(Arrays.asList(stl));
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
                                     String workflowId, String totleTradeId) {
        StlPayOrdersDto stlPayOrders = null;
        for (BussTrdTransferOrderDto dto : stlPayOrdersList) {
            if (dto.getPortfolioId().equals(dealAdd.getPortfolioId())) {
                stlPayOrders = dto;
                stlPayOrders.setOrderStatus(DictCodeList.清算操作类型_新建.getValue());
                //stlPayOrders.setTradeId(totleTradeId);
                stlPayOrders.setWorkflowId(workflowId);
            }
        }
        return stlPayOrders;
    }

    /**
     * 查询净价信息
     *
     * @param pageParam
     * @return
     */
    //add by xuzhihao start
    public BigDecimal findUnitCprice(Map<String, Object> pageParam) {
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/SecTransfer/findUnitCprice", pageParam,
                BigDecimal.class);
    }
    //add by xuzhihao end

    /**
     * 查询组合名称
     *
     * @see
     * @since 1.0
     */
    public String selectPortfolioName(String portfolioId) {
        return simpleRestTemplate.postForObject(ServiceApplication.RISK_SERVICE_NAME + "/SecTransfer/selectPortfolioName", portfolioId, String.class);
    }

    private PaTrdFinProductDealTotal 生成汇总扩展表(SecTransferDto dto) {
        PaTrdFinProductDealTotal productDealTotal = new PaTrdFinProductDealTotal();
        productDealTotal.setTotleTradeId(dto.getTotleTradeId());
        productDealTotal.setPaBussType(getPaBussType(dto.getPs()));
        return productDealTotal;
    }

    private TrdDealTotalAdd 生成汇总附属表(SecTransferDto dto) {
        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
        trdDealTotalAdd.setTotleTradeId(dto.getTotleTradeId());
        trdDealTotalAdd.setOurShareAmt(com.joyintech.framework.util.ArithmeticUtil.divide(dto.getOwnTradeAmt(), 100, 4));
        trdDealTotalAdd.setLendinShareAmt(com.joyintech.framework.util.ArithmeticUtil.divide(dto.getIntegrateTradeAmt(), 100, 4));
        trdDealTotalAdd.setOurFpriceAmt(dto.getOwnFpriceAmt());
        trdDealTotalAdd.setLendinFpriceAmt(dto.getIntegrateFpriceAmt() == null ? BigDecimal.ZERO : dto.getIntegrateFpriceAmt());
        trdDealTotalAdd.setOurCpriceAmt(com.joyintech.framework.util.ArithmeticUtil.subtract(dto.getCpriceAmt(), dto.getIntegrateCpriceAmt()));
        trdDealTotalAdd.setLendinCpriceAmt(dto.getIntegrateCpriceAmt() == null ? BigDecimal.ZERO : dto.getIntegrateCpriceAmt());
        trdDealTotalAdd.setOurFpriceAmt2(dto.getOwnIntAmt());
        trdDealTotalAdd.setLendinFpriceAmt2(dto.getIntegrateIntAmt() == null ? BigDecimal.ZERO : dto.getIntegrateIntAmt());
        trdDealTotalAdd.setAssetRecommandOrg(dto.getAssetRecommandOrg());
        trdDealTotalAdd.setEntrustId(dto.getEntrustId());
        return trdDealTotalAdd;
    }

    private String getPaBussType(String 交易方向) {
        // 平安业务类型(数据字典缓存.平安业务类型（按交易方向取对应值）)
        if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), 交易方向)) {
            return DictCodeListExt1.平安业务类型_债券类资产买入.getValue();
        } else if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), 交易方向)) {
            return DictCodeListExt1.平安业务类型_债券类资产卖出.getValue();
        }
        // 一体化对应etliu start
        // else if (StringUtil.equals(DictCodeList.交易方向_行权.getValue(), 交易方向)) {
        else if (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), 交易方向)
                || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), 交易方向)) {
            // 一体化对应etliu end
            return DictCodeListExt1.平安业务类型_债券类资产行权.getValue();
        }
        return null;
    }

    public int selectData(Map<String, Object> map) {
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/SecTransfer/selectData", map, int.class);
    }

    public int selectYwmsData(Map<String, Object> map) {
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/SecTransfer/selectYwmsData", map, int.class);
    }

    public int selectCpriceData(Map<String, Object> map) {
        return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/SecTransfer/selectCpriceData", map, int.class);
    }

    public TrdProductDeal getOrder(SecTransferDto dto) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, dto);
        trdProductDeal.setTradeId(dto.getTradeId());
        trdProductDeal.setIsOutTrade("N");
        return trdProductDeal;
    }


    public List<TrdProductDeal> generateOrdersYQs(SecTransferDto transferOutDto, List<SecTransferDto> transferInDtoList) {
        List<TrdProductDeal> trdProductDealList = new ArrayList<>();

        List<PtlCapacctInfo> outPayerTypeList = new ArrayList<>();
        if (DictCodeListExt1.交易场所_银行间.getValue().equals(transferOutDto.getTradeMarket()) || DictCodeListExt1.交易场所_商业银行柜台.getValue().equals(transferOutDto.getTradeMarket())) {
            String portfolioId = transferOutDto.getPortfolioId();
            //托管户查询
            outPayerTypeList = etrTrdCommonMapper.getPtlAcctInfo(portfolioId, Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()));

            for (SecTransferDto secTransferDto : transferInDtoList) {
                secTransferDto.setTradeId(secTransferDto.getStlPayId());
                TrdProductDeal trdProductDeal = getOrder(secTransferDto);
                trdProductDeal.setTradeAmt(secTransferDto.getTradeRepAmt());

                List<PtlCapacctInfo> inPayerTypeList = etrTrdCommonMapper.getPtlAcctInfo(secTransferDto.getPortfolioId(), Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()));
                if (outPayerTypeList.size() == 1 && inPayerTypeList.size() == 1 && outPayerTypeList.get(0).getAcctNo().equals(inPayerTypeList.get(0))) {
                    trdProductDeal.setStlPayOrderShow("N");  //※划款指令共通页面调用【业务组件.划款指令共通组件.指令生成】，页面不展示
                } else if (outPayerTypeList.size() == 1 && inPayerTypeList.size() == 1 && outPayerTypeList.get(0).getAcctNo() != inPayerTypeList.get(0).getAcctNo()) {
                    //页面展示, 不做任何处理
                } else {
                    trdProductDeal.setStlPayOrderShow("Y");//页面展示,但是划款账号设置为空
                }
                trdProductDealList.add(trdProductDeal);
            }
        } else if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(transferOutDto.getTradeMarket()) || DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(transferOutDto.getTradeMarket())) {
            for (SecTransferDto secTransferDto : transferInDtoList) {
                secTransferDto.setTradeId(secTransferDto.getStlPayId());
                TrdProductDeal trdProductDeal = getOrder(secTransferDto);
                trdProductDeal.setTradeAmt(secTransferDto.getTradeRepAmt());
                trdProductDeal.setStlPayOrderShow("N"); // 页面不展示
                trdProductDealList.add(trdProductDeal);
            }
        }
        return trdProductDealList;
    }

    public List<TrdProductDeal> generateOrdersNQs(SecTransferDto transferOutDto, List<SecTransferDto> transferInDtoList) {
        List<TrdProductDeal> trdProductDealList = new ArrayList<>();
        //托管户查询(转出账号)
        List<PtlCapacctInfo> ptlCapacctList = etrTrdCommonMapper.getPtlAcctInfo(transferOutDto.getPortfolioId(), Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()));

        for (SecTransferDto secTransferDto : transferInDtoList) {
            String portfolioIdIn = secTransferDto.getPortfolioId();
            //托管户查询(转入账号)
            List<PtlCapacctInfo> ptlCapacctListIn = etrTrdCommonMapper.getPtlAcctInfo(portfolioIdIn, Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()));

            TrdProductDeal trdProductDeal = getOrder(secTransferDto);
            trdProductDeal.setTradeAmt(secTransferDto.getTradeRepAmt());

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

    public DeviationDegreeDto calculateDeviationDegree(DeviationDegreeDto dto) {
        BigDecimal valuation = findValuation(dto);
        if (Objects.nonNull(valuation)) {
            BigDecimal deviationDegree = dto.getUnitPrice().subtract(valuation)
                    .multiply(BigDecimal.valueOf(100)).divide(valuation, 2, RoundingMode.HALF_UP);
            dto.setDeviationDegree(deviationDegree);
        } else {
            dto.setDeviationDegree(new BigDecimal("0.00"));
        }

        return dto;
    }

    public BigDecimal findValuation(DeviationDegreeDto dto) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("finprodId", dto.getFinprodId());
        map.put("tradeDate", iPlatformDate.getSysDateStr());
        if (StringUtil.equals(dto.getTradePlace(), 交易场所_上海证券交易所.getValue())
                || StringUtil.equals(dto.getTradePlace(), 交易场所_深圳证券交易所.getValue())) {
            map.put("valueSource", 估值来源_中证登_CSD.getValue());
        } else {
            map.put("valueSource", 估值来源_中债登_CBC.getValue());
        }
        BondValuationDto valuationDto = simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                + "/SecTransfer/findValuation", map, BondValuationDto.class);
        if (Objects.nonNull(valuationDto) && Objects.nonNull(valuationDto.getValuation())) {
            return valuationDto.getValuation();
        }

        return null;
    }
}
