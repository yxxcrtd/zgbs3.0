package com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dao.FinProductSpecial001Mapper;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.FinProductSpecialDto;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.FinProductSpecialImportDto;
import com.joyintech.workflow.engine.constant.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 债券特殊信息 check service
 *
 * @author zhuys
 * @date 2022-05-06
 */
@Component
public class FinProductSpecialValidatorService implements BeanValidator {

    private static final String NO_INFO_SHOW_OUT = "noInfoShowOut";

    private static final String CHANGE_LINE_MARK = "<br/>";

    private static final String PAGE_ID = "CfetsBasicInfo002";

    private static final Integer IMPORT_MAXIMUM_SIZE = 100;
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;
    @Autowired
    private FinProductSpecial001Service finProductSpecial001Service;
    @Autowired
    private FinProductSpecial001Mapper finProductSpecial001Mapper;
    @Resource
    private FinProductMapper finProductMapper;

    @Override
    public List<ValidMessage> valid(Object value) {
        return null;
    }

    /**
     * 自定义校验
     *
     * @param dto
     * @return
     */
    public AjaxResult validCustom(FinProductSpecialDto dto) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> resultList = new ArrayList<>();
        if (!dto.getOperateType().equals(OperationType.DELETE.name())) {
            if (StringUtil.isBlank(dto.getFinprodMarketId())) {
                resultList.add(new ValidMessage("【债券代码】必填!\n\r"));
            }
            if (StringUtil.isBlank(dto.getIsAbs())) {
                resultList.add(new ValidMessage("【是否资产支持证券】必填!\n\r"));
            }
            if (StringUtil.isBlank(dto.getIsSpecialDebt())) {
                resultList.add(new ValidMessage("【是否特殊债】必填!\n\r"));
            }
            if (StringUtil.isBlank(dto.getIsDefaultBondIndicator())) {
                resultList.add(new ValidMessage("【是否到期违约债】必填!\n\r"));
            }
            if (DictCodeList.是否_否.getValue().equals(dto.getIsDefaultBondIndicator()) && DictCodeList.是否_否.getValue().equals(dto.getIsAbs()) && DictCodeList.是否_否.getValue().equals(dto.getIsSpecialDebt())) {
                resultList.add(new ValidMessage("特殊债券标识至少其中一个为是!\n\r"));
            }
            if (!CollectionUtils.isEmpty(resultList)) {
                ajaxResult.setErrorList(resultList);
                ajaxResult.setMessageType(MessageType.错误消息);
                return ajaxResult;
            }
        }
        //校验流程中数据
        WflValidInputParameter parameter = new WflValidInputParameter();
        parameter.setPageId(PAGE_ID);
        parameter.setBusinessDataPk1(dto.getFinprodMarketId());
        ProcessMark dataEntityFromProcessMark = mstWorkflowCommonService.getDataEntityFromProcessMark(parameter);
        if (Objects.nonNull(dataEntityFromProcessMark)) {
            resultList.add(new ValidMessage("", "资产存在流程中数据,不允许操作"));
            ajaxResult.setErrorList(resultList);
            ajaxResult.setMessageType(MessageType.错误消息);
            return ajaxResult;
        }

        //判断特殊资产维护是否存在流程中数据
        if (dto.getOperateType().equals(OperationType.DELETE.name())) {
            String finprodMarketId = dto.getFinprodMarketId();
            FinProduct finProduct = finProductMapper.selectByFinprodMarketId(finprodMarketId);
            if (Objects.nonNull(finProduct) && Objects.nonNull(finProduct.getFinprodId())) {
                finprodMarketId = finProduct.getFinprodId();
            }
            //判断是否存在交易数据
            Integer countFinProdIdProcessing = finProductSpecial001Service.countFinprodIdProcessing(finprodMarketId);
            if (countFinProdIdProcessing > 0) {
                resultList.add(new ValidMessage("存在有效或流程中的指令及交易数据，不允许删除"));
            }
            ajaxResult.setErrorList(resultList);
            ajaxResult.setMessageType(MessageType.错误消息);
            return ajaxResult;
        }

        if (dto.getOperateType().equals(OperationType.MODIFY.name())) {
            String finprodMarketId = dto.getFinprodMarketId();
            FinProduct finProduct = finProductMapper.selectByFinprodMarketId(finprodMarketId);
            if (Objects.nonNull(finProduct) && Objects.nonNull(finProduct.getFinprodId())) {
                finprodMarketId = finProduct.getFinprodId();
            }
            //判断是否流程中的数据
            Integer countFinProdIdProcessingAndDate = finProductSpecial001Service.countFinprodIdProcessingAndDate(finprodMarketId);
            Integer countFinProdIdProcessing = finProductSpecial001Service.countFinprodIdProcessing(finprodMarketId);
            if (countFinProdIdProcessingAndDate > 0) {
                resultList.add(new ValidMessage("存在有效或流程中的当日指令及交易数据，不允许修改"));
                ajaxResult.setErrorList(resultList);
                ajaxResult.setMessageType(MessageType.错误消息);
            } else if (countFinProdIdProcessing > 0) {
                resultList.add(new ValidMessage("已存在成交数据，是否修改？"));
                ajaxResult.setErrorList(resultList);
                ajaxResult.setMessageType(MessageType.警告消息);
            }
            return ajaxResult;
        }
        return ajaxResult;
    }


    /**
     * 文件校验
     *
     * @param fileName
     * @return
     */
    public AjaxResult checkConfirmMsgForImportData(String fileName) {
        List<List<String>> dataList = ExcelUtil.getDataList(fileName);
        AjaxResult result = new AjaxResult();
        result.setData(dataList);
        if (!com.joyintech.framework.util.StringUtil.isEmptyList(dataList)) {
            if (!CollectionUtils.isEmpty(dataList)) {
                if (dataList.size() > IMPORT_MAXIMUM_SIZE) {
                    result.setMessage("单次最多导入100条！", MessageType.错误消息);
                    return result;
                }
                //对象转换
                List<FinProductSpecialImportDto> finProductSpecialImportDtos = convertToBean(dataList);
                StringBuilder allErrorMsg = new StringBuilder();
                StringBuilder allConfirmErrorMsg = new StringBuilder();
                for (int i = 0; i < finProductSpecialImportDtos.size(); i++) {
                    int j = i + 2;
                    //确认消息
                    StringBuilder confirmErrorMsg = new StringBuilder();
                    //错误消息
                    StringBuilder errorMsg = new StringBuilder();
                    FinProductSpecialImportDto finProductSpecialImportDto = finProductSpecialImportDtos.get(i);
                    if (DictCodeList.是否_否.getCodeName().equals(finProductSpecialImportDto.getIsDefaultBondIndicator()) && DictCodeList.是否_否.getCodeName().equals(finProductSpecialImportDto.getIsAbs()) && DictCodeList.是否_否.getCodeName().equals(finProductSpecialImportDto.getIsSpecialDebt())) {
                        errorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_IS", String.valueOf(j))).append(CHANGE_LINE_MARK);
                    }
                    if (StringUtil.isBlank(finProductSpecialImportDto.getIsSpecialDebt())) {
                        errorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_REQUIRED", "是否特殊债", String.valueOf(j))).append(CHANGE_LINE_MARK);
                    }
                    if (StringUtil.isBlank(finProductSpecialImportDto.getIsAbs())) {
                        errorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_REQUIRED", "是否资产支持证券", String.valueOf(j))).append(CHANGE_LINE_MARK);
                    }
                    if (StringUtil.isBlank(finProductSpecialImportDto.getIsDefaultBondIndicator())) {
                        errorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_REQUIRED", "是否到期违约债", String.valueOf(j))).append(CHANGE_LINE_MARK);
                    }
                    if (StringUtil.isBlank(finProductSpecialImportDto.getFinprodMarketId()) && StringUtil.isBlank(finProductSpecialImportDto.getFinprodAbbr())) {
                        errorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_REQUIRED", "债券代码/债券代码", String.valueOf(j))).append(CHANGE_LINE_MARK);
                    }
                    if (StringUtil.isNotBlank(finProductSpecialImportDto.getIsSpecialDebt()) && (!finProductSpecialImportDto.getIsSpecialDebt().equals(DictCodeList.是否_否.getCodeName()) && !finProductSpecialImportDto.getIsSpecialDebt().equals(DictCodeList.是否_是.getCodeName()))) {
                        errorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_MEET_THE_REQUIREMENTS", "是否特殊债", String.valueOf(j))).append(CHANGE_LINE_MARK);
                    }
                    if (StringUtil.isNotBlank(finProductSpecialImportDto.getIsAbs()) && (!finProductSpecialImportDto.getIsAbs().equals(DictCodeList.是否_否.getCodeName()) && !finProductSpecialImportDto.getIsAbs().equals(DictCodeList.是否_是.getCodeName()))) {
                        errorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_MEET_THE_REQUIREMENTS", "是否资产支持证券", String.valueOf(j))).append(CHANGE_LINE_MARK);
                    }
                    if (StringUtil.isNotBlank(finProductSpecialImportDto.getIsDefaultBondIndicator()) && (!finProductSpecialImportDto.getIsDefaultBondIndicator().equals(DictCodeList.是否_否.getCodeName()) && !finProductSpecialImportDto.getIsDefaultBondIndicator().equals(DictCodeList.是否_是.getCodeName()))) {
                        errorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_MEET_THE_REQUIREMENTS", "是否到期违约债", String.valueOf(j))).append(CHANGE_LINE_MARK);
                    }
                    String search = finProductSpecialImportDto.getFinprodMarketId();
                    if (StringUtil.isBlank(search)) {
                        search = finProductSpecialImportDto.getFinprodAbbr();
                    }
                    if (StringUtil.isNotBlank(search)) {
                        if (finProductSpecial001Mapper.countFinprodIdInProduct(search) == 0) {
                            errorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_BOND_NON_EXISTENT", String.valueOf(j))).append(CHANGE_LINE_MARK);
                        }
                    }

                    //校验流程中数据
                    String finprodMarketId = finProductSpecialImportDto.getFinprodMarketId();
                    if (StringUtil.isBlank(finprodMarketId)) {
                        finprodMarketId = finProductSpecial001Service.queryFinprodIdByAbbr(finProductSpecialImportDto.getFinprodAbbr());
                    }

                    if (finProductSpecial001Mapper.countFinprodIdForSpecial(finprodMarketId) > 0) {
                        confirmErrorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_BOND_ALREADY_EXISTS", String.valueOf(j))).append(CHANGE_LINE_MARK);
                    }

                    WflValidInputParameter parameter = new WflValidInputParameter();
                    parameter.setPageId(PAGE_ID);
                    parameter.setBusinessDataPk1(finprodMarketId);
                    ProcessMark dataEntityFromProcessMark = mstWorkflowCommonService.getDataEntityFromProcessMark(parameter);
                    if (Objects.nonNull(dataEntityFromProcessMark)) {
                        errorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_IN_PROCESS", String.valueOf(j))).append(CHANGE_LINE_MARK);
                    }

                    if (StringUtil.isNotBlank(errorMsg)) {
                        allErrorMsg.append(errorMsg);
                    }
                    if (StringUtil.isNotBlank(confirmErrorMsg)) {
                        allConfirmErrorMsg.append(confirmErrorMsg);
                    }
                    finProductSpecialImportDtos.get(i).setIndex(j);
                }
                //校验Excel中重复数据
                Map<String, List<FinProductSpecialImportDto>> collect = finProductSpecialImportDtos.stream().collect(Collectors.groupingBy(FinProductSpecialImportDto::getFinprodMarketId));
                for (Map.Entry<String, List<FinProductSpecialImportDto>> entry : collect.entrySet()) {
                    List<FinProductSpecialImportDto> value = entry.getValue();
                    if (!CollectionUtils.isEmpty(value) && value.size() > 1) {
                        StringBuilder stringBuilder = new StringBuilder();
                        value.stream().forEach(finProductSpecialImportDto -> {
                            Integer index = finProductSpecialImportDto.getIndex();
                            stringBuilder.append(index).append(",");
                        });
                        allErrorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_EXCEL_ALREADY_EXISTS", stringBuilder.toString())).append(CHANGE_LINE_MARK);
                    }
                }
                if (StringUtil.isNotBlank(allErrorMsg)) {
                    result.setMessage(allErrorMsg.toString(), MessageType.错误消息);
                    return result;
                }

                if (StringUtil.isNotBlank(allConfirmErrorMsg)) {
                    result.setMessage(allConfirmErrorMsg.toString(), MessageType.警告消息);
                    result.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    return result;
                }

            } else {
                result.setData(NO_INFO_SHOW_OUT);
            }
        } else {
            result.setMessage("导入文件为空！", MessageType.错误消息);
        }
        return result;
    }

    /**
     * 导入数据转换
     *
     * @param dataList
     * @return
     */
    private List<FinProductSpecialImportDto> convertToBean(List<List<String>> dataList) {
        List<FinProductSpecialImportDto> entities = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            List<String> list = dataList.get(i);
            FinProductSpecialImportDto entity = new FinProductSpecialImportDto();
            String id = list.get(1);
            String text = list.get(2);
            if (StringUtil.isBlank(id)) {
                id = finProductSpecial001Service.queryFinprodIdByAbbr(text);
            }
            entity.setSerialNumber(list.get(0));
            entity.setFinprodMarketId(id);
            entity.setFinprodAbbr(text);
            entity.setIsAbs(list.get(3));
            entity.setIsSpecialDebt(list.get(4));
            entity.setIsDefaultBondIndicator(list.get(5));
            entities.add(entity);
        }
        return entities;
    }
}