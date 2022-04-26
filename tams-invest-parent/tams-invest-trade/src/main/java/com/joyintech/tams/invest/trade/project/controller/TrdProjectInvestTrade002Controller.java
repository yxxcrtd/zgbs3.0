package com.joyintech.tams.invest.trade.project.controller;

import static com.joyintech.tams.invest.trade.project.service.TrdProjectInvestTrade002Service.genBizCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.trade.common.aop.TradeRdsCommonAop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductTypeKey;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.project.BussParticipantInfoService;
import com.joyintech.tams.component.business.project.dto.BussParticipantInfoDto;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.trade.common.aop.TradeRdsCommonAop;
import com.joyintech.tams.invest.trade.common.controller.TrdCommonController;
import com.joyintech.tams.invest.trade.common.dto.EtrInvestInfoDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.project.dto.TrdProjectInvestTrade002Dto;
import com.joyintech.tams.invest.trade.project.service.TrdProjectInvestTrade002Service;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.systemtools.util.PageElementUtil;
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
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.invest.trade.project.service.TrdProjectInvestTrade002Service.genBizCode;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.period.trdbondduration.controller
 * @Description: 项目类资产 存续期确认
 * @date Date : 2018年09月21日 13:48
 */
@Controller
@PageId("TrdProjectInvestTrade002")
@RequestMapping("/invest/trade/project/TrdProjectInvestTrade002")
public class TrdProjectInvestTrade002Controller extends BaseController {

    @Autowired
    private BussParticipantInfoService bussParticipantInfoService;

    @Autowired
    private TrdProjectInvestTrade002Service trdProjectInvestTrade002Service;

    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;

    @Autowired
    private TradeCommonService tradeCommonService;

    @Autowired
    private IPlatformDate platformDateImpl;

    /**
     * 基础Mapper代理Service
     */
    @Autowired
     private  BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired(required = false)
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;
    
    @Autowired
    private TrdCommonController trdCommonController;

    /**
     * 需要初始化的下拉菜单
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.交易流程状态, DictCode.清算速度,
            // 项目类资产五级分类改造 add by renping 2019-08-01 start
            DictCode.行内资产二级分类, DictCode.行内资产三级分类, DictCode.行内资产四级分类,
            // 项目类资产五级分类改造 add by renping 2019-08-01 end
            DictCode.币种, DictCode.交易方向, DictCode.项目类一级分类,DictCode.是否,DictCode.投资目的};

    @Override
    public String init(Model model) {
        TrdProjectInvestTrade002Dto entity = new TrdProjectInvestTrade002Dto();
        String statusValid = RequestUtil.getParam("statusValid");
//        if (DictCodeListExt1.画面数据状态_待处理.getValue().equals(statusValid)) {
//            entity = setTrdProjectInvestTrade002Dto(model);
//        }
        if ((!WorkFlowUtil.isNotFromToDoList() || DictCodeListExt1.交易流程状态_流程中.getValue().equals(statusValid))
            && StringUtil.isNotEmpty(RequestUtil.getParam("workflowId"))) {
            String workflowId = RequestUtil.getParam("workflowId");
            entity = trdProjectInvestTrade002Service.getBussinessEntityByWorkflowId(workflowId);
            if (entity.getRiskTrialRetDto() != null) {
                model.addAttribute("riskTrialRetDto", entity.getRiskTrialRetDto());
            }
            model.addAttribute("workflowId",workflowId);
            model.addAttribute("tradeId", workflowId);
        }
        else if (!GlobalConstant.DETAIL_PAGE_MODEL_NEW.equals(getPageModel())) {
            entity = setTrdProjectInvestTrade002Dto(model);
        }
        else {
            String entrustId = getParam("entrustId");
            if (StringUtil.isNotEmpty(entrustId)) {
                // 委托的场合
                EtrInvestInfoDto etrInvestInfoDto = tradeCommonService.findEtrInfo(entrustId);
                if (etrInvestInfoDto != null) {
                    entity = genTradeDto(etrInvestInfoDto);
                    TrdInvestPersonInfo002Dto info002Dto = new TrdInvestPersonInfo002Dto();
                    info002Dto.setCounterId(etrInvestInfoDto.getEtrEntrust().getCounterId());
                    info002Dto.setCounterTrader(etrInvestInfoDto.getEtrEntrust().getCounterTrader());
                    info002Dto.setContactInfo(etrInvestInfoDto.getEtrEntrust().getContactInfo());
                    info002Dto.setIsOutTrade(etrInvestInfoDto.getEtrEntrust().getIsOutTrade());
                    info002Dto.setTrader(LoginUserUtil.getUserName());
                    info002Dto.setTraderDept(LoginUserUtil.getDeptCode());
                    entity.setTrdInvestPersonInfo(info002Dto);
                    if(StringUtil.isNotBlank(entity.getRemark())&&StringUtil.contains(entity.getRemark(),"退回原因")){
                        entity.setRemark("");
                    }
                }
                model.addAttribute("tradeId", entrustId);
            }
            String sysDate = platformDateImpl.getSysDateStr();
            entity.setTradeDate(sysDate);
            entity.setSettleDate(sysDate);
            entity.setUnitCprice(new BigDecimal(100));
        }
        if (entity.getTrdInvestPersonInfo() != null) {
            model.addAttribute("trdInvestPersonInfo002Dto", JsonUtils.object2Json(entity.getTrdInvestPersonInfo()));
        }
        if (entity.getBussTrdTransferOrder() != null && !entity.getBussTrdTransferOrder().isEmpty()) {
            model.addAttribute("entity_bussTrdTransferOrderDto", JsonUtils.object2Json(entity.getBussTrdTransferOrder().get(0)));
        }

        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        model.addAttribute("T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("T02", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("T40", DictCodeList.交易方向_卖出到账.getValue());
        model.addAttribute("F18", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        model.addAttribute("SETTLE_SPEED01", DictCodeList.清算速度_T_0.getValue());
        model.addAttribute("SETTLE_SPEED02", DictCodeList.清算速度_T_1.getValue());
        model.addAttribute("SETTLE_SPEED03", DictCodeList.清算速度_T_2.getValue());
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("FINPROD_TYPE_F18", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        model.addAttribute("collectType_01", DictCodeListExt2.收取方式_回款前扣除.getValue());
        this.setSelectOptionToModel(model, DictCode.业务模式);
        setSelectOptionToModel(model, DICT_CODES);
        model.addAttribute(GlobalConstant.ENTITY, entity);
        
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        //add by lizhi 是否限制划款指令开关 2020年2月26日  start
        model.addAttribute("GEN_PAY_ORDERS_AUTO",CacheUtil.getSysParam(BussinessConstant.GEN_PAY_ORDERS_AUTO));
        //add by lizhi 是否限制划款指令开关 2020年2月26日  end
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数

        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);
        //查看交易方向添加清理核销 add by yzg 20201102 start
        if(StringUtil.equals(this.getParam("pageModel"), GlobalConstant.DETAIL_PAGE_MODEL_REFERENCE)) {
        	SelectDto selectDto = getSelectDataFromDataDic(DictCode.交易方向,null);
        	SelectOptionDto selectOptionDto = new SelectOptionDto();
        	selectOptionDto.setId(DictCodeListExt3.交易方向_清理核销.getValue());
        	selectOptionDto.setText(DictCodeListExt3.交易方向_清理核销.getCodeName());
        	selectOptionDto.setCol(DictCode.交易方向.getValue());
        	selectDto.getOptions().add(selectOptionDto);
        	model.addAttribute(DictCode.交易方向.getValue(), JsonUtils.objectArray2Json(selectDto.getOptions().toArray()));
        }
        //查看交易方向添加个清理核销 add by yzg 20201102 end
        return super.init(model);
    }

    private TrdProjectInvestTrade002Dto setTrdProjectInvestTrade002Dto(Model model) {
        TrdProjectInvestTrade002Dto entity;
        entity = trdProjectInvestTrade002Service.findEffectiveData(RequestUtil.getPageParam());
        TrdInvestPersonInfo002Dto info002Dto = new TrdInvestPersonInfo002Dto();
        info002Dto.setTrader(entity.getTrader());
        info002Dto.setCounterId(entity.getCounterId());
        info002Dto.setCounterTrader(entity.getCounterTrader());
        info002Dto.setContactInfo(entity.getCounterContact());
        info002Dto.setIsOutTrade(entity.getIsOutTrade());
        entity.setTrdInvestPersonInfo(info002Dto);
        model.addAttribute("tradeId", entity.getBizCode());
        //转仓交易目的 显示为转仓交易的 交易目的
        if(entity!=null){
            String turnInvAim = trdProjectInvestTrade002Service.getReverseTradeDtoInvAim(entity.getBizCode());
            if(StringUtil.isNotEmpty(turnInvAim)) {
                entity.setTurnInvAim(turnInvAim);
            }
        }
        return entity;
    }

    private TrdProjectInvestTrade002Dto genTradeDto(EtrInvestInfoDto etrInvestInfoDto) {
        TrdProjectInvestTrade002Dto entity = new TrdProjectInvestTrade002Dto();
        if (etrInvestInfoDto.getEtrEntrust() != null) {
            FinProduct finProduct = null;
            FinProductType finProductType = null;
            BeanUtil.copyPropertiesIgnoreNull(entity, etrInvestInfoDto.getEtrEntrust());
            String finprodId = etrInvestInfoDto.getEtrEntrust().getFinprodId();
            entity.setTradeAmt(etrInvestInfoDto.getEtrEntrust().getEntrustAmt());
            if(etrInvestInfoDto.getEtrFinishStatusList()!=null){
            	BigDecimal finish = BigDecimal.ZERO;
            	for(EtrFinishStatus fin:etrInvestInfoDto.getEtrFinishStatusList()){
            		finish= ArithmeticUtil.doAdd(finish,fin.getFinishAmt());
            	}
            	entity.setPrinAmt(ArithmeticUtil.subtract(etrInvestInfoDto.getEtrEntrust().getEntrustAmt(),finish));
            }else{
            	entity.setPrinAmt(etrInvestInfoDto.getEtrEntrust().getEntrustAmt());
            }
            if (com.joyintech.framework.util.StringUtil.isNotEmpty(finprodId) && bussCommonMapperDelegateService != null) {
                finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, finprodId,
                    FinProduct.class);
                FinProductTypeKey key = new FinProductTypeKey();
                key.setFinprodId(finprodId);
                key.setBranch(0l);
                finProductType = bussCommonMapperDelegateService.selectByMultiPrimaryKey(FinProductTypeMapper.class, key,
                    FinProductType.class);
            }
            if (finProduct != null) {
                entity.setFinprodMarketId(finProduct.getFinprodMarketId());
                entity.setFinprodName(finProduct.getFinprodName());
            }
            if (finProductType != null) {
                entity.setProjectType(finProductType.getType6());
                // 项目类资产五级分类改造 add by renping 2019-08-01 start
                entity.setType9(finProductType.getType9());
                entity.setType10(finProductType.getType10());
                entity.setType11(finProductType.getType11());
                // 项目类资产五级分类改造 add by renping 2019-08-01 end
            }
        }
        if (etrInvestInfoDto.getEtrEntrustAdd() != null && com.joyintech.framework.util.StringUtil.isNotEmpty(etrInvestInfoDto.getEtrEntrustAdd().getSettleSpreed())) {
            entity.setSettleSpeed("0" + (etrInvestInfoDto.getEtrEntrustAdd().getSettleSpreed() + 1));
        }
        //查询 委托表里存的业务模式字段
        entity.setBusinessModel(tradeCommonService.findEtrWordInfo(etrInvestInfoDto.getEtrEntrust().getEntrustId()));
        return entity;
    }

    /**
     * 画面点击保存按钮时check
     *
     * @param dto 实体类
     * @param result
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) TrdProjectInvestTrade002Dto dto,
                            BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        //huwenbin 指令接收交易录单验证数据已存在流程中
      /*  if(StringUtil.isNotEmpty(dto.getEntrustId()) && WorkFlowUtil.isNotFromToDoList()){
    		String str = tradeCommonService.check(dto.getEntrustId(), this.getPageId());
    		if(StringUtil.isNotEmpty(str)){
    			AjaxResult res= new AjaxResult();
				res.setMessage(str, MessageType.错误消息);
    			return res;
    		}
        	// 查询交易流程中录入的总金额	王贵奇	2019/9/3
        	BigDecimal entrustNum = tradeCommonService.getEntrustAmtByEntrustId(dto.getEntrustId());
            BigDecimal wflPrinAmt = tradeCommonService.getWflPrinAmt(dto.getEntrustId(), dto.getPortfolioId());
            if(entrustNum.compareTo(wflPrinAmt.add(dto.getPrinAmt())) == -1) {
            	AjaxResult res= new AjaxResult();
            	List<ValidMessage> errorList = new ArrayList<>();
           	 ValidMessage validMessage = new ValidMessage("shareAmt",
           			 "可用委托金额不足,当前最多可委托金额为:"+ entrustNum.subtract(wflPrinAmt));
                    errorList.add(validMessage);
               res.setErrorList(errorList);
               res.setMessageType(MessageType.错误消息);
               return res;
            }
    	}*/
        
        // 确认提醒
        AjaxResult ajaxResult = trdProjectInvestTrade002Service.checkConfirmInfo(dto, result);
        
        // 移动到trdProjectInvestTrade002Service.checkConfirmInfo内
//        // 可用现金软校验
//        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
//            && dto.getBussTrdAvailableCashQuery() != null) {
//           if (dto.getPrinAmt().compareTo(dto.getBussTrdAvailableCashQuery().getAvalibleCash()) == 1) {
//               AjaxResult ar = new AjaxResult();
//               ar.setData(this.getMessage("TRD_C_0002"));
//               return ar;
//           }
//        }

        if (ajaxResult != null) {
            return ajaxResult;
        }
        
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 查询净投资收益率
     * 
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("findInvestRate")
    public Map<String, Object> findInvestRate() {
        return trdProjectInvestTrade002Service.findInvestRate(RequestUtil.getPageParam());
    }

    /**
     * 根据资产编号查询通道信息
     * @return
     */
    @ResponseBody
    @RequestMapping("getChlIdByFinProdId")
    public List<SelectOptionDto> getChlIdByFinProdId(){
        List<BussParticipantInfoDto> bussParticipantInfoList = new ArrayList<BussParticipantInfoDto>();
        String finprodId = this.getParam("finprodId");
        List<SelectOptionDto> pageList = new ArrayList<>();
        if (StringUtil.isNotEmpty(finprodId)) {
            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("finprodId", finprodId);
            paramMap.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_通道.getValue());
            paramMap.put("isChl", DictCodeList.是否_是.getValue());
            paramMap.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
            paramMap.put("cashType02c11", DictCodeListExt1.现金流二级类型_通道托管费.getValue());
            paramMap.put("cashType02c12", DictCodeListExt1.现金流二级类型_通道管理费.getValue());
            paramMap.put("cashType02c13", DictCodeListExt1.现金流二级类型_通道其他费.getValue());
            bussParticipantInfoList =  bussParticipantInfoService.queryBussParticipantInfo(paramMap);
        }
        if (bussParticipantInfoList.size() == 0){
            return pageList;
        }
        bussParticipantInfoList.forEach(item -> {
            SelectOptionDto opt = new SelectOptionDto();
            opt.setId(item.getCounterId());
            opt.setText(item.getCounterName());
            pageList.add(opt);
        });
        return pageList;
    }

    /**
     * 保存
     *
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) TrdProjectInvestTrade002Dto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        
        
        
        ProcessParameter<TrdProjectInvestTrade002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdProjectInvestTrade002Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        if (StringUtil.isEmpty(dto.getBizCode())) {
            BussinessCodeParameter parameter = genBizCode(dto);
            dto.setBizCode(baseGetBusinessIdImpl.buildBussinessCode(parameter));//汇总主键
            dto.setTradeId(baseGetBusinessIdImpl.buildBussinessCode(parameter));//交易编号
        }
        //----------add by jjpei 20200114 核风险版本 start----------
        if(dto.getOperateType().equals(BussinessConstant.SUBMIT)&& CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)){
            AjaxResult ar=interfRiskRdsService.checkRiskRds(new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
                    BussinessDictCodeListRds.接口系统ID_实占接口.getValue(),DictCodeList.平安资产类型_项目类资产.getValue(),
                    (en)->interfRiskRdsService.sendRiskRdsData(dto,en));
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
            // add by zhangrui1 start
            // 将实占信息设置到线程缓存出错时撤销用
            TradeRdsCommonAop.setLocalCache(dto);
            // add by zhangrui1 end
        }
        //----------add by jjpei 20200114 核风险版本 end----------
        params.setBusinessCode(dto.getBizCode());
        params.setBusinessType(DictCodeList.共通业务类型_项目类资产交易.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("交易编号：" + dto.getBizCode(), "资产名称：" + entity.getFinprodName(),
            "交易日：" + entity.getTradeDate()));
        processParameter.setWorkFlowParameter(params);
        if (BussinessConstant.SUBMIT.equals(dto.getOperateType())) {
            dto.setBaseTradeInfoDto(trdProjectInvestTrade002Service.genFinProductTradeDto(dto));
        }
        dto.setCounterId(dto.getTrdInvestPersonInfo().getCounterId());//交易对手
        String msg = trdProjectInvestTrade002Service.start(processParameter, dto);
        
        //授信占用额度
//        trdCommonController.delLinesDeal(dto.getRiskCreditListCommonDto(),dto.getBizCode());
        return new AjaxResult(msg);
    }

    /**
     * 获取累计未付还本应计利息
     * @param map 请求参数
     * @return 累计未付还本应计利息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getUnpayIntAmt")
    public AjaxResult getUnpayIntAmt(@RequestBody Map<String,Object> map){
         String intAmt = trdProjectInvestTrade002Service.getUnpayIntAmt(map);
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setData(intAmt);
        return ajaxResult;
    }

    @ResponseBody
    @RequestMapping("findRepayFlag")
    public Map<String, Object> findRepayFlag(@RequestBody Map<String,Object> param) {
        return trdProjectInvestTrade002Service.findRepayFlag(param);
    }
}
