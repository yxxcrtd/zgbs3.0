/*
 * 文件名：CashFlowsStatement001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：QuJianZhong
 * 修改时间：2018年10月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.service.valuation.accounadjustmanage.cashflowsstatement.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchGrpPrdlink;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.accounadjustmanage.assetbook.service.ValAssetBook001Service;
import com.joyintech.service.valuation.accounadjustmanage.cashflowsstatement.dao.CashFlowsStatement001Mapper;
import com.joyintech.service.valuation.accounadjustmanage.cashflowsstatement.dto.CashFlowsDto;
import com.joyintech.service.valuation.accounadjustmanage.cashflowsstatement.dto.CashFlowsStatement001Dto;
import com.joyintech.tams.common.base.entity.BokBookset;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListNatureGroup;
import com.joyintech.tams.systemtools.service.SystemToolService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.SubjectBalance;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.common.batch.finproduct.service.AccountingCommonBatchService;
import com.joyintech.tams.valuation.common.batch.finproduct.service.FinProductCommonBatchService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author QuJianZhong
 * @version 1.0 2018年9月18日
 * @see CashFlowsStatement001Service
 * @since 1.0
 */
@Service(value="serviceCashFlowsStatement001Service")
public class CashFlowsStatement001Service extends CommonExportService<CashFlowsStatement001Dto> {

    @Autowired
    private CashFlowsStatement001Mapper cashFlowsStatement001Mapper;

    @Autowired
    private SystemToolService systemToolService;


    @Autowired
    private ValAssetBook001Service valAssetBook001Service;

    /**
    * 针对weblogic部署无法获取模版问题
    */
    private static final String TMP_DIR = "/tpl/";
   
    @Autowired
    private InterfCommonService interfCommonService;

    @Autowired
    private FinProductCommonBatchService finProductCommonBatchService;

    /**
     * 账务类共通
     */
    @Autowired
    private AccountingCommonBatchService accountingCommonBatchService;
    
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(CashFlowsStatement001Service.class);

    /** modify by ynaya on 20201221 指标抽象及计算中特定字符 start**/
    private final static String 汇总指标后缀 = "活动产生的现金流量";
    private final static String 汇总指标1 = "一、经营活动产生的现金流量";
    private final static String 汇总指标2 = "二、投资活动产生的现金流量";
    private final static String 汇总指标3 = "三、筹资活动产生的现金流量";
    private final static String 汇总指标4 = "四、汇率变动对现金及现金等价物的影响";
    private final static String 汇总指标5 = "五、现金及现金等价物净变动额";
    private final static String 汇总指标6 = "加:期初现金及现金等价物余额";
    private final static String 汇总指标7 = "六、期末现金及现金等价物余额";

    private final static String 小计指标1_1 = "经营活动现金流入小计";
    private final static String 小计指标1_2 = "经营活动现金流出小计";
    private final static String 小计指标1_3 = "经营活动产生的现金流量净额";

    private final static String 小计指标2_1 = "投资活动现金流入小计";
    private final static String 小计指标2_2 = "投资活动现金流出小计";
    private final static String 小计指标2_3 = "投资活动产生的现金流量净额";

    private final static String 小计指标3_1 = "筹资活动现金流入小计";
    private final static String 小计指标3_2 = "筹资活动现金流出小计";
    private final static String 小计指标3_3 = "筹资活动产生的现金流量净额";

    private  final static String 提前计算指标1 = "存款类资产交易-负值";
    private  final static String 提前计算指标2 = "存款类资产交易-正值";

    private final static String 取正 = "GT";
    private final static String 取负 = "LT";
    private final static String 原始 = "ORI";
    private final static String 正标识 = "P";
    private final static String 负标识 = "N";

    private final static String 利息 = "INTAMT";
    private final static String 本金 = "PRINAMT";
    private final static String 交易金额 = "TRADEAMT";
    private final static String 收息额 = "RECINT";
    private final static String 付息 = "PAYINT";
    private final static String 买入交易金额 = "BUYTRADEAMT";
    private final static String 卖出端交易金额 = "SELLTRADEAMT";

    private final static String 自定义现金流 = "ZDYXJL";
    private final static String 手工凭证 = "SGPZ";

    /** modify by ynaya on 20201221 指标抽象及计算中特定字符 end**/

    /**
     * 报表各项统计规则，计算各项值的通用方法会用到-计算前置条件
     *
     * {
     * 项目,
     * 父项目,
     * 金融产品类型,交易方向,买入现金流正负标识(N-负 P-正),金额分类(PRINAMT-本金,INTAMT-利息,TRADEAMT-交易金额)  此项有多条以|分割
     * 显示正值负值标识LT-合计值为负值显示，否则为0 GT-合计值为正值显示，否则为0 ORI-直接显示合计值
     * }
     */
    private static final String[][] CAL_RULE_PRE = {
            {提前计算指标1,
                    "F07,T02,P,PRINAMT,03|"
                            + "F07,T01,N,PRINAMT,03|"
                            + "F07,T23,P,TRADEAMT,03|"
                            + "F07,T25,N,PRINAMT,03|"
                            + "F07,T26,P,PRINAMT,03|",
                    取负},
            {提前计算指标2,
                    "F07,T02,P,PRINAMT,03|"
                            + "F07,T01,N,PRINAMT,03|"
                            + "F07,T23,P,TRADEAMT,03|"
                            + "F07,T25,N,PRINAMT,03|"
                            + "F07,T26,P,PRINAMT,03|",
                    取正}
    };
    /**
     * 报表各项统计规则，计算各项值的通用方法会用到
     *
     * {
     * 项目,
     * 父项目,
     * 金融产品类型,交易方向,买入现金流正负标识(N-负 P-正),金额分类(PRINAMT-本金,INTAMT-利息,TRADEAMT-交易金额)  此项有多条以|分割
     * 显示正值负值标识LT-合计值为负值显示，否则为0 GT-合计值为正值显示，否则为0 ORI-直接显示合计值
     * }
     */
    private static final String[][] CAL_RULE = {
            /** modify by yanya on 20201221 根据最新的估值核算指引改造展示表列 start**/
            //{"经营活动产生的现金流量"},
            {汇总指标1},
            /**modify by wujie on 20190105 start remark:显示正值负值标识由LT 改为 GT
            {"存放同业款项净减少额","经营活动现金流入小计","F07,T01,N,PRINAMT|F07,T02,P,PRINAMT|F07,T23,P,TRADEAMT","LT"},
             **/
//            {"存放同业款项净减少额","经营活动现金流入小计",
//            	"F07,T01,N,PRINAMT,03|"
//            	+ "F07,T02,P,PRINAM,03|"
//            	+ "F07,T23,P,TRADEAMT,03|"
//            	/**add by wujie on 20200213 start**/
//            	+ "F07,T25,N,PRINAMT,03|"
//            	+ "F07,T26,P,PRINAMT,03"
//            	/**add by wujie on 20200213 end**/
//            	,"GT"},
            /**modify by wujie on 20190105 end**/

//            {"卖出回购金融资产款净增加额","经营活动现金流入小计",
//            		"F02,T01,P,PRINAMT|"
//            		+ "F02,T02,N,PRINAMT|"
//            		+ "F02,T07,N,PRINAMT","GT"},
//
//            /** modify by wujie on 20190105 start remark:显示正值负值标识由LT 改为 GT
//            {"买入返售金融资产净减少额","经营活动现金流入小计","F03,T01,N,PRINAMT|F03,T02,P,PRINAMT|F03,T07,P,PRINAMT","LT"},
//             **/
//            {"买入返售金融资产净减少额","经营活动现金流入小计",
//            			"F03,T01,N,PRINAMT|"
//            			+ "F03,T02,P,PRINAMT|"
//            			+ "F03,T07,P,PRINAMT","GT"},
//            /**modify by wujie on 20190105 end**/
//
//            {"收取利息的现金","经营活动现金流入小计",
//            				"F07,T02,P,INTAMT,03|"
//            				+ "F07,T22,P,TRADEAMT,03|"
//            				/**add by wujie on 20200213 start**/
//            				+ "F07,T26,P,INTAMT,03|"
//            				/**add by wujie on 20200213 end**/
//            				+ "F03,T02,P,INTAMT,03|"
//            				+ "F03,T07,P,INTAMT,03","ORI"},
            {"收取利息、手续费及佣金的现金",
                    小计指标1_1,
                    "F07,T02,P,INTAMT,03|"
                            + "F07,T22,P,TRADEAMT,03|"
            				+ "F07,T26,P,INTAMT,03|"
                            /** modify by yanya on 20210113 增加买断式的统计 start**/
                            + "F05,T02,P,INTAMT,05|"
                            + "F05,T07,P,INTAMT,05|"
                            /** modify by yanya on 20210113 增加买断式的统计 end**/
            				+ "F03,T02,P,INTAMT,04|"
            				+ "F03,T07,P,INTAMT,04",
                    原始},
            {"回购业务资金净增加额",
                    小计指标1_1,
                    /** modify by yanya on 20210113 增加买断式的统计 start**/
                    "F04,T01,P,PRINAMT|"
                            + "F04,T02,N,PRINAMT|"
                            + "F04,T07,N,PRINAMT|"
                            + "F05,T01,N,PRINAMT|"
                            + "F05,T02,P,PRINAMT|"
                            + "F05,T07,P,PRINAMT|"
                    /** modify by yanya on 20210113 增加买断式的统计 end**/
            		    + "F02,T01,P,PRINAMT|"
            		    + "F02,T02,N,PRINAMT|"
            		    + "F02,T07,N,PRINAMT|"
                        + "F03,T01,N,PRINAMT|"
                        + "F03,T02,P,PRINAMT|"
                        + "F03,T07,P,PRINAMT",
                    原始},
            {"收到其他与经营活动有关的现金",
                    小计指标1_1,
                    "F09,T02,P,TRADEAMT|"
                            + "F09,T23,P,PRINAMT|"
                            + "F09,T22,P,INTAMT|"
                            + "F21,T02,P,TRADEAMT,01|"
                            + "F21,T23,P,PRINAMT,01|"
                            + "F21,T22,P,INTAMT,01|"
                            + "F21,T26,P,TRADEAMT,01|"
                            + 自定义现金流 + ",T98|"
                            + 提前计算指标1 +"|"
                            + 手工凭证 + ",D",
                    原始},
            {小计指标1_1},

//            /**modify by wujie on 20190105 start remark:显示正值负值标识由GT 改为 LT
//            {"存放同业款项净增加额","经营活动现金流出小计","F07,T01,N,PRINAMT|F07,T02,P,PRINAMT|F07,T07,P,PRINAMT|F07,T23,P,PRINAMT","GT"},
//             **/
//            {"存放同业款项净增加额","经营活动现金流出小计",
//            	"F07,T01,N,PRINAMT,03|"
//            	+ "F07,T02,P,PRINAMT,03|"
//            	+ "F07,T07,P,PRINAMT,03|"
//            	+ "F07,T23,P,PRINAMT,03","LT"},
//            /**modify by wujie on 20190105 end**/
//
//            {"卖出回购金融资产款净减少额","经营活动现金流出小计",
//            		"F02,T01,P,PRINAMT|"
//            		+ "F02,T02,N,PRINAMT|"
//            		+ "F02,T07,N,PRINAMT","LT"},
//
//            /** modify by wujie on 20190105 start remark:显示正值负值标识由GT 改为 LT
//            {"买入返售金融资产净增加额","经营活动现金流出小计","F03,T01,N,PRINAMT|F03,T02,P,PRINAMT|F03,T07,P,PRINAMT","GT"},
//             **/
//            {"买入返售金融资产净增加额","经营活动现金流出小计",
//            			"F03,T01,N,PRINAMT|"
//            			+ "F03,T02,P,PRINAMT|"
//            			+ "F03,T07,P,PRINAMT","LT"},
//            /**modify by wujie on 20190105 end**/
//
//            {"支付利息的现金","经营活动现金流出小计",
//            				"F07,T01,P,INTAMT,03|"
//            				/**add by wujie on 20200213 start**/
//            				+ "F07,T25,P,INTAMT,03|"
//            				/**add by wujie on 20200213 end**/
//            				+ "F02,T02,P,INTAMT|"
//            				+ "F02,T07,P,INTAMT","ORI"},
//            {"支付管理人报酬、托管费、销售管理费等支付的现金","经营活动现金流出小计",
//            					"F16,C11,P,TRADEAMT|"
//            					+ "C03,C11,P,TRADEAMT|"
//            					+ "C04,C11,P,TRADEAMT|"
//            					+ "C05,C11,P,TRADEAMT|"
//            					+ "C06,C11,P,TRADEAMT|"
//            					+ "C09,C11,P,TRADEAMT|"
//            					+ "C07,C12,P,TRADEAMT|"
//            					+ "C07,C13,N,TRADEAMT|"
//            					+ "C10,C11,P,TRADEAMT|"
//            					+ "C11,T10,P,TRADEAMT","ORI"},
//            {"支付其他与经营活动有关的现金","经营活动现金流出小计",
//            						"F09,T01,P,PRINAMT|"
//            						+ "F21,T01,P,PRINAMT,01|"
//            						/**add by wujie on 20200213 start**/
//            						+ "F21,T25,P,TRADEAMT,01|"
//            						/**add by wujie on 20200213 end**/
//            						+ "SGPZ,C","ORI"},
            {"支付手续费及佣金支付的现金",
                    小计指标1_2,
                    "F16,C11,P,TRADEAMT|"
                            + "C03,C11,P,TRADEAMT|"
                            + "C04,C11,P,TRADEAMT|"
                            + "C05,C11,P,TRADEAMT|"
                            + "C06,C11,P,TRADEAMT|"
                            + "C09,C11,P,TRADEAMT|"
                            + "C10,C11,P,TRADEAMT|"
                            + "C11,T10,P,TRADEAMT",
                    原始},
            {"支付的各项税费",
                    小计指标1_2,
                    "C07,C12,P,TRADEAMT|"
                            + "C07,C13,N,TRADEAMT|",
                    原始},
            {"支付其他与经营活动有关的现金",
                    小计指标1_2,
                    "F09,T01,P,PRINAMT|"
                            + "F21,T01,P,PRINAMT,01|"
                            + "F21,T25,P,TRADEAMT,01|"
                            + "F07,T01,P,INTAMT,03|"
                            + "F07,T25,P,INTAMT,03|"
                            /** modify by yanya on 20210113 增加买断式的统计 start**/
                            + "F04,T02,P,INTAMT|"
                            + "F04,T07,P,INTAMT|"
                            /** modify by yanya on 20210113 增加买断式的统计 end**/
                            + "F02,T02,P,INTAMT|"
                            + "F02,T07,P,INTAMT|"
                            + 自定义现金流 + ",T99|"
                            + 提前计算指标2 + "|"
                            + 手工凭证 + ",C",
                    原始},
            {小计指标1_2},
            {小计指标1_3},

            //{"投资活动产生的现金流量"},
            {汇总指标2},
            /** modify by wujie on 20190105 start
             * remark:
             *      计划类(净值型)资产:T02 交易金额
             *      计划类(净值型)资产:T24 交易金额
            {"收回投资收到的现金","投资活动现金流入小计","F01,T23,P,TRADEAMT|F01,T02,P,PRINAMT|F01,T31,P,TRADEAMT|F01,T34,P,PRINAMT|F06,T02,P,TRADEAMT|F06,T24,P,TRADEAMT|F17,T23,P,TRADEAMT|F17,T02,P,PRINAMT","ORI"},
             **/
            {"收回投资收到的现金",
                    小计指标2_1,
                    "F01,T23,P,TRADEAMT,02|"+
                            "F01,T02,P,PRINAMT,02|"+
                    		/**modify by wujie on 20200213 start
                            "F01,T31,P,TRADEAMT|"+
                            "F01,T34,P,PRINAMT|"+
                            **/
                            "F01,T26,P,PRINAMT,02|"+
                            "F01,T23,P,TRADEAMT,29|"+
                            "F01,T02,P,PRINAMT,29|"+
                            /**modify by wujie on 20200213 end**/

                            /**add by yinya on 20211213 end**/
                             "F01,T03,P,PRINAMT,02|"+
                             "F01,T39,P,PRINAMT,02|"+
                            /**add by yinya on 20211213 end**/

                            
                            "F06,T02,P,TRADEAMT|"+
                            "F06,T24,P,TRADEAMT|"+
                            
                            "F17,T23,P,TRADEAMT,09|"+
                            "F17,T02,P,PRINAMT,09|"+
                            "F17,T24,P,TRADEAMT,07|" +
                            /**add by wujie on 20200213 start**/
                            "F17,T02,P,TRADEAMT,07|"+
                            "F17,T26,P,PRINAMT,09|"+
							"F17,T24,P,TRADEAMT,06|" +
							"F17,T02,P,TRADEAMT,06|"+
							/**add by wujie on 20200213 end**/

                            "F11,300,P,BUYTRADEAMT|" +
                            "F12,410,P,BUYTRADEAMT|" +
                            "F34,760,P,BUYTRADEAMT|" +
                            "F34,761,P,BUYTRADEAMT|" +
                            "F10,200,P,BUYTRADEAMT|" +
                            "F10,107,P,BUYTRADEAMT|"+
                            /** add by yanya on 20200222 增加期权**/
                            "F35,T01_T59_01,P,TRADEAMT|"+
                            "F35,T01_T60_01,P,TRADEAMT|"+
                            "F35,T01_T59_02,N,TRADEAMT|"+
                            "F35,T01_T60_02,N,TRADEAMT|"+
                            "F35,T02_T59_01,P,TRADEAMT|"+
                            "F35,T02_T60_01,P,TRADEAMT|"+
                            "F35,T02_T59_02,N,TRADEAMT|"+
                            "F35,T02_T60_02,N,TRADEAMT|"
                    ,
                    原始},
            /**modify by wujie on 20190105 end**/

            /**modify by wujie on 20190105 start
             * remark:
             * 	计划类(净值型)资产:T11   交易金额
             *    计划类(净值型)资产:T12 交易金额
            {"取得投资收益收到的现金","投资活动现金流入小计","F01,T22,P,TRADEAMT|F01,T02,P,INTAMT|F01,T28,P,TRADEAMT|F01,T34,P,INTAMT|F06,T11,P,TRADEAMT|F06,T12,P,TRADEAMT|F17,T22,P,TRADEAMT|F17,T02,P,INTAMT|F24,T07,P,TRADEAMT","ORI"},
             **/
            {"取得投资收益收到的现金",
                    小计指标2_1,
                    "F01,T22,P,TRADEAMT,02|"
                            + "F01,T02,P,INTAMT,02|"
                    		/**modify by wujie on 20200213 start
                            + "F01,T28,P,TRADEAMT|"
                            + "F01,T34,P,INTAMT|"
                            **/
                            + "F01,T26,P,INTAMT,02|"
                            + "F01,T22,P,TRADEAMT,29|"
                            + "F01,T02,P,INTAMT,29|"
                            /**modify by wujie on 20200213 end**/

                            /**add by yinya on 20211213 end**/
                            + "F01,T03,P,INTAMT,02|"
                            + "F01,T39,P,INTAMT,02|"
                            /**add by yinya on 20211213 end**/
                            
                            + "F06,T11,P,TRADEAMT|"
                            + "F06,T02,P,INTAMT|"
                            + "F06,T12,P,TRADEAMT|"
                            + "F17,T22,P,TRADEAMT,09|"
                            + "F17,T02,P,INTAMT,09|"
                            + "F17,T11,P,TRADEAMT,07|"
                            + "F17,T12,P,TRADEAMT,07|"
                            /**add by wujie on 20200213 start**/
                            + "F17,T26,P,INTAMT,09|"
                            + "F17,T11,P,TRADEAMT,06|"
                            + "F17,T12,P,TRADEAMT,06|"
							/**add by wujie on 20200213 end**/
                            
                            + "F24,T07,P,TRADEAMT|"

                            + "F14,752,P,RECINT|"
                            + "F14,751,P,RECINT|"
                            + "F10,103,P,BUYTRADEAMT|",
                    原始},
            /**modify by wujie on 20190105 end**/

            {"收到的其他与投资活动有关的现金",小计指标2_1,
                    	"F01,T22,P,TRADEAMT,37|"
                    	+ "F01,T02,P,TRADEAMT,37|"
                                + "F01,T03,P,TRADEAMT,37|"
                                + "F01,T39,P,TRADEAMT,37|"
                    	+ "F17,T22,P,TRADEAMT,37|"
                    	+ "F17,T02,P,TRADEAMT,37|"
                    	+ "F07,T22,P,TRADEAMT,37|"
                    	+ "F07,T02,P,TRADEAMT,37|"
                    	+ "F21,T22,P,TRADEAMT,37|"
                    	+ "F21,T02,P,TRADEAMT,37",原始},
            
            {小计指标2_1},
            {"投资支付的现金",小计指标2_2,
                    "F01,T01,P,TRADEAMT,02|" +
                    "F01,T29,P,TRADEAMT,29|" +
                    "F01,T32,P,TRADEAMT,29|" +
                    /**modify by wujie on 20200213 start
                    "F01,T36,P,TRADEAMT|" +
                    **/
                    "F01,T25,P,TRADEAMT,02|" +
                    "F01,T01,P,TRADEAMT,29|" +
                    /**modify by wujie on 20200213 end**/
                    "F06,T01,P,TRADEAMT|" +
                    "F06,T52,P,TRADEAMT|" +
                    "F06,T04,P,TRADEAMT|" +

                    "F17,T01,P,TRADEAMT,09|" +
                    "F17,T05,P,TRADEAMT,07|" +
                    /**add by wujie on 20200213 start**/
                    "F17,T01,P,TRADEAMT,07|" +
                    "F17,T25,P,TRADEAMT,09|" +
                    
					"F17,T05,P,TRADEAMT,06|" +
					"F17,T01,P,TRADEAMT,06|" +
					/**add by wujie on 20200213 end**/
                    
                    "F23,T07,P,TRADEAMT|" +
                    "F11,300,P,SELLTRADEAMT|" +
                    "F12,410,P,SELLTRADEAMT|" +
                    "F34,760,P,SELLTRADEAMT|" +
                    "F34,761,P,SELLTRADEAMT|" +

                    "F14,752,P,PAYINT|" +
                    "F34,751,P,PAYINT|" +

                    "F10,100,P,BUYTRADEAMT|" +
                    "F10,105,P,BUYTRADEAMT|" +
                    "F10,108,P,BUYTRADEAMT|" +
                    /** add by yanya on 20200222 增加期权**/
                    "F35,T01_T01,P,TRADEAMT|" +
                    "F35,T02_T01,N,TRADEAMT|"
                    ,
                    原始},
            {"支付的其他与投资活动有关的现金",小计指标2_2,
                    	"F01,T01,P,TRADEAMT,37|"
                    	+ "F17,T01,P,TRADEAMT,37|"
                    	+ "F07,T01,P,TRADEAMT,37|"
                    	+ "F21,T01,P,TRADEAMT,37",原始},
            
            {小计指标2_2},
            {小计指标2_3},

            //{"筹资活动产生的现金流量"},
            {汇总指标3},
            //{"募集理财资金收到的现金",
            {"吸收理财资金收到的现金",
                    小计指标3_1,
            	    "F16,C1,P,TRADEAMT|"
            	        + "F16,C2,P,TRADEAMT|"
            	        + "F16,C3,P,TRADEAMT",
                    原始},
//            {"借款收到的现金","筹资活动现金流入小计",
//            		"F08,T01,P,TRADEAMT","ORI"},
            {"收到的其他与筹资活动有关的现金",
                    小计指标3_1,
                    "F08,T01,P,TRADEAMT",
                    原始},
            {小计指标3_1},

            {"理财资金赎回支付的现金",小计指标3_2,
            	"F16,C4,P,TRADEAMT|"
            	+ "F16,C5,P,TRADEAMT|"
                +"F16,C17,P,TRADEAMT|"
                + "F16,C18,P,TRADEAMT|"
                + "F16,C20,P,TRADEAMT|"
                + "F16,C6,P,TRADEAMT",原始},
            //{"偿还债务支付的现金","筹资活动现金流出小计","F08,T02,P,TRADEAMT|F08,T07,P,TRADEAMT","ORI"},
            {"分配红利支付的现金",小计指标3_2,
                    "F16,C7,P,TRADEAMT|"
                            + "F16,C9,P,TRADEAMT|"
                            + "F16,C14,P,TRADEAMT|"
                            + "F16,C15,P,TRADEAMT",/**增加赎回收益确认**/
                    "ORI"},
            {"支付的其他与筹资活动有关的现金",
                    小计指标3_2,
                    "F08,T02,P,TRADEAMT|"
                            + "F08,T07,P,TRADEAMT",
                    原始},
            {小计指标3_2},
            {小计指标3_3},

//            {"加：年初现金及现金等价物余额"},
//            {"年末现金及现金等价物余额"},
//            {"现金及现金等价物净(减少)增加额"},
//            {"汇率变动对现金及现金等价物的影响"}
            {汇总指标4},
            {汇总指标5},
            {汇总指标6},
            {汇总指标7}
            /** modify by yanya on 20201221 根据最新的估值核算指引改造展示表列 end**/
    };

    /**
     * 查询现金流量表数据list
     *
     * @param pageParam
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    public List<CashFlowsStatement001Dto> selectList(Map<String, Object> pageParam) throws ParseException {
        changeMapData(pageParam);
        Map<String, CashFlowsStatement001Dto> dtoMap = new HashMap<String, CashFlowsStatement001Dto>();
        initData(dtoMap);
        // 报表维度为单账套
        if (DictCodeListExt2.批处理账套类型_账套.getValue().equals(String.valueOf(pageParam.get("reportDimen")))) {
            // 统计本期金额
            pageParam.put("vdate", pageParam.get("thisTermVdate"));
            pageParam.put("mdate", pageParam.get("thisTermMdate"));
            List<CashFlowsDto> thisTermData = cashFlowsStatement001Mapper.selectCashFlowsStatement(pageParam);
            // JX 衍生品类型
            List<CashFlowsDto> listThisDer = this.findDerDetail((String) pageParam.get("booksetId"),
                    (String) pageParam.get("thisTermVdate"),
                    (String) pageParam.get("thisTermMdate"));
            thisTermData.addAll(listThisDer);
            calValues(thisTermData, true, dtoMap, pageParam);

            /** modfify by yanya on 20201218 去除上期金额 start**/
//            // 统计上期金额
//            pageParam.put("vdate", pageParam.get("lastTermVdate"));
//            pageParam.put("mdate", pageParam.get("lastTermMdate"));
//            List<CashFlowsDto> lastTermData = cashFlowsStatement001Mapper.selectCashFlowsStatement(pageParam);
//            // JX 衍生品类型
//            List<CashFlowsDto> listLastDer = this.findDerDetail((String) pageParam.get("booksetId"),
//                    (String) pageParam.get("lastTermVdate"),
//                    (String) pageParam.get("lastTermMdate"));
//            lastTermData.addAll(listLastDer);
//            calValues(lastTermData, false, dtoMap, pageParam);
            /** modfify by yanya on 20201218 去除上期金额 end**/
        } else {
            // 报表维度为账套组
            List<SysBatchGrpPrdlink> list = valAssetBook001Service.findAllProduct(pageParam);
            pageParam.put("booksetId",
                    list.stream().map(SysBatchGrpPrdlink::getProductId).distinct().collect(Collectors.joining(",")));
            // 统计本期金额
            pageParam.put("vdate", pageParam.get("thisTermVdate"));
            pageParam.put("mdate", pageParam.get("thisTermMdate"));
            List<CashFlowsDto> thisTermData = cashFlowsStatement001Mapper.selectCashFlowsStatement(pageParam);
            /** 衍生品处理方法中以单账套为模式处理，故此处进行拆分，modify by yanya on 2021015 start **/
            String[] booksetIds = StringUtil.split((String)pageParam.get("booksetId").toString(), ",");
            if(booksetIds != null && booksetIds.length > 0) {
                for(String str : booksetIds) {
                    // JX 衍生品类型
//                List<CashFlowsDto> listThisDer = this.findDerDetail((String) pageParam.get("booksetId"),
//                        (String) pageParam.get("thisTermVdate"),
//                        (String) pageParam.get("thisTermMdate"));
                    List<CashFlowsDto> listThisDer = this.findDerDetail(str,
                            (String) pageParam.get("thisTermVdate"),
                            (String) pageParam.get("thisTermMdate"));
                    thisTermData.addAll(listThisDer);
                }
            }
            /** 衍生品处理方法中以单账套为模式处理，故此处进行拆分，modify by yanya on 2021015 end **/
            calValues(thisTermData, true, dtoMap, pageParam);

            /** modfify by yanya on 20201218 去除上期金额 start**/
//            pageParam.put("vdate", pageParam.get("lastTermVdate"));
//            pageParam.put("mdate", pageParam.get("lastTermMdate"));
//            List<CashFlowsDto> lastTermData = cashFlowsStatement001Mapper.selectCashFlowsStatement(pageParam);
//            // JX 衍生品类型
//            List<CashFlowsDto> listLastDer = this.findDerDetail((String) pageParam.get("booksetId"),
//                    (String) pageParam.get("lastTermVdate"),
//                    (String) pageParam.get("lastTermMdate"));
//            lastTermData.addAll(listLastDer);
//            calValues(lastTermData, false, dtoMap, pageParam);
            /** modfify by yanya on 20201218 去除上期金额 end**/
        }
        List<CashFlowsStatement001Dto> list = new ArrayList<CashFlowsStatement001Dto>();
        list.addAll(dtoMap.values());

        list.sort(new Comparator<CashFlowsStatement001Dto>() {

            @Override
            public int compare(CashFlowsStatement001Dto arg0, CashFlowsStatement001Dto arg1) {
                if (arg0.getSortNum() < arg1.getSortNum()) {
                    return -1;
                }
                return 1;
            }
        });
        // 账套组情况下,添加样式
        // if
        // (DictCodeListExt1.批处理账套类型_账套组.getValue().equals(String.valueOf(pageParam.get("reportDimen"))))
        // {
        // dealStyle(list);
        // }
        return list;
    }

//    /**
//     * 处理样式
//     *
//     * @param list
//     * @param style
//     * @param reportDimen
//     *            报表维度
//     * @see
//     * @since 1.0
//     */
//    public void dealStyle(List<CashFlowsStatement001Dto> list) {
//        for (CashFlowsStatement001Dto dto : list) {
//            if (StringUtils.isNotEmpty(dto.getProject()) && dto.getThisTermAmt() != null
//                    && dto.getLastTermAmt() != null) {
//                dto.setProject("<label style='color:blue;'>" + dto.getProject() + "</label>");
//            }
//        }
//    }

    /**
     * 查询现金流量表数据list
     *
     * @param pageParam
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    //public List<CashFlowsStatement001Dto> selectListp(Map<String, Object> pageParam, CashFlowsStatement001Dto dto)
    public List<CashFlowsStatement001Dto> selectListp(Map<String, Object> pageParam)
            throws ParseException {
        changeMapData(pageParam);
        Map<String, CashFlowsStatement001Dto> dtoMap = new HashMap<String, CashFlowsStatement001Dto>();
        List<CashFlowsStatement001Dto> list1 = new ArrayList<CashFlowsStatement001Dto>();
        // 全账套
        //List<String> accountList = Arrays.asList(dto.getBooksetId().split(","));
        List<String> accountList = Arrays.asList(pageParam.get("booksetIds").toString().split(","));
        // 项目名
        //String project = dto.getProject();
        String project = pageParam.get("project").toString();
        Map<String, Object> mapnew = new HashMap<String, Object>();
        for (String account : accountList) {
            initData(dtoMap);
            // 报表维度为账套组
            mapnew.clear();
            mapnew.put("booksetId", account);
            // 统计本期金额
            mapnew.put("vdate", pageParam.get("thisTermVdate"));
            mapnew.put("mdate", pageParam.get("thisTermMdate"));
            pageParam.put("booksetId", account);
            pageParam.put("vdate", pageParam.get("thisTermVdate"));
            pageParam.put("mdate", pageParam.get("thisTermMdate"));
            List<CashFlowsDto> thisTermData = cashFlowsStatement001Mapper.selectCashFlowsStatement(mapnew);
            /** modify by yanya on 20210115 衍生品的相关计算逻辑丢失修正 start**/
            List<CashFlowsDto> listThisDer = this.findDerDetail(account,
                    (String) pageParam.get("thisTermVdate"),
                    (String) pageParam.get("thisTermMdate"));
            thisTermData.addAll(listThisDer);
            /** modify by yanya on 20210115 衍生品的相关计算逻辑丢失修正 end**/

            calValues(thisTermData, true, dtoMap, pageParam);
//            // 统计上期金额
//            mapnew.put("vdate", pageParam.get("lastTermVdate"));
//            mapnew.put("mdate", pageParam.get("lastTermMdate"));
//            pageParam.put("vdate", pageParam.get("lastTermVdate"));
//            pageParam.put("mdate", pageParam.get("lastTermMdate"));
//            List<CashFlowsDto> lastTermData = cashFlowsStatement001Mapper.selectCashFlowsStatement(mapnew);
//            calValues(lastTermData, false, dtoMap, pageParam);

            List<CashFlowsStatement001Dto> list = new ArrayList<CashFlowsStatement001Dto>();
            list.addAll(dtoMap.values());

            // 找到项目对应的
            Map<String, CashFlowsStatement001Dto> map = Maps.uniqueIndex(list,
                    new Function<CashFlowsStatement001Dto, String>() {

                        @Override
                        public String apply(CashFlowsStatement001Dto t) {
                            return t.getProject();
                        }

                    });
            CashFlowsStatement001Dto tempDto = map.get(project);
            tempDto.setBooksetId(account);
            list1.add(tempDto);

        }
        return list1;
    }

    /**
     * 根据获取正负值方向，返回计算出来值
     * 与给定的方向一致则返回值，否则返回0
     * @param value
     * @param lgFlag
     * @return
     */
    private BigDecimal getCalcValue(BigDecimal value, String lgFlag) {
        // 项目计算小于0时展示
        if (取负.equals(lgFlag)) {
            if (BigDecimal.ZERO.compareTo(value) < 0) {
                // 项目值大于0，将项目值置为0
                value = BigDecimal.ZERO;
            } else {
                // 项目值小于0，显示绝对值
                value = value.abs();
            }

        }
        // 项目计算大于0时展示
        if (取正.equals(lgFlag)) {
            if (BigDecimal.ZERO.compareTo(value) > 0) {
                // 项目值小于0，将项目值置为0
                value = BigDecimal.ZERO;
            }
        }
        return value;
    }
    /**
     * 根据期内交易数据，计算报表数据集各项目值
     *
     * @param termData
     *            期内交易数据
     * @param thisTermFlag
     *            本期标志
     * @param dtoMap
     *            报表数据集
     * @param pageParam
     * @see
     * @since 1.0
     */
    private void calValues(List<CashFlowsDto> termData, boolean thisTermFlag,
                           Map<String, CashFlowsStatement001Dto> dtoMap, Map<String, Object> pageParam) {
        Map<String, BigDecimal> preMap = Maps.newHashMap();
        /** add by yanya on 20201221 增加计算前处理逻辑，因为之前的有的计算项进行合并展示，但其中需要判定正负值，所以提前先计算好 start**/
        for (int i = 0; i < CAL_RULE_PRE.length; i++) {
            String[] arr = CAL_RULE_PRE[i];
            if (arr.length < 3) {
                //长度小的不处理
                continue;
            }
            // 项目值
            BigDecimal value = BigDecimal.ZERO;
            //要统计的金融产品类型、交易类型集合
            String[] types = arr[1].split("\\|");
            for (String single : types) {
                value = value.add(calcByFinProdType(single, termData, pageParam, null));
            }
            value = this.getCalcValue(value, arr[2]);
            preMap.put(arr[0], value);
        }

        /** add by yanya on 20201221 增加计算前处理逻辑，因为之前的有的计算项进行合并展示，但其中需要判定正负值，所以提前先计算好 end**/
        for (int i = 0; i < CAL_RULE.length; i++) {
            String[] arr = CAL_RULE[i];

            if (arr.length == 1) {
                // 计算列，在后面计算，此处不处理
                continue;
            }
            // 项目,dtoMap的主键
            String key = arr[0];
            // 父项目
            String parentKey = arr[1];
            // 项目值
            BigDecimal value = BigDecimal.ZERO;
            // 要统计的金融产品类型、交易类型集合
            String[] finProdTypes = arr[2].split("\\|");
            // 显示正负值标志
            String lgFlag = arr[3];
            // 计算项目值
            for (String finProdType : finProdTypes) {
                value = value.add(calcByFinProdType(finProdType, termData, pageParam, preMap));
            }

//            // 项目计算小于0时展示
//            if ("LT".equals(lgFlag)) {
//                if (BigDecimal.ZERO.compareTo(value) < 0) {
//                    // 项目值大于0，将项目值置为0
//                    value = BigDecimal.ZERO;
//                } else {
//                    // 项目值小于0，显示绝对值
//                    value = value.abs();
//                }
//
//            }
//            // 项目计算大于0时展示
//            if ("GT".equals(lgFlag)) {
//                if (BigDecimal.ZERO.compareTo(value) > 0) {
//                    // 项目值小于0，将项目值置为0
//                    value = BigDecimal.ZERO;
//                }
//            }
            value = getCalcValue(value, lgFlag);

            if (thisTermFlag) {
                // 本期金额
                dtoMap.get(key).setThisTermAmt(value);

                // 更新父项目金额
                dtoMap.get(parentKey).setThisTermAmt(dtoMap.get(parentKey).getThisTermAmt().add(value));
            } else {
                // 上期金额
                dtoMap.get(key).setLastTermAmt(value);

                // 更新父项目金额
                dtoMap.get(parentKey).setLastTermAmt(dtoMap.get(parentKey).getLastTermAmt().add(value));
            }
        }

        // (1) 经营活动产生的现金流量净额 = 经营活动现金流入小计 - 经营活动现金流出小计
        String key1 = 小计指标1_3;
        String key1_1 = 小计指标1_1;
        String key1_2 = 小计指标1_2;
        dtoMap.get(key1)
                .setThisTermAmt(dtoMap.get(key1_1).getThisTermAmt().subtract(dtoMap.get(key1_2).getThisTermAmt()));
        dtoMap.get(key1)
                .setLastTermAmt(dtoMap.get(key1_1).getLastTermAmt().subtract(dtoMap.get(key1_2).getLastTermAmt()));

        // (2) 投资活动产生的现金流量净额 = 投资活动现金流入小计 - 投资活动现金流出小计
        String key2 = 小计指标2_3;
        String key2_1 = 小计指标2_1;
        String key2_2 = 小计指标2_2;
        dtoMap.get(key2)
                .setThisTermAmt(dtoMap.get(key2_1).getThisTermAmt().subtract(dtoMap.get(key2_2).getThisTermAmt()));
        dtoMap.get(key2)
                .setLastTermAmt(dtoMap.get(key2_1).getLastTermAmt().subtract(dtoMap.get(key2_2).getLastTermAmt()));

        // (3) 筹资活动产生的现金流量净额 = 筹资活动现金流入小计 - 筹资活动现金流出小计
        String key3 = 小计指标3_3;
        String key3_1 = 小计指标3_1;
        String key3_2 = 小计指标3_2;
        dtoMap.get(key3)
                .setThisTermAmt(dtoMap.get(key3_1).getThisTermAmt().subtract(dtoMap.get(key3_2).getThisTermAmt()));
        dtoMap.get(key3)
                .setLastTermAmt(dtoMap.get(key3_1).getLastTermAmt().subtract(dtoMap.get(key3_2).getLastTermAmt()));
        if (DictCodeListExt2.批处理账套类型_账套.getValue().equals(String.valueOf(pageParam.get("reportDimen")))) {
            if (thisTermFlag) {
                // 本期

                // (4) 加：年初现金及现金等价物余额
                //String key4 = "加：年初现金及现金等价物余额";
                String key4 = 汇总指标6;
                Map<String, Object> queryMap = new HashMap<String, Object>();
                /**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 start **/
                String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
                queryMap.put("accountEntryType",accountEntryType);
                /**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 end **/
                int thisYear = DateUtil.getYear(pageParam.get("thisTermMdate").toString());
                queryMap.put("mdate", thisYear + "-01-01");
                queryMap.put("booksetId", pageParam.get("booksetId"));
                BigDecimal year_start_cashamt = cashFlowsStatement001Mapper.selectCashAmt(queryMap);
                dtoMap.get(key4).setThisTermAmt(year_start_cashamt);

                // (5) 年末现金及现金等价物余额
                //String key5 = "年末现金及现金等价物余额";
                String key5 = 汇总指标7;
                queryMap.put("mdate", pageParam.get("thisTermMdate"));
                BigDecimal year_end_cashamt = cashFlowsStatement001Mapper.selectCashAmt(pageParam);
                dtoMap.get(key5).setThisTermAmt(year_end_cashamt);
            } else {
                // 上期

                // (4) 加：年初现金及现金等价物余额
                //String key4 = "加：年初现金及现金等价物余额";
                String key4 = 汇总指标6;
                Map<String, Object> queryMap = new HashMap<String, Object>();
                /**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 start **/
                String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
                queryMap.put("accountEntryType",accountEntryType);
                /**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 end **/
                int lastYear = DateUtil.getYear(pageParam.get("lastTermMdate").toString());
                queryMap.put("mdate", lastYear + "-01-01");
                queryMap.put("booksetId", pageParam.get("booksetId"));
                BigDecimal year_start_cashamt = cashFlowsStatement001Mapper.selectCashAmt(queryMap);
                dtoMap.get(key4).setLastTermAmt(year_start_cashamt);

                // (5) 年末现金及现金等价物余额
                //String key5 = "年末现金及现金等价物余额";
                String key5 = 汇总指标7;
                queryMap.put("mdate", pageParam.get("lastTermMdate"));
                BigDecimal year_end_cashamt = cashFlowsStatement001Mapper.selectCashAmt(pageParam);
                dtoMap.get(key5).setLastTermAmt(year_end_cashamt);
            }
        } else {
            if (thisTermFlag) {
                // 账套组
                // 本期
                // (4) 加：年初现金及现金等价物余额
                //String key4 = "加：年初现金及现金等价物余额";
                String key4 = 汇总指标6;
                Map<String, Object> queryMap = new HashMap<String, Object>();
                /**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 start **/
                String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
                queryMap.put("accountEntryType",accountEntryType);
                /**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 end **/
                int thisYear = DateUtil.getYear(pageParam.get("thisTermMdate").toString());
                queryMap.put("mdate", thisYear + "-01-01");
                queryMap.put("booksetId", pageParam.get("booksetId"));
                BigDecimal year_start_cashamt = cashFlowsStatement001Mapper.selectCashAmtp(queryMap);
                dtoMap.get(key4).setThisTermAmt(year_start_cashamt);

                // (5) 年末现金及现金等价物余额
                //String key5 = "年末现金及现金等价物余额";
                String key5 = 汇总指标7;
                queryMap.put("mdate", pageParam.get("thisTermMdate"));
                BigDecimal year_end_cashamt = cashFlowsStatement001Mapper.selectCashAmtp(pageParam);
                dtoMap.get(key5).setThisTermAmt(year_end_cashamt);
            } else {
                // 上期
                // (4) 加：年初现金及现金等价物余额
                //String key4 = "加：年初现金及现金等价物余额";
                String key4 = 汇总指标6;
                Map<String, Object> queryMap = new HashMap<String, Object>();
                /**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 start **/
                String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
                queryMap.put("accountEntryType",accountEntryType);
                /**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 end **/
                int lastYear = DateUtil.getYear(pageParam.get("lastTermMdate").toString());
                queryMap.put("mdate", lastYear + "-01-01");
                queryMap.put("booksetId", pageParam.get("booksetId"));
                BigDecimal year_start_cashamt = cashFlowsStatement001Mapper.selectCashAmtp(queryMap);
                dtoMap.get(key4).setLastTermAmt(year_start_cashamt);

                // (5) 年末现金及现金等价物余额
                //String key5 = "年末现金及现金等价物余额";
                String key5 = 汇总指标7;
                queryMap.put("mdate", pageParam.get("lastTermMdate"));
                BigDecimal year_end_cashamt = cashFlowsStatement001Mapper.selectCashAmtp(pageParam);
                dtoMap.get(key5).setLastTermAmt(year_end_cashamt);
            }
        }

        // (6) 现金及现金等价物净(减少)增加额 = 上(1)+上(2)+上(3)
        //String key6 = "现金及现金等价物净(减少)增加额";
        String key6 = 汇总指标5;
        dtoMap.get(key6).setThisTermAmt(dtoMap.get(key1).getThisTermAmt().add(dtoMap.get(key2).getThisTermAmt())
                .add(dtoMap.get(key3).getThisTermAmt()));
        dtoMap.get(key6).setLastTermAmt(dtoMap.get(key1).getLastTermAmt().add(dtoMap.get(key2).getLastTermAmt())
                .add(dtoMap.get(key3).getLastTermAmt()));

        // 汇率变动对现金及现金等价物的影响
        // Σ【统计期内买入外币金额*（期末汇率-资金交收时的交易汇率）】
        // +Σ【统计期内卖出外币金额*（资金交收时的交易汇率-期末汇率）】
        // +Σ【期初外币存量*（期末汇率-期初汇率）】
        List<CashFlowsDto> hlxj = termData.stream().filter(item -> "HLXJ".equals(item.getFinProdType())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(hlxj)) {
            //String key7 = "汇率变动对现金及现金等价物的影响";
            String key7 = 汇总指标4;
            if (thisTermFlag){
                dtoMap.get(key7).setThisTermAmt(hlxj.get(0).getTradeamt());
            }else {
                dtoMap.get(key7).setLastTermAmt(hlxj.get(0).getTradeamt());
            }
        }
    }

    /**
     * 根据金融产品类型及交易方向等信息,筛选交易记录并合计交易金额 <功能详细描述>
     *
     * @param finProdType
     *            金融产品类型及交易方向等信息
     * @param termData
     *            期内交易数据
     * @param pageParam
     *            画面传参
     * @param preMap  提前计算的值
     * @return 筛选交易金额合计
     * @see
     * @since 1.0
     */
    private BigDecimal calcByFinProdType(String finProdType, List<CashFlowsDto> termData,
                                         Map<String, Object> pageParam, Map<String, BigDecimal> preMap) {
        BigDecimal retValue = BigDecimal.ZERO;
        String[] rules = finProdType.split(",");
        //特殊处理 提前计算的值
        if(rules.length == 1) {
            if(preMap != null && preMap.containsKey(rules[0])) {
                return preMap.get(rules[0]);
            }
        }
        if (rules.length == 2 && 自定义现金流.equals(rules[0])) {
            for (CashFlowsDto dto : termData) {
                //交易方向相等
                if(StringUtil.equals(dto.getPs(), rules[1])) {
                    retValue = retValue.add(dto.getTradeamt());
                }
            }
        }
        // 手工凭证
        if (rules.length == 2 && 手工凭证.equals(rules[0])) {
            Map<String, Object> queryMap = new HashMap<String, Object>();
            /**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 start **/
            String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
            queryMap.put("accountEntryType",accountEntryType);
            /**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 end **/
            queryMap.put("booksetId", pageParam.get("booksetId"));
            queryMap.put("crdrFlg", rules[1]);
            queryMap.put("vdate", pageParam.get("vdate"));
            queryMap.put("mdate", pageParam.get("mdate"));
            BigDecimal subjectAmt = cashFlowsStatement001Mapper.selectSubjectAmount(queryMap);
            retValue = retValue.add(subjectAmt);
        }
        /**modify by wujie on 20200213 start
        if (rules.length == 4) {**/
        if (rules.length >= 4) {
        /**modify by wujie on 20200213 end **/
            // 金融产品类型
            String finType = rules[0];
            // 交易方向
            String ps = rules[1];
            // 金额方向
            BigDecimal direction = 正标识.equals(rules[2]) ? BigDecimal.ONE : BigDecimal.ONE.negate();
            // 金额类别
            String amtFlag = rules[3];
            /**add by wujie on 20200213 start**/
            //业务类型
            String busiType = null;

            /**add by yanya on 20200219 start**/
            //外汇交易方向
            //String excPs = null;
            if(rules.length>4){
            	busiType = rules[4];
            	//if(DictCodeListExt3.金融产品类型_期权.getValue().equals(finType)) {
                //excPs = rules[4];
                //}
            }
            /**add by wujie on 20200213 end**/

            for (CashFlowsDto dto : termData) {
                if (finType.equals(dto.getFinProdType()) && ps.equals(dto.getPs())
                		/**add by wujie on 20200213 start**/
                		&& (busiType == null || dto.getBusiType().equals(busiType)
                        /**add by yanya on 20200219 增加外汇交易方向 start**/
                            //|| StringUtil.equals(dto.getExcPs(), excPs)
                        )
                		/**add by wujie on 20200213 end**/
                		) {
                    //if ("PRINAMT".equals(amtFlag)) {
                    if (本金.equals(amtFlag)) {
                        retValue = retValue.add(dto.getPrinamt().multiply(direction));
                    }
                    //if ("INTAMT".equals(amtFlag)) {
                    if (利息.equals(amtFlag)) {
                        retValue = retValue.add(dto.getIntamt().multiply(direction));
                    }
                    //if ("TRADEAMT".equals(amtFlag)) {
                    if (交易金额.equals(amtFlag)) {
                        retValue = retValue.add(dto.getTradeamt().multiply(direction));
                    }
                    //if ("RECINT".equals(amtFlag)) {
                    if (收息额.equals(amtFlag)) {
                        // 收息额
                        retValue = retValue.add(dto.getRecInt().multiply(direction));
                    }
                    //if ("PAYINT".equals(amtFlag)) {
                    if (付息.equals(amtFlag)) {
                        // 付息
                        retValue = retValue.add(dto.getPayInt().multiply(direction));
                    }
                    //if ("BUYTRADEAMT".equals(amtFlag)) {
                    if (买入交易金额.equals(amtFlag)) {
                        // 买入交易金额
                        retValue = retValue.add(dto.getBuyTradeAmt().multiply(direction));
                    }
                    //if ("SELLTRADEAMT".equals(amtFlag)) {
                    if (卖出端交易金额.equals(amtFlag)) {
                        // 卖出端交易金额
                        retValue = retValue.add(dto.getSellTradeAmt().multiply(direction));
                    }
                }
            }
        }

        return retValue;
    }

    /**
     * 初始化数据
     *
     * @param dtoMap
     * @return
     * @see
     * @since 1.0
     */
    public void initData(Map<String, CashFlowsStatement001Dto> dtoMap) {
        CashFlowsStatement001Dto newDto = null;
        for (int i = 0; i < CAL_RULE.length; i++) {
            String[] arr = CAL_RULE[i];
            newDto = new CashFlowsStatement001Dto();
            newDto.setSortNum(i + 1);
            newDto.setProject(arr[0]);
            newDto.setThisTermAmt(BigDecimal.ZERO);
            newDto.setLastTermAmt(BigDecimal.ZERO);
            //if (arr.length == 1 && arr[0].endsWith("活动产生的现金流量")) {
            if (arr.length == 1 && arr[0].endsWith(汇总指标后缀)) {
                newDto.setThisTermAmt(null);
                newDto.setLastTermAmt(null);
            }
            dtoMap.put(arr[0], newDto);
        }
    }

    /**
     * 设置页面传回参数
     *
     * @param pageParam
     * @throws ParseException
     * @see
     * @since 1.0
     */
    public void changeMapData(Map<String, Object> pageParam) throws ParseException {
        String reportTermType = (String) pageParam.get("reportTermType");
        String valDate = DateUtil.format(analyseDate(pageParam), DateUtil.DEFAULT_DATE_FORMAT_EN);
        // 上期维度
        String lastDimen = (String) pageParam.get("lastDimen");
        int thisYear = DateUtil.getYear(valDate);
        int lastYear = thisYear - 1;
        String reportTerm = (String) pageParam.get("reportTerm"); // 期限明细
        Date now = DateUtil.parse(valDate);
        if (StringUtil.equals(reportTermType, DictCodeListExt1.报表期限类型_日报.getValue())) {
            pageParam.put("thisTermVdate", valDate);
            pageParam.put("thisTermMdate", valDate);
            if (DictCodeListExt1.上期维度_同比.getValue().equals(lastDimen)) {
                pageParam.put("lastTermVdate", DateUtil.format(DateUtil.addYear(now, -1)));
                pageParam.put("lastTermMdate", DateUtil.format(DateUtil.addYear(now, -1)));
            } else if (DictCodeListExt1.上期维度_环比.getValue().equals(lastDimen)) {
                pageParam.put("lastTermVdate", DateUtil.addDay(valDate, -1));
                pageParam.put("lastTermMdate", DateUtil.addDay(valDate, -1));
            }

        } else if (StringUtil.equals(reportTermType, DictCodeListExt1.报表期限类型_月报.getValue())) {
            pageParam.put("thisTermVdate",
                    DateUtil.format(DateUtil.getFirstDay(DateUtil.parse(valDate)), DateUtil.DEFAULT_DATE_FORMAT_EN));
            pageParam.put("thisTermMdate",
                    DateUtil.format(DateUtil.getLastDay(DateUtil.parse(valDate)), DateUtil.DEFAULT_DATE_FORMAT_EN));
            if (DictCodeListExt1.上期维度_同比.getValue().equals(lastDimen)) {
                // 查询上期开始日 = 画面.日期-1年的月初
                pageParam.put("lastTermVdate", DateUtil.format(DateUtil.getFirstDay(DateUtil.addYear(now, -1))));
                // 查询上期结束日 = 画面.日期-1年的月末
                pageParam.put("lastTermMdate", DateUtil.format(DateUtil.getLastDay(DateUtil.addYear(now, -1))));
            } else if (DictCodeListExt1.上期维度_环比.getValue().equals(lastDimen)) {
                // 查询上期开始日 = 画面.日期-1月的月初
                pageParam.put("lastTermVdate", DateUtil.format(DateUtil.getFirstDay(DateUtil.addMonthDate(now, -1))));
                // 查询上期结束日 = 画面.日期-1月的月末
                pageParam.put("lastTermMdate", DateUtil.format(DateUtil.getLastDay(DateUtil.addMonthDate(now, -1))));
            }
        } else if (StringUtil.equals(reportTermType, DictCodeListExt1.报表期限类型_季报.getValue())) {
            if (StringUtil.equals(reportTerm, DictCodeListExt1.季度_第一季度.getValue())) {
                pageParam.put("thisTermVdate", thisYear + "-01-01");
                pageParam.put("thisTermMdate", thisYear + "-03-31");
                if (DictCodeListExt1.上期维度_同比.getValue().equals(lastDimen)) {
                    pageParam.put("lastTermVdate", lastYear + "-01-01");
                    pageParam.put("lastTermMdate", lastYear + "-03-31");
                } else if (DictCodeListExt1.上期维度_环比.getValue().equals(lastDimen)) {
                    pageParam.put("lastTermVdate", lastYear + "-10-01");
                    pageParam.put("lastTermMdate", lastYear + "-12-31");
                }

            } else if (StringUtil.equals(reportTerm, DictCodeListExt1.季度_第二季度.getValue())) {
                pageParam.put("thisTermVdate", thisYear + "-04-01");
                pageParam.put("thisTermMdate", thisYear + "-06-30");
                if (DictCodeListExt1.上期维度_同比.getValue().equals(lastDimen)) {
                    pageParam.put("lastTermVdate", lastYear + "-04-01");
                    pageParam.put("lastTermMdate", lastYear + "-06-30");
                } else if (DictCodeListExt1.上期维度_环比.getValue().equals(lastDimen)) {
                    pageParam.put("lastTermVdate", thisYear + "-01-01");
                    pageParam.put("lastTermMdate", thisYear + "-03-31");
                }
            } else if (StringUtil.equals(reportTerm, DictCodeListExt1.季度_第三季度.getValue())) {
                pageParam.put("thisTermVdate", thisYear + "-07-01");
                pageParam.put("thisTermMdate", thisYear + "-09-30");
                if (DictCodeListExt1.上期维度_同比.getValue().equals(lastDimen)) {
                    pageParam.put("lastTermVdate", lastYear + "-07-01");
                    pageParam.put("lastTermMdate", lastYear + "-09-30");
                } else if (DictCodeListExt1.上期维度_环比.getValue().equals(lastDimen)) {
                    pageParam.put("lastTermVdate", thisYear + "-04-01");
                    pageParam.put("lastTermMdate", thisYear + "-06-30");
                }

            } else {
                pageParam.put("thisTermVdate", thisYear + "-10-01");
                pageParam.put("thisTermMdate", thisYear + "-12-31");
                if (DictCodeListExt1.上期维度_同比.getValue().equals(lastDimen)) {
                    pageParam.put("lastTermVdate", lastYear + "-10-01");
                    pageParam.put("lastTermMdate", lastYear + "-12-31");
                } else if (DictCodeListExt1.上期维度_环比.getValue().equals(lastDimen)) {
                    pageParam.put("lastTermVdate", thisYear + "-07-01");
                    pageParam.put("lastTermMdate", thisYear + "-09-30");
                }

            }
        } else if (StringUtil.equals(reportTermType, DictCodeListExt1.报表期限类型_半年报.getValue())) {
            if (StringUtil.equals(reportTerm, DictCodeListExt1.半年_上半年.getValue())) {
                pageParam.put("thisTermVdate", thisYear + "-01-01");
                pageParam.put("thisTermMdate", thisYear + "-06-30");
                if (DictCodeListExt1.上期维度_同比.getValue().equals(lastDimen)) {
                    pageParam.put("lastTermVdate", lastYear + "-01-01");
                    pageParam.put("lastTermMdate", lastYear + "-06-30");
                } else if (DictCodeListExt1.上期维度_环比.getValue().equals(lastDimen)) {
                    pageParam.put("lastTermVdate", lastYear + "-07-01");
                    pageParam.put("lastTermMdate", lastYear + "-12-31");
                }

            } else if (StringUtil.equals(reportTerm, DictCodeListExt1.半年_下半年.getValue())) {
                pageParam.put("thisTermVdate", thisYear + "-07-01");
                pageParam.put("thisTermMdate", thisYear + "-12-31");
                if (DictCodeListExt1.上期维度_同比.getValue().equals(lastDimen)) {
                    pageParam.put("lastTermVdate", lastYear + "-07-01");
                    pageParam.put("lastTermMdate", lastYear + "-12-31");
                } else if (DictCodeListExt1.上期维度_环比.getValue().equals(lastDimen)) {
                    pageParam.put("lastTermVdate", thisYear + "-01-01");
                    pageParam.put("lastTermMdate", thisYear + "-06-30");
                }
            }
        } else if (StringUtil.equals(reportTermType, DictCodeListExt1.报表期限类型_年报.getValue())) {
            pageParam.put("thisTermVdate", thisYear + "-01-01");
            pageParam.put("thisTermMdate", thisYear + "-12-31");
            pageParam.put("lastTermVdate", lastYear + "-01-01");
            pageParam.put("lastTermMdate", lastYear + "-12-31");
        }
    }

    /**
     * 分析余额日期
     *
     * @param pageParam
     *            查询条件
     * @param balanceType
     *            期末、年初
     * @return
     * @throws ParseException
     */
    public Date analyseDate(Map<String, Object> pageParam) throws ParseException {

        String reportTermType = (String) pageParam.get("reportTermType");
        String reportDate = (String) pageParam.get("reportDate");
        String reportTerm = (String) pageParam.get("reportTerm");

        int year = 0;
        int month = -1;
        int day = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);// 时
        calendar.set(Calendar.MINUTE, 0);// 分
        calendar.set(Calendar.SECOND, 0);// 秒
        calendar.set(Calendar.MILLISECOND, 0);// 毫秒

        String[] strArr = reportDate.split(",")[0].split("-");
        if (DictCodeListExt1.报表期限类型_年报.getValue().equals(reportTermType)) {// 年底
            year = Integer.valueOf(strArr[0]);
            month += 12;
            day = 31;
        }
        if (DictCodeListExt1.报表期限类型_半年报.getValue().equals(reportTermType)) {
            year = Integer.valueOf(strArr[0]);
            if (DictCodeListExt1.半年_上半年.getValue().equals(reportTerm)) {
                month += 6;
                day = 30;
            } else {
                month += 12;
                day = 31;
            }
        }
        if (DictCodeListExt1.报表期限类型_季报.getValue().equals(reportTermType)) {
            year = Integer.valueOf(strArr[0]);
            if (DictCodeListExt1.季度_第一季度.getValue().equals(reportTerm)) {
                month += 3;
                day = 31;
            } else if (DictCodeListExt1.季度_第二季度.getValue().equals(reportTerm)) {
                month += 6;
                day = 30;
            } else if (DictCodeListExt1.季度_第三季度.getValue().equals(reportTerm)) {
                month += 9;
                day = 30;
            } else {
                month += 12;
                day = 31;
            }
        }
        if (DictCodeListExt1.报表期限类型_月报.getValue().equals(reportTermType)) {// 月底：当前月的下个月的第0天
            year = Integer.valueOf(strArr[0]);
            month += Integer.valueOf(strArr[1]) + 1;
            day = 0;
        }
        if (DictCodeListExt1.报表期限类型_日报.getValue().equals(reportTermType)) {
            year = Integer.valueOf(strArr[0]);
            month += Integer.valueOf(strArr[1]);
            day = Integer.valueOf(strArr[2]);
        }

        calendar.set(year, month, day);
        return calendar.getTime();

    }

    /**
     * 通用联动查询
     *
     * @param param
     *            : dict_code_parent,dict_code_child,item_code_parent
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.OTHER)
    public List<SelectOptionDto> getLinkSelectOption(Map<String, Object> param) {
        return cashFlowsStatement001Mapper.getLinkSelectOption(param);
    }

    /**
     * 金融衍生品交易明细查询
     * @param booksetId
     * @param vdate
     * @param mdate
     * @return
     */
    private List<CashFlowsDto> findDerDetail(String booksetId, String vdate, String mdate) {
        // 1.定义变量
        // 定义 变量.交易明细List
        List<CashFlowsDto> listCashFlowsDto;

        Map<String, Object> param = Maps.newHashMap();
        param.put("booksetId", booksetId);
        param.put("vdate", vdate);
        param.put("mdate", mdate);
        // 查询交易明细信息
        listCashFlowsDto = cashFlowsStatement001Mapper.findDerDetail(param);

        if (CollectionUtils.isEmpty(listCashFlowsDto)){
            return Lists.newArrayList();
        }

        // 2.3 按照平均汇率结算对应的金额字段值
        // 定义 变量.汇率Map Map<KEY:币种+日期，汇率>
        Map<String, BigDecimal> mapRate = Maps.newHashMap();
        // 定义 变量.汇总信息DTO 汇率变动对现金及现金等价物的影响
        CashFlowsDto sumInfoDto = new CashFlowsDto();
        // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息查询(根据账套代码)SHEET
        Map<String, Object> mapBookInfo = interfCommonService.getBooksetInfoById(booksetId, InterfCommonConstant.账套信息查询_账套信息);
        // 变量.本位币 = DTO.get("BOOKSET").本位币
        BokBookset valBokBookset = (BokBookset)mapBookInfo.get(InterfCommonConstant.账套信息查询_账套信息);
        String bCcy = valBokBookset.getStandardCcy();

        // LOOP：变量.交易明细List -> 交易信息
        for (CashFlowsDto item : listCashFlowsDto) {
            // 定义 变量.交易汇率 = 交易信息.交易汇率
            BigDecimal fxrate = item.getFxrate();
            // IF 变量汇率Map.get(交易信息.交易币种+交易信息.结算日期) IS NULL
            if (mapRate.get(formatKey(item.getTradeCcy(), item.getSettleDate())) == null) {
                // 变量汇率Map.put(交易信息.交易币种+交易信息.结算日期，NULL)
                mapRate.put(formatKey(item.getTradeCcy(), item.getSettleDate()), null);
                // 调用共通设计->平均汇率计算SHEET
                findRate(mapRate, bCcy, item.getSettleDate(), booksetId);
            }
            // 变量.交易汇率 = 变量汇率Map.get(交易信息.交易币种+交易信息.结算日期)
            fxrate = mapRate.get(formatKey(item.getTradeCcy() , DateUtil.format(item.getSettleDate())));

            // 定义 变量.空方交易汇率 = 交易信息.空方交易汇率
            BigDecimal fxrate2 = item.getFxrate2();
            // IF 变量汇率Map.get(交易信息.卖出币种+交易信息.结算日期) IS NULL
            if (mapRate.get(formatKey(item.getSellCcy(), item.getSettleDate())) == null) {
                // 变量汇率Map.put(交易信息.卖出币种+交易信息.结算日期，NULL)
                mapRate.put(formatKey(item.getSellCcy(), item.getSettleDate()), null);
                // 调用共通设计->平均汇率计算SHEET
                findRate(mapRate, bCcy, item.getSettleDate(), booksetId);
            }
            // 变量.空方交易汇率 = 变量汇率Map.get(交易信息.卖出币种+交易信息.结算日期)
            fxrate2 = mapRate.get(formatKey(item.getSellCcy(), item.getSettleDate()));
            // 定义 变量.汇率变动金额 = 变量.汇总信息DTO.交易金额
            BigDecimal rateChangeAmt = sumInfoDto.getTradeamt();
            // IF 交易信息.交易币种 ！= 变量.本位币
            if (!StringUtils.equals(item.getTradeCcy(), bCcy)) {
                // 定义 变量.买入汇率变动 = 0
                BigDecimal buyRateChange = BigDecimal.ZERO;
                // IF 变量汇率Map.get(交易信息.交易币种+输入.查询结束日) IS NULL
                if (mapRate.get(formatKey(item.getTradeCcy(), mdate)) == null) {
                    // 变量汇率Map.put(交易信息.交易币种+输入.查询结束日，NULL)
                    mapRate.put(formatKey(item.getTradeCcy(), mdate), null);
                    // 调用共通设计->平均汇率计算SHEET
                    findRate(mapRate, bCcy, DateUtil.parse(mdate), booksetId);
                }
                // 变量.买入汇率变动 = 变量汇率Map.get(交易信息.交易币种+输入.查询结束日) - 变量汇率Map.get(交易信息.交易币种+交易信息.结算日期)
                buyRateChange = ArithmeticUtil.subtract(
                    mapRate.get(formatKey(item.getTradeCcy(), mdate)),
                    mapRate.get(formatKey(item.getTradeCcy(), item.getSettleDate())));
                // 变量.汇率变动金额 = 变量.汇率变动金额 + 交易信息.买入端交易金额 * 变量.买入汇率变动
                rateChangeAmt = ArithmeticUtil.doAdd(rateChangeAmt,
                    ArithmeticUtil.multiply(item.getBuyTradeAmt(), buyRateChange));
                // 变量.汇率变动金额 = 变量.汇率变动金额 + 交易信息.收息 * 变量.买入汇率变动
                rateChangeAmt = ArithmeticUtil.doAdd(rateChangeAmt,
                    ArithmeticUtil.multiply(item.getRecInt(), buyRateChange));
            }

            // IF 交易信息.卖出币种 ！= 变量.本位币
            if (!StringUtils.equals(item.getSellCcy(), bCcy)) {
                // 定义 变量.卖出汇率变动 = 0
                BigDecimal sellRateChange = BigDecimal.ZERO;
                // IF 变量汇率Map.get(交易信息.卖出币种+输入.查询结束日) IS NULL
                if (mapRate.get(formatKey(item.getSellCcy(), mdate)) == null) {
                    // 变量汇率Map.put(交易信息.卖出币种+输入.查询结束日，NULL)
                    mapRate.put(formatKey(item.getSellCcy(), mdate), null);
                    // 调用共通设计->平均汇率计算SHEET
                    findRate(mapRate, bCcy, DateUtil.parse(mdate), booksetId);
                }
                // 变量.卖出汇率变动 = 变量汇率Map.get(交易信息.卖出币种+交易信息.结算日期) - 变量汇率Map.get(交易信息.卖出币种+输入.查询结束日)
                sellRateChange = ArithmeticUtil.subtract(
                    mapRate.get(formatKey(item.getSellCcy(), item.getSettleDate())),
                    mapRate.get(formatKey(item.getSellCcy(), mdate)));
                // 变量.汇率变动金额 = 变量.汇率变动金额 + 交易信息.卖出端交易金额 * 变量.卖出汇率变动
                rateChangeAmt = ArithmeticUtil.doAdd(rateChangeAmt,
                    ArithmeticUtil.multiply(item.getSellTradeAmt(), sellRateChange));
                // 变量.汇率变动金额 = 变量.汇率变动金额 + 交易信息.付息 * 变量.卖出汇率变动
                rateChangeAmt = ArithmeticUtil.doAdd(rateChangeAmt,
                    ArithmeticUtil.multiply(item.getPayInt(), sellRateChange));
            }
            
            // 变量.汇总信息DTO.交易金额 = 变量.汇率变动金额
            sumInfoDto.setTradeamt(rateChangeAmt);

            // 交易信息.买入端交易金额 = 交易信息.买入端交易金额 * 变量.交易汇率 保留2位有效小数位
            item.setBuyTradeAmt(ArithmeticUtil.multiply(2, RoundingMode.HALF_UP,
                    item.getBuyTradeAmt(), fxrate));
            // 交易信息.收息 = 交易信息.收息 * 变量.交易汇率 保留2位有效小数位
            item.setRecInt(ArithmeticUtil.multiply(2, RoundingMode.HALF_UP,
                    item.getRecInt(), fxrate));

            // 交易信息.卖出端交易金额 = 交易信息.卖出端交易金额 * 变量.空方交易汇率 保留2位有效小数位
            item.setSellTradeAmt(ArithmeticUtil.multiply(2, RoundingMode.HALF_UP,
                    item.getSellTradeAmt(), fxrate2));
            // 交易信息.付息 = 交易信息.付息 * 变量.空方交易汇率 保留2位有效小数位
            item.setPayInt(ArithmeticUtil.multiply(2, RoundingMode.HALF_UP,
                    item.getPayInt(), fxrate2));
        }
        
        // 变量.汇总信息DTO.金融产品类型 = “HLXJ”
        sumInfoDto.setFinProdType("HLXJ");
        // 变量.汇总信息DTO.交易方向 = “SUM”
        sumInfoDto.setPs("SUM");

        // 2.4 期初外币存量
        // 定义 变量.期初外币存量Map Map<KEY:币种，Value：原币余额>
        Map<String, BigDecimal> initMap;
        // 定义 变量.基础科目性质List
        List<String> listBaseSubjectNature;

        // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->基础科目性质查询SHEET
        // 变量.基础科目性质List = DTO “XJL_KMCX” "QY"
        listBaseSubjectNature = accountingCommonBatchService.getValSubjectNatureListWithAcctStandardId(
        		DictCodeListNatureGroup.分组代码_现金类对应科目查询.getValue(),
                DictCodeListExt2.会计准则_企业准则.getValue());

        // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setHappenDate(DateUtil.addDay(DateUtil.parse(vdate), -1));
        searchDto.setBookDate(DateUtil.addDay(DateUtil.parse(vdate), -1));
        searchDto.setBooksetId(booksetId);
        searchDto.setBaseSubjectNatureList(listBaseSubjectNature);
        List<SubjectBalance> listSubjectBalance = accountingCommonBatchService.getSubjectBalanceExpectDirection(searchDto,
                DictCodeList.借贷方向_借.getValue());
        // 按原币币种进行分组，结果保存至 变量.期初外币存量Map
        initMap =  listSubjectBalance.stream().collect(Collectors.groupingBy(SubjectBalance::getOCcy,
                Collectors.collectingAndThen(Collectors.toList(), list->list.get(0).getBalance())));
        // LOOP 变量.期初外币存量Map.keySet() key
        for (String key : initMap.keySet()) {
            // IF key eq 变量.本位币
            if (key.equals(bCcy)) {
                continue;
            }

            // IF 变量汇率Map.get(key+输入.查询结束日) IS NULL
            if (mapRate.get(formatKey(key, mdate)) == null) {
                // 变量汇率Map.put(key+输入.查询结束日，NULL)
                mapRate.put(formatKey(key, mdate), null);
                // 调用共通设计->平均汇率计算SHEET
                findRate(mapRate, bCcy, DateUtil.parse(mdate), booksetId);
            }

            // IF 变量汇率Map.get(key+输入.查询开始日) IS NULL
            if (mapRate.get(formatKey(key, vdate)) == null) {
                // 变量汇率Map.put(key+输入.查询开始日，NULL)
                mapRate.put(formatKey(key, vdate), null);
                // 调用共通设计->平均汇率计算SHEET
                findRate(mapRate, bCcy, DateUtil.parse(vdate), booksetId);
            }
            // 变量.汇总信息DTO.交易金额 += 变量.期初外币存量Map.get(key) * （变量.汇率Map.get(key+输入.查询结束日) - 变量.汇率Map.get(key+输入.查询开始日))
//            BigDecimal temp =  ArithmeticUtil.multiply(initMap.get(key), mapRate.get(formatKey(key, mdate)));
//            temp = ArithmeticUtil.subtract(temp, mapRate.get(formatKey(key, vdate)));
            BigDecimal temp =  ArithmeticUtil.subtract(mapRate.get(formatKey(key, mdate)), mapRate.get(formatKey(key, vdate)));
            temp = ArithmeticUtil.multiply(initMap.get(key), temp);
            sumInfoDto.setTradeamt(ArithmeticUtil.doAdd(sumInfoDto.getTradeamt(), temp));
        }

        // 变量.交易明细List.add(变量.汇总信息DTO);
        listCashFlowsDto.add(sumInfoDto);

        return listCashFlowsDto;
    }

    /**
     * 格式化 map key
     * @param ccy
     * @param date
     * @return
     */
    private String formatKey(String ccy, Date date) {
        return formatKey(ccy, DateUtil.format(date));
    }

    /**
     * 格式化 map key
     * @param ccy
     * @param date
     * @return
     */
    private String formatKey(String ccy, String date) {
        return ccy + GlobalConstant.UNDER_LINE + date;
    }

    /**
     * 汇率查询
     * @param mapCcy
     * @param bccy
     * @param date
     * @param booksetId
     */
    private void findRate(Map<String, BigDecimal> mapCcy, String bccy, Date date, String booksetId) {

        // LOOP 输入.基础币种Map.keySet() key
        for (String key : mapCcy.keySet()) {
            String keyCcy = key.substring(0, key.indexOf(GlobalConstant.UNDER_LINE));
            // IF key == 输入.本位币
            if (keyCcy.equals(bccy)) {
                // 输入.基础币种Map.put(key,1)
                mapCcy.put(key, BigDecimal.ONE);
                continue;
            }

            // IF 输入.基础币种Map.get(key) is null or 输入.基础币种Map.get(key) ==0
            if (mapCcy.get(key) == null || mapCcy.get(key).equals(BigDecimal.ZERO)) {
                // 调用共通设计->【详细设计-计算类共通方法设计.xlsx】->汇率查询SHEET
                List<CurrencyMarketDto> currencyMarket = finProductCommonBatchService.getCurrencyMarket(date, booksetId, bccy, keyCcy);
                // 输入.基础币种Map.put(key,DTO)
                if (CollectionUtils.isNotEmpty(currencyMarket)) {
                    mapCcy.put(key, currencyMarket.get(0).getClosePrice());
                }
            }
        }
    }
    
    /**
     * 获取默认日期信息
     * 
     * @param pageParam
     * @return CashFlowsStatement001Dto
     */
    public CashFlowsStatement001Dto getDateInfo(Map<String, Object> pageParam) {
        String booksetId = String.valueOf(pageParam.get("booksetId"));
        String reportTermType = String.valueOf(pageParam.get("reportTermType"));
        CashFlowsStatement001Dto datedto = new CashFlowsStatement001Dto();
        if(booksetId != "null" && booksetId != ""){
            datedto = cashFlowsStatement001Mapper.getDateInfo1(pageParam);
        }else{
            List<CashFlowsStatement001Dto> dateList = cashFlowsStatement001Mapper.getDateInfo2(pageParam);
            for(CashFlowsStatement001Dto item:dateList){
                if(datedto.getReportDate() == null || datedto.getReportDate() == ""){
                    datedto.setReportDate(item.getReportDate());
                }else{
                    if(datedto.getReportDate().compareTo(item.getReportDate()) > 0){
                        datedto.setReportDate(item.getReportDate());
                    }
                }
            }
        }
        if(DictCodeListExt1.报表期限类型_日报.getValue().equals(reportTermType)){
            datedto.setReportDate(DateUtil.format(DateUtil.addDate(DateUtil.parse(datedto.getReportDate()), 0, 0, -1)));
//            datedto.setReportDate(DateUtil.addDate(datedto.getReportDate(), 0, 0, -1));
        }else if(DictCodeListExt1.报表期限类型_月报.getValue().equals(reportTermType)){
            datedto.setReportDate(DateUtil.format(DateUtil.addDate(DateUtil.parse(datedto.getReportDate()), 0, -1, 0), "yyyy-MM"));
        }else if(DictCodeListExt1.报表期限类型_季报.getValue().equals(reportTermType)){
            int season = DateUtil.getSeason(DateUtil.parse(datedto.getReportDate()));
            if(season == 1){
                datedto.setReportTerm(DictCodeListExt1.季度_第四季度.getValue());
                datedto.setReportDate(DateUtil.format(DateUtil.addDate(DateUtil.parse(datedto.getReportDate()), -1, 0, 0), "yyyy"));
            }else if(season == 2){
                datedto.setReportTerm(DictCodeListExt1.季度_第一季度.getValue());
                datedto.setReportDate(DateUtil.format(DateUtil.parse(datedto.getReportDate()), "yyyy"));
            }else if(season == 3){
                datedto.setReportTerm(DictCodeListExt1.季度_第二季度.getValue());
                datedto.setReportDate(DateUtil.format(DateUtil.parse(datedto.getReportDate()), "yyyy"));
            }else if(season == 4){
                datedto.setReportTerm(DictCodeListExt1.季度_第三季度.getValue());
                datedto.setReportDate(DateUtil.format(DateUtil.parse(datedto.getReportDate()), "yyyy"));
            }
        }else if(DictCodeListExt1.报表期限类型_半年报.getValue().equals(reportTermType)){
            int month = DateUtil.getMonth(datedto.getReportDate());
            if(month<=6){
                datedto.setReportTerm(DictCodeListExt1.半年_下半年.getValue());
                datedto.setReportDate(DateUtil.format(DateUtil.addDate(DateUtil.parse(datedto.getReportDate()), -1, 0, 0), "yyyy"));
            }else{
                datedto.setReportTerm(DictCodeListExt1.半年_上半年.getValue());
                datedto.setReportDate(DateUtil.format(DateUtil.parse(datedto.getReportDate()), "yyyy"));
            }
        }else if(DictCodeListExt1.报表期限类型_年报.getValue().equals(reportTermType)){
            datedto.setReportDate(DateUtil.format(DateUtil.addDate(DateUtil.parse(datedto.getReportDate()), -1, 0, 0), "yyyy"));
        }
        return datedto;
    }
    
}
