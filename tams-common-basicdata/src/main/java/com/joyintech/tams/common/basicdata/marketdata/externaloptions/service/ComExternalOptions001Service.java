package com.joyintech.tams.common.basicdata.marketdata.externaloptions.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.basicdata.constant.ServiceApplication;
import com.joyintech.tams.common.basicdata.marketdata.externaloptions.dto.ComExternalOptions001Dto;
import com.joyintech.tams.common.basicdata.marketdata.externaloptions.dto.ComExternalOptions002Dto;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.util.ExcelUtil;
import com.joyintech.workflow.engine.constant.OperationType;

/**
 * @author 姚轶文
 * @create 2020- 01-06 16:01
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ComExternalOptions001Service extends CommonExportService<ComExternalOptions001Dto> {

    @Autowired
    private ComExternalOptions002Service comExternalOptions002Service;

    /**
     * 分页查询
     * @param params
     * @return
     */
    public Page<ComExternalOptions001Dto> queryPageList(Map<String, Object> params){
        String str = DictCodeListExt3.基准类型_2_合约标的.getValue(); //07
        //System.out.println(str);

        params.put("BENCHMARK_TYPE2",str);
        return restTemplate.postForPage(ServiceApplication.CommonBasicDataService + "/ComExternalOptions/findPage",
                params, ComExternalOptions001Dto.class);
    }


    public List<SuggestDto> findTargetId(Map<String, Object> params) {

        return restTemplate.postForList(ServiceApplication.CommonBasicDataService + "/ComExternalOptions/findTargetId",
                params, SuggestDto.class);
    }

    public List<SuggestDto> findFinprodId(Map<String, Object> params) {

        return restTemplate.postForList(ServiceApplication.CommonBasicDataService + "/ComExternalOptions/findFinprodId",
                params, SuggestDto.class);
    }

    /**
     * 查询所有
     * @param params
     * @return
     */
    public List<ComExternalOptions001Dto> queryAllList(Map<String, Object> params){
        String str = DictCodeListExt3.基准类型_2_合约标的.getValue(); //07
        //System.out.println(str);

        params.put("BENCHMARK_TYPE2",str);
        return restTemplate.postForList(
                ServiceApplication.CommonBasicDataService + "/ComExternalOptions/queryAllList",
                params, ComExternalOptions001Dto.class);
    }

    /**
     * 导入check
     * @param fileName
     * @return
     */
    public AjaxResult checkImportData(String fileName) {
        List<List<String>> objects = ExcelUtil.getTempList(fileName);
        if (objects.size() - 1 > 1000) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("errorMsg", "行情单次最多导入1000条。");
            AjaxResult wr = new AjaxResult();
            wr.setData(resultMap);
            return wr;
        }
        String totalMsg = "";
        List<List<String>> jsonArrays = new ArrayList<>();
        // 重复数据
        int repeatNum = 0;
        if (objects.size() <= 1) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("errorMsg", "数据格式错误，内容需要大于1行，模板是有表头的。");
            AjaxResult wr = new AjaxResult();
            wr.setData(resultMap);
            return wr;
        } else {
            if (null == objects.get(0) || objects.get(0).size() != 6) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("errorMsg", "数据格式错误，数据格式需要6列。");
                AjaxResult wr = new AjaxResult();
                wr.setData(resultMap);
                return wr;
            }else {
                StringBuilder returnerrorMsg = new StringBuilder("");
                for (int i = 1; i < objects.size(); i++) {
                    StringBuilder errorMsg = new StringBuilder("");
                    List<String> rowData = objects.get(i);
                    //交易编号 必填
                    if (!StringUtil.isNotEmpty(rowData.get(0))){
                        errorMsg.append("交易编号 必填！");
                    }
                    //估值日期 必填
                    if (!StringUtil.isNotEmpty(rowData.get(4))){
                        errorMsg.append("估值日期 必填！");
                    }
                    //收盘价 必填
                    if (!StringUtil.isNotEmpty(rowData.get(5))){
                        errorMsg.append("收盘价 必填！");
                    }
                    if (StringUtil.isNotEmpty(errorMsg.toString())){
                        errorMsg.append("<br />");
                        returnerrorMsg.append("第"+(i+1)+"行数据："+errorMsg.toString());
                    }
                }
                if (StringUtil.isNotEmpty(returnerrorMsg.toString())){
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("errorMsg", returnerrorMsg.toString());
                    AjaxResult wr = new AjaxResult();
                    wr.setData(resultMap);
                    return wr;
                }
                for (int i = objects.size() - 1; i >= 1; i--) {
                    List<String> list1 = objects.get(i);
                    Boolean isRepeat = false;
                    for (int k = i - 1; k >= 1; k--) {
                        List<String> list2 = objects.get(k);
                        //交易编号、估值日期，相同时为重复数据
                        if ((StringUtil.equals(list1.get(0), list2.get(0))
                                && StringUtil.equals(list1.get(4), list2.get(4))
                        )) {
                            isRepeat = true;
                            break;
                        }
                    }
                    if (isRepeat){
                        repeatNum++;
                    }else {
                        jsonArrays.add(list1);
                    }
                }
            }
        }
        if (repeatNum > 0){
            totalMsg += "导入文件存在"+String.valueOf(repeatNum)+"条数据重复，重复数据不导入，是否确认？";
        }
        Map<String, Object> resultMap = new HashMap<>();
        if (StringUtil.isNotBlank(totalMsg)) {
            resultMap.put("confirmMsg", totalMsg);
        }
        resultMap.put("correctData", JsonUtils.object2Json(jsonArrays));
        AjaxResult wr = new AjaxResult();
        wr.setData(resultMap);
        return wr;
    }

    /**
     * 期权行情信息
     * @param tempList
     * @return
     * @throws IOException
     */
    public ComExternalOptions002Dto getValNetAssetQuotationInfoListData(String tempList)   {
        ComExternalOptions002Dto dto = new ComExternalOptions002Dto();
        // 行情信息列表
        List<ComExternalOptions002Dto> externalOptions002DtoList = new ArrayList<>();
        dto.setSelectedItem(externalOptions002DtoList);
        JSONArray objects = JSONArray.parseArray(tempList);

        for (int i = 0; i < objects.size(); i++) {
            JSONArray rowData = JSONArray.parseArray(((JSONArray) objects.get(i)).toJSONString());
//        	 List<String> rowData = JsonUtils.json2List(objects.get(i),String.class);
            ComExternalOptions002Dto externalOptions002Dto = new ComExternalOptions002Dto();
            // 交易编号【0】
            if (StringUtil.isNotEmpty(rowData.get(0))) {
                externalOptions002Dto.setFinprodId2(rowData.get(0).toString());
            }
            // 合约标的【1】
            if (StringUtil.isNotEmpty(rowData.get(1))) {
                externalOptions002Dto.setTargetName(rowData.get(1).toString());
            }
            // 标的代码【2】
            if (StringUtil.isNotEmpty(rowData.get(2))) {
                externalOptions002Dto.setTargetId(rowData.get(2).toString());
            }
            // 标的类型【3】
            if (StringUtil.isNotEmpty(rowData.get(3))) {
                externalOptions002Dto.setTargetType(this.getCode(rowData.get(3).toString()));
            }
            // 估值日期【4】
            if (StringUtil.isNotEmpty(rowData.get(4))) {
                externalOptions002Dto.setValDate(DateUtil.format(DateUtil.parse(rowData.get(4).toString())));
            }
            // 收盘价【5】
            if (StringUtil.isNotEmpty(rowData.get(5))) {
                externalOptions002Dto.setValuation(new BigDecimal(rowData.get(5).toString()));
            }
            externalOptions002DtoList.add(externalOptions002Dto);
        }
        dto.setSelectedItem(externalOptions002DtoList);
        return dto;
    }
    /**
     * 取空格前的数据
     * @param str
     * @return
     */
    private String getCode(String str) {
        return str.split(" ")[0];
    }
    /**
     * 导入
     * @param param
     * @return
     * @throws IOException
     */
    public AjaxResult importFile(Map<String, Object> param) {
        AjaxResult ajaxResult = new AjaxResult();
        String tempList = (String) param.get("tempList");
        ComExternalOptions002Dto dto = getValNetAssetQuotationInfoListData(tempList);
        List<ComExternalOptions002Dto> selectedItem = dto.getSelectedItem();
        List<ComExternalOptions002Dto>  existData = new ArrayList<>();
        List<ComExternalOptions002Dto>  newData = new ArrayList<>();
        for (ComExternalOptions002Dto temp:selectedItem
        ) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("finprodId2", temp.getFinprodId2());
            map.put("valDate", temp.getValDate());
            map.put("targetId", temp.getTargetId());
            ComExternalOptions002Dto byPrimaryKey = comExternalOptions002Service.findByPrimaryKey(map);
            //数据已存在 【交易编号、合约标的、估值日期 相同】，做更新操作。
            if (StringUtil.isNotEmpty(byPrimaryKey)){
                existData.add(temp);
            }else {
                newData.add(temp);
            }
        }
        //更新操作
        for (ComExternalOptions002Dto existDataDto : existData
        ) {
            comExternalOptions002Service.doComplete(existDataDto,OperationType.MODIFY);
        }
        //插入操作
        for (ComExternalOptions002Dto newDataDto : newData
        ) {
            comExternalOptions002Service.doComplete(newDataDto, OperationType.NEW);
        }
        ajaxResult.setStatuCode(HttpStatus.OK.value());
        return ajaxResult;
    }
}
