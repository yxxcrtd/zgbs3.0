/*
 * 文件名：CstpSelect001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年12月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.trade.cstp.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.TrdMatchInfoMapper;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.cstp.dto.CstpSelectDto;
import com.joyintech.tams.invest.trade.investtrade.commandreceive.dto.TrdCommandReceive001Dto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * cstp下行交易查询
 * @author wangxuelei
 * @version 1.0 2018年12月3日
 * @see CstpSelect001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class CstpSelect001Service extends CommonExportService<CstpSelectDto> {

    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;
    @Autowired
    private IPlatformDate iPlatformDate;
    @Autowired
    private TrdMatchInfoMapper trdMatchInfoMapper;
    /**
     * 分页查询
     * @param map 查询参数
     * @return 分页查询结果
     * @see
     * @since 1.0
     */
    public Page<CstpSelectDto> findPage(Map<String,Object> map){
        String isEntrust = (String) map.get("isEntrust");
        if(StringUtils.isNotBlank((String)map.get("matchStatus"))){
            String[] matchStatus = map.get("matchStatus").toString().split(",");
            map.replace("matchStatus", matchStatus);
        }else{
            map.replace("matchStatus", null);
        }
        if(map.get("execid") != null && StringUtil.isNotEmpty(map.get("execid").toString())){
            String[] execid = map.get("execid").toString().split(",");
            map.replace("execid", execid);
        }else{
            map.replace("execid", null);
        }
        if(map.get("tradeDateBegin") != null && !map.get("tradeDateBegin").toString().isEmpty()){
            map.replace("tradeDateBegin",map.get("tradeDateBegin").toString().replace("-", ""));
            map.replace("tradeDateEnd",map.get("tradeDateEnd").toString().replace("-", ""));
        }else{
            map.replace("tradeDateBegin",null);
            map.replace("tradeDateEnd",null);
        }
        map.put("IS_CANCEL",DictCodeList.是否_否.getValue());
        setParam(map);
        Page<CstpSelectDto> cstpSelectDtoPage = restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME
                + "/CSTPSelect/findPage", map, CstpSelectDto.class);
//        if(Boolean.parseBoolean(isEntrust)){
//            this.findEntrustPageByEntrust(map,cstpSelectDtoPage);
//        }
        return cstpSelectDtoPage;
    }

    private void setParam(Map<String,Object> map){
        if(StringUtil.equals(String.valueOf(map.get("isEntrust")),"true")){
            if(map.containsKey("entrustSecAcctId")){
                map.put("secAcctId",map.get("entrustSecAcctId").toString());
            }
            if(map.containsKey("entrustPortfolioId")){
                map.put("portfolioId",map.get("entrustPortfolioId").toString());
            }
            if(map.containsKey("entrustPriceModel")){
                map.put("priceModel",map.get("entrustPriceModel").toString());
            }
            if(map.containsKey("entrustMatchType")){
                map.put("matchType",map.get("entrustMatchType").toString());
            }
            /*if(map.containsKey("entrustSettleSpeed")){
                map.put("settleSpeed",map.get("entrustSettleSpeed").toString());
            }*/
            if(map.containsKey("entrustFinprodType")){
                map.put("finprodType",map.get("entrustFinprodType").toString());
            }
            if(map.containsKey("entrustPrice")){
                map.put("price",map.get("entrustPrice").toString());
            }
            if(map.containsKey("entrustUnderlyingyield2")){
                map.put("underlyingyield2",map.get("entrustUnderlyingyield2").toString());
            }
            if(map.containsKey("entrustStrikeyield")){
                map.put("strikeyield",map.get("entrustStrikeyield").toString());
            }
            if(map.containsKey("entrustTradelimitdays")){
                map.put("tradelimitdays",map.get("entrustTradelimitdays").toString());
            }
            if(map.containsKey("entrustFinprodId")){
                map.put("finprodId",map.get("entrustFinprodId").toString());
            }
            if(map.containsKey("entrustPs")){
                map.put("ps",map.get("entrustPs").toString());
            }
            if(map.containsKey("entrustTradeMarket")){
                map.put("tradeMarket",map.get("entrustTradeMarket").toString());
            }
            if(map.containsKey("entrustTradeDateBegin")){
                map.put("entrustStartDate",map.get("entrustTradeDateBegin").toString());
            }
            if(map.containsKey("entrustTradeDateEnd")){
                map.put("entrustEndDate",map.get("entrustTradeDateEnd").toString());
            }
        }
    }
    /**
     * 获取成交编号下拉框实体类
     * @param map 查询参数
     * @return 下拉框集合
     * @return
     * @see
     * @since 1.0
     */

    public Page<SelectOptionDto> getExecid(Map<String,Object> map){
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME
            + "/CSTPSelect/getExecid",map,SelectOptionDto.class);
    }
    
    /**
     * 报价信息 委托  预交易
     * @param map 查询参数
     * @return 下拉框集合
     * @return
     * @see
     * @since 1.0
     */

    public Page<SelectOptionDto> getBusiCode(Map<String,Object> map){
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME
            + "/CSTPSelect/getBusiCode",map,SelectOptionDto.class);
    }
    
    /**
     * 获取资产代码下拉框
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getFinprodId(Map<String,Object> map){
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
            + "/CSTPSelect/getFinprodId",map,SuggestDto.class);
    }
    /**
     * 导出 
     */
     @Override
     protected void buildExportParam(ExportParam exportParam) {
         exportParam.config(
                 "/com/joyintech/tams/invest/trade/cstp/conf/export_conf.xml").dataSource(DataSourceType.MASERT);
     }
    
     /**
      * {@inheritDoc}
      */
     @Override
     public Page<CstpSelectDto> selectPageData(Map<String, Object> map) {
         return findPage(map);
     }

    /**
     * @description 委托匹配cstp查询
     * @author wangxuelei
     * @date 2019/11/20 0020
      * @param param
 * @param page 
     * @return com.joyintech.framework.grid.Page<com.joyintech.tams.invest.trade.investtrade.commandreceive.dto.TrdCommandReceive001Dto>
     */
     public Page<TrdCommandReceive001Dto> findEntrustPageByCstp(Map<String,Object> param,Page<TrdCommandReceive001Dto> page ){
         BigDecimal price;
         BigDecimal matureYield;
         BigDecimal exerYield;
         List<TrdCommandReceive001Dto> list = new ArrayList<>();
         if(StringUtils.isBlank((String) param.get("price"))){
             price = null;
         }else{
              price = new BigDecimal((String) param.get("price"));
         }
         if(StringUtils.isBlank((String) param.get("matureYield"))){
             matureYield = null;
         }else{
             matureYield = new BigDecimal((String) param.get("matureYield"));
             matureYield = ArithmeticUtil.divide(matureYield,new BigDecimal(100));//除于100
         }
         if(StringUtils.isBlank((String) param.get("exerYield"))){
             exerYield = null;
         }else{
             exerYield = new BigDecimal((String) param.get("exerYield"));
             exerYield =  ArithmeticUtil.divide(exerYield,new BigDecimal(100));
         }
         if(CollectionUtils.isNotEmpty(page.getRoot())){
             List<TrdCommandReceive001Dto> root = page.getRoot();
             for (TrdCommandReceive001Dto dto:root) {
                 if(StringUtils.isNoneBlank(dto.getFinprodType2())
                         &&StringUtil.equals(dto.getTradeType(), DictCodeListExt3.交易类型_投资指令.getValue())){
                    if(StringUtils.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
                        if(StringUtils.isNoneBlank(dto.getPriceMode())){
                            if(StringUtils.equals(dto.getPriceMode(),DictCodeListExt2.价格模式_指定价格.getValue())){
                                if(dto.getPrice()!=null&&price!=null&& ArithmeticUtil.equals(dto.getPrice(),price)){
                                    if(dto.getMatureYield()!=null&&matureYield!=null&&ArithmeticUtil.equals(dto.getMatureYield(),matureYield)){
                                        if(dto.getExerYield()!=null&&exerYield!=null&&ArithmeticUtil.equals(dto.getExerYield(),exerYield)){
                                            list.add(dto);
                                            continue;
                                        }
                                        list.add(dto);
                                        continue;
                                    }else{
                                        continue;
                                    }
                                }else{
                                    continue;
                                }
                            }else if(StringUtils.equals(dto.getPriceMode(),DictCodeListExt2.价格模式_限价指令.getValue())){
                                //买入时净价为上限就是cstp的要比委托的小，到期收益率和权益收益率为下限，cstp要比委托的大
                                if(StringUtils.isNoneBlank(dto.getPs())&&StringUtils.equals(dto.getPs(),DictCodeList.交易方向_买入.getValue())){
                                    if(dto.getPrice()!=null&&price!=null&&(dto.getPrice().compareTo(price)==1||dto.getPrice().compareTo(price)==0) ){
                                        if(dto.getMatureYield()!=null&&matureYield!=null&&(dto.getMatureYield().compareTo(matureYield)==-1||dto.getMatureYield().compareTo(matureYield)==0)){
                                            if(dto.getExerYield()!=null&&exerYield!=null&&(dto.getExerYield().compareTo(exerYield)==-1||dto.getExerYield().compareTo(exerYield)==0)){
                                                list.add(dto);
                                                continue;
                                            }
                                            list.add(dto);
                                            continue;
                                        }else{
                                            continue;
                                        }
                                    }else{
                                        continue;
                                    }
                                }else if(StringUtils.isNoneBlank(dto.getPs())&&StringUtils.equals(dto.getPs(),DictCodeList.交易方向_卖出.getValue())){
                                    //买出时净价为下限就是cstp的要比委托的大，到期收益率和权益收益率为上限，cstp要比委托的小
                                    if(dto.getPrice()!=null&&price!=null&&(dto.getPrice().compareTo(price)==-1||dto.getPrice().compareTo(price)==0) ){
                                        if(dto.getMatureYield()!=null&&matureYield!=null&&(dto.getMatureYield().compareTo(matureYield)==1||dto.getMatureYield().compareTo(matureYield)==0)){
                                            if(dto.getExerYield()!=null&&exerYield!=null&&(dto.getExerYield().compareTo(exerYield)==1||dto.getExerYield().compareTo(exerYield)==0)){
                                                list.add(dto);
                                                continue;
                                            }
                                            list.add(dto);
                                            continue;
                                        }else{
                                            continue;
                                        }
                                    }else{
                                        continue;
                                    }
                                }
                            }else{
                                list.add(dto);
                            }
                        }
                    }else{
                        if(StringUtils.isNoneBlank(dto.getPriceMode())){
                            if(StringUtils.equals(dto.getPriceMode(),DictCodeListExt2.价格模式_指定价格.getValue())){
                                if(dto.getPrice()!=null&&price!=null&& ArithmeticUtil.equals(ArithmeticUtil.multiply(dto.getPrice(),new BigDecimal(100)),price)){
                                    list.add(dto);
                                    continue;
                                }
                            }else if(StringUtils.equals(dto.getPriceMode(),DictCodeListExt2.价格模式_限价指令.getValue())){
                                //正回购时，cstp小于等于委托的回购利率
                                if(StringUtils.isNoneBlank(dto.getPs())&&StringUtils.equals(dto.getPs(),"03")){
                                    if(dto.getPrice()!=null&&price!=null&&(ArithmeticUtil.multiply(dto.getPrice(),new BigDecimal(100)).compareTo(price)==1||ArithmeticUtil.multiply(dto.getPrice(),new BigDecimal(100)).compareTo(price)==0) ){
                                        list.add(dto);
                                        continue;
                                    }
                                }else if(StringUtils.isNoneBlank(dto.getPs())&&StringUtils.equals(dto.getPs(),"04")){
                                    //逆回购时，委托小于等于cstp的回购利率
                                    if(dto.getPrice()!=null&&price!=null&&(ArithmeticUtil.multiply(dto.getPrice(),new BigDecimal(100)).compareTo(price)==-1||ArithmeticUtil.multiply(dto.getPrice(),new BigDecimal(100)).compareTo(price)==0) ){
                                        list.add(dto);
                                        continue;
                                    }
                                }
                            }else{
                                list.add(dto);
                            }
                        }
                    }
                 }
                 //预录入
                 else{
                     list.add(dto);
                 }
             }
         }
         page.setRoot(list);
         return page;
     }

    /**
     * @description cstp匹配委托查询
     * @author wangxuelei
     * @date 2019/11/20 0020
      * @param param
 * @param page 
     * @return com.joyintech.framework.grid.Page<com.joyintech.tams.invest.trade.investtrade.commandreceive.dto.TrdCommandReceive001Dto>
     */
    public Page<CstpSelectDto> findEntrustPageByEntrust(Map<String,Object> param,Page<CstpSelectDto> page ){
        String priceModel = (String) param.get("priceModel");
        String portfolioId = (String)param.get("portfolioId");
        String tradeMarket = (String)param.get("tradeMarket");
        String secAcctId = (String)param.get("secAcctId");
        String matchType = param.get("matchType").toString();
        BigDecimal price;
        BigDecimal underlyingyield2;
        BigDecimal strikeyield;
        List<CstpSelectDto> list = new ArrayList<>();
        if(StringUtils.isBlank((String) param.get("price"))){
            price = null;
        }else{
            price = new BigDecimal((String) param.get("price"));
        }
        if(StringUtils.isBlank((String) param.get("underlyingyield2"))){
            underlyingyield2 = null;
        }else{
            underlyingyield2 = new BigDecimal((String) param.get("underlyingyield2"));
            underlyingyield2 = ArithmeticUtil.multiply(underlyingyield2,new BigDecimal(100));
        }
        if(StringUtils.isBlank((String) param.get("strikeyield"))){
            strikeyield = null;
        }else{
            strikeyield = new BigDecimal((String) param.get("strikeyield"));
            strikeyield = ArithmeticUtil.multiply(strikeyield,new BigDecimal(100));//乘以100
        }
        if(CollectionUtils.isNotEmpty(page.getRoot())){
            List<CstpSelectDto> root = page.getRoot();
            for (CstpSelectDto dto:root) {
                if(StringUtils.isNoneBlank(dto.getBusinesstype())
                        &&StringUtil.equals(matchType,DictCodeListExt3.交易类型_投资指令.getValue())){
                    if(StringUtils.equals(dto.getBusinesstype(),"T01")){
                        if(StringUtils.isNoneBlank(priceModel)){
                            if(StringUtils.equals(priceModel,DictCodeListExt2.价格模式_指定价格.getValue())){
                                if(dto.getPrice()!=null&&price!=null&& ArithmeticUtil.equals(dto.getPrice(),price)){
                                    if(dto.getUnderlyingyield2()!=null&&underlyingyield2!=null&&ArithmeticUtil.equals(dto.getUnderlyingyield2(),underlyingyield2)){
                                        if(dto.getStrikeyield()!=null&&strikeyield!=null&&ArithmeticUtil.equals(dto.getStrikeyield(),strikeyield)){
                                            list.add(dto);
                                            continue;
                                        }
                                        list.add(dto);
                                        continue;
                                    }else{
                                        continue;
                                    }
                                }else{
                                    continue;
                                }
                            }else if(StringUtils.equals(priceModel,DictCodeListExt2.价格模式_限价指令.getValue())){
                                if(StringUtils.isNoneBlank(dto.getSide())&&StringUtils.equals(dto.getSide(),DictCodeList.交易方向_买入.getValue())){
                                    //买入时净价为上限就是cstp的要比委托的小，到期收益率和权益收益率为下限，cstp要比委托的大
                                    if(dto.getPrice()!=null&&price!=null&&(dto.getPrice().compareTo(price)==-1||dto.getPrice().compareTo(price)==0) ){
                                        if(dto.getUnderlyingyield2()!=null&&underlyingyield2!=null&&(dto.getUnderlyingyield2().compareTo(underlyingyield2)==1||dto.getUnderlyingyield2().compareTo(underlyingyield2)==0)){
                                            if(dto.getStrikeyield()!=null&&strikeyield!=null&&(dto.getStrikeyield().compareTo(strikeyield)==1||dto.getStrikeyield().compareTo(strikeyield)==0)){
                                                list.add(dto);
                                                continue;
                                            }
                                            list.add(dto);
                                            continue;
                                        }else{
                                            continue;
                                        }
                                    }else{
                                        continue;
                                    }
                                }else if(StringUtils.isNoneBlank(dto.getSide())&&StringUtils.equals(dto.getSide(),DictCodeList.交易方向_卖出.getValue())){
                                    //买出时净价为下限就是cstp的要比委托的大，到期收益率和权益收益率为上限，cstp要比委托的小
                                    if(dto.getPrice()!=null&&price!=null&&(dto.getPrice().compareTo(price)==1||dto.getPrice().compareTo(price)==0) ){
                                        if(dto.getUnderlyingyield2()!=null&&underlyingyield2!=null&&(dto.getUnderlyingyield2().compareTo(underlyingyield2)==-1||dto.getUnderlyingyield2().compareTo(underlyingyield2)==0)){
                                            if(dto.getStrikeyield()!=null&&strikeyield!=null&&(dto.getStrikeyield().compareTo(strikeyield)==-1||dto.getStrikeyield().compareTo(strikeyield)==0)){
                                                list.add(dto);
                                                continue;
                                            }
                                            list.add(dto);
                                            continue;
                                        }else{
                                            continue;
                                        }
                                    }else{
                                        continue;
                                    }
                                }
                            }else{
                                list.add(dto);
                            }
                        }
                    }else{
                        if(StringUtils.isNoneBlank(priceModel)){
                            if(StringUtils.equals(priceModel,DictCodeListExt2.价格模式_指定价格.getValue())){
                                if(dto.getPrice()!=null&&price!=null&& ArithmeticUtil.equals(dto.getPrice(),ArithmeticUtil.multiply(price,new BigDecimal(100)))){
                                    list.add(dto);
                                    continue;
                                }
                            }else if(StringUtils.equals(priceModel,DictCodeListExt2.价格模式_限价指令.getValue())){
                                //正回购时，cstp小于等于委托的回购利率
                                if(StringUtils.isNoneBlank(dto.getSide())&&StringUtils.equals(dto.getSide(),"03")){
                                    if(dto.getPrice()!=null&&price!=null&&(dto.getPrice().compareTo(ArithmeticUtil.multiply(price,new BigDecimal(100)))==-1||dto.getPrice().compareTo(ArithmeticUtil.multiply(price,new BigDecimal(100)))==0) ){
                                        list.add(dto);
                                        continue;
                                    }
                                }else if(StringUtils.isNoneBlank(dto.getSide())&&StringUtils.equals(dto.getSide(),"04")){
                                    if(dto.getPrice()!=null&&price!=null&&(dto.getPrice().compareTo(ArithmeticUtil.multiply(price,new BigDecimal(100)))==1||dto.getPrice().compareTo(ArithmeticUtil.multiply(price,new BigDecimal(100)))==0) ){
                                        list.add(dto);
                                        continue;
                                    }
                                }
                            }else{
                                list.add(dto);
                            }
                        }
                    }
                }else{//预录入的
                    list.add(dto);
                }
                //w委托没有选择证券账户的时候，查询组合的证券账户
                if(StringUtil.isBlank(secAcctId)){
                    if(StringUtil.isNoneBlank(portfolioId)){
                        Map<String, Object> parameters = new HashMap<>();
                        parameters.put("portfolioId",portfolioId);
                        parameters.put("tradeMarket",tradeMarket);
                        //证券账户的资金账户
                        List<SelectOptionDto> secManageAcctId = trdBondInvestTrade002Service.getSecManageAcctId(parameters);
                        for (SelectOptionDto select:secManageAcctId) {

                            if(StringUtil.equals(select.getId(),dto.getOurdepositaccountnumber())){
                                list.add(dto);
                                break;
                            }
                        }
                    }
                }
            }
        }
        page.setRoot(list);
        return page;
    }
}
