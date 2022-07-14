package com.joyintech.service.period.trdbondduration.service;

import com.github.pagehelper.util.StringUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.invest.common.autoperiod.common.dao.PeriodCommonMapper;
import com.joyintech.tams.invest.common.autoperiod.common.service.InvestPeriodCommonService;
import com.joyintech.service.period.trdbondduration.dao.TrdBondDurationMapper;
import com.joyintech.tams.common.base.dao.RepTradeMapper;
import com.joyintech.tams.common.base.entity.RepTrade;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.TrdBondExerciseProcessDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.common.TrdCommonDeleteDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.SecPositionExerciseInfoDto;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.TrdBondDurationDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: microservice
 * @Package com.joyintech.service.period.trdbondduration.service
 * @Description: TODO
 * @date Date : 2018年09月21日 14:40
 */
@Service
public class TrdBondDurationService extends BaseService {

    @Autowired
    private TrdBondDurationMapper trdBondDurationMapper;

    @Autowired
    private IPlatformDate platformDateImpl;

//    @Autowired
//    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
//
//    @Autowired
//    private StlPayOrdersMapper stlPayOrdersMapper;
//
//    @Autowired
//    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;
//
//    @Autowired
//    private TrdProductDealMapper trdProductDealMapper;
//
//    @Autowired
//    private TrdProductDealAddMapper trdProductDealAddMapper;
//
//    @Autowired
//    private PaTrdProductDealMapper paTrdProductDealMapper;
//
//    @Autowired
//    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private PeriodCommonMapper periodCommonMapper;

//    @Autowired
//    private WflTrdProductDealMapper wflTrdProductDealMapper;
//
//    @Autowired
//    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;
//
//    @Autowired
//    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;
//
//    @Autowired
//    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;
//
//    @Autowired
//    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private RepTradeMapper repTradeMapper;

    @Autowired
    private InvestPeriodCommonService investPeriodCommonService;
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

    public Page<SelectOptionDto> getBusiCode(int page, int length, Map<String, Object> param) {
        param.put("FINPROD_TYPE_TGF01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());
        return queryForPageForSelectReturn(page, length, () -> trdBondDurationMapper.getBusiCode(param));
    }

    public Page<SelectOptionDto> getSecManageAcctId(int page, int length, Map<String, Object> param) {
        param.put("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());
        return queryForPageForSelectReturn(page, length, () -> trdBondDurationMapper.getSecManageAcctId(param));
    }

    public Page<SelectOptionDto> getFinprodMarketId(int page, int length, Map<String, Object> param) {
        param.put("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());
        return queryForPageForSelectReturn(page, length, () -> trdBondDurationMapper.getFinprodMarketId(param));
    }

    public Page<TrdBondDurationDto> findPage(int page, int length, Map<String, Object> param) {
        List<String> statusValid = param.get("statusValid") == null ? null : (List)param.get("statusValid");
        setParams(param);
        this.setParamForFindAllData(param);
        Page<TrdBondDurationDto> result = null;
        //update by wangcheng SHBS-2192 查询条件输入 资金到账日  ，有效状态选择 待处理  报错
        if(statusValid != null && StringUtil.isNotEmpty((String)param.get("tradeDateStart"))&&((statusValid.size()==1) && statusValid.contains("01"))){
            result = new Page<TrdBondDurationDto>();
        }else{
            result = queryForPageForSelectReturn(page, length, () -> trdBondDurationMapper.findPage(param));
        }
        return result;
    }

    /**
     * 查询有效、撤销或待处理的存续期交易数据（所有数据）
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    public List<TrdBondDurationDto> findAllData(@RequestBody Map<String, Object> param) {
        setParams(param);
        this.setParamForFindAllData(param);
        return trdBondDurationMapper.findPage(param);
    }

    private void setParamForFindAllData(Map<String, Object> param) {
        List<String> statusValid = param.get("statusValid") == null ? null : (List)param.get("statusValid");

        String cashType = param.get("cashType") == null ? null : (String)param.get("cashType");
        param.put("paBizType", cashType);
        StringBuilder cashTypeSb = new StringBuilder();
        if (cashType != null) {
            if (cashType.contains(DictCodeListExt1.平安业务类型_债券中间收息.getValue())
                || cashType.contains(DictCodeListExt1.平安业务类型_债券中间还本.getValue())) {
                cashTypeSb.append(DictCodeList.现金流类型_中间收息.getValue());
                cashTypeSb.append(DictCodeList.现金流类型_中间还本.getValue());
            }
            if (cashType.contains(DictCodeListExt1.平安业务类型_债券到期.getValue())) {
                cashTypeSb.append(DictCodeList.现金流类型_到期收息.getValue());
                cashTypeSb.append(DictCodeList.现金流类型_到期还本.getValue());
            }

            if (cashType.contains(DictCodeListExt1.平安业务类型_债券中间收息.getValue())) {
                param.put("cashType", DictCodeList.现金流类型_中间收息.getValue());
            }
            else if (cashType.contains(DictCodeListExt1.平安业务类型_债券到期.getValue())) {
                param.put("cashType", DictCodeList.现金流类型_到期还本.getValue());
            }
            else if (cashType.contains(DictCodeListExt1.平安业务类型_债券中间还本.getValue())) {
                param.put("cashType", DictCodeList.现金流类型_中间还本.getValue());
            }
            param.put("cashTypeSb", cashTypeSb.toString());
        }
    }

    private void setParams(Map<String, Object> param) {
        param.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
        param.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
        param.put("PAGE_DATE_STATUS01", DictCodeListExt1.画面数据状态_待处理.getValue());
        param.put("PAGE_DATE_STATUS03", DictCodeListExt1.画面数据状态_流程中.getValue());
        param.put("PAGE_DATE_STATUS05", DictCodeListExt1.画面数据状态_有效.getValue());
        param.put("PAGE_DATE_STATUS06", DictCodeListExt1.画面数据状态_撤销.getValue());
        param.put("CASH_CLASS_TYPE01", DictCodeList.现金流类型_中间收息.getValue());
        param.put("CASH_CLASS_TYPE02", DictCodeList.现金流类型_中间还本.getValue());
        param.put("CASH_CLASS_TYPE03", DictCodeList.现金流类型_到期还本.getValue());
        param.put("CASH_CLASS_TYPE04", DictCodeList.现金流类型_到期收息.getValue());
        // 数据字典缓存.交易状态.初始
        param.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("YESNO_N", DictCodeList.是否_否.getValue());
        param.put("YESNO_Y", DictCodeList.是否_是.getValue());
        // 数据字典缓存.持仓类型.实际持仓
        param.put("POSITION_TYPE03", DictCodeList.持仓类型_实际.getValue());
        // 数据字典缓存.金融产品类型 投管分类.债券
        param.put("FINPROD_TYPE_TGF01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("TRD10", DictCodeList.共通业务类型_债券类资产存续期.getValue());
        param.put("STATUS_N", DictCodeList.数据状态_新建.getValue());
        param.put("adjustMDate", DateUtil.parse((String)param.get("mDate")));
        String plateDate = platformDateImpl.getSysDateStr();
        param.put("PLATEFORM_DATE", DateUtil.parse(plateDate));
        if (param.get(BussinessConstant.PLUS_DAYS) != null) {
            param.put("PLATEFORM_DATE_OTHER", DateUtil.plusDays(plateDate, (Integer)param.get(BussinessConstant.PLUS_DAYS)));
        }
        else {
            param.put("PLATEFORM_DATE_OTHER", DateUtil.plusDays(plateDate, 90));
        }
        // 多级法人改造 add by caiqun 20200615
        if(MultilevelDataAuthorityUtil.isEnabled()) {
            //当前用户所属机构代码
            String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
            param.put("institutionCode",createInst);
        }
        // 多级法人改造 add by caiqun 20200615
        param.put("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());
    }

    public TrdBondDurationDto findEffectiveData(Map<String, Object> params) {
        setParams(params);
        // 根据statusValid查询数据
        return trdBondDurationMapper.findEffectiveData(params);
    }
    public String doComplete(TrdBondDurationDto durationDto) {
        String updateUser = durationDto.getUpdateUser();
        Date updateTime = new Date();
        if (BussinessConstant.REVOKE.equals(durationDto.getOperateType())) {
            //查找授信交易TRADE_ID 撤销释放额度用
            List<String> tradeidList = trdBondDurationMapper.findCeTradeIdByTotleTradeId(durationDto.getBusiCode());
            //删除授信占用
            if (tradeidList != null && !tradeidList.isEmpty()){
                tradeidList.forEach(tradeid -> {
                    riskCreditListCommonValidatorService.deleteCredit(tradeid);
                });
            }
            
            investPeriodCommonService.deleteByWorkflowId(durationDto.getWorkflowId());
            TrdFinProductDealTotal trdFinProductDealTotal = investPeriodCommonService.revoke汇总和交易相关表(durationDto.getBusiCode(),
                updateUser, durationDto.getRevokeReason(), null, null);
            // 删除其他表
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
                //授信释放
                if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null &&
                		DictCodeList.交易方向_还本到账.getValue().equals(baseTradeInfoDto.getBaseTrade().getTrdProductDeal().getPs())){
                    TrdProductDeal trdProductDeal1 = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal1, baseTradeInfoDto.getBaseTrade().getTrdProductDeal());
                    trdProductDeal1.setPs(DictCodeList.交易方向_还本.getValue());
                    AllTradeCommonInputDto inputDto1 = new AllTradeCommonInputDto();
                    trdProductDeal1.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                    inputDto1.setTrdProductDeal(trdProductDeal1);
                    AllTradeCommonUtil.handleBeforeTradeComplete(inputDto1, false);
                }          
                调估值接口(baseTradeInfoDto, portfolioId);

                baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(baseTradeInfoDto1 -> {
                    String portfolioId1 = baseTradeInfoDto1.getTrdProductDealAdd() != null ?
                            baseTradeInfoDto1.getTrdProductDealAdd().getPortfolioId() : baseTradeInfoDto1.getBaseTrade().getTrdProductDealAdd().getPortfolioId();
                    调估值接口(baseTradeInfoDto1, portfolioId1); // 子组合调用估值接口
                });

            });
        }
        return Boolean.TRUE.toString();
    }

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
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal, baseTradeInfoDto.getTrdProductDeal());
            if (com.joyintech.framework.util.StringUtil.equals("999999", deal.getInvAim())) {
                deal.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
            }
            inputDto.setTrdProductDeal(deal);
            inputDto.setTrdProductDealAdd(baseTradeInfoDto.getTrdProductDealAdd());
            inputDto.setPtlTradeList(baseTradeInfoDto.getPtlTrade());
            inputDto.setStlPayOrders(baseTradeInfoDto.getStlPayOrders());
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            // 存续期增加通道费 add by renping 20191018 start
            inputDto.setTrdTradeFeeDetail(null);
            // 存续期增加通道费 add by renping 20191018 end
        }
        if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal,
                baseTradeInfoDto.getBaseTrade().getTrdProductDeal());
            if (com.joyintech.framework.util.StringUtil.equals("999999", deal.getInvAim())) {
                deal.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
            }
            inputDto.setTrdProductDeal(deal);
            inputDto.setTrdProductDealAdd(baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd());
            inputDto.setPtlTradeList(baseTradeInfoDto.getBaseTrade().getPtlTrade());
            inputDto.setStlPayOrders(baseTradeInfoDto.getStlPayOrders());
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
        }
    }

    private void insertRep(TrdBondDurationDto durationDto, String updateUser, Date updateTime, TrdProductDeal deal,
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
//    public String doComplete(TrdBondDurationDto trdBondDurationDto) {
//        String updateUser = trdBondDurationDto.getUpdateUser();
//        Date updateTime = new Date();
//        if (BussinessConstant.REVOKE.equalsIgnoreCase(trdBondDurationDto.getOperateType())) {
//            periodCommonService.revoke汇总和交易相关表(trdBondDurationDto.getBusiCode(), updateUser,
//                trdBondDurationDto.getRevokeReason());
//            deleteWflByWorkflowId(trdBondDurationDto.getWorkflowId());
//            return Boolean.TRUE.toString();
//        }
//
//        TrdBondDurDbInertDto trdBondDurDbInertDto = trdBondDurationDto.getTrdBondDurDbInertDto();
//        // 金融产品交易信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
//        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = trdBondDurDbInertDto.getWflTrdFinProductDealTotal();
//        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
//        BeanUtils.copyProperties(wflTrdFinProductDealTotal, trdFinProductDealTotal);
//        trdFinProductDealTotal.setUpdateUser(updateUser);
//        trdFinProductDealTotal.setUpdateTime(updateTime);
//        trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
//
//        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
//        List<WflStlPayOrders> wflStlPayOrders = trdBondDurDbInertDto.getWflStlPayOrdersList();
//        wflStlPayOrders.forEach(wflStlPayOrders1 -> {
//            StlPayOrders stlPayOrders = new StlPayOrders();
//            BeanUtils.copyProperties(wflStlPayOrders1, stlPayOrders);
//            stlPayOrders.setUpdateUser(updateUser);
//            stlPayOrders.setUpdateTime(updateTime);
//            stlPayOrdersMapper.insertSelective(stlPayOrders);
//        });
//
//        // 金融产品交易汇总扩展Entity -> 金融产品交易汇总扩展业务流程表
//        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = trdBondDurDbInertDto.getWflPaTrdFinDealTotal();
//        PaTrdFinProductDealTotal paTrdFinDealTotal = new PaTrdFinProductDealTotal();
//        BeanUtils.copyProperties(wflPaTrdFinDealTotal, paTrdFinDealTotal);
//        paTrdFinDealTotal.setUpdateUser(updateUser);
//        paTrdFinDealTotal.setUpdateTime(updateTime);
//        paTrdFinProductDealTotalMapper.insertSelective(paTrdFinDealTotal);
//
//        // 金融产品交易Entity -> 金融产品交易业务流程表
//        List<WflTrdProductDeal> wflTrdProductDeals = trdBondDurDbInertDto.getWflTrdProductDealList();
//        wflTrdProductDeals.forEach(deal -> {
//            TrdProductDeal de = new TrdProductDeal();
//            BeanUtils.copyProperties(deal, de);
//            PaTrdProductDeal paDeal = new PaTrdProductDeal();
//            paDeal.setTradeId(de.getTradeId());
//            if (DictCodeList.交易方向_到期.getValue().equals(de.getPs())) {
//                paDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券到期.getValue());
//            }
//            else if (DictCodeList.交易方向_收息.getValue().equals(de.getPs())) {
//                paDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券中间收息.getValue());
//            }
//            else if (DictCodeList.交易方向_还本.getValue().equals(de.getPs())) {
//                paDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券中间还本.getValue());
//            }
//            paDeal.setUpdateUser(updateUser);
//            paDeal.setUpdateTime(updateTime);
//            de.setUpdateUser(updateUser);
//            de.setUpdateTime(updateTime);
//            paTrdProductDealMapper.insertSelective(paDeal);
//            trdProductDealMapper.insertSelective(de);
//        });
//
//        // 附属表
//        List<WflTrdProductDealAdd> wflTrdProductDealAdds = trdBondDurDbInertDto.getWflTrdProductDealAdds();
//        if (wflTrdProductDealAdds != null) {
//            for (WflTrdProductDealAdd wflTrdProductDealAdd : wflTrdProductDealAdds) {
//                TrdProductDealAdd dealAdd = new TrdProductDealAdd();
//                BeanUtils.copyProperties(wflTrdProductDealAdd, dealAdd);
//                trdProductDealAddMapper.insert(dealAdd);
//            }
//        }
//
//        List<PtlTrade> ptlTrades = trdBondDurDbInertDto.getPtlTrades();
//        ptlTrades.forEach(ptlTrade -> ptlTradeMapper.insertSelective(ptlTrade));
//        String 产品登记编码 = periodCommonMapper.selectProdRegistCode(wflTrdFinProductDealTotal.getPortfolioId(),
//            DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
//        if (StringUtil.isNotEmpty(产品登记编码)) {
//            List<RepTrade> repTrades = gen交易报送信息(trdBondDurationDto, wflTrdProductDeals, 产品登记编码);
//            repTrades.forEach(repTrade -> {
//                repTrade.setCreateUser(trdBondDurationDto.getCreateUser());
//                repTrade.setCreateTime(trdBondDurationDto.getCreateTime());
//                repTrade.setCreateDept(trdBondDurationDto.getCreateDept());
//                repTrade.setUpdateUser(updateUser);
//                repTrade.setUpdateTime(updateTime);
//                repTradeMapper.insertSelective(repTrade);
//            });
//        }
//        deleteWflByWorkflowId(trdBondDurationDto.getWorkflowId());
//        return Boolean.TRUE.toString();
//    }
    private List<RepTrade> gen交易报送信息(TrdBondDurationDto entity, TrdProductDeal deal, String 产品登记编码) {
        List<RepTrade> result = new ArrayList<>();
        // 产品登记编码
        Map<String, Object> params = new HashMap<>();
        BigDecimal 最低收盘价 = BigDecimal.ONE;
        if (!DictCodeList.币种_人民币_CNY.getValue().equals(entity.getCcy())) {
            最低收盘价 = investPeriodCommonService.find最低收盘价(entity.getCcy(), entity.getTradeDate());
        }
        BigDecimal 最低收盘价F = 最低收盘价;
        // 折算人民币金额（元）
        RepTrade repTrade = new RepTrade();
        repTrade.setRepFinprodRegId(产品登记编码);
        repTrade.setRepIssuePartyId(entity.getIssuePartyId());
        repTrade.setRepBankTradeCode(deal.getTradeId());
        repTrade.setRepBankAssetCode(entity.getFinprodMarketId());
        repTrade.setRepCcy(entity.getCcy());
        repTrade.setRepCount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(100)));
        repTrade.setRepTradeDate(DateUtil.parse(entity.getTradeDate()));
        repTrade.setRepDelCode(deal.getTradeId());
        repTrade.setRepRemark(entity.getRemark());
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
        repTrade.setRepStatusDate(platformDateImpl.getSysDate());
        repTrade.setRepTradeComponent("无");

        if (DictCodeList.交易方向_到期.getValue().equals(deal.getStatus())
                || DictCodeList.交易方向_还本.getValue().equals(deal.getStatus())) {
            repTrade.setRepOccurMoney(deal.getPrinAmt());
            repTrade.setRepMoneyCny(ArithmeticUtil.multiply(deal.getPrinAmt(), 最低收盘价F));
            if (DictCodeList.交易方向_到期.getValue().equals(deal.getStatus())) {
                repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_资产卖出或到期.getValue());
                repTrade.setRepMoneyFlowTypeExp("债券到期还本本金");
                repTrade.setRepUnitPriceFull(entity.getPrinAmt()); // 单位成交全价
                repTrade.setRepUnitPriceNet(entity.getPrinAmt()); // 单位成交净价
            } else {
                repTrade.setRepUnitPriceFull(entity.getPrinAmt() != null ? entity.getPrinAmt().negate() : null); // 单位成交全价
                repTrade.setRepUnitPriceNet(entity.getPrinAmt() != null ? entity.getPrinAmt().negate() : null); // 单位成交净价
                repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_其他收入.getValue());
                repTrade.setRepMoneyFlowTypeExp("债券中间还本本金");
            }
        } else if (DictCodeList.交易方向_收息.getValue().equals(deal.getStatus())) {
            repTrade.setRepOccurMoney(deal.getIntAmt());
            repTrade.setRepMoneyCny(ArithmeticUtil.multiply(deal.getIntAmt(), 最低收盘价F));
            repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_投资资产所得收益.getValue());
            repTrade.setRepMoneyFlowTypeExp("债券利息收入");
        }
        result.add(repTrade);
        return result;
    }
//    private List<RepTrade> gen交易报送信息(TrdBondDurationDto entity, List<WflTrdProductDeal> wflTrdProductDeals, String 产品登记编码) {
//        List<RepTrade> result = new ArrayList<>();
//        // 产品登记编码
//        Map<String, Object> params = new HashMap<>();
//        BigDecimal 最低收盘价 = BigDecimal.ONE;
//        if (!DictCodeList.币种_人民币_CNY.getValue().equals(entity.getCcy())) {
//            最低收盘价 = periodCommonService.find最低收盘价(entity.getCcy(), entity.getTradeDate());
//        }
//        BigDecimal 最低收盘价F = 最低收盘价;
//        wflTrdProductDeals.forEach(wflTrdProductDeal -> {
//            // 折算人民币金额（元）
//            RepTrade repTrade = new RepTrade();
//            repTrade.setRepFinprodRegId(产品登记编码);
//            repTrade.setRepIssuePartyId(entity.getIssuePartyId());
//            repTrade.setRepBankTradeCode(wflTrdProductDeal.getTradeId());
//            repTrade.setRepBankAssetCode(entity.getFinprodMarketId());
//            repTrade.setRepCcy(entity.getCcy());
//            repTrade.setRepCount(ArithmeticUtil.multiply(wflTrdProductDeal.getShareAmt(), new BigDecimal(100)));
//            repTrade.setRepTradeDate(DateUtil.parse(entity.getTradeDate()));
//            repTrade.setRepDelCode(wflTrdProductDeal.getTradeId());
//            repTrade.setRepRemark(entity.getRemark());
//            repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
//            repTrade.setRepStatusDate(platformDateImpl.getSysDate());
//            repTrade.setRepTradeComponent("无");
//            if (DictCodeList.交易方向_到期.getValue().equals(wflTrdProductDeal.getPs())
//                || DictCodeList.交易方向_还本.getValue().equals(wflTrdProductDeal.getPs())) {
//                repTrade.setRepOccurMoney(wflTrdProductDeal.getPrinAmt());
//                repTrade.setRepMoneyCny(ArithmeticUtil.multiply(wflTrdProductDeal.getPrinAmt(), 最低收盘价F));
//                if (DictCodeList.交易方向_到期.getValue().equals(wflTrdProductDeal.getPs())) {
//                    repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_资产卖出或到期.getValue());
//                    repTrade.setRepMoneyFlowTypeExp("债券到期还本本金");
//                    repTrade.setRepUnitPriceFull(entity.getPrinAmt()); // 单位成交全价
//                    repTrade.setRepUnitPriceNet(entity.getPrinAmt()); // 单位成交净价
//                }
//                else {
//                    repTrade.setRepUnitPriceFull(entity.getPrinAmt() != null ? entity.getPrinAmt().negate() : null); // 单位成交全价
//                    repTrade.setRepUnitPriceNet(entity.getPrinAmt() != null ? entity.getPrinAmt().negate() : null); // 单位成交净价
//                    repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_其他收入.getValue());
//                    repTrade.setRepMoneyFlowTypeExp("债券中间还本本金");
//                }
//            }
//            else if (DictCodeList.交易方向_收息.getValue().equals(wflTrdProductDeal.getPs())) {
//                repTrade.setRepOccurMoney(wflTrdProductDeal.getIntAmt());
//                repTrade.setRepMoneyCny(ArithmeticUtil.multiply(wflTrdProductDeal.getIntAmt(), 最低收盘价F));
//                repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_投资资产所得收益.getValue());
//                repTrade.setRepMoneyFlowTypeExp("债券利息收入");
//            }
//            result.add(repTrade);
//        });
//        return result;
//    }

    public int selectMatchedData(String bizCode) {
        return trdBondDurationMapper.selectMatchedData(bizCode);
    }

    public String doDelete(TrdCommonDeleteDto trdCommonDeleteDto) {
        investPeriodCommonService.deleteByWorkflowId(trdCommonDeleteDto.getWorkflowId());
        return Boolean.TRUE.toString();
    }

//    private void deleteWflByWorkflowId(String workflowId) {
//        wflTrdProductDealMapper.deleteByWorkflowId(workflowId);
//        wflPaTrdFinDealTotalMapper.deleteByWorkflowId(workflowId);
//        wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
//        wflStlPayOrdersMapper.deleteByWorkflowId(workflowId);
//        wflTrdProductDealAddMapper.deleteByWorkflowId(workflowId);
//    }

    public String doRevoke(TrdBondDurationDto trdBondDurationDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("bizCode", trdBondDurationDto.getBusiCode());
        params.put("workflowId", trdBondDurationDto.getWorkflowId());
        params.put("workflowStatus", DictCodeList.数据状态_删除中.getValue());
        params.put("user", trdBondDurationDto.getCreateUser());
        params.put("dept", trdBondDurationDto.getCreateDept()==null?"dept":trdBondDurationDto.getCreateDept());
        params.put("time", trdBondDurationDto.getCreateTime());
        params.put("revokeReason", trdBondDurationDto.getRevokeReason());
        investPeriodCommonService.insertWflTable(params);
        return Boolean.TRUE.toString();
    }

    /**
     * 当前维度是否是计息结束日最大
     * @param params
     * @return
     */
    public Boolean checkIfMaxDate(Map<String, String> params) {
        params.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        params.put("YESNO_N", DictCodeList.是否_否.getValue());
        long num = trdBondDurationMapper.checkIfMaxDate(params);
        return num <= 0l;
    }

    public Boolean checkIfMinDate(Map<String, String> params) {
        params.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        params.put("YESNO_N", DictCodeList.是否_否.getValue());
        params.put("CASH_CLASS_TYPE01", DictCodeList.现金流类型_中间收息.getValue());
        params.put("CASH_CLASS_TYPE02", DictCodeList.现金流类型_中间还本.getValue());
        params.put("CASH_CLASS_TYPE03", DictCodeList.现金流类型_到期还本.getValue());
        params.put("CASH_CLASS_TYPE04", DictCodeList.现金流类型_到期收息.getValue());
        params.put("POSITION_TYPE03", DictCodeList.持仓类型_实际.getValue());
        params.put("PLATEFORM_DATE", platformDateImpl.getSysDateStr());
        long num = trdBondDurationMapper.checkIfMinDate(params);
        return num <= 0l;
    }
    public List<FinAssetIntInfo> findAssetIntInfo(Map<String, Object> params) {
        setParams(params);
        return trdBondDurationMapper.findAssetIntInfo(params);
    }

    /**
     * 获取存在流程中的同类交易
     * @param map 参数
     * @return 统计结果
     */
    public List<String> getFlowDeal(Map<String, Object> map) {
        return trdBondDurationMapper.getFlowDeal(map);
    }

    public List<SecPositionExerciseInfoDto> findSecPosition(Map<String, Object> param) {
        return trdBondDurationMapper.findSecPosition(param);
    }

    public List<String> findProcessDtos(Map<String, Object> param) {
        param.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("YESNO_N", DictCodeList.是否_否.getValue());
        param.put("FINPROD_TYPE_TGF01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("STATUS_N", DictCodeList.数据状态_新建.getValue());
        param.put("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());
        return trdBondDurationMapper.findProcessDtos(param);
    }

    public Map<String,Object> getPkByBizCode(String bizCode){
    	return trdBondDurationMapper.getPkByBizCode(bizCode);
    }

    public List<String> findWorkflowProcessDtos(Map<String, Object> params) {
        params.put("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());
        return trdBondDurationMapper.findWorkflowProcessDtos(params);
    }

    /**
     * 资产代码：下拉框
     *
     * @param
     * @return 资产代码：下拉框
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodId(int page, int length, Map<String, Object> param){
        param.put("loginUserName", SecurityUtil.getLoginUser().getUserName());
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("isautobond", CacheUtil.getSysParam("IS_AUTOBOND"));
        param.put("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());
        return queryForPageForSelectReturn(page, length, () -> trdBondDurationMapper.getFinprodId(param));
    }

    public Page<SelectOptionDto> getBusiCodeNew(int page, int length, Map<String, Object> param) {
        param.put("FINPROD_TYPE_TGF01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("YESNO_N", DictCodeList.是否_否.getValue());
        param.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("TRD10", DictCodeList.共通业务类型_债券类资产存续期.getValue());
        param.put("userName", LoginUserUtil.getUserName());
        param.put("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());
        return queryForPageForSelectReturn(page, length, () -> trdBondDurationMapper.getBusiCodeNew(param));
    }

    // modify by etliu 20220228 start
    // 广农商二阶段 GNS-896:【广农商场内清算】场内债券存续期-行权赎回，批处理后未生成交易
    /**
     * 查询存续期交易数据(行权)
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    public TrdBondExerciseProcessDto findEffectiveDataXq(Map<String, Object> params) {
        TrdBondExerciseProcessDto resultData = trdBondDurationMapper.findEffectiveDataXq(params);
        if (resultData != null) {
            resultData.setTrdBondPtlSecExerciseInfos(trdBondDurationMapper.selectTrdBondPtlSecExerciseInfoList(params));
        }
        return resultData;
    }
    // modify by etliu 20220228 end
}
