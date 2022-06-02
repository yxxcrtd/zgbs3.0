package com.joyintech.tams.cfets.manualsettle.controller;


import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.cfets.manualsettle.dto.*;
import com.joyintech.tams.cfets.manualsettle.service.CfetsManualSettleQueryService;
import com.joyintech.tams.cfets.trdquery.dto.CfetsTrdQueryTradeInfoListResponse;
import com.joyintech.tams.cfets.trdquery.service.CfetsTrdQueryService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/cfets/manualsettle/ManualSettleQuery")
@PageId("cfetsManualSettleQuery")
@Api(tags = "银行间手工交收查询")
public class CfetsManualSettleQueryController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CfetsManualSettleQueryController.class);

    @Autowired
    private CfetsManualSettleQueryService cfetsManualSettleQueryService;

    @Autowired
    private CfetsTrdQueryService cfetsTrdQueryService;

    @Autowired
    private IPlatformDate platformDateImpl;

    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams(){
        Map<String, Object> initPageParams = new HashMap<>();
        //数据字典初始化数据
        //资产类型
        initPageParams.put("PINAN_ASSET_TYPE", this.getSelectDataFromDataDic(DictCode.平安资产类型, null));
        //业务类型
        initPageParams.put("BUSI_TYPE_POSITION", this.getSelectDataFromDataDic(DictCode.头寸业务类型, null));
        //交易方向
        initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
        //清算状态
        initPageParams.put("SETTLE_STATUS", this.getSelectDataFromDataDic(DictCode.清算状态, null));
        //结算指令状态
        initPageParams.put("CLEAN_STL", this.getSelectDataFromDataDic(DictCode.结算指令状态, null));
        //结算合同状态
        initPageParams.put("CLEAN_ARG", this.getSelectDataFromDataDic(DictCode.结算合同状态, null));

        //自动刷新时间(ms)
        initPageParams.put("cfetsManualSettle_tradeList_refresh_period", CacheUtil.getSysParam("cfetsManualSettle_tradeList_refresh_period"));

        // 平台日字符串格式
        initPageParams.put("SESSION_SYS_DATE", DateUtil.format(platformDateImpl.getSysDate()));

        initPageParams.put("CUSTOM_ASSET_TYPE_TWO", this.getSelectDataFromDataDic(DictCode.行内资产二级分类, null));

        initPageParams.put("pageId", "cfetsManualSettleQuery");
        return new Message<Map<String, Object>>(initPageParams).success();
    }

    /**
     * 银行间手工交收分页查询
     *
     * @param dto
     * @return Page<CfetsManualSettleTradeListRespond>
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "银行间手工交收分页查询", notes = "银行间手工交收分页查询")
    @PostMapping("/findPage")
    @ResponseBody
    public Message<Page<CfetsManualSettleTradeListRespond>> findPage(@RequestBody CfetsManualSettleTradeListRequest dto) {
        try {
            Page<CfetsManualSettleTradeListRespond> page = cfetsManualSettleQueryService.findPage(dto);
            return new Message<Page<CfetsManualSettleTradeListRespond>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询指令管理分页数据异常", e);
            return new Message<Page<CfetsManualSettleTradeListRespond>>().fail().setMessage("查询银行间手工交收分页数据异常");
        }
    }

    @PostMapping(value = "/exportManualSettleTradeList")
    @ResponseBody
    @ApiOperation(value = "手工交收列表导出", notes = "手工交收列表导出")
    public void exportEtrDispatcherList(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "手工交收", () -> {
            List dataList = cfetsManualSettleQueryService.findPage(req.getDto(CfetsManualSettleTradeListRequest.class)).getRoot();
            return dataList;
        });
    }

    /**
     * 交收流水列表分页查询
     *
     * @param dto
     * @return Page<CfetsManualSettleFlowRespond>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "交收流水列表分页查询", notes = "交收流水列表分页查询")
    @PostMapping("/findManualSettleFlow")
    @ResponseBody
    public Message<Page<CfetsManualSettleFlowRespond>> findManualSettleFlow(@RequestBody CfetsManualSettleCommonRequest dto)
            throws Exception {
        try {
            Page<CfetsManualSettleFlowRespond> page = cfetsManualSettleQueryService.findManualSettleFlow(dto);
            return new Message<Page<CfetsManualSettleFlowRespond>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询交收流水列表数据异常", e);
            return new Message<Page<CfetsManualSettleFlowRespond>>().fail().setMessage("查询交收流水列表数据异常");
        }
    }

    @ApiOperation(value = "只读表单设置及数据查询_成交信息查询", notes = "只读表单设置及数据查询_成交信息查询")
    @PostMapping("/findManualSettleInfo/{pageCode}/{dtCode}/{spcdCode}")
    @ResponseBody
    public Message<Map<String,Object>> findManualSettleInfo(@PathVariable("pageCode") String pageCode,
                                                              @PathVariable("dtCode") String dtCode,
                                                              @PathVariable("spcdCode") String spcdCode,
                                                              @RequestBody CfetsManualSettleCommonRequest dto) throws Exception {
        try {
            Map<String,Object> resultData = new HashMap<>();
            CfetsManualSettleDto settleDto = dto.getSelectedItem().get(0);
            if ((DictCodeListExt4.投标方式_分销买入.getValue().equals(settleDto.getBidModle())
                    || DictCodeListExt4.投标方式_自主投标.getValue().equals(settleDto.getBidModle())
                    || StringUtil.equals(settleDto.getTgBusiType(), DictCodeListExt3.头寸业务类型_债券一级申购.getValue())
                    && !StringUtil.equals("01", dto.getPageFrom()))) {
                CfetsTrdQueryTradeInfoListResponse response = new CfetsTrdQueryTradeInfoListResponse();
                BeanUtil.copyPropertiesIgnoreNull(response,settleDto);
                resultData =  cfetsTrdQueryService.selectManualSettleInfo(pageCode, dtCode, spcdCode, response);
            }else {
                resultData = cfetsManualSettleQueryService.findManualSettleInfo(pageCode, dtCode, spcdCode, dto);
            }
            return new Message<Map<String,Object>>().success().setData(resultData);
        } catch (Exception e) {
            logger.error("查询成交信息数据异常", e);
            return new Message<Map<String,Object>>().fail().setMessage("查询成交信息数据异常");
        }
    }
}
