package com.joyintech.tams.cfets.trd.repo.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.trade.common.service.TrdAvailableCashQueryService;
import com.joyintech.tams.business.common.dao.CfgDataTableFlagMapper;
import com.joyintech.tams.business.common.dao.CfgDataTableMapper;
import com.joyintech.tams.business.common.dao.CfgReadonlyFormShowMapper;
import com.joyintech.tams.business.common.dao.JoyinbusinessCommonMapper;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.entity.CfgDataTableShow;
import com.joyintech.tams.business.common.entity.CfgDataTableShowFlag;
import com.joyintech.tams.business.common.entity.CfgReadonlyFormShow;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondEtrMapper;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondMapper;
import com.joyintech.tams.cfets.trd.repo.dao.CfetsIbuQuoteRepo003Mapper;
import com.joyintech.tams.cfets.trd.repo.dao.CfetsIbuQuoteRepoMapper;
import com.joyintech.tams.cfets.trd.repo.dto.*;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbClosePosEtrRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbClosePositionRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtrIbBondinfoService;
import com.joyintech.tams.cfets.etr.repo.service.CfetsEtrIbRepoinfoService;
import com.joyintech.tams.common.base.dao.EtrDistributeMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.component.base.entrust.impl.dao.BaseEtrInvestInfoMapper;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.BasicTradeFeeInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.riskinfrds.util.EntrustCompleteUtil;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.EtrInvestBondInfoDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 本币交易-质押式回购交易 Service
 *
 * @author jiangpeng
 * @date 2021/12/4 15:37
 */
@Service
public class IbuQuoteRepoService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(IbuQuoteRepoService.class);

    /**
     * 用户自定义设置
     */
    private static final int EFFERANGE_IS = 1;

    /**
     * 默认设置
     */
    private static final int EFFERANGE_NO = 0;


    /**
     * 默认设置用户ID
     */
    private static final String USER_ID = "COMM";

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    @Autowired
    private CfetsRepoQuotedPrice004Service cfetsRepoQuotedPrice004Service;

    @Autowired
    private CfetsEtrIbRepoinfoService cfetsEtrIbRepoinfoService;

    @Autowired
    private TrdAvailableCashQueryService trdAvailableCashQueryService;

    @Autowired
    private CfetsEtrIbBondinfoService cfetsEtrIbBondinfoService;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;

    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    @Autowired(required = false)
    private BaseEtrInvestInfoServiceImpl etrInvesInfo;

    @Autowired
    private CfetsIbuQuoteRepoMapper cfetsIbuQuoteRepoMapper;

    @Autowired
    private CfetsIbuQuoteRepo003Mapper cfetsIbuQuoteRepo003Mapper;

    @Autowired
    private CfetsIbuQuoteBondMapper cfetsIbuQuoteBondMapper;

    @Autowired
    private CfetsIbuQuoteBondEtrMapper cfetsIbuQuoteBondEtrMapper;

    @Autowired
    private JoyinbusinessCommonMapper joyinbusinessCommonMapper;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private BaseEtrInvestInfoMapper baseEtrInvestInfoMapper;

    @Autowired
    private CfgReadonlyFormShowMapper cfgReadonlyFormShowMapper;

    @Autowired
    private CfgDataTableFlagMapper cfgDataTableFlagMapper;

    @Autowired
    private CfgDataTableMapper cfgDataTableMapper;

    @Autowired
    private EtrDistributeMapper etrDistributeMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;
    /**
     * 递增维度
     */
    private final static String dimension = "0.01";

    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    /**
     * 费用查询
     */
    @Autowired(required = false)
    private BaseTradeFeeCommService baseTradeFeeCommService;

    @Autowired(required = false)
    private DateCalService dateCalService;
    /**
     * 利率保留位数
     */
    private static final int yieldScale = 4;

    /**
     * 质押式回购 头寸信息查询
     *
     * @param requestDto 查询条件
     * @return 头寸信息
     */
    public List<BaseMap> queryPositionInfo(CfetsIbuQuoteRepoPosiInfoRequestDto requestDto) {
        // 条件非空校验
        posiInfoCheckCondition(requestDto);
        // 未选择组合时展示
        if (requestDto.getPortfolioIdList() == null || requestDto.getPortfolioIdList().size() == 0) {
            return nullData();
        }
        BigDecimal cashT = BigDecimal.ZERO; // 组合t日可用
        BigDecimal cashT1 = BigDecimal.ZERO; // 组合t1日可用
        BigDecimal realCash = BigDecimal.ZERO; // 账户现金
        List<String> list = requestDto.getPortfolioIdList();
        // 托管账户
        String acctType_A01 = BussinessDictCodeList.资金账户类型_托管账户.getValue();
        // 从原投管系统取值
        if (list != null) {
            for (String portfolioId : list) {
                Date tradeDate = DateUtil.parse(requestDto.getCdate());//交易日期
                String dateId = "03";//交易日历_银行间
                //账户类型
                String acctTypeListStr = acctType_A01 + GlobalConstant.COMMA + BussinessDictCodeList.资金账户类型_交易所资金账户.getValue();
                // 交易日历的节假日取得
                List<String> holidayList = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDay(tradeDate, 15), dateId);
                BaseMap paramMap = BaseMap.newMap();
                paramMap.put("portfolioId", portfolioId);//组合代码
                paramMap.put("ccy", DictCodeList.币种_人民币_CNY.getValue());//币种
                paramMap.put("tradeDate", requestDto.getCdate());//交易日期
                paramMap.put("tradeDateNext", DateUtil.format(dateCalService.calculationTradingDay(tradeDate, 1, holidayList, "1")));//T+1日
                paramMap.put("acctTypeList", acctTypeListStr);//账户类型
                // 原投管系统查询组合可用现金
                Map<String, BigDecimal> mapRst = trdAvailableCashQueryService.getAvailableCash(paramMap);
                if (mapRst != null) { // 组合t日可用，组合t1日可用
                    cashT = ArithmeticUtil.doAdd(cashT, mapRst.get("T_0"));
                    cashT1 = ArithmeticUtil.doAdd(cashT1, mapRst.get("T_1"));
                }
                PtlCashPositionFlowDto positionQueryDto = new PtlCashPositionFlowDto();
                positionQueryDto.setPortfolioId(portfolioId);
                positionQueryDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                positionQueryDto.setCdate(iPlatformDate.getSysDate());
                List<String> acctTypeListTemp = new ArrayList<>();
                acctTypeListTemp.add(acctType_A01);
                positionQueryDto.setAcctType(acctTypeListTemp);
                // 实际现金（账户现金）
                realCash = ArithmeticUtil.doAdd(realCash, basePtlCashPositionInfoService.queryUsableCashPosition(positionQueryDto));
            }
        }
        // 从组合现金表中取值
        BaseMap bm = BaseMap.newMap();
        bm.put("S_004", DictCodeListExt3.资产类型_汇总可用现金.getValue());
        bm.put("C_01", DictCodeListExt3.资产类型_现金账户持仓.getValue());
        bm.put("portfolioIdList", requestDto.getPortfolioIdList());
        bm.put("cdate", requestDto.getCdate());
        bm.put("acctType", acctType_A01);
        // 查询头寸信息
        Map map = cfetsIbuQuoteRepoMapper.queryPositionInfo(bm);
        // 汇总以及组装数据
        return assemble(map, cashT, cashT1, realCash);
    }

    /**
     * 初始化指令列表
     *
     * @param dto 查询条件
     * @return 指令数据
     */
    public Page<RepoEntrustDto> listEntrust(RepoEntrustRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        pageParam.put("sort", StringUtil.isNotEmpty(dto.getPageMode()) && StringUtil.isNotEmpty(dto.getPageMode().getSort()) ? dto.getPageMode().getSort() : "");// 排序字段
        pageParam.put("direction", StringUtil.isNotEmpty(dto.getPageMode()) && StringUtil.isNotEmpty(dto.getPageMode().getDirection()) ? dto.getPageMode().getDirection() : "");//排序方式
        // 银行间质押式回购质押券模式
        pageParam.put("repo_pledge_model", CacheUtil.getSysParam("repo_pledge_model"));
        setParam(pageParam, 1);
        if (StringUtil.isNotEmpty(pageParam.get("entrustId"))) {
            pageParam.put("entrustIdSearch", pageParam.get("entrustId"));
            pageParam.remove("entrustId");
        }
        int pageNo = dto.getPageMode() == null ? 1 : dto.getPageMode().getPageNo();
        int pageSize = dto.getPageMode() == null ? 10 : dto.getPageMode().getPageSize();
        return super.queryForPage(pageNo, pageSize, () -> cfetsIbuQuoteRepoMapper.listEntrust(pageParam));
    }

    /**
     * 初始化指令列表
     *
     * @param dto 查询条件
     * @return 指令数据
     */
    public List<RepoEntrustDto> listEntrust(RepoEntrustRequest dto, String loginUserId) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        pageParam.put("sort", StringUtil.isNotEmpty(dto.getPageMode()) && StringUtil.isNotEmpty(dto.getPageMode().getSort()) ? dto.getPageMode().getSort() : "");// 排序字段
        pageParam.put("direction", StringUtil.isNotEmpty(dto.getPageMode()) && StringUtil.isNotEmpty(dto.getPageMode().getDirection()) ? dto.getPageMode().getDirection() : "");//排序方式
        // 银行间质押式回购质押券模式
        pageParam.put("repo_pledge_model", CacheUtil.getSysParam("repo_pledge_model"));
        setParam(pageParam, 1);
        if (StringUtil.isNotEmpty(loginUserId)) {
            pageParam.put("loginUserId", loginUserId);
        }
        List<RepoEntrustDto> result = new ArrayList<>();
        List<CfetsRepoDealEntrustOrderDto> list = cfetsIbuQuoteRepoMapper.listEntrust(pageParam);
        list.forEach(item -> {
            RepoEntrustDto entrustDto = JsonUtils.json2Object(JsonUtils.object2Json(item), RepoEntrustDto.class);
            result.add(entrustDto);
        });
        return result;
    }


    /**
     * 交易执行点击check
     *
     * @param cfetsRepoDealQuoteRequest 查询条件
     * @return 校验结果
     */
    public AjaxResult dealexecutioncheck(CfetsRepoDealQuoteRequest cfetsRepoDealQuoteRequest) {
        AjaxResult ajaxResult = new AjaxResult();
        if (CollectionUtils.isNotEmpty(cfetsRepoDealQuoteRequest.getEntrustIdList())) {
            try {
                Map<String, Object> param = BeanUtil.object2Map(cfetsRepoDealQuoteRequest);
                setParam(param, 1);
                List<CfetsRepoDealEntrustOrderDto> entrustDtos = cfetsIbuQuoteRepoMapper.listEntrust(param);
                if (!StringUtil.isEmptyList(entrustDtos)) {
                    ajaxResult = preconditionJudgment(entrustDtos, cfetsRepoDealQuoteRequest.getQuoteType());
                }
            } catch (Exception e) {
                ajaxResult.setMessage("交易执行检查异常!");
                return ajaxResult;
            }
        }
        return ajaxResult;
    }

    /**
     * 查询质押券信息
     *
     * @param request 查询条件
     * @return 质押券信息
     */
    public List<RepoEntrustPledgeDto> selectRepoEntrustPledges(CfetsRepoDealQuoteRequest request) {
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        setParam(pageParam, 2);
        return cfetsIbuQuoteRepoMapper.selectRepoEntrustPledges(pageParam);
    }

    /**
     * 查询可用质押券信息
     *
     * @param request 查询条件
     * @return 质押券信息
     */
    public Page<RepoEntrustPledgeDto> pledgesAdd(RepoPledgeRequest request) {
        Page<RepoEntrustPledgeDto> page = new Page<>();
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        String repo_pledge_model = CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL);
        List<RepoEntrustPledgeDto> list = new ArrayList<>();
        List<String> portFinList = new ArrayList<>();
        // 去除已质押券
        for (RepoEntrustPledgeDto dto : request.getPledgeDtos() == null ? new ArrayList<RepoEntrustPledgeDto>() : request.getPledgeDtos()) {
            portFinList.add(dto.getPortfolioId() + dto.getFinprodId());
        }
        pageParam.put("portFinList", portFinList);
        int pageNo = request.getPageMode() == null ? 1 : request.getPageMode().getPageNo();
        int pageSize = request.getPageMode() == null ? 10 : request.getPageMode().getPageSize();
        if (StringUtil.equals(repo_pledge_model, "02")) {//指定质押券
            setParam(pageParam, 2);
            page = super.queryForPage(pageNo, pageSize, () -> cfetsIbuQuoteRepoMapper.selectUsableEntrustPledges(pageParam));
        } else {
            setParam(pageParam, 3);
            page = super.queryForPage(pageNo, pageSize, () -> cfetsIbuQuoteRepoMapper.selectUsablePledges(pageParam));
        }
        for (RepoEntrustPledgeDto dto : page.getRoot()) {
            String pendingPeriodStr = joyinbusinessCommonService.calPendingPeriod(dto.getFinprodId());
            String period = pendingPeriodStr.substring(0, pendingPeriodStr.length() - 1);
            String periodUnit = pendingPeriodStr.substring(pendingPeriodStr.length() - 1, pendingPeriodStr.length());
            dto.setPeriodStr(pendingPeriodStr);
        }
        return page;
    }

    /**
     * 查询可用质押券信息
     *
     * @param request 查询条件
     * @return 质押券信息
     */
    public Page<RepoEntrustPledgeDto> pledgesAdd04(RepoPledgeRequest request) {
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        List<RepoEntrustPledgeDto> list = new ArrayList<>();
        List<String> finprodIds = new ArrayList<>();
        // 去除已质押券
        for (RepoEntrustPledgeDto dto : request.getPledgeDtos() == null ? new ArrayList<RepoEntrustPledgeDto>() : request.getPledgeDtos()) {
            finprodIds.add(dto.getFinprodId());
        }
        setParam(pageParam, 2);
        pageParam.put("nofinprodIdList", finprodIds);
        int pageNo = 1;
        int pageSize = 10;
        if (StringUtil.isNotEmpty(request.getPageMode())) {
            pageNo = StringUtil.isNotEmpty(request.getPageMode().getPageNo()) ? request.getPageMode().getPageNo() : 1;
            pageSize = StringUtil.isNotEmpty(request.getPageMode().getPageSize()) ? request.getPageMode().getPageSize() : 10;
            pageParam.put("sort", request.getPageMode().getSort());
            pageParam.put("direction", request.getPageMode().getDirection());
        }
        return super.queryForPage(pageNo, pageSize, () -> cfetsIbuQuoteRepoMapper.selectBondInfo(pageParam));
    }

    /**
     * 初始化报价页面(委托代码)
     *
     * @param cfetsRepoDealQuoteRequest 查询条件
     * @return 初始化数据
     */
    public CfetsRepoDealQuoteCommonDto queryEntrustInfoById(CfetsRepoDealQuoteRequest cfetsRepoDealQuoteRequest) {
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = new CfetsRepoDealQuoteCommonDto();
        Map<String, Object> param = BeanUtil.object2Map(cfetsRepoDealQuoteRequest);
        setParam(param, 1);
        param.put("isInit", "Y");
        // 指令信息查询
        List<CfetsRepoDealEntrustOrderDto> entrustDtos = cfetsIbuQuoteRepoMapper.listEntrust(param);
        setParam(param, 2);
        // 质押券信息查询
        List<RepoEntrustPledgeDto> repoEntrustPledgeDtos = cfetsIbuQuoteRepoMapper.selectRepoEntrustPledgesInit(param);
        if (!StringUtil.isEmptyList(entrustDtos)) {
            cfetsRepoDealQuoteCommonDto = buildEntrustInfo(entrustDtos, repoEntrustPledgeDtos);
        }
        // 期限品种
        cfetsRepoDealQuoteCommonDto.setTermType(cfetsEtrIbRepoinfoService.getTermType(cfetsRepoDealQuoteCommonDto.getRepoTerm()));
        cfetsRepoDealQuoteCommonDto.setLoginUserId(LoginUserUtil.getUserName());
        cfetsRepoDealQuoteCommonDto.setQuotaPageModel("new");
        return cfetsRepoDealQuoteCommonDto;
    }

    /**
     * 添加指令联动添加质押券
     *
     * @param cfetsRepoDealQuoteRequest 查询条件
     * @return 初始化数据
     */
    public List<RepoEntrustPledgeDto> ptlAllocationAddAfter(RepoAutoAllocationRequest request) {
        List<RepoEntrustPledgeDto> resultList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("entrustIdList", request.getEntrustIdList());
        setParam(param, 2);
        // 质押券信息查询
        List<RepoEntrustPledgeDto> repoEntrustPledgeDtos = cfetsIbuQuoteRepoMapper.selectRepoEntrustPledgesInit(param);
        for (RepoEntrustPledgeDto newDto : repoEntrustPledgeDtos) {
            boolean flag = false;
            for (RepoEntrustPledgeDto oldDto : request.getRepoEntrustPledgeDtos()) {
                if ((StringUtil.equals("dialogueResponse", request.getQuotaPageModel())
                        || StringUtil.equals("edit", request.getQuotaPageModel()))
                        && StringUtil.equals(DictCodeListExt3.交易方向_逆回购.getValue(), request.getPs())) {
                    flag = true;
                    continue;
                }
                // 正回购
                if (StringUtil.equals(newDto.getFinprodId(), oldDto.getFinprodId())
                        && StringUtil.equals(newDto.getPortfolioId(), oldDto.getPortfolioId())) {
                    oldDto.setPledgePrin(ArithmeticUtil.doAdd(oldDto.getPledgePrin(), newDto.getPledgePrin()));
                    oldDto.setAvaPledgePrin(ArithmeticUtil.doAdd(oldDto.getAvaPledgePrin(), newDto.getAvaPledgePrin()));
                    oldDto.setConvertAmt(ArithmeticUtil.multiply(oldDto.getPledgePrin(), ArithmeticUtil.divide(oldDto.getPledgeRatio(), new BigDecimal(100), 6)));
                    flag = true;
                }
                // 逆回购
                if (StringUtil.equals(newDto.getFinprodId(), oldDto.getFinprodId())
                        && !StringUtil.equals("dialogueResponse", request.getQuotaPageModel())
                        && !StringUtil.equals("edit", request.getQuotaPageModel())
                        && StringUtil.equals(DictCodeListExt3.交易方向_逆回购.getValue(), request.getPs())) {
                    oldDto.setPledgePrin(ArithmeticUtil.doAdd(oldDto.getPledgePrin(), newDto.getPledgePrin()));
                    oldDto.setAvaPledgePrin(ArithmeticUtil.doAdd(oldDto.getAvaPledgePrin(), newDto.getAvaPledgePrin()));
                    oldDto.setConvertAmt(ArithmeticUtil.multiply(oldDto.getPledgePrin(), ArithmeticUtil.divide(oldDto.getPledgeRatio(), new BigDecimal(100), 6)));
                    flag = true;
                }
            }
            if (!flag) {
                resultList.add(newDto);
            }
        }
        resultList.addAll(request.getRepoEntrustPledgeDtos());
        return resultList;
    }

    /**
     * 质押式回购计算费用 根据[交易方向][交易金额][证券账户][期限品种]计算 [交易手续费][结算手续费]
     *
     * @param dto ps [交易方向] tradeAmt [交易金额] secManageAcctId[证券账户] termType[期限品种]
     * @return com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDealQuoteCommonDto
     * @author jiangpeng
     * @date 2021/12/8 15:44
     */
    public CfetsRepoDealQuoteCommonDto calFee(RepoCalRequest dto) {
        CfetsRepoDealQuoteCommonDto resultDto = new CfetsRepoDealQuoteCommonDto();
        try {
            List<TrdTradeFeeDetail> tradeFeeList = new ArrayList<>();
            BasicTradeFeeInputDto inputDto = new BasicTradeFeeInputDto();
            inputDto.setTradeDate(iPlatformDate.getSysDate());
            inputDto.setFinprodType2(BussinessDictCodeList.平安资产类型_回购.getValue());
            inputDto.setPs(dto.getPs());
            inputDto.setFpriceAmt(dto.getTradeAmt());
            inputDto.setPrinAmt(dto.getTradeAmt());
            List<String> feeTypeList = new ArrayList<>();
            // 银行间费用设置
            inputDto.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
            feeTypeList.add(BussinessDictCodeList.费用类型_交易手续费.getValue());
            feeTypeList.add(BussinessDictCodeList.费用类型_结算手续费.getValue());
            inputDto.setFeeTypeList(feeTypeList);
            // 登记托管机构用证券托管户的登记托管机构
            if (StringUtil.isNotEmpty(dto.getSecManageAcctId())) {
                String registOrg = cfetsIbuQuoteBondMapper.getRegistOrg(dto.getSecManageAcctId());
                inputDto.setRegistOrg(registOrg);
            }
            inputDto.setTermType(dto.getTermType());
            if (dto.getPledgeNum() > 1) {
                inputDto.setIsGrade(BussinessDictCodeList.是否_是.getValue());
            } else {
                inputDto.setIsGrade(BussinessDictCodeList.是否_否.getValue());
            }
            tradeFeeList = baseTradeFeeCommService.innerBankFeeDeal(inputDto);

            if (CollectionUtils.isNotEmpty(tradeFeeList)) {
                tradeFeeList.forEach(item -> {
                    if (StringUtil.equals(item.getFeeType(), BussinessDictCodeList.费用类型_交易手续费.getValue())) {
                        resultDto.setDealFeeAmt(item.getFeeAmt());
                        return;
                    }
                    if (StringUtil.equals(item.getFeeType(), BussinessDictCodeList.费用类型_结算手续费.getValue())) {
                        resultDto.setSettFeeAmt(item.getFeeAmt());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("计算费用异常:" + e.getMessage());
        }
        return resultDto;
    }

    /**
     * 组合列表添加初始化
     *
     * @param ptlAllocationAddRequest notentrustIdList已添加组合
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.RepoEntrustDto>
     * @author jiangpeng
     * @date 2021/12/20 14:15
     */
    public List<RepoEntrustDto> ptlAllocationAdd(RepoPtlAllocationAddRequest ptlAllocationAddRequest) {
        Map<String, Object> param = BeanUtil.object2Map(ptlAllocationAddRequest);
        param.put("notentrustIdList", ptlAllocationAddRequest.getNotEntrustIdList());
        param.put("platDate", iPlatformDate.getSysDateStr());
        param.put("entrustStatus", Collections.singletonList(DictCodeList.委托状态_已接收.getValue()));
        setParam(param, 1);
        param.put("repo_pledge_model", CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL));
        List<CfetsRepoDealEntrustOrderDto> entrustDtos = new ArrayList<>();
        if (StringUtil.isEmpty(ptlAllocationAddRequest.getQuoteId())) {
            entrustDtos = cfetsIbuQuoteRepoMapper.listEntrust(param);
        } else {
            param.put("A_001", DictCodeListExt3.资产类型_资产账户持仓.getValue());
            param.put("A_012", DictCodeListExt3.资产类型_质押式双边回购本方券已质押持仓.getValue());
            param.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
            entrustDtos = cfetsIbuQuoteRepo003Mapper.listEntrust(param);
        }
        if (!StringUtil.isEmptyList(entrustDtos)) {
            return initPtlAllocation(entrustDtos);
        }
        return new ArrayList<>();
    }

    /**
     * 删除组合联动
     *
     * @param ptlAllocationAddRequest entrustIdList删除前的所有指令代码列表,delEtrId 删除的指令代码
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.RepoEntrustPledgeDto>
     * @author jiangpeng
     * @date 2021/12/20 14:16
     */
    public List<RepoEntrustPledgeDto> ptlAllocationDel(RepoAutoAllocationRequest ptlAllocationAddRequest) {
        List<RepoEntrustPledgeDto> pledgeDtos = new ArrayList<>();
        List<RegFinPledge> regFinPledges = cfetsRepoQuotedPrice004Service.dealWithPledgeList(
                ptlAllocationAddRequest.getRepoEntrustPledgeDtos(), ptlAllocationAddRequest.getEntrustIdList(), null, null, null);
        for (RepoEntrustPledgeDto pledgeDto : ptlAllocationAddRequest.getRepoEntrustPledgeDtos()) {
            for (RegFinPledge pledge : regFinPledges) {
                if (StringUtil.equals(pledge.getPortfolioId(), pledgeDto.getPortfolioId())
                        && StringUtil.equals(pledge.getPledgeFinprodId(), pledgeDto.getFinprodId())
                        && StringUtil.equals(pledge.getEntrustId(), ptlAllocationAddRequest.getDelEtrId())) {
                    if (StringUtil.isNotEmpty(pledgeDto.getPledgePrinStr())) {
                        pledgeDto.setPledgePrin(ArithmeticUtil.multiply(new BigDecimal(pledgeDto.getPledgePrinStr()), new BigDecimal(10000)));
                    }
                    pledgeDto.setPledgePrin(ArithmeticUtil.subtract(pledgeDto.getPledgePrin(),
                            ArithmeticUtil.multiply(pledge.getPledgeNumber(), new BigDecimal(100))));
                }
            }
            if (BigDecimal.ZERO.compareTo(pledgeDto.getPledgePrin()) != 0) {
                pledgeDtos.add(pledgeDto);
            }
        }
        return pledgeDtos;
    }

    /**
     * 自动分配 根据[分配模式][组合分配信息][质押券信息]计算 组合分配中[交易金额][回购利息][到期结算金额][交易手续费]
     *
     * @param autoAllocationRequest distribution[分配模式] [组合分配信息] [质押券信息]
     * @return com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDealQuoteCommonDto
     * @author jiangpeng
     * @date 2021/12/20 14:18
     */
    public CfetsRepoDealQuoteCommonDto autoAllocation(RepoAutoAllocationRequest autoAllocationRequest) {
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = new CfetsRepoDealQuoteCommonDto();
        //备份剩余额度
        List<RepoPtlAllocationDto> ptlAllocations = autoAllocationRequest.getPtlAllocations();
        if (CollectionUtils.isNotEmpty(ptlAllocations)) {
            ptlAllocations.forEach(item -> {
                item.setAvaNumOld(item.getAvaNum());
            });
        }
        try {
            //无分配模式
            if (StringUtil.isEmpty(autoAllocationRequest.getDistribution())) {
                // 按比例分配 交易手续费 回购利息 到期结算金额; 平均分配结算手续费
                ptlAllocations = noneDistribution(autoAllocationRequest, autoAllocationRequest.getPtlAllocations(), autoAllocationRequest.getRepoEntrustPledgeDtos());
            }
            //平均分配
            if (StringUtil.equals(autoAllocationRequest.getDistribution(), "139")) {
                ptlAllocations = aveDistribution(autoAllocationRequest, autoAllocationRequest.getPtlAllocations(), autoAllocationRequest.getRepoEntrustPledgeDtos());
            }
            //比例分配
            if (StringUtil.equals(autoAllocationRequest.getDistribution(), "143")) {
                ptlAllocations = propDistribution(autoAllocationRequest, autoAllocationRequest.getPtlAllocations(), autoAllocationRequest.getRepoEntrustPledgeDtos());
            }
            if (CollectionUtils.isNotEmpty(ptlAllocations)) {
                ptlAllocations.forEach(item -> {
                    item.setAvaNum(item.getAvaNumOld());
                });
            }
        } catch (Exception e) {
            logger.error("自动匹配计算异常:" + e.getMessage());
        }
        if (CollectionUtils.isNotEmpty(ptlAllocations)) {
            cfetsRepoDealQuoteCommonDto.setPtlAllocations(ptlAllocations);
        }

        return cfetsRepoDealQuoteCommonDto;
    }

    /**
     * 无分配模式
     * 按比例分配 交易手续费 回购利息 到期结算金额; 平均分配结算手续费
     *
     * @param request               入参
     * @param ptlAllocations        组合分配信息
     * @param repoEntrustPledgeDtos 质押券信息
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.RepoPtlAllocationDto>
     * @author jiangpeng
     * @date 2021/12/20 14:28
     */
    public List<RepoPtlAllocationDto> noneDistribution(RepoAutoAllocationRequest request,
                                                       List<RepoPtlAllocationDto> ptlAllocations, List<RepoEntrustPledgeDto> repoEntrustPledgeDtos) {
        // 过滤没有质押券的组合
        List<RepoPtlAllocationDto> ptlAllocationListNeed = new ArrayList<>();
        List<RepoPtlAllocationDto> ptlAllocationListNoNeed = new ArrayList<>();
        for (RepoPtlAllocationDto ptlAllocation : ptlAllocations) {
            boolean flag = false;
            for (RepoEntrustPledgeDto repoEntrustPledgeDto : repoEntrustPledgeDtos) {
                if (StringUtil.isEmpty(repoEntrustPledgeDto.getPortfolioId())
                        || StringUtil.equals(ptlAllocation.getPortfolioId(), repoEntrustPledgeDto.getPortfolioId())) {
                    flag = true;
                    break;
                }
            }
            // 逆回购没有质押券时
            if (CollectionUtils.isEmpty(repoEntrustPledgeDtos)) {
                flag = false;
            }
            if (flag) {
                ptlAllocationListNeed.add(ptlAllocation);
            } else {
                ptlAllocationListNoNeed.add(ptlAllocation);
            }
        }
        if (StringUtil.equals("dialogueResponse", request.getQuotaPageModel())) {
            // 过滤没有质押券的组合
            BigDecimal tradeAmt = request.getTradeAmt();
            //降序
            ptlAllocationListNeed.sort((p1, p2) -> p1.getAvaNum().compareTo(p2.getAvaNum()));
            BigDecimal avg = ArithmeticUtil.divide(tradeAmt, ptlAllocationListNeed.size(), 4);
            //剩余尾差
            BigDecimal tailDifference = tradeAmt;
            for (RepoPtlAllocationDto item : ptlAllocationListNeed) {
                if (item.getAvaNum().compareTo(avg) >= 0) {
                    item.setTradeAmt(avg);
                } else {
                    item.setTradeAmt(item.getAvaNum());
                }
                item.setAvaNum(ArithmeticUtil.subtract(item.getAvaNum(), item.getTradeAmt()));
                tailDifference = ArithmeticUtil.subtract(tailDifference, item.getTradeAmt());
                if(tailDifference.compareTo(BigDecimal.ZERO)<=0){
                    break;
                }
            }
            handleTailDifference(ptlAllocationListNeed, tailDifference);
        }
        calOtherForPtlAllocation(ptlAllocationListNeed, request.getPs(), request.getTradeAmt(), request.getmIntAmt(),
                request.getMtradeAmt(), request.getDealFeeAmt(), request.getSettFeeAmt(), request.getConvertAmt(), request.getShareAmt());
        ptlAllocationListNeed.addAll(ptlAllocationListNoNeed);
        return ptlAllocationListNeed;
    }

    /**
     * 金额信息计算 根据[清算速度][交易金额][回购利率][回购期限] 计算 [首次结算日][到期结算日][实际占款天数][回购利息][到期结算金额]
     *
     * @param request settleSpreedCode[清算速度] tradeAmt[交易金额] price[回购利率] repoTerm[回购期限]
     * @return com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDealQuoteCommonDto
     * @author jiangpeng
     * @date 2021/12/9 15:15
     */
    public CfetsRepoDealQuoteCommonDto calAmtInfo(RepoCalRequest request) {
        // 平台日
        Date tradeDate = iPlatformDate.getSysDate();
        // 回购期限
        int repoTerm = request.getRepoTerm();
        // 银行间交易日历的节假日取得
        List<String> holidayList = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDay(tradeDate, repoTerm + 15), "03");
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = new CfetsRepoDealQuoteCommonDto();
        // 首次结算日：平台日+清算速度，根据银行间节假日规则遇节假日顺延至下一个工作日
        if (StringUtil.equals(request.getSettleSpreedCode(), BussinessDictCodeList.清算速度_T加0.getValue())) {
            cfetsRepoDealQuoteCommonDto.setSettlementStartDate(tradeDate);
        }
        if (StringUtil.equals(request.getSettleSpreedCode(), BussinessDictCodeList.清算速度_T加1.getValue())) {
            cfetsRepoDealQuoteCommonDto.setSettlementStartDate(dateCalService.calculationTradingDay(tradeDate, 1, holidayList, "1"));
        }
        if (StringUtil.equals(request.getSettleSpreedCode(), BussinessDictCodeList.清算速度_T加2.getValue())) {
            cfetsRepoDealQuoteCommonDto.setSettlementStartDate(dateCalService.calculationTradingDay(tradeDate, 2, holidayList, "1"));
        }
        // 到期结算日=首次结算日+回购期限，遇银行间节假日调整到下一工作日
        if (StringUtil.isNotEmpty(cfetsRepoDealQuoteCommonDto.getSettlementStartDate())) {
            cfetsRepoDealQuoteCommonDto.setSettlementEndDate(DateUtil.addDay(cfetsRepoDealQuoteCommonDto.getSettlementStartDate(), repoTerm - 1));
            cfetsRepoDealQuoteCommonDto.setSettlementEndDate(
                    dateCalService.calculationTradingDay(cfetsRepoDealQuoteCommonDto.getSettlementEndDate(), 1, holidayList, "1"));
        }
        // 实际占款天数=到期结算日-首次结算日
        cfetsRepoDealQuoteCommonDto.setActCapDays(
                DateUtil.getBetweenDayNumber(cfetsRepoDealQuoteCommonDto.getSettlementStartDate(), cfetsRepoDealQuoteCommonDto.getSettlementEndDate()));
        // 回购利息 本金金额*回购利率*实际占款天数/计息基础(365)
        cfetsRepoDealQuoteCommonDto.setmIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(request.getTradeAmt(),
                ArithmeticUtil.divide(request.getPrice(), new BigDecimal(100), 12, RoundingMode.HALF_UP),
                new BigDecimal(cfetsRepoDealQuoteCommonDto.getActCapDays())), new BigDecimal(365), 2, RoundingMode.HALF_UP));
        // 到期结算金额 本金金额+回购利息
        cfetsRepoDealQuoteCommonDto.setMtradeAmt(ArithmeticUtil.doAdd(request.getTradeAmt(), cfetsRepoDealQuoteCommonDto.getmIntAmt()));
        cfetsRepoDealQuoteCommonDto.setTermType(cfetsEtrIbRepoinfoService.getTermType(repoTerm));
        return cfetsRepoDealQuoteCommonDto;
    }

    /**
     * 风险试算check
     *
     * @param entity 入参
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2021/12/20 14:30
     */
    public AjaxResult riskCheckNotEmpty(RepoRiskCalRequest entity) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if (!StringUtil.isNotEmpty(entity.getTradeDate())) {
            errorList.add(new ValidMessage("请输入交易日期"));
        }
        if (!StringUtil.isNotEmpty(entity.getPs())) {
            errorList.add(new ValidMessage("请输入交易方向"));
        }
        if (CollectionUtils.isNotEmpty(entity.getPtlAllocations())) {
            for (RepoPtlAllocationDto ptlAllocation : entity.getPtlAllocations()) {
                if (!StringUtil.isNotEmpty(ptlAllocation.getTradeAmt())) {
                    errorList.add(new ValidMessage("请输入组合分配本金金额"));
                    break;
                }
                if (!StringUtil.isNotEmpty(ptlAllocation.getPortfolioId())) {
                    errorList.add(new ValidMessage("请输入组合分配组合代码"));
                    break;
                }
            }
        } else {
            errorList.add(new ValidMessage("请输入组合分配"));
        }
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * 风险试算实体组装
     *
     * @param riskCalRequest 入参
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author jiangpeng
     * @date 2021/12/20 14:31
     */
    public Map<String, Object> doRiskcalPackage(RepoRiskCalRequest riskCalRequest) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> bussRepoSecPtl = new HashMap<>();
        List<Map> pledgeBondList = new ArrayList<>();
        for (RepoPtlAllocationDto ptlAllocationDto : riskCalRequest.getPtlAllocations()) {
            Map<String, Object> stringObjectMap = BeanUtil.object2Map(ptlAllocationDto);
            stringObjectMap.put("priAmt", ptlAllocationDto.getTradeAmt());// 本金金额
            stringObjectMap.put("rebackPrice", riskCalRequest.getPrice());//回购利率
            stringObjectMap.put("vdate", riskCalRequest.getSettlementStartDate());// 起息日
            stringObjectMap.put("mdate", riskCalRequest.getSettlementEndDate());// 到期日
            stringObjectMap.put("tradeMarket", "01");//交易场所 银行间
            stringObjectMap.put("assetType1", DictCodeList.平安资产类型_质押式回购.getValue());// 交易类型
            stringObjectMap.put("ps", riskCalRequest.getPs());// 交易方向
            stringObjectMap.put("ccy", "CNY");// 币种
            stringObjectMap.put("calendarld", "03");// 交易日历 银行间
            stringObjectMap.put("tradeDate", riskCalRequest.getTradeDate());// 交易日期
            stringObjectMap.put("counterId", riskCalRequest.getCounterId());// 交易对手
            pledgeBondList.add(stringObjectMap);
        }
        bussRepoSecPtl.put("pledgeBondList", pledgeBondList);//

        result.put("bussRepoSecPtl", bussRepoSecPtl);
        return result;

    }

    /**
     * 风险状态
     *
     * @param kriStatus 风险状态
     * @return java.lang.String
     * @author jiangpeng
     * @date 2021/12/13 11:44
     */
    public String getKriStatusCnByKriStatus(String kriStatus) {
        if (StringUtil.isNotEmpty(kriStatus)) {
            if (StringUtil.equals(kriStatus, "0")) {
                return "正常";
            } else if (StringUtil.equals(kriStatus, "1")) {
                return "预警";
            } else if (StringUtil.equals(kriStatus, "2")) {
                return "审批";
            } else if (StringUtil.equals(kriStatus, "3")) {
                return "违规";
            } else if (StringUtil.equals(kriStatus, "4")) {
                return "异常";
            }
        }
        return null;
    }

    /**
     * 查询交易执行明细信息
     *
     * @param entrustId 指令代码
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.DealTransactionDetailsDto>
     * @author jiangpeng
     * @date 2021/12/20 14:31
     */
    public Page<RepoDealTransactionDetailsDto> listDealTransactionDetails(Map<String, Object> param) {
        String loginUserId = LoginUserUtil.getUserName();
        param.put("rolePos03", DictCodeListExt2.角色岗位_交易处长.getValue());
        if ("admin".equals(loginUserId)) {
//            loginUserId = "jp";//TODO 测试写死登录用户 提交代码时修改
        }
        param.put("loginUserId", loginUserId);
        int pageNo = 1;
        int pageSize = 10;
        if (param.get("pageNo") != null) {
            pageNo = (int) param.get("pageNo");
        }
        if (param.get("pageSize") != null) {
            pageSize = (int) param.get("pageSize");
        }
        return super.queryForPage(pageNo, pageSize, () -> cfetsIbuQuoteRepoMapper.listDealTransactionDetails(param));
    }

    /**
     * 查询交易执行明细信息
     *
     * @param entrustId 指令代码
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.DealTransactionDetailsDto>
     * @author jiangpeng
     * @date 2021/12/20 14:31
     */
    public List<RepoDealTransactionDetailsDto> listDealTransactionDetailsNoUserId(String entrustId) {
        Map<String, Object> param = new HashMap<>();
        String loginUserId = LoginUserUtil.getUserName();
        param.put("entrustId", entrustId);
        return cfetsIbuQuoteRepoMapper.listDealTransactionDetails(param);
    }

    /**
     * 根据交易编号查询查看信息界面
     *
     * @param quoteId 交易编号
     * @return com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDealQuoteCommonDto
     * @author jiangpeng
     * @date 2021/12/20 14:32
     */
    public CfetsRepoDealQuoteCommonDto queryViewPage(String quoteId) {
        Map<String, Object> param = new HashMap<>();
        // 交易汇总信息
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = cfetsIbuQuoteRepoMapper.selectCommDtoByTotleTradeId(quoteId);
        // 对应组合交易数据
        List<RepoPtlAllocationDto> ptlAllocations = cfetsIbuQuoteRepoMapper.listRepoPtlAllocationDtos(quoteId);
        param.put("totleTradeId", quoteId);
        setParam(param, 2);
        // 质押券信息
        List<RepoEntrustPledgeDto> repoEntrustPledgeDtos = cfetsIbuQuoteRepoMapper.listRepoEntrustPledgeDtos(param);
        cfetsRepoDealQuoteCommonDto.setLoginUserId(cfetsRepoDealQuoteCommonDto.getMySideTrader());//登记 本方交易员取值
        cfetsRepoDealQuoteCommonDto.setRepoEntrustPledgeDtos(repoEntrustPledgeDtos);
        cfetsRepoDealQuoteCommonDto.setPtlAllocations(ptlAllocations);

        return cfetsRepoDealQuoteCommonDto;
    }

    /**
     * 当日登记(分页列表)
     *
     * @param dto 入参
     * @return com.joyintech.framework.grid.Page<com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDayRegDto>
     * @author jiangpeng
     * @date 2021/12/20 14:32
     */
    public Page<CfetsRepoDayRegDto> pageDayReg(RepoEntrustRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        pageParam.put("OPR_TYPE_09", DictCodeListExt3.操作类型_导出.getValue());
        // 排序处理
        if(StringUtil.isNotEmpty(dto.getPageMode())){
            if(StringUtil.isNotEmpty(dto.getPageMode().getDirection())){
                pageParam.put("direction", dto.getPageMode().getDirection());
            }
            if(StringUtil.isNotEmpty(dto.getPageMode().getSort())){
                pageParam.put("sort", dto.getPageMode().getSort());
            }
        }
        setParam(pageParam, 1);
        pageParam.put("CONFIRM_STATUS_0", com.joyintech.tams.constant.DictCodeList.确认状态_未确认.getValue());
        int pageNo = dto.getPageMode() == null ? 1 : dto.getPageMode().getPageNo();
        int pageSize = dto.getPageMode() == null ? 10 : dto.getPageMode().getPageSize();
        return super.queryForPage(pageNo, pageSize, () -> cfetsIbuQuoteRepoMapper.listDayReg(pageParam));
    }

    /**
     * 当日登记
     *
     * @param pageParam 入参
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.bond.dto.CfetsBondDayRegDto>
     * @author jiangpeng
     * @date 2021/12/14 19:25
     */
    public List<CfetsRepoDayRegDto> listDayReg(Map<String, Object> pageParam) {
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        pageParam.put("OPR_TYPE_09", DictCodeListExt3.操作类型_导出.getValue());
        setParam(pageParam, 1);
        pageParam.put("CONFIRM_STATUS_0", DictCodeList.确认状态_未确认.getValue());
        return cfetsIbuQuoteRepoMapper.listDayRegExport(pageParam);
    }

    /**
     * 当日登记 本币业务导出
     *
     * @param pageParam 入参
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.bond.dto.CfetsBondDayRegDto>
     * @author jiangpeng
     * @date 2021/12/14 19:25
     */
    public List<CfetsRepoDayRegDto> listDayRegExport(Map<String, Object> pageParam) {
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        pageParam.put("OPR_TYPE_09", DictCodeListExt3.操作类型_导出.getValue());
        setParam(pageParam, 1);
        pageParam.put("CONFIRM_STATUS_0", DictCodeList.确认状态_未确认.getValue());
        List<CfetsRepoDayRegDto> list = cfetsIbuQuoteRepoMapper.listDayRegExport(pageParam);// 查询且按业务编码排序
        List<CfetsRepoDayRegDto> resultDtos = new ArrayList<>();
        // 记录前一个dto
        CfetsRepoDayRegDto lastDto = new CfetsRepoDayRegDto();
        for (CfetsRepoDayRegDto dayRegDto : list) {
            resultDtos.add(formatExportData(dayRegDto, lastDto));
            lastDto = dayRegDto;
        }
        return resultDtos;
    }

    /**
     * 当日登记check
     *
     * @param dataList 数据列表
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author jiangpeng
     * @date 2021/12/20 14:32
     */
    public Map<String, Object> exportDayRegCheck(List<CfetsRepoDayRegDto> dataList) {
        Map<String, Object> result = new HashMap<>();
        //是否首次导出
        String message = StringUtil.EMPTY;
        List<String> quoteIds = new ArrayList<>();
        for (CfetsRepoDayRegDto item : dataList) {
            if (!StringUtil.equals(item.getValidStatus(), BussinessDictCodeList.有效状态_有效.getValue())) {
                message = StringUtil.join("非有效状态,不可导出!业务编号:" + item.getQuoteId());
                break;
            }
            if (!StringUtil.equals(item.getQuoteType(), "01")) {
                message = StringUtil.join("报价方式非对话报价,现阶段不可导出!业务编号:" + item.getQuoteId());
                break;
            }
            if (item.getStatisticalTimes() != 0) {
                quoteIds.add(item.getQuoteId());
            }
        }
        result.put("message", message);
        if (CollectionUtils.isNotEmpty(quoteIds)) {
            result.put("quoteIds", quoteIds.stream().distinct().collect(Collectors.toList()));
        }
        return result;

    }

    /**
     * 本币交易导出履历处理
     *
     * @param dataList 数据列表
     * @author jiangpeng
     * @date 2021/12/20 14:33
     */
    public void handExportTimes(List<CfetsRepoDayRegDto> dataList) {
        List<CfetsOprHisInfoRequest> result = new ArrayList<>();
        dataList.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getQuoteId())) {
                CfetsOprHisInfoRequest cfetsOprHisInfoRequest = new CfetsOprHisInfoRequest();
                cfetsOprHisInfoRequest.setEntrustId(item.getQuoteId());
                cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_导出.getValue());
                cfetsOprHisInfoRequest.setSeqNo(IDGenerator.generate(32));
                cfetsOprHisInfoRequest.setOperDate(DateUtil.platDateFromCache());
                cfetsOprHisInfoRequest.setOperTime(DateUtil.platDateFromCache());
                cfetsOprHisInfoRequest.setUserId(SecurityUtil.getLoginUser().getUserName());
                cfetsOprHisInfoRequest.setCreateUser(SecurityUtil.getLoginUser().getUserName());
                cfetsOprHisInfoRequest.setCreateDept(CommonUtil.getDeptCode());
                cfetsOprHisInfoRequest.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
                joyinbusinessCommonService.updateCfetsOprHis(cfetsOprHisInfoRequest);
            }
        });
    }

    /**
     * 组合分配(正回购)汇总计算
     *
     * @param autoAllocationRequest
     * @return com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDealQuoteCommonDto
     * @author jiangpeng
     * @date 2021/12/20 14:33
     */
    public CfetsRepoDealQuoteCommonDto ptlAllocationCal(RepoAutoAllocationRequest autoAllocationRequest) {
        CfetsRepoDealQuoteCommonDto resultDto = new CfetsRepoDealQuoteCommonDto();
        CfetsRepoDealQuoteCommonDto temp = new CfetsRepoDealQuoteCommonDto();
        if (CollectionUtils.isEmpty(autoAllocationRequest.getPtlAllocations())) {
            return resultDto;
        }
        // 交易金额
        BigDecimal tradaAmt = BigDecimal.ZERO;
        // 质押券折算总额
        BigDecimal convertAmt = BigDecimal.ZERO;
        for (RepoPtlAllocationDto ptlAllocationDto : autoAllocationRequest.getPtlAllocations()) {
            if (ptlAllocationDto.getTradeAmt() == null) {
                continue;
            }
            tradaAmt = ArithmeticUtil.doAdd(tradaAmt, ptlAllocationDto.getTradeAmt());
        }
        if (CollectionUtils.isNotEmpty(autoAllocationRequest.getRepoEntrustPledgeDtos())) {
            for (RepoEntrustPledgeDto tempDto : autoAllocationRequest.getRepoEntrustPledgeDtos()) {
                if (StringUtil.isNotEmpty(tempDto.getPledgePrinStr())) {
                    tempDto.setPledgePrin(ArithmeticUtil.multiply(new BigDecimal(tempDto.getPledgePrinStr()), new BigDecimal(10000)));
                }
                tempDto.setConvertAmt(ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(tempDto.getPledgeRatio(), tempDto.getPledgePrin()), new BigDecimal(100), 4));
                convertAmt = ArithmeticUtil.doAdd(tempDto.getConvertAmt(), convertAmt);
            }
        }
        autoAllocationRequest.setTradeAmt(tradaAmt);
        // 交易金额变动 重新计算
        resultDto = this.tradeAmtChange(autoAllocationRequest);
        // 质押券折算汇总
        resultDto.setConvertAmt(convertAmt);
        // 交易金额赋值
        resultDto.setTradeAmt(tradaAmt);

        return resultDto;
    }

    /**
     * 交易金额变动联动计算
     * 1.质押式回购计算费用 根据[交易方向][交易金额][证券账户][期限品种]计算 [交易手续费][结算手续费]
     * 2.金额信息计算 根据[清算速度][交易金额][回购利率][回购期限]计算[首次结算日][到期结算日][实际占款天数][回购利息][到期结算金额]
     * 3.自动分配 根据[分配模式][组合分配信息][质押券信息]计算 组合分配中[交易金额][回购利息][到期结算金额][交易手续费]
     *
     * @param
     * @return com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDealQuoteCommonDto
     * @author jiangpeng
     * @date 2021/12/20 14:33
     */
    public CfetsRepoDealQuoteCommonDto tradeAmtChange(RepoAutoAllocationRequest request) {
        CfetsRepoDealQuoteCommonDto resultDto = new CfetsRepoDealQuoteCommonDto();
        // copy赋值
        BeanUtil.copyPropertiesIgnoreNull(resultDto, request);
        CfetsRepoDealQuoteCommonDto temp = new CfetsRepoDealQuoteCommonDto();
        if (CollectionUtils.isEmpty(request.getPtlAllocations())) {
            return resultDto;
        }
        // 计算费用
        RepoCalRequest calRequest = new RepoCalRequest();
        calRequest.setTradeAmt(request.getTradeAmt());//交易金额
        calRequest.setPs(request.getPs());//交易方向
        calRequest.setSecManageAcctId(request.getSecManageAcctId());//证券账户
        calRequest.setTermType(request.getTermType());//期限品种
        calRequest.setPledgeNum(request.getRepoEntrustPledgeDtos().size());//质押券数量
        calRequest.setTradeDate(iPlatformDate.getSysDate());//交易日期
        temp = this.calFee(calRequest);//计算费用
        // 交易手续费
        resultDto.setDealFeeAmt(temp.getDealFeeAmt());
        // 结算手续费
        resultDto.setSettFeeAmt(temp.getSettFeeAmt());
        // 计算金额信息
        calRequest.setPrice(request.getPrice());//回购利率
        calRequest.setSettleSpreedCode(request.getSettleSpreedCode());//清算速度
        calRequest.setRepoTerm(request.getRepoTerm());//回购期限
        temp = this.calAmtInfo(calRequest);
        // 首次结算日：平台日+清算速度，根据银行间节假日规则遇节假日顺延至下一个工作日
        resultDto.setSettlementStartDate(temp.getSettlementStartDate());
        // 到期结算日=首次结算日+回购期限，遇银行间节假日调整到下一工作日
        resultDto.setSettlementEndDate(temp.getSettlementEndDate());
        // 实际占款天数=到期结算日-首次结算日
        resultDto.setActCapDays(temp.getActCapDays());
        // 回购利息 本金金额*回购利率*实际占款天数/计息基础(365)
        resultDto.setmIntAmt(temp.getmIntAmt());
        // 到期结算金额 本金金额+回购利息
        resultDto.setMtradeAmt(temp.getMtradeAmt());
        // 组合分配
        request.setmIntAmt(resultDto.getmIntAmt());//回购利息
        request.setMtradeAmt(resultDto.getMtradeAmt());//到期结算金额
        request.setDistribution(resultDto.getDistribution());//分配方式
        request.setDealFeeAmt(resultDto.getDealFeeAmt());// 交易手续费
        request.setSettFeeAmt(resultDto.getSettFeeAmt());// 结算手续费
        request.setConvertAmt(null);
        temp = this.autoAllocation(request);
        // 组合分配数据处理
        resultDto.setPtlAllocations(temp.getPtlAllocations());

        return resultDto;
    }

    /**
     * 组合分配(正回购) 根据质押券计算组合分配中交易金额
     *
     * @param autoAllocationRequest 质押券信息 组合分配信息
     * @return com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDealQuoteCommonDto
     * @author jiangpeng
     * @date 2021/12/20 14:34
     */
    public CfetsRepoDealQuoteCommonDto pledgesAddPtl(RepoAutoAllocationRequest autoAllocationRequest) {
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = new CfetsRepoDealQuoteCommonDto();
        // 组合分配信息
        if (CollectionUtils.isEmpty(autoAllocationRequest.getPtlAllocations())) {
            return cfetsRepoDealQuoteCommonDto;
        }
        // 质押债券信息
        if (CollectionUtils.isEmpty(autoAllocationRequest.getRepoEntrustPledgeDtos())) {
            return cfetsRepoDealQuoteCommonDto;
        }
        // 质押券折算总额 按组合维度汇总
        Map<String, BigDecimal> pledgeConvertAmtMap = new HashMap<>();
        autoAllocationRequest.getRepoEntrustPledgeDtos().forEach(item -> {
            if (StringUtil.isNotEmpty(item.getPledgePrinStr())) {
                item.setPledgePrin(ArithmeticUtil.multiply(new BigDecimal(item.getPledgePrinStr()), new BigDecimal(10000)));
            }
            // 质押券折算总额 获取
            BigDecimal convertAmt = pledgeConvertAmtMap.get(item.getPortfolioId());
            // 计算该笔质押券信息的质押券折算总额
            BigDecimal pledgeRatio = StringUtil.isNotEmpty(item.getPledgeRatio())
                    ? ArithmeticUtil.divide(item.getPledgeRatio(), new BigDecimal(100), 6) : BigDecimal.ONE;
            item.setConvertAmt(ArithmeticUtil.multiply(item.getPledgePrin(), pledgeRatio));
            // 获取到组合的 质押券折算总额 进行累加,获取不到,则赋予初始值
            if (StringUtil.isNotEmpty(convertAmt)) {
                pledgeConvertAmtMap.put(item.getPortfolioId(), ArithmeticUtil.doAdd(convertAmt, item.getConvertAmt()));
            } else {
                pledgeConvertAmtMap.put(item.getPortfolioId(), item.getConvertAmt());
            }
        });
        // 组合分配 按组合维度汇总
        Map<String, List<RepoPtlAllocationDto>> ptlListMap = new HashMap<>();
        // 指令的剩余指令额度 按组合维度汇总
        Map<String, BigDecimal> avaAmtMap = new HashMap<>();
        autoAllocationRequest.getPtlAllocations().forEach(ptlAllocationDto -> {
            // 获取组合对应的指令数据
            List<RepoPtlAllocationDto> ptlList = ptlListMap.get(ptlAllocationDto.getPortfolioId());
            // 为空则初始化
            if (CollectionUtils.isEmpty(ptlList)) {
                ptlList = new ArrayList<>();
            }
            ptlAllocationDto.setConvertAmt(null);
            // 添加该笔组合分配数据
            ptlList.add(ptlAllocationDto);
            ptlListMap.put(ptlAllocationDto.getPortfolioId(), ptlList);
            // 指令的剩余指令额度 获取
            BigDecimal avaAmt = avaAmtMap.get(ptlAllocationDto.getPortfolioId());
            // 获取到组合的 指令的剩余指令额度 进行累加,获取不到,则赋予初始值
            if (StringUtil.isNotEmpty(avaAmt)) {
                avaAmtMap.put(ptlAllocationDto.getPortfolioId(), ArithmeticUtil.doAdd(avaAmt, ptlAllocationDto.getAvaNum()));
            } else {
                avaAmtMap.put(ptlAllocationDto.getPortfolioId(), ptlAllocationDto.getAvaNum());
            }
        });
        List<RepoPtlAllocationDto> repoPtlAllocationDtos = new ArrayList<>();
        BigDecimal tradeAmt = BigDecimal.ZERO;
        //计算交易金额 尾差用最小变动单位序列分配算法
        for (Map.Entry<String, List<RepoPtlAllocationDto>> map : ptlListMap.entrySet()) {
            // 按组合维度获取组合分配信息列表
            List<RepoPtlAllocationDto> ptlList = map.getValue();
            // 获取组合对应的 质押券折算总额 汇总值
            BigDecimal convertAmtSum = pledgeConvertAmtMap.get(map.getKey());
            // 获取组合对应的 指令的剩余指令额度 汇总值
            BigDecimal avaAmtSum = avaAmtMap.get(map.getKey());
            // 没有汇总值,则该组合没有对应的质押券
            if (StringUtil.isNotEmpty(convertAmtSum) && StringUtil.isNotEmpty(avaAmtSum)) {
                // 按剩余指令额度比例拆分质押券折算总额
                handleTailDifference(ptlList, convertAmtSum, 5);
            }
            // 遍历组合分配信息列表
//            for (RepoPtlAllocationDto ptlAllocationDto : ptlList) {
//                ptlAllocationDto.setConvertAmt(convertAmtSum);
//                // 按剩余指令额度比例拆分质押券折算总额 计入交易金额
//                ptlAllocationDto.setTradeAmt(ArithmeticUtil.divide(
//                        ArithmeticUtil.multiply(ptlAllocationDto.getAvaNum(), convertAmtSum), avaAmtSum, 4, RoundingMode.HALF_UP));
//            }
            for (RepoPtlAllocationDto item : ptlList) {
                item.setTradeAmt(item.getConvertAmt());
                if (item.getTradeAmt() != null && item.getAvaNum() != null
                        && item.getTradeAmt().compareTo(item.getAvaNum()) > 0) {
                    item.setTradeAmt(item.getAvaNum());
                }
                tradeAmt = ArithmeticUtil.doAdd(tradeAmt, item.getTradeAmt());
                repoPtlAllocationDtos.add(item);
            }
        }
        // 交易金额汇总
        cfetsRepoDealQuoteCommonDto.setTradeAmt(tradeAmt);
        // 质押券数据不做变动
        cfetsRepoDealQuoteCommonDto.setRepoEntrustPledgeDtos(autoAllocationRequest.getRepoEntrustPledgeDtos());
        // 组合分配数据处理
        cfetsRepoDealQuoteCommonDto.setPtlAllocations(repoPtlAllocationDtos);
        return cfetsRepoDealQuoteCommonDto;
    }

    /**
     * 组合分配增删,质押券增删改时,联动计算
     * 1.正回购 删除组合 联动计算质押券的信息
     * 2.正回购 联动计算 根据质押券计算组合分配中交易金额
     * 3.质押式回购计算费用 根据[交易方向][交易金额][证券账户][期限品种]计算 [交易手续费][结算手续费]
     * 4.金额信息计算 根据[清算速度][交易金额][回购利率][回购期限]计算[首次结算日][到期结算日][实际占款天数][回购利息][到期结算金额]
     * 5.自动分配 根据[分配模式][组合分配信息][质押券信息]计算 组合分配中[交易金额][回购利息][到期结算金额][交易手续费]
     *
     * @param request 组合分配信息 质押券信息 交易方向 期限品种 回购利率 清算速度 回购期限
     * @return com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDealQuoteCommonDto
     * @author jiangpeng
     * @date 2021/12/20 10:55
     */
    public CfetsRepoDealQuoteCommonDto ptlAndPledgeEdit(RepoAutoAllocationRequest request) {
        CfetsRepoDealQuoteCommonDto resultDto = new CfetsRepoDealQuoteCommonDto();
        CfetsRepoDealQuoteCommonDto temp = new CfetsRepoDealQuoteCommonDto();
        // copy赋值
        BeanUtil.copyPropertiesIgnoreNull(resultDto, request);
        if (CollectionUtils.isEmpty(request.getRepoEntrustPledgeDtos())
                && StringUtil.equals(request.getPs(), DictCodeListExt3.交易方向_正回购.getValue())) {
            return resultDto;
        }
        if (CollectionUtils.isEmpty(request.getPtlAllocations())
                && StringUtil.equals(request.getPs(), DictCodeListExt3.交易方向_逆回购.getValue())) {
            return resultDto;
        }
        BigDecimal convertAmt = BigDecimal.ZERO;
        BigDecimal shareAmt = BigDecimal.ZERO;
        if (StringUtil.equals(request.getPs(), DictCodeListExt3.交易方向_正回购.getValue()) &&
                StringUtil.isNotEmpty(request.getDelEtrId()) && CollectionUtils.isNotEmpty(request.getEntrustIdList())) {
            // 删除组合分配中数据
            resultDto.setRepoEntrustPledgeDtos(this.ptlAllocationDel(request));
            request.setRepoEntrustPledgeDtos(resultDto.getRepoEntrustPledgeDtos());
        }
        if (StringUtil.equals(request.getPs(), DictCodeListExt3.交易方向_正回购.getValue())) {
            // 组合分配(正回购)添加质押券后联动 计算组合分配中交易金额,并汇总基本信息中交易金额
            temp = this.pledgesAddPtl(request);
            // 组合分配数据处理
            resultDto.setPtlAllocations(temp.getPtlAllocations());
            // 组合分配数据处理
            request.setPtlAllocations(resultDto.getPtlAllocations());
        }
        if (StringUtil.equals(request.getPs(), DictCodeListExt3.交易方向_正回购.getValue())
                && !StringUtil.equals("dialogueResponse", request.getQuotaPageModel())) {
            // 交易金额汇总
            resultDto.setTradeAmt(temp.getTradeAmt());
            // 交易金额汇总
            request.setTradeAmt(resultDto.getTradeAmt());
        }
        if (CollectionUtils.isNotEmpty(request.getRepoEntrustPledgeDtos())
                && !StringUtil.equals("dialogueResponse", request.getQuotaPageModel())) {
            for (RepoEntrustPledgeDto tempDto : request.getRepoEntrustPledgeDtos()) {
                if (StringUtil.isNotEmpty(tempDto.getPledgePrinStr())) {
                    tempDto.setPledgePrin(ArithmeticUtil.multiply(new BigDecimal(tempDto.getPledgePrinStr()), new BigDecimal(10000)));
                }
                BigDecimal pledgeRatio = StringUtil.isNotEmpty(tempDto.getPledgeRatio())
                        ? ArithmeticUtil.divide(tempDto.getPledgeRatio(), new BigDecimal(100), 6) : BigDecimal.ONE;
                tempDto.setConvertAmt(ArithmeticUtil.multiply(pledgeRatio, tempDto.getPledgePrin()));
                convertAmt = ArithmeticUtil.doAdd(tempDto.getConvertAmt(), convertAmt);
                shareAmt = ArithmeticUtil.doAdd(tempDto.getPledgePrin(), shareAmt);
            }
        }
        // 质押券数据不做变动
        resultDto.setRepoEntrustPledgeDtos(request.getRepoEntrustPledgeDtos());
        // 计算费用
        RepoCalRequest calRequest = new RepoCalRequest();
        calRequest.setTradeAmt(request.getTradeAmt());//交易金额
        calRequest.setPs(request.getPs());//交易方向
        calRequest.setSecManageAcctId(request.getSecManageAcctId());//证券账户
        calRequest.setTermType(request.getTermType());//期限品种
        calRequest.setPledgeNum(request.getRepoEntrustPledgeDtos().size());//质押券数量
        calRequest.setTradeDate(iPlatformDate.getSysDate());//交易日期
        temp = this.calFee(calRequest);//计算费用
        // 交易手续费
        resultDto.setDealFeeAmt(temp.getDealFeeAmt());
        // 结算手续费
        resultDto.setSettFeeAmt(temp.getSettFeeAmt());
        // 计算金额信息
        calRequest.setPrice(request.getPrice());//回购利率
        calRequest.setSettleSpreedCode(request.getSettleSpreedCode());//清算速度
        calRequest.setRepoTerm(request.getRepoTerm());//回购期限
        temp = this.calAmtInfo(calRequest);
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = new CfetsRepoDealQuoteCommonDto();
        // 首次结算日：平台日+清算速度，根据银行间节假日规则遇节假日顺延至下一个工作日
        resultDto.setSettlementStartDate(temp.getSettlementStartDate());
        // 到期结算日=首次结算日+回购期限，遇银行间节假日调整到下一工作日
        resultDto.setSettlementEndDate(temp.getSettlementEndDate());
        // 实际占款天数=到期结算日-首次结算日
        resultDto.setActCapDays(temp.getActCapDays());
        // 回购利息 本金金额*回购利率*实际占款天数/计息基础(365)
        resultDto.setmIntAmt(temp.getmIntAmt());
        // 到期结算金额 本金金额+回购利息
        resultDto.setMtradeAmt(temp.getMtradeAmt());
        if (!StringUtil.equals("dialogueResponse", request.getQuotaPageModel())) {
            // 质押券折算总额
            resultDto.setConvertAmt(convertAmt);
            // 质押券面总额合计
            resultDto.setShareAmt(shareAmt);
        }
        // 组合分配
        request.setTradeAmt(resultDto.getTradeAmt());//交易金额
        request.setmIntAmt(resultDto.getmIntAmt());//回购利息
        request.setMtradeAmt(resultDto.getMtradeAmt());//到期结算金额
        request.setDistribution(resultDto.getDistribution());//分配方式
        request.setRepoEntrustPledgeDtos(resultDto.getRepoEntrustPledgeDtos());//质押券信息
        request.setPtlAllocations(resultDto.getPtlAllocations());//组合分配信息
        request.setDealFeeAmt(resultDto.getDealFeeAmt());// 交易手续费
        request.setSettFeeAmt(resultDto.getSettFeeAmt());// 结算手续费
        request.setShareAmt(resultDto.getShareAmt());
        request.setConvertAmt(resultDto.getConvertAmt());
        temp = this.autoAllocation(request);
        // 组合分配数据处理
        resultDto.setPtlAllocations(temp.getPtlAllocations());

        return resultDto;
    }

    /**
     * 质押券保存校验
     *
     * @param request
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2021/12/20 18:51
     */
    public AjaxResult doSavePledgesCheck(RepoAutoAllocationRequest request) {
        AjaxResult result = new AjaxResult();
        if (CollectionUtils.isEmpty(request.getRepoEntrustPledgeDtos())) {
            return result;
        }
        List<String> finProdIds = new ArrayList<>();
        String repRegistOrg = request.getRepoEntrustPledgeDtos().get(0).getRepRegistOrg();
        for (RepoEntrustPledgeDto item : request.getRepoEntrustPledgeDtos()) {
            finProdIds.add(item.getFinprodId());
            if (!StringUtil.equals(item.getRepRegistOrg(), repRegistOrg)) {
                result.setMessage("托管机构不一致，不能添加！", MessageType.错误消息);
                return result;
            }
        }
        Map<String, Object> param = new HashMap<>();
        param.put("finProdIds", finProdIds);
        param.put("platDate", iPlatformDate.getSysDateStr());
        if (StringUtil.equals(request.getPs(), DictCodeListExt3.交易方向_正回购.getValue())) {
            List<FinExercisePlan> finExercisePlans = cfetsIbuQuoteRepoMapper.findFinExercisePlan(param);
            if (CollectionUtils.isEmpty(finExercisePlans)) {
                return result;
            }
            for (FinExercisePlan finExercisePlan : finExercisePlans) {
                for (RepoEntrustPledgeDto repoEntrustPledgeDto : request.getRepoEntrustPledgeDtos()) {
                    if (StringUtil.isNotEmpty(request.getSettlementEndDate())
                            && DateUtil.compareTo(request.getSettlementEndDate(), repoEntrustPledgeDto.getMdate()) < 0) {
                        result.setMessage("所选质押券" + repoEntrustPledgeDto.getBondCode() +
                                repoEntrustPledgeDto.getBondAbbrName() + "到期日为" + repoEntrustPledgeDto.getMdate() + "，不可小于等于回购到期结算日", MessageType.错误消息);
                        return result;
                    }
                    if (DateUtil.compareTo(finExercisePlan.getExerciseDate(), request.getSettlementEndDate()) < 0) {
                        result.setMessage("债券" + repoEntrustPledgeDto.getBondCode() +
                                repoEntrustPledgeDto.getBondAbbrName() + "已做赎回登记，是否确认继续质押", MessageType.确认消息);
                    } else {
                        result.setMessage("债券" + repoEntrustPledgeDto.getBondCode() +
                                repoEntrustPledgeDto.getBondAbbrName() + "已赎回登记，且到期结算日大于等于理论行权日，不可质押", MessageType.错误消息);
                    }
                    return result;
                }
            }
        }
        return result;
    }

    /*****************************************************************私有方法************************************************************************/

    /**
     * 入参非空校验
     *
     * @param requestDto 质押式回购头寸信息查询请求参数
     * @author jiangpeng
     * @date 2021/12/3 17:14
     */
    private void posiInfoCheckCondition(CfetsIbuQuoteRepoPosiInfoRequestDto requestDto) {
        if (requestDto == null) {
            throw new RuntimeException("入参不能为空！");
        }

        if (StringUtil.isEmpty(requestDto.getCdate())) {
            requestDto.setCdate(iPlatformDate.getSysDateStr());
        }
    }

    /**
     * 头寸空值展示
     *
     * @return java.util.List<com.joyintech.tams.business.common.pojo.BaseMap>
     * @author jiangpeng
     * @date 2021/12/3 17:20
     */
    private List<BaseMap> nullData() {
        List<BaseMap> resp = new ArrayList<>();

        /*T日可用现金*/
        resp.add(BaseMap.newMap()
                .put("field", "T0KYXJ")
                .put("name", "T日可用现金（元）")
                .put("amount", "--")
                .put("type", "xj"));
        /*T+1日可用现金*/
        resp.add(BaseMap.newMap()
                .put("field", "T1KYXJ")
                .put("name", "T+1日可用现金（元）")
                .put("amount", "--")
                .put("type", "xj"));
        /*账户现金*/
        resp.add(BaseMap.newMap()
                .put("field", "ZHXJ")
                .put("name", "账户现金（元）")
                .put("amount", "--")
                .put("type", "xj"));
        return resp;
    }

    /**
     * 数据组装
     *
     * @param map      头寸信息
     * @param cashT    t日可用
     * @param cashT1   t1日可用
     * @param realCash 账户现金
     * @return java.util.List<com.joyintech.tams.business.common.pojo.BaseMap>
     * @author jiangpeng
     * @date 2021/12/3 17:59
     */
    private List<BaseMap> assemble(Map map, BigDecimal cashT, BigDecimal cashT1, BigDecimal realCash) {
        String T0KYXJ = "T0KYXJ", T1KYXJ = "T1KYXJ", ZHXJ = "ZHXJ";
        if (map == null) {
            map = new HashMap();
            map.put(T0KYXJ, "0");
            map.put(T1KYXJ, "0");
            map.put(ZHXJ, "0");
        }
        // 初始化返回集合
        List<BaseMap> resp = new ArrayList<>();
        BaseMap bm = BaseMap.newMap(map);
        /**
         * 若老投管现金存在需累计
         */
        if (cashT != null) {
            bm.put(T0KYXJ, addToStr(bm.getBigDecimal(T0KYXJ), cashT));
        }
        if (cashT1 != null) {
            bm.put(T1KYXJ, addToStr(bm.getBigDecimal(T1KYXJ), cashT1));
        }
        if (realCash != null) {
            bm.put(ZHXJ, addToStr(bm.getBigDecimal(ZHXJ), realCash));
        }

        /*T日可用现金*/
        resp.add(BaseMap.newMap()
                .put("field", T0KYXJ)
                .put("name", "T日可用现金（元）")
                .put("amount", bm.getBigDecimalStr(T0KYXJ, "0.00"))
                .put("type", "xj"));
        /*T+1日可用现金*/
        resp.add(BaseMap.newMap()
                .put("field", T1KYXJ)
                .put("name", "T+1日可用现金（元）")
                .put("amount", bm.getBigDecimalStr(T1KYXJ, "0.00"))
                .put("type", "xj"));
        /*账户现金*/
        resp.add(BaseMap.newMap()
                .put("field", ZHXJ)
                .put("name", "账户现金（元）")
                .put("amount", bm.getBigDecimalStr(ZHXJ, "0.00"))
                .put("type", "xj"));

        return resp;
    }

    /**
     * 获取两数相加的值,返回前端格式保留小数位
     *
     * @param a 数值
     * @param b 数值
     * @return java.lang.String
     * @author jiangpeng
     * @date 2021/12/3 17:57
     */
    private String addToStr(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) {
            return "--";
        }
        return decimalToStr(ArithmeticUtil.doAdd(a, b), "0.00");
    }

    /**
     * 返回前端格式保留小数位
     *
     * @param b       数值
     * @param pattern 保留格式 0.00
     * @return 转化后的数值字符串
     */
    private String decimalToStr(BigDecimal b, String pattern) {
        if (b == null) {
            return "--";
        }
        DecimalFormat df1 = new DecimalFormat(pattern);
        return df1.format(b);
    }

    /**
     * 分页查询 参数赋值
     *
     * @param param 查询条件集合初始化
     * @param type  初始化类型: 1 指令列表查询条件初始化 2 质押券信息查询条件初始化
     * @author jiangpeng
     * @date 2021/12/4 16:17
     */
    private void setParam(Map<String, Object> param, int type) {
        String loginUserId = LoginUserUtil.getUserName();
        if ("admin".equals(loginUserId)) {
//            loginUserId = "jp";//TODO 测试写死登录用户 提交代码时修改
        }
        param.put("platDate", iPlatformDate.getSysDateStr());
        param.put("loginUserId", loginUserId);
        param.put("rolePos03", DictCodeListExt2.角色岗位_投资处长.getValue());
        //银行间质押式回购质押券模式：01无质押券库，02单个质押库，03多个质押券库
        param.put("repo_pledge_model", CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL));
        if (type == 1) {
            param.put("finprodType2_F02", BussinessDictCodeList.金融产品类型_债券质押式正回购.getValue());
            param.put("finprodType2_F03", BussinessDictCodeList.金融产品类型_债券质押式逆回购.getValue());
            param.put("tradeMarket01", BussinessDictCodeList.交易场所_银行间.getValue());
            param.put("tradeMarket02", BussinessDictCodeList.交易场所_商业银行柜台市场.getValue());
            param.put("entrustStatusUnAccept", BussinessDictCodeList.委托状态_待接收.getValue());
            param.put("entrustStatusAccept", BussinessDictCodeList.委托状态_已接收.getValue());
            param.put("entrustStatusComple", BussinessDictCodeList.委托状态_已完成.getValue());
            param.put("entrustStatusReturn", BussinessDictCodeList.委托状态_已退回.getValue());
            param.put("secAcctSource", BussinessDictCodeList.账户来源_自营.getValue());
            param.put("ps_03", DictCodeListExt3.交易方向_正回购.getValue());
            param.put("ps_04", DictCodeListExt3.交易方向_逆回购.getValue());
        } else if (type == 2) {
            param.put("POSITION_TYPE_01", DictCodeList.持仓类型_可用.getValue());
            param.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());
            param.put("POSITION_TYPE_04", DictCodeList.持仓类型_质押.getValue());
            param.put("POSITION_TYPE_05", DictCodeList.持仓类型_融入.getValue());
            param.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
            param.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
            param.put("A_001", DictCodeListExt3.资产类型_资产账户持仓.getValue());
            param.put("A_012", DictCodeListExt3.资产类型_质押式双边回购本方券已质押持仓.getValue());
            param.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
        } else {
            param.put("A_001", DictCodeListExt3.资产类型_资产账户持仓.getValue());
            param.put("A_012", DictCodeListExt3.资产类型_质押式双边回购本方券已质押持仓.getValue());
            param.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
            param.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
            param.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            param.put("TRADEPLACE01", DictCodeListExt1.交易场所_银行间.getValue());
            param.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
            param.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
            param.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
            param.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        }
    }

    /**
     * 传入指令列表信息,校验 委托状态,交易场所+交易方向+清算速度+交易对手+证券账户+交易账户必须一致,价格判断
     *
     * @param entrustDtos 数据列表
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2021/12/7 9:11
     */
    private AjaxResult preconditionJudgment(List<CfetsRepoDealEntrustOrderDto> entrustDtos, String quoteType) {
        // 第一条数据
        CfetsRepoDealEntrustOrderDto one = entrustDtos.get(0);
        // 返回信息
        String message = StringUtil.EMPTY;
        // 所有限价指令的回购利率中的最小值或最大值
        BigDecimal minOrMaxYield = null;
        // 指定价格的回购利率
        BigDecimal yieldPRM01 = null;
        // 交易方向
        String ps = one.getPs();
        // 参数：本币交易导出按钮配置是否显示  01 表示显示  02表示不显示
        String exportShow = CacheUtil.getSysParam("EXPORT_SHOW");
        //系统是否支持报价业务 Y:支持,N:不支持
        // 设置为支持时，【交易类型】默认选中对话
        // 设置为不支持时，【交易类型】默认选中登记
        String is_sys_support_quote = CacheUtil.getSysParam("is_sys_support_quote");
        boolean isReg = false;
        if (StringUtil.isNotEmpty(quoteType)
                && StringUtil.equals(is_sys_support_quote, DictCodeList.是否_否.getValue())) {
            isReg = true;
        }
        if (StringUtil.equals(quoteType, BussinessDictCodeList.报价方式_登记报价.getValue())) {//登记
            isReg = true;
        }
        // 返回结果集
        AjaxResult ajaxResult = new AjaxResult();
        // 回购期限判断标识
        boolean repoTermFlag = true;
        // 交易方向判断标识
        boolean psFlag = true;
        // 清算速度判断标识
        boolean settleSpreedFlag = true;
        // 交易对手判断标识
        boolean counterIdFlag = true;
        // 证券账户判断标识
        boolean secManageAcctIdFlag = true;
        // 交易账户判断标识
        boolean partyIdFlag = true;
        // 交易对手只判断勾选指令中交易对手字段存在数据的是否一致
        // 交易对手
        String counterId = one.getCounterId();
        // 多条指定价格指令，回购利率一致标识
        boolean yieldFlag = true;
        for (CfetsRepoDealEntrustOrderDto item : entrustDtos) {
            // 委托状态校验
            if (!StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已接收.getValue())) {
                message = "指令" + item.getEntrustId() + "委托状态不是已接收,不可交易执行!";
                ajaxResult.setMessage(message);
                return ajaxResult;
            }
            // 交易账户校验
            // 当参数：本币交易导出按钮配置为显示 OR (系统是否支持报价业务设置为支持时 and 本币交易导出按钮配置为不显示时)
            if (StringUtil.isEmpty(item.getPartyId()) && (StringUtil.equals(exportShow, "01") ||
                    (!isReg && StringUtil.equals(exportShow, "02")))) {
                message = "指令【" + item.getEntrustId() + "】下组合未关联交易账户，请至[银行间业务参数管理-CFETS关联账户]中维护";
                ajaxResult.setMessage(message);
                return ajaxResult;
            }
            // 当参数：系统是否支持报价业务设置为支持时 and 本币交易导出按钮配置为不显示时
            if (!isReg && StringUtil.equals(exportShow, "02")
                    && !StringUtil.equals(one.getPartyId(), item.getPartyId())) {
                message = "所选指令交易账户不同";
                ajaxResult.setMessage(message);
                return ajaxResult;
            }
            if (repoTermFlag && one.getRepoTerm() != item.getRepoTerm()) {
                repoTermFlag = false;
                message = msgSplicing(message, "OR");
                message = message + "回购期限";
            }
            if (psFlag && !StringUtil.equals(one.getPs(), item.getPs())) {
                psFlag = false;
                message = msgSplicing(message, "OR");
                message = message + "交易方向";
            }
            if (settleSpreedFlag && !StringUtil.equals(one.getSettleSpreed(), item.getSettleSpreed())) {
                settleSpreedFlag = false;
                message = msgSplicing(message, "OR");
                message = message + "清算速度";
            }
            if (counterIdFlag && StringUtil.isNotEmpty(counterId) && StringUtil.isNotEmpty(item.getCounterId())
                    && !StringUtil.equals(one.getCounterId(), item.getCounterId())) {
                counterIdFlag = false;
                message = msgSplicing(message, "OR");
                message = message + "交易对手";
            }
            if (StringUtil.isEmpty(counterId)) {
                counterId = item.getCounterId();
            }
            if (secManageAcctIdFlag && !StringUtil.equals(one.getSecManageAcctIdZZ(), item.getSecManageAcctIdZZ())
                    && !StringUtil.equals(one.getSecManageAcctIdSQ(), item.getSecManageAcctIdSQ())) {
                secManageAcctIdFlag = false;
                message = msgSplicing(message, "OR");
                message = message + "证券账户";
            }
            // EXPORT_SHOW ==01 本币交易导出按钮配置为显示
            if (StringUtil.equals(exportShow, "01")) {
                // 交易场所+交易方向+清算速度+交易对手+证券账户+交易账户必须一致
                if (partyIdFlag && !StringUtil.equals(one.getPartyId(), item.getPartyId())) {
                    partyIdFlag = false;
                    message = msgSplicing(message, "OR");
                    message = message + "交易账户";
                }
            }
            /*********************************************价格判断 start*************************************************/
            //所有数据以保留4位小数，第5位四舍五入的值进行判断
            // 所有限价指令的回购利率中的最小值 初始化
            if (minOrMaxYield == null && StringUtil.equals(item.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue())) {
                minOrMaxYield = ArithmeticUtil.round(item.getTradeYield(), yieldScale, RoundingMode.HALF_UP);
            }
            // 交易方式为正回购时,查找限价指令的回购利率中最小值
            if (minOrMaxYield != null && StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购.getValue())
                    && StringUtil.equals(item.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue())) {
                minOrMaxYield = minOrMaxYield.compareTo(ArithmeticUtil.round(item.getTradeYield(), yieldScale, RoundingMode.HALF_UP)) <= 0
                        ? minOrMaxYield : ArithmeticUtil.round(item.getTradeYield(), yieldScale, RoundingMode.HALF_UP);
            }
            // 易方式为逆回购时,查找限价指令的回购利率中最大值
            else if (minOrMaxYield != null && StringUtil.equals(ps, DictCodeListExt3.交易方向_逆回购.getValue())
                    && StringUtil.equals(item.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue())) {
                minOrMaxYield = minOrMaxYield.compareTo(ArithmeticUtil.round(item.getTradeYield(), 4, RoundingMode.HALF_UP)) >= 0
                        ? minOrMaxYield : ArithmeticUtil.round(item.getTradeYield(), yieldScale, RoundingMode.HALF_UP);
            }
            // 指定价格回购利率 初始化
            if (yieldPRM01 == null && StringUtil.equals(item.getPriceModel(), DictCodeListExt2.价格模式_指定价格.getValue())) {
                yieldPRM01 = ArithmeticUtil.round(item.getTradeYield(), yieldScale, RoundingMode.HALF_UP);
            }
            if (yieldPRM01 != null && yieldPRM01.compareTo(ArithmeticUtil.round(item.getTradeYield(), yieldScale, RoundingMode.HALF_UP)) != 0
                    && StringUtil.equals(item.getPriceModel(), DictCodeListExt2.价格模式_指定价格.getValue())) {
                yieldFlag = false;
            }
            /*********************************************价格判断 end*************************************************/
        }
        // 交易方向+清算速度+回购期限+交易对手+证券账户+交易账户 不一致时提示
        if (StringUtil.isNotEmpty(message)) {
            message = message + "】";
            ajaxResult.setMessage("所选指令不满足汇总条件：" + message + "不同");
            return ajaxResult;
        }
        // 指定价格的回购利率必须一致
        if (!yieldFlag) {
            ajaxResult.setMessage("存在多条指定价格指令，回购利率需一致");
            return ajaxResult;
        }
        // 交易方式为正回购时,指定价格的回购利率需小于等于所有限价指令的回购利率中的最小值
        if (yieldPRM01 != null && minOrMaxYield != null && yieldPRM01.compareTo(minOrMaxYield) > 0
                && StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购.getValue())) {
            ajaxResult.setMessage("同时存在指定价格指令和限价指令，指定价格指令不能大于限价指令中回购利率的最小值");
            return ajaxResult;
        }
        // 交易方式为逆回购时，指定价格的回购利率需大于等于所有限价指令的回购利率中的最大值
        if (yieldPRM01 != null && minOrMaxYield != null && yieldPRM01.compareTo(minOrMaxYield) < 0
                && StringUtil.equals(ps, DictCodeListExt3.交易方向_逆回购.getValue())) {
            ajaxResult.setMessage("同时存在指定价格和限价指令，指定价格指令不能小于限价指令中回购利率的最大值");
            return ajaxResult;
        }
        // 勾选指令的价格模式只存在指定价格或同时存在指定价格和限价指令时,返回指定价格指令中的回购利率
        if (yieldPRM01 != null) {
            ajaxResult.setData(yieldPRM01);
        }
        //勾选指令的价格模式只存在限价指令时,逆回购 限价指令中回购利率的最大值 正回购 限价指令中回购利率的最小值
        else if (minOrMaxYield != null) {
            ajaxResult.setData(minOrMaxYield);
        }

        return ajaxResult;
    }

    /**
     * 消息字符串拼接
     * 判断传入的字符串是否为空,非空则添加连接符
     *
     * @param message 消息字符串
     * @param joiner  连接符
     * @author jiangpeng
     * @date 2021/12/7 9:19
     */
    private String msgSplicing(String message, String joiner) {
        String messageTemp = message;
        if (StringUtil.isEmpty(message)) {
            message = "【" + message;
        }
        if (StringUtil.isNotEmpty(messageTemp) && StringUtil.isNotEmpty(joiner)) {
            message = message + joiner;
        }
        return message;
    }

    /**
     * 组装报价初始化信息
     *
     * @param entrustDtos           指令信息列表
     * @param repoEntrustPledgeDtos 质押券信息列表
     * @return com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDealQuoteCommonDto
     * @author jiangpeng
     * @date 2021/12/7 14:33
     */
    private CfetsRepoDealQuoteCommonDto buildEntrustInfo(List<CfetsRepoDealEntrustOrderDto> entrustDtos, List<RepoEntrustPledgeDto> repoEntrustPledgeDtos) {
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = new CfetsRepoDealQuoteCommonDto();
        CfetsRepoDealEntrustOrderDto cfetsRepoDealEntrustOrderDto = entrustDtos.get(0);
        // copy赋值
        BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuoteCommonDto, cfetsRepoDealEntrustOrderDto);
        //处理组合分配
        List<RepoPtlAllocationDto> ptlAllocations = new ArrayList<>();
        // 所有限价指令的回购利率中的最小值或最大值
        BigDecimal minOrMaxYield = null;
        // 指定价格的回购利率
        BigDecimal yieldPRM01 = null;
        // 交易金额
        BigDecimal tradeAmt = BigDecimal.ZERO;
        for (CfetsRepoDealEntrustOrderDto item : entrustDtos) {
            // 所有限价指令的回购利率中的最小值 初始化
            if (minOrMaxYield == null && StringUtil.equals(item.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue())) {
                minOrMaxYield = ArithmeticUtil.round(item.getTradeYield(), yieldScale, RoundingMode.HALF_UP);
            }
            // 交易方式为正回购时,查找限价指令的回购利率中最小值
            if (minOrMaxYield != null && StringUtil.equals(cfetsRepoDealEntrustOrderDto.getPs(), DictCodeListExt3.交易方向_正回购.getValue())
                    && StringUtil.equals(item.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue())) {
                minOrMaxYield = minOrMaxYield.compareTo(ArithmeticUtil.round(item.getTradeYield(), yieldScale, RoundingMode.HALF_UP)) <= 0
                        ? minOrMaxYield : ArithmeticUtil.round(item.getTradeYield(), yieldScale, RoundingMode.HALF_UP);
            }
            // 易方式为逆回购时,查找限价指令的回购利率中最大值
            else if (minOrMaxYield != null && StringUtil.equals(cfetsRepoDealEntrustOrderDto.getPs(), DictCodeListExt3.交易方向_逆回购.getValue())
                    && StringUtil.equals(item.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue())) {
                minOrMaxYield = minOrMaxYield.compareTo(ArithmeticUtil.round(item.getTradeYield(), yieldScale, RoundingMode.HALF_UP)) >= 0
                        ? minOrMaxYield : ArithmeticUtil.round(item.getTradeYield(), yieldScale, RoundingMode.HALF_UP);
            }
            // 指定价格回购利率 初始化
            if (yieldPRM01 == null && StringUtil.equals(item.getPriceModel(), DictCodeListExt2.价格模式_指定价格.getValue())) {
                yieldPRM01 = ArithmeticUtil.round(item.getTradeYield(), yieldScale, RoundingMode.HALF_UP);
            }
            // 处理组合分配
            RepoPtlAllocationDto ptlAllocationDto = new RepoPtlAllocationDto();
            BeanUtil.copyPropertiesIgnoreNull(ptlAllocationDto, item);
            ptlAllocationDto.setAvaNum(item.getAvaNum());
            //交易金额应默认等于剩余指令额度
            ptlAllocationDto.setTradeAmt(item.getAvaNum());
            // 回购利率
            ptlAllocationDto.setPrice(item.getTradeYield());
            ptlAllocations.add(ptlAllocationDto);
            // 基本信息中【交易金额】默认为组合分配中剩余指令额度的汇总
            tradeAmt = ArithmeticUtil.doAdd(tradeAmt, ptlAllocationDto.getTradeAmt());
        }
        //【回购利率】可根据指令自动带出，带出逻辑
        // 勾选指令的价格模式只存在指定价格或同时存在指定价格和限价指令时,返回指定价格指令中的回购利率
        if (yieldPRM01 != null) {
            cfetsRepoDealQuoteCommonDto.setPrice(yieldPRM01);
            cfetsRepoDealQuoteCommonDto.setPriceModelClass("01");
            if (minOrMaxYield != null) {
                cfetsRepoDealQuoteCommonDto.setPriceModelClass("03");
                cfetsRepoDealQuoteCommonDto.setPrice(null);
            }
        }
        //勾选指令的价格模式只存在限价指令时,逆回购 限价指令中回购利率的最大值 正回购 限价指令中回购利率的最小值
        else if (minOrMaxYield != null) {
            cfetsRepoDealQuoteCommonDto.setPrice(minOrMaxYield);
            cfetsRepoDealQuoteCommonDto.setPriceModelClass("02");
            cfetsRepoDealQuoteCommonDto.setPrice(null);
        }
        // 处理组合分配
        cfetsRepoDealQuoteCommonDto.setPtlAllocations(ptlAllocations);
        // 处理组合分配
        cfetsRepoDealQuoteCommonDto.setRepoEntrustPledgeDtos(repoEntrustPledgeDtos);
        // 基本信息中【交易金额】默认为组合分配中剩余指令额度的汇总
        cfetsRepoDealQuoteCommonDto.setTradeAmt(tradeAmt);
        if (CollectionUtils.isEmpty(repoEntrustPledgeDtos)
                && StringUtil.equals(cfetsRepoDealQuoteCommonDto.getPs(), DictCodeListExt3.交易方向_正回购.getValue())) {
            cfetsRepoDealQuoteCommonDto.setTradeAmt(null);
        }
        // 【券面总额合计】等于质押券信息列表中质押券面总额的汇总
        BigDecimal shareAmt = BigDecimal.ZERO;
        // 【质押券折算总额】等于质押券信息中质押券面总额*折算比例的汇总
        BigDecimal convertAmt = BigDecimal.ZERO;
        for (RepoEntrustPledgeDto pledgeDto : repoEntrustPledgeDtos) {
            if (StringUtil.isNotEmpty(pledgeDto.getPledgePrinStr())) {
                pledgeDto.setPledgePrin(ArithmeticUtil.multiply(new BigDecimal(pledgeDto.getPledgePrinStr()), new BigDecimal(10000)));
            }
            // 质押券面额(元)
            shareAmt = ArithmeticUtil.doAdd(shareAmt, pledgeDto.getPledgePrin());
            // 质押券折算(元)
            convertAmt = ArithmeticUtil.doAdd(convertAmt, ArithmeticUtil.multiply(pledgeDto.getPledgePrin(),
                    ArithmeticUtil.divide(pledgeDto.getPledgeRatio(), new BigDecimal(100), 6)));
        }
        // 【券面总额合计】
        cfetsRepoDealQuoteCommonDto.setShareAmt(shareAmt);
        // 【质押券折算总额】
        cfetsRepoDealQuoteCommonDto.setConvertAmt(convertAmt);
        // 格式化数据
        formatCfetsRepoDealQuote(cfetsRepoDealQuoteCommonDto, cfetsRepoDealEntrustOrderDto);
        // 基本信息中【交易手续费】、【结算服务费】，系统根据银行间交易费用规则，自动计算每笔交易的结算服务费和交易手续费，用户可手工修改
        if (StringUtil.equals(cfetsRepoDealQuoteCommonDto.getPs(), DictCodeListExt3.交易方向_逆回购.getValue())) {
            //计算费用
            RepoCalRequest calRequest = new RepoCalRequest();
            calRequest.setTradeAmt(tradeAmt);//交易金额
            calRequest.setPs(cfetsRepoDealQuoteCommonDto.getPs());//交易方向
            calRequest.setSecManageAcctId(cfetsRepoDealQuoteCommonDto.getSecManageAcctId());//证券账户
            calRequest.setTermType(cfetsRepoDealEntrustOrderDto.getTermType());//期限品种
            calRequest.setPledgeNum(repoEntrustPledgeDtos == null ? 0 : repoEntrustPledgeDtos.size());//质押券数量
            calRequest.setTradeDate(cfetsRepoDealQuoteCommonDto.getTradeDate());//交易日期
            CfetsRepoDealQuoteCommonDto temp = this.calFee(calRequest);//计算费用
            cfetsRepoDealQuoteCommonDto.setDealFeeAmt(temp.getDealFeeAmt());//交易手续费
            cfetsRepoDealQuoteCommonDto.setSettFeeAmt(temp.getSettFeeAmt());//结算手续费
            // 计算金额信息
            calRequest.setPrice(cfetsRepoDealQuoteCommonDto.getPrice());//回购利率
            calRequest.setSettleSpreedCode(cfetsRepoDealQuoteCommonDto.getSettleSpreedCode());//清算速度
            calRequest.setRepoTerm(cfetsRepoDealQuoteCommonDto.getRepoTerm());//回购期限
            temp = this.calAmtInfo(calRequest);
            cfetsRepoDealQuoteCommonDto.setmIntAmt(temp.getmIntAmt());//回购利息
            cfetsRepoDealQuoteCommonDto.setMtradeAmt(temp.getMtradeAmt());//到期结算金额
            cfetsRepoDealQuoteCommonDto.setSettlementStartDate(temp.getSettlementStartDate());//首次结算日
            cfetsRepoDealQuoteCommonDto.setSettlementEndDate(temp.getSettlementEndDate());//到期结算日
            cfetsRepoDealQuoteCommonDto.setActCapDays(temp.getActCapDays());//实际占款天数
            cfetsRepoDealQuoteCommonDto.setTermType(temp.getTermType());//回购期限
        }

        return cfetsRepoDealQuoteCommonDto;
    }

    /**
     * 交易执行初始化数据格式化
     *
     * @param cfetsRepoDealQuoteCommonDto  初始化数据
     * @param cfetsRepoDealEntrustOrderDto 指令信息
     * @author jiangpeng
     * @date 2021/12/8 11:07
     */
    private void formatCfetsRepoDealQuote(CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto,
                                          CfetsRepoDealEntrustOrderDto cfetsRepoDealEntrustOrderDto) {
        // 备注
        cfetsRepoDealQuoteCommonDto.setRemark(StringUtil.EMPTY);
        //清算速度处理
        if (StringUtil.equals(cfetsRepoDealEntrustOrderDto.getSettleSpreed(), "0")) {
            cfetsRepoDealQuoteCommonDto.setSettleSpreedCode(BussinessDictCodeList.清算速度_T加0.getValue());
        }
        if (StringUtil.equals(cfetsRepoDealEntrustOrderDto.getSettleSpreed(), "1")) {
            cfetsRepoDealQuoteCommonDto.setSettleSpreedCode(BussinessDictCodeList.清算速度_T加1.getValue());
        }
        if (StringUtil.equals(cfetsRepoDealEntrustOrderDto.getSettleSpreed(), "2")) {
            cfetsRepoDealQuoteCommonDto.setSettleSpreedCode(BussinessDictCodeList.清算速度_T加2.getValue());
        }
        // 交易日期
        cfetsRepoDealQuoteCommonDto.setTradeDate(iPlatformDate.getSysDate());
        // 结算日期
//        cfetsRepoDealQuoteCommonDto.setSettleDate(DateUtil.addDay(iPlatformDate.getSysDate(), Integer.parseInt(cfetsRepoDealEntrustOrderDto.getSettleSpreed())));
        //本方交易员应默认当前登录用户姓名
        if (StringUtil.isEmpty(cfetsRepoDealQuoteCommonDto.getMySideTrader()) && null != SecurityUtil.getLoginUser()) {
            cfetsRepoDealQuoteCommonDto.setMySideTrader(SecurityUtil.getLoginUser().getUserName());
            cfetsRepoDealQuoteCommonDto.setOuTraderName(SecurityUtil.getLoginUser().getRealName());
        }
        // 回购利率格式化,保留四位小数
        if (StringUtil.isNotEmpty(cfetsRepoDealQuoteCommonDto.getPrice())) {
            cfetsRepoDealQuoteCommonDto.setPrice(
                    ArithmeticUtil.round(cfetsRepoDealQuoteCommonDto.getPrice(), yieldScale, RoundingMode.HALF_UP));
        }
    }

    /**
     * 初始化组合分配列表(委托列表触发)
     *
     * @param list
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.RepoEntrustDto>
     * @author jiangpeng
     * @date 2021/12/20 14:40
     */
    private List<RepoEntrustDto> initPtlAllocation(List<CfetsRepoDealEntrustOrderDto> list) {
        List<RepoEntrustDto> result = new ArrayList<>();
        for (CfetsRepoDealEntrustOrderDto item : list) {
            if ((new BigDecimal(100)).compareTo(item.getTransactioncom()) <= 0) {
                // 执行进度不到100%
                continue;
            }
            RepoEntrustDto entrustDto = new RepoEntrustDto();
            BeanUtil.copyPropertiesIgnoreNull(entrustDto, item);
            result.add(entrustDto);
        }
        return result;
    }

    /**
     * 组合分配方式 平均分配
     *
     * @param request               入参
     * @param ptlAllocations        组合分配信息
     * @param repoEntrustPledgeDtos 质押券信息
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.RepoPtlAllocationDto>
     * @author jiangpeng
     * @date 2021/12/20 14:40
     */
    private List<RepoPtlAllocationDto> aveDistribution(RepoAutoAllocationRequest request,
                                                       List<RepoPtlAllocationDto> ptlAllocations, List<RepoEntrustPledgeDto> repoEntrustPledgeDtos) {
        // 过滤没有质押券的组合
        BigDecimal tradeAmt = request.getTradeAmt();
        //降序
        ptlAllocations.sort((p1, p2) -> p1.getAvaNum().compareTo(p2.getAvaNum()));
        BigDecimal avg = ArithmeticUtil.divide(tradeAmt, ptlAllocations.size(), 4);
        //剩余尾差
        BigDecimal tailDifference = tradeAmt;
        for (RepoPtlAllocationDto item : ptlAllocations) {
            if (item.getAvaNum().compareTo(avg) >= 0) {
                item.setTradeAmt(avg);
            } else {
                item.setTradeAmt(item.getAvaNum());
            }
            item.setAvaNum(ArithmeticUtil.subtract(item.getAvaNum(), item.getTradeAmt()));
            tailDifference = ArithmeticUtil.subtract(tailDifference, item.getTradeAmt());
        }
        handleTailDifference(ptlAllocations, tailDifference);
        calOtherForPtlAllocation(ptlAllocations, request.getPs(), request.getTradeAmt(), request.getmIntAmt(),
                request.getMtradeAmt(), request.getDealFeeAmt(), request.getSettFeeAmt(), request.getConvertAmt(), request.getShareAmt());
        return ptlAllocations;
    }

    /**
     * 尾差处理
     *
     * @param ptlAllocations 组合分配信息
     * @param tailDifference 尾差
     * @author jiangpeng
     * @date 2021/12/20 14:40
     */
    private void handleTailDifference(List<RepoPtlAllocationDto> ptlAllocations, BigDecimal tailDifference) {
        //递增维度
        BigDecimal addDimension = new BigDecimal(dimension);
        int size = ptlAllocations.size();
        //委托维度 已经完全分配的集合
        Set<String> set = new HashSet<>();
        while (tailDifference.compareTo(BigDecimal.ZERO) > 0 && size > 0) {
            if (tailDifference.compareTo(new BigDecimal("1")) >= 0) {
                addDimension = ArithmeticUtil.divide(tailDifference, size, 2);
            }
            for (RepoPtlAllocationDto item : ptlAllocations) {
                if ((item.getAvaNum().compareTo(item.getTradeAmt()) == 0 || tailDifference.compareTo(BigDecimal.ZERO) <= 0)) {
                    if (set.add(item.getEntrustId())) {
                        size--;
                    }
                } else {
                    item.setTradeAmt(ArithmeticUtil.doAdd(item.getTradeAmt(), addDimension));
                    tailDifference = ArithmeticUtil.subtract(tailDifference, addDimension);
                }
            }
        }
    }

    /**
     * 组合分配方式 比例分配
     *
     * @param request               入参
     * @param ptlAllocations        组合分配信息
     * @param repoEntrustPledgeDtos 质押券信息
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.RepoPtlAllocationDto>
     * @author jiangpeng
     * @date 2021/12/20 14:41
     */
    private List<RepoPtlAllocationDto> propDistribution(RepoAutoAllocationRequest request,
                                                        List<RepoPtlAllocationDto> ptlAllocations, List<RepoEntrustPledgeDto> repoEntrustPledgeDtos) {
        // 过滤没有质押券的组合
        BigDecimal tradeAmt = request.getTradeAmt();
        //降序
        ptlAllocations.sort((p1, p2) -> p1.getAvaNum().compareTo(p2.getAvaNum()));
        //剩余额度汇总
        BigDecimal avaNumTotal = ptlAllocations.stream().map(RepoPtlAllocationDto::getAvaNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        //尾差
        BigDecimal tailDifference = tradeAmt;
        for (RepoPtlAllocationDto ptlAllocation : ptlAllocations) {
            ptlAllocation.setTradeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(tradeAmt, ptlAllocation.getTradeAmt()), avaNumTotal, 4));
            tailDifference = ArithmeticUtil.subtract(tailDifference, ptlAllocation.getTradeAmt());
        }
        handleTailDifference(ptlAllocations, tailDifference);
        calOtherForPtlAllocation(ptlAllocations, request.getPs(), request.getTradeAmt(), request.getmIntAmt(),
                request.getMtradeAmt(), request.getDealFeeAmt(), request.getSettFeeAmt(), request.getConvertAmt(), request.getShareAmt());
        return ptlAllocations;
    }

    /**
     * 计算组合列表其他值[回购利息,到期结算金额,交易手续费,结算手续费]
     *
     * @param ptlAllocations 组合分配
     * @param ps             交易方向
     * @param tradeAmt       交易金额
     * @param mIntAmt        回购利息
     * @param mtradeAmt      到期结算金额
     * @param dealFeeAmt     交易手续费
     * @param settFeeAmt     结算服务费
     * @param convertAmt     折算质押券面总额
     * @author jiangpeng
     * @date 2021/12/9 14:12
     */
    public void calOtherForPtlAllocation(List<RepoPtlAllocationDto> ptlAllocations, String ps,
                                         BigDecimal tradeAmt, BigDecimal mIntAmt, BigDecimal mtradeAmt,
                                         BigDecimal dealFeeAmt, BigDecimal settFeeAmt, BigDecimal convertAmt,
                                         BigDecimal shareAmt) {
        //尾差
        BigDecimal mIntAmtTailDifference = mIntAmt;
        BigDecimal mtradeAmtTailDifference = mtradeAmt;
        BigDecimal dealFeeAmtTailDifference = dealFeeAmt;
        BigDecimal settFeeAmtTailDifference = settFeeAmt;
        BigDecimal convertAmtTailDifference = convertAmt;
        BigDecimal shareAmtTailDifference = shareAmt;
        BigDecimal size = new BigDecimal(ptlAllocations.size());

        for (RepoPtlAllocationDto item : ptlAllocations) {
            //组合分配中交易金额/基本信息中总的交易金额*金额信息中回购利息
            BigDecimal mIntAmtTemp = ArithmeticUtil.round(
                    ArithmeticUtil.multiply(
                            ArithmeticUtil.divide(item.getTradeAmt(), tradeAmt, 12, RoundingMode.HALF_UP), mIntAmt), 2, RoundingMode.HALF_UP);
            item.setmIntAmt(StringUtil.isNotEmpty(mIntAmtTemp) ? mIntAmtTemp : BigDecimal.ZERO);
            mIntAmtTailDifference = ArithmeticUtil.subtract(mIntAmtTailDifference, item.getmIntAmt());
            //组合分配中交易金额/基本信息中总的交易金额*金额信息中到期结算金额
            BigDecimal mtradeAmtTemp = ArithmeticUtil.round(
                    ArithmeticUtil.multiply(
                            ArithmeticUtil.divide(item.getTradeAmt(), tradeAmt, 12, RoundingMode.HALF_UP), mtradeAmt), 2, RoundingMode.HALF_UP);
            item.setMtradeAmt(StringUtil.isNotEmpty(mtradeAmtTemp) ? mtradeAmtTemp : BigDecimal.ZERO);
            mtradeAmtTailDifference = ArithmeticUtil.subtract(mtradeAmtTailDifference, item.getMtradeAmt());
            //组合分配中交易金额/基本信息中总的交易金额*基本信息中交易手续费
            BigDecimal dealFeeAmtTemp = ArithmeticUtil.round(
                    ArithmeticUtil.multiply(
                            ArithmeticUtil.divide(item.getTradeAmt(), tradeAmt, 12, RoundingMode.HALF_UP), dealFeeAmt), 2, RoundingMode.HALF_UP);
            item.setDealFeeAmt(StringUtil.isNotEmpty(dealFeeAmtTemp) ? dealFeeAmtTemp : BigDecimal.ZERO);
            dealFeeAmtTailDifference = ArithmeticUtil.subtract(dealFeeAmtTailDifference, item.getDealFeeAmt());
            // 结算手续费 平均分配
            BigDecimal settFeeAmtTemp = ArithmeticUtil.divide(settFeeAmt, size, 2, RoundingMode.HALF_UP);
            item.setSettFeeAmt(StringUtil.isNotEmpty(settFeeAmtTemp) ? settFeeAmtTemp : BigDecimal.ZERO);
            settFeeAmtTailDifference = ArithmeticUtil.subtract(settFeeAmtTailDifference, item.getSettFeeAmt());
            //组合分配中交易金额/基本信息中总的折算券面*基本信息中交易手续费
            if (convertAmt != null) {
                BigDecimal convertAmtTemp = ArithmeticUtil.round(
                        ArithmeticUtil.multiply(
                                ArithmeticUtil.divide(item.getTradeAmt(), tradeAmt, 12, RoundingMode.HALF_UP), convertAmt), 2, RoundingMode.HALF_UP);
                item.setConvertAmt(convertAmtTemp);
                convertAmtTailDifference = ArithmeticUtil.subtract(convertAmtTailDifference, item.getConvertAmt());
            }
            if (shareAmt != null) {
                BigDecimal shareAmtTemp = ArithmeticUtil.round(
                        ArithmeticUtil.multiply(
                                ArithmeticUtil.divide(item.getTradeAmt(), tradeAmt, 12, RoundingMode.HALF_UP), shareAmt), 2, RoundingMode.HALF_UP);
                item.setShareAmt(shareAmtTemp);
                shareAmtTailDifference = ArithmeticUtil.subtract(shareAmtTailDifference, item.getShareAmt());
            }
        }
        handleTailDifference(ptlAllocations, mIntAmtTailDifference, 0);
        handleTailDifference(ptlAllocations, mtradeAmtTailDifference, 1);
        handleTailDifference(ptlAllocations, dealFeeAmtTailDifference, 2);
        handleTailDifference(ptlAllocations, settFeeAmtTailDifference, 3);
        if (convertAmt != null) {
            handleTailDifference(ptlAllocations, convertAmtTailDifference, 5);
        }
        if (shareAmt != null) {
            handleTailDifference(ptlAllocations, shareAmtTailDifference, 6);
        }
    }

    /**
     * 尾差处理
     *
     * @param ptlAllocations 组合分配信息
     * @param tailDifference 尾差
     * @param type           尾差类型 0 回购利息 1 到期结算金额 2 交易手续费 3 结算服务费 4 交易金额 5 折算质押券
     * @author jiangpeng
     * @date 2021/12/9 14:27
     */
    private void handleTailDifference(List<RepoPtlAllocationDto> ptlAllocations, BigDecimal tailDifference, int type) {
        //递增维度
        BigDecimal addDimension = new BigDecimal(dimension);
        int size = ptlAllocations.size();
        while (tailDifference.compareTo(BigDecimal.ZERO) != 0 && size > 0) {
            if (tailDifference.compareTo(new BigDecimal("1")) >= 0) {
                addDimension = ArithmeticUtil.divide(tailDifference, size, 2);
            }
            if (size > 0 && tailDifference.compareTo(BigDecimal.ZERO) < 0) {
                RepoPtlAllocationDto ptlAllocationDto = ptlAllocations.get(size - 1);
                //回购利息
                if (type == 0) {
                    ptlAllocationDto.setmIntAmt(ArithmeticUtil.doAdd(ptlAllocationDto.getmIntAmt(), tailDifference));
                }
                //到期结算金额
                if (type == 1) {
                    ptlAllocationDto.setMtradeAmt(ArithmeticUtil.doAdd(ptlAllocationDto.getMtradeAmt(), tailDifference));
                }
                //交易手续费
                if (type == 2) {
                    ptlAllocationDto.setDealFeeAmt(ArithmeticUtil.doAdd(ptlAllocationDto.getDealFeeAmt(), tailDifference));
                }
                //结算服务费
                if (type == 3) {
                    ptlAllocationDto.setSettFeeAmt(ArithmeticUtil.doAdd(ptlAllocationDto.getSettFeeAmt(), tailDifference));
                }
                //交易金额
                if (type == 4) {
                    ptlAllocationDto.setTradeAmt(ArithmeticUtil.doAdd(ptlAllocationDto.getTradeAmt(), tailDifference));
                }
                //折算质押券
                if (type == 5) {
                    ptlAllocationDto.setConvertAmt(ArithmeticUtil.doAdd(ptlAllocationDto.getConvertAmt(), tailDifference));
                }
                //质押券面
                if (type == 6) {
                    ptlAllocationDto.setShareAmt(ArithmeticUtil.doAdd(ptlAllocationDto.getShareAmt(), tailDifference));
                }
                break;
            }
            for (RepoPtlAllocationDto item : ptlAllocations) {
                //回购利息
                if (type == 0) {
                    item.setmIntAmt(ArithmeticUtil.doAdd(item.getmIntAmt(), addDimension));
                }
                //到期结算金额
                if (type == 1) {
                    item.setMtradeAmt(ArithmeticUtil.doAdd(item.getMtradeAmt(), addDimension));
                }
                //交易手续费
                if (type == 2) {
                    item.setDealFeeAmt(ArithmeticUtil.doAdd(item.getDealFeeAmt(), addDimension));
                }
                //结算服务费
                if (type == 3) {
                    item.setSettFeeAmt(ArithmeticUtil.doAdd(item.getSettFeeAmt(), addDimension));
                }
                //交易金额
                if (type == 4) {
                    item.setTradeAmt(ArithmeticUtil.doAdd(item.getTradeAmt(), addDimension));
                }
                //折算质押券
                if (type == 5) {
                    item.setConvertAmt(ArithmeticUtil.doAdd(item.getConvertAmt(), addDimension));
                }
                //质押券面
                if (type == 6) {
                    item.setShareAmt(ArithmeticUtil.doAdd(item.getShareAmt(), addDimension));
                }
                tailDifference = ArithmeticUtil.subtract(tailDifference, addDimension);
                if (tailDifference.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                }
            }
        }
    }

    /**
     * 本币交易导出赋值
     *
     * @param org     查询数据
     * @param lastDto 上一条数据
     * @return com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDayRegDto
     * @author jiangpeng
     * @date 2021/12/15 16:04
     */
    private CfetsRepoDayRegDto formatExportData(CfetsRepoDayRegDto org, CfetsRepoDayRegDto lastDto) {
        CfetsRepoDayRegDto dest = new CfetsRepoDayRegDto();
        // list已经经过排序,所以与前一个dto的业务编号比较以判断是否是同一笔交易
        if (!StringUtil.isNotEmpty(lastDto)
                || !StringUtil.equals(org.getQuoteId(), lastDto.getQuoteId())) {
            BeanUtil.copyPropertiesIgnoreNull(dest, org);
            // 默认为空值
            dest.setExcRate(null);
            dest.setStatus(null);
            dest.setmIntAmt(null);
            dest.setMtradeAmt(null);
            dest.setQuoteType(null);
            dest.setUpdateTime(null);
        } else {
            dest.setQuoteId(org.getQuoteId());
            dest.setFinprodMarketId(org.getFinprodMarketId());
            dest.setFinprodName(org.getFinprodName());
            dest.setPledgePrin(org.getPledgePrin());
            dest.setPledgeRatio(org.getPledgeRatio());
        }
        return dest;
    }

    /**
     * 强制完成check
     *
     * @param param
     * @return
     */
    public AjaxResult doForcedFinishCheck(Map<String, Object> param, boolean isremark) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        setParam(param, 1);
        if (!StringUtil.isNotEmpty(param.get("remark")) && isremark) {
            errorList.add(new ValidMessage("请输入强制完成原因!"));
        }
        if (cfetsIbuQuoteRepoMapper.countWflEtr(param) > 0) {
            errorList.add(new ValidMessage("存在流程中交易数据，不可进行强制完成!"));
        }
        List<CfetsRepoDealEntrustOrderDto> entrustDtos = cfetsIbuQuoteRepoMapper.listEntrust(param);
        if (CollectionUtils.isNotEmpty(entrustDtos)) {
            entrustDtos.forEach(item -> {
                if (!StringUtil.equals(item.getEntrustStatus(), BussinessDictCodeList.委托状态_已接收.getValue())) {
                    errorList.add(new ValidMessage("只可对已接收指令执行强制完成操作!"));
                    return;
                }
                if (null != item.getTradeFinishPercent() && item.getTradeFinishPercent().compareTo(BigDecimal.ZERO) == 0) {
                    errorList.add(new ValidMessage("只可对成交进度>0%的已接收未完成指令进行强制完成操作!"));
                    return;
                }
                WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
                wflValidInputParameter.setPageId("RepoEntrustForcedFinish");
                wflValidInputParameter.setBusinessDataPk1(item.getEntrustId());
                if (mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
                    //流程存在情况
                    errorList.add(new ValidMessage("存在流程中交易数据，不可进行强制完成!"));
                    return;
                }
            });
        }
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * 强制完成
     *
     * @param param 入参
     * @author jiangpeng
     * @date 2021/12/20 14:44
     */
    public void doForcedFinish(Map<String, Object> param) {
        String loginUserId = (String) param.get("loginUserId");
        this.setParam(param, 1);
        param.put("loginUserId", loginUserId);
        List<CfetsRepoDealEntrustOrderDto> entrustDtos = cfetsIbuQuoteRepoMapper.listEntrust(param);
        if (entrustDtos != null && entrustDtos.size() > 0) {
            List<CfetsEtribBondInfo003Dto> dtolist = new ArrayList<>();
            for (CfetsRepoDealEntrustOrderDto item : entrustDtos) {
                // 委托指令查询
                EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(item.getEntrustId());
                // 更改委托状态
                etrEntrust.setEntrustStatus(com.joyintech.tams.constant.DictCodeList.委托状态_已完成.getValue());
                etrEntrust.setRemark(StringUtil.join(etrEntrust.getRemark(), (String) param.get("remark")));
                // 修改委托指令表中值
                int flag = etrEntrustMapper.updateByPrimaryKeySelective(etrEntrust);
                // 委托指令附属表查询
                EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(item.getEntrustId());
                //获取已完成额度
                Map<String, Object> params = new HashMap<>();
                params.put("sumTradeFlag","01");
                params.put("portfolioId",etrEntrust.getPortfolioId());
                params.put("entrustId",etrEntrust.getEntrustId());
                params.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
                params.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
                params.put("PS_T01", DictCodeList.交易方向_买入.getValue());
                BigDecimal finshAmt = cfetsIbuQuoteRepoMapper.selectFinshAmt(params);
                BigDecimal entrustAmt = etrEntrust.getEntrustNum() != null ? etrEntrust.getEntrustNum() : etrEntrust.getEntrustAmt();
                etrEntrustAdd.setForcedFinishAmt(ArithmeticUtil.subtract(entrustAmt, finshAmt));
                etrEntrustAddMapper.updateByPrimaryKeySelective(etrEntrustAdd);
                // 修改委托指令分发表中值 add by yangwenjun 20201209
                baseEtrInvestInfoMapper.updatEetrDistribute(etrEntrust);
                CfetsEtribBondInfo003Dto etrInvestBondInfoDto = new CfetsEtribBondInfo003Dto();
                BeanUtil.copyPropertiesIgnoreNull(etrInvestBondInfoDto, item);
                etrInvestBondInfoDto.setForcedFinishAmt(ArithmeticUtil.subtract(entrustAmt, finshAmt));
                dtolist.add(etrInvestBondInfoDto);
            }
            List<String> entrustIdList = dtolist.stream().map(
                    CfetsEtribBondInfo003Dto::getEntrustId).collect(Collectors.toList());
            // ZGZGST-2330:委托关闭判断是否有完成额度，分情况处理 fuyongsheng 20210919 edit start
            if (CollectionUtils.isNotEmpty(entrustIdList)) {
                Map<String, BigDecimal> etrFinishMap = EntrustCompleteUtil.getEtrFinishAmtList(entrustIdList);
                //委托没有做过交易的，和撤销相同逻辑，删除预占
                List<CfetsEtribBondInfo003Dto> recallEntrustIdList = new ArrayList<>();
                //委托已有完成额度，做过交易，走关闭完成的逻辑，更新预占
                List<String> closeEntrustIdList = new ArrayList<>();
                for (CfetsEtribBondInfo003Dto etrDto : dtolist) {
                    if (StringUtil.isNotEmpty(etrFinishMap) && StringUtil.isNotEmpty(etrFinishMap.get(etrDto.getEntrustId()))
                            && BigDecimal.ZERO.compareTo(etrFinishMap.get(etrDto.getEntrustId())) < 0) {
                        closeEntrustIdList.add(etrDto.getEntrustId());
                    } else if (entrustIdList.contains(etrDto.getEntrustId())) {
                        recallEntrustIdList.add(etrDto);
                    }
                }

                //没有做过交易，直接关闭的
                if (CollectionUtils.isNotEmpty(recallEntrustIdList)) {
                    // add by yushilong 批量撤销预占 start
                    try {
                        EntrustRdsCommonAop.doListPreOccupy(recallEntrustIdList, DictCodeList.平安资产类型_质押式回购.getValue(),
                                BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(), false, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // add by yushilong 批量撤销预占 end
                }

                //已经有委托完成度的
                if (!StringUtil.isEmptyList(closeEntrustIdList)) {
                    EntrustCompleteUtil.doComplete(closeEntrustIdList);
                }
            }
            // ZGZGST-2330:委托关闭判断是否有完成额度，分情况处理 fuyongsheng 20210919 edit end
            // add by zhangrui1 批量委托关闭 end

            if (StringUtil.equals(com.joyintech.tams.constant.DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                //插入旧头寸
                dtolist.forEach(item ->{
                    baseEtrCashPositionCommService.entrustCloseExecute(item.getEntrustId(), com.joyintech.tams.constant.DictCodeList.是否_是.getValue());
                });
            }
//            //发送头寸，批量关闭
            cfetsEtrIbBondinfoService.closeDelegatePositionMethod(dtolist, DictCodeList.平安资产类型_质押式回购.getValue());
//            //更新操作日志
            dtolist.forEach(this::closeDelegatOprHisMethod);
        }
    }

    /**
     * 更新操作日志
     *
     * @param dto 入参
     * @author jiangpeng
     * @date 2021/12/20 14:45
     */
    public void closeDelegatOprHisMethod(CfetsEtribBondInfo003Dto dto) {
        List<CfetsEtribBondInfo003Dto> dtolist = dto.getSelectedItem();
        if (dtolist != null && dtolist.size() > 0) {
            for (CfetsEtribBondInfo003Dto entity : dtolist) {
                //更新操作记录日志
                CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
                oprHisReq.setEntrustId(entity.getEntrustId());
                oprHisReq.setOperType(DictCodeListExt3.操作类型_关闭.getValue());
                oprHisReq.setBusiDes(dto.getRemark());
                joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
            }
        }

    }

    /**
     * 发送头寸，批量关闭
     *
     * @param dtolist 入参
     * @author jiangpeng
     * @date 2021/12/20 14:46
     */
    public void closeDelegatePositionMethod(List<EtrInvestBondInfoDto> dtolist) {
        CfetsEtrIbClosePositionRequest req = new CfetsEtrIbClosePositionRequest();
        List<CfetsEtrIbClosePosEtrRequest> etrList = new ArrayList<>();
        for (EtrInvestBondInfoDto t : dtolist) {
            CfetsEtrIbClosePosEtrRequest closeItem = new CfetsEtrIbClosePosEtrRequest();
            closeItem.setBusiType(DictCodeListExt3.头寸业务类型_质押式回购首期.getValue());
            closeItem.setPs(t.getPs());
            closeItem.setCloseId(t.getEntrustId());
            closeItem.setSystemAct(DictCodeListExt3.系统动作_关闭.getValue());
            closeItem.setBusiAct(DictCodeListExt3.业务动作_指令手动关闭.getValue());
            etrList.add(closeItem);
        }
        req.setData(etrList);
        req.setUserId(SecurityUtil.getLoginUser().getUserName());
        req.setTotalTradeId(IDGenerator.generate());
        req.setModelType(DictCodeListExt1.子系统代码_委托.getValue());
        PositionHttpRequest.batchClose(req);
    }

    /**
     * 获取指令详情
     *
     * @param pageCode
     * @param dtCode
     * @param spcdCodes
     * @param dto
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author jiangpeng
     * @date 2021/12/30 20:31
     */
    public Map<String, Object> findEntrustDet(String pageCode, String dtCode, String spcdCodes, CfetsRepoDealQuoteRequest dto) {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtil.isBlank(spcdCodes)) {
            return resultData;
        }
        String repo_pledge_model = CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL);
        List<String> spcdCodeList = new ArrayList<>(Arrays.asList(spcdCodes.split(",")));
        Map<String, Object> param = new HashMap<>();
        param.put("pageCode", pageCode);
        param.put("dtCode", dtCode);
        param.put("spcdCodes", spcdCodeList);
        //基本信息
        List<CfgReadonlyFormShow> infoForm = cfgReadonlyFormShowMapper.getReadonlyForms(param);
        Map<String, Object> paramMap = BeanUtil.object2Map(dto);
        setParam(paramMap, 1);
        //银行间质押式回购质押券模式：01无质押券库，02单个质押库，03多个质押券库
        paramMap.put("repo_pledge_model", repo_pledge_model);
        List<CfetsRepoDealEntrustOrderDto> entrustDtos = cfetsIbuQuoteRepoMapper.listEntrust(paramMap);
        CfetsRepoDealEntrustOrderDto entrustOrderDto = new CfetsRepoDealEntrustOrderDto();
        if (CollectionUtils.isNotEmpty(entrustDtos)) {
            entrustOrderDto = entrustDtos.get(0);
        }
        entrustOrderDto.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());
        if (StringUtil.equals(repo_pledge_model, "02")) {
            List<CfgDataTableShow> infoForm1;
            //获取登录用户的ID
            String userId = SecurityUtil.getLoginUser().getUserName();
            CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCodes(pageCode, dtCode, spcdCodeList, userId);
            if (null == flag) {
                infoForm1 = cfgDataTableMapper.getBySpcdCodes(pageCode, dtCode, spcdCodeList, EFFERANGE_NO, USER_ID);
            } else {
                infoForm1 = cfgDataTableMapper.getBySpcdCodes(pageCode, dtCode, spcdCodeList, EFFERANGE_IS, userId);
            }
            List<RepoEntrustPledgeDto> list = this.selectRepoEntrustPledges(dto);
            BigDecimal shareAmt = BigDecimal.ZERO;
            for (RepoEntrustPledgeDto temp : list) {
                shareAmt = ArithmeticUtil.doAdd(shareAmt, ArithmeticUtil.divide(ArithmeticUtil.multiply(temp.getPledgePrin(), temp.getPledgeRatio()), new BigDecimal(100), 2));
            }
            entrustOrderDto.setSumShareAmt(shareAmt);
            resultData.put(dtCode + "_table", infoForm1);
            resultData.put(dtCode + "_tableData", list);
        }
        resultData.put(dtCode + "_form", infoForm);
        resultData.put(dtCode + "_data", entrustOrderDto);

        return resultData;
    }

    /**
     * check 退回指令
     *
     * @param param
     * @return
     */
    public AjaxResult entrustRefuceCheck(Map<String, Object> param, boolean isRefuce) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if (!StringUtil.isNotEmpty(param.get("remark")) && isRefuce) {
            errorList.add(new ValidMessage("请输入退回原因!"));
        }
        setParam(param, 1);
        List<CfetsRepoDealEntrustOrderDto> cfetsBondDealEntrustOrderDtos = cfetsIbuQuoteRepoMapper.listEntrust(param);
        boolean statusFlag = false;
        boolean flowFlag = false;
        if (CollectionUtils.isNotEmpty(cfetsBondDealEntrustOrderDtos)) {
            for (CfetsRepoDealEntrustOrderDto item : cfetsBondDealEntrustOrderDtos) {
                if (!StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已接收.getValue())
                        || item.getTransactioncom().compareTo(BigDecimal.ZERO) != 0) {
                    statusFlag = true;
                }
                param.put("entrustId", item.getEntrustId());
                if (cfetsIbuQuoteBondEtrMapper.checkDataStatus(param) > 0) {
                    flowFlag = true;
                }
            }
        }
        param.put("QUOTE_STUTAS_IBU_08", BussinessDictCodeList.银行间上行报价状态_已终止.getValue());
        param.put("QUOTE_STUTAS_IBU_14", BussinessDictCodeList.银行间上行报价状态_已拒绝.getValue());
        param.put("QUOTE_STUTAS_IBU_15", BussinessDictCodeList.银行间上行报价状态_已撤销.getValue());
        int s = cfetsIbuQuoteRepoMapper.checkWflData(param);
        if (statusFlag||s>0) {
            errorList.add(new ValidMessage("只可对待分发或者已接收执行进度为0%的指令执行退回操作!"));
        }
        if (flowFlag) {
            errorList.add(new ValidMessage("存在流程中的交易数据，不可退回!"));
        }

        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }


    /**
     * 接收指令
     *
     * @param param
     * @return
     */
    public List<ValidMessage> receiveEntrust(Map<String, Object> param) {
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        String trader = LoginUserUtil.getUserName();
        if ("admin".equals(trader)) {
//            trader = "jp";//TODO 测试写死登录用户 提交代码时修改
        }
        param.put("trader", trader);
        List<EtrDistribute> list = getDistributeTrader(param);
        List<String> traderList = new ArrayList<String>();
        for (EtrDistribute etrDis : list) {
            errorList.addAll(etrInvesInfo.checkDelegateOperation(etrDis.getEntrustId(), DictCodeListExt1.委托操作类型_接收.getValue(), traderList, DictCodeListExt1.委托关闭方式_人工.getValue()));
            etrDis.setUpdateUser(etrDis.getTrader());
        }
        //无错误时，进行接收操作
        if (errorList.size() == 0) {
            etrInvesInfo.receptionDelegate(list);
            List<String> entrustIds = StringUtil.splitToList(",", (String) param.get("entrustIds"));
            for (String entrustId : entrustIds) {
                //更新操作记录日志
                CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
                oprHisReq.setEntrustId(entrustId);
                oprHisReq.setOperType(DictCodeListExt3.操作类型_接收.getValue());
                joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
            }
        }
        return errorList;
    }

    public List<EtrDistribute> getDistributeTrader(Map<String, Object> param) {
        List<EtrDistribute> list = new ArrayList<EtrDistribute>();
        EtrDistributeKey etrKey = new EtrDistributeKey();
        etrKey.setTrader((String) param.get("trader"));
        List<String> entrustIds = StringUtil.splitToList(",", (String) param.get("entrustIds"));
        for (String entrustId : entrustIds) {
            etrKey.setEntrustId(entrustId);
            list.add(etrDistributeMapper.selectByPrimaryKey(etrKey));
        }
        return list;
    }

    /**
     * 指令放弃
     *
     * @param entrustId
     * @return
     */
    public String giveUpEntrust(String entrustId) {
        String massege = "";
        if (StringUtil.isEmpty(entrustId)) {
            massege = "请选择指令！";
        }
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        if (etrEntrust == null) {
            massege = "未查询到指令信息！";
        }

        Map<String, Object> params = new HashMap<>();
        params.put("entrustId", entrustId);
        setParam(params, 1);
        List<CfetsRepoDealEntrustOrderDto> cfetsBondDealEntrustOrderDtos = cfetsIbuQuoteRepoMapper.listEntrust(params);
        // 已接收且执行进度<100%的指令可以选择【放弃】接收
        if (CollectionUtils.isNotEmpty(cfetsBondDealEntrustOrderDtos)) {
            for (CfetsRepoDealEntrustOrderDto item : cfetsBondDealEntrustOrderDtos) {
                if (!StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已接收)
                        && cfetsRepoQuotedPrice004Service.ACompareToB(item.getTransactioncom(), new BigDecimal(100), 2) >= 0) {
                    return "已接收且执行进度<100%的指令,才能执行放弃操作！";
                }
            }
        }
        Map<String, Object> param = new HashMap<>();
        param.put("entrustId", entrustId);
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustId);
        if (etrEntrustAdd == null) {
            return "未查询到指令附属信息！";
        }
        //是否存在流程中或者有效且数据状态不为 已撤回 已终止
        int i = cfetsIbuQuoteBondEtrMapper.checkDataStatus(param);
        if (i > 0) {
            return "存在流程中的报价，不可放弃！";
        }
        //2.查询指令分发的临时表数据  插入  指令分发表，并将状态改为  待接收。
        List<EtrDistributeInterfTmp> distmpList = cfetsIbuQuoteBondEtrMapper.listEtrDistributeInterfTmp(param);
        if (!StringUtil.isEmptyList(distmpList)) {
            boolean status = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId, DictCodeList.委托状态_待接收.getValue());
            List<EtrDistribute> disList = new ArrayList<EtrDistribute>();
            for (EtrDistributeInterfTmp entity : distmpList) {
                EtrDistribute dis = new EtrDistribute();
                dis.setEntrustId(entity.getEntrustId());
                dis.setTrader(entity.getTrader());
                dis.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                dis.setTradingHeader(entity.getTradingHeader());
                disList.add(dis);
            }
            this.etrInvestInfoServiceImpl.distributeDelegate(disList);
        } else {
            boolean status = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId, DictCodeListExt3.委托状态_待分发.getValue());
        }
        //更新操作记录日志
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(entrustId);
        oprHisReq.setOperType(DictCodeListExt3.操作类型_放弃.getValue());
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
        return null;
    }

    /**
     * 获取机构信息
     *
     * @return
     */
    public Map<String, String> selectAllCustomer() {
        Map<String, String> map = new HashMap<>();
        cfetsIbuQuoteRepoMapper.selectAllCustomer().stream().forEach(x -> {
            map.put(x.get("PARTY_ID"), x.get("PARTY_ABBR"));
        });
        return map;
    }
}

