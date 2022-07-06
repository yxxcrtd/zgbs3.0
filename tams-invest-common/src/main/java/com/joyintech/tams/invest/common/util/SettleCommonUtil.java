package com.joyintech.tams.invest.common.util;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;

import java.util.Map;

/**
 * @program: tams-invest-parent
 * @description: 清算共通工具类
 * @author: 强成西
 * @create: 2018-12-04 10:39
 **/
public class SettleCommonUtil {

    public static void setParams(Map<String, Object> params) {
        params.put("sysDate", DateUtil.format(DateUtil.platDateFromCache()));
        params.put("A01", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        params.put("ACCT_TYPE_A08", DictCodeListExt1.资金账户类型_甲类户.getValue());
        // 清算变更 start 2019年3月14日 --
        params.put("ACCT_TYPE_A09", DictCodeListExt1.资金账户类型_上清所DVP账户.getValue());
        params.put("ACCT_TYPE_A10", DictCodeListExt1.资金账户类型_中债登DVP账户.getValue());
        params.put("ROI_01", DictCodeList.登记托管机构_中央国债登记结算有限责任公司.getValue());
        params.put("ROI_02", DictCodeList.登记托管机构_上海清算所.getValue());
        // 清算变更 end 2019年3月14日 --
        params.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        params.put("ORDER_STATUS_01", DictCodeListExt1.指令状态_新建.getValue());
        params.put("NO", DictCodeList.是否_否.getValue());
        params.put("YES", DictCodeList.是否_是.getValue());
        params.put("CD_FLOW_01", DictCodeList.托管流水借贷方向_借.getValue());
        params.put("T01", DictCodeList.交易方向_买入.getValue());
        params.put("T02", DictCodeList.交易方向_卖出.getValue());
        params.put("T04", DictCodeList.交易方向_申购.getValue());

        params.put("T39", DictCodeList.交易方向_行权_赎回.getValue());
        params.put("T03", DictCodeList.交易方向_行权_回售.getValue());

        params.put("PS01", DictCodeListExt1.收支方向_收入.getValue());
        params.put("PS02", DictCodeListExt1.收支方向_支出.getValue());

        params.put("STM_01", DictCodeListExt1.交易市场_一级市场.getValue());

        params.put("TP01", DictCodeListExt1.交易场所_银行间.getValue());
        params.put("TP03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        params.put("TP04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());

			  params.put("SHARES_TRADE_PLATFORM_01", DictCodeList.交易平台_外汇交易中心.getValue());
        params.put("SHARES_TRADE_PLATFORM_99", DictCodeList.交易平台_其它.getValue());

        params.put(BussinessConstant.总集经办, BussinessConstant.总集经办);
        params.put(BussinessConstant.总集复核, BussinessConstant.总集复核);

        params.put("STATUS_VALID_05", DictCodeListExt1.画面数据状态_有效.getValue());
        params.put("STATUS_VALID_01", DictCodeListExt1.画面数据状态_待处理.getValue());
        params.put("STATUS_VALID_03", DictCodeListExt1.画面数据状态_流程中.getValue());
        params.put("MATCH_STATUS_02", DictCodeListExt3.匹配状态_未匹配.getValue());
        params.put("MATCH_STATUS_01", DictCodeListExt3.匹配状态_已匹配.getValue());
        params.put("CD_FLOW_FLAG_01", DictCodeList.托管流水借贷方向_借.getValue());
        params.put("CD_FLOW_FLAG_02", DictCodeList.托管流水借贷方向_贷.getValue());
        // 汇总
        params.put("PA_BUSINESS_TYPE01", DictCodeListExt1.平安业务类型_产品成立.getValue());
        params.put("PA_BUSINESS_TYPE02", DictCodeListExt1.平安业务类型_产品强增.getValue());
        params.put("PA_BUSINESS_TYPE03", DictCodeListExt1.平安业务类型_产品强减.getValue());
        params.put("PA_BUSINESS_TYPE04", DictCodeListExt1.平安业务类型_产品申购.getValue());
        params.put("PA_BUSINESS_TYPE08", DictCodeListExt1.平安业务类型_产品费用支付.getValue());
        params.put("PA_BUSINESS_TYPE118", DictCodeListExt1.平安业务类型_通道费用支付.getValue());
        params.put("PA_BUSINESS_TYPE05", DictCodeListExt1.平安业务类型_产品赎回.getValue());
        params.put("PA_BUSINESS_TYPE06", DictCodeListExt1.平安业务类型_产品中间付息.getValue());
        params.put("PA_BUSINESS_TYPE07", DictCodeListExt1.平安业务类型_产品到期.getValue());
        params.put("PA_BUSINESS_TYPE100", DictCodeListExt1.平安业务类型_产品现金分红.getValue());
        params.put("PA_BUSINESS_TYPE117", DictCodeListExt1.平安业务类型_产品红利转投.getValue());

        params.put("PA_BUSINESS_TYPE144", DictCodeListExt3.平安业务类型_已到期资产存续期.getValue());

        params.put("PA_BUSINESS_TYPE44", DictCodeListExt1.平安业务类型_普通拆出到期.getValue());
        params.put("PA_BUSINESS_TYPE45", DictCodeListExt1.平安业务类型_项目类资产中间付息.getValue());
        params.put("PA_BUSINESS_TYPE46", DictCodeListExt1.平安业务类型_项目类资产中间还本.getValue());
        params.put("PA_BUSINESS_TYPE47", DictCodeListExt1.平安业务类型_项目类资产到期.getValue());
        params.put("PA_BUSINESS_TYPE48", DictCodeListExt1.平安业务类型_计划类资产_利率型_中间付息.getValue());
        params.put("PA_BUSINESS_TYPE49", DictCodeListExt1.平安业务类型_计划类资产_利率型_中间还本.getValue());
        params.put("PA_BUSINESS_TYPE50", DictCodeListExt1.平安业务类型_计划类资产_利率型_到期.getValue());
        params.put("PA_BUSINESS_TYPE35", DictCodeListExt1.平安业务类型_债券中间收息.getValue());
        params.put("PA_BUSINESS_TYPE36", DictCodeListExt1.平安业务类型_债券中间还本.getValue());
        params.put("PA_BUSINESS_TYPE37", DictCodeListExt1.平安业务类型_债券到期.getValue());
        params.put("PA_BUSINESS_TYPE38", DictCodeListExt1.平安业务类型_存款中间还本.getValue());
        params.put("PA_BUSINESS_TYPE39", DictCodeListExt1.平安业务类型_存款中间付息.getValue());
        params.put("PA_BUSINESS_TYPE40", DictCodeListExt1.平安业务类型_存款到期.getValue());
        params.put("PA_BUSINESS_TYPE41", DictCodeListExt1.平安业务类型_存款提前支取.getValue());
        params.put("PA_BUSINESS_TYPE125", DictCodeListExt2.平安业务类型_保证金回款.getValue());

        params.put("PA_BUSINESS_TYPE42", DictCodeListExt1.平安业务类型_普通拆入到期.getValue());
        params.put("PA_BUSINESS_TYPE43", DictCodeListExt1.平安业务类型_特拆拆入到期.getValue());

        params.put("PA_BUSINESS_TYPE52", DictCodeListExt1.平安业务类型_正回购到期.getValue());
        params.put("PA_BUSINESS_TYPE51", DictCodeListExt1.平安业务类型_逆回购到期.getValue());
        params.put("PA_BUSINESS_TYPE76", DictCodeListExt1.平安业务类型_买断式正回购到期.getValue());
        params.put("PA_BUSINESS_TYPE77", DictCodeListExt1.平安业务类型_买断式逆回购到期.getValue());
        params.put("PA_BUSINESS_TYPE116",DictCodeListExt1.平安业务类型_增值税缴纳.getValue());

        params.put("PA_BUSINESS_TYPE137",DictCodeListExt3.平安业务类型_融入债券付息还本_付.getValue());
        params.put("PA_BUSINESS_TYPE138",DictCodeListExt3.平安业务类型_融出债券付息还本_收.getValue());

        // 交易
        params.put("PA_BUSINESS_TYPE14", DictCodeListExt1.平安业务类型_普通拆入.getValue());
        params.put("PA_BUSINESS_TYPE15", DictCodeListExt1.平安业务类型_特拆拆入.getValue());
        params.put("PA_BUSINESS_TYPE16", DictCodeListExt1.平安业务类型_普通拆出.getValue());
        params.put("PA_BUSINESS_TYPE19", DictCodeListExt1.平安业务类型_逆回购.getValue());
        params.put("PA_BUSINESS_TYPE20", DictCodeListExt1.平安业务类型_正回购.getValue());
        params.put("PA_BUSINESS_TYPE75", DictCodeListExt1.平安业务类型_买断式逆回购.getValue());
        params.put("PA_BUSINESS_TYPE74", DictCodeListExt1.平安业务类型_买断式正回购.getValue());
        params.put("PA_BUSINESS_TYPE61", DictCodeListExt1.平安业务类型_活期结息.getValue());
        params.put("PA_BUSINESS_TYPE62", DictCodeListExt1.平安业务类型_自定义现金流收取.getValue());
        params.put("PA_BUSINESS_TYPE63", DictCodeListExt1.平安业务类型_自定义现金流支付.getValue());
        params.put("PA_BUSINESS_TYPE64", DictCodeListExt1.平安业务类型_资金划拨证银转账.getValue());
        params.put("PA_BUSINESS_TYPE65", DictCodeListExt1.平安业务类型_资金划拨银证转账.getValue());
        params.put("PA_BUSINESS_TYPE81", DictCodeListExt1.平安业务类型_DVP划款.getValue());

        params.put("PA_BUSINESS_TYPE12", DictCodeListExt1.平安业务类型_存款类资产买入.getValue());
        params.put("PA_BUSINESS_TYPE13", DictCodeListExt1.平安业务类型_存款类资产卖出.getValue());

        params.put("PA_BUSINESS_TYPE17", DictCodeListExt1.平安业务类型_项目类资产买入.getValue());
        params.put("PA_BUSINESS_TYPE18", DictCodeListExt1.平安业务类型_项目类资产卖出.getValue());
        params.put("PA_BUSINESS_TYPE83", DictCodeListExt1.平安业务类型_项目类资产卖出到账.getValue());

        params.put("PA_BUSINESS_TYPE33", DictCodeListExt1.平安业务类型_计划类资产_利率型_申购.getValue());
        params.put("PA_BUSINESS_TYPE34", DictCodeListExt1.平安业务类型_计划类资产_利率型_赎回.getValue());

        params.put("PA_BUSINESS_TYPE29", DictCodeListExt1.平安业务类型_计划类资产_净值型_申购申请.getValue());
        params.put("PA_BUSINESS_TYPE58", DictCodeListExt1.平安业务类型_计划类资产_净值型_现金分红.getValue());
        params.put("PA_BUSINESS_TYPE68", DictCodeListExt1.平安业务类型_计划类资产_净值型_赎回到账.getValue());
        params.put("PA_BUSINESS_TYPE32", DictCodeListExt1.平安业务类型_计划类资产_净值型_赎回确认.getValue());
        params.put("PA_BUSINESS_TYPE60", DictCodeListExt1.平安业务类型_计划类资产_净值型_分红除权.getValue());

        params.put("PA_BUSINESS_TYPE21", DictCodeListExt1.平安业务类型_货币基金申购申请.getValue());
        params.put("PA_BUSINESS_TYPE22", DictCodeListExt1.平安业务类型_货币基金申购确认.getValue());
        params.put("PA_BUSINESS_TYPE54", DictCodeListExt1.平安业务类型_货币基金现金分红.getValue());
        params.put("PA_BUSINESS_TYPE69", DictCodeListExt1.平安业务类型_货币基金分红除权.getValue());
        params.put("PA_BUSINESS_TYPE66", DictCodeListExt1.平安业务类型_货币基金赎回到账.getValue());
        params.put("PA_BUSINESS_TYPE24", DictCodeListExt1.平安业务类型_货币基金赎回确认.getValue());


        params.put("PA_BUSINESS_TYPE25", DictCodeListExt1.平安业务类型_非货币基金申购申请.getValue());
        params.put("PA_BUSINESS_TYPE56", DictCodeListExt1.平安业务类型_非货币基金现金分红.getValue());
        params.put("PA_BUSINESS_TYPE57", DictCodeListExt1.平安业务类型_非货币基金分红除权.getValue());
        params.put("PA_BUSINESS_TYPE67", DictCodeListExt1.平安业务类型_非货币基金赎回到账.getValue());
        params.put("PA_BUSINESS_TYPE28", DictCodeListExt1.平安业务类型_非货币基金赎回确认.getValue());

        params.put("PA_BUSINESS_TYPE97", DictCodeListExt1.平安业务类型_计划类资产_货币型_申购申请.getValue());
        params.put("PA_BUSINESS_TYPE80", DictCodeListExt1.平安业务类型_计划类资产_货币型_现金分红.getValue());
        params.put("PA_BUSINESS_TYPE79", DictCodeListExt1.平安业务类型_计划类资产_货币型_除权登记.getValue());
        params.put("PA_BUSINESS_TYPE73", DictCodeListExt1.平安业务类型_计划类资产_货币型_赎回到账.getValue());
        params.put("PA_BUSINESS_TYPE72", DictCodeListExt1.平安业务类型_计划类资产_货币型_赎回确认.getValue());

        params.put("PA_BUSINESS_TYPE09", DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        params.put("PA_BUSINESS_TYPE10", DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
        params.put("PA_BUSINESS_TYPE11", DictCodeListExt1.平安业务类型_债券类资产行权.getValue());

        params.put("PA_BUSINESS_TYPE86", DictCodeListExt1.平安业务类型_通道划款.getValue());
        params.put("PA_BUSINESS_TYPE87", DictCodeListExt1.平安业务类型_通道回款.getValue());

        params.put("PA_BUSINESS_TYPE102", DictCodeListExt2.平安业务类型_外汇掉期起息.getValue());
        params.put("PA_BUSINESS_TYPE103", DictCodeListExt2.平安业务类型_外汇掉期到期.getValue());
        params.put("PA_BUSINESS_TYPE104", DictCodeListExt2.平安业务类型_外汇即期.getValue());
//        params.put("PA_BUSINESS_TYPE101", DictCodeListExt2.平安业务类型_外汇远期起息.getValue());
        params.put("PA_BUSINESS_TYPE101", DictCodeListExt2.平安业务类型_外汇远期到期.getValue());
        params.put("PA_BUSINESS_TYPE105", DictCodeListExt2.平安业务类型_债券借贷融入.getValue());
        params.put("PA_BUSINESS_TYPE107", DictCodeListExt2.平安业务类型_债券借贷融入到期.getValue());
        params.put("PA_BUSINESS_TYPE106", DictCodeListExt2.平安业务类型_债券借贷融出.getValue());
        params.put("PA_BUSINESS_TYPE108", DictCodeListExt2.平安业务类型_债券借贷融出到期.getValue());
//        params.put("PA_BUSINESS_TYPE109", DictCodeListExt2.平安业务类型_利率互换中间收息.getValue());
//        params.put("PA_BUSINESS_TYPE110", DictCodeListExt2.平安业务类型_利率互换提前终止.getValue());
//        params.put("PA_BUSINESS_TYPE111", DictCodeListExt2.平安业务类型_利率互换到期.getValue());
        params.put("PA_BUSINESS_TYPE112", DictCodeListExt2.平安业务类型_股票交易.getValue());
        params.put("PA_BUSINESS_TYPE113", DictCodeListExt2.平安业务类型_现金分红.getValue());
        params.put("PA_BUSINESS_TYPE114", DictCodeListExt2.平安业务类型_送转.getValue());
        params.put("PA_BUSINESS_TYPE115", DictCodeListExt2.平安业务类型_分红送转.getValue());
        params.put("PA_BUSINESS_TYPE145", DictCodeListExt3.平安业务类型_期权买入.getValue());
        params.put("PA_BUSINESS_TYPE146", DictCodeListExt3.平安业务类型_期权卖出.getValue());
        params.put("PA_BUSINESS_TYPE147", DictCodeListExt3.平安业务类型_期权放弃行权.getValue());
        params.put("PA_BUSINESS_TYPE148", DictCodeListExt3.平安业务类型_期权提前行权.getValue());
        params.put("PA_BUSINESS_TYPE149", DictCodeListExt3.平安业务类型_期权到期行权.getValue());
        params.put("PA_BUSINESS_TYPE151", DictCodeListExt4.平安业务类型_中标结果维护.getValue());
        params.put("PA_BUSINESS_TYPE202",DictCodeListExt4.平安业务类型_债券分销买入确认.getValue());
        params.put("PA_BUSINESS_TYPE180",DictCodeListExt4.平安业务类型_一级申购.getValue());

        params.put("F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        params.put("F29", DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
        params.put("F30", DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
        params.put("F14", DictCodeListExt1.金融产品类型_投管用_利率互换.getValue());
        params.put("F11", DictCodeListExt1.金融产品类型_投管用_即期.getValue());
        params.put("F12", DictCodeListExt1.金融产品类型_投管用_远期.getValue());
        params.put("F13", DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
        params.put("F35", DictCodeListExt1.金融产品类型_投管用_期权.getValue());
        params.put("F37", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());

        params.put("TRADE_FLOW_STATUS01", DictCodeListExt1.交易流程状态_流程中.getValue());
        params.put("TRADE_FLOW_STATUS02", DictCodeListExt1.交易流程状态_有效.getValue());

        params.put("AS_02", DictCodeListExt1.账户来源_自营.getValue());
        params.put("AS_01", DictCodeListExt1.账户来源_理财.getValue());

        params.put("PAY_TYPE01", DictCodeListExt2.划款方式_逐笔划款.getValue());

        params.put("PROFIT_TYPE_01", DictCodeList.收益类型_净值型.getValue());
        params.put("PROFIT_TYPE_02", DictCodeList.收益类型_收益型.getValue());

        params.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        params.put("TRADE_STATUS_03", DictCodeListExt1.交易状态_撤销.getValue());
        params.put("revoke", "revoke");
        params.put("new", "new");

        // 指令处理状态_处理失败
        params.put("COMMAND_DEAL_STATUS_UNDO", DictCodeList.指令处理状态_处理失败.getValue());
        params.put("PAY_TYPE0104", DictCodeListExt3.平安业务类型_货币型产品轧差清算.getValue());

        //增加平安业务类型_计划类资产_净值型_卖出 add by yujinfa 20201021 start
        params.put("PA_BUSINESS_TYPE200", DictCodeListExt1.平安业务类型_计划类资产_净值型_卖出.getValue());
        //增加平安业务类型_计划类资产_净值型_卖出 add by yujinfa 20201021 end
    }
}
