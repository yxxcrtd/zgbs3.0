/*
 * 文件名：TrdBondInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.bond.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.settle.BussSettleOrderCommService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeRevokeDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade001Service;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.*;

/**
 * 债券交易管理
 *
 * @author Zane.Y
 * @version 1.0 2018年9月6日
 * @see TrdBondInvestTrade001Controller
 * @since 1.0
 */
@Controller
@PageId("TrdBondInvestTrade001")
@RequestMapping("/invest/trade/bond/TrdBondInvestTrade001")
public class TrdBondInvestTrade001Controller extends BaseController {

    @Autowired
    private TrdBondInvestTrade001Service trdBondInvestTrade001Service;

    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;

    /**
     * 划款指令组件
     */
    @Autowired
    private BussSettleOrderCommService bussSettleOrderCommService;

    /**
     * 流程组件
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    /**
     * 交易基础组件调用Service
     */
    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    @Override
    public String init(Model model) {
        String fTradeId = this.getParam("fTradeId");
        String entrustId = this.getParam("entrustId");
        //添加登记托管机构数据字典 add by lishuping 20191010 start
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.交易场所,
            DictCode.交易流程状态, DictCode.交易平台, DictCode.交易市场, DictCode.登记托管机构);
        //添加登记托管机构数据字典 add by lishuping 20191010 end
        if(StringUtils.isNotBlank(fTradeId)){
            model.addAttribute("fTradeId",fTradeId);
        }
        if(StringUtils.isNotBlank(entrustId)){
            model.addAttribute("entrustId",entrustId);
        }
        //add by wp 是否查询资产减值清理交易 20210427 start
        model.addAttribute("IS_QUERY_ASSETS_LOSS", CacheUtil.getSysParam("IS_QUERY_ASSETS_LOSS"));
        
      //如果为是交易方向添加清理核销
//        if(StringUtil.equals(CacheUtil.getSysParam("IS_QUERY_ASSETS_LOSS"), "01")) {
// 	       SelectDto selectDto = getSelectDataFromDataDic(DictCode.交易方向,null);
// 	       SelectOptionDto selectOptionDto = new SelectOptionDto();
// 	       selectOptionDto.setId(DictCodeListExt3.交易方向_清理核销.getValue());
// 	       selectOptionDto.setText(DictCodeListExt3.交易方向_清理核销.getCodeName());
// 	       selectOptionDto.setCol(DictCode.交易方向.getValue());
// 	       selectDto.getOptions().add(selectOptionDto);
// 	       model.addAttribute(DictCode.交易方向.getValue(), JsonUtils.objectArray2Json(selectDto.getOptions().toArray()));
//        }
      //add by wp 是否查询资产减值清理交易 20210427 end
        return super.init(model);
    }
    /**
     * 分页查询
     *
     * @return Page<TrdBondInvestTradeDto>泛型的page实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("findPage")
    @ResponseBody
    public Page<TrdBondInvestTradeDto> findPage()
         {
        return trdBondInvestTrade001Service.findPage(RequestUtil.getPageParam());
    }

//    @RequestMapping("getEntrustId")
//    @ResponseBody
//    public List<SuggestDto> getEntrustId()
//            throws Exception {
//        return trdBondInvestTrade001Service.getEntrustId(RequestUtil.getParameters(this.getRequest()));
//    }

//    @RequestMapping("getPortfolioId")
//    @ResponseBody
//    public List<SuggestDto> getPortfolioId()
//            throws Exception {
//        return trdBondInvestTrade001Service.getPortfolioId(RequestUtil.getParameters(this.getRequest()));
//    }

//    @RequestMapping("getFinprodMarketId")
//    @ResponseBody
//    public List<SuggestDto> getFinprodMarketId()
//            throws Exception {
//        return trdBondInvestTrade001Service.getFinprodMarketId(RequestUtil.getParameters(this.getRequest()));
//    }

    /**
     * 导出信息excel
     *
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export()
         {
        return trdBondInvestTrade001Service.export(this.getPageId(), RequestUtil.getPageParam());
    }

    /**
     * 撤销前check
     *
     * @param inputDto 画面输入参数dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @RequestMapping("checkRevoke")
    @ResponseBody
    public AjaxResult check(@Valid @RequestBody TrdBondInvestTradeRevokeDto inputDto,
                            BindingResult result) {
      WflValidInputParameter parameter = new WflValidInputParameter();
        parameter.setPageId("TrdBondInvestTrade002");
        parameter.setBusinessDataPk1(inputDto.getTradeId() + "R");
        parameter.setBusinessDataPk2("F01");
        if (mstWorkflowCommonService.isInWflProcess(parameter)) {
            ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
            errorList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0041")));
            AjaxResult ar = new AjaxResult();
            ar.setErrorList(errorList);
            return ar;
        }
        /*add by xr   已撤销的交易不允许撤销*/
        TrdBondInvestTradeDto dto = trdBondInvestTrade002Service.getTrdBondInvestTradeDto(inputDto.getTradeId() + "R");
        if(Objects.isNull(dto)){
            ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
            errorList.add(new ValidMessage(this.getMessage("已撤销的交易不允许撤销")));
            AjaxResult ar = new AjaxResult();
            ar.setErrorList(errorList);
            return ar;
        }

        TrdBondInvestTradeDto tradeDto = trdBondInvestTrade002Service.getTrdBondInvestTradeDto(inputDto.getTradeId());
        int count = trdBondInvestTrade002Service.selectIssueAsset(tradeDto.getFinprodId());
        if (count > 0){
            ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
            errorList.add(new ValidMessage(this.getMessage("发行标的存在对应的上市流通交易,不允许撤销")));
            AjaxResult ar = new AjaxResult();
            ar.setErrorList(errorList);
            return ar;
        }
        
        // 确认提醒 TISHKBS-1113【组合资金账户头寸查询】内转交易撤销提醒 add by hpp 2020-09-17 start
        if (result.getErrorCount() == 0) {
            AjaxResult ajaxResult = new AjaxResult();

            //根据交易编号查询另一笔未撤销的转仓交易信息
            String tradeId = inputDto.getTradeId();
//            TrdProductDeal res = trdCommonBaseService.selectZCDeal(tradeId);
//            if(StringUtil.isNotEmpty(res)){
//                confirmMsg = "此交易为转仓交易，撤销后需要将业务编号为["+
//                        res.getTradeId()+"]的反向转仓交易也撤销掉，请确认是否继续？";
//            }else{
//                confirmMsg = "请确认是否撤销？";
//            }
//            if (StringUtil.isNotBlank(confirmMsg)) {
//                ajaxResult.setData(confirmMsg);
//            }
//            if (ajaxResult != null) {
//                return ajaxResult;
//            }
            TrdProductDeal trdProductDeal = trdCommonBaseService.selectZCDealByTid(tradeId, true);
            // 查询到 卖出的转仓交易  说明 点击的是买入。
            if(trdProductDeal!=null&& StringUtil.equals(DictCodeList.交易方向_卖出.getValue(),trdProductDeal.getPs())){
                ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
                errorList.add(new ValidMessage(this.getMessage("内部转仓只能撤销卖出交易,买入自动撤销！")));
                ajaxResult.setErrorList(errorList);
                return ajaxResult;
            }

        }
        // TISHKBS-1349 债券 进行买入交易后 隔天做卖出交易 卖出交易完成后对买入交易进行撤销,此时不提示存在后续交易 增加提示 add by caojin 20201001 start
        int T02sum = trdBondInvestTrade002Service.selectTradeT02(inputDto.getTradeId());
        if(T02sum > 0){
            ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
            errorList.add(new ValidMessage(this.getMessage("后续存在有效的卖出交易！")));
            AjaxResult ar = new AjaxResult();
            ar.setErrorList(errorList);
            return ar;
        }
        // TISHKBS-1349 债券 进行买入交易后 隔天做卖出交易 卖出交易完成后对买入交易进行撤销,此时不提示存在后续交易 增加提示 add by caojin 20201001 end

        // 确认提醒 TISHKBS-1113【组合资金账户头寸查询】内转交易撤销提醒 add by hpp 2020-09-17 end
        return commonCheck(result, inputDto.getOperateType());
    }

    /**
     * 撤销
     * @param inputDto 画面输入参数dto
     * @return 撤销结果
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("doRevoke")
    @ResponseBody
    AjaxResult doRevoke(@RequestBody TrdBondInvestTradeDto inputDto) throws WorkFlowException {
        String operateType = inputDto.getOperateType();
        String totleTradeId = inputDto.getTotleTradeId();
        inputDto = trdBondInvestTrade002Service.getTrdBondInvestTradeDto(inputDto.getTotleTradeId());
        //add by  meifaxin 后面审批用到 start
        inputDto.setTradeId(totleTradeId);
        //add by  meifaxin 后面审批用到 end
        String turnInvAim = trdBondInvestTrade002Service.getReverseTradeDtoInvAim(inputDto.getTotleTradeId());
        if(StringUtil.isNotEmpty(turnInvAim)) {
            inputDto.setTurnInvAim(turnInvAim);
        }
        //划款指令
        if (inputDto != null && StringUtil.isEmptyList(inputDto.getBussTrdTransferOrder())){
            List<BussTrdTransferOrderBase> orderDtolist = bussSettleOrderCommService.selectOrdersByBizCode(inputDto.getTotleTradeId()) ;
            if (!StringUtil.isEmptyList(orderDtolist)){
               List<BussTrdTransferOrderDto> bussTrdTransferOrderDtoList = new ArrayList<BussTrdTransferOrderDto>();
                BussTrdTransferOrderDto bussTrdTransferOrderDto = new BussTrdTransferOrderDto();
                BeanUtil.copyPropertiesIgnoreNull(bussTrdTransferOrderDto, orderDtolist.get(0));
                bussTrdTransferOrderDtoList.add(bussTrdTransferOrderDto);
                inputDto.setBussTrdTransferOrder(bussTrdTransferOrderDtoList);
            }
        }
        // 转仓新增债券 基金走撤销新头寸。
        if(StringUtil.equals(inputDto.getTradeMarketMode(),DictCodeListExt1.交易市场_一级市场.getValue())
            || DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(inputDto.getFinprodType2())
            || DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(inputDto.getFinprodType2())){
            inputDto.setBondFirstSign("01");
        }
        inputDto.setTotleTradeId(totleTradeId + "R");
        inputDto.setFinprodType2("F01");
        inputDto.setOperateType(operateType);
        //判断流程中是否存在,如果流程不存在则说明是生成的反向交易，组装撤销dto yjm end 20191015
        ProcessParameter<TrdBondInvestTradeDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdBondInvestTradeDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId("TrdBondInvestTrade002");
        processParameter.setOperationType(OperationType.REVOKE);

        workFlowParameter.setPageId("TrdBondInvestTrade002");
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode((entity) -> entity.getTotleTradeId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
            getMessage("TrdBondInvestTrade002.tradeId") + GlobalConstant.COLON + entity.getTotleTradeId(),
            getMessage("TrdBondInvestTrade002.finprodName") + GlobalConstant.COLON + entity.getFinprodName(),
            getMessage("TrdBondInvestTrade002.tradeDate") + GlobalConstant.COLON + DateUtil.format(entity.getTradeDate())));

        String msg = trdBondInvestTrade002Service.start(processParameter, inputDto);

        return new AjaxResult(msg);
    }

    /**
     * 获取委托代码
     * @return 联想文本框实体类集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getEntrustIdByType")
    public List<SuggestDto> getEntrustIdByType() {
        String entrustId = getParam("param");
        Map<String, Object> map = new HashMap<>();
        map.put("param", entrustId);
        map.put("finProdType", Arrays.asList(DictCodeListExt1.金融产品类型_债券.getValue()));
        map.put("ENTRUST_STATUS_LIST", Arrays.asList(DictCodeList.委托状态_已接收.getValue(),
                DictCodeList.委托状态_已完成.getValue(), DictCodeList.委托状态_已关闭.getValue()));
        return trdBondInvestTrade001Service.getEntrustIdByType(map);
    }

    /**
     * 导出pdf add by zengchengfa 20210803
     * @return
     * @throws Exception
     */
    @RequestMapping("exportPdf")
    public ResponseEntity<byte[]> exportPdf(){
        return trdBondInvestTrade001Service.exportPdf(this.getPageId(),RequestUtil.getPageParam());
    }
}
