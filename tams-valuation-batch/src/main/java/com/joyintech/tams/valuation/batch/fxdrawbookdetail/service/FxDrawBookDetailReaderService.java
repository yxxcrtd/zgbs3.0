package com.joyintech.tams.valuation.batch.fxdrawbookdetail.service;
/**
 * @author yanya
 * @create 2019-12-12 11:03
 */

import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokMatchRlat;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodMapper;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.cache.service.ValuationBatchCacheService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dao.DrawBaseInfoSelectComMapper;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.fxdrawbookdetail.dao.FxDrawBookDetailMapper;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName OverdueInterestCalBookReaderService
 * @Description: 逾期罚息计提类读入Service
 * @Author yanya
 * @Date 2019/12/23
 * @Version V1.0
 **/
@Service
@Scope("prototype")
public class FxDrawBookDetailReaderService {
    /**
     * 拆借计提入账批处理Mapper
     */
    @Autowired
    private DrawBaseInfoSelectComMapper drawBaseInfoSelectComMapper;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    @Autowired
    private ValuationBatchCacheService valuationBatchCacheService;

    /**
     * 逾期罚息计提mapper
     */
    @Autowired
    FxDrawBookDetailMapper fxDrawBookDetailMapper;

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
        List<DrawBaseInfoSelectComDTO> listHisDrawBaseInfoSelectComDTO = fxDrawBookDetailMapper.selectDrawBaseInfoFromHis(sqlMap);

        if (CollectionUtils.isEmpty(listHisDrawBaseInfoSelectComDTO)) {
            return result;
        }

        // 获取分录信息
        Set<String> bookSetIds = new HashSet<String>();
        listHisDrawBaseInfoSelectComDTO.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getBooksetId())) {
                bookSetIds.add(item.getBooksetId());
            }
        });
        Map<String, TreeNode> booksetTreeNodeMap = bookCommonLogic.commonForGetTreeNodeMap(bookSetIds, batchTask.getTaskId());
        //场景配置信息为空 直接return
        if(booksetTreeNodeMap == null) {
            return result;
        }

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
                    + hisDTO.getoCcy().trim() + hisDTO.getHappenDate().toString().trim() + hisDTO.getBranch().toString().trim();
            if (currMap.get(hisKey) == null) {
                mergeList.add(hisDTO);
            }
        }

        // 2.4 拼接processer用的结果数据
        Map<String, Object> resultMap = new HashMap<>();
        String strKey = "";
        for (DrawBaseInfoSelectComDTO currDTO : mergeList) {
            strKey = currDTO.getBooksetId().trim() + currDTO.getBokDetailId().trim() + currDTO.getBranch().toString().trim();
            currDTO.setTaskPri(batchTask.getTaskPri());
            if (resultMap.get(strKey) == null) {
                DrawBaseInfoSelectComResult drawBaseInfoSelectComResult = new DrawBaseInfoSelectComResult();
                drawBaseInfoSelectComResult.setBooksetId(currDTO.getBooksetId());
                drawBaseInfoSelectComResult.setBokDetailId(currDTO.getBokDetailId());
                drawBaseInfoSelectComResult.setInvAim(currDTO.getInvAim());
                drawBaseInfoSelectComResult.setBookDate((Date)param.get(BookConstant.BOOK_DATE));
                drawBaseInfoSelectComResult.setHappenDate((Date)param.get(BookConstant.HAPPEN_DATE));
                drawBaseInfoSelectComResult.setBookSetTreeNode(booksetTreeNodeMap.get(currDTO.getBooksetId()));
                drawBaseInfoSelectComResult.setBusiId(currDTO.getBusiId());
                drawBaseInfoSelectComResult.setBusiDetailType(currDTO.getBusiDetailType());
                drawBaseInfoSelectComResult.setBranch(currDTO.getBranch());
                drawBaseInfoSelectComResult.setBaseSubjectNature(currDTO.getBaseSubjectNature());
                drawBaseInfoSelectComResult.getListDrawBaseInfoSelectComDTO().add(currDTO);
                resultMap.put(strKey, drawBaseInfoSelectComResult);
            }
            else {
                DrawBaseInfoSelectComResult drawBaseInfoSelectComResult = (DrawBaseInfoSelectComResult)resultMap.get(strKey);
                drawBaseInfoSelectComResult.getListDrawBaseInfoSelectComDTO().add(currDTO);
                resultMap.put(strKey, drawBaseInfoSelectComResult);
            }
        }

        for (String keyStr : resultMap.keySet()) {
            result.add((DrawBaseInfoSelectComResult)resultMap.get(keyStr));
        }

        return result;
    }
}
