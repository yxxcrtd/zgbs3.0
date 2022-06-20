/*
 * 文件名：SppiTest001Controller
 * 版权：Copyright 2017-2020 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Jiaoxujin
 * 修改时间：2020-10-09
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.invest.asset.sppi.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.asset.sppi.dto.SppiTemplateDto;
import com.joyintech.tams.invest.asset.sppi.dto.SppiTest001Dto;
import com.joyintech.tams.invest.asset.sppi.service.SppiTest001Service;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * SPPI测试查询控制类
 *
 * @author Jiaoxujin
 * @version 1.0 2020-10-09
 */
@PageId("SppiTest001")
@Controller
@RequestMapping("/invest/asset/sppi/SppiTest001")
public class SppiTest001Controller extends BaseController {
    @Autowired
    private SppiTest001Service service;

    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        setSelectOptionToModel(model, DictCode.SPPI测试状态);

        SelectOptionDto bondSelectDto = new SelectOptionDto();
        SelectOptionDto projectSelectDto = new SelectOptionDto();
        SelectOptionDto planSelectDto = new SelectOptionDto();
        SelectOptionDto firstBidSelectDto = new SelectOptionDto();
        SelectOptionDto firstBidSelectDto1 = new SelectOptionDto();

        bondSelectDto.setId(DictCodeList.平安资产类型_债券.getValue());
        bondSelectDto.setText(DictCodeList.平安资产类型_债券.getCodeName());
        projectSelectDto.setId(DictCodeList.平安资产类型_项目类资产.getValue());
        projectSelectDto.setText(DictCodeList.平安资产类型_项目类资产.getCodeName());
        planSelectDto.setId(DictCodeList.平安资产类型_计划类资产_利率型.getValue());
        planSelectDto.setText(DictCodeList.平安资产类型_计划类资产_利率型.getCodeName());
        firstBidSelectDto.setId(DictCodeList.平安资产类型_一级市场债.getValue());
        firstBidSelectDto.setText(DictCodeList.平安资产类型_一级市场债.getCodeName());

        firstBidSelectDto1.setId(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        firstBidSelectDto1.setText(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getCodeName());
        List<SelectOptionDto> assetTypeList = Arrays.asList(bondSelectDto, projectSelectDto, planSelectDto, firstBidSelectDto, firstBidSelectDto1);
        model.addAttribute("ASSET_TYPES", JsonUtils.objectArray2Json(assetTypeList.toArray()));

        model.addAttribute("SPPI_TESTING", DictCodeListExt3.SPPI测试状态_测试中.getValue());
        //model.addAttribute("SPPI_BACKED", DictCodeListExt3.SPPI测试状态_已退回.getValue());
        model.addAttribute("SPPI_PASSED", DictCodeListExt3.SPPI测试状态_测试通过.getValue());
        model.addAttribute("SPPI_FAILD", DictCodeListExt3.SPPI测试状态_测试不通过.getValue());

        return super.init(model);
    }

    /*
     * 分页查询方法
     */
    @ResponseBody
    @RequestMapping("findOnePage")
    public Page<SppiTest001Dto> selectForPage() {
        Map<String, Object> map = RequestUtil.getParameters(getRequest());
        return service.findPage(map);
    }

    /**
     * 查询适用于SPPI测试的资产标的列表<br />
     * 仅能查询出债券类资产、项目类资产、计划类利率型资产
     *
     * @param paramMap Map&lt;String, Object&gt;
     * @return List&lt;SelectOptionDto&gt;
     */
    @ResponseBody
    @RequestMapping("querySppiAssetList")
    public List<SelectOptionDto> querySppiAssetList(@RequestParam Map<String, Object> paramMap) {
        return service.querySppiAssetList(paramMap);
    }

    /**
     * 导出excel
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export() {
        return service.export(this.getPageId(), RequestUtil.getParameters(getRequest()));
    }

    @RequestMapping("/queryValidTradeCount")
    @ResponseBody
    public AjaxResult queryValidTradeCount() throws Exception {
        AjaxResult result = new AjaxResult();

        String finprodId = getParam("finprodId");
        int count = service.queryValidTradeCount(finprodId);
        if (count > 0) {
            result.setMessage(getMessage("FIN_ASSET_SPPI_003"), MessageType.警告消息);
        }

        return result;
    }

    /**
     * 导入数据文件之前检查数据
     *
     * @param fileName 导入的文件名
     * @return 处理结果
     */
    @ResponseBody
    @RequestMapping(value = "checkConfirmMsgForImportData")
    public AjaxResult checkConfirmMsgForImportData(String fileName) {
        return service.checkConfirmMsgForImportData(fileName);
    }

    /**
     * 导入数据文件
     *
     * @param fileName 导入的文件名
     * @return 导入结果，包含验证内容，如果验证失败，返回message
     */
    @ResponseBody
    @RequestMapping("/importFile")
    public List<ExcelValidDto> importData(String fileName) {
        return this.service.importData(fileName);
    }
}
