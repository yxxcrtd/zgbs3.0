package com.joyintech.tams.invest.trade.project.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.settle.BussSettleOrderCommService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.project.dto.TrdProjectInvestTrade001Dto;
import com.joyintech.tams.invest.trade.project.dto.TrdProjectInvestTrade002Dto;
import com.joyintech.tams.invest.trade.project.service.TrdProjectInvestTrade001Service;
import com.joyintech.tams.invest.trade.project.service.TrdProjectInvestTrade002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.period.trdbondduration.controller
 * @Description: 项目类资产 存续期查询
 * @date Date : 2018年09月21日 13:48
 */
@Controller
@PageId("TrdProjectInvestTrade001")
@RequestMapping("/invest/trade/project/TrdProjectInvestTrade001")
public class TrdProjectInvestTrade001Controller extends BaseController {

    @Autowired
    private TrdProjectInvestTrade001Service TrdProjectInvestTrade001Service;

    @Autowired
    private TrdProjectInvestTrade002Service TrdProjectInvestTrade002Service;

    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;

    /**
     * PeriodCommonService
     */
    @Autowired
    private TradeCommonService tradeCommonService;

    /**
     * 清算指令共通Service
     */
    @Autowired(required = false)
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

    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;

    /**
     * 需要初始化的下拉菜单
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.交易流程状态,DictCode.交易方向,
            // 项目类资产五级分类改造 add by renping 2019-08-01 start
            DictCode.行内资产二级分类, DictCode.行内资产三级分类, DictCode.行内资产四级分类,
            // 项目类资产五级分类改造 add by renping 2019-08-01 start
            DictCode.项目类一级分类};

    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DICT_CODES);
        model.addAttribute("PAGE_DATA_STATUS05", DictCodeListExt1.画面数据状态_有效.getValue());
        
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        //add by yzg 是否查询资产减值清理交易 20201022 start
        model.addAttribute("IS_QUERY_ASSETS_LOSS", CacheUtil.getSysParam("IS_QUERY_ASSETS_LOSS"));
        //如果为是交易方向添加清理核销
        if(StringUtil.equals(CacheUtil.getSysParam("IS_QUERY_ASSETS_LOSS"), "01")) {
 	       SelectDto selectDto = getSelectDataFromDataDic(DictCode.交易方向,null);
 	       SelectOptionDto selectOptionDto = new SelectOptionDto();
 	       selectOptionDto.setId(DictCodeListExt3.交易方向_清理核销.getValue());
 	       selectOptionDto.setText(DictCodeListExt3.交易方向_清理核销.getCodeName());
 	       selectOptionDto.setCol(DictCode.交易方向.getValue());
 	       selectDto.getOptions().add(selectOptionDto);
 	       model.addAttribute(DictCode.交易方向.getValue(), JsonUtils.objectArray2Json(selectDto.getOptions().toArray()));
        }
        //add by yzg 是否查询资产减值清理交易 20201022 end
        return super.init(model);
    }

    /**
     * 导出
     *
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export()
         {
    	Map<String, Object> param = RequestUtil.getParameters(getRequest());
    	param.put("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE"));
        return TrdProjectInvestTrade001Service.export(this.getPageId(), param);
    }

    /**
     * 撤销
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("revoke")
    public AjaxResult revoke(@Validated({ValidateGroupDelete.class}) TrdProjectInvestTrade002Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        //判断流程中是否存在,如果流程不存在则说明是生成的反向交易，组装撤销dto yjm start 20191015
        WflValidInputParameter parameter = new WflValidInputParameter();
        parameter.setPageId("TrdProjectInvestTrade002");
        parameter.setBusinessDataPk1(dto.getBizCode());
        if (!mstWorkflowCommonService.isProcessData(parameter) && !dto.isTransfer()){
            Map<String, Object> map = new HashMap<>();
            map.put("statusValid", DictCodeListExt1.画面数据状态_新建.getValue());
            map.put("bizCode", dto.getBizCode());
            dto = TrdProjectInvestTrade002Service.findEffectiveData(map);
            //划款指令
            if (dto != null && StringUtil.isEmptyList(dto.getBussTrdTransferOrder())){
                List<BussTrdTransferOrderBase> orderDtolist = bussSettleOrderCommService.selectOrdersByBizCode(dto.getBizCode()) ;
                if (!StringUtil.isEmptyList(orderDtolist)){
                    List<BussTrdTransferOrderDto> bussTrdTransferOrderDtoList = new ArrayList<BussTrdTransferOrderDto>();
                    BussTrdTransferOrderDto bussTrdTransferOrderDto = new BussTrdTransferOrderDto();
                    BeanUtil.copyPropertiesIgnoreNull(bussTrdTransferOrderDto, orderDtolist.get(0));
                    bussTrdTransferOrderDtoList.add(bussTrdTransferOrderDto);
                    dto.setBussTrdTransferOrder(bussTrdTransferOrderDtoList);
                }
            }
        }
        //判断流程中是否存在,如果流程不存在则说明是生成的反向交易，组装撤销dto yjm end 20191015   
        ProcessParameter<TrdProjectInvestTrade002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdProjectInvestTrade002Dto> params = new WorkFlowParameter<>();
        params.setPageId("TrdProjectInvestTrade002");
        params.setElementId("TrdProjectInvestTrade001_btn_revoke");
        params.setBusinessCode(dto==null?null:dto.getBizCode());
        params.setBusinessType(DictCodeList.共通业务类型_项目类资产交易.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("交易编号：" + entity.getBizCode(), "资产名称：" + entity.getFinprodName(),
                "交易日：" + entity.getTradeDate()));
        processParameter.setWorkFlowParameter(params);
        TrdProjectInvestTrade002Dto entity = TrdProjectInvestTrade002Service.findEffectiveData(BeanUtil.object2Map(dto));
        if(entity.getTrdInvestPersonInfo()==null){
            TrdInvestPersonInfo002Dto info002Dto = new TrdInvestPersonInfo002Dto();
            info002Dto.setTrader(entity.getTrader());
            info002Dto.setCounterId(entity.getCounterId());
            info002Dto.setCounterTrader(entity.getCounterTrader());
            info002Dto.setContactInfo(entity.getCounterContact());
            info002Dto.setIsOutTrade(entity.getIsOutTrade());
            entity.setTrdInvestPersonInfo(info002Dto);
        }
        entity.setRevokeReason(dto.getRevokeReason());
        String msg = TrdProjectInvestTrade002Service.start(processParameter, entity);
        return new AjaxResult(msg);
    }

    /**
     * 撤销check
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("revokeCheck")
    public AjaxResult revokeCheck(@Validated({ValidateGroupDelete.class}) TrdProjectInvestTrade002Dto dto,
                                  BindingResult result) {
        // TISHKBS-1349 债券 进行买入交易后 隔天做卖出交易 卖出交易完成后对买入交易进行撤销,此时不提示存在后续交易 增加提示 add by caojin 20201001 start
        if (StringUtil.equals(dto.getOperateType(), "revoke")) {
            if (trdCommonBaseService.isAlreadyRevoked(dto.getBizCode())) {
                ValidMessage validMessage = new ValidMessage(getMessage("TRD_W_0001",
                        getMessage("pingan.common.revoke"), getMessage("pingan.common.revoke")));
                ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
                errorList.add(validMessage);
                AjaxResult ar = new AjaxResult();
                ar.setErrorList(errorList);
                return ar;
            }
        }
        // 确认提醒 TISHKBS-1113【组合资金账户头寸查询】内转交易撤销提醒 add by hpp 2020-09-17 start
        if (result.getErrorCount() == 0) {
            AjaxResult ajaxResult = new AjaxResult();
            String confirmMsg = "";
            String changeLineMark = "<br/>";

            //根据交易编号查询另一笔未撤销的转仓交易信息
            String tradeId = dto.getBizCode();
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
        // 确认提醒 TISHKBS-1113【组合资金账户头寸查询】内转交易撤销提醒 add by hpp 2020-09-17 end

        int T02sum = trdBondInvestTrade002Service.selectTradeT02(dto.getBizCode());
        if(T02sum > 0){
            ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
            errorList.add(new ValidMessage(this.getMessage("后续存在有效的卖出交易！")));
            AjaxResult ar = new AjaxResult();
            ar.setErrorList(errorList);
            return ar;
        }
        // TISHKBS-1349 债券 进行买入交易后 隔天做卖出交易 卖出交易完成后对买入交易进行撤销,此时不提示存在后续交易 增加提示 add by caojin 20201001 end


        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 分页查询 市场代码
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("getFinprodMarketId")
    public Page<SelectOptionDto> getFinprodMarketId() {
        return TrdProjectInvestTrade001Service.getFinprodMarketId();
    }

    /**
     * 分页查询 委托代码
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("findEntrustId")
    public Page<SelectOptionDto> findEntrustId() {
        return TrdProjectInvestTrade001Service.findEntrustId();
    }

    /**
     * 分页查询 有效、撤销或待处理的债券存续期交易数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<TrdProjectInvestTrade001Dto> findPage() {
        return TrdProjectInvestTrade001Service.findPage(RequestUtil.getPageParam());
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
        map.put("finProdType", Arrays.asList(DictCodeListExt1.金融产品类型_投资组合.getValue()));
        map.put("ENTRUST_STATUS_LIST", Arrays.asList(DictCodeList.委托状态_已接收.getValue(),
                DictCodeList.委托状态_已完成.getValue(), DictCodeList.委托状态_已关闭.getValue()));
        return TrdProjectInvestTrade001Service.getEntrustIdByType(map);
    }
}
