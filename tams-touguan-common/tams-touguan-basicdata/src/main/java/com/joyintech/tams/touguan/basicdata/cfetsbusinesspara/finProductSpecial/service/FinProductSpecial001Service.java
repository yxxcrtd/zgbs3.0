package com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.service;

import com.google.common.collect.Lists;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.common.base.dao.FinProductSpecialMapper;
import com.joyintech.tams.common.base.dao.WflFinProductSpecialMapper;
import com.joyintech.tams.common.base.entity.FinProductSpecial;
import com.joyintech.tams.common.base.entity.WflFinProductSpecial;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dao.FinProductSpecial001Mapper;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.FinProductSpecialDto;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.FinProductSpecialSelectOptionDto;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.req.FinProductSpecialQueryRequest;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.req.FinProductSpecialSelectOptionQueryRequest;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 债券特殊信息service
 *
 * @author zhuys
 * @date 2022-05-06
 */
@Service
public class FinProductSpecial001Service extends BaseService<FinProductSpecialDto> implements BaseProcessService<FinProductSpecialDto> {

    @Autowired
    private FinProductSpecial001Mapper finProductSpecial001Mapper;
    @Autowired
    private FinProductSpecialMapper finProductSpecialMapper;
    @Autowired
    private WflFinProductSpecialMapper wflFinProductSpecialMapper;
    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 特殊债券信息分页查询
     *
     * @param request
     * @return
     */
    public Page<FinProductSpecialDto> queryForPage(FinProductSpecialQueryRequest request) {
        Page<FinProductSpecialDto> pageList = super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(),
                () -> finProductSpecial001Mapper.queryForPage(request));
        return pageList;
    }

    /**
     * 债券信息下拉查询
     *
     * @param request
     * @return
     */
    public List<FinProductSpecialSelectOptionDto> querySelectOption(FinProductSpecialSelectOptionQueryRequest request) {
        return finProductSpecial001Mapper.querySelectOption(request);
    }


    /**
     * 判断金融产品代码是否使用过
     *
     * @param finprodId
     * @return
     */
    public Integer countFinprodIdProcessing(String finprodId) {
        return finProductSpecial001Mapper.countFinprodIdProcessing(finprodId, iPlatformDate.getSysDate());
    }

    @Override
    public void doComplete(FinProductSpecialDto entity, OperationType type) {
        //删除流程表
        if (Objects.nonNull(type)) {
            delWflData(entity.getWorkflowId());
        }
        //记录业务表
        insertData(entity);
    }

    /**
     * 处理发起流程逻辑
     *
     * @param dto
     * @param type
     */
    @Override
    public void doInsert(FinProductSpecialDto dto, OperationType type) {
        //记录流程表
        insertWflData(dto);
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
     * 删除流程表
     *
     * @param workflowId
     */
    private void delWflData(String workflowId) {
        wflFinProductSpecialMapper.deleteByWorkflowId(workflowId);
    }

    /**
     * 流程表数据入库
     *
     * @param entity
     */
    private void insertWflData(FinProductSpecialDto entity) {
        WflFinProductSpecial wflFinProductSpecial = new WflFinProductSpecial();
        BeanUtil.copyPropertiesIgnoreNull(wflFinProductSpecial, entity);
        wflFinProductSpecial.setInputType(DictCodeListExt1.录入方式_手工录入.getValue());
        wflFinProductSpecial.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        wflFinProductSpecialMapper.insertSelective(wflFinProductSpecial);
    }

    /**
     * 文件导入
     *
     * @param fileName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ExcelValidDto> importData(String fileName) {
        List<List<String>> dataList = ExcelUtil.getDataList(fileName);
        List<FinProductSpecialDto> finProductSpecialDtos = convertToBean(dataList);
        finProductSpecialDtos.stream().forEach(finProductSpecialDto -> doComplete(finProductSpecialDto, null));
        List<ExcelValidDto> excelValidDtos = Lists.newArrayList();
        return excelValidDtos;
    }

    /**
     * 导入数据转换
     *
     * @param dataList
     * @return
     */
    private List<FinProductSpecialDto> convertToBean(List<List<String>> dataList) {
        List<FinProductSpecialDto> entities = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            List<String> list = dataList.get(i);
            FinProductSpecialDto entity = new FinProductSpecialDto();
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
