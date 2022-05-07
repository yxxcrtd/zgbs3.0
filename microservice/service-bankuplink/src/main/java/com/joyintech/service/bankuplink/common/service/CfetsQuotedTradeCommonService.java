package com.joyintech.service.bankuplink.common.service;

import com.joyintech.background.component.uplink.interbank.business.dto.ResultBaseDto;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.service.bankuplink.common.dao.CstpTradeMapper;
import com.joyintech.service.bankuplink.common.dto.*;
import com.joyintech.service.bankuplink.common.util.BusinessTypeUtil;
import com.joyintech.service.bankuplink.common.util.MarketIndicatorUtil;
import com.joyintech.service.bankuplink.common.util.OrgTypeUtil;
import com.joyintech.service.bankuplink.common.util.ProdTypeUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joyintech.framework.util.DateUtil.getSysDate;
import static com.mchange.v2.log.MLog.getLogger;

/**
 * @author zhangnian
 * @ClassName ImixTradeService
 * @description cstp交易入库
 * @date 2020/12/31 0031 15:05
 **/
@Service
public class CfetsQuotedTradeCommonService{

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CfetsQuotedTradeCommonService.class);

    /**
     * 库表生成的cstp交易处理
     */
    @Autowired
    private InterfCstpTradeMapper mapper;

    @Autowired
    private InterfCstpBjfxjgMapper interfCstpBjfxjgMapper;

    /**
     * 接口处理cstp
     */
    @Autowired
    private CstpTradeMapper cstpMapper;

    /**
     * 库表生成的债券交易处理
     */
    @Autowired
    private InterfTradSecdealMapper secMapper;

    /**
     * 库表生成的回购交易处理
     */
    @Autowired
    private InterfOpicsTradRepoMapper repoMapper;

    /**
     * 库表生成的质押式回购关联
     */
    @Autowired
    private InterfTradRepoasstMapper repoAsstMapper;


    /**
     * 投资理财前缀
     */
    private final static String prefix = "CSTP";

    /**
     * 理财子系统前缀
     */
    private final static String subSystemPrefix = "PMSCSTP";

    /**
     * 是否理财子系统
     */
    private final static String isSubSystem = "is_subsystem";

    /**
     * cstp交易入库服务
     */
    @Autowired
    private CstpTradeService cstpTradeSvc;

    /**
     * 文件扩展名xml
     */
    private static final String XML = "xml";

    /**
     * 回购交易下行自动录单
     */
    @Autowired
    private CstpRepoAutoTradeService repoAutoTradeService;

    /**
     * 债券交易下行自动录单
     */
    @Autowired
    private CstpSecAutoTradeService secAutoTradeService;

    // 异步方法检查数据库中外部报价编号的最大等待时间（毫秒）
    @Value("${application.quote.resp.maxWaitTime:3000}")
    private int maxWaitTime;

    /**
     * cstp交易入库
     */
    @Transactional(rollbackFor = { Exception.class })
    @DTXTransaction
    public ResultBaseDto insertCstpTrade(CfetsQuotedTradeCommonDto entity) throws Exception {
        try {
            Thread.sleep(maxWaitTime);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        ResultBaseDto resultBaseDto = new ResultBaseDto(true);
        InterfCstpTrade iCstpTrade = new InterfCstpTrade();
        try{
            handleiCstpTrade(iCstpTrade,entity);
            // 插入cstp交易表
            mapper.insertSelective(iCstpTrade);
            //自从生成交易标识
            String flagAutoTrade = CacheUtil.getSysParam("FLAG_AUTOTRADE");
            flagAutoTrade = StringUtil.isNotEmpty(flagAutoTrade)?flagAutoTrade:"01";
            InterfTradSecdeal interfTradSecdeal = null;InterfOpicsTradRepo interfOpicsTradRepo = null;
            // 从cstp分发到债券和回购两张表
            if(StringUtil.equals(entity.getMarketIndicator(),MarketIndicatorUtil.MARKETINDICATOR_BOND)){
                // 插入或更新债券交易表
                interfTradSecdeal = insertOrUpdateSec(iCstpTrade);
                // 交易所下行数据自动生成交易
                getLogger().info("银行间下行自动匹配(债券)录单开关状态：" + flagAutoTrade);
                if (StringUtil.equals(flagAutoTrade, "01")) {
                    //secAutoTradeService.secAutoTrade(iCstpTrade,interfTradSecdeal,resultBaseDto);
                }else{
                    //resultBaseDto.setErrMsg("银行间下行自动匹配(债券)录单开关关闭!");
                }
            }else if(StringUtil.equals(entity.getMarketIndicator(),MarketIndicatorUtil.MARKETINDICATOR_REPO)){
                // 插入或更新回购交易表
                interfOpicsTradRepo = insertOrUpdateRepo(iCstpTrade,entity.getNoUnderlyings());
                // 交易所下行数据自动生成交易
                getLogger().info("银行间下行自动匹配(回购)录单开关状态：" + flagAutoTrade);
                if (StringUtil.equals(flagAutoTrade, "01")) {
                    //repoAutoTradeService.repoAutoTrade(iCstpTrade,interfOpicsTradRepo,resultBaseDto);
                }else{
                    //resultBaseDto.setErrMsg("银行间下行自动匹配(债券)录单开关关闭!");
                }
            }
           // if (StringUtil.equals(flagAutoTrade, "01")) {
                InterfTradSecdeal finalInterfTradSecdeal = interfTradSecdeal;
                InterfOpicsTradRepo finalInterfOpicsTradRepo = interfOpicsTradRepo;
                getLogger().info("银行间下行成交处理业务数据线程开启");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(StringUtil.equals(entity.getMarketIndicator(),MarketIndicatorUtil.MARKETINDICATOR_BOND)){
                                secAutoTradeService.secAutoTrade2(iCstpTrade, finalInterfTradSecdeal,resultBaseDto);
                        }else if(StringUtil.equals(entity.getMarketIndicator(),MarketIndicatorUtil.MARKETINDICATOR_REPO)){
                                repoAutoTradeService.repoAutoTrade2(iCstpTrade, finalInterfOpicsTradRepo,resultBaseDto);
                        }
                    }
                });
                thread.start();
          //  }
            //登记数据自动匹配功能,针对单条指令，自动匹配
           /* if(StringUtil.equals(entity.getMarketIndicator(),MarketIndicatorUtil.MARKETINDICATOR_BOND)){
                cstpSecTradeAutoMatch(interfTradSecdeal,iCstpTrade);
            }else if(StringUtil.equals(entity.getMarketIndicator(),MarketIndicatorUtil.MARKETINDICATOR_REPO)){
                cstpRepoTradeAutoMatch(interfOpicsTradRepo,iCstpTrade);
            }*/

        }catch (Exception e){
            resultBaseDto.setErrMsg(e.getMessage());
            //logger.error(e.getMessage());
            throw new Exception(e);
        }
        return resultBaseDto;
    }

    /**
     * 一级申购 cstp交易入库
     */
    @Transactional(rollbackFor = { Exception.class })
    @DTXTransaction
    public ResultBaseDto insertBidCstpTrade(CfetsQuotedTradeFirstBondDto entity) throws Exception {
        try {
            Thread.sleep(maxWaitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InterfCstpBjfxjg interfCstpBjfxjg = new InterfCstpBjfxjg();
        this.handleiCstpBidTrade(interfCstpBjfxjg, entity);
        ResultBaseDto resultBaseDto = new ResultBaseDto(true);
        try {
            // 生成
            interfCstpBjfxjg.setTrdId(cstpMapper.selectDealNo(DateUtil.formatDate(getSysDate()), getPrefix(isSubSystem)));
            // 插入cstp交易表
            interfCstpBjfxjgMapper.insertSelective(interfCstpBjfxjg);
            getLogger().info("银行间下行成交处理业务数据线程开启");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    secAutoTradeService.secAutoBidTrade(interfCstpBjfxjg, resultBaseDto);
                }
            });
            thread.start();
        } catch (Exception e) {
            resultBaseDto.setErrMsg(e.getMessage());
            throw new Exception(e);
        }
        return resultBaseDto;
    }

    /**
     * 报文---实体封装(一级申购)
     */
    private void handleiCstpBidTrade(InterfCstpBjfxjg interfCstpBjfxjg, CfetsQuotedTradeFirstBondDto entity) {
        BeanUtil.copyPropertiesIgnoreNull(interfCstpBjfxjg, entity);
//        iCstpTrade.setTrademethod(entity.getTradeMethod());
//        iCstpTrade.setSettltype(entity.getSettlType());
//        iCstpTrade.setDealtranstype(entity.getDealTransType());
//        iCstpTrade.setClordid(entity.getClOrdID());
//        iCstpTrade.setReference(entity.getReference());
//        iCstpTrade.setDatacategoryindicator(entity.getDataCategoryIndicator());
//        iCstpTrade.setContingencyindicator(entity.getContingencyIndicator());
//        //交易日期
//        iCstpTrade.setTradedate(entity.getTradeDate());
//        iCstpTrade.setTradetime(entity.getTradeTime());
//        iCstpTrade.setSettldate(entity.getSettlDate());
//        iCstpTrade.setRemarkindicator(entity.getRemarkIndicator());
//        //行权收益率 到期收益率
//        if(StringUtil.isNotEmpty(entity.getNoStipulations())){
//            entity.getNoStipulations().forEach(item -> {
//                if(StringUtil.equals(item.getStipulationType(),"Yield2")){
//                    iCstpTrade.setYield2(ArithmeticUtil.divide(item.getStipulationValue(),new BigDecimal(100),6));
//                }
//                if(StringUtil.equals(item.getStipulationType(),"StrikeYield")){
//                    iCstpTrade.setStrikeyield(ArithmeticUtil.divide(item.getStipulationValue(),new BigDecimal(100),6));
//                }
//            });
//        }
//        if(StringUtil.equals(entity.getMarketIndicator(),MarketIndicatorUtil.MARKETINDICATOR_BOND)){
//            iCstpTrade.setBusinesstype(BusinessTypeUtil.BUSINESSTYPE_BOND);
//            if(null!=entity.getPrice()){
//                iCstpTrade.setPrice(entity.getPrice().setScale(4,RoundingMode.HALF_UP));
//            }
//            if(null!=entity.getDirtyPrice()){
//                iCstpTrade.setDirtyprice(entity.getDirtyPrice().setScale(4,RoundingMode.HALF_UP));
//            }
//            iCstpTrade.setTradetype(entity.getTradeType());
//            iCstpTrade.setTermtomaturity(entity.getTermToMaturity());
//        }
//        else if(StringUtil.equals(entity.getMarketIndicator(),MarketIndicatorUtil.MARKETINDICATOR_REPO)){
//            iCstpTrade.setBusinesstype(BusinessTypeUtil.BUSINESSTYPE_REPO);
//            iCstpTrade.setCashholdingdays(entity.getCashHoldingDays());
//            iCstpTrade.setTradelimitdays(entity.getTradeLimitDays());
//            iCstpTrade.setSettlcurramt2(entity.getSettlCurrAmt2());
//            iCstpTrade.setUnderlyingsymbol(entity.getUnderlyingSymbol());
//            iCstpTrade.setUnderlyingsecurityid(entity.getSecurityID());
//            if(null!=entity.getPrice()){
//                iCstpTrade.setPrice(ArithmeticUtil.divide(entity.getPrice(),new BigDecimal(100),6));
//            }
//            iCstpTrade.setSettldate2(entity.getSettlDate2());
//            //处理跨托管交易本金
//            if(!StringUtil.isEmptyList(entity.getNoCrossCustodians())){
//                entity.setIsKtg("Y");
//                entity.getNoCrossCustodians().forEach(item -> {
//                    if(StringUtil.equals(item.getCrossCustodianInstitutionName(), OrgTypeUtil.CB)){
//                        iCstpTrade.setCbFpriceAmt(item.getCrossCustodianTradeAmt());
//                        return;
//                    }
//                    if(StringUtil.equals(item.getCrossCustodianInstitutionName(),OrgTypeUtil.SC)){
//                        iCstpTrade.setScFpriceAmt(item.getCrossCustodianTradeAmt());
//                        return;
//                    }
//                });
//            }
//        }
//        //处理交易员信息
//        处理交易员信息(iCstpTrade,entity);
    }

    /**
     * 报文---实体封装
    */
    private void handleiCstpTrade(InterfCstpTrade iCstpTrade,CfetsQuotedTradeCommonDto entity){
        BeanUtil.copyPropertiesIgnoreNull(iCstpTrade, entity);
        iCstpTrade.setDatasource(DictCodeListExt1.生成方式_接口接入.getValue());
        iCstpTrade.setMsgtype(entity.getMsgType());
        iCstpTrade.setMsgseqnum(entity.getMsgSeqNum());
        iCstpTrade.setSendingtime(entity.getSendingTime());
        iCstpTrade.setOnbehalfofcompid(entity.getOnBehalfOfCompID());
        iCstpTrade.setTransacttime(entity.getTransactTime());
        iCstpTrade.setExectype(entity.getExecType());
        iCstpTrade.setExecid(entity.getExecID());
        iCstpTrade.setSecurityid(entity.getSecurityID());
        iCstpTrade.setQuoteid(entity.getQuoteID());
        iCstpTrade.setLastqty(entity.getLastQty());
        iCstpTrade.setSettlcurramt(entity.getSettlCurrAmt());
        iCstpTrade.setMarketindicator(entity.getMarketIndicator());
        iCstpTrade.setClearingmethod(entity.getClearingMethod());
        iCstpTrade.setPrincipal(entity.getPrincipal());
        iCstpTrade.setTotalprincipal(entity.getTotalPrincipal());
        iCstpTrade.setCashbondindicator(entity.getCashBondIndicator());
        iCstpTrade.setSettlcurrency(entity.getSettlCurrency());
        iCstpTrade.setDeliverytype(entity.getDeliveryType());
        iCstpTrade.setDeliverytype2(entity.getDeliveryType2());
        if(null!=entity.getAccruedInterestTotalAmt()){
            iCstpTrade.setAccruedinteresttotalamt(entity.getAccruedInterestTotalAmt().setScale(2,RoundingMode.HALF_UP));
        }
        if(null!=entity.getTradeCashAmt()){
            iCstpTrade.setTradecashamt(entity.getTradeCashAmt().setScale(2,RoundingMode.HALF_UP));
        }
        iCstpTrade.setOrderid(entity.getOrderID());
        if(null!=entity.getAccruedInterestAmt()){
            iCstpTrade.setAccruedinterestamt(entity.getAccruedInterestAmt().setScale(5,RoundingMode.HALF_UP));
        }
        iCstpTrade.setTrademethod(entity.getTradeMethod());
        iCstpTrade.setSettltype(entity.getSettlType());
        iCstpTrade.setDealtranstype(entity.getDealTransType());
        iCstpTrade.setClordid(entity.getClOrdID());
        iCstpTrade.setReference(entity.getReference());
        iCstpTrade.setDatacategoryindicator(entity.getDataCategoryIndicator());
        iCstpTrade.setContingencyindicator(entity.getContingencyIndicator());
        //交易日期
        iCstpTrade.setTradedate(entity.getTradeDate());
        iCstpTrade.setTradetime(entity.getTradeTime());
        iCstpTrade.setSettldate(entity.getSettlDate());
        iCstpTrade.setRemarkindicator(entity.getRemarkIndicator());
        //行权收益率 到期收益率
        if(StringUtil.isNotEmpty(entity.getNoStipulations())){
            entity.getNoStipulations().forEach(item -> {
                if(StringUtil.equals(item.getStipulationType(),"Yield2")){
                    iCstpTrade.setYield2(ArithmeticUtil.divide(item.getStipulationValue(),new BigDecimal(100),6));
                }
                if(StringUtil.equals(item.getStipulationType(),"StrikeYield")){
                    iCstpTrade.setStrikeyield(ArithmeticUtil.divide(item.getStipulationValue(),new BigDecimal(100),6));
                }
            });
        }
        if(StringUtil.equals(entity.getMarketIndicator(),MarketIndicatorUtil.MARKETINDICATOR_BOND)){
            iCstpTrade.setBusinesstype(BusinessTypeUtil.BUSINESSTYPE_BOND);
            if(null!=entity.getPrice()){
                iCstpTrade.setPrice(entity.getPrice().setScale(4,RoundingMode.HALF_UP));
            }
            if(null!=entity.getDirtyPrice()){
                iCstpTrade.setDirtyprice(entity.getDirtyPrice().setScale(4,RoundingMode.HALF_UP));
            }
            iCstpTrade.setTradetype(entity.getTradeType());
            iCstpTrade.setTermtomaturity(entity.getTermToMaturity());
        }
        else if(StringUtil.equals(entity.getMarketIndicator(),MarketIndicatorUtil.MARKETINDICATOR_REPO)){
            iCstpTrade.setBusinesstype(BusinessTypeUtil.BUSINESSTYPE_REPO);
            iCstpTrade.setCashholdingdays(entity.getCashHoldingDays());
            iCstpTrade.setTradelimitdays(entity.getTradeLimitDays());
            iCstpTrade.setSettlcurramt2(entity.getSettlCurrAmt2());
            iCstpTrade.setUnderlyingsymbol(entity.getUnderlyingSymbol());
            iCstpTrade.setUnderlyingsecurityid(entity.getSecurityID());
            if(null!=entity.getPrice()){
                iCstpTrade.setPrice(ArithmeticUtil.divide(entity.getPrice(),new BigDecimal(100),6));
            }
            iCstpTrade.setSettldate2(entity.getSettlDate2());
            //处理跨托管交易本金
            if(!StringUtil.isEmptyList(entity.getNoCrossCustodians())){
                entity.setIsKtg("Y");
                entity.getNoCrossCustodians().forEach(item -> {
                    if(StringUtil.equals(item.getCrossCustodianInstitutionName(), OrgTypeUtil.CB)){
                        iCstpTrade.setCbFpriceAmt(item.getCrossCustodianTradeAmt());
                        return;
                    }
                    if(StringUtil.equals(item.getCrossCustodianInstitutionName(),OrgTypeUtil.SC)){
                        iCstpTrade.setScFpriceAmt(item.getCrossCustodianTradeAmt());
                        return;
                    }
                });
            }
        }
        //处理交易员信息
        处理交易员信息(iCstpTrade,entity);
    }

    private void 处理交易员信息(InterfCstpTrade iCstpTrade,CfetsQuotedTradeCommonDto entity){
        if(StringUtil.isNotEmpty(entity.getNoPartyIDs())){
            entity.getNoPartyIDs().forEach(item -> {
                String counterdepositaccountnumberKtg = StringUtil.EMPTY;
                if(StringUtil.equals(entity.getIsKtg(),"Y")&&!StringUtil.isEmptyList(item.getNoRelatedPartydDetails())){
                    for(NoRelatedPartydDetail noRelatedPartydDetail : item.getNoRelatedPartydDetails()){
                        if(!StringUtil.isEmptyList(noRelatedPartydDetail.getNoRelatedPartydDetailSubIDs())){
                            for(NoRelatedPartydDetailSubID noRelatedPartydDetailSubID : noRelatedPartydDetail.getNoRelatedPartydDetailSubIDs()){
                                if(StringUtil.equals(noRelatedPartydDetailSubID.getRelatedPartydDetailSubType(),"10")){
                                    counterdepositaccountnumberKtg = counterdepositaccountnumberKtg+noRelatedPartydDetailSubID.getRelatedPartydDetailSubID()+",";
                                    break;
                                }
                            }
                        }
                    }
                }
                item.setCounterdepositaccountnumberKtg(counterdepositaccountnumberKtg);
                if((StringUtil.equals(entity.getSide(),"1")&&StringUtil.equals(item.getPartyRole(),"119"))
                ||(StringUtil.equals(entity.getSide(),"4")&&StringUtil.equals(item.getPartyRole(),"120"))){
                    iCstpTrade.setOurinstitutioncode(item.getPartyID());
                    iCstpTrade.setOurinstitutionrole(item.getPartyRole());
                    本方交易员信息(iCstpTrade,item);
                }
                if((StringUtil.equals(entity.getSide(),"4")&&StringUtil.equals(item.getPartyRole(),"119"))
                        ||(StringUtil.equals(entity.getSide(),"1")&&StringUtil.equals(item.getPartyRole(),"120"))){
                    iCstpTrade.setCounterinstitutioncode(item.getPartyID());
                    iCstpTrade.setCounterinstitutionrole(item.getPartyRole());
                    对方交易员信息(iCstpTrade,item);
                }
            });
        }
    }

    private void 本方交易员信息(InterfCstpTrade iCstpTrade, NoPartyID noPartyId){
        if(StringUtil.isNotEmpty(noPartyId.getNoPartySubIDs())){
            noPartyId.getNoPartySubIDs().forEach(item ->{
                String partySubID = item.getPartySubID();
                switch (item.getPartySubIDType()){
                    case "6" :
                        iCstpTrade.setOuraddress(partySubID);
                        break;
                    case "10" :
                        iCstpTrade.setOurdepositaccountnumber(StringUtil.isNotEmpty(noPartyId.getCounterdepositaccountnumberKtg())?noPartyId.getCounterdepositaccountnumberKtg():partySubID);
                        break;
                    case "15" :
                        iCstpTrade.setOurcapitalaccountnumber(partySubID);
                        break;
                    case "22" :
                        iCstpTrade.setOurdepositaccountname(partySubID);
                        break;
                    case "23" :
                        iCstpTrade.setOurcapitalaccountname(partySubID);
                        break;
                    case "29" :
                        iCstpTrade.setOurtrademembergroup(partySubID);
                        break;
                    case "101" :
                        iCstpTrade.setOurtradername(partySubID);
                        break;
                    case "102" :
                        iCstpTrade.setOurinstitutionenglishshortname(partySubID);
                        break;
                    case "110" :
                        iCstpTrade.setOurcapitalbankname(partySubID);
                        break;
                    case "111" :
                        iCstpTrade.setOurdepositinstitution(partySubID);
                        break;
                    case "112" :
                        iCstpTrade.setOurpaymentsystemcode(partySubID);
                        break;
                    case "124" :
                        iCstpTrade.setOurinstitutionchinesefullname(partySubID);
                        break;
                    case "125" :
                        iCstpTrade.setOurinstitutionchineseshortname(partySubID);
                        break;
                    case "126" :
                        iCstpTrade.setOurtraderchinesename(partySubID);
                        break;
                }
            });
        }
        // mod by chenggaoling 20211223 交易员联系方式，当方向为4时赋值不正确  start
        if(StringUtil.isNotEmpty(noPartyId.getNoContactInfos())){
            noPartyId.getNoContactInfos().forEach(item ->{
                if(StringUtil.equals(item.getContactInfoIDType(),"6")){
                    iCstpTrade.setOurtelephone(item.getContactInfoID());
                }
            });
        }
        //交易员联系方式(iCstpTrade,noPartyId);
        // mod by chenggaoling 20211223 交易员联系方式，当方向为4时赋值不正确  end
    }

    private void 对方交易员信息(InterfCstpTrade iCstpTrade, NoPartyID noPartyId){
        if(StringUtil.isNotEmpty(noPartyId.getNoPartySubIDs())){
            noPartyId.getNoPartySubIDs().forEach(item ->{
                String partySubID = item.getPartySubID();
                switch (item.getPartySubIDType()){
                    case "6" :
                        iCstpTrade.setCounteraddress(partySubID);
                        break;
                    case "10" :
                        iCstpTrade.setCounterdepositaccountnumber(StringUtil.isNotEmpty(noPartyId.getCounterdepositaccountnumberKtg())?noPartyId.getCounterdepositaccountnumberKtg():partySubID);
                        break;
                    case "15" :
                        iCstpTrade.setCountercapitalaccountnumber(partySubID);
                        break;
                    case "22" :
                        iCstpTrade.setCounterdepositaccountname(partySubID);
                        break;
                    case "23" :
                        iCstpTrade.setCountercapitalaccountname(partySubID);
                        break;
                    case "29" :
                        iCstpTrade.setCountertrademembergroup(partySubID);
                        break;
                    case "101" :
                        iCstpTrade.setCountertradername(partySubID);
                        break;
                    case "102" :
                        iCstpTrade.setInstitutionenglishshortname(partySubID);
                        break;
                    case "110" :
                        iCstpTrade.setCountercapitalbankname(partySubID);
                        break;
                    case "111" :
                        iCstpTrade.setCounterdepositinstitution(partySubID);
                        break;
                    case "112" :
                        iCstpTrade.setCounterpaymentsystemcode(partySubID);
                        break;
                    case "124" :
                        iCstpTrade.setInstitutionchinesefullname(partySubID);
                        break;
                    case "125" :
                        iCstpTrade.setInstitutionchineseshortname(partySubID);
                        break;
                    case "126" :
                        iCstpTrade.setCountertraderchinesename(partySubID);
                        break;
                }
            });
        }
        // mod by chenggaoling 20211223 交易员联系方式，当方向为4时赋值不正确  start
        if(StringUtil.isNotEmpty(noPartyId.getNoContactInfos())){
            noPartyId.getNoContactInfos().forEach(item ->{
                if(StringUtil.equals(item.getContactInfoIDType(),"6")){
                    iCstpTrade.setCountertelephone(item.getContactInfoID());
                }
            });
        }
        //交易员联系方式(iCstpTrade,noPartyId);
        // mod by chenggaoling 20211223 交易员联系方式，当方向为4时赋值不正确  end
    }

    private void 交易员联系方式(InterfCstpTrade iCstpTrade, NoPartyID noPartyId){
        if(StringUtil.isNotEmpty(noPartyId.getNoContactInfos())){
            noPartyId.getNoContactInfos().forEach(item ->{
                if(StringUtil.equals(item.getContactInfoIDType(),"6")){
                    if(StringUtil.equals(noPartyId.getPartyRole(),"119")){
                        iCstpTrade.setOurtelephone(item.getContactInfoID());
                    }
                    else if(StringUtil.equals(noPartyId.getPartyRole(),"120")){
                        iCstpTrade.setCountertelephone(item.getContactInfoID());
                    }
                }
            });
        }
    }
    /**
     * 插入或更新回购交易表
     *
     * @param iCstpTrade
     * @return 条数
     * @see
     * @since 1.0
     */
    private InterfOpicsTradRepo insertOrUpdateRepo(InterfCstpTrade iCstpTrade,List<NoUnderlyings> seList) {
        // 成交编号
        String execid = iCstpTrade.getExecid();
        // 交易编号
        String dealno = cstpMapper.selectRepoByExecid(execid);
        // 本方交易员查询,交易员代码
        //String traderId = cstpMapper.selectBuytrader(iCstpTrade.getOurtraderchinesename());

        InterfOpicsTradRepo interfOpicsTradRepo = cstpMapper.selectTradRepo(execid, getSysDate(),
                BusinessTypeUtil.BUSINESSTYPE_REPO);
        interfOpicsTradRepo.setDealno(dealno);
        interfOpicsTradRepo.setBuycost(cstpMapper.selectPortfolioIdByQuoteId(iCstpTrade.getQuoteid(),BusinessTypeUtil.BUSINESSTYPE_REPO));
        interfOpicsTradRepo.setProdtype(ProdTypeUtil.PRODTYPE_PLEDGE);
        interfOpicsTradRepo.setOritradeid(execid);
        if(StringUtil.isNotEmpty(iCstpTrade.getOurinstitutioncode())){
            int count = cstpMapper.getPtlByOurinstitutioncode(iCstpTrade.getOurinstitutioncode());
            //已经关联组合
            if(count>0){
                interfOpicsTradRepo.setBuytrader(iCstpTrade.getOurtradername());
                interfOpicsTradRepo.setSelltrader(iCstpTrade.getCounterinstitutioncode());
                interfOpicsTradRepo.setSeller(iCstpTrade.getCountertradername());
                if(StringUtil.equals(interfOpicsTradRepo.getPs(),"RB")){
                    interfOpicsTradRepo.setPs(DictCodeList.回购拆借方向_逆回购.getValue());
                }else{
                    interfOpicsTradRepo.setPs(DictCodeList.回购拆借方向_正回购.getValue());
                }
            }else{
                interfOpicsTradRepo.setBuytrader(iCstpTrade.getCountertradername());
                interfOpicsTradRepo.setSelltrader(iCstpTrade.getOurinstitutioncode());
                interfOpicsTradRepo.setSeller(iCstpTrade.getOurtradername());
                if(StringUtil.equals(interfOpicsTradRepo.getPs(),"RB")){
                    interfOpicsTradRepo.setPs(DictCodeList.回购拆借方向_正回购.getValue());
                }else{
                    interfOpicsTradRepo.setPs(DictCodeList.回购拆借方向_逆回购.getValue());

                }
            }
        }
        /*if (StringUtil.isNotEmpty(traderId)) {
            interfOpicsTradRepo.setBuytrader(traderId);
        }*/

        if (StringUtil.isNotEmpty(dealno)) {
            // 更新
            interfOpicsTradRepo.setTradeid(dealno);
            // 回购交易表新加主键会自动生成更新
            repoMapper.updateByPrimaryKeySelective(interfOpicsTradRepo);
        } else {
            // 插入
            dealno = cstpMapper.selectDealNo(DateUtil.formatDate(getSysDate()), getPrefix(isSubSystem));
            interfOpicsTradRepo.setTradeid(dealno);
            repoMapper.insertSelective(interfOpicsTradRepo);
        }

        // 如果是质押式，需要插入或更新质押品信息
        if (StringUtil.isNotEmpty(seList)) {
            insertOrUpdateRepoAsset(seList, execid,iCstpTrade.getOurdepositaccountnumber());
        }
        return interfOpicsTradRepo;
    }

    /**
     * 插入或更新质押品信息
     *
     * @param seList 抵押品信息
     * @param execid 成交编号
     * @return 条数
     * @see
     * @since 1.0
     */
    private int insertOrUpdateRepoAsset(List<NoUnderlyings> seList, String execid,String ourdepositaccountnumber) {
        int count = 0;
        for (NoUnderlyings sEntity : seList) {
            InterfTradRepoasst interfTradRepoasst = new InterfTradRepoasst();
            BeanUtil.copyPropertiesIgnoreNull(interfTradRepoasst, sEntity);
            interfTradRepoasst.setTradeid(execid);
            // 折算比例
            interfTradRepoasst.setConvrate(new BigDecimal(1));
            if(StringUtil.isNotEmpty(sEntity.getNoUnderlyingStips())){
                sEntity.getNoUnderlyingStips().forEach(item -> {
                    if(StringUtil.equals(item.getUnderlyingStipType(),"Haircut")){
                        interfTradRepoasst.setConvrate(item.getUnderlyingStipValue());
                    }
                });
            }
            // 成交金额=折算比例*券面总额
            interfTradRepoasst.setFacetotalamt(sEntity.getUnderlyingQty());
            interfTradRepoasst.setDealamt(ArithmeticUtil.multiply(interfTradRepoasst.getConvrate(),sEntity.getUnderlyingQty()));
            interfTradRepoasst.setSecid(sEntity.getUnderlyingSecurityID());
            interfTradRepoasst.setSecname(sEntity.getUnderlyingSymbol());
            interfTradRepoasst.setOurdepositaccountnumber(ourdepositaccountnumber);
            // 判断抵押品信息是否存在，若存在则更新，否则插入
            String tradeid = cstpMapper.selectrepoAsset(sEntity.getUnderlyingSecurityID(), execid);
            interfTradRepoasst.setSeqNo(Long.parseLong(cstpMapper.selectSeqNo()));
            if (StringUtil.isNotEmpty(tradeid)) {
                // 更新
                count += cstpMapper.updateRepoAsst(interfTradRepoasst);
            } else {
                count += repoAsstMapper.insertSelective(interfTradRepoasst);
            }
        }
        return count;
    }


    /**
     * 获取组合代码
     *
     * @param ourcapitalaccountnumber 资金账号
     * @return 组合代码
     * @see
     * @since 1.0
     */
    private String selectPortfolioId(String ourcapitalaccountnumber) {
        Map<String, Object> map = new HashMap<>();
        map.put("A08", BussinessDictCodeList.资金账户类型_甲类户.getValue());
        map.put("A09", BussinessDictCodeList.资金账户类型_上清所DVP账户.getValue());
        map.put("A10", BussinessDictCodeList.资金账户类型_中债登DVP账户.getValue());
        map.put("ourcapitalaccountnumber", ourcapitalaccountnumber);

        return cstpMapper.selectPortfolioId(map);

    }

    /**
     * 插入或更新债券交易表
     *
     * @param interfCstpTrade
     * @return 条数
     * @see
     * @since 1.0
     */
    private InterfTradSecdeal insertOrUpdateSec(InterfCstpTrade interfCstpTrade) {
        // 交易编号
        String dealno = cstpMapper.selectSecByExecid(interfCstpTrade.getExecid());
        // 本方交易员查询,交易员代码
        String traderId = cstpMapper.selectBuytrader(interfCstpTrade.getOurtraderchinesename());
        InterfTradSecdeal interfCstpSecdeal = cstpMapper.selectTradeSec(interfCstpTrade.getExecid(), getSysDate(),
                BusinessTypeUtil.BUSINESSTYPE_BOND);
        //interfCstpSecdeal.setProfolioId(selectPortfolioId(interfCstpTrade.getOurcapitalaccountnumber()));
        //投资交易系统  可以通过报价编号查询到组合
        interfCstpSecdeal.setProfolioId(cstpMapper.selectPortfolioIdByQuoteId(interfCstpTrade.getQuoteid(),BusinessTypeUtil.BUSINESSTYPE_BOND));

        if (StringUtil.isNotEmpty(traderId)) {
            interfCstpSecdeal.setTrad(traderId);
        }
        if(StringUtil.isNotEmpty(interfCstpTrade.getOurinstitutioncode())){
            int count = cstpMapper.getPtlByOurinstitutioncode(interfCstpTrade.getOurinstitutioncode());
            //已经关联组合
            if(count>0){
                interfCstpSecdeal.setTrad(interfCstpTrade.getOurtradername());
                interfCstpSecdeal.setCno(interfCstpTrade.getCounterinstitutioncode());
                interfCstpSecdeal.setClienttrader(interfCstpTrade.getCountertradername());
                if(StringUtil.equals(interfCstpSecdeal.getPs(),"P")){
                    interfCstpSecdeal.setPs(DictCodeList.交易方向_买入.getValue());
                }else{
                    interfCstpSecdeal.setPs(DictCodeList.交易方向_卖出.getValue());
                }
            }else{
                interfCstpSecdeal.setTrad(interfCstpTrade.getCountertradername());
                interfCstpSecdeal.setCno(interfCstpTrade.getOurinstitutioncode());
                interfCstpSecdeal.setClienttrader(interfCstpTrade.getOurtradername());
                if(StringUtil.equals(interfCstpSecdeal.getPs(),"P")){
                    interfCstpSecdeal.setPs(DictCodeList.交易方向_卖出.getValue());
                }else{
                    interfCstpSecdeal.setPs(DictCodeList.交易方向_买入.getValue());
                }
            }
        }
        // 根据原始交易编号，判断数据是否已经存在，如果已存在则更新，否则进入插入操作
        if (StringUtil.isNotEmpty(dealno)) {
            // 更新
            interfCstpSecdeal.setDealno(dealno);
            secMapper.updateByPrimaryKeySelective(interfCstpSecdeal);
        } else {
            dealno = cstpMapper.selectDealNo(DateUtil.formatDate(getSysDate()), getPrefix(isSubSystem));
            interfCstpSecdeal.setDealno(dealno);
            // 插入
            secMapper.insertSelective(interfCstpSecdeal);
        }
        return interfCstpSecdeal;
    }


    private String getPrefix(String isSubSystem) {
        return prefix;
    }

    /**
     * 下行交易债券自动匹配
     * @param interfCstpTrade
     */
   /* private void cstpSecTradeAutoMatch(InterfTradSecdeal interfTradSecdeal,InterfCstpTrade iCstpTrade) {
        if(!StringUtil.isNotEmpty(interfTradSecdeal.getYield8()) || !StringUtil.isNotEmpty(interfTradSecdeal.getPrinamt())
                 || ! StringUtil.isNotEmpty(interfTradSecdeal.getSettdate()) || !StringUtil.isNotEmpty(interfTradSecdeal.getDealdate())
        ){
            return;
        }
        TrdConfirmRegAndIbuInfoDto confirmDto=new TrdConfirmRegAndIbuInfoDto();
        Boolean isContinue=true;
        Boolean isUpdate=false;
        Boolean isInsert=false;
        //CSTP下行成交回报数据的成交编号匹配已生成的银行间交易数据中的成交编号,根据cstp下行成交回报的成交编号，查询匹配关系
        TrdConfirmMatchDto matchDto=cstpMapper.selectMatchDtoByExecId(interfTradSecdeal.getFedealno());
        if(isContinue && null!=matchDto && StringUtil.isNotEmpty(matchDto.getPreTradeId())){
            Map<String, Object> param=new HashMap<>();
            param.put("totleTradeId",matchDto.getPreTradeId());
            param.put("finprodType2",DictCodeListExt1.金融产品类型_债券.getValue());
            //根据登记编号，查询登记信息
            List<TrdConfirmRegAndIbuInfoDto> confirmDtoList=cstpMapper.selectRegDtoByPrimaryKey(param);
            if(StringUtil.isEmptyList(confirmDtoList)){
               confirmDto=confirmDtoList.get(0);
               isContinue=false;
               isUpdate=true;
            }
        }
        //CSTP下行成交回报数据中的报价编号匹配报价数据的业务编号（报价编号）
        if(isContinue && null!=iCstpTrade && StringUtil.isNotEmpty(iCstpTrade.getQuoteid())){
            //根据报价编号，查询报价信息
            confirmDto=cstpMapper.selectIbuDtoByPrimaryKey(iCstpTrade.getQuoteid());
            isContinue=false;
            isInsert=true;
        }

        //CSTP下行成交回报数据再与已生成的银行间交易品种（默认债券买卖和质押式回购首期）交易登记数据进行匹配,查询所有的符合条件登记信息
        if(isContinue && null!=interfTradSecdeal  && null!=iCstpTrade){
            Map<String, Object> param=new HashMap<>();
            param.put("finprodType2",DictCodeListExt1.金融产品类型_债券.getValue());
            param.put("tradeDate",interfTradSecdeal.getDealdate());
            param.put("finprodMarketId",interfTradSecdeal.getSecid());
            param.put("ps",interfTradSecdeal.getPs());
            param.put("settleSpeed",interfTradSecdeal.getSettleSpeed());
            param.put("secManageAcctId",interfTradSecdeal.getOurdepositaccountnumber());
            param.put("tradeYield",interfTradSecdeal.getYield8());
            param.put("faceValue",interfTradSecdeal.getPrinamt());
            param.put("settleDate",interfTradSecdeal.getSettdate());
            param.put("deliveryType",iCstpTrade.getDeliverytype());
            //根据登记编号，查询登记信息
            List<TrdConfirmRegAndIbuInfoDto> confirmDtoList=cstpMapper.selectRegDtoByPrimaryKey(param);
            if(StringUtil.isEmptyList(confirmDtoList)){
                confirmDto=confirmDtoList.get(0);
                isInsert=true;
            }
        }
        //将CSTP数据和已有的登记或报价数据进行比较
        //债券
        if( null==confirmDto || !StringUtil.isNotEmpty(confirmDto.getTradeYield())
                || !StringUtil.isNotEmpty(confirmDto.getFaceValue())
                || !StringUtil.isNotEmpty(confirmDto.getSettleDate())
                || !StringUtil.isNotEmpty(confirmDto.getTradeDate())
        ){
            return;
        }
        if (confirmDto.getTradeDate().compareTo(interfTradSecdeal.getDealdate())==0
                && StringUtil.equals(confirmDto.getFinprodMarketId(),interfTradSecdeal.getSecid())
                && StringUtil.equals(confirmDto.getPs(), interfTradSecdeal.getPs())
                && StringUtil.equals(confirmDto.getSettleSpeed(), interfTradSecdeal.getSettleSpeed())
                && StringUtil.equals(confirmDto.getSecManageAcctId(), interfTradSecdeal.getOurdepositaccountnumber())
                && confirmDto.getTradeYield().compareTo(interfTradSecdeal.getYield8())==0
                && confirmDto.getFaceValue().compareTo(interfTradSecdeal.getPrinamt())==0
                && confirmDto.getSettleDate().compareTo(interfTradSecdeal.getSettdate())!=0
                && StringUtil.equals(confirmDto.getDeliveryType(), iCstpTrade.getDeliverytype())
        ) {
            //更新下行匹配关系表
            if (isUpdate) {
                // 更新
                TrdConfirmMatchDto match=new TrdConfirmMatchDto();
                match.setPreTradeId(confirmDto.getTotleTradeId());
                match.setMatchType(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue());
                match.setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
                match.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
                cstpMapper.updateTrdQutoMatchInfo(matchDto);
                return;
            } else if(isInsert){
                //插入匹配表
                TrdConfirmMatchDto match=new TrdConfirmMatchDto();
                match.setSeqno(IDGenerator.generate());
                match.setCstpTradeId(interfTradSecdeal.getDealno());
                match.setExeId(interfTradSecdeal.getFedealno());
                match.setPreTradeId(confirmDto.getTotleTradeId());
                match.setMatchType(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue());
                match.setMatchStatus(BussinessDictCodeList.匹配状态_已匹配.getValue());
                match.setTrdConfStatus(DictCodeList.确认状态_未确认.getValue());
                match.setCreateUser(SecurityUtil.getLoginUser().getUserName());
                match.setCreateDept(CommonUtil.getDeptCode());
                match.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
                cstpMapper.insertTrdQutoMatchInfo(matchDto);
                return;
            }
        }
    }*/

} 

