package com.joyintech.tams.valuation.trademanage.irs.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSeclendDeal;
import com.joyintech.tams.valuation.trademanage.bondlend.dto.TrdBondLendTrade002Dto;
import com.joyintech.tams.valuation.trademanage.bondlend.dto.TrdBondLendTradeResult;
import com.joyintech.tams.valuation.trademanage.bondlend.service.TrdBondLendTradeBServer;
import com.joyintech.tams.valuation.trademanage.irs.dto.TrdIRSValuationTrade002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TrdIRSValuationTrade002Service extends CommonExportService<TrdIRSValuationTrade002Dto> implements BaseProcessService<TrdIRSValuationTrade002Dto> {

    @Autowired
    private TrdBondLendTradeBServer trdBondLendTradeBServer;


    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public TrdIRSValuationTrade002Dto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade002/queryTradeInfoByTradeId", tradeId,
                TrdIRSValuationTrade002Dto.class);
    }

    /**
     * 资金账户下拉框
     *
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getTrdAcctId(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade002/getTrdAcctId" , map,
                SelectOptionDto.class);
    }

    //自增序列号
    public int querySequences(){
        return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdBondLendTrade002/querySequences", null,Integer.class);
    }


    @Override
    public void doComplete(TrdIRSValuationTrade002Dto trdIRSValuationTrade002Dto, OperationType operationType) {
//        TrdBondLendTradeResult trdBondLendTradeResult = generateEntity(TrdBondLendTrade002Dto);
//        String action = StringUtil.EMPTY;
//         if (OperationType.NEW.equals(operationType)) {
//             action = "insert";
//        }
//        else {
//            action = "update";
//        }
//        restTemplate.postForObject(
//                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade002/" + action, trdBondLendTradeResult,
//                Integer.class);

    }

    //封装实体类
    public TrdBondLendTradeResult generateEntity(TrdBondLendTrade002Dto dto){
        //将页面输入值赋值给接口实体类
        List<ValSeclendDeal> listValSeclendDeal=new ArrayList<>();
        ValSeclendDeal entity = new ValSeclendDeal();
        entity.setTradeId(dto.getTradeId());
        entity.setProdId(dto.getProdId());
        //金融产品类型
        if("01".equals(dto.getPs())){
            entity.setFinprodType(DictCodeListExt1.金融产品类型_债券借贷融入.getValue());
        }else if("02".equals(dto.getPs())){
            entity.setFinprodType(DictCodeListExt1.金融产品类型_债券借贷融出.getValue());
        }else if("03".equals(dto.getPs())){
            entity.setPs(DictCodeList.交易方向_到期.getValue());
        }
        entity.setTradeDate(DateUtil.parse(dto.getTradeDate()));//交易日
        entity.setVdate(DateUtil.parse(dto.getVdate()));//起息日
        entity.setMdate(DateUtil.parse(dto.getMdate()));//到期日
        entity.setTerm(dto.getTerm());//借贷期限
        entity.setFeeRate(dto.getFeeRate());//借贷费率
        entity.setFee(dto.getFee());//借贷费用
        entity.setCcy(dto.getCcy());//币种
        entity.setSecId("F01_"+dto.getSecId());//标的债券代码
        entity.setPrinAmt(dto.getPrinAmt());//券面总额
        entity.setStlFeeAmt(dto.getStlFeeAmt());//结算服务费
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setFinprodId(dto.getTradeId());//金融产品代码
        entity.setDataActionSign("01");//数据动作标志
        entity.setPs("T01");//交易方向
        entity.setBasis("A/365");//计息基础
        entity.setCounterId("JYDS000000000424");//交易对手
        entity.setTradeMarket("01");//交易场所
        entity.setmPrinAmt(dto.getPrinAmt());//质押券面总额
        entity.setIsClean("Y");//有无清算指令
        entity.setRegistAgency("01");//登记结算机构
        entity.setIsStlFeeAmt("Y");//结算手续费是否随交易支付
        entity.setMyAcctNo("201907300001:jixu0001");
        listValSeclendDeal.add(entity);

        return trdBondLendTradeBServer.generateEntity(dto,listValSeclendDeal);

    }

}
