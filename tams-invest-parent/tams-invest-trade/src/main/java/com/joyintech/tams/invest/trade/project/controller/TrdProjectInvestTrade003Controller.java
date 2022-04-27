package com.joyintech.tams.invest.trade.project.controller;

import static com.joyintech.tams.invest.trade.project.service.TrdProjectInvestTrade002Service.genBizCode;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.*;

import com.joyintech.tams.invest.common.constant.BussinessDictCode;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.component.business.project.BussParticipantInfoService;
import com.joyintech.tams.component.business.project.dto.BussParticipantInfoDto;
import com.joyintech.tams.component.business.riskrds.constant.InterfUtil;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.trade.bond.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.trade.common.dto.EtrInvestInfoDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.project.dto.TrdProjectInvestTrade002Dto;
import com.joyintech.tams.invest.trade.project.service.TrdProjectInvestTrade002Service;  
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil; 

/**
 * @author : wc
 * @version 2.1.2
 * @Project: tams-invest-parent
 * @Package com.joyintech.tams.invest.trade.project.controller
 * @Description: 项目类资产多组合交易新建
 * @date Date : 2019年12月24日 14:18
 */
@Controller
@PageId("TrdProjectInvestTrade003")
@RequestMapping("/invest/trade/project/TrdProjectInvestTrade003")
public class TrdProjectInvestTrade003Controller extends BaseController {

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
    
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 基础Mapper代理Service
     */
    @Autowired
     private  BussCommonMapperDelegateService bussCommonMapperDelegateService;
    
    /**
     * 组合可用现金和实际现金Service
     */
    @Autowired(required = false)
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;
    
    @Autowired(required = false)
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;
    
    /**
     * 需要初始化的下拉菜单
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.交易流程状态, DictCode.清算速度,
            // 项目类资产五级分类改造 add by renping 2019-08-01 start
            DictCode.行内资产二级分类, DictCode.行内资产三级分类, DictCode.行内资产四级分类,
            // 项目类资产五级分类改造 add by renping 2019-08-01 end
            DictCode.币种, DictCode.交易方向, DictCode.项目类一级分类,DictCode.分配方式,
            DictCode.投资目的};

    @Override
    public String init(Model model) {
        TrdProjectInvestTrade002Dto entity = new TrdProjectInvestTrade002Dto();
        String statusValid = RequestUtil.getParam("statusValid");
        if (DictCodeListExt1.画面数据状态_待处理.getValue().equals(statusValid)) {
            entity = setTrdProjectInvestTrade002Dto(model);
        }
        else if ((!WorkFlowUtil.isNotFromToDoList() || DictCodeListExt1.交易流程状态_流程中.getValue().equals(statusValid))
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
                    entity.setEntrustId(entrustId);
                }
                model.addAttribute("tradeId", entrustId);
            }
            String sysDate = platformDateImpl.getSysDateStr();
            entity.setTradeDate(sysDate);
            entity.setSettleDate(sysDate);
            entity.setUnitCprice(new BigDecimal(100));
            entity.setPrinAmt(null);
        }
        if (entity.getTrdInvestPersonInfo() != null) {
            model.addAttribute("trdInvestPersonInfo002Dto", JsonUtils.object2Json(entity.getTrdInvestPersonInfo()));
        }
        if (entity.getBussTrdTransferOrder() != null && !entity.getBussTrdTransferOrder().isEmpty()) {
            model.addAttribute("entity_bussTrdTransferOrderDto", JsonUtils.object2Json(entity.getBussTrdTransferOrder().get(0)));
        }

        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
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
        model.addAttribute("ACCT_TYPE_A01", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        model.addAttribute("ACCT_TYPE_A07", DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        this.setSelectOptionToModel(model, DictCode.业务模式);
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数
        setSelectOptionToModel(model, DICT_CODES);
        model.addAttribute(GlobalConstant.ENTITY, entity);
        
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        //add by lizhi 是否限制划款指令开关 2020年2月26日  start
        model.addAttribute("GEN_PAY_ORDERS_AUTO",CacheUtil.getSysParam(BussinessConstant.GEN_PAY_ORDERS_AUTO));
        //add by lizhi 是否限制划款指令开关 2020年2月26日  end
      
        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);
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
        return entity;
    }
    
    @ResponseBody
    @RequestMapping("/selectIndex")
    public String selectIndex() {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_项目类资产交易);
        String index=baseGetBusinessId.buildBussinessCode(codeParameter);
        return StringUtil.trimHtmlTag(index);
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
        
        if(null != dto && null != dto.getBussTrdTransfenpeiDto() && dto.getBussTrdTransfenpeiDto().size() > 0){
        	BigDecimal total = BigDecimal.ZERO;
        	for(BussTrdTransfenpeiDto fenpei : dto.getBussTrdTransfenpeiDto()) {
                total = total.add(fenpei.getPrinAmt());

                String confirmMsg = "";
                String changeLineMark = "<br/>";
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
                    //交易金额check
                    if (fenpei.getTradeAmt() != null && fenpei.getBussTrdAvailableCashQueryavalibleCash() != null
                            && StringUtil.isEmpty(dto.getWorkflowId())
                            && fenpei.getTradeAmt().compareTo(fenpei.getBussTrdAvailableCashQueryavalibleCash()) == 1) {
                        if (StringUtil.isNotBlank(confirmMsg)) {
                            confirmMsg += changeLineMark;
                        }
                        confirmMsg += this.getMessage("TRD_C_0002");
                    }
                }
                if (StringUtil.isNotBlank(confirmMsg)) {
                    AjaxResult wr = new AjaxResult();
                    wr.setData(confirmMsg);
                    return wr;
                }
            }
        	if(total.compareTo(dto.getPrinAmt()) != 0){
                    AjaxResult res= new AjaxResult();
                    res.setMessage("金额信息中的本金金额不等于各组合的本金金额之和！", MessageType.错误消息);
                    return res;
        	}
        }
        // 确认提醒
        AjaxResult ajaxResult = trdProjectInvestTrade002Service.checkConfirmInfo(dto, result);
        
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
	 * 保存提交
	 *
	 * @param dto
	 * @return 画面输入参数
	 * @throws Exception
	 *             异常
	 * @see
	 * @since 1.0
	 */
	@RequestMapping("save")
	@ResponseBody
	AjaxResult save(@RequestBody TrdProjectInvestTrade002Dto dto) throws InvocationTargetException, IllegalAccessException, IOException, InstantiationException, WorkFlowException {
		List<TrdProjectInvestTrade002Dto> dtoList = new ArrayList<>();
		List<BussTrdTransfenpeiDto> fenpeiList = dto.getBussTrdTransfenpeiDto();// 组合分配
		List<BussTrdTransferOrderDto> orderList = dto.getBussTrdTransferOrder();// 划款指令
		for (int i = 0; i < fenpeiList.size(); i++) {
			TrdProjectInvestTrade002Dto tempDto = new TrdProjectInvestTrade002Dto();
			BeanUtil.copyProperties(tempDto, dto);
			List<BussTrdTransferOrderDto> tmpOrderList = new ArrayList<BussTrdTransferOrderDto>();
			if (!(fenpeiList.get(i).getPrinAmt().compareTo(BigDecimal.ZERO) == 1)) {
				continue;
			}
			tmpOrderList.add(orderList.get(i));
			tempDto.setBussTrdTransferOrder(tmpOrderList);
			tempDto.setEntrustId(fenpeiList.get(i).getEntrustId());// 委托代码
			tempDto.setPortfolioId(fenpeiList.get(i).getPortfolioId());// 组合代码
			tempDto.setPrinAmt(fenpeiList.get(i).getPrinAmt());// 本金金额
			tempDto.setCpriceAmt(fenpeiList.get(i).getCpriceAmt());// 净价总额
			tempDto.setIntAmt(fenpeiList.get(i).getIntAmt());// 应计利息总额
			tempDto.setChannelFee(fenpeiList.get(i).getChannelFee());// 通道费
			tempDto.setTradeAmt(fenpeiList.get(i).getTradeAmt());// 实付金额
			tempDto.setCommissionAmt(fenpeiList.get(i).getCommissionAmt());// 手续费
			if (StringUtil.isEmpty(tempDto.getBizCode())) {
				BussinessCodeParameter parameter = genBizCode(dto);
				tempDto.setBizCode(baseGetBusinessIdImpl.buildBussinessCode(parameter));// 汇总主键
				tempDto.setTradeId(baseGetBusinessIdImpl.buildBussinessCode(parameter));// 交易编号
			}
			tempDto.setRiskTrialRetDto(null);
            //多组合交易对风险试算处理逻辑
            if(CollectionUtils.isNotEmpty(dto.getRiskTrialRetDto())){
                 List<RiskTrialRetDto> riskTrialRetlist = new ArrayList<>();
                 for (RiskTrialRetDto riskTrialRetDto:dto.getRiskTrialRetDto()) {
                    if(StringUtils.equals(riskTrialRetDto.getPortfolioCode(),fenpeiList.get(i).getPortfolioId())){
                        riskTrialRetlist.add(riskTrialRetDto); 
                    }
                    List<String> colist= interfRiskRdsService.queryPortfolioPackageDtlByBagId(riskTrialRetDto.getPortfolioCode());
                    if (!CollectionUtils.isEmpty(colist) && colist.contains(fenpeiList.get(i).getPortfolioId())) {
                        riskTrialRetlist.add(riskTrialRetDto); 
                    } 
                 }  
                 tempDto.setRiskTrialRetDto(riskTrialRetlist);
            }
			dtoList.add(tempDto);
		} 
		//----------add by jjpei 20200114 核风险版本 start----------
        if(dto.getOperateType().equals(BussinessConstant.SUBMIT)&& CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)){
            AjaxResult ar =interfRiskRdsService.checkBatchRiskRds(dtoList,new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
                    BussinessDictCodeListRds.接口系统ID_实占接口.getValue(), BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(),DictCodeList.平安资产类型_项目类资产.getValue(),
                    (sm,en)->interfRiskRdsService.sendRiskRdsData(sm,en));
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        //----------add by jjpei 20200114 核风险版本 end----------
        
		ProcessParameter<TrdProjectInvestTrade002Dto> processParameter = new ProcessParameter<>();
		WorkFlowParameter<TrdProjectInvestTrade002Dto> params = new WorkFlowParameter<>();
		params.setPageId("TrdProjectInvestTrade002");
		params.setElementId(this.getParam("elementId"));
		params.setBusinessCode((entity) -> entity.getBizCode());
		params.setBusinessType(DictCodeList.共通业务类型_项目类资产交易.getValue());
		params.setComment(entity -> WorkFlowUtil.concatComment("交易编号：" + entity.getBizCode(),
				"资产名称：" + entity.getFinprodName(), "交易日：" + entity.getTradeDate()));
		processParameter.setWorkFlowParameter(params);
		if (BussinessConstant.SUBMIT.equals(dto.getOperateType())) {
			for (TrdProjectInvestTrade002Dto trdProjectInvestTrade002Dto : dtoList) {
				trdProjectInvestTrade002Dto.setBaseTradeInfoDto(
						trdProjectInvestTrade002Service.genFinProductTradeDto(trdProjectInvestTrade002Dto));
				trdProjectInvestTrade002Dto.setCounterId(dto.getTrdInvestPersonInfo().getCounterId());// 交易对手
			}
		}
		String msg = trdProjectInvestTrade002Service.start(processParameter,
				dtoList.toArray(new TrdProjectInvestTrade002Dto[] {}));
		return new AjaxResult(msg);
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
    public List<BaseEtrInvestInfoDto> getEntrustInfos(@RequestBody TrdProjectInvestTrade002Dto dto) {
    	return trdProjectInvestTrade002Service.getEntrustInfos(dto.getEntrustId());
    }
    
}
