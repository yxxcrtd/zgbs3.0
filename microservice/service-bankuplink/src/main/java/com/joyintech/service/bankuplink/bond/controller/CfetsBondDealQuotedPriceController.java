/*
 * 文件名：CfetsBondDealQuotedPriceController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.bankuplink.bond.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.joyintech.background.component.common.pack.common.security.User;
import com.joyintech.background.component.common.pack.common.util.IDGenerator;
import com.joyintech.background.component.uplink.interbank.business.dto.*;
import com.joyintech.background.component.uplink.interbank.common.CommonUtils;
import com.joyintech.background.component.uplink.interbank.common.DictCodeList;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.bankuplink.bond.dto.*;
import com.joyintech.service.bankuplink.bond.service.CfetsBondDealQuotedPriceService;
import com.joyintech.service.bankuplink.calc.service.SecCalRequestQuoteService;
import com.joyintech.service.bankuplink.cstp.dto.CstpSelectDto;
import com.joyintech.service.bankuplink.ibu.dto.BondQuoteInfoDto;
import com.joyintech.service.bankuplink.ibu.service.IbuComponentReceiveService;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.MstBondValuationInfo;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * CFETS现券买卖报价
 * 
 * @author zhangliang
 * @version 1.0 2020年5月11日
 * @see CfetsBondDealQuotedPriceController
 * @since 1.0
 */
@RestController("cfetsBondDealQuotedPriceCtl_dtx")
@RequestMapping("/CfetsBondDealQuotedPrice")
public class CfetsBondDealQuotedPriceController {
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsBondDealQuotedPriceService cfetsBondDealQuotedPriceService;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private IPlatformDate iPlatformDate;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductMapper finProductMapper;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private IbuComponentReceiveService ibuComponentReceiveService;

    @Autowired
    private SecCalRequestQuoteService secCalRequestQuoteService;

    /**
     * 委托指令查询
     * 
     * @param param 入参
     * @param page 页数
     * @param length 步长
     * @return 委托指令
     * @see
     * @since 1.0
     */
    @PostMapping("queryEntrustOrderList")
    public List<CfetsBondDealEntrustOrderDto> queryEntrustOrderList(@RequestBody Map<String, Object> param){
    	
        return cfetsBondDealQuotedPriceService.queryEntrustOrderList(param);
    }
    
    /**
     * 根据查询查询符合条件的委托列表分页
     * 
     * @param param 入参
     * @param page 页数
     * @param length 步长
     * @return 委托列表
     * @see
     * @since 1.0
     */
    @PostMapping("queryEntrustOrderPage/{page}/{length}")
    public Page<CfetsBondDealEntrustOrderDto> queryEntrustOrderPage(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                    @PathVariable("length") int length){
        return cfetsBondDealQuotedPriceService.queryEntrustOrderPage(page,length,param);
    }
    
    /**
     * 根据查询查询符合条件的委托列表
     * 
     * @param param 入参
     * @param page 页数
     * @param length 步长
     * @return 委托列表
     * @see
     * @since 1.0
     */
    @PostMapping("queryEntrustInfoList/{page}/{length}")
    public Page<SelectOptionDto> queryEntrustInfoList(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                      @PathVariable("length") int length){
        return cfetsBondDealQuotedPriceService.queryEntrustInfoList(page,length,param);
    }

    // ZGZGST-1936:CFETS历史报价查询-指令代码使用文本框 fuyongsheng 20210904 add start
    /**
     * 根据查询查询符合条件的历史委托列表
     *
     * @param param 入参
     * @param page 页数
     * @param length 步长
     * @return 委托列表
     * @see
     * @since 1.0
     */
    @PostMapping("queryHisEntrustInfoList/{page}/{length}")
    public Page<SuggestDto> queryHisEntrustInfoList(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                      @PathVariable("length") int length){
        return cfetsBondDealQuotedPriceService.queryHisEntrustInfoList(page,length,param);
    }
    // ZGZGST-1936:CFETS历史报价查询-指令代码使用文本框 fuyongsheng 20210904 add end

    /**
     * 根据查询查询符合条件的报价编号
     * 
     * @param param 入参
     * @param page 页数
     * @param length 步长
     * @return 报价编号
     * @see
     * @since 1.0
     */
    @PostMapping("queryCfetsQuoteIdList/{page}/{length}")
    public Page<SuggestDto> queryCfetsQuoteIdList(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                    @PathVariable("length") int length){
        return cfetsBondDealQuotedPriceService.queryCfetsQuoteIdList(page,length,param);
    }
    
    /**
     * 现券对话报价新增<br />
     * 对话报价信息入流程表
     * 
     * @param cfetsBondDealDialogueQuoteDto 现券对话报价信息
     * @return boolean 是否成功
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("insertWflBondDialogueQuote")
    public boolean insertWflBondDialogueQuote(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        return cfetsBondDealQuotedPriceService.insertWflBondDialogueQuote(cfetsBondDealDialogueQuoteDto);
    }
    
    /**
     * 现券对话报价新增<br />
     * 对话报价信息入库
     * 
     * @param cfetsBondDealDialogueQuoteDto 现券对话报价信息
     * @return boolean 是否成功
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("insertBondDialogueQuote")
    public boolean insertBondDialogueQuote(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        return cfetsBondDealQuotedPriceService.insertBondDialogueQuote(cfetsBondDealDialogueQuoteDto);
    }
    
    /**
     * 现券对话报价新增发送<br />
     * 根据传入的对话报价新增进行发送，并更新全部报价信息
     * 
     * @param inputBean BondDialogueQuoteDto 现券对话报价信息
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("sendNewBondDialogueQuote")
    public boolean sendNewBondDialogueQuote(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        return cfetsBondDealQuotedPriceService.sendNewBondDialogueQuote(cfetsBondDealDialogueQuoteDto);
    }
    
    /**
     * 现券对话报价新增发送<br />
     * 根据传入的对话报价新增进行发送，并更新全部报价信息
     * 
     * @param inputBean BondDialogueQuoteDto 现券对话报价信息
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("sendUpdateBondDialogueQuote")
    public boolean sendUpdateBondDialogueQuote(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        return cfetsBondDealQuotedPriceService.sendUpdateBondDialogueQuote(cfetsBondDealDialogueQuoteDto);
    }
    
    /**
     * 本方撤销请求回复；<br />
     * 做市方回复非做市方请求报价
     * 操作类型：本方新增请求回复
     * @param quoteId String 报价编号
     * @param attrMap Map<String, String>
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("sendBondRequestQuoteNewReply")
    public boolean sendBondRequestQuoteNewReply(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        return cfetsBondDealQuotedPriceService.sendBondRequestQuoteNewReply(cfetsBondDealDialogueQuoteDto);
    }
    // add by wangzhuangzhuang 新增请求画面 20200520 start
    
    /**
     * 现券对话报价新增<br />
     * 对话报价信息入流程表
     * 
     * @param cfetsBondDealDialogueQuoteDto 现券对话报价信息
     * @return boolean 是否成功
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("insertWflBondRequestQuote")
    public boolean insertWflBondRequestQuote(@RequestBody CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        return cfetsBondDealQuotedPriceService.insertWflBondRequestQuote(cfetsBondDealRequestQuoteDto);
    }
    /**
     * 现券请求报价新增<br />
     * 请求报价信息入库
     * 
     * @param cfetsBondDealRequestQuoteDto 现券请求报价信息
     * @return boolean 是否成功
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("insertBondRequestQuote")
    public boolean insertBondRequestQuote(@RequestBody CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        return cfetsBondDealQuotedPriceService.insertBondRequestQuote(cfetsBondDealRequestQuoteDto);
    }
    /**
     * 现券请求报价新增<br />
     * 请求报价信息入库
     * 
     * @param cfetsBondDealRequestQuoteDto 现券请求报价信息
     * @return boolean 是否成功
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("sendBondRequestQuote")
    public boolean sendBondRequestQuote(@RequestBody CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        return cfetsBondDealQuotedPriceService.sendBondRequestQuote(cfetsBondDealRequestQuoteDto);
    }
    /**
     * 现券请求报价回复成交<br />
     * 报价回复成交发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealRequestQuoteDto 现券请求报价信息
     * @return boolean 是否成功
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("sendBondRequestQuoteDeal")
    public boolean sendBondRequestQuoteDeal(@RequestBody CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        return cfetsBondDealQuotedPriceService.sendBondRequestQuoteDeal(cfetsBondDealRequestQuoteDto);
    }
    
    /**
     * 现券请求报价修改<br />
     * 报价修改发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealRequestQuoteDto 现券请求报价信息
     * @return boolean 是否成功
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("sendBondRequestQuoteModify")
    public boolean sendBondRequestQuoteModify(@RequestBody CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        return cfetsBondDealQuotedPriceService.sendBondRequestQuoteModify(cfetsBondDealRequestQuoteDto);
    }
    // add by wangzhuangzhuang 新增请求画面 20200520 end
    
    /**
     * 现券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     * 
     * @param pageNum int 页码，从1开始
     * @param pageSize int 分页大小
     * @param inputBean BondQuoteSearchDto 现券报价查询条件
     * @param userInfo User 登录用户信息
     * @return Page<BondQuoteExtDto>
     */
    @PostMapping("getDayOfferTableList")
    public List<BondQuoteInfoDto> getDayOfferTableList(@RequestBody Map<String, Object> param){
        return cfetsBondDealQuotedPriceService.getDayOfferTableList(param);
    }
    
    /**
     * 现券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     * 
     * @param pageNum int 页码，从1开始
     * @param pageSize int 分页大小
     * @param inputBean BondQuoteSearchDto 现券报价查询条件
     * @param userInfo User 登录用户信息
     * @return Page<BondQuoteExtDto>
     */
    @PostMapping("getOrderOfferTableList")
    public List<BondQuoteInfoDto> getOrderOfferTableList(@RequestBody Map<String, Object> param){
        return cfetsBondDealQuotedPriceService.getOrderOfferTableList(param);
    }
    
    /**
     * 现券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     * 
     * @param pageNum int 页码，从1开始
     * @param pageSize int 分页大小
     * @param inputBean BondQuoteSearchDto 现券报价查询条件
     * @param userInfo User 登录用户信息
     * @return Page<BondQuoteExtDto>
     */
    @PostMapping("getMarketOfferTableList")
    public List<BondQuoteInfoDto> getMarketOfferTableList(@RequestBody Map<String, Object> param){
        return cfetsBondDealQuotedPriceService.getMarketOfferTableList(param);
    }
    
    /**
     * 现券对话报价查询<br />
     * 根据主键查询对话报价信息
     * 
     * @param quoteId String 报价编号
     * @return BondDialogueQuoteDto
     */
    @PostMapping("getBondDialogueQuote")
    public CfetsBondDealDialogueQuoteDto getBondDialogueQuote(@RequestBody Map<String, Object> param){
        return cfetsBondDealQuotedPriceService.getBondDialogueQuote(param);
    }
    
    
    
    @PostMapping("getCfetsBondDealDialogueQuoteDtoList")
    public List<CfetsBondDealDialogueQuoteDto> getCfetsBondDealDialogueQuoteDtoList(@RequestBody Map<String, Object> param){
        return cfetsBondDealQuotedPriceService.getCfetsBondDealDialogueQuoteDtoList(param);
    }
    
    @PostMapping("getCfetsBondDealRequestQuoteDtoList")
    public List<CfetsBondDealRequestQuoteDto> getCfetsBondDealRequestQuoteDtoList(@RequestBody Map<String, Object> param){
        return cfetsBondDealQuotedPriceService.getCfetsBondDealRequestQuoteDtoList(param);
    }
    
    // add by wangzhuangzhuang 报价双击查看详情 20200521 start
    /**
     * 现券请求报价查询<br />
     * 根据主键查询请求报价信息
     * 
     * @param quoteId String 报价编号
     * @return BondRequestQuoteDto
     */
    @PostMapping("getBondRequestQuote")
    public CfetsBondDealRequestQuoteDto getBondRequestQuote(@RequestBody Map<String, Object> param){
        return cfetsBondDealQuotedPriceService.getBondRequestQuote(param);
    }

    // add by wangzhuangzhuang 报价双击查看详情 20200521 end
    
    /**
     * 调用算法计算相关金额元素
     * 
     * @param cfetsBondDealDialogueQuoteDto 画面对象
     * @return 处理后的画面对象
     * @see
     * @since 1.0
     */
    @PostMapping("calcAmt")
    public CfetsBondDealDialogueQuoteDto calcAmt(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        return cfetsBondDealQuotedPriceService.calcAmt(cfetsBondDealDialogueQuoteDto);
    }
    
    /**
     * 查询委托已完成额度
     * 
     * @param map 入参
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("queryEntrustDealedAmt")
    public BigDecimal queryEntrustDealedAmt(@RequestBody Map<String, Object> map){
        return cfetsBondDealQuotedPriceService.queryEntrustDealedAmt(map);
    }

    /**
     * 现券对话报价撤回；类似“撤回邮件”<br />
     * 报价撤回发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealDialogueQuoteDto 对话报价对象
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("sendBondDialogueQuoteRevoke")
    public boolean sendBondDialogueQuoteRevoke(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        return cfetsBondDealQuotedPriceService.sendBondDialogueQuoteRevoke(cfetsBondDealDialogueQuoteDto);
    }
    
    /**
     * 现券请求报价撤回；类似“撤回邮件”<br />
     * 报价撤回发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealDialogueQuoteDto 对话报价对象
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("sendBondRequestQuoteRevoke")
    public boolean sendBondRequestQuoteRevoke(@RequestBody CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        return cfetsBondDealQuotedPriceService.sendBondRequestQuoteRevoke(cfetsBondDealRequestQuoteDto);
    }
    
    /**
     * 现券请求报价撤回；类似“撤回邮件”<br />
     * 报价撤回发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealDialogueQuoteDto 对话报价对象
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("sendBondRequestQuoteRevokeReply")
    public boolean sendBondRequestQuoteRevokeReply(@RequestBody CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        return cfetsBondDealQuotedPriceService.sendBondRequestQuoteRevokeReply(cfetsBondDealRequestQuoteDto);
    }
    
    /**
     * 现券请求报价撤回；类似“撤回邮件”<br />
     * 报价撤回发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealDialogueQuoteDto 对话报价对象
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("bondDialogueQuoteRestore")
    public boolean bondDialogueQuoteRestore(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        return cfetsBondDealQuotedPriceService.bondDialogueQuoteRestore(cfetsBondDealDialogueQuoteDto);
    }
    
    /**
     * 现券请求报价撤回；类似“撤回邮件”<br />
     * 报价撤回发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealRequestQuoteDto 请求报价对象
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("sendBondRequestQuoteRestore")
    public boolean sendBondRequestQuoteRestore(@RequestBody CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        return cfetsBondDealQuotedPriceService.sendBondRequestQuoteRestore(cfetsBondDealRequestQuoteDto);
    }
    
    /**
     * 现券请求报价撤回；类似“撤回邮件”<br />
     * 报价撤回发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealDialogueQuoteDto 对话报价对象
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("sendBondDialogueQuoteDeal")
    public boolean sendBondDialogueQuoteDeal(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        return cfetsBondDealQuotedPriceService.sendBondDialogueQuoteDeal(cfetsBondDealDialogueQuoteDto);
    }
    
    /**
     * 现券请求报价回退；把失败状态还原成待处理<br />
     * 报价回退发送处理，更新接口返回数据
     * 操作类型：本方报价回退
     * @param quoteId String 报价编号
     * @param attrMap Map<String, String>
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("sendBondDialogueQuoteRefuse")
    public boolean sendBondDialogueQuoteRefuse(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        return cfetsBondDealQuotedPriceService.sendBondDialogueQuoteRefuse(cfetsBondDealDialogueQuoteDto);
    }
    
    /**
     * 
     * 删除请求报价的主表，主表历史，明细，明细历史数据
     * @param quoteId
     * @return QuoteResultDto
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("deleteBondRequestQuote")
	public boolean deleteBondRequestQuote(String quoteId) {
		int affectRows = cfetsBondDealQuotedPriceService.doDeletePtlPositionHoldByQuoteId(quoteId);
		return cfetsBondDealQuotedPriceService.deleteBondRequestQuote(quoteId);
	}
    
    /**
     * 
     * 删除对话报价的主表，主表历史，明细，明细历史数据
     * @param quoteId
     * @return QuoteResultDto
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("deleteBondDialogueQuote")
	public boolean deleteBondDialogueQuote(String quoteId) {
		int affectRows = cfetsBondDealQuotedPriceService.doDeletePtlPositionHoldByQuoteId(quoteId);
		return cfetsBondDealQuotedPriceService.deleteBondDialogueQuote(quoteId);
	}

    /**
     * 现券报价批量新增发送<br />
     * @param param Map<String, Object>
     * @param QuoteResultBatDto
     */
    @SuppressWarnings("unchecked")
    @PostMapping("batchNewBondDialogueQuote/{duplicateCheck}")
    @Transactional
    @DTXTransaction
    public QuoteBatchResult batchNewBondDialogueQuote(
                                                      @RequestBody Map<String, Object> param, 
                                                      @PathVariable("duplicateCheck") boolean duplicateCheck) {
        long successCount=0L, failCount=0L;
        List<String> errMsgList = new ArrayList<>();
        List<BondQuoteImportPreviewDto> inputList = new ArrayList<>();
        QuoteBatchResult result = new QuoteBatchResult();
        result.setSuccessAccout(0L);

        try {
            Map<String, Object> userMap = (Map<String, Object>)param.get("userInfo");
            User userInfo = new User();
            userInfo.setUserName(userMap.get("userName").toString());
            userInfo.setDeptCode(userMap.get("deptCode").toString());

            List<Map<String, Object>> inputMapList = (List<Map<String, Object>>)param.get("inputList");
            inputList = JSONObject.parseObject(JSON.toJSONString(inputMapList),
                new TypeReference<List<BondQuoteImportPreviewDto>>() {});
            
            // 平台日期
            Date platDate = iPlatformDate.getSysDate();
            
            List<BondDialogueQuoteDto> dialogueList = getDialogueList(inputList, duplicateCheck, platDate);
            List<BondRequestQuoteDto> requestList = getRequestList(inputList, duplicateCheck, platDate);

            if (CollectionUtils.isNotEmpty(dialogueList)) {
                QuoteResultBatDto dialogueResult = 
                    cfetsBondDealQuotedPriceService.batchNewBondDialogueQuote(dialogueList, userInfo);
                successCount += dialogueResult.getSuccessAccout();
                failCount += dialogueResult.getFailAccout();
                if (CollectionUtils.isNotEmpty(dialogueResult.getErrMsgList())) {
                    errMsgList.addAll(dialogueResult.getErrMsgList());
                }
            }
            if (CollectionUtils.isNotEmpty(requestList)) {
                QuoteResultBatDto requestResult = 
                    cfetsBondDealQuotedPriceService.batchNewBondRequestQuote(requestList, userInfo);
                successCount += requestResult.getSuccessAccout();
                failCount += requestResult.getFailAccout();
                if (CollectionUtils.isNotEmpty(requestResult.getErrMsgList())) {
                    errMsgList.addAll(requestResult.getErrMsgList());
                }
            }
        } catch(Exception ex) {
            log.error(ex.getMessage());
            successCount = 0L;
            failCount = inputList.size();
            errMsgList.add(ex.getMessage());
        } finally {
            result.setSuccessAccout(successCount);
            result.setFailAccout(failCount);
            if (successCount <= 0) {
                errMsgList.add(cfetsBondDealQuotedPriceService.getMessage("QUOTE_UPLOAD_028"));
            }
            result.setErrMsgList(errMsgList);
        }

        return result;
    }

    /**
     * 重复数据校验，返回带校验信息的数据对象列表
     * @param inputList List<BondQuoteImportPreviewDto> 不为空
     * @param List<BondQuoteImportPreviewDto>
     */
    @PostMapping("bondQuoteListDuplicateCheck")
    public List<BondQuoteImportPreviewDto> bondQuoteListDuplicateCheck(
                                              @RequestBody List<BondQuoteImportPreviewDto> inputList) {
        // 平台日期
        Date platDate = iPlatformDate.getSysDate();
        
        List<BondDialogueQuoteDto> dialogueList = getDialogueList(inputList, true, platDate);
        List<BondRequestQuoteDto> requestList = getRequestList(inputList, true, platDate);

        if (CollectionUtils.isNotEmpty(dialogueList)) {
            List<String> quoteIdList = dialogueList.parallelStream().map(
                p -> p.getIbuQuoteBondDto().getQuoteId()).collect(Collectors.toList());
            List<String> detIdList = dialogueList.parallelStream().filter(
                p -> p.getIbuQuoteBondDetDto() != null).map(
                    q -> q.getIbuQuoteBondDetDto().getDetId()).collect(Collectors.toList());

            validatedDuplicate(inputList, getFilter(DictCodeList.报价系统区分类型_现券对话报价.getValue()), 
                quoteIdList, detIdList, false);
        }
        if (CollectionUtils.isNotEmpty(requestList)) {
            List<String> quoteIdList = requestList.parallelStream().map(
                p -> p.getIbuQuoteBondDto().getQuoteId()).collect(Collectors.toList());
            List<String> detIdList = requestList.parallelStream().filter(
                p -> CollectionUtils.isNotEmpty(p.getIbuQuoteBondDetDtoList())).flatMap(
                    q -> q.getIbuQuoteBondDetDtoList().stream().map(x -> x.getDetId()).collect(
                        Collectors.toList()).stream()).collect(Collectors.toList());

            validatedDuplicate(inputList, getFilter(DictCodeList.报价系统区分类型_现券请求报价.getValue()),
                quoteIdList, detIdList, true);
        }
 
        return inputList;
    }

    @PostMapping("convertToQuoteResultDto/{duplicateCheck}")
    public QuoteResultDto convertToQuoteResultDto(@RequestBody BondQuoteImportPreviewDto previewDto,
                                                  @PathVariable("duplicateCheck") boolean duplicateCheck) {
        QuoteResultDto quoteResult = new QuoteResultDto(false);
        
        if (previewDto != null) {
            Date platDate = iPlatformDate.getSysDate();
            if (DictCodeList.报价系统区分类型_现券对话报价.getValue().equals(previewDto.getQuoteType())) {
                List<BondDialogueQuoteDto> dialogueList = getDialogueList(Arrays.asList(previewDto), duplicateCheck, platDate);
                if (CollectionUtils.isNotEmpty(dialogueList)) {
                    quoteResult = new QuoteResultDto();
                    quoteResult.setStatus(true);
                    quoteResult.setBondDialogueQuote(dialogueList.get(0));
                }
            }
            else if (DictCodeList.报价系统区分类型_现券请求报价.getValue().equals(previewDto.getQuoteType())) {
                List<BondRequestQuoteDto> requestList = getRequestList(Arrays.asList(previewDto), duplicateCheck, platDate);
                if (CollectionUtils.isNotEmpty(requestList)) {
                    quoteResult = new QuoteResultDto();
                    quoteResult.setStatus(true);
                    quoteResult.setBondRequestQuote(requestList.get(0));
                }
            }
        }

        return quoteResult;
    }

    private Predicate<BondQuoteImportPreviewDto> getFilter(String quoteType) {
        return p -> StringUtils.equals(quoteType, p.getQuoteType());
    }

    /**
     *“重复数据验证”
     */
    private void validatedDuplicate(List<BondQuoteImportPreviewDto> validatedList,
                                    Predicate<BondQuoteImportPreviewDto> filter,
                                    List<String> quoteIdList, List<String> detIdList,
                                    boolean isOneToMulti) {
        List<String> existsQuoteIdList = null;
        List<String> existsDetIdList = null;

        validatedList.parallelStream().filter(filter).forEach(p -> {
            //region 检查参数数据本身中是否有重复数据
            if (isOneToMulti) {
                // 只检查detIdList中是否有重复
                if (CollectionUtils.isNotEmpty(detIdList)
                    && detIdList.stream().filter(q -> StringUtils.equals(q,
                        String.valueOf(p.getBondQuoteDetDistinctKey()))).count() > 1) {
                    p.addValidationErrorMsg(cfetsBondDealQuotedPriceService.getMessage("QUOTE_UPLOAD_003"));
                }
            }
            else {
                // 检查主信息是否有重复
                if (CollectionUtils.isNotEmpty(quoteIdList)
                    && quoteIdList.stream().filter(q -> StringUtils.equals(q,
                        String.valueOf(p.getBondQuoteDistinctKey()))).count() > 1) {
                    p.addValidationErrorMsg(cfetsBondDealQuotedPriceService.getMessage("QUOTE_UPLOAD_004"));
                }

                // 检查明细是否有重复
                if (CollectionUtils.isNotEmpty(detIdList)
                    && detIdList.stream().filter(q -> StringUtils.equals(q,
                        String.valueOf(p.getBondQuoteDetDistinctKey()))).count() > 1) {
                    p.addValidationErrorMsg(cfetsBondDealQuotedPriceService.getMessage("QUOTE_UPLOAD_003"));
                }
            }
            //endRegion
        });

        // 根据主键检查数据库中是否有数据与参数中的数据重复
        if (CollectionUtils.isNotEmpty(quoteIdList)) {
            existsQuoteIdList = cfetsBondDealQuotedPriceService.
                selectQuoteIdsByIdList(quoteIdList.stream().distinct().collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(detIdList)) {
            existsDetIdList = cfetsBondDealQuotedPriceService.
                selectDetIdsByIdList(detIdList.stream().distinct().collect(Collectors.toList()));
        }

        if (CollectionUtils.isNotEmpty(existsQuoteIdList) || CollectionUtils.isNotEmpty(existsDetIdList)) {
            Iterator<BondQuoteImportPreviewDto> dupcateIterator = validatedList.parallelStream().filter(filter).iterator();
            while (dupcateIterator.hasNext()) {
                BondQuoteImportPreviewDto previewDto = dupcateIterator.next();
                if (isOneToMulti) {
                    // 只检查是否有明细记录重复
                    if (CollectionUtils.isNotEmpty(existsDetIdList) && existsDetIdList.contains(previewDto.getBondQuoteDetDistinctKey())) {
                        previewDto.addValidationErrorMsg(cfetsBondDealQuotedPriceService.getMessage("QUOTE_UPLOAD_005"));
                    }
                }
                else {
                    if (CollectionUtils.isNotEmpty(existsQuoteIdList) && existsQuoteIdList.contains(previewDto.getBondQuoteDistinctKey())) {
                        previewDto.addValidationErrorMsg(cfetsBondDealQuotedPriceService.getMessage("QUOTE_UPLOAD_006"));
                    }
                    if (CollectionUtils.isNotEmpty(existsDetIdList) && existsDetIdList.contains(previewDto.getBondQuoteDetDistinctKey())) {
                        previewDto.addValidationErrorMsg(cfetsBondDealQuotedPriceService.getMessage("QUOTE_UPLOAD_005"));
                    }
                }
            }
        }

    }

    /**
     * 获取对话报价数据列表 
     */
    private List<BondDialogueQuoteDto> getDialogueList(List<BondQuoteImportPreviewDto> inputList, boolean duplicateCheck, Date platDate) {
        // 放在这，若本批次数据中有重复数据，则会发生主键制约错误

        List<BondDialogueQuoteDto> dialogueList = inputList.parallelStream().filter(
            p -> DictCodeList.报价系统区分类型_现券对话报价.getValue().equals(p.getQuoteType())).map(q -> {
            BondDialogueQuoteDto dto = new BondDialogueQuoteDto();
            IbuQuoteBondDto bondDto = new IbuQuoteBondDto();
            IbuQuoteBondDetDto bondDetDto = new IbuQuoteBondDetDto();

            String detId = IDGenerator.generate();

            setBondDto(q, bondDto, platDate);
            setBondDetDto(q, bondDetDto, detId, bondDto.getSettleDate());

            // 待补充值
            dto.setAttrMap(new HashMap<String, String>());
            dto.setIbuQuoteBondDto(bondDto);
            dto.setIbuQuoteBondDetDto(bondDetDto);
            return dto;
        }).collect(Collectors.toList());

        return dialogueList;
    }

    /**
     * 设置现卷报价明细信息
     * @param q CfetsBondDealEntrustOrderDto
     * @param bondDetDto IbuQuoteBondDetDto
     * @param quoteId String
     * @param detId String
     */
    private void setBondDetDto(CfetsBondDealEntrustOrderDto q, IbuQuoteBondDetDto bondDetDto, String detId, Date settleDate) {
        // 明细表设值
        bondDetDto.setVersion(-1L);
        bondDetDto.setTradeStatus(DictCodeList.银行间上行报价状态_新增中.getValue());
        bondDetDto.setSendStatus(DictCodeList.银行间上行发送状态_已发送.getValue());
        bondDetDto.setExerYield(q.getExerYield());
        bondDetDto.setCpPartyId(q.getCounterId());
        bondDetDto.setCpTrader(q.getCounterTrader());
        bondDetDto.setPrinAmt(q.getEntrustAmt().multiply(BigDecimal.valueOf(10000.0d)));
        bondDetDto.setUnitCprice(q.getPrice());
        bondDetDto.setYield(q.getMatureYield());
        bondDetDto.setQuoteId(q.getQuoteId());
        bondDetDto.setDetId(detId);
        
        // 对话报价需要调用算法计算金额要素
        if (DictCodeList.报价系统区分类型_现券对话报价.getValue().equals(q.getQuoteType())) {
            bondDetDto.setShareAmt(q.getEntrustAmt().multiply(BigDecimal.valueOf(100.0d)));
            if (StringUtil.isNotEmpty(q.getPrice())){
                bondDetDto.setCalculateField(BussinessDictCodeList.计算选择方式_净价.getValue());
            }
            else if (StringUtil.isNotEmpty(q.getMatureYield())){
                bondDetDto.setCalculateField(BussinessDictCodeList.计算选择方式_到期收益率.getValue());
            }
            else if (StringUtil.isNotEmpty(q.getExerYield())){
                bondDetDto.setCalculateField(BussinessDictCodeList.计算选择方式_行权收益率.getValue());
            }
            
            CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
            cfetsBondDealDialogueQuoteDto.setFinprodId(q.getFinprodId());
            cfetsBondDealDialogueQuoteDto.setAvaNum(bondDetDto.getPrinAmt());
            cfetsBondDealDialogueQuoteDto.setSettleDate(settleDate);
            cfetsBondDealDialogueQuoteDto.setCalculateField(bondDetDto.getCalculateField());
            if (StringUtil.equals(bondDetDto.getCalculateField(), BussinessDictCodeList.计算选择方式_净价.getValue())){
                cfetsBondDealDialogueQuoteDto.setPrice(q.getPrice());
            }
            else if (StringUtil.equals(bondDetDto.getCalculateField(), BussinessDictCodeList.计算选择方式_到期收益率.getValue())){
                cfetsBondDealDialogueQuoteDto.setMatureYield(q.getMatureYield());
            }
            else if (StringUtil.equals(bondDetDto.getCalculateField(), BussinessDictCodeList.计算选择方式_行权收益率.getValue())){
                cfetsBondDealDialogueQuoteDto.setExerYield(q.getExerYield());
            }
            this.calcAmt(cfetsBondDealDialogueQuoteDto);
            
            bondDetDto.setUnitCprice(cfetsBondDealDialogueQuoteDto.getPrice());
            bondDetDto.setUnitInt(cfetsBondDealDialogueQuoteDto.getUnitInt());
            bondDetDto.setUnitFprice(cfetsBondDealDialogueQuoteDto.getUnitFprice());
            bondDetDto.setParValue(cfetsBondDealDialogueQuoteDto.getParValue());
            bondDetDto.setParValueAmt(cfetsBondDealDialogueQuoteDto.getParValueAmt());
            bondDetDto.setCpriceAmt(cfetsBondDealDialogueQuoteDto.getCpriceAmt());
            bondDetDto.setIntAmt(cfetsBondDealDialogueQuoteDto.getIntAmt());
            bondDetDto.setFpriceAmt(cfetsBondDealDialogueQuoteDto.getFpriceAmt());
            bondDetDto.setYield(ArithmeticUtil.divide(cfetsBondDealDialogueQuoteDto.getMatureYield(), new BigDecimal(100), 6));
            bondDetDto.setExerYield(ArithmeticUtil.divide(cfetsBondDealDialogueQuoteDto.getExerYield(), new BigDecimal(100), 6));
        }
    }

    /**
     * 设置现劵报价主信息
     * @param q CfetsBondDealEntrustOrderDto
     * @param bondDto IbuQuoteBondDto
     * @param quoteId String
     */
    private void setBondDto(CfetsBondDealEntrustOrderDto q, IbuQuoteBondDto bondDto, Date platDate) {
        // 主表设值
        bondDto.setVersion(-1L);
        bondDto.setbVersion(-1L);
        bondDto.setTradeStatus(DictCodeList.银行间上行报价状态_新增中.getValue());
        bondDto.setSendStatus(DictCodeList.银行间上行发送状态_已发送.getValue());
        bondDto.setEntrustId(q.getEntrustId());
        bondDto.setPortfolioId(q.getPortfolioId());
        bondDto.setQuoteType(q.getQuoteType());
        bondDto.setFinprodId(q.getFinprodId());
        bondDto.setPs(q.getPs());
        if (DictCodeList.报价系统区分类型_现券对话报价.getValue().equals(q.getQuoteType())) {
            bondDto.setQuoteRoleType(DictCodeList.银行间上行本方角色_询价方.getValue());
            bondDto.setExeType(DictCodeList.银行间上行操作类型_本方新增发送.getValue());
        }
        else {
            bondDto.setQuoteRoleType(DictCodeList.银行间上行本方角色_非做市方.getValue());
            bondDto.setExeType(DictCodeList.银行间上行操作类型_本方新增请求报价发送.getValue());
        }

        bondDto.setOuPartyId(q.getMySide());
        bondDto.setOuTraderId(q.getMySideTrader());
        bondDto.setDeliveryType(q.getSettleMethod());
        bondDto.setSettType(q.getSettleType());
        bondDto.setQuoteId(q.getQuoteId());
        bondDto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        bondDto.setBranch(0L);
        bondDto.setSettleDate(platDate);
        bondDto.setQuoteDate(platDate);
        if (StringUtil.equals(q.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加0.getValue())) {
            bondDto.setSettleSpeed(0L);
        }
        else if (StringUtil.equals(q.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加1.getValue())) {
            bondDto.setSettleSpeed(1L);
            // 交割日需要根据清算速度 + 1推算
            bondDto.setSettleDate(cfetsBondDealQuotedPriceService.getNextWorkDay(platDate));
        }
        bondDto.setShareAmt(q.getEntrustAmt().multiply(new BigDecimal(100)));
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(bondDto.getFinprodId());
        if (finProduct != null){
            bondDto.setSettCcy(finProduct.getCcy());
        }
        else{
            bondDto.setSettCcy(BussinessDictCodeList.币种_人民币.getValue());
        }
        bondDto.setEffTime(DateUtil.parse(DateUtil.format(platDate) + " "
                           + CacheUtil.getSysParam("ibuVar_quoteEffTime") + ":00"));
    }

    /**
     * 获取请求报价数据列表 
     */
    private List<BondRequestQuoteDto> getRequestList(List<BondQuoteImportPreviewDto> inputList,
                                                     boolean duplicateCheck, Date platDate) {
        List<BondDialogueQuoteDto> requestList = inputList.parallelStream().filter(
            p -> DictCodeList.报价系统区分类型_现券请求报价.getValue().equals(p.getQuoteType())).map(q -> {
                // 在本批次中，相同的主表信息，那么产生的主表主键仍然是一样的，否则无法处理请求报价一对多明细的关系
                // 若使用batchKey，若本批次数据中有重复数据，则会发生明细表主键制约错误
                String detId = IDGenerator.generate();

                BondDialogueQuoteDto dto = new BondDialogueQuoteDto();
                IbuQuoteBondDto bondDto = new IbuQuoteBondDto();
                IbuQuoteBondDetDto bondDetDto = new IbuQuoteBondDetDto();
                setBondDto(q, bondDto, platDate);
                setBondDetDto(q, bondDetDto, detId, bondDto.getSettleDate());
                dto.setIbuQuoteBondDto(bondDto);
                dto.setIbuQuoteBondDetDto(bondDetDto);
                return dto;
            }).collect(Collectors.toList());

        // 主表信息去重集合
        List<IbuQuoteBondDto> bondDtoList = requestList.parallelStream().map(
            p -> p.getIbuQuoteBondDto()).filter(
                CommonUtils.distinctByKey(b -> b.getQuoteId())).collect(Collectors.toList());

        // 子表信息集合
        List<IbuQuoteBondDetDto> bondDetDtoList = requestList.parallelStream().map(
            p -> p.getIbuQuoteBondDetDto()).collect(Collectors.toList());

        List<BondRequestQuoteDto> resultList = bondDtoList.parallelStream().map(p -> {
            BondRequestQuoteDto dto = new BondRequestQuoteDto();
            dto.setAttrMap(new HashMap<String, String>()); // 待补充值
            dto.setIbuQuoteBondDto(p);
            dto.setIbuQuoteBondDetDtoList(bondDetDtoList.parallelStream().filter(
                x -> StringUtils.equals(x.getQuoteId(), p.getQuoteId())).collect(
                    Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());

        return resultList;
    }
    
    /**
     * 发送网关处理
     * 
     * @param param 参数
     * @return
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("sendGateWayHandle")
    public boolean sendGateWayHandle(@RequestBody Map<String, Object> param){
        return cfetsBondDealQuotedPriceService.sendGateWayHandle(param);
    }
    
    /**
     * 查询债券最新估值
     * 
     * @param map 入参
     * @return 最新估值
     * @see
     * @since 1.0
     */
    @PostMapping("queryLastBondValuation")
    public MstBondValuationInfo queryLastBondValuation(@RequestBody Map<String, Object> param){
        return cfetsBondDealQuotedPriceService.queryLastBondValuation(param);
    }

    /**
     * 历史报价撤销
     * 
     * @param bondQuoteInfoDto 现券对象
     * @return 处理结果
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doHisRevoke")
    public boolean doHisRevoke(@RequestBody BondQuoteInfoDto bondQuoteInfoDto){
        return cfetsBondDealQuotedPriceService.doHisRevoke(bondQuoteInfoDto);
    }
    
    /**
     * 超时刷新机制
     * 
     * @param quoteId 报价编号
     * @return 返回结果
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @GetMapping("timeOutQuoteRefreshBond")
    public ResultBaseDto timeOutQuoteRefreshBond(String quoteId){
        return ibuComponentReceiveService.timeOutQuoteRefreshBond(quoteId);
    }
    
    /**
	 * 新增、修改和成交时，指令持仓流水 、现金流水处理
	 * add by zhangpei 
	 * 20201128
	 * @param dto
	 */
	@Transactional
	@DTXTransaction
	@PostMapping("doInsetOrUpdateDialogueForPtlPositionHold")
	public void doInsetOrUpdateDialogueForPtlPositionHold(@RequestBody CfetsBondDealDialogueQuoteDto dto) {
		cfetsBondDealQuotedPriceService.doInsetOrUpdateForPtlPositionHold(dto);
	}
	
	
	/**
	 * 新增、修改和成交时，指令持仓流水 、现金流水处理
	 * add by zhangpei 
	 * 20201128
	 * @param dto
	 */
	@Transactional
	@DTXTransaction
	@PostMapping("doInsetOrUpdateRequestForPtlPositionHold")
	public void doInsetOrUpdateRequestForPtlPositionHold(@RequestBody CfetsBondDealRequestQuoteDto dto) {
		cfetsBondDealQuotedPriceService.doInsetOrUpdateForPtlPositionHold(dto);
	}
	
	/**
	 * 删除工作流数据 add by amyli 20201230
	 * 
	 * @param dto
	 */
	@Transactional
	@DTXTransaction
	@PostMapping("doDeleteWflDataForBond")
	public boolean doDeleteWflDataForBond(@RequestBody Map<String, Object> param) {
		try {
			cfetsBondDealQuotedPriceService.doDeleteWflDataForBond(param);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	/**
	 * 删除预占的流水
	 * 
	 * @param dto
	 */
	@Transactional
	@DTXTransaction
	@PostMapping("doDeletePtlPositionHold")
	public boolean doDeletePtlPositionHold(@RequestBody Map<String, Object> param) {
		try {
			cfetsBondDealQuotedPriceService.doDeletePtlPositionHold(param);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

    /**
     *
     * 获取新建界面的资产代码联想下拉框框
     * <功能详细描述>
     * @param finprodId
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @PostMapping("getfinprodIdForAdd/{page}/{length}")
    public Page<SelectOptionDto> getfinprodIdForAdd(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                    @PathVariable("length") int length)
            throws Exception {
        Page<SelectOptionDto> temp = cfetsBondDealQuotedPriceService.getfinprodIdForAdd(page,length,param);
        return temp;
    }


    /**
     * 成交查询
     * @param map 查询
     * @return 分页查询结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @PostMapping("getCstpQryTableList")
    public List<CstpSelectDto> getCstpQryTableList(@RequestBody Map<String,Object> map) throws Exception{
        return cfetsBondDealQuotedPriceService.getCstpQryTableList(map);
    }

    @PostMapping("getWflEntrustDealedAmt")
    public BigDecimal getWflEntrustDealedAmt(@RequestBody Map<String, Object> map){
        return cfetsBondDealQuotedPriceService.getWflEntrustDealedAmt(map);
    }


    @ResponseBody
    @PostMapping("selectExportPdf")
    public List<CstpSelectDto> selectExportPdf(@RequestBody Map<String,Object> map) throws Exception{
        return cfetsBondDealQuotedPriceService.selectExportPdf(map);
    }

    @PostMapping("queryEntrustDealedAmt2")
    public BigDecimal queryEntrustDealedAmt2(@RequestBody Map<String, Object> map){
        return cfetsBondDealQuotedPriceService.queryEntrustDealedAmt2(map);
    }

    @PostMapping("getCurSecAmt")
    public BigDecimal getCurSecAmt(@RequestBody Map<String, Object> map){
        return secCalRequestQuoteService.getCurSecAmt(DateUtil.parse((String)map.get("settleDate")),(String)map.get("finprodId"));
    }

}
