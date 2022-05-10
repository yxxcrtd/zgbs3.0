package com.joyintech.tams.business.asset.protect.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificateDto;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificateRequest;
import com.joyintech.tams.business.asset.protect.service.CreditProtectionCertificate001Service;
import com.joyintech.tams.business.common.pojo.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
public class CreditProtectionCertificate001Controller {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CreditProtectionCertificate001Service creditProtectionCertificate001Service;
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
            return new Message<Page<CreditProtectionCertificateDto>>().fail().setMessage("查询信用保护凭证标的信息");
        }
    }


}
