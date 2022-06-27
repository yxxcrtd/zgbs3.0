/*
 * 文件名：TrdBondInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trdquery.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.service.CfetsTrdBondFirstSub002Service;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一级投标交易新建
 *
 * @author jiangpeng
 * @date 2022/6/25 11:32
 */
@Controller
@PageId("CfetsTrdFirstBondInvestTrade002")
@RequestMapping("/cfets/trdquery/CfetsTrdFirstBondInvestTrade002")
public class CfetsTrdFirstBondInvestTrade002Controller extends BaseController {

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    CfetsTrdBondFirstSub002Service cfetsTrdBondFirstSub002Service;

    /**
     * 初始化页面
     *
     * @param
     * @return java.lang.String
     * @author jiangpeng
     * @date 2022/4/22 10:56
     */
    @ApiOperation(value = "审批初始化", notes = "审批页面初始化 入参 workflowId isFromToDoList")
    @PostMapping("/initPage")
    @ResponseBody
    public Message<Map<String, Object>> initPage(@RequestBody Map<String, Object> map) {
        Map<String, Object> initPageParams = new HashMap<>();
        // 有效状态
        initPageParams.put("ISSUE_STATUS", this.getSelectDataFromDataDic(DictCode.发行状态, null));
        // 发行状态
        initPageParams.put("STATUS_VALID", this.getSelectDataFromDataDic(DictCode.有效状态, null));
        // 债券类型
        initPageParams.put("BOND_TYPE", this.getSelectDataFromDataDic(DictCode.行内资产四级分类, null));
        // 委托状态_待接收
        initPageParams.put("ENTRUST_STATUS_02", DictCodeList.委托状态_待接收.getValue());
        // 委托状态_已接收
        initPageParams.put("ENTRUST_STATUS_03", DictCodeList.委托状态_已接收.getValue());
        // 委托状态_已完成
        initPageParams.put("ENTRUST_STATUS_04", DictCodeList.委托状态_已完成.getValue());
        // 委托状态_已退回
        initPageParams.put("ENTRUST_STATUS_05", DictCodeList.委托状态_已退回.getValue());
        // 资金账户类型_托管账户
        initPageParams.put("acctType_A01", BussinessDictCodeList.资金账户类型_托管账户.getValue());
        // 系统日期
        initPageParams.put("sysDate", iPlatformDate.getSysDateStr());
        // 币种_人民币
        initPageParams.put("CCY_CNY", DictCodeList.币种_人民币_CNY.getValue());
        // 系统参数 【是否支持CSTP下行接口_一级申购】
        initPageParams.put("IS_SUPPORT_CSTP_FLAG_BONDFIRST", CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BONDFIRST"));
        // 登记页面id 用于登记撤销
        initPageParams.put("PAGE_ID_BOND_FIRST", "CfetsTrdFirstBondInvestTrade002");

        //流程id
        String workflowId = "null".equals(map.get(GlobalConstant.WORKFLOW_ID)) ? null : (String) map.get(GlobalConstant.WORKFLOW_ID);
        String isFromToDoList = getParam("isFromToDoList");
        if (StringUtil.isNotEmpty(workflowId)) {
            CfetsTrdBondFirstSubInfoDto entity = cfetsTrdBondFirstSub002Service.getBussinessEntityByWorkflowId(workflowId);
//            cfetsTrdBondFirstSub002Service.selectCash(entity);
            initPageParams.put(GlobalConstant.WORKFLOW_ID, workflowId);
            initPageParams.put(GlobalConstant.ENTITY, entity);
        }
        return new Message<>(initPageParams).success();
    }

    private void setSelectOptions(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.交易场所,
                DictCode.有效状态, DictCode.交易平台, DictCode.币种,
                DictCode.投资目的, DictCode.交易市场, DictCode.清算速度, DictCode.是否,
                DictCode.行内资产二级分类, DictCode.行内资产三级分类, DictCode.行内资产四级分类,// 新增资产二、三、四级分类 王贵奇 2019/7/31
                DictCode.WIND一级分类_含债券一级分类等, BussinessDictCode.缴款方式, DictCode.业务模式, DictCode.结算方式, DictCode.清算类型);
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.投资目的, null);
        if (selectDto != null && !StringUtil.isEmptyList(selectDto.getOptions())) {
            List<SelectOptionDto> options = selectDto.getOptions();
            for (int index = options.size() - 1; index >= 0; index--) {
                SelectOptionDto option = options.get(index);
                if (StringUtil.equals(option.getId(), BaseDictCodeList.数据状态_新建.getValue())
                        || StringUtil.equals(option.getId(),
                        BaseDictCodeList.数据状态_删除中.getValue()) || StringUtil.equals(option.getId(),
                        BaseDictCodeList.数据状态_修改中.getValue())) {
                    options.remove(option);
                }

            }
            model.addAttribute("TURN_INV_AIM", JsonUtils.objectArray2Json(options.toArray()));
        }

        model.addAttribute("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        model.addAttribute("TRADE_PLACE_02", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("TRADE_PLACE_03", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_01", DictCodeList.交易平台_外汇交易中心.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_99", DictCodeList.交易平台_其它.getValue());
        model.addAttribute("SHARES_TRADE_MARKET_01", DictCodeListExt1.交易市场_一级市场.getValue());
        model.addAttribute("SHARES_TRADE_MARKET_02", DictCodeListExt1.交易市场_二级市场.getValue());
        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
        // 一体化对应etliu start
//        model.addAttribute("PS_T03", DictCodeList.交易方向_行权.getValue());
        model.addAttribute("PS_T03", DictCodeList.交易方向_行权_回售.getValue());
        model.addAttribute("PS_T39", DictCodeList.交易方向_行权_赎回.getValue());
        // 一体化对应etliu end
        model.addAttribute("ACCOUNT_SPEED_T0", DictCodeList.清算速度_T_0.getValue());
        model.addAttribute("ACCOUNT_SPEED_T1", DictCodeList.清算速度_T_1.getValue());
        model.addAttribute("ACCOUNT_SPEED_T2", DictCodeList.清算速度_T_2.getValue());
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        model.addAttribute("FINPROD_TYPE_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());

        //--------add by 言伟 2019/05/16------start--------
        model.addAttribute("ETR_PAY_TYPE_01", BussinessDictCodeList.缴款方式_一级分销.getValue());
        model.addAttribute("ETR_PAY_TYPE_02", BussinessDictCodeList.缴款方式_一级缴款.getValue());
        model.addAttribute("ETR_PAY_TYPE_03", BussinessDictCodeList.缴款方式_二级交易.getValue());
        //--------add by 言伟 2019/05/16------End--------

        //--------add by wangpeng 2019/08/16------start--------
        model.addAttribute("WINDTYPEONE_B17", DictCodeList.WIND一级分类_含债券一级分类等_优先股.getValue());
        //--------add by wangpeng 2019/08/16------End--------

        //--------add by wangpeng 2019/09/26------start--------
        model.addAttribute("Collect_Type_01", DictCodeListExt2.收取方式_回款前扣除.getValue());
        //--------add by wangpeng 2019/09/26------End--------


        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE") == null ? "01" : CacheUtil.getSysParam("IS_SHOW_TYPE"));
        model.addAttribute("isShowCprice", CacheUtil.getSysParam("IS_SHOW_CPRICE") == null ? "N" : CacheUtil.getSysParam("IS_SHOW_CPRICE"));
        model.addAttribute("bankCode", CacheUtil.getSysParam("CUSTOMER_BANK_CODE"));
        //画面元素是否显示 add by yzg 20200410
        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        model.addAttribute("isEleShow", isEleShow);
    }
}