/*
 * 文件名：CfetsEtrDispatcherService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：sjj
 * 修改时间：2021年11月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.etrdispatcher.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.JoyinMessage;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.etrbondfirstlbidding.dao.EtrBondFirstLBiddingMapper;
import com.joyintech.service.entrust.etrbondfirstlbidding.dto.EtrBondFirstLBiddingDto;
import com.joyintech.service.entrust.etrbondfirstlbidding.service.EtrBondFirstLBiddingService;
import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EntrCommandDispatcherDto;
import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EntrCommandDispatcherTotalDto;
import com.joyintech.service.trade.investtrade.commanddispatcher.service.EntrCommandDispatcherService;
import com.joyintech.tams.business.common.dao.CfgDataTableMapper;
import com.joyintech.tams.business.common.dao.CfgReadonlyFormShowMapper;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.dto.CommonSearchInfoRequest;
import com.joyintech.tams.business.common.entity.CfgDataTableShow;
import com.joyintech.tams.business.common.entity.CfgReadonlyFormShow;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtribRepoInfo003Dto;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsPledgeBondInfoDto;
import com.joyintech.tams.cfets.etr.repo.service.CfetsEtrIbRepoinfoService;
import com.joyintech.tams.cfets.etrdispatcher.dao.CfetsEtrDispatcherMapper;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherAutoRequest;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherDealListInfoResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherDealRequest;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherExeListInfoResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherInfoExcResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherInfoOtcResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherInfoResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherInvResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherTradeListRequest;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherTradeListResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherTraderAuthListResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherTraderListRequest;
import com.joyintech.tams.common.base.dao.AutoDispenseRightMapper;
import com.joyintech.tams.common.base.dao.EtrAutoDispenseRuleDetailMapper;
import com.joyintech.tams.common.base.dao.EtrAutoDispenseRuleMapper;
import com.joyintech.tams.common.base.dao.EtrDistributeMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.entity.AutoDispenseRight;
import com.joyintech.tams.common.base.entity.EtrAutoDispenseRule;
import com.joyintech.tams.common.base.entity.EtrAutoDispenseRuleDetail;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrDistributeKey;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustBidPrice;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.User;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.business.riskrds.constant.InterfUtil;
import com.joyintech.tams.component.business.riskrds.constant.PageFieldToInterfFactory;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.component.base.entrust.impl.dao.BaseEtrInvestInfoMapper;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.BussRepoSecMapper;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.util.DictCodeUtil;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.trade.common.aop.TradeRdsCommonAop;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * 场外指令分发
 *
 * @author sjj
 * @version 1.0 2021年10月26日
 * @see CfetsEtrDispatcherService
 * @since 1.0
 */
@Service("CfetsEtrDispatcherService")
public class CfetsEtrDispatcherService extends BaseService {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(CfetsEtrDispatcherService.class);

    @Autowired
    private CfetsEtrDispatcherMapper cfetsEtrDispatcherMapper;

    @Autowired
    private EtrAutoDispenseRuleMapper etrAutoDispenseRuleMapper;

    @Autowired
    private EtrAutoDispenseRuleDetailMapper etrAutoDispenseRuleDetailMapper;

    @Autowired
    private AutoDispenseRightMapper autoDispenseRightMapper;
    /**
     * 自定义债券一级投标委托mapper
     */
    @Autowired(required = false)
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

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;

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
     * 指令推送信息查询service
     */
    @Autowired(required = false)
    private EntrCommandDispatcherService entrCommandDispatcherService;
    @Autowired
    private CfgDataTableMapper cfgDataTableMapper;

    @Autowired
    private CfetsEtrIbRepoinfoService cfetsEtrIbRepoinfoService;

    /**
     * FileAttachmentProcess
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    @Autowired(required = false)
    private EtrBondFirstLBiddingService etrBondFirstLBiddingService;
    @Autowired
    private EtrCommonService etrCommonService;

    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * 指令列表分页查询
     *
     * @param dto 入数
     * @return Page<CfetsEtrDispatcherTradeListResponse>
     */
    public Page<CfetsEtrDispatcherTradeListResponse> findAllEtrDispatcherCommon(CfetsEtrDispatcherTradeListRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
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
        pageParam.put("busiTypePosition05", DictCodeListExt3.头寸业务类型_质押式回购首期.getValue());


        List<String> listTemp = Arrays.asList(dto.getEntrustStatus());
        if (!listTemp.contains("all")) {
            pageParam.put("entrustStatusList", listTemp);
        }
        String portfolioId = (String) pageParam.get("portfolioId");
        if (StringUtil.isNotEmpty(pageParam.get("portfolioId"))) {
            List<String> portfolioIdList = StringUtil.splitToList(",", (String) pageParam.get("portfolioId"));
            pageParam.put("portfolioIdList", portfolioIdList);
        }

        String finprodMarketId = (String) pageParam.get("finprodMarketId");
        if (StringUtil.isNotEmpty(pageParam.get("finprodMarketId"))) {
            List<String> finprodMarketIdList = StringUtil.splitToList(",", (String) pageParam.get("finprodMarketId"));
            pageParam.put("finprodMarketIdList", finprodMarketIdList);
        }

        String secAcctId = (String) pageParam.get("secAcctId");
        if (StringUtil.isNotEmpty(pageParam.get("secAcctId"))) {
            List<String> secAcctIdList = StringUtil.splitToList(",", (String) pageParam.get("secAcctId"));
            pageParam.put("secAcctIdList", secAcctIdList);
        }

        String invManagerId = (String) pageParam.get("invManagerId");
        if (StringUtil.isNotEmpty(pageParam.get("invManagerId"))) {
            List<String> invManagerIdList = StringUtil.splitToList(",", (String) pageParam.get("invManagerId"));
            pageParam.put("invManagerIdList", invManagerIdList);
        }

        String finprodType2Old = (String) pageParam.get("finprodType2");
        //资产类型转换 同业存单转换为债券
        if (StringUtil.isNotEmpty(finprodType2Old)) {
            if (DictCodeList.平安资产类型_同业存单.getValue().equals(finprodType2Old)) {
                pageParam.put("finprodType2", DictCodeList.平安资产类型_债券.getValue());
                pageParam.put("TYPE_9", "01");
            }
        }
        if (StringUtil.isNotEmpty(finprodType2Old) || StringUtil.isNotEmpty(pageParam.get("ps"))) {
            List<String> finprodType2ListOld = StringUtil.splitToList(GlobalConstant.COMMA, finprodType2Old);
//            pageParam.put("finprodType2List", finprodType2ListOld);
            //交易方向为利率互换 转换
            if (StringUtil.isNotEmpty((String) pageParam.get("ps"))) {
                if (StringUtil.equals((String) pageParam.get("ps"), DictCode.互换方式.getValue() + DictCodeListExt2.互换方式_收取固定.getValue())) {
                    pageParam.put("ps", DictCodeListExt2.互换方式_收取固定.getValue());
                    pageParam.put("IRSps", DictCodeListExt2.互换方式_收取固定.getValue());
                }
                if (StringUtil.equals((String) pageParam.get("ps"), DictCode.互换方式.getValue() + DictCodeListExt2.互换方式_收取浮动.getValue())) {
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
        pageParam.put("effectFlag", DictCodeListExt1.有效状态_有效.getValue());
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

        if (StringUtil.equals(dto.getSettleSpeed(), DictCodeList.清算速度_T_0.getValue())) {
            pageParam.put("settleSpeed", "0");
        } else if (StringUtil.equals(dto.getSettleSpeed(), DictCodeList.清算速度_T_1.getValue())) {
            pageParam.put("settleSpeed", "1");
        } else if (StringUtil.equals(dto.getSettleSpeed(), DictCodeList.清算速度_T_2.getValue())) {
            pageParam.put("settleSpeed", "2");
        }
        pageParam.put("sort", dto.getPageMode().getSort());
        pageParam.put("direction", dto.getPageMode().getDirection());

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int year = c.get(Calendar.YEAR);
        pageParam.put("year", year);

        //根据组合权限表添加检索权限
        Page<CfetsEtrDispatcherTradeListResponse> pageLimit = new Page<>();
        if (StringUtil.isNotEmpty(SecurityUtil.getLoginUser())) {
//            pageParam.put("userId", "li02");
            pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            pageLimit = super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                    () -> cfetsEtrDispatcherMapper.findAllEtrDispatcherCommon(pageParam));
        } else {
            return pageLimit;
        }

        //将交易员和投资经理转为真实姓名
        List<CfetsEtrDispatcherTradeListResponse> list = pageLimit.getRoot();
        if (!list.isEmpty()) {
            for (CfetsEtrDispatcherTradeListResponse response : list) {
                if (StringUtil.isNotEmpty(response.getTrader())) {
                    String[] traders = response.getTrader().split(",");
                    StringBuffer buffer = new StringBuffer();
                    StringBuffer userName = new StringBuffer();
                    for (String trader : traders) {
                        userName.append(trader + ",");
                        User user = cfetsEtrDispatcherMapper.getRealNameByUserName(trader);
                        if (StringUtil.isNotEmpty(user)) {
                            buffer.append(user.getRealName() + ",");
                        }
                    }
                    if (buffer.toString().length() != 0) {
                        response.setTrader(buffer.toString().substring(0, buffer.toString().length() - 1));
                    }
//                    if(userName.toString().length() !=0){
//                        cfetsEtrDispatcherTradeListResponse.setUserName(userName.toString().substring(0,userName.toString().length() - 1));
//                    }
                }
                if (StringUtil.isNotEmpty(response.getInvManagerId())) {
                    User user1 = cfetsEtrDispatcherMapper.getRealNameByUserName(response.getInvManagerId());
                    if (StringUtil.isNotEmpty(user1)) {
                        response.setInvManagerId(user1.getRealName());
                    }
                }
                if (StringUtil.isNotEmpty(response.getBusiPs())) {
                    //委托指令附属表中取出回购/拆借方向
                    if (StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), response.getFinprodType2())
                            || StringUtil.equals(DictCodeList.平安资产类型_拆借.getValue(), response.getFinprodType2())) {
                        response.setPs(response.getBusiPs());
                    }
                }
                //这里特殊处理成交进度和成交额度,如果场内和场外的资产类型重合了，考虑用业务类型或者交易场所进行区分，不能单纯的排除某一种类型
                if(DictCodeUtil.equals(response.getFinprodType2(),BussinessDictCodeList.平安资产类型_一级投标债券,BussinessDictCodeList.平安资产类型_计划类资产_利率型,
                        BussinessDictCodeList.平安资产类型_计划类资产_货币型,BussinessDictCodeList.平安资产类型_计划类资产_净值型,BussinessDictCodeList.平安资产类型_拆借,BussinessDictCodeList.平安资产类型_买断式回购,
                        BussinessDictCodeList.平安资产类型_存款,BussinessDictCodeList.平安资产类型_项目类资产)
                        || (DictCodeUtil.equals(response.getFinprodType2(),DictCodeList.平安资产类型_质押式回购)
                            && (StringUtil.equals(response.getBusiTypePosition(),DictCodeListExt3.头寸业务类型_质押式回购首期.getValue()) || StringUtil.equals(response.getBusiTypePosition(),DictCodeListExt3.头寸业务类型_质押式回购到期.getValue())))
                        || StringUtil.equals(response.getBusiTypePosition(),DictCodeListExt3.头寸业务类型_现券交易.getValue())
                        || (StringUtil.equals(response.getFinprodType2(),BussinessDictCodeList.平安资产类型_基金.getValue()) && StringUtil.isEmpty(response.getBusiTypePosition()))) {
                    DecimalFormat df = new DecimalFormat("0.##");
                    response.setDealProgress(ArithmeticUtil.divide(response.getFinishAmt(), response.getEntrustAmt(), 4).multiply(new BigDecimal("100")));
                    response.setTransRemainderAmt(new BigDecimal(df.format(ArithmeticUtil.divide(ArithmeticUtil.subtract(response.getEntrustAmt(), response.getFinishAmt()), new BigDecimal("10000"), 4))));
                    response.setTransFinishAmt(new BigDecimal(df.format(ArithmeticUtil.divide(response.getFinishAmt(), new BigDecimal("10000"), 4))));
                }
                if(StringUtil.equals(response.getFinprodType2(), BussinessDictCodeList.平安资产类型_一级投标债券.getValue())){
                    response.setPs(null);
                    CfetsEtrDispatcherTradeListResponse dto2 = cfetsEtrDispatcherMapper.getEtrBidInfo(response.getEntrustId());
                    if(StringUtil.isNotEmpty(dto2)) {
                        response.setIsWin(dto2.getIsWin());
                        response.setEtrPrice(dto2.getEtrPrice());
                       /* CfetsEtrDispatcherTradeListResponse deal =  cfetsEtrDispatcherMapper.getEtrDealInfo(response.getEntrustId());
                        if(Objects.nonNull(deal)) {
                            response.setDealProgress(deal.getDealProgress());
                            response.setTransFinishAmt(deal.getTransFinishAmt());
                            response.setTransRemainderAmt(deal.getTransRemainderAmt());
                        }*/
                    }
                }

            }
        }
        return pageLimit;
    }



    /**
     * 自动分发规则、参数设置check
     *
     * @param cfetsDto br
     * @return Message<AjaxResult>
     * @author sjj
     */
    public String checkSave(CfetsEtrDispatcherAutoRequest cfetsDto, BindingResult br) {
        String returnMsg = "";
        if (StringUtil.isEmpty(cfetsDto.getVdate())) {
            returnMsg = "请选择分发日期";
        }
        return returnMsg;
    }

    /**
     * 删除自动分发的日期
     *
     * @return int
     */
    public void deleteEtrAutoTime() {
        cfetsEtrDispatcherMapper.deleteEtrAutoRule();
        cfetsEtrDispatcherMapper.deleteEtrAutoRuleDetail();
    }

    /**
     * 插入自动分发的日期
     *
     * @param cfetsDto
     * @return int
     */
    public int insertEtrAutoTime(CfetsEtrDispatcherAutoRequest cfetsDto) {
        try {
            String generate = IDGenerator.generate(32);
            EtrAutoDispenseRule rule = new EtrAutoDispenseRule();
            rule.setRuleId(generate);
            rule.setVdate(DateUtil.parse(cfetsDto.getVdate()));
            rule.setMdate(DateUtil.parse(cfetsDto.getMdate()));
            rule.setDispenseRule(cfetsDto.getDistributeRule());
            etrAutoDispenseRuleMapper.insertSelective(rule);
            if (StringUtil.isNotBlank(cfetsDto.getDistributeParameter())) {
                String[] split = cfetsDto.getDistributeParameter().split(",");
                if (split.length > 0) {
                    EtrAutoDispenseRuleDetail detail = new EtrAutoDispenseRuleDetail();
                    for (String str : split) {
                        detail.setRuleId(generate);
                        detail.setDispensePara(str);
                        etrAutoDispenseRuleDetailMapper.insertSelective(detail);
                    }
                }
            }
            return 1;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 查询交易员指令分发权限配置列表
     *
     * @return List<CfetsEtrDispatcherTraderAuthListResponse>
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    public List<CfetsEtrDispatcherTraderAuthListResponse> getTraderAuthList() {
        List<CfetsEtrDispatcherTraderAuthListResponse> list = cfetsEtrDispatcherMapper.getTraderAuthList();
        return list;
    }

    public AjaxResult checkSaveTraderAuthList(List<CfetsEtrDispatcherTraderAuthListResponse> dtoList) {
        AjaxResult ajaxResult = new AjaxResult();
        String returnMsg = "";
        List<String> selectList = new ArrayList<String>();//存入所有交易员已选择的投资品种类
        List<String> allList = new ArrayList<String>();//存入所有的投资品种类
        if (!StringUtil.isEmptyList(dtoList) && dtoList.size() > 0) {
            dtoList.forEach(item -> {
                if (StringUtil.isNotEmpty(item.getFinprodType2())) {
                    List<String> itemList = Arrays.asList(item.getFinprodType2().split(GlobalConstant.COMMA));
                    itemList.forEach(ii ->
                    {
                        if (!selectList.contains(ii)) {
                            selectList.add(ii);
                        }
                    });
                }
            });
        }
        List<? extends DataDictItem> dataDictItemlist = SysDataUtil.getDataDictItem(DictCode.平安资产类型.getValue());
        if (!StringUtil.isEmptyList(dataDictItemlist) && dataDictItemlist.size() > 0) {
            dataDictItemlist.forEach(data ->
                    {
                        allList.add(data.getItemCode());
                    }
            );
        }
        //获取未被选择的投资品，然后分配到所有的交易员上面
        List<String> noSelectList = allList.stream().filter(item -> !selectList.contains(item)).collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        if (!StringUtil.isEmptyList(noSelectList) && noSelectList.size() > 0) {
            if (!StringUtil.isEmptyList(noSelectList)) {
                noSelectList.forEach(entity ->
                {
                    stringBuilder.append(SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, entity) + "、");
                });
            }
        }
        if (StringUtil.isNotEmpty(stringBuilder.toString())) {
            returnMsg = "[" + stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1) + "]品种权限未选，将默认所有交易员都有此权限";
        }
        ajaxResult.setMessage(returnMsg);
        ajaxResult.setStatuCode(statuCode);
        return ajaxResult;
    }

    /**
     * 分发权限配置提交
     *
     * @return Page<CfetsEtrDispatcherTradeListResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public AjaxResult doSaveTraderAuthList(List<CfetsEtrDispatcherTraderAuthListResponse> dtoList) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        List<String> selectList = new ArrayList<String>();//存入所有交易员已选择的投资品种类
        List<String> allList = new ArrayList<String>();//存入所有的投资品种类
        if (!StringUtil.isEmptyList(dtoList) && dtoList.size() > 0) {
            dtoList.forEach(item -> {

                if (StringUtil.isNotEmpty(item.getFinprodType2())) {
                    List<String> itemList = Arrays.asList(item.getFinprodType2().split(GlobalConstant.COMMA));
                    itemList.forEach(ii ->
                    {
                        if (!selectList.contains(ii)) {
                            selectList.add(ii);
                        }
                    });
                }
            });
        }
        List<? extends DataDictItem> dataDictItemlist = SysDataUtil.getDataDictItem(DictCode.平安资产类型.getValue());
        if (!StringUtil.isEmptyList(dataDictItemlist) && dataDictItemlist.size() > 0) {
            dataDictItemlist.forEach(data ->
                    {
                        allList.add(data.getItemCode());

                    }
            );
        }
        //获取未被选择的投资品，然后分配到所有的交易员上面
        List<String> noSelectList = allList.stream().filter(item -> !selectList.contains(item)).collect(Collectors.toList());

        //新增交易员分配的权限【先删除后添加】
        try {
            cfetsEtrDispatcherMapper.deleteTraderAuthList();

            if (!StringUtil.isEmptyList(dtoList) && dtoList.size() > 0) {
                dtoList.forEach(item -> {

                    //添加原来选择的
                    if (StringUtil.isNotEmpty(item.getFinprodType2())) {
                        List<String> itemList = Arrays.asList(item.getFinprodType2().split(GlobalConstant.COMMA));
                        itemList.forEach(ii ->
                        {
                            AutoDispenseRight autoDispenseRight = new AutoDispenseRight();
                            autoDispenseRight.setUserId(item.getUserName());
                            autoDispenseRight.setAssetType(ii);
                            autoDispenseRightMapper.insert(autoDispenseRight);
                        });
                    }
                    // 添加未分配的默认项
                    if (!StringUtil.isEmptyList(noSelectList)) {
                        noSelectList.forEach(entity ->
                        {
                            AutoDispenseRight autoDispenseRightEntity = new AutoDispenseRight();
                            autoDispenseRightEntity.setUserId(item.getUserName());
                            autoDispenseRightEntity.setAssetType(entity);
                            autoDispenseRightMapper.insert(autoDispenseRightEntity);
                        });
                    }
                });
            }
        } catch (Exception e) {
            errorList.add(new ValidMessage("分发权限配置异常！请联系管理员"));
            ajaxResult.setErrorList(errorList);
        }
        return ajaxResult;
    }

    /**
     * 指令信息明细查询
     *
     * @param dto
     * @return Page<CfetsEtrDispatcherInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public CfetsEtrDispatcherInfoResponse getEtrDispatcherInfo(CommonSearchInfoRequest dto) {
        CfetsEtrDispatcherInfoResponse cfetsResponseDto = new CfetsEtrDispatcherInfoResponse();
        //dto转成map
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("YESORNO_YES", DictCodeList.是否_是.getValue());
        List<String> entrustIdList = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        if (StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1) {
            return new CfetsEtrDispatcherInfoResponse();
        } else {
            cfetsResponseDto = cfetsEtrDispatcherMapper.getEtrDispatcherInfo(pageParam);
            //一级市场投标增加投标标位List
            if (cfetsResponseDto != null) {
                //组装组合名称
                if (StringUtil.isNotEmpty(cfetsResponseDto.getPortfolioId())) {
                    String portfolioNameByIds = getPortfolioNameByIds(cfetsResponseDto.getPortfolioId());
                    cfetsResponseDto.setPortfolioName(portfolioNameByIds);
                }
                if (StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券一级投标.getValue())) {
                    cfetsResponseDto.setBidPriceList(mapper.selectBidPriceByKey(pageParam));
                    cfetsResponseDto.setEtrBidDate(cfetsResponseDto.getEntrustDate());
                } else if (StringUtil.equals(dto.getBusiTypePosition(), "利率互换首期")) {
                    if (cfetsResponseDto.getDueDate().equals(" - ")) {
                        cfetsResponseDto.setDueDate("");
                    }
                } else if (StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券借贷首期.getValue())
                        || StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_质押式回购首期.getValue())) {
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
                        cfetsResponseDto.setPledgeBondList(pledgeList);
                        cfetsResponseDto.setTotalPrinAmt(ArithmeticUtil.divide(totalPrinAmt, new BigDecimal("10000"), 4));

                    }
                } else if (StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_现券交易.getValue())) {
                    if (StringUtil.equals("01", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"))) {
                        cfetsResponseDto.setEntrustOwnNum(BigDecimal.ZERO);
                        cfetsResponseDto.setIntegrateSecAmount(BigDecimal.ZERO);
                    }

                }
            }
            //cfetsResponseDto.setRepoPledgeModel(CacheUtil.getSysParam(CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL)));
            return cfetsResponseDto;
        }
    }

    /**
     * 指令成交明细查询
     *
     * @param dto
     * @return Page<CfetsEtrDispatcherExeListInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<CfetsEtrDispatcherExeListInfoResponse> getEtrDispatcherExeListInfo(CommonSearchInfoRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        pageParam.put("feeType08", BussinessDictCodeList.费用类型_结算手续费.getValue());
        pageParam.put("TRADE_FLOW_STATUS01", DictCodeListExt1.交易流程状态_流程中.getValue());
        pageParam.put("TRADE_FLOW_STATUS02", DictCodeListExt1.交易流程状态_有效.getValue());
        pageParam.put("TRADE_FLOW_STATUS03", DictCodeListExt1.交易流程状态_撤销.getValue());
        pageParam.put("quoteType", BussinessDictCodeList.报价方式_登记报价.getValue());
        List<String> entrustIdList = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        if (StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1) {
            return new Page<CfetsEtrDispatcherExeListInfoResponse>();
        }
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                () -> cfetsEtrDispatcherMapper.getEtrDispatcherExeListInfo(pageParam));
    }

    public Page<CfetsEtrDispatcherExeListInfoResponse> getEtrDispatcherExeListInfoByExe(CommonSearchInfoRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("instructionId", dto.getEntrustId());
        Page<CfetsEtrDispatcherExeListInfoResponse> page = super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                () -> cfetsEtrDispatcherMapper.queryEntrust2Touguan(pageParam));
        if (!CollectionUtils.isEmpty(page.getRoot())) {
            for (CfetsEtrDispatcherExeListInfoResponse response : page.getRoot()) {
                if (response.getEntrustBs().equals(DictCodeListExt3.交易方向_质押券出库.getValue())
                || response.getEntrustBs().equals(DictCodeListExt3.交易方向_质押券入库.getValue())) {
                    response.setEntrustPrice(StringUtil.isNotEmpty(response.getEntrustPrice())?ArithmeticUtil.multiply(response.getEntrustPrice(),BigDecimal.valueOf(100)):null);
                    response.setTradedPrice(StringUtil.isNotEmpty(response.getTradedPrice())?ArithmeticUtil.multiply(response.getTradedPrice(),BigDecimal.valueOf(100)):null);
                }
                response.setTradingDay(StringUtil.isNotEmpty(response.getTradingDay())?DateUtil.format(DateUtil.parse(response.getTradingDay()),DateUtil.DEFAULT_DATE_FORMAT_EN):null);
            }
        }
        return page;
    }

    /**
     * 指令成交明细查询
     *
     * @param dto
     * @return Page<CfetsEtrDispatcherDealListInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<CfetsEtrDispatcherDealListInfoResponse> getEtrDispatcherDealListInfo(CommonSearchInfoRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        pageParam.put("feeType08", BussinessDictCodeList.费用类型_结算手续费.getValue());
        pageParam.put("TRADE_FLOW_STATUS01", DictCodeListExt1.交易流程状态_流程中.getValue());
        pageParam.put("TRADE_FLOW_STATUS02", DictCodeListExt1.交易流程状态_有效.getValue());
        pageParam.put("TRADE_FLOW_STATUS03", DictCodeListExt1.交易流程状态_撤销.getValue());
        List<String> entrustIdList = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        if (StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1) {
            return new Page<CfetsEtrDispatcherDealListInfoResponse>();
        }
        Page<CfetsEtrDispatcherDealListInfoResponse> page = null;
        if(StringUtil.equals(dto.getBusiTypePosition(),DictCodeListExt3.头寸业务类型_债券一级投标.getValue())) {
            //这里处理一级投标
            page = super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsEtrDispatcherMapper.getEtrDispatcherBidDealListInfo(pageParam));
            for(CfetsEtrDispatcherDealListInfoResponse response : page.getRoot()) {
                CfetsEtrDispatcherTradeListResponse dto2 = cfetsEtrDispatcherMapper.getEtrBidInfo(response.getEntrustId());
                if(StringUtil.isNotEmpty(dto2)) {
                    response.setIsWin(dto2.getIsWin());
                    response.setBidprice(dto2.getEtrPrice());
                }
            }
        }else {
            page = super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsEtrDispatcherMapper.getEtrDispatcherDealListInfo(pageParam));
        }
        if ("02".equals(CacheUtil.getSysParam("repo_pledge_model")) && "05".equals(dto.getBusiTypePosition())) {
            //处理质押券明细
            CfetsEtribRepoInfo003Dto item = new CfetsEtribRepoInfo003Dto();
            item.setEntrustId(dto.getEntrustId());
            CfetsEtribRepoInfo003Dto result = cfetsEtrIbRepoinfoService.findEtrinfo(item);
            if (result != null) {
                List<CfetsPledgeBondInfoDto> detailList = result.getPledgeBondTotalListDetailed();
                if (!CollectionUtils.isEmpty(detailList)) {
                    StringBuffer sb = new StringBuffer("");
                    for (CfetsPledgeBondInfoDto infoDto : detailList) {
                        infoDto.setPledgePrin(ArithmeticUtil.divide(infoDto.getPledgePrin(), new BigDecimal("10000"), 2));
                        infoDto.setPledStrdPrinAmt(ArithmeticUtil.divide(infoDto.getPledStrdPrinAmt(), new BigDecimal("10000"), 2));
                        sb.append(infoDto.getFinprodAbbr()).append(",");
                    }
                    String finprodAbbr = sb.substring(0, sb.length() - 1);
                    for (CfetsEtrDispatcherDealListInfoResponse response : page.getRoot()) {
                        response.setPledgeStockName(finprodAbbr);
                        response.setDetailList(detailList);
                    }
                }
            }
        }
        return page;
    }

    public Page<CfetsEtrDispatcherDealListInfoResponse> getEtrDispatcherDealListInfoByExe(CommonSearchInfoRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("instructionId", dto.getEntrustId());
        Page<CfetsEtrDispatcherDealListInfoResponse> page = super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                () -> cfetsEtrDispatcherMapper.queryTrade2Touguan(pageParam));
        if (!CollectionUtils.isEmpty(page.getRoot())) {
            for (CfetsEtrDispatcherDealListInfoResponse response : page.getRoot()) {
                if (response.getEntrustBs().equals(DictCodeListExt3.交易方向_质押券出库.getValue())
                        || response.getEntrustBs().equals(DictCodeListExt3.交易方向_质押券入库.getValue())) {
                    response.setTradedPrice(StringUtil.isNotEmpty(response.getTradedPrice())?ArithmeticUtil.multiply(response.getTradedPrice(),BigDecimal.valueOf(100)):null);
                }
                response.setTradingDay(StringUtil.isNotEmpty(response.getTradingDay())?DateUtil.format(DateUtil.parse(response.getTradingDay()),DateUtil.DEFAULT_DATE_FORMAT_EN):null);
            }

        }
        return page;
    }

    /**
     * 交易员列表模糊查询
     *
     * @param pageParam
     * @return
     */
    public List<CfetsEtrDispatcherTraderListRequest> getEtrTraderList(Map<String, Object> pageParam) {
        String dealers = (String) pageParam.get("search");
        List<String> list = new ArrayList<>();
//        list.add(DictCodeList.角色岗位_交易处长.getValue());
        list.add(DictCodeList.角色岗位_交易员.getValue());
//        list.add(DictCodeList.角色岗位_交易员_资本境内_岗.getValue());
//        list.add(DictCodeList.角色岗位_交易员_资本境外_岗.getValue());
        pageParam.put("rolePosList", list);
        pageParam.put("dealers", dealers);
        return cfetsEtrDispatcherMapper.getEtrTraderList(pageParam);
    }

    /**
     * 交易员与工作量列表查询
     *
     * @param pageParam
     * @return
     */
    public List<CfetsEtrDispatcherTraderListRequest> getEtrTraderAndWorkloadList(Map<String, Object> pageParam) {
        pageParam.put("sysDate", iPlatformDate.getSysDateStr());
        List<CfetsEtrDispatcherTraderListRequest> list = cfetsEtrDispatcherMapper.getEtrTraderAndWorkloadList(pageParam);
        return list;
    }

    /**
     * 点击分发按钮check
     *
     * @param pageParam
     * @return Message<AjaxResult>
     * @author sjj
     */
    public AjaxResult doDispatcherCheck(Map<String, Object> pageParam, BindingResult br) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        List<String> entrustIdList = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        if (entrustIdList.isEmpty() || entrustIdList.size() == 0) {
            errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0026")));
        } else {
            pageParam.put("entrustIdList", entrustIdList);
            List<String> list = cfetsEtrDispatcherMapper.doDispatcherCheck(pageParam);
            for (String entrustStatu : list) {
                if (!(StringUtil.equals(entrustStatu, DictCodeList.委托状态_待接收.getValue()) || StringUtil.equals(entrustStatu, DictCodeListExt3.委托状态_待分发.getValue())
                )) {
                    errorList.add(new ValidMessage("", getMessage("TRD_W_0001", SysDataUtil.getNameFromDataDict(DictCode.委托状态, entrustStatu), "分发")));
                }
            }
        }
        ajaxResult.setErrorList(errorList);
        ajaxResult.setMessage(errorList.toString());
        return ajaxResult;
    }

    /**
     * 点击分发按钮check
     *
     * @param pageParam
     * @return Message<AjaxResult>
     * @author sjj
     */
    public AjaxResult doDispatcherCheck(Map<String, Object> pageParam) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        List<String> entrustIdList = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        List<String> newTraders = StringUtil.splitToList(",", (String) pageParam.get("traders"));
        List<CfetsEtrDispatcherResponse> etrDispatcherList = new ArrayList<>();

        if (CollectionUtils.isEmpty(entrustIdList)) {
            errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0026")));
        } else {
            pageParam.put("entrustIdList", entrustIdList);
            List<String> list = cfetsEtrDispatcherMapper.doDispatcherCheck(pageParam);
            for (String entrustStatu : list) {
                if (!(StringUtil.equals(entrustStatu, DictCodeList.委托状态_待接收.getValue()) || StringUtil.equals(entrustStatu, DictCodeListExt3.委托状态_待分发.getValue())
                )) {
                    errorList.add(new ValidMessage("", getMessage("TRD_W_0001", SysDataUtil.getNameFromDataDict(DictCode.委托状态, entrustStatu), "分发")));
                }
            }
        }
        List<String> finprodIdList = StringUtil.splitToList(",", (String) pageParam.get("finprodId")).stream()
                .distinct().collect(Collectors.toList());
        for (String finprodId : finprodIdList) {
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
            if (!DictCodeListExt2.平安资产类型_一级市场债.getValue().equals(finProduct.getFinprodType2())) {
                break;
            }
            CfetsEtrDispatcherResponse etrDispatcherDto = new CfetsEtrDispatcherResponse();
            List<String> traderList = cfetsEtrDispatcherMapper.getDispatcherTraderList(finprodId, DictCodeList.委托状态_待接收.getValue());
            if (CollectionUtils.isNotEmpty(traderList)) {
                errorList.add(new ValidMessage(getMessage("同一投标日的债券["+finprodId+"]一级投标已有分发记录，是否将已分发记录的交易员也调整为交易员"+newTraders.toString()+" ！")));
            }
            etrDispatcherDto.setTraderList(traderList);
            etrDispatcherDto.setFinprodId(finprodId);
            etrDispatcherList.add(etrDispatcherDto);

        }
        ajaxResult.setErrorList(errorList);
        ajaxResult.setData(etrDispatcherList);
        return ajaxResult;
    }

    /**
     * 获取联想文本框交易员的查询方法
     *
     * @param pageParam param
     * @return List
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getUsers(Map<String, Object> pageParam) {
        String dealers = (String) pageParam.get("search");
        List<String> list = new ArrayList<>();
        list.add(DictCodeList.角色岗位_交易处长.getValue());
        list.add(DictCodeList.角色岗位_交易员.getValue());
        list.add(DictCodeList.角色岗位_交易员_资本境内_岗.getValue());
        list.add(DictCodeList.角色岗位_交易员_资本境外_岗.getValue());
        pageParam.put("rolePosList", list);
        pageParam.put("dealers", dealers);
        return cfetsEtrDispatcherMapper.getUsers(pageParam);
    }

    /**
     * 点击分发保存按钮check
     *
     * @param pageParam
     * @return Message<AjaxResult>
     * @author sjj
     */
    public AjaxResult doDispatcherDealCheck(Map<String, Object> pageParam, BindingResult br) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        List<String> tradersList = StringUtil.splitToList(",", (String) pageParam.get("traders"));
        List<String> finprodType2List = StringUtil.splitToList(",", (String) pageParam.get("finprodType2"));
        List<String> finprodIdList = StringUtil.splitToList(",", (String) pageParam.get("finprodId"));
        if (tradersList.isEmpty()) {
            errorList.add(new ValidMessage(getMessage("TRD_W_0002")));
        }
        for (String finprodId : finprodIdList) {
            List<String> traders = cfetsEtrDispatcherMapper.getDispatcherTraderList(finprodId, DictCodeList.委托状态_已接收.getValue());
            if (CollectionUtils.isNotEmpty(traders)) {
                traders.stream().sorted();
                tradersList.stream().sorted();
                if (!traders.toString().equals(tradersList.toString())) {
                    errorList.add(new ValidMessage(getMessage("同一投标日的债券一级投标已有交易员接收，必须分给交易员"+traders.toString())));
                }
            }
        }

        ajaxResult.setErrorList(errorList);
        ajaxResult.setMessage(errorList.toString());
        return ajaxResult;
    }

    /**
     * 点击分发确认
     *
     * @param pageParam
     * @return Message<AjaxResult>
     * @author sjj
     */
    @Transactional(rollbackFor = Exception.class)
    public void doDispatcherDeal(Map<String, Object> pageParam) {
        // 删除原先分发过的所有交易员记录
        List<String> entrustIds = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        List<String> tradersList = StringUtil.splitToList(",", (String) pageParam.get("traders"));
        EtrDistributeKey key = new EtrDistributeKey();
        EtrDistribute record = new EtrDistribute();
        for (int j = 0; j < entrustIds.size(); j++) {
            key.setEntrustId(entrustIds.get(j));
            cfetsEtrDispatcherMapper.deleteEtrDistributeByPrimaryKey(key);
            //待分发转为待接收
            EtrEntrust etrEntrust = new EtrEntrust();
            etrEntrust.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            etrEntrust.setEntrustId(entrustIds.get(j));
            etrEntrustMapper.updateByPrimaryKeySelective(etrEntrust);

            //更新操作记录日志
            CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
            oprHisReq.setEntrustId(entrustIds.get(j));
            oprHisReq.setOperType(DictCodeListExt3.操作类型_分发.getValue());
            joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
        }
        for (int i = 0; i < tradersList.size(); i++) {
            record.setTrader(tradersList.get(i));
            for (int j = 0; j < entrustIds.size(); j++) {
                record.setEntrustId(entrustIds.get(j));
                record.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                record.setCreateTime(DateUtil.now());
                record.setCreateUser(LoginUserUtil.getUserName());
                record.setUpdateUser(LoginUserUtil.getUserName());
                etrDistributeMapper.insert(record);
            }
        }

        Map<String, Object> param = new HashMap<>();
        param.put("entrustId", (String) pageParam.get("entrustId"));
        param.put("isAutoReceive", null);
        EntrCommandDispatcherTotalDto msgdto = entrCommandDispatcherService.pushCommandMsg(param);

        // modify by etliu 20210203 可转债网下申购不发送交易端 start
        if (msgdto != null && !StringUtil.isEmptyList(msgdto.getEtr_entrust_list())) {
            String message = restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                    + "/EtrCommandDispatcher/pushCommandMsg", msgdto, String.class);
            if(StringUtil.isNotEmpty(message)){
                throw new BusinessException(message);
            }
        }
        // modify by etliu 20210203 可转债网下申购不发送交易端 end
    }

    /**
     * 点击退回按钮check
     *
     * @param pageParam
     * @return Message<AjaxResult>
     * @author sjj
     */
    public AjaxResult doRefuseEntrustCheck(Map<String, Object> pageParam, BindingResult br) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        List<String> entrustIdList = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        List<String> entrustStatusList = StringUtil.splitToList(",", (String) pageParam.get("entrustStatus"));
        if (!StringUtil.isNotEmpty(pageParam.get("rekReason"))) {
            errorList.add(new ValidMessage("请输入退回原因!"));
        }
        if (entrustIdList.isEmpty()) {
            errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0026")));
        } else {
            for (String entrustStatus : entrustStatusList) {
                if (!StringUtil.equals(entrustStatus, DictCodeListExt3.委托状态_待分发.getValue())) {
                    errorList.add(new ValidMessage("只可对待分发指令执行退回操作！"));
                    break;
                }
            }
        }
        ajaxResult.setMessage(errorList.toString());
        return ajaxResult;
    }

    private String queryPageId(String finprodType2) {
        String pageId = "";
        //存款
        if (StringUtil.equals(DictCodeList.平安资产类型_存款.getValue(), finprodType2)) {
            pageId = "TrdDepositInvestTrade002";
        }
        //拆借
        else if (StringUtil.equals(DictCodeList.平安资产类型_拆借.getValue(), finprodType2)) {
            pageId = "TrdLendInvestTrade002";
        }
        //基金
        else if (StringUtil.equals(DictCodeList.平安资产类型_基金.getValue(), finprodType2)) {
            pageId = "TrdFundInvestTrade002";
        }
        //计划类净值型
        else if (StringUtil.equals(DictCodeList.平安资产类型_计划类资产_净值型.getValue(), finprodType2)) {
            pageId = "TrdPlanNetInvestTrade002";
        }
        //计划类货币型
        else if (StringUtil.equals(DictCodeList.平安资产类型_计划类资产_货币型.getValue(), finprodType2)) {
            pageId = "TrdPlanCcyInvestTrade002";
        }
        //计划类利率型
        else if (StringUtil.equals(DictCodeList.平安资产类型_计划类资产_利率型.getValue(), finprodType2)) {
            pageId = "TrdPlanRateInvestTrade002";
        }
        //股票
        else if (StringUtil.equals(DictCodeListExt2.平安资产类型_股票.getValue(), finprodType2)) {
            pageId = "TrdStock002";
        }
        //项目类
        else if (StringUtil.equals(DictCodeList.平安资产类型_项目类资产.getValue(), finprodType2)) {
            pageId = "TrdProjectInvestTrade002";
        }
        //外汇即期
        else if (StringUtil.equals(DictCodeListExt2.平安资产类型_外汇即期.getValue(), finprodType2)) {
            pageId = "TrdSpotexInvestTrade002";
        }
        //外汇远期
        else if (StringUtil.equals(DictCodeListExt2.平安资产类型_外汇远期.getValue(), finprodType2)) {
            pageId = "TrdForwexInvestTrade002";
        }
        //外汇掉期
        else if (StringUtil.equals(DictCodeListExt2.平安资产类型_外汇掉期.getValue(), finprodType2)) {
            pageId = "TrdFxswapInvestTrade002";
        }
        //债券借贷
        else if (StringUtil.equals(DictCodeListExt2.平安资产类型_债券借贷.getValue(), finprodType2)) {
            pageId = "TrdBondLendInvestTrade002";
        }
        //利率互换
        else if (StringUtil.equals(DictCodeListExt2.平安资产类型_利率互换.getValue(), finprodType2)) {
            pageId = "TrdIRSTrade002";
        }//债券交易
        else if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), finprodType2)) {
            pageId = "TrdBondInvestTrade002";
        }
        return pageId;
    }

    /**
     * 点击退回按钮
     *
     * @param pageParam
     * @return Message<AjaxResult>
     * @author sjj
     */
    public AjaxResult refuseEntrust(Map<String, Object> pageParam) {
        AjaxResult result = new AjaxResult();
        if (this.returnEntrust(pageParam)) {
            result = this.returnRsk(pageParam);
        }
        return result;
    }

    /**
     * 指令退回
     *
     * @param pageParam
     * @return Message<AjaxResult>
     * @author sjj
     */
    private Boolean returnEntrust(Map<String, Object> pageParam) {
        List<String> entrustIdList = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        if (entrustIdList != null && entrustIdList.size() > 0) {
            for (String entrustId : entrustIdList) {
                EtrDistributeKey key = new EtrDistributeKey();
                key.setEntrustId(entrustId);
                List<EtrDistribute> etrDistributeList = etrInvestInfoMapper.selectEtrDistributeByPrimaryKey(key);
                // 更新委托表的委托状态
                etrInvestInfoServiceImpl.updateDelegateStatus(entrustId, DictCodeList.委托状态_已退回.getValue());
//                // 委托退回原因
                etrInvestInfoServiceImpl.updateBackReason(entrustId, (String) pageParam.get("rekReason"));
                // 删除交易员的委托数据
                for (EtrDistribute entity : etrDistributeList) {
                    entity.setRecStatus(DictCodeList.委托状态_已退回.getValue());
                    etrDistributeMapper.updateByPrimaryKeySelective(entity);
                }
                if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
                    //插入旧头寸
                    if (baseEtrCashPositionCommService != null) {
                        baseEtrCashPositionCommService.entrustCloseExecute(entrustId, BussinessDictCodeListRds.是否_是.getValue());
                    }
                }

                //更新操作记录日志
                CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
                oprHisReq.setEntrustId(entrustId);
                oprHisReq.setOperType(DictCodeListExt3.操作类型_退回.getValue());
                oprHisReq.setBusiDes((String) pageParam.get("rekReason"));
                joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);

                Map<String, Object> param = new HashMap<>();
                param.put("entrustId", entrustId);
                param.put("isAutoReceive", null);
                EntrCommandDispatcherTotalDto msgdto = entrCommandDispatcherService.pushCommandMsg(param);
                // modify by etliu 20210203 可转债网下申购不发送交易端 start
                if (msgdto != null && !StringUtil.isEmptyList(msgdto.getEtr_entrust_list())) {
                    for (EntrCommandDispatcherDto entrCommandDispatcherDto: msgdto.getEtr_entrust_list()){
                        entrCommandDispatcherDto.setEntrustStatus(DictCodeList.委托状态_已退回.getValue());
                    }
                    restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                            + "/EtrCommandDispatcher/pushCommandMsg", msgdto, String.class);
                }
                // modify by etliu 20210203 可转债网下申购不发送交易端 end
            }
            return true;
        }
        return false;
    }

    private AjaxResult returnRsk(Map<String, Object> pageParam) {
        AjaxResult result = new AjaxResult();
        @SuppressWarnings("unchecked")
        List<String> enTrustIds = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        List<Map<String, Object>> list = new ArrayList<>();
        List<String> counterIdList = new ArrayList<>();
        Map<String, String> counterIdMap = splitParams("counterIds",
                str -> counterIdList.add(str));
        Map<String, PtlPortfolio> innerIds = AllTradeCommonUtil.selectPortfolioIds(
                counterIdList);
        Map<String, String> psMap = splitParams("psList", null);
        if (!StringUtil.isEmptyList(enTrustIds)) {
            String ps = BussinessDictCodeList.交易方向_买入.getValue();
            enTrustIds.forEach(id -> {
                Map<String, Object> finDto = new HashMap<>();
                finDto.put(PageFieldToInterfFactory.INF_EVENTID, id);
                finDto.put(PageFieldToInterfFactory.INF_PS, psMap.get(id));
                finDto.put(PageFieldToInterfFactory.INF_EVENTDATE, DateUtil.format(
                        DateUtil.platDateFromCache(), DateUtil.DEFAULT_DATE_FORMAT_EN));
                finDto.put(PageFieldToInterfFactory.INF_REVOKEFLG, BussinessDictCodeListRds.是否_是.getValue());
                finDto.put(PageFieldToInterfFactory.INF_ISINNERTRD, innerIds.containsKey(counterIdMap.get(
                        id)) ? BussinessDictCodeListRds.是否_是.getValue() : BussinessDictCodeListRds.是否_否.getValue());
                TradeRdsCommonAop.getInterfRiskRdsService().changeEventId(finDto);
                list.add(finDto);
                // 生成方向指令
                if (innerIds.containsKey(counterIdMap.get(id))) {
                    finDto = new HashMap<>();
                    finDto.put(PageFieldToInterfFactory.INF_EVENTID, id + ps);
                    finDto.put(PageFieldToInterfFactory.INF_EVENTDATE, DateUtil.format(
                            DateUtil.platDateFromCache(),
                            DateUtil.DEFAULT_DATE_FORMAT_EN));
                    finDto.put(PageFieldToInterfFactory.INF_PS, ps);
                    finDto.put(PageFieldToInterfFactory.INF_REVOKEFLG, BussinessDictCodeListRds.是否_是.getValue());
                    finDto.put(PageFieldToInterfFactory.INF_ISINNERTRD, BussinessDictCodeListRds.是否_是.getValue());
                    list.add(finDto);
                }
                // 转换申请生成买入方向指令
                else if (CommonUtil.isType(psMap.get(id), DictCodeListExt2.交易方向_转换申请)
                        || CommonUtil.isType(psMap.get(id), DictCodeListExt1.交易方向_债转股)) {
                    finDto.put(PageFieldToInterfFactory.INF_PS,
                            BussinessDictCodeList.交易方向_卖出.getValue());
                    finDto.put(PageFieldToInterfFactory.INF_EVENTID,
                            id + BussinessDictCodeList.交易方向_卖出.getValue());
                    finDto = new HashMap<>();
                    finDto.put(PageFieldToInterfFactory.INF_EVENTID, id + ps);
                    finDto.put(PageFieldToInterfFactory.INF_EVENTDATE, DateUtil.format(
                            DateUtil.platDateFromCache(), DateUtil.DEFAULT_DATE_FORMAT_EN));
                    finDto.put(PageFieldToInterfFactory.INF_PS, ps);
                    finDto.put(PageFieldToInterfFactory.INF_REVOKEFLG,
                            BussinessDictCodeListRds.是否_是.getValue());
                    finDto.put(PageFieldToInterfFactory.INF_ISINNERTRD,
                            BussinessDictCodeListRds.是否_是.getValue());
                    list.add(finDto);
                }
            });
        }
        try {
            ClientData data = TradeRdsCommonAop.getInterfRiskRdsService().sendListDataCommon(
                    list, BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue());
            if (StringUtil.equals(data.getHead().get(InterfUtil.std400msgt), InterfUtil.E)) {
                result.setMessage(data.getHead().get(InterfUtil.stdrtninfo).toString());
            }
        } catch (Exception e) {
            result.setMessage("指令撤销异常");
        }
        return result;
    }

    private Map<String, String> splitParams(String param, Consumer<String> consumer) {
        List<String> list = StringUtil.splitToList(GlobalConstant.COMMA,
                RequestUtil.getParam(param));
        Map<String, String> map = new HashMap<>();
        if (!StringUtil.isEmptyList(list)) {
            list.forEach(str -> {
                String[] arr = StringUtil.split(str, GlobalConstant.COLON);
                if (arr == null || arr.length == 0) {
                    return;
                }
                map.put(arr[0], arr.length == 2 ? arr[1] : StringUtil.EMPTY);
                if (consumer != null && arr.length == 2 && !StringUtil.isEmpty(arr[1])) {
                    consumer.accept(arr[1]);
                }
            });
        }
        return map;
    }

    /**
     * 自动分发规则查询
     *
     * @return CfetsEtrDispatcherAutoRequest
     * @author sjj
     */
    public CfetsEtrDispatcherAutoRequest findAutoDetail() {
        CfetsEtrDispatcherAutoRequest request = cfetsEtrDispatcherMapper.findAutoDetail(DateUtil.format(iPlatformDate.getSysDate()));
        if (request == null) {
            request = new CfetsEtrDispatcherAutoRequest();
            request.setDistributeRule("01");
        }
        return request;
    }

    /**
     * 只读表单设置及数据查询_指令详情
     *
     * @param pageCode
     * @param dtCode
     * @param spcdCode
     * @return
     */
    public Map<String, Object> getReadonlyFormEtrInfo(String pageCode, String dtCode, String spcdCode, CommonSearchInfoRequest dto) {
        Map<String, Object> resultData = new HashMap<>();
        //基本信息-字段
        List<CfgReadonlyFormShow> infoForm = new ArrayList<>();
        //委托投资人员信息-字段
        List<CfgReadonlyFormShow> invInfoFrom = new ArrayList<>();
        //质押券-字段
        List<CfgDataTableShow> infoTable = new ArrayList<>();
        //附件信息-字段
        List<CfgDataTableShow> fileTable = new ArrayList<>();
        List<CfgReadonlyFormShow> totalInfo = new ArrayList<>();

        if (StringUtil.equals(DictCodeListExt1.交易场所_香港联合交易所.getValue(), dto.getTradeMarket()) ||
                StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), dto.getTradeMarket()) ||
                StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), dto.getTradeMarket())) {
            //场内指令信息展示列字段查询
            pageCode = "FieldEtrDispatcher";
            String newdtCode = "FieldEtrDispatcherInfo";
            infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, newdtCode, spcdCode);
            if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), dto.getFinprodType2()) &&
                    (StringUtil.equals(DictCodeListExt3.投管业务类型_大宗交易.getValue(), dto.getBusiTypePosition()) ||
                            StringUtil.equals(DictCodeListExt3.投管业务类型_竞价交易.getValue(), dto.getBusiTypePosition()))) {
                String newspcdCode = DictCodeList.平安资产类型_债券.getValue() + spcdCode;
                infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, newdtCode, newspcdCode));
            } else if (StringUtil.equals(DictCodeList.平安资产类型_基金.getValue(), dto.getFinprodType2()) &&
                    (StringUtil.equals(DictCodeListExt3.投管业务类型_大宗交易.getValue(), dto.getBusiTypePosition()) ||
                            StringUtil.equals(DictCodeListExt3.投管业务类型_竞价交易.getValue(), dto.getBusiTypePosition()))) {
                spcdCode = DictCodeList.平安资产类型_基金.getValue() + spcdCode;
                infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, newdtCode, spcdCode));
            } else if (StringUtil.equals(BussinessDictCodeList.平安资产类型_股票.getValue(), dto.getFinprodType2()) &&
                    (StringUtil.equals(DictCodeListExt3.投管业务类型_大宗交易.getValue(), dto.getBusiTypePosition()) ||
                            StringUtil.equals(DictCodeListExt3.投管业务类型_竞价交易.getValue(), dto.getBusiTypePosition()))) {
                spcdCode = BussinessDictCodeList.平安资产类型_股票.getValue() + spcdCode;
                infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, newdtCode, spcdCode));
            } else if (StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), dto.getFinprodType2()) &&
                    StringUtil.equals(DictCodeListExt3.投管业务类型_竞价交易.getValue(), dto.getBusiTypePosition())) {
                spcdCode = DictCodeList.平安资产类型_质押式回购.getValue() + spcdCode;
                infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, newdtCode, spcdCode));
            }
        } else {
            if (StringUtil.equals(DictCodeListExt3.头寸业务类型_质押式回购首期.getValue(), dto.getBusiTypePosition())) {//质押式回购
                String repoPledgeModel = CacheUtil.getSysParam("repo_pledge_model");
                spcdCode = spcdCode.concat(",").concat(repoPledgeModel);
                infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
                spcdCode = spcdCode.concat(",").concat(dto.getPs());
                infoTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, null, null);
                totalInfo = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
                //infoForm.addAll(totalInfo);
            } else if (StringUtil.equals(DictCodeListExt3.头寸业务类型_债券借贷首期.getValue(), dto.getBusiTypePosition())) {
                //String repoPledgeModel = CacheUtil.getSysParam("repo_pledge_model");
                String repoPledgeModel = "02";
                spcdCode = spcdCode.concat(",").concat(repoPledgeModel);
                infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
                invInfoFrom = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode.concat(",P"));
                fileTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode.concat(",F"), null, null);
                //spcdCode = spcdCode.concat(",").concat(dto.getBusiPs());
                spcdCode = spcdCode.concat(",").concat("05");
                infoTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, null, null);

            } else if (StringUtil.equals(DictCodeListExt3.头寸业务类型_买断式回购首期.getValue(), dto.getBusiTypePosition()) ||
                    StringUtil.equals(DictCodeListExt3.头寸业务类型_拆借首期.getValue(), dto.getBusiTypePosition())) {
                infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
                invInfoFrom = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode.concat(",P"));
                fileTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode.concat(",F"), null, null);
            } else if (StringUtil.equals(DictCodeListExt3.头寸业务类型_现券交易.getValue(), dto.getBusiTypePosition()) ||
                    StringUtil.equals(DictCodeListExt3.头寸业务类型_债券行权交易.getValue(), dto.getBusiTypePosition()) ||
                    StringUtil.equals(DictCodeListExt3.头寸业务类型_债券存续期交易.getValue(), dto.getBusiTypePosition())) {
                infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
                if (StringUtil.equals(CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"), "02")) {
                    infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
                    spcdCode = spcdCode + ",02";
                    infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode));
                }
            }else if(StringUtil.equals(DictCodeListExt3.头寸业务类型_债券一级投标.getValue(), dto.getBusiTypePosition())) {
                //一级投标
                return bondFirstLBidding(pageCode, dtCode, spcdCode,dto);
            }else {
                if (StringUtil.isNotEmpty(dto.getFinprodType2())) {
                    if (StringUtil.equals(DictCodeListExt3.中债_上游资产一级分类_存款类资产.getValue(), dto.getFinprodType2())) {
                        spcdCode = dto.getFinprodType2();
                        infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
                        invInfoFrom = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode.concat(",P"));
                        fileTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode.concat(",F"), null, null);

                        if (StringUtil.isNotEmpty(dto.getAssetType11()) && !StringUtil.equals("143", dto.getAssetType11())) {
                            spcdCode = spcdCode.concat(",").concat("S01");
                            List<CfgReadonlyFormShow> infoForm1 = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
                            infoForm.addAll(infoForm1);
                        }

                        //资产四级类型& 交易方向
                        if (StringUtil.isNotEmpty(dto.getAssetType11()) && StringUtil.isNotEmpty(dto.getPs())) {
                            spcdCode = spcdCode.concat(",").concat(dto.getAssetType11()).concat(",").concat(dto.getPs());
                            List<CfgReadonlyFormShow> infoForm2 = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
                            infoForm.addAll(infoForm2);
                        }

                    } else {
                        spcdCode = dto.getFinprodType2();
                        infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
                        invInfoFrom = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode.concat(",P"));
                        fileTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode.concat(",F"), null, null);

                        //交易方向
                        if (StringUtil.isNotEmpty(dto.getPs())) {
                            List<CfgReadonlyFormShow> infoForm1 = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode.concat(",").concat(dto.getPs()));
                            infoForm.addAll(infoForm1);
                        }
                    }
                }else {//处理本币业务指令交易页面的债券
                    infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
                    if (StringUtil.equals(CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"), "02")) {
                        infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
                        spcdCode = spcdCode + ",02";
                        infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode));
                    }
                }
            }
        }

        //基本信息
        if (!CollectionUtils.isEmpty(infoForm)) {
            //排序
            Collections.sort(infoForm, new Comparator<CfgReadonlyFormShow>() {
                @Override
                public int compare(CfgReadonlyFormShow o1, CfgReadonlyFormShow o2) {
                    return o1.getSeqno().compareTo(o2.getSeqno());
                }
            });
            resultData.put(dtCode + "_form", infoForm);
            if (StringUtil.isNotEmpty(dto.getBusiTypePosition())) {
                CfetsEtrDispatcherInfoResponse cfetsResponseDto = this.getEtrDispatcherInfo(dto);
                resultData.put(dtCode + "_data", cfetsResponseDto);
            } else {
                CfetsEtrDispatcherInfoOtcResponse cfetsOtcDto = this.getEtrDispatcherOtc(dto);
                resultData.put(dtCode + "_data", cfetsOtcDto);
            }
        }

        //质押券
        if (!CollectionUtils.isEmpty(infoTable)) {//如果表格有数据
            resultData.put(dtCode + "_table", infoTable);
            resultData.put(dtCode + "_total", totalInfo);
            // 获取质押券信息
            CfetsEtribRepoInfo003Dto item = new CfetsEtribRepoInfo003Dto();
            item.setEntrustId(dto.getEntrustId());
            CfetsEtribRepoInfo003Dto result = cfetsEtrIbRepoinfoService.findEtrinfo(item);
            if (result != null) {
                List<CfetsPledgeBondInfoDto> detailList = result.getPledgeBondTotalListDetailed();
                BigDecimal totalPrinAmt = BigDecimal.ZERO;
                if (!CollectionUtils.isEmpty(detailList)) {
                    for (CfetsPledgeBondInfoDto infoDto : detailList) {
                        infoDto.setPledgeRatio(ArithmeticUtil.multiply(infoDto.getPledgeRatio(), new BigDecimal("100")));
                        totalPrinAmt = ArithmeticUtil.doAdd(totalPrinAmt, ArithmeticUtil.multiply(infoDto.getPledgePrin(), infoDto.getPledgeRatio()).divide(new BigDecimal("100")));
                        infoDto.setPledgePrin(ArithmeticUtil.divide(infoDto.getPledgePrin(), new BigDecimal("10000"), 2));
                        infoDto.setPledStrdPrinAmt(ArithmeticUtil.divide(infoDto.getPledStrdPrinAmt(), new BigDecimal("10000"), 2));
                        if (StringUtil.isNoneBlank(infoDto.getRegistOrg())) {
                            infoDto.setRepRegistOrg(SysDataUtil.getNameFromDataDict(DictCode.登记托管机构_中债, infoDto.getRegistOrg()));
                        }
                    }
                    CfetsEtrDispatcherInfoResponse totalResult = new CfetsEtrDispatcherInfoResponse();
                    if (!StringUtil.equals(DictCodeListExt3.头寸业务类型_质押式回购首期.getValue(), dto.getBusiTypePosition())) {
                        totalResult.setTotalPrinAmt(ArithmeticUtil.divide(totalPrinAmt, new BigDecimal("10000"), 2));
                    }else {
                        totalResult.setTotalPrinAmt(totalPrinAmt);
                    }
                    resultData.put(dtCode + "_datatotal", totalResult);
                    detailList.forEach(e -> e.setTotalPrinAmt(totalResult.getTotalPrinAmt()));
                }
                resultData.put(dtCode + "_datatable", detailList);
            }
        }

        //委托投资人员信息
        if (!CollectionUtils.isEmpty(invInfoFrom)) {
            Collections.sort(invInfoFrom, new Comparator<CfgReadonlyFormShow>() {
                @Override
                public int compare(CfgReadonlyFormShow o1, CfgReadonlyFormShow o2) {
                    return o1.getSeqno().compareTo(o2.getSeqno());
                }
            });

            CfetsEtrDispatcherInvResponse invDto = this.getInvInfo(dto);
            resultData.put(dtCode + "_invFrom", invInfoFrom);
            resultData.put(dtCode + "_dataInvFrom", invDto);
        }

        //附件信息
        if (!CollectionUtils.isEmpty(fileTable)) {
            if (StringUtil.isNotEmpty(dto.getEntrustId())) {
                List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(dto.getEntrustId());
                resultData.put(dtCode + "_fileTable", fileTable);
                resultData.put(dtCode + "_dataFileTable", files);
            }

        }


        return resultData;
    }

    private Map<String, Object> bondFirstLBidding(String pageCode, String dtCode, String spcdCode, CommonSearchInfoRequest dto) {
        Map<String, Object> resultData = new HashMap<>();
        //基本信息
        List<CfgReadonlyFormShow> infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
        EtrBondFirstLBiddingDto infoData = etrBondFirstLBiddingService.selectByKey(dto.getEntrustId(),dto.getFinprodId(),dto.getPortfolioId());
        if(Objects.nonNull(infoData)) {
            BigDecimal entrustNum = BigDecimal.ZERO;
            if(!CollectionUtils.isEmpty(infoData.getBidPriceList())) {
                for(EtrEntrustBidPrice bidPrice : infoData.getBidPriceList()) {
                    entrustNum = ArithmeticUtil.doAdd(bidPrice.getBidPrinAmt(),entrustNum);
                    bidPrice.setBidPrinAmt(ArithmeticUtil.divide(bidPrice.getBidPrinAmt(),new BigDecimal("10000"),4));
                }
                infoData.setEntrustNum(ArithmeticUtil.divide(entrustNum,new BigDecimal("10000"),4));
            }
            Map<String,Object> param = new HashMap<>();
            param.put("portfolioId",dto.getPortfolioId());
            param.put("finprodId",dto.getFinprodId());
            param.put("ccy",dto.getCcy());
            Map<String, Object> map = etrCommonService.getProAvailableCash(param);
            if(Objects.nonNull(map.get("cashT1"))) {
                infoData.setProAvailableCash1((BigDecimal) map.get("cashT1"));
            }
            if(Objects.nonNull(map.get("cashT"))) {
                infoData.setProAvailableCash((BigDecimal) map.get("cashT"));
            }
        }
        resultData.put(dtCode + "_infoForm",infoForm);
        resultData.put(dtCode + "_infoFormData",infoData);
        //备注
        List<CfgReadonlyFormShow> rFrom = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode.concat(",R"));
        resultData.put(dtCode + "_rFrom",rFrom);
        resultData.put(dtCode + "_rFromData",infoData);
        //投标券面总额汇总
        List<CfgReadonlyFormShow> aFrom = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode.concat(",A"));
        resultData.put(dtCode + "_aFrom",aFrom);
        resultData.put(dtCode + "_aFromData",infoData);
        //投标标位
        List<CfgDataTableShow> infoTable =  cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, null, null);
        resultData.put(dtCode + "_infoTable",infoTable);
        resultData.put(dtCode + "_infoTableData",infoData.getBidPriceList());
        //指令投资人员信息
        List<CfgReadonlyFormShow> invInfoFrom = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode.concat(",P"));
        CfetsEtrDispatcherInvResponse invDto = this.getInvInfo(dto);
        resultData.put(dtCode + "_invInfoFrom",invInfoFrom);
        resultData.put(dtCode + "_invInfoFromData",invDto);
        //附件信息-字段
        List<CfgDataTableShow> fileTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode.concat(",F"), null, null);
        List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(dto.getEntrustId());
        resultData.put(dtCode + "_fileTable",fileTable);
        resultData.put(dtCode + "_fileTableData",files);
        return resultData;
    }



    /**
     * 获取指令明细基本信息 场外
     *
     * @param dto
     * @return
     */
    private CfetsEtrDispatcherInfoOtcResponse getEtrDispatcherOtc(CommonSearchInfoRequest dto) {
        //dto转成map
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        List<String> entrustIdList = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        if (StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1) {
            return new CfetsEtrDispatcherInfoOtcResponse();
        } else {
            CfetsEtrDispatcherInfoOtcResponse etrDispatcherOtc = cfetsEtrDispatcherMapper.getEtrDispatcherOtc(pageParam);
            if (!Objects.isNull(etrDispatcherOtc) && StringUtil.isNotEmpty(etrDispatcherOtc.getPortfolioId())) {
                //组装组合名称
                String portfolioNameByIds = getPortfolioNameByIds(etrDispatcherOtc.getPortfolioId());
                etrDispatcherOtc.setPortfolioName(portfolioNameByIds);
            }
            return etrDispatcherOtc;
        }
    }

    /**
     * 获取委托投资人员信息
     *
     * @param dto
     * @return
     */
    public CfetsEtrDispatcherInvResponse getInvInfo(CommonSearchInfoRequest dto) {
        //dto转成map
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        List<String> entrustIdList = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        if (StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1) {
            return new CfetsEtrDispatcherInvResponse();
        } else {
            return cfetsEtrDispatcherMapper.getInvInfo(pageParam);
        }
    }

    /**
     * 只读表单设置及数据查询_指令详情
     *
     * @param pageCode
     * @param dtCode
     * @param spcdCode
     * @return
     */
    public Map<String, Object> getReadonlyFormEtrInfoExc(String pageCode, String dtCode, String spcdCode, CommonSearchInfoRequest dto) {
        if ("null".equals(spcdCode) && Objects.nonNull(dto) && StringUtil.isNotEmpty(dto.getEntrustId())) {
            String busiTypePosition = cfetsEtrDispatcherMapper.getBusiTypePosition(dto.getEntrustId());
            logger.debug("fieldEtrDispatcherMapper getBusiTypePosition with entrustId = [{}], result busiTypePosittion = [{}]", dto.getEntrustId(), busiTypePosition);
            if (StringUtil.isEmpty(busiTypePosition)) {
                dto.setBusiTypePosition(StringUtil.EMPTY);
            } else {
                dto.setBusiTypePosition(busiTypePosition);
                spcdCode = busiTypePosition;
            }
        }
        Map<String, Object> resultData = new HashMap<>();
        List<CfgReadonlyFormShow> infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
        if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), dto.getFinprodType2()) &&
                (StringUtil.equals(DictCodeListExt3.投管业务类型_大宗交易.getValue(), dto.getBusiTypePosition()) ||
                        StringUtil.equals(DictCodeListExt3.投管业务类型_竞价交易.getValue(), dto.getBusiTypePosition()))) {
            spcdCode = DictCodeList.平安资产类型_债券.getValue() + spcdCode;
            infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode));
        } else if (StringUtil.equals(DictCodeList.平安资产类型_基金.getValue(), dto.getFinprodType2()) &&
                (StringUtil.equals(DictCodeListExt3.投管业务类型_大宗交易.getValue(), dto.getBusiTypePosition()) ||
                        StringUtil.equals(DictCodeListExt3.投管业务类型_竞价交易.getValue(), dto.getBusiTypePosition()))) {
            spcdCode = DictCodeList.平安资产类型_基金.getValue() + spcdCode;
            infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode));
        } else if (StringUtil.equals(BussinessDictCodeList.平安资产类型_股票.getValue(), dto.getFinprodType2()) &&
                (StringUtil.equals(DictCodeListExt3.投管业务类型_大宗交易.getValue(), dto.getBusiTypePosition()) ||
                        StringUtil.equals(DictCodeListExt3.投管业务类型_竞价交易.getValue(), dto.getBusiTypePosition()))) {
            spcdCode = BussinessDictCodeList.平安资产类型_股票.getValue() + spcdCode;
            infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode));
        } else if (StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), dto.getFinprodType2()) &&
                StringUtil.equals(DictCodeListExt3.投管业务类型_竞价交易.getValue(), dto.getBusiTypePosition())) {
            spcdCode = DictCodeList.平安资产类型_质押式回购.getValue() + spcdCode;
            infoForm.addAll(cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode));
        }
        //排序
        Collections.sort(infoForm, new Comparator<CfgReadonlyFormShow>() {
            @Override
            public int compare(CfgReadonlyFormShow o1, CfgReadonlyFormShow o2) {
                return o1.getSeqno().compareTo(o2.getSeqno());
            }
        });
        CfetsEtrDispatcherInfoExcResponse fieldResponseDto = this.getEtrDispatcherExcInfo(dto);
        if(StringUtil.isNotEmpty(fieldResponseDto) && DictCodeListExt3.投管业务类型_新股申购.equals(fieldResponseDto.getBusiTypePosition())){
            fieldResponseDto.setPriceModel(StringUtil.EMPTY);
        }
        String showInvaimOrBussinssmodel = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
        if(null != showInvaimOrBussinssmodel && showInvaimOrBussinssmodel.equals("02")){
            infoForm.forEach(field->{
                if(field.getColname().equals("投资目的")){
                    field.setColname("业务模式");
                }
            });
        }
        resultData.put(dtCode + "_form", infoForm);
        resultData.put(dtCode + "_data", fieldResponseDto);
        return resultData;
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
    public CfetsEtrDispatcherInfoExcResponse getEtrDispatcherExcInfo(CommonSearchInfoRequest dto) {
        CfetsEtrDispatcherInfoExcResponse fieldResponseDto = new CfetsEtrDispatcherInfoExcResponse();
        //dto转成map
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("YESORNO_YES", DictCodeList.是否_是.getValue());
        List<String> entrustIdList = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        pageParam.put("sysDateStr", platformDateImpl.getSysDateStr());
        pageParam.put("SHOW_INVAIM_OR_BUSSINSSMODEL", CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL"));

        if (StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1) {
            return new CfetsEtrDispatcherInfoExcResponse();
        } else {
            fieldResponseDto = cfetsEtrDispatcherMapper.getEtrDispatcherExcInfo(pageParam);
            //一级市场投标增加投标标位List
            if (fieldResponseDto != null) {
                if (StringUtil.isNotEmpty(fieldResponseDto.getPortfolioId())) {
                    String portfolioNameByIds = getPortfolioNameByIds(fieldResponseDto.getPortfolioId());
                    fieldResponseDto.setPortfolioName(portfolioNameByIds);
                }
                if (StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券一级投标.getValue())) {
                    fieldResponseDto.setBidPriceList(mapper.selectBidPriceByKey(pageParam));
                    fieldResponseDto.setEtrBidDate(fieldResponseDto.getEntrustDate());
                } else if (StringUtil.equals(dto.getBusiTypePosition(), "利率互换首期")) {
                    if (fieldResponseDto.getDueDate().equals(" - ")) {
                        fieldResponseDto.setDueDate("");
                    }
                } else if (StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券借贷首期.getValue())
                        || StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_质押式回购首期.getValue())) {
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
                } else if (StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.投管业务类型_质押券出入库.getValue())) {
                    String ps = (String) pageParam.get("ps");
                    if (DictCodeListExt3.交易方向_质押券入库.getValue().equals(ps) && StringUtil.isNotEmpty(fieldResponseDto.getPledgeRatio())) {
                        if(StringUtil.isNotEmpty(fieldResponseDto.getStockSecId())){
                            Date sysDate = DateUtil.platDateFromCache();
                            BigDecimal faceValue = etrCommonService.select券面面值(fieldResponseDto.getStockSecId(), DateUtil.format(sysDate));
                            fieldResponseDto.setPledgeStandardNumber(ArithmeticUtil.divide(ArithmeticUtil.multiply(fieldResponseDto.getPledgeRatio().divide(new BigDecimal("100")), fieldResponseDto.getEntrustNum(),faceValue),new BigDecimal(100),0, RoundingMode.DOWN));
                        }else{
                            fieldResponseDto.setPledgeStandardNumber(ArithmeticUtil.multiply(fieldResponseDto.getPledgeRatio().divide(new BigDecimal("100")), fieldResponseDto.getEntrustNum()));
                        }
                    }else if (DictCodeListExt3.交易方向_质押券出库.getValue().equals(ps) && StringUtil.isNotEmpty(fieldResponseDto.getPledgeRatio())) {
                        if(StringUtil.isNotEmpty(fieldResponseDto.getStockSecId())){
                            Date sysDate = DateUtil.platDateFromCache();
                            BigDecimal faceValue = etrCommonService.select券面面值(fieldResponseDto.getStockSecId(), DateUtil.format(sysDate));
                            fieldResponseDto.setPledgeStandardNumber(ArithmeticUtil.divide(ArithmeticUtil.multiply(fieldResponseDto.getPledgeRatio().divide(new BigDecimal("100")), fieldResponseDto.getEntrustNum(),faceValue),new BigDecimal(100),0, RoundingMode.DOWN));
                        }
                    }
                } else if (StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_现券交易.getValue())) {
                    if (StringUtil.equals("01", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"))) {
                        fieldResponseDto.setEntrustOwnNum(BigDecimal.ZERO);
                        fieldResponseDto.setIntegrateSecAmount(BigDecimal.ZERO);
                    }
                } else if (StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), dto.getFinprodType2())
                        && StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.投管业务类型_竞价交易.getValue())) {
                    fieldResponseDto.setUnitPrice(ArithmeticUtil.multiply(fieldResponseDto.getUnitPrice(), BigDecimal.valueOf(100)));
                }
                ////将投资经理转为真实姓名
                if (StringUtil.isNotEmpty(fieldResponseDto.getInvManagerId())) {
                    User user1 = cfetsEtrDispatcherMapper.getRealNameByUserName(fieldResponseDto.getInvManagerId());
                    if (StringUtil.isNotEmpty(user1)) {
                        fieldResponseDto.setInvManagerId(user1.getRealName());
                    }
                }
            }
            return fieldResponseDto;
        }
    }

    /**
     * 获取组合名称
     *
     * @param portfolioId
     * @return
     */
    private String getPortfolioNameByIds(String portfolioId) {
        List<String> portfolioIds = StringUtil.splitToList(",", portfolioId);
        List<PtlPortfolio> ptlPortfolios = ptlPortfolioMapper.selectPortfolioByIds(portfolioIds);
        if (!CollectionUtils.isEmpty(ptlPortfolios)) {
            return ptlPortfolios.stream().map(r -> r.getPortfolioName()).collect(Collectors.joining(","));
        }
        return "";
    }

    /**
     * 一级投标指令分发校验
     * @param cfetsDto
     * @return
     */
    public AjaxResult getDispatcherFirstBid(CfetsEtrDispatcherDealRequest cfetsDto) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        List<JoyinMessage> joyinMessages = new ArrayList<>();
        List<String> finprodIdListFirst = new ArrayList<>();
        List<String> finprodIdListAlready = new ArrayList<>();
        List<CfetsEtrDispatcherResponse> etrDispatcherList = new ArrayList<>();

        List<String> finprodIdList = StringUtil.splitToList(",", cfetsDto.getFinprodId()).stream().distinct().collect(Collectors.toList());
        List<String> entrustIdList = StringUtil.splitToList(",", cfetsDto.getEntrustId());

        for (String finprodId : finprodIdList) {
            List<String> statusList = new ArrayList<>();
            statusList.add(DictCodeList.委托状态_待接收.getValue());
            statusList.add(DictCodeList.委托状态_已接收.getValue());
            List<String> entrustIdAlready = cfetsEtrDispatcherMapper.getDispatcherEntrustIdList(finprodId, statusList);
            if (CollectionUtils.isNotEmpty(entrustIdAlready)) {
                finprodIdListAlready.add(finprodId);
            }else {
                statusList.clear();
                statusList.add(DictCodeListExt3.委托状态_待分发.getValue());
                List<String> entrustIdFirst = cfetsEtrDispatcherMapper.getDispatcherEntrustIdList(finprodId, statusList);
                if (CollectionUtils.isNotEmpty(entrustIdFirst)) {
                    finprodIdListFirst.add(finprodId);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(finprodIdListFirst) && CollectionUtils.isNotEmpty(finprodIdListAlready)) {
            errorList.add(new ValidMessage(getMessage("选择的资产不能同时存在首次分发和已分发数据,请重新选择。首次分发资产["
                    + finprodIdListFirst.toString() + "],已分发资产[" + finprodIdListAlready.toString() + "]")));
            ajaxResult.setErrorList(errorList);
            return ajaxResult;
        }
        //首次分发资产 返回未勾选指令信息
        if (CollectionUtils.isNotEmpty(finprodIdListFirst)){
            for (String finprodId : finprodIdListFirst) {
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
                if (!DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue().equals(finProduct.getFinprodType2())) {
                    break;
                }
                CfetsEtrDispatcherResponse etrDispatcherDto = new CfetsEtrDispatcherResponse();
                List<String> unSelectEntrustIds = new ArrayList<>();
                List<String> statusList = new ArrayList<>();
                statusList.add(DictCodeListExt3.委托状态_待分发.getValue());
                List<String> entrustIds = cfetsEtrDispatcherMapper.getDispatcherEntrustIdList(finprodId, statusList);
                for (String entrustId : entrustIds) {
                    if (!entrustIdList.contains(entrustId)) {
                        unSelectEntrustIds.add(entrustId);
                    }
                }
                etrDispatcherDto.setEntrustIdList(unSelectEntrustIds);
                etrDispatcherDto.setFinprodId(finprodId);
                etrDispatcherList.add(etrDispatcherDto);
            }
            StringBuffer buffer = new StringBuffer();
            if (CollectionUtils.isNotEmpty(etrDispatcherList)) {
                for (CfetsEtrDispatcherResponse response : etrDispatcherList) {
                    buffer.append("是否确认将该债券["+response.getFinprodId()+"]所有的投标指令均分发给此交易员？本次未选择的指令:"+response.getEntrustIdList().toString()+"\n");
                }
            }
            ajaxResult.setMessage(buffer.toString());
            ajaxResult.setData(etrDispatcherList);
            return ajaxResult;
        }

        //已分发资产 返回交易员信息
        if (CollectionUtils.isNotEmpty(finprodIdListAlready)) {
            for (String finprodId : finprodIdListAlready) {
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
                if (!DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue().equals(finProduct.getFinprodType2())) {
                    break;
                }
                CfetsEtrDispatcherResponse etrDispatcherDto = new CfetsEtrDispatcherResponse();
                //资产存在已分发的已接收记录,返回对应的交易员信息且交易员信息不可修改
                List<String> traderList1 = cfetsEtrDispatcherMapper.getDispatcherTraderList(finprodId, DictCodeList.委托状态_已接收.getValue());
                if (CollectionUtils.isNotEmpty(traderList1)) {
                    etrDispatcherDto.setFinprodId(finprodId);
                    etrDispatcherDto.setTraderList(traderList1);
                    etrDispatcherDto.setReadOnlyField(DictCodeList.是否_否.getValue());
                } else {
                    //资产存在已分发的待接收记录,返回对应的交易员信息且交易员信息可修改
                    List<String> traderList2 = cfetsEtrDispatcherMapper.getDispatcherTraderList(finprodId, DictCodeList.委托状态_待接收.getValue());
                    if (CollectionUtils.isNotEmpty(traderList2)) {
                        etrDispatcherDto.setFinprodId(finprodId);
                        etrDispatcherDto.setTraderList(traderList2);
                        etrDispatcherDto.setReadOnlyField(DictCodeList.是否_是.getValue());
                    }
                }
                etrDispatcherList.add(etrDispatcherDto);
            }
            ajaxResult.setData(etrDispatcherList);
            return ajaxResult;
        }

        return ajaxResult;

    }
}
