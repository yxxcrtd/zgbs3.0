/*
 * 文件名：DrawBaseInfoSelectComMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wudebao
 * 修改时间：2018年5月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dao;

import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 计提基础信息读取
 * 〈功能详细描述〉
 * @author wudebao
 * @version 1.0 2018年5月5日
 * @see DrawBaseInfoSelectComNewMapper
 * @since 1.0
 */
@Repository
public interface DrawBaseInfoSelectComNewMapper {
 
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return
     */
    public List<DrawBaseInfoSelectComDTO> selectDrawBaseInfoFromCurr(Map<String, Object> param);
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return
     */
    public List<DrawBaseInfoSelectComDTO> selectDrawBaseInfoFromHis(Map<String, Object> param);

}
