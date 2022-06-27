/*
 * 文件名：cFinDepositTarget001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月5日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.depo.controller;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

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
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.invest.asset.depo.dto.FinDepositTargetDto;
import com.joyintech.tams.invest.asset.depo.service.FinDepositTargetService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 存款类资产管理
 * @author zhangnian
 * @version 1.0 2018年9月5日
 * @see FinDepositTarget001Controller
 * @since 1.0
 */
@Controller
@PageId("FinDepositTarget001")
@RequestMapping("/invest/asset/depo/FinDepositTarget001")
public class FinDepositTarget001Controller extends BaseController{

    @Autowired
    private FinDepositTargetService finDepositTargetService;
    /**
     * 初始化数据字典下拉框
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.存款类资产类型,DictCode.存款类资产存款类型,
    // 增加数据字典行内资产二级、三级、四级、五级分类  赵东东 2019.7.23  start
        DictCode.是否,DictCode.币种,DictCode.行内资产二级分类,DictCode.行内资产三级分类,DictCode.行内资产四级分类,DictCode.行内资产五级分类};
    // 增加数据字典行内资产二级、三级、四级、五级分类  赵东东 2019.7.23  end
    @Override
    public String init(Model model){
        setSelectOptionToModel(model, DICT_CODES);
        model.addAttribute("FINPROD_TYPE_TG_F07", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        return super.init(model);
    }
    
    
    @ResponseBody
    @RequestMapping("findPage")
    public Page<FinDepositTargetDto> selectAll() {
        Page<FinDepositTargetDto> page = finDepositTargetService.selectForPage(
            RequestUtil.getParameters(getRequest()));
        return page;
    }
    
    /**
     * 删除check
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupDelete.class}) FinDepositTargetDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    
    /**
     * 编辑按钮check
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("editCheck")
    public AjaxResult editCheck(@RequestBody @Validated FinDepositTargetDto dto, BindingResult result) {
        AjaxResult checkResult = commonCheck(result, dto.getOperateType());
        //如果平台日不为空,校验平台日和终止日期

        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)
                && StringUtil.isNotEmpty(DateUtil.platDateFromCache())){
            if (Objects.nonNull(dto.getMdate()) && DateUtil.compareDate(dto.getMdate(), DateUtil.platDateFromCache()) < 0){
                ArrayList<ValidMessage> errorList = new ArrayList<>();
                errorList.add(new ValidMessage("不能修改已到期资产要素！"));
                checkResult.setErrorList(errorList);
            }
        }
        return checkResult;
       // return commonCheck(result, dto.getOperateType());
    }
    
    
    /**
     * 删除
     * @param dto
     * @param br
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    
    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Validated({
        ValidateGroupDelete.class}) FinDepositTargetDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // 设置留痕参数和工作流参数
        ProcessParameter<FinDepositTargetDto> processParameter = new ProcessParameter<FinDepositTargetDto>();
        WorkFlowParameter<FinDepositTargetDto> params = new WorkFlowParameter<FinDepositTargetDto>();
        params.setPageId("FinDepositTarget002");
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getFinprodMarketId());
        params.setBusinessType(DictCodeList.共通业务类型_存款类资产信息.getValue());
        // 设置流程业务说明
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment(getMessage("FinDepositTarget002.finprodId")+
                GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodMarketId()+GlobalConstant.CLOSE_PARENTHESIS+
                getMessage("FinDepositTarget002.finprodName")+GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodName()+GlobalConstant.CLOSE_PARENTHESIS
                +this.getMessage("FinDepositTarget002.type6")+GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodType2()+GlobalConstant.CLOSE_PARENTHESIS);
        });
        processParameter.setWorkFlowParameter(params);
        String msg = finDepositTargetService.start(processParameter,
            dto.getSelectedItem().toArray(
                new FinDepositTargetDto[dto.getSelectedItem().size()]));// 调用工作流事件
        return new AjaxResult(msg);// 返回工作流错误信息
    }
    
    /**
     * 导出
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export(){
    	Map<String, Object> param = RequestUtil.getParameters(getRequest());
    	param.put("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE"));
        return finDepositTargetService.export(this.getPageId(),param);
    }
}
