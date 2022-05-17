package com.joyintech.tams.settle.batch.dealrevoke.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleBatchPara;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.service.StlDownDataDealReaderService;
import com.joyintech.tams.settle.batch.dealrevoke.dto.DealRevokeParamDto;
import com.joyintech.tams.settle.batch.dealrevoke.service.InvestDealRevokeService;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 何阳阳
 * @date 2020/8/3
 * @see InvestDealRevokeReader
 * @since 1.0
 */
@Service
@Lazy
public class InvestDealRevokeReader extends BaseReader<DealRevokeParamDto> {
    @Autowired
    private InvestDealRevokeService investDealRevokeService;

    @Autowired
    private StlDownDataDealReaderService stlDownDataDealService;

    @Override
    public List<DealRevokeParamDto> createList() {
        //mody by   meifaxin  20211028 不从画面触发，改成文件解析后触发 start
        // 批处理执行信息
        SysBatchExecInfo execInfo = getBatchExecuteInfo();
        // 日期
        Date tradeDate = execInfo.getExecuteDate();
//        // 数据清算参数表
//        List<SettleBatchPara> list = stlDownDataDealService.selectAllParam(tradeDate);
        // 撤销参数
        List<DealRevokeParamDto> revokeParamDtos = new ArrayList<>();

//        // 按证券品种分组
//        Map<String, List<SettleBatchPara>> listMap = list.stream().collect(JoyinCollectors.groupingBy(SettleBatchPara::getSecType));
//
//        listMap.forEach((key, value) -> {
//            // 提取当前分组中的组合代码
//            List<String> portfolioIdList = value.stream().map(SettleBatchPara::getPortfolioId).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
//            // 根据证券品种分别调用不同的查询逻辑，查询交易编号和汇总交易编号
//            if(StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), key)) {
                // 汇总交易编号
                List<String> portfolioIdList=null;
                List<String> bondTotalTradeIdList = investDealRevokeService.selectTotalTradeIdForBond(tradeDate, portfolioIdList);
                Map<String,String> bondTotcalTradeIdMap = investDealRevokeService.getTotalTradeIdPsMap(bondTotalTradeIdList);
                // modify by etliu 20220222 start
                // 广农商二阶段 GNS-890:【广农商场内清算】场内债券存续期交易重复跑批时，未对原交易数据进行撤销操作
                for (String totalTradeId: bondTotalTradeIdList) {
                    if (!bondTotcalTradeIdMap.containsKey(totalTradeId)) {
                        bondTotcalTradeIdMap.put(totalTradeId, totalTradeId);
                    }
                }
                // modify by etliu 20220222 end
                revokeParamDtos.add(new DealRevokeParamDto(DictCodeListExt1.证券品种_债券.getValue(), tradeDate, portfolioIdList,null, bondTotalTradeIdList,null,bondTotcalTradeIdMap));
//            }
//            else if(StringUtil.equals(DictCodeListExt1.证券品种_回购.getValue(), key)) {
                // 交易编号
                List<String> tradeIdList = investDealRevokeService.selectTradeIdForRepo(tradeDate, portfolioIdList);
                // 汇总交易编号
                List<String> repoTotalTradeIdList = investDealRevokeService.selectTotalTradeIdForRepo(tradeDate, portfolioIdList);
                Map<String,String> repoTradeIdMap = investDealRevokeService.getTradeIdPsMap(tradeIdList);
                Map<String,String> repoTotcalTradeIdMap = investDealRevokeService.getRepoTradeIdPsMap(repoTotalTradeIdList);
                // modify by etliu 20220222 start
                // 广农商二阶段 GNS-890:【广农商场内清算】场内债券存续期交易重复跑批时，未对原交易数据进行撤销操作
                for (String totalTradeId: repoTotalTradeIdList) {
                    if (!repoTotcalTradeIdMap.containsKey(totalTradeId)) {
                        repoTotcalTradeIdMap.put(totalTradeId, totalTradeId);
                    }
                }
                // modify by etliu 20220222 end
                revokeParamDtos.add(new DealRevokeParamDto(DictCodeListExt1.证券品种_回购.getValue(), tradeDate, portfolioIdList, tradeIdList, repoTotalTradeIdList,repoTradeIdMap,repoTotcalTradeIdMap));
//            }
//            else if(StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), key)) {
                // 交易编号
                List<String> sharesTradeIdList = investDealRevokeService.selectTradeIdForShares(tradeDate, portfolioIdList);
                // 汇总交易编号
                List<String> sharesTotalTradeIdList = investDealRevokeService.selectTotalTradeIdForShares(tradeDate, portfolioIdList);
                Map<String,String> sharesTradeIdMap = investDealRevokeService.getTradeIdPsMap(sharesTradeIdList);
                Map<String,String> sharesTotcalTradeIdMap = investDealRevokeService.getTotalTradeIdPsMap(sharesTotalTradeIdList);
                // modify by etliu 20220222 start
                // 广农商二阶段 GNS-890:【广农商场内清算】场内债券存续期交易重复跑批时，未对原交易数据进行撤销操作
                for (String totalTradeId: sharesTotalTradeIdList) {
                    if (!sharesTotcalTradeIdMap.containsKey(totalTradeId)) {
                        sharesTotcalTradeIdMap.put(totalTradeId, totalTradeId);
                    }
                }
                // modify by etliu 20220222 end
                revokeParamDtos.add(new DealRevokeParamDto(DictCodeListExt1.证券品种_股票.getValue(), tradeDate, portfolioIdList, sharesTradeIdList, sharesTotalTradeIdList,sharesTradeIdMap,sharesTotcalTradeIdMap));
//            }
//            else if(StringUtil.equals(DictCodeListExt1.证券品种_基金.getValue(), key)) {
                // 交易编号
                List<String> fundTradeIdList = investDealRevokeService.selectTradeIdForFund(tradeDate, portfolioIdList);
                // 汇总交易编号
                List<String> fundTotalTradeIdList = investDealRevokeService.selectTotalTradeIdForFund(tradeDate, portfolioIdList);
                Map<String,String> fundTradeIdMap = investDealRevokeService.getTradeIdPsMap(fundTradeIdList);
                Map<String,String> fundTotalTradeIdMap = investDealRevokeService.getTotalTradeIdPsMap(fundTotalTradeIdList);
                // modify by etliu 20220222 start
                // 广农商二阶段 GNS-890:【广农商场内清算】场内债券存续期交易重复跑批时，未对原交易数据进行撤销操作
                for (String totalTradeId: fundTotalTradeIdList) {
                    if (!fundTotalTradeIdMap.containsKey(totalTradeId)) {
                        fundTotalTradeIdMap.put(totalTradeId, totalTradeId);
                    }
                }
                // modify by etliu 20220222 end
                revokeParamDtos.add(new DealRevokeParamDto(DictCodeListExt1.证券品种_基金.getValue(), tradeDate, portfolioIdList, fundTradeIdList, fundTotalTradeIdList,fundTradeIdMap,fundTotalTradeIdMap));
            //}

                // 信用保护凭证交易撤销 add by fuyongsheng 20220517 start
                List<String> creditTotalTradeIdList = investDealRevokeService.selectTotalTradeIdForCredit(tradeDate, portfolioIdList);
                Map<String,String> creditTotalTradeIdMap = investDealRevokeService.getTotalTradeIdPsMap(creditTotalTradeIdList);
                for (String totalTradeId: creditTotalTradeIdList) {
                    if (!creditTotalTradeIdMap.containsKey(totalTradeId)) {
                        creditTotalTradeIdMap.put(totalTradeId, totalTradeId);
                    }
                }
                revokeParamDtos.add(new DealRevokeParamDto(DictCodeListStl.证券品种_信用保护凭证.getValue(), tradeDate,
                        portfolioIdList,null, creditTotalTradeIdList,null, creditTotalTradeIdMap));
                // 信用保护凭证交易撤销 add by fuyongsheng 20220517 end
        return revokeParamDtos;
        //mody by   meifaxin  20211028 不从画面触发，改成文件解析后触发 end
    }
}
