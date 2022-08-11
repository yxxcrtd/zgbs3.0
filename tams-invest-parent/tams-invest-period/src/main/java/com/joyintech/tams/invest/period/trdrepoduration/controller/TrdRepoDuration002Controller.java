package com.joyintech.tams.invest.period.trdrepoduration.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.BasicTradeFeeInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdrepoduration.dto.TrdRepoDuration002Dto;
import com.joyintech.tams.invest.period.trdrepoduration.service.TrdRepoDuration002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : 强成西
 * @version V1.0 回购存续期 确认
 * @date Date : 2018年09月21日 13:48
 */
@Controller
@PageId("TrdRepoDuration002")
@RequestMapping("/invest/period/trdrepoduration/TrdRepoDuration002")
public class TrdRepoDuration002Controller extends BaseController {

    /**
     * TrdRepoDuration002Service
     */
    @Autowired
    private TrdRepoDuration002Service trdRepoDuration002Service;

    /**
     * 编号生成
     */
    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;

    @Autowired
    private PeriodCommonService periodCommonService;

    /**
     * 平台日期
     */
    @Autowired
    private IPlatformDate platformDateImpl;

    @Autowired
    private BaseTradeFeeCommService baseTradeFeeCommService;

    /**
     * 需要初始化的下拉菜单
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.币种,DictCode.回购期限品种};

    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DICT_CODES);
        String bustype = this.getParam("bustype");
        String isTodoList = RequestUtil.getParam("isTodoList");
        TrdRepoDuration002Dto entity;
        String workflowId = RequestUtil.getParam("workflowId");
        if (StringUtil.isNotEmpty(isTodoList) && StringUtil.equals(isTodoList, "true") || (StringUtil.isNotEmpty(workflowId)&&!WorkFlowUtil.isNotFromToDoList())) {
            entity = trdRepoDuration002Service.getBussinessEntityByWorkflowId(workflowId);
            if(entity.getPstype()!=null && entity.getPstype().equals(DictCodeList.交易方向_到期续做.getValue())
                    && bustype==null){//流程中显示到期续做信息
                bustype=entity.getPstype();
            }
        }
        else {
            entity = trdRepoDuration002Service.findEffectiveData(RequestUtil.getPageParam());
            String statusValid = RequestUtil.getParam("statusValid");
            if (entity != null) {
                String busiCode = StringUtil.equals(statusValid,DictCodeListExt1.画面数据状态_撤销.getValue())?entity.getpTradeId():entity.getBusiCode();
                entity.setStatusValid(statusValid);
                if (DictCodeListExt1.画面数据状态_待处理.getValue().equals(statusValid) && entity != null) {
                    entity.setSettleDate(DateUtil.format(DateUtil.platDateFromCache()));
                    entity.setTradeAmt(entity.getTheoryAmt());
                    entity.setFpriceAmt(entity.getTheoryAmt());
                    if(bustype!=null && bustype.equals(DictCodeList.交易方向_到期续做.getValue())){//到期续做界面初始化
                        entity.setTheoryPrinex(entity.getTheoryPrin());//续做本金
                        entity.setIntRatex(entity.getIntRate());//续做利率
                        entity.setCounterTraderx(entity.getCounterTrader());//对手方交易员
                        entity.setCounterIdex(entity.getCounterId());//交易对手
                        entity.setTraderx(entity.getTrader());//交易员
                        entity.setCounterNamex(entity.getCounterName());

                    }
                }
                else {
                    entity.setBussTrdTransferOrder(periodCommonService.selectPayOrders(busiCode));
                    if(!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())){
//                        settle_acct_no settle_acct_name


                        // modify by wj 20200620  五个特殊参数 修改 为特殊取值方法
                        String institutionCode = "";
                        if(MultilevelDataAuthorityUtil.isEnabled()){
                            institutionCode =  MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName());
                        }

//                        String settle_acct_no = CacheUtil.getSysParam("settle_acct_no");
//                        String settle_acct_name = CacheUtil.getSysParam("settle_acct_name");
                        String settle_acct_no = CacheUtil.getSpecialSysParam(BussinessConstant.SETTLE_ACCT_NO_KEY,institutionCode);
                        String settle_acct_name =  CacheUtil.getSpecialSysParam(BussinessConstant.SETTLE_ACCT_NAME_KEY,institutionCode);

                        entity.getBussTrdTransferOrder().forEach(item->{
                            if(StringUtil.equals(settle_acct_no,item.getRecAcctNo())){
                                item.setRecAcctName(settle_acct_name);
                            }
                        });
                    }
                    if(bustype!=null && bustype.equals(DictCodeList.交易方向_到期续做.getValue())) {//有效数据中到期续做界面数据查询
                        //查询到期续作信息

                        TrdRepoDuration002Dto trdTrdRepoDuration=trdRepoDuration002Service.findTrdDealInfoByBusiCode(busiCode);
                        if(trdTrdRepoDuration!=null) {//有效数据
                            String orgcounterName=entity.getCounterName();
                            BeanUtil.copyPropertiesIgnoreNull(entity, trdTrdRepoDuration);
                            entity.setCounterName(orgcounterName);//修复因copy后导致丢失的数值
                            entity.setTermTypeReback(entity.getTermTypeReback().replace("-", ""));
                        }
                    }
                }
                model.addAttribute("bizCode", busiCode);
            }
        }
        if(entity!=null) {
            TrdProductDeal deal = TrdRepoDuration002Service.getTrdProductDeal(entity);

            String statusValid = RequestUtil.getParam("statusValid");
            if (entity != null && DictCodeListExt1.画面数据状态_待处理.getValue().equals(statusValid) && bustype != null && bustype.equals(DictCodeList.交易方向_到期续做.getValue())) {
                entity.setTeletype(deal.getCounterContact());//联系方式
                entity.setPstype(bustype);
            }
            deal.setTradeId(StringUtil.EMPTY);
            model.addAttribute(GlobalConstant.ENTITY, entity);
            String type;
            if (StringUtil.isEmpty(entity.getPs())) {
                if (DateUtil.compareDate(entity.getmDate(), entity.getSettleDate()) > 0) {
                    type = DictCodeListExt3.交易方向_投资端_提前终止.getValue();
                    entity.setDeadDate(DateUtil.format(DateUtil.platDateFromCache()));
                } else if (DateUtil.compareDate(entity.getmDate(), entity.getSettleDate()) == 0) {
                    if (bustype != null && bustype.equals(DictCodeList.交易方向_到期续做.getValue())) {
                        type = DictCodeList.交易方向_到期续做.getValue();
                    } else {
                        type = DictCodeList.交易方向_到期.getValue();
                    }
                } else {
                    type = DictCodeListExt3.交易方向_投资端_逾期返售.getValue();
//                设置为逾期贩售
                    deal.setPs(type);
                }
            } else {
                if(StringUtils.equals(entity.getPs(), "T54")){
                    entity.setPs("T96");
                }
                if(StringUtils.equals(entity.getPs(), "T07")){
                    entity.setPs("T95");
                }
                type = entity.getPs();
            }

            model.addAttribute("type", type);
            model.addAttribute("deal", JsonUtils.object2Json(deal));
            model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
            model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
            model.addAttribute("repoType_jjrepo", DictCodeList.回购类型_质押式竞价回购.getValue());
            model.addAttribute("Collect_Type_01", DictCodeListExt2.收取方式_回款前扣除.getValue());
            model.addAttribute("bustype", bustype);//判断是否是到期续做
            model.addAttribute("finprodtye", entity.getFinprodType2());//质押类型
            model.addAttribute("tradeId", entity.getBusiCode());//质押类型
            // 回购拆借方向_逆回购
            model.addAttribute("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
            // 回购拆借方向_逆回购
            model.addAttribute("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            model.addAttribute("PS_TQZZ", DictCodeListExt3.交易方向_投资端_提前终止.getValue());
            model.addAttribute("PS_YQFS", DictCodeListExt3.交易方向_投资端_逾期返售.getValue());
            model.addAttribute("JYCS_YHJ", DictCodeListExt1.交易场所_银行间.getValue());
            model.addAttribute("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
            model.addAttribute("R001", DictCodeList.回购期限品种_R001.getValue());
            model.addAttribute("R007", DictCodeList.回购期限品种_R007.getValue());
            model.addAttribute("R014", DictCodeList.回购期限品种_R014.getValue());
            model.addAttribute("R021", DictCodeList.回购期限品种_R021.getValue());
            model.addAttribute("R1M", DictCodeList.回购期限品种_R1M.getValue());
            model.addAttribute("R2M", DictCodeList.回购期限品种_R2M.getValue());
            model.addAttribute("R3M", DictCodeList.回购期限品种_R3M.getValue());
            model.addAttribute("R4M", DictCodeList.回购期限品种_R4M.getValue());
            model.addAttribute("R6M", DictCodeList.回购期限品种_R6M.getValue());
            model.addAttribute("R9M", DictCodeList.回购期限品种_R9M.getValue());
            model.addAttribute("R1Y", DictCodeList.回购期限品种_R1Y.getValue());
            model.addAttribute("IE_FLAG_01", DictCodeListExt1.收支方向_收入.getValue());
            model.addAttribute("IE_FLAG_02", DictCodeListExt1.收支方向_支出.getValue());
            model.addAttribute("PS_DQXZ", DictCodeList.交易方向_到期续做.getValue());
            model.addAttribute("secManageAcctId", entity.getSecManageAcctId());//证券户
            model.addAttribute("isPayWithTrade", DictCodeList.是否_是.getValue());//是否随交易支付,默认随交易支付
            model.addAttribute("formTodoList", !WorkFlowUtil.isNotFromToDoList());
            model.addAttribute("GEN_PAY_ORDERS_AUTO", CacheUtil.getSysParam("GEN_PAY_ORDERS_AUTO"));
            setSelectOptionToModel(model, DictCode.交易方向);
            if (entity != null && entity.getBussTrdTransferOrder() != null && !entity.getBussTrdTransferOrder().isEmpty()) {
                model.addAttribute("entity_bussTrdTransferOrderDto", JsonUtils.object2Json(entity.getBussTrdTransferOrder().get(0)));
            }
        }
            return super.init(model);

    }

    /**
     * 检索债券质押信息
     *
     * @return 债券质押信息
     */
    @ResponseBody
    @RequestMapping("findRepoInfo")
    public Page<RepoInfo> findRepoInfo() {
        return trdRepoDuration002Service.findRepoInfo();
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
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) TrdRepoDuration002Dto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
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
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) TrdRepoDuration002Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<TrdRepoDuration002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdRepoDuration002Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        BussinessCodeParameter parameter = new BussinessCodeParameter();
		
        // // 【UAT0324】回购到期，待办事项审批界面，业务编号和业务说明显示错误
//        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
            parameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购到期);
        } else {
            parameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购到期);
        }
        
        dto.setBusiCode(baseGetBusinessIdImpl.buildBussinessCode(parameter));
        params.setBusinessCode(dto.getBusiCode());
        params.setBusinessType(DictCodeList.共通业务类型_质押式回购存续期.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment(
            "交易编号:" + entity.getBusiCode() + "(" + SysDataUtil.getNameFromDataDict(DictCode.共通业务类型,
                DictCodeList.共通业务类型_质押式回购存续期.getValue()) + ")"));
        processParameter.setWorkFlowParameter(params);
        String msg = trdRepoDuration002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * 计算两个日期之间天数
     * @return 日期区间
     */
    @RequestMapping("getBetweenDays")
    @ResponseBody
    public String getBetweenDays(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return trdRepoDuration002Service.getBetweenDays(map);
    }

    /**
     *
     * 计算交易费用，结算费，佣金
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getFeeDetail")

    public Map<String,Object> getFeeDetail() throws ParseException {
        Map<String, Object> param = RequestUtil.getPageParam();
        List<TrdTradeFeeDetail> tradeFeeList = new ArrayList<>();
        BasicTradeFeeInputDto inputDto=new BasicTradeFeeInputDto();
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse((String)param.get("mDate"));
        inputDto.setTradeDate(date);
        inputDto.setFinprodType2(DictCodeList.平安资产类型_质押式回购.getValue());//回购类型
        if (StringUtil.equals((String)param.get("finProdtye2"), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
            inputDto.setPs(DictCodeList.回购拆借方向_正回购.getValue());  // 回购拆借方向_正回购
        }else{
            inputDto.setPs(DictCodeList.回购拆借方向_逆回购.getValue());  // 回购拆借方向_正回购
        }
        inputDto.setFpriceAmt(new BigDecimal((String)param.get("theoryPrinex")));//续做本金
        inputDto.setPrinAmt(new BigDecimal((String)param.get("theoryPrinex")));

        String tradeMarket = (String)param.get("tradeMarket");
        List<String> feeTypeList = new ArrayList<>();
        // 交易所费佣金费用设置
        inputDto.setTradeMarket(tradeMarket);
        feeTypeList.add(BussinessDictCodeList.费用类型_佣金.getValue());
        inputDto.setFeeTypeList(feeTypeList);
        inputDto.setSecAcctId((String)param.get("secAcctId"));//证券户
        inputDto.setTermType((String)param.get("termTypeReback"));
        tradeFeeList = baseTradeFeeCommService.bourseFeeDeal(inputDto);
        Map<String, Object> map = new HashMap<>();
        if(tradeFeeList!=null&&!tradeFeeList.isEmpty()){
            map.put("commission", tradeFeeList.get(0));
        }else{
            map.put("commission", "");
        }
        return map;
    }

}
