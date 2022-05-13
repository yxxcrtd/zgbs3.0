package com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.controller;

import com.joyintech.background.component.common.pack.common.grid.Page;
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
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;

import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProResponse;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProSaveRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.service.TradeInfoPro001Service;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
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
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static String CONTAIN_DIGIT_REGEX = ".*[0-9].*";

    @ApiOperation(value = "交易商信息维护页面初始化参数", notes = "交易商信息维护查询页面初始化参数")
    @PostMapping("/getPageParams001")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams001() {
        Map<String, Object> initPageParams = new HashMap<>();
//        initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));

        Date platformDate = DateUtil.platDateFromCache();
        initPageParams.put("iPlatformDate", DateUtil.format(platformDate));

        initPageParams.put("YES_OR_NO",this.getSelectDataFromDataDic(DictCode.是否,null));
        initPageParams.put("DEALER_PARTY_TYPE",this.getSelectDataFromDataDic(DictCode.交易主体类型,null));

        initPageParams.put("TRADE_MARKET_03", DictCodeList.交易流通场所_上海证券交易所.getValue());
        initPageParams.put("TRADE_MARKET_04", DictCodeList.交易流通场所_深圳证券交易所.getValue());
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.交易场所, null);
                List<SelectOptionDto> jys = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt3.交易场所_上海证券交易所.getValue())
                    || StringUtil.equals(option.getId(), DictCodeListExt3.交易场所_深圳证券交易所.getValue());
        }).collect(Collectors.toList());
        initPageParams.put("TRADE_PLACE", JsonUtils.objectArray2Json(jys.toArray()));
        return new Message<Map<String, Object>>(initPageParams).success();
    }


    @ApiOperation(value = "获取交易商名称", notes = "获取交易商名称")
    @ResponseBody
    @PostMapping("/getCustomerName")
    public Message<List<SelectOptionDto>> getCustomerName(){
        try {
//            Map<String, Object> map = BeanUtil.object2Map(dto);
            List<SelectOptionDto> tempList= tradeInfoPro001Service.getCustomerName();
            return new Message<List<SelectOptionDto>>().success().setData(tempList);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<List<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }

    @ApiOperation(value = "交易商信息查询", notes = "交易商信息查询")
    @ResponseBody
    @PostMapping("/getDealerTraderInfo")
    public Message<Page<TradeInfoProResponse>> selectTraderInfo(@RequestBody TradeInfoProRequest dto ){
        try {
            Page<TradeInfoProResponse> page= tradeInfoPro001Service.getDealerTraderInfo(dto);
            return new Message<Page<TradeInfoProResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<TradeInfoProResponse>>().fail().setMessage("查询异常");
        }
    }

    /**
     * 删除交易商信息及其详细信息
     * @param dto
     * @return
     */
    @ApiOperation(value = "删除交易商及其详情", notes = "删除交易商及其详情")
    @ResponseBody
    @PostMapping("deleteDealer")
    public Message deleteDealer(@RequestBody TradeInfoProSaveRequest dto){
        String dealerName = dto.getDealerName();
             //根据页面上交易商名称查找客户代码
//              String customerId = tradeInfoPro001Service.findCustomerIdByDealerName(dealerName);
              dto.setCustomerId(dealerName);

        try{
              deleteDealer1(dto);
//             int count = tradeInfoPro001Service.deleteDealer(dto);
             if (tradeInfoPro001Service.hasDealerDetails(dealerName)>0){
                 //删除该交易商对应的详情信息
//                 tradeInfoPro001Service.deleteDealerDetails(customerId);
                 deleteDealerDetails1(dto);
             }
             return new Message().success();
        }catch (Exception e){
            logger.error("删除功能异常", e);
            return new Message().fail().setMessage("删除异常");

        }

    }

    private int deleteDealer1(TradeInfoProSaveRequest dto) {
        return tradeInfoPro001Service.deleteDealer(dto);
    }


    private int deleteDealerDetails1(TradeInfoProSaveRequest dto) {
        return tradeInfoPro001Service.deleteDealerDetails(dto);
    }
    /**
     * 删除交易主体和交易员信息
     * @param dto
     * @param dto
     * @return
     */
    @ApiOperation(value = "删除交易主体和交易员信息", notes = "删除交易主体和交易员信息")
    @ResponseBody
    @PostMapping("deleteTrader")
    public Message deleteTrader(@RequestBody TradeInfoProSaveRequest dto){
        String dealerName = dto.getDealerName();
        //根据页面上交易商名称查找客户代码
//        String customerId = tradeInfoPro001Service.findCustomerIdByDealerName(dealerName);
        dto.setCustomerId(dealerName);
        String dealerPartyNo =  dto.getDealerPartyNo();
        try{
            if (StringUtil.equals(dto.getTradeMarket(),"03")){
                tradeInfoPro001Service.deleteTraderSJ(dto);
            }else if(StringUtil.equals(dto.getTradeMarket(),"04")){
                //删除交易主体和交易员信息
                tradeInfoPro001Service.deleteTrader(dto);
            }

            return new Message().success();
        }catch (Exception e){
            logger.error("删除功能异常", e);
            return new Message().fail().setMessage("删除异常");

        }
    }

    /**
     * 新增交易商信息
     */
    @ApiOperation(value = "新增交易商信息", notes = "新增交易商信息")
    @ResponseBody
    @PostMapping("insertDealer")
    public Message insertDealer (@RequestBody TradeInfoProSaveRequest dto){
        TradeInfoProSaveRequest tradeInfoProSaveRequest = new TradeInfoProSaveRequest();
        BeanUtil.copyPropertiesIgnoreNull(tradeInfoProSaveRequest,dto);
//        String dealerName = dto.getDealerName();
//        //根据页面上交易商名称查找客户代码
//        String customerId = tradeInfoPro001Service.findCustomerIdByDealerName(dealerName);//客户代码，交易商代码不能改
        dto.setCustomerId(dto.getDealerName());
        if (tradeInfoProSaveRequest.getDealerNo().getBytes().length>50){
            return new Message().fail().setMessage("交易商输入的字符过长，请重新输入!");
        }
        try{
            if(existsDealer(tradeInfoProSaveRequest)>0){
                return new Message().fail().setMessage("交易商名称和交易商代码已存在，请勿重复插入！");
            }else if (existsDealerAndTradeMarket(tradeInfoProSaveRequest)>0){
                return new Message().fail().setMessage("交易商和交易场所不能重复！");
            }
            //新增交易商信息
             insertDealer1(dto);
            return new Message().success();
        }catch (Exception e){
            logger.error("新增功能异常", e);
            return new Message().fail().setMessage("新增异常");

        }
    }

    private int insertDealer1(TradeInfoProSaveRequest dto) {
        return tradeInfoPro001Service.insertDealer(dto);
    }

    /**
     * 新增交易商详情信息
     */
    @ApiOperation(value = "新增交易商详情信息", notes = "新增交易商详情信息")
    @ResponseBody
    @PostMapping("insertDealerDetails")
    public Message insertDealerDetails (@RequestBody TradeInfoProSaveRequest dto){
        TradeInfoProSaveRequest tradeInfoProSaveRequest = new TradeInfoProSaveRequest();
        BeanUtil.copyPropertiesIgnoreNull(tradeInfoProSaveRequest,dto);
//        String dealerName = dto.getDealerName();
//        //根据页面上交易商名称查找客户代码
//        String customerId = tradeInfoPro001Service.findCustomerIdByDealerName(dealerName);//客户代码，交易商代码不能改
        tradeInfoProSaveRequest.setCustomerId(dto.getDealerName());
        BussinessCodeParameter param = new BussinessCodeParameter();
        String seqNo = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param): IDGenerator.generate();
        tradeInfoProSaveRequest.setSeqNo(seqNo);
        if (tradeInfoProSaveRequest.getTrader().getBytes().length>50){
            return new Message().fail().setMessage("交易员名称输入的字符过长，请重新输入!");
        }
        if (tradeInfoProSaveRequest.getTraderNo().getBytes().length>32){
            return new Message().fail().setMessage("交易员代码输入的字符过长，请重新输入!");
        }
        if (StringUtil.equals(dto.getTradeMarket(),"04")){
            if (tradeInfoProSaveRequest.getDealerPartyNo().getBytes().length>50){
                return new Message().fail().setMessage("交易主体代码输入的字符过长，请重新输入!");
            }
            if (tradeInfoProSaveRequest.getDealerPartyName().getBytes().length>50){
                return new Message().fail().setMessage("交易主体名称输入的字符过长，请重新输入!");
            }
        }
        try{
               //一个交易商只能维护一个 本方交易员
            if (existsOur(tradeInfoProSaveRequest)>0 && StringUtil.equals(tradeInfoProSaveRequest.getIsOur(),"Y")){
                return new Message().fail().setMessage("一个交易商只能维护一个本方交易员!");
            }
            //上交所新增交易主体时，同一交易商+交易场所下，交易员代码应不允许重复
            if(StringUtil.equals(dto.getTradeMarket(),"03")&& existsTrader(tradeInfoProSaveRequest)>0){
                   return new Message().fail().setMessage("交易员代码不允许重复!");
            }
            //深交所交易主体类型为机构经济/个人经济时，交易主体代码应只允许维护一条
            if(StringUtil.equals(dto.getTradeMarket(),"04")&&(StringUtil.equals(dto.getDealerPartyType(),"03"))){
                 if (existsDealerParty(dto)>0){
                     return new Message().fail().setMessage("深交所交易主体类型为机构经济时,交易主体代码应只允许维护一条!");
                 }
            }
            if(StringUtil.equals(dto.getTradeMarket(),"04")&&(StringUtil.equals(dto.getDealerPartyType(),"04"))){
                if (existsDealerParty(dto)>0){
                    return new Message().fail().setMessage("深交所交易主体类型为个人经济时,交易主体代码应只允许维护一条!");
                }
            }

               //新增交易商详情信息
            insertDealerDetails1(tradeInfoProSaveRequest);
            return new Message().success();

        }catch (Exception e){
            logger.error("新增功能异常", e);
            return new Message().fail().setMessage("新增异常");

        }
    }

    private int existsDealerParty(TradeInfoProSaveRequest dto) {
        return tradeInfoPro001Service.existsDealerParty(dto);
    }

    private int existsOur(TradeInfoProSaveRequest dto) {
        return tradeInfoPro001Service.existsOur(dto);
    }

    private int existsTrader(TradeInfoProSaveRequest dto) {
        return tradeInfoPro001Service.existsTrader(dto);
    }

    private int existsDealerAndTradeMarket(TradeInfoProSaveRequest dto) {
        return tradeInfoPro001Service.existsDealerAndTradeMarket(dto);
    }

    private int existsDealer(TradeInfoProSaveRequest dto) {
        return tradeInfoPro001Service.existsDealer(dto);
    }

    private int insertDealerDetails1(TradeInfoProSaveRequest dto) {
        return tradeInfoPro001Service.insertDealerDetails(dto);
    }


    /**
     * 修改交易商信息
     */
    @ApiOperation(value = "修改交易商信息", notes = "修改交易商信息")
    @ResponseBody
    @PostMapping("editDealer")
    public Message editDealer (@RequestBody TradeInfoProSaveRequest dto){
        if (dto.getDealerNo().getBytes().length>50){
            return new Message().fail().setMessage("交易商代码输入的字符过长，无法修改，请重新输入!");
        }
        String dealerName = dto.getDealerName();
        //根据页面上交易商名称查找客户代码
        String customerId = tradeInfoPro001Service.findCustomerIdByDealerName(dealerName);//客户代码，交易商代码不能改
        dto.setCustomerId(dealerName);
        try{
            if(existsDealer(dto)>0){
                return new Message().fail().setMessage("交易商名称和交易商代码不能重复！");
            }else if (existsDealerAndTradeMarket(dto)>0){
                return new Message().fail().setMessage("交易商和交易场所不能重复！");
            }
//              deleteDealer1(dto);
//              TradeInfoProSaveRequest tradeInfoProSaveRequest = new TradeInfoProSaveRequest();
//              BeanUtil.copyPropertiesIgnoreNull(tradeInfoProSaveRequest,dto);
//              insertDealer1(tradeInfoProSaveRequest);
            tradeInfoPro001Service.updateByCustomerId(dto);
              return new Message().success();
        }catch (Exception e){
              logger.error("修改功能异常", e);
              return new Message().fail().setMessage("修改异常");

        }

    }

    /**
     * 修改交易商详情信息
     */
    @ApiOperation(value = "修改交易商详情信息", notes = "修改交易商详情信息")
    @ResponseBody
    @PostMapping("editDealerDetails")
    public Message editDealerDetails (@RequestBody TradeInfoProSaveRequest dto){
        if (dto.getTrader().getBytes().length>50){
            return new Message().fail().setMessage("交易员名称输入的字符过长，请重新输入!");
        }
        if (dto.getTraderNo().getBytes().length>32){
            return new Message().fail().setMessage("交易员代码输入的字符过长，请重新输入!");
        }
        if (StringUtil.equals(dto.getTradeMarket(),"04")){
            if (dto.getDealerPartyNo().getBytes().length>50){
                return new Message().fail().setMessage("交易主体代码输入的字符过长，请重新输入!");
            }
            if (dto.getDealerPartyName().getBytes().length>50){
                return new Message().fail().setMessage("交易主体名称输入的字符过长，请重新输入!");
            }
        }
        //一个交易商只能维护一个 本方交易员
        if (existsOur(dto)>0 && StringUtil.equals(dto.getIsOur(),"Y")){
            return new Message().fail().setMessage("一个交易商只能维护一个本方交易员!");
        }
        try{
//            deleteDealerDetails1(dealerName);
//            TradeInfoProSaveRequest tradeInfoProSaveRequest = new TradeInfoProSaveRequest();
//            BeanUtil.copyPropertiesIgnoreNull(tradeInfoProSaveRequest,dto);
//            BussinessCodeParameter param = new BussinessCodeParameter();
//            String seqNo = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param): IDGenerator.generate();
//            tradeInfoProSaveRequest.setSeqNo(seqNo);
//            insertDealerDetails1(tradeInfoProSaveRequest);
            int n = 0;
            if (StringUtil.equals(dto.getTradeMarket(),"03")){
                if(existsTrader(dto)>0 && !StringUtil.equals(dto.getTraderNo(),dto.getTraderNoPre())){
                    return new Message().fail().setMessage("交易员代码已经存在,不允许重复!");
                }
                 n = tradeInfoPro001Service.updateDetailsSJ(dto);
            }else if(StringUtil.equals(dto.getTradeMarket(),"04")){
                 n =tradeInfoPro001Service.updateDetails(dto);
            }

            if (n == 0){
                return new Message().fail().setMessage("您还没修改！");
            }
            return new Message().success();
        }catch (Exception e){
            logger.error("修改功能异常", e);
            return new Message().fail().setMessage("修改异常");

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
//    @ApiOperation(value = "保存提交", notes = "保存提交")
//    @PostMapping("save")
//    @ResponseBody
//    public Message<AjaxResult> save(@RequestBody TradeInfoProSaveRequest dto, BindingResult br){
//        try {
//            if (br.hasErrors()) {
//                return new Message<AjaxResult>().fail().setData(new AjaxResult(br));
//            }
//            String msg ="";
//            if (StringUtil.equals("save", dto.getOperateType())) {  //保存没有check和任何限制
//                    //业务编号生成规则参数
//                    ProcessParameter<TradeInfoProSaveRequest> processParameter = new ProcessParameter<TradeInfoProSaveRequest>();
//                    msg = tradeInfoPro001Service.start(processParameter, dto);
//                    return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
//                }
//            } catch (Exception e){
//            e.printStackTrace();
//            }
//            return new Message<AjaxResult>().fail().setData(new AjaxResult("保存失败"));
//
//    }

}
