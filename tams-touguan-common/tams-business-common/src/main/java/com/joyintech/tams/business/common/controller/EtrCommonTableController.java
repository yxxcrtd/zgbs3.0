package com.joyintech.tams.business.common.controller;

/**
 * 共通查询tab页
 * @author msk
 * @version 1.0 2021年12月27日
 * @see FieldEtrDispatcherController
 * @since 1.0
 */


import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.business.common.dto.CommonSearchInfoRequest;
import com.joyintech.tams.business.common.dto.CommonSearchQueryWorkflowRequest;
import com.joyintech.tams.business.common.dto.CommonSearchQueryWorkflowResponse;
import com.joyintech.tams.business.common.dto.CommonSearchRskInfoResponse;
import com.joyintech.tams.business.common.dto.CommonSearchOperateListRequest;
import com.joyintech.tams.business.common.dto.CommonSearchInfoOperateListResponse;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.EtrCommonTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/common/commonSearchTab")
@Api(tags = "共通查询tab页")
public class EtrCommonTableController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(EtrCommonTableController.class);

    @Autowired
    private EtrCommonTableService etrCommonTableService;
  
    /**
     * 指令风险试算查询
     *
     * @param dto
     * @return Page<CommonSearchRskInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "风险试算查询", notes = "风险试算查询")
    @PostMapping("/getCommonSearchTabRskInfo")
    @ResponseBody
    public Message<Page<CommonSearchRskInfoResponse>> getCommonSearchTabRskInfo(@RequestBody CommonSearchInfoRequest dto){
        try {
            Page<CommonSearchRskInfoResponse> page = etrCommonTableService.getCommonSearchTabRskInfo(dto);
            return new Message<Page<CommonSearchRskInfoResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询风险试算分页数据异常", e);
            return new Message<Page<CommonSearchRskInfoResponse>>().fail().setMessage("查询风险试算异常");
        }
    }

    /**
     * 操作记录查询
     *
     * @param dto
     * @return Page<FieldEtrInfoOperateListResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "操作记录查询", notes = "操作记录查询")
    @PostMapping("/getCommonSearchTabInfoQueryOperate")
    @ResponseBody
    public Message<Page<CommonSearchInfoOperateListResponse>> getEtrCommonTabInfoQueryOperate(@RequestBody CommonSearchOperateListRequest dto){
        try {
            Page<CommonSearchInfoOperateListResponse> page = etrCommonTableService.getCommonSearchTabInfoQueryOperate(dto);
            return new Message<Page<CommonSearchInfoOperateListResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询操作记录分页数据异常", e);
            return new Message<Page<CommonSearchInfoOperateListResponse>>().fail().setMessage("查询操作记录分页数据异常");
        }
    }

    @ApiOperation(value = "审批记录分页查询", notes = "审批记录分页查询")
    @PostMapping("/queryAllWorkFlowInfoList")
    @ResponseBody
    public Message<Page<CommonSearchQueryWorkflowResponse>> queryAllWorkFlowInfoList(@RequestBody CommonSearchQueryWorkflowRequest dto){
        try {
            Page<CommonSearchQueryWorkflowResponse> page = etrCommonTableService.queryAllWorkFlowInfoList(dto);
            return new Message<Page<CommonSearchQueryWorkflowResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询审批记录分页查询数据异常", e);
            return new Message<Page<CommonSearchQueryWorkflowResponse>>().fail().setMessage("查询审批记录分页查询数据异常");
        }
    }
}
