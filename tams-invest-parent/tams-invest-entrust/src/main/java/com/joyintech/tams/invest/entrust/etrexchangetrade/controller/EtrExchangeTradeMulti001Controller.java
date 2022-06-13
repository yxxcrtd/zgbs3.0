/*
 * 文件名：EtrTradePlace001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrexchangetrade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.*;
import com.joyintech.tams.invest.entrust.etrexchangetrade.service.EtrExchangeTrade001Service;
import com.joyintech.tams.invest.entrust.etrexchangetrade.service.EtrExchangeTradeMulti001Service;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 * @author chendijun
 * @version 1.0 2020年7月1日
 * @see EtrExchangeTradeMulti001Controller
 * @since 1.0
 */
@Controller
@PageId("EtrExchangeTradeMulti001")
@RequestMapping("/invest/entrust/etrexchangetrade/EtrExchangeTradeMulti001")
public class EtrExchangeTradeMulti001Controller extends BaseController{

	/**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;

    @Autowired
    private EtrExchangeTrade001Service etrExchangeTrade001Service;

    @Autowired
    private EtrExchangeTradeMulti001Service etrExchangeTradeMulti001Service;

    @Autowired
    private IPlatformDate iPlatformDate;

	public static final String ENTRUST_RECORD_NEW = "new";

	public static final String ENTRUST_RECORD_MODIFY = "modify";

    @Value("${quote.flag:false}")
    private boolean useInterfQuoteFlag;

    @Value("${quote.ws.host}")
    private String wsHost;

    @Value("${quote.ws.url}")
    private String wsUrl;
    
    @Override
    public String init(Model model) {
    	this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.投资目的, DictCode.交易场所, DictCode.目标类型, DictCode.申报类型, DictCode.业务模式,DictCode
        .场内多组合分配方式, DictCode.权重方式, DictCode.目标值单位);
        etrExchangeTrade001Service.setPsSelectOptions(model,this.getSelectDataFromDataDic(DictCode.交易方向, null),true);
        etrExchangeTrade001Service.setPriceModelSelectOptions(model,this.getSelectDataFromDataDic(DictCode.价格模式, null));
        etrExchangeTrade001Service.setDeclaTypeSelectOptions(model,this.getSelectDataFromDataDic(DictCode.申报类型, null));
        //清算速度
        etrExchangeTrade001Service.setSettleSpreedSelectOptions(model,getSelectDataFromDataDic(DictCode.清算速度, null));
        // 设置固定的数据字典明细参数
        etrExchangeTrade001Service.setConstSelectOption(model);


    	Date platformDate = DateUtil.platDateFromCache();
        //流程id
        String workflowId = "null".equals(getParam(GlobalConstant.WORKFLOW_ID))?null:getParam(GlobalConstant.WORKFLOW_ID);
        //委托代码
        String entrustId = "null".equals(getParam("entrustId"))?null:getParam("entrustId");

        String isFromToDoList = getParam("isFromToDoList");
        EtrExchangeTradeMulti001Dto entity = new EtrExchangeTradeMulti001Dto();
        if (StringUtil.isNotEmpty(workflowId)) {
        	 //流程代办checkImportData
            entity = etrExchangeTradeMulti001Service.getBussinessEntityByWorkflowId(workflowId);
            //如果是单组合的流程数据则，多组合置空
            if(StringUtil.isNotEmpty(entity) && !StringUtil.equals(entity.getIsMulti(),DictCodeList.是否_是.getValue())) {
                entity = new EtrExchangeTradeMulti001Dto();
            }
            else {
                String[] secIds = StringUtil.split(entity.getFinprodId(),"_");
                if(secIds.length == 2 && StringUtil.isEmpty(entity.getStockId())){
                    entity.setStockId(secIds[1]);
                }
            }

            //add by chenjiaxing 流程中不显示资产名称 end
        }
        else if (StringUtil.isNotEmpty(entrustId) && !"true".equals(isFromToDoList)) {
        	//查看
        	entity = etrExchangeTradeMulti001Service.getEtrExchangeTradeMultiInfo(entrustId);
        }
        else{
            //新建页面初始化
            entity.setEntrustDate(platformDate);
            entity.setEntrustStatus(BussinessDictCodeList.委托状态_新建.getValue());
        }
        // 场内交易指令画面行情区域自动刷新时间（毫秒）
        long etrExchange_HqArea_refresh_period = 20000;
        String tempSysParaVal = CacheUtil.getSysParam( BussinessConstant.ETREXCHANGE_HQAREA_REFRESH_PERIOD);
        if (StringUtil.isNotBlank(tempSysParaVal)
            && !StringUtil.equals(BussinessConstant.ETREXCHANGE_HQAREA_REFRESH_PERIOD,
                tempSysParaVal)) {
            try {
                etrExchange_HqArea_refresh_period = Long.parseLong(tempSysParaVal);
            }
            catch (Exception e) {
            }
        }

        //最小价差List
        model.addAttribute("CONTROL_TYPE_01", BussinessDictCodeListRds.控制类型_大于.getValue());
        model.addAttribute("CONTROL_TYPE_02", BussinessDictCodeListRds.控制类型_大于等于.getValue());
        model.addAttribute("CONTROL_TYPE_03", BussinessDictCodeListRds.控制类型_小于.getValue());
        model.addAttribute("CONTROL_TYPE_04", BussinessDictCodeListRds.控制类型_小于等于.getValue());

        //清算速度
        model.addAttribute("ACCOUNT_SPEED_01", DictCodeList.清算速度_T_0.getValue());
        model.addAttribute("ACCOUNT_SPEED_02", DictCodeList.清算速度_T_1.getValue());
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        model.addAttribute("etrExchange_HqArea_refresh_period", etrExchange_HqArea_refresh_period);

        model.addAttribute("useInterfQuoteFlag", useInterfQuoteFlag);
        model.addAttribute("wsUri", wsHost + wsUrl);
        model.addAttribute("iPlatformDate", DateUtil.format(platformDate));

        model.addAttribute("ENTRUST_RECORD_NEW", ENTRUST_RECORD_NEW);
        model.addAttribute("ENTRUST_RECORD_MODIFY", ENTRUST_RECORD_MODIFY);
        // 根据价格模式确定当前的数据来源
        if(entity != null && StringUtil.equals(entity.getPriceModel(), DictCodeListExt3.申报类型_点击成交申报.getValue())) {
            entity.setDataSource("01");
        }
        model.addAttribute(GlobalConstant.ENTITY, entity);
        if (null != entity && null != entity.getEntrustPrice()) {
            model.addAttribute("BOND_SCALE", entity.getEntrustPrice().scale());
        } else {
            model.addAttribute("BOND_SCALE", 3);
        }
        if(null != entity && null != entity.getCalculateField()) {
            model.addAttribute("calculateField", entity.getCalculateField());
        }

        //配置单位
        // moddify by etliu 20220301 bond_traded_unit系统参数删除，默认张 start
//        model.addAttribute("bond_traded_unit", CacheUtil.getSysParam("bond_traded_unit") );
        model.addAttribute("bond_traded_unit", BussinessConstant.BOND_TRADED_UNIT_DEFAULT);
        // moddify by etliu 20220301 end
        //母页面的pageModel
        model.addAttribute("parentPageModel", getParam(GlobalConstant.DETAIL_PAGE_MODEL_KEY));
        //是否_是
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());

        model.addAttribute("workflowId", workflowId);

        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());

        return super.init(model);
    }

    /**
     * 计算
     * @param dto
     * @return
     */
    @ResponseBody
    @RequestMapping("calc")
    public AjaxResult calc(@RequestBody EtrExchangeTradeMulti001Dto dto) {
        AjaxResult result = new AjaxResult();
        EtrExchangeTradeMulti001Dto exchangeTradeMulti001Dto  = etrExchangeTradeMulti001Service.calc(dto);
        result.setData(exchangeTradeMulti001Dto);
        return result;
    }

    /**
     * 计算
     * @param dto
     * @return
     */
    @ResponseBody
    @RequestMapping("addRow")
    public AjaxResult addRow(@RequestBody EtrExchangeTradeMulti001Dto dto) {
        AjaxResult result = new AjaxResult();
        EtrExchangeTradeMultiPtl001Dto etrExchangeTradeMultiPtl001Dto  = etrExchangeTradeMulti001Service.addRow(dto);
        result.setData(etrExchangeTradeMultiPtl001Dto);
        return result;
    }

    /**
     * 获取多组合
     * @return
     */
    @ResponseBody
    @RequestMapping("selectPortfolioTreeId")
    public Page<SelectOptionDto> selectPortfolioTreeId(){
        return etrExchangeTradeMulti001Service.selectPortfolioTreeId(RequestUtil.getPageParam());
    }

    /**
     * 查询多组合明细列表
     * @return
     */
    @ResponseBody
    @RequestMapping("selectPortfolioTreeDetail")
    public List<EtrExchangeTradeMultiPtl001Dto> selectPortfolioTreeDetail(@RequestParam Map<String, Object> param) {
        return etrExchangeTradeMulti001Service.selectPortfolioTreeDetail(param);
    }

    /**
     * 选择组合代码后查询其他字段信息
     * @param paraMap
     * @return
     */
    @ResponseBody
    @RequestMapping("selectAddPortfolioDetail")
    public EtrExchangeTradeMultiPtl001Dto selectAddPortfolioDetail(@RequestBody Map<String,Object> paraMap){
        List<EtrExchangeTradeMultiPtl001Dto> list = etrExchangeTradeMulti001Service.selectPortfolioTreeDetail(paraMap);
        if(StringUtil.isEmptyList(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 获取多组合分配资金账户
     * @return
     */
    @ResponseBody
    @RequestMapping("selectMultiAcctNo")
    public Page<SelectOptionDto> selectMultiAcctNo(@RequestParam Map<String, Object> param){
        return etrExchangeTradeMulti001Service.selectMultiAcctNo(param);
    }

    /**
     * 查询组合+资金账户+交易场所对应的证券账户（多组合）
     * @param paraMap
     * @return
     */
    @ResponseBody
    @RequestMapping("selectMultiSecAcctId")
    public AjaxResult selectMultiSecAcctId(@RequestBody Map<String,Object> paraMap){
        AjaxResult result = new AjaxResult();
        String secAcctId = etrExchangeTradeMulti001Service.selectMultiSecAcctId(paraMap);
        result.setData(secAcctId);
        return result;
    }


    /**
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("selectPortfolioId")
    public List<SelectOptionDto> selectPortfolioId(@RequestParam Map<String, Object> param){
        return etrExchangeTradeMulti001Service.selectPortfolioId(param);
    }

    /**
     * check
     * @param dto
     * @param result
     * @return
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ ValidateGroupEdit.class }) EtrExchangeTradeMulti001Dto dto,
                            BindingResult result) {
        if (result.hasErrors()) {
            return new AjaxResult(result);
        }

        return commonCheck(result, dto.getOperateType());
    }

    /**
     * check
     * @param dto
     * @return
     */
    @ResponseBody
    @RequestMapping("calcCheck")
    public AjaxResult calcCheck(@RequestBody EtrExchangeTradeMulti001Dto dto) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();

        //ckeck必填字段
        if (!StringUtil.isNotEmpty(dto.getPortfolioTreeId())) {
            errorList.add(new ValidMessage("portfolioTreeId", this.getMessage("ETR_ERROR_0065")));
        }

        if (!StringUtil.isNotEmpty(dto.getStockId())&&!StringUtil.isNotEmpty(dto.getFinprodId())) {
            errorList.add(new ValidMessage("stockId", this.getMessage("ETR_ERROR_0016")));
        }

        if (!StringUtil.isNotEmpty(dto.getPs())) {
            errorList.add(new ValidMessage("ps", this.getMessage("ETR_ERROR_0017")));
        }

//        if (!StringUtil.isNotEmpty(dto.getTargetType())) {
//            errorList.add(new ValidMessage("targetType", this.getMessage("ETR_ERROR_0022")));
//        }

        if (!StringUtil.isNotEmpty(dto.getTargetValue())) {
            errorList.add(new ValidMessage("targetValue", this.getMessage("ETR_ERROR_0066")));
        }

//        if(StringUtil.equals(CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL"),"02")){
            if(!StringUtil.isNotEmpty(dto.getBusinessModel())
                    &&dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
                errorList.add(new ValidMessage("businessModel", "请输入业务模式"));
            }
//        }
//        else{
//            if(!StringUtil.isNotEmpty(dto.getInvAim())
//                    &&dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
//                errorList.add(new ValidMessage("invAim", this.getMessage("ETR_ERROR_0060")));
//            }
//        }

        // 大宗交易增加涨跌幅限制 add by 代志伟 20201015 start
        // 债券交易、债券质押式回购，指令价格需要在前收盘价格的上下30%或当日已成交的最高、最低价格之间（min（最低价（债券五档行情中的最低价），收盘价x70%）<=price<=max（最高价（债券五档行情中的最高价），收盘价x130%））；
        // 股票交易、基金交易，指令价格需要满足涨跌停限制；
        if (dto.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue()) && !DictCodeListExt2.价格模式_市价指令.getValue().equals(dto.getPriceModel())){
            Map<String, Object> map = new HashMap<>();
            map.put("secId",dto.getStockId());
            Map<String, Object> resultMap  = etrExchangeTrade001Service.findQuotations(map);
            if (resultMap != null){
                BigDecimal price = dto.getEntrustPrice();
                if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue())
                        || dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue())){
                    // 涨停
                    BigDecimal limitUp = new BigDecimal(resultMap.get("upperLimitPrice").toString()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    // 跌停
                    BigDecimal limitDown = new BigDecimal(resultMap.get("lowerLimitPrice").toString()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    if (price.compareTo(limitUp) > 0 || price.compareTo(limitDown) < 0){
                        errorList.add(new ValidMessage("price", this.getMessage("ETR_ERROR_0034")));
                    }
                }
                if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                        || (dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue()) || dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue()))){
                    // 昨收盘价
                    BigDecimal preClosePrice = new BigDecimal(resultMap.get("preClosePrice").toString()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    // 最高价
                    BigDecimal highPrice = new BigDecimal(resultMap.get("highPrice").toString()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    // 最低价
                    BigDecimal lowPrice = new BigDecimal(resultMap.get("lowPrice").toString()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    BigDecimal max = highPrice.max(preClosePrice.multiply(new BigDecimal(1.3)));
                    BigDecimal min = lowPrice.min(preClosePrice.multiply(new BigDecimal(0.7)));
                    if (price.compareTo(max) > 0 || price.compareTo(min) < 0){
                        errorList.add(new ValidMessage("price", this.getMessage("ETR_ERROR_0055")));
                    }
                }

            }
        }

        // 大宗交易增加涨跌幅限制 add by 代志伟 20201015 end
        if (StringUtil.isNotEmpty(dto.getFinprodId()) && StringUtil.isNotEmpty(dto.getPs())
                && StringUtil.isNotEmpty(dto.getTradeMarket())) {
            if (StringUtil.isNotEmpty(dto.getEntrustAmt()) && dto.getEntrustAmt().compareTo(BigDecimal.ZERO) > 0
                    && StringUtil.isNotEmpty(dto.getEntrustNum())
                    && dto.getEntrustNum().compareTo(BigDecimal.ZERO) > 0) {
                String finprodType2 = dto.getFinprodType2();
                if (finprodType2.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                        || finprodType2.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                        || finprodType2.equals("ZYSHG")) {
                    finprodType2 = DictCodeList.平安资产类型_质押式回购.getValue();
                }
                MinTrdUnitLimitDto minTrdUnitLimitDto = etrExchangeTrade001Service.getInterfSysMinTrdUnit(
                        dto.getFinprodId(), dto.getTradePlat(), dto.getTradeMarket(), dto.getFinprodType2(),
                        dto.getPs());
                if (null != minTrdUnitLimitDto) {
                    boolean flag1 = false;
                    boolean flag2 = false;
                    if (null == minTrdUnitLimitDto.getMinAmt() || (minTrdUnitLimitDto.getMinAmt() != null
                            && minTrdUnitLimitDto.getMinAmt().compareTo(BigDecimal.ZERO) > 0
                            && dto.getEntrustAmt().compareTo(minTrdUnitLimitDto.getMinAmt()) < 0)) {
                        flag1 = true;
                    }
                    if (null == minTrdUnitLimitDto.getMinNum() || (minTrdUnitLimitDto.getMinNum() != null
                            && minTrdUnitLimitDto.getMinNum().compareTo(BigDecimal.ZERO) > 0
                            && dto.getEntrustNum().compareTo(minTrdUnitLimitDto.getMinNum()) < 0)) {
                        flag2 = true;
                        if (StringUtils.equals(dto.getTradePlat(), DictCodeList.交易平台_竞价撮合平台.getValue())
                                && (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)
                                || DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(finprodType2)
                                || DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(finprodType2))
                                && StringUtils.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                                && etrExchangeTrade001Service.isSalesOfSmallShares(dto.getAvailableNum(),
                                minTrdUnitLimitDto.getMinStep(), dto.getEntrustNum())) {
                            flag2 = false;
                        }
                    }
                    if (flag1 && flag2 && !(null == minTrdUnitLimitDto.getMinNum()
                            && null == minTrdUnitLimitDto.getMinAmt())) {
                        String prompt = "";
                        if (flag1 && null != minTrdUnitLimitDto.getMinNum()) {
                            prompt += "最低交易数量" + etrExchangeTrade001Service.getNumAndUnit(
                                    minTrdUnitLimitDto.getMinNum(), minTrdUnitLimitDto.getMinNumUnit());
                        }
                        if (flag1 && flag2 && flag1 && null != minTrdUnitLimitDto.getMinNum()
                                && null != minTrdUnitLimitDto.getMinAmt()) {
                            prompt += "，";
                        }
                        if (flag2 && null != minTrdUnitLimitDto.getMinAmt()) {
                            prompt += "最低交易金额"
                                    + etrExchangeTrade001Service.getNumAndUnit(minTrdUnitLimitDto.getMinAmt());
                        }
                        if (flag1 && flag2 && flag1 && null != minTrdUnitLimitDto.getMinNum()
                                && null != minTrdUnitLimitDto.getMinAmt()) {
                            prompt += "，应至少满足一个！";
                        }
                        errorList.add(new ValidMessage(prompt));
                    }
                }
            }
        }
        // 如果是债券(交易方向非配债)、check 净价、全价、收益率 不可为空 输入某项,其他两项会计算,如果计算不出来，说明债券不符合
        if (StringUtil.isNotEmpty(dto.getFinprodType2())
                && StringUtils.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                && (!StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_配债.getValue())
                && !StringUtils.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                && !StringUtils.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                && !StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_债转股.getValue())
                && !StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券入库.getValue())
                && !StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券出库.getValue()))) {
            if (!StringUtil.equals(DictCodeListExt2.价格模式_市价指令.getValue(), dto.getPriceModel())) {
                if (null == dto.getPrice()) {
                    errorList.add(new ValidMessage("price", this.getMessage("ETR_ERROR_0028")));
                }
                if (null == dto.getFprice()) {
                    errorList.add(new ValidMessage("fprice", this.getMessage("ETR_ERROR_0029")));
                }
                if (null == dto.getYield()) {
                    errorList.add(new ValidMessage("yield", this.getMessage("ETR_ERROR_0030")));
                }
            }
        }

        //目标值单位为%时，目标值输入值不在0-100之间时
        if(StringUtil.equals(dto.getMulTargetUnit(),DictCodeListExt3.目标值单位_百分比.getValue())) {
            if(BigDecimal.ZERO.compareTo(dto.getTargetValue())>0 || new BigDecimal(100).compareTo(dto.getTargetValue())<0) {
                errorList.add(new ValidMessage("targetValue", this.getMessage("ETR_ERROR_0067")));
            }
        }

        //目标单位为股、张、份时，目标值请输入大于0的整数！
        if(StringUtil.equals(dto.getMulTargetUnit(),DictCodeListExt3.目标值单位_股.getValue())
        || StringUtil.equals(dto.getMulTargetUnit(),DictCodeListExt3.目标值单位_份.getValue())
        || StringUtil.equals(dto.getMulTargetUnit(),DictCodeListExt3.目标值单位_张.getValue())) {
            if(BigDecimal.ZERO.compareTo(dto.getTargetValue())>=0) {
                errorList.add(new ValidMessage("targetValue", this.getMessage("ETR_ERROR_0068")));
            }
        }

        //目标值单位为%时，目标值输入值不在0-100之间时
        if(StringUtil.equals(dto.getMulTargetUnit(),DictCodeListExt3.目标值单位_元.getValue())
        || StringUtil.equals(dto.getMulTargetUnit(),DictCodeListExt3.目标值单位_万元.getValue())) {
            if(BigDecimal.ZERO.compareTo(dto.getTargetValue())>=0) {
                errorList.add(new ValidMessage("targetValue", this.getMessage("ETR_ERROR_0069")));
            }
        }

        //目标类型为总数量或总金额时，权重方式不能为空！
        if((StringUtil.equals(dto.getMulDistriClass(),DictCodeListExt3.场内多组合分配方式_总数量.getValue())
                || StringUtil.equals(dto.getMulDistriClass(),DictCodeListExt3.场内多组合分配方式_总金额.getValue()))
        && !StringUtil.isNotEmpty(dto.getWeightType())) {
            errorList.add(new ValidMessage("weightType", this.getMessage("ETR_ERROR_0070")));
        }

        //组合分配列表的校验
        if(!StringUtil.isEmptyList(dto.getTradeMultiPtl001Dtos())) {
            Map<String,List<EtrExchangeTradeMultiPtl001Dto>> groupMap= dto.getTradeMultiPtl001Dtos().stream().collect(Collectors.groupingBy(item->item.getPortfolioId()+"_"+item.getAcctNo()));
            BigDecimal totalProportion = BigDecimal.ZERO;
            Map<String,String> indexMap = new HashMap<>();
            for(int i = 0;i<dto.getTradeMultiPtl001Dtos().size();i++) {
                EtrExchangeTradeMultiPtl001Dto tradeMultiPtl001Dto = dto.getTradeMultiPtl001Dtos().get(i);
                if(!StringUtil.isNotEmpty(tradeMultiPtl001Dto.getPortfolioId()) || !StringUtil.isNotEmpty(tradeMultiPtl001Dto.getAcctNo())) {
                    errorList.add(new ValidMessage("", "第"+(i+1)+"行：组合分配列表的组合和资金账户不能为空！"));
                }
                else {
                    if(!StringUtil.isEmptyList(groupMap.get(tradeMultiPtl001Dto.getPortfolioId()+"_"+tradeMultiPtl001Dto.getAcctNo()))
                    && groupMap.get(tradeMultiPtl001Dto.getPortfolioId()+"_"+tradeMultiPtl001Dto.getAcctNo()).size()>1) {
                        if(StringUtil.isNotEmpty(indexMap.get(tradeMultiPtl001Dto.getPortfolioId()+"_"+tradeMultiPtl001Dto.getAcctNo()))) {
                            indexMap.put(tradeMultiPtl001Dto.getPortfolioId()+"_"+tradeMultiPtl001Dto.getAcctNo(),indexMap.get(tradeMultiPtl001Dto.getPortfolioId()+"_"+tradeMultiPtl001Dto.getAcctNo())+","+i);
                        }
                        else {
                            indexMap.put(tradeMultiPtl001Dto.getPortfolioId()+"_"+tradeMultiPtl001Dto.getAcctNo(),i+"");
                        }
                    }
                }

                if(StringUtil.equals(dto.getWeightType(),DictCodeListExt3.权重方式_自定义.getValue())) {
                    if(!StringUtil.isNotEmpty(tradeMultiPtl001Dto.getProportion())) {
                        errorList.add(new ValidMessage("", "第"+(i+1)+"行：权重方式为自定义时，分配比例不能为空！"));
                    }
                    else if(BigDecimal.ZERO.compareTo(tradeMultiPtl001Dto.getProportion())>0
                    || new BigDecimal(100).compareTo(tradeMultiPtl001Dto.getProportion())<0) {
                        errorList.add(new ValidMessage("", "第"+(i+1)+"行：权重方式为自定义时，分配比例请输入0-100的数值！"));
                    }
                    else {
                        totalProportion = ArithmeticUtil.doAdd(totalProportion,tradeMultiPtl001Dto.getProportion());
                    }
                }

                if(!StringUtil.isNotEmpty(tradeMultiPtl001Dto.getPrice()) && !StringUtil.isNotEmpty(tradeMultiPtl001Dto.getFprice())
                && !StringUtil.isNotEmpty(tradeMultiPtl001Dto.getYield()) && !StringUtil.isNotEmpty(tradeMultiPtl001Dto.getEntrustPrice())) {
                    errorList.add(new ValidMessage("", "第"+(i+1)+"行：请输入组合分配列表的指令价格、净价、全价、到期收益率或回购利率！"));
                }

                if(StringUtil.isNotEmpty(tradeMultiPtl001Dto.getFinprodType2()) && StringUtil.isNotEmpty(tradeMultiPtl001Dto.getPs())) {
                    EtrExchangeTrade001Dto etrExchangeTrade001Dto = etrExchangeTradeMulti001Service.genEtrExchangeTrade001Dto(dto,tradeMultiPtl001Dto);
                    etrExchangeTrade001Service.setEntrustBussType(etrExchangeTrade001Dto);
                    AjaxResult ajax  = etrExchangeTrade001Service.checkSave(etrExchangeTrade001Dto);
                    if (ajax != null && !StringUtil.isEmptyList(ajax.getErrorList())) {
                        for(ValidMessage validMessage:ajax.getErrorList()) {
                            if(!StringUtil.equals(validMessage.getField(),"entrustAmt")
                                    && !StringUtil.equals(validMessage.getField(),"entrustNum")) {
                                validMessage.setMessage("第"+(i+1)+"行："+validMessage.getMessage());
                                errorList.add(validMessage);
                            }

                        }
                    }
                }

            }

            for(String key:indexMap.keySet()) {
                errorList.add(new ValidMessage("", "第"+indexMap.get(key)+"行：请输入组合分配列表的指令价格、净价、全价、到期收益率或回购利率！"));
            }

            if(StringUtil.equals(dto.getWeightType(),DictCodeListExt3.权重方式_自定义.getValue())
                    && new BigDecimal(100).compareTo(totalProportion)<0
            ) {
                errorList.add(new ValidMessage("", "权重方式为自定义时，分配比例总和不能超过100！"));
            }
        }
        else {
            errorList.add(new ValidMessage("", "组合分配列表不能为空"));
        }

        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * 提交保存
     * @param dto
     * @param br
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody EtrExchangeTradeMulti001Dto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        String msg ="";
        String businessType = etrExchangeTrade001Service.setEntrustBussType(dto);

        Map<String,List<EtrExchangeTradeMultiPtl001Dto>> groupMap= dto.getTradeMultiPtl001Dtos().stream().collect(
                Collectors.groupingBy(item->item.getAcctNo()+"_"+item.getPs()+"_"+item.getEntrustPrice()+"_"+item.getPrice()));
        //如果分组Map，key的数量>1,说明是多组合指令，key的数量=1,则为系列户指令
        if(groupMap.keySet().size() == 1 && dto.getTradeMultiPtl001Dtos().size()>1) {
            //业务编号生成规则参数
            if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
                BussinessCodeParameter param = etrExchangeTrade001Service.genBussinessCodeParameter(dto);
                String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                dto.setEntrustId(code);
            }

            BigDecimal totalEntrustAmt = dto.getTradeMultiPtl001Dtos().stream().map(EtrExchangeTradeMultiPtl001Dto::getEntrustAmt).reduce(BigDecimal::add).get();
            BigDecimal totalEntrustNum = dto.getTradeMultiPtl001Dtos().stream().map(EtrExchangeTradeMultiPtl001Dto::getEntrustNum).reduce(BigDecimal::add).get();
            dto.setEntrustAmt(totalEntrustAmt);
            dto.setEntrustNum(totalEntrustNum);
            dto.setBusinessTypeName(businessType);


            // ----------预占功能 start----------
            if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
                // add by zhangrui1 start
                String finprodType2 = "";
                if(dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    finprodType2 =  DictCodeList.平安资产类型_债券.getValue();
                }
                else if(dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
                    finprodType2 =  DictCodeList.平安资产类型_基金.getValue();
                }
                else if(dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                    finprodType2 =  DictCodeListExt2.平安资产类型_股票.getValue();
                }
                else if((dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue()) || dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue())) ) {
                    finprodType2 =  DictCodeList.平安资产类型_质押式回购.getValue();
                }
                //债转股需要走批量接口
                String infCode =  BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
                AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dto, infCode, finprodType2);
                if (StringUtil.isNotEmpty(ar)) {
                    return ar;
                }
            }
            // ----------预占功能 end----------
            ProcessParameter<EtrExchangeTradeMulti001Dto> processParameter = new ProcessParameter<EtrExchangeTradeMulti001Dto>();
            WorkFlowParameter<EtrExchangeTradeMulti001Dto> params = new WorkFlowParameter<EtrExchangeTradeMulti001Dto>();
            params.setPageId("EtrExchangeTradeMain");
            params.setElementId(dto.getElementId());
            params.setBusinessType(businessType);
            params.setBusinessCode(dto.getEntrustId());
            params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")"
                    + "-" + "业务类型"+ "(" + entity.getBusinessTypeName() + ")-"
                    + "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
            processParameter.setWorkFlowParameter(params);
            msg = etrExchangeTradeMulti001Service.start(processParameter, dto);
        }
        //多组合指令
        else {
            List<EtrExchangeTrade001Dto> etrExchangeTrade001DtoList = new ArrayList<>();
            for(EtrExchangeTradeMultiPtl001Dto item:dto.getTradeMultiPtl001Dtos()) {
                EtrExchangeTrade001Dto etrExchangeTrade001Dto = etrExchangeTradeMulti001Service.genEtrExchangeTrade001Dto(dto,item);

                if (dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue())) {
                    etrExchangeTrade001Dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                } else if (dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue())) {
                    etrExchangeTrade001Dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                }

                BussinessCodeParameter param = etrExchangeTrade001Service.genBussinessCodeParameter(dto);
                String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                etrExchangeTrade001Dto.setEntrustId(code);
                etrExchangeTrade001Dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
                etrExchangeTrade001Dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
                etrExchangeTrade001Dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
                etrExchangeTrade001Dto.setCreateTime(new Date());// 创建时间
                etrExchangeTrade001Dto.setBusinessTypeName(businessType);//此处存的是类型
                etrExchangeTrade001Dto.setOptType(ENTRUST_RECORD_NEW);
                etrExchangeTrade001Dto.setIsMulti(DictCodeList.是否_否.getValue());

                etrExchangeTrade001DtoList.add(etrExchangeTrade001Dto);
            }

            // ----------预占功能 start----------
            if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
                // add by zhangrui1 start
                AjaxResult ar = EntrustRdsCommonAop.doListPreOccupy(getPageId(), etrExchangeTrade001DtoList,
                        BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue());
                if (null != ar) {
                    return ar;
                }
                EntrustRdsCommonAop.riskListToChildren(dto.getRiskTrialRetDto(), etrExchangeTrade001DtoList,
                        (etrExchangeTrade001Dto, list) -> etrExchangeTrade001Dto.setRiskTrialRetDto(list));
            }
            // ----------预占功能 end----------

            ProcessParameter<EtrExchangeTrade001Dto> processParameter = new ProcessParameter<EtrExchangeTrade001Dto>();
            WorkFlowParameter<EtrExchangeTrade001Dto> params = etrExchangeTrade001Service.getWorkFlowParameter(dto);
            params.setPageId("EtrExchangeTradeMain");
            params.setElementId(dto.getElementId());
            params.setBusinessType(item->item.getBusinessTypeName());
            params.setBusinessCode(item->item.getEntrustId());
            params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")"
                    + "-" + "业务类型"+ "(" + SysDataUtil.getNameFromDataDict(DictCode.共通业务类型, entity.getBusinessTypeName()) + ")-"
                    + "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
            processParameter.setWorkFlowParameter(params);

            msg = etrExchangeTrade001Service.start(processParameter, etrExchangeTrade001DtoList.toArray(new EtrExchangeTrade001Dto[etrExchangeTrade001DtoList.size()]));
        }

        return new AjaxResult(msg);
    }

    /**
     * 获取多组合详情
     * @return
     */
    @ResponseBody
    @RequestMapping("selectMultDetail")
    public EtrExchangeTradeMulti001Dto selectMultDetail(Map<String, Object> param){
        return etrExchangeTradeMulti001Service.selectMultDetail(RequestUtil.getPageParam());
    }
}
	
