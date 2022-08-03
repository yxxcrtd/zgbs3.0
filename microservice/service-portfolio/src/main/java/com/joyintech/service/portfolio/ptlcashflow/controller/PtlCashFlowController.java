/*
 * 文件名：PtlCashFlowController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年10月26日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.portfolio.ptlcashflow.controller;

import java.util.List;
import java.util.Map;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.TrdCommonWflParam;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;

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
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.service.portfolio.ptlcashflow.dto.PtlCashFlowDto;
import com.joyintech.service.portfolio.ptlcashflow.service.PtlCashFlowService;

/**
 * 自定义现金流
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年10月26日
 * @see PtlCashFlowController
 * @since 1.0
 */
@RestController("ptlCashFlowController_dtx")
@RequestMapping("/PtlCashFlow")
public class PtlCashFlowController {
    /**
     * ptlCashFlowService
     */
    @Autowired
    private PtlCashFlowService ptlCashFlowService;

    @Autowired
    private MapperDelegateService mapperDelegateService;

    /**
     * 
     * 获取业务编号
     * <功能详细描述>
     * @param param param
     * @return SuggestDto
     * @see
     * @since 1.0
     */
    @GetMapping("/getBusiNo")
    public List<SuggestDto> getBusiNo(String param){
        return ptlCashFlowService.getBusiNo(param);
    }
    /**
     * 
     * 获得组合代码
     * <功能详细描述>
     * @param param param
     * @param page page
     * @param length length
     * @return SuggestDto
     * @see
     * @since 1.0
     */
    @PostMapping("/getPortfolioId/{page}/{length}")
    public Page<SelectOptionDto> getPortfolioId(@RequestBody Map<String, Object> param,
                                                @PathVariable("page") int page,
                                                @PathVariable("length") int length){
        return ptlCashFlowService.getPortfolioId(page, length,param);
    }
    /**
     * 获取组合代码的下拉框
     * <功能详细描述>
     * @param param param
     * @return SuggestDto
     * @see
     * @since 1.0
     */
    @PostMapping("/getPortfolioIdToNew")
    public List<SelectOptionDto> getPortfolioIdToNew(@RequestBody Map<String, Object> param) {
        return ptlCashFlowService.getPortfolioIdToNew(param);
    }
    /**
     * 
     * 获得资产代码
     * <功能详细描述>
     * @param param param
     * @return SuggestDto
     * @see
     * @since 1.0
     */
    @GetMapping("/getFinprodMarketId")
    public List<SuggestDto> getFinprodMarketId(String param){
        return ptlCashFlowService.getFinprodMarketId(param);
    }
    
    /**
     * 画面查询方法
     * @param param param 
     * @param page page
     * @param length length
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<PtlCashFlowDto> selectAll(@RequestBody Map<String, Object> param,
         @PathVariable("page") int page, @PathVariable("length") int length)  {

        param.put("validStatus_02", DictCodeListExt1.交易流程状态_有效.getValue());
        param.put("validStatus_01", DictCodeListExt1.交易流程状态_流程中.getValue());
        param.put("validStatus_03", DictCodeListExt1.交易流程状态_撤销.getValue());
       // param.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        param.put("pingTypeBuy", DictCodeListExt1.平安业务类型_存款类资产买入.getValue());
        param.put("pingTypeSale", DictCodeListExt1.平安业务类型_存款类资产卖出.getValue());
        param.put("tradeFlowStatus", DictCodeListExt1.交易流程状态_流程中.getValue());

        return ptlCashFlowService.selectForPage(page, length, param);
    }
    /**
     * 
     * 获取交易对手
     * <功能详细描述>
     * @param param param
     * @param page page
     * @param length length
     * @return SuggestDto
     * @see
     * @since 1.0
     */
    @PostMapping("getCounterId/{page}/{length}")
    public Page<SelectOptionDto> getCounterId(@RequestBody Map<String, Object> param,
                                              @PathVariable("page") int page,
                                              @PathVariable("length") int length){
        return ptlCashFlowService.getCounterId(page, length, param);
    }
    /**
     * 新建画面资产代码的联想文本框
     * <一句话功能简述>
     * <功能详细描述>
     * @param param param
     * @param page page
     * @param length length
     * @return SuggestDto
     * @see
     * @since 1.0
     */
    @PostMapping("/getFinprodIdToNew/{page}/{length}")
    public Page<SelectOptionDto> getFinprodIdToNew(@RequestBody Map<String, Object> param,
                                                   @PathVariable("page") int page,
                                                   @PathVariable("length") int length) {
        return ptlCashFlowService.getFinprodIdToNew(page, length,param);
    }
    /**
     * 
     * 查看和撤销画面展示entity
     * <功能详细描述>
     * @param map map
     * @return PtlCashFlowDto
     * @see
     * @since 1.0
     */
    @PostMapping("/getEntity")
    public PtlCashFlowDto getEntity(@RequestBody Map<String, Object> map) {
        return ptlCashFlowService.getEntity(map);
    }
    /**
     * 删除
     * 
     * @param dto
     *            页面选中的数据
     * @return 结果
     * @throws Exception
     *             异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody PtlCashFlowDto dto) {
        ptlCashFlowService.deleteWflData(dto.getWorkflowId());
        return ptlCashFlowService.delete(dto);
    }
    
    /**
     * 插入
     * 
     * @param dto
     *            页面选中的数据
     * @return 结果
     * @throws Exception
     *             异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("insert")
    public int insert(@RequestBody PtlCashFlowDto dto) {
        return ptlCashFlowService.insert(dto);
    }
    
    /**
     * 
     * 流程作废
     * <功能详细描述>
     * @param workflowId
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @GetMapping("doDelete")
    public void doDelete(String workflowId) {
        ptlCashFlowService.doDelete(workflowId);
    }
    /**
     * 关联业务编号联想文本框
     * <功能详细描述>
     * @param param param
     * @return SuggestDto
     * @see
     * @since 1.0
     */
    @PostMapping("/getFTradeId")
    public List<SelectOptionDto> getFTradeId(@RequestBody Map<String, Object> param) {
        return ptlCashFlowService.getFTradeId(param);
    }

    @Transactional
    @DTXTransaction
    @PostMapping("insertAutoClassifyBatch")
    public String insertAutoClassifyBatch(@RequestBody TrdCommonWflParam trdCommonWflParam) {
        mapperDelegateService.insertAutoClassifyBatch(trdCommonWflParam.getWflTrdProductDeals(),
                trdCommonWflParam.getWflStlPayOrdersDtos(),
                trdCommonWflParam.getWflPaDeals(),
                trdCommonWflParam.getWflDealAdds());
        return "Success";
    }

    /**
     * 投管接口：其他现金流交易信息
     * 
     * @param dto 接口数据
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("insertCashFlowInfo")
    public int insertCashFlowInfo(@RequestBody PtlCashFlowDto dto) {
        return ptlCashFlowService.insert(dto);
    }
}
