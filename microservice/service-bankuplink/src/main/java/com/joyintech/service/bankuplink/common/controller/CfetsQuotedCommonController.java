/*
 * 文件名：CfetsQuotedCommonController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.bankuplink.common.controller;

import com.joyintech.background.component.uplink.interbank.business.dto.ResultBaseDto;
import com.joyintech.background.component.uplink.interbank.common.DictCodeList;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.bankuplink.common.dto.CfetsQuotedTradeCommonDto;
import com.joyintech.service.bankuplink.common.dto.CfetsQuotedTradeFirstBondDto;
import com.joyintech.service.bankuplink.common.framework.thread.JoyinThreadPoolExecutor;
import com.joyintech.service.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.service.bankuplink.common.service.CfetsQuotedTradeCommonService;
import com.joyintech.service.bankuplink.ibu.service.IbuComponentReceiveService;
import com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto;
import com.joyintech.system.base.entity.SysUserAdd;
import com.joyintech.tams.common.base.entity.InterfCstpBjfxjg;
import com.joyintech.tams.invest.common.service.DateCalCommonService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
/**
 * 银行间上行共通Controller
 * 
 * @author zhangliang
 * @version 1.0 2020年5月13日
 * @see CfetsQuotedCommonController
 * @since 1.0
 */
@RestController("cfetsQuotedCommonCtl_dtx")
@RequestMapping("/CfetsQuotedCommon")
public class CfetsQuotedCommonController {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CfetsQuotedCommonController.class);

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private IbuComponentReceiveService ibuComponentReceiveService;

    @Autowired
    private CfetsQuotedTradeCommonService cfetsQuotedTradeCommonService;

    /**
     * DateCalCommonService
     */
    @Autowired
    private DateCalCommonService dateCalCommonService;
    //创建公共的线程池
    private final ExecutorService service = new JoyinThreadPoolExecutor(10);

    /**
     * 委托放弃处理
     * 
     * @param entrustId 委托代码
     * @return 执行是否成功
     * @see
     * @since 1.0
     */
    @GetMapping("entrustGiveUpHandle")
    public String entrustGiveUpHandle(String entrustId){
        return cfetsQuotedCommonService.entrustGiveUpHandle(entrustId);
    }
    
    /**
     * 委托批量放弃
     * @param param
     * @return
     */
    @Transactional
    @DTXTransaction
	@PostMapping("/entrustGiveUpHandleList")
	public List<ValidMessage> entrustGiveUpHandleList(@RequestBody Map<String,Object> param){
    	List<ValidMessage> errorList=new ArrayList<ValidMessage>();
    	@SuppressWarnings("unchecked")
		List<String> entrustIds=(List<String>)param.get("entrustIds");
    	if(StringUtil.isNotEmpty(entrustIds)){
    		for(String entrustId : entrustIds){
    			String flag  = cfetsQuotedCommonService.entrustGiveUpHandle(entrustId);
    			if(StringUtil.equals("1", flag)){
        			errorList.add(new ValidMessage("已接收且完成度为0指令,才能执行放弃操作！"+"指令编号："+entrustId));
        		}
    			else if(StringUtil.equals("2", flag)){
        			errorList.add(new ValidMessage("存在流程中的报价，不可放弃！"+"指令编号："+entrustId));
        		}
        	}
    	}
		return errorList;
	}
    
    /**
     * 查询本方交易员列表
     * 
     * @param param 入参
     * @param page 页数
     * @param length 步长
     * @return 委托列表
     * @see
     * @since 1.0
     */
    @PostMapping("queryMySideTraderPage/{page}/{length}")
    public Page<SelectOptionDto> queryMySideTraderPage(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                    @PathVariable("length") int length){
        return cfetsQuotedCommonService.queryMySideTraderPage(page,length,param);
    }
    
    
    /**
     * 查询对手方交易员列表
     * 
     * @param param 入参
     * @param page 页数
     * @param length 步长
     * @return 委托列表
     * @see
     * @since 1.0
     */
    @PostMapping("queryCounterSideTraderPage/{page}/{length}")
    public Page<SelectOptionDto> queryCounterSideTraderPage(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                    @PathVariable("length") int length){
        return cfetsQuotedCommonService.queryCounterSideTraderPage(page,length,param);
    }
    
    /**
     * 查询本方交易员列表
     * 
     * @param param 入参
     * @param page 页数
     * @param length 步长
     * @return 委托列表
     * @see
     * @since 1.0
     */
    @PostMapping("queryMySideTraderList")
    public List<SelectOptionDto> queryMySideTraderPage(@RequestBody Map<String, Object> param){
        return cfetsQuotedCommonService.queryMySideTraderList(param);
    }
    
    /**
     * 查询本方机构列表
     * 
     * @param param 入参
     * @param page 页数
     * @param length 步长
     * @return 本方机构列表
     * @see
     * @since 1.0
     */
    @PostMapping("queryMySidePage/{page}/{length}")
    public Page<SelectOptionDto> queryMySidePage(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                    @PathVariable("length") int length){
        return cfetsQuotedCommonService.queryMySidePage(page,length,param);
    }
    
    /**
     * 查询本方机构列表
     * 
     * @param param 入参
     * @param page 页数
     * @param length 步长
     * @return 本方机构列表
     * @see
     * @since 1.0
     */
    @PostMapping("queryCounterSidePage/{page}/{length}")
    public Page<SelectOptionDto> queryCounterSidePage(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                    @PathVariable("length") int length){
        return cfetsQuotedCommonService.queryCounterSidePage(page,length,param);
    }
    
    /**
     * 查询本方机构列表
     * 
     * @param param 入参
     * @param page 页数
     * @param length 步长
     * @return 本方机构列表
     * @see
     * @since 1.0
     */
    @PostMapping("queryMySideList")
    public List<SelectOptionDto> queryMySideList(@RequestBody Map<String, Object> param){
        return cfetsQuotedCommonService.queryMySideList(param);
    }
    
    /**
     * 查询本方机构列表
     * 
     * @param param 入参
     * @param page 页数
     * @param length 步长
     * @return 本方机构列表
     * @see
     * @since 1.0
     */
    @PostMapping("queryCounterSideList")
    public List<SelectOptionDto> queryCounterSideList(@RequestBody Map<String, Object> param){
        return cfetsQuotedCommonService.queryCounterSideList(param);
    }
    
    /**
     * 
     * 根据报价编号查询错误信息
     * @param quoteId String 报价代码
     * @return IbuQuoteErrlogDto 错误信息
     * @see
     * @since 1.0
     */
    @GetMapping("selectErrlog")
    public String selectErrlog(String quoteId){
        return cfetsQuotedCommonService.selectErrlog(quoteId);
    }
    
    /**
     * 更新对话报价的接口反馈信息
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("receiveBondDialogueRequest")
    public ResultBaseDto receiveBondDialogueRequest(@RequestBody Map<String, Object> map){
        //return ibuComponentReceiveService.updateBondDialogueQuoteInterfResp(map);
        ResultBaseDto resultBaseDto = new ResultBaseDto(true);
        logger.info("【CFETS接口接收-现券对话报价】处理业务数据线程开启" + map.toString());


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ibuComponentReceiveService.updateBondDialogueQuoteInterfResp(map);
                } catch (Exception e) {
                    logger.error("【CFETS接口接收-现券对话报价】处理业务数据异常：", e);
                }
            }
        });
        service.execute(thread);
//		thread.start();
        return resultBaseDto;
    }
    
    /**
     * 更新请求报价的接口反馈信息
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("receiveBondRequestRequest")
    public ResultBaseDto receiveBondRequestRequest(@RequestBody Map<String, Object> map){
        //return ibuComponentReceiveService.updateBondRequestQuoteInterfResp(map);
        ResultBaseDto resultBaseDto = new ResultBaseDto(true);
        logger.info("【CFETS接口接收-现券请求报价】处理业务数据线程开启" + map.toString());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ibuComponentReceiveService.updateBondRequestQuoteInterfResp(map);
                } catch (Exception e) {
                    logger.error("【CFETS接口接收-现券请求报价】处理业务数据异常：", e);
                }
            }
        });
        service.execute(thread);
//		thread.start();
        return resultBaseDto;
    }
    
    /**
     * 更新质押式回购报价的接口反馈信息
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("receiveRepoDialogueRequest")
    public ResultBaseDto receiveRepoDialogueRequest(@RequestBody Map<String, Object> map){
       // return ibuComponentReceiveService.updateRepoDialogueQuoteInterfResp(map);
        //        return ibuComponentReceiveService.updateRepoDialogueQuoteInterfResp(map);
        ResultBaseDto resultBaseDto = new ResultBaseDto(true);
        logger.info("【CFETS接口接收-回购对话报价】处理业务数据线程开启" + map.toString());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ibuComponentReceiveService.updateRepoDialogueQuoteInterfResp(map);
                } catch (Exception e) {
                    logger.error("【CFETS接口接收-回购对话报价】处理业务数据异常：", e);
                }
            }
        });
        service.execute(thread);
//		thread.start();
        return resultBaseDto;
    }

    /**
     * 是否银行间交易日历节假日
     * @param param 参数
     * @return 是否银行间交易日历节假日
     * @see
     * @since 1.0
     */
    @PostMapping("isDealCalendarHoliday")
    public Map<String, Object> isDealCalendarHoliday(@RequestBody Map<String, Object> param){
        String dealCalId = CacheUtil.getSysParam("ibuVar_calendarId");
        if (StringUtil.isBlank(dealCalId) || StringUtil.equals("ibuVar_calendarId", dealCalId)) {
            dealCalId = "IB";
        }
        Date today = (Date) param.get("today");

        boolean isDealCalendarHoliday = dateCalCommonService.isDealCalendarHoliday(today, dealCalId);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("isDealCalendarHoliday", isDealCalendarHoliday ? DictCodeList.是否_是.getValue() : DictCodeList.是否_否.getValue());
        return resultMap;
    }

    /**
     * 组合代码查询（无权限限制）
     * <功能详细描述>
     * @param param 参数
     * @param page 页数
     * @param length 条数
     * @return 数据集合
     * @see
     * @since 1.0
     */
    @RequestMapping("selectPortfolioId/{page}/{length}")
    public Page<SelectOptionDto> selectPortfolioId( @RequestBody Map<String, Object> param, 
            @PathVariable("page") int page, @PathVariable("length") int length) {
        return cfetsQuotedCommonService.selectPortfolioId(page, length, param);
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
    @PostMapping("queryFinprodIdPage/{page}/{length}")
    public Page<SelectOptionDto> queryFinprodIdPage(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                    @PathVariable("length") int length){
        return cfetsQuotedCommonService.queryFinprodIdPage(page,length,param);
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
    @PostMapping("queryFinprodIdList")
    public List<SelectOptionDto> queryFinprodIdList(@RequestBody Map<String, Object> param){
        return cfetsQuotedCommonService.queryFinprodIdList(param);
    }
    
    /**
     * 查询市场代码列表
     * 
     * @return 市场代码列表
     * @see
     * @since 1.0
     */
    @GetMapping("queryFinprodMarketIdList")
    public List<String> queryFinprodMarketIdList(){
        return cfetsQuotedCommonService.queryFinprodMarketIdList();
    }
    /**
     * 
     * 根据当前登录用户查询对应的交易员id<br/>
     * <功能详细描述>
     * @param param 查询参数
     * @return 交易员id
     * @see
     * @since 1.0
     */
    @PostMapping("queryLoginTraderId")
    public List<SysUserAdd> queryLoginTraderId(@RequestBody Map<String, Object> param){
        return cfetsQuotedCommonService.queryLoginTraderId(param);
    }
    /**
     * 根据组合代码和本方机构查询账户来源
     * 
     * @param param 入参
     * @return 账户来源
     */
    @PostMapping("querySecAcctSourceByPtlIdPartyId")
    public String querySecAcctSourceByPtlIdPartyId(@RequestBody Map<String, Object> param) {
    	return cfetsQuotedCommonService.querySecAcctSourceByPtlIdPartyId(param);
    }
    
    /**
     * 委托已报额度查询
     * @param param
     * @return
     */
    @PostMapping("queryUsedShareAmt")
    public BigDecimal queryUsedShareAmt(@RequestBody Map<String, Object> param) {
    	return cfetsQuotedCommonService.queryUsedShareAmt(param);
    }

    /**
     * 查找组合对应的证券账户
     * @param param
     * @return
     */
    @PostMapping("qryPortfolioSecAcctId")
    public String qryPortfolioSecAcctId(@RequestBody Map<String, Object> param) {
        return cfetsQuotedCommonService.qryPortfolioSecAcctId(param);
    }


    /**
     * 根据查询查询符合条件的指令编号
     *
     * @param param 入参
     * @param page 页数
     * @param length 步长
     * @return 报价编号
     * @see
     * @since 1.0
     */
    @PostMapping("queryCfetsEntrustIdList/{page}/{length}")
    public Page<SuggestDto> queryCfetsEntrustIdList(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                    @PathVariable("length") int length){
        return cfetsQuotedCommonService.queryCfetsEntrustIdList(page,length,param);
    }


    /**
     * 下行成交数据
     * @author zhangnian
     * @date 2021/1/20 0020
     * @param  * @param map
     * @return void
    */
    @PostMapping("receiveCfetsTradeDialogueRequest")
    public ResultBaseDto receive(@RequestBody Map<String,Object> map) throws Exception {
       /* CfetsQuotedTradeCommonDto cfetsQuotedTradeCommonDto = (CfetsQuotedTradeCommonDto) JsonUtils.map2entity(map, CfetsQuotedTradeCommonDto.class);
        return cfetsQuotedTradeCommonService.insertCstpTrade(cfetsQuotedTradeCommonDto);*/
        //        try {
//            Thread.sleep(maxWaitTime);
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        ResultBaseDto resultBaseDto = new ResultBaseDto(true);
        logger.info("银行间下行成交处理业务数据线程开启" + map.toString());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    CfetsQuotedTradeCommonDto cfetsQuotedTradeCommonDto = (CfetsQuotedTradeCommonDto) JsonUtils.map2entity(map, CfetsQuotedTradeCommonDto.class);
                    cfetsQuotedTradeCommonService.insertCstpTrade(cfetsQuotedTradeCommonDto);
                }catch (Exception e){
                    logger.error("CSTP下行匹配异常：", e);
                }
            }});
        service.execute(thread);
//        thread.start();
        return resultBaseDto;
    }


    /**
     * 一级申购下行成交数据
     * @author jiangpeng
     * @date 2022/5/6 16:46
     * @param map
     * @return com.joyintech.background.component.uplink.interbank.business.dto.ResultBaseDto
     */
    @PostMapping("receiveFirstBondCfetsTradeDialogueRequest")
    public ResultBaseDto receiveFirstBond(@RequestBody Map<String,Object> map) throws Exception {
        ResultBaseDto resultBaseDto = new ResultBaseDto(true);
        logger.info("银行间一级申购下行成交处理业务数据线程开启" + map.toString());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CfetsQuotedTradeFirstBondDto cfetsQuotedTradeFirstBondDto = (CfetsQuotedTradeFirstBondDto) JsonUtils.map2entity(map, CfetsQuotedTradeFirstBondDto.class);
                    cfetsQuotedTradeCommonService.insertBidCstpTrade(cfetsQuotedTradeFirstBondDto);
                } catch (Exception e) {
                    logger.error("CSTP下行匹配异常：", e);
                }
            }
        });
        service.execute(thread);
        return resultBaseDto;
    }
    
    /**
     * 交易撤销时，恢复报价可用头寸
     * @param map 参数:成交编号
     * @throws Exception
     */
    @Transactional(rollbackFor = { Exception.class })
    @DTXTransaction
    @PostMapping("recoveryQuotePositionData")
    public Boolean recoveryQuotePositionData(@RequestBody Map<String, Object> map) throws Exception {
        cfetsQuotedCommonService.recoveryQuotePositionData(map);
        return true;
    }
    
    /**
     * 删除报价可用头寸
     * @param map 参数:成交编号
     * @throws Exception
     */
    @Transactional(rollbackFor = { Exception.class })
    @DTXTransaction
    @PostMapping("deleteQuotePositionData")
    public Boolean deleteQuotePositionData(@RequestBody Map<String, Object> map) throws Exception {
        cfetsQuotedCommonService.deleteQuotePositionData(map);
        return true;
    }
    
 // add by etliu 20210129 start
    /**
     * 检查画面选择的成交编号是否存在对应的报价
     * 目前债券二级交易和回购交易画面会用到
     * 
     * @return 是否存在对应的报价
     * @see
     * @since 1.0
     */
    @GetMapping("checkExecIdHasQuote")
    public String checkExecIdHasQuote(String execId) {
        return cfetsQuotedCommonService.checkExecIdHasQuote(execId);
    }
 // add by etliu 20210129 end

    @RequestMapping("selectEntrustId/{page}/{length}")
    public Page<SelectOptionDto> selectEntrustId( @RequestBody Map<String, Object> param,
                                                    @PathVariable("page") int page, @PathVariable("length") int length) {
        return cfetsQuotedCommonService.selectEntrustId(page, length, param);
    }

    @RequestMapping("isOpeQuotedPrice")
    public boolean isOpeQuotedPrice(@RequestBody Map<String,Object> param){
        return cfetsQuotedCommonService.isOpeQuotedPrice(param);
    }

    @GetMapping("listIbuQuoteEntCrepPledge")
    public List<CfetsRepoDealQuotedPrice003Dto> listIbuQuoteEntCrepPledge(String quoteId){
        return cfetsQuotedCommonService.listIbuQuoteEntCrepPledge(quoteId);
    }
}
