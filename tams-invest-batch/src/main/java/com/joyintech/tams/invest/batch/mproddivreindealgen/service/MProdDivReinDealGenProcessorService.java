/*
 * 文件名：ResetBaseRuleInfoProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.batch.mproddivreindealgen.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.joyintech.framework.util.EncryptUtil;
import com.joyintech.tams.constant.DictCodeListExt1;

import com.joyintech.tams.invest.common.util.MD5Util;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


import com.joyintech.framework.base.BaseService;

import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;

import com.joyintech.tams.common.base.entity.PaTrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.TrdAsynHandleStatus;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.mproddivreindealgen.dto.MProdDivReinDealGenDto;
import com.joyintech.tams.invest.batch.mproddivreindealgen.dto.MProdDivReinDealGenReaderResult;
import com.joyintech.tams.invest.batch.mproddivreindealgen.dto.MProdDivReinDealGenResult;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;

/**
 * processor相关业务处理
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月16日
 * @see MProdDivReinDealGenProcessorService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
@Scope("prototype")
public class MProdDivReinDealGenProcessorService extends BaseService{

	@Autowired(required = false)
	private BaseGetBussinessId baseGetBussinessId;
    
    /**
     * process 数据处理
     * <功能详细描述>
     * @param param1
     * @return
     * @see
     * @since 1.0
     */
    public MProdDivReinDealGenResult execute(Map<String, Object> param1) {
        // 获取reader返回值
    	MProdDivReinDealGenReaderResult readerResult = (MProdDivReinDealGenReaderResult)param1.get(
            BatchConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (readerResult == null) {
            return null;
        }
      // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param1.get(BatchConstant.BATCH_INFO);
        
        if(batchExecuteInfo == null){
            return null;
        }
        // 待处
        List<MProdDivReinDealGenDto>  dealDtos = readerResult.getDealDtos();
        if(CollectionUtils.isEmpty(dealDtos)){
        	return null;
        }
        MProdDivReinDealGenResult resultDto = new MProdDivReinDealGenResult();
        resultDto.setTrdProductDeals(new ArrayList<TrdProductDeal>());
        resultDto.setTrdProductDealAdds(new ArrayList<TrdProductDealAdd>());
        resultDto.setBokDealAccountings(new ArrayList<BokDealAccounting>());
        resultDto.setBokDealAccountingDetails(new ArrayList<BokDealAccountingDetail>());
        BussinessCodeParameter parameter = new BussinessCodeParameter();
   	    parameter.setCommonBusinessType(DictCodeList.共通业务类型_收益型产品中间付息管理);
   	    String totleTradeId =  baseGetBussinessId.buildBussinessCode(parameter);
    
   	    List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<PtlSecPositionFlowDto>();
        for(MProdDivReinDealGenDto dealDto : dealDtos){
       	    String tradeId =  baseGetBussinessId.buildBussinessCode(parameter);
       	    TrdProductDeal deal = new TrdProductDeal();
       	    deal.setTradeId(tradeId);
       	    deal.setBusiType(DictCodeList.业务类型_货币型TA交易.getValue());
       	    deal.setFinprodId(dealDto.getFinprodId());
       	    deal.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
       	    deal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
       	    deal.setBranch(0l);
        	deal.setTradeAmt(dealDto.getTdyUnpaidIntincexp());
        	deal.setCpriceAmt(dealDto.getTdyUnpaidIntincexp());
       	    deal.setShareAmt(dealDto.getTdyUnpaidIntincexp());
       	    deal.setFpriceAmt(dealDto.getTdyUnpaidIntincexp());
       	    deal.setVdate(DateUtil.addDay(batchExecuteInfo.getExecuteDate(), -1));
       	    deal.setMdate(batchExecuteInfo.getExecuteDate());
       	    deal.setTradeDate(batchExecuteInfo.getExecuteDate());
       	    deal.setSettleDate(batchExecuteInfo.getExecuteDate());
       	    deal.setPs(DictCodeList.交易方向_产品红利转投.getValue());
       	    deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
       	    deal.setTotleTradeId(totleTradeId);
            deal.setCcy(readerResult.getCcy());
            deal.setIsCancel(DictCodeList.是否_否.getValue());
       	    resultDto.getTrdProductDeals().add(deal);
       	    
       	    TrdProductDealAdd dealAdd = new TrdProductDealAdd();
       	    dealAdd.setTradeId(tradeId);
       	    dealAdd.setPortfolioId(readerResult.getPortfolioId());
       	    dealAdd.setGenType(DictCodeListExt1.生成方式_批处理生成.getValue());
       	    resultDto.getTrdProductDealAdds().add(dealAdd);
       	    BokDealAccounting bokDealAccounting = new BokDealAccounting();
       	    bokDealAccounting.setBooksetId(readerResult.getBooksetId());
       	    bokDealAccounting.setBusiNo(tradeId);
       	    bokDealAccounting.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
       	    bokDealAccounting.setBookDate(batchExecuteInfo.getExecuteDate());
        	bokDealAccounting.setBooksetDate(batchExecuteInfo.getExecuteDate());
       	    bokDealAccounting.setHappenDate(batchExecuteInfo.getExecuteDate());
       	    bokDealAccounting.setIsAccounted(DictCodeList.是否_是.getValue());
       	    resultDto.getBokDealAccountings().add(bokDealAccounting);
       	    String md5BokDetailId = "1" + dealDto.getFinprodId()+"2"+DictCodeListExt1.金融产品类型_理财产品.getValue();
       	    String bokDetailId = EncryptUtil.getInstance().md5(md5BokDetailId);
       	    BokDealAccountingDetail bokDealAccountingDetail = new BokDealAccountingDetail();
       	    bokDealAccountingDetail.setBooksetId(readerResult.getBooksetId());
       	    bokDealAccountingDetail.setBusiNo(tradeId);
          	bokDealAccountingDetail.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
       	    bokDealAccountingDetail.setBusiType(DictCodeList.业务类型_货币型TA交易.getValue());
       	    bokDealAccountingDetail.setBokDetailId(bokDetailId);
       	    resultDto.getBokDealAccountingDetails().add(bokDealAccountingDetail);
       	    //组合持仓
       	    PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
       	    BussinessCodeParameter parameter2 = new BussinessCodeParameter();
       	    parameter2.setSeqNo("9999");
       	    String seqNo = baseGetBussinessId.buildBussinessCode(parameter2);
       	    ptlSecPositionFlowDto.setSeqno(seqNo);
    	    ptlSecPositionFlowDto.setTradeId(tradeId);
    	    ptlSecPositionFlowDto.setPortfolioId(readerResult.getPortfolioId());
    	    ptlSecPositionFlowDto.setFinprodId(dealDto.getFinprodId());
    	    ptlSecPositionFlowDto.setBranch(0l);
    	    ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
    	    ptlSecPositionFlowDto.setCcy(readerResult.getCcy());
    	    ptlSecPositionFlowDto.setSecAcctId("999999");
    	    ptlSecPositionFlowDto.setInvAim("999999");
    	    ptlSecPositionFlowDto.setAmount(dealDto.getTdyUnpaidIntincexp());
    	    ptlSecPositionFlowDto.setCdate(batchExecuteInfo.getExecuteDate());
    	    ptlSecPositionFlows.add(ptlSecPositionFlowDto);
        }
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(totleTradeId);
        trdFinProductDealTotal.setBusiType(DictCodeList.业务类型_货币型TA交易.getValue());
        trdFinProductDealTotal.setFinprodId(readerResult.getFinprodId());
        trdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        trdFinProductDealTotal.setCcy(readerResult.getCcy());
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_产品红利转投.getValue());
        trdFinProductDealTotal.setVdate(DateUtil.addDay(batchExecuteInfo.getExecuteDate(), -1));
        trdFinProductDealTotal.setMdate(batchExecuteInfo.getExecuteDate());
        trdFinProductDealTotal.setTradeDate(batchExecuteInfo.getExecuteDate());
        trdFinProductDealTotal.setSettleDate(batchExecuteInfo.getExecuteDate());
        trdFinProductDealTotal.setTradeAmt(readerResult.getTdyUnpaidIntincexp());
        trdFinProductDealTotal.setCpriceAmt(readerResult.getTdyUnpaidIntincexp());
        trdFinProductDealTotal.setShareAmt(readerResult.getTdyUnpaidIntincexp());
        trdFinProductDealTotal.setFpriceAmt(readerResult.getTdyUnpaidIntincexp());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setPortfolioId(readerResult.getPortfolioId());
        resultDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        paTrdFinProductDealTotal.setTotleTradeId(totleTradeId);
        resultDto.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(readerResult.getPortfolioId());
        ptlTrade.setBusiNo(totleTradeId);
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(readerResult.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.业务类型_货币型TA交易.getValue());
        ptlTrade.setSettleDate(batchExecuteInfo.getExecuteDate());
        resultDto.setPtlTrade(ptlTrade);
        //add by wm 交易异步状态表 2020-01-06 start
        TrdAsynHandleStatus trdAsynHandleStatus = new TrdAsynHandleStatus();
        trdAsynHandleStatus.setTradeId(trdFinProductDealTotal.getTotleTradeId());		//交易汇总id
        trdAsynHandleStatus.setTrdTableType("01");		// 交易表类型(01：金融产品交易汇总表，02：金融产品交易表)
        trdAsynHandleStatus.setHandleStatus("01");		//01	待处理
        resultDto.setTrdAsynHandleStatus(trdAsynHandleStatus);
        //add by wm 交易异步状态表 2020-01-06 start
        //组合持仓流水增加
        resultDto.setPtlSecPositionFlows(ptlSecPositionFlows);
        return resultDto;
    }
    
}