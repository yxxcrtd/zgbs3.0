/*
 * 文件名：TrdInvestPersonInfo002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年9月19日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.business.trade.controller;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;

import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.trade.BussEtrInvestPersonInfoService;
import com.joyintech.tams.component.business.trade.TrdInvestPersonInfo002Service;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 投资交易人员信息controller
 * 〈功能详细描述〉
 * @author 杨智
 * @version 1.0 2018年9月19日
 * @see TrdInvestPersonInfo002Controller
 * @since 1.0
 */
@Controller
@RequestMapping("/component/trade/TrdInvestPersonInfo002")
public class TrdInvestPersonInfo002Controller extends BaseController {

    /**
     * trdInvestPersonInfo002Service
     */
    @Autowired
    private TrdInvestPersonInfo002Service trdInvestPersonInfo002Service;
    
    @Autowired(required=false)
    private BussEtrInvestPersonInfoService bussEtrInvestPersonInfoService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        String pageId = this.getParam("pageId");
        model.addAttribute("WINDTYPEONE_B14", DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue());
        model.addAttribute("pageId", pageId);
        model.addAttribute("psId", RequestUtil.getParam("psId"));
        model.addAttribute("pfId", RequestUtil.getParam("pfId"));
        model.addAttribute("callPageId", RequestUtil.getParam("callPageId"));
        model.addAttribute("IS_AUTO_GENERATE_NEGATE_DEAL", CacheUtil.getSysParam(BussinessConstant.IS_AUTO_GENERATE_NEGATE_DEAL));
        model.addAttribute("entityName", StringUtil.defaultString(this.getParam("entityName"), TrdInvestPersonInfo002Service.DEFAULT_ENTITY_NAME));
        String entityStr = this.getParam("entity");
        TrdInvestPersonInfo002Dto entity = JsonUtils.json2Object(entityStr, TrdInvestPersonInfo002Dto.class);
        if(entity == null){
            entity = new TrdInvestPersonInfo002Dto();
            entity.setTrader(LoginUserUtil.getUserName());

            entity.setTraderDept(LoginUserUtil.getDeptCode());
        }
        // ZGZGST-2008: fuyongsheng 20210918 add start
        // 1、投资经理指定外部交易对手，交易员不能修改为内部交易对手，可以修改为其他外部交易对手
        // 2、投资经理没有指定交易对手，交易员可以选择外部交易对手，不能选择内部交易对手
        // 3、投资经理指定内部交易对手，交易员不能修改交易对手
        // 4、无指令交易录入，交易员可以选择外部交易对手和内部交易对手
        else{
            if(StringUtil.isNotEmpty(entity.getCounterId())){
                Map<String, Object> params = new HashMap<>();
                params.put("counterId", entity.getCounterId());
                Page<SelectOptionDto> result = trdInvestPersonInfo002Service.queryCounterId(params);
                if(!result.getRoot().isEmpty()){
                    String object = (String)result.getRoot().get(0).getObject();
                    entity.setIsOutTrade(DictCodeList.是否_否.getValue().equals(object) ?
                            Boolean.FALSE.toString() : Boolean.TRUE.toString());
                    model.addAttribute("innerTrade", DictCodeList.是否_否.getValue().equals(object) ?
                            Boolean.TRUE.toString() : Boolean.FALSE.toString());
                }
            }else{
                model.addAttribute("innerTrade", Boolean.FALSE.toString());
            }
            // ZGZGST-2008: fuyongsheng 20210918 add end
            //add  wsz 20220601 交易对手不显示问题修改 start 3.0.6场内 ZGCPCN-1007
            if(Objects.equals("trdInvestPersonInfo002Dto",this.getParam("entityName"))){
                model.addAttribute("innerTrade", Boolean.FALSE.toString());
            }
            //add  wsz 交易对手不显示问题修改end
        }
        HashMap<String, Object> param = new HashMap<>();
//        model.addAttribute("COUNTER_ID_LIST",
//            JsonUtils.object2Json(trdInvestPersonInfo002Service.selectCounterId(param).toArray()));
        model.addAttribute("entity", entity);
//        model.addAttribute("innerTrade", getParam("innerTrade"));
        // add by wangguoxian 增加交易对手类型传参 20190713 start
        model.addAttribute("counterType", getParam("counterType"));
        // add by wangguoxian 增加交易对手类型传参 20190713 end
        return "component/trade/TrdInvestPersonInfo002";
    }
    
    /**
     * 
     * 查询对手方交易员
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("/selectCounterId")
    @ResponseBody
    public Page<SelectOptionDto> selectCounterId(){
        Map<String, Object> params = RequestUtil.getPageParam();
        if (StringUtil.isNotEmpty(params.get("callPageId"))
				&& (StringUtil.equals("TrdBondInvestTrade002", params.get("callPageId").toString()) 
						&& !StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), params.get("ps"))
						&& StringUtil.equals(DictCodeListExt1.交易场所_银行间.getValue(), params.get("tradeMarket"))
						&&!StringUtil.isNotEmpty(params.get("chlId"))
						&& (StringUtil.isNotEmpty(params.get("fTradeId")))||StringUtil.isNotEmpty(params.get("fTradeIdShow")))) {
			//return bussEtrInvestPersonInfoService.getCfetsCounterId(params);
			return bussEtrInvestPersonInfoService.getCounterIdCfets(params);
		}
        // 判断转仓开关
        if (CacheUtil.getSysParam("TURN_OVER_SWITCH")!=null 
            && "01".equals(CacheUtil.getSysParam("TURN_OVER_SWITCH")) 
        		&& params.get("portfolioId") != null 
        		&& DictCodeList.交易方向_卖出.getValue().equals(params.get("ps"))) {
			params.put("pfId", params.get("portfolioId").toString());
		}
        
        if (StringUtil.isEmpty((String) params.get("innerTrade")) || "null".equals(params.get("innerTrade"))) {
            params.put("innerTrade", null);
        }
        else if (Boolean.FALSE.toString().equals(params.get("innerTrade"))) {
            params.put("innerTrade", false);
        }
        else if (Boolean.TRUE.toString().equals(params.get("innerTrade"))) {
            params.put("innerTrade", true);
        }
        if (StringUtil.isEmpty((String) params.get("counterType")) || "null".equals(params.get("counterType"))) {
            params.put("counterType",null);
        }
        
        return trdInvestPersonInfo002Service.selectCounterId(params);
    }
    
    /**
     * 查询本方交易员
     * @return
     */
    @RequestMapping("/selectTrader")
    @ResponseBody
    public Page<SelectOptionDto> selectTrader(){
    	Map<String, Object> params = RequestUtil.getPageParam();
    	if (StringUtil.isNotEmpty(params.get("callPageId"))
				&& (StringUtil.equals("TrdBondInvestTrade002", params.get("callPageId").toString()) 
						&& !StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), params.get("ps"))
						&& StringUtil.equals(DictCodeListExt1.交易场所_银行间.getValue(), params.get("tradeMarket"))
						&& !StringUtil.isNotEmpty(params.get("chlId")))) {
    		if(StringUtil.isNotEmpty(params.get("trdInvestPersonInfo.counterId"))) {
    			params.put("etrInvestPersonInfo.counterId", params.get("trdInvestPersonInfo.counterId"));
    		}
			return bussEtrInvestPersonInfoService.getCfetsTrader(params);
		}
    	return trdInvestPersonInfo002Service.selectTrader(RequestUtil.getPageParam());
    }
}
