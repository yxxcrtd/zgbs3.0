package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.dto.CfetsBondPortfolioCashRequest;
import com.joyintech.tams.business.common.dto.CfetsBondPositionRequest;
import com.joyintech.tams.business.common.dto.CfetsSearchInfoRequest;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.BigDecimalUtil;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.position.service.PositionInfoService;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondMapper;
import com.joyintech.tams.cfets.trd.bond.dto.*;
import com.joyintech.tams.cfets.trd.bond.service.IbuQuoteBondCalService;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dao.CfetsTrdBondFirstSub001Mapper;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dao.CfetsTrdBondFirstSub002Mapper;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.*;
import com.joyintech.tams.cfets.trd.repo.dto.CfetsRepoDealRegisterQuoteDto;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 一级申购 新建 SERVICE
 *
 * @author jp
 * @date 2022/04/18
 */
@Service
public class CfetsTrdBondFirstSub002Service extends BaseService<CfetsTrdBondFirstSubInfoDto> implements BaseProcessService<CfetsTrdBondFirstSubInfoDto> {

    private static final Logger logger = LoggerFactory.getLogger(CfetsTrdBondFirstSub002Service.class);

    /**
     * 平台日期组件接口，提供平台日期的获取和更新
     */
    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private IbuQuoteBondCalService ibuQuoteBondCalService;

    @Autowired
    private CfetsTrdBondFirstSub001Service cfetsTrdBondFirstSub001Service;
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

    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Autowired
    private CfetsTrdBondFirstSub001Mapper cfetsTrdBondFirstSub001Mapper;

    @Autowired
    private CfetsTrdBondFirstSub002Mapper cfetsTrdBondFirstSub002Mapper;

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
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    @Autowired
    private CfetsIbuQuoteBondMapper cfetsIbuQuoteBondMapper;

    @Autowired
    private PositionInfoService positionInfoService;

    /**
     * 认购量最小变动单位
     */
    private static final BigDecimal subMinChangeUnit = new BigDecimal(1000);

    /**
     * 应缴款金额最小变动单位
     */
    private static final BigDecimal payMinChangeUnit = new BigDecimal("0.01");

    /**
     * 结算服务费最小变动单位
     */
    private static final BigDecimal feeMinChangeUnit = new BigDecimal("0.01");

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 申购点击check
     *
     * @param request 入参,指令信息
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/4/18 15:16
     */
    public AjaxResult dealexecutioncheck(CfetsTrdBondFirstSubInitRequest request) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        Date systemDate = iPlatformDate.getSysDate();
        logger.debug("自动分配,入参:" + JsonUtils.object2Json(request));
        if (StringUtil.isNotEmpty(request) && StringUtil.isNotEmpty(request.getEntrustDtos())) {
            String finprodId = null;
            String secManageAcctId = null;
            for (CfetsTrdBondFirstSubEntrustDto entrustDto : request.getEntrustDtos()) {
                ValidMessage error = new ValidMessage();
                // 资产代码+证券账户必须一致提示信息
                String msg = "";
                // 1.指令状态为已接收且该指令不存在指令流程中数据，不满足弹出提示
                if (!StringUtil.equals(entrustDto.getEntrustStatus(), DictCodeList.委托状态_已接收.getValue())) {
                    errorList.add(new ValidMessage("只可对已接收的指令执行申购操作!"));
                }
                WflEtrEntrustKey wflEtrEntrust = wflEtrEntrustMapper.selectByEntrustId(entrustDto.getEntrustId());
                if (StringUtil.isNotEmpty(wflEtrEntrust)) {
                    errorList.add(new ValidMessage("指令存在指令流程中数据不可执行申购操作!"));
                }
                // 2.平台日必须等于发行日，否则弹出提示“平台日为发行日才可进行申购”
                if (DateUtil.compareTo(systemDate, entrustDto.getIssueDate()) != 0) {
                    errorList.add(new ValidMessage("平台日为发行日才可进行申购!"));
                }
                // 3.选择多条指令时，资产代码+证券账户必须一致，如不满足则提示“所选指令不满足汇总条件：【债券标的OR证券账户】不同”，多个条件不满足则同时显示多个条件。
                if (StringUtil.isEmpty(finprodId)) {
                    finprodId = entrustDto.getFinprodId();
                }
                if (!StringUtil.equals(finprodId, entrustDto.getFinprodId())) {
                    msg = "债券标的";
                }
                if (StringUtil.isEmpty(secManageAcctId)) {
                    secManageAcctId = entrustDto.getSecManageAcctId();
                }
                if (!StringUtil.equals(secManageAcctId, entrustDto.getSecManageAcctId())) {
                    if (StringUtil.isNotEmpty(msg)) {
                        msg += "OR";
                    }
                    msg += "证券账户";
                }
                if (StringUtil.isNotEmpty(msg)) {
                    msg = "【" + msg + "】不同";
                    errorList.add(new ValidMessage(msg));
                    break;
                }
            }
        }
        if (StringUtil.isNotEmpty(errorList)) {
            ajaxResult.setErrorList(errorList);
        }
        return ajaxResult;
    }

    /**
     * 初始化 申购页面
     *
     * @param request 指令信息
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto
     * @author jiangpeng
     * @date 2022/4/19 19:32
     */
    public CfetsTrdBondFirstSubInfoDto queryEntrustInfoById(CfetsTrdBondFirstSubInitRequest request) {
        logger.debug("初始化申购页面,入参:" + JsonUtils.object2Json(request));
        CfetsTrdBondFirstSubInfoDto cfetsTrdBondFirstSubInfoDto = new CfetsTrdBondFirstSubInfoDto();
        Map<String, Object> param = new HashMap<>();
        List<String> entrustIdList = new ArrayList<>();
        //1.查询申购产品列表(获取资产信息,及剩余指令额度)
        request.getEntrustDtos().forEach(item -> {
            entrustIdList.add(item.getEntrustId());
        });
        param.put("entrustIdList", entrustIdList);
        this.setParam(param);
        List<CfetsTrdBondFirstSubInfoDto> listTmp = cfetsTrdBondFirstSub002Mapper.selectSubProducts(param);
        cfetsTrdBondFirstSubInfoDto = listTmp.get(0);
        // 数据为空或查询结果大于1 返回空值
        if (CollectionUtils.isEmpty(listTmp)
                || CollectionUtils.isEmpty(cfetsTrdBondFirstSubInfoDto.getProductDtos())
                || (CollectionUtils.isNotEmpty(listTmp) && listTmp.size() > 1)) {
            logger.debug("初始化申购页面,查询结果数:" + listTmp.size());
            return null;
        }
        //2.汇总认购量 由各组合指令自动带入，默认为各组合指令的剩余指令额度，可修改，修改后不得高于各组合指令的剩余指令额度
        cfetsTrdBondFirstSubInfoDto.setSubscriptionAmount(BigDecimal.ZERO);
        // 本方校验错误类型 0 符合要求 1 存在不同机构 2 存在机构为空
        int errorType = 0;
        String partyId = null;
        String isSupportCstpFlagBondfirst = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BONDFIRST");
        for (CfetsTrdBondFirstSubProductDto product : cfetsTrdBondFirstSubInfoDto.getProductDtos()) {
            cfetsTrdBondFirstSubInfoDto.setSubscriptionAmount(ArithmeticUtil.doAdd(cfetsTrdBondFirstSubInfoDto.getSubscriptionAmount(), product.getSubscriptionAmount()));
            if (StringUtil.isEmpty(product.getPartyId())) {
                errorType = 2;
            }
            if (StringUtil.isNotEmpty(partyId) && !StringUtil.equals(partyId, product.getPartyId()) && errorType != 2) {
                errorType = 1;
            }
            partyId = product.getPartyId();
        }
        if (errorType == 0) {
            cfetsTrdBondFirstSubInfoDto.setOuPartyId(partyId);
        }
        // 3.计算 【应缴款金额】=发行价格*认购量*10000/100 【结算服务费】，根据银行间债券交易费用规则计算
        this.calAmtAndFee(cfetsTrdBondFirstSubInfoDto);
        cfetsTrdBondFirstSubInfoDto.getProductDtos().sort(Comparator.comparing(CfetsTrdBondFirstSubProductDto::getAvaNum).reversed());
        logger.debug("初始化申购页面,结果:" + JsonUtils.object2Json(cfetsTrdBondFirstSubInfoDto));
        return cfetsTrdBondFirstSubInfoDto;
    }

    /**
     * 添加或删除申购产品列表数据
     *
     * @param request 页面元素
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto
     * @author jiangpeng
     * @date 2022/5/5 16:08
     */
    public CfetsTrdBondFirstSubInfoDto addOrDelList(CfetsTrdBondFirstSubInfoDto request) {
        logger.debug("添加或删除申购产品列表数据,入参:" + JsonUtils.object2Json(request));
        if (StringUtil.isNotEmpty(request.getEntrustDtos()) && request.getEntrustDtos().size() > 0) {
            CfetsTrdBondFirstSubInfoDto cfetsTrdBondFirstSubInfoDto = new CfetsTrdBondFirstSubInfoDto();
            Map<String, Object> param = new HashMap<>();
            List<String> entrustIdList = new ArrayList<>();
            //1.查询申购产品列表(获取资产信息,及剩余指令额度)
            request.getEntrustDtos().forEach(item -> {
                entrustIdList.add(item.getEntrustId());
            });
            param.put("entrustIdList", entrustIdList);
            this.setParam(param);
            List<CfetsTrdBondFirstSubInfoDto> listTmp = cfetsTrdBondFirstSub002Mapper.selectSubProducts(param);
            cfetsTrdBondFirstSubInfoDto = listTmp.get(0);
            // 数据为空或查询结果大于1 返回空值
            if (CollectionUtils.isEmpty(listTmp)
                    || CollectionUtils.isEmpty(cfetsTrdBondFirstSubInfoDto.getProductDtos())
                    || (CollectionUtils.isNotEmpty(listTmp) && listTmp.size() > 1)) {
                logger.debug("添加或删除申购产品列表数据,查询结果数:" + listTmp.size());
                return null;
            }
            request.getProductDtos().addAll(cfetsTrdBondFirstSubInfoDto.getProductDtos());
        }
        //2.汇总认购量 由各组合指令自动带入，默认为各组合指令的剩余指令额度，可修改，修改后不得高于各组合指令的剩余指令额度
        request.setSubscriptionAmount(BigDecimal.ZERO);
        // 本方校验错误类型 0 符合要求 1 存在不同机构 2 存在机构为空
        int errorType = 0;
        String partyId = null;
        String isSupportCstpFlagBondfirst = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BONDFIRST");
        for (CfetsTrdBondFirstSubProductDto product : request.getProductDtos()) {
            request.setSubscriptionAmount(ArithmeticUtil.doAdd(request.getSubscriptionAmount(), product.getSubscriptionAmount()));
            if (StringUtil.isEmpty(product.getPartyId())) {
                errorType = 2;
            }
            if (StringUtil.isNotEmpty(partyId) && !StringUtil.equals(partyId, product.getPartyId()) && errorType != 2) {
                errorType = 1;
            }
            partyId = product.getPartyId();
        }
        request.setOuPartyId(null);
        if (errorType == 0) {
            request.setOuPartyId(partyId);
        }
        // 3.计算 【应缴款金额】=发行价格*认购量*10000/100 【结算服务费】，根据银行间债券交易费用规则计算
        this.calAmtAndFee(request);
        // 4.自动分配
        request.setProductDtos(this.autoAllocation(request));
        // 5.清空指令列表
        request.setEntrustDtos(null);
        logger.debug("添加或删除申购产品列表数据,结果:" + JsonUtils.object2Json(request));
        return request;
    }

    /**
     * 申购交易查看
     *
     * @param request 交易编号
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto
     * @author jiangpeng
     * @date 2022/4/27 18:39
     */
    public CfetsTrdBondFirstSubInfoDto viewPage(CfetsTrdBondFirstSubInitRequest request) {
        logger.debug("初始化申购查看页面,入参:" + JsonUtils.object2Json(request));
        if (StringUtil.isEmpty(request.getBidId())) {
            logger.debug("初始化申购查看页面,交易编号为空");
            return null;
        }
        CfetsTrdBondFirstSubInfoDto cfetsTrdBondFirstSubInfoDto = new CfetsTrdBondFirstSubInfoDto();
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        List<CfetsTrdBondFirstSubInfoDto> listTmp = cfetsTrdBondFirstSub002Mapper.viewPage(pageParam);
        if (StringUtil.isNotEmpty(listTmp)) {
            cfetsTrdBondFirstSubInfoDto = listTmp.get(0);
        }
        logger.debug("初始化申购查看页面,结果:" + JsonUtils.object2Json(cfetsTrdBondFirstSubInfoDto));
        return cfetsTrdBondFirstSubInfoDto;
    }

    /**
     * 汇总认购量 废弃?
     *
     * @param cfetsTrdBondFirstSubInfoDto 申购信息
     * @author jiangpeng
     * @date 2022/4/24 11:26
     */
    /*private void sumSubscriptionAmount(CfetsTrdBondFirstSubInfoDto cfetsTrdBondFirstSubInfoDto) {
        cfetsTrdBondFirstSubInfoDto.setSubscriptionAmount(BigDecimal.ZERO);
        for (CfetsTrdBondFirstSubProductDto product : cfetsTrdBondFirstSubInfoDto.getProductDtos()) {
            // 认购量不得大于各组合指令的剩余指令额度
            // 考虑到可能有的指令不是初次申购或者不是初始化调用的该方法,此时认购量的值可能大于剩余指令额度,所以先做值比较和赋值,再汇总
            if (BigDecimalUtil.ACompareToB(product.getSubscriptionAmount(), product.getAvaNum(), 6) > 0) {
                product.setSubscriptionAmount(product.getAvaNum());
            }
            cfetsTrdBondFirstSubInfoDto.setSubscriptionAmount(ArithmeticUtil.doAdd(cfetsTrdBondFirstSubInfoDto.getSubscriptionAmount(), product.getSubscriptionAmount()));
        }
    }*/

    /**
     * 计算 应缴款金额 结算服务费
     *
     * @param dto 入参
     * @author jiangpeng
     * @date 2022/4/22 16:25
     */
    public CfetsTrdBondFirstSubInfoDto calAmtAndFee(CfetsTrdBondFirstSubInfoDto dto) {
        logger.debug("计算 应缴款金额 结算服务费,入参:" + JsonUtils.object2Json(dto));
        // 认购量为空不处理
        if (!StringUtil.isNotEmpty(dto.getSubscriptionAmount())) {
            return dto;
        }
        // 【应缴款金额】=发行价格*认购量*10000/100
        dto.setPayableAmount(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getSubscriptionAmount(), dto.getIssuePrice()), 100, 6));
        // 【结算服务费】，根据银行间债券交易费用规则计算
        CalRequest calRequest = new CalRequest();
        calRequest.setFpriceAmt(dto.getPayableAmount());
        calRequest.setSettleDate(dto.getTradeDate());
        calRequest.setPs(DictCodeList.交易方向_买入.getValue());
        calRequest.setSecManageAcctId(dto.getProductDtos().get(0).getSecManageAcctId());
        calRequest.setShareAmt(dto.getSubscriptionAmount());
        calRequest.setFinprodId(dto.getFinprodId());
        List<TrdTradeFeeDetail> trdTradeFeeDetails = ibuQuoteBondCalService.calFee(calRequest);
        if (CollectionUtils.isNotEmpty(trdTradeFeeDetails)) {
            trdTradeFeeDetails.forEach(item -> {
                if (StringUtil.equals(item.getFeeType(), BussinessDictCodeList.费用类型_结算手续费.getValue())) {
                    dto.setSettFeeAmt(item.getFeeAmt());
                }
            });
        }
        logger.debug("计算 应缴款金额 结算服务费,结果:" + JsonUtils.object2Json(dto));
        return dto;
    }

    /**
     * 自动分配
     *
     * @param autoRequest 画面元素
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubProductDto>
     * @author jiangpeng
     * @date 2022/4/21 13:52
     */
    public List<CfetsTrdBondFirstSubProductDto> autoAllocation(CfetsTrdBondFirstSubInfoDto autoRequest) {
        logger.debug("自动分配,入参:" + JsonUtils.object2Json(autoRequest));
        if (StringUtil.isEmpty(autoRequest.getDipType())) {
            logger.debug("自动分配,分配方式为空");
            return autoRequest.getProductDtos();
        }
        BigDecimal subscriptionAmount = autoRequest.getSubscriptionAmount();
        // 剩余指令额度不为0的数据
        List<CfetsTrdBondFirstSubProductDto> list = new ArrayList<>();
        // 剩余指令额度为0的数据
        List<CfetsTrdBondFirstSubProductDto> listAumIsZero = new ArrayList<>();
        //当存在剩余指令额度为0的数据时，认购量为空，且整条数据置灰不可修改并放置列表最底部，此种数据不参与如认购量分配、应缴款金额分配、结算服务费分配等此类计算
        for (CfetsTrdBondFirstSubProductDto dto : autoRequest.getProductDtos()) {
            if (BigDecimalUtil.isZero(dto.getAvaNum()) || dto.getAvaNum() == null) {
                listAumIsZero.add(dto);
            } else {
                list.add(dto);
            }
        }
        // 1.按比例分配
        //按比例分配
        if (StringUtil.equals(autoRequest.getDipType(), "143")) {
            list = this.propDistribution(subscriptionAmount, list);
        }
        // 2.平均分配
        // 平均分配
        if (StringUtil.equals(autoRequest.getDipType(), "139")) {
            list = this.aveDistribution(subscriptionAmount, list);
        }
        // 3.分配应缴款金额和结算服务费
        this.otherDistribution(autoRequest.getSubscriptionAmount(), autoRequest.getPayableAmount(), autoRequest.getSettFeeAmt(), list);
        list.addAll(listAumIsZero);
        logger.debug("自动分配,结果:" + JsonUtils.object2Json(list));
        return list;
    }

    /**
     * 自动分配-按比例分配
     *
     * @param subAmt      认购量
     * @param productDtos 申购产品列表
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubProductDto>
     * @author jiangpeng
     * @date 2022/4/21 13:52
     */
    private List<CfetsTrdBondFirstSubProductDto> propDistribution(BigDecimal subAmt, List<CfetsTrdBondFirstSubProductDto> productDtos) {
        // 自动计算的计算结果需要为最小变动单位1000的整数倍，且每条数据的认购量须小于等于剩余指令额度
        BigDecimal minChangeUnit = subMinChangeUnit;
        //指令剩余额度汇总
        BigDecimal avaNumTotal = productDtos.stream().map(CfetsTrdBondFirstSubProductDto::getAvaNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 剩余尾差
        BigDecimal tailDifference = subAmt;
        int index = 0;
        for (CfetsTrdBondFirstSubProductDto item : productDtos) {
            index++;
            // 占比值 根据每条数据的“剩余指令额度/指令剩余额度汇总*基本信息中认购量”计算
            BigDecimal proportion = ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getAvaNum(), subAmt), avaNumTotal, 4);
            proportion = ArithmeticUtil.multiply(ArithmeticUtil.divide(proportion, minChangeUnit, 0, RoundingMode.HALF_DOWN), minChangeUnit);
            // 记录剩余指令额度
            item.setAvaNumTemp(item.getAvaNum());
            // 最后一位时
            if (index == productDtos.size()) {
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 6) >= 0
                        && BigDecimalUtil.ACompareToB(proportion, tailDifference, 6) >= 0) {
                    // 剩余指令额度大于尾差且尾差小于占比值时 用倒减法求值
                    item.setSubscriptionAmount(tailDifference);
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), proportion, 6) >= 0
                        && BigDecimalUtil.ACompareToB(proportion, tailDifference, 6) < 0) {
                    // 剩余指令额度大于占比值且尾差大于占比值时
                    item.setSubscriptionAmount(proportion);
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), proportion, 6) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 6) < 0) {
                    // 剩余指令额度小于占比值且剩余指令额度小于尾差时
                    item.setSubscriptionAmount(item.getAvaNumTemp());
                }
            } else {// 每条数据的认购量须小于等于剩余指令额度
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), proportion, 6) >= 0) {
                    item.setSubscriptionAmount(proportion);
                } else {
                    item.setSubscriptionAmount(item.getAvaNumTemp());
                }
            }
            item.setAvaNumTemp(ArithmeticUtil.subtract(item.getAvaNumTemp(), item.getSubscriptionAmount()));
            tailDifference = ArithmeticUtil.subtract(tailDifference, item.getSubscriptionAmount());
        }
        // 处理尾差,最小变动单位序列分配算法
        this.handleTailDifferenceSubscriptionAmount(productDtos, tailDifference, minChangeUnit);
        return productDtos;
    }

    /**
     * 自动分配-平均分配
     *
     * @param subAmt      认购量
     * @param productDtos 申购产品列表
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubProductDto>
     * @author jiangpeng
     * @date 2022/4/21 13:52
     */
    private List<CfetsTrdBondFirstSubProductDto> aveDistribution(BigDecimal subAmt, List<CfetsTrdBondFirstSubProductDto> productDtos) {
        // 自动计算的计算结果需要为最小变动单位1000的整数倍，且每条数据的认购量须小于等于剩余指令额度
        BigDecimal minChangeUnit = subMinChangeUnit;
        // 平均值计算
        BigDecimal avg = ArithmeticUtil.divide(subAmt, productDtos.size(), 4);
        avg = ArithmeticUtil.multiply(ArithmeticUtil.divide(avg, minChangeUnit, 0, RoundingMode.HALF_DOWN), minChangeUnit);
        // 剩余尾差
        BigDecimal tailDifference = subAmt;
        int index = 0;
        for (CfetsTrdBondFirstSubProductDto item : productDtos) {
            index++;
            // 记录剩余指令额度
            item.setAvaNumTemp(item.getAvaNum());
            // 最后一位时
            if (index == productDtos.size()) {
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 6) >= 0
                        && BigDecimalUtil.ACompareToB(avg, tailDifference, 6) >= 0) {
                    // 剩余指令额度大于尾差且尾差小于平均值时 用倒减法求值
                    item.setSubscriptionAmount(tailDifference);
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), avg, 6) >= 0
                        && BigDecimalUtil.ACompareToB(avg, tailDifference, 6) < 0) {
                    // 剩余指令额度大于平均值且尾差大于平均值时
                    item.setSubscriptionAmount(avg);
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), avg, 6) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 6) < 0) {
                    // 剩余指令额度小于平均值且剩余指令额度小于尾差时
                    item.setSubscriptionAmount(item.getAvaNumTemp());
                }
            } else {// 每条数据的认购量须小于等于剩余指令额度
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), avg, 6) >= 0) {
                    item.setSubscriptionAmount(avg);
                } else {
                    item.setSubscriptionAmount(item.getAvaNumTemp());
                }
            }
            item.setAvaNumTemp(ArithmeticUtil.subtract(item.getAvaNumTemp(), item.getSubscriptionAmount()));
            tailDifference = ArithmeticUtil.subtract(tailDifference, item.getSubscriptionAmount());
        }
        // 处理尾插,最小变动单位序列分配算法
        this.handleTailDifferenceSubscriptionAmount(productDtos, tailDifference, minChangeUnit);

        return productDtos;
    }

    /**
     * 分配应缴款金额和结算服务费
     *
     * @param subAmt
     * @param payableAmount
     * @param settFeeAmt
     * @param productDtos
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubProductDto>
     * @author jiangpeng
     * @date 2022/4/24 14:06
     */
    private List<CfetsTrdBondFirstSubProductDto> otherDistribution(BigDecimal subAmt, BigDecimal payableAmount, BigDecimal settFeeAmt,
                                                                   List<CfetsTrdBondFirstSubProductDto> productDtos) {
        if (subAmt == null) {
            subAmt = BigDecimal.ZERO;
        }
        if (payableAmount == null) {
            payableAmount = BigDecimal.ZERO;
        }
        if (settFeeAmt == null) {
            settFeeAmt = BigDecimal.ZERO;
        }
        // 结算服务费平均值计算
        BigDecimal avg = ArithmeticUtil.divide(settFeeAmt, new BigDecimal(productDtos.size()), 2);
        // 应缴款金额剩余尾差
        BigDecimal payTailDifference = payableAmount;
        // 费用剩余尾差
        BigDecimal feeTailDifference = settFeeAmt;
        int index = 0;
        for (CfetsTrdBondFirstSubProductDto item : productDtos) {
            index++;
            //申购产品列表中认购量/基本信息中总的认购量*基本信息中应缴款金额
            BigDecimal payableAmountDetail = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(item.getSubscriptionAmount(), payableAmount), subAmt, 2, RoundingMode.DOWN);
            BigDecimal feeAmountDetail = avg;
            // 最后一笔且尾差小于计算出的分配值时,用倒减值
            if (index == productDtos.size()) {
                if (BigDecimalUtil.ACompareToB(payableAmountDetail, payTailDifference, 3) >= 0) {
                    payableAmountDetail = payTailDifference;
                }
                if (BigDecimalUtil.ACompareToB(avg, feeTailDifference, 3) >= 0) {
                    feeAmountDetail = feeTailDifference;
                }
            }
            if (item.getSubscriptionAmount() == null || BigDecimalUtil.isZero(item.getSubscriptionAmount())) {
                // 认购量为0时,不分配应缴款金额和结算服务费
                payableAmountDetail = BigDecimal.ZERO;
                feeAmountDetail = BigDecimal.ZERO;
            }
            // 应缴款金额
            // 根据申购产品列表中 认购量/基本信息中总的认购量*基本信息中应缴款金额，计算认购量占比时，根据最小变动单位位数，舍位计算；
            // 尾差适用最小变动单位序列分配算法继续分配；
            item.setPayableAmount(payableAmountDetail);
            // 结算服务费
            // 根据申购产品列表中数据条数平均分配结算服务费，尾差适用最小变动单位序列分配算法继续分配
            item.setSettFeeAmt(feeAmountDetail);
            payTailDifference = ArithmeticUtil.subtract(payTailDifference, item.getPayableAmount());
            feeTailDifference = ArithmeticUtil.subtract(feeTailDifference, item.getSettFeeAmt());
        }
        this.handleTailDifferenceOhter(productDtos, payTailDifference, feeTailDifference);
        return productDtos;
    }

    /**
     * 其他金额分配
     *
     * @param autoRequest 页面元素
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubProductDto>
     * @author jiangpeng
     * @date 2022/4/24 16:34
     */
    public List<CfetsTrdBondFirstSubProductDto> otherAllocation(CfetsTrdBondFirstSubInfoDto autoRequest) {
        logger.debug("其他金额分配,入参:" + JsonUtils.object2Json(autoRequest));
        BigDecimal subscriptionAmount = autoRequest.getSubscriptionAmount();
        // 剩余指令额度不为0的数据
        List<CfetsTrdBondFirstSubProductDto> list = new ArrayList<>();
        // 剩余指令额度为0的数据
        List<CfetsTrdBondFirstSubProductDto> listAumIsZero = new ArrayList<>();
        //当存在剩余指令额度为0的数据时，认购量为空，且整条数据置灰不可修改并放置列表最底部，此种数据不参与如认购量分配、应缴款金额分配、结算服务费分配等此类计算
        for (CfetsTrdBondFirstSubProductDto dto : autoRequest.getProductDtos()) {
            if (BigDecimalUtil.isZero(dto.getAvaNum()) || dto.getAvaNum() == null) {
                listAumIsZero.add(dto);
            } else {
                list.add(dto);
            }
        }
        // 降序
        list.sort(Comparator.comparing(CfetsTrdBondFirstSubProductDto::getAvaNum).reversed());
        // 分配应缴款金额和结算服务费
        this.otherDistribution(autoRequest.getSubscriptionAmount(), autoRequest.getPayableAmount(), autoRequest.getSettFeeAmt(), list);
        list.addAll(listAumIsZero);
        logger.debug("其他金额分配,结果:" + JsonUtils.object2Json(list));
        return list;
    }

    /**
     * 重新计算 应缴款金额 结算服务费 并 重新分配产品列表中 认购量 应缴款金额 结算服务费
     *
     * @param autoRequest 页面元素
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto
     * @author jiangpeng
     * @date 2022/4/24 17:16
     */
    public CfetsTrdBondFirstSubInfoDto calData(CfetsTrdBondFirstSubInfoDto autoRequest) {
        // 重新计算 应缴款金额 结算服务费
        CfetsTrdBondFirstSubInfoDto result = this.calAmtAndFee(autoRequest);
        // 重新分配 认购量 应缴款金额 结算服务费
        result.setProductDtos(this.autoAllocation(autoRequest));
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
     * @param ps 交易方向
     * @return com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter
     * @author jiangpeng
     * @date 2022/4/21 14:29
     */
    public String getBidId(BussinessDictCodeList ps) {
        if (!StringUtil.isNotEmpty(ps)) {
            ps = BussinessDictCodeList.共同业务类型_一级投标确认_申购_登记;
        }
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(ps);
            return baseGetBusinessId.buildBussinessCode(codeParameter);
        } else {
            return IDGenerator.generate();
        }
    }

    /**
     * 获取发行人名称
     *
     * @param finprodId
     * @return java.lang.String
     * @author jiangpeng
     * @date 2022/5/18 19:33
     */
    public String getIssuer(String finprodId) {
        String Issuer = cfetsTrdBondFirstSub002Mapper.getIssuer(finprodId);
        return Issuer;
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
    public AjaxResult check(CfetsTrdBondFirstSubInfoDto dto) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        //必填项校验
        errorList.addAll(this.checkNotEmpty(dto));
        //金额校验
        errorList.addAll(this.checkCalAmt(dto));
        if (StringUtil.isNotEmpty(errorList)) {
            ajaxResult.setErrorList(errorList);
        }
//        ajaxResult.resetStatuCode();
        return ajaxResult;
    }

    /**
     * 处理发起流程逻辑
     *
     * @param dto           页面元素
     * @param operationType 操作类型
     * @author jiangpeng
     * @date 2022/4/24 11:42
     */
    @Override
    public void doInsert(CfetsTrdBondFirstSubInfoDto entity, OperationType type) {
        if ("save".equals(entity.getOperateType())) {
            return;
        }
        if (!StringUtil.isNotEmpty(entity.getTradeDate())) {
            entity.setTradeDate(DateUtil.parse(iPlatformDate.getSysDateStr()));
        }
        logger.debug("处理发起流程逻辑,入参:" + JsonUtils.object2Json(entity));
        if (!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
            this.insertWflData(entity);
            // 发送头寸
            this.sendPositionMethod(entity, true, null);
            // 老头寸处理
            this.dealOldPosition(entity, false);
        }
        // AOP中处理操作履历
    }

    /**
     * 退回或保存后提交
     *
     * @param entity
     * @author jiangpeng
     * @date 2022/4/28 19:54
     */
    @Override
    public void doUpdate(CfetsTrdBondFirstSubInfoDto entity) {
        Map<String, Object> map = new HashMap<>();
        if (!StringUtil.isNotEmpty(entity.getTradeDate())) {
            entity.setTradeDate(DateUtil.parse(iPlatformDate.getSysDateStr()));
        }
        logger.debug("退回或保存后提交,入参:" + JsonUtils.object2Json(entity));
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            // 先删除流程中数据
            this.deleteWflData(entity.getBidId());
            //登录业务流程
            this.insertWflData(entity);
            String confirmOk = RequestUtil.getParam("confirmOk");
            ProcessMark newEntity;
            //处理指令执行进度
            this.insertEtrFinishStatus(entity, OperationType.NEW);
            // 发头寸
            try {
                if (StringUtil.isNotEmpty(entity.getWorkflowId())) {
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
            this.dealOldPosition(entity, false);
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(CfetsTrdBondFirstSubInfoDto.class, true);
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
     * @param dto           页面元素
     * @param operationType 操作类型
     * @author jiangpeng
     * @date 2022/4/24 11:42
     */
    @Override
    public void doComplete(CfetsTrdBondFirstSubInfoDto entity, OperationType type) {
        if (!StringUtil.isNotEmpty(entity.getTradeDate())) {
            entity.setTradeDate(DateUtil.parse(iPlatformDate.getSysDateStr()));
        }
        logger.debug("处理生效逻辑,入参:" + JsonUtils.object2Json(entity));
        this.deleteWflData(entity.getBidId());
        this.insertData(entity, type);
        //头寸
        if (type.equals(OperationType.REVOKE) || type.equals(OperationType.DELETE)) {
            // 匹配取消
            cfetsTrdBondFirstSub002Mapper.deleteByPreTradeId(entity.getBidId());
            PositionHttpRequest.dorevoke(entity.getBidId(), DictCodeListExt1.子系统代码_交易.getValue(),
                    DictCodeListExt3.头寸业务类型_债券一级申购.getValue(), DictCodeList.交易方向_买入.getValue());
            // 老头寸处理
            this.dealOldPosition(entity, true);
        }
        //处理指令执行进度
        this.insertEtrFinishStatus(entity, type);
    }

    /**
     * 根据交易表数据组装dto
     *
     * @param bidId
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto
     * @author jiangpeng
     * @date 2022/5/12 15:08
     */
    public CfetsTrdBondFirstSubInfoDto prepareDto(String bidId) {
        CfetsTrdBondFirstSubInfoDto entity = new CfetsTrdBondFirstSubInfoDto();
        BidOrder bidOrder = bidOrderMapper.selectByPrimaryKey(bidId);
        BeanUtil.copyPropertiesIgnoreNull(entity, bidOrder);
        entity.setTradeDate(bidOrder.getBidDate());
        List<BidOrderBidPrice> list = bidOrderBidPriceMapper.selectListByPrimaryKey(bidId);
        List<CfetsTrdBondFirstSubProductDto> productDtos = new ArrayList<>();
        for (BidOrderBidPrice item : list) {
            CfetsTrdBondFirstSubProductDto productDto = new CfetsTrdBondFirstSubProductDto();
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
     * @date 2022/5/12 14:53
     */
    public void dorevoke(String totleTradeId, String remark, Boolean isNeedDealPosition, String tgBusiType) {
        CfetsTrdBondFirstSubInfoDto entity = this.prepareDto(totleTradeId);
        entity.setBidId(totleTradeId);
        this.insertData(entity, OperationType.REVOKE);
        try {
            if (isNeedDealPosition) {
                // 匹配取消
                cfetsTrdBondFirstSub002Mapper.deleteByPreTradeId(entity.getBidId());
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
    public void doBack(CfetsTrdBondFirstSubInfoDto entity) {
        // 删除流程中表数据
        this.deleteWflDataByWorkflowId(entity.getWorkflowId());
        // 老头寸处理
        this.dealOldPosition(entity, true);
    }

    /**
     * @param requst
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/4/27 10:30
     */
    public AjaxResult checkDayRegRevokeCheck(CfetsBondFirstSubCheckRequst requst) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        String msg = "";
        for (CfetsBondFirstSubCheckDetaileRequst item : requst.getCfetsBondFirstSubCheckDetaileRequsts()) {
            // 存在已确认的数据
            if (joyinbusinessCommonService.countMatchInfo(item.getBidId(), DictCodeList.确认状态_已确认.getValue()) > 0) {
                errorList.add(
                        new ValidMessage("所选对象存在已确认的数据!"));
            }
            //如已有撤销数据在流程中，不允许进行再次进行撤销
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setPageId("TenderBondSub002");
            wflValidInputParameter.setBusinessDataPk1(item.getBidId());
            if (null != mstWorkflowCommonService &&
                    mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
                errorList.add(
                        new ValidMessage("所选对象存在流程中的数据!"));
            }
            if (joyinbusinessCommonService.countMatchInfo(item.getBidId(), DictCodeList.确认状态_未确认.getValue()) > 0
                    && (StringUtil.equals(DictCodeList.是否_否.getValue(), requst.getIsSure()) || StringUtil.isEmpty(requst.getIsSure()))) {
                //如勾选数据已生成匹配待确认的记录
                msg += item.getBidId() + "存在已匹配待确认记录，是否确认撤销?/n";
            }
            //如勾选数据发行状态不为空不可撤销
            if (StringUtil.isNotEmpty(item.getIssueStatus())) {
                errorList.add(new ValidMessage(item.getBidId() + "发行状态不为空不可撤销"));
            }
        }
        if (StringUtil.isNotEmpty(errorList)) {
            ajaxResult.setErrorList(errorList);
            ajaxResult.setMessageType(MessageType.错误消息);
        } else {
            ajaxResult.setMessage(msg, MessageType.确认消息);
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

    /**
     * 根据流程编号删除流程中数据
     *
     * @param bidId 投标编号
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
     * @param entity 页面元素
     * @author jiangpeng
     * @date 2022/4/26 9:57
     */
    private void insertWflData(CfetsTrdBondFirstSubInfoDto entity) {
        WflBidOrder wflBidOrder = new WflBidOrder();
        BeanUtil.copyPropertiesIgnoreNull(wflBidOrder, entity);
        // 投标日期=交易日
        wflBidOrder.setBidDate(entity.getTradeDate());
        wflBidOrder.setIsCancel(DictCodeList.是否_否.getValue());
        wflBidOrder.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflBidOrder.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        wflBidOrderMapper.insertSelective(wflBidOrder);
        for (CfetsTrdBondFirstSubProductDto item : entity.getProductDtos()) {
            WflBidOrderBidPrice wflBidOrderBidPrice = new WflBidOrderBidPrice();
            BeanUtil.copyPropertiesIgnoreNull(wflBidOrderBidPrice, item);
            wflBidOrderBidPrice.setBidPrice(new BigDecimal(99999999));//主键必传,用99999999做区分
            wflBidOrderBidPrice.setBidId(entity.getBidId());
            wflBidOrderBidPrice.setIsCancel(DictCodeList.是否_否.getValue());
            wflBidOrderBidPrice.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            wflBidOrderBidPrice.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflBidOrderBidPriceMapper.insertSelective(wflBidOrderBidPrice);
        }
    }

    /**
     * 流程生效时数据入库
     *
     * @param entity 页面元素
     * @author jiangpeng
     * @date 2022/4/26 9:56
     */
    private void insertData(CfetsTrdBondFirstSubInfoDto entity, OperationType type) {
        BidOrder bidOrder = new BidOrder();
        BeanUtil.copyPropertiesIgnoreNull(bidOrder, entity);
        if (type.equals(OperationType.REVOKE) || type.equals(OperationType.DELETE)) {
            // 撤销数据处理
            bidOrder.setBidId(StringUtil.join(entity.getBidId(), "R"));
            bidOrder.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            bidOrder.setIsCancel(DictCodeList.是否_否.getValue());
            bidOrder.setpBidId(entity.getBidId());
            if (StringUtil.isNotEmpty(entity.getRemark())) {
                bidOrder.setRemark(entity.getRemark());
            }
        } else {
            // 投标日期=交易日
            bidOrder.setBidDate(entity.getTradeDate());
            bidOrder.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            bidOrder.setIsCancel(DictCodeList.是否_否.getValue());
        }
        bidOrderMapper.insertSelective(bidOrder);
        // 明细表数据入库
        for (CfetsTrdBondFirstSubProductDto item : entity.getProductDtos()) {
            BidOrderBidPrice bidOrderBidPrice = new BidOrderBidPrice();
            BeanUtil.copyPropertiesIgnoreNull(bidOrderBidPrice, item);
            if (type.equals(OperationType.REVOKE) || type.equals(OperationType.DELETE)) {
                bidOrderBidPrice.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                bidOrderBidPrice.setIsCancel(DictCodeList.是否_否.getValue());
                bidOrderBidPrice.setBidId(bidOrder.getBidId());
                bidOrderBidPrice.setpBidId(entity.getBidId());
                bidOrderBidPrice.setBidPrice(new BigDecimal(99999999));//主键必传,用99999999做区分
            } else {
                bidOrderBidPrice.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                bidOrderBidPrice.setIsCancel(DictCodeList.是否_否.getValue());
                bidOrderBidPrice.setBidId(bidOrder.getBidId());
                bidOrderBidPrice.setBidPrice(new BigDecimal(99999999));//主键必传,用99999999做区分
            }
            bidOrderBidPriceMapper.insertSelective(bidOrderBidPrice);
        }
        // 更新撤销的原交易数据
        if (type.equals(OperationType.REVOKE) || type.equals(OperationType.DELETE)) {
            cfetsTrdBondFirstSub002Mapper.updateBidOrder(entity.getBidId(), entity.getRemark());
            cfetsTrdBondFirstSub002Mapper.updateBidOrderBidPrice(entity.getBidId());
        }
    }

    /**
     * 处理指令完成度
     *
     * @param dto           页面元素
     * @param operationType 操作类型
     */
    public void insertEtrFinishStatus(CfetsTrdBondFirstSubInfoDto dto, OperationType operationType) {
        List<CfetsTrdBondFirstSubProductDto> productDtos = dto.getProductDtos();
        if (CollectionUtils.isNotEmpty(productDtos)) {
            List<EtrFinishStatusDto> etrFinishStatusDtos = new ArrayList<>();
            Map<String, EtrFinishStatusDto> etrFinishStatusDtoMap = new HashMap<>();
            for (CfetsTrdBondFirstSubProductDto productDto : productDtos) {
                EtrFinishStatusDto status = new EtrFinishStatusDto();
                // 考虑到存在多组合指令的情况
                if (etrFinishStatusDtoMap.get(productDto.getEntrustId()) != null) {
                    status = etrFinishStatusDtoMap.get(productDto.getEntrustId());
                    status.setEntFinishAmt(ArithmeticUtil.doAdd(status.getEntFinishAmt(), productDto.getSubscriptionAmount()));
                } else {
                    status.setEntrustId(productDto.getEntrustId());
                    status.setTradeId(dto.getBidId());
                    status.setFinishDate(dto.getTradeDate());
                    status.setEntFinishAmt(productDto.getSubscriptionAmount());
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
        param.put("ISSUE_CLASS_01", DictCodeListExt4.新债_发行方式_报价发行.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("YN_N", DictCodeListExt1.是否_否.getValue());
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
     * 认购量尾差处理,最小变动单位序列分配算法
     * 求取平均数时是取余,平均分配的尾差必定不小于0
     *
     * @param productDtos    产品列表
     * @param tailDifference 尾差
     * @param minChangeUnit  最小变动单位
     * @author jiangpeng
     * @date 2022/4/24 9:49
     */
    private void handleTailDifferenceSubscriptionAmount(List<CfetsTrdBondFirstSubProductDto> productDtos, BigDecimal tailDifference, BigDecimal minChangeUnit) {
        // 最大循环次数
        // 降序
        productDtos.sort(Comparator.comparing(CfetsTrdBondFirstSubProductDto::getAvaNum).reversed());
        int maxCycle = productDtos.size() * 30;
        while (tailDifference.compareTo(BigDecimal.ZERO) > 0 && maxCycle != 0) {
            for (CfetsTrdBondFirstSubProductDto item : productDtos) {
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
                // 认购量加上分配值
                item.setSubscriptionAmount(ArithmeticUtil.doAdd(item.getSubscriptionAmount(), assign));
                // 尾差减去分配值
                tailDifference = ArithmeticUtil.subtract(tailDifference, assign);
                // 剩余指令额度减去分配值
                item.setAvaNumTemp(ArithmeticUtil.subtract(item.getAvaNumTemp(), assign));
            }
        }
    }

    /**
     * 应缴款金额和结算服务费尾差处理,最小变动单位序列分配算法
     *
     * @param productDtos       产品列表
     * @param payTailDifference 应缴款金额尾差
     * @param feeTailDifference 结算服务费尾差
     * @author jiangpeng
     * @date 2022/4/24 9:49
     */
    private void handleTailDifferenceOhter(List<CfetsTrdBondFirstSubProductDto> productDtos, BigDecimal payTailDifference, BigDecimal feeTailDifference) {
        // 最大循环次数
        int maxCycle = productDtos.size() * 30;
        while ((payTailDifference.compareTo(BigDecimal.ZERO) > 0 || feeTailDifference.compareTo(BigDecimal.ZERO) > 0) && maxCycle != 0) {
            for (CfetsTrdBondFirstSubProductDto item : productDtos) {
                maxCycle--;
                // 尾差等于0,循环结束
                if (BigDecimalUtil.isZero(payTailDifference) && BigDecimalUtil.isZero(feeTailDifference)) {
                    break;
                }
                // 认购量为0时,不分配应缴款金额和结算服务费
                if (item.getSubscriptionAmount() == null || BigDecimalUtil.isZero(item.getSubscriptionAmount())) {
                    continue;
                }
                // 应缴款金额分配值
                BigDecimal payAssign = BigDecimal.ZERO;
                // 费用分配值
                BigDecimal feeAssign = BigDecimal.ZERO;
                if (BigDecimalUtil.ACompareToB(payTailDifference, payMinChangeUnit, 4) >= 0) {
                    // 尾差值大于等于最小分配单位
                    payAssign = payMinChangeUnit;
                } else {
                    payAssign = payTailDifference;
                }
                if (BigDecimalUtil.ACompareToB(feeTailDifference, feeMinChangeUnit, 4) >= 0) {
                    // 尾差值大于等于最小分配单位
                    feeAssign = payMinChangeUnit;
                } else {
                    feeAssign = feeTailDifference;
                }
                // 认购量加上分配值
                item.setPayableAmount(ArithmeticUtil.doAdd(item.getPayableAmount(), payAssign));
                // 认购量加上分配值
                item.setSettFeeAmt(ArithmeticUtil.doAdd(item.getSettFeeAmt(), feeAssign));
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
     * @param dto 页面元素
     * @return java.util.List<com.joyintech.framework.validation.ValidMessage>
     * @author jiangpeng
     * @date 2022/4/25 10:40
     */
    private List<ValidMessage> checkNotEmpty(CfetsTrdBondFirstSubInfoDto dto) {
        List<ValidMessage> errorList = new ArrayList<>();
        // 资产代码
        if (StringUtil.isEmpty(dto.getFinprodId())) {
            errorList.add(new ValidMessage("资产代码不可为空!"));
        }
        // 系统参数 【是否支持CSTP下行接口_一级申购】
        String isSupportCstpFlagBondfirst = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BONDFIRST");
        // 本方 - 条件必填
        if (StringUtil.isEmpty(dto.getOuPartyId()) && StringUtil.equals(isSupportCstpFlagBondfirst, DictCodeList.是否_是.getValue())) {
            errorList.add(new ValidMessage("所有组合关联的银行间机构数据必须一致!"));
        }
        // 本方交易员
        if (StringUtil.isEmpty(dto.getOurTrader())) {
            errorList.add(new ValidMessage("本方交易员不可为空!"));
        }
        // 发行价格
        if (!StringUtil.isNotEmpty(dto.getIssuePrice())) {
            errorList.add(new ValidMessage("发行价格不可为空!"));
        }
        // 参考收益率
        if (!StringUtil.isNotEmpty(dto.getReferenceRateStr())
                && !StringUtil.isNotEmpty(dto.getReferenceRate())
                && !StringUtil.isNotEmpty(dto.getRate())) {
            errorList.add(new ValidMessage("参考收益率/票面利率不可为空!"));
        }
        // 认购量
        if (!StringUtil.isNotEmpty(dto.getSubscriptionAmount())) {
            errorList.add(new ValidMessage("认购量不可为空!"));
        } else if (BigDecimalUtil.ACompareToB(dto.getSubscriptionAmount(), new BigDecimal(1000), 4) < 0) {//认购量需要大于1000
            errorList.add(new ValidMessage("认购量需要大于0.1万元!"));
        }
        // 结算手续费 需要大于零
        if (BigDecimalUtil.ACompareToB(dto.getSettFeeAmt(), BigDecimal.ZERO, 4) < 0) {
            errorList.add(new ValidMessage("结算手续费不允许为负数!"));
        }
        // 应缴款金额
        if (!StringUtil.isNotEmpty(dto.getPayableAmount())) {
            errorList.add(new ValidMessage("应缴款金额不可为空!"));
        }
        // 分配方式
        if (StringUtil.isEmpty(dto.getDipType())) {
            errorList.add(new ValidMessage("分配方式不可为空!"));
        }
        return errorList;
    }

    /**
     * 数值校验
     *
     * @param dto 页面元素
     * @return java.util.List<com.joyintech.framework.validation.ValidMessage>
     * @author jiangpeng
     * @date 2022/4/25 10:40
     */
    private List<ValidMessage> checkCalAmt(CfetsTrdBondFirstSubInfoDto dto) {
        logger.debug("数值校验,入参:" + JsonUtils.object2Json(dto));
        List<ValidMessage> errorList = new ArrayList<>();
        BigDecimal avaNum = BigDecimal.ZERO;
        BigDecimal payableAmount = BigDecimal.ZERO;
        BigDecimal subscriptionAmount = BigDecimal.ZERO;
        BigDecimal settFeeAmt = BigDecimal.ZERO;
        String partyId = null;
        String secManageAcctId = null;
        // 系统参数 【是否支持CSTP下行接口_一级申购】
        String isSupportCstpFlagBondfirst = CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BONDFIRST");
        // 本方校验错误类型 0 符合要求 1 存在不同机构 2 存在机构为空
        int errorType = 0;
        // 产品申购列表
        for (CfetsTrdBondFirstSubProductDto item : dto.getProductDtos()) {
            // 剩余指令额度
            // 需求:当存在剩余指令额度为0的数据时，认购量为空，且整条数据置灰不可修改并放置列表最底部
            if (BigDecimalUtil.isZero(item.getAvaNum())) {
                continue;
            } else {
                avaNum = ArithmeticUtil.doAdd(avaNum, item.getAvaNum());
            }
            // 指令代码
            if (StringUtil.isEmpty(item.getEntrustId())) {
                errorList.add(new ValidMessage("申购产品列表中指令代码不可为空!"));
            }
            // 组合名称
            if (StringUtil.isEmpty(item.getPortfolioId())) {
                errorList.add(new ValidMessage("申购产品列表中组合名称不可为空!"));
            }
            // 认购量
            if (!StringUtil.isNotEmpty(item.getSubscriptionAmount())) {
                errorList.add(new ValidMessage("申购产品列表中结算服务费不可为空!"));
            } else {
                subscriptionAmount = ArithmeticUtil.doAdd(subscriptionAmount, item.getSubscriptionAmount());
            }
            // 结算服务费
            if (!StringUtil.isNotEmpty(item.getSettFeeAmt())) {
                errorList.add(new ValidMessage("申购产品列表中结算服务费不可为空!"));
            } else {
                settFeeAmt = ArithmeticUtil.doAdd(settFeeAmt, item.getSettFeeAmt());
            }
            // 应缴款金额
            if (!StringUtil.isNotEmpty(item.getPayableAmount())) {
                errorList.add(new ValidMessage("申购产品列表中应缴款金额不可为空!"));
            } else {
                payableAmount = ArithmeticUtil.doAdd(payableAmount, item.getPayableAmount());
            }
            // 业务模式
            if (StringUtil.isEmpty(item.getBusinessModel())) {
                errorList.add(new ValidMessage("申购产品列表中业务模式不可为空!"));
            }
            // 证券账户
            if (StringUtil.isEmpty(item.getSecManageAcctId())) {
                errorList.add(new ValidMessage("申购产品列表中证券账户不可为空!"));
            } else {
                secManageAcctId = item.getSecManageAcctId();
            }
            if (StringUtil.isEmpty(item.getPartyId())) {
                errorType = 2;
            }
            // 申购产品列表中所有组合关联的交易账户必须一致
            if (StringUtil.isNotEmpty(partyId) && !StringUtil.equals(partyId, item.getPartyId()) && errorType != 2) {
                errorList.add(new ValidMessage("申购产品列表中所有组合关联的交易账户必须一致!"));
                errorType = 1;
            }
            partyId = item.getPartyId();
            // 申购产品列表中认购量要小于等于（剩余指令额度-流程中申购额度），不满足时提示到组合维度
            Map<String, Object> params = new HashMap<>();
            params.put("entrustId", item.getEntrustId());
            params.put("protfolioId", item.getPortfolioId());
            BigDecimal etrAvaNum = cfetsTrdBondFirstSub002Mapper.selectEtrAvaNum(params);
            if (BigDecimalUtil.ACompareToB(etrAvaNum, item.getSubscriptionAmount(), 6) < 0) {
                errorList.add(new ValidMessage("指令【" + item.getEntrustId() + "】中组合【" + item.getPortfolioName() + "】剩余指令额度不足!"));
            }

        }
        // 当参数为是时，本方必填，带入组合关联的银行间机构简称；
        //  申购产品列表中已添加的组合必须关联银行间机构数据，及所有组合关联的银行间机构数据必须一致，不满足时弹出提示且不可提交
        if (StringUtil.equals(DictCodeList.是否_是.getValue(), isSupportCstpFlagBondfirst) && errorType != 0) {
            errorList.add(new ValidMessage("申购产品列表中已添加的组合必须关联银行间机构数据，及所有组合关联的银行间机构数据必须一致!"));
        }

        // 申购产品列表中认购量、应缴款金额、结算手续费要等于基本信息中各字段的汇总值
        if (BigDecimalUtil.ACompareToB(subscriptionAmount, dto.getSubscriptionAmount(), 6) != 0) {
            errorList.add(new ValidMessage("申购产品列表中认购量的汇总值要等于基本信息中认购量!"));
        }
        if (BigDecimalUtil.ACompareToB(payableAmount, dto.getPayableAmount(), 6) != 0) {
            errorList.add(new ValidMessage("申购产品列表中应缴款金额的汇总值要等于基本信息中应缴款金额!"));
        }
        if (BigDecimalUtil.ACompareToB(settFeeAmt, dto.getSettFeeAmt(), 6) != 0) {
            errorList.add(new ValidMessage("申购产品列表中结算手续费的汇总值要等于基本信息中结算手续费!"));
        }
        FinProductIssue finProductIssue = finProductIssueMapper.selectByPrimaryKey(dto.getFinprodId());
        logger.debug("数值校验,发行信息:" + JsonUtils.object2Json(finProductIssue));
        // 基本信息中的认购量需大于等于标的中单笔最低量，小于等于单笔最高量，如标的中无数据则不用判断
        if (StringUtil.isNotEmpty(finProductIssue.getMinPamt())
                && BigDecimalUtil.ACompareToB(dto.getSubscriptionAmount(), finProductIssue.getMinPamt(), 6) < 0) {
            errorList.add(new ValidMessage("认购量需大于等于标的中单笔最低量!"));
        }
        if (StringUtil.isNotEmpty(finProductIssue.getMaxPamt())
                && BigDecimalUtil.ACompareToB(dto.getSubscriptionAmount(), finProductIssue.getMaxPamt(), 6) >= 0) {
            errorList.add(new ValidMessage("认购量需小于等于单笔最高量!"));
        }
        // 校验当前资产+证券账户维度下流程中、有效的申购记录认购量之和需小于等于标的中最高认购总量，如标的中无数据则不用判断
        Map<String, Object> param = new HashMap<>();
        param.put("secManageAcctId", secManageAcctId);
        param.put("finprodId", dto.getFinprodId());
        BigDecimal sumSubAmt = cfetsTrdBondFirstSub002Mapper.selectSumSubAmtBySecAcctIdAndFin(param);
        logger.debug("数值校验,当前资产+证券账户维度下流程中、有效的申购记录认购量之和:" + sumSubAmt);
        if (StringUtil.isNotEmpty(finProductIssue.getMaxTotalamt())
                && BigDecimalUtil.ACompareToB(sumSubAmt, finProductIssue.getMaxTotalamt(), 6) >= 0) {
            errorList.add(new ValidMessage("当前资产+证券账户维度下流程中、有效的申购记录认购量之和需小于等于标的中最高认购总量!"));
        }
        logger.debug("数值校验,结果:" + JsonUtils.object2Json(errorList));
        return errorList;
    }

    /**
     * 头寸处理
     *
     * @param cfetsRepoDealRegisterQuoteDto
     * @param cfetsRepoDealDbDto
     * @author jiangpeng
     * @date 2021/12/24 10:12
     */
    private void sendPositionMethod(CfetsTrdBondFirstSubInfoDto entity, boolean isSubmit, String confirmOk) {
        List<CfetsBondPortfolioCashRequest> ptlList = new ArrayList<>();

        for (CfetsTrdBondFirstSubProductDto productDto : entity.getProductDtos()) {
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
            posport.setCashAmount(productDto.getSubscriptionAmount());
            ptlList.add(posport);
        }
        //调用头寸
        CfetsBondPositionRequest req = new CfetsBondPositionRequest();
        req.setInterfaceType("Bond001");//报文类型
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
        req.setBusiType(DictCodeListExt3.头寸业务类型_债券一级申购.getValue());
        req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
        req.setBusiAct(DictCodeListExt3.业务动作_交易登记.getValue());
        req.setPs(DictCodeList.交易方向_买入.getValue());
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
     * @param entity   页面元素
     * @param flowType 交易流程状态 参数字典 TRADE_FLOW_STATUS
     * @author jiangpeng
     * @date 2022/4/28 10:37
     */
    public void dealOldPosition(CfetsTrdBondFirstSubInfoDto entity, boolean isRevokeOrBack) {
//        if (StringUtil.isNotEmpty(entity)) {
//            return;
//        }
        if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            // 撤销或退回
            entity.setTradeDate(iPlatformDate.getSysDate());
            if (isRevokeOrBack) {
                baseAssetCashPositionCommMapper.deleteCashPositionByOrderIds(Arrays.asList(entity.getBidId()));
            } else {
                for (CfetsTrdBondFirstSubProductDto deal : entity.getProductDtos()) {
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
                    trdProductDeal.setPrinAmt(deal.getSubscriptionAmount());
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
                    stlPayOrder.setSettleAmt(deal.getPayableAmount());
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

    /**
     * 获取组合头寸信息
     *
     * @param dto
     * @author jiangpeng
     * @date 2022/5/18 19:10
     */
    public void selectCash(CfetsTrdBondFirstSubInfoDto dto) {
        for (CfetsTrdBondFirstSubProductDto productDto : dto.getProductDtos()) {
            BaseMap cashMap = positionInfoService.queryTrdAvailableCash(
                    Arrays.asList(productDto.getPortfolioId()),
                    dto.getTradeDateStr(),
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


    @Override
    public String generateTitle(CfetsTrdBondFirstSubInfoDto dto, OperationType type) {
        String issuer = null;
        String finprodAbbr = null;
        String SubscriptionAmount = null;
        String str = null;
        if (StringUtil.isNotEmpty(dto.getFinprodAbbr())) {
            finprodAbbr = "资产简称:" + dto.getFinprodAbbr();
        }
        if (StringUtil.isNotEmpty(dto.getFinprodId())) {
            issuer = "发行人:" + this.getIssuer(dto.getFinprodId());
        }
        if (StringUtil.isNotEmpty(dto.getSubscriptionAmount())) {
            SubscriptionAmount = "认购量:" + ArithmeticUtil.divide(dto.getSubscriptionAmount(), new BigDecimal(10000), 2, RoundingMode.HALF_UP) + "万元";
        }
        str = "登记日期:" + DateUtil.format(iPlatformDate.getSysDate());
        str += StringUtil.isNotEmpty(finprodAbbr) ? "-" + finprodAbbr : "";
        str += StringUtil.isNotEmpty(issuer) ? "-" + issuer : "";
        str += StringUtil.isNotEmpty(SubscriptionAmount) ? "-" + SubscriptionAmount : "";
        return str;
    }
}

