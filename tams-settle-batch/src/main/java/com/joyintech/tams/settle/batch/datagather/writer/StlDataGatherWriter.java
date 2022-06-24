package com.joyintech.tams.settle.batch.datagather.writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinDealPledgeMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductAddMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.common.base.dao.PtlTradeMapper;
import com.joyintech.tams.common.base.dao.StlPayOrdersMapper;
import com.joyintech.tams.common.base.dao.TrdDealTotalAddMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealMapper;
import com.joyintech.tams.common.base.dao.TrdTradeFeeDetailMapper;
import com.joyintech.tams.common.base.entity.FinDealPledge;
import com.joyintech.tams.common.base.entity.FinTradeProductAdd;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdDealTotalAdd;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datagather.dao.StlDataGatherMapper;
import com.joyintech.tams.settle.batch.datagather.dto.FinTradeProductDto;
import com.joyintech.tams.settle.batch.datagather.dto.InvestTradeWriteDto;
import com.joyintech.tams.settle.batch.datagather.dto.TotalTrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.dto.TrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;

/**
 * @author 何阳阳
 * @date 2020/7/16
 * @see StlDataGatherWriter
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherWriter extends BaseWriter<NeedToWriteEntity> {




    @Autowired
    private StlDataGatherService stlDataGatherService;

    @Override
    public void write(List<? extends NeedToWriteEntity> items) throws Exception {
        for (NeedToWriteEntity item : items) {
            if(item instanceof InvestTradeWriteDto) {
                InvestTradeWriteDto tradeWriteDto = (InvestTradeWriteDto)item;
                stlDataGatherService.writeData(tradeWriteDto);
                
                
                // 更新持仓
                doComplete(tradeWriteDto);
//                if (CollectionUtils.isNotEmpty(productDealList)) {
//                    TrdProductDealDto trdProductDealDto = productDealList.get(0);
//                    if (StringUtil.equals(trdProductDealDto.getBusiType(), DictCodeList.共通业务类型_债券类资产交易.getValue())) {
//                        if ( Arrays.asList(DictCodeList.交易方向_买入.getValue()
//                                ,DictCodeList.交易方向_卖出.getValue()
//                                ,DictCodeList.交易方向_行权_回售.getValue())
//                                .contains(trdProductDealDto.getPs())) {
//                            // 参考债券二级交易生效逻辑，更新头寸、持仓、委托完成度信息。
//                            doCompleteTrdBondInvestTrade(tradeWriteDto);
//                        }
//                    }
//                }

//                4、参考可转债申购交易生效逻辑，更新头寸、持仓、委托完成度信息。
//
//                5、按每个投管交易DTO的成交编号，更新‘成交数据与投管交易关系表’中的数据
//                        投管交易编号 = 投管交易DTO.汇总交易编号
//                投管交易数据状态 = ‘已处理’


//                4、参考可转债申购确认交易生效逻辑，更新头寸、持仓、委托完成度信息。
//
//                5、按每个投管交易DTO的成交编号，更新‘成交数据与投管交易关系表’中的数据
//                        投管交易编号 = 投管交易DTO.汇总交易编号
//                投管交易数据状态 = ‘已处理’

//                4、参考债转股交易生效逻辑，更新头寸、持仓、委托完成度信息。
//
//                5、按每个投管交易DTO的成交编号，更新‘成交数据与投管交易关系表’中的数据
//                        投管交易编号 = 投管交易DTO.汇总交易编号
//                投管交易数据状态 = ‘已处理’



//                4、参考债券质押式回购交易生效逻辑，更新头寸、持仓、委托完成度信息。
//
//                5、按每个投管交易DTO的成交编号，更新‘成交数据与投管交易关系表’中的数据
//                        投管交易编号 = 投管交易DTO.交易明细.交易编号
//                投管交易数据状态 = ‘已处理’

//                6、参考债券质押式回购到期续作交易生效逻辑，更新头寸、持仓、委托完成度信息。
//
//                7、按每个投管交易DTO的成交编号，更新‘成交数据与投管交易关系表’中的数据
//                        投管交易编号 = 投管交易DTO.交易明细.交易编号
//                投管交易数据状态 = ‘已处理’

//                5、参考债券质押式回购提前终止、到期交易生效逻辑，更新头寸、持仓、委托完成度信息。
//
//                6、按每个投管交易DTO的成交编号，更新‘成交数据与投管交易关系表’中的数据
//                        投管交易编号 = 投管交易DTO.交易明细.交易编号
//                投管交易数据状态 = ‘已处理’

//                5、参考债券质押式回购提前终止、到期交易生效逻辑，更新头寸、持仓、委托完成度信息。
//
//                6、按每个投管交易DTO的成交编号，更新‘成交数据与投管交易关系表’中的数据
//                        投管交易编号 = 投管交易DTO.交易明细.交易编号
//                投管交易数据状态 = ‘已处理’

//                5、参考债券质押式回购到期交易生效逻辑，更新头寸、持仓、委托完成度信息。
//
//                6、按每个投管交易DTO的成交编号，更新‘成交数据与投管交易关系表’中的数据
//                        投管交易编号 = 投管交易DTO.交易明细.交易编号
//                投管交易数据状态 = ‘已处理’

            }
        }
    }

    /**
     * 参考债券二级交易生效逻辑，更新头寸、持仓、委托完成度信息
     * @param tradeWriteDto 交易DTO
     *
     */
    private void doComplete(InvestTradeWriteDto tradeWriteDto) {
        // 更新头寸、持仓
        Map<String, Object> params = new HashMap<>();
        List<String> tradeIdList = tradeWriteDto.getProductDealList()
                .stream().map(TrdProductDealDto::getTradeId).collect(Collectors.toList());
        params.put("tradeIdList", tradeIdList);

        if (CollectionUtils.isNotEmpty(tradeWriteDto.getFinProductDealTotalList())) {
            List<String> totalTradeIdList = tradeWriteDto.getFinProductDealTotalList()
                    .stream().map(TotalTrdProductDealDto::getTotleTradeId).collect(Collectors.toList());
            // modify by etliu 20220228 start
            // 广农商二阶段 GNS-890:【广农商场内清算】场内债券存续期交易重复跑批时，未对原交易数据进行撤销操作
            List<String> pTotalTradeIdList = tradeWriteDto.getFinProductDealTotalList()
                    .stream().map(TotalTrdProductDealDto::getpTotleTradeId).collect(Collectors.toList());
            if (!StringUtil.isEmptyList(pTotalTradeIdList)) {
                for (String pTotalTradeId : pTotalTradeIdList) {
                    if (totalTradeIdList.contains(pTotalTradeId)) {
                        totalTradeIdList.remove(pTotalTradeId);
                    }
                }
            }
            // modify by etliu 20220228 end
            params.put("totalTradeIdList", totalTradeIdList);
        }
        params.put("busiType", tradeWriteDto.getSecType());
        params.put("status", DictCodeListExt1.交易流程状态_有效.getValue());
        // 二级资产类型
        String assetType2 = null;
        if (tradeWriteDto != null && tradeWriteDto.getTradeProductList() != null && tradeWriteDto.getTradeProductList().size() > 0) {
        	assetType2 = DictCodeList.回购类型_质押式竞价回购.getValue();
        }
        params.put("assetType2", assetType2);
        stlDataGatherService.excutePosition(params);
        
    }


}
