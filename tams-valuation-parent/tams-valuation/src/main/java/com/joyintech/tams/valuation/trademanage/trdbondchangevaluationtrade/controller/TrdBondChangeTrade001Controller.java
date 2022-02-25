/*
 * 文件名：TrdBondInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTradeDto;
import com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.service.TrdBondChangeTrade001Service;
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
 * 证券转换交易
 *
 * @author SUNSHUAISHUAI
 * @version 1.0 2022年01月04日
 * @see TrdBondChangeTrade001Controller
 * @since 1.0
 */
@Controller
@PageId("TrdBondChangeTrade001")
@RequestMapping("/valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade001")
public class TrdBondChangeTrade001Controller extends BaseController {

    @Autowired
    private TrdBondChangeTrade001Service trdBondChangeTrade001Service;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Override
    public String init(Model model) {

        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.交易场所);

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
    public Page<TrdBondChangeTradeDto> findPage()
        throws Exception {
        return trdBondChangeTrade001Service.findPage(RequestUtil.getPageParam());
    }

    /**
     * 产品代码下拉框
     *
     * @return 产品代码
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getFinprodName")
    public List<SelectOptionDto> getFinprodName() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("finprodType","F16");
        return commonBusinessService.getProdName(param);
    }

    /**
     * 获取转出证券名称联想下拉框信息
     * 显示可转债代码和基金代码
     * @return 资产代码
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getAssetId")
    public Page<SelectOptionDto> getAssetId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        return trdBondChangeTrade001Service.getAssetId(param);
    }

    /**
     *
     * 获取正股代码联想文本框信息
     * 显示股票代码和基金代码
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getStockId")
    public Page<SelectOptionDto> getStockId(String param){
        Page<SelectOptionDto> temp = trdBondChangeTrade001Service.getStockId(RequestUtil.getPageParam());
        return temp;
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
    public AjaxResult check(@RequestBody @Valid TrdBondChangeTradeDto dto,
                                  BindingResult result) throws ParseException {

        List<TrdBondChangeTradeDto> list = dto.getSelectedItem();
        if(list==null||list.size()<=0){
            return new AjaxResult("请选择数据", MessageType.错误消息);
        }
        //删除时的校验
        if(StringUtil.isNotEmpty(list) && "delete".equals(dto.getOperateType())) {
            for (TrdBondChangeTradeDto item : list) {
                //1.判断是否在流程中
                //判断当前流程是否存在
                if (trdBondChangeTrade001Service.checkBusinessDataInFlow(item)) {
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
                for (TrdBondChangeTradeDto item : list) {
                    //1.判断是否在流程中
                    //判断当前流程是否存在
                    if(trdBondChangeTrade001Service.checkBusinessDataInFlow(item)){
                        //流程存在情况
                        return new AjaxResult("交易编号："+item.getTradeId()+"已存在流程中", MessageType.错误消息);
                    }
            }
        }
        return new AjaxResult();
    }

    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Valid TrdBondChangeTradeDto dto, BindingResult br)
    {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        // 启动流程
        ProcessParameter<TrdBondChangeTradeDto> proParam = new ProcessParameter<>();
        WorkFlowParameter<TrdBondChangeTradeDto> params = new WorkFlowParameter<>();

        // 业务代码（凭证流水号）
        params.setBusinessCode(item -> item.getTradeId());
        // 业务类型
        params.setBusinessType(DictCodeListExt2.业务类型_证券转换交易.getValue());
        // 页面id
        params.setPageId("TrdBondChangeTrade002");
        // 元素id
        params.setElementId(dto.getElementId());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("证券转换交易删除：交易编号", entity.getTradeId(),
                "交易日期",DateUtil.format(entity.getTradeDate())
        ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = trdBondChangeTrade001Service.start(proParam, dto.getSelectedItem().toArray(
                    new TrdBondChangeTradeDto[dto.getSelectedItem().size()]));
        }
        catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

        return new AjaxResult(msg);
    }

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
        List<TrdBondChangeTradeDto> listdto;
        if (datas != null) {
            listdto = JsonUtils.json2List(datas, TrdBondChangeTradeDto.class);
        }
        else {
            listdto = trdBondChangeTrade001Service.queryPageList(params);
        }

        File outFile = trdBondChangeTrade001Service.makeFile(
                "/com/joyintech/tams/valuation/trademanage/trdbondchangevaluationtrade/conf/export_conf.xml",
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
