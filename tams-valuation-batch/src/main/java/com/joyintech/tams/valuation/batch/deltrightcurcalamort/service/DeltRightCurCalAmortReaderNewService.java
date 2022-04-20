/*
 * 文件名：DeltRightCurCalAmortReaderService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：niuqianyu
 * 修改时间：2019年1月29日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.deltrightcurcalamort.service;

import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.deltrightcurcalamort.dao.DeltRightCurCalAmortMapper;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author niuqianyu
 * @version 1.0 2019年1月29日
 * @see DeltRightCurCalAmortReaderNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DeltRightCurCalAmortReaderNewService {

    @Autowired
    private DeltRightCurCalAmortMapper deltRightCurCalAmortMapper;
    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    @Autowired
    private BookAccountGenLogicNew bookAccountGenLogicNew;

    /**
     * {@inheritDoc}
     */
    public List<DrawBaseInfoSelectComResult> execute(Map<String, Object> param) {

        // 1、处理变量设置
        List<DrawBaseInfoSelectComResult> result = new ArrayList<DrawBaseInfoSelectComResult>();
        Map<String, Object> sqlMap = new HashMap<String, Object>();
//        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");
        SysBatchTask batchTask = (SysBatchTask)param.get("BATCHTASK");

        // 2、读取计提基础信息数据
        // 是否到起息
        sqlMap.put("isBackValue", DictCodeList.是否_是.getValue());
        // 任务优先级
        sqlMap.put("taskPri", batchTask.getTaskPri());
        // 匹配类型
        sqlMap.put("matchType", DictCodeListExt1.匹配类型_任务代码_科目性质.getValue());
        sqlMap.put("matchType2", DictCodeListExt1.匹配类型_任务代码_计提成本会计场景.getValue());
        sqlMap.put("finprodType", DictCodeListExt1.金融产品类型_计划类资产.getValue());
        sqlMap.put("paramCode", DictCode.净值数据延迟天数.getValue());
        sqlMap.putAll(param);
        // 2.1、从当前表中，读取计提基础信息数据
        // List<DrawBaseInfoSelectComDTO> listCurrDrawBaseInfoSelectComDTO = new
        // ArrayList<DrawBaseInfoSelectComDTO>();
        // listCurrDrawBaseInfoSelectComDTO =
        // drawBaseInfoSelectComMapper.selectDrawBaseInfoFromCurr(sqlMap);

        // 2.2、从当前表中，读取计提基础信息数据
        List<DrawBaseInfoSelectComDTO> listHisDrawBaseInfoSelectComDTO = new ArrayList<DrawBaseInfoSelectComDTO>();
        listHisDrawBaseInfoSelectComDTO = deltRightCurCalAmortMapper.selectDrawBaseInfoFromHis(
            sqlMap);

        if (CollectionUtils.isEmpty(listHisDrawBaseInfoSelectComDTO)) {
            return result;
        }

        // 获取分录信息
        // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
        // 定义 变量.账务明细代码集合
        List<String> listBokdetailId = new ArrayList<>();
        // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end
        Set<String> bookSetIds = new HashSet<String>();
        listHisDrawBaseInfoSelectComDTO.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getBooksetId())) {
                bookSetIds.add(item.getBooksetId());
            }
            // 去重，判断是否已存在
            if(!listBokdetailId.contains(item.getBokDetailId())){
                listBokdetailId.add(item.getBokDetailId());
            }
        });
        Map<String, TreeNode> booksetTreeNodeMap = bookCommonLogic.commonForGetTreeNodeMap(
            bookSetIds, batchTask.getTaskId());
        // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
        Map<String, SubjectMappingConfig> mappingConfigMap  = bookAccountGenLogicNew.getSubjectMappingConfigList(null, listBokdetailId);
        // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end
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
            hisKey = hisDTO.getBooksetId().trim() + hisDTO.getBokDetailId().trim()
                     + hisDTO.getBaseSubjectNature().trim() + hisDTO.getoCcy().trim()
                     + hisDTO.getHappenDate().toString().trim();
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
                drawBaseInfoSelectComResult.setHappenDate(
                    (Date)param.get(BookConstant.HAPPEN_DATE));
                drawBaseInfoSelectComResult.setBookSetTreeNode(
                    booksetTreeNodeMap.get(currDTO.getBooksetId()));
                drawBaseInfoSelectComResult.setBusiId(currDTO.getBusiId());
                drawBaseInfoSelectComResult.getListDrawBaseInfoSelectComDTO().add(currDTO);
                resultMap.put(strKey, drawBaseInfoSelectComResult);
            }
            else {
                DrawBaseInfoSelectComResult drawBaseInfoSelectComResult = (DrawBaseInfoSelectComResult)resultMap.get(
                    strKey);
                drawBaseInfoSelectComResult.getListDrawBaseInfoSelectComDTO().add(currDTO);
                resultMap.put(strKey, drawBaseInfoSelectComResult);
            }
        }

        for (String skey : resultMap.keySet()) {
            result.add((DrawBaseInfoSelectComResult)resultMap.get(skey));
        }

        return result;
    }

}
