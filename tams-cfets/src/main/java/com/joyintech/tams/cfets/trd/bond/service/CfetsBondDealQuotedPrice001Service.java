/*
 * 文件名：CfetsBondDealQuotedPrice001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trd.bond.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.bankuplink.bond.service.CfetsBondDealQuotedPriceService;
import com.joyintech.service.bankuplink.ibu.dto.BondQuoteInfoDto;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDayPriceDto;
import com.joyintech.tams.component.business.basicdata.dto.BusiFinProductRatingDto;
import com.joyintech.tams.component.business.basicdata.service.BusiProductRatingService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * CFETS现券买卖报价
 * 
 * @author zhangliang
 * @version 1.0 2020年5月12日
 * @see CfetsBondDealQuotedPrice001Service
 * @since 1.0
 */
@Service("vuefetsBondDealQuotedPrice001Service")
public class CfetsBondDealQuotedPrice001Service {

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 产品评级共通
     */
    @Autowired(required = false)
    private BusiProductRatingService busiProductRatingService;

    @Autowired
    private CfetsBondDealQuotedPriceService cfetsBondDealQuotedPriceService;

    public List<CfetsBondDayPriceDto> listDayPrice(Map<String, Object> param){
        List<CfetsBondDayPriceDto> result = new ArrayList<>();
        List<BondQuoteInfoDto> dayOfferTableList = getDayOfferTableList(param);
        if(CollectionUtils.isNotEmpty(dayOfferTableList)){
            dayOfferTableList.forEach(item ->{
                CfetsBondDayPriceDto cfetsBondDayPriceDto = new CfetsBondDayPriceDto();
                BeanUtil.copyPropertiesIgnoreNull(cfetsBondDayPriceDto,item);
                cfetsBondDayPriceDto.setOuPartyName(item.getPartyAbbr());
                cfetsBondDayPriceDto.setCpPartyName(item.getCpPartyName());
                cfetsBondDayPriceDto.setPrice(item.getUnitCprice());
                cfetsBondDayPriceDto.setMatureYield(item.getYield());
                cfetsBondDayPriceDto.setShareAmt(item.getPrinAmtHun());
                cfetsBondDayPriceDto.setExecid(item.getfTradeId());
                result.add(cfetsBondDayPriceDto);
            });
        }
        return result;
    }

    public List<com.joyintech.service.bankuplink.ibu.dto.BondQuoteInfoDto> getDayOfferTableList(Map<String, Object> param) {
        User user = SecurityUtil.getLoginUser();
        if (user == null) {
            return Collections.emptyList();
        }
        param.put("loginUserId", user.getUserName());
        //add by zznn 20210730 start
        //岗位信息 框架没有封装  单独处理
        //交易主管(交易处长03)、超级管理员能够查询出所有的报价信息
        Map<String,Object> paramUser = new HashMap<>();
        paramUser.put("userId",user.getUserName());
        paramUser.put("rolePos","03");
        boolean traDirector = commonBusinessService.isTraDirector(paramUser);
        param.put("isAdmin", user.isAdmin()||traDirector);
        //add by zznn 20210730 end
        // 待回复报价是否所有交易员可见（01：不允许，02：允许）
        param.put("unAnswerFlag", CacheUtil.getSysParam("ibuFlag_allTraderCanShowUnAnswerQuote"));
        /*String keywords = "";
        if(param.containsKey("keywords")){
            keywords =(String) param.get("keywords");
            param.remove("keywords");
        }*/
        List<com.joyintech.service.bankuplink.ibu.dto.BondQuoteInfoDto> res = cfetsBondDealQuotedPriceService.getDayOfferTableList(param);

        // 债项评级
        for(com.joyintech.service.bankuplink.ibu.dto.BondQuoteInfoDto dto : res ){
            Map<String,Object> zxparam = new HashMap<String,Object>();
            zxparam.put("oCode", dto.getFinprodMarketId());
            zxparam.put("oType", DictCodeList.评级类型_债项评级.getValue());
            //查询评级类型是债项评级的数据
            List<BusiFinProductRatingDto> zxList = busiProductRatingService.getZxBestRatingByMarketId(zxparam);
            if(zxList != null && zxList.size()>0){
                //只取第一条数据
                dto.setGrade(zxList.get(0).getGradeResult());
            }
        }
        /*if(StringUtil.isNotEmpty(keywords)&&!StringUtil.isEmptyList(res)){
            res = (List<com.joyintech.service.bankuplink.ibu.dto.BondQuoteInfoDto>) getKeyWordsList(res,keywords,(String) param.get("keycols"));
        }*/
        return res;
    }


    private List<?> getKeyWordsList(List<?> list,String keywords,String keycols){
        if(StringUtil.isEmptyList(list)){
            return list;
        }
        if(!StringUtil.isNotEmpty(keywords)){
            return list;
        }
        if(!StringUtil.isNotEmpty(keycols)){
            return list;
        }
        List <String> keycolList = StringUtil.splitToList(GlobalConstant.COMMA,keycols);
        List<Object> containsKeyList = new ArrayList<>();
        for(Object cstpSelectDto:list){
            Map<String, Object> map = JsonUtils.object2Map(cstpSelectDto);
            for(String col:keycolList){
                Object value = map.get(col);
                if(StringUtil.isNotEmpty(value)&&value.toString().contains(keywords)){
                    containsKeyList.add(cstpSelectDto);
                    break;
                }
            }
        }
        return containsKeyList;
    }
}
