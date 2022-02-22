/*
 * 文件名：TrdLendInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：杨智 修改时间：2018年10月10日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdlendvaluationtrade.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;

import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.LendDealCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.lend.LendDealRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValDlDeal;
import com.joyintech.tams.valuation.trademanage.trdlendvaluationtrade.dto.TrdLendValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdlendvaluationtrade.dto.TrdLendValuationTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrdLendValuationTrade002Service extends BaseService<TrdLendValuationTradeDto> implements BaseProcessService<TrdLendValuationTradeDto> {


    @Autowired
    private LendDealRecService lendDealRecService;

    /**
     * 合成初始化实体类
     *
     * @param tradeId 交易编号
     * @return 存款类资产实体类
     * @see
     * @since 1.0
     */
    public TrdLendValuationTradeDto getInitEntity(String tradeId) {
        TrdLendValuationTradeDto entity = new TrdLendValuationTradeDto();
        TrdProductDeal trdProductDeal = null;
        TrdProductDealAdd trdProductDealAdd = null;
        String finprodId = null;
        FinProduct finProduct = null;
        return entity;
    }

    /**
     * 根据交易编号查询详情
     */
    public TrdLendValuationTradeDto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdLendValuationTrade/queryTradeInfoByTradeId",
                tradeId, TrdLendValuationTradeDto.class);
    }

    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createPlanTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdLendValuationTrade/createPlanTradeId",
                null, String.class);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdLendValuationTradeDto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdLendValuationTradeResult trdLendValuationTradeResult = new TrdLendValuationTradeResult();
        trdLendValuationTradeResult = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdLendValuationTrade/" + action, trdLendValuationTradeResult, Integer.class);
    }

    //封装实体类
    public TrdLendValuationTradeResult generateEntity(TrdLendValuationTradeDto dto){
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");
        TemplateOrderInfoReaderResult acctEntity= new TemplateOrderInfoReaderResult();
        // 定义返回变量
        LendDealCommonResult result = new LendDealCommonResult();

        //将页面输入值赋值给接口实体类
        List<ValDlDeal> listValDlDeal=new ArrayList<>();
        ValDlDeal entity = new ValDlDeal();
        BeanUtil.copyPropertiesIgnoreNull(entity,dto);

        entity.setTradeId(dto.getTradeId());  //交易编号
        entity.setProdId(dto.getPortfolioId()); //产品代码
        entity.setDataActionSign("01");//数据动作标志

//        entity.setBusiType(dto.getBusiPs()); //业务类型 拆借
        entity.setBusiType("01"); //业务类型 拆借
        entity.setDealType("T01"); //交易类型
//        entity.setAssetId("F01_"+dto.getFinprodMarketId());  //资产代码
        entity.setDealDate(dto.getTradeDate());//交易日
        entity.setSettleDate(dto.getSettDate()); //交割日
        entity.setVdate(dto.getVdate()); //起息日
        entity.setMdate(dto.getMdate()); //到期日
        entity.setRate(dto.getIntRate()); //拆借利率
        entity.setCapital(dto.getPrinAmt()); //本金金额
        entity.setActInt(dto.getIntAmt()); //实收利息 =应计利息
        entity.setDealAmt(dto.getPrinAmt().add(dto.getIntAmt())); //交易金额
        entity.setCcy(dto.getCcy());//币种
        entity.setpTradeId(dto.getPortfolioId()); //原交易编号
        entity.setCounterId("JYDS000000000424");//交易对手
        entity.setIsClean("Y");//有无清算指令
        listValDlDeal.add(entity);
        Date date = new Date();
        acctEntity.setResovleDate(date);
        acctEntity.setFourLevelSubjectKg("Y");
        Map<String, Object> map = lendDealRecService.handleValDlDeal(listValDlDeal,acctEntity,params,null);

        result.setListValFinProductDeal((List<TrdProductDeal>)map.get("ValFinProductDeal"));
        result.setListValFinProductDealExt((List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
        result.setBokDealAccountingList((List<BokDealAccounting>)map.get("ValDealAccounting"));
        result.setBokDealAccountingDetailList((List<BokDealAccountingDetail>)map.get("ValDealAccountingDetail"));
        result.setBokDetailIdList((List<BokDetailId>)map.get("ValBokDetailId"));
        result.setListValOrders((List<StlOrders>)map.get("valOrdersList"));
        result.setListValOrdersExt((List<StlOrdersExt>)map.get("valOrdersExtList"));


        TrdLendValuationTradeResult rst = new TrdLendValuationTradeResult(result);
        rst.setTrdLendValuationTradeDto(dto);
        return rst;
    }


}
