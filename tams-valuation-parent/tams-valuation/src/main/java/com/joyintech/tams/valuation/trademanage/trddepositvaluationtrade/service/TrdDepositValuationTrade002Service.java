/*
 * 文件名：TrdDepositInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：fanshuanghu
 * 修改时间：2018年10月18日 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.service;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;

import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfNeedOperateEntity;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.depositasset.DepositeAssetAstDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValBotAstTrade;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMngMnfTade;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecVestPeriod;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSeclendDeal;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.dto.TrdDepositValuationTrade001Dto;
import com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.dto.TrdDepositValuationTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.constant.ProcessType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 存款类新建Service
 * 
 * @author fanshuanghu
 * @version 1.0 2018年10月18日
 * @see TrdDepositValuationTrade002Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdDepositValuationTrade002Service extends CommonExportService<TrdDepositValuationTrade001Dto> implements BaseProcessService<TrdDepositValuationTrade001Dto> {

    @Autowired
    private DepositeAssetAstDealTemplateRecService depositeAssetAstDealTemplateRecService;

    //交易对手
    public Page<SelectOptionDto> getCounterId(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME
                + "/TrdDepositValuationTrade001/getCounterId" , param,SelectOptionDto.class);
    }

    /**
     * 根据交易编号查询详情
     */
    public TrdDepositValuationTrade001Dto queryTradeInfoByTradeId(Map<String, Object> params) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdDepositValuationTrade001/queryTradeInfoByTradeId",
                params, TrdDepositValuationTrade001Dto.class);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdDepositValuationTrade001Dto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdDepositValuationTradeResult trdDepositValuationTradeResult = new TrdDepositValuationTradeResult();
        trdDepositValuationTradeResult = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdDepositValuationTrade001/" + action, trdDepositValuationTradeResult, Integer.class);
    }

    //封装实体类
    public TrdDepositValuationTradeResult generateEntity(TrdDepositValuationTrade001Dto dto){
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getProdId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        //将页面输入值赋值给接口实体类
        List<ValBotAstTrade> listValBotAstTrade=new ArrayList<>();
        ValBotAstTrade entity = new ValBotAstTrade();
        BeanUtil.copyPropertiesIgnoreNull(entity,dto);
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setfTradeId(dto.getOutTradeId());  //外部交易编号
        entity.setDataActionSign("01");//数据动作标志
        entity.setDealDate(dto.getTradeDate());//交易日
        entity.setTradeCapi(dto.getPrinAmt()); //交易本金
        entity.setDealAmt(dto.getSettleAmt());//交易金额
        entity.setCcy(dto.getCcy());//币种
        entity.setCounterId(dto.getCounterId());//交易对手
        entity.setIsClean("N");//有无清算指令
        entity.setChlFeeAmt(null);
        entity.setIsChlFeeAmt("N");
        entity.setChannelId(null);

        //设置数据来源：画面录入
        entity.setDataSource(DictCodeListExt1.生成方式_画面录入.getValue());

        listValBotAstTrade.add(entity);
        TemplateOrderInfoReaderResult acctEntity= new TemplateOrderInfoReaderResult();
        Date date = new Date();
        acctEntity.setResovleDate(date);
        acctEntity.setFourLevelSubjectKg("Y");
        Map<String, Object> map = depositeAssetAstDealTemplateRecService.handleValBotAstTrade(listValBotAstTrade,acctEntity,params,null);
        // 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get("ValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get("ValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get("ValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get("ValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail");
        result.setListValTradeFeeDetail(listValTradeFeeDetail);
        //账务明细代码表
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get("ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);

        //结算指令
        List<StlOrders> listValOrders = (List<StlOrders>)map.get("listValOrders");
        List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get("listValOrdersExt");
        result.setListValOrders(listValOrders);
        result.setListValOrdersExt(listValOrdersExt);

        TrdDepositValuationTradeResult rst = new TrdDepositValuationTradeResult(result);
        rst.setTrdDepositValuationTrade001Dto(dto);
        return rst;
    }

}
