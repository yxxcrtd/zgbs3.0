/**
 * 场外指令分发
 *
 * @author sjj
 * @version 1.0 2021年10月26日
 * @see com.joyintech.tams.cfets.etrdispatcher.controller.CfetsEtrDispatcherController
 * @since 1.0
 */

package com.joyintech.tams.cfets.etrdispatcher.controller;


import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.dto.CommonSearchInfoRequest;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.cfets.etrdispatcher.dto.*;
import com.joyintech.tams.cfets.etrdispatcher.service.CfetsEtrDispatcherService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@PageId("CfetsEtrDispatcher")
@RequestMapping("/cfets/etrdispatcher/CfetsEtrDispatcher")
@Api(tags = "银行间指令分发")
public class CfetsEtrDispatcherController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CfetsEtrDispatcherController.class);

    @Autowired
    private CfetsEtrDispatcherService cfetsEtrDispatcherService;

    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams() {
        Map<String, Object> initPageParams = new HashMap<>();
        //数据字典初始化数据
        initPageParams.put("PINAN_ASSET_TYPE", this.getSelectDataFromDataDic(DictCode.平安资产类型, null));
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.平安资产类型, null);
        if (selectDto != null && !StringUtil.isEmptyList(selectDto.getOptions())) {
            List<SelectOptionDto> options = selectDto.getOptions();
            for (int index = options.size() - 1; index >= 0; index--) {
                SelectOptionDto option = options.get(index);
                // 去除不需要的字典类型
                if (StringUtil.equals(option.getId(), DictCodeList.平安资产类型_一级市场债.getValue())) {
                    options.remove(option);
                }
            }
            initPageParams.put("PINAN_ASSET_TYPE_OP", selectDto);
        }

        initPageParams.put("FIRST_BID_STATUS", this.getSelectDataFromDataDic(DictCode.一级投标中标状态, null));
        SelectDto busiTypePosition = this.getSelectDataFromDataDic(DictCode.头寸业务类型, null);
        SelectDto busiTypeTg = this.getSelectDataFromDataDic(DictCode.投管业务类型, null);
        List<SelectOptionDto> selectOptionDtoList = new ArrayList<>();
        selectOptionDtoList.addAll(busiTypePosition.getOptions());
        selectOptionDtoList.addAll(busiTypeTg.getOptions());
        busiTypePosition.setOptions(selectOptionDtoList);
        initPageParams.put("BUSI_TYPE_POSITION",busiTypePosition);
        initPageParams.put("TRADE_MARKET", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("CCY", this.getSelectDataFromDataDic(DictCode.币种, null));
        initPageParams.put("ACCOUNT_SPEED", this.getSelectDataFromDataDic(DictCode.清算速度, null));
//        initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
        List<SelectOptionDto> psList = makeAllFloatRate();
        initPageParams.put("PS", JsonUtils.objectArray2Json(psList.toArray()));
        initPageParams.put("SHORTSELLINGSUPPORTED01", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
        //自动刷新时间(ms)
        initPageParams.put("cfetsEtrDispatcher_tradeList_refresh_period", CacheUtil.getSysParam("cfetsEtrDispatcher_tradeList_refresh_period"));
        initPageParams.put("cfetsEtrDispatcher_dealersTable_refresh_period", CacheUtil.getSysParam("cfetsEtrDispatcher_dealersTable_refresh_period"));
        initPageParams.put("pageId", "CfetsEtrDispatcher");
        return new Message<Map<String, Object>>(initPageParams).success();
    }

    /**
     *
     * 组交易方向下拉框值
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    private List<SelectOptionDto> makeAllFloatRate() {
        List<SelectOptionDto> listFloatRate = new ArrayList<SelectOptionDto>();

        SelectDto selectDto1 = this.getSelectDataFromDataDic(DictCode.交易方向, null);
        for (SelectOptionDto dto : selectDto1.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto2 = this.getSelectDataFromDataDic(DictCode.回购拆借方向, null);
        for (SelectOptionDto dto : selectDto2.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto3 = this.getSelectDataFromDataDic(DictCode.互换方式, null);
        for (SelectOptionDto dto : selectDto3.getOptions()) {
            listFloatRate.add(dto);
        }
        return listFloatRate;
    }

    /**
     * 指令列表分页查询
     *
     * @param dto
     * @return Page<CfetsEtrDispatcherTradeListResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "指令列表分页查询", notes = "指令列表分页查询")
    @PostMapping("/findAllEtrDispatcherCommon")
    @ResponseBody
    public Message<Page<CfetsEtrDispatcherTradeListResponse>> findAllEtrDispatcherCommon(@RequestBody CfetsEtrDispatcherTradeListRequest dto)
            throws Exception {
        try {
            Page<CfetsEtrDispatcherTradeListResponse> page = cfetsEtrDispatcherService.findAllEtrDispatcherCommon(dto);
            return new Message<Page<CfetsEtrDispatcherTradeListResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询指令管理分页数据异常", e);
            return new Message<Page<CfetsEtrDispatcherTradeListResponse>>().fail().setMessage("查询指令列表分页数据异常");
        }
    }

    /**
     * 指令执行查询
     *
     * @param dto
     * @return Page<CfetsEtrDispatcherExeListInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "指令执行查询", notes = "指令执行查询")
    @PostMapping("/getEtrDispatcherExeListInfo")
    @ResponseBody
    public Message<Page<CfetsEtrDispatcherExeListInfoResponse>> getEtrDispatcherExeListInfo(@RequestBody CommonSearchInfoRequest dto)
            throws Exception {
        try {
            Page<CfetsEtrDispatcherExeListInfoResponse> returnDto = cfetsEtrDispatcherService.getEtrDispatcherExeListInfo(dto);
            if (returnDto.getTotal() == 0 && CollectionUtils.isEmpty(returnDto.getRoot())) {
                returnDto = cfetsEtrDispatcherService.getEtrDispatcherExeListInfoByExe(dto);
            }
            return new Message<Page<CfetsEtrDispatcherExeListInfoResponse>>().success().setData(returnDto);
        } catch (Exception e) {
            logger.error("查询指令管理分页数据异常", e);
            return new Message<Page<CfetsEtrDispatcherExeListInfoResponse>>().fail().setMessage("指令执行查询异常");
        }
    }

    /**
     * 一级债券指令执行查询
     *
     * @param dto
     * @return Page<CfetsEtrDispatcherExeListInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "一级债券指令执行查询", notes = "一级债券指令执行查询")
    @PostMapping("/getEtrBondFirstExeListInfo")
    @ResponseBody
    public Message<Page<CfetsEtrBondFirstExeListInfoResponse>> getEtrBondFirstExeListInfo(@RequestBody CommonSearchInfoRequest dto)
            throws Exception {
        try {
            Page<CfetsEtrBondFirstExeListInfoResponse> returnDto = cfetsEtrDispatcherService.getEtrBondFirstExeListInfo(dto);
            return new Message<Page<CfetsEtrBondFirstExeListInfoResponse>>().success().setData(returnDto);
        } catch (Exception e) {
            logger.error("一级债券指令执行查询异常", e);
            return new Message<Page<CfetsEtrBondFirstExeListInfoResponse>>().fail().setMessage("一级债券指令执行查询查询异常");
        }
    }

    /**
     * 指令成交查询
     *
     * @param dto
     * @return Page<CfetsEtrDispatcherDealListInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "指令成交查询", notes = "指令成交查询")
    @PostMapping("/getEtrDispatcherDealListInfo")
    @ResponseBody
    public Message<Page<CfetsEtrDispatcherDealListInfoResponse>> getEtrDispatcherDealListInfo(@RequestBody CommonSearchInfoRequest dto)
            throws Exception {
        try {
            Page<CfetsEtrDispatcherDealListInfoResponse> returnDto = cfetsEtrDispatcherService.getEtrDispatcherDealListInfo(dto);
            if (returnDto.getTotal() == 0 && CollectionUtils.isEmpty(returnDto.getRoot())) {
                returnDto = cfetsEtrDispatcherService.getEtrDispatcherDealListInfoByExe(dto);
            }
            return new Message<Page<CfetsEtrDispatcherDealListInfoResponse>>().success().setData(returnDto);
        } catch (Exception e) {
            logger.error("查询指令成交异常", e);
            return new Message<Page<CfetsEtrDispatcherDealListInfoResponse>>().fail().setMessage("查询指令成交异常");
        }
    }


    @PostMapping(value = "/exportEtrDispatcherList")
    @ResponseBody
    @ApiOperation(value = "指令分发列表导出", notes = "指令分发列表导出")
    public void exportEtrDispatcherList(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "指令分发", () -> {
            List dataList = cfetsEtrDispatcherService.findAllEtrDispatcherCommon(req.getDto(CfetsEtrDispatcherTradeListRequest.class)).getRoot();
            return dataList;
        });
    }

    /**
     * 自动分发
     *
     * @param cfetsDto br
     * @return Message<AjaxResult>
     * @author sjj
     */
    @ResponseBody
    @ApiOperation(value = "自动分发提交", notes = "自动分发提交")
    @PostMapping("submit")
    public Message<AjaxResult> submit(@RequestBody CfetsEtrDispatcherAutoRequest cfetsDto, BindingResult br)
            throws Exception {
        //提交check
        String returnMsg = cfetsEtrDispatcherService.checkSave(cfetsDto, br);
        if (StringUtil.isNotBlank(returnMsg)) {
            return new Message<AjaxResult>().fail().setMessage(returnMsg);
        }

        //设置提交
        cfetsEtrDispatcherService.deleteEtrAutoTime();
        if (cfetsDto.getVdate().equals("1900-01-01") && cfetsDto.getMdate().equals("2099-12-31")) {

        } else {
            int count = cfetsEtrDispatcherService.insertEtrAutoTime(cfetsDto);
            if (count == 0) {
                return new Message<AjaxResult>().fail().setMessage("自动分发设置失败，请联系管理员！");
            }
        }
        return new Message<AjaxResult>().success().setMessage("自动分发设置完成");
    }

    /**
     * 自动分发查询
     *
     * @return Message<CfetsEtrDispatcherAutoRequest>
     * @author sjj
     */
    @ResponseBody
    @ApiOperation(value = "自动分发查询", notes = "自动分发查询")
    @PostMapping("findAutoDetail")
    public Message<CfetsEtrDispatcherAutoRequest> findAutoDetail()
            throws Exception {
        try {
            CfetsEtrDispatcherAutoRequest cfetsEtrDispatcherAutoRequest = cfetsEtrDispatcherService.findAutoDetail();
            return new Message<CfetsEtrDispatcherAutoRequest>().success().setData(cfetsEtrDispatcherAutoRequest);
        } catch (Exception e) {
            logger.error("自动分发查询异常", e);
            return new Message<CfetsEtrDispatcherAutoRequest>().fail().setMessage("自动分发查询异常");
        }

    }


    /**
     * 点击分发按钮check
     *
     * @param cfetsDto br
     * @return Message<AjaxResult>
     * @author sjj
     */
    @ResponseBody
    @ApiOperation(value = "分发check", notes = "分发check")
    @PostMapping("doDispatcherCheck")
    public Message<AjaxResult> doDispatcherCheck(@RequestBody CfetsEtrDispatcherDealRequest cfetsDto)
            throws Exception {
        try {
            //提交check
            Map<String, Object> pageParam = BeanUtil.object2Map(cfetsDto);
            AjaxResult ajaxResult = cfetsEtrDispatcherService.doDispatcherCheck(pageParam);
            if (null != ajaxResult) {
                return new Message<AjaxResult>().success().setData(ajaxResult);
            } else {
                return new Message<AjaxResult>().success();
            }
        } catch (Exception e) {
            logger.error("分发check异常", e);
            return new Message<AjaxResult>().fail().setMessage("分发check异常");
        }
    }

    /**
     * 一级投标指令分发校验
     *
     * @param cfetsDto
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "一级投标指令分发校验", notes = "一级投标指令分发校验")
    @PostMapping("/getDispatcherFirstBid")
    @ResponseBody
    public Message<AjaxResult> getDispatcherFirstBid(@RequestBody CfetsEtrDispatcherDealRequest cfetsDto)
            throws Exception {
        try {
            //提交check
            AjaxResult ajaxResult = cfetsEtrDispatcherService.getDispatcherFirstBid(cfetsDto);
            if (null != ajaxResult) {
                return new Message<AjaxResult>().success().setData(ajaxResult);
            } else {
                return new Message<AjaxResult>().success();
            }
        } catch (Exception e) {
            logger.error("一级投标指令分发check异常", e);
            return new Message<AjaxResult>().fail().setMessage("一级投标指令分发check异常");
        }
    }

    /**
     * 点击分发确认
     *
     * @param cfetsDto br
     * @return Message<AjaxResult>
     * @author sjj
     */
    @ResponseBody
    @ApiOperation(value = "分发", notes = "分发")
    @PostMapping("doDispatcher")
    public Message<AjaxResult> doDispatcher(@RequestBody CfetsEtrDispatcherDealRequest cfetsDto, BindingResult br)
            throws Exception {
        //提交check
        try {
            Map<String, Object> pageParam = BeanUtil.object2Map(cfetsDto);
            AjaxResult ajaxResult = cfetsEtrDispatcherService.doDispatcherDealCheck(pageParam, br);
            if (!StringUtil.isEmptyList(ajaxResult.getErrorList())) {
                return new Message<AjaxResult>().fail().setMessage(ajaxResult.getMessage());
            } else {
                //选择交易员
                cfetsEtrDispatcherService.doDispatcherDeal(pageParam);
                return new Message<AjaxResult>().success().setMessage("分发成功");
            }
        }catch(BusinessException e1){
            return new Message<AjaxResult>().fail().setMessage(e1.getMessage());
        } catch (Exception e) {
            logger.error("分发异常", e);
            return new Message<AjaxResult>().fail().setMessage("分发异常");
        }
    }

    /**
     * 点击退回
     *
     * @param cfetsDto br
     * @return Message<AjaxResult>
     * @author sjj
     */
    @ResponseBody
    @ApiOperation(value = "退回", notes = "退回")
    @PostMapping("doRefuseEntrust")
    public Message<AjaxResult> doRefuseEntrust(@RequestBody CfetsEtrDispatcherDealRequest cfetsDto, BindingResult br)
            throws Exception {
        //退回check
        try {
            Map<String, Object> pageParam = BeanUtil.object2Map(cfetsDto);
            AjaxResult ajaxResult = cfetsEtrDispatcherService.doRefuseEntrustCheck(pageParam, br);
            if (!StringUtil.isEmptyList(ajaxResult.getErrorList())) {
                return new Message<AjaxResult>().fail().setMessage(ajaxResult.getMessage());
            } else {
                //退回指令
                ajaxResult = cfetsEtrDispatcherService.refuseEntrust(pageParam);

                return new Message<AjaxResult>().success().setMessage("退回成功");
            }
        } catch (Exception e) {
            logger.error("退回异常", e);
            return new Message<AjaxResult>().fail().setMessage("退回异常");
        }
    }

    /**
     * 分发权限配置交易员查询
     *
     * @return Page<CfetsEtrDispatcherTraderAuthListResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "分发权限配置交易员查询", notes = "分发权限配置交易员查询")
    @PostMapping("/getTraderAuthList")
    @ResponseBody
    public Message<List<CfetsEtrDispatcherTraderAuthListResponse>> getTraderAuthList()
            throws Exception {
        try {
            List<CfetsEtrDispatcherTraderAuthListResponse> list = cfetsEtrDispatcherService.getTraderAuthList();
            return new Message<List<CfetsEtrDispatcherTraderAuthListResponse>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询分发权限配置交易员数据异常", e);
            return new Message<List<CfetsEtrDispatcherTraderAuthListResponse>>().fail().setMessage("查询指令列表分页数据异常");
        }
    }

    /**
     * 分发权限配置保存
     *
     * @return AjaxResult
     * @param  dto
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "分发权限配置保存", notes = "分发权限配置保存")
    @PostMapping("/saveTraderAuthList")
    @ResponseBody
    public Message<AjaxResult> saveTraderAuthList(@RequestBody CfetsEtrDispatcherTraderAuthListRequest dto)
            throws Exception {

        AjaxResult ajaxResult = new AjaxResult();
        List<CfetsEtrDispatcherTraderAuthListResponse> dtoList = dto.getDtoList();
        //提交check，软控
        if (StringUtil.equals(dto.getIsConfirmFlag(), DictCodeList.是否_否.getValue())) {
            ajaxResult = cfetsEtrDispatcherService.checkSaveTraderAuthList(dtoList);
            if (StringUtil.isNotBlank(ajaxResult.getMessage())) {
                return new Message<AjaxResult>().success().setData(ajaxResult);
            }
        }

        //保存
        ajaxResult = cfetsEtrDispatcherService.doSaveTraderAuthList(dtoList);
        if (!StringUtil.isEmptyList(ajaxResult.getErrorList())) {
            return new Message<AjaxResult>().fail().setMessage("分发权限设置失败");
        } else {
            return new Message<AjaxResult>().success().setMessage("分发权限配置成功");
        }
    }

    @GetMapping(value = "/getEtrTraderList")
    @ResponseBody
    @ApiOperation(value = "交易员列表模糊查询", notes = "交易员列表模糊查询")
    public Message<List<CfetsEtrDispatcherTraderListRequest>> getEtrTraderList() {
        Map<String, Object> param = RequestUtil.getParameters(getRequest());
        List<CfetsEtrDispatcherTraderListRequest> list = cfetsEtrDispatcherService.getEtrTraderList(param);
        return new Message<List<CfetsEtrDispatcherTraderListRequest>>().success().setData(list);
    }

    @GetMapping(value = "/getEtrTraderAndWorkloadList")
    @ResponseBody
    @ApiOperation(value = "交易员与工作量列表查询", notes = "交易员与工作量列表查询")
    public Message<List<CfetsEtrDispatcherTraderListRequest>> getEtrTraderAndWorkloadList() {
        Map<String, Object> param = RequestUtil.getParameters(getRequest());
        List<CfetsEtrDispatcherTraderListRequest> list = cfetsEtrDispatcherService.getEtrTraderAndWorkloadList(param);
        return new Message<List<CfetsEtrDispatcherTraderListRequest>>().success().setData(list);
    }

    @ResponseBody
    @PostMapping("/getReadonlyFormEtrInfo/{pageCode}/{dtCode}/{spcdCode}")
    @ApiOperation(value = "只读表单设置及数据查询_指令详情", notes = "只读表单设置及数据查询_指令详情")
    public Message<Map<String, Object>> getReadonlyFormEtrInfo(@PathVariable("pageCode") String pageCode,
                                                               @PathVariable("dtCode") String dtCode,
                                                               @PathVariable("spcdCode") String spcdCode,
                                                               @RequestBody CommonSearchInfoRequest dto) {
        try {
            Map<String, Object> resultData = cfetsEtrDispatcherService.getReadonlyFormEtrInfo(pageCode, dtCode, spcdCode, dto);
            SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.投管业务类型, null);
            List<String> list = Lists.newArrayList();
            if (StringUtil.isNotEmpty(selectDto) && !CollectionUtils.isEmpty(selectDto.getOptions())) {
                list = selectDto.getOptions().stream().map(item -> item.getId()).collect(Collectors.toList());
            }
            if (!CollectionUtils.isEmpty(list) && list.contains(dto.getBusiTypePosition())) {
                resultData = cfetsEtrDispatcherService.getReadonlyFormEtrInfoExc(pageCode, dtCode, spcdCode, dto);
            }
            return new Message<Map<String, Object>>().success().setData(resultData);
        } catch (Exception e) {
            logger.error("查询指令信息明细分页数据异常", e);
            return new Message<Map<String, Object>>().fail().setMessage("查询指令信息明细异常");
        }
    }
}
