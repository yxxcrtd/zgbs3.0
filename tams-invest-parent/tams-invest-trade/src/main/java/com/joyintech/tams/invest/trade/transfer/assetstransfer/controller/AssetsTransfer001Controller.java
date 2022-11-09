package com.joyintech.tams.invest.trade.transfer.assetstransfer.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.trade.transfer.assetstransfer.dto.AssetsTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.assetstransfer.service.AssetsTransfer001Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 资产转仓
 *
 * @author liuwei
 * @version 1.0 2019年12月10日
 * @see AssetsTransfer001Controller
 * @since 1.0
 */
@Controller
@PageId("AssetsTransfer001")
@RequestMapping("/invest/trade/transfer/assetstransfer/AssetsTransfer001")
public class AssetsTransfer001Controller extends BaseController {

    /**
     * AssetsTransfer001Service
     */
    @Autowired
    private AssetsTransfer001Service assetsTransfer001Service;

    /**
     * 重写init方法，加载页面
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        //下拉框
        this.setSelectOptionToModel(model, DictCode.平安资产类型, DictCode.交易流程状态);
        model.addAttribute("sysDate", DateUtil.platDateFromCache());
        model.addAttribute("PINAN_ASSET_TYPE_F01",DictCodeList.平安资产类型_债券.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F02",DictCodeList.平安资产类型_基金.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F03",DictCodeList.平安资产类型_计划类资产_利率型.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F04",DictCodeList.平安资产类型_计划类资产_净值型.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F05",DictCodeList.平安资产类型_存款.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F06",DictCodeList.平安资产类型_项目类资产.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F07",DictCodeList.平安资产类型_质押式回购.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F08",DictCodeList.平安资产类型_拆借.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F09",DictCodeList.平安资产类型_计划类资产_货币型.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F10",DictCodeList.平安资产类型_买断式回购.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F11", DictCodeListExt2.平安资产类型_股票.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F12",DictCodeListExt2.平安资产类型_债券借贷.getValue());
        model.addAttribute("BUS_TYPE", DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue());
        model.addAttribute("ps_T05", DictCodeList.交易方向_申购确认.getValue());
        model.addAttribute("ps_T19", DictCodeList.交易方向_赎回确认.getValue());
        model.addAttribute("TRADE_STATUS_02", DictCodeListExt1.交易流程状态_有效.getValue());
        Map<String,Object> paramMap = new HashMap<>();
//        return assetsTransfer001Service.getFinprodId(paramMap);
//        model.addAttribute("finprodList", JsonUtils.object2Json(assetsTransfer001Service.getFinprodId(paramMap)));
        return super.init(model);
    }

    /**
     * 获取组合代码联想文本框
     * @return 组合代码列表
     * @throws Exception Exception
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getPortfolioId")
    public List<SelectOptionDto> getPortfolioId(){
        Map<String,Object> paramMap = RequestUtil.getPageParam();
        paramMap.put("userId", LoginUserUtil.getUserName());
        return assetsTransfer001Service.getPortfolioId(paramMap);
    }

    /**
     * 获取组合结构数据
     * @return 画面元素
     * @throws Exception Exception
     * @since 1.0
     */
    @SuppressWarnings("rawtypes")
    @ResponseBody
    @RequestMapping("/getPortfolioIdTree")
    public List<TreeNode> getPortfolioIdTree(@RequestParam(name="portfolioId", required=false) String portfolioId,
                                             @RequestParam(name="mdate", required=false) Date mdate)
             {
        return assetsTransfer001Service.getPortfolioIdTree(portfolioId,mdate);
    }

    /**
     * 获取待转仓列表
     *
     * @return 组合代码列表
     * @throws Exception Exception
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/findUnTransPage")
    public Page<AssetsTransfer001Dto> findUnTransPage(){
        Map<String,Object> paramMap = RequestUtil.getPageParam();
        paramMap.put("userId", LoginUserUtil.getUserName());
        return assetsTransfer001Service.findUnTransPage(paramMap);
    }

    /**
     * 获取已转仓列表
     *
     * @return 组合代码列表
     * @throws Exception Exception
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/findTransPage")
    public Page<AssetsTransfer001Dto> findTransPage(){
        Map<String,Object> paramMap = RequestUtil.getPageParam();
        paramMap.put("userId", LoginUserUtil.getUserName());
        return assetsTransfer001Service.findTransPage(paramMap);
    }

    /**
     * 获取资产代码联想下拉框
     * @return 资产代码列表
     *
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getFinprodId")
    public List<SelectOptionDto> getFinprodId(){
        Map<String,Object> paramMap = RequestUtil.getPageParam();
        return assetsTransfer001Service.getFinprodId(paramMap);
    }

    /**
     * 添加按钮按下前check
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("revokeCheck")
    public AjaxResult revokeCheck(@RequestBody AssetsTransfer001Dto dto, BindingResult br) {
        AjaxResult ar = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if (StringUtil.isNotEmpty(dto) && StringUtil.isNotEmpty(dto.getTradeDate()) &&
                dto.getTradeDate().compareTo(DateUtil.platDateFromCache()) != 0) {
            errorList.add(new ValidMessage("tradeDate","交易日期等于平台日的交易才能撤销！"));
        }
        if (StringUtil.isEmptyList(errorList)) {
            ar.setStatuCode(HttpStatus.OK.value());
        } else {
            ar.setErrorList(errorList);
            ar.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return ar;
    }

 /**
     * 获取业务编号联想文本框
     * @return 业务编号列表
     * @throws Exception Exception
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getTradeId")
    public List<SuggestDto> getTradeId(){
    	String param = super.getRequest().getParameter("param");
        List<SuggestDto> list = assetsTransfer001Service.getTradeId(param);
        return list;
    }
    
    @ResponseBody
    @RequestMapping("/getFinprodMarketId")
    public Page<SelectOptionDto> getFinprodMarketId(){
        Map<String,Object> paramMap = RequestUtil.getPageParam();
        if(StringUtil.isNotEmpty(paramMap.get("assetType"))){
            String assetTypeList =(String)paramMap.get("assetType");
            paramMap.put("assetTypeList",assetTypeList.split(","));
        }
        return assetsTransfer001Service.getFinprodMarketId(paramMap);
    }
}
