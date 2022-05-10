/*
 * 文件名：FieldEtrDispatcherService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：msk
 * 修改时间：2021年11月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrdispatcher.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.service.entrust.etrbondfirstlbidding.dao.EtrBondFirstLBiddingMapper;
import com.joyintech.tams.business.common.dao.CfgReadonlyFormShowMapper;
import com.joyintech.tams.business.common.dto.CommonSearchInfoRequest;
import com.joyintech.tams.business.common.entity.CfgReadonlyFormShow;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.User;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrdispatcher.dao.FieldEtrDispatcherMapper;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrdispatcher.dto.*;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.component.base.entrust.impl.dao.BaseEtrInvestInfoMapper;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.trade.dao.BussRepoSecMapper;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;


/**
 * 场内指令查询
 * @author msk
 * @version 1.0 2021年12月27日
 * @see FieldEtrDispatcherService
 * @since 1.0
 */
@Service("FieldEtrDispatcherService")
public class FieldEtrDispatcherService extends BaseService{

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(FieldEtrDispatcherService.class);

    @Autowired
    private FieldEtrDispatcherMapper fieldEtrDispatcherMapper;

    @Autowired
    private EtrAutoDispenseRuleMapper etrAutoDispenseRuleMapper;

    @Autowired
    private EtrAutoDispenseRuleDetailMapper etrAutoDispenseRuleDetailMapper;

    @Autowired
    private AutoDispenseRightMapper autoDispenseRightMapper;

    @Autowired
    private EtrCommonService etrCommonService;
    /**
     * 自定义债券一级投标委托mapper
     */
    @Autowired
    private EtrBondFirstLBiddingMapper mapper;

    @Autowired
    private IPlatformDate platformDateImpl;

    @Autowired
    private BussRepoSecMapper bussRepoSecMapper;

    /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    /**
     * 委托指令分发信息保存的mapper
     */
    @Autowired
    private EtrDistributeMapper etrDistributeMapper;

    /**
     * 工作流CommonServicemapper
     */
    @Autowired(required = false)
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapper;

    /**
     * 投资委托信息Service
     */
    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;

    /**
     * 投资委托信息Mapper
     */
    @Autowired
    private BaseEtrInvestInfoMapper etrInvestInfoMapper;

    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    /**
     * 状态码，默认为200
     */
    private int statuCode = HttpStatus.CONTINUE.value();

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private CfgReadonlyFormShowMapper cfgReadonlyFormShowMapper;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    /**
     * 指令列表分页查询
     * @param dto 入数
     * @return Page<FieldEtrDispatcherTradeListResponse>
     */
    public Page<FieldEtrDispatcherTradeListResponse> findAllEtrDispatcherCommon(FieldEtrDispatcherTradeListRequest dto) {
        Map<String,Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("entrustAwait", DictCodeList.委托状态_待接收.getValue());
        pageParam.put("entrustDistribute", DictCodeListExt3.委托状态_待分发.getValue());
        pageParam.put("entrustReceive", DictCodeList.委托状态_已接收.getValue());
        pageParam.put("entrustUntread", DictCodeList.委托状态_已完成.getValue());
        pageParam.put("entrustClose", DictCodeList.委托状态_已关闭.getValue());
        pageParam.put("entrustReturn", DictCodeList.委托状态_已退回.getValue());
        pageParam.put("entrustRecall", DictCodeList.委托状态_已撤回.getValue());


        pageParam.put("busiTypePosition01", DictCodeListExt3.头寸业务类型_债券一级投标.getValue());
        pageParam.put("busiTypePosition02", DictCodeListExt3.头寸业务类型_现券交易.getValue());
        pageParam.put("busiTypePosition03", DictCodeListExt3.头寸业务类型_债券行权交易.getValue());

        List<String> listTemp = Arrays.asList(dto.getEntrustStatus());
        if(!listTemp.contains("all")){
            pageParam.put("entrustStatusList", listTemp);
        }
        String portfolioId = (String)pageParam.get("portfolioId");
        if(StringUtil.isNotEmpty(pageParam.get("portfolioId"))){
            List<String> portfolioIdList = StringUtil.splitToList(",",(String)pageParam.get("portfolioId"));
            pageParam.put("portfolioIdList", portfolioIdList);
        }

        String finprodMarketId = (String)pageParam.get("finprodMarketId");
        if(StringUtil.isNotEmpty(pageParam.get("finprodMarketId"))){
            List<String> finprodMarketIdList = StringUtil.splitToList(",",(String)pageParam.get("finprodMarketId"));
            pageParam.put("finprodMarketIdList", finprodMarketIdList);
        }

        String secAcctId = (String)pageParam.get("secAcctId");
        if(StringUtil.isNotEmpty(pageParam.get("secAcctId"))){
            List<String> secAcctIdList = StringUtil.splitToList(",",(String)pageParam.get("secAcctId"));
            pageParam.put("secAcctIdList", secAcctIdList);
        }

        String invManagerId = (String)pageParam.get("invManagerId");
        if(StringUtil.isNotEmpty(pageParam.get("invManagerId"))){
            List<String> invManagerIdList = StringUtil.splitToList(",",(String)pageParam.get("invManagerId"));
            pageParam.put("invManagerIdList", invManagerIdList);
        }

        String finprodType2Old = (String)pageParam.get("finprodType2");
        if (StringUtil.isNotEmpty(finprodType2Old) || StringUtil.isNotEmpty(pageParam.get("ps"))) {
            List<String> finprodType2ListOld = StringUtil.splitToList(GlobalConstant.COMMA, finprodType2Old);
//            pageParam.put("finprodType2List", finprodType2ListOld);
            //交易方向为利率互换 转换
            if(StringUtil.isNotEmpty((String)pageParam.get("ps"))){
                if( StringUtil.equals((String)pageParam.get("ps") , DictCode.互换方式.getValue()+DictCodeListExt2.互换方式_收取固定.getValue()) ){
                    pageParam.put("ps", DictCodeListExt2.互换方式_收取固定.getValue());
                    pageParam.put("IRSps", DictCodeListExt2.互换方式_收取固定.getValue());
                }
                if( StringUtil.equals((String)pageParam.get("ps") , DictCode.互换方式.getValue()+DictCodeListExt2.互换方式_收取浮动.getValue()) ){
                    pageParam.put("ps", DictCodeListExt2.互换方式_收取浮动.getValue());
                    pageParam.put("IRSps", DictCodeListExt2.互换方式_收取浮动.getValue());
                }
                pageParam.put("PS_T01", DictCodeList.交易方向_买入.getValue());
            }
        }
        pageParam.put("IRSType_01", DictCodeListExt2.互换方式_收取固定.getValue());
        pageParam.put("IRSType_02", DictCodeListExt2.互换方式_收取浮动.getValue());
        pageParam.put("ASSET_TYPE_07", DictCodeList.平安资产类型_拆借.getValue());
        pageParam.put("ASSET_TYPE_08", DictCodeList.平安资产类型_质押式回购.getValue());
        pageParam.put("ASSET_TYPE_10", DictCodeList.平安资产类型_买断式回购.getValue());
        pageParam.put("ASSET_TYPE_12", DictCodeListExt2.平安资产类型_债券借贷.getValue());
        pageParam.put("ASSET_TYPE_16", DictCodeListExt2.平安资产类型_利率互换.getValue());
        pageParam.put("BACK_LEND_DIRECTION", DictCode.回购拆借方向.getValue());
        pageParam.put("IRS_TYPE", DictCode.互换方式.getValue());
        pageParam.put("PS", DictCode.交易方向.getValue());
        pageParam.put("effectFlag",DictCodeListExt1.有效状态_有效.getValue());
//        //未完成状态的对应
//        if(StringUtil.isNotEmpty(entrustStatus) ){
//            if(StringUtil.equals("99", entrustStatus)){
//                pageParam.put("entrustStatus1", DictCodeList.委托状态_已接收.getValue());
//                pageParam.put("entrustStatus2", DictCodeList.委托状态_待接收.getValue());
//                pageParam.put("entrustStatus8", DictCodeListExt3.委托状态_待分发.getValue());
//            }
//        }
        pageParam.put("finType_zq", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("ps_49", DictCodeListExt3.交易方向_债转股.getValue());

        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
            pageParam.put("settleSpeed","0");
        }else if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
            pageParam.put("settleSpeed","1");
        }else if(StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_2.getValue())){
            pageParam.put("settleSpeed","2");
        }
        pageParam.put("sort",dto.getPageMode().getSort());
        pageParam.put("direction",dto.getPageMode().getDirection());

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int year = c.get(Calendar.YEAR);
        pageParam.put("year",year);

        //根据组合权限表添加检索权限
        Page<FieldEtrDispatcherTradeListResponse> pageLimit = new Page<FieldEtrDispatcherTradeListResponse>();
        if(StringUtil.isNotEmpty(SecurityUtil.getLoginUser())) {
            pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            pageLimit = super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                    () -> fieldEtrDispatcherMapper.findAllEtrDispatcherCommon(pageParam));
        }else{
            return pageLimit;
        }

        //将交易员和投资经理转为真实姓名
        List<FieldEtrDispatcherTradeListResponse> list = pageLimit.getRoot();
        if(!list.isEmpty()){
            for(FieldEtrDispatcherTradeListResponse  fieldEtrDispatcherTradeListResponse : list){
                if(StringUtil.isNotEmpty(fieldEtrDispatcherTradeListResponse.getTrader())){
                    String[] traders = fieldEtrDispatcherTradeListResponse.getTrader().split(",");
                    StringBuffer buffer = new StringBuffer();
                    StringBuffer userName = new StringBuffer();
                    for (String trader : traders) {
                        userName.append(trader + ",");
                        User user=fieldEtrDispatcherMapper.getRealNameByUserName(trader);
                        if(StringUtil.isNotEmpty(user)){
                            buffer.append(user.getRealName() + ",");
                        }
                    }
                    if(buffer.toString().length() !=0){
                        fieldEtrDispatcherTradeListResponse.setTrader(buffer.toString().substring(0, buffer.toString().length() - 1));
                    }
                }
                if(StringUtil.isNotEmpty(fieldEtrDispatcherTradeListResponse.getInvManagerId())){
                    User user1=fieldEtrDispatcherMapper.getRealNameByUserName(fieldEtrDispatcherTradeListResponse.getInvManagerId());
                    if(StringUtil.isNotEmpty(user1)){
                        fieldEtrDispatcherTradeListResponse.setInvManagerId(user1.getRealName());
                    }
                }
                if(StringUtil.isNotEmpty(fieldEtrDispatcherTradeListResponse.getBusiPs())){
                    //委托指令附属表中取出回购/拆借方向
                    if(StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), fieldEtrDispatcherTradeListResponse.getFinprodType2())
                            || StringUtil.equals(DictCodeList.平安资产类型_拆借.getValue(), fieldEtrDispatcherTradeListResponse.getFinprodType2()) ){
                        fieldEtrDispatcherTradeListResponse.setPs(fieldEtrDispatcherTradeListResponse.getBusiPs());
                    }
                }
            }
        }
        return pageLimit;
    }

    /**
     * 查询交易员指令分发权限配置列表
     * @return List<FieldEtrDispatcherTraderAuthListResponse>
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    public List<FieldEtrDispatcherTraderAuthListResponse> getTraderAuthList( ) {
        List<FieldEtrDispatcherTraderAuthListResponse> list =  fieldEtrDispatcherMapper.getTraderAuthList();
        return list;
    }

    /**
     * 指令信息明细查询
     *
     * @param dto
     * @return Page<FieldEtrDispatcherInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public FieldEtrDispatcherInfoResponse getEtrDispatcherInfo(CommonSearchInfoRequest dto) {
        FieldEtrDispatcherInfoResponse fieldResponseDto =new FieldEtrDispatcherInfoResponse();
        //dto转成map
        Map<String,Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("ASSET_TYPE_07", DictCodeList.平安资产类型_拆借.getValue());
        pageParam.put("ASSET_TYPE_08", DictCodeList.平安资产类型_质押式回购.getValue());
        pageParam.put("ASSET_TYPE_10", DictCodeList.平安资产类型_买断式回购.getValue());
        pageParam.put("ASSET_TYPE_12", DictCodeListExt2.平安资产类型_债券借贷.getValue());
        pageParam.put("YESORNO_YES", DictCodeList.是否_是.getValue());
        pageParam.put("BACK_LEND_DIRECTION", DictCode.回购拆借方向.getValue());
        pageParam.put("PS", DictCode.交易方向.getValue());
//        pageParam.put("SHOW_INVAIM_OR_BUSSINSSMODEL", CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL"));
        List<String> entrustIdList = StringUtil.splitToList(",",(String)pageParam.get("entrustId"));
        if(StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1){
            return new FieldEtrDispatcherInfoResponse();
        }else {
            List<FieldEtrDispatcherInfoResponse> list = fieldEtrDispatcherMapper.getEtrDispatcherInfo(pageParam);
            if(!CollectionUtils.isEmpty(list)){
                fieldResponseDto = list.get(0);
            }
            //一级市场投标增加投标标位List
            if (fieldResponseDto != null) {
                //折算率 计算 * 面值
                BigDecimal pledgeStandardNumber = fieldResponseDto.getPledgeStandardNumber();
                Date sysDate = DateUtil.platDateFromCache();
                String finprodId = fieldResponseDto.getFinprodId();
                if (StringUtil.isNotEmpty(finprodId)) {
                    BigDecimal faceAmt = (BigDecimal) etrCommonService.select券面面值(finprodId, DateUtil.format(sysDate));
                    fieldResponseDto.setPledgeStandardNumber(pledgeStandardNumber.multiply(faceAmt).divide(new BigDecimal(100)));
                }
                if (StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券一级投标.getValue())) {
                    fieldResponseDto.setBidPriceList(mapper.selectBidPriceByKey(pageParam));
                    fieldResponseDto.setEtrBidDate(fieldResponseDto.getEntrustDate());
                } else if (StringUtil.equals(dto.getBusiTypePosition(), "利率互换首期")) {
                    if (fieldResponseDto.getDueDate().equals(" - ")) {
                        fieldResponseDto.setDueDate("");
                    }
                } else if (StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券借贷首期.getValue())
                                ||StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_质押式回购首期.getValue())) {
                    String ps = (String) pageParam.get("ps");
                    if (DictCodeList.回购拆借方向_正回购.getValue().equals(ps) || DictCodeList.回购拆借方向_融入.getValue().equals(ps)) {
                        pageParam.put("POSITION_TYPE_01", DictCodeList.持仓类型_可用.getValue());
                        pageParam.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());
                        pageParam.put("POSITION_TYPE_04", DictCodeList.持仓类型_质押.getValue());
                        pageParam.put("POSITION_TYPE_05", DictCodeList.持仓类型_融入.getValue());
                        pageParam.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
                        pageParam.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
                        pageParam.put("plateDate", platformDateImpl.getSysDateStr());
                        List<PledgeBondInfoDto> pledgeList = bussRepoSecMapper.selectBussRepoSecSellRepo(pageParam);
                        BigDecimal totalPrinAmt = BigDecimal.ZERO;
                        for (PledgeBondInfoDto pbi : pledgeList) {
                            totalPrinAmt = ArithmeticUtil.doAdd(totalPrinAmt, ArithmeticUtil.multiply(pbi.getPledgePrin(), pbi.getPledgeRatio()).divide(new BigDecimal("100")));
                        }
                        fieldResponseDto.setPledgeBondList(pledgeList);
                        fieldResponseDto.setTotalPrinAmt(totalPrinAmt);
                    }
                }else if(StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_现券交易.getValue())){
                    if(StringUtil.equals("01", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"))){
                        fieldResponseDto.setEntrustOwnNum(BigDecimal.ZERO);
                        fieldResponseDto.setIntegrateSecAmount(BigDecimal.ZERO);
                    }

                }
            }
            return fieldResponseDto;
        }
    }

    /**
     * 指令成交明细查询
     *
     * @param dto
     * @return Page<FieldEtrDispatcherExeListInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<FieldEtrDispatcherExeListInfoResponse> getEtrDispatcherExeListInfo(CommonSearchInfoRequest dto) {
        Map<String,Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        pageParam.put("feeType08", BussinessDictCodeList.费用类型_结算手续费.getValue());
        pageParam.put("TRADE_FLOW_STATUS01", DictCodeListExt1.交易流程状态_流程中.getValue());
        pageParam.put("TRADE_FLOW_STATUS02", DictCodeListExt1.交易流程状态_有效.getValue());
        pageParam.put("TRADE_FLOW_STATUS03", DictCodeListExt1.交易流程状态_撤销.getValue());
        pageParam.put("quoteType", BussinessDictCodeList.报价方式_登记报价.getValue());
        List<String> entrustIdList = StringUtil.splitToList(",",(String)pageParam.get("entrustId"));
        if(StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1){
            return new Page<FieldEtrDispatcherExeListInfoResponse>();
        }
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                () -> fieldEtrDispatcherMapper.getEtrDispatcherExeListInfo(pageParam));
    }

    /**
     * 指令成交明细查询
     *
     * @param dto
     * @return Page<FieldEtrDispatcherDealListInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<FieldEtrDispatcherDealListInfoResponse> getEtrDispatcherDealListInfo(CommonSearchInfoRequest dto) {
        Map<String,Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        pageParam.put("feeType08", BussinessDictCodeList.费用类型_结算手续费.getValue());
        pageParam.put("TRADE_FLOW_STATUS01", DictCodeListExt1.交易流程状态_流程中.getValue());
        pageParam.put("TRADE_FLOW_STATUS02", DictCodeListExt1.交易流程状态_有效.getValue());
        pageParam.put("TRADE_FLOW_STATUS03", DictCodeListExt1.交易流程状态_撤销.getValue());
        List<String> entrustIdList = StringUtil.splitToList(",",(String)pageParam.get("entrustId"));
        if(StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1){
            return new Page<FieldEtrDispatcherDealListInfoResponse>();
        }
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                () -> fieldEtrDispatcherMapper.getEtrDispatcherDealListInfo(pageParam));
    }
    
    /**
     * 交易员列表模糊查询
     * @param pageParam
     * @return
     */
    public List<FieldEtrDispatcherTraderListRequest> getEtrTraderList(Map<String, Object> pageParam) {
        String dealers = (String)pageParam.get("search");
        List<String> list=new ArrayList<>();
//        list.add(DictCodeList.角色岗位_交易处长.getValue());
        list.add(DictCodeList.角色岗位_交易员.getValue());
//        list.add(DictCodeList.角色岗位_交易员_资本境内_岗.getValue());
//        list.add(DictCodeList.角色岗位_交易员_资本境外_岗.getValue());
        pageParam.put("rolePosList", list);
        pageParam.put("dealers", dealers);
        return fieldEtrDispatcherMapper.getEtrTraderList(pageParam);
    }

    /**
     * 交易员与工作量列表查询
     * @param pageParam
     * @return
     */
    public List<FieldEtrDispatcherTraderListRequest> getEtrTraderAndWorkloadList(Map<String, Object> pageParam) {
        List<FieldEtrDispatcherTraderListRequest> list = fieldEtrDispatcherMapper.getEtrTraderAndWorkloadList(pageParam);
        return list;
    }

    /**
     * 自动分发规则查询
     * @return FieldEtrDispatcherAutoRequest
     * @author sjj
     */
    public FieldEtrDispatcherAutoRequest findAutoDetail() {
        FieldEtrDispatcherAutoRequest request = fieldEtrDispatcherMapper.findAutoDetail(DateUtil.format(iPlatformDate.getSysDate()));
        if(request == null){
            request = new FieldEtrDispatcherAutoRequest();
            request.setDistributeRule("01");
        }
        return request;
    }

    /**
     * 只读表单设置及数据查询_指令详情
     * @param pageCode
     * @param dtCode
     * @param spcdCode
     * @return
     */
    public Map<String, Object> getReadonlyFormEtrInfo(String pageCode, String dtCode, String spcdCode, CommonSearchInfoRequest dto) {
        if ("null".equals(spcdCode) && Objects.nonNull(dto) && StringUtil.isNotEmpty(dto.getEntrustId())) {
            String busiTypePosition = fieldEtrDispatcherMapper.getBusiTypePosition(dto.getEntrustId());
            logger.debug("fieldEtrDispatcherMapper getBusiTypePosition with entrustId = [{}], result busiTypePosittion = [{}]", dto.getEntrustId(), busiTypePosition);
            if (StringUtil.isEmpty(busiTypePosition)) {
                dto.setBusiTypePosition(StringUtil.EMPTY);
            } else {
                dto.setBusiTypePosition(busiTypePosition);
                spcdCode = busiTypePosition;
            }
        }
        Map<String, Object> resultData = new HashMap<>();
        List<CfgReadonlyFormShow>  infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
        if(StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(),dto.getFinprodType2()) &&
                (StringUtil.equals(DictCodeListExt3.投管业务类型_大宗交易.getValue(),dto.getBusiTypePosition()) ||
                StringUtil.equals(DictCodeListExt3.投管业务类型_竞价交易.getValue(),dto.getBusiTypePosition()))){
            spcdCode = DictCodeList.平安资产类型_债券.getValue() + spcdCode;
            infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode));
        }else if(StringUtil.equals(DictCodeList.平安资产类型_基金.getValue(),dto.getFinprodType2()) &&
                (StringUtil.equals(DictCodeListExt3.投管业务类型_大宗交易.getValue(),dto.getBusiTypePosition()) ||
                        StringUtil.equals(DictCodeListExt3.投管业务类型_竞价交易.getValue(),dto.getBusiTypePosition())   )){
            spcdCode = DictCodeList.平安资产类型_基金.getValue() + spcdCode;
            infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode));    
        }else if(StringUtil.equals(BussinessDictCodeList.平安资产类型_股票.getValue(),dto.getFinprodType2()) &&
                (StringUtil.equals(DictCodeListExt3.投管业务类型_大宗交易.getValue(),dto.getBusiTypePosition()) ||
                        StringUtil.equals(DictCodeListExt3.投管业务类型_竞价交易.getValue(),dto.getBusiTypePosition()))){
            spcdCode = BussinessDictCodeList.平安资产类型_股票.getValue() + spcdCode;
            infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode));
        }else if(StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(),dto.getFinprodType2()) &&
                    StringUtil.equals(DictCodeListExt3.投管业务类型_竞价交易.getValue(),dto.getBusiTypePosition())){
            spcdCode = DictCodeList.平安资产类型_质押式回购.getValue() + spcdCode;
            infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode));
        }
        //排序
        Collections.sort(infoForm, new Comparator<CfgReadonlyFormShow>() {
            @Override
            public int compare(CfgReadonlyFormShow o1, CfgReadonlyFormShow o2) {
                return o1.getSeqno().compareTo(o2.getSeqno());
            }
        });
        FieldEtrDispatcherInfoResponse fieldResponseDto = this.getEtrDispatcherInfo(dto);
//        String showInvaimOrBussinssmodel = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        if(null != showInvaimOrBussinssmodel && showInvaimOrBussinssmodel.equals("02")){
//            infoForm.forEach(field->{
//                if(field.getColname().equals("投资目的")){
//                    field.setColname("业务模式");
//                }
//            });
//        }
        resultData.put(dtCode+"_form",infoForm);
        resultData.put(dtCode+"_data",fieldResponseDto);
        return resultData;
    }
}
