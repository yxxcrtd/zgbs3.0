package com.joyintech.tams.settle.batch.datagather.reader;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleBatchPara;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt4;
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
 * 分红和送转股票交易数据处理
 *
 * @author chenzhuo
 * @date 2020/7/20
 * @see StlDataGatherShares2Reader
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherShares2Reader extends StlDataGatherReader {

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
        setSearchParam(searchParam);
        searchParam.put("dealDate", execInfo.getExecuteDate());
        searchParam.put("DELIVERY_RESULT_01", DictCodeListExt4.交收结果_正常交收.getValue());
        searchParam.put("DELIVERY_RESULT_02", DictCodeListExt4.交收结果_部分交收.getValue());
        searchParam.put("TRADE_MARKET_03", BussinessDictCodeList.交易场所_上海证券交易所.getValue());
        searchParam.put("TRADE_MARKET_04", BussinessDictCodeList.交易场所_深圳证券交易所.getValue());
        searchParam.put("PS_T12", DictCodeList.交易方向_红利再投.getValue());
        searchParam.put("PS_T11", DictCodeList.交易方向_现金分红.getValue());
        searchParam.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        searchParam.put("HODING_TYPE_03", DictCodeList.持仓类型_实际.getValue());

        // GNS-932:除权交易不从SHARE_BONUS_REG读取数据 modify by fuyongsheng 20220224 start
//        List<SettleDelDealDetailDto> settleDelDealDetailDtoList = stlDataGatherService.selectSettleDelDealDetail(searchParam);
//        investTradeReaderDto.setSettleTransDataDelDtoList(settleDelDealDetailDtoList);
        // GNS-932:除权交易不从SHARE_BONUS_REG读取数据 modify by fuyongsheng 20220224 end

        //现金分红和送转和除权登记
        // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 start
        // List<SettleTransDataDto> settleTransDataDtos = stlDataGatherMapper.selectCashBonusSettleTransData(searchParam);
        List<SettleTransDataDto> settleTransDataDtos;
        // 新老头寸开关(Y:新头寸,N:老头寸)
        String newSecPositionSwitch = CacheUtil.getSysParam("NEW_SEC_POSITION_SWITCH");
        // 开关配置N查老头存
        if(DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)){
            settleTransDataDtos = stlDataGatherMapper.selectCashBonusSettleTransData(searchParam);
        }else{ // 默认查新头寸
            searchParam.put("hodingType", DictCodeListExt3.资产类型_资产账户持仓.getValue());
            searchParam.put("dateType", StlDataGatherConstant.DATE_TYPE_T_0);
            settleTransDataDtos = stlDataGatherMapper.selectCashBonusSettleTransDataNewPosition(searchParam);
        }
        // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 end
        investTradeReaderDto.setSettleTransDataList(settleTransDataDtos);

        resultList.add(investTradeReaderDto);
        return resultList;
    }

    @Override
    protected void setSearchParam(Map<String, Object> searchParam) {
        // 交易方向列表
        searchParam.put("psList",
                Arrays.asList(DictCodeList.交易方向_现金分红.getValue(), DictCodeList.交易方向_红利再投.getValue()));
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
