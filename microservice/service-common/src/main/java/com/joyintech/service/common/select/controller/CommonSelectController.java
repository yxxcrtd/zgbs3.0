/*
 * 文件名：CommonSelectController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年09月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.common.select.controller;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.common.select.service.CommonSelectService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/CommonSelect")
public class CommonSelectController {

    /**
     * 共通下拉框Service
     */
    @Autowired
    private CommonSelectService commonSelectService;

    /**
     * 资产代码(债券)
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getAssetIdBond")
    public List<SuggestDto> getAssetIdBond(@RequestBody Map<String, Object> param)
    {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        return commonSelectService.getAssetIdCommon(param);
    }
    
    /**
     * 资产简称(债券)
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getFinprodAbbr")
    public List<SuggestDto> getFinprodAbbr(@RequestBody Map<String, Object> param)
    {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        return commonSelectService.getFinprodAbbr(param);
    }

    /**
     * 资产代码(存款)
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getAssetIdDepo")
    public List<SuggestDto> getAssetIdDepo(@RequestBody Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        return commonSelectService.getAssetIdCommon(param);
    }

    /**
     * 资产代码(计划类利率型)
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getAssetIdMngRate")
    public List<SuggestDto> getAssetIdMngRate(@RequestBody Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        param.put("PROFIT_TYPE", DictCodeList.收益类型_收益型.getValue());
        return commonSelectService.getAssetIdCommon(param);
    }

    /**
     * 资产代码(计划类净值型)
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getAssetIdMngNet")
    public List<SuggestDto> getAssetIdMngNet(@RequestBody Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        param.put("PROFIT_TYPE", DictCodeList.收益类型_净值型.getValue());
        return commonSelectService.getAssetIdCommon(param);
    }

    /**
     * 资产代码(计划类净值型)
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getAssetIdMngCcy")
    public List<SuggestDto> getAssetIdMngCcy(@RequestBody Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        param.put("PROFIT_TYPE", DictCodeList.收益类型_货币型.getValue());
        return commonSelectService.getAssetIdCommon(param);
    }

    /**
     * 资产代码(项目类)
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getAssetIdProj")
    public List<SuggestDto> getAssetIdProj(@RequestBody Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        return commonSelectService.getAssetIdCommon(param);
    }

    /**
     * 资产代码(基金)
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getAssetIdFund")
    public List<SuggestDto> getAssetIdFund(@RequestBody Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        return commonSelectService.getAssetIdCommon(param);
    }

    /**
     * 分行
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getBrachBankId")
    public List<SuggestDto> getBrachBankId(@RequestBody Map<String, Object> param) {
        param.put("COUNTER_TYPE", DictCodeListExt1.交易对手类型_分行.getValue());
        return commonSelectService.getBrachBankId(param);
    }

    /**
     * 管理人
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getManagerId")
    public List<SuggestDto> getManagerId(@RequestBody Map<String, Object> param) {
        param.put("IS_ASSTE_MANAGER", DictCodeList.是否_是.getValue());
        return commonSelectService.getCustomerId(param);
    }

    /**
     * 托管人
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getTrusterId")
    public List<SuggestDto> getTrusterId(@RequestBody Map<String, Object> param) {
        param.put("IS_TRUSTER", DictCodeList.是否_是.getValue());
        return commonSelectService.getCustomerId(param);
    }

    /**
     * 发行人
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getIssuerId")
    public List<SuggestDto> getIssuerId(@RequestBody Map<String, Object> param) {
        param.put("IS_ISSUER", DictCodeList.是否_是.getValue());
        return commonSelectService.getCustomerId(param);
    }

    /**
     * 融资人
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @see
     * @since 1.0
     */
    @RequestMapping("getFinancierId")
    public List<SuggestDto> getFinancierId(@RequestBody Map<String, Object> param) {
        param.put("IS_FINANCIER", DictCodeList.是否_是.getValue());
        return commonSelectService.getCustomerId(param);
    }

    /**
     * 融资人
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @see
     * @since 1.0
     */
    @RequestMapping("getFinancier/{page}/{length}")
    public Page<SelectOptionDto> getFinancier(@RequestBody Map<String, Object> param,
                                              @PathVariable("page") int page,
                                              @PathVariable("length") int length) {
        return commonSelectService.getCustomer(page, length, param);
    }

    /**
     * 担保人
     *
     * @param param 过滤参数
     * @return 下拉框列表
     * @see
     * @since 1.0
     */
    @RequestMapping("getGuaranteeId")
    public List<SuggestDto> getGuaranteeId(@RequestBody Map<String, Object> param) {
        param.put("IS_GUARANTEE", DictCodeList.是否_是.getValue());
        return commonSelectService.getCustomerId(param);
    }
    /**
     * 担保人
     *
     * @param param 过滤参数
     * @return 下拉框列表
     * @see
     * @since 1.0
     */
    @RequestMapping("getGuarantee/{page}/{length}")
    public Page<SelectOptionDto> getGuarantee(@RequestBody Map<String, Object> param,
                                              @PathVariable("page") int page,
                                              @PathVariable("length") int length) {
        param.put("IS_GUARANTEE", DictCodeList.是否_是.getValue());
        return commonSelectService.getCustomer(page, length, param);
    }

    /**
     * 委托代码
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getEntrustId")
    public List<SuggestDto> getEntrustId(@RequestBody Map<String, Object> param) {
        param.put("ENTRUST_STATUS_LIST", Arrays.asList(DictCodeList.委托状态_已接收.getValue(),
            DictCodeList.委托状态_已完成.getValue(), DictCodeList.委托状态_已关闭.getValue()));
        return commonSelectService.getEntrustId(param);
    }

    /**
     * 获取资产代码下来框
     *
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("selectFinprodMarketId/{page}/{length}")
    public Page<SelectOptionDto> selectFinprodMarketId(@RequestBody Map<String, Object> param,
                                                    @PathVariable("page") int page,
                                                    @PathVariable("length") int length) {
        return commonSelectService.selectFinprodMarketId(page, length, param);
    }

    /**
     * 利率行情+复合基准
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getMarketValuationId")
    public List<SuggestDto> getMarketValuationId(@RequestBody Map<String, Object> param) {
        param.put("MARKET_INFO_TYPE", DictCodeList.行情类型_利率.getValue());
        return commonSelectService.getMarketValuationId(param);
    }

    @RequestMapping("get{selectTarget}")
    public List<SuggestDto> getCommonSelect(@PathVariable("selectTarget") String selectTarget,
                                            @RequestBody Map<String, Object> param) {
        return commonSelectService.getCommonSelect(selectTarget, param);
    }

    @RequestMapping("select{selectTarget}")
    public List<SelectOptionDto> selectCommonSelect(@PathVariable("selectTarget") String selectTarget,
                                                    @RequestBody Map<String, Object> param) {
        return commonSelectService.selectCommonSelect(selectTarget, param);
    }

    @RequestMapping("select{selectTarget}/{page}/{length}")
    public Page<SelectOptionDto> selectCommonSelect(@PathVariable("selectTarget") String selectTarget,
                                                    @PathVariable("page") int page,
                                                    @PathVariable("length") int length,
                                                    @RequestBody Map<String, Object> param) {
        return commonSelectService.selectCommonSelectForPge(selectTarget, page, length, param);
    }

    /**
     * 管理人
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getManager/{page}/{length}")
    public Page<SelectOptionDto> getManager(@RequestBody Map<String, Object> param,
                                            @PathVariable("page") int page,
                                            @PathVariable("length") int length) {
        param.put("IS_ASSTE_MANAGER", DictCodeList.是否_是.getValue());
        return commonSelectService.getCustomer(page, length, param);
    }

    /**
     * 托管人
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getTruster/{page}/{length}")
    public Page<SelectOptionDto> getTruster(@RequestBody Map<String, Object> param,
                                            @PathVariable("page") int page,
                                            @PathVariable("length") int length) {
        param.put("IS_TRUSTER", DictCodeList.是否_是.getValue());
        return commonSelectService.getCustomer(page, length, param);
    }

    /**
     * 发行人
     * 
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getIssuer/{page}/{length}")
    public Page<SelectOptionDto> getIssuer(@RequestBody Map<String, Object> param,
                                           @PathVariable("page") int page,
                                           @PathVariable("length") int length) {
        param.put("IS_ISSUER", DictCodeList.是否_是.getValue());
        return commonSelectService.getCustomer(page, length, param);
    }

    /**
     * 原始权益人下拉
     *
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getOriginalOwner/{page}/{length}")
    public Page<SelectOptionDto> getOriginalOwner(@RequestBody Map<String, Object> param,
                                           @PathVariable("page") int page,
                                           @PathVariable("length") int length) {
        param.put("IS_ORIGINAL_OWNER", DictCodeList.是否_是.getValue());
        return commonSelectService.getCustomer(page, length, param);
    }

    /**
     * 实际管理人
     *
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getActualManager/{page}/{length}")
    public Page<SelectOptionDto> getActualManager(@RequestBody Map<String, Object> param,
                                           @PathVariable("page") int page,
                                           @PathVariable("length") int length) {
        param.put("IS_ASSTE_MANAGER", DictCodeList.是否_是.getValue());
        return commonSelectService.getCustomer(page, length, param);
    }

    /**
     * 组合代码下拉框 <功能详细描述>
     * 
     * @param param
     * @param page
     * @param length
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("getGroupIdInfo/{page}/{length}")
    public Page<SelectOptionDto> getGroupIdInfo(@RequestBody Map<String, Object> param,
                                                @PathVariable("page") int page,
                                                @PathVariable("length") int length) {
        return commonSelectService.getGroupIdInfo(page, length, param);
    }

    /**
     * 资金账户 <功能详细描述>
     * 
     * @param param
     * @param page
     * @param length
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("getAcctNo/{page}/{length}")
    public Page<SelectOptionDto> getAcctNo(@RequestBody Map<String, Object> param,
                                           @PathVariable("page") int page,
                                           @PathVariable("length") int length) {
        return commonSelectService.getAcctNo(page, length, param);
    }

    /**
     * 组合清算账户 <功能详细描述>
     * 
     * @param param 过滤参数{ search:画面输入.账户账号， initValue:画面输入.初始值， ccy:画面输入.币种， portfolioId:画面输入.组合代码，
     *            acctType:数据字典.资金账户类型}
     * @return 账户下拉框列表
     * @see
     * @since 1.0
     */
    @RequestMapping("selectPtlCapAcctNo")
    public List<SelectOptionDto> selectPtlCapAcctNo(@RequestBody Map<String, Object> param) {
        return commonSelectService.selectPtlCapAcctNo(param);
    }

    // 分页查询 add by renping 20191030 start
    /**
     * 组合清算账户 <功能详细描述>
     * 
     * @param param 过滤参数{ search:画面输入.账户账号， initValue:画面输入.初始值， ccy:画面输入.币种， portfolioId:画面输入.组合代码，
     *            acctType:数据字典.资金账户类型}
     * @param page
     * @param length
     * @return 账户下拉框列表
     * @see
     * @since 1.0
     */
    @RequestMapping("getPtlCapAcctNo/{page}/{length}")
    public Page<SelectOptionDto> qryPtlCapAcctNoByRequestParam(@RequestBody Map<String, Object> param,
                                                    @PathVariable("page") int page,
                                                    @PathVariable("length") int length) {
        return commonSelectService.qryPtlCapAcctNoByRequestParam(page, length, param);
    }

    /**
     * 交易对手方账户 <功能详细描述>
     * @return 账户下拉框列表
     * @see
     * @since 1.0
     */
    @RequestMapping("getMstCapAcctNo/{page}/{length}")
    public Page<SelectOptionDto> qryMstCapAcctNoByRequestParam(@RequestBody Map<String, Object> param,
                                                               @PathVariable("page") int page,
                                                               @PathVariable("length") int length) {
        if(param.get("trdInvestPersonInfo.counterId")!=null) {
            param.put("counterId",param.get("trdInvestPersonInfo.counterId"));
        }
        return commonSelectService.qryMstCapAcctNoByRequestParam(page, length, param);
    }
    // 分页查询 add by renping 20191030 end
    /**
     * 一级投标债券资产代码(债券)
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getAssetIdPrimaryBond")
    public List<SuggestDto> getAssetIdPrimaryBond(@RequestBody Map<String, Object> param)
    {
        param.put("FINPROD_TYPE_TG", BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        return commonSelectService.getAssetIdCommon(param);
    }
    
    
    /**
     * 交易对手方账户 <功能详细描述>
     * @return 账户下拉框列表
     * @see
     * @since 1.0
     */
    @RequestMapping("selectMstCapAcctNo")
    public List<SelectOptionDto> selectMstCapAcctNo(@RequestBody Map<String, Object> param) {
        if(param.get("trdInvestPersonInfo.counterId")!=null) {
        	param.put("counterId",param.get("trdInvestPersonInfo.counterId"));
        }
    	return commonSelectService.selectMstCapAcctNo(param);
    }


    /**
     * 联想查询通道
     *
     * @param params 画面参数
     * @return 通道代码：下拉框
     */
    @PostMapping("getChannelIdForAsset")
    public List<SelectOptionDto> getChannelIdForAsset(@RequestBody Map<String, Object> params) {
        return commonSelectService.getChannelIdForAsset(params);
    }
    
    /**
     * 联想查询通道
     *
     * @param params 画面参数
     * @return 通道代码：下拉框
     */
    @PostMapping("getChannelIdAndCollectTypeForAsset")
    public List<SelectOptionDto> getChannelIdAndCollectTypeForAsset(@RequestBody Map<String, Object> params) {
        return commonSelectService.getChannelIdAndCollectTypeForAsset(params);
    }


    /**
     * 资产代码查询（可投金额试算使用）
     * Add by nixiukuan 20190927
     *债券、基金、股票
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    @RequestMapping("getFinprodByType")
    public List<SuggestDto> getFinprodByType(@RequestBody Map<String, Object> param) {
        return commonSelectService.getFinprodByType(param);
    }

    @RequestMapping("getSecManageAcctId/{page}/{length}")
    public Page<SelectOptionDto> getSecManageAcctId(@RequestBody Map<String, Object> param,
                                               @PathVariable("page") int page,
                                               @PathVariable("length") int length) {
        return commonSelectService.getSecManageAcctId(page, length, param);
    }

    @RequestMapping("getPortfolioIdChild/{page}/{length}")
    public Page<SelectOptionDto> qryPortfolioIdChildByRequestParam(@RequestBody Map<String, Object> param,
                                                                   @PathVariable("page") int page,
                                                                   @PathVariable("length") int length) {
        return commonSelectService.qryPortfolioIdChildByRequestParam(page, length, param);
    }
}
