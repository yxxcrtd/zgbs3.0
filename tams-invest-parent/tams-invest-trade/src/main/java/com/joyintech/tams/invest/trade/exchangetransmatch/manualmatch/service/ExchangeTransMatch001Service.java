package com.joyintech.tams.invest.trade.exchangetransmatch.manualmatch.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.SettleTransDataTotalAddMapper;
import com.joyintech.tams.common.base.dao.SettleTransDataTotalMapper;
import com.joyintech.tams.common.base.entity.SettleTransDataTotal;
import com.joyintech.tams.common.base.entity.SettleTransDataTotalAdd;
import com.joyintech.tams.common.base.entity.SettleTransDataTotalMatch;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.service.BatchManualExcuteService;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.trade.bondtostock.dto.TrdBondToStock001Dto;
import com.joyintech.tams.invest.trade.conbond.dto.TrdConBond003Dto;
import com.joyintech.tams.invest.trade.conbond.dto.TrdConBondDto;
import com.joyintech.tams.invest.trade.cstp.dto.CstpSelectDto;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.ExchangeTransMatchCommonService;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bond.ExchangeTransMatchBondCommonService;
import com.joyintech.tams.invest.trade.exchangetransmatch.manualmatch.dto.*;
import com.joyintech.tams.invest.trade.fund.dto.TrdFundInvestTradeDto;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTrade002Dto;
import com.joyintech.tams.invest.trade.trdstockconnect.controller.TrdStockConnect002Controller;
import com.joyintech.tams.invest.trade.trdstockconnect.dto.TrdStockConnectDto;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(rollbackFor=Exception.class)
public class ExchangeTransMatch001Service extends CommonExportService<ExchangeTransMatch004Dto>{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ExchangeTransMatchCommonService exchangeTransMatchCommonService;

    @Autowired
    private ExchangeTransMatchBondCommonService exchangeTransMatchBondCommonService;

    @Autowired
    private SettleTransDataTotalMapper settleTransDataTotalMapper;

    @Autowired
    private SettleTransDataTotalAddMapper settleTransDataTotalAddMapper;

    @Autowired
    private BatchManualExcuteService batchManualExcuteService;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    /**
     * 获取资产代码下拉框
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodId(Map<String,Object> map){
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME
                + "/ExchangeTransMatchController/getFinprodId", map, SelectOptionDto.class);
    }

    /**
     * 获取交易编号
     * @param map 查询参数
     * @return 下拉框集合
     * @return
     * @see
     * @since 1.0
     */

    public Page<SelectOptionDto> getBusiCode(Map<String,Object> map){
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME
                + "/ExchangeTransMatchController/getBusiCode", map, SelectOptionDto.class);
    }

    /**
     * 申请编号
     * @param map
     * @return
     */
    public Page<SelectOptionDto> getApplyNo(Map<String,Object> map){
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME
                + "/ExchangeTransMatchController/getApplyNo", map, SelectOptionDto.class);
    }

    /**
     * 获取证券账户
     * @param map
     * @return
     */
    public Page<SelectOptionDto> getSecAcctNo(Map<String,Object> map){
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME
                + "/ExchangeTransMatchController/getSecAcctNo", map, SelectOptionDto.class);
    }

    /**
     * 获取成交编号
     * @param map
     * @return
     */
    public Page<SelectOptionDto> getDealId(Map<String,Object> map){
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME
                + "/ExchangeTransMatchController/getDealId", map, SelectOptionDto.class);
    }


    /**
     * 分页查询成交数据
     * @param map 查询参数
     * @return 分页查询结果
     * @see
     * @since 1.0
     */
    public Page<ExchangeTransMatch002Dto> findPage(Map<String,Object> map){
        if(StringUtil.isNotEmpty(map.get("applyNo"))) {
            String applyNo = (String)map.get("applyNo");
            map.put("applyNoList",StringUtil.splitToList(GlobalConstant.COMMA, applyNo));
        }

        if(StringUtil.isNotEmpty(map.get("ps"))) {
            String ps = (String)map.get("ps");
            if(StringUtil.equals(ps,DictCodeListExt3.交易方向_正回购.getValue())
            || StringUtil.equals(ps,DictCodeListExt3.交易方向_逆回购.getValue()) ) {
                // GNS-959:交易方向不必特殊处理 modify by fuyongsheng 20220227 start
                //map.put("ps",DictCodeList.交易方向_买入.getValue());
                // GNS-959:交易方向不必特殊处理 modify by fuyongsheng 20220227 end
                map.put("finprodType2",StringUtil.equals(ps,DictCodeListExt3.交易方向_正回购.getValue())?DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue():DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            }

            if(StringUtil.equals(ps,DictCodeListExt3.交易方向_到期购回.getValue())) {
                map.put("ps",DictCodeList.交易方向_到期.getValue());
            }
            else if(StringUtil.equals(ps, DictCodeListExt3.交易方向_提前购回.getValue())){
                map.put("ps",DictCodeListExt3.交易方向_投资端_提前终止.getValue());
            }
        }


        Page<ExchangeTransMatch002Dto> page = restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME
                + "/ExchangeTransMatchController/findPage", map, ExchangeTransMatch002Dto.class);

        return page;
    }

    /**
     * 分页查询委托交易数据
     * @param map 查询参数
     * @return 分页查询结果
     * @see
     * @since 1.0
     */
    public Page<ExchangeTransMatch003Dto> findEntrustPage(Map<String,Object> map){
        if(StringUtil.isNotEmpty(map.get("portfolioId"))) {
            String portfolioId = (String)map.get("portfolioId");
            map.put("portfolioIdList",StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }

        if(StringUtil.isNotEmpty(map.get("finprodType2"))) {
            String finprodType2 = (String)map.get("finprodType2");
            map.put("finprodType2List",StringUtil.splitToList(GlobalConstant.COMMA, finprodType2));
        }

        Page<ExchangeTransMatch003Dto> page = restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME
                + "/ExchangeTransMatchController/findEntrustPage", map, ExchangeTransMatch003Dto.class);
        return page;
    }

    /**
     * 分页查询
     * @param map 查询参数
     * @return 分页查询结果
     * @see
     * @since 1.0
     */
    public Page<ExchangeTransMatch004Dto> findTransMatchInfoPage(Map<String,Object> map){

        if(StringUtil.isNotEmpty(map.get("portfolioId"))) {
            String portfolioId = (String)map.get("portfolioId");
            map.put("portfolioIdList",StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }

//        if(StringUtil.isNotEmpty(map.get("dealId"))) {
//            String dealId = (String)map.get("dealId");
//            map.put("dealIdList",StringUtil.splitToList(GlobalConstant.COMMA, dealId));
//        }
        if(StringUtil.isNotEmpty(map.get("ps"))) {
            String ps = (String)map.get("ps");
            if(StringUtil.equals(ps,DictCodeListExt3.交易方向_正回购.getValue())
                    || StringUtil.equals(ps,DictCodeListExt3.交易方向_逆回购.getValue()) ) {
                map.put("ps",DictCodeList.交易方向_买入.getValue());
                map.put("finprodType2_hg",StringUtil.equals(ps,DictCodeListExt3.交易方向_正回购.getValue())?DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue():DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            }
        }
        Page<ExchangeTransMatch004Dto> page = restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME
                + "/ExchangeTransMatchController/findTransMatchInfoPage", map, ExchangeTransMatch004Dto.class);

        return page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<ExchangeTransMatch004Dto> selectPageData(Map<String, Object> map) {
        return findTransMatchInfoPage(map);
    }

    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
                "/com/joyintech/tams/invest/trade/exchangetransmatch/manualmatch/conf/export_conf.xml").dataSource(DataSourceType.MASERT);
    }

    /**
     * 处理导出参数，格式化导出的格式
     * @param beanName beanName
     * @param cell 单元格
     * @param propName 配置
     * @param value 值
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(StringUtil.equals("tradeDate", propName)) {
            cell.setCellValue(DateUtil.format((Date)value));
        }
    }


    /**
     * 成交匹配
     * @param exchangeTransMatch002Dto
     */
    public void transactionMacth(ExchangeTransMatch002Dto exchangeTransMatch002Dto){
        if(StringUtil.isEmptyList(exchangeTransMatch002Dto.getEntrustList())) {
            return;
        }

        ExchangeTransMatchCommonDto exchangeTransMatchCommonDto = new ExchangeTransMatchCommonDto();
        BeanUtil.copyPropertiesIgnoreNull(exchangeTransMatchCommonDto,exchangeTransMatch002Dto);
        List<String> entrustIdList = new ArrayList<>();
        if(!StringUtil.isEmptyList(exchangeTransMatch002Dto.getEntrustList())) {
            exchangeTransMatch002Dto.getEntrustList().stream().forEach(item ->{
                entrustIdList.add(item.getEntrustId());
            });
        }
        exchangeTransMatchCommonDto.setEntrustIdList(entrustIdList);

        //调用成交匹配共通方法
        exchangeTransMatchCommonDto.setMatchType(DictCodeListExt3.下行交易匹配类型_手动匹配.getValue());
        exchangeTransMatchCommonService.transactionMacth(exchangeTransMatchCommonDto);

        Integer unFinishMatchNum = trdCommonBaseService.getUnFinishMatchSettleTransDataNum(exchangeTransMatchCommonDto.getTradeDate());
        logger.debug("查询当日未完全成交匹配数量:"+ unFinishMatchNum);
        if(unFinishMatchNum == 0){
            Map<String, Object> param = new HashMap<>();
            param.put("beanNames", Collections.singletonList("stlDownDurationDataGatherTask"));
            logger.debug("当日无未完全成交匹配数据,启动存续期交易生成批处理任务,日期:" + DateUtil.format(exchangeTransMatchCommonDto.getTradeDate()));
            batchManualExcuteService.execute(param);
        }
    }

    /**
     * 取消匹配
     * @param dto
     */
    public void revokeTransactionMacth(ExchangeTransMatch004Dto dto) {
        for(ExchangeTransMatch004Dto item: dto.getSelectItem()) {
            SettleTransDataTotalMatch settleTransDataTotalMatch = new SettleTransDataTotalMatch();
            settleTransDataTotalMatch.setSeqNo(item.getSeqId());
            settleTransDataTotalMatch.setEntrunstTradeId(item.getBusiCode());
            settleTransDataTotalMatch.setMatchClass(item.getMatchType());
            settleTransDataTotalMatch.setMatchType(item.getSecType());

            exchangeTransMatchCommonService.revokeTransactionMacth(settleTransDataTotalMatch);
        }
    }

    /**
     * 生成交易录单数据
     * @param dto
     * @return
     */
    public String genTransactionRecord(ExchangeTransMatch002Dto dto) {
        String dtoStr = null;
        SettleTransDataTotal settleTransDataTotal = settleTransDataTotalMapper.selectByPrimaryKey(dto.getSeqId());
        SettleTransDataTotalAdd settleTransDataTotalAdd = settleTransDataTotalAddMapper.selectByPrimaryKey(dto.getSeqId());
        if(StringUtil.isNotEmpty(settleTransDataTotal)) {
            if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_配债.getValue())) {
                    TrdBondInvestTradeDto trdBondInvestTradeDto = new TrdBondInvestTradeDto();
                    BeanUtil.copyPropertiesIgnoreNull(trdBondInvestTradeDto,settleTransDataTotal);

                    trdBondInvestTradeDto.setDealSqeId(settleTransDataTotal.getSeqId());
                    trdBondInvestTradeDto.setIsCancel(DictCodeList.是否_否.getValue());
                    trdBondInvestTradeDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                    trdBondInvestTradeDto.setIsOutTrade(DictCodeList.是否_是.getValue());
                    trdBondInvestTradeDto.setTradeAmt(dto.getRemainAmount());
                    trdBondInvestTradeDto.setShareAmt(dto.getRemainNum());
                    trdBondInvestTradeDto.setSecManageAcctId(dto.getSecAcctId());
                    trdBondInvestTradeDto.setOwnTradeAmt(ArithmeticUtil.multiply(dto.getRemainNum(),new BigDecimal(100)));

                    if(StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_配债.getValue())) {
                        trdBondInvestTradeDto.setPs(DictCodeList.交易方向_买入.getValue());
                    }

                    TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
                    if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                        trdBondInvestTradeDto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
                        trdInvestPersonInfo.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
                    }
                    else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                        trdBondInvestTradeDto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
                        trdInvestPersonInfo.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
                    }
                    trdInvestPersonInfo.setTrader(LoginUserUtil.getUserName());
                    trdBondInvestTradeDto.setTrdInvestPersonInfo(trdInvestPersonInfo);
                    dtoStr = JsonUtils.object2Json(trdBondInvestTradeDto);
                }
                else if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
                    TrdConBondDto trdConBondDto = new TrdConBondDto();
                    BeanUtil.copyPropertiesIgnoreNull(trdConBondDto,settleTransDataTotal);
                    trdConBondDto.setDealSqeId(settleTransDataTotal.getSeqId());
                    trdConBondDto.setIsCancel(DictCodeList.是否_否.getValue());
                    trdConBondDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                    trdConBondDto.setIsOutTrade(DictCodeList.是否_是.getValue());
                    trdConBondDto.setTradeAmt(dto.getRemainAmount());
                    trdConBondDto.setShareAmt(dto.getRemainNum());

                    if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                        trdConBondDto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
                    }
                    else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                        trdConBondDto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
                    }


                    TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
                    trdInvestPersonInfo.setCounterId(trdConBondDto.getCounterId());
                    trdInvestPersonInfo.setTrader(LoginUserUtil.getUserName());
                    trdConBondDto.setTrdInvestPersonInfo(trdInvestPersonInfo);

                    dtoStr = JsonUtils.object2Json(trdConBondDto);
                }
                else if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
                    TrdConBond003Dto trdConBond003Dto = new TrdConBond003Dto();
                    BeanUtil.copyPropertiesIgnoreNull(trdConBond003Dto,settleTransDataTotal);
                    trdConBond003Dto.setDealSqeId(settleTransDataTotal.getSeqId());
                    trdConBond003Dto.setIsCancel(DictCodeList.是否_否.getValue());
                    trdConBond003Dto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                    trdConBond003Dto.setIsOutTrade(DictCodeList.是否_是.getValue());
                    trdConBond003Dto.setbTradeId(exchangeTransMatchBondCommonService.selectBTradeId(settleTransDataTotal.getApplyTradeId(),settleTransDataTotal.getPs()));
                    trdConBond003Dto.setTradeAmt(dto.getRemainAmount());
                    trdConBond003Dto.setShareAmt(dto.getRemainNum());

                    if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                        trdConBond003Dto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
                    }
                    else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                        trdConBond003Dto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
                    }

                    TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
                    trdInvestPersonInfo.setCounterId(trdConBond003Dto.getCounterId());
                    trdInvestPersonInfo.setTrader(LoginUserUtil.getUserName());
                    trdConBond003Dto.setTrdInvestPersonInfo(trdInvestPersonInfo);

                    dtoStr = JsonUtils.object2Json(trdConBond003Dto);
                }
                else if(StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_债转股.getValue())) {
                    if(StringUtil.isNotEmpty(settleTransDataTotalAdd)) {
                        TrdBondToStock001Dto trdBondToStock001Dto = new TrdBondToStock001Dto();

                        BeanUtil.copyPropertiesIgnoreNull(trdBondToStock001Dto,settleTransDataTotal);
                        trdBondToStock001Dto.setDealSqeId(settleTransDataTotal.getSeqId());
                        trdBondToStock001Dto.setIsCancel(DictCodeList.是否_否.getValue());
                        trdBondToStock001Dto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                        trdBondToStock001Dto.setIsOutTrade(DictCodeList.是否_是.getValue());
                        trdBondToStock001Dto.setConShareId(settleTransDataTotalAdd.getrFinprodId());
//                        trdBondToStock001Dto.setConvdate(settleTransDataTotal.getVdate());
                        trdBondToStock001Dto.setConNum(settleTransDataTotalAdd.getConAmt());
                        trdBondToStock001Dto.setConPrice(settleTransDataTotalAdd.getConPrice());
                        trdBondToStock001Dto.setTradeAmt(dto.getRemainAmount());
                        // GZYHTZJY-1140:债转股带入的“转股券面总额”有误 modify fuyongsheng 20220115 start
                        // trdBondToStock001Dto.setShareAmt(dto.getRemainNum());
                        trdBondToStock001Dto.setShareAmt(ArithmeticUtil.divide(dto.getRemainNum(),new BigDecimal(100L), 4));
                        // GZYHTZJY-1140:债转股带入的“转股券面总额”有误 modify fuyongsheng 20220115 end
                        if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                            trdBondToStock001Dto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
                        }
                        else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                            trdBondToStock001Dto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
                        }

                        TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
                        trdInvestPersonInfo.setCounterId(trdBondToStock001Dto.getCounterId());
                        trdInvestPersonInfo.setTrader(LoginUserUtil.getUserName());
                        trdBondToStock001Dto.setTrdInvestPersonInfo(trdInvestPersonInfo);

                        dtoStr = JsonUtils.object2Json(trdBondToStock001Dto);
                    }
                }
                else if(StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券入库.getValue())
                || StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券出库.getValue())){
                    //TODO 页面待开发
                }
                else if(StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_配债.getValue())) {
                    //TODO 页面待开发
                }

            }
            else if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                    || StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                TrdRepoTrade002Dto trdRepoTrade002Dto =new TrdRepoTrade002Dto();
                BeanUtil.copyPropertiesIgnoreNull(trdRepoTrade002Dto,settleTransDataTotal);
                trdRepoTrade002Dto.setDealSqeId(settleTransDataTotal.getSeqId());

                //期限品种
                trdRepoTrade002Dto.setTermTypeReback(settleTransDataTotal.getTermType());
                //回购利率
                trdRepoTrade002Dto.setRebackIntAmt(settleTransDataTotal.getIntAmt());
                //回购类型
                trdRepoTrade002Dto.setRepoType(settleTransDataTotalAdd.getRepoType());
                trdRepoTrade002Dto.setIsCancel(DictCodeList.是否_否.getValue());
                trdRepoTrade002Dto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                trdRepoTrade002Dto.setIsOutTrade(DictCodeList.是否_是.getValue());
                trdRepoTrade002Dto.setTradeAmt(dto.getRemainAmount());
                trdRepoTrade002Dto.setPrinAmt(dto.getRemainAmount());
                trdRepoTrade002Dto.setRebackPrice(ArithmeticUtil.multiply(dto.getUnitCprice(),new BigDecimal(100)));
                trdRepoTrade002Dto.setShareAmt(dto.getRemainNum());
                trdRepoTrade002Dto.setCommissionAmt(dto.getCommissionAmt());
                trdRepoTrade002Dto.setTradeAmtAndFee(ArithmeticUtil.subtract(trdRepoTrade002Dto.getPrinAmt(),trdRepoTrade002Dto.getCommissionAmt()));
                if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                    trdRepoTrade002Dto.setPs(DictCodeList.回购拆借方向_正回购.getValue());
                }
                else {
                    trdRepoTrade002Dto.setPs(DictCodeList.回购拆借方向_逆回购.getValue());
                }

                if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                    trdRepoTrade002Dto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
                }
                else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                    trdRepoTrade002Dto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
                }

                TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
                trdInvestPersonInfo.setCounterId(trdRepoTrade002Dto.getCounterId());
                trdInvestPersonInfo.setTrader(LoginUserUtil.getUserName());
                trdRepoTrade002Dto.setTrdInvestPersonInfo(trdInvestPersonInfo);

                dtoStr = JsonUtils.object2Json(trdRepoTrade002Dto);
            }
            else if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
                TrdFundInvestTradeDto trdFundInvestTradeDto =new TrdFundInvestTradeDto();
                BeanUtil.copyPropertiesIgnoreNull(trdFundInvestTradeDto,settleTransDataTotal);
                trdFundInvestTradeDto.setIsCancel(DictCodeList.是否_否.getValue());
                trdFundInvestTradeDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                trdFundInvestTradeDto.setSecManageAcctId(settleTransDataTotal.getSecAcctId());
                trdFundInvestTradeDto.setDealSqeId(settleTransDataTotal.getSeqId());
                trdFundInvestTradeDto.setFinprodMarketId(dto.getFinprodMarketId());
                trdFundInvestTradeDto.setTradeAmt(dto.getRemainAmount());
                trdFundInvestTradeDto.setShareAmt(dto.getRemainNum());
                trdFundInvestTradeDto.setCommissionFee(dto.getCommissionAmt());

                if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                    trdFundInvestTradeDto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
                }
                else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                    trdFundInvestTradeDto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
                }

                TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
                trdInvestPersonInfo.setCounterId(trdFundInvestTradeDto.getCounterId());
                trdInvestPersonInfo.setTrader(LoginUserUtil.getUserName());
                trdFundInvestTradeDto.setTrdInvestPersonInfo(trdInvestPersonInfo);

                dtoStr = JsonUtils.object2Json(trdFundInvestTradeDto);
            }
            else if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                TrdStockConnectDto trdStockConnectDto = new TrdStockConnectDto();
                BeanUtil.copyPropertiesIgnoreNull(trdStockConnectDto, settleTransDataTotal);
                trdStockConnectDto.setDealSqeId(settleTransDataTotal.getSeqId());
                trdStockConnectDto.setIsCancel(DictCodeList.是否_否.getValue());
                trdStockConnectDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                trdStockConnectDto.setIsOutTrade(DictCodeList.是否_是.getValue());
                trdStockConnectDto.setTradeAmt(dto.getRemainAmount());
                trdStockConnectDto.setShareAmt(dto.getRemainNum());

                if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                    trdStockConnectDto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
                }
                else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                    trdStockConnectDto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
                }

                TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
                trdInvestPersonInfo.setCounterId(trdStockConnectDto.getCounterId());
                trdInvestPersonInfo.setTrader(LoginUserUtil.getUserName());
                trdStockConnectDto.setTrdInvestPersonInfo(trdInvestPersonInfo);

                dtoStr = JsonUtils.object2Json(trdStockConnectDto);
            }
        }
        return dtoStr;
    }

    public List<ExchangeTransMatch003Dto> findEntrustList(String seqNo) {
       return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/ExchangeTransMatchController/findEntrustList", seqNo, ExchangeTransMatch003Dto.class);
    }

    public List<ExchangeTransMatch002Dto> findDealList(String seqNo) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/ExchangeTransMatchController/findDealList", seqNo, ExchangeTransMatch002Dto.class);
    }
}
