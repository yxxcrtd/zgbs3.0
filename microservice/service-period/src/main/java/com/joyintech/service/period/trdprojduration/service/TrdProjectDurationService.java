package com.joyintech.service.period.trdprojduration.service;

import com.github.pagehelper.util.StringUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.BasePtlRecePositionService;
import com.joyintech.tams.invest.common.autoperiod.common.dao.PeriodCommonMapper;
import com.joyintech.tams.invest.common.autoperiod.common.service.InvestPeriodCommonService;
import com.joyintech.service.period.trdbondduration.dao.TrdBondDurationMapper;
import com.joyintech.service.period.trdprojduration.dao.TrdProjectDurationMapper;
import com.joyintech.tams.common.base.dao.RepTradeMapper;
import com.joyintech.tams.common.base.entity.RepTrade;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.common.TrdCommonDeleteDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.trdprojduration.TrdProjectDurationDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.service.TradeProductDealCommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: microservice
 * @Package com.joyintech.service.period.trdbondduration.service
 * @Description: 项目类资产 存续期 微服务用Service
 * @date Date : 2018年09月21日 14:40
 */
@Service
public class TrdProjectDurationService extends BaseService {

    @Autowired
    private TrdProjectDurationMapper trdProjectDurationMapper;

    @Autowired
    private TrdBondDurationMapper trdBondDurationMapper;
    @Autowired
    private BasePtlRecePositionService basePtlRecePositionService;

    @Autowired
    private IPlatformDate platformDateImpl;
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

    public Page<TrdProjectDurationDto> findPage(int page, int length, Map<String, Object> param) {
        param.put("DEPTTYPE_01", "01");
//        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
//        String branchInstitutionCode = trdProjectDurationMapper.findBranchInstitutionCode(param);
//        DynamicDataSourceHolder.clearDataSource();
//        param.put("branchInstitutionCode", branchInstitutionCode);
        setParams(param);
        setParamsForSearch(param);
        Page<TrdProjectDurationDto> page1 = queryForPageForSelectReturn(page, length, () -> trdProjectDurationMapper.findPage(param));
       //查询区间净投资收益率 yjm start 20190822
        List<TrdProjectDurationDto>  list = page1.getRoot();
        for (TrdProjectDurationDto dto : list){
            //查询区间净投资收益率
            Map<String, Object> map = new HashMap<>();
           // map.put("busiCode", dto.getBusiCode());
            map.put("vDate", dto.getvDate());
            map.put("mDate", dto.getmDate());
            map.put("C02", DictCodeListExt1.现金流二级类型_利息.getValue());
            map.put("finprodId", dto.getFinprodId());
            map.put("F18", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
            map.put("ISPAYINT", DictCodeList.是否_是.getValue());
            //判断资产还本不带息，若为还本不带息，则随付息计划。若为还本带息，则应是最接近的付息计划的利率
            int count=trdProjectDurationMapper.getisPayInt(dto.getFinprodId());
            if(count>0){
                map.put("ISPAYINT", DictCodeList.是否_否.getValue());
            }
            BigDecimal yield = trdProjectDurationMapper.selectYield(map);
            if (!StringUtils.isEmpty(yield)){
                dto.setYield(yield);
            }
        }
        //查询区间净投资收益率 yjm end 20190822
        return page1;
    }

    public List<TrdProjectDurationDto> selectAllData(Map<String, Object> param) {
        setParams(param);
        setParamsForSearch(param);
        return trdProjectDurationMapper.findPage(param);
    }

    private void setParamsForSearch(Map<String, Object> param) {
        List<String> statusValid = param.get("statusValid") == null ? null : (List)param.get("statusValid");

        String cashType = param.get("cashType") == null ? null : (String)param.get("cashType");
        param.put("paBizType", cashType);
        StringBuilder cashTypeSb = new StringBuilder();
        if (cashType != null) {
            if (cashType.contains(DictCodeListExt1.平安业务类型_项目类资产中间付息.getValue())
                || cashType.contains(DictCodeListExt1.平安业务类型_项目类资产中间还本.getValue())) {
                cashTypeSb.append(DictCodeList.现金流类型_中间收息.getValue());
                cashTypeSb.append(DictCodeList.现金流类型_中间还本.getValue());
            }
            if (cashType.contains(DictCodeListExt1.平安业务类型_项目类资产到期.getValue())) {
                cashTypeSb.append(DictCodeList.现金流类型_到期收息.getValue());
                cashTypeSb.append(DictCodeList.现金流类型_到期还本.getValue());
            }
            if (cashType.contains(DictCodeListExt1.平安业务类型_项目类资产中间付息.getValue())) {
                param.put("cashType", DictCodeList.现金流类型_中间收息.getValue());
            }
            else if (cashType.contains(DictCodeListExt1.平安业务类型_项目类资产中间还本.getValue())) {
                param.put("cashType", DictCodeList.现金流类型_中间还本.getValue());
            }
            else if (cashType.contains(DictCodeListExt1.平安业务类型_项目类资产到期.getValue())) {
                param.put("cashType", DictCodeList.现金流类型_到期还本.getValue());
            }
            param.put("cashTypeSb", cashTypeSb.toString());
        }
    }

    private void setParams(Map<String, Object> param) {
        param.put("PAGE_DATE_STATUS01", DictCodeListExt1.画面数据状态_待处理.getValue());
        param.put("PAGE_DATE_STATUS03", DictCodeListExt1.画面数据状态_流程中.getValue());
        param.put("PAGE_DATE_STATUS05", DictCodeListExt1.画面数据状态_有效.getValue());
        param.put("PAGE_DATE_STATUS06", DictCodeListExt1.画面数据状态_撤销.getValue());
        param.put("CASH_CLASS_TYPE01", DictCodeList.现金流类型_中间收息.getValue());
        param.put("CASH_CLASS_TYPE02", DictCodeList.现金流类型_中间还本.getValue());
        param.put("CASH_CLASS_TYPE03", DictCodeList.现金流类型_到期还本.getValue());
        param.put("CASH_CLASS_TYPE04", DictCodeList.现金流类型_到期收息.getValue());
        param.put("PS_T07", DictCodeList.交易方向_到期.getValue());
        param.put("PS_T09", DictCodeList.交易方向_收息.getValue());
        param.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
        param.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
        // 数据字典缓存.交易状态.初始
        param.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("YESNO_N", DictCodeList.是否_否.getValue());
        param.put("YESNO_Y", DictCodeList.是否_是.getValue());
        // 数据字典缓存.持仓类型.实际持仓
        param.put("POSITION_TYPE03", DictCodeList.持仓类型_实际.getValue());
        param.put("FINPROD_TYPE_TGF17", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        param.put("TRD15", DictCodeList.共通业务类型_项目类资产存续期.getValue());

        String plateDate = platformDateImpl.getSysDateStr();
        param.put("PLATEFORM_DATE", DateUtil.parse(plateDate));
        if (param.get(BussinessConstant.PLUS_DAYS) != null) {
            param.put("PLATEFORM_DATE_OTHER", DateUtil.plusDays(plateDate, (Integer)param.get(BussinessConstant.PLUS_DAYS)));
        }
        else {
            param.put("PLATEFORM_DATE_OTHER", DateUtil.plusDays(plateDate, 90));
        }
    }

    public TrdProjectDurationDto findEffectiveData(Map<String, Object> params) {
        setParams(params);
        TrdProjectDurationDto trdProjectDurationDto=trdProjectDurationMapper.findEffectiveData(params);
        //查询资产是不是还本不带息资产
        int count=trdProjectDurationMapper.getisPayInt(trdProjectDurationDto.getFinprodId());
        //该区间，是否仅仅还本，若仅仅还本，也无需展示累计还本未付金额
        Map<String, Object> mapIsPayInt=new HashMap<>();
        mapIsPayInt.put("finprodId",params.get("finprodMarketId").toString());
        mapIsPayInt.put("mdate",params.get("mDate").toString());
        int countt=trdProjectDurationMapper.isPayIntDate(mapIsPayInt);
        if(count<1){
            trdProjectDurationDto.setIntAmtUnpayFlag(false);//不是还本不带息的资产,不展示累计还本未付金额
        }else{
            if(countt<1){
                trdProjectDurationDto.setIntAmtUnpayFlag(false);
            }
        }
        return trdProjectDurationDto;
    }

    public List<FinAssetIntInfo> findAssetIntInfo(Map<String, Object> params) {
        setParams(params);
        List<FinAssetIntInfo> list  =  trdProjectDurationMapper.findAssetIntInfo(params);
        //查询区间净投资收益率 yjm start 20190822
        for (FinAssetIntInfo dto : list){                  
            params.put("C02", DictCodeListExt1.现金流二级类型_利息.getValue());
            params.put("F18", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
            params.put("ISPAYINT", DictCodeList.是否_是.getValue());
            //判断资产还本不带息，若为还本不带息，则随付息计划。若为还本带息，则应是最接近的付息计划的利率
            int count=trdProjectDurationMapper.getisPayInt(params.get("finprodId").toString());
            if(count>0){
                params.put("ISPAYINT", DictCodeList.是否_否.getValue());
            }
            BigDecimal yield = trdProjectDurationMapper.selectYield(params);
            if (!StringUtils.isEmpty(yield)){
                dto.setYield(yield);
            }
            //查询区间未付利息
            String statusValid = (String)params.get("statusValid");//获取交易状态
            Long branch=new Long(DateUtil.formatDate(dto.getmDate())+"02");
            params.put("portfolioId",dto.getPortfolioId());//列表组合
            params.put("branch",branch);//列表分支序号
            BigDecimal repaymentUnpayInterest=BigDecimal.ZERO;//理论区间未付初始值为0
            BigDecimal sjRepaymentUnpayInterest=BigDecimal.ZERO;//实际区间未付初始值为0
            if(statusValid.equals(DictCodeListExt1.画面数据状态_待处理.getValue())){//待处理，从应收款流水表取值
                repaymentUnpayInterest = basePtlRecePositionService.getRepayAmt(params);//区间未付利息
                sjRepaymentUnpayInterest=repaymentUnpayInterest;
            }else{//从交易表取值
                //取交易表的branch
                String actbranch=trdProjectDurationMapper.getBranch(params);
                if(actbranch!=null) {
                    params.put("branch", actbranch);
                    //理论区间未付还本不带息的值
                    repaymentUnpayInterest = basePtlRecePositionService.getRepaymentUnpayInterest(params);//区间未付利息
                    //实际区间未付还本不带息的值
                    sjRepaymentUnpayInterest = trdProjectDurationMapper.getRepaymentUnpayInterest(params);
                }
            }
            dto.setRepaymentUnpayInterest(repaymentUnpayInterest);//理论未付还本不带息金额
            dto.setActrRepaymentUnpayInterest(sjRepaymentUnpayInterest);
        }
        //查询区间净投资收益率 yjm end 20190822
        return list;
    }

    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    public String doDelete(TrdCommonDeleteDto trdCommonDeleteDto) {
        investPeriodCommonService.deleteByWorkflowId(trdCommonDeleteDto.getWorkflowId());
        return Boolean.TRUE.toString();
    }

    @Autowired
    private InvestPeriodCommonService investPeriodCommonService;
    @Autowired
    private TradeProductDealCommService baseTradeProductDealCommService;
    @Autowired
    private PeriodCommonMapper periodCommonMapper;

    @Autowired
    private RepTradeMapper repTradeMapper;

    public String doComplete(TrdProjectDurationDto durationDto) {
        String updateUser = durationDto.getUpdateUser();
        Date updateTime = new Date();
        if (BussinessConstant.REVOKE.equals(durationDto.getOperateType())) {
            //查找授信交易TRADE_ID 撤销释放额度用
            List<String> tradeidList = trdProjectDurationMapper.findCeTradeIdByTotleTradeId(durationDto.getBusiCode());
            //删除授信占用
            if (tradeidList != null && !tradeidList.isEmpty()){
                tradeidList.forEach(tradeid -> {
                    riskCreditListCommonValidatorService.deleteCredit(tradeid);
                });
            }
            
            TrdFinProductDealTotal trdFinProductDealTotal = investPeriodCommonService.revoke汇总和交易相关表(durationDto.getBusiCode(),
                updateUser, durationDto.getRevokeReason(), null, null);
            // 删除其他表
            investPeriodCommonService.deleteByWorkflowId(durationDto.getWorkflowId());
            return Boolean.TRUE.toString();
        }
        investPeriodCommonService.doInsertAndDelete(durationDto.getBaseTotalTradeInfoDto(), updateUser, updateTime,
            durationDto.getWorkflowId());

        List<BaseTradeInfoDto> baseTradeInfoDtos = durationDto.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList();
        if (baseTradeInfoDtos != null && !baseTradeInfoDtos.isEmpty()) {
            baseTradeInfoDtos.forEach(baseTradeInfoDto -> {
                String portfolioId = baseTradeInfoDto.getTrdProductDealAdd() != null ? baseTradeInfoDto.getTrdProductDealAdd().getPortfolioId() : baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd().getPortfolioId();

                String 产品登记编码 = periodCommonMapper.selectProdRegistCode(portfolioId,
                    DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
                if (StringUtil.isNotEmpty(产品登记编码)) {
                    if (baseTradeInfoDto.getTrdProductDeal() != null) {
                        insertRep(durationDto, updateUser, updateTime, baseTradeInfoDto.getTrdProductDeal(), 产品登记编码);
                    }
                    if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
                        insertRep(durationDto, updateUser, updateTime, baseTradeInfoDto.getBaseTrade().getTrdProductDeal(),
                            产品登记编码);
                    }
                }

                // 一体化对应etliu start
                // 所有交易生效后调用的共通方法
//                if (baseTradeInfoDto.getTrdProductDeal() != null) {
//                    AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//                    inputDto.setProdid(portfolioId);
//                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//                    inputDto.setTrdProductDealAdd(baseTradeInfoDto.getTrdProductDealAdd());
//                    inputDto.setTrdProductDeal(baseTradeInfoDto.getTrdProductDeal());
//                    //update zhangweiqi 通道费修改 2019-10-16 begin
//                    if(!com.joyintech.framework.util.StringUtil.isEmptyList(baseTradeInfoDto.getTrdTradeFeeDetail())){
//                 	   inputDto.setTrdTradeFeeDetail(baseTradeInfoDto.getTrdTradeFeeDetail().get(0));
//                    }
//                  //update zhangweiqi 通道费修改 2019-10-16 end
//                    inputDto.setPtlTradeList(baseTradeInfoDto.getPtlTrade());
//                    AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
//                }
//
//                if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
//                    AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//                    inputDto.setProdid(portfolioId);
//                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//                    inputDto.setTrdProductDealAdd(baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd());
//                    inputDto.setTrdProductDeal(baseTradeInfoDto.getBaseTrade().getTrdProductDeal());
//                    // update zhangweiqi 通道费修改 2019-10-16 begin
//                    if (baseTradeInfoDto.getTrdProductDeal() == null
//                        && !com.joyintech.framework.util.StringUtil.isEmptyList(
//                            baseTradeInfoDto.getTrdTradeFeeDetail())) {
//                        inputDto.setTrdTradeFeeDetail(
//                            baseTradeInfoDto.getTrdTradeFeeDetail().get(0));
//                    }
//                    // update zhangweiqi 通道费修改 2019-10-16 end
//                    inputDto.setPtlTradeList(baseTradeInfoDto.getBaseTrade().getPtlTrade());
//                    AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
//                }

                调估值接口(baseTradeInfoDto, portfolioId);
                if(baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList()!=null && baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().size()>0 ){
                    baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().forEach(receDto->{
                        String portfolioId1 = receDto.getTrdProductDealAdd() != null ?
                                receDto.getTrdProductDealAdd().getPortfolioId() : receDto.getBaseTrade().getTrdProductDealAdd().getPortfolioId();
                        调估值接口(receDto, portfolioId1); // 还本不带息调用估值接口
                    });
                }
                if(baseTradeInfoDto.getBaseTradeInfoDtoList() != null) {
                    baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(baseTradeInfoDto1 -> {
                        String portfolioId1 = baseTradeInfoDto1.getTrdProductDealAdd() != null ?
                                baseTradeInfoDto1.getTrdProductDealAdd().getPortfolioId() : baseTradeInfoDto1.getBaseTrade().getTrdProductDealAdd().getPortfolioId();
                        调估值接口(baseTradeInfoDto1, portfolioId1); // 子组合调用估值接口

                    });

                     baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(subRece->{
                         if(subRece.getRepaymentUnpayInterestBaseTradeInfoDtoList()!=null){
                             for(BaseTradeInfoDto rece:subRece.getRepaymentUnpayInterestBaseTradeInfoDtoList()){
                                 String portfolioId1 = rece.getTrdProductDealAdd() != null ?
                                         rece.getTrdProductDealAdd().getPortfolioId() : rece.getBaseTrade().getTrdProductDealAdd().getPortfolioId();
                                 调估值接口(rece, portfolioId1); // 子组合调用估值接口
                             }
                         }
                     });
                }


                // 一体化对应etliu end
            });
        }
        return Boolean.TRUE.toString();
    }

    /**
     * 调用估值接口生成入账凭证（参考债券存续期）
     * @param baseTradeInfoDto
     * @param portfolioId
     */
    private void 调估值接口(BaseTradeInfoDto baseTradeInfoDto, String portfolioId) {
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        inputDto.setProdid(portfolioId);
        //update zhangweiqi 通道费修改 2019-10-16 begin
        if(!com.joyintech.framework.util.StringUtil.isEmptyList(baseTradeInfoDto.getTrdTradeFeeDetail())) {
            inputDto.setTrdTradeFeeDetail(baseTradeInfoDto.getTrdTradeFeeDetail().get(0));
        }
        //update zhangweiqi 通道费修改 2019-10-16 end
        inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
        if (baseTradeInfoDto.getTrdProductDeal() != null) {
            inputDto.setTrdProductDeal(baseTradeInfoDto.getTrdProductDeal());
            inputDto.setTrdProductDealAdd(baseTradeInfoDto.getTrdProductDealAdd());
            inputDto.setPtlTradeList(baseTradeInfoDto.getPtlTrade());
            inputDto.setStlPayOrders(baseTradeInfoDto.getStlPayOrders());
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            // 存续期增加通道费 add by renping 20191018 start
            inputDto.setTrdTradeFeeDetail(null);
            // 存续期增加通道费 add by renping 20191018 end
        }
        if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
            inputDto.setTrdProductDeal(baseTradeInfoDto.getBaseTrade().getTrdProductDeal());
            inputDto.setTrdProductDealAdd(baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd());
            inputDto.setPtlTradeList(baseTradeInfoDto.getBaseTrade().getPtlTrade());
            inputDto.setStlPayOrders(baseTradeInfoDto.getStlPayOrders());
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
        }
    }

    private void insertRep(TrdProjectDurationDto durationDto, String updateUser, Date updateTime, TrdProductDeal deal,
                           String 产品登记编码) {
        List<RepTrade> repTrades = gen交易报送信息(durationDto, deal, 产品登记编码);
        repTrades.forEach(repTrade -> {
            repTrade.setCreateUser(durationDto.getCreateUser());
            repTrade.setCreateTime(durationDto.getCreateTime());
            repTrade.setCreateDept(durationDto.getCreateDept());
            repTrade.setUpdateUser(updateUser);
            repTrade.setUpdateTime(updateTime);
            repTradeMapper.insertSelective(repTrade);
        });
    }

    private List<RepTrade> gen交易报送信息(TrdProjectDurationDto entity, TrdProductDeal deal, String 产品登记编码) {
        List<RepTrade> result = new ArrayList<>();
        // 产品登记编码
        Map<String, Object> params = new HashMap<>();
        BigDecimal 最低收盘价 = BigDecimal.ONE;
        if (!DictCodeList.币种_人民币_CNY.getValue().equals(deal.getCcy())) {
            最低收盘价 = investPeriodCommonService.find最低收盘价(deal.getCcy(), entity.getArrivalDate());
        }
        // 金融机构中文名(交易对手)
        String institutionName = periodCommonMapper.findInstitutionName(entity.getFinprodId());
        BigDecimal 最低收盘价F = 最低收盘价;
        // 折算人民币金额（元）
        RepTrade repTrade = new RepTrade();
        repTrade.setRepFinprodRegId(产品登记编码);
        repTrade.setRepIssuePartyId(entity.getIssuePartyId());
        repTrade.setRepBankTradeCode(deal.getTradeId());
        repTrade.setRepBankAssetCode(entity.getFinAssetInfo().getFinprodMarketId());
        repTrade.setRepCcy(deal.getCcy());
        repTrade.setRepCount(deal.getShareAmt());
        repTrade.setRepTradeDate(DateUtil.parse(entity.getArrivalDate()));
        repTrade.setRepDelCode(deal.getTradeId());
        repTrade.setRepRemark(entity.getRemark());
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
        repTrade.setRepStatusDate(platformDateImpl.getSysDate());
        repTrade.setRepUnitPriceNet(deal.getUnitCprice());
        repTrade.setRepUnitPriceFull(deal.getUnitFprice());
        repTrade.setRepTradeComponent(institutionName);
        if (DictCodeList.交易方向_到期.getValue().equals(deal.getStatus())
            || DictCodeList.交易方向_还本.getValue().equals(deal.getStatus())) {
            repTrade.setRepOccurMoney(deal.getTradeAmt());
            repTrade.setRepMoneyCny(ArithmeticUtil.multiply(deal.getTradeAmt(), 最低收盘价F));
            repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_资产卖出或到期.getValue());
            repTrade.setRepCount(deal.getTradeAmt());
            if (DictCodeList.交易方向_到期.getValue().equals(deal.getStatus())) {
                repTrade.setRepMoneyFlowTypeExp("到期还本");
            }
            else if (DictCodeList.交易方向_还本.getValue().equals(deal.getStatus())) {
                repTrade.setRepMoneyFlowTypeExp("中间还本");
            }
            repTrade.setRepUnitPriceNet(new BigDecimal(100));
            repTrade.setRepUnitPriceFull(new BigDecimal(100));
        }
        else  {
            repTrade.setRepOccurMoney(deal.getTradeAmt());
            repTrade.setRepMoneyCny(ArithmeticUtil.multiply(deal.getTradeAmt(), 最低收盘价F));
            repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_投资资产所得收益.getValue());
            repTrade.setRepMoneyFlowTypeExp("利息收入");
        }
        result.add(repTrade);
        return result;
    }

    /**
     * 插入流程表，查询流程中数据用到汇总和汇总扩展，详情页面利用留痕表展示
     *
     * @param baseTotalTradeInfoDto BaseTotalTradeInfoDto
     * @return 是否成功
     */
    public String doInsert(BaseTotalTradeInfoDto baseTotalTradeInfoDto) {
        if(StringUtil.isNotEmpty(baseTotalTradeInfoDto.getTrdFinProductDealTotal().getWorkflowId())){
            baseTradeProductDealCommService.deleteWflData(baseTotalTradeInfoDto.getTrdFinProductDealTotal().getWorkflowId());
        }
        investPeriodCommonService.insertWflData(baseTotalTradeInfoDto);
        return Boolean.TRUE.toString();
    }

    public String doRevoke(TrdProjectDurationDto durationDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("bizCode", durationDto.getBusiCode());
        params.put("workflowId", durationDto.getWorkflowId());
        params.put("workflowStatus", DictCodeList.数据状态_删除中.getValue());
        params.put("user", durationDto.getCreateUser());
        params.put("dept", StringUtil.isEmpty(durationDto.getCreateDept()) ? "dept" : durationDto.getCreateDept());
        params.put("time", durationDto.getCreateTime());
        params.put("revokeReason", durationDto.getRevokeReason());
        investPeriodCommonService.insertWflTable(params);
        return Boolean.TRUE.toString();
    }

    public Page<SelectOptionDto> getBusiCode(int page, int length, Map<String, Object> param) {
        return queryForPageForSelectReturn(page, length, () -> trdProjectDurationMapper.getBusiCode(param));
    }

    public List<BaseTradeProductDealDto> findEffectiveDeal(Map<String, Object> params) {
        return trdProjectDurationMapper.findTotals(params);
    }
    public List<BaseTradeProductDealDto> findChildTradeInfo(Map<String, Object> params) {
        return trdProjectDurationMapper.findChildTradeInfo(params);
    }
    /**
     * 取区间的未付还本不带息金额
     *
     * @param params
     * @return 返回实际的未付还本不带息金额
     */
    public BigDecimal getRepaymentUnpayInterest(Map<String, Object> params) {
        return trdProjectDurationMapper.getRepaymentUnpayInterest(params);
    }
    /**
     * 取交易的BRANCH
     *
     * @param params
     * @return 交易的BRANCH
     */
    public String getBranch(Map<String, Object> params) {
        return trdProjectDurationMapper.getBranch(params);
    }
    //add by liupengfei for 多级法人S
    public Page<SelectOptionDto> getFinprodMarketId(int page, int length, Map<String, Object> param) {
        return queryForPageForSelectReturn(page, length, () -> trdProjectDurationMapper.getFinprodMarketId(param));
    }
    //add by liupengfei for 多级法人E

    // add by etliu 20210426 权益类资产相关代码合并 start
    public List<StlPayOrdersDto> getGroupPayByList(Map<String,Object> param){
        return trdProjectDurationMapper.getGroupPayByList(param);
    }
    // add by etliu 20210426 权益类资产相关代码合并 end

    /**
     * 资产是否还本带息，若还本带息，还本日是否等于付息日
     * @param map
     * @return
     */
    public boolean isPayIntDate(Map<String, Object> map) {
        //若针对，还本带息，返回 true，针对还本不带息，若有付息日=还本日，返回true
        int count=trdProjectDurationMapper.isNoPayInt(map);
        if(count>0){
            int countt=trdProjectDurationMapper.isPayIntDate(map);
            if(countt>0){
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }
}
