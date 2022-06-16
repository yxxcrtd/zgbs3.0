/*
 * 文件名：CfetsEtrInfoQueryService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：sjj
 * 修改时间：2021年11月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.etrinfoquery.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.cfets.etrdispatcher.dao.CfetsEtrDispatcherMapper;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherTradeListResponse;
import com.joyintech.tams.cfets.etrinfoquery.dao.CfetsEtrInfoQueryMapper;
import com.joyintech.tams.cfets.etrinfoquery.dto.CfetsEtrInfoQueryListRequest;
import com.joyintech.tams.cfets.etrinfoquery.dto.CfetsEtrInfoQueryListResponse;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 银行间指令查询
 * @author sjj
 * @version 1.0 2021年11月26日
 * @see CfetsEtrInfoQueryService
 * @since 1.0
 */
@Service("CfetsEtrInfoQueryService")
public class CfetsEtrInfoQueryService extends BaseService{

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(CfetsEtrInfoQueryService.class);

    @Autowired
    private CfetsEtrInfoQueryMapper cfetsEtrInfoQueryMapper;

    @Autowired
    private CfetsEtrDispatcherMapper cfetsEtrDispatcherMapper;

    /**
     * 指令列表分页查询
     * @param dto 入数
     * @return Page<CfetsEtrInfoQueryListResponse>
     */
    public Page<CfetsEtrInfoQueryListResponse> findAllData(CfetsEtrInfoQueryListRequest dto) {

        Map<String,Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("entrustAwait", DictCodeList.委托状态_待接收.getValue());
        pageParam.put("entrustDistribute", DictCodeListExt3.委托状态_待分发.getValue());
        pageParam.put("entrustReceive", DictCodeList.委托状态_已接收.getValue());
        pageParam.put("entrustUntread", DictCodeList.委托状态_已完成.getValue());
        pageParam.put("entrustClose", DictCodeList.委托状态_已关闭.getValue());
        pageParam.put("entrustReturn", DictCodeList.委托状态_已退回.getValue());
        pageParam.put("entrustRecall", DictCodeList.委托状态_已撤回.getValue());
        pageParam.put("entrustInwfl", DictCodeListExt3.委托状态_流程中.getValue());


        pageParam.put("busiTypePosition01", DictCodeListExt3.头寸业务类型_债券一级投标.getValue());
        pageParam.put("busiTypePosition02", DictCodeListExt3.头寸业务类型_现券交易.getValue());
        pageParam.put("busiTypePosition03", DictCodeListExt3.头寸业务类型_债券行权交易.getValue());
        pageParam.put("busiTypePosition05", DictCodeListExt3.头寸业务类型_质押式回购首期.getValue());


        List<String> listTemp = Arrays.asList(dto.getEntrustStatus());
        if(!listTemp.contains("all")){
            pageParam.put("entrustStatusList", listTemp);
        }
        String portfolioId = (String)pageParam.get("portfolioId");
        if(StringUtil.isNotEmpty(pageParam.get("portfolioId"))){
            List<String> portfolioIdList = StringUtil.splitToList(",",(String)pageParam.get("portfolioId"));
            pageParam.put("portfolioIdList", portfolioIdList);
        }

        String finprodMarketId = (String)pageParam.get("finprodMarketId");
        if(StringUtil.isNotEmpty(pageParam.get("finprodMarketId"))){
            List<String> finprodMarketIdList = StringUtil.splitToList(",",(String)pageParam.get("finprodMarketId"));
            pageParam.put("finprodMarketIdList", finprodMarketIdList);
        }

        String secAcctId = (String)pageParam.get("secAcctId");
        if(StringUtil.isNotEmpty(pageParam.get("secAcctId"))){
            List<String> secAcctIdList = StringUtil.splitToList(",",(String)pageParam.get("secAcctId"));
            pageParam.put("secAcctIdList", secAcctIdList);
        }

        String invManagerId = (String)pageParam.get("invManagerId");
        if(StringUtil.isNotEmpty(pageParam.get("invManagerId"))){
            List<String> invManagerIdList = StringUtil.splitToList(",",(String)pageParam.get("invManagerId"));
            pageParam.put("invManagerIdList", invManagerIdList);
        }

        String finprodType2Old = (String)pageParam.get("finprodType2");
        if (StringUtil.isNotEmpty(finprodType2Old) || StringUtil.isNotEmpty(pageParam.get("ps"))) {
            List<String> finprodType2ListOld = StringUtil.splitToList(GlobalConstant.COMMA, finprodType2Old);
            //交易方向为利率互换 转换
            if(StringUtil.isNotEmpty((String)pageParam.get("ps"))){
                if( StringUtil.equals((String)pageParam.get("ps") , DictCode.互换方式.getValue()+DictCodeListExt2.互换方式_收取固定.getValue()) ){
                    pageParam.put("ps", DictCodeListExt2.互换方式_收取固定.getValue());
                    pageParam.put("IRSps", DictCodeListExt2.互换方式_收取固定.getValue());
                }
                if( StringUtil.equals((String)pageParam.get("ps") , DictCode.互换方式.getValue()+DictCodeListExt2.互换方式_收取浮动.getValue()) ){
                    pageParam.put("ps", DictCodeListExt2.互换方式_收取浮动.getValue());
                    pageParam.put("IRSps", DictCodeListExt2.互换方式_收取浮动.getValue());
                }
                pageParam.put("PS_T01", DictCodeList.交易方向_买入.getValue());
            }
        }
        pageParam.put("IRSType_01", DictCodeListExt2.互换方式_收取固定.getValue());
        pageParam.put("IRSType_02", DictCodeListExt2.互换方式_收取浮动.getValue());
        pageParam.put("ASSET_TYPE_07", DictCodeList.平安资产类型_拆借.getValue());
        pageParam.put("ASSET_TYPE_08", DictCodeList.平安资产类型_质押式回购.getValue());
        pageParam.put("ASSET_TYPE_10", DictCodeList.平安资产类型_买断式回购.getValue());
        pageParam.put("ASSET_TYPE_12", DictCodeListExt2.平安资产类型_债券借贷.getValue());
        pageParam.put("ASSET_TYPE_16", DictCodeListExt2.平安资产类型_利率互换.getValue());
        pageParam.put("BACK_LEND_DIRECTION", DictCode.回购拆借方向.getValue());
        pageParam.put("IRS_TYPE", DictCode.互换方式.getValue());
        pageParam.put("PS", DictCode.交易方向.getValue());
        pageParam.put("effectFlag",DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("effectFlag04",DictCodeListExt1.有效状态_流程中.getValue());
        pageParam.put("finType_zq", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("ps_49", DictCodeListExt3.交易方向_债转股.getValue());

        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
            pageParam.put("settleSpeed","0");
        }else if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
            pageParam.put("settleSpeed","1");
        }else if(StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_2.getValue())){
            pageParam.put("settleSpeed","2");
        }
        pageParam.put("sort",dto.getPageMode().getSort());
        pageParam.put("direction",dto.getPageMode().getDirection());

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int year = c.get(Calendar.YEAR);
        pageParam.put("year",year);

        //根据组合权限表添加检索权限
        Page<CfetsEtrInfoQueryListResponse> pageLimit = new Page<CfetsEtrInfoQueryListResponse>();
        if(StringUtil.isNotEmpty(SecurityUtil.getLoginUser())) {
            //查询登录用户是否是领导、交易处长岗位
            Map<String,Object> map = new HashMap<>();
            map.put("rolepos01", DictCodeListExt2.角色岗位_领导.getValue());
            map.put("rolepos03", DictCodeListExt2.角色岗位_交易处长.getValue());
            map.put("userName", SecurityUtil.getLoginUser().getUserName());
            int count = cfetsEtrInfoQueryMapper.queryRolePosByLoginUser(map);
            if(count==0){
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            pageLimit = super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                    () -> cfetsEtrInfoQueryMapper.findAllData(pageParam));
        }else{
            return pageLimit;
        }

        List<CfetsEtrInfoQueryListResponse> list = pageLimit.getRoot();
        if (CollectionUtils.isNotEmpty(list)) {

            for (CfetsEtrInfoQueryListResponse response : list) {
                //一级投标债券 重新计算执行进度和成交进度
                if (StringUtil.equals(response.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券一级投标.getValue()) ||
                        StringUtil.equals(response.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券一级申购.getValue())) {
                    CfetsEtrDispatcherTradeListResponse dto2 = cfetsEtrDispatcherMapper.getEtrFirstBidInfo(response.getEntrustId());
                    if (dto2 != null) {
                        response.setExecFinishAmt(ArithmeticUtil.divide(dto2.getExecFinishAmt(), BigDecimal.valueOf(10000),2));
                        response.setExecRemainderAmt(ArithmeticUtil.divide(dto2.getExecRemainderAmt(), BigDecimal.valueOf(10000),2));
                        response.setExeProgress(dto2.getExeProgress());
                        response.setTransFinishAmt(ArithmeticUtil.divide(dto2.getTransFinishAmt(), BigDecimal.valueOf(10000),2));
                        response.setTransRemainderAmt(ArithmeticUtil.divide(dto2.getTransRemainderAmt(), BigDecimal.valueOf(10000),2));
                        response.setDealProgress(dto2.getDealProgress());
                    }
                }
            }

        }

        pageLimit.setRoot(list);
        //将交易员和投资经理转为真实姓名
//        List<CfetsEtrInfoQueryListResponse> list = pageLimit.getRoot();
//        if(!list.isEmpty()){
//            for(CfetsEtrDispatcherTradeListResponse  cfetsEtrDispatcherTradeListResponse : list){
//                if(StringUtil.isNotEmpty(cfetsEtrDispatcherTradeListResponse.getTrader())){
//                    String[] traders = cfetsEtrDispatcherTradeListResponse.getTrader().split(",");
//                    StringBuffer buffer = new StringBuffer();
//                    StringBuffer userName = new StringBuffer();
//                    for (String trader : traders) {
//                        userName.append(trader + ",");
//                        User user=cfetsEtrInfoQueryMapper.getRealNameByUserName(trader);
//                        if(StringUtil.isNotEmpty(user)){
//                            buffer.append(user.getRealName() + ",");
//                        }
//                    }
//                    if(buffer.toString().length() !=0){
//                        cfetsEtrDispatcherTradeListResponse.setTrader(buffer.toString().substring(0, buffer.toString().length() - 1));
//                    }
////                    if(userName.toString().length() !=0){
////                        cfetsEtrDispatcherTradeListResponse.setUserName(userName.toString().substring(0,userName.toString().length() - 1));
////                    }
//                }
//                if(StringUtil.isNotEmpty(cfetsEtrDispatcherTradeListResponse.getInvManagerId())){
//                    User user1=cfetsEtrDispatcherMapper.getRealNameByUserName(cfetsEtrDispatcherTradeListResponse.getInvManagerId());
//                    if(StringUtil.isNotEmpty(user1)){
//                        cfetsEtrDispatcherTradeListResponse.setInvManagerId(user1.getRealName());
//                    }
//                }
//                if(StringUtil.isNotEmpty(cfetsEtrDispatcherTradeListResponse.getBusiPs())){
//                    //委托指令附属表中取出回购/拆借方向
//                    if(StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), cfetsEtrDispatcherTradeListResponse.getFinprodType2())
//                            || StringUtil.equals(DictCodeList.平安资产类型_拆借.getValue(), cfetsEtrDispatcherTradeListResponse.getFinprodType2()) ){
//                        cfetsEtrDispatcherTradeListResponse.setPs(cfetsEtrDispatcherTradeListResponse.getBusiPs());
//                    }
//                }
//            }
//        }
        return pageLimit;
    }

}
