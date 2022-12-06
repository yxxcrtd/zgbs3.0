package com.joyintech.service.basicdata.dayendaccount.service;

import com.codingapi.txlcn.common.util.Maps;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.basicdata.dayendaccount.constant.StlDataGatherConstant;
import com.joyintech.service.basicdata.dayendaccount.dao.DayEndAccountMapper;
import com.joyintech.service.basicdata.dayendaccount.dto.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.reconciliation.dao.ReconciliationCommonMapper;
import com.joyintech.tams.invest.common.reconciliation.dto.AccountFeePlan;
import com.joyintech.tams.invest.common.reconciliation.dto.AccountPlanSearchInfo;
import com.joyintech.tams.invest.common.reconciliation.dto.DayEndAccountDaichuliDto;
import com.joyintech.tams.invest.common.reconciliation.service.ReconciliationCommonService;
import com.joyintech.tams.invest.common.service.BatchManualExcuteService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chengzhong
 */
@Service("backDayEndAccountService")
public class DayEndAccountService extends BaseService<DayEndAccountDto>{
    /**
     * 定义mapper(mine)
     */
    @Autowired
    private DayEndAccountMapper dayEndAccountMapper;

    @Autowired
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;

    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private StlSecReconMapper stlSecReconMapper;

    @Autowired
    private SettleTransDataMapper settleTransDataMapper;

    @Autowired
    private SettleTransDataStatusMapper settleTransDataStatusMapper;

    @Autowired
    private SettleTransDataAddMapper settleTransDataAddMapper;

    @Autowired
    private ReconciliationCommonService reconciliationCommonService;

    @Autowired
    private ReconciliationCommonMapper reconciliationCommonMapper;

    @Autowired
    private BatchManualExcuteService batchManualExcuteService;

    /**
     * 平台日获取接口
     */
    @Autowired(required=false)
    private IPlatformDate iPlatformDate;

    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     * @param pageParam pageParam
     * @return Page类  Page
     * @throws Exception 异常信息
     */
    public Page<DayEndAccountDto> selectForPage(int page, int length, Map<String, Object> pageParam)
        throws Exception {
        Object accountObj = pageParam.get("account");
        if (accountObj != null) {
            pageParam.put("accountList", StringUtil.splitToList(GlobalConstant.COMMA, (String)accountObj));
        }
        else {
            pageParam.put("accountList", new ArrayList<>());
        }
        Object finprodIdObj = pageParam.get("finprodId");
        if (StringUtil.isNotEmpty(finprodIdObj) && StringUtil.isNotBlank((String)finprodIdObj)) {
            List<String> finprodMarketIdList = StringUtil.splitToList(GlobalConstant.COMMA, (String)finprodIdObj);
            Map<String, Object> finProdIdParam = new HashMap<>();
            finProdIdParam.put("finprodMarketIdList", finprodMarketIdList);
            List<String> finprodList = dayEndAccountMapper.queryFinProdIds(finProdIdParam);
            pageParam.put("finprodIdList",finprodList);
        }
        else {
            pageParam.put("finprodIdList", new ArrayList<>());
        }
        String sysDate = iPlatformDate.getSysDateStr();
        pageParam.put("sysDate", sysDate);
        pageParam.put("PARA_CODE_02", DictCodeListStl.参数代码_对账维度.getValue());
        pageParam.put("PARA_VALUE_01", DictCodeListStl.对账维度_证券汇总.getValue());
        pageParam.put("PARA_VALUE_02", DictCodeListStl.对账维度_委托汇总.getValue());
        pageParam.put("PARA_VALUE_03", DictCodeListStl.对账维度_成交明细.getValue());
        pageParam.put("PARA_CODE_03", DictCodeListStl.参数代码_佣金对账方式.getValue());
        pageParam.put("PARA_VALUE_QYJ", DictCodeListStl.佣金对账方式_全佣金.getValue());
        pageParam.put("PARA_VALUE_JYJ", DictCodeListStl.佣金对账方式_净佣金.getValue());
        pageParam.put("CHECK_RESULT_2", DictCodeListExt3.对账状态结果_待处理.getValue());
        pageParam.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        pageParam.put("PS_T07", DictCodeList.交易方向_到期.getValue());
        pageParam.put("PS_T54", DictCodeListExt3.交易方向_投资端_提前终止.getValue());
        pageParam.put("PS_T55", DictCodeListExt3.交易方向_投资端_逾期返售.getValue());
        pageParam.put("PS_T56", DictCodeListExt3.交易方向_到期续做.getValue());
        pageParam.put("PS_T95", DictCodeListExt3.交易方向_到期购回.getValue());
        pageParam.put("PS_T96", DictCodeListExt3.交易方向_提前购回.getValue());
        pageParam.put("PS_T49", DictCodeListExt3.交易方向_债转股.getValue());
        pageParam.put("prodType2_F07", DictCodeList.平安资产类型_质押式回购.getValue());
        pageParam.put("prodType2_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("prodType2_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        pageParam.put("prodType2_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        pageParam.put("BUSI_SCENE_CALSS_0902RS03", DictCodeListStl.业务场景分类明细_债券协议正回购_到期.getValue());
        pageParam.put("BUSI_SCENE_CALSS_0902RB01", DictCodeListStl.业务场景分类明细_债券协议逆回购_首期_买入.getValue());
        pageParam.put("BUSI_SCENE_CALSS_0902RB04", DictCodeListStl.业务场景分类明细_债券协议逆回购_到期续作.getValue());
        pageParam.put("TRADE_MARKET_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        pageParam.put("TRADE_MARKET_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        Page<DayEndAccountDto> result = super.queryForPage(page, length, () -> dayEndAccountMapper.findAccountInfo(pageParam));
        for(DayEndAccountDto dto:result.getRoot()){
            if(StringUtil.isNotEmpty(dto.getFinprodId())){
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
                if(finProduct == null){
                    // 标的表不存在回购资产,市场代码统一使用金融代码
                    dto.setFinprodMarketId(dto.getFinprodId());
                    continue;
                }
                dto.setFinprodMarketId(finProduct.getFinprodMarketId());
                dto.setFinprodName(finProduct.getFinprodAbbr());
            }
        }
        return result;
    }

    public Page<SelectOptionDto> queryAssetCode(int page, int length,
                                                  Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        param.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        return queryForPageForSelectReturn(page, length,
                () -> dayEndAccountMapper.queryAssetCode(param));
    }

    public AccountResult qryAccount(List<DayEndAccountDto> dtoList) {
        AccountResult result = new AccountResult();
        //对账前check
        if (!checkAccount(result, dtoList)) {
            return result;
        }
        //处理对账逻辑
        chuliAccount(dtoList);
        //触发下一步的批处理任务
        triggerNextBatchTask();
        return result;
    }

    /**
     * 触发下一步的批处理任务
     */
    private void triggerNextBatchTask(){
        Map<String, Object> param = new HashMap<>();
        param.put("CHECK_RESULT_0", DictCodeListExt3.对账状态结果_不一致.getValue());
        param.put("CHECK_RESULT_2", DictCodeListExt3.对账状态结果_待处理.getValue());
        param.put("execDate", DateUtil.format(iPlatformDate.getSysDate(),"yyyyMMdd"));
        // 查询对账结果不一致的数量
        int count = reconciliationCommonMapper.queryCheckDisaccordCount(param);
        if(count == 0){
            param.put("beanNames", Arrays.asList("stlSettleTransDataGatherTask","stlDownDataGatherTask","stlDownDurationDataGatherTask"));

            batchManualExcuteService.execute(param);
        }
    }

    private boolean checkAccount(AccountResult result, List<DayEndAccountDto> dtoList) {
        Map<String, Object> param = new HashMap<String, Object>();
        if(CollectionUtils.isEmpty(dtoList)){
            result.setMessage("请选择对账记录");
            return false;
        }
        for(DayEndAccountDto dto:dtoList){
            if(!DictCodeListExt3.对账状态结果_待处理.getValue().equals(dto.getCheckResult())
                    && !DictCodeListExt3.对账状态结果_不一致.getValue().equals(dto.getCheckResult())){
                result.setMessage("请选择状态为不一致和待处理的对账记录");
                return false;
            }
        }
        return true;
    }

    /**
     * 处理对账
     * @param dtoList
     */
    private void chuliAccount(List<DayEndAccountDto> dtoList) {
        if(CollectionUtils.isEmpty(dtoList)){
            return;
        }
        for(DayEndAccountDto dto : dtoList){
            // 查询成交明细数据
            List<SettleTransData> settleTransDatas = querySettleTransDatas(dto);
            // 更新成交明细数据
            updateSettleTransDatas(dto, settleTransDatas);
            // 更新成交明细状态表
            updateSettleTransDataStatus(dto, settleTransDatas);
            // 更新券商对账单结果表
            StlSecRecon stlSecRecon = new StlSecRecon();
            stlSecRecon.setSeqNo(dto.getSeqNo());
            stlSecRecon.setCheckResult(DictCodeListExt3.对账状态结果_手工完成.getValue());
            stlSecReconMapper.updateByPrimaryKeySelective(stlSecRecon);
        }
    }

    /**
     * 查询成交明细数据
     * @param dto
     * @return
     */
    private List<SettleTransData> querySettleTransDatas(DayEndAccountDto dto){
        List<SettleTransData> settleTransDatas = new ArrayList<>();
        if(DictCodeListStl.对账维度_委托汇总.getValue().equals(dto.getStlSecDeme())){
            //调用【清算明细查询】
            Map<String, Object> param = new HashMap<>();
            param.put("applyTradeId", dto.getEntrustTradeId());
            param.put("dealDate", dto.getSettleDate());
            param.put("seatNumber", dto.getSeatNumber());
            settleTransDatas = reconciliationCommonMapper.getSettleTransData(param);
        }else if(DictCodeListStl.对账维度_证券汇总.getValue().equals(dto.getStlSecDeme())){
            //调用【清算明细查询】
            Map<String, Object> param = new HashMap<>();
            param.put("finprodId", dto.getFinprodId());
            param.put("dealDate", dto.getSettleDate());
            param.put("secAcctId", dto.getSecAcctId());
            param.put("seatNumber", dto.getSeatNumber());
            param.put("ps", dto.getPs());
            settleTransDatas = reconciliationCommonMapper.getSettleTransData(param);
        }else if(DictCodeListStl.对账维度_成交明细.getValue().equals(dto.getStlSecDeme())){
            //调用【清算明细查询】
            Map<String, Object> param = new HashMap<>();
            if(StringUtil.isNotEmpty(dto.getTradeId())){ // 有券商对账单
                param.put("cjbh", dto.getTradeId());
            }else { // 无券商对账单
                param.put("seqNo", dto.getSeqNo());
                List<StlSecReconDetail> details = reconciliationCommonMapper.queryStlSecReconDetails(param);
                if(CollectionUtils.isNotEmpty(details)){
                    param.put("tradeId", details.get(0).getTradeId());
                }
            }
            param.put("dealDate", dto.getSettleDate());
            settleTransDatas = reconciliationCommonMapper.getSettleTransData(param);
        }
        return settleTransDatas;
    }

    /**
     * 更新成交明细数据
     * @param dto
     * @param settleTransDatas
     */
    private void updateSettleTransDatas(DayEndAccountDto dto, List<SettleTransData> settleTransDatas){
        if(CollectionUtils.isEmpty(settleTransDatas)){
            return;
        }
        if(DictCodeListExt3.对账状态结果_不一致.getValue().equals(dto.getCheckResult())){
            boolean isModify = false;
            // 判断成交明细数据是否有人工修改
            for(SettleTransData settleTransData : settleTransDatas){
                if(DictCodeListExt1.是否_是.getValue().equals(settleTransData.getIsModify())){
                    isModify = true;
                }
            }
            if(!isModify){ // // 如果成交明细无人工修改,则用券商对账单数据更新成交明细数据
                if(DictCodeListStl.对账维度_委托汇总.getValue().equals(dto.getStlSecDeme())
                        || DictCodeListStl.对账维度_证券汇总.getValue().equals(dto.getStlSecDeme())){
                    // 处理误差值
                    DayEndAccountDaichuliDto daichuliDto = covertToDaichuliDto(dto);
                    reconciliationCommonService.chuliWucha(daichuliDto);
                    // 对账维度为证券维度,并且成交明细数量大于1的要将误差进行拆分
                    if(DictCodeListStl.对账维度_证券汇总.getValue().equals(dto.getStlSecDeme()) && settleTransDatas.size() > 1){
                        // 获取分配误差Map
                        Map<String, DayEndAccountDaichuliDto> ceMap = reconciliationCommonService.distributeSettleTransDataCe(daichuliDto, settleTransDatas);
                        for(SettleTransData settleTransData : settleTransDatas){
                            DayEndAccountDaichuliDto daichuli = ceMap.get(settleTransData.getTradeId());
                            // 重新结算交易明细数据
                            SettleTransData std = reconciliationCommonService.getReCalSettleTransData(settleTransData, daichuli);
                            // 更新成交明细数据
                            settleTransDataMapper.updateByPrimaryKeySelective(std);
                        }
                    }
                    else{
                        // GZYHTZJY-1106: 协议回购到期续作,更新首期的成交明细 modify fuyongsheng 20220112 start
                        SettleTransData updateSettleTransData = settleTransDatas.get(0);
                        // 协议回购到期续作更新成交明细更新首期
                        if(DictCodeListExt3.交易方向_到期续做.getValue().equals(settleTransDatas.get(0).getPs())){
                            for(SettleTransData settleTransData : settleTransDatas){
                                if(DictCodeListStl.业务场景分类明细_债券协议逆回购_首期_买入.getValue().equals(settleTransData.getBusiSceneCalss())
                                        || DictCodeListStl.业务场景分类明细_债券协议正回购_首期_买入.getValue().equals(settleTransData.getBusiSceneCalss())){
                                    updateSettleTransData = settleTransData;
                                }
                            }
                        }
                        // GZYHTZJY-1106: 协议回购到期续作,更新首期的成交明细 modify fuyongsheng 20220112 end
                        // 重新结算交易明细数据
                        SettleTransData std = reconciliationCommonService.getReCalSettleTransData(updateSettleTransData,daichuliDto);
                        settleTransDataMapper.updateByPrimaryKeySelective(std);
                    }
                }
                else if(DictCodeListStl.对账维度_成交明细.getValue().equals(dto.getStlSecDeme())){
                    DayEndAccountDaichuliDto daichuliDto = covertToDaichuliDto(dto);
                    reconciliationCommonService.chuliWucha(daichuliDto);
                    for(SettleTransData settleTransData : settleTransDatas){
                        SettleTransData std = reconciliationCommonService.getReCalSettleTransData(settleTransData,daichuliDto);
                        settleTransDataMapper.updateByPrimaryKeySelective(std);
                    }
                }
            }
        }
    }

    /**
     * 数据转换
     * @param dto
     * @return
     */
    private DayEndAccountDaichuliDto covertToDaichuliDto(DayEndAccountDto dto){
        DayEndAccountDaichuliDto daichuliDto = new DayEndAccountDaichuliDto();
        daichuliDto.setSeqNo(dto.getSeqNo());
        daichuliDto.setPs(dto.getPs());
        daichuliDto.setSecAcctId(dto.getSecAcctId());
        daichuliDto.setFinprodId(dto.getFinprodId());
        daichuliDto.setTradeMarket(dto.getTradeMarket());
        daichuliDto.setFinprodType(dto.getFinprodType());
        daichuliDto.setCustomerId(dto.getCustomerId());
        daichuliDto.setJsfAmt(dto.getJsfAmt()); // 经手费
        daichuliDto.setJsfAmt2(dto.getJsfAmt2()); // 清算经手费
        daichuliDto.setZgfAmt(dto.getZgfAmt()); // 证管费
        daichuliDto.setZgfAmt2(dto.getZgfAmt2()); // 清算证管费
        daichuliDto.setSettleFee(dto.getSettleFee()); // 结算费
        daichuliDto.setSettleFee2(dto.getSettleFee2()); // 清算结算费
        daichuliDto.setGhfAmt(dto.getGhfAmt()); // 过户费
        daichuliDto.setGhfAmt2(dto.getGhfAmt2()); // 清算过户费
        daichuliDto.setSxfAmt(dto.getSxfAmt()); // 手续费
        daichuliDto.setSxfAmt2(dto.getSxfAmt2()); // 清算手续费
        daichuliDto.setCommissionAmt(dto.getCommissionAmt()); // 佣金
        daichuliDto.setCommissionAmt2(dto.getCommissionAmt2()); // 清算佣金
        daichuliDto.setStampFee(dto.getStampFee()); // 印花税
        daichuliDto.setStampFee2(dto.getStampFee2()); // 清算印花税
        daichuliDto.setTradeAmt(dto.getTradeAmt()); // 成交金额
        daichuliDto.setTradeAmt2(dto.getTradeAmt2()); // 清算成交金额
        daichuliDto.setTradeNum(dto.getTradeNum()); // 成交数量
        daichuliDto.setTradeNum2(dto.getTradeNum2()); // 清算成交数量
        daichuliDto.setParaValue1(dto.getParaValue1()); // 佣金对账方式参数
        daichuliDto.setTradeDate(dto.getSettleDate()); // 清算日期
        daichuliDto.setIsPayWithTrade(dto.getIsPayWithTrade()); // 是否随交易支付
        return daichuliDto;
    }

    /**
     * 更新成交明细状态表
     * @param dto
     * @param settleTransDatas
     */
    private void updateSettleTransDataStatus(DayEndAccountDto dto, List<SettleTransData> settleTransDatas){
        for(SettleTransData settleTransData : settleTransDatas){
            if(DictCodeListExt3.对账状态结果_不一致.getValue().equals(dto.getCheckResult())){
                // 更新成交明细数据状态表
                SettleTransDataStatus status = new SettleTransDataStatus();
                status.setTradeId(settleTransData.getTradeId());
                status.setCheckResult(DictCodeListExt3.对账状态结果_手工完成.getValue());
                settleTransDataStatusMapper.updateByPrimaryKeySelective(status);
            }else if(DictCodeListExt3.对账状态结果_待处理.getValue().equals(dto.getCheckResult())){
                settleTransDataStatusMapper.deleteByPrimaryKey(settleTransData.getTradeId());
                // 插入成交明细数据状态表
                SettleTransDataStatus status = new SettleTransDataStatus();
                status.setTradeId(settleTransData.getTradeId());
                status.setCheckResult(DictCodeListExt3.对账状态结果_手工完成.getValue());
                settleTransDataStatusMapper.insertSelective(status);
            }
        }
    }

    public DayEndAccount002Dto getReconDealInfo(Map<String, Object> param) {
        DayEndAccount002Dto dto = new DayEndAccount002Dto();
        //获取券商单数据
        String seqNo = (String) param.get("seqNo");
        // ZGZGST-4203:修改查询券商对账单方式 fuyongsheng modify 20211215 start
        // StlSecReconDeal stlSecReconDeal = stlSecReconDealMapper.selectByPrimaryKey(seqNo);
        StlSecReconDeal stlSecReconDeal = dayEndAccountMapper.getStlSecReconDeal(seqNo);
        // 佣金对账方式
        String stlSecReconPara3 = "QYJ";
        if (Objects.nonNull(stlSecReconDeal)) {
            Map<String, Object> paramMap = Maps.of("customerId", stlSecReconDeal.getCustomerId());
            paramMap.put("sysDate", DateUtil.getSysDate());
            List<String> stlSecReconPara3List = dayEndAccountMapper.getStlSecReconPara3(paramMap);
            stlSecReconPara3 = CollectionUtils.isNotEmpty(stlSecReconPara3List) ? stlSecReconPara3List.get(0) : "QYJ";
        }
        // ZGZGST-4203:修改查询券商对账单方式 fuyongsheng modify 20211215 end
        List<DayEndAccountDto> reconDealInfoList = new ArrayList<DayEndAccountDto>();
        //费用加减标识
        BigDecimal stampFeeFlg = BigDecimal.ZERO;
        BigDecimal jsfAmtFlg = BigDecimal.ZERO;
        BigDecimal ghfAmtFlg = BigDecimal.ZERO;
        BigDecimal zgfAmtFlg = BigDecimal.ZERO;
        BigDecimal sxfAmtFlg = BigDecimal.ZERO;
        BigDecimal settleFeeFlg = BigDecimal.ZERO;
        BigDecimal commissionAmtFlg = BigDecimal.ZERO;

        if(null != stlSecReconDeal){
            DayEndAccountDto reconDealInfo = new DayEndAccountDto();
            reconDealInfo.setSeqNo(stlSecReconDeal.getSeqNo());
            reconDealInfo.setAccount(stlSecReconDeal.getAccout());
            reconDealInfo.setPortfolioId(stlSecReconDeal.getPortfolioId());
            reconDealInfo.setSettleDate(stlSecReconDeal.getTradeDate());
            reconDealInfo.setFinprodId(stlSecReconDeal.getMarketId());
            reconDealInfo.setTradeMarket(stlSecReconDeal.getTradeMarket());
            reconDealInfo.setFinprodType(stlSecReconDeal.getFinprodType());
            reconDealInfo.setCommissionAmt(stlSecReconDeal.getCommissionAmt());
            reconDealInfo.setNetCommissionAmt(stlSecReconDeal.getNetCommissionAmt());
            reconDealInfo.setJsfAmt(stlSecReconDeal.getJsfAmt());
            reconDealInfo.setZgfAmt(stlSecReconDeal.getZgfAmt());
            reconDealInfo.setStampFee(stlSecReconDeal.getStampFee());
            reconDealInfo.setSettleFee(stlSecReconDeal.getSettleFee());
            reconDealInfo.setSxfAmt(stlSecReconDeal.getSxfAmt());
            reconDealInfo.setGhfAmt(stlSecReconDeal.getGhfAmt());
            reconDealInfo.setTradeAmt(stlSecReconDeal.getTradeAmt());
            reconDealInfo.setTradeNum(stlSecReconDeal.getTradeNum());
            reconDealInfo.setCommissionType(stlSecReconPara3);
            reconDealInfoList.add(reconDealInfo);
        }

        List<SettleTransData> listSettleTransData = dayEndAccountMapper.selectAllSettleTransData(param);
        List<DayEndAccountDto> dealDetailList = new ArrayList<DayEndAccountDto>();

        if (listSettleTransData != null && listSettleTransData.size() > 0) {
            for (SettleTransData settleTransData : listSettleTransData) {
                DayEndAccountDto dealDetail = new DayEndAccountDto();
                dealDetail.setFinprodType2(settleTransData.getFinprodType2());
                dealDetail.setFinprodId(settleTransData.getFinprodId());
                dealDetail.setPs(settleTransData.getPs());
                dealDetail.setCjbh(settleTransData.getCjbh());
                dealDetail.setCommissionAmt(settleTransData.getCommissionAmt());
                dealDetail.setNetCommissionAmt(settleTransData.getNetCommissionAmt());
                dealDetail.setJsfAmt(settleTransData.getJsfAmtRaw());
                dealDetail.setZgfAmt(settleTransData.getZgfAmtRaw());
                dealDetail.setStampFee(settleTransData.getStampFeeRaw());
                dealDetail.setSettleFee(settleTransData.getSettleFeeRaw());
                dealDetail.setSxfAmt(settleTransData.getSxfAmtRaw());
                dealDetail.setGhfAmt(settleTransData.getGhfAmtRaw());
                dealDetail.setTradeAmt(settleTransData.getFpriceAmt());
                dealDetail.setTradeNum(settleTransData.getShareAmt());
                dealDetail.setTradeId(settleTransData.getTradeId());
                dealDetail.setIsPayWithTrade(settleTransData.getIsPayWithTrade());

                String isPay = settleTransData.getIsPayWithTrade();
                //费用信息.是否随交易支付 = 是 and 费用信息.费用类型 = 佣金
                if (DictCodeListExt1.是否_是.getValue().equals(isPay)) {
                    //if 待对账数据.交易方向 = 买入 or 正回购
                    if (DictCodeList.交易方向_买入.getValue().equals(settleTransData.getPs())
                            || DictCodeList.交易方向_申购.getValue().equals(settleTransData.getPs())
                            || DictCodeList.回购拆借方向_逆回购.getValue().equals(settleTransData.getPs())) {
                        commissionAmtFlg = BigDecimal.ONE;
                    } else if (DictCodeList.交易方向_卖出.getValue().equals(settleTransData.getPs())
                            || DictCodeList.交易方向_行权_回售.getValue().equals(settleTransData.getPs())
                            || DictCodeList.回购拆借方向_正回购.getValue().equals(settleTransData.getPs())) {
                        commissionAmtFlg = BigDecimal.ONE.negate();
                    }
                }
                if (DictCodeList.交易方向_买入.getValue().equals(settleTransData.getPs())
                        || DictCodeList.回购拆借方向_逆回购.getValue().equals(settleTransData.getPs())
                        || DictCodeList.交易方向_申购.getValue().equals(settleTransData.getPs())) {
                    jsfAmtFlg = BigDecimal.ONE;
                } else if (DictCodeList.交易方向_卖出.getValue().equals(settleTransData.getPs())
                        || DictCodeList.交易方向_行权_回售.getValue().equals(settleTransData.getPs())
                        || DictCodeList.回购拆借方向_正回购.getValue().equals(settleTransData.getPs())) {
                    jsfAmtFlg = BigDecimal.ONE.negate();
                }

                if (DictCodeList.交易方向_买入.getValue().equals(settleTransData.getPs())
                        || DictCodeList.交易方向_申购.getValue().equals(settleTransData.getPs())
                        || DictCodeList.回购拆借方向_逆回购.getValue().equals(settleTransData.getPs())) {
                    zgfAmtFlg = BigDecimal.ONE;
                } else if (DictCodeList.交易方向_卖出.getValue().equals(settleTransData.getPs())
                        || DictCodeList.交易方向_行权_回售.getValue().equals(settleTransData.getPs())
                        || DictCodeList.回购拆借方向_正回购.getValue().equals(settleTransData.getPs())) {
                    zgfAmtFlg = BigDecimal.ONE.negate();
                }

                if (DictCodeList.交易方向_买入.getValue().equals(settleTransData.getPs())
                        || DictCodeList.交易方向_申购.getValue().equals(settleTransData.getPs())
                        || DictCodeList.回购拆借方向_逆回购.getValue().equals(settleTransData.getPs())) {
                    stampFeeFlg = BigDecimal.ONE;
                } else if (DictCodeList.交易方向_卖出.getValue().equals(settleTransData.getPs())
                        || DictCodeList.交易方向_行权_回售.getValue().equals(settleTransData.getPs())
                        || DictCodeList.回购拆借方向_正回购.getValue().equals(settleTransData.getPs())) {
                    stampFeeFlg = BigDecimal.ONE.negate();
                }

                if (DictCodeList.交易方向_买入.getValue().equals(settleTransData.getPs())
                        || DictCodeList.交易方向_申购.getValue().equals(settleTransData.getPs())
                        || DictCodeList.回购拆借方向_逆回购.getValue().equals(settleTransData.getPs())) {
                    settleFeeFlg = BigDecimal.ONE;
                } else if (DictCodeList.交易方向_卖出.getValue().equals(settleTransData.getPs())
                        || DictCodeList.交易方向_行权_回售.getValue().equals(settleTransData.getPs())
                        || DictCodeList.回购拆借方向_正回购.getValue().equals(settleTransData.getPs())) {
                    settleFeeFlg = BigDecimal.ONE.negate();
                }

                if (DictCodeList.交易方向_买入.getValue().equals(settleTransData.getPs())
                        || DictCodeList.交易方向_申购.getValue().equals(settleTransData.getPs())
                        || DictCodeList.回购拆借方向_逆回购.getValue().equals(settleTransData.getPs())) {
                    ghfAmtFlg = BigDecimal.ONE;
                } else if (DictCodeList.交易方向_卖出.getValue().equals(settleTransData.getPs())
                        || DictCodeList.交易方向_行权_回售.getValue().equals(settleTransData.getPs())
                        || DictCodeList.回购拆借方向_正回购.getValue().equals(settleTransData.getPs())) {
                    ghfAmtFlg = BigDecimal.ONE.negate();
                }

                if (DictCodeList.交易方向_买入.getValue().equals(settleTransData.getPs())
                        || DictCodeList.交易方向_申购.getValue().equals(settleTransData.getPs())
                        || DictCodeList.回购拆借方向_逆回购.getValue().equals(settleTransData.getPs())) {
                    sxfAmtFlg = BigDecimal.ONE;
                } else if (DictCodeList.交易方向_卖出.getValue().equals(settleTransData.getPs())
                        || DictCodeList.交易方向_行权_回售.getValue().equals(settleTransData.getPs())
                        || DictCodeList.回购拆借方向_正回购.getValue().equals(settleTransData.getPs())) {
                    sxfAmtFlg = BigDecimal.ONE.negate();
                }

                //费用标识，没有0，增加1，扣减-1
                dealDetail.setCommissionAmtFlg(commissionAmtFlg);
                dealDetail.setJsfAmtFlg(jsfAmtFlg);
                dealDetail.setZgfAmtFlg(zgfAmtFlg);
                dealDetail.setStampFeeFlg(stampFeeFlg);
                dealDetail.setSettleFeeFlg(settleFeeFlg);
                dealDetail.setSxfAmtFlg(sxfAmtFlg);
                dealDetail.setGhfAmtFlg(ghfAmtFlg);
                //隐藏用于记录调整值字段
                if(stlSecReconPara3.equals("QYJ")){
                    dealDetail.setCommissionAmt2(settleTransData.getCommissionAmt());
                }else{
                    dealDetail.setCommissionAmt2(settleTransData.getNetCommissionAmt());
                }
                dealDetail.setJsfAmt2(settleTransData.getJsfAmt());
                dealDetail.setZgfAmt2(settleTransData.getZgfAmt());
                dealDetail.setStampFee2(settleTransData.getStampFee());
                dealDetail.setSettleFee2(settleTransData.getSettleFee());
                dealDetail.setSxfAmt2(settleTransData.getSxfAmt());
                dealDetail.setGhfAmt2(settleTransData.getGhfAmt());
                dealDetail.setCommissionType(stlSecReconPara3);
                dealDetailList.add(dealDetail);
            }
        }
        // GNS-847:资产类型优先取明细数据的 modify by fuyongsheng 20220215 start
        if(CollectionUtils.isNotEmpty(reconDealInfoList) && CollectionUtils.isNotEmpty(dealDetailList)){
            reconDealInfoList.forEach(deal->deal.setFinprodType(dealDetailList.get(0).getFinprodType2()));
        }
        // GNS-847:资产类型优先取明细数据的 modify by fuyongsheng 20220215 e
        dto.setReconDealInfoList(reconDealInfoList);
        dto.setDealDetailList(dealDetailList);
        return dto;
    }

    public DayEndAccount002Dto getDealDetailInfo(Map<String, Object> param) {
        DayEndAccount002Dto dto = new DayEndAccount002Dto();
        //费用加减标识
        BigDecimal stampFeeFlg = BigDecimal.ZERO;
        BigDecimal jsfAmtFlg = BigDecimal.ZERO;
        BigDecimal ghfAmtFlg = BigDecimal.ZERO;
        BigDecimal zgfAmtFlg = BigDecimal.ZERO;
        BigDecimal sxfAmtFlg = BigDecimal.ZERO;
        BigDecimal settleFeeFlg = BigDecimal.ZERO;
        BigDecimal commissionAmtFlg = BigDecimal.ZERO;
        List<SettleTransData> listSettleTransData = dayEndAccountMapper.selectSettleTransData(param);
        List<DayEndAccountDto> dealDetailList = new ArrayList<DayEndAccountDto>();
        if (listSettleTransData != null && listSettleTransData.size() > 0) {
            for (SettleTransData settleTransData : listSettleTransData) {
                DayEndAccountDto dealDetail = new DayEndAccountDto();
                dealDetail.setCommissionAmt(settleTransData.getCommissionAmt());
                dealDetail.setNetCommissionAmt(settleTransData.getNetCommissionAmt());
                dealDetail.setCjbh(settleTransData.getCjbh());
                dealDetail.setPs(settleTransData.getPs());
                dealDetail.setJsfAmt(settleTransData.getJsfAmtRaw());
                dealDetail.setZgfAmt(settleTransData.getZgfAmtRaw());
                dealDetail.setStampFee(settleTransData.getStampFeeRaw());
                dealDetail.setSettleFee(settleTransData.getSettleFeeRaw());
                dealDetail.setSxfAmt(settleTransData.getSxfAmtRaw());
                dealDetail.setGhfAmt(settleTransData.getGhfAmtRaw());
                dealDetail.setTradeAmt(settleTransData.getFpriceAmt());
                dealDetail.setTradeNum(settleTransData.getShareAmt());
                dealDetail.setTradeId(settleTransData.getTradeId());
                //费用标识，没有0，增加1，扣减-1
                dealDetail.setCommissionAmtFlg(commissionAmtFlg);
                dealDetail.setJsfAmtFlg(jsfAmtFlg);
                dealDetail.setZgfAmtFlg(zgfAmtFlg);
                dealDetail.setStampFeeFlg(stampFeeFlg);
                dealDetail.setSettleFeeFlg(settleFeeFlg);
                dealDetail.setSxfAmtFlg(sxfAmtFlg);
                dealDetail.setGhfAmtFlg(ghfAmtFlg);
                //隐藏用于记录调整值字段
                dealDetail.setCommissionAmt2(settleTransData.getCommissionAmt());
                dealDetail.setJsfAmt2(settleTransData.getJsfAmtRaw());
                dealDetail.setZgfAmt2(settleTransData.getZgfAmtRaw());
                dealDetail.setStampFee2(settleTransData.getStampFeeRaw());
                dealDetail.setSettleFee2(settleTransData.getSettleFeeRaw());
                dealDetail.setSxfAmt2(settleTransData.getSxfAmtRaw());
                dealDetail.setGhfAmt2(settleTransData.getGhfAmtRaw());
                dealDetail.setTradeAmt2(BigDecimal.ZERO);
                dealDetailList.add(dealDetail);
            }
        }
        dto.setDealDetailList(dealDetailList);
        return dto;
    }

    /**
     * 实现docomplement方法
     */
    public void doComplete(DayEndAccount002Dto entity, String type) {
        //更新明细
        if (entity.getDealDetailList() !=null && entity.getDealDetailList().size() > 0) {
            for (DayEndAccountDto dto : entity.getDealDetailList()) {
                SettleTransData data = new SettleTransData();
                if (DictCodeListStl.佣金对账方式_全佣金.getValue().equals(dto.getCommissionType())) {
                    data.setCommissionAmt(dto.getCommissionAmt() == null ? BigDecimal.ZERO : dto.getCommissionAmt());
                } else {
                    data.setNetCommissionAmt(dto.getCommissionAmt() == null ? BigDecimal.ZERO : dto.getCommissionAmt());
                    BigDecimal commissionAmt = dto.getCommissionAmt();
                    if(null == dto.getJsfAmt2()){
                        commissionAmt = ArithmeticUtil.doAdd(commissionAmt, dto.getJsfAmt());
                    }
                    if(null == dto.getZgfAmt2()){
                        commissionAmt = ArithmeticUtil.doAdd(commissionAmt, dto.getZgfAmt());
                    }
                    if(null == dto.getStampFee2()){
                        commissionAmt = ArithmeticUtil.doAdd(commissionAmt, dto.getStampFee());
                    }
                    if(null == dto.getSettleFee2()){
                        commissionAmt = ArithmeticUtil.doAdd(commissionAmt, dto.getSettleFee());
                    }
                    if(null == dto.getGhfAmt2()){
                        commissionAmt = ArithmeticUtil.doAdd(commissionAmt, dto.getGhfAmt());
                    }
                    data.setCommissionAmt(commissionAmt);
                }
                data.setJsfAmt(null == dto.getJsfAmt2() ? null : dto.getJsfAmt());
                data.setZgfAmt(null == dto.getZgfAmt2() ? null :dto.getZgfAmt());
                data.setStampFee(null == dto.getStampFee2() ? null :dto.getStampFee());
                data.setSettleFee(null == dto.getSettleFee2() ? null :dto.getSettleFee());
                data.setGhfAmt(null == dto.getGhfAmt2() ? null :dto.getGhfAmt());
                data.setJsfAmtRaw(dto.getJsfAmt());
                data.setZgfAmtRaw(dto.getZgfAmt());
                data.setStampFeeRaw(dto.getStampFee());
                data.setSettleFeeRaw(dto.getSettleFee());
                data.setGhfAmtRaw(dto.getGhfAmt());

                data.setShareAmt(dto.getTradeNum() == null ? BigDecimal.ZERO : dto.getTradeNum());
                data.setFpriceAmt(dto.getTradeAmt() == null ? BigDecimal.ZERO : dto.getTradeAmt());
                data.setTradeAmt(dto.getTradeAmt() == null ? BigDecimal.ZERO : dto.getTradeAmt());
                data.setIsPayWithTrade(dto.getIsPayWithTrade());
                if(DictCodeListExt1.是否_是.getValue().equals(data.getIsPayWithTrade())){
                    if(dto.getCommissionAmtFlg() != null && dto.getCommissionAmtFlg().compareTo(BigDecimal.ZERO) > 0){
                        data.setTradeAmt(ArithmeticUtil.doAdd(data.getTradeAmt(), data.getCommissionAmt()));
                    }
                    else if(dto.getCommissionAmtFlg() != null && dto.getCommissionAmtFlg().compareTo(BigDecimal.ZERO) < 0){
                        data.setTradeAmt(ArithmeticUtil.subtract(data.getTradeAmt(), data.getCommissionAmt()));
                    }
                }

                if(dto.getJsfAmt2() != null){
                    if(dto.getJsfAmtFlg() != null && dto.getJsfAmtFlg().compareTo(BigDecimal.ZERO) > 0){
                        data.setTradeAmt(ArithmeticUtil.doAdd(data.getTradeAmt(), dto.getJsfAmt()));
                    }
                    else if(dto.getJsfAmtFlg() != null && dto.getJsfAmtFlg().compareTo(BigDecimal.ZERO) < 0){
                        data.setTradeAmt(ArithmeticUtil.subtract(data.getTradeAmt(), dto.getJsfAmt()));
                    }
                }

                if(dto.getZgfAmt2() != null){
                    if(dto.getZgfAmtFlg() != null && dto.getZgfAmtFlg().compareTo(BigDecimal.ZERO) > 0){
                        data.setTradeAmt(ArithmeticUtil.doAdd(data.getTradeAmt(), dto.getZgfAmt()));
                    }
                    else if(dto.getZgfAmtFlg() != null && dto.getZgfAmtFlg().compareTo(BigDecimal.ZERO) < 0){
                        data.setTradeAmt(ArithmeticUtil.subtract(data.getTradeAmt(), dto.getZgfAmt()));
                    }
                }

                if(dto.getStampFee2() != null){
                    if(dto.getStampFeeFlg() != null && dto.getStampFeeFlg().compareTo(BigDecimal.ZERO) > 0){
                        data.setTradeAmt(ArithmeticUtil.doAdd(data.getTradeAmt(), dto.getStampFee()));
                    }
                    else if(dto.getStampFeeFlg() != null && dto.getStampFeeFlg().compareTo(BigDecimal.ZERO) < 0){
                        data.setTradeAmt(ArithmeticUtil.subtract(data.getTradeAmt(), dto.getStampFee()));
                    }
                }

                if(dto.getSettleFee2() != null){
                    if(dto.getSettleFeeFlg() != null && dto.getSettleFeeFlg().compareTo(BigDecimal.ZERO) > 0){
                        data.setTradeAmt(ArithmeticUtil.doAdd(data.getTradeAmt(), dto.getSettleFee()));
                    }
                    else if(dto.getSettleFeeFlg() != null && dto.getSettleFeeFlg().compareTo(BigDecimal.ZERO) < 0){
                        data.setTradeAmt(ArithmeticUtil.subtract(data.getTradeAmt(), dto.getSettleFee()));
                    }
                }

                if(dto.getGhfAmt2() != null){
                    if(dto.getGhfAmtFlg() != null && dto.getGhfAmtFlg().compareTo(BigDecimal.ZERO) > 0){
                        data.setTradeAmt(ArithmeticUtil.doAdd(data.getTradeAmt(), dto.getGhfAmt()));
                    }
                    else if(dto.getGhfAmtFlg() != null && dto.getGhfAmtFlg().compareTo(BigDecimal.ZERO) < 0){
                        data.setTradeAmt(ArithmeticUtil.subtract(data.getTradeAmt(), dto.getGhfAmt()));
                    }
                }

                data.setTradeId(dto.getTradeId());
                SettleTransData settleTransData = settleTransDataMapper.selectByPrimaryKey(data.getTradeId());
                data.setIntAmt(ArithmeticUtil.subtract(data.getFpriceAmt(), settleTransData.getCpriceAmt()));
                data.setIsModify(DictCodeListExt1.是否_是.getValue());
                if (DictCodeListStl.佣金对账方式_净佣金.getValue().equals(dto.getCommissionType())) {
                    // 判断净佣金是否被修改
                    if(dto.getCommissionAmt() != null && dto.getCommissionAmt().equals(dto.getCommissionAmt2())){
                        // 如果净佣金未被修改,则将全佣金字段设置null,为了后续sql不更新该字段
                        data.setCommissionAmt(null);
                    }
                }
                settleTransDataMapper.updateByPrimaryKeySelective(data);
            }
        }
    }

    public Page<SelectOptionDto> selectCustomerId(int page, int length,Map<String, Object> param) {
        return super.queryForPageForSelectReturn(page, length, () -> dayEndAccountMapper.selectCustomerId(param));
    }

    public Page<SelectOptionDto> queryBusiAcctNo(int page, int length,Map<String, Object> param) {
        return super.queryForPageForSelectReturn(page, length, () -> dayEndAccountMapper.queryBusiAcctNo(param));
    }

    public Page<DayEndAccountDto> selectPage(int page, int length, Map<String, Object> param) {
        Object busiAcctNo = param.get("busiAcctNo");
        param.put("finProdType2_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        Page<DayEndAccountDto> result;
        if (busiAcctNo != null) {
            param.put("busiAcctNoList",
                    StringUtil.splitToList(GlobalConstant.COMMA, (String)busiAcctNo));
        }
        else {
            param.put("busiAcctNoList", new ArrayList<>());
        }
        // 新老头寸开关(Y:新头寸,N:老头寸)
        String newSecPositionSwitch = CacheUtil.getSysParam("NEW_SEC_POSITION_SWITCH");

        if(StringUtil.isNotEmpty(param.get("parentId")) && !StringUtil.equals(param.get("parentId"),"root")) {
            String param1 = (String) param.get("parentId");
            String[] s = param1.split(",");
            if(!"null".equals(s[0])){
                Page page1 = new Page();
                page1.setRoot(new ArrayList());
                return page1;
            }

            if("1".equals(s[1])){
                param.put("busiAcctNo",s[2]);
                param.put("settleDate",s[3]);
                param.put("ccy",s[4]);
                // 开关配置N查老头存
                if(DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)) {
                    return super.queryForPage(page, length, () -> dayEndAccountMapper.selectPage1(param));
                }else{// 默认查新头寸
                    param.put("cash_hodingType", DictCodeListExt3.资产类型_现金账户持仓.getValue());
                    param.put("cash_hodingType_C_11", DictCodeListExt3.资产类型_可用中间值.getValue());
                    param.put("cash_hodingType_C_12", DictCodeListExt3.资产类型_可取中间值.getValue());
                    param.put("dateType", StlDataGatherConstant.DATE_TYPE_T_0);
                    return super.queryForPage(page, length, () -> dayEndAccountMapper.selectPageNew1(param));
                }
            }else{
                param.put("finprodId",s[2]);
                param.put("settleDate",s[3]);
                param.put("secAcctId",s[4]);
                if(StringUtil.isNotEmpty(s[5])&&!"null".equals(s[5])){
                    param.put("termType",s[5]);
                }
                if(DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)) {
                    return  super.queryForPage(page, length, () -> dayEndAccountMapper.selectPage2(param));
                }else{// 默认查新头寸
                    param.put("hodingType", DictCodeListExt3.资产类型_资产账户持仓.getValue());
                    param.put("dateType", StlDataGatherConstant.DATE_TYPE_T_0);
                    return  super.queryForPage(page, length, () -> dayEndAccountMapper.selectPageNew2(param));
                }
            }
        }
        // 开关配置N查老头存
        if(DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)) {
            return super.queryForPage(page, length, () -> dayEndAccountMapper.selectPage(param));
        }else{// 默认查新头寸
            param.put("cash_hodingType_C_11", DictCodeListExt3.资产类型_可用中间值.getValue());
            param.put("cash_hodingType_C_12", DictCodeListExt3.资产类型_可取中间值.getValue());
            param.put("hodingType", DictCodeListExt3.资产类型_资产账户持仓.getValue());
            param.put("dateType", StlDataGatherConstant.DATE_TYPE_T_0);
            return super.queryForPage(page, length, () -> dayEndAccountMapper.selectPageNew(param));
        }
    }

    private void setFinprodMarketId(Page<DayEndAccountDto> page){
        for(DayEndAccountDto dto:page.getRoot()){
            if(StringUtil.isNotEmpty(dto.getFinprodId())){
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
                dto.setFinprodMarketId(finProduct.getFinprodMarketId());
            }
        }
    }

    public Page<SelectOptionDto> selectSecAcctId(int page, int length,Map<String, Object> param) {
        return super.queryForPageForSelectReturn(page, length, () -> dayEndAccountMapper.selectSecAcctId(param));
    }
}
