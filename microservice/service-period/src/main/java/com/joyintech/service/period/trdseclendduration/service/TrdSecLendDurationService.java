package com.joyintech.service.period.trdseclendduration.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.autoperiod.common.service.InvestPeriodCommonService;
import com.joyintech.service.period.trdseclendduration.dao.TrdSecLendDurationMapper;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommonDeleteDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommonWflParam;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.TrdRepoDurationDto;
import com.joyintech.tams.invest.common.dto.period.trdseclendduration.TrdSecLendDurationDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TrdSecLendDurationService extends BaseService<TrdRepoDurationDto> {

    @Autowired
    private TrdSecLendDurationMapper trdSecLendDurationMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;

    @Autowired
    private RepTradeMapper repTradeMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;

    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private InvestPeriodCommonService investPeriodCommonService;

    @Autowired
    private MapperDelegateService mapperDelegateService;

    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoServiceImpl;

    /**
     * 平台日期
     */
    @Autowired
    private IPlatformDate platformDateImpl;
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

    public Page<TrdSecLendDurationDto> findPage(int page, int length, Map<String, Object> param) {
        setParams(param);
        Page<TrdSecLendDurationDto> pageList = queryForPageForSelectReturn(page, length, () -> trdSecLendDurationMapper.findPage(param));
        return pageList;
    }

    public Map<String,Object> getShowHandNum( Map<String, Object> param) {
        param.put("hodingType", DictCodeListExt3.资产类型_债券借贷融入券可用持仓.getValue());
        BigDecimal intposition = trdSecLendDurationMapper.getShowHandNum(param);
        Map<String,Object> result = new HashMap<>();
        result.put("INTEGRATE_POSITION",intposition);
        return result;
    }



    public List<TrdSecLendDurationDto> selectAllData(Map<String, Object> param) {
        setParams(param);
        return trdSecLendDurationMapper.findPage(param);
    }

    private void setParams(Map<String, Object> param) {
        param.put("PAGE_DATE_STATUS01", DictCodeListExt1.画面数据状态_待处理.getValue());
        param.put("PAGE_DATE_STATUS03", DictCodeListExt1.画面数据状态_流程中.getValue());
        param.put("PAGE_DATE_STATUS05", DictCodeListExt1.画面数据状态_有效.getValue());
        param.put("PAGE_DATE_STATUS06", DictCodeListExt1.画面数据状态_撤销.getValue());
        param.put("COMMON_BUSINESS_TYPETRD51", DictCodeListExt2.共通业务类型_债券借贷交易.getValue());
        param.put("FINPROD_TYPE_TGF29", DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
        param.put("FINPROD_TYPE_TGF30", DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
        param.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        param.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        param.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        param.put("C02", DictCodeListExt1.现金流二级类型_利息.getValue());
        param.put("PST07", DictCodeList.交易方向_到期.getValue());
        param.put("PST22", DictCodeList.交易方向_收息到账.getValue());
    }

    public TrdSecLendDurationDto findEffectiveData(Map<String, Object> params) {
        setParams(params);
        return trdSecLendDurationMapper.findEffectiveData(params);
    }

    /**
     * 流程结束时处理
     *
     * @param trdSelLendDurationDto 参数
     * @return
     */
    public String doComplete(TrdSecLendDurationDto trdSelLendDurationDto) {
        String workflowId = trdSelLendDurationDto.getWorkflowId();
        //撤销时查找授信交易编号
        String tradeid = trdSecLendDurationMapper.findCeTradeIdByWorkflowId(workflowId);
        // 金融产品交易Entity
        List<WflTrdProductDeal> wflTrdProductDeals = trdSecLendDurationMapper.findDealByWorkflowId(workflowId);
        // 组合交易
        List<PtlTrade> ptlTrades = gen组合交易(trdSelLendDurationDto, wflTrdProductDeals);
        String updateUser = trdSelLendDurationDto.getUpdateUser();
        Date updateTime = new Date();
        if (BussinessConstant.REVOKE.equalsIgnoreCase(trdSelLendDurationDto.getOperateType())) {
            TrdFinProductDealTotal trdFinProductDealTotal = investPeriodCommonService.revoke汇总和交易相关表(trdSelLendDurationDto.getBusiCode(),
                    updateUser, trdSelLendDurationDto.getCancelRemark(), null, trdSelLendDurationDto.getActPortfolioId());
            
            // 删除质押券持仓
            WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
            BeanUtils.copyProperties(trdFinProductDealTotal, wflTrdFinProductDealTotal);
            deletePledgeSecPosition(wflTrdFinProductDealTotal);
            deleteWflByWorkflowId(workflowId);
            
            //删除授信占用
            riskCreditListCommonValidatorService.deleteCredit(tradeid);
            return Boolean.TRUE.toString();
        }

        // 金融产品交易汇总
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = trdSecLendDurationMapper.findTotalByWorkflowId(workflowId);
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        BeanUtils.copyProperties(wflTrdFinProductDealTotal, trdFinProductDealTotal);
        trdFinProductDealTotal.setUpdateTime(updateTime);
        trdFinProductDealTotal.setUpdateUser(updateUser);
        trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);

        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
        trdDealTotalAdd.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        trdDealTotalAdd.setIsCashset(trdSelLendDurationDto.getIsCashPay());
        trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);

        List<TrdProductDeal> trdProductDeals = new ArrayList<>();
        wflTrdProductDeals.forEach(wflTrdProductDeal -> {
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtils.copyProperties(wflTrdProductDeal, deal);
            String counterid = trdSecLendDurationMapper.selectCounterIdByTotleTradeId(wflTrdProductDeal.getTotleTradeId());
            deal.setCounterId(counterid);
            deal.setUpdateTime(updateTime);
            deal.setUpdateUser(updateUser);
            trdProductDeals.add(deal);
        });

        // 划款指令
        List<WflStlPayOrders> stlPayOrdersDtos = trdSecLendDurationMapper.findOrdersByWorkflowId(workflowId);
        stlPayOrdersDtos.forEach(order -> {
            StlPayOrders deal = new StlPayOrders();
            BeanUtils.copyProperties(order, deal);
            deal.setUpdateTime(updateTime);
            deal.setUpdateUser(updateUser);
            stlPayOrdersMapper.insertSelective(deal);
        });

        List<TrdProductDeal> tempTrdProductDealList = new ArrayList<>();
        List<TrdProductDealAdd> tempTrdProductDealAddList = new ArrayList<>();

        // 7.4 插入金融产品交易表
        trdProductDeals.forEach(trdProductDeal -> {
            trdProductDeal.setUpdateTime(updateTime);
            trdProductDeal.setUpdateUser(updateUser);
            trdProductDealMapper.insertSelective(trdProductDeal);

            // 交易附属表添加
            TrdProductDealAdd add = new TrdProductDealAdd();
            add.setTradeId(trdProductDeal.getTradeId());
            add.setPortfolioId(trdSelLendDurationDto.getPortfolioId());
            trdProductDealAddMapper.insertSelective(add);
            tempTrdProductDealAddList.add(add);
            // 交易附属表添加
            tempTrdProductDealList.add(trdProductDeal);

            if (StringUtil.isNotEmpty(trdSelLendDurationDto.getActPortfolioId())
                    && !StringUtil.equals(trdSelLendDurationDto.getPortfolioId(), trdSelLendDurationDto.getActPortfolioId())) {
                TrdProductDeal deal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(deal, trdProductDeal);
                String tradeId = deal.getTradeId();
                deal.setTradeId(deal.getTradeId() + "S");
                trdProductDealMapper.insertSelective(deal);
                tempTrdProductDealList.add(deal);

                TrdProductDealAdd add1 = new TrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(add1, add);
                add1.setTradeId(add1.getTradeId() + "S");
                add1.setPortfolioId(trdSelLendDurationDto.getActPortfolioId());
                trdProductDealAddMapper.insertSelective(add1);
                tempTrdProductDealAddList.add(add1);
            }
        });

        // 金融产品交易汇总扩展Entity
        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = trdSecLendDurationMapper.findPaTotalByWorkflowId(workflowId);
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        BeanUtils.copyProperties(wflPaTrdFinDealTotal, paTrdFinProductDealTotal);
        paTrdFinProductDealTotal.setUpdateTime(updateTime);
        paTrdFinProductDealTotal.setUpdateUser(updateUser);
        paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);

        //这段没用，在持仓流水共通中调用过了
       /* if (DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue().equals(trdRepoDurationDto.getFinprodType2())) {
            Map<String, Object> params = new HashMap<>();
            params.put("finprodId", wflTrdFinProductDealTotal.getFinprodId());
            params.put("holdingType", DictCodeList.持仓类型_质押.getValue());
            params.put("mDate", wflTrdFinProductDealTotal.getMdate());
            params.put("seqNo", IDGenerator.generate());
            PtlSecPositionFlow flow = trdSecLendDurationMapper.selectSecFlow(params);
            trdSecLendDurationMapper.insertSelect04Sec(flow);
            trdSecLendDurationMapper.insertSelect04SecFlow(params);
        }*/

        // 7.3 插入组合交易
        ptlTrades.forEach(ptlTrade -> {
            PtlTrade deal = new PtlTrade();
            BeanUtils.copyProperties(ptlTrade, deal);
            deal.setUpdateTime(updateTime);
            deal.setUpdateUser(updateUser);
            ptlTradeMapper.insertSelective(deal);
            if (StringUtil.isNotEmpty(trdSelLendDurationDto.getActPortfolioId())
                    && !StringUtil.equals(trdSelLendDurationDto.getPortfolioId(), trdSelLendDurationDto.getActPortfolioId())) {
                deal.setPortfolioId(trdSelLendDurationDto.getActPortfolioId());
                deal.setBusiNo(deal.getBusiNo() + "S");
                ptlTradeMapper.insertSelective(deal);
            }
        });

        // 7.7 交易报送信息
        List<RepTrade> repTrades = gen交易报送信息(trdSelLendDurationDto, wflTrdProductDeals);
        repTrades.forEach(repTrade -> {
            RepTrade deal = new RepTrade();
            BeanUtils.copyProperties(repTrade, deal);
            deal.setUpdateTime(updateTime);
            deal.setUpdateUser(updateUser);
            repTradeMapper.insertSelective(deal);
        });
        // 7.2 删除原业务流程表中的交易记录
        deleteWflByWorkflowId(workflowId);

        // 所有交易生效后调用的共通方法
        for (TrdProductDeal baseDto : tempTrdProductDealList) {
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            tempTrdProductDealAddList.forEach(trdProductDealAdd -> {
                if (trdProductDealAdd.getTradeId().equals(baseDto.getTradeId())) {
                    inputDto.setProdid(trdProductDealAdd.getPortfolioId());
                }
            });

            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            inputDto.setTrdProductDeal(baseDto);
            List<PtlTrade> ptlTradeList = new ArrayList<>();
            ptlTrades.forEach(ptlTrade -> {
                if (baseDto.getTradeId().equals(ptlTrade.getBusiNo())) {
                    ptlTradeList.add(ptlTrade);
                }
            });
            inputDto.setPtlTradeList(ptlTradeList);
            if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                StlPayOrders order = new StlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(order, stlPayOrdersDtos.get(0));
                inputDto.setStlPayOrders(order);
            }
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
        }

        return Boolean.TRUE.toString();
    }

    private String deleteWflByWorkflowId(String workflowId) {
        wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        wflTrdDealTotalAddMapper.deleteByWorkflowId(workflowId);
        wflStlPayOrdersMapper.deleteByWorkflowId(workflowId);
        wflPaTrdFinDealTotalMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealMapper.deleteByWorkflowId(workflowId);
        wflPaTrdProductDealMapper.deleteByWorkflowId(workflowId);
        return Boolean.TRUE.toString();
    }

    /**
     * 删除原业务流程表中的交易记录
     *
     * @param trdCommonDeleteDto TrdCommonDeleteDto
     * @return true
     */
    public String doDelete(TrdCommonDeleteDto trdCommonDeleteDto) {
//        deleteWflByWorkflowId(trdCommonDeleteDto.getWorkflowId());
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = trdCommonDeleteDto.getWflTrdFinProductDealTotal();
        deletePledgeSecPosition(wflTrdFinProductDealTotal);
        return Boolean.TRUE.toString();
    }

    private void deletePledgeSecPosition(WflTrdFinProductDealTotal wflTrdFinProductDealTotal) {
        if (wflTrdFinProductDealTotal != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("finprodId", wflTrdFinProductDealTotal.getFinprodId());
            List<RepoInfo> repoInfos = findRepoInfo(params);
            repoInfos.forEach(repoInfo -> {
                PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
                ptlSecPositionFlowDto.setPortfolioId(wflTrdFinProductDealTotal.getPortfolioId());
                ptlSecPositionFlowDto.setSecAcctId(wflTrdFinProductDealTotal.getSecManageAcctId());
                ptlSecPositionFlowDto.setFinprodId(repoInfo.getPledgeFinprodId());
                ptlSecPositionFlowDto.setBranch(wflTrdFinProductDealTotal.getBranch());
                ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_质押.getValue());
                ptlSecPositionFlowDto.setCdate(wflTrdFinProductDealTotal.getSettleDate());
                ptlSecPositionFlowDto.setCcy(wflTrdFinProductDealTotal.getCcy());
                ptlSecPositionFlowDto.setTradeId(wflTrdFinProductDealTotal.getTotleTradeId());
                basePtlSecPositionInfoServiceImpl.deleteSecPosition(ptlSecPositionFlowDto);
            });
        }
    }

    private List<RepTrade> gen交易报送信息(TrdSecLendDurationDto entity, List<WflTrdProductDeal> wflTrdProductDeals) {
        List<RepTrade> result = new ArrayList<>();
        // 产品登记编码
        Map<String, Object> params = new HashMap<>();
        params.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        params.put("portfolioId", entity.getPortfolioId());
        String 产品登记编码 = trdSecLendDurationMapper.findPrdRegCode(params);
        if (StringUtil.isEmpty(产品登记编码)) {
            return result;
        }
        BigDecimal 最低收盘价 = BigDecimal.ONE;
        if (!DictCodeList.币种_人民币_CNY.getValue().equals(entity.getCcy())) {
            最低收盘价 = investPeriodCommonService.find最低收盘价(entity.getCcy(), DateUtil.format(entity.getTradeDate()));
        }
        BigDecimal 最低收盘价F = 最低收盘价;
        wflTrdProductDeals.forEach(wflTrdProductDeal -> {
            RepTrade repTrade = new RepTrade();
            repTrade.setRepIssuePartyId(entity.getIssuePartyId());
            repTrade.setRepFinprodRegId(产品登记编码);
            repTrade.setRepBankTradeCode(wflTrdProductDeal.getTradeId());
            repTrade.setRepBankAssetCode(wflTrdProductDeal.getFinprodId());
            repTrade.setRepCcy(entity.getCcy());
            repTrade.setRepCount(wflTrdProductDeal.getShareAmt());
            repTrade.setRepTradeDate(entity.getTradeDate());
            repTrade.setRepTradeComponent(entity.getCounterId());
            repTrade.setRepDelCode(wflTrdProductDeal.getTradeId());
            repTrade.setRepRemark(entity.getRemark());
            repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
            repTrade.setRepStatusDate(platformDateImpl.getSysDate());
            repTrade.setCreateUser(wflTrdProductDeal.getCreateUser());
            repTrade.setCreateDept(wflTrdProductDeal.getCreateDept());
            repTrade.setCreateTime(wflTrdProductDeal.getCreateTime());

            if (DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue().equals(entity.getFinprodType2())) {
                repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_投资资产支出.getValue());
            } else {
                repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_投资资产所得收益.getValue());
            }
            repTrade.setRepMoneyFlowTypeExp("债券借贷费用");
            result.add(repTrade);
        });
        return result;
    }

    private List<PtlTrade> gen组合交易(TrdSecLendDurationDto entity, List<WflTrdProductDeal> wflTrdProductDeals) {
        List<PtlTrade> result = new ArrayList<>();
        wflTrdProductDeals.forEach(wflTrdProductDeal -> {
            PtlTrade ptlTrade2 = new PtlTrade();
            ptlTrade2.setPortfolioId(entity.getPortfolioId());
            ptlTrade2.setBusiNo(wflTrdProductDeal.getTradeId());
            ptlTrade2.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade2.setBusiId(entity.getFinprodId());
            ptlTrade2.setBusiType(DictCodeListExt2.共通业务类型_债券借贷存续期.getValue());
            ptlTrade2.setSecManageAcctId(entity.getSecManageAcctId());
            ptlTrade2.setSettleDate(entity.getSettleDate());
            result.add(ptlTrade2);
        });
        return result;
    }

    public List<RepoInfo> findRepoInfo(Map<String, Object> params) {
        return trdSecLendDurationMapper.findRepoInfo(params);
    }

    public List<FinCashPlan> findFinCashInfo(String finprodId) {
        return trdSecLendDurationMapper.findFinCashInfo(finprodId);
    }

    public Page<SelectOptionDto> getBusiCode(int page, int length, Map<String, Object> param) {
        param.put("FINPROD_TYPE_TGF29", DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
        param.put("FINPROD_TYPE_TGF30", DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());

        param.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("IS_CANCEL_N", DictCodeList.是否_否.getValue());
        param.put("PS_T07", DictCodeList.交易方向_到期.getValue());
        return queryForPageForSelectReturn(page, length, () -> trdSecLendDurationMapper.getBusiCode(param));
    }

    public String insertAutoClassifyBatch(TrdCommonWflParam trdCommonWflParam) {
        mapperDelegateService.insertAutoClassifyBatch(trdCommonWflParam.getWflTrdProductDeals(), trdCommonWflParam.getWflPaTrdFinDealTotal(),
                trdCommonWflParam.getWflTrdFinProductDealTotal(), trdCommonWflParam.getWflStlPayOrdersDtos());
        return "Success";
    }
}