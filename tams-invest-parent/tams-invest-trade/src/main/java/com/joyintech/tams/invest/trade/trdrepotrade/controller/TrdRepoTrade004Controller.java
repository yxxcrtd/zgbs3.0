/*
 * 文件名：TrdRepoTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.trdrepotrade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.trade.bondlend.dto.TrdBondLendInvestTradeDto;
import com.joyintech.tams.invest.trade.bondlend.service.TrdBondLendInvestTrade002Service;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTrade001Dto;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTrade002Dto;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTrade004Dto;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTradeRevokeDto;
import com.joyintech.tams.invest.trade.trdrepotrade.service.TrdRepoTrade001Service;
import com.joyintech.tams.invest.trade.trdrepotrade.service.TrdRepoTrade002Service;
import com.joyintech.tams.invest.trade.trdrepotrade.service.TrdRepoTrade004Service;
import com.joyintech.tams.invest.trade.trdrepotrade.validator.TrdRepoTrade004DtoValidator;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 质押式回购投资交易-》质押券置换
 * 〈功能详细描述〉
 *
 * @author nixiukuan
 * @version 1.0 2019年11月07日
 * @see TrdRepoTrade004Controller
 * @since 1.0
 */
@PageId("TrdRepoTrade004")
@RequestMapping("/invest/trade/trdrepotrade/TrdRepoTrade004")
@Controller
public class TrdRepoTrade004Controller extends BaseController {
    /**
     * TrdRepoTrade004Service
     */
    @Autowired
    private TrdRepoTrade004Service trdRepoTrade004Service;
    @Autowired
    private TrdBondLendInvestTrade002Service trdBondLendInvestTrade002Service;
    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;
    @Override
    public String init(Model model) {
        this.setSelectOptions(model);
        String tradeId = this.getParam("tradeId");// 交易编号
        String fTradeId=this.getParam("fTradeId");// 原交易编号
        if(StringUtils.isNotBlank(fTradeId)){
            model.addAttribute("fTradeId",fTradeId);
            if(StringUtils.isEmpty(tradeId))
            {
                tradeId=fTradeId;
            }
        }
        String ps = this.getParam("ps");
        String workflowStatus = this.getParam("workflowStatus");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        TrdRepoTrade004Dto dto=new TrdRepoTrade004Dto();
        if (StringUtil.equals(workflowStatus, DictCodeListExt1.交易流程状态_流程中.getValue())
                || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(workflowId))) {
            dto = trdRepoTrade004Service.getBussinessEntityByWorkflowId(
                    workflowId);
            model.addAttribute("workflowId", workflowId);
            if (StringUtil.isBlank(dto.getTradeId())) {
                // 根据工作流代码查询交易编号
                dto.setTradeId(trdRepoTrade004Service.selectTradeIdByWorkflowId(workflowId));
            }
            tradeId=dto.getTradeId();
//            //选出质押券列表
//            if(!StringUtil.isEmptyList(dto.getOutDataTable())) {
//                dto.getOutDataTable().stream().forEach(item -> {
//                    if (item.getPledgeRatio() != null && !item.getPledgeRatio().equals(BigDecimal.ZERO)) {
//                        item.setAvaPledgePrin(ArithmeticUtil.multiply(ArithmeticUtil.divide(item.getPledgePrin(), item.getPledgeRatio(), 4),new BigDecimal(100)));
//                    } else {
//                        item.setAvaPledgePrin(item.getPledgePrin());
//                    }
//                });
//            }

            model.addAttribute("entity_outBussRepoSec",
                    dto.getOutDataTable() == null ? null : JsonUtils.object2Json(
                            dto.getOutDataTable().stream()
                                    .filter(a->(a.getPledgePrin()==null?new BigDecimal(0):a.getPledgePrin()).compareTo(new BigDecimal(0))!=0)
                                    .map(entity -> {
                                        entity.setPledgePrin(ArithmeticUtil.divide(entity.getPledgePrin(), 10000, 2));
                                        entity.setPrin(ArithmeticUtil.divide(entity.getPrin(), 10000, 2));
                                        return entity;
                                    }).toArray()));
            //最终质押券列表
//            //选出质押券列表
//            if(!StringUtil.isEmptyList(dto.getBussResultRepoSec())) {
//                dto.getBussResultRepoSec().stream().forEach(item -> {
//                    if (item.getPledgeRatio() != null && !item.getPledgeRatio().equals(BigDecimal.ZERO)) {
//                        item.setAvaPledgePrin(ArithmeticUtil.multiply(ArithmeticUtil.divide(item.getPledgePrin(), item.getPledgeRatio(), 4),new BigDecimal(100)));
//                    } else {
//                        item.setAvaPledgePrin(item.getPledgePrin());
//                    }
//                });
//            }

            model.addAttribute("bussResultRepoSecMsg",
                    dto.getBussResultRepoSec() == null ? null : JsonUtils.object2Json(
                            dto.getBussResultRepoSec()));
        }
        else if (StringUtil.isNotEmpty(tradeId)) {
            //根据交易方向来判断是债券借贷还是质押式回购，查询对应的数据
            if(ps.equals(DictCodeList.回购拆借方向_正回购.getValue())||ps.equals(DictCodeList.回购拆借方向_逆回购.getValue())) {
                dto = trdRepoTrade004Service.getTrdRepoTradeDto(tradeId);
            }
            else  if(ps.equals(DictCodeList.回购拆借方向_融入.getValue())||ps.equals(DictCodeList.回购拆借方向_融出.getValue()))
            {
                TrdBondLendInvestTradeDto trdBondLendInvestTradeDto = trdBondLendInvestTrade002Service.getTrdBondLendInvestTradeDto(tradeId);
                BeanUtil.copyPropertiesIgnoreNull(dto,trdBondLendInvestTradeDto);
                dto.setSettlementStartDate(trdBondLendInvestTradeDto.getVdate());//首次结算日
                dto.setSettlementEndDate(trdBondLendInvestTradeDto.getMdate());// 到期结算日
                dto.setBondFinprod(trdBondLendInvestTradeDto.getFinprodId());//债券标的
                dto.setTotalPrinMoney(trdBondLendInvestTradeDto.getShareAmt());//券面总额
                dto.setSecAcctId(trdBondLendInvestTradeDto.getSecManageAcctId());//证券托管户
                dto.setTermTypeReback(trdBondLendInvestTradeDto.getTermType());//期限品种
            }

            //获取换出质押券列表数据
            Map<String, Object> outRepoSecParm=new HashMap<>();
            outRepoSecParm.put("branch",dto.getBranch()==null?Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)):dto.getBranch());

            if(ps.equals(DictCodeList.回购拆借方向_正回购.getValue())||ps.equals(DictCodeList.回购拆借方向_逆回购.getValue())) {
                outRepoSecParm.put("finprodId",this.getParam("finprodId"));
            }
            else
            {
                outRepoSecParm.put("finprodId",dto.getTradeId());
            }

            Page<PledgeBondInfoDto> trdRepoTrade001DtoPage = trdRepoTrade004Service.findOutPage(outRepoSecParm);

//            if(!StringUtil.isEmptyList(trdRepoTrade001DtoPage.getRoot())) {
//                trdRepoTrade001DtoPage.getRoot().stream().forEach(item -> {
//                    if (item.getPledgeRatio() != null && !item.getPledgeRatio().equals(BigDecimal.ZERO)) {
//                        item.setAvaPledgePrin(ArithmeticUtil.multiply(ArithmeticUtil.divide(item.getPledgePrin(), item.getPledgeRatio(), 4),new BigDecimal(100)));
//                    } else {
//                        item.setAvaPledgePrin(item.getPledgePrin());
//                    }
//                });
//            }
            model.addAttribute("entity_outBussRepoSec",
                    JsonUtils.object2Json(
                            trdRepoTrade001DtoPage.getRoot().stream()
                                    .filter(a->(a.getPledgePrin()==null?new BigDecimal(0):a.getPledgePrin()).compareTo(new BigDecimal(0))!=0)
                                    .map(entity -> {
                                        entity.setPledgePrin(ArithmeticUtil.divide(entity.getPledgePrin(), 10000, 2));
                                        entity.setPrin(ArithmeticUtil.divide(entity.getPrin(), 10000, 2));
                                        return entity;
                                    }).toArray()));
        }
        model.addAttribute("noBranchNoValue", CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        model.addAttribute("entity", dto);
        // 人员信息
        model.addAttribute("entity_trdInvestPersonInfo",
                dto.getTrdInvestPersonInfo() == null ? null : JsonUtils.object2Json(
                        dto.getTrdInvestPersonInfo()));
        if(dto.getTrdInvestPersonInfo()!=null) {
            model.addAttribute("trader", dto.getTrdInvestPersonInfo().getTrader());
            model.addAttribute("counterParty",StringUtil.isEmpty(dto.getTrdInvestPersonInfo().getCounterId())==true?"":trdRepoTrade004Service.selectCounterName(dto.getTrdInvestPersonInfo().getCounterId()));
            model.addAttribute("counterTrader", dto.getTrdInvestPersonInfo().getCounterTrader());

        }
        // 添加质押券信息(选入质押券)
        if(StringUtil.isNotBlank(workflowId))
        {
            model.addAttribute("entity_bussRepoSecDto",  dto.getBussRepoSec() == null ? null : JsonUtils.object2Json(
                    dto.getBussRepoSec()));
        }
        else {
            model.addAttribute("entity_bussRepoSecDto", null);
        }
        Date sysDate = DateUtil.platDateFromCache();
        String formatDate = DateUtil.format(sysDate);
        model.addAttribute("formatDate",formatDate);
        if(StringUtils.isNotBlank(this.getParam("fTradeId"))){
            model.addAttribute("fTradeId",this.getParam("fTradeId"));
        }
        if(StringUtils.isNotBlank(tradeId)){
            model.addAttribute("tradeId",tradeId);
        }
        if(StringUtils.isNotBlank(dto.getBussinessCode())){
            model.addAttribute("bussinessCode",dto.getBussinessCode());
        }
        if(StringUtils.isNotBlank(this.getParam("entrustId"))){
            model.addAttribute("entrustId",this.getParam("entrustId"));
        }
        return super.init(model);
    }

    /**
     * 分页查询->换出质押券列表
     *
     * @return Page<TrdBondInvestTradeDto>泛型的page实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("findOutPage")
    @ResponseBody
    public Page<PledgeBondInfoDto> findOutPage() {

        Page<PledgeBondInfoDto> trdRepoTrade001DtoPage = trdRepoTrade004Service.findOutPage(RequestUtil.getPageParam());
        return trdRepoTrade001DtoPage;
    }

    /**
     * 向model添加下拉选项列表，及相关下拉选项常量
     *
     * @param model model
     */
    private void setSelectOptions(Model model) {
        this.setSelectOptionToModel(model, DictCode.回购拆借方向, DictCode.回购类型,
                DictCode.交易场所,  DictCode.有效状态, DictCode.币种, DictCode.计息基础);
        // 交易方向
        model.addAttribute("TRADE_PS_03", DictCodeList.回购拆借方向_正回购.getValue());
        model.addAttribute("TRADE_PS_04", DictCodeList.回购拆借方向_逆回购.getValue());
        model.addAttribute("TRADE_PS_05", DictCodeList.回购拆借方向_融入.getValue());
        model.addAttribute("TRADE_PS_06", DictCodeList.回购拆借方向_融出.getValue());
        SelectDto bondlendTermTypeDataDic = getSelectDataFromDataDic(DictCode.期限品种_债券借贷, "");
        SelectDto termTypeDataDic = getSelectDataFromDataDic(DictCode.回购期限品种, "");
        //债券借贷的期限品种要合并到当前期限品种中
        List<SelectOptionDto> optionsList=new ArrayList<>();
        if(bondlendTermTypeDataDic!=null)
        {
            optionsList.addAll(bondlendTermTypeDataDic.getOptions());
        }
        if(termTypeDataDic!=null)
        {
            optionsList.addAll(termTypeDataDic.getOptions());
        }
        model.addAttribute(DictCode.回购期限品种.getValue(),JsonUtils.object2Json(optionsList));
    }

    /**
     * 画面点击保存按钮时check
     *
     * @param dto
     *            实体类
     * @param result
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(
            @RequestBody @Validated({ ValidateGroupEdit.class }) TrdRepoTrade004Dto dto,
            BindingResult result) {
       return commonCheck(result, dto.getOperateType());
    }

    /**
     * 保存处理
     *
     * @param dto
     * @return
     * @throws Exception
     */
    @RequestMapping("save")
    @ResponseBody
    public  AjaxResult save(@RequestBody TrdRepoTrade004Dto dto) throws WorkFlowException {

        ProcessParameter<TrdRepoTrade004Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdRepoTrade004Dto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            processParameter.setOperationType(OperationType.NEW);
        } else {
            processParameter.setOperationType(OperationType.MODIFY);
        }
        workFlowParameter.setComment(entity -> WorkFlowUtil.concatComment(
                "质押券置换交易编号：" + entity.getTradeId() + "置换日期：" + DateUtil.format(dto.getSubstitutionDate()))
        );

        //业务编号参数
        dto.setBusiType(DictCodeListExt3.共通业务类型_质押券置换.getValue());
        String bussinessCode="";
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessExtType(
                DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_质押券置换));
        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        bussinessCode=baseGetBusinessIdImpl.buildBussinessCode(parameter);
        workFlowParameter.setBusinessCode(bussinessCode);
        workFlowParameter.setBusinessType(DictCodeListExt3.共通业务类型_质押券置换.getValue());
        dto.setBussinessCode(bussinessCode);
        String msg = trdRepoTrade004Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * add by yangwenjun 20201123 根据业务编号查询合作资产
     * @param param
     * @return
     */
    @RequestMapping("getChlId")
    @ResponseBody
    public Map<String, String> getChlId(@RequestBody Map<String, Object> param) {
        String tradeId=(String)param.get("tradeId");
        String chlId = trdRepoTrade004Service.getChlId(tradeId);
        Map<String,String> map=new HashMap<>();
        map.put("chlId",chlId);
        return map;
    }

}
