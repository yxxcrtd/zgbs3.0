/*
 * 文件名：BussTrdTransferOrderController.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：杨智 修改时间：2018年09月27日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.component.business.trade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.settle.BussSettleOrderCommService;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.entrust.dao.EtrTrdCommonMapper;
import com.joyintech.tams.invest.common.trade.dao.TrdCommonBaseMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/component/trade/BussTrdTransferOrder")
public class BussTrdTransferOrderController extends BaseController {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(BussTrdTransferOrderController.class);

    /**
     * 清算指令共通Service
     */
    @Autowired(required = false)
    private BussSettleOrderCommService bussSettleOrderCommService;
    /**
	 * 主键生成类
	 */
	@Autowired(required = false)
	private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private TrdCommonBaseMapper trdCommonBaseMapper;

   @Autowired
    private EtrTrdCommonMapper etrTrdCommonMapper;

    /**
     * 共通Mapper的通用代理方案
     */

    /**
     * BussTrdTransferOrderService
     */
    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    @Override
    public String init(Model model) {
        if (!StringUtil.equals(this.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)
            || Boolean.valueOf(getParam("isTodoList"))
            || !StringUtil.equals(getParam("statusValid"),
                DictCodeListExt1.画面数据状态_待处理.getValue())) {
            String entityStr = this.getParam("entity");
            String bizCode = this.getParam("bizCode");
            BussTrdTransferOrderBase entity = StringUtil.isNotEmpty(
                    entityStr) ? JsonUtils.json2Object(entityStr,
                    BussTrdTransferOrderBase.class) : null;
            // bizCode修改为支持多个交易编号用逗号分隔 by renping 20190916 start
            List<BussTrdTransferOrderBase> list = new ArrayList<>();
            if (entity == null
                && (StringUtil.isEmpty(this.getParam("entityList"))
                    || "null".equals(this.getParam("entityList")))
                && StringUtil.isNotEmpty(bizCode)) {
                String[] bizCodeList = bizCode.split(",");
                for (String item : bizCodeList) {
                    list.addAll(bussSettleOrderCommService.selectOrdersByBizCode(item));
                }
                if (StringUtil.equals("bussFinProductFeePay",StringUtil.defaultString(this.getParam("entityName"),
                        BussTrdTransferOrderService.DEFAULT_ENTITY_NAME))) {
                    model.addAttribute("entityList", list);
                }else{
                    if (list != null && !list.isEmpty()) {
                        entity = list.get(0);
                    }
                }
            }
            // bizCode修改为支持多个交易编号用逗号分隔 by renping 20190916 end
            List<SelectOptionDto> acctNoList = bussTrdTransferOrderService.queryAcctList(null);
            // 不改变原有逻辑的基础上，新增处理逻辑，展示多条指令用  add by renping 2019-07-10 start
            if (StringUtil.isNotEmpty(this.getParam("isMulti")) && !StringUtil.isEmptyList(list)) {
                for (BussTrdTransferOrderBase item : list) {
                    item.setPayAcctNoList(acctNoList);
                    item.setRecAcctNoList(acctNoList);
                    StlCapacctInfo stlCapacctInfo = bussTrdTransferOrderService.queryStlCapacctInfoByBusiAcctNo(
                        item.getCcy(), item.getRecAcctNo());
                    if (stlCapacctInfo != null) {
                        item.setRecAcctPoint(stlCapacctInfo.getLargePayno());
                        if (StringUtil.isNotEmpty(stlCapacctInfo.getInOutBank())) {
                            item.setInOutBank(SysDataUtil.getNameFromDataDict(DictCode.资产来源,
                                stlCapacctInfo.getInOutBank()));
                        }
                    }
                }
                model.addAttribute("entityList", list);
            }
            // entityList参数非空时展示（工作流流程中场合使用）
            else if (StringUtil.isNotEmpty(this.getParam("isMulti"))) {
                try {
                    list = JsonUtils.json2List(this.getParam("entityList"),
                        BussTrdTransferOrderBase.class);
                    model.addAttribute("entityList", list);
                }
                catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
            }
            // 不改变原有逻辑的基础上，新增处理逻辑，展示多条指令用  add by renping 2019-07-10 end
            if (entity != null) {
                // SelectOptionDto optionDto = new SelectOptionDto();
                // if (StringUtil.isNotEmpty(entity.getPayAcctNo())) {
                // optionDto.setId(entity.getPayAcctNo());
                // optionDto.setText(entity.getPayAcctName());
                // entity.setPayAcctNoList(Arrays.asList(optionDto));
                // }
                // if (StringUtil.isNotEmpty(entity.getRecAcctNo())) {
                // optionDto = new SelectOptionDto();
                // optionDto.setId(entity.getRecAcctNo());
                // optionDto.setText(entity.getRecAcctName());
                // entity.setRecAcctNoList(Arrays.asList(optionDto));
                // }

                entity.setPayAcctNoList(acctNoList);
                entity.setRecAcctNoList(acctNoList);
                StlCapacctInfo stlCapacctInfo = bussTrdTransferOrderService.queryStlCapacctInfoByBusiAcctNo(entity.getCcy(), entity.getRecAcctNo());
                if (stlCapacctInfo != null){
                    entity.setRecAcctPoint(stlCapacctInfo.getLargePayno());
                    if(StringUtil.isNotEmpty(stlCapacctInfo.getInOutBank())){
                    	entity.setInOutBank(SysDataUtil.getNameFromDataDict(DictCode.资产来源, stlCapacctInfo.getInOutBank()));
                    }
                }
            }

            model.addAttribute("entity", entity);
        }
        // add by jiangpeng on 20210422 TISHKBS-1803
        String isTodo = this.getParam("isTodo");
        if(StringUtil.isNotEmpty(isTodo)) {
            model.addAttribute("isTodo", isTodo);
        }
        // add by jiangpeng on 20210422 TISHKBS-1803

        model.addAttribute("entityName", StringUtil.defaultString(this.getParam("entityName"),
            BussTrdTransferOrderService.DEFAULT_ENTITY_NAME));

     // 一体化对应etliu start
//        setSelectOptionToModel(model, DictCode.是否, DictCode.币种, DictCode.划款类型, DictCode.资产来源);
        setSelectOptionToModel(model, DictCode.是否, DictCode.币种, DictCode.平安划款类型, DictCode.资产来源);
     // 一体化对应etliu end
        model.addAttribute("NO",DictCodeListExt1.是否_否.getValue());
        model.addAttribute("YES",DictCodeListExt1.是否_是.getValue());
        model.addAttribute("orderPageId",this.getParam("orderPageId"));
        return "component/trade/BussTrdTransferOrder";
    }

    private void notEmptyCheck(Object obj, String fieldName, List<ValidMessage> messages) {
        if (!StringUtil.isNotEmpty(obj)) {
            // 一体化对应etliu start
            String tempFieldId = fieldName;
            fieldName = getMessage(fieldName);
//            messages.add(new ValidMessage(fieldName, getMessage("FW_E_0006", fieldName)));
            messages.add(new ValidMessage(tempFieldId, getMessage("FW_E_0006", fieldName)));
            // 一体化对应etliu end
        }
    }


    private void notEmptyCheckTrade(Object obj, String fieldName, List<ValidMessage> messages) {
        if (!StringUtil.isNotEmpty(obj)) {
            // 一体化对应etliu start
            String tempFieldId = fieldName;
            fieldName = getMessage(fieldName);
//            messages.add(new ValidMessage(fieldName, getMessage("FW_E_0006", fieldName)));
            messages.add(new ValidMessage(tempFieldId, getMessage(fieldName)));
            // 一体化对应etliu end
        }
    }


    @RequestMapping("check")
    @ResponseBody
    public AjaxResult check(@RequestBody BussSettleOrderParameterDto param) {
        AjaxResult result = new AjaxResult();
        List<ValidMessage> messages = new ArrayList<>();
        notEmptyCheck(param.getPortfolioId(), "BussTrdTransferOrder.portfolioId", messages);
        //质押式回购交易页面生成指令提示错误
        if (StringUtil.equals(param.getRepPageId(),"TrdRepoTrade002")){
            notEmptyCheck(param.getDeal().getFinprodType2(), "BussTrdTransferOrder.ps",
                    messages);
        }else{
            notEmptyCheck(param.getDeal().getFinprodType2(), "BussTrdTransferOrder.finprodType2",
                    messages);
        }
        //存款类交易新建交易对手非空校验 yjm start 20190724
        if (StringUtil.equals(param.getRepPageId(), "TrdDepositInvestTrade002")){
            notEmptyCheck(param.getDeal().getCounterId(), "BussTrdTransferOrder.counterId",
                    messages);
        }
        //存款类交易新建交易对手非空校验 yjm end 20190724
        notEmptyCheck(param.getDeal().getPs(), "BussTrdTransferOrder.ps", messages);
        if(StringUtils.equals(param.getRepPageId(),"PtlFundAllocation002")) {
            notEmptyCheck(param.getDeal().getTradeDate(), "BussTrdTransferOrder.settleDate", messages);
        }else {
            notEmptyCheck(param.getDeal().getTradeDate(), "BussTrdTransferOrder.tradeDate", messages);
        }
        if(StringUtils.equals(param.getIsDzh(), DictCodeList.是否_是.getValue())){
            if(StringUtils.equals(param.getRepPageId(),"PtlFundAllocation002")) {
                notEmptyCheck(param.getDeal().getTradeAmt(), "BussTrdTransferOrder.transferAmt", messages);
            }
            else {
                notEmptyCheckTrade(param.getDeal().getTradeAmt(), "BussTrdTransferOrder.DzhTradeAmt", messages);
            }
        }else{
            if(StringUtils.equals(param.getRepPageId(),"PtlFundAllocation002")) {
                notEmptyCheck(param.getDeal().getTradeAmt(), "BussTrdTransferOrder.transferAmt", messages);
            }else if(StringUtils.equals(param.getRepPageId(),"FinNetProductDuration") && param.getDeal().getTradeAmt() ==null){
                messages.add(new ValidMessage("此交易没有金额，不需生成指令！可直接提交交易"));
            }
            else {
                notEmptyCheck(param.getDeal().getTradeAmt(), "BussTrdTransferOrder.tradeAmt", messages);
            }
        }
        notEmptyCheck(param.getDeal().getCcy(), "BussTrdTransferOrder.ccy", messages);
        if (messages.size() > 0) {
            result.setErrorList(messages);
        }
        result.setData(param);
        return result;
    }
    
    /**
	 * 指令新建生成划款信息 add by yzg 20191101
	 * 
	 * @param ret
	 * @return
	 */
	@ResponseBody
	@RequestMapping("generatorOrderNewBuild")
	public AjaxResult generatorOrderNewBuild(@RequestBody BussTrdTransferOrderDto ret) {
		AjaxResult ar = new AjaxResult();
		List<ValidMessage> messages = new ArrayList<>();
		// 指令新建错误提示
		// 币种
		if (!StringUtil.isNotEmpty(ret.getCcy())) {
			messages.add(new ValidMessage("ccy", getMessage("FW_E_0006", getMessage("BussTrdTransferOrder.ccy"))));
		}
		// 划款类型
		if (!StringUtil.isNotEmpty(ret.getPayType())) {
			messages.add(
					new ValidMessage("payType", getMessage("FW_E_0006", getMessage("BussTrdTransferOrder.payType"))));
		}
		// 支付日期
		if (!StringUtil.isNotEmpty(ret.getSettleDate())) {
			messages.add(new ValidMessage("settleDate",
					getMessage("FW_E_0006", getMessage("BussTrdTransferOrder.settleDate"))));
		}
		// 金额
		if (!StringUtil.isNotEmpty(ret.getSettleAmt())) {
			messages.add(new ValidMessage("settleAmt",
					getMessage("FW_E_0006", getMessage("BussTrdTransferOrder.tradeAmt"))));
		}
		// 付款账号
		if (!StringUtil.isNotEmpty(ret.getPayAcctNo())) {
			messages.add(new ValidMessage("myAcctNo",
					getMessage("FW_E_0006", getMessage("BussTrdTransferOrder.payAcctNo"))));
		}
		// 收款账号
		if (!StringUtil.isNotEmpty(ret.getRecAcctNo())) {
			messages.add(new ValidMessage("counterAcctNo",
					getMessage("FW_E_0006", getMessage("BussTrdTransferOrder.recAcctNo"))));
		}
		// 资金用途
		if (!StringUtil.isNotEmpty(ret.getPurpose())) {
			messages.add(
					new ValidMessage("purpose", getMessage("FW_E_0006", getMessage("BussTrdTransferOrder.purpose"))));
		}
		// 是否发送指令
		if (!StringUtil.isNotEmpty(ret.getIsNeedSend())) {
			messages.add(new ValidMessage("isNeedSend",
					getMessage("FW_E_0006", getMessage("BussTrdTransferOrder.isNeedSend"))));
		}
		// 生成orderId
		if (!StringUtil.isNotEmpty(ret.getOrderId())) {
			String orderId = "";
			if (baseGetBusinessId != null) {
				BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
				bussinessCodeParameter.setSeqNo("9998");
				orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
				ret.setOrderId(orderId);
			} else {
				orderId = IDGenerator.generate();
			}
			ret.setOrderId(orderId);
		}
		if (messages.size() > 0) {
			ar.setErrorList(messages);
		}
		ar.setData(ret);
		return ar;
	}
    
    /**
     * 债券质押回购多组合新增
     * 张庚林
     * 20190723
     */
    List<BussTrdTransferOrderDto> dealList2=null;
    @RequestMapping("generateOrder")
    @ResponseBody
    public AjaxResult generateOrder(@RequestBody BussSettleOrderParameterDto param) {
        BussTrdTransferOrderDto ret = bussTrdTransferOrderService.generateOrder(param);
        if (ret == null) {
            return new AjaxResult(getMessage("BussTrdTransferOrder_E_0001"), MessageType.错误消息);
        }
        if (DictCodeListExt1.是或否_是.getValue().equals(CacheUtil.getSysParam("GEN_PAY_ORDERS_AUTO"))) {
            ret.setIsNeedSend(DictCodeList.是否_否.getValue());
        }
        AjaxResult result = new AjaxResult();
        result.setData(ret);
        
        //债券质押回购多组合新增，张庚林，20190723 start 
        if(param.getDealList()!=null){
            if(dealList2!=null){
                for(int i=0;i<dealList2.size();i++){
                    if(!param.getDeal().getTradeId().equals(dealList2.get(i).getpTradeId())){
                        dealList2.remove(i);
                        i--;
                    }
                }
            }else{
                dealList2=new ArrayList<>();
            }
            
            ret.setpTradeId(param.getDeal().getTradeId());
            dealList2.add(ret);
            result.setData(dealList2);
        }
      //债券质押回购多组合新增，张庚林，20190723 end 
        
        return result;

        // BussSettleOrderResultDto dto = bussSettleOrderCommService.generateSettleOrder(param);
        // BussTrdTransferOrderDto ret = new BussTrdTransferOrderDto();
        // if (dto == null) {
        // return new AjaxResult(getMessage("BussTrdTransferOrder_E_0001"), MessageType.错误消息);
        // }
        // BeanUtil.copyPropertiesIgnoreNull(ret, dto);
        // genOrderSelect(dto, ret);
        // ret.setPortfolioId(param.getPortfolioId());
        //
        // // 根据组合代码获取组合名称
        // PtlPortfolio ptlPortfolio = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
        // PtlPortfolioMapper.class, param.getPortfolioId(), PtlPortfolio.class);
        // if (ptlPortfolio != null) {
        // ret.setPortfolioName(ptlPortfolio.getPortfolioName());
        // }
        // else {
        // ret.setPortfolioName(param.getPortfolioId());
        // }
        //
        // AjaxResult result = new AjaxResult();
        // result.setData(ret);
        // return result;
    }

    @RequestMapping("select")
    @ResponseBody
    public List<SelectOptionDto> select() throws IOException {
        List<SelectOptionDto> tempList = new ArrayList<>();
        BussTrdTransferOrderDto bussTrdTransferOrderDto = new BussTrdTransferOrderDto();
        // DVP资金调拨管理,直接使用画面上选择的收付款账户
        String selectValue = getParam("selectValue");
        String selectRowValue = getParam("selectRowValue");
        if(StringUtils.isNotBlank(selectValue)){
             tempList = JsonUtils.json2List(selectValue, SelectOptionDto.class);
        }
        if(StringUtils.isNotBlank(selectRowValue)){
            bussTrdTransferOrderDto = JsonUtils.json2Object(selectRowValue, BussTrdTransferOrderDto.class);
        }
        if ("Y".equals(getParam("isLimitAcct"))) {
            String portfolioId = "";
            if (null != bussTrdTransferOrderDto && "recAcctNo".equals(getParam("type"))) {
                portfolioId = bussTrdTransferOrderDto.getPortfolioId();
            }
            if (null != bussTrdTransferOrderDto && "payAcctNo".equals(getParam("type"))) {
                portfolioId = bussTrdTransferOrderDto.getInPortfolioId();
            }
            return etrTrdCommonMapper.getPtlAcctInfoByPortId(portfolioId, Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        }

        if (!StringUtil.isEmptyList(tempList)) {
            if (StringUtil.equals("isUsePageAcctTrue", tempList.get(0).getObject())) {
                return tempList;
            }
        }
        if (null != bussTrdTransferOrderDto) {
            if (("recAcctNo".equals(getParam("type")) && "Y".equals(bussTrdTransferOrderDto.getReAcctNoGdhFlag()))
                || "payAcctNo".equals(getParam("type")) && "Y".equals(bussTrdTransferOrderDto.getAcctNoGdhFlag())) {
                return trdCommonBaseMapper.queryReAcctNoByPortfolioId(bussTrdTransferOrderDto.getPortfolioId());
            }
        }

        
        return bussTrdTransferOrderService.queryAcctList(RequestUtil.getPageParam());
        // String initValue = getParam("initValue");
        // String selectValue = getParam("selectValue");
        // return JsonUtils.json2List(selectValue, SelectOptionDto.class).stream().filter(
        // item -> StringUtil.isEmpty(initValue) || StringUtil.equals(item.getId(),
        // initValue)).collect(Collectors.toList());
    }

    /**
     * 根据主键查询清算账户表
     * 
     * @param ccy 币种
     * @param acctNo 网点账号
     * @return 清算账户表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("queryStlCapacctInfoByAcctNo")
    public StlCapacctInfo queryStlCapacctInfoByAcctNo(String ccy, String acctNo) {
        return bussTrdTransferOrderService.queryStlCapacctInfoByBusiAcctNo(ccy, acctNo);
    }
}
