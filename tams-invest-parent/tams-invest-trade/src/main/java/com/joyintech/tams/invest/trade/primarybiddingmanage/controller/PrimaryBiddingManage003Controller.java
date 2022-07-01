package com.joyintech.tams.invest.trade.primarybiddingmanage.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;

import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.trade.common.aop.TradeRdsCommonAop;
import com.joyintech.tams.invest.trade.common.dto.EtrInvestInfoDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.primarybiddingmanage.dto.BidOrderBidPriceDto;
import com.joyintech.tams.invest.trade.primarybiddingmanage.dto.TrdBidPriceDto;
import com.joyintech.tams.invest.trade.primarybiddingmanage.dto.TrdBiddingManage003Dto;
import com.joyintech.tams.invest.trade.primarybiddingmanage.service.PrimaryBiddingManage003Service;
import com.joyintech.tams.invest.trade.primarybiddingmanage.validator.PrimaryBidPriceValidator;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@PageId("PrimaryBiddingManage003")
@RequestMapping("/invest/trade/primarybiddingmanage/PrimaryBiddingManage003")
public class PrimaryBiddingManage003Controller extends BaseController {
	
	   @Autowired
	    private TradeCommonService tradeCommonService;
	   
	   @Autowired
	   private PrimaryBiddingManage003Service primaryBiddingManage003;

	   @Autowired
	   private PrimaryBidPriceValidator primaryBidPriceValidator;
	   /**
	     * 需要初始化的下拉菜单
	     */
	    private static final IDictCode[] DICT_CODES = new BussinessDictCode[] {BussinessDictCode.缴款方式, BussinessDictCode.中标方式,
	        BussinessDictCode.是否, BussinessDictCode.交易场所};
	   
    @Override
    public String init(Model model) {
    	 this.setSelectOptionToModel(model, DICT_CODES);
    	 String bidId = getParam("bidId");
    	 Map<String, Object> map = RequestUtil.getPageParam();
    	 String workflowId = getParam("workflowId");
    	 TrdBiddingManage003Dto entity=new TrdBiddingManage003Dto();
    	 // 待办跳转
         if (!WorkFlowUtil.isNotFromToDoList()&&StringUtil.isNotEmpty(workflowId)) {
             model.addAttribute("workflowId", workflowId);
             entity= primaryBiddingManage003.getBussinessEntityByWorkflowId(workflowId);
//             不能直接在这修改，会将流程中的数据均改变
//             if(entity.getAllBidPrinAmt() != null) {
//                 entity.setAllBidPrinAmt(entity.getAllBidPrinAmt().multiply(new BigDecimal(10000)));//转成元
//             }
         } else if (WorkFlowUtil.isNotFromToDoList() &&StringUtil.isNotEmpty(bidId)){
        	 entity=primaryBiddingManage003.getBussinessEntityByBidId(bidId);
             entity.setAllBidPrinAmt(ArithmeticUtil.multiply(new BigDecimal(map.get("allBidPrinAmt").toString()),new BigDecimal(10000)));
             // 委托的场合
             EtrInvestInfoDto etrInvestInfoDto = tradeCommonService.findEtrInfo(entity.getEntrustId());
             if (etrInvestInfoDto != null) {
                // entity = genTradeDto(etrInvestInfoDto);
                 TrdInvestPersonInfo002Dto info002Dto = new TrdInvestPersonInfo002Dto();
                 info002Dto.setCounterId(etrInvestInfoDto.getEtrEntrust().getCounterId());
                 info002Dto.setCounterTrader(etrInvestInfoDto.getEtrEntrust().getCounterTrader());
                 info002Dto.setContactInfo(etrInvestInfoDto.getEtrEntrust().getContactInfo());
                 info002Dto.setIsOutTrade(etrInvestInfoDto.getEtrEntrust().getIsOutTrade());
                 info002Dto.setTrader(LoginUserUtil.getUserName());
                 info002Dto.setTraderDept(LoginUserUtil.getDeptCode());
                 entity.setTrdInvestPersonInfo(info002Dto);
             }
             List<BidOrderBidPriceDto> bidDetailByBidId = primaryBiddingManage003.getBidDetailByBidId(bidId);
             //如果是单标位
             if ("01".equals(entity.getWinType())){
                 Map<String,List<BidOrderBidPriceDto>> mapTaskMap=bidDetailByBidId.stream().collect(Collectors.groupingBy(BidOrderBidPriceDto::getEntrustId));
                 List<BidOrderBidPriceDto> bidOrderBidPriceDtos = new ArrayList<>();
                 for (String entrustId : mapTaskMap.keySet()) {
                     BidOrderBidPriceDto bidOrderBidPriceDto = mapTaskMap.get(entrustId).get(0);
                     BigDecimal bidPriceAmt = BigDecimal.ZERO;
                     String bidPriceStr = "";
                     for (BidOrderBidPriceDto dto : bidDetailByBidId) {
                         bidPriceAmt = ArithmeticUtil.doAdd(dto.getBidPrinAmt(),bidPriceAmt);
                         bidPriceStr += dto.getBidPrice().toString() + ",";
                     }
                     bidOrderBidPriceDto.setBidPriceStr(bidPriceStr.substring(0,bidPriceStr.length()-1));
                     bidOrderBidPriceDto.setBidPrinAmt(bidPriceAmt);
                     bidOrderBidPriceDtos.add(bidOrderBidPriceDto);
                 }

                 entity.setBidOrderBidPriceList(bidOrderBidPriceDtos);
             }else {
                 for (BidOrderBidPriceDto bidOrderBidPriceDto : bidDetailByBidId) {
                     bidOrderBidPriceDto.setBidPriceStr(bidOrderBidPriceDto.getBidPrice().toString());
                 }
                 if (!bidDetailByBidId.isEmpty()){
                     entity.setBidOrderBidPriceList(bidDetailByBidId);
                 }
             }
         }
         //配置单位 edit by yeming 20210805 start
         if(entity.getWinPrinAmt() != null) {
             entity.setWinPrinAmt(entity.getWinPrinAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
         }
        if(entity.getAllBidPrinAmt() != null) {
            entity.setAllBidPrinAmt(entity.getAllBidPrinAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
        }
         List<BidOrderBidPriceDto> BidOrderBidPriceDtoList = entity.getBidOrderBidPriceList();
         for (BidOrderBidPriceDto dto : BidOrderBidPriceDtoList) {
             if(dto.getBidPrinAmt() != null) {
                 dto.setBidPrinAmt(dto.getBidPrinAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
             }
             if(dto.getTheWinPrinAmt() != null) {
                 dto.setTheWinPrinAmt(dto.getTheWinPrinAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
             }
             if(dto.getWinPrinAmt() != null) {
                 dto.setWinPrinAmt(dto.getWinPrinAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
             }
             if(StringUtil.isEmpty(dto.getIsFrozenPosition())){
                 dto.setIsFrozenPosition("N");
             }
         }
         //配置单位 edit by yeming 20210805 end
         if (entity.getTrdInvestPersonInfo() != null) {
             model.addAttribute("trdInvestPersonInfo002Dto", JsonUtils.object2Json(entity.getTrdInvestPersonInfo()));
         }
         model.addAttribute(GlobalConstant.ENTITY, entity);
        model.addAttribute("tradeId", entity.getBidId());
        model.addAttribute("entrustAmtUnit", CacheUtil.getSysParam("entrustAmtUnit"));

        model.addAttribute("MY_SUP_TIME_TYPE_02", BussinessDictCodeList.监督时分_委托监控.getValue());
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate", DateUtil.format(sysDate));

        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> BaseRiskMathCommMap = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(BaseRiskMathCommMap);
        model.addAttribute("isEleShow", isEleShow);

        model.addAttribute("TRD61", BussinessDictCodeList.共同业务类型_中标结果维护.getValue());

        return super.init(model);
    }
    /**
     * 获取维护撤销流程参数
     * @return
     */
    @ResponseBody
    @RequestMapping("getRevokeParam")
    public TrdBiddingManage003Dto getRevokeParam(@RequestBody TrdBiddingManage003Dto inputDto) {

        TrdBiddingManage003Dto entity = primaryBiddingManage003.getBussinessEntityByBidId(inputDto.getBidId());
        List<BidOrderBidPriceDto> bidDetailByBidId = primaryBiddingManage003.getBidDetailByBidId(entity.getBidId());
        for (BidOrderBidPriceDto bidOrderBidPriceDto : bidDetailByBidId) {
            bidOrderBidPriceDto.setBidPriceStr(bidOrderBidPriceDto.getBidPrice().toString());
        }
        if (!bidDetailByBidId.isEmpty()) {
            entity.setBidOrderBidPriceList(bidDetailByBidId);
        }
        // 委托的场合
        EtrInvestInfoDto etrInvestInfoDto = tradeCommonService.findEtrInfo(entity.getEntrustId());
        if (etrInvestInfoDto != null) {
            // entity = genTradeDto(etrInvestInfoDto);
            TrdInvestPersonInfo002Dto info002Dto = new TrdInvestPersonInfo002Dto();
            info002Dto.setCounterId(etrInvestInfoDto.getEtrEntrust().getCounterId());
            info002Dto.setCounterTrader(etrInvestInfoDto.getEtrEntrust().getCounterTrader());
            info002Dto.setContactInfo(etrInvestInfoDto.getEtrEntrust().getContactInfo());
            info002Dto.setIsOutTrade(etrInvestInfoDto.getEtrEntrust().getIsOutTrade());
            info002Dto.setTrader(LoginUserUtil.getUserName());
            info002Dto.setTraderDept(LoginUserUtil.getDeptCode());
            entity.setTrdInvestPersonInfo(info002Dto);
        }
        return entity;
    }

    /**
     * 自动分配
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("findBidPrice")
    public List<BidOrderBidPriceDto> findBidPricetest(@RequestBody TrdBiddingManage003Dto inputDto) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("finprodId", inputDto.getFinprodId());
        params.put("counterId",inputDto.getCounterId());
        params.put("bidId",inputDto.getBidId());
        List<BidOrderBidPriceDto> pageBidPrice = primaryBiddingManage003.findBidPrice(params);
        BigDecimal totalPrice = BigDecimal.ZERO;
        Map<BigDecimal,BigDecimal> bidPriceMap = new HashMap<>();
        List<BidOrderBidPriceDto> resultList = new ArrayList<BidOrderBidPriceDto>();
//
        Map<BigDecimal,BigDecimal> adjustMap = new HashMap<>();
        //汇总统一标位，该标位的总委托券面总额  maptotoal<mprice,totoal>
        for(BidOrderBidPriceDto dto:pageBidPrice){
            if (bidPriceMap.containsKey(dto.getBidPrice())){
                totalPrice = ArithmeticUtil.doAdd(bidPriceMap.get(dto.getBidPrice()),dto.getBidPrinAmt());
                bidPriceMap.put(dto.getBidPrice(),totalPrice);
                adjustMap.put(dto.getBidPrice(),totalPrice);
            }else {
                bidPriceMap.put(dto.getBidPrice(),dto.getBidPrinAmt());
                adjustMap.put(dto.getBidPrice(),dto.getBidPrinAmt());
            }
        }
        //已经分配的总额
        BigDecimal totalPLATPrice = BigDecimal.ZERO;
        //获取第一个标位
        BigDecimal firstBidPrice = pageBidPrice.get(0).getBidPrice();
        //是否分配完
        boolean isEnd = false;
        //总中标标位
        BigDecimal subtract = inputDto.getWinPrinAmt();
        //中标的标位
        BigDecimal winPrice =inputDto.getWinPrice();
        //遍历集合分配额度
        for (int i = 0; i < pageBidPrice.size(); i++){
            BidOrderBidPriceDto dto = pageBidPrice.get(i);
            //如果是多位标
            if ("02".equals(inputDto.getWinType())){
            	if(dto.getBidPrice().compareTo(winPrice)>0) {
            		bidPriceMap.remove(dto.getBidPrice());
            		adjustMap.remove(dto.getBidPrice());
            		continue;
            	}
            }
            if (firstBidPrice.compareTo(dto.getBidPrice())!=0){
                subtract = ArithmeticUtil.subtract(inputDto.getWinPrinAmt(),totalPLATPrice);
                firstBidPrice = dto.getBidPrice();
            }
            //判断剩余中标总额是否大于该标位的总额，大于等于则直接分配，小于则按比例分配
            if (subtract.compareTo(bidPriceMap.get(dto.getBidPrice()))>=0){
                dto.setWinPrice(dto.getBidPrice());
                dto.setTheWinPrinAmt(dto.getBidPrinAmt());
                dto.setBidPriceStr(dto.getBidPrice().toString());
                dto.setWinPrinAmt(dto.getTheWinPrinAmt());
//
            }else {
                //理论分配额度 = 该投标总额 / 该标位的总额 * 剩余分配总额
                BigDecimal bigDecimal = bidPriceMap.get(dto.getBidPrice());
//                BigDecimal subtract = inputDto.getWinPrinAmt();
//                if (firstBidPrice.compareTo(dto.getBidPrice())!=0){
//                    subtract = ArithmeticUtil.subtract(inputDto.getWinPrinAmt(), totalPLATPrice);
//                    firstBidPrice = dto.getBidPrice();
//                }
                //如果剩余分配额度小于等于0，则中标标位，额度置空
                if (subtract.doubleValue()<=0 || isEnd){
                    dto.setWinPrice(null);
                    dto.setTheWinPrinAmt(null);
                    dto.setBidPriceStr(dto.getBidPrice().toString());
                    dto.setWinPrinAmt(dto.getTheWinPrinAmt());
                    if (null!=dto.getWinPrice()){
                        BigDecimal winCprice = primaryBiddingManage003.calculateWinCprice(params, dto.getWinPrice(), dto.getWinPrinAmt(), dto.getEntrustId());
                        dto.setWinCprice(winCprice.setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                    resultList.add(dto);
                    isEnd = true;
                    continue;
                }
                //理论分配额度
                double theWinPrinAmt = dto.getBidPrinAmt().doubleValue() / bigDecimal.doubleValue() * subtract.doubleValue();
                //该标位的最后一个元素，且是最后一个中标，则倒减
                if ((i+1) < pageBidPrice.size()
                    && dto.getBidPrinAmt().doubleValue() - theWinPrinAmt >= 0
                    && dto.getBidPrice().compareTo(pageBidPrice.get(i+1).getBidPrice())!=0 ){
                    dto.setWinPrice(dto.getBidPrice());
                    dto.setTheWinPrinAmt(ArithmeticUtil.subtract(inputDto.getWinPrinAmt(), totalPLATPrice));
                    dto.setBidPriceStr(dto.getBidPrice().toString());
                    dto.setWinPrinAmt(dto.getTheWinPrinAmt());
                    adjustMap.put(dto.getBidPrice(),ArithmeticUtil.subtract(adjustMap.get(dto.getBidPrice()),dto.getTheWinPrinAmt()));
                    if (null!=dto.getWinPrice()){
                        BigDecimal winCprice = primaryBiddingManage003.calculateWinCprice(params, dto.getWinPrice(), dto.getWinPrinAmt(), dto.getEntrustId());
                        dto.setWinCprice(winCprice.setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                    resultList.add(dto);
                    totalPLATPrice=ArithmeticUtil.doAdd(dto.getTheWinPrinAmt(),totalPLATPrice);
                    isEnd = true;
                    continue;
                }
                if (i == pageBidPrice.size()-1){
                    //最后一个元素且中标倒减
                    theWinPrinAmt = inputDto.getWinPrinAmt().doubleValue() - totalPLATPrice.doubleValue()
                        >=dto.getBidPrinAmt().doubleValue()?dto.getBidPrinAmt().doubleValue()
                        :inputDto.getWinPrinAmt().doubleValue() - totalPLATPrice.doubleValue();
                }
                //该标位最终结果
                BigDecimal resultPrinAmt = new BigDecimal(theWinPrinAmt).setScale(2,BigDecimal.ROUND_HALF_UP);
//                BigDecimal resultPrinAmt = BigDecimal.valueOf(Math.round(theWinPrinAmt / 100.0) * 100.0);

                dto.setTheWinPrinAmt(resultPrinAmt);
                dto.setWinPrice(dto.getBidPrice());
                dto.setBidPriceStr(dto.getBidPrice().toString());
                dto.setWinPrinAmt(dto.getTheWinPrinAmt());
            }
            totalPLATPrice=ArithmeticUtil.doAdd(dto.getTheWinPrinAmt(),totalPLATPrice);
//            更新每个标位的剩余金额
            adjustMap.put(dto.getBidPrice(),ArithmeticUtil.subtract(adjustMap.get(dto.getBidPrice()),dto.getTheWinPrinAmt()));
            if (null!=dto.getWinPrice()){
                BigDecimal bigDecimal = primaryBiddingManage003.calculateWinCprice(params, dto.getWinPrice(), dto.getWinPrinAmt(), dto.getEntrustId());
                if (bigDecimal != null) {
                    dto.setWinCprice(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
            resultList.add(dto);
        }

        //如果是单标位
        if ("01".equals(inputDto.getWinType())){
            Map<String,List<BidOrderBidPriceDto>> mapTaskMap=resultList.stream().collect(Collectors.groupingBy(BidOrderBidPriceDto::getEntrustId));
            List<BidOrderBidPriceDto> result = new ArrayList<BidOrderBidPriceDto>();
            for (String key : mapTaskMap.keySet()) {
                List<BidOrderBidPriceDto> bidOrderBidPriceDtos = mapTaskMap.get(key);
                BigDecimal total = BigDecimal.ZERO;
                BigDecimal theTotal = BigDecimal.ZERO;
                String bidPriceStr="";
                BidOrderBidPriceDto bidOrderBidPriceDto = bidOrderBidPriceDtos.get(0);
                for (BidOrderBidPriceDto dto : bidOrderBidPriceDtos) {
                    bidPriceStr += dto.getBidPrice().toString()+",";
                    total = ArithmeticUtil.doAdd(dto.getBidPrinAmt(),total);
                    if (null != dto.getTheWinPrinAmt()){
                        theTotal = ArithmeticUtil.doAdd(dto.getTheWinPrinAmt(),theTotal);
                    }else {
                        theTotal = ArithmeticUtil.doAdd(BigDecimal.ZERO,theTotal);
                    }
                }
                bidOrderBidPriceDto.setBidPriceStr(bidPriceStr.substring(0,bidPriceStr.length()-1));
                bidOrderBidPriceDto.setBidPrinAmt(total);//投标券面总额
                bidOrderBidPriceDto.setWinPrice(inputDto.getWinPrice());//中标标位
                if (theTotal.compareTo(BigDecimal.ZERO)<=0){
                    bidOrderBidPriceDto.setWinPrice(null);
                    theTotal = null;
                }
                bidOrderBidPriceDto.setTheWinPrinAmt(theTotal);
                bidOrderBidPriceDto.setWinPrinAmt(bidOrderBidPriceDto.getTheWinPrinAmt());
                if (null!=bidOrderBidPriceDto.getWinPrice()){
                    BigDecimal winCprice = primaryBiddingManage003.calculateWinCprice(params, bidOrderBidPriceDto.getWinPrice(), bidOrderBidPriceDto.getWinPrinAmt(), bidOrderBidPriceDto.getEntrustId());
                    bidOrderBidPriceDto.setWinCprice(winCprice.setScale(2, BigDecimal.ROUND_HALF_UP));
                }
                result.add(bidOrderBidPriceDto);
            }
            return result;
        }
        else{
            //        调整中签量
            String adjustWinningAmount = CacheUtil.getSysParam(BussinessConstant.ADJUST_WINNING_AMOUNT)==null?"N": CacheUtil.getSysParam(BussinessConstant.ADJUST_WINNING_AMOUNT);
            if(!StringUtil.equals(adjustWinningAmount,"N")&& CollectionUtils.isNotEmpty(resultList)){
                BigDecimal unitAdjustAmt = new BigDecimal(adjustWinningAmount);
//                检测需要处理的标位 {可能出现需要调整的标位满足两个要求1：当前的标位分配了额度，但没有分配全部,2:当前标位产品不唯一}
                for (BigDecimal bidPrice:adjustMap.keySet()){
//                    当前的标位分配了额度，但没有分配全部
                    if(BigDecimal.ZERO.compareTo(adjustMap.get(bidPrice))!=0&&adjustMap.get(bidPrice).compareTo(bidPriceMap.get(bidPrice))!=0){
                        List<BidOrderBidPriceDto>tempList = resultList.stream().filter(x->x.getBidPrice().equals(bidPrice)).collect(Collectors.toList());
                        primaryBiddingManage003.adjustBidOrderList(tempList,unitAdjustAmt);
                    }
                }
            }
        }
        return resultList;
    }


    
//    /**
//     * 自动分配
//     *
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping("findBidPrice")
//    public List<BidOrderBidPriceDto> findBidPrice(@RequestBody TrdBiddingManage003Dto inputDto) {
//    	Map<String, Object> params=new HashMap<String,Object>();
//    	params.put("finprodId", inputDto.getFinprodId());
//    	Page<BidOrderBidPriceDto> pageBidPrice= primaryBiddingManage003.findBidPrice(params);
//    	BigDecimal totalDownPrice=BigDecimal.ZERO;
//    	BigDecimal totalPLATPrice=BigDecimal.ZERO;
//
//    	List<BidOrderBidPriceDto> resultList=new ArrayList<BidOrderBidPriceDto>();
//    	for(BidOrderBidPriceDto dto:pageBidPrice.getRoot()){//汇总统一标位，总委托券面总额  maptotoal<mprice,totoal>
//
//            //小于中标标位的所有券面之和
//            if(inputDto.getWinPrice().compareTo(dto.getBidPrice())>0){
//                totalDownPrice=ArithmeticUtil.doAdd(dto.getBidPrinAmt(),totalDownPrice);
//            }
//            //等于中标标位的所有券面之和
//            if(inputDto.getWinPrice().compareTo(dto.getBidPrice())==0){
//                totalPLATPrice=ArithmeticUtil.doAdd(dto.getBidPrinAmt(),totalPLATPrice);
//            }
//        }
//    	list re
//        for(BidOrderBidPriceDto dto:pageBidPrice.getRoot()){//分配券面
//
//            //小于中标标位的所有券面之和
//            if(inputDto.getWinPrice().compareTo(dto.getBidPrice())>0){
//                dto.setWinPrinAmt(dto.getBidPrinAmt());
//                yifeiquanmian+=dto.getBidPrinAmt();
//            }
//            //大于等于中标标位的所有券面之和
//            else{
//
//                totalPLATPrice=maptotoal.get(dto.getBidPrice());
//                lilunfeizhi  = ( dto.getBidPrinAmt()/totalPLATPrice)*(inputDto.getAllBidPrinAmt()-yifeiquanmian);
//                是否是最后标位=( dto.getBidPrinAmt()-lilunfeizhi)>=0；
//                if（是否是最后标位&dto.getBidPrice()!=next(ddd).getBidPrice()){
//                    该标位最终结果=总中标标位-已分配标位；
//                    break；
//                }
//                未调整精度分配=   ( lilunfeizhi-dto.getBidPrinAmt())>=0?dto.getBidPrinAmt():lilunfeizhi;
//                该标位最终结果=round(未调整精度分配/100，0）*100;
//                已分配标位+=该标位最终结果；
//                dto.setWinPrinAmt(该标位最终结果);
//            }
//            resultList.add(dto);
//        }
//
//        if(danbiao){
//            循环结果  按委托id 汇总中标券面；
//
//
//        }
//
//
//    	//剩下给等于中标标位的券面余额
//    	BigDecimal userTotalPrice=ArithmeticUtil.subtract(inputDto.getWinPrinAmt(), totalDownPrice);
//    	List<BidOrderBidPriceDto> queryList=pageBidPrice.getRoot();
//    	//单标位模式
//		if("01".equals(inputDto.getWinType())){
//			Map<String,List<BidOrderBidPriceDto>> mapTaskMap=queryList.stream().collect(Collectors.groupingBy(BidOrderBidPriceDto::getEntrustId));
//			int keyEnt=0;
//			BigDecimal totalBidPriceAmtKey=BigDecimal.ZERO;
//			for(String key :mapTaskMap.keySet()){
//				BigDecimal totalTheWinPriceAmt=BigDecimal.ZERO;
//				BigDecimal totalBidPriceAmt=BigDecimal.ZERO;
//				BidOrderBidPriceDto retDto=new BidOrderBidPriceDto();
//				BeanUtil.copyPropertiesIgnoreNull(retDto,mapTaskMap.get(key).get(0));
//				keyEnt++;
//				String bidPriceStr="";
//				boolean isWin=false;
//				for(BidOrderBidPriceDto dto:mapTaskMap.get(key)){
//					if(inputDto.getWinPrice().compareTo(dto.getBidPrice())>=0){
//						bidPriceStr+=dto.getBidPrice().toString()+",";
//						totalBidPriceAmt=ArithmeticUtil.doAdd(dto.getBidPrinAmt(),totalBidPriceAmt);
//						isWin=true;
//						if(inputDto.getWinPrice().compareTo(dto.getBidPrice())>0) {
//							dto.setWinPrice(inputDto.getWinPrice());//设置中标标位
//							totalTheWinPriceAmt=ArithmeticUtil.doAdd(dto.getBidPrinAmt(),totalTheWinPriceAmt);
//						}
//						if(inputDto.getWinPrice().compareTo(dto.getBidPrice())==0){
//							dto.setWinPrice(inputDto.getWinPrice());//设置中标标位
//							BigDecimal theWinPrintAmt=ArithmeticUtil.multiply(userTotalPrice,ArithmeticUtil.divide(dto.getBidPrinAmt(), inputDto.getWinPrinAmt(),2, RoundingMode.HALF_UP));
//							totalTheWinPriceAmt=ArithmeticUtil.doAdd(theWinPrintAmt,totalTheWinPriceAmt);
//						}
//					}
//
//				}
//				if(isWin){
//					retDto.setBidPriceStr(bidPriceStr);//投标标位
//					retDto.setBidPrinAmt(new BigDecimal(Math.ceil(totalBidPriceAmt.doubleValue() / 100.0) * 100.0));//投标券面总额
//					retDto.setWinPrice(inputDto.getWinPrice());//中标标位
//					//最后一个倒减
//					if(keyEnt==mapTaskMap.keySet().size()){
//						retDto.setTheWinPrinAmt(ArithmeticUtil.subtract(inputDto.getWinPrinAmt(),totalBidPriceAmtKey));
//					}
//					else {
//						retDto.setTheWinPrinAmt(new BigDecimal(Math.ceil(totalTheWinPriceAmt.doubleValue() / 100.0) * 100.0));//理论券面总额
//						totalBidPriceAmtKey=ArithmeticUtil.doAdd(retDto.getTheWinPrinAmt(),totalBidPriceAmtKey);
//					}
//					resultList.add(retDto);
//				}
//			}
//		}
//		else {//多标位模式
//			for (BidOrderBidPriceDto dto : pageBidPrice.getRoot()) {
//				BidOrderBidPriceDto retDto = new BidOrderBidPriceDto();
//				if (inputDto.getWinPrice().compareTo(dto.getBidPrice()) > 0) {
//					dto.setWinPrice(dto.getBidPrice());//设置中标标位
//					dto.setTheWinPrinAmt(dto.getBidPrinAmt());//设置理论中标金额
//					resultList.add(dto);
//				}
//				if (inputDto.getWinPrice().compareTo(dto.getBidPrice()) == 0) {
//					dto.setWinPrice(dto.getBidPrice());//设置中标标位
//                    //BigDecimal bigDecimal = new BigDecimal(ArithmeticUtil.multiply(userTotalPrice, ArithmeticUtil.divide(dto.getBidPrinAmt(), inputDto.getWinPrinAmt(), 2, RoundingMode.HALF_UP)).doubleValue());
//                    double v = dto.getBidPrinAmt().doubleValue() / inputDto.getWinPrinAmt().doubleValue();
//                    BigDecimal bigDecimal = new BigDecimal(ArithmeticUtil.multiply(userTotalPrice, new BigDecimal(v)).doubleValue());
//                    BigDecimal bigScale = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
//                    dto.setTheWinPrinAmt(bigScale);
//					resultList.add(dto);
//				}
//			}
//		}
//    	return resultList;
//    }

    /**
     * 画面点击保存按钮时check
     *
     * @param dto 实体类
     * @param result
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("checkBidPrice")
    public AjaxResult checkBidPrice(@RequestBody TrdBidPriceDto dto, BindingResult result) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> valid = primaryBidPriceValidator.valid(dto);
        ajaxResult.setErrorList(valid);
        return ajaxResult;
    }

    /**
     * 画面点击保存按钮时check
     *
     * @param dto 实体类
     * @param result
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) TrdBiddingManage003Dto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }


    /**
     * 保存
     * @param dto dto
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) TrdBiddingManage003Dto dto, BindingResult br)
        throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // add by zhangrui1 调用风险限额预占 start

        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            AjaxResult ar = TradeRdsCommonAop.doPreOccupy(dto,
                    BussinessDictCodeListRds.接口系统ID_预占接口.getValue(),
                    BussinessDictCodeList.共同业务类型_中标结果维护.getValue());
            if (ar != null) {
                return ar;
            }
        }

        // add by zhangrui1 调用风险限额预占 end
        ProcessParameter<TrdBiddingManage003Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdBiddingManage003Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getBidId());
        params.setBusinessType(BussinessDictCodeList.共同业务类型_中标结果维护.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("投标编号：" + dto.getBidId(), "资产名称：" + entity.getFinprodAbbr(),
            "投标日：" + DateUtil.format(entity.getBidDate())));
        processParameter.setWorkFlowParameter(params);
        String msg = primaryBiddingManage003.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}
