/*
 * 文件名：ComCoinFundQuotationInfo001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年8月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.common.basicdata.marketdata.mstcoinfundquotationinfo.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.basicdata.constant.ServiceApplication;
import com.joyintech.tams.common.basicdata.marketdata.mstcoinfundquotationinfo.dto.ComCoinFundQuotationInfo001Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.systemtools.util.ExcelUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 基金类行情信息
 * 
 * @author fanshuanghu
 * @version 1.0 2018年8月3日
 * @see ComCoinFundQuotationInfo001Service
 * @since 1.0
 */
@Service
public class ComCoinFundQuotationInfo001Service extends CommonExportService<ComCoinFundQuotationInfo001Dto>
    implements BaseProcessService<ComCoinFundQuotationInfo001Dto> {

    /**
     * 分页查询
     * 
     * @param pageParam 查询参数
     * @return page对象
     * @throws Exception 错误
     * @see
     * @since 1.0
     */
    public Page<ComCoinFundQuotationInfo001Dto> selectForPage(Map<String, Object> pageParam){
        return restTemplate.postForPage(
            ServiceApplication.CommonBasicDataService + "/ComCoinFundQuotationInfo/selectForPage",
            pageParam, ComCoinFundQuotationInfo001Dto.class);
    }

    /**
     * 画面数据初期化
     * 
     * @param pageParam 查询参数
     * @return page对象
     * @throws Exception 错误
     * @see
     * @since 1.0
     */
    public List<ComCoinFundQuotationInfo001Dto> selectPageInfo(Map<String, Object> pageParam){
        return restTemplate.postForList(ServiceApplication.CommonBasicDataService + "/ComCoinFundQuotationInfo/selectPageInfo", 
            pageParam, ComCoinFundQuotationInfo001Dto.class);
    }
    /**
     * 联想文本框service层实现
     * 
     * @param param 检索参数
     * @return SuggestDto
     */
    public List<SelectOptionDto> selectForCode(Map<String, Object> param) {
        return restTemplate.postForList(
            ServiceApplication.CommonBasicDataService + "/ComCoinFundQuotationInfo/selectForCode",
            param, SelectOptionDto.class);
    }

    /**
     * data-url 方式的下拉框
     * 
     * @param map 检索参数
     * @return SuggestDto
     */
    public List<SelectOptionDto> getManagement(Map<String, Object> map) {
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        map.put("yes", DictCodeList.是否_是.getValue());
        return restTemplate.postForList(
            ServiceApplication.CommonBasicDataService + "/ComCoinFundQuotationInfo/getManagement",
            map, SelectOptionDto.class);
    }

    /**
     * 联想下拉框基金代码
     * @param map 查询参数
     * @return List<FinprodSeleOptionDto>金融产品实体类集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryFinprod(Map<String, Object> map) {
        map.put("finprodType2", DictCodeListExt1.金融产品类型_基金.getValue());
        return restTemplate.postForList(
            ServiceApplication.CommonBasicDataService + "/ComCoinFundQuotationInfo/queryFinprod",
            map, SelectOptionDto.class);
    }

    /**
     * 以基金代码获取金融产品信息集合
     * 
     * @param finprodId
     * @return List<ComCoinFundQuotationInfo001Dto>金融产品实体类集合
     * @see
     * @since 1.0
     *//*
       * public List<ComCoinFundQuotationInfo001Dto> getByFunCode(String finprodId){ return
       * mstCoinFundQuotationInfo001Mapper.getByfinprodId(finprodId); }
       */
    /**
     * 以基金代码获取基金类型和基金管理人名称
     * 
     * @param map 界面参数
     * @return ComCoinFundQuotationInfo001Dto金融产品实体类
     * @see
     * @since 1.0
     */
    public ComCoinFundQuotationInfo001Dto getManageAndType(Map<String,Object> map) {
        return restTemplate.postForObject(
            ServiceApplication.CommonBasicDataService + "/ComCoinFundQuotationInfo/getManageAndType",
            map, ComCoinFundQuotationInfo001Dto.class);
    }

    /**
     * 流程中间类 {@inheritDoc}
     */
    @Override
    public void doComplete(ComCoinFundQuotationInfo001Dto entity, OperationType type) {
        new HashMap<>();
        entity.setType(type.toString());
        restTemplate.postForObject(
            ServiceApplication.CommonBasicDataService + "/ComCoinFundQuotationInfo/doComplete",
            entity, Integer.class);
    }
    /**
     * 导出 {@inheritDoc}
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
                "/com/joyintech/tams/common/basicdata/marketdata/mstcoinfundquotationinfo/conf/export_conf.xml").dataSource(
                    DataSourceType.MASERT);
    }
    /**
     * 导出
     */
    @Override
    public Page<ComCoinFundQuotationInfo001Dto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }
    /**
     * 设置日期格式
     * {@inheritDoc}
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(("valDate".equals(propName) || "importDay".equals(propName) || "publishDate".equals(propName)) && value != null){
            cell.setCellValue(DateUtil.format((Date)value));
        }
    }
    /**
     * 通过估值日与ID查询
     * <功能详细描述>
     * @param finprodId id
     * @param valDate 估值日期
     * @return 结果
     * @see
     * @since 1.0
     */
    public String selectByIdAndValDate(String finprodId, String valDate) {
        Map<String,Object>map=new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("valDate", valDate);
        return restTemplate.postForObject(
            ServiceApplication.CommonBasicDataService + "/ComCoinFundQuotationInfo/selectByIdAndValDate",
            map, String.class);
    }
    
    public String getIplatDate(){
        return restTemplate.getForObject(
            ServiceApplication.CommonBasicDataService + "/ComCoinFundQuotationInfo/getIplatDate", String.class);
    }
    /**
     * 导入check
     * @param fileName
     * @return
     */
    public AjaxResult checkImportData(String fileName) {
        //Pattern pattern = Pattern.compile("[0-9]*");
        // 正则校验由整数改为 浮点数校验 区分保留位数  likaitao 2022/5/9 start
        Pattern patternDecimal14 = Pattern.compile("([1-9]\\d*\\.?\\d{1,14})|(0\\.\\d*[0-9]{1,14}|[0-9]\\d*)");
        Pattern patternDecimal4 = Pattern.compile("([1-9]\\d*\\.?\\d{1,4})|(0\\.\\d*[0-9]{1,4}|[0-9]\\d*)");
        Pattern patternDecimal2 = Pattern.compile("([1-9]\\d*\\.?\\d{1,2})|(0\\.\\d*[0-9]{1,2}|[0-9]\\d*)");
        // 正则校验由整数改为 浮点数校验 区分保留位数  likaitao 2022/5/9 end
        List<String> findList =  this.queryFinprod(Maps.newHashMap()).stream().map(item->item.getId()).collect(Collectors.toList());
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
            if (null == objects.get(0) || objects.get(0).size() != 9) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("errorMsg", "数据格式错误，数据格式需要9列。");
                AjaxResult wr = new AjaxResult();
                wr.setData(resultMap);
                return wr;
            }else {
                StringBuilder returnerrorMsg = new StringBuilder("");
                for (int i = 1; i < objects.size(); i++) {
                    StringBuilder errorMsg = new StringBuilder("");
                    List<String> rowData = objects.get(i);
                    //基金代码 必填
                    if (!StringUtil.isNotEmpty(rowData.get(0))){
                        errorMsg.append("基金代码 必填！");
                    }else if(!findList.contains(rowData.get(0))){
                        errorMsg.append("基金代码不存在！");
                    }

                    //估值日期 必填
                    if (!StringUtil.isNotEmpty(rowData.get(2))){
                        errorMsg.append("估值日期 必填！");
                    }else if(!this.isDateStr(rowData.get(2))){
                        errorMsg.append("估值日期 格式不正确！");
                    }

                    //万份收益/净值 必填
                    if (!StringUtil.isNotEmpty(rowData.get(3))){
                        errorMsg.append("万份收益/净值 必填！");
                    }else if (!patternDecimal14.matcher(rowData.get(3)).matches()){
                        errorMsg.append("万份收益/净值 格式不正确！");
                    }

                    //累计净值	复权单位净值	七日年化收益率	基金总份额
                    if (StringUtil.isNotEmpty(rowData.get(5))){
                        if (!patternDecimal4.matcher(rowData.get(5)).matches()){
                            errorMsg.append("累计净值 格式不正确！");
                        }
                    }

                    if (StringUtil.isNotEmpty(rowData.get(6))){
                        if (!patternDecimal4.matcher(rowData.get(6)).matches()){
                            errorMsg.append("复权单位净值 格式不正确！");
                        }
                    }

                    if (StringUtil.isNotEmpty(rowData.get(7))){
                        if (!patternDecimal4.matcher(rowData.get(7)).matches()){
                            errorMsg.append("七日年化收益率 格式不正确！");
                        }
                    }

                    if (StringUtil.isNotEmpty(rowData.get(8))){
                        if (!patternDecimal2.matcher(rowData.get(8)).matches()){
                            errorMsg.append("基金总份额 格式不正确！");
                        }
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
                //第一行【i=0】为表头，所以不操作。
                for (int i = objects.size() - 1; i >= 1; i--) {
                    List<String> list1 = objects.get(i);
                    Boolean isRepeat = false;
                    for (int k = i - 1; k >= 1; k--) {
                        List<String> list2 = objects.get(k);
                        //基金代码、估值日期，相同时为重复数据
                        if ((StringUtil.equals(list1.get(0), list2.get(0)) && StringUtil.equals(list1.get(2), list2.get(2)))) {
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
                /*for (int i = 1; i < objects.size(); i++) {
                    List<String> list1 = objects.get(i);
                    Boolean isRepeat = false;
                    for (int k = i + 1; k < objects.size(); k++ ) {
                        List<String> list2 = objects.get(k);
                        //基金代码、估值日期，相同时为重复数据
                        if ((StringUtil.equals(list1.get(0), list2.get(0)) && StringUtil.equals(list1.get(2), list2.get(2)))) {
                            isRepeat = true;
                            break;
                        }
                    }
                    if (isRepeat){
                        repeatNum++;
                    }else {
                        jsonArrays.add(list1);
                    }
                }*/
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

    public static boolean isDateStr(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        try {
            format.setLenient(false);
            format.parse(str);
            return true;
        } catch (Exception var3) {
            return false;
        }
    }

    /**
     * 基金类估值行情信息
     * @param tempList
     * @return
     * @throws IOException 
     */
    public ComCoinFundQuotationInfo001Dto getValNetAssetQuotationInfoListData(String tempList) {
        ComCoinFundQuotationInfo001Dto dto = new ComCoinFundQuotationInfo001Dto();
        // 行情信息列表
        List<ComCoinFundQuotationInfo001Dto> valNetAssetQuotationInfoList = new ArrayList<>();
        dto.setSelectedItem(valNetAssetQuotationInfoList);
        JSONArray objects = JSONArray.parseArray(tempList);

        for (int i = 0; i < objects.size(); i++) {
            JSONArray rowData = JSONArray.parseArray(((JSONArray) objects.get(i)).toJSONString());
//            List<String> rowData = JsonUtils.json2List( objects.get(i), String.class);
            ComCoinFundQuotationInfo001Dto valCoinFundQuotationInfo001Dto = new ComCoinFundQuotationInfo001Dto();
            // 基金代码【0】
            if (StringUtil.isNotEmpty(rowData.get(0))) {
                valCoinFundQuotationInfo001Dto.setFinprodMarketId(rowData.get(0).toString());
            }
            // 基金类型【1】
            if (StringUtil.isNotEmpty(rowData.get(1))) {
                valCoinFundQuotationInfo001Dto.setFinprodType2(this.getCode(rowData.get(1).toString()));
            }
            // 估值日期【2】
            if (StringUtil.isNotEmpty(rowData.get(2))) {
                valCoinFundQuotationInfo001Dto.setValDate(DateUtil.parse(rowData.get(2).toString()));
            }
            // 万份收益/净值【3】
            if (StringUtil.isNotEmpty(rowData.get(3))) {
                valCoinFundQuotationInfo001Dto.setValuation(new BigDecimal(rowData.get(3).toString()));
            }
            // 基金管理人【4】
            if (StringUtil.isNotEmpty(rowData.get(4))) {
                valCoinFundQuotationInfo001Dto.setFundManager(rowData.get(4).toString());
            }
            // 累计净值【5】
            if (StringUtil.isNotEmpty(rowData.get(5))) {
                valCoinFundQuotationInfo001Dto.setTotalValue(new BigDecimal(rowData.get(5).toString()));
            }
            // 复权单位净值【6】
            if (StringUtil.isNotEmpty(rowData.get(6))) {
                valCoinFundQuotationInfo001Dto.setRestNetValue(new BigDecimal(rowData.get(6).toString()));
            }
            // 七日年化收益率【7】
            if (StringUtil.isNotEmpty(rowData.get(7))) {
                valCoinFundQuotationInfo001Dto.setDay7YearYield(new BigDecimal(rowData.get(7).toString()));
            }
            valCoinFundQuotationInfo001Dto.setFinprodId(valCoinFundQuotationInfo001Dto.getFinprodMarketId());
            valNetAssetQuotationInfoList.add(valCoinFundQuotationInfo001Dto);
        }
        dto.setSelectedItem(valNetAssetQuotationInfoList);
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

    public AjaxResult importFile(Map<String, Object> param) {
        AjaxResult ajaxResult = new AjaxResult();
        String tempList = (String) param.get("tempList");
        ComCoinFundQuotationInfo001Dto dto = getValNetAssetQuotationInfoListData(tempList);
        List<ComCoinFundQuotationInfo001Dto> selectedItem = dto.getSelectedItem();
        List<ComCoinFundQuotationInfo001Dto>  existData = new ArrayList<>();
        List<ComCoinFundQuotationInfo001Dto>  newData = new ArrayList<>();
        for (ComCoinFundQuotationInfo001Dto temp:selectedItem
        ) {
            //数据已存在 【资产代码、估值日 相同】，做更新操作。
            if (StringUtil.isNotEmpty(this.selectByIdAndValDate(temp.getFinprodMarketId(),DateUtil.format(temp.getValDate())))){
                existData.add(temp);
            }else {
                newData.add(temp);
            }
        }
        //更新操作
        for (ComCoinFundQuotationInfo001Dto existDataDto : existData
        ) {
            this.doComplete(existDataDto,OperationType.MODIFY);
        }
        //插入操作
        for (ComCoinFundQuotationInfo001Dto newDataDto : newData
        ) {
            this.doComplete(newDataDto,OperationType.NEW);
        }
        ajaxResult.setStatuCode(HttpStatus.OK.value());
        return ajaxResult;
    }
}
