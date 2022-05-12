package com.joyintech.tams.touguan.system.bond.special.service;

import com.google.common.collect.Lists;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.common.base.dao.FinProductSpecialMapper;
import com.joyintech.tams.common.base.dao.WflFinProductSpecialMapper;
import com.joyintech.tams.common.base.entity.FinProductSpecial;
import com.joyintech.tams.common.base.entity.WflFinProductSpecial;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.touguan.system.bond.special.dao.FinProductSpecialAssetMapper;
import com.joyintech.tams.touguan.system.bond.special.dto.FinProductSpecialDto;
import com.joyintech.tams.touguan.system.bond.special.dto.FinProductSpecialImportDto;
import com.joyintech.tams.touguan.system.bond.special.dto.FinProductSpecialSelectOptionDto;
import com.joyintech.tams.touguan.system.bond.special.dto.req.FinProductSpecialQueryRequest;
import com.joyintech.tams.touguan.system.bond.special.dto.req.FinProductSpecialSelectOptionQueryRequest;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class FinProductSpecialService extends BaseService<FinProductSpecialDto> implements BaseProcessService<FinProductSpecialDto> {

    @Autowired
    private FinProductSpecialAssetMapper finProductSpecialAssetMapper;
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
                () -> finProductSpecialAssetMapper.queryForPage(request));
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

    /**
     * 根据债券简称查询债券代码
     *
     * @param finProdAbbr
     * @return
     */
    private String queryFinProdMarketIdByAbbr(String finProdAbbr) {
        FinProductSpecialSelectOptionQueryRequest request = new FinProductSpecialSelectOptionQueryRequest();
        request.setSearch(finProdAbbr);
        FinProductSpecialSelectOptionDto finProductSpecialSelectOptionDto = finProductSpecialAssetMapper.querySampleFinProduct(request);
        if (Objects.nonNull(finProductSpecialSelectOptionDto)) {
            return finProductSpecialSelectOptionDto.getFinprodMarketId();
        } else {
            finProductSpecialSelectOptionDto = finProductSpecialAssetMapper.querySampleInterf(request);
            if (Objects.nonNull(finProductSpecialSelectOptionDto)) {
                return finProductSpecialSelectOptionDto.getFinprodMarketId();
            }
        }
        return null;
    }
}
