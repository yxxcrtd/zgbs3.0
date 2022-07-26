package com.joyintech.tams.invest.usernavbar.service;/*
 * 文件名：MyUserNavBarService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年10月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.joyintech.framework.navbar.UserNavBarService;
import com.joyintech.framework.navbar.UserNavbar;
/**
 * @version 1.0 2022年7月21日
 * @see MyUserNavBarService
 * @since 1.0
 */
@Component
public class MyUserNavBarService implements UserNavBarService {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserNavbar> getUserNavbar() {
        List<UserNavbar> list = new ArrayList<>();
        UserNavbar bar = new UserNavbar();
        bar.setId("helpdoc");
        bar.setName("帮助手册");
        bar.setUrl("http://192.168.70.190:8181/docs/mindoc/mindoc-1d1a2d81h288r");
        bar.setTarget("_blank");
        list.add(bar);
        return list;
    }
}