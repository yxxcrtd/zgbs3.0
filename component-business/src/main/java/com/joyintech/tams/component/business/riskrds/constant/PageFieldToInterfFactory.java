package com.joyintech.tams.component.business.riskrds.constant;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;

public class PageFieldToInterfFactory {

    /*************************************************************/
    /**                        *接口字段区域 *                        */
    /*************************************************************/
    public static final String INF_EVENTDATE = "eventDate";
    public static final String INF_EVENTID = "eventId";
    public static final String INF_PORTFOLIOCODE = "portfolioCode";
    public static final String INF_ASSETCODE = "assetCode";
    public static final String INF_ASSETTYPE = "assetType";
    public static final String INF_ASSETTYPE2 = "assetType2";
    public static final String INF_PS = "ps";
    public static final String INF_AMOUNT = "amount";
    public static final String INF_PRICEAMT = "priceAmt";
    public static final String INF_TRADENUM = "tradeNum";
    public static final String INF_NETPRICE = "netPrice";
    public static final String INF_ALLPRICE = "allPrice";
    public static final String INF_UNITNET = "unitNet";
    public static final String INF_ASSETRATE = "assetRate";
    public static final String INF_EVENTTYPE = "eventType";
    public static final String INF_ENTRUSTCODE = "entrustCode";
    public static final String INF_TRADER = "trader";
    public static final String INF_MANAGER = "manager";
    public static final String INF_DELIVERYDATE = "deliveryDate";
    public static final String INF_STARTDATE = "startDate";
    public static final String INF_ENDDATE = "endDate";
    public static final String INF_ISWARNNEEDCONFIRM = "isWarnNeedConfirm";
    public static final String INF_ISAPPRNEEDCONFIRM = "isApprNeedConfirm";
    public static final String INF_PREOCCUPYCODE = "preOccupyCode";
    public static final String INF_SOURCESYS = "sourceSys";
    public static final String INF_COUNTERID = "counterId";
    public static final String INF_MARKETCODE = "marketCode";
    public static final String INF_TRADEPLACE = "tradePlace";
    public static final String INF_CCY = "ccy";
    public static final String INF_MDATE = "mdate";
    public static final String INF_RSK_CALC_LIST = "rskCalcList";
    public static final String INF_RSK_LIST = "RSK_LIST";
    public static final String INF_CODE = "infCode";
    public static final String INF_REVOKEFLG = "revokeFlg";
    public static final String INF_ISINNERTRD = "isInnerTrd";

    // 预占功能 add by zhangrui1 start
    public static final String INF_OCCUPYDATE = "occupyDate";
    // 预占功能 add by zhangrui1 end
    public static final String INF_TOTALFACEAMT = "totalFaceAmt";
    public static final String INF_VARIETYCODE = "varietyCode";
    public static final String INF_CALENDARCODE = "calendarCode";
    public static final String INF_INVAIM = "invAim";
    /*************************************************************/
    /**                      *△接口字段区域△*                        */
    /*************************************************************/

    /**
     * 画面字段与接口字段对应关系
     * 
     * @author zhangrui1
     *
     */
    enum FieldStruct {
        PORTFOLIOID("portfolioId", INF_PORTFOLIOCODE),
        FINPRODID("finprodId", INF_ASSETCODE),
        BUSICODE("busiCode", INF_EVENTID),
        FINPRODTYPE2("finprodType2", INF_ASSETTYPE),
        PS("ps", INF_PS),
        PS2("ps2", INF_PS),
        PORTFOLIOID_C("portfolioId", INF_COUNTERID),
        CCY("ccy", INF_CCY),
        TRADEDATE("tradeDate", INF_EVENTDATE),
        FINPRODMARKETID("finprodMarketId", INF_MARKETCODE),
        SHAREAMT("shareAmt", INF_AMOUNT),
        CONNUM("conNum", INF_TRADENUM),
        CONPRICE("conPrice", INF_PRICEAMT),
        FINMDATE("finmdate", INF_MDATE),
        CHANGETOFINPRODNAME("changeToFinprodId", INF_ASSETCODE),
        TRADEMARKET("tradeMarket", INF_TRADEPLACE),
        SHAREFINPRODID("shareFinprodId", INF_ASSETCODE),
        TRADEID("tradeId", INF_EVENTID),
        // 债券借贷 start
        F12_MTRADEAMT("mTradeAmt", INF_AMOUNT),
        F12_MTRADEAMT_NUM("mTradeAmt", INF_TRADENUM),
        F12_PRINAMT("prinAmt", INF_AMOUNT),
        F12_SHAREAMT("shareAmt", INF_TOTALFACEAMT),
        F12_PLEDGEFINPRODID("pledgeFinprodId", INF_ASSETCODE),
        F12_CALENDARID("calendarId", INF_CALENDARCODE),
        F12_INVAIM(INF_INVAIM, INF_INVAIM),
        // 债券借贷 end
        ;
        /**
         * 画面字段
         */
        String pageField;

        /**
         * 接口字段
         */
        String infField;

        private FieldStruct(String pageField, String infField) {
            this.pageField = pageField;
            this.infField = infField;
        }
    }

    /**
     * 各种业务画面的字段对应关系
     */
    private static final Map<String, List<FieldStruct>> ASSET_TRADE_MAP = new HashMap<>();
    static {
        // 债券借贷-到期
        add(concat(DictCodeListExt2.平安资产类型_债券借贷, DictCodeList.交易方向_到期),
            // 债券借贷到期确认.组合代码-接口.组合代码
            FieldStruct.PORTFOLIOID,
            // 债券借贷到期确认.转债代码-接口.资产代码
            FieldStruct.FINPRODID,
            FieldStruct.FINPRODTYPE2,
            // 债券借贷到期确认.业务编码-接口.事件代码
            FieldStruct.BUSICODE,
            // 债券借贷到期确认.交易方向-接口.交易方向
            FieldStruct.PS,
            // 债券借贷到期确认.币种-接口.币种
            FieldStruct.CCY,
            // 债券借贷到期确认.借贷费用-接口.金额
            FieldStruct.F12_MTRADEAMT,
            // 债券借贷到期确认.借贷费用-接口.数量
            FieldStruct.F12_MTRADEAMT_NUM,
            // 债券借贷到期确认.交易日历-接口.交易日历
            FieldStruct.F12_CALENDARID);
        // 债券借贷-融入-买入
        add(concat(DictCodeListExt2.平安资产类型_债券借贷, DictCodeList.交易方向_到期, BussinessDictCodeList.交易方向_买入),
            // 债券借贷到期确认.组合代码-接口.组合代码
            FieldStruct.PORTFOLIOID,
            // 债券借贷到期确认.转债代码-接口.资产代码
            FieldStruct.F12_PLEDGEFINPRODID,
            // 债券借贷到期确认.业务编码-接口.事件代码
            FieldStruct.BUSICODE,
            // 债券借贷到期确认.交易方向-接口.交易方向
            FieldStruct.PS2,
            // 债券借贷到期确认.币种-接口.币种
            FieldStruct.CCY,
            // 债券借贷到期确认.借贷费用-接口.金额
            FieldStruct.F12_PRINAMT,
            // 债券借贷到期确认.借贷费用-接口.数量
            FieldStruct.F12_SHAREAMT);
        // 债券借贷-融出-卖出
        add(concat(DictCodeListExt2.平安资产类型_债券借贷, DictCodeList.交易方向_到期, BussinessDictCodeList.交易方向_卖出),
            // 债券借贷到期确认.组合代码-接口.组合代码
            FieldStruct.PORTFOLIOID,
            // 债券借贷到期确认.转债代码-接口.资产代码
            FieldStruct.F12_PLEDGEFINPRODID,
            // 债券借贷到期确认.业务编码-接口.事件代码
            FieldStruct.BUSICODE,
            // 债券借贷到期确认.交易方向-接口.交易方向
            FieldStruct.PS2,
            // 债券借贷到期确认.币种-接口.币种
            FieldStruct.CCY,
            // 债券借贷到期确认.借贷费用-接口.金额
            FieldStruct.F12_PRINAMT,
            // 债券借贷到期确认.借贷费用-接口.数量
            FieldStruct.F12_SHAREAMT);
        // 债券-债转股
        add(concat(BussinessDictCodeList.平安资产类型_债券, DictCodeListExt1.交易方向_债转股), 
                // 债转股交易画面。组合代码-接口。组合代码
                FieldStruct.PORTFOLIOID,
                // 债转股交易画面。转债代码-接口。资产代码
                FieldStruct.FINPRODID,
                // 债转股交易画面。业务编码-接口。事件代码
                FieldStruct.BUSICODE,
                // 债转股交易画面。金融产品类型（投管）-接口。系统资产大类
                FieldStruct.FINPRODTYPE2,
                // 债转股交易画面。交易方向-接口。交易方向
                FieldStruct.PS,
                // 债转股交易画面。币种-接口。币种
                FieldStruct.CCY,
                // 债转股交易画面。组合代码-接口。交易对手
//                FieldStruct.PORTFOLIOID_C,
                // 债转股交易画面。交易日期-接口。事件日期
                FieldStruct.TRADEDATE,
                // 债转股交易画面。转股券面总额-接口。金额
                FieldStruct.SHAREAMT,
                // 债转股交易画面。产品到期日-接口。产品到期日
                FieldStruct.FINMDATE);
        // 股票-债转股
        add(concat(BussinessDictCodeList.平安资产类型_股票, DictCodeListExt1.交易方向_债转股), 
                // 债转股交易画面。组合代码-接口。组合代码
                FieldStruct.PORTFOLIOID,
                // 债转股交易画面。转债资产代码-接口。资产代码
                FieldStruct.SHAREFINPRODID,
                // 债转股交易画面。转债代码-接口。市场代码
                FieldStruct.FINPRODMARKETID,
                // 债转股交易画面。交易场所-接口。交易场所
                FieldStruct.TRADEMARKET,
                // 债转股交易画面。业务编码-接口。事件代码
                FieldStruct.BUSICODE,
                // 债转股交易画面。金融产品类型（投管）-接口。系统资产大类
                FieldStruct.FINPRODTYPE2,
                // 债转股交易画面。交易方向-接口。交易方向
                FieldStruct.PS,
                // 债转股交易画面。币种-接口。币种
                FieldStruct.CCY,
                // 债转股交易画面。组合代码-接口。交易对手
//                FieldStruct.PORTFOLIOID_C,
                // 债转股交易画面。转股数-接口。数量
                FieldStruct.CONNUM,
                // 债转股交易画面。转股价格-接口。价格
                FieldStruct.CONPRICE,
                // 债转股交易画面。交易日期-接口。事件日期
                FieldStruct.TRADEDATE,
                // 债转股交易画面。产品到期日-接口。产品到期日
                FieldStruct.FINMDATE);
        // 基金-转换申请-卖出
        add(concat(BussinessDictCodeList.平安资产类型_基金, DictCodeListExt2.交易方向_转换申请, BussinessDictCodeList.交易方向_买入),
                // 基金交易画面。组合代码-接口。组合代码
                FieldStruct.PORTFOLIOID,
                // 基金交易画面。转入资产代码-接口。资产代码
                FieldStruct.CHANGETOFINPRODNAME,
                // 债转股交易画面。业务编码-接口。事件代码
                FieldStruct.BUSICODE,
                // 基金交易画面。金融产品类型（投管）-接口。系统资产大类
                FieldStruct.FINPRODTYPE2,
                // 基金交易画面。交易方向-接口。交易方向
                FieldStruct.PS2,
                // 基金交易画面。币种-接口。币种
                FieldStruct.CCY,
                // 债转股交易画面。交易日期-接口。事件日期
                FieldStruct.TRADEDATE,
                // 基金交易画面。组合代码-接口。交易对手
//                FieldStruct.PORTFOLIOID_C,
                // 基金交易画面。转股数-接口。数量
                FieldStruct.SHAREAMT);
        // 基金-转换申请-买入
        add(concat(BussinessDictCodeList.平安资产类型_基金, DictCodeListExt2.交易方向_转换申请, BussinessDictCodeList.交易方向_卖出),
                // 基金交易画面。组合代码-接口。组合代码
                FieldStruct.PORTFOLIOID,
                // 基金交易画面。资产代码-接口。资产代码
                FieldStruct.FINPRODID,
                // 基金交易画面。业务编码-接口。事件代码
                FieldStruct.BUSICODE,
                // 基金交易画面。金融产品类型（投管）-接口。系统资产大类
                FieldStruct.FINPRODTYPE2,
                // 基金交易画面。交易方向-接口。交易方向
                FieldStruct.PS2,
                // 基金交易画面。币种-接口。币种
                FieldStruct.CCY,
                // 债转股交易画面。交易日期-接口。事件日期
                FieldStruct.TRADEDATE,
                // 基金交易画面。组合代码-接口。交易对手
//                FieldStruct.PORTFOLIOID_C,
                // 基金交易画面。转股数-接口。数量
                FieldStruct.SHAREAMT);
    }

    private static final void add(String key, FieldStruct...fields) {
        if (fields != null && fields.length > 0) {
            ASSET_TRADE_MAP.put(key, Arrays.asList(fields));
        }
    }

    private static final String concat(BaseDictCodeList... list) {
        if (list == null || list.length == 0) {
            return StringUtil.EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        for(BaseDictCodeList dict: list) {
            builder.append(dict.getValue());
        }
        return builder.toString();
    }

    /**
     * 将画面字段转化为风险接口的字段
     * 
     * @param pageMap 画面数据
     * @param infMap 接口字段
     */
    public static final void mapping(Map<String, Object> pageMap, Map<String, Object> infMap) {
        String assetType = (String) pageMap.get(InterfUtil.finprodType2);
        String ps = (String) pageMap.get(InterfUtil.ps);
        String ps2 = (String) pageMap.get(InterfUtil.ps + 2);
        String key = assetType + ps + (StringUtil.isNotEmpty(ps2) ? ps2 : "");
        List<FieldStruct> fieldList = ASSET_TRADE_MAP.get(key);
        fieldList.forEach(field -> formatObject(field, pageMap, infMap));
    }

    private static final void formatObject(FieldStruct field, Map<String, Object> pageMap, Map<String, Object> infMap) {
        Object obj = pageMap.get(field.pageField);
        if (obj instanceof Date) {
            Date dt = (Date)obj;
            infMap.put(field.infField, DateUtil.format(dt,DateUtil.DEFAULT_DATE_FORMAT_EN));
        }
        else if (obj != null) {
            infMap.put(field.infField, obj);
        }
    }
}
