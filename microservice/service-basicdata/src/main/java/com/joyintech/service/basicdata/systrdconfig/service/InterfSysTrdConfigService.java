package com.joyintech.service.basicdata.systrdconfig.service;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.service.basicdata.systrdconfig.dao.InterfSysTrdConfigMapper;
import com.joyintech.service.basicdata.systrdconfig.dto.*;
import com.joyintech.tams.common.base.dao.MstCounterPartyInfoMapper;
import com.joyintech.tams.common.base.entity.MstCounterPartyInfo;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname InterfSysTrdConfigService
 * @Description TODO
 * @author zhuhanlu 
 * @date 2020/10/13 11:41 
 * @version 1.0 
 */
@Service
public class InterfSysTrdConfigService{
    @Autowired
    private InterfSysTrdConfigMapper interfSysTrdConfigMapper;

    @Autowired
    private MstCounterPartyInfoMapper mstCounterPartyInfoMapper;

    //平台日组件
    @Autowired
    IPlatformDate iPlatformDateImpl;



    /**
     *
     * @Description //查询最小价差
     * @Param [map]
     * @return java.util.List<com.joyintech.service.basicdata.systrdconfig.dto.MinPirceDto>
     * @version 1.0
     * @see
    **/
    public Map<String,Object> getMinPrice(Map<String,Object> map){
            map.put("date",iPlatformDateImpl.getSysDateStr());
            Map<String,Object> returnMap = new HashMap<String,Object>();
        if (map.get("assetType")!=null && map.get("assetType")!=""&&map.get("assetType").equals("ZYSHG")){
            map.put("assetType","F07");
        }
        if (map.get("finprodId")!=null && map.get("finprodId")!=""){
            if(StringUtil.equals(map.get("assetType"),"F07")){
                Map<String,Object> map2 = interfSysTrdConfigMapper.getMstRepoProductInfo(map);
                map.put("tradeMarket",map2.get("TRADE_MARKET"));
            }else{
                Map<String,Object> map1 = interfSysTrdConfigMapper.getFinProdInfo(map);
                map.put("tradeMarket",map1.get("TRADE_MARKET"));
                map.put("assetType",map1.get("TYPE_8"));
                map.put("assetType1",map1.get("TYPE_6"));
                map.put("assetType2",map1.get("TYPE_7"));
            }
        }
        returnMap.put("errorCode",0);
        returnMap.put("errorMsg","成功");
        List<MinPirceDto> minPirceConfigDtoList = interfSysTrdConfigMapper.getMinPriceConfig(map);
        if (minPirceConfigDtoList.size() != 1){
            List<MinPirceDto> minPirceDtoList = interfSysTrdConfigMapper.getMinPrice(map);
            returnMap.put("data",minPirceDtoList);  //当有多条数据时候，直接返回
        }
        else {
            map.put("tradeMarket",minPirceConfigDtoList.get(0).getTradeMarket());
            map.put("assetType",minPirceConfigDtoList.get(0).getAssetType());
            map.put("assetType1",minPirceConfigDtoList.get(0).getAssetType1());
            map.put("assetType2",minPirceConfigDtoList.get(0).getAssetType2());
            String isGrade = minPirceConfigDtoList.get(0).getIsGrade();
            List<MinPirceDto> minPirceDtoList = interfSysTrdConfigMapper.getMinPrice(map);
            if (isGrade.equals(DictCodeList.是否_否.getValue())){
                returnMap.put("data",minPirceDtoList);           //只有一条数据，不分档时候返回
            }
            else {
                if (map.get("currPrice") !=null && map.get("currPrice")!=""){        //当未传入CurrPirce ，直接返回
                  List<MinPirceDto> returnList = new ArrayList<MinPirceDto>();
                  for (MinPirceDto minPirceDto : minPirceDtoList){
                      BigDecimal gradeAmt = minPirceDto.getGradeAmt();
                      String gradeCondition = minPirceDto.getGradeCondition();
                      int flag = gradeAmt.compareTo(new BigDecimal(map.get("currPrice").toString()));
                      if (gradeCondition.equals(BussinessDictCodeListRds.控制类型_小于.getValue())){
                          if (flag > 0){
                              returnList.add(minPirceDto);
                              returnMap.put("data",returnList);break;
                          }
                      }
                      else if (gradeCondition.equals(BussinessDictCodeListRds.控制类型_小于等于.getValue())){
                          if (flag >= 0){
                              returnList.add(minPirceDto);
                              returnMap.put("data",returnList);break;}
                      }
                      else if (gradeCondition.equals(BussinessDictCodeListRds.控制类型_大于.getValue())){
                          if (flag < 0){
                              returnList.add(minPirceDto);
                              returnMap.put("data",returnList);break;}
                      }
                      else if (gradeCondition.equals(BussinessDictCodeListRds.控制类型_大于等于.getValue())){
                          if (flag <= 0){
                              returnList.add(minPirceDto);
                              returnMap.put("data",returnList);break;}
                      }
                  }
                }
                else returnMap.put("data",minPirceDtoList);
            }

        }
        return returnMap;
    }

    /**
     *
     * @Description //查询最小交易单位，交易上下限数据
     * @Param [map]
     * @return java.util.List<com.joyintech.service.basicdata.systrdconfig.dto.MinTrdUnitLimitDto>
     * @version 1.0
     * @see
    **/
    public Map<String,Object> getMinTrdUnitLimit(Map<String,Object> map){
            map.put("date",iPlatformDateImpl.getSysDateStr());
        if (map.get("assetType")!=null && map.get("assetType")!=""&&map.get("assetType").equals("ZYSHG")){
            map.put("assetType","F07");
        }
        if (map.get("finprodId")!=null && map.get("finprodId")!=""){
            Map<String,Object> map1 = interfSysTrdConfigMapper.getFinProdInfo(map);
            if (map1 != null) {
                map.put("tradeMarket",map1.get("TRADE_MARKET"));
                map.put("assetType",map1.get("TYPE_8"));
                map.put("assetType1",map1.get("TYPE_6"));
                map.put("assetType2",map1.get("TYPE_7"));
                map.put("marketType",map1.get("BOARD"));
            }
        }
        Map<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("errorCode",0);
        returnMap.put("errorMsg","成功");
        returnMap.put("data",interfSysTrdConfigMapper.getMinTrdUnitLimit(map));
        return returnMap;
    }


    /**
     *
     * @Description //查询交易时间段
     * @Param []
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @version 1.0
     * @see
     **/
    public Map<String,Object> getTrdTimeConfig(Map<String,Object> map){
        if (map.get("assetType")!=null && map.get("assetType")!=""&& map.get("assetType").equals("ZYSHG")){
            map.put("assetType","F07");
        }
        if (map.get("finprodId")!=null && map.get("finprodId")!=""){
            Map<String,Object> map1 = interfSysTrdConfigMapper.getFinProdInfo(map);
            map.put("tradeMarket",map1.get("TRADE_MARKET"));
            map.put("assetType",map1.get("TYPE_8"));
            map.put("assetType1",map1.get("TYPE_6"));
            map.put("assetType2",map1.get("TYPE_7"));

        }
        Map<String,Object> returnMap = new HashMap<String,Object>();
        Map<String,Object> tempmap = new HashMap<String,Object>();
        tempmap.put("tradeMarket",map.get("tradeMarket"));
        tempmap.put("tradePlat",map.get("tradePlat"));
        returnMap.put("errorCode",0);
        returnMap.put("errorMsg","成功");
        List<TrdTimeConfigDto> dtoList = interfSysTrdConfigMapper.getTrdTimeConfig(tempmap);   //根据交易场所和交易平台查出交易时间段
       if (dtoList.size()==0)
       {
           returnMap.put("data",new ArrayList<TrdTimeConfigDto>());
       }
       else {
           if (dtoList.get(0).getAssetType()==null && dtoList.get(0).getDeclarationType()==null){
               map.remove("assetType");
               map.remove("assetType1");
               map.remove("assetType2");
               map.remove("declarationType");
               returnMap.put("data",interfSysTrdConfigMapper.getTrdTimeConfig(map));
           }
           else if (dtoList.get(0).getAssetType()!=null&& dtoList.get(0).getDeclarationType()==null){
               map.remove("declarationType");
               returnMap.put("data",interfSysTrdConfigMapper.getTrdTimeConfig(map));
           }

           else if (dtoList.get(0).getAssetType()!=null&& dtoList.get(0).getDeclarationType()!=null){
               returnMap.put("data",interfSysTrdConfigMapper.getTrdTimeConfig(map));
           }

           else {
               map.remove("assetType");
               map.remove("assetType1");
               map.remove("assetType2");
               returnMap.put("data",interfSysTrdConfigMapper.getTrdTimeConfig(map));
           }
       }
        return returnMap;
    }


    public Map<String,Object> getTrdOthCodeInfoDto(Map<String,Object> map){
        Map<String,Object> returnMap = new HashMap<String,Object>();
        map.put("FINPROD_OTH_CODE_TYPE_02", DictCodeListExt3.金融产品其他代码类型_质押代码.getValue());
        returnMap.put("errorCode",0);
        returnMap.put("errorMsg","成功");
        returnMap.put("data",interfSysTrdConfigMapper.getTrdOthCodeInfo(map));
        return returnMap;
    }


    /**
     * 查询交易对手信息
     * @param params
     * @return
     */
    public List<Map<String, Object>> selectMstCounterParty(Map<String, Object> params) {
        params.put("counterType", DictCodeListExt1.交易对手类型_市场机构.getValue());
        return interfSysTrdConfigMapper.selectMstCounterParty(params);
    }

    /**
     * 根据交易对手代码和席位代码模糊查询交易对手席位
     * @param params
     * @return
     */
    public List<Map<String, Object>> selectCounterSeatId(Map<String, Object> params) {
        return interfSysTrdConfigMapper.selectCounterSeatId(params);
    }
    /**
     * 根据交易对手代码和对手方交易员代码模糊查询对手方交易员
     * @param params
     * @return
     */
    public List<Map<String, Object>> selectCounterTraderId(Map<String, Object> params) {
        return interfSysTrdConfigMapper.selectCounterTraderId(params);
    }

    public Map<String, Object> insertCounterTraderId(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        if (params.get("counterId") == null || params.get("counterTraderId") == null){
            resultMap.put("errorNo",9);
            resultMap.put("errorInfo","参数缺失");
            return resultMap;
        }
        MstCounterPartyInfo mstCounterPartyInfo = interfSysTrdConfigMapper.findMstCounterPartyInfo(params);
        if (mstCounterPartyInfo != null){
            resultMap.put("errorNo",9);
            resultMap.put("errorInfo","数据已存在");
            return resultMap;
        }
        String counterId = params.get("counterId").toString();
        String counterTraderId = params.get("counterTraderId").toString();
        mstCounterPartyInfo = new MstCounterPartyInfo();
        mstCounterPartyInfo.setSeqno(IDGenerator.generate());
        mstCounterPartyInfo.setCounterId(counterId);
        mstCounterPartyInfo.setCounterTraderId(counterTraderId);

        mstCounterPartyInfoMapper.insert(mstCounterPartyInfo);
        resultMap.put("errorNo",0);
        resultMap.put("errorInfo","新增成功");
        return resultMap;
    }

    public Map<String, Object> insertCounterSeatId(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        if (params.get("counterId") == null || params.get("counterSeatId") == null){
            resultMap.put("errorNo",9);
            resultMap.put("errorInfo","参数缺失");
            return resultMap;
        }
        MstCounterPartyInfo mstCounterPartyInfo = interfSysTrdConfigMapper.findMstCounterPartyInfo(params);
        if (mstCounterPartyInfo != null){
            resultMap.put("errorNo",9);
            resultMap.put("errorInfo","数据已存在");
            return resultMap;
        }
        String counterId = params.get("counterId").toString();
        String counterSeatId = params.get("counterSeatId").toString();
        mstCounterPartyInfo = new MstCounterPartyInfo();
        mstCounterPartyInfo.setSeqno(IDGenerator.generate());
        mstCounterPartyInfo.setCounterId(counterId);
        mstCounterPartyInfo.setCounterSeatId(counterSeatId);

        mstCounterPartyInfoMapper.insert(mstCounterPartyInfo);
        resultMap.put("errorNo",0);
        resultMap.put("errorInfo","新增成功");
        return resultMap;
    }


    public Map<String,Object> getMoreConvrate(Map<String,Object> map){
        Map<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("errorCode",0);
        returnMap.put("errorMsg","成功");
        returnMap.put("data",interfSysTrdConfigMapper.getMoreConvrate(map));
        return returnMap;
    }

    public Map<String,Object> getAllConvrate(Map<String,Object> map){
        Map<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("errorCode",0);
        returnMap.put("errorMsg","成功");
        map.put ("all", DictCodeListExt3.所有_所有.getValue ());
        returnMap.put("data",interfSysTrdConfigMapper.getAllConvrate(map));
        return returnMap;
    }

    public Map<String, Object> getPurchQuotaInfo(Map<String, Object> params) {
        Map<String,Object> returnMap = new HashMap<String,Object>();
        params.put("date",iPlatformDateImpl.getSysDateStr());
        returnMap.put("errorCode",0);
        returnMap.put("errorMsg","成功");
        returnMap.put("data",interfSysTrdConfigMapper.getPurchQuotaInfo(params));
        return returnMap;

    }


    public List<TradeInfoProResponse> getDealerTraderInfo(TradeInfoProRequest dto) {
        return interfSysTrdConfigMapper.getDealerTraderInfo(dto);
    }
}
