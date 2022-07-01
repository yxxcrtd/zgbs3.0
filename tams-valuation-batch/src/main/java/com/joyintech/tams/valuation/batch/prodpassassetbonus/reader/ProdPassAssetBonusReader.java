/*
 * 文件名：caa.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2019年3月14日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.batch.prodpassassetbonus.reader;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.ValBooksetTodoDto;
import com.joyintech.tams.valuation.batch.prodpassassetbonus.dao.ProdPassAssetBonusMapper;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 产品资产通道自动红利再投Reader
 * @author weipeng
 * @version 1.0 2019年3月14日
 * @see caa
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ProdPassAssetBonusReader extends BaseReader<ValBooksetTodoDto> {
    
    @Autowired
    private ProdPassAssetBonusMapper prodPassAssetBonusMapper;
    
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValBooksetTodoDto> createList() {
        Map<String,Object> params = getParams();
        params.put("acctSubjectType", DictCodeListExt1.金融产品类型_通道.getValue());
        //根据参数查询对应待处理的账套
        List<ValBooksetTodoDto> listDto = prodPassAssetBonusMapper.selectBooksetTodo(params);
        // 获取场景代码会计分录
        if (CollectionUtils.isNotEmpty(listDto)) {
            // 获取分录信息
            Set<String> bookSetIds = new HashSet<String>();
            listDto.forEach(item -> {
                if (StringUtil.isNotEmpty(item.getBooksetId())) {
                    bookSetIds.add(item.getBooksetId());
                }
            });
            Map<String, TreeNode> nodes = bookCommonLogic.commonForGetTreeNodeMap(bookSetIds, listDto.get(0).getTaskId());
            for (ValBooksetTodoDto currDTO : listDto) {
                currDTO.setBookSetTreeNode(nodes.get(currDTO.getBooksetId()));
            }
        }
        return listDto;
    }
}
