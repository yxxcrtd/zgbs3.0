package com.joyintech.tams.business.common.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.tams.constant.DictCodeList;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 文件上传辅助画面（导入）
 * @author etliu
 * @version 1.0 2022年03月01日
 * @see FileUploadHelperController
 * @since 1.0
 */
@Controller
@PageId("FileUploadHelper")
@RequestMapping("/common/FileUploadHelper")
@Api(tags = "文件上传共通（导入）")
public class FileUploadHelperController extends BaseController {

    @Override
    public String init(Model model) {
        model.addAttribute("importPageId", this.getParam("importPageId"));
        model.addAttribute("SHARES_TRADE_PLATFORM_02", DictCodeList.交易平台_竞价撮合平台.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_03", DictCodeList.交易平台_固定收益平台.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_04", DictCodeList.交易平台_大宗交易平台.getValue());

        return "common/FileUploadHelper";
    }
}
