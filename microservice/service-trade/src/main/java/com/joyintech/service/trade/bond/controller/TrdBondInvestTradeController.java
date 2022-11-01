/*
 * 文件名：TrdBondInvestTradeController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.trade.bond.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.service.trade.bond.dto.TrdBondInvestTradePdfDto;
import com.joyintech.service.trade.bond.service.TrdBondCalcService;
import com.joyintech.tams.invest.common.dto.period.trdfundduration.TrdFundDurationCommonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.trade.bond.dto.SecCpriceInfoDto;
import com.joyintech.service.trade.bond.dto.TrdBondInvestTradeCSTPDto;
import com.joyintech.service.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.service.trade.bond.service.TrdBondInvestTradeService;
import com.joyintech.tams.common.base.entity.AssetTargetDb;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.common.base.entity.InterfTradSecdeal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.trade.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;

/**
 * 债券交易微服务
 *
 * @author Zane.Y
 * @version 1.0 2018年9月7日
 * @see TrdBondInvestTradeController
 * @since 1.0
 */
@RestController
@RequestMapping("/TrdBondInvestTrade")
public class TrdBondInvestTradeController {
    /**
     * 注入service类
     */
    @Autowired
    private TrdBondInvestTradeService trdBondInvestTradeService;

    @Autowired
    private TrdBondCalcService trdBondCalcService;

    /**
     * 分页查询
     *
     * @return Page<TrdBondInvestTradeDto>泛型的page实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("findPage/{page}/{length}")
    public Page<TrdBondInvestTradeDto> queryForPage(@RequestBody Map<String, Object> param,
                                                    @PathVariable("page") int page, @PathVariable("length") int length)
    {
        return trdBondInvestTradeService.selectForPage(page, length, param);
    }

    /**
     * 查询债券投资交易信息
     *
     * @param tradeId 交易编号
     * @return 债券投资交易信息
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("getTrdBondInvestTradeDto/{tradeId}")
    public TrdBondInvestTradeDto getTrdBondInvestTradeDto(@PathVariable("tradeId") String tradeId)
    {
        return trdBondInvestTradeService.getTrdBondInvestTradeDto(tradeId);
    }

    //货币型产品投资目的默认显示摊余成本 add by liupeilin 2020-01-15 begin
    /**
     * 查询产品类型
     *
     * @param portfolioId
     * @return 产品类型
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("getProfitType/{portfolioId}")
    public String getProfitType(@PathVariable("portfolioId") String portfolioId)
    {
        return trdBondInvestTradeService.getProfitType(portfolioId);
    }
    //货币型产品投资目的默认显示摊余成本 add by liupeilin 2020-01-15 end
    
    
    
    /**
     * 查询委托信息
     *
     * @param entrustId 委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    @RequestMapping("getEntrustInfo/{entrustId}")
    public BaseEtrInvestInfoDto getEntrustInfo(@PathVariable("entrustId") String entrustId) {
        return trdBondInvestTradeService.getEntrustInfo(entrustId);
    }
    @RequestMapping("getEntrustInfos/{entrustId}")
    public List<BaseEtrInvestInfoDto> getEntrustInfos(@PathVariable("entrustId") String entrustId) {
        return trdBondInvestTradeService.getEntrustInfos(entrustId);
    }

    /**
     * 查询证券托管户：联想框文本框
     *
     * @param param 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    @RequestMapping("getSecManageAcctId")
    public List<SuggestDto> getSecManageAcctId(@RequestBody Map<String, Object> param) {
        return trdBondInvestTradeService.getSecManageAcctId(param);
    }
    
    
    /**
     * 通道：联想框文本框
     *
     * @param param 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    @RequestMapping("getChannel")
    public List<SuggestDto> getChannel(@RequestBody Map<String, Object> param) {
        return trdBondInvestTradeService.getChannel(param);
    }

    @RequestMapping("getChannelInfo")
    public List<String> getChannelInfo(@RequestBody Map<String, Object> param) {
        return trdBondInvestTradeService.getChannelInfo(param);
    }

    
    /**
     * 1.6计算偏离度
     *
     * @param params 画面参数
     * @return 偏离度
     * @see
     * @since 1.0
     */
    @RequestMapping("calcDeviationDegree")
    public String calcDeviationDegree(@RequestBody Map<String, Object> param) {
        return trdBondInvestTradeService.calcDeviationDegree(param);
    }

    /**
     * 1.8计算单位面值
     *
     * @param param 参数：金融产品代码 交割日
     * @return 单位面值
     * @see
     * @since 1.0
     */
    @RequestMapping("getUnitPrice")
    public BigDecimal getUnitPrice(@RequestBody Map<String, Object> param) {
        return trdBondInvestTradeService.getUnitPrice(param);
    }

    /**
     * 资产代码：下拉框
     *
     * @param params 画面参数
     * @return 资产代码：下拉框
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("getFinprodId/{page}/{length}")
    public Page<SelectOptionDto> getFinprodId(@RequestBody Map<String, Object> param,
                                              @PathVariable("page") int page, @PathVariable("length") int length) {
        return trdBondInvestTradeService.getFinprodId(page, length, param);
    }
    //    资产推荐方 新增 zhuyongkang 20191023 start
    /**
     * 构建推荐方下拉框
     *
     * @param param 参数
     * @return 下拉框查询结果
     * @see
     * @since 1.0
     */
    @RequestMapping("getAssetRecommandOrg/{page}/{length}")
    public Page<SelectOptionDto> getAssetRecommandOrg(@RequestBody Map<String, Object> param,
                                                      @PathVariable("page") int page,
                                                      @PathVariable("length") int length) {
        return trdBondInvestTradeService.getAssetRecommandOrg(page, length, param);
    }
    //    资产推荐方 新增 zhuyongkang 20191023 end

    /**
     * 行权收益率是否显示
     *
     * @param finprodId 金融产品代码
     * @return 行权收益率是否显示
     * @see
     * @since 1.0
     */
    @RequestMapping("isExerYieldDivShow")
    public String isExerYieldDivShow(@RequestBody Map<String, Object> param) {
        return trdBondInvestTradeService.isExerYieldDivShow(param);
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
    @RequestMapping("isExerciseDate")
    public boolean isExerciseDate(@RequestBody Map<String, Object> param) {
        String finprodId = (String) param.get("finprodId");
        String paramDate = (String) param.get("paramDate");
        return trdBondInvestTradeService.isExerciseDate(finprodId, paramDate);
    }

    /**
     * 判断是否含权债
     *
     * @param finprodId 金融产品代码
     * @see
     * @since 1.0
     */
    @RequestMapping("isRight")
    public boolean isRight(@RequestBody Map<String, Object> param) {
        String finprodId = (String) param.get("finprodId");
        return trdBondInvestTradeService.isRight(finprodId);
    }
    
    /**
     * 查询债券基本信息
     *
     * @param finprodId 金融产品代码
     * @return 债券基本信息
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("getSecInfoModel")
    public SecInfoModelDto getSecInfoModel(@RequestBody Map<String, Object> param)
    {
        return trdBondInvestTradeService.getSecInfoModel(param);
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
    @RequestMapping("getFinCashPlan")
    public List<FinCashPlanDto> getFinCashPlan(@RequestBody Map<String, Object> param)
    {
        String finprodId = (String) param.get("finprodId");
        return trdBondInvestTradeService.getFinCashPlan(finprodId);
    }

    /**
     * a.当前债券面额取得（根据还本计划计算）
     *
     * @param param 参数
     * @return a.当前债券面额取得（根据还本计划计算）
     * @see
     * @since 1.0
     */
    @RequestMapping("getCurSecAmt")
    public BigDecimal getCurSecAmt(@RequestBody Map<String, Object> param) {
        return trdBondInvestTradeService.getCurSecAmt(param);
    }

    /**
     * 查询债券利率重置信息
     *
     * @param finprodId 金融产品代码
     * @return 债券利率重置信息
     * @see
     * @since 1.0
     */
    @RequestMapping("selectBondsIntResetList")
    public List<FinCashCalcRule> selectBondsIntResetList(@RequestBody Map<String, Object> paramMap) {
        String finprodId = (String) paramMap.get("finprodId");

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("finprodId", finprodId);
        param.put("finprodType", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        return trdBondInvestTradeService.selectBondsIntResetList(finprodId);
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
    @RequestMapping("getIntAmtFromValution")
    public BigDecimal getIntAmtFromValution(@RequestBody Map<String, Object> param) {
        return trdBondInvestTradeService.getIntAmtFromValution(param);
    }
    
    
    @RequestMapping("getPortfolioId")
    public List<SelectOptionDto> getPortfolioId(@RequestBody Map<String, Object> param) {
        return trdBondInvestTradeService.getPortfolioId(param);
    }
    
    @RequestMapping("selectPortfolioId")
    public List<SelectOptionDto> selectPortfolioId(@RequestBody Map<String, Object> param) {
        return trdBondInvestTradeService.selectPortfolioId(param);
    }

    /**
     * 取得证券托管户的登记托管机构
     * 
     * @param param Map<String, Object>
     * @return 证券托管户的登记托管机构
     * @see
     * @since 1.0
     */
    @GetMapping("getRegistOrg")
    public String getRegistOrg(String secAcctId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("secAcctId", secAcctId);
        return trdBondInvestTradeService.getRegistOrg(map);
    }

    /**
     * 查询债券cstp交易下拉框
     * @param param 输入参数
     * @return
     */
    @RequestMapping("querySecCstpInfoList/{page}/{length}")
    public Page<SelectOptionDto> querySecCstpInfoList(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                 @PathVariable("length") int length){
        Page<SelectOptionDto> suggestDtos = trdBondInvestTradeService.querySecCstpInfoList(page,length,param);
        return suggestDtos;
    }
    /**
     * 根据成交编号查询cstp交易信息
     * @param tradeId 成交编号
     * @param loginUserName 登录用户
     * @return cstp交易信息
     */

    @GetMapping("querySecCstpInfoByNo")
    public TrdBondInvestTradeCSTPDto querySecCstpInfoByNo(String fedealno, String entrustId,String preTradeId, String loginUserName,String isCstpSure){
        return trdBondInvestTradeService.querySecCstpInfoByNo(fedealno,entrustId,preTradeId, loginUserName,isCstpSure);
    }

    /**
     * 通道可用现金查询
     * @param map
     * @return
     */
    @PostMapping("getTongDaoCash")
    public BigDecimal getTongDaoCash(@RequestBody Map<String, Object> map){
        BigDecimal big = trdBondInvestTradeService.getTongDaoCash(map);
        if(big!=null){
            return big;
        }
        return BigDecimal.ZERO;
    }

    /**
     * 根据成交编号查询劵面总额的值是否和接口的值一致
     * @param fTradeId
     * @return
     */
    @PostMapping("isAllTradeAmtByFTradeId")
    public Boolean isAllTradeAmtByFTradeId(@RequestBody Map<String, Object> map){
        Boolean allTradeAmtByFTradeId = trdBondInvestTradeService.isAllTradeAmtByFTradeId(map);
        return allTradeAmtByFTradeId;
    }
    
    
    /**
     * 根据成交编号查询费用的值是否和接口的值一致
     * @param fTradeId
     * @return
     */
    @PostMapping("getAllFeeByFTradeId")
    public List<TrdTradeFeeDetail>  getAllFeeByFTradeId(@RequestBody Map<String, Object> map){
    	List<TrdTradeFeeDetail>  allFee = trdBondInvestTradeService.getAllFeeByFTradeId(map);
        return allFee;
    }
    
    @PostMapping("getInterfTradSecdeal")
    public InterfTradSecdeal  getInterfTradSecdeal(@RequestBody Map<String, Object> map){
    	InterfTradSecdeal  interfTradSecdeal = trdBondInvestTradeService.getInterfTradSecdeal(map);
        return interfTradSecdeal;
    }
    
    
    
    /**
     * 根据资产代码获取相关交易信息列表
     * @param map
     * @return
     */
    @PostMapping("getTradeListByFinprodId")
    public List<TrdProductDeal> getTradeListByFinprodId(@RequestBody Map<String, Object> map){
        List<TrdProductDeal>  list = trdBondInvestTradeService.getTradeListByFinprodId(map);
        return list;
    }
    /**
     * 根据资产代码、起息日、分支序号获取计息基础
     * @param map
     * @return
     */
    @PostMapping("getBasisByFinprodId")
    public FinCashCalcRule getBasisByFinprodId(@RequestBody Map<String, Object> map){
        FinCashCalcRule  finCashCalcRule = trdBondInvestTradeService.getBasisByFinprodId(map);
        return finCashCalcRule;
    }
    
    /**
     * 查询交易页面债券的市值净价和摊销净价（吉林银行产品化需求）
     * @param params
     * @return
     * @see
     * @since 1.0 malinlin 2019.12.2
     */
    @PostMapping("getCprice")
    public SecCpriceInfoDto getCprice(@RequestBody Map<String, Object> map){
        SecCpriceInfoDto dto = trdBondInvestTradeService.getCprice(map);
        return dto;
    }

    /**
     * 查询交易对手是否为净值高频或者货币
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("getCounterId")
    public String getCounterId(@RequestBody Map<String, Object> map){
        String isCounterId = trdBondInvestTradeService.getCounterId(map);
        return isCounterId;
    }
 
    /**
     * 根据资产代码、查询日、分支序号获取票面利率
     * @param map
     * @return
     */
    @PostMapping("getYieldByFinprodId")
    public FinCashCalcRule getYieldByFinprodId(@RequestBody Map<String, Object> map) {
        return trdBondInvestTradeService.getYieldByFinprodId(map);
    }
    /**
     * 根据资产代码、查询日、行权方式、资产类型查询下一行权日
     * @param map
     * @return
     */
    @PostMapping("getExerciseDate")
    public List<FinExercisePlan> getExerciseDate(@RequestBody Map<String, Object> map){
        return trdBondInvestTradeService.getExerciseDate(map);

    }


    /**
     *根据资产代码查询昨日指标信息的持仓数量和综合成本收益率
     * @param map
     * @return
     */
    @PostMapping("getYesterdayTarget")
    public AssetTargetDb getYesterdayTarget(@RequestBody Map<String, Object> map) {
        return trdBondInvestTradeService.getYesterdayTarget(map);
    }

    /**
     *根据资产代码查询昨日指标信息的持仓数量和综合成本收益率
     * @param map
     * @return
     */
    @PostMapping("getAllYesterdayTarget")
    public List<AssetTargetDb> getAllYesterdayTarget(@RequestBody Map<String, Object> map) {
        return trdBondInvestTradeService.getAllYesterdayTarget(map);
    }

    /**
     * 查询当前时间的持仓市值和应计利息汇总值
     *资产类型为债券，查询日期
     * @paramFAIRE_PRICE
     * @return
     */
    @PostMapping("getSumPrice")
    public AssetTargetDb getSumPrice(@RequestBody Map<String, Object> map) {
        return trdBondInvestTradeService.getSumPrice(map);
    }
    
    /**
     * 查找授信使用的交易编号
     *
     * @see
     * @since 1.0
     */
    @PostMapping("selectCeTradeId")
    public String selectCeTradeId(@RequestBody Map<String, Object> map){
        String tradeid = trdBondInvestTradeService.selectCeTradeId(map);
        return tradeid;
    }


    /**
     * 查询债券投资交易信息
     *
     * @param tradeId 交易编号
     * @return 债券投资交易信息
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("getReverseTradeDtoInvAim/{tradeId}")
    public String getReverseTradeDtoInvAim(@PathVariable("tradeId") String tradeId)
    {
        return trdBondInvestTradeService.getReverseTradeDtoInvAim(tradeId);
    }

    /**
     * 查找买入交易之后的卖出交易
     *
     * @see
     * @since 1.0
     */
    @RequestMapping("selectTradeT02/{tradeId}")
    public int selectTradeT02(@PathVariable("tradeId") String tradeId){
        return trdBondInvestTradeService.selectTradeT02(tradeId);
    }

    /* add by msk 新增查询通道划款交易 20201111 start */
    /**
     * 查找通道划款交易
     *
     * @see
     * @since 1.0
     */
    @RequestMapping("selectchlTrade")
    public int selectchlTrade(@RequestBody Map<String,Object> map){
        return trdBondInvestTradeService.selectchlTrade(map);
    }
    /* add by msk 新增查询通道划款交易 20201111 end */

    @Transactional
    @DTXTransaction
    @PostMapping("getType7")
    public String getType7(@RequestBody TrdBondInvestTradeDto dto) {
        return trdBondInvestTradeService.getType7(dto);
    }



    // 按照净价计算
    @PostMapping("calculate01Data")
    public AjaxResult calculate01Data(@RequestBody Map<String, Object> map) {
        TrdBondInvestTradeDto inputDto = JSON.parseObject(
                JSON.toJSONString(map), TrdBondInvestTradeDto.class);
        inputDto.setCalculateField("unitCprice");

        // add by swwan 20211105 GZYHTZJY-197 场内交易净价、全价、到期收益率未联动 start
        Map<String, Object> tempParamMap = new HashMap<>();
        tempParamMap.put("finprodId", map.get("finprodId"));
        SecInfoModelDto secInfoModelDto = getSecInfoModel(tempParamMap);
        if (secInfoModelDto != null && secInfoModelDto.getStartDate() != null
                && DateUtil.compareTo(secInfoModelDto.getStartDate(), inputDto.getSettleDate()) > 0) {
            inputDto.setSettleDate(secInfoModelDto.getStartDate());
        }
        // add by swwan 20211105 GZYHTZJY-197 场内交易净价、全价、到期收益率未联动 end
        return trdBondCalcService.calculateData(inputDto);
    }

    // 按照全价总额计算
    @PostMapping("calculate02Data")
    public AjaxResult calculate02Data(@RequestBody Map<String, Object> map) {
        TrdBondInvestTradeDto inputDto = JSON.parseObject(
                JSON.toJSONString(map), TrdBondInvestTradeDto.class);
        inputDto.setCalculateField("fpriceAmt");
        return trdBondCalcService.calculateData(inputDto);
    }

    // 按照到期收益率计算
    @PostMapping("calculate03Data")
    public AjaxResult calculate03Data(@RequestBody Map<String, Object> map) {
        TrdBondInvestTradeDto inputDto = JSON.parseObject(
                JSON.toJSONString(map), TrdBondInvestTradeDto.class);
        inputDto.setCalculateField("yield");
        return trdBondCalcService.calculateData(inputDto);
    }

    /**
     * 查询导出pdf数据 add by zengchengfa 20210730
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("selectExportPdf")
    public List<TrdBondInvestTradePdfDto> selectExportPdf(@RequestBody Map<String, Object> param)
            throws Exception {
        List<TrdBondInvestTradePdfDto > result = trdBondInvestTradeService.selectExportPdf( param);
        return result;
    }
}
