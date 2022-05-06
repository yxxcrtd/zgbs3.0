/*
 * 文件名：ValManualVoucher002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：weipeng
 * 修改时间：2018年5月7日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.dto.ValManualVoucher001Dto;
import com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.dto.ValManualVoucher002Dto;
import com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.service.ValManualVoucher001Service;
import com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.service.ValManualVoucher002Service;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板示例<br>
 * 手工凭证详情查看
 * 
 * @author weipeng
 * @version 2.0 2018年5月3日
 * @see ValManualVoucher002Controller
 * @since 2.0
 */
@Controller
@PageId("ValManualVoucher002")
@RequestMapping("/valuation/accounadjustmanage/manualvoucher/ValManualVoucher002")
public class ValManualVoucher002Controller extends BaseController {

    /**
     * ValManualVoucher002Service
     */
    @Autowired
    private ValManualVoucher002Service valManualVoucher002Service;
    
    /**
     * ValManualVoucher001Service
     */
    @Autowired
    private ValManualVoucher001Service valManualVoucher001Service;

    /**
     * 重写init方法，加载页面 {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.会计场景代码, DictCode.币种, DictCode.冲正标识,
                DictCode.借贷方向, DictCode.投资目的, DictCode.分录类型, DictCode.金融产品类型 ,DictCode.记账顺序);
        // 获取业务类型
        // 获取 金融产品类型 数据字典
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.金融产品类型, null);
        List<SelectOptionDto> options = selectDto.getOptions();
        // 获取 现金流二级类型 数据字典
        selectDto = this.getSelectDataFromDataDic(DictCode.现金流二级类型, null);
        options.addAll(selectDto.getOptions());
        // 获取 资金账户类型 数据字典
        selectDto = this.getSelectDataFromDataDic(DictCode.资金账户类型, null);
        options.addAll(selectDto.getOptions());
        // 获取 一次性费用类型 数据字典
        selectDto = this.getSelectDataFromDataDic(DictCode.一次性费用类型, null);
        options.addAll(selectDto.getOptions());
        // 4个数据字典的全部放入一个下拉框中
        model.addAttribute("busiType", JsonUtils.objectArray2Json(options.toArray()));
        model.addAttribute("QTYSK_BJ", DictCodeBaseSubjectNatrue.其他应收款_本金.getValue());
        model.addAttribute("QTYSK_LX", DictCodeBaseSubjectNatrue.其他应收款_利息.getValue());
        model.addAttribute("FINPROD_TYPE_F35", DictCodeListExt3.金融产品类型_期权.getValue());

        // 获取 投资目的 数据字典
        selectDto = this.getSelectDataFromDataDic(DictCode.投资目的, null);
        options = selectDto.getOptions();
        List<SelectOptionDto> invOptions = new ArrayList<>();
        // 取债券相关投资目的
        for (SelectOptionDto item : options) {
            if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(item.getId())
                    || DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(item.getId())
                    || DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(item.getId())) {
                invOptions.add(item);
            }
        }
        model.addAttribute("INV_AIM", JsonUtils.objectArray2Json(invOptions.toArray()));
        //String tradeId = this.getParam("tradeId");
        String seqno = this.getParam("seqno");
        //model.addAttribute("tradeId", tradeId);
        
        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);
        if(StringUtil.isNotEmpty(workflowId)){
            ValManualVoucher001Dto entity= valManualVoucher001Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("offsetFlag", entity.getOffsetFlag());
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("sysDate", entity.getBookDate());
            model.addAttribute("finprodType",entity.getTableId());
        }else{
            ValManualVoucher001Dto entity = valManualVoucher002Service.getBySeqno(seqno);

            model.addAttribute("entity", entity);
            // 冲销标志默认值
            model.addAttribute("offsetFlag", entity.getOffsetFlag());
            // 借贷方向设一个默认值
            model.addAttribute("cdFlag", entity.getCdFlag());
            // 入账日期默认为系统平台日
            model.addAttribute("sysDate", entity.getBookDate());
        }

        return super.init(model);
    }

    /**
     * 根据手工凭证编号bookId获取对应相同编号的所有数据
     *
     * @return 记账手工凭证数据
     * @throws Exception
     *             异常
     * @see
     */
    @RequestMapping("searchList")
    @ResponseBody
    public List<ValManualVoucher002Dto> searchList() {
        List<ValManualVoucher002Dto> returnList = new ArrayList<>();
        // 手工凭证编号
        String seqno = this.getParam("seqno");
        String workflowId = this.getParam("workflowId");
        //根据流程id获取对应的凭证新建时的分录信息
        if(workflowId!=null && workflowId.length()>0){
            ValManualVoucher001Dto entity= valManualVoucher001Service.getBussinessEntityByWorkflowId(workflowId);
            returnList = entity.getListAddDto();
        }
        if(!StringUtil.isNotEmpty(workflowId) && seqno!=null && seqno.length()>0){
            returnList=valManualVoucher002Service.searchList(seqno);
        }
        return returnList;
    }
    
}
