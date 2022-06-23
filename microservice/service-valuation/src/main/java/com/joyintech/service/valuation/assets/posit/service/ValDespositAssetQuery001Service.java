package com.joyintech.service.valuation.assets.posit.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.assets.posit.dao.ValDespositAssetQuery001Mapper;
import com.joyintech.service.valuation.assets.posit.dto.ValDespositAssetQuery001Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 存款类资产查询
 * 
 * @author wangtingting
 * @version 1.0 2018年7月19日
 * @see ValDespositAssetQuery001Service
 * @since 1.0
 */
@Service(value="serviceValDespositAssetQuery001Service")

public class ValDespositAssetQuery001Service extends CommonExportService<ValDespositAssetQuery001Dto> {

    /**
     * ValDespositAssetQuery001Mapper
     */
    @Autowired
    private ValDespositAssetQuery001Mapper mapper;

  

    /**
     * 根据条件获取表格信息
     * 
     * @param params Map<String, Object>
     * @return Page<ValDespositAssetQuery001Dto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<ValDespositAssetQuery001Dto> findPage(int page, int length, Map<String, Object> params){

        String finprodId = (String)params.get("finprodId");

        // String profitType = (String)params.get("profitType");
        String[] finprodIdArr = null;
        if (StringUtil.isNotEmpty(finprodId)) {
            finprodIdArr = finprodId.split(",");
        }
        params.put("finprodIdArr", finprodIdArr);
        // params.put("profitTypeArr", profitTypeArr);
        params.put("finprodType", DictCodeListExt1.金融产品类型_存款.getValue());
        return super.queryForPageForSelectReturn(page, length, () -> mapper.queryPageList(params));
    }

    /**
     * 下拉框获取：资产
     * 
     * @param params Map<String, Object>
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryValFinproId(Map<String, Object> params) {
        params.put("finprodType", DictCodeListExt1.金融产品类型_存款.getValue());// 仅查询存款型资产信息
        List<ValDespositAssetQuery001Dto> resultList = mapper.queryPageList(params);
        List<SelectOptionDto> selectOptionDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (ValDespositAssetQuery001Dto single : resultList) { // 组装select节点对象
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(single.getFinprodId());
                if (StringUtils.isEmpty(single.getFinprodAbbr())) {
                    selectOptionDto.setText(single.getFinprodName());
                }
                else {
                    selectOptionDto.setText(single.getFinprodAbbr());
                }
                // selectOptionDto.setText(single.getFinprodId());
                selectOptionDtoList.add(selectOptionDto);
            }
        }
        return selectOptionDtoList;
    }

    /**
     * 格式化创建SelectOptionDto对象 <功能详细描述>
     * 
     * @param selectId 下拉Id值
     * @param text 下拉文本值
     * @return dto
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
     * 导出功能
     * 
     * @param params Map<String, Object>
     * @return List<ValDespositAssetQuery001Dto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public List<ValDespositAssetQuery001Dto> findAll(Map<String, Object> params){
        params.put("finprodType", DictCodeListExt1.金融产品类型_存款.getValue());
        return mapper.queryPageList(params);

    }

    /**
     * checkFinprodId
     * 
     * @param finprodId finprodId
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean checkFinprodId(String finprodId) {
        // int count = mapper.checkFinprodId(finprodId);
        // if (count == 0) {
        // return true;
        // }
        // else {
        return mapper.checkFinprodId(finprodId);
        // }

    }

    /**
     * 删除
     * 
     * @param finprodId finprodId
     * @return int
     * @see
     * @since 1.0
     */
    public int delete(String finprodId) {
        // FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
        // FinProductKey finProductKey =new FinProductKey();
        // finProductTypeKey.setFinprodId(finprodId);
        // finProductTypeKey.setFinprodType(finprodType);
        // finProductKey.setFinprodId(finprodId);
        // finProductKey.setFinprodType(finprodType);
        // 金融产品分类表
        int count = 0;
        String finprodType = DictCodeListExt1.金融产品类型_存款.getValue();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", finprodId);
        map.put("finprodType", finprodType);
        // count+=finProductMapper.deleteByPrimaryKey(finProductKey);
        count += mapper.deleteFinProductType(map);
        count += mapper.deleteFinProduct(map);
        count += mapper.deleteFinCashCalcRule(map);
        count += mapper.deleteFinCashInfo(map);
        count += mapper.deleteFinCashPayRule(map);
        count += mapper.deleteFinCashPlan(map);
        return count;

    }

}
