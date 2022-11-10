/*
 * 文件名：BussSettleOrderCommService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年09月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.settle.order.service;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.settle.order.dao.BussSettleOrderCommMapper;
import com.joyintech.service.settle.order.dto.BussSettleOrderConditionDto;
import com.joyintech.service.settle.order.dto.BussSettleOrderInputDto;
import com.joyintech.service.settle.order.dto.BussSettleOrderResultDto;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 清算指令共通
 */
@Service("backBussSettleOrderCommService")
public class BussSettleOrderCommService {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * baseGetBussinessId
     */
    @Autowired(required = false)
    private BaseGetBussinessId baseGetBussinessId;
    /**
     * bussSettleOrderCommMapper
     */
    @Autowired
    private BussSettleOrderCommMapper bussSettleOrderCommMapper;

    /**
     * @param dto 分支选择条件
     * @return 分支结果，与getResult拼接后反射调用该方法
     */
    private String getBranch(BussSettleOrderConditionDto dto){
        if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_理财产品.getValue())
                ||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_固定管理费.getValue())
                ||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_托管费.getValue())
                ||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_销售费.getValue())
            //上面的费用类型针对费用支付对应不上 合并上农商的代码
                || StringUtil.equals(dto.getFinprodType2(), DictCodeList.费用品种类型_固定管理费.getValue())
                    ||StringUtil.equals(dto.getFinprodType2(), DictCodeList.费用品种类型_托管费.getValue())
                    ||StringUtil.equals(dto.getFinprodType2(), DictCodeList.费用品种类型_销售手续费.getValue())
                    ||StringUtil.equals(dto.getFinprodType2(), DictCodeList.业务类型_增值税交易.getValue())
                    ||StringUtil.equals(dto.getPs(), DictCodeList.交易方向_产品付费.getValue())
                    ){
            DictCodeList psDict = DictCodeList.from(DictCode.交易方向, dto.getPs());
            switch (psDict){
                case 交易方向_产品认购:
                case 交易方向_产品申购:
                case 交易方向_份额强增:
                    return "B01";
                case 交易方向_赎回:
                    // 一体化对应etliu start
                case 交易方向_产品赎回:
                case 交易方向_产品提前赎回:
                case 交易方向_产品违约赎回:
                case 交易方向_赎回收益确认:
                 // 一体化对应etliu end
                case 交易方向_中间付息:
                case 交易方向_产品现金分红:
                case 交易方向_产品红利转投:
                    return "B02";
                case 交易方向_到期:
                case 交易方向_产品到期:
                case 交易方向_产品到期分红:
                case 交易方向_提前终止:
                case 交易方向_份额强减:
                    return "B03";
                case 交易方向_产品付费:
                case 交易方向_税费缴税: 
                case 交易方向_税费退税: 
                    return "B21";
                default:
                    return null;
            }
        } else if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_资金账户.getValue())) {
                if(StringUtil.equals(dto.getBusiType(), DictCodeList.共通业务类型_活期结息.getValue())
                    && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收息.getValue())) {
                    return "B28"; 
                }
                return null;
        }
        else{
            // update by caiqun 20200716  组合保本做交易的时候，指令是从虚拟户走，是平安的个性化，主产品将代码注释  satrt
            /*if(StringUtil.equals(dto.getIsGuarantee(), DictCodeList.是否_是.getValue())){
                if(
                    (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        && (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()) ||
                            StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_机构间私募系统.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆出.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆入.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_自定义现金流.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_支付.getValue()))
                ){
                    return "B04";
                }
                else if(
                    (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                        	// 一体化对应etliu start
//                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权.getValue())
                        	|| StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                        	|| StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())
                        	// 一体化对应etliu end
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_还本.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收息.getValue()))
                        && (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()) ||
                            StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_机构间私募系统.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                            && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())||StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_投资端_逾期返售.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_还本.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收息.getValue())
                            *//*增加存款的提前支取状态*//*
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_提前支取.getValue())
                            *//*增加活期存放结息*//*
                            ||StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收息到账.getValue())))
                            || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆出.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆入.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_自定义现金流.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收取.getValue()))
                ){
                    return "B05";
                }
            }
            else{*/
            // update by caiqun 20200716  组合保本做交易的时候，指令是从虚拟户走，是平安的个性化，主产品将代码注释  end
            	// 内转交易（通过交易对手的交易对手类别是内部交易对手来判断）
            	if (StringUtil.isNotEmpty(dto.getCounterId())) {
            		String counterType = bussSettleOrderCommMapper.findCounterType(dto.getCounterId());
            		if (StringUtil.equals(DictCodeListExt1.交易对手类型_内部交易对手.getValue(), counterType)) {
            			if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()) ||
            				StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
            				return "B22";
            			}
            			if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()) ||
            					// 一体化对应etliu start
//            				StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权.getValue()) ||
            					StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue()) ||
            					StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue()) ||
            				// 一体化对应etliu end
            				StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue()) ||
            				StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue()) ||
            				StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回到账.getValue())) {
            				return "B23";
            			}
            		}
            	}

            	// 非内转交易
                if(
                    (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        && (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()) ||
                            StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_机构间私募系统.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        && StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                            && StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                            && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())||StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_投资端_逾期返售.getValue()))
                        && StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                            && StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                        //----add by jjpei 20190705---债券借贷-----
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue()) &&StringUtil.equals(dto.getBankLendingType(), DictCodeListExt1.结算方式_债券借贷_劵款对付.getValue()))
                ){
                    return "B06";
                }
                else if(
                    (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                            && !(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()) ||
                            StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_机构间私募系统.getValue()))
                            && !StringUtil.equals(dto.getTradeMarketMode(), DictCodeListExt1.交易市场_一级市场.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                            && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                            && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())||StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_投资端_逾期返售.getValue()))
                        && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                            && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                    // add by renping 利率互换划款指令用行内统一账户 20190826 start
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_利率互换.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_支付.getValue()))
                    // add by renping 利率互换划款指令用行内统一账户 20190826 end
                    // add by renping 场内基金新增 20191117 start
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())
                            && ((StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                                    && (StringUtil.equals(dto.getCounterId(), BussinessConstant.COUNTER_PARTY_ID_SHANGHAI)
                                    || StringUtil.equals(dto.getCounterId(), BussinessConstant.COUNTER_PARTY_ID_SHENZHEN)))
                                || ((StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                                        || StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_认购.getValue()))
                                    && (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                                        || StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue()))
                                    && StringUtil.isNotEmpty(dto.getSecManageAcctId()))))
                    // add by renping 场内基金新增 20191117 end
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())
                            && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                                ||StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_配股.getValue())
                            	||StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_增发.getValue())
                            	||StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_新股中签.getValue()))
                            && (StringUtil.equals(dto.getDealMode(), DictCodeListExt2.外汇交易模式_大宗交易.getValue())
                            	||StringUtil.equals(dto.getDealMode(), DictCodeListExt2.外汇交易模式_盘后定价交易.getValue())
                            	||StringUtil.equals(dto.getDealMode(), DictCodeListExt2.外汇交易模式_竞价交易.getValue())
                                	))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                            && StringUtil.equals(dto.getTradeMarket(), DictCodeListExt2.申购方式_网上申购.getValue())
                            && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()))
                            && (StringUtil.equals(dto.getDealMode(), DictCodeListExt2.申购类型_优先配售.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                            && StringUtil.equals(dto.getTradeMarket(), DictCodeListExt2.申购方式_网上申购.getValue())
                            && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue()))
                            && (StringUtil.equals(dto.getDealMode(), DictCodeListExt2.申购类型_普通申购.getValue())))
                            || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期续做.getValue())
                            && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())
                            && (StringUtil.equals(dto.getIeFlag(), DictCodeListExt1.收支方向_支出.getValue())))
                            || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期续做.getValue())
                            && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())
                            && (StringUtil.equals(dto.getIeFlag(), DictCodeListExt1.收支方向_支出.getValue())))
        		){
                    return "B07";
                }
                else if(
                (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                 && (StringUtil.equals(dto.getBusiType(), DictCodeListExt3.平安业务类型_融入债券付息还本_付.getValue())
                     || StringUtil.equals(dto.getBusiType(), DictCodeListExt3.平安业务类型_融出债券付息还本_收.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                            && !(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()) ||
                            StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_机构间私募系统.getValue()))
                            && StringUtil.equals(dto.getTradeMarketMode(), DictCodeListExt1.交易市场_一级市场.getValue()))
                            //----add by jjpei 20190727---债券借贷---券券对付----------
                        ||(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue())
                             &&StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                            &&!StringUtil.equals(dto.getBankLendingType(), DictCodeListExt1.结算方式_债券借贷_劵款对付.getValue()))
                        // 金融产品类型_投管用_外汇掉期 add by renping
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_掉期.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_支付.getValue())))
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_远期.getValue())
                                && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                                || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_支付.getValue())))
                        // 金融产品类型_投管用_外汇即期 add by wangguoxian 20190715 start
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_即期.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_支付.getValue())))
                        // 金融产品类型_投管用_外汇即期 add by wangguoxian 20190715 end
                ){
                    return "B08";
                }
                else if(
                        (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                                && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                                && (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()) ||
                            StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_机构间私募系统.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())||StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_投资端_逾期返售.getValue()))
                        && StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        && StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                            && StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                            && StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                        // 金融产品类型_投管用_外汇掉期 add by renping
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_掉期.getValue())
                            && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                                || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收取.getValue())))
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_远期.getValue())
                                && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                                || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收取.getValue())))
                        // 金融产品类型_投管用_外汇即期 add by wangguoxian 20190715 start
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_即期.getValue())
                            && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                                || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收取.getValue())))
                        // 金融产品类型_投管用_外汇即期 add by wangguoxian 20190715 start
                ){
                    return "B09";
                }
                else if(
                    (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                                || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue()))
                        && (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()) ||
                            StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_机构间私募系统.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_还本.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收息.getValue()))
                ){
                    if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()) ||
                            StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_机构间私募系统.getValue()) ){
                        // 判断证券账户是否为甲类户
                        if (StringUtil.isNotEmpty(dto.getSecManageAcctId())) {
                            Map<String, Object> param = new HashMap<String, Object>();
                            param.put("secAcctId", dto.getSecManageAcctId());
                            String secAcctSource = bussSettleOrderCommMapper.selectSecAcctSource(param);
                            if (!StringUtil.equals(secAcctSource, DictCodeListExt1.账户来源_自营.getValue())) {
                                return "B10";
                            }
                            else{
                                return "B11";
                            }
                        }
                        else{
                            return "B11";
                        }
                    }
                    else{
                        return "B12";
                    }
                }
                //----add by jjpei 20190705---债券借贷-----start------------
                else if (
                     StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue())
                    && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                             &&StringUtil.equals(dto.getBankLendingType(), DictCodeListExt1.结算方式_债券借贷_劵款对付.getValue())){
                    return "B11";
                }
                //----add by jjpei 20190705---债券借贷-----end------------
                else if(
                        (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                                && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                                && !(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()) ||
                            StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_机构间私募系统.getValue())))
                    // add by renping 场内基金新增 20191117 start
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())
                            && ((StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                                    && (StringUtil.equals(dto.getCounterId(), BussinessConstant.COUNTER_PARTY_ID_SHANGHAI)
                                        || StringUtil.equals(dto.getCounterId(), BussinessConstant.COUNTER_PARTY_ID_SHENZHEN)))
                                || ((StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回到账.getValue()))
                                    && (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                                        || StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue()))
                                    && StringUtil.isNotEmpty(dto.getSecManageAcctId()))))
                    // add by renping 场内基金新增 20191117 end
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                                && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue()))
                                && !(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()) ||
                            StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_机构间私募系统.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue()) || StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_投资端_逾期返售.getValue()) )
                        && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                            && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                            && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()))
                    ||(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_股票.getValue())
                        &&StringUtil.equals(dto.getPs(),DictCodeList.交易方向_现金分红.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())
                            && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))
                                && (StringUtil.equals(dto.getDealMode(), DictCodeListExt2.外汇交易模式_大宗交易.getValue())
                                	||StringUtil.equals(dto.getDealMode(), DictCodeListExt2.外汇交易模式_盘后定价交易.getValue())
                                	||StringUtil.equals(dto.getDealMode(), DictCodeListExt2.外汇交易模式_竞价交易.getValue())
                                    	))
                        ||(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())&&
                                StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_债转股.getValue())
                                )
                                || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                                && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期续做.getValue())
                                && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())
                        && (StringUtil.equals(dto.getIeFlag(), DictCodeListExt1.收支方向_收入.getValue())))
                                || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                                && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期续做.getValue())
                                && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())
                                && (StringUtil.equals(dto.getIeFlag(), DictCodeListExt1.收支方向_收入.getValue())))
                ){
                    return "B13";
                }
                else if((StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()) ||
                        		// 一体化对应etliu start
//                        	StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权.getValue()))
                        		StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                        		|| StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue()))
                     // 一体化对应etliu end
                        && !(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue()) ||
                            StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_机构间私募系统.getValue())))
                		|| (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())
                                && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))
                                    && (StringUtil.equals(dto.getDealMode(), DictCodeListExt2.外汇交易模式_大宗交易专场.getValue())
                                        	))
                        //----add by jjpei 20190727---债券借贷---券券对付----------
                        // add by renping 利率互换划款指令用行内统一账户 20190826 start
                		|| (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_利率互换.getValue())
                		    && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收取.getValue()))
                        // add by renping 利率互换划款指令用行内统一账户 20190826 end
                        ||(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue())
                        &&StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                        &&!StringUtil.equals(dto.getBankLendingType(), DictCodeListExt1.结算方式_债券借贷_劵款对付.getValue()))
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                                && StringUtil.equals(dto.getTradeMarket(), DictCodeListExt2.申购方式_线下申购.getValue())
                                && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue())||StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_退款到账.getValue()))
                                && (StringUtil.equals(dto.getDealMode(), DictCodeListExt2.申购类型_普通申购.getValue()))
                                && (StringUtil.equals(dto.getIeFlag(), DictCodeListExt1.收支方向_收入.getValue())))
                        
                		){
                    return "B14";
                }
                else if((StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())
                    	&& StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
            		|| (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())
                            && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                                ||StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_配股.getValue())
                            	||StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_增发.getValue())
                            	||StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_新股中签.getValue()))
                            && (StringUtil.equals(dto.getDealMode(), DictCodeListExt2.外汇交易模式_大宗交易专场.getValue())
                                	))){
                    return "B15";
                }
                else if(
                    (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_还本.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收息.getValue())
                            /*增加存款提前支取状态*/
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_提前支取.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收息到账.getValue())))
                            || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆入.getValue())
                        && StringUtil.equals(dto.getBankLendingType(), DictCodeList.拆借类型_普通拆借.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆出.getValue())
                        && StringUtil.equals(dto.getBankLendingType(), DictCodeList.拆借类型_普通拆借.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出到账.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_还本.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收息.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                        && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_收益型.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())
                         // 一体化对应etliu start
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                         // 一体化对应etliu end
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_还本.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收息.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_自定义现金流.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收取.getValue()))
                            //期权交易，卖出 by zuoyinyi
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt3.金融产品类型_投管用_期权.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))
                    || ( StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_期权.getValue())
                            && DictCodeList.收付方向_收入.getValue().equals(dto.getIeFlag())
                            && (StringUtil.equals(dto.getPs(), DictCodeListExt1.交易方向_提前行权.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeListExt1.交易方向_到期行权.getValue())
                    ))

                ){
                    return "B16";
                }
                else if(
                    (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆出.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        && StringUtil.equals(dto.getBankLendingType(), DictCodeList.拆借类型_普通拆借.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆入.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                        && StringUtil.equals(dto.getBankLendingType(), DictCodeList.拆借类型_普通拆借.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_自定义现金流.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_支付.getValue()))
                    // modify by renping 场内基金新增 20191117 start
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())
                        && (((StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_认购.getValue()))
                                && ((!StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                                    && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue()))
                                    || StringUtil.isEmpty(dto.getSecManageAcctId())))
                            || (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                                && (!StringUtil.equals(dto.getCounterId(), BussinessConstant.COUNTER_PARTY_ID_SHANGHAI)
                                    && !StringUtil.equals(dto.getCounterId(), BussinessConstant.COUNTER_PARTY_ID_SHENZHEN)))))
                    // modify by renping 场内基金新增 20191117 end
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                        && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_净值型.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                                || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue())
                        		|| StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                    && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_货币型.getValue())
                    && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                    		// modify by wj 缺少计划类资产 货币型的 申购确认数据。
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                            && StringUtil.equals(dto.getTradeMarket(), DictCodeListExt2.申购方式_线下申购.getValue())
                            && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()))
                            && (StringUtil.equals(dto.getDealMode(), DictCodeListExt2.申购类型_优先配售.getValue())
                            		||StringUtil.equals(dto.getDealMode(), DictCodeListExt2.申购类型_普通申购.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                            && StringUtil.equals(dto.getTradeMarket(), DictCodeListExt2.申购方式_线下申购.getValue())
                            && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue()))
                            && (StringUtil.equals(dto.getDealMode(), DictCodeListExt2.申购类型_普通申购.getValue()))
                            && (StringUtil.equals(dto.getIeFlag(), DictCodeListExt1.收支方向_支出.getValue())))
                            //期权交易 买入 by zuoyinyi
                    ||(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt3.金融产品类型_投管用_期权.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
                    || ( StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_期权.getValue())
                            && DictCodeListExt1.收付方向_支出.getValue().equals(dto.getIeFlag())
                            && (StringUtil.equals(dto.getPs(), DictCodeListExt1.交易方向_提前行权.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeListExt1.交易方向_到期行权.getValue())
                    ))
                    // add by etliu 20210421 权益类资产相关代码合并 start
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue())
                    && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())))
                    // add by etliu 20210421 权益类资产相关代码合并 end
                ) {
                    return "B17";
                }
                else if(
                    (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆入.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        && !StringUtil.equals(dto.getBankLendingType(), DictCodeList.拆借类型_普通拆借.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())
                        /*&& !StringUtil.equals(dto.getBankLendingType(), DictCodeList.拆借类型_普通拆借.getValue())*/)
                ){
                    return "B18";
                }
                else if(
                    (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                            || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
                        && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_收益型.getValue()))
                ){
                    return "B19";
                }
                else if ((StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())
//                            修改场景为除权登记 非产品除权登记
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_除权登记.getValue()))
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())
//                            添加基金现金分红
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_现金分红.getValue()))
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())
//                            添加基金现金分红
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_红利再投.getValue()))) {
                    return "B27";
                }
                else if(
                    // modify by renping 场内基金新增 20191117 start
                    (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                        && ((!StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                            && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue()))
                            || StringUtil.isEmpty(dto.getSecManageAcctId())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回到账.getValue())
                        && ((!StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                            && !StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue()))
                            || StringUtil.isEmpty(dto.getSecManageAcctId())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                            && (!StringUtil.equals(dto.getCounterId(), BussinessConstant.COUNTER_PARTY_ID_SHANGHAI)
                                && !StringUtil.equals(dto.getCounterId(), BussinessConstant.COUNTER_PARTY_ID_SHENZHEN))))
                    // modify by renping 场内基金新增 20191117 end
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                        && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_收益型.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_除权登记.getValue())
//                            修改场景为除权登记 非产品除权登记
                        && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_收益型.getValue()))
                    ||  (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_现金分红.getValue())
                        && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_净值型.getValue())
                    ||  (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_除权登记.getValue())
                            && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_净值型.getValue()))
                    ||  (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_红利再投.getValue())
                            && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_净值型.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                        && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_净值型.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回到账.getValue())
                        && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_净值型.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                        && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_货币型.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回到账.getValue())
                        && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_货币型.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_现金分红.getValue())
                            && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_货币型.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_除权登记.getValue())
                            && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_货币型.getValue()))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
//                          添加基金现金分红
                          && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_红利再投.getValue())
                          && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_货币型.getValue())))
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                        && StringUtil.equals(dto.getProfitType(), DictCodeList.收益类型_净值型.getValue()))
                    // add by etliu 20210421 权益类资产相关代码合并 start
                    || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回到账.getValue()))
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue()))
                        || (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))
                        ||  (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_现金分红.getValue()) )
                        ||  (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue())
                        && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_除权登记.getValue()))
                    // add by etliu 20210421 权益类资产相关代码合并 end
                ){
                    return "B20";
                }else if(StringUtil.equals(dto.getFinprodType2(), DictCodeList.共通业务类型_资金划拨管理.getValue())){
                    return "B24";
                }
                else if (StringUtil.equals(dto.getFinprodType2(), DictCodeList.共通业务类型_DVP资金调拨管理.getValue())){
                    return "B25";
                }
                else if (StringUtil.equals(dto.getFinprodType2(), DictCodeList.共通业务类型_通道资金划拨管理.getValue())){
                    return "B26";
                }
            }
            //}
        return null;
    }

    /**
     * 生成清算指令
     * @param deal 金融交易
     * @param isTotalTrade 是否汇总交易
     * @param bankLendingType 拆借类型
     * @param portfolioId 组合代码
     * @param cacheMap 缓存map
     * @return 清算指令
     */
    public BussSettleOrderResultDto generateSettleOrder(TrdProductDeal deal, String isTotalTrade, String bankLendingType, String portfolioId
    		, String dealMode,String ieFlag, Map<String, String> cacheMap) {
        final String orderId;
        if(baseGetBussinessId != null){
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setSeqNo("9998");
            orderId = baseGetBussinessId.buildBussinessCode(bussinessCodeParameter);
        }
        else {
            orderId = IDGenerator.generate();
        }

        // 如果交易对手是内部交易对手，本方和对手方账户取组合的托管账户
        if (StringUtil.isNotEmpty(deal.getCounterId())) {
            Boolean 是否是内部交易对手 = bussSettleOrderCommMapper.isNaibuCounter(deal.getCounterId());
            if (是否是内部交易对手 != null && 是否是内部交易对手) {
                return do内部处理(orderId, deal, isTotalTrade, portfolioId);
            }
        }

        // 通道交易 新增 强成西 2019/03/26 start
        // 通道费用支付特殊处理，逐笔汇款：托管户 <-> 对手方账户，一次性划款：通道户 <-> 对手方账户
        String payType = null;
        if (StringUtil.isNotEmpty(deal.getChlId()) && !"999999".equals(deal.getChlId())) {
            payType = bussSettleOrderCommMapper.getPayType(deal.getChlId());
            if (DictCodeList.交易方向_付费.getValue().equals(deal.getPs())) {
                return do通道处理(orderId, deal, isTotalTrade, portfolioId, payType);
            }
        }
        // 通道交易 新增 强成西 2019/03/26 end

        BussSettleOrderConditionDto conditionDto = new BussSettleOrderConditionDto();
        BeanUtil.copyPropertiesIgnoreNull(conditionDto, deal);
        
        if(StringUtil.isNotEmpty(deal.getFinprodId())){
            String profitType = bussSettleOrderCommMapper.selectProfitType(deal.getFinprodId());
            conditionDto.setProfitType(profitType);
        }
        // 债券交易场所设置
        if (StringUtil.isNotEmpty(deal.getFinprodId())) {
	        Map<String, Object> param = new HashMap<String, Object>();
	        param.put("finprodId", deal.getFinprodId());
	        param.put("FINPROD_TYPE2_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
	        String tradeMarket = bussSettleOrderCommMapper.selectTradeMarket(param);
	        if (StringUtil.isNotEmpty(tradeMarket)&&(!StringUtil.equals(DictCodeList.交易方向_申购.getValue(), deal.getPs())
	        											&&!StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), deal.getPs())
	        											&&!StringUtil.equals(DictCodeListExt3.交易方向_退款到账.getValue(), deal.getPs()))) {
	        	conditionDto.setTradeMarket(tradeMarket);
	        }
        }
        conditionDto.setBankLendingType(bankLendingType);
        conditionDto.setIsGuarantee(bussSettleOrderCommMapper.selectIsGuarantee(portfolioId));
        conditionDto.setDealMode(dealMode);//交易方式
        conditionDto.setIeFlag(ieFlag);//收支方向
        String branch = getBranch(conditionDto);

        // 特殊处理：若获取不到对应分支，且是保本组合，则当做非保本组合重新处理
        // 例如：保本组合，非银行间债券，按照非保本组合情况处理
        if (StringUtil.isBlank(branch) && StringUtil.equals(conditionDto.getIsGuarantee(),
            DictCodeList.是否_是.getValue())) {
            conditionDto.setIsGuarantee(DictCodeList.是否_否.getValue());
            branch = getBranch(conditionDto);
        }

        if (branch == null) {
            logger.error("生成清算指令失败，没有相关分支，请检查交易：" + conditionDto);
            return null;
        }
        try {
            logger.debug("清算路径分支号" + branch);
            Method method = ReflectUtils.findDeclaredMethod(this.getClass(), "getResult" + branch, new Class[]{BussSettleOrderInputDto.class});
            BussSettleOrderInputDto inputDto = new BussSettleOrderInputDto();
            inputDto.setDeal(deal);
            inputDto.setIsTotalTrade(isTotalTrade);
            inputDto.setOrderId(orderId);
            inputDto.setProfitType(conditionDto.getProfitType());
            inputDto.setPortfolioId(portfolioId);
            inputDto.setCacheMap(cacheMap);
            inputDto.setFinprodType2(conditionDto.getFinprodType2());

            // 通道交易 新增 强成西 2019/03/26 start
            BussSettleOrderResultDto result = (BussSettleOrderResultDto) method.invoke(this, inputDto);
            // 通道存续期划款指令特殊处理
            if (StringUtil.isNotEmpty(deal.getChlId()) && !"999999".equals(deal.getChlId())) {
                do通道户特殊处理(result, payType, deal);
            }

            if (result != null) {
                if (StringUtil.equals(conditionDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                    || StringUtil.equals(conditionDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                    || StringUtil.equals(conditionDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                    if (StringUtil.isNotBlank(conditionDto.getSecManageAcctId())) {
                        // 判断证券账户是否为甲类户
                        Map<String, Object> param = new HashMap<String, Object>();
                        param.put("secAcctId", conditionDto.getSecManageAcctId());
                        String secAcctSource = bussSettleOrderCommMapper.selectSecAcctSource(param);
                        if (StringUtil.equals(secAcctSource, DictCodeListExt1.账户来源_自营.getValue())
                                || DictCodeListExt1.交易市场_一级市场.getValue().equals(deal.getTradeMarketMode())) {
                            result.setIsNeedSend(DictCodeList.是否_是.getValue());
                            if (DictCodeListExt1.交易市场_一级市场.getValue().equals(deal.getTradeMarket())
                                    && DictCodeList.交易平台_外汇交易中心.getValue().equals(deal.getTradePlat())) {
                                result.setIsNeedSend(DictCodeList.是否_否.getValue());
                            }
                        }
                    }
                }
            }
            // add by wangguoxian 外汇即远掉期集中净额清算模式集中账户特殊处理 20190805 start
            if ((StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_即期.getValue(),deal.getFinprodType2())
                    || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_远期.getValue(),deal.getFinprodType2())
                    || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_掉期.getValue(),deal.getFinprodType2()))
                    && StringUtil.equals(DictCodeListExt2.结算方式_集中净额清算.getValue(),deal.getDeliveryType())){
                do外汇交易特殊处理(result,deal,cacheMap);
            }
            // add by wangguoxian 外汇即远掉期集中净额清算模式集中账户特殊处理 20190805 end

            return result;
            // 通道交易 新增 强成西 2019/03/26 end
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("生成清算指令失败", e);
            return null;
        } catch (Exception e) {
            logger.error("生成清算指令失败", e);
            return null;
        }
    }
    // add by wangguoxian 外汇即远掉期集中净额清算模式集中账户特殊处理 20190805 start
    private void do外汇交易特殊处理(BussSettleOrderResultDto dto,TrdProductDeal deal, Map<String, String> cacheMap){
        if (StringUtil.equals(DictCodeListExt1.收支方向_收入.getValue(),dto.getPs())){
            dto.setMyCapacctInfoList(getSysParamCapacctInfoList(BussinessConstant.CENTRAL_NET_LIQUIDATION_ACCOUNT_NO_KEY,BussinessConstant.CENTRAL_NET_LIQUIDATION_ACCOUNT_NAME_KEY,cacheMap));
            dto.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
        }
        else if (StringUtil.equals(DictCodeListExt1.收支方向_支出.getValue(),dto.getPs())){
            dto.setCounterCapacctInfoList(getSysParamCapacctInfoList(BussinessConstant.CENTRAL_NET_LIQUIDATION_ACCOUNT_NO_KEY,BussinessConstant.CENTRAL_NET_LIQUIDATION_ACCOUNT_NAME_KEY,cacheMap));
        }
    }
    // add by wangguoxian 外汇即远掉期集中净额清算模式集中账户特殊处理 20190805 end

    private void do通道户特殊处理(BussSettleOrderResultDto dto, String payType, TrdProductDeal deal) {
        if (DictCodeListExt2.划款方式_一次性划款.getValue().equals(payType)) {
            dto.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
            // MyCapacct 是付款账户
            List<StlCapacctInfo> myAccts = bussSettleOrderCommMapper.selectChlCapacctInfoList(deal.getChlId());
            if (DictCodeListExt1.收支方向_支出.getValue().equals(dto.getPs())) {
                dto.setMyCapacctInfoList(myAccts);
                if (StringUtil.isNotEmpty(deal.getCounterId()) && (dto.getCounterCapacctInfoList() == null || dto.getCounterCapacctInfoList().isEmpty())) {
                    List<StlCapacctInfo> counterAccts = bussSettleOrderCommMapper.selectCounterCapacctInfoList(deal.getCounterId(),
                            DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy());
                    dto.setCounterCapacctInfoList(counterAccts);
                }
            }
            else {
                dto.setCounterCapacctInfoList(myAccts);
            }
            dto.setIsNeedSend(DictCodeList.是否_否.getValue());
        }
        else if (DictCodeListExt2.划款方式_逐笔划款.getValue().equals(payType)) {
            if (DictCodeListExt1.收支方向_支出.getValue().equals(dto.getPs())) {
                dto.setIsNeedSend(DictCodeList.是否_是.getValue());
                dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectChlCapacctInfoList(deal.getChlId()));
            } else {
                dto.setIsNeedSend(DictCodeList.是否_否.getValue());
                dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectChlCapacctInfoList(deal.getChlId()));
            }
        }
    }

    private BussSettleOrderResultDto do内部处理(String orderId, TrdProductDeal deal, String isTotalTrade, String portfolioId) {
        BussSettleOrderResultDto dto = new BussSettleOrderResultDto();
        dto.setOrderId(orderId);
        dto.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
        dto.setSettleDate(deal.getSettleDate() == null ? deal.getTradeDate() : deal.getSettleDate());
        // 内部交易时，只有买入卖出
        if (StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆入.getValue())
            || StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
            || StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
            dto.setPs(DictCodeList.交易方向_买入.getValue().equals(
                deal.getPs()) ? DictCodeListExt1.收支方向_收入.getValue() : DictCodeListExt1.收支方向_支出.getValue());
        }
        else {
            dto.setPs((DictCodeList.交易方向_买入.getValue().equals(
                    deal.getPs()) || DictCodeList.交易方向_支付.getValue().equals(
                    deal.getPs()) )? DictCodeListExt1.收支方向_支出.getValue() : DictCodeListExt1.收支方向_收入.getValue());
        }
        dto.setSettleAmt(deal.getTradeAmt());
        dto.setCcy(deal.getCcy());
        dto.setSettleStatus(DictCodeList.结算状态_已结算.getValue());
        dto.setIsNeedSend(DictCodeList.是否_否.getValue());
        dto.setIsTotalTrade(isTotalTrade);
        dto.setFinprodType2(deal.getFinprodType2());
        dto.setTradeId(deal.getTradeId());
        dto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());

        String mstSaleFlag = CacheUtil.getSysParam("TRANSFER_MST_SALE_FLAG");
        // 通道为空  并且 上交所  并且   债券业务  查券商资金账户  
        if(!StringUtil.isNotEmpty(deal.getChlId()) && ( DictCodeListExt1.估值交易市场_上交所.getValue().equals(deal.getTradeMarket())
        		|| DictCodeListExt1.估值交易市场_深交所.getValue().equals(deal.getTradeMarket())) && "Y".equals(mstSaleFlag)){
        	if (DictCodeListExt1.收支方向_支出.getValue().equals(dto.getPs())) {
	            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(portfolioId, deal.getCcy(),
	                    DictCodeListExt1.资金账户类型_券商资金账户.getValue()));
	            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(deal.getCounterId(), deal.getCcy(),
	                    DictCodeListExt1.资金账户类型_券商资金账户.getValue()));
	        }
	        else {
	            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(deal.getCounterId(), deal.getCcy(),
	                    DictCodeListExt1.资金账户类型_券商资金账户.getValue()));
	            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(portfolioId, deal.getCcy(),
	                    DictCodeListExt1.资金账户类型_券商资金账户.getValue()));
	        }
        }else{
        	
        	String payType = "";
        	if(StringUtil.isNotEmpty(deal.getChlId()) && !"999999".equals(deal.getChlId()) ){
        	    // start MODIFY BY WJ 20200519 通道转仓 一次性划款 取通道账户   TISHKBS-421【组合资金账户头寸查询】转仓交易，买入方资金流水明细统计错误
        		payType = bussSettleOrderCommMapper.getPayType(deal.getChlId());
        		
        	}	
        	
    		if(DictCodeListExt2.划款方式_一次性划款.getValue().equals(payType)){
    			
    			 dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectChlCapacctInfoList(deal.getChlId()));
    			 dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectChlCapacctInfoList(deal.getChlId()));
        		
    			 // end MODIFY BY WJ 20200519 通道转仓 一次性划款 取通道账户   TISHKBS-421【组合资金账户头寸查询】转仓交易，买入方资金流水明细统计错误
        	}else{
	        	// MyCapacct 是付款账户
	        	if (DictCodeListExt1.收支方向_支出.getValue().equals(dto.getPs())) {
		            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(portfolioId, deal.getCcy(),
		                    DictCodeListExt1.资金账户类型_产品托管户.getValue()));
		            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(deal.getCounterId(), deal.getCcy(),
		                    DictCodeListExt1.资金账户类型_产品托管户.getValue()));
		        }
		        else {
		            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(deal.getCounterId(), deal.getCcy(),
		                    DictCodeListExt1.资金账户类型_产品托管户.getValue()));
		            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(portfolioId, deal.getCcy(),
		                    DictCodeListExt1.资金账户类型_产品托管户.getValue()));
		        }
        	}
        }
        
        return dto;
    }

    // 通道交易 新增 强成西 2019/03/26 start
    private BussSettleOrderResultDto do通道处理(String orderId, TrdProductDeal deal, String isTotalTrade, String portfolioId, String payType) {
        BussSettleOrderResultDto dto = new BussSettleOrderResultDto();
        dto.setOrderId(orderId);
        dto.setSettleDate(deal.getSettleDate() == null ? deal.getTradeDate() : deal.getSettleDate());
        // 内部交易时，只有买入卖出
        dto.setPs(
            DictCodeList.交易方向_买入.getValue().equals(deal.getPs()) || DictCodeList.交易方向_付费.getValue().equals(
                deal.getPs()) ? DictCodeListExt1.收支方向_支出.getValue() : DictCodeListExt1.收支方向_收入.getValue());
        dto.setSettleAmt(deal.getTradeAmt());
        dto.setCcy(deal.getCcy());
        dto.setSettleStatus(DictCodeList.结算状态_已结算.getValue());
        dto.setIsNeedSend(DictCodeList.是否_否.getValue());
        dto.setIsTotalTrade(isTotalTrade);
        dto.setFinprodType2(deal.getFinprodType2());
        dto.setTradeId(deal.getTradeId());
        dto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
        dto.setMyCapacctInfoList(new ArrayList<>());
        dto.setCounterCapacctInfoList(new ArrayList<>());
        if (DictCodeListExt2.划款方式_一次性划款.getValue().equals(payType)) {
            dto.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
            // MyCapacct 是付款账户
            List<StlCapacctInfo> myAccts = bussSettleOrderCommMapper.selectChlCapacctInfoList(deal.getChlId());
            if (DictCodeListExt1.收支方向_支出.getValue().equals(dto.getPs())) {
                dto.setMyCapacctInfoList(myAccts);
                // 通道付费时，收款账户设为空
                if (!DictCodeList.交易方向_付费.getValue().equals(deal.getPs())) {
                    List<StlCapacctInfo> counterAccts = bussSettleOrderCommMapper.selectCounterCapacctInfoList(deal.getCounterId(),
                            DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy());
                    dto.setCounterCapacctInfoList(counterAccts);
                    dto.setOrderType(DictCodeListExt1.指令类型_费用支付_交易场所费用支付.getValue());
                }
            }
            else {
                List<StlCapacctInfo> counterAccts = bussSettleOrderCommMapper.selectCounterCapacctInfoList(deal.getCounterId(),
                        DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy());
                dto.setMyCapacctInfoList(counterAccts);
                dto.setCounterCapacctInfoList(myAccts);
            }
        }
        else  {
            dto.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
            List<StlCapacctInfo> myAccts = bussSettleOrderCommMapper.selectProdCapacctInfoList(portfolioId, deal.getCcy(),
                DictCodeListExt1.资金账户类型_产品托管户.getValue());
            if (DictCodeListExt1.收支方向_支出.getValue().equals(dto.getPs())) {
                dto.setMyCapacctInfoList(myAccts);
                if (!DictCodeList.交易方向_付费.getValue().equals(deal.getPs())) {
                    List<StlCapacctInfo> counterAccts = bussSettleOrderCommMapper.selectCounterCapacctInfoList(deal.getCounterId(),
                            DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy());
                    dto.setCounterCapacctInfoList(counterAccts);
                    dto.setOrderType(DictCodeListExt1.指令类型_费用支付_交易场所费用支付.getValue());
                }
            }
            else {
                List<StlCapacctInfo> counterAccts = bussSettleOrderCommMapper.selectCounterCapacctInfoList(deal.getCounterId(),
                        DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy());
                dto.setMyCapacctInfoList(counterAccts);
                dto.setCounterCapacctInfoList(myAccts);
            }
        }

        return dto;
    }
    // 通道交易 新增 强成西 2019/03/26 end

    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB01(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_清算划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());

        dto.setMyCapacctInfoList(new ArrayList<>());
        dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        return dto;
    }

    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB02(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();
     // 一体化对应etliu start
//        if(!StringUtil.equals(deal.getPs(), DictCodeList.交易方向_赎回.getValue())
        if(!(StringUtil.equals(deal.getPs(), DictCodeList.交易方向_赎回.getValue())
            || StringUtil.equals(deal.getPs(), DictCodeList.交易方向_产品赎回.getValue()))
         // 一体化对应etliu end
            || !StringUtil.equals(inputDto.getProfitType(), DictCodeList.收益类型_收益型.getValue())){

        	// 一体化对应etliu start
//            BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                    DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
//                    DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
        	BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                    DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
                    DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
         // 一体化对应etliu end

            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品兑付户.getValue()));
            if (dto.getCounterCapacctInfoList().isEmpty() && inputDto.getDeal() != null && StringUtil.isNotEmpty(inputDto.getDeal().getFinprodId())) { // 新增，组合的兑付户没挂时，取产品的兑付户
                dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoListForProd(inputDto.getDeal().getFinprodId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品兑付户.getValue()));
            }
            if(StringUtil.equals(deal.getPs(), DictCodeList.交易方向_赎回.getValue())
                && !StringUtil.equals(inputDto.getProfitType(), DictCodeList.收益类型_收益型.getValue())){
                dto.setIsNeedSend(DictCodeList.是否_否.getValue());
            }
            return dto;
        }
        else{
            return getResultB03(inputDto);
        }
    }

    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB03(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
//                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
     // 一体化对应etliu end

        String acctType = null;
        if(StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_理财产品.getValue())){
            acctType = DictCodeListExt1.资金账户类型_产品兑付户.getValue();
        }
        else if(StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_固定管理费.getValue())){
            acctType = DictCodeListExt1.资金账户类型_产品管理费账户.getValue();
        }
        else if(StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_托管费.getValue())){
            acctType = DictCodeListExt1.资金账户类型_产品托管费账户.getValue();
        }
        else if(StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_销售费.getValue())){
            acctType = DictCodeListExt1.资金账户类型_产品销售费账户.getValue();
        }

        // 一体化对应etliu start
//        dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), acctType));
//        dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), acctType));
        // 一体化对应etliu end
        return dto;
    }

    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB21(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
//                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
     // 一体化对应etliu end

        dto.setCounterCapacctInfoList(Collections.emptyList());
        // FinProductFeePay004页面传的是费用品种类型
        if(StringUtil.equals(deal.getFinprodType2(), DictCodeList.费用品种类型_固定管理费.getValue()) 
        		|| StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_固定管理费.getValue())){
            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品管理费账户.getValue()));
        }
        else if(StringUtil.equals(deal.getFinprodType2(), DictCodeList.费用品种类型_托管费.getValue())
        		|| StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_托管费.getValue())){
            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管费账户.getValue()));
        }
        else if(StringUtil.equals(deal.getFinprodType2(), DictCodeList.费用品种类型_销售手续费.getValue())
        		|| StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_销售费.getValue())){
            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品销售费账户.getValue()));
        } 
        else if(StringUtil.equals(deal.getFinprodType2(), DictCodeList.业务类型_增值税交易.getValue())) {
        	if(StringUtil.equals(DictCodeList.交易方向_税费退税.getValue(), deal.getPs())) {
        		 dto.setPs(DictCodeListExt1.收支方向_收入.getValue());
        	}
        	dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_增值税账户.getValue()));
        }
        // modify by jiangpeng on 20210510 start YHZGCPB20210115001 通道增值税缴纳
        if(StringUtil.isNotEmpty(deal.getChlId())){
            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectChlAcctNoInfoList(deal.getChlId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        }else {
            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        }
        // modify by jiangpeng on 20210510 end YHZGCPB20210115001 通道增值税缴纳
//        if (StringUtil.equals(deal.getFinprodType2(), DictCodeList.业务类型_增值税交易.getValue())
//                && StringUtil.equals(DictCodeList.交易方向_税费退税.getValue(), deal.getPs())) {
//                dto.setPs(DictCodeListExt1.收支方向_收入.getValue());
//                dto.setCounterCapacctInfoList(dto.getMyCapacctInfoList());
//                dto.setMyCapacctInfoList(Collections.emptyList());
//            }
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB04(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
//                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
     // 一体化对应etliu end

        dto.setMyCapacctInfoList(getSysParamCapacctInfoList(inputDto.getCacheMap()));
        if(StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())
            ||StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆出.getValue())
            ||StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆入.getValue())
            ||StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_自定义现金流.getValue())){
            dto.setCounterCapacctInfoList(new ArrayList<>());
        }
        else{
            if (StringUtil.isNotEmpty(deal.getSecManageAcctId())) {
                dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectDealCapacctInfoList(deal.getSecManageAcctId(), deal.getCcy()));
            }
            else {
                dto.setCounterCapacctInfoList(Collections.EMPTY_LIST);
            }
        }
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB05(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
     // 一体化对应etliu end

        if(StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())
                ||StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆出.getValue())
                ||StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆入.getValue())
                ||StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_自定义现金流.getValue())){
            dto.setMyCapacctInfoList(new ArrayList<>());
        }
        else{
            if (StringUtil.isNotEmpty(deal.getSecManageAcctId())) {
                dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectDealCapacctInfoList(deal.getSecManageAcctId(), deal.getCcy()));
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("secAcctId", deal.getSecManageAcctId());
                String secAcctSource = bussSettleOrderCommMapper.selectSecAcctSource(param);
                if (StringUtil.equals(secAcctSource, DictCodeListExt1.账户来源_自营.getValue())) {
                    dto.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
                }
            }
            else {
                dto.setMyCapacctInfoList(Collections.EMPTY_LIST);
            }
        }
        dto.setCounterCapacctInfoList(getSysParamCapacctInfoList(inputDto.getCacheMap()));
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB06(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
//                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
     // 一体化对应etliu end

        dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        // 证券托管户来源为理财的，交易市场一级市场，交易平台为其他的，收款账户为交易对手账户
        dto.setCounterCapacctInfoList(new ArrayList<StlCapacctInfo>());
        if (DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                && DictCodeListExt1.交易市场_一级市场.getValue().equals(deal.getTradeMarketMode()) && DictCodeList.交易平台_其它.getValue().equals(deal.getTradePlat())
                && StringUtil.isNotEmpty(deal.getSecManageAcctId())) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("secAcctId", deal.getSecManageAcctId());
            String secAcctSource = bussSettleOrderCommMapper.selectSecAcctSource(param);
            if (DictCodeListExt1.账户来源_理财.getValue().equals(secAcctSource)) {
                dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectCounterCapacctInfoList(deal.getCounterId(), DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy()));
            }
            else {
                dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectDealCapacctInfoList(deal.getSecManageAcctId(), deal.getCcy()));
            }
        }
        else {
        	if(StringUtil.isNotEmpty(deal.getSecManageAcctId())){
        		dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectDealCapacctInfoList(deal.getSecManageAcctId(), deal.getCcy()));
        	}
        }
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB07(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
//                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
     // 一体化对应etliu end

        dto.setMyCapacctInfoList(getDealCapacctInfoList(deal, inputDto));
        // add by renping 利率互换划款指令用行内统一账户 20190826 start
        if (StringUtil.equals(deal.getFinprodType2(),
            DictCodeListExt1.金融产品类型_投管用_利率互换.getValue())) {
            dto.setCounterCapacctInfoList(
                getSysParamCapacctInfoList(BussinessConstant.IRS_ACCOUNT_NO_KEY,
                    BussinessConstant.IRS_ACCOUNT_NAME_KEY, inputDto.getCacheMap()));
        }
        else {
            dto.setCounterCapacctInfoList(getSysParamSettleCapacctInfoList(inputDto.getCacheMap()));
        }
        // add by renping 利率互换划款指令用行内统一账户 20190826 end
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB08(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());

        if (StringUtil.equals(deal.getBusiType(), DictCodeListExt3.平安业务类型_融出债券付息还本_收.getValue())) {
            dto.setPs(DictCodeListExt1.收支方向_收入.getValue());
        }

        if (StringUtil.equals(deal.getBusiType(), DictCodeListExt3.平安业务类型_融出债券付息还本_收.getValue())) {
            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectCounterCapacctInfoList(deal.getCounterId(), DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy()));
            dto.setIsNeedSend(DictCodeList.是否_否.getValue());
        }
        else {
            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectCounterCapacctInfoList(deal.getCounterId(), DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy()));
        }
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB09(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
//                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
     // 一体化对应etliu end

        dto.setSettleDate(deal.getSettleDate() == null ? deal.getTradeDate() : deal.getSettleDate());
        if (StringUtil.isNotEmpty(deal.getSecManageAcctId())) {
            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectDealCapacctInfoList(deal.getSecManageAcctId(), deal.getCcy()));
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("secAcctId", deal.getSecManageAcctId());
            String secAcctSource = bussSettleOrderCommMapper.selectSecAcctSource(param);
            if (StringUtil.equals(secAcctSource, DictCodeListExt1.账户来源_自营.getValue())) {
                dto.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
            }
        }
        else {
            dto.setMyCapacctInfoList(Collections.EMPTY_LIST);
        }
        dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB10(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
//                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
     // 一体化对应etliu end

        List<StlCapacctInfo> list = new ArrayList<>();// getSysParamSettleCapacctInfoList(inputDto.getCacheMap());
        dto.setMyCapacctInfoList(list);
        dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB11(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_清算划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
     // 一体化对应etliu end
        if (StringUtil.isNotEmpty(deal.getSecManageAcctId())) {
            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectDealCapacctInfoList(deal.getSecManageAcctId(), deal.getCcy()));
        }
        else {
            dto.setMyCapacctInfoList(Collections.EMPTY_LIST);
        }
        dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB12(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
//                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
     // 一体化对应etliu end

        dto.setMyCapacctInfoList(getSysParamSettleCapacctInfoList(inputDto.getCacheMap()));
        dto.setCounterCapacctInfoList(getDealCapacctInfoList(deal, inputDto));
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB13(BussSettleOrderInputDto inputDto) {
        return getResultB12(inputDto);
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB14(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
//                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
     // 一体化对应etliu end
        // add by renping 利率互换划款指令用行内统一账户 20190826 start
        if (StringUtil.equals(deal.getFinprodType2(),
            DictCodeListExt1.金融产品类型_投管用_利率互换.getValue())) {
            dto.setMyCapacctInfoList(
                getSysParamCapacctInfoList(BussinessConstant.IRS_ACCOUNT_NO_KEY,
                    BussinessConstant.IRS_ACCOUNT_NAME_KEY, inputDto.getCacheMap()));
        }
        else {
            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectCounterCapacctInfoList(deal.getCounterId(), DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy()));
        }
        // add by renping 利率互换划款指令用行内统一账户 20190826 end
        dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB15(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
//                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
     // 一体化对应etliu end

        dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(),
                DictCodeListExt1.资金账户类型_产品托管户.getValue()));
    	dto.setCounterCapacctInfoList(new ArrayList<>());
    	if (StringUtil.isNotEmpty(deal.getCounterId())) {
            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectCounterCapacctInfoList(deal.getCounterId(),
                    DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy()));
        }
    	//dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectCounterCapacctInfoList(bussSettleOrderCommMapper.selectAssetProvider(deal.getFinprodId()), DictCodeListExt1.资金账户类型_交易对手账户.getValue()));
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB16(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
//                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
     // 一体化对应etliu end

        dto.setMyCapacctInfoList(new ArrayList<>());
        if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())
            ||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_期权.getValue())
            ||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue()) ){
        	if (StringUtil.isNotEmpty(deal.getCounterId())) {
                dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectCounterCapacctInfoList(deal.getCounterId(),
                        DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy()));
            }
        }

        // add by etliu 20210820 start
        // 自定义现金流 收取 付款账户默认空
        if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_自定义现金流.getValue())
                && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收取.getValue())) {
            dto.setMyCapacctInfoList(new ArrayList<>());
        }
        // add by etliu 20210820 end

        dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB17(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
//                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
     // 一体化对应etliu end

        dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectCounterCapacctInfoList(deal.getCounterId(), DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy()));
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB18(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();
        if(StringUtil.equals(deal.getPs(), DictCodeList.交易方向_买入.getValue())){

        	// 一体化对应etliu start
//            BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                    DictCodeList.划款类型_清算划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
//                    DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
        	BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                    DictCodeListExt1.平安划款类型_清算划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
                    DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
         // 一体化对应etliu end

            dto.setMyCapacctInfoList(getSysParamCapacctInfoList(BussinessConstant.LENDING_ACCT_NO_KEY, BussinessConstant.LENDING_ACCT_NAME_KEY, inputDto.getCacheMap()));
            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
            return dto;
        }
        else{
        	// 一体化对应etliu start
//            BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                    DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
//                    DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
        	BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                    DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
                    DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
         // 一体化对应etliu end

            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
            dto.setCounterCapacctInfoList(getSysParamCapacctInfoList(BussinessConstant.LENDING_ACCT_NO_KEY, BussinessConstant.LENDING_ACCT_NAME_KEY, inputDto.getCacheMap()));
            return dto;
        }
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB19(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//                DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
//                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
                DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
     // 一体化对应etliu end

        dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectFirstChannelCapacctInfoList(deal.getFinprodId(), DictCodeListExt1.资金账户类型_通道账户.getValue()));
        if(StringUtil.isEmptyList(dto.getCounterCapacctInfoList())&&StringUtil.isNotEmpty(deal.getCounterId())){
            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectCounterCapacctInfoList(deal.getCounterId(), DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy()));
        }
        return dto;
    }
    /**
     * 生成清算指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB20(BussSettleOrderInputDto inputDto) {
//        return getResultB19(inputDto);

        TrdProductDeal deal = inputDto.getDeal();

     // 一体化对应etliu start
//        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
//            DictCodeList.划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
//            DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
            DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
     // 一体化对应etliu end

        if (StringUtil.isNotEmpty(deal.getSecManageAcctId())) {
            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectDealCapacctInfoList(deal.getSecManageAcctId(), deal.getCcy()));
        }
        else {
            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectFirstChannelCapacctInfoList(
                    deal.getFinprodId(), DictCodeListExt1.资金账户类型_通道账户.getValue()));
        }
        if (StringUtil.isEmptyList(dto.getMyCapacctInfoList())
            && StringUtil.isNotEmpty(deal.getCounterId())) {
            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectCounterCapacctInfoList(
                deal.getCounterId(), DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy()));
        }
        dto.setCounterCapacctInfoList(
            bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(),
                deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        return dto;
    }

    /**
     * 生成托管指令
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB27(BussSettleOrderInputDto inputDto) {

        TrdProductDeal deal = inputDto.getDeal();
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
            DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());

        dto.setCounterCapacctInfoList(
                bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(),
                        deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        if (StringUtil.isNotEmpty(deal.getSecManageAcctId())) {
            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectDealCapacctInfoList(deal.getSecManageAcctId(), deal.getCcy()));
            dto.setMyCapacctInfoList(new ArrayList<>());
        }
        else {
            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectFirstChannelCapacctInfoList(
                    deal.getFinprodId(), DictCodeListExt1.资金账户类型_通道账户.getValue()));
        }
        if (StringUtil.isEmptyList(dto.getMyCapacctInfoList())
            && StringUtil.isNotEmpty(deal.getCounterId())) {
            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectCounterCapacctInfoList(
                deal.getCounterId(), DictCodeListExt1.资金账户类型_交易对手账户.getValue(), deal.getCcy()));
        }
        return dto;
    }
    /**
     * 生成清算指令（内转买入）
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB22(BussSettleOrderInputDto inputDto) {
//        return getResultB19(inputDto);

        TrdProductDeal deal = inputDto.getDeal();
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
            DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
            DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
        dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        dto.setCounterCapacctInfoList(new ArrayList<>());
        return dto;
    }
    /**
     * 生成清算指令（内转卖出）
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB23(BussSettleOrderInputDto inputDto) {
//        return getResultB19(inputDto);

        TrdProductDeal deal = inputDto.getDeal();
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
            DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
            DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());
        dto.setMyCapacctInfoList(new ArrayList<>());
        dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
        return dto;
    }

    /**
     * 生成清算指令（资金划拨管理）
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB24(BussSettleOrderInputDto inputDto) {
//        return getResultB19(inputDto);

        TrdProductDeal deal = inputDto.getDeal();
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
            DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
            DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());

        // 资金的划拨管理，收付款账户直接取画面上的收款账户和付款账户,后台直接返回空列表，前台处理
        dto.setMyCapacctInfoList(new ArrayList<>());
        dto.setCounterCapacctInfoList(new ArrayList<>());

        if(StringUtil.equals(deal.getPs(), DictCodeListExt1.指令类型_银证转账_证转银.getValue())){
//            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_券商资金账户.getValue()));
//            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.getPtlSecTrdAcctNo(inputDto.getPortfolioId()));
//            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
            dto.setPs(DictCodeListExt1.收支方向_收入.getValue());
        }else{
//            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_券商资金账户.getValue()));
//            dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.getPtlSecTrdAcctNo(inputDto.getPortfolioId()));
//            dto.setMyCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));
            dto.setPs(DictCodeListExt1.收支方向_支出.getValue());
        }
        return dto;
    }

    /**
     * 生成清算指令（DVP资金调拨管理）
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB25(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
            DictCodeListExt1.平安划款类型_DVP划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
            DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());

        if (StringUtil.equals(DictCodeList.交易方向_托管户_上清DVP.getValue(), deal.getPs())
            || StringUtil.equals(DictCodeList.交易方向_托管户_中债DVP.getValue(), deal.getPs())) {
            dto.setPs(DictCodeListExt1.收支方向_支出.getValue());
            dto.setIsNeedSend(DictCodeList.是否_是.getValue());
        }
        else if (StringUtil.equals(DictCodeList.交易方向_上清DVP_托管户.getValue(), deal.getPs())
            || StringUtil.equals(DictCodeList.交易方向_中债DVP_托管户.getValue(), deal.getPs())) {
            dto.setPs(DictCodeListExt1.收支方向_收入.getValue());
            dto.setIsNeedSend(DictCodeList.是否_是.getValue());
        }
        //add by wm 清DVP_中债DVP与中债DVP_上清DVP生成指令时，是否发送为是 start 2019-09-11
        else if (StringUtil.equals(DictCodeList.交易方向_上清DVP_中债DVP.getValue(), deal.getPs())
                || StringUtil.equals(DictCodeList.交易方向_中债DVP_上清DVP.getValue(), deal.getPs())) {
                dto.setPs(DictCodeListExt1.收支方向_支出.getValue());
                dto.setIsNeedSend(DictCodeList.是否_是.getValue());
        }
        //add by wm 清DVP_中债DVP与中债DVP_上清DVP生成指令时，是否发送为是 end 2019-09-11
        else {
            dto.setPs(DictCodeListExt1.收支方向_支出.getValue());
            dto.setIsNeedSend(DictCodeList.是否_否.getValue());
        }

        // DVP资金调拨管理,收付款账户直接取画面上的收款账户和付款账户,后台直接返回空列表，前台处理
        dto.setMyCapacctInfoList(new ArrayList<>());
        dto.setCounterCapacctInfoList(new ArrayList<>());

        return dto;
    }

    /**
     * 生成清算指令（通道资金划拨）
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB26(BussSettleOrderInputDto inputDto) {
        TrdProductDeal deal = inputDto.getDeal();
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
            DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_支出.getValue(),
            DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_是.getValue());

        if (StringUtil.equals(DictCodeList.交易方向_通道划款.getValue(), deal.getPs())) {
            dto.setPs(DictCodeListExt1.收支方向_支出.getValue());
        }
        else if (StringUtil.equals(DictCodeList.交易方向_通道回款.getValue(), deal.getPs())) {
            dto.setPs(DictCodeListExt1.收支方向_收入.getValue());
            // modify by wj 20200514   TISHKBS-455 【UAT通道资金划拨管理】通道回款交易页面指令信息栏是否发送指令默认值应为否
            dto.setIsNeedSend(DictCodeList.是否_否.getValue());
        }

        // 通道资金划拨管理,收付款账户直接取画面上的收款账户和付款账户,后台直接返回空列表，前台处理
        dto.setMyCapacctInfoList(new ArrayList<>());
        dto.setCounterCapacctInfoList(new ArrayList<>());

        return dto;
    }
    
    /**
     * 生成划款指令（收息）
     * @param inputDto 入参
     * @return 清算指令
     */
    private BussSettleOrderResultDto getResultB28(BussSettleOrderInputDto inputDto) {

        TrdProductDeal deal = inputDto.getDeal();
        BussSettleOrderResultDto dto = getBussSettleOrderResultDto(inputDto, deal,
                DictCodeListExt1.平安划款类型_托管划款.getValue(), DictCodeListExt1.收支方向_收入.getValue(),
            DictCodeList.结算状态_已结算.getValue(), DictCodeList.是否_否.getValue());

        dto.setMyCapacctInfoList(Collections.emptyList());
        
        dto.setCounterCapacctInfoList(bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue()));

        return dto;
    }

    /**
     * 如果证券托管户为空，获取产品资金账户，否则获取交易资金账户
     * @param deal 金融交易
     * @return 资金账户列表
     */
    private List<StlCapacctInfo> getDealCapacctInfoList(TrdProductDeal deal, BussSettleOrderInputDto inputDto) {
        if(StringUtil.isEmpty(deal.getSecManageAcctId()) || DictCodeListExt1.交易场所_境外交易场所.getValue().equals(deal.getTradeMarket())){
            return bussSettleOrderCommMapper.selectProdCapacctInfoList(inputDto.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
        }
        else{
            return bussSettleOrderCommMapper.selectDealCapacctInfoList(deal.getSecManageAcctId(), deal.getCcy());
        }
    }

    /**
     * 获取系统参数指定的资金账户
     *  @param cacheMap 缓存map
     * @return 资金账户
     */
    private List<StlCapacctInfo> getSysParamCapacctInfoList(Map<String, String> cacheMap) {
        return getSysParamCapacctInfoList(BussinessConstant.PROD_TRU_ACCT_NO_KEY, BussinessConstant.PROD_TRU_ACCT_NAME_KEY, cacheMap);
    }

    /**
     * 获取系统参数指定的资金账户
     *  @param cacheMap 缓存map
     * @return 资金账户
     */
    private List<StlCapacctInfo> getSysParamSettleCapacctInfoList(Map<String, String> cacheMap) {
        return getSysParamCapacctInfoList(BussinessConstant.SETTLE_ACCT_NO_KEY, BussinessConstant.SETTLE_ACCT_NAME_KEY, cacheMap);
    }

    /**
     * 获取指定系统参数指定的资金账户
     * @param acctNoKey 账户代码key
     * @param acctNameKey 账户名称key
     * @param cacheMap 缓存map
     * @return 资金账户
     */
    private List<StlCapacctInfo> getSysParamCapacctInfoList(String acctNoKey, String acctNameKey, Map<String, String> cacheMap) {
        List<StlCapacctInfo> list = new ArrayList<>(1);
        StlCapacctInfo info = new StlCapacctInfo();
        // info.setAcctNo(CacheUtil.getSysParam(acctNoKey));
        // info.setAcctName(CacheUtil.getSysParam(acctNameKey));
        info.setAcctNo(cacheMap.get(acctNoKey));
        info.setAcctName(cacheMap.get(acctNameKey));
        list.add(info);
        return list;
    }

    /**
     * 生成清算指令结果通用字段
     * @param inputDto 入参
     * @param deal 金融交易
     * @param payType 划款类型
     * @param ps 交易方向
     * @param settleStatus 结算状态
     * @param isNeedSend 是否发送
     * @return 清算指令
     */
    private BussSettleOrderResultDto getBussSettleOrderResultDto(BussSettleOrderInputDto inputDto, TrdProductDeal deal, String payType, String ps, String settleStatus, String isNeedSend) {
        BussSettleOrderResultDto dto = new BussSettleOrderResultDto();
        dto.setOrderId(inputDto.getOrderId());
        dto.setPayType(payType);
        dto.setSettleDate(deal.getSettleDate() == null ? deal.getTradeDate() : deal.getSettleDate());
        dto.setPs(ps);
        dto.setSettleAmt(deal.getTradeAmt());
        dto.setCcy(deal.getCcy());
        dto.setSettleStatus(settleStatus);
        dto.setIsNeedSend(isNeedSend);
        dto.setIsTotalTrade(inputDto.getIsTotalTrade());
        dto.setFinprodType2(inputDto.getFinprodType2());
        dto.setTradeId(deal.getTradeId());
        dto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
        return dto;
    }

    public List<BussTrdTransferOrderBase> selectOrdersByBizCode(String bizCode) {
        List<BussTrdTransferOrderBase> result = bussSettleOrderCommMapper.selectOrdersByBizCode(bizCode, DictCodeListExt1.收支方向_支出.getValue());
        if (result == null || result.isEmpty()) {
            result = new ArrayList<>();
            List<String> subBizCodes = bussSettleOrderCommMapper.findSubTotalTradeId(bizCode);
            if (subBizCodes != null && !subBizCodes.isEmpty()) {
                List<BussTrdTransferOrderBase> result1 = new ArrayList<>();
                subBizCodes.forEach(s -> {
                    result1.addAll(bussSettleOrderCommMapper.selectOrdersByBizCode(s, DictCodeListExt1.收支方向_支出.getValue()));
                });
                result.addAll(result1);
            } else {
                subBizCodes = bussSettleOrderCommMapper.findSubWflTotalTradeId(bizCode);
                if (subBizCodes != null && !subBizCodes.isEmpty()) {
                    List<BussTrdTransferOrderBase> result1 = new ArrayList<>();
                    subBizCodes.forEach(s -> {
                        result1.addAll(bussSettleOrderCommMapper.selectWflOrdersByBizCode(s, DictCodeListExt1.收支方向_支出.getValue()));
                    });
                    result.addAll(result1);
                }
            }
        }
        if (result == null || result.isEmpty()) {
            result = bussSettleOrderCommMapper.selectWflOrdersByBizCode(bizCode, DictCodeListExt1.收支方向_支出.getValue());
        }
        if (result == null || result.isEmpty()) {
            List<String> tradeIds = bussSettleOrderCommMapper.findSubTradeId(bizCode);
            if (tradeIds != null && !tradeIds.isEmpty()) {
                List<BussTrdTransferOrderBase> result1 = new ArrayList<>();
                tradeIds.forEach(s -> {
                    result1.addAll(bussSettleOrderCommMapper.selectOrdersByBizCode(s, DictCodeListExt1.收支方向_支出.getValue()));
                });
                result.addAll(result1);
            } else {
                tradeIds = bussSettleOrderCommMapper.findSubWflTradeId(bizCode);
                if (tradeIds != null && !tradeIds.isEmpty()) {
                    List<BussTrdTransferOrderBase> result1 = new ArrayList<>();
                    tradeIds.forEach(s -> {
                        result1.addAll(bussSettleOrderCommMapper.selectWflOrdersByBizCode(s, DictCodeListExt1.收支方向_支出.getValue()));
                    });
                    result.addAll(result1);
                }
            }
        }
        // 撤销数据查看时，根据“R”结尾的total_trade_id查找R结尾的交易表数据，并查找对应的指令 add by renping 20190916 start
        if ((result == null || result.isEmpty()) && bizCode != null && bizCode.endsWith("R")) {
            String oldBizCode = bizCode.substring(0, bizCode.length() - 1);
            List<String> tradeIds = bussSettleOrderCommMapper.findSubTradeId(oldBizCode);
            if (tradeIds != null && !tradeIds.isEmpty()) {
                List<BussTrdTransferOrderBase> result1 = new ArrayList<>();
                tradeIds.forEach(s -> {
                    if (s.endsWith("R")) {
                        result1.addAll(bussSettleOrderCommMapper.selectOrdersByBizCode(s,
                            DictCodeListExt1.收支方向_支出.getValue()));
                    }
                });
                result.addAll(result1);
            }
        }
        // 撤销数据查看时，根据“R”结尾的total_trade_id查找R结尾的交易表数据，并查找对应的指令 add by renping 20190916 end
        if (!StringUtil.isEmptyList(result)){
             result.forEach(item->{
                 if (StringUtil.isNotEmpty(item.getPayAcctNo())
                         && StringUtil.equals(item.getPayAcctNo(), CacheUtil.getSysParam("settle_acct_no"))){
                     item.setPayAcctName(CacheUtil.getSysParam("settle_acct_name"));
                 }
             });
        }
        return result;
    }

    public List<BussTrdTransferOrderBase> selectOrdersByBizCodeZhichu(String bizCode) {
        return bussSettleOrderCommMapper.selectOrdersByBizCode(bizCode, DictCodeListExt1.收支方向_支出.getValue());
    }

    /**
     * 查询流程中的数据
     * <功能详细描述>
     * @param bizCode
     * @return
     * @see
     * @since 1.0
     */
    public List<BussTrdTransferOrderBase> selectWflOrdersByBizCode(String bizCode) {
        return bussSettleOrderCommMapper.selectWflOrdersByBizCode(bizCode, DictCodeListExt1.收支方向_支出.getValue());
    }

    /**
     * 收付款账户下拉框
     * @param paramMap 过滤参数
     * @return 下拉框列表
     * @throws Exception
     */
    public List<SelectOptionDto> queryAcctList(Map<String, Object> paramMap) {
        List<SelectOptionDto> selectList =  bussSettleOrderCommMapper.queryAcctList(paramMap);
        for (SelectOptionDto dto : selectList){
          if (dto.getId().startsWith(":")){
              dto.setId(dto.getId().substring(1,dto.getId().length()));
          }
        }
        return selectList;
    }

    /**
     * 根据账号查询清算账户表
     *
     * @param ccy 币种
     * @param busiAcctNo 账号
     * @return 清算账户表
     * @see
     * @since 1.0
     */
    public StlCapacctInfo queryStlCapacctInfoByBusiAcctNo(String ccy, String busiAcctNo){
        StlCapacctInfo rsultDto = bussSettleOrderCommMapper.queryStlCapacctInfoByBusiAcctNo(ccy, busiAcctNo);
        if (rsultDto == null) {
            rsultDto = bussSettleOrderCommMapper.queryStlCapacctInfoByBusiAcctNo(ccy, ":" + busiAcctNo);
        }

        return rsultDto;
    }

    /**
     * 根据账号查询清算账户表
     *
     * @param paramMap 币种
     * @return 清算账户表
     * @see
     * @since 1.0
     */
    public List<StlCapacctInfo> selectProdCapacctInfoList(Map<String, Object> paramMap){
    	String portfolioId = (String) paramMap.get("portfolioId");
    	String ccy = (String) paramMap.get("ccy");
    	String capacctType = (String) paramMap.get("capacctType");
        return bussSettleOrderCommMapper.selectProdCapacctInfoList(portfolioId, ccy, capacctType);
    }

    /**
     * 根据证券托管户和通道代码查询账号信息
     * @param paramMap 参数
     * @return 账号信息
     */
    public List<StlCapacctInfo> getAcctNoByInfo(Map<String, Object> paramMap) {
        if(paramMap.containsKey("chlId")&& StringUtil.isNotEmpty(paramMap.get("chlId"))){
            return bussSettleOrderCommMapper.selectChlCapacctInfoList((String)paramMap.get("chlId"));
        }
        else if(paramMap.containsKey("secManageAcctId")&& StringUtil.isNotEmpty(paramMap.get("secManageAcctId"))){
            return bussSettleOrderCommMapper.selectDealCapacctInfoList((String)paramMap.get("secManageAcctId"),(String)paramMap.get("ccy"));
        }
        else{
            return null;
        }
    }

    public List<StlCapacctInfo> selectCounterCapacctInfoList(Map<String, Object> paramMap) {
        return bussSettleOrderCommMapper.selectCounterCapacctInfoList((String)paramMap.get("counterId"),
                (String)paramMap.get("acctType"),(String)paramMap.get("ccy"));
    }

    public List<BussTrdTransferOrderBase> selectOrdersByBizCodeByBTradeId(String bizCode) {
        List<BussTrdTransferOrderBase> result = bussSettleOrderCommMapper.selectOrdersByBizCodeByBTradeId(bizCode, DictCodeListExt1.收支方向_支出.getValue());
        return result;
    }
}
