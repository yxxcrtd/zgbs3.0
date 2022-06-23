/*
 * 文件名：ValVoucherQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：weipeng
 * 修改时间：2018年5月3日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.service.valuation.accounadjustmanage.voucherquery.service;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.service.valuation.accounadjustmanage.voucherquery.dao.ValVoucherQuery001Mapper;
import com.joyintech.service.valuation.accounadjustmanage.voucherquery.dto.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 记账凭证查询service
 * 
 * @author weipeng
 * @version 2.0 2018年5月3日
 * @see ValVoucherQuery001Service
 * @since 2.0
 */
@Service(value="serviceValVoucherQuery001Service")
@Transactional(rollbackFor = Exception.class)
public class ValVoucherQuery001Service extends CommonExportService<ValVoucherQuery001Dto> {
	
    /**
     * ValVoucherQuery001Mapper
     */
    @Autowired
	private ValVoucherQuery001Mapper mapper;
	
	/**
     * 所属账套下拉框获取
     *
     * @return 返回的数据
     * @exception Exception 异常
     * @since 1.0
     */
    public List<SelectOptionDto> getBooksetId() {
    	List<SelectOptionDto> listSelectDto=new ArrayList<SelectOptionDto>();
    	List<ValAssetbook001Dto> listBookset = mapper.getBooksetId();
    	listBookset.forEach(item -> {
    	    listSelectDto.add(createSelect(item.getBooksetId(), item.getBooksetName()));
    	});
        return listSelectDto;
    }

    /**
     * 业务类别下拉框获取
     *
     * @return 返回的数据
     * @exception Exception 异常
     * @since 1.0
     */
    public List<SelectOptionDto> getBusiClass() {
    	List<SelectOptionDto> listSelectDto=new ArrayList<SelectOptionDto>();
    	List<ValTablelist001Dto> listTable = mapper.getBusiClass();
    	listTable.forEach(item -> {
            listSelectDto.add(createSelect(item.getTableId(), item.getTableName()));
        });
        return listSelectDto;
    }
    
    /**
     * 格式化创建SelectOptionDto对象
     * <功能详细描述>
     * @param selectId 下拉Id值
     * @param text	下拉文本值
     * @return dto
     * @see
     * @since 1.0
     */
    public SelectOptionDto createSelect(String selectId, String text){
    	SelectOptionDto dto=new SelectOptionDto();
    	dto.setId(selectId);
    	dto.setText(text);
    	return dto;
    }

    /**
     * 根据条件获取表格信息
     *
     * @return 表格信息
     * @throws Exception 
     */
    public Page<ValVoucherQuery001Dto> findPage(int age,int lenght,ValVoucherQuery001SearchDto dto) {
        dto.setBooksetType(DictCodeList.账套类别_理财账套.getValue());
        dto.setBooksetType2(DictCodeList.账套类别_银行账套.getValue());
        dto.setUserId(LoginUserUtil.getUserName());
    	return super.queryForPage(age,lenght,()-> mapper.queryPageList(dto));
    }
    
    /**
     * 
     * 根据条件查询全部信息
     * @return 全部信息
     * @see
     * @since 1.0
     */
    public List<ValVoucherQuery001Dto> selectAll(ValVoucherQuery001SearchDto dto){
        dto.setBooksetType(DictCodeList.账套类别_理财账套.getValue());
        dto.setBooksetType2(DictCodeList.账套类别_银行账套.getValue());
    	return mapper.queryPageList(dto);
    }

    /**
     * 获取凭证打印信息
     * @param param
     * @return
     */
    public ValVoucherQueryPrintDto findPrintInfo(Map<String, Object> param) {
        ValVoucherQueryPrintDto dto = new ValVoucherQueryPrintDto();

        dto.setBookDate((String) param.get("bookDate"));
        dto.setBooksetId((String) param.get("booksetId"));
        dto.setBooksetName((String) param.get("booksetName"));
        dto.setBusVouchnum((String) param.get("busVouchnum"));
        dto.setTableId((String) param.get("tableId"));

        List<ValVoucherQuery002Dto> list = mapper.selectPrintInfo(param);
        dto.setList(list);
        // 汇总计算
        if (CollectionUtils.isNotEmpty(list)){
            dto.setCcy(SysDataUtil.getNameFromDataDict(DictCode.币种, list.get(0).getCcy()));

            // 获取交易复核人和交易经办人 by 杨欣欣
            String tradeId = list.get(0).getTradeId();
            ProcessMark processMark = new ProcessMark();
            processMark.setBusinessDataPk1(tradeId);
            Map<String, Object> map = WorkFlowUtil.getNodeInfoMap(processMark, 2);

            dto.setcAmtSum(list.stream().map(ValVoucherQuery002Dto::getcHappenAmt)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.setdAmtSum(list.stream().map(ValVoucherQuery002Dto::getdHappenAmt)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.setcBAmtSum(list.stream().map(ValVoucherQuery002Dto::getcBHappenAmt)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.setdBAmtSum(list.stream().map(ValVoucherQuery002Dto::getdBHappenAmt)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.setNumSum(list.stream().map(ValVoucherQuery002Dto::getHappenNumber)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return dto;
    }
}
