package com.joyintech.tams.invest.trade.transfer.planratetransfer.controller;

import com.alibaba.fastjson.JSONArray;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.PtlCapacctInfo;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.MstFileOutputParameter;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.entrust.dao.EtrTrdCommonMapper;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.transfer.aop.RdsTransferAop;
import com.joyintech.tams.invest.trade.transfer.planratetransfer.dto.PlanTransferDto;
import com.joyintech.tams.invest.trade.transfer.planratetransfer.dto.PlanTransferDtos;
import com.joyintech.tams.invest.trade.transfer.planratetransfer.service.TrdPlanRateInvestTradeTransfer001Service;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransferDto;
import com.joyintech.tams.invest.trade.transfer.trdassetsProject.service.TrdAssetsProject001Service;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 利率型计划转仓
 *
 * @package com.joyintech.tams.trade.resolute.planratetransfer.controller
 * @author: Xu Zhihao
 * @date: 2019/12/17 16:04
 * @version: V1.0
 * @description: 利率型计划转仓
 */
@Controller
@PageId("TrdPlanRateInvestTradeTransfer001")
@RequestMapping("/invest/trade/transfer/planratetransfer/TrdPlanRateInvestTradeTransfer001")
public class TrdPlanRateInvestTradeTransfer001Controller extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(TrdPlanRateInvestTradeTransfer001Controller.class);

    @Autowired
    private TrdPlanRateInvestTradeTransfer001Service trdPlanRateInvestTradeTransfer001Service;

    @Autowired
    TrdAssetsProject001Service trdAssetsProject001Service;

    /**
     * 通用Mapper共同代理方法
     */
    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

//    /**
//     * 计划类利率型资产存续期 service
//     * trdPlanInterestDuration001Service
//     */
//    @Autowired
//    private TrdPlanInterestDuration001Service trdPlanInterestDuration001Service;

    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;

    @Autowired
    private com.joyintech.tams.invest.trade.transfer.assign.service.TrdAssetAssignService trdAssetAssignService;

    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        //下拉框
        this.setSelectOptionToModel(model, DictCode.币种);
        Map<String, Object> paraMap = RequestUtil.getPageParam();
        String tradeId = (String) paraMap.get("tradeId");
        String portfolioId = (String) paraMap.get("portfolioId");
        String pageModel = (String) paraMap.get("pageModel");
        String workflowId = (String) paraMap.get("workflowId");
        String tradeDate = DateUtil.getSysDate();
        String invAim = MapUtils.getString(paraMap, "invAim");
        paraMap.put("tradeDate", tradeDate);
        PlanTransferDto entity = trdPlanRateInvestTradeTransfer001Service.queryAssetInt(paraMap);
        //来自代办
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            // 根据工作流与留痕处理提供的共通方法获取业务实体
            entity = trdPlanRateInvestTradeTransfer001Service.getBussinessEntityByWorkflowId(workflowId);
            // 设置到model中
            model.addAttribute(com.joyintech.framework.constant.GlobalConstant.WORKFLOW_ID, workflowId);
            model.addAttribute("entityData", JsonUtils.object2Json(entity.getPlanTransferDtos()));
            model.addAttribute("entity", entity);
            model.addAttribute("formTodoList", true);
            model.addAttribute("bussTrdTransferOrder", JsonUtils.object2Json(entity.getBussTrdTransferOrder()));
            // TODO
            /*for (SecTransferDto info : entity.getSecResoluteDtoList()) {
                for (BussTrdTransferOrderDto bussDto : entity.getBussTrdTransferOrder()) {
                    if(bussDto.getTradeId().equals(info.getStlPayId())) {
                        info.setStlPayOrdersList(Arrays.asList(bussDto));
                    }
                }
            }*/
        } else {
            //初始化界面
            //Map<String, Object> map = new HashMap<>();
            if (entity == null) {
                entity = new PlanTransferDto();
            }
            //资产代码
            entity.setFinprodId((String) paraMap.get("finprodId"));
            //资产名称
            entity.setFinprodAbbr((String) paraMap.get("finprodName"));
            //本金金额
            entity.setPrinAmt(new BigDecimal((String) paraMap.get("printAmt")));
            //剩余可转仓本金金额
            //剩余可转仓本金金额，为可用持仓，若存在流程中数据应扣减当前流程中转仓数据start
            paraMap.put("searchDate", DateUtil.getSysDate());
            BigDecimal wflAmt = trdAssetsProject001Service.selectWflAmtAsset(paraMap);
            if (wflAmt != null) {
                BigDecimal newRestAmt = ArithmeticUtil.doAdd(entity.getPrinAmt(), wflAmt);
                entity.setRestAmt(newRestAmt);
            } else {
                entity.setRestAmt(entity.getPrinAmt());
            }
            //剩余可转仓本金金额，为可用持仓，若存在流程中数据应扣减当前流程中转仓数据end
            //交易日期
            entity.setTradeDate(DateUtil.parse((String) paraMap.get("tradeDate")));
            //交割日期
            entity.setSettleDate(DateUtil.parse((String) paraMap.get("tradeDate")));
            //交易方向
            entity.setPs((String) paraMap.get("ps"));
            //通道
            entity.setChlId((String) paraMap.get("chlId"));
            //币种
            entity.setCcy("CNY");
            //净投资收益率
            entity.setLoadRate(new BigDecimal((String) paraMap.get("yield")));
            //交易编号
            entity.setTradeId(tradeId);
            //待转仓产品组合
            entity.setTransferPortfolioId(portfolioId);

            entity.setInvAim(invAim);

            //交易日历编号
            entity.setCalendarId((String) paraMap.get("calendarId"));
            Map<String, String> params = new HashMap<>();
            params.put("portfolioId", portfolioId);
            params.put("finprodId", entity.getFinprodId());
            params.put("payDate", DateUtil.format(entity.getTradeDate()));
            params.put("prinamt", entity.getPrinAmt().toPlainString());
            com.joyintech.tams.invest.trade.transfer.assign.dto.TrdAssetAssignDto assignDto = trdAssetAssignService.calcAssignAmt(params);
            if (assignDto != null) {
                //entity.setAssignPrinamt(assignDto.getAssignPrinamt());
                entity.setAssignIntamt(assignDto.getAssignIntamt());
                entity.setLiBranch(assignDto.getLiBranch());
                entity.setIsNeedAssign(assignDto.getIsNeedAssign());
            }
        }
        model.addAttribute(GlobalConstant.ENTITY, entity);
        model.addAttribute(GlobalConstant.DETAIL_PAGE_MODEL_KEY, pageModel);
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("FINPROD_TYPE_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("ACCT_TYPE_A01", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        model.addAttribute("ACCT_TYPE_A07", DictCodeListExt1.资金账户类型_券商资金账户.getValue());

        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);
        return super.init(model);
    }

    /**
     * 组合代码下拉框
     *
     * @return 下拉框列表
     * @throws Exception Exception
     */
    @RequestMapping("queryProdIdInfo")
    @ResponseBody
    public List<SelectOptionDto> queryProdIdInfo() {
        return trdPlanRateInvestTradeTransfer001Service.selectPortoId(RequestUtil.getPageParam());
    }

    /**
     * 组合可用现金和实际现金查询组件
     */
    @Autowired(required = false)
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    /**
     * 查询组合可用现金和通道可用现金
     *
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping("queryPortoAmtAndChlAmt")
    @ResponseBody
    public Map<String, Object> queryPortoAmtAndChlAmt(@RequestParam Map<String, Object> param) {
        PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
        Date sysdate = DateUtil.parse((String) param.get("cDate"));
        dto.setCdate(sysdate);
        List<String> acctTypeList = new ArrayList<>();
        acctTypeList.add(BussinessDictCodeList.资金账户类型_托管账户.getValue());
        List<String> acctTypeList1 = new ArrayList<>();
        acctTypeList.add(BussinessDictCodeList.资金账户类型_交易所资金账户.getValue());
        dto.setAcctType(acctTypeList);
        dto.setPortfolioId((String) param.get("portfolioId"));
        dto.setCcy("CNY");
        Map<String, Object> result = new HashMap<>();
        if (basePtlCashPositionInfoService != null) {
            BigDecimal cash = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            result.put("portoAvailableAmt", cash);
        }
        /*dto.setAcctType(acctTypeList1);
        if (basePtlCashPositionInfoService != null) {
            BigDecimal cashT = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            result.put("chlAvailableAmt", cashT);
        }*/
        return result;
    }

    /**
     * 保存按钮点击时的check
     *
     * @param dto dto
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody PlanTransferDto dto) {
        AjaxResult wr = new AjaxResult();
        if (StringUtil.isEmptyList(dto.getPlanTransferDtos())) {
            wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            wr.setMessage("转仓列表信息不能空", MessageType.错误消息);
            return wr;
        }
        if (StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
            wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            wr.setMessage("指令列表信息不能空", MessageType.错误消息);
            return wr;
        }
        if (dto.getPlanTransferDtos().size() != dto.getBussTrdTransferOrder().size()) {
            wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            wr.setMessage("指令生成要素已变更，请重新生成划款指令", MessageType.错误消息);
            return wr;
        }
        Map<String, BussTrdTransferOrderDto> bussMap = dto.getBussTrdTransferOrder().stream().collect(Collectors.toMap(BussTrdTransferOrderDto::getInPortfolioId, v1->v1));
        for (PlanTransferDtos info : dto.getPlanTransferDtos()) {
            if (null == bussMap.get(info.getPortfolioId()) || bussMap.get(info.getPortfolioId()).getSettleAmt().compareTo(info.getTransferTradeAmt()) != 0) {
                wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                wr.setMessage("指令生成要素已变更，请重新生成划款指令", MessageType.错误消息);
                return wr;
            }
        }
        if (StringUtil.isNotEmpty(dto.getChlId())) {
            Integer count;
            String str = "";
            Map<String, Object> sMap = new HashMap<>();
            for (PlanTransferDtos ptl : dto.getPlanTransferDtos()) {
                sMap.put("chlId", dto.getChlId());
                sMap.put("counterId", ptl.getPortfolioId());
                count = trdBondInvestTrade002Service.selectchlTrade(sMap);
                if (count <= 0) {
                    if (!StringUtil.equals(str, "")) {
                        str += "</br>";
                    }
                    str += "交易对手(" + ptl.getPortfolioId() + ")通过一次性通道投资请先进行划款！";
                }
            }
            if (StringUtil.isNotEmpty(str)) {
                wr.setMessage(str, MessageType.错误消息);
                return wr;
            }
        }
        //查询计划类利率型资产存续期交易 状态为 01-待处理、03-流程中 的数据  2020-10-22 start
		/*Map<String, Object> params = new HashMap<String,Object>();
		params.put("finprodId", dto.getFinprodId());//金融产品代码
		params.put("finprodMarketId", dto.getFinprodMarketId());//市场代码
		params.put("mdateStart", DateUtil.format(dto.getSettleDate(), DateUtil.DEFAULT_DATE_FORMAT_EN));//计息结束日开始
		params.put("mdateEnd", DateUtil.format(dto.getSettleDate(), DateUtil.DEFAULT_DATE_FORMAT_EN));//计息结束日结束
		params.put("statusValid", "01,03");//数据状态

		Page<TrdPlanInterestDurationProcessDto> pageData = trdPlanInterestDuration001Service.findPage(params);
		if(pageData != null && pageData.getRoot() != null && !pageData.getRoot().isEmpty()) {
			wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			wr.setMessage("该资产当天存在存续期记录，请先处理！", MessageType.错误消息);
			return wr;
		}*/
        //查询计划类利率型资产存续期交易 状态为 01-待处理、03-流程中  2020-10-22 end

        StringBuilder tips = new StringBuilder();
        for (PlanTransferDtos item : dto.getPlanTransferDtos()) {
            BigDecimal tradeAmt = item.getTransferTradeAmt() == null ? BigDecimal.ZERO : item.getTransferTradeAmt();
            BigDecimal prinAmt = item.getPrinAmt() == null ? BigDecimal.ZERO : item.getPrinAmt();
            BigDecimal intAmt = item.getIntAmt() == null ? BigDecimal.ZERO : item.getIntAmt();
            if (tradeAmt.compareTo(prinAmt.add(intAmt)) != 0) {
                tips.append("【" + item.getPortfolioId() + "】交易金额不等于本息和！</br>");
            }
        }
        if (StringUtil.isNotEmpty(tips.toString())) {
            wr.setData("请确认转仓列表, 是否继续提交？</br>" + tips.toString());
        } else {
            String btnName = "操作";
            if (StringUtil.equalsIgnoreCase("save", dto.getOperateType())) {
                btnName = "SysCommon.save";
            } else if (StringUtil.equalsIgnoreCase("submit", dto.getOperateType())) {
                btnName = "SysCommon.submit";
            }
            wr.setData(SpringContextUtil.getTextValue("COMMON_C_0001",
                    new String[]{SpringContextUtil.getTextValue(btnName)}));
        }
        return wr;
    }

    /**
     * 主键生成类
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 保存
     *
     * @param dto dto
     * @param br  br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
            ValidateGroupEdit.class}) PlanTransferDto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<PlanTransferDto> processParameter = new ProcessParameter<PlanTransferDto>();
        WorkFlowParameter<PlanTransferDto> params = new WorkFlowParameter<PlanTransferDto>();
        params.setPageId("TrdPlanRateInvestTradeTransfer001");
        FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                FinProductMapper.class, dto.getFinprodId(), FinProduct.class);
        BussinessCodeParameter bussinessCodeParameterIn = new BussinessCodeParameter();
        BussinessCodeParameter bussinessCodeParameterOut = new BussinessCodeParameter();
        if (BussinessConstant.SUBMIT.equals(dto.getOperateType()) &&
                !StringUtil.isEmptyList(dto.getPlanTransferDtos()) && finProduct != null) {
            dto.getPlanTransferDtos().forEach(planTransferDtos -> {
                bussinessCodeParameterIn.setCommonBusinessType(DictCodeList.共通业务类型_净值型资产申购);
                if (StringUtil.equals(DictCodeList.计划类资产一级分类_券商资产管理计划, finProduct.getFinprodType2())) {
                    bussinessCodeParameterIn.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
                } else if (StringUtil.equals(DictCodeList.计划类资产一级分类_信托产品, finProduct.getFinprodType2())) {
                    bussinessCodeParameterIn.setType1(DictCodeList.业务编号一级分类_信托产品);
                } else {
                    bussinessCodeParameterIn.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
                }
                planTransferDtos.setTradeId(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameterIn) : IDGenerator.generate());
                planTransferDtos.setTotleTradeId(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameterIn) : IDGenerator.generate());

                bussinessCodeParameterOut.setCommonBusinessType(DictCodeList.共通业务类型_净值型资产赎回);
                if (StringUtil.equals(DictCodeList.计划类资产一级分类_券商资产管理计划, finProduct.getFinprodType2())) {
                    bussinessCodeParameterOut.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
                } else if (StringUtil.equals(DictCodeList.计划类资产一级分类_信托产品, finProduct.getFinprodType2())) {
                    bussinessCodeParameterOut.setType1(DictCodeList.业务编号一级分类_信托产品);
                } else {
                    bussinessCodeParameterOut.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
                }
                planTransferDtos.setTradeIdOut(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameterOut) : IDGenerator.generate());
                planTransferDtos.setTotleTradeIdOut(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameterIn) : IDGenerator.generate());
                trdPlanRateInvestTradeTransfer001Service.setEntity(dto, planTransferDtos);
            });
        }
        // 转仓编号
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资产转仓);
            dto.setZcId(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            dto.setZcId(IDGenerator.generate());
        }
        params.setBusinessCode(dto.getZcId());

        params.setBusinessType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
        // 业务说明：“交易编号：” + 上述取得的交易编号+“资产名称：”+画面.资产名称+“交易日：”+画面.交易日
        params.setComment(
                entity -> "交易编号："
                        + entity.getZcId()
                        + "资产名称："
                        + entity.getFinprodAbbr()
                        + "交易日："
                        + DateUtil.format(entity.getTradeDate()));
        processParameter.setWorkFlowParameter(params);
        //实占
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            AjaxResult ar = RdsTransferAop.doListOccupy(dto);
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        String msg = trdPlanRateInvestTradeTransfer001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    @ResponseBody
    @RequestMapping("/selectIndex")
    public String selectIndex() {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_利率型_交易);
        String index = baseGetBusinessId.buildBussinessCode(codeParameter);
        return index;
    }

    /**
     * 划款指令生成实体类
     */
    @ResponseBody
    @RequestMapping("getEntity")
    public List<TrdProductDeal> getEntity(HttpServletRequest request) {
        List<TrdProductDeal> trdProductDealList = new ArrayList<>();
        Map<String, Object> map = RequestUtil.getParameters(getRequest());
        PlanTransferDto transferOutDto = BeanUtil.mapToObject(map, PlanTransferDto.class);
        List<PlanTransferDtos> transferInDtoList = JSONArray.parseArray(String.valueOf(map.get("dataList")), PlanTransferDtos.class);
        trdProductDealList = trdPlanRateInvestTradeTransfer001Service.generateOrdersNQs(transferOutDto, transferInDtoList);
        return trdProductDealList;
    }

    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    @ResponseBody
    @RequestMapping("/importFileSave")
    public List<Integer> importFileSave(String fileName) throws Exception {
        MstFileOutputParameter uploadFileList = mstFileCommonService.getFileInfoByFileName(fileName);
        return new ArrayList();
    }
}
