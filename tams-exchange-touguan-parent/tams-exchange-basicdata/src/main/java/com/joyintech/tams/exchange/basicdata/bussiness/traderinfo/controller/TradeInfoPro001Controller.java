package com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProResponse;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProSaveRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.service.TradeInfoPro001Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.joyintech.tams.business.common.pojo.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@PageId("TradeInfoPro001")
@RequestMapping("/bussiness/traderinfo/TradeInfoPro001")
@Api(tags = "交易商信息维护")
public class TradeInfoPro001Controller extends BaseController {

    @Autowired
    private TradeInfoPro001Service tradeInfoPro001Service;
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @ApiOperation(value = "交易商信息维护页面初始化参数", notes = "交易商信息维护查询页面初始化参数")
    @PostMapping("/getPageParams001")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams001() {
        Map<String, Object> initPageParams = new HashMap<>();
        initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));

        Date platformDate = DateUtil.platDateFromCache();
        initPageParams.put("iPlatformDate", DateUtil.format(platformDate));

        initPageParams.put("YES_OR_NO",this.getSelectDataFromDataDic(DictCode.是否,null));
        initPageParams.put("DEALER_PARTY_TYPE",this.getSelectDataFromDataDic(DictCode.交易主体类型,null));
        //流程id
//        String workflowId = "null".equals(getParam(GlobalConstant.WORKFLOW_ID)) ? null : getParam(GlobalConstant.WORKFLOW_ID);
//        initPageParams.put("workflowId", workflowId);
//        String parentPageModel = getParam(GlobalConstant.DETAIL_PAGE_MODEL_KEY);
//        initPageParams.put("parentPageModel", parentPageModel);
        initPageParams.put("TRADE_MARKET_03", DictCodeList.交易流通场所_上海证券交易所.getValue());
        initPageParams.put("TRADE_MARKET_04", DictCodeList.交易流通场所_深圳证券交易所.getValue());
        return new Message<Map<String, Object>>(initPageParams).success();
    }


    @ApiOperation(value = "获取交易商名称", notes = "获取交易商名称")
    @ResponseBody
    @PostMapping("/getCustomerName")
    public Message<List<SelectOptionDto>> getCustomerName(@RequestBody TradeInfoProRequest dto){
        try {
            Map<String, Object> map = BeanUtil.object2Map(dto);
            List<SelectOptionDto> tempList= tradeInfoPro001Service.getCustomerName(map);
            return new Message<List<SelectOptionDto>>().success().setData(tempList);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<List<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }

    @ApiOperation(value = "交易商信息查询", notes = "交易商信息查询")
    @ResponseBody
    @PostMapping("/getDealerTraderInfo")
    public Message<TradeInfoProResponse> selectTraderInfo(@RequestBody TradeInfoProRequest dto ){
        try {
            TradeInfoProResponse tradeInfoProResponse= tradeInfoPro001Service.getDealerTraderInfo(dto);
            return new Message<TradeInfoProResponse>().success().setData(tradeInfoProResponse);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<TradeInfoProResponse>().fail().setMessage("查询异常");
        }
    }

    @ApiOperation(value = "选中删除", notes = "选中删除")
    @ResponseBody
    @PostMapping(ACTION_DELETE)
    public Message<AjaxResult> deleteSelectedItem(@RequestBody TradeInfoProSaveRequest dto, BindingResult br){
        ProcessParameter<TradeInfoProSaveRequest> processParameter = new ProcessParameter<TradeInfoProSaveRequest>();
        WorkFlowParameter<TradeInfoProSaveRequest> params = new WorkFlowParameter<TradeInfoProSaveRequest>();
        params.setPageId("TradeInfoPro001");
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getDealerNo());
        params.setBusinessType(DictCodeList.共通业务类型_债券类资产信息.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment(getMessage("FinBondTargetAsset002.finprodId")+
                    GlobalConstant.OPEN_PARENTHESIS+entity.getDealerNo()+GlobalConstant.CLOSE_PARENTHESIS+
                    getMessage("FinBondTargetAsset002.finprodName")+GlobalConstant.OPEN_PARENTHESIS+
                    entity.getDealer()+GlobalConstant.CLOSE_PARENTHESIS);
        });
        processParameter.setWorkFlowParameter(params);
        String msg = null;
        try{
            msg = tradeInfoPro001Service.start(processParameter,
                    dto.getSelectedItem().toArray(new TradeInfoProSaveRequest[dto.getSelectedItem().size()]));
             return new Message().success();
        }catch (Exception e){
            logger.error("删除功能异常", e);
            return new Message<AjaxResult>().fail().setMessage("删除异常");

        }

    }



    /**
     * 保存/提交
     *
     * @param dto
     *            dto
     * @param br
     *            br
     * @return 结果
     * @throws Exception
     *             异常
     */
    @ApiOperation(value = "保存提交", notes = "保存提交")
    @PostMapping("save")
    @ResponseBody
    public Message<AjaxResult> save(@RequestBody TradeInfoProSaveRequest dto, BindingResult br){
        try {
            if (br.hasErrors()) {
                return new Message<AjaxResult>().fail().setData(new AjaxResult(br));
            }
            String msg ="";
            if (StringUtil.equals("save", dto.getOperateType())) {  //保存没有check和任何限制
                    //业务编号生成规则参数
                    ProcessParameter<TradeInfoProSaveRequest> processParameter = new ProcessParameter<TradeInfoProSaveRequest>();
                    msg = tradeInfoPro001Service.start(processParameter, dto);
                    return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
                }
            } catch (Exception e){
            e.printStackTrace();
            }
            return new Message<AjaxResult>().fail().setData(new AjaxResult("保存失败"));

    }

}
