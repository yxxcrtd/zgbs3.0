/*
 * 文件名：TrdBondInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdirsvaluationduration.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdirsvaluationduration.dto.TrdIrsValuationDuration001Dto;
import com.joyintech.tams.valuation.trademanage.trdirsvaluationduration.service.TrdIrsValuationDuration001Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 利率互换存续期管理
 *
 * @author sunshuaishuai
 * @version 1.0 2022年02月16日
 * @see TrdIrsValuationDuration001Controller
 * @since 1.0
 */
@Controller
@PageId("TrdIrsValuationDuration001")
@RequestMapping("/valuation/trademanage/trdirsvaluationduration/TrdIrsValuationDuration001")
public class TrdIrsValuationDuration001Controller extends BaseController {

    @Autowired
    private TrdIrsValuationDuration001Service trdIrsValuationDuration001Service;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Override
    public String init(Model model) {

        this.setSelectOptionToModel(model, DictCode.利率互换存续期类型);
        return super.init(model);
    }
    /**
     * 分页查询
     *
     * @return Page<TrdBondInvestTradeDto>泛型的page实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("findPage")
    @ResponseBody
    public Page<TrdIrsValuationDuration001Dto> findPage()
            throws Exception {
        return trdIrsValuationDuration001Service.findPage(RequestUtil.getPageParam());
    }

    /**
     * 产品代码下拉框
     *
     * @return 产品代码
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getFinprodId")
    public List<SelectOptionDto> getFinprodId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("finprodType","F16");
        return commonBusinessService.getProdName(param);
    }

    /**
     * 资产代码下拉框
     *
     * @return 资产代码
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getAssetId")
    public List<SelectOptionDto> getAssetId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("assetType","F01");
        return commonBusinessService.getAssetName(param);
    }

    /**
     * 删除前判断
     *
     * @param dto
     * @param result
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Valid TrdIrsValuationDuration001Dto dto,
                            BindingResult result) throws ParseException {

        List<TrdIrsValuationDuration001Dto> list = dto.getSelectedItem();
        if(list==null||list.size()<=0){
            return new AjaxResult("请选择数据", MessageType.错误消息);
        }
        //删除时的校验
        if(StringUtil.isNotEmpty(list) && "delete".equals(dto.getOperateType())) {
            for (TrdIrsValuationDuration001Dto item : list) {
                //1.判断是否在流程中
                //判断当前流程是否存在
                if (trdIrsValuationDuration001Service.checkBusinessDataInFlow(item)) {
                    //流程存在情况
                    return new AjaxResult("交易编号：" + item.getTradeId() + "已存在流程中", MessageType.错误消息);
                }
//                //2.判断交易日等于平台日
//                String tradeDate = item.getTradeDate();
//                // 系统平台日
//                Date sysDate = baseCommonService.getSysDate();
//                if (StringUtil.isNotEmpty(tradeDate)) {
//                    Date bDate = DateUtil.parseDate(tradeDate, "yyyy-MM-dd");
//                    if (DateUtil.compareDate(sysDate, bDate) != 0) {
//                        return new AjaxResult("只能删除交易日等于平台日的交易", MessageType.错误消息);
//                    } else {
//                        return new AjaxResult("确定要删除？", MessageType.确认消息);
//                    }
//                }

            }
        }else if("edit".equals(dto.getOperateType())){
            for (TrdIrsValuationDuration001Dto item : list) {
                //1.判断是否在流程中
                //判断当前流程是否存在
                if(trdIrsValuationDuration001Service.checkBusinessDataInFlow(item)){
                    //流程存在情况
                    return new AjaxResult("交易编号："+item.getTradeId()+"已存在流程中", MessageType.错误消息);
                }
            }
        }
        return new AjaxResult();
    }

    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Valid TrdIrsValuationDuration001Dto dto, BindingResult br)
    {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        // 启动流程
        ProcessParameter<TrdIrsValuationDuration001Dto> proParam = new ProcessParameter<>();
        WorkFlowParameter<TrdIrsValuationDuration001Dto> params = new WorkFlowParameter<>();

        // 业务代码（凭证流水号）
        params.setBusinessCode(item -> item.getTradeId());
        // 业务类型
        params.setBusinessType(DictCodeList.业务类型_银行间债券交易.getValue());
        // 页面id
        params.setPageId("TrdBondValuationDuration002");
        // 元素id
        params.setElementId(dto.getElementId());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("债券存续期交易删除：", entity.getTradeId(),
                DateUtil.format(entity.getTradeDate())
        ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = trdIrsValuationDuration001Service.start(proParam, dto.getSelectedItem().toArray(
                    new TrdIrsValuationDuration001Dto[dto.getSelectedItem().size()]));
        }
        catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

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
//    @RequestMapping(ACTION_EXPORT)
//    public ResponseEntity<byte[]> export()
//        throws Exception {
//        return trdBondValuationDuration001Service.export(this.getPageId(), RequestUtil.getPageParam());
//    }

    /**
     * 导出数据 <一句话功能简述> <功能详细描述>
     *
     * @return List<ValFundMarketInfo001dto>
     * @throws Exception Exception
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export()
            throws ParseException, IOException {

        String datas = this.getParam("datas");
        Map<String, Object> params = RequestUtil.getPageParam();
        List<TrdIrsValuationDuration001Dto> listdto;
        if (datas != null) {
            listdto = JsonUtils.json2List(datas, TrdIrsValuationDuration001Dto.class);
        }
        else {
            listdto = trdIrsValuationDuration001Service.queryPageList(params);
        }

        File outFile = trdIrsValuationDuration001Service.makeFile(
                "/com/joyintech/tams/valuation/trademanage/trdirsvaluationduration/conf/export_conf.xml",
                listdto);
        return ExportUtil.export(
                this.getMessage(this.getPageId()) + SystemGlobalConstant.OPEN_PARENTHESIS
                        + DateUtil.format(new Date())
                        + SystemGlobalConstant.CLOSE_PARENTHESIS
                        + SystemGlobalConstant.POINT
                        + FileNameUtil.getFileExtName(outFile),
                outFile, true);
    }

}
