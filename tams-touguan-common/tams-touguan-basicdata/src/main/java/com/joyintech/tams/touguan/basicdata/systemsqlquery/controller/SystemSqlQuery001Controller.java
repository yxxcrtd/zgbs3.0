/*
 * 文件名：InvestTradeQuery001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年7月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.touguan.basicdata.systemsqlquery.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.joyintech.tams.touguan.basicdata.systemsqlquery.dto.ExportEntity;
import com.joyintech.tams.touguan.basicdata.systemsqlquery.dto.WebResult;
import com.joyintech.tams.touguan.basicdata.systemsqlquery.service.SystemSqlQuery001Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;

/**
 * 投资交易查询-controller
 * 
 * @author yangjian
 * @version 1.0 2018年7月24日
 * @see SystemSqlQuery001Controller
 * @since 1.0
 */
@Controller
@PageId("SystemSqlQuery001")
@RequestMapping("/touguan/system/systemsqlquery/SystemSqlQuery001")
public class SystemSqlQuery001Controller extends BaseController {

    
    @Autowired
    private SystemSqlQuery001Service service;
    /**
     * 加载页面 {@inheritDoc}
     */
    @RequestMapping("init")
    public String init(Model model) {
//        String password = this.getParam("password");
//        if (StringUtil.equals("admin", password)) {
//            return "/valuation/system/systemsqlquery/SystemSqlQuery001";
//        }
//        return "/error/404";
        return super.init(model);
    }

    @RequestMapping("checkSearch")
    @ResponseBody
    public WebResult checkSearch(HttpSession session) {
        String sqlText = this.getRequest().getParameter("sqlText");
        WebResult webResult = new WebResult();
        if (StringUtil.isEmpty(sqlText) || StringUtil.isEmpty(sqlText.trim())) {
            webResult.appendMessage(new ValidMessage("sql文不能为空！"));
        }else {
            if (sqlText.trim().toUpperCase().indexOf("SELECT") == 0 || sqlText.trim().toUpperCase().indexOf("WITH") == 0) {
                List<LinkedHashMap<String, Object>> resultList = this.service.selectAllData(sqlText.replace(";", ""));
                if (StringUtil.isEmptyList(resultList)) {
                    webResult.appendMessage(new ValidMessage("没有符合条件的记录！"));
                }else{
                    List<String> resultColumns = new ArrayList<>(resultList.get(0).keySet());
                    webResult.setData(resultColumns);
                }
            }else if (sqlText.trim().toUpperCase().indexOf("INSERT") == 0){
                
            }else if (sqlText.trim().toUpperCase().indexOf("DELETE") == 0){
                
            }else if (sqlText.trim().toUpperCase().indexOf("UPDATE") == 0) {
                
            }else{
                webResult.appendMessage(new ValidMessage("非法执行语句"));
            }
        }
        session.setAttribute("SystemSqlQuery001_sqlText", sqlText);
        return webResult;
    }
    @RequestMapping("search")
    @ResponseBody
    public List<ExportEntity> search()
         {
        String sqlText = this.getRequest().getParameter("sqlText");
        List<ExportEntity> allData = new ArrayList<>();
        try {
            if (sqlText.trim().toUpperCase().indexOf("SELECT") == 0 || sqlText.trim().toUpperCase().indexOf("WITH") == 0) {
                List<LinkedHashMap<String, Object>> resultList = this.service.selectAllData(sqlText.replace(";", ""));
                // 所有查询结果，最外面的List为行数据，里面的List为列数据
                if (!StringUtil.isEmptyList(resultList)) {
                    List<String> resultColumns = new ArrayList<>(resultList.get(0).keySet());
                    allData.add(this.listToEntity(resultColumns));
                    for (Map<String, Object> map : resultList) {
                        List<String> rowList = new ArrayList<>();
                        for (String columnName : resultColumns) {
                            Object value = map.get(columnName.trim());
                            rowList.add(String.valueOf(value));
                        }
                        allData.add(listToEntity(rowList));
                    }
                }
            }
            else {
                if (sqlText.trim().toUpperCase().indexOf("INSERT") == 0) {
                    ExportEntity ee = new ExportEntity();
                    ee.setColumn1("受影响条数：");
                    allData.add(ee);
                    ExportEntity ee2 = new ExportEntity();
                    ee2.setColumn1("成功执行插入数据" + this.service.insertAllData(sqlText.replace(";", ""))+"条");
                    allData.add(ee2);
                }
                else if (sqlText.trim().toUpperCase().indexOf("DELETE") == 0) {
                    ExportEntity ee = new ExportEntity();
                    ee.setColumn1("受影响条数：");
                    allData.add(ee);
                    ExportEntity ee2 = new ExportEntity();
                    ee2.setColumn1("成功执行删除数据" + this.service.deleteAllData(sqlText.replace(";", ""))+"条");
                    allData.add(ee2);
                }
                else if (sqlText.trim().toUpperCase().indexOf("UPDATE") == 0) {
                    ExportEntity ee = new ExportEntity();
                    ee.setColumn1("受影响条数：");
                    allData.add(ee);
                    ExportEntity ee2 = new ExportEntity();
                    ee2.setColumn1("成功执行更新数据" + this.service.updateAllData(sqlText.replace(";", ""))+"条");
                    allData.add(ee2);
                }
                else {
                    ExportEntity ee = new ExportEntity();
                    ee.setColumn1("受影响条数：");
                    allData.add(ee);
                    ExportEntity ee2 = new ExportEntity();
                    ee2.setColumn1("请检查sql");
                    allData.add(ee2);
                }
            }
        }
        catch (Exception e) {
            ExportEntity ee = new ExportEntity();
            ee.setColumn1(e.getMessage());
            allData.add(ee);
            ExportEntity ee2 = new ExportEntity();
            ee2.setColumn1(e.getMessage());
            allData.add(ee2);
        }
        return allData;
    }
    private ExportEntity listToEntity(List<String> allColumns) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ExportEntity entity = new ExportEntity();
        int index = 0;
        while (index < 100 && index < allColumns.size()) {
            Method method = entity.getClass().getDeclaredMethod("setColumn" + (index + 1), String.class);
            method.invoke(entity, allColumns.get(index));
            index++ ;
        }
        while (index < 150 && index < allColumns.size()) {
            Method method = entity.getClass().getDeclaredMethod("setName" + (index - 100 + 1), String.class);
            method.invoke(entity, allColumns.get(index));
            index++ ;
        }
        return entity;
    }

}
