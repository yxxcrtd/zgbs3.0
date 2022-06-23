package com.joyintech.service.product.flowforecastreport.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.service.product.flowforecastreport.constants.FlowConstant;
import com.joyintech.service.product.flowforecastreport.dao.FlowForecastReportMapper;
import com.joyintech.service.product.flowforecastreport.dto.DateAreaDto;
import com.joyintech.service.product.flowforecastreport.dto.FlowCommonDto;
import com.joyintech.service.product.flowforecastreport.dto.FlowQueryDataDto;
import com.joyintech.tams.common.base.entity.FlowForecastConfig;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;

/**
 * @author weipeng
 */
@Service(value = "serviceFlowForecastReportService")
@Transactional(rollbackFor = Exception.class)
public class FlowForecastReportService extends CommonExportService<FlowCommonDto> {

    /**
     * {@linkplain}
     */
    @Autowired
    private FlowForecastReportMapper flowForecastReportMapper;

    public static List<String> listProdIds = Arrays.asList(FlowConstant.FINPROD_ID_RRS,
        FlowConstant.FINPROD_ID_YYS, FlowConstant.FINPROD_ID_XXXR, FlowConstant.FINPROD_ID_XXXR_B,
        FlowConstant.FINPROD_ID_LCB);

    /**
     * 3.流动性储备模块查询
     * 
     * @param page page
     * @param length length
     * @param map map
     * @return Page类 Page
     * @throws Exception 异常信息
     */
    public Page<FlowCommonDto> selectReserve(int page, int length, Map<String, Object> map)
    {
        // 定义变量.查询时间
        String queryDateStr = (String)map.get("queryDate");
        // 定义 变量.返回结果List
        List<FlowCommonDto> listPage = new ArrayList<FlowCommonDto>();
        Date queryDate = DateUtil.parse(queryDateStr);
        Map<String, Object> params = new HashMap<>();
        params.put("queryDate", queryDate);
        params.put("holdingType", DictCodeList.持仓类型_实际.getValue());
        // 变量.产品列表List
        params.put("listFinprodId", this.getFinprodIds(DictCodeList.开放类型_开放式.getValue()));
        // 金融产品代码-日日升
        params.put("listProdIds", listProdIds);
        // 金融产品代码-其他
        params.put("finprodId_qt", FlowConstant.FINPROD_ID_QT);
        // 3.1.一级流动性备付-现金
        List<FlowQueryDataDto> listFlowReserveCash = flowForecastReportMapper.getFlowReserveCash(
            params);
        // 封装listFlowReserveCash
        Map<String, FlowQueryDataDto> cashMap = this.getMapFromList(listFlowReserveCash,
            FlowQueryDataDto::getFinprodId);
        // 变量.现金Entity = new 流动性储备Entity
        FlowCommonDto cashEntity = this.getEntityFromMapById(cashMap, "", FlowConstant.NODE_现金,
            FlowConstant.NODE_一级流动性备付合计, "现金");
        listPage.add(cashEntity);

        // 3.2.一级流动性备付-回购
        params.put("finprodType2_1", DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
        params.put("finprodType2_2", DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue());
        params.put("finprodType2_3", DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
        params.put("finprodType2_4", DictCodeListExt1.金融产品类型_债券买断式正回购.getValue());
        // 正回购，用于返回数据分组
        params.put("finprodTypeRepo", FlowConstant.FINPROD_TYPE_REPO);
        // 逆回购，用于返回数据分组
        params.put("finprodTypeRepoN", FlowConstant.FINPROD_TYPE_REPON);
        List<FlowQueryDataDto> listFlowReserveRepo = flowForecastReportMapper.getFlowReserveRepo(
            params);
        // 定义key返回方法
        Function<FlowQueryDataDto, String> keyMapper = item -> {
            return item.getFinprodId() + item.getType();
        };
        // 定义 变量.回购map = 返回list封装
        Map<String, FlowQueryDataDto> repoMap = this.getMapFromList(listFlowReserveRepo,
            keyMapper);
        // 变量.债券逆回购余额Entity = new 流动性储备Entity
        FlowCommonDto secRepoNEntity = this.getEntityFromMapById(repoMap,
            FlowConstant.FINPROD_TYPE_REPON, FlowConstant.NODE_债券逆回购余额, FlowConstant.NODE_债券逆回购净额,
            "债券逆回购余额");
        listPage.add(secRepoNEntity);

        // 变量.债券正回购余额Entity = new 流动性储备Entity
        FlowCommonDto secRepoEntity = this.getEntityFromMapById(repoMap,
            FlowConstant.FINPROD_TYPE_REPO, FlowConstant.NODE_债券正回购余额, FlowConstant.NODE_债券逆回购净额,
            "债券正回购余额");
        listPage.add(secRepoEntity);

        // 变量.债券逆回购净额Entity = new 流动性储备Entity
        FlowCommonDto secRepoNNetEntity = new FlowCommonDto();
        // 余额 = 债券逆回购余额-债券正回购余额
        secRepoNNetEntity = this.doSubCollect(secRepoNEntity, secRepoEntity,FlowConstant.NODE_债券逆回购净额,
            FlowConstant.NODE_一级流动性备付合计, "债券逆回购净额");
        listPage.add(secRepoNNetEntity);

        // 3.3.一级流动性备付-货基
        Map<String, FlowForecastConfig> flowForecastConfigMap = this.getFlowForecastConfigMap(
            queryDateStr, DictCodeListExt2.流动性预测维护_流动性种类_流动性储备.getValue());
        // 变量.货基Entity = new 流动性储备Entity
        String key = DateUtil.format(queryDate, "YYYYMMDD")
                     + DictCodeListExt2.流动性预测维护_部门类型_产品管理处.getValue()
                     + DictCodeListExt2.流动性预测维护_流动性种类_流动性储备.getValue();
        FlowCommonDto moneyFundEntity = new FlowCommonDto();
        moneyFundEntity.setRowId(FlowConstant.NODE_货基);
        moneyFundEntity.setfRowId(FlowConstant.NODE_一级流动性备付合计);
        moneyFundEntity.setLeaf(true);
        moneyFundEntity.setRowName("货基");
        // 日日升余额 = 流动性储备Map.Get(KEY-货基[指标种类=日日升]).get指标值1
        if (flowForecastConfigMap.get(
            key + DictCodeListExt2.流动性预测维护_指标种类_日日升.getValue()) != null) {
            moneyFundEntity.setRrsBalance(flowForecastConfigMap.get(
                key + DictCodeListExt2.流动性预测维护_指标种类_日日升.getValue()).getTargetTimeone());
        }
        // 月月升余额 = 流动性储备Map.Get(KEY-货基[指标种类=月月升]).get指标值1
        if (flowForecastConfigMap.get(
            key + DictCodeListExt2.流动性预测维护_指标种类_月月升.getValue()) != null) {
            moneyFundEntity.setYysBalance(flowForecastConfigMap.get(
                key + DictCodeListExt2.流动性预测维护_指标种类_月月升.getValue()).getTargetTimeone());
        }
        // 鑫鑫向荣余额 = 流动性储备Map.Get(KEY-货基[指标种类=鑫鑫向荣]).get指标值1
        if (flowForecastConfigMap.get(
            key + DictCodeListExt2.流动性预测维护_指标种类_鑫鑫向荣.getValue()) != null) {
            moneyFundEntity.setXxxrBalance(flowForecastConfigMap.get(
                key + DictCodeListExt2.流动性预测维护_指标种类_鑫鑫向荣.getValue()).getTargetTimeone());
        }
        // 鑫鑫向荣B款余额 = 流动性储备Map.Get(KEY-货基[指标种类=鑫鑫向荣B款]).get指标值1
        if (flowForecastConfigMap.get(
            key + DictCodeListExt2.流动性预测维护_指标种类_鑫鑫向荣B.getValue()) != null) {
            moneyFundEntity.setXxxrBBalance(flowForecastConfigMap.get(
                key + DictCodeListExt2.流动性预测维护_指标种类_鑫鑫向荣B.getValue()).getTargetTimeone());
        }
        // 理财宝余额 = 流动性储备Map.Get(KEY-货基[指标种类=理财宝]).get指标值1
        if (flowForecastConfigMap.get(
            key + DictCodeListExt2.流动性预测维护_指标种类_理财宝.getValue()) != null) {
            moneyFundEntity.setLcbBalance(flowForecastConfigMap.get(
                key + DictCodeListExt2.流动性预测维护_指标种类_理财宝.getValue()).getTargetTimeone());
        }
        // 定开净值型汇总 = 流动性储备Map.Get(KEY-货基[指标种类=定开净值型汇总]).get指标值1
        if (flowForecastConfigMap.get(
            key + DictCodeListExt2.流动性预测维护_指标种类_净值型产品开放期净申赎.getValue()) != null) {
            moneyFundEntity.setDkNetSum(flowForecastConfigMap.get(
                key + DictCodeListExt2.流动性预测维护_指标种类_净值型产品开放期净申赎.getValue()).getTargetTimeone());
        }
        // 合计 = 上面余额合计
        moneyFundEntity.setTotalBalance(
            ArithmeticUtil.doAdd(moneyFundEntity.getRrsBalance(), moneyFundEntity.getYysBalance(),
                moneyFundEntity.getXxxrBalance(), moneyFundEntity.getXxxrBBalance(),
                moneyFundEntity.getLcbBalance(), moneyFundEntity.getDkNetSum()));
        listPage.add(moneyFundEntity);

        // 3.4.一级流动性备付合计
        // 变量.一级流动性备付合计 = new 流动性储备Entity
        FlowCommonDto firstFlowReverseDto = new FlowCommonDto();
        // 余额 = 现金+债券逆回购净额+货基
        // 定义 变量.待汇总List
        List<FlowCommonDto> listCollectDto = new ArrayList<>();
        listCollectDto.add(cashEntity);
        listCollectDto.add(secRepoNNetEntity);
        listCollectDto.add(moneyFundEntity);
        firstFlowReverseDto = this.doCollect(listCollectDto, FlowConstant.NODE_一级流动性备付合计,
            FlowConstant.NODE_ROOT, "一级流动性备付合计");
        listPage.add(firstFlowReverseDto);

        // 3.5.二级流动性备付-债券
        params.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());
        // 2019年9月5日09:27:28 魏鹏：确认后，投管存现金流信息表中债券现在存01
//      params.put("finprodType", DictCodeListExt2.回购标的类别_债券.getValue());
        params.put("finprodType", FlowConstant.FINPROD_TYPE_SEC);
        params.put("holdingType_SJ", DictCodeList.持仓类型_实际.getValue());
        params.put("holdingType_ZY", DictCodeList.持仓类型_质押.getValue());
        params.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        params.put("secType1_GZ", DictCodeList.WIND一级分类_含债券一级分类等_国债.getValue());
        params.put("secType1_TYCD", DictCodeList.WIND一级分类_含债券一级分类等_同业存单.getValue());
        params.put("secType1_JRZ", DictCodeList.WIND一级分类_含债券一级分类等_金融债.getValue());
        params.put("secType2_ZCYHZ", DictCodeList.WIND二级分类_含债券二级分类等_政策银行债.getValue());
        List<FlowQueryDataDto> listSecondFlowReserveSec = flowForecastReportMapper.getSecondFlowReserveSec(
            params);
        // 变量.国债余额Entity = new 流动性储备Entity
        Map<String, FlowQueryDataDto> nationSecMap = this.getMapFromList(listSecondFlowReserveSec,
            keyMapper);
        FlowCommonDto nationDeltEntity = this.getEntityFromMapById(nationSecMap,
            DictCodeList.WIND一级分类_含债券一级分类等_国债.getValue(), FlowConstant.NODE_国债,
            FlowConstant.NODE_二级流动性备付合计, "国债");
        listPage.add(nationDeltEntity);

        // 变量.政策性金融债余额Entity = new 流动性储备Entity
        keyMapper = item -> {
            // 查询结果.Get(产品代码+政策性金融债+持仓种类)
            return item.getFinprodId() + item.getType() + item.getHoldingType();
        };
        Map<String, FlowQueryDataDto> policySecMap = this.getMapFromList(listSecondFlowReserveSec,
            keyMapper);

        // 变量.政策性金融债余额Entity赋值
        FlowCommonDto policySecEntity = this.getEntityFromMapById(policySecMap,
            DictCodeList.WIND一级分类_含债券一级分类等_金融债.getValue() + DictCodeList.持仓类型_实际.getValue(),
            FlowConstant.NODE_政策性金融债余额, FlowConstant.NODE_政策性金融债净额, "政策性金融债余额");
        listPage.add(policySecEntity);

        // 变量.已质押政策性金融债Entity = new 流动性储备Entity
        FlowCommonDto pledgedPolicySecEntity = this.getEntityFromMapById(policySecMap,
            DictCodeList.WIND一级分类_含债券一级分类等_金融债.getValue() + DictCodeList.持仓类型_质押.getValue(),
            FlowConstant.NODE_已质押政策性金融债, FlowConstant.NODE_政策性金融债净额, "已质押政策性金融债");
        listPage.add(pledgedPolicySecEntity);

        // 变量.政策性金融债净额Entity = new 流动性储备Entity
        FlowCommonDto policySecNetEntity = new FlowCommonDto();
        // 政策性金融债净额.x余额 = 政策性金融债.x余额-已质押政策性.x金融债
        policySecNetEntity = this.doSubCollect(policySecEntity,pledgedPolicySecEntity, FlowConstant.NODE_政策性金融债净额,
            FlowConstant.NODE_二级流动性备付合计, "政策性金融债净额");
        listPage.add(policySecNetEntity);

        // 变量.同业存单余额Entity = new 流动性储备Entity
        FlowCommonDto tycdEntity = this.getEntityFromMapById(policySecMap,
            DictCodeList.WIND一级分类_含债券一级分类等_同业存单.getValue() + DictCodeList.持仓类型_实际.getValue(),
            FlowConstant.NODE_同业存单余额, FlowConstant.NODE_同业存单净额, "同业存单余额");
        listPage.add(tycdEntity);

        // 变量.已质押同业存单Entity = new 流动性储备Entity
        FlowCommonDto pledgedTycdEntity = this.getEntityFromMapById(policySecMap,
            DictCodeList.WIND一级分类_含债券一级分类等_同业存单.getValue() + DictCodeList.持仓类型_质押.getValue(),
            FlowConstant.NODE_已质押同业存单, FlowConstant.NODE_同业存单净额, "已质押同业存单");
        listPage.add(pledgedTycdEntity);

        // 变量.同业存单净额Entity = new 流动性储备Entity
        FlowCommonDto tycdNetEntity = new FlowCommonDto();
        // 同业存单净额.x余额 = 同业存单.x余额-已质押同业存单.x金融债
        tycdNetEntity = this.doSubCollect(tycdEntity, pledgedTycdEntity, FlowConstant.NODE_同业存单净额,
            FlowConstant.NODE_二级流动性备付合计, "同业存单净额");
        listPage.add(tycdNetEntity);

        // 3.6.二级流动性备付合计
        FlowCommonDto secondFlowReverseDto = new FlowCommonDto();
        // 二级流动性备付合计.x余额 = 国债+政策性金融债净额+同业存单净额
        listCollectDto.clear();
        listCollectDto.add(nationDeltEntity);
        listCollectDto.add(policySecNetEntity);
        listCollectDto.add(tycdNetEntity);
        secondFlowReverseDto = this.doCollect(listCollectDto, FlowConstant.NODE_二级流动性备付合计,
            FlowConstant.NODE_ROOT, "二级流动性备付合计");
        listPage.add(secondFlowReverseDto);

        // 3.7.一二级流动性备付合计
        // 变量.一二级流动性备付合计 = new 流动性储备Entity
        FlowCommonDto 一二级流动性备付合计 = new FlowCommonDto();
        // 一二级流动性备付合计.x余额 = 一级流动性备付合计+二级流动性备付合计
        listCollectDto.clear();
        listCollectDto.add(firstFlowReverseDto);
        listCollectDto.add(secondFlowReverseDto);
        一二级流动性备付合计 = this.doCollect(listCollectDto, FlowConstant.NODE_一二级流动性备付合计,
            FlowConstant.NODE_ROOT, "一二级流动性备付合计");
        listPage.add(一二级流动性备付合计);

        // 3.8.产品规模
        List<FlowQueryDataDto> listFinproductScale = flowForecastReportMapper.getFinproductScale(
            params);
        Map<String, FlowQueryDataDto> finproductScaleMap = this.getMapFromList(listFinproductScale,
            FlowQueryDataDto::getFinprodId);
        FlowCommonDto 产品规模 = this.getEntityFromMapById(finproductScaleMap, "",
            FlowConstant.NODE_产品规模, FlowConstant.NODE_ROOT, "产品规模");
        listPage.add(产品规模);

        // 3.9.一级流动性备付占比
        // 变量.一级流动性备付占比 = new 流动性储备Entity
        FlowCommonDto 一级流动性备付占比 = new FlowCommonDto();
        一级流动性备付占比.setRowId(FlowConstant.NODE_一级流动性备付占比);
        一级流动性备付占比.setfRowId(FlowConstant.NODE_ROOT);
        一级流动性备付占比.setLeaf(true);
        一级流动性备付占比.setRowName("一级流动性备付占比");
        // 一级流动性备付占比 = 一级流动性备付合计/产品规模 保留4位小数
        一级流动性备付占比.setRrsBalance(
            ArithmeticUtil.divide(firstFlowReverseDto.getRrsBalance(), 产品规模.getRrsBalance(), 4));
        一级流动性备付占比.setYysBalance(
            ArithmeticUtil.divide(firstFlowReverseDto.getYysBalance(), 产品规模.getYysBalance(), 4));
        一级流动性备付占比.setXxxrBalance(
            ArithmeticUtil.divide(firstFlowReverseDto.getXxxrBalance(), 产品规模.getXxxrBalance(), 4));
        一级流动性备付占比.setXxxrBBalance(ArithmeticUtil.divide(firstFlowReverseDto.getXxxrBBalance(),
            产品规模.getXxxrBBalance(), 4));
        一级流动性备付占比.setLcbBalance(
            ArithmeticUtil.divide(firstFlowReverseDto.getLcbBalance(), 产品规模.getLcbBalance(), 4));
        一级流动性备付占比.setDkNetSum(
            ArithmeticUtil.divide(firstFlowReverseDto.getDkNetSum(), 产品规模.getDkNetSum(), 4));
        一级流动性备付占比.setTotalBalance(ArithmeticUtil.divide(firstFlowReverseDto.getTotalBalance(),
            产品规模.getTotalBalance(), 4));
        listPage.add(一级流动性备付占比);

        // 3.10.二级流动性备付占比
        // 变量.二级流动性备付占比 = new 流动性储备Entity
        FlowCommonDto 二级流动性备付占比 = new FlowCommonDto();
        二级流动性备付占比.setRowId(FlowConstant.NODE_二级流动性备付占比);
        二级流动性备付占比.setfRowId(FlowConstant.NODE_ROOT);
        二级流动性备付占比.setLeaf(true);
        二级流动性备付占比.setRowName("二级流动性备付占比");
        // 二级流动性备付占比 = 二级流动性备付合计/产品规模 保留4位小数
        二级流动性备付占比.setRrsBalance(
            ArithmeticUtil.divide(secondFlowReverseDto.getRrsBalance(), 产品规模.getRrsBalance(), 4));
        二级流动性备付占比.setYysBalance(
            ArithmeticUtil.divide(secondFlowReverseDto.getYysBalance(), 产品规模.getYysBalance(), 4));
        二级流动性备付占比.setXxxrBalance(ArithmeticUtil.divide(secondFlowReverseDto.getXxxrBalance(),
            产品规模.getXxxrBalance(), 4));
        二级流动性备付占比.setXxxrBBalance(ArithmeticUtil.divide(secondFlowReverseDto.getXxxrBBalance(),
            产品规模.getXxxrBBalance(), 4));
        二级流动性备付占比.setLcbBalance(
            ArithmeticUtil.divide(secondFlowReverseDto.getLcbBalance(), 产品规模.getLcbBalance(), 4));
        二级流动性备付占比.setDkNetSum(
            ArithmeticUtil.divide(secondFlowReverseDto.getDkNetSum(), 产品规模.getDkNetSum(), 4));
        二级流动性备付占比.setTotalBalance(ArithmeticUtil.divide(secondFlowReverseDto.getTotalBalance(),
            产品规模.getTotalBalance(), 4));
        listPage.add(二级流动性备付占比);

        return super.queryForTree(() -> listPage, FlowCommonDto::getRowId,
            FlowCommonDto::getfRowId, FlowCommonDto::getRowId, (item) -> true);
    }

    /**
     * 4.流动性预测模块查询
     * 
     * @param page page
     * @param length length
     * @param map map
     * @return Page类 Page
     * @throws Exception 异常信息
     */
    public Page<FlowCommonDto> selectForcast(int page, int length, Map<String, Object> map)
    {
        // 定义变量.查询时间
        String queryDateStr = (String)map.get("queryDate");
        // 定义 变量.返回结果List
        List<FlowCommonDto> listPage = new ArrayList<FlowCommonDto>();
        Date queryDate = DateUtil.parse(queryDateStr);
        Map<String, Object> params = new HashMap<>();
        params.put("queryDate", queryDate);
        params.put("holdingType", DictCodeList.持仓类型_实际.getValue());
        params.put("listFinprodId", this.getAllFinprodIds());

        // 获取流动性种类为流动性预测的数据，封装
        Map<String, FlowForecastConfig> flowForecastConfigMap = this.getFlowForecastConfigMap(
            queryDateStr, DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue());

        // 4.1.变量.资产到期回款-非标资产Entity = new 流动性预测Entity
        String key = DateUtil.format(queryDate, "YYYYMMDD")
                     + DictCodeListExt2.流动性预测维护_部门类型_投融资处.getValue()
                     + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
                     + DictCodeListExt2.流动性预测维护_指标种类_非标资产.getValue();
        FlowCommonDto 资产到期回款_非标资产Entity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_非标资产, FlowConstant.NODE_资金到期回款, "非标资产");
        listPage.add(资产到期回款_非标资产Entity);

        // 4.2.资金总流入-资产到期回款-其他
        key = DateUtil.format(queryDate, "YYYYMMDD")
              + DictCodeListExt2.流动性预测维护_部门类型_组合投资处.getValue()
              + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
              + DictCodeListExt2.流动性预测维护_指标种类_资产到期回款_其他.getValue();
        FlowCommonDto 资产到期回款_其他Entity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_其他, FlowConstant.NODE_资金到期回款, "其他");
        listPage.add(资产到期回款_其他Entity);

        // 4.3.资金总流入-资产到期回款-逆回购到期、资金总流出-正回购到期
        List<DateAreaDto> listAreaDto = this.setDateAreas(queryDate);
        // 将日期区间放入params参数中
        for (DateAreaDto item : listAreaDto) {
            params.put("area" + (listAreaDto.indexOf(item) + 1), item.getDateArea());
            params.put("area" + (listAreaDto.indexOf(item) + 1) + "BeginDate",
                item.getBeginDate());
            params.put("area" + (listAreaDto.indexOf(item) + 1) + "EndDate", item.getEndDate());
        }
        params.put("finprodType2_1", DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
        params.put("finprodType2_2", DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue());
        params.put("finprodType2_3", DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
        params.put("finprodType2_4", DictCodeListExt1.金融产品类型_债券买断式正回购.getValue());
        // 正回购，用于返回数据分组
        params.put("finprodTypeRepo", FlowConstant.FINPROD_TYPE_REPO);
        // 逆回购，用于返回数据分组
        params.put("finprodTypeRepoN", FlowConstant.FINPROD_TYPE_REPON);
        params.put("holdingType_SJ", DictCodeList.持仓类型_实际.getValue());
        params.put("holdingType_ZY", DictCodeList.持仓类型_质押.getValue());
        params.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // 查询 资金总流入-资产到期回款-逆回购到期、资金总流出-正回购到期 数据
        List<FlowQueryDataDto> listFunds = flowForecastReportMapper.getRepoMaturityFunds(params);
        // 定义key返回方法
        Function<FlowQueryDataDto, String> keyMapper = item -> {
            // 查询结果.Get(类型 + 区间)
            return item.getAreaIndex() + item.getType();
        };
        // 封装listFunds
        Map<String, FlowQueryDataDto> fundsMap = this.getMapFromList(listFunds, keyMapper);

        // 变量.逆回购到期Entity
        FlowCommonDto 逆回购到期Entity = this.getEntityFromMapByDateArea(fundsMap,
            FlowConstant.FINPROD_TYPE_REPON, FlowConstant.NODE_逆回购到期, FlowConstant.NODE_资金到期回款,
            "逆回购到期");
        listPage.add(逆回购到期Entity);

        // 变量.正回购到期Entity
        FlowCommonDto 正回购到期Entity = this.getEntityFromMapByDateArea(fundsMap,
            FlowConstant.FINPROD_TYPE_REPO, FlowConstant.NODE_正回购到期, FlowConstant.NODE_资金总流出,
            "正回购到期");
        listPage.add(正回购到期Entity);

        // 4.4.资金总流入-资产到期回款-自主投资债券
        params.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());
        // 2019年9月5日09:27:28 魏鹏：确认后，投管存现金流信息表中债券现在存01
//        params.put("finprodType", DictCodeListExt2.回购标的类别_债券.getValue());
        params.put("finprodType", FlowConstant.FINPROD_TYPE_SEC);
        List<FlowQueryDataDto> listSelfInvSecPredict = flowForecastReportMapper.getSelfInvSecPredict(
            params);
        // 变量.自主投资债券还本Entity = new 流动性预测Entity
        FlowCommonDto 自主投资债券Entity = new FlowCommonDto();
        自主投资债券Entity.setRowId(FlowConstant.NODE_自主投资债券);
        自主投资债券Entity.setfRowId(FlowConstant.NODE_资金到期回款);
        自主投资债券Entity.setLeaf(true);
        自主投资债券Entity.setRowName("自主投资债券");
        if (CollectionUtils.isNotEmpty(listSelfInvSecPredict)) {
            // 流动性预测维护listSelfInvSecPredict.get(0).指标值1
            自主投资债券Entity.setTargetTimeone(listSelfInvSecPredict.get(0).getTargetTimeone());
            // 流动性预测维护listSelfInvSecPredict.get(0).指标值2
            自主投资债券Entity.setTargetTimetwo(listSelfInvSecPredict.get(0).getTargetTimetwo());
            // 流动性预测维护listSelfInvSecPredict.get(0).指标值3
            自主投资债券Entity.setTargetTimethree(listSelfInvSecPredict.get(0).getTargetTimethree());
            // 流动性预测维护listSelfInvSecPredict.get(0).指标值4
            自主投资债券Entity.setTargetTimefour(listSelfInvSecPredict.get(0).getTargetTimefour());
            // 流动性预测维护listSelfInvSecPredict.get(0).指标值5
            自主投资债券Entity.setTargetTimefive(listSelfInvSecPredict.get(0).getTargetTimefive());
            // 流动性预测维护listSelfInvSecPredict.get(0).指标值6
            自主投资债券Entity.setTargetTimesix(listSelfInvSecPredict.get(0).getTargetTimesix());
            // 合计在get方法中写好了
        }
        listPage.add(自主投资债券Entity);

        // 4.5.资金总流入-资产到期回款-合计
        // 变量.资产到期回款合计Entity = new 流动性预测Entity
        FlowCommonDto 资金到期回款Entity = new FlowCommonDto();
        // 定义 变量.待汇总List
        List<FlowCommonDto> listCollectDto = new ArrayList<>();
        // 余额 = 非标资产 + 逆回购 + 自主投资债券 + 其它汇总
        listCollectDto.add(资产到期回款_非标资产Entity);
        listCollectDto.add(逆回购到期Entity);
        listCollectDto.add(自主投资债券Entity);
        listCollectDto.add(资产到期回款_其他Entity);
        资金到期回款Entity = this.doCollect(listCollectDto, FlowConstant.NODE_资金到期回款,
            FlowConstant.NODE_资金总流入, "资金到期回款");
        listPage.add(资金到期回款Entity);

        // 4.6.资金总流入-开放式产品净申赎预测
        // 变量.日日升Entity = new 流动性预测Entity
        key = DateUtil.format(queryDate, "YYYYMMDD")
              + DictCodeListExt2.流动性预测维护_部门类型_产品管理处.getValue()
              + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
              + DictCodeListExt2.流动性预测维护_指标种类_日日升.getValue();
        FlowCommonDto 日日升Entity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_开放式产品净申赎预测_日日升, FlowConstant.NODE_开放式产品净申赎预测, "日日升");
        listPage.add(日日升Entity);

        // 变量.月月升Entity = new 流动性预测Entity
        key = DateUtil.format(queryDate, "YYYYMMDD")
              + DictCodeListExt2.流动性预测维护_部门类型_产品管理处.getValue()
              + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
              + DictCodeListExt2.流动性预测维护_指标种类_月月升.getValue();
        FlowCommonDto 月月升Entity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_开放式产品净申赎预测_月月升, FlowConstant.NODE_开放式产品净申赎预测, "月月升");
        listPage.add(月月升Entity);

        // 变量.鑫鑫向荣Entity = new 流动性预测Entity
        key = DateUtil.format(queryDate, "YYYYMMDD")
              + DictCodeListExt2.流动性预测维护_部门类型_产品管理处.getValue()
              + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
              + DictCodeListExt2.流动性预测维护_指标种类_鑫鑫向荣.getValue();
        FlowCommonDto 鑫鑫向荣Entity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_开放式产品净申赎预测_鑫鑫向荣, FlowConstant.NODE_开放式产品净申赎预测, "鑫鑫向荣");
        listPage.add(鑫鑫向荣Entity);

        // 变量.鑫鑫向荣BEntity = new 流动性预测Entity
        key = DateUtil.format(queryDate, "YYYYMMDD")
              + DictCodeListExt2.流动性预测维护_部门类型_产品管理处.getValue()
              + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
              + DictCodeListExt2.流动性预测维护_指标种类_鑫鑫向荣B.getValue();
        FlowCommonDto 鑫鑫向荣BEntity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_开放式产品净申赎预测_鑫鑫向荣B, FlowConstant.NODE_开放式产品净申赎预测, "鑫鑫向荣B");
        listPage.add(鑫鑫向荣BEntity);

        // 变量.理财宝Entity = new 流动性预测Entity
        key = DateUtil.format(queryDate, "YYYYMMDD")
              + DictCodeListExt2.流动性预测维护_部门类型_产品管理处.getValue()
              + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
              + DictCodeListExt2.流动性预测维护_指标种类_理财宝.getValue();
        FlowCommonDto 理财宝Entity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_开放式产品净申赎预测_理财宝, FlowConstant.NODE_开放式产品净申赎预测, "理财宝");
        listPage.add(理财宝Entity);

        // 变量.定开净值型产品Entity = new 流动性预测Entity
        key = DateUtil.format(queryDate, "YYYYMMDD")
              + DictCodeListExt2.流动性预测维护_部门类型_产品管理处.getValue()
              + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
              + DictCodeListExt2.流动性预测维护_指标种类_净值型产品开放期净申赎.getValue();
        FlowCommonDto 定开净值型产品Entity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_净值型产品开放期净申赎, FlowConstant.NODE_开放式产品净申赎预测, "净值型产品开放期净申赎");
        listPage.add(定开净值型产品Entity);

        // 变量.开放式产品净申赎预测Entity = new 流动性预测Entity
        FlowCommonDto 开放式产品净申赎预测Entity = new FlowCommonDto();
        // 变量.待汇总List清空初始化
        listCollectDto.clear();
        // 余额 = 日日升/月月升/鑫鑫向荣/鑫鑫向荣B款/理财宝/定开净值型汇总
        listCollectDto.add(日日升Entity);
        listCollectDto.add(月月升Entity);
        listCollectDto.add(鑫鑫向荣Entity);
        listCollectDto.add(鑫鑫向荣BEntity);
        listCollectDto.add(理财宝Entity);
        listCollectDto.add(定开净值型产品Entity);
        开放式产品净申赎预测Entity = this.doCollect(listCollectDto, FlowConstant.NODE_开放式产品净申赎预测,
            FlowConstant.NODE_资金总流入, "开放式产品净申赎预测");
        listPage.add(开放式产品净申赎预测Entity);

        // 4.7.资金总流入-期次型/定开净值型产品募集预测
        // 变量.个人期次型产品Entity = new 流动性预测Entity
        key = DateUtil.format(queryDate, "YYYYMMDD")
              + DictCodeListExt2.流动性预测维护_部门类型_产品管理处.getValue()
              + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
              + DictCodeListExt2.流动性预测维护_指标种类_个人期次型产品.getValue();
        FlowCommonDto 个人期次型产品Entity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_个人期次型产品, FlowConstant.NODE_期次型定开净值型产品募集预测, "个人期次型产品");
        listPage.add(个人期次型产品Entity);

        // 变量.净值型产品（新发）Entity = new 流动性预测Entity
        key = DateUtil.format(queryDate, "YYYYMMDD")
              + DictCodeListExt2.流动性预测维护_部门类型_产品管理处.getValue()
              + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
              + DictCodeListExt2.流动性预测维护_指标种类_净值型产品_新发.getValue();
        FlowCommonDto 净值型产品_新发Entity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_净值型产品_新发, FlowConstant.NODE_期次型定开净值型产品募集预测, "净值型产品（新发）");
        listPage.add(净值型产品_新发Entity);

        // 变量.机构期次型产品Entity = new 流动性预测Entity
        key = DateUtil.format(queryDate, "YYYYMMDD")
              + DictCodeListExt2.流动性预测维护_部门类型_产品管理处.getValue()
              + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
              + DictCodeListExt2.流动性预测维护_指标种类_机构期次型产品.getValue();
        FlowCommonDto 机构期次型产品Entity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_机构期次型产品, FlowConstant.NODE_期次型定开净值型产品募集预测, "机构期次型产品");
        listPage.add(机构期次型产品Entity);

        // 变量.期次型定开净值型产品募集预测Entity = new 流动性预测Entity
        FlowCommonDto 期次型定开净值型产品募集预测Entity = new FlowCommonDto();
        // 变量.待汇总List清空初始化
        listCollectDto.clear();
        // 余额 = 日日升/月月升/鑫鑫向荣/鑫鑫向荣B款/理财宝/定开净值型汇总
        listCollectDto.add(个人期次型产品Entity);
        listCollectDto.add(机构期次型产品Entity);
        listCollectDto.add(净值型产品_新发Entity);
        期次型定开净值型产品募集预测Entity = this.doCollect(listCollectDto, FlowConstant.NODE_期次型定开净值型产品募集预测,
            FlowConstant.NODE_资金总流入, "期次型/定开净值型产品募集预测");
        listPage.add(期次型定开净值型产品募集预测Entity);

        // 4.9.资金总流入-合计
        // 变量.资金总流入Entity = new 流动性预测Entity
        FlowCommonDto 资金总流入Entity = new FlowCommonDto();
        // 变量.待汇总List清空初始化
        listCollectDto.clear();
        // 汇总 资产到期回款合计Entity 开放式产品净申赎预测合计Entity 期次定开产品净申赎预测合计Entity
        listCollectDto.add(资金到期回款Entity);
        listCollectDto.add(开放式产品净申赎预测Entity);
        listCollectDto.add(期次型定开净值型产品募集预测Entity);
        资金总流入Entity = this.doCollect(listCollectDto, FlowConstant.NODE_资金总流入,
            FlowConstant.NODE_ROOT, "资金总流入");
        listPage.add(资金总流入Entity);

        // 4.10.资金总流出-封闭式产品到期
        // 查询 资金总流出-封闭式产品到期
        params.put("listClosedFinprodId", this.getFinprodIds(DictCodeList.开放类型_封闭式.getValue()));
        params.put("operationType", DictCodeList.开放类型_封闭式.getValue());
        params.put("invType_GR", FlowConstant.目标客户类型_个人);
        params.put("invType_GR_1", DictCodeList.目标客户类型_一般个人客户.getValue());
        params.put("invType_GR_2", DictCodeList.目标客户类型_私人银行客户专属.getValue());
        params.put("invType_GR_3", DictCodeList.目标客户类型_高资产净值客户.getValue());
        params.put("invType_JG", FlowConstant.目标客户类型_机构);
        params.put("invType_JG_1", DictCodeList.目标客户类型_金融同业专属.getValue());
        params.put("invType_JG_2", DictCodeList.目标客户类型_机构客户专属.getValue());
        List<FlowQueryDataDto> closedProMaturity = flowForecastReportMapper.getClosedProMaturity(
            params);
        // 定义key返回方法
        keyMapper = item -> {
            // 查询结果.Get(类型 + 区间)
            return item.getAreaIndex() + item.getType();
        };
        // 封装listFunds
        Map<String, FlowQueryDataDto> closedProMaturityMap = this.getMapFromList(closedProMaturity,
            keyMapper);
        // 变量.封闭式产品到期-个人Entity = new 流动性预测Entity
        // IF(目标客户类型 IN ([私人银行客户专属],[一般个人客户],[高资产净值客户]) -- sql中已合并
        FlowCommonDto 封闭式产品到期_个人Entity = this.getEntityFromMapByDateArea(closedProMaturityMap,
            FlowConstant.目标客户类型_个人, FlowConstant.NODE_封闭式产品到期_个人, FlowConstant.NODE_封闭式产品到期, "个人");
        listPage.add(封闭式产品到期_个人Entity);

        // 变量.封闭式产品到期-机构Entity
        // ELSE IF(目标客户类型 IN ([银行同业专属],[机构客户专属]) -- sql中已合并
        FlowCommonDto 封闭式产品到期_机构Entity = this.getEntityFromMapByDateArea(closedProMaturityMap,
            FlowConstant.目标客户类型_机构, FlowConstant.NODE_封闭式产品到期_机构, FlowConstant.NODE_封闭式产品到期, "机构");
        listPage.add(封闭式产品到期_机构Entity);

        // 4.11.资金总流出-封闭式产品到期-合计
        // 变量.封闭式产品到期-合计Entity = new 流动性预测Entity
        FlowCommonDto 封闭式产品到期Entity = new FlowCommonDto();
        // 变量.待汇总List清空初始化
        listCollectDto.clear();
        // 封闭式产品到期-合计Entity = 封闭式产品到期-个人Entity+封闭式产品到期-机构Entity
        listCollectDto.add(封闭式产品到期_个人Entity);
        listCollectDto.add(封闭式产品到期_机构Entity);
        封闭式产品到期Entity = this.doCollect(listCollectDto, FlowConstant.NODE_封闭式产品到期,
            FlowConstant.NODE_资金总流出, "封闭式产品到期");
        listPage.add(封闭式产品到期Entity);

        // 4.11.资金总流出-合计
        // 变量.资金总流出-合计Entity = new 流动性预测Entity
        FlowCommonDto 资金总流出Entity = new FlowCommonDto();
        // 变量.待汇总List清空初始化
        listCollectDto.clear();
        // 资金总流出-合计Entity = 正回购到期Entity + 封闭式产品到期Entity
        listCollectDto.add(正回购到期Entity);
        listCollectDto.add(封闭式产品到期Entity);
        资金总流出Entity = this.doCollect(listCollectDto, FlowConstant.NODE_资金总流出,
            FlowConstant.NODE_ROOT, "资金总流出");
        listPage.add(资金总流出Entity);

        // 4.12.资金总流出-资金盈余/缺口-合计
        // 变量.资金盈余/缺口-合计Entity = new 流动性预测Entity
        FlowCommonDto 资金盈余缺口Entity = new FlowCommonDto();
        资金盈余缺口Entity.setRowId(FlowConstant.NODE_资金盈余缺口);
        资金盈余缺口Entity.setfRowId(FlowConstant.NODE_ROOT);
        资金盈余缺口Entity.setLeaf(true);
        资金盈余缺口Entity.setRowName("资金盈余/缺口");
        // Entity.指标值=（资金总流入-合计）-（资金总流出-合计）
        // 指标1 = （资金总流入-合计）-（资金总流出-合计）
        资金盈余缺口Entity.setTargetTimeone(ArithmeticUtil.subtract(资金总流入Entity.getTargetTimeone(),
            资金总流出Entity.getTargetTimeone()));
        // 指标2 = 同上
        资金盈余缺口Entity.setTargetTimetwo(ArithmeticUtil.subtract(资金总流入Entity.getTargetTimetwo(),
            资金总流出Entity.getTargetTimetwo()));
        // 指标3 = 同上
        资金盈余缺口Entity.setTargetTimethree(ArithmeticUtil.subtract(资金总流入Entity.getTargetTimethree(),
            资金总流出Entity.getTargetTimethree()));
        // 指标4 = 同上
        资金盈余缺口Entity.setTargetTimefour(ArithmeticUtil.subtract(资金总流入Entity.getTargetTimefour(),
            资金总流出Entity.getTargetTimefour()));
        // 指标5 = 同上
        资金盈余缺口Entity.setTargetTimefive(ArithmeticUtil.subtract(资金总流入Entity.getTargetTimefive(),
            资金总流出Entity.getTargetTimefive()));
        // 指标6 = 同上
        资金盈余缺口Entity.setTargetTimesix(ArithmeticUtil.subtract(资金总流入Entity.getTargetTimesix(),
            资金总流出Entity.getTargetTimesix()));
        listPage.add(资金盈余缺口Entity);

        // 4.13.新增走款安排
        // 新增走款安排-投融资处/新增走款安排-固收处/新增走款安排-组合投资处
        // 变量.新增走款安排-投融资处Entity = new 流动性预测Entity
        key = DateUtil.format(queryDate, "YYYYMMDD")
              + DictCodeListExt2.流动性预测维护_部门类型_投融资处.getValue()
              + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
              + DictCodeListExt2.流动性预测维护_指标种类_投融资处.getValue();
        FlowCommonDto 新增走款安排_投融资处Entity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_投融资处, FlowConstant.NODE_新增走款安排, "投融资处");
        listPage.add(新增走款安排_投融资处Entity);

        // 变量.新增走款安排-固收处Entity = new 流动性预测Entity
        key = DateUtil.format(queryDate, "YYYYMMDD")
              + DictCodeListExt2.流动性预测维护_部门类型_固收处.getValue()
              + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
              + DictCodeListExt2.流动性预测维护_指标种类_新增走款安排_固收处.getValue();
        FlowCommonDto 新增走款安排_固收处Entity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_固收处, FlowConstant.NODE_新增走款安排, "固收处");
        listPage.add(新增走款安排_固收处Entity);

        // 变量.新增走款安排-组合投资处Entity = new 流动性预测Entity
        key = DateUtil.format(queryDate, "YYYYMMDD")
              + DictCodeListExt2.流动性预测维护_部门类型_组合投资处.getValue()
              + DictCodeListExt2.流动性预测维护_流动性种类_流动性预测.getValue()
              + DictCodeListExt2.流动性预测维护_指标种类_新增走款安排_组合投资处.getValue();
        FlowCommonDto 新增走款安排_组合投资处Entity = this.getEntityFromForcastMap(flowForecastConfigMap, key,
            FlowConstant.NODE_组合投资处, FlowConstant.NODE_新增走款安排, "组合投资处");
        listPage.add(新增走款安排_组合投资处Entity);

        // 变量.新增走款安排-合计Entity = new 流动性预测Entity
        FlowCommonDto 新增走款安排Entity = new FlowCommonDto();
        // 变量.待汇总List清空初始化
        listCollectDto.clear();
        // Entity.指标值=（资金总流出-新增走款安排-投融资处）+（资金总流出-新增走款安排-固收处）+（资金总流出-新增走款安排-组合投资处）
        listCollectDto.add(新增走款安排_投融资处Entity);
        listCollectDto.add(新增走款安排_固收处Entity);
        listCollectDto.add(新增走款安排_组合投资处Entity);
        新增走款安排Entity = this.doCollect(listCollectDto, FlowConstant.NODE_新增走款安排,
            FlowConstant.NODE_ROOT, "新增走款安排");
        listPage.add(新增走款安排Entity);

        // 4.15.走款后资金盈余/缺口-合计
        // 变量.走款后资金盈余/缺口Entity = new 流动性预测Entity
        FlowCommonDto 走款后资金盈余缺口Entity = new FlowCommonDto();
        走款后资金盈余缺口Entity.setRowId(FlowConstant.NODE_走款后资金盈余缺口);
        走款后资金盈余缺口Entity.setfRowId(FlowConstant.NODE_ROOT);
        走款后资金盈余缺口Entity.setLeaf(true);
        走款后资金盈余缺口Entity.setRowName("走款后资金盈余/缺口");
        // Entity.指标值=（资金盈余/缺口-合计）-（新增走款安排-合计）
        // 指标1 = （资金总流入-合计）-（资金总流出-合计）
        走款后资金盈余缺口Entity.setTargetTimeone(ArithmeticUtil.subtract(资金盈余缺口Entity.getTargetTimeone(),
            新增走款安排Entity.getTargetTimeone()));
        // 指标2 = 同上
        走款后资金盈余缺口Entity.setTargetTimetwo(ArithmeticUtil.subtract(资金盈余缺口Entity.getTargetTimetwo(),
            新增走款安排Entity.getTargetTimetwo()));
        // 指标3 = 同上
        走款后资金盈余缺口Entity.setTargetTimethree(ArithmeticUtil.subtract(
            资金盈余缺口Entity.getTargetTimethree(), 新增走款安排Entity.getTargetTimethree()));
        // 指标4 = 同上
        走款后资金盈余缺口Entity.setTargetTimefour(ArithmeticUtil.subtract(资金盈余缺口Entity.getTargetTimefour(),
            新增走款安排Entity.getTargetTimefour()));
        // 指标5 = 同上
        走款后资金盈余缺口Entity.setTargetTimefive(ArithmeticUtil.subtract(资金盈余缺口Entity.getTargetTimefive(),
            新增走款安排Entity.getTargetTimefive()));
        // 指标6 = 同上
        走款后资金盈余缺口Entity.setTargetTimesix(ArithmeticUtil.subtract(资金盈余缺口Entity.getTargetTimesix(),
            新增走款安排Entity.getTargetTimesix()));
        listPage.add(走款后资金盈余缺口Entity);

        return super.queryForTree(() -> listPage, FlowCommonDto::getRowId,
            FlowCommonDto::getfRowId, FlowCommonDto::getRowId, (item) -> true);
    }

    /**
     * 获取流动性储备/预测信息
     * 
     * @param queryDateStr 查询日期
     * @param flowType 流动种类
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, FlowForecastConfig> getFlowForecastConfigMap(String queryDateStr,
                                                                    String flowType) {
        // 定义 变量.返回结果Map
        Map<String, FlowForecastConfig> configMap = new HashMap<>();
        // 查询流动性信息
        List<FlowForecastConfig> listFlowForecastConfig = flowForecastReportMapper.getConfigsByDate(
            queryDateStr, flowType);
        if (CollectionUtils.isNotEmpty(listFlowForecastConfig)) {
            // 以FlowForecastConfig主键作为configMap的主键，放入map中
            listFlowForecastConfig.forEach(item -> {
                // *KEY可以使用拼接STRING：查询日期+","+部门类型+","+流动性种类+","+指标种类
                String key = DateUtil.format(item.getQueryDate(), "YYYYMMDD") + item.getDepType()
                             + item.getFlowType() + item.getTargetType();
                // 放入configMap中
                configMap.put(key, item);
            });
        }
        return configMap;
    }

    /**
     * 将list集合根据key成封装map
     * 
     * @param listFlowReserveRepo
     * @param keyMapper
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, FlowQueryDataDto> getMapFromList(List<FlowQueryDataDto> listFlowReserveRepo,
                                                        Function<FlowQueryDataDto, String> keyMapper) {
        Map<String, FlowQueryDataDto> result = new HashMap<>();
        if (CollectionUtils.isNotEmpty(listFlowReserveRepo)) {
            // 以参数keyMapper作为key值
            result = listFlowReserveRepo.stream().collect(
                Collectors.toMap(keyMapper, p -> p, (key1, key2) -> key1));
        }
        return result;
    }

    /**
     * 通过finprodId区分，+剩下key值作为key从map获取数据，返回entity
     * 
     * @param map 参数map
     * @param nextkeyValue 待拼接key值，没有，则传空字符串""
     * @param rowId 节点rowId
     * @param fRowId 父级rowId
     * @param isleaf 是否叶子节点
     * @param rowName 行名称
     * @return Entity
     * @see
     * @since 1.0
     */
    public FlowCommonDto getEntityFromMapById(Map<String, FlowQueryDataDto> map,
                                              String nextKeyValue, String rowId, String fRowId,
                                              String rowName) {
        FlowCommonDto resultEntity = new FlowCommonDto();
        resultEntity.setRowId(rowId);
        resultEntity.setfRowId(fRowId);
        // 调取该方法肯定为叶子节点
        resultEntity.setLeaf(true);
        resultEntity.setRowName(rowName);
        // 日日升余额 = 查询结果.Get(产品代码=日日升+拼接key值)
        if (map.get(FlowConstant.FINPROD_ID_RRS + nextKeyValue) != null) {
            resultEntity.setRrsBalance(
                map.get(FlowConstant.FINPROD_ID_RRS + nextKeyValue).getAmount());
        }
        // 月月升余额 = 查询结果.Get(产品代码=月月升+拼接key值)
        if (map.get(FlowConstant.FINPROD_ID_YYS + nextKeyValue) != null) {
            resultEntity.setYysBalance(
                map.get(FlowConstant.FINPROD_ID_YYS + nextKeyValue).getAmount());
        }
        // 鑫鑫向荣余额 = 查询结果.Get(产品代码=鑫鑫向荣+拼接key值)
        if (map.get(FlowConstant.FINPROD_ID_XXXR + nextKeyValue) != null) {
            resultEntity.setXxxrBalance(
                map.get(FlowConstant.FINPROD_ID_XXXR + nextKeyValue).getAmount());
        }
        // 鑫鑫向荣B款余额 = 查询结果.Get(产品代码=鑫鑫向荣B款+拼接key值)
        if (map.get(FlowConstant.FINPROD_ID_XXXR_B + nextKeyValue) != null) {
            resultEntity.setXxxrBBalance(
                map.get(FlowConstant.FINPROD_ID_XXXR_B + nextKeyValue).getAmount());
        }
        // 理财宝余额 = 查询结果.Get(产品代码=理财宝&&类型+拼接key值)
        if (map.get(FlowConstant.FINPROD_ID_LCB + nextKeyValue) != null) {
            resultEntity.setLcbBalance(
                map.get(FlowConstant.FINPROD_ID_LCB + nextKeyValue).getAmount());
        }
        // 定开净值型汇总 = 查询结果.Get(产品代码=其他+拼接key值)
        if (map.get(FlowConstant.FINPROD_ID_QT + nextKeyValue) != null) {
            resultEntity.setDkNetSum(
                map.get(FlowConstant.FINPROD_ID_QT + nextKeyValue).getAmount());
        }
        // 合计 = 上面余额合计
        resultEntity.setTotalBalance(
            ArithmeticUtil.doAdd(resultEntity.getRrsBalance(), resultEntity.getYysBalance(),
                resultEntity.getXxxrBalance(), resultEntity.getXxxrBBalance(),
                resultEntity.getLcbBalance(), resultEntity.getDkNetSum()));
        return resultEntity;
    }

    /**
     * 通过key值作为key从map获取数据，返回entity
     * 
     * @param map 参数map
     * @param nextkeyValue 待拼接key值，没有，则传空字符串""
     * @param rowId 节点rowId
     * @param fRowId 父级rowId
     * @param isleaf 是否叶子节点
     * @param rowName 行名称
     * @return Entity
     * @see
     * @since 1.0
     */
    public FlowCommonDto getEntityFromForcastMap(Map<String, FlowForecastConfig> map, String key,
                                                 String rowId, String fRowId, String rowName) {
        FlowCommonDto resultEntity = new FlowCommonDto();
        resultEntity.setRowId(rowId);
        resultEntity.setfRowId(fRowId);
        // 调取该方法肯定为叶子节点
        resultEntity.setLeaf(true);
        resultEntity.setRowName(rowName);
        // 流动性预测维护Map.Get(KEY-非标到期).指标值1
        if (map.get(key) != null) {
            // 流动性预测维护Map.Get(KEY).指标值1
            resultEntity.setTargetTimeone(map.get(key).getTargetTimeone());
            // 流动性预测维护Map.Get(KEY).指标值2
            resultEntity.setTargetTimetwo(map.get(key).getTargetTimetwo());
            // 流动性预测维护Map.Get(KEY).指标值3
            resultEntity.setTargetTimethree(map.get(key).getTargetTimethree());
            // 流动性预测维护Map.Get(KEY).指标值4
            resultEntity.setTargetTimefour(map.get(key).getTargetTimefour());
            // 流动性预测维护Map.Get(KEY).指标值5
            resultEntity.setTargetTimefive(map.get(key).getTargetTimefive());
            // 流动性预测维护Map.Get(KEY).指标值6
            resultEntity.setTargetTimesix(map.get(key).getTargetTimesix());
        }
        return resultEntity;
    }

    /**
     * 通过key（类型+日期区间）值作为key从map获取数据，返回entity
     * 
     * @param map 参数map
     * @param nextkeyValue 待拼接key值，没有，则传空字符串""
     * @param rowId 节点rowId
     * @param fRowId 父级rowId
     * @param isleaf 是否叶子节点
     * @param rowName 行名称
     * @return Entity
     * @see
     * @since 1.0
     */
    public FlowCommonDto getEntityFromMapByDateArea(Map<String, FlowQueryDataDto> map,
                                                    String nextKeyValue, String rowId,
                                                    String fRowId, String rowName) {
        FlowCommonDto resultEntity = new FlowCommonDto();
        resultEntity.setRowId(rowId);
        resultEntity.setfRowId(fRowId);
        // 调取该方法肯定为叶子节点
        resultEntity.setLeaf(true);
        resultEntity.setRowName(rowName);
        // 指标值1 = 查询结果.Get(类型=逆回购&&区间=区间1).数量
        if (map.get(FlowConstant.areaIndex_1 + nextKeyValue) != null) {
            // 指标值1 = 查询结果.Get(类型=逆回购&&区间=区间1).数量
            resultEntity.setTargetTimeone(
                map.get(FlowConstant.areaIndex_1 + nextKeyValue).getAmount());
        }
        // 指标值2 = 查询结果.Get(类型=逆回购&&区间=区间2).数量
        if (map.get(FlowConstant.areaIndex_2 + nextKeyValue) != null) {
            resultEntity.setTargetTimetwo(
                map.get(FlowConstant.areaIndex_2 + nextKeyValue).getAmount());
        }
        // 指标值3 = 查询结果.Get(类型=逆回购&&区间=区间3).数量
        if (map.get(FlowConstant.areaIndex_3 + nextKeyValue) != null) {
            resultEntity.setTargetTimethree(
                map.get(FlowConstant.areaIndex_3 + nextKeyValue).getAmount());
        }
        // 指标值4 = 查询结果.Get(类型=逆回购&&区间=区间4).数量
        if (map.get(FlowConstant.areaIndex_4 + nextKeyValue) != null) {
            resultEntity.setTargetTimefour(
                map.get(FlowConstant.areaIndex_4 + nextKeyValue).getAmount());
        }
        // 指标值5 = 查询结果.Get(类型=逆回购&&区间=区间5).数量
        if (map.get(FlowConstant.areaIndex_5 + nextKeyValue) != null) {
            resultEntity.setTargetTimefive(
                map.get(FlowConstant.areaIndex_5 + nextKeyValue).getAmount());
        }
        // 指标值6 = 查询结果.Get(类型=逆回购&&区间=区间6).数量
        if (map.get(FlowConstant.areaIndex_6 + nextKeyValue) != null) {
            resultEntity.setTargetTimesix(
                map.get(FlowConstant.areaIndex_6 + nextKeyValue).getAmount());
        }
        return resultEntity;
    }

    /**
     * 汇总list取汇总值，返回entity
     * 
     * @param listCollectDto 待汇总List
     * @param rowId
     * @param fRowId
     * @param isleaf
     * @param rowName
     * @return
     * @see
     * @since 1.0
     */
    public FlowCommonDto doCollect(List<FlowCommonDto> listCollectDto, String rowId, String fRowId,
                                   String rowName) {
        FlowCommonDto collectSumDto = new FlowCommonDto();
        collectSumDto.setRowId(rowId);
        collectSumDto.setfRowId(fRowId);
        // 汇总肯定不是叶子节点，false
        collectSumDto.setLeaf(false);
        collectSumDto.setRowName(rowName);
        if (CollectionUtils.isNotEmpty(listCollectDto)) {
            for (FlowCommonDto dto : listCollectDto) {
                collectSumDto.setRrsBalance(
                    ArithmeticUtil.doAdd(collectSumDto.getRrsBalance(), dto.getRrsBalance()));
                // 月月升余额 = 同上
                collectSumDto.setYysBalance(
                    ArithmeticUtil.doAdd(collectSumDto.getYysBalance(), dto.getYysBalance()));
                // 鑫鑫向荣余额 = 同上
                collectSumDto.setXxxrBalance(
                    ArithmeticUtil.doAdd(collectSumDto.getXxxrBalance(), dto.getXxxrBalance()));
                // 鑫鑫向荣B款余额 = 同上
                collectSumDto.setXxxrBBalance(
                    ArithmeticUtil.doAdd(collectSumDto.getXxxrBBalance(), dto.getXxxrBBalance()));
                // 理财宝余额 = 同上
                collectSumDto.setLcbBalance(
                    ArithmeticUtil.doAdd(collectSumDto.getLcbBalance(), dto.getLcbBalance()));
                // 定开净值型汇总 = 同上
                collectSumDto.setDkNetSum(
                    ArithmeticUtil.doAdd(collectSumDto.getDkNetSum(), dto.getDkNetSum()));
                // 合计 = 同上
                collectSumDto.setTotalBalance(
                    ArithmeticUtil.doAdd(collectSumDto.getTotalBalance(), dto.getTotalBalance()));
                // 指标1 = 同上
                collectSumDto.setTargetTimeone(ArithmeticUtil.doAdd(
                    collectSumDto.getTargetTimeone(), dto.getTargetTimeone()));
                // 指标2 = 同上
                collectSumDto.setTargetTimetwo(ArithmeticUtil.doAdd(
                    collectSumDto.getTargetTimetwo(), dto.getTargetTimetwo()));
                // 指标3 = 同上
                collectSumDto.setTargetTimethree(ArithmeticUtil.doAdd(
                    collectSumDto.getTargetTimethree(), dto.getTargetTimethree()));
                // 指标4 = 同上
                collectSumDto.setTargetTimefour(ArithmeticUtil.doAdd(
                    collectSumDto.getTargetTimefour(), dto.getTargetTimefour()));
                // 指标5 = 同上
                collectSumDto.setTargetTimefive(ArithmeticUtil.doAdd(
                    collectSumDto.getTargetTimefive(), dto.getTargetTimefive()));
                // 指标6 = 同上
                collectSumDto.setTargetTimesix(ArithmeticUtil.doAdd(
                    collectSumDto.getTargetTimesix(), dto.getTargetTimesix()));
            }
        }
        return collectSumDto;
    }

    /**
     * 汇总list取减汇总值，返回entity
     * 
     * @param listCollectDto 待汇总List
     * @param rowId
     * @param fRowId
     * @param isleaf
     * @param rowName
     * @return
     * @see
     * @since 1.0
     */
    public FlowCommonDto doSubCollect(FlowCommonDto dto1, FlowCommonDto dto2, String rowId,
                                      String fRowId, String rowName) {
        FlowCommonDto collectSumDto = new FlowCommonDto();
        collectSumDto.setRowId(rowId);
        collectSumDto.setfRowId(fRowId);
        // 汇总肯定不是叶子节点，false
        collectSumDto.setLeaf(false);
        collectSumDto.setRowName(rowName);
        collectSumDto.setRrsBalance(
            ArithmeticUtil.subtract(dto1.getRrsBalance(), dto2.getRrsBalance()));
        // 月月升余额 = 同上
        collectSumDto.setYysBalance(
            ArithmeticUtil.subtract(dto1.getYysBalance(), dto2.getYysBalance()));
        // 鑫鑫向荣余额 = 同上
        collectSumDto.setXxxrBalance(
            ArithmeticUtil.subtract(dto1.getXxxrBalance(), dto2.getXxxrBalance()));
        // 鑫鑫向荣B款余额 = 同上
        collectSumDto.setXxxrBBalance(
            ArithmeticUtil.subtract(dto1.getXxxrBBalance(), dto2.getXxxrBBalance()));
        // 理财宝余额 = 同上
        collectSumDto.setLcbBalance(
            ArithmeticUtil.subtract(dto1.getLcbBalance(), dto2.getLcbBalance()));
        // 定开净值型汇总 = 同上
        collectSumDto.setDkNetSum(ArithmeticUtil.subtract(dto1.getDkNetSum(), dto2.getDkNetSum()));
        // 合计 = 同上
        collectSumDto.setTotalBalance(
            ArithmeticUtil.subtract(dto1.getTotalBalance(), dto2.getTotalBalance()));
        // 指标1 = 同上
        collectSumDto.setTargetTimeone(
            ArithmeticUtil.subtract(dto1.getTargetTimeone(), dto2.getTargetTimeone()));
        // 指标2 = 同上
        collectSumDto.setTargetTimetwo(
            ArithmeticUtil.subtract(dto1.getTargetTimetwo(), dto2.getTargetTimetwo()));
        // 指标3 = 同上
        collectSumDto.setTargetTimethree(
            ArithmeticUtil.subtract(dto1.getTargetTimethree(), dto2.getTargetTimethree()));
        // 指标4 = 同上
        collectSumDto.setTargetTimefour(
            ArithmeticUtil.subtract(dto1.getTargetTimefour(), dto2.getTargetTimefour()));
        // 指标5 = 同上
        collectSumDto.setTargetTimefive(
            ArithmeticUtil.subtract(dto1.getTargetTimefive(), dto2.getTargetTimefive()));
        // 指标6 = 同上
        collectSumDto.setTargetTimesix(
            ArithmeticUtil.subtract(dto1.getTargetTimesix(), dto2.getTargetTimesix()));
        return collectSumDto;
    }

    /**
     * 根据查询日期获取日期区间
     * 
     * @param queryDate 查询日期
     * @return 日期区间
     * @see
     * @since 1.0
     */
    public List<DateAreaDto> setDateAreas(Date queryDate) {
        List<DateAreaDto> dateAreas = new ArrayList<>();
        // 区间1开始日 = 画面.查询日
        DateAreaDto dateAreaDto1 = new DateAreaDto();
        dateAreaDto1.setBeginDate(queryDate);
        // 区间1结束日 = 区间1开始日+6
        dateAreaDto1.setEndDate(DateUtil.addDay(dateAreaDto1.getBeginDate(), 6));
        dateAreas.add(dateAreaDto1);

        // 区间2开始日 = 区间1结束日+1
        DateAreaDto dateAreaDto2 = new DateAreaDto();
        dateAreaDto2.setBeginDate(DateUtil.addDay(dateAreaDto1.getEndDate(), 1));
        // 区间2结束日 = 区间2开始日+6
        dateAreaDto2.setEndDate(DateUtil.addDay(dateAreaDto2.getBeginDate(), 6));
        dateAreas.add(dateAreaDto2);

        // 区间3开始日 = 区间2结束日+1
        DateAreaDto dateAreaDto3 = new DateAreaDto();
        dateAreaDto3.setBeginDate(DateUtil.addDay(dateAreaDto2.getEndDate(), 1));
        // 区间3结束日 = 区间3开始日+6
        dateAreaDto3.setEndDate(DateUtil.addDay(dateAreaDto3.getBeginDate(), 6));
        dateAreas.add(dateAreaDto3);

        // 区间4开始日 = 区间3结束日+1
        DateAreaDto dateAreaDto4 = new DateAreaDto();
        dateAreaDto4.setBeginDate(DateUtil.addDay(dateAreaDto3.getEndDate(), 1));
        // IF(区间3结束日当月月末 - 区间3结束日) >=5
        if (DateUtil.compareDate(DateUtil.getLastDay(dateAreaDto3.getEndDate()),
            DateUtil.addDay(dateAreaDto3.getEndDate(), 5)) >= 0) {
            // 区间4结束日 = 区间4开始日当月月末
            dateAreaDto4.setEndDate(DateUtil.getLastDay(dateAreaDto4.getBeginDate()));
        }
        else {
            // 区间4结束日 = 区间4开始日次月月末
            Calendar calendar = Calendar.getInstance();
            // 把平台日期赋值给Calendar
            calendar.setTime(dateAreaDto4.getBeginDate());
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            // 月份+1
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
            int endday = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            // 取月末
            calendar.set(Calendar.DAY_OF_MONTH, endday);
            dateAreaDto4.setEndDate(calendar.getTime());
        }
        dateAreas.add(dateAreaDto4);

        // 区间5开始日 = 区间4结束日+1
        DateAreaDto dateAreaDto5 = new DateAreaDto();
        dateAreaDto5.setBeginDate(DateUtil.addDay(dateAreaDto4.getEndDate(), 1));
        // 区间5结束日 = 区间5开始日当月月末
        dateAreaDto5.setEndDate(DateUtil.getLastDay(dateAreaDto5.getBeginDate()));
        dateAreas.add(dateAreaDto5);

        // 区间6开始日 = 区间5结束日+1
        DateAreaDto dateAreaDto6 = new DateAreaDto();
        dateAreaDto6.setBeginDate(DateUtil.addDay(dateAreaDto5.getEndDate(), 1));
        // 区间6结束日 = 区间6开始日当月月末
        dateAreaDto6.setEndDate(DateUtil.getLastDay(dateAreaDto6.getBeginDate()));
        dateAreas.add(dateAreaDto6);

        // 区间x(1~6) = 区间x开始日(m.d) + "-" + 区间x结束日(m.d)
        for (DateAreaDto dto : dateAreas) {
            dto.setDateArea(DateUtil.format(dto.getBeginDate(), "M.dd") + "-"
                            + DateUtil.format(dto.getEndDate(), "M.dd"));
        }
        // 返回区间
        return dateAreas;
    }

    /**
     * 获取金融产品代码List
     * 
     * @return
     * @see
     * @since 1.0
     */
    public List<String> getFinprodIds(String operationType) {
        Map<String, Object> params = new HashMap<>();
        params.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());
        List<String> listFinprodId = new ArrayList<>();
        listFinprodId.add(FlowConstant.FINPROD_ID_RRS);
        listFinprodId.add(FlowConstant.FINPROD_ID_YYS);
        listFinprodId.add(FlowConstant.FINPROD_ID_XXXR);
        listFinprodId.add(FlowConstant.FINPROD_ID_XXXR_B);
        listFinprodId.add(FlowConstant.FINPROD_ID_LCB);
        params.put("listFinprodId", listFinprodId);
        if (DictCodeList.开放类型_开放式.getValue().equals(operationType)) {
            params.put("profitType", DictCodeList.收益类型_净值型.getValue());
            params.put("openMode", DictCodeList.开放模式_定期开放.getValue());
        }
        else {
            params.put("operationType", DictCodeList.开放类型_封闭式.getValue());
        }
        params.put("finprodType", DictCodeListExt1.金融产品类型_理财产品.getValue());
        return flowForecastReportMapper.getFinprodIds(params);
    }
    /**
     * 获取金融产品代码List
     * 
     * @return
     * @see
     * @since 1.0
     */
    public List<String> getAllFinprodIds() {
        return flowForecastReportMapper.getAllFinprodIds(DictCodeListExt1.金融产品类型_理财产品.getValue());
    }
}
