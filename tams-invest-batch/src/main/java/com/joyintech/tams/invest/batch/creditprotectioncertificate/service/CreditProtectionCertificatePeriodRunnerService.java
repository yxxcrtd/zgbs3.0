package com.joyintech.tams.invest.batch.creditprotectioncertificate.service;

import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dao.CreditProtectionCertificatePeriodRunnerMapper;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodExpireDataDto;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodWriterDto;
import com.joyintech.tams.invest.batch.susprdfutureopenplangen.relyingonresources.DateCalService;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.constant.InterfCommonConstant;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_新建;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.*;

/**
 * 〈功能详细描述〉
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月25日
 * @since 1.0
 */
@Service
@Scope("prototype")
public class CreditProtectionCertificatePeriodRunnerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CreditProtectionCertificatePeriodRunnerMapper creditProtectionCertificatePeriodRunnerMapper;

    @Autowired
    private JoyinRestTemplate restTemplate;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private DateCalService dateCalService;

    @Autowired
    private PtlSecPositionFlowMapper ptlSecPositionFlowMapper;

    @Autowired
    private EtrSecPositionFlowMapper etrSecPositionFlowMapper;

    @Autowired
    private BaseGetBussinessId baseGetBussinessId;


    public void dealExpireData(CreditProtectionCertificatePeriodWriterDto writerDto) {
        try {

            List<CreditProtectionCertificatePeriodExpireDataDto> expireDatas = writerDto.getExpireDatas();

            //汇总交易编号
            String businessCode = generateBusinessCode();
            writerDto.setBussinessCode(businessCode);

            //平台日
            Date platDate = iPlatformDate.getSysDate();

            String calendarId;

            if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(expireDatas.get(0).getTradeMarket())) {
                calendarId = CacheUtil.getSysParam("STL_DOWNDATA_DEAL_SHJYS_CALID");
            } else {
                calendarId = CacheUtil.getSysParam("STL_DOWNDATA_DEAL_SZJYS_CALID");
            }
            // 下一个交易日
            Date nextWorkDate = getNextWorkDay(iPlatformDate.getSysDate(), calendarId);
            BigDecimal amount = BigDecimal.ZERO;

            for (CreditProtectionCertificatePeriodExpireDataDto expireData : expireDatas) {
                String businessCodeForDetail = generateBusinessCode();
                expireData.setTradeId(businessCodeForDetail);
                amount = amount.add(expireData.getRepNomCapi());
                //处理明细数据
                TrdProductDeal trdProductDeal = new TrdProductDeal();
                TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
                PtlTrade ptlTrade = new PtlTrade();

                trdProductDeal.setTradeId(businessCodeForDetail);
                trdProductDeal.setFinprodId(writerDto.getFinprodId());
                trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_信用保护凭证.getValue());
                trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
                trdProductDeal.setBranch(0L);
                trdProductDeal.setTradeDate(platDate);
                trdProductDeal.setVdate(expireData.getVdate());
                trdProductDeal.setMdate(expireData.getMdate());
                trdProductDeal.setSettleDate(nextWorkDate);
                trdProductDeal.setShareAmt(expireData.getRepNomCapi());
                trdProductDeal.setTradeAmt(BigDecimal.ZERO);
                trdProductDeal.setPs(DictCodeList.交易方向_到期.getValue());
                trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                trdProductDeal.setIsCancel(DictCodeListExt1.是否_否.getValue());
                trdProductDeal.setTotleTradeId(businessCode);
                trdProductDeal.setTradeMarket(expireData.getTradeMarket());
                trdProductDeal.setSecManageAcctId(expireData.getSecAcctId());
                trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_到期.getValue());
                trdProductDeal.setCreateUser("SYSTEM");
                trdProductDeal.setCreateTime(platDate);
                trdProductDeal.setUpdateTime(DateUtil.now());
                trdProductDeal.setUpdateUser("SYSTEM");
                trdProductDeal.setCreateDept("dept");

                trdProductDealAdd.setTradeId(businessCodeForDetail);
                trdProductDealAdd.setPortfolioId(expireData.getPortfolioId());
                trdProductDealAdd.setCreateUser("SYSTEM");
                trdProductDealAdd.setCreateTime(platDate);
                trdProductDealAdd.setUpdateTime(DateUtil.now());
                trdProductDealAdd.setUpdateUser("SYSTEM");
                trdProductDealAdd.setCreateDept("dept");


                ptlTrade.setPortfolioId(expireData.getPortfolioId());
                ptlTrade.setBusiNo(businessCodeForDetail);
                ptlTrade.setBusiId(businessCodeForDetail);
                ptlTrade.setBusiTable("02");
                ptlTrade.setSettleDate(nextWorkDate);
                ptlTrade.setBusiType(trdProductDeal.getBusiType());
                ptlTrade.setSecManageAcctId(expireData.getSecAcctId());
                ptlTrade.setCreateUser("SYSTEM");
                ptlTrade.setCreateTime(platDate);
                ptlTrade.setUpdateTime(DateUtil.now());
                ptlTrade.setUpdateUser("SYSTEM");
                ptlTrade.setCreateDept("dept");

                trdProductDealMapper.insertSelective(trdProductDeal);
                trdProductDealAddMapper.insertSelective(trdProductDealAdd);
                ptlTradeMapper.insertSelective(ptlTrade);
            }

            //汇总数据
            TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
            TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();

            trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_信用保护凭证存续期.getValue());
            trdFinProductDealTotal.setTotleTradeId(businessCode);
            trdFinProductDealTotal.setFinprodId(writerDto.getFinprodId());
            trdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_信用保护凭证.getValue());
            trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
            trdFinProductDealTotal.setTradeDate(platDate);
            trdFinProductDealTotal.setMdate(expireDatas.get(0).getMdate());
            trdFinProductDealTotal.setVdate(expireDatas.get(0).getVdate());
            trdFinProductDealTotal.setSettleDate(nextWorkDate);
            trdFinProductDealTotal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            trdFinProductDealTotal.setTradeAmt(BigDecimal.ZERO);
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_到期.getValue());
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdFinProductDealTotal.setIsCancel(DictCodeListExt1.是否_否.getValue());
            trdFinProductDealTotal.setTradeMarket(expireDatas.get(0).getTradeMarket());
            trdFinProductDealTotal.setCreateUser("SYSTEM");
            trdFinProductDealTotal.setCreateTime(platDate);
            trdFinProductDealTotal.setUpdateTime(DateUtil.now());
            trdFinProductDealTotal.setUpdateUser("SYSTEM");
            trdFinProductDealTotal.setCreateDept("dept");
            trdFinProductDealTotal.setTgBusiType(DictCodeListExt1.平安业务类型_到期.getValue());
            trdFinProductDealTotal.setShareAmt(amount);

            trdDealTotalAdd.setTotleTradeId(businessCode);
            trdDealTotalAdd.setCreateUser("SYSTEM");
            trdDealTotalAdd.setCreateTime(platDate);
            trdDealTotalAdd.setUpdateTime(DateUtil.now());
            trdDealTotalAdd.setUpdateUser("SYSTEM");
            trdDealTotalAdd.setCreateDept("dept");

            trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
            trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);

            sendNewPosition(writerDto);
            dealOldPosition(writerDto, nextWorkDate, false);
        } catch (Exception e) {
            logger.error("信用保护凭证自动生成错误", e);
            throw e;
        }
    }

    private String generateBusinessCode() {
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_信用保护凭证存续期);
        // 生成的业务编号
        if (null != baseGetBussinessId) {
            if (null != parameter.getCommonBusinessExtType() && !parameter.getCommonBusinessExtType().isEmpty()) {
                if (StringUtil.isNotEmpty(parameter.getCommonBusinessType())) {
                    parameter.getCommonBusinessType().setCodeName(
                            parameter.getCommonBusinessExtType().get("codeName").toString());
                    parameter.getCommonBusinessType().setValue(parameter.getCommonBusinessExtType().get("value").toString());
                } else {
                    DictCodeList dictCodeList = DictCodeList.枚举LIST_EXT转换LIST;
                    dictCodeList.setCodeName(parameter.getCommonBusinessExtType().get("codeName").toString());
                    dictCodeList.setValue(parameter.getCommonBusinessExtType().get("value").toString());
                    parameter.setCommonBusinessType(dictCodeList);
                }
            }
            return baseGetBussinessId.buildBussinessCode(parameter);
        }
        return IDGenerator.generate();
    }

    /**
     * 获取下一个工作日
     */
    public Date getNextWorkDay(Date tradeDate, String calendarId) {
        if (dateCalService != null) {
            List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDays(tradeDate, 31), calendarId);
            return dateCalService.calculationTradingDay(tradeDate, 1, holiday, "1");
        }
        return null;
    }


    private void dealOldPosition(CreditProtectionCertificatePeriodWriterDto writerDto, Date nextWorkDate, boolean isRevoke) {
        //指令持仓流水&指令持仓：扣减实际T+0信用保护凭证'名义本金'
        //交易持仓流水&交易持仓：扣减可用T+0信用保护凭证'名义本金'
        //交易持仓流水&交易持仓：增加可用T+1信用保护凭证'名义本金'
        //交易持仓流水&交易持仓：扣减实际T+1信用保护凭证'名义本金'

        //指令持仓流水&指令持仓
        List<EtrSecPositionFlow> etrSecPositionFlows = new ArrayList<>();
        //交易持仓流水&交易持仓
        List<PtlSecPositionFlow> ptlSecPositionFlows = new ArrayList<>();

        List<CreditProtectionCertificatePeriodExpireDataDto> expireDatas = writerDto.getExpireDatas();
        for (CreditProtectionCertificatePeriodExpireDataDto expireData : expireDatas) {
            //指令持仓流水
            EtrSecPositionFlow etrSecPositionFlow = new EtrSecPositionFlow();
            etrSecPositionFlow.setSeqno(IDGenerator.generate());
            etrSecPositionFlow.setPortfolioId(expireData.getPortfolioId());
            etrSecPositionFlow.setSecAcctId(expireData.getSecAcctId());
            etrSecPositionFlow.setFinprodId(expireData.getFinprodId());
            etrSecPositionFlow.setBranch(InterfCommonConstant.分支序号_默认);
            etrSecPositionFlow.setInvAim(null);
            etrSecPositionFlow.setHodingType(DictCodeList.持仓类型_实际.getValue());
            etrSecPositionFlow.setCdate(iPlatformDate.getSysDate());
            etrSecPositionFlow.setAmount(isRevoke ? expireData.getRepNomCapi() : expireData.getRepNomCapi().negate());
            etrSecPositionFlow.setpFinprodId(null);
            etrSecPositionFlow.setTradeId(expireData.getTradeId());
            etrSecPositionFlow.setOffsetId(expireData.getTradeId());
            etrSecPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
            etrSecPositionFlow.setCreateUser("SYSTEM");
            etrSecPositionFlow.setCreateTime(iPlatformDate.getSysDate());
            etrSecPositionFlow.setUpdateTime(DateUtil.now());
            etrSecPositionFlow.setUpdateUser("SYSTEM");
            etrSecPositionFlow.setCreateDept("dept");
            etrSecPositionFlow.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            etrSecPositionFlows.add(etrSecPositionFlow);

            //交易持仓流水
            PtlSecPositionFlow ptlSecPositionFlow = new PtlSecPositionFlow();
            ptlSecPositionFlow.setSeqno(IDGenerator.generate());
            ptlSecPositionFlow.setPortfolioId(expireData.getPortfolioId());
            ptlSecPositionFlow.setSecAcctId(expireData.getSecAcctId());
            ptlSecPositionFlow.setFinprodId(expireData.getFinprodId());
            ptlSecPositionFlow.setBranch(InterfCommonConstant.分支序号_默认);
            ptlSecPositionFlow.setInvAim(null);
            ptlSecPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
            ptlSecPositionFlow.setCdate(iPlatformDate.getSysDate());
            ptlSecPositionFlow.setAmount(isRevoke ? expireData.getRepNomCapi() : expireData.getRepNomCapi().negate());
            ptlSecPositionFlow.setpFinprodId(null);
            ptlSecPositionFlow.setTradeId(expireData.getTradeId());
            ptlSecPositionFlow.setOffsetId(expireData.getTradeId());
            ptlSecPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
            ptlSecPositionFlow.setCreateUser("SYSTEM");
            ptlSecPositionFlow.setCreateTime(iPlatformDate.getSysDate());
            ptlSecPositionFlow.setUpdateTime(DateUtil.now());
            ptlSecPositionFlow.setUpdateUser("SYSTEM");
            ptlSecPositionFlow.setCreateDept("dept");
            ptlSecPositionFlow.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            ptlSecPositionFlows.add(ptlSecPositionFlow);

            PtlSecPositionFlow ptlSecPositionFlow2 = new PtlSecPositionFlow();
            BeanUtils.copyProperties(ptlSecPositionFlow, ptlSecPositionFlow2);
            ptlSecPositionFlow2.setSeqno(IDGenerator.generate());
            ptlSecPositionFlow2.setCdate(nextWorkDate);
            ptlSecPositionFlow2.setAmount(isRevoke ? expireData.getRepNomCapi().negate() : expireData.getRepNomCapi());
            ptlSecPositionFlows.add(ptlSecPositionFlow2);

            PtlSecPositionFlow ptlSecPositionFlow3 = new PtlSecPositionFlow();
            BeanUtils.copyProperties(ptlSecPositionFlow, ptlSecPositionFlow3);
            ptlSecPositionFlow3.setSeqno(IDGenerator.generate());
            ptlSecPositionFlow3.setHodingType(DictCodeList.持仓类型_实际.getValue());
            ptlSecPositionFlow3.setCdate(nextWorkDate);
            ptlSecPositionFlow2.setAmount(isRevoke ? expireData.getRepNomCapi() : expireData.getRepNomCapi().negate());
            ptlSecPositionFlows.add(ptlSecPositionFlow3);
        }
        for (PtlSecPositionFlow ptlSecPositionFlow : ptlSecPositionFlows) {
            ptlSecPositionFlowMapper.insertSelective(ptlSecPositionFlow);
        }
        for (EtrSecPositionFlow etrSecPositionFlow : etrSecPositionFlows) {
            etrSecPositionFlowMapper.insertSelective(etrSecPositionFlow);
        }
    }


    private void sendNewPosition(CreditProtectionCertificatePeriodWriterDto writerDto) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("decisionAct", 系统动作_新建.getValue());
        resultMap.put("systemAct", 系统动作_新建.getValue());
        resultMap.put("flowStatus", DictCodeListExt3.流程节点_审批生效.getValue());

        resultMap.put(INTERFACE_TYPE, "CPC001");
        resultMap.put("userId", "SYSTEM");
//        resultMap.put("computerId","");
        resultMap.put(TOTAL_TRADE_ID, writerDto.getBussinessCode());
        resultMap.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());
        resultMap.put("balanceCheck", "N");
        resultMap.put("busiType", DictCodeListExt3.头寸业务类型_信用保护凭证交易.getValue());
        resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_交易登记.getValue());
        resultMap.put(PS, DictCodeList.交易方向_到期.getValue());

        List<CreditProtectionCertificatePeriodExpireDataDto> expireDatas = writerDto.getExpireDatas();
        resultMap.put(TRADE_PLACE, expireDatas.get(0).getTradeMarket());

        resultMap.put("cDate", DateUtil.format(iPlatformDate.getSysDate()));
        resultMap.put(CCY, DictCodeList.币种_人民币_CNY.getValue());

        List<Map<String, Object>> ptlList = new ArrayList<>();

        for (CreditProtectionCertificatePeriodExpireDataDto expireDataDto : expireDatas) {
            Map<String, Object> ptl = new HashMap<>();
            ptl.put("portfolioId", expireDataDto.getPortfolioId());
            ptl.put("finprodId", expireDataDto.getFinprodId());
            ptl.put("tradeId", expireDataDto.getTradeId());
            ptl.put("cashAcctId", expireDataDto.getAcctNo());
            ptl.put("secAcctId", expireDataDto.getSecAcctId());
            ptl.put("secAmount", expireDataDto.getRepNomCapi());
            ptl.put("cashAmount", BigDecimal.ZERO);
            ptlList.add(ptl);
        }
        resultMap.put("ptlList", ptlList);
        PositionHttpRequest.send(resultMap);
    }

    public void doRevokePositionForNew(String busiCode) {
        PositionHttpRequest.dorevoke(busiCode, DictCodeListExt1.子系统代码_交易.getValue(), DictCodeListExt3.头寸业务类型_信用保护凭证交易.getValue(), DictCodeList.交易方向_到期.getValue());
    }

    public void doRevokePositionForOld(String busiCode) {
        List<CreditProtectionCertificatePeriodExpireDataDto> details = creditProtectionCertificatePeriodRunnerMapper.selectDetailsByTotleTradeId(busiCode);
        if (!details.isEmpty()) {
            String calendarId;

            if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(details.get(0).getTradeMarket())) {
                calendarId = CacheUtil.getSysParam("STL_DOWNDATA_DEAL_SHJYS_CALID");
            } else {
                calendarId = CacheUtil.getSysParam("STL_DOWNDATA_DEAL_SZJYS_CALID");
            }
            // 下一个交易日
            Date nextWorkDate = getNextWorkDay(iPlatformDate.getSysDate(), calendarId);
            CreditProtectionCertificatePeriodWriterDto writerDto = new CreditProtectionCertificatePeriodWriterDto();
            writerDto.setBussinessCode(busiCode);
            writerDto.setExpireDatas(details);
            dealOldPosition(writerDto, nextWorkDate, true);
        }
    }

}
