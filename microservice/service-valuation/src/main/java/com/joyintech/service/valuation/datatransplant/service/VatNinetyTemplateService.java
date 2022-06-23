/*
 * 文件名：VatNinetyTemplateService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：niuqianyu
 * 修改时间：2018年11月14日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.service.valuation.datatransplant.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.systemtools.service.SystemToolService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.datatransplant.dao.VatNinetyTemplateMapper;
import com.joyintech.service.valuation.datatransplant.dto.BooksetTaxConfigDto;
import com.joyintech.service.valuation.datatransplant.dto.TemplateCheckDto;
import com.joyintech.service.valuation.datatransplant.dto.VatNinetyTemplateDto;
import com.joyintech.tams.common.base.dao.TaxBokBalanceMapper;
import com.joyintech.tams.common.base.dao.TaxTurnedBalanceMapper;
import com.joyintech.tams.common.base.entity.TaxBokBalance;
import com.joyintech.tams.common.base.entity.TaxTurnedBalance;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.service.DataTransplantCommonService;
import com.joyintech.tams.valuation.common.utils.DBUtil;

/**
 * 增值税_90号文模板-service
 * 
 * @author niuqianyu
 * @version 1.0 2018年11月14日
 * @see VatNinetyTemplateService
 * @since 1.0
 */
@Service(value="serviceVatNinetyTemplateService")
public class VatNinetyTemplateService {

    @Autowired
    private DBUtil dBUtil;

    @Autowired
    private SystemToolService systemToolService;

    /**
     * 数据移植公用方法
     */
    @Autowired
    private DataTransplantCommonService dataTransplantCommonService;

    @Autowired
    private TaxBokBalanceMapper taxBokBalanceMapper;

    @Autowired
    private TaxTurnedBalanceMapper taxTurnedBalanceMapper;

    @Autowired
    private VatNinetyTemplateMapper vatNinetyTemplateMapper;

    /**
     * 定义 明细数据小数位数
     */
    private int scale = 15;

    /**
     * 定义 明细数据位数处理方式 = 四舍五入
     */
    private RoundingMode roundingMode = RoundingMode.HALF_UP;

    /**
     * 税额结转频率
     */
    public static final String TAX_CARRYOVER_CYCLE = "TAX_CARRYOVER_CYCLE";

    /**
     * 税额计算公式
     */
    public static final String TAX_CALC_FORMULA = "TAX_CALC_FORMULA";

    /**
     * round(amt/(1+rate),2)*rate
     */
    public static final String TAX_CALC_FORMULA_2 = "FORMULA_2";

    /**
     * amt/(1+rate)*rate
     */
    public static final String TAX_CALC_FORMULA_1 = "FORMULA_1";

    /**
     * 增值税_90号文模板导入
     * 
     * @param sheetDataList
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public List<TemplateCheckDto> vatNinetyTemplateExec(List<List<String>> sheetDataList) {
        List<VatNinetyTemplateDto> dtoList = this.convertToDto(sheetDataList);
        // 存储校验信息
        List<TemplateCheckDto> checkList = new ArrayList<>();
        // 类型
        // modify by chendong in 2018-11-22 start
        // 错误条数
        int number = 0;
        Map<String, Object> map = checkInfo(dtoList, number, checkList);
        // modify by chendong in 2018-11-22 end
        checkList = (List<TemplateCheckDto>)map.get("checkList");
        number = (int)map.get("number");
        if (number == 0) {
            // 增值税账务明细余额(TAX_BOK_BALANCE)数据入库
            this.insertTaxBokBalance(dtoList);
            // 增值税待抵扣增值额(TAX_TURNED_BALANCE)数据入库
            this.insertTaxTurnedBalance(dtoList);
        }
        return checkList;
    }

    /**
     * 增值税账务明细余额(TAX_BOK_BALANCE)数据入库
     * 
     * @param dtoList
     * @return
     * @see
     * @since 1.0
     */
    public int insertTaxBokBalance(List<VatNinetyTemplateDto> dtoList) {

        // 插入数据条数
        int insertNum = 0;

        // 税率
        BigDecimal taxRate = new BigDecimal("0.03");

        // 循环数据处理
        for (VatNinetyTemplateDto dto : dtoList) {

            // 定义表实体类
            TaxBokBalance taxBokBalance = new TaxBokBalance();
            // 流水
            taxBokBalance.setSeqNo(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
            // 账套代码
            taxBokBalance.setBooksetId(dto.getBooksetId());
            // 账套日期
            taxBokBalance.setBooksetDate(dto.getImpDate());
            // 账务明细代码
            taxBokBalance.setBokDetailId(dataTransplantCommonService.getBookDetailId(
                dto.getBusiType(), dto.getBusiId(), null, dto.getInvAim(),dto.getCcy()));
            // 发生日期
            taxBokBalance.setHappenDate(dto.getImpDate());
            // 入账日期
            taxBokBalance.setBookDate(dto.getImpDate());
            // 币种
            taxBokBalance.setoCcy(dto.getCcy());
            // 增值税类型
            taxBokBalance.setVatType(DictCodeList.增值税类型_价差收入.getValue());
            // 确认标识
            taxBokBalance.setTaxPType(DictCodeList.增值税_确认类型_预估计税.getValue());
            // 计税方式
            taxBokBalance.setCalcType(DictCodeList.计税方式_简易计税.getValue());
            // 发生场景
            taxBokBalance.setIncomeSource(DictCodeList.增值税_发生场景_待抵扣增值额.getValue());
            // 计税收入余额
            taxBokBalance.setIncomeAmtAll(dto.getIncomeAmt().negate());
            // 当日计税收入发生额
            taxBokBalance.setIncomeHappenAmt(BigDecimal.ZERO);
            // 当日结转累计发生额
            taxBokBalance.setCarryAmt(BigDecimal.ZERO);
            // 当日发生额
            taxBokBalance.setHappenAmt(BigDecimal.ZERO);
            // 增值税金额
            taxBokBalance.setTaxAmt(dataTransplantCommonService.taxCalculate(dto.getIncomeAmt(),
                taxRate, scale, roundingMode, this.getTaxCalcFormula(dto.getBooksetId())).negate());
            // 处理序列
            taxBokBalance.setBookOrder(BigDecimal.ONE);
            // 创建人
            taxBokBalance.setCreateUser(GlobalConstant.数据移植账户);
            // 创建时间
            taxBokBalance.setCreateTime(new Date());
            // 更新人
            taxBokBalance.setUpdateUser(GlobalConstant.数据移植账户);
            // 更新时间
            taxBokBalance.setUpdateTime(new Date());

            // 数据入库
            insertNum = insertNum + taxBokBalanceMapper.insert(taxBokBalance);

        }
        return insertNum;

    }

    /**
     * 增值税待抵扣增值额(TAX_TURNED_BALANCE)数据入库
     * 
     * @param dtoList
     * @return
     * @see
     * @since 1.0
     */
    public int insertTaxTurnedBalance(List<VatNinetyTemplateDto> dtoList) {

        // 插入数据条数
        int insertNum = 0;

        // 循环数据处理
        for (VatNinetyTemplateDto dto : dtoList) {

            // 定义表实体类
            TaxTurnedBalance taxTurnedBalance = new TaxTurnedBalance();
            // 流水
            taxTurnedBalance.setSeqNo(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
            // 账套代码
            taxTurnedBalance.setBooksetId(dto.getBooksetId());
            // 账务明细代码
            taxTurnedBalance.setBokDetailId(dataTransplantCommonService.getBookDetailId(
                dto.getBusiType(), dto.getBusiId(), null, dto.getInvAim(),dto.getCcy()));
            // 币种
            taxTurnedBalance.setoCcy(dto.getCcy());
            // 待抵扣资产份额
            taxTurnedBalance.setCbAmt(dto.getCbAmt());
            // 待抵扣增值额
            taxTurnedBalance.setIncomeAmt(dto.getIncomeAmt());
            // 创建人
            taxTurnedBalance.setCreateUser(GlobalConstant.数据移植账户);
            // 创建时间
            taxTurnedBalance.setCreateTime(new Date());
            // 更新人
            taxTurnedBalance.setUpdateUser(GlobalConstant.数据移植账户);
            // 更新时间
            taxTurnedBalance.setUpdateTime(new Date());

            insertNum = insertNum + taxTurnedBalanceMapper.insert(taxTurnedBalance);
        }
        return insertNum;

    }

    /**
     * 将excel中的数据转换为实体类
     * 
     * @param sheetDataList
     * @return
     * @see
     * @since 1.0
     */
    public List<VatNinetyTemplateDto> convertToDto(List<List<String>> sheetDataList) {
        // 定义返回实体类
        List<VatNinetyTemplateDto> dtoList = new ArrayList<VatNinetyTemplateDto>();

        // 循环进行数据处理
        for (List<String> row : sheetDataList) {
            // 定义实体类
            VatNinetyTemplateDto dto = new VatNinetyTemplateDto();
            // 塞值
            dto.setBooksetId(row.get(0) == "" ? null : row.get(0).trim());// 账套代码
            dto.setBusiId(row.get(1) == "" ? null : row.get(1).trim());// 业务明细代码
            dto.setBusiType(
                row.get(2) == "" ? null : row.get(2).trim().split(GlobalConstant.分号)[0]);// 业务明细类型
            dto.setImpDate(row.get(3) == "" ? null : DateUtil.parse(row.get(3).trim()));// 日期
            /**add by wujie on 20181214 start remark:模板增加投资目的**/
            int colnum = 4;
            String invAim =row.get(colnum) == "" ? null : row.get(colnum).trim().split(GlobalConstant.分号)[0];
            dto.setInvAim(invAim);
            /**add by wujie on 20181214 end**/
            
            dto.setCcy(row.get(colnum+1) == "" ? null : row.get(colnum+1).trim());// 币种
            dto.setCbAmt(row.get(colnum+2) == "" ? null : new BigDecimal(row.get(colnum+2).trim()));// 待抵扣资产份额
            dto.setIncomeAmt(row.get(colnum+3) == "" ? null : new BigDecimal(row.get(colnum+3).trim()));// 待抵扣增值额

            // 塞入list
            dtoList.add(dto);

        }

        return dtoList;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     * 
     * @param booksetId
     * @return
     * @see
     * @since 1.0
     */
    public String getTaxCalcFormula(String booksetId) {
        List<BooksetTaxConfigDto> listBooksetTaxConfig = vatNinetyTemplateMapper.queryBooksetTaxConfig(
            booksetId, DictCodeListExt1.配置方案类型_税务方案.getValue(), null);

        Map<String, String> cfConfigs = new HashMap<>();
        // 若账套税务配置不为空，则查询对应不启用场景
        if (CollectionUtils.isNotEmpty(listBooksetTaxConfig)) {
            listBooksetTaxConfig.forEach(item -> {
                cfConfigs.put(item.getConfigId(), item.getConfigValue());
            });
        }
        // 没有取到频率的情况,取为季度
        if (cfConfigs.get(TAX_CARRYOVER_CYCLE) == null) {
            cfConfigs.put(TAX_CARRYOVER_CYCLE, DictCodeList.频率_季.getValue());
        }

        // 没有取到频率的情况,取为季度
        String calcFormula = cfConfigs.get(TAX_CALC_FORMULA);
        if (calcFormula == null) {
            cfConfigs.put(TAX_CALC_FORMULA, TAX_CALC_FORMULA_2);
            calcFormula = TAX_CALC_FORMULA_2;
        }

        return calcFormula;

    }

    /**
     * 校验是否有错误信息
     * 
     * @param dtoList
     * @param number
     * @return checkList
     * @see
     * @since 1.0
     */
    private Map<String, Object> checkInfo(List<VatNinetyTemplateDto> dtoList, int number,
                                          List<TemplateCheckDto> checkList) {

        Map<String, Object> map = new HashMap<>();
        // 类型
        String typeError = "错误";
        // String typeWarn = "警告";

        // 行号
        for (int i = 0; i < dtoList.size(); i++ ) {
            // 判断是否有错误
            boolean error = false;
            // 校验账套表中是否存在该账套
            if (StringUtil.isNotEmpty(dtoList.get(i).getBooksetId())) {
                boolean flag = dataTransplantCommonService.checkBooksetId(
                    dtoList.get(i).getBooksetId());
                if (!flag) {
                    TemplateCheckDto templateCheckDto = new TemplateCheckDto();
                    templateCheckDto.setErrorLineNum(i + 2);
                    templateCheckDto.setErrorType(typeError);
                    templateCheckDto.setErrorMessage("账套表中不存在该账套");
                    checkList.add(templateCheckDto);
                    error = true;
                }
            }
            // 校验对应的标的是否存在
            if (StringUtil.isNotEmpty(dtoList.get(i).getBusiId())
                && StringUtil.isNotEmpty(dtoList.get(i).getBusiType())) {
            	Map<String,Object> params = new HashMap<String,Object>();
            	params.put("finprodId", dtoList.get(i).getBusiId());
            	params.put("finprodType", dtoList.get(i).getBusiType());
            	params.put("booksetId", dtoList.get(i).getBooksetId());
            	
                boolean flag = dataTransplantCommonService.checkUnderlying(params);
                if (!flag) {
                    TemplateCheckDto templateCheckDto = new TemplateCheckDto();
                    templateCheckDto.setErrorLineNum(i + 2);
                    templateCheckDto.setErrorType(typeError);
                    templateCheckDto.setErrorMessage("对应的标的不存在");
                    checkList.add(templateCheckDto);
                    error = true;
                }
            }
            if (!StringUtil.isNotEmpty(dtoList.get(i).getBooksetId())) {
                TemplateCheckDto templateCheckDto = new TemplateCheckDto();
                templateCheckDto.setErrorLineNum(i + 2);
                templateCheckDto.setErrorType(typeError);
                templateCheckDto.setErrorMessage("账套代码不能为空");
                checkList.add(templateCheckDto);
                error = true;
            }
            if (!StringUtil.isNotEmpty(dtoList.get(i).getBusiId())) {
                TemplateCheckDto templateCheckDto = new TemplateCheckDto();
                templateCheckDto.setErrorLineNum(i + 2);
                templateCheckDto.setErrorType(typeError);
                templateCheckDto.setErrorMessage("业务明细代码不能为空");
                checkList.add(templateCheckDto);
                error = true;
            }
            if (!StringUtil.isNotEmpty(dtoList.get(i).getBusiType())) {
                TemplateCheckDto templateCheckDto = new TemplateCheckDto();
                templateCheckDto.setErrorLineNum(i + 2);
                templateCheckDto.setErrorType(typeError);
                templateCheckDto.setErrorMessage("业务明细类型不能为空");
                checkList.add(templateCheckDto);
                error = true;
            }
            if (dtoList.get(i).getImpDate() == null) {
                TemplateCheckDto templateCheckDto = new TemplateCheckDto();
                templateCheckDto.setErrorLineNum(i + 2);
                templateCheckDto.setErrorType(typeError);
                templateCheckDto.setErrorMessage("日期不能为空");
                checkList.add(templateCheckDto);
                error = true;
            }
            
            /** 
             * add by wujie on 20181211 start
             * 业务明细类型为 F01 债券类型 时 投资目的不能为空
             */
            if(DictCodeListExt1.金融产品类型_债券.getValue().equals(dtoList.get(i).getBusiType())){
            	if (!StringUtil.isNotEmpty(dtoList.get(i).getInvAim())) {
            		 TemplateCheckDto templateCheckDto = new TemplateCheckDto();
                     templateCheckDto.setErrorLineNum(i + 2);
                     templateCheckDto.setErrorType(typeError);
                     templateCheckDto.setErrorMessage("业务明细类型为[F01:债券]时，投资目的不能为空");
                     checkList.add(templateCheckDto);
                     error = true;
            	}
            }else{
            	if (StringUtil.isNotEmpty(dtoList.get(i).getInvAim())) {
           		 	TemplateCheckDto templateCheckDto = new TemplateCheckDto();
                    templateCheckDto.setErrorLineNum(i + 2);
                    templateCheckDto.setErrorType(typeError);
                    templateCheckDto.setErrorMessage("业务明细类型非[F01:债券]时，投资目的必须为空");
                    checkList.add(templateCheckDto);
                    error = true;
            	}
            }
            /**add by wujie on 20181211 end**/
            
            if (!StringUtil.isNotEmpty(dtoList.get(i).getCcy())) {
                TemplateCheckDto templateCheckDto = new TemplateCheckDto();
                templateCheckDto.setErrorLineNum(i + 2);
                templateCheckDto.setErrorType(typeError);
                templateCheckDto.setErrorMessage("币种不能为空");
                checkList.add(templateCheckDto);
                error = true;
            }
            if (!StringUtil.isNotEmpty(dtoList.get(i).getCbAmt())) {
                TemplateCheckDto templateCheckDto = new TemplateCheckDto();
                templateCheckDto.setErrorLineNum(i + 2);
                templateCheckDto.setErrorType(typeError);
                templateCheckDto.setErrorMessage("待抵扣资产份额不能为空");
                checkList.add(templateCheckDto);
                error = true;
            }
            if (!StringUtil.isNotEmpty(dtoList.get(i).getIncomeAmt())) {
                TemplateCheckDto templateCheckDto = new TemplateCheckDto();
                templateCheckDto.setErrorLineNum(i + 2);
                templateCheckDto.setErrorType(typeError);
                templateCheckDto.setErrorMessage("待抵扣增值额不能为空");
                checkList.add(templateCheckDto);
                error = true;
            }
            // 校验枚举字段是否有效
            if (StringUtil.isNotEmpty(dtoList.get(i).getBusiType())) {
                DataDictItem from = systemToolService.getDataDictItem(DictCode.金融产品类型.getValue(),
                    dtoList.get(i).getBusiType());
                DataDictItem from2 = systemToolService.getDataDictItem(DictCode.资金账户类型.getValue(),
                    dtoList.get(i).getBusiType());
                if (!StringUtil.isNotEmpty(from) && !StringUtil.isNotEmpty(from2)) {
                    TemplateCheckDto templateCheckDto = new TemplateCheckDto();
                    templateCheckDto.setErrorLineNum(i + 2);
                    templateCheckDto.setErrorType(typeError);
                    templateCheckDto.setErrorMessage(
                        "业务明细类型枚举值：" + dtoList.get(i).getBusiType() + "无效");
                    checkList.add(templateCheckDto);
                    error = true;
                }
            }
            if (error) {
                number++ ;
            }

        }

        map.put("checkList", checkList);
        map.put("number", number);

        return map;
    }

}
