package com.joyintech.tams.invest.bankuplink.bond.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.bankuplink.bond.service.CfetsBondDealQuotedPriceSingleService;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@PageId("CfetsBondDealQuotedPriceSingle")
@RequestMapping("/invest/bankuplink/bond/CfetsBondDealQuotedPriceSingle")
public class CfetsBondDealQuotedPriceSingleController extends BaseController {

    @Autowired
    private CfetsBondDealQuotedPriceSingleService cfetsBondDealQuotedPriceSingleService;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, BussinessDictCode.交易方向, BussinessDictCode.清算速度,
                BussinessDictCode.结算方式, BussinessDictCode.清算类型, BussinessDictCode.币种, DictCode.业务模式);
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数
        return super.init(model);
    }

    @ResponseBody
    @RequestMapping("/getfinprodIdForAdd")
    public Page<SelectOptionDto> getfinprodIdForAdd()
            throws Exception {
        Page<SelectOptionDto> temp = cfetsBondDealQuotedPriceSingleService.getfinprodIdForAdd(RequestUtil.getPageParam());
        return temp;
    }
}
