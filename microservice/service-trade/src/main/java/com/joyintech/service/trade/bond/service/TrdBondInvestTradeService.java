/*
 * 文件名：TrdBondInvestTradeService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.trade.bond.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.service.trade.bond.dao.TrdBondInvestTradeMapper;
import com.joyintech.service.trade.bond.dto.SecCpriceInfoDto;
import com.joyintech.service.trade.bond.dto.TrdBondInvestTradeCSTPDto;
import com.joyintech.service.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.service.trade.bond.dto.TrdBondInvestTradePdfDto;
import com.joyintech.service.trade.cstp.dao.CstpSelectMapper;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.cstp.EtrEntrustExt;
import com.joyintech.tams.invest.common.pagemenusplit.PageMenuSplitUtil;
import com.joyintech.tams.invest.common.trade.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 债券交易Service
 *
 * @author Zane.Y
 * @version 1.0 2018年9月7日
 * @see TrdBondInvestTradeService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
public class TrdBondInvestTradeService extends BaseService {


    @Autowired
    private TrdBondInvestTradeMapper trdBondInvestTradeMapper;
    @Autowired
    private IPlatformDate pPlatformDate;

    /**
     * 投资委托基础组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * FinProductMapper
     */
    @Autowired(required = false)
    private FinProductMapper finProductMapper;

    /**
     * PtlSecValutionMapper
     */
    @Autowired
    private PtlSecValutionMapper ptlSecValutionMapper;

    /**
     * 费用汇总
     */
    @Autowired
    private TrdTradeFeeTotalMapper trdTradeFeeTotalMapper;

    @Autowired
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;

    @Autowired(required = false)
    private InterfTradSecdealMapper interfTradSecdealMapper;

    @Autowired
    private CstpSelectMapper cstpSelectMapper;

    /**
     * 分页查询
     *
     * @param page 页编号
     * @param length 页条数
     * @param pageParam 画面参数
     * @return 查询结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<TrdBondInvestTradeDto> selectForPage(int page, int length,
                                                     Map<String, Object> pageParam)
    {
        pageParam.put("VALID_STATUS_01", DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("VALID_STATUS_02", DictCodeListExt1.有效状态_待处理.getValue());
        pageParam.put("VALID_STATUS_03", DictCodeListExt1.有效状态_撤销.getValue());
        pageParam.put("YES_NO_YES", DictCodeList.是否_是.getValue());
        pageParam.put("YES_NO_NO", DictCodeList.是否_否.getValue());
        pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("PS_T75", DictCodeListExt3.交易方向_清理核销.getValue());
        pageParam.put("BUSI_TYPE_T75", DictCodeListExt3.共通业务类型_清理核销.getValue());

        // 一体化对应etliu start
        List<String> psList = new ArrayList<String>();
        psList.add(DictCodeList.交易方向_买入.getValue());
        psList.add(DictCodeList.交易方向_卖出.getValue());
        psList.add(DictCodeList.交易方向_行权_回售.getValue());
        psList.add(DictCodeList.交易方向_行权_赎回.getValue());
        psList.add(DictCodeListExt3.交易方向_清理核销.getValue());
        pageParam.put("psList", psList);
        // 一体化对应etliu end

        // add by etliu 20220219 start
        // GNS-894:【广农商场内清算】交易查询 页面查询条件“交易方向”缺少枚举值“T97：配债”，查询结果也要能查到配债交易
        psList.add(DictCodeListExt3.交易方向_配债.getValue());
        // add by etliu 20220219 end

        // add by etliu 20210501 银行间场内场外拆分 start
        pageParam.put("defaultTradeMarketList", PageMenuSplitUtil.getTradeMarketList((String) pageParam.get("pageId")));
        pageParam.put("defaultTradePlatFormList", PageMenuSplitUtil.getTradePlatFormList((String) pageParam.get("pageId")));
        // add by etliu 20210501 银行间场内场外拆分 end

        return this.queryForPage(page, length,
            () -> trdBondInvestTradeMapper.selectDeal(pageParam));
    }

    /**
     * 查询投资委托信息
     *
     * @param entrustId 委托代码
     * @return 投资委托信息
     * @see
     * @since 1.0
     */
    public BaseEtrInvestInfoDto getEntrustInfo(String entrustId) {
        if (baseEtrInvestInfoService == null) {
            return new BaseEtrInvestInfoDto();
        }
        BaseEtrInvestInfoDto resultDto = getBaseEtrInvestInfoDtoResult(entrustId);
        return resultDto;
    }

    public BaseEtrInvestInfoDto getBaseEtrInvestInfoDtoResult(String entrustId) {
        BaseEtrInvestInfoDto resultDto = baseEtrInvestInfoService.queryActiveMandate(entrustId);
        if (resultDto != null && resultDto.getEtrEntrust() != null) {
            // 委托数量中扣除已完成额度
            if (!StringUtil.isEmptyList(resultDto.getEtrFinishStatusList())) {
                BigDecimal 已完成额度 = BigDecimal.ZERO;
                for (EtrFinishStatus etrFinishStatus : resultDto.getEtrFinishStatusList()) {
                    已完成额度 = ArithmeticUtil.doAdd(已完成额度,etrFinishStatus.getFinishAmt());
                }
                if(StringUtil.isNotEmpty(resultDto.getEtrEntrust().getEntrustNum())){
                	resultDto.getEtrEntrust().setEntrustNum(
                            ArithmeticUtil.subtract(resultDto.getEtrEntrust().getEntrustNum(), 已完成额度));
                    //剩余额度
                    resultDto.getEtrEntrustAdd().setOurEntNum(resultDto.getEtrEntrust().getEntrustNum());

                }else if(StringUtil.isNotEmpty(resultDto.getEtrEntrust().getEntrustAmt())){
                	resultDto.getEtrEntrust().setEntrustAmt(
                            ArithmeticUtil.subtract(resultDto.getEtrEntrust().getEntrustAmt(), 已完成额度));
                    //剩余额度
                    resultDto.getEtrEntrustAdd().setOurEntNum(resultDto.getEtrEntrust().getEntrustNum());
                }else{
                	resultDto.getEtrEntrust().setEntrustNum(
                            ArithmeticUtil.subtract(resultDto.getEtrEntrust().getEntrustNum(), 已完成额度));
                    //剩余额度
                    resultDto.getEtrEntrustAdd().setOurEntNum(resultDto.getEtrEntrust().getEntrustNum());
                }
            }else{
                //无已完成额 ，
                resultDto.getEtrEntrustAdd().setOurEntNum(resultDto.getEtrEntrust().getEntrustNum());
            }

            if (!StringUtil.equals(DictCodeListExt1.金融产品类型_债券.getValue(),
                resultDto.getEtrEntrust().getFinprodType2())) {
                resultDto.getEtrEntrust().setPrice(ArithmeticUtil.multiply(
                    resultDto.getEtrEntrust().getPrice(), BigDecimal.valueOf(100)));
            }
            else {
                resultDto.getEtrEntrust().setEntrustNum(ArithmeticUtil.divide(
                    resultDto.getEtrEntrust().getEntrustNum(), BigDecimal.valueOf(100),6));
            }

            // 查询金融产品名称
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(resultDto.getEtrEntrust().getFinprodId());
            if (finProduct != null) {
                resultDto.setFinprodName(finProduct.getFinprodAbbr());//资产简称
            }
        }
        if (resultDto != null && resultDto.getEtrEntrustAdd() != null) {
            // 清算速度
            if (resultDto.getEtrEntrustAdd().getSettleSpreed() == null
                || resultDto.getEtrEntrustAdd().getSettleSpreed() == 0L) {
                resultDto.setSettleSpeedStr(DictCodeList.清算速度_T_0.getValue());
            }
            else if(resultDto.getEtrEntrustAdd().getSettleSpreed() == 1L){
                resultDto.setSettleSpeedStr(DictCodeList.清算速度_T_1.getValue());
            }else if(resultDto.getEtrEntrustAdd().getSettleSpreed() == 2L){
                resultDto.setSettleSpeedStr(DictCodeList.清算速度_T_2.getValue());
            }

            // 到期收益率
            if (resultDto.getEtrEntrustAdd().getMatureYield() != null) {
                resultDto.getEtrEntrustAdd().setMatureYield(ArithmeticUtil.multiply(
                    resultDto.getEtrEntrustAdd().getMatureYield(), BigDecimal.valueOf(100)));
            }
            // 行权收益率
            if (resultDto.getEtrEntrustAdd().getExerYield() != null) {
                resultDto.getEtrEntrustAdd().setExerYield(ArithmeticUtil.multiply(
                    resultDto.getEtrEntrustAdd().getExerYield(), BigDecimal.valueOf(100)));
            }
        }
        return resultDto;
    }

    /**
     * 交易录单多组合模式
     * @param entrustId
     * @return
     */
    public List<BaseEtrInvestInfoDto> getEntrustInfos(String entrustId){
        List<BaseEtrInvestInfoDto> dtos = new ArrayList<>();
        if (baseEtrInvestInfoService == null) {
            return dtos;
        }
        String[] split = entrustId.split(",");
        if(split.length>0){
            for (String str:split) {
                BaseEtrInvestInfoDto baseEtrInvestInfoDtoResult = getBaseEtrInvestInfoDtoResult(str);
                //
                dtos.add(baseEtrInvestInfoDtoResult);
            }
        }
        return dtos;
    }
    /**
     * 查询证券托管户：联想框文本框
     *
     * @param param 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getSecManageAcctId(Map<String, Object> param) {
        Object finprodIdTemp = param.get("finprodId");
        String finProdType = DictCodeListExt1.金融产品类型_投管用_债券.getValue();
        //部分页面没有finprodId特殊处理
        if(finprodIdTemp == null){
            //债券二级指令页面
            if(param.get("finprodName") != null){
                String finprodName = param.get("finprodName").toString();
                finprodName = finProdType.concat(GlobalConstant.UNDER_LINE).concat(finprodName);
                param.put("finprodId",finprodName);
            }
            // 可转债申购指令
            if(param.get("finprodMarketId") != null){
                String finprodMarketId = param.get("finprodMarketId").toString();
                finprodMarketId = finProdType.concat(GlobalConstant.UNDER_LINE).concat(finprodMarketId);
                param.put("finprodId",finprodMarketId);
            }
        }else if(StringUtil.isNotEmpty(finprodIdTemp) && !StringUtil.equals(finprodIdTemp.toString().substring(0, 3), "F01")){
            param.put("finprodId",finProdType.concat(GlobalConstant.UNDER_LINE).concat(finprodIdTemp.toString()));
        }
        return this.queryForSuggest(() -> trdBondInvestTradeMapper.getSecManageAcctId(param),
            null);
    }

    
    
    /**
     * 查询证券托管户：联想框文本框
     *
     * @param param 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getChannel(Map<String, Object> param) {
        return this.queryForSuggest(() -> trdBondInvestTradeMapper.getChannel(param),
            null);
    }
    
    
    public List<String> getChannelInfo(Map<String, Object> param) {
        return this.queryForSuggest(() -> trdBondInvestTradeMapper.getChannelInfo(param),
            null);
    }
    /**
     * 1.6计算偏离度
     *
     * @param
     * @return 偏离度
     * @see
     * @since 1.0
     */
    public String calcDeviationDegree(Map<String, Object> param) {
        // 参数.资产代码(市场代码)
        String 市场代码 = (String) param.get("finprodMarketId");
        // 参数.净价
        String 净价 = (String) param.get("unitCprice");
        if (StringUtil.isBlank(市场代码) || StringUtil.isBlank(净价)) {
            return null;
        }

        String sysDate = (String) param.get("sysDate");
        param.put("sysDate", DateUtil.parse(sysDate));

        // 查询债券登记托管机构
        FinProduct finProduct = finProductMapper.selectByPrimaryKey((String) param.get("finprodId"));
        if (finProduct != null) {
            if (StringUtil.equals(DictCodeList.登记托管机构_中债_中央国债登记结算有限责任公司.getValue(),
                finProduct.getRegistOrg())) {
                param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
            }
            else if (StringUtil.equals(DictCodeList.登记托管机构_中债_上海清算所.getValue(),
                finProduct.getRegistOrg())) {
                param.put("VALUATION_SOURCE", DictCodeList.估值来源_上清所_SCH.getValue());
            }
            else if (StringUtil.equals(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue(),
                finProduct.getRegistOrg())) {
                param.put("VALUATION_SOURCE", DictCodeList.估值来源_中证登_CSD.getValue());
            }
            else {
                param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
            }
        }
        else {
            param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
        }

        // 查询上一估值日的净价
        BigDecimal 上一估值日净价 = trdBondInvestTradeMapper.getLastNetPrice(param);
        if (上一估值日净价 == null || BigDecimal.ZERO.compareTo(上一估值日净价) == 0) {
            //估值行情取不到值就需要取当前标的剩余单位面值
            param.put("CASH_CLASS_TYPE_02",DictCodeList.现金流类型_中间还本.getValue());
            param.put("CASH_CLASS_TYPE_03",DictCodeList.现金流类型_到期还本.getValue());
            上一估值日净价 = trdBondInvestTradeMapper.getResidualFace(param);
            if (上一估值日净价 == null || BigDecimal.ZERO.compareTo(上一估值日净价) == 0) {
                return "0%";
            }
        }

        try {
            BigDecimal 所输净价 = new BigDecimal(净价);

            // 计算公式：（所输净价-上一估值净价）/上一估值净价*100%
            BigDecimal result = ArithmeticUtil.subtract(所输净价, 上一估值日净价);
            result = ArithmeticUtil.multiply(result, new BigDecimal(100));
            result = ArithmeticUtil.divide(result, 上一估值日净价, 2);

            return String.valueOf(result) + "%";
        }
        catch (Exception e) {
        }

        return null;
    }

    /**
     * 1.8计算单位面值
     *
     * @param param 参数：金融产品代码 交割日
     * @return 单位面值
     * @see
     * @since 1.0
     */
    public BigDecimal getUnitPrice(Map<String, Object> param) {
        String settleDate = (String) param.get("settleDate");
        param.put("settleDate", DateUtil.parse(settleDate));

        return trdBondInvestTradeMapper.getUnitPrice(param);
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
    @SuppressWarnings("unchecked")
    public Page<SelectOptionDto> getFinprodId(int page, int length, Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
//        List<SelectOptionDto> resultList = trdBondInvestTradeMapper.getFinprodId(param);
        param.put("isautobond", CacheUtil.getSysParam("IS_AUTOBOND"));

        // add by etliu 20210501 银行间场内场外拆分 start
        param.put("defaultTradeMarketList", PageMenuSplitUtil.getTradeMarketList((String) param.get("parentPageId")));
        // add by etliu 20210501 银行间场内场外拆分 end

        param.put("platDate", pPlatformDate.getSysDate());

        return this.queryForPage(page, length,
                () -> trdBondInvestTradeMapper.getFinprodId(param));

//        if (!StringUtil.isEmptyList(resultList)) {
//            for (SelectOptionDto selectOptionDto : resultList) {
//                if (selectOptionDto.getObject() != null) {
//                    @SuppressWarnings("unchecked")
//                    Map<String, Object> objMap = (Map<String, Object>) selectOptionDto.getObject();
//                    if (objMap.containsKey("isRight")) {
//                        String isRight = (String) objMap.get("isRight");
//                        if (StringUtil.contains(isRight, GlobalConstant.COMMA)) {
//                            objMap.put("isRight", StringUtil.splitToList(GlobalConstant.COMMA, isRight).get(0));
//                            selectOptionDto.setObject(objMap);
//                        }
//                    }
//                }
//            }
//        }
//        return resultList;
    }
    //    资产推荐方 新增 zhuyongkang 20191023 end
    /**
     * 构建推荐方下拉框
     *
     * @param param 参数
     * @return 下拉框查询结果
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getAssetRecommandOrg(int page, int length, Map<String, Object> param) {
        param.put("counterType", DictCodeListExt1.交易对手类型_分行.getValue());
        return this.queryForPageForSelectReturn(page, length,
                () -> trdBondInvestTradeMapper.getAssetRecommandOrg(param));
    }
    //    资产推荐方 新增 zhuyongkang 20191023 end
    /**
     * 行权收益率是否显示
     *
     * @param
     * @return 行权收益率是否显示
     * @see
     * @since 1.0
     */
    public String isExerYieldDivShow(Map<String, Object> param) {
        String finprodId = (String) param.get("finprodId");
        if (StringUtil.isBlank(finprodId)) {
            return DictCodeList.是否_否.getValue();
        }

        // 是否含权债
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
        if (finProduct == null) {
            return DictCodeList.是否_否.getValue();
        }
        else {
            String isRight = finProduct.getIsRight();
            if (StringUtil.contains(isRight, GlobalConstant.COMMA)) {
                isRight = StringUtil.splitToList(GlobalConstant.COMMA, isRight).get(0);
            }
            if (!StringUtil.equals(DictCodeList.是否_是.getValue(), isRight)) {
                return DictCodeList.是否_否.getValue();
            }
        }

        // tradeDate
        if (StringUtil.isBlank((String) param.get("tradeDate"))) {
            param.put("sysDate", pPlatformDate.getSysDate());
        }
        else {
            param.put("sysDate", DateUtil.parse((String) param.get("tradeDate")));
        }

        int dataCount = trdBondInvestTradeMapper.selectBondHasEexrPlanCount(param);
        if (dataCount > 0) {
            return DictCodeList.是否_是.getValue();
        }

        return DictCodeList.是否_否.getValue();
    }

    /**
     * 判断某一天是否是债券行权日
     *
     * @param finprodId 金融产品代码
     * @param paramDate 判断日期
     * @return 判断结果
     * @see
     * @since 1.0
     */
    public boolean isExerciseDate(String finprodId, String paramDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("finprodId", finprodId);
        paramMap.put("paramDate", DateUtil.parse(paramDate));

        return trdBondInvestTradeMapper.selectBondEexrPlanCountForOneDay(paramMap) > 0;
    }
    
    /**
     * 判断是否含权债
     *
     * @param finprodId 金融产品代码
     * @see
     * @since 1.0
     */
    public boolean isRight(String finprodId) {
    	FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
    	if (finProduct != null && DictCodeList.是否_是.getValue().equals(finProduct.getIsRight())) {
    		return true;
    	}
    	return false;
    }

    
  //货币型产品投资目的默认显示摊余成本 add by liupeilin 2020-01-15 begin
    /**
     * 获取产品类型
     * 
     * */
    public String getProfitType(String portfolioId){
       String profitType = trdBondInvestTradeMapper.getProfitType(portfolioId);
        return profitType;
        
    }
    //货币型产品投资目的默认显示摊余成本 add by liupeilin 2020-01-15 end
    
    
    /**
     * 债券投资交易新建画面查看模式查询债券投资交易信息
     *
     * @param tradeId 交易编号
     * @return 债券投资交易信息
     * @see
     * @since 1.0
     */
    public TrdBondInvestTradeDto getTrdBondInvestTradeDto(String tradeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("tradeId", tradeId);
        params.put("B09", DictCodeListExt1.一次性费用类型_通道费.getValue());
        params.put("Y", DictCodeList.是否_是.getValue());
        TrdBondInvestTradeDto resultDto = trdBondInvestTradeMapper.getTrdBondInvestTradeDto(params);
        if (resultDto != null) {
            // etliu 20200716 modify 画面上的清算速度已经是输入框，显示数值，不是下拉框显示数据字典了 start
//            // 清算速度
//           if (resultDto.getSettleSpeed() == null || resultDto.getSettleSpeed() == 0L) {
//                resultDto.setSettleSpeedStr(DictCodeList.清算速度_T_0.getValue());
//            }
//            else {
//                resultDto.setSettleSpeedStr(DictCodeList.清算速度_T_1.getValue());
//            }
//            if (resultDto.getSettleSpeed() != null) {
//                resultDto.setSettleSpeedStr(String.valueOf(resultDto.getSettleSpeed()));
//            }
            // etliu 20200716 modify 画面上的清算速度已经是输入框，显示数值，不是下拉框显示数据字典了 end
            // 清算速度
            if (resultDto.getSettleSpeed() == null || resultDto.getSettleSpeed() == 0L) {
                //del by xr 展示位0、1、2
//                resultDto.setSettleSpeedStr(DictCodeList.清算速度_T_0.getValue());
                resultDto.setSettleSpeedStr("0");
            }
            else if(resultDto.getSettleSpeed() == 1L){
//                resultDto.setSettleSpeedStr(DictCodeList.清算速度_T_1.getValue());
                resultDto.setSettleSpeedStr("1");
            }else if(resultDto.getSettleSpeed() == 2L){
                resultDto.setSettleSpeedStr("2");
            }else{
                resultDto.setSettleSpeedStr(resultDto.getSettleSpeed().toString());
            }
            // 到期收益率
            if (resultDto.getYield() != null) {
                resultDto.setYield(ArithmeticUtil.multiply(resultDto.getYield(), BigDecimal.valueOf(100)));
            }
            // 行权收益率
            if (resultDto.getExerYield() != null) {
                resultDto.setExerYield(ArithmeticUtil.multiply(resultDto.getExerYield(), BigDecimal.valueOf(100)));
            }
            
            // 交易手续费
            TrdTradeFeeTotalKey trdTradeFeeTotalKey = new TrdTradeFeeTotalKey();
            trdTradeFeeTotalKey.setTotleTradeId(resultDto.getTotleTradeId());
            trdTradeFeeTotalKey.setFeeType(BussinessDictCodeList.费用类型_交易手续费.getValue());
            TrdTradeFeeTotal trdTradeFeeTotal = trdTradeFeeTotalMapper.selectByPrimaryKey(trdTradeFeeTotalKey);
            if (trdTradeFeeTotal != null) {
                resultDto.setProcedureAmt(trdTradeFeeTotal.getFeeAmt());
            }

            // 结算费服务费
            trdTradeFeeTotalKey.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
            trdTradeFeeTotal = trdTradeFeeTotalMapper.selectByPrimaryKey(trdTradeFeeTotalKey);
            if (trdTradeFeeTotal != null) {
                resultDto.setSettleServiceAmt(trdTradeFeeTotal.getFeeAmt());
            }
            
            // 佣金
            trdTradeFeeTotalKey.setFeeType(BussinessDictCodeList.费用类型_佣金.getValue());
            trdTradeFeeTotal = trdTradeFeeTotalMapper.selectByPrimaryKey(trdTradeFeeTotalKey);
            if (trdTradeFeeTotal != null) {
                resultDto.setCommissionAmt(trdTradeFeeTotal.getFeeAmt());
                // add by wangguoxian 费用设置 20180808 start
                resultDto.setIsPayWithTrade(trdTradeFeeTotal.getIsPayWithTrade());
                // add by wangguoxian 费用设置 20180808 end
            }

            //结算费
            trdTradeFeeTotalKey.setFeeType(BussinessDictCodeList.费用类型_结算费.getValue());
            trdTradeFeeTotal = trdTradeFeeTotalMapper.selectByPrimaryKey(trdTradeFeeTotalKey);
            if (trdTradeFeeTotal != null){
                resultDto.setSettleAmt(trdTradeFeeTotal.getFeeAmt());
                resultDto.setSettleIsPayWithTrade(trdTradeFeeTotal.getIsPayWithTrade());
            }

            //经手费
            trdTradeFeeTotalKey.setFeeType(BussinessDictCodeList.费用类型_经手费.getValue());
            trdTradeFeeTotal = trdTradeFeeTotalMapper.selectByPrimaryKey(trdTradeFeeTotalKey);
            if (trdTradeFeeTotal != null){
                resultDto.setExchangeAmt(trdTradeFeeTotal.getFeeAmt());
                resultDto.setExchangeIsPayWithTrade(trdTradeFeeTotal.getIsPayWithTrade());
            }

         // 印花税
            trdTradeFeeTotalKey.setFeeType(BussinessDictCodeList.费用类型_印花税.getValue());
            trdTradeFeeTotal = trdTradeFeeTotalMapper.selectByPrimaryKey(trdTradeFeeTotalKey);
            if (trdTradeFeeTotal != null) {
                resultDto.setStampDutyAmt(trdTradeFeeTotal.getFeeAmt());
                resultDto.setStampDutyIsPayWithTrade(trdTradeFeeTotal.getIsPayWithTrade());
            }
        // 通道费
            trdTradeFeeTotalKey.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
            trdTradeFeeTotal = trdTradeFeeTotalMapper.selectByPrimaryKey(trdTradeFeeTotalKey);
            if (trdTradeFeeTotal != null) {
                resultDto.setChannelFee(trdTradeFeeTotal.getFeeAmt());
            }
        }
        return resultDto;
    }

    /**
     * 查询债券基本信息
     *
     * @param map 参数
     * @return 债券基本信息
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public SecInfoModelDto getSecInfoModel(Map<String, Object> map) {
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        map.put("sysDate", pPlatformDate.getSysDate());
        map.put("YES_NO_Y", DictCodeList.是否_是.getValue());
        map.put("EXEC_MODE02", DictCodeList.行权方式_回售.getValue());
        map.put("EXEC_MODE01", DictCodeList.行权方式_赎回.getValue());
        map.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
        map.put("psT39", DictCodeList.交易方向_行权_赎回.getValue());

        return trdBondInvestTradeMapper.getSecInfoModel(map);
    }

    /**
     * 获取债券付息计划
     *
     * @param finprodId 金融产品代码
     * @return 债券付息计划
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public List<FinCashPlanDto> getFinCashPlan(String finprodId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        return trdBondInvestTradeMapper.getFinCashPlan(map);
    }

    /**
     * a.当前债券面额取得（根据还本计划计算）
     *
     * @param param 参数
     * @return a.当前债券面额取得（根据还本计划计算）
     * @see
     * @since 1.0
     */
    public BigDecimal getCurSecAmt(Map<String, Object> param) {
        param.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());
        param.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        String settleDate = (String) param.get("settleDate");
        param.put("settleDate", DateUtil.parse(settleDate));
        return trdBondInvestTradeMapper.getCurSecAmt(param);
    }

    /**
     * 查询债券利率重置信息
     *
     * @param finprodId 金融产品代码
     * @return 债券利率重置信息
     * @see
     * @since 1.0
     */
    public List<FinCashCalcRule> selectBondsIntResetList(String finprodId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("finprodId", finprodId);
        param.put("finprodType", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        return trdBondInvestTradeMapper.selectBondsIntResetList(param);
    }

    /**
     * 应计利息根据后台估值核算中前一个自然日的数据计算，等于后台的应计利息总额/后台的债券持仓总数量，
     * 如取不到估值核算中前一个自然日的数据，计算方式见算法文档；
     * 画面.应计利息=PTL_SEC_VALUTION.TDY_INTINCEXP/PTL_SEC_VALUTION.SHARE_AMT
     * （PTL_SEC_VALUTION.日期 = 画面.交割日-1 ，取不到，取最大日期对应数据）
     *
     * @param param 参数
     * @return 应计利息
     * @see
     * @since 1.0
     */
    public BigDecimal getIntAmtFromValution(Map<String, Object> param) {
        Date 交割日 = DateUtil.parse((String) param.get("settleDate"));
        String 组合代码 = (String) param.get("portfolioId");
        String 金融产品代码 = (String) param.get("finprodId");
        String 投资目的 = (String) param.get("invAim");
        String 证券管理通道代码 =  (String) param.get("secAcctId");

        if (交割日 == null || StringUtil.isBlank(组合代码) || StringUtil.isBlank(金融产品代码)
            || StringUtil.isBlank(投资目的) || StringUtil.isBlank(证券管理通道代码)) {
            return null;
        }

        BigDecimal 应计利息 = null;

        Date cdate = DateUtil.addDay(交割日, -1);
        PtlSecValutionKey ptlSecValutionKey = new PtlSecValutionKey();
        ptlSecValutionKey.setCdate(cdate);
        ptlSecValutionKey.setPortfolioId(组合代码);
        ptlSecValutionKey.setFinprodId(金融产品代码);
        ptlSecValutionKey.setInvAim(投资目的);
        ptlSecValutionKey.setSecAcctId(证券管理通道代码);

        PtlSecValution ptlSecValution = ptlSecValutionMapper.selectByPrimaryKey(ptlSecValutionKey);
        if (ptlSecValution == null) {
            ptlSecValution = trdBondInvestTradeMapper.selectPtlSecValutionData(ptlSecValutionKey);
        }

        if (ptlSecValution != null) {
            应计利息 = ArithmeticUtil.divide(ptlSecValution.getTdyIntincexp(),
                ptlSecValution.getShareAmt(), 12);
        }

        return 应计利息;
    }

	public List<SelectOptionDto> getPortfolioId(Map<String, Object> param) {
        return trdBondInvestTradeMapper.getPortfolioId(param);
	}
	
    public List<SelectOptionDto> selectPortfolioId(Map<String, Object> param) {
        String userid=LoginUserUtil.getUserName();
        param.put("userid", userid);
        return trdBondInvestTradeMapper.selectPortfolioId(param);
    }

    /**
     * 取得证券托管户的登记托管机构
     * 
     * @param param Map<String, Object>
     * @return 证券托管户的登记托管机构
     * @see
     * @since 1.0
     */
    public String getRegistOrg(Map<String, Object> param) {
        return trdBondInvestTradeMapper.getRegistOrg(param);
    }

    /**
     * 查询债券cstp交易下拉框
     * @param param 输入参数
     * @return
     */
    public Page<SelectOptionDto> querySecCstpInfoList(int page, int length,Map<String, Object> param){
        param.put("tradeStatusInit", BussinessDictCodeList.交易状态_初始.getValue());
        param.put("noFlag", BussinessDictCodeList.是否_否.getValue());
        if(StringUtils.isNotBlank((String)param.get("search"))){
            param.put("param",param.get("search"));
        }
        Page<SelectOptionDto> pages = this.queryForPageForSelectReturn(page, length, () -> trdBondInvestTradeMapper.querySecCstpInfoList(param));
//        if (StringUtil.isEmptyList(pages.getRoot())){
//            if (StringUtil.isNotEmpty(param.get("initValue"))){
//                SuggestDto suggestDto = new SuggestDto();
//                suggestDto.setCode(param.get("initValue").toString());
//                suggestDto.setName(param.get("initValue").toString());
//                pages.getRoot().add(suggestDto);
//            }
//        }
        return pages;
    }

    /**
     * 根据成交编号查询cstp交易信息
     * @param
     * @param loginUserName 登录用户
     * @return cstp交易信息
     */
    public TrdBondInvestTradeCSTPDto querySecCstpInfoByNo(String fedealno, String entrustId,String preTradeId, String loginUserName,String isCstpSure){

        TrdBondInvestTradeCSTPDto trdBondInvestTradeCSTPDto = new TrdBondInvestTradeCSTPDto();

        InterfTradSecdeal interfTradSecdeal = trdBondInvestTradeMapper.querySecCstpInfoByNo(fedealno,entrustId,preTradeId,isCstpSure);
        if (interfTradSecdeal != null){
            BeanUtil.copyPropertiesIgnoreNull(trdBondInvestTradeCSTPDto, interfTradSecdeal);
            if(StringUtil.equals(isCstpSure,"true")&&StringUtil.isNotEmpty(fedealno)&&StringUtil.isNotEmpty(entrustId)){
                trdBondInvestTradeCSTPDto.setPrinamt(trdBondInvestTradeMapper.selectMatchNum(fedealno,entrustId));
            }
            if(interfTradSecdeal.getDealdate()!=null){
                trdBondInvestTradeCSTPDto.setDealDateStr();
            }
            if(interfTradSecdeal.getSettdate()!=null){
                trdBondInvestTradeCSTPDto.setSettDateStr();
            }
            FinProduct finProduct = new FinProduct();
            if(StringUtils.isNotBlank(trdBondInvestTradeCSTPDto.getSecid())){
                // 根据债券代码查询资产代码
                 finProduct = trdBondInvestTradeMapper.queryFinProductInfoByMark(trdBondInvestTradeCSTPDto.getSecid(), BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
            }
            if (finProduct != null){
                trdBondInvestTradeCSTPDto.setSecid(finProduct.getFinprodId());
                trdBondInvestTradeCSTPDto.setFinprodMarketId(finProduct.getFinprodMarketId());
            }
            // 交易方向转换
//            if (StringUtil.equals(trdBondInvestTradeCSTPDto.getPs(), "P")){
//                trdBondInvestTradeCSTPDto.setPs(BussinessDictCodeList.交易方向_买入.getValue());
//            }
//            else{
//                trdBondInvestTradeCSTPDto.setPs(BussinessDictCodeList.交易方向_卖出.getValue());
//            }
            // 查询报价信息
            EtrEntrustExt etrEntrustExt = trdBondInvestTradeMapper.queryBondEntrustInfo(interfTradSecdeal.getFedealno());
            if(null!=etrEntrustExt){
            	//有报价
            	trdBondInvestTradeCSTPDto.setIsOfferFlag("01");
                if(StringUtil.isNotEmpty(etrEntrustExt.getCpPartyId())){
                    trdBondInvestTradeCSTPDto.setCno(trdBondInvestTradeMapper.selectCounterId(etrEntrustExt.getCpPartyId()));
                }
                trdBondInvestTradeCSTPDto.setClienttrader(etrEntrustExt.getCpTrader());
//                trdBondInvestTradeCSTPDto.setEntrustId(etrEntrustExt.getEntrustId());
            }else{
                /*if (StringUtil.isNotEmpty(trdBondInvestTradeCSTPDto.getCno())) {
                        MstCounterParty mstCounterParty = trdBondInvestTradeMapper.queryMstCounterPartyByCounterName(trdBondInvestTradeCSTPDto.getCno());
                    if (mstCounterParty != null) {
                        trdBondInvestTradeCSTPDto.setCno(mstCounterParty.getCounterId());
                    }
                }
                else{
                    trdBondInvestTradeCSTPDto.setCno(null);
                }*/
            	//无报价
            	trdBondInvestTradeCSTPDto.setIsOfferFlag("02");
            	//无报价交易对手置空
            	trdBondInvestTradeCSTPDto.setClienttrader(StringUtil.EMPTY);
            	trdBondInvestTradeCSTPDto.setCno(StringUtil.EMPTY);
            }
            // 根据证券账户查询对应组合
            // 查询证券账户是否系统存在
            StlSecAcctInfo stlSecAcctInfo = stlSecAcctInfoMapper.selectByPrimaryKey(trdBondInvestTradeCSTPDto.getOurdepositaccountnumber());
            if (StringUtil.isEmpty(trdBondInvestTradeCSTPDto.getOurdepositaccountnumber()) || stlSecAcctInfo== null){
                // 不存在默认设置甲类户
                trdBondInvestTradeCSTPDto.setOurdepositaccountnumber("A000000001");
            }
            // 根据证券账户查询关联关联组合
            List<PtlPortfolio> portfolioList = trdBondInvestTradeMapper.queryPortfolioIdByFedealno(loginUserName, fedealno);
            if (!StringUtil.isEmptyList(portfolioList) && portfolioList.size() == 1){
                trdBondInvestTradeCSTPDto.setPortfolioId(portfolioList.get(0).getPortfolioId());
                String invAim = BussinessDictCodeList.投资目的_公允价值变动计入当期损益.getValue();
                String profitType =  portfolioList.get(0).getProfitType();
                if(StringUtil.equals(profitType,DictCodeList.收益类型_货币型.getValue())
                        ||StringUtil.equals(profitType,DictCodeList.收益类型_收益型.getValue())){
                    invAim = BussinessDictCodeList.投资目的_摊余成本类.getValue();
                }
                trdBondInvestTradeCSTPDto.setInvAim(invAim);
            }
        }
        return trdBondInvestTradeCSTPDto;
    }


     public BigDecimal getTongDaoCash(Map<String, Object> map) {
        map.put("POSITION_TYPE_01", DictCodeList.持仓类型_可用.getValue());
        map.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());
        return trdBondInvestTradeMapper.getTongDaoCash(map);
    }


    public Boolean isAllTradeAmtByFTradeId(Map<String, Object> map){
        String fTradeId = (String) map.get("fTradeId");

        BigDecimal tradeAmt = null;
        if(map.get("tradeAmt") instanceof Integer){
            tradeAmt =  new BigDecimal((int)map.get("tradeAmt"));
        }else if(map.get("tradeAmt") instanceof Double){
            tradeAmt =  new BigDecimal((Double) map.get("tradeAmt"));
        }else{
            tradeAmt = new BigDecimal((String) map.get("tradeAmt"));
        }
        String workflowId = (String) map.get("workflowId");
        tradeAmt = com.joyintech.calculation.util.ArithmeticUtil.doAdd(2, RoundingMode.HALF_UP,tradeAmt, new BigDecimal(0));
        BigDecimal all = new BigDecimal(0);
        if(StringUtils.isNotBlank(fTradeId)){
            BigDecimal prinamt = BigDecimal.ZERO;
            Map<String,Object> ftrademap = new HashMap<>();
            ftrademap.put("fTradeId",fTradeId);
            //InterfTradSecdeal interfTradSecdeal = interfTradSecdealMapper.selectByPrimaryKey(fTradeId);
            InterfTradSecdeal interfTradSecdeal = trdBondInvestTradeMapper.selectInterfTradSecdeal(fTradeId);
            BigDecimal amountByFtradeId = cstpSelectMapper.getAmountByFtradeId(ftrademap);
            if(amountByFtradeId!=null){
                prinamt = amountByFtradeId;
            }
            //有效
            List<TrdProductDeal> trdProductDealByFTradeId = trdBondInvestTradeMapper.getTrdProductDealByFTradeId(fTradeId,BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
            //流程中
            List<WflTrdProductDeal> wftTrdProductDealByFTradeId = trdBondInvestTradeMapper.getWftTrdProductDealByFTradeId(fTradeId,BussinessDictCodeList.金融产品类型_投管用_债券.getValue(),workflowId);
            if(interfTradSecdeal!=null){
                for (TrdProductDeal deal:trdProductDealByFTradeId) {
//                    all = all.add(ArithmeticUtil.multiply(deal.getShareAmt(),BigDecimal.valueOf(100))).setScale(2,BigDecimal.ROUND_HALF_UP);
                    BigDecimal multiply = ArithmeticUtil.multiply(deal.getShareAmt(), BigDecimal.valueOf(100));
                    all =  ArithmeticUtil.doAdd(all,multiply);
                }
                for (WflTrdProductDeal deal:wftTrdProductDealByFTradeId) {
//                    all = all.add(deal.getTradeAmt()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    BigDecimal multiply = ArithmeticUtil.multiply(deal.getShareAmt(), BigDecimal.valueOf(100));
                    all =  ArithmeticUtil.doAdd(all,multiply);
                }
                if(tradeAmt!=null){
//                 all = all.add(tradeAmt).setScale(2,BigDecimal.ROUND_HALF_UP);
                  all =  ArithmeticUtil.doAdd(all,tradeAmt);
                }
                //有效和流程中的总和大于接口的数据
                if(all.compareTo(prinamt)==1){
                    return false;
                }else{
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据资产代码获取相关交易信息列表
     * @param map
     * @return
     */
    public List<TrdProductDeal> getTradeListByFinprodId(Map<String, Object> map) {
        return trdBondInvestTradeMapper.getTradeListByFinprodId(map);
    }
    /**
     * 根据资产代码、起息日、分支序号获取计息基础
     * @param map
     * @return
     */
    public FinCashCalcRule getBasisByFinprodId(Map<String, Object> map) {
        return trdBondInvestTradeMapper.getBasisByFinprodId(map);
    }

    /**
     * 根据资产代码、查询日、分支序号获取票面利率
     * @param map
     * @return
     */
    public FinCashCalcRule getYieldByFinprodId(Map<String, Object> map) {
        return trdBondInvestTradeMapper.getYieldByFinprodId(map);
    }
    /**
     * 根据资产代码、查询日、行权方式、资产类型查询下一行权日
     * @param map
     * @return
     */
    public List<FinExercisePlan> getExerciseDate(Map<String, Object> map) {
        return trdBondInvestTradeMapper.getExerciseDate(map);
    }

    /**
     *根据资产代码查询昨日指标信息的持仓数量和综合成本收益率
     * @param map
     * @return
     */
    public AssetTargetDb getYesterdayTarget(Map<String, Object> map) {
        return trdBondInvestTradeMapper.getYesterdayTarget(map);
    }

    /**
     * 查询当前时间的持仓市值和应计利息汇总值
     *资产类型为债券，查询日期
     * @paramFAIRE_PRICE
     * @return
     */
    public AssetTargetDb getSumPrice(Map<String, Object> map) {
        return trdBondInvestTradeMapper.getSumPrice(map);
    }
    

    /**
     * 查询交易页面债券的市值净价和摊销净价（吉林银行产品化需求）
     * @param params
     * @return
     * @see
     * @since 1.0 malinlin 2019.12.2
     */
    public SecCpriceInfoDto getCprice(Map<String, Object> map) {
        // 查询交易页面债券的市值净价和摊销净价
        SecCpriceInfoDto dto = trdBondInvestTradeMapper.getCprice(map);
        if (dto != null) {
            // 组合代码
            String counterId = (String) map.get("portfolioId");
            // 查询本方组合代码的收益类型
            String profitType = trdBondInvestTradeMapper.getProdProfitType(counterId);
            dto.setProfitType(profitType);
        }
        return dto;
    }
 
    /**
     * 查询交易对手是否为净值高频或者货币
     * @param params
     * @return
     * @see 
     */
    public String getCounterId(Map<String, Object> map) { 
        String isCounterId = trdBondInvestTradeMapper.getCounterId(map); 
        return isCounterId;
    }
    
    public String selectCeTradeId(Map<String, Object> param) {
        String tradeid = trdBondInvestTradeMapper.selectCeTradeId(param);
        return tradeid;
    }

    public String getReverseTradeDtoInvAim(String tradeId) {
        return trdBondInvestTradeMapper.getReverseTradeDtoInvAim(tradeId);
    }

    public int selectTradeT02(String tradeId) {
        return trdBondInvestTradeMapper.selectTradeT02(tradeId);
    }

    /* add by msk 新增查询通道划款交易 TISHKBS-1038 20201111 start */
    public int selectchlTrade(Map<String,Object> map) {
        return trdBondInvestTradeMapper.selectchlTrade(map);
    }

    /* add by msk 新增查询通道划款交易 TISHKBS-1038 20201111 end */
    public String getType7(TrdBondInvestTradeDto dto) {
        return trdBondInvestTradeMapper.getType7(dto);
    }

		public List<TrdTradeFeeDetail>  getAllFeeByFTradeId(Map<String, Object> map) {
		List<TrdTradeFeeDetail>  allFee = trdBondInvestTradeMapper.getAllFeeByFTradeId(map);
		return allFee;
	}

	public InterfTradSecdeal getInterfTradSecdeal(Map<String, Object> map) {
		String fTradeId = (String) map.get("fTradeId");
		return trdBondInvestTradeMapper.selectInterfTradSecdeal(fTradeId);
	}

    /**
     * 查询导出pdf数据 add by zengchengfa
     * @param param
     * @return
     * @throws Exception
     */
    public List<TrdBondInvestTradePdfDto> selectExportPdf(Map<String, Object> param) throws Exception{
        if (StringUtil.isNotEmpty(param.get("selectedItem"))){
            List<String> selectedItem = StringUtil.splitToList(",",(String)param.get("selectedItem"));
            if(selectedItem.size() > 0){
                param.put("tradeIdList",selectedItem);
            }
        }
        param.put("defaultTradeMarketList", PageMenuSplitUtil.getTradeMarketList((String) param.get("pageId")));
        return trdBondInvestTradeMapper.selectExportPdf(param);
    }
}
