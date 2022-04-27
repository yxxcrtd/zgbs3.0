/*
 * 文件名：CfetsQuotedCommonController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.bankuplink.common.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.CacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;

import io.netty.util.internal.StringUtil;



/**
 * 银行间上行共通Controller
 *
 * @author zhangliang
 * @version 1.0 2020年5月11日
 * @see CfetsQuotedCommon001Controller
 * @since 1.0
 */
@Controller
@PageId("CfetsQuotedCommon001")
@RequestMapping("/invest/bankuplink/common/CfetsQuotedCommon001")
public class CfetsQuotedCommon001Controller extends BaseController {

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;


    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.分配方式, BussinessDictCode.清算速度,DictCode.业务模式);
        model.addAttribute("entity",this.getParam("entity"));
        model.addAttribute("quoteType", this.getParam("quoteType"));
        model.addAttribute("quotaPageModel", this.getParam("quotaPageModel"));
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数
        // 定义topic
        return super.init(model);
    }

    /**
     * 根据查询查询符合条件的委托列表
     *
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryEntrustInfoList")
    public Page<SelectOptionDto> getentrustIdAdd(){
        return cfetsQuotedCommonService.queryEntrustInfoList(RequestUtil.getPageParam());
    }

    /**
     * 委托放弃处理
     *
     * @param entrustId 委托代码
     * @return 执行是否成功
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/entrustGiveUpHandle")
    public AjaxResult entrustGiveUpHandle(String entrustId) {
        return cfetsQuotedCommonService.entrustGiveUpHandle(entrustId);
    }

    /**
     * 批量放弃委托
     * @return
     */
    @ResponseBody
    @RequestMapping("/entrustGiveUpHandleList")
    public AjaxResult entrustGiveUpHandleList(){
        Map<String,Object> param=RequestUtil.getPageParam();
        param.put("entrustIds",com.joyintech.framework.util.StringUtil.splitToList(GlobalConstant.COMMA, RequestUtil.getParam("entrustIds")));
        AjaxResult result=new AjaxResult();
        result.setErrorList(cfetsQuotedCommonService.entrustGiveUpHandleList(param));
        return result;
    }
    /**
     * 查询本方交易员列表
     *
     * @return 本方交易员列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryMySideTraderList")
    public Page<SelectOptionDto> queryMySideTraderList(){
        return cfetsQuotedCommonService.queryMySideTraderPage(RequestUtil.getPageParam());
    }

    /**
     * 查询本方机构列表
     *
     * @return 本方机构列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryMySideList")
    public Page<SelectOptionDto> queryMySideList(){
        return cfetsQuotedCommonService.queryMySidePage(RequestUtil.getPageParam());
    }

    /**
     * 查询本方机构列表
     *
     * @return 本方机构列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryCounterSideList")
    public Page<SelectOptionDto> queryCounterSideList(){
        return cfetsQuotedCommonService.queryCounterSidePage(RequestUtil.getPageParam());
    }

    /**
     * 获取下一个工作日
     *
     * @param params 画面参数
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
    @RequestMapping("getNextWorkDay")
    @ResponseBody
    public String getNextWorkDay(@RequestParam Map<String, Object> params) {
        if (params.get("tradeDate") != null) {
            if (params.get("settleSpreed") != null) {
                // 清算速度 T+1/T+2 默认T+1 nextDate 下一工作日
                Date nextDate = cfetsQuotedCommonService.getNextWorkDay(DateUtil.parse((String) params.get("tradeDate")));
                if (StringUtils.equals(BussinessDictCodeList.清算速度_T加2.getValue(),params.get("settleSpreed").toString())) {
                    nextDate = cfetsQuotedCommonService.getNextWorkDay(nextDate);
                }
                return DateUtil.format(nextDate);
            } else {
                return DateUtil.format(cfetsQuotedCommonService.getNextWorkDay(DateUtil.parse((String) params.get("tradeDate"))));
            }
        }
        return StringUtil.EMPTY_STRING;
    }

    /**
     * 获取工作日
     *
     * @param params 画面参数
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
    @RequestMapping("getWorkDay")
    @ResponseBody
    public String getWorkDay(@RequestParam Map<String, Object> params) {
        return cfetsQuotedCommonService.getWorkDay(params);
    }

    /**
     *
     * 根据报价编号查询错误信息
     * @param quoteId String 报价代码
     * @return String 错误信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/selectErrlog")
    public AjaxResult selectErrlog(String quoteId) {
        AjaxResult ar = new AjaxResult();
        ar.setData(cfetsQuotedCommonService.selectErrlog(quoteId));
        return ar;
    }

    /**
     * 查询本方交易员列表
     *
     * @return 本方交易员列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/selectPortfolioId")
    public Page<SelectOptionDto> selectPortfolioId(){
        return cfetsQuotedCommonService.selectPortfolioId(RequestUtil.getPageParam());
    }

    /**
     * 获取新建界面的资产代码联想下拉框框
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryFinprodIdPage")
    public Page<SelectOptionDto> queryFinprodIdPage(){
        return cfetsQuotedCommonService.queryFinprodIdPage(RequestUtil.getPageParam());
    }

    /**
     * 获取新建界面的资产代码联想下拉框框
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryFinprodIdList")
    public List<SelectOptionDto> queryFinprodIdList(){
        return cfetsQuotedCommonService.queryFinprodIdList(RequestUtil.getPageParam());
    }

    /**
     * 根据当前登录用户查询对应的交易员id<br/>
     *
     * @return 对应的交易员id
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryLoginTraderId")
    public String queryLoginTraderId(){
        return cfetsQuotedCommonService.queryLoginTraderId(RequestUtil.getPageParam());
    }


    /**
     * 查询指令编号列表
     *
     * @return 报价编号列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryCfetsEntrustIdList")
    public Page<SuggestDto> queryCfetsEntrustIdList(String type) {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("type", type);
        return cfetsQuotedCommonService.queryCfetsEntrustIdList(param);
    }
}
