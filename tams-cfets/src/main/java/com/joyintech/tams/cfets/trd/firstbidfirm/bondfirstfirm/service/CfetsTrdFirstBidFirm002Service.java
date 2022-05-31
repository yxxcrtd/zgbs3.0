package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.dto.CfetsBondPortfolioCashRequest;
import com.joyintech.tams.business.common.dto.CfetsBondPositionRequest;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.BigDecimalUtil;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.position.service.PositionInfoService;
import com.joyintech.tams.cfets.trd.bond.dto.CalRequest;
import com.joyintech.tams.cfets.trd.bond.dto.CalRespone;
import com.joyintech.tams.cfets.trd.bond.dto.PtlAllocationDto;
import com.joyintech.tams.cfets.trd.bond.dto.RiskCalRequest;
import com.joyintech.tams.cfets.trd.bond.service.IbuQuoteBondCalService;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dao.CfetsTrdFirstBidFirm002Mapper;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.*;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trdconfirm.dao.CfetsTrdConfirmMapper;
import com.joyintech.tams.cfets.trdconfirm.service.CfetsTrdConfirmService;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.BasicTradeFeeInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.trade.entrust.EtrFinishStatusDto;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 一级投标确认 新建 SERVICE
 *
 * @author jp
 * @date 2022/05/25
 */
@Service
public class CfetsTrdFirstBidFirm002Service extends BaseService<CfetsTrdFirstBidFirmInfoDto> implements BaseProcessService<CfetsTrdFirstBidFirmInfoDto> {

    private static final Logger logger = LoggerFactory.getLogger(CfetsTrdFirstBidFirm002Service.class);

    /**
     * 平台日期组件接口，提供平台日期的获取和更新
     */
    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private IbuQuoteBondCalService ibuQuoteBondCalService;

    @Autowired(required = false)
    private BaseGetBussinessId baseGetBussinessId;

    /**
     * 公共方法service
     */
    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    /**
     * 投资委托 基础组件
     */
    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoServiceImpl;

    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Autowired
    private CfetsTrdFirstBidFirm002Mapper cfetsTrdFirstBidFirm002Mapper;

    @Autowired
    BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;

    /**
     * 指令信息(流程中)
     */
    @Autowired
    private WflEtrEntrustMapper wflEtrEntrustMapper;

    @Autowired
    private BidOrderMapper bidOrderMapper;

    @Autowired
    private WflBidOrderMapper wflBidOrderMapper;

    @Autowired
    private BidOrderBidPriceMapper bidOrderBidPriceMapper;

    @Autowired
    private WflBidOrderBidPriceMapper wflBidOrderBidPriceMapper;

    @Autowired
    private FinProductIssueMapper finProductIssueMapper;

    @Autowired
    private FinProductMapper finProductMapper;

    @Autowired
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    /**
     * 认购量最小变动单位
     */
    private static final BigDecimal subMinChangeUnit = new BigDecimal(1000);

    /**
     * 金额最小变动单位
     */
    private static final BigDecimal payMinChangeUnit = new BigDecimal("0.01");

    /**
     * 结算服务费最小变动单位
     */
    private static final BigDecimal feeMinChangeUnit = new BigDecimal("0.01");

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private PositionInfoService positionInfoService;

    @Autowired
    private CfetsTrdConfirmService cfetsTrdConfirmService;

    @Autowired(required = false)
    private BaseTradeFeeCommService baseTradeFeeCommService;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;

    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;

    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private TrdTradeFeeTotalMapper trdTradeFeeTotalMapper;

    @Autowired
    private CfetsTrdConfirmMapper cfetsTrdConfirmMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;

    /**
     * <投标确认>界面初始化检查
     *
     * @param request 入参
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/5/16 16:21
     */
    public AjaxResult dealexecutioncheck(CfetsTrdFirstBidFirmInitRequest request) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        Date systemDate = iPlatformDate.getSysDate();
        List<String> entrustIdList = new ArrayList<>();
        logger.debug("自动分配,入参:" + JsonUtils.object2Json(request));
        if (StringUtil.isNotEmpty(request) && StringUtil.isNotEmpty(request.getEntrustDtos())) {
            String finprodId = null;
            String counterId = null;
            Date issueDate = null;
            for (CfetsTrdFirstBidFirmEntrustDto entrustDto : request.getEntrustDtos()) {
                ValidMessage error = new ValidMessage();
                // 资产代码+证券账户必须一致提示信息
                String msg = "";
                // 1.指令状态为已接收且该指令不存在指令流程中数据，不满足弹出提示
                if (!StringUtil.equals(entrustDto.getEntrustStatus(), DictCodeList.委托状态_已接收.getValue())) {
                    errorList.add(new ValidMessage("只可对已接收的指令执行投标确认操作!"));
                }
                WflEtrEntrustKey wflEtrEntrust = wflEtrEntrustMapper.selectByEntrustId(entrustDto.getEntrustId());
                if (StringUtil.isNotEmpty(wflEtrEntrust)) {
                    errorList.add(new ValidMessage("指令存在指令流程中数据不可执行投标确认操作!"));
                }
                // 2.选择多条指令时，资产代码+投标日+交易对手必须一致，
                // 如不满足则提示“所选指令不满足汇总条件：【新债标的OR投标日OR交易对手】不同”，
                // 多个条件不满足则同时显示多个条件。交易对手只判断勾选指令中交易对手字段存在数据的是否一致
                // 新债标的
                if (StringUtil.isEmpty(finprodId)) {
                    finprodId = entrustDto.getFinprodId();
                }
                if (!StringUtil.equals(finprodId, entrustDto.getFinprodId())) {
                    msg = "新债标的";
                }
                // 投标日
                if (issueDate == null) {
                    issueDate = entrustDto.getIssueDate();
                }
                if (DateUtil.compareTo(issueDate, entrustDto.getIssueDate()) != 0) {
                    if (StringUtil.isNotEmpty(msg)) {
                        msg += "OR";
                    }
                    msg += "投标日";
                }
                // 交易对手
                if (StringUtil.isEmpty(counterId)) {
                    counterId = entrustDto.getCounterId();
                }
                if (!StringUtil.equals(counterId, entrustDto.getCounterId())) {
                    if (StringUtil.isNotEmpty(msg)) {
                        msg += "OR";
                    }
                    msg += "交易对手";
                }
                if (StringUtil.isNotEmpty(msg)) {
//                    msg = "【" + msg + "】不同";
                    errorList.add(new ValidMessage(msg));
                    errorList.add(new ValidMessage(getMessage("COMMON_NOTSAME_001", msg)));
                    break;
                }
                entrustIdList.add(entrustDto.getEntrustId());
            }
        }
        if (StringUtil.isNotEmpty(errorList)) {
            ajaxResult.setErrorList(errorList);
        } else {
            // 3.指令不存在流程中或有效的投标方式为自主投标和代投代缴的投标确认数据，不满足弹出提示
            Map<String, Object> param = new HashMap<>();
            param.put("bidModles", Arrays.asList(DictCodeListExt4.投标方式_自主投标.getValue(), DictCodeListExt4.投标方式_代投代缴.getValue()));
            param.put("entrustIdList", entrustIdList);
            if (cfetsTrdFirstBidFirm002Mapper.selectBidFirmCountByEntrustId(param) > 0) {
                errorList.add(new ValidMessage("指令存在流程中或有效的投标方式为自主投标和代投代缴的投标确认数据,不可执行投标确认操作!"));
            }
        }
        if (StringUtil.isNotEmpty(errorList)) {
            ajaxResult.setErrorList(errorList);
        }
        return ajaxResult;
    }

    /**
     * 初始化 <投标确认>界面
     *
     * @param request 指令信息
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/16 16:20
     */
    public CfetsTrdFirstBidFirmInfoDto dealexecutioninit(CfetsTrdFirstBidFirmInitRequest request) {
        logger.debug("初始化<投标确认>界面,入参:" + JsonUtils.object2Json(request));
        CfetsTrdFirstBidFirmInfoDto cfetsTrdFirstBidFirmInfoDto = new CfetsTrdFirstBidFirmInfoDto();
        Map<String, Object> param = new HashMap<>();
        List<String> entrustIdList = new ArrayList<>();
        // 共通业务规则
        request.getEntrustDtos().forEach(item -> {
            entrustIdList.add(item.getEntrustId());
        });
        param.put("entrustIdList", entrustIdList);
        cfetsTrdFirstBidFirmInfoDto = this.dealInitData(param, null);
        logger.debug("初始化<投标确认>界面,结果:" + JsonUtils.object2Json(cfetsTrdFirstBidFirmInfoDto));
        return cfetsTrdFirstBidFirmInfoDto;
    }

    /**
     * 投标方式联动计算
     *
     * @param cfetsTrdFirstBidFirmInfoDto 入参
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/25 10:07
     */
    public CfetsTrdFirstBidFirmInfoDto biddingMethodLink(CfetsTrdFirstBidFirmInfoDto cfetsTrdFirstBidFirmInfoDto) {
        Map<String, Object> param = new HashMap<>();
        List<String> entrustIdList = new ArrayList<>();
        // 共通业务规则
        cfetsTrdFirstBidFirmInfoDto.getProductDtos().forEach(item -> {
            entrustIdList.add(item.getEntrustId());
        });
        param.put("entrustIdList", entrustIdList);
        cfetsTrdFirstBidFirmInfoDto = this.dealInitData(param, cfetsTrdFirstBidFirmInfoDto.getBiddingMethod());
        return cfetsTrdFirstBidFirmInfoDto;
    }

    /**
     * 处理初始化数据
     *
     * @param dto
     * @author jiangpeng
     * @date 2022/5/27 15:59
     */
    private CfetsTrdFirstBidFirmInfoDto dealInitData(Map<String, Object> param, String bidModel) {
        CfetsTrdFirstBidFirmInfoDto cfetsTrdFirstBidFirmInfoDto = new CfetsTrdFirstBidFirmInfoDto();
        this.setParam(param);
        List<CfetsTrdFirstBidFirmInfoDto> list = cfetsTrdFirstBidFirm002Mapper.selectProductsInit(param);
        if (CollectionUtils.isEmpty(list) || list.size() > 1) {
            logger.error("初始化结果数不为1 " + JsonUtils.object2Json(list));
            return null;
        }
        cfetsTrdFirstBidFirmInfoDto = list.get(0);
        if (StringUtil.isEmpty(bidModel)) {
            bidModel = cfetsTrdFirstBidFirm002Mapper.selectBidModelInData(param);
        }
        if (StringUtil.isNotEmpty(bidModel)) {
            cfetsTrdFirstBidFirmInfoDto.setBiddingMethod(bidModel);
        }
        cfetsTrdFirstBidFirmInfoDto.setTradeDate(iPlatformDate.getSysDate());
        //  自主投标和代投代缴
        if (StringUtil.equals(DictCodeListExt4.投标方式_自主投标.getValue(), cfetsTrdFirstBidFirmInfoDto.getBiddingMethod())
                || StringUtil.equals(DictCodeListExt4.投标方式_代投代缴.getValue(), cfetsTrdFirstBidFirmInfoDto.getBiddingMethod())) {
            this.dealInfoSelfOrAgency(cfetsTrdFirstBidFirmInfoDto);
        }
        // 分销买入
        if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), cfetsTrdFirstBidFirmInfoDto.getBiddingMethod())) {
            this.dealInfoDistribution(cfetsTrdFirstBidFirmInfoDto);
        }
        return cfetsTrdFirstBidFirmInfoDto;
    }

    /**
     * 交易信息初始化 自主投标和代投代缴
     *
     * @param dto 入参
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/18 10:23
     */
    private CfetsTrdFirstBidFirmInfoDto dealInfoSelfOrAgency(CfetsTrdFirstBidFirmInfoDto dto) {
        List<CfetsTrdFirstBidFirmProductDto> list = dto.getProductDtos();
        // 投标标的升序
        Comparator<CfetsTrdFirstBidFirmProductDto> byBidPrice = null;
        //招标标的为利率和利差时从小到大排列
        if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue())
                || StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())) {
            byBidPrice = Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getBidPrice);
        } else {
            //招标标的为价格时，从大到小排列
            byBidPrice = Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getBidPrice).reversed();
        }
        // 指令代码升序
        Comparator<CfetsTrdFirstBidFirmProductDto> byEntIdDESC = Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getEntrustId, String.CASE_INSENSITIVE_ORDER);
        // 组合名称升序
        Comparator<CfetsTrdFirstBidFirmProductDto> byPNameDESC = Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getPortfolioName, String.CASE_INSENSITIVE_ORDER);
        // 联合排序
        Comparator<CfetsTrdFirstBidFirmProductDto> finalComparator = byBidPrice.thenComparing(byEntIdDESC).thenComparing(byPNameDESC);
        // 排序
        list = list.stream().sorted(finalComparator).collect(Collectors.toList());
        //缴款方式
        dto.setPayType(this.getPayType(dto.getBiddingMethod()));
        BigDecimal etrBidPriceTotal = BigDecimal.ZERO;
        BigDecimal totalBiddingNum = BigDecimal.ZERO;
        for (CfetsTrdFirstBidFirmProductDto item : list) {
            if (!StringUtil.isNotEmpty(item.getEtrBidPriceTotal())) {
                item.setEtrBidPriceTotal(item.getAvaNum());
            }
            if (!StringUtil.isNotEmpty(item.getTotalBiddingNum())) {
                item.setTotalBiddingNum(item.getEtrBidPriceTotal());
            }
            etrBidPriceTotal = ArithmeticUtil.doAdd(etrBidPriceTotal, item.getEtrBidPriceTotal());
            totalBiddingNum = ArithmeticUtil.doAdd(totalBiddingNum, item.getTotalBiddingNum());
        }
        dto.setEtrBidPriceTotal(etrBidPriceTotal);
        dto.setTotalBiddingNum(totalBiddingNum);
        dto.setProductDtos(list);
        dto.setBidTotalList(this.createBidTotalList(dto));
        return dto;
    }

    /**
     * 交易信息初始化 分销买入
     *
     * @param dto 入参
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/18 10:23
     */
    private CfetsTrdFirstBidFirmInfoDto dealInfoDistribution(CfetsTrdFirstBidFirmInfoDto dto) {
        // 排序
        List<CfetsTrdFirstBidFirmProductDto> list = productDtoSort(dto);
        //缴款方式
        dto.setPayType(this.getPayType(dto.getBiddingMethod()));
        BigDecimal bidPriceTotal = BigDecimal.ZERO;
        BigDecimal etrBidPriceTotal = BigDecimal.ZERO;
        BigDecimal maxBidPrice = null;
        BigDecimal minBidPrice = null;
        String tenderTarget = dto.getTenderTarget();
        for (CfetsTrdFirstBidFirmProductDto productDto : list) {
            // 【券面总额】根据剩余指令额度默认带出，可修改
            productDto.setBidPriceTotal(productDto.getAvaNum());
            bidPriceTotal = ArithmeticUtil.doAdd(bidPriceTotal, productDto.getBidPriceTotal());
            if (!StringUtil.isNotEmpty(productDto.getEtrBidPriceTotal())) {
                productDto.setEtrBidPriceTotal(productDto.getAvaNum());
            }
            etrBidPriceTotal = ArithmeticUtil.doAdd(etrBidPriceTotal, productDto.getEtrBidPriceTotal());
            if (!StringUtil.isNotEmpty(maxBidPrice)) {
                maxBidPrice = productDto.getBidPrice();
            }
            if (!StringUtil.isNotEmpty(minBidPrice)) {
                minBidPrice = productDto.getBidPrice();
            }
            if (BigDecimalUtil.ACompareToB(minBidPrice, productDto.getBidPrice(), 6) > 0) {
                minBidPrice = productDto.getBidPrice();
            }
            if (BigDecimalUtil.ACompareToB(maxBidPrice, productDto.getBidPrice(), 6) < 0) {
                maxBidPrice = productDto.getBidPrice();
            }
        }
        // 【中标标位】根据勾选指令中投标标位情况带出，
        // 【招标标的】为利率和利差时，默认带出最大值，
        // 【招标标的】为价格时，默认带出最小值
        if (StringUtil.equals(tenderTarget, DictCodeListExt4.招标标的_利率.getValue())
                || StringUtil.equals(tenderTarget, DictCodeListExt4.招标标的_利差.getValue())) {
            dto.setWinPrice(maxBidPrice);
        }
        if (StringUtil.equals(tenderTarget, DictCodeListExt4.招标标的_价格.getValue())) {
            dto.setWinPrice(minBidPrice);
        }
        //券面总额
        dto.setBidPriceTotal(bidPriceTotal);
        dto.setEtrBidPriceTotal(etrBidPriceTotal);
        // 净价
        dto.setPrice(this.calPrice(dto));
        dto.setProductDtos(list);
        // 计算
        this.calAmtAndFee(dto, null);
        dto.setIntAmt(BigDecimal.ZERO);
        return dto;
    }

    /**
     * 分销买入产品列表排序
     *
     * @param dto
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmProductDto>
     * @author jiangpeng
     * @date 2022/5/19 11:45
     */
    private List<CfetsTrdFirstBidFirmProductDto> productDtoSort(CfetsTrdFirstBidFirmInfoDto dto) {

        List<CfetsTrdFirstBidFirmProductDto> list = dto.getProductDtos();
        // 投标标的升序
        Comparator<CfetsTrdFirstBidFirmProductDto> byBidPrice = null;
        //招标标的为利率和利差时从小到大排列
        if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue())
                || StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())) {
            byBidPrice = Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getBidPrice);
        }
        //招标标的为价格时，从大到小排列
        if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_价格.getValue())) {
            byBidPrice = Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getBidPrice).reversed();
        }
        // 指令代码升序
        Comparator<CfetsTrdFirstBidFirmProductDto> byEntIdDESC = Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getEntrustId, String.CASE_INSENSITIVE_ORDER);
        // 组合名称升序
        Comparator<CfetsTrdFirstBidFirmProductDto> byPNameDESC = Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getPortfolioName, String.CASE_INSENSITIVE_ORDER);
        // 联合排序
        Comparator<CfetsTrdFirstBidFirmProductDto> finalComparator = byBidPrice.thenComparing(byEntIdDESC).thenComparing(byPNameDESC);
        // 排序
        return list.stream().sorted(finalComparator).collect(Collectors.toList());
    }

    /**
     * 根据投标方式获取缴款方式(一级缴款/一级分销/二级交易)
     * <p>当投标方式为自主投标时，可选项为一级缴款/一级分销；
     * <p>当投标方式为代投代缴时，默认为二级交易，不可修改；
     * <p>当投标方式为分销买入时，默认为一级分销，不可修改；
     *
     * @param biddingMethod 投标方式
     * @return java.lang.String
     * @author jiangpeng
     * @date 2022/5/18 10:17
     */
    private String getPayType(String biddingMethod) {
        if (StringUtil.equals(biddingMethod, DictCodeListExt4.投标方式_代投代缴.getValue())) {
            return BussinessDictCodeList.缴款方式_二级交易.getValue();
        }
        if (StringUtil.equals(biddingMethod, DictCodeListExt4.投标方式_分销买入.getValue())) {
            return BussinessDictCodeList.缴款方式_一级分销.getValue();
        }
        return StringUtil.EMPTY;
    }

    /**
     * 【中标标位】根据勾选指令中投标标位情况带出，
     * <p>【招标标的】为利率和利差时，默认带出最大值，
     * <p>【招标标的】为价格时，默认带出最小值；可修改，修改后联动筛选投标产品列表数据，不展示其中不符合【中标标位】的数据；
     * <p>不符合【中标标位】的数据指
     * <p>招标标的】为利率和利差时，大于【中标标位】的数据；
     * <p>【招标标的】为价格时，小于【中标标位】的数据；
     *
     * @param dto
     * @return java.math.BigDecimal
     * @author jiangpeng
     * @date 2022/5/18 11:01
     */
    private List<CfetsTrdFirstBidFirmProductDto> getWinPrice(CfetsTrdFirstBidFirmInfoDto dto) {
        List<CfetsTrdFirstBidFirmProductDto> productDtos = dto.getProductDtos();
        BigDecimal winPrice = dto.getWinPrice();
        String tenderTarget = dto.getTenderTarget();
        List<CfetsTrdFirstBidFirmProductDto> resultList = new ArrayList<>();
        if (StringUtil.equals(tenderTarget, DictCodeListExt4.招标标的_利率.getValue())
                || StringUtil.equals(tenderTarget, DictCodeListExt4.招标标的_利差.getValue())) {
            BigDecimal finalWinPrice = winPrice;
            productDtos.forEach(productDto -> {
                //【招标标的】为利率和利差时，小于等于【中标标位】的数据
                if (BigDecimalUtil.ACompareToB(productDto.getBidPrice(), finalWinPrice, 6) <= 0) {
                    resultList.add(productDto);
                }
            });
        }
        if (StringUtil.equals(tenderTarget, DictCodeListExt4.招标标的_价格.getValue())) {
            BigDecimal finalWinPrice = winPrice;
            productDtos.forEach(productDto -> {
                //【招标标的】为价格时，大于等于【中标标位】的数据
                if (BigDecimalUtil.ACompareToB(productDto.getBidPrice(), finalWinPrice, 6) >= 0) {
                    resultList.add(productDto);
                }
            });
        }
        return resultList;
    }

    /**
     * 添加或删除产品列表数据
     *
     * @param request
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/20 16:19
     */
    public CfetsTrdFirstBidFirmInfoDto addOrDelList(CfetsTrdFirstBidFirmInfoDto request) {
        logger.debug("添加或删除产品列表数据,入参:" + JsonUtils.object2Json(request));
        CfetsTrdFirstBidFirmInfoDto cfetsTrdFirstBidFirmInfoDto = new CfetsTrdFirstBidFirmInfoDto();
        Map<String, Object> param = new HashMap<>();
        List<String> entrustIdList = new ArrayList<>();
        // 共通业务规则
        //1.查询申购产品列表(获取资产信息,及剩余指令额度)
        request.getEntrustDtos().forEach(item -> {
            entrustIdList.add(item.getEntrustId());
        });
        param.put("entrustIdList", entrustIdList);
        this.setParam(param);
        List<CfetsTrdFirstBidFirmInfoDto> list = cfetsTrdFirstBidFirm002Mapper.selectProductsInit(param);
        if (CollectionUtils.isEmpty(list) || list.size() > 1 || CollectionUtils.isEmpty(entrustIdList)) {
            logger.error("初始化结果数不为1 " + JsonUtils.object2Json(list));
            return request;
        }
        cfetsTrdFirstBidFirmInfoDto = list.get(0);
        request.getProductDtos().addAll(cfetsTrdFirstBidFirmInfoDto.getProductDtos());
        //  自主投标和代投代缴
        if (StringUtil.equals(DictCodeListExt4.投标方式_自主投标.getValue(), request.getBiddingMethod())
                || StringUtil.equals(DictCodeListExt4.投标方式_代投代缴.getValue(), request.getBiddingMethod())) {
            this.dealInfoSelfOrAgency(request);
        }
        // 分销买入
        if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), request.getBiddingMethod())) {
            this.dealInfoDistribution(request);
        }
        request = this.autoAllocation(request);
        request.setEntrustDtos(null);
        logger.debug("添加或删除产品列表数据,结果:" + JsonUtils.object2Json(request));
        return request;
    }

    /**
     * 一级投标确认交易查看
     *
     * @param request
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/16 15:15
     */
    public CfetsTrdFirstBidFirmInfoDto viewPage(CfetsTrdFirstBidFirmInitRequest request) {
        logger.debug("一级投标确认交易查看,入参:" + JsonUtils.object2Json(request));
        if (StringUtil.isEmpty(request.getBidId())) {
            logger.debug("一级投标确认交易查看,交易编号为空");
            return null;
        }
        CfetsTrdFirstBidFirmInfoDto viewInfo = new CfetsTrdFirstBidFirmInfoDto();
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        List<CfetsTrdFirstBidFirmInfoDto> listTmp = cfetsTrdFirstBidFirm002Mapper.viewPage(pageParam);
        if (StringUtil.isNotEmpty(listTmp)) {
            viewInfo = listTmp.get(0);
            viewInfo.setBidTotalList(this.createBidTotalList(viewInfo));
        }
        logger.debug("初始化一级投标确认交易查看页面,结果:" + JsonUtils.object2Json(viewInfo));
        return viewInfo;
    }

    /**
     * 计算 金额信息 净价 结算服务费
     *
     * @param dto
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/20 11:16
     */
    public CfetsTrdFirstBidFirmInfoDto calAmtAndFee(CfetsTrdFirstBidFirmInfoDto dto, BigDecimal price) {
        logger.debug("计算 金额信息,入参:" + JsonUtils.object2Json(dto));
        if (dto.getProductDtos().size() < 1) {
            return dto;
        }
        CalRequest calRequest = new CalRequest();
        calRequest.setFpriceAmt(dto.getBidPriceTotal());
        calRequest.setSettleDate(dto.getTradeDate());
        calRequest.setPs(DictCodeList.交易方向_买入.getValue());
        calRequest.setSecManageAcctId(dto.getProductDtos().get(0).getSecManageAcctId());
        calRequest.setShareAmt(dto.getBidPriceTotal());
        calRequest.setFinprodId(dto.getFinprodId());
        // 当【招标标的】为利率或利差时，如该资产已完成上市流通，则根据【中标标位】计算【中标净价】，可修改
        if (!StringUtil.isNotEmpty(price)) {
            price = this.calPrice(dto);
        }
        dto.setPrice(price);
        // 【结算服务费】，根据银行间债券交易费用规则计算
        List<TrdTradeFeeDetail> trdTradeFeeDetails = this.calFee(calRequest, dto.getRegistOrg(), dto.getTradeMarket(), DictCodeList.平安资产类型_债券.getValue());
        if (CollectionUtils.isNotEmpty(trdTradeFeeDetails)) {
            trdTradeFeeDetails.forEach(item -> {
                if (StringUtil.equals(item.getFeeType(), BussinessDictCodeList.费用类型_结算手续费.getValue())) {
                    dto.setSettFeeAmt(item.getFeeAmt());
                }
            });
        }
        // 【交易金额】 =基本信息.券面总额*金额信息.净价/100
        dto.setTradeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getBidPriceTotal(), dto.getPrice()), new BigDecimal(100), 4));
        // 【应计利息总额】 =基本信息.券面总额*金额信息.应计利息/100
        if (StringUtil.isNotEmpty(dto.getIntAmt())) {
            dto.setIntAmtTatol(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getBidPriceTotal(), dto.getIntAmt()), new BigDecimal(100), 4));
        }
        // 【全价】 =金额信息.净价+金额信息.应计利息
        if (StringUtil.isNotEmpty(dto.getIntAmt())) {
            dto.setFullPrice(ArithmeticUtil.doAdd(dto.getPrice(), dto.getIntAmt()));
        }
        // 【缴款金额】 ==金额信息.交易金额+金额信息.应计利息总额（元）
        if (StringUtil.isNotEmpty(dto.getIntAmtTatol())) {
            dto.setPayableAmount(ArithmeticUtil.doAdd(dto.getTradeAmt(), dto.getIntAmtTatol()));
        }
        logger.debug("计算 金额信息,结果:" + JsonUtils.object2Json(dto));
        return dto;
    }

    /**
     * 计算净价
     *
     * @param dto
     * @return java.math.BigDecimal
     * @author jiangpeng
     * @date 2022/5/25 14:19
     */
    public BigDecimal calPrice(CfetsTrdFirstBidFirmInfoDto dto) {

        CalRequest calRequest = new CalRequest();
        calRequest.setFpriceAmt(dto.getBidPriceTotal());
        calRequest.setSettleDate(dto.getTradeDate());
        calRequest.setPs(DictCodeList.交易方向_买入.getValue());
        calRequest.setSecManageAcctId(dto.getProductDtos().get(0).getSecManageAcctId());
        calRequest.setShareAmt(dto.getBidPriceTotal());
        calRequest.setFinprodId(dto.getFinprodId());
        // 当【招标标的】为利率或利差时，如该资产已完成上市流通，则根据【中标标位】计算【中标净价】，可修改
        calRequest.setCalculateField("02");
        if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())
                || StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue())) {
            //CASE WHEN （标的类金融产品发行信息.招标标的 IN（利率，利差） AND 标的类金融产品.上市流通日期 IS NOT NULL）
            if (StringUtil.isNotEmpty(dto.getCirculationDate())) {
                FinProduct finProduct2 = finProductMapper.selectByFinprodMarketId(dto.getFinprodMarketId());
                calRequest.setFinprodId(finProduct2.getFinprodId());
                //THEN 参照原债券二级交易画面，到期收益率反算净价的算法，计算净价
                if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue())) {
                    // 招标标的=利率时，将中标标位作为到期收益率计算；
                    calRequest.setMatureYield(dto.getWinPrice());
                } else {
                    //招标标的=利差时，取现金流利率信息表.基准编号当天的利率（结合现金流利率信息表.基准利率精度）*现金流利率信息表.系数+投标标位（作为利差）作为到期收益率计算。
                    Map<String, Object> pageParam = new HashMap<>();
                    pageParam.put("finprodId", finProduct2.getFinprodId());
                    pageParam.put("sysDate", iPlatformDate.getSysDateStr());
                    calRequest.setMatureYield(cfetsTrdFirstBidFirm002Mapper.getRate(pageParam));
                }
                CalRespone calRespone = ibuQuoteBondCalService.calAmt(calRequest);
                return calRespone.getPrice();
            } else if (StringUtil.equals(dto.getCouponSpecies(), DictCodeList.息票品种_附息.getValue())
                    || StringUtil.equals(dto.getCouponSpecies(), DictCodeList.息票品种_利随本清.getValue())) {
                //WHEN （标的类金融产品发行信息.招标标的 IN（利率，利差） AND 标的类金融产品.上市流通日期 IS NULL AND 标的类金融产品.息票品种 IN（附息，利随本清））
                //THEN 100
                return BigDecimal.valueOf(100);
            } else {
                //WHEN （标的类金融产品发行信息.招标标的 IN（利率，利差） AND 标的类金融产品.上市流通日期 IS NULL AND 标的类金融产品.息票品种 IN（零息，贴现））
                //THEN NULL
                return null;
            }
        } else if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_价格.getValue())) {
            //WHEN 标的类金融产品发行信息.招标标的 = 价格          THEN MIN(委托投标标位.投标标位)
            // 【净价】
            return dto.getWinPrice();
        }
        return null;
    }

    /**
     * 计算费用
     *
     * @param calRequest
     * @param registOrg
     * @param tradeMarket
     * @param finprodType2
     * @return java.util.List<com.joyintech.tams.common.base.entity.TrdTradeFeeDetail>
     * @author jiangpeng
     * @date 2022/5/25 14:18
     */
    public List<TrdTradeFeeDetail> calFee(CalRequest calRequest, String registOrg, String tradeMarket, String finprodType2) {
        if (StringUtil.isEmpty(finprodType2)) {
            finprodType2 = DictCodeList.平安资产类型_债券.getValue();
        }
        if (StringUtil.isEmpty(tradeMarket)) {
            tradeMarket = BussinessDictCodeList.交易场所_银行间.getValue();
        }
        BasicTradeFeeInputDto inputDto = new BasicTradeFeeInputDto();
        inputDto.setTradeAmt(calRequest.getFpriceAmt());
        inputDto.setTradeDate(calRequest.getSettleDate());
        inputDto.setTradeMarket(tradeMarket);
        inputDto.setFinprodType2(finprodType2);
        inputDto.setPs(calRequest.getPs());// 交易方向
        inputDto.setFpriceAmt(calRequest.getFpriceAmt());// 全价总额
        inputDto.setPrinAmt(calRequest.getShareAmt());// 券面总额
        inputDto.setIsGrade("1");
        inputDto.setRegistOrg(registOrg);
        inputDto.setFeeTypeList(Arrays.asList(BussinessDictCodeList.费用类型_交易手续费.getValue(), BussinessDictCodeList.费用类型_结算手续费.getValue()));
        return baseTradeFeeCommService.innerBankFeeDeal(inputDto);
    }

    /**
     * 自动分配
     *
     * @param autoRequest 画面元素
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdFirstBidFirmProductDto>
     * @author jiangpeng
     * @date 2022/4/21 13:52
     */
    public CfetsTrdFirstBidFirmInfoDto autoAllocation(CfetsTrdFirstBidFirmInfoDto autoRequest) {
        logger.debug("自动分配,入参:" + JsonUtils.object2Json(autoRequest));
        if (StringUtil.isEmpty(autoRequest.getDipType()) && StringUtil.isEmpty(autoRequest.getAdjustStrategy())) {
            logger.debug("自动分配,分配方式和调整策略都为空");
            return autoRequest;
        }
        if (CollectionUtils.isEmpty(autoRequest.getProductDtos())) {
            logger.debug("自动分配,产品列表为空");
            return autoRequest;
        }
        CfetsTrdFirstBidFirmInfoDto dto = new CfetsTrdFirstBidFirmInfoDto();
        if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), autoRequest.getBiddingMethod())) {
            //1.投标方式为分销买入，分配方式选择平均分配时
            if (StringUtil.equals(DictCodeListExt4.组合分配方式_平均分配.getValue(), autoRequest.getDipType())) {
                dto = this.aveDistribution(autoRequest);
            }
            //2.投标方式为分销买入，分配方式选择比例分配时
            if (StringUtil.equals(DictCodeListExt4.组合分配方式_比例分配.getValue(), autoRequest.getDipType())) {
                dto = this.propDistribution(autoRequest);
            }
            this.otherDistribution(dto);
            // 排序
            dto.setProductDtos(this.productDtoSort(dto));
        }
        if (StringUtil.equals(DictCodeListExt4.投标方式_自主投标.getValue(), autoRequest.getBiddingMethod())) {
            //3.投标方式为自主投标或代投代缴，调整策略选择比例分配时
            if (StringUtil.equals(DictCodeListExt3.调整策略_按比例分配.getValue(), autoRequest.getAdjustStrategy())) {
                dto = this.propAdjustStrategy(autoRequest);
            }
            //4.投标方式为自主投标或代投代缴，调整策略选择价格优先分配时
            if (StringUtil.equals(DictCodeListExt3.调整策略_价格优先分配.getValue(), autoRequest.getAdjustStrategy())) {
                dto = this.priceAdjustStrategy(autoRequest);
            }
            dto.setBidTotalList(this.createBidTotalList(dto));
        }
        logger.debug("自动分配,结果:" + JsonUtils.object2Json(dto));
        return dto;
    }

    /**
     * 自动分配-按比例分配
     *
     * @param dto
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/19 11:10
     */
    private CfetsTrdFirstBidFirmInfoDto propDistribution(CfetsTrdFirstBidFirmInfoDto dto) {
        // 自动计算的计算结果需要为最小变动单位1000的整数倍，且每条数据的券面总额须小于等于剩余指令额度
        BigDecimal minChangeUnit = subMinChangeUnit;
        //指令剩余额度汇总
        BigDecimal avaNumTotal = dto.getProductDtos().stream().map(CfetsTrdFirstBidFirmProductDto::getAvaNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 剩余尾差
        BigDecimal tailDifference = dto.getBidPriceTotal();
        int index = 0;
        for (CfetsTrdFirstBidFirmProductDto item : dto.getProductDtos()) {
            index++;
            // 占比值 根据每条数据的“剩余指令额度/指令剩余额度汇总*基本信息中券面总额”计算
            BigDecimal proportion = ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getAvaNum(), dto.getBidPriceTotal()), avaNumTotal, 4);
            proportion = ArithmeticUtil.multiply(ArithmeticUtil.divide(proportion, minChangeUnit, 0, RoundingMode.HALF_DOWN), minChangeUnit);
            // 记录剩余指令额度
            item.setAvaNumTemp(item.getAvaNum());
            // 最后一位时
            if (index == dto.getProductDtos().size()) {
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 6) >= 0
                        && BigDecimalUtil.ACompareToB(proportion, tailDifference, 6) >= 0) {
                    // 剩余指令额度大于尾差且尾差小于占比值时 用倒减法求值
                    item.setBidPriceTotal(tailDifference);
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), proportion, 6) >= 0
                        && BigDecimalUtil.ACompareToB(proportion, tailDifference, 6) < 0) {
                    // 剩余指令额度大于占比值且尾差大于占比值时
                    item.setBidPriceTotal(proportion);
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), proportion, 6) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 6) < 0) {
                    // 剩余指令额度小于占比值且剩余指令额度小于尾差时
                    item.setBidPriceTotal(item.getAvaNumTemp());
                }
            } else {// 每条数据的券面总额须小于等于剩余指令额度
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), proportion, 6) >= 0) {
                    item.setBidPriceTotal(proportion);
                } else {
                    item.setBidPriceTotal(item.getAvaNumTemp());
                }
            }
            item.setAvaNumTemp(ArithmeticUtil.subtract(item.getAvaNumTemp(), item.getBidPriceTotal()));
            tailDifference = ArithmeticUtil.subtract(tailDifference, item.getBidPriceTotal());
        }
        // 处理尾差,最小变动单位序列分配算法
        this.handleTailDifferenceSubscriptionAmount(dto.getProductDtos(), tailDifference, minChangeUnit);
        return dto;
    }

    /**
     * 自动分配-平均分配
     *
     * @param dto
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/19 11:11
     */
    private CfetsTrdFirstBidFirmInfoDto aveDistribution(CfetsTrdFirstBidFirmInfoDto dto) {
        // 自动计算的计算结果需要为最小变动单位1000的整数倍，且每条数据的券面总额须小于等于剩余指令额度
        BigDecimal minChangeUnit = subMinChangeUnit;
        // 平均值计算
        BigDecimal avg = ArithmeticUtil.divide(dto.getBidPriceTotal(), dto.getProductDtos().size(), 4);
        avg = ArithmeticUtil.multiply(ArithmeticUtil.divide(avg, minChangeUnit, 0, RoundingMode.HALF_DOWN), minChangeUnit);
//        // 剩余尾差
        BigDecimal tailDifference = dto.getBidPriceTotal();
        int index = 0;
        for (CfetsTrdFirstBidFirmProductDto item : dto.getProductDtos()) {
            index++;
            // 记录剩余指令额度
            item.setAvaNumTemp(item.getAvaNum());
            // 最后一位时
            if (index == dto.getProductDtos().size()) {
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 6) >= 0
                        && BigDecimalUtil.ACompareToB(avg, tailDifference, 6) >= 0) {
                    // 剩余指令额度大于尾差且尾差小于平均值时 用倒减法求值
                    item.setBidPriceTotal(tailDifference);
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), avg, 6) >= 0
                        && BigDecimalUtil.ACompareToB(avg, tailDifference, 6) < 0) {
                    // 剩余指令额度大于平均值且尾差大于平均值时
                    item.setBidPriceTotal(avg);
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), avg, 6) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 6) < 0) {
                    // 剩余指令额度小于平均值且剩余指令额度小于尾差时
                    item.setBidPriceTotal(item.getAvaNumTemp());
                }
            } else {// 每条数据的券面总额须小于等于剩余指令额度
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), avg, 6) >= 0) {
                    item.setBidPriceTotal(avg);
                } else {
                    item.setBidPriceTotal(item.getAvaNumTemp());
                }
            }
            item.setAvaNumTemp(ArithmeticUtil.subtract(item.getAvaNumTemp(), item.getBidPriceTotal()));
            tailDifference = ArithmeticUtil.subtract(tailDifference, item.getBidPriceTotal());
        }
        // 处理尾插,最小变动单位序列分配算法
        this.handleTailDifferenceSubscriptionAmount(dto.getProductDtos(), tailDifference, minChangeUnit);
        return dto;
    }

    /**
     * 调整策略-比例分配
     * <p>重新计算以下字段：
     * <p>投标产品列表：
     * <p>汇总投标券面总额		=	该行指令投标券面总额/基本信息.指令投标券面总额*基本信息.汇总投标券面总额，计算结果按最小变动单位的整数倍向下取整，尾差采用最小变动单位序列分配算法
     * <p>尾差算法：将投标列表中数据根据标位（招标标的为利率和利差是由小到大、招标标的为价格时从大到小）进行排列，
     *
     * @param dto
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/19 11:13
     */
    private CfetsTrdFirstBidFirmInfoDto propAdjustStrategy(CfetsTrdFirstBidFirmInfoDto dto) {
        //一级投标确认_汇总投标券面总额最小值
        String minUnit = CacheUtil.getSysParam("trdFirstBidFirm_minUnit_totalBiddingNum");
        BigDecimal minChangeUnit = StringUtil.isNotEmpty(minUnit)
                ? new BigDecimal(minUnit) : new BigDecimal(500000);
        // 剩余尾差
        BigDecimal tailDifference = dto.getTotalBiddingNum();
        int index = 0;
        for (CfetsTrdFirstBidFirmProductDto item : dto.getProductDtos()) {
            index++;
            // 记录指令投标券面总额
            item.setAvaNumTemp(item.getEtrBidPriceTotal());
            // 占比值 根据每条数据的“指令投标券面总额/基本信息.指令投标券面总额*基本信息.汇总投标券面总额”计算
            BigDecimal proportion = ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getEtrBidPriceTotal(), dto.getTotalBiddingNum()), dto.getEtrBidPriceTotal(), 4);
            proportion = ArithmeticUtil.multiply(ArithmeticUtil.divide(proportion, minChangeUnit, 0, RoundingMode.HALF_DOWN), minChangeUnit);
            // 最后一位时
            if (index == dto.getProductDtos().size()) {
                if (BigDecimalUtil.ACompareToB(item.getEtrBidPriceTotal(), tailDifference, 6) >= 0
                        && BigDecimalUtil.ACompareToB(proportion, tailDifference, 6) >= 0) {
                    // 指令投标券面总额大于尾差且尾差小于占比值时 用倒减法求值
                    item.setTotalBiddingNum(tailDifference);
                } else if (BigDecimalUtil.ACompareToB(item.getEtrBidPriceTotal(), proportion, 6) >= 0
                        && BigDecimalUtil.ACompareToB(proportion, tailDifference, 6) < 0) {
                    // 指令投标券面总额大于占比值且尾差大于占比值时
                    item.setTotalBiddingNum(proportion);
                } else if (BigDecimalUtil.ACompareToB(item.getEtrBidPriceTotal(), proportion, 6) < 0
                        && BigDecimalUtil.ACompareToB(item.getEtrBidPriceTotal(), tailDifference, 6) < 0) {
                    // 指令投标券面总额小于占比值且指令投标券面总额小于尾差时
                    item.setTotalBiddingNum(item.getAvaNumTemp());
                }
            } else {// 每条数据的汇总投标券面总额须小于等于指令投标券面总额
                if (BigDecimalUtil.ACompareToB(item.getEtrBidPriceTotal(), proportion, 6) >= 0) {
                    item.setTotalBiddingNum(proportion);
                } else {
                    item.setTotalBiddingNum(item.getEtrBidPriceTotal());
                }
            }
            tailDifference = ArithmeticUtil.subtract(tailDifference, item.getTotalBiddingNum());
        }
        //招标标的为利率和利差时从小到大排列
        if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue())
                || StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())) {
            dto.getProductDtos().sort(Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getBidPrice));
        }
        //招标标的为价格时，从大到小排列
        if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_价格.getValue())) {
            dto.getProductDtos().sort(Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getBidPrice).reversed());
        }
        // 处理尾差,最小变动单位序列分配算法
        this.handleTailDifference(dto.getProductDtos(), tailDifference, minChangeUnit);
        return dto;
    }

    /**
     * 调整策略-价格优先
     *
     * @param dto
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/19 11:13
     */
    private CfetsTrdFirstBidFirmInfoDto priceAdjustStrategy(CfetsTrdFirstBidFirmInfoDto dto) {
        //1.排序：将投标产品列表中数据根据标位（招标标的为利率和利差是由小到大、招标标的为价格时从大到小）进行排列，
        //招标标的为利率和利差时从小到大排列
        if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue())
                || StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())) {
            dto.getProductDtos().sort(Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getBidPrice));
        }
        //招标标的为价格时，从大到小排列
        if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_价格.getValue())) {
            dto.getProductDtos().sort(Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getBidPrice).reversed());
        }
        // 2.设值：排序后依次将该条数据的【汇总投标券面总额】默认等于【指令投标券面总额】，
        // a.取至某条数据导致总和大于基本信息中【汇总投标券面总额】,且该数据的标位只有一条数据，则将该条数据的【汇总投标券面总额】削减至和值等于基本信息中【汇总投标券面总额】；
        // b.若取至同一标位的多条数据导致总和大于基本信息中【汇总投标券面总额】，则将剩余量按比例分配至对应多条数据，尾差适用最小变动单位序列算法
        // c.直到列表中各条数据的【汇总投标券面总额】之和等于基本信息中【汇总投标券面总额】为止，剩余的投标产品列表数据中【汇总投标券面总额】默认为0
        Map<BigDecimal, List<CfetsTrdFirstBidFirmProductDto>> resultListMap = new HashMap<>();
        BigDecimal totalBiddingNum = BigDecimal.ZERO;
        // 记录剩余量,为c情况考虑
        BigDecimal totalBiddingNumLat = BigDecimal.ZERO;
        BigDecimal bidPrice = BigDecimal.ZERO;
        // 溢出标志:列表中【汇总投标券面总额】总和大于基本信息中【汇总投标券面总额】
        boolean overFlag = false;
        for (CfetsTrdFirstBidFirmProductDto firmProductDto : dto.getProductDtos()) {
            // 为情况b记录标位,溢出标志出现则不用记录了
            if (!overFlag) {
                bidPrice = firmProductDto.getBidPrice();
            }
            // map中按同标位汇总为一条list,方便后续 对b情况处理
            List<CfetsTrdFirstBidFirmProductDto> list = resultListMap.get(firmProductDto.getBidPrice());
            if (CollectionUtils.isEmpty(list)) {
                list = new ArrayList<>();
            }
            // 溢出时,剩余的投标产品列表数据中【汇总投标券面总额】默认为0
            if (overFlag) {
                firmProductDto.setTotalBiddingNum(BigDecimal.ZERO);
                list.add(firmProductDto);
                resultListMap.put(firmProductDto.getBidPrice(), list);
                continue;
            }
            // 汇总列表已分配的【汇总投标券面总额】
            totalBiddingNum = ArithmeticUtil.doAdd(totalBiddingNum, firmProductDto.getEtrBidPriceTotal());
            // 【汇总投标券面总额】默认等于【指令投标券面总额】，
            firmProductDto.setTotalBiddingNum(firmProductDto.getEtrBidPriceTotal());
            // 溢出时,标志位修改
            if (BigDecimalUtil.ACompareToB(totalBiddingNum, dto.getTotalBiddingNum(), 6) > 0) {
                // 将该条数据的【汇总投标券面总额】削减至和值等于基本信息中【汇总投标券面总额】
                totalBiddingNumLat = ArithmeticUtil.subtract(dto.getTotalBiddingNum(),
                        ArithmeticUtil.subtract(totalBiddingNum, firmProductDto.getEtrBidPriceTotal()));
                if (list.size() == 0) {
                    firmProductDto.setTotalBiddingNum(totalBiddingNumLat);
                }
                overFlag = true;
            }
            list.add(firmProductDto);
            resultListMap.put(firmProductDto.getBidPrice(), list);
        }
        // 溢出标志不变则情况c,否则需判断是情况a还是b
        if (overFlag) {
            // 获取map中的溢出的标位对应的汇总list,当list大小为1时,为情况a不用继续处理 否则为情况b,需要继续处理
            List<CfetsTrdFirstBidFirmProductDto> list = resultListMap.get(bidPrice);
            if (list.size() != 1) {
                // 汇总该标位的 汇总投标券面总额
                BigDecimal totalBiddingNumTemp = list.stream().map(CfetsTrdFirstBidFirmProductDto::getTotalBiddingNum).reduce(BigDecimal.ZERO, BigDecimal::add);
                this.distribution(list, totalBiddingNumTemp);
            }
        }
        List<CfetsTrdFirstBidFirmProductDto> resultList = new ArrayList<>();
        for (List<CfetsTrdFirstBidFirmProductDto> list : resultListMap.values()) {
            resultList.addAll(list);
        }
        //招标标的为利率和利差时从小到大排列
        if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue())
                || StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())) {
            resultList.sort(Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getBidPrice));
        }
        //招标标的为价格时，从大到小排列
        if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_价格.getValue())) {
            resultList.sort(Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getBidPrice).reversed());
        }
        dto.setProductDtos(resultList);
        return dto;
    }

    /**
     * 将剩余量按比例分配至对应多条数据
     *
     * @param list
     * @param tailDifference
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmProductDto>
     * @author jiangpeng
     * @date 2022/5/19 17:17
     */
    private void distribution(List<CfetsTrdFirstBidFirmProductDto> list, BigDecimal tailDifference) {
        //一级投标确认_汇总投标券面总额最小值
        String minUnit = CacheUtil.getSysParam("trdFirstBidFirm_minUnit_totalBiddingNum");
        BigDecimal minChangeUnit = StringUtil.isNotEmpty(minUnit)
                ? new BigDecimal(minUnit) : new BigDecimal(500000);
        // 最大循环次数
        int maxCycle = list.size();
        // 降序
        while (tailDifference.compareTo(BigDecimal.ZERO) > 0 && maxCycle != 0) {
            for (CfetsTrdFirstBidFirmProductDto item : list) {
                // 分配值
                BigDecimal assign = BigDecimal.ZERO;
                // 尾差等于0,循环结束
                if (BigDecimalUtil.isZero(tailDifference)) {
                    break;
                }
                if (!StringUtil.isNotEmpty(item.getAvaNumTemp()) || BigDecimalUtil.isZero(item.getAvaNumTemp())) {
                    item.setAvaNumTemp(item.getTotalBiddingNum());
                }
                // 处理尾差值的不同
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) >= 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) >= 0) {
                    // 剩余指令额度大于等于最小分配单位 尾差大于等于最小变动单位 时,分配最小变动单位
                    assign = minChangeUnit;
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) >= 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) < 0) {
                    // 剩余指令额度大于最小分配单位 尾差小于最小变动单位 时,直接分配尾差的值
                    assign = tailDifference;
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 4) >= 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) < 0) {
                    // 剩余指令额度小于最小分配单位但不小于尾差时,直接分配尾差的值
                    assign = tailDifference;
                    maxCycle--;
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 4) < 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) < 0) {
                    // 剩余指令额度小于最小分配单位且小于尾差时,直接分配剩余指令额度
                    assign = item.getEtrBidPriceTotal();
                    maxCycle--;
                }
                // 券面总额加上分配值
                item.setTotalBiddingNum(ArithmeticUtil.doAdd(item.getTotalBiddingNum(), assign));
                // 尾差减去分配值
                tailDifference = ArithmeticUtil.subtract(tailDifference, assign);
                // 剩余指令额度减去分配值
                item.setAvaNumTemp(ArithmeticUtil.subtract(item.getAvaNumTemp(), assign));
            }
        }
    }

    /**
     * 汇总投标列表生成
     *
     * @param dto
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/19 15:02
     */
    private List<CfetsTrdFirstBidFirmTotalDto> createBidTotalList(CfetsTrdFirstBidFirmInfoDto dto) {
        // 汇总投标列表
        Map<BigDecimal, CfetsTrdFirstBidFirmTotalDto> totalDtoMap = new HashMap<>();
        for (CfetsTrdFirstBidFirmProductDto productDto : dto.getProductDtos()) {
            if (!StringUtil.isNotEmpty(productDto.getTotalBiddingNum())) {
                //【汇总投标券面总额】由各指令自动带入，默认为各指令的指令投标券面总额，可修改，修改后不得高于各指令的指令投标券面总额
                productDto.setTotalBiddingNum(productDto.getEtrBidPriceTotal());
            }
            CfetsTrdFirstBidFirmTotalDto totalDto = totalDtoMap.get(productDto.getBidPrice());
            if (!StringUtil.isNotEmpty(totalDto)) {
                totalDto = new CfetsTrdFirstBidFirmTotalDto();
                totalDto.setBidPrice(productDto.getBidPrice());
                totalDto.setEtrBidPriceTotal(BigDecimal.ZERO);
                totalDto.setTotalBiddingNum(BigDecimal.ZERO);
            }
            totalDto.setEtrBidPriceTotal(ArithmeticUtil.doAdd(totalDto.getEtrBidPriceTotal(), productDto.getEtrBidPriceTotal()));
            totalDto.setTotalBiddingNum(ArithmeticUtil.doAdd(totalDto.getTotalBiddingNum(), productDto.getTotalBiddingNum()));
            totalDtoMap.put(productDto.getBidPrice(), totalDto);
        }
        List<CfetsTrdFirstBidFirmTotalDto> totalDtoList = new ArrayList<>();
        for (CfetsTrdFirstBidFirmTotalDto totalDto : totalDtoMap.values()) {
            totalDtoList.add(totalDto);
        }
        return totalDtoList;
    }


    /**
     * 交易金额/应计利息总额/缴款金额和结算服务费 分配
     *
     * @param dto
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdFirstBidFirmProductDto>
     * @author jiangpeng
     * @date 2022/4/24 14:06
     */
    public List<CfetsTrdFirstBidFirmProductDto> otherDistribution(CfetsTrdFirstBidFirmInfoDto dto) {
        // 根据投标产品列表中指令条数平均分配结算服务费
        BigDecimal settFeeAmt = StringUtil.isNotEmpty(dto.getSettFeeAmt()) ? dto.getSettFeeAmt() : BigDecimal.ZERO;
        // 交易金额
        BigDecimal tradeAmt = StringUtil.isNotEmpty(dto.getTradeAmt()) ? dto.getTradeAmt() : BigDecimal.ZERO;
        // 应计利息总额
        BigDecimal intAmtTatol = StringUtil.isNotEmpty(dto.getIntAmtTatol()) ? dto.getIntAmtTatol() : BigDecimal.ZERO;
        // 缴款金额
        BigDecimal payableAmount = dto.getPayableAmount();
        List<CfetsTrdFirstBidFirmProductDto> productDtos = dto.getProductDtos();
        // 费用平均值
        BigDecimal avg = ArithmeticUtil.divide(settFeeAmt, new BigDecimal(productDtos.size()), 2);
        // 交易金额剩余尾差
        BigDecimal tradeTailDifference = tradeAmt;
        // 应计利息总额剩余尾差
        BigDecimal intTailDifference = intAmtTatol;
        // 缴款金额剩余尾差
        BigDecimal payTailDifference = payableAmount;
        // 费用剩余尾差
        BigDecimal feeTailDifference = settFeeAmt;
        int index = 0;
        for (CfetsTrdFirstBidFirmProductDto item : productDtos) {
            index++;
            //产品列表中券面总额/基本信息中总的券面总额*基本信息中交易金额
            BigDecimal tradeAmountDetail = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(item.getBidPriceTotal(), tradeAmt), dto.getBidPriceTotal(), 2, RoundingMode.DOWN);
            //产品列表中券面总额/基本信息中总的券面总额*基本信息中应计利息总额
            BigDecimal intAmountDetail = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(item.getBidPriceTotal(), intAmtTatol), dto.getBidPriceTotal(), 2, RoundingMode.DOWN);
            //产品列表中券面总额/基本信息中总的券面总额*基本信息中缴款金额
            BigDecimal payableAmountDetail = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(item.getBidPriceTotal(), payableAmount), dto.getBidPriceTotal(), 2, RoundingMode.DOWN);
            //费用金额
            BigDecimal feeAmountDetail = avg;
            // 最后一笔且尾差小于计算出的分配值时,用倒减值
            if (index == productDtos.size()) {
                if (BigDecimalUtil.ACompareToB(tradeAmountDetail, tradeTailDifference, 3) >= 0) {
                    tradeAmountDetail = tradeTailDifference;
                }
                if (BigDecimalUtil.ACompareToB(intAmountDetail, intTailDifference, 3) >= 0) {
                    intAmountDetail = intTailDifference;
                }
                if (BigDecimalUtil.ACompareToB(payableAmountDetail, payTailDifference, 3) >= 0) {
                    payableAmountDetail = payTailDifference;
                }
                if (BigDecimalUtil.ACompareToB(avg, feeTailDifference, 3) >= 0) {
                    feeAmountDetail = feeTailDifference;
                }
            }
            // 交易金额
            item.setTradeAmt(tradeAmountDetail);
            // 应计利息总额
            item.setIntAmtTatol(intAmountDetail);
            // 缴款金额
            item.setPayAmt(payableAmountDetail);
            // 结算服务费
            item.setSettFeeAmt(feeAmountDetail);
            tradeTailDifference = ArithmeticUtil.subtract(tradeTailDifference, item.getTradeAmt());
            intTailDifference = ArithmeticUtil.subtract(intTailDifference, item.getIntAmtTatol());
            payTailDifference = ArithmeticUtil.subtract(payTailDifference, item.getPayAmt());
            feeTailDifference = ArithmeticUtil.subtract(feeTailDifference, item.getSettFeeAmt());
        }
        this.handleTailDifferenceOhter(productDtos, tradeTailDifference, intTailDifference, payTailDifference, feeTailDifference);
        return productDtos;
    }

    /**
     * 修改 基本信息中【汇总投标券面总额】和汇总投标列表中【投标标位】
     *
     * @param dto
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/5/20 16:53
     */
    public AjaxResult checkFinInfo(CfetsTrdFirstBidFirmInfoDto dto) {
        AjaxResult ajaxResult = new AjaxResult();
        //一级投标确认_最低投标总量
        String minToal = CacheUtil.getSysParam("trdFirstBidFirm_minToal_totalBiddingNum");
        //一级投标确认_汇总投标券面总额最小变动单位
        String minUnit = CacheUtil.getSysParam("trdFirstBidFirm_minUnit_totalBiddingNum");
        FinProductIssue finProductIssue = finProductIssueMapper.selectByPrimaryKey(dto.getFinprodId());
        List<CheckMassgeInfo> checkMassgeInfos = new ArrayList<>();
        boolean flag = false;
        if (StringUtil.equals("1", dto.getEditFeild())) {
            // 标的类金融产品发行信息.最高投标总量 IS NOT NULL AND 汇总投标券面总额（万元）*10000 + 系统中该资产的有效、流程中的汇总券面总额 > 标的类金融产品发行信息.最高投标总量
            if (!flag && (StringUtil.isNotEmpty(finProductIssue.getMaxTotalTender())
                    && BigDecimalUtil.ACompareToB(dto.getTotalBiddingNum(), finProductIssue.getMaxTotalTender(), 4) > 0)) {
                CheckMassgeInfo checkMassgeInfo = new CheckMassgeInfo();
                checkMassgeInfo.setPrimaryKey(dto.getTotalBiddingNum().toPlainString());
                checkMassgeInfo.setFeild("totalBiddingNum");
                checkMassgeInfo.setMessage("所有总和应小于等于最高投标总量!");
                checkMassgeInfos.add(checkMassgeInfo);
                flag = true;
            }
            // 系统参数.一级投标确认_最小变动单位 IS NOT NULL AND 汇总投标券面总额（万元）*10000 不是最小变动单位的整数倍
            if (!flag && StringUtil.isNotEmpty(minUnit)
                    && !BigDecimalUtil.divideResultIsInteger(dto.getTotalBiddingNum(), new BigDecimal(minUnit))) {
                CheckMassgeInfo checkMassgeInfo = new CheckMassgeInfo();
                checkMassgeInfo.setPrimaryKey(dto.getTotalBiddingNum().toPlainString());
                checkMassgeInfo.setFeild("totalBiddingNum");
                checkMassgeInfo.setMessage("应是最小变动单位的整数倍!");
                checkMassgeInfos.add(checkMassgeInfo);
                flag = true;
            }
            // 系统参数.一级投标确认_最低投标总量 IS NOT NULL AND 汇总投标券面总额（万元）*10000 < 一级投标确认_最低投标总量
            if (!flag && (StringUtil.isNotEmpty(minToal)
                    && BigDecimalUtil.ACompareToB(dto.getTotalBiddingNum(), new BigDecimal(minToal), 4) < 0)) {
                CheckMassgeInfo checkMassgeInfo = new CheckMassgeInfo();
                checkMassgeInfo.setPrimaryKey("totalBiddingNum");
                checkMassgeInfo.setMessage("应大于最低投标总量!");
                checkMassgeInfos.add(checkMassgeInfo);
                flag = true;
            }
        } else {
            Map<String, CheckMassgeInfo> massgeInfoMap = new HashMap<>();
            BigDecimal minBidPrice = null;
            BigDecimal maxBidPrice = null;
            BigDecimal lastBidPrice = null;
            dto.getBidTotalList().sort(Comparator.comparing(CfetsTrdFirstBidFirmTotalDto::getBidPrice));
            for (CfetsTrdFirstBidFirmTotalDto item : dto.getBidTotalList()) {
                Map<String, String> map = new HashMap<>();
                // 中标标位
                BigDecimal bidPrice = BigDecimal.ZERO;
                if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue())) {
                    bidPrice = ArithmeticUtil.divide(item.getBidPrice(), new BigDecimal(100), 4);
                } else if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())) {
                    bidPrice = ArithmeticUtil.divide(item.getBidPrice(), new BigDecimal(10000), 4);
                } else {//价格
                    bidPrice = item.getBidPrice();
                }
                if (!StringUtil.isNotEmpty(minBidPrice)
                        || BigDecimalUtil.ACompareToB(minBidPrice, bidPrice, 6) > 0) {
                    minBidPrice = bidPrice;
                }
                if (!StringUtil.isNotEmpty(maxBidPrice)
                        || BigDecimalUtil.ACompareToB(maxBidPrice, bidPrice, 6) < 0) {
                    maxBidPrice = bidPrice;
                }
                // 标的类金融产品发行信息.投标步长 IS NOT NULL AND 投标标位不是标的类金融产品发行信息.投标步长的整数倍
                if (StringUtil.isNotEmpty(finProductIssue.getTenderUnit())
                        && !BigDecimalUtil.divideResultIsInteger(bidPrice, finProductIssue.getTenderUnit())) {
                    CheckMassgeInfo checkMassgeInfo = new CheckMassgeInfo();
                    checkMassgeInfo.setPrimaryKey(bidPrice.toPlainString());
                    checkMassgeInfo.setFeild("bidPrice");
                    checkMassgeInfo.setMessage("应是投标步长整数倍!");
                    massgeInfoMap.put(checkMassgeInfo.getPrimaryKey(), checkMassgeInfo);
                }
                // 标的类金融产品发行信息.投标步长 IS NOT NULL AND 标的类金融产品发行信息.连续投标限制=是 AND 汇总投标列表.投标标位的差值为1个投标步长
                if (StringUtil.isNotEmpty(finProductIssue.getTenderUnit()) && StringUtil.equals(finProductIssue.getSeriYN(), DictCodeList.是否_是.getValue())
                        && BigDecimalUtil.ACompareToB(ArithmeticUtil.subtract(lastBidPrice, bidPrice), finProductIssue.getTenderUnit(), 2) > 0) {
                    CheckMassgeInfo checkMassgeInfo = new CheckMassgeInfo();
                    checkMassgeInfo.setPrimaryKey(bidPrice.toPlainString());
                    checkMassgeInfo.setFeild("bidPrice");
                    checkMassgeInfo.setMessage("不符合连续投标限制!");
                    massgeInfoMap.put(checkMassgeInfo.getPrimaryKey(), checkMassgeInfo);
                }
                // 标的类金融产品发行信息.每标位最低投标量 IS NOT NULL AND 汇总投标券面总额（万元）*10000 < 标的类金融产品发行信息.每标位最低投标量
                if ((StringUtil.isNotEmpty(finProductIssue.getMinPerTamt())
                        && BigDecimalUtil.ACompareToB(item.getTotalBiddingNum(), finProductIssue.getMinPerTamt(), 4) < 0)) {
                    CheckMassgeInfo checkMassgeInfo = new CheckMassgeInfo();
                    checkMassgeInfo.setPrimaryKey(bidPrice.toPlainString());
                    checkMassgeInfo.setFeild("totalBiddingNum");
                    checkMassgeInfo.setMessage("应大于等于每标位最低投标量!");
                    massgeInfoMap.put(checkMassgeInfo.getPrimaryKey(), checkMassgeInfo);
                }
                // 标的类金融产品发行信息.每标位最高投标量 IS NOT NULL AND 汇总投标券面总额（万元）*10000 > 标的类金融产品发行信息.每标位最高投标量
                if ((StringUtil.isNotEmpty(finProductIssue.getMaxPerUtamt())
                        && BigDecimalUtil.ACompareToB(item.getTotalBiddingNum(), finProductIssue.getMaxPerUtamt(), 4) > 0)) {
                    CheckMassgeInfo checkMassgeInfo = new CheckMassgeInfo();
                    checkMassgeInfo.setPrimaryKey(bidPrice.toPlainString());
                    checkMassgeInfo.setFeild("totalBiddingNum");
                    checkMassgeInfo.setMessage("应小于等于每标位最高投标量!");
                    massgeInfoMap.put(checkMassgeInfo.getPrimaryKey(), checkMassgeInfo);
                }
                lastBidPrice = bidPrice;
            }
            // 标的类金融产品发行信息.最低标位 IS NOT NULL AND 投标标位 < 标的类金融产品发行信息.最低标位
            if (StringUtil.isNotEmpty(finProductIssue.getMinTamt())
                    && BigDecimalUtil.ACompareToB(minBidPrice, finProductIssue.getMinTamt(), 4) < 0) {
                CheckMassgeInfo checkMassgeInfo = new CheckMassgeInfo();
                checkMassgeInfo.setPrimaryKey(minBidPrice.toPlainString());
                checkMassgeInfo.setFeild("bidPrice");
                checkMassgeInfo.setMessage("应大于等于标的的最低标位!");
                massgeInfoMap.put(checkMassgeInfo.getPrimaryKey(), checkMassgeInfo);
            }
            // 标的类金融产品发行信息.最高标位 IS NOT NULL AND 投标标位 > 标的类金融产品发行信息.最高标位
            if (StringUtil.isNotEmpty(finProductIssue.getMaxTamt())
                    && BigDecimalUtil.ACompareToB(maxBidPrice, finProductIssue.getMaxTamt(), 4) > 0) {
                CheckMassgeInfo checkMassgeInfo = new CheckMassgeInfo();
                checkMassgeInfo.setPrimaryKey(maxBidPrice.toPlainString());
                checkMassgeInfo.setFeild("bidPrice");
                checkMassgeInfo.setMessage("应小于等于标的的最高标位!");
                massgeInfoMap.put(checkMassgeInfo.getPrimaryKey(), checkMassgeInfo);
            }
            // 标的类金融产品发行信息.最大投标标位数 IS NOT NULL AND 投标标位的数量 > 标的类金融产品发行信息.最大投标标位数
            if (StringUtil.isNotEmpty(finProductIssue.getMaxTenderUnit())
                    && BigDecimalUtil.ACompareToB(new BigDecimal(dto.getBidTotalList().size()), finProductIssue.getMaxTenderUnit(), 2) > 0) {
                CheckMassgeInfo checkMassgeInfo = new CheckMassgeInfo();
                checkMassgeInfo.setPrimaryKey("maxLength");
                checkMassgeInfo.setFeild("bidTotalList");
                checkMassgeInfo.setMessage("标位个数应小于等于最大投标标位数!");
                massgeInfoMap.put(checkMassgeInfo.getPrimaryKey(), checkMassgeInfo);
            }
            // 标的类金融产品发行信息.最大投标标位差 IS NOT NULL AND 汇总投标列表的最高标位 - 汇总投标列表的最低标位的差的绝对值 > 标的类金融产品发行信息.最大投标标位差
            if (StringUtil.isNotEmpty(finProductIssue.getMaxTenderPlus())
                    && BigDecimalUtil.ACompareToB(ArithmeticUtil.subtract(maxBidPrice, minBidPrice).abs(), finProductIssue.getMaxTenderPlus(), 2) > 0) {
                CheckMassgeInfo checkMassgeInfo1 = new CheckMassgeInfo();
                checkMassgeInfo1.setPrimaryKey(maxBidPrice.toPlainString());
                checkMassgeInfo1.setFeild("bidPrice");
                checkMassgeInfo1.setMessage("不符合最大投标标位差!");
                massgeInfoMap.put(checkMassgeInfo1.getPrimaryKey(), checkMassgeInfo1);
                CheckMassgeInfo checkMassgeInfo2 = new CheckMassgeInfo();
                checkMassgeInfo2.setPrimaryKey(minBidPrice.toPlainString());
                checkMassgeInfo2.setFeild("bidPrice");
                checkMassgeInfo2.setMessage("不符合最大投标标位差!");
                massgeInfoMap.put(checkMassgeInfo2.getPrimaryKey(), checkMassgeInfo2);
            }
            for (CheckMassgeInfo massgeInfo : massgeInfoMap.values()) {
                checkMassgeInfos.add(massgeInfo);
            }
        }
        if (checkMassgeInfos.size() > 0) {
            ajaxResult.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        ajaxResult.setData(checkMassgeInfos);
        return ajaxResult;
    }

    /**
     * @param dto
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/24 17:05
     */
    public CfetsTrdFirstBidFirmInfoDto editLink(CfetsTrdFirstBidFirmInfoDto dto) {
        // 自动分配
        dto = this.autoAllocation(dto);
        // 汇总
        dto.setBidTotalList(this.createBidTotalList(dto));
        return dto;
    }

    /**
     * @param dto
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/24 17:04
     */
    public CfetsTrdFirstBidFirmInfoDto productListLink(CfetsTrdFirstBidFirmInfoDto dto) {
        // 汇总
        dto.setBidTotalList(this.createBidTotalList(dto));
        return dto;
    }

    /**
     * 中标标位联动
     *
     * @param dto
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/20 10:54
     */
    public CfetsTrdFirstBidFirmInfoDto winPriceLink(CfetsTrdFirstBidFirmInfoDto dto) {
        // 招标标的 IN（利率，利差）时，投标产品列表中，只展示投标标位小于等于基本信息.中标标位的记录
        List<CfetsTrdFirstBidFirmProductDto> list = new ArrayList<>();
        // 剩余指令额度汇总
        BigDecimal avaNumTotal = BigDecimal.ZERO;
        // 指令投标券面总额  SUM（委托投标标位.投标券面），转化成万元展示，为满足投标标位的标位券面总额汇总
        BigDecimal etrBidPriceTotal = BigDecimal.ZERO;
        for (CfetsTrdFirstBidFirmProductDto productDto : dto.getProductDtos()) {
            if ((StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue())
                    || StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue()))
                    && BigDecimalUtil.ACompareToB(productDto.getBidPrice(), dto.getWinPrice(), 6) <= 0) {
                list.add(productDto);
                etrBidPriceTotal = ArithmeticUtil.doAdd(etrBidPriceTotal, productDto.getBidPriceTotal());
                continue;
            }
            if (StringUtil.equals(dto.getTenderTarget(), DictCodeListExt4.招标标的_价格.getValue())
                    && BigDecimalUtil.ACompareToB(productDto.getBidPrice(), dto.getWinPrice(), 6) >= 0) {
                list.add(productDto);
                etrBidPriceTotal = ArithmeticUtil.doAdd(etrBidPriceTotal, productDto.getBidPriceTotal());
                continue;
            }
            avaNumTotal = ArithmeticUtil.doAdd(avaNumTotal, productDto.getAvaNum());
        }
        dto.setEtrBidPriceTotal(etrBidPriceTotal);
        // 若券面总额在修改中标标位前手动修改过，不变；若没有手动修改过，重新计算，=指令投标券面总额
        if (BigDecimalUtil.ACompareToB(avaNumTotal, dto.getBidPriceTotal(), 4) == 0) {
            dto.setBidPriceTotal(etrBidPriceTotal);
        }
        dto.setProductDtos(list);
        // 重新计算金额并自动分配
        this.calAmtAndAutoAllocation(dto, null);
        return dto;
    }

    /**
     * 重新计算金额并自动分配
     *
     * @param dto
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/20 13:41
     */
    public CfetsTrdFirstBidFirmInfoDto calAmtAndAutoAllocation(CfetsTrdFirstBidFirmInfoDto dto, BigDecimal price) {
        // 计算金额信息
        this.calAmtAndFee(dto, price);
        // 自动分配
        this.autoAllocation(dto);
        return dto;
    }

    /**
     * 其他金额分配
     *
     * @param autoRequest
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdFirstBidFirmProductDto>
     * @author jiangpeng
     * @date 2022/5/19 14:40
     */
    public List<CfetsTrdFirstBidFirmProductDto> otherAllocation(CfetsTrdFirstBidFirmInfoDto autoRequest) {
        logger.debug("其他金额分配,入参:" + JsonUtils.object2Json(autoRequest));
//        BigDecimal subscriptionAmount = autoRequest.getSubscriptionAmount();
        // 剩余指令额度不为0的数据
        List<CfetsTrdFirstBidFirmProductDto> list = new ArrayList<>();
        // 剩余指令额度为0的数据
        List<CfetsTrdFirstBidFirmProductDto> listAumIsZero = new ArrayList<>();
        //当存在剩余指令额度为0的数据时，认购量为空，且整条数据置灰不可修改并放置列表最底部，此种数据不参与如认购量分配、应缴款金额分配、结算服务费分配等此类计算
        for (CfetsTrdFirstBidFirmProductDto dto : autoRequest.getProductDtos()) {
            if (BigDecimalUtil.isZero(dto.getAvaNum()) || dto.getAvaNum() == null) {
                listAumIsZero.add(dto);
            } else {
                list.add(dto);
            }
        }
        // 降序
        list.sort(Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getAvaNum));
        // 分配应缴款金额和结算服务费
//        this.otherDistribution(autoRequest.getSubscriptionAmount(), autoRequest.getPayableAmount(), autoRequest.getSettFeeAmt(), list);
        list.addAll(listAumIsZero);
        logger.debug("其他金额分配,结果:" + JsonUtils.object2Json(list));
        return list;
    }

    /**
     * 重新计算 应缴款金额 结算服务费 并 重新分配产品列表中 认购量 应缴款金额 结算服务费
     *
     * @param autoRequest 页面元素
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/4/24 17:16
     */
    public CfetsTrdFirstBidFirmInfoDto calData(CfetsTrdFirstBidFirmInfoDto autoRequest) {
        // 重新计算 应缴款金额 结算服务费
        CfetsTrdFirstBidFirmInfoDto result = this.calAmtAndFee(autoRequest, null);
        // 重新分配 认购量 应缴款金额 结算服务费
//        result.setProductDtos(this.autoAllocation(autoRequest));
        return result;
    }

    /**
     * 风险试算check
     *
     * @param entity 入参 画面元素
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/4/18 15:00
     */
    public AjaxResult riskCheckNotEmpty(RiskCalRequest entity) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entity.getPtlAllocations())) {
            ajaxResult.setErrorList(errorList);
        }
        return ajaxResult;
    }

    /**
     * 构造生成交易编号的参数
     *
     * @param commonBusinessType
     * @return java.lang.String
     * @author jiangpeng
     * @date 2022/5/21 11:24
     */
    public String getBidId(BussinessDictCodeList commonBusinessType) {
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(commonBusinessType);
            return baseGetBusinessId.buildBussinessCode(codeParameter);
        } else {
            return IDGenerator.generate();
        }
    }

    /**
     * 风险试算实体组装
     *
     * @param riskCalRequest
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author jiangpeng
     * @date 2022/4/21 14:28
     */
    public Map<String, Object> doRiskcalPackage(RiskCalRequest riskCalRequest) {
        Map<String, Object> result = new HashMap<>();
        List<Map> bussTrdTransfenpeiDto = new ArrayList<>();
        for (PtlAllocationDto ptlAllocationDto : riskCalRequest.getPtlAllocations()) {
            Map<String, Object> stringObjectMap = BeanUtil.object2Map(ptlAllocationDto);
            stringObjectMap.put("tradeAmount", ptlAllocationDto.getShareAmt());
            stringObjectMap.put("fpriceAmt", ptlAllocationDto.getShareAmt());
            stringObjectMap.put("portfolioCode", ptlAllocationDto.getPortfolioId());
            bussTrdTransfenpeiDto.add(stringObjectMap);
        }
        result.put("bussTrdTransfenpeiDto", bussTrdTransfenpeiDto);
        result.put("finprodId", riskCalRequest.getFinprodId());
        result.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        result.put("ps", riskCalRequest.getPs());
        result.put("ccy", "CNY");
        return result;
    }

    /**
     * 提交校验
     *
     * @param dto 页面元素
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/4/25 10:35
     */
    public AjaxResult check(CfetsTrdFirstBidFirmInfoDto dto) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        //必填项校验
        //1.必填项公共部分校验
        // 资产代码
        if (StringUtil.isEmpty(dto.getFinprodId())) {
            errorList.add(new ValidMessage("资产代码不可为空!"));
        }
        // 资产简称
        if (StringUtil.isEmpty(dto.getFinprodAbbr())) {
            errorList.add(new ValidMessage("资产简称不可为空!"));
        }
        // 本方交易员
        if (StringUtil.isEmpty(dto.getOurTrader())) {
            errorList.add(new ValidMessage("本方交易员不可为空!"));
        }
        // 交易对手
        if (StringUtil.isEmpty(dto.getCounterId())) {
            errorList.add(new ValidMessage("交易对手不可为空!"));
        }
        // 招标标的
        if (StringUtil.isEmpty(dto.getTenderTarget())) {
            errorList.add(new ValidMessage("招标标的不可为空!"));
        }
        // 币种
        if (StringUtil.isEmpty(dto.getCcy())) {
            errorList.add(new ValidMessage("币种不可为空!"));
        }
        // 投标日
        if (!StringUtil.isNotEmpty(dto.getBidDateStart())) {
            errorList.add(new ValidMessage("投标日开始日不可为空!"));
        }
        if (!StringUtil.isNotEmpty(dto.getBidDateEnd())) {
            errorList.add(new ValidMessage("投标日结束日不可为空!"));
        }
        // 投标方式
        if (StringUtil.isEmpty(dto.getBiddingMethod())) {
            errorList.add(new ValidMessage("投标方式不可为空!"));
        }
        // 缴款方式
        if (StringUtil.isEmpty(dto.getPayType())) {
            errorList.add(new ValidMessage("缴款方式不可为空!"));
//            errorList.add(new ValidMessage(getMessage("COMMON_NOTEMPTY_001", "缴款方式")));
        }
        // 指令投标券面总额
        if (!StringUtil.isNotEmpty(dto.getEtrBidPriceTotal())) {
            errorList.add(new ValidMessage("指令投标券面总额不可为空!"));
        }

        // 结算手续费 需要大于零
        if (BigDecimalUtil.ACompareToB(dto.getSettFeeAmt(), BigDecimal.ZERO, 4) < 0) {
            errorList.add(new ValidMessage("结算手续费不允许为负数!"));
        }

        if (StringUtil.equals(DictCodeListExt4.投标方式_自主投标.getValue(), dto.getBiddingMethod())) {
            //  自主投标
            errorList.addAll(this.checkZHNotEmpty(dto));
        } else if (StringUtil.equals(DictCodeListExt4.投标方式_代投代缴.getValue(), dto.getBiddingMethod())) {
            //代投代缴
            errorList.addAll(this.checkZHNotEmpty(dto));
        } else if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), dto.getBiddingMethod())) {
            // 分销买入
            errorList.addAll(this.checkFXNotEmpty(dto));
        }
        //金额校验
        if (StringUtil.isNotEmpty(errorList)) {
            ajaxResult.setErrorList(errorList);
        }
        return ajaxResult;
    }

    /**
     * 处理发起流程逻辑
     *
     * @param entity 页面元素
     * @param type   操作类型
     * @author jiangpeng
     * @date 2022/4/24 11:42
     */
    @Override
    public void doInsert(CfetsTrdFirstBidFirmInfoDto entity, OperationType type) {
        if ("save".equals(entity.getOperateType())) {
            return;
        }
        logger.debug("处理发起流程逻辑,入参:" + JsonUtils.object2Json(entity));
        if (!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
            WflBidOrder wflBidOrder = new WflBidOrder();
            List<WflBidOrderBidPrice> wflBidOrderBidPrices = new ArrayList<>();
            if (!StringUtil.isNotEmpty(entity.getTradeDate())) {
                entity.setTradeDate(DateUtil.parse(iPlatformDate.getSysDateStr()));
            }
            // 组装数据-投标确认
            this.prepareBidOrderAndList(entity, wflBidOrder, wflBidOrderBidPrices);
            // 数据入库-投标确认
            this.insertWflData(wflBidOrder, wflBidOrderBidPrices);
            if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), entity.getBiddingMethod())) {
                // 组装数据-交易数据
                List<CfetsTrdFirstBidFirmInvestTradeDto> tradeDtos = this.prepareWflBidEntity(entity);
                // 数据入库-交易数据
                tradeDtos.forEach(tradeDto -> {
                    this.doInsertBidMethod(tradeDto);
                });
            }
            // 发送头寸
            if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), entity.getBiddingMethod())) {
                entity.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
                this.sendPositionMethod(entity, true, null);
            }
            // 老头寸处理
//            this.dealOldPosition(entity, false);
        }
        // AOP中处理操作履历
    }

    /**
     * 组装入库数据
     *
     * @param entity
     * @param wflBidOrder
     * @param wflBidOrderBidPrices
     * @author jiangpeng
     * @date 2022/5/21 17:25
     */
    private void prepareBidOrderAndList(CfetsTrdFirstBidFirmInfoDto entity,
                                        WflBidOrder wflBidOrder, List<WflBidOrderBidPrice> wflBidOrderBidPrices) {
        // 投标编号
        wflBidOrder.setBidId(entity.getBidId());
        // 资产代码
        wflBidOrder.setFinprodId(entity.getFinprodId());
        // 本方交易员
        wflBidOrder.setOurTrader(entity.getOurTrader());
        // 交易对手
        wflBidOrder.setCounterId(entity.getCounterId());
        // 投标日 = 平台日
        wflBidOrder.setBidDate(iPlatformDate.getSysDate());
        // 投标方式
        wflBidOrder.setBidModle(entity.getBiddingMethod());
        // 缴款方式
        wflBidOrder.setPayType(entity.getPayType());
        // 工作流id
        wflBidOrder.setWorkflowId(entity.getWorkflowId());
        // 工作流状态
        wflBidOrder.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
        // 备注
        wflBidOrder.setRemark(entity.getRemark());
        if (StringUtil.equals(DictCodeListExt4.投标方式_自主投标.getValue(), entity.getBiddingMethod())
                || StringUtil.equals(DictCodeListExt4.投标方式_代投代缴.getValue(), entity.getBiddingMethod())) {
            //  自主投标 代投代缴
            // 缴款日 = 画面.缴款截止日
            wflBidOrder.setPayDate(entity.getPayEdate());
            // 投标券面总额 = 画面.汇总投标券面总额
            wflBidOrder.setWinPrinAmt(entity.getTotalBiddingNum());
            // 分配方式 = 画面.调整策略
            wflBidOrder.setDipType(entity.getAdjustStrategy());
            for (CfetsTrdFirstBidFirmProductDto item : entity.getProductDtos()) {
                WflBidOrderBidPrice wflBidOrderBidPrice = new WflBidOrderBidPrice();
                // 投标编号
                wflBidOrderBidPrice.setBidId(entity.getBidId());
                // 委托代码
                wflBidOrderBidPrice.setEntrustId(item.getEntrustId());
                // 投标标位
                if (StringUtil.equals(entity.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue())) {
                    wflBidOrderBidPrice.setBidPrice(ArithmeticUtil.divide(item.getBidPrice(), new BigDecimal(100), 4));
                } else if (StringUtil.equals(entity.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())) {
                    wflBidOrderBidPrice.setBidPrice(ArithmeticUtil.divide(item.getBidPrice(), new BigDecimal(10000), 4));
                } else {//价格
                    wflBidOrderBidPrice.setBidPrice(item.getBidPrice());
                }
                // 投标券面总额
                wflBidOrderBidPrice.setTheWinPrinAmt(item.getTotalBiddingNum());
                // 组合代码
                wflBidOrderBidPrice.setPortfolioId(item.getPortfolioId());
                // 投资目的
                wflBidOrderBidPrice.setInvAim(item.getBusinessModel());
                // 证券账户
                wflBidOrderBidPrice.setSecAcctId(item.getSecManageAcctId());
                // 资金账户
                wflBidOrderBidPrice.setAcctNo(item.getAcctNo());
                // 工作流id
                wflBidOrderBidPrice.setWorkflowId(entity.getWorkflowId());
                // 工作流状态
                wflBidOrderBidPrice.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                wflBidOrderBidPrices.add(wflBidOrderBidPrice);
            }
        } else if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), entity.getBiddingMethod())) {
            // 分销买入
            // 缴款日 = 画面.缴款日
            wflBidOrder.setPayDate(entity.getPayDate());
            // 投标券面总额 = 画面.券面总额
            wflBidOrder.setWinPrinAmt(entity.getBidPriceTotal());
            // 中标标位
            if (StringUtil.equals(entity.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue())) {
                wflBidOrder.setWinPrice(ArithmeticUtil.divide(entity.getWinPrice(), new BigDecimal(100), 4));
            } else if (StringUtil.equals(entity.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())) {
                wflBidOrder.setWinPrice(ArithmeticUtil.divide(entity.getWinPrice(), new BigDecimal(10000), 4));
            } else {//价格
                wflBidOrder.setWinPrice(entity.getWinPrice());
            }
            // 分配方式 = 画面.分配方式
            wflBidOrder.setDipType(entity.getDipType());
            // 净价(元)
            wflBidOrder.setUnitCprice(entity.getPrice());
            // 应计利息
            wflBidOrder.setUnitInt(entity.getIntAmt());
            // 交易金额
            wflBidOrder.setTradeAmt(entity.getTradeAmt());
            // 应计利息总额
            wflBidOrder.setIntAmt(entity.getIntAmtTatol());
            // 缴款金额(元)
            wflBidOrder.setPayableAmount(entity.getPayableAmount());
            // 结算服务费(元)
            wflBidOrder.setSettFeeAmt(entity.getSettFeeAmt());
            // 债权债务登记日
            wflBidOrder.setAssignRegdate(entity.getAssignRegdate());
            // 上市日
            wflBidOrder.setOnmarketDate(entity.getListingDate());
            for (CfetsTrdFirstBidFirmProductDto item : entity.getProductDtos()) {
                WflBidOrderBidPrice wflBidOrderBidPrice = new WflBidOrderBidPrice();
                // 投标编号
                wflBidOrderBidPrice.setBidId(entity.getBidId());
                // 委托代码
                wflBidOrderBidPrice.setEntrustId(item.getEntrustId());
                // 投标标位
                if (StringUtil.equals(entity.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue())) {
                    wflBidOrderBidPrice.setBidPrice(ArithmeticUtil.divide(item.getBidPrice(), new BigDecimal(100), 4));
                } else if (StringUtil.equals(entity.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())) {
                    wflBidOrderBidPrice.setBidPrice(ArithmeticUtil.divide(item.getBidPrice(), new BigDecimal(10000), 4));
                } else {//价格
                    wflBidOrderBidPrice.setBidPrice(item.getBidPrice());
                }
                // 投标券面总额
                wflBidOrderBidPrice.setTheWinPrinAmt(item.getTotalBiddingNum());
                // 组合代码
                wflBidOrderBidPrice.setPortfolioId(item.getPortfolioId());
                // 投资目的
                wflBidOrderBidPrice.setInvAim(item.getBusinessModel());
                // 证券账户
                wflBidOrderBidPrice.setSecAcctId(item.getSecManageAcctId());
                // 资金账户
                wflBidOrderBidPrice.setAcctNo(item.getAcctNo());
                // 交易金额
                wflBidOrderBidPrice.setTradeAmt(item.getTradeAmt());
                // 应计利息总额
                wflBidOrderBidPrice.setIntAmt(item.getIntAmtTatol());
                // 缴款金额(元)
                wflBidOrderBidPrice.setPayableAmount(item.getPayAmt());
                // 结算服务费(元)
                wflBidOrderBidPrice.setSettFeeAmt(item.getSettFeeAmt());
                // 工作流id
                wflBidOrderBidPrice.setWorkflowId(entity.getWorkflowId());
                // 工作流状态
                wflBidOrderBidPrice.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                wflBidOrderBidPrices.add(wflBidOrderBidPrice);
            }
        }
    }


    /**
     * 分销买入保存提交时，交易数据组装
     *
     * @param dto
     * @param genType
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInvestTradeDto
     * @author jiangpeng
     * @date 2022/5/24 15:48
     */
    public List<CfetsTrdFirstBidFirmInvestTradeDto> prepareWflBidEntity(CfetsTrdFirstBidFirmInfoDto dto) {
        String userName = null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "system";
        // 按证券账户维度汇总
        List<CfetsTrdFirstBidFirmProductDto> secIdProducts = this.sumBySec(dto.getProductDtos());
        List<CfetsTrdFirstBidFirmInvestTradeDto> results = new ArrayList<>();
        // 组装 金融产品交易汇总表
        WflTrdFinProductDealTotal trdFinProductDealTotal = new WflTrdFinProductDealTotal();
        // 金融产品类型（估值核算）
//        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);
        trdFinProductDealTotal.setBranch(branchLong);
        trdFinProductDealTotal.setFinprodType(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        trdFinProductDealTotal.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        trdFinProductDealTotal.setbTradeId(trdFinProductDealTotal.getbTradeId());
        // 资产代码
        trdFinProductDealTotal.setFinprodId(dto.getFinprodId());
        // 交易日期 = 平台日
        trdFinProductDealTotal.setTradeDate(iPlatformDate.getSysDate());
        // 交割日期 = 画面.债权债务登记日
        trdFinProductDealTotal.setSettleDate(dto.getAssignRegdate());
        // 交易币种 = 画面.币种
        trdFinProductDealTotal.setCcy(dto.getCcy());
        // 单位净价 = 画面.净价
        trdFinProductDealTotal.setUnitCprice(dto.getPrice());
        // 单位利息 = 画面.应计利息
        trdFinProductDealTotal.setUnitInt(dto.getIntAmt());
        // 单位全价 = 画面.金额信息.全价
        trdFinProductDealTotal.setUnitFprice(dto.getFullPrice());
        // 单位面值
        trdFinProductDealTotal.setParValue(BigDecimal.ONE/*cfetsTrdConfirmService.getUnitPrice(trdFinProductDealTotal.getFinprodId(), trdFinProductDealTotal.getSettleDate())*/);
        // 交易方向
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_买入.getValue());
        // 交易状态
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 交易状态
        trdFinProductDealTotal.setIsCancel(DictCodeListExt1.是否_否.getValue());
        // 交易对手
        trdFinProductDealTotal.setCounterId(dto.getCounterId());
        // 是否外部交易
        trdFinProductDealTotal.setIsOutTrade(DictCodeListExt1.是否_是.getValue());
        // 交易场所
        trdFinProductDealTotal.setTradeMarket(dto.getTradeMarket());
        // 交易平台
        if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(dto.getTradeMarket())) {
            trdFinProductDealTotal.setTradePlat(DictCodeList.交易平台_综合协议交易平台.getValue());
        } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(dto.getTradeMarket())) {
            trdFinProductDealTotal.setTradePlat(DictCodeList.交易平台_固定收益平台.getValue());
        } else if (DictCodeListExt1.交易场所_银行间.getValue().equals(dto.getTradeMarket())) {
            trdFinProductDealTotal.setTradePlat(DictCodeList.交易平台_外汇交易中心.getValue());
        }
        // 交易市场
        trdFinProductDealTotal.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue());
        // 交易员
        trdFinProductDealTotal.setTrader(dto.getOurTrader());
        // 备注
        trdFinProductDealTotal.setRemark(dto.getRemark());
        // 投管业务类型 = 数据字典.平安业务类型（PINAN_BUSINESS_TYPE）.202（债券分销买入确认）
        trdFinProductDealTotal.setTgBusiType(DictCodeListExt4.平安业务类型_债券分销买入确认.getValue());
        // 交易编号
        trdFinProductDealTotal.setpTotleTradeId(dto.getBidId());
        trdFinProductDealTotal.setWorkflowId(dto.getWorkflowId());
        trdFinProductDealTotal.setBusiType(BussinessDictCodeList.共同业务类型_一级投标确认_投标_分销买入.getValue());
//        现金流代码，金融产品代码+金融产品类型+现金流二级类型，还本、付息、付费等时存对应计划的现金流代码
//        前置交易编号，记录该交易的前置交易编号。比如申购确认时存申购申请交易编号							投标确认.投标编号
        for (CfetsTrdFirstBidFirmProductDto item : secIdProducts) {
            CfetsTrdFirstBidFirmInvestTradeDto result = new CfetsTrdFirstBidFirmInvestTradeDto();
            WflTrdFinProductDealTotal total = new WflTrdFinProductDealTotal();
            if (baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
                total.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            } else {
                total.setTotleTradeId(IDGenerator.generate());
            }
            BeanUtil.copyPropertiesIgnoreNull(total, trdFinProductDealTotal);
            // 净价总额 = 画面.投标产品列表.交易金额（元）
            total.setCpriceAmt(item.getTradeAmt());
            // 交易本金 = 画面.投标产品列表.券面总额
            total.setPrinAmt(item.getBidPriceTotal());
            // 利息总额 = 画面.投标产品列表.应计利息总额
            total.setIntAmt(item.getIntAmtTatol());
            // 全价总额 = 画面.投标产品列表.缴款金额
            total.setFpriceAmt(item.getPayAmt());
            // 随交易支付费用 = 0
            total.setFeeAmtPay(BigDecimal.ZERO);
            // 交易金额 = 画面.投标产品列表.缴款金额
            total.setTradeAmt(item.getPayAmt());
            // 未随交易支付费用 = 画面.投标产品列表.结算服务费
            total.setFeeAmtUnpay(item.getSettFeeAmt());
            // 总交易费用 = 画面.投标产品列表.结算服务费
            total.setFeeAmt(item.getSettFeeAmt());
            // 交易份额 = 画面.投标产品列表.券面总额/100
            total.setShareAmt(ArithmeticUtil.divide(item.getBidPriceTotal(), new BigDecimal(100), 4));
            // 证券管理户代码 "银行间时，为画面.投标产品列表.证券账户； 交易所时，为画面.投标产品列表.资金账户对应的证券账户"
            total.setSecManageAcctId(item.getSecManageAcctId());
            total.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            result.setWflTrdFinProductDealTotal(total);
            // 金融产品交易汇总附属表
            WflTrdDealTotalAdd trdDealTotalAdd = new WflTrdDealTotalAdd();
            // TODO 本方
//            trdDealTotalAdd.setOuPartyId(dto.getOurTrader());
            trdDealTotalAdd.setTotleTradeId(total.getTotleTradeId());
            trdDealTotalAdd.setWorkflowId(dto.getWorkflowId());
            trdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            result.setWflTrdDealTotalAdd(trdDealTotalAdd);
            List<WflTrdProductDeal> deals = new ArrayList<>();
            List<WflTrdProductDealAdd> dealAdds = new ArrayList<>();
            List<WflTrdTradeFeeDetail> feeDetails = new ArrayList<>();
            for (CfetsTrdFirstBidFirmProductDto productDto : item.getList()) {
                WflTrdProductDeal deal = new WflTrdProductDeal();
                if (baseGetBusinessId != null) {
                    BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
                    deal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
                } else {
                    deal.setTradeId(IDGenerator.generate());
                }
                BeanUtil.copyPropertiesIgnoreNull(deal, total);
                // 组合代码
//                deal.setPortfolioId(productDto.getPortfolioId());
                // 净价总额 = 画面.投标产品列表.交易金额（元）
                deal.setCpriceAmt(productDto.getTradeAmt());
                // 交易本金 = 画面.投标产品列表.券面总额
                deal.setPrinAmt(productDto.getBidPriceTotal());
                // 利息总额 = 画面.投标产品列表.应计利息总额
                deal.setIntAmt(productDto.getIntAmtTatol());
                // 全价总额 = 画面.投标产品列表.缴款金额
                deal.setFpriceAmt(productDto.getPayAmt());
                // 随交易支付费用 = 0
                deal.setFeeAmtPay(BigDecimal.ZERO);
                // 交易金额 = 画面.投标产品列表.缴款金额
                deal.setTradeAmt(productDto.getPayAmt());
                // 未随交易支付费用 = 画面.投标产品列表.结算服务费
                deal.setFeeAmtUnpay(productDto.getSettFeeAmt());
                // 总交易费用 = 画面.投标产品列表.结算服务费
                deal.setFeeAmt(productDto.getSettFeeAmt());
                // 交易份额 = 画面.投标产品列表.券面总额/100
                deal.setShareAmt(ArithmeticUtil.divide(productDto.getBidPriceTotal(), new BigDecimal(100), 4));
                // 证券管理户代码 "银行间时，为画面.投标产品列表.证券账户； 交易所时，为画面.投标产品列表.资金账户对应的证券账户"
                deal.setSecManageAcctId(productDto.getSecManageAcctId());
                // 业务模式
                deal.setInvAim(productDto.getBusinessModel());
                deal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                deal.setWorkflowId(dto.getWorkflowId());
                deals.add(deal);
                WflTrdProductDealAdd dealAdd = new WflTrdProductDealAdd();
                dealAdd.setTradeId(deal.getTradeId());
                dealAdd.setPortfolioId(productDto.getPortfolioId());
                dealAdd.setEntrustId(productDto.getEntrustId());
                dealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                dealAdd.setWorkflowId(dto.getWorkflowId());
                dealAdds.add(dealAdd);
                // 费用
                WflTrdTradeFeeDetail feeDetail = new WflTrdTradeFeeDetail();
                feeDetail.setTradeId(deal.getTradeId());
                feeDetail.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
                feeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                feeDetail.setFeeAmt(deal.getFeeAmt());
                feeDetail.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                feeDetail.setWorkflowId(dto.getWorkflowId());
                feeDetails.add(feeDetail);
            }
            result.setWflTrdProductDeals(deals);
            result.setWflTrdProductDealAdds(dealAdds);
            result.setWflTrdTradeFeeDetails(feeDetails);
            results.add(result);
        }
        return results;
    }

    public List<CfetsTrdFirstBidFirmInvestTradeDto> prepareBidEntity(CfetsTrdFirstBidFirmInfoDto dto) {
        List<CfetsTrdFirstBidFirmInvestTradeDto> list = new ArrayList<>();
        // 数据库取值
        List<WflTrdFinProductDealTotal> wflTotals = cfetsTrdFirstBidFirm002Mapper.findTotalByPTotleTradeId(dto.getBidId());
        for (WflTrdFinProductDealTotal wflTotal : wflTotals) {
            CfetsTrdFirstBidFirmInvestTradeDto tradeDto = new CfetsTrdFirstBidFirmInvestTradeDto();
            TrdFinProductDealTotal total = new TrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(total, wflTotal);
            tradeDto.setTrdFinProductDealTotal(total);
            tradeDto.setWflTrdFinProductDealTotal(wflTotal);
            WflTrdDealTotalAdd wflTotalAdd = wflTrdDealTotalAddMapper.selectByPrimaryKey(total.getTotleTradeId());
            TrdDealTotalAdd totalAdd = new TrdDealTotalAdd();
            BeanUtil.copyPropertiesIgnoreNull(totalAdd, wflTotalAdd);
            tradeDto.setTrdDealTotalAdd(totalAdd);
            tradeDto.setWflTrdDealTotalAdd(wflTotalAdd);
            // 汇总费用
            TrdTradeFeeTotal ttfd = new TrdTradeFeeTotal();
            ttfd.setTotleTradeId(total.getTotleTradeId());
            ttfd.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
            ttfd.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            ttfd.setFeeAmt(total.getFeeAmt());
            tradeDto.setTrdTradeFeeTotal(ttfd);
            // 结算指令表（STL_ORDERS）
            StlPayOrders stlPayOrders = new StlPayOrders();
            stlPayOrders.setTradeId(total.getTotleTradeId());
            if (baseGetBussinessId != null) {
                BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                bussinessCodeParameter.setSeqNo("9998");
                stlPayOrders.setOrderId(baseGetBussinessId.buildBussinessCode(bussinessCodeParameter));
            } else {
                stlPayOrders.setOrderId(IDGenerator.generate());
            }
            stlPayOrders.setSettleDate(total.getSettleDate());
            stlPayOrders.setMyAcctNo(total.getSecManageAcctId());
            stlPayOrders.setPs(DictCodeListExt1.收付方向_支出.getValue());
            stlPayOrders.setCounterAcctNo(total.getCounterContact());
            stlPayOrders.setSettleAmt(total.getTradeAmt());
            stlPayOrders.setCcy(total.getCcy());
            stlPayOrders.setSettleStatus(DictCodeList.结算状态_已结算.getValue());
            stlPayOrders.setStatus(DictCodeListExt1.交易状态_初始.getValue());
            stlPayOrders.setOrderStatus(DictCodeListExt1.交易状态_初始.getValue());
            stlPayOrders.setOrderRetStatus(DictCodeList.指令处理状态_处理成功.getValue());
            tradeDto.setStlPayOrders(stlPayOrders);
            // 数据库取值
            List<WflTrdProductDeal> wflDeals = cfetsTrdFirstBidFirm002Mapper.findDealByTotleTradeId(total.getTotleTradeId());
            List<TrdProductDeal> deals = new ArrayList<>();
            List<WflTrdProductDealAdd> wflDealAdds = new ArrayList<>();
            List<TrdProductDealAdd> dealAdds = new ArrayList<>();
            List<TrdTradeFeeDetail> feeDetails = new ArrayList<>();
            List<PtlTrade> ptlTrades = new ArrayList<>();
            for (WflTrdProductDeal wflDeal : wflDeals) {
                TrdProductDeal deal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(deal, wflDeal);
                WflTrdProductDealAdd wflDealAdd = new WflTrdProductDealAdd();
                wflDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflDealAdd.setTradeId(wflDeal.getTradeId());
                wflDealAdd = wflTrdProductDealAddMapper.selectByPrimaryKey(wflDealAdd);
                TrdProductDealAdd dealAdd = new TrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(dealAdd, wflDealAdd);
                deal.setPortfolioId(wflDealAdd.getPortfolioId());
                deals.add(deal);
                dealAdds.add(dealAdd);
                // 费用
                TrdTradeFeeDetail feeDetail = new TrdTradeFeeDetail();
                feeDetail.setTradeId(deal.getTradeId());
                feeDetail.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
                feeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                feeDetail.setFeeAmt(deal.getFeeAmt());
                feeDetails.add(feeDetail);
                //组合交易
                PtlTrade ptlTrade = new PtlTrade();
                BeanUtil.copyPropertiesIgnoreNull(ptlTrade, deal);
                ptlTrade.setBusiNo(deal.getTradeId());
                ptlTrade.setBusiId(deal.getFinprodId());
                ptlTrade.setBusiTable(BussinessDictCodeList.共同业务类型_一级投标确认_投标_分销买入.getValue());
                ptlTrades.add(ptlTrade);
            }
            tradeDto.setWflTrdProductDeals(wflDeals);
            tradeDto.setTrdProductDeals(deals);
            tradeDto.setWflTrdProductDealAdds(wflDealAdds);
            tradeDto.setTrdProductDealAdds(dealAdds);
            tradeDto.setTrdTradeFeeDetails(feeDetails);
            tradeDto.setPtlTrades(ptlTrades);
            list.add(tradeDto);
        }
        return list;
    }

    /**
     * 按证券账户维度汇总产品列表数据
     *
     * @param list
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmProductDto>
     * @author jiangpeng
     * @date 2022/5/25 19:32
     */
    private List<CfetsTrdFirstBidFirmProductDto> sumBySec(List<CfetsTrdFirstBidFirmProductDto> list) {
        // 按证券账户排序
        list.sort(Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getSecManageAcctId));
        List<CfetsTrdFirstBidFirmProductDto> productDtos = new ArrayList<>();
        CfetsTrdFirstBidFirmProductDto productDto = new CfetsTrdFirstBidFirmProductDto();
        list.forEach(item -> {
            if (StringUtil.isEmpty(productDto.getSecManageAcctId())
                    || !StringUtil.equals(productDto.getSecManageAcctId(), item.getSecManageAcctId())) {
                BeanUtil.copyPropertiesIgnoreNull(productDto, item);
                List<CfetsTrdFirstBidFirmProductDto> temps = new ArrayList<>();
                temps.add(item);
                productDto.setList(temps);
            } else {
                // 交易金额
                productDto.setTradeAmt(ArithmeticUtil.doAdd(productDto.getTradeAmt(), item.getTradeAmt()));
                // 券面总额
                productDto.setBidPriceTotal(ArithmeticUtil.doAdd(productDto.getBidPriceTotal(), item.getBidPriceTotal()));
                // 应计利息总额
                productDto.setIntAmtTatol(ArithmeticUtil.doAdd(productDto.getIntAmtTatol(), item.getIntAmtTatol()));
                // 缴款金额
                productDto.setPayAmt(ArithmeticUtil.doAdd(productDto.getPayAmt(), item.getPayAmt()));
                // 结算服务费
                productDto.setSettFeeAmt(ArithmeticUtil.doAdd(productDto.getSettFeeAmt(), item.getSettFeeAmt()));
                productDto.getList().add(item);
            }
            productDtos.add(productDto);
        });
        return productDtos;
    }

    /**
     * 退回或保存后提交
     *
     * @param entity
     * @author jiangpeng
     * @date 2022/4/28 19:54
     */
    @Override
    public void doUpdate(CfetsTrdFirstBidFirmInfoDto entity) {
        Map<String, Object> map = new HashMap<>();
        if (!StringUtil.isNotEmpty(entity.getTradeDate())) {
            entity.setTradeDate(DateUtil.parse(iPlatformDate.getSysDateStr()));
        }
        if ("save".equals(entity.getOperateType())) {
            return;
        }
        logger.debug("退回或保存后提交,入参:" + JsonUtils.object2Json(entity));
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            // 先删除流程中数据
            this.deleteWflData(entity.getBidId());
            //登录业务流程
            WflBidOrder wflBidOrder = new WflBidOrder();
            List<WflBidOrderBidPrice> wflBidOrderBidPrices = new ArrayList<>();
            // 组装数据
            this.prepareBidOrderAndList(entity, wflBidOrder, wflBidOrderBidPrices);
            // 数据入库
            this.insertWflData(wflBidOrder, wflBidOrderBidPrices);
            if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), entity.getBiddingMethod())) {
                List<CfetsTrdFirstBidFirmInvestTradeDto> tradeDtos = this.prepareBidEntity(entity);
                tradeDtos.forEach(tradeDto -> {
                    this.deleteTradeData(tradeDto);
                });
                // 组装数据-交易数据
                tradeDtos = this.prepareWflBidEntity(entity);
                // 数据入库-交易数据
                tradeDtos.forEach(tradeDto -> {
                    this.doInsertBidMethod(tradeDto);
                });
            }
            String confirmOk = RequestUtil.getParam("confirmOk");
            ProcessMark newEntity;
            //处理指令执行进度
//            this.insertEtrFinishStatus(entity, OperationType.NEW);
            // 发头寸
            try {
                if (StringUtil.isNotEmpty(entity.getWorkflowId())
                        && StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), entity.getBiddingMethod())) {
                    boolean isSubmit = true;
                    SysWflBusinessList sysWflBusinessList = sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
                    if (StringUtil.equals(sysWflBusinessList.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
                        isSubmit = false;
                        entity.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
                        this.sendPositionMethod(entity, isSubmit, confirmOk);
                    } else {
                        entity.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
                        this.sendPositionMethod(entity, isSubmit, confirmOk);
                    }
                }
            } catch (PositionException e) {
                Message message = PositionUtil.ajaxErrorReturn(e);
                BaseMap bm = (BaseMap) message.getData();
                String code = (String) bm.get("code");
                String messageStr = JsonUtils.json2Object((String) bm.get("message").toString(), String.class);
                if (StringUtil.isEmpty(confirmOk)
                        && StringUtil.equals(PositionHttpRequest.CODE_BUSI_RK, code)) {
                    WorkFlowUtil.showMessage(messageStr, MessageType.确认消息);
                }
                if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_YK, code)) {
                    WorkFlowUtil.showMessage(messageStr, MessageType.错误消息);
                }
                throw new PositionException(messageStr);
            }
            // 老头寸处理
//            this.dealOldPosition(entity, false);
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(CfetsTrdFirstBidFirmInfoDto.class, true);
                // 工作流ID
                newEntity.setWorkflowId(entity.getWorkflowId());
                newEntity.setStatus(entity.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(entity));
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理生效逻辑
     *
     * @param entity 页面元素
     * @param type   操作类型
     * @author jiangpeng
     * @date 2022/4/24 11:42
     */
    @Override
    public void doComplete(CfetsTrdFirstBidFirmInfoDto entity, OperationType type) {
        logger.debug("处理生效逻辑,入参:" + JsonUtils.object2Json(entity));
        this.deleteWflData(entity.getBidId());
        WflBidOrder wflBidOrder = new WflBidOrder();
        List<WflBidOrderBidPrice> wflBidOrderBidPrices = new ArrayList<>();
        // 组装数据
        this.prepareBidOrderAndList(entity, wflBidOrder, wflBidOrderBidPrices);
        // 数据入库
        this.insertData(wflBidOrder, wflBidOrderBidPrices, type);
        //头寸
        if (type.equals(OperationType.REVOKE) || type.equals(OperationType.DELETE)) {
            // 匹配取消
            PositionHttpRequest.dorevoke(entity.getBidId(), DictCodeListExt1.子系统代码_交易.getValue(),
                    DictCodeListExt3.头寸业务类型_债券一级投标.getValue(), DictCodeList.交易方向_买入.getValue());
            // 老头寸处理
//            this.dealOldPosition(entity, true);
        }
        if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), entity.getBiddingMethod())) {
            List<CfetsTrdFirstBidFirmInvestTradeDto> tradeDtos = this.prepareBidEntity(entity);
            tradeDtos.forEach(tradeDto -> {
                this.deleteTradeData(tradeDto);
                this.doCompleteBidMethod(entity, tradeDto, type);
            });
        }
        //处理指令执行进度
        this.insertEtrFinishStatus(entity, type);
    }

    /**
     * 分销买入 交易数据处理 - 生效
     *
     * @param entity
     * @param operationType
     * @author jiangpeng
     * @date 2022/5/6 16:26
     */
    public void doCompleteBidMethod(CfetsTrdFirstBidFirmInfoDto dto, CfetsTrdFirstBidFirmInvestTradeDto entity, OperationType operationType) {

        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {

            Map<String, Object> params = new HashMap<>();
            String bizCode = entity.getTrdFinProductDealTotal().getTotleTradeId();
            String settleRemark = entity.getTrdFinProductDealTotal().getRemark();
            if (bizCode.lastIndexOf('R') == bizCode.length() - 1) {
                bizCode = bizCode.substring(0, bizCode.length() - 1);
            }
            params.put("workflowId", dto.getWorkflowId());
            params.put("bizCode", bizCode);
            params.put("updateUser", SecurityUtil.getLoginUser().getUserName());
            if (StringUtil.isNotEmpty(settleRemark)) {
                params.put("revokeReason", "交易撤销:" + settleRemark);
            }
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/doRevokeComplete?", params,
                    String.class);
        } else {
            trdFinProductDealTotalMapper.insertSelective(entity.getTrdFinProductDealTotal());

            trdDealTotalAddMapper.insertSelective(entity.getTrdDealTotalAdd());

            //改造保存结算费经手费
            trdTradeFeeTotalMapper.insertSelective(entity.getTrdTradeFeeTotal());

            stlPayOrdersMapper.insertSelective(entity.getStlPayOrders());

            entity.getTrdProductDealAdds().forEach(trdProductDealAdd -> {
                trdProductDealAddMapper.insertSelective(trdProductDealAdd);
            });

            for (TrdProductDeal trdProductDeal : entity.getTrdProductDeals()) {
                trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
                trdProductDealMapper.insertSelective(trdProductDeal);
            }

            entity.getTrdTradeFeeDetails().forEach(
                    trdTradeFeeDetail -> trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail));

            entity.getPtlTrades().forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
        }
    }


    /**
     * 分销买入 交易数据处理 - 提交
     *
     * @param entity
     * @param operationType
     * @author jiangpeng
     * @date 2022/5/6 16:26
     */
    public void doInsertBidMethod(CfetsTrdFirstBidFirmInvestTradeDto entity) {

        wflTrdFinProductDealTotalMapper.insertSelective(entity.getWflTrdFinProductDealTotal());

        wflTrdDealTotalAddMapper.insertSelective(entity.getWflTrdDealTotalAdd());

        //改造保存结算费经手费
//        trdTradeFeeTotalMapper.insertSelective(entity.getTrdTradeFeeTotal());

//        stlPayOrdersMapper.insertSelective(entity.getStlPayOrders());

        entity.getWflTrdProductDealAdds().forEach(wflTrdProductDealAdd -> {
            wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        });

        entity.getWflTrdProductDeals().forEach(wflTrdProductDeal -> {
            wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            wflTrdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
            wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
        });

//        entity.getTrdTradeFeeDetails().forEach(
//                trdTradeFeeDetail -> trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail));
//
//        entity.getPtlTrades().forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
    }

    /**
     * 根据交易表数据组装dto
     *
     * @param bidId
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdFirstBidFirmInfoDto
     * @author jiangpeng
     * @date 2022/5/12 15:08
     */
    public CfetsTrdFirstBidFirmInfoDto prepareDto(String bidId) {
        CfetsTrdFirstBidFirmInfoDto entity = new CfetsTrdFirstBidFirmInfoDto();
        BidOrder bidOrder = bidOrderMapper.selectByPrimaryKey(bidId);
        BeanUtil.copyPropertiesIgnoreNull(entity, bidOrder);
        entity.setTradeDate(bidOrder.getBidDate());
        List<BidOrderBidPrice> list = bidOrderBidPriceMapper.selectListByPrimaryKey(bidId);
        List<CfetsTrdFirstBidFirmProductDto> productDtos = new ArrayList<>();
        for (BidOrderBidPrice item : list) {
            CfetsTrdFirstBidFirmProductDto productDto = new CfetsTrdFirstBidFirmProductDto();
            BeanUtil.copyPropertiesIgnoreNull(item, productDto);
            productDtos.add(productDto);
        }
        entity.setProductDtos(productDtos);
        return entity;
    }

    /**
     * 撤销，共外部调用
     *
     * @param totleTradeId
     * @param remark
     * @param isNeedDealPosition
     * @param tgBusiType
     * @author jiangpeng
     * @date 2022/5/21 17:13
     */
    public void doRevoke(String totleTradeId, String remark, Boolean isNeedDealPosition, String tgBusiType) {
        CfetsTrdFirstBidFirmInfoDto entity = this.prepareDto(totleTradeId);
        entity.setBidId(totleTradeId);
        WflBidOrder wflBidOrder = new WflBidOrder();
        List<WflBidOrderBidPrice> wflBidOrderBidPrices = new ArrayList<>();
        // 组装数据
        this.prepareBidOrderAndList(entity, wflBidOrder, wflBidOrderBidPrices);
        // 数据入库
        this.insertData(wflBidOrder, wflBidOrderBidPrices, OperationType.REVOKE);
        try {
            if (isNeedDealPosition) {
                // 匹配取消
//                cfetsTrdFirstBidFirm002Mapper.deleteByPreTradeId(entity.getBidId());
                PositionHttpRequest.dorevoke(entity.getBidId(), DictCodeListExt1.子系统代码_交易.getValue(),
                        DictCodeListExt3.头寸业务类型_债券一级申购.getValue(), DictCodeList.交易方向_买入.getValue());
            }
            // 老头寸处理
            this.dealOldPosition(entity, true);
            //处理指令执行进度
            this.insertEtrFinishStatus(entity, OperationType.REVOKE);
        } catch (PositionException e) {
            e.printStackTrace();
            Message message = PositionUtil.ajaxErrorReturn(e);
            throw new BusinessException("MSG:" + message.getMessage());
        }
    }

    /**
     * 退回
     *
     * @param entity 页面元素
     * @author jiangpeng
     * @date 2022/4/26 16:15
     */
    @Override
    public void doBack(CfetsTrdFirstBidFirmInfoDto entity) {
        this.deleteWflDataByWorkflowId(entity.getWorkflowId());
        // 老头寸处理
//        this.dealOldPosition(entity, true);
    }

    /**
     * 作废
     *
     * @param workflowId 流程id
     * @author jiangpeng
     * @date 2022/4/26 16:15
     */
    @Override
    public void doDelete(String workflowId) {
        ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
        CfetsTrdFirstBidFirmInfoDto entity = getBussinessEntityByWorkflowId(workflowId);
        if (StringUtil.isNotEmpty(processMark)
                && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
            PositionHttpRequest.doDeleteByWorkFlowId(entity.getWorkflowId(), DictCodeListExt1.子系统代码_交易.getValue(),
                    DictCodeListExt3.头寸业务类型_债券一级投标.getValue(), DictCodeList.交易方向_买入.getValue());
        }
    }

    /**
     * @param requst
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/4/27 10:30
     */
    public AjaxResult checkRevoke(CfetsTrdFirstBidFirmRevokeCheckRequst requst) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        for (CfetsTrdFirstBidFirmCheckDetaileRequst item : requst.getDetails()) {
            //如已有撤销数据在流程中，不允许进行再次进行撤销
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setPageId("CfetsTrdFirstBidFirm002");
            wflValidInputParameter.setBusinessDataPk1(item.getBidId());
            if (null != mstWorkflowCommonService &&
                    mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
                errorList.add(
                        new ValidMessage("存在流程中数据，不可撤销，请重新选择!"));
            }
            if (!(StringUtil.equals(item.getValidStatus(), DictCodeListExt1.交易流程状态_有效.getValue())
                    && StringUtil.equals(item.getFirstBidStatus(), DictCodeList.一级投标中标状态_已投标.getValue()))
                    && StringUtil.equals("01", requst.getType())) {
                errorList.add(new ValidMessage("仅可对有效已投标的数据进行撤销，请重新选择!"));
            }
            // TODO
            if (!(StringUtil.equals(item.getValidStatus(), DictCodeListExt1.交易流程状态_有效.getValue())
                    /*&& StringUtil.equals(item.getFirstBidStatus(), DictCodeList.一级投标中标状态_已投标.getValue())*/)
                    && StringUtil.equals("03", requst.getType())) {
                errorList.add(new ValidMessage("仅可对有效未清算成功的数据进行撤销，请重新选择!"));
            }
        }
        if (StringUtil.isNotEmpty(errorList)) {
            ajaxResult.setErrorList(errorList);
            ajaxResult.setMessageType(MessageType.错误消息);
        }
        return ajaxResult;
    }

    /**
     * 根据投标编号删除流程中数据
     *
     * @param bidId 投标编号
     * @author jiangpeng
     * @date 2022/4/26 9:51
     */
    private void deleteWflData(String bidId) {
        wflBidOrderMapper.deleteByPrimaryKey(bidId);
        wflBidOrderBidPriceMapper.deleteByBidId(bidId);
    }

    private void deleteTradeData(CfetsTrdFirstBidFirmInvestTradeDto entity) {
        wflTrdFinProductDealTotalMapper.deleteByPrimaryKey(entity.getWflTrdFinProductDealTotal());

        wflTrdDealTotalAddMapper.deleteByPrimaryKey(entity.getWflTrdDealTotalAdd().getTotleTradeId());

        entity.getWflTrdProductDealAdds().forEach(wflTrdProductDealAdd -> {
            wflTrdProductDealAddMapper.deleteByPrimaryKey(wflTrdProductDealAdd);
        });

        entity.getWflTrdProductDeals().forEach(wflTrdProductDeal -> {
            wflTrdProductDealMapper.deleteByPrimaryKey(wflTrdProductDeal);
        });
    }

    /**
     * 根据流程编号删除流程中数据
     *
     * @param workflowId 流程编号
     * @author jiangpeng
     * @date 2022/4/26 9:51
     */
    private void deleteWflDataByWorkflowId(String workflowId) {
        wflBidOrderMapper.deleteByWorkflowId(workflowId);
        wflBidOrderBidPriceMapper.deleteByWorkflowId(workflowId);
    }

    /**
     * 流程表数据入库
     *
     * @param wflBidOrder
     * @param wflBidOrderBidPrices
     * @author jiangpeng
     * @date 2022/4/26 9:57
     */
    private void insertWflData(WflBidOrder wflBidOrder, List<WflBidOrderBidPrice> wflBidOrderBidPrices) {
        wflBidOrder.setIsCancel(DictCodeList.是否_否.getValue());
        wflBidOrder.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflBidOrder.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        wflBidOrderMapper.insertSelective(wflBidOrder);
        for (WflBidOrderBidPrice wflBidOrderBidPrice : wflBidOrderBidPrices) {
            wflBidOrderBidPrice.setIsCancel(DictCodeList.是否_否.getValue());
            wflBidOrderBidPrice.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            wflBidOrderBidPrice.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflBidOrderBidPriceMapper.insertSelective(wflBidOrderBidPrice);
        }
    }

    /**
     * 流程生效时数据入库
     *
     * @param wflBidOrder
     * @param wflBidOrderBidPrices
     * @param type
     * @author jiangpeng
     * @date 2022/4/26 9:56
     */
    private void insertData(WflBidOrder wflBidOrder, List<WflBidOrderBidPrice> wflBidOrderBidPrices, OperationType type) {
        BidOrder bidOrder = new BidOrder();
        BeanUtil.copyPropertiesIgnoreNull(bidOrder, wflBidOrder);
        if (type.equals(OperationType.REVOKE) || type.equals(OperationType.DELETE)) {
            // 撤销数据处理
            bidOrder.setpBidId(bidOrder.getBidId());
            bidOrder.setBidId(StringUtil.join(bidOrder.getBidId(), "R"));
            bidOrder.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            bidOrder.setIsCancel(DictCodeList.是否_否.getValue());
            if (StringUtil.isNotEmpty(bidOrder.getRemark())) {
                bidOrder.setRemark(bidOrder.getRemark());
            }
        } else {
            bidOrder.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            bidOrder.setIsCancel(DictCodeList.是否_否.getValue());
        }
        bidOrderMapper.insertSelective(bidOrder);
        // 明细表数据入库
        for (WflBidOrderBidPrice wflbidOrderBidPrice : wflBidOrderBidPrices) {
            BidOrderBidPrice bidOrderBidPrice = new BidOrderBidPrice();
            BeanUtil.copyPropertiesIgnoreNull(bidOrderBidPrice, wflbidOrderBidPrice);
            if (type.equals(OperationType.REVOKE) || type.equals(OperationType.DELETE)) {
                bidOrderBidPrice.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                bidOrderBidPrice.setIsCancel(DictCodeList.是否_否.getValue());
                bidOrderBidPrice.setpBidId(bidOrder.getpBidId());
                bidOrderBidPrice.setBidId(bidOrder.getBidId());
            } else {
                bidOrderBidPrice.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                bidOrderBidPrice.setIsCancel(DictCodeList.是否_否.getValue());
                bidOrderBidPrice.setBidId(bidOrder.getBidId());
            }
            bidOrderBidPriceMapper.insertSelective(bidOrderBidPrice);
        }
        // 更新撤销的原交易数据
        if (type.equals(OperationType.REVOKE) || type.equals(OperationType.DELETE)) {
            cfetsTrdFirstBidFirm002Mapper.updateBidOrder(bidOrder.getpBidId(), bidOrder.getRemark());
            cfetsTrdFirstBidFirm002Mapper.updateBidOrderBidPrice(bidOrder.getpBidId());
        }
    }

    /**
     * 处理指令完成度
     *
     * @param dto           页面元素
     * @param operationType 操作类型
     */
    public void insertEtrFinishStatus(CfetsTrdFirstBidFirmInfoDto dto, OperationType operationType) {
        List<CfetsTrdFirstBidFirmProductDto> productDtos = dto.getProductDtos();
        if (CollectionUtils.isNotEmpty(productDtos)) {
            List<EtrFinishStatusDto> etrFinishStatusDtos = new ArrayList<>();
            Map<String, EtrFinishStatusDto> etrFinishStatusDtoMap = new HashMap<>();
            for (CfetsTrdFirstBidFirmProductDto productDto : productDtos) {
                EtrFinishStatusDto status = new EtrFinishStatusDto();
                // 考虑到存在多组合指令的情况
                if (etrFinishStatusDtoMap.get(productDto.getEntrustId()) != null) {
                    status = etrFinishStatusDtoMap.get(productDto.getEntrustId());
                    status.setEntFinishAmt(ArithmeticUtil.doAdd(status.getEntFinishAmt(), productDto.getTotalBiddingNum()));
                } else {
                    status.setEntrustId(productDto.getEntrustId());
                    status.setTradeId(dto.getBidId());
                    status.setFinishDate(dto.getTradeDate());
                    status.setEntFinishAmt(productDto.getTotalBiddingNum());
                    status.setTrader(dto.getOurTrader());
                    etrFinishStatusDtoMap.put(productDto.getEntrustId(), status);
                }
            }
            for (Map.Entry<String, EtrFinishStatusDto> map : etrFinishStatusDtoMap.entrySet()) {
                etrFinishStatusDtos.add(map.getValue());
                // 撤销删除时,页面传过来的就是原始交易编号
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    baseEtrInvestInfoServiceImpl.cancelCompleteDelegate(map.getKey(), dto.getBidId());
                }
            }
            if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
                baseEtrInvestInfoServiceImpl.completeDelegate(etrFinishStatusDtos);
            }
        }
    }

    /**
     * 组装输入参数
     *
     * @param param 参数map集
     * @author jiangpeng
     * @date 2022/4/19 14:21
     */
    private void setParam(Map<String, Object> param) {

        param.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());

        param.put("YN_N", DictCodeListExt1.是否_否.getValue());

        param.put("TENDER_TYPE_01", DictCodeListExt4.招标方式_单一价格招标.getValue());
        param.put("TENDER_TYPE_02", DictCodeListExt4.招标方式_数量招标.getValue());
        param.put("TENDER_TYPE_03", DictCodeListExt4.招标方式_荷兰式招标.getValue());
        param.put("TENDER_TYPE_04", DictCodeListExt4.招标方式_混合式招标.getValue());

        param.put("WIN_TYPE_01", DictCodeListExt4.投标方式_自主投标.getValue());
        param.put("WIN_TYPE_02", DictCodeListExt4.投标方式_代投代缴.getValue());
        param.put("WIN_TYPE_03", DictCodeListExt4.投标方式_分销买入.getValue());

        param.put("ISSUE_CLASS_02", DictCodeListExt4.新债_发行方式_招标发行.getValue());
        param.put("ISSUE_CLASS_03", DictCodeListExt4.新债_发行方式_簿记建档.getValue());

        param.put("sysDate", iPlatformDate.getSysDateStr());

        String loginUserId = LoginUserUtil.getUserName();
        param.put("loginUserId", loginUserId);
    }

    /**
     * 分页设置默认值
     *
     * @param pageMode 页面信息
     * @author jiangpeng
     * @date 2022/4/19 14:22
     */
    private PageMode setPageMode(PageMode pageMode) {
        if (pageMode == null) {
            pageMode = new PageMode();
        }
        int pageNo = StringUtil.isNotEmpty(pageMode.getPageNo()) ? pageMode.getPageNo() : 1;//默认第一页
        int pageSize = StringUtil.isNotEmpty(pageMode.getPageSize()) ? pageMode.getPageSize() : 10;//默认每页10条
        pageMode.setPageNo(pageNo);
        pageMode.setPageSize(pageSize);
        return pageMode;
    }

    /**
     * 尾差处理,最小变动单位序列分配算法
     *
     * @param productDtos    产品列表
     * @param tailDifference 尾差
     * @param minChangeUnit  最小变动单位
     * @author jiangpeng
     * @date 2022/4/24 9:49
     */
    private void handleTailDifference(List<CfetsTrdFirstBidFirmProductDto> productDtos,
                                      BigDecimal tailDifference, BigDecimal minChangeUnit) {
        // 最大循环次数
        int maxCycle = productDtos.size() * 30;
        // 降序
        while (tailDifference.compareTo(BigDecimal.ZERO) > 0 && maxCycle != 0) {
            for (CfetsTrdFirstBidFirmProductDto item : productDtos) {
                maxCycle--;
                // 分配值
                BigDecimal assign = BigDecimal.ZERO;
                // 尾差等于0,循环结束
                if (BigDecimalUtil.isZero(tailDifference)) {
                    break;
                }
                // 处理尾差值的不同
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) >= 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) >= 0) {
                    // 剩余指令额度大于等于最小分配单位 尾差大于等于最小变动单位 时,分配最小变动单位
                    assign = minChangeUnit;
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) >= 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) < 0) {
                    // 剩余指令额度大于最小分配单位 尾差小于最小变动单位 时,直接分配尾差的值
                    assign = tailDifference;
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 4) >= 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) < 0) {
                    // 剩余指令额度小于最小分配单位但不小于尾差时,直接分配尾差的值
                    assign = tailDifference;
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 4) < 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) < 0) {
                    // 剩余指令额度小于最小分配单位且小于尾差时,直接分配剩余指令额度
                    assign = item.getEtrBidPriceTotal();
                }
                // 券面总额加上分配值
                item.setTotalBiddingNum(ArithmeticUtil.doAdd(item.getTotalBiddingNum(), assign));
                // 尾差减去分配值
                tailDifference = ArithmeticUtil.subtract(tailDifference, assign);
                // 剩余指令额度减去分配值
                item.setAvaNumTemp(ArithmeticUtil.subtract(item.getAvaNumTemp(), assign));
            }
        }
    }

    /**
     * 尾差处理,最小变动单位序列分配算法
     *
     * @param productDtos    产品列表
     * @param tailDifference 尾差
     * @param minChangeUnit  最小变动单位
     * @author jiangpeng
     * @date 2022/4/24 9:49
     */
    private void handleTailDifferenceSubscriptionAmount(List<CfetsTrdFirstBidFirmProductDto> productDtos,
                                                        BigDecimal tailDifference, BigDecimal minChangeUnit) {
        // 最大循环次数
        int maxCycle = productDtos.size() * 30;
        // 降序
        productDtos.sort(Comparator.comparing(CfetsTrdFirstBidFirmProductDto::getAvaNum).reversed());
        while (tailDifference.compareTo(BigDecimal.ZERO) > 0 && maxCycle != 0) {
            for (CfetsTrdFirstBidFirmProductDto item : productDtos) {
                maxCycle--;
                // 分配值
                BigDecimal assign = BigDecimal.ZERO;
                // 尾差等于0,循环结束
                if (BigDecimalUtil.isZero(tailDifference)) {
                    break;
                }
                //指令剩余额度为0时,处理下一条数据
                if (BigDecimalUtil.isZero(item.getAvaNumTemp())) {
                    continue;
                }
                // 处理尾差值的不同
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) >= 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) >= 0) {
                    // 剩余指令额度大于等于最小分配单位 尾差大于等于最小变动单位 时,分配最小变动单位
                    assign = minChangeUnit;
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) >= 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) < 0) {
                    // 剩余指令额度大于最小分配单位 尾差小于最小变动单位 时,直接分配尾差的值
                    assign = tailDifference;
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 4) >= 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) < 0) {
                    // 剩余指令额度小于最小分配单位但不小于尾差时,直接分配尾差的值
                    assign = tailDifference;
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 4) < 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) < 0) {
                    // 剩余指令额度小于最小分配单位且小于尾差时,直接分配剩余指令额度
                    assign = item.getAvaNumTemp();
                }
                // 券面总额加上分配值
                item.setBidPriceTotal(ArithmeticUtil.doAdd(item.getBidPriceTotal(), assign));
                // 尾差减去分配值
                tailDifference = ArithmeticUtil.subtract(tailDifference, assign);
                // 剩余指令额度减去分配值
                item.setAvaNumTemp(ArithmeticUtil.subtract(item.getAvaNumTemp(), assign));
            }
        }
    }

    /**
     * 交易金额/应计利息总额/缴款金额和结算服务费 尾差处理,最小变动单位序列分配算法
     *
     * @param productDtos
     * @param tradeTailDifference 交易金额
     * @param intTailDifference   应计利息总额
     * @param payTailDifference   缴款金额
     * @param feeTailDifference   结算服务费
     * @author jiangpeng
     * @date 2022/5/19 14:34
     */
    private void handleTailDifferenceOhter(List<CfetsTrdFirstBidFirmProductDto> productDtos, BigDecimal tradeTailDifference,
                                           BigDecimal intTailDifference, BigDecimal payTailDifference, BigDecimal feeTailDifference) {
        //递增维度
        BigDecimal addDimension = payMinChangeUnit;
        // 交易金额分配值
        BigDecimal tradeAssign = BigDecimal.ZERO;
        // 应计利息总额分配值
        BigDecimal intAssign = BigDecimal.ZERO;
        // 缴款金额分配值
        BigDecimal payAssign = BigDecimal.ZERO;
        // 费用分配值
        BigDecimal feeAssign = BigDecimal.ZERO;
        int tradeSize = productDtos.size();
        Set<String> tradeSet = new HashSet<>();
        int intSize = productDtos.size();
        Set<String> intSet = new HashSet<>();
        int paySize = productDtos.size();
        Set<String> paySet = new HashSet<>();
        int feeSize = productDtos.size();
        Set<String> feeSet = new HashSet<>();
        while ((tradeTailDifference.compareTo(BigDecimal.ZERO) > 0 || intTailDifference.compareTo(BigDecimal.ZERO) > 0 ||
                payTailDifference.compareTo(BigDecimal.ZERO) > 0 || feeTailDifference.compareTo(BigDecimal.ZERO) > 0)
                && (tradeSize != 0 || intSize != 0 || paySize != 0 || feeSize != 0)) {
            if (tradeTailDifference.compareTo(payMinChangeUnit) > 0) {
                tradeAssign = ArithmeticUtil.divide(tradeTailDifference, new BigDecimal(tradeSize), 2, RoundingMode.HALF_UP);
                tradeAssign = ArithmeticUtil.multiply(ArithmeticUtil.divide(tradeAssign, payMinChangeUnit, 0, RoundingMode.DOWN), payMinChangeUnit);
            }
            if (intTailDifference.compareTo(payMinChangeUnit) > 0) {
                intAssign = ArithmeticUtil.divide(intTailDifference, new BigDecimal(intSize), 2, RoundingMode.HALF_UP);
                intAssign = ArithmeticUtil.multiply(ArithmeticUtil.divide(intAssign, payMinChangeUnit, 0, RoundingMode.DOWN), payMinChangeUnit);
            }
            if (payTailDifference.compareTo(payMinChangeUnit) > 0) {
                payAssign = ArithmeticUtil.divide(payTailDifference, new BigDecimal(paySize), 2, RoundingMode.HALF_UP);
                payAssign = ArithmeticUtil.multiply(ArithmeticUtil.divide(payAssign, payMinChangeUnit, 0, RoundingMode.DOWN), payMinChangeUnit);
            }
            if (feeTailDifference.compareTo(payMinChangeUnit) > 0) {
                feeAssign = ArithmeticUtil.divide(feeTailDifference, new BigDecimal(feeSize), 2, RoundingMode.HALF_UP);
                feeAssign = ArithmeticUtil.multiply(ArithmeticUtil.divide(feeAssign, payMinChangeUnit, 0, RoundingMode.DOWN), payMinChangeUnit);
            }
            for (CfetsTrdFirstBidFirmProductDto item : productDtos) {
                // 尾差等于0,循环结束
                if (BigDecimalUtil.isZero(tradeTailDifference) && BigDecimalUtil.isZero(intTailDifference) &&
                        BigDecimalUtil.isZero(payTailDifference) && BigDecimalUtil.isZero(feeTailDifference)) {
                    break;
                }
                if (BigDecimalUtil.ACompareToB(tradeAssign, payMinChangeUnit, 2) > 0) {
                    //
                } else if (BigDecimalUtil.ACompareToB(tradeTailDifference, payMinChangeUnit, 4) >= 0) {
                    // 尾差值大于等于最小分配单位
                    tradeAssign = payMinChangeUnit;
                } else {
                    tradeAssign = tradeTailDifference;
                    if (tradeSet.add(item.getEntrustId())) {
                        tradeSize--;
                    }
                }

                if (BigDecimalUtil.ACompareToB(intAssign, payMinChangeUnit, 2) > 0) {
                    //
                } else if (BigDecimalUtil.ACompareToB(intTailDifference, payMinChangeUnit, 4) >= 0) {
                    // 尾差值大于等于最小分配单位
                    intAssign = payMinChangeUnit;
                } else {
                    intAssign = intTailDifference;
                    if (intSet.add(item.getEntrustId())) {
                        intSize--;
                    }
                }

                if (BigDecimalUtil.ACompareToB(payAssign, payMinChangeUnit, 2) > 0) {
                    //
                } else if (BigDecimalUtil.ACompareToB(payTailDifference, payMinChangeUnit, 4) >= 0) {
                    // 尾差值大于等于最小分配单位
                    payAssign = payMinChangeUnit;
                } else {
                    payAssign = payTailDifference;
                    if (paySet.add(item.getEntrustId())) {
                        paySize--;
                    }
                }

                if (BigDecimalUtil.ACompareToB(feeAssign, feeMinChangeUnit, 2) > 0) {
                    //
                } else if (BigDecimalUtil.ACompareToB(feeTailDifference, feeMinChangeUnit, 4) >= 0) {
                    // 尾差值大于等于最小分配单位
                    feeAssign = payMinChangeUnit;
                } else {
                    feeAssign = feeTailDifference;
                    if (feeSet.add(item.getEntrustId())) {
                        feeSize--;
                    }
                }
                // 交易金额加上分配值
                item.setTradeAmt(ArithmeticUtil.doAdd(item.getTradeAmt(), tradeAssign));
                // 应计利息总额加上分配值
                item.setIntAmt(ArithmeticUtil.doAdd(item.getIntAmt(), intAssign));
                // 缴款金额加上分配值
                item.setPayAmt(ArithmeticUtil.doAdd(item.getPayAmt(), payAssign));
                // 结算费用加上分配值
                item.setSettFeeAmt(ArithmeticUtil.doAdd(item.getSettFeeAmt(), feeAssign));
                // 尾差减去分配值
                tradeTailDifference = ArithmeticUtil.subtract(tradeTailDifference, tradeAssign);
                // 尾差减去分配值
                intTailDifference = ArithmeticUtil.subtract(intTailDifference, intAssign);
                // 尾差减去分配值
                payTailDifference = ArithmeticUtil.subtract(payTailDifference, payAssign);
                // 尾差减去分配值
                feeTailDifference = ArithmeticUtil.subtract(feeTailDifference, feeAssign);
            }
        }
    }

    /**
     * 必填项校验
     *
     * @param dto
     * @return java.util.List<com.joyintech.framework.validation.ValidMessage>
     * @author jiangpeng
     * @date 2022/5/21 11:30
     */

    /**
     * 自主投标必填校验
     *
     * @param dto
     * @return java.util.List<com.joyintech.framework.validation.ValidMessage>
     * @author jiangpeng
     * @date 2022/5/21 11:31
     */
    private List<ValidMessage> checkZHNotEmpty(CfetsTrdFirstBidFirmInfoDto dto) {
        List<ValidMessage> errorList = new ArrayList<>();
        String errorInfo = "自主投标时";
        // 汇总投标券面总额
        if (!StringUtil.isNotEmpty(dto.getTotalBiddingNum())) {
            errorList.add(new ValidMessage(errorInfo + "汇总投标券面总额不可为空!"));
        }
        // 调整策略
        if (!StringUtil.isNotEmpty(dto.getAdjustStrategy())) {
            errorList.add(new ValidMessage(errorInfo + "调整策略不可为空!"));
//            errorList.add(new ValidMessage(getMessage("COMMON_NOTEMPTY_003", errorInfo,"调整策略")));
        }
        for (CfetsTrdFirstBidFirmProductDto item : dto.getProductDtos()) {
            // 指令代码
            if (!StringUtil.isNotEmpty(item.getEntrustId())) {
                errorList.add(new ValidMessage("投标产品列表中指令代码不可为空!"));
            }
            // 组合名称
            if (!StringUtil.isNotEmpty(item.getPortfolioName())) {
                errorList.add(new ValidMessage("投标产品列表中组合名称不可为空!"));
            }
            // 汇总投标券面总额
            if (!StringUtil.isNotEmpty(item.getTotalBiddingNum())) {
                errorList.add(new ValidMessage("投标产品列表中汇总投标券面总额不可为空!"));
            } else if (BigDecimalUtil.ACompareToB(item.getTotalBiddingNum(), BigDecimal.ZERO, 4) <= 0
                    || BigDecimalUtil.ACompareToB(item.getTotalBiddingNum(), item.getEtrBidPriceTotal(), 4) > 0) {
                errorList.add(new ValidMessage("投标产品列表中汇总投标券面总额必须大于0，小于等于指令投标券面总额!"));
            }
            // 指令投标券面总额
            if (!StringUtil.isNotEmpty(item.getEtrBidPriceTotal())) {
                errorList.add(new ValidMessage("投标产品列表中指令投标券面总额不可为空!"));
            }
            // 业务模式
            if (!StringUtil.isNotEmpty(item.getBusinessModel())) {
                errorList.add(new ValidMessage("投标产品列表中业务模式不可为空!"));
            }
            // 投资经理
            if (!StringUtil.isNotEmpty(item.getInvManagerId())) {
                errorList.add(new ValidMessage("投标产品列表中投资经理不可为空!"));
            }
            // 投资经理
            if (!StringUtil.isNotEmpty(item.getInvManagerName())) {
                errorList.add(new ValidMessage("投标产品列表中投资经理不可为空!"));
            }
            // 投标产品列表中汇总投标券面总额要小于等于指令投标券面总额
            if (BigDecimalUtil.ACompareToB(item.getTotalBiddingNum(), item.getEtrBidPriceTotal(), 4) > 0) {
                errorList.add(new ValidMessage("投标产品列表中汇总投标券面总额要小于等于指令投标券面总额!"));
            }
        }
        return errorList;
    }

    /**
     * 分销买入必填校验
     *
     * @param dto
     * @return java.util.List<com.joyintech.framework.validation.ValidMessage>
     * @author jiangpeng
     * @date 2022/5/21 11:31
     */
    private List<ValidMessage> checkFXNotEmpty(CfetsTrdFirstBidFirmInfoDto dto) {
        List<ValidMessage> errorList = new ArrayList<>();
        String errorInfo = "分销买入时";
        // 缴款日
        if (!StringUtil.isNotEmpty(dto.getPayDate())) {
            errorList.add(new ValidMessage(errorInfo + "缴款日不可为空!"));
        }
        // 债权债务登记日
        if (!StringUtil.isNotEmpty(dto.getAssignRegdate())) {
            errorList.add(new ValidMessage(errorInfo + "债权债务登记日不可为空!"));
        }
        // 上市日
//        if (!StringUtil.isNotEmpty(dto.getListingDate())) {
//            errorList.add(new ValidMessage(errorInfo + "上市日不可为空!"));
//        }
        // 券面总额
        if (!StringUtil.isNotEmpty(dto.getBidPriceTotal())) {
            errorList.add(new ValidMessage(errorInfo + "券面总额不可为空!"));
        }
        // 中标标位
        if (!StringUtil.isNotEmpty(dto.getWinPrice())) {
            errorList.add(new ValidMessage(errorInfo + "中标标位不可为空!"));
        }
        // 净价
        if (!StringUtil.isNotEmpty(dto.getPrice())) {
            errorList.add(new ValidMessage(errorInfo + "净价不可为空!"));
        }
        // 应计利息
        if (!StringUtil.isNotEmpty(dto.getIntAmt())) {
            errorList.add(new ValidMessage(errorInfo + "应计利息不可为空!"));
        }
        // 分配方式
        if (!StringUtil.isNotEmpty(dto.getDipType())) {
            errorList.add(new ValidMessage(errorInfo + "分配方式不可为空!"));
        }
        // 缴款金额
        if (!StringUtil.isNotEmpty(dto.getPayableAmount())) {
            errorList.add(new ValidMessage("缴款金额不可为空!"));
        }
        // 结算服务费
        if (StringUtil.isNotEmpty(dto.getSettFeeAmt())
                && BigDecimalUtil.ACompareToB(dto.getSettFeeAmt(), BigDecimal.ZERO, 4) < 0) {
            errorList.add(new ValidMessage(errorInfo + "结算服务费必须大于等于0!"));
        }
        for (CfetsTrdFirstBidFirmProductDto item : dto.getProductDtos()) {
            // 指令代码
            if (!StringUtil.isNotEmpty(item.getEntrustId())) {
                errorList.add(new ValidMessage("投标产品列表中指令代码不可为空!"));
            }
            // 组合名称
            if (!StringUtil.isNotEmpty(item.getPortfolioName())) {
                errorList.add(new ValidMessage("投标产品列表中组合名称不可为空!"));
            }
            // 券面总额
            if (!StringUtil.isNotEmpty(item.getBidPriceTotal())) {
                errorList.add(new ValidMessage("投标产品列表中券面总额不可为空!"));
            } else if (BigDecimalUtil.ACompareToB(item.getBidPriceTotal(), BigDecimal.ZERO, 4) <= 0
                    || BigDecimalUtil.ACompareToB(item.getBidPriceTotal(), item.getAvaNum(), 4) > 0) {
                errorList.add(new ValidMessage("投标产品列表中券面总额总额必须大于0，小于等于剩余指令额度!"));
            }
            // 结算服务费
            if (StringUtil.isNotEmpty(item.getSettFeeAmt())
                    && BigDecimalUtil.ACompareToB(item.getSettFeeAmt(), BigDecimal.ZERO, 4) < 0) {
                errorList.add(new ValidMessage("投标产品列表中结算服务费必须大于等于0!"));
            }
            // 交易金额
            if (!StringUtil.isNotEmpty(item.getTradeAmt())) {
                errorList.add(new ValidMessage("投标产品列表中交易金额不可为空!"));
            }
            // 应计利息总额
            if (!StringUtil.isNotEmpty(item.getIntAmtTatol())) {
                errorList.add(new ValidMessage("投标产品列表中应计利息总额不可为空!"));
            }
            // 缴款金额
            if (!StringUtil.isNotEmpty(item.getPayAmt())) {
                errorList.add(new ValidMessage("投标产品列表中缴款金额不可为空!"));
            }
            // 投标标位
            if (!StringUtil.isNotEmpty(item.getBidPrice())) {
                errorList.add(new ValidMessage("投标产品列表中投标标位不可为空!"));
            }
            // 业务模式
            if (!StringUtil.isNotEmpty(item.getBusinessModel())) {
                errorList.add(new ValidMessage("投标产品列表中业务模式不可为空!"));
            }
            // 证券账户
            if (!StringUtil.isNotEmpty(item.getSecManageAcctId())) {
                errorList.add(new ValidMessage("投标产品列表中证券账户不可为空!"));
            }
            // 投资经理
            if (!StringUtil.isNotEmpty(item.getInvManagerId())) {
                errorList.add(new ValidMessage("投标产品列表中投资经理不可为空!"));
            }
            // 投资经理
            if (!StringUtil.isNotEmpty(item.getInvManagerName())) {
                errorList.add(new ValidMessage("投标产品列表中投资经理不可为空!"));
            }
            // 投标方式为分销买入时，投标产品列表中存在券面总额(万元)>剩余指令额度(万元)-流程汇总券面总额时
        }
        return errorList;
    }

    /**
     * 头寸处理
     *
     * @param entity
     * @param isSubmit
     * @param confirmOk
     * @author jiangpeng
     * @date 2021/12/24 10:12
     */
    private void sendPositionMethod(CfetsTrdFirstBidFirmInfoDto entity, boolean isSubmit, String confirmOk) {
        List<CfetsBondPortfolioCashRequest> ptlList = new ArrayList<>();

        for (CfetsTrdFirstBidFirmProductDto productDto : entity.getProductDtos()) {
            //调用头寸
            CfetsBondPortfolioCashRequest posport = new CfetsBondPortfolioCashRequest();
            posport.setPortfolioId(productDto.getPortfolioId());
            posport.setEtrId(productDto.getEntrustId());
            posport.setTradeId(entity.getBidId());
            if (StringUtil.isNotEmpty(productDto.getSecManageAcctId())) {
                posport.setSecAcctId(productDto.getSecManageAcctId()); // 证券户
            }
            if (StringUtil.isNotEmpty(productDto.getPortfolioId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", productDto.getPortfolioId());
                map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                String cashAcctId = joyinbusinessCommonService.qryPortfolioCashAcctId(map);
                if (StringUtil.isNotEmpty(cashAcctId)) {
                    posport.setCashAcctId(cashAcctId); // 托管户
                }
            }
            posport.setCashAmount(productDto.getTradeAmt());
            posport.setSecAmount(productDto.getBidPriceTotal());
            posport.setOwnSecAmount(productDto.getBidPriceTotal().toPlainString());
            ptlList.add(posport);
        }
        //调用头寸
        CfetsBondPositionRequest req = new CfetsBondPositionRequest();
        req.setInterfaceType("Bond001");//报文类型
        req.setFinprodId(entity.getFinprodId());
        req.setUserId(SecurityUtil.getLoginUser().getUserName());
        req.setTotalTradeId(entity.getBidId());
        req.setFlowStatus(DictCodeListExt3.流程节点_流程提交.getValue());
        req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
        req.setWorkFlowId(entity.getWorkflowId());
        String balanceCheck = DictCodeList.是否_是.getValue();
        if ((StringUtil.isNotEmpty(entity.getSoftSkipMode())
                && StringUtil.equals(entity.getSoftSkipMode(), DictCodeList.是否_是.getValue()))
                || StringUtil.isNotEmpty(confirmOk)) {
            balanceCheck = DictCodeList.是否_否.getValue();
        }
        req.setBalanceCheck(balanceCheck);
        req.setBusiType(DictCodeListExt3.头寸业务类型_债券一级投标.getValue());
        req.setTradePlace(entity.getTradeMarket());
        req.setBusiAct(DictCodeListExt3.业务动作_汇总确认.getValue());
        req.setPs(DictCodeList.交易方向_买入.getValue());
        req.setPurchModle(DictCodeListExt4.头寸申购方式_分销买入.getValue());
        if (isSubmit) {
            req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
            req.setSystemAct(entity.getSystemAct());
        } else {
            req.setDecisionAct(DictCodeListExt3.系统动作_修改.getValue());
            req.setSystemAct(entity.getSystemAct());
            req.setbTotalTradeId(entity.getBidId());
        }
        req.setCdate(entity.getTradeDate());
        req.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        req.setPtlList(ptlList);
        PositionHttpRequest.send(req);
    }

    /**
     * TODO 老头存处理 系统参数是否处理旧头寸标识(IS_HANDLE_OLDPOS_FLAG)为Y时处理
     *
     * @param entity         页面元素
     * @param isRevokeOrBack 交易流程状态 参数字典 TRADE_FLOW_STATUS
     * @author jiangpeng
     * @date 2022/4/28 10:37
     */
    public void dealOldPosition(CfetsTrdFirstBidFirmInfoDto entity, boolean isRevokeOrBack) {
//        if (StringUtil.isNotEmpty(entity)) {
//            return;
//        }
        if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            // 撤销或退回
            entity.setTradeDate(iPlatformDate.getSysDate());
            if (isRevokeOrBack) {
                baseAssetCashPositionCommMapper.deleteCashPositionByOrderIds(Arrays.asList(entity.getBidId()));
            } else {
                for (CfetsTrdFirstBidFirmProductDto deal : entity.getProductDtos()) {
                    AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                    FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    finProductTradeDto.setAssetType2("ISSUE_CLASS_01");
                    finProductTradeDto.setIsExistNew(DictCodeList.是否_是.getValue());
                    BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
                    baseTradeProductDealDto.setEntrustId(deal.getEntrustId());
                    TrdProductDeal trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, deal);
                    trdProductDeal.setFinprodId(entity.getFinprodId());
                    trdProductDeal.setFinprodType2(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
                    trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
                    trdProductDeal.setSettleDate(entity.getTradeDate());
//                    trdProductDeal.setPrinAmt(deal.getSubscriptionAmount());
                    trdProductDeal.setTradeDate(entity.getTradeDate());
                    trdProductDeal.setSettleDate(entity.getTradeDate());
                    baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
                    TrdProductDealAdd add = new TrdProductDealAdd();
                    add.setPortfolioId(deal.getPortfolioId());
                    add.setTradeId(entity.getBidId());
                    add.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
                    add.setEntrustId(deal.getEntrustId());
                    baseTradeProductDealDto.setTrdProductDealAdd(add);
                    //处理指令完成情况
                    List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
                    StlPayOrdersDto stlPayOrder = new StlPayOrdersDto();
                    stlPayOrder.setMyAcctNo(deal.getSecManageAcctId());
                    stlPayOrder.setOrderId(entity.getBidId());
                    stlPayOrder.setCcy("CNY");
//                    stlPayOrder.setSettleAmt(deal.getPayableAmount());
                    stlPayOrder.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
                    stlPayOrder.setPs(DictCodeListExt1.收支方向_支出.getValue());
                    stlPayOrder.setSettleDate(entity.getTradeDate());
                    stlPayOrders.add(stlPayOrder);
                    baseTradeProductDealDto.setStlPayOrdersList(stlPayOrders);
                    finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
                    baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                }
            }
        }
    }


    @Override
    public String generateTitle(CfetsTrdFirstBidFirmInfoDto dto, OperationType type) {
        String str = "投标日期:" + DateUtil.format(iPlatformDate.getSysDate());
        String finprodAbbr = null;
        String counterName = null;
        String tradeAmt = null;
        if (StringUtil.isNotEmpty(dto.getFinprodAbbr())) {
            finprodAbbr = "资产简称:" + dto.getFinprodAbbr();
        }
        if (StringUtil.isNotEmpty(dto.getCounterName())) {
            counterName = "对手方:" + dto.getCounterName();
        }
        if (StringUtil.isNotEmpty(dto.getTotalBiddingNum())) {
            tradeAmt = "汇总投标券面总额:" +
                    ArithmeticUtil.divide(dto.getTotalBiddingNum(), new BigDecimal(10000), 2, RoundingMode.HALF_UP).toPlainString() + "万元";
        }
        if (StringUtil.equals(DictCodeListExt4.投标方式_自主投标.getValue(), dto.getBiddingMethod())) {
            //  自主投标
        } else if (StringUtil.equals(DictCodeListExt4.投标方式_代投代缴.getValue(), dto.getBiddingMethod())) {
            //代投代缴
        } else if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), dto.getBiddingMethod())) {
            // 分销买入
            str = "交易日期:" + DateUtil.format(iPlatformDate.getSysDate());
            tradeAmt = "分销买入券面总额:" +
                    ArithmeticUtil.divide(dto.getBidPriceTotal(), new BigDecimal(10000), 2, RoundingMode.HALF_UP).toPlainString() + "万元";
        }
        str += StringUtil.isNotEmpty(finprodAbbr) ? "-" + finprodAbbr : "";
        str += StringUtil.isNotEmpty(counterName) ? "-" + counterName : "";
        str += StringUtil.isNotEmpty(tradeAmt) ? "-" + tradeAmt : "";
        return str;
    }

    /**
     * 获取组合头寸信息
     *
     * @param dto
     * @author jiangpeng
     * @date 2022/5/18 19:10
     */
    public void selectCash(CfetsTrdFirstBidFirmInfoDto dto) {
        for (CfetsTrdFirstBidFirmProductDto productDto : dto.getProductDtos()) {
            BaseMap cashMap = positionInfoService.queryTrdAvailableCash(
                    Arrays.asList(productDto.getPortfolioId()),
                    DateUtil.format(dto.getTradeDate()),
                    null,
                    null,
                    null,
                    Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()),
                    Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()),
                    dto.getFinprodId()
            );
            productDto.setCashT(cashMap.getBigDecimal("cashT"));
            productDto.setCashT1(cashMap.getBigDecimal("cashT1"));
            productDto.setRealCash(cashMap.getBigDecimal("realCash"));
            productDto.setSecDesirableCash(cashMap.getBigDecimal("secDesirableCash"));
        }
    }
}

