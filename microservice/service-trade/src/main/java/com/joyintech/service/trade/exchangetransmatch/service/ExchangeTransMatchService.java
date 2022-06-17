package com.joyintech.service.trade.exchangetransmatch.service;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.trade.exchangetransmatch.dao.ExchangeTransMatchMapper;
import com.joyintech.service.trade.exchangetransmatch.dto.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExchangeTransMatchService extends BaseService {

    @Autowired
    private ExchangeTransMatchMapper exchangeTransMatchMapper;


    /**
     * 查询成交数据
     *
     * @param map
     * @param page
     * @param length
     * @return
     */
    public Page<ExchangeTransMatch002Dto> findPage(Map<String, Object> map, int page, int length) {
        map.put("TRD_MATCH_STATUS_01", DictCodeListExt3.下行交易匹配状态_未匹配.getValue());
        map.put("TRD_MATCH_STATUS_02",DictCodeListExt3.下行交易匹配状态_部分匹配.getValue());
        map.put("SEC_TYPE_09", DictCodeListExt1.证券品种_回购.getValue());
        map.put("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        map.put("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        map.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        map.put("TARGET_BACK_TYPE_H01", DictCodeList.回购类型_质押式协议回购.getValue());
        map.put("PS_T07", DictCodeList.交易方向_到期.getValue());

        return super.queryForPage(page, length, () -> exchangeTransMatchMapper.findPage(map));
    }


    public Page<ExchangeTransMatch004Dto> findTransMatchInfoPage(Map<String, Object> map, int page, int length) {
        map.put("TRD_MATCH_STATUS_0", DictCodeListExt3.下行交易匹配状态_未匹配.getValue());
        map.put("TRD_MATCH_STATUS_1",DictCodeListExt3.下行交易匹配状态_部分匹配.getValue());
        map.put("TRD_MATCH_STATUS_2",DictCodeListExt3.下行交易匹配状态_匹配完成.getValue());
        map.put("ENTRUST_CLASS_02",DictCodeListExt3.投资指令类型_多组合单资产指令.getValue());
        map.put("SEC_TYPE_09", DictCodeListExt1.证券品种_回购.getValue());
        map.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        if(StringUtil.isNotEmpty(map.get("finprodType2"))) {
            if(StringUtil.equals((String)map.get("finprodType2"),DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                map.put("secType", DictCodeListExt1.证券品种_债券.getValue());
            }
            else if(StringUtil.equals((String)map.get("finprodType2"),DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
                map.put("secType", DictCodeListExt1.证券品种_基金.getValue());
            }
            else if(StringUtil.equals((String)map.get("finprodType2"),DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                map.put("secType", DictCodeListExt1.证券品种_股票.getValue());
            }
            else if(StringUtil.equals((String)map.get("finprodType2"),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
            || StringUtil.equals((String)map.get("finprodType2"),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                map.put("secType", DictCodeListExt1.证券品种_回购.getValue());
            }
        }

        return super.queryForPage(page, length, () -> exchangeTransMatchMapper.findTransMatchInfoPage(map));
    }

    /**
     * 查询委托交易数据
     *
     * @param map
     * @param page
     * @param length
     * @return
     */
    public Page<ExchangeTransMatch003Dto> findEntrustPage(Map<String, Object> map, int page, int length) {
        map.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        map.put("ENTRUST_STATUS_03",DictCodeList.委托状态_已接收.getValue());
        map.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        map.put("ENTRUST_CLASS_02",DictCodeListExt3.投资指令类型_多组合单资产指令.getValue());

        map.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        map.put("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        map.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        map.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        map.put("FINPROD_TYPE_TG_F32", BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        map.put("FINPROD_TYPE_TG_F37", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());

        map.put("PS_T93", DictCodeListExt3.交易方向_解除质押.getValue());
        map.put("PS_T94", DictCodeListExt3.交易方向_中途换券.getValue());

        map.put("SEC_TYPE_01", DictCodeListExt1.证券品种_债券.getValue());
        map.put("SEC_TYPE_06", DictCodeListExt1.证券品种_股票.getValue());
        map.put("SEC_TYPE_02", DictCodeListExt1.证券品种_基金.getValue());
        map.put("SEC_TYPE_09", DictCodeListExt1.证券品种_回购.getValue());
        map.put("SEC_TYPE_14", DictCodeListStl.证券品种_信用保护凭证.getValue());

        map.put("TARGET_CLASS_01", DictCodeListExt3.目标类型_数量.getValue());
        map.put("TARGET_CLASS_02", DictCodeListExt3.目标类型_金额.getValue());

        map.put("TRADE_TYPE_02", DictCodeListExt3.交易类型_投资指令.getValue());
        map.put("TRADE_TYPE_03", DictCodeListExt3.交易类型_交易数据.getValue());

        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        map.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());

        // GNS-956:特殊场景不加日期条件查询指令 modify by fuyongsheng 20220224 start
        String ps = String.valueOf(map.get("ps"));
        String finprodType = String.valueOf(map.get("finprodType1"));
        if(com.joyintech.framework.util.StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(),ps)
                || (com.joyintech.framework.util.StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), finprodType)
                && com.joyintech.framework.util.StringUtil.equals(DictCodeList.交易方向_申购.getValue(), ps)
        )
                || (com.joyintech.framework.util.StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue(), finprodType)
                && com.joyintech.framework.util.StringUtil.equals(DictCodeList.交易方向_申购.getValue(), ps)
        )
                || com.joyintech.framework.util.StringUtil.equals(DictCodeListExt3.交易方向_配债.getValue(), ps)
                || com.joyintech.framework.util.StringUtil.equals(DictCodeListExt2.交易方向_配股.getValue(), ps)
        ){
            map.put("dealDate", null);
        }
        // GNS-956:特殊场景不加日期条件查询指令 modify by fuyongsheng 20220224 end

        return super.queryForPage(page, length, () -> exchangeTransMatchMapper.findEntrustPage(map));
    }

    /**
     * 获取资产代码下拉框
     * @param map
     * @param page
     * @param length
     * @return
     */
    public Page<SelectOptionDto> getFinprodId(Map<String, Object> map, int page, int length) {
        return super.queryForPage(page, length, () -> exchangeTransMatchMapper.getFinprodId(map));
    }

    /**
     * 获取交易编号下拉框
     * @param map
     * @param page
     * @param length
     * @return
     */
    public Page<SelectOptionDto> getBusiCode(Map<String, Object> map, int page, int length) {
        map.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        map.put("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        map.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        map.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        map.put("FINPROD_TYPE_TG_F37", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());

        return super.queryForPage(page, length, () -> exchangeTransMatchMapper.getBusiCode(map));
    }

    /**
     * 获取申请编号下拉框
     * @param map
     * @param page
     * @param length
     * @return
     */
    public Page<SelectOptionDto> getApplyNo(Map<String, Object> map, int page, int length) {
        map.put("TRD_MATCH_STATUS_0", DictCodeListExt3.下行交易匹配状态_未匹配.getValue());
        map.put("TRD_MATCH_STATUS_1",DictCodeListExt3.下行交易匹配状态_部分匹配.getValue());
        map.put("TRD_MATCH_STATUS_2",DictCodeListExt3.下行交易匹配状态_匹配完成.getValue());
        return super.queryForPage(page, length, () -> exchangeTransMatchMapper.getApplyNo(map));
    }


    /**
     * 获取证券账户下拉框
     * @param map
     * @param page
     * @param length
     * @return
     */
    public Page<SelectOptionDto> getSecAcctNo(Map<String, Object> map, int page, int length) {
        return super.queryForPage(page, length, () -> exchangeTransMatchMapper.getSecAcctNo(map));
    }

    /**
     * 获取成交编号下拉框
     * @param map
     * @param page
     * @param length
     * @return
     */
    public Page<SelectOptionDto> getDealId(Map<String, Object> map, int page, int length) {
        map.put("TRD_MATCH_STATUS_01",DictCodeListExt3.下行交易匹配状态_未匹配.getValue());
        map.put("TRD_MATCH_STATUS_02",DictCodeListExt3.下行交易匹配状态_部分匹配.getValue());

        return super.queryForPage(page, length, () -> exchangeTransMatchMapper.getDealId(map));
    }

    /**
     * 获取委托信息
     * @param seqNo
     * @return
     */
    public List<ExchangeTransMatch003Dto> findEntrustList(String seqNo) {
        Map<String,Object> map = new HashMap<>();
        map.put("seqNo",seqNo);
        map.put("SEC_TYPE_09", DictCodeListExt1.证券品种_回购.getValue());
        map.put("TRD_MATCH_STATUS_1",DictCodeListExt3.下行交易匹配状态_部分匹配.getValue());
        map.put("TRD_MATCH_STATUS_2",DictCodeListExt3.下行交易匹配状态_匹配完成.getValue());
        map.put("TRADE_TYPE_02", DictCodeListExt3.交易类型_投资指令.getValue());
        map.put("ENTRUST_CLASS_02",DictCodeListExt3.投资指令类型_多组合单资产指令.getValue());
        return  exchangeTransMatchMapper.findEntrustList(map);
    }

    /**
     * 获取成交信息
     * @param seqNo
     * @return
     */
    public List<ExchangeTransMatch002Dto> findDealList(String seqNo) {
        Map<String,Object> map = new HashMap<>();
        map.put("seqNo",seqNo);
        map.put("SEC_TYPE_09", DictCodeListExt1.证券品种_回购.getValue());
        map.put("TRD_MATCH_STATUS_1",DictCodeListExt3.下行交易匹配状态_部分匹配.getValue());
        map.put("TRD_MATCH_STATUS_2",DictCodeListExt3.下行交易匹配状态_匹配完成.getValue());
        return  exchangeTransMatchMapper.findDealList(map);
    }
}
