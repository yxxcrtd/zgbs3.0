package com.joyintech.tams.cfets.etr.repo.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtribRepoInfo003Dto;
import com.joyintech.tams.cfets.etr.repo.service.CfetsEtrIbRepoinfo003Service;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 银行间质押式回购指令- 多组合指令新建
 * @author wl
 * @date 2021年02月02日
 * @see CfetsEtribRepoInfo003Controller
 * @since 1.0
 */
@Controller
@PageId("CfetsEtrIbRepoinfo003")
@RequestMapping("/cfets/etrib/cfetsEtrIbRepoinfo003")
@Api(tags = "银行间回购指令新建")
public class CfetsEtribRepoInfo003Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CfetsEtribRepoInfo003Controller.class);

    @Autowired
    private CfetsEtrIbRepoinfo003Service cfetsEtrIbRepoinfo003Service;

    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
    /**
     * 多组合指令新建画面
     * @param model
     * @return
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易场所);
        this.setSelectOptionToModel(model, DictCode.交易市场);
        this.setSelectOptionToModel(model, DictCode.回购拆借方向);
        this.setSelectOptionToModel(model, DictCode.币种);
        this.setSelectOptionToModel(model, DictCode.回购类型);
        this.setSelectOptionToModel(model, DictCode.回购期限品种);
        this.setSelectOptionToModel(model, DictCode.清算速度);
        this.setSelectOptionToModel(model, DictCode.价格模式);
        Date sysDate = DateUtil.platDateFromCache();
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        CfetsEtribRepoInfo003Dto entity = null;
        boolean todoList = false;
        // 代办中过来
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            entity = cfetsEtrIbRepoinfo003Service.getBussinessEntityByWorkflowId(workflowId);
            if(Objects.nonNull(entity.getPrice())) {
                entity.setPrice(entity.getPrice().multiply(new BigDecimal("100")));
            }
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("person_entity", JsonUtils.object2Json(entity.getEtrInvestPersonInfo()));
//            model.addAttribute("businessId",workflowId);
            model.addAttribute("entity_BussCombDistribtList",  JsonUtils.object2Json(entity.getEtrInvestRepoInfoDtos()));
            model.addAttribute("entity_PledgeBondTotalListDetailed",  JsonUtils.object2Json(entity.getPledgeBondTotalListDetailed()));
            model.addAttribute("entity_RiskTrialRetList",  JsonUtils.object2Json(entity.getRiskTrialRetDto()));
            model.addAttribute("entity_riskTrialSpan",entity.getCfetsEtribdoRiskcalResponse()== null ? null : entity.getCfetsEtribdoRiskcalResponse().getRiskTrialSpan());
            model.addAttribute("entity_RiskcalResponse",entity.getCfetsEtribdoRiskcalResponse());

            todoList = true;
            model.addAttribute("businessId", entity.getTotalEtrustId());
        } else {
            entity = new CfetsEtribRepoInfo003Dto();
            entity.setInvManagerId(LoginUserUtil.getUserName());
            entity.setVdate(sysDate);
            entity.setMdate(sysDate);
            entity.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            entity.setCalendarId(BussinessDictCodeList.wind交易场所_银行间.getValue());
            entity.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            entity.setEntrustStatus(DictCodeList.委托状态_新建.getValue());
            entity.setSecAcctId("999999");
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("businessId", IDGenerator.generate());
        }
        model.addAttribute("ps_03", DictCodeList.回购拆借方向_正回购.getValue());
        model.addAttribute("ps_04", DictCodeList.回购拆借方向_逆回购.getValue());
        model.addAttribute("tradeP01", DictCodeListExt1.交易场所_银行间.getValue());
        model.addAttribute("tradeP02", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("tradeP03", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        model.addAttribute("repoTypeH01", DictCodeList.回购类型_质押式协议回购.getValue());
        model.addAttribute("repoTypeH02", DictCodeList.回购类型_质押式竞价回购.getValue());
        model.addAttribute("speed0", DictCodeList.清算速度_T_0.getValue());
        model.addAttribute("speed1", DictCodeList.清算速度_T_1.getValue());
        model.addAttribute("GC001", DictCodeList.回购期限品种_GC001.getValue());
        model.addAttribute("GC002", DictCodeList.回购期限品种_GC002.getValue());
        model.addAttribute("GC003", DictCodeList.回购期限品种_GC003.getValue());
        model.addAttribute("GC004", DictCodeList.回购期限品种_GC004.getValue());
        model.addAttribute("GC007", DictCodeList.回购期限品种_GC007.getValue());
        model.addAttribute("GC014", DictCodeList.回购期限品种_GC014.getValue());
        model.addAttribute("GC028", DictCodeList.回购期限品种_GC028.getValue());
        model.addAttribute("GC091", DictCodeList.回购期限品种_GC091.getValue());
        model.addAttribute("GC182", DictCodeList.回购期限品种_GC182.getValue());
        model.addAttribute("R001", DictCodeList.回购期限品种_R001.getValue());
        model.addAttribute("R007", DictCodeList.回购期限品种_R007.getValue());
        model.addAttribute("R014", DictCodeList.回购期限品种_R014.getValue());
        model.addAttribute("R021", DictCodeList.回购期限品种_R021.getValue());
        model.addAttribute("RC001", DictCodeList.回购期限品种_RC001.getValue());
        model.addAttribute("RC003", DictCodeList.回购期限品种_RC003.getValue());
        model.addAttribute("RC007", DictCodeList.回购期限品种_RC007.getValue());
        model.addAttribute("R_001", DictCodeList.回购期限品种_R_001.getValue());
        model.addAttribute("R_002", DictCodeList.回购期限品种_R_002.getValue());
        model.addAttribute("R_003", DictCodeList.回购期限品种_R_003.getValue());
        model.addAttribute("R_004", DictCodeList.回购期限品种_R_004.getValue());
        model.addAttribute("R_007", DictCodeList.回购期限品种_R_007.getValue());
        model.addAttribute("R_014", DictCodeList.回购期限品种_R_014.getValue());
        model.addAttribute("R_028", DictCodeList.回购期限品种_R_028.getValue());
        model.addAttribute("R_091", DictCodeList.回购期限品种_R_091.getValue());
        model.addAttribute("R_182", DictCodeList.回购期限品种_R_182.getValue());
        model.addAttribute("R1M", DictCodeList.回购期限品种_R1M.getValue());
        model.addAttribute("R2M", DictCodeList.回购期限品种_R2M.getValue());
        model.addAttribute("R3M", DictCodeList.回购期限品种_R3M.getValue());
        model.addAttribute("R4M", DictCodeList.回购期限品种_R4M.getValue());
        model.addAttribute("R6M", DictCodeList.回购期限品种_R6M.getValue());
        model.addAttribute("N", DictCodeList.是否_否.getValue());
        model.addAttribute("PRICEMODEL_03", DictCodeListExt2.价格模式_市价指令.getValue());
        model.addAttribute("fintype_zhg", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        model.addAttribute("fintype_nhg", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        model.addAttribute("sysDate",DateUtil.format(sysDate));
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("TodoList", todoList);
        model.addAttribute("SHARES_TRADE_MARKET_02", DictCodeListExt1.交易市场_二级市场.getValue());
        model.addAttribute("repo_pledge_model",CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL));
        return super.init(model);
    }

    /**
     * 批量提交
     *
     * @param RepoInfoDto EtrInvestRepoInfoDto
     * @param br msg
     * @throws Exception 异常
     */
    @ResponseBody
    @PostMapping("save")
    @ApiOperation(value = "多组合指令新建", notes = "多组合指令新建")
    public Message<AjaxResult> save(@RequestBody CfetsEtribRepoInfo003Dto RepoInfoDto) throws Exception {
        RepoInfoDto.setAssetType1(DictCodeList.回购类型_质押式回购.getValue());
        List<CfetsEtribRepoInfo003Dto> dataList = new ArrayList<>();
        cfetsEtrIbRepoinfo003Service.packageEquity(RepoInfoDto,dataList);
        EntrustRdsCommonAop.riskListToChildren(RepoInfoDto.getRiskTrialRetDto(), RepoInfoDto.getEtrInvestRepoInfoDtos(),(dto, list) -> dto.setRiskTrialRetDto(list));
        // 临时主键
        if(!StringUtil.isNotEmpty(RepoInfoDto.getEntrustId())) {
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共通业务类型_回购投资委托);
            String generatedId = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
            RepoInfoDto.setTotalEtrustId(generatedId);
        }else{
            RepoInfoDto.setTotalEtrustId(RepoInfoDto.getEntrustId());
        }
//        for (int i = 0; i < RepoInfoDto.getBussCombDistribtList().size(); i++) {
//            RepoInfoDto.getBussCombDistribtList().get(i).setEntrustId(RepoInfoDto.getEtrInvestRepoInfoDtos().get(i).getEntrustId());
//        }
        ProcessParameter<CfetsEtribRepoInfo003Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<CfetsEtribRepoInfo003Dto> params = new WorkFlowParameter<>();
        params.setPageId("CfetsEtrIbRepoinfo003");
        params.setElementId(RepoInfoDto.getElementId());
        params.setBusinessCode(CfetsEtribRepoInfo003Dto::getTotalEtrustId);
        params.setBusinessType(DictCodeListExt3.共通业务类型_本币业务指令_质押式回购.getValue());
        processParameter.setWorkFlowParameter(params);

        String msg = null;
        try {
            msg = cfetsEtrIbRepoinfo003Service.start(processParameter, RepoInfoDto);
            AjaxResult m =new AjaxResult(msg);
            return new Message<AjaxResult>().success().setData(m);
        }catch (Exception e) {
            logger.error("保存银行间质押式多组合指令新建失败",e);
            return PositionUtil.ajaxErrorReturn(e);
        }
    }



}
