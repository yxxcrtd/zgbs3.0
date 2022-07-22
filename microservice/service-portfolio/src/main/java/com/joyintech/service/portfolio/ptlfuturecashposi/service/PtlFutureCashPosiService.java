/*
 * 文件名：PtlFutureCashPosiService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：renping
 * 修改时间：2019年11月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.portfolio.ptlfuturecashposi.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.portfolio.ptlfuturecashposi.dao.PtlFutureCashPosiMapper;
import com.joyintech.service.portfolio.ptlfuturecashposi.dto.PtlFutureCashPosiDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;

/**
 * 组合未来头寸分析头寸查询 〈功能详细描述〉
 * 
 * @author renping
 * @version 1.0 2019年11月9日
 * @see PtlFutureCashPosiService
 * @since 1.0
 */
@Service("backPtlFutureCashPosiService")
public class PtlFutureCashPosiService extends BaseService<PtlFutureCashPosiDto> {

    /**
     * PtlFutureCashPosiMapper
     */
    @Autowired
    private PtlFutureCashPosiMapper ptlFutureCashPosiMapper;

    /**
     * 查询
     * 
     * @param pageParam 查询条件
     * @return List对象
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public List<PtlFutureCashPosiDto> selectData(Map<String, Object> pageParam)
    {
        setParam(pageParam);
        List<PtlFutureCashPosiDto> pages = ptlFutureCashPosiMapper.selectData(pageParam);
        if (StringUtil.isEmptyList(pages)) {
            return pages;
        }
        List<String> dateArrayList = (List<String>)pageParam.get("dateArrayList");
        List<PtlFutureCashPosiDto> result = new ArrayList<>();
        List<Map<String, PtlFutureCashPosiDto>> resultMapList = new ArrayList<>();
        Map<String, PtlFutureCashPosiDto> periodMap;
        for (int i = 1; i < dateArrayList.size(); i++ ) {
            periodMap = new HashMap<>();
            resultMapList.add(periodMap);
            String startDate;
            String index = "";
            if (StringUtil.equals(pageParam.get("showDimension"),
                DictCodeListExt3.展示维度_短期.getValue())) {
                startDate = dateArrayList.get(0);
                if (i == 2) {
                    index = "一周";
                }
                else if (i == 3) {
                    index = "一月";
                }
            }
            else {
                startDate = dateArrayList.get(i - 1);
            }
            String endDate = dateArrayList.get(i);
            PtlFutureCashPosiDto dto = new PtlFutureCashPosiDto();
            result.add(dto);
            dto.setPortfolioId(DateUtil.addDay(startDate, 1) + "至" + endDate + index);
            dto.setUpperPortfolioCode("root");
            dto.setHasChildren("1");
            for (PtlFutureCashPosiDto item : pages) {
                if (item.getPayDate().compareTo(DateUtil.parse(startDate)) > 0
                    && item.getPayDate().compareTo(DateUtil.parse(endDate)) <= 0) {
                    PtlFutureCashPosiDto periodDto;
                    if (periodMap.containsKey(item.getPortfolioId())) {
                        periodDto = periodMap.get(item.getPortfolioId());
                    }
                    else {
                        periodDto = new PtlFutureCashPosiDto();
                        periodDto.setPortfolioId(item.getPortfolioId());
                        periodDto.setMdate(item.getMdate());
                        periodDto.setUpperPortfolioCode(dto.getPortfolioId());
                        periodDto.setProfitType(item.getProfitType());
                        periodDto.setBelongInstitutionCode(item.getBelongInstitutionCode());
                        periodDto.setBelongInstitutionName(item.getBelongInstitutionName());
                        periodMap.put(item.getPortfolioId(), periodDto);
                    }

                    periodDto.setAssetPeriodInAmt(ArithmeticUtil.doAdd(
                        periodDto.getAssetPeriodInAmt(), item.getAssetPeriodInAmt()));
                    periodDto.setAssetPeriodOutAmt(ArithmeticUtil.doAdd(
                        periodDto.getAssetPeriodOutAmt(), item.getAssetPeriodOutAmt()));
                    periodDto.setCashAmt(
                        ArithmeticUtil.doAdd(periodDto.getCashAmt(), item.getCashAmt()));
                    periodDto.setPrdEndAmt(
                        ArithmeticUtil.doAdd(periodDto.getPrdEndAmt(), item.getPrdEndAmt()));
                    periodDto.setUsableAmt(
                        ArithmeticUtil.doAdd(periodDto.getUsableAmt(), item.getUsableAmt()));
                }
            }
        }
        List<PtlFutureCashPosiDto> resultList = new ArrayList<>();
        for (int i = 0; i < dateArrayList.size() - 1; i++ ) {
            Map<String, PtlFutureCashPosiDto> map = resultMapList.get(i);
            PtlFutureCashPosiDto total = result.get(i);
            resultList.add(total);
            total.setAssetPeriodInAmt(BigDecimal.ZERO);
            total.setAssetPeriodOutAmt(BigDecimal.ZERO);
            total.setCashAmt(BigDecimal.ZERO);
            total.setPrdEndAmt(BigDecimal.ZERO);
            total.setUsableAmt(BigDecimal.ZERO);
            total.setCashGapAmt(BigDecimal.ZERO);
            if (!StringUtil.equals(pageParam.get("showDimension"),
                DictCodeListExt3.展示维度_短期.getValue()) && i > 0) {
                // 组合数据补全,并根据上一区间计算组合可用现金
                doPortfolioDataAdd(map, resultMapList.get(i - 1), total.getPortfolioId(),
                    dateArrayList.get(i));
            }
            for (PtlFutureCashPosiDto m : map.values()) {
                resultList.add(m);
                // 组合类型为净值型或货币性且组合结束日小等于查询结束日时，组合结束日所在的时间区间的 产品到期 =
                // -1*（可用现金+资产存续期（资金流入）+资产存续期（资金流出））
                if ((StringUtil.equals(m.getProfitType(), DictCodeListExt2.收益类型_净值.getValue())
                     || StringUtil.equals(m.getProfitType(), DictCodeListExt2.收益类型_货币.getValue()))
                    && m.getMdate().compareTo(DateUtil.parse(dateArrayList.get(i))) > 0
                    && m.getMdate().compareTo(DateUtil.parse(dateArrayList.get(i + 1))) <= 0) {
                    m.setPrdEndAmt(ArithmeticUtil.multiply(ArithmeticUtil.doAdd(m.getUsableAmt(),
                        m.getAssetPeriodInAmt(), m.getAssetPeriodOutAmt()), new BigDecimal(-1)));
                    m.setCashGapAmt(ArithmeticUtil.doAdd(ArithmeticUtil.doSub(m.getUsableAmt(),
                            m.getAssetPeriodOutAmt()), m.getAssetPeriodInAmt(), m.getPrdEndAmt()));
                }else{
                    // 到期缺口：各时间区间的组合可用现金+产品到期+资产存续期（资金流入）+资产存续期（资金流出）
    //                m.setCashGapAmt(ArithmeticUtil.doAdd(m.getUsableAmt(), m.getPrdEndAmt(),
    //                    m.getAssetPeriodInAmt(), m.getAssetPeriodOutAmt()));
                    // add by liuwenjuan 到期缺口计算 start
                    // 到期缺口:各时间区间的组合可用现金-产品到期+资产存续期（资金流入）-资产存续期（资金流出）
                    m.setCashGapAmt(ArithmeticUtil.doAdd(ArithmeticUtil.doSub(m.getUsableAmt(), m.getPrdEndAmt(),
                            m.getAssetPeriodOutAmt()), m.getAssetPeriodInAmt()));
                    // add by liuwenjuan 到期缺口计算 end
                }
                // 计算汇总金额
                total.setAssetPeriodInAmt(
                    ArithmeticUtil.doAdd(total.getAssetPeriodInAmt(), m.getAssetPeriodInAmt()));
                total.setAssetPeriodOutAmt(
                    ArithmeticUtil.doAdd(total.getAssetPeriodOutAmt(), m.getAssetPeriodOutAmt()));
                total.setCashAmt(ArithmeticUtil.doAdd(total.getCashAmt(), m.getCashAmt()));
                total.setPrdEndAmt(ArithmeticUtil.doAdd(total.getPrdEndAmt(), m.getPrdEndAmt()));
                total.setUsableAmt(ArithmeticUtil.doAdd(total.getUsableAmt(), m.getUsableAmt()));
                total.setCashGapAmt(
                    ArithmeticUtil.doAdd(total.getCashGapAmt(), m.getCashGapAmt()));
            }

        }
        return resultList;
    }

    /**
     * 当前区间如果没有上一区间的组合数据，则补齐组合可用现金，其余金额均为0，否则根据上一区间组合数据直接计算当前区间组合可用现金
     * 
     * @param periodMap 当前区间组合数据Map
     * @param lastMap 上一区间组合数据Map
     * @param upperPortfolioCode 当前区间父节点
     * @param arrayLastEndDate 上一区间结束日
     * @see
     * @since 1.0
     */
    private void doPortfolioDataAdd(Map<String, PtlFutureCashPosiDto> periodMap,
                                    Map<String, PtlFutureCashPosiDto> lastMap,
                                    String upperPortfolioCode, String arrayLastEndDate) {
        for (PtlFutureCashPosiDto lastDto : lastMap.values()) {
            if (periodMap.containsKey(lastDto.getPortfolioId())) {
                // 组合可用现金 = 上个区间的组合可用现金+ 上个区间的 资产存续期（资金流出）+ 资产存续期（资金流入）
                periodMap.get(lastDto.getPortfolioId()).setUsableAmt(
                        ArithmeticUtil.subtract(ArithmeticUtil.doAdd(lastDto.getUsableAmt(), lastDto.getAssetPeriodInAmt()
                        ),lastDto.getAssetPeriodOutAmt()));
            }
            else if (DateUtil.parse(arrayLastEndDate).compareTo(lastDto.getMdate()) < 0) {
                PtlFutureCashPosiDto newDto = new PtlFutureCashPosiDto();
                newDto.setPortfolioId(lastDto.getPortfolioId());
                newDto.setMdate(lastDto.getMdate());
                newDto.setUpperPortfolioCode(upperPortfolioCode);
                newDto.setAssetPeriodInAmt(BigDecimal.ZERO);
                newDto.setAssetPeriodOutAmt(BigDecimal.ZERO);
                newDto.setCashAmt(BigDecimal.ZERO);
                newDto.setPrdEndAmt(BigDecimal.ZERO);
                // 组合可用现金 = 上个区间的组合可用现金+ 上个区间的 资产存续期（资金流出）+ 资产存续期（资金流入）
                newDto.setUsableAmt(ArithmeticUtil.subtract(ArithmeticUtil.doAdd(lastDto.getUsableAmt(),
                    lastDto.getAssetPeriodInAmt()), lastDto.getAssetPeriodOutAmt()));
                periodMap.put(newDto.getPortfolioId(), newDto);
            }
        }
    }

    /**
     * 查询参数常量设置
     * 
     * @param pageParam 参数
     * @see
     * @since 1.0
     */
    private void setParam(Map<String, Object> pageParam) {
        pageParam.put("YES_OR_NO_N", DictCodeListExt1.是否_否.getValue());
        pageParam.put("YES_OR_NO_Y", DictCodeListExt1.是否_是.getValue());
        pageParam.put("CASH_CLASS_TYPE_02", DictCodeList.现金流类型_中间还本.getValue());
        pageParam.put("CASH_CLASS_TYPE_03", DictCodeList.现金流类型_到期还本.getValue());
        pageParam.put("CASH_CLASS_TYPE_01", DictCodeList.现金流类型_中间收息.getValue());
        pageParam.put("CASH_CLASS_TYPE_04", DictCodeList.现金流类型_到期收息.getValue());
        pageParam.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());
        pageParam.put("POSITION_TYPE_08", DictCodeListExt1.持仓类型_预占用.getValue());
        pageParam.put("PORTFOLIO_TYPE_01", DictCodeListExt1.组合类型_投资类组合.getValue());
        pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("FINPROD_TYPE_TG_F07", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        pageParam.put("FINPROD_TYPE_TG_F18", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        pageParam.put("FINPROD_TYPE_TG_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        pageParam.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        pageParam.put("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        pageParam.put("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        pageParam.put("FINPROD_TYPE_TG_F08", DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
        pageParam.put("FINPROD_TYPE_TG_F09", DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
        pageParam.put("FINPROD_TYPE_TG_F04", DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
        pageParam.put("FINPROD_TYPE_TG_F05", DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
        pageParam.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        pageParam.put("FINPROD_TYPE_TG_F16", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        pageParam.put("FINPROD_TYPE_TG_F29", DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
        pageParam.put("FINPROD_TYPE_TG_F30", DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
        pageParam.put("PROFIT_TYPE_02", DictCodeListExt2.收益类型_预期收益.getValue());
        pageParam.put("PROFIT_TYPE_01", DictCodeListExt2.收益类型_净值.getValue());
        pageParam.put("PROFIT_TYPE_03", DictCodeListExt2.收益类型_货币.getValue());
        pageParam.put("EXEC_MODE_01", DictCodeList.行权方式_赎回.getValue());
        pageParam.put("EXEC_MODE_02", DictCodeList.行权方式_回售.getValue());
        pageParam.put("VALUATION_SOURCE_CSD", DictCodeList.估值来源_中证登_CSD.getValue());
        pageParam.put("VALUATION_SOURCE_CBC", DictCodeList.估值来源_中债登_CBC.getValue());
        pageParam.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        pageParam.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        pageParam.put("CCY_CNY", DictCodeList.币种_人民币_CNY.getValue());
        pageParam.put("BASIS_A_360", DictCodeList.计息基础_A_360.getValue());
        pageParam.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
    }
}
