/*
 * 文件名：DrawBaseInfoSelectComReaderService.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：估值核算系统V1.0 修改人：wudebao
 * 修改时间：2018年5月24日 修改内容：新建 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.service;

import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokMatchRlat;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodMapper;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.cache.service.ChangingCacheService;
import com.joyintech.tams.valuation.batch.common.cache.service.ValuationBatchCacheService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dao.DrawBaseInfoSelectComNewMapper;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.InitFinProductCommonBatchService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.InitConstant;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 计提批处理基础信息读入service
 *
 * @author wudebao
 * @version 1.0 2018年5月25日
 * @see DrawBaseInfoSelectComReaderNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DrawBaseInfoSelectComReaderNewService {
    /**
     * 拆借计提入账批处理Mapper
     */
    @Autowired
    private DrawBaseInfoSelectComNewMapper drawBaseInfoSelectComNewMapper;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    @Autowired
    private BookAccountGenLogicNew bookAccountGenLogicNew;
    /**
     * 标的共通方法service
     */
    @Autowired
    private InitFinProductCommonBatchService initFinProductCommonBatchService;
    @Autowired
    private ValuationBatchCacheService valuationBatchCacheService;
    @Autowired
    private ChangingCacheService changingCacheService;
    /**
     * {@inheritDoc}
     */
    public List<DrawBaseInfoSelectComResult> execute(Map<String, Object> param) {

        // 1、处理变量设置
        List<DrawBaseInfoSelectComResult> result = new ArrayList<DrawBaseInfoSelectComResult>();
        Map<String, Object> sqlMap = new HashMap<String, Object>();
        // SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");
        SysBatchTask batchTask = (SysBatchTask)param.get("BATCHTASK");

        // 2、读取计提基础信息数据
        // 2、读取计提基础信息数据
        // 是否到起息
        sqlMap.put("isBackValue", DictCodeList.是否_是.getValue());
        // 任务优先级
        sqlMap.put("taskPri", batchTask.getTaskPri());
        // 匹配类型
        sqlMap.put("matchType", DictCodeListExt1.匹配类型_任务代码_科目性质.getValue());
        sqlMap.put("matchType2", DictCodeListExt1.匹配类型_任务代码_计提成本会计场景.getValue());
        /** add by caoguo on 20201031 for 增加减值P3的枚举传入 strat */
        sqlMap.put("stageP3", DictCodeListExt3.阶段_买入后出现减值_P3.getValue());
        /** add by caoguo on 20201031 end*/
        sqlMap.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        sqlMap.putAll(param);
        // 2.1、从当前表中，读取计提基础信息数据
        // List<DrawBaseInfoSelectComDTO> listCurrDrawBaseInfoSelectComDTO = new
        // ArrayList<DrawBaseInfoSelectComDTO>();
        // listCurrDrawBaseInfoSelectComDTO =
        // drawBaseInfoSelectComMapper.selectDrawBaseInfoFromCurr(sqlMap);
        List<String> matchTypeList = new ArrayList<>();
        matchTypeList.add(DictCodeListExt1.匹配类型_任务代码_科目性质.getValue());
        //List<BokMatchRlat> valMatchRlats = accountingMethodMapper.selectValMatchRlatBySource(matchTypeList, (String)param.get("taskId"), null);
        List<BokMatchRlat> valMatchRlats = valuationBatchCacheService.getBokMatchRlatFromCache(matchTypeList, (String)param.get("taskId"), null);
        if (valMatchRlats != null && valMatchRlats.size() > 0) {
            List<String> targetList = new ArrayList<>();
            valMatchRlats.forEach(x->{
                targetList.add(x.getTarget());
            });
            sqlMap.put("baseSubjectNatureList", targetList);
        }

        // 2.2、从当前表中，读取计提基础信息数据
        List<DrawBaseInfoSelectComDTO> listHisDrawBaseInfoSelectComDTO = new ArrayList<DrawBaseInfoSelectComDTO>();
        listHisDrawBaseInfoSelectComDTO = drawBaseInfoSelectComNewMapper.selectDrawBaseInfoFromHis(sqlMap);

        if (CollectionUtils.isEmpty(listHisDrawBaseInfoSelectComDTO)) {
            return result;
        }

        // 获取分录信息
        Set<String> bookSetIds = new HashSet<String>();
        // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
        // 定义 变量.辅助核算项Map<账套代码+账务明细代码, 辅助核算项配置>
        // 定义 变量.账务明细代码分组Map
//        Map<String,List<String>> bokdetailIdMap = new HashMap<>();
        List<String> listBokdetailId = new ArrayList<>();
        // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end
        listHisDrawBaseInfoSelectComDTO.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getBooksetId())) {
                bookSetIds.add(item.getBooksetId());
                // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
//                // 按账套对账务明细代码进行分组
//                List<String> bokdetailIds = new ArrayList<>();
//                if(bokdetailIdMap.get(item.getBooksetId()) != null){
//                    bokdetailIds = bokdetailIdMap.get(item.getBooksetId());
//                }
//                bokdetailIds.add(item.getBokDetailId());
//                bokdetailIdMap.put(item.getBooksetId(), bokdetailIds);
                // 去重，判断是否已存在
                if(!listBokdetailId.contains(item.getBokDetailId())){
                    listBokdetailId.add(item.getBokDetailId());
                }
                // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end
            }
        });

        // 查询辅助核算项配置信息
        // 定义 变量.辅助核算项配置信息
//            List<SubjectMappingConfig> listConfigInfo = new ArrayList<>();
//            for(String key : bokdetailIdMap.keySet()){
//                List<SubjectMappingConfig> listConfig= bookAccountGenLogicNew.getSubjectMappingConfigList(key, bokdetailIdMap.get(key));
//                if(CollectionUtils.isNotEmpty(listConfig)){
//                    listConfigInfo.addAll(listConfig);
//                }
//            }
//            List<SubjectMappingConfig> listConfigInfo = bookAccountGenLogicNew.getSubjectMappingConfigList(null, listBokdetailId);
//            // 遍历 变量.辅助核算项配置信息，按账套+账务明细代码进行分组
//            if(CollectionUtils.isNotEmpty(listConfigInfo)){
//                listConfigInfo.forEach(item ->{
//                    mappingConfigMap.put(item.getBooksetId().trim() + item.getBokDetailId().trim(), item);
//                });
//            }
        Map<String, SubjectMappingConfig> mappingConfigMap  = bookAccountGenLogicNew.getSubjectMappingConfigList(null, listBokdetailId);

        // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end
        Map<String, TreeNode> booksetTreeNodeMap = bookCommonLogic.commonForGetTreeNodeMap(bookSetIds, batchTask.getTaskId());

        // 2.3、去重处理
        Map<String, Object> currMap = new HashMap<>();
        List<DrawBaseInfoSelectComDTO> mergeList = new ArrayList<DrawBaseInfoSelectComDTO>();
        // 2.3.1 当前表的数据记录全部保留
        // for(DrawBaseInfoSelectComDTO currDTO : listCurrDrawBaseInfoSelectComDTO){
        // mergeList.add(currDTO);
        // strMergeMapKey = currDTO.getBooksetId().trim() + currDTO.getBokDetailId().trim() +
        // currDTO.getBaseSubjectNature().trim() + currDTO.getOCcy().trim() +
        // currDTO.getHappenDate().toString().trim();
        // currMap.put(strMergeMapKey , currDTO);
        // }

        // 2.3.2 当前表不存在，历史表存在的数据保留
        String hisKey = "";
        for (DrawBaseInfoSelectComDTO hisDTO : listHisDrawBaseInfoSelectComDTO) {
            hisKey = hisDTO.getBooksetId().trim() + hisDTO.getBokDetailId().trim() + hisDTO.getBaseSubjectNature().trim()
                    + hisDTO.getoCcy().trim() + hisDTO.getHappenDate().toString().trim();
            if (currMap.get(hisKey) == null) {
                mergeList.add(hisDTO);
            }
        }

        // 2.4 拼接processer用的结果数据
        Map<String, Object> resultMap = new HashMap<>();
        String strKey = "";
        for (DrawBaseInfoSelectComDTO currDTO : mergeList) {
            strKey = currDTO.getBooksetId().trim() + currDTO.getBokDetailId().trim();
            currDTO.setTaskPri(batchTask.getTaskPri());
            // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
            if(mappingConfigMap != null){
                currDTO.setSubjectMappingConfig(mappingConfigMap.get(strKey));
            }
            // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end
            if (resultMap.get(strKey) == null) {
                DrawBaseInfoSelectComResult drawBaseInfoSelectComResult = new DrawBaseInfoSelectComResult();
                drawBaseInfoSelectComResult.setBooksetId(currDTO.getBooksetId());
                drawBaseInfoSelectComResult.setBokDetailId(currDTO.getBokDetailId());
                drawBaseInfoSelectComResult.setInvAim(currDTO.getInvAim());
                drawBaseInfoSelectComResult.setBookDate((Date)param.get(BookConstant.BOOK_DATE));
                drawBaseInfoSelectComResult.setHappenDate((Date)param.get(BookConstant.HAPPEN_DATE));
                drawBaseInfoSelectComResult.setBookSetTreeNode(booksetTreeNodeMap.get(currDTO.getBooksetId()));
                drawBaseInfoSelectComResult.setBusiId(currDTO.getBusiId());
                /** add by weipeng on 20220429 : 增加方案收益类型 start**/
                drawBaseInfoSelectComResult.setSchProfitType(currDTO.getSchProfitType());
                /** add by weipeng on 20220429 : 增加方案收益类型 end**/
                drawBaseInfoSelectComResult.getListDrawBaseInfoSelectComDTO().add(currDTO);
                resultMap.put(strKey, drawBaseInfoSelectComResult);
            }
            else {
                DrawBaseInfoSelectComResult drawBaseInfoSelectComResult = (DrawBaseInfoSelectComResult)resultMap.get(strKey);
                drawBaseInfoSelectComResult.getListDrawBaseInfoSelectComDTO().add(currDTO);
                resultMap.put(strKey, drawBaseInfoSelectComResult);
            }
        }

        for (String skey : resultMap.keySet()) {
            result.add((DrawBaseInfoSelectComResult)resultMap.get(skey));
        }
        /**性能优化，在reder中预处理process中需要的信息**/
        if(null != param.get(InitConstant.BATCH_FETCH_READE) && CollectionUtils.isNotEmpty(result)) {
            DrawBaseInfoReaderParameterGenerator parameterGenerator = (DrawBaseInfoReaderParameterGenerator) SpringContextUtil.getBean((String)param.get(InitConstant.BATCH_FETCH_READE));
            initFinProductCommonBatchService.quaryResultInfo(result, parameterGenerator);
        }

        return result;
    }
}
