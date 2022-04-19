package com.joyintech.tams.invest.portfolio.ptlmock.service;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.invest.portfolio.ptlmock.dto.PtlMockTestTradeDto;
import com.joyintech.tams.systemtools.util.SysDatadictUtil;
import com.joyintech.tools.collectors.JoyinCollectors;

/**
 * 模拟试算导入处理
 *
 * @author yjm
 * @version 1.0 2020/04/16
 * @see PtlMockTestImportService
 * @since 1.0
 */
@Service
public class PtlMockTestImportService extends BaseService<ITableRow> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(PtlMockTestImportService.class);
    
    @Autowired
    private PtlMockTest002Service ptlMockTest002Service;

    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 交易导入
     *
     * @param fileName
     * @return
     */
    public List<PtlMockTestTradeDto> importData(String fileName) {
        List<List<String>> dataList = null;
        List<PtlMockTestTradeDto> ptlMockTestTradeDtoList = null;
        try {
            dataList = ExcelUtil.readToList(fileName, null, null, 0, 1, 0);
        }
        catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
        }
        if (StringUtil.isNotEmpty(dataList)) {
            ptlMockTestTradeDtoList = new ArrayList<>(dataList.size());
            convertToEntity(dataList, ptlMockTestTradeDtoList);
        }
        return ptlMockTestTradeDtoList;
    }

    /**
     * 校验交易导入
     *
     * @param fileName
     * @return
     */
    public List<ExcelValidDto> checkForImportFile(String fileName) {
        List<List<String>> dataList = null;
        List<ExcelValidDto> checkResult = null;
        try {
            dataList = ExcelUtil.readToList(fileName, null, null, 0, 1, 0);
        }
        catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
        }

        if (StringUtil.isNotEmpty(dataList)) {
            List<PtlMockTestTradeDto> ptlMockTestTradeDtoList = new ArrayList<>(dataList.size());
            checkResult = convertToEntity(dataList, ptlMockTestTradeDtoList);
        }
        return checkResult;
    }

    /**
     * 导入交易转换DTO
     *
     * @param dataList
     * @param ptlMockTestTradeDtoList
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<ExcelValidDto> convertToEntity(List<List<String>> dataList,
                                                List<PtlMockTestTradeDto> ptlMockTestTradeDtoList) {
        // 校验结果
        List<ExcelValidDto> checkResult = new ArrayList<>();
        
        if (StringUtil.isEmptyList(dataList)) {
            checkResult.add(new ExcelValidDto(StringUtil.EMPTY, StringUtil.EMPTY,
                getMessage("PtlMockTest_E_0024")));
            return checkResult;
        }

        // 查询所有名称
        Map<String, Object> param = new HashMap<>();
        param.put("searchAllPtl", DictCodeList.是否_是.getValue());
        param.put("loginUserName", LoginUserUtil.getUserName());
        List<SelectOptionDto> ptlInfoList = ptlMockTest002Service.getPtlInfo(param);
        Map<String, SelectOptionDto> ptlInfoMap = ptlInfoList.stream().collect(
            JoyinCollectors.toMap(x -> x.getId(), x -> x));
        
        // 查询资产信息
        List<SelectOptionDto> assetInfoList = ptlMockTest002Service.getFinProdId(param);
        Map<String, SelectOptionDto> assetInfoMap = assetInfoList.stream().collect(
            JoyinCollectors.toMap(x -> ((Map<String, String>) JSONObject.parseObject(
                JSONObject.toJSONString(x.getObject()),
                Map.class)).get("FINPROD_MARKET_ID"), x -> x));
        
        for (int i = 0; i < dataList.size(); i++ ) {
            PtlMockTestTradeDto dto = new PtlMockTestTradeDto();
            // 验证消息
            List<String> messageList = new ArrayList<>();

            List<String> rowData = dataList.get(i);
            //组合代码不能为空
            this.checkIsBlank(rowData.get(1), "组合代码", messageList);
            if (StringUtil.isNotBlank(rowData.get(1))) {
                if (!ptlInfoMap.containsKey(rowData.get(1))) {
                    messageList.add(getMessage("PtlMockTest_E_0030", "组合代码"+rowData.get(1)));
                }
                else {
                    dto.setPortfolioName(((LinkedHashMap) ptlInfoMap.get(rowData.get(1)).getObject()).get("PORTFOLIO_NAME").toString());
                }
            }
            
            if (StringUtil.isNotEmpty(rowData.get(2))) {
                if (!assetInfoMap.containsKey(rowData.get(2))) {
                    messageList.add(getMessage("PtlMockTest_E_0026", "资产代码"));
                }
                else {
                    if(StringUtil.isNotBlank(rowData.get(3))&&(StringUtil.equals(DictCodeList.平安资产类型_债券.getCodeName(), rowData.get(3))
                        ||StringUtil.equals("项目类资产", rowData.get(3)))) {
                        if (StringUtil.isNotBlank(rowData.get(3))
                                && !StringUtil.equals(((LinkedHashMap) assetInfoMap.get(rowData.get(2)).getObject()).get("TYPE_8"),
                                SysDatadictUtil.getCodeFromDataDictByName(DictCode.平安资产类型, rowData.get(3)))) {
                            messageList.add(getMessage("PtlMockTest_E_0031", "资产代码" + rowData.get(2), rowData.get(3)));
                        } else {
                            Map map  = (Map) assetInfoMap.get(rowData.get(2)).getObject();
                            dto.setFinprodId(map.get("finprodId").toString());
                            dto.setFinprodAbbr(assetInfoMap.get(rowData.get(2)).getText());
                        }
                    }
                }
            }

            // 资产类型不能为空
            this.checkIsBlank(rowData.get(3), "资产类型", messageList);
            // 校验资产类型
            if (StringUtil.isNotBlank(rowData.get(3))&&!StringUtil.equals(DictCodeList.平安资产类型_债券.getCodeName(), rowData.get(3))
                    && !StringUtil.equals("项目类资产", rowData.get(3))) {
                messageList.add("资产类型只能输入" + DictCodeList.平安资产类型_债券.getCodeName()
                        + ",项目类资产!");
            }
            //交易日期不能为空
            this.checkIsBlank(rowData.get(4), "交易日期", messageList);
            this.checkIsDate(rowData.get(4), "交易日期", messageList);
            // 交易方向不能为空
            this.checkIsBlank(rowData.get(5), "交易方向", messageList);
            // 交易方向
            if(StringUtil.isNotBlank(rowData.get(5))&&!StringUtil.equals(DictCodeList.交易方向_买入.getCodeName(),rowData.get(5))
                    &&!StringUtil.equals(DictCodeList.交易方向_卖出.getCodeName(),rowData.get(5))){
                messageList.add("交易方向只能输入"+DictCodeList.交易方向_买入.getCodeName()
                        +","+DictCodeList.交易方向_卖出.getCodeName()+"!");
            };
            // 投资目的不能为空
            if (StringUtil.equals(DictCodeList.平安资产类型_债券.getCodeName(), rowData.get(3))) {
                this.checkIsBlank(rowData.get(6), "投资目的", messageList);
                // 投资目的
                if(StringUtil.isNotBlank(rowData.get(6))&&!StringUtil.equals(DictCodeListExt1.投资目的_以出售该金融资产为目的.getCodeName(),rowData.get(6))
                        &&!StringUtil.equals(DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getCodeName(),rowData.get(6))
                        &&!StringUtil.equals(DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getCodeName(),rowData.get(6))){
                    messageList.add("投资目的只能输入"+DictCodeListExt1.投资目的_以出售该金融资产为目的.getCodeName()
                            +","+DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getCodeName()
                            +","+DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getCodeName()+"!");
                };
            }
            // 交易数量不能为空
            this.checkIsBlank(rowData.get(7), "交易数量", messageList);
            if(StringUtil.isNotBlank(rowData.get(7))){
                this.checkIsDeciaml(rowData.get(7), "交易数量", messageList);
            }
            // 交易净价不能为空
            this.checkIsBlank(rowData.get(8), "交易净价", messageList);
            if(StringUtil.isNotBlank(rowData.get(8))) {
                this.checkIsDeciaml(rowData.get(8), "交易净价", messageList);
            }
//            // 应计利息不能为空
//            this.checkIsBlank(rowData.get(9), "应计利息", messageList);
            this.checkIsDeciaml(rowData.get(9), "应计利息", messageList);
            // 到期日不能为空

            if (StringUtil.isEmpty(rowData.get(2))) {
                this.checkIsBlank(rowData.get(10), "到期日", messageList);
            }
            if(StringUtil.isNotBlank(rowData.get(10))) {
                this.checkIsDate(rowData.get(10), "到期日", messageList);
            }
            // 资产代码为空时，判断到期收益率必填
            if (StringUtil.isEmpty(rowData.get(2))) {
                // 到期收益率不能为空
                this.checkIsBlank(rowData.get(11), "到期收益率", messageList);
            }

            if(StringUtil.isNotBlank(rowData.get(11))) {
                this.checkIsDeciaml(rowData.get(11), "到期收益率", messageList);
            }
            // 币种不能为空
            this.checkIsBlank(rowData.get(12), "币种", messageList);
            // 币种
            Map<String, String> map = SysDatadictUtil.getSysDictDataMap(DictCode.币种);
            if(StringUtil.isNotBlank(rowData.get(12))&&!SysDatadictUtil.getSysDictDataMap(DictCode.币种).containsValue(rowData.get(12))){
                messageList.add("请输入正确的币种!");
            };

            if (messageList.size() > 0) {
                checkResult.add(new ExcelValidDto(StringUtil.EMPTY, String.valueOf(i + 1),
                    StringUtil.joinString(messageList, "<br/>")));
            }
            else {
                dto.setPortfolioId(rowData.get(1));
                dto.setFinprodMarketId(rowData.get(2));
                dto.setType8(SysDatadictUtil.getCodeFromDataDictByName(DictCode.平安资产类型, rowData.get(3)));
                dto.setPs(SysDatadictUtil.getCodeFromDataDictByName(DictCode.交易方向, rowData.get(5)));
                dto.setInvAim(SysDatadictUtil.getCodeFromDataDictByName(DictCode.投资目的, rowData.get(6)));
                if (StringUtil.isNotBlank(rowData.get(7))) {
                    dto.setAmount(new BigDecimal(rowData.get(7)));
                }
                if (StringUtil.isNotBlank(rowData.get(8))) {
                    dto.setTradePrice(new BigDecimal(rowData.get(8)));
                }
                if (StringUtil.isNotBlank(rowData.get(9))) {
                    dto.setAccuir(new BigDecimal(rowData.get(9)));
                }
                //
                if (StringUtil.isNotBlank(rowData.get(4))) {
                    dto.setTradeDate(DateUtil.parse(rowData.get(4)));
                }else{
                    dto.setTradeDate(DateUtil.parse(iPlatformDate.getSysDateStr()));
                }

                if (StringUtil.isNotBlank(rowData.get(10))) {
                    dto.setMdate(DateUtil.parse(rowData.get(10)));
                }
                if(StringUtil.isNotEmpty(dto.getFinprodId())){
                    dto.setMdate(DateUtil.parse(((LinkedHashMap) assetInfoMap.get(rowData.get(2)).getObject()).get("MDATE").toString()));
                }

                if (StringUtil.isNotBlank(rowData.get(11))) {
                    dto.setYield(new BigDecimal(rowData.get(11)));
                }
                if (StringUtil.isNotBlank(rowData.get(12))) {
                    dto.setCcy(SysDatadictUtil.getCodeFromDataDictByName(DictCode.币种, rowData.get(12)));
                }
                else {
                    dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                }

                ptlMockTestTradeDtoList.add(dto);
            }
        }
        return checkResult;
    }

    /**
     * check模拟试算导入字段用,空判断
     *
     * @param cellData cellData
     * @param labelName labelName
     * @param messageList messageList
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public List<String> checkIsBlank(String cellData, String labelName, List<String> messageList) {
        // 判断是否是空
        if (StringUtil.isBlank(cellData)) {
            messageList.add(this.getMessage("PtlMockTest_E_0016", labelName));
        }
        return messageList;
    }

    /**
     * check模拟试算导入字段用,日期格式
     *
     * @param cellData cellData
     * @param labelName labelName
     * @param messageList messageList
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public List<String> checkIsDate(String cellData, String labelName, List<String> messageList) {
        // 判断是否是正确的日期格式
        if (StringUtil.isNotBlank(cellData)) {
            try {
                Date result = DateUtil.parse(cellData);
                if (result == null) {
                    messageList.add(this.getMessage("PtlMockTest_E_0017", labelName));
                }
            }
            catch (RuntimeException e) {
                messageList.add(this.getMessage("PtlMockTest_E_0017", labelName));
            }
        }
        return messageList;

    }

    /**
     * check模拟试算导入字段用,数值格式
     *
     * @param cellData cellData
     * @param labelName labelName
     * @param messageList messageList
     * @return 错误信息
     * @see
     * @since 1.0
     */
    public List<String> checkIsDeciaml(String cellData, String labelName, List<String> messageList) {
        // 判断是否是正确的日期格式
        if (StringUtil.isNotBlank(cellData)) {
            try {
                BigDecimal result = new BigDecimal(cellData);
                if (result == null) {
                    messageList.add(this.getMessage("PtlMockTest_E_0029", labelName));
                }
            }
            catch (RuntimeException e) {
                messageList.add(this.getMessage("PtlMockTest_E_0029", labelName));
            }
        }
        return messageList;

    }


}
