package com.joyintech.tams.settle.batch.autoreconciliation.service;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.reconciliation.dto.DayEndAccountDaichuliDto;
import com.joyintech.tams.invest.common.reconciliation.service.ReconciliationCommonService;
import com.joyintech.tams.settle.batch.autoreconciliation.dao.AutoReconciliationMapper;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动对账批处理
 *
 * @author fuyongsheng
 * @version 1.0 2021年10月26日
 * @see AutoReconciliationService
 * @since 1.0
 */
@Service
public class AutoReconciliationService {

    @Autowired
    private ReconciliationCommonService reconciliationCommonService;

    @Autowired
    private AutoReconciliationMapper autoReconciliationMapper;
    /**
     * 平台日获取接口
     */
    @Autowired(required=false)
    private IPlatformDate iPlatformDate;

    @Transactional(rollbackFor=Exception.class)
    public void reconciliation(Date batchDate){
        Map<String, Object> param = new HashMap<>();
        String sysDate = iPlatformDate.getSysDateStr();
        param.put("sysdate", sysDate);
        param.put("batchDate", DateUtil.format(batchDate));
        param.put("PARA_CODE_02", DictCodeListStl.参数代码_对账维度.getValue());
        param.put("PARA_VALUE_01", DictCodeListStl.对账维度_证券汇总.getValue());
        param.put("PARA_VALUE_02", DictCodeListStl.对账维度_委托汇总.getValue());
        param.put("PARA_VALUE_03", DictCodeListStl.对账维度_成交明细.getValue());
        param.put("CHECK_RESULT_0", DictCodeListExt3.对账状态结果_不一致.getValue());
        param.put("CHECK_RESULT_2", DictCodeListExt3.对账状态结果_待处理.getValue());
        // 删除对账结果不一致和待处理数据
        reconciliationCommonService.deleteCheckDisaccords(batchDate);
        param.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        param.put("PS_T07", DictCodeList.交易方向_到期.getValue());
        param.put("PS_T54", DictCodeListExt3.交易方向_投资端_提前终止.getValue());
        param.put("PS_T55", DictCodeListExt3.交易方向_投资端_逾期返售.getValue());
        param.put("PS_T56", DictCodeListExt3.交易方向_到期续做.getValue());
        param.put("PS_T95", DictCodeListExt3.交易方向_到期购回.getValue());
        param.put("PS_T96", DictCodeListExt3.交易方向_提前购回.getValue());
        param.put("PS_T49", DictCodeListExt3.交易方向_债转股.getValue());
        // add by fuyongsheng 20211208 没有对账单的自动批处理(step2)功能开发 start
        param.put("PS_T68", DictCodeListExt3.交易方向_质押券入库.getValue());
        param.put("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
        param.put("PS_T94", DictCodeListExt3.交易方向_中途换券.getValue());
        // GZYHTZJY-1015:解除质押不需要对账 modify by fuyongsheng 20211231 start
        param.put("PS_T93", DictCodeListExt3.交易方向_解除质押.getValue());
        // GZYHTZJY-1015:解除质押不需要对账 modify by fuyongsheng 20211231 end
        param.put("PS_T04", DictCodeList.交易方向_申购.getValue());
        param.put("PS_T05", DictCodeList.交易方向_申购确认.getValue());
        param.put("PS_T11", DictCodeList.交易方向_现金分红.getValue());
        param.put("PS_T12", DictCodeList.交易方向_红利再投.getValue());
        param.put("PS_T39", DictCodeList.交易方向_行权_赎回.getValue());
        param.put("PS_T08", DictCodeList.交易方向_还本.getValue());
        param.put("PS_T09", DictCodeList.交易方向_收息.getValue());
        //param.put("PS_T49", DictCodeListExt1.交易方向_债转股.getValue());
        param.put("PS_T46", DictCodeListExt2.交易方向_配股.getValue());
        param.put("PS_T97", DictCodeListExt3.交易方向_配债.getValue());
        // add by fuyongsheng 20211208 没有对账单的自动批处理(step2)功能开发 end
        param.put("prodType2_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("prodType2_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        param.put("prodType2_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        param.put("prodType2_F07", DictCodeList.平安资产类型_质押式回购.getValue());
        param.put("prodType2_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        param.put("prodType2_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        param.put("prodType2_F32", BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        param.put("prodType2_F37", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        param.put("TARGET_BACK_TYPE_H01", DictCodeList.回购类型_质押式协议回购.getValue());
        param.put("BUSI_SCENE_CALSS_0902RS03", DictCodeListStl.业务场景分类明细_债券协议正回购_到期.getValue());
        param.put("BUSI_SCENE_CALSS_0902RB01", DictCodeListStl.业务场景分类明细_债券协议逆回购_首期_买入.getValue());
        param.put("BUSI_SCENE_CALSS_0902RB04", DictCodeListStl.业务场景分类明细_债券协议逆回购_到期续作.getValue());
        param.put("INTER_TYPE_1", DictCodeListExt3.柜台接口类型_恒生.getValue());
        param.put("INTER_TYPE_2", DictCodeListExt3.柜台接口类型_金证w版.getValue());
        param.put("INTER_TYPE_4", DictCodeListExt3.柜台接口类型_金证u版.getValue());
        param.put("TRADE_MARKET_03", BussinessDictCodeList.交易场所_上海证券交易所.getValue());
        param.put("TRADE_MARKET_04", BussinessDictCodeList.交易场所_深圳证券交易所.getValue());
        param.put("DELIVERY_RESULT_01", DictCodeListExt4.交收结果_正常交收.getValue());
        param.put("DELIVERY_RESULT_02", DictCodeListExt4.交收结果_部分交收.getValue());
        // 获取待自动对账的对账数据
        List<DayEndAccountDaichuliDto> autoAccountList = autoReconciliationMapper.queryAccountTodoList(param);
        reconciliationCommonService.executeReconciliation(autoAccountList);
        // 处理没有券商对账单,只有结算明细的待处理对账数据
        List<DayEndAccountDaichuliDto> manualAccountList = autoReconciliationMapper.queryManualAccountTodoList(param);
        reconciliationCommonService.executeManualReconciliation(manualAccountList);
    }
}
