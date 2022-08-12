package com.joyintech.tams.invest.settle.fundback.controller;

import java.util.HashMap;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeList;
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
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.util.SettleCommonUtil;
import com.joyintech.tams.invest.settle.fundback.dto.FundBackgroundManagement001Dto;
import com.joyintech.tams.invest.settle.fundback.service.FundBackgroundManagement001Service;

/**
 * @program: tams-invest-parent
 * @description: 资金后台管理
 * @author: 强成西
 * @create: 2018-11-15 11:28
 **/
@Controller
@PageId("FundBackgroundManagement001")
@RequestMapping("/invest/settle/fundback/FundBackgroundManagement001")
public class FundBackgroundManagement001Controller extends BaseController {

    @Autowired
    private FundBackgroundManagement001Service fundBackgroundManagement001Service;

    @Autowired
    private IPlatformDate platformDateImpl;

    /**
     * 需要初始化的下拉菜单
     */
 // 一体化对应etliu start
//    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.指令状态, DictCode.交易流程状态,
//        DictCode.平安业务类型, DictCode.划款类型};
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.指令状态, DictCode.交易流程状态,
            DictCode.平安业务类型, DictCode.平安划款类型};
 // 一体化对应etliu end

    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DICT_CODES);
        Map<String, Object> params = new HashMap<>();
        SettleCommonUtil.setParams(params);
        model.addAllAttributes(params);
        model.addAttribute("PROFIT_TYPE02", BussinessDictCodeList.收益类型_预期收益.getValue());
        String sysDate = platformDateImpl.getSysDateStr();
        model.addAttribute("cashDate", sysDate + " - " + sysDate);
        model.addAttribute("PINAN_ASSET_TYPE_F01", DictCodeList.平安资产类型_债券.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F07", DictCodeList.平安资产类型_质押式回购.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F32", BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        return super.init(model);
    }

    /**
     * 获取所有组合代码
     *
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPortfolioId")
    public Page<SelectOptionDto> findPortfolioId() {
        return fundBackgroundManagement001Service.findPortfolioId();
    }

    /**
     * 获取所有产品资产代码
     *
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findBizCode")
    public Page<SelectOptionDto> findBizCode() {
        return fundBackgroundManagement001Service.findBizCode();
    }

    /**
     * 获取所有满足条件的数据
     *
     * @return grid集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<FundBackgroundManagement001Dto> findPage() {
        return fundBackgroundManagement001Service.findPage(RequestUtil.getPageParam());
    }

    /**
     * 查询工作流相关信息
     *
     * @return Map
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findWorkflowInfo")
    public Map<String, Object> findWorkflowInfo() {
        return fundBackgroundManagement001Service.findWorkflowInfo();
    }

    /**
     * 获取所有满足条件的数据
     *
     * @return grid集合
     * @see
     * @since 1.0
     */
    @RequestMapping("printPayOrders")
    public String printPayOrders(Model model) {
        return fundBackgroundManagement001Service.printPayOrders(model);
    }

    /**
     * 导出
     *
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export()
         {
        return fundBackgroundManagement001Service.export(getPageId(), RequestUtil.getParameters(getRequest()));
    }
}
