package com.joyintech.tams.invest.asset.issue.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.asset.bond.dto.FinBondTargetAssetDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 债券标的（新）查询
 *
 * @author zhuys
 * @version 1.0 2022年04月11日
 * @see FinProductIssue001Controller
 * @since 1.0
 */
@PageId("FinProductIssue001")
@Controller
@RequestMapping("/invest/asset/issue/FinProductIssue001")
public class FinProductIssue001Controller extends BaseController {

    /**
     * 初始化数据字典下拉框
     */
    private static final IDictCode[] DICT_CODES = new DictCode[]{DictCode.息票品种, DictCode.利率类型,
            // 新增行内五级分类 wangguiqi 2019-07-23 start
            DictCode.行内资产二级分类, DictCode.行内资产三级分类, DictCode.行内资产四级分类, DictCode.行内资产五级分类,
            // 新增行内五级分类 wangguiqi 2019-07-23 end
            DictCode.WIND一级分类_含债券一级分类等, DictCode.WIND二级分类_含债券二级分类等, DictCode.是否, DictCode.币种};

    @Override
    public String init(Model model) {
        //初始化数据字典下拉框
        setSelectOptionToModel(model, DICT_CODES);
        model.addAttribute("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE") == null ? "01" : CacheUtil.getSysParam("IS_SHOW_TYPE"));
        //是否新增债券查询条件添加004控制  modify by liupeilin 2020-01-15  begin
        model.addAttribute("YES", DictCodeList.是否_是.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        if ("004".equals(CacheUtil.getSysParam("CUSTOMER_BANK_CODE"))) {
            model.addAttribute("BANK_CODE", "004");
        }
        //是否新增债券查询条件添加004控制  modify by liupeilin 2020-01-15  end
        return super.init(model);
    }


    /**
     * 初始化查询
     *
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<FinBondTargetAssetDto> selectAll() {
        return null;
    }
}
