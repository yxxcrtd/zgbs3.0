package com.joyintech.tams.touguan.system.bond.special.service;

import com.google.common.collect.Lists;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductSpecialMapper;
import com.joyintech.tams.common.base.dao.WflFinProductSpecialMapper;
import com.joyintech.tams.common.base.entity.FinProductSpecial;
import com.joyintech.tams.common.base.entity.WflFinProductSpecial;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.touguan.system.bond.special.dao.FinProductSpecialAssetMapper;
import com.joyintech.tams.touguan.system.bond.special.dto.FinProductSpecialDto;
import com.joyintech.tams.touguan.system.bond.special.dto.FinProductSpecialImportDto;
import com.joyintech.tams.touguan.system.bond.special.dto.FinProductSpecialSelectOptionDto;
import com.joyintech.tams.touguan.system.bond.special.dto.req.FinProductSpecialQueryRequest;
import com.joyintech.tams.touguan.system.bond.special.dto.req.FinProductSpecialSelectOptionQueryRequest;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 债券特殊信息service
 *
 * @author zhuys
 * @date 2022-05-06
 */
@Service
public class FinProductSpecialService extends BaseService<FinProductSpecialDto> implements BaseProcessService<FinProductSpecialDto> {

    private static final String NO_INFO_SHOW_OUT = "noInfoShowOut";

    private static final Integer IMPORT_MAXIMUM_SIZE = 100;


    @Autowired
    private FinProductSpecialAssetMapper finProductSpecialAssetMapper;
    @Autowired
    private FinProductSpecialMapper finProductSpecialMapper;
    @Autowired
    private WflFinProductSpecialMapper wflFinProductSpecialMapper;
    @Autowired
    private IPlatformDate iPlatformDate;

    @Override
    public void doComplete(FinProductSpecialDto entity, OperationType type) {
        if (type == OperationType.NEW) {

        } else if (type == OperationType.MODIFY) {

        }
    }

    /**
     * 处理发起流程逻辑
     *
     * @param dto
     * @param type
     */
    @Override
    public void doInsert(FinProductSpecialDto dto, OperationType type) {
        if ("save".equals(dto.getOperateType())) {
            return;
        }
    }

    /**
     * 特殊债券信息分页查询
     *
     * @param request
     * @return
     */
    public Page<FinProductSpecialDto> queryForPage(FinProductSpecialQueryRequest request) {
        Page<FinProductSpecialDto> pageList = super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(), () -> finProductSpecialAssetMapper.queryForPage(request));
        return pageList;
    }

    /**
     * 债券信息下拉查询
     *
     * @param request
     * @return
     */
    public List<FinProductSpecialSelectOptionDto> querySelectOption(FinProductSpecialSelectOptionQueryRequest request) {
        return finProductSpecialAssetMapper.querySelectOption(request);
    }


    /**
     * 判断金融产品代码是否使用过
     *
     * @param finprodId
     * @return
     */
    public Integer countFinprodIdProcessing(String finprodId) {
        return finProductSpecialAssetMapper.countFinprodIdProcessing(finprodId, iPlatformDate.getSysDate());
    }


    /**
     * 业务表表数据入库
     *
     * @param entity
     */
    private void insertData(FinProductSpecialDto entity) {
        FinProductSpecial finProductSpecial = new FinProductSpecial();
        BeanUtil.copyPropertiesIgnoreNull(finProductSpecial, entity);
        finProductSpecialMapper.insertSelective(finProductSpecial);
    }


    /**
     * 流程表数据入库
     *
     * @param entity
     */
    private void insertWflData(FinProductSpecialDto entity) {
        WflFinProductSpecial wflFinProductSpecial = new WflFinProductSpecial();
        BeanUtil.copyPropertiesIgnoreNull(wflFinProductSpecial, entity);
        wflFinProductSpecial.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        wflFinProductSpecialMapper.insertSelective(wflFinProductSpecial);
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
        if (!StringUtil.isEmptyList(dataList)) {
            if (CollectionUtils.isNotEmpty(dataList)) {
                if (dataList.size() > IMPORT_MAXIMUM_SIZE) {
                    result.setMessage("单次最多导入100条！", MessageType.错误消息);
                    return result;
                }
                List<FinProductSpecialImportDto> finProductSpecialImportDtos = convertToBean(dataList);

                for (int i = 0; i < finProductSpecialImportDtos.size(); i++) {

//                    提示：{0}必须输入（行号为{1}）。
//                    有{2}条记录导入失败。请确认是否继续？
//                    0：文件中为空的列
//                    1：行号
//                    2：错误记录条数

                    FinProductSpecialImportDto finProductSpecialImportDto = finProductSpecialImportDtos.get(i);
                    if (DictCodeList.是否_否.getCodeName().equals(finProductSpecialImportDto.getIsDefaultBondIndicator())
                            && DictCodeList.是否_否.getCodeName().equals(finProductSpecialImportDto.getIsAbs())
                            && DictCodeList.是否_否.getCodeName().equals(finProductSpecialImportDto.getIsSpecialDebt())) {
                        result.setMessage("第" + (i + 2) + "行特殊债券标识至少其中一个为是!", MessageType.错误消息);
                        return result;
                    }
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
     * 文件导入
     *
     * @param fileName
     * @return
     */
    public List<ExcelValidDto> importData(String fileName) {
        List<ExcelValidDto> excelValidDtos = Lists.newArrayList();
        return excelValidDtos;
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
