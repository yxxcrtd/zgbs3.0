/*
 * 文件名：ValSubjectSet001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：杨智
 * 修改时间：2018年5月17日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.accounadjustmanage.subjectset.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.valuation.accounadjustmanage.subjectset.dto.ValSubjectHandDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.accounadjustmanage.subjectset.dao.ValSubjectSet001Mapper;
import com.joyintech.tams.valuation.accounadjustmanage.subjectset.dto.ValBokSubjectDto;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;

@Service
@Transactional(rollbackFor = Exception.class)
public class ValSubjectSet001Service extends CommonExportService<ValBokSubjectDto> {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
//    @Autowired
//    private ValSubjectSet001Mapper valSubjectSet001Mapper;
    
//    /**
//     * 查询所有账套
//     * <br/> 用于所属账套控件初期化
//     * @return SelectOptionDto list
//     * @see
//     * @since 1.0
//     */
//    public List<SelectOptionDto> findBooksets(){
//        return valSubjectSet001Mapper.findBooksets();
//    }
    
    /**
     * 检索上级科目代码列表
     * 
     * @param param 过滤参数
     * @return 上级科目代码列表
     * @see 
     * @since 1.0
     */
    public List<SuggestDto> getFSubjectId(String param) {
        return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME+"/ValSubjectSet001/getFSubjectId", param, SuggestDto.class);
//        return queryForSuggest(valSubjectSet001Mapper::getFSubjectId, null, param);
    }
    /**
     * 获取科目文本输入框模糊查询
     * 
     * @param param 过滤参数
     * @return 上级科目代码列表
     * @see 
     * @since 1.0
     */
    public List<SuggestDto> getSubjectId(String param) {
        return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME+"/ValSubjectSet001/getSubjectId", param, SuggestDto.class);
//        return queryForSuggest(valSubjectSet001Mapper::getSubjectId, null, param);
    }
    
//    /**
//     * 获取科目文本输入框模糊查询 和 联动
//     * 
//     * @param param 过滤参数
//     * @return 上级科目代码列表
//     * @see 
//     * @since 1.0
//     */
//    public List<SuggestDto> getSubjectId2(Map<String, Object> map) {
//        return queryForSuggest(()-> valSubjectSet001Mapper.getSubjectId2(map), (String)map.get("param"), null);
//    } 
    
    /**
     * 获取科目文本输入框模糊查询 和 联动
     * 
     * @param param 过滤参数
     * @return 上级科目代码列表
     * @throws Exception 
     * @see 
     * @since 1.0
     */
    public List<SelectOptionDto> getSubjectId2(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME+"/ValSubjectSet001/getSubjectId2", map, SelectOptionDto.class);
//        List<SelectOptionDto> list = valSubjectSet001Mapper.getSubjectId2(map);
//        if(CollectionUtils.isNotEmpty(list)) {
//            list.forEach(item -> {
//                item.setId(item.getId());
//                item.setText(item.getId() + "（" + item.getText() + "）" );
//            });
//        }
//        return list;
    }
    
    /**
     * 获取科目文本输入框模糊查询 和 联动
     * 
     * @param param 过滤参数
     * @return 上级科目代码列表
     * @throws Exception 
     * @see 
     * @since 1.0
     */
    public List<SelectOptionDto> getSubjectLevel(Map<String, Object> map)  {
        return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME+"/ValSubjectSet001/getSubjectLevel", map, SelectOptionDto.class);
//        return valSubjectSet001Mapper.getSubjectLevel(map);
    }
    
    /**
     * 获取科目下拉框
     * @param hashMap
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryValBokSubject(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME+"/ValSubjectSet001/queryValBokSubject", map, SelectOptionDto.class);
        
//        String subjectLevel = (String) map.get("subjectLevel");
//        if(StringUtil.isNotEmpty(subjectLevel)){
//            // 若是传的4级，则查询三级科目
//            if(subjectLevel.equals("4")){
//                subjectLevel = "3";
//                map.put("subjectLevel", subjectLevel);
//            }
//            
//        }
//        return valSubjectSet001Mapper.queryValBokSubject(map);
    }
    
    /**
     * 检索科目列表
     * 
     * @param pageParam 过滤参数
     * @return 科目列表
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<ValBokSubjectDto> selectForPage(Map<String, Object> pageParam) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME+"/ValSubjectSet001/findPage", pageParam, ValBokSubjectDto.class);
//        return super.queryForPage(()-> valSubjectSet001Mapper.selectAll(pageParam));
    }
    /**
     * @author  殷亚
     * @description 根据主键检索科目
     * @date 2022-2-9 14:26
     * @param dto:
     * @return com.joyintech.tams.valuation.accounadjustmanage.subjectset.dto.ValBokSubjectDto
     */
    public ValBokSubjectDto selectValBokSubject(ValBokSubjectDto dto) {
        Map<String,Object> params = new HashMap<String,Object>();
        return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/ValSubjectSet002/selectValBokSubject", dto, ValBokSubjectDto.class);
    }

    /**
     * 检索上级科目代码列表(支持入参)
     * 
     * @param param 过滤参数
     * @return 上级科目代码列表
     * @see 
     * @since 1.0
     */
    public List<SuggestDto> getFSubjectId2(Map<String, Object> param) {
//        Map<String, Object> map =  Maps.newHashMap();
//        map.put("param", param);
        return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME+"/ValSubjectSet001/getFSubjectId2", param, SuggestDto.class);
//        return valSubjectSet001Mapper.getFSubjectId2(map);
    }
    
    
    /**
     * 批量删除（删除当前科目及其子科目）
     * @param selectedItem
     * @see
     * @since 1.0
     */
    public void deleteByBatch(List<ValBokSubjectDto> selectedItem) {
        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/ValSubjectSet001/deleteByBatch", selectedItem, boolean.class);
        
//        selectedItem.forEach(item -> {
//            valSubjectSet001Mapper.deleteByFSubjectId(item);
//        });
    }
   
    public List<SuggestDto> getBooksetId(Map<String, Object> param) {
        return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME+"/ValSubjectSet001/getBooksetId", param, SuggestDto.class);
    }
    
    public void save(ValBokSubjectDto dto) {
        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/ValSubjectSet002/save", dto, boolean.class);
    }

    public List<ValBokSubjectDto> selectValBokSubjectByInCode(ValBokSubjectDto dto) {
        return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME+"/ValSubjectSet002/selectValBokSubjectByInCode", dto, ValBokSubjectDto.class);
    }

    /*add by sunshuaishuai 辅助核算项修改 20220406 start*/
    /**
     * 根据基础科目性质查询辅助核算项
     *
     * @param map 过滤参数
     * @return 上级科目代码列表
     * @throws Exception
     * @see
     * @since 1.0
     */
    public List<ValSubjectHandDto> getHandCode(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
                +"/ValSubjectSet002/getHandCode", map, ValSubjectHandDto.class);

    }

    /**
     * 根据基础科目性质联动辅助核算项下拉框
     *
     * @param map 过滤参数
     * @return 上级科目代码列表
     * @throws Exception
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getType(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
                +"/ValSubjectSet002/getType", map, SelectOptionDto.class);

    }

    /*add by sunshuaishuai 辅助核算项修改 20220406 end*/
}
