/*
 * 文件名：TrdBondInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.bond.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.intf.DataDict;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.base.service.DealDictCodeDataService;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.listener.FileUploadListener;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.pagemenusplit.PageMenuSplitUtil;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeCSTPDto;
import com.joyintech.tams.invest.trade.bond.validator.TrdBondInvestTradeDtoValidator;
import com.joyintech.tams.invest.trade.common.aop.TradeRdsCommonAop;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.trade.bond.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 债券交易新建
 *
 * @author Zane.Y
 * @version 1.0 2018年9月7日
 * @see TrdBondInvestTrade003Controller
 * @since 1.0
 */
@Controller
@PageId("TrdBondInvestTrade003")
@RequestMapping("/invest/trade/bond/TrdBondInvestTrade003")
public class TrdBondInvestTrade003Controller extends BaseController {
	@Autowired
	private TradeCommonService tradeCommonService;
    /**
     * trdBondInvestTrade002Service
     */
    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;
    @Autowired
    private TrdBondInvestTradeDtoValidator trdBondInvestTradeDtoValidator;
    
    @Autowired
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;

    @Autowired(required = false)
    private DealDictCodeDataService dealDictCodeDataService;

    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;

    @Override
    public String init(Model model) {
        setSelectOptions(model);
        TrdBondInvestTradeDto dto = new TrdBondInvestTradeDto();
        // 默认指令类型为单组合指令
        dto.setZhilingtype("01");
        String prinamt = this.getParam("prinamt");
        String tradeId = this.getParam("tradeId");
        String workflowStatus = this.getParam("workflowStatus");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        String payType = this.getParam("payType");      // 缴款方式
        String yield = this.getParam("winPrice");       // 中标 "标位"
        String winPrinAmt = this.getParam("winPrinAmt");// 中标 "中标券面总额"
        model.addAttribute("winPrinAmtList",(String)this.getParam("winPrinAmtList"));
        String bondFirstSign = this.getParam("bondFirstSign");
        String isCstp = this.getParam("isCstp");
        String isCstpSure = this.getParam("isCstpSure");
        String entrustAmtUnit = CacheUtil.getSysParam("entrustAmtUnit");
        model.addAttribute("entrustAmtUnit", entrustAmtUnit );
        if (StringUtil.equals(workflowStatus, DictCodeListExt1.交易流程状态_流程中.getValue())
            || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(workflowId))) {
            dto = trdBondInvestTrade002Service.getBussinessEntityByWorkflowId(
                workflowId);
            if (dto.getTotalFaceValue() != null) {
                dto.setTradeAmt(dto.getTotalFaceValue());
            }
            model.addAttribute("riskTrialRetDto", JsonUtils.object2Json(dto.getRiskTrialRetDto()));
            model.addAttribute("workflowId",workflowId);
        }
        else if (StringUtil.isNotEmpty(tradeId)) {
            dto = trdBondInvestTrade002Service.getTrdBondInvestTradeDto(tradeId);
        }
        else {
            String entrustId = this.getParam("entrustId");
            if (StringUtil.isNotEmpty(entrustId)) {
                dto.setEntrustId(entrustId);
            }
        }
        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());

        // 从"债券中标结果查询"进来，准备进行缴款 add by gejiaao 2019/07/26 Start
        if (StringUtil.isNotEmpty(bondFirstSign)){
            // 当"缴款方式"不为空时，表示为从，债券中标结果中进来的
            dto.setPayType(payType);
            BigDecimal winPrinAmtBig = BigDecimal.ZERO;
            if(StringUtil.isNotEmpty(winPrinAmt)){
                winPrinAmtBig = new BigDecimal(winPrinAmt);
                winPrinAmtBig = winPrinAmtBig.multiply(new BigDecimal(10000));
            }
            dto.setTradeAmt(winPrinAmtBig);
            dto.setYield(BigDecimal.valueOf(Double.valueOf(yield == null ? "0" : yield)));
            model.addAttribute("bondFirstSign",bondFirstSign);
            model.addAttribute("winPrinAmt",winPrinAmtBig);
            model.addAttribute("yield",yield);
            dto.setFinprodId(this.getParam("finprodId"));
            dto.setUnitCprice(new BigDecimal(this.getParam("winCprice")));
        }
        // 从"债券中标结果查询"进来，准备进行缴款 add by gejiaao 2019/07/26 End
        if (StringUtil.isNotBlank(tradeId)) {
            model.addAttribute("tradeId", tradeId);
        }
        else {
            //zhujun BSBS-441@2019-05-20 兼容指令接收交易录单附件不显示问题
            model.addAttribute("tradeId", dto.getTotleTradeId()==null?this.getParam("entrustId"):dto.getTotleTradeId());
        }
        if (StringUtil.isNotBlank(workflowId)) {
            model.addAttribute("workflowId", workflowId);
        }
        else {
            model.addAttribute("workflowId", dto.getWorkflowId());
        }

        model.addAttribute("entity", dto);
        model.addAttribute("entity_trdInvestPersonInfo",
            dto.getTrdInvestPersonInfo() == null ? null : JsonUtils.object2Json(
                dto.getTrdInvestPersonInfo()));
        // 划款信息
        if (dto != null && !StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
            model.addAttribute("entity_bussRepFinProductInfoDto",
                JsonUtils.object2Json(dto.getBussTrdTransferOrder().get(0)));
        }
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数

        // 净价radio默认选中
        model.addAttribute("calculateField", "unitCprice");

        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        //--------add by 裴家俊 2019/03/28------start--------
        model.addAttribute("FEE_TYPE_05", BussinessDictCodeList.费用类型_佣金.getValue());
        model.addAttribute("FEE_TYPE_07", BussinessDictCodeList.费用类型_结算费.getValue());
        model.addAttribute("FEE_TYPE_01", BussinessDictCodeList.费用类型_经手费.getValue());
        model.addAttribute("FEE_TYPE_02", BussinessDictCodeList.费用类型_印花税.getValue());
        model.addAttribute("FEE_TYPE_06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        model.addAttribute("FEE_TYPE_JSSXF", BussinessDictCodeList.费用类型_结算手续费.getValue());
        //--------add by 裴家俊 2019/03/28------end--------

        //sjj
        List<SelectOptionDto> options = new ArrayList<>();
        SelectOptionDto option1 = new SelectOptionDto();
        option1.setId("02");
        option1.setText("多组合指令");
        option1.setCol("ZHILING_TYPE");
        option1.setParentId("");
        
        options.add(option1);
        
        model.addAttribute("ZHILING_TYPE", JsonUtils.objectArray2Json(options.toArray()));
//        this.setSelectOptionToModel(model, DictCode.投资目的);
        model.addAttribute("formTodoList", !WorkFlowUtil.isNotFromToDoList());
        Date sysDate = DateUtil.platDateFromCache();
        String formatDate = DateUtil.format(sysDate);
        model.addAttribute("formatDate",formatDate);
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        if(org.apache.commons.lang.StringUtils.isNotBlank(this.getParam("fTradeId"))){
            model.addAttribute("fTradeId",this.getParam("fTradeId"));
            model.addAttribute("prinamt",prinamt);
        }
        model.addAttribute("isCstp",isCstp);
        model.addAttribute("isCstpSure",isCstpSure);
        //add by lizhi 是否限制划款指令开关 2020年2月26日  start
        model.addAttribute("GEN_PAY_ORDERS_AUTO",CacheUtil.getSysParam(BussinessConstant.GEN_PAY_ORDERS_AUTO));
        //add by lizhi 是否限制划款指令开关 2020年2月26日  end
        //add by liuwenjuan 费用支持平均分配 20200323 start
        model.addAttribute("IS_AVERAGE_DISTRIBUTION_OF_EXPENSES", CacheUtil.getSysParam(BussinessConstant.IS_AVERAGE_DISTRIBUTION_OF_EXPENSES));
        //add by liuwenjuan 费用支持平均分配 20200323 end
        //add by liuwenjuan 回款前扣除 20200326 start
        model.addAttribute("Collect_Type_01", DictCodeListExt2.收取方式_回款前扣除.getValue());
        //add by liuwenjuan 回款前扣除 20200326 end
        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);

        if (dto.getTradeAmt() != null){
            dto.setTradeAmt(dto.getTradeAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
            if(StringUtil.isNotEmpty(dto.getOwnTradeAmt())){
            	dto.setOwnTradeAmt(dto.getOwnTradeAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
            }
            if(StringUtil.isNotEmpty(dto.getIntegrateTradeAmt())){
            	dto.setIntegrateTradeAmt(dto.getIntegrateTradeAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
            }
        }
        
        // add by etliu 20210501 银行间场内场外拆分 start
        model.addAttribute("parentPageId", this.getParam("parentPageId"));
        PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易场所, this.getParam("parentPageId"));
        PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易平台, this.getParam("parentPageId"));
        // add by etliu 20210501 银行间场内场外拆分 end

        if(workflowId!=null){
            return "invest/trade/bond/TrdBondInvestTrade002";
        }else{
            return super.init(model);
        }
    }

    private void setSelectOptions(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.交易场所,
            DictCode.有效状态, DictCode.交易平台, DictCode.币种,
            DictCode.投资目的, DictCode.交易市场, DictCode.清算速度, DictCode.是否,
            DictCode.行内资产二级分类,DictCode.行内资产三级分类,DictCode.行内资产四级分类,// 新增资产二、三、四级分类 王贵奇 2019/7/31
            DictCode.WIND一级分类_含债券一级分类等, BussinessDictCode.缴款方式,DictCode.分配方式, DictCode.业务模式);
        model.addAttribute("TURN_INV_AIM",
                JsonUtils.objectArray2Json(this.getSelectDataFromDataDic(DictCode.投资目的, null).getOptions().toArray()));
        model.addAttribute("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        model.addAttribute("TRADE_PLACE_02", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("TRADE_PLACE_03", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        model.addAttribute("TRADE_PLACE_05", DictCodeListExt1.交易场所_机构间私募系统.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_01", DictCodeList.交易平台_外汇交易中心.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_99", DictCodeList.交易平台_其它.getValue());
        //add by zhangnian 20190910  start
        model.addAttribute("SHARES_TRADE_PLATFORM_02", DictCodeList.交易平台_竞价撮合平台.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_03", DictCodeList.交易平台_固定收益平台.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_04", DictCodeList.交易平台_大宗交易平台.getValue());
        //add by zhangnian 20190910  end
        model.addAttribute("SHARES_TRADE_MARKET_01", DictCodeListExt1.交易市场_一级市场.getValue());
        model.addAttribute("SHARES_TRADE_MARKET_02", DictCodeListExt1.交易市场_二级市场.getValue());
        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
        // 一体化对应etliu start
//        model.addAttribute("PS_T03", DictCodeList.交易方向_行权.getValue());
        model.addAttribute("PS_T03", DictCodeList.交易方向_行权_回售.getValue());
        model.addAttribute("PS_T39", DictCodeList.交易方向_行权_赎回.getValue());
        // 一体化对应etliu end
        model.addAttribute("ACCOUNT_SPEED_T0", DictCodeList.清算速度_T_0.getValue());
        model.addAttribute("ACCOUNT_SPEED_T1", DictCodeList.清算速度_T_1.getValue());
        model.addAttribute("ACCOUNT_SPEED_T2", DictCodeList.清算速度_T_2.getValue());
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        model.addAttribute("FINPROD_TYPE_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());

        //--------add by 言伟 2019/05/16------start--------
        model.addAttribute("ETR_PAY_TYPE_01", BussinessDictCodeList.缴款方式_一级分销.getValue());
        model.addAttribute("ETR_PAY_TYPE_02", BussinessDictCodeList.缴款方式_一级缴款.getValue());
        model.addAttribute("ETR_PAY_TYPE_03", BussinessDictCodeList.缴款方式_二级交易.getValue());
        model.addAttribute("WINDTYPEONE_B17", DictCodeList.WIND一级分类_含债券一级分类等_优先股.getValue());
        model.addAttribute("ACCT_TYPE_A01", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        model.addAttribute("ACCT_TYPE_A07", DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        //--------add by 言伟 2019/05/16------End--------
    }

    /**
     * 查询委托信息
     *
     * @return 委托信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getEntrustInfo")
    public BaseEtrInvestInfoDto getEntrustInfo(@RequestBody TrdBondInvestTradeDto dto) {
        //中标结果查询进来  add by gejiaao 2019/7/26 start
        if (StringUtil.isNotBlank(dto.getBondFirstSign())){
            return trdBondInvestTrade002Service.getEntrustInfoTmp(dto.getEntrustId());
        }
        //中标结果查询进来  add by gejiaao 2019/7/26 end
        return trdBondInvestTrade002Service.getEntrustInfo(dto.getEntrustId());
    }

    /**
     * 查询证券托管户：联想框文本框
     *
     * @param params 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    @RequestMapping("getSecManageAcctId")
    @ResponseBody
    public List<SelectOptionDto> getSecManageAcctId(@RequestParam Map<String, Object> params) {
        List<SelectOptionDto> secManageAcctId = trdBondInvestTrade002Service.getSecManageAcctId(params);
        return secManageAcctId;
    }
    
    
    /**
     * 通道：联想框文本框
     *
     * @param params 画面参数
     * @return 通道：联想框文本框
     * @see
     * @since 1.0
     */
    @RequestMapping("getChannel")
    @ResponseBody
    public List<SelectOptionDto> getChannel(@RequestParam Map<String, Object> params) {
        return trdBondInvestTrade002Service.getChannel(params);
    }

    /**
     * 资产代码：下拉框
     *
     * @param params 画面参数
     * @return 资产代码：下拉框
     * @see
     * @since 1.0
     */
    @RequestMapping("getFinprodId")
    @ResponseBody
    public Page<SelectOptionDto> getFinprodId(@RequestParam Map<String, Object> params) {
        return trdBondInvestTrade002Service.getFinprodId(params);
    }

    /**
     * 行权收益率是否显示
     *
     * @param paramMap 画面参数
     * @return 行权收益率是否显示
     * @see
     * @since 1.0
     */
    @RequestMapping("isExerYieldDivShow")
    @ResponseBody
    public String isExerYieldDivShow(@RequestParam Map<String, Object> paramMap) {
        return StringUtil.trimHtmlTag(trdBondInvestTrade002Service.isExerYieldDivShow(paramMap));
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
        return trdBondInvestTrade002Service.getNextWorkDay(params);
    }

    /**
     * 1.6计算偏离度
     *
     * @param params 画面参数
     * @return 偏离度
     * @see
     * @since 1.0
     */
    @RequestMapping("calcDeviationDegree")
    @ResponseBody
    public String calcDeviationDegree(@RequestParam Map<String, Object> params) {
        return StringUtil.trimHtmlTag(trdBondInvestTrade002Service.calcDeviationDegree(params));
    }

    /**
     * 1.7计算按钮点击事件
     *
     * @param inputDto 画面参数
     * @return 计算结果
     * @see
     * @since 1.0
     */
    @RequestMapping("calculateData")
    @ResponseBody
    public AjaxResult calculateData(@RequestBody TrdBondInvestTradeDto inputDto) {
        return trdBondInvestTrade002Service.calculateData(inputDto);
    }

    /**
     * 保存提交前的数据检查
     *
     * @param dto 画面输入参数
     * @return 检查结果
     * @see
     * @since 1.0
     */
    @RequestMapping("check")
    @ResponseBody
    public AjaxResult check(@Validated({ValidateGroupEdit.class}) @RequestBody TrdBondInvestTradeDto dto, BindingResult result) {
        //先报错误信息在报确认信息 add by gejiaao 2019-07-31 start
//        AjaxResult errorResult = new AjaxResult();
//        List<ValidMessage> valid = trdBondInvestTradeDtoValidator.valid(dto);
//        if (!valid.isEmpty()){
//            errorResult.setErrorList(valid);
//            return errorResult;
//        }
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }

        //先报错误信息在报确认信息 add by gejiaao 2019-07-31 start

        //huwenbin 指令接收交易录单验证数据已存在流程中
        //add by zhujun @2019-05-16 投资委托交易退回时，此check阻塞修改保存操作
        if(StringUtil.isNotEmpty(dto.getEntrustId()) && WorkFlowUtil.isNotFromToDoList() && StringUtil.isEmpty(dto.getBondFirstSign())){
            String str = tradeCommonService.check(dto.getEntrustId(), this.getPageId());
            if(StringUtil.isNotEmpty(str)){
                AjaxResult res= new AjaxResult();
                res.setMessage(str, MessageType.错误消息);
                return res;
            }
        }

        // 确认提醒
        if (result.getErrorCount() == 0) {
            AjaxResult ajaxResult = trdBondInvestTrade002Service.checkConfirmInfo(dto, result);
            String confirmMsg = "";
            if (null != ajaxResult) {
                confirmMsg = ajaxResult.getData().toString();
            }
            String changeLineMark = "<br/>";
            //添加托管账户剩余现金校验 add by yjm  2019-07-03 start
            //银行间 债券买入交易和逐笔划款的通道债券买入交易，提交时都没有判断托管户可用剩余现金是否足够
            List<BussTrdTransfenpeiDto> bussTrdTransfenpeiDto=dto.getBussTrdTransfenpeiDto();
            if(StringUtil.isNotBlank(dto.getZhilingtype())&&StringUtil.equals(dto.getZhilingtype(),"02")){
                if(CollectionUtils.isNotEmpty(bussTrdTransfenpeiDto)){
                    for(int i=0;i<bussTrdTransfenpeiDto.size();i++){
                        if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dto.getPs())) {
                            if ((StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()) && StringUtil.isEmpty(dto.getPayType()))
                                    || StringUtil.equals(dto.getPayType(), DictCodeListExt2.划款方式_逐笔划款.getValue())){
                                if (StringUtil.isNotEmpty(bussTrdTransfenpeiDto.get(i).getTradeAmtAndFee())) {
                                    if (bussTrdTransfenpeiDto.get(i).getTgSurpluscash()!=null&&!(bussTrdTransfenpeiDto.get(i).getTgSurpluscash().compareTo(BigDecimal.ZERO)==0)
                                            &&bussTrdTransfenpeiDto.get(i).getTradeAmtAndFee().compareTo(bussTrdTransfenpeiDto.get(i).getTgSurpluscash()) > 0) {
                                        if (StringUtil.isNotBlank(confirmMsg)) {
                                            confirmMsg += changeLineMark;
                                        }
                                        confirmMsg += this.getMessage("托管户剩余现金不足，请确认是否继续？");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dto.getPs())) {
                    if ((StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()) && StringUtil.isEmpty(dto.getPayType()))
                            || StringUtil.equals(dto.getPayType(), DictCodeListExt2.划款方式_逐笔划款.getValue())){
                        if (StringUtil.isNotEmpty(dto.getBussTrdAvailableCashQuery()) && StringUtil.isNotEmpty(dto.getTradeAmtAndFee())) {
                            if (dto.getBussTrdAvailableCashQuery().getSecAcctBalance()!=null&&!(dto.getBussTrdAvailableCashQuery().getSecAcctBalance().compareTo(BigDecimal.ZERO)==0)
                                   && dto.getTradeAmtAndFee().compareTo(dto.getBussTrdAvailableCashQuery().getSecAcctBalance()) > 0) {
                                if (StringUtil.isNotBlank(confirmMsg)) {
                                    confirmMsg += changeLineMark;
                                }
                                confirmMsg += this.getMessage("托管户剩余现金不足，请确认是否继续？");
                            }
                        }
                    }
                }
            }
            //添加托管账户剩余现金校验 add by yjm  2019-07-03 end
            if (StringUtil.isNotBlank(confirmMsg)) {
                ajaxResult = new AjaxResult();
                ajaxResult.setData(confirmMsg);
            }
            if (ajaxResult != null) {
                return ajaxResult;
            }
        }
        return commonCheck(result, dto.getOperateType());  
    }

    /**
     * 保存提交
     *
     * @param dto
     * @return 画面输入参数
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("save")
    @ResponseBody
    AjaxResult save(@RequestBody TrdBondInvestTradeDto dto) throws Exception {
    	List<TrdBondInvestTradeDto> dtoList = new ArrayList<>();
    	List<BussTrdTransfenpeiDto> fenpeiList = dto.getBussTrdTransfenpeiDto();//组合分配
    	List<BussTrdTransferOrderDto> orderList = dto.getBussTrdTransferOrder();//划款指令
		
		if(fenpeiList != null && fenpeiList.size()>0){
            for (int i = 0; i < fenpeiList.size(); i++) {
                TrdBondInvestTradeDto tempDto = new TrdBondInvestTradeDto();
                BeanUtil.copyProperties(tempDto, dto);
                BussTrdTransfenpeiDto fenpei = fenpeiList.get(i);
                if(fenpei.getTradeAmt()!=null&&fenpei.getTradeAmt().compareTo(new BigDecimal(0))>0){
                    List<BussTrdTransferOrderDto> tmpOrderList = new ArrayList<BussTrdTransferOrderDto>();
                    tmpOrderList.add(orderList.get(i));
                    tempDto.setPortfolioId(fenpei.getPortfolioId());
                    tempDto.setTradeAmt(fenpei.getTradeAmt());
                    tempDto.setCpriceAmt(fenpei.getCpriceAmt());
                    tempDto.setFpriceAmt(fenpei.getFpriceAmt());
                    tempDto.setBussTrdTransferOrder(tmpOrderList);
                    tempDto.setProcedureAmt(fenpei.getProcedureAmt());//手续费
                    tempDto.setSettleServiceAmt(fenpei.getSettleServiceAmt());//结算服务费
                    tempDto.setSettleAmt(fenpei.getSettleAmt());//结算费
                    tempDto.setExchangeAmt(fenpei.getExchangeAmt());//经手费
                    tempDto.setCommissionAmt(fenpei.getCommissionAmt());//佣金
                    tempDto.setTradeAmtAndFee(fenpei.getTradeAmtAndFee());//交易金额
                    tempDto.setSecManageAcctId(fenpei.getSecManageAcctIdDuo());//证券账户
                    tempDto.setIntAmt(fenpei.getIntAmt());//应计利息总额
                    tempDto.setEntrustId(fenpei.getEntrustId());//设置委托代码
                    tempDto.setOwnTradeAmt(fenpei.getTradeAmt());
                    tempDto.setInvAim(fenpei.getInvestaim());
                    if (baseGetBusinessId != null) {
                        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                        if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), tempDto.getPs())) {
                            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
                        }
                        else {
                            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券卖出);
                        }
                        tempDto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
                    } else {
                        tempDto.setTotleTradeId(IDGenerator.generate());
                    }
                    tempDto.setRiskTrialRetDto(null);
                    //多组合交易对风险试算处理逻辑
                    if(CollectionUtils.isNotEmpty(dto.getRiskTrialRetDto())){
                         List<RiskTrialRetDto> riskTrialRetlist = new ArrayList<>();
                         for (RiskTrialRetDto riskTrialRetDto:dto.getRiskTrialRetDto()) {
                            if(StringUtils.equals(riskTrialRetDto.getPortfolioCode(),fenpei.getPortfolioId())){
                                riskTrialRetlist.add(riskTrialRetDto); 
                            }
                            List<String> list= interfRiskRdsService.queryPortfolioPackageDtlByBagId(riskTrialRetDto.getPortfolioCode());
                            if (!CollectionUtils.isEmpty(list) && list.contains(fenpei.getPortfolioId())) {
                                riskTrialRetlist.add(riskTrialRetDto); 
                            } 
                         }    
                         tempDto.setRiskTrialRetDto(riskTrialRetlist);
                    }
                    if(StringUtil.equals(tempDto.getSettleSpeedStr(),DictCodeList.清算速度_T_0.getValue())){
                        tempDto.setSettleSpeedStr("0");
                    }else if(StringUtil.equals(tempDto.getSettleSpeedStr(),DictCodeList.清算速度_T_1.getValue())){
                        tempDto.setSettleSpeedStr("1");
                    }else if(StringUtil.equals(tempDto.getSettleSpeedStr(),DictCodeList.清算速度_T_2.getValue())){
                        tempDto.setSettleSpeedStr("2");
                    }
                    dtoList.add(tempDto);
                }else{
                    fenpeiList.remove(i);
                    i--;
                }
    		}
		}else{
		    TrdBondInvestTradeDto tempDto = new TrdBondInvestTradeDto();
            BeanUtil.copyProperties(tempDto, dto);
			if (baseGetBusinessId != null) {
				BussinessCodeParameter codeParameter = new BussinessCodeParameter();
				if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), tempDto.getPs())) {
					codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
				}
				else {
					codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券卖出);
				}
				tempDto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
			}
			else {
				tempDto.setTotleTradeId(IDGenerator.generate());
			}
			dtoList.add(tempDto);
		}
        //----------add by jjpei 20200114 核风险版本 start----------
//        if(dto.getOperateType().equals(BussinessConstant.SUBMIT)&& CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)){
//            AjaxResult ar =interfRiskRdsService.checkBatchRiskRds(dtoList,new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
//                    BussinessDictCodeListRds.接口系统ID_实占接口.getValue(), BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(),DictCodeList.平安资产类型_债券.getValue(),
//                    (sm,en)->interfRiskRdsService.sendRiskRdsData(sm,en));
//            if (StringUtil.isNotEmpty(ar)) {
//                return ar;
//            }
//        }
        //----------add by jjpei 20200114 核风险版本 end----------

        // ----------add by zhangrui1 20200704 批量实占功能 start----------
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            AjaxResult ar = TradeRdsCommonAop.doListOccupy(dtoList,
                    DictCodeList.平安资产类型_债券.getValue(), true, dto.getTrdInvestPersonInfo());
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        // ----------add by zhangrui1 20200704 批量实占功能 end----------
        for(int i=0;i<dtoList.size();i++){
            if(i==0){
                // 将附件和当前业务对象绑定（更新附件的业务代码）
                fileAttachmentProcess.updateBussinessNo(
                        dto.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                                Collectors.toList()),
                        dtoList.get(0).getTotleTradeId());
            }else{
                List<UploadFile> uploadFileList =new ArrayList<UploadFile>();
                for(UploadFile uploadFile : dto.getUploadFileList()){
                    UploadFile file = fileAttachmentProcess.fetch(uploadFile.getFileNo());
                    if (file != null) {
                        file.setFileNo(IDGenerator.generate());
                        fileAttachmentProcess.upload(file);
                        uploadFileList.add(file);
                    }
                }
                // 将附件和当前业务对象绑定（更新附件的业务代码）
                fileAttachmentProcess.updateBussinessNo(
                        uploadFileList.stream().map(UploadFile::getFileNo).collect(
                                Collectors.toList()),
                        dtoList.get(i).getTotleTradeId());
            }
        }

        ProcessParameter<TrdBondInvestTradeDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdBondInvestTradeDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId("TrdBondInvestTrade002");
//        processParameter.setPageId(this.getPageId());
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            processParameter.setOperationType(OperationType.NEW);
        }
        else {
            processParameter.setOperationType(OperationType.MODIFY);
        }
        workFlowParameter.setPageId("TrdBondInvestTrade002");

//        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode((entity) -> entity.getTotleTradeId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
            getMessage("TrdBondInvestTrade002.tradeId") + GlobalConstant.COLON + entity.getTotleTradeId(),
            getMessage("TrdBondInvestTrade002.finprodName") + GlobalConstant.COLON + entity.getFinprodName(),
            getMessage("TrdBondInvestTrade002.tradeDate") + GlobalConstant.COLON + DateUtil.format(entity.getTradeDate())));
        trdBondInvestTrade002Service.setEntity(dtoList);
        String msg = trdBondInvestTrade002Service.start(processParameter, dtoList.toArray(new TrdBondInvestTradeDto[]{}));
        return new AjaxResult(msg);
    }
    
    //组合代码
    @ResponseBody
    @RequestMapping("getPortfolioId")
    public List<SelectOptionDto> getPortfolioId(@RequestParam Map<String, Object> params) {
        List<SelectOptionDto> PortfolioI = trdBondInvestTrade002Service.selectPortfolioId(params);
        return PortfolioI;
    }


    /**
     * 查询债券cstp交易下拉框
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping("querySecCstpInfoList")
    public Page<SelectOptionDto> querySecCstpInfoList() {
        Page<SelectOptionDto> suggestDtos = trdBondInvestTrade002Service.querySecCstpInfoList(RequestUtil.getPageParam());
        return suggestDtos;
    }
   
    @ResponseBody
    @RequestMapping("querySecCstpInfoByNo")
    public TrdBondInvestTradeCSTPDto querySecCstpInfoByNo(String fedealno,String entrustId,String preTradeId, String isCstpSure){
        return trdBondInvestTrade002Service.querySecCstpInfoByNo(fedealno,entrustId,preTradeId,isCstpSure);
    }

    /**
     * 银行间成交编号联动费用设置
     *
     * @param inputDto 画面参数
     * @return 计算结果
     * @see
     * @since 1.0
     */
    @RequestMapping("calculateFeeData")
    @ResponseBody
    public AjaxResult calculateFeeData(@RequestBody TrdBondInvestTradeDto inputDto) {
        return trdBondInvestTrade002Service.calculateFeeData(inputDto);
    }
    
    // add by zhanggenglin 质押式回购多组合 20190813 start
    @ResponseBody
    @RequestMapping("/selectIndex")
    public String selectIndex() {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
        String index=baseGetBusinessId.buildBussinessCode(codeParameter);
        return StringUtil.trimHtmlTag(index);
    }
    // add by zhanggenglin 质押式回购多组合 20190813 end

    /**
     * 查询通道可用现金
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTongDaoCash")
    public String getTongDaoCash(@RequestBody Map<String, Object> param){
        BigDecimal tongDaoCash = trdBondInvestTrade002Service.getTongDaoCash(param);
        String result = String.valueOf(tongDaoCash);
        return result;
    }

    /**
     * 查询银行间可用余额
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("/getBankCash")
    public String getBankCash(@RequestBody Map<String, Object> param){
        BigDecimal tongDaoCash = trdBondInvestTrade002Service.getBankCash(param);
        String result = String.valueOf(tongDaoCash);
        return result;
    }

    /**
     * 查询委托信息
     *
     * @return 委托信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getEntrustInfos")
    public List<BaseEtrInvestInfoDto> getEntrustInfos(@RequestBody TrdBondInvestTradeDto dto) {
        //中标结果查询进来  add by gejiaao 2019/7/26 start
//        if (StringUtil.isNotBlank(dto.getBondFirstSign())){
//            return trdBondInvestTrade002Service.getEntrustInfoTmp(dto.getEntrustId());
//        }
        //中标结果查询进来  add by gejiaao 2019/7/26 end
        List<BaseEtrInvestInfoDto> entrustInfos = trdBondInvestTrade002Service.getEntrustInfos(dto.getEntrustId());
        if (StringUtil.isNotBlank(dto.getBondFirstSign())){
            String[] winPrinAmtList = dto.getWinPrinAmtList().split(",");
            for (int i = 0; i < entrustInfos.size(); i++) {
                entrustInfos.get(i).getEtrEntrust().setEntrustNum(new BigDecimal(winPrinAmtList[i]));
            }
        }
        return entrustInfos;
    }
    
    /**
     * 查询委托信息
     *
     * @return 委托信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getEntrustInfos2")
    public List<BussTrdTransfenpeiDto> getEntrustInfos2() {
        Map<String,Object> pageMap = RequestUtil.getPageParam();
        String entrustId = (String)pageMap.get("entrustId");
        String distribution = (String)pageMap.get("distribution");
        String ps = (String)pageMap.get("ps");
        List<BaseEtrInvestInfoDto> entrustInfos = trdBondInvestTrade002Service.getEntrustInfos(entrustId);
        
        
        List<BussTrdTransfenpeiDto> list = new ArrayList<>();
        if(entrustInfos!=null){
            for(BaseEtrInvestInfoDto ret: entrustInfos){
                if(ret.getEtrEntrust()!=null){
                    BussTrdTransfenpeiDto bussTrdTransfenpeiDto = new BussTrdTransfenpeiDto();
                    bussTrdTransfenpeiDto.setEntrustId(ret.getEtrEntrust().getEntrustId());
                    bussTrdTransfenpeiDto.setDoEntrustMoney(ret.getDoEntrustMoney().multiply(new BigDecimal(100)));
                    bussTrdTransfenpeiDto.setTradeAmt(ret.getDoEntrustMoney().multiply(new BigDecimal(100)));
                    if(ret.getEtrEntrust().getPrice()==null){
                        bussTrdTransfenpeiDto.setUnitCprice(new BigDecimal(-5));
                    }else{
                        bussTrdTransfenpeiDto.setUnitCprice(ret.getEtrEntrust().getPrice());
                    }
                    bussTrdTransfenpeiDto.setEntrustDate(ret.getEtrEntrust().getCreateTime());
                    bussTrdTransfenpeiDto.setPortfolioId(ret.getEtrEntrust().getPortfolioId());
                    bussTrdTransfenpeiDto.setInvestaim(ret.getEtrEntrust().getInvAim());
                    list.add(bussTrdTransfenpeiDto);
                }
            }
            
            if("140".equals(distribution)){//时间优先
                //排序
                Collections.sort(list, new Comparator<BussTrdTransfenpeiDto>() {
                   @Override
                   public int compare(BussTrdTransfenpeiDto o1, BussTrdTransfenpeiDto o2) {
                       // TODO Auto-generated method stub
                       return o1.getEntrustDate().compareTo(o2.getEntrustDate());
                   }
                 });
            }else if("141".equals(distribution)){//数量优先
              //排序
              Collections.sort(list, new Comparator<BussTrdTransfenpeiDto>() {
                 @Override
                 public int compare(BussTrdTransfenpeiDto o1, BussTrdTransfenpeiDto o2) {
                     // TODO Auto-generated method stub
                     return o2.getTradeAmt().compareTo(o1.getTradeAmt());
                 }
               });
            }else if("142".equals(distribution)){//价格优先
                Collections.sort(list, new Comparator<BussTrdTransfenpeiDto>() {
                    @Override
                    public int compare(BussTrdTransfenpeiDto o1, BussTrdTransfenpeiDto o2) {
                        // TODO Auto-generated method stub
                        return o1.getTradeAmt().compareTo(o2.getTradeAmt());
                    }
                  });
                //排序
                if("T01".equals(ps)){
                    Collections.sort(list, new Comparator<BussTrdTransfenpeiDto>() {
                        @Override
                        public int compare(BussTrdTransfenpeiDto o1, BussTrdTransfenpeiDto o2) {
                            // TODO Auto-generated method stub
                            return o2.getUnitCprice().compareTo(o1.getUnitCprice());
                        }
                      });
                }else if("T02".equals(ps)){
                    Collections.sort(list, new Comparator<BussTrdTransfenpeiDto>() {
                        @Override
                        public int compare(BussTrdTransfenpeiDto o1, BussTrdTransfenpeiDto o2) {
                            // TODO Auto-generated method stub
                            return o1.getUnitCprice().compareTo(o2.getUnitCprice());
                        }
                      });
                }
                
            }
        }
        return list;
    }
}
