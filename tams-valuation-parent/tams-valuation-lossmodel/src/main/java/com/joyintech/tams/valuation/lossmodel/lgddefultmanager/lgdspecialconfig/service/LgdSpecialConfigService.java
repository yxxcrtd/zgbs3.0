/*
 * 文件名：ModGradePdConfigController.java
 * 版权：Copyright 2021-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中邮理财资产管理系统V1.0
 * 修改人：yuzj
 * 修改时间：2021年6月23日
 * 修改内容：新建
 * 系统名称：中邮理财资产管理系统V1.0
 */

package com.joyintech.tams.valuation.lossmodel.lgddefultmanager.lgdspecialconfig.service;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.lossmodel.lgddefultmanager.lgdspecialconfig.dto.LgdSpecialConfigDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import org.springframework.transaction.annotation.Transactional;


/**
 * 特殊资产LGD维护Service
 *
 * @author yuzj
 * @version 1.0 2021年6月23日
 * @see LgdSpecialConfigService
 * @since 1.0
 */
@Service
public class LgdSpecialConfigService extends  CommonExportService<LgdSpecialConfigDto> implements BaseProcessService<LgdSpecialConfigDto> {
    private static final String TMP_DIR = "/tpl/";
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(LgdSpecialConfigService.class);


    /**
     * getFilter
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    private Predicate<LgdSpecialConfigDto> getFilter(Map<String, Object> pageParam) {
        return (item -> {
            String seqNo = (String)pageParam.get("subjectNo");
            if (StringUtil.isNotEmpty(seqNo)) {
                if (!StringUtil.equals(item.getSeqNo(), seqNo)) {
                    return false;
                }
            }
            return true;
        });
    }



    /**
     * selectForPage
     * @param pageParam
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<LgdSpecialConfigDto> selectForPage(Map<String, Object> pageParam) throws Exception {
        Page<LgdSpecialConfigDto> pageList = restTemplate.postForPage(ServiceApplication.LOSS_MODEL_SERVICE_NAME+"/LgdSpecialConfig/findPage", pageParam, LgdSpecialConfigDto.class);
        for(LgdSpecialConfigDto mpd:pageList.getRoot()){
            //组装数据
//            if(StringUtils.isNotBlank(mpd.getGradeOrgId())){
//                mpd.setGradeOrgName(SysDataUtil.getNameFromDataDict(DictCode.评级机构,mpd.getGradeOrgId()));//组装评级机构名称
//            }
//            if(StringUtils.isNotBlank(mpd.getoType())){
//                mpd.setoTypeName(SysDataUtil.getNameFromDataDict(DictCode.评级类型,mpd.getoType()));
//            }

        }
        return pageList;
    }



    /**
     * 根据主键查询
     * @param
     * @return LgdSpecialConfigDto 实体类
     */
    public LgdSpecialConfigDto selectByPrimaryKey(LgdSpecialConfigDto dto) {
        return restTemplate.postForObject(
                ServiceApplication.LOSS_MODEL_SERVICE_NAME +"/LgdSpecialConfig/selectByPrimaryKey",
                dto, LgdSpecialConfigDto.class);
    }



    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public LgdSpecialConfigDto selectByKey(LgdSpecialConfigDto dto) {
        return restTemplate.postForObject(
                ServiceApplication.LOSS_MODEL_SERVICE_NAME +"/LgdSpecialConfig/selectByKey",
                dto,LgdSpecialConfigDto.class);
    }
    /**
     * 查询需要导出的数据
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public List<LgdSpecialConfigDto> selectExportList(LgdSpecialConfigDto dto) {
        List<LgdSpecialConfigDto> listAll =
                restTemplate.postForList(ServiceApplication.LOSS_MODEL_SERVICE_NAME+"/LgdSpecialConfig/selectExportList",dto,LgdSpecialConfigDto.class);
        for (LgdSpecialConfigDto dtoo : listAll) {
//            //组装数据
//            if(StringUtils.isNotBlank(dtoo.getGradeOrgId())){
//                dtoo.setGradeOrgName(SysDataUtil.getNameFromDataDict(DictCode.评级机构,dtoo.getGradeOrgId()));//组装评级机构名称
//            }
//            if(StringUtils.isNotBlank(dtoo.getoType())){
//                dtoo.setoTypeName(SysDataUtil.getNameFromDataDict(DictCode.评级类型,dtoo.getoType()));
//            }
        }
        return listAll;
    }



    @Override
    public void doComplete(LgdSpecialConfigDto entity, OperationType type) {
        String action = StringUtil.EMPTY;
        if (OperationType.DELETE.equals(type)) {
            action = "delete";
        } else if (OperationType.NEW.equals(type)) {
            action = "insert";
        } else {
            action = "update";
        }
        restTemplate.postForObject(ServiceApplication.LOSS_MODEL_SERVICE_NAME+"/LgdSpecialConfig/"+ action,
                entity, Integer.class);

    }






    /**
     * 根据Map获取评级PD信息
     * @return
     */
    public List<LgdSpecialConfigDto> selectByMap(Map<String,Object> params){
        List<LgdSpecialConfigDto> lgdSpecialConfigDtoList = restTemplate.getForList(
                ServiceApplication.LOSS_MODEL_SERVICE_NAME + "/LgdSpecialConfig/selectByMap"
                        + params,
                LgdSpecialConfigDto.class);
        return  lgdSpecialConfigDtoList;
    }



    public Boolean getRepeatCheck(LgdSpecialConfigDto dto){
        return restTemplate.postForObject(
                ServiceApplication.LOSS_MODEL_SERVICE_NAME +"/LgdSpecialConfig/getRepeatCheck",
                dto,Boolean.class);
    }


    public List<SelectOptionDto> getFinprodIdByLossType(Map<String, Object> params){
        return restTemplate.postForList(
                ServiceApplication.LOSS_MODEL_SERVICE_NAME +"/LgdSpecialConfig/getFinprodIdByLossType",
                params,SelectOptionDto.class);
    }

    public Page<SelectOptionDto> getAssetInfoSelectOption(Map<String, Object> param) {
        // 去除回购类资产
        param.put("notInAssetType", DictCodeListExt1.证券品种_回购.getValue());
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME+"/LossModCommon/getModAssetInfoPage", param, SelectOptionDto.class);
    }





}
