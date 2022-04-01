/*
 * 文件名：BookCommonLogic.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统 修改人：wangzhiang 修改时间：2017年1月3日 修改内容：新建 系统名称：中信银行资管系统
 */

package com.joyintech.tams.valuation.batch.common.accountingmethod.accounting;

import com.joyintech.batch.core.dto.DimensionDto;
import com.joyintech.batch.core.dto.SysBatchExecInfoDto;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.assetimpairmentprovision.dto.AssetImpairmentProvisionReaderResult;
import com.joyintech.tams.valuation.batch.assetintdatecarryover.dao.AssetIntdateCarryoverMapper;
import com.joyintech.tams.valuation.batch.common.accounting.entity.PrdCashEntity;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodMapper;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodNewMapper;
import com.joyintech.tams.valuation.batch.common.accountingmethod.definiton.NodeType;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountEntry;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SceneConfigEntry;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.ValBooksetTodoDto;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.vatdraw.dto.PrdCashDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.service.BookAccountCommonService;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 记账公共类New
 *
 * @author qiangchengxi
 * @version 1.0 2017年1月3日
 * @see BookCommonLogicNew
 * @since 1.0
 */
@Service
public class BookCommonLogicNew {
    /**
     * AccountingMethodMapper
     */
    @Autowired
    private AccountingMethodNewMapper accountingMethodNewMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 根据会计场景代码获取某账套下的场景分支配置信息
     *
     * @param accountSceneCodes 本批次批处理任务记账时需要使用的全部会计场景代码
     * @param booksetId 账套代码
     * @return 会计场景配置信息
     * @see
     * @since 1.0
     */
    public List<AccountEntry> getSceneSubjectConfigByNodeId(List<String> accountSceneCodes,
                                                            String booksetId) {
        if (accountSceneCodes == null || accountSceneCodes.isEmpty()
            || StringUtil.isEmpty(booksetId)) {
            return null;
        }
        List<AccountEntry> accountEntries= new ArrayList<>();
        accountEntries = accountingMethodNewMapper.getSceneSubjectConfigByNodeId(booksetId, accountSceneCodes,
                GlobalConstant.ACCOUNTEVENTS);
        // 估值会计引擎升级，查询分录下基础科目性质对应所有的科目信息
        // 定义 变量.内部科目编码集合
        List<String> listSubjectInternalCode = new ArrayList<>();
        // 循环会计分录，初始化内部科目编码集合
        for(AccountEntry accountEntry : accountEntries){
            // 若科目基础科目性质不为空，则说明分录为非调整后的内部科目编码分录，不做处理，直接返回
            listSubjectInternalCode.add(accountEntry.getSubjectId());
        }
        //去重处理
        listSubjectInternalCode=listSubjectInternalCode.stream().distinct().collect(Collectors.toList());
        // 2. 根据内部编码集合查询相关底层科目信息
        Map<String, Object> params = new HashMap<>();
        params.put("booksetId", booksetId);
        params.put("listSubjectInternalCode", listSubjectInternalCode);
        params.put("isAcc", DictCodeList.是否_是.getValue());
        List<BokSubject> subjectList =  accountingMethodNewMapper.getSubjectByInternalCode(params);
        // 定义 变量.科目Map = 科目按照内部科目编码进行分组
        if(CollectionUtils.isNotEmpty(subjectList)){
            Map<String, List<BokSubject>> subjectMap= subjectList.stream().collect(Collectors.groupingBy(BokSubject :: getSubjectInternalCode));
            for(AccountEntry accountEntry : accountEntries){
                accountEntry.setBokSubjectList(subjectMap.get(accountEntry.getSubjectId()));
            }
        }
        return accountEntries;
        // modify by weipeng on 20220113 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end
    }

}
