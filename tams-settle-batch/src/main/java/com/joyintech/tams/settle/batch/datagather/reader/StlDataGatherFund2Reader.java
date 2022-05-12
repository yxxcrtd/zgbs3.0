/*
 * 文件名：StlDataGatherFund2Reader.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2020年07月30日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.settle.batch.datagather.reader;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleBatchPara;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.settle.batch.datagather.constant.StlDataGatherConstant;
import com.joyintech.tams.settle.batch.datagather.dto.InvestTradeReaderDto;
import com.joyintech.tams.settle.batch.datagather.dto.SettleDelDealDetailDto;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 基金分红处理
 * @author 王波
 * @date 2020年07月30日
 * @see StlDataGatherFund2Reader
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherFund2Reader extends StlDataGatherReader {

    @Override
    public List<InvestTradeReaderDto> createList() {
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

        Map<String, Object> searchParam = new HashMap<>();
        searchParam.put("portfolioIdList", portfolioIdList);
        searchParam.put("dealDate", execInfo.getExecuteDate());
        // 设置公共查询参数
        this.setSearchParam(searchParam);
        
        List<InvestTradeReaderDto> res = new ArrayList<InvestTradeReaderDto>();
        
        InvestTradeReaderDto investTradeReaderDto = new InvestTradeReaderDto();
        investTradeReaderDto.setSettleTransDataList(new ArrayList<>());
        // 除权公告数据（仅针对非货基的现金分红）
        // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 start
        List<SettleDelDealDetailDto> settleTransDataDelDtos;
        // 新老头寸开关(Y:新头寸,N:老头寸)
        String newSecPositionSwitch = CacheUtil.getSysParam("NEW_SEC_POSITION_SWITCH");
        // 开关配置N查老头存
        if(DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)){
            settleTransDataDelDtos = stlDataGatherMapper.selectFundDelSettleTransData(searchParam);
        }else{ // 默认查新头寸
            searchParam.put("hodingType", DictCodeListExt3.资产类型_资产账户持仓.getValue());
            searchParam.put("dateType", StlDataGatherConstant.DATE_TYPE_T_0);
            settleTransDataDelDtos = stlDataGatherMapper.selectFundDelSettleTransDataNewPosition(searchParam);
        }
        // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 end
        if (settleTransDataDelDtos != null && settleTransDataDelDtos.size() > 0) {
	        investTradeReaderDto.setSettleTransDataDelDtoList(settleTransDataDelDtos);
        }
        searchParam.put("DELIVERY_RESULT_01", DictCodeListExt4.交收结果_正常交收.getValue());
        searchParam.put("DELIVERY_RESULT_02", DictCodeListExt4.交收结果_部分交收.getValue());
        List<SettleTransDataDto> tempSettleTransDataDtos = stlDataGatherMapper.selectFundSettleTransData(searchParam);
        if(CollectionUtils.isNotEmpty(tempSettleTransDataDtos)) {
            for (SettleTransDataDto dto : tempSettleTransDataDtos) {
                // GNS-872:参数传入sql中不能为空 modify by fuyongsheng 20220218 start
                dto.setUnitCprice(StringUtil.isNotEmpty(dto.getUnitCprice())? dto.getUnitCprice() : new BigDecimal(0));
                dto.setPrinAmt(StringUtil.isNotEmpty(dto.getPrinAmt())? dto.getPrinAmt() : new BigDecimal(0));
                dto.setIntAmt(StringUtil.isNotEmpty(dto.getIntAmt())? dto.getIntAmt() : new BigDecimal(0));
                dto.setFpriceAmt(StringUtil.isNotEmpty(dto.getFpriceAmt())? dto.getFpriceAmt() : new BigDecimal(0));
                dto.setFragmentaryFunds(StringUtil.isNotEmpty(dto.getFragmentaryFunds())? dto.getFragmentaryFunds() : new BigDecimal(0));
                dto.setTradeAmt(StringUtil.isNotEmpty(dto.getTradeAmt())? dto.getTradeAmt() : new BigDecimal(0));
                // GNS-872:参数传入sql中不能为空 modify by fuyongsheng 20220218 end
                if("Y".equals(dto.getIsHuobi())){
                    // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 start
                    // investTradeReaderDto.getSettleTransDataList().addAll(stlDataGatherMapper.selectFundSettleTransData1(dto));
                    // 新老头寸开关(Y:新头寸,N:老头寸)
                    if(DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)){
                        investTradeReaderDto.getSettleTransDataList().addAll(stlDataGatherMapper.selectFundSettleTransData1(dto));
                    }else{ // 默认查新头寸
                        investTradeReaderDto.getSettleTransDataList().addAll(stlDataGatherMapper.selectFundSettleTransData1NewPosition(dto));
                    }
                    // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 end
                }else {
                    // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 start
                    // investTradeReaderDto.getSettleTransDataList().addAll(stlDataGatherMapper.selectFundSettleTransData2(dto));
                    // 开关配置N查老头存
                    if(DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)){
                        investTradeReaderDto.getSettleTransDataList().addAll(stlDataGatherMapper.selectFundSettleTransData2(dto));
                    }else{ // 默认查新头寸
                        investTradeReaderDto.getSettleTransDataList().addAll(stlDataGatherMapper.selectFundSettleTransData2NewPosition(dto));
                    }
                    // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 end
                }
            }
        }
        res.add(investTradeReaderDto);
        return res;
    }

    @Override
    protected void setSearchParam(Map<String, Object> searchParam) {
        // 业务类型
    	List<String> busiTypeList = new ArrayList<String>();
    	busiTypeList.add(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
    	busiTypeList.add(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
        searchParam.put("busiTypeList", busiTypeList);
        // 交易方向列表
        searchParam.put("psList", Arrays.asList(DictCodeList.交易方向_现金分红.getValue(), DictCodeList.交易方向_红利再投.getValue()));
    }

    @Override
    protected boolean isSecType(String secType) {
        return false;
    }

    @Override
    protected boolean isCreateTrade(){
        return false;
    }
}
