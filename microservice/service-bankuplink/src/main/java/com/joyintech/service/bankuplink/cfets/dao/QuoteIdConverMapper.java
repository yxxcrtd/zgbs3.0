/*
 * 文件名：CfetsInterfMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2020年12月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.bankuplink.cfets.dao;

import com.joyintech.service.bankuplink.cfets.dto.QuoteIdConverDto;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

/**
 * 报价编号和客户参考编号转化
 * @author wangyang1
 * @version 1.0 2021年10月28日
 * @see QuoteIdConverMapper
 * @since 1.0
 */
@Repository
public interface QuoteIdConverMapper {

    /**
     * 查询转换表数据
     * @return 转换表数据
     */
    List<QuoteIdConverDto> selectQuoteIdConver(Map<String, Object> paramMap);

    /**
     * 添加转换表数据
     * @param quoteIdConverDto 增加数据
     * @return 条数
     */
    int insertQuoteIdConver(QuoteIdConverDto quoteIdConverDto);
}
