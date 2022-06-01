/*
 * 文件名：EtrBondFirstLBiddingService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.service;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.service.entrust.common.service.WFlowCommonService;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;

import com.joyintech.background.component.common.pack.common.security.User;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepAdvBuyDto;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepExpireDto;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepExpireUnBuyDto;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepFristIssueDto;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepHwayChgSecDto;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepOpenSecDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrBaseDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrParameterDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrSinglePortfolioDto;
import com.joyintech.background.component.order.exchange.common.service.impl.EtrComponentFactory;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dao.EtrExchangeAgreRepTradeMapper;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto.EtrExchangeAgreRepTrade001Dto;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.SelectOptionRequestDto;
import com.joyintech.tams.exchange.entrust.etrexchange.position.service.PositionRequestServiceEtrExchangeAgreRepTrade001DtoRequest;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.position.dto.PostionParameter;
import com.joyintech.tams.invest.common.position.service.CommonPositionService;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;

import static com.joyintech.tams.constant.DictCodeListExt3.*;


/**
 * 场内交易委托 〈功能详细描述〉
 *
 * @author zhangnian
 * @version 1.0 2019年5月6日
 * @see EtrExchangeAgreRepTrade001Service
 * @since 1.0
 */
@Service
public class EtrExchangeAgreRepTrade001Service extends CommonExportService<EtrExchangeAgreRepTrade001Dto> implements BaseProcessService<EtrExchangeAgreRepTrade001Dto> {

    private static final Logger logger = LoggerFactory.getLogger(EtrExchangeAgreRepTrade001Service.class);

    @Autowired
    private EtrExchangeAgreRepTradeMapper etrExchangeAgreRepTradeMapper;
    /**
     * 委托自动分发
     */
    @Autowired(required = false)
    private BusiAutoEtrDispService busiAutoEtrDispService;
    /**
     * 投资委托基础组件
     */
    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     *回购质押品
     */
    @Autowired
    private FinDealPledgeMapper finDealPledgeMapper;

    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    @Autowired
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;
    
    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;

    /**
     * 指令分发接口
     */
    @Autowired
    private EtrCommandDispatcherService etrCommandDispatcherService;

    /**
     * 交易类金融产品表
     */
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    /**
     * 日期计算
     */
    @Autowired
    private DateCalService dateCalService;

    @Autowired
    private CommonPositionService commonPositionService;
    
    @Autowired
	private PositionRequestServiceEtrExchangeAgreRepTrade001DtoRequest positionRequestService;
    
    @Autowired
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;
    
    /**
     * 指令废弃表
     */
    @Autowired
    private EtrEntrustBackupMapper etrEntrustBackupMapper;
    @Autowired
    private EtrEntrustDetailBackUpMapper etrEntrustDetailBackUpMapper;
    @Autowired
    private EtrEntrustDetailMapper etrEntrustDetailMapper;

    public static final String ENTRUST_RECORD_MODIFY = "modify";
    
    public static final String ENTRUST_RECORD_NEW = "new";

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private EtrPledgeMapper etrPledgeMapper;
    /**
     * 对手方交易员联想文本框初始化。
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<SelectOptionDto> selectCounterTrader(Map<String, Object> map) {
        map.put("counterTraderId",map.get("param"));
        if (StringUtil.isNotEmpty(map.get("homeTrader"))){
            map.put("counterId",map.get("homeTrader"));
        }
        Object object = restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/selectCounterTraderId",
                map, Object.class);
        List<SelectOptionDto> resultList = new ArrayList<>();
        if (object instanceof List<?>){
            List<Map<String,Object>> mapList = (List<Map<String,Object>>)object;
            Map<String, String> counterTraderIdMap = new HashMap<>();
            mapList.forEach(item ->{
                if (!counterTraderIdMap.containsKey(item.get("counterTraderId").toString())) {
                    SelectOptionDto dto = new SelectOptionDto();
                    dto.setId(item.get("counterTraderId").toString());
                    dto.setText(item.get("counterTraderId").toString());
                    dto.setObject(item);
                    resultList.add(dto);
                    counterTraderIdMap.put(dto.getId(), dto.getId());
                }
            });
        }
        return resultList;
    }

    /**
     * 对手方交易员联想文本框初始化。
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<SuggestDto> selectCounterTraderWfl(Map<String, Object> map) {
        List<SuggestDto> resultList = new ArrayList<>();

        List<SelectOptionDto> tempList = etrExchangeAgreRepTradeMapper.selectCounterTrader(map);
        tempList.forEach(item ->{
            SuggestDto dto = new SuggestDto();
            dto.setCode(item.getId());
            dto.setObject(item);
            resultList.add(dto);
        });
        return resultList;
    }

    /**
     * 本方交易员联想文本框初始化。
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<SelectOptionDto> selectTrader(Map<String, Object> map) {
        List<SelectOptionDto> suggestDtos = etrExchangeAgreRepTradeMapper.selectTrader(map);
        return suggestDtos;
    }

    /**
     * 本方交易员联想文本框初始化。
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<SuggestDto> selectTraderWfl(Map<String, Object> map) {
        List<SuggestDto> resultList = new ArrayList<>();

        List<SelectOptionDto> tempList = etrExchangeAgreRepTradeMapper.selectTrader(map);
        tempList.forEach(item ->{
            SuggestDto dto = new SuggestDto();
            dto.setCode(item.getId());
            dto.setObject(item);
            resultList.add(dto);
        });
        return resultList;
    }

    /**
     * 查询原成交信息
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public EtrExchangeAgreRepTrade001Dto getEntrustInfoByTradeId(Map<String, Object> map) {
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        map.put("UNIT_01", DictCodeListExt3.交易单位_张.getValue());
        map.put("UNIT_04", DictCodeListExt3.交易单位_手.getValue());
        List<EtrExchangeAgreRepTrade001Dto> result = etrExchangeAgreRepTradeMapper.getEntrustInfoByTradeId(map);
        if(StringUtil.isEmptyList(result)){
            return null;
        }
        return result.get(0);
    }
    /**
     * 获取指令信息
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public EtrExchangeAgreRepTrade001Dto getEntrustInfoByEntrustId(Map<String, Object> map) {
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        map.put("UNIT_01", DictCodeListExt3.交易单位_张.getValue());
        map.put("UNIT_04", DictCodeListExt3.交易单位_手.getValue());
        List<EtrExchangeAgreRepTrade001Dto> result = etrExchangeAgreRepTradeMapper.getEntrustInfoByEntrustId(map);
        if(StringUtil.isEmptyList(result)){
            return null;
        }
        return result.get(0);
    }

    /**
     * 获取到期结算日
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public Map<String,Object> addDate(Map<String, Object> map) {
        String fristSettleDate = (String)map.get("fristSettleDate");

        BigDecimal termDic = new BigDecimal((String) map.get("term"));
        int term = termDic.setScale(0,BigDecimal.ROUND_HALF_UP).intValue();

        String tradeMarket = (String)map.get("tradeMarket");
        Date mDate = DateUtil.addDay(DateUtil.parse(fristSettleDate), term);
        map.put("mDate", DateUtil.format(mDate));
        String dealCalId = "05";
        if(DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(tradeMarket)){
            dealCalId = "04";
        }
        List<String> holidayList =  dateCalService.getDealCalendarHoliday(mDate, DateUtil.addDay(mDate,30), dealCalId);

        while(holidayList.contains(DateUtil.format(mDate))){
            mDate = DateUtil.addDay(mDate,1);
        }
        map.put("expireSettleDate", DateUtil.format(mDate));
        return map;
    }

    public Map<String,Object> getCashAmount(String portfolioId,String acctNo,String sysDate){
        Map<String, Object> result = new HashMap<>();
        PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
        Date vdate = DateUtil.parse(sysDate);
        dto.setCdate(vdate);
        List<String> acctTypeList = new ArrayList<>();
        dto.setAcctType(Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue(),DictCodeListExt1.资金账户类型_券商资金账户.getValue()));
        dto.setPortfolioId(portfolioId);
        dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        dto.setAcctId(acctNo);
        //可用现金
        //BigDecimal cashUse = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
        BigDecimal cashUse = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
        BigDecimal cashGet = basePtlCashPositionInfoService.queryUsableCashPositionOther(dto);
        //可取现金
        result.put("cashUse",cashUse);
        result.put("cashGet",cashGet);
        return result;
    }

    /**
     * 构建流程表数据
     * @param
     * @return
     */
    public BaseEtrInvestInfoDto buildWflEntity(EtrExchangeAgreRepTrade001Dto entity) throws InvocationTargetException, IllegalAccessException {
    	BaseEtrInvestInfoDto dto = new BaseEtrInvestInfoDto();
    	EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        List<EtrPledge> list = new ArrayList<>();
        EtrPledge etrPledge = new EtrPledge();
        etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
        //除首期以外的指令，指令表的存储，金融产品代码存储首期对应的交易编号
        // 构造单组合单资产DTO
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(entity);
        //协议回购首期指令
    	AgreRepFristIssueDto agreRepFristIssueDto = new AgreRepFristIssueDto();
    	//协议回购提前购回指令
    	AgreRepAdvBuyDto agreRepAdvBuyDto = new AgreRepAdvBuyDto();
    	//协议回购到期购回指令
    	AgreRepExpireDto agreRepExpireDto = new AgreRepExpireDto();
    	//协议回购到期续作指令
    	AgreRepExpireUnBuyDto agreRepExpireUnBuyDto = new AgreRepExpireUnBuyDto();
    	//协议回购中途换券指令
    	AgreRepHwayChgSecDto agreRepHwayChgSecDto = new AgreRepHwayChgSecDto();
    	//协议回购解除质押指令
    	AgreRepOpenSecDto agreRepOpenSecDto = new AgreRepOpenSecDto();

        if (DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            
            BeanUtil.copyProperties(agreRepFristIssueDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepFristIssueDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
                agreRepFristIssueDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepFristIssueDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepFristIssueDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepFristIssueDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepFristIssueDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepFristIssueDto.setPledgeFaceValue(entity.getPledgeFaceValue());

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepFristIssueDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepFristIssueDto.setPledgeNumber(entity.getRepSecNum());
            }

            // 质押率
            agreRepFristIssueDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepFristIssueDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
                agreRepFristIssueDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepFristIssueDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepFristIssueDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepFristIssueDto.setTrader(entity.getTrader());
            // 约定号
            agreRepFristIssueDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepFristIssueDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepFristIssueDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepFristIssueDto.setBusiPs(entity.getRepPs());
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepFristIssueDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepFristIssueDto.setAssetType10("H04");
            }
            
            // 质押券代码
            agreRepFristIssueDto.setRepSecId(entity.getRepSecId());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepFristIssueDto);
            etrEntrust.setPs(entity.getBusiPs());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepFristIssueDto);
            if(StringUtil.isNotEmpty(agreRepFristIssueDto.getFinprodId())) {
            	BeanUtil.copyPropertiesIgnoreNull(etrPledge, agreRepFristIssueDto);
            	list.add(etrPledge);
            }
        } else if (DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepAdvBuyDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepAdvBuyDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepAdvBuyDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepAdvBuyDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepAdvBuyDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepAdvBuyDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepAdvBuyDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepAdvBuyDto.setPledgeFaceValue(entity.getPledgeFaceValue());

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepAdvBuyDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepAdvBuyDto.setPledgeNumber(entity.getRepSecNum());
            }
            // 质押率
            agreRepAdvBuyDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepAdvBuyDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepAdvBuyDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepAdvBuyDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepAdvBuyDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepAdvBuyDto.setTrader(entity.getTrader());
            // 约定号
            agreRepAdvBuyDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepAdvBuyDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepAdvBuyDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepAdvBuyDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepAdvBuyDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
                agreRepAdvBuyDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
                agreRepAdvBuyDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepAdvBuyDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepAdvBuyDto.setAssetType10("H04");
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepAdvBuyDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepAdvBuyDto);
            if(StringUtil.isNotEmpty(agreRepAdvBuyDto.getFinprodId())) {
            	BeanUtil.copyPropertiesIgnoreNull(etrPledge, agreRepAdvBuyDto);
            	list.add(etrPledge);
            }
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepExpireDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepExpireDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepExpireDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepExpireDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepExpireDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepExpireDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepExpireDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepExpireDto.setPledgeFaceValue(entity.getPledgeFaceValue());

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepExpireDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepExpireDto.setPledgeNumber(entity.getRepSecNum());
            }
            // 质押率
            agreRepExpireDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepExpireDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepExpireDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepExpireDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepExpireDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepExpireDto.setTrader(entity.getTrader());
            // 约定号
            agreRepExpireDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepExpireDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepExpireDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepExpireDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepExpireDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepExpireDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepExpireDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireDto.setAssetType10("H04");
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepExpireDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepExpireDto);
            if(StringUtil.isNotEmpty(agreRepExpireDto.getFinprodId())) {
            	BeanUtil.copyPropertiesIgnoreNull(etrPledge, agreRepExpireDto);
            	list.add(etrPledge);
            }
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepExpireUnBuyDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepExpireUnBuyDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepExpireUnBuyDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepExpireUnBuyDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepExpireUnBuyDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepExpireUnBuyDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepExpireUnBuyDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepExpireUnBuyDto.setPledgeFaceValue(entity.getPledgeFaceValue());

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepExpireUnBuyDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepExpireUnBuyDto.setPledgeNumber(entity.getRepSecNum());
            }
            // 质押率
            agreRepExpireUnBuyDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepExpireUnBuyDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepExpireUnBuyDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepExpireUnBuyDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepExpireUnBuyDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepExpireUnBuyDto.setTrader(entity.getTrader());
            // 约定号
            agreRepExpireUnBuyDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepExpireUnBuyDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepExpireUnBuyDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepExpireUnBuyDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepExpireUnBuyDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepExpireUnBuyDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepExpireUnBuyDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireUnBuyDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireUnBuyDto.setAssetType10("H04");
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepExpireUnBuyDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepExpireUnBuyDto);
            if(StringUtil.isNotEmpty(agreRepExpireUnBuyDto.getFinprodId())) {
            	BeanUtil.copyPropertiesIgnoreNull(etrPledge, agreRepExpireUnBuyDto);
            	list.add(etrPledge);
            }
            etrEntrustAdd.setRedoType(entity.getRedoType());
        } else if (DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepHwayChgSecDto, etrSinglePortfolioDto);  
            
            // 请求号
            agreRepHwayChgSecDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepHwayChgSecDto.setPledgeFaceValue(entity.getNewpledgeFaceValue());
            //质押率
            agreRepHwayChgSecDto.setPledgeRatio(ArithmeticUtil.divide(entity.getNewConRatio(),new BigDecimal(100),10));

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepHwayChgSecDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getNewRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepHwayChgSecDto.setPledgeNumber(entity.getNewRepSecNum());
            }

            // 质押金额
            agreRepHwayChgSecDto.setPledgeAmt(entity.getTradeAmt());
   
            // 交易对手
            agreRepHwayChgSecDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepHwayChgSecDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepHwayChgSecDto.setTrader(entity.getTrader());
            // 约定号
            agreRepHwayChgSecDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepHwayChgSecDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepHwayChgSecDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepHwayChgSecDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepHwayChgSecDto.setRepSecId(entity.getNewRepSecId());
            // 目标类型
            agreRepOpenSecDto.setTargetType(DictCodeListExt3.目标类型_数量.getValue());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepHwayChgSecDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepHwayChgSecDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepHwayChgSecDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepHwayChgSecDto.setAssetType10("H04");
            }

            //无法从页面带回购首期的部分数据，需要重新查一次
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("tradeId",agreRepHwayChgSecDto.getTradeId());
            List<EtrExchangeAgreRepTrade001Dto> repFristList = etrExchangeAgreRepTradeMapper.getEntrustInfoByTradeId(queryMap);
            if(!StringUtil.isEmptyList(repFristList)) {
                repFristList.forEach(item->{
                    // 到期应计利息
                    agreRepHwayChgSecDto.setmIntAmt(item.getIntAmt());

                    //到期结算金额
                    agreRepHwayChgSecDto.setmTradeAmt(item.getExpireSettleAmt());

                    // 首次交收日
                    if(StringUtil.isNotEmpty(item.getFristSettleDate())){
                        agreRepHwayChgSecDto.setVpayDate(DateUtil.parse(item.getFristSettleDate()));
                    }
                    if(StringUtil.isNotEmpty(item.getExpireSettleDate())){
                        // 到期交收日
                        agreRepHwayChgSecDto.setMpayDate(DateUtil.parse(item.getExpireSettleDate()));
                    }

                    //占款天数
                    if(item.getOccupyDays()!=null){
                        agreRepHwayChgSecDto.setCapDays(BigDecimal.valueOf(item.getOccupyDays()));
                    }

                    // 期限天数，到期日-起息日
                    if(item.getTerm()!=null){
                        agreRepHwayChgSecDto.setTermDays(Long.valueOf(item.getTerm()));
                    }
                });
            }

            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepHwayChgSecDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepHwayChgSecDto);
            if(StringUtil.isNotEmpty(agreRepHwayChgSecDto.getFinprodId())) {
            	BeanUtil.copyPropertiesIgnoreNull(etrPledge, agreRepHwayChgSecDto);
                etrPledge.setPledgeAmt(ArithmeticUtil.multiply(entity.getNewRepSecNum(),entity.getNewpledgeFaceValue()));
                etrPledge.setDisplaceDir(DictCodeListExt3.质押券置换_置换方向_换入.getValue());
                list.add(etrPledge);
                //换出券
                if(entity.getOutRepSecNum()!=null){
                    EtrPledge outPledge=new EtrPledge();
                    outPledge.setEntrustId(entity.getEntrustId());
                    outPledge.setFinprodId(entity.getOutRepSecId());
                    // 质押面值
                    outPledge.setPledgeFaceValue(entity.getOutPledgeFaceValue());
                    //质押率
                    outPledge.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),2));
                    // 质押数量
                    outPledge.setPledgeNumber(entity.getOutRepSecNum());
                    // 质押金额
                    outPledge.setPledgeAmt(ArithmeticUtil.multiply(entity.getOutRepSecNum(),entity.getOutPledgeFaceValue()));
                    outPledge.setDisplaceDir(DictCodeListExt3.质押券置换_置换方向_换出.getValue());
                    list.add(outPledge);
                }
            }
        }else if (上农商_指令业务类型_协议回购中途换券指令_换钱.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {

            BeanUtil.copyProperties(agreRepHwayChgSecDto, etrSinglePortfolioDto);

            // 请求号
            agreRepHwayChgSecDto.setRequestId(entity.getReqOnId());

            // 交易对手
            agreRepHwayChgSecDto.setCounterId(entity.getCounterId());
            //备注
            agreRepHwayChgSecDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepHwayChgSecDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepHwayChgSecDto.setRepSecId(entity.getNewRepSecId());
            // 目标类型
            agreRepOpenSecDto.setTargetType(DictCodeListExt3.目标类型_金额.getValue());
            agreRepHwayChgSecDto.setTradeId(entity.getIntiTradeId());

            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
                agreRepHwayChgSecDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
                agreRepHwayChgSecDto.setAssetType10("H04");
            }
            agreRepHwayChgSecDto.setEntrustAmt(entity.getOutAmt());
            agreRepHwayChgSecDto.setEntrustNum(entity.getOutAmt());

            //无法从页面带回购首期的部分数据，需要重新查一次
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("tradeId",agreRepHwayChgSecDto.getTradeId());
            List<EtrExchangeAgreRepTrade001Dto> repFristList = etrExchangeAgreRepTradeMapper.getEntrustInfoByTradeId(queryMap);
            if(!StringUtil.isEmptyList(repFristList)) {
                repFristList.forEach(item->{
                    // 到期应计利息
                    agreRepHwayChgSecDto.setmIntAmt(item.getIntAmt());

                    //到期结算金额
                    agreRepHwayChgSecDto.setmTradeAmt(item.getExpireSettleAmt());

                    // 首次交收日
                    if(StringUtil.isNotEmpty(item.getFristSettleDate())){
                        agreRepHwayChgSecDto.setVpayDate(DateUtil.parse(item.getFristSettleDate()));
                    }
                    if(StringUtil.isNotEmpty(item.getExpireSettleDate())){
                        // 到期交收日
                        agreRepHwayChgSecDto.setMpayDate(DateUtil.parse(item.getExpireSettleDate()));
                    }

                    //占款天数
                    if(item.getOccupyDays()!=null){
                        agreRepHwayChgSecDto.setCapDays(BigDecimal.valueOf(item.getOccupyDays()));
                    }

                    // 期限天数，到期日-起息日
                    if(item.getTerm()!=null){
                        agreRepHwayChgSecDto.setTermDays(Long.valueOf(item.getTerm()));
                    }
                });
            }

            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepHwayChgSecDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepHwayChgSecDto);
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepOpenSecDto, etrSinglePortfolioDto);  
            
            // 请求号
            agreRepOpenSecDto.setRequestId(entity.getReqOnId());

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepOpenSecDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepOpenSecDto.setPledgeNumber(entity.getRepSecNum());
            }

            agreRepOpenSecDto.setEntrustNum(agreRepOpenSecDto.getPledgeNumber());
  
            // 交易对手
            agreRepOpenSecDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepOpenSecDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepOpenSecDto.setTrader(entity.getTrader());
            // 约定号
            agreRepOpenSecDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepOpenSecDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepOpenSecDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepOpenSecDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepOpenSecDto.setRepSecId(entity.getRepSecId());
            // 目标类型
            agreRepOpenSecDto.setTargetType(DictCodeListExt3.目标类型_数量.getValue());

            // 质押金额
            agreRepAdvBuyDto.setPledgeAmt(entity.getTradeAmt());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepOpenSecDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepOpenSecDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepOpenSecDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepOpenSecDto.setAssetType10("H04");
            }

            //无法从页面带回购首期的部分数据，需要重新查一次
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("tradeId",agreRepOpenSecDto.getTradeId());
            List<EtrExchangeAgreRepTrade001Dto> repFristList = etrExchangeAgreRepTradeMapper.getEntrustInfoByTradeId(queryMap);
            if(!StringUtil.isEmptyList(repFristList)) {
                repFristList.forEach(item->{
                    // 到期应计利息
                    agreRepOpenSecDto.setmIntAmt(item.getIntAmt());

                    //到期结算金额
                    agreRepOpenSecDto.setmTradeAmt(item.getExpireSettleAmt());

                    // 首次交收日
                    if(StringUtil.isNotEmpty(item.getFristSettleDate())){
                        agreRepOpenSecDto.setVpayDate(DateUtil.parse(item.getFristSettleDate()));
                    }
                    if(StringUtil.isNotEmpty(item.getExpireSettleDate())){
                        // 到期交收日
                        agreRepOpenSecDto.setMpayDate(DateUtil.parse(item.getExpireSettleDate()));
                    }

                    //占款天数
                    if(item.getOccupyDays()!=null){
                        agreRepOpenSecDto.setCapDays(BigDecimal.valueOf(item.getOccupyDays()));
                    }

                    // 期限天数，到期日-起息日
                    if(item.getTerm()!=null){
                        agreRepOpenSecDto.setTermDays(Long.valueOf(item.getTerm()));
                    }

                    //质押率
                    if(item.getOrigConRatio()!=null){
                        agreRepOpenSecDto.setPledgeRatio(ArithmeticUtil.divide(item.getOrigConRatio(),new BigDecimal(100),10));
                    }

                    //面值 = 首期的面值
                    FinDealPledgeKey key = new FinDealPledgeKey();
                    key.setFinprodId(item.getTradeId());
                    key.setBranch(0L);
                    key.setPledgeFinprodId(agreRepOpenSecDto.getFinprodId());
                    FinDealPledge finDealPledge = finDealPledgeMapper.selectByPrimaryKey(key);
                    if(finDealPledge != null) {
                        agreRepOpenSecDto.setPledgeFaceValue(finDealPledge.getPledgeFaceValue());
                        agreRepOpenSecDto.setPledgeAmt(finDealPledge.getPledgeAmt());
                    }
                });
            }


            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepOpenSecDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepOpenSecDto);
            if(StringUtil.isNotEmpty(agreRepOpenSecDto.getFinprodId())) {
            	BeanUtil.copyPropertiesIgnoreNull(etrPledge, agreRepOpenSecDto);
            	list.add(etrPledge);
            }
        }
        dto.setEtrEntrust(etrEntrust);
        if (etrEntrustAdd.getVpayDate() != null && etrEntrustAdd.getTermDays() != null) {
            etrEntrustAdd.setMdate(DateUtil.addDay(etrEntrustAdd.getVpayDate(), etrEntrustAdd.getTermDays().intValue()));
        }
        dto.setEtrEntrustAdd(etrEntrustAdd);
        dto.setEtrPledgeList(list);
        return dto;
    }
    /**
     * 工作流完成方法
     * @param entity
     * @param operationType
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void doHandleComplete(EtrExchangeAgreRepTrade001Dto entity, String operationType) throws Exception{

        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());

        // add by tanxuan on 20200514 指令分发推送MQ 参数定义 start
        String isPushMsg = DictCodeList.是否_否.getValue();
        // add by tanxuan on 20200514 指令分发推送MQ 参数定义 end

        // 构造单组合单资产DTO
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(entity);

        //协议回购首期指令
    	AgreRepFristIssueDto agreRepFristIssueDto = new AgreRepFristIssueDto();
    	//协议回购提前购回指令
    	AgreRepAdvBuyDto agreRepAdvBuyDto = new AgreRepAdvBuyDto();
    	//协议回购到期购回指令
    	AgreRepExpireDto agreRepExpireDto = new AgreRepExpireDto();
    	//协议回购到期续作指令
    	AgreRepExpireUnBuyDto agreRepExpireUnBuyDto = new AgreRepExpireUnBuyDto();
    	//协议回购中途换券指令
    	AgreRepHwayChgSecDto agreRepHwayChgSecDto = new AgreRepHwayChgSecDto();
    	//协议回购解除质押指令
    	AgreRepOpenSecDto agreRepOpenSecDto = new AgreRepOpenSecDto();

        if (DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {

            BeanUtil.copyProperties(agreRepFristIssueDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepFristIssueDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
                agreRepFristIssueDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }

            // 到期应计利息
            agreRepFristIssueDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepFristIssueDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepFristIssueDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }

            // 请求号
            agreRepFristIssueDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepFristIssueDto.setPledgeFaceValue(entity.getPledgeFaceValue());

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepFristIssueDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepFristIssueDto.setPledgeNumber(entity.getRepSecNum());
            }
            // 质押率
            agreRepFristIssueDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepFristIssueDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
                agreRepFristIssueDto.setTermDays(Long.valueOf(entity.getTerm()));
            }

            if (agreRepFristIssueDto.getVpayDate() != null && agreRepFristIssueDto.getTermDays() != null) {
                agreRepFristIssueDto.setAddMdate(DateUtil.addDay(agreRepFristIssueDto.getVpayDate(), agreRepFristIssueDto.getTermDays().intValue()));
            }

            // 交易对手
            agreRepFristIssueDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepFristIssueDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepFristIssueDto.setTrader(entity.getTrader());
            // 约定号
            agreRepFristIssueDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepFristIssueDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepFristIssueDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepFristIssueDto.setBusiPs(entity.getRepPs());
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepFristIssueDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepFristIssueDto.setAssetType10("H04");
            }
            //委托方向
            agreRepFristIssueDto.setPs(entity.getRepPs());
            // 质押券代码
            agreRepFristIssueDto.setRepSecId(entity.getRepSecId());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepFristIssueDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepFristIssueDto);

        } else if (DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {

            BeanUtil.copyProperties(agreRepAdvBuyDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepAdvBuyDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepAdvBuyDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepAdvBuyDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepAdvBuyDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepAdvBuyDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }

            // 请求号
            agreRepAdvBuyDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepAdvBuyDto.setPledgeFaceValue(entity.getPledgeFaceValue());

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepAdvBuyDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepAdvBuyDto.setPledgeNumber(entity.getRepSecNum());
            }
            // 质押率
            agreRepAdvBuyDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepAdvBuyDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepAdvBuyDto.setTermDays(Long.valueOf(entity.getTerm()));
            }

            if (agreRepAdvBuyDto.getVpayDate() != null && agreRepAdvBuyDto.getTermDays() != null) {
                agreRepAdvBuyDto.setAddMdate(DateUtil.addDay(agreRepAdvBuyDto.getVpayDate(), agreRepAdvBuyDto.getTermDays().intValue()));
            }

            // 交易对手
            agreRepAdvBuyDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepAdvBuyDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepAdvBuyDto.setTrader(entity.getTrader());
            // 约定号
            agreRepAdvBuyDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepAdvBuyDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepAdvBuyDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepAdvBuyDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepAdvBuyDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
                agreRepAdvBuyDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
                agreRepAdvBuyDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepAdvBuyDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepAdvBuyDto.setAssetType10("H04");
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepAdvBuyDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepAdvBuyDto);

        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {

            BeanUtil.copyProperties(agreRepExpireDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepExpireDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepExpireDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepExpireDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepExpireDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepExpireDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }

            // 请求号
            agreRepExpireDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepExpireDto.setPledgeFaceValue(entity.getPledgeFaceValue());

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepExpireDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepExpireDto.setPledgeNumber(entity.getRepSecNum());
            }
            // 质押率
            agreRepExpireDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepExpireDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepExpireDto.setTermDays(Long.valueOf(entity.getTerm()));
            }

            if (agreRepExpireDto.getVpayDate() != null && agreRepExpireDto.getTermDays() != null) {
                agreRepExpireDto.setAddMdate(DateUtil.addDay(agreRepExpireDto.getVpayDate(), agreRepExpireDto.getTermDays().intValue()));
            }

            // 交易对手
            agreRepExpireDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepExpireDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepExpireDto.setTrader(entity.getTrader());
            // 约定号
            agreRepExpireDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepExpireDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepExpireDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepExpireDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepExpireDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepExpireDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepExpireDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireDto.setAssetType10("H04");
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepExpireDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepExpireDto);
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {

            BeanUtil.copyProperties(agreRepExpireUnBuyDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepExpireUnBuyDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepExpireUnBuyDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepExpireUnBuyDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepExpireUnBuyDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepExpireUnBuyDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }

            // 请求号
            agreRepExpireUnBuyDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepExpireUnBuyDto.setPledgeFaceValue(entity.getPledgeFaceValue());

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepExpireUnBuyDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepExpireUnBuyDto.setPledgeNumber(entity.getRepSecNum());
            }
            // 质押率
            agreRepExpireUnBuyDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepExpireUnBuyDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepExpireUnBuyDto.setTermDays(Long.valueOf(entity.getTerm()));
            }

            if (agreRepExpireUnBuyDto.getVpayDate() != null && agreRepExpireUnBuyDto.getTermDays() != null) {
                agreRepExpireUnBuyDto.setAddMdate(DateUtil.addDay(agreRepExpireUnBuyDto.getVpayDate(), agreRepExpireUnBuyDto.getTermDays().intValue()));
            }

            // 交易对手
            agreRepExpireUnBuyDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepExpireUnBuyDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepExpireUnBuyDto.setTrader(entity.getTrader());
            // 约定号
            agreRepExpireUnBuyDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepExpireUnBuyDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepExpireUnBuyDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepExpireUnBuyDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepExpireUnBuyDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepExpireUnBuyDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepExpireUnBuyDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireUnBuyDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireUnBuyDto.setAssetType10("H04");
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepExpireUnBuyDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepExpireUnBuyDto);
            etrEntrustAdd.setRedoType(entity.getRedoType());
            agreRepExpireUnBuyDto.setRedoType(entity.getRedoType());
        } else if (DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {

            BeanUtil.copyProperties(agreRepHwayChgSecDto, etrSinglePortfolioDto);

            // 请求号
            agreRepHwayChgSecDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepHwayChgSecDto.setPledgeFaceValue(entity.getNewpledgeFaceValue());
            //质押率
            agreRepHwayChgSecDto.setPledgeRatio(ArithmeticUtil.divide(entity.getNewConRatio(),new BigDecimal(100),10));

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepHwayChgSecDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getNewRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepHwayChgSecDto.setPledgeNumber(entity.getNewRepSecNum());
            }

            // 质押金额
            agreRepHwayChgSecDto.setPledgeAmt(entity.getTradeAmt());

            // 交易对手
            agreRepHwayChgSecDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepHwayChgSecDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepHwayChgSecDto.setTrader(entity.getTrader());
            // 约定号
            agreRepHwayChgSecDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepHwayChgSecDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepHwayChgSecDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepHwayChgSecDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepHwayChgSecDto.setRepSecId(entity.getNewRepSecId());
            // 目标类型
            agreRepOpenSecDto.setTargetType(DictCodeListExt3.目标类型_数量.getValue());

            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepHwayChgSecDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepHwayChgSecDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepHwayChgSecDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepHwayChgSecDto.setAssetType10("H04");
            }

            //无法从页面带回购首期的部分数据，需要重新查一次
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("tradeId",agreRepHwayChgSecDto.getTradeId());
            List<EtrExchangeAgreRepTrade001Dto> repFristList = etrExchangeAgreRepTradeMapper.getEntrustInfoByTradeId(queryMap);
            if(!StringUtil.isEmptyList(repFristList)) {
                repFristList.forEach(item->{
                    // 到期应计利息
                    agreRepHwayChgSecDto.setmIntAmt(item.getIntAmt());

                    //到期结算金额
                    agreRepHwayChgSecDto.setmTradeAmt(item.getExpireSettleAmt());

                    // 首次交收日
                    if(StringUtil.isNotEmpty(item.getFristSettleDate())){
                        agreRepHwayChgSecDto.setVpayDate(DateUtil.parse(item.getFristSettleDate()));
                    }
                    if(StringUtil.isNotEmpty(item.getExpireSettleDate())){
                        // 到期交收日
                        agreRepHwayChgSecDto.setMpayDate(DateUtil.parse(item.getExpireSettleDate()));
                    }

                    //占款天数
                    if(item.getOccupyDays()!=null){
                        agreRepHwayChgSecDto.setCapDays(BigDecimal.valueOf(item.getOccupyDays()));
                    }

                    // 期限天数，到期日-起息日
                    if(item.getTerm()!=null){
                        agreRepHwayChgSecDto.setTermDays(Long.valueOf(item.getTerm()));
                    }

                    if (agreRepHwayChgSecDto.getVpayDate() != null && agreRepHwayChgSecDto.getTermDays() != null) {
                        agreRepHwayChgSecDto.setAddMdate(DateUtil.addDay(agreRepHwayChgSecDto.getVpayDate(), agreRepHwayChgSecDto.getTermDays().intValue()));
                    }
                });
            }

            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepHwayChgSecDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepHwayChgSecDto);
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {

            BeanUtil.copyProperties(agreRepOpenSecDto, etrSinglePortfolioDto);

            // 请求号
            agreRepOpenSecDto.setRequestId(entity.getReqOnId());

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepOpenSecDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepOpenSecDto.setPledgeNumber(entity.getRepSecNum());
            }

            agreRepOpenSecDto.setEntrustNum(agreRepOpenSecDto.getPledgeNumber());

            // 交易对手
            agreRepOpenSecDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepOpenSecDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepOpenSecDto.setTrader(entity.getTrader());
            // 约定号
            agreRepOpenSecDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepOpenSecDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepOpenSecDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepOpenSecDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepOpenSecDto.setRepSecId(entity.getRepSecId());
            // 目标类型
            agreRepOpenSecDto.setTargetType(DictCodeListExt3.目标类型_数量.getValue());

            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepOpenSecDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepOpenSecDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepOpenSecDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepOpenSecDto.setAssetType10("H04");
            }

            //无法从页面带回购首期的部分数据，需要重新查一次
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("tradeId",agreRepOpenSecDto.getTradeId());
            List<EtrExchangeAgreRepTrade001Dto> repFristList = etrExchangeAgreRepTradeMapper.getEntrustInfoByTradeId(queryMap);
            if(!StringUtil.isEmptyList(repFristList)) {
                repFristList.forEach(item->{
                    // 到期应计利息
                    agreRepOpenSecDto.setmIntAmt(item.getIntAmt());

                    //到期结算金额
                    agreRepOpenSecDto.setmTradeAmt(item.getExpireSettleAmt());

                    // 首次交收日
                    if(StringUtil.isNotEmpty(item.getFristSettleDate())){
                        agreRepOpenSecDto.setVpayDate(DateUtil.parse(item.getFristSettleDate()));
                    }
                    if(StringUtil.isNotEmpty(item.getExpireSettleDate())){
                        // 到期交收日
                        agreRepOpenSecDto.setMpayDate(DateUtil.parse(item.getExpireSettleDate()));
                    }

                    //占款天数
                    if(item.getOccupyDays()!=null){
                        agreRepOpenSecDto.setCapDays(BigDecimal.valueOf(item.getOccupyDays()));
                    }

                    // 期限天数，到期日-起息日
                    if(item.getTerm()!=null){
                        agreRepOpenSecDto.setTermDays(Long.valueOf(item.getTerm()));
                    }

                    if (agreRepOpenSecDto.getVpayDate() != null && agreRepOpenSecDto.getTermDays() != null) {
                        agreRepOpenSecDto.setAddMdate(DateUtil.addDay(agreRepOpenSecDto.getVpayDate(), agreRepOpenSecDto.getTermDays().intValue()));
                    }

                    //质押率
                    if(item.getOrigConRatio()!=null){
                        agreRepOpenSecDto.setPledgeRatio(ArithmeticUtil.divide(item.getOrigConRatio(),new BigDecimal(100),10));
                    }

                    //面值 = 首期的面值
                    FinDealPledgeKey key = new FinDealPledgeKey();
                    key.setFinprodId(item.getTradeId());
                    key.setBranch(0L);
                    key.setPledgeFinprodId(agreRepOpenSecDto.getFinprodId());
                    FinDealPledge finDealPledge = finDealPledgeMapper.selectByPrimaryKey(key);
                    if(finDealPledge != null) {
                        agreRepOpenSecDto.setPledgeFaceValue(finDealPledge.getPledgeFaceValue());
                        agreRepOpenSecDto.setPledgeAmt(finDealPledge.getPledgeAmt());
                    }
                });
            }


            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepOpenSecDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepOpenSecDto);
        }

        if (etrEntrustAdd.getVpayDate() != null && etrEntrustAdd.getTermDays() != null) {
            etrEntrustAdd.setMdate(DateUtil.addDay(etrEntrustAdd.getVpayDate(), etrEntrustAdd.getTermDays().intValue()));
        }

        //待分发逻辑处理
        List<EtrDistribute> etrDistributeList = new ArrayList<>();
        if (busiAutoEtrDispService != null) {
            // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
            // 设置指令日期 add by yufei 20200808 start
            if(etrEntrust.getEntrustDate() == null) {
                etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            }
            // 设置指令日期 add by yufei 20200808 end
            if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                // 自动分发
                etrDistributeList = busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd);
                isPushMsg = DictCodeList.是否_是.getValue();
            }

        }

        if(StringUtil.isEmptyList(etrDistributeList)) {//无分发人员
            etrSinglePortfolioDto.setEntrustStatus(DictCodeListExt3.委托状态_待分发.getValue());// 指令状态
        }

        String optType = ENTRUST_RECORD_NEW;

		if (null != entity.getEntrustId() && null != etrEntrustMapper.selectByPrimaryKey(entity.getEntrustId())) {
			optType = ENTRUST_RECORD_MODIFY;
		}
        if (StringUtil.equals(optType,ENTRUST_RECORD_NEW)
                && null != entity.getEntrustId() &&  null != etrEntrustBackupMapper.selectByPrimaryKey(entity.getEntrustId())) {
            EtrPledgeKey key = new EtrPledgeKey();
            key.setEntrustId(entity.getEntrustId());
            key.setFinprodId(entity.getFinprodId());
            etrPledgeMapper.deleteByPrimaryKey(key);
        }
		EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);
        if (StringUtil.equals(optType, ENTRUST_RECORD_NEW)) {//
        	if (DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                agreRepFristIssueDto.setEntrustStatus(etrSinglePortfolioDto.getEntrustStatus());

        		 EtrComponentFactory.getComponentService(AgreRepFristIssueDto.class).updateSettings(paraDto);
                 EtrComponentFactory.getComponentService(AgreRepFristIssueDto.class).entrustNew(agreRepFristIssueDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                agreRepAdvBuyDto.setEntrustStatus(etrSinglePortfolioDto.getEntrustStatus());

            	EtrComponentFactory.getComponentService(AgreRepAdvBuyDto.class).updateSettings(paraDto);
            	EtrComponentFactory.getComponentService(AgreRepAdvBuyDto.class).entrustNew(agreRepAdvBuyDto);
            }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                agreRepExpireDto.setEntrustStatus(etrSinglePortfolioDto.getEntrustStatus());

            	EtrComponentFactory.getComponentService(AgreRepExpireDto.class).updateSettings(paraDto);
            	EtrComponentFactory.getComponentService(AgreRepExpireDto.class).entrustNew(agreRepExpireDto);
            }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                agreRepExpireUnBuyDto.setEntrustStatus(etrSinglePortfolioDto.getEntrustStatus());

            	EtrComponentFactory.getComponentService(AgreRepExpireUnBuyDto.class).updateSettings(paraDto);
            	EtrComponentFactory.getComponentService(AgreRepExpireUnBuyDto.class).entrustNew(agreRepExpireUnBuyDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                agreRepHwayChgSecDto.setEntrustStatus(etrSinglePortfolioDto.getEntrustStatus());

            	EtrComponentFactory.getComponentService(AgreRepHwayChgSecDto.class).updateSettings(paraDto);
            	EtrComponentFactory.getComponentService(AgreRepHwayChgSecDto.class).entrustNew(agreRepHwayChgSecDto);
            }else if (DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                agreRepOpenSecDto.setEntrustStatus(etrSinglePortfolioDto.getEntrustStatus());

            	EtrComponentFactory.getComponentService(AgreRepOpenSecDto.class).updateSettings(paraDto);
            	EtrComponentFactory.getComponentService(AgreRepOpenSecDto.class).entrustNew(agreRepOpenSecDto);
            }
        } else {
        	if (DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                //退回后改变质押券后需要更新指令质押券信息，这里设置旧的质押券金融产品代码
        	    EtrEntrust oldEtrEntrust = etrEntrustMapper.selectByPrimaryKey(agreRepFristIssueDto.getEntrustId());
                if(StringUtil.isNotEmpty(oldEtrEntrust)) {
                    agreRepFristIssueDto.setOldFinprodId(oldEtrEntrust.getFinprodId());
                }

                agreRepFristIssueDto.setEntrustStatus(etrSinglePortfolioDto.getEntrustStatus());

       		    EtrComponentFactory.getComponentService(AgreRepFristIssueDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(AgreRepFristIssueDto.class).entrustEdit(agreRepFristIssueDto);
           } else if (DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                agreRepAdvBuyDto.setEntrustStatus(etrSinglePortfolioDto.getEntrustStatus());

           	    EtrComponentFactory.getComponentService(AgreRepAdvBuyDto.class).updateSettings(paraDto);
           	    EtrComponentFactory.getComponentService(AgreRepAdvBuyDto.class).entrustEdit(agreRepAdvBuyDto);
           }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                agreRepExpireDto.setEntrustStatus(etrSinglePortfolioDto.getEntrustStatus());

           	    EtrComponentFactory.getComponentService(AgreRepExpireDto.class).updateSettings(paraDto);
           	    EtrComponentFactory.getComponentService(AgreRepExpireDto.class).entrustEdit(agreRepExpireDto);
           }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                agreRepExpireUnBuyDto.setEntrustStatus(etrSinglePortfolioDto.getEntrustStatus());

           	    EtrComponentFactory.getComponentService(AgreRepExpireUnBuyDto.class).updateSettings(paraDto);
           	    EtrComponentFactory.getComponentService(AgreRepExpireUnBuyDto.class).entrustEdit(agreRepExpireUnBuyDto);
           } else if (DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                agreRepHwayChgSecDto.setEntrustStatus(etrSinglePortfolioDto.getEntrustStatus());

                //退回后改变质押券后需要更新指令质押券信息，这里设置旧的质押券金融产品代码
                EtrEntrust oldEtrEntrust = etrEntrustMapper.selectByPrimaryKey(agreRepHwayChgSecDto.getEntrustId());
                if(StringUtil.isNotEmpty(oldEtrEntrust)) {
                    agreRepHwayChgSecDto.setOldFinprodId(oldEtrEntrust.getFinprodId());
                }
                EtrComponentFactory.getComponentService(AgreRepHwayChgSecDto.class).updateSettings(paraDto);
           	    EtrComponentFactory.getComponentService(AgreRepHwayChgSecDto.class).entrustEdit(agreRepHwayChgSecDto);
           }else if (DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                agreRepOpenSecDto.setEntrustStatus(etrSinglePortfolioDto.getEntrustStatus());

           	    EtrComponentFactory.getComponentService(AgreRepOpenSecDto.class).updateSettings(paraDto);
           	    EtrComponentFactory.getComponentService(AgreRepOpenSecDto.class).entrustEdit(agreRepOpenSecDto);
           }
        }
        // 自动分发数据入库 add by yufei 20200808 start
        if(!StringUtil.isEmptyList(etrDistributeList)){
            baseEtrInvestInfoService.distributeDelegate(etrDistributeList);
        }
        // 自动分发数据入库 add by yufei 20200808 end
        // add by tanxuan on 20200514 指令分发推送信息到交易系统
        if (DictCodeList.是否_是.getValue().equals(isPushMsg)) {
            Map<String, Object> param = new HashMap<>();
            param.put("entrustId", entity.getEntrustId());
            etrCommandDispatcherService.pushCommandMsg(param);
        }
//        // 自动分发自动接收 ，杭银投资poc add by yufei 20200905 start
//        if(!StringUtil.isEmptyList(etrDistributeList) && etrDistributeList.size() == 1){
//            baseEtrInvestInfoService.receptionDelegate(etrDistributeList);
//            // 上交所和深交所推送接收信息到交易系统
//            if (StringUtil.equals(entity.getTradeMarket(),
//                DictCodeListExt1.交易场所_上海证券交易所.getValue()) || StringUtil.equals(
//                		entity.getTradeMarket(),
//                DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
//                // TODO 调用交易接口做接收操作
//            }
//        }
//        // 自动分发自动接收 ，杭银投资poc add by yufei 20200905 end
    }
    /**
     * 配置用户
     */
    public void setUser(EtrParameterDto paraDto){
        User user = new User();
        user.setUserName(LoginUserUtil.getUserName());
        user.setDeptCode(LoginUserUtil.getDeptCode());
        paraDto.setUser(user);
    }

    /**
     * 质押券下拉框
     * @param dto
     * @return
     */
    public Page<SelectOptionDto> selectPledgeInfo(SelectOptionRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        map.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        map.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("sysDate", iPlatformDate.getSysDate());
        map.put("search",dto.getSearch());
        return super.queryForPageForSelectReturn(dto.getPageMode().getPageNo(),dto.getPageMode().getPageSize(),()->etrExchangeAgreRepTradeMapper.selectPledgeInfo(map));
    }

    /**
     * 交易编号下拉框下拉框
     * @param dto
     * @return
     */
    public Page<SelectOptionDto> selectTradeId(SelectOptionRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        map.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        map.put("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        map.put("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        map.put("ps_T01", DictCodeList.交易方向_买入.getValue());
        map.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        map.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        map.put("search",dto.getSearch());
        return super.queryForPageForSelectReturn(dto.getPageMode().getPageNo(),dto.getPageMode().getPageSize(),()->etrExchangeAgreRepTradeMapper.selectTradeId(map));
    }

    /**
     * 保存check
     * @param entity
     * @return
     */
    public AjaxResult checkSave(EtrExchangeAgreRepTrade001Dto entity, BindingResult result)
        throws IllegalAccessException, InvocationTargetException {

        // 构造单组合单资产DTO
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(entity);
        etrSinglePortfolioDto.setStockId("11");
        EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);

        if (DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            //协议回购首期指令
        	AgreRepFristIssueDto agreRepFristIssueDto = new AgreRepFristIssueDto();
            BeanUtil.copyProperties(agreRepFristIssueDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepFristIssueDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
                agreRepFristIssueDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepFristIssueDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepFristIssueDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepFristIssueDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepFristIssueDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepFristIssueDto.setPledgeFaceValue(entity.getPledgeFaceValue());

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepFristIssueDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepFristIssueDto.setPledgeNumber(entity.getRepSecNum());
            }
            // 质押率
            if(StringUtil.isNotEmpty(entity.getConRatio())) {
                agreRepFristIssueDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            }
            // 质押金额
            agreRepFristIssueDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
                agreRepFristIssueDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepFristIssueDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepFristIssueDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepFristIssueDto.setTrader(entity.getTrader());
            // 约定号
            agreRepFristIssueDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepFristIssueDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepFristIssueDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepFristIssueDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepFristIssueDto.setRepSecId(entity.getRepSecId());
            //回购利率
            if(StringUtil.isNotEmpty(entity.getYield())) {
                agreRepFristIssueDto.setYield(ArithmeticUtil.divide(entity.getYield(),new BigDecimal(100),10));
            }
            EtrComponentFactory.getComponentService(AgreRepFristIssueDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(AgreRepFristIssueDto.class).entrustCheck(agreRepFristIssueDto, result);
        } else if (DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	//协议回购提前购回指令
        	AgreRepAdvBuyDto agreRepAdvBuyDto = new AgreRepAdvBuyDto();
            BeanUtil.copyProperties(agreRepAdvBuyDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepAdvBuyDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepAdvBuyDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepAdvBuyDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepAdvBuyDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepAdvBuyDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepAdvBuyDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepAdvBuyDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepAdvBuyDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepAdvBuyDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepAdvBuyDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepAdvBuyDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepAdvBuyDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepAdvBuyDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepAdvBuyDto.setTrader(entity.getTrader());
            // 约定号
            agreRepAdvBuyDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepAdvBuyDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepAdvBuyDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepAdvBuyDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepAdvBuyDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
                agreRepAdvBuyDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
                agreRepAdvBuyDto.setTradeId(entity.getTradeId());
            }
            EtrComponentFactory.getComponentService(AgreRepAdvBuyDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(AgreRepAdvBuyDto.class).entrustCheck(agreRepAdvBuyDto, result);
    
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	//协议回购到期购回指令
        	AgreRepExpireDto agreRepExpireDto = new AgreRepExpireDto();
            BeanUtil.copyProperties(agreRepExpireDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepExpireDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepExpireDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepExpireDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepExpireDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepExpireDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepExpireDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepExpireDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepExpireDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepExpireDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepExpireDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepExpireDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepExpireDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepExpireDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepExpireDto.setTrader(entity.getTrader());
            // 约定号
            agreRepExpireDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepExpireDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepExpireDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepExpireDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepExpireDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepExpireDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepExpireDto.setTradeId(entity.getTradeId());
            }
            EtrComponentFactory.getComponentService(AgreRepExpireDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(AgreRepExpireDto.class).entrustCheck(agreRepExpireDto, result);
    
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	//协议回购到期续作指令
        	AgreRepExpireUnBuyDto agreRepExpireUnBuyDto = new AgreRepExpireUnBuyDto();
            BeanUtil.copyProperties(agreRepExpireUnBuyDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepExpireUnBuyDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepExpireUnBuyDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepExpireUnBuyDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepExpireUnBuyDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepExpireUnBuyDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepExpireUnBuyDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepExpireUnBuyDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepExpireUnBuyDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepExpireUnBuyDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepExpireUnBuyDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepExpireUnBuyDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepExpireUnBuyDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepExpireUnBuyDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepExpireUnBuyDto.setTrader(entity.getTrader());
            // 约定号
            agreRepExpireUnBuyDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepExpireUnBuyDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepExpireUnBuyDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepExpireUnBuyDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepExpireUnBuyDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepExpireUnBuyDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepExpireUnBuyDto.setTradeId(entity.getTradeId());
            }
            EtrComponentFactory.getComponentService(AgreRepExpireUnBuyDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(AgreRepExpireUnBuyDto.class).entrustCheck(agreRepExpireUnBuyDto, result);
        } else if (DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	//协议回购中途换券指令
        	AgreRepHwayChgSecDto agreRepHwayChgSecDto = new AgreRepHwayChgSecDto();
            BeanUtil.copyProperties(agreRepHwayChgSecDto, etrSinglePortfolioDto);  
            
            // 请求号
            agreRepHwayChgSecDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepHwayChgSecDto.setPledgeFaceValue(entity.getNewpledgeFaceValue());
            // 质押率
            agreRepHwayChgSecDto.setPledgeRatio(ArithmeticUtil.divide(entity.getNewConRatio(),new BigDecimal(100),10));

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepHwayChgSecDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getNewRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepHwayChgSecDto.setPledgeNumber(entity.getNewRepSecNum());
            }
  
            // 质押金额
            agreRepHwayChgSecDto.setPledgeAmt(entity.getTradeAmt());

            // 交易对手
            agreRepHwayChgSecDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepHwayChgSecDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepHwayChgSecDto.setTrader(entity.getTrader());
            // 约定号
            agreRepHwayChgSecDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepHwayChgSecDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepHwayChgSecDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepHwayChgSecDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepHwayChgSecDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepHwayChgSecDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepHwayChgSecDto.setTradeId(entity.getTradeId());
            }
            EtrComponentFactory.getComponentService(AgreRepHwayChgSecDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(AgreRepHwayChgSecDto.class).entrustCheck(agreRepHwayChgSecDto, result);
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	//协议回购解除质押指令
        	AgreRepOpenSecDto agreRepOpenSecDto = new AgreRepOpenSecDto();
            BeanUtil.copyProperties(agreRepOpenSecDto, etrSinglePortfolioDto);  
            
            // 请求号
            agreRepOpenSecDto.setRequestId(entity.getReqOnId());

            // 质押数量
            if(StringUtil.equals(DictCodeListExt3.交易单位_手,entity.getPledgeUnit())) {
                agreRepOpenSecDto.setPledgeNumber(ArithmeticUtil.multiply(entity.getRepSecNum(),new BigDecimal(10)));
            }
            else {
                agreRepOpenSecDto.setPledgeNumber(entity.getRepSecNum());
            }
  
            // 交易对手
            agreRepOpenSecDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepOpenSecDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepOpenSecDto.setTrader(entity.getTrader());
            // 约定号
            agreRepOpenSecDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepOpenSecDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepOpenSecDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepOpenSecDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepOpenSecDto.setRepSecId(entity.getRepSecId());

            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepOpenSecDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepOpenSecDto.setTradeId(entity.getTradeId());
            }
            EtrComponentFactory.getComponentService(AgreRepOpenSecDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(AgreRepOpenSecDto.class).entrustCheck(agreRepOpenSecDto, result);
        }


        AjaxResult ajax = new AjaxResult(result);
        Field[] fields = ReflectUtil.getDeclaredFields(ajax);
        for(Field field : fields) {
            if(StringUtil.equals(field.getName(), "messageType")) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, ajax, MessageType.错误消息.getCode());
                break;
            }
        }
        return ajax;
    }
    /**
     * 构造单组合单资产DTO
     * @param dto
     * @return
     */
    public EtrSinglePortfolioDto getEtrSinglePortfolio(EtrExchangeAgreRepTrade001Dto dto) {
        //构造投资指令DTO
        EtrBaseDto baseDto = getEtrBaseDto(dto);
        // 构造单组合单资产DTO start
        EtrSinglePortfolioDto etrSinglePortfolioDto = new EtrSinglePortfolioDto();
        BeanUtil.copyPropertiesIgnoreNull(etrSinglePortfolioDto, baseDto);
        etrSinglePortfolioDto.setPortfolioId(dto.getPortfolioId());// 组合代码
        if(DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue().equals(dto.getEntrustBussType())){
        	etrSinglePortfolioDto.setStockId(dto.getNewRepSecId());// 证券代码
            etrSinglePortfolioDto.setFinprodId(dto.getNewfinprodId());// 证券代码对应的金融产品代码
        }else{
        	etrSinglePortfolioDto.setStockId(dto.getRepSecId());// 证券代码
            etrSinglePortfolioDto.setFinprodId(dto.getFinprodId());// 证券代码对应的金融产品代码
        }
        if (!交易方向_首期.getValue().equals(dto.getPs())){
            etrSinglePortfolioDto.setFinprodId(getFinprodIdNoFirst(dto));
        }
        etrSinglePortfolioDto.setSecAcctId(dto.getSecAcctId());// 证券账户
        etrSinglePortfolioDto.setDealCurAcct(dto.getAcctNo());// 资金账户
        etrSinglePortfolioDto.setPs(dto.getPs());// 委托方向
        etrSinglePortfolioDto.setPriceModel(DictCodeListExt2.价格模式_限价指令.getValue());// 价格模式
        etrSinglePortfolioDto.setPrice(ArithmeticUtil.divide(dto.getYield(), new BigDecimal(100),10));// 指令价格
        etrSinglePortfolioDto.setEntrustNum(dto.getTradeAmt());// 指令数量
        etrSinglePortfolioDto.setEntrustAmt(dto.getTradeAmt());// 指令金额
        etrSinglePortfolioDto.setInvAim(null);// 投资目的
        etrSinglePortfolioDto.setTradeMarket(dto.getTradeMarket());// 交易场所
        etrSinglePortfolioDto.setBusiTypePosition(dto.getBusiTypePosition());
        //etrSinglePortfolioDto.setTradePlat(dto.getTradePlat());// 交易平台
        /*
         * if 画面.交易平台 = 数据字典.交易平台.大宗交易平台 and 画面.交易场所 = 数据字典.交易场所.深圳证券交易所
         * 交易平台 = 数据字典.交易平台.综合协议平台
         * else if 画面.交易平台 = 数据字典.交易平台.大宗交易平台 and 画面.交易场所 = 数据字典.交易场所.上海证券交易所
         * 交易平台 = 数据字典.交易平台.大宗交易平台 end
         */
        if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(dto.getTradeMarket())) {
            etrSinglePortfolioDto.setTradePlat(DictCodeList.交易平台_综合协议交易平台.getValue());

        } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(dto.getTradeMarket())) {
            etrSinglePortfolioDto.setTradePlat(DictCodeList.交易平台_固定收益平台.getValue());
        }

        /***********************************************************************/
        
        if(DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getRepPs())){
        	etrSinglePortfolioDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());// 金融产品分类
        }else{
        	etrSinglePortfolioDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());// 金融产品分类
        }
        etrSinglePortfolioDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
        etrSinglePortfolioDto.setAssetType1("H01");
//        etrSinglePortfolioDto.setDealCurAcct(dto.getAcctNo());// 资金账户
//        /***********************************************************************/
//        etrSinglePortfolioDto.setDealMode(dto.getDeclaType());//申报类型-db 交易方式
        // 构造单组合单资产DTO end
        return etrSinglePortfolioDto;
    }
    /**
     * 构造投资指令DTO
     * @param dto
     * @return
     */
    public EtrBaseDto getEtrBaseDto(EtrExchangeAgreRepTrade001Dto dto) {
        // 构造投资指令DTO start
        EtrBaseDto baseDto = new EtrBaseDto();
        baseDto.setMdate(DateUtil.parse(dto.getMdate()));// 指令起始日
        baseDto.setVdate(DateUtil.parse(dto.getVdate()));// 指令结束日
        baseDto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());// 指令状态
        // 指令接收日期 NULL
        // 指令关闭日期 NULL
        baseDto.setInvManagerId(dto.getInvManagerId());// 登录用户.用户ID
        baseDto.setCreateUser(dto.getCreateUser());// 创建用户
        baseDto.setCreateDept(dto.getCreateDept());// 创建部门
        baseDto.setCreateTime(dto.getCreateTime());// 创建时间
        baseDto.setUpdateUser(LoginUserUtil.getUserName());// 更新用户
        baseDto.setUpdateTime(new Date());// 更新时间
        baseDto.setEntrustClass(DictCodeListExt3.投资指令类型_单组合单资产指令.getValue());// 数据字典.指令类型.单组合单资产
        baseDto.setEntrustType(dto.getEntrustBussType());// 指令业务类型
        baseDto.setEntrustId(dto.getEntrustId());// 指令编号
        if (iPlatformDate != null) {
            baseDto.setEntrustDate(iPlatformDate.getSysDate());// 委托日期
        }
        baseDto.setTargetType(DictCodeListExt3.目标类型_金额.getValue());// 目标类型
        // 构造投资指令DTO end
        return baseDto;
    }


    public List<SelectOptionDto> getptlAcctNo(Map<String, Object> map) {
        return etrExchangeAgreRepTradeMapper.getptlAcctNo(map);
    }

    public BigDecimal queryOtherSecPosition(Map<String, Object> param) {
        Date sysDate = DateUtil.platDateFromCache();
        param.put("sysDate", DateUtil.format(sysDate));
        // 查询最大日期
        param.put("cdate",sysDate);
        param.put("hodingType",DictCodeListExt1.持仓类型_委托指令可用.getValue());
        Date maxCdate = etrExchangeAgreRepTradeMapper.selectSecPositionMaxCdate(param);
        param.put("cdate",maxCdate);
        if (maxCdate == null) {
            return BigDecimal.ZERO;
        }
        return etrExchangeAgreRepTradeMapper.selectAvailablePositionAmount(param);
    }


    public void revokeOrCloseEntrust(Object object){
        // 指令持仓流水、现金流水处理
        PostionParameter parameter = new PostionParameter();
        parameter.setPs("ALL");
        parameter.setTradeMarket("ALL");
        parameter.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
        parameter.setOperationType("REVOKE_CLOSE");
        parameter.setTradeType("01");
        parameter.setBusinessType(DictCodeList.回购类型_质押式协议回购.getValue());
        parameter.setCustom(object);
        try {
            commonPositionService.process(parameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void doComplete(EtrExchangeAgreRepTrade001Dto etrExchangeAgreRepTrade001Dto, OperationType operationType) {
        preEntity(etrExchangeAgreRepTrade001Dto);
        try {
            this.doHandleComplete(etrExchangeAgreRepTrade001Dto,etrExchangeAgreRepTrade001Dto.getOperateType());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("MSG:指令审批发生异常，异常信息：" + e.getMessage());
        }
        doDeleteWflEntity(etrExchangeAgreRepTrade001Dto.getWorkflowId());
    }

    @Override
    public void doInsert(EtrExchangeAgreRepTrade001Dto entity, OperationType type) {
        if ("save".equals(entity.getOperateType())) {
            return;
        }
        //增加调用头寸引擎代码start
        String optType = ENTRUST_RECORD_NEW;
        if (null != entity.getEntrustId() && null != etrEntrustMapper.selectByPrimaryKey(entity.getEntrustId())) {
            optType = ENTRUST_RECORD_MODIFY;
        }
        try {
            //退回的移到备份表
            BaseEtrInvestInfoDto dto = new BaseEtrInvestInfoDto();
            EtrEntrust etrEntrust=new EtrEntrust();
            etrEntrust.setEntrustId(entity.getEntrustId());
            dto.setEtrEntrust(etrEntrust);
            doInsertBackupQuery(dto);

            doInsertWflEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("MSG:指令提交发生异常，异常信息：" + e.getMessage());
        }
        //处理履历
        updateCfetsOprHis(entity.getEntrustId(),optType);

        String decisionAct = DictCodeListExt3.系统动作_修改.getValue();
  		String systemAct = DictCodeListExt3.系统动作_修改.getValue();
  		if (StringUtil.equals(optType, ENTRUST_RECORD_NEW)) {
          	systemAct = DictCodeListExt3.系统动作_新建.getValue();
          	decisionAct = DictCodeListExt3.系统动作_新建.getValue();
        }
  		//positionRequestService.send(entity,decisionAct,systemAct);
  		//增加调用头寸引擎代码end

    }

    @Override
    public void doUpdate(EtrExchangeAgreRepTrade001Dto entity) {
        if ("save".equals(entity.getOperateType())) {
            return;
        }

        if (!StringUtil.equals("save", entity.getOperateType()) && !StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            try {
                //退回的移到备份表
                BaseEtrInvestInfoDto dto = new BaseEtrInvestInfoDto();
                EtrEntrust etrEntrust=new EtrEntrust();
                etrEntrust.setEntrustId(entity.getEntrustId());
                dto.setEtrEntrust(etrEntrust);
                doInsertBackupQuery(dto);
                if (StringUtil.isNotBlank(entity.getWorkflowId())) {
                    doDeleteWflEntity(entity.getWorkflowId());
                }
                doInsertWflEntity(entity);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("MSG:指令提交发生异常，异常信息：" + e.getMessage());
            }
        }
        //增加调用头寸引擎代码start
        String decisionAct = 系统动作_修改.getValue();
		String systemAct = 系统动作_修改.getValue();
		EtrEntrust etrEntrust = etrEntrustBackupMapper.selectByPrimaryKey(entity.getEntrustId());
        SysWflBusinessList sysBus=sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
        if(null!=sysBus && StringUtil.equals(sysBus.getProcessState(),DictCodeList.流程状态_已退回.getValue())){
        	if(etrEntrust==null) {
        		systemAct = 系统动作_新建.getValue();
        		decisionAct = 系统动作_修改.getValue();
        	}
        }else {
        	if(etrEntrust==null) {
        		systemAct = 系统动作_新建.getValue();
        		decisionAct = 系统动作_新建.getValue();
        	}
        }
        if(StringUtil.equals("1", RequestUtil.getParam("confirmOk"))){
            entity.setSoftSkipMode("Y");
        }
        try {
            positionRequestService.send(entity,decisionAct,systemAct);
        } catch (PositionException e) {
            Message message = PositionUtil.ajaxErrorReturn(e);
            if (StringUtil.isNotBlank(message.getMessage())) {
                WorkFlowUtil.showMessage(message.getMessage(), MessageType.错误消息);
            }
            else {
                if (!StringUtil.equals("1", RequestUtil.getParam("confirmOk"))) {
                    BaseMap bm = (BaseMap) message.getData();
                    StringBuffer messageStr = (StringBuffer) bm.get("message");
                    WorkFlowUtil.showMessage(messageStr.toString(), MessageType.确认消息);
                }
            }
        }
        //增加调用头寸引擎代码end
    }

    @Override
    public void doDelete(String workflowId) {
        doDeleteWflEntity(workflowId);
        //增加调用头寸引擎代码start
        ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
        if (StringUtil.isNotEmpty(processMark)
                && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
        	EtrExchangeAgreRepTrade001Dto entity = getBussinessEntityByWorkflowId(workflowId);
      		positionRequestService.sendDelete(entity);
        }
  		//增加调用头寸引擎代码end
    }

    @Override
    public void doBack(EtrExchangeAgreRepTrade001Dto entity) {
        doDeleteWflEntity(entity.getWorkflowId());
        //从废弃表还原
        backReRoll(entity.getEntrustId());
        // 释放指令头寸
        EtrExchangeAgreRepTrade001Dto postionFlowDto = new EtrExchangeAgreRepTrade001Dto();
        BeanUtil.copyPropertiesIgnoreNull(postionFlowDto,entity);
        // 质押数量
        if(StringUtil.equals(DictCodeListExt3.交易单位_手,postionFlowDto.getPledgeUnit())) {
            postionFlowDto.setRepSecNum(ArithmeticUtil.multiply(postionFlowDto.getRepSecNum(),new BigDecimal(10)));
        }
        revokeOrCloseEntrust(postionFlowDto);
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        EtrExchangeAgreRepTrade001Dto dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        //回购金额
        BigDecimal amount = new BigDecimal(0);
        //交易方向
        String ps = "";
        //逆回购和正回购按正常交易金额和交易方向授权
//        if(StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_正回购.getValue())
//                || StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_逆回购.getValue())) {
//            ps = dto.getPs();
//            amount = dto.getTradeAmt();
//        }
        if(StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_首期.getValue())) {
            if(StringUtil.equals(dto.getRepPs(),DictCodeList.回购拆借方向_正回购.getValue())) {
                ps = DictCodeListExt3.交易方向_正回购.getValue();
            }
            else {
                ps = DictCodeListExt3.交易方向_逆回购.getValue();
            }
            amount = dto.getTradeAmt();
        }
        //提前购回、到期购回、中途换券、解除质押根据原始回购的交易方向和回购金额进行授权判断
        else if (StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_提前购回.getValue())
                || StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_到期购回.getValue())
                || StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_中途换券.getValue())
                || StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_解除质押.getValue())) {
            //获得初始活够金额和交易方向
            Map<String, Object> map = new HashMap<>();
            // 上交所、深交所取值来源不一致
            map.put("tradeId", (dto.getIntiTradeId() == null || dto.getIntiTradeId().equals("")) ? dto.getTradeId() : dto.getIntiTradeId());
            EtrExchangeAgreRepTrade001Dto  result =  this.getEntrustInfoByTradeId(map);
            if(StringUtil.isNotEmpty(result)) {
                ps = result.getPs();
                amount = result.getTradeAmt();
            }
        }
        //到期续作使用原始的回购金额和续做的回购金额中的较大值进行判断
        else if (StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_到期续做.getValue())){
            //获得初始回购金额和交易方向
            Map<String, Object> map = new HashMap<>();
            map.put("tradeId", (dto.getIntiTradeId() == null || dto.getIntiTradeId().equals("")) ? dto.getTradeId() : dto.getIntiTradeId());
            EtrExchangeAgreRepTrade001Dto  result =  this.getEntrustInfoByTradeId(map);
            if(StringUtil.isNotEmpty(result)) {
                ps = result.getPs();
                amount = result.getTradeAmt();
                if(dto.getTradeAmt().compareTo(amount)>0) {
                    amount = dto.getTradeAmt();
                }
            }else {
                ps = DictCodeListExt3.交易方向_逆回购.getValue();
                amount = dto.getTradeAmt();
            }
        }
        if(taskId != null) {
            try {
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), amount, LoginUserUtil.getUserName(),
                        BussinessDictCodeList.平安资产类型_回购.getValue(), DictCodeList.回购类型_质押式协议回购.getValue(), dto.getTradeMarket(), ps
                        , "", entity.getWorkflowId()
                );
                //params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
                return commonBusinessService.是否需要上级审批_委托用(params);
            }catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        return null;
    }



    /**
     * 代办提交修改操作类型
     * @param dto
     * @return
     */
    public EtrExchangeAgreRepTrade001Dto preEntity(EtrExchangeAgreRepTrade001Dto dto) {
        // 单位面值
        if (dto.getPledgeFaceValue() == null && StringUtil.isNotBlank(dto.getFinprodId())) {
            dto.setPledgeFaceValue(trdCommonBaseService.getFaceValue(dto.getFinprodId(), iPlatformDate.getSysDate()));
        }

        if(!StringUtil.isNotEmpty(dto.getEntrustBussType())) {
            if(DictCodeListExt3.交易方向_提前购回.getValue().equals(dto.getPs())){
                dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue());
                dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue());
            }else if(DictCodeListExt3.交易方向_到期购回.getValue().equals(dto.getPs())){
                dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue());
                dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue());
            }else if(DictCodeListExt3.交易方向_中途换券.getValue().equals(dto.getPs())){
                dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue());
                dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue());
            }else if(DictCodeListExt3.交易方向_解除质押.getValue().equals(dto.getPs())){
                dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue());
                dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue());
            }else if(DictCodeListExt3.交易方向_到期续做.getValue().equals(dto.getPs())){
                dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue());
                dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue());
            }else{
                dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue());
                dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue());
            }

            if(!StringUtil.isNotEmpty(dto.getFromEtrExchange())) {
                dto.setFromEtrExchange("Y");
            }
            if(!StringUtil.isNotEmpty(dto.getBusiPs())) {
                dto.setBusiPs(dto.getRepPs());
            }

            if(!StringUtil.isNotEmpty(dto.getAssetType2())) {
                dto.setAssetType2(DictCodeList.回购类型_质押式协议回购.getValue());
            }

            if(!StringUtil.isNotEmpty(dto.getFinprodType2())) {
                if(StringUtil.equals(dto.getRepPs(),DictCodeList.回购拆借方向_正回购.getValue())) {
                    dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                }
                else {
                    dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                }

            }
        }
        return dto;
    }

    private void doInsertWflEntity(EtrExchangeAgreRepTrade001Dto dto) throws Exception{
        preEntity(dto);
        BaseEtrInvestInfoDto entity = this.buildWflEntity(dto);
        entity.getEtrEntrust().setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
        entity.setWorkFlowId(dto.getWorkflowId());
        // 设置指令创建来源
        entity.getEtrEntrustAdd().setEntrustCreateSource(DictCodeListExt3.指令创建来源_场内实时.getValue());
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doInsertEtrFlow", entity,
                Boolean.class);
        // 新增交易对手交易员
        if (StringUtil.isNotEmpty(dto.getCounterId()) && StringUtil.isNotEmpty(dto.getCounterTrader())){
            Map<String,Object> map = new HashMap<>();
            map.put("counterId",dto.getCounterId());
            map.put("counterTraderId",dto.getCounterTrader());
            restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterTraderId", map,
                    Object.class);
        }
        // 新增本方交易员
        if (StringUtil.isNotEmpty(dto.getTrader())){
            Map<String,Object> map = new HashMap<>();
            map.put("counterId","homeTrader");
            map.put("counterTraderId",dto.getTrader());
            restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterTraderId", map,
                    Object.class);
        }
        // 指令持仓流水、现金流水处理
        PostionParameter parameter = new PostionParameter();
        parameter.setPs(dto.getPs());
        parameter.setTradeMarket(dto.getTradeMarket());
        parameter.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
        parameter.setOperationType("NEW");
        parameter.setTradeType("01");
        if(DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getRepPs())){
            String businessType = DictCodeList.回购类型_质押式协议回购.getValue().concat(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
            parameter.setBusinessType(businessType);
        }else{
            parameter.setBusinessType(DictCodeList.回购类型_质押式协议回购.getValue().concat(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()));
        }
        // 查询首期交易类金融产品数据
        FinTradeProductKey finTradeProductKey = new FinTradeProductKey();
        finTradeProductKey.setFinprodId(dto.getTradeId());
        finTradeProductKey.setBranch(0L);
        FinTradeProduct firstFinTradeProduct = finTradeProductMapper.selectByPrimaryKey(finTradeProductKey);
        if (firstFinTradeProduct != null){
            dto.setFirstFinTradeProduct(firstFinTradeProduct);
        }
        if (StringUtil.equals(dto.getPs(),DictCodeListExt3.交易方向_到期续做.getValue()) && DictCodeList.回购拆借方向_逆回购.getValue().equals(dto.getRepPs())){
            // 三方续做
            if (StringUtil.equals(dto.getRedoType(),"1") || firstFinTradeProduct == null){
                parameter.setPs(dto.getPs()+"_1");
            }else {
                // 非三方续做
                parameter.setPs(dto.getPs()+"_0");
            }
        }
        dto.setTradeDate(DateUtil.parse(dto.getVdate()));

        EtrExchangeAgreRepTrade001Dto postionFlowDto = new EtrExchangeAgreRepTrade001Dto();
        BeanUtil.copyPropertiesIgnoreNull(postionFlowDto,dto);
        // 质押数量
        if(StringUtil.equals(DictCodeListExt3.交易单位_手,postionFlowDto.getPledgeUnit())) {
            postionFlowDto.setRepSecNum(ArithmeticUtil.multiply(postionFlowDto.getRepSecNum(),new BigDecimal(10)));
        }

        parameter.setCustom(postionFlowDto);
        try {
            logger.info("持仓请求报文:" + JsonUtils.object2Json(parameter));
            commonPositionService.process(parameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDeleteWflEntity(String workflowId) {
        Map<String,Object> param = new HashMap<>();
        param.put("workflowId",workflowId);
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doDeleteEtrFlow", param,
                Boolean.class);
    }

    /**
     * 查询交易对手【非内部交易对手】
     * @return
     */
    public List<SelectOptionDto> selectCounterIdByCounterType(Map<String, Object> map) {
        Object object = restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/selectMstCounterParty",
                map, Object.class);
        List<SelectOptionDto> resultList = new ArrayList<>();
        if (object instanceof List<?>){
            List<Map<String,Object>> mapList = (List<Map<String,Object>>)object;
            mapList.forEach(item ->{
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(item.get("counterId").toString());
                selectOptionDto.setText(item.get("counterName").toString());
                selectOptionDto.setObject(item);
                resultList.add(selectOptionDto);
            });
        }
        return resultList;
    }

    /**
     *风险试算时，后台数据转换
     * @return
     */
    public  Map<String, Object> doRiskcalPackageEquity(EtrExchangeAgreRepTrade001Dto dto) {
        Map<String, Object> dataMap = BeanUtil.object2Map(dto);
        dataMap.put("repPs", dto.getRepPs());
        dataMap.put("redoType", dto.getRedoType());
        dataMap.put("assetType2", dto.getAssetType2());
        dataMap.put("busiPs", dto.getBusiPs());
        dataMap.put("counterId", dto.getCounterId());
        dataMap.put("expireSettleAmt", dto.getExpireSettleAmt());
        dataMap.put("expireSettleDate", dto.getExpireSettleDate());
        dataMap.put("finprodId", dto.getFinprodId());
        dataMap.put("fristSettleDate", dto.getFristSettleDate());
        dataMap.put("fromEtrExchange", "Y");
        dataMap.put("intAmt", dto.getIntAmt());
        dataMap.put("vdate", dto.getVdate());
        dataMap.put("mdate", dto.getMdate());
        dataMap.put("portfolioId", dto.getPortfolioId());
        dataMap.put("printAmt", dto.getPrintAmt());
        dataMap.put("ps", dto.getPs());
        dataMap.put("repSecId", dto.getRepSecId());
        dataMap.put("repSecNum", dto.getRepSecNum());
        dataMap.put("secAcctId", dto.getSecAcctId());
        dataMap.put("term", dto.getTerm());
        dataMap.put("tradeAmt", dto.getTradeAmt());
        dataMap.put("tradeMarket", dto.getTradeMarket());
        dataMap.put("yield", dto.getYield());

        return dataMap;
    }

    private void doInsertBackupQuery(BaseEtrInvestInfoDto dto) {
        WFlowCommonService.inserBackuptWithQuery(dto);
    }

    /**
     * 修改指令回退时,从废弃表将数据取出重新插入
     *
     * @param entrustId 指令代码
     */
    public void backReRoll(String entrustId) {
        //从废弃表中获取，转移到指令表
        etrEntrustBackupMapper.insertEtrFromQuery(entrustId);
        etrEntrustBackupMapper.insertEtrAddFromQuery(entrustId);
        List<EtrEntrustDetailBackUp> list=etrEntrustDetailBackUpMapper.selectList(entrustId);
        if(!org.apache.commons.collections.CollectionUtils.isEmpty(list)){
            for(EtrEntrustDetailBackUp etrEntrustDetailBackUp:list){
                EtrEntrustDetail etrEntrustDetail = new EtrEntrustDetail();
                BeanUtil.copyPropertiesIgnoreNull(etrEntrustDetail,etrEntrustDetailBackUp);
                etrEntrustDetailMapper.insert(etrEntrustDetail);
            }
        }
        // 删除废弃表
        etrEntrustBackupMapper.deleteByPrimaryKey(entrustId);
        etrEntrustBackupMapper.deleteAddByPrimaryKey(entrustId);
        etrEntrustBackupMapper.deleteDetailByPrimaryKey(entrustId);
    }

    /**
     * 判断是否已经存在提前购回、到期购回、到期续作、解除质押的指令
     * @param ps 交易方向
     * @param initTradeId 首期成交编号
     * @return 是否已经存在提前购回、到期购回、到期续作、解除质押的指令
     */
    public boolean isAlreadyDoEntrust(String ps, String initTradeId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ps", ps);
        paramMap.put("initTradeId", initTradeId);

        int count = etrExchangeAgreRepTradeMapper.selectDoneEtrEntrustCount(paramMap);
        if (count > 0) {
            return true;
        }

        return false;
    }

    /**
     * 处理履历
     */
    public void updateCfetsOprHis(String entrustId,String optType){
        CfetsOprHisInfoRequest oprHisReq=new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(entrustId);
        if(ENTRUST_RECORD_NEW.equals(optType)){
            oprHisReq.setOperType(DictCodeListExt3.操作类型_新建.getValue());
        }else if(ENTRUST_RECORD_MODIFY.equals(optType)){
            oprHisReq.setOperType(DictCodeListExt3.操作类型_修改.getValue());
        }else if("close".equals(optType)){
            oprHisReq.setOperType(DictCodeListExt3.操作类型_关闭.getValue());
        }else if("revoke".equals(optType)){
            oprHisReq.setOperType(DictCodeListExt3.操作类型_撤回.getValue());
        }else if("export".equals(optType)){
            oprHisReq.setOperType(DictCodeListExt3.操作类型_导出.getValue());
        }
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

    //获取首期交易编号
    private String getFinprodIdNoFirst(EtrExchangeAgreRepTrade001Dto dto){
        String tradeId="";
        TrdProductDeal trdProductDeal=trdCommonBaseService.getFirstRepoTrade(dto.getIntiTradeId(),
                "", dto.getSecAcctId(),
                DateUtil.parse(dto.getVdate()));
        if(trdProductDeal!=null){
            tradeId=trdProductDeal.getTradeId();
        }
        return tradeId;

    }
}
