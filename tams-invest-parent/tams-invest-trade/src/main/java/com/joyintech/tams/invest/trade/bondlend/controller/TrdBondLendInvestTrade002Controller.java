/*
 * 文件名：TrdBondLendInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年6月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.bondlend.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.component.business.asset.FinInterestFactorService;
import com.joyintech.tams.component.business.asset.FinPayInterestStdInfoService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.trade.bondlend.dto.TrdBondLendInvestTradeDto;
import com.joyintech.tams.invest.trade.bondlend.service.TrdBondLendInvestTrade002Service;
import com.joyintech.tams.invest.trade.common.controller.TrdCommonController;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 债券借贷交易新建
 *
 * @author 裴家俊
 * @version 1.0 2019年6月25日
 * @see TrdBondLendInvestTrade002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdBondLendInvestTrade002")
@RequestMapping("/invest/trade/bondlend/TrdBondLendInvestTrade002")
public class TrdBondLendInvestTrade002Controller extends BaseController {
	@Autowired
	private TradeCommonService tradeCommonService;
    /**
     * trdBondInvestTrade002Service
     */
    @Autowired
    private TrdBondLendInvestTrade002Service trdBondLendInvestTrade002Service;
    
    @Autowired
    private FinPayInterestStdInfoService finPayInterestStdInfoService;

    @Autowired(required = false)
    private FinInterestFactorService finInterestFactorService;
    
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * BussTrdTransferOrderService
     */
    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    
    /**
     * DateCalService
     */
    @Autowired
    private DateCalService dateCalService;

    @Autowired(required = false)
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;
    
    @Autowired
    private TrdCommonController trdCommonController;

    @Override
    public String init(Model model) {
        setSelectOptions(model);
        TrdBondLendInvestTradeDto dto = new TrdBondLendInvestTradeDto();
        String tradeId = this.getParam("tradeId");
        String workflowStatus = this.getParam("workflowStatus");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        //配置单位 add by yeming 20210726 start
        String entrustAmtUnit = CacheUtil.getSysParam("entrustAmtUnit");
        model.addAttribute("entrustAmtUnit", entrustAmtUnit );
        //配置单位 add by yeming 20210726 end

        if (StringUtil.equals(workflowStatus, DictCodeListExt1.交易流程状态_流程中.getValue())
            || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(workflowId))) {
            dto = trdBondLendInvestTrade002Service.getBussinessEntityByWorkflowId(
                workflowId);
            model.addAttribute("riskTrialRetDto", JsonUtils.object2Json(dto.getRiskTrialRetDto()));
            model.addAttribute("workflowId",workflowId);
        }
        else if (StringUtil.isNotEmpty(tradeId)) {
            dto = trdBondLendInvestTrade002Service.getTrdBondLendInvestTradeDto(tradeId);
        }
        else {
            String entrustId = this.getParam("entrustId");
            if (StringUtil.isNotEmpty(entrustId)) {
                model.addAttribute("entrustId", entrustId);
                dto.setEntrustId(entrustId);
                //add by wangpeng 20190901 交易录单带出不出质押券信息 
                BaseEtrInvestInfoDto baseEtrDto =trdBondLendInvestTrade002Service.getEntrustInfo(dto.getEntrustId());
                BeanUtil.copyPropertiesIgnoreNull(dto, baseEtrDto.getEtrEntrust());
                BeanUtil.copyPropertiesIgnoreNull(dto, baseEtrDto.getEtrEntrustAdd());
                if(StringUtil.isNotBlank(dto.getRemark())&&StringUtil.contains(dto.getRemark(),"退回原因")){
                    dto.setRemark("");
                }
                //add by wangpeng 20190901 end
            }
        }
        //配置单位 edit by yeming 20210802 start
        if(StringUtil.equals(entrustAmtUnit,"02")){
            if(dto.getShareAmt() != null) {
                dto.setShareAmt(dto.getShareAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
            }
        }
        //配置单位 edit by yeming 20210802 end
        if (StringUtil.isNotBlank(tradeId)) {
            model.addAttribute("tradeId", tradeId);
            model.addAttribute("tradeEntrustId", tradeId);
        }
        else {
            //zhujun BSBS-441@2019-05-20 兼容指令接收交易录单附件不显示问题
            model.addAttribute("tradeId", dto.getTradeId());
            model.addAttribute("tradeEntrustId", dto.getTradeId()==null?this.getParam("entrustId"):dto.getTradeId());
        }
        if (StringUtil.isNotBlank(workflowId)) {
            model.addAttribute("workflowId", workflowId);
        }
        else {
            model.addAttribute("workflowId", dto.getWorkflowId());
        }

        model.addAttribute("entity", dto);
        //人员信息
        model.addAttribute("entity_trdInvestPersonInfo",
            dto.getTrdInvestPersonInfo() == null ? null : JsonUtils.object2Json(
                dto.getTrdInvestPersonInfo()));
        // 添加质押券信息
        model.addAttribute("entity_bussRepoSecDto",
            dto.getBussRepoSec() == null ? null : JsonUtils.object2Json(
                dto.getBussRepoSec()));
        // 划款信息
        if (dto != null && !StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
            model.addAttribute("entity_bussRepFinProductInfoDto",
                JsonUtils.object2Json(dto.getBussTrdTransferOrder().get(0)));
        }
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F12",DictCodeListExt2.平安资产类型_债券借贷.getValue());
        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);
        return super.init(model);
    }

    private void setSelectOptions(Model model) {
        this.setSelectOptionToModel(model, DictCode.回购拆借方向, DictCode.交易场所,
            DictCode.有效状态, DictCode.争议解决方式, DictCode.币种,
            DictCode.结算方式_债券借贷, DictCode.期限品种_债券借贷, DictCode.清算速度, DictCode.是否, DictCode.持有目的);
        model.addAttribute("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        model.addAttribute("BACK_LEND_DIRECTION_05", DictCodeListExt2.回购拆借方向_融入.getValue());
        model.addAttribute("BACK_LEND_DIRECTION_06", DictCodeListExt2.回购拆借方向_融出.getValue());
        model.addAttribute("ACCOUNT_SPEED_T0", DictCodeList.清算速度_T_0.getValue());
        model.addAttribute("ACCOUNT_SPEED_T1", DictCodeList.清算速度_T_1.getValue());
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        List<SelectOptionDto> options = new ArrayList<>();
        SelectOptionDto option = new SelectOptionDto();
        option.setId("02");
        option.setText("券券对付");
        option.setCol("SETTLEMENT_WAY_LENDING");
        option.setParentId("");
        options.add(option);
        model.addAttribute("SETTLEMENT_WAY_LENDING_02", JsonUtils.objectArray2Json(options.toArray()));
        model.addAttribute("FINPROD_TYPE_TG_F29", DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F30", DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
        //add by wangpeng 20101011 
        model.addAttribute("TERM_TYPE_BONDLEND_L001", DictCodeListExt2.期限品种_债券借贷_L001.getValue());
        model.addAttribute("TERM_TYPE_BONDLEND_L007", DictCodeListExt2.期限品种_债券借贷_L007.getValue());
        model.addAttribute("TERM_TYPE_BONDLEND_L014", DictCodeListExt2.期限品种_债券借贷_L014.getValue());
        model.addAttribute("TERM_TYPE_BONDLEND_L021", DictCodeListExt2.期限品种_债券借贷_L021.getValue());
        model.addAttribute("TERM_TYPE_BONDLEND_L1M", DictCodeListExt2.期限品种_债券借贷_L1M.getValue());
        model.addAttribute("TERM_TYPE_BONDLEND_L2M", DictCodeListExt2.期限品种_债券借贷_L2M.getValue());
        model.addAttribute("TERM_TYPE_BONDLEND_L3M", DictCodeListExt2.期限品种_债券借贷_L3M.getValue());
        model.addAttribute("TERM_TYPE_BONDLEND_L4M", DictCodeListExt2.期限品种_债券借贷_L4M.getValue());
        model.addAttribute("TERM_TYPE_BONDLEND_L6M", DictCodeListExt2.期限品种_债券借贷_L6M.getValue());
        model.addAttribute("TERM_TYPE_BONDLEND_L9M", DictCodeListExt2.期限品种_债券借贷_L9M.getValue());
        model.addAttribute("TERM_TYPE_BONDLEND_L1Y", DictCodeListExt2.期限品种_债券借贷_L1Y.getValue());
        model.addAttribute("SETTLEMENT_WAY_LENDING_QQDF", DictCodeListExt1.结算方式_债券借贷_券券对付.getValue());
        //add by wangpeng 20101011 end
    }

    /**
     * 查询委托信息
     *
     * @param entrustId 委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getEntrustInfo")
    public BaseEtrInvestInfoDto getEntrustInfo(String entrustId) {
        return trdBondLendInvestTrade002Service.getEntrustInfo(entrustId);
    }

    /**
     * 查询付息计划
     */
    @ResponseBody
    @RequestMapping("generationPlan")
    public List<FinCashPlanDto> generationPlan(String finprodId,String vdate){
    	List<FinCashPlanDto> invList=new ArrayList<FinCashPlanDto>();
    	 // 查询付息计划
        if (StringUtil.isNotEmpty(finprodId)) {
            invList = finPayInterestStdInfoService.getFinCashPlanByProductId(finprodId,
                DictCodeListExt1.现金流二级类型_利息.getValue(), DictCodeListExt1.现金流业务类型_金融产品.getValue(), null);
//            FinInterestFactorDto finInterestFactorDto = finInterestFactorService.getFinInterestFactorByProductId(finprodId,vdate);
//            if(StringUtil.isNotEmpty(finInterestFactorDto)&&StringUtil.isNotEmpty(finInterestFactorDto.getRate())){
//	            invList.forEach(item->{
//	            	item.setNewRate(finInterestFactorDto.getRate());
//	            });
//            }
            invList.forEach(item->{
            	item.setNewRate(item.getRangeYield());
                // 处理支付日（前端使用payDateUnadjust进行展示）
                if (Objects.isNull(item.getPayDateUnadjust())) {
                    item.setPayDateUnadjust(item.getPayDate());
                }
            });
        }
        return invList;
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
        return trdBondLendInvestTrade002Service.getSecManageAcctId(params);
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
        return trdBondLendInvestTrade002Service.getChannel(params);
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
        return trdBondLendInvestTrade002Service.getFinprodId(params);
    }
    
    /**
     * 检验是否节假日
     * @param map
     * @return
     */
    @RequestMapping("isDealCalendarHoliday")
    @ResponseBody
    public boolean isDealCalendarHoliday(@RequestBody Map<String, Object> map) {
        return dateCalService.isDealCalendarHoliday(DateUtil.parse((String)map.get("mpayDate")),(String)map.get("calendarId"));
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
        return trdBondLendInvestTrade002Service.getNextWorkDay(params);
    }

    /**
     * 当选择组合及币种时查询对应的清算账户
     * @param params 画面参数
     * @return 清算账户信息
     */
    @RequestMapping("getProAccountInfo")
    @ResponseBody
    public List<StlCapacctInfo> getProAccountInfo(@RequestParam Map<String, Object> params) {
        params.put("capacctType", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        return trdBondLendInvestTrade002Service.getProAccountInfo(params);
    }

    /**
     * 当选择证券托管户对应的清算账户
     * @param params 画面参数
     * @return 清算账户信息
     */
    @RequestMapping("getDealAccountInfo")
    @ResponseBody
    public List<StlCapacctInfo> getDealAccountInfo(@RequestParam Map<String, Object> params) {
        return trdBondLendInvestTrade002Service.getDealAccountInfo(params);
    }

    /**
     * 当选择交易对手对应的清算账户
     * @param params 画面参数
     * @return 清算账户信息
     */
    @RequestMapping("getCounterCapacctInfo")
    @ResponseBody
    public List<StlCapacctInfo> getCounterCapacctInfo(@RequestParam Map<String, Object> params) {
        List<StlCapacctInfo> retList = trdBondLendInvestTrade002Service.getCounterCapacctInfo(params);
        return retList;
    }

    /**
     * 查询所有的清算账号
     */
    @RequestMapping("getAllAccountInfo")
    @ResponseBody
    public List<SelectOptionDto> getAllAccountInfo() {
        return bussTrdTransferOrderService.queryAcctList(RequestUtil.getPageParam());
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
    public AjaxResult check(@Validated({ValidateGroupEdit.class}) @RequestBody TrdBondLendInvestTradeDto dto, BindingResult result) {
    	 if (result != null && result.hasErrors()) {
             return new AjaxResult(result);
         }
    	//huwenbin 指令接收交易录单验证数据已存在流程中
        //add by zhujun @2019-05-16 投资委托交易退回时，此check阻塞修改保存操作
        /*if(StringUtil.isNotEmpty(dto.getEntrustId()) && WorkFlowUtil.isNotFromToDoList()){
			
			  String str = tradeCommonService.check(dto.getEntrustId(), this.getPageId());
			  if(StringUtil.isNotEmpty(str)){ 
				  AjaxResult res= new AjaxResult();
				  res.setMessage(str, MessageType.错误消息); 
				  return res; 
			  }
			 
        	// 查询交易流程中录入的总金额	王贵奇	2019/9/3
        	BigDecimal entrustNum = tradeCommonService.getEntrustAmtByEntrustId(dto.getEntrustId());
            BigDecimal wflPrinAmt = tradeCommonService.getWflPrinAmt(dto.getEntrustId(), dto.getPortfolioId());
            if(entrustNum.compareTo(wflPrinAmt.add(dto.getShareAmt())) == -1) {
            	AjaxResult res= new AjaxResult();
            	List<ValidMessage> errorList = new ArrayList<>();
           	 ValidMessage validMessage = new ValidMessage("prinAmt",
           			 "可用委托份额不足,当前最多可委托份额为:"+ entrustNum.subtract(wflPrinAmt).divide(new BigDecimal(100)));
                    errorList.add(validMessage);
               res.setErrorList(errorList);
               res.setMessageType(MessageType.错误消息);
               return res;
            }
    	}*/
        // 确认提醒
        AjaxResult ajaxResult = trdBondLendInvestTrade002Service.checkConfirmInfo(dto, result);
        if (ajaxResult != null) {
            return ajaxResult;
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
    AjaxResult save(@RequestBody TrdBondLendInvestTradeDto dto) throws Exception {

        //万元转换 add  by yeming 20210817 start
        if(dto.getBussRepoSec() != null){
            if( dto.getBussRepoSec().getTotalPrinAmt()  != null){
                dto.getBussRepoSec().setTotalPrinAmt(ArithmeticUtil.multiply( dto.getBussRepoSec().getTotalPrinAmt(),new BigDecimal(10000)));
            }
            for (PledgeBondInfoDto pledgeBond :  dto.getBussRepoSec().getPledgeBondList()) {
                if (pledgeBond.getAvaPledgePrin() != null) {
                    pledgeBond.setAvaPledgePrin(ArithmeticUtil.multiply(pledgeBond.getAvaPledgePrin(), new BigDecimal(10000)));
                }
                if (pledgeBond.getPledgePrin() != null) {
                    pledgeBond.setPledgePrin(ArithmeticUtil.multiply(pledgeBond.getPledgePrin(), new BigDecimal(10000)));
                }
                if (pledgeBond.getPrin() != null) {
                    pledgeBond.setPrin(ArithmeticUtil.multiply(pledgeBond.getPrin(), new BigDecimal(10000)));
                }
            }
        }
        //万元转换 add  by yeming 20210817 end
        
        if(StringUtil.isBlank(dto.getTradeId())) {
            if (baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                //codeParameter.setCommonBusinessType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_债券借贷交易));
                codeParameter.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_债券借贷交易));
                dto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            } else {
                dto.setTradeId(IDGenerator.generate());
            }
        }
        //GLCP-51:债券借贷交易无需实占 modify by fuyongsheng 20220707 start
        //----------add by jjpei 20200114 核风险版本 start----------
//        if(dto.getOperateType().equals(BussinessConstant.SUBMIT)&& CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)){
//            AjaxResult ar=interfRiskRdsService.checkRiskRds(new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
//                    BussinessDictCodeListRds.接口系统ID_实占接口.getValue(),DictCodeListExt2.平安资产类型_债券借贷.getValue(),
//                    (en)->interfRiskRdsService.sendRiskRdsData(dto,en));
//            if (StringUtil.isNotEmpty(ar)) {
//                return ar;
//            }
//        }
        //----------add by jjpei 20200114 核风险版本 end----------
        //GLCP-51:债券借贷交易无需实占 modify by fuyongsheng 20220707 end
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典（根据交易方向与回购类型来综合判断）
        if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_融入.getValue())) {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
        } else {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
        }
        ProcessParameter<TrdBondLendInvestTradeDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdBondLendInvestTradeDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            processParameter.setOperationType(OperationType.NEW);
        }
        else {
            processParameter.setOperationType(OperationType.MODIFY);
        }
        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode((entity) -> entity.getTradeId());
        workFlowParameter.setBusinessType(DictCodeListExt2.共通业务类型_债券借贷交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
            getMessage("TrdBondLendInvestTrade002.tradeId") + GlobalConstant.COLON + entity.getTradeId(),
            getMessage("TrdBondLendInvestTrade002.finprodName") + GlobalConstant.COLON + entity.getFinprodName(),
            getMessage("TrdBondLendInvestTrade002.tradeDate") + GlobalConstant.COLON + DateUtil.format(entity.getTradeDate())));

        String msg = trdBondLendInvestTrade002Service.start(processParameter, dto);
        
        //授信占用额度
//        trdCommonController.delLinesDeal(dto.getRiskCreditListCommonDto(),dto.getTradeId());
        return new AjaxResult(msg);
    }
    
    //组合代码
    @ResponseBody
    @RequestMapping("getPortfolioId")
    public List<SelectOptionDto> getPortfolioId(@RequestParam Map<String, Object> params) {
        return trdBondLendInvestTrade002Service.getPortfolioId(params);
    }
}
