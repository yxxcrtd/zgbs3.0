package com.joyintech.tams.invest.trade.exchangetransmatch.manualmatch.controller;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.trade.cstptrdmatchinfo.service.CstpTrdMatchInfoService;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.trade.exchangetransmatch.manualmatch.dto.ExchangeTransMatch002Dto;
import com.joyintech.tams.invest.trade.exchangetransmatch.manualmatch.dto.ExchangeTransMatch003Dto;
import com.joyintech.tams.invest.trade.exchangetransmatch.manualmatch.dto.ExchangeTransMatch004Dto;
import com.joyintech.tams.invest.trade.exchangetransmatch.manualmatch.service.ExchangeTransMatch001Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@PageId("ExchangeTransMatch001")
@RequestMapping("/invest/trade/exchangetransmatch/ExchangeTransMatch001")
public class ExchangeTransMatch001Controller  extends BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 成交匹配service
     */
    @Autowired
    private ExchangeTransMatch001Service exchangeTransMatch001Service;

    @Autowired
    private CstpTrdMatchInfoService cstpTrdMatchInfoService;
    /**
     * 初始化方法
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.下行交易匹配状态,DictCode.交易场所,DictCode.金融产品类型_投管用,DictCode.交易类型,DictCode.交易平台,DictCode.下行交易匹配类型,DictCode.交易方向);

        model.addAttribute("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());

        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("PS_T03", DictCodeList.交易方向_行权_回售.getValue());
        model.addAttribute("PS_T04", DictCodeList.交易方向_申购.getValue());
        model.addAttribute("PS_T05", DictCodeList.交易方向_申购确认.getValue());
        model.addAttribute("PS_T49", DictCodeListExt3.交易方向_债转股.getValue());
        model.addAttribute("PS_T97", DictCodeListExt3.交易方向_配债.getValue());
        model.addAttribute("PS_03", DictCodeList.回购拆借方向_正回购.getValue());
        model.addAttribute("PS_04", DictCodeList.回购拆借方向_逆回购.getValue());
        model.addAttribute("PS_T48", DictCodeListExt2.交易方向_新股中签.getValue());
        model.addAttribute("PS_T47", DictCodeListExt2.交易方向_增发.getValue());
        model.addAttribute("PS_T46", DictCodeListExt2.交易方向_配股.getValue());

        model.addAttribute("TARGET_BACK_TYPE_H01",DictCodeList.回购类型_质押式协议回购.getValue());

        model.addAttribute("TRD_MATCH_TYPE_0", DictCodeListExt3.下行交易匹配类型_自动匹配.getValue());
        model.addAttribute("TRD_MATCH_TYPE_1", DictCodeListExt3.下行交易匹配类型_手动匹配.getValue());

        return super.init(model);
    }

    /**
     * 画面导出功能实现
     *
     * @return ResponseEntity 确认的实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("exportExcelQueDing")
    public ResponseEntity<byte[]> exportExcelQueDing()
    {
        return exchangeTransMatch001Service.export(this.getPageId(),
                RequestUtil.getParameters(getRequest()));
    }


    /**
     * 获取资产代码下拉框
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getFinprodId")
    public Page<SelectOptionDto> getFinprodId(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return exchangeTransMatch001Service.getFinprodId(map);
    }

    /**
     * 获取交易编号
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getBusiCode")
    public Page<SelectOptionDto> getBusiCode(){
        Map<String,Object> map = RequestUtil.getPageParam();
        Page<SelectOptionDto> busiCodPage = exchangeTransMatch001Service.getBusiCode(map);
        return busiCodPage;
    }

    /**
     * 获取申请编号
     * @return
     */
    @ResponseBody
    @RequestMapping("getApplyNo")
    public Page<SelectOptionDto> getApplyNo(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return exchangeTransMatch001Service.getApplyNo(map);
    }

    /**
     * 获取证券账户
     * @return
     */
    @ResponseBody
    @RequestMapping("getSecAcctNo")
    public Page<SelectOptionDto> getSecAcctNo() {
        Map<String,Object> map = RequestUtil.getPageParam();
        Page<SelectOptionDto> secAcctNoPage = exchangeTransMatch001Service.getSecAcctNo(map);
        return secAcctNoPage;
    }

    /**
     * 获取成交编号
     * @return
     */
    @ResponseBody
    @RequestMapping("getDealId")
    public Page<SelectOptionDto> getDealId() {
        Map<String,Object> map = RequestUtil.getPageParam();
        Page<SelectOptionDto> secAcctNoPage = exchangeTransMatch001Service.getDealId(map);
        return secAcctNoPage;
    }


    /**
     * 分页查询
     * @return 分页查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<ExchangeTransMatch002Dto> findPage(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return exchangeTransMatch001Service.findPage(map);
    }

    /**
     * 分页查询
     * @return 分页查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findEntrustPage")
    public Page<ExchangeTransMatch003Dto> findEntrustPage() {
        Map<String,Object> map = RequestUtil.getPageParam();
        return exchangeTransMatch001Service.findEntrustPage(map);
    }

    /**
     * 匹配查询
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping("matchSearch")
    public Page<ExchangeTransMatch003Dto> matchSearch(@RequestBody Map<String,Object> map) {

        if(StringUtil.isNotEmpty(map.get("ps"))) {
            String ps = (String)map.get("ps");
            if(StringUtil.equals(ps,DictCodeList.交易方向_到期.getValue())) {
                map.put("ps",DictCodeListExt3.交易方向_到期购回.getValue());
            }
            else if (StringUtil.equals(ps,DictCodeListExt3.交易方向_投资端_提前终止.getValue())) {
                map.put("ps",DictCodeListExt3.交易方向_提前购回.getValue());
            }
        }

        if(StringUtil.isNotEmpty(map.get("finprodType1"))) {
            String finprodType = (String)map.get("finprodType1");
            String ps = (String)map.get("ps");
            if(StringUtil.equals(finprodType,DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                    && StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())) {
                map.put("ps",DictCodeList.回购拆借方向_正回购.getValue());
            }
            else if (StringUtil.equals(finprodType,DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                    && StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())) {
                map.put("ps",DictCodeList.回购拆借方向_逆回购.getValue());
            }
        }

        return exchangeTransMatch001Service.findEntrustPage(map);
    }

    /**
     * 分页查询
     * @return 分页查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findTransMatchInfoPage")
    public Page<ExchangeTransMatch004Dto> findTransMatchInfoPage(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return exchangeTransMatch001Service.findTransMatchInfoPage(map);
    }

    /**
     * 成交匹配检测
     * @param dto
     * @param result
     * @return
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated ExchangeTransMatch002Dto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 成交匹配
     * @param dto
     * @return
     */
    @ResponseBody
    @RequestMapping("transactionMacth")
    public AjaxResult transactionMacth(@RequestBody ExchangeTransMatch002Dto dto){
//        try {
            exchangeTransMatch001Service.transactionMacth(dto);
//        } catch (Exception e) {
//            logger.error("交易匹配确认失败"+e.getMessage());
//            return new AjaxResult("成交匹配失败", MessageType.错误消息);
//        }
        return new AjaxResult("成交匹配成功",MessageType.提示消息);
    }

    /**
     * 生成交易录单数据
     * @param dto
     * @return
     */
    @ResponseBody
    @RequestMapping("genTransactionRecord")
    public String genTransactionRecord(@RequestBody ExchangeTransMatch002Dto dto) {
        return exchangeTransMatch001Service.genTransactionRecord(dto);
    }

    /**
     * 成交匹配
     * @param dto
     * @return
     */
    @ResponseBody
    @RequestMapping("revokeTransactionMacth")
    public AjaxResult revokeTransactionMacth(@RequestBody ExchangeTransMatch004Dto dto){
        try {
            exchangeTransMatch001Service.revokeTransactionMacth(dto);
        } catch (Exception e) {
            logger.error("取消成交匹配失败"+e.getMessage());
            return new AjaxResult("取消成交匹配失败", MessageType.错误消息);
        }
        return new AjaxResult("取消成交匹配成功",MessageType.提示消息);
    }

}
