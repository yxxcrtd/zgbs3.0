package com.joyintech.tams.invest.riskredis.quota.portfolioindicator.controller;

import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.tams.invest.riskredis.quota.portfolioindicator.dto.RskPtlPortfolioDto;
import com.joyintech.tams.invest.riskredis.quota.portfolioindicator.service.RskSupervisionPortfPoint005Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.RequestUtil;


/**
 * 查询组合成分
 * @author 裴家俊
 * @version 1.0 2019年03月18日
 * @see RskSupervisionPortfPoint005Controller
 * @since 1.0
 */
@Controller
@PageId("RskSupervisionPortfPoint005")
@RequestMapping("/invest/riskredis/quota/portfolioindicator/RskSupervisionPortfPoint005")
public class RskSupervisionPortfPoint005Controller extends BaseController {
	
	/**
     * RskSupervisionPortfPoint005Service
     */
    @Autowired(required = false)
    private RskSupervisionPortfPoint005Service rskSupervisionPortfPoint005Service;

    /**
     * 初始化的查询条件
     */
   private static final IDictCode[] DICT_CODES = new BussinessDictCodeRds[] {BussinessDictCodeRds.收益类型,BussinessDictCodeRds.开放类型,
		   BussinessDictCodeRds.募集方式, BussinessDictCodeRds.投资性质,BussinessDictCodeRds.组合类型
	       };
    @Override
    public String init(Model model) {
    	this.setSelectOptionToModel(model, DICT_CODES);
    	model.addAttribute("ptlBagId", getParam("ptlBagId"));
    	model.addAttribute("ptlBagName", getParam("ptlBagName"));
        model.addAttribute("isRule", getParam("isRule"));
        return super.init(model);
    }
   
    /**
     * 画面查询方法
     * @return 所有数据
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<RskPtlPortfolioDto> selectAll()
         {
        return rskSupervisionPortfPoint005Service.selectForPage(
            RequestUtil.getParameters(getRequest()));
    }
    
    /**
     * 导出信息excel
     * @return excel
     * @throws Exception 异常
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export()
         {
        return rskSupervisionPortfPoint005Service.export(this.getPageId(),
            RequestUtil.getParameters(getRequest()));

    }
}
