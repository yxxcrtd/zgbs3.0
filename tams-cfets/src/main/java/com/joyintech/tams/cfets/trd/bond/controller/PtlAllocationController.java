package com.joyintech.tams.cfets.trd.bond.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 组合分配
 * @author zznn
 * @date 2022/1/18
 */
@Controller
@PageId("PtlAllocation")
@RequestMapping("/cfets/bond/PtlAllocation")
public class PtlAllocationController extends BaseController {

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.分配方式, BussinessDictCode.清算速度,DictCode.业务模式);
        model.addAttribute("entity",this.getParam("entity"));
        model.addAttribute("quoteType", this.getParam("quoteType"));
        model.addAttribute("quotaPageModel", this.getParam("quotaPageModel"));
        model.addAttribute("distribution",this.getParam("distribution"));
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //卖空模式
        model.addAttribute("sellPara",CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
        model.addAttribute("ps",this.getParam("ps"));
        return super.init(model);
    }
}

