package com.joyintech.tams.cfets.etr.repo.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoLibAvaPledgeBondResponse;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoLibDto;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrRepoLibAvaPledgeBondRequest;
import com.joyintech.tams.cfets.etr.repo.service.CfetsEtrIbRepoinfoService;
import com.joyintech.tams.cfets.etr.repo.service.CfetsIbRepoLibService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@PageId("CfetsEtrIbRepoLib")
@RequestMapping("/cfets/etrib/cfetsEtrIbRepoLib")
@Controller
@Api(tags = "银行间回购质押券库查询")
public class CfetsEtrIbRepoLibController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(CfetsEtrIbRepoinfoController.class);

	@Autowired
	private CfetsIbRepoLibService cfetsIbRepoLibService;

    @Autowired
    private CfetsEtrIbRepoinfoService cfetsEtrIbRepoinfoService;
	
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 根据画面条件检索可质押券信息
     *
     * @return List<TrdStandardBond002Dto>
     */
    @PostMapping("selectAvaPledgeBondList")
    @ResponseBody
    @ApiOperation(value = "根据画面条件检索可质押券信息", notes = "根据画面条件检索可质押券信息")
    public Message<Page<CfetsEtrIbRepoLibAvaPledgeBondResponse>> selectAvaPledgeBondList(@RequestBody CfetsEtrRepoLibAvaPledgeBondRequest dto) {
        try {
            Page<CfetsEtrIbRepoLibAvaPledgeBondResponse> list = cfetsEtrIbRepoinfoService.selectAvaPledgeBondList(dto);
            return new Message<Page<CfetsEtrIbRepoLibAvaPledgeBondResponse>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<CfetsEtrIbRepoLibAvaPledgeBondResponse>>().fail().setMessage("查询异常");
        }
    }



    /**
     * 根据画面条件检索已入库债券信息
     *
     * @return List<TrdStandardBond002Dto>
     */
    @PostMapping("selectPledgeBondList")
    @ResponseBody
    @ApiOperation(value = "根据画面条件检索已入库债券信息", notes = "根据画面条件检索已入库债券信息")
    public Message<List<CfetsEtrIbRepoLibAvaPledgeBondResponse>> selectPledgeBondList(@RequestBody CfetsEtrRepoLibAvaPledgeBondRequest cfetsEtrRepoZLCXPositionRequest) {
        try {
            List<CfetsEtrIbRepoLibAvaPledgeBondResponse> list = cfetsIbRepoLibService.selectPledgeBondList(cfetsEtrRepoZLCXPositionRequest);
            return new Message<List<CfetsEtrIbRepoLibAvaPledgeBondResponse>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<List<CfetsEtrIbRepoLibAvaPledgeBondResponse>>().fail().setMessage("查询异常");
        }
    }
    
    /**
     * 保存处理
     *
     * @param dto
     * @return
     * @throws Exception
     */
    @PostMapping("check")
    @ResponseBody
    @ApiOperation(value = "保存check", notes = "保存check")
    public Message<AjaxResult> check(@Validated({ValidateGroupEdit.class}) @RequestBody CfetsEtrIbRepoLibDto dto, BindingResult result)
            throws Exception {
        return new Message<AjaxResult>().setData(commonCheck(result, null));
    }

    /**
     * 保存处理
     *
     * @param dto
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("save")
    @ApiOperation(value = "保存处理", notes = "保存处理")
    public Message<AjaxResult> save(@Validated({ValidateGroupEdit.class}) @RequestBody CfetsEtrIbRepoLibDto dto, BindingResult result)
            throws Exception {
        if (result != null && result.hasErrors()) {
            return new Message<AjaxResult>().fail().setData(commonCheck(result, null));
        }
        AjaxResult ajaxResult = cfetsIbRepoLibService.inSave(dto);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }
}
