package com.joyintech.service.common.config.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.common.config.dao.DynamicConfigMapper;

import org.apache.commons.lang.StringEscapeUtils;
//import groovy.json.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 李东
 * @version 3.0 2021年6月23日
 * @see DynamicConfigService
 * @since 3.0
 */
@Service
public class DynamicConfigService extends BaseService {

    @Autowired
    private DynamicConfigMapper dynamicConfigMapper;

    /**
     * @param dynId
     * @return  示例： {"all":"secName,secType,issuer","checked":"secName"}
     */
    private String selectByDynId(String dynId) {
        List<Map> list = dynamicConfigMapper.selectByDynId(dynId);
        String jsonStr = "";
        if (list != null && list.size() == 1){
            jsonStr = list.get(0).get("dynInfo") + "";
        }
        return jsonStr;
    }
    
    public String selectByMap(Map<String,Object> map) {
        String userName = LoginUserUtil.getUserName();
        String dynId = map.get("dynId") + "";
        
        if (StringUtil.equals("system", map.get("level"))){
            // system级别
            List<Map> list = dynamicConfigMapper.selectByDynId(dynId);
            String jsonStr = "";
            if (list != null && list.size() == 1){
                jsonStr = list.get(0).get("dynInfo") + "";
            }
            return jsonStr;
        }else{
            // 默认用户级别
            map.put("dynId",dynId);
            map.put("userName", userName);
            List<Map> list = dynamicConfigMapper.selectUserDynByDynId(map);
            String jsonStr = "";
            if (list != null && list.size() == 1){ // 数据库已存在
                jsonStr = list.get(0).get("DYN_INFO") + "";
                String allStr = selectByDynId(dynId);
                if (StringUtil.isNotEmpty(allStr)){
                    Map m = JsonUtils.json2Object(allStr, HashMap.class);
                    if (StringUtil.contains(jsonStr, "{")){
                        m.put("checked", JsonUtils.json2Object(jsonStr, Map.class));
                    }else {
                        m.put("checked", jsonStr);
                    }
//                    return StringEscapeUtils.unescapeJavaScript(JsonUtils.object2Json(m));
                    return StringEscapeUtils.unescapeJavaScript(JsonUtils.object2Json(m));
                }
            }else { // 数据库不存在，则返回模板表数据
                jsonStr = selectByDynId(dynId);
            }
            return jsonStr;
        }
    }
    
    /**
     * 
     * @param param {"dynId":"abc", "checked":"secName,secType,issuer"}
     * @return
     */
    public Integer updateByParam(Map<String, Object> param) {
        if (param != null){
            Object checked = param.get("checked");
            if (!(checked instanceof String)) {
                param.put("checked", JsonUtils.object2Json(checked));
            }
            
            String dynId = param.get("dynId") + "";
            if (StringUtil.isNotEmpty(dynId)){
                if (StringUtil.equals("system", param.get("level"))){
                    // system级别：更新DYN_CONFIG_INFO表
                    String jsonStr = selectByDynId(dynId);
                    if (StringUtil.isNotEmpty(jsonStr)){
                        Map m = JsonUtils.json2Object(jsonStr, HashMap.class);
                        m.put("checked", param.get("checked"));
                        param.put("dynInfo", JsonUtils.object2Json(m));
                        return dynamicConfigMapper.updateByParam(param);
                    }
                }else{
                    // 默认用户级别：更新或插入DYN_CONFIG_INFO_USER表
                    String userName = LoginUserUtil.getUserName();
                    param.put("userName", userName);
                    List<Map> list = dynamicConfigMapper.selectUserDynByDynId(param);
                    if (list != null && list.size() == 1) { // 数据库已存在
                        return dynamicConfigMapper.updateUserDynByParam(param);
                    }else{
                        // insert
                        return dynamicConfigMapper.insertUserDyn(param);
                    }
                }
            }
        }
        return 0;
    }

    public Page<SelectOptionDto> selectPage(int page, int length,
                                            Map<String, Object> param) {
        Page<SelectOptionDto> pages = this.queryForPageForSelectReturn(page, length,
                () -> dynamicConfigMapper.selectPage(param));
        System.out.println(pages);
        return pages;
    }
}
