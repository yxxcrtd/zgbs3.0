package com.joyintech.tams.valuation.batch.feeForwardDrawTask.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.joyintech.tams.common.base.entity.TrdAccountmfConfig;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.feeForwardDrawTask.dao.FeeForwadDrawMapper;
import com.joyintech.tams.valuation.batch.feeForwardDrawTask.dto.FeeForwadDrawConfigDto;
import com.joyintech.tams.valuation.batch.feeForwardDrawTask.dto.FeeForwadDrawReaderResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.common.constants.BookConstant;

/**
 * 费用预提批处理 reader 的Service
 * @author jinmaodong
 * @date 2021-03-18 17:03:249
 *
 */
@Service
@Scope("prototype")
public class FeeForwadDrawReaderService {
	/**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    @Autowired
    private FeeForwadDrawMapper feeForwadDrawMapper;

    /**
     * {@inheritDoc}
     */
    public List<FeeForwadDrawReaderResult> execute(Map<String, Object> param) {

        // 1、处理变量设置
        List<FeeForwadDrawReaderResult> result = new ArrayList<FeeForwadDrawReaderResult>();
        SysBatchTask batchTask = (SysBatchTask)param.get("BATCHTASK");

        // 2、读取计提基础信息数据
        List<FeeForwadDrawConfigDto> listAccountMaintFeeBookDto = feeForwadDrawMapper.getFeeForwadDrawInfo(param);

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
        //按账套、账务明细代码进行分组
        for (FeeForwadDrawConfigDto dto : listAccountMaintFeeBookDto) {
            strKey = dto.getBooksetId()+dto.getBokDetailId();
            if (resultMap.get(strKey) == null) {
                FeeForwadDrawReaderResult amfbresult = new FeeForwadDrawReaderResult();
                amfbresult.setBooksetId(dto.getBooksetId());
                amfbresult.setBookDate((Date)param.get(BookConstant.BOOK_DATE));
                amfbresult.setHappenDate((Date)param.get(BookConstant.HAPPEN_DATE));
                amfbresult.setBookSetTreeNode(booksetTreeNodeMap.get(dto.getBooksetId()));
                amfbresult.setBokDetailId(dto.getBokDetailId());
                amfbresult.setInvAim(null);
                amfbresult.setBusiDetailType(dto.getFinprodType());
                amfbresult.setBusiId(dto.getFinprodiId());
                amfbresult.getListAccountMaintFeeBookReaderDto().add(dto);
                resultMap.put(strKey, amfbresult);
            }
            else {
            	FeeForwadDrawReaderResult amfbresult = (FeeForwadDrawReaderResult)resultMap.get(strKey);
            	amfbresult.getListAccountMaintFeeBookReaderDto().add(dto);
                resultMap.put(strKey, amfbresult);
            }
        }

        for (String skey : resultMap.keySet()) {
            result.add((FeeForwadDrawReaderResult)resultMap.get(skey));
        }

        return result;
    }

}
