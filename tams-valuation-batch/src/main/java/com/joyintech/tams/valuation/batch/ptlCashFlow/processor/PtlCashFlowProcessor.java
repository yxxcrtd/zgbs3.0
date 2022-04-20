package com.joyintech.tams.valuation.batch.ptlCashFlow.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.profitdistributionnet.dto.ProfitDistributionNetResult;
import com.joyintech.tams.valuation.batch.ptlCashFlow.service.PtlCashFlowNewService;
import com.joyintech.tams.valuation.batch.ptlCashFlow.service.PtlCashFlowService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义现金流账务处理
 */
@Service
@Scope("prototype")
public class PtlCashFlowProcessor extends BaseProcessor<NeedOperateEntity, BookGenerateResult>{

    /**
     * processor方法的 Service
     */
    @Autowired
    private PtlCashFlowService ptlCashFlowService;
    /**
     * processor方法的 Service
     */
    @Autowired
    private PtlCashFlowNewService ptlCashFlowNewService;

    @Override
    public BookGenerateResult process(NeedOperateEntity items)
            throws Exception {
        // 获取reader方法的返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)items;

        Map<String, Object> thisParams = new HashMap<>();
        thisParams.putAll(this.getParams());
        // 将返回值塞入参数中
        thisParams.put(GlobalConstant.READER_RESULT, acctEntity);

        // modify by weipeng on 20220307 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start
        BookGenerateResult result = new BookGenerateResult();
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
        // 若按照内部科目编码分录，则需要映射分录对应科目信息.
        //调用service方法进行逻辑逻辑
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            result = ptlCashFlowNewService.execute(thisParams);
        }
        else{
            result = ptlCashFlowService.execute(thisParams);
        }
        // modify by weipeng on 20220307 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end

        //当为null时候，给予初始化
        if(result == null){
            result = new BookGenerateResult();
        }

        result.setProdId(
                acctEntity.getBooksetId() + GlobalConstant.SPLIT + acctEntity.getBokDetailId());

        return result;
    }
}
