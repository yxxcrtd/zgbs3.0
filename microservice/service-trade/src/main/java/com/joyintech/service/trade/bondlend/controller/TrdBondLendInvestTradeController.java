/*
 * 文件名：TrdBondLendInvestTradeController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年06月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.trade.bondlend.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.trade.bondlend.dto.FinCashPlanDto;
import com.joyintech.service.trade.bondlend.dto.SecInfoModelDto;
import com.joyintech.service.trade.bondlend.dto.TrdBondLendInvestTradeDto;
import com.joyintech.service.trade.bondlend.service.TrdBondLendInvestTradeService;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommonWflParam;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 债券借贷交易微服务
 *
 * @author 裴家俊
 * @version 1.0 2019年6月11日
 * @see TrdBondLendInvestTradeController
 * @since 1.0
 */
@RestController("TrdBondLendInvestTradeCtr_dtx")
@RequestMapping("/TrdBondLendInvestTrade")
public class TrdBondLendInvestTradeController {
    /**
     * 注入service类
     */
    @Autowired
    private TrdBondLendInvestTradeService trdBondLendInvestTradeService;

    @Autowired
    private MapperDelegateService bussCommonMapperDelegateService;

    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;



    /**
     * 分页查询
     *
     * @return Page<TrdBondInvestTradeDto>泛型的page实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("findPage/{page}/{length}")
    public Page<TrdBondLendInvestTradeDto> queryForPage(@RequestBody Map<String, Object> param,
                                                        @PathVariable("page") int page, @PathVariable("length") int length)
    {
        Page<TrdBondLendInvestTradeDto> returnPage = trdBondLendInvestTradeService.selectForPage(page, length, param);
        return returnPage;
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
    @RequestMapping("getTrdBondLendInvestTradeDto/{tradeId}")
    public TrdBondLendInvestTradeDto getTrdBondLendInvestTradeDto(@PathVariable("tradeId") String tradeId)
     {
        return trdBondLendInvestTradeService.getTrdBondLendInvestTradeDto(tradeId);
    }

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
        return trdBondLendInvestTradeService.getEntrustInfo(entrustId);
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
        return trdBondLendInvestTradeService.getSecManageAcctId(param);
    }

    @RequestMapping("getAssetAvailablePosition")
    public Map<String, Object> getAssetAvailablePosition(@RequestBody Map<String, Object> param) {
        return trdBondLendInvestTradeService.getAssetAvailablePosition(param);
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
        return trdBondLendInvestTradeService.getChannel(param);
    }

    @RequestMapping("getChannelInfo")
    public List<String> getChannelInfo(@RequestBody Map<String, Object> param) {
        return trdBondLendInvestTradeService.getChannelInfo(param);
    }

    /**
     * 资产代码：下拉框
     *
     * @param param 画面参数
     * @return 资产代码：下拉框
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("getFinprodId/{page}/{length}")
    public Page<SelectOptionDto> getFinprodId(@RequestBody Map<String, Object> param,
                                              @PathVariable("page") int page, @PathVariable("length") int length) {
        return trdBondLendInvestTradeService.getFinprodId(page, length, param);
    }

    /**
     * 查询债券基本信息
     *
     * @param param 金融产品代码
     * @return 债券基本信息
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("getSecInfoModel")
    public SecInfoModelDto getSecInfoModel(@RequestBody Map<String, Object> param)
    {
        String finprodId = (String) param.get("finprodId");
        return trdBondLendInvestTradeService.getSecInfoModel(finprodId);
    }

    /**
     * 获取债券付息计划
     *
     * @param param 金融产品代码
     * @return 债券付息计划
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("getFinCashPlan")
    public List<FinCashPlanDto> getFinCashPlan(@RequestBody Map<String, Object> param)
    {
        String finprodId = (String) param.get("finprodId");
        return trdBondLendInvestTradeService.getFinCashPlan(finprodId);
    }

    /**
     * 查询产品类账户信息
     */
    @RequestMapping("getProAccountInfo")
    public List<StlCapacctInfo> getProAccountInfo(@RequestBody Map<String, Object> param) {
        String portfolioId = (String) param.get("portfolioId");
        String ccy = (String) param.get("ccy");
        String capacctType = (String) param.get("capacctType");
        return trdBondLendInvestTradeService.getProAccountInfo(portfolioId, ccy, capacctType);
    }

    /**
     * 查询交易类账户信息
     */
    @RequestMapping("getDealAccountInfo")
    public List<StlCapacctInfo> getDealAccountInfo(@RequestBody Map<String, Object> param) {
        String secManageAcctId = (String) param.get("secManageAcctId");
        String ccy = (String) param.get("ccy");
        return trdBondLendInvestTradeService.getDealAccountInfo(secManageAcctId, ccy);
    }

    /**
     * 查询交易类账户信息
     */
    @RequestMapping("getCounterCapacctInfo")
    public List<StlCapacctInfo> getCounterCapacctInfo(@RequestBody Map<String, Object> param) {
        String counterId = (String) param.get("counterId");
        return trdBondLendInvestTradeService.getCounterCapacctInfo(counterId);
    }

    @RequestMapping("getPortfolioId")
    public List<SelectOptionDto> getPortfolioId(@RequestBody Map<String, Object> param) {
        return trdBondLendInvestTradeService.getPortfolioId(param);
    }

    /**
     * 取得证券托管户的登记托管机构
     *
     * @param secAcctId secAcctId
     * @return 证券托管户的登记托管机构
     * @see
     * @since 1.0
     */
    @GetMapping("getRegistOrg")
    public String getRegistOrg(String secAcctId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("secAcctId", secAcctId);
        return trdBondLendInvestTradeService.getRegistOrg(map);
    }

    @Transactional
    @DTXTransaction
    @PostMapping("deleteBussFlowData")
    public String deleteBussFlowData(@RequestBody String workflowId) {
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "WORKFLOW_ID",
                workflowId);

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD",
                "WORKFLOW_ID", workflowId);

        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL",
                "WORKFLOW_ID", workflowId);

        // 金融产品交易其它信息Entity -> 金融产品交易其它信息业务流程表
        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_OTH", "WORKFLOW_ID",
                workflowId);
        return "1";
    }

    @Transactional
    @DTXTransaction
    @PostMapping("insertBatch")
    public String insertBatch(@RequestBody TrdCommonWflParam trdCommonWflParam) {
        if(trdCommonWflParam.getUpdate()){
            baseTradeProductDealCommService.delete(trdCommonWflParam.getWorkflowId());
        }
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        bussCommonMapperDelegateService.insertAutoClassifyBatch(trdCommonWflParam.getWflTrdProductDeals(), trdCommonWflParam.getWflDealAdds(),
                trdCommonWflParam.getWflPaDeals(), trdCommonWflParam.getWflTrdProductDealOth());
        return "Success";
    }

    @Transactional
    @DTXTransaction
    @PostMapping("doRevokeInsert")
    public String doRevokeInsert(@RequestBody TrdCommonWflParam trdCommonWflParam) {
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        bussCommonMapperDelegateService.insertAutoClassifyBatch(trdCommonWflParam.getWflTrdProductDeals(),
                trdCommonWflParam.getWflDealAdds()
        );
        return "Success";
    }
}
