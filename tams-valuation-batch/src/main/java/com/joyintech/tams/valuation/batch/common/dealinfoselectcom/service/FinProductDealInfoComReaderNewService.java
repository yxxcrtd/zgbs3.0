/*
 * 文件名：FinProductDealInfoComRederService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wudebao
 * 修改时间：2018年5月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.dealinfoselectcom.service;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokMatchRlat;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodMapper;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dao.FinProductDealInfoComNewMapper;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoKey;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 拆借交易信息查詢
 *
 * @author wudebao
 * @version 1.0 2018年4月26日
 * @see FinProductDealInfoComReaderNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FinProductDealInfoComReaderNewService {

    /**
     * 拆借计提入账批处理Mapper
     */
    @Autowired
    private FinProductDealInfoComNewMapper finProductDealInfoComNewMapper;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    @Autowired
    private AccountingMethodMapper accountingMethodMapper;

    @Autowired
    private BookAccountGenLogicNew bookAccountGenLogicNew;

    /**
     * {@inheritDoc}
     */
    public List<FinProductDealInfoResult> execute(Map<String, Object> param) {

        // 1、处理变量设置
        List<FinProductDealInfoResult> result = new ArrayList<FinProductDealInfoResult>();

        Map<FinProductDealInfoKey, FinProductDealInfoResult> mResult = new HashMap<>();


        Map<String, Object> sqlMap = new HashMap<String, Object>();
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");
        SysBatchTask batchTask = (SysBatchTask)param.get("BATCHTASK");

        // 2、读取拆借交易数据
        // 交易状态
        sqlMap.put("tradeStsCX", DictCodeListExt1.交易状态_撤销.getValue());
        // 是否交易对手
        sqlMap.put("counterY", DictCodeList.是否_是.getValue());
        // 是否随交易出账
        sqlMap.put("isTradeY", DictCodeList.是否_是.getValue());
        sqlMap.putAll(param);

        // 获取匹配关系表
        List<String> matchTypeList = new ArrayList<>();
        matchTypeList.add(DictCodeListExt1.匹配类型_任务代码_业务类型.getValue());
        List<BokMatchRlat> valMatchRlats = accountingMethodMapper.selectValMatchRlatBySource(matchTypeList, (String)param.get("taskId"), null);
        if (valMatchRlats != null && valMatchRlats.size() > 0) {
            sqlMap.put("target", valMatchRlats.get(0).getTarget());
        }
        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTO = finProductDealInfoComNewMapper.selectDealInfo(sqlMap);

        // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
        List<String> listBokdetailId = new ArrayList<>();
        // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end
        if (CollectionUtils.isEmpty(listFinProductDealInfoComDTO)) {
            return result;
        }

        // 获取分录信息
        Set<String> bookSetIds = new HashSet<String>();
        listFinProductDealInfoComDTO.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getBooksetId())) {
                bookSetIds.add(item.getBooksetId());
            }
            // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
            // 去重，判断是否已存在
            if(!listBokdetailId.contains(item.getBokDetailId())){
                listBokdetailId.add(item.getBokDetailId());
            }
            // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end
        });
        // 查询辅助核算项配置信息
        // 定义 变量.辅助核算项Map<账套代码+账务明细代码, 辅助核算项配置>
        Map<String, SubjectMappingConfig> mappingConfigMap = bookAccountGenLogicNew.getSubjectMappingConfigList(null, listBokdetailId);

        // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end
        Map<String, TreeNode> booksetTreeNodeMap = bookCommonLogic.commonForGetTreeNodeMap(
            bookSetIds, batchTask.getTaskId());

        // 2.拼接为返回的数据结构
        Map<FinProductDealInfoKey, Object> resultMap = new HashMap<>();
        // 交易费用明细map
        Map<String, Object> detailMap = new HashMap<>();
        for (FinProductDealInfoComDTO finDTO : listFinProductDealInfoComDTO) {
            // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
            if(mappingConfigMap != null){
                SubjectMappingConfig config = mappingConfigMap.get(finDTO.getBooksetId().trim() + finDTO.getBokDetailId().trim());
                // 资金账户类型
                config.setAccountType(finDTO.getAcctType());
                finDTO.setSubjectMappingConfig(config);
            }
            // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end
            // 组装费用明细
            TrdTradeFeeDetail feeDetail = genFeeDetail(finDTO);
            if (feeDetail != null) {
                finDTO.getValTradeFeeDetailList().add(feeDetail);
            }
            FinProductDealInfoKey strKey = new FinProductDealInfoKey();
            strKey.setBooksetId(finDTO.getBooksetId());
            strKey.setBusiType(finDTO.getBusiType());
            strKey.setBokDetailId(finDTO.getBokDetailId());
            strKey.setBookDate((Date) param.get(BookConstant.BOOK_DATE));
            strKey.setHappenDate((Date) param.get(BookConstant.HAPPEN_DATE));
            strKey.setBooksetDate(finDTO.getBooksetDate());
            strKey.setInvAim(finDTO.getInvAim());
            strKey.setBookSetTreeNode(booksetTreeNodeMap.get(finDTO.getBooksetId()));
            strKey.setStandardCcy(finDTO.getStandardCcy());
            strKey.setAcctStandard(finDTO.getAcctStandard());
            finDTO.setTaskId(batchExecuteInfo.getTaskId());
            finDTO.setTaskPri(batchTask.getTaskPri());
            if (resultMap.get(strKey) == null) {
                FinProductDealInfoResult newFinProductDealInfoResult = new FinProductDealInfoResult();
                newFinProductDealInfoResult.setBooksetId(finDTO.getBooksetId());
                newFinProductDealInfoResult.setBusiType(finDTO.getBusiType());
                newFinProductDealInfoResult.setBokDetailId(finDTO.getBokDetailId());
                newFinProductDealInfoResult.setBookDate((Date) param.get(BookConstant.BOOK_DATE));
                newFinProductDealInfoResult.setHappenDate(batchExecuteInfo.getExecuteDate());
                newFinProductDealInfoResult.setBooksetDate(finDTO.getBooksetDate());
                newFinProductDealInfoResult.setInvAim(finDTO.getInvAim());
                newFinProductDealInfoResult.setBookSetTreeNode(
                    booksetTreeNodeMap.get(finDTO.getBooksetId()));
                newFinProductDealInfoResult.setStandardCcy(finDTO.getStandardCcy());
                newFinProductDealInfoResult.setAcctStandard(finDTO.getAcctStandard());
                newFinProductDealInfoResult.getListFinProductDealInfoComDTO().add(finDTO);
                resultMap.put(strKey, newFinProductDealInfoResult);
            }
            else {
                FinProductDealInfoResult finProductDealInfoResult = (FinProductDealInfoResult)resultMap.get(
                    strKey);
                // 是否是相同交易标识
                boolean samFlg = false;
                for (FinProductDealInfoComDTO dto : finProductDealInfoResult.getListFinProductDealInfoComDTO()) {
                    if (dto.getTradeId().equals(finDTO.getTradeId())) {
                        samFlg = true;
                        // 复制费用明细
                        if (CollectionUtils.isNotEmpty(finDTO.getValTradeFeeDetailList())
                                && finDTO.getValTradeFeeDetailList().get(0) != null) {
                            dto.getValTradeFeeDetailList().add(finDTO.getValTradeFeeDetailList().get(0));
                        }
                        break;
                    }
                }
                // 不是相同的交易，则添加入集合中
                if (!samFlg) {
                    finProductDealInfoResult.getListFinProductDealInfoComDTO().add(finDTO);
                    resultMap.put(strKey, finProductDealInfoResult);
                }
            }

        }
        for (FinProductDealInfoKey skey : resultMap.keySet()) {
        	if(mResult.containsKey(skey)){
        		mResult.remove(skey);
        	}
        	mResult.put(skey, bookCommonLogic.sortDealInfoByPs((FinProductDealInfoResult)resultMap.get(skey)));
//            result.add(bookCommonLogic.sortDealInfoByPs((FinProductDealInfoResult)resultMap.get(skey)));
        }
        for(FinProductDealInfoResult item : mResult.values()){
        	result.add(item);
        }
        return result;
    }

    private TrdTradeFeeDetail genFeeDetail(FinProductDealInfoComDTO finDTO) {
        TrdTradeFeeDetail valTradeFeeDetail = null;
        // 增加费用明细
        if (StringUtils.isNotEmpty(finDTO.getFeeType())) {
            String detailKey = finDTO.getTradeId().trim();
            //                                   + finDTO.getFeeType().trim();
            //            if (detailMap.get(detailKey) == null) {
            valTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号
            valTradeFeeDetail.setTradeId(finDTO.getTradeId());
            // 一次性费用类型
            valTradeFeeDetail.setFeeType(finDTO.getFeeType());
            // 是否随交易支付
            valTradeFeeDetail.setIsPayWithTrade(finDTO.getIsPayWithTrade());
            // 费用金额
            valTradeFeeDetail.setFeeAmt(finDTO.getFeeAmtDetail());
            // 费用代码
            valTradeFeeDetail.setFeeId(finDTO.getFeeId());
            //                detailMap.put(detailKey, valTradeFeeDetail);
            // 是否作为待摊
            valTradeFeeDetail.setIsPrepaid(finDTO.getIsPrepaid());
        }
        return valTradeFeeDetail;
    }
}