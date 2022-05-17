/*
 * 文件名：EtrBondFirstLBiddingService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.etr.bond.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.etrinvestbondinfo.service.EtrInvestBondInfoService;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.dto.PositionBondExecuteParam;
import com.joyintech.tams.business.common.dto.PositionRulePtlData;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondMapper;
import com.joyintech.tams.cfets.etr.bond.dao.CfetsEtrIbBondinfo002Mapper;
import com.joyintech.tams.cfets.etr.bond.dao.CfetsEtrIbBondinfoMapper;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsBussCombDistribtBaseDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsBussCombDistribtDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrCalcAmountRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrCalcAmountResponse;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrFinProductDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbBondPosiInfoRequestDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbBondPosiPage001RequestDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbBondPosinfo001Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbBondinfo001Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbBondinfo002Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbBondinfoDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbClosePosEtrRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbClosePositionRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbdoRiskcalDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbdoRiskcalRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbfindEtrinfoRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbfindEtrinfoResponse;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrInvestBondInfoReqDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrTrdProductDealDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribSaveRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsUnitCostDto;
import com.joyintech.tams.cfets.etr.socket.BondSocketClient;
import com.joyintech.tams.cfets.position.service.PositionInfoService;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.SppiImportDataMapper;
import com.joyintech.tams.common.base.dao.WflEtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.WflEtrEntrustMapper;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.SppiImportData;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.WflEtrEntrust;
import com.joyintech.tams.common.base.entity.WflEtrEntrustAdd;
import com.joyintech.tams.common.base.entity.WflEtrEntrustKey;
import com.joyintech.tams.component.business.basicdata.dto.BusiFinProductRatingDto;
import com.joyintech.tams.component.business.basicdata.service.BusiProductRatingService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.riskinfrds.util.EntrustCompleteUtil;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.EtrInvestBondInfoDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 银行间债券指令
 * @author caiqun
 * @version 1.0 2021年10月14日
 * @see CfetsEtrIbBondinfoService
 * @since 1.0
 */
@Service("CfetsEtrIbBondinfoService")
public class CfetsEtrIbBondinfoService extends BaseService{

    @Autowired
    private CfetsEtrIbBondinfoMapper cfetsetrIbBondinfoMapper;

    /**
     * 委托指令流程中Mapper
     */
    @Autowired
    private WflEtrEntrustMapper wflEtrEntrustMapper;

    /**
     * 委托指令附属表流程中Mapper
     */
    @Autowired
    private WflEtrEntrustAddMapper wflEtrEntrustAddMapper;



    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    @Autowired
    private CfetsEtrIbBondinfo002Mapper cfetsEtrIbBondinfo002Mapper;

    /**
     * 产品评级
     **/
    @Autowired
    private BusiProductRatingService busiProductRatingService;
    /**
     * 投资委托信息Service
     */
    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;
    /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;
    /**
     * 委托共通
     */
    @Autowired
    private EtrCommonService etrCommonService;
    
	@Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;
    @Autowired
    private CfetsEtribBondInfo003Service cfetsetribBondInfo003Service;
    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;
    @Autowired
    private EtrInvestBondInfoService etrInvestBondInfoService;
    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;
    @Autowired
    private CfetsIbuQuoteBondMapper cfetsIbuQuoteBondMapper;

    @Autowired
    private PositionInfoService positionInfoService;

    @Autowired
    private SppiImportDataMapper sppiImportDataMapper;
    /**
     * 持仓信息查询
     *
     * @param page int
     * @param length int
     * @param pageParam int
     * @return
     * @see
     * @since 1.0
     */
    public Page<CfetsEtrIbBondinfo001Dto> findPositionsPage(int page, int length,
                                                       Map<String, Object> pageParam) {
        pageParam.put("sysDate", iPlatformDate.getSysDateStr());
        pageParam.put("POSITION_TYPE_03", BussinessDictCodeList.持仓类型_实际.getValue());
        pageParam.put("POSITION_TYPE_04", BussinessDictCodeList.持仓类型_质押.getValue());
        pageParam.put("POSITION_TYPE_05", BussinessDictCodeList.持仓类型_融入.getValue());
        pageParam.put("POSITION_TYPE_06", BussinessDictCodeList.持仓类型_融出.getValue());
        pageParam.put("VALUATION_SOURCE_CSD", BussinessDictCodeList.估值来源_中证登.getValue());
        pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        String portfolioId = (String)pageParam.get("portfolioId");
        if(StringUtil.isNotEmpty(portfolioId)){
            pageParam.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA,portfolioId));
        }
        String finprodId = (String)pageParam.get("finprodId");
        if(StringUtil.isNotEmpty(finprodId)){
            pageParam.put("finprodIdList", StringUtil.splitToList(GlobalConstant.COMMA,finprodId));
        }

        addCondionTradeMarket(pageParam); // 交易场所，仅限银行间指令
        pageParam.put("BOND_PRICE", BussinessConstant.BOND_PRICE);
        Page<CfetsEtrIbBondinfo001Dto> result = super.queryForPage(page, length, () -> cfetsetrIbBondinfoMapper.findPositionsPage(pageParam));

        //根据资产代码查询相关所有的交易信息
        List<CfetsEtrTrdProductDealDto> tradeList = cfetsetrIbBondinfoMapper.getTradeListByFinprodId(pageParam);
        if (result != null && !result.getRoot().isEmpty()) {
            for (CfetsEtrIbBondinfo001Dto dto : result.getRoot()) {
                //昨日持仓数量
                BigDecimal yesterDayPosAmount = dto.getCostPositionNum() == null ? BigDecimal.ZERO : dto.getCostPositionNum();
                //昨日综合成本
                BigDecimal yesterDaycostPosition = dto.getCostPosition() == null ? BigDecimal.ZERO : dto.getCostPosition();
                if (!StringUtil.isEmptyList(tradeList)) {
                    for (CfetsEtrTrdProductDealDto trdProductDeal : tradeList) {
                        if (trdProductDeal.getFinprodId().equals(dto.getFinprodId())
                                && trdProductDeal.getPortfolioId().equals(dto.getPortfolioId())) {
                            if (trdProductDeal.getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                                BigDecimal shareAmt = trdProductDeal.getShareAmt() == null ? BigDecimal.ZERO : trdProductDeal.getShareAmt();
                                BigDecimal unitCprice = trdProductDeal.getUnitCprice() == null ? BigDecimal.ZERO : trdProductDeal.getUnitCprice();
                                if (ArithmeticUtil.doAdd(yesterDayPosAmount, shareAmt).compareTo(BigDecimal.ZERO) != 0) {
                                    //综合成本=昨日成本*昨日持仓数量+净价*份额
                                    yesterDaycostPosition = ArithmeticUtil.multiply(yesterDaycostPosition, yesterDayPosAmount);
                                    yesterDaycostPosition = ArithmeticUtil.doAdd(yesterDaycostPosition, ArithmeticUtil.multiply(shareAmt, unitCprice));
                                    //今日数量= 昨日持仓数量 + 份额
                                    yesterDayPosAmount = ArithmeticUtil.doAdd(yesterDayPosAmount, shareAmt);
                                    //单位成本= 综合成本 / 今日数量
                                    yesterDaycostPosition = ArithmeticUtil.divide(yesterDaycostPosition, yesterDayPosAmount, 12);
                                } else {
                                    yesterDayPosAmount = BigDecimal.ZERO;
                                    yesterDaycostPosition = BigDecimal.ZERO;
                                }
                            }
                            if (trdProductDeal.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                                yesterDayPosAmount = yesterDayPosAmount.subtract(trdProductDeal.getShareAmt() == null ? new BigDecimal(0) : trdProductDeal.getShareAmt());
                            }
                        }
                    }
                }
                //单位持仓成本
                dto.setUnitCost(yesterDaycostPosition);
                //取实时行情
                String secCode = (String) pageParam.get("seccode");
                String url = pageParam.get("MarketInfoInterf_cmdsUrl")+"/rest/markinfo/sec/getMarkinfoBySecCode?seccode=" + secCode;
                Map<String, Object> map = getMarketInfo(url);
                if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("data"))) {
                    Map<String, Object> data = (Map<String, Object>)map.get("data");
                    if(StringUtil.isNotEmpty(data.get("latestCleanPrice"))) {
                        //最新净价取实时行情净价
                        dto.setPrice(new BigDecimal(data.get("latestCleanPrice").toString()).setScale(4, BigDecimal.ROUND_HALF_UP));
                        //持仓市值=持仓券面总额*最新净价/单位面值
                        dto.setPositionsPrice(dto.getShareAmt().multiply(dto.getPrice()).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                }
                //持仓成本=持仓券面总额(万元)*持仓单位成本*100
                //dto.setPositionsAmt(ArithmeticUtil.multiply(dto.getShareAmt(), yesterDaycostPosition, new BigDecimal(100)));
                //持仓成本=持仓数量*持仓单位成本
                dto.setPositionsAmt(ArithmeticUtil.multiply(dto.getAmount(), yesterDaycostPosition).divide(new BigDecimal(10000)));
                //浮动盈亏 = 持仓市值-持仓成本
                dto.setFloatingPl(ArithmeticUtil.subtract(dto.getPositionsPrice(), dto.getPositionsAmt()));
            }
        }
        return result;

    }

    /**
     * 请求市场行情数据接口
     * @param url
     * @return
     */
    public Map<String,Object> getMarketInfo(String url){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1000);
        factory.setReadTimeout(1000);
        RestTemplate template = new RestTemplate(factory);
        try {
            Map<String, Object> quoteMap = template.getForObject(url, Map.class);
            return quoteMap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 只包含交易场所：01 银行间
     * @param pageParam 查询参数
     */
    private void addCondionTradeMarket(Map<String, Object> pageParam) {
        List<String> tradeMarketList = new ArrayList<>();
        tradeMarketList.add(DictCodeListExt1.交易场所_银行间.getValue());
        pageParam.put("tradeMarketList", tradeMarketList);
    }

    /**
     * 调用方式
     * PositionFutureManager.send(createMessage(dto, DictCodeListExt3.流程节点_审批生效.getValue(), DictCodeListExt3.系统动作_新建.getValue()));
     * 冲正方式
     * PositionFutureManager.revoke(dto.getEntrustId(), DictCodeListExt1.子系统代码_委托.getValue(), e);
     * 
     * 撤回
     * PositionRuleInterDto pDto = PositionUtil.initDto(dto.getEntrustId());
     * pDto.setSystemAct(DictCodeListExt3.系统动作_新建.getValue()); // 
     * pDto.setModelType(DictCodeListExt1.子系统代码_委托.getValue());// 委托
     * pDto.setFlowStatus(DictCodeListExt3.流程节点_流程退回.getValue());
     *
     *         // 调用头寸引擎
     *         PositionFutureManager.sendRevoke(pDto);
     *         
     * @param dto
     * @param flowStatus
     * @param systemAct
     * @return
     */
    public PositionBondExecuteParam createMessage(CfetsEtrIbBondinfo002Dto dto, String flowStatus, String systemAct) {

        PositionBondExecuteParam result = PositionUtil.initDto(dto.getEntrustId());
        try {
            // head
            result.setFlowStatus(flowStatus); // 
            result.setModelType(DictCodeListExt1.子系统代码_委托.getValue());

            if (StringUtil.isNotEmpty(dto.getBalanceCheck())){
                result.setBalanceCheck(dto.getBalanceCheck());
            }

            // body 
            result.setBusiType(DictCodeListExt3.头寸业务类型_现券交易.getValue()); // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())){
                result.setBusiType(DictCodeListExt3.头寸业务类型_债券行权交易.getValue()); // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易    
            }

            result.setSettleSpeed(dto.getSettleSpreed()); // 清算速度 
            result.setTradePlace(dto.getTradeMarket()); // 上交所或深交所
            result.setDeliveryType("03"); // 结算方式
            result.setCcy(dto.getCcy()); // 币种
            result.setCdate(dto.getVdate()); // 开始日
            result.setBusiAct(DictCodeListExt3.业务动作_投资指令.getValue()); // 业务动作：01投资指令，02交易登记，03汇总确认，中标结果维护
            result.setPs(dto.getPs());
            result.setSystemAct(systemAct);
            result.setFinprodId(dto.getFinprodId());

            // ptlList
            PositionRulePtlData d = new PositionRulePtlData();
            d.setPortfolioId(dto.getPortfolioId()); // 组合代码
            d.setCashAcctId(EtrCommonCashPositionDealService.select组合托管户(dto.getPortfolioId(), dto.getCcy(), DictCodeListExt2.资金账户类型_托管户.getValue()));
            d.setCashAmount(dto.getFpriceAmt()); // 交易金额
            d.setSecAcctId(dto.getSecAcctId());
            d.setSecAmount(dto.getEntrustNum()); // 本方券面总额
            d.setTradeId(dto.getEntrustId()); // 指令或交易编号
            d.setEtrId(dto.getEntrustId());
            d.setEtrComplete(DictCodeList.是否_是.getValue());
            d.setInvAim(dto.getInvAim());
            d.setIntegrateSecAmount(dto.getIntAmt()); // 场内暂时为空，银行间会有
            result.setPtlList(Arrays.asList(d)); // 融入券券面总额

            return result;
        } catch (Exception e) {
            throw new RuntimeException("指令消息初始化失败", e);
        }
    }
    /**
     * 持仓信息查询
     *
     * @param page int
     * @param length int
     * @param pageParam int
     * @return
     * @see
     * @since 1.0
     */
    public Page<CfetsEtrIbBondPosinfo001Dto> findPositionsPageNew(int page, int length,
                                                               Map<String, Object> pageParam) {
        Page<CfetsEtrIbBondPosinfo001Dto> result = new Page<CfetsEtrIbBondPosinfo001Dto>();
        List<CfetsEtrIbBondPosinfo001Dto> resultList = new ArrayList<CfetsEtrIbBondPosinfo001Dto>();
        pageParam.put("sysDate", iPlatformDate.getSysDateStr());
        //调用头寸，查询当前日期的所有持仓数据
        Map<String, Object> map=new HashMap<>();
        //map.put("cdate",pageParam.get("sysDate"));
        map.put("cdate","2021-09-06");
        String m=PositionHttpRequest.query(map);
        List<Map> list = JsonUtils.json2Object(m, List.class);
        List<String> portfolioIdList=new ArrayList<>();//组合List
        List<String> finprodIdList=new ArrayList<>();//资产List
        for(Map t:list){
            portfolioIdList.add(t.get("portfolioId").toString());
            finprodIdList.add(t.get("finprodId").toString());
        }
        pageParam.put("portfolioIdList", portfolioIdList);
        pageParam.put("finprodIdList", finprodIdList);
        addCondionTradeMarket(pageParam); // 交易场所，仅限银行间指令

        //根据组合信息List，查询出所有的组合信息
        List<CfetsEtrIbBondPosinfo001Dto> portfolioInfoist=cfetsetrIbBondinfoMapper.selectPortfolioInfoist(pageParam);
        //根据资产信息List，查询出所有的资产信息
        List<CfetsEtrIbBondPosinfo001Dto> finprodInfoList=cfetsetrIbBondinfoMapper.selectFinprodInfoList(pageParam);

        //根据接口平台数据组装新的list
        for(Map t:list){
            String portfolioId=t.get("portfolioId").toString();
            String finprodId=t.get("finprodId").toString();
            CfetsEtrIbBondPosinfo001Dto cfetsEtrIbBondPosinfo001Dto=new CfetsEtrIbBondPosinfo001Dto();
            cfetsEtrIbBondPosinfo001Dto.setPortfolioId(portfolioId);
            for(CfetsEtrIbBondPosinfo001Dto prd:portfolioInfoist){
                if(StringUtil.equals(prd.getPortfolioId(),portfolioId)){
                    cfetsEtrIbBondPosinfo001Dto.setPortfolioName(prd.getPortfolioName());
                    break;
                }
            }
            for(CfetsEtrIbBondPosinfo001Dto fin:finprodInfoList){
                if(StringUtil.equals(fin.getFinprodId(),finprodId)){
                    cfetsEtrIbBondPosinfo001Dto.setFinprodMarketId(fin.getFinprodMarketId());
                    cfetsEtrIbBondPosinfo001Dto.setFinprodAbbr(fin.getFinprodAbbr());
                    cfetsEtrIbBondPosinfo001Dto.setSecType1(fin.getSecType1());
                    cfetsEtrIbBondPosinfo001Dto.setSecType1Name(SysDataUtil.getNameFromDataDict(DictCode.行内资产二级分类, fin.getSecType1()));
                    cfetsEtrIbBondPosinfo001Dto.setPrice(fin.getPrice());
                    break;
                }
            }
            List<Map> resultLists= (List<Map>) t.get("resultList");
            for(Map bt:resultLists){
                if(StringUtil.isNotEmpty(bt.get("hodingType"))){
                    if(StringUtil.equals(bt.get("hodingType").toString(),"A_004")){
                        cfetsEtrIbBondPosinfo001Dto.setEtrPledgePrin(new BigDecimal( bt.get("balance").toString()) );
                    }
                    if(StringUtil.equals(bt.get("hodingType").toString(),"A_026")){
                        cfetsEtrIbBondPosinfo001Dto.setEtrAvaPledgePrin(new BigDecimal( bt.get("balance").toString()) );
                    }
                }

            }

            resultList.add(cfetsEtrIbBondPosinfo001Dto);
        }
        result.setRoot(resultList);


        return result;

    }

    /**
     * 指令信息查询
     *
     * @return
     * @see
     * @since 1.0
     */
    public Page<CfetsEtrIbfindEtrinfoResponse> findEtrinfoPage(CfetsEtrIbfindEtrinfoRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());
        pageParam.put("TRADE_MARKET_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
        pageParam.put("ENTRUST_STATUS_08", DictCodeListExt3.委托状态_流程中.getValue());
        List<String> statusList=dto.getEntrustStatus();
        if(!StringUtil.isEmptyList(statusList) && statusList.contains("all")){
           dto.setEntrustStatus(null);
           pageParam.put("entrustStatus",null);
        }
        String portfolioId = (String)pageParam.get("portfolioId");
        if(StringUtil.isNotEmpty(portfolioId)){
            pageParam.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA,portfolioId));
        }
        String finprodId = (String)pageParam.get("finprodId");
        if(StringUtil.isNotEmpty(finprodId)){
            pageParam.put("finprodIdList", StringUtil.splitToList(GlobalConstant.COMMA,finprodId));
        }
       /* if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
            pageParam.put("settleSpeed","0");
        }
        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
            pageParam.put("settleSpeed","1");
        }*/
        String settleSpeed=(String)pageParam.get("settleSpeed");
        if(StringUtil.isNotEmpty(settleSpeed)){
            pageParam.put("settleSpeedList", StringUtil.splitToList(GlobalConstant.COMMA,settleSpeed));
        }
        String ps = (String)pageParam.get("ps");
        if(StringUtil.isNotEmpty(ps)){
            pageParam.put("psList", StringUtil.splitToList(GlobalConstant.COMMA,ps));
        }
        String repManager = (String)pageParam.get("repManager");
        if(StringUtil.isNotEmpty(repManager)){
            pageParam.put("repManagerList", StringUtil.splitToList(GlobalConstant.COMMA,repManager));
        }
        if(StringUtil.isNotEmpty(dto.getSearch())){
            pageParam.put("search_isNumberFlag", BaseMap.isNumeric(dto.getSearch()));
        }

        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.parse(DateUtil.getSysDate()));
        int year = c.get(Calendar.YEAR);
        pageParam.put("year",year);
        pageParam.put("sort",dto.getPageMode().getSort());
        pageParam.put("direction",dto.getPageMode().getDirection());
        String userId = SecurityUtil.getLoginUser().getUserName();
        //String userId = UserUtil.getUserName();
        if (StringUtils.isNotEmpty(userId)) {
            pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsetrIbBondinfoMapper.findEtrinfoPage(pageParam));
        }else{
            return null;
        }

    }

    @Transactional
    public BaseEtrInvestInfoDto saveBaseEtrInvestInfoDto(CfetsEtrIbBondinfo002Dto dto) {
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        try {
            EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
            EtrEntrust etrEntrust = new EtrEntrust();
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
            dto.setInvManagerId(LoginUserUtil.getUserName());
            dto.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            dto.setRecStatus(DictCodeListExt3.委托状态_流程中.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(null);
            etrEntrust.setEntrustNum(dto.getEntrustNum().multiply(new BigDecimal(100))); //前台单位万元
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            //etrEntrustAdd.setOurEntNum(com.joyintech.framework.util.ArithmeticUtil.divide(dto.getEntrustOwnNum(), 100, 4));
            //etrEntrustAdd.setLendinEntNum(com.joyintech.framework.util.ArithmeticUtil.divide(dto.getEntrustIntegrateNum(), 100, 4));
            // 单位全价 fuyongsheng 20210917 edit start
            etrEntrustAdd.setFullPrice(dto.getUnitFprice());
            // 单位全价 fuyongsheng 20210917 edit end
            if (dto.getExerYield() != null) {
                etrEntrustAdd.setExerYield(dto.getExerYield().divide(new BigDecimal(100)));
            }
            if (dto.getMatureYield() != null) {
                etrEntrustAdd.setMatureYield(dto.getMatureYield().divide(new BigDecimal(100)));
            }

            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 0);
            }
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 1);
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            if (entity != null) {
                WflEtrEntrust entrust = new WflEtrEntrust();
                WflEtrEntrustAdd entrustAdd = new WflEtrEntrustAdd();
                BeanUtil.copyPropertiesIgnoreNull(entrust, entity.getEtrEntrust());
                BeanUtil.copyPropertiesIgnoreNull(entrustAdd, entity.getEtrEntrustAdd());
                wflEtrEntrustMapper.insertSelective(entrust);
                wflEtrEntrustAddMapper.insertSelective(entrustAdd);
                createMessage(dto, DictCodeListExt3.流程节点_审批生效.getValue(), DictCodeListExt3.系统动作_新建.getValue());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 持仓列表查询
     * @param requestDto
     * @return
     */
    public Page<CfetsEtrIbBondPosinfo001Dto> queryPositionsPage(CfetsEtrIbBondPosiPage001RequestDto requestDto) {
        String platDate = iPlatformDate.getSysDateStr();
        if (!StringUtil.isNotBlank(requestDto.getCdate())){
            requestDto.setCdate(platDate); 
        }
        // 初始化请求参数
        BaseMap req = getRequestMap(requestDto); 
        // 分页查询列表
        Page<CfetsEtrIbBondPosinfo001Dto> pages = super.queryForPage(
                requestDto.getPageNo(), requestDto.getPageSize(), () -> cfetsetrIbBondinfoMapper.queryPositionsPage(req));
        // 根据查询结果查询估值、交易数据，用来加工单位成本等字段
        final List pList = new ArrayList<String>();
        final List fList = new ArrayList<String>();
        if (pages != null && pages.getRoot() != null && !pages.getRoot().isEmpty()){
            pages.getRoot().stream().forEach(c -> {
                if (!pList.contains(c.getPortfolioId())){
                    pList.add(c.getPortfolioId());
                }
                if (!fList.contains(c.getFinprodId())){
                    fList.add(c.getFinprodId());
                }
            });
        }
        // 查询单位成本数据
        Map<String, CfetsUnitCostDto> unitCostMap = calcEachCostAmtMap(DateUtil.parse(platDate), pList, fList, requestDto.getSecType1());
        // 计算单位成本、计算持仓成本、最新净价、持仓市值、浮动盈亏
        return calculationAmount(unitCostMap, pages, platDate);
    }

    private Page<CfetsEtrIbBondPosinfo001Dto> calculationAmount(Map<String, CfetsUnitCostDto> unitCostMap, Page<CfetsEtrIbBondPosinfo001Dto> pages, String platDate) {
        if(pages != null && pages.getRoot()!= null && !pages.getRoot().isEmpty()){
            pages.getRoot().forEach( r->{
                CfetsUnitCostDto dto = unitCostMap.get(r.getPortfolioId() + r.getFinprodId());
                if (dto != null){
                    // 1.持仓成本（元）
                    r.setPositionsAmt(dto.getBuyCostAmt());
                    // 2.持仓成本价（元） 取单位成本
                    r.setUnitCost(dto.getUnitCost());
                }

                // 3.最新净价（元） 优先从内存获取债券最新净价，没有则使用dto中债券估值行情的值r.getPrice()
                BigDecimal price = BondSocketClient.getLocalCachePrice(r.getFinprodMarketId());
                if (price != null) {
                    r.setPrice(price);
                }else{
                    if (r.getPrice() == null) {
                        // 取标的可用面值
                        price = etrInvestBondInfoService.getAvailableNum(BaseMap.newMap().put("finprodId", r.getFinprodId()).put("sysdate", platDate));
                        r.setPrice(price);
                    }
                }

                if (r.getPrice() != null){
                    // 4.持仓市值（元）=（交易可用券面总额+交易在途券面总额）* 最新净价 / 单位面值
                    BigDecimal doAdd = ArithmeticUtil.multiply(ArithmeticUtil.doAdd(r.getTrdAvaShareAmt(), r.getTrdIntransShareAmt()), 
                            BigDecimal.valueOf(10000L));
                    r.setPositionsPrice(ArithmeticUtil.multiply(doAdd, r.getPrice()).divide(BigDecimal.valueOf(100)));
                    
                    if (dto != null){
                        // 5.持仓成本=（交易可用券面总额+交易在途券面总额）* 持仓成本价（元）
                        r.setPositionsAmt(ArithmeticUtil.multiply(doAdd, r.getUnitCost()).divide(BigDecimal.valueOf(100)));
                    }

                    if (r.getPositionsAmt() != null){
                        // 6.浮动盈亏（元） = 持仓市值（元） - 持仓成本（元）
                        r.setFloatingPl(ArithmeticUtil.subtract(r.getPositionsPrice(), r.getPositionsAmt()));
                    }
                }
            });
        }
        return pages;
    }

    private BaseMap getRequestMap(CfetsEtrIbBondPosiPage001RequestDto requestDto) {
        User user = SecurityUtil.getLoginUser();
        if (user == null){
            throw new RuntimeException("获取不到登录用户!");
        }
        // 市价净价取值估值配置
        String BOND_VALUATION_RULE = CacheUtil.getSysParam("BOND_VALUATION_RULE");
        BaseMap req = BaseMap.newMap(BeanUtil.object2Map(requestDto))
                .put("BOND_VALUATION_RULE", BOND_VALUATION_RULE)
                .put("S_001", DictCodeListExt3.资产类型_指令汇总可用持仓.getValue())
                .put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue())
                .put("A_012", DictCodeListExt3.资产类型_质押式双边回购本方券已质押持仓.getValue())
                .put("A_004", DictCodeListExt3.资产类型_债券本方券可用持仓.getValue())
                .put("A_008", DictCodeListExt3.资产类型_债券本方券在途持仓.getValue())
                .put("A_001", DictCodeListExt3.资产类型_资产账户持仓.getValue())
                .put("VALUATION_SOURCE_CBC", DictCodeList.估值来源_中债登_CBC.getValue())
                .put("VALUATION_SOURCE_SCH", DictCodeList.估值来源_上清所_SCH.getValue())
                .put("REGIST_ORG_01", DictCodeList.登记托管机构_中央国债登记结算有限责任公司.getValue())
                .put("REGIST_ORG_02", DictCodeList.登记托管机构_上海清算所.getValue())
                .put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                .put("search_isNumberFlag", BaseMap.isNumeric(requestDto.getSearch()))
                .put("DATE_TYPE_0", "0") // 头寸处理速度
                .put("userId", user.getUserName())
                ;

        List<String> portfolioIdList = null;
        if (requestDto != null && requestDto.getPortfolioId() != null){
            int index = StringUtil.indexOf(requestDto.getPortfolioId(), ",");
            if (index > 0){
                portfolioIdList = Arrays.asList(requestDto.getPortfolioId().split(","));
                if (portfolioIdList != null && !portfolioIdList.isEmpty()){
                    portfolioIdList.stream().map(s -> {return StringUtil.trim(s.trim());});
                    req.put("portfolioIdList", portfolioIdList).remove("portfolioId");
                }
            }
        }

        List<String> finprodIdList = null;
        if (requestDto != null && requestDto.getFinprodId() != null){
            int index = StringUtil.indexOf(requestDto.getFinprodId(), ",");
            if (index > 0){
                finprodIdList = Arrays.asList(requestDto.getFinprodId().split(","));
                if (finprodIdList != null && !finprodIdList.isEmpty()){
                    finprodIdList.stream().map(s -> {return StringUtil.trim(s.trim());});
                    req.put("finprodIdList", finprodIdList).remove("finprodId");
                }
            }
        }
        return req;
    }

    /**
     * @return
     */
    private Map<String, CfetsUnitCostDto> calcEachCostAmtMap(Date cdate, List<String> portfolioIdList, List<String> finprodIdList, String secType1) {
        /** 返回结果
         * KEY:组合+资产代码+投资目的
         * VALUE:持仓单位成本dto
         */
        Map<String, CfetsUnitCostDto> resultMap = new HashMap<>();
        
        // 查询前一日持仓指标
        List<BaseMap> lastCostAmt = selectLastCostAmt(cdate, portfolioIdList, finprodIdList, secType1);
        
        if (lastCostAmt != null){
            lastCostAmt.forEach(e->{
                if(BigDecimal.ZERO.compareTo(e.getBigDecimal("SHARE_AMT")) != 0){
                    // 数量不为0时生成单位净价
                    CfetsUnitCostDto dto = new CfetsUnitCostDto();

                    dto.setBuyCostAmt(e.getBigDecimal("BUY_COST_AMT"));
                    dto.setShareAmt(e.getBigDecimal("SHARE_AMT"));
                    // 单位成本=成本/数量， （EACH_COST = BUY_COST_AMT / SHARE_AMT）
                    dto.setUnitCost(ArithmeticUtil.divide(
                            e.getBigDecimal("BUY_COST_AMT"),
                            e.getBigDecimal("SHARE_AMT"),
                            4));
                    resultMap.put(getKey(e), dto);
                }
            });
        }
        
        // 当日交易数据查询
        List<BaseMap> currTrdData = selectCurrTrdData(cdate, portfolioIdList, finprodIdList, secType1);
        if (currTrdData != null){
            currTrdData.forEach(i -> {
                String key = getKey(i);
                CfetsUnitCostDto dto = resultMap.get(key);
                if (dto == null){
                    dto = new CfetsUnitCostDto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                    resultMap.put(key, dto);
                }
                
                if (StringUtil.equals(i.getString("PS"), DictCodeList.交易方向_买入.getValue())){
                    // 成本
                    BigDecimal thisBuyCostAmt = dto.getBuyCostAmt()
                            .add(ArithmeticUtil.multiply(i.getBigDecimal("PRICE"), i.getBigDecimal("AMT")));
                    dto.setBuyCostAmt(thisBuyCostAmt);
                    // 数量
                    dto.setShareAmt(dto.getShareAmt().add(i.getBigDecimal("AMT")));
                    // 单位成本 = 成本/数量
                    dto.setUnitCost(ArithmeticUtil.divide(dto.getBuyCostAmt(), dto.getShareAmt(), 4));
                }else{ // 卖出或行权回售
                    // 成本
                    BigDecimal thisBuyCostAmt = dto.getBuyCostAmt()
                            .subtract(ArithmeticUtil.multiply(i.getBigDecimal("PRICE"), i.getBigDecimal("AMT")));
                    dto.setBuyCostAmt(thisBuyCostAmt);
                    // 数量
                    dto.setShareAmt(dto.getShareAmt().subtract(i.getBigDecimal("AMT")));
                    // 单位成本 = 成本/数量
                    dto.setUnitCost(ArithmeticUtil.divide(dto.getBuyCostAmt(), dto.getShareAmt(), 2));
                }
                
                resultMap.put(key, dto);
            });
        }
        return resultMap;
    }

    private static String getKey(BaseMap bm){
        return bm.getString("PORTFOLIO_ID") + bm.get("FINPROD_ID");
    }
    
    /**
     * 查询前一日持仓指标
     * @param cdate 交易日期
     * @param portfolioIdList 组合代码
     * @param secType1 债券一级分类
     */
    public List<BaseMap> selectLastCostAmt(Date cdate, List<String> portfolioIdList, List<String> finprodIdList, String secType1){
        BaseMap reqMap = BaseMap.newMap()
                .put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue())    // 债券
                .put("SVR_TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue())           // 银行间
                .put("cdate", DateUtil.addDay(cdate, -1));

        if(portfolioIdList != null && !portfolioIdList.isEmpty()) reqMap.put("portfolioIdList", portfolioIdList);
        if(finprodIdList != null && !finprodIdList.isEmpty()) reqMap.put("finprodIdList",finprodIdList);
        if(StringUtil.isNotEmpty(secType1)) reqMap.put("secType1",secType1);
        
        List<BaseMap> list = cfetsetrIbBondinfoMapper.selectLastCostAmt(reqMap);
        /**
         * 返回结果 
         * PORTFOLIO_ID、FINPROD_ID、INV_AIM、
         * BUY_COST_AMT 成本
         * SHARE_AMT    数量
         */
        return list;
    }

    /**
     * 当日交易数据查询
     * @param cdate 交易日期
     * @param portfolioIdList 组合代码
     * @param secType1 债券一级分类
     */
    public List<BaseMap> selectCurrTrdData(Date cdate, List<String> portfolioIdList, List<String> finprodIdList, String secType1){
        BaseMap reqMap = BaseMap.newMap()
                .put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue())    // 债券
                .put("SVR_TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue())           // 银行间
                .put("PS_T01", DictCodeList.交易方向_买入.getValue())
                .put("PS_T02", DictCodeList.交易方向_卖出.getValue())
                .put("PS_T03", DictCodeList.交易方向_行权_回售.getValue())
                .put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue())
                .put("YN_N", DictCodeList.是否_否.getValue())
                .put("cdate", cdate);

        if(portfolioIdList != null && !portfolioIdList.isEmpty()) reqMap.put("portfolioIdList", portfolioIdList);
        if(finprodIdList != null && !finprodIdList.isEmpty()) reqMap.put("finprodIdList",finprodIdList);
        if(StringUtil.isNotEmpty(secType1)) reqMap.put("secType1",secType1);

        List<BaseMap> list = cfetsetrIbBondinfoMapper.selectCurrTrdData(reqMap);
        /**
         *  PORTFOLIO_ID、FINPROD_ID、INV_AIM、PS
         *  PRICE,  净价
         *  AMT     数量
         */
        return list;
    }
    
    /**
     * 银行间指令，头寸信息
     * @return
     */
    public List<BaseMap> queryPositionInfo(CfetsEtrIbBondPosiInfoRequestDto requestDto) {
        // 卖空模式
        String sellFlag = CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED");

        // 条件非空校验
        posiInfoCheckCondition(requestDto);

        if (requestDto.getPortfolioIdList() == null || requestDto.getPortfolioIdList().size() == 0){
            return nullData(sellFlag);
        }

        // 从原投管系统取
        BigDecimal cashT = BigDecimal.ZERO; // 指令t日可用
        BigDecimal cashT1 = BigDecimal.ZERO; // 指令t1日可用
        BigDecimal realCash = BigDecimal.ZERO; // 账户现金
        List<String> list = requestDto.getPortfolioIdList();
        if (list != null) {
            for (String portfolioId : list){
                BaseMap paramMap = BaseMap.newMap()
                        .put("finprodId", requestDto.getFinprodId())
                        .put("portfolioId", portfolioId)
                        .put("ccy", DictCodeList.币种_人民币_CNY.getValue())
                        .put("vdate", requestDto.getCdate())
                        .put("calId", "03") // 银行间
                        ;
                Map<String, Object> mapRst = etrCommonService.getProAvailableCash(paramMap);
                if (mapRst != null){ // 指令t日可用，指令t1日可用
                    cashT = ArithmeticUtil.doAdd(cashT, BaseMap.newMap(mapRst).getBigDecimal("cashT"));
                    cashT1 = ArithmeticUtil.doAdd(cashT1, BaseMap.newMap(mapRst).getBigDecimal("cashT1"));
                }
                PtlCashPositionFlowDto positionQueryDto = new PtlCashPositionFlowDto();
                positionQueryDto.setPortfolioId(portfolioId);
                positionQueryDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                positionQueryDto.setCdate(iPlatformDate.getSysDate());
                // 实际现金（账户现金）
                realCash = ArithmeticUtil.doAdd(realCash, basePtlCashPositionInfoService.queryUsableCashPosition(positionQueryDto));
            }
        }
        
        BaseMap bm = BaseMap.newMap()
                .put("S_001", DictCodeListExt3.资产类型_指令汇总可用持仓.getValue())
                .put("S_003", DictCodeListExt3.资产类型_融入券卖空待购回持仓.getValue())
                .put("S_004", DictCodeListExt3.资产类型_汇总可用现金.getValue())
                .put("A_004", DictCodeListExt3.资产类型_债券本方券可用持仓.getValue())
                .put("A_005", DictCodeListExt3.资产类型_债券融入券可用持仓.getValue())
                .put("C_01", DictCodeListExt3.资产类型_现金账户持仓.getValue())
                .put("portfolioIdList", requestDto.getPortfolioIdList())
                .put("cdate", requestDto.getCdate())
                .put("finprodId", requestDto.getFinprodId())
                .put("acctId", requestDto.getAcctId());
        // 查询头寸信息
        Map map = cfetsetrIbBondinfoMapper.queryPositionInfo(bm);
        
        return assemble(sellFlag, map, cashT, cashT1, realCash);
    }

    private void posiInfoCheckCondition(CfetsEtrIbBondPosiInfoRequestDto requestDto) {
        if (requestDto == null){
            throw new RuntimeException("入参不空！");
        }

        if (StringUtil.isEmpty(requestDto.getCdate())){
            requestDto.setCdate(iPlatformDate.getSysDateStr());
        }
    }

    private List<BaseMap> nullData(String flag) {
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

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "02")){
            // 卖空模式设置为模式一、模式二时显示此字段
            /*融入券卖空待购回持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "RRQMKDGHCC")
                    .put("name", "融入券卖空待购回持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "03")){
            // 卖空模式设置为模式一、模式三时显示此字段
            /*资产T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "ZCTRKYCC")
                    .put("name", "资产T日可用持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "03")){
            // 卖空模式设置为模式一、模式三时显示此字段
            /*资产T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "ZCT1RKYCC")
                    .put("name", "资产T+1日可用持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")){
            // 卖空模式设置为模式二时显示此字段
            /*本方券T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "BFQTRKYCC")
                    .put("name", "本方券T日可用持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")){
            // 卖空模式设置为模式二时显示此字段
            /*本方券T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "BFQT1RKYCC")
                    .put("name", "本方券T+1日可用持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")){
            // 卖空模式设置为模式二时显示此字段
            /*融入券T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "RRQTRKYCC")
                    .put("name", "融入券T日可用持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")){
            // 卖空模式设置为模式二时显示此字段
            /*融入券T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", "RRQT1RKYCC")
                    .put("name", "融入券T+1日可用持仓（元）")
                    .put("amount", "--")
                    .put("type", "cc"));
        }
        return resp;
    }

    private String addToStr(BigDecimal a, BigDecimal b){
        if (a == null && b== null){
            return "--";
        }
        return decimalToStr(ArithmeticUtil.doAdd(a,b),"0.00");
    }

    /**
     * 返回前端格式保留小数位
     * @param b
     * @param pattern
     * @return
     */
    private String decimalToStr(BigDecimal b, String pattern){
        if (b == null){
            return "--";
        }
        DecimalFormat df1 = new DecimalFormat(pattern);
        return df1.format(b);
    }
    
    private List<BaseMap> assemble(String flag, Map map, BigDecimal cashT, BigDecimal cashT1, BigDecimal realCash) {
        String T0KYXJ="T0KYXJ",T1KYXJ="T1KYXJ",ZHXJ="ZHXJ",RRQMKDGHCC="RRQMKDGHCC",ZCTRKYCC="ZCTRKYCC";
        String ZCT1RKYCC="ZCT1RKYCC",BFQTRKYCC="BFQTRKYCC",BFQT1RKYCC="BFQT1RKYCC",RRQTRKYCC="RRQTRKYCC",RRQT1RKYCC="RRQT1RKYCC";
        if (map == null) {
            map = new HashMap();
            map.put(T0KYXJ, "0");
            map.put(T1KYXJ, "0");
            map.put(ZHXJ, "0");
//            map.put(RRQMKDGHCC, "0");
//            map.put(ZCTRKYCC, "0");
//            map.put(ZCT1RKYCC, "0");
//            map.put(BFQTRKYCC, "0");
//            map.put(BFQT1RKYCC, "0");
//            map.put(RRQTRKYCC, "0");
//            map.put(RRQT1RKYCC, "0");
        }
        // 初始化返回集合
        List<BaseMap> resp = new ArrayList<>();
        BaseMap bm = BaseMap.newMap(map);
        /**
         * 若老投管现金存在需累计
         */
        if (cashT != null){
            //bm.put(T0KYXJ, addToStr(bm.getBigDecimal(T0KYXJ), cashT));
            bm.put(T0KYXJ, cashT);
        }
        if (cashT1 != null){
           // bm.put(T1KYXJ, addToStr(bm.getBigDecimal(T1KYXJ), cashT1));
            bm.put(T1KYXJ, cashT1);
        }
        if (realCash != null){
            bm.put(ZHXJ, addToStr(bm.getBigDecimal(ZHXJ), realCash));
        }
        
        /*T日可用现金*/
        resp.add(BaseMap.newMap()
                .put("field", T0KYXJ)
                .put("name", "T日可用现金（元）")
                .put("amount", bm.get(T0KYXJ))
                .put("type", "xj"));
        /*T+1日可用现金*/
        resp.add(BaseMap.newMap()
                .put("field", T1KYXJ)
                .put("name", "T+1日可用现金（元）")
                .put("amount", bm.get(T1KYXJ))
                .put("type", "xj"));
        /*账户现金*/
        resp.add(BaseMap.newMap()
                .put("field", ZHXJ)
                .put("name", "账户现金（元）")
                .put("amount", bm.get(ZHXJ))
                .put("type", "xj"));

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "02")){
            // 卖空模式设置为模式一、模式二时显示此字段
            /*融入券卖空待购回持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", RRQMKDGHCC)
                    .put("name", "融入券卖空待购回持仓（元）")
                    .put("amount", decimalToStr(bm.getBigDecimal(RRQMKDGHCC), "0.00"))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "03")){
            // 卖空模式设置为模式一、模式三时显示此字段
            /*资产T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", ZCTRKYCC)
                    .put("name", "资产T日可用持仓（元）")
                    .put("amount", decimalToStr(bm.getBigDecimal(ZCTRKYCC), "0.00"))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "01") || StringUtil.equals(flag, "03")){
            // 卖空模式设置为模式一、模式三时显示此字段
            /*资产T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", ZCT1RKYCC)
                    .put("name", "资产T+1日可用持仓（元）")
                    .put("amount", decimalToStr(bm.getBigDecimal(ZCT1RKYCC), "0.00"))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")){
            // 卖空模式设置为模式二时显示此字段
            /*本方券T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", BFQTRKYCC)
                    .put("name", "本方券T日可用持仓（元）")
                    .put("amount", decimalToStr(bm.getBigDecimal(BFQTRKYCC), "0.00"))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")){
            // 卖空模式设置为模式二时显示此字段
            /*本方券T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", BFQT1RKYCC)
                    .put("name", "本方券T+1日可用持仓（元）")
                    .put("amount", decimalToStr(bm.getBigDecimal(BFQT1RKYCC), "0.00"))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")){
            // 卖空模式设置为模式二时显示此字段
            /*融入券T日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", RRQTRKYCC)
                    .put("name", "融入券T日可用持仓（元）")
                    .put("amount", decimalToStr(bm.getBigDecimal(RRQTRKYCC), "0.00"))
                    .put("type", "cc"));
        }

        if (StringUtil.equals(flag, "02")){
            // 卖空模式设置为模式二时显示此字段
            /*融入券T+1日可用持仓*/
            resp.add(BaseMap.newMap()
                    .put("field", RRQT1RKYCC)
                    .put("name", "融入券T+1日可用持仓（元）")
                    .put("amount", decimalToStr(bm.getBigDecimal(RRQT1RKYCC), "0.00"))
                    .put("type", "cc"));
        }
        return resp;
    }

    /**
     * 获得债券信息
     *
     * @param secId
     * @return dto
     */
    public CfetsEtrFinProductDto getEtrFinProductInfo(String secId) {
        Map<String, Object> map = new HashMap<>();
        map.put("secId",secId);
        map.put("sysDate",iPlatformDate.getSysDate());
        //债券估值行情取值规则 01：优先取中债；02：根据托管机构取值
        map.put("BOND_VALUATION_RULE", CacheUtil.getSysParam("BOND_VALUATION_RULE"));
        map.put("VALUATION_SOURCE_CBC", DictCodeList.估值来源_中债登_CBC.getValue());
        map.put("VALUATION_SOURCE_SCH", DictCodeList.估值来源_上清所_SCH.getValue());
        map.put("REGIST_ORG_01", DictCodeList.登记托管机构_中央国债登记结算有限责任公司.getValue());
        map.put("REGIST_ORG_02", DictCodeList.登记托管机构_上海清算所.getValue());
        map.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        CfetsEtrFinProductDto dto = cfetsetrIbBondinfoMapper.getEtrFinProductInfo(map);
        dto.setLastqx(joyinbusinessCommonService.calPendingPeriod(secId));
        if (dto != null) {
            map.put("marketId",dto.getMarketId());
            //            获取信用评级结果
            List<BusiFinProductRatingDto> ztList = busiProductRatingService.getZtBestRatingByOrgId(map);
            if (ztList != null && ztList.size() > 0) {
//                遍历获取主体评级明细
                for (BusiFinProductRatingDto busdto : ztList) {
                    if (DictCodeListExt3.评级来源类型_外部.getValue().equals(busdto.getInOrOut())) {
//                        主体评级为主体评级明细的评级结果
                        dto.setZtGrade(busdto.getGradeResult());
                    }
                    if (DictCodeListExt3.评级来源类型_内部.getValue().equals(busdto.getInOrOut())) {
                        dto.setZtGrade(busdto.getGradeResult());
                    }
                }
            }

//            以标的市场代码查询最优先债项评级
            List<BusiFinProductRatingDto> zxList = busiProductRatingService.getZxBestRatingByMarketId(map);
            if (zxList != null && zxList.size() > 0) {
//                遍历获取债项评级明细
                for (BusiFinProductRatingDto busdto : zxList) {
                    if (DictCodeListExt3.评级来源类型_外部.getValue().equals(busdto.getInOrOut())) {
//                        主体评级为债项评级明细的评级结果
                        dto.setZxGrade(busdto.getGradeResult());
                    }
                    if (DictCodeListExt3.评级来源类型_内部.getValue().equals(busdto.getInOrOut())) {
                        dto.setZxGrade(busdto.getGradeResult());
                    }

                }
            }

//            估值日<=交易日取最大 估值来源=中债登(SQL中)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                if (dto.getValueDate() != null || !StringUtil.isEmpty(dto.getValueDate())) {
                    Date valueDate = sdf.parse(dto.getValueDate());
                    Date maxDate = cfetsetrIbBondinfoMapper.getMaxTradeDate(secId);
                    if (maxDate != null) {
                        if (maxDate.compareTo(valueDate) <= 0){
                            dto.setValueDate(dto.getValueDate());
                        }else {
                            dto.setValueDate("--");
                        }
                    }else {
                        dto.setValueDate("--");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            调整后的计算结束日>交易日取最小  现金流类型=利息(SQL中)
            try {
                if (dto.getLastPaydate() != null || !StringUtil.isEmpty(dto.getLastPaydate())) {
                    dto.setLastPaydate(dto.getLastPaydate());
                } else {
                    dto.setLastPaydate("--");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //是否含权
            if (dto.getXqFlg() != null || dto.getXqFlg() == "") {
                dto.setXqFlg(dto.getXqFlg().equals("Y")?"是":"否");
            }else {
                dto.setXqFlg("无");
            }

            //付息频率
            if (dto.getPayCycle().equals("月")||dto.getPayCycle().equals("季")||dto.getPayCycle().equals("年")||dto.getPayCycle().equals("半年")) {
                dto.setPayCycle(dto.getPayCycle()+"付");
            }

            //如果估价净价（行权）、估价净价（推荐）为空，显示为--
            if (dto.getPriceXq().equals("元")) {
                dto.setPriceXq("--");
            }
            if (dto.getPriceTj().equals("元")) {
                dto.setPriceTj("--");
            }
            //下一行权日若无，则显示为“—”  计划行权日>交易日取最小
            if (dto.getXqdate() != null || !StringUtil.isEmpty(dto.getXqdate())) {
                try {
                    Date xqDate = sdf.parse(dto.getXqdate());
//                    Date minDate = cfetsetrIbBondinfoMapper.getMinTradeDate(secId);
                    Date sysdate = iPlatformDate.getSysDate();
                    if (sysdate != null && sysdate.compareTo(xqDate) <= 0) {
                        dto.setLastPaydate(dto.getLastPaydate());
                    }else {
                        dto.setXqdate("--");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else {
                dto.setXqdate("--");
            }
//            如果估值收益率没有数据，则显示为空
            if (dto.getGzRate().equals("%")) {
                dto.setGzRate(" ");
            }
//            若为含权债，显示为含权债的行权方式；若有多个行权方式，用分号“;”隔开    非含权债，显示为“无”
//            map.put("sysDate", iPlatformDate.getSysDateStr());
            map.put("EXEC_MODE_01", DictCodeList.行权方式_赎回.getValue());
            map.put("EXEC_MODE_02", DictCodeList.行权方式_回售.getValue());
            map.put("FINPROD_TYPE2_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            String xqfangshi = "";
            List<String> plan = cfetsetrIbBondinfoMapper.getEtrFinExerciseInfo(map);
            if (plan != null && plan.size() > 0) {
                xqfangshi = StringUtil.joinString(plan,";");
            }else {
                xqfangshi = "无";
            }
            dto.setXqFlg(xqfangshi);

            //增加剩余行权期限，到期日-理论行权日
            if (!"--".equals(dto.getXqdate())) {
                long days = DateUtil.getBetweenDayNumber(DateUtil.parse(dto.getXqdate()),DateUtil.parse(dto.getMdate()));
                dto.setLastXQDate(ArithmeticUtil.divide(new BigDecimal(days+""),new BigDecimal("365"),2).toString());
            }else {
                dto.setLastXQDate("--");
            }

        }
        return dto;
    }

    /**
     * 指令信息查询
     *
     * @return
     * @see
     * @since 1.0
     */
    public CfetsEtrIbBondinfoDto findEtrinfo(CfetsEtrInvestBondInfoReqDto dto) {
        return  cfetsetrIbBondinfoMapper.findEtrinfo(dto);
    }

    /**
     * 保存提交时，后台数据转换
     * @param cfetsDto
     * @return
     */
    public CfetsEtribBondInfo003Dto packageEquityForSubmit(CfetsEtribBondInfo003Dto dto) {
        FinProduct fp = null;
        if(StringUtil.isNotEmpty(dto.getFinprodId())) {
            fp = cfetsetrIbBondinfoMapper.findAssetInfo(dto.getFinprodId());
        }
        if(StringUtil.isNotEmpty(fp)){
            dto.setTradeMarket(fp.getTradeMarket());
            dto.setCalendarId(fp.getCalendarId());
            dto.setCcy(fp.getCcy());
            Date entrustDate=DateUtil.compareDate(dto.getEtrDate(),fp.getVdate())>0?dto.getEtrDate():fp.getVdate();
            dto.setEntrustDate(entrustDate);
            dto.setFinvdate(fp.getVdate());
            dto.setFinmdate(fp.getMdate());
            dto.setFinprodAbbr(fp.getFinprodAbbr());
        }
        dto.setInvManagerId(SecurityUtil.getLoginUser().getUserName());
        dto.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        dto.setElementId(dto.getElementId());
        List<CfetsBussCombDistribtDto> bussCombDistribtList = dto.getBussCombDistribtList();
        List<CfetsEtribBondInfo003Dto> etrInvestBondInfoDtosList=new ArrayList<>();
        //本方券券面总额
        BigDecimal bfqtrkyccSAmt=BigDecimal.ZERO;
        //融入券卖空待购回持仓
        BigDecimal rrqmkdghSAmt=BigDecimal.ZERO;
        for(CfetsBussCombDistribtDto item:bussCombDistribtList){
            CfetsEtribBondInfo003Dto dd=new CfetsEtribBondInfo003Dto();
            BeanUtil.copyPropertiesIgnoreNull(dd,dto);
            //流程中数据对象重复  取值有问题 这个处理为空
            dd.setEtrInvestBondInfoDtos(null);
            dd.setPortfolioId(item.getPortfolioId());
            dd.setTradeAmt(item.getEntrustNum());
            dd.setEntrustNum(item.getEntrustNum());
            dd.setInvAim(item.getBusinessModel());
            dd.setBusinessModel(item.getBusinessModel());
            dd.setEntrustOwnNum(item.getEntrustOwnNum());
            dd.setEntrustIntegrateNum(item.getEntrustIntegrateNum());
            dd.setElementId(item.getElementId());
            dd.setEntrustId(item.getEntrustId());
            //根据卖空模式对本方券和融入券/卖空购回券面总额进行赋值
            String shortSellingSupported=CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED");
            if(StringUtil.equals(shortSellingSupported,"01")){
                //只有券面总额
                //查询系统融入券待购回持仓和本方券T日可用持仓
                BaseMap bm = BaseMap.newMap()
                        .put("S_003", DictCodeListExt3.资产类型_融入券卖空待购回持仓.getValue())
                        .put("A_004", DictCodeListExt3.资产类型_债券本方券可用持仓.getValue())
                        .put("portfolioId", item.getPortfolioId())
                        .put("cdate", dto.getEtrDate())
                        .put("finprodId", dto.getFinprodId());
                // 查询头寸信息
                Map map = cfetsetrIbBondinfoMapper.queryPositionByEtrInfo(bm);
                //本方券T日可用持仓
                BigDecimal bfqtrkyccAmt=BigDecimal.ZERO;
                if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("BFQTRKYCC"))){
                    bfqtrkyccAmt=new BigDecimal(map.get("BFQTRKYCC").toString());
                }
                //融入券卖空待购回持仓
                BigDecimal rrqmkdghAmt=BigDecimal.ZERO;
                if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("RRQMKDGH"))){
                    rrqmkdghAmt=new BigDecimal(map.get("RRQMKDGH").toString());
                }
                if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_买入.getValue())){
                    BigDecimal integrateSecAmount=rrqmkdghAmt;
                    if(integrateSecAmount.compareTo(item.getEntrustNum())>0){
                        dd.setEntrustIntegrateNum(item.getEntrustNum());
                        dd.setEntrustOwnNum(BigDecimal.ZERO);
                    }else{
                        dd.setEntrustIntegrateNum(integrateSecAmount);
                        dd.setEntrustOwnNum(ArithmeticUtil.subtract(item.getEntrustNum(),integrateSecAmount));
                    }
                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_卖出.getValue())){
                    //查询系统本方券可用持仓
                    BigDecimal ownSecAmount=bfqtrkyccAmt;
                    if(ownSecAmount.compareTo(item.getEntrustNum())>0){
                        dd.setEntrustOwnNum(item.getEntrustNum());
                        dd.setEntrustIntegrateNum(BigDecimal.ZERO);
                    }else{
                        dd.setEntrustOwnNum(ownSecAmount);
                        dd.setEntrustIntegrateNum(ArithmeticUtil.subtract(item.getEntrustNum(),ownSecAmount));
                    }
                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_行权_回售.getValue())){
                    dd.setEntrustOwnNum(item.getEntrustNum());
                    dd.setEntrustIntegrateNum(BigDecimal.ZERO);
                }
                bfqtrkyccSAmt=ArithmeticUtil.doAdd(bfqtrkyccSAmt,dd.getEntrustOwnNum());
                rrqmkdghSAmt=ArithmeticUtil.doAdd(rrqmkdghSAmt,dd.getEntrustIntegrateNum());
            }else if(StringUtil.equals(shortSellingSupported,"02")){
                if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(dto.getPs(),DictCodeList.交易方向_卖出.getValue())){
                    dd.setEntrustOwnNum(item.getEntrustOwnNum()==null?BigDecimal.ZERO:item.getEntrustOwnNum());
                    dd.setEntrustIntegrateNum(item.getEntrustIntegrateNum()==null?BigDecimal.ZERO:item.getEntrustIntegrateNum());
                    bfqtrkyccSAmt=ArithmeticUtil.doAdd(bfqtrkyccSAmt,dd.getEntrustOwnNum());
                    rrqmkdghSAmt=ArithmeticUtil.doAdd(rrqmkdghSAmt,dd.getEntrustIntegrateNum());
                }
                if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_行权_回售.getValue())){
                    dd.setEntrustOwnNum(item.getEntrustNum());
                    dd.setEntrustIntegrateNum(BigDecimal.ZERO);
                    bfqtrkyccSAmt=ArithmeticUtil.doAdd(bfqtrkyccSAmt,dd.getEntrustOwnNum());
                    rrqmkdghSAmt=BigDecimal.ZERO;
                }

            }else if(StringUtil.equals(shortSellingSupported,"03")){
                dd.setEntrustOwnNum(item.getEntrustNum());
                dd.setEntrustIntegrateNum(BigDecimal.ZERO);
                bfqtrkyccSAmt=ArithmeticUtil.doAdd(bfqtrkyccSAmt,dd.getEntrustOwnNum());
                rrqmkdghSAmt=BigDecimal.ZERO;
            }
            //根据组合id查询组合信息
            PtlPortfolio pp=cfetsetrIbBondinfoMapper.findPtlInfo(item.getPortfolioId());
            if(null!=pp){
                item.setPortfolioName(pp.getPortfolioName());
            }
            //针对组合分配中的数据查询头寸信息
            item.setEntrustOwnNum(dd.getEntrustOwnNum());
            item.setEntrustIntegrateNum(dd.getEntrustIntegrateNum());
            //查询系统融入券待购回持仓和本方券T日可用持仓

            // 从原投管系统取
            BigDecimal cashT = BigDecimal.ZERO; // 指令t日可用
            BigDecimal cashT1 = BigDecimal.ZERO; // 指令t1日可用
            BigDecimal realCash = BigDecimal.ZERO; // 账户现金
            BaseMap paramMap = BaseMap.newMap()
                    .put("finprodId", dto.getFinprodId())
                    .put("portfolioId", item.getPortfolioId())
                    .put("ccy", fp.getCcy())
                    .put("vdate", dto.getEtrDate())
                    .put("calId", "03") // 银行间
                    ;
            Map<String, Object> mapRst = etrCommonService.getProAvailableCash(paramMap);
            if (mapRst != null){ // 指令t日可用，指令t1日可用
                cashT = ArithmeticUtil.doAdd(cashT, BaseMap.newMap(mapRst).getBigDecimal("cashT"));
                cashT1 = ArithmeticUtil.doAdd(cashT1, BaseMap.newMap(mapRst).getBigDecimal("cashT1"));
            }
            PtlCashPositionFlowDto positionQueryDto = new PtlCashPositionFlowDto();
            positionQueryDto.setPortfolioId(item.getPortfolioId());
            positionQueryDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            positionQueryDto.setCdate(iPlatformDate.getSysDate());
            // 实际现金（账户现金）
            realCash = ArithmeticUtil.doAdd(realCash, basePtlCashPositionInfoService.queryUsableCashPosition(positionQueryDto));
            List<String> portfolioIdList=new ArrayList<>();
            portfolioIdList.add(item.getPortfolioId());

            BaseMap bm = BaseMap.newMap()
                    .put("S_001", DictCodeListExt3.资产类型_指令汇总可用持仓.getValue())
                    .put("S_003", DictCodeListExt3.资产类型_融入券卖空待购回持仓.getValue())
                    .put("S_004", DictCodeListExt3.资产类型_汇总可用现金.getValue())
                    .put("A_004", DictCodeListExt3.资产类型_债券本方券可用持仓.getValue())
                    .put("A_005", DictCodeListExt3.资产类型_债券融入券可用持仓.getValue())
                    .put("C_01", DictCodeListExt3.资产类型_现金账户持仓.getValue())
                    .put("portfolioIdList", portfolioIdList)
                    .put("cdate", DateUtil.format(dto.getEtrDate(),"yyyy-MM-dd"))
                    .put("finprodId", dto.getFinprodId())
                    .put("acctId", null);
            // 查询头寸信息
            Map map = cfetsetrIbBondinfoMapper.queryPositionInfo(bm);
           /* if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("T0KYXJ"))){
                cashT=ArithmeticUtil.doAdd(new BigDecimal(map.get("T0KYXJ").toString()), cashT);
            }*/
            item.setProAvailableCash(cashT);
            /*if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("T1KYXJ"))){
                cashT1=ArithmeticUtil.doAdd(new BigDecimal(map.get("T1KYXJ").toString()), cashT1);
            }*/
            item.setProAvailableCash1(cashT1);
            if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("ZHXJ"))){
                realCash=ArithmeticUtil.doAdd(new BigDecimal(map.get("ZHXJ").toString()), realCash);
            }
            item.setSecAcctAvailableCash(realCash);
            //融入券卖空购回持仓
            BigDecimal rrqmkghccAmt=BigDecimal.ZERO;
            if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("RRQMKDGHCC"))){
                rrqmkghccAmt=new BigDecimal(map.get("RRQMKDGHCC").toString());
            }
            item.setEntrustIntegrateSecNum(rrqmkghccAmt);
            //本方券T日可用持仓
            BigDecimal bfqtrkyccAmt=BigDecimal.ZERO;
            if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("BFQTRKYCC"))){
                bfqtrkyccAmt=new BigDecimal(map.get("BFQTRKYCC").toString());
            }
            item.setOwnPorAvailableSec(bfqtrkyccAmt);
            //本方券T+1日可用持仓
            BigDecimal bfqt1rkyccAmt=BigDecimal.ZERO;
            if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("BFQT1RKYCC"))){
                bfqt1rkyccAmt=new BigDecimal(map.get("BFQT1RKYCC").toString());
            }
            item.setOwnProAvailableSec1(bfqt1rkyccAmt);
            //资产T日可用持仓
            BigDecimal zctrkyccAmt=BigDecimal.ZERO;
            if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("ZCTRKYCC"))){
                zctrkyccAmt=new BigDecimal(map.get("ZCTRKYCC").toString());
            }
            item.setPorAvailableSec(zctrkyccAmt);

            //资产T+1日可用持仓
            BigDecimal zct1rkyccAmt=BigDecimal.ZERO;
            if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("ZCT1RKYCC"))){
                zct1rkyccAmt=new BigDecimal(map.get("ZCT1RKYCC").toString());
            }
            item.setPorAvailableSec1(zct1rkyccAmt);
            //针对退回指令，查询指令下达时间
            if(StringUtil.isNotEmpty(dto.getEntrustId())){
                //根据指令id查询出该资产是退回指令，且该指令的下达时间
                CfetsEtribBondInfo003Dto transmitTimeDto=cfetsetrIbBondinfoMapper.selectTransmitTimeByInfo(dto.getEntrustId());
                if(null!=transmitTimeDto && StringUtil.isNotEmpty(transmitTimeDto.getTransmitTime())){
                    dd.setTransmitTime(transmitTimeDto.getTransmitTime());
                    dto.setTransmitTime(transmitTimeDto.getTransmitTime());
                }
                dd.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
                dto.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
            }else{
                dd.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
                dto.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
            }
            etrInvestBondInfoDtosList.add(dd);
        }
        dto.setEntrustOwnNum(bfqtrkyccSAmt);
        dto.setEntrustIntegrateNum(rrqmkdghSAmt);
        //查询新建金额数据计算
        CfetsEtrCalcAmountRequest calcAmtReq=new CfetsEtrCalcAmountRequest();
        calcAmtReq.setShareNum(dto.getEntrustNum());
        calcAmtReq.setEntrustIntegrateNum(dto.getEntrustIntegrateNum());
        calcAmtReq.setEntrustOwnNum(dto.getEntrustOwnNum());
        calcAmtReq.setPrice(dto.getPrice());
        calcAmtReq.setUnitFprice(dto.getUnitFprice());
        CfetsEtrCalcAmountResponse calcAmtRes=cfetsetribBondInfo003Service.calAmountData(calcAmtReq);
        dto.setPriceAmt(calcAmtRes.getPriceAmt());
        dto.setUnitInt(calcAmtRes.getUnitInt());
        dto.setUnitIntAmt(calcAmtRes.getUnitIntAmt());
        dto.setUnitFpriceAmt(calcAmtRes.getUnitFpriceAmt());
        dto.setEntrustOwnInt(calcAmtRes.getEntrustOwnInt());
        dto.setEntrustIntegrateInt(calcAmtRes.getEntrustIntegrateInt());
        dto.setEntrustOwnUnitFpriceAmt(calcAmtRes.getEntrustOwnUnitFpriceAmt());
        dto.setEntrustIntegrateUnitFpriceAmt(calcAmtRes.getEntrustIntegrateUnitFpriceAmt());
        //风险
    /*    CfetsEtribdoRiskcalResponse riskDto=cfetsDto.getCfetsEtribdoRiskcalReturn();

        List<RiskTrialRetDto> riskTrialRetDto=new ArrayList<>();
        if(!StringUtil.isEmptyList(dto.getRiskTrialRetDto())){
            for(CfetsEtribdoRiskcalResponse it:riskDto.getRiskcalReturnList()){
                RiskTrialRetDto t=new RiskTrialRetDto();
                BeanUtil.copyPropertiesIgnoreNull(t,it);
                t.setConclusion(riskDto.getRiskTrialSpan());
                riskTrialRetDto.add(t);
            }
        }
        dto.setRiskTrialRetDto(riskTrialRetDto);*/
        dto.setEtrInvestBondInfoDtos(etrInvestBondInfoDtosList);
        return dto;
    }

    /**
     * 保存提交时，后台数据转换
     * @param cfetsDto
     * @return
     */
    public CfetsEtribBondInfo003Dto packageEquity(CfetsEtribSaveRequest cfetsDto) {
        FinProduct fp = null;
        if(StringUtil.isNotEmpty(cfetsDto.getFinprodId())) {
            fp = cfetsetrIbBondinfoMapper.findAssetInfo(cfetsDto.getFinprodId());
        }
        CfetsEtribBondInfo003Dto dto = new CfetsEtribBondInfo003Dto();
        BeanUtil.copyPropertiesIgnoreNull(dto,cfetsDto);
        if(StringUtil.isNotEmpty(fp)){
            dto.setTradeMarket(fp.getTradeMarket());
            dto.setCalendarId(fp.getCalendarId());
            dto.setCcy(fp.getCcy());
            Date entrustDate=DateUtil.compareDate(cfetsDto.getEtrDate(),fp.getVdate())>0?cfetsDto.getEtrDate():fp.getVdate();
            dto.setEntrustDate(entrustDate);
            dto.setFinvdate(fp.getVdate());
            dto.setFinmdate(fp.getMdate());
            dto.setFinprodAbbr(fp.getFinprodAbbr());
        }
        dto.setInvManagerId(SecurityUtil.getLoginUser().getUserName());
        dto.setEntrustNum(cfetsDto.getTradeAmt());
        dto.setVdate(cfetsDto.getEtrDate());
        dto.setMdate(cfetsDto.getEtrDate());
        dto.setSettleSpeed(cfetsDto.getSettleSpeed());
        dto.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        dto.setElementId(cfetsDto.getElementId());
        dto.setAssetType11(cfetsDto.getAssetType11());
        List<CfetsBussCombDistribtDto> bussCombDistribtList = dto.getBussCombDistribtList();
        List<CfetsEtribBondInfo003Dto> etrInvestBondInfoDtosList=new ArrayList<>();
        //本方券券面总额
        BigDecimal bfqtrkyccSAmt=BigDecimal.ZERO;
        //融入券卖空待购回持仓
        BigDecimal rrqmkdghSAmt=BigDecimal.ZERO;
        for(CfetsBussCombDistribtDto item:bussCombDistribtList){
            if(Objects.isNull(item.getEntrustNum())) {
                item.setEntrustNum(BigDecimal.ZERO);
            }
            CfetsEtribBondInfo003Dto dd=new CfetsEtribBondInfo003Dto();
            BeanUtil.copyPropertiesIgnoreNull(dd,dto);
            dd.setPortfolioId(item.getPortfolioId());
            dd.setTradeAmt(item.getEntrustNum());
            dd.setEntrustNum(item.getEntrustNum());
            dd.setInvAim(item.getBusinessModel());
            dd.setBusinessModel(item.getBusinessModel());
            dd.setEntrustOwnNum(item.getEntrustOwnNum());
            dd.setEntrustIntegrateNum(item.getEntrustIntegrateNum());
            dd.setElementId(item.getElementId());
            //根据卖空模式对本方券和融入券/卖空购回券面总额进行赋值
            String shortSellingSupported=CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED");
            if(StringUtil.equals(shortSellingSupported,"01")){
                //只有券面总额
                //查询系统融入券待购回持仓和本方券T日可用持仓
                BaseMap bm = BaseMap.newMap()
                        .put("S_003", DictCodeListExt3.资产类型_融入券卖空待购回持仓.getValue())
                        .put("A_004", DictCodeListExt3.资产类型_债券本方券可用持仓.getValue())
                        .put("portfolioId", item.getPortfolioId())
                        .put("cdate", cfetsDto.getEtrDate())
                        .put("finprodId", cfetsDto.getFinprodId());
                // 查询头寸信息
                Map map = cfetsetrIbBondinfoMapper.queryPositionByEtrInfo(bm);
                //本方券T日可用持仓
                BigDecimal bfqtrkyccAmt=BigDecimal.ZERO;
                if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("BFQTRKYCC"))){
                    bfqtrkyccAmt=new BigDecimal(map.get("BFQTRKYCC").toString());
                }
                //融入券卖空待购回持仓
                BigDecimal rrqmkdghAmt=BigDecimal.ZERO;
                if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("RRQMKDGH"))){
                    rrqmkdghAmt=new BigDecimal(map.get("RRQMKDGH").toString());
                }
                if(StringUtil.equals(cfetsDto.getPs(),DictCodeList.交易方向_买入.getValue())){
                    BigDecimal integrateSecAmount=rrqmkdghAmt;
                    if(integrateSecAmount.compareTo(item.getEntrustNum())>0){
                        dd.setEntrustIntegrateNum(item.getEntrustNum());
                        dd.setEntrustOwnNum(BigDecimal.ZERO);
                    }else{
                        dd.setEntrustIntegrateNum(integrateSecAmount);
                        dd.setEntrustOwnNum(ArithmeticUtil.subtract(item.getEntrustNum(),integrateSecAmount));
                    }
                }else if(StringUtil.equals(cfetsDto.getPs(),DictCodeList.交易方向_卖出.getValue())){
                    //查询系统本方券可用持仓
                    BigDecimal ownSecAmount = bfqtrkyccAmt;

                    if(ownSecAmount.compareTo(item.getEntrustNum())>0){
                        dd.setEntrustOwnNum(item.getEntrustNum());
                        dd.setEntrustIntegrateNum(BigDecimal.ZERO);
                    }else{
                        dd.setEntrustOwnNum(ownSecAmount);
                        dd.setEntrustIntegrateNum(ArithmeticUtil.subtract(item.getEntrustNum(),ownSecAmount));
                    }
                }else if(StringUtil.equals(cfetsDto.getPs(),DictCodeList.交易方向_行权_回售.getValue())){
                    dd.setEntrustOwnNum(item.getEntrustNum());
                    dd.setEntrustIntegrateNum(BigDecimal.ZERO);
                }
                bfqtrkyccSAmt=ArithmeticUtil.doAdd(bfqtrkyccSAmt,dd.getEntrustOwnNum());
                rrqmkdghSAmt=ArithmeticUtil.doAdd(rrqmkdghSAmt,dd.getEntrustIntegrateNum());
            }else if(StringUtil.equals(shortSellingSupported,"02")){
                if(StringUtil.equals(cfetsDto.getPs(),DictCodeList.交易方向_买入.getValue())
                || StringUtil.equals(cfetsDto.getPs(),DictCodeList.交易方向_卖出.getValue())){
                    dd.setEntrustOwnNum(item.getEntrustOwnNum()==null?BigDecimal.ZERO:item.getEntrustOwnNum());
                    dd.setEntrustIntegrateNum(item.getEntrustIntegrateNum()==null?BigDecimal.ZERO:item.getEntrustIntegrateNum());
                    bfqtrkyccSAmt=ArithmeticUtil.doAdd(bfqtrkyccSAmt,dd.getEntrustOwnNum());
                    rrqmkdghSAmt=ArithmeticUtil.doAdd(rrqmkdghSAmt,dd.getEntrustIntegrateNum());
                }
                if(StringUtil.equals(cfetsDto.getPs(),DictCodeList.交易方向_行权_回售.getValue())){
                    dd.setEntrustOwnNum(item.getEntrustNum());
                    dd.setEntrustIntegrateNum(BigDecimal.ZERO);
                    bfqtrkyccSAmt=ArithmeticUtil.doAdd(bfqtrkyccSAmt,dd.getEntrustOwnNum());
                    rrqmkdghSAmt=BigDecimal.ZERO;
                }

            }else if(StringUtil.equals(shortSellingSupported,"03")){
                dd.setEntrustOwnNum(item.getEntrustNum());
                dd.setEntrustIntegrateNum(BigDecimal.ZERO);
                bfqtrkyccSAmt=ArithmeticUtil.doAdd(bfqtrkyccSAmt,dd.getEntrustOwnNum());
                rrqmkdghSAmt=BigDecimal.ZERO;
            }
            //根据组合id查询组合信息
            PtlPortfolio pp=cfetsetrIbBondinfoMapper.findPtlInfo(item.getPortfolioId());
            if(null!=pp){
                item.setPortfolioName(pp.getPortfolioName());
            }
            //针对组合分配中的数据查询头寸信息
            item.setEntrustOwnNum(dd.getEntrustOwnNum());
            item.setEntrustIntegrateNum(dd.getEntrustIntegrateNum());
            //查询系统融入券待购回持仓和本方券T日可用持仓

            // 从原投管系统取
            BigDecimal cashT = BigDecimal.ZERO; // 指令t日可用
            BigDecimal cashT1 = BigDecimal.ZERO; // 指令t1日可用
            BigDecimal realCash = BigDecimal.ZERO; // 账户现金
            BaseMap paramMap = BaseMap.newMap()
                    .put("finprodId", cfetsDto.getFinprodId())
                    .put("portfolioId", item.getPortfolioId())
                    .put("ccy", fp != null ? fp.getCcy() : null)
                    .put("vdate", DateUtil.format(cfetsDto.getEtrDate()))
                    .put("calId", "03"); // 银行间

            Map<String, Object> mapRst = etrCommonService.getProAvailableCash(paramMap);
            if (mapRst != null){ // 指令t日可用，指令t1日可用
                cashT = ArithmeticUtil.doAdd(cashT, BaseMap.newMap(mapRst).getBigDecimal("cashT"));
                cashT1 = ArithmeticUtil.doAdd(cashT1, BaseMap.newMap(mapRst).getBigDecimal("cashT1"));
            }
            PtlCashPositionFlowDto positionQueryDto = new PtlCashPositionFlowDto();
            positionQueryDto.setPortfolioId(item.getPortfolioId());
            positionQueryDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            positionQueryDto.setCdate(iPlatformDate.getSysDate());
            // 实际现金（账户现金）
            realCash = ArithmeticUtil.doAdd(realCash, basePtlCashPositionInfoService.queryUsableCashPosition(positionQueryDto));
            List<String> portfolioIdList=new ArrayList<>();
            portfolioIdList.add(item.getPortfolioId());

            BaseMap bm = BaseMap.newMap()
                    .put("S_001", DictCodeListExt3.资产类型_指令汇总可用持仓.getValue())
                    .put("S_003", DictCodeListExt3.资产类型_融入券卖空待购回持仓.getValue())
                    .put("S_004", DictCodeListExt3.资产类型_汇总可用现金.getValue())
                    .put("A_004", DictCodeListExt3.资产类型_债券本方券可用持仓.getValue())
                    .put("A_005", DictCodeListExt3.资产类型_债券融入券可用持仓.getValue())
                    .put("C_01", DictCodeListExt3.资产类型_现金账户持仓.getValue())
                    .put("portfolioIdList", portfolioIdList)
                    .put("cdate", DateUtil.format(cfetsDto.getEtrDate(),"yyyy-MM-dd"))
                    .put("finprodId", cfetsDto.getFinprodId())
                    .put("acctId", null);
            // 查询头寸信息
            Map map = cfetsetrIbBondinfoMapper.queryPositionInfo(bm);
           /* if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("T0KYXJ"))){
                cashT=ArithmeticUtil.doAdd(new BigDecimal(map.get("T0KYXJ").toString()), cashT);
            }*/
            item.setProAvailableCash(cashT);
            /*if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("T1KYXJ"))){
                cashT1=ArithmeticUtil.doAdd(new BigDecimal(map.get("T1KYXJ").toString()), cashT1);
            }*/
            item.setProAvailableCash1(cashT1);
            if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("ZHXJ"))){
                realCash=ArithmeticUtil.doAdd(new BigDecimal(map.get("ZHXJ").toString()), realCash);
            }
            item.setSecAcctAvailableCash(realCash);
            //融入券卖空购回持仓
            BigDecimal rrqmkghccAmt=BigDecimal.ZERO;
            if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("RRQMKDGHCC"))){
                rrqmkghccAmt=new BigDecimal(map.get("RRQMKDGHCC").toString());
            }
            item.setEntrustIntegrateSecNum(rrqmkghccAmt);
            //本方券T日可用持仓
            BigDecimal bfqtrkyccAmt=BigDecimal.ZERO;
            if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("BFQTRKYCC"))){
                bfqtrkyccAmt=new BigDecimal(map.get("BFQTRKYCC").toString());
            }
            item.setOwnPorAvailableSec(bfqtrkyccAmt);
            //本方券T+1日可用持仓
            BigDecimal bfqt1rkyccAmt=BigDecimal.ZERO;
            if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("BFQT1RKYCC"))){
                bfqt1rkyccAmt=new BigDecimal(map.get("BFQT1RKYCC").toString());
            }
            item.setOwnProAvailableSec1(bfqt1rkyccAmt);
            //资产T日可用持仓
            BigDecimal zctrkyccAmt=BigDecimal.ZERO;
            if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("ZCTRKYCC"))){
                zctrkyccAmt=new BigDecimal(map.get("ZCTRKYCC").toString());
            }
            item.setPorAvailableSec(zctrkyccAmt);

            //资产T+1日可用持仓
            BigDecimal zct1rkyccAmt=BigDecimal.ZERO;
            if(StringUtil.isNotEmpty(map) && StringUtil.isNotEmpty(map.get("ZCT1RKYCC"))){
                zct1rkyccAmt=new BigDecimal(map.get("ZCT1RKYCC").toString());
            }
            item.setPorAvailableSec1(zct1rkyccAmt);
            //针对退回指令，查询指令下达时间
            if(StringUtil.isNotEmpty(cfetsDto.getEntrustId())){
                //根据指令id查询出该资产是退回指令，且该指令的下达时间
                CfetsEtribBondInfo003Dto transmitTimeDto=cfetsetrIbBondinfoMapper.selectTransmitTimeByInfo(cfetsDto.getEntrustId());
                if(null!=transmitTimeDto && StringUtil.isNotEmpty(transmitTimeDto.getTransmitTime())){
                    dd.setTransmitTime(transmitTimeDto.getTransmitTime());
                    dto.setTransmitTime(transmitTimeDto.getTransmitTime());
                }
                dd.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
                dto.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
            }else{
                dd.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
                dto.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
            }
            etrInvestBondInfoDtosList.add(dd);
        }
        dto.setEntrustOwnNum(bfqtrkyccSAmt);
        dto.setEntrustIntegrateNum(rrqmkdghSAmt);
        //查询新建金额数据计算
        CfetsEtrCalcAmountRequest calcAmtReq=new CfetsEtrCalcAmountRequest();
        calcAmtReq.setShareNum(dto.getEntrustNum());
        calcAmtReq.setEntrustIntegrateNum(dto.getEntrustIntegrateNum());
        calcAmtReq.setEntrustOwnNum(dto.getEntrustOwnNum());
        calcAmtReq.setPrice(dto.getPrice());
        calcAmtReq.setUnitFprice(dto.getUnitFprice());
        CfetsEtrCalcAmountResponse calcAmtRes=cfetsetribBondInfo003Service.calAmountData(calcAmtReq);
        dto.setPriceAmt(calcAmtRes.getPriceAmt());
        dto.setUnitInt(calcAmtRes.getUnitInt());
        dto.setUnitIntAmt(calcAmtRes.getUnitIntAmt());
        dto.setUnitFpriceAmt(calcAmtRes.getUnitFpriceAmt());
        dto.setEntrustOwnInt(calcAmtRes.getEntrustOwnInt());
        dto.setEntrustIntegrateInt(calcAmtRes.getEntrustIntegrateInt());
        dto.setEntrustOwnUnitFpriceAmt(calcAmtRes.getEntrustOwnUnitFpriceAmt());
        dto.setEntrustIntegrateUnitFpriceAmt(calcAmtRes.getEntrustIntegrateUnitFpriceAmt());
        //风险
        CfetsEtribdoRiskcalResponse riskDto=cfetsDto.getCfetsEtribdoRiskcalReturn();
        List<RiskTrialRetDto> riskTrialRetDto=new ArrayList<>();
        if(StringUtil.isNotEmpty(riskDto) && !StringUtil.isEmptyList(riskDto.getRiskcalReturnList())){
            for(CfetsEtribdoRiskcalResponse it:riskDto.getRiskcalReturnList()){
                RiskTrialRetDto t=new RiskTrialRetDto();
                BeanUtil.copyPropertiesIgnoreNull(t,it);
                t.setConclusion(riskDto.getRiskTrialSpan());
                riskTrialRetDto.add(t);
            }
        }
        dto.setRiskTrialRetDto(riskTrialRetDto);
        dto.setEtrInvestBondInfoDtos(etrInvestBondInfoDtosList);

        return dto;
    }

    /**
     * 保存提交时check检查
     * @param allDto
     * @param br
     * @return
     */
    public AjaxResult  checkSaveErrorMethod(CfetsEtribSaveRequest allDto, BindingResult br) {
        List<ValidMessage> messages = new ArrayList<>();
        if(!StringUtil.isNotEmpty(allDto.getFinprodId())){
            messages.add(new ValidMessage("请输入资产代码。"));
        }
        if(!StringUtil.isNotEmpty(allDto.getPs())){
            messages.add(new ValidMessage("请输入交易方向。"));
        }
        if(StringUtil.isNotEmpty(allDto.getPs())
                && !StringUtil.equals(allDto.getPs(),BussinessDictCodeList.交易方向_行权回售.getValue())
                && !StringUtil.isNotEmpty(allDto.getSettleSpeed())
        ){
            messages.add(new ValidMessage("请输入清算速度。"));
        }
        if(!StringUtil.isNotEmpty(allDto.getPriceModel())){
            messages.add(new ValidMessage("请输入价格模式。"));
        }
        // 价格模式check
        if (StringUtil.isNotEmpty(allDto.getPriceModel()) &&
                (StringUtil.equals(allDto.getPriceModel(), DictCodeListExt2.价格模式_指定价格.getValue())
                        || StringUtil.equals(allDto.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue()))) {
            // 价格模式选择限价指令，净价必填
            if (allDto.getPrice() == null ) {
                messages.add(new ValidMessage("请输入净价。"));
            }
        }
        if(StringUtil.isNotEmpty(allDto.getPrice()) && allDto.getPrice().compareTo(BigDecimal.ZERO)<=0){
            messages.add(new ValidMessage("净价不允许小于等于0。"));

        }
        if(StringUtil.isNotEmpty(allDto.getTradeAmt())&& allDto.getTradeAmt().compareTo(BigDecimal.ZERO)<=0){
            messages.add(new ValidMessage("基本信息中“券面总额”不允许小于等于0。"));

        }
        String shortSellingSupported=CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED");
        if(StringUtil.equals(shortSellingSupported,"02")){
            if(StringUtil.isNotEmpty(allDto.getEntrustOwnNum())&& allDto.getEntrustOwnNum().compareTo(BigDecimal.ZERO)<0){
                messages.add(new ValidMessage("基本信息中“本方券券面总额”不允许小于0。"));

            }
            if(StringUtil.isNotEmpty(allDto.getEntrustIntegrateNum())&& allDto.getEntrustIntegrateNum().compareTo(BigDecimal.ZERO)<0){
                if(StringUtil.isNotEmpty(allDto.getPs()) && StringUtil.equals(allDto.getPs(),DictCodeList.交易方向_买入.getValue())){
                    messages.add(new ValidMessage("基本信息中“卖空购回券面总额”不允许小于0。"));

                }else{
                    messages.add(new ValidMessage("基本信息中“融入券券面总额”不允许小于0。"));
                }
            }
        }
        FinProduct fp=cfetsetrIbBondinfoMapper.findAssetInfo(allDto.getFinprodId());

        List<CfetsBussCombDistribtDto> dtoList=allDto.getBussCombDistribtList();
        //券面总额
        BigDecimal entrustNum=BigDecimal.ZERO;
        //本方券券面总额
        BigDecimal entrustOwnNum=BigDecimal.ZERO;
        //卖空购回券面总额
        BigDecimal entrustIntegrateNum=BigDecimal.ZERO;

        for(int i = 0; i< dtoList.size(); i++) {
            CfetsBussCombDistribtDto distrib=dtoList.get(i);
            if(!StringUtil.isNotEmpty(dtoList.get(i).getPortfolioId()))
                messages.add(new ValidMessage("请输入组合代码(第"+(i+1)+"行)。"));
            if(!StringUtil.isNotEmpty(dtoList.get(i).getEntrustNum()))
               messages.add(new ValidMessage("请输入券面总额(第"+(i+1)+"行)。"));
            if(!StringUtil.isNotEmpty(dtoList.get(i).getBusinessModel()))
               messages.add(new ValidMessage("请输入业务模式(第"+(i+1)+"行)。"));
            if(StringUtil.isNotEmpty(distrib.getEntrustNum())&& distrib.getEntrustNum().compareTo(BigDecimal.ZERO)<=0){
                messages.add(new ValidMessage("组合分配处“券面总额”不允许小于等于0。"));

            }
            if(StringUtil.equals(shortSellingSupported,"02")){
                if(StringUtil.isNotEmpty(distrib.getEntrustOwnNum())&& distrib.getEntrustOwnNum().compareTo(BigDecimal.ZERO)<0){
                    messages.add(new ValidMessage("组合分配处“本方券券面总额”不允许小于0。"));

                }
                if(StringUtil.isNotEmpty(distrib.getEntrustIntegrateNum())&& distrib.getEntrustIntegrateNum().compareTo(BigDecimal.ZERO)<0){
                    if(StringUtil.isNotEmpty(allDto.getPs()) && StringUtil.equals(allDto.getPs(),DictCodeList.交易方向_买入.getValue())){
                        messages.add(new ValidMessage("组合分配处“卖空购回券面总额”不允许小于0。"));
                    }else{
                        messages.add(new ValidMessage("组合分配处“融入券券面总额”不允许小于0。"));
                    }
                }
            }
            entrustNum=ArithmeticUtil.doAdd(entrustNum,dtoList.get(i).getEntrustNum());
            entrustOwnNum=ArithmeticUtil.doAdd(entrustOwnNum,dtoList.get(i).getEntrustOwnNum());
            entrustIntegrateNum=ArithmeticUtil.doAdd(entrustIntegrateNum,dtoList.get(i).getEntrustIntegrateNum());
            //交易方向为卖出，回售时，判断是否存在不同的业务模式
            if(StringUtil.isNotEmpty(allDto.getPs()) && StringUtil.isNotEmpty(allDto.getFinprodId())
                    && StringUtil.isNotEmpty(distrib.getPortfolioId())
                    && StringUtil.isNotEmpty(distrib.getBusinessModel())
                    && (StringUtil.equals(allDto.getPs(),DictCodeList.交易方向_卖出.getValue())
                      || StringUtil.equals(allDto.getPs(),DictCodeList.交易方向_行权_回售.getValue()))){
                //查询该资产和该组合存在的业务模式
                Map<String, Object> pageParam=new HashMap<>();
                pageParam.put("finprodId",allDto.getFinprodId());
                pageParam.put("portfolioId",distrib.getPortfolioId());
                pageParam.put("cdate",DateUtil.format(allDto.getEtrDate(),"yyyy-MM-dd"));
                List<CfetsBussCombDistribtDto> busmodelList=cfetsetrIbBondinfoMapper.selectAllBusModelListByInfo(pageParam);
                if(!StringUtil.isEmptyList(busmodelList)){
                    for(CfetsBussCombDistribtDto bus:busmodelList){
                         if(!StringUtil.equals(bus.getBusinessModel(),distrib.getBusinessModel())){
                             messages.add(new ValidMessage("组合【"+bus.getPortfolioName()+"】存在多个业务模式，请先做资产重分类"));
                             break;
                         }
                    }
                }
            }

        }
        if(StringUtil.isNotEmpty(allDto.getTradeAmt()) && allDto.getTradeAmt().compareTo(entrustNum)!=0){
            messages.add(new ValidMessage("组合分配中各组合券面总额之和必须等于基本信息中券面总额。"));

        }
        //根据卖空模式对本方券和融入券/卖空购回券面总额进行赋值
        if(StringUtil.equals(shortSellingSupported,"02")){
            if(StringUtil.isNotEmpty(allDto.getEntrustOwnNum()) && allDto.getEntrustOwnNum().compareTo(entrustOwnNum)!=0){
                messages.add(new ValidMessage("组合分配中各组合本方券券面总额之和必须等于基本信息中本方券券面总额。"));

            }
            if(StringUtil.isNotEmpty(allDto.getEntrustIntegrateNum()) && allDto.getEntrustIntegrateNum().compareTo(entrustIntegrateNum)!=0){
                messages.add(new ValidMessage("组合分配中各组合卖空购回券面总额之和必须等于基本信息中卖空购回券面总额。"));

            }
            if(StringUtil.isNotEmpty(allDto.getEntrustOwnNum()) && StringUtil.isNotEmpty(allDto.getEntrustIntegrateNum())
               &&  StringUtil.isNotEmpty(allDto.getTradeAmt()) && !allDto.getPs().equals("T03")
            ){
                if(allDto.getTradeAmt().compareTo(ArithmeticUtil.doAdd(allDto.getEntrustOwnNum(),allDto.getEntrustIntegrateNum()))!=0){
                    messages.add(new ValidMessage("基本信息中券面总额必须等于基本信息中本方券券面总额和卖空购回券面总额之和。"));
                }
            }
        }
        //指令日期若是节假日，不可以保存、提交
        if(StringUtil.isNotEmpty(fp)){
            String isHoliday = cfetsetrIbBondinfoMapper.getIsHoliday(allDto.getEtrDate(), fp.getCalendarId());
            if(StringUtil.isNotEmpty(isHoliday) && !StringUtil.equals(isHoliday,"0")){
                messages.add(new ValidMessage("指令日期必须为交易日。"));

            }
        }
        boolean isAuthorizationSoftcontrol = Boolean.parseBoolean(CacheUtil.getSysParam("IS_AUTHORIZATION_SOFTCONTROL"));
        // 授权额度检查（当本金金额高于副总裁/总裁助理相应资产的授权额度时，提示消息） 且不等于赎回
        if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH),"01") && StringUtil.isEmptyList(messages)
                && (!StringUtil.equals(allDto.getPs()==null? "":allDto.getPs(), DictCodeList.交易方向_赎回.getValue()))) {
            if (!isAuthorizationSoftcontrol){
                /*if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH_IS_TOTAL), "Y")){*/
                    String mess=getEtrLimitSwitch(allDto);
                    if(StringUtil.isNotEmpty(mess)){
                        messages.add(new ValidMessage(mess));

                    }
              /*  }else{
                    List<CfetsBussCombDistribtDto> etribComDistribtDtoList =allDto.getBussCombDistribtList();
                    for(CfetsBussCombDistribtDto distribt:etribComDistribtDtoList){
                        CfetsEtribSaveRequest itemDto=new CfetsEtribSaveRequest();
                        BeanUtil.copyPropertiesIgnoreNull(itemDto,allDto);
                        itemDto.setTradeAmt(distribt.getEntrustNum());
                        String mess=getEtrLimitSwitch(itemDto);
                        if(StringUtil.isNotEmpty(mess)){
                            //根据组合id查询组合信息
                            PtlPortfolio pp=cfetsetrIbBondinfoMapper.findPtlInfo(distribt.getPortfolioId());
                            if(null!=pp){
                                mess=pp.getPortfolioName().toString()+"组合下"+mess;
                                messages.add(mess);
                            }
                        }
                    }
                }*/
            }
        }
       //SPPI测试
        if(StringUtil.isNotEmpty(allDto.getPs()) && StringUtil.isNotEmpty(allDto.getFinprodId())
                || StringUtil.equals(allDto.getPs(),DictCodeList.交易方向_买入.getValue())){
            //判断是否进行了SPPI测试
            Map<String, Object> paramsppi =new HashMap<>();
            paramsppi.put("finprodId",allDto.getFinprodId());
            SppiImportData sppiDto=cfetsetrIbBondinfoMapper.queryAssetIsSppiTest(paramsppi);
            if(StringUtil.isNotEmpty(sppiDto)){
                if(StringUtil.isNotEmpty(sppiDto.getIsSppiPass()) && StringUtil.equals(sppiDto.getIsSppiPass(),"2")){
                    //判断该资产未来是否有行权计划
                    Map<String,Object> param = new HashMap<>();
                    param.put("finprodId",allDto.getFinprodId());
                    param.put("sysDate",DateUtil.platDateFromCache());
                    if(cfetsIbuQuoteBondMapper.countBondHasEexrPlan(param)>0){
                        for(CfetsBussCombDistribtDto item:dtoList){
                            if(StringUtil.isNotEmpty(item.getBusinessModel()) && StringUtil.equals(item.getBusinessModel(),DictCodeListExt3.业务模式_收取合同约定的现金流.getValue())){
                                SppiImportData sppiImportData = sppiImportDataMapper.selectByPrimaryKey(allDto.getFinprodId());
                                if(null!=sppiImportData&&StringUtil.isNotEmpty(sppiImportData.getBidPriceCon())){
                                    String[] split = sppiImportData.getBidPriceCon().split(",");
                                    if(allDto.getPrice().compareTo(new BigDecimal(split[1]))<=0){
                                        BaseMap baseMap = positionInfoService.queryTrdPosition(Arrays.asList(item.getPortfolioId()), DateUtil.format(allDto.getEtrDate()), allDto.getFinprodId());
                                        //
                                        if(null!=baseMap&&baseMap.containsKey("ZCTRKYCC")&&((BigDecimal)baseMap.get("ZCTRKYCC")).compareTo(BigDecimal.ZERO)>0){
                                            messages.add(new ValidMessage(StringUtil.join("交易净价不满足使权不重大的购入条件:",new BigDecimal(split[1]),"业务模式由收取合同现金流调整为出售,请资产重分类!")));
                                        }else{
                                            messages.add(new ValidMessage(StringUtil.join("交易净价不满足使权不重大的购入条件:",new BigDecimal(split[1]),"业务模式由收取合同现金流调整为出售!")));
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }else{
                messages.add(new ValidMessage("当前资产未进行SPPI测试，无法进行买入指令提交/保存。"));
            }
        }

        if(messages.size()>0) {
            AjaxResult errorRs = new AjaxResult();
            errorRs.setErrorList(messages);
            return errorRs;
        }
        return null;
    }
    private String getEtrLimitSwitch(CfetsEtribSaveRequest allDto){
        String message=null;

        TrdProductDeal trdProductDeal = null;
        String repType = null;

        // 副总裁/总裁助理授权额度检查相关处理
        String counterId = null;
        BigDecimal prinAmt = null;
        String 平安资产类型 = null;
        String windtypeone = null;
        String tradeMarket = null;
        String ps = null;
        // 资产类型改为授权类型 add by renping 2019-08-06  start
        String finprodId = null;
        // 资产类型改为授权类型 add by renping 2019-08-06  end
        // 资产类型改为授权类型 add by zhangnian 2020-03-04  start
        String 无标的投管资产类型映射行内资产分类 = null;
        // 资产类型改为授权类型 add by zhangnian 2019-03-04  end
        // 无标的投管资产类型映射行内资产分类 inputValue1 inputValue2 inputValue3 add by zhangnian 2020-03-04  start
        String inputValue1 = null;String inputValue2 = null;String inputValue3 = null;
        // 无标的投管资产类型映射行内资产分类 inputValue1 inputValue2 inputValue3 add by zhangnian 2019-03-04  end
        // 债券投资交易
        if (allDto instanceof CfetsEtribSaveRequest) {
            CfetsEtribSaveRequest inputDto = (CfetsEtribSaveRequest)allDto;
            // 修改债券获取金额by赵东东 2019.9.4 start
            prinAmt = inputDto.getTradeAmt();
            // 修改债券获取金额by赵东东 2019.9.4 end
            平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
            windtypeone = DictCodeList.平安资产类型_债券.getValue();
            tradeMarket = BussinessDictCodeList.交易场所_银行间.getValue();
            trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
            ps = inputDto.getPs();
            finprodId = inputDto.getFinprodId();
        }
        boolean isAuthorizationSoftcontrol = Boolean.parseBoolean(CacheUtil.getSysParam("IS_AUTHORIZATION_SOFTCONTROL"));

        if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH),"01")
                && prinAmt != null
                && (!StringUtil.equals(allDto.getPs()==null? "":allDto.getPs(), DictCodeList.交易方向_赎回.getValue()))) {
            if (!isAuthorizationSoftcontrol){
                CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
                Map<String, Object> params = CommonBusinessService.setParams(counterId, prinAmt, LoginUserUtil.getUserName(),
                        平安资产类型, windtypeone, tradeMarket, ps, finprodId, null);
                if (commonBusinessService != null && commonBusinessService.是否超过副总额额度_委托用(params)) {
                    // 交易本金金额超过副总裁/总裁助理授权额度，无法发起交易!
                    ValidMessage validMessage = new ValidMessage(
                            // 修改超过副总裁的授权额度时报出的message 赵东东 2019.8.27 start
                            getMessage("TRD_E_0016","领导"));
                    // 修改超过副总裁的授权额度时报出的message 赵东东 2019.8.27 end
                    // validMessageList.add(validMessage);
                    message="交易本金金额超过领导授权额度，无法发起交易。";
                }
            }
        }
        return  message;
    }
    /**
     * 保存提交时check检查
     * @param allDto
     * @param br
     * @return
     */
    public String  checkSaveWarn(CfetsEtribSaveRequest allDto, BindingResult br) {
        List<CfetsBussCombDistribtDto> dtoList=allDto.getBussCombDistribtList();
        for(int i = 0; i< dtoList.size(); i++) {
            //查询可用现金和可用持仓

            CfetsBussCombDistribtDto dto = dtoList.get(i);
            List<String> portfolioIdList=new ArrayList<>();
            portfolioIdList.add(dto.getPortfolioId());
            BaseMap bm = BaseMap.newMap()
                    .put("S_001", DictCodeListExt3.资产类型_指令汇总可用持仓.getValue())
                    .put("S_003", DictCodeListExt3.资产类型_融入券卖空待购回持仓.getValue())
                    .put("S_004", DictCodeListExt3.资产类型_汇总可用现金.getValue())
                    .put("A_004", DictCodeListExt3.资产类型_债券本方券可用持仓.getValue())
                    .put("A_005", DictCodeListExt3.资产类型_债券融入券可用持仓.getValue())
                    .put("C_01", DictCodeListExt3.资产类型_现金账户持仓.getValue())
                    .put("portfolioIdList", portfolioIdList)
                    .put("cdate", DateUtil.format(allDto.getEtrDate(),"yyyy-MM-dd"))
                    .put("finprodId", allDto.getFinprodId());
            // 查询头寸信息
            Map map = cfetsetrIbBondinfoMapper.queryPositionInfo(bm);
            BigDecimal proAvailableCash=map==null? BigDecimal.ZERO:new BigDecimal(map.get("T0KYXJ").toString());
            BigDecimal assetAvailablePosition=map==null? BigDecimal.ZERO:new BigDecimal(map.get("ZCTRKYCC").toString());
            if (StringUtil.equals(allDto.getPs(), DictCodeList.交易方向_买入.getValue())
                    && proAvailableCash != null) {
                if (dto.getEntrustNum().compareTo(proAvailableCash) == 1) {
                    String message=this.getMessage("TRD_C_0002") + "(第"+(i+1)+"行)。";
                    return message;
                }
            }
            if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
                    DictCodeListExt1.是或否_是.getValue())
                    && (StringUtil.equals(allDto.getPs(), DictCodeList.交易方向_卖出.getValue())
                    || StringUtil.equals(allDto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())
                    || StringUtil.equals(allDto.getPs(), DictCodeList.交易方向_行权_回售.getValue()))
                    && assetAvailablePosition != null) {
                if (dto.getEntrustNum().compareTo(assetAvailablePosition) > 0) {
                    String message=this.getMessage("ETR_BOND_002")+ "(第"+(i+1)+"行)。";
                    return message;
                }
            }

        }
        return null;
    }
    public String doRiskcalCheckMethod(CfetsEtrIbdoRiskcalRequest dto){
        //参数不能为空
        if(StringUtil.isEmptyList(dto.getBussCombDistribtList())){
            return "组合分配列表不能为空";
        }
        List<String> messages = new ArrayList<>();
        if(StringUtil.isNotEmpty(dto.getBussCombDistribtList())){
            for(CfetsBussCombDistribtBaseDto item:dto.getBussCombDistribtList()){
                if(!StringUtil.isNotEmpty(item.getPortfolioId())){
                    messages.add("请输入组合名称。");
                }
                if(!StringUtil.isNotEmpty(item.getEntrustNum())){
                    messages.add("请输入券面总额。");
                }
            }
        }
        if(!StringUtil.isNotEmpty(dto.getFinprodId())){
            messages.add("请输入资产代码。");
        }
        if(messages.size()>0) {
            return  StringUtil.joinString(messages,  GlobalConstant.VERTICAL_LINE);
        }


        return null;
    }

    public AjaxResult closeDelegateCheckMethod(CfetsEtribBondInfo003Dto dto){
        if(!StringUtil.isNotEmpty(dto)|| StringUtil.isEmptyList(dto.getSelectedItem())){
            AjaxResult res=new AjaxResult();
            ValidMessage  validMessage= new ValidMessage();
            validMessage.setMessage(getMessage("COMMON_E_0002"));
            res.setErrorList(Arrays.asList(validMessage));
            return res;
        }
        List<CfetsEtribBondInfo003Dto> selectedItem = dto.getSelectedItem();
        AjaxResult ajaxResult=new AjaxResult();
        if(StringUtil.isNotEmpty(selectedItem)){
            List<String> entrustIds = new ArrayList<>();
            for(CfetsEtribBondInfo003Dto Item:selectedItem){
                if(StringUtil.equals(Item.getEntrustStatus(),BussinessDictCodeList.委托状态_流程中.getValue())){
                    AjaxResult res=new AjaxResult();
                    ValidMessage  validMessage= new ValidMessage();
                    validMessage.setMessage("流程中的数据不允许关闭");
                    res.setErrorList(Arrays.asList(validMessage));
                    return res;
                }
                if(StringUtil.isNotEmpty(Item.getEntrustId()))
                {
                    WflEtrEntrustKey wflEtrEntrustKey = new WflEtrEntrustKey();
                    wflEtrEntrustKey.setEntrustId(Item.getEntrustId());
                    wflEtrEntrustKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    WflEtrEntrust wflEtrEntrust = wflEtrEntrustMapper.selectByPrimaryKey(wflEtrEntrustKey);
                    if(wflEtrEntrust!=null){
                        AjaxResult res=new AjaxResult();
                        ValidMessage  validMessage= new ValidMessage();
                        validMessage.setMessage("存在流程中数据，无法关闭。");
                        res.setErrorList(Arrays.asList(validMessage));
                        return res;
                    }
                }
                entrustIds.add(Item.getEntrustId());
            }
            Map<String,Object> param= new HashMap<>();
            param.put("entrustIds",entrustIds);
            ajaxResult.setErrorList(etrCommonService.entrustCheckCloseList(param));
            return ajaxResult;
        }
        ValidMessage  validMessage= new ValidMessage();
        validMessage.setMessage("请选择列表中数据");
        ajaxResult.setErrorList(Arrays.asList(validMessage));
        return ajaxResult;
    }
    public AjaxResult recallDelegateCheckMethod(CfetsEtribBondInfo003Dto dto){
        if(!StringUtil.isNotEmpty(dto)|| StringUtil.isEmptyList(dto.getSelectedItem())){
            AjaxResult res=new AjaxResult();
            ValidMessage  validMessage= new ValidMessage();
            validMessage.setMessage(getMessage("COMMON_E_0002"));
            res.setErrorList(Arrays.asList(validMessage));
            return res;
        }
        List<CfetsEtribBondInfo003Dto> selectedItem = dto.getSelectedItem();
        AjaxResult ajaxResult=new AjaxResult();
        if(StringUtil.isNotEmpty(selectedItem)){
            for(CfetsEtribBondInfo003Dto Item:selectedItem){
                if(!StringUtil.equals(Item.getEntrustStatus(),BussinessDictCodeList.委托接收状态_待接收.getValue())
                        && !StringUtil.equals(Item.getEntrustStatus(),BussinessDictCodeList.委托状态_待分发.getValue())
                ){
                    AjaxResult res=new AjaxResult();
                    ValidMessage  validMessage= new ValidMessage();
                    validMessage.setMessage("只有指令状态为待分发、待接收的指令允许撤回");
                    res.setErrorList(Arrays.asList(validMessage));
                    return res;
                }
            };
        }
        return new AjaxResult();
    }
    public AjaxResult modifyDelegateCheckMethod(CfetsEtrInvestBondInfoReqDto dto){
        if(!StringUtil.isNotEmpty(dto)|| StringUtil.isEmptyList(dto.getSelectedItem())){
            AjaxResult res=new AjaxResult();
            ValidMessage  validMessage= new ValidMessage();
            validMessage.setMessage(getMessage("COMMON_E_0002"));
            res.setErrorList(Arrays.asList(validMessage));
            return res;
        }
        List<CfetsEtrInvestBondInfoReqDto> selectedItem = dto.getSelectedItem();
        AjaxResult ajaxResult=new AjaxResult();
        if(StringUtil.isNotEmpty(selectedItem)){
            if(selectedItem.size()>1){
                AjaxResult res=new AjaxResult();
                ValidMessage  validMessage= new ValidMessage();
                validMessage.setMessage("委托指令修改仅允许一条数据");
                res.setErrorList(Arrays.asList(validMessage));
                return res;
            }
            for(CfetsEtrInvestBondInfoReqDto Item:selectedItem){
                if(!StringUtil.equals(Item.getEntrustStatus(),BussinessDictCodeList.委托接收状态_已撤回.getValue())
                        && !StringUtil.equals(Item.getEntrustStatus(),BussinessDictCodeList.委托状态_已退回.getValue())
                ){
                    AjaxResult res=new AjaxResult();
                    ValidMessage  validMessage= new ValidMessage();
                    validMessage.setMessage("只有指令状态为已撤回、已退回的指令允许修改");
                    res.setErrorList(Arrays.asList(validMessage));
                    return res;
                }
            };
        }
        return new AjaxResult();
    }
    public boolean recallDelegateMethod(CfetsEtribBondInfo003Dto dto)
            throws InvocationTargetException, IllegalAccessException {
        BaseEtrInvestInfoDto entity=recallDelegatePackageEquity(dto);
        // 将传来dto中的委托指令的委托状态改为"已撤回"
        entity.getEtrEntrust().setEntrustStatus(DictCodeList.委托状态_已撤回.getValue());
        // 调用投资委托组件的更新委托状态接口，将委托状态设置为"已撤回"
        boolean flag = etrInvestInfoServiceImpl.updateDelegateStatus(entity.getEtrEntrust().getEntrustId(),
                entity.getEtrEntrust().getEntrustStatus());
        if(!StringUtil.equals(dto.getAssetType(),DictCodeListExt2.平安资产类型_一级市场债.getValue())){
            if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                //插入旧头寸
                if (baseEtrCashPositionCommService != null) {
                    baseEtrCashPositionCommService.entrustCloseExecute(entity.getEtrEntrust().getEntrustId(),DictCodeList.是否_是.getValue());
                }
            }
        }

        //更新操作记录日志
        CfetsOprHisInfoRequest oprHisReq=new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(dto.getEntrustId());
        oprHisReq.setOperType(DictCodeListExt3.操作类型_撤回.getValue());
        oprHisReq.setBusiDes(dto.getRemark());
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
        if (flag) {
            return true;
        }
        return false;
    }
    public BaseEtrInvestInfoDto recallDelegatePackageEquity(CfetsEtribBondInfo003Dto dto)
            throws IllegalAccessException, InvocationTargetException {
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        BeanUtil.copyProperties(etrEntrust, dto);
        BeanUtil.copyProperties(etrEntrustAdd, dto);
        List<EtrDistribute> etrDistributeList = new ArrayList<>();
        EtrDistribute etrDistribute = new EtrDistribute();
        BeanUtil.copyProperties(etrDistribute, dto);
        etrDistributeList.add(etrDistribute);
        entity.setEtrEntrust(etrEntrust);
        entity.setEtrEntrustAdd(etrEntrustAdd);
        entity.setEtrDistributeList(etrDistributeList);
        return entity;
    }
    public boolean closeDelegateMethod(String entrustId){
        EtrEntrust ee = etrEntrustMapper.selectByPrimaryKey(entrustId);
        if (ee != null) {
            // 调用投资委托组件的更新委托状态接口，将委托状态设置为“已关闭”
            String tempEntrustStatu = ee.getEntrustStatus();// 获取该委托原始状态，防止后续更改成关闭
            boolean flag = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId, DictCodeList.委托状态_已关闭.getValue());
            if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                //插入旧头寸
                if (baseEtrCashPositionCommService != null && DictCodeList.委托状态_已接收.getValue().equals(tempEntrustStatu)) {
                    baseEtrCashPositionCommService.entrustCloseExecute(entrustId,DictCodeList.是否_是.getValue());
                }
            }
            // 判断关闭委托是否完成
            if (flag) {
                return true;
            }
        }
        return false;
    }
    public void closeDelegatePositionMethod(List<CfetsEtribBondInfo003Dto> dtolist,String assetType){
        CfetsEtrIbClosePositionRequest req=new CfetsEtrIbClosePositionRequest();
        List<CfetsEtrIbClosePosEtrRequest> etrList=new ArrayList<>();
        for(CfetsEtribBondInfo003Dto t: dtolist){
            CfetsEtrIbClosePosEtrRequest closeItem=new CfetsEtrIbClosePosEtrRequest();
            String busiTypePosition=DictCodeListExt3.头寸业务类型_现券交易.getValue();
            if(StringUtil.equals(assetType,DictCodeList.平安资产类型_质押式回购.getValue())){
                busiTypePosition =DictCodeListExt3.头寸业务类型_质押式回购首期.getValue();
            }else{
                if(StringUtil.isNotEmpty(t.getPs())
                        && StringUtil.equals(t.getPs(),BussinessDictCodeList.交易方向_行权回售.getValue())){
                    busiTypePosition=DictCodeListExt3.头寸业务类型_债券行权交易.getValue(); // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易
                }
            }
            closeItem.setBusiType(busiTypePosition);
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
    public void closeDelegatOprHisMethod(CfetsEtribBondInfo003Dto dto){
        List<CfetsEtribBondInfo003Dto> dtolist = dto.getSelectedItem();
        if(dtolist != null && dtolist.size()>0) {
            for (CfetsEtribBondInfo003Dto entity : dtolist) {
                //更新操作记录日志
                CfetsOprHisInfoRequest oprHisReq=new CfetsOprHisInfoRequest();
                oprHisReq.setEntrustId(entity.getEntrustId());
                oprHisReq.setOperType(DictCodeListExt3.操作类型_关闭.getValue());
                oprHisReq.setBusiDes(dto.getRemark());
                joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
            }
        }

    }

    public void forcedFinishOprHisMethod(CfetsEtribBondInfo003Dto dto){
            //更新操作记录日志
            CfetsOprHisInfoRequest oprHisReq=new CfetsOprHisInfoRequest();
            oprHisReq.setEntrustId(dto.getEntrustId());
            oprHisReq.setOperType(DictCodeListExt3.操作类型_强制完成.getValue());
            oprHisReq.setBusiDes(dto.getRemark());
            joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

    public void recallDelegateOprHisMethod(EtrInvestBondInfoDto dto){
        List<EtrInvestBondInfoDto> dtolist = dto.getSelectedItem();
        if(dtolist != null && dtolist.size()>0) {
            for (EtrInvestBondInfoDto entity : dtolist) {
                //更新操作记录日志
                CfetsOprHisInfoRequest oprHisReq=new CfetsOprHisInfoRequest();
                oprHisReq.setEntrustId(entity.getEntrustId());
                oprHisReq.setOperType(DictCodeListExt3.操作类型_撤回.getValue());
                oprHisReq.setBusiDes(dto.getRemark());
                joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
            }
        }

    }
    /**
     *风险试算时，后台数据转换
     * @return
     */
    public  Map<String, Object> doRiskcalPackageEquity(CfetsEtrIbdoRiskcalRequest dto) {
        Map<String, Object> dataMap=new HashMap<>();
        //风险试算封装dto
        CfetsEtrIbdoRiskcalDto d=new CfetsEtrIbdoRiskcalDto();
        BeanUtil.copyPropertiesIgnoreNull(d,dto);
        List<Map> bussTrdTransfenpeiDto=new ArrayList<>();
        for(CfetsBussCombDistribtBaseDto pt:dto.getBussCombDistribtList()){
            pt.setInvAim(pt.getBusinessModel());
            bussTrdTransfenpeiDto.add(BeanUtil.object2Map(pt));
        }
        d.setBussTrdTransfenpeiDto(bussTrdTransfenpeiDto);
        d.setBusiType("02");
        d.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
        d.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        d.setVdate(dto.getEtrDate());
        d.setMdate(dto.getEtrDate());;
        d.setCcy("CNY");
        d.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());

        dataMap=BeanUtil.object2Map(d);
        return dataMap;

    }
    public  String getKriStatusCnByKriStatus(String kriStatus){
        if(StringUtil.isNotEmpty(kriStatus)){
            if(StringUtil.equals(kriStatus,"0")){
                return "正常";
            }else if(StringUtil.equals(kriStatus,"1")){
                return "预警";
            }else  if(StringUtil.equals(kriStatus,"2")){
                return "审批";
            }else  if(StringUtil.equals(kriStatus,"3")){
                return "违规";
            }else  if(StringUtil.equals(kriStatus,"4")){
                return "异常";
            }
        }
        return null;
    }
    /**
     * 查询该资产对应的组合分配列表
     * @return
     */
    public List<CfetsBussCombDistribtBaseDto> queryCfetsBussCombDisList(String secId) {
        if(!StringUtil.isNotEmpty(secId)){
           return null;
        }
        Map<String, Object> param =new HashMap<>();
        param.put("platDate",DateUtil.platDateFromCache());
        param.put("S_001", DictCodeListExt3.资产类型_指令汇总可用持仓.getValue());
        param.put("finprodId",secId);
        String userId = SecurityUtil.getLoginUser().getUserName();
        if (StringUtils.isNotEmpty(userId)) {
            param.put("userId", SecurityUtil.getLoginUser().getUserName());
            return cfetsetrIbBondinfoMapper.queryCfetsBussCombDisList(param);
        }else{
            return null;
        }

    }
    /**
     * 查询资产是否行权债
     * @return
     */
    public Boolean queryAssetIsRight(String secId) {
        if(!StringUtil.isNotEmpty(secId)){
            return false;
        }
        Map<String, Object> param =new HashMap<>();
        param.put("platDate",DateUtil.platDateFromCache());
        param.put("finprodId",secId);
        return cfetsetrIbBondinfoMapper.queryAssetIsRight(param);
    }

    public Message<AjaxResult> recallCheckDelegate( CfetsEtribBondInfo003Dto dto) throws Exception {
        //对选择的数据进行check
        AjaxResult ajaxResult = recallDelegateCheckMethod(dto);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

    @Transactional
    public Message<AjaxResult> recallDelegate( CfetsEtribBondInfo003Dto dto) throws Exception {
        //对选择的数据进行check
        AjaxResult ajaxResult =recallDelegateCheckMethod(dto);
        if (!StringUtil.isEmptyList(ajaxResult.getErrorList())) {
            return new Message<AjaxResult>().success().setData(ajaxResult);
        }
        List<CfetsEtribBondInfo003Dto> dtolist = dto.getSelectedItem();
        if(dtolist != null && dtolist.size()>0){
            for(CfetsEtribBondInfo003Dto entity : dtolist){
                recallDelegateMethod(entity);
            }
            //更新操作日志
            // cfetsEtrIbBondinfoService.recallDelegateOprHisMethod(dto);
            // add by zhangrui1 批量撤销预占 start
            String assetType = DictCodeList.平安资产类型_债券.getValue();
            if(StringUtil.equals(dto.getAssetType(),DictCodeList.平安资产类型_质押式回购.getValue())){
                assetType = DictCodeList.平安资产类型_质押式回购.getValue();
            }
            if(StringUtil.equals(dto.getAssetType(),DictCodeListExt2.平安资产类型_一级市场债.getValue())){
                assetType = DictCodeListExt2.平安资产类型_一级市场债.getValue();
            }
            EntrustRdsCommonAop.doListPreOccupy(dtolist, assetType,
                    BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(), false, null);
            // add by zhangrui1 批量撤销预占 end
            return new Message<AjaxResult>().success().setData(new AjaxResult(this.getMessage("ETR_X_001")));

        }
        return new Message<AjaxResult>().success().setData(new AjaxResult(this.getMessage("ETR_X_002")));
    }

    public Message<AjaxResult> closeCheckDelegate( CfetsEtribBondInfo003Dto dto){
        //对选择的数据进行check
        AjaxResult ajaxResult = closeDelegateCheckMethod(dto);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

    @Transactional
    public Message<AjaxResult> closeDelegate( CfetsEtribBondInfo003Dto dto) throws Exception{

        List<CfetsEtribBondInfo003Dto> dtolist = dto.getSelectedItem();

        if(dtolist != null && dtolist.size()>0){
            for(CfetsEtribBondInfo003Dto entity : dtolist){
                closeDelegateMethod(entity.getEntrustId());
            }
            // add by zhangrui1 批量委托关闭 start
            // 若预占下没有实占则预占信息清除，否则预占信息保留，撤销交易时可恢复原状态
            List<String> entrustIdList = dtolist.stream().map(
                    CfetsEtribBondInfo003Dto::getEntrustId).collect(Collectors.toList());
            // ZGZGST-2330:委托关闭判断是否有完成额度，分情况处理 fuyongsheng 20210919 edit start
            if(CollectionUtils.isNotEmpty(entrustIdList)){
                Map<String, BigDecimal> etrFinishMap = EntrustCompleteUtil.getEtrFinishAmtList(entrustIdList);
                //委托没有做过交易的，和撤销相同逻辑，删除预占
                List<CfetsEtribBondInfo003Dto> recallEntrustIdList = new ArrayList<>();
                //委托已有完成额度，做过交易，走关闭完成的逻辑，更新预占
                List<String> closeEntrustIdList = new ArrayList<>();
                for(CfetsEtribBondInfo003Dto etrDto : dtolist) {
                    if(StringUtil.isNotEmpty(etrFinishMap) && StringUtil.isNotEmpty(etrFinishMap.get(etrDto.getEntrustId()))
                            && BigDecimal.ZERO.compareTo(etrFinishMap.get(etrDto.getEntrustId()))<0) {
                        closeEntrustIdList.add(etrDto.getEntrustId());
                    }
                    else if(entrustIdList.contains(etrDto.getEntrustId())){
                        recallEntrustIdList.add(etrDto);
                    }
                }

                //没有做过交易，直接关闭的
                if(!StringUtil.isEmptyList(recallEntrustIdList)) {
                    if(StringUtil.equals(dto.getAssetType(),DictCodeList.平安资产类型_质押式回购.getValue())){
                        EntrustRdsCommonAop.doListPreOccupy(
                                recallEntrustIdList, DictCodeList.平安资产类型_质押式回购.getValue(),
                                BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(),
                                false, null);
                    } else if (StringUtil.equals(dtolist.get(0).getAssetType(), DictCodeList.平安资产类型_一级市场债.getValue())) {
                        EntrustRdsCommonAop.doListPreOccupy(
                                recallEntrustIdList, DictCodeList.平安资产类型_一级市场债.getValue(),
                                BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(),
                                false, null);
                    }else {
                        // add by yushilong 批量撤销预占 start
                        EntrustRdsCommonAop.doListPreOccupy(recallEntrustIdList, DictCodeList.平安资产类型_债券.getValue(),
                                BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(), false, null);
                        // add by yushilong 批量撤销预占 end
                    }
                }

                //已经有委托完成度的
                if(!StringUtil.isEmptyList(closeEntrustIdList)) {
                    EntrustCompleteUtil.doComplete(closeEntrustIdList);
                }
            }
            // ZGZGST-2330:委托关闭判断是否有完成额度，分情况处理 fuyongsheng 20210919 edit end
            // add by zhangrui1 批量委托关闭 end

            //发送头寸，批量关闭
            closeDelegatePositionMethod(dtolist,dto.getAssetType());
            //更新操作日志
            closeDelegatOprHisMethod(dto);

            return new Message<AjaxResult>().success().setData(new AjaxResult(this.getMessage("ETR_X_003")));
        }
        return new Message<AjaxResult>().success().setData(new AjaxResult(this.getMessage("ETR_X_004")));
    }

    /**
     * 查询资产是否行权债
     * @return
     */
    public Boolean queryAssetIsShReg(String secId) {
        if(!StringUtil.isNotEmpty(secId)){
            return false;
        }
        Map<String, Object> param =new HashMap<>();
        param.put("finprodId",secId);
        param.put("platDate",DateUtil.platDateFromCache());
        return cfetsetrIbBondinfoMapper.queryAssetIsShReg(param);
    }
    /**
     * 查询业务模式
     * @param secId
     * @return
     */
    public String findInvAim(String portfolioId,String secId) {
        Map<String, Object> param =new HashMap<>();
        param.put("finprodId",secId);
        param.put("portfolioId",portfolioId);
        param.put("platDate",DateUtil.platDateFromCache());
        String invAim = cfetsetrIbBondinfoMapper.findInvAimInTrd(param);
        if (StringUtil.isEmpty(invAim)) {
            invAim = cfetsetrIbBondinfoMapper.findInvAimInEtr(param);
        }
        return invAim;
    }
    /**
     * 查询资产是否进行了SPPI测试
     * @return
     */
    public SppiImportData queryAssetIsSppiTest(String secId) {
        if(!StringUtil.isNotEmpty(secId)){
            return null;
        }
        Map<String, Object> param =new HashMap<>();
        param.put("finprodId",secId);
        //param.put("platDate",DateUtil.platDateFromCache());
        return cfetsetrIbBondinfoMapper.queryAssetIsSppiTest(param);
    }

}
