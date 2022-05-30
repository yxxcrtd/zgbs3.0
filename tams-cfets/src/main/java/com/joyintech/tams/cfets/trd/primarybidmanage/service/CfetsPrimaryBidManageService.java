package com.joyintech.tams.cfets.trd.primarybidmanage.service;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.SecPrinSchedule;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.shell.AccurIntCalculator;
import com.joyintech.calculation.shell.AccurIntForSecheduleCalculator;
import com.joyintech.calculation.shell.PriceByYieldCalculator;
import com.joyintech.calculation.shell.YieldByPriceCalculator;
import com.joyintech.calculation.shell.param.*;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.dao.CfgReadonlyFormShowMapper;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.entity.CfgDataTableShow;
import com.joyintech.tams.business.common.entity.CfgDataTableShowFlag;
import com.joyintech.tams.business.common.entity.CfgReadonlyFormShow;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.cfets.trd.primarybidmanage.dao.CfetsPrimaryBidManageMapper;
import com.joyintech.tams.business.common.dao.CfgDataTableFlagMapper;
import com.joyintech.tams.business.common.dao.CfgDataTableMapper;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.invest.common.asset.service.BondAssetCalcService.*;
import static com.joyintech.tams.invest.common.asset.service.BondAssetCalcService.toLocalDate;

/**
 * 债券标的信息新建
 *
 * @author wangyang1
 * @version 1.0 2022年5月6日
 * @see CfetsPrimaryBidManageService
 * @since 1.0
 */
@Service
public class CfetsPrimaryBidManageService extends BaseService<CfetsPrimaryBidManageSaveRequest> implements BaseProcessService<CfetsPrimaryBidManageSaveRequest>{

    @Autowired(required = false)
    private CfetsPrimaryBidManageMapper cfetsPrimaryBidManageMapper;

    @Autowired(required = false)
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired(required = false)
    private WflBidResultDetailMapper wflBidResultDetailMapper;

    @Autowired(required = false)
    private WflBidResultTotalMapper wflBidResultTotalMapper;

    @Autowired(required = false)
    private BidResultDetailMapper bidResultDetailMapper;

    @Autowired(required = false)
    private BidResultTotalMapper bidResultTotalMapper;

    @Autowired(required = false)
    private BidOrderMapper bidOrderMapper;

    @Autowired(required = false)
    private FinProductIssueMapper finProductIssueMapper;

    @Autowired(required = false)
    private FinProductMapper finProductMapper;

    @Autowired(required = false)
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;

    @Autowired(required = false)
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired(required = false)
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired(required = false)
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired(required = false)
    private TrdTradeFeeTotalMapper trdTradeFeeTotalMapper;

    @Autowired(required = false)
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired(required = false)
    private PtlTradeMapper ptlTradeMapper;

    @Autowired(required = false)
    private EtrFinishStatusMapper etrFinishStatusMapper;

    @Autowired(required = false)
    private StlOrdersMapper stlOrdersMapper;

    @Autowired
    private IPlatformDate iplatformDate;
    /**
     * 注入业务主键生产类
     */
    @Autowired(required=false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired(required=false)
    private CfgReadonlyFormShowMapper cfgReadonlyFormShowMapper;

    @Autowired(required=false)
    private CfgDataTableFlagMapper cfgDataTableFlagMapper;

    @Autowired(required=false)
    private CfgDataTableMapper cfgDataTableMapper;

    /**
     * 默认设置
     */
    private static final int EFFERANGE_NO = 0;


    /**
     * 默认设置用户ID
     */
    private static final String USER_ID = "COMM";

    /**
     * 用户自定义设置
     */
    private static final int EFFERANGE_IS = 1;

    public Map<String,Object> getBidManageCfmInfo(CfetsPrimaryBidManageCfmRequest request) {
        if (StringUtil.isNotEmpty(request.getBidId())) {
            String pageCode = "CfetsPrimaryBidManage001";
            String dtCode = "CfetsPrimaryInfo";
            String spcdCode = "999999";
            Map<String, Object> param = BeanUtil.object2Map(request);
            Map<String, Object> resultData = new HashMap<>();
            List<CfgReadonlyFormShow> infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            //排序
            Collections.sort(infoForm, new Comparator<CfgReadonlyFormShow>() {
                @Override
                public int compare(CfgReadonlyFormShow o1, CfgReadonlyFormShow o2) {
                    return o1.getSeqno().compareTo(o2.getSeqno());
                }
            });
            String userId = SecurityUtil.getLoginUser().getUserName();
            CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, "BidProductInfo", spcdCode, userId);
            List<CfgDataTableShow> detailTable = null;
            if (null == flag) {
                detailTable = cfgDataTableMapper.getByPageCode(pageCode, "BidProductInfo", spcdCode, EFFERANGE_NO, USER_ID);
            } else {
                detailTable = cfgDataTableMapper.getByPageCode(pageCode, "BidProductInfo", spcdCode, EFFERANGE_IS, userId);
            }
            flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, "SummaryBidInfo", spcdCode, userId);
            List<CfgDataTableShow> listTable = null;
            if (null == flag) {
                listTable = cfgDataTableMapper.getByPageCode(pageCode, "SummaryBidInfo", spcdCode, EFFERANGE_NO, USER_ID);
            } else {
                listTable = cfgDataTableMapper.getByPageCode(pageCode, "SummaryBidInfo", spcdCode, EFFERANGE_IS, userId);
            }
            CfetsPrimaryBidManageCfmResponse response = cfetsPrimaryBidManageMapper.getBidManageCfmInfo(param);
            List<BidProductDto> bidProductList = cfetsPrimaryBidManageMapper.getBidProductList(param);
            Map<BigDecimal, List<BidProductDto>> summaryBidMap = bidProductList.stream().collect(JoyinCollectors.groupingBy(item -> item.getWinPrice()));
            List<SummaryBidDto>  summaryBidList = new ArrayList<>();
            for(BigDecimal winPrice:summaryBidMap.keySet()){
                SummaryBidDto summaryBidDto = new SummaryBidDto();
                BigDecimal totalBidPrinamt = null;
                BigDecimal winPrinAmt = null;
                for (BidProductDto bidProductDto:summaryBidMap.get(winPrice)){
                    totalBidPrinamt = ArithmeticUtil.doAdd(totalBidPrinamt,bidProductDto.getTotalBidPrinamt());
                    winPrinAmt = ArithmeticUtil.doAdd(winPrinAmt,bidProductDto.getWinPrinAmt());
                }
                summaryBidDto.setTotalBidPrinamt(totalBidPrinamt);
                summaryBidDto.setWinPrinAmt(winPrinAmt);
                summaryBidDto.setWinPrice(winPrice);
                summaryBidList.add(summaryBidDto);
            }
//            response.setSummaryBidList(summaryBidList);
            resultData.put(dtCode + "_form", infoForm);
            resultData.put(dtCode + "_formDetail", detailTable);
            resultData.put(dtCode + "_formList", listTable);
            resultData.put(dtCode + "_data", response);
            resultData.put(dtCode + "_detail", bidProductList);
            resultData.put(dtCode + "_list", summaryBidList);
            return resultData;
        }
        return null;
    }

    public CfetsPrimaryBidManageSaveRequest getBidManageCfmInsert(CfetsPrimaryBidManageCfmRequest request) {
        if (StringUtil.isNotEmpty(request.getBidId())) {
            Map<String, Object> param = BeanUtil.object2Map(request);
            CfetsPrimaryBidManageSaveRequest response = cfetsPrimaryBidManageMapper.selectBidOrderInfo(param);
            List<CfetsPrimaryBidManageSaveDetailDto> detailDtos = cfetsPrimaryBidManageMapper.selectBidOrderDtail(param);
            response.setAllocationDetails(detailDtos);
            return response;
        }
        return null;
    }

    public void unBidSave(CfetsPrimaryBidManageSaveRequest request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        BidResultTotal bidResultTotal = new BidResultTotal();
        BeanUtil.copyPropertiesIgnoreNull(bidResultTotal,request);
        bidResultTotal.setStatus(DictCodeListExt1.有效状态_有效.getValue());
        bidResultTotalMapper.insertSelective(bidResultTotal);
        BidOrder record = new BidOrder();
        record.setBidId(request.getBidId());
        record.setIsWin(DictCodeList.是否_否.getValue());
        bidOrderMapper.updateByPrimaryKeySelective(record);
        for(CfetsPrimaryBidManageSaveDetailDto detailDto: request.getAllocationDetails()){
            BidResultDetail bidResultDetail = new BidResultDetail();
            BeanUtil.copyPropertiesIgnoreNull(bidResultDetail,request);
            BeanUtil.copyPropertiesIgnoreNull(bidResultDetail,detailDto);
            bidResultDetail.setDetailWinId(IDGenerator.generate());
            bidResultDetail.setStatus(DictCodeListExt1.有效状态_有效.getValue());
            bidResultDetailMapper.insertSelective(bidResultDetail);
        }
    }

    public AjaxResult unBidSave(CfetsPrimaryBidManageCfmRequest request) {
        AjaxResult ajax = new AjaxResult();
        if (StringUtil.isNotEmpty(request.getBidId())) {
            Map<String, Object> param = BeanUtil.object2Map(request);
            CfetsPrimaryBidManageSaveRequest response = cfetsPrimaryBidManageMapper.selectBidOrderInfo(param);
            response.setIsWin(DictCodeList.是否_否.getValue());
            List<CfetsPrimaryBidManageSaveDetailDto> detailDtos = cfetsPrimaryBidManageMapper.selectBidOrderDtail(param);
            BidResultTotal bidResultTotal = new BidResultTotal();
            bidResultTotal.setTotalWinId(IDGenerator.generate());
            BeanUtil.copyPropertiesIgnoreNull(bidResultTotal,response);
            bidResultTotalMapper.insertSelective(bidResultTotal);
            BidOrder record = new BidOrder();
            record.setBidId(request.getBidId());
            record.setIsWin(DictCodeList.是否_否.getValue());
            bidOrderMapper.updateByPrimaryKeySelective(record);
            for(CfetsPrimaryBidManageSaveDetailDto detailDto: detailDtos){
                BidResultDetail bidResultDetail = new BidResultDetail();
                BeanUtil.copyPropertiesIgnoreNull(bidResultDetail,response);
                BeanUtil.copyPropertiesIgnoreNull(bidResultDetail,detailDto);
                bidResultDetail.setDetailWinId(IDGenerator.generate());
                bidResultDetailMapper.insertSelective(bidResultDetail);
            }
            ajax.setMessage("未中标信息保存成功！");
        }
        return ajax;
    }
    public CfetsPrimaryBidManageSaveRequest selectBasicInfo(CfetsPrimaryBidManageSaveRequest request) {
        if (StringUtil.isNotEmpty(request.getTotalWinId())) {
            Map<String, Object> param = BeanUtil.object2Map(request);
            CfetsPrimaryBidManageSaveRequest response = cfetsPrimaryBidManageMapper.selectBasicInfo(param);
            List<CfetsPrimaryBidManageSaveDetailDto> detailDtos = cfetsPrimaryBidManageMapper.selectBasicDtail(param);
            response.setAllocationDetails(detailDtos);
        }
        return null;
    }

    public BidOrder getBidOrder(String bidId){
        BidOrder bidOrder = bidOrderMapper.selectByPrimaryKey(bidId);
        return bidOrder;
    }

    /**
     * 工作流（留痕操作结束的时候执行的方法）
     */
    @Override
    public void doComplete(CfetsPrimaryBidManageSaveRequest entity, OperationType type) {
        String action = StringUtil.EMPTY;
        if (OperationType.REVOKE.equals(type)) {
            action = OperationType.REVOKE.name();
            BidResultTotal bidResultTotal = new BidResultTotal();
            bidResultTotal.setTotalWinId(entity.getTotalWinId());
            bidResultTotal.setStatus(DictCodeListExt1.有效状态_撤销.getValue());
            bidResultTotalMapper.updateByPrimaryKey(bidResultTotal);
            if(StringUtil.equals(entity.getIsWin(),DictCodeList.是否_是.getValue())) {
                List<TrdFinProductDealTotal> trdFinProductDealTotalList = cfetsPrimaryBidManageMapper.getTrdFinProductDealTotal(entity.getTotalWinId());
                for (TrdFinProductDealTotal trdFinProductDealTotal : trdFinProductDealTotalList) {
                    TrdFinProductDealTotal trdFinProductDealTotalRevoke = new TrdFinProductDealTotal();
                    trdFinProductDealTotalRevoke.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
                    trdFinProductDealTotalRevoke.setIsCancel(DictCodeList.是否_是.getValue());
                    trdFinProductDealTotalMapper.updateByPrimaryKeySelective(trdFinProductDealTotalRevoke);
                    trdFinProductDealTotal.setpTradeId(trdFinProductDealTotal.getTotleTradeId());
                    trdFinProductDealTotal.setTotleTradeId(
                            trdFinProductDealTotal.getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    TrdFinProductDealTotal trdFinProductDealTotalR = trdFinProductDealTotalMapper.selectByPrimaryKey(trdFinProductDealTotal.getTotleTradeId());
                    if (StringUtil.isNotEmpty(trdFinProductDealTotalR)) {
                        trdFinProductDealTotalMapper.updateByPrimaryKey(trdFinProductDealTotal);
                    } else {
                        trdFinProductDealTotalMapper.insert(trdFinProductDealTotal);
                    }
                    // 根据汇总交易编号查询金融产品交易
                    List<TrdProductDeal> trdProductDealList = cfetsPrimaryBidManageMapper.queryTrdProductDealByTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
                    if (!trdProductDealList.isEmpty()) {
                        for (TrdProductDeal trdProductDeal : trdProductDealList) {
                            /**金融产品交易表**/
                            TrdProductDeal trdProductDealRevoke = new TrdProductDeal();
                            trdProductDealRevoke.setTradeId(trdProductDeal.getTradeId());
                            trdProductDealRevoke.setIsCancel(DictCodeList.是否_是.getValue());
                            trdProductDealMapper.updateByPrimaryKeySelective(trdProductDealRevoke);

                            trdProductDeal.setpTradeId(trdProductDeal.getTradeId());
                            trdProductDeal.setTradeId(trdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                            trdProductDeal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
                            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                            trdProductDealMapper.insert(trdProductDeal);
                            /**交易附属表 **/
                            TrdProductDealAdd trdProductDealAdd = trdProductDealAddMapper.selectByPrimaryKey(trdProductDeal.getTradeId());
                            if (trdProductDealAdd != null) {
                                trdProductDealAdd.setTradeId(trdProductDealAdd.getTradeId()
                                        + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                                trdProductDealAddMapper.insert(trdProductDealAdd);
                            }
                            // 组合交易表
                            List<PtlTrade> ptlTradeList = cfetsPrimaryBidManageMapper.queryPtlTradeByTradeId(trdProductDeal.getTradeId());
                            if (!ptlTradeList.isEmpty()) {
                                for (PtlTrade ptlTrade : ptlTradeList) {
                                    ptlTrade.setBusiNo(ptlTrade.getBusiNo() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                                    ptlTrade.setCreateUser(entity.getCreateUser());
                                    ptlTrade.setCreateTime(new Date());
                                    ptlTrade.setUpdateUser(entity.getCreateUser());
                                    ptlTrade.setUpdateTime(new Date());
                                    ptlTradeMapper.insert(ptlTrade);
                                }
                            }
                        }

                    }
                    // 划款指令表
                    List<StlOrders> stlPayOrdersList = cfetsPrimaryBidManageMapper.queryStlPayOrdersByTradeId(trdFinProductDealTotal.getTotleTradeId());
                    if (!stlPayOrdersList.isEmpty()) {
                        for (StlOrders stlOrders : stlPayOrdersList) {
                            stlOrders.setOrderId(stlOrders.getOrderId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                            stlOrders.setpOrderId(stlOrders.getOrderId());
                            stlOrders.setTradeId(trdFinProductDealTotal.getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                            stlOrders.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                            stlOrdersMapper.insert(stlOrders);
                        }
                    }
                }
            }
            updateCfetsOprHis(entity.getTotalWinId(),action);
        } else {
            action = OperationType.NEW.name();
            if(StringUtil.equals(entity.getIsWin(),DictCodeList.是否_是.getValue())) {
                entity = cncapBaseBidManageBaseInfoDto(entity);
                doCompleteInsert(entity);
            }else{
                unBidSave(entity);
            }
            updateCfetsOprHis(entity.getTotalWinId(),action);
        }
    }

    private void doCompleteInsert(CfetsPrimaryBidManageSaveRequest entity) {
        BaseBidManageBaseInfoDto dto = entity.getBaseBidManageBaseInfoDto();
        //中标结果汇总表
        BidResultTotal bidResultTotal = dto.getBidResultTotal();
        wflBidResultTotalMapper.deleteByPrimaryKey(bidResultTotal.getTotalWinId());
        wflBidResultDetailMapper.deleteByTotalWinId(bidResultTotal.getTotalWinId());
        BidOrder record = new BidOrder();
        record.setBidId(bidResultTotal.getBidId());
        record.setIsWin(bidResultTotal.getIsWin());
        bidOrderMapper.updateByPrimaryKeySelective(record);
        bidResultTotal.setStatus(DictCodeListExt1.有效状态_有效.getValue());
        bidResultTotalMapper.insertSelective(bidResultTotal);
        //中标结果明细表
        for(BidResultDetail bidResultDetail: dto.getBidResultDetailList()) {
            bidResultDetail.setStatus(DictCodeListExt1.有效状态_有效.getValue());
            bidResultDetailMapper.insertSelective(bidResultDetail);
        }
        //金融产品交易汇总表
        for(TrdFinProductDealTotal trdFinProductDealTotal: dto.getTrdFinProductDealTotalList()) {
            trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
        }
        //金融产品交易表
        for(TrdProductDeal trdProductDeal: dto.getTrdProductDealList()) {
            trdProductDealMapper.insertSelective(trdProductDeal);
        }
        //金融产品交易附属表
        for(TrdProductDealAdd trdProductDealAdd: dto.getTrdProductDealAddList()) {
            trdProductDealAddMapper.insertSelective(trdProductDealAdd);
        }
        //汇总交易费用表
        for(TrdTradeFeeTotal trdTradeFeeTotal: dto.getTrdTradeFeeTotalList()) {
            trdTradeFeeTotalMapper.insertSelective(trdTradeFeeTotal);
        }
        //汇总交易费用明细表
        for(TrdTradeFeeDetail trdTradeFeeDetail: dto.getTrdTradeFeeDetailList()) {
            trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
        }
        //组合交易表
        for(PtlTrade ptlTrade: dto.getPtlTradeList()) {
            ptlTradeMapper.insertSelective(ptlTrade);
        }
        //委托完成状况表
        for(EtrFinishStatus etrFinishStatus: dto.getEtrFinishStatusList()) {
            etrFinishStatusMapper.insertSelective(etrFinishStatus);
        }
        //结算指令表
        for(StlOrders stlOrders: dto.getStlOrdersList()) {
            stlOrdersMapper.insertSelective(stlOrders);
        }
    }

    /**
     * 处理履历
     */
    public void updateCfetsOprHis(String entrustId, String optType) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(entrustId);
        if (OperationType.NEW.name().equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_中标结果维护.getValue());
        } else if (OperationType.REVOKE.equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_中标结果撤销.getValue());
        }
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

    private CfetsPrimaryBidManageSaveRequest cncapBaseBidManageBaseInfoDto(CfetsPrimaryBidManageSaveRequest dto){
        Long branch = StringUtil.isEmpty(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)) ? 0 : Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        BaseBidManageBaseInfoDto baseBidManageBaseInfoDto = new BaseBidManageBaseInfoDto();
        //中标结果汇总
        BidResultTotal bidResultTotal = new BidResultTotal();
        BeanUtil.copyPropertiesIgnoreNull(bidResultTotal,dto);
        bidResultTotal.setWinDate(iplatformDate.getSysDate());
        bidResultTotal.setStatus(DictCodeListExt1.有效状态_有效.getValue());
        //中标结果明细表
        List<BidResultDetail> bidResultDetailList = new ArrayList<>();
        for (CfetsPrimaryBidManageSaveDetailDto detailDto:dto.getAllocationDetails()) {
            BidResultDetail bidResultDetail = new BidResultDetail();
            BeanUtil.copyPropertiesIgnoreNull(bidResultDetail,detailDto);
            bidResultDetail.setDetailWinId(IDGenerator.generate());
            bidResultDetail.setTotalWinId(dto.getTotalWinId());
            bidResultDetail.setStatus(DictCodeListExt1.有效状态_有效.getValue());
            bidResultDetailList.add(bidResultDetail);
        }
        Map<String, List<CfetsPrimaryBidManageSaveDetailDto>> allocationListMap = dto.getAllocationDetails().stream().collect(JoyinCollectors.groupingBy(item -> item.getSecAcctId()));
        //金融产品交易汇总表
        List<TrdFinProductDealTotal> trdFinProductDealTotalList = new ArrayList<>();
        //汇总交易费用明细表
        List<TrdTradeFeeTotal> trdTradeFeeTotalList = new ArrayList<>();
        List<StlOrders> stlOrdersList = new ArrayList<>();
        Map<String,String> secAcctIdTotleTradeId = new HashMap<>();
        for (String secAcctId:allocationListMap.keySet()) {
            TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
            trdFinProductDealTotal.setTotleTradeId((baseGetBusinessId != null && baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) != null) ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) : IDGenerator.generate().substring(0, 30));
            trdFinProductDealTotal.setSecManageAcctId(secAcctId);
            secAcctIdTotleTradeId.put(secAcctId,trdFinProductDealTotal.getTotleTradeId());
            trdFinProductDealTotal.setFinprodId(dto.getFinprodId());
            trdFinProductDealTotal.setFinprodType2(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
            trdFinProductDealTotal.setBusiType(BussinessDictCodeList.共同业务类型_中标结果维护.getValue());
            trdFinProductDealTotal.setBranch(branch);
            trdFinProductDealTotal.setTradeDate(iplatformDate.getSysDate());
            trdFinProductDealTotal.setSettleDate(dto.getCreditorDebtDate());
            trdFinProductDealTotal.setCcy(dto.getCcy());
            BigDecimal unitCprice = null;
            BigDecimal unitInt = null;
            BigDecimal winPrinamt = null;
            BigDecimal intAmt = null;
            BigDecimal fpriceAmt = null;
            BigDecimal settFeeAmt= null;
            for (CfetsPrimaryBidManageSaveDetailDto detailDto:allocationListMap.get(secAcctId)){
                unitCprice = detailDto.getWinCprice();
                unitInt = detailDto.getIntAmt();
                winPrinamt = ArithmeticUtil.doAdd(winPrinamt,detailDto.getTheWinPrinamt());
                intAmt= ArithmeticUtil.doAdd(intAmt,detailDto.getIntAmt());
                settFeeAmt= ArithmeticUtil.doAdd(settFeeAmt,detailDto.getSettFeeAmt());
            }
            if (StringUtil.equals(dto.getTenderTarget(),DictCodeListExt4.招标标的_价格.getValue())) {
                trdFinProductDealTotal.setUnitCprice(unitCprice);
            }
            trdFinProductDealTotal.setUnitInt(unitInt);
            trdFinProductDealTotal.setParValue(BigDecimal.valueOf(100));
            CfetsBidManageCalcAmountRequest request = new CfetsBidManageCalcAmountRequest();
            request.setWinPrinamt(winPrinamt);
            request.setAllocationDetails(allocationListMap.get(secAcctId));
            CfetsBidManageCalcAmountResponse calAmountResponse = this.calAmountData(request);
            trdFinProductDealTotal.setCpriceAmt(calAmountResponse.getTradeAmt());
            trdFinProductDealTotal.setShareAmt(ArithmeticUtil.multiply(winPrinamt,BigDecimal.valueOf(100)));
            trdFinProductDealTotal.setPrinAmt(ArithmeticUtil.multiply(winPrinamt,BigDecimal.valueOf(10000)));
            trdFinProductDealTotal.setIntAmt(intAmt);
            //画面.金额信息.交易金额（元）+画面.金额信息.应计利息总额（元）
            trdFinProductDealTotal.setFpriceAmt(ArithmeticUtil.doAdd(calAmountResponse.getTradeAmt(),calAmountResponse.getEntrustOwnInt()));
            trdFinProductDealTotal.setFeeAmtPay(settFeeAmt);
            trdFinProductDealTotal.setTradeAmt(calAmountResponse.getPayAmt());
            trdFinProductDealTotal.setFeeAmtUnpay(settFeeAmt);
            trdFinProductDealTotal.setFeeAmt(settFeeAmt);
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_买入.getValue());
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            trdFinProductDealTotal.setIsOutTrade(DictCodeList.是否_是.getValue());
            trdFinProductDealTotal.setTradeMarket(dto.getTradeMarket());
            if(StringUtil.equals(dto.getTradeMarket(),DictCodeListExt3.交易场所_银行间.getValue())
            || StringUtil.equals(dto.getTradeMarket(),DictCodeListExt3.交易场所_商业银行柜台.getValue())){
                trdFinProductDealTotal.setTradePlat(DictCodeList.交易平台_外汇交易中心.getValue());
            }else{
                trdFinProductDealTotal.setTradePlat("99");
            }
            trdFinProductDealTotal.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue());
            trdFinProductDealTotal.setTrader(dto.getOuTraderId());
//            参数字典.平安业务类型.中标结果维护
            trdFinProductDealTotal.setTgBusiType("151");
            trdFinProductDealTotal.setbTradeId(dto.getTotalWinId());
            trdFinProductDealTotal.setCounterId(dto.getCounterId());
            trdFinProductDealTotal.setRemark(dto.getRemark());
            trdFinProductDealTotalList.add(trdFinProductDealTotal);
            //汇总交易费用明细表
            TrdTradeFeeTotal trdTradeFeeTotal = new TrdTradeFeeTotal();
            trdTradeFeeTotal.setFeeType(BussinessDictCodeList.费用类型_结算费.getValue());
            trdTradeFeeTotal.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            trdTradeFeeTotal.setFeeAmt(settFeeAmt);
            trdTradeFeeTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
            trdTradeFeeTotalList.add(trdTradeFeeTotal);
            //结算指令表
            StlOrders stlOrders = new StlOrders();
            BussinessCodeParameter bussinessCodeParameter2 = new BussinessCodeParameter();
            bussinessCodeParameter2.setSeqNo("9999");
            String seqno = baseGetBusinessId !=null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameter2):IDGenerator.generate();
            stlOrders.setOrderId(seqno);
            stlOrders.setBusiType(trdFinProductDealTotal.getTgBusiType());
            stlOrders.setSettleDate(trdFinProductDealTotal.getSettleDate());
            stlOrders.setTradeId(trdFinProductDealTotal.getTotleTradeId());
            StlSecAcctInfo stlSecAcctInfo = stlSecAcctInfoMapper.selectByPrimaryKey(secAcctId);
            if (StringUtil.isNotEmpty(stlSecAcctInfo)) {
                stlOrders.setMyAcctNo(stlSecAcctInfo.getTrdAcctId());
            }
            stlOrders.setPs("02");
            List<StlCapacctInfo> stlCapacctInfos = cfetsPrimaryBidManageMapper.selectCounterCapacctInfoList(dto.getCounterId(), null, dto.getCcy());
            if (CollectionUtils.isNotEmpty(stlCapacctInfos)) {
               stlOrders.setCounterAcctNo(stlCapacctInfos.get(0).getAcctNo());
            }
            stlOrders.setSettleAmt(dto.getPayAmt());
            stlOrders.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            stlOrders.setSettleStatus(DictCodeList.结算状态_已结算.getValue());
            stlOrders.setOrderStatus(DictCodeListExt1.交易状态_初始.getValue());
            stlOrdersList.add(stlOrders);
        }
        //金融产品交易表
        List<TrdProductDeal> trdProductDealList = new ArrayList<>();
        //金融产品交易表
        List<TrdProductDealAdd> trdProductDealAddList = new ArrayList<>();
        //交易费用明细表
        List<TrdTradeFeeDetail> trdTradeFeeDetailList = new ArrayList<>();
        //委托完成状况表
        List<EtrFinishStatus> etrFinishStatusList = new ArrayList<>();
        //组合交易表
        List<PtlTrade> ptlTradeList = new ArrayList<>();
        Map<String, List<CfetsPrimaryBidManageSaveDetailDto>> allocationListTwoMap = dto.getAllocationDetails().stream()
                .collect(JoyinCollectors.groupingBy(item -> item.getSecAcctId() + GlobalConstant.COMMA + item.getEntrustId() + GlobalConstant.COMMA + item.getPortfolioId()));
        for (String allocation:allocationListTwoMap.keySet()) {
            List<CfetsPrimaryBidManageSaveDetailDto> detailDtos = allocationListTwoMap.get(allocation);
            List<String> allocationList = StringUtil.splitToList(GlobalConstant.COMMA, allocation);
            String secAcctId = allocationList.get(0);
            String entrustId = allocationList.get(1);
            String portfolioId = allocationList.get(2);
            //金融产品交易表
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
            bussinessCodeParameter.setType1(DictCodeList.业务编号一级分类_同业借款);
            trdProductDeal.setTradeId((baseGetBusinessId != null && baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) != null)? baseGetBusinessId.buildBussinessCode(bussinessCodeParameter): IDGenerator.generate().substring(0, 30));
            trdProductDeal.setSecManageAcctId(secAcctId);
            trdProductDeal.setFinprodId(dto.getFinprodId());
            trdProductDeal.setFinprodType2(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
            trdProductDeal.setBranch(branch);
            trdProductDeal.setTradeDate(iplatformDate.getSysDate());
            trdProductDeal.setSettleDate(dto.getCreditorDebtDate());
            trdProductDeal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            BigDecimal cpriceAmt =null;
            BigDecimal winPrinamt =null;
            BigDecimal intAmt =null;
            BigDecimal fpriceAmt = null;
            BigDecimal settFeeAmt = null;
            String invAim = null;
            for(CfetsPrimaryBidManageSaveDetailDto detailDto: detailDtos) {
                cpriceAmt = ArithmeticUtil.doAdd(cpriceAmt,detailDto.getTradeAmt());
                winPrinamt = ArithmeticUtil.doAdd(winPrinamt,detailDto.getTheWinPrinamt());
                intAmt = ArithmeticUtil.doAdd(intAmt,detailDto.getIntAmt());
                fpriceAmt = ArithmeticUtil.doAdd(fpriceAmt,detailDto.getPayAmt());
                settFeeAmt= ArithmeticUtil.doAdd(settFeeAmt,detailDto.getSettFeeAmt());
                invAim = detailDto.getInvAim();
            }
            trdProductDeal.setCpriceAmt(cpriceAmt);
            trdProductDeal.setShareAmt(ArithmeticUtil.multiply(winPrinamt,BigDecimal.valueOf(100)));
            trdProductDeal.setPrinAmt(ArithmeticUtil.multiply(winPrinamt,BigDecimal.valueOf(10000)));
            trdProductDeal.setIntAmt(intAmt);
            trdProductDeal.setFpriceAmt(fpriceAmt);
            trdProductDeal.setTradeAmt(fpriceAmt);
            trdProductDeal.setFeeAmtUnpay(settFeeAmt);
            trdProductDeal.setFeeAmt(settFeeAmt);
            trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
            trdProductDeal.setpTradeId(secAcctIdTotleTradeId.get(secAcctId));
            trdProductDeal.setTradeMarket(dto.getTradeMarket());
            if(StringUtil.equals(dto.getTradeMarket(),DictCodeListExt3.交易场所_银行间.getValue())
                    || StringUtil.equals(dto.getTradeMarket(),DictCodeListExt3.交易场所_商业银行柜台.getValue())){
                trdProductDeal.setTradePlat(DictCodeList.交易平台_外汇交易中心.getValue());
            }else{
                trdProductDeal.setTradePlat("99");
            }
            trdProductDeal.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue());
//            参数字典.平安业务类型.中标结果维护
            trdProductDeal.setTgBusiType("151");
            trdProductDeal.setbTradeId(dto.getTotalWinId());
            trdProductDeal.setCounterId(dto.getCounterId());
            trdProductDeal.setRemark(dto.getRemark());
            trdProductDeal.setInvAim(invAim);
            trdProductDealList.add(trdProductDeal);
            //金融产品交易附属表
            TrdProductDealAdd  trdProductDealAdd = new TrdProductDealAdd();
            trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
            trdProductDealAdd.setPortfolioId(portfolioId);
            trdProductDealAdd.setEntrustId(entrustId);
            trdProductDealAddList.add(trdProductDealAdd);
            //组合交易
            PtlTrade  ptlTrade = new PtlTrade();
            ptlTrade.setBusiNo(trdProductDeal.getTradeId());
            ptlTrade.setPortfolioId(portfolioId);
            ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade.setBusiId(dto.getFinprodId());
            ptlTrade.setInvAim(invAim);
            ptlTrade.setSecManageAcctId(secAcctId);
            ptlTrade.setSettleDate(trdProductDeal.getSettleDate());
            ptlTradeList.add(ptlTrade);
            //交易费用明细表
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
            trdTradeFeeDetail.setFeeType(BussinessDictCodeList.费用类型_结算费.getValue());
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            trdTradeFeeDetail.setFeeAmt(settFeeAmt);
            //委托完成状况表
            EtrFinishStatus  etrFinishStatus = new EtrFinishStatus();
            etrFinishStatus.setEntrustId(entrustId);
            etrFinishStatus.setTradeId(secAcctIdTotleTradeId.get(secAcctId));
            etrFinishStatus.setFinishDate(iplatformDate.getSysDate());
            etrFinishStatus.setTrader(dto.getOuTraderId());
            etrFinishStatus.setTranFinishAmt(trdProductDeal.getPrinAmt());
            etrFinishStatusList.add(etrFinishStatus);
        }
        baseBidManageBaseInfoDto.setBidResultTotal(bidResultTotal);
        baseBidManageBaseInfoDto.setBidResultDetailList(bidResultDetailList);
        baseBidManageBaseInfoDto.setTrdFinProductDealTotalList(trdFinProductDealTotalList);
        baseBidManageBaseInfoDto.setTrdProductDealList(trdProductDealList);
        baseBidManageBaseInfoDto.setTrdProductDealAddList(trdProductDealAddList);
        baseBidManageBaseInfoDto.setTrdTradeFeeTotalList(trdTradeFeeTotalList);
        baseBidManageBaseInfoDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);
        baseBidManageBaseInfoDto.setPtlTradeList(ptlTradeList);
        baseBidManageBaseInfoDto.setEtrFinishStatusList(etrFinishStatusList);
        baseBidManageBaseInfoDto.setStlOrdersList(stlOrdersList);
        dto.setBaseBidManageBaseInfoDto(baseBidManageBaseInfoDto);
        return dto;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doInsert(CfetsPrimaryBidManageSaveRequest entity, OperationType type) {
        if (type.toString().equals("NEW") && entity.getOperateType().equals("submit")) {
            if(!StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.WFL_AUTO), "02")){
                beforeWorkFlow(entity);
            }
        }
        else if (type.toString().equals("REVOKE")) {
            entity.setOperateType("revoke");
            beforeWorkFlow(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(CfetsPrimaryBidManageSaveRequest entity) {
        if (entity.getOperateType().equals("submit")) {
            doDelete(entity.getWorkflowId());
            beforeWorkFlow(entity);
        }
    }

    /**
     * 作废操作,清空业务表 {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        wflBidResultTotalMapper.deleteByWorkflowId(workflowId);
        wflBidResultDetailMapper.deleteByWorkflowId(workflowId);
    }

    public CfetsPrimaryBidManageSaveRequest getBasicInfos(String bidId, String bidDate) {
        Map<String,Object> param=new HashMap<>();
        CfetsPrimaryBidManageSaveRequest request = cfetsPrimaryBidManageMapper.selectBasicInfo(param);
        return request;
    }

    /**
     * 流程中数据处理
     *
     * @param request  request
     * @see
     * @since 1.0
     */
    public void beforeWorkFlow(CfetsPrimaryBidManageSaveRequest request) {
        if (request.getOperateType().equals("revoke")) {
            List<CfetsPrimaryBidManageSaveRequest> selectedItem = request.getSelectedItem();
            for (CfetsPrimaryBidManageSaveRequest dto : selectedItem) {
                WflBidResultTotal wflBidResultTotal = new WflBidResultTotal();
                BeanUtil.copyPropertiesIgnoreNull(wflBidResultTotal, dto);
                wflBidResultTotal.setStatus(DictCodeListExt1.有效状态_撤销.getValue());
                wflBidResultTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflBidResultTotal.setWorkflowId(dto.getWorkflowId());
                wflBidResultTotal.setWinDate(iplatformDate.getSysDate());
//                wflBidResultTotal.setpTotalWinId(wflBidResultTotal.getTotalWinId());
                wflBidResultTotal.setTotalWinId(wflBidResultTotal.getTotalWinId() + "R");
                wflBidResultTotalMapper.insertSelective(wflBidResultTotal);
                if (dto.getAllocationDetails() != null && dto.getAllocationDetails().size() > 0) {
                    for (CfetsPrimaryBidManageSaveDetailDto detail : dto.getAllocationDetails()) {
                        WflBidResultDetail wflBidResultDetail = new WflBidResultDetail();
                        BeanUtil.copyPropertiesIgnoreNull(wflBidResultDetail, detail);
                        wflBidResultDetail.setDetailWinId(IDGenerator.generate());
                        wflBidResultDetail.setWorkflowId(dto.getWorkflowId());
                        wflBidResultDetail.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                        wflBidResultDetail.setTotalWinId(dto.getTotalWinId() + "R");
                        wflBidResultDetail.setStatus(DictCodeListExt1.有效状态_撤销.getValue());
                        wflBidResultDetailMapper.insertSelective(wflBidResultDetail);
                    }
                }
            }
        }
        else {
            WflBidResultTotal wflBidResultTotal = new WflBidResultTotal();
            BeanUtil.copyPropertiesIgnoreNull(wflBidResultTotal, request);
            wflBidResultTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflBidResultTotal.setWorkflowId(request.getWorkflowId());
            wflBidResultTotal.setWinDate(iplatformDate.getSysDate());
            wflBidResultTotal.setStatus(DictCodeListExt1.有效状态_流程中.getValue());
            wflBidResultTotalMapper.insertSelective(wflBidResultTotal);
            if (request.getAllocationDetails() != null && request.getAllocationDetails().size() > 0) {
                for (CfetsPrimaryBidManageSaveDetailDto detail : request.getAllocationDetails()) {
                    WflBidResultDetail wflBidResultDetail = new WflBidResultDetail();
                    BeanUtil.copyPropertiesIgnoreNull(wflBidResultDetail, detail);
                    wflBidResultTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflBidResultDetail.setWorkflowId(request.getWorkflowId());
                    wflBidResultDetail.setDetailWinId(IDGenerator.generate());
                    wflBidResultDetail.setTotalWinId(request.getTotalWinId());
                    wflBidResultDetail.setStatus(DictCodeListExt1.有效状态_流程中.getValue());
                    wflBidResultDetailMapper.insertSelective(wflBidResultDetail);
                }
            }
        }
    }

    /**
     *
     * 	2.2.1、更新中标标位
     *
     * 	2.2.2、更新理论中标券面
     *
     * 	2.2.3、更新中标净价、缴款金额、结算服务费
     */
    public CfetsPrimaryBidManageSaveRequest autoAllocation(CfetsPrimaryBidManageSaveRequest request) {
        if (CollectionUtils.isNotEmpty(request.getAllocationDetails())){
            //更新中标标位
            BigDecimal bidMinUnit = new BigDecimal(CacheUtil.getSysParam("BID_MIN_UNIT"));
            String isModifiedIntamt = CacheUtil.getSysParam("IS_MODIFIED_INTAMT");
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(request.getFinprodId());
            if (null != request.getAllocationDetails().get(0).getBidType()
            && StringUtil.equals(request.getAllocationDetails().get(0).getBidType(), DictCodeListExt4.中标方式_单标位中标.getValue())){
                if (StringUtil.equals(request.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue()) ||
                        StringUtil.equals(request.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())){
                    for (CfetsPrimaryBidManageSaveDetailDto detailDto : request.getAllocationDetails()){
                        if (detailDto.getBidPrice().compareTo(request.getWinPrice())<=0){
                            detailDto.setWinPrice(request.getWinPrice());
                        }
                    }
                }else if (StringUtil.equals(request.getTenderTarget(), DictCodeListExt4.招标标的_价格.getValue())){
                    for (CfetsPrimaryBidManageSaveDetailDto detailDto : request.getAllocationDetails()){
                        if (detailDto.getBidPrice().compareTo(request.getWinPrice())>=0){
                            detailDto.setWinPrice(request.getWinPrice());
                        }
                    }
                }
            }else{
                if (StringUtil.equals(request.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue()) ||
                        StringUtil.equals(request.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())){
                    for (CfetsPrimaryBidManageSaveDetailDto detailDto : request.getAllocationDetails()){
                        if (detailDto.getBidPrice().compareTo(request.getWinPrice())<=0){
                            detailDto.setWinPrice(detailDto.getBidPrice());
                        }
                    }
                }else if (StringUtil.equals(request.getTenderTarget(), DictCodeListExt4.招标标的_价格.getValue())){
                    for (CfetsPrimaryBidManageSaveDetailDto detailDto : request.getAllocationDetails()){
                        if (detailDto.getBidPrice().compareTo(request.getWinPrice())>=0){
                            detailDto.setWinPrice(detailDto.getBidPrice());
                        }
                    }
                }
            }
            //更新理论中标券面
           BigDecimal remainWinPrinamt = request.getWinPrinamt();
           List<CfetsPrimaryBidManageSaveDetailDto> allocationDetails = request.getAllocationDetails();
           if (StringUtil.equals(request.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue()) ||
                    StringUtil.equals(request.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())){
               allocationDetails = allocationDetails.stream().filter(item->StringUtil.isNotEmpty(item.getWinPrice())).sorted(Comparator.comparing(CfetsPrimaryBidManageSaveDetailDto::getBidPrice)
                       .thenComparing(CfetsPrimaryBidManageSaveDetailDto::getEntrustId).thenComparing(CfetsPrimaryBidManageSaveDetailDto::getPortfolioId)).collect(Collectors.toList());
           }else if (StringUtil.equals(request.getTenderTarget(), DictCodeListExt4.招标标的_价格.getValue())){
               allocationDetails = allocationDetails.stream().filter(item->StringUtil.isNotEmpty(item.getWinPrice())).sorted(Comparator.comparing(CfetsPrimaryBidManageSaveDetailDto::getBidPrice).reversed()
                       .thenComparing(CfetsPrimaryBidManageSaveDetailDto::getEntrustId).thenComparing(CfetsPrimaryBidManageSaveDetailDto::getPortfolioId)).collect(Collectors.toList());
            }
           for (CfetsPrimaryBidManageSaveDetailDto detailDto:allocationDetails) {
                if (detailDto.getTotalBidPrinamt().compareTo(remainWinPrinamt)<=0){
                    detailDto.setTheWinPrinamt(detailDto.getTotalBidPrinamt());
                    detailDto.setActWinPrinamt(detailDto.getTotalBidPrinamt());
                }else if(remainWinPrinamt.compareTo(bidMinUnit)>=0){
                    BigDecimal winPrinamt =  ArithmeticUtil.subtract(remainWinPrinamt,remainWinPrinamt.divideAndRemainder(bidMinUnit)[1]);
                    detailDto.setTheWinPrinamt(winPrinamt);
                    detailDto.setActWinPrinamt(winPrinamt);
                }else{
                    //中标标位、中标净价、应计利息、缴款金额、结算服务费
                    detailDto.setWinPrice(null);
                    detailDto.setWinCprice(null);
                    detailDto.setIntAmt(null);
                    detailDto.setPayAmt(null);
                    detailDto.setSettFeeAmt(null);
                }
                remainWinPrinamt = ArithmeticUtil.subtract(remainWinPrinamt,detailDto.getTheWinPrinamt());
            }
            allocationDetails = allocationDetails.stream().sorted(Comparator.comparing(CfetsPrimaryBidManageSaveDetailDto::getTheWinPrinamt).reversed()).collect(Collectors.toList());
            allocationDetails.get(0).setTheWinPrinamt(ArithmeticUtil.doAdd(allocationDetails.get(0).getTheWinPrinamt(),remainWinPrinamt));
            allocationDetails.get(0).setActWinPrinamt(ArithmeticUtil.doAdd(allocationDetails.get(0).getActWinPrinamt(),remainWinPrinamt));
            //更新中标净价、缴款金额、结算服务费
            for (CfetsPrimaryBidManageSaveDetailDto detailDto:request.getAllocationDetails()) {
                 if (StringUtil.isNotEmpty(detailDto.getWinPrice())){
                     if (StringUtil.equals(request.getTenderTarget(), DictCodeListExt4.招标标的_利率.getValue()) ||
                             StringUtil.equals(request.getTenderTarget(), DictCodeListExt4.招标标的_利差.getValue())){
                         if(StringUtil.isNotEmpty(finProduct.getCirculationCode())){
                             //TODO
                             Map<String,Object> param = new HashMap<>();
                             param.put("finprodId",request.getFinprodId());
                             param.put("tradeMarket",request.getTradeMarket());
                             param.put("ps",DictCodeList.交易方向_买入.getValue());
                             param.put("calculateField",DictCodeListExt3.债券计算模式_到期收益率.getValue());
                             param.put("yield",detailDto.getWinPrice());
                             Map<String, BigDecimal> calcPriceAndYieldMap = calcPriceAndYield(param);
                             if (StringUtil.isNotEmpty(calcPriceAndYieldMap)) {
                                 detailDto.setWinCprice(calcPriceAndYieldMap.get("cprice"));
                             }
                         }else if(StringUtil.equals(finProduct.getCouponSpecies(), DictCodeList.息票品种_附息.getValue())
                         || StringUtil.equals(finProduct.getCouponSpecies(), DictCodeList.息票品种_利随本清.getValue())){
                             detailDto.setWinCprice(BigDecimal.valueOf(100));
                         }
                     }else if (StringUtil.equals(request.getTenderTarget(), DictCodeListExt4.招标标的_价格.getValue())){
                         detailDto.setWinCprice(detailDto.getWinPrice());
                     }
                 }
            }
        }
        return null;
    }

    public FinProductIssue getFinProductIssueDto(String finprodId) {
        FinProductIssue finProductIssue = finProductIssueMapper.selectByPrimaryKey(finprodId);
        return finProductIssue;
    }

    public boolean isInWflProcess(WflValidInputParameter parameter) {
        parameter.setSeachMode("01");
        List<String> result = cfetsPrimaryBidManageMapper.getBusinessData(parameter);
        if (CollectionUtils.isNotEmpty(result)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public BidResultTotal getBidResultTotal(String totalWinId) {
        return bidResultTotalMapper.selectByPrimaryKey(totalWinId);
    }


    /**
     *
     * 	2.3、应计利息离开事件联动计算
     * 		if 系统参数.是否可修改应计利息 = 是 可编辑 【缴款金额】=（中标净价+应计利息）*实际中标券面总额/100
     * 			联动修改：分配明细中中标标位不为空的记录.应计利息
     * 			结算服务费按分配明细中中标记录平均分配，尾差处理同中标券面总额
     * 			联动计算：缴款金额、应计利息总额 其他计算见需求
     * 	2.4、中标净价离开事件联动计算
     * 		联动计算缴款金额
     * 	2.5、缴款金额离开事件联动计算
     * 		反算中标净价
     * @param dto
     * @see
     * @since 1.0
     */
    public void calcAmt(CfetsPrimaryBidManageSaveRequest dto) {
        String isModifiedIntamt = CacheUtil.getSysParam("IS_MODIFIED_INTAMT");
        if (StringUtil.equals(isModifiedIntamt,DictCodeList.是否_是.getValue())){
            for (CfetsPrimaryBidManageSaveDetailDto detailDto:dto.getAllocationDetails()) {
                if (StringUtil.equals(isModifiedIntamt,DictCodeList.是否_是.getValue())){
                    detailDto.setPayAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(ArithmeticUtil.doAdd(detailDto.getWinCprice(),detailDto.getIntAmt()),detailDto.getActWinPrinamt()),BigDecimal.valueOf(100),4));
                }
            }
        }
    }

    /**
     *
     * 净新建指令金额计算功能联动
     * 当【中标方式】为单标位中标时，默认等于分配明细中的【中标净价】
     * 当【中标方式】为多标位中标时，默认等于分配明细中的【中标净价】的加权平均值
     * 应计利息=分配明细中的应计利息
     * 全价=中标净价+应计利息
     * 交易金额=基本信息中中标券面总额*中标净价/100
     * 应计利息总额=基本信息中中标券面总额*应计利息/100
     * 缴款金额=交易金额+应计利息总额
     * @param dto
     * @see
     * @since 1.0
     */
    public CfetsBidManageCalcAmountResponse calAmountData(CfetsBidManageCalcAmountRequest dto) {
        CfetsBidManageCalcAmountResponse result=new CfetsBidManageCalcAmountResponse();
        BigDecimal intAmt = null;
        BigDecimal winCprice = null;
        if (CollectionUtils.isNotEmpty(dto.getAllocationDetails())
                && StringUtil.equals(dto.getAllocationDetails().get(0).getBidType(),"01")) {
            BigDecimal total = null; // 总净价
            BigDecimal weightTotal = null; // 权重汇总
            for (CfetsPrimaryBidManageSaveDetailDto allocationDetail : dto.getAllocationDetails()) {
                total = ArithmeticUtil.doAdd(total, allocationDetail.getWinCprice());
                weightTotal = ArithmeticUtil.doAdd(weightTotal, ArithmeticUtil.multiply(allocationDetail.getActWinPrinamt(), allocationDetail.getWinCprice()));
                if (StringUtil.isNotEmpty(allocationDetail.getIntAmt())) {
                    intAmt = allocationDetail.getIntAmt();
                }
            }
            winCprice = ArithmeticUtil.divide(weightTotal, total, 4);
        }else if (CollectionUtils.isNotEmpty(dto.getAllocationDetails())){
            winCprice = dto.getAllocationDetails().get(0).getWinCprice();
            intAmt = dto.getAllocationDetails().get(0).getIntAmt();
        }
        //中标净价
        result.setWinCprice(winCprice);
        //应计利息
        intAmt = ArithmeticUtil.round(intAmt,5,RoundingMode.HALF_UP);
        result.setIntAmt(intAmt);
        //交易金额
        result.setTradeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getWinPrinamt(), winCprice),
                new BigDecimal(100), 2));
        //应计利息总额
        result.setEntrustOwnInt(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getWinPrinamt(), intAmt),
                new BigDecimal(100), 2));
        //全价总额
        result.setUnitFpriceAmt(ArithmeticUtil.doAdd(winCprice,intAmt));
        //缴款金额
        result.setPayAmt(ArithmeticUtil.doAdd(result.getTradeAmt(),result.getEntrustOwnInt()));
        return result;
    }

    /**
     * 根据画面的信息计算价格和到期收益率。<br/>
     * 如果以净价为基准，则计算全价和到期收益率<br/>
     * 如果以全价为基准，则计算净价和到期收益率<br/>
     * 如果以到期收益率为基准，则计算净价和全价<br/>
     * 注意：该方法支持返回结果如下：<br/>
     * <table style="border: 1px solid;">
     * <tr>
     * <th style="border: 1px solid;">字段中文</th>
     * <th style="border: 1px solid;">字段英文</th>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">单位应计利息</td>
     * <td style="border: 1px solid;">unitInt</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">应计利息总额</td>
     * <td style="border: 1px solid;">intAmt</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">单位净价</td>
     * <td style="border: 1px solid;">cprice</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">净价总额</td>
     * <td style="border: 1px solid;">cpriceAmt</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">单位全价</td>
     * <td style="border: 1px solid;">fprice</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">全价总额</td>
     * <td style="border: 1px solid;">fprice</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">到期收益率</td>
     * <td style="border: 1px solid;">yield</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">行权收益率</td>
     * <td style="border: 1px solid;">exerYield</td>
     * </tr>
     * </table>
     *
     * @param param
     * @return 计算结果
     * @author dubaojun
     */
    public Map<String, BigDecimal> calcPriceAndYield(Map<String, Object> param) {
        // 获取所有前台取得的参数
        Date settleDate = DateUtil.platDateFromCache();
        String finprodId = (String) param.get("finprodId");
        BigDecimal prinAmt = new BigDecimal(100);
        if (StringUtil.isNotEmpty(param.get("prinAmt"))) {
            prinAmt = new BigDecimal(param.get("prinAmt").toString());
        }
        String tradeMarket = (String) param.get("tradeMarket");
        String ps = (String) param.get("ps");
        String calculateField = (String) param.get("calculateField");
        // 定义需要返回的参数
        BigDecimal unitInt = null;// 单位应计利息
        BigDecimal intAmt = null;// 应计利息总额
        BigDecimal cprice = null;
        if (StringUtil.isNotEmpty(param.get("price"))) {
            cprice = new BigDecimal(param.get("price").toString());// 单位净价
        }
        BigDecimal cpriceAmt = null;// 净价总额
        BigDecimal fprice = null;// 单位全价
        if (StringUtil.isNotEmpty(param.get("fprice"))) {
            fprice = new BigDecimal(param.get("fprice").toString());// 单位全价
        }
        BigDecimal fpriceAmt = null;// 全价总额
        BigDecimal yield = null;// 到期收益率
        if (StringUtil.isNotEmpty(param.get("yield"))) {
            yield = new BigDecimal(param.get("yield").toString()).divide(new BigDecimal(100));// 到期收益率
        }
        BigDecimal exerYield = null;// 行权收益率
        if (StringUtil.isNotEmpty(param.get("exerYield"))) {
            exerYield = new BigDecimal(param.get("exerYield").toString()).divide(new BigDecimal(100));// 行权收益率
        }
        // 如果交易场所为上交所、深交所以外的情况
        boolean isJiaoYiSuo = true;
        if (!StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), tradeMarket)
                && !StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), tradeMarket)) {
            isJiaoYiSuo = false;
        }
        final int SCALE = 12;
        Map<String, Object> tempParamMap = new HashMap<>();
        tempParamMap.put("finprodId", finprodId);
        // b.获取计息区间投资端利率报价信息（债券的利率重置信息）
        param.put("finprodType", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        List<FinCashCalcRule> finCashCalcRuleList = cfetsPrimaryBidManageMapper.selectBondsIntResetList(param);
        // c.获取债券的付息计划信息
        HashMap<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        List<FinCashPlanDto> finCashPlanList = cfetsPrimaryBidManageMapper.getFinCashPlan(map);
        List<SecIntSchedule> invScheduleList = new ArrayList<>();
        List<FinCashPlanDto> intSchList = new ArrayList<FinCashPlanDto>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(), DictCodeListExt1.现金流一级类型_利息.getValue())
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
        tempParamMap.put("ps", ps);
        tempParamMap.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        tempParamMap.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        tempParamMap.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        tempParamMap.put("sysDate", DateUtil.platDateFromCache());
        tempParamMap.put("YES_NO_Y", DictCodeList.是否_是.getValue());
        tempParamMap.put("EXEC_MODE02", DictCodeList.行权方式_回售.getValue());
        tempParamMap.put("EXEC_MODE01", DictCodeList.行权方式_赎回.getValue());
        tempParamMap.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
        tempParamMap.put("psT39", DictCodeList.交易方向_行权_赎回.getValue());
        SecInfoModelDto secInfoModelDto = cfetsPrimaryBidManageMapper.getSecInfoModel(tempParamMap);
        SecInfoModel secInfoModel = toSecInfoModel(secInfoModelDto);
        if (secInfoModelDto != null && secInfoModelDto.getStartDate() != null
                && DateUtil.compareTo(secInfoModelDto.getStartDate(), settleDate) > 0) {
            settleDate = secInfoModelDto.getStartDate();
        }
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
            if (StringUtil.equals(item.getCashType1(), DictCodeListExt1.现金流一级类型_本金.getValue())) {
                SecPrinSchedule schedule = new SecPrinSchedule();
                schedule.setAmount(item.getIntAmt());
                schedule.setFlag(StringUtil.equals(item.getIsPayInt(), DictCodeList.是否_是.getValue()));
                schedule.setPrincipal(item.getPrinAmt());
                schedule.setInterestEndDate(toLocalDate(item.getMdate()));
                secPrinScheduleList.add(schedule);
            }
        }

        List<PaperIrReset> paperIrResetList = finCashCalcRuleList.stream().map(item -> {
            PaperIrReset ret = new PaperIrReset();
            ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            ret.setParRate(item.getYield());
            ret.setParRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            return ret;
        }).collect(Collectors.toList());
        if (paperIrResetList != null && paperIrResetList.size() > 0) {
            paperIrResetList.get(0).setBaseRateEffDate(BasicUtil.dateToLocal(secInfoModelDto.getStartDate()));
        }
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            // 多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        YieldByPriceCalculator yieldByPriceCalculator = new YieldByPriceCalculator();
        PriceByYieldCalculator priceByYieldCalculator = new PriceByYieldCalculator();
        YieldPriceInParameter yieldPriceInParameter = new YieldPriceInParameter();
        yieldPriceInParameter.setCalDate(toLocalDate(settleDate));
        yieldPriceInParameter.setSecInfoModel(secInfoModel);
        yieldPriceInParameter.setSecIntScheduleList(invScheduleList);
        yieldPriceInParameter.setSecPrinScheduleList(secPrinScheduleList);
        yieldPriceInParameter.setPaperIrResetList(paperIrResetList);
        PriceYieldInParameter priceYieldInParameter = new PriceYieldInParameter();
        priceYieldInParameter.setCalDate(toLocalDate(settleDate));
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
        // 交易所债券特殊处理start----------------------------------------------------------------
        Date tempSettleDate = settleDate;
        // 如果交易场所为上交所、深交所的情况
        if (isJiaoYiSuo) {
            // 行权-回售 行权-赎回 交割日不加1
            if (!StringUtil.equals(ps, DictCodeList.交易方向_行权_回售.getValue())
                    && !StringUtil.equals(ps, DictCodeList.交易方向_行权_赎回.getValue())) {
                tempSettleDate = DateUtil.addDay(tempSettleDate, 1);
            }
        }
        // 交易所债券特殊处理end----------------------------------------------------------------
        // a.当前债券面额取得（根据还本计划计算）
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("finprodId", finprodId);
        paraMap.put("settleDate", DateUtil.format(tempSettleDate));
        param.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());
        param.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
//		String settleDate = (String) param.get("settleDate");
        param.put("settleDate", settleDate);
        BigDecimal curSecAmt = cfetsPrimaryBidManageMapper.getCurSecAmt(param);
        AccurIntCalInParameter accurIntCalInParameter = new AccurIntCalInParameter();
        // 计算日
        accurIntCalInParameter.setCalDate(toLocalDate(tempSettleDate));
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            // 多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        // 债券基本资料
        accurIntCalInParameter.setSecInfoModel(secInfoModel);
        // 付息计划
        accurIntCalInParameter.setSecIntScheduleList(invScheduleList);
        // 利率重置信息
        accurIntCalInParameter.setPaperIrResetList(paperIrResetList);
        // 交易所债券特殊处理start----------------------------------------------------------------
        AccurIntCalOutParameter out = null;
        // 如果交易场所为上交所、深交所的情况
        if (isJiaoYiSuo) {
            // 付息计划截止日对应的利息
            Map<String, BigDecimal> intMap = new HashMap<>();
            if (null != invScheduleList && !invScheduleList.isEmpty()) {
                for (SecIntSchedule intSche : invScheduleList) {
                    intMap.put(intSche.getActualCutoffDate().toString(), intSche.getAmount());
                }
            }
            if (intMap.containsKey(accurIntCalInParameter.getCalDate().toString())
                    && !((Variety.贴现.equals(secInfoModel.getBondsType()))
                    || Variety.零息.equals(secInfoModel.getBondsType()))) {
                out = new AccurIntCalOutParameter();
                out.setInterest(intMap.get(accurIntCalInParameter.getCalDate().toString())
                        .multiply(new BigDecimal("100")).divide(curSecAmt, 12, RoundingMode.HALF_UP));
                accurIntCalInParameter.setCalDate(accurIntCalInParameter.getCalDate());
                AccurIntForSecheduleCalculator calculator2 = CalculatorFactory.createAccurIntForSecheduleCalculator();
                if (accurIntCalInParameter.getPaperIrResetList() != null
                        && accurIntCalInParameter.getPaperIrResetList().size() > 1) {
                    accurIntCalInParameter.getSecInfoModel().setIntRateType(IntRateType.浮动利率);
                }
                out = calculator2.calculate(accurIntCalInParameter);
            }
        }
        // 交易所债券特殊处理end----------------------------------------------------------------
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
        if (isJiaoYiSuo) {
            unitInt = ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                    Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam("SEC_INT_SCALE_EXCHANGE")) ? "8"
                            : CacheUtil.getSysParam("SEC_INT_SCALE_EXCHANGE")),
                    RoundingMode.HALF_UP);
        } else {
            unitInt = ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                    Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam("SEC_INT_SCALE_BANK")) ? "12"
                            : CacheUtil.getSysParam("SEC_INT_SCALE_BANK")),
                    RoundingMode.HALF_UP);
        }
        intAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(unitInt, prinAmt), 100, 2);
        if ((StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), ps)
                || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), ps))) {
            if (unitInt.compareTo(BigDecimal.ZERO) < 0) {
                unitInt = BigDecimal.ZERO;
            }
            if (intAmt.compareTo(BigDecimal.ZERO) < 0) {
                intAmt = BigDecimal.ZERO;
            }
        }
        try {
            // 单位净价
            if (StringUtil.equals(calculateField, DictCodeListExt3.债券计算模式_单位净价.getValue())) {
                cpriceAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(cprice, prinAmt), new BigDecimal(100), 2);
                // 全价总额 = 净价总额+ 利息总额
                fpriceAmt = ArithmeticUtil.doAdd(intAmt, cpriceAmt);
                // round（全价总额/券面总额，10）*100
                // 全价 = 单位应计利息 + 单位净价 (保持横向的一致性)
                fprice = ArithmeticUtil.doAdd(cprice, unitInt);
            }
            YieldPriceOutParameter yieldPriceOutParameter = null;
            PriceYieldOutParameter priceYieldOutParameter = null;
            switch (calculateField) {
                // 单位净价
                case "01":
                    // 全价总额
                case "02":
                    // 全价总额==>到期收益率
                    yieldPriceInParameter.setFullPrice(fprice);
                    yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                    yield = yieldPriceOutParameter.getMatYield();
                    if (secInfoModelDto.getNextExeDate() != null) {
                        // 全价总额==>行权收益率
                        yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                            exerYield = yieldPriceOutParameter.getExeYield();
                        }
                    }
                    break;
                // 到期收益率
                case "03":
                    // 到期收益率==>全价总额
                    priceYieldInParameter.setMatYield(yield);
                    priceYieldOutParameter = priceByYieldCalculator.calculate(priceYieldInParameter);
                    fprice = priceYieldOutParameter.getFullPrice();
                    // 全价总额==>行权收益率
                    if (secInfoModelDto.getNextExeDate() != null) {
                        yieldPriceInParameter.setFullPrice(fprice);
                        yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                            exerYield = yieldPriceOutParameter.getExeYield();
                        }
                    }
                    break;
                // 行权收益率（暂时不存在）
                case "04":
                    // 行权收益率==>全价总额
                    if (secInfoModelDto.getNextExeDate() != null) {
                        priceYieldInParameter.setMatYield(exerYield);
                        yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
                        priceYieldOutParameter = priceByYieldCalculator.calculate(priceYieldInParameter);
                        fprice = priceYieldOutParameter.getFullPrice();
                        yieldPriceInParameter.setFullPrice(fprice);
                        yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                        yield = yieldPriceOutParameter.getMatYield();
                    }
                    break;
                default:
                    break;
            }
            // 如果是全价、到期收益率 或 行权收益率
            if (StringUtil.equals(calculateField, DictCodeListExt3.债券计算模式_单位全价.getValue())
                    || StringUtil.equals(calculateField, DictCodeListExt3.债券计算模式_到期收益率.getValue())
                ) {
                // 全价总额 = 单位全价 * 券面总额 / 100
                fpriceAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(fprice, prinAmt), new BigDecimal(100), 2);
                cpriceAmt = ArithmeticUtil.subtract(fpriceAmt, intAmt);
                // round（净价总额/券面总额，10）*100
                // 单位净价 = 全价-单位应计利息 (保持横向的一致性)
                cprice = ArithmeticUtil.subtract(fprice, unitInt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (yield != null) {
            yield = ArithmeticUtil.multiply(yield, new BigDecimal(100));
        }
        if (exerYield != null) {
            exerYield = ArithmeticUtil.multiply(exerYield, new BigDecimal(100));
        }

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("unitInt", unitInt);
        result.put("intAmt", intAmt);
        result.put("cprice", cprice);
        result.put("cpriceAmt", cpriceAmt);
        result.put("fprice", fprice);
        result.put("fpriceAmt", fpriceAmt);
        result.put("yield", yield == null ? new BigDecimal(0.00) : yield);
        result.put("exerYield", exerYield);
        return result;
    }

    public BigDecimal getYield(CfetsYieldRequest request){
        if (StringUtil.equals(request.getTenderTarget(),DictCodeListExt4.招标标的_利率.getValue())){
            return request.getWinPrice();
        }else  if (StringUtil.equals(request.getTenderTarget(),DictCodeListExt4.招标标的_利差.getValue())){
            FinRateInfo finRateInfo = cfetsPrimaryBidManageMapper.getFinRateInfoByFinprodId(request.getFinprodId());
            if (StringUtil.isNotEmpty(finRateInfo) && StringUtil.isNotEmpty(finRateInfo.getRate()) && StringUtil.isNotEmpty(finRateInfo.getCoefficient())){
                BigDecimal rate =null;
                if (StringUtil.isNotEmpty(finRateInfo.getBenchmarkIdAccuracy())){
                    rate = finRateInfo.getRate().setScale(finRateInfo.getBenchmarkIdAccuracy().intValue(), RoundingMode.HALF_UP);
                 }else{
                    rate = finRateInfo.getRate();
                }
                BigDecimal yield = ArithmeticUtil.doAdd(ArithmeticUtil.multiply(rate, finRateInfo.getCoefficient()), request.getWinPrice());
                return yield;
            }else{
                return request.getWinPrice();
            }
        }
        return request.getWinPrice();
    }
}
