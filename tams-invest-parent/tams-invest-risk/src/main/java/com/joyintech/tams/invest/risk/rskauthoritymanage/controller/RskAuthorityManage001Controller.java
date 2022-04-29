/*
 * 文件名：RskAuthorityManage001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.risk.rskauthoritymanage.controller;

import java.util.ArrayList;
import java.util.List;

import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.risk.rskauthoritymanage.dto.RskAuthorityManageDto;
import com.joyintech.tams.invest.risk.rskauthoritymanage.service.RskAuthorityManageService;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;

/**
 * 风险授权管理
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年11月30日
 * @see RskAuthorityManage001Controller
 * @since 1.0
 */
@Controller
@PageId("RskAuthorityManage001")
@RequestMapping("/invest/risk/rskauthoritymanage/RskAuthorityManage001")
public class RskAuthorityManage001Controller extends BaseController {
    /**
     * rskAuthorityManageService
     */
    @Autowired
    private RskAuthorityManageService rskAuthorityManageService;
    /**
     * 初始化的查询条件
     */
   private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.角色岗位,
            DictCode.平安资产类型, DictCode.交易场所,
            // 资产类型改为授权类型 add by renping 2019-08-06  start
            DictCode.授权类型一级, DictCode.授权类型二级
            // 资产类型改为授权类型 add by renping 2019-08-06  end
           };

    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DICT_CODES);
        List<SelectOptionDto> assetSecTypeList = makeAllAssetSecType();
        model.addAttribute("ASSET_SEC_TYPE", JsonUtils.objectArray2Json(assetSecTypeList.toArray()));
        List<SelectOptionDto> psList = makeAllPs();
        model.addAttribute("PS", JsonUtils.objectArray2Json(psList.toArray()));
        return super.init(model);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    private List<SelectOptionDto> makeAllPs() {
        List<SelectOptionDto> listFloatRate = new ArrayList<SelectOptionDto>();
        SelectDto selectDto1 = this.getSelectDataFromDataDic(DictCode.交易方向, null);
        for (SelectOptionDto dto : selectDto1.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto2 = this.getSelectDataFromDataDic(DictCode.回购拆借方向, null);
        for (SelectOptionDto dto : selectDto2.getOptions()) {
            listFloatRate.add(dto);
        }
        return listFloatRate;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    private List<SelectOptionDto> makeAllAssetSecType() {
        List<SelectOptionDto> listFloatRate = new ArrayList<SelectOptionDto>();
        SelectDto selectDto1 = this.getSelectDataFromDataDic(DictCode.WIND一级分类_含债券一级分类等, null);
        for (SelectOptionDto dto : selectDto1.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto2 = this.getSelectDataFromDataDic(DictCode.存款类资产类型, null);
        for (SelectOptionDto dto : selectDto2.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto3 = this.getSelectDataFromDataDic(DictCode.项目类一级分类, null);
        for (SelectOptionDto dto : selectDto3.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto4 = this.getSelectDataFromDataDic(DictCode.基金类资产类型, null);
        for (SelectOptionDto dto : selectDto4.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto5 = this.getSelectDataFromDataDic(DictCode.计划类资产一级分类, null);
        for (SelectOptionDto dto : selectDto5.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto6 = this.getSelectDataFromDataDic(DictCode.拆借类型, null);
        for (SelectOptionDto dto : selectDto6.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto7 = this.getSelectDataFromDataDic(DictCode.回购类型, null);
        for (SelectOptionDto dto : selectDto7.getOptions()) {
            listFloatRate.add(dto);
        }
        return listFloatRate;
    }
    /**
     * 新建产品代码联想框 
     * @return List List
     * @throws Exception Exception
     */
    @ResponseBody
    @RequestMapping("/getUser")
    public Page<SelectOptionDto> getUser()
    {
        return rskAuthorityManageService.getUser(RequestUtil.getPageParam());
    }
    /**
     * 画面查询方法
     * @return 所有数据
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<RskAuthorityManageDto> selectAll()
         {
        return rskAuthorityManageService.selectForPage(
            RequestUtil.getParameters(getRequest()));
    }
    /**
     * 删除按钮点击时的check
     *
     * @param dto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupDelete.class}) RskAuthorityManageDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    /**
     *  页面删除
     * @param dto 费用品种
     * @param br br
     * @return 删除结果
     * @throws Exception Exception
     */
    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Validated({
        ValidateGroupDelete.class}) RskAuthorityManageDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<RskAuthorityManageDto> processParameter = new ProcessParameter<RskAuthorityManageDto>();
        WorkFlowParameter<RskAuthorityManageDto> params = new WorkFlowParameter<RskAuthorityManageDto>();
        params.setPageId("RskAuthorityManage002");
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getSeqNo());
        params.setBusinessType(DictCodeList.共通业务类型_授权管理.getValue());
        // 设置流程业务说明, entity即传入的dto
        // 业务说明：“流水号：” + 上述取得的流水号+“用户：”+画面.用户+“授权额度：”+画面.授权额度
        params.setComment(entity -> "流水号" + "(" + entity.getSeqNo() + ")" + "-" + "授权额度" + "("
                                    + entity.getAuthorizeLimitAmt() + ")");
        processParameter.setWorkFlowParameter(params);
        String msg = rskAuthorityManageService.start(processParameter,
            dto.getSelectedItem().toArray(new RskAuthorityManageDto[dto.getSelectedItem().size()]));
        return new AjaxResult(msg);
    }
    /**
     * 导出信息excel
     * 
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */

    @RequestMapping("export")
    public ResponseEntity<byte[]> export()
         {
        return rskAuthorityManageService.export(this.getPageId(),
            RequestUtil.getParameters(getRequest()));

    }
}
