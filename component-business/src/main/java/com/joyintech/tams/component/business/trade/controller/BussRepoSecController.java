/*
 * 文件名：TrdRepoTrade003Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.trade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.trade.BussRepoSecService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 添加质押券
 * 〈功能详细描述〉
 *
 * @author 陈志力
 * @version 1.0 2019年01月11日
 * @see BussRepoSecController
 * @since 1.0
 */
@Controller
@RequestMapping("/component/trade/BussRepoSec")
public class BussRepoSecController extends BaseController {

    @Autowired(required = false)
    private BussRepoSecService bussRepoSecService;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.是否);
        String portfolioId = this.getParam("portfolioId");
        String isBondLendTrade = this.getParam("isBondLendTrade");
        String ps = this.getParam("ps");
        String chlId = this.getParam("chlId");
        String securitiesCust = this.getParam("securitiesCust");
        String bussType = this.getParam("bussType");
        String bussBondType = this.getParam("bussBondType");
        String entrustId = this.getParam("entrustId");
        String tradeType = this.getParam("tradeType");
        String pageFrom = this.getParam("pageFrom");
        String isDzh = this.getParam("isDzh");
        String quoteId = this.getParam("quoteId");
        //Modify by gaopeng 2019/10/12 end
        if (!StringUtil.equals(this.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW) || Boolean.valueOf(getParam("isTodoList"))
                || !StringUtil.equals(getParam("statusValid"), DictCodeListExt1.画面数据状态_待处理.getValue())) {
            String entityStr = this.getParam("entity");
            String bizCode = this.getParam("bizCode");
            BussRepoSecDto entity = StringUtil.isNotEmpty(entityStr) ? JsonUtils.json2Object(entityStr, BussRepoSecDto.class) : null;
            if (entity == null
                    && (StringUtil.isNotEmpty(entrustId) || StringUtil.isNotEmpty(bizCode))) {
                Map<String, Object> initSearchParam = new HashMap<>();
                initSearchParam.put("portfolioId", portfolioId);
                initSearchParam.put("ps", ps);
                initSearchParam.put("bizCode", bizCode);
                initSearchParam.put("SEC_ACCT_ID",securitiesCust != null? securitiesCust: chlId);
                // 新建时
                if (StringUtil.isNotEmpty(entrustId)) {
                    initSearchParam.put("entrustId", entrustId);
                    initSearchParam.put("bizCode", null);
                }
                if(StringUtil.isNotEmpty(entrustId)&&StringUtils.isNotBlank(bizCode)
                        &&StringUtils.isNotBlank(bussType)){
                    initSearchParam.put("entrustId", null);
                    initSearchParam.put("bizCode", bizCode);
                }
                if(StringUtils.isNotBlank(bussBondType)){
                    initSearchParam.put("bizCode", bizCode);
                    initSearchParam.put("bussBondType", bussBondType);
                }
                //质押式回购报价页面  单独处理
                if(StringUtils.isNotEmpty(pageFrom)&&(StringUtils.equals(pageFrom,"CfetsRepoDealQuotedPrice002")||StringUtils.equals(pageFrom,"CfetsRepoDealQuotedPrice003"))){
                }else{
                    entity = bussRepoSecService.selectBussRepoSec(initSearchParam);
                }
            }
            if (entity != null) {
                // 已质押券信息一览
                //万元转换 add  by yeming 20210817 start
                if( entity.getTotalPrinAmt()  != null){
                    entity.setTotalPrinAmt(ArithmeticUtil.divide(entity.getTotalPrinAmt(),new BigDecimal(10000),4));
                }
                //add by chenjiaxing 质押券回购初始化报错,报价界面回复初始化时质押券多除了10000  ，ZGZGST-2401 CfetsRepoDealQuotedPr006
                if(StringUtil.equals(entity.getPageFrom(),"CfetsRepoDealQuotedPr006")){
                    entity.setTotalPrinAmt(ArithmeticUtil.multiply(entity.getTotalPrinAmt(),new BigDecimal(10000)));
                }
                if(!(entity.getPledgeBondList() == null) && (!StringUtil.equals(entity.getPageFrom(),"CfetsRepoDealQuotedPrice002")
                        && !StringUtil.equals(pageFrom,"CfetsRepoDealQuotedPrice002")
                        && !StringUtil.equals(entity.getPageFrom(),"CfetsRepoDealQuotedPrice003")
                        && !StringUtil.equals(pageFrom,"CfetsRepoDealQuotedPrice003")
                        && !StringUtil.equals(entity.getPageFrom(),"CfetsRepoDealQuotedPr006")
                        && !StringUtil.equals(pageFrom,"CfetsRepoDealQuotedPr006"))){
                    for (PledgeBondInfoDto dto : entity.getPledgeBondList()) {
                        if (dto.getAvaPledgePrin() != null) {
                            dto.setAvaPledgePrin(ArithmeticUtil.divide(dto.getAvaPledgePrin(), new BigDecimal(10000),4));
                        }
                        if (dto.getPledgePrin() != null) {
                            dto.setPledgePrin(ArithmeticUtil.divide(dto.getPledgePrin(), new BigDecimal(10000),4));
                        }
                    }
                }
                //万元转换 add  by yeming 20210817 end
                model.addAttribute("entity", entity);
            }
        }
        // 佣金
        model.addAttribute("FEE_TYPE_05", BussinessDictCodeList.费用类型_佣金.getValue());
        // 交易手续费
        model.addAttribute("FEE_TYPE_06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        // 结算手续费
        model.addAttribute("FEE_TYPE_08", BussinessDictCodeList.费用类型_结算手续费.getValue());
        //add by gaopeng 2019/10/12 start
        model.addAttribute("entrustId", entrustId);
        //add by gaopeng 2019/10/12 end
        model.addAttribute("pageFrom", getParam("pageFrom"));
        model.addAttribute("repo_pledge_model",CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL));
        model.addAttribute("tradeMarketId", getParam("tradeMarketId"));
        model.addAttribute("statusValid", getParam("statusValid"));
        model.addAttribute("bizCode", getParam("bizCode"));
        model.addAttribute("isTodoList", getParam("isTodoList"));
        model.addAttribute("pageModel", this.getPageModel());
        String entityName = StringUtil.defaultString(this.getParam("entityNameBussRepoSec"), BussRepoSecService.DEFAULT_ENTITY_NAME);
        model.addAttribute("entityNameBussRepoSec", entityName);
        model.addAttribute(GlobalConstant.WORKFLOW_ID, this.getParam(GlobalConstant.WORKFLOW_ID));
        model.addAttribute("operationType", StringUtil.defaultString(this.getParam("operationType")));
        model.addAttribute("BACK_LEND_DIRECTION_03", DictCodeList.回购拆借方向_正回购.getValue());
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        model.addAttribute("isBondLendTrade",isBondLendTrade);
        model.addAttribute("tradeType",tradeType);
        this.setSelectOptionToModel(model, DictCode.WIND一级分类_含债券一级分类等
                , DictCode.登记托管机构);
        model.addAttribute("ibuFlag_repoOverAvaCanContinue",
                CacheUtil.getSysParam("ibuFlag_repoOverAvaCanContinue"));
        model.addAttribute("isDzh",isDzh);
        model.addAttribute("quoteId",quoteId);
        //配置单位 add by yeming 20210721 start
        model.addAttribute("entrustAmtUnit", CacheUtil.getSysParam("entrustAmtUnit"));
        //配置单位 add by yeming 20210721 end
        model.addAttribute("tradePlace",getParam("tradePlace"));
        model.addAttribute("parentPageId",this.getParam("parentPageId"));
        return "component/trade/BussRepoSec";
    }

    /**
     * 实体类进行检查
     *
     * @param map 传入的实体类
     * @return 风险结果实体类
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("check")
    @ResponseBody
    public AjaxResult check(@RequestBody Map<String, Object> map) {
        AjaxResult result = new AjaxResult();
        List<ValidMessage> messages = new ArrayList<>();
        Map<String, String> data = (HashMap<String, String>) map.get("data");

        String[] validParams = {"portfolioId", "ps"};
        if(!"dzh".equals((String)data.get("dzh"))){
            for (String param : validParams) {
                if (map.get(param) != null) {
                    List<String> fields = (List<String>) map.get(param);
                    notEmptyCheck(data.get(param), fields.get(0).toString(), fields.get(1).toString(), messages);
                }
            }
        }
        
        //添加质押券信息校验回购类型 必输----update by jjpei 20190727----去除此验证-----
      /*  if (!(StringUtil.isNotEmpty(data.get("repoType")) || StringUtil.isNotEmpty(data.get("assetType1")))){
            messages.add(new ValidMessage("BUS_REPO_SEC_003",getMessage("BUS_REPO_SEC_004")));
        }*/
        //添加质押券信息校验 账号和通道二者必输其一，  逆回购 不需要校验
        //String repoType = StringUtil.isNotEmpty(data.get("repoType")) ? (String)data.get("repoType"): (String)data.get("assetType1");
        //----update by jjpei 20190727----去除此验证-----
        if (!(StringUtil.equals((String)data.get("ps"), DictCodeList.回购拆借方向_逆回购.getValue()))
                && !(StringUtil.isNotEmpty(data.get("secAcctId")) || StringUtil.isNotEmpty(data.get("chlId")) ||
                        StringUtil.isNotEmpty(data.get("securitiesCust")))){
            if("03".equals(data.get("tradeMarketId")) || "04".equals(data.get("tradeMarketId"))) {
                messages.add(new ValidMessage("BUS_REPO_SEC_001", getMessage("BUS_REPO_SEC_002")));
            }
        }

       if (messages.size() > 0) {
            result.setErrorList(messages);
        }
        return result;
    }

    /**
     * check检查
     *
     * @param obj       检查得到值
     * @param fieldName 返回消息名
     * @param messages  检查结果信息
     * @see
     * @since 1.0
     */
    private void notEmptyCheck(Object obj, String fieldName, String massage, List<ValidMessage> messages) {
        if (!StringUtil.isNotEmpty(obj)) {
            fieldName = getMessage(fieldName);
            messages.add(new ValidMessage(fieldName, getMessage("FW_E_0006", getMessage(massage))));
        }
    }
    
    /**
     * 自动导入质押券
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("checkAutoPledge")
    @ResponseBody
    public AjaxResult checkAutoPledge(@RequestBody Map<String, Object> map) {
        AjaxResult result = new AjaxResult();
        List<ValidMessage> messages = new ArrayList<>();
        Map<String, String> data = (HashMap<String, String>) map.get("data");
        //组合代码,委托金额，基准日(委托开始日)，必填校验
        String[] validParams = {"portfolioId", "entrustAmt", "vdate"};
        for (String param : validParams) {
            if (map.get(param) != null) {
                List<String> fields = (List<String>) map.get(param);
                notEmptyCheck(data == null ? null : data.get(param), fields.get(0).toString(), fields.get(1).toString(), messages);
            }
        }
        // Sonar Fix modify by huwei 220608
        if (null == data || !(StringUtil.equals(data.get("ps"), DictCodeList.回购拆借方向_逆回购.getValue()))
            && !(StringUtil.isNotEmpty(data.get("secAcctId")) || StringUtil.isNotEmpty(data.get("chlId")) ||
                    StringUtil.isNotEmpty(data.get("securitiesCust")))){
            messages.add(new ValidMessage("BUS_REPO_SEC_001",getMessage("BUS_REPO_SEC_002")));
        }
       
        // 必填项校验完成，则自动导入
        if (!(messages.size() > 0)) {
            // 自动导入质押券 查询
            List<PledgeBondInfoDto> dataList = bussRepoSecService.selectAutoPledge(data);
            if(dataList.size() > 0){
                result.setData(dataList);
            }else{
                messages.add(new ValidMessage("BUS_REPO_SEC_001","无可用质押券，请手动添加"));
            }
        }
        if(messages.size() > 0){
            result.setErrorList(messages);
        }
        return result;
    }
    
    /**
     * 自动导入质押券
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("checkAutoPledgeOfEtrIbRepoinfo")
    @ResponseBody
    public AjaxResult checkAutoPledgeOfEtrIbRepoinfo(@RequestBody Map<String, Object> map) {
        AjaxResult result = new AjaxResult();
        List<ValidMessage> messages = new ArrayList<>();
        Map<String, String> data = (HashMap<String, String>) map.get("data");
        //组合代码,委托金额，基准日(委托开始日)，必填校验
        String[] validParams = {"portfolioId", "entrustAmt", "vdate"};
        for (String param : validParams) {
            if (map.get(param) != null) {
                List<String> fields = (List<String>) map.get(param);
                notEmptyCheck(data == null ? null : data.get(param), fields.get(0).toString(), fields.get(1).toString(), messages);
            }
        }
//        if (!(StringUtil.equals(data ==null?null :(String)data.get("ps"), DictCodeList.回购拆借方向_逆回购.getValue()))
//            && !(StringUtil.isNotEmpty(data.get("secAcctId")) || StringUtil.isNotEmpty(data.get("chlId")) ||
//                    StringUtil.isNotEmpty(data.get("securitiesCust")))){
//            messages.add(new ValidMessage("BUS_REPO_SEC_001",getMessage("BUS_REPO_SEC_002")));
//        }
//       
        // 必填项校验完成，则自动导入
        if (!(messages.size() > 0)) {
            // 自动导入质押券 查询
            List<PledgeBondInfoDto> dataList = bussRepoSecService.selectAutoPledge(data);
            if(dataList.size() > 0){
                result.setData(dataList);
            }else{
                messages.add(new ValidMessage("BUS_REPO_SEC_001","无可用质押券，请手动添加"));
            }
        }
        if(messages.size() > 0){
            result.setErrorList(messages);
        }
        return result;
    }

    /**
     * 根据指令编号查询质押券数据
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("selectEtrPledgeData")
    @ResponseBody
    public AjaxResult selectEtrPledgeData(@RequestBody Map<String, Object> map) {
        AjaxResult ajaxResult = new AjaxResult();

        BussRepoSecDto bussRepoSecDto = bussRepoSecService.selectBussRepoSec(map);
        if (bussRepoSecDto != null && !StringUtil.isEmptyList(bussRepoSecDto.getPledgeBondList())) {
            ajaxResult.setData(bussRepoSecDto.getPledgeBondList());
        }

        return ajaxResult;
    }
    
}
