package com.joyintech.tams.business.asset.protect.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.asset.bond.dto.FinBondAssetSaveRequest;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificateDto;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificateInsertRequest;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificateRequest;
import com.joyintech.tams.business.asset.protect.service.CreditProtectionCertificate001Service;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 信用保护凭证标的
 *
 * @author wangshengzhi1
 * @version 1.0 2022年5月09日
 * @see CreditProtectionCertificate001Controller
 * @since 1.0
 */
@Controller
@PageId("CreditProtectionCertificate001")
@RequestMapping("/business/asset/protect/CreditProtectionCertificate001")
@Api(tags = "信用保护凭证标的")
public class CreditProtectionCertificate001Controller extends BaseController {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CreditProtectionCertificate001Service creditProtectionCertificate001Service;

    @ResponseBody
    @PostMapping("/getPageParams")
    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    public Message<Map<String, Object>> getPageParams() {
        Map<String, Object> initPageParams = new HashMap<>();
        //数据字典初始化数据
        initPageParams.put("PROTECT_OBJECT", this.getSelectDataFromDataDic(DictCode.保护对象, null));
        initPageParams.put("SETTLE_METHOD", this.getSelectDataFromDataDic(DictCode.结算方式_标的, null));
        initPageParams.put("FINPROD_TYPE", this.getSelectDataFromDataDic(DictCode.金融产品类型, null));
        initPageParams.put("FINPROD_TYPE_TG", this.getSelectDataFromDataDic(DictCode.金融产品类型_投管用, null));
        initPageParams.put("COUPON_SPECIES", this.getSelectDataFromDataDic(DictCode.息票品种, null));
        initPageParams.put("CCY", this.getSelectDataFromDataDic(DictCode.币种, null));
        initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("RAISE_TYPE", this.getSelectDataFromDataDic(DictCode.募集方式, null));
        initPageParams.put("ISSUE_CLASS", this.getSelectDataFromDataDic(DictCode.新债_发行方式, null));
        initPageParams.put("AMT_INCOME_TYPE", this.getSelectDataFromDataDic(DictCode.本金收益特征, null));
        initPageParams.put("REGIST_ORG", this.getSelectDataFromDataDic(DictCode.登记托管机构, null));
        initPageParams.put("YES_OR_NO", this.getSelectDataFromDataDic(DictCode.是否, null));
        initPageParams.put("PINAN_ASSET_TYPE", this.getSelectDataFromDataDic(DictCode.平安资产类型, null));
        initPageParams.put("CASH_TYPE_1", this.getSelectDataFromDataDic(DictCode.现金流一级类型, null));
        initPageParams.put("CASH_TYPE_2", this.getSelectDataFromDataDic(DictCode.现金流二级类型, null));
        initPageParams.put("FIN_CASH_BUSINESS_TYPE", this.getSelectDataFromDataDic(DictCode.现金流业务类型, null));
        initPageParams.put("BASE_TYPE", this.getSelectDataFromDataDic(DictCode.基数类型, null));
        initPageParams.put("BASE_DATE_TYPE", this.getSelectDataFromDataDic(DictCode.基数日期类型, null));
        initPageParams.put("CASH_CLASS_TYPE", this.getSelectDataFromDataDic(DictCode.现金流类型, null));
        initPageParams.put("CASH_UNIT_TYPE", this.getSelectDataFromDataDic(DictCode.现金流单位, null));
        initPageParams.put("PAY_TYPE", this.getSelectDataFromDataDic(DictCode.支付方式, null));
        initPageParams.put("MARKET_INSTI_TYPE_SCA", this.getSelectDataFromDataDic(DictCode.市场机构机构类型, null));
        initPageParams.put("BASIS", this.getSelectDataFromDataDic(DictCode.计息基础, null));
        //交易日历

        //pageId
        initPageParams.put("pageId", this.getPageId());
        //当前平台日
        initPageParams.put("platDate", DateUtil.platDateFromCache());
        //当前用户
        initPageParams.put("userId", SecurityUtil.getLoginUser().getUserName());
        return Message.success("success", initPageParams);
    }


    /**
     * 初始化查询
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="查询信用保护凭证标的信息",notes = "查询信用保护凭证标的信息")
    @ResponseBody
    @PostMapping("findPage")
    public Message<Page<CreditProtectionCertificateDto>> findPage(@RequestBody CreditProtectionCertificateRequest request) {
        try {
            Page<CreditProtectionCertificateDto> page = creditProtectionCertificate001Service.selectForPage(request);
            return new Message<Page<CreditProtectionCertificateDto>>().success().setData(page);
        }catch (Exception e) {
            logger.error("查询债券标的信息分页数据异常",e);
            return new Message<Page<CreditProtectionCertificateDto>>().fail().setMessage("查询信用保护凭证标的信息数据异常");
        }
    }

    @ResponseBody
    @PostMapping("/finprodIdForSelect")
    @ApiOperation(value = "资产代码下拉列表", notes = "资产代码列表")
    public Message<Page<SelectOptionDto>> finprodIdForSelect(@RequestBody CreditProtectionCertificateRequest request) {
        try {

            Page<SelectOptionDto> page = creditProtectionCertificate001Service.getFinprodIdForSelect(request);
            return new Message<Page<SelectOptionDto>>().success().setData(page);
        }catch (Exception e) {
            logger.error("债券简称下拉框数据异常",e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("资产代码下拉列表");
        }
    }

    @ResponseBody
    @PostMapping("/findForSelect")
    @ApiOperation(value = "参考债券下拉列表", notes = "参考债券下拉列表")
    public Message<Page<SelectOptionDto>> findForSelect(@RequestBody CreditProtectionCertificateRequest request) {
        try {

            Page<SelectOptionDto> page = creditProtectionCertificate001Service.findForSelect(request);
            return new Message<Page<SelectOptionDto>>().success().setData(page);
        }catch (Exception e) {
            logger.error("债券简称下拉框数据异常",e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("参考债券下拉列表");
        }
    }

    /**
     * 删除按钮点击时的check
     *
     * @param dto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "删除按钮点击时的check", notes = "删除按钮点击时的check")
    @ResponseBody
    @PostMapping("check")
    public Message<AjaxResult> check(@RequestBody CreditProtectionCertificateRequest dto, BindingResult result) {
        AjaxResult res= new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if(null!=dto.getSelectedItem()&&!dto.getSelectedItem().isEmpty()) {
            for (CreditProtectionCertificateInsertRequest entity : dto.getSelectedItem()) {
                WflValidInputParameter parameter = new WflValidInputParameter();
                parameter.setPageId("CreditProtectionCertificate002");
                parameter.setBusinessDataPk1(entity.getFinprodMarketId());
                if (creditProtectionCertificate001Service.isInWflProcess(parameter)) {
                    errorList.add(new ValidMessage("", getMessage("所选资产存在流程中的数据！,不可删除!")));
                    res.setErrorList(errorList);
                    res.setMessageType(MessageType.错误消息);
                    return new Message<AjaxResult>().success().setData(res);
                }
                Integer count = creditProtectionCertificate001Service.countFinprodIdUse(entity.getFinprodMarketId());
                if (Objects.nonNull(count) && count > 0) {
                    errorList.add(new ValidMessage("", getMessage("存在流程中/待执行指令交易数据,不可删除!")));
                    res.setErrorList(errorList);
                    res.setMessageType(MessageType.错误消息);
                    return new Message<AjaxResult>().success().setData(res);
                }

            }
        }
        AjaxResult ajaxResult = commonCheck(result, "revoke");
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

    @ResponseBody
    @PostMapping(value = "/export")
    @ApiOperation(value = "信用保护凭证标的列表导出", notes = "信用保护凭证标的列表导出")
    public void export(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        ExcelUitl.doExport(req, response, "信用保护凭证标的", () ->
                creditProtectionCertificate001Service.selectForPage(req.getDto(CreditProtectionCertificateRequest.class)).getRoot()
        );
    }


}
