/**
 * 场外成交确认
 * @author sjj
 * @version 1.0 2021年10月26日
 * @see CfetsEtrDispatcherController
 * @since 1.0
 */

package com.joyintech.tams.cfets.trdquery.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdConfirmRegInfoListRequest;
import com.joyintech.tams.cfets.trdquery.dto.*;
import com.joyintech.tams.cfets.trdquery.service.CfetsTrdBondInvestTrade002Service;
import com.joyintech.tams.cfets.trdquery.service.CfetsTrdQueryService;
import com.joyintech.tams.cfets.trdquery.service.CfetsTrdRepoTrade002Service;
import com.joyintech.tams.cfets.trdredeem.dto.CfetsTrdRedeemDealRegisterQuoteDto;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.settle.BussSettleOrderCommService;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeRevokeDto;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTrade002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@PageId("CfetsTrdQuery")
@RequestMapping("/cfets/trdquery/CfetsTrdQuery")
@Api(tags = "银行间交易查询")
public class CfetsTrdQueryController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CfetsTrdQueryController.class);

    @Autowired
    private CfetsTrdQueryService cfetsTrdQueryService;
    /**
     * 流程组件
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;
    @Autowired
    private CfetsTrdBondInvestTrade002Service cfetsTrdBondInvestTrade002Service;
    @Autowired
    private CfetsTrdRepoTrade002Service cfetsTrdRepoTrade002Service;

    /**
     * 划款指令组件
     */
    @Autowired
    private BussSettleOrderCommService bussSettleOrderCommService;
    /**
     * 交易基础组件调用Service
     */
    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams(){
        Map<String, Object> initPageParams = new HashMap<>();
        //是否支持CSTP下行接口
       // initPageParams.put("IS_SUPPORT_CSTP_FLAG", CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG"));
        initPageParams.put("IS_SUPPORT_CSTP_FLAG_BOND", CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BOND"));
        initPageParams.put("IS_SUPPORT_CSTP_FLAG_REPO", CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_REPO"));


        //自动刷新时间
        initPageParams.put("cfetsTrdQuery_regCurList_refresh_period", CacheUtil.getSysParam("cfetsTrdQuery_regCurList_refresh_period"));
        initPageParams.put("cfetsTrdQuery_ibuCurList_refresh_period", CacheUtil.getSysParam("cfetsTrdQuery_ibuCurList_refresh_period"));
        initPageParams.put("cfetsTrdQuery_cstpCurList_refresh_period", CacheUtil.getSysParam("cfetsTrdQuery_cstpCurList_refresh_period"));
        initPageParams.put("cfetsTrdQuery_regPreList_refresh_period", CacheUtil.getSysParam("cfetsTrdQuery_regPreList_refresh_period"));
        initPageParams.put("cfetsTrdQuery_ibuPreList_refresh_period", CacheUtil.getSysParam("cfetsTrdQuery_ibuPreList_refresh_period"));
        initPageParams.put("cfetsTrdQuery_cstpPreList_refresh_period", CacheUtil.getSysParam("cfetsTrdQuery_cstpPreList_refresh_period"));
        initPageParams.put("cfetsTrdQuery_trdList_refresh_period", CacheUtil.getSysParam("cfetsTrdQuery_trdList_refresh_period"));
        initPageParams.put("cfetsTrdQuery_trdDetailList_refresh_period", CacheUtil.getSysParam("cfetsTrdQuery_trdDetailList_refresh_period"));

        //数据字典初始化数据
        initPageParams.put("REG_ASSET_TYPE", this.getSelectDataFromDataDic(DictCode.登记资产类型, null));
        initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
        initPageParams.put("TRADE_MARKET", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("ACCOUNT_SPEED", this.getSelectDataFromDataDic(DictCode.清算速度, null));
        initPageParams.put("QUOTE_TYPE", this.getSelectDataFromDataDic(BussinessDictCode.报价方式, null));
        initPageParams.put("STATUS_VALID", this.getSelectDataFromDataDic(DictCode.有效状态, null));
        initPageParams.put("MATCH_STATUS", this.getSelectDataFromDataDic(DictCode.匹配状态, null));
        initPageParams.put("REG_STATUS", this.getSelectDataFromDataDic(DictCode.登记状态, null));
        initPageParams.put("DEAL_TYPE_IBU", this.getSelectDataFromDataDic(BussinessDictCode.银行间成交状态, null));
        initPageParams.put("CSTP_MATCH_STATUS", this.getSelectDataFromDataDic(DictCode.CSTP匹配状态, null));
        initPageParams.put("SETTLE_STATUS", this.getSelectDataFromDataDic(DictCode.清算状态, null));
        initPageParams.put("BUSI_TYPE_POSITION", this.getSelectDataFromDataDic(DictCode.头寸业务类型, null));

        initPageParams.put("TRD_MATCH_TYPE", this.getSelectDataFromDataDic(DictCode.下行交易匹配类型, null));
        initPageParams.put("CSTP_BUSINESS_TYPE", this.getSelectDataFromDataDic(DictCode.CSTP资产类型, null));
        initPageParams.put("QUOTE_STYLE", this.getSelectDataFromDataDic(BussinessDictCode.报价方式_登记, null));
        initPageParams.put("DELIVERY_STATUS", this.getSelectDataFromDataDic(DictCode.交收状态, null));
        initPageParams.put("SHARES_TRADE_MARKET", this.getSelectDataFromDataDic(DictCode.交易市场, null));


        //pageId
        initPageParams.put("bondPageId", "CfetsTrdBondInvestTrade002");
        initPageParams.put("repoPageId", "CfetsTrdRepoTrade002");


        //当前平台日
        initPageParams.put("platDate",DateUtil.platDateFromCache());
        //当前用户
        initPageParams.put("userId", SecurityUtil.getLoginUser().getUserName());
        return new Message<Map<String, Object>>(initPageParams).success();
    }
    /**
     * 交易登记列表分页查询
     *
     * @param dto
     * @return Page<CfetsTrdConfirmRegInfoListResponse>
     * @throws Exception Exception
     * @since 1.0
     */
    @ApiOperation(value = "交易登记列表分页查询", notes = "交易登记列表分页查询")
    @PostMapping("/queryAllRegInfoList")
    @ResponseBody
    public Message<Page<CfetsTrdQueryRegInfoListResponse>> queryAllRegInfoList(@RequestBody CfetsTrdQueryRegInfoListRequest dto)
            throws Exception {
        try {
            Page<CfetsTrdQueryRegInfoListResponse> page =cfetsTrdQueryService.queryAllRegInfoList(dto);
            return new Message<Page<CfetsTrdQueryRegInfoListResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询交易登记列表分页数据异常", e);
            return new Message<Page<CfetsTrdQueryRegInfoListResponse>>().fail().setMessage("查询交易登记列表分页数据异常");
        }
    }
    @ApiOperation(value = "只读表单设置及数据查询_登记信息查询", notes = "只读表单设置及数据查询_登记信息查询")
    @PostMapping("/findRegInfo/{pageCode}/{dtCode}/{spcdCode}")
    @ResponseBody
    public Message<Map<String,Object>> findRegInfo(@PathVariable("pageCode") String pageCode,
                                                            @PathVariable("dtCode") String dtCode,
                                                            @PathVariable("spcdCode") String spcdCode,
                                                            @RequestBody CfetsTrdQueryRegInfoListResponse dto) throws Exception {
        try {
            Map<String,Object> resultData = null;
            if (StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券一级申购.getValue())
            || StringUtil.equals(dto.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券一级投标.getValue())){
                resultData = cfetsTrdQueryService.findRegBidInfo(pageCode,dtCode,dto.getBusiTypePosition(),dto);
            }else {
                resultData = cfetsTrdQueryService.findRegInfo(pageCode, dtCode, spcdCode, dto);
            }
            return new Message<Map<String,Object>>().success().setData(resultData);
        } catch (Exception e) {
            logger.error("查询登记信息数据异常", e);
            return new Message<Map<String,Object>>().fail().setMessage("查询登记信息数据异常");
        }
    }

    @PostMapping(value="/exportQueryAllRegInfoList")
    @ResponseBody
    @ApiOperation(value = "交易登记列表导出", notes = "交易登记列表导出")
    public void exportQueryAllRegInfoList(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "交易登记列表管理", () -> {
            List dataList = cfetsTrdQueryService.queryAllRegInfoList(req.getDto(CfetsTrdQueryRegInfoListRequest.class)).getRoot();
            return dataList;
        });
    }
    /**
     * 报价列表分页查询
     *
     * @param dto
     * @return Page<CfetsTrdQueryIbuInfoListResponse>
     * @throws Exception Exception
     * @since 1.0
     */
    @ApiOperation(value = "报价列表分页查询", notes = "报价列表分页查询")
    @PostMapping("/queryAllIbuInfoList")
    @ResponseBody
    public Message<Page<CfetsTrdQueryIbuInfoListResponse>> queryAllIbuInfoList(@RequestBody CfetsTrdQueryIbuInfoListRequest dto)
            throws Exception {
        try {
            Page<CfetsTrdQueryIbuInfoListResponse> page =cfetsTrdQueryService.queryAllIbuInfoList(dto);
            return new Message<Page<CfetsTrdQueryIbuInfoListResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询报价列表分页数据异常", e);
            return new Message<Page<CfetsTrdQueryIbuInfoListResponse>>().fail().setMessage("查询报价列表分页数据异常");
        }
    }
    @ApiOperation(value = "只读表单设置及数据查询_报价信息查询", notes = "只读表单设置及数据查询_报价信息查询")
    @PostMapping("/findIbuInfo/{pageCode}/{dtCode}/{spcdCode}")
    @ResponseBody
    public Message<Map<String,Object>> findIbuInfo(@PathVariable("pageCode") String pageCode,
                                                   @PathVariable("dtCode") String dtCode,
                                                   @PathVariable("spcdCode") String spcdCode,
                                                   @RequestBody CfetsTrdQueryIbuInfoListResponse dto) throws Exception {
        try {
            Map<String,Object> resultData = cfetsTrdQueryService.findIbuInfo(pageCode,dtCode,spcdCode,dto);
            return new Message<Map<String,Object>>().success().setData(resultData);
        } catch (Exception e) {
            logger.error("查询报价信息数据异常", e);
            return new Message<Map<String,Object>>().fail().setMessage("查询报价信息数据异常");
        }
    }
    @PostMapping(value="/exportQueryAllIbuInfoList")
    @ResponseBody
    @ApiOperation(value = "报价列表导出", notes = "报价列表导出")
    public void exportQueryAllIbuInfoList(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "报价列表管理", () -> {
            List dataList = cfetsTrdQueryService.queryAllIbuInfoList(req.getDto(CfetsTrdQueryIbuInfoListRequest.class)).getRoot();
            return dataList;
        });
    }
    /**
     * CSTP列表分页查询
     *
     * @param dto
     * @return Page<CfetsTrdQueryIbuInfoListResponse>
     * @throws Exception Exception
     * @since 1.0
     */
    @ApiOperation(value = "成交列表分页查询", notes = "成交列表分页查询")
    @PostMapping("/queryAllCstpInfoList")
    @ResponseBody
    public Message<Page<CfetsTrdQueryCstpInfoListResponse>> queryAllCstpInfoList(@RequestBody CfetsTrdQueryCstpInfoListRequest dto)
            throws Exception {
        try {
            Page<CfetsTrdQueryCstpInfoListResponse> page =cfetsTrdQueryService.queryAllCstpInfoList(dto);
            return new Message<Page<CfetsTrdQueryCstpInfoListResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询报价列表分页数据异常", e);
            return new Message<Page<CfetsTrdQueryCstpInfoListResponse>>().fail().setMessage("查询报价列表分页数据异常");
        }
    }
    @ApiOperation(value = "只读表单设置及数据查询_成交信息查询", notes = "只读表单设置及数据查询_成交信息查询")
    @PostMapping("/findCstpInfo/{pageCode}/{dtCode}/{spcdCode}")
    @ResponseBody
    public Message<Map<String,Object>> findCstpInfo(@PathVariable("pageCode") String pageCode,
                                                   @PathVariable("dtCode") String dtCode,
                                                   @PathVariable("spcdCode") String spcdCode,
                                                   @RequestBody CfetsTrdQueryCstpInfoListResponse dto) throws Exception {
        try {
            Map<String,Object> resultData = new HashMap<>();
            if (StringUtil.equals(spcdCode, DictCodeListExt3.头寸业务类型_债券一级申购.getValue())){
                resultData = cfetsTrdQueryService.findBidCstpInfo(pageCode,dtCode,spcdCode,dto);
            }else{
                resultData = cfetsTrdQueryService.findCstpInfo(pageCode,dtCode,spcdCode,dto);
            }
            return new Message<Map<String,Object>>().success().setData(resultData);
        } catch (Exception e) {
            logger.error("查询成交信息数据异常", e);
            return new Message<Map<String,Object>>().fail().setMessage("查询成交信息数据异常");
        }
    }
    @PostMapping(value="/exportQueryAllCstpInfoList")
    @ResponseBody
    @ApiOperation(value = "成交列表导出", notes = "成交列表导出")
    public void exportQueryAllCstpInfoList(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "成交列表管理", () -> {
            List dataList = cfetsTrdQueryService.queryAllCstpInfoList(req.getDto(CfetsTrdQueryCstpInfoListRequest.class)).getRoot();
            return dataList;
        });
    }
    /**
     * 银行间交易汇总分页查询
     *
     * @param dto
     * @return Page<CfetsManualSettleTradeListRespond>
     * @throws Exception Exception
     * @seeTrade
     * @since 1.0
     */
    @ApiOperation(value = "银行间交易汇总分页查询", notes = "银行间交易汇总分页查询")
    @PostMapping("/queryAllTradeInfoList")
    @ResponseBody
    public Message<Page<CfetsTrdQueryTradeInfoListResponse>> queryAllTradeInfoList(@RequestBody CfetsTrdQueryTradeInfoListRequest dto)
            throws Exception {
        try {
            Page<CfetsTrdQueryTradeInfoListResponse> page = cfetsTrdQueryService.queryAllTradeInfoList(dto);
            return new Message<Page<CfetsTrdQueryTradeInfoListResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询银行间交易汇总数据异常", e);
            return new Message<Page<CfetsTrdQueryTradeInfoListResponse>>().fail().setMessage("查询银行间交易汇总数据异常");
        }
    }
    @PostMapping(value="/exportQueryAllTradeInfoList")
    @ResponseBody
    @ApiOperation(value = "交易汇总列表导出", notes = "交易汇总列表导出")
    public void exportQueryAllTradeInfoList(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "交易汇总列表管理", () -> {
            List dataList = cfetsTrdQueryService.queryAllTradeInfoList(req.getDto(CfetsTrdQueryTradeInfoListRequest.class)).getRoot();
            return dataList;
        });
    }
    /**
     * 银行间交易明细分页查询
     *
     * @param dto
     * @return Page<CfetsManualSettleTradeListRespond>
     * @throws Exception Exception
     * @seeTrade
     * @since 1.0
     */
    @ApiOperation(value = "银行间交易明细分页查询", notes = "银行间交易明细分页查询")
    @PostMapping("/queryAllTradeDetailInfoList")
    @ResponseBody
    public Message<Page<CfetsTrdQueryTradeInfoListResponse>> queryAllTradeDetailInfoList(@RequestBody CfetsTrdQueryTradeInfoListRequest dto)
            throws Exception {
        try {
            Page<CfetsTrdQueryTradeInfoListResponse> page = cfetsTrdQueryService.queryAllTradeDetailInfoList(dto);
            return new Message<Page<CfetsTrdQueryTradeInfoListResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询银行间交易明细数据异常", e);
            return new Message<Page<CfetsTrdQueryTradeInfoListResponse>>().fail().setMessage("查询银行间交易明细数据异常");
        }
    }
    @PostMapping(value="/exportQueryAllTradeDetailInfoList")
    @ResponseBody
    @ApiOperation(value = "交易明细列表导出", notes = "交易明细列表导出")
    public void exportQueryAllTradeDetailInfoList(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "交易明细列表管理", () -> {
            List dataList = cfetsTrdQueryService.queryAllTradeDetailInfoList(req.getDto(CfetsTrdQueryTradeInfoListRequest.class)).getRoot();
            return dataList;
        });
    }

    @ApiOperation(value = "只读表单设置及数据查询_交易信息查询", notes = "只读表单设置及数据查询_交易信息查询")
    @PostMapping("/findTradeInfo/{pageCode}/{dtCode}/{spcdCode}")
    @ResponseBody
    public Message<Map<String,Object>> findTradeInfo(@PathVariable("pageCode") String pageCode,
                                                            @PathVariable("dtCode") String dtCode,
                                                            @PathVariable("spcdCode") String spcdCode,
                                                            @RequestBody CfetsTrdQueryTradeInfoListResponse dto) throws Exception {
        try {
            Map<String,Object> resultData = cfetsTrdQueryService.findTradeInfo(pageCode,dtCode,spcdCode,dto);
            return new Message<Map<String,Object>>().success().setData(resultData);
        } catch (Exception e) {
            logger.error("查询交易信息数据异常", e);
            return new Message<Map<String,Object>>().fail().setMessage("查询交易信息数据异常");
        }
    }
    @ApiOperation(value = "只读表单设置及数据查询_明细交易信息查询", notes = "只读表单设置及数据查询_明细交易信息查询")
    @PostMapping("/findTradeDetailInfo/{pageCode}/{dtCode}/{spcdCode}")
    @ResponseBody
    public Message<Map<String,Object>> findTradeDetailInfo(@PathVariable("pageCode") String pageCode,
                                                     @PathVariable("dtCode") String dtCode,
                                                     @PathVariable("spcdCode") String spcdCode,
                                                     @RequestBody CfetsTrdQueryTradeInfoListResponse dto) throws Exception {
        try {
            Map<String, Object> resultData = new HashMap<>();
            if (DictCodeListExt4.投标方式_分销买入.getValue().equals(dto.getBidModle())
                    || DictCodeListExt4.投标方式_自主投标.getValue().equals(dto.getBidModle())
                    || StringUtil.equals(dto.getTgBusiType(), DictCodeListExt3.头寸业务类型_债券一级投标.getValue())) {
                resultData = cfetsTrdQueryService.findBidTradeDetailInfo(pageCode, dtCode, spcdCode, dto);
            }else{
                resultData = cfetsTrdQueryService.findTradeDetailInfo(pageCode, dtCode, spcdCode, dto);
            }
            return new Message<Map<String,Object>>().success().setData(resultData);
        } catch (Exception e) {
            logger.error("查询交易信息数据异常", e);
            return new Message<Map<String,Object>>().fail().setMessage("查询交易信息数据异常");
        }
    }

    @ApiOperation(value = "交易撤销前check", notes = "交易撤销前check")
    @PostMapping("checkRevoke")
    @ResponseBody
    public  Message<AjaxResult> checkRevoke(@RequestBody CfetsTrdQueryTradeInfoListResponse inputDto)
          throws Exception {
            try {
                AjaxResult ajax= cfetsTrdBondInvestTrade002Service.checkRevoke(inputDto);
                if(null!=ajax){
                    return new Message<AjaxResult>().success().setData(ajax);
                }else{
                    return new Message<AjaxResult>().success().setMessage("校验通过");
                }
            }catch (Exception e) {
                logger.error("交易撤销校验异常",e);
                return new Message<AjaxResult>().fail().setMessage("校验失败");
            }
    }

    /**
     * 撤销
     * @param inputDto 画面输入参数dto
     * @return 撤销结果
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "交易撤销", notes = "交易撤销")
    @PostMapping("/doRevoke")
    @ResponseBody
    public Message<AjaxResult> doRevoke(@RequestBody CfetsTrdQueryTradeInfoListResponse input) throws WorkFlowException {
        List<CfetsTrdQueryTradeInfoListResponse> list=input.getSelectedItem();
        String msg="";
        for(CfetsTrdQueryTradeInfoListResponse item:list){
            if(StringUtil.equals(item.getFinprodType2(),DictCodeListExt1.金融产品类型_债券.getValue())){
                //债券
                String operateType = item.getOperateType();
                String totleTradeId = item.getTotleTradeId();
                CfetsTrdBondInvestTradeDto inputDto = cfetsTrdBondInvestTrade002Service.getTrdBondInvestTradeDto(item.getTotleTradeId());
                inputDto.setWorkflowParameter(item.getWorkflowParameter());
                //add by  meifaxin 后面审批用到 start
                inputDto.setTradeId(totleTradeId);
                //add by  meifaxin 后面审批用到 end
                String turnInvAim = cfetsTrdBondInvestTrade002Service.getReverseTradeDtoInvAim(inputDto.getTotleTradeId());
                if(StringUtil.isNotEmpty(turnInvAim)) {
                    inputDto.setTurnInvAim(turnInvAim);
                }
                //划款指令
                if (inputDto != null && StringUtil.isEmptyList(inputDto.getBussTrdTransferOrder())){
                    List<BussTrdTransferOrderBase> orderDtolist = bussSettleOrderCommService.selectOrdersByBizCode(inputDto.getTotleTradeId()) ;
                    if (!com.joyintech.framework.util.StringUtil.isEmptyList(orderDtolist)){
                        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtoList = new ArrayList<BussTrdTransferOrderDto>();
                        BussTrdTransferOrderDto bussTrdTransferOrderDto = new BussTrdTransferOrderDto();
                        BeanUtil.copyPropertiesIgnoreNull(bussTrdTransferOrderDto, orderDtolist.get(0));
                        bussTrdTransferOrderDtoList.add(bussTrdTransferOrderDto);
                        inputDto.setBussTrdTransferOrder(bussTrdTransferOrderDtoList);
                    }
                }
                if(StringUtil.equals(inputDto.getTradeMarketMode(), DictCodeListExt1.交易市场_一级市场.getValue())){
                    inputDto.setBondFirstSign("01");
                }
                inputDto.setRevokeReason(input.getRevokeReason());
                inputDto.setTotleTradeId(totleTradeId + "R");
                inputDto.setFinprodType2("F01");
                inputDto.setOperateType(operateType);
                //判断流程中是否存在,如果流程不存在则说明是生成的反向交易，组装撤销dto yjm end 20191015
                ProcessParameter<CfetsTrdBondInvestTradeDto> processParameter = new ProcessParameter<>();
                WorkFlowParameter<CfetsTrdBondInvestTradeDto> workFlowParameter = new WorkFlowParameter<>();
                processParameter.setWorkFlowParameter(workFlowParameter);
                processParameter.setPageId("CfetsTrdBondInvestTrade002");
                processParameter.setOperationType(OperationType.REVOKE);

                workFlowParameter.setPageId("CfetsTrdBondInvestTrade002");
                workFlowParameter.setElementId(this.getParam("elementId"));
                workFlowParameter.setBusinessCode((entity) -> entity.getTotleTradeId());
                workFlowParameter.setBusinessType(DictCodeList.共通业务类型_债券类资产交易.getValue());
                workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
                        getMessage("TrdBondInvestTrade002.tradeId") + GlobalConstant.COLON + entity.getTotleTradeId(),
                        getMessage("TrdBondInvestTrade002.finprodName") + GlobalConstant.COLON + entity.getFinprodName(),
                        getMessage("TrdBondInvestTrade002.tradeDate") + GlobalConstant.COLON + DateUtil.format(entity.getTradeDate())));

                msg += cfetsTrdBondInvestTrade002Service.start(processParameter, inputDto);

            }else if(StringUtil.equals(item.getFinprodType2(),DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())
                    || StringUtil.equals(item.getFinprodType2(),DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())){
                //质押式回购
                String totleTradeId = item.getTotleTradeId();
                CfetsTrdRepoTrade002Dto inputDto =cfetsTrdRepoTrade002Service.getTrdRepoInvestTradeDto(item.getTotleTradeId());
                inputDto.setRevokeReason(input.getRevokeReason());
                inputDto.setWorkflowParameter(item.getWorkflowParameter());
                inputDto.setTotleTradeId(totleTradeId + "R");
                ProcessParameter<CfetsTrdRepoTrade002Dto> processParameter = new ProcessParameter<>();
                WorkFlowParameter<CfetsTrdRepoTrade002Dto> workFlowParameter = new WorkFlowParameter<>();
                processParameter.setWorkFlowParameter(workFlowParameter);
                processParameter.setPageId("CfetsTrdRepoTrade002");
                processParameter.setOperationType(OperationType.REVOKE);

                workFlowParameter.setPageId("CfetsTrdRepoTrade002");
                workFlowParameter.setElementId(this.getParam("elementId"));
                workFlowParameter.setBusinessCode((entity) -> entity.getTotleTradeId());
                workFlowParameter.setBusinessType(DictCodeList.共通业务类型_质押式回购交易.getValue());
                workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
                        getMessage("TrdRepoTrade002.tradeId") + GlobalConstant.OPEN_PARENTHESIS + entity.getTradeId() + GlobalConstant.CLOSE_PARENTHESIS,
                        DictCodeListExt1.交易场所_银行间.getValue().equals(entity.getTradeMarket())?"":getMessage("TrdRepoTrade002.repoType") + GlobalConstant.OPEN_PARENTHESIS + SysDataUtil.getNameFromDataDict(DictCode.回购类型, entity.getRepoType()) + GlobalConstant.CLOSE_PARENTHESIS,
                        getMessage("TrdRepoTrade002.tradeDate") + GlobalConstant.OPEN_PARENTHESIS + DateUtil.format(entity.getTradeDate()) + GlobalConstant.CLOSE_PARENTHESIS));

                msg += cfetsTrdRepoTrade002Service.start(processParameter, inputDto);
            }
        }
        try {
            AjaxResult m =new AjaxResult(msg);
            return new Message<AjaxResult>().success().setData(m);
        }catch (Exception e) {
            logger.error("撤销交易失败",e);
            return PositionUtil.ajaxErrorReturn(e);
        }
    }

    @ApiOperation(value = "修改成交编号前check", notes = "修改成交编号前check")
    @PostMapping("checkModifyExecId")
    @ResponseBody
    public  Message<AjaxResult> checkModifyExecId(@RequestBody CfetsTrdBondInvestTradeDto inputDto)
            throws Exception {
        try {
            AjaxResult ajax= cfetsTrdBondInvestTrade002Service.checkModifyExecId(inputDto);
            if(null!=ajax){
                return new Message<AjaxResult>().success().setData(ajax);
            }else{
                return new Message<AjaxResult>().success().setMessage("校验通过");
            }
        }catch (Exception e) {
            logger.error("修改成交编号校验异常",e);
            return new Message<AjaxResult>().fail().setMessage("校验失败");
        }
    }

    @ApiOperation(value = "修改成交编号", notes = "修改成交编号")
    @PostMapping("/modifyExecId")
    @ResponseBody
    public Message<AjaxResult> modifyExecIdMethod(@RequestBody CfetsTrdBondInvestTradeDto input) throws WorkFlowException {
        try {
            AjaxResult ajax= cfetsTrdBondInvestTrade002Service.modifyExecIdMethod(input);
            if(null!=ajax){
                return new Message<AjaxResult>().success().setData(ajax);
            }else{
                return new Message<AjaxResult>().success().setMessage("修改成交编号成功");
            }
        }catch (Exception e) {
            logger.error("修改成交编号成功异常",e);
            return new Message<AjaxResult>().fail().setMessage("修改成交编号失败");
        }
    }

}
