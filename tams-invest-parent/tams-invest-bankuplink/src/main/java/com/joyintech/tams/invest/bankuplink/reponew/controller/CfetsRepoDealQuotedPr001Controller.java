package com.joyintech.tams.invest.bankuplink.reponew.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.*;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.invest.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.tams.invest.bankuplink.cstp.dto.CstpSelectDto;
import com.joyintech.tams.invest.bankuplink.reponew.dto.CfetsRepoDealQuotedPr001Dto;
import com.joyintech.tams.invest.bankuplink.reponew.dto.EtrFinProductCommonDto;
import com.joyintech.tams.invest.bankuplink.reponew.dto.EtrFinProductDto;
import com.joyintech.tams.invest.bankuplink.reponew.dto.RepoQuoteInfoDto;
import com.joyintech.tams.invest.bankuplink.reponew.service.CfetsRepoDealQuotedExport001Service;
import com.joyintech.tams.invest.bankuplink.reponew.service.CfetsRepoDealQuotedExport002Service;
import com.joyintech.tams.invest.bankuplink.reponew.service.CfetsRepoDealQuotedExport003Service;
import com.joyintech.tams.invest.bankuplink.reponew.service.CfetsRepoDealQuotedPr001Service;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 质押式回购报价
 * @author chenjiaxing
 * @version 1.0 2021年8月6日
 * @see CfetsRepoDealQuotedPr001Controller
 * @since 1.0
 */
@Controller
@PageId("CfetsRepoDealQuotedPr001")
@RequestMapping("/invest/bankuplink/reponew/CfetsRepoDealQuotedPr001")
public class CfetsRepoDealQuotedPr001Controller extends BaseController{

    /**
     * 外汇交易中心CMDS现券行情接口地址
     */
    @Value("${cmdsWebSocketUrl}")
    private String cmdsWebSocketUrl;

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;

    @Autowired
    private CfetsRepoDealQuotedPr001Service cfetsRepoDealQuotedPr001Service;

    @Autowired
    private CfetsRepoDealQuotedExport001Service cfetsRepoDealQuotedExport001;

    @Autowired
    private CfetsRepoDealQuotedExport002Service cfetsRepoDealQuotedExport002;
    @Autowired
    private CfetsRepoDealQuotedExport003Service cfetsRepoDealQuotedExport003;

    @Value("${application.rocketmq.topic.repo:trcms_pfms_tbank_repo}")
    private String repoTopic;
    
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, BussinessDictCode.回购拆借方向, BussinessDictCode.交易方向, BussinessDictCode.清算速度);
        model.addAttribute("DYN_INFO", "ZLZX");
        model.addAttribute("repo_pledge_model",CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL));
        model.addAttribute("entrustStatusUnAccept", BussinessDictCodeList.委托状态_待接收.getValue());
        model.addAttribute("entrustStatusAccept", BussinessDictCodeList.委托状态_已接收.getValue());
        model.addAttribute("unSendStatus", BussinessDictCodeList.银行间上行发送状态_待发送.getValue());
        model.addAttribute("quoteTypeDialog", BussinessDictCodeList.交易类型_对话报价.getValue());
        model.addAttribute("ps_pay_name", BussinessDictCodeList.交易方向_买入.getCodeName());
        model.addAttribute("ps_sell_name", BussinessDictCodeList.交易方向_卖出.getCodeName());
        model.addAttribute("tradeStatusPending", BussinessDictCodeList.银行间上行报价状态_待回复.getValue());
        model.addAttribute("quoteRoleTypeEnquiry", BussinessDictCodeList.银行间上行本方角色_询价方.getValue());
        model.addAttribute("quoteRoleTypeReply", BussinessDictCodeList.银行间上行本方角色_回复方.getValue());
        // 可否无委托对话报价
        model.addAttribute("ibuFlag_startDialogueQuoteWithNoEntrust", CacheUtil.getSysParam("ibuFlag_startDialogueQuoteWithNoEntrust"));
        model.addAttribute("sendStatus_fail", BussinessDictCodeList.银行间上行发送状态_失败.getCodeName());
        model.addAttribute("sendStatus_unsend", BussinessDictCodeList.银行间上行发送状态_待发送.getCodeName());
        model.addAttribute("sendStatus_send", BussinessDictCodeList.银行间上行发送状态_已发送.getCodeName());
        model.addAttribute("quoteStatus_added", BussinessDictCodeList.银行间上行报价状态_新增中.getCodeName());
        model.addAttribute("loginTraderId", cfetsQuotedCommonService.queryLoginTraderId(RequestUtil.getPageParam()));
        this.setSelectOptionToModel(model, BussinessDictCode.匹配状态,BussinessDictCode.交易类型_CSTP);
        // 定义topic
        model.addAttribute("repoTopic", repoTopic);
        return super.init(model);
    }

    /***
     * 初始化行情
     * @return
     */
    @RequestMapping(value = "/initHq")
    public String initHq(Model model){
        model.addAttribute("cmdsWebSocketUrl", cmdsWebSocketUrl);
        return "invest/bankuplink/reponew/CfetsRepoDealQuotedPrHq";
    }

    /**
     * 根据查询符合条件的指令执行列表
     *
     * @return 指令执行数据列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryZlzxList")
    public List<CfetsRepoDealQuotedPr001Dto> queryZlzxList() {

        return cfetsRepoDealQuotedPr001Service.queryZlzxList(
                RequestUtil.getPageParam());
    }

    /**
     * 根据查询符合条件的质押券管理列表
     *
     * @return 质押券管理数据列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryZyqglList")
    public List<CfetsRepoDealQuotedPr001Dto> queryZyqglList() {
        return cfetsRepoDealQuotedPr001Service.queryZyqglList(
                RequestUtil.getPageParam());
    }


    /**
     * 根据查询符合条件的质押券管理列表
     *
     * @return 质押券管理数据列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/gencjcxTableParam")
    public List<CstpSelectDto> gencjcxTableParam() {
        return cfetsRepoDealQuotedPr001Service.gencjcxTableParam(
                RequestUtil.getPageParam());
    }

    /**
     * 委托查询
     *
     * @return Page<BondQuoteInfoDto>
     */
    @ResponseBody
    @RequestMapping("/queryWtdhbjList")
    public List<RepoQuoteInfoDto> queryWtdhbjList() {
        return cfetsRepoDealQuotedPr001Service.queryWtdhbjList(RequestUtil.getPageParam());
    }
    /**
     * 导出信息excel
     *
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export() throws Exception {
        Map<String, Object> param = RequestUtil.getPageParam();
        String menuId = param.get("exportMenu").toString();
        /*指令执行导出*/
        if(StringUtil.equals(menuId,"zlzx")){
            String datas = this.getParam("datas");
            List<CfetsRepoDealQuotedPr001Dto> param1;
            if (datas == null) {
                param1 = cfetsRepoDealQuotedPr001Service.queryZlzxList(param);
            }else{
                param1 = JsonUtils.json2List(datas, CfetsRepoDealQuotedPr001Dto.class);
            }
            File outFile =  cfetsRepoDealQuotedExport003.makeFile(
                    "/com/joyintech/tams/invest/bankuplink/reponew/conf/export_conf1.xml",
                    param1);
            return ExportUtil.export(
                    "质押式回购报价指令执行" + SystemGlobalConstant.OPEN_PARENTHESIS
                            + DateUtil.format(DateUtil.platDateFromCache())
                            + SystemGlobalConstant.CLOSE_PARENTHESIS
                            + SystemGlobalConstant.POINT
                            + FileNameUtil.getFileExtName(outFile),
                    outFile, true);
        }
        /*委托查询对话导出*/
        else if(StringUtil.equals(menuId,"wtcxdh")){
            String datas = this.getParam("datas");
            List<RepoQuoteInfoDto> param1;
            if (datas == null) {
                param1 = cfetsRepoDealQuotedPr001Service.queryWtdhbjList(param);
            }else{
                param1 = JsonUtils.json2List(datas, RepoQuoteInfoDto.class);
            }
            List<String> quoteIdList = new ArrayList<>();
            RepoQuoteInfoDto emptyDto =new RepoQuoteInfoDto();
            Iterator<RepoQuoteInfoDto> iterator = param1.iterator();
            while (iterator.hasNext()) {
                RepoQuoteInfoDto repoQuoteInfoDto = iterator.next();
                if(quoteIdList.contains(repoQuoteInfoDto.getQuoteId())){
                    String bondCode = repoQuoteInfoDto.getBondCode();
                    String bondAbbrName = repoQuoteInfoDto.getBondAbbrName() ;
                    BigDecimal pledgeRate = repoQuoteInfoDto.getPledgeRate() ;
                    BigDecimal prin = repoQuoteInfoDto.getPrin() ;
                    BeanUtil.copyProperties(repoQuoteInfoDto,emptyDto);
                    repoQuoteInfoDto.setBondCode(bondCode);
                    repoQuoteInfoDto.setBondAbbrName(bondAbbrName);
                    repoQuoteInfoDto.setPledgeRate(pledgeRate);
                    repoQuoteInfoDto.setPrin(prin);
                }
                if(!StringUtil.isEmpty(repoQuoteInfoDto.getQuoteId())){
                    quoteIdList.add(repoQuoteInfoDto.getQuoteId());
                }
            }
            File outFile =  cfetsRepoDealQuotedExport002.makeFile(
                    "/com/joyintech/tams/invest/bankuplink/reponew/conf/export_conf2.xml",
                    param1);
            return ExportUtil.export(
                    "质押式回购报价委托查询" + SystemGlobalConstant.OPEN_PARENTHESIS
                            + DateUtil.format(DateUtil.platDateFromCache())
                            + SystemGlobalConstant.CLOSE_PARENTHESIS
                            + SystemGlobalConstant.POINT
                            + FileNameUtil.getFileExtName(outFile),
                    outFile, true);
        }
        /*成交查询导出*/
        else if(StringUtil.equals(menuId,"cjcx")){
            String datas = this.getParam("datas");
            List<CstpSelectDto> param1;
            if (datas == null) {
                param1 = cfetsRepoDealQuotedPr001Service.gencjcxTableParam(param);
            }else{
                param1 = JsonUtils.json2List(datas, CstpSelectDto.class);
            }
            File outFile =  cfetsRepoDealQuotedExport001.makeFile(
                    "/com/joyintech/tams/invest/bankuplink/reponew/conf/export_conf3.xml",
                    param1);
            return ExportUtil.export(
                    "质押式回购报价成交查询" + SystemGlobalConstant.OPEN_PARENTHESIS
                            + DateUtil.format(DateUtil.platDateFromCache())
                            + SystemGlobalConstant.CLOSE_PARENTHESIS
                            + SystemGlobalConstant.POINT
                            + FileNameUtil.getFileExtName(outFile),
                    outFile, true);
        }
        return null;
    }

    @RequestMapping(value="findQuotations")
    @ResponseBody
    public Map<String, Object> findQuotations() {
        return cfetsRepoDealQuotedPr001Service.findQuotations(RequestUtil.getPageParam());
    }

    /**
     * 查询持仓信息
     *
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPositionInfo")
    public EtrFinProductDto findPositionInfo(@RequestBody Map<String, Object> param){
        return cfetsRepoDealQuotedPr001Service.findPositionInfo(param);
    }

    /**
     * 查询现金数据
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("findCashAmountInfo")
    public EtrFinProductCommonDto findCashAmountInfo(@RequestBody Map<String, Object> param){
        return cfetsRepoDealQuotedPr001Service.findCashAmountInfo(param);
    }

    /**
     * 查询回购信息
     *
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findhginfo")
    public EtrFinProductDto findhginfo(@RequestBody Map<String, Object> param){
        return cfetsRepoDealQuotedPr001Service.findhginfo(param);
    }

    @RequestMapping("exportPdf")
    public ResponseEntity<byte[]> exportPdf(){
        return cfetsRepoDealQuotedPr001Service.exportPdf(this.getPageId(),RequestUtil.getPageParam());
    }

}
