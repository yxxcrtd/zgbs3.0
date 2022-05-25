package com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.service;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.FinProductSpecialDto;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.FinProductSpecialImportDto;
import com.joyintech.workflow.engine.constant.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 债券特殊信息 check service
 *
 * @author zhuys
 * @date 2022-05-06
 */
@Component
public class FinProductSpecialValidatorService implements BeanValidator {

    private static final String NO_INFO_SHOW_OUT = "noInfoShowOut";

    private static final Integer IMPORT_MAXIMUM_SIZE = 100;
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;
    @Autowired
    private FinProductSpecial001Service finProductSpecial001Service;
    @Resource
    private FinProductMapper finProductMapper;

    @Override
    public List<ValidMessage> valid(Object value) {
        List<ValidMessage> resultList = new ArrayList<>();
        FinProductSpecialDto dto = (FinProductSpecialDto) value;
        if (!dto.getOperateType().equals(OperationType.DELETE.name())) {
            if (StringUtil.isBlank(dto.getFinprodMarketId())) {
                resultList.add(new ValidMessage("【债券代码】必填!"));
            }
            if (StringUtil.isBlank(dto.getIsAbs())) {
                resultList.add(new ValidMessage("【是否资产支持证券】必填!"));
            }
            if (StringUtil.isBlank(dto.getIsSpecialDebt())) {
                resultList.add(new ValidMessage("【是否特殊债】必填!"));
            }
            if (StringUtil.isBlank(dto.getIsDefaultBondIndicator())) {
                resultList.add(new ValidMessage("【是否到期违约债】必填!"));
            }
            if (DictCodeList.是否_否.getValue().equals(dto.getIsDefaultBondIndicator())
                    && DictCodeList.是否_否.getValue().equals(dto.getIsAbs())
                    && DictCodeList.是否_否.getValue().equals(dto.getIsSpecialDebt())) {
                resultList.add(new ValidMessage("特殊债券标识至少其中一个为是!"));
            }
        }
        if (dto.getOperateType().equals(OperationType.NEW.name())) {
            WflValidInputParameter parameter = new WflValidInputParameter();
            parameter.setPageId("CfetsBasicInfo001");
            parameter.setBusinessDataPk1(dto.getFinprodMarketId());
            ProcessMark dataEntityFromProcessMark = mstWorkflowCommonService.getDataEntityFromProcessMark(parameter);
            if (Objects.nonNull(dataEntityFromProcessMark)) {
                resultList.add(new ValidMessage("", "资产存在流程中数据,不可新建"));
                return resultList;
            }
        }
        //判断特殊资产维护是否存在流程中数据
        if (dto.getOperateType().equals(OperationType.DELETE.name())) {
            WflValidInputParameter parameter = new WflValidInputParameter();
            parameter.setPageId("CfetsBasicInfo001");
            parameter.setBusinessDataPk1(dto.getFinprodMarketId());
            ProcessMark dataEntityFromProcessMark = mstWorkflowCommonService.getDataEntityFromProcessMark(parameter);
            if (Objects.nonNull(dataEntityFromProcessMark)) {
                String businessData = dataEntityFromProcessMark.getBusinessData();
                BaseMap responseMap = JsonUtils.json2Object(businessData, BaseMap.class);
                resultList.add(new ValidMessage("", "资产存在流程中数据,不可删除! 流程编号：【" + responseMap.getString("workflowId") + "】"));
                return resultList;
            }
        }

        if (dto.getOperateType().equals(OperationType.MODIFY.name()) || dto.getOperateType().equals(OperationType.DELETE.name())) {
            String finprodMarketId = dto.getFinprodMarketId();
            FinProduct finProduct = finProductMapper.selectByFinprodMarketId(finprodMarketId);
            if (Objects.nonNull(finProduct) && Objects.nonNull(finProduct.getFinprodId())) {
                finprodMarketId = finProduct.getFinprodId();
            }
            //判断是否流程中的数据
            Integer countFinProdIdProcessing = finProductSpecial001Service.countFinprodIdProcessing(finprodMarketId);
            if (countFinProdIdProcessing > 0) {
                String message = dto.getOperateType().equals(OperationType.MODIFY.name()) ? "修改" : "删除";
                resultList.add(new ValidMessage("存在有效或流程中的当日指令及交易数据，不允许"+message));
            }
        }
        return resultList;
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
                Integer failNum = 0;
                StringBuilder allErrorMsg = new StringBuilder();
                StringBuilder allConfirmErrorMsg = new StringBuilder();
                for (int i = 0; i < finProductSpecialImportDtos.size(); i++) {
                    int j = i + 2;
                    FinProductSpecialImportDto finProductSpecialImportDto = finProductSpecialImportDtos.get(i);
                    if (DictCodeList.是否_否.getCodeName().equals(finProductSpecialImportDto.getIsDefaultBondIndicator()) && DictCodeList.是否_否.getCodeName().equals(finProductSpecialImportDto.getIsAbs()) && DictCodeList.是否_否.getCodeName().equals(finProductSpecialImportDto.getIsSpecialDebt())) {
                        result.setMessage(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_MAXIMUM_SIZE", String.valueOf(j)), MessageType.错误消息);
                        return result;
                    }

                    //确认消息
                    StringBuilder confirmErrorMsg = new StringBuilder();
                    //错误消息
                    StringBuilder errorMsg = new StringBuilder();
                    if (com.joyintech.framework.util.StringUtil.isBlank(finProductSpecialImportDto.getIsSpecialDebt())) {
                        confirmErrorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_REQUIRED", "是否特殊债", String.valueOf(j))).append("\n");
                    }
                    if (com.joyintech.framework.util.StringUtil.isBlank(finProductSpecialImportDto.getIsAbs())) {
                        confirmErrorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_REQUIRED", "是否资产支持证券", String.valueOf(j))).append("\n");
                    }
                    if (com.joyintech.framework.util.StringUtil.isBlank(finProductSpecialImportDto.getIsDefaultBondIndicator())) {
                        confirmErrorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_REQUIRED", "是否到期违约债", String.valueOf(j))).append("\n");
                    }
                    if (com.joyintech.framework.util.StringUtil.isBlank(finProductSpecialImportDto.getFinprodMarketId()) && com.joyintech.framework.util.StringUtil.isBlank(finProductSpecialImportDto.getFinprodAbbr())) {
                        confirmErrorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_REQUIRED", "债券代码/债券代码", String.valueOf(j))).append("\n");
                    }
                    if (com.joyintech.framework.util.StringUtil.isNotBlank(confirmErrorMsg)) {
                        allConfirmErrorMsg.append(confirmErrorMsg);
                        failNum++;
                    }
                }
                if (com.joyintech.framework.util.StringUtil.isNotBlank(allConfirmErrorMsg)) {
                    allConfirmErrorMsg.append(this.getMessage("FIN_PRODUCT_SPECIAL_IMPORT_FAIL", String.valueOf(failNum)));
                    result.setMessage(allConfirmErrorMsg.toString(), MessageType.确认消息);
                    return result;
                }
            } else {
                result.setData(NO_INFO_SHOW_OUT);
            }
        } else {
            result.setMessage("文件内容无有效数据！", MessageType.错误消息);
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
            entity.setSerialNumber(list.get(0));
            entity.setFinprodMarketId(list.get(1));
            entity.setFinprodAbbr(list.get(2));
            entity.setIsAbs(list.get(3));
            entity.setIsSpecialDebt(list.get(4));
            entity.setIsDefaultBondIndicator(list.get(5));
            entities.add(entity);
        }
        return entities;
    }
}