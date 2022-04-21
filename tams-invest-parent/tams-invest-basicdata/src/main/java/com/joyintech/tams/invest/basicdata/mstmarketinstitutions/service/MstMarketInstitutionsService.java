/*
 * 文件名：MstMarketInstitutionsService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年8月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.basicdata.mstmarketinstitutions.service;

import java.util.*;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.dao.InterfCompintroductionMapper;
import com.joyintech.tams.common.base.dao.MstCustomerInfoMapper;
import com.joyintech.tams.common.base.entity.InterfCmsInstitution;
import com.joyintech.tams.common.base.entity.InterfCompintroduction;
import com.joyintech.tams.common.base.entity.MstCustomerInfo;
import com.joyintech.tams.common.base.entity.MstCustomerInfoAddWithBLOBs;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.basicdata.mstmarketinstitutions.dto.MstMarketInstitutionsDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.constant.OperationType;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;

/**
 * 市场机构管理
 * 〈功能详细描述〉
 *
 * @author huwenbin
 * @version 1.0 2018年8月20日
 * @see MstMarketInstitutionsService
 * @since 1.0
 */
@Service
public class MstMarketInstitutionsService extends CommonExportService<MstMarketInstitutionsDto>
        implements BaseProcessService<MstMarketInstitutionsDto> {

    /**
     * wind_公司简介
     */
    @Autowired
    private InterfCompintroductionMapper interfCompintroductionMapper;
    // 调用投管接口 add by wm 20190206 start
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    // 调用投管接口 add by wm 20190206 end
    /**
     * 客户信息表
     */
    @Autowired
    private MstCustomerInfoMapper mstCustomerInfoMapper;

    /**
     * 客户代码联想文本框service层实现
     *
     * @param map 检索参数
     * @return SuggestDto
     */
    public List<SuggestDto> getCustomerId(Map<String, Object> map) {
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/getCustomerId",
                map, SuggestDto.class);
    }

    /**
     * 客户名称联想文本框service层实现
     *
     * @param map Map<String, Object> 检索参数
     * @return SuggestDto
     */
    public List<SuggestDto> getCustomerName(Map<String, Object> map) {
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/getCustomerName",
                map, SuggestDto.class);
    }

    /**
     * 使用给定的检索分页数据。返回类型是Service的泛型。
     * 市场机构代码
     * 市场机构名称
     *
     * @param pageParam 查询方法
     * @return Page类
     * @throws Exception 异常信息
     */
    public Page<MstMarketInstitutionsDto> selectForPage(Map<String, Object> pageParam) {
        pageParam.put("customerType", DictCodeList.客户类型_法人.getValue());
        return restTemplate.postForPage(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/selectForPage",
                pageParam, MstMarketInstitutionsDto.class);
    }

    /**
     * selectPaRskCustomerPage
     * 根据客户代码查询市场机构详细信息
     * <功能详细描述>
     *
     * @param map map
     * @return MstMarketInstitutionsDto
     * @see
     * @since 1.0
     */
    public MstMarketInstitutionsDto selectDetail(Map<String, Object> map) {
        return restTemplate.postForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/selectDetail",
                map, MstMarketInstitutionsDto.class);
    }

    /**
     * 删除时检查数据是否被使用过
     * <功能详细描述>
     *
     * @param dto MstMarketInstitutionsDto
     * @return int
     * @see
     * @since 1.0
     */
    public int checkCodeIsUse(MstMarketInstitutionsDto dto) {
        return restTemplate.postForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/checkCodeIsUse",
                dto, Integer.class);
    }

    /**
     * 批量删除时检查数据是否被使用过
     * <功能详细描述>
     *
     * @param item List<MstMarketInstitutionsDto>
     * @return int
     * @see
     * @since 1.0
     */
    public List<MstMarketInstitutionsDto> checkAllCodeIsUse(List<MstMarketInstitutionsDto> item) {
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/checkAllCodeIsUse",
                item, MstMarketInstitutionsDto.class);
    }

    /**
     * 通过客户代码删除客户评级表信息
     * <功能详细描述>
     *
     * @param customerId String
     * @return int
     * @see
     * @since 1.0
     */
    public int deleteMstBondCreditRatingByPrimaryKey(String customerId) {
        return restTemplate.postForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/deleteMstBondCreditRatingByPrimaryKey",
                customerId, Integer.class);
    }

    /**
     * 文件导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
                "/com/joyintech/tams/invest/basicdata/mstmarketinstitutions/conf/export_conf.xml")
                .dataSource(DataSourceType.MASERT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<MstMarketInstitutionsDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }

    /**
     * 实现docomplement方法
     */
    @Override
    public void doComplete(MstMarketInstitutionsDto entity, OperationType type) {
        entity.setOperateType(type.toString());
        //add by wm 调用投管接口 start 2019-02-06 start
        this.mstMarketInterf(entity);
        //add by wm 调用投管接口 start 2019-02-06 end
        restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/doComplete", entity, Integer.class);
    }

    /**
     * 调用投管接口
     *
     * @param entity 界面上信息
     */
    private void mstMarketInterf(MstMarketInstitutionsDto entity) {

        if (entity != null) {
            InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
            investInterfCommDto.setPageId("MstMarketInstitutions002");
            MstCustomerInfo mstCustomerInfo = new MstCustomerInfo();
            MstCustomerInfoAddWithBLOBs istEntity = new MstCustomerInfoAddWithBLOBs();
            BeanUtil.copyPropertiesIgnoreNull(istEntity, entity);
            if (StringUtil.equals(entity.getOperateType(), "DELETE")) {
                investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
                mstCustomerInfo.setCustomerId(entity.getCustomerId());//客户代码
                mstCustomerInfo.setCustomerName(entity.getCustomerName());//客户名称
            } else if (StringUtil.equals(entity.getOperateType(), "NEW")) {

                investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());

                //客户信息表插入数据
                mstCustomerInfo.setCustomerId(entity.getCustomerId());//客户代码
                mstCustomerInfo.setCustomerName(entity.getCustomerName());//客户名称
                mstCustomerInfo.setCustomerType(DictCodeList.客户类型_法人.getValue());
            } else {
                investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_修改.getValue());
                //客户信息表更新数据
                mstCustomerInfo = mstCustomerInfoMapper.selectByPrimaryKey(entity.getCustomerId());
                mstCustomerInfo.setCustomerId(entity.getCustomerId());//客户代码
                mstCustomerInfo.setCustomerName(entity.getCustomerName());//客户名称
                mstCustomerInfo.setCustomerType(DictCodeList.客户类型_法人.getValue());
            }
            investInterfCommService.doInvestInterf(investInterfCommDto);
        }
    }

    /**
     * 省市县联动
     * <功能详细描述>
     *
     * @param param Map<String,Object>
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getAddressSelectOption(Map<String, Object> param) {
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/getAddressSelectOption",
                param, SelectOptionDto.class);
    }

    /**
     * 查询所有的市场机构代码、名称
     * <功能详细描述>
     *
     * @return List<MstMarketInstitutionsDto>
     * @see
     * @since 1.0
     */
    public List<MstMarketInstitutionsDto> customerNameCheck(String customerName) {
        return restTemplate.getForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/customerNameCheck?customerName=" + customerName,
                MstMarketInstitutionsDto.class);
    }

    /**
     * 根据becif查询CMS_机构信息
     * <功能详细描述>
     *
     * @param becif String
     * @return InterfCmsInstitution
     * @see
     * @since 1.0
     */
    public InterfCmsInstitution selectInterfCmsInstitution(String becif) {
        return restTemplate.getForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/selectInterfCmsInstitution?becif=" + becif,
                InterfCmsInstitution.class);
    }

    /**
     * 根据wind编码查询wind_公司简介
     * <功能详细描述>
     *
     * @param objectId String
     * @return InterfCompintroduction
     * @see
     * @since 1.0
     */
    public InterfCompintroduction selectInterfCompintroduction(String objectId) {
        return interfCompintroductionMapper.selectByPrimaryKey(objectId);
    }

    /**
     * 获取评级机构
     *
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getGradeOrgId(Map<String, Object> parameters) {
        //parameters.put("isRatingAgencies", DictCodeList.是否_是.getValue());
        List<SuggestDto> tempList = restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/getGradeOrgId",
                parameters, SuggestDto.class);

        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }

    /**
     * 批量删除时检查数据是否进行过客户性质匹配
     * <功能详细描述>
     *
     * @param item List<MstMarketInstitutionsDto>
     * @return int
     * @see
     * @since 1.0
     */
    public List<MstMarketInstitutionsDto> deleteCheck(List<MstMarketInstitutionsDto> item) {
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/deleteCheck",
                item, MstMarketInstitutionsDto.class);
    }

    /**
     * 1.判断机构表中是否存在
     * 存在：则设置为发行人
     * 不存在：执行第二段逻辑
     * 2.判断接口表里是否存在,
     * 不存在：返回null
     * 存在：判断必填元素是否齐全，齐全则自动添加并设置为发行人
     * 要素不齐全则返回查询数据回前台
     *
     * @param insitituteCode
     * @return
     */
    public Map<String, Object> autoAddInstitutions(String insitituteCode) {
        Map<String, Object> result = new HashMap<>();
        MstMarketInstitutionsDto mstDto = restTemplate.getForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/autoAddInstitutions?insitituteCode=" + insitituteCode, MstMarketInstitutionsDto.class);
        if (Objects.nonNull(mstDto) && mstDto.getDealFlag() == 1) {
            mstDto.setPageModel(GlobalConstant.DETAIL_PAGE_MODEL_NEW);
            mstDto.setOperateType("NEW");
            mstDto.setMstBondCreditRating(new ArrayList<>());
            Set<ConstraintViolation<MstMarketInstitutionsDto>> validate = Validation.buildDefaultValidatorFactory().getValidator().validate(mstDto, ValidateGroupEdit.class);
            if (CollectionUtils.isNotEmpty(validate)) {//没有校验通过
                result.put("errorCode", 0);
                result.put("message", "匹配失败");
            } else {//校验通过直接入库
                this.mstMarketInterf(mstDto);
                restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/MstMarketInstitutions/doComplete", mstDto, Integer.class);
                mstDto.setDealFlag(0);
                result.put("errorCode", 1);
                result.put("message", "发行人已自动生成!");
            }
        } else if (Objects.nonNull(mstDto) && mstDto.getDealFlag() == 0) {
            result.put("errorCode", 1);
            result.put("message", "发行人的客户性质已配置!");
        } else {
            result.put("errorCode", 0);
            result.put("message", "匹配失败");
        }
        result.put("data", mstDto);
        return result;
    }


}
