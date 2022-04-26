/*
 * 文件名：PaActiveRemind001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年8月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.remind.controller;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.service.ActiveReminderSubPage;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.remind.service.PaActiveRemind001Service;

/**
 * 主动提醒
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2018年8月20日
 * @see PaActiveRemind001Controller
 * @since 1.0
 */
@Controller
@PageId("PaActiveRemind001")
@RequestMapping("/invest/remind/PaActiveRemind001")
public class PaActiveRemind001Controller extends BaseController implements ActiveReminderSubPage{
    
    /**
     * 平台日组件
     */
    @Autowired(required = false)
    private IPlatformDate platformDate;
    /**
     * 获取service对象
     */
    @Autowired
    private PaActiveRemind001Service paActiveRemind001Service;
    /**
     * 实现主动提醒页面关联接口
     */
    @Override
    public String getMyActiveReminderPage() {
        String url = "/invest/remind/PaActiveRemind001/init";
        return url;
    }
    /**
     * 主动提醒页面初始化
     */
    @Override
    public String init(Model model) {
        Date sysDate = DateUtil.now();
        if(null != platformDate) {
            sysDate = platformDate.getSysDate();
        }
        model.addAttribute("sysDate",sysDate);
        Map<String, Object> param = new HashMap<>();
        param.put("quaryDate", sysDate);
        Map<String, Object> result=paActiveRemind001Service.searchCount(param);
        model.addAttribute("result", result);
        //add by liupengfei for 多级法人改造 S
        model.addAttribute("isEjfr", MultilevelDataAuthorityUtil.isEnabled());
        //add by liupengfei for 多级法人改造 E
        //大分类
        model.addAttribute("R1", BussinessDictCodeList.提醒类型_大分类_产品端.getValue());
        model.addAttribute("R2", BussinessDictCodeList.提醒类型_大分类_315检查.getValue());
        model.addAttribute("R3", BussinessDictCodeList.提醒类型_大分类_回款及存续期确认.getValue());
        model.addAttribute("R4", BussinessDictCodeList.提醒类型_大分类_债券覆盖及行权.getValue());
        model.addAttribute("R5", BussinessDictCodeList.提醒类型_大分类_风险提醒.getValue());
        //净值披露提醒,债券评级提醒,项目类资产标的修改提醒,债券借贷融出的券还本收息提醒
        model.addAttribute("R6", BussinessDictCodeList.提醒类型_大分类_披露管理.getValue());
        model.addAttribute("R7", BussinessDictCodeList.提醒类型_大分类_变更提醒.getValue());
        model.addAttribute("R9", BussinessDictCodeList.提醒类型_大分类_公共数据变更.getValue());
        //add by chenjiaxing 分销缴款提醒 20210727 start
        model.addAttribute("R11", BussinessDictCodeList.提醒类型_大分类_分销缴款提醒.getValue());
        //add by chenjiaxing 分销缴款提醒 20210727 end
        //小分类
        model.addAttribute("R6S1", BussinessDictCodeList.提醒类型_小分类_净值披露提醒.getValue());
        model.addAttribute("R7S1", BussinessDictCodeList.提醒类型_小分类_债项和主体评级变动.getValue());
        model.addAttribute("R7S2", BussinessDictCodeList.提醒类型_小分类_项目类资产标的修改提醒.getValue());
        model.addAttribute("R7S3", BussinessDictCodeList.提醒类型_小分类_舆情提醒.getValue());
        model.addAttribute("R3S16", BussinessDictCodeList.提醒类型_小分类_债券借贷融出的券还本收息.getValue());
        model.addAttribute("R1S1", BussinessDictCodeList.提醒类型_小分类_产品正在募集.getValue());
        model.addAttribute("R1S2", BussinessDictCodeList.提醒类型_小分类_产品起息.getValue());
        model.addAttribute("R1S3", BussinessDictCodeList.提醒类型_小分类_产品中间付息.getValue());
        model.addAttribute("R1S4", BussinessDictCodeList.提醒类型_小分类_子周期强制赎回提醒.getValue());
        model.addAttribute("R1S5", BussinessDictCodeList.提醒类型_小分类_产品到期.getValue());
        model.addAttribute("R1S6", BussinessDictCodeList.提醒类型_小分类_产品费用支付.getValue());
        model.addAttribute("R1S7", BussinessDictCodeList.提醒类型_小分类_产品的最新净值小于1.getValue());

        model.addAttribute("R2S1", BussinessDictCodeList.提醒类型_小分类_项目类提前5天.getValue());
        model.addAttribute("R2S2", BussinessDictCodeList.提醒类型_小分类_项目类提前1个月.getValue());
        model.addAttribute("R2S3", BussinessDictCodeList.提醒类型_小分类_项目类提前3个月.getValue());
        model.addAttribute("R2S4", BussinessDictCodeList.提醒类型_小分类_计划类利率型提前5天.getValue());
        model.addAttribute("R2S5", BussinessDictCodeList.提醒类型_小分类_计划类利率型提前1个月.getValue());
        model.addAttribute("R2S6", BussinessDictCodeList.提醒类型_小分类_计划类利率型提前3个月.getValue());

        model.addAttribute("R3S1", BussinessDictCodeList.提醒类型_小分类_基金类净值型回款确认.getValue());
        model.addAttribute("R3S2", BussinessDictCodeList.提醒类型_小分类_基金类货币型回款确认.getValue());
        model.addAttribute("R3S3", BussinessDictCodeList.提醒类型_小分类_计划类净值型回款确认.getValue());
        model.addAttribute("R3S4", BussinessDictCodeList.提醒类型_小分类_货币基金分红提醒.getValue());
        model.addAttribute("R3S5", BussinessDictCodeList.提醒类型_小分类_债券存续期.getValue());
        model.addAttribute("R3S6", BussinessDictCodeList.提醒类型_小分类_债券存续期回款确认.getValue());
        model.addAttribute("R3S7", BussinessDictCodeList.提醒类型_小分类_回购回款确认.getValue());
        model.addAttribute("R3S8", BussinessDictCodeList.提醒类型_小分类_项目类资产存续期.getValue());
        model.addAttribute("R3S9", BussinessDictCodeList.提醒类型_小分类_项目类资产存续期回款确认.getValue());
        model.addAttribute("R3S10", BussinessDictCodeList.提醒类型_小分类_计划类利率型存续期.getValue());
        model.addAttribute("R3S11", BussinessDictCodeList.提醒类型_小分类_计划类利率型存续期回款确认.getValue());
        model.addAttribute("R3S12", BussinessDictCodeList.提醒类型_小分类_计划类货币型回款确认.getValue());
        model.addAttribute("R3S13", BussinessDictCodeList.提醒类型_小分类_存款类资产存续期.getValue());
        model.addAttribute("R3S14", BussinessDictCodeList.提醒类型_小分类_质押式回购存续期.getValue());
        model.addAttribute("R3S15", BussinessDictCodeList.提醒类型_小分类_买断式回购存续期.getValue());

        model.addAttribute("R4S1", BussinessDictCodeList.提醒类型_小分类_债券行权.getValue());
        model.addAttribute("R4S2", BussinessDictCodeList.提醒类型_小分类_债券覆盖.getValue());

        model.addAttribute("R5S1", BussinessDictCodeList.提醒类型_小分类_指标预警提醒.getValue());
        model.addAttribute("R5S2", BussinessDictCodeList.提醒类型_小分类_指标违规提醒.getValue());
        model.addAttribute("R5S3", BussinessDictCodeList.提醒类型_小分类_固定值参数更新提醒.getValue());

        model.addAttribute("R9S1", BussinessDictCodeList.提醒类型_小分类_数据修改.getValue());
        //add by chenjiaxing 分销缴款提醒 20210727 start
        model.addAttribute("R11S1", BussinessDictCodeList.提醒类型_小分类_分销缴款提醒.getValue());
        //add by chenjiaxing 分销缴款提醒 20210727 end

        model.addAttribute("R10", BussinessDictCodeList.提醒类型_大分类_债券上市流通.getValue());
        model.addAttribute("R10S1", BussinessDictCodeList.提醒类型_小分类_债券上市流通操作.getValue());
        model.addAttribute("R10S2", BussinessDictCodeList.提醒类型_小分类_资产要素登记修改.getValue());

        if (MultilevelDataAuthorityUtil.isEnabled()) {
            User user = SecurityUtil.getLoginUser();
            model.addAttribute("isEJFRflag", true);
            model.addAttribute("indexInstitutionCode",user==null?"":user.getOrgCode());
        }else{
            model.addAttribute("isEJFRflag", false);
        }

        return super.init(model);
    }
    /**
     * 
     * 查询提醒信息条数
     * <功能详细描述>
     * @param param 传入查询参数
     * @return 返回查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("searchCount")
    public Map<String, Object> searchCount(@RequestBody Map<String, Object> param){
        param.put("quaryDate", DateUtil.parse((String)param.get("quaryDate")));
        Map<String, Object> result=paActiveRemind001Service.searchCount(param);
        return result;
    }
    
}
