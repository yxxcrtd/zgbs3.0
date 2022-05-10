/*
 * 文件名：MstMarketInstitutions002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2018年7月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.basicdata.mstmarketinstitutions.controller;

import java.util.*;

import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.joyintech.tams.common.base.entity.InterfCmsInstitution;
import com.joyintech.tams.common.base.entity.InterfCompintroduction;
import com.joyintech.tams.common.base.entity.MstBondCreditRating;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.system.constant.SystemDictCode;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.invest.basicdata.mstmarketinstitutions.dto.MstMarketInstitutionsDto;
import com.joyintech.tams.invest.basicdata.mstmarketinstitutions.service.MstBondCreditRatingService;
import com.joyintech.tams.invest.basicdata.mstmarketinstitutions.service.MstMarketInstitutionsService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.thymeleaf.util.StringUtils;

/**
 * 市场机构信息新建
 * 显示市场机构信息的新建页面
 *
 * @author huwenbin
 * @version 1.0 2018年7月23日
 * @see MstMarketInstitutions002Controller
 * @since 1.0
 */
@Controller
@PageId("MstMarketInstitutions002")
@RequestMapping("/invest/basicdata/mstmarketinstitutions/MstMarketInstitutions002")
public class MstMarketInstitutions002Controller extends BaseController {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(MstMarketInstitutions002Controller.class);

    /**
     * 客户评级service注入
     */
    @Autowired
    private MstBondCreditRatingService mstBondCreditRatingService;

    /**
     * 市场机构管理service注入
     */
    @Autowired
    private MstMarketInstitutionsService mstMarketInstitutionsService;
    /**
     * 业务编号组件
     */
    @Autowired(required = false)
    private BaseGetBusinessId bussGetBussinessIdService;

    /**
     * 新建页面初始化
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        //REPORT_YEAR
        List<SelectOptionDto> listYear = new ArrayList<SelectOptionDto>();
        for (int i = 2017; i <= 2025; i++) {
            SelectOptionDto year = new SelectOptionDto();
            year.setId(i + "");
            year.setText(i + "");
            listYear.add(year);
        }
        model.addAttribute("REPORT_YEAR", JsonUtils.objectArray2Json(listYear.toArray()));
        this.setSelectOptionToModel(model, DictCode.机构所属行业一级分类);
        this.setSelectOptionToModel(model, DictCode.机构所属行业二级分类);
        this.setSelectOptionToModel(model, DictCode.机构所属行业三级分类);
        this.setSelectOptionToModel(model, DictCode.机构所属行业四级分类);


        //公司性质
        this.setSelectOptionToModel(model, DictCode.企业性质);
        //是否
        this.setSelectOptionToModel(model, DictCode.是否);
        this.setSelectOptionToModel(model, SystemDictCode.行政区划_省);
        //控股类型
        this.setSelectOptionToModel(model, DictCode.控股类型);
        //客户主体类型
        this.setSelectOptionToModel(model, DictCode.客户主体类型);
        //产业结构调整类型
        this.setSelectOptionToModel(model, DictCode.产业结构调整类型);
        //工业转型升级标识
        this.setSelectOptionToModel(model, DictCode.工业转型升级标识);
        //发行机构类型_按经济类型划分
        this.setSelectOptionToModel(model, DictCode.发行机构类型_按经济类型划分);
        //发行机构类型_按技术领域划分
        this.setSelectOptionToModel(model, DictCode.发行机构类型_按技术领域划分);
        // 20200819 机构类型（规模）参数取值 市场机构机构类型（规模） wangwei start
//        //发行机构类型_按规模划分
//        this.setSelectOptionToModel(model,DictCode.发行机构类型_按规模划分 );
        // 市场机构机构类型(规模)
        this.setSelectOptionToModel(model, DictCode.市场机构机构类型);
        // 20200819 机构类型（规模）参数取值 市场机构机构类型（规模） wangwei end
        //授信策略
        this.setSelectOptionToModel(model, DictCode.授信策略);
        //表格数据字典
        this.setSelectOptionToModel(model, DictCode.长期短期, DictCode.评级结果, DictCode.评级展望, DictCode.评级机构, DictCode.变动方向);
        //核算分类
        this.setSelectOptionToModel(model, DictCode.核算分类);
        //客户机构代码
        String customerId = getParam("customerId");

        //String operationType = getParam("");
        String workflowId = getParam("workflowId");
        String insitituteCode = getParam("insitituteCode");
        String closeFuncName = getParam("closeFuncName");
        if (StringUtils.isEmpty(closeFuncName)) {
            if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(customerId)) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("customerId", customerId);
                MstMarketInstitutionsDto mstMarketInstitutionsDto = mstMarketInstitutionsService.selectDetail(map);
                model.addAttribute("entity", mstMarketInstitutionsDto);
            } else if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
                MstMarketInstitutionsDto mstMarketInstitutionsDto = mstMarketInstitutionsService.getBussinessEntityByWorkflowId(workflowId);
                model.addAttribute("entity", mstMarketInstitutionsDto);
                model.addAttribute("workflowId", workflowId);
            }
            if (StringUtil.isEmpty(customerId) && StringUtil.isEmpty(workflowId)) {

            }
        } else {//增加债券/新债页面打开发行人新增页面
            Map<String, Object> map = mstMarketInstitutionsService.autoAddInstitutions(insitituteCode);
            if (Objects.nonNull(map.get("data")) && Objects.nonNull(map.get("errorCode")) && StringUtil.equals("0", map.get("errorCode"))) {
                MstMarketInstitutionsDto dto = (MstMarketInstitutionsDto) map.get("data");
                dto.setIsIssuer(DictCodeList.是否_是.getValue());
                model.addAttribute("entity", map.get("data"));
            } else {
                String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{3,40}";
                MstMarketInstitutionsDto dto = new MstMarketInstitutionsDto();
                dto.setIsIssuer(DictCodeList.是否_是.getValue());
                if (StringUtil.isNotEmpty(insitituteCode)) {
                    if (insitituteCode.matches(regex)) {//数组和字符组成的则是编号，其他为名称
                        dto.setInsitituteCode(insitituteCode);
                    } else {
                        dto.setCustomerName(insitituteCode);
                    }
                }
                model.addAttribute("entity", dto);
            }
            model.addAttribute("insitituteCode", insitituteCode);
            model.addAttribute("closeFuncName", closeFuncName);
        }
        return super.init(model);
    }

    /**
     * 编辑修改页面表格查询
     * <功能详细描述>
     *
     * @return Page<PaRskCustomerRatingDto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<MstBondCreditRating> selectAllMstCaptialAccount() {
        Page<MstBondCreditRating> page = mstBondCreditRatingService.selectMstBondCreditPage(RequestUtil.getPageParam());
        return page;
    }

    /**
     * 保存按钮点击时的check
     *
     * @param dto    dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) MstMarketInstitutionsDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param dto MstMarketInstitutionsDto
     * @param br  BindingResult
     * @return AjaxResult
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) MstMarketInstitutionsDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if (StringUtil.isEmpty(dto.getCustomerId())) {
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共通业务类型_市场机构管理);
            try {
                String customerId = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                dto.setCustomerId(customerId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        ProcessParameter<MstMarketInstitutionsDto> processParameter = new ProcessParameter<MstMarketInstitutionsDto>();
        WorkFlowParameter<MstMarketInstitutionsDto> params = new WorkFlowParameter<MstMarketInstitutionsDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getCustomerId());
        params.setBusinessType(DictCodeList.共通业务类型_市场机构管理.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(
                entity -> "市场机构代码" + "(" + entity.getCustomerId() + ")" + "-" + "市场机构名称" + "(" + entity.getCustomerName() + ")");
        processParameter.setWorkFlowParameter(params);
        String msg = mstMarketInstitutionsService.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @return List<SelectOptionDto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getCity")
    public List<SelectOptionDto> getCity() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("dict_code_parent", SystemDictCode.行政区划_省.getValue());
        param.put("item_code_child", param.get("province"));
        return mstMarketInstitutionsService.getAddressSelectOption(param);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @return List<SelectOptionDto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getCounties")
    public List<SelectOptionDto> getCounties() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("dict_code_parent", SystemDictCode.行政区划_市.getValue());
        param.put("item_code_child", param.get("city"));
        return mstMarketInstitutionsService.getAddressSelectOption(param);
    }

    /**
     * 根据becif查询CMS_机构信息
     * <功能详细描述>
     *
     * @return InterfCmsInstitution
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/selectInterfCmsInstitution")
    public InterfCmsInstitution selectInterfCmsInstitution() {
        Map<String, Object> param = RequestUtil.getPageParam();
        return mstMarketInstitutionsService.selectInterfCmsInstitution((String) param.get("becif"));
    }

    /**
     * 根据wind编码查询wind_公司简介
     * <功能详细描述>
     *
     * @return InterfCompintroduction
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/selectInterfCompintroduction")
    public InterfCompintroduction selectInterfCompintroduction() {
        Map<String, Object> param = RequestUtil.getPageParam();
        return mstMarketInstitutionsService.selectInterfCompintroduction((String) param.get("objectId"));
    }

    /**
     * 获取评级机构
     *
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    @RequestMapping("getGradeOrgId")
    @ResponseBody
    public List<SelectOptionDto> getGradeOrgId(@RequestParam Map<String, Object> params) {
        return mstMarketInstitutionsService.getGradeOrgId(params);
    }

    @ResponseBody
    @GetMapping("/autoAddInstitutions")
    public Map<String, Object> autoAddInstitutions(@RequestParam("insitituteCode") String insitituteCode) {
        return mstMarketInstitutionsService.autoAddInstitutions(insitituteCode);
    }

}

