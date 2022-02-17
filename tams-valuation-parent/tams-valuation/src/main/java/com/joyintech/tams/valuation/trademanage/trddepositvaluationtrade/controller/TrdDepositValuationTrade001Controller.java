/*
 * 文件名：TrdDepositInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;

import com.joyintech.tams.valuation.common.service.MstWorkflowCommonService;
import com.joyintech.tams.valuation.trademanage.bond.dto.TrdBondTrade001Dto;
import com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.dto.TrdDepositValuationTrade001Dto;
import com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.service.TrdDepositValuationTrade001Service;
import com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.service.TrdDepositValuationTrade002Service;

import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanValuationTradeDto;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
 * 存款交易查询
 * @author fanshuanghu
 * @version 1.0 2018年10月10日
 * @see TrdDepositValuationTrade001Controller
 * @since 1.0
 */
@Controller
@PageId("TrdDepositValuationTrade001")
@RequestMapping("/valuation/trademanage/trddepositvaluationtrade/TrdDepositValuationTrade001")
public class TrdDepositValuationTrade001Controller extends BaseController {
    
        /*
         * 注入service
         */
        @Autowired
        private TrdDepositValuationTrade001Service trdDepositValuationTrade001Service;
        
        @Autowired
        private TrdDepositValuationTrade002Service trdDepositValuationTrade002Service;
//    /**
//     * 通用Mapper共同代理方法
//     */
//    @Autowired(required = false)
//    private BussCommonMapperDelegateService bussCommonMapperDelegateService;



    /**
     * 流程组件
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;
    
    @Autowired
    private TrdDepositValuationTrade002Controller trade002Controller;
        /**
         * 初始化方法
         * {@inheritDoc}
         */
        @Override
        public String init(Model model) {
            this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.交易流程状态, DictCode.存款类资产类型
            		, DictCode.行内资产二级分类, DictCode.行内资产三级分类, DictCode.行内资产四级分类, DictCode.行内资产五级分类);//新增资产二、三、四级分类 王贵奇 2019/08/01
            model.addAttribute("TRADE_STATUS", DictCodeListExt1.交易流程状态_有效.getValue());
            
            model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01": CacheUtil.getSysParam("IS_SHOW_TYPE"));
            
            return super.init(model);
        }


    /**
     * @Description:页面查询，表格分页
     * @return:
     */
//    @ResponseBody
//    @RequestMapping("findPage")
//    public Page<TrdDepositValuationTrade001Dto> findPage()
//            throws Exception {
//        Page<TrdDepositValuationTrade001Dto> page = trdDepositValuationTrade001Service.findPage(
//                RequestUtil.getParameters(getRequest()));
//        return page;
//    }


    @RequestMapping("findPage")
    @ResponseBody
    public Page<TrdDepositValuationTrade001Dto> findPage()
            throws Exception {
                Page<TrdDepositValuationTrade001Dto> page = trdDepositValuationTrade001Service.findPage(
                RequestUtil.getParameters(getRequest()));
                return page;
    }


        
        /**
         * 删除按钮点击时的check
         * @param dto 验证的实体类
         * @param result 验证的结果
         * @return 错误信息
         */
        @ResponseBody
        @RequestMapping("check")
        public AjaxResult check(@RequestBody @Validated({
            ValidateGroupDelete.class}) TrdDepositValuationTrade001Dto dto, BindingResult result) {
            return commonCheck(result, dto.getOperateType());
        }

    /**
     * 导出数据
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export()
            throws ParseException, IOException {

        String datas = this.getParam("datas");
        Map<String, Object> params = RequestUtil.getPageParam();
        List<TrdDepositValuationTrade001Dto> listdto;
        if (datas != null) {
            listdto = JsonUtils.json2List(datas, TrdDepositValuationTrade001Dto.class);
        } else {
            listdto = trdDepositValuationTrade001Service.queryPageList(params);
        }
        File outFile = trdDepositValuationTrade001Service.makeFile(
                "/com/joyintech/tams/valuation/trademanage/trddepositvaluationtrade/conf/export_conf.xml",
                listdto);
        return ExportUtil.export(
                this.getMessage(this.getPageId()) + SystemGlobalConstant.OPEN_PARENTHESIS
                        + DateUtil.format(new Date())
                        + SystemGlobalConstant.CLOSE_PARENTHESIS
                        + SystemGlobalConstant.POINT
                        + FileNameUtil.getFileExtName(outFile),
                outFile, true);
    }



    //删除
    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Valid TrdDepositValuationTrade001Dto dto, BindingResult br)
    {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // 启动流程
        ProcessParameter<TrdDepositValuationTrade001Dto> proParam = new ProcessParameter<>();
        WorkFlowParameter<TrdDepositValuationTrade001Dto> params = new WorkFlowParameter<>();

        // 业务代码（凭证流水号）
        params.setBusinessCode(item -> item.getTradeId());
        // 业务类型
        params.setBusinessType(DictCodeList.业务类型_银行间债券交易.getValue());
        // 页面id
        params.setPageId("TrdDepositValuationTrade002");
        // 元素id
        params.setElementId(dto.getElementId());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("存款类资产查询删除：", entity.getTradeId(),
                DateUtil.format(entity.getTradeDate(),DateUtil.DEFAULT_DATETIME_FORMAT)
        ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = trdDepositValuationTrade001Service.start(proParam, dto.getSelectedItem().toArray(
                    new TrdDepositValuationTrade001Dto[dto.getSelectedItem().size()]));
        }
        catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

        return new AjaxResult(msg);
    }


}

