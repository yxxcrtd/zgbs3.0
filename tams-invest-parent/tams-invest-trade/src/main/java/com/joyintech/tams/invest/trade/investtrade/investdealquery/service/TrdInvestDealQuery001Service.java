/*
 * 文件名：TrdInvestDealQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangkang
 * 修改时间：2018年9月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.investtrade.investdealquery.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.file.SafelyFile;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.exportpdf.service.CommonExportPdfService;
import com.joyintech.tams.invest.trade.investtrade.investdealquery.dto.AssetTransferExport001Dto;
import com.joyintech.tams.invest.trade.investtrade.investdealquery.dto.TrdDealQueryDto;
import com.joyintech.tams.invest.trade.investtrade.investdealquery.dto.TrdInvestDealQuery001Dto;
import com.joyintech.tams.invest.trade.investtrade.investdealquery.dto.TrdInvestDealQueryPdfDto;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 交易查询service
 * 〈功能详细描述〉
 *
 * @author wangkang
 * @version 1.0 2018年9月29日
 * @see TrdInvestDealQuery001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdInvestDealQuery001Service extends CommonExportService<TrdInvestDealQuery001Dto> {


    private Logger logger = LoggerFactory.getLogger(TrdInvestDealQuery001Service.class);
    /**
     * add by zengchengfa 20210803 导出pdf共通
     */
    @Autowired
    private CommonExportPdfService commonExportPdfService;

    /**
     * 根据有效状态的不同的查询
     *
     * @param pageParam
     * @return
     * @throws Exception
     */
    public Page<TrdInvestDealQuery001Dto> selectForPage(Map<String, Object> pageParam) {
        //得到数据状态
        String validStatus = (String) pageParam.get("statusValid");
//		pageParam.put("prodPs", DictCodeList.交易方向_买入.getValue().substring(0, 1));
        pageParam.put("tradeStatus01", DictCodeListExt1.交易流程状态_流程中.getValue());
        pageParam.put("tradeStatus02", DictCodeListExt1.交易流程状态_有效.getValue());
        pageParam.put("tradeStatus03", DictCodeListExt1.交易流程状态_撤销.getValue());
//		String finprodType2 = (String)pageParam.get("finprodType2");
//		if(StringUtil.isNotEmpty(finprodType2)){
//            pageParam.put("finprodType2List", StringUtil.splitToList(GlobalConstant.COMMA,finprodType2));
//        }
        /**交易查询 通过角色岗位控制权限    20190814 add by qmh start*/
        pageParam.put("rolePos01", DictCodeListExt2.角色岗位_领导.getValue());
        pageParam.put("rolePos02", DictCodeListExt2.角色岗位_投资处长.getValue());
        pageParam.put("rolePos03", DictCodeListExt2.角色岗位_交易处长.getValue());
        pageParam.put("rolePos05", DictCodeListExt2.角色岗位_投资经理.getValue());
        pageParam.put("rolePos06", DictCodeListExt2.角色岗位_交易员.getValue());
        pageParam.put("loginUserName", LoginUserUtil.getUserName());
        /**交易查询 通过角色岗位控制权限    20190814 add by qmh end*/

        /**交易查询 当isCancel为空时  查询不出来    20190822 add by wangpeng start*/
        pageParam.put("YORN_N", DictCodeList.是否_否.getValue());
        /**交易查询 当isCancel为空时  查询不出来    20190822 add by wangpeng end*/

        if (StringUtil.isNotEmpty(pageParam.get("portfolioId"))) {
            pageParam.put("portfolioId", StringUtil.splitToList(GlobalConstant.COMMA, (String) pageParam.get("portfolioId")));
        }
        //勾选导出
        if (StringUtil.isNotEmpty(pageParam.get("selectedItem"))) {
            List<String> selectedItem = StringUtil.splitToList(",", (String) pageParam.get("selectedItem"));
            if (selectedItem.size() > 0) {
                pageParam.put("etridList", selectedItem);
            }
        }

        //多选下拉框处理（合作资产）
        Object chlFinprodIdObj = pageParam.get("chlFinprodId");
        if (chlFinprodIdObj != null) {
            pageParam.put("chlFinprodIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, (String) chlFinprodIdObj));
        } else {
            pageParam.put("chlFinprodIdList", new ArrayList<>());
        }

        //平安数据字典转为投管用字典
        String finprodType2Old = (String) pageParam.get("finprodType2");
        if (StringUtil.isNotEmpty(finprodType2Old) || StringUtil.isNotEmpty(pageParam.get("ps"))) {
            List<String> finprodType2ListOld = StringUtil.splitToList(GlobalConstant.COMMA, finprodType2Old);
            List<String> list1 = new ArrayList<>();
            List<String> list2 = new ArrayList<>();
            List<String> list3 = new ArrayList<>();
            for (String finprodType2 : finprodType2ListOld) {
//              //为不同资产类型赋查询条件
                if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                } else if (StringUtil.equals(DictCodeList.平安资产类型_基金.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
                } else if (StringUtil.equals(DictCodeList.平安资产类型_计划类资产_利率型.getValue(), finprodType2)) {
                    //交易方向选择的时候，查询条件资产类型赋值错误 yjm start 20190815
                    list3.add(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
                    //交易方向选择的时候，查询条件资产类型赋值错误 yjm end 20190815
                    list2.add(DictCodeList.收益类型_收益型.getValue());
                } else if (StringUtil.equals(DictCodeList.平安资产类型_计划类资产_净值型.getValue(), finprodType2)) {
                    //交易方向选择的时候，查询条件资产类型赋值错误 yjm start 20190815
                    list3.add(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
                    //交易方向选择的时候，查询条件资产类型赋值错误 yjm end 20190815
                    list2.add(DictCodeList.收益类型_净值型.getValue());
                } else if (StringUtil.equals(DictCodeList.平安资产类型_存款.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt1.金融产品类型_存款.getValue());
                } else if (StringUtil.equals(DictCodeList.平安资产类型_项目类资产.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
                } else if (StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                    list1.add(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                } else if (StringUtil.equals(DictCodeList.平安资产类型_拆借.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
                    list1.add(DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
                    list1.add(DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue());
                } else if (StringUtil.equals(DictCodeList.平安资产类型_买断式回购.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
                    list1.add(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
                } else if (StringUtil.equals(DictCodeList.平安资产类型_计划类资产_货币型.getValue(), finprodType2)) {
                    //交易方向选择的时候，查询条件资产类型赋值错误 yjm start 20190815
                    list3.add(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
                    //交易方向选择的时候，查询条件资产类型赋值错误 yjm end 20190815
                    list2.add(DictCodeList.收益类型_货币型.getValue());
                } else if (StringUtil.equals(DictCodeListExt2.平安资产类型_外汇即期.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt1.金融产品类型_投管用_即期.getValue());
                } else if (StringUtil.equals(DictCodeListExt2.平安资产类型_外汇远期.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt1.金融产品类型_投管用_远期.getValue());
                } else if (StringUtil.equals(DictCodeListExt2.平安资产类型_外汇掉期.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
                } else if (StringUtil.equals(DictCodeListExt2.平安资产类型_利率互换.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt1.金融产品类型_投管用_利率互换.getValue());
                } else if (StringUtil.equals(DictCodeListExt2.平安资产类型_债券借贷.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
                    list1.add(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
                } else if (StringUtil.equals(DictCodeListExt2.平安资产类型_股票.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
                } else if (StringUtil.equals(DictCodeListExt2.平安资产类型_一级市场债.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
                } else if (StringUtil.equals(DictCodeListExt2.平安资产类型_一级市场债.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
                } else if (StringUtil.equals(DictCodeListExt3.平安资产类型_期权.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt3.金融产品类型_投管用_期权.getValue());
                }
                // add by etliu 20210421 权益类资产相关代码合并 start
                else if (StringUtil.equals(DictCodeListExt3.平安资产类型_权益类资产.getValue(), finprodType2)) {
                    list1.add(DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue());
                }
                // add by etliu 20210421 权益类资产相关代码合并 end
				else if (StringUtil.equals(DictCodeList.平安资产类型_信用保护凭证.getValue(), finprodType2)) {
					list1.add(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
				}
            }
            //交易方向买入卖出
//            if( list1.isEmpty() ){
//                if( StringUtil.equals((String)pageParam.get("ps") , DictCodeList.交易方向_买入.getValue())
//                        ||StringUtil.equals((String)pageParam.get("ps") , DictCodeList.交易方向_卖出.getValue()) ){
//                    list1.add(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
//                    list1.add(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
//                    list1.add(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
//                }
//            }
            pageParam.put("finType_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
            pageParam.put("profitType", list2);
            pageParam.put("finprodType2List", list1);
            pageParam.put("list3", list3);
        }
        //利率互换交易方向  转换为真实交易方向
        if (StringUtil.equals((String) pageParam.get("ps"), DictCode.互换方式.getValue() + DictCodeListExt2.互换方式_收取固定.getValue())) {
            pageParam.put("ps", DictCodeListExt2.互换方式_收取固定.getValue());
            pageParam.put("IRSps", DictCodeListExt2.互换方式_收取固定.getValue());
        }
        if (StringUtil.equals((String) pageParam.get("ps"), DictCode.互换方式.getValue() + DictCodeListExt2.互换方式_收取浮动.getValue())) {
            pageParam.put("ps", DictCodeListExt2.互换方式_收取浮动.getValue());
            pageParam.put("IRSps", DictCodeListExt2.互换方式_收取浮动.getValue());
        }
        //add by wangpeng 201909019 交易方向传入买入时  外汇卖出 回购 拆借 债券借贷不应被查询出
        pageParam.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        pageParam.put("finType_F08", DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
        pageParam.put("finType_F09", DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
        pageParam.put("finType_F25", DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue());
        pageParam.put("finType_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        pageParam.put("finType_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        pageParam.put("finType_F04", DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
        pageParam.put("finType_F05", DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
        pageParam.put("finType_F29", DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
        pageParam.put("finType_F30", DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
        pageParam.put("IRSType_01", DictCodeListExt2.互换方式_收取固定.getValue());
        pageParam.put("IRSType_02", DictCodeListExt2.互换方式_收取浮动.getValue());
        //add by wangpeng end
        //add by wangpeng 201909019 外汇 利率互换 页面查询交易方向为真实交易方向  
        pageParam.put("finType_F11", DictCodeListExt1.金融产品类型_投管用_即期.getValue());
        pageParam.put("finType_F12", DictCodeListExt1.金融产品类型_投管用_远期.getValue());
        pageParam.put("finType_F13", DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
        pageParam.put("finType_F14", DictCodeListExt1.金融产品类型_投管用_利率互换.getValue());
        pageParam.put("COUNTER_TYPE_05", DictCodeListExt1.交易对手类型_内部交易对手.getValue());
        pageParam.put("YES_NO_Y", DictCodeList.是否_是.getValue());
        pageParam.put("YES_NO_N", DictCodeList.是否_否.getValue());
        pageParam.put("finType_F01", DictCodeListExt1.金融产品类型_债券.getValue());
        //add by wangpeng end
        pageParam.put("PS_T75", DictCodeListExt3.交易方向_清理核销.getValue());
        //判断状态
        Page<TrdInvestDealQuery001Dto> returnList = new Page<TrdInvestDealQuery001Dto>();
        if (StringUtil.isNotEmpty(validStatus)) {
            //流程中的查询
            if (DictCodeListExt1.画面数据状态_流程中.getValue().equals(validStatus)) {
                pageParam.put("tradeStatus", DictCodeListExt1.交易流程状态_流程中.getValue());
                returnList = restTemplate.postForPage(
                        ServiceApplication.TRADE_SERVICE_NAME + "/TrdInvestDealQuery001/findPage", pageParam,
                        TrdInvestDealQuery001Dto.class);
            } else if (DictCodeListExt1.画面数据状态_有效.getValue().equals(validStatus)) {
                pageParam.put("isCancel", DictCodeList.是否_否.getValue());
                pageParam.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
                returnList = restTemplate.postForPage(
                        ServiceApplication.TRADE_SERVICE_NAME + "/TrdInvestDealQuery001/validFindPage", pageParam,
                        TrdInvestDealQuery001Dto.class);
            } else if (DictCodeListExt1.画面数据状态_撤销.getValue().equals(validStatus)) {
                pageParam.put("isCancel", DictCodeList.是否_否.getValue());
                pageParam.put("tradeStatus", DictCodeListExt1.交易状态_撤销.getValue());
                returnList = restTemplate.postForPage(
                        ServiceApplication.TRADE_SERVICE_NAME + "/TrdInvestDealQuery001/cancelFindPage", pageParam,
                        TrdInvestDealQuery001Dto.class);
            }
        } else {
            pageParam.put("allStatus02", DictCodeListExt1.交易流程状态_流程中.getValue());
            pageParam.put("allStatus01", DictCodeListExt1.交易状态_初始.getValue());
            pageParam.put("allStatus03", DictCodeListExt1.交易状态_撤销.getValue());
            pageParam.put("isCancelN", DictCodeList.是否_否.getValue());
            pageParam.put("isCancelY", DictCodeList.是否_否.getValue());
            returnList = restTemplate.postForPage(
                    ServiceApplication.TRADE_SERVICE_NAME + "/TrdInvestDealQuery001/allFindPage", pageParam,
                    TrdInvestDealQuery001Dto.class);
        }
        if (returnList != null && returnList.getRoot() != null
                && returnList.getRoot().size() > 0) {
            for (TrdInvestDealQuery001Dto dto : returnList.getRoot()) {
                if (StringUtil.isNotEmpty(dto.getFinprodType2()) && dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue())) {
                    dto.setFinprodType2(DictCodeList.平安资产类型_拆借.getValue());
                }
                if (StringUtil.isNotEmpty(dto.getFinprodType2()) && dto.getFinprodType2().equals(DictCodeListExt3.金融产品类型_投管用_期权.getValue())) {
                    dto.setFinprodType2(DictCodeListExt3.平安资产类型_期权.getValue());
                }

                // add by caozhenming 20210730 start
                String finprodType2 = dto.getFinprodType2();

                // modify by etliu 20211111 start
                // TISHKS-331：需求中描述“交易价格”字段除债券二级交易和股票展示外，其他不展示
                if (DictCodeList.平安资产类型_债券.getValue().equals(finprodType2)
                        || DictCodeListExt2.平安资产类型_股票.getValue().equals(finprodType2)) {
                    // 交易价格
                    dto.setTradePrice(dto.getUnitCprice());
                }
                // modify by etliu 20211111 TISHKS-331 start

                // 利率
                if (DictCodeList.平安资产类型_存款.getValue().equals(finprodType2)
                        || DictCodeList.平安资产类型_计划类资产_利率型.getValue().equals(finprodType2)
                        || DictCodeList.平安资产类型_项目类资产.getValue().equals(finprodType2)) {
                    // 计提利率
                    dto.setRate(dto.getUnitInt());
                } else if (DictCodeList.平安资产类型_质押式回购.getValue().equals(finprodType2)
                        || DictCodeList.平安资产类型_买断式回购.getValue().equals(finprodType2)) {
                    // 回购利率
                    dto.setRate(dto.getUnitInt());
                } else if (DictCodeList.平安资产类型_债券.getValue().equals(finprodType2)) {
                    // 到期收益率或行权收益率
                    BigDecimal yield = dto.getYield();
                    if (Objects.nonNull(yield)) {
                        dto.setRate(yield);
                    } else {
                        dto.setRate(dto.getExerYield());
                    }
                }

                // add by caozhenming 20210730 end
            }
        }
        return returnList;
    }

    /**
     * 委托代码联想框
     *
     * @param entrustId
     * @return
     */
    public List<SuggestDto> getEntIds(String entrustId) {
        return restTemplate.getForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdInvestDealQuery001/getEntrustIds?entrustId=" + entrustId, SuggestDto.class);
    }

    /**
     * 组合代码的联想文本框
     *
     * @param portfolioId
     * @return
     */
    public List<SuggestDto> getPortfolioIds(String portfolioId) {
        return restTemplate.getForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdInvestDealQuery001/getPortfolioIds?portfolioId=" + portfolioId, SuggestDto.class);
    }

    /**
     * 资产代码的联想文本框
     *
     * @param finprodMarketId
     * @return
     */
    public List<SuggestDto> getFinprodMarketIds(String finprodMarketId) {
        return restTemplate.getForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdInvestDealQuery001/getFinprodMarketIds?finprodMarketId=" + finprodMarketId, SuggestDto.class);
    }

    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        // TODO Auto-generated method stub
        exportParam.config(
                "/com/joyintech/tams/invest/trade/investtrade/investdealquery/conf/export_conf.xml").dataSource(DataSourceType.MASERT);
    }

    /**
     *
     */
    @Override
    public Page<TrdInvestDealQuery001Dto> selectPageData(Map<String, Object> selectParam) {
        // TODO Auto-generated method stub
        return selectForPage(selectParam);
    }

    /**
     * <单笔导出转移定价审批表>
     * <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public ResponseEntity<byte[]> singleExport(TrdDealQueryDto dto) {
        Map<Object, Object> data = createData(dto);
        String dateStr = DateUtil.format(DateUtil.now(), DateUtil.DEFAULT_DATETIME_FORMAT_SSS);
        String termname = "转移定价审批表(" + dateStr + ")";
        data.put("termname", termname);
        return exportSingleExcel(data, "AssetTransferExport");
    }

    /**
     * <批量导出转移定价审批表>
     * <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public ResponseEntity<byte[]> batchExportExcel(TrdDealQueryDto dto) {
        Map<Object, Object> data = createData(dto);
        String dateStr = DateUtil.format(DateUtil.now(), DateUtil.DEFAULT_DATETIME_FORMAT_SSS);
        String termname = "转移定价审批表(" + dateStr + ")";
        data.put("termname", termname);
        return exportExcel(data, "AssetTransferExport");
    }


    /**
     * 获取数据
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public Map<Object, Object> createData(TrdDealQueryDto dto) {
        Map<Object, Object> result = new HashMap<Object, Object>();
        //债券类交易
        List<String> bondList = new ArrayList<>();
        //存款类交易
        List<String> depositList = new ArrayList<>();
        //计划类利率型交易
        List<String> planLlList = new ArrayList<>();
        //计划类净值型交易
        List<String> planJzList = new ArrayList<>();
        //计划类货币型交易
        List<String> planHbList = new ArrayList<>();
        //基金类交易
        List<String> fundList = new ArrayList<>();
        //项目类交易
        List<String> projectList = new ArrayList<>();
        for (TrdDealQueryDto queryDto : dto.getSelectedItem()) {
            if (StringUtil.equals(queryDto.getFinprodType2(), DictCodeList.平安资产类型_债券.getValue())) {
                bondList.add(queryDto.getTradeId());
            } else if (StringUtil.equals(queryDto.getFinprodType2(), DictCodeList.平安资产类型_存款.getValue())) {
                depositList.add(queryDto.getTradeId());
            } else if (StringUtil.equals(queryDto.getFinprodType2(), DictCodeList.平安资产类型_计划类资产_净值型.getValue())) {
                planJzList.add(queryDto.getTradeId());
            } else if (StringUtil.equals(queryDto.getFinprodType2(), DictCodeList.平安资产类型_计划类资产_利率型.getValue())) {
                planLlList.add(queryDto.getTradeId());
            } else if (StringUtil.equals(queryDto.getFinprodType2(), DictCodeList.平安资产类型_计划类资产_货币型.getValue())) {
                planHbList.add(queryDto.getTradeId());
            } else if (StringUtil.equals(queryDto.getFinprodType2(), DictCodeList.平安资产类型_基金.getValue())) {
                fundList.add(queryDto.getTradeId());
            } else if (StringUtil.equals(queryDto.getFinprodType2(), DictCodeList.平安资产类型_项目类资产.getValue())) {
                projectList.add(queryDto.getTradeId());
            }
        }
        List<AssetTransferExport001Dto> daoList = new ArrayList<>();
        /*modify by liupeilin 2020-01-17 begin*/
        if (!StringUtil.isEmptyList(depositList)) {
            List<AssetTransferExport001Dto> cklist = this.selectDataByDeposit(depositList);
            for (AssetTransferExport001Dto assetTransferExport001Dto : cklist) {
                //净投资收益率
                BigDecimal investRate = this.selectInvestRateByProject(assetTransferExport001Dto);
                assetTransferExport001Dto.setYield(StringUtil.isNotEmpty(investRate) ? investRate.toString() : "0");
            }
            daoList.addAll(cklist);
        }
        /*modify by liupeilin 2020-01-17 begin*/
        if (!StringUtil.isEmptyList(bondList)) {
            daoList.addAll(this.selectDataByBond(bondList));
        }
        if (!StringUtil.isEmptyList(depositList)) {
            daoList.addAll(this.selectDataByDeposit(depositList));
        }
        if (!StringUtil.isEmptyList(planLlList)) {
            List<AssetTransferExport001Dto> llList = this.selectDataByPlanLl(planLlList);
            for (AssetTransferExport001Dto assetTransferExport001Dto : llList) {
                //净投资收益率
                BigDecimal investRate = this.selectInvestRateByPlan(assetTransferExport001Dto);
                assetTransferExport001Dto.setYield(StringUtil.isNotEmpty(investRate) ? investRate.toString() : "0");
            }
            daoList.addAll(llList);
        }
        if (!StringUtil.isEmptyList(planJzList)) {
            daoList.addAll(this.selectDataByPlanJz(planJzList));
        }
        if (!StringUtil.isEmptyList(planHbList)) {
            daoList.addAll(this.selectDataByPlanHb(planHbList));
        }
        if (!StringUtil.isEmptyList(fundList)) {
            daoList.addAll(this.selectDataByFund(fundList));
        }
        if (!StringUtil.isEmptyList(projectList)) {
            List<AssetTransferExport001Dto> proList = this.selectDataByProject(projectList);
            for (AssetTransferExport001Dto assetTransferExport001Dto : proList) {
                //净投资收益率
                BigDecimal investRate = this.selectInvestRateByProject(assetTransferExport001Dto);
                assetTransferExport001Dto.setYield(StringUtil.isNotEmpty(investRate) ? investRate.toString() : "0");
            }
            daoList.addAll(proList);
        }
        List<Map<String, Object>> lists = getColumnMap(daoList);
        result.put("lists", lists);
        return result;
    }

    /**
     * 获取excel列表数据
     *
     * @param daoList
     * @return
     * @see
     * @since 1.0
     */
    public List<Map<String, Object>> getColumnMap(List<AssetTransferExport001Dto> daoList) {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (AssetTransferExport001Dto dto : daoList) {
            Map<String, Object> info = new HashMap<String, Object>();
            //转出资产理财产品名称
            info.put("inProdName", dto.getInProdName());
            //转入资产理财产品名称
            info.put("outprodName", dto.getOutprodName());
            //转移资产名称
            info.put("assetName", dto.getAssetName());
            //转移资产代码
            info.put("assetId", dto.getAssetId());
            //转移日期
            info.put("tradeDate", dto.getTradeDate());
            //转移净价
            info.put("unitCprice", StringUtil.isNotEmpty(dto.getUnitCprice()) ?
                    new BigDecimal(dto.getUnitCprice()).setScale(4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            //转移全价
            info.put("unitFprice", StringUtil.isNotEmpty(dto.getUnitFprice()) ?
                    new BigDecimal(dto.getUnitFprice()).setScale(4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            //转移面额（万元）
            info.put("tradeAmt", StringUtil.isNotEmpty(dto.getTradeAmt()) ?
                    new BigDecimal(dto.getTradeAmt()).setScale(4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            //转移金额（元）
            info.put("fpriceAmt", StringUtil.isNotEmpty(dto.getFpriceAmt()) ?
                    new BigDecimal(dto.getFpriceAmt()).setScale(4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            //利率/到期收益率
            info.put("yield", StringUtil.isNotEmpty(dto.getYield()) ?
                    new BigDecimal(dto.getYield()).setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            lists.add(info);
        }
        return lists;
    }

    /**
     * 生成excel方法
     *
     * @param data
     * @param exceltemplate
     * @return
     * @see
     * @since 1.0
     */
    public ResponseEntity<byte[]> exportExcel(Map<Object, Object> data,
                                              String exceltemplate) {
        String termname = data.get("termname").toString();
        String flieName = termname + ".xls";
        File depositfile = createFile(data, exceltemplate);
        return ExportUtil.export(flieName, depositfile, true);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param map
     * @param exceltemplate
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public ResponseEntity<byte[]> exportSingleExcel(Map<Object, Object> map,
                                                    String exceltemplate) {
        String termname = map.get("termname").toString();
        List<Map<String, String>> mapList = (List<Map<String, String>>) map.get("lists");
        List<Map<String, String>> lists = new ArrayList<>();
        Map<Object, Object> data = new HashMap<>();
        List<File> fileList = new ArrayList<>();
        for (Map<String, String> mapto : mapList) {
            lists.add(mapto);
            data.put("lists", lists);
            data.put("termname", mapto.get("outprodName") + DateUtil.format(DateUtil.now(), DateUtil.DEFAULT_DATETIME_FORMAT_SSS));
            File depositfile = createFile(data, exceltemplate);
            fileList.add(depositfile);
            lists.clear();
            data.clear();
        }
        return this.exportZip(fileList, termname + ".zip");
    }

    /**
     * 创建file
     *
     * @param data
     * @param exceltemplate
     * @return
     * @see
     * @since 1.0
     */
    public File createFile(Map<Object, Object> data, String exceltemplate) {
        String termname = data.get("termname").toString();
        String flieName = termname + ".xls";
        File depositfile = null;
        OutputStream os = null;
        InputStream stream = null;
        FileOutputStream fos = null;
        try {
            // 关联模板
            XLSTransformer transformer = new XLSTransformer();
            // 传放地址
            String depositfilePath = CacheUtil.getSysParam(
                    GlobalConstant.COM_PROP_ACCESS_EXPORTPATH);
            depositfile = new SafelyFile(depositfilePath + File.separator + flieName);
            if (!depositfile.getParentFile().exists()) {
                depositfile.getParentFile().mkdirs();
            }
            String templateFile = exceltemplate + ".xls";
            // 模板输入流
            stream = CommonExportService.class.getResourceAsStream(
                    "/tpl/" + templateFile);
            Workbook workbook = transformer.transformXLS(stream, data);
            fos = new FileOutputStream(depositfilePath + File.separator + flieName);
            os = new BufferedOutputStream(fos);
            workbook.write(os);
        } catch (Exception e) {
            logger.info(exceltemplate + "生成失败！");
            throw new BusinessException(e);
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
                if (stream != null) {
                    stream.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return depositfile;
    }

    /**
     * 单笔导出
     *
     * @param list     需要导出的列
     * @param fileName 文件名
     * @return 文件
     * @see
     * @since 1.0
     */
    @SuppressWarnings("all")
    public ResponseEntity<byte[]> exportZip(List<File> files, String fileName) {
        String exportFilePath = CacheUtil.getSysParam(GlobalConstant.COM_PROP_ACCESS_EXPORTPATH);
        File tmpDir = new SafelyFile(
                exportFilePath + File.separator + fileName.substring(0, fileName.length() - 4));
        if (tmpDir.exists()) {
            tmpDir.delete();
        }
        tmpDir.mkdirs();
        ZipOutputStream zipOut = null;
        FileOutputStream fos = null;
        InputStream input = null;
        try {
            File zipFile = new SafelyFile(exportFilePath + File.separator + fileName);
            fos = new FileOutputStream(zipFile);
            zipOut = new ZipOutputStream(fos);
            if (files != null && !files.isEmpty()) {
                for (File realFile : files) {
                    if (realFile.exists()) {
                        input = new FileInputStream(realFile);
                        zipOut.putNextEntry(
                                new ZipEntry(tmpDir.getName() + File.separator + realFile.getName()));
                        int tmp = 0;
                        while ((tmp = input.read()) != -1) {
                            zipOut.write(tmp);
                        }
                        input.close();
                    }
                }
            }
            zipOut.flush();
            zipOut.close();
            fos.close();
            HttpHeaders headers = new HttpHeaders();
            fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(
                    FileUtils.readFileToByteArray(zipFile), headers, HttpStatus.CREATED);
            zipFile.delete();
            return responseEntity;
        } catch (Exception e) {
            logger.info(fileName + "zip生成失败！");
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (zipOut != null) {
                    zipOut.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
//		return null;
    }

    /**
     * check是否为转仓交易
     *
     * @param teadIds
     * @return
     * @see
     * @since 1.0
     */
    public int selectCheck(List<String> teadIds) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdInvestDealQuery001/selectCheck", teadIds, int.class);
    }

    /**
     * 债券类交易
     *
     * @param teadIds
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByBond(List<String> teadIds) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdInvestDealQuery001/selectDataByBond", teadIds, AssetTransferExport001Dto.class);

    }

    /**
     * 存款类交易
     *
     * @param teadIds
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByDeposit(List<String> teadIds) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdInvestDealQuery001/selectDataByDeposit", teadIds, AssetTransferExport001Dto.class);
    }

    /**
     * 计划类利率型交易
     *
     * @param teadIds
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByPlanLl(List<String> teadIds) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdInvestDealQuery001/selectDataByPlanLl", teadIds, AssetTransferExport001Dto.class);
    }

    /**
     * 计划类净值型交易
     *
     * @param teadIds
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByPlanJz(List<String> teadIds) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdInvestDealQuery001/selectDataByPlanJz", teadIds, AssetTransferExport001Dto.class);
    }

    /**
     * 计划类货币型交易
     *
     * @param teadIds
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByPlanHb(List<String> teadIds) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdInvestDealQuery001/selectDataByPlanHb", teadIds, AssetTransferExport001Dto.class);
    }

    /**
     * 基金类交易
     *
     * @param teadIds
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByFund(List<String> teadIds) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdInvestDealQuery001/selectDataByFund", teadIds, AssetTransferExport001Dto.class);
    }

    /**
     * 项目类交易
     *
     * @param teadIds
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByProject(List<String> teadIds) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdInvestDealQuery001/selectDataByProject", teadIds, AssetTransferExport001Dto.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal selectInvestRateByPlan(AssetTransferExport001Dto dto) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdInvestDealQuery001/selectInvestRateByPlan", dto, BigDecimal.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal selectInvestRateByProject(AssetTransferExport001Dto dto) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdInvestDealQuery001/selectInvestRateByProject", dto, BigDecimal.class);
    }

    /**
     * 导出pdf数据 add by zengchengfa 20210803
     *
     * @param param
     * @return
     */
    public List<TrdInvestDealQueryPdfDto> selectExportPdfList(Map param) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdInvestDealQuery001/selectExportPdfList",
                param, TrdInvestDealQueryPdfDto.class);
    }

    /**
     * 导出pdf add by zengchengfa 20210803
     *
     * @param pageId 画面id
     * @param param  参数
     * @return
     */
    public ResponseEntity<byte[]> exportPdf(String pageId, Map param) {
        List<TrdInvestDealQueryPdfDto> datas = selectExportPdfList(param);
        DecimalFormat df2 = new DecimalFormat("#0.00##");
        datas.forEach(exportPdfDto -> {
            // 券面总额/交易金额
            if (StringUtil.isNotEmpty(exportPdfDto.getTradeAmt())) {
                exportPdfDto.setTradeAmt(new BigDecimal(df2.format(exportPdfDto.getTradeAmt()
                        .setScale(2, BigDecimal.ROUND_HALF_UP))));
            }
            // 交易价格
            if (StringUtil.isNotEmpty(exportPdfDto.getUnitCprice())) {
                exportPdfDto.setUnitCprice(new BigDecimal(df2.format(exportPdfDto.getUnitCprice()
                        .setScale(2, BigDecimal.ROUND_HALF_UP))));
            }
        });
        //汇总交易金额
        BigDecimal sumAmt = datas.stream().map(TrdInvestDealQueryPdfDto::getTradeAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        TrdInvestDealQueryPdfDto dto = new TrdInvestDealQueryPdfDto();
        dto.setPortfolioId("合计");
        dto.setTradeAmt(sumAmt);
        datas.add(dto);
        return commonExportPdfService.exportPdf(pageId, getConfigFile(pageId), datas);
    }

    /**
     * 获取配置文件绝对路径 add by zengchengfa 20210803
     *
     * @return
     */
    public String getConfigFile(String pageId) {
        String configFilePath = "";
        String basePath = "com/joyintech/tams/invest/trade/investtrade/investdealquery/conf/";
        String tempPath = CacheUtil.getSysParam("access_exportfilepath");
        String tempConfigFilePath = "";
        configFilePath = basePath + "export_conf_pdf.xml";
        tempConfigFilePath = tempPath + "/" + pageId + '_' + "export_conf_pdf.xml";
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(configFilePath);
        // 在临时目录下生成配置文件
        commonExportPdfService.createTempConfigFile(in, tempPath, tempConfigFilePath);
        return tempConfigFilePath;
    }
}
