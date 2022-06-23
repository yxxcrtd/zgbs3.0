/*
 * 文件名：ValAssetBook002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：huangjian
 * 修改时间：2018年5月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.service.valuation.accounadjustmanage.assetbook.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.service.valuation.accounadjustmanage.assetbook.dao.ValAssetBook002Mapper;
import com.joyintech.service.valuation.accounadjustmanage.assetbook.dto.ValAcctStandardDto;
import com.joyintech.service.valuation.accounadjustmanage.assetbook.dto.ValAssetbook002Dto;
import com.joyintech.tams.common.base.dao.BokBooksetDateMapper;
import com.joyintech.tams.common.base.dao.BokBooksetMapper;
import com.joyintech.tams.common.base.entity.BokBookset;
import com.joyintech.tams.common.base.entity.BokBooksetDate;
import com.joyintech.tams.common.base.entity.BokBooksetPlan;
import com.joyintech.tams.common.base.entity.BokSchemeInfo;
import com.joyintech.tams.common.base.entity.BokValScheme;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 新建账套
 *
 * @author 黄健
 * @version 1.0 2018年5月10日
 * @see ValAssetBook002Service
 * @since 1.0
 */
@Service(value="serviceValAssetBook002Service")
@Transactional(rollbackFor = Exception.class)
public class ValAssetBook002Service extends CommonExportService<ValAssetbook002Dto> implements BaseProcessService<ValAssetbook002Dto> {

    /**
     * ValAssetBook002Mapper
     */
    @Autowired
    private ValAssetBook002Mapper mapper;

    /**
     * ValBokBooksetMapper
     */
    @Autowired
    private BokBooksetMapper booksetMapper;

    /**
     * 账套日期
     */
    @Autowired
    private BokBooksetDateMapper bokBooksetDateMapper;

    /**
     * 获取账套准则
     *
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getAcctStandard() {
        List<SelectOptionDto> listSelectDto = new ArrayList<SelectOptionDto>();
        List<ValAcctStandardDto> list = mapper.getAcctStandard();
        list.forEach(item -> {
            listSelectDto.add(createSelect(item.getAcctStandardId(), item.getAcctStandardName()));
        });
        return listSelectDto;
    }

    /**
     * 获取本位币 <功能详细描述>
     *
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getStandardCcy() {
        List<SelectOptionDto> listSelectDto = new ArrayList<SelectOptionDto>();
        List<String> list = mapper.getStandardCcy();
        list.forEach(item -> {
            listSelectDto.add(createSelect(item.toString(), item.toString()));
        });
        return listSelectDto;
    }

    /**
     * 格式化创建SelectOptionDto对象
     *
     * @param selectId 下拉Id值
     * @param text 下拉文本值
     * @return SelectOptionDto
     * @see
     * @since 1.0
     */
    public SelectOptionDto createSelect(String selectId, String text) {
        SelectOptionDto dto = new SelectOptionDto();
        dto.setId(selectId);
        dto.setText(text);
        return dto;
    }

    /**
     * 检查账套代码是否存在
     *
     * @param id 账套代码
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean checkBooksetId(String id) {
        return mapper.checkBooksetId(id);
    }

    /**
     * 检查账套代码是否存在
     *
     * @param id 账套代码
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean checkBooksetName(ValAssetbook002Dto dto) {
        return mapper.checkBooksetName(dto);
    }


    /**
     * 插入 <一句话功能简述> <功能详细描述>
     *
     * @param dto 账套dto
     * @return int
     * @see
     * @since 1.0
     */
    public int insert(ValAssetbook002Dto dto) {
        BokBooksetDate valBokBooksetDate = bokBooksetDateMapper.selectByPrimaryKey(
                dto.getBooksetId());
        // 不存在账套日期，新增，已存在则更新 注释修改账套日期的逻辑by yinya on 20211221： 修改账套日期会导致手工凭证余额方向置空
        if (valBokBooksetDate != null) {
//            valBokBooksetDate.setBooksetId(dto.getBooksetId());
//            // 账套日期 = 账套.开始日期
//            valBokBooksetDate.setBooksetDate(dto.getStartDate());
//            // 更新账套日期
//            bokBooksetDateMapper.updateByPrimaryKey(valBokBooksetDate);
        }
        else {
            valBokBooksetDate = new BokBooksetDate();
            valBokBooksetDate.setBooksetId(dto.getBooksetId());
            // 账套日期 = 账套.开始日期
            valBokBooksetDate.setBooksetDate(dto.getStartDate());
            valBokBooksetDate.setCreateUser(SecurityUtil.getLoginUser().getUserName());
            valBokBooksetDate.setCreateTime(new Date());
            valBokBooksetDate.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
            valBokBooksetDate.setUpdateTime(new Date());

            // 保存账套日期
            bokBooksetDateMapper.insert(valBokBooksetDate);
        }

        // 账套估值方案表 TODO
        if (StringUtils.isNoneEmpty(dto.getBooksetSchemeId())) {
            BokBooksetPlan bokBooksetPlan = new BokBooksetPlan();
            bokBooksetPlan.setBooksetSchemeId(dto.getBooksetSchemeId());
            bokBooksetPlan.setSchemeId(dto.getSchemeId());
            mapper.updateBooksetPlan(bokBooksetPlan);
        }
        else {// 账套估值方案未关联 - 新建
            BokBooksetPlan bokBooksetPlan = new BokBooksetPlan();
            bokBooksetPlan.setBooksetSchemeId(IDGenerator.generate());
            bokBooksetPlan.setBooksetId(dto.getBooksetId());
            bokBooksetPlan.setSchemeId(dto.getSchemeId());
            bokBooksetPlan.setSchemeType(DictCodeListExt1.配置方案类型_估值方案.getValue());
            mapper.insertBooksetPlan(bokBooksetPlan);
        }

        // 判断是新建操作还是修改操作
        if (DictCodeList.是否_是.getValue().equals(dto.getIsEdit())) {
            // 修改操作
            return booksetMapper.updateByPrimaryKey(dto);
        }
        else {
            // 新建操作
            return booksetMapper.insertSelective(dto);
        }

    }

    public ValAssetbook002Dto getBookSetById(String booksetId) {
        return mapper.getBookSetById(booksetId);
    }

    /**
     * 下拉框：所属估值方案
     *
     * @param pageParam
     * @return
     */
    public List<SelectOptionDto> findScheme(Map<String, Object> pageParam) {
        List<BokValScheme> resultList = mapper.selectScheme(pageParam);
        List<SelectOptionDto> selectOptionList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (BokValScheme single : resultList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(single.getSchemeId());
                selectOptionDto.setText(single.getSchemeName());
                selectOptionList.add(selectOptionDto);
            }
        }
        return selectOptionList;
    }


    public void doComplete(ValAssetbook002Dto entity, OperationType type) {
        BokBooksetDate valBokBooksetDate = bokBooksetDateMapper.selectByPrimaryKey(
                entity.getBooksetId());
        // 不存在账套日期，新增，已存在则更新 注释修改账套日期的逻辑by yinya on 20211221： 修改账套日期会导致手工凭证余额方向置空
        if (valBokBooksetDate != null) {
//            valBokBooksetDate.setBooksetId(entity.getBooksetId());
//            // 账套日期 = 账套.开始日期
//            valBokBooksetDate.setBooksetDate(entity.getStartDate());
//            // 更新账套日期
//            bokBooksetDateMapper.updateByPrimaryKey(valBokBooksetDate);
        }
        else {
            valBokBooksetDate = new BokBooksetDate();
            valBokBooksetDate.setBooksetId(entity.getBooksetId());
            // 账套日期 = 账套.开始日期
            valBokBooksetDate.setBooksetDate(entity.getStartDate());
            valBokBooksetDate.setCreateUser(SecurityUtil.getLoginUser().getUserName());
            valBokBooksetDate.setCreateTime(new Date());
            valBokBooksetDate.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
            valBokBooksetDate.setUpdateTime(new Date());

            // 保存账套日期
            bokBooksetDateMapper.insert(valBokBooksetDate);
        }

        // 账套估值方案表 TODO
        if (StringUtils.isNoneEmpty(entity.getBooksetSchemeId())) {
            BokBooksetPlan bokBooksetPlan = new BokBooksetPlan();
            bokBooksetPlan.setBooksetSchemeId(entity.getBooksetSchemeId());
            bokBooksetPlan.setSchemeId(entity.getSchemeId());
            mapper.updateBooksetPlan(bokBooksetPlan);
        }
        else {// 账套估值方案未关联 - 新建
            BokBooksetPlan bokBooksetPlan = new BokBooksetPlan();
            bokBooksetPlan.setBooksetSchemeId(IDGenerator.generate());
            bokBooksetPlan.setBooksetId(entity.getBooksetId());
            bokBooksetPlan.setSchemeId(entity.getSchemeId());
            bokBooksetPlan.setSchemeType(DictCodeListExt1.配置方案类型_估值方案.getValue());
            mapper.insertBooksetPlan(bokBooksetPlan);
        }
        // 税务方案表 TODO
        if (StringUtils.isNoneEmpty(entity.getBooksetTaxSchemeId())) {
            BokBooksetPlan bokBooksetPlan = new BokBooksetPlan();
            bokBooksetPlan.setBooksetSchemeId(entity.getBooksetTaxSchemeId());
            bokBooksetPlan.setSchemeId(entity.getTaxSchemeId());
            mapper.updateBooksetPlan(bokBooksetPlan);
        }
        else {// 税务方案未关联 - 新建
            BokBooksetPlan bokBooksetPlan = new BokBooksetPlan();
            bokBooksetPlan.setBooksetSchemeId(IDGenerator.generate());
            bokBooksetPlan.setBooksetId(entity.getBooksetId());
            bokBooksetPlan.setSchemeId(entity.getTaxSchemeId());
            bokBooksetPlan.setSchemeType(DictCodeListExt1.配置方案类型_税务方案.getValue());
            mapper.insertBooksetPlan(bokBooksetPlan);
        }
        // 影子定价方案表 TODO
        if (StringUtils.isNoneEmpty(entity.getBooksetShadowPriceSchemeId())) {
            BokBooksetPlan bokBooksetPlan = new BokBooksetPlan();
            bokBooksetPlan.setBooksetSchemeId(entity.getBooksetShadowPriceSchemeId());
            bokBooksetPlan.setSchemeId(entity.getShadowPriceSchemeId());
            mapper.updateBooksetPlan(bokBooksetPlan);
        }
        else {// 影子定价方案未关联 - 新建
            BokBooksetPlan bokBooksetPlan = new BokBooksetPlan();
            bokBooksetPlan.setBooksetSchemeId(IDGenerator.generate());
            bokBooksetPlan.setBooksetId(entity.getBooksetId());
            bokBooksetPlan.setSchemeId(entity.getShadowPriceSchemeId());
            bokBooksetPlan.setSchemeType(DictCodeListExt1.配置方案类型_影子定价方案.getValue());
            mapper.insertBooksetPlan(bokBooksetPlan);
        }
        BokBookset bokBookset = new BokBookset();
//        bokBookset.setAcctMode(entity.getAcctMode());
        bokBookset.setAcctStandard(entity.getAcctStandard());
        bokBookset.setAcctSubjectId(entity.getAcctSubjectId());
        bokBookset.setAcctSubjectType(entity.getAcctSubjectType());
        bokBookset.setBokSpecMode(entity.getBokSpecMode());
        bokBookset.setBooksetFullname(entity.getBooksetFullname());
        bokBookset.setBooksetId(entity.getBooksetId());
        bokBookset.setBooksetIdTmpl(entity.getBooksetIdTmpl());
        bokBookset.setBooksetName(entity.getBooksetName());
        bokBookset.setBooksetType(entity.getBooksetType());
        bokBookset.setEndDate(entity.getEndDate());
        bokBookset.setStandardCcy(entity.getStandardCcy());
        bokBookset.setStartDate(entity.getStartDate());
        booksetMapper.updateByPrimaryKeySelective(bokBookset);
    }

    /**
     * 查询账套生成凭证数
     *
     * @param booksetId
     * @return
     */
    public int queryVoucherCount(String booksetId) {
        return mapper.queryVoucherCount(booksetId);
    }

    /**
     *
     *税务方案
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> findTaxScheme(Map<String, Object> pageParam) {
        List<BokSchemeInfo> resultList = mapper.selectTaxScheme(pageParam);
        List<SelectOptionDto> selectOptionList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (BokSchemeInfo single : resultList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(single.getSchId());
                selectOptionDto.setText(single.getSchName());
                selectOptionList.add(selectOptionDto);
            }
        }
        return selectOptionList;
    }

    /**
     *
     * 影子定价方案
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> findShadowPriceScheme(Map<String, Object> pageParam) {
        List<BokSchemeInfo> resultList = mapper.selectShadowPriceScheme(pageParam);
        List<SelectOptionDto> selectOptionList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (BokSchemeInfo single : resultList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(single.getSchId());
                selectOptionDto.setText(single.getSchName());
                selectOptionList.add(selectOptionDto);
            }
        }
        return selectOptionList;
    }




}
