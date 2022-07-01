/*
 * 文件名：NavbarService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改时间：2018年1月24日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.framework.navbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.intf.Menu;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;

/**
 * 导航栏获取服务
 * @version 1.0 2018年1月24日
 * @see NavbarService
 * @since 1.0
 */
@Service
public class NavbarService {

    /**
     * 更加用户名获取该用户所有有权限的导航
     * @return 导航列表
     */
    public List<Navbar> selectNavbars() {
        List<Navbar> navList = new ArrayList<>();
        if(SecurityUtil.hasAuthenticated()) {
            // 从缓存中获取菜单
            Collection<Menu> menus = CacheUtil.getAllMenu();
            if(null != menus) {
                menus.forEach(menu -> {
                    if(SecurityUtil.isPermitted(menu.getId())) {
                        navList.add(parseNavbar(menu));
                    }
                });
            }
        }
        return navList.stream().sorted((a,b) -> {
            return a.getOrder() - b.getOrder();
        }).collect(Collectors.toList());
    }
    
    /**
     * 通过Menu创建Navbar
     * @param menu {@link Menu}
     * @return {@link Navbar}
     */
    protected Navbar parseNavbar(Menu menu) {
        Navbar nav = new Navbar();
        nav.setId(menu.getId());
        nav.setTitle(menu.getName());
        nav.setIcon(menu.getIcon());
        nav.setSpread(menu.isSpread());
        nav.setOrder(menu.getOrderNo());
        if(null != menu.getPage()) {
            nav.setUrl(parseUrl(menu.getPage().getPath()));
            nav.setPageId(menu.getPage().getId());
            nav.setPageType(menu.getPage().getFile());
        }
        nav.setChildren(getChildren(menu.getChildren()));
        return nav;
    }
    
    /**
     * 解析URL地址栏的参数
     * @param url 画面地址
     * @return 解析后的画面地址
     */
    protected String parseUrl(String url) {
    	if(null != url && url.contains(".html")) {
    		return url;
    	}
        if(null != url && url.contains("?")) {
            String[] paths = StringUtil.split(url.trim(), "?");
            StringBuilder urlBuilder = new StringBuilder(paths[0]);
            urlBuilder.append("?");
            // 取出地址栏中的参数字符串
            String paraStr = paths[1];
            if(null != paraStr) {
                String[] params = StringUtil.split(paraStr, "&");
                for(int i=0;i<params.length;i++) {
                    String param = params[i];
                    if(param != null && param.contains("=")) {
                        String[] pp = StringUtil.split(param, "=");
                        if(i > 0) {
                            urlBuilder.append("&");
                        }
                        urlBuilder.append(pp[0]).append("=");
                        param = pp[1];
                    }
                    if(param.startsWith("{") && param.endsWith("}")) {
                        param = param.substring(1, param.length() - 1);
                        urlBuilder.append(getParam(param));
                    }
                }
            }
            return urlBuilder.toString();
        }
        return url;
    }
    
    /**
     * 获取参数
     * @param param 参数名
     * @return 参数值
     */
    protected String getParam(String param) {
        Map<String, PageUrlParamHandle> handleMap = SpringContextUtil.getBeans(PageUrlParamHandle.class);
        handleMap.values().forEach(handle -> {
            if(handle.process(param)) {
                return;
            }
        });
        return param;
    }

    /**
     * @param children list Menu
     * @return the list of {@link Navbar}
     */
    private List<Navbar> getChildren(Collection<Menu> children) {
        if(CollectionUtils.isNotEmpty(children)) {
            List<Navbar> navbars = new ArrayList<>(children.size());
            children.forEach(menu -> {
                if(SecurityUtil.isPermitted(menu.getId())) {
                    navbars.add(parseNavbar(menu));
                }
            });
            return navbars.stream().sorted((a,b) -> {
                return a.getOrder() - b.getOrder();
            }).collect(Collectors.toList());
        }
        return null;
    }
}
