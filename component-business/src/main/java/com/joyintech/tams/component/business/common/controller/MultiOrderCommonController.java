package com.joyintech.tams.component.business.common.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.dao.StlCapacctInfoMapper;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.common.base.entity.StlCapacctInfoKey;
import com.joyintech.tams.component.business.settle.BussSettleOrderCommService;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderResultDto;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.joyintech.tams.component.business.settle.BussSettleOrderCommService.genOrderSelect;

/**
 * @program: tams-invest-parent
 * @description: 存续期共通
 * @author: 强成西
 * @create: 2018-11-13 09:48
 **/
@Controller
@RequestMapping("/common/multiOrder")
public class MultiOrderCommonController extends BaseController {

    /**
     * 清算指令共通Service
     */
    @Autowired(required = false)
    private BussSettleOrderCommService bussSettleOrderCommService;
    
    /**
     * 清算指令共通Service
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;
    @Autowired
    private StlCapacctInfoMapper stlCapacctInfoMapper;

    @RequestMapping("genOrderCheck")
    @ResponseBody
    public AjaxResult check(@RequestBody ArrayList<BussSettleOrderParameterDto> bussSettleOrderParameterDtos) {
        AjaxResult result = new AjaxResult();
        List<ValidMessage> messages = new ArrayList<>();
        bussSettleOrderParameterDtos.forEach(bussSettleOrderParameterDto -> {
            notEmptyCheck(bussSettleOrderParameterDto.getPortfolioId(), "BussTrdTransferOrder.portfolioId", messages);
            notEmptyCheck(bussSettleOrderParameterDto.getDeal().getFinprodType2(), "BussTrdTransferOrder.finprodType2", messages);
            notEmptyCheck(bussSettleOrderParameterDto.getDeal().getPs(), "BussTrdTransferOrder.ps", messages);
            notEmptyCheck(bussSettleOrderParameterDto.getDeal().getTradeDate(), "BussTrdTransferOrder.tradeDate", messages);
            notEmptyCheck(bussSettleOrderParameterDto.getDeal().getTradeAmt(), "BussTrdTransferOrder.tradeAmt", messages);
            notEmptyCheck(bussSettleOrderParameterDto.getDeal().getCcy(), "BussTrdTransferOrder.ccy", messages);
        });
        if (messages.size() > 0) {
            result.setErrorList(messages);
        }
        return result;
    }

    /**
     * 共通Mapper的通用代理方案
     */


    @RequestMapping("generateOrder")
    @ResponseBody
    public AjaxResult generateOrder(@RequestBody ArrayList<BussSettleOrderParameterDto> bussSettleOrderParameterDtos) {

        if (bussSettleOrderParameterDtos == null) {
            return new AjaxResult(getMessage("BussTrdTransferOrder_E_0001"), MessageType.错误消息);
        }
        List<BussTrdTransferOrderDto> resultData = new ArrayList<>();
        bussSettleOrderParameterDtos.forEach(param -> {
            BussSettleOrderResultDto dto = bussSettleOrderCommService.generateSettleOrder(param);
            BussTrdTransferOrderDto ret = new BussTrdTransferOrderDto();
            resultData.add(ret);
            BeanUtil.copyPropertiesIgnoreNull(ret, dto);
            genOrderSelect(dto, ret);
            ret.setPortfolioId(param.getPortfolioId());
            ret.setInvAim(param.getInvAim());
            ret.setSecAcctId(StringUtil.isNotEmpty(param.getDeal().getChlId()) ? param.getDeal().getChlId() : param.getDeal().getSecManageAcctId());
            if (DictCodeListExt1.是或否_是.getValue().equals(CacheUtil.getSysParam("GEN_PAY_ORDERS_AUTO"))) {
                ret.setIsNeedSend(DictCodeList.是否_否.getValue());
            }
            // 设置付款账户下拉框列表
//            List<SelectOptionDto> acctList = bussTrdTransferOrderService.queryAcctList(null);
//            if (!StringUtil.isEmptyList(acctList)) {
//                ret.setPayAcctNoList(acctList);
//                ret.setRecAcctNoList(acctList);
//            }

            // 根据组合代码获取组合名称
            PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(param.getPortfolioId());
            if (ptlPortfolio != null) {
                ret.setPortfolioName(ptlPortfolio.getPortfolioName());
            }
            else {
                ret.setPortfolioName(param.getPortfolioId());
            }
            
            // 根据收款账户查询划款性质
            if (StringUtil.isNotEmpty(ret.getRecAcctNo()) && StringUtil.isNotEmpty(ret.getCcy())){
                StlCapacctInfoKey stlCapacctInfoKey = new StlCapacctInfoKey();
                stlCapacctInfoKey.setAcctNo(ret.getRecAcctNo());
                stlCapacctInfoKey.setCcy(ret.getCcy());
                StlCapacctInfo stlCapacctInfo = stlCapacctInfoMapper.selectByPrimaryKey(stlCapacctInfoKey);
                if (stlCapacctInfo != null){
                    if (StringUtil.isNotEmpty(stlCapacctInfo.getInOutBank())){
                        ret.setInOutBank(SysDataUtil.getNameFromDataDict(DictCode.资产来源, stlCapacctInfo.getInOutBank()));
                    }
                    ret.setRecAcctPoint(stlCapacctInfo.getLargePayno());
                }
            }
        });

        AjaxResult result = new AjaxResult();
        result.setData(resultData);
        return result;
    }

    @RequestMapping("select")
    @ResponseBody
    public List<SelectOptionDto> select() throws IOException {
        String selectValue = getParam("selectValue");
        List<SelectOptionDto> tempList = new ArrayList<>();
        if(selectValue.indexOf("isUsePageAcctTrue")!=-1){
            tempList =JsonUtils.json2List(selectValue, SelectOptionDto.class);
        }    
        if (!StringUtil.isEmptyList(tempList)) {
            if (StringUtil.equals("isUsePageAcctTrue", tempList.get(0).getObject())) {
                return tempList;
            }
        }

        return bussTrdTransferOrderService.queryAcctList(RequestUtil.getPageParam());
    }

    private void notEmptyCheck(Object obj, String fieldName, List<ValidMessage> messages) {
        if (!StringUtil.isNotEmpty(obj)) {
            messages.add(new ValidMessage(fieldName, getMessage("FW_E_0006", getMessage(fieldName))));
        }
    }
}
