package com.joyintech.tams.valuation.batch.accountmaintfeebook.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.batch.accountmaintfeebook.dao.AccountMaintFeeBookMapper;
import com.joyintech.tams.valuation.batch.accountmaintfeebook.dto.AccountMaintFeeBookDto;
import com.joyintech.tams.valuation.batch.accountmaintfeebook.dto.AccountMaintFeeBookResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.common.constants.BookConstant;

/**
 * 账户维护费每日计提 reader 的Service
 * @author hpp
 *
 */
@Service
@Scope("prototype")
public class AccountMaintFeeBookReaderService {
	/**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;
    
    @Autowired
    private AccountMaintFeeBookMapper accountMaintFeeBookMapper;
    
    /**
     * {@inheritDoc}
     */
    public List<AccountMaintFeeBookResult> execute(Map<String, Object> param) {

        // 1、处理变量设置
        List<AccountMaintFeeBookResult> result = new ArrayList<AccountMaintFeeBookResult>();
        SysBatchTask batchTask = (SysBatchTask)param.get("BATCHTASK");

        // 2、读取计提基础信息数据
        //List<AccountMaintFeeBookDto> listAccountMaintFeeBookDto = accountMaintFeeBookMapper.getAccMaintFeeInfo((Date)param.get(BookConstant.BOOK_DATE));
        List<AccountMaintFeeBookDto> listAccountMaintFeeBookDto = accountMaintFeeBookMapper.getAccMaintFeeInfo(param);

        if (CollectionUtils.isEmpty(listAccountMaintFeeBookDto)) {
            return result;
        }

        // 获取分录信息
        Set<String> bookSetIds = new HashSet<String>();
        listAccountMaintFeeBookDto.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getBooksetId())) {
                bookSetIds.add(item.getBooksetId());
            }
        });
        Map<String, TreeNode> booksetTreeNodeMap = bookCommonLogic.commonForGetTreeNodeMap(bookSetIds, batchTask.getTaskId());

        // 2.4 拼接processer用的结果数据
        Map<String, Object> resultMap = new HashMap<>();
        String strKey = "";
        for (AccountMaintFeeBookDto dto : listAccountMaintFeeBookDto) {
            strKey = dto.getBooksetId().trim() + dto.getBokDetailId().trim();
            dto.setTaskId(param.get("taskId").toString());
            dto.setTaskPri(batchTask.getTaskPri());
            dto.setBooksetDate((Date) param.get(BookConstant.BOOKSET_DATE));
            if (resultMap.get(strKey) == null) {
                AccountMaintFeeBookResult amfbresult = new AccountMaintFeeBookResult();
                amfbresult.setBooksetId(dto.getBooksetId());
                amfbresult.setBokDetailId(dto.getBokDetailId());
                amfbresult.setBookDate((Date)param.get(BookConstant.BOOK_DATE));
                amfbresult.setHappenDate((Date)param.get(BookConstant.HAPPEN_DATE));
                amfbresult.setBookSetTreeNode(booksetTreeNodeMap.get(dto.getBooksetId()));
                amfbresult.setBusiId(dto.getBusiId());
                amfbresult.getListAccountMaintFeeBookReaderDto().add(dto);
                resultMap.put(strKey, amfbresult);
            }
            else {
            	AccountMaintFeeBookResult amfbresult = (AccountMaintFeeBookResult)resultMap.get(strKey);
            	amfbresult.getListAccountMaintFeeBookReaderDto().add(dto);
                resultMap.put(strKey, amfbresult);
            }
        }

        for (String skey : resultMap.keySet()) {
            result.add((AccountMaintFeeBookResult)resultMap.get(skey));
        }

        return result;
    }
    
}
