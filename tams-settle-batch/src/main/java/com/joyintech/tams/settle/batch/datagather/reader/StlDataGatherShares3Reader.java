package com.joyintech.tams.settle.batch.datagather.reader;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleBatchPara;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.settle.batch.datadeal.service.StlDownDataDealService;
import com.joyintech.tams.settle.batch.datagather.constant.StlDataGatherConstant;
import com.joyintech.tams.settle.batch.datagather.dto.InvestTradeReaderDto;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 股票申购确认交易数据处理
 *
 * @author fuyongsheng
 * @date 2022/4/8
 * @see StlDataGatherShares3Reader
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherShares3Reader extends StlDataGatherReader {

    @Autowired
    private StlDownDataDealService stlDownDataDealService;

    @Autowired
    private StlDataGatherService stlDataGatherService;

    @Override
    public List<InvestTradeReaderDto> createList() {
        List<InvestTradeReaderDto> resultList = new ArrayList<InvestTradeReaderDto>();
        InvestTradeReaderDto investTradeReaderDto = new InvestTradeReaderDto();
        // 批处理执行信息
        SysBatchExecInfo execInfo = getBatchExecuteInfo();
        // 数据清算参数表
        List<SettleBatchPara> list = stlDownDataDealService.selectAllParam(execInfo.getExecuteDate());
        // 组合代码List
        Set<String> portfolioIdList = new HashSet<>();

        list.forEach(item -> {
            // 证券品种
            if (StringUtil.isNotEmpty(item.getSecType()) && isSecType(item.getSecType())) {
                // 组合代码
                if (StringUtil.isNotEmpty(item.getPortfolioId())) {
                    portfolioIdList.add(item.getPortfolioId());
                }
            }
        });

        Map<String, Object> searchParam = new HashMap<String, Object>();
        searchParam.put("portfolioIdList", portfolioIdList);
        searchParam.put("dealDate", execInfo.getExecuteDate());
        searchParam.put("PS_T04", DictCodeList.交易方向_申购.getValue());
        searchParam.put("PS_T05", DictCodeList.交易方向_申购确认.getValue());
        searchParam.put("IS_CANCEL_N", BussinessDictCodeList.是否_否.getValue());
        searchParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        searchParam.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        searchParam.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        searchParam.put("DELIVERY_RESULT_01", "01");
        searchParam.put("DELIVERY_RESULT_02", "02");
        //申购确认
        investTradeReaderDto.setSettleTransDataSgqrList(stlDataGatherMapper.selectCashBonusSettleTransData1(searchParam));

        resultList.add(investTradeReaderDto);
        return resultList;
    }

    @Override
    protected void setSearchParam(Map<String, Object> searchParam) {
        // 交易方向列表
        searchParam.put("psList", Arrays.asList(DictCodeList.交易方向_申购确认.getValue()));
    }

    @Override
    protected boolean isSecType(String secType) {
        return StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), secType);
    }

    @Override
    protected boolean isCreateTrade(){
        return false;
    }
}
